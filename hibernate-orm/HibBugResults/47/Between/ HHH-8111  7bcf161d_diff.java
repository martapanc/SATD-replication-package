diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index 57220006eb..6f660262bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,611 +1,601 @@
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
 package org.hibernate.mapping;
 
 import javax.persistence.AttributeConverter;
 import java.lang.annotation.Annotation;
 import java.lang.reflect.TypeVariable;
-import java.sql.CallableStatement;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.AbstractSingleColumnStandardBasicType;
 import org.hibernate.type.Type;
-import org.hibernate.type.descriptor.ValueBinder;
-import org.hibernate.type.descriptor.ValueExtractor;
-import org.hibernate.type.descriptor.WrapperOptions;
+import org.hibernate.type.descriptor.converter.AttributeConverterSqlTypeDescriptorAdapter;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
-import org.hibernate.type.descriptor.sql.BasicBinder;
-import org.hibernate.type.descriptor.sql.BasicExtractor;
 import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry;
 import org.hibernate.usertype.DynamicParameterizedType;
 
 /**
  * Any value that maps to columns.
  * @author Gavin King
  */
 public class SimpleValue implements KeyValue {
 	private static final Logger log = Logger.getLogger( SimpleValue.class );
 
 	public static final String DEFAULT_ID_GEN_STRATEGY = "assigned";
 
 	private final Mappings mappings;
 
 	private final List<Selectable> columns = new ArrayList<Selectable>();
 
 	private String typeName;
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private boolean alternateUniqueKey;
 	private Properties typeParameters;
 	private boolean cascadeDeleteEnabled;
 
 	private AttributeConverterDefinition jpaAttributeConverterDefinition;
 	private Type type;
 
 	public SimpleValue(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	public SimpleValue(Mappings mappings, Table table) {
 		this( mappings );
 		this.table = table;
 	}
 
 	public Mappings getMappings() {
 		return mappings;
 	}
 
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
 		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
 	}
 	
 	public void addColumn(Column column) {
 		if ( !columns.contains(column) ) columns.add(column);
 		column.setValue(this);
 		column.setTypeIndex( columns.size()-1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add(formula);
 	}
 	
 	public boolean hasFormula() {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Object o = iter.next();
 			if (o instanceof Formula) return true;
 		}
 		return false;
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 	public Iterator<Selectable> getColumnIterator() {
 		return columns.iterator();
 	}
 	public List getConstraintColumns() {
 		return columns;
 	}
 	public String getTypeName() {
 		return typeName;
 	}
 	public void setTypeName(String type) {
 		this.typeName = type;
 	}
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public void createForeignKey() throws MappingException {}
 
 	public void createForeignKeyOfEntity(String entityName) {
 		if ( !hasFormula() && !"none".equals(getForeignKeyName())) {
 			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName );
 			fk.setCascadeDeleteEnabled(cascadeDeleteEnabled);
 		}
 	}
 
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect, 
 			String defaultCatalog, 
 			String defaultSchema, 
 			RootClass rootClass) throws MappingException {
 		
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
 				if ( iter.hasNext() ) tables.append(", ");
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
 		params.put(
 				Environment.PREFER_POOLED_VALUES_LO,
 				mappings.getConfigurationProperties().getProperty( Environment.PREFER_POOLED_VALUES_LO, "false" )
 		);
 
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
 		
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
 		if ( hasFormula() ) return true;
 		boolean nullable = true;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			if ( !( (Column) iter.next() ).isNullable() ) {
 				nullable = false;
 				return nullable; //shortcut
 			}
 		}
 		return nullable;
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
 
 		Type result = mappings.getTypeResolver().heuristicType( typeName, typeParameters );
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
 
-	@SuppressWarnings("unchecked")
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
 
 		if ( jpaAttributeConverterDefinition == null ) {
 			// this is here to work like legacy.  This should change when we integrate with metamodel to
 			// look for SqlTypeDescriptor and JavaTypeDescriptor individually and create the BasicType (well, really
 			// keep a registry of [SqlTypeDescriptor,JavaTypeDescriptor] -> BasicType...)
 			if ( className == null ) {
 				throw new MappingException( "you must specify types for a dynamic entity: " + propertyName );
 			}
 			typeName = ReflectHelper.reflectedPropertyClass( className, propertyName ).getName();
 			return;
 		}
 
 		// we had an AttributeConverter...
