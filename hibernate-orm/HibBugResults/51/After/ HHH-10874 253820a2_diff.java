diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 6993693797..a3962dfa5c 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -15,2037 +15,2041 @@ import java.util.HashMap;
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
 			iter = indexedCollection.getIndex().getColumnIterator();
 			indexColumnNames = new String[indexSpan];
 			indexFormulaTemplates = new String[indexSpan];
 			indexFormulas = new String[indexSpan];
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
 					indexColumnIsSettable[i] = true;
 				}
 				i++;
 			}
 			indexContainsFormula = hasFormula;
 			baseIndex = indexedCollection.isList() ?
 					( (List) indexedCollection ).getBaseIndex() : 0;
 		}
 		else {
 			indexContainsFormula = false;
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
+				.addWhereToken( sqlWhereString )
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
+				.addWhereToken( sqlWhereString )
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
+				.addWhereToken( sqlWhereString )
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
+				.addWhereToken( sqlWhereString )
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
 			for ( int i = 0; i < indexColumnIsSettable.length; i++ ) {
 				if ( indexColumnIsSettable[i] ) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/SimpleSelect.java b/hibernate-core/src/main/java/org/hibernate/sql/SimpleSelect.java
index 51fbc86482..f27f407b2c 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/SimpleSelect.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/SimpleSelect.java
@@ -1,210 +1,215 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.sql;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.Dialect;
 
 /**
  * An SQL <tt>SELECT</tt> statement with no table joins
  *
  * @author Gavin King
  */
 public class SimpleSelect {
 
 	public SimpleSelect(Dialect dialect) {
 		this.dialect = dialect;
 	}
 
 	//private static final Alias DEFAULT_ALIAS = new Alias(10, null);
 
 	private String tableName;
 	private String orderBy;
 	private Dialect dialect;
 	private LockOptions lockOptions = new LockOptions( LockMode.READ );
 	private String comment;
 
 	private List<String> columns = new ArrayList<String>();
 	private Map<String, String> aliases = new HashMap<String, String>();
 	private List<String> whereTokens = new ArrayList<String>();
 
 	public SimpleSelect addColumns(String[] columnNames, String[] columnAliases) {
 		for ( int i = 0; i < columnNames.length; i++ ) {
 			if ( columnNames[i] != null ) {
 				addColumn( columnNames[i], columnAliases[i] );
 			}
 		}
 		return this;
 	}
 
 	public SimpleSelect addColumns(String[] columns, String[] aliases, boolean[] ignore) {
 		for ( int i = 0; i < ignore.length; i++ ) {
 			if ( !ignore[i] && columns[i] != null ) {
 				addColumn( columns[i], aliases[i] );
 			}
 		}
 		return this;
 	}
 
 	public SimpleSelect addColumns(String[] columnNames) {
 		for ( String columnName : columnNames ) {
 			if ( columnName != null ) {
 				addColumn( columnName );
 			}
 		}
 		return this;
 	}
 
 	public SimpleSelect addColumn(String columnName) {
 		columns.add( columnName );
 		//aliases.put( columnName, DEFAULT_ALIAS.toAliasString(columnName) );
 		return this;
 	}
 
 	public SimpleSelect addColumn(String columnName, String alias) {
 		columns.add( columnName );
 		aliases.put( columnName, alias );
 		return this;
 	}
 
 	public SimpleSelect setTableName(String tableName) {
 		this.tableName = tableName;
 		return this;
 	}
 
 	public SimpleSelect setLockOptions(LockOptions lockOptions) {
 		LockOptions.copy( lockOptions, this.lockOptions );
 		return this;
 	}
 
 	public SimpleSelect setLockMode(LockMode lockMode) {
 		this.lockOptions.setLockMode( lockMode );
 		return this;
 	}
 
 	public SimpleSelect addWhereToken(String token) {
-		whereTokens.add( token );
+		if (token != null ) {
+			if (!whereTokens.isEmpty()) {
+				and();
+			}
+			whereTokens.add( token );
+		}
 		return this;
 	}
 
 	private void and() {
 		if ( whereTokens.size() > 0 ) {
 			whereTokens.add( "and" );
 		}
 	}
 
 	public SimpleSelect addCondition(String lhs, String op, String rhs) {
 		and();
 		whereTokens.add( lhs + ' ' + op + ' ' + rhs );
 		return this;
 	}
 
 	public SimpleSelect addCondition(String lhs, String condition) {
 		and();
 		whereTokens.add( lhs + ' ' + condition );
 		return this;
 	}
 
 	public SimpleSelect addCondition(String[] lhs, String op, String[] rhs) {
 		for ( int i = 0; i < lhs.length; i++ ) {
 			addCondition( lhs[i], op, rhs[i] );
 		}
 		return this;
 	}
 
 	public SimpleSelect addCondition(String[] lhs, String condition) {
 		for ( String lh : lhs ) {
 			if ( lh != null ) {
 				addCondition( lh, condition );
 			}
 		}
 		return this;
 	}
 
 	public String toStatementString() {
 		StringBuilder buf = new StringBuilder(
 				columns.size() * 10 +
 						tableName.length() +
 						whereTokens.size() * 10 +
 						10
 		);
 
 		if ( comment != null ) {
 			buf.append( "/* " ).append( comment ).append( " */ " );
 		}
 
 		buf.append( "select " );
 		Set<String> uniqueColumns = new HashSet<String>();
 		Iterator<String> iter = columns.iterator();
 		boolean appendComma = false;
 		while ( iter.hasNext() ) {
 			String col = iter.next();
 			String alias = aliases.get( col );
 			if ( uniqueColumns.add( alias == null ? col : alias ) ) {
 				if ( appendComma ) {
 					buf.append( ", " );
 				}
 				buf.append( col );
 				if ( alias != null && !alias.equals( col ) ) {
 					buf.append( " as " )
 							.append( alias );
 				}
 				appendComma = true;
 			}
 		}
 
 		buf.append( " from " )
 				.append( dialect.appendLockHint( lockOptions, tableName ) );
 
 		if ( whereTokens.size() > 0 ) {
 			buf.append( " where " )
 					.append( toWhereClause() );
 		}
 
 		if ( orderBy != null ) {
 			buf.append( orderBy );
 		}
 
 		if ( lockOptions != null ) {
 			buf = new StringBuilder(dialect.applyLocksToSql( buf.toString(), lockOptions, null ) );
 		}
 
 		return dialect.transformSelectString( buf.toString() );
 	}
 
 	public String toWhereClause() {
 		StringBuilder buf = new StringBuilder( whereTokens.size() * 5 );
 		Iterator<String> iter = whereTokens.iterator();
 		while ( iter.hasNext() ) {
 			buf.append( iter.next() );
 			if ( iter.hasNext() ) {
 				buf.append( ' ' );
 			}
 		}
 		return buf.toString();
 	}
 
 	public SimpleSelect setOrderBy(String orderBy) {
 		this.orderBy = orderBy;
 		return this;
 	}
 
 	public SimpleSelect setComment(String comment) {
 		this.comment = comment;
 		return this;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/extralazy/Championship.java b/hibernate-core/src/test/java/org/hibernate/test/extralazy/Championship.java
new file mode 100644
index 0000000000..e59bd4f5de
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/extralazy/Championship.java
@@ -0,0 +1,44 @@
+package org.hibernate.test.extralazy;
+
+import java.util.ArrayList;
+import java.util.List;
+import javax.persistence.CascadeType;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.OneToMany;
+import javax.persistence.Table;
+
+import org.hibernate.annotations.LazyCollection;
+import org.hibernate.annotations.LazyCollectionOption;
+import org.hibernate.annotations.Where;
+
+@Entity
+@Table(name = "championship")
+public class Championship {
+
+	@Id
+	private int id;
+
+	@OneToMany(cascade = CascadeType.ALL)
+	@LazyCollection(LazyCollectionOption.EXTRA)
+	@Where(clause = " gpa >= 4 ")
+	private List<Student> students = new ArrayList<>();
+
+	public Championship() {}
+
+	public Championship(int id) {
+		this.id = id;
+	}
+
+	public int getId() {
+		return id;
+	}
+
+	public void setId(int id) {
+		this.id = id;
+	}
+
+	public List<Student> getStudents() {
+		return students;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/extralazy/ExtraLazyTest.java b/hibernate-core/src/test/java/org/hibernate/test/extralazy/ExtraLazyTest.java
index 1b7afddbc6..cd621bd067 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/extralazy/ExtraLazyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/extralazy/ExtraLazyTest.java
@@ -1,252 +1,344 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.extralazy;
-import static org.junit.Assert.assertEquals;
-import static org.junit.Assert.assertFalse;
-import static org.junit.Assert.assertNotNull;
-import static org.junit.Assert.assertNull;
-import static org.junit.Assert.assertTrue;
 
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 
 import org.hibernate.testing.DialectChecks;
+import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.RequiresDialectFeature;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertNotNull;
+import static org.junit.Assert.assertNull;
+import static org.junit.Assert.assertTrue;
+
 /**
  * @author Gavin King
  */
 public class ExtraLazyTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "extralazy/UserGroup.hbm.xml","extralazy/Parent.hbm.xml","extralazy/Child.hbm.xml" };
 	}
+	
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class<?>[] { School.class, Student.class, Championship.class };
+	}
 
 	@Test
 	public void testOrphanDelete() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User gavin = new User("gavin", "secret");
 		Document hia = new Document("HiA", "blah blah blah", gavin);
 		Document hia2 = new Document("HiA2", "blah blah blah blah", gavin);
 		s.persist(gavin);
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		gavin = (User) s.get(User.class, "gavin");
 		assertEquals( 2, gavin.getDocuments().size() );
 		gavin.getDocuments().remove(hia2);
 		assertFalse( gavin.getDocuments().contains(hia2) );
 		assertTrue( gavin.getDocuments().contains(hia) );
 		assertEquals( 1, gavin.getDocuments().size() );
 		assertFalse( Hibernate.isInitialized( gavin.getDocuments() ) );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		gavin = (User) s.get(User.class, "gavin");
 		assertEquals( 1, gavin.getDocuments().size() );
 		assertFalse( gavin.getDocuments().contains(hia2) );
 		assertTrue( gavin.getDocuments().contains(hia) );
 		assertFalse( Hibernate.isInitialized( gavin.getDocuments() ) );
 		assertNull( s.get(Document.class, "HiA2") );
 		gavin.getDocuments().clear();
 		assertTrue( Hibernate.isInitialized( gavin.getDocuments() ) );
 		s.delete(gavin);
 		t.commit();
 		s.close();
 	}
 	
 	@Test
 	public void testGet() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User gavin = new User("gavin", "secret");
 		User turin = new User("turin", "tiger");
 		Group g = new Group("developers");
 		g.getUsers().put("gavin", gavin);
 		g.getUsers().put("turin", turin);
 		s.persist(g);
 		gavin.getSession().put( "foo", new SessionAttribute("foo", "foo bar baz") );
 		gavin.getSession().put( "bar", new SessionAttribute("bar", "foo bar baz 2") );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		g = (Group) s.get(Group.class, "developers");
 		gavin = (User) g.getUsers().get("gavin");
 		turin = (User) g.getUsers().get("turin");
 		assertNotNull(gavin);
 		assertNotNull(turin);
 		assertNull( g.getUsers().get("emmanuel") );
 		assertFalse( Hibernate.isInitialized( g.getUsers() ) );
 		assertNotNull( gavin.getSession().get("foo") );
 		assertNull( turin.getSession().get("foo") );
 		assertFalse( Hibernate.isInitialized( gavin.getSession() ) );
 		assertFalse( Hibernate.isInitialized( turin.getSession() ) );
 		s.delete(gavin);
 		s.delete(turin);
 		s.delete(g);
 		t.commit();
 		s.close();
 	}
 	
 	@Test
 	public void testRemoveClear() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User gavin = new User("gavin", "secret");
 		User turin = new User("turin", "tiger");
 		Group g = new Group("developers");
 		g.getUsers().put("gavin", gavin);
 		g.getUsers().put("turin", turin);
 		s.persist(g);
 		gavin.getSession().put( "foo", new SessionAttribute("foo", "foo bar baz") );
 		gavin.getSession().put( "bar", new SessionAttribute("bar", "foo bar baz 2") );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		g = (Group) s.get(Group.class, "developers");
 		gavin = (User) g.getUsers().get("gavin");
 		turin = (User) g.getUsers().get("turin");
 		assertFalse( Hibernate.isInitialized( g.getUsers() ) );
 		g.getUsers().clear();
 		gavin.getSession().remove("foo");
 		assertTrue( Hibernate.isInitialized( g.getUsers() ) );
 		assertTrue( Hibernate.isInitialized( gavin.getSession() ) );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		g = (Group) s.get(Group.class, "developers");
 		assertTrue( g.getUsers().isEmpty() );
 		assertFalse( Hibernate.isInitialized( g.getUsers() ) );
 		gavin = (User) s.get(User.class, "gavin");
 		assertFalse( gavin.getSession().containsKey("foo") );
 		assertFalse( Hibernate.isInitialized( gavin.getSession() ) );
 		s.delete(gavin);
 		s.delete(turin);
 		s.delete(g);
 		t.commit();
 		s.close();
 	}
 	
 	@Test
 	public void testIndexFormulaMap() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User gavin = new User("gavin", "secret");
 		User turin = new User("turin", "tiger");
 		Group g = new Group("developers");
 		g.getUsers().put("gavin", gavin);
 		g.getUsers().put("turin", turin);
 		s.persist(g);
 		gavin.getSession().put( "foo", new SessionAttribute("foo", "foo bar baz") );
 		gavin.getSession().put( "bar", new SessionAttribute("bar", "foo bar baz 2") );
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		g = (Group) s.get(Group.class, "developers");
 		assertEquals( g.getUsers().size(), 2 );
 		g.getUsers().remove("turin");
 		Map smap = ( (User) g.getUsers().get("gavin") ).getSession();
 		assertEquals(smap.size(), 2);
 		smap.remove("bar");
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		g = (Group) s.get(Group.class, "developers");
 		assertEquals( g.getUsers().size(), 1 );
 		smap = ( (User) g.getUsers().get("gavin") ).getSession();
 		assertEquals(smap.size(), 1);
 		gavin = (User) g.getUsers().put("gavin", turin);
 		s.delete(gavin);
 		assertEquals( s.createQuery("select count(*) from SessionAttribute").uniqueResult(), new Long(0) );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		g = (Group) s.get(Group.class, "developers");
 		assertEquals( g.getUsers().size(), 1 );
 		turin = (User) g.getUsers().get("turin");
 		smap = turin.getSession();
 		assertEquals(smap.size(), 0);
 		assertEquals( s.createQuery("select count(*) from User").uniqueResult(), new Long(1) );
 		s.delete(g);
 		s.delete(turin);
 		assertEquals( s.createQuery("select count(*) from User").uniqueResult(), new Long(0) );
 		t.commit();
 		s.close();
 	}
 	
 	@Test
 	@RequiresDialectFeature( DialectChecks.DoubleQuoteQuoting.class )
 	public void testSQLQuery() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User gavin = new User("gavin", "secret");
 		User turin = new User("turin", "tiger");
 		gavin.getSession().put( "foo", new SessionAttribute("foo", "foo bar baz") );
 		gavin.getSession().put( "bar", new SessionAttribute("bar", "foo bar baz 2") );
 		s.persist(gavin);
 		s.persist(turin);
 		s.flush();
 		s.clear();
 		List results = s.getNamedQuery("userSessionData").setParameter("uname", "%in").list();
 		assertEquals( results.size(), 2 );
 		gavin = (User) ( (Object[]) results.get(0) )[0];
 		assertEquals( gavin.getName(), "gavin" );
 		assertEquals( gavin.getSession().size(), 2 );
 		s.createQuery("delete SessionAttribute").executeUpdate();
 		s.createQuery("delete User").executeUpdate();
 		t.commit();
 		s.close();
 		
 	}
 
 	@Test
 	@TestForIssue(jiraKey="HHH-4294")
 	public void testMap() {
 		Session session1 = openSession();
 		Transaction tx1 = session1.beginTransaction();
 		Parent parent = new Parent ();		
 		Child child = new Child ();
 		child.setFirstName("Ben");
 		parent.getChildren().put(child.getFirstName(), child);
 		child.setParent(parent);		
 		session1.save(parent);
 		tx1.commit();
 		session1.close();
 		// END PREPARE SECTION
 		
 		Session session2 = openSession();
 		Parent parent2 = (Parent)session2.get(Parent.class, parent.getId());
 		Child child2 = parent2.getChildren().get(child.getFirstName()); // causes SQLGrammarException because of wrong condition: 	where child0_.PARENT_ID=? and child0_.null=?
 		assertNotNull(child2);
 		session2.close();
 	}
