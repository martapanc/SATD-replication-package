diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java
index 45854d25d5..62b7a08bb5 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java
@@ -1,776 +1,776 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg;
 
 import java.util.Map;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.ColumnDefault;
 import org.hibernate.annotations.ColumnTransformer;
 import org.hibernate.annotations.ColumnTransformers;
 import org.hibernate.annotations.Index;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.ObjectNameNormalizer;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.boot.model.relational.Database;
 import org.hibernate.boot.model.source.spi.AttributePath;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.cfg.annotations.Nullability;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 
 import org.jboss.logging.Logger;
 
 /**
  * Wrap state of an EJB3 @Column annotation
  * and build the Hibernate column mapping element
  *
  * @author Emmanuel Bernard
  */
 public class Ejb3Column {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Ejb3Column.class.getName());
 
 	private MetadataBuildingContext context;
 
 	private Column mappingColumn;
 	private boolean insertable = true;
 	private boolean updatable = true;
 	private String explicitTableName;
 	protected Map<String, Join> joins;
 	protected PropertyHolder propertyHolder;
 	private boolean isImplicit;
 	public static final int DEFAULT_COLUMN_LENGTH = 255;
 	public String sqlType;
 	private int length = DEFAULT_COLUMN_LENGTH;
 	private int precision;
 	private int scale;
 	private String logicalColumnName;
 	private String propertyName;
 	private boolean unique;
 	private boolean nullable = true;
 	private String formulaString;
 	private Formula formula;
 	private Table table;
 	private String readExpression;
 	private String writeExpression;
 
 	private String defaultValue;
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public String getLogicalColumnName() {
 		return logicalColumnName;
 	}
 
 	public String getSqlType() {
 		return sqlType;
 	}
 
 	public int getLength() {
 		return length;
 	}
 
 	public int getPrecision() {
 		return precision;
 	}
 
 	public int getScale() {
 		return scale;
 	}
 
 	public boolean isUnique() {
 		return unique;
 	}
 
 	public boolean isFormula() {
 		return StringHelper.isNotEmpty( formulaString );
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public String getFormulaString() {
 		return formulaString;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public String getExplicitTableName() {
 		return explicitTableName;
 	}
 
 	public void setExplicitTableName(String explicitTableName) {
 		if ( "``".equals( explicitTableName ) ) {
 			this.explicitTableName = "";
 		}
 		else {
 			this.explicitTableName = explicitTableName;
 		}
 	}
 
 	public void setFormula(String formula) {
 		this.formulaString = formula;
 	}
 
 	public boolean isImplicit() {
 		return isImplicit;
 	}
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public void setUpdatable(boolean updatable) {
 		this.updatable = updatable;
 	}
 
 	protected MetadataBuildingContext getBuildingContext() {
 		return context;
 	}
 
 	public void setBuildingContext(MetadataBuildingContext context) {
 		this.context = context;
 	}
 
 	public void setImplicit(boolean implicit) {
 		isImplicit = implicit;
 	}
 
 	public void setSqlType(String sqlType) {
 		this.sqlType = sqlType;
 	}
 
 	public void setLength(int length) {
 		this.length = length;
 	}
 
 	public void setPrecision(int precision) {
 		this.precision = precision;
 	}
 
 	public void setScale(int scale) {
 		this.scale = scale;
 	}
 
 	public void setLogicalColumnName(String logicalColumnName) {
 		this.logicalColumnName = logicalColumnName;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public String getPropertyName() {
 		return propertyName;
 	}
 
 	public void setUnique(boolean unique) {
 		this.unique = unique;
 	}
 
 	public boolean isNullable() {
 		return isFormula() ? true : mappingColumn.isNullable();
 	}
 
 	public String getDefaultValue() {
 		return defaultValue;
 	}
 
 	public void setDefaultValue(String defaultValue) {
 		this.defaultValue = defaultValue;
 	}
 
 	public Ejb3Column() {
 	}
 
 	public void bind() {
 		if ( StringHelper.isNotEmpty( formulaString ) ) {
 			LOG.debugf( "Binding formula %s", formulaString );
 			formula = new Formula();
 			formula.setFormula( formulaString );
 		}
 		else {
 			initMappingColumn(
 					logicalColumnName, propertyName, length, precision, scale, nullable, sqlType, unique, true
 			);
 			if ( defaultValue != null ) {
 				mappingColumn.setDefaultValue( defaultValue );
 			}
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Binding column: %s", toString() );
 			}
 		}
 	}
 
 	protected void initMappingColumn(
 			String columnName,
 			String propertyName,
 			int length,
 			int precision,
 			int scale,
 			boolean nullable,
 			String sqlType,
 			boolean unique,
 			boolean applyNamingStrategy) {
 		if ( StringHelper.isNotEmpty( formulaString ) ) {
 			this.formula = new Formula();
 			this.formula.setFormula( formulaString );
 		}
 		else {
 			this.mappingColumn = new Column();
 			redefineColumnName( columnName, propertyName, applyNamingStrategy );
 			this.mappingColumn.setLength( length );
 			if ( precision > 0 ) {  //revelent precision
 				this.mappingColumn.setPrecision( precision );
 				this.mappingColumn.setScale( scale );
 			}
 			this.mappingColumn.setNullable( nullable );
 			this.mappingColumn.setSqlType( sqlType );
 			this.mappingColumn.setUnique( unique );
 
 			if(writeExpression != null && !writeExpression.matches("[^?]*\\?[^?]*")) {
 				throw new AnnotationException(
 						"@WriteExpression must contain exactly one value placeholder ('?') character: property ["
 								+ propertyName + "] and column [" + logicalColumnName + "]"
 				);
 			}
 			if ( readExpression != null) {
 				this.mappingColumn.setCustomRead( readExpression );
 			}
 			if ( writeExpression != null) {
 				this.mappingColumn.setCustomWrite( writeExpression );
 			}
 		}
 	}
 
 	public boolean isNameDeferred() {
 		return mappingColumn == null || StringHelper.isEmpty( mappingColumn.getName() );
 	}
 
 	public void redefineColumnName(String columnName, String propertyName, boolean applyNamingStrategy) {
 		final ObjectNameNormalizer normalizer = context.getObjectNameNormalizer();
 		final Database database = context.getMetadataCollector().getDatabase();
 		final ImplicitNamingStrategy implicitNamingStrategy = context.getBuildingOptions().getImplicitNamingStrategy();
 		final PhysicalNamingStrategy physicalNamingStrategy = context.getBuildingOptions().getPhysicalNamingStrategy();
 
 		if ( applyNamingStrategy ) {
 			if ( StringHelper.isEmpty( columnName ) ) {
 				if ( propertyName != null ) {
 					final AttributePath attributePath = AttributePath.parse( propertyName );
 
 					Identifier implicitName = normalizer.normalizeIdentifierQuoting(
 							implicitNamingStrategy.determineBasicColumnName(
 									new ImplicitBasicColumnNameSource() {
 										@Override
 										public AttributePath getAttributePath() {
 											return attributePath;
 										}
 
 										@Override
 										public boolean isCollectionElement() {
 											// if the propertyHolder is a collection, assume the
 											// @Column refers to the element column
 											return !propertyHolder.isComponent()
 													&& !propertyHolder.isEntity();
 										}
 
 										@Override
 										public MetadataBuildingContext getBuildingContext() {
 											return context;
 										}
 									}
 							)
 					);
 
 					// HHH-6005 magic
 					if ( implicitName.getText().contains( "_collection&&element_" ) ) {
 						implicitName = Identifier.toIdentifier( implicitName.getText().replace( "_collection&&element_", "_" ),
 								implicitName.isQuoted() );
 					}
 
 					final Identifier physicalName = physicalNamingStrategy.toPhysicalColumnName( implicitName, database.getJdbcEnvironment() );
 					mappingColumn.setName( physicalName.render( database.getDialect() ) );
 				}
 				//Do nothing otherwise
 			}
 			else {
 				final Identifier explicitName = database.toIdentifier( columnName );
 				final Identifier physicalName =  physicalNamingStrategy.toPhysicalColumnName( explicitName, database.getJdbcEnvironment() );
 				mappingColumn.setName( physicalName.render( database.getDialect() ) );
 			}
 		}
 		else {
 			if ( StringHelper.isNotEmpty( columnName ) ) {
 				mappingColumn.setName( normalizer.toDatabaseIdentifierText( columnName ) );
 			}
 		}
 	}
 
 	public String getName() {
 		return mappingColumn.getName();
 	}
 
 	public Column getMappingColumn() {
 		return mappingColumn;
 	}
 
 	public boolean isInsertable() {
 		return insertable;
 	}
 
 	public boolean isUpdatable() {
 		return updatable;
 	}
 
 	public void setNullable(boolean nullable) {
 		if ( mappingColumn != null ) {
 			mappingColumn.setNullable( nullable );
 		}
 		else {
 			this.nullable = nullable;
 		}
 	}
 
 	public void setJoins(Map<String, Join> joins) {
 		this.joins = joins;
 	}
 
 	public PropertyHolder getPropertyHolder() {
 		return propertyHolder;
 	}
 
 	public void setPropertyHolder(PropertyHolder propertyHolder) {
 		this.propertyHolder = propertyHolder;
 	}
 
 	protected void setMappingColumn(Column mappingColumn) {
 		this.mappingColumn = mappingColumn;
 	}
 
 	public void linkWithValue(SimpleValue value) {
 		if ( formula != null ) {
 			value.addFormula( formula );
 		}
 		else {
 			getMappingColumn().setValue( value );
-			value.addColumn( getMappingColumn() );
+			value.addColumn( getMappingColumn(), insertable, updatable );
 			value.getTable().addColumn( getMappingColumn() );
 			addColumnBinding( value );
 			table = value.getTable();
 		}
 	}
 
 	protected void addColumnBinding(SimpleValue value) {
 		final String logicalColumnName;
 		if ( StringHelper.isNotEmpty( this.logicalColumnName ) ) {
 			logicalColumnName = this.logicalColumnName;
 		}
 		else {
 			final ObjectNameNormalizer normalizer = context.getObjectNameNormalizer();
 			final Database database = context.getMetadataCollector().getDatabase();
 			final ImplicitNamingStrategy implicitNamingStrategy = context.getBuildingOptions()
 					.getImplicitNamingStrategy();
 
 			final Identifier implicitName = normalizer.normalizeIdentifierQuoting(
 					implicitNamingStrategy.determineBasicColumnName(
 							new ImplicitBasicColumnNameSource() {
 								@Override
 								public AttributePath getAttributePath() {
 									return AttributePath.parse( propertyName );
 								}
 
 								@Override
 								public boolean isCollectionElement() {
 									return false;
 								}
 
 								@Override
 								public MetadataBuildingContext getBuildingContext() {
 									return context;
 								}
 							}
 					)
 			);
 			logicalColumnName = implicitName.render( database.getDialect() );
 		}
 		context.getMetadataCollector().addColumnNameBinding( value.getTable(), logicalColumnName, getMappingColumn() );
 	}
 
 	/**
 	 * Find appropriate table of the column.
 	 * It can come from a secondary table or from the main table of the persistent class
 	 *
 	 * @return appropriate table
 	 * @throws AnnotationException missing secondary table
 	 */
 	public Table getTable() {
 		if ( table != null ){
 			return table;
 		}
 
 		if ( isSecondary() ) {
 			return getJoin().getTable();
 		}
 		else {
 			return propertyHolder.getTable();
 		}
 	}
 
 	public boolean isSecondary() {
 		if ( propertyHolder == null ) {
 			throw new AssertionFailure( "Should not call getTable() on column w/o persistent class defined" );
 		}
 
 		return StringHelper.isNotEmpty( explicitTableName )
 				&& !propertyHolder.getTable().getName().equals( explicitTableName );
 	}
 
 	public Join getJoin() {
 		Join join = joins.get( explicitTableName );
 		if ( join == null ) {
 			// annotation binding seems to use logical and physical naming somewhat inconsistently...
 			final String physicalTableName = getBuildingContext().getMetadataCollector().getPhysicalTableName( explicitTableName );
 			if ( physicalTableName != null ) {
 				join = joins.get( physicalTableName );
 			}
 		}
 
 		if ( join == null ) {
 			throw new AnnotationException(
 					"Cannot find the expected secondary table: no "
 							+ explicitTableName + " available for " + propertyHolder.getClassName()
 			);
 		}
 
 		return join;
 	}
 
 	public void forceNotNull() {
 		if ( mappingColumn == null ) {
 			throw new CannotForceNonNullableException(
 					"Cannot perform #forceNotNull because internal org.hibernate.mapping.Column reference is null: " +
 							"likely a formula"
 			);
 		}
 		mappingColumn.setNullable( false );
 	}
 
 	public static Ejb3Column[] buildColumnFromAnnotation(
 			javax.persistence.Column[] anns,
 			org.hibernate.annotations.Formula formulaAnn,
 			Nullability nullability,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			Map<String, Join> secondaryTables,
 			MetadataBuildingContext context) {
 		return buildColumnFromAnnotation(
 				anns,
 				formulaAnn,
 				nullability,
 				propertyHolder,
 				inferredData,
 				null,
 				secondaryTables,
 				context
 		);
 	}
 	public static Ejb3Column[] buildColumnFromAnnotation(
 			javax.persistence.Column[] anns,
 			org.hibernate.annotations.Formula formulaAnn,
 			Nullability nullability,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			String suffixForDefaultColumnName,
 			Map<String, Join> secondaryTables,
 			MetadataBuildingContext context) {
 		Ejb3Column[] columns;
 		if ( formulaAnn != null ) {
 			Ejb3Column formulaColumn = new Ejb3Column();
 			formulaColumn.setFormula( formulaAnn.value() );
 			formulaColumn.setImplicit( false );
 			formulaColumn.setBuildingContext( context );
 			formulaColumn.setPropertyHolder( propertyHolder );
 			formulaColumn.bind();
 			columns = new Ejb3Column[] { formulaColumn };
 		}
 		else {
 			javax.persistence.Column[] actualCols = anns;
 			javax.persistence.Column[] overriddenCols = propertyHolder.getOverriddenColumn(
 					StringHelper.qualify( propertyHolder.getPath(), inferredData.getPropertyName() )
 			);
 			if ( overriddenCols != null ) {
 				//check for overridden first
 				if ( anns != null && overriddenCols.length != anns.length ) {
 					throw new AnnotationException( "AttributeOverride.column() should override all columns for now" );
 				}
 				actualCols = overriddenCols.length == 0 ? null : overriddenCols;
 				LOG.debugf( "Column(s) overridden for property %s", inferredData.getPropertyName() );
 			}
 			if ( actualCols == null ) {
 				columns = buildImplicitColumn(
 						inferredData,
 						suffixForDefaultColumnName,
 						secondaryTables,
 						propertyHolder,
 						nullability,
 						context
 				);
 			}
 			else {
 				final int length = actualCols.length;
 				columns = new Ejb3Column[length];
 				for (int index = 0; index < length; index++) {
 
 					final ObjectNameNormalizer normalizer = context.getObjectNameNormalizer();
 					final Database database = context.getMetadataCollector().getDatabase();
 					final ImplicitNamingStrategy implicitNamingStrategy = context.getBuildingOptions().getImplicitNamingStrategy();
 					final PhysicalNamingStrategy physicalNamingStrategy = context.getBuildingOptions().getPhysicalNamingStrategy();
 
 					javax.persistence.Column col = actualCols[index];
 
 					final String sqlType;
 					if ( col.columnDefinition().equals( "" ) ) {
 						sqlType = null;
 					}
 					else {
 						sqlType = normalizer.applyGlobalQuoting( col.columnDefinition() );
 					}
 
 					final String tableName;
 					if ( StringHelper.isEmpty( col.table() ) ) {
 						tableName = "";
 					}
 					else {
 						tableName = database.getJdbcEnvironment()
 								.getIdentifierHelper()
 								.toIdentifier( col.table() )
 								.render();
 //						final Identifier logicalName = database.getJdbcEnvironment()
 //								.getIdentifierHelper()
 //								.toIdentifier( col.table() );
 //						final Identifier physicalName = physicalNamingStrategy.toPhysicalTableName( logicalName );
 //						tableName = physicalName.render( database.getDialect() );
 					}
 
 					final String columnName;
 					if ( "".equals( col.name() ) ) {
 						columnName = null;
 					}
 					else {
 						// NOTE : this is the logical column name, not the physical!
 						columnName = database.getJdbcEnvironment()
 								.getIdentifierHelper()
 								.toIdentifier( col.name() )
 								.render();
 					}
 
 					Ejb3Column column = new Ejb3Column();
 
 					if ( length == 1 ) {
 						applyColumnDefault( column, inferredData );
 					}
 
 					column.setImplicit( false );
 					column.setSqlType( sqlType );
 					column.setLength( col.length() );
 					column.setPrecision( col.precision() );
 					column.setScale( col.scale() );
 					if ( StringHelper.isEmpty( columnName ) && ! StringHelper.isEmpty( suffixForDefaultColumnName ) ) {
 						column.setLogicalColumnName( inferredData.getPropertyName() + suffixForDefaultColumnName );
 					}
 					else {
 						column.setLogicalColumnName( columnName );
 					}
 
 					column.setPropertyName(
 							BinderHelper.getRelativePath( propertyHolder, inferredData.getPropertyName() )
 					);
 					column.setNullable(
 						col.nullable()
 					); //TODO force to not null if available? This is a (bad) user choice.
 					column.setUnique( col.unique() );
 					column.setInsertable( col.insertable() );
 					column.setUpdatable( col.updatable() );
 					column.setExplicitTableName( tableName );
 					column.setPropertyHolder( propertyHolder );
 					column.setJoins( secondaryTables );
 					column.setBuildingContext( context );
 					column.extractDataFromPropertyData(inferredData);
 					column.bind();
 					columns[index] = column;
 				}
 			}
 		}
 		return columns;
 	}
 
 	private static void applyColumnDefault(Ejb3Column column, PropertyData inferredData) {
 		final XProperty xProperty = inferredData.getProperty();
 		if ( xProperty != null ) {
 			ColumnDefault columnDefaultAnn = xProperty.getAnnotation( ColumnDefault.class );
 			if ( columnDefaultAnn != null ) {
 				column.setDefaultValue( columnDefaultAnn.value() );
 			}
 		}
 		else {
 			LOG.trace(
 					"Could not perform @ColumnDefault lookup as 'PropertyData' did not give access to XProperty"
 			);
 		}
 	}
 
 	//must only be called afterQuery all setters are defined and beforeQuery bind
 	private void extractDataFromPropertyData(PropertyData inferredData) {
 		if ( inferredData != null ) {
 			XProperty property = inferredData.getProperty();
 			if ( property != null ) {
 				processExpression( property.getAnnotation( ColumnTransformer.class ) );
 				ColumnTransformers annotations = property.getAnnotation( ColumnTransformers.class );
 				if (annotations != null) {
 					for ( ColumnTransformer annotation : annotations.value() ) {
 						processExpression( annotation );
 					}
 				}
 			}
 		}
 	}
 
 	private void processExpression(ColumnTransformer annotation) {
 		String nonNullLogicalColumnName = logicalColumnName != null ? logicalColumnName : ""; //use the default for annotations
 		if ( annotation != null &&
 				( StringHelper.isEmpty( annotation.forColumn() )
 						|| annotation.forColumn().equals( nonNullLogicalColumnName ) ) ) {
 			readExpression = annotation.read();
 			if ( StringHelper.isEmpty( readExpression ) ) {
 				readExpression = null;
 			}
 			writeExpression = annotation.write();
 			if ( StringHelper.isEmpty( writeExpression ) ) {
 				writeExpression = null;
 			}
 		}
 	}
 
 	private static Ejb3Column[] buildImplicitColumn(
 			PropertyData inferredData,
 			String suffixForDefaultColumnName,
 			Map<String, Join> secondaryTables,
 			PropertyHolder propertyHolder,
 			Nullability nullability,
 			MetadataBuildingContext context) {
 		Ejb3Column column = new Ejb3Column();
 		Ejb3Column[] columns = new Ejb3Column[1];
 		columns[0] = column;
 
 		//not following the spec but more clean
 		if ( nullability != Nullability.FORCED_NULL
 				&& inferredData.getClassOrElement().isPrimitive()
 				&& !inferredData.getProperty().isArray() ) {
 			column.setNullable( false );
 		}
 		column.setLength( DEFAULT_COLUMN_LENGTH );
 		final String propertyName = inferredData.getPropertyName();
 		column.setPropertyName(
 				BinderHelper.getRelativePath( propertyHolder, propertyName )
 		);
 		column.setPropertyHolder( propertyHolder );
 		column.setJoins( secondaryTables );
 		column.setBuildingContext( context );
 
 		// property name + suffix is an "explicit" column name
 		if ( !StringHelper.isEmpty( suffixForDefaultColumnName ) ) {
 			column.setLogicalColumnName( propertyName + suffixForDefaultColumnName );
 			column.setImplicit( false );
 		}
 		else {
 			column.setImplicit( true );
 		}
 		applyColumnDefault( column, inferredData );
 		column.extractDataFromPropertyData( inferredData );
 		column.bind();
 		return columns;
 	}
 
 	public static void checkPropertyConsistency(Ejb3Column[] columns, String propertyName) {
 		int nbrOfColumns = columns.length;
 
 		if ( nbrOfColumns > 1 ) {
 			for (int currentIndex = 1; currentIndex < nbrOfColumns; currentIndex++) {
 
 				if (columns[currentIndex].isFormula() || columns[currentIndex - 1].isFormula()) {
 					continue;
 				}
 
 				if ( columns[currentIndex].isInsertable() != columns[currentIndex - 1].isInsertable() ) {
 					throw new AnnotationException(
 							"Mixing insertable and non insertable columns in a property is not allowed: " + propertyName
 					);
 				}
 				if ( columns[currentIndex].isNullable() != columns[currentIndex - 1].isNullable() ) {
 					throw new AnnotationException(
 							"Mixing nullable and non nullable columns in a property is not allowed: " + propertyName
 					);
 				}
 				if ( columns[currentIndex].isUpdatable() != columns[currentIndex - 1].isUpdatable() ) {
 					throw new AnnotationException(
 							"Mixing updatable and non updatable columns in a property is not allowed: " + propertyName
 					);
 				}
 				if ( !columns[currentIndex].getTable().equals( columns[currentIndex - 1].getTable() ) ) {
 					throw new AnnotationException(
 							"Mixing different tables in a property is not allowed: " + propertyName
 					);
 				}
 			}
 		}
 
 	}
 
 	public void addIndex(Index index, boolean inSecondPass) {
 		if ( index == null ) return;
 		String indexName = index.name();
 		addIndex( indexName, inSecondPass );
 	}
 
 	void addIndex(String indexName, boolean inSecondPass) {
 		IndexOrUniqueKeySecondPass secondPass = new IndexOrUniqueKeySecondPass( indexName, this, context, false );
 		if ( inSecondPass ) {
 			secondPass.doSecondPass( context.getMetadataCollector().getEntityBindingMap() );
 		}
 		else {
 			context.getMetadataCollector().addSecondPass( secondPass );
 		}
 	}
 
 	void addUniqueKey(String uniqueKeyName, boolean inSecondPass) {
 		IndexOrUniqueKeySecondPass secondPass = new IndexOrUniqueKeySecondPass( uniqueKeyName, this, context, true );
 		if ( inSecondPass ) {
 			secondPass.doSecondPass( context.getMetadataCollector().getEntityBindingMap() );
 		}
 		else {
 			context.getMetadataCollector().addSecondPass( secondPass );
 		}
 	}
 
 	@Override
 	public String toString() {
 		return "Ejb3Column" + "{table=" + getTable()
 				+ ", mappingColumn=" + mappingColumn.getName()
 				+ ", insertable=" + insertable
 				+ ", updatable=" + updatable
 				+ ", unique=" + unique + '}';
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
index 7351499968..69d5ce3b7a 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
@@ -1,679 +1,684 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Properties;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.FilterConfiguration;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 /**
  * Mapping for a collection. Subclasses specialize to particular collection styles.
  *
  * @author Gavin King
  */
 public abstract class Collection implements Fetchable, Value, Filterable {
 
 	public static final String DEFAULT_ELEMENT_COLUMN_NAME = "elt";
 	public static final String DEFAULT_KEY_COLUMN_NAME = "id";
 
 	private final MetadataImplementor metadata;
 	private PersistentClass owner;
 
 	private KeyValue key;
 	private Value element;
 	private Table collectionTable;
 	private String role;
 	private boolean lazy;
 	private boolean extraLazy;
 	private boolean inverse;
 	private boolean mutable = true;
 	private boolean subselectLoadable;
 	private String cacheConcurrencyStrategy;
 	private String cacheRegionName;
 	private String orderBy;
 	private String where;
 	private String manyToManyWhere;
 	private String manyToManyOrderBy;
 	private String referencedPropertyName;
 	private String mappedByProperty;
 	private boolean sorted;
 	private Comparator comparator;
 	private String comparatorClassName;
 	private boolean orphanDelete;
 	private int batchSize = -1;
 	private FetchMode fetchMode;
 	private boolean embedded = true;
 	private boolean optimisticLocked = true;
 	private Class collectionPersisterClass;
 	private String typeName;
 	private Properties typeParameters;
 	private final java.util.List filters = new ArrayList();
 	private final java.util.List manyToManyFilters = new ArrayList();
 	private final java.util.Set<String> synchronizedTables = new HashSet<String>();
 
 	private String customSQLInsert;
 	private boolean customInsertCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private String customSQLUpdate;
 	private boolean customUpdateCallable;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private String customSQLDelete;
 	private boolean customDeleteCallable;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 	private String customSQLDeleteAll;
 	private boolean customDeleteAllCallable;
 	private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
 
 	private String loaderName;
 
 	protected Collection(MetadataImplementor metadata, PersistentClass owner) {
 		this.metadata = metadata;
 		this.owner = owner;
 	}
 
 	public MetadataImplementor getMetadata() {
 		return metadata;
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return getMetadata().getMetadataBuildingOptions().getServiceRegistry();
 	}
 
 	public boolean isSet() {
 		return false;
 	}
 
 	public KeyValue getKey() {
 		return key;
 	}
 
 	public Value getElement() {
 		return element;
 	}
 
 	public boolean isIndexed() {
 		return false;
 	}
 
 	public Table getCollectionTable() {
 		return collectionTable;
 	}
 
 	public void setCollectionTable(Table table) {
 		this.collectionTable = table;
 	}
 
 	public boolean isSorted() {
 		return sorted;
 	}
 
 	public Comparator getComparator() {
 		if ( comparator == null && comparatorClassName != null ) {
 			try {
 				final ClassLoaderService classLoaderService = getMetadata().getMetadataBuildingOptions()
 						.getServiceRegistry()
 						.getService( ClassLoaderService.class );
 				setComparator( (Comparator) classLoaderService.classForName( comparatorClassName ).newInstance() );
 			}
 			catch (Exception e) {
 				throw new MappingException(
 						"Could not instantiate comparator class [" + comparatorClassName
 								+ "] for collection " + getRole()
 				);
 			}
 		}
 		return comparator;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public abstract CollectionType getDefaultCollectionType() throws MappingException;
 
 	public boolean isPrimitiveArray() {
 		return false;
 	}
 
 	public boolean isArray() {
 		return false;
 	}
 
 	public boolean hasFormula() {
 		return false;
 	}
 
 	public boolean isOneToMany() {
 		return element instanceof OneToMany;
 	}
 
 	public boolean isInverse() {
 		return inverse;
 	}
 
 	public String getOwnerEntityName() {
 		return owner.getEntityName();
 	}
 
 	public String getOrderBy() {
 		return orderBy;
 	}
 
 	public void setComparator(Comparator comparator) {
 		this.comparator = comparator;
 	}
 
 	public void setElement(Value element) {
 		this.element = element;
 	}
 
 	public void setKey(KeyValue key) {
 		this.key = key;
 	}
 
 	public void setOrderBy(String orderBy) {
 		this.orderBy = orderBy;
 	}
 
 	public void setRole(String role) {
 		this.role = role;
 	}
 
 	public void setSorted(boolean sorted) {
 		this.sorted = sorted;
 	}
 
 	public void setInverse(boolean inverse) {
 		this.inverse = inverse;
 	}
 
 	public PersistentClass getOwner() {
 		return owner;
 	}
 
 	/**
 	 * @param owner The owner
 	 *
 	 * @deprecated Inject the owner into constructor.
 	 */
 	@Deprecated
 	public void setOwner(PersistentClass owner) {
 		this.owner = owner;
 	}
 
 	public String getWhere() {
 		return where;
 	}
 
 	public void setWhere(String where) {
 		this.where = where;
 	}
 
 	public String getManyToManyWhere() {
 		return manyToManyWhere;
 	}
 
 	public void setManyToManyWhere(String manyToManyWhere) {
 		this.manyToManyWhere = manyToManyWhere;
 	}
 
 	public String getManyToManyOrdering() {
 		return manyToManyOrderBy;
 	}
 
 	public void setManyToManyOrdering(String orderFragment) {
 		this.manyToManyOrderBy = orderFragment;
 	}
 
 	public boolean isIdentified() {
 		return false;
 	}
 
 	public boolean hasOrphanDelete() {
 		return orphanDelete;
 	}
 
 	public void setOrphanDelete(boolean orphanDelete) {
 		this.orphanDelete = orphanDelete;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public void setBatchSize(int i) {
 		batchSize = i;
 	}
 
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public void setFetchMode(FetchMode fetchMode) {
 		this.fetchMode = fetchMode;
 	}
 
 	public void setCollectionPersisterClass(Class persister) {
 		this.collectionPersisterClass = persister;
 	}
 
 	public Class getCollectionPersisterClass() {
 		return collectionPersisterClass;
 	}
 
 	public void validate(Mapping mapping) throws MappingException {
 		assert getKey() != null : "Collection key not bound : " + getRole();
 		assert getElement() != null : "Collection element not bound : " + getRole();
 
 		if ( getKey().isCascadeDeleteEnabled() && ( !isInverse() || !isOneToMany() ) ) {
 			throw new MappingException(
 					"only inverse one-to-many associations may use on-delete=\"cascade\": "
 							+ getRole()
 			);
 		}
 		if ( !getKey().isValid( mapping ) ) {
 			throw new MappingException(
 					"collection foreign key mapping has wrong number of columns: "
 							+ getRole()
 							+ " type: "
 							+ getKey().getType().getName()
 			);
 		}
 		if ( !getElement().isValid( mapping ) ) {
 			throw new MappingException(
 					"collection element mapping has wrong number of columns: "
 							+ getRole()
 							+ " type: "
 							+ getElement().getType().getName()
 			);
 		}
 
 		checkColumnDuplication();
 	}
 
-	private void checkColumnDuplication(java.util.Set distinctColumns, Iterator columns)
+	private void checkColumnDuplication(java.util.Set distinctColumns, Value value)
 			throws MappingException {
-		while ( columns.hasNext() ) {
-			Selectable s = (Selectable) columns.next();
-			if ( !s.isFormula() ) {
+		final boolean[] insertability = value.getColumnInsertability();
+		final boolean[] updatability = value.getColumnUpdateability();
+		final Iterator<Selectable> iterator = value.getColumnIterator();
+		int i = 0;
+		while ( iterator.hasNext() ) {
+			Selectable s = iterator.next();
+			// exclude formulas and coluns that are not insertable or updatable
+			// since these values can be be repeated (HHH-5393)
+			if ( !s.isFormula() && ( insertability[i] || updatability[i] ) ) {
 				Column col = (Column) s;
 				if ( !distinctColumns.add( col.getName() ) ) {
 					throw new MappingException(
 							"Repeated column in mapping for collection: "
 									+ getRole()
 									+ " column: "
 									+ col.getName()
 					);
 				}
 			}
+			i++;
 		}
 	}
 
 	private void checkColumnDuplication() throws MappingException {
 		HashSet cols = new HashSet();
-		checkColumnDuplication( cols, getKey().getColumnIterator() );
+		checkColumnDuplication( cols, getKey() );
 		if ( isIndexed() ) {
 			checkColumnDuplication(
-					cols, ( (IndexedCollection) this )
-							.getIndex()
-							.getColumnIterator()
+					cols,
+					( (IndexedCollection) this ).getIndex()
 			);
 		}
 		if ( isIdentified() ) {
 			checkColumnDuplication(
-					cols, ( (IdentifierCollection) this )
-							.getIdentifier()
-							.getColumnIterator()
+					cols,
+					( (IdentifierCollection) this ).getIdentifier()
 			);
 		}
 		if ( !isOneToMany() ) {
-			checkColumnDuplication( cols, getElement().getColumnIterator() );
+			checkColumnDuplication( cols, getElement() );
 		}
 	}
 
 	public Iterator<Selectable> getColumnIterator() {
 		return Collections.<Selectable>emptyList().iterator();
 	}
 
 	public int getColumnSpan() {
 		return 0;
 	}
 
 	public Type getType() throws MappingException {
 		return getCollectionType();
 	}
 
 	public CollectionType getCollectionType() {
 		if ( typeName == null ) {
 			return getDefaultCollectionType();
 		}
 		else {
 			return metadata.getTypeResolver()
 					.getTypeFactory()
 					.customCollection( typeName, typeParameters, role, referencedPropertyName );
 		}
 	}
 
 	public boolean isNullable() {
 		return true;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return false;
 	}
 
 	public Table getTable() {
 		return owner.getTable();
 	}
 
 	public void createForeignKey() {
 	}
 
 	public boolean isSimpleValue() {
 		return false;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return true;
 	}
 
 	private void createForeignKeys() throws MappingException {
 		// if ( !isInverse() ) { // for inverse collections, let the "other end" handle it
 		if ( referencedPropertyName == null ) {
 			getElement().createForeignKey();
 			key.createForeignKeyOfEntity( getOwner().getEntityName() );
 		}
 		// }
 	}
 
 	abstract void createPrimaryKey();
 
 	public void createAllKeys() throws MappingException {
 		createForeignKeys();
 		if ( !isInverse() ) {
 			createPrimaryKey();
 		}
 	}
 
 	public String getCacheConcurrencyStrategy() {
 		return cacheConcurrencyStrategy;
 	}
 
 	public void setCacheConcurrencyStrategy(String cacheConcurrencyStrategy) {
 		this.cacheConcurrencyStrategy = cacheConcurrencyStrategy;
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) {
 	}
 
 	public String getCacheRegionName() {
 		return cacheRegionName == null ? role : cacheRegionName;
 	}
 
 	public void setCacheRegionName(String cacheRegionName) {
 		this.cacheRegionName = cacheRegionName;
 	}
 
 
 	public void setCustomSQLInsert(String customSQLInsert, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLInsert = customSQLInsert;
 		this.customInsertCallable = callable;
 		this.insertCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLInsert() {
 		return customSQLInsert;
 	}
 
 	public boolean isCustomInsertCallable() {
 		return customInsertCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLInsertCheckStyle() {
 		return insertCheckStyle;
 	}
 
 	public void setCustomSQLUpdate(String customSQLUpdate, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLUpdate = customSQLUpdate;
 		this.customUpdateCallable = callable;
 		this.updateCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLUpdate() {
 		return customSQLUpdate;
 	}
 
 	public boolean isCustomUpdateCallable() {
 		return customUpdateCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLUpdateCheckStyle() {
 		return updateCheckStyle;
 	}
 
 	public void setCustomSQLDelete(String customSQLDelete, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLDelete = customSQLDelete;
 		this.customDeleteCallable = callable;
 		this.deleteCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLDelete() {
 		return customSQLDelete;
 	}
 
 	public boolean isCustomDeleteCallable() {
 		return customDeleteCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteCheckStyle() {
 		return deleteCheckStyle;
 	}
 
 	public void setCustomSQLDeleteAll(
 			String customSQLDeleteAll,
 			boolean callable,
 			ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLDeleteAll = customSQLDeleteAll;
 		this.customDeleteAllCallable = callable;
 		this.deleteAllCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLDeleteAll() {
 		return customSQLDeleteAll;
 	}
 
 	public boolean isCustomDeleteAllCallable() {
 		return customDeleteAllCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteAllCheckStyle() {
 		return deleteAllCheckStyle;
 	}
 
 	public void addFilter(
 			String name,
 			String condition,
 			boolean autoAliasInjection,
 			java.util.Map<String, String> aliasTableMap,
 			java.util.Map<String, String> aliasEntityMap) {
 		filters.add(
 				new FilterConfiguration(
 						name,
 						condition,
 						autoAliasInjection,
 						aliasTableMap,
 						aliasEntityMap,
 						null
 				)
 		);
 	}
 
 	public java.util.List getFilters() {
 		return filters;
 	}
 
 	public void addManyToManyFilter(
 			String name,
 			String condition,
 			boolean autoAliasInjection,
 			java.util.Map<String, String> aliasTableMap,
 			java.util.Map<String, String> aliasEntityMap) {
 		manyToManyFilters.add(
 				new FilterConfiguration(
 						name,
 						condition,
 						autoAliasInjection,
 						aliasTableMap,
 						aliasEntityMap,
 						null
 				)
 		);
 	}
 
 	public java.util.List getManyToManyFilters() {
 		return manyToManyFilters;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
 	public java.util.Set<String> getSynchronizedTables() {
 		return synchronizedTables;
 	}
 
 	public String getLoaderName() {
 		return loaderName;
 	}
 
 	public void setLoaderName(String name) {
 		this.loaderName = name;
 	}
 
 	public String getReferencedPropertyName() {
 		return referencedPropertyName;
 	}
 
 	public void setReferencedPropertyName(String propertyRef) {
 		this.referencedPropertyName = propertyRef;
 	}
 
 	public boolean isOptimisticLocked() {
 		return optimisticLocked;
 	}
 
 	public void setOptimisticLocked(boolean optimisticLocked) {
 		this.optimisticLocked = optimisticLocked;
 	}
 
 	public boolean isMap() {
 		return false;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 
 	public void setTypeName(String typeName) {
 		this.typeName = typeName;
 	}
 
 	public Properties getTypeParameters() {
 		return typeParameters;
 	}
 
 	public void setTypeParameters(Properties parameterMap) {
 		this.typeParameters = parameterMap;
 	}
 
 	public void setTypeParameters(java.util.Map parameterMap) {
 		if ( parameterMap instanceof Properties ) {
 			this.typeParameters = (Properties) parameterMap;
 		}
 		else {
 			this.typeParameters = new Properties();
 			typeParameters.putAll( parameterMap );
 		}
 	}
 
 	public boolean[] getColumnInsertability() {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
 	public boolean[] getColumnUpdateability() {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 
 	public void setSubselectLoadable(boolean subqueryLoadable) {
 		this.subselectLoadable = subqueryLoadable;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public void setMutable(boolean mutable) {
 		this.mutable = mutable;
 	}
 
 	public boolean isExtraLazy() {
 		return extraLazy;
 	}
 
 	public void setExtraLazy(boolean extraLazy) {
 		this.extraLazy = extraLazy;
 	}
 
 	public boolean hasOrder() {
 		return orderBy != null || manyToManyOrderBy != null;
 	}
 
 	public void setComparatorClassName(String comparatorClassName) {
 		this.comparatorClassName = comparatorClassName;
 	}
 
 	public String getComparatorClassName() {
 		return comparatorClassName;
 	}
 
 	public String getMappedByProperty() {
 		return mappedByProperty;
 	}
 
 	public void setMappedByProperty(String mappedByProperty) {
 		this.mappedByProperty = mappedByProperty;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index 3411cfa703..15577dc186 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,724 +1,745 @@
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
+	private final List<Boolean> insertability = new ArrayList<Boolean>();
+	private final List<Boolean> updatability = new ArrayList<Boolean>();
 
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
-		if ( !columns.contains(column) ) {
+		addColumn( column, true, true );
+	}
+
+	public void addColumn(Column column, boolean isInsertable, boolean isUpdatable) {
+		int index = columns.indexOf( column );
+		if ( index == -1 ) {
 			columns.add(column);
+			insertability.add( isInsertable );
+			updatability.add( isUpdatable );
 		}
-		column.setValue(this);
+		else {
+			if ( insertability.get( index ) != isInsertable ) {
+				throw new IllegalStateException( "Same column is added more than once with different values for isInsertable" );
+			}
+			if ( updatability.get( index ) != isUpdatable ) {
+				throw new IllegalStateException( "Same column is added more than once with different values for isUpdatable" );
+			}
+		}
+		column.setValue( this );
 		column.setTypeIndex( columns.size() - 1 );
 	}
-	
+
 	public void addFormula(Formula formula) {
 		columns.add( formula );
+		insertability.add( false );
+		updatability.add( false );
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
 		// find the standard SqlTypeDescriptor for that JDBC type code.
 		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
 		// find the JavaTypeDescriptor representing the "intermediate database type representation".  Back to the
 		// 		illustration, this should be the type descriptor for Strings
 		final JavaTypeDescriptor intermediateJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( databaseColumnJavaType );
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
-		boolean[] result = new boolean[ getColumnSpan() ];
-		int i = 0;
-		Iterator iter = getColumnIterator();
-		while ( iter.hasNext() ) {
-			Selectable s = (Selectable) iter.next();
-			result[i++] = !s.isFormula();
-		}
-		return result;
+		return extractBooleansFromList( insertability );
 	}
 	
 	public boolean[] getColumnUpdateability() {
-		return getColumnInsertability();
+		return extractBooleansFromList( updatability );
+	}
+
+	private static boolean[] extractBooleansFromList(List<Boolean> list) {
+		final boolean[] array = new boolean[ list.size() ];
+		int i = 0;
+		for ( Boolean value : list ) {
+			array[ i++ ] = value;
+		}
+		return array;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index a3962dfa5c..ce91715333 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,2080 +1,2086 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.persister.collection;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.boot.model.relational.Database;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StructuredCollectionCacheEntry;
 import org.hibernate.cache.spi.entry.StructuredMapCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.List;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Table;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.internal.CompositionSingularSubAttributesHelper;
 import org.hibernate.persister.walking.internal.StandardAnyTypeDefinition;
 import org.hibernate.persister.walking.spi.AnyMappingDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeSource;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.ordering.antlr.ColumnMapper;
 import org.hibernate.sql.ordering.antlr.ColumnReference;
 import org.hibernate.sql.ordering.antlr.FormulaReference;
 import org.hibernate.sql.ordering.antlr.OrderByAliasResolver;
 import org.hibernate.sql.ordering.antlr.OrderByTranslation;
 import org.hibernate.sql.ordering.antlr.SqlValueReference;
 import org.hibernate.type.AnyType;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 /**
  * Base implementation of the <tt>QueryableCollection</tt> interface.
  *
  * @author Gavin King
  * @see BasicCollectionPersister
  * @see OneToManyPersister
  */
 public abstract class AbstractCollectionPersister
 		implements CollectionMetadata, SQLLoadableCollection {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class,
 			AbstractCollectionPersister.class.getName() );
 
 	// TODO: encapsulate the protected instance variables!
 
 	private final String role;
 
 	// SQL statements
 	private final String sqlDeleteString;
 	private final String sqlInsertRowString;
 	private final String sqlUpdateRowString;
 	private final String sqlDeleteRowString;
 	private final String sqlSelectSizeString;
 	private final String sqlSelectRowByIndexString;
 	private final String sqlDetectRowByIndexString;
 	private final String sqlDetectRowByElementString;
 
 	protected final boolean hasWhere;
 	protected final String sqlWhereString;
 	private final String sqlWhereStringTemplate;
 
 	private final boolean hasOrder;
 	private final OrderByTranslation orderByTranslation;
 
 	private final boolean hasManyToManyOrder;
 	private final OrderByTranslation manyToManyOrderByTranslation;
 
 	private final int baseIndex;
 
 	private String mappedByProperty;
 
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
+	protected final boolean[] indexColumnIsGettable;
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
 	protected final boolean isInverse;
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
 	protected final SqlExceptionHelper sqlExceptionHelper;
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
 			Collection collectionBinding,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			PersisterCreationContext creationContext) throws MappingException, CacheException {
 
 		final Database database = creationContext.getMetadata().getDatabase();
 		final JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
 
 		this.factory = creationContext.getSessionFactory();
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		if ( factory.getSessionFactoryOptions().isStructuredCacheEntriesEnabled() ) {
 			cacheEntryStructure = collectionBinding.isMap()
 					? StructuredMapCacheEntry.INSTANCE
 					: StructuredCollectionCacheEntry.INSTANCE;
 		}
 		else {
 			cacheEntryStructure = UnstructuredCacheEntry.INSTANCE;
 		}
 
 		dialect = factory.getDialect();
 		sqlExceptionHelper = factory.getSQLExceptionHelper();
 		collectionType = collectionBinding.getCollectionType();
 		role = collectionBinding.getRole();
 		entityName = collectionBinding.getOwnerEntityName();
 		ownerPersister = factory.getEntityPersister( entityName );
 		queryLoaderName = collectionBinding.getLoaderName();
 		isMutable = collectionBinding.isMutable();
 		mappedByProperty = collectionBinding.getMappedByProperty();
 
 		Table table = collectionBinding.getCollectionTable();
 		fetchMode = collectionBinding.getElement().getFetchMode();
 		elementType = collectionBinding.getElement().getType();
 		// isSet = collectionBinding.isSet();
 		// isSorted = collectionBinding.isSorted();
 		isPrimitiveArray = collectionBinding.isPrimitiveArray();
 		isArray = collectionBinding.isArray();
 		subselectLoadable = collectionBinding.isSubselectLoadable();
 
 		qualifiedTableName = determineTableName( table, jdbcEnvironment );
 
 		int spacesSize = 1 + collectionBinding.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = qualifiedTableName;
 		Iterator iter = collectionBinding.getSynchronizedTables().iterator();
 		for ( int i = 1; i < spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 
 		sqlWhereString = StringHelper.isNotEmpty( collectionBinding.getWhere() ) ? "( " + collectionBinding.getWhere() + ") " : null;
 		hasWhere = sqlWhereString != null;
 		sqlWhereStringTemplate = hasWhere ?
 				Template.renderWhereStringTemplate( sqlWhereString, dialect, factory.getSqlFunctionRegistry() ) :
 				null;
 
 		hasOrphanDelete = collectionBinding.hasOrphanDelete();
 
 		int batch = collectionBinding.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSessionFactoryOptions().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 
 		isVersioned = collectionBinding.isOptimisticLocked();
 
 		// KEY
 
 		keyType = collectionBinding.getKey().getType();
 		iter = collectionBinding.getKey().getColumnIterator();
 		int keySpan = collectionBinding.getKey().getColumnSpan();
 		keyColumnNames = new String[keySpan];
 		keyColumnAliases = new String[keySpan];
 		int k = 0;
 		while ( iter.hasNext() ) {
 			// NativeSQL: collect key column and auto-aliases
 			Column col = ( (Column) iter.next() );
 			keyColumnNames[k] = col.getQuotedName( dialect );
 			keyColumnAliases[k] = col.getAlias( dialect, table );
 			k++;
 		}
 
 		// unquotedKeyColumnNames = StringHelper.unQuote(keyColumnAliases);
 
 		// ELEMENT
 
 		if ( elementType.isEntityType() ) {
 			String entityName = ( (EntityType) elementType ).getAssociatedEntityName();
 			elementPersister = factory.getEntityPersister( entityName );
 			// NativeSQL: collect element column and auto-aliases
 
 		}
 		else {
 			elementPersister = null;
 		}
 
 		int elementSpan = collectionBinding.getElement().getColumnSpan();
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
 		iter = collectionBinding.getElement().getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable selectable = (Selectable) iter.next();
 			elementColumnAliases[j] = selectable.getAlias( dialect, table );
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
 
 		hasIndex = collectionBinding.isIndexed();
 		if ( hasIndex ) {
 			// NativeSQL: collect index column and auto-aliases
 			IndexedCollection indexedCollection = (IndexedCollection) collectionBinding;
 			indexType = indexedCollection.getIndex().getType();
 			int indexSpan = indexedCollection.getIndex().getColumnSpan();
+			boolean[] indexColumnInsertability = indexedCollection.getIndex().getColumnInsertability();
+			boolean[] indexColumnUpdatability = indexedCollection.getIndex().getColumnUpdateability();
 			iter = indexedCollection.getIndex().getColumnIterator();
 			indexColumnNames = new String[indexSpan];
 			indexFormulaTemplates = new String[indexSpan];
 			indexFormulas = new String[indexSpan];
+			indexColumnIsGettable = new boolean[indexSpan];
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
-					indexColumnIsSettable[i] = true;
+					indexColumnIsGettable[i] = true;
+					indexColumnIsSettable[i] = indexColumnInsertability[i] || indexColumnUpdatability[i];
 				}
 				i++;
 			}
 			indexContainsFormula = hasFormula;
 			baseIndex = indexedCollection.isList() ?
 					( (List) indexedCollection ).getBaseIndex() : 0;
 		}
 		else {
 			indexContainsFormula = false;
+			indexColumnIsGettable = null;
 			indexColumnIsSettable = null;
 			indexFormulaTemplates = null;
 			indexFormulas = null;
 			indexType = null;
 			indexColumnNames = null;
 			indexColumnAliases = null;
 			baseIndex = 0;
 		}
 
 		hasIdentifier = collectionBinding.isIdentified();
 		if ( hasIdentifier ) {
 			if ( collectionBinding.isOneToMany() ) {
 				throw new MappingException( "one-to-many collections with identifiers are not supported" );
 			}
 			IdentifierCollection idColl = (IdentifierCollection) collectionBinding;
 			identifierType = idColl.getIdentifier().getType();
 			iter = idColl.getIdentifier().getColumnIterator();
 			Column col = (Column) iter.next();
 			identifierColumnName = col.getQuotedName( dialect );
 			identifierColumnAlias = col.getAlias( dialect );
 			// unquotedIdentifierColumnName = identifierColumnAlias;
 			identifierGenerator = idColl.getIdentifier().createIdentifierGenerator(
 					creationContext.getMetadata().getIdentifierGeneratorFactory(),
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
 
 		if ( collectionBinding.getCustomSQLInsert() == null ) {
 			sqlInsertRowString = generateInsertRowString();
 			insertCallable = false;
 			insertCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlInsertRowString = collectionBinding.getCustomSQLInsert();
 			insertCallable = collectionBinding.isCustomInsertCallable();
 			insertCheckStyle = collectionBinding.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collectionBinding.getCustomSQLInsert(), insertCallable )
 					: collectionBinding.getCustomSQLInsertCheckStyle();
 		}
 
 		if ( collectionBinding.getCustomSQLUpdate() == null ) {
 			sqlUpdateRowString = generateUpdateRowString();
 			updateCallable = false;
 			updateCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlUpdateRowString = collectionBinding.getCustomSQLUpdate();
 			updateCallable = collectionBinding.isCustomUpdateCallable();
 			updateCheckStyle = collectionBinding.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collectionBinding.getCustomSQLUpdate(), insertCallable )
 					: collectionBinding.getCustomSQLUpdateCheckStyle();
 		}
 
 		if ( collectionBinding.getCustomSQLDelete() == null ) {
 			sqlDeleteRowString = generateDeleteRowString();
 			deleteCallable = false;
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteRowString = collectionBinding.getCustomSQLDelete();
 			deleteCallable = collectionBinding.isCustomDeleteCallable();
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		if ( collectionBinding.getCustomSQLDeleteAll() == null ) {
 			sqlDeleteString = generateDeleteString();
 			deleteAllCallable = false;
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteString = collectionBinding.getCustomSQLDeleteAll();
 			deleteAllCallable = collectionBinding.isCustomDeleteAllCallable();
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		sqlSelectSizeString = generateSelectSizeString( collectionBinding.isIndexed() && !collectionBinding.isMap() );
 		sqlDetectRowByIndexString = generateDetectRowByIndexString();
 		sqlDetectRowByElementString = generateDetectRowByElementString();
 		sqlSelectRowByIndexString = generateSelectRowByIndexString();
 
 		logStaticSQL();
 
 		isLazy = collectionBinding.isLazy();
 		isExtraLazy = collectionBinding.isExtraLazy();
 
 		isInverse = collectionBinding.isInverse();
 
 		if ( collectionBinding.isArray() ) {
 			elementClass = ( (org.hibernate.mapping.Array) collectionBinding ).getElementClass();
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
 
 		hasOrder = collectionBinding.getOrderBy() != null;
 		if ( hasOrder ) {
 			orderByTranslation = Template.translateOrderBy(
 					collectionBinding.getOrderBy(),
 					new ColumnMapperImpl(),
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			orderByTranslation = null;
 		}
 
 		// Handle any filters applied to this collectionBinding
 		filterHelper = new FilterHelper( collectionBinding.getFilters(), factory);
 
 		// Handle any filters applied to this collectionBinding for many-to-many
 		manyToManyFilterHelper = new FilterHelper( collectionBinding.getManyToManyFilters(), factory);
 		manyToManyWhereString = StringHelper.isNotEmpty( collectionBinding.getManyToManyWhere() ) ?
 				"( " + collectionBinding.getManyToManyWhere() + ")" :
 				null;
 		manyToManyWhereTemplate = manyToManyWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( manyToManyWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		hasManyToManyOrder = collectionBinding.getManyToManyOrdering() != null;
 		if ( hasManyToManyOrder ) {
 			manyToManyOrderByTranslation = Template.translateOrderBy(
 					collectionBinding.getManyToManyOrdering(),
 					new ColumnMapperImpl(),
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			manyToManyOrderByTranslation = null;
 		}
 
 		initCollectionPropertyMap();
 	}
 
 	protected String determineTableName(Table table, JdbcEnvironment jdbcEnvironment) {
 		if ( table.getSubselect() != null ) {
 			return "( " + table.getSubselect() + " )";
 		}
 
 		return jdbcEnvironment.getQualifiedObjectNameFormatter().format(
 				table.getQualifiedTableName(),
 				jdbcEnvironment.getDialect()
 		);
 	}
 
 	private class ColumnMapperImpl implements ColumnMapper {
 		@Override
 		public SqlValueReference[] map(String reference) {
 			final String[] columnNames;
 			final String[] formulaTemplates;
 
 			// handle the special "$element$" property name...
 			if ( "$element$".equals( reference ) ) {
 				columnNames = elementColumnNames;
 				formulaTemplates = elementFormulaTemplates;
 			}
 			else {
 				columnNames = elementPropertyMapping.toColumns( reference );
 				formulaTemplates = formulaTemplates( reference, columnNames.length );
 			}
 
 			final SqlValueReference[] result = new SqlValueReference[ columnNames.length ];
 			int i = 0;
 			for ( final String columnName : columnNames ) {
 				if ( columnName == null ) {
 					// if the column name is null, it indicates that this index in the property value mapping is
 					// actually represented by a formula.
 //					final int propertyIndex = elementPersister.getEntityMetamodel().getPropertyIndex( reference );
 					final String formulaTemplate = formulaTemplates[i];
 					result[i] = new FormulaReference() {
 						@Override
 						public String getFormulaFragment() {
 							return formulaTemplate;
 						}
 					};
 				}
 				else {
 					result[i] = new ColumnReference() {
 						@Override
 						public String getColumnName() {
 							return columnName;
 						}
 					};
 				}
 				i++;
 			}
 			return result;
 		}
 	}
 
 	private String[] formulaTemplates(String reference, int expectedSize) {
 		try {
 			final int propertyIndex = elementPersister.getEntityMetamodel().getPropertyIndex( reference );
 			return  ( (Queryable) elementPersister ).getSubclassPropertyFormulaTemplateClosure()[propertyIndex];
 		}
 		catch (Exception e) {
 			return new String[expectedSize];
 		}
 	}
 
 	@Override
 	public void postInstantiate() throws MappingException {
 		initializer = queryLoaderName == null ?
 				createCollectionInitializer( LoadQueryInfluencers.NONE ) :
 				new NamedQueryCollectionInitializer( queryLoaderName, this );
 	}
 
 	protected void logStaticSQL() {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Static SQL for collection: %s", getRole() );
 			if ( getSQLInsertRowString() != null ) {
 				LOG.debugf( " Row insert: %s", getSQLInsertRowString() );
 			}
 			if ( getSQLUpdateRowString() != null ) {
 				LOG.debugf( " Row update: %s", getSQLUpdateRowString() );
 			}
 			if ( getSQLDeleteRowString() != null ) {
 				LOG.debugf( " Row delete: %s", getSQLDeleteRowString() );
 			}
 			if ( getSQLDeleteString() != null ) {
 				LOG.debugf( " One-shot delete: %s", getSQLDeleteString() );
 			}
 		}
 	}
 
 	@Override
 	public void initialize(Serializable key, SharedSessionContractImplementor session) throws HibernateException {
 		getAppropriateInitializer( key, session ).initialize( key, session );
 	}
 
 	protected CollectionInitializer getAppropriateInitializer(Serializable key, SharedSessionContractImplementor session) {
 		if ( queryLoaderName != null ) {
 			// if there is a user-specified loader, return that
 			// TODO: filters!?
 			return initializer;
 		}
 		CollectionInitializer subselectInitializer = getSubselectInitializer( key, session );
 		if ( subselectInitializer != null ) {
 			return subselectInitializer;
 		}
 		else if ( session.getLoadQueryInfluencers().getEnabledFilters().isEmpty() ) {
 			return initializer;
 		}
 		else {
 			return createCollectionInitializer( session.getLoadQueryInfluencers() );
 		}
 	}
 
 	private CollectionInitializer getSubselectInitializer(Serializable key, SharedSessionContractImplementor session) {
 
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
 
 	protected abstract CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SharedSessionContractImplementor session);
 
 	protected abstract CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
 			throws MappingException;
 
 	@Override
 	public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	@Override
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	@Override
 	public CollectionType getCollectionType() {
 		return collectionType;
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	@Override
 	public String getSQLOrderByString(String alias) {
 		return hasOrdering()
 				? orderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
 				: "";
 	}
 
 	@Override
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
 				? manyToManyOrderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
 				: "";
 	}
 
 	@Override
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	@Override
 	public boolean hasOrdering() {
 		return hasOrder;
 	}
 
 	@Override
 	public boolean hasManyToManyOrdering() {
 		return isManyToMany() && hasManyToManyOrder;
 	}
 
 	@Override
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
 
 	@Override
 	public Type getKeyType() {
 		return keyType;
 	}
 
 	@Override
 	public Type getIndexType() {
 		return indexType;
 	}
 
 	@Override
 	public Type getElementType() {
 		return elementType;
 	}
 
 	/**
 	 * Return the element class of an array, or null otherwise.  needed by arrays
 	 */
 	@Override
 	public Class getElementClass() {
 		return elementClass;
 	}
 
 	@Override
 	public Object readElement(ResultSet rs, Object owner, String[] aliases, SharedSessionContractImplementor session)
 			throws HibernateException, SQLException {
 		return getElementType().nullSafeGet( rs, aliases, session, owner );
 	}
 
 	@Override
 	public Object readIndex(ResultSet rs, String[] aliases, SharedSessionContractImplementor session)
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
 
 	@Override
 	public Object readIdentifier(ResultSet rs, String alias, SharedSessionContractImplementor session)
 			throws HibernateException, SQLException {
 		Object id = getIdentifierType().nullSafeGet( rs, alias, session, null );
 		if ( id == null ) {
 			throw new HibernateException( "null identifier column for collection: " + role );
 		}
 		return id;
 	}
 
 	@Override
 	public Object readKey(ResultSet rs, String[] aliases, SharedSessionContractImplementor session)
 			throws HibernateException, SQLException {
 		return getKeyType().nullSafeGet( rs, aliases, session, null );
 	}
 
 	/**
 	 * Write the key to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeKey(PreparedStatement st, Serializable key, int i, SharedSessionContractImplementor session)
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
 	protected int writeElement(PreparedStatement st, Object elt, int i, SharedSessionContractImplementor session)
 			throws HibernateException, SQLException {
 		getElementType().nullSafeSet( st, elt, i, elementColumnIsSettable, session );
 		return i + ArrayHelper.countTrue( elementColumnIsSettable );
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndex(PreparedStatement st, Object index, int i, SharedSessionContractImplementor session)
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
 	protected int writeElementToWhere(PreparedStatement st, Object elt, int i, SharedSessionContractImplementor session)
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
 	protected int writeIndexToWhere(PreparedStatement st, Object index, int i, SharedSessionContractImplementor session)
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
 	public int writeIdentifier(PreparedStatement st, Object id, int i, SharedSessionContractImplementor session)
 			throws HibernateException, SQLException {
 
 		getIdentifierType().nullSafeSet( st, id, i, session );
 		return i + 1;
 	}
 
 	@Override
 	public boolean isPrimitiveArray() {
 		return isPrimitiveArray;
 	}
 
 	@Override
 	public boolean isArray() {
 		return isArray;
 	}
 
 	@Override
 	public String[] getKeyColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( keyColumnAliases );
 	}
 
 	@Override
 	public String[] getElementColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( elementColumnAliases );
 	}
 
 	@Override
 	public String[] getIndexColumnAliases(String suffix) {
 		if ( hasIndex ) {
 			return new Alias( suffix ).toAliasStrings( indexColumnAliases );
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
 	public String getIdentifierColumnAlias(String suffix) {
 		if ( hasIdentifier ) {
 			return new Alias( suffix ).toAliasString( identifierColumnAlias );
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
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
 	@Override
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
 				.addWhereToken( sqlWhereString )
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
 				.addWhereToken( sqlWhereString )
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
 				.addWhereToken( sqlWhereString )
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
 				.addWhereToken( sqlWhereString )
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
-			for ( int i = 0; i < indexColumnIsSettable.length; i++ ) {
-				if ( indexColumnIsSettable[i] ) {
+			for ( int i = 0; i < indexColumnIsGettable.length; i++ ) {
+				if ( indexColumnIsGettable[i] ) {
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
 
 	@Override
 	public String[] getIndexColumnNames() {
 		return indexColumnNames;
 	}
 
 	@Override
 	public String[] getIndexFormulas() {
 		return indexFormulas;
 	}
 
 	@Override
 	public String[] getIndexColumnNames(String alias) {
 		return qualify( alias, indexColumnNames, indexFormulaTemplates );
 	}
 
 	@Override
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
 
 	@Override
 	public String[] getElementColumnNames() {
 		return elementColumnNames; // TODO: something with formulas...
 	}
 
 	@Override
 	public String[] getKeyColumnNames() {
 		return keyColumnNames;
 	}
 
 	@Override
 	public boolean hasIndex() {
 		return hasIndex;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	@Override
 	public boolean isInverse() {
 		return isInverse;
 	}
 
 	@Override
 	public String getTableName() {
 		return qualifiedTableName;
 	}
 
 	private BasicBatchKey removeBatchKey;
 
 	@Override
 	public void remove(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
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
 					st = session
 							.getJdbcCoordinator()
 							.getBatch( removeBatchKey )
 							.getBatchStatement( sql, callable );
 				}
 				else {
 					st = session
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql, callable );
 				}
 
 				try {
 					offset += expectation.prepare( st );
 
 					writeKey( st, id, offset, session );
 					if ( useBatch ) {
 						session
 								.getJdbcCoordinator()
 								.getBatch( removeBatchKey )
 								.addToBatch();
 					}
 					else {
 						expectation.verifyOutcome( session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 					}
 				}
 				catch ( SQLException sqle ) {
 					if ( useBatch ) {
 						session.getJdbcCoordinator().abortBatch();
 					}
 					throw sqle;
 				}
 				finally {
 					if ( !useBatch ) {
 						session.getJdbcCoordinator().getResourceRegistry().release( st );
 						session.getJdbcCoordinator().afterStatementExecution();
 					}
 				}
 
 				LOG.debug( "Done deleting collection" );
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
 
 	protected BasicBatchKey recreateBatchKey;
 
 	@Override
 	public void recreate(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session)
 			throws HibernateException {
 
 		if ( isInverse ) {
 			return;
 		}
 
 		if ( !isRowInsertEnabled() ) {
 			return;
 		}
 
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Inserting collection: %s",
 					MessageHelper.collectionInfoString( this, collection, id, session )
 			);
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
 							st = session
 									.getJdbcCoordinator()
 									.getBatch( recreateBatchKey )
 									.getBatchStatement( sql, callable );
 						}
 						else {
 							st = session
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
 								session
 										.getJdbcCoordinator()
 										.getBatch( recreateBatchKey )
 										.addToBatch();
 							}
 							else {
 								expectation.verifyOutcome( session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 							}
 
 							collection.afterRowInsert( this, entry, i );
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
 								session.getJdbcCoordinator().abortBatch();
 							}
 							throw sqle;
 						}
 						finally {
 							if ( !useBatch ) {
 								session.getJdbcCoordinator().getResourceRegistry().release( st );
 								session.getJdbcCoordinator().afterStatementExecution();
 							}
 						}
 
 					}
 					i++;
 				}
 
 				LOG.debugf( "Done inserting collection: %s rows inserted", count );
 
 			}
 			else {
 				LOG.debug( "Collection was empty" );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw sqlExceptionHelper.convert(
 					sqle,
 					"could not insert collection: " +
 							MessageHelper.collectionInfoString( this, collection, id, session ),
 					getSQLInsertRowString()
 			);
 		}
 	}
 
 	protected boolean isRowDeleteEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	@Override
 	public void deleteRows(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session)
 			throws HibernateException {
 
 		if ( isInverse ) {
 			return;
 		}
 
 		if ( !isRowDeleteEnabled() ) {
 			return;
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Deleting rows of collection: %s",
 					MessageHelper.collectionInfoString( this, collection, id, session )
 			);
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
 						st = session
 								.getJdbcCoordinator()
 								.getBatch( deleteBatchKey )
 								.getBatchStatement( sql, callable );
 					}
 					else {
 						st = session
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
 							session
 									.getJdbcCoordinator()
 									.getBatch( deleteBatchKey )
 									.addToBatch();
 						}
 						else {
 							expectation.verifyOutcome( session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 						}
 						count++;
 					}
 					catch ( SQLException sqle ) {
 						if ( useBatch ) {
 							session.getJdbcCoordinator().abortBatch();
 						}
 						throw sqle;
 					}
 					finally {
 						if ( !useBatch ) {
 							session.getJdbcCoordinator().getResourceRegistry().release( st );
 							session.getJdbcCoordinator().afterStatementExecution();
 						}
 					}
 
 					LOG.debugf( "Done deleting collection rows: %s deleted", count );
 				}
 			}
 			else {
 				LOG.debug( "No rows to delete" );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw sqlExceptionHelper.convert(
 					sqle,
 					"could not delete collection rows: " +
 							MessageHelper.collectionInfoString( this, collection, id, session ),
 					getSQLDeleteRowString()
 			);
 		}
 	}
 
 	protected boolean isRowInsertEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey insertBatchKey;
 
 	@Override
 	public void insertRows(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session)
 			throws HibernateException {
 
 		if ( isInverse ) {
 			return;
 		}
 
 		if ( !isRowInsertEnabled() ) {
 			return;
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Inserting rows of collection: %s",
 					MessageHelper.collectionInfoString( this, collection, id, session )
 			);
 		}
 
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
 							st = session
 									.getJdbcCoordinator()
 									.getBatch( insertBatchKey )
 									.getBatchStatement( sql, callable );
 						}
 					}
 					else {
 						st = session
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
 							session.getJdbcCoordinator().getBatch( insertBatchKey ).addToBatch();
 						}
 						else {
 							expectation.verifyOutcome( session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 						}
 						collection.afterRowInsert( this, entry, i );
 						count++;
 					}
 					catch ( SQLException sqle ) {
 						if ( useBatch ) {
 							session.getJdbcCoordinator().abortBatch();
 						}
 						throw sqle;
 					}
 					finally {
 						if ( !useBatch ) {
 							session.getJdbcCoordinator().getResourceRegistry().release( st );
 							session.getJdbcCoordinator().afterStatementExecution();
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
 							MessageHelper.collectionInfoString( this, collection, id, session ),
 					getSQLInsertRowString()
 			);
 		}
 	}
 
 	@Override
 	public String getRole() {
 		return role;
 	}
 
 	public String getOwnerEntityName() {
 		return entityName;
 	}
 
 	@Override
 	public EntityPersister getOwnerEntityPersister() {
 		return ownerPersister;
 	}
 
 	@Override
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 
 	@Override
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 	@Override
 	public boolean hasOrphanDelete() {
 		return hasOrphanDelete;
 	}
 
 	@Override
 	public Type toType(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return indexType;
 		}
 		return elementPropertyMapping.toType( propertyName );
 	}
 
 	@Override
 	public abstract boolean isManyToMany();
 
 	@Override
 	public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 		StringBuilder buffer = new StringBuilder();
 		manyToManyFilterHelper.render( buffer, elementPersister.getFilterAliasGenerator(alias), enabledFilters );
 
 		if ( manyToManyWhereString != null ) {
 			buffer.append( " and " )
 					.append( StringHelper.replace( manyToManyWhereTemplate, Template.TEMPLATE, alias ) );
 		}
 
 		return buffer.toString();
 	}
 
 	@Override
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return qualify( alias, indexColumnNames, indexFormulaTemplates );
 		}
 		return elementPropertyMapping.toColumns( alias, propertyName );
 	}
 
 	private String[] indexFragments;
 
 	@Override
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
 
 	@Override
 	public Type getType() {
 		return elementPropertyMapping.getType(); // ==elementType ??
 	}
 
 	@Override
 	public String getName() {
 		return getRole();
 	}
 
 	@Override
 	public EntityPersister getElementPersister() {
 		if ( elementPersister == null ) {
 			throw new AssertionFailure( "not an association" );
 		}
 		return elementPersister;
 	}
 
 	@Override
 	public boolean isCollection() {
 		return true;
 	}
 
 	@Override
 	public Serializable[] getCollectionSpaces() {
 		return spaces;
 	}
 
 	protected abstract String generateDeleteString();
 
 	protected abstract String generateDeleteRowString();
 
 	protected abstract String generateUpdateRowString();
 
 	protected abstract String generateInsertRowString();
 
 	@Override
 	public void updateRows(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && collection.isRowUpdatePossible() ) {
 
 			LOG.debugf( "Updating rows of collection: %s#%s", role, id );
 
 			// update all the modified entries
 			int count = doUpdateRows( id, collection, session );
 
 			LOG.debugf( "Done updating rows: %s updated", count );
 		}
 	}
 
 	protected abstract int doUpdateRows(Serializable key, PersistentCollection collection, SharedSessionContractImplementor session)
 			throws HibernateException;
 
 	@Override
 	public void processQueuedOps(PersistentCollection collection, Serializable key, SharedSessionContractImplementor session)
 			throws HibernateException {
 		if ( collection.hasQueuedOperations() ) {
 			doProcessQueuedOps( collection, key, session );
 		}
 	}
 
 	/**
 	 * Process queued operations within the PersistentCollection.
 	 *
 	 * @param collection The collection
 	 * @param key The collection key
 	 * @param nextIndex The next index to write
 	 * @param session The session
 	 * @throws HibernateException
 	 *
 	 * @deprecated Use {@link #doProcessQueuedOps(org.hibernate.collection.spi.PersistentCollection, java.io.Serializable, org.hibernate.engine.spi.SharedSessionContractImplementor)}
 	 */
 	@Deprecated
 	protected void doProcessQueuedOps(PersistentCollection collection, Serializable key,
 			int nextIndex, SharedSessionContractImplementor session)
 			throws HibernateException {
 		doProcessQueuedOps( collection, key, session );
 	}
 
 	protected abstract void doProcessQueuedOps(PersistentCollection collection, Serializable key, SharedSessionContractImplementor session)
 			throws HibernateException;
 
 	@Override
 	public CollectionMetadata getCollectionMetadata() {
 		return this;
 	}
 
 	@Override
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected String filterFragment(String alias) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
 	protected String filterFragment(String alias, Set<String> treatAsDeclarations) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
 	@Override
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	@Override
 	public String filterFragment(
 			String alias,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias, treatAsDeclarations ) ).toString();
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
 		return oneToManyFilterFragment( alias );
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
 
 	@Override
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + role + ')';
 	}
 
 	@Override
 	public boolean isVersioned() {
 		return isVersioned && getOwnerEntityPersister().isVersioned();
 	}
 
 	// TODO: deprecate???
 	protected SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	// TODO: needed???
 	protected SqlExceptionHelper getSQLExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
 	}
 
 	@Override
 	public boolean isAffectedByEnabledFilters(SharedSessionContractImplementor session) {
 		return filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() ) ||
 				( isManyToMany() && manyToManyFilterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() ) );
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return isMutable;
 	}
 
 	@Override
 	public String[] getCollectionPropertyColumnAliases(String propertyName, String suffix) {
 		String[] rawAliases = (String[]) collectionPropertyColumnAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String[] result = new String[rawAliases.length];
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
 
 	@Override
 	public int getSize(Serializable key, SharedSessionContractImplementor session) {
 		try {
 			PreparedStatement st = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectSizeString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next() ? rs.getInt( 1 ) - baseIndex : 0;
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, st );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( st );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve collection size: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 			);
 		}
 	}
 
 	@Override
 	public boolean indexExists(Serializable key, Object index, SharedSessionContractImplementor session) {
 		return exists( key, incrementIndexByBase( index ), getIndexType(), sqlDetectRowByIndexString, session );
 	}
 
 	@Override
 	public boolean elementExists(Serializable key, Object element, SharedSessionContractImplementor session) {
 		return exists( key, element, getElementType(), sqlDetectRowByElementString, session );
 	}
 
 	private boolean exists(Serializable key, Object indexOrElement, Type indexOrElementType, String sql, SharedSessionContractImplementor session) {
 		try {
 			PreparedStatement st = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				indexOrElementType.nullSafeSet( st, indexOrElement, keyColumnNames.length + 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next();
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, st );
 				}
 			}
 			catch ( TransientObjectException e ) {
 				return false;
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( st );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not check row existence: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 			);
 		}
 	}
 
 	@Override
 	public Object getElementByIndex(Serializable key, Object index, SharedSessionContractImplementor session, Object owner) {
 		try {
 			PreparedStatement st = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectRowByIndexString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				getIndexType().nullSafeSet( st, incrementIndexByBase( index ), keyColumnNames.length + 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					if ( rs.next() ) {
 						return getElementType().nullSafeGet( rs, elementColumnAliases, session, owner );
 					}
 					else {
 						return null;
 					}
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, st );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( st );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not read row: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 			);
 		}
 	}
 
 	@Override
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
 
 	@Override
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	@Override
 	public String getMappedByProperty() {
 		return mappedByProperty;
 	}
 
 	private class StandardOrderByAliasResolver implements OrderByAliasResolver {
 		private final String rootAlias;
 
 		private StandardOrderByAliasResolver(String rootAlias) {
 			this.rootAlias = rootAlias;
 		}
 
 		@Override
 		public String resolveTableAlias(String columnReference) {
 			if ( elementPersister == null ) {
 				// we have collection of non-entity elements...
 				return rootAlias;
 			}
 			else {
 				return ( (Loadable) elementPersister ).getTableAliasForColumn( columnReference, rootAlias );
 			}
 		}
 	}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/map/LocalizedString.java b/hibernate-core/src/test/java/org/hibernate/test/collection/map/LocalizedString.java
new file mode 100644
index 0000000000..6f8d1c3f9d
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/map/LocalizedString.java
@@ -0,0 +1,34 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.collection.map;
+
+import javax.persistence.Embeddable;
+
+@Embeddable
+public class LocalizedString {
+	private String language;
+	private String text;
+
+	public LocalizedString() {
+	}
+
+	public String getLanguage() {
+		return language;
+	}
+
+	public void setLanguage(String language) {
+		this.language = language;
+	}
+
+	public String getText() {
+		return text;
+	}
+
+	public void setText(String text) {
+		this.text = text;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/map/MultilingualString.java b/hibernate-core/src/test/java/org/hibernate/test/collection/map/MultilingualString.java
new file mode 100644
index 0000000000..3155d6cf51
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/map/MultilingualString.java
@@ -0,0 +1,45 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.collection.map;
+
+import java.util.HashMap;
+import java.util.Map;
+import javax.persistence.CollectionTable;
+import javax.persistence.ElementCollection;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.MapKeyColumn;
+import javax.persistence.Table;
+
+@Entity
+@Table(name = "multilingual")
+public class MultilingualString {
+	@Id
+	@GeneratedValue
+	private long id;
+
+	@ElementCollection
+	@MapKeyColumn(name = "language", insertable = false, updatable = false)
+	@CollectionTable(name = "multilingual_string_map", joinColumns = @JoinColumn(name = "string_id"))
+	private Map<String, LocalizedString> map = new HashMap<String, LocalizedString>();
+
+	public long getId() {
+		return id;
+	}
+	public void setId(long id) {
+		this.id = id;
+	}
+
+	public Map<String, LocalizedString> getMap() {
+		return map;
+	}
+	public void setMap(Map<String, LocalizedString> map) {
+		this.map = map;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/map/PersistentMapTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/map/PersistentMapTest.java
index 526559a3e5..be0966f829 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/collection/map/PersistentMapTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/map/PersistentMapTest.java
@@ -1,221 +1,260 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.collection.map;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Map;
 
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.MapKeyColumn;
 import javax.persistence.OneToMany;
 import javax.persistence.Table;
 
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.collection.internal.PersistentMap;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
 /**
  * Test various situations using a {@link PersistentMap}.
  *
  * @author Steve Ebersole
  * @author Brett Meyer
+ * @author Gail Badner
  */
 public class PersistentMapTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "collection/map/Mappings.hbm.xml" };
 	}
 	
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
-		return new Class<?>[] { User.class, UserData.class };
+		return new Class<?>[] {
+				User.class,
+				UserData.class,
+				MultilingualString.class
+		};
 	}
 
 	@Test
 	@SuppressWarnings({ "unchecked" })
 	public void testWriteMethodDirtying() {
 		Parent parent = new Parent( "p1" );
 		Child child = new Child( "c1" );
 		parent.getChildren().put( child.getName(), child );
 		child.setParent( parent );
 		Child otherChild = new Child( "c2" );
 
 		Session session = openSession();
 		session.beginTransaction();
 		session.save( parent );
 		session.flush();
 		// at this point, the map on parent has now been replaced with a PersistentMap...
 		PersistentMap children = ( PersistentMap ) parent.getChildren();
 
 		Object old = children.put( child.getName(), child );
 		assertTrue( old == child );
 		assertFalse( children.isDirty() );
 
 		old = children.remove( otherChild.getName() );
 		assertNull( old );
 		assertFalse( children.isDirty() );
 
 		HashMap otherMap = new HashMap();
 		otherMap.put( child.getName(), child );
 		children.putAll( otherMap );
 		assertFalse( children.isDirty() );
 
 		otherMap = new HashMap();
 		otherMap.put( otherChild.getName(), otherChild );
 		children.putAll( otherMap );
 		assertTrue( children.isDirty() );
 
 		children.clearDirty();
 		session.delete( child );
 		children.clear();
 		assertTrue( children.isDirty() );
 		session.flush();
 
 		children.clear();
 		assertFalse( children.isDirty() );
 
 		session.delete( parent );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testPutAgainstUninitializedMap() {
 		// prepare map owner...
 		Session session = openSession();
 		session.beginTransaction();
 		Parent parent = new Parent( "p1" );
 		session.save( parent );
 		session.getTransaction().commit();
 		session.close();
 
 		// Now, reload the parent and test adding children
 		session = openSession();
 		session.beginTransaction();
 		parent = ( Parent ) session.get( Parent.class, parent.getName() );
 		parent.addChild( "c1" );
 		parent.addChild( "c2" );
 		session.getTransaction().commit();
 		session.close();
 
 		assertEquals( 2, parent.getChildren().size() );
 
 		session = openSession();
 		session.beginTransaction();
 		session.delete( parent );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
     public void testRemoveAgainstUninitializedMap() {
         Parent parent = new Parent( "p1" );
         Child child = new Child( "c1" );
         parent.addChild( child );
 
         Session session = openSession();
         session.beginTransaction();
         session.save( parent );
         session.getTransaction().commit();
         session.close();
 
         // Now reload the parent and test removing the child
         session = openSession();
         session.beginTransaction();
         parent = ( Parent ) session.get( Parent.class, parent.getName() );
         Child child2 = ( Child ) parent.getChildren().remove( child.getName() );
 		child2.setParent( null );
 		assertNotNull( child2 );
 		assertTrue( parent.getChildren().isEmpty() );
         session.getTransaction().commit();
         session.close();
 
 		// Load the parent once again and make sure child is still gone
 		//		then cleanup
         session = openSession();
         session.beginTransaction();
 		parent = ( Parent ) session.get( Parent.class, parent.getName() );
 		assertTrue( parent.getChildren().isEmpty() );
 		session.delete( child2 );
 		session.delete( parent );
         session.getTransaction().commit();
         session.close();
     }
 	
 	@Test
 	@TestForIssue(jiraKey = "HHH-5732")
 	public void testClearMap() {
 		Session s = openSession();
 		s.beginTransaction();
 		
 		User user = new User();
 		UserData userData = new UserData();
 		userData.user = user;
 		user.userDatas.put( "foo", userData );
 		s.persist( user );
 		
 		s.getTransaction().commit();
 		s.clear();
 		
 		s.beginTransaction();
 		
 		user = s.get( User.class, 1 );
 	    user.userDatas.clear();
 	    s.update( user );
 		Query q = s.createQuery( "DELETE FROM " + UserData.class.getName() + " d WHERE d.user = :user" );
 		q.setParameter( "user", user );
 		q.executeUpdate();
 		
 		s.getTransaction().commit();
 
 		s.getTransaction().begin();
 
 		assertEquals( s.get( User.class, user.id ).userDatas.size(), 0 );
 		assertEquals( s.createQuery( "FROM " + UserData.class.getName() ).list().size(), 0 );
 		s.createQuery( "delete " + User.class.getName() ).executeUpdate();
 
 		s.getTransaction().commit();
 		s.close();
 	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-5393")
+	public void testMapKeyColumnInEmbeddableElement() {
+		Session s = openSession();
+		s.getTransaction().begin();
+		MultilingualString m = new MultilingualString();
+		LocalizedString localizedString = new LocalizedString();
+		localizedString.setLanguage( "English" );
+		localizedString.setText( "name" );
+		m.getMap().put( localizedString.getLanguage(), localizedString );
+		localizedString = new LocalizedString();
+		localizedString.setLanguage( "English Pig Latin" );
+		localizedString.setText( "amenay" );
+		m.getMap().put( localizedString.getLanguage(), localizedString );
+		s.persist( m );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		m = s.get( MultilingualString.class, m.getId());
+		assertEquals( 2, m.getMap().size() );
+		localizedString = m.getMap().get( "English" );
+		assertEquals( "English", localizedString.getLanguage() );
+		assertEquals( "name", localizedString.getText() );
+		localizedString = m.getMap().get( "English Pig Latin" );
+		assertEquals( "English Pig Latin", localizedString.getLanguage() );
+		assertEquals( "amenay", localizedString.getText() );
+		s.delete( m );
+		s.getTransaction().commit();
+		s.close();
+	}
 	
 	@Entity
 	@Table(name = "MyUser")
 	private static class User implements Serializable {
 		@Id @GeneratedValue
 		private Integer id;
 		
 		@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
 		@MapKeyColumn(name = "name", nullable = true)
 		private Map<String, UserData> userDatas = new HashMap<String, UserData>();
 	}
 	
 	@Entity
 	@Table(name = "UserData")
 	private static class UserData {
 		@Id @GeneratedValue
 		private Integer id;
 		
 		@ManyToOne
 		@JoinColumn(name = "userId")
 		private User user;
 	}
+
 }