+		type = buildAttributeConverterTypeAdapter();
+	}
+
+	/**
+	 * Build a Hibernate Type that incorporates the JPA AttributeConverter.  AttributeConverter works totally in
+	 * memory, meaning it converts between one Java representation (the entity attribute representation) and another
+	 * (the value bound into JDBC statements or extracted from results).  However, the Hibernate Type system operates
+	 * at the lower level of actually dealing directly with those JDBC objects.  So even though we have an
+	 * AttributeConverter, we still need to "fill out" the rest of the BasicType data and bridge calls
+	 * to bind/extract through the converter.
+	 * <p/>
+	 * Essentially the idea here is that an intermediate Java type needs to be used.  Let's use an example as a means
+	 * to illustrate...  Consider an {@code AttributeConverter<Integer,String>}.  This tells Hibernate that the domain
+	 * model defines this attribute as an Integer value (the 'entityAttributeJavaType'), but that we need to treat the
+	 * value as a String (the 'databaseColumnJavaType') when dealing with JDBC (aka, the database type is a
+	 * VARCHAR/CHAR):<ul>
+	 *     <li>
+	 *         When binding values to PreparedStatements we need to convert the Integer value from the entity
+	 *         into a String and pass that String to setString.  The conversion is handled by calling
+	 *         {@link AttributeConverter#convertToDatabaseColumn(Object)}
+	 *     </li>
+	 *     <li>
+	 *         When extracting values from ResultSets (or CallableStatement parameters) we need to handle the
+	 *         value via getString, and convert that returned String to an Integer.  That conversion is handled
+	 *         by calling {@link AttributeConverter#convertToEntityAttribute(Object)}
+	 *     </li>
+	 * </ul>
+	 *
+	 * For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
+	 // the AttributeConverter to resolve the corresponding descriptor.  For the SqlTypeDescriptor portion we use the
+	 // "database column representation" part of the AttributeConverter to resolve the "recommended" JDBC type-code
+	 // and use that type-code to resolve the SqlTypeDescriptor to use.
+	 *
+	 *
+	 * <p/>
+	 *
+	 * <p/>
+	 * <p/>
+	 *
+	 * @todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
+	 * then we can "play them against each other" in terms of determining proper typing
+	 *
+	 * @todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
+	 *
+	 * @return The built AttributeConverter -> Type adapter
+	 */
+	@SuppressWarnings("unchecked")
+	private Type buildAttributeConverterTypeAdapter() {
+		// todo : validate the number of columns present here?
 
-		// todo : we should validate the number of columns present
-		// todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
-		//		then we can "play them against each other" in terms of determining proper typing
-		// todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
-
-		// AttributeConverter works totally in memory, meaning it converts between one Java representation (the entity
-		// attribute representation) and another (the value bound into JDBC statements or extracted from results).
-		// However, the Hibernate Type system operates at the lower level of actually dealing with those JDBC objects.
-		// So even though we have an AttributeConverter, we still need to "fill out" the rest of the BasicType
-		// data.  For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
-		// the AttributeConverter to resolve the corresponding descriptor.  For the SqlTypeDescriptor portion we use the
-		// "database column representation" part of the AttributeConverter to resolve the "recommended" JDBC type-code
-		// and use that type-code to resolve the SqlTypeDescriptor to use.
 		final Class entityAttributeJavaType = jpaAttributeConverterDefinition.getEntityAttributeType();
 		final Class databaseColumnJavaType = jpaAttributeConverterDefinition.getDatabaseColumnType();
-		final int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
 
-		final JavaTypeDescriptor javaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
+
+		// resolve the JavaTypeDescriptor ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
+		// the AttributeConverter to resolve the corresponding descriptor.
+		final JavaTypeDescriptor entityAttributeJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
+
+
+		// build the SqlTypeDescriptor adapter ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// Going back to the illustration, this should be a SqlTypeDescriptor that handles the Integer <-> String
+		//		conversions.  This is the more complicated piece.  First we need to determine the JDBC type code
+		//		corresponding to the AttributeConverter's declared "databaseColumnJavaType" (how we read that value out
+		// 		of ResultSets).  See JdbcTypeJavaClassMappings for details.  Again, given example, this should return
+		// 		VARCHAR/CHAR
+		final int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
+		// find the standard SqlTypeDescriptor for that JDBC type code.
 		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
-		// the adapter here injects the AttributeConverter calls into the binding/extraction process...
+		// find the JavaTypeDescriptor representing the "intermediate database type representation".  Back to the
+		// 		illustration, this should be the type descriptor for Strings
+		final JavaTypeDescriptor intermediateJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( databaseColumnJavaType );
+		// and finally construct the adapter, which injects the AttributeConverter calls into the binding/extraction
+		// 		process...
 		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
 				jpaAttributeConverterDefinition.getAttributeConverter(),
-				sqlTypeDescriptor
+				sqlTypeDescriptor,
+				intermediateJavaTypeDescriptor
 		);
 
+
 		final String name = "BasicType adapter for AttributeConverter<" + entityAttributeJavaType + "," + databaseColumnJavaType + ">";
