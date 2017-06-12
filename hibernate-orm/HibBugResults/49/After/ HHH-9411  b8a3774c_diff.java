diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
index 85db93a138..b20f2876d4 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
@@ -1,825 +1,840 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.boot.model.relational.Database;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.DynamicFilterAliasGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.Value;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.sql.InFragment;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.DiscriminatorType;
 import org.hibernate.type.Type;
 
 /**
  * The default implementation of the <tt>EntityPersister</tt> interface.
  * Implements the "table-per-class-hierarchy" or "roll-up" mapping strategy
  * for an entity class and its inheritence hierarchy.  This is implemented
  * as a single table holding all classes in the hierarchy with a discrimator
  * column used to determine which concrete class is referenced.
  *
  * @author Gavin King
  */
 public class SingleTableEntityPersister extends AbstractEntityPersister {
 
 	// the class hierarchy structure
 	private final int joinSpan;
 	private final String[] qualifiedTableNames;
 	private final boolean[] isInverseTable;
 	private final boolean[] isNullableTable;
 	private final String[][] keyColumnNames;
 	private final boolean[] cascadeDeleteEnabled;
 	private final boolean hasSequentialSelects;
 
 	private final String[] spaces;
 
 	private final String[] subclassClosure;
 
 	private final String[] subclassTableNameClosure;
 	private final boolean[] subclassTableIsLazyClosure;
 	private final boolean[] isInverseSubclassTable;
 	private final boolean[] isNullableSubclassTable;
 	private final boolean[] subclassTableSequentialSelect;
 	private final String[][] subclassTableKeyColumnClosure;
 	private final boolean[] isClassOrSuperclassTable;
 
 	// properties of this class, including inherited properties
 	private final int[] propertyTableNumbers;
 
 	// the closure of all columns used by the entire hierarchy including
 	// subclasses and superclasses of this class
 	private final int[] subclassPropertyTableNumberClosure;
 
 	private final int[] subclassColumnTableNumberClosure;
 	private final int[] subclassFormulaTableNumberClosure;
 
 	// discriminator column
 	private final Map<Object, String> subclassesByDiscriminatorValue = new HashMap<Object, String>();
 	private final boolean forceDiscriminator;
 	private final String discriminatorColumnName;
 	private final String discriminatorColumnReaders;
 	private final String discriminatorColumnReaderTemplate;
 	private final String discriminatorFormula;
 	private final String discriminatorFormulaTemplate;
 	private final String discriminatorAlias;
 	private final Type discriminatorType;
 	private final Object discriminatorValue;
 	private final String discriminatorSQLValue;
 	private final boolean discriminatorInsertable;
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	//private final Map propertyTableNumbersByName = new HashMap();
 	private final Map<String, Integer> propertyTableNumbersByNameAndSubclass = new HashMap<String, Integer>();
 
 	private final Map<String, String> sequentialSelectStringsByEntityName = new HashMap<String, String>();
 
 	private static final Object NULL_DISCRIMINATOR = new MarkerObject( "<null discriminator>" );
 	private static final Object NOT_NULL_DISCRIMINATOR = new MarkerObject( "<not null discriminator>" );
 	private static final String NULL_STRING = "null";
 	private static final String NOT_NULL_STRING = "not null";
 
 	//INITIALIZATION:
 
 	public SingleTableEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final PersisterCreationContext creationContext) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, creationContext );
 
 		final SessionFactoryImplementor factory = creationContext.getSessionFactory();
 
 		final Database database = creationContext.getMetadata().getDatabase();
 		final JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
 
 		// CLASS + TABLE
 
 		joinSpan = persistentClass.getJoinClosureSpan() + 1;
 		qualifiedTableNames = new String[joinSpan];
 		isInverseTable = new boolean[joinSpan];
 		isNullableTable = new boolean[joinSpan];
 		keyColumnNames = new String[joinSpan][];
 		final Table table = persistentClass.getRootTable();
 		qualifiedTableNames[0] = determineTableName( table, jdbcEnvironment );
 
 		isInverseTable[0] = false;
 		isNullableTable[0] = false;
 		keyColumnNames[0] = getIdentifierColumnNames();
 		cascadeDeleteEnabled = new boolean[joinSpan];
 
 		// Custom sql
 		customSQLInsert = new String[joinSpan];
 		customSQLUpdate = new String[joinSpan];
 		customSQLDelete = new String[joinSpan];
 		insertCallable = new boolean[joinSpan];
 		updateCallable = new boolean[joinSpan];
 		deleteCallable = new boolean[joinSpan];
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 
 		customSQLInsert[0] = persistentClass.getCustomSQLInsert();
 		insertCallable[0] = customSQLInsert[0] != null && persistentClass.isCustomInsertCallable();
 		insertResultCheckStyles[0] = persistentClass.getCustomSQLInsertCheckStyle() == null
 				? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[0], insertCallable[0] )
 				: persistentClass.getCustomSQLInsertCheckStyle();
 		customSQLUpdate[0] = persistentClass.getCustomSQLUpdate();
 		updateCallable[0] = customSQLUpdate[0] != null && persistentClass.isCustomUpdateCallable();
 		updateResultCheckStyles[0] = persistentClass.getCustomSQLUpdateCheckStyle() == null
 				? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[0], updateCallable[0] )
 				: persistentClass.getCustomSQLUpdateCheckStyle();
 		customSQLDelete[0] = persistentClass.getCustomSQLDelete();
 		deleteCallable[0] = customSQLDelete[0] != null && persistentClass.isCustomDeleteCallable();
 		deleteResultCheckStyles[0] = persistentClass.getCustomSQLDeleteCheckStyle() == null
 				? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[0], deleteCallable[0] )
 				: persistentClass.getCustomSQLDeleteCheckStyle();
 
 		// JOINS
 
 		Iterator joinIter = persistentClass.getJoinClosureIterator();
 		int j = 1;
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			qualifiedTableNames[j] = determineTableName( join.getTable(), jdbcEnvironment );
 			isInverseTable[j] = join.isInverse();
 			isNullableTable[j] = join.isOptional();
 			cascadeDeleteEnabled[j] = join.getKey().isCascadeDeleteEnabled() &&
 					factory.getDialect().supportsCascadeDelete();
 
 			customSQLInsert[j] = join.getCustomSQLInsert();
 			insertCallable[j] = customSQLInsert[j] != null && join.isCustomInsertCallable();
 			insertResultCheckStyles[j] = join.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[j], insertCallable[j] )
 					: join.getCustomSQLInsertCheckStyle();
 			customSQLUpdate[j] = join.getCustomSQLUpdate();
 			updateCallable[j] = customSQLUpdate[j] != null && join.isCustomUpdateCallable();
 			updateResultCheckStyles[j] = join.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[j], updateCallable[j] )
 					: join.getCustomSQLUpdateCheckStyle();
 			customSQLDelete[j] = join.getCustomSQLDelete();
 			deleteCallable[j] = customSQLDelete[j] != null && join.isCustomDeleteCallable();
 			deleteResultCheckStyles[j] = join.getCustomSQLDeleteCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[j], deleteCallable[j] )
 					: join.getCustomSQLDeleteCheckStyle();
 
 			Iterator iter = join.getKey().getColumnIterator();
 			keyColumnNames[j] = new String[join.getKey().getColumnSpan()];
 			int i = 0;
 			while ( iter.hasNext() ) {
 				Column col = (Column) iter.next();
 				keyColumnNames[j][i++] = col.getQuotedName( factory.getDialect() );
 			}
 
 			j++;
 		}
 
 		constraintOrderedTableNames = new String[qualifiedTableNames.length];
 		constraintOrderedKeyColumnNames = new String[qualifiedTableNames.length][];
 		for ( int i = qualifiedTableNames.length - 1, position = 0; i >= 0; i--, position++ ) {
 			constraintOrderedTableNames[position] = qualifiedTableNames[i];
 			constraintOrderedKeyColumnNames[position] = keyColumnNames[i];
 		}
 
 		spaces = ArrayHelper.join(
 				qualifiedTableNames,
 				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
 		);
 
 		final boolean lazyAvailable = isInstrumented();
 
 		boolean hasDeferred = false;
 		ArrayList<String> subclassTables = new ArrayList<String>();
 		ArrayList<String[]> joinKeyColumns = new ArrayList<String[]>();
 		ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
 		ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
 		ArrayList<Boolean> isInverses = new ArrayList<Boolean>();
 		ArrayList<Boolean> isNullables = new ArrayList<Boolean>();
 		ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
 		subclassTables.add( qualifiedTableNames[0] );
 		joinKeyColumns.add( getIdentifierColumnNames() );
 		isConcretes.add( Boolean.TRUE );
 		isDeferreds.add( Boolean.FALSE );
 		isInverses.add( Boolean.FALSE );
 		isNullables.add( Boolean.FALSE );
 		isLazies.add( Boolean.FALSE );
 		joinIter = persistentClass.getSubclassJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			isConcretes.add( persistentClass.isClassOrSuperclassJoin( join ) );
 			isDeferreds.add( join.isSequentialSelect() );
 			isInverses.add( join.isInverse() );
 			isNullables.add( join.isOptional() );
 			isLazies.add( lazyAvailable && join.isLazy() );
 			if ( join.isSequentialSelect() && !persistentClass.isClassOrSuperclassJoin( join ) ) {
 				hasDeferred = true;
 			}
 			subclassTables.add(
 					join.getTable().getQualifiedName(
 							factory.getDialect(),
 							factory.getSettings().getDefaultCatalogName(),
 							factory.getSettings().getDefaultSchemaName()
 					)
 			);
 			Iterator iter = join.getKey().getColumnIterator();
 			String[] keyCols = new String[join.getKey().getColumnSpan()];
 			int i = 0;
 			while ( iter.hasNext() ) {
 				Column col = (Column) iter.next();
 				keyCols[i++] = col.getQuotedName( factory.getDialect() );
 			}
 			joinKeyColumns.add( keyCols );
 		}
 
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray( isDeferreds );
 		subclassTableNameClosure = ArrayHelper.toStringArray( subclassTables );
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray( isLazies );
 		subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( joinKeyColumns );
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray( isConcretes );
 		isInverseSubclassTable = ArrayHelper.toBooleanArray( isInverses );
 		isNullableSubclassTable = ArrayHelper.toBooleanArray( isNullables );
 		hasSequentialSelects = hasDeferred;
 
 		// DISCRIMINATOR
 
 		if ( persistentClass.isPolymorphic() ) {
 			Value discrimValue = persistentClass.getDiscriminator();
 			if ( discrimValue == null ) {
 				throw new MappingException( "discriminator mapping required for single table polymorphic persistence" );
 			}
 			forceDiscriminator = persistentClass.isForceDiscriminator();
 			Selectable selectable = (Selectable) discrimValue.getColumnIterator().next();
 			if ( discrimValue.hasFormula() ) {
 				Formula formula = (Formula) selectable;
 				discriminatorFormula = formula.getFormula();
 				discriminatorFormulaTemplate = formula.getTemplate(
 						factory.getDialect(),
 						factory.getSqlFunctionRegistry()
 				);
 				discriminatorColumnName = null;
 				discriminatorColumnReaders = null;
 				discriminatorColumnReaderTemplate = null;
 				discriminatorAlias = "clazz_";
 			}
 			else {
 				Column column = (Column) selectable;
 				discriminatorColumnName = column.getQuotedName( factory.getDialect() );
 				discriminatorColumnReaders = column.getReadExpr( factory.getDialect() );
 				discriminatorColumnReaderTemplate = column.getTemplate(
 						factory.getDialect(),
 						factory.getSqlFunctionRegistry()
 				);
 				discriminatorAlias = column.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 				discriminatorFormula = null;
 				discriminatorFormulaTemplate = null;
 			}
 			discriminatorType = persistentClass.getDiscriminator().getType();
 			if ( persistentClass.isDiscriminatorValueNull() ) {
 				discriminatorValue = NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( persistentClass.isDiscriminatorValueNotNull() ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else {
 				discriminatorInsertable = persistentClass.isDiscriminatorInsertable() && !discrimValue.hasFormula();
 				try {
 					DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
 					discriminatorValue = dtype.stringToObject( persistentClass.getDiscriminatorValue() );
 					discriminatorSQLValue = dtype.objectToSQLString( discriminatorValue, factory.getDialect() );
 				}
 				catch (ClassCastException cce) {
 					throw new MappingException( "Illegal discriminator type: " + discriminatorType.getName() );
 				}
 				catch (Exception e) {
 					throw new MappingException( "Could not format discriminator value to SQL string", e );
 				}
 			}
 		}
 		else {
 			forceDiscriminator = false;
 			discriminatorInsertable = false;
 			discriminatorColumnName = null;
 			discriminatorColumnReaders = null;
 			discriminatorColumnReaderTemplate = null;
 			discriminatorAlias = null;
 			discriminatorType = null;
 			discriminatorValue = null;
 			discriminatorSQLValue = null;
 			discriminatorFormula = null;
 			discriminatorFormulaTemplate = null;
 		}
 
 		// PROPERTIES
 
 		propertyTableNumbers = new int[getPropertySpan()];
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			propertyTableNumbers[i++] = persistentClass.getJoinNumber( prop );
 
 		}
 
 		//TODO: code duplication with JoinedSubclassEntityPersister
 
 		ArrayList<Integer> columnJoinNumbers = new ArrayList<Integer>();
 		ArrayList<Integer> formulaJoinedNumbers = new ArrayList<Integer>();
 		ArrayList<Integer> propertyJoinNumbers = new ArrayList<Integer>();
 
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			Integer join = persistentClass.getJoinNumber( prop );
 			propertyJoinNumbers.add( join );
 
 			//propertyTableNumbersByName.put( prop.getName(), join );
 			propertyTableNumbersByNameAndSubclass.put(
 					prop.getPersistentClass().getEntityName() + '.' + prop.getName(),
 					join
 			);
 
 			Iterator citer = prop.getColumnIterator();
 			while ( citer.hasNext() ) {
 				Selectable thing = (Selectable) citer.next();
 				if ( thing.isFormula() ) {
 					formulaJoinedNumbers.add( join );
 				}
 				else {
 					columnJoinNumbers.add( join );
 				}
 			}
 		}
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray( columnJoinNumbers );
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray( formulaJoinedNumbers );
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray( propertyJoinNumbers );
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 		if ( persistentClass.isPolymorphic() ) {
 			addSubclassByDiscriminatorValue( discriminatorValue, getEntityName() );
 		}
 
 		// SUBCLASSES
 		if ( persistentClass.isPolymorphic() ) {
 			iter = persistentClass.getSubclassIterator();
 			int k = 1;
 			while ( iter.hasNext() ) {
 				Subclass sc = (Subclass) iter.next();
 				subclassClosure[k++] = sc.getEntityName();
 				if ( sc.isDiscriminatorValueNull() ) {
 					addSubclassByDiscriminatorValue( NULL_DISCRIMINATOR, sc.getEntityName() );
 				}
 				else if ( sc.isDiscriminatorValueNotNull() ) {
 					addSubclassByDiscriminatorValue( NOT_NULL_DISCRIMINATOR, sc.getEntityName() );
 				}
 				else {
 					try {
 						DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
 						addSubclassByDiscriminatorValue(
 								dtype.stringToObject( sc.getDiscriminatorValue() ),
 								sc.getEntityName()
 						);
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException( "Illegal discriminator type: " + discriminatorType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException( "Error parsing discriminator value", e );
 					}
 				}
 			}
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap( persistentClass );
 
 		postConstruct( creationContext.getMetadata() );
 
 	}
 
 	private void addSubclassByDiscriminatorValue(Object discriminatorValue, String entityName) {
 		String mappedEntityName = subclassesByDiscriminatorValue.put( discriminatorValue, entityName );
 		if ( mappedEntityName != null ) {
 			throw new MappingException(
 					"Entities [" + entityName + "] and [" + mappedEntityName
 							+ "] are mapped with the same discriminator value '" + discriminatorValue + "'."
 			);
 		}
 	}
 
 	protected boolean isInverseTable(int j) {
 		return isInverseTable[j];
 	}
 
 	protected boolean isInverseSubclassTable(int j) {
 		return isInverseSubclassTable[j];
 	}
 
 	public String getDiscriminatorColumnName() {
 		return discriminatorColumnName;
 	}
 
 	public String getDiscriminatorColumnReaders() {
 		return discriminatorColumnReaders;
 	}
 
 	public String getDiscriminatorColumnReaderTemplate() {
 		return discriminatorColumnReaderTemplate;
 	}
 
 	protected String getDiscriminatorAlias() {
 		return discriminatorAlias;
 	}
 
 	protected String getDiscriminatorFormulaTemplate() {
 		return discriminatorFormulaTemplate;
 	}
 
 	public String getTableName() {
 		return qualifiedTableNames[0];
 	}
 
 	public Type getDiscriminatorType() {
 		return discriminatorType;
 	}
 
 	public Object getDiscriminatorValue() {
 		return discriminatorValue;
 	}
 
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLValue;
 	}
 
 	public String[] getSubclassClosure() {
 		return subclassClosure;
 	}
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		if ( value == null ) {
 			return subclassesByDiscriminatorValue.get( NULL_DISCRIMINATOR );
 		}
 		else {
 			String result = subclassesByDiscriminatorValue.get( value );
 			if ( result == null ) {
 				result = subclassesByDiscriminatorValue.get( NOT_NULL_DISCRIMINATOR );
 			}
 			return result;
 		}
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces;
 	}
 
 	//Access cached SQL
 
 	protected boolean isDiscriminatorFormula() {
 		return discriminatorColumnName == null;
 	}
 
 	protected String getDiscriminatorFormula() {
 		return discriminatorFormula;
 	}
 
 	protected String getTableName(int j) {
 		return qualifiedTableNames[j];
 	}
 
 	protected String[] getKeyColumns(int j) {
 		return keyColumnNames[j];
 	}
 
 	protected boolean isTableCascadeDeleteEnabled(int j) {
 		return cascadeDeleteEnabled[j];
 	}
 
 	protected boolean isPropertyOfTable(int property, int j) {
 		return propertyTableNumbers[property] == j;
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
 	}
 
 	// Execute the SQL:
 
 	public String fromTableFragment(String name) {
 		return getTableName() + ' ' + name;
 	}
 
 	@Override
 	public String filterFragment(String alias) throws MappingException {
 		String result = discriminatorFilterFragment( alias );
 		if ( hasWhere() ) {
 			result += " and " + getSQLWhereString( alias );
 		}
 		return result;
 	}
 
 	private String discriminatorFilterFragment(String alias) throws MappingException {
 		return discriminatorFilterFragment( alias, null );
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return forceDiscriminator
 				? discriminatorFilterFragment( alias, null )
 				: "";
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
 		return needsDiscriminator()
 				? discriminatorFilterFragment( alias, treatAsDeclarations )
 				: "";
 	}
 
 	@Override
 	public String filterFragment(String alias, Set<String> treatAsDeclarations) {
 		String result = discriminatorFilterFragment( alias, treatAsDeclarations );
 		if ( hasWhere() ) {
 			result += " and " + getSQLWhereString( alias );
 		}
 		return result;
 	}
 
 	private String discriminatorFilterFragment(String alias, Set<String> treatAsDeclarations) {
 		final boolean hasTreatAs = treatAsDeclarations != null && !treatAsDeclarations.isEmpty();
 
 		if ( !needsDiscriminator() && !hasTreatAs ) {
 			return "";
 		}
 
 		final InFragment frag = new InFragment();
 		if ( isDiscriminatorFormula() ) {
 			frag.setFormula( alias, getDiscriminatorFormulaTemplate() );
 		}
 		else {
 			frag.setColumn( alias, getDiscriminatorColumnName() );
 		}
 
 		if ( hasTreatAs ) {
 			frag.addValues( decodeTreatAsRequests( treatAsDeclarations ) );
 		}
 		else {
 			frag.addValues( fullDiscriminatorValues() );
 		}
 
 		return " and " + frag.toFragmentString();
 	}
 
 	private boolean needsDiscriminator() {
 		return forceDiscriminator || isInherited();
 	}
 
 	private String[] decodeTreatAsRequests(Set<String> treatAsDeclarations) {
 		final List<String> values = new ArrayList<String>();
 		for ( String subclass : treatAsDeclarations ) {
 			final Queryable queryable = (Queryable) getFactory().getEntityPersister( subclass );
 			if ( !queryable.isAbstract() ) {
 				values.add( queryable.getDiscriminatorSQLValue() );
 			}
+			else if ( queryable.hasSubclasses() ) {
+				// if the treat is an abstract class, add the concrete implementations to values if any
+				Set<String> actualSubClasses = queryable.getEntityMetamodel().getSubclassEntityNames();
+
+				for ( String actualSubClass : actualSubClasses ) {
+					if ( actualSubClass.equals( subclass ) ) {
+						continue;
+					}
+
+					Queryable actualQueryable = (Queryable) getFactory().getEntityPersister( actualSubClass );
+					if ( !actualQueryable.hasSubclasses() ) {
+						values.add( actualQueryable.getDiscriminatorSQLValue() );
+					}
+				}
+			}
 		}
 		return values.toArray( new String[values.size()] );
 	}
 
 	private String[] fullDiscriminatorValues;
 
 	private String[] fullDiscriminatorValues() {
 		if ( fullDiscriminatorValues == null ) {
 			// first access; build it
 			final List<String> values = new ArrayList<String>();
 			for ( String subclass : getSubclassClosure() ) {
 				final Queryable queryable = (Queryable) getFactory().getEntityPersister( subclass );
 				if ( !queryable.isAbstract() ) {
 					values.add( queryable.getDiscriminatorSQLValue() );
 				}
 			}
 			fullDiscriminatorValues = values.toArray( new String[values.size()] );
 		}
 
 		return fullDiscriminatorValues;
 	}
 
 	public String getSubclassPropertyTableName(int i) {
 		return subclassTableNameClosure[subclassPropertyTableNumberClosure[i]];
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		if ( isDiscriminatorFormula() ) {
 			select.addFormula( name, getDiscriminatorFormulaTemplate(), getDiscriminatorAlias() );
 		}
 		else {
 			select.addColumn( name, getDiscriminatorColumnName(), getDiscriminatorAlias() );
 		}
 	}
 
 	protected int[] getPropertyTableNumbersInSelect() {
 		return propertyTableNumbers;
 	}
 
 	protected int getSubclassPropertyTableNumber(int i) {
 		return subclassPropertyTableNumberClosure[i];
 	}
 
 	public int getTableSpan() {
 		return joinSpan;
 	}
 
 	protected void addDiscriminatorToInsert(Insert insert) {
 
 		if ( discriminatorInsertable ) {
 			insert.addColumn( getDiscriminatorColumnName(), discriminatorSQLValue );
 		}
 
 	}
 
 	protected int[] getSubclassColumnTableNumberClosure() {
 		return subclassColumnTableNumberClosure;
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return subclassFormulaTableNumberClosure;
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return propertyTableNumbers;
 	}
 
 	protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
 		return hasSequentialSelects &&
 				isSubclassTableSequentialSelect( getSubclassPropertyTableNumber( propertyName, entityName ) );
 	}
 
 	public boolean hasSequentialSelect() {
 		return hasSequentialSelects;
 	}
 
 	private int getSubclassPropertyTableNumber(String propertyName, String entityName) {
 		Type type = propertyMapping.toType( propertyName );
 		if ( type.isAssociationType() && ( (AssociationType) type ).useLHSPrimaryKey() ) {
 			return 0;
 		}
 		final Integer tabnum = propertyTableNumbersByNameAndSubclass.get( entityName + '.' + propertyName );
 		return tabnum == null ? 0 : tabnum;
 	}
 
 	protected String getSequentialSelect(String entityName) {
 		return sequentialSelectStringsByEntityName.get( entityName );
 	}
 
 	private String generateSequentialSelect(Loadable persister) {
 		//if ( this==persister || !hasSequentialSelects ) return null;
 
 		//note that this method could easily be moved up to BasicEntityPersister,
 		//if we ever needed to reuse it from other subclasses
 
 		//figure out which tables need to be fetched
 		AbstractEntityPersister subclassPersister = (AbstractEntityPersister) persister;
 		HashSet<Integer> tableNumbers = new HashSet<Integer>();
 		String[] props = subclassPersister.getPropertyNames();
 		String[] classes = subclassPersister.getPropertySubclassNames();
 		for ( int i = 0; i < props.length; i++ ) {
 			int propTableNumber = getSubclassPropertyTableNumber( props[i], classes[i] );
 			if ( isSubclassTableSequentialSelect( propTableNumber ) && !isSubclassTableLazy( propTableNumber ) ) {
 				tableNumbers.add( propTableNumber );
 			}
 		}
 		if ( tableNumbers.isEmpty() ) {
 			return null;
 		}
 
 		//figure out which columns are needed
 		ArrayList<Integer> columnNumbers = new ArrayList<Integer>();
 		final int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		for ( int i = 0; i < getSubclassColumnClosure().length; i++ ) {
 			if ( tableNumbers.contains( columnTableNumbers[i] ) ) {
 				columnNumbers.add( i );
 			}
 		}
 
 		//figure out which formulas are needed
 		ArrayList<Integer> formulaNumbers = new ArrayList<Integer>();
 		final int[] formulaTableNumbers = getSubclassColumnTableNumberClosure();
 		for ( int i = 0; i < getSubclassFormulaTemplateClosure().length; i++ ) {
 			if ( tableNumbers.contains( formulaTableNumbers[i] ) ) {
 				formulaNumbers.add( i );
 			}
 		}
 
 		//render the SQL
 		return renderSelect(
 				ArrayHelper.toIntArray( tableNumbers ),
 				ArrayHelper.toIntArray( columnNumbers ),
 				ArrayHelper.toIntArray( formulaNumbers )
 		);
 	}
 
 
 	protected String[] getSubclassTableKeyColumns(int j) {
 		return subclassTableKeyColumnClosure[j];
 	}
 
 	public String getSubclassTableName(int j) {
 		return subclassTableNameClosure[j];
 	}
 
 	public int getSubclassTableSpan() {
 		return subclassTableNameClosure.length;
 	}
 
 	protected boolean isClassOrSuperclassTable(int j) {
 		return isClassOrSuperclassTable[j];
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return subclassTableIsLazyClosure[j];
 	}
 
 	protected boolean isNullableTable(int j) {
 		return isNullableTable[j];
 	}
 
 	protected boolean isNullableSubclassTable(int j) {
 		return isNullableSubclassTable[j];
 	}
 
 	public String getPropertyTableName(String propertyName) {
 		Integer index = getEntityMetamodel().getPropertyIndexOrNull( propertyName );
 		if ( index == null ) {
 			return null;
 		}
 		return qualifiedTableNames[propertyTableNumbers[index]];
 	}
 
 	protected void doPostInstantiate() {
 		if ( hasSequentialSelects ) {
 			String[] entityNames = getSubclassClosure();
 			for ( int i = 1; i < entityNames.length; i++ ) {
 				Loadable loadable = (Loadable) getFactory().getEntityPersister( entityNames[i] );
 				if ( !loadable.isAbstract() ) { //perhaps not really necessary...
 					String sequentialSelect = generateSequentialSelect( loadable );
 					sequentialSelectStringsByEntityName.put( entityNames[i], sequentialSelect );
 				}
 			}
 		}
 	}
 
 	public boolean isMultiTable() {
 		return getTableSpan() > 1;
 	}
 
 	public String[] getConstraintOrderedTableNameClosure() {
 		return constraintOrderedTableNames;
 	}
 
 	public String[][] getContraintOrderedTableKeyColumnClosure() {
 		return constraintOrderedKeyColumnNames;
 	}
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new DynamicFilterAliasGenerator( qualifiedTableNames, rootAlias );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jpa/ql/TreatKeywordTest.java b/hibernate-core/src/test/java/org/hibernate/test/jpa/ql/TreatKeywordTest.java
index 2dad5a8533..f82a5e5b1a 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jpa/ql/TreatKeywordTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jpa/ql/TreatKeywordTest.java
@@ -1,337 +1,394 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.jpa.ql;
 
 import javax.persistence.DiscriminatorColumn;
 import javax.persistence.DiscriminatorType;
 import javax.persistence.DiscriminatorValue;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
