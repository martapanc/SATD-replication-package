diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index 22ae48bb50..f5fb84a81b 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,607 +1,610 @@
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
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
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
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
 import org.hibernate.type.descriptor.sql.BasicBinder;
 import org.hibernate.type.descriptor.sql.BasicExtractor;
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
 
 	private final List columns = new ArrayList();
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
 	public Iterator getColumnIterator() {
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
 
 	@SuppressWarnings("unchecked")
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
 
 		// todo : we should validate the number of columns present
 		// todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
 		//		then we can "play them against each other" in terms of determining proper typing
 		// todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
 
 		// AttributeConverter works totally in memory, meaning it converts between one Java representation (the entity
 		// attribute representation) and another (the value bound into JDBC statements or extracted from results).
 		// However, the Hibernate Type system operates at the lower level of actually dealing with those JDBC objects.
 		// So even though we have an AttributeConverter, we still need to "fill out" the rest of the BasicType
 		// data.  For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
 		// the AttributeConverter to resolve the corresponding descriptor.  For the SqlTypeDescriptor portion we use the
 		// "database column representation" part of the AttributeConverter to resolve the "recommended" JDBC type-code
 		// and use that type-code to resolve the SqlTypeDescriptor to use.
 		final Class entityAttributeJavaType = jpaAttributeConverterDefinition.getEntityAttributeType();
 		final Class databaseColumnJavaType = jpaAttributeConverterDefinition.getDatabaseColumnType();
 		final int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
 
 		final JavaTypeDescriptor javaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
 		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
 		// the adapter here injects the AttributeConverter calls into the binding/extraction process...
 		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
 				jpaAttributeConverterDefinition.getAttributeConverter(),
 				sqlTypeDescriptor
 		);
 
 		final String name = "BasicType adapter for AttributeConverter<" + entityAttributeJavaType + "," + databaseColumnJavaType + ">";
 		type = new AbstractSingleColumnStandardBasicType( sqlTypeDescriptorAdapter, javaTypeDescriptor ) {
 			@Override
 			public String getName() {
 				return name;
 			}
 		};
 		log.debug( "Created : " + name );
 
 		// todo : cache the BasicType we just created in case that AttributeConverter is applied multiple times.
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
 
 	public static class AttributeConverterSqlTypeDescriptorAdapter implements SqlTypeDescriptor {
 		private final AttributeConverter converter;
 		private final SqlTypeDescriptor delegate;
 
 		public AttributeConverterSqlTypeDescriptorAdapter(AttributeConverter converter, SqlTypeDescriptor delegate) {
 			this.converter = converter;
 			this.delegate = delegate;
 		}
 
 		@Override
 		public int getSqlType() {
 			return delegate.getSqlType();
 		}
 
 		@Override
 		public boolean canBeRemapped() {
 			return delegate.canBeRemapped();
 		}
 
 		@Override
 		public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
 			final ValueBinder realBinder = delegate.getBinder( javaTypeDescriptor );
 			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
 				@SuppressWarnings("unchecked")
 				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 						throws SQLException {
 					realBinder.bind( st, converter.convertToDatabaseColumn( value ), index, options );
 				}
 			};
 		}
 
 		@Override
 		public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor) {
 			final ValueExtractor realExtractor = delegate.getExtractor( javaTypeDescriptor );
 			return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( rs, name, options ) );
 				}
 
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(CallableStatement statement, int index, WrapperOptions options)
 						throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, index, options ) );
 				}
 
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, new String[] {name}, options ) );
 				}
 			};
 		}
 	}
 
 	private void createParameterImpl() {
 		try {
 			String[] columnsNames = new String[columns.size()];
 			for ( int i = 0; i < columns.size(); i++ ) {
 				columnsNames[i] = ( (Column) columns.get( i ) ).getName();
 			}
 
 			final XProperty xProperty = (XProperty) typeParameters.get( DynamicParameterizedType.XPROPERTY );
 			// todo : not sure this works for handling @MapKeyEnumerated
-			final Annotation[] annotations = xProperty.getAnnotations();
+			final Annotation[] annotations = xProperty == null
+					? null
+					: xProperty.getAnnotations();
 
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
-							columnsNames )
+							columnsNames
+					)
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
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/type/EnumType.java b/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
index 284f2da6b0..f4a572960c 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
@@ -1,500 +1,502 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2012, Red Hat Inc. or third-party contributors as
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
 
 import javax.persistence.Enumerated;
 import javax.persistence.MapKeyEnumerated;
 import java.io.Serializable;
 import java.lang.annotation.Annotation;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.usertype.DynamicParameterizedType;
 import org.hibernate.usertype.EnhancedUserType;
 
 /**
  * Value type mapper for enumerations.
  *
  * Generally speaking, the proper configuration is picked up from the annotations associated with the mapped attribute.
  *
  * There are a few configuration parameters understood by this type mapper:<ul>
  *     <li>
  *         <strong>enumClass</strong> - Names the enumeration class.
  *     </li>
  *     <li>
  *         <strong>useNamed</strong> - Should enum be mapped via name.  Default is to map as ordinal.  Used when
  *         annotations are not used (otherwise {@link javax.persistence.EnumType} is used).
  *     </li>
  *     <li>
  *         <strong>type</strong> - Identifies the JDBC type (via type code) to be used for the column.
  *     </li>
  * </ul>
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  * @author Steve Ebersole
  */
 @SuppressWarnings("unchecked")
 public class EnumType implements EnhancedUserType, DynamicParameterizedType, Serializable {
     private static final Logger LOG = Logger.getLogger( EnumType.class.getName() );
 
 	public static final String ENUM = "enumClass";
 	public static final String NAMED = "useNamed";
 	public static final String TYPE = "type";
 
 	private Class<? extends Enum> enumClass;
 	private EnumValueMapper enumValueMapper;
 	private int sqlType = Types.INTEGER;  // before any guessing
 
 	@Override
 	public int[] sqlTypes() {
 		return new int[] { sqlType };
 	}
 
 	@Override
 	public Class<? extends Enum> returnedClass() {
 		return enumClass;
 	}
 
 	@Override
 	public boolean equals(Object x, Object y) throws HibernateException {
 		return x == y;
 	}
 
 	@Override
 	public int hashCode(Object x) throws HibernateException {
 		return x == null ? 0 : x.hashCode();
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws SQLException {
 		if ( enumValueMapper == null ) {
 			resolveEnumValueMapper( rs,  names[0] );
 		}
 		return enumValueMapper.getValue( rs, names );
 	}
 
 	private void resolveEnumValueMapper(ResultSet rs, String name) {
 		if ( enumValueMapper == null ) {
 			try {
 				resolveEnumValueMapper( rs.getMetaData().getColumnType( rs.findColumn( name ) ) );
 			}
 			catch (Exception e) {
 				// because some drivers do not implement this
 				LOG.debugf(
 						"JDBC driver threw exception calling java.sql.ResultSetMetaData.getColumnType; " +
 								"using fallback determination [%s] : %s",
 						enumClass.getName(),
 						e.getMessage()
 				);
 				// peek at the result value to guess type (this is legacy behavior)
 				try {
 					Object value = rs.getObject( name );
 					if ( Number.class.isInstance( value ) ) {
 						treatAsOrdinal();
 					}
 					else {
 						treatAsNamed();
 					}
 				}
 				catch (SQLException ignore) {
 					treatAsOrdinal();
 				}
 			}
 		}
 	}
 
 	private void resolveEnumValueMapper(int columnType) {
 		// fallback for cases where not enough parameter/parameterization information was passed in
 		if ( isOrdinal( columnType ) ) {
 			treatAsOrdinal();
 		}
 		else {
 			treatAsNamed();
 		}
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
 		if ( enumValueMapper == null ) {
 			resolveEnumValueMapper( st, index );
 		}
 		enumValueMapper.setValue( st, (Enum) value, index );
 	}
 
 	private void resolveEnumValueMapper(PreparedStatement st, int index) {
 		if ( enumValueMapper == null ) {
 			try {
 				resolveEnumValueMapper( st.getParameterMetaData().getParameterType( index ) );
 			}
 			catch (Exception e) {
 				// because some drivers do not implement this
 				LOG.debugf(
 						"JDBC driver threw exception calling java.sql.ParameterMetaData#getParameterType; " +
 								"falling back to ordinal-based enum mapping [%s] : %s",
 						enumClass.getName(),
 						e.getMessage()
 				);
 				treatAsOrdinal();
 			}
 		}
 	}
 
 	@Override
 	public Object deepCopy(Object value) throws HibernateException {
 		return value;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	@Override
 	public Serializable disassemble(Object value) throws HibernateException {
 		return ( Serializable ) value;
 	}
 
 	@Override
 	public Object assemble(Serializable cached, Object owner) throws HibernateException {
 		return cached;
 	}
 
 	@Override
 	public Object replace(Object original, Object target, Object owner) throws HibernateException {
 		return original;
 	}
 
 	@Override
 	public void setParameterValues(Properties parameters) {
 		final ParameterType reader = (ParameterType) parameters.get( PARAMETER_TYPE );
 
 		// IMPL NOTE : be protective about not setting enumValueMapper (i.e. calling treatAsNamed/treatAsOrdinal)
 		// in cases where we do not have enough information.  In such cases we do additional checks
 		// as part of nullSafeGet/nullSafeSet to query against the JDBC metadata to make the determination.
 
 		if ( reader != null ) {
 			enumClass = reader.getReturnedClass().asSubclass( Enum.class );
 
 			final boolean isOrdinal;
 			final javax.persistence.EnumType enumType = getEnumType( reader );
 			if ( enumType == null ) {
 				isOrdinal = true;
 			}
 			else if ( javax.persistence.EnumType.ORDINAL.equals( enumType ) ) {
 				isOrdinal = true;
 			}
 			else if ( javax.persistence.EnumType.STRING.equals( enumType ) ) {
 				isOrdinal = false;
 			}
 			else {
 				throw new AssertionFailure( "Unknown EnumType: " + enumType );
 			}
 
 			if ( isOrdinal ) {
 				treatAsOrdinal();
 			}
 			else {
 				treatAsNamed();
 			}
 			sqlType = enumValueMapper.getSqlType();
 		}
 		else {
 			String enumClassName = (String) parameters.get( ENUM );
 			try {
 				enumClass = ReflectHelper.classForName( enumClassName, this.getClass() ).asSubclass( Enum.class );
 			}
 			catch ( ClassNotFoundException exception ) {
 				throw new HibernateException( "Enum class not found", exception );
 			}
 
 			final Object useNamedSetting = parameters.get( NAMED );
 			if ( useNamedSetting != null ) {
 				final boolean useNamed = ConfigurationHelper.getBoolean( NAMED, parameters );
 				if ( useNamed ) {
 					treatAsNamed();
 				}
 				else {
 					treatAsOrdinal();
 				}
 				sqlType = enumValueMapper.getSqlType();
 			}
 		}
 
 		final String type = (String) parameters.get( TYPE );
 		if ( type != null ) {
 			sqlType = Integer.decode( type );
 		}
 	}
 
 	private void treatAsOrdinal() {
 		if ( enumValueMapper == null || ! OrdinalEnumValueMapper.class.isInstance( enumValueMapper ) ) {
 			enumValueMapper = new OrdinalEnumValueMapper();
+			sqlType = enumValueMapper.getSqlType();
 		}
 	}
 
 	private void treatAsNamed() {
 		if ( enumValueMapper == null || ! NamedEnumValueMapper.class.isInstance( enumValueMapper ) ) {
 			enumValueMapper = new NamedEnumValueMapper();
+			sqlType = enumValueMapper.getSqlType();
 		}
 	}
 
 	private javax.persistence.EnumType getEnumType(ParameterType reader) {
 		javax.persistence.EnumType enumType = null;
 		if ( reader.isPrimaryKey() ) {
 			MapKeyEnumerated enumAnn = getAnnotation( reader.getAnnotationsMethod(), MapKeyEnumerated.class );
 			if ( enumAnn != null ) {
 				enumType = enumAnn.value();
 			}
 		}
 		else {
 			Enumerated enumAnn = getAnnotation( reader.getAnnotationsMethod(), Enumerated.class );
 			if ( enumAnn != null ) {
 				enumType = enumAnn.value();
 			}
 		}
 		return enumType;
 	}
 
 	private <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> anClass) {
 		for ( Annotation annotation : annotations ) {
 			if ( anClass.isInstance( annotation ) ) {
 				return (T) annotation;
 			}
 		}
 		return null;
 	}
 
 	@Override
 	public String objectToSQLString(Object value) {
 		return enumValueMapper.objectToSQLString( (Enum) value );
 	}
 
 	@Override
 	public String toXMLString(Object value) {
 		return enumValueMapper.toXMLString( (Enum) value );
 	}
 
 	@Override
 	public Object fromXMLString(String xmlValue) {
 		return enumValueMapper.fromXMLString( xmlValue );
 	}
 
 	private static interface EnumValueMapper {
 		public int getSqlType();
 		public Enum getValue(ResultSet rs, String[] names) throws SQLException;
 		public void setValue(PreparedStatement st, Enum value, int index) throws SQLException;
 
 		public String objectToSQLString(Enum value);
 		public String toXMLString(Enum value);
 		public Enum fromXMLString(String xml);
 	}
 
 	public abstract class EnumValueMapperSupport implements EnumValueMapper {
 		protected abstract Object extractJdbcValue(Enum value);
 
 		@Override
 		public void setValue(PreparedStatement st, Enum value, int index) throws SQLException {
 			final Object jdbcValue = value == null ? null : extractJdbcValue( value );
 
 			if ( jdbcValue == null ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.trace(String.format("Binding null to parameter: [%s]", index));
 				}
 				st.setNull( index, getSqlType() );
 				return;
 			}
 
 			if ( LOG.isTraceEnabled() ) {
 				LOG.trace(String.format("Binding [%s] to parameter: [%s]", jdbcValue, index));
 			}
 			st.setObject( index, jdbcValue, EnumType.this.sqlType );
 		}
 	}
 
 	private class OrdinalEnumValueMapper extends EnumValueMapperSupport implements EnumValueMapper {
 		private transient Enum[] enumsByOrdinal;
 
 		@Override
 		public int getSqlType() {
 			return Types.INTEGER;
 		}
 
 		@Override
 		public Enum getValue(ResultSet rs, String[] names) throws SQLException {
 			final int ordinal = rs.getInt( names[0] );
 			if ( rs.wasNull() ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.trace(String.format("Returning null as column [%s]", names[0]));
 				}
 				return null;
 			}
 
 			final Enum enumValue = fromOrdinal( ordinal );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.trace(String.format("Returning [%s] as column [%s]", enumValue, names[0]));
 			}
 			return enumValue;
 		}
 
 		private Enum fromOrdinal(int ordinal) {
 			final Enum[] enumsByOrdinal = enumsByOrdinal();
 			if ( ordinal < 0 || ordinal >= enumsByOrdinal.length ) {
 				throw new IllegalArgumentException(
 						String.format(
 								"Unknown ordinal value [%s] for enum class [%s]",
 								ordinal,
 								enumClass.getName()
 						)
 				);
 			}
 			return enumsByOrdinal[ordinal];
 
 		}
 
 		private Enum[] enumsByOrdinal() {
 			if ( enumsByOrdinal == null ) {
 				enumsByOrdinal = enumClass.getEnumConstants();
 				if ( enumsByOrdinal == null ) {
 					throw new HibernateException( "Failed to init enum values" );
 				}
 			}
 			return enumsByOrdinal;
 		}
 
 		@Override
 		public String objectToSQLString(Enum value) {
 			return toXMLString( value );
 		}
 
 		@Override
 		public String toXMLString(Enum value) {
 			return Integer.toString( value.ordinal() );
 		}
 
 		@Override
 		public Enum fromXMLString(String xml) {
 			return fromOrdinal( Integer.parseInt( xml ) );
 		}
 
 		@Override
 		protected Object extractJdbcValue(Enum value) {
 			return value.ordinal();
 		}
 	}
 
 	private class NamedEnumValueMapper extends EnumValueMapperSupport implements EnumValueMapper {
 		@Override
 		public int getSqlType() {
 			return Types.VARCHAR;
 		}
 
 		@Override
 		public Enum getValue(ResultSet rs, String[] names) throws SQLException {
 			final String value = rs.getString( names[0] );
 
 			if ( rs.wasNull() ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.trace(String.format("Returning null as column [%s]", names[0]));
 				}
 				return null;
 			}
 
 			final Enum enumValue = fromName( value );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.trace(String.format("Returning [%s] as column [%s]", enumValue, names[0]));
 			}
 			return enumValue;
 		}
 
 		private Enum fromName(String name) {
 			try {
 				return Enum.valueOf( enumClass, name );
 			}
 			catch ( IllegalArgumentException iae ) {
 				throw new IllegalArgumentException(
 						String.format(
 								"Unknown name value [%s] for enum class [%s]",
 								name,
 								enumClass.getName()
 						)
 				);
 			}
 		}
 
 		@Override
 		public String objectToSQLString(Enum value) {
 			return '\'' + toXMLString( value ) + '\'';
 		}
 
 		@Override
 		public String toXMLString(Enum value) {
 			return value.name();
 		}
 
 		@Override
 		public Enum fromXMLString(String xml) {
 			return fromName( xml );
 		}
 
 		@Override
 		protected Object extractJdbcValue(Enum value) {
 			return value.name();
 		}
 	}
 
 	public boolean isOrdinal() {
 		return isOrdinal( sqlType );
 	}
 
 	private boolean isOrdinal(int paramType) {
 		switch ( paramType ) {
 			case Types.INTEGER:
 			case Types.NUMERIC:
 			case Types.SMALLINT:
 			case Types.TINYINT:
 			case Types.BIGINT:
 			case Types.DECIMAL: //for Oracle Driver
 			case Types.DOUBLE:  //for Oracle Driver
 			case Types.FLOAT:   //for Oracle Driver
 				return true;
 			case Types.CHAR:
 			case Types.LONGVARCHAR:
 			case Types.VARCHAR:
 				return false;
 			default:
 				throw new HibernateException( "Unable to persist an Enum in a column of SQL Type: " + paramType );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/enums/UnspecifiedEnumTypeEntity.java b/hibernate-core/src/test/java/org/hibernate/test/enums/UnspecifiedEnumTypeEntity.java
new file mode 100644
index 0000000000..f69488f125
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/enums/UnspecifiedEnumTypeEntity.java
@@ -0,0 +1,82 @@
+package org.hibernate.test.enums;
+
+import java.io.Serializable;
+
+/**
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
+ */
+public class UnspecifiedEnumTypeEntity implements Serializable {
+	public static enum E1 { X, Y }
+	public static enum E2 { A, B }
+
+	private Long id;
+
+	private E1 enum1;
+
+	private E2 enum2;
+
+	public UnspecifiedEnumTypeEntity() {
+	}
+
+	public UnspecifiedEnumTypeEntity(E1 enum1, E2 enum2) {
+		this.enum1 = enum1;
+		this.enum2 = enum2;
+	}
+
+	public UnspecifiedEnumTypeEntity(E1 enum1, E2 enum2, Long id) {
+		this.enum1 = enum1;
+		this.enum2 = enum2;
+		this.id = id;
+	}
+
+	@Override
+	public boolean equals(Object o) {
+		if ( this == o ) return true;
+		if ( ! ( o instanceof UnspecifiedEnumTypeEntity ) ) return false;
+
+		UnspecifiedEnumTypeEntity that = (UnspecifiedEnumTypeEntity) o;
+
+		if ( enum1 != that.enum1 ) return false;
+		if ( enum2 != that.enum2 ) return false;
+		if ( id != null ? !id.equals( that.id ) : that.id != null ) return false;
+
+		return true;
+	}
+
+	@Override
+	public int hashCode() {
+		int result = id != null ? id.hashCode() : 0;
+		result = 31 * result + ( enum1 != null ? enum1.hashCode() : 0 );
+		result = 31 * result + ( enum2 != null ? enum2.hashCode() : 0 );
+		return result;
+	}
+
+	@Override
+	public String toString() {
+		return "UnspecifiedEnumTypeEntity(id = " + id + ", enum1 = " + enum1 + ", enum2 = " + enum2 + ")";
+	}
+
+	public E1 getEnum1() {
+		return enum1;
+	}
+
+	public void setEnum1(E1 enum1) {
+		this.enum1 = enum1;
+	}
+
+	public E2 getEnum2() {
+		return enum2;
+	}
+
+	public void setEnum2(E2 enum2) {
+		this.enum2 = enum2;
+	}
+
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/enums/UnspecifiedEnumTypeTest.java b/hibernate-core/src/test/java/org/hibernate/test/enums/UnspecifiedEnumTypeTest.java
new file mode 100644
index 0000000000..84a9f1e000
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/enums/UnspecifiedEnumTypeTest.java
@@ -0,0 +1,97 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.enums;
+
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
+
+import org.hibernate.Session;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.cfg.Environment;
+import org.hibernate.dialect.H2Dialect;
+
+import org.hibernate.testing.RequiresDialect;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+/**
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
+ */
+@TestForIssue( jiraKey = "HHH-7780" )
+@RequiresDialect( value = H2Dialect.class )
+public class UnspecifiedEnumTypeTest extends BaseCoreFunctionalTestCase {
+	@Override
+	protected String[] getMappings() {
+		return new String[] { "enums/mappings.hbm.xml" };
+	}
+
+	@Override
+	protected void configure(Configuration configuration) {
+		super.configure( configuration );
+		configuration.setProperty( Environment.HBM2DDL_AUTO, "" );
+	}
+
+	@Before
+	public void prepareTable() {
+		Session session = openSession();
+		dropTable( session );
+		createTable( session );
+		session.close();
+	}
+
+	public void dropTable(Session session) {
+		executeUpdateSafety( session, "drop table ENUM_ENTITY if exists" );
+	}
+
+	private void createTable(Session session) {
+		executeUpdateSafety(
+				session,
+				"create table ENUM_ENTITY (ID bigint not null, enum1 varchar(255), enum2 integer, primary key (ID))"
+		);
+	}
+
+	@After
+	public void dropTable() {
+		dropTable( session );
+	}
+
+	@Test
+	public void testEnumTypeDiscovery() {
+		Session session = openSession();
+		session.beginTransaction();
+		UnspecifiedEnumTypeEntity entity = new UnspecifiedEnumTypeEntity( UnspecifiedEnumTypeEntity.E1.X, UnspecifiedEnumTypeEntity.E2.A );
+		session.persist( entity );
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	private void executeUpdateSafety(Session session, String query) {
+		try {
+			session.createSQLQuery( query ).executeUpdate();
+		}
+		catch ( Exception e ) {
+		}
+	}
+}
diff --git a/hibernate-core/src/test/resources/org/hibernate/test/enums/mappings.hbm.xml b/hibernate-core/src/test/resources/org/hibernate/test/enums/mappings.hbm.xml
new file mode 100644
index 0000000000..3000e3fb8f
--- /dev/null
+++ b/hibernate-core/src/test/resources/org/hibernate/test/enums/mappings.hbm.xml
@@ -0,0 +1,21 @@
+<?xml version="1.0" encoding="WINDOWS-1251"?>
+<!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate Mapping DTD//EN" "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
+<hibernate-mapping>
+    <class name="org.hibernate.test.enums.UnspecifiedEnumTypeEntity" table="ENUM_ENTITY">
+        <id name="id" column="ID" type="long">
+            <generator class="increment" />
+        </id>
+        <property name="enum1">
+            <type name="org.hibernate.type.EnumType">
+                <!--<param name="useNamed">true</param>-->
+                <param name="enumClass">org.hibernate.test.enums.UnspecifiedEnumTypeEntity$E1</param>
+            </type>
+        </property>
+        <property name="enum2">
+            <type name="org.hibernate.type.EnumType">
+                <!--<param name="useNamed">false</param>-->
+                <param name="enumClass">org.hibernate.test.enums.UnspecifiedEnumTypeEntity$E2</param>
+            </type>
+        </property>
+    </class>
+</hibernate-mapping>
