diff --git a/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java
index 4e9bf960e4..a713dd8cde 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java
@@ -1,179 +1,184 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.id.enhanced;
 
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
+import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.boot.model.relational.Database;
 import org.hibernate.boot.model.relational.QualifiedName;
 import org.hibernate.boot.model.relational.Schema;
 import org.hibernate.boot.model.relational.Sequence;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.id.IntegralDataTypeHolder;
 import org.hibernate.internal.CoreMessageLogger;
 
 import org.jboss.logging.Logger;
 
 /**
  * Describes a sequence.
  *
  * @author Steve Ebersole
  */
 public class SequenceStructure implements DatabaseStructure {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			SequenceStructure.class.getName()
 	);
 
 	private final QualifiedName logicalQualifiedSequenceName;
 	private final int initialValue;
 	private final int incrementSize;
 	private final Class numberType;
 
 
 	private String sequenceName;
 	private String sql;
 	private boolean applyIncrementSizeToSourceValues;
 	private int accessCounter;
 
 	public SequenceStructure(
 			JdbcEnvironment jdbcEnvironment,
 			QualifiedName qualifiedSequenceName,
 			int initialValue,
 			int incrementSize,
 			Class numberType) {
 		this.logicalQualifiedSequenceName = qualifiedSequenceName;
 
 		this.initialValue = initialValue;
 		this.incrementSize = incrementSize;
 		this.numberType = numberType;
 	}
 
 	@Override
 	public String getName() {
 		return sequenceName;
 	}
 
 	@Override
 	public int getIncrementSize() {
 		return incrementSize;
 	}
 
 	@Override
 	public int getTimesAccessed() {
 		return accessCounter;
 	}
 
 	@Override
 	public int getInitialValue() {
 		return initialValue;
 	}
 
 	@Override
 	public AccessCallback buildCallback(final SessionImplementor session) {
+		if ( sql == null ) {
+			throw new AssertionFailure( "SequenceStyleGenerator's SequenceStructure was not properly initialized" );
+		}
+
 		return new AccessCallback() {
 			@Override
 			public IntegralDataTypeHolder getNextValue() {
 				accessCounter++;
 				try {
 					final PreparedStatement st = session.getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
 					try {
 						final ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
 						try {
 							rs.next();
 							final IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder( numberType );
 							value.initialize( rs, 1 );
 							if ( LOG.isDebugEnabled() ) {
 								LOG.debugf( "Sequence value obtained: %s", value.makeValue() );
 							}
 							return value;
 						}
 						finally {
 							try {
 								session.getJdbcCoordinator().getResourceRegistry().release( rs, st );
 							}
 							catch( Throwable ignore ) {
 								// intentionally empty
 							}
 						}
 					}
 					finally {
 						session.getJdbcCoordinator().getResourceRegistry().release( st );
 						session.getJdbcCoordinator().afterStatementExecution();
 					}
 
 				}
 				catch ( SQLException sqle) {
 					throw session.getFactory().getSQLExceptionHelper().convert(
 							sqle,
 							"could not get next sequence value",
 							sql
 					);
 				}
 			}
 
 			@Override
 			public String getTenantIdentifier() {
 				return session.getTenantIdentifier();
 			}
 		};
 	}
 
 	@Override
 	public void prepare(Optimizer optimizer) {
 		applyIncrementSizeToSourceValues = optimizer.applyIncrementSizeToSourceValues();
 	}
 
 	@Override
 	public void registerExportables(Database database) {
 		final int sourceIncrementSize = applyIncrementSizeToSourceValues ? incrementSize : 1;
 
 
 		final Schema schema = database.locateSchema(
 				logicalQualifiedSequenceName.getCatalogName(),
 				logicalQualifiedSequenceName.getSchemaName()
 		);
 		Sequence sequence = schema.locateSequence( logicalQualifiedSequenceName.getObjectName() );
 		if ( sequence != null ) {
 			sequence.validate( initialValue, sourceIncrementSize );
 		}
 		else {
 			sequence = schema.createSequence( logicalQualifiedSequenceName.getObjectName(), initialValue, sourceIncrementSize );
 		}
 
 		final JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
 		final Dialect dialect = jdbcEnvironment.getDialect();
 
 		this.sequenceName = jdbcEnvironment.getQualifiedObjectNameFormatter().format(
 				sequence.getName(),
 				dialect
 		);
 		this.sql = jdbcEnvironment.getDialect().getSequenceNextValString( sequenceName );
 	}
 
 	@Override
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		final int sourceIncrementSize = applyIncrementSizeToSourceValues ? incrementSize : 1;
 		return dialect.getCreateSequenceStrings( sequenceName, initialValue, sourceIncrementSize );
 	}
 
 	@Override
 	public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
 		return dialect.getDropSequenceStrings( sequenceName );
 	}
 
 	@Override
 	public boolean isPhysicalSequence() {
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
index 4a537931e2..47d575ec2d 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
@@ -1,287 +1,292 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.id.enhanced;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
+import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.relational.Database;
 import org.hibernate.boot.model.relational.InitCommand;
 import org.hibernate.boot.model.relational.QualifiedName;
 import org.hibernate.boot.model.relational.Schema;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.ExportableColumn;
 import org.hibernate.id.IdentifierGenerationException;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.id.IntegralDataTypeHolder;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.jdbc.AbstractReturningWork;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.LongType;
 
 import org.jboss.logging.Logger;
 
 /**
  * Describes a table used to mimic sequence behavior
  *
  * @author Steve Ebersole
  */
 public class TableStructure implements DatabaseStructure {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			TableStructure.class.getName()
 	);
 
 	private final QualifiedName logicalQualifiedTableName;
 	private final Identifier logicalValueColumnNameIdentifier;
 	private final int initialValue;
 	private final int incrementSize;
 	private final Class numberType;
 
 	private String tableNameText;
 	private String valueColumnNameText;
 
 	private String selectQuery;
 	private String updateQuery;
 
 	private boolean applyIncrementSizeToSourceValues;
 	private int accessCounter;
 
 	public TableStructure(
 			JdbcEnvironment jdbcEnvironment,
 			QualifiedName qualifiedTableName,
 			Identifier valueColumnNameIdentifier,
 			int initialValue,
 			int incrementSize,
 			Class numberType) {
 		this.logicalQualifiedTableName = qualifiedTableName;
 		this.logicalValueColumnNameIdentifier = valueColumnNameIdentifier;
 
 		this.initialValue = initialValue;
 		this.incrementSize = incrementSize;
 		this.numberType = numberType;
 	}
 
 	@Override
 	public String getName() {
 		return tableNameText;
 	}
 
 	@Override
 	public int getInitialValue() {
 		return initialValue;
 	}
 
 	@Override
 	public int getIncrementSize() {
 		return incrementSize;
 	}
 
 	@Override
 	public int getTimesAccessed() {
 		return accessCounter;
 	}
 
 	@Override
 	public void prepare(Optimizer optimizer) {
 		applyIncrementSizeToSourceValues = optimizer.applyIncrementSizeToSourceValues();
 	}
 
 	private IntegralDataTypeHolder makeValue() {
 		return IdentifierGeneratorHelper.getIntegralDataTypeHolder( numberType );
 	}
 
 	@Override
 	public AccessCallback buildCallback(final SessionImplementor session) {
 		final SqlStatementLogger statementLogger = session.getFactory().getServiceRegistry()
 				.getService( JdbcServices.class )
 				.getSqlStatementLogger();
+		if ( selectQuery == null || updateQuery == null ) {
+			throw new AssertionFailure( "SequenceStyleGenerator's TableStructure was not properly initialized" );
+		}
+
 		final SessionEventListenerManager statsCollector = session.getEventListenerManager();
 
 		return new AccessCallback() {
 			@Override
 			public IntegralDataTypeHolder getNextValue() {
 				return session.getTransactionCoordinator().createIsolationDelegate().delegateWork(
 						new AbstractReturningWork<IntegralDataTypeHolder>() {
 							@Override
 							public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
 								final IntegralDataTypeHolder value = makeValue();
 								int rows;
 								do {
 									final PreparedStatement selectStatement = prepareStatement(
 											connection,
 											selectQuery,
 											statementLogger,
 											statsCollector
 									);
 									try {
 										final ResultSet selectRS = executeQuery( selectStatement, statsCollector );
 										if ( !selectRS.next() ) {
 											final String err = "could not read a hi value - you need to populate the table: " + tableNameText;
 											LOG.error( err );
 											throw new IdentifierGenerationException( err );
 										}
 										value.initialize( selectRS, 1 );
 										selectRS.close();
 									}
 									catch (SQLException sqle) {
 										LOG.error( "could not read a hi value", sqle );
 										throw sqle;
 									}
 									finally {
 										selectStatement.close();
 									}
 
 
 									final PreparedStatement updatePS = prepareStatement(
 											connection,
 											updateQuery,
 											statementLogger,
 											statsCollector
 									);
 									try {
 										final int increment = applyIncrementSizeToSourceValues ? incrementSize : 1;
 										final IntegralDataTypeHolder updateValue = value.copy().add( increment );
 										updateValue.bind( updatePS, 1 );
 										value.bind( updatePS, 2 );
 										rows = executeUpdate( updatePS, statsCollector );
 									}
 									catch (SQLException e) {
 										LOG.unableToUpdateQueryHiValue( tableNameText, e );
 										throw e;
 									}
 									finally {
 										updatePS.close();
 									}
 								} while ( rows == 0 );
 
 								accessCounter++;
 
 								return value;
 							}
 						},
 						true
 				);
 			}
 
 			@Override
 			public String getTenantIdentifier() {
 				return session.getTenantIdentifier();
 			}
 		};
 	}
 
 	private PreparedStatement prepareStatement(
 			Connection connection,
 			String sql,
 			SqlStatementLogger statementLogger,
 			SessionEventListenerManager statsCollector) throws SQLException {
 		statementLogger.logStatement( sql, FormatStyle.BASIC.getFormatter() );
 		try {
 			statsCollector.jdbcPrepareStatementStart();
 			return connection.prepareStatement( sql );
 		}
 		finally {
 			statsCollector.jdbcPrepareStatementEnd();
 		}
 	}
 
 	private int executeUpdate(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
 		try {
 			statsCollector.jdbcExecuteStatementStart();
 			return ps.executeUpdate();
 		}
 		finally {
 			statsCollector.jdbcExecuteStatementEnd();
 		}
 
 	}
 
 	private ResultSet executeQuery(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
 		try {
 			statsCollector.jdbcExecuteStatementStart();
 			return ps.executeQuery();
 		}
 		finally {
 			statsCollector.jdbcExecuteStatementEnd();
 		}
 	}
 
 	@Override
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		return new String[] {
 				dialect.getCreateTableString() + " " + tableNameText + " ( " + valueColumnNameText + " " + dialect.getTypeName( Types.BIGINT ) + " )",
 				"insert into " + tableNameText + " values ( " + initialValue + " )"
 		};
 	}
 
 	@Override
 	public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
 		return new String[] { dialect.getDropTableString( tableNameText ) };
 	}
 
 	@Override
 	public boolean isPhysicalSequence() {
 		return false;
 	}
 
 	@Override
 	public void registerExportables(Database database) {
 		final JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
 		final Dialect dialect = jdbcEnvironment.getDialect();
 
 		final Schema schema = database.locateSchema(
 				logicalQualifiedTableName.getCatalogName(),
 				logicalQualifiedTableName.getSchemaName()
 		);
 
 		Table table = schema.locateTable( logicalQualifiedTableName.getObjectName() );
 		if ( table == null ) {
 			table = schema.createTable( logicalQualifiedTableName.getObjectName(), false );
 		}
 
 		this.tableNameText = jdbcEnvironment.getQualifiedObjectNameFormatter().format(
 				table.getQualifiedTableName(),
 				dialect
 		);
 
 		this.valueColumnNameText = logicalValueColumnNameIdentifier.render( dialect );
 
 
 		this.selectQuery = "select " + valueColumnNameText + " as id_val" +
 				" from " + dialect.appendLockHint( LockMode.PESSIMISTIC_WRITE, tableNameText ) +
 				dialect.getForUpdateString();
 
 		this.updateQuery = "update " + tableNameText +
 				" set " + valueColumnNameText + "= ?" +
 				" where " + valueColumnNameText + "=?";
 
 		ExportableColumn valueColumn = new ExportableColumn(
 				database,
 				table,
 				valueColumnNameText,
 				LongType.INSTANCE
 		);
 		table.addColumn( valueColumn );
 
 		database.addInitCommand(
 				new InitCommand( "insert into " + tableNameText + " values ( " + initialValue + " )" )
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index 2af454ac64..d02a7c3eac 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,643 +1,651 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.lang.annotation.Annotation;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 import javax.persistence.AttributeConverter;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cfg.AttributeConverterDefinition;
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
 import org.hibernate.type.descriptor.converter.AttributeConverterSqlTypeDescriptorAdapter;
 import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
 import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
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
 
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private boolean alternateUniqueKey;
 	private boolean cascadeDeleteEnabled;
 
 	private AttributeConverterDefinition attributeConverterDefinition;
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
 		column.setTypeIndex( columns.size()-1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add(formula);
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
 				attributeConverterDefinition = new AttributeConverterDefinition( converterClass.newInstance(), false );
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
 
+	private IdentifierGenerator identifierGenerator;
+
 	@Override
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect, 
 			String defaultCatalog, 
 			String defaultSchema, 
 			RootClass rootClass) throws MappingException {
-		
+
+		if ( identifierGenerator != null ) {
+			return identifierGenerator;
+		}
+
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
-		return identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
+		identifierGenerator = identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
+
+		return identifierGenerator;
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
 
 		if ( attributeConverterDefinition == null ) {
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
 
 		final Class entityAttributeJavaType = attributeConverterDefinition.getEntityAttributeType();
 		final Class databaseColumnJavaType = attributeConverterDefinition.getDatabaseColumnType();
 
 
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
 				attributeConverterDefinition.getAttributeConverter(),
 				sqlTypeDescriptor,
 				intermediateJavaTypeDescriptor
 		);
 
 		// todo : cache the AttributeConverterTypeAdapter in case that AttributeConverter is applied multiple times.
 
 		final String name = AttributeConverterTypeAdapter.NAME_PREFIX + attributeConverterDefinition.getAttributeConverter().getClass().getName();
 		final String description = String.format(
 				"BasicType adapter for AttributeConverter<%s,%s>",
 				entityAttributeJavaType.getSimpleName(),
 				databaseColumnJavaType.getSimpleName()
 		);
 		return new AttributeConverterTypeAdapter(
 				name,
 				description,
 				attributeConverterDefinition.getAttributeConverter(),
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
 
 	public void setJpaAttributeConverterDefinition(AttributeConverterDefinition attributeConverterDefinition) {
 		this.attributeConverterDefinition = attributeConverterDefinition;
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
