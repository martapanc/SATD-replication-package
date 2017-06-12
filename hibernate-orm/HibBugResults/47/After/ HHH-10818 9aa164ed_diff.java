diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index 15577dc186..7ebc7ab0cf 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,745 +1,753 @@
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
 import org.hibernate.boot.internal.AttributeConverterDescriptorNonAutoApplicableImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.AttributeConverterDescriptor;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.config.spi.StandardConverters;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.BinaryType;
 import org.hibernate.type.RowVersionType;
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
 	private final List<Boolean> insertability = new ArrayList<Boolean>();
 	private final List<Boolean> updatability = new ArrayList<Boolean>();
 
 	private String typeName;
 	private Properties typeParameters;
 	private boolean isVersion;
 	private boolean isNationalized;
 	private boolean isLob;
 
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private String foreignKeyDefinition;
 	private boolean alternateUniqueKey;
 	private boolean cascadeDeleteEnabled;
 
 	private AttributeConverterDescriptor attributeConverterDescriptor;
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
 		addColumn( column, true, true );
 	}
 
 	public void addColumn(Column column, boolean isInsertable, boolean isUpdatable) {
 		int index = columns.indexOf( column );
 		if ( index == -1 ) {
 			columns.add(column);
 			insertability.add( isInsertable );
 			updatability.add( isUpdatable );
 		}
 		else {
 			if ( insertability.get( index ) != isInsertable ) {
 				throw new IllegalStateException( "Same column is added more than once with different values for isInsertable" );
 			}
 			if ( updatability.get( index ) != isUpdatable ) {
 				throw new IllegalStateException( "Same column is added more than once with different values for isUpdatable" );
 			}
 		}
 		column.setValue( this );
 		column.setTypeIndex( columns.size() - 1 );
 	}
 
 	public void addFormula(Formula formula) {
 		columns.add( formula );
 		insertability.add( false );
 		updatability.add( false );
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
 				attributeConverterDescriptor = new AttributeConverterDescriptorNonAutoApplicableImpl( converterClass.newInstance() );
 				return;
 			}
 			catch (Exception e) {
 				log.logBadHbmAttributeConverterType( typeName, e.getMessage() );
 			}
 		}
 
 		this.typeName = typeName;
 	}
 
 	public void makeVersion() {
 		this.isVersion = true;
 	}
 
 	public boolean isVersion() {
 		return isVersion;
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
 			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName, getForeignKeyDefinition() );
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
 		if ( cs.getSettings().get( AvailableSettings.PREFERRED_POOLED_OPTIMIZER ) != null ) {
 			params.put(
 					AvailableSettings.PREFERRED_POOLED_OPTIMIZER,
 					cs.getSettings().get( AvailableSettings.PREFERRED_POOLED_OPTIMIZER )
 			);
 		}
 
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
 		return IdentityGenerator.class.isAssignableFrom(identifierGeneratorFactory.getIdentifierGeneratorClass( identifierGeneratorStrategy ));
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
 	
 	public String getForeignKeyDefinition() {
 		return foreignKeyDefinition;
 	}
 
 	public void setForeignKeyDefinition(String foreignKeyDefinition) {
 		this.foreignKeyDefinition = foreignKeyDefinition;
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
 		// if this is a byte[] version/timestamp, then we need to use RowVersionType
 		// instead of BinaryType (HHH-10413)
 		if ( isVersion && BinaryType.class.isInstance( result ) ) {
 			log.debug( "version is BinaryType; changing to RowVersionType" );
 			result = RowVersionType.INSTANCE;
 		}
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
 
 		if ( attributeConverterDescriptor == null ) {
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
 
 		final Class entityAttributeJavaType = attributeConverterDescriptor.getDomainType();
 		final Class databaseColumnJavaType = attributeConverterDescriptor.getJdbcType();
 
 
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
-		// find the standard SqlTypeDescriptor for that JDBC type code.
-		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
+
+		// find the standard SqlTypeDescriptor for that JDBC type code (allow itr to be remapped if needed!)
+		final SqlTypeDescriptor sqlTypeDescriptor = metadata.getMetadataBuildingOptions().getServiceRegistry()
+				.getService( JdbcServices.class )
+				.getJdbcEnvironment()
+				.getDialect()
+				.remapSqlTypeDescriptor( SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode ) );
+
 		// find the JavaTypeDescriptor representing the "intermediate database type representation".  Back to the
 		// 		illustration, this should be the type descriptor for Strings
 		final JavaTypeDescriptor intermediateJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( databaseColumnJavaType );
+
 		// and finally construct the adapter, which injects the AttributeConverter calls into the binding/extraction
 		// 		process...
 		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
 				attributeConverterDescriptor.getAttributeConverter(),
 				sqlTypeDescriptor,
 				intermediateJavaTypeDescriptor
 		);
 
 		// todo : cache the AttributeConverterTypeAdapter in case that AttributeConverter is applied multiple times.
 
 		final String name = AttributeConverterTypeAdapter.NAME_PREFIX + attributeConverterDescriptor.getAttributeConverter().getClass().getName();
 		final String description = String.format(
 				"BasicType adapter for AttributeConverter<%s,%s>",
 				entityAttributeJavaType.getSimpleName(),
 				databaseColumnJavaType.getSimpleName()
 		);
 		return new AttributeConverterTypeAdapter(
 				name,
 				description,
 				attributeConverterDescriptor.getAttributeConverter(),
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
 
 	public void copyTypeFrom( SimpleValue sourceValue ) {
 		setTypeName( sourceValue.getTypeName() );
 		setTypeParameters( sourceValue.getTypeParameters() );
 
 		type = sourceValue.type;
 		attributeConverterDescriptor = sourceValue.attributeConverterDescriptor;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + columns.toString() + ')';
 	}
 
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 	public boolean[] getColumnInsertability() {
 		return extractBooleansFromList( insertability );
 	}
 	
 	public boolean[] getColumnUpdateability() {
 		return extractBooleansFromList( updatability );
 	}
 
 	private static boolean[] extractBooleansFromList(List<Boolean> list) {
 		final boolean[] array = new boolean[ list.size() ];
 		int i = 0;
 		for ( Boolean value : list ) {
 			array[ i++ ] = value;
 		}
 		return array;
 	}
 
 	public void setJpaAttributeConverterDescriptor(AttributeConverterDescriptor attributeConverterDescriptor) {
 		this.attributeConverterDescriptor = attributeConverterDescriptor;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterSqlTypeDescriptorAdapter.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterSqlTypeDescriptorAdapter.java
index 6ffc37a8cd..7af451f4d4 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterSqlTypeDescriptorAdapter.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterSqlTypeDescriptorAdapter.java
@@ -1,153 +1,152 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type.descriptor.converter;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import javax.persistence.AttributeConverter;
 import javax.persistence.PersistenceException;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 import org.jboss.logging.Logger;
 
 /**
  * Adapter for incorporating JPA {@link AttributeConverter} handling into the SqlTypeDescriptor contract.
  * <p/>
  * Essentially this is responsible for mapping to/from the intermediate database type representation.  Continuing the
  * {@code AttributeConverter<Integer,String>} example from
  * {@link org.hibernate.mapping.SimpleValue#buildAttributeConverterTypeAdapter()}, the "intermediate database type
  * representation" would be the String representation.  So on binding, we convert the incoming Integer to String;
  * on extraction we extract the value as String and convert to Integer.
  *
  * @author Steve Ebersole
  */
 public class AttributeConverterSqlTypeDescriptorAdapter implements SqlTypeDescriptor {
 	private static final Logger log = Logger.getLogger( AttributeConverterSqlTypeDescriptorAdapter.class );
 
 	private final AttributeConverter converter;
 	private final SqlTypeDescriptor delegate;
 	private final JavaTypeDescriptor intermediateJavaTypeDescriptor;
 
 	public AttributeConverterSqlTypeDescriptorAdapter(
 			AttributeConverter converter,
 			SqlTypeDescriptor delegate,
 			JavaTypeDescriptor intermediateJavaTypeDescriptor) {
 		this.converter = converter;
 		this.delegate = delegate;
 		this.intermediateJavaTypeDescriptor = intermediateJavaTypeDescriptor;
 	}
 
 	@Override
 	public int getSqlType() {
 		return delegate.getSqlType();
 	}
 
 	@Override
 	public boolean canBeRemapped() {
-		// todo : consider the ramifications of this.
-		// certainly we need to account for the remapping of the delegate sql-type, but is it really valid to
-		// allow remapping of the converter sql-type?
-		return delegate.canBeRemapped();
+		// any remapping of the underlying SqlTypeDescriptor should have
+		// happened prior to it being passed to us.
+		return false;
 	}
 
 
 	// Binding ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
 		// Get the binder for the intermediate type representation
 		final ValueBinder realBinder = delegate.getBinder( intermediateJavaTypeDescriptor );
 
 		return new ValueBinder<X>() {
 			@Override
 			public void bind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				final Object convertedValue;
 				try {
 					convertedValue = converter.convertToDatabaseColumn( value );
 				}
 				catch (PersistenceException pe) {
 					throw pe;
 				}
 				catch (RuntimeException re) {
 					throw new PersistenceException( "Error attempting to apply AttributeConverter", re );
 				}
 
 				log.debugf( "Converted value on binding : %s -> %s", value, convertedValue );
 				realBinder.bind( st, convertedValue, index, options );
 			}
 
 			@Override
 			public void bind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
 				final Object convertedValue;
 				try {
 					convertedValue = converter.convertToDatabaseColumn( value );
 				}
 				catch (PersistenceException pe) {
 					throw pe;
 				}
 				catch (RuntimeException re) {
 					throw new PersistenceException( "Error attempting to apply AttributeConverter", re );
 				}
 
 				log.debugf( "Converted value on binding : %s -> %s", value, convertedValue );
 				realBinder.bind( st, convertedValue, name, options );
 			}
 		};
 	}
 
 
 	// Extraction ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor) {
 		// Get the extractor for the intermediate type representation
 		final ValueExtractor realExtractor = delegate.getExtractor( intermediateJavaTypeDescriptor );
 
 		return new ValueExtractor<X>() {
 			@Override
 			public X extract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return doConversion( realExtractor.extract( rs, name, options ) );
 			}
 
 			@Override
 			public X extract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return doConversion( realExtractor.extract( statement, index, options ) );
 			}
 
 			@Override
 			public X extract(CallableStatement statement, String[] paramNames, WrapperOptions options) throws SQLException {
 				if ( paramNames.length > 1 ) {
 					throw new IllegalArgumentException( "Basic value extraction cannot handle multiple output parameters" );
 				}
 				return doConversion( realExtractor.extract( statement, paramNames, options ) );
 			}
 
 			@SuppressWarnings("unchecked")
 			private X doConversion(Object extractedValue) {
 				try {
 					X convertedValue = (X) converter.convertToEntityAttribute( extractedValue );
 					log.debugf( "Converted value on extraction: %s -> %s", extractedValue, convertedValue );
 					return convertedValue;
 				}
 				catch (PersistenceException pe) {
 					throw pe;
 				}
 				catch (RuntimeException re) {
 					throw new PersistenceException( "Error attempting to apply AttributeConverter", re );
 				}
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/caching/Address.java b/hibernate-core/src/test/java/org/hibernate/test/converter/caching/Address.java
new file mode 100644
index 0000000000..1413ad1804
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/caching/Address.java
@@ -0,0 +1,73 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.test.converter.caching;
+
+import javax.persistence.Cacheable;
+import javax.persistence.Convert;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.Lob;
+
+import org.hibernate.annotations.Cache;
+import org.hibernate.annotations.CacheConcurrencyStrategy;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+@Cacheable
+@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
+public class Address {
+	@Id
+	private Integer id;
+	private String streetLine1;
+	private String streetLine2;
+	@Convert(converter = PostalAreaConverter.class)
+	private PostalArea postalArea;
+
+	public Address() {
+	}
+
+	public Address(
+			Integer id,
+			String streetLine1,
+			String streetLine2,
+			PostalArea postalArea) {
+		this.id = id;
+		this.streetLine1 = streetLine1;
+		this.streetLine2 = streetLine2;
+		this.postalArea = postalArea;
+	}
+
+	public Integer getId() {
+		return id;
+	}
+
+	public String getStreetLine1() {
+		return streetLine1;
+	}
+
+	public void setStreetLine1(String streetLine1) {
+		this.streetLine1 = streetLine1;
+	}
+
+	public String getStreetLine2() {
+		return streetLine2;
+	}
+
+	public void setStreetLine2(String streetLine2) {
+		this.streetLine2 = streetLine2;
+	}
+
+	public PostalArea getPostalArea() {
+		return postalArea;
+	}
+
+	public void setPostalArea(PostalArea postalArea) {
+		this.postalArea = postalArea;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/caching/BasicStructuredCachingOfConvertedValueTest.java b/hibernate-core/src/test/java/org/hibernate/test/converter/caching/BasicStructuredCachingOfConvertedValueTest.java
new file mode 100644
index 0000000000..757831ad6c
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/caching/BasicStructuredCachingOfConvertedValueTest.java
@@ -0,0 +1,104 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.test.converter.caching;
+
+import java.util.Map;
+
+import org.hibernate.Session;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.persister.entity.EntityPersister;
+
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.cache.CachingRegionFactory;
+import org.hibernate.testing.cache.EntityRegionImpl;
+import org.hibernate.testing.cache.ReadWriteEntityRegionAccessStrategy;
+import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
+import org.junit.Test;
+
+import static org.hamcrest.CoreMatchers.instanceOf;
+import static org.hamcrest.CoreMatchers.is;
+import static org.hamcrest.MatcherAssert.assertThat;
+
+/**
+ * @author Steve Ebersole
+ */
+public class BasicStructuredCachingOfConvertedValueTest extends BaseNonConfigCoreFunctionalTestCase {
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9615" )
+	@SuppressWarnings("unchecked")
+	public void basicCacheStructureTest() {
+		EntityPersister persister =  sessionFactory().getMetamodel().entityPersisters().get( Address.class.getName() );
+		EntityRegionImpl region = (EntityRegionImpl) persister.getCacheAccessStrategy().getRegion();
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// test during store...
+		PostalAreaConverter.clearCounts();
+
+		Session session = openSession();
+		session.getTransaction().begin();
+		session.save( new Address( 1, "123 Main St.", null, PostalArea._78729 ) );
+		session.getTransaction().commit();
+		session.close();
+
+		{
+			final Object cachedItem = region.getDataMap().values().iterator().next();
+			final Map<String, ?> state = (Map) ( (ReadWriteEntityRegionAccessStrategy.Item) cachedItem ).getValue();
+			// this is the point of the Jira.. that this "should be" the converted value
+			assertThat( state.get( "postalArea" ), instanceOf( PostalArea.class ) );
+		}
+
+		assertThat( PostalAreaConverter.toDatabaseCallCount, is(1) );
+		assertThat( PostalAreaConverter.toDomainCallCount, is(0) );
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// test during load...
+		PostalAreaConverter.clearCounts();
+		region.evictAll();
+
+		session = openSession();
+		session.getTransaction().begin();
+		Address address = session.get( Address.class, 1 );
+		session.getTransaction().commit();
+		session.close();
+
+		{
+			final Object cachedItem = region.getDataMap().values().iterator().next();
+			final Map<String, ?> state = (Map) ( (ReadWriteEntityRegionAccessStrategy.Item) cachedItem ).getValue();
+			// this is the point of the Jira.. that this "should be" the converted value
+			assertThat( state.get( "postalArea" ), instanceOf( PostalArea.class ) );
+		}
+
+		assertThat( PostalAreaConverter.toDatabaseCallCount, is(0) );
+		assertThat( PostalAreaConverter.toDomainCallCount, is(1) );
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// cleanup
+		session = openSession();
+		session.getTransaction().begin();
+		session.delete( address );
+		session.getTransaction().commit();
+		session.close();
+	}
+
+
+	@Override
+	protected void addSettings(Map settings) {
+		super.addSettings( settings );
+
+		settings.put( AvailableSettings.USE_SECOND_LEVEL_CACHE, "true" );
+		settings.put( AvailableSettings.CACHE_REGION_FACTORY, CachingRegionFactory.class );
+		settings.put( AvailableSettings.USE_STRUCTURED_CACHE, "true" );
+
+	}
+
+	@Override
+	protected Class[] getAnnotatedClasses() {
+		return new Class[] { Address.class };
+	}
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/caching/BasicUnstructuredCachingOfConvertedValueTest.java b/hibernate-core/src/test/java/org/hibernate/test/converter/caching/BasicUnstructuredCachingOfConvertedValueTest.java
new file mode 100644
index 0000000000..3a6b479357
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/caching/BasicUnstructuredCachingOfConvertedValueTest.java
@@ -0,0 +1,105 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.test.converter.caching;
+
+import java.util.Map;
+
+import org.hibernate.Session;
+import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.persister.entity.EntityPersister;
+
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.cache.CachingRegionFactory;
+import org.hibernate.testing.cache.EntityRegionImpl;
+import org.hibernate.testing.cache.ReadWriteEntityRegionAccessStrategy;
+import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
+import org.junit.Test;
+
+import static org.hamcrest.CoreMatchers.instanceOf;
+import static org.hamcrest.CoreMatchers.is;
+import static org.hamcrest.MatcherAssert.assertThat;
+
+/**
+ * @author Steve Ebersole
+ */
+public class BasicUnstructuredCachingOfConvertedValueTest extends BaseNonConfigCoreFunctionalTestCase {
+
+	public static final int postalAreaAttributeIndex = 0;
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9615" )
+	@SuppressWarnings("unchecked")
+	public void basicCacheStructureTest() {
+		EntityPersister persister =  sessionFactory().getMetamodel().entityPersisters().get( Address.class.getName() );
+		EntityRegionImpl region = (EntityRegionImpl) persister.getCacheAccessStrategy().getRegion();
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// test during store...
+		PostalAreaConverter.clearCounts();
+
+		Session session = openSession();
+		session.getTransaction().begin();
+		session.save( new Address( 1, "123 Main St.", null, PostalArea._78729 ) );
+		session.getTransaction().commit();
+		session.close();
+
+		{
+			final Object cachedItem = region.getDataMap().values().iterator().next();
+			final StandardCacheEntryImpl state = (StandardCacheEntryImpl) ( (ReadWriteEntityRegionAccessStrategy.Item) cachedItem ).getValue();
+			assertThat( state.getDisassembledState()[postalAreaAttributeIndex], instanceOf( PostalArea.class ) );
+		}
+
+		assertThat( PostalAreaConverter.toDatabaseCallCount, is(1) );
+		assertThat( PostalAreaConverter.toDomainCallCount, is(0) );
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// test during load...
+		PostalAreaConverter.clearCounts();
+		region.evictAll();
+
+		session = openSession();
+		session.getTransaction().begin();
+		Address address = session.get( Address.class, 1 );
+		session.getTransaction().commit();
+		session.close();
+
+		{
+			final Object cachedItem = region.getDataMap().values().iterator().next();
+			final StandardCacheEntryImpl state = (StandardCacheEntryImpl) ( (ReadWriteEntityRegionAccessStrategy.Item) cachedItem ).getValue();
+			assertThat( state.getDisassembledState()[postalAreaAttributeIndex], instanceOf( PostalArea.class ) );
+		}
+
+		assertThat( PostalAreaConverter.toDatabaseCallCount, is(0) );
+		assertThat( PostalAreaConverter.toDomainCallCount, is(1) );
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// cleanup
+		session = openSession();
+		session.getTransaction().begin();
+		session.delete( address );
+		session.getTransaction().commit();
+		session.close();
+	}
+
+
+	@Override
+	protected void addSettings(Map settings) {
+		super.addSettings( settings );
+
+		settings.put( AvailableSettings.USE_SECOND_LEVEL_CACHE, "true" );
+		settings.put( AvailableSettings.CACHE_REGION_FACTORY, CachingRegionFactory.class );
+		settings.put( AvailableSettings.USE_STRUCTURED_CACHE, "false" );
+
+	}
+
+	@Override
+	protected Class[] getAnnotatedClasses() {
+		return new Class[] { Address.class };
+	}
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/caching/PostalArea.java b/hibernate-core/src/test/java/org/hibernate/test/converter/caching/PostalArea.java
new file mode 100644
index 0000000000..296ca148ce
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/caching/PostalArea.java
@@ -0,0 +1,57 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.test.converter.caching;
+
+import org.hibernate.annotations.Immutable;
+
+/**
+ * @author Steve Ebersole
+ */
+@Immutable
+public enum PostalArea {
+	_78729( "78729", "North Austin", "Austin", State.TX );
+
+	private final String zipCode;
+	private final String name;
+	private final String cityName;
+	private final State state;
+
+	PostalArea(
+			String zipCode,
+			String name,
+			String cityName,
+			State state) {
+		this.zipCode = zipCode;
+		this.name = name;
+		this.cityName = cityName;
+		this.state = state;
+	}
+
+	public static PostalArea fromZipCode(String zipCode) {
+		if ( _78729.zipCode.equals( zipCode ) ) {
+			return _78729;
+		}
+
+		throw new IllegalArgumentException( "Unknown zip code" );
+	}
+
+	public String getZipCode() {
+		return zipCode;
+	}
+
+	public String getName() {
+		return name;
+	}
+
+	public String getCityName() {
+		return cityName;
+	}
+
+	public State getState() {
+		return state;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/caching/PostalAreaConverter.java b/hibernate-core/src/test/java/org/hibernate/test/converter/caching/PostalAreaConverter.java
new file mode 100644
index 0000000000..9e00901fbb
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/caching/PostalAreaConverter.java
@@ -0,0 +1,43 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.test.converter.caching;
+
+import javax.persistence.AttributeConverter;
+
+/**
+ * @author Steve Ebersole
+ */
+public class PostalAreaConverter
+		implements AttributeConverter<PostalArea, String> {
+	static int toDatabaseCallCount = 0;
+	static int toDomainCallCount = 0;
+
+	@Override
+	public String convertToDatabaseColumn(PostalArea attribute) {
+		toDatabaseCallCount++;
+		if ( attribute == null ) {
+			return null;
+		}
+		else {
+			return attribute.getZipCode();
+		}
+	}
+
+	@Override
+	public PostalArea convertToEntityAttribute(String dbData) {
+		toDomainCallCount++;
+		if ( dbData == null ) {
+			return null;
+		}
+		return PostalArea.fromZipCode( dbData );
+	}
+
+	static void clearCounts() {
+		toDatabaseCallCount = 0;
+		toDomainCallCount = 0;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/caching/State.java b/hibernate-core/src/test/java/org/hibernate/test/converter/caching/State.java
new file mode 100644
index 0000000000..335aed3c1f
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/caching/State.java
@@ -0,0 +1,33 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.test.converter.caching;
+
+import org.hibernate.annotations.Immutable;
+
+/**
+ * @author Steve Ebersole
+ */
+@Immutable
+public enum State {
+	TX( "TX", "Texas" );
+
+	private final String code;
+	private final String name;
+
+	State(String code, String name) {
+		this.code = code;
+		this.name = name;
+	}
+
+	public String getCode() {
+		return code;
+	}
+
+	public String getName() {
+		return name;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/lob/Address.java b/hibernate-core/src/test/java/org/hibernate/test/converter/lob/Address.java
new file mode 100644
index 0000000000..621fd75185
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/lob/Address.java
@@ -0,0 +1,74 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.test.converter.lob;
+
+import javax.persistence.Cacheable;
+import javax.persistence.Convert;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.Lob;
+
+import org.hibernate.annotations.Cache;
+import org.hibernate.annotations.CacheConcurrencyStrategy;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+@Cacheable
+@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
+public class Address {
+	@Id
+	Integer id;
+	String streetLine1;
+	String streetLine2;
+	@Lob
+	@Convert(converter = PostalAreaConverter.class)
+	PostalArea postalArea;
+
+	public Address() {
+	}
+
+	public Address(
+			Integer id,
+			String streetLine1,
+			String streetLine2,
+			PostalArea postalArea) {
+		this.id = id;
+		this.streetLine1 = streetLine1;
+		this.streetLine2 = streetLine2;
+		this.postalArea = postalArea;
+	}
+
+	public Integer getId() {
+		return id;
+	}
+
+	public String getStreetLine1() {
+		return streetLine1;
+	}
+
+	public void setStreetLine1(String streetLine1) {
+		this.streetLine1 = streetLine1;
+	}
+
+	public String getStreetLine2() {
+		return streetLine2;
+	}
+
+	public void setStreetLine2(String streetLine2) {
+		this.streetLine2 = streetLine2;
+	}
+
+	public PostalArea getPostalArea() {
+		return postalArea;
+	}
+
+	public void setPostalArea(PostalArea postalArea) {
+		this.postalArea = postalArea;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/lob/ConverterAndLobTest.java b/hibernate-core/src/test/java/org/hibernate/test/converter/lob/ConverterAndLobTest.java
new file mode 100644
index 0000000000..3382aaa8ef
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/lob/ConverterAndLobTest.java
@@ -0,0 +1,79 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.test.converter.lob;
+
+import java.util.Map;
+
+import org.hibernate.Session;
+import org.hibernate.cfg.AvailableSettings;
+
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
+import org.junit.Test;
+
+import static org.hamcrest.CoreMatchers.is;
+import static org.hamcrest.MatcherAssert.assertThat;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ConverterAndLobTest extends BaseNonConfigCoreFunctionalTestCase {
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9615" )
+	@SuppressWarnings("unchecked")
+	public void basicTest() {
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// test during store...
+		PostalAreaConverter.clearCounts();
+
+		Session session = openSession();
+		session.getTransaction().begin();
+		session.save( new Address( 1, "123 Main St.", null, PostalArea._78729 ) );
+		session.getTransaction().commit();
+		session.close();
+
+		assertThat( PostalAreaConverter.toDatabaseCallCount, is(1) );
+		assertThat( PostalAreaConverter.toDomainCallCount, is(0) );
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// test during load...
+		PostalAreaConverter.clearCounts();
+
+		session = openSession();
+		session.getTransaction().begin();
+		Address address = session.get( Address.class, 1 );
+		session.getTransaction().commit();
+		session.close();
+
+		assertThat( PostalAreaConverter.toDatabaseCallCount, is(0) );
+		assertThat( PostalAreaConverter.toDomainCallCount, is(1) );
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// cleanup
+		session = openSession();
+		session.getTransaction().begin();
+		session.delete( address );
+		session.getTransaction().commit();
+		session.close();
+	}
+
+
+	@Override
+	protected void addSettings(Map settings) {
+		super.addSettings( settings );
+		settings.put( AvailableSettings.USE_SECOND_LEVEL_CACHE, "false" );
+
+	}
+
+	@Override
+	protected Class[] getAnnotatedClasses() {
+		return new Class[] { Address.class };
+	}
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/lob/PostalArea.java b/hibernate-core/src/test/java/org/hibernate/test/converter/lob/PostalArea.java
new file mode 100644
index 0000000000..ada3d92ad7
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/lob/PostalArea.java
@@ -0,0 +1,57 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.test.converter.lob;
+
+import org.hibernate.annotations.Immutable;
+
+/**
+ * @author Steve Ebersole
+ */
+@Immutable
+public enum PostalArea {
+	_78729( "78729", "North Austin", "Austin", State.TX );
+
+	private final String zipCode;
+	private final String name;
+	private final String cityName;
+	private final State state;
+
+	PostalArea(
+			String zipCode,
+			String name,
+			String cityName,
+			State state) {
+		this.zipCode = zipCode;
+		this.name = name;
+		this.cityName = cityName;
+		this.state = state;
+	}
+
+	public static PostalArea fromZipCode(String zipCode) {
+		if ( _78729.zipCode.equals( zipCode ) ) {
+			return _78729;
+		}
+
+		throw new IllegalArgumentException( "Unknown zip code" );
+	}
+
+	public String getZipCode() {
+		return zipCode;
+	}
+
+	public String getName() {
+		return name;
+	}
+
+	public String getCityName() {
+		return cityName;
+	}
+
+	public State getState() {
+		return state;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/lob/PostalAreaConverter.java b/hibernate-core/src/test/java/org/hibernate/test/converter/lob/PostalAreaConverter.java
new file mode 100644
index 0000000000..b76c578c82
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/lob/PostalAreaConverter.java
@@ -0,0 +1,43 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.test.converter.lob;
+
+import javax.persistence.AttributeConverter;
+
+/**
+ * @author Steve Ebersole
+ */
+public class PostalAreaConverter
+		implements AttributeConverter<PostalArea, String> {
+	static int toDatabaseCallCount = 0;
+	static int toDomainCallCount = 0;
+
+	@Override
+	public String convertToDatabaseColumn(PostalArea attribute) {
+		toDatabaseCallCount++;
+		if ( attribute == null ) {
+			return null;
+		}
+		else {
+			return attribute.getZipCode();
+		}
+	}
+
+	@Override
+	public PostalArea convertToEntityAttribute(String dbData) {
+		toDomainCallCount++;
+		if ( dbData == null ) {
+			return null;
+		}
+		return PostalArea.fromZipCode( dbData );
+	}
+
+	static void clearCounts() {
+		toDatabaseCallCount = 0;
+		toDomainCallCount = 0;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/lob/State.java b/hibernate-core/src/test/java/org/hibernate/test/converter/lob/State.java
new file mode 100644
index 0000000000..034962c9bb
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/lob/State.java
@@ -0,0 +1,33 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.test.converter.lob;
+
+import org.hibernate.annotations.Immutable;
+
+/**
+ * @author Steve Ebersole
+ */
+@Immutable
+public enum State {
+	TX( "TX", "Texas" );
+
+	private final String code;
+	private final String name;
+
+	State(String code, String name) {
+		this.code = code;
+		this.name = name;
+	}
+
+	public String getCode() {
+		return code;
+	}
+
+	public String getName() {
+		return name;
+	}
+}
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java
index d53608642a..00b6f16d54 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java
@@ -1,368 +1,368 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.UUID;
 import java.util.concurrent.atomic.AtomicLong;
 import java.util.concurrent.locks.ReentrantReadWriteLock;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Strong Liu
  */
-abstract class AbstractReadWriteAccessStrategy extends BaseRegionAccessStrategy {
+public abstract class AbstractReadWriteAccessStrategy extends BaseRegionAccessStrategy {
 	private static final Logger LOG = Logger.getLogger( AbstractReadWriteAccessStrategy.class.getName() );
 
 	private final UUID uuid = UUID.randomUUID();
 	private final AtomicLong nextLockId = new AtomicLong();
 	private ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
 	protected java.util.concurrent.locks.Lock readLock = reentrantReadWriteLock.readLock();
 	protected java.util.concurrent.locks.Lock writeLock = reentrantReadWriteLock.writeLock();
 
 	/**
 	 * Returns <code>null</code> if the item is not readable.  Locked items are not readable, nor are items created
 	 * afterQuery the start of this transaction.
 	 */
 	@Override
 	public final Object get(SharedSessionContractImplementor session, Object key, long txTimestamp) throws CacheException {
 		LOG.debugf( "getting key[%s] from region[%s]", key, getInternalRegion().getName() );
 		try {
 			readLock.lock();
 			Lockable item = (Lockable) getInternalRegion().get( session, key );
 
 			boolean readable = item != null && item.isReadable( txTimestamp );
 			if ( readable ) {
 				LOG.debugf( "hit key[%s] in region[%s]", key, getInternalRegion().getName() );
 				return item.getValue();
 			}
 			else {
 				if ( item == null ) {
 					LOG.debugf( "miss key[%s] in region[%s]", key, getInternalRegion().getName() );
 				}
 				else {
 					LOG.debugf( "hit key[%s] in region[%s], but it is unreadable", key, getInternalRegion().getName() );
 				}
 				return null;
 			}
 		}
 		finally {
 			readLock.unlock();
 		}
 	}
 
 	abstract Comparator getVersionComparator();
 
 	/**
 	 * Returns <code>false</code> and fails to put the value if there is an existing un-writeable item mapped to this
 	 * key.
 	 */
 	@Override
 	public final boolean putFromLoad(
 			SharedSessionContractImplementor session,
 			Object key,
 			Object value,
 			long txTimestamp,
 			Object version,
 			boolean minimalPutOverride)
 			throws CacheException {
 		try {
 			LOG.debugf( "putting key[%s] -> value[%s] into region[%s]", key, value, getInternalRegion().getName() );
 			writeLock.lock();
 			Lockable item = (Lockable) getInternalRegion().get( session, key );
 			boolean writeable = item == null || item.isWriteable( txTimestamp, version, getVersionComparator() );
 			if ( writeable ) {
 				LOG.debugf(
 						"putting key[%s] -> value[%s] into region[%s] success",
 						key,
 						value,
 						getInternalRegion().getName()
 				);
 				getInternalRegion().put( session, key, new Item( value, version, getInternalRegion().nextTimestamp() ) );
 				return true;
 			}
 			else {
 				LOG.debugf(
 						"putting key[%s] -> value[%s] into region[%s] fail due to it is unwriteable",
 						key,
 						value,
 						getInternalRegion().getName()
 				);
 				return false;
 			}
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 	/**
 	 * Soft-lock a cache item.
 	 */
 	@Override
 	public final SoftLock lockItem(SharedSessionContractImplementor session, Object key, Object version) throws CacheException {
 
 		try {
 			LOG.debugf( "locking key[%s] in region[%s]", key, getInternalRegion().getName() );
 			writeLock.lock();
 			Lockable item = (Lockable) getInternalRegion().get( session, key );
 			long timeout = getInternalRegion().nextTimestamp() + getInternalRegion().getTimeout();
 			final Lock lock = ( item == null ) ? new Lock( timeout, uuid, nextLockId(), version ) : item.lock(
 					timeout,
 					uuid,
 					nextLockId()
 			);
 			getInternalRegion().put( session, key, lock );
 			return lock;
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 	/**
 	 * Soft-unlock a cache item.
 	 */
 	@Override
 	public final void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
 
 		try {
 			LOG.debugf( "unlocking key[%s] in region[%s]", key, getInternalRegion().getName() );
 			writeLock.lock();
 			Lockable item = (Lockable) getInternalRegion().get( session, key );
 
 			if ( ( item != null ) && item.isUnlockable( lock ) ) {
 				decrementLock(session, key, (Lock) item );
 			}
 			else {
 				handleLockExpiry(session, key, item );
 			}
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 	private long nextLockId() {
 		return nextLockId.getAndIncrement();
 	}
 
 	/**
 	 * Unlock and re-put the given key, lock combination.
 	 */
 	protected void decrementLock(SharedSessionContractImplementor session, Object key, Lock lock) {
 		lock.unlock( getInternalRegion().nextTimestamp() );
 		getInternalRegion().put( session, key, lock );
 	}
 
 	/**
 	 * Handle the timeout of a previous lock mapped to this key
 	 */
 	protected void handleLockExpiry(SharedSessionContractImplementor session, Object key, Lockable lock) {
 		LOG.info( "Cached entry expired : " + key );
 
 		long ts = getInternalRegion().nextTimestamp() + getInternalRegion().getTimeout();
 		// create new lock that times out immediately
 		Lock newLock = new Lock( ts, uuid, nextLockId.getAndIncrement(), null );
 		newLock.unlock( ts );
 		getInternalRegion().put( session, key, newLock );
 	}
 
 	/**
 	 * Interface type implemented by all wrapper objects in the cache.
 	 */
-	protected interface Lockable {
+	public interface Lockable {
 
 		/**
 		 * Returns <code>true</code> if the enclosed value can be read by a transaction started at the given time.
 		 */
 		boolean isReadable(long txTimestamp);
 
 		/**
 		 * Returns <code>true</code> if the enclosed value can be replaced with one of the given version by a
 		 * transaction started at the given time.
 		 */
 		boolean isWriteable(long txTimestamp, Object version, Comparator versionComparator);
 
 		/**
 		 * Returns the enclosed value.
 		 */
 		Object getValue();
 
 		/**
 		 * Returns <code>true</code> if the given lock can be unlocked using the given SoftLock instance as a handle.
 		 */
 		boolean isUnlockable(SoftLock lock);
 
 		/**
 		 * Locks this entry, stamping it with the UUID and lockId given, with the lock timeout occuring at the specified
 		 * time.  The returned Lock object can be used to unlock the entry in the future.
 		 */
 		Lock lock(long timeout, UUID uuid, long lockId);
 	}
 
 	/**
 	 * Wrapper type representing unlocked items.
 	 */
-	protected final static class Item implements Serializable, Lockable {
+	public final static class Item implements Serializable, Lockable {
 
 		private static final long serialVersionUID = 1L;
 		private final Object value;
 		private final Object version;
 		private final long timestamp;
 
 		/**
 		 * Creates an unlocked item wrapping the given value with a version and creation timestamp.
 		 */
 		Item(Object value, Object version, long timestamp) {
 			this.value = value;
 			this.version = version;
 			this.timestamp = timestamp;
 		}
 
 		@Override
 		public boolean isReadable(long txTimestamp) {
 			return txTimestamp > timestamp;
 		}
 
 		@Override
 		public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
 			return version != null && versionComparator.compare( version, newVersion ) < 0;
 		}
 
 		@Override
 		public Object getValue() {
 			return value;
 		}
 
 		@Override
 		public boolean isUnlockable(SoftLock lock) {
 			return false;
 		}
 
 		@Override
 		public Lock lock(long timeout, UUID uuid, long lockId) {
 			return new Lock( timeout, uuid, lockId, version );
 		}
 	}
 
 	/**
 	 * Wrapper type representing locked items.
 	 */
-	protected final static class Lock implements Serializable, Lockable, SoftLock {
+	public final static class Lock implements Serializable, Lockable, SoftLock {
 
 		private static final long serialVersionUID = 2L;
 
 		private final UUID sourceUuid;
 		private final long lockId;
 		private final Object version;
 
 		private long timeout;
 		private boolean concurrent;
 		private int multiplicity = 1;
 		private long unlockTimestamp;
 
 		/**
 		 * Creates a locked item with the given identifiers and object version.
 		 */
 		Lock(long timeout, UUID sourceUuid, long lockId, Object version) {
 			this.timeout = timeout;
 			this.lockId = lockId;
 			this.version = version;
 			this.sourceUuid = sourceUuid;
 		}
 
 		@Override
 		public boolean isReadable(long txTimestamp) {
 			return false;
 		}
 
 		@Override
 		public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
 			if ( txTimestamp > timeout ) {
 				// if timedout then allow write
 				return true;
 			}
 			if ( multiplicity > 0 ) {
 				// if still locked then disallow write
 				return false;
 			}
 			return version == null ? txTimestamp > unlockTimestamp : versionComparator.compare(
 					version,
 					newVersion
 			) < 0;
 		}
 
 		@Override
 		public Object getValue() {
 			return null;
 		}
 
 		@Override
 		public boolean isUnlockable(SoftLock lock) {
 			return equals( lock );
 		}
 
 		@Override
 		public boolean equals(Object o) {
 			if ( o == this ) {
 				return true;
 			}
 			else if ( o instanceof Lock ) {
 				return ( lockId == ( (Lock) o ).lockId ) && sourceUuid.equals( ( (Lock) o ).sourceUuid );
 			}
 			else {
 				return false;
 			}
 		}
 
 		@Override
 		public int hashCode() {
 			int hash = ( sourceUuid != null ? sourceUuid.hashCode() : 0 );
 			int temp = (int) lockId;
 			for ( int i = 1; i < Long.SIZE / Integer.SIZE; i++ ) {
 				temp ^= ( lockId >>> ( i * Integer.SIZE ) );
 			}
 			return hash + temp;
 		}
 
 		/**
 		 * Returns true if this Lock has been concurrently locked by more than one transaction.
 		 */
 		public boolean wasLockedConcurrently() {
 			return concurrent;
 		}
 
 		@Override
 		public Lock lock(long timeout, UUID uuid, long lockId) {
 			concurrent = true;
 			multiplicity++;
 			this.timeout = timeout;
 			return this;
 		}
 
 		/**
 		 * Unlocks this Lock, and timestamps the unlock event.
 		 */
 		public void unlock(long timestamp) {
 			if ( --multiplicity == 0 ) {
 				unlockTimestamp = timestamp;
 			}
 		}
 
 		@Override
 		public String toString() {
 			return "Lock Source-UUID:" + sourceUuid + " Lock-ID:" + lockId;
 		}
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegionAccessStrategy.java
index bc568049c8..aea7c62bfe 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegionAccessStrategy.java
@@ -1,118 +1,118 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Strong Liu
  */
-abstract class BaseRegionAccessStrategy implements RegionAccessStrategy {
+public abstract class BaseRegionAccessStrategy implements RegionAccessStrategy {
 
 	private static final Logger LOG = Logger.getLogger( BaseRegionAccessStrategy.class );
 
 	protected abstract BaseGeneralDataRegion getInternalRegion();
 
 	protected abstract boolean isDefaultMinimalPutOverride();
 
 	@Override
 	public Object get(SharedSessionContractImplementor session, Object key, long txTimestamp) throws CacheException {
 		return getInternalRegion().get( session, key );
 	}
 
 	@Override
 	public boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version) throws CacheException {
 		return putFromLoad(session, key, value, txTimestamp, version, isDefaultMinimalPutOverride() );
 	}
 
 	@Override
 	public boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 
 		if ( key == null || value == null ) {
 			return false;
 		}
 		if ( minimalPutOverride && getInternalRegion().contains( key ) ) {
 			LOG.debugf( "Item already cached: %s", key );
 			return false;
 		}
 		LOG.debugf( "Caching: %s", key );
 		getInternalRegion().put( session, key, value );
 		return true;
 
 	}
 
 	/**
 	 * Region locks are not supported.
 	 *
 	 * @return <code>null</code>
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#lockRegion()
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#lockRegion()
 	 */
 	@Override
 	public SoftLock lockRegion() throws CacheException {
 		return null;
 	}
 
 	/**
 	 * Region locks are not supported - perform a cache clear as a precaution.
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#unlockRegion(org.hibernate.cache.spi.access.SoftLock)
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#unlockRegion(org.hibernate.cache.spi.access.SoftLock)
 	 */
 	@Override
 	public void unlockRegion(SoftLock lock) throws CacheException {
 		evictAll();
 	}
 
 	@Override
 	public SoftLock lockItem(SharedSessionContractImplementor session, Object key, Object version) throws CacheException {
 		return null;
 	}
 
 	@Override
 	public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
 	}
 
 
 	/**
 	 * A no-op since this is an asynchronous cache access strategy.
 	 *
 	 * @see RegionAccessStrategy#remove(SharedSessionContractImplementor, Object)
 	 */
 	@Override
 	public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
 	}
 
 	/**
 	 * Called to evict data from the entire region
 	 *
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#removeAll()
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#removeAll()
 	 */
 	@Override
 	public void removeAll() throws CacheException {
 		evictAll();
 	}
 
 	@Override
 	public void evict(Object key) throws CacheException {
 		getInternalRegion().evict( key );
 	}
 
 	@Override
 	public void evictAll() throws CacheException {
 		getInternalRegion().evictAll();
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteEntityRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteEntityRegionAccessStrategy.java
index e4ed531ef0..dba17638d7 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteEntityRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteEntityRegionAccessStrategy.java
@@ -1,120 +1,120 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import java.util.Comparator;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * @author Strong Liu
  */
-class ReadWriteEntityRegionAccessStrategy extends AbstractReadWriteAccessStrategy
+public class ReadWriteEntityRegionAccessStrategy extends AbstractReadWriteAccessStrategy
 		implements EntityRegionAccessStrategy {
 	private final EntityRegionImpl region;
 
 	ReadWriteEntityRegionAccessStrategy(EntityRegionImpl region) {
 		this.region = region;
 	}
 
 	@Override
 	public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
 		return false;
 	}
 
 	@Override
 	public boolean update(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion)
 			throws CacheException {
 		return false;
 	}
 
 	@Override
 	public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
 
 		try {
 			writeLock.lock();
 			Lockable item = (Lockable) region.get( session, key );
 			if ( item == null ) {
 				region.put( session, key, new Item( value, version, region.nextTimestamp() ) );
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 
 	@Override
 	public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
 			throws CacheException {
 		try {
 			writeLock.lock();
 			Lockable item = (Lockable) region.get( session, key );
 
 			if ( item != null && item.isUnlockable( lock ) ) {
 				Lock lockItem = (Lock) item;
 				if ( lockItem.wasLockedConcurrently() ) {
 					decrementLock(session, key, lockItem );
 					return false;
 				}
 				else {
 					region.put( session, key, new Item( value, currentVersion, region.nextTimestamp() ) );
 					return true;
 				}
 			}
 			else {
 				handleLockExpiry(session, key, item );
 				return false;
 			}
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 
 	@Override
 	protected BaseGeneralDataRegion getInternalRegion() {
 		return region;
 	}
 
 	@Override
 	protected boolean isDefaultMinimalPutOverride() {
 		return region.getSettings().isMinimalPutsEnabled();
 	}
 
 	@Override
 	Comparator getVersionComparator() {
 		return region.getCacheDataDescription().getVersionComparator();
 	}
 
 	@Override
 	public EntityRegion getRegion() {
 		return region;
 	}
 
 	@Override
 	public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
 		return region.getRegionFactory().getCacheKeysFactory().createEntityKey( id, persister, factory, tenantIdentifier );
 	}
 
 	@Override
 	public Object getCacheKeyId(Object cacheKey) {
 		return region.getRegionFactory().getCacheKeysFactory().getEntityId( cacheKey );
 	}
 }