+	
+	@Test
+	@TestForIssue(jiraKey = "HHH-10874")
+	public void testWhereClauseOnBidirectionalCollection() {
+		Session s = openSession();
+		Transaction t = s.beginTransaction();
+
+		School school = new School(1);
+		s.persist(school);
+
+		Student gavin = new Student("gavin", 4);
+		Student turin = new Student("turin", 3);
+		Student mike = new Student("mike", 5);
+		Student fred = new Student("fred", 2);
+
+		gavin.setSchool(school);
+		turin.setSchool(school);
+		mike.setSchool(school);
+		fred.setSchool(school);
+
+		s.persist(gavin);
+		s.persist(turin);
+		s.persist(mike);
+		s.persist(fred);
+
+		t.commit();
+		s.close();
+
+		s = openSession();
+		School school2 = s.get(School.class, 1);
+
+		assertEquals(4, school2.getStudents().size());
+
+		assertEquals( 2, school2.getTopStudents().size() );
+		assertTrue( school2.getTopStudents().contains( gavin ) );
+		assertTrue( school2.getTopStudents().contains( mike ) );
+
+		assertEquals(2,  school2.getStudentsMap().size() );
+		assertTrue( school2.getStudentsMap().containsKey( gavin.getId() ) );
+		assertTrue( school2.getStudentsMap().containsKey( mike.getId() ) );
+
+		s.close();
+	}
+
+	@Test
+	@FailureExpected( jiraKey = "HHH-3319" )
+	public void testWhereClauseOnUnidirectionalCollection() {
+		Session s = openSession();
+		Transaction t = s.beginTransaction();
+
+		Championship championship = new Championship( 1 );
+		s.persist(championship);
+
+		Student gavin = new Student("gavin", 4);
+		Student turin = new Student("turin", 3);
+		Student mike = new Student("mike", 5);
+		Student fred = new Student("fred", 2);
+
+		championship.getStudents().add( gavin );
+		championship.getStudents().add( turin );
+		championship.getStudents().add( mike );
+		championship.getStudents().add( fred );
+
+		s.persist(gavin);
+		s.persist(turin);
+		s.persist(mike);
+		s.persist(fred);
+
+		t.commit();
+		s.close();
+
+		s = openSession();
+
+		Championship championship2 = s.get(Championship.class, 1);
+		assertEquals( 2, championship2.getStudents().size() );
+		assertTrue( championship2.getStudents().contains( gavin ) );
+		assertTrue( championship2.getStudents().contains( mike ) );
+
+		s.close();
+	}
+
+	@Override
+	protected boolean rebuildSessionFactoryOnError() {
+		return false;
+	}
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/extralazy/School.java b/hibernate-core/src/test/java/org/hibernate/test/extralazy/School.java
new file mode 100644
index 0000000000..faf307da80
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/extralazy/School.java
@@ -0,0 +1,64 @@
+package org.hibernate.test.extralazy;
+
+import java.util.HashMap;
+import java.util.HashSet;
+import java.util.Map;
+import java.util.Set;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.MapKey;
+import javax.persistence.OneToMany;
+import javax.persistence.Table;
+
+import org.hibernate.annotations.LazyCollection;
+import org.hibernate.annotations.LazyCollectionOption;
+import org.hibernate.annotations.Where;
+
+@Entity
+@Table(name = "school")
+public class School {
+
+	@Id
+	private int id;
+
+	@OneToMany(mappedBy = "school")
+	@LazyCollection(LazyCollectionOption.EXTRA)
+	private Set<Student> students = new HashSet<Student>();
+
+	@OneToMany(mappedBy = "school")
+	@LazyCollection(LazyCollectionOption.EXTRA)
+	@Where(clause = " gpa >= 4 ")
+	private Set<Student> topStudents = new HashSet<Student>();
+
+	@OneToMany(mappedBy = "school")
+	@LazyCollection(LazyCollectionOption.EXTRA)
+	@Where(clause = " gpa >= 4 ")
+	@MapKey
+	private Map<String, Student> studentsMap = new HashMap<>();
+	
+	public School() {}
+
+	public School(int id) {
+		this.id = id;
+	}
+
+	public int getId() {
+		return id;
+	}
+
+	public void setId(int id) {
+		this.id = id;
+	}
+
+	public Set<Student> getStudents() {
+		return students;
+	}
+
+	public Set<Student> getTopStudents() {
+		return topStudents;
+	}
+
+	public Map<String, Student> getStudentsMap() {
+		return studentsMap;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/extralazy/Student.java b/hibernate-core/src/test/java/org/hibernate/test/extralazy/Student.java
new file mode 100644
index 0000000000..6086d92b59
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/extralazy/Student.java
@@ -0,0 +1,83 @@
+package org.hibernate.test.extralazy;
+
+import java.util.Objects;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.ManyToOne;
+import javax.persistence.Table;
+
+import static javax.persistence.GenerationType.IDENTITY;
+
+@Entity
+@Table(name = "student")
+public class Student {
+
+	@Id
+	@GeneratedValue(strategy = IDENTITY)
+	private String id;
+
+	@ManyToOne
+	private School school;
+
+	private String firstName;
+
+	private int gpa;
+	
+	public Student() {}
+
+	public Student(String firstName, int gpa) {
+		this.firstName = firstName;
+		this.gpa = gpa;
+	}
+
+	public String getId() {
+		return id;
+	}
+
+	public void setId(String id) {
+		this.id = id;
+	}
+
+	public School getSchool() {
+		return school;
+	}
+
+	public void setSchool(School school) {
+		this.school = school;
+	}
+
+	public String getFirstName() {
+		return firstName;
+	}
+
+	public void setFirstName(String firstName) {
+		this.firstName = firstName;
+	}
+
+	public int getGpa() {
+		return gpa;
+	}
+
+	public void setGpa(int gpa) {
+		this.gpa = gpa;
+	}
+
+
+	@Override
+	public boolean equals(Object o) {
+		if ( this == o ) {
+			return true;
+		}
+		if ( !( o instanceof Student ) ) {
+			return false;
+		}
+		Student student = (Student) o;
+		return Objects.equals( getFirstName(), student.getFirstName() );
+	}
+
+	@Override
+	public int hashCode() {
+		return Objects.hash( getFirstName() );
+	}
+}