-		type = new AbstractSingleColumnStandardBasicType( sqlTypeDescriptorAdapter, javaTypeDescriptor ) {
+		final Type type = new AbstractSingleColumnStandardBasicType( sqlTypeDescriptorAdapter, entityAttributeJavaTypeDescriptor ) {
 			@Override
 			public String getName() {
 				return name;
 			}
 		};
 		log.debug( "Created : " + name );
 
 		// todo : cache the BasicType we just created in case that AttributeConverter is applied multiple times.
+
+		return type;
 	}
 
 	private Class extractType(TypeVariable typeVariable) {
 		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
 		if ( boundTypes == null || boundTypes.length != 1 ) {
 			return null;
 		}
 
 		return (Class) boundTypes[0];
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
 
 	public void setJpaAttributeConverterDefinition(AttributeConverterDefinition jpaAttributeConverterDefinition) {
 		this.jpaAttributeConverterDefinition = jpaAttributeConverterDefinition;
 	}
 
-	public static class AttributeConverterSqlTypeDescriptorAdapter implements SqlTypeDescriptor {
-		private final AttributeConverter converter;
-		private final SqlTypeDescriptor delegate;
-
-		public AttributeConverterSqlTypeDescriptorAdapter(AttributeConverter converter, SqlTypeDescriptor delegate) {
-			this.converter = converter;
-			this.delegate = delegate;
-		}
-
-		@Override
-		public int getSqlType() {
-			return delegate.getSqlType();
-		}
-
-		@Override
-		public boolean canBeRemapped() {
-			return delegate.canBeRemapped();
-		}
-
-		@Override
-		public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
-			final ValueBinder realBinder = delegate.getBinder( javaTypeDescriptor );
-			return new BasicBinder<X>( javaTypeDescriptor, this ) {
-				@Override
-				@SuppressWarnings("unchecked")
-				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
-						throws SQLException {
-					realBinder.bind( st, converter.convertToDatabaseColumn( value ), index, options );
-				}
-			};
-		}
-
-		@Override
-		public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor) {
-			final ValueExtractor realExtractor = delegate.getExtractor( javaTypeDescriptor );
-			return new BasicExtractor<X>( javaTypeDescriptor, this ) {
-				@Override
-				@SuppressWarnings("unchecked")
-				protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
-					return (X) converter.convertToEntityAttribute( realExtractor.extract( rs, name, options ) );
-				}
-
-				@Override
-				@SuppressWarnings("unchecked")
-				protected X doExtract(CallableStatement statement, int index, WrapperOptions options)
-						throws SQLException {
-					return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, index, options ) );
-				}
-
-				@Override
-				@SuppressWarnings("unchecked")
-				protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
-					return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, new String[] {name}, options ) );
-				}
-			};
-		}
-	}
-
 	private void createParameterImpl() {
 		try {
 			String[] columnsNames = new String[columns.size()];
 			for ( int i = 0; i < columns.size(); i++ ) {
 				columnsNames[i] = ( (Column) columns.get( i ) ).getName();
 			}
 
 			final XProperty xProperty = (XProperty) typeParameters.get( DynamicParameterizedType.XPROPERTY );
 			// todo : not sure this works for handling @MapKeyEnumerated
 			final Annotation[] annotations = xProperty == null
 					? null
 					: xProperty.getAnnotations();
 
 			typeParameters.put(
 					DynamicParameterizedType.PARAMETER_TYPE,
 					new ParameterTypeImpl(
 							ReflectHelper.classForName(
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
 		catch ( ClassNotFoundException cnfe ) {
 			throw new MappingException( "Could not create DynamicParameterizedType for type: " + typeName, cnfe );
 		}
 	}
 
 	private final class ParameterTypeImpl implements DynamicParameterizedType.ParameterType {
 
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
new file mode 100644
index 0000000000..80c8511ee2
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/AttributeConverterSqlTypeDescriptorAdapter.java
@@ -0,0 +1,121 @@
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
+package org.hibernate.type.descriptor.converter;
+
+import javax.persistence.AttributeConverter;
+import java.sql.CallableStatement;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+
+import org.hibernate.type.descriptor.ValueBinder;
+import org.hibernate.type.descriptor.ValueExtractor;
+import org.hibernate.type.descriptor.WrapperOptions;
+import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
+import org.hibernate.type.descriptor.sql.BasicBinder;
+import org.hibernate.type.descriptor.sql.BasicExtractor;
+import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
+
+/**
+ * Adapter for incorporating JPA {@link AttributeConverter} handling into the SqlTypeDescriptor contract.
+ * <p/>
+ * Essentially this is responsible for mapping to/from the intermediate database type representation.  Continuing the
+ * {@code AttributeConverter<Integer,String>} example from
+ * {@link org.hibernate.mapping.SimpleValue#buildAttributeConverterTypeAdapter()}, the "intermediate database type
+ * representation" would be the String representation.  So on binding, we convert the incoming Integer to String;
+ * on extraction we extract the value as String and convert to Integer.
+ *
+ * @author Steve Ebersole
+ */
+public class AttributeConverterSqlTypeDescriptorAdapter implements SqlTypeDescriptor {
+	private final AttributeConverter converter;
+	private final SqlTypeDescriptor delegate;
+	private final JavaTypeDescriptor intermediateJavaTypeDescriptor;
+
+	public AttributeConverterSqlTypeDescriptorAdapter(
+			AttributeConverter converter,
+			SqlTypeDescriptor delegate,
+			JavaTypeDescriptor intermediateJavaTypeDescriptor) {
+		this.converter = converter;
+		this.delegate = delegate;
+		this.intermediateJavaTypeDescriptor = intermediateJavaTypeDescriptor;
+	}
+
+	@Override
+	public int getSqlType() {
+		return delegate.getSqlType();
+	}
+
+	@Override
+	public boolean canBeRemapped() {
+		// todo : consider the ramifications of this.
+		// certainly we need to account for the remapping of the delegate sql-type, but is it really valid to
+		// allow remapping of the converter sql-type?
+		return delegate.canBeRemapped();
+	}
+
+
+	// Binding ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	@SuppressWarnings("unchecked")
+	public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
+		// Get the binder for the intermediate type representation
+		final ValueBinder realBinder = delegate.getBinder( intermediateJavaTypeDescriptor );
+		return new BasicBinder<X>( javaTypeDescriptor, this ) {
+			@Override
+			@SuppressWarnings("unchecked")
+			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
+				realBinder.bind( st, converter.convertToDatabaseColumn( value ), index, options );
+			}
+		};
+	}
+
+
+	// Extraction ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor) {
+		final ValueExtractor realExtractor = delegate.getExtractor( intermediateJavaTypeDescriptor );
+		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
+			@Override
+			@SuppressWarnings("unchecked")
+			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
+				return (X) converter.convertToEntityAttribute( realExtractor.extract( rs, name, options ) );
+			}
+
+			@Override
+			@SuppressWarnings("unchecked")
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, index, options ) );
+			}
+
+			@Override
+			@SuppressWarnings("unchecked")
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, new String[] {name}, options ) );
+			}
+		};
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/package-info.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/package-info.java
new file mode 100644
index 0000000000..2f67826f26
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/converter/package-info.java
@@ -0,0 +1,5 @@
+/**
+ * Support for handling JPA {@link javax.persistence.AttributeConverter} instances as part of the
+ * Hibernate {@link org.hibernate.type.Type} system.
+ */
+package org.hibernate.type.descriptor.converter;
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JavaTypeDescriptorRegistry.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JavaTypeDescriptorRegistry.java
index 48e94a1dca..d5b1da2fac 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JavaTypeDescriptorRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JavaTypeDescriptorRegistry.java
@@ -1,112 +1,116 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
  * Basically a map from {@link Class} -> {@link JavaTypeDescriptor}
  *
  * @author Steve Ebersole
  */
 public class JavaTypeDescriptorRegistry {
+	private static final Logger log = Logger.getLogger( JavaTypeDescriptorRegistry.class );
+
 	public static final JavaTypeDescriptorRegistry INSTANCE = new JavaTypeDescriptorRegistry();
 
 	private ConcurrentHashMap<Class,JavaTypeDescriptor> descriptorsByClass = new ConcurrentHashMap<Class, JavaTypeDescriptor>();
 
 	/**
 	 * Adds the given descriptor to this registry
 	 *
 	 * @param descriptor The descriptor to add.
 	 */
 	public void addDescriptor(JavaTypeDescriptor descriptor) {
 		descriptorsByClass.put( descriptor.getJavaTypeClass(), descriptor );
 	}
 
 	@SuppressWarnings("unchecked")
 	public <T> JavaTypeDescriptor<T> getDescriptor(Class<T> cls) {
 		if ( cls == null ) {
 			throw new IllegalArgumentException( "Class passed to locate Java type descriptor cannot be null" );
 		}
 
 		JavaTypeDescriptor<T> descriptor = descriptorsByClass.get( cls );
 		if ( descriptor != null ) {
 			return descriptor;
 		}
 
+		if ( Serializable.class.isAssignableFrom( cls ) ) {
+			return new SerializableTypeDescriptor( cls );
+		}
+
 		// find the first "assignable" match
 		for ( Map.Entry<Class,JavaTypeDescriptor> entry : descriptorsByClass.entrySet() ) {
-			if ( cls.isAssignableFrom( entry.getKey() ) ) {
+			if ( entry.getKey().isAssignableFrom( cls ) ) {
+				log.debugf( "Using  cached JavaTypeDescriptor instance for Java class [%s]", cls.getName() );
 				return entry.getValue();
 			}
 		}
 
-		// we could not find one; warn the user (as stuff is likely to break later) and create a fallback instance...
-		if ( Serializable.class.isAssignableFrom( cls ) ) {
-			return new SerializableTypeDescriptor( cls );
-		}
-		else {
-			return new FallbackJavaTypeDescriptor<T>( cls );
-		}
+		log.warnf( "Could not find matching type descriptor for requested Java class [%s]; using fallback", cls.getName() );
+		return new FallbackJavaTypeDescriptor<T>( cls );
 	}
 
 	public static class FallbackJavaTypeDescriptor<T> extends AbstractTypeDescriptor<T> {
 
 		@SuppressWarnings("unchecked")
 		protected FallbackJavaTypeDescriptor(Class<T> type) {
 			// MutableMutabilityPlan would be the "safest" option, but we do not necessarily know how to deepCopy etc...
 			super( type, ImmutableMutabilityPlan.INSTANCE );
 		}
 
 		@Override
 		public String toString(T value) {
 			return value == null ? "<null>" : value.toString();
 		}
 
 		@Override
 		public T fromString(String string) {
 			throw new HibernateException(
 					"Not known how to convert String to given type [" + getJavaTypeClass().getName() + "]"
 			);
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public <X> X unwrap(T value, Class<X> type, WrapperOptions options) {
 			return (X) value;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public <X> T wrap(X value, WrapperOptions options) {
 			return (T) value;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java
index 493a5247e5..fc87fb9773 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java
@@ -1,142 +1,157 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.io.ByteArrayInputStream;
 import java.io.InputStream;
 import java.io.Serializable;
 import java.sql.Blob;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.jdbc.internal.BinaryStreamImpl;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.engine.jdbc.BinaryStream;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
  * Descriptor for general {@link Serializable} handling.
  *
  * @author Steve Ebersole
  * @author Brett meyer
  */
 public class SerializableTypeDescriptor<T extends Serializable> extends AbstractTypeDescriptor<T> {
 
 	// unfortunately the param types cannot be the same so use something other than 'T' here to make that obvious
 	public static class SerializableMutabilityPlan<S extends Serializable> extends MutableMutabilityPlan<S> {
 		private final Class<S> type;
 
 		public static final SerializableMutabilityPlan<Serializable> INSTANCE
 				= new SerializableMutabilityPlan<Serializable>( Serializable.class );
 
 		public SerializableMutabilityPlan(Class<S> type) {
 			this.type = type;
 		}
 
 		@Override
         @SuppressWarnings({ "unchecked" })
 		public S deepCopyNotNull(S value) {
 			return (S) SerializationHelper.clone( value );
 		}
 
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public SerializableTypeDescriptor(Class<T> type) {
 		super(
 				type,
 				Serializable.class.equals( type )
 						? (MutabilityPlan<T>) SerializableMutabilityPlan.INSTANCE
 						: new SerializableMutabilityPlan<T>( type )
 		);
 	}
 
 	public String toString(T value) {
 		return PrimitiveByteArrayTypeDescriptor.INSTANCE.toString( toBytes( value ) );
 	}
 
 	public T fromString(String string) {
 		return fromBytes( PrimitiveByteArrayTypeDescriptor.INSTANCE.fromString( string ) );
 	}
 
 	@Override
 	public boolean areEqual(T one, T another) {
 		if ( one == another ) {
 			return true;
 		}
 		if ( one == null || another == null ) {
 			return false;
 		}
 		return one.equals( another )
 				|| PrimitiveByteArrayTypeDescriptor.INSTANCE.areEqual( toBytes( one ), toBytes( another ) );
 	}
 
 	@Override
 	public int extractHashCode(T value) {
 		return PrimitiveByteArrayTypeDescriptor.INSTANCE.extractHashCode( toBytes( value ) );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(T value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
-		} else if ( byte[].class.isAssignableFrom( type ) ) {
+		}
+		else if ( type.isInstance( value ) ) {
+			return (X) value;
+		}
+		else if ( byte[].class.isAssignableFrom( type ) ) {
 			return (X) toBytes( value );
-		} else if ( InputStream.class.isAssignableFrom( type ) ) {
+		}
+		else if ( InputStream.class.isAssignableFrom( type ) ) {
 			return (X) new ByteArrayInputStream( toBytes( value ) );
-		} else if ( BinaryStream.class.isAssignableFrom( type ) ) {
+		}
+		else if ( BinaryStream.class.isAssignableFrom( type ) ) {
 			return (X) new BinaryStreamImpl( toBytes( value ) );
-		} else if ( Blob.class.isAssignableFrom( type )) {
+		}
+		else if ( Blob.class.isAssignableFrom( type )) {
 			return (X) options.getLobCreator().createBlob( toBytes(value) );
 		}
 		
 		throw unknownUnwrap( type );
 	}
 
+	@SuppressWarnings("unchecked")
 	public <X> T wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
-		} else if ( byte[].class.isInstance( value ) ) {
+		}
+		else if ( byte[].class.isInstance( value ) ) {
 			return fromBytes( (byte[]) value );
-		} else if ( InputStream.class.isInstance( value ) ) {
+		}
+		else if ( InputStream.class.isInstance( value ) ) {
 			return fromBytes( DataHelper.extractBytes( (InputStream) value ) );
-		} else if ( Blob.class.isInstance( value )) {
+		}
+		else if ( Blob.class.isInstance( value )) {
 			try {
 				return fromBytes( DataHelper.extractBytes( ( (Blob) value ).getBinaryStream() ) );
-			} catch ( SQLException e ) {
+			}
+			catch ( SQLException e ) {
 				throw new HibernateException(e);
 			}
 		}
+		else if ( getJavaTypeClass().isInstance( value ) ) {
+			return (T) value;
+		}
 		throw unknownWrap( value.getClass() );
 	}
 
 	protected byte[] toBytes(T value) {
 		return SerializationHelper.serialize( value );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected T fromBytes(byte[] bytes) {
 		return (T) SerializationHelper.deserialize( bytes, getJavaTypeClass().getClassLoader() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/JdbcTypeJavaClassMappings.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/JdbcTypeJavaClassMappings.java
index 9d894b5343..e837f1b521 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/JdbcTypeJavaClassMappings.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/JdbcTypeJavaClassMappings.java
@@ -1,132 +1,135 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.math.BigDecimal;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.Date;
 import java.sql.Ref;
 import java.sql.Struct;
 import java.sql.Time;
 import java.sql.Timestamp;
 import java.sql.Types;
 import java.util.Calendar;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.mapping.Array;
 
 /**
- * Presents recommended {@literal JDCB typecode <-> Java Class} mappings.  Currently the recommendations
- * contained here come from the JDBC spec itself, as outlined at <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/jdbc/getstart/mapping.html#1034737"/>
- * Eventually, the plan is to have {@link org.hibernate.dialect.Dialect} contribute this information.
+ * Presents recommended {@literal JDCB typecode <-> Java Class} mappings.  Currently the mappings contained here come
+ * from the recommendations defined by the JDBC spec itself, as outlined at
+ * <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/jdbc/getstart/mapping.html#1034737"/>.
+ * <p/>
+ * Eventually, the plan is to have {@link org.hibernate.dialect.Dialect} and
+ * {@link java.sql.DatabaseMetaData#getTypeInfo()} contribute this information.
  *
  * @author Steve Ebersole
  */
 public class JdbcTypeJavaClassMappings {
 	private static final Logger log = Logger.getLogger( JdbcTypeJavaClassMappings.class );
 
 	private static final ConcurrentHashMap<Class, Integer> javaClassToJdbcTypeCodeMap = buildJdbcJavaClassMappings();
 	private static final ConcurrentHashMap<Integer, Class> jdbcTypeCodeToJavaClassMap = transpose( javaClassToJdbcTypeCodeMap );
 
 	public static final JdbcTypeJavaClassMappings INSTANCE = new JdbcTypeJavaClassMappings();
 
 	private JdbcTypeJavaClassMappings() {
 	}
 
 	public int determineJdbcTypeCodeForJavaClass(Class cls) {
 		Integer typeCode = JdbcTypeJavaClassMappings.javaClassToJdbcTypeCodeMap.get( cls );
 		if ( typeCode != null ) {
 			return typeCode;
 		}
 
 		int specialCode = cls.hashCode();
 		log.debug(
 				"JDBC type code mapping not known for class [" + cls.getName() + "]; using custom code [" + specialCode + "]"
 		);
 		return specialCode;
 	}
 
 	public Class determineJavaClassForJdbcTypeCode(int typeCode) {
 		Class cls = jdbcTypeCodeToJavaClassMap.get( Integer.valueOf( typeCode ) );
 		if ( cls != null ) {
 			return cls;
 		}
 
 		log.debugf(
 				"Java Class mapping not known for JDBC type code [%s]; using java.lang.Object",
 				typeCode
 		);
 		return Object.class;
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private static ConcurrentHashMap<Class, Integer> buildJdbcJavaClassMappings() {
 		ConcurrentHashMap<Class, Integer> jdbcJavaClassMappings = new ConcurrentHashMap<Class, Integer>();
 
 		// these mappings are the ones outlined specifically in the spec
 		jdbcJavaClassMappings.put( String.class, Types.VARCHAR );
 		jdbcJavaClassMappings.put( BigDecimal.class, Types.NUMERIC );
 		jdbcJavaClassMappings.put( Boolean.class, Types.BIT );
 		jdbcJavaClassMappings.put( Integer.class, Types.INTEGER );
 		jdbcJavaClassMappings.put( Long.class, Types.BIGINT );
 		jdbcJavaClassMappings.put( Float.class, Types.REAL );
 		jdbcJavaClassMappings.put( Double.class, Types.DOUBLE );
 		jdbcJavaClassMappings.put( byte[].class, Types.LONGVARBINARY );
 		jdbcJavaClassMappings.put( Date.class, Types.DATE );
 		jdbcJavaClassMappings.put( Time.class, Types.TIME );
 		jdbcJavaClassMappings.put( Timestamp.class, Types.TIMESTAMP );
 		jdbcJavaClassMappings.put( Blob.class, Types.BLOB );
 		jdbcJavaClassMappings.put( Clob.class, Types.CLOB );
 		jdbcJavaClassMappings.put( Array.class, Types.ARRAY );
 		jdbcJavaClassMappings.put( Struct.class, Types.STRUCT );
 		jdbcJavaClassMappings.put( Ref.class, Types.REF );
 		jdbcJavaClassMappings.put( Class.class, Types.JAVA_OBJECT );
 
 		// additional "common sense" registrations
 		jdbcJavaClassMappings.put( Character.class, Types.CHAR );
 		jdbcJavaClassMappings.put( char[].class, Types.VARCHAR );
 		jdbcJavaClassMappings.put( Character[].class, Types.VARCHAR );
 		jdbcJavaClassMappings.put( Byte[].class, Types.LONGVARBINARY );
 		jdbcJavaClassMappings.put( Date.class, Types.TIMESTAMP );
 		jdbcJavaClassMappings.put( Calendar.class, Types.TIMESTAMP );
 
 		return jdbcJavaClassMappings;
 	}
 
 	private static ConcurrentHashMap<Integer, Class> transpose(ConcurrentHashMap<Class, Integer> javaClassToJdbcTypeCodeMap) {
 		final ConcurrentHashMap<Integer, Class> transposed = new ConcurrentHashMap<Integer, Class>();
 
 		for ( Map.Entry<Class,Integer> entry : javaClassToJdbcTypeCodeMap.entrySet() ) {
 			transposed.put( entry.getValue(), entry.getKey() );
 		}
 
 		return transposed;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java b/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java
index a1ab1ced22..895367f738 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java
@@ -1,146 +1,349 @@
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
 package org.hibernate.test.type;
 
 import javax.persistence.AttributeConverter;
 import javax.persistence.Convert;
 import javax.persistence.Converter;
 import javax.persistence.Entity;
 import javax.persistence.Id;
+import java.io.Serializable;
 import java.sql.Clob;
+import java.sql.Timestamp;
 import java.sql.Types;
 
 import org.hibernate.IrrelevantEntity;
+import org.hibernate.Session;
+import org.hibernate.SessionFactory;
 import org.hibernate.cfg.AttributeConverterDefinition;
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.type.AbstractStandardBasicType;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.Type;
+import org.hibernate.type.descriptor.java.JdbcTimestampTypeDescriptor;
 import org.hibernate.type.descriptor.java.StringTypeDescriptor;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 
 /**
  * Tests the principle of adding "AttributeConverter" to the mix of {@link org.hibernate.type.Type} resolution
  *
  * @author Steve Ebersole
  */
 public class AttributeConverterTest extends BaseUnitTestCase {
 	@Test
 	public void testBasicOperation() {
 		Configuration cfg = new Configuration();
 		SimpleValue simpleValue = new SimpleValue( cfg.createMappings() );
 		simpleValue.setJpaAttributeConverterDefinition(
 				new AttributeConverterDefinition( new StringClobConverter(), true )
 		);
 		simpleValue.setTypeUsingReflection( IrrelevantEntity.class.getName(), "name" );
 
 		Type type = simpleValue.getType();
 		assertNotNull( type );
 		assertTyping( BasicType.class, type );
 		AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 		assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 		assertEquals( Types.CLOB, basicType.getSqlTypeDescriptor().getSqlType() );
 	}
 
 	@Test
-	public void testNormalOperation() {
+	public void testBasicConverterApplication() {
 		Configuration cfg = new Configuration();
 		cfg.addAttributeConverter( StringClobConverter.class, true );
 		cfg.addAnnotatedClass( Tester.class );
 		cfg.addAnnotatedClass( Tester2.class );
 		cfg.buildMappings();
 
 		{
 			PersistentClass tester = cfg.getClassMapping( Tester.class.getName() );
 			Property nameProp = tester.getProperty( "name" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			assertTyping( BasicType.class, type );
 			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.CLOB, basicType.getSqlTypeDescriptor().getSqlType() );
 		}
 
 		{
 			PersistentClass tester = cfg.getClassMapping( Tester2.class.getName() );
 			Property nameProp = tester.getProperty( "name" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			assertTyping( BasicType.class, type );
 			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.VARCHAR, basicType.getSqlTypeDescriptor().getSqlType() );
 		}
 	}
 
+	@Test
+	public void testBasicUsage() {
+		Configuration cfg = new Configuration();
+		cfg.addAttributeConverter( IntegerToVarcharConverter.class, false );
+		cfg.addAnnotatedClass( Tester4.class );
+		cfg.setProperty( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
+		cfg.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
 
-	@Entity
+		SessionFactory sf = cfg.buildSessionFactory();
+
+		try {
+			Session session = sf.openSession();
+			session.beginTransaction();
+			session.save( new Tester4( 1L, "steve", 200 ) );
+			session.getTransaction().commit();
+			session.close();
+
+			sf.getStatistics().clear();
+			session = sf.openSession();
+			session.beginTransaction();
+			session.get( Tester4.class, 1L );
+			session.getTransaction().commit();
+			session.close();
+			assertEquals( 0, sf.getStatistics().getEntityUpdateCount() );
+
+			session = sf.openSession();
+			session.beginTransaction();
+			Tester4 t4 = (Tester4) session.get( Tester4.class, 1L );
+			t4.code = 300;
+			session.getTransaction().commit();
+			session.close();
+
+			session = sf.openSession();
+			session.beginTransaction();
+			t4 = (Tester4) session.get( Tester4.class, 1L );
+			assertEquals( 300, t4.code.longValue() );
+			session.delete( t4 );
+			session.getTransaction().commit();
+			session.close();
+		}
+		finally {
+			sf.close();
+		}
+	}
+
+	@Test
+	public void testBasicTimestampUsage() {
+		Configuration cfg = new Configuration();
+		cfg.addAttributeConverter( InstantConverter.class, false );
+		cfg.addAnnotatedClass( IrrelevantInstantEntity.class );
+		cfg.setProperty( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
+		cfg.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
+
+		SessionFactory sf = cfg.buildSessionFactory();
+
+		try {
+			Session session = sf.openSession();
+			session.beginTransaction();
+			session.save( new IrrelevantInstantEntity( 1L ) );
+			session.getTransaction().commit();
+			session.close();
+
+			sf.getStatistics().clear();
+			session = sf.openSession();
+			session.beginTransaction();
+			IrrelevantInstantEntity e = (IrrelevantInstantEntity) session.get( IrrelevantInstantEntity.class, 1L );
+			session.getTransaction().commit();
+			session.close();
+			assertEquals( 0, sf.getStatistics().getEntityUpdateCount() );
+
+			session = sf.openSession();
+			session.beginTransaction();
+			session.delete( e );
+			session.getTransaction().commit();
+			session.close();
+		}
+		finally {
+			sf.close();
+		}
+	}
+
+	// Entity declarations used in the test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Entity(name = "T1")
 	public static class Tester {
 		@Id
 		private Long id;
 		private String name;
+
+		public Tester() {
+		}
+
+		public Tester(Long id, String name) {
+			this.id = id;
+			this.name = name;
+		}
 	}
 
-	@Entity
+	@Entity(name = "T2")
 	public static class Tester2 {
 		@Id
 		private Long id;
 		@Convert(disableConversion = true)
 		private String name;
 	}
 
-	@Entity
+	@Entity(name = "T3")
 	public static class Tester3 {
 		@Id
 		private Long id;
 		@org.hibernate.annotations.Type( type = "string" )
+		@Convert(disableConversion = true)
+		private String name;
+	}
+
+	@Entity(name = "T4")
+	public static class Tester4 {
+		@Id
+		private Long id;
 		private String name;
+		@Convert( converter = IntegerToVarcharConverter.class )
+		private Integer code;
+
+		public Tester4() {
+		}
+
+		public Tester4(Long id, String name, Integer code) {
+			this.id = id;
+			this.name = name;
+			this.code = code;
+		}
 	}
 
+	// This class is for mimicking an Instant from Java 8, which a converter might convert to a java.sql.Timestamp
+	public static class Instant implements Serializable {
+		private static final long serialVersionUID = 1L;
+
+		private long javaMillis;
+
+		public Instant(long javaMillis) {
+			this.javaMillis = javaMillis;
+		}
+
+		public long toJavaMillis() {
+			return javaMillis;
+		}
+
+		public static Instant fromJavaMillis(long javaMillis) {
+			return new Instant( javaMillis );
+		}
+
+		public static Instant now() {
+			return new Instant( System.currentTimeMillis() );
+		}
+	}
+
+	@Entity
+	public static class IrrelevantInstantEntity {
+		@Id
+		private Long id;
+		private Instant dateCreated;
+
+		public IrrelevantInstantEntity() {
+		}
+
+		public IrrelevantInstantEntity(Long id) {
+			this( id, Instant.now() );
+		}
+
+		public IrrelevantInstantEntity(Long id, Instant dateCreated) {
+			this.id = id;
+			this.dateCreated = dateCreated;
+		}
+
+		public Long getId() {
+			return id;
+		}
+
+		public void setId(Long id) {
+			this.id = id;
+		}
+
+		public Instant getDateCreated() {
+			return dateCreated;
+		}
+
+		public void setDateCreated(Instant dateCreated) {
+			this.dateCreated = dateCreated;
+		}
+	}
+
+
+	// Converter declarations used in the test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
 	@Converter( autoApply = true )
 	public static class StringClobConverter implements AttributeConverter<String,Clob> {
 		@Override
 		public Clob convertToDatabaseColumn(String attribute) {
 			return null;
 		}
 
 		@Override
 		public String convertToEntityAttribute(Clob dbData) {
 			return null;
 		}
 	}
+
+	@Converter( autoApply = true )
+	public static class IntegerToVarcharConverter implements AttributeConverter<Integer,String> {
+		@Override
+		public String convertToDatabaseColumn(Integer attribute) {
+			return attribute == null ? null : attribute.toString();
+		}
+
+		@Override
+		public Integer convertToEntityAttribute(String dbData) {
+			return dbData == null ? null : Integer.valueOf( dbData );
+		}
+	}
+
+
+	@Converter( autoApply = true )
+	public static class InstantConverter implements AttributeConverter<Instant, Timestamp> {
+		@Override
+		public Timestamp convertToDatabaseColumn(Instant attribute) {
+			return new Timestamp( attribute.toJavaMillis() );
+		}
+
+		@Override
+		public Instant convertToEntityAttribute(Timestamp dbData) {
+			return Instant.fromJavaMillis( dbData.getTime() );
+		}
+	}
 }
