diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
index e0540d435b..0e9270a5b9 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
@@ -1,837 +1,841 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * An <tt>EntityPersister</tt> implementing the normalized "table-per-subclass"
  * mapping strategy
  *
  * @author Gavin King
  */
 public class JoinedSubclassEntityPersister extends AbstractEntityPersister {
 
 	// the class hierarchy structure
 	private final int tableSpan;
 	private final String[] tableNames;
 	private final String[] naturalOrderTableNames;
 	private final String[][] tableKeyColumns;
 	private final String[][] tableKeyColumnReaders;
 	private final String[][] tableKeyColumnReaderTemplates;
 	private final String[][] naturalOrderTableKeyColumns;
 	private final String[][] naturalOrderTableKeyColumnReaders;
 	private final String[][] naturalOrderTableKeyColumnReaderTemplates;
 	private final boolean[] naturalOrderCascadeDeleteEnabled;
 
 	private final String[] spaces;
 
 	private final String[] subclassClosure;
 
 	private final String[] subclassTableNameClosure;
 	private final String[][] subclassTableKeyColumnClosure;
 	private final boolean[] isClassOrSuperclassTable;
 
 	// properties of this class, including inherited properties
 	private final int[] naturalOrderPropertyTableNumbers;
 	private final int[] propertyTableNumbers;
 
 	// the closure of all properties in the entire hierarchy including
 	// subclasses and superclasses of this class
 	private final int[] subclassPropertyTableNumberClosure;
 
 	// the closure of all columns used by the entire hierarchy including
 	// subclasses and superclasses of this class
 	private final int[] subclassColumnTableNumberClosure;
 	private final int[] subclassFormulaTableNumberClosure;
 
 	private final boolean[] subclassTableSequentialSelect;
 	private final boolean[] subclassTableIsLazyClosure;
 
 	// subclass discrimination works by assigning particular
 	// values to certain combinations of null primary key
 	// values in the outer join using an SQL CASE
 	private final Map subclassesByDiscriminatorValue = new HashMap();
 	private final String[] discriminatorValues;
 	private final String[] notNullColumnNames;
 	private final int[] notNullColumnTableNumbers;
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
+	private final Object discriminatorValue;
 	private final String discriminatorSQLString;
 
 	// Span of the tables directly mapped by this entity and super-classes, if any
 	private final int coreTableSpan;
 	// only contains values for SecondaryTables, ie. not tables part of the "coreTableSpan"
 	private final boolean[] isNullableTable;
 
 	//INITIALIZATION:
 
 	public JoinedSubclassEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, factory );
 
 		// DISCRIMINATOR
 