+import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Inheritance;
 import javax.persistence.InheritanceType;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
+import java.util.Arrays;
 import java.util.List;
 
 import org.hibernate.Session;
+import org.hibernate.Transaction;
 
 import org.junit.Test;
 
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Steve Ebersole
  */
 public class TreatKeywordTest extends BaseCoreFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] {
 				JoinedEntity.class, JoinedEntitySubclass.class, JoinedEntitySubSubclass.class,
 				JoinedEntitySubclass2.class, JoinedEntitySubSubclass2.class,
-				DiscriminatorEntity.class, DiscriminatorEntitySubclass.class, DiscriminatorEntitySubSubclass.class
+				DiscriminatorEntity.class, DiscriminatorEntitySubclass.class, DiscriminatorEntitySubSubclass.class,
+				Animal.class, Dog.class, Dachshund.class, Greyhound.class
 		};
 	}
 
 	@Test
 	public void testBasicUsageInJoin() {
 		// todo : assert invalid naming of non-subclasses in TREAT statement
 		Session s = openSession();
 
 		s.createQuery( "from DiscriminatorEntity e join treat(e.other as DiscriminatorEntitySubclass) o" ).list();
 		s.createQuery( "from DiscriminatorEntity e join treat(e.other as DiscriminatorEntitySubSubclass) o" ).list();
 		s.createQuery( "from DiscriminatorEntitySubclass e join treat(e.other as DiscriminatorEntitySubSubclass) o" ).list();
 
 		s.createQuery( "from JoinedEntity e join treat(e.other as JoinedEntitySubclass) o" ).list();
 		s.createQuery( "from JoinedEntity e join treat(e.other as JoinedEntitySubSubclass) o" ).list();
 		s.createQuery( "from JoinedEntitySubclass e join treat(e.other as JoinedEntitySubSubclass) o" ).list();
 
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-8637" )
 	public void testFilteringDiscriminatorSubclasses() {
 		Session s = openSession();
 		s.beginTransaction();
 		DiscriminatorEntity root = new DiscriminatorEntity( 1, "root" );
 		s.save( root );
 		DiscriminatorEntitySubclass child = new DiscriminatorEntitySubclass( 2, "child", root );
 		s.save( child );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 
 		// in select clause
 		List result = s.createQuery( "select e from DiscriminatorEntity e" ).list();
 		assertEquals( 2, result.size() );
 		result = s.createQuery( "select treat (e as DiscriminatorEntitySubclass) from DiscriminatorEntity e" ).list();
 		assertEquals( 1, result.size() );
 		result = s.createQuery( "select treat (e as DiscriminatorEntitySubSubclass) from DiscriminatorEntity e" ).list();
 		assertEquals( 0, result.size() );
 
 		// in join
 		result = s.createQuery( "from DiscriminatorEntity e inner join e.other" ).list();
 		assertEquals( 1, result.size() );
 		result = s.createQuery( "from DiscriminatorEntity e inner join treat (e.other as DiscriminatorEntitySubclass)" ).list();
 		assertEquals( 0, result.size() );
 		result = s.createQuery( "from DiscriminatorEntity e inner join treat (e.other as DiscriminatorEntitySubSubclass)" ).list();
 		assertEquals( 0, result.size() );
 
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( root );
 		s.delete( child );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-8637" )
 	public void testFilteringJoinedSubclasses() {
 		Session s = openSession();
 		s.beginTransaction();
 		JoinedEntity root = new JoinedEntity( 1, "root" );
 		s.save( root );
 		JoinedEntitySubclass child = new JoinedEntitySubclass( 2, "child", root );
 		s.save( child );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 
 		// in the select clause which causes an implicit inclusion of subclass joins, the test here makes sure that
 		// the TREAT-AS effects the join-type used.
 		List result = s.createQuery( "select e from JoinedEntity e" ).list();
 		assertEquals( 2, result.size() );
 		result = s.createQuery( "select treat (e as JoinedEntitySubclass) from JoinedEntity e" ).list();
 		assertEquals( 1, result.size() );
 		result = s.createQuery( "select treat (e as JoinedEntitySubSubclass) from JoinedEntity e" ).list();
 		assertEquals( 0, result.size() );
 
 		// in join
 		result = s.createQuery( "from JoinedEntity e inner join e.other" ).list();
 		assertEquals( 1, result.size() );
 		result = s.createQuery( "from JoinedEntity e inner join treat (e.other as JoinedEntitySubclass)" ).list();
 		assertEquals( 0, result.size() );
 		result = s.createQuery( "from JoinedEntity e inner join treat (e.other as JoinedEntitySubSubclass)" ).list();
 		assertEquals( 0, result.size() );
 
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( child );
 		s.delete( root );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9862" )
 	@FailureExpected( jiraKey = "HHH-9862" )
 	public void testRestrictionsOnJoinedSubclasses() {
 		Session s = openSession();
 		s.beginTransaction();
 		JoinedEntity root = new JoinedEntity( 1, "root" );
 		s.save( root );
 		JoinedEntitySubclass child1 = new JoinedEntitySubclass( 2, "child1", root );
 		s.save( child1 );
 		JoinedEntitySubclass2 child2 = new JoinedEntitySubclass2( 3, "child2", root );
 		s.save( child2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 
 		List result = s.createQuery( "select e from JoinedEntity e where treat (e as JoinedEntitySubclass ).name = 'child1'" ).list();
 		assertEquals( 1, result.size() );
 		assertTrue( JoinedEntitySubclass.class.isInstance( result.get( 0 ) ) );
 
 		result = s.createQuery( "select e from JoinedEntity e where treat (e as JoinedEntitySubclass2 ).name = 'child1'" ).list();
 		assertEquals( 0, result.size() );
 
 		result = s.createQuery( "select e from JoinedEntity e where treat (e as JoinedEntitySubclass2 ).name = 'child2'" ).list();
 		assertEquals( 1, result.size() );
 		assertTrue( JoinedEntitySubclass2.class.isInstance( result.get( 0 ) ) );
 
 		result = s.createQuery( "select e from JoinedEntity e where treat (e as JoinedEntitySubclass ).name = 'child1' or treat (e as JoinedEntitySubclass2 ).name = 'child2'" ).list();
 		assertEquals( 2, result.size() );
 
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( child1 );
 		s.delete( child2 );
 		s.delete( root );
 		s.getTransaction().commit();
 		s.close();
 	}
 
+	@Test
+	@TestForIssue(jiraKey = "HHH-9411")
+	public void testTreatWithRestrictionOnAbstractClass() {
+		Session s = openSession();
+		Transaction tx = s.beginTransaction();
+
+		Greyhound greyhound = new Greyhound();
+		Dachshund dachshund = new Dachshund();
+		s.save( greyhound );
+		s.save( dachshund );
+
+		List results = s.createQuery( "select treat (a as Dog) from Animal a where a.fast = TRUE" ).list();
+
+		assertEquals( Arrays.asList( greyhound ), results );
+
+		tx.commit();
+		s.close();
+	}
+
 	@Entity( name = "JoinedEntity" )
 	@Table( name = "JoinedEntity" )
 	@Inheritance( strategy = InheritanceType.JOINED )
 	public static class JoinedEntity {
 		@Id
 		public Integer id;
 		public String name;
 		@ManyToOne( fetch = FetchType.LAZY )
 		public JoinedEntity other;
 
 		public JoinedEntity() {
 		}
 
 		public JoinedEntity(Integer id, String name) {
 			this.id = id;
 			this.name = name;
 		}
 
 		public JoinedEntity(Integer id, String name, JoinedEntity other) {
 			this.id = id;
 			this.name = name;
 			this.other = other;
 		}
 	}
 
 	@Entity( name = "JoinedEntitySubclass" )
 	@Table( name = "JoinedEntitySubclass" )
 	public static class JoinedEntitySubclass extends JoinedEntity {
 		public JoinedEntitySubclass() {
 		}
 
 		public JoinedEntitySubclass(Integer id, String name) {
 			super( id, name );
 		}
 
 		public JoinedEntitySubclass(Integer id, String name, JoinedEntity other) {
 			super( id, name, other );
 		}
 	}
 
 	@Entity( name = "JoinedEntitySubSubclass" )
 	@Table( name = "JoinedEntitySubSubclass" )
 	public static class JoinedEntitySubSubclass extends JoinedEntitySubclass {
 		public JoinedEntitySubSubclass() {
 		}
 
 		public JoinedEntitySubSubclass(Integer id, String name) {
 			super( id, name );
 		}
 
 		public JoinedEntitySubSubclass(Integer id, String name, JoinedEntity other) {
 			super( id, name, other );
 		}
 	}
 
 	@Entity( name = "JoinedEntitySubclass2" )
 	@Table( name = "JoinedEntitySubclass2" )
 	public static class JoinedEntitySubclass2 extends JoinedEntity {
 		public JoinedEntitySubclass2() {
 		}
 
 		public JoinedEntitySubclass2(Integer id, String name) {
 			super( id, name );
 		}
 
 		public JoinedEntitySubclass2(Integer id, String name, JoinedEntity other) {
 			super( id, name, other );
 		}
 	}
 
 	@Entity( name = "JoinedEntitySubSubclass2" )
 	@Table( name = "JoinedEntitySubSubclass2" )
 	public static class JoinedEntitySubSubclass2 extends JoinedEntitySubclass2 {
 		public JoinedEntitySubSubclass2() {
 		}
 
 		public JoinedEntitySubSubclass2(Integer id, String name) {
 			super( id, name );
 		}
 
 		public JoinedEntitySubSubclass2(Integer id, String name, JoinedEntity other) {
 			super( id, name, other );
 		}
 	}
 
 	@Entity( name = "DiscriminatorEntity" )
 	@Table( name = "DiscriminatorEntity" )
 	@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
 	@DiscriminatorColumn( name = "e_type", discriminatorType = DiscriminatorType.STRING )
 	@DiscriminatorValue( "B" )
 	public static class DiscriminatorEntity {
 		@Id
 		public Integer id;
 		public String name;
 		@ManyToOne( fetch = FetchType.LAZY )
 		public DiscriminatorEntity other;
 
 		public DiscriminatorEntity() {
 		}
 
 		public DiscriminatorEntity(Integer id, String name) {
 			this.id = id;
 			this.name = name;
 		}
 
 		public DiscriminatorEntity(
 				Integer id,
 				String name,
 				DiscriminatorEntity other) {
 			this.id = id;
 			this.name = name;
 			this.other = other;
 		}
 	}
 
 	@Entity( name = "DiscriminatorEntitySubclass" )
 	@DiscriminatorValue( "S" )
 	public static class DiscriminatorEntitySubclass extends DiscriminatorEntity {
 		public DiscriminatorEntitySubclass() {
 		}
 
 		public DiscriminatorEntitySubclass(Integer id, String name) {
 			super( id, name );
 		}
 
 		public DiscriminatorEntitySubclass(
 				Integer id,
 				String name,
 				DiscriminatorEntity other) {
 			super( id, name, other );
 		}
 	}
 
 	@Entity( name = "DiscriminatorEntitySubSubclass" )
 	@DiscriminatorValue( "SS" )
 	public static class DiscriminatorEntitySubSubclass extends DiscriminatorEntitySubclass {
 		public DiscriminatorEntitySubSubclass() {
 		}
 
 		public DiscriminatorEntitySubSubclass(Integer id, String name) {
 			super( id, name );
 		}
 
 		public DiscriminatorEntitySubSubclass(
 				Integer id,
 				String name,
 				DiscriminatorEntity other) {
 			super( id, name, other );
 		}
 	}
+
+	@Entity(name = "Animal")
+	public static abstract class Animal {
+		@Id
+		@GeneratedValue
+		private Long id;
+	}
+
+	@Entity(name = "Dog")
+	public static abstract class Dog extends Animal {
+		private boolean fast;
+
+		protected Dog(boolean fast) {
+			this.fast = fast;
+		}
+
+		public final boolean isFast() {
+			return fast;
+		}
+	}
+
+	@Entity(name = "Dachshund")
+	public static class Dachshund extends Dog {
+		public Dachshund() {
+			super( false );
+		}
+	}
+
+	@Entity(name = "Greyhound")
+	public static class Greyhound extends Dog {
+		public Greyhound() {
+			super( true );
+		}
+	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/TreatKeywordTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/TreatKeywordTest.java
index aa4c6637b0..4701f51c14 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/TreatKeywordTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/TreatKeywordTest.java
@@ -1,216 +1,290 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.jpa.test.criteria;
 
+import java.util.Arrays;
 import java.util.List;
+import javax.persistence.Entity;
 import javax.persistence.EntityManager;
+import javax.persistence.EntityTransaction;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.criteria.CriteriaQuery;
 import javax.persistence.criteria.Root;
 import javax.persistence.metamodel.EntityType;
 
 import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
 import org.hibernate.jpa.test.metamodel.Thing;
 import org.hibernate.jpa.test.metamodel.ThingWithQuantity;
 import org.hibernate.jpa.test.metamodel.ThingWithQuantity_;
 
 import org.hibernate.testing.TestForIssue;
 import org.junit.Assert;
 import org.junit.Test;
 
+import static org.junit.Assert.assertEquals;
+
 /**
  * @author Steve Ebersole
  */
 public class TreatKeywordTest extends BaseEntityManagerFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
-		return new Class[] { Animal.class, Elephant.class, Human.class, Thing.class, ThingWithQuantity.class };
+		return new Class[] {
+				Animal.class, Elephant.class, Human.class, Thing.class, ThingWithQuantity.class,
+				TreatAnimal.class, Dog.class, Dachshund.class, Greyhound.class
+		};
 	}
 
 	@Test
 	public void basicTest() {
 		EntityManager em = getOrCreateEntityManager();
 		CriteriaBuilder builder = em.getCriteriaBuilder();
 		CriteriaQuery<Thing> criteria = builder.createQuery( Thing.class );
 		Root<Thing> root = criteria.from( Thing.class );
 		criteria.select( root );
 		criteria.where(
 				builder.equal(
 						builder.treat( root, ThingWithQuantity.class ).get( ThingWithQuantity_.quantity ),
 						2
 				)
 		);
 		em.createQuery( criteria ).getResultList();
 		em.close();
 	}
 
 	@Test
 	public void basicTest2() {
 		EntityManager em = getOrCreateEntityManager();
 		CriteriaBuilder builder = em.getCriteriaBuilder();
 		CriteriaQuery<Animal> criteria = builder.createQuery( Animal.class );
 		Root<Animal> root = criteria.from( Animal.class );
 		criteria.select( root );
 		criteria.where(
 				builder.equal(
 						builder.treat( root, Human.class ).get( "name" ),
 						"2"
 				)
 		);
 		em.createQuery( criteria ).getResultList();
 		em.close();
 	}
 
 	@Test
 	public void treatPathClassTest() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Animal animal = new Animal();
 		animal.setId(100L);
 		animal.setName("2");
 		em.persist(animal);
 		Human human = new Human();
 		human.setId(200L);
 		human.setName("2");
 		em.persist(human);
 		Elephant elephant = new Elephant();
 		elephant.setId( 300L );
 		elephant.setName( "2" );
 		em.persist( elephant );
 		em.getTransaction().commit();
 
 		CriteriaBuilder builder = em.getCriteriaBuilder();
 		CriteriaQuery<String> criteria = builder.createQuery( String.class );
 		Root<Animal> root = criteria.from( Animal.class );
 		EntityType<Animal> Animal_ = em.getMetamodel().entity(Animal.class);
 		criteria.select(root.get(Animal_.getSingularAttribute("name", String.class)));
 
 		criteria.where(builder.like(builder.treat(root, Human.class).get( org.hibernate.jpa.test.criteria.Human_.name ), "2%"));
 		List<String> animalList = em.createQuery( criteria ).getResultList();
 		Assert.assertEquals("treat(Animal as Human) was ignored",1, animalList.size());
 
 		CriteriaQuery<Long> idCriteria = builder.createQuery( Long.class );
 		Root<Animal> idRoot = idCriteria.from( Animal.class );
 		idCriteria.select( idRoot.get( Animal_.getSingularAttribute( "id", Long.class ) ) );
 
 		idCriteria.where(
 				builder.like(
 						builder.treat( idRoot, Human.class )
 								.get( org.hibernate.jpa.test.criteria.Human_.name ), "2%"
 				)
 		);
 		List<Long> animalIdList = em.createQuery( idCriteria ).getResultList();
 		Assert.assertEquals( "treat(Animal as Human) was ignored", 1, animalIdList.size() );
 		Assert.assertEquals( 200L, animalIdList.get( 0 ).longValue() );
 
 		em.close();
 	}
 
 	@Test
 	public void treatPathClassTestHqlControl() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Animal animal = new Animal();
 		animal.setId(100L);
 		animal.setName("2");
 		em.persist( animal );
 		Human human = new Human();
 		human.setId(200L);
 		human.setName("2");
 		em.persist(human);
 		Elephant elephant = new Elephant();
 		elephant.setId( 300L );
 		elephant.setName( "2" );
 		em.persist( elephant );
 		em.getTransaction().commit();
 
 		List<String> animalList = em.createQuery( "select a.name from Animal a where treat (a as Human).name like '2%'" ).getResultList();
 		Assert.assertEquals( "treat(Animal as Human) was ignored", 1, animalList.size() );
 
 		List<Long> animalIdList = em.createQuery( "select a.id from Animal a where treat (a as Human).name like '2%'" ).getResultList();
 		Assert.assertEquals("treat(Animal as Human) was ignored",1, animalList.size());
 		Assert.assertEquals( 200L, animalIdList.get( 0 ).longValue() );
 
 		em.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9549" )
 	public void treatRoot() {
 		EntityManager em = getOrCreateEntityManager();
 
 		em.getTransaction().begin();
 		Animal animal = new Animal();
 		animal.setId(100L);
 		animal.setName("2");
 		em.persist(animal);
 		Human human = new Human();
 		human.setId(200L);
 		human.setName("2");
 		em.persist(human);
 		Elephant elephant = new Elephant();
 		elephant.setId( 300L );
 		elephant.setName( "2" );
 		em.persist( elephant );
 		em.getTransaction().commit();
 
 		CriteriaBuilder builder = em.getCriteriaBuilder();
 		CriteriaQuery<Human> criteria = builder.createQuery( Human.class );
 		Root<Animal> root = criteria.from( Animal.class );
 		criteria.select( builder.treat( root, Human.class ) );
 		List<Human> humans = em.createQuery( criteria ).getResultList();
 		Assert.assertEquals( 1, humans.size() );
 
 		em.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9549" )
 	public void treatRootReturnSuperclass() {
 		EntityManager em = getOrCreateEntityManager();
 
 		em.getTransaction().begin();
 		Animal animal = new Animal();
 		animal.setId(100L);
 		animal.setName("2");
 		em.persist(animal);
 		Human human = new Human();
 		human.setId(200L);
 		human.setName("2");
 		em.persist(human);
 		Elephant elephant = new Elephant();
 		elephant.setId( 300L );
 		elephant.setName( "2" );
 		em.persist( elephant );
 		em.getTransaction().commit();
 
 		CriteriaBuilder builder = em.getCriteriaBuilder();
 		CriteriaQuery<Animal> criteria = builder.createQuery( Animal.class );
 		Root<Animal> root = criteria.from( Animal.class );
 		criteria.select( builder.treat( root, Human.class ) );
 		List<Animal> animalsThatAreHuman = em.createQuery( criteria ).getResultList();
 		Assert.assertEquals( 1, animalsThatAreHuman.size() );
 		Assert.assertTrue( Human.class.isInstance( animalsThatAreHuman.get( 0 ) ) );
 
 		em.close();
 	}
 
 	@Test
 	public void testSelectSubclassPropertyFromDowncast() {
 		EntityManager em = getOrCreateEntityManager();
 		CriteriaBuilder builder = em.getCriteriaBuilder();
 		CriteriaQuery<Integer> criteria = builder.createQuery( Integer.class );
 		Root<Thing> root = criteria.from( Thing.class );
 		Root<ThingWithQuantity> subroot = builder.treat( root, ThingWithQuantity.class );
 		criteria.select( subroot.<Integer>get( "quantity" ) );
 		em.createQuery( criteria ).getResultList();
 		em.close();
 	}
 
 
+	@Test
+	@TestForIssue(jiraKey = "HHH-9411")
+	public void testTreatWithRestrictionOnAbstractClass() {
+		EntityManager em = getOrCreateEntityManager();
+		EntityTransaction entityTransaction = em.getTransaction();
+		entityTransaction.begin();
+
+		Greyhound greyhound = new Greyhound();
+		Dachshund dachshund = new Dachshund();
+		em.persist( greyhound );
+		em.persist( dachshund );
+
+		CriteriaBuilder cb = em.getCriteriaBuilder();
+		CriteriaQuery<TreatAnimal> criteriaQuery = cb.createQuery( TreatAnimal.class );
+
+		Root<TreatAnimal> animal = criteriaQuery.from( TreatAnimal.class );
+
+		Root<Dog> dog = cb.treat( animal, Dog.class );
+
+		// only fast dogs
+		criteriaQuery.where( cb.isTrue( dog.<Boolean>get( "fast" ) ) );
+
+		List<TreatAnimal> results = em.createQuery( criteriaQuery ).getResultList();
+
+		// we should only have a single Greyhound here, not slow long dogs!
+		assertEquals( Arrays.asList( greyhound ), results );
+
+		entityTransaction.commit();
+		em.close();
+	}
+
+	@Entity
+	public static abstract class TreatAnimal {
+		@Id
+		@GeneratedValue
+		private Long id;
+	}
+
+	@Entity
+	public static abstract class Dog extends TreatAnimal {
+		private boolean fast;
+
+		protected Dog(boolean fast) {
+			this.fast = fast;
+		}
+
+		public final boolean isFast() {
+			return fast;
+		}
+	}
+
+	@Entity
+	public static class Dachshund extends Dog {
+		public Dachshund() {
+			super( false );
+		}
+	}
+
+	@Entity
+	public static class Greyhound extends Dog {
+		public Greyhound() {
+			super( true );
+		}
+	}
 }