-		final Object discriminatorValue;
 		if ( persistentClass.isPolymorphic() ) {
 			try {
 				discriminatorValue = new Integer( persistentClass.getSubclassId() );
 				discriminatorSQLString = discriminatorValue.toString();
 			}
 			catch ( Exception e ) {
 				throw new MappingException( "Could not format discriminator value to SQL string", e );
 			}
 		}
 		else {
 			discriminatorValue = null;
 			discriminatorSQLString = null;
 		}
 
 		if ( optimisticLockStyle() == OptimisticLockStyle.ALL || optimisticLockStyle() == OptimisticLockStyle.DIRTY ) {
 			throw new MappingException( "optimistic-lock=all|dirty not supported for joined-subclass mappings [" + getEntityName() + "]" );
 		}
 
 		//MULTITABLES
 
 		final int idColumnSpan = getIdentifierColumnSpan();
 
 		ArrayList tables = new ArrayList();
 		ArrayList keyColumns = new ArrayList();
 		ArrayList keyColumnReaders = new ArrayList();
 		ArrayList keyColumnReaderTemplates = new ArrayList();
 		ArrayList cascadeDeletes = new ArrayList();
 		Iterator titer = persistentClass.getTableClosureIterator();
 		Iterator kiter = persistentClass.getKeyClosureIterator();
 		while ( titer.hasNext() ) {
 			Table tab = (Table) titer.next();
 			KeyValue key = (KeyValue) kiter.next();
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			tables.add( tabname );
 			String[] keyCols = new String[idColumnSpan];
 			String[] keyColReaders = new String[idColumnSpan];
 			String[] keyColReaderTemplates = new String[idColumnSpan];
 			Iterator citer = key.getColumnIterator();
 			for ( int k = 0; k < idColumnSpan; k++ ) {
 				Column column = (Column) citer.next();
 				keyCols[k] = column.getQuotedName( factory.getDialect() );
 				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
 				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			}
 			keyColumns.add( keyCols );
 			keyColumnReaders.add( keyColReaders );
 			keyColumnReaderTemplates.add( keyColReaderTemplates );
 			cascadeDeletes.add( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() );
 		}
 
 		//Span of the tables directly mapped by this entity and super-classes, if any
 		coreTableSpan = tables.size();
 
 		isNullableTable = new boolean[persistentClass.getJoinClosureSpan()];
 
 		int tableIndex = 0;
 		Iterator joinIter = persistentClass.getJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 
 			isNullableTable[tableIndex++] = join.isOptional();
 
 			Table table = join.getTable();
 
 			String tableName = table.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			tables.add( tableName );
 
 			KeyValue key = join.getKey();
 			int joinIdColumnSpan = key.getColumnSpan();
 
 			String[] keyCols = new String[joinIdColumnSpan];
 			String[] keyColReaders = new String[joinIdColumnSpan];
 			String[] keyColReaderTemplates = new String[joinIdColumnSpan];
 
 			Iterator citer = key.getColumnIterator();
 
 			for ( int k = 0; k < joinIdColumnSpan; k++ ) {
 				Column column = (Column) citer.next();
 				keyCols[k] = column.getQuotedName( factory.getDialect() );
 				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
 				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			}
 			keyColumns.add( keyCols );
 			keyColumnReaders.add( keyColReaders );
 			keyColumnReaderTemplates.add( keyColReaderTemplates );
 			cascadeDeletes.add( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() );
 		}
 
 		naturalOrderTableNames = ArrayHelper.toStringArray( tables );
 		naturalOrderTableKeyColumns = ArrayHelper.to2DStringArray( keyColumns );
 		naturalOrderTableKeyColumnReaders = ArrayHelper.to2DStringArray( keyColumnReaders );
 		naturalOrderTableKeyColumnReaderTemplates = ArrayHelper.to2DStringArray( keyColumnReaderTemplates );
 		naturalOrderCascadeDeleteEnabled = ArrayHelper.toBooleanArray( cascadeDeletes );
 
 		ArrayList subtables = new ArrayList();
 		ArrayList isConcretes = new ArrayList();
 		ArrayList isDeferreds = new ArrayList();
 		ArrayList isLazies = new ArrayList();
 
 		keyColumns = new ArrayList();
 		titer = persistentClass.getSubclassTableClosureIterator();
 		while ( titer.hasNext() ) {
 			Table tab = (Table) titer.next();
 			isConcretes.add( persistentClass.isClassOrSuperclassTable( tab ) );
 			isDeferreds.add( Boolean.FALSE );
 			isLazies.add( Boolean.FALSE );
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			subtables.add( tabname );
 			String[] key = new String[idColumnSpan];
 			Iterator citer = tab.getPrimaryKey().getColumnIterator();
 			for ( int k = 0; k < idColumnSpan; k++ ) {
 				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
 			}
 			keyColumns.add( key );
 		}
 
 		//Add joins
 		joinIter = persistentClass.getSubclassJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 
 			Table tab = join.getTable();
 
 			isConcretes.add( persistentClass.isClassOrSuperclassTable( tab ) );
 			isDeferreds.add( join.isSequentialSelect() );
 			isLazies.add( join.isLazy() );
 
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			subtables.add( tabname );
 			String[] key = new String[idColumnSpan];
 			Iterator citer = tab.getPrimaryKey().getColumnIterator();
 			for ( int k = 0; k < idColumnSpan; k++ ) {
 				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
 			}
 			keyColumns.add( key );
 		}
 
 		String[] naturalOrderSubclassTableNameClosure = ArrayHelper.toStringArray( subtables );
 		String[][] naturalOrderSubclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( keyColumns );
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray( isConcretes );
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray( isDeferreds );
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray( isLazies );
 
 		constraintOrderedTableNames = new String[naturalOrderSubclassTableNameClosure.length];
 		constraintOrderedKeyColumnNames = new String[naturalOrderSubclassTableNameClosure.length][];
 		int currentPosition = 0;
 		for ( int i = naturalOrderSubclassTableNameClosure.length - 1; i >= 0; i--, currentPosition++ ) {
 			constraintOrderedTableNames[currentPosition] = naturalOrderSubclassTableNameClosure[i];
 			constraintOrderedKeyColumnNames[currentPosition] = naturalOrderSubclassTableKeyColumnClosure[i];
 		}
 
 		/**
 		 * Suppose an entity Client extends Person, mapped to the tables CLIENT and PERSON respectively.
 		 * For the Client entity:
 		 * naturalOrderTableNames -> PERSON, CLIENT; this reflects the sequence in which the tables are 
 		 * added to the meta-data when the annotated entities are processed.
 		 * However, in some instances, for example when generating joins, the CLIENT table needs to be 
 		 * the first table as it will the driving table.
 		 * tableNames -> CLIENT, PERSON
 		 */
 
 		tableSpan = naturalOrderTableNames.length;
 		tableNames = reverse( naturalOrderTableNames, coreTableSpan );
 		tableKeyColumns = reverse( naturalOrderTableKeyColumns, coreTableSpan );
 		tableKeyColumnReaders = reverse( naturalOrderTableKeyColumnReaders, coreTableSpan );
 		tableKeyColumnReaderTemplates = reverse( naturalOrderTableKeyColumnReaderTemplates, coreTableSpan );
 		subclassTableNameClosure = reverse( naturalOrderSubclassTableNameClosure, coreTableSpan );
 		subclassTableKeyColumnClosure = reverse( naturalOrderSubclassTableKeyColumnClosure, coreTableSpan );
 
 		spaces = ArrayHelper.join(
 				tableNames,
 				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
 		);
 
 		// Custom sql
 		customSQLInsert = new String[tableSpan];
 		customSQLUpdate = new String[tableSpan];
 		customSQLDelete = new String[tableSpan];
 		insertCallable = new boolean[tableSpan];
 		updateCallable = new boolean[tableSpan];
 		deleteCallable = new boolean[tableSpan];
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
 
 		PersistentClass pc = persistentClass;
 		int jk = coreTableSpan - 1;
 		while ( pc != null ) {
 			customSQLInsert[jk] = pc.getCustomSQLInsert();
 			insertCallable[jk] = customSQLInsert[jk] != null && pc.isCustomInsertCallable();
 			insertResultCheckStyles[jk] = pc.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault(
 					customSQLInsert[jk], insertCallable[jk]
 			)
 					: pc.getCustomSQLInsertCheckStyle();
 			customSQLUpdate[jk] = pc.getCustomSQLUpdate();
 			updateCallable[jk] = customSQLUpdate[jk] != null && pc.isCustomUpdateCallable();
 			updateResultCheckStyles[jk] = pc.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[jk], updateCallable[jk] )
 					: pc.getCustomSQLUpdateCheckStyle();
 			customSQLDelete[jk] = pc.getCustomSQLDelete();
 			deleteCallable[jk] = customSQLDelete[jk] != null && pc.isCustomDeleteCallable();
 			deleteResultCheckStyles[jk] = pc.getCustomSQLDeleteCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[jk], deleteCallable[jk] )
 					: pc.getCustomSQLDeleteCheckStyle();
 			jk--;
 			pc = pc.getSuperclass();
 		}
 
 		if ( jk != -1 ) {
 			throw new AssertionFailure( "Tablespan does not match height of joined-subclass hiearchy." );
 		}
 
 		joinIter = persistentClass.getJoinClosureIterator();
 		int j = coreTableSpan;
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 
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
 			j++;
 		}
 
 		// PROPERTIES
 		int hydrateSpan = getPropertySpan();
 		naturalOrderPropertyTableNumbers = new int[hydrateSpan];
 		propertyTableNumbers = new int[hydrateSpan];
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			String tabname = prop.getValue().getTable().getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			propertyTableNumbers[i] = getTableId( tabname, tableNames );
 			naturalOrderPropertyTableNumbers[i] = getTableId( tabname, naturalOrderTableNames );
 			i++;
 		}
 
 		// subclass closure properties
 
 		//TODO: code duplication with SingleTableEntityPersister
 
 		ArrayList columnTableNumbers = new ArrayList();
 		ArrayList formulaTableNumbers = new ArrayList();
 		ArrayList propTableNumbers = new ArrayList();
 
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			Table tab = prop.getValue().getTable();
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			Integer tabnum = new Integer( getTableId( tabname, subclassTableNameClosure ) );
 			propTableNumbers.add( tabnum );
 
 			Iterator citer = prop.getColumnIterator();
 			while ( citer.hasNext() ) {
 				Selectable thing = (Selectable) citer.next();
 				if ( thing.isFormula() ) {
 					formulaTableNumbers.add( tabnum );
 				}
 				else {
 					columnTableNumbers.add( tabnum );
 				}
 			}
 
 		}
 
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray( columnTableNumbers );
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray( propTableNumbers );
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray( formulaTableNumbers );
 
 		// SUBCLASSES
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[subclassSpan - 1] = getEntityName();
 		if ( persistentClass.isPolymorphic() ) {
 			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
 			discriminatorValues = new String[subclassSpan];
 			discriminatorValues[subclassSpan - 1] = discriminatorSQLString;
 			notNullColumnTableNumbers = new int[subclassSpan];
 			final int id = getTableId(
 					persistentClass.getTable().getQualifiedName(
 							factory.getDialect(),
 							factory.getSettings().getDefaultCatalogName(),
 							factory.getSettings().getDefaultSchemaName()
 					),
 					subclassTableNameClosure
 			);
 			notNullColumnTableNumbers[subclassSpan - 1] = id;
 			notNullColumnNames = new String[subclassSpan];
 			notNullColumnNames[subclassSpan - 1] = subclassTableKeyColumnClosure[id][0]; //( (Column) model.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
 		}
 		else {
 			discriminatorValues = null;
 			notNullColumnTableNumbers = null;
 			notNullColumnNames = null;
 		}
 
 		iter = persistentClass.getSubclassIterator();
 		int k = 0;
 		while ( iter.hasNext() ) {
 			Subclass sc = (Subclass) iter.next();
 			subclassClosure[k] = sc.getEntityName();
 			try {
 				if ( persistentClass.isPolymorphic() ) {
 					// we now use subclass ids that are consistent across all
 					// persisters for a class hierarchy, so that the use of
 					// "foo.class = Bar" works in HQL
 					Integer subclassId = new Integer( sc.getSubclassId() );//new Integer(k+1);
 					subclassesByDiscriminatorValue.put( subclassId, sc.getEntityName() );
 					discriminatorValues[k] = subclassId.toString();
 					int id = getTableId(
 							sc.getTable().getQualifiedName(
 									factory.getDialect(),
 									factory.getSettings().getDefaultCatalogName(),
 									factory.getSettings().getDefaultSchemaName()
 							),
 							subclassTableNameClosure
 					);
 					notNullColumnTableNumbers[k] = id;
 					notNullColumnNames[k] = subclassTableKeyColumnClosure[id][0]; //( (Column) sc.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
 				}
 			}
 			catch ( Exception e ) {
 				throw new MappingException( "Error parsing discriminator value", e );
 			}
 			k++;
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap( persistentClass );
 
 		postConstruct( mapping );
 
 	}
 
 	public JoinedSubclassEntityPersister(
 			final EntityBinding entityBinding,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 		super( entityBinding, cacheAccessStrategy, factory );
 		// TODO: implement!!! initializing final fields to null to make compiler happy
 		tableSpan = -1;
 		tableNames = null;
 		naturalOrderTableNames = null;
 		tableKeyColumns = null;
 		tableKeyColumnReaders = null;
 		tableKeyColumnReaderTemplates = null;
 		naturalOrderTableKeyColumns = null;
 		naturalOrderTableKeyColumnReaders = null;
 		naturalOrderTableKeyColumnReaderTemplates = null;
 		naturalOrderCascadeDeleteEnabled = null;
 		spaces = null;
 		subclassClosure = null;
 		subclassTableNameClosure = null;
 		subclassTableKeyColumnClosure = null;
 		isClassOrSuperclassTable = null;
 		naturalOrderPropertyTableNumbers = null;
 		propertyTableNumbers = null;
 		subclassPropertyTableNumberClosure = null;
 		subclassColumnTableNumberClosure = null;
 		subclassFormulaTableNumberClosure = null;
 		subclassTableSequentialSelect = null;
 		subclassTableIsLazyClosure = null;
 		discriminatorValues = null;
 		notNullColumnNames = null;
 		notNullColumnTableNumbers = null;
 		constraintOrderedTableNames = null;
 		constraintOrderedKeyColumnNames = null;
+		discriminatorValue = null;
 		discriminatorSQLString = null;
 		coreTableSpan = -1;
 		isNullableTable = null;
 	}
 
 	protected boolean isNullableTable(int j) {
 		if ( j < coreTableSpan ) {
 			return false;
 		}
 		return isNullableTable[j - coreTableSpan];
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
 	}
 
 	/*public void postInstantiate() throws MappingException {
 		super.postInstantiate();
 		//TODO: other lock modes?
 		loader = createEntityLoader(LockMode.NONE, CollectionHelper.EMPTY_MAP);
 	}*/
 
 	public String getSubclassPropertyTableName(int i) {
 		return subclassTableNameClosure[subclassPropertyTableNumberClosure[i]];
 	}
 
 	public Type getDiscriminatorType() {
 		return StandardBasicTypes.INTEGER;
 	}
 
+	public Object getDiscriminatorValue() {
+		return discriminatorValue;
+	}
+
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLString;
 	}
 
-
 	public String getSubclassForDiscriminatorValue(Object value) {
 		return (String) subclassesByDiscriminatorValue.get( value );
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces; // don't need subclass tables, because they can't appear in conditions
 	}
 
 
 	protected String getTableName(int j) {
 		return naturalOrderTableNames[j];
 	}
 
 	protected String[] getKeyColumns(int j) {
 		return naturalOrderTableKeyColumns[j];
 	}
 
 	protected boolean isTableCascadeDeleteEnabled(int j) {
 		return naturalOrderCascadeDeleteEnabled[j];
 	}
 
 	protected boolean isPropertyOfTable(int property, int j) {
 		return naturalOrderPropertyTableNumbers[property] == j;
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	/*public Object load(Serializable id,	Object optionalObject, LockMode lockMode, SessionImplementor session)
 	throws HibernateException {
 
 		if ( log.isTraceEnabled() ) log.trace( "Materializing entity: " + MessageHelper.infoString(this, id) );
 
 		final UniqueEntityLoader loader = hasQueryLoader() ?
 				getQueryLoader() :
 				this.loader;
 		try {
 
 			final Object result = loader.load(id, optionalObject, session);
 
 			if (result!=null) lock(id, getVersion(result), result, lockMode, session);
 
 			return result;
 
 		}
 		catch (SQLException sqle) {
 			throw new JDBCException( "could not load by id: " +  MessageHelper.infoString(this, id), sqle );
 		}
 	}*/
 	private static final void reverse(Object[] objects, int len) {
 		Object[] temp = new Object[len];
 		for ( int i = 0; i < len; i++ ) {
 			temp[i] = objects[len - i - 1];
 		}
 		for ( int i = 0; i < len; i++ ) {
 			objects[i] = temp[i];
 		}
 	}
 
 
 	/**
 	 * Reverse the first n elements of the incoming array
 	 *
 	 * @param objects
 	 * @param n
 	 *
 	 * @return New array with the first n elements in reversed order
 	 */
 	private static String[] reverse(String[] objects, int n) {
 
 		int size = objects.length;
 		String[] temp = new String[size];
 
 		for ( int i = 0; i < n; i++ ) {
 			temp[i] = objects[n - i - 1];
 		}
 
 		for ( int i = n; i < size; i++ ) {
 			temp[i] = objects[i];
 		}
 
 		return temp;
 	}
 
 	/**
 	 * Reverse the first n elements of the incoming array
 	 *
 	 * @param objects
 	 * @param n
 	 *
 	 * @return New array with the first n elements in reversed order
 	 */
 	private static String[][] reverse(String[][] objects, int n) {
 		int size = objects.length;
 		String[][] temp = new String[size][];
 		for ( int i = 0; i < n; i++ ) {
 			temp[i] = objects[n - i - 1];
 		}
 
 		for ( int i = n; i < size; i++ ) {
 			temp[i] = objects[i];
 		}
 
 		return temp;
 	}
 
 
 	public String fromTableFragment(String alias) {
 		return getTableName() + ' ' + alias;
 	}
 
 	public String getTableName() {
 		return tableNames[0];
 	}
 
 	private static int getTableId(String tableName, String[] tables) {
 		for ( int j = 0; j < tables.length; j++ ) {
 			if ( tableName.equals( tables[j] ) ) {
 				return j;
 			}
 		}
 		throw new AssertionFailure( "Table " + tableName + " not found" );
 	}
 
 	public void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		if ( hasSubclasses() ) {
 			select.setExtraSelectList( discriminatorFragment( name ), getDiscriminatorAlias() );
 		}
 	}
 
 	private CaseFragment discriminatorFragment(String alias) {
 		CaseFragment cases = getFactory().getDialect().createCaseFragment();
 
 		for ( int i = 0; i < discriminatorValues.length; i++ ) {
 			cases.addWhenColumnNotNull(
 					generateTableAlias( alias, notNullColumnTableNumbers[i] ),
 					notNullColumnNames[i],
 					discriminatorValues[i]
 			);
 		}
 
 		return cases;
 	}
 
 	public String filterFragment(String alias) {
 		return hasWhere() ?
 				" and " + getSQLWhereString( generateFilterConditionAlias( alias ) ) :
 				"";
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return generateTableAlias( rootAlias, tableSpan - 1 );
 	}
 
 	public String[] getIdentifierColumnNames() {
 		return tableKeyColumns[0];
 	}
 
 	public String[] getIdentifierColumnReaderTemplates() {
 		return tableKeyColumnReaderTemplates[0];
 	}
 
 	public String[] getIdentifierColumnReaders() {
 		return tableKeyColumnReaders[0];
 	}
 
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		if ( ENTITY_CLASS.equals( propertyName ) ) {
 			// This doesn't actually seem to work but it *might*
 			// work on some dbs. Also it doesn't work if there
 			// are multiple columns of results because it
 			// is not accounting for the suffix:
 			// return new String[] { getDiscriminatorColumnName() };
 
 			return new String[] { discriminatorFragment( alias ).toFragmentString() };
 		}
 		else {
 			return super.toColumns( alias, propertyName );
 		}
 	}
 
 	protected int[] getPropertyTableNumbersInSelect() {
 		return propertyTableNumbers;
 	}
 
 	protected int getSubclassPropertyTableNumber(int i) {
 		return subclassPropertyTableNumberClosure[i];
 	}
 
 	public int getTableSpan() {
 		return tableSpan;
 	}
 
 	public boolean isMultiTable() {
 		return true;
 	}
 
 	protected int[] getSubclassColumnTableNumberClosure() {
 		return subclassColumnTableNumberClosure;
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return subclassFormulaTableNumberClosure;
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return naturalOrderPropertyTableNumbers;
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
 
 	protected boolean isSubclassTableLazy(int j) {
 		return subclassTableIsLazyClosure[j];
 	}
 
 
 	protected boolean isClassOrSuperclassTable(int j) {
 		return isClassOrSuperclassTable[j];
 	}
 
 	public String getPropertyTableName(String propertyName) {
 		Integer index = getEntityMetamodel().getPropertyIndexOrNull( propertyName );
 		if ( index == null ) {
 			return null;
 		}
 		return tableNames[propertyTableNumbers[index.intValue()]];
 	}
 
 	public String[] getConstraintOrderedTableNameClosure() {
 		return constraintOrderedTableNames;
 	}
 
 	public String[][] getContraintOrderedTableKeyColumnClosure() {
 		return constraintOrderedKeyColumnNames;
 	}
 
 	public String getRootTableName() {
 		return naturalOrderTableNames[0];
 	}
 
 	public String getRootTableAlias(String drivingAlias) {
 		return generateTableAlias( drivingAlias, getTableId( getRootTableName(), tableNames ) );
 	}
 
 	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
 		if ( "class".equals( propertyPath ) ) {
 			// special case where we need to force incloude all subclass joins
 			return Declarer.SUBCLASS;
 		}
 		return super.getSubclassPropertyDeclarer( propertyPath );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/Loadable.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/Loadable.java
index a04a875966..eaffcfd027 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/Loadable.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/Loadable.java
@@ -1,117 +1,122 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Middleware LLC.
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
  *
  */
 package org.hibernate.persister.entity;
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Implemented by a <tt>EntityPersister</tt> that may be loaded
  * using <tt>Loader</tt>.
  *
  * @see org.hibernate.loader.Loader
  * @author Gavin King
  */
 public interface Loadable extends EntityPersister {
 	
 	public static final String ROWID_ALIAS = "rowid_";
 
 	/**
 	 * Does this persistent class have subclasses?
 	 */
 	public boolean hasSubclasses();
 
 	/**
 	 * Get the discriminator type
 	 */
 	public Type getDiscriminatorType();
 
 	/**
+	 * Get the discriminator value
+	 */
+	public Object getDiscriminatorValue();
+
+	/**
 	 * Get the concrete subclass corresponding to the given discriminator
 	 * value
 	 */
 	public String getSubclassForDiscriminatorValue(Object value);
 
 	/**
 	 * Get the names of columns used to persist the identifier
 	 */
 	public String[] getIdentifierColumnNames();
 
 	/**
 	 * Get the result set aliases used for the identifier columns, given a suffix
 	 */
 	public String[] getIdentifierAliases(String suffix);
 	/**
 	 * Get the result set aliases used for the property columns, given a suffix (properties of this class, only).
 	 */
 	public String[] getPropertyAliases(String suffix, int i);
 	
 	/**
 	 * Get the result set column names mapped for this property (properties of this class, only).
 	 */
 	public String[] getPropertyColumnNames(int i);
 	
 	/**
 	 * Get the result set aliases used for the identifier columns, given a suffix
 	 */
 	public String getDiscriminatorAlias(String suffix);
 	
 	/**
 	 * @return the column name for the discriminator as specified in the mapping.
 	 */
 	public String getDiscriminatorColumnName();
 	
 	/**
 	 * Does the result set contain rowids?
 	 */
 	public boolean hasRowId();
 	
 	/**
 	 * Retrieve property values from one row of a result set
 	 */
 	public Object[] hydrate(
 			ResultSet rs,
 			Serializable id,
 			Object object,
 			Loadable rootLoadable,
 			String[][] suffixedPropertyColumns,
 			boolean allProperties, 
 			SessionImplementor session)
 	throws SQLException, HibernateException;
 
 	public boolean isAbstract();
 
 	/**
 	 * Register the name of a fetch profile determined to have an affect on the
 	 * underlying loadable in regards to the fact that the underlying load SQL
 	 * needs to be adjust when the given fetch profile is enabled.
 	 * 
 	 * @param fetchProfileName The name of the profile affecting this.
 	 */
 	public void registerAffectingFetchProfile(String fetchProfileName);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
index 9c0c2922a7..f142147b19 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
@@ -1,1034 +1,1037 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Middleware LLC.
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
  *
  */
 package org.hibernate.persister.entity;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
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
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.SimpleValueBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.TableSpecification;
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
 	private final Map subclassesByDiscriminatorValue = new HashMap();
 	private final boolean forceDiscriminator;
 	private final String discriminatorColumnName;
 	private final String discriminatorColumnReaders;
 	private final String discriminatorColumnReaderTemplate;
 	private final String discriminatorFormula;
 	private final String discriminatorFormulaTemplate;
 	private final String discriminatorAlias;
 	private final Type discriminatorType;
+	private final Object discriminatorValue;
 	private final String discriminatorSQLValue;
 	private final boolean discriminatorInsertable;
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	//private final Map propertyTableNumbersByName = new HashMap();
 	private final Map propertyTableNumbersByNameAndSubclass = new HashMap();
 	
 	private final Map sequentialSelectStringsByEntityName = new HashMap();
 
 	private static final Object NULL_DISCRIMINATOR = new MarkerObject("<null discriminator>");
 	private static final Object NOT_NULL_DISCRIMINATOR = new MarkerObject("<not null discriminator>");
 	private static final String NULL_STRING = "null";
 	private static final String NOT_NULL_STRING = "not null";
 
 	//INITIALIZATION:
 
 	public SingleTableEntityPersister(
 			final PersistentClass persistentClass, 
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, factory );
 
 		// CLASS + TABLE
 
 		joinSpan = persistentClass.getJoinClosureSpan()+1;
 		qualifiedTableNames = new String[joinSpan];
 		isInverseTable = new boolean[joinSpan];
 		isNullableTable = new boolean[joinSpan];
 		keyColumnNames = new String[joinSpan][];
 		final Table table = persistentClass.getRootTable();
 		qualifiedTableNames[0] = table.getQualifiedName( 
 				factory.getDialect(), 
 				factory.getSettings().getDefaultCatalogName(), 
 				factory.getSettings().getDefaultSchemaName() 
 		);
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
 			qualifiedTableNames[j] = join.getTable().getQualifiedName( 
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			);
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
 			keyColumnNames[j] = new String[ join.getKey().getColumnSpan() ];
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
 		ArrayList subclassTables = new ArrayList();
 		ArrayList joinKeyColumns = new ArrayList();
 		ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
 		ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
 		ArrayList<Boolean> isInverses = new ArrayList<Boolean>();
 		ArrayList<Boolean> isNullables = new ArrayList<Boolean>();
 		ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
 		subclassTables.add( qualifiedTableNames[0] );
 		joinKeyColumns.add( getIdentifierColumnNames() );
 		isConcretes.add(Boolean.TRUE);
 		isDeferreds.add(Boolean.FALSE);
 		isInverses.add(Boolean.FALSE);
 		isNullables.add(Boolean.FALSE);
 		isLazies.add(Boolean.FALSE);
 		joinIter = persistentClass.getSubclassJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			isConcretes.add( persistentClass.isClassOrSuperclassJoin(join) );
 			isDeferreds.add( join.isSequentialSelect() );
 			isInverses.add( join.isInverse() );
 			isNullables.add( join.isOptional() );
 			isLazies.add( lazyAvailable && join.isLazy() );
 			if ( join.isSequentialSelect() && !persistentClass.isClassOrSuperclassJoin(join) ) hasDeferred = true;
 			subclassTables.add( join.getTable().getQualifiedName( 
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			) );
 			Iterator iter = join.getKey().getColumnIterator();
 			String[] keyCols = new String[ join.getKey().getColumnSpan() ];
 			int i = 0;
 			while ( iter.hasNext() ) {
 				Column col = (Column) iter.next();
 				keyCols[i++] = col.getQuotedName( factory.getDialect() );
 			}
 			joinKeyColumns.add(keyCols);
 		}
 		
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
 		subclassTableNameClosure = ArrayHelper.toStringArray(subclassTables);
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
 		subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( joinKeyColumns );
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
 		isInverseSubclassTable = ArrayHelper.toBooleanArray(isInverses);
 		isNullableSubclassTable = ArrayHelper.toBooleanArray(isNullables);
 		hasSequentialSelects = hasDeferred;
 
 		// DISCRIMINATOR
 
-		final Object discriminatorValue;
 		if ( persistentClass.isPolymorphic() ) {
 			Value discrimValue = persistentClass.getDiscriminator();
 			if (discrimValue==null) {
 				throw new MappingException("discriminator mapping required for single table polymorphic persistence");
 			}
 			forceDiscriminator = persistentClass.isForceDiscriminator();
 			Selectable selectable = (Selectable) discrimValue.getColumnIterator().next();
 			if ( discrimValue.hasFormula() ) {
 				Formula formula = (Formula) selectable;
 				discriminatorFormula = formula.getFormula();
 				discriminatorFormulaTemplate = formula.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				discriminatorColumnName = null;
 				discriminatorColumnReaders = null;
 				discriminatorColumnReaderTemplate = null;
 				discriminatorAlias = "clazz_";
 			}
 			else {
 				Column column = (Column) selectable;
 				discriminatorColumnName = column.getQuotedName( factory.getDialect() );
 				discriminatorColumnReaders = column.getReadExpr( factory.getDialect() );
 				discriminatorColumnReaderTemplate = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
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
 					throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 				}
 				catch (Exception e) {
 					throw new MappingException("Could not format discriminator value to SQL string", e);
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
 
 		propertyTableNumbers = new int[ getPropertySpan() ];
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i=0;
 		while( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			propertyTableNumbers[i++] = persistentClass.getJoinNumber(prop);
 
 		}
 
 		//TODO: code duplication with JoinedSubclassEntityPersister
 		
 		ArrayList columnJoinNumbers = new ArrayList();
 		ArrayList formulaJoinedNumbers = new ArrayList();
 		ArrayList propertyJoinNumbers = new ArrayList();
 		
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			Integer join = persistentClass.getJoinNumber(prop);
 			propertyJoinNumbers.add(join);
 
 			//propertyTableNumbersByName.put( prop.getName(), join );
 			propertyTableNumbersByNameAndSubclass.put( 
 					prop.getPersistentClass().getEntityName() + '.' + prop.getName(), 
 					join 
 			);
 
 			Iterator citer = prop.getColumnIterator();
 			while ( citer.hasNext() ) {
 				Selectable thing = (Selectable) citer.next();
 				if ( thing.isFormula() ) {
 					formulaJoinedNumbers.add(join);
 				}
 				else {
 					columnJoinNumbers.add(join);
 				}
 			}
 		}
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnJoinNumbers);
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaJoinedNumbers);
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propertyJoinNumbers);
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 		if ( persistentClass.isPolymorphic() ) {
 			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
 		}
 
 		// SUBCLASSES
 		if ( persistentClass.isPolymorphic() ) {
 			iter = persistentClass.getSubclassIterator();
 			int k=1;
 			while ( iter.hasNext() ) {
 				Subclass sc = (Subclass) iter.next();
 				subclassClosure[k++] = sc.getEntityName();
 				if ( sc.isDiscriminatorValueNull() ) {
 					subclassesByDiscriminatorValue.put( NULL_DISCRIMINATOR, sc.getEntityName() );
 				}
 				else if ( sc.isDiscriminatorValueNotNull() ) {
 					subclassesByDiscriminatorValue.put( NOT_NULL_DISCRIMINATOR, sc.getEntityName() );
 				}
 				else {
 					try {
 						DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
 						subclassesByDiscriminatorValue.put(
 							dtype.stringToObject( sc.getDiscriminatorValue() ),
 							sc.getEntityName()
 						);
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException("Error parsing discriminator value", e);
 					}
 				}
 			}
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap(persistentClass);
 		
 		postConstruct(mapping);
 
 	}
 
 	public SingleTableEntityPersister(
 			final EntityBinding entityBinding,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( entityBinding, cacheAccessStrategy, factory );
 
 		// CLASS + TABLE
 
 		// TODO: fix when joins are working (HHH-6391)
 		//joinSpan = entityBinding.getJoinClosureSpan() + 1;
 		joinSpan = 1;
 		qualifiedTableNames = new String[joinSpan];
 		isInverseTable = new boolean[joinSpan];
 		isNullableTable = new boolean[joinSpan];
 		keyColumnNames = new String[joinSpan][];
 
 		final TableSpecification table = entityBinding.getPrimaryTable();
 		qualifiedTableNames[0] = table.getQualifiedName( factory.getDialect() );
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
 
 		initializeCustomSql( entityBinding.getCustomInsert(), 0, customSQLInsert, insertCallable, insertResultCheckStyles );
 		initializeCustomSql( entityBinding.getCustomUpdate(), 0, customSQLUpdate, updateCallable, updateResultCheckStyles );
 		initializeCustomSql( entityBinding.getCustomDelete(), 0, customSQLDelete, deleteCallable, deleteResultCheckStyles );
 
 		// JOINS
 
 		// TODO: add join stuff when HHH-6391 is working
 
 		constraintOrderedTableNames = new String[qualifiedTableNames.length];
 		constraintOrderedKeyColumnNames = new String[qualifiedTableNames.length][];
 		for ( int i = qualifiedTableNames.length - 1, position = 0; i >= 0; i--, position++ ) {
 			constraintOrderedTableNames[position] = qualifiedTableNames[i];
 			constraintOrderedKeyColumnNames[position] = keyColumnNames[i];
 		}
 
 		spaces = ArrayHelper.join(
 				qualifiedTableNames,
 				ArrayHelper.toStringArray( entityBinding.getSynchronizedTableNames() )
 		);
 
 		final boolean lazyAvailable = isInstrumented();
 
 		boolean hasDeferred = false;
 		ArrayList subclassTables = new ArrayList();
 		ArrayList joinKeyColumns = new ArrayList();
 		ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
 		ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
 		ArrayList<Boolean> isInverses = new ArrayList<Boolean>();
 		ArrayList<Boolean> isNullables = new ArrayList<Boolean>();
 		ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
 		subclassTables.add( qualifiedTableNames[0] );
 		joinKeyColumns.add( getIdentifierColumnNames() );
 		isConcretes.add(Boolean.TRUE);
 		isDeferreds.add(Boolean.FALSE);
 		isInverses.add(Boolean.FALSE);
 		isNullables.add(Boolean.FALSE);
 		isLazies.add(Boolean.FALSE);
 
 		// TODO: add join stuff when HHH-6391 is working
 
 
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
 		subclassTableNameClosure = ArrayHelper.toStringArray(subclassTables);
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
 		subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( joinKeyColumns );
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
 		isInverseSubclassTable = ArrayHelper.toBooleanArray(isInverses);
 		isNullableSubclassTable = ArrayHelper.toBooleanArray(isNullables);
 		hasSequentialSelects = hasDeferred;
 
 		// DISCRIMINATOR
 
-		final Object discriminatorValue;
 		if ( entityBinding.isPolymorphic() ) {
 			SimpleValue discriminatorRelationalValue = entityBinding.getHierarchyDetails().getEntityDiscriminator().getBoundValue();
 			if ( discriminatorRelationalValue == null ) {
 				throw new MappingException("discriminator mapping required for single table polymorphic persistence");
 			}
 			forceDiscriminator = entityBinding.getHierarchyDetails().getEntityDiscriminator().isForced();
 			if ( DerivedValue.class.isInstance( discriminatorRelationalValue ) ) {
 				DerivedValue formula = ( DerivedValue ) discriminatorRelationalValue;
 				discriminatorFormula = formula.getExpression();
 				discriminatorFormulaTemplate = getTemplateFromString( formula.getExpression(), factory );
 				discriminatorColumnName = null;
 				discriminatorColumnReaders = null;
 				discriminatorColumnReaderTemplate = null;
 				discriminatorAlias = "clazz_";
 			}
 			else {
 				org.hibernate.metamodel.relational.Column column = ( org.hibernate.metamodel.relational.Column ) discriminatorRelationalValue;
 				discriminatorColumnName = column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 				discriminatorColumnReaders =
 						column.getReadFragment() == null ?
 								column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() ) :
 								column.getReadFragment();
 				discriminatorColumnReaderTemplate = getTemplateFromColumn( column, factory );
 				discriminatorAlias = column.getAlias( factory.getDialect() );
 				discriminatorFormula = null;
 				discriminatorFormulaTemplate = null;
 			}
 
 			discriminatorType = entityBinding.getHierarchyDetails()
 					.getEntityDiscriminator()
 					.getExplicitHibernateTypeDescriptor()
 					.getResolvedTypeMapping();
 			if ( entityBinding.getDiscriminatorMatchValue() == null ) {
 				discriminatorValue = NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( entityBinding.getDiscriminatorMatchValue().equals( NULL_STRING ) ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( entityBinding.getDiscriminatorMatchValue().equals( NOT_NULL_STRING ) ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else {
 				discriminatorInsertable = entityBinding.getHierarchyDetails().getEntityDiscriminator().isInserted()
 						&& ! DerivedValue.class.isInstance( discriminatorRelationalValue );
 				try {
 					DiscriminatorType dtype = ( DiscriminatorType ) discriminatorType;
 					discriminatorValue = dtype.stringToObject( entityBinding.getDiscriminatorMatchValue() );
 					discriminatorSQLValue = dtype.objectToSQLString( discriminatorValue, factory.getDialect() );
 				}
 				catch (ClassCastException cce) {
 					throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 				}
 				catch (Exception e) {
 					throw new MappingException("Could not format discriminator value to SQL string", e);
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
 
 		propertyTableNumbers = new int[ getPropertySpan() ];
 		int i=0;
 		for( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			// TODO: fix when joins are working (HHH-6391)
 			//propertyTableNumbers[i++] = entityBinding.getJoinNumber( attributeBinding);
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() ) {
 				continue; // skip identifier binding
 			}
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				continue;
 			}
 			propertyTableNumbers[ i++ ] = 0;
 		}
 
 		//TODO: code duplication with JoinedSubclassEntityPersister
 
 		ArrayList columnJoinNumbers = new ArrayList();
 		ArrayList formulaJoinedNumbers = new ArrayList();
 		ArrayList propertyJoinNumbers = new ArrayList();
 
 		for ( AttributeBinding attributeBinding : entityBinding.getSubEntityAttributeBindingClosure() ) {
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				continue;
 			}
 			SingularAttributeBinding singularAttributeBinding = (SingularAttributeBinding) attributeBinding;
 
 			// TODO: fix when joins are working (HHH-6391)
 			//int join = entityBinding.getJoinNumber(singularAttributeBinding);
 			int join = 0;
 			propertyJoinNumbers.add(join);
 
 			//propertyTableNumbersByName.put( singularAttributeBinding.getName(), join );
 			propertyTableNumbersByNameAndSubclass.put(
 					singularAttributeBinding.getContainer().getPathBase() + '.' + singularAttributeBinding.getAttribute().getName(),
 					join
 			);
 
 			for ( SimpleValueBinding simpleValueBinding : singularAttributeBinding.getSimpleValueBindings() ) {
 				if ( DerivedValue.class.isInstance( simpleValueBinding.getSimpleValue() ) ) {
 					formulaJoinedNumbers.add( join );
 				}
 				else {
 					columnJoinNumbers.add( join );
 				}
 			}
 		}
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnJoinNumbers);
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaJoinedNumbers);
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propertyJoinNumbers);
 
 		int subclassSpan = entityBinding.getSubEntityBindingClosureSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 		if ( entityBinding.isPolymorphic() ) {
 			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
 		}
 
 		// SUBCLASSES
 		if ( entityBinding.isPolymorphic() ) {
 			int k=1;
 			for ( EntityBinding subEntityBinding : entityBinding.getPostOrderSubEntityBindingClosure() ) {
 				subclassClosure[k++] = subEntityBinding.getEntity().getName();
 				if ( subEntityBinding.isDiscriminatorMatchValueNull() ) {
 					subclassesByDiscriminatorValue.put( NULL_DISCRIMINATOR, subEntityBinding.getEntity().getName() );
 				}
 				else if ( subEntityBinding.isDiscriminatorMatchValueNotNull() ) {
 					subclassesByDiscriminatorValue.put( NOT_NULL_DISCRIMINATOR, subEntityBinding.getEntity().getName() );
 				}
 				else {
 					try {
 						DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
 						subclassesByDiscriminatorValue.put(
 							dtype.stringToObject( subEntityBinding.getDiscriminatorMatchValue() ),
 							subEntityBinding.getEntity().getName()
 						);
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException("Error parsing discriminator value", e);
 					}
 				}
 			}
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap( entityBinding );
 
 		postConstruct( mapping );
 	}
 
 	private static void initializeCustomSql(
 			CustomSQL customSql,
 			int i,
 			String[] sqlStrings,
 			boolean[] callable,
 			ExecuteUpdateResultCheckStyle[] checkStyles) {
 		sqlStrings[i] = customSql != null ?  customSql.getSql(): null;
 		callable[i] = sqlStrings[i] != null && customSql.isCallable();
 		checkStyles[i] = customSql != null && customSql.getCheckStyle() != null ?
 				customSql.getCheckStyle() :
 				ExecuteUpdateResultCheckStyle.determineDefault( sqlStrings[i], callable[i] );
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
 
+	public Object getDiscriminatorValue() {
+		return discriminatorValue;
+	}
+
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLValue;
 	}
 
 	public String[] getSubclassClosure() {
 		return subclassClosure;
 	}
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		if (value==null) {
 			return (String) subclassesByDiscriminatorValue.get(NULL_DISCRIMINATOR);
 		}
 		else {
 			String result = (String) subclassesByDiscriminatorValue.get(value);
 			if (result==null) result = (String) subclassesByDiscriminatorValue.get(NOT_NULL_DISCRIMINATOR);
 			return result;
 		}
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces;
 	}
 
 	//Access cached SQL
 
 	protected boolean isDiscriminatorFormula() {
 		return discriminatorColumnName==null;
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
 		return propertyTableNumbers[property]==j;
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
 	}
 	
 	// Execute the SQL:
 
 	public String fromTableFragment(String name) {
 		return getTableName() + ' ' + name;
 	}
 
 	public String filterFragment(String alias) throws MappingException {
 		String result = discriminatorFilterFragment(alias);
 		if ( hasWhere() ) result += " and " + getSQLWhereString(alias);
 		return result;
 	}
 	
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return forceDiscriminator ?
 			discriminatorFilterFragment(alias) :
 			"";
 	}
 
 	private String discriminatorFilterFragment(String alias) throws MappingException {
 		if ( needsDiscriminator() ) {
 			InFragment frag = new InFragment();
 
 			if ( isDiscriminatorFormula() ) {
 				frag.setFormula( alias, getDiscriminatorFormulaTemplate() );
 			}
 			else {
 				frag.setColumn( alias, getDiscriminatorColumnName() );
 			}
 
 			String[] subclasses = getSubclassClosure();
 			for ( int i=0; i<subclasses.length; i++ ) {
 				final Queryable queryable = (Queryable) getFactory().getEntityPersister( subclasses[i] );
 				if ( !queryable.isAbstract() ) frag.addValue( queryable.getDiscriminatorSQLValue() );
 			}
 
 			StringBuffer buf = new StringBuffer(50)
 				.append(" and ")
 				.append( frag.toFragmentString() );
 
 			return buf.toString();
 		}
 		else {
 			return "";
 		}
 	}
 
 	private boolean needsDiscriminator() {
 		return forceDiscriminator || isInherited();
 	}
 
 	public String getSubclassPropertyTableName(int i) {
 		return subclassTableNameClosure[ subclassPropertyTableNumberClosure[i] ];
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		if ( isDiscriminatorFormula() ) {
 			select.addFormula( name, getDiscriminatorFormulaTemplate(), getDiscriminatorAlias() );
 		}
 		else {
 			select.addColumn( name, getDiscriminatorColumnName(),  getDiscriminatorAlias() );
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
 
 		if (discriminatorInsertable) {
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
 			isSubclassTableSequentialSelect( getSubclassPropertyTableNumber(propertyName, entityName) );
 	}
 	
 	public boolean hasSequentialSelect() {
 		return hasSequentialSelects;
 	}
 	
 	private int getSubclassPropertyTableNumber(String propertyName, String entityName) {
 		Type type = propertyMapping.toType(propertyName);
 		if ( type.isAssociationType() && ( (AssociationType) type ).useLHSPrimaryKey() ) return 0;
 		final Integer tabnum = (Integer) propertyTableNumbersByNameAndSubclass.get(entityName + '.' + propertyName);
 		return tabnum==null ? 0 : tabnum.intValue();
 	}
 	
 	protected String getSequentialSelect(String entityName) {
 		return (String) sequentialSelectStringsByEntityName.get(entityName);
 	}
 
 	private String generateSequentialSelect(Loadable persister) {
 		//if ( this==persister || !hasSequentialSelects ) return null;
 
 		//note that this method could easily be moved up to BasicEntityPersister,
 		//if we ever needed to reuse it from other subclasses
 		
 		//figure out which tables need to be fetched
 		AbstractEntityPersister subclassPersister = (AbstractEntityPersister) persister;
 		HashSet tableNumbers = new HashSet();
 		String[] props = subclassPersister.getPropertyNames();
 		String[] classes = subclassPersister.getPropertySubclassNames();
 		for ( int i=0; i<props.length; i++ ) {
 			int propTableNumber = getSubclassPropertyTableNumber( props[i], classes[i] );
 			if ( isSubclassTableSequentialSelect(propTableNumber) && !isSubclassTableLazy(propTableNumber) ) {
 				tableNumbers.add( propTableNumber);
 			}
 		}
 		if ( tableNumbers.isEmpty() ) return null;
 		
 		//figure out which columns are needed
 		ArrayList columnNumbers = new ArrayList();
 		final int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		for ( int i=0; i<getSubclassColumnClosure().length; i++ ) {
 			if ( tableNumbers.contains( columnTableNumbers[i] ) ) {
 				columnNumbers.add( i );
 			}
 		}
 		
 		//figure out which formulas are needed
 		ArrayList formulaNumbers = new ArrayList();
 		final int[] formulaTableNumbers = getSubclassColumnTableNumberClosure();
 		for ( int i=0; i<getSubclassFormulaTemplateClosure().length; i++ ) {
 			if ( tableNumbers.contains( formulaTableNumbers[i] ) ) {
 				formulaNumbers.add( i );
 			}
 		}
 		
 		//render the SQL
 		return renderSelect( 
 			ArrayHelper.toIntArray(tableNumbers),
 			ArrayHelper.toIntArray(columnNumbers),
 			ArrayHelper.toIntArray(formulaNumbers)
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
 		Integer index = getEntityMetamodel().getPropertyIndexOrNull(propertyName);
 		if (index==null) return null;
 		return qualifiedTableNames[ propertyTableNumbers[ index.intValue() ] ];
 	}
 	
 	public void postInstantiate() {
 		super.postInstantiate();
 		if (hasSequentialSelects) {
 			String[] entityNames = getSubclassClosure();
 			for ( int i=1; i<entityNames.length; i++ ) {
 				Loadable loadable = (Loadable) getFactory().getEntityPersister( entityNames[i] );
 				if ( !loadable.isAbstract() ) { //perhaps not really necessary...
 					String sequentialSelect = generateSequentialSelect(loadable);
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
index 38c2f09f33..a95fc50b19 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
@@ -1,500 +1,507 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cfg.Settings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.collections.SingletonIterator;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of the "table-per-concrete-class" or "roll-down" mapping 
  * strategy for an entity and its inheritence hierarchy.
  *
  * @author Gavin King
  */
 public class UnionSubclassEntityPersister extends AbstractEntityPersister {
 
 	// the class hierarchy structure
 	private final String subquery;
 	private final String tableName;
 	//private final String rootTableName;
 	private final String[] subclassClosure;
 	private final String[] spaces;
 	private final String[] subclassSpaces;
+	private final Object discriminatorValue;
 	private final String discriminatorSQLValue;
 	private final Map subclassByDiscriminatorValue = new HashMap();
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	//INITIALIZATION:
 
 	public UnionSubclassEntityPersister(
 			final PersistentClass persistentClass, 
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, factory );
 		
 		if ( getIdentifierGenerator() instanceof IdentityGenerator ) {
 			throw new MappingException(
 					"Cannot use identity column key generation with <union-subclass> mapping for: " + 
 					getEntityName() 
 			);
 		}
 
 		// TABLE
 
 		tableName = persistentClass.getTable().getQualifiedName( 
 				factory.getDialect(), 
 				factory.getSettings().getDefaultCatalogName(), 
 				factory.getSettings().getDefaultSchemaName() 
 		);
 		/*rootTableName = persistentClass.getRootTable().getQualifiedName( 
 				factory.getDialect(), 
 				factory.getDefaultCatalog(), 
 				factory.getDefaultSchema() 
 		);*/
 
 		//Custom SQL
 
 		String sql;
 		boolean callable = false;
 		ExecuteUpdateResultCheckStyle checkStyle = null;
 		sql = persistentClass.getCustomSQLInsert();
 		callable = sql != null && persistentClass.isCustomInsertCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 	            : persistentClass.getCustomSQLInsertCheckStyle() == null
 						? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 	                    : persistentClass.getCustomSQLInsertCheckStyle();
 		customSQLInsert = new String[] { sql };
 		insertCallable = new boolean[] { callable };
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[] { checkStyle };
 
 		sql = persistentClass.getCustomSQLUpdate();
 		callable = sql != null && persistentClass.isCustomUpdateCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 	            : persistentClass.getCustomSQLUpdateCheckStyle() == null
 						? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 	                    : persistentClass.getCustomSQLUpdateCheckStyle();
 		customSQLUpdate = new String[] { sql };
 		updateCallable = new boolean[] { callable };
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[] { checkStyle };
 
 		sql = persistentClass.getCustomSQLDelete();
 		callable = sql != null && persistentClass.isCustomDeleteCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 	            : persistentClass.getCustomSQLDeleteCheckStyle() == null
 						? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 	                    : persistentClass.getCustomSQLDeleteCheckStyle();
 		customSQLDelete = new String[] { sql };
 		deleteCallable = new boolean[] { callable };
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[] { checkStyle };
 
+		discriminatorValue = new Integer( persistentClass.getSubclassId() );
 		discriminatorSQLValue = String.valueOf( persistentClass.getSubclassId() );
 
 		// PROPERTIES
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 
 		// SUBCLASSES
 		subclassByDiscriminatorValue.put( 
 				new Integer( persistentClass.getSubclassId() ), 
 				persistentClass.getEntityName() 
 		);
 		if ( persistentClass.isPolymorphic() ) {
 			Iterator iter = persistentClass.getSubclassIterator();
 			int k=1;
 			while ( iter.hasNext() ) {
 				Subclass sc = (Subclass) iter.next();
 				subclassClosure[k++] = sc.getEntityName();
 				subclassByDiscriminatorValue.put( new Integer( sc.getSubclassId() ), sc.getEntityName() );
 			}
 		}
 		
 		//SPACES
 		//TODO: i'm not sure, but perhaps we should exclude
 		//      abstract denormalized tables?
 		
 		int spacesSize = 1 + persistentClass.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = tableName;
 		Iterator iter = persistentClass.getSynchronizedTables().iterator();
 		for ( int i=1; i<spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 		
 		HashSet subclassTables = new HashSet();
 		iter = persistentClass.getSubclassTableClosureIterator();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			subclassTables.add( table.getQualifiedName(
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			) );
 		}
 		subclassSpaces = ArrayHelper.toStringArray(subclassTables);
 
 		subquery = generateSubquery(persistentClass, mapping);
 
 		if ( isMultiTable() ) {
 			int idColumnSpan = getIdentifierColumnSpan();
 			ArrayList tableNames = new ArrayList();
 			ArrayList keyColumns = new ArrayList();
 			if ( !isAbstract() ) {
 				tableNames.add( tableName );
 				keyColumns.add( getIdentifierColumnNames() );
 			}
 			iter = persistentClass.getSubclassTableClosureIterator();
 			while ( iter.hasNext() ) {
 				Table tab = ( Table ) iter.next();
 				if ( !tab.isAbstractUnionTable() ) {
 					String tableName = tab.getQualifiedName(
 							factory.getDialect(),
 							factory.getSettings().getDefaultCatalogName(),
 							factory.getSettings().getDefaultSchemaName()
 					);
 					tableNames.add( tableName );
 					String[] key = new String[idColumnSpan];
 					Iterator citer = tab.getPrimaryKey().getColumnIterator();
 					for ( int k=0; k<idColumnSpan; k++ ) {
 						key[k] = ( ( Column ) citer.next() ).getQuotedName( factory.getDialect() );
 					}
 					keyColumns.add( key );
 				}
 			}
 
 			constraintOrderedTableNames = ArrayHelper.toStringArray( tableNames );
 			constraintOrderedKeyColumnNames = ArrayHelper.to2DStringArray( keyColumns );
 		}
 		else {
 			constraintOrderedTableNames = new String[] { tableName };
 			constraintOrderedKeyColumnNames = new String[][] { getIdentifierColumnNames() };
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap(persistentClass);
 		
 		postConstruct(mapping);
 
 	}
 
 	public UnionSubclassEntityPersister(
 			final EntityBinding entityBinding,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 		super(entityBinding, cacheAccessStrategy, factory );
 		// TODO: implement!!! initializing final fields to null to make compiler happy.
 		subquery = null;
 		tableName = null;
 		subclassClosure = null;
 		spaces = null;
 		subclassSpaces = null;
+		discriminatorValue = null;
 		discriminatorSQLValue = null;
 		constraintOrderedTableNames = null;
 		constraintOrderedKeyColumnNames = null;
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return subclassSpaces;
 	}
 	
 	public String getTableName() {
 		return subquery;
 	}
 
 	public Type getDiscriminatorType() {
 		return StandardBasicTypes.INTEGER;
 	}
 
+	public Object getDiscriminatorValue() {
+		return discriminatorValue;
+	}
+
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLValue;
 	}
 
 	public String[] getSubclassClosure() {
 		return subclassClosure;
 	}
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		return (String) subclassByDiscriminatorValue.get(value);
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces;
 	}
 
 	protected boolean isDiscriminatorFormula() {
 		return false;
 	}
 
 	/**
 	 * Generate the SQL that selects a row by id
 	 */
 	protected String generateSelectString(LockMode lockMode) {
 		SimpleSelect select = new SimpleSelect( getFactory().getDialect() )
 			.setLockMode(lockMode)
 			.setTableName( getTableName() )
 			.addColumns( getIdentifierColumnNames() )
 			.addColumns( 
 					getSubclassColumnClosure(), 
 					getSubclassColumnAliasClosure(),
 					getSubclassColumnLazyiness()
 			)
 			.addColumns( 
 					getSubclassFormulaClosure(), 
 					getSubclassFormulaAliasClosure(),
 					getSubclassFormulaLazyiness()
 			);
 		//TODO: include the rowids!!!!
 		if ( hasSubclasses() ) {
 			if ( isDiscriminatorFormula() ) {
 				select.addColumn( getDiscriminatorFormula(), getDiscriminatorAlias() );
 			}
 			else {
 				select.addColumn( getDiscriminatorColumnName(), getDiscriminatorAlias() );
 			}
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "load " + getEntityName() );
 		}
 		return select.addCondition( getIdentifierColumnNames(), "=?" ).toStatementString();
 	}
 
 	protected String getDiscriminatorFormula() {
 		return null;
 	}
 
 	protected String getTableName(int j) {
 		return tableName;
 	}
 
 	protected String[] getKeyColumns(int j) {
 		return getIdentifierColumnNames();
 	}
 	
 	protected boolean isTableCascadeDeleteEnabled(int j) {
 		return false;
 	}
 	
 	protected boolean isPropertyOfTable(int property, int j) {
 		return true;
 	}
 
 	// Execute the SQL:
 
 	public String fromTableFragment(String name) {
 		return getTableName() + ' '  + name;
 	}
 
 	public String filterFragment(String name) {
 		return hasWhere() ?
 			" and " + getSQLWhereString(name) :
 			"";
 	}
 
 	public String getSubclassPropertyTableName(int i) {
 		return getTableName();//ie. the subquery! yuck!
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		select.addColumn( name, getDiscriminatorColumnName(),  getDiscriminatorAlias() );
 	}
 	
 	protected int[] getPropertyTableNumbersInSelect() {
 		return new int[ getPropertySpan() ];
 	}
 
 	protected int getSubclassPropertyTableNumber(int i) {
 		return 0;
 	}
 
 	public int getSubclassPropertyTableNumber(String propertyName) {
 		return 0;
 	}
 
 	public boolean isMultiTable() {
 		// This could also just be true all the time...
 		return isAbstract() || hasSubclasses();
 	}
 
 	public int getTableSpan() {
 		return 1;
 	}
 
 	protected int[] getSubclassColumnTableNumberClosure() {
 		return new int[ getSubclassColumnClosure().length ];
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return new int[ getSubclassFormulaClosure().length ];
 	}
 
 	protected boolean[] getTableHasColumns() {
 		return new boolean[] { true };
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return new int[ getPropertySpan() ];
 	}
 
 	protected String generateSubquery(PersistentClass model, Mapping mapping) {
 
 		Dialect dialect = getFactory().getDialect();
 		Settings settings = getFactory().getSettings();
 		
 		if ( !model.hasSubclasses() ) {
 			return model.getTable().getQualifiedName(
 					dialect,
 					settings.getDefaultCatalogName(),
 					settings.getDefaultSchemaName()
 				);
 		}
 
 		HashSet columns = new LinkedHashSet();
 		Iterator titer = model.getSubclassTableClosureIterator();
 		while ( titer.hasNext() ) {
 			Table table = (Table) titer.next();
 			if ( !table.isAbstractUnionTable() ) {
 				Iterator citer = table.getColumnIterator();
 				while ( citer.hasNext() ) columns.add( citer.next() );
 			}
 		}
 
 		StringBuilder buf = new StringBuilder()
 			.append("( ");
 
 		Iterator siter = new JoinedIterator(
 			new SingletonIterator(model),
 			model.getSubclassIterator()
 		);
 
 		while ( siter.hasNext() ) {
 			PersistentClass clazz = (PersistentClass) siter.next();
 			Table table = clazz.getTable();
 			if ( !table.isAbstractUnionTable() ) {
 				//TODO: move to .sql package!!
 				buf.append("select ");
 				Iterator citer = columns.iterator();
 				while ( citer.hasNext() ) {
 					Column col = (Column) citer.next();
 					if ( !table.containsColumn(col) ) {
 						int sqlType = col.getSqlTypeCode(mapping);
 						buf.append( dialect.getSelectClauseNullString(sqlType) )
 							.append(" as ");
 					}
 					buf.append( col.getName() );
 					buf.append(", ");
 				}
 				buf.append( clazz.getSubclassId() )
 					.append(" as clazz_");
 				buf.append(" from ")
 					.append( table.getQualifiedName(
 							dialect,
 							settings.getDefaultCatalogName(),
 							settings.getDefaultSchemaName()
 					) );
 				buf.append(" union ");
 				if ( dialect.supportsUnionAll() ) {
 					buf.append("all ");
 				}
 			}
 		}
 		
 		if ( buf.length() > 2 ) {
 			//chop the last union (all)
 			buf.setLength( buf.length() - ( dialect.supportsUnionAll() ? 11 : 7 ) );
 		}
 
 		return buf.append(" )").toString();
 	}
 
 	protected String[] getSubclassTableKeyColumns(int j) {
 		if (j!=0) throw new AssertionFailure("only one table");
 		return getIdentifierColumnNames();
 	}
 
 	public String getSubclassTableName(int j) {
 		if (j!=0) throw new AssertionFailure("only one table");
 		return tableName;
 	}
 
 	public int getSubclassTableSpan() {
 		return 1;
 	}
 
 	protected boolean isClassOrSuperclassTable(int j) {
 		if (j!=0) throw new AssertionFailure("only one table");
 		return true;
 	}
 
 	public String getPropertyTableName(String propertyName) {
 		//TODO: check this....
 		return getTableName();
 	}
 
 	public String[] getConstraintOrderedTableNameClosure() {
 			return constraintOrderedTableNames;
 	}
 
 	public String[][] getContraintOrderedTableKeyColumnClosure() {
 		return constraintOrderedKeyColumnNames;
 	}
 }
