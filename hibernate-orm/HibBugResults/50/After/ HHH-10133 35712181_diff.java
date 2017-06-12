diff --git a/hibernate-core/src/main/java/org/hibernate/boot/model/relational/QualifiedName.java b/hibernate-core/src/main/java/org/hibernate/boot/model/relational/QualifiedName.java
index 7246d86e11..accffef431 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/model/relational/QualifiedName.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/model/relational/QualifiedName.java
@@ -1,26 +1,40 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.boot.model.relational;
 
 import org.hibernate.boot.model.naming.Identifier;
 
 /**
  * Models the qualified name of a database object.  Some things to keep in
  * mind wrt catalog/schema:<ol>
  *     <li>{@link java.sql.DatabaseMetaData#isCatalogAtStart}</li>
  *     <li>{@link java.sql.DatabaseMetaData#getCatalogSeparator()}</li>
  * </ol>
+ * <p/>
+ * Also, be careful about the usage of {@link #render}.  If the intention is get get the name
+ * as used in the database, the {@link org.hibernate.engine.jdbc.env.spi.JdbcEnvironment} ->
+ * {@link org.hibernate.engine.jdbc.env.spi.QualifiedObjectNameFormatter#format} should be
+ * used instead.
  *
  * @author Steve Ebersole
  */
 public interface QualifiedName {
 	Identifier getCatalogName();
 	Identifier getSchemaName();
 	Identifier getObjectName();
 
+	/**
+	 * Returns a String-form of the qualified name.
+	 * <p/>
+	 * Depending on intention, may not be appropriate.  May want
+	 * {@link org.hibernate.engine.jdbc.env.spi.QualifiedObjectNameFormatter#format}
+	 * instead.  See {@link org.hibernate.engine.jdbc.env.spi.JdbcEnvironment#getQualifiedObjectNameFormatter}
+	 *
+	 * @return The string form
+	 */
 	String render();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
index 77da722fda..92bd8623ff 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
@@ -1,878 +1,889 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.relational.Exportable;
 import org.hibernate.boot.model.relational.InitCommand;
 import org.hibernate.boot.model.relational.Namespace;
 import org.hibernate.boot.model.relational.QualifiedTableName;
 import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.env.spi.QualifiedObjectNameFormatter;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.tool.hbm2ddl.ColumnMetadata;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 import org.hibernate.tool.schema.extract.spi.ColumnInformation;
 import org.hibernate.tool.schema.extract.spi.TableInformation;
 
 import org.jboss.logging.Logger;
 
 /**
  * A relational table
  *
  * @author Gavin King
  */
 @SuppressWarnings("unchecked")
 public class Table implements RelationalModel, Serializable, Exportable {
 	private Identifier catalog;
 	private Identifier schema;
 	private Identifier name;
 
 	/**
 	 * contains all columns, including the primary key
 	 */
 	private Map columns = new LinkedHashMap();
 	private KeyValue idValue;
 	private PrimaryKey primaryKey;
 	private Map<ForeignKeyKey, ForeignKey> foreignKeys = new LinkedHashMap<ForeignKeyKey, ForeignKey>();
 	private Map<String, Index> indexes = new LinkedHashMap<String, Index>();
 	private Map<String,UniqueKey> uniqueKeys = new LinkedHashMap<String,UniqueKey>();
 	private int uniqueInteger;
 	private List<String> checkConstraints = new ArrayList<String>();
 	private String rowId;
 	private String subselect;
 	private boolean isAbstract;
 	private boolean hasDenormalizedTables;
 	private String comment;
 
 	private List<InitCommand> initCommands;
 
 	public Table() {
 	}
 
 	public Table(String name) {
 		setName( name );
 	}
 
 	public Table(
 			Namespace namespace,
 			Identifier physicalTableName,
 			boolean isAbstract) {
 		this.catalog = namespace.getPhysicalName().getCatalog();
 		this.schema = namespace.getPhysicalName().getSchema();
 		this.name = physicalTableName;
 		this.isAbstract = isAbstract;
 	}
 
 	public Table(
 			Identifier catalog,
 			Identifier schema,
 			Identifier physicalTableName,
 			boolean isAbstract) {
 		this.catalog = catalog;
 		this.schema = schema;
 		this.name = physicalTableName;
 		this.isAbstract = isAbstract;
 	}
 
 	public Table(Namespace namespace, Identifier physicalTableName, String subselect, boolean isAbstract) {
 		this.catalog = namespace.getPhysicalName().getCatalog();
 		this.schema = namespace.getPhysicalName().getSchema();
 		this.name = physicalTableName;
 		this.subselect = subselect;
 		this.isAbstract = isAbstract;
 	}
 
 	public Table(Namespace namespace, String subselect, boolean isAbstract) {
 		this.catalog = namespace.getPhysicalName().getCatalog();
 		this.schema = namespace.getPhysicalName().getSchema();
 		this.subselect = subselect;
 		this.isAbstract = isAbstract;
 	}
 
+	/**
+	 * @deprecated Should use {@link QualifiedObjectNameFormatter#format} on QualifiedObjectNameFormatter
+	 * obtained from {@link org.hibernate.engine.jdbc.env.spi.JdbcEnvironment}
+	 */
+	@Deprecated
 	public String getQualifiedName(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		if ( subselect != null ) {
 			return "( " + subselect + " )";
 		}
 		String quotedName = getQuotedName( dialect );
 		String usedSchema = schema == null ?
 				defaultSchema :
 				getQuotedSchema( dialect );
 		String usedCatalog = catalog == null ?
 				defaultCatalog :
 				getQuotedCatalog( dialect );
 		return qualify( usedCatalog, usedSchema, quotedName );
 	}
 
+	/**
+	 * @deprecated Should use {@link QualifiedObjectNameFormatter#format} on QualifiedObjectNameFormatter
+	 * obtained from {@link org.hibernate.engine.jdbc.env.spi.JdbcEnvironment}
+	 */
+	@Deprecated
 	public static String qualify(String catalog, String schema, String table) {
 		StringBuilder qualifiedName = new StringBuilder();
 		if ( catalog != null ) {
 			qualifiedName.append( catalog ).append( '.' );
 		}
 		if ( schema != null ) {
 			qualifiedName.append( schema ).append( '.' );
 		}
 		return qualifiedName.append( table ).toString();
 	}
 
 	public void setName(String name) {
 		this.name = Identifier.toIdentifier( name );
 	}
 
 	public String getName() {
 		return name == null ? null : name.getText();
 	}
 
 	public Identifier getNameIdentifier() {
 		return name;
 	}
 
 	public String getQuotedName() {
 		return name == null ? null : name.toString();
 	}
 
 	public String getQuotedName(Dialect dialect) {
 		return name == null ? null : name.render( dialect );
 	}
 
 	public QualifiedTableName getQualifiedTableName() {
 		return name == null ? null : new QualifiedTableName( catalog, schema, name );
 	}
 
 	public boolean isQuoted() {
 		return name.isQuoted();
 	}
 
 	public void setQuoted(boolean quoted) {
 		if ( quoted == name.isQuoted() ) {
 			return;
 		}
 		this.name = new Identifier( name.getText(), quoted );
 	}
 
 	public void setSchema(String schema) {
 		this.schema = Identifier.toIdentifier( schema );
 	}
 
 	public String getSchema() {
 		return schema == null ? null : schema.getText();
 	}
 
 	public String getQuotedSchema() {
 		return schema == null ? null : schema.toString();
 	}
 
 	public String getQuotedSchema(Dialect dialect) {
 		return schema == null ? null : schema.render( dialect );
 	}
 
 	public boolean isSchemaQuoted() {
 		return schema != null && schema.isQuoted();
 	}
 
 	public void setCatalog(String catalog) {
 		this.catalog = Identifier.toIdentifier( catalog );
 	}
 
 	public String getCatalog() {
 		return catalog == null ? null : catalog.getText();
 	}
 
 	public String getQuotedCatalog() {
 		return catalog == null ? null : catalog.render();
 	}
 
 	public String getQuotedCatalog(Dialect dialect) {
 		return catalog == null ? null : catalog.render( dialect );
 	}
 
 	public boolean isCatalogQuoted() {
 		return catalog != null && catalog.isQuoted();
 	}
 
 	/**
 	 * Return the column which is identified by column provided as argument.
 	 *
 	 * @param column column with atleast a name.
 	 * @return the underlying column or null if not inside this table. Note: the instance *can* be different than the input parameter, but the name will be the same.
 	 */
 	public Column getColumn(Column column) {
 		if ( column == null ) {
 			return null;
 		}
 
 		Column myColumn = (Column) columns.get( column.getCanonicalName() );
 
 		return column.equals( myColumn ) ?
 				myColumn :
 				null;
 	}
 
 	public Column getColumn(Identifier name) {
 		if ( name == null ) {
 			return null;
 		}
 
 		return (Column) columns.get( name.getCanonicalName() );
 	}
 
 	public Column getColumn(int n) {
 		Iterator iter = columns.values().iterator();
 		for ( int i = 0; i < n - 1; i++ ) {
 			iter.next();
 		}
 		return (Column) iter.next();
 	}
 
 	public void addColumn(Column column) {
 		Column old = getColumn( column );
 		if ( old == null ) {
 			columns.put( column.getCanonicalName(), column );
 			column.uniqueInteger = columns.size();
 		}
 		else {
 			column.uniqueInteger = old.uniqueInteger;
 		}
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
 	public Iterator getColumnIterator() {
 		return columns.values().iterator();
 	}
 
 	public Iterator<Index> getIndexIterator() {
 		return indexes.values().iterator();
 	}
 
 	public Iterator getForeignKeyIterator() {
 		return foreignKeys.values().iterator();
 	}
 
 	public Map<ForeignKeyKey, ForeignKey> getForeignKeys() {
 		return Collections.unmodifiableMap( foreignKeys );
 	}
 
 	public Iterator<UniqueKey> getUniqueKeyIterator() {
 		return getUniqueKeys().values().iterator();
 	}
 
 	Map<String, UniqueKey> getUniqueKeys() {
 		cleanseUniqueKeyMapIfNeeded();
 		return uniqueKeys;
 	}
 
 	private int sizeOfUniqueKeyMapOnLastCleanse;
 
 	private void cleanseUniqueKeyMapIfNeeded() {
 		if ( uniqueKeys.size() == sizeOfUniqueKeyMapOnLastCleanse ) {
 			// nothing to do
 			return;
 		}
 		cleanseUniqueKeyMap();
 		sizeOfUniqueKeyMapOnLastCleanse = uniqueKeys.size();
 	}
 
 	private void cleanseUniqueKeyMap() {
 		// We need to account for a few conditions here...
 		// 	1) If there are multiple unique keys contained in the uniqueKeys Map, we need to deduplicate
 		// 		any sharing the same columns as other defined unique keys; this is needed for the annotation
 		// 		processor since it creates unique constraints automagically for the user
 		//	2) Remove any unique keys that share the same columns as the primary key; again, this is
 		//		needed for the annotation processor to handle @Id @OneToOne cases.  In such cases the
 		//		unique key is unnecessary because a primary key is already unique by definition.  We handle
 		//		this case specifically because some databases fail if you try to apply a unique key to
 		//		the primary key columns which causes schema export to fail in these cases.
 		if ( uniqueKeys.isEmpty() ) {
 			// nothing to do
 			return;
 		}
 		else if ( uniqueKeys.size() == 1 ) {
 			// we have to worry about condition 2 above, but not condition 1
 			final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeys.entrySet().iterator().next();
 			if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 				uniqueKeys.remove( uniqueKeyEntry.getKey() );
 			}
 		}
 		else {
 			// we have to check both conditions 1 and 2
 			final Iterator<Map.Entry<String,UniqueKey>> uniqueKeyEntries = uniqueKeys.entrySet().iterator();
 			while ( uniqueKeyEntries.hasNext() ) {
 				final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeyEntries.next();
 				final UniqueKey uniqueKey = uniqueKeyEntry.getValue();
 				boolean removeIt = false;
 
 				// condition 1 : check against other unique keys
 				for ( UniqueKey otherUniqueKey : uniqueKeys.values() ) {
 					// make sure its not the same unique key
 					if ( uniqueKeyEntry.getValue() == otherUniqueKey ) {
 						continue;
 					}
 					if ( otherUniqueKey.getColumns().containsAll( uniqueKey.getColumns() )
 							&& uniqueKey.getColumns().containsAll( otherUniqueKey.getColumns() ) ) {
 						removeIt = true;
 						break;
 					}
 				}
 
 				// condition 2 : check against pk
 				if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 					removeIt = true;
 				}
 
 				if ( removeIt ) {
 					//uniqueKeys.remove( uniqueKeyEntry.getKey() );
 					uniqueKeyEntries.remove();
 				}
 			}
 
 		}
 	}
 
 	private boolean isSameAsPrimaryKeyColumns(UniqueKey uniqueKey) {
 		if ( primaryKey == null || ! primaryKey.columnIterator().hasNext() ) {
 			// happens for many-to-many tables
 			return false;
 		}
 		return primaryKey.getColumns().containsAll( uniqueKey.getColumns() )
 				&& uniqueKey.getColumns().containsAll( primaryKey.getColumns() );
 	}
 
 	@Override
 	public int hashCode() {
 		final int prime = 31;
 		int result = 1;
 		result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
 		result = prime * result + ((name == null) ? 0 : name.hashCode());
 		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
 		return result;
 	}
 
 	@Override
 	public boolean equals(Object object) {
 		return object instanceof Table && equals((Table) object);
 	}
 
 	public boolean equals(Table table) {
 		if (null == table) {
 			return false;
 		}
 		if (this == table) {
 			return true;
 		}
 
 		return Identifier.areEqual( name, table.name )
 				&& Identifier.areEqual( schema, table.schema )
 				&& Identifier.areEqual( catalog, table.catalog );
 	}
 	
 	public void validateColumns(Dialect dialect, Mapping mapping, TableMetadata tableInfo) {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			ColumnMetadata columnInfo = tableInfo.getColumnMetadata( col.getName() );
 
 			if ( columnInfo == null ) {
 				throw new HibernateException( "Missing column: " + col.getName() + " in " + Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()));
 			}
 			else {
 				final boolean typesMatch = col.getSqlType( dialect, mapping ).toLowerCase(Locale.ROOT)
 						.startsWith( columnInfo.getTypeName().toLowerCase(Locale.ROOT) )
 						|| columnInfo.getTypeCode() == col.getSqlTypeCode( mapping );
 				if ( !typesMatch ) {
 					throw new HibernateException(
 							"Wrong column type in " +
 							Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()) +
 							" for column " + col.getName() +
 							". Found: " + columnInfo.getTypeName().toLowerCase(Locale.ROOT) +
 							", expected: " + col.getSqlType( dialect, mapping )
 					);
 				}
 			}
 		}
 
 	}
 
 	public Iterator sqlAlterStrings(
 			Dialect dialect,
 			Mapping p,
 			TableInformation tableInfo,
 			String defaultCatalog,
 			String defaultSchema) throws HibernateException {
 
 		StringBuilder root = new StringBuilder( "alter table " )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( ' ' )
 				.append( dialect.getAddColumnString() );
 
 		Iterator iter = getColumnIterator();
 		List results = new ArrayList();
 		
 		while ( iter.hasNext() ) {
 			final Column column = (Column) iter.next();
 			final ColumnInformation columnInfo = tableInfo.getColumn( Identifier.toIdentifier( column.getName(), column.isQuoted() ) );
 
 			if ( columnInfo == null ) {
 				// the column doesnt exist at all.
 				StringBuilder alter = new StringBuilder( root.toString() )
 						.append( ' ' )
 						.append( column.getQuotedName( dialect ) )
 						.append( ' ' )
 						.append( column.getSqlType( dialect, p ) );
 
 				String defaultValue = column.getDefaultValue();
 				if ( defaultValue != null ) {
 					alter.append( " default " ).append( defaultValue );
 				}
 
 				if ( column.isNullable() ) {
 					alter.append( dialect.getNullColumnString() );
 				}
 				else {
 					alter.append( " not null" );
 				}
 
 				if ( column.isUnique() ) {
 					String keyName = Constraint.generateName( "UK_", this, column );
 					UniqueKey uk = getOrCreateUniqueKey( keyName );
 					uk.addColumn( column );
 					alter.append( dialect.getUniqueDelegate()
 							.getColumnDefinitionUniquenessFragment( column ) );
 				}
 
 				if ( column.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 					alter.append( " check(" )
 							.append( column.getCheckConstraint() )
 							.append( ")" );
 				}
 
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					alter.append( dialect.getColumnComment( columnComment ) );
 				}
 
 				alter.append( dialect.getAddColumnSuffixString() );
 
 				results.add( alter.toString() );
 			}
 
 		}
 
 		if ( results.isEmpty() ) {
 			Logger.getLogger( SchemaUpdate.class ).debugf( "No alter strings for table : %s", getQuotedName() );
 		}
 
 		return results.iterator();
 	}
 
 	public boolean hasPrimaryKey() {
 		return getPrimaryKey() != null;
 	}
 
 	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
 		StringBuilder buf = new StringBuilder( hasPrimaryKey() ? dialect.getCreateTableString() : dialect.getCreateMultisetTableString() )
 				.append( ' ' )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( " (" );
 
 		boolean identityColumn = idValue != null && idValue.isIdentityColumn( p.getIdentifierGeneratorFactory(), dialect );
 
 		// Try to find out the name of the primary key to create it as identity if the IdentityGenerator is used
 		String pkname = null;
 		if ( hasPrimaryKey() && identityColumn ) {
 			pkname = ( (Column) getPrimaryKey().getColumnIterator().next() ).getQuotedName( dialect );
 		}
 
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			buf.append( col.getQuotedName( dialect ) )
 					.append( ' ' );
 
 			if ( identityColumn && col.getQuotedName( dialect ).equals( pkname ) ) {
 				// to support dialects that have their own identity data type
 				if ( dialect.getIdentityColumnSupport().hasDataTypeInIdentityColumn() ) {
 					buf.append( col.getSqlType( dialect, p ) );
 				}
 				buf.append( ' ' )
 						.append( dialect.getIdentityColumnSupport().getIdentityColumnString( col.getSqlTypeCode( p ) ) );
 			}
 			else {
 
 				buf.append( col.getSqlType( dialect, p ) );
 
 				String defaultValue = col.getDefaultValue();
 				if ( defaultValue != null ) {
 					buf.append( " default " ).append( defaultValue );
 				}
 
 				if ( col.isNullable() ) {
 					buf.append( dialect.getNullColumnString() );
 				}
 				else {
 					buf.append( " not null" );
 				}
 
 			}
 			
 			if ( col.isUnique() ) {
 				String keyName = Constraint.generateName( "UK_", this, col );
 				UniqueKey uk = getOrCreateUniqueKey( keyName );
 				uk.addColumn( col );
 				buf.append( dialect.getUniqueDelegate()
 						.getColumnDefinitionUniquenessFragment( col ) );
 			}
 				
 			if ( col.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 				buf.append( " check (" )
 						.append( col.getCheckConstraint() )
 						.append( ")" );
 			}
 
 			String columnComment = col.getComment();
 			if ( columnComment != null ) {
 				buf.append( dialect.getColumnComment( columnComment ) );
 			}
 
 			if ( iter.hasNext() ) {
 				buf.append( ", " );
 			}
 
 		}
 		if ( hasPrimaryKey() ) {
 			buf.append( ", " )
 					.append( getPrimaryKey().sqlConstraintString( dialect ) );
 		}
 
 		buf.append( dialect.getUniqueDelegate().getTableCreationUniqueConstraintsFragment( this ) );
 
 		if ( dialect.supportsTableCheck() ) {
 			for ( String checkConstraint : checkConstraints ) {
 				buf.append( ", check (" )
 						.append( checkConstraint )
 						.append( ')' );
 			}
 		}
 
 		buf.append( ')' );
 
 		if ( comment != null ) {
 			buf.append( dialect.getTableComment( comment ) );
 		}
 
 		return buf.append( dialect.getTableTypeString() ).toString();
 	}
 
 	public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		return dialect.getDropTableString( getQualifiedName( dialect, defaultCatalog, defaultSchema ) );
 	}
 
 	public PrimaryKey getPrimaryKey() {
 		return primaryKey;
 	}
 
 	public void setPrimaryKey(PrimaryKey primaryKey) {
 		this.primaryKey = primaryKey;
 	}
 
 	public Index getOrCreateIndex(String indexName) {
 
 		Index index =  indexes.get( indexName );
 
 		if ( index == null ) {
 			index = new Index();
 			index.setName( indexName );
 			index.setTable( this );
 			indexes.put( indexName, index );
 		}
 
 		return index;
 	}
 
 	public Index getIndex(String indexName) {
 		return  indexes.get( indexName );
 	}
 
 	public Index addIndex(Index index) {
 		Index current =  indexes.get( index.getName() );
 		if ( current != null ) {
 			throw new MappingException( "Index " + index.getName() + " already exists!" );
 		}
 		indexes.put( index.getName(), index );
 		return index;
 	}
 
 	public UniqueKey addUniqueKey(UniqueKey uniqueKey) {
 		UniqueKey current = uniqueKeys.get( uniqueKey.getName() );
 		if ( current != null ) {
 			throw new MappingException( "UniqueKey " + uniqueKey.getName() + " already exists!" );
 		}
 		uniqueKeys.put( uniqueKey.getName(), uniqueKey );
 		return uniqueKey;
 	}
 
 	public UniqueKey createUniqueKey(List keyColumns) {
 		String keyName = Constraint.generateName( "UK_", this, keyColumns );
 		UniqueKey uk = getOrCreateUniqueKey( keyName );
 		uk.addColumns( keyColumns.iterator() );
 		return uk;
 	}
 
 	public UniqueKey getUniqueKey(String keyName) {
 		return uniqueKeys.get( keyName );
 	}
 
 	public UniqueKey getOrCreateUniqueKey(String keyName) {
 		UniqueKey uk = uniqueKeys.get( keyName );
 
 		if ( uk == null ) {
 			uk = new UniqueKey();
 			uk.setName( keyName );
 			uk.setTable( this );
 			uniqueKeys.put( keyName, uk );
 		}
 		return uk;
 	}
 
 	public void createForeignKeys() {
 	}
 
 	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName) {
 		return createForeignKey( keyName, keyColumns, referencedEntityName, null );
 	}
 
 	public ForeignKey createForeignKey(
 			String keyName,
 			List keyColumns,
 			String referencedEntityName,
 			List referencedColumns) {
 		final ForeignKeyKey key = new ForeignKeyKey( keyColumns, referencedEntityName, referencedColumns );
 
 		ForeignKey fk = foreignKeys.get( key );
 		if ( fk == null ) {
 			fk = new ForeignKey();
 			fk.setTable( this );
 			fk.setReferencedEntityName( referencedEntityName );
 			fk.addColumns( keyColumns.iterator() );
 			if ( referencedColumns != null ) {
 				fk.addReferencedColumns( referencedColumns.iterator() );
 			}
 
 			// NOTE : if the name is null, we will generate an implicit name during second pass processing
 			// after we know the referenced table name (which might not be resolved yet).
 			fk.setName( keyName );
 
 			foreignKeys.put( key, fk );
 		}
 
 		if ( keyName != null ) {
 			fk.setName( keyName );
 		}
 
 		return fk;
 	}
 
 
 	// This must be done outside of Table, rather than statically, to ensure
 	// deterministic alias names.  See HHH-2448.
 	public void setUniqueInteger( int uniqueInteger ) {
 		this.uniqueInteger = uniqueInteger;
 	}
 
 	public int getUniqueInteger() {
 		return uniqueInteger;
 	}
 
 	public void setIdentifierValue(KeyValue idValue) {
 		this.idValue = idValue;
 	}
 
 	public KeyValue getIdentifierValue() {
 		return idValue;
 	}
 
 	public void addCheckConstraint(String constraint) {
 		checkConstraints.add( constraint );
 	}
 
 	public boolean containsColumn(Column column) {
 		return columns.containsValue( column );
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public String toString() {
 		StringBuilder buf = new StringBuilder().append( getClass().getName() )
 				.append( '(' );
 		if ( getCatalog() != null ) {
 			buf.append( getCatalog() ).append( "." );
 		}
 		if ( getSchema() != null ) {
 			buf.append( getSchema() ).append( "." );
 		}
 		buf.append( getName() ).append( ')' );
 		return buf.toString();
 	}
 
 	public String getSubselect() {
 		return subselect;
 	}
 
 	public void setSubselect(String subselect) {
 		this.subselect = subselect;
 	}
 
 	public boolean isSubselect() {
 		return subselect != null;
 	}
 
 	public boolean isAbstractUnionTable() {
 		return hasDenormalizedTables() && isAbstract;
 	}
 
 	public boolean hasDenormalizedTables() {
 		return hasDenormalizedTables;
 	}
 
 	void setHasDenormalizedTables() {
 		hasDenormalizedTables = true;
 	}
 
 	public void setAbstract(boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public boolean isPhysicalTable() {
 		return !isSubselect() && !isAbstractUnionTable();
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
 
 	public Iterator<String> getCheckConstraintsIterator() {
 		return checkConstraints.iterator();
 	}
 
 	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		List comments = new ArrayList();
 		if ( dialect.supportsCommentOn() ) {
 			String tableName = getQualifiedName( dialect, defaultCatalog, defaultSchema );
 			if ( comment != null ) {
 				comments.add( "comment on table " + tableName + " is '" + comment + "'" );
 			}
 			Iterator iter = getColumnIterator();
 			while ( iter.hasNext() ) {
 				Column column = (Column) iter.next();
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					comments.add( "comment on column " + tableName + '.' + column.getQuotedName( dialect ) + " is '" + columnComment + "'" );
 				}
 			}
 		}
 		return comments.iterator();
 	}
 
 	@Override
 	public String getExportIdentifier() {
 		return Table.qualify(
 				render( catalog ),
 				render( schema ),
 				name.render()
 		);
 	}
 
 	private String render(Identifier identifier) {
 		return identifier == null ? null : identifier.render();
 	}
 
 
 	public static class ForeignKeyKey implements Serializable {
 		String referencedClassName;
 		List columns;
 		List referencedColumns;
 
 		ForeignKeyKey(List columns, String referencedClassName, List referencedColumns) {
 			this.referencedClassName = referencedClassName;
 			this.columns = new ArrayList();
 			this.columns.addAll( columns );
 			if ( referencedColumns != null ) {
 				this.referencedColumns = new ArrayList();
 				this.referencedColumns.addAll( referencedColumns );
 			}
 			else {
 				this.referencedColumns = Collections.EMPTY_LIST;
 			}
 		}
 
 		public int hashCode() {
 			return columns.hashCode() + referencedColumns.hashCode();
 		}
 
 		public boolean equals(Object other) {
 			ForeignKeyKey fkk = (ForeignKeyKey) other;
 			return fkk.columns.equals( columns ) &&
 					fkk.referencedClassName.equals( referencedClassName ) && fkk.referencedColumns
 					.equals( referencedColumns );
 		}
 
 		@Override
 		public String toString() {
 			return "ForeignKeyKey{" +
 					"columns=" + StringHelper.join( ",", columns ) +
 					", referencedClassName='" + referencedClassName + '\'' +
 					", referencedColumns=" + StringHelper.join( ",", referencedColumns ) +
 					'}';
 		}
 	}
 
 	public void addInitCommand(InitCommand command) {
 		if ( initCommands == null ) {
 			initCommands = new ArrayList<InitCommand>();
 		}
 		initCommands.add( command );
 	}
 
 	public List<InitCommand> getInitCommands() {
 		if ( initCommands == null ) {
 			return Collections.emptyList();
 		}
 		else {
 			return Collections.unmodifiableList( initCommands );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 424154a1e1..61631bb958 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,1587 +1,1599 @@
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
+import org.hibernate.boot.model.relational.Database;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StructuredCollectionCacheEntry;
 import org.hibernate.cache.spi.entry.StructuredMapCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
+import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
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
 
+		final Database database = creationContext.getMetadata().getDatabase();
+		final JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
+
 		this.factory = creationContext.getSessionFactory();
 		this.cacheAccessStrategy = cacheAccessStrategy;
-		if ( factory.getSettings().isStructuredCacheEntriesEnabled() ) {
+		if ( factory.getSessionFactoryOptions().isStructuredCacheEntriesEnabled() ) {
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
 
-		qualifiedTableName = table.getQualifiedName(
-				dialect,
-				factory.getSettings().getDefaultCatalogName(),
-				factory.getSettings().getDefaultSchemaName()
-				);
+		qualifiedTableName = determineTableName( table, jdbcEnvironment );
 
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
-			batch = factory.getSettings().getDefaultBatchFetchSize();
+			batch = factory.getSessionFactoryOptions().getDefaultBatchFetchSize();
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
 
+	protected String determineTableName(Table table, JdbcEnvironment jdbcEnvironment) {
+		if ( table.getSubselect() != null ) {
+			return "( " + table.getSubselect() + " )";
+		}
+
+		return jdbcEnvironment.getQualifiedObjectNameFormatter().format(
+				table.getQualifiedTableName(),
+				jdbcEnvironment.getDialect()
+		);
+	}
+
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
 	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 		getAppropriateInitializer( key, session ).initialize( key, session );
 	}
 
 	protected CollectionInitializer getAppropriateInitializer(Serializable key, SessionImplementor session) {
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
 
 	private CollectionInitializer getSubselectInitializer(Serializable key, SessionImplementor session) {
 
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
 
 	protected abstract CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session);
 
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
 	public Object readElement(ResultSet rs, Object owner, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		return getElementType().nullSafeGet( rs, aliases, session, owner );
 	}
 
 	@Override
 	public Object readIndex(ResultSet rs, String[] aliases, SessionImplementor session)
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
 	public Object readIdentifier(ResultSet rs, String alias, SessionImplementor session)
 			throws HibernateException, SQLException {
 		Object id = getIdentifierType().nullSafeGet( rs, alias, session, null );
 		if ( id == null ) {
 			throw new HibernateException( "null identifier column for collection: " + role );
 		}
 		return id;
 	}
 
 	@Override
 	public Object readKey(ResultSet rs, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		return getKeyType().nullSafeGet( rs, aliases, session, null );
 	}
 
 	/**
 	 * Write the key to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeKey(PreparedStatement st, Serializable key, int i, SessionImplementor session)
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
 	protected int writeElement(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getElementType().nullSafeSet( st, elt, i, elementColumnIsSettable, session );
 		return i + ArrayHelper.countTrue( elementColumnIsSettable );
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndex(PreparedStatement st, Object index, int i, SessionImplementor session)
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
 	protected int writeElementToWhere(PreparedStatement st, Object elt, int i, SessionImplementor session)
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
 	protected int writeIndexToWhere(PreparedStatement st, Object index, int i, SessionImplementor session)
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
 	public int writeIdentifier(PreparedStatement st, Object id, int i, SessionImplementor session)
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
 	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
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
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
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
 	public void deleteRows(PersistentCollection collection, Serializable id, SessionImplementor session)
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
 	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 05f969ce3a..ecfe0cff93 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1094 +1,1096 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoader;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StructuredCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.CacheHelper;
 import org.hibernate.engine.internal.ImmutableEntityEntryFactory;
 import org.hibernate.engine.internal.MutableEntityEntryFactory;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
+import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext.NaturalIdHelper;
 import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 import org.hibernate.engine.spi.PersistentAttributeInterceptor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.jdbc.TooManyRowsAffectedException;
 import org.hibernate.loader.entity.BatchingEntityLoaderBuilder;
 import org.hibernate.loader.entity.CascadeEntityLoader;
 import org.hibernate.loader.entity.EntityLoader;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
+import org.hibernate.mapping.Table;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.internal.EntityIdentifierDefinitionHelper;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.access.internal.PropertyAccessStrategyBackRefImpl;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.Update;
 import org.hibernate.tuple.GenerationTiming;
 import org.hibernate.tuple.InDatabaseValueGenerationStrategy;
 import org.hibernate.tuple.InMemoryValueGenerationStrategy;
 import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.tuple.ValueGeneration;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.hibernate.type.VersionType;
 
 /**
  * Basic functionality for persisting an entity via JDBC
  * through either generated or custom SQL
  *
  * @author Gavin King
  */
 public abstract class AbstractEntityPersister
 		implements OuterJoinLoadable, Queryable, ClassMetadata, UniqueKeyLoadable,
 				SQLLoadable, LazyPropertyInitializer, PostInsertIdentityPersister, Lockable {
 
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( AbstractEntityPersister.class );
 
 	public static final String ENTITY_CLASS = "class";
 
 	// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final SessionFactoryImplementor factory;
 	private final EntityRegionAccessStrategy cacheAccessStrategy;
 	private final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy;
 	private final boolean isLazyPropertiesCacheable;
 	private final CacheEntryHelper cacheEntryHelper;
 	private final EntityMetamodel entityMetamodel;
 	private final EntityTuplizer entityTuplizer;
 	private final EntityEntryFactory entityEntryFactory;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private final String[] rootTableKeyColumnNames;
 	private final String[] rootTableKeyColumnReaders;
 	private final String[] rootTableKeyColumnReaderTemplates;
 	private final String[] identifierAliases;
 	private final int identifierColumnSpan;
 	private final String versionColumnName;
 	private final boolean hasFormulaProperties;
 	protected final int batchSize;
 	private final boolean hasSubselectLoadableCollections;
 	protected final String rowIdName;
 
 	private final Set lazyProperties;
 
 	// The optional SQL string defined in the where attribute
 	private final String sqlWhereString;
 	private final String sqlWhereStringTemplate;
 
 	//information about properties of this class,
 	//including inherited properties
 	//(only really needed for updatable/insertable properties)
 	private final int[] propertyColumnSpans;
 	private final String[] propertySubclassNames;
 	private final String[][] propertyColumnAliases;
 	private final String[][] propertyColumnNames;
 	private final String[][] propertyColumnFormulaTemplates;
 	private final String[][] propertyColumnReaderTemplates;
 	private final String[][] propertyColumnWriters;
 	private final boolean[][] propertyColumnUpdateable;
 	private final boolean[][] propertyColumnInsertable;
 	private final boolean[] propertyUniqueness;
 	private final boolean[] propertySelectable;
 
 	private final List<Integer> lobProperties = new ArrayList<Integer>();
 
 	//information about lazy properties of this class
 	private final String[] lazyPropertyNames;
 	private final int[] lazyPropertyNumbers;
 	private final Type[] lazyPropertyTypes;
 	private final String[][] lazyPropertyColumnAliases;
 
 	//information about all properties in class hierarchy
 	private final String[] subclassPropertyNameClosure;
 	private final String[] subclassPropertySubclassNameClosure;
 	private final Type[] subclassPropertyTypeClosure;
 	private final String[][] subclassPropertyFormulaTemplateClosure;
 	private final String[][] subclassPropertyColumnNameClosure;
 	private final String[][] subclassPropertyColumnReaderClosure;
 	private final String[][] subclassPropertyColumnReaderTemplateClosure;
 	private final FetchMode[] subclassPropertyFetchModeClosure;
 	private final boolean[] subclassPropertyNullabilityClosure;
 	private final boolean[] propertyDefinedOnSubclass;
 	private final int[][] subclassPropertyColumnNumberClosure;
 	private final int[][] subclassPropertyFormulaNumberClosure;
 	private final CascadeStyle[] subclassPropertyCascadeStyleClosure;
 
 	//information about all columns/formulas in class hierarchy
 	private final String[] subclassColumnClosure;
 	private final boolean[] subclassColumnLazyClosure;
 	private final String[] subclassColumnAliasClosure;
 	private final boolean[] subclassColumnSelectableClosure;
 	private final String[] subclassColumnReaderTemplateClosure;
 	private final String[] subclassFormulaClosure;
 	private final String[] subclassFormulaTemplateClosure;
 	private final String[] subclassFormulaAliasClosure;
 	private final boolean[] subclassFormulaLazyClosure;
 
 	// dynamic filters attached to the class-level
 	private final FilterHelper filterHelper;
 
 	private final Set<String> affectingFetchProfileNames = new HashSet<String>();
 
 	private final Map uniqueKeyLoaders = new HashMap();
 	private final Map lockers = new HashMap();
 	private final Map loaders = new HashMap();
 
 	// SQL strings
 	private String sqlVersionSelectString;
 	private String sqlSnapshotSelectString;
 	private String sqlLazySelectString;
 
 	private String sqlIdentityInsertString;
 	private String sqlUpdateByRowIdString;
 	private String sqlLazyUpdateByRowIdString;
 
 	private String[] sqlDeleteStrings;
 	private String[] sqlInsertStrings;
 	private String[] sqlUpdateStrings;
 	private String[] sqlLazyUpdateStrings;
 
 	private String sqlInsertGeneratedValuesSelectString;
 	private String sqlUpdateGeneratedValuesSelectString;
 
 	//Custom SQL (would be better if these were private)
 	protected boolean[] insertCallable;
 	protected boolean[] updateCallable;
 	protected boolean[] deleteCallable;
 	protected String[] customSQLInsert;
 	protected String[] customSQLUpdate;
 	protected String[] customSQLDelete;
 	protected ExecuteUpdateResultCheckStyle[] insertResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] updateResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] deleteResultCheckStyles;
 
 	private InsertGeneratedIdentifierDelegate identityDelegate;
 
 	private boolean[] tableHasColumns;
 
 	private final String loaderName;
 
 	private UniqueEntityLoader queryLoader;
 
 	private final Map subclassPropertyAliases = new HashMap();
 	private final Map subclassPropertyColumnNames = new HashMap();
 
 	protected final BasicEntityPropertyMapping propertyMapping;
 
 	private final boolean useReferenceCacheEntries;
 
 	protected void addDiscriminatorToInsert(Insert insert) {
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 	}
 
 	protected abstract int[] getSubclassColumnTableNumberClosure();
 
 	protected abstract int[] getSubclassFormulaTableNumberClosure();
 
 	public abstract String getSubclassTableName(int j);
 
 	protected abstract String[] getSubclassTableKeyColumns(int j);
 
 	protected abstract boolean isClassOrSuperclassTable(int j);
 
 	protected abstract int getSubclassTableSpan();
 
 	protected abstract int getTableSpan();
 
 	protected abstract boolean isTableCascadeDeleteEnabled(int j);
 
 	protected abstract String getTableName(int j);
 
 	protected abstract String[] getKeyColumns(int j);
 
 	protected abstract boolean isPropertyOfTable(int property, int j);
 
 	protected abstract int[] getPropertyTableNumbersInSelect();
 
 	protected abstract int[] getPropertyTableNumbers();
 
 	protected abstract int getSubclassPropertyTableNumber(int i);
 
 	protected abstract String filterFragment(String alias) throws MappingException;
 
 	protected abstract String filterFragment(String alias, Set<String> treatAsDeclarations);
 
 	private static final String DISCRIMINATOR_ALIAS = "clazz_";
 
 	public String getDiscriminatorColumnName() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	public String getDiscriminatorColumnReaders() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	public String getDiscriminatorColumnReaderTemplate() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	protected String getDiscriminatorAlias() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	protected String getDiscriminatorFormulaTemplate() {
 		return null;
 	}
 
 	protected boolean isInverseTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableSubclassTable(int j) {
 		return false;
 	}
 
 	protected boolean isInverseSubclassTable(int j) {
 		return false;
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return entityMetamodel.getSubclassEntityNames().contains( entityName );
 	}
 
 	private boolean[] getTableHasColumns() {
 		return tableHasColumns;
 	}
 
 	public String[] getRootTableKeyColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	protected String[] getSQLUpdateByRowIdStrings() {
 		if ( sqlUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan() + 1];
 		result[0] = sqlUpdateByRowIdString;
 		System.arraycopy( sqlUpdateStrings, 0, result, 1, getTableSpan() );
 		return result;
 	}
 
 	protected String[] getSQLLazyUpdateByRowIdStrings() {
 		if ( sqlLazyUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan()];
 		result[0] = sqlLazyUpdateByRowIdString;
 		System.arraycopy( sqlLazyUpdateStrings, 1, result, 1, getTableSpan() - 1 );
 		return result;
 	}
 
 	protected String getSQLSnapshotSelectString() {
 		return sqlSnapshotSelectString;
 	}
 
 	protected String getSQLLazySelectString() {
 		return sqlLazySelectString;
 	}
 
 	protected String[] getSQLDeleteStrings() {
 		return sqlDeleteStrings;
 	}
 
 	protected String[] getSQLInsertStrings() {
 		return sqlInsertStrings;
 	}
 
 	protected String[] getSQLUpdateStrings() {
 		return sqlUpdateStrings;
 	}
 
 	protected String[] getSQLLazyUpdateStrings() {
 		return sqlLazyUpdateStrings;
 	}
 
 	/**
 	 * The query that inserts a row, letting the database generate an id
 	 *
 	 * @return The IDENTITY-based insertion query.
 	 */
 	protected String getSQLIdentityInsertString() {
 		return sqlIdentityInsertString;
 	}
 
 	protected String getVersionSelectString() {
 		return sqlVersionSelectString;
 	}
 
 	protected boolean isInsertCallable(int j) {
 		return insertCallable[j];
 	}
 
 	protected boolean isUpdateCallable(int j) {
 		return updateCallable[j];
 	}
 
 	protected boolean isDeleteCallable(int j) {
 		return deleteCallable[j];
 	}
 
 	protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
 		return false;
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return false;
 	}
 
 	public boolean hasSequentialSelect() {
 		return false;
 	}
 
 	/**
 	 * Decide which tables need to be updated.
 	 * <p/>
 	 * The return here is an array of boolean values with each index corresponding
 	 * to a given table in the scope of this persister.
 	 *
 	 * @param dirtyProperties The indices of all the entity properties considered dirty.
 	 * @param hasDirtyCollection Whether any collections owned by the entity which were considered dirty.
 	 *
 	 * @return Array of booleans indicating which table require updating.
 	 */
 	protected boolean[] getTableUpdateNeeded(final int[] dirtyProperties, boolean hasDirtyCollection) {
 
 		if ( dirtyProperties == null ) {
 			return getTableHasColumns(); // for objects that came in via update()
 		}
 		else {
 			boolean[] updateability = getPropertyUpdateability();
 			int[] propertyTableNumbers = getPropertyTableNumbers();
 			boolean[] tableUpdateNeeded = new boolean[getTableSpan()];
 			for ( int property : dirtyProperties ) {
 				int table = propertyTableNumbers[property];
 				tableUpdateNeeded[table] = tableUpdateNeeded[table] ||
 						( getPropertyColumnSpan( property ) > 0 && updateability[property] );
 			}
 			if ( isVersioned() ) {
 				tableUpdateNeeded[0] = tableUpdateNeeded[0] ||
 						Versioning.isVersionIncrementRequired(
 								dirtyProperties,
 								hasDirtyCollection,
 								getPropertyVersionability()
 						);
 			}
 			return tableUpdateNeeded;
 		}
 	}
 
 	public boolean hasRowId() {
 		return rowIdName != null;
 	}
 
 	protected boolean[][] getPropertyColumnUpdateable() {
 		return propertyColumnUpdateable;
 	}
 
 	protected boolean[][] getPropertyColumnInsertable() {
 		return propertyColumnInsertable;
 	}
 
 	protected boolean[] getPropertySelectable() {
 		return propertySelectable;
 	}
 
 	@SuppressWarnings("UnnecessaryBoxing")
 	public AbstractEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final PersisterCreationContext creationContext) throws HibernateException {
 
 		// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		this.factory = creationContext.getSessionFactory();
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		this.naturalIdRegionAccessStrategy = naturalIdRegionAccessStrategy;
 		isLazyPropertiesCacheable = persistentClass.isLazyPropertiesCacheable();
 
 		this.entityMetamodel = new EntityMetamodel( persistentClass, this, factory );
 		this.entityTuplizer = this.entityMetamodel.getTuplizer();
 
 		if ( entityMetamodel.isMutable() ) {
 			this.entityEntryFactory = MutableEntityEntryFactory.INSTANCE;
 		}
 		else {
 			this.entityEntryFactory = ImmutableEntityEntryFactory.INSTANCE;
 		}
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		int batch = persistentClass.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSessionFactoryOptions().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 		hasSubselectLoadableCollections = persistentClass.hasSubselectLoadableCollections();
 
 		propertyMapping = new BasicEntityPropertyMapping( this );
 
 		// IDENTIFIER
 
 		identifierColumnSpan = persistentClass.getIdentifier().getColumnSpan();
 		rootTableKeyColumnNames = new String[identifierColumnSpan];
 		rootTableKeyColumnReaders = new String[identifierColumnSpan];
 		rootTableKeyColumnReaderTemplates = new String[identifierColumnSpan];
 		identifierAliases = new String[identifierColumnSpan];
 
 		rowIdName = persistentClass.getRootTable().getRowId();
 
 		loaderName = persistentClass.getLoaderName();
 
 		Iterator iter = persistentClass.getIdentifier().getColumnIterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 			rootTableKeyColumnNames[i] = col.getQuotedName( factory.getDialect() );
 			rootTableKeyColumnReaders[i] = col.getReadExpr( factory.getDialect() );
 			rootTableKeyColumnReaderTemplates[i] = col.getTemplate(
 					factory.getDialect(),
 					factory.getSqlFunctionRegistry()
 			);
 			identifierAliases[i] = col.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( persistentClass.isVersioned() ) {
 			versionColumnName = ( (Column) persistentClass.getVersion().getColumnIterator().next() ).getQuotedName(
 					factory.getDialect()
 			);
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( persistentClass.getWhere() ) ?
 				"( " + persistentClass.getWhere() + ") " :
 				null;
 		sqlWhereStringTemplate = sqlWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate(
 						sqlWhereString,
 						factory.getDialect(),
 						factory.getSqlFunctionRegistry()
 				);
 
 		// PROPERTIES
 
 		final boolean lazyAvailable = isInstrumented() || entityMetamodel.isLazyLoadingBytecodeEnhanced();
 
 		int hydrateSpan = entityMetamodel.getPropertySpan();
 		propertyColumnSpans = new int[hydrateSpan];
 		propertySubclassNames = new String[hydrateSpan];
 		propertyColumnAliases = new String[hydrateSpan][];
 		propertyColumnNames = new String[hydrateSpan][];
 		propertyColumnFormulaTemplates = new String[hydrateSpan][];
 		propertyColumnReaderTemplates = new String[hydrateSpan][];
 		propertyColumnWriters = new String[hydrateSpan][];
 		propertyUniqueness = new boolean[hydrateSpan];
 		propertySelectable = new boolean[hydrateSpan];
 		propertyColumnUpdateable = new boolean[hydrateSpan][];
 		propertyColumnInsertable = new boolean[hydrateSpan][];
 		HashSet thisClassProperties = new HashSet();
 
 		lazyProperties = new HashSet();
 		ArrayList lazyNames = new ArrayList();
 		ArrayList lazyNumbers = new ArrayList();
 		ArrayList lazyTypes = new ArrayList();
 		ArrayList lazyColAliases = new ArrayList();
 
 		iter = persistentClass.getPropertyClosureIterator();
 		i = 0;
 		boolean foundFormula = false;
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			thisClassProperties.add( prop );
 
 			int span = prop.getColumnSpan();
 			propertyColumnSpans[i] = span;
 			propertySubclassNames[i] = prop.getPersistentClass().getEntityName();
 			String[] colNames = new String[span];
 			String[] colAliases = new String[span];
 			String[] colReaderTemplates = new String[span];
 			String[] colWriters = new String[span];
 			String[] formulaTemplates = new String[span];
 			Iterator colIter = prop.getColumnIterator();
 			int k = 0;
 			while ( colIter.hasNext() ) {
 				Selectable thing = (Selectable) colIter.next();
 				colAliases[k] = thing.getAlias( factory.getDialect(), prop.getValue().getTable() );
 				if ( thing.isFormula() ) {
 					foundFormula = true;
 					formulaTemplates[k] = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				}
 				else {
 					Column col = (Column) thing;
 					colNames[k] = col.getQuotedName( factory.getDialect() );
 					colReaderTemplates[k] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					colWriters[k] = col.getWriteExpr();
 				}
 				k++;
 			}
 			propertyColumnNames[i] = colNames;
 			propertyColumnFormulaTemplates[i] = formulaTemplates;
 			propertyColumnReaderTemplates[i] = colReaderTemplates;
 			propertyColumnWriters[i] = colWriters;
 			propertyColumnAliases[i] = colAliases;
 
 			if ( lazyAvailable && prop.isLazy() ) {
 				lazyProperties.add( prop.getName() );
 				lazyNames.add( prop.getName() );
 				lazyNumbers.add( i );
 				lazyTypes.add( prop.getValue().getType() );
 				lazyColAliases.add( colAliases );
 			}
 
 			propertyColumnUpdateable[i] = prop.getValue().getColumnUpdateability();
 			propertyColumnInsertable[i] = prop.getValue().getColumnInsertability();
 
 			propertySelectable[i] = prop.isSelectable();
 
 			propertyUniqueness[i] = prop.getValue().isAlternateUniqueKey();
 
 			if ( prop.isLob() && getFactory().getDialect().forceLobAsLastValue() ) {
 				lobProperties.add( i );
 			}
 
 			i++;
 
 		}
 		hasFormulaProperties = foundFormula;
 		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
 		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
 		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
 		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );
 
 		// SUBCLASS PROPERTY CLOSURE
 
 		ArrayList columns = new ArrayList();
 		ArrayList columnsLazy = new ArrayList();
 		ArrayList columnReaderTemplates = new ArrayList();
 		ArrayList aliases = new ArrayList();
 		ArrayList formulas = new ArrayList();
 		ArrayList formulaAliases = new ArrayList();
 		ArrayList formulaTemplates = new ArrayList();
 		ArrayList formulasLazy = new ArrayList();
 		ArrayList types = new ArrayList();
 		ArrayList names = new ArrayList();
 		ArrayList classes = new ArrayList();
 		ArrayList templates = new ArrayList();
 		ArrayList propColumns = new ArrayList();
 		ArrayList propColumnReaders = new ArrayList();
 		ArrayList propColumnReaderTemplates = new ArrayList();
 		ArrayList joinedFetchesList = new ArrayList();
 		ArrayList cascades = new ArrayList();
 		ArrayList definedBySubclass = new ArrayList();
 		ArrayList propColumnNumbers = new ArrayList();
 		ArrayList propFormulaNumbers = new ArrayList();
 		ArrayList columnSelectables = new ArrayList();
 		ArrayList propNullables = new ArrayList();
 
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			names.add( prop.getName() );
 			classes.add( prop.getPersistentClass().getEntityName() );
 			boolean isDefinedBySubclass = !thisClassProperties.contains( prop );
 			definedBySubclass.add( Boolean.valueOf( isDefinedBySubclass ) );
 			propNullables.add( Boolean.valueOf( prop.isOptional() || isDefinedBySubclass ) ); //TODO: is this completely correct?
 			types.add( prop.getType() );
 
 			Iterator colIter = prop.getColumnIterator();
 			String[] cols = new String[prop.getColumnSpan()];
 			String[] readers = new String[prop.getColumnSpan()];
 			String[] readerTemplates = new String[prop.getColumnSpan()];
 			String[] forms = new String[prop.getColumnSpan()];
 			int[] colnos = new int[prop.getColumnSpan()];
 			int[] formnos = new int[prop.getColumnSpan()];
 			int l = 0;
 			Boolean lazy = Boolean.valueOf( prop.isLazy() && lazyAvailable );
 			while ( colIter.hasNext() ) {
 				Selectable thing = (Selectable) colIter.next();
 				if ( thing.isFormula() ) {
 					String template = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					formnos[l] = formulaTemplates.size();
 					colnos[l] = -1;
 					formulaTemplates.add( template );
 					forms[l] = template;
 					formulas.add( thing.getText( factory.getDialect() ) );
 					formulaAliases.add( thing.getAlias( factory.getDialect() ) );
 					formulasLazy.add( lazy );
 				}
 				else {
 					Column col = (Column) thing;
 					String colName = col.getQuotedName( factory.getDialect() );
 					colnos[l] = columns.size(); //before add :-)
 					formnos[l] = -1;
 					columns.add( colName );
 					cols[l] = colName;
 					aliases.add( thing.getAlias( factory.getDialect(), prop.getValue().getTable() ) );
 					columnsLazy.add( lazy );
 					columnSelectables.add( Boolean.valueOf( prop.isSelectable() ) );
 
 					readers[l] = col.getReadExpr( factory.getDialect() );
 					String readerTemplate = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					readerTemplates[l] = readerTemplate;
 					columnReaderTemplates.add( readerTemplate );
 				}
 				l++;
 			}
 			propColumns.add( cols );
 			propColumnReaders.add( readers );
 			propColumnReaderTemplates.add( readerTemplates );
 			templates.add( forms );
 			propColumnNumbers.add( colnos );
 			propFormulaNumbers.add( formnos );
 
 			joinedFetchesList.add( prop.getValue().getFetchMode() );
 			cascades.add( prop.getCascadeStyle() );
 		}
 		subclassColumnClosure = ArrayHelper.toStringArray( columns );
 		subclassColumnAliasClosure = ArrayHelper.toStringArray( aliases );
 		subclassColumnLazyClosure = ArrayHelper.toBooleanArray( columnsLazy );
 		subclassColumnSelectableClosure = ArrayHelper.toBooleanArray( columnSelectables );
 		subclassColumnReaderTemplateClosure = ArrayHelper.toStringArray( columnReaderTemplates );
 
 		subclassFormulaClosure = ArrayHelper.toStringArray( formulas );
 		subclassFormulaTemplateClosure = ArrayHelper.toStringArray( formulaTemplates );
 		subclassFormulaAliasClosure = ArrayHelper.toStringArray( formulaAliases );
 		subclassFormulaLazyClosure = ArrayHelper.toBooleanArray( formulasLazy );
 
 		subclassPropertyNameClosure = ArrayHelper.toStringArray( names );
 		subclassPropertySubclassNameClosure = ArrayHelper.toStringArray( classes );
 		subclassPropertyTypeClosure = ArrayHelper.toTypeArray( types );
 		subclassPropertyNullabilityClosure = ArrayHelper.toBooleanArray( propNullables );
 		subclassPropertyFormulaTemplateClosure = ArrayHelper.to2DStringArray( templates );
 		subclassPropertyColumnNameClosure = ArrayHelper.to2DStringArray( propColumns );
 		subclassPropertyColumnReaderClosure = ArrayHelper.to2DStringArray( propColumnReaders );
 		subclassPropertyColumnReaderTemplateClosure = ArrayHelper.to2DStringArray( propColumnReaderTemplates );
 		subclassPropertyColumnNumberClosure = ArrayHelper.to2DIntArray( propColumnNumbers );
 		subclassPropertyFormulaNumberClosure = ArrayHelper.to2DIntArray( propFormulaNumbers );
 
 		subclassPropertyCascadeStyleClosure = new CascadeStyle[cascades.size()];
 		iter = cascades.iterator();
 		int j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyCascadeStyleClosure[j++] = (CascadeStyle) iter.next();
 		}
 		subclassPropertyFetchModeClosure = new FetchMode[joinedFetchesList.size()];
 		iter = joinedFetchesList.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyFetchModeClosure[j++] = (FetchMode) iter.next();
 		}
 
 		propertyDefinedOnSubclass = new boolean[definedBySubclass.size()];
 		iter = definedBySubclass.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			propertyDefinedOnSubclass[j++] = (Boolean) iter.next();
 		}
 
 		// Handle any filters applied to the class level
 		filterHelper = new FilterHelper( persistentClass.getFilters(), factory );
 
 		// Check if we can use Reference Cached entities in 2lc
 		// todo : should really validate that the cache access type is read-only
 		boolean refCacheEntries = true;
 		if ( !factory.getSessionFactoryOptions().isDirectReferenceCacheEntriesEnabled() ) {
 			refCacheEntries = false;
 		}
 
 		// for now, limit this to just entities that:
 		// 		1) are immutable
 		if ( entityMetamodel.isMutable() ) {
 			refCacheEntries = false;
 		}
 
 		//		2)  have no associations.  Eventually we want to be a little more lenient with associations.
 		for ( Type type : getSubclassPropertyTypeClosure() ) {
 			if ( type.isAssociationType() ) {
 				refCacheEntries = false;
 			}
 		}
 
 		useReferenceCacheEntries = refCacheEntries;
 
 		this.cacheEntryHelper = buildCacheEntryHelper();
 
 	}
 
 	protected CacheEntryHelper buildCacheEntryHelper() {
 		if ( cacheAccessStrategy == null ) {
 			// the entity defined no caching...
 			return NoopCacheEntryHelper.INSTANCE;
 		}
 
 		if ( canUseReferenceCacheEntries() ) {
 			entityMetamodel.setLazy( false );
 			// todo : do we also need to unset proxy factory?
 			return new ReferenceCacheEntryHelper( this );
 		}
 
 		return factory.getSessionFactoryOptions().isStructuredCacheEntriesEnabled()
 				? new StructuredCacheEntryHelper( this )
 				: new StandardCacheEntryHelper( this );
 	}
 
 	public boolean canUseReferenceCacheEntries() {
 		return useReferenceCacheEntries;
 	}
 
 	protected static String getTemplateFromString(String string, SessionFactoryImplementor factory) {
 		return string == null ?
 				null :
 				Template.renderWhereStringTemplate( string, factory.getDialect(), factory.getSqlFunctionRegistry() );
 	}
 
 	protected String generateLazySelectString() {
 
 		if ( !entityMetamodel.hasLazyProperties() ) {
 			return null;
 		}
 
 		HashSet tableNumbers = new HashSet();
 		ArrayList columnNumbers = new ArrayList();
 		ArrayList formulaNumbers = new ArrayList();
 		for ( String lazyPropertyName : lazyPropertyNames ) {
 			// all this only really needs to consider properties
 			// of this class, not its subclasses, but since we
 			// are reusing code used for sequential selects, we
 			// use the subclass closure
 			int propertyNumber = getSubclassPropertyIndex( lazyPropertyName );
 
 			int tableNumber = getSubclassPropertyTableNumber( propertyNumber );
 			tableNumbers.add( tableNumber );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int colNumber : colNumbers ) {
 				if ( colNumber != -1 ) {
 					columnNumbers.add( colNumber );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int formNumber : formNumbers ) {
 				if ( formNumber != -1 ) {
 					formulaNumbers.add( formNumber );
 				}
 			}
 		}
 
 		if ( columnNumbers.size() == 0 && formulaNumbers.size() == 0 ) {
 			// only one-to-one is lazy fetched
 			return null;
 		}
 
 		return renderSelect(
 				ArrayHelper.toIntArray( tableNumbers ),
 				ArrayHelper.toIntArray( columnNumbers ),
 				ArrayHelper.toIntArray( formulaNumbers )
 		);
 
 	}
 
 	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session) {
 		final EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 
 		if ( hasCollections() ) {
 			final Type type = getPropertyType( fieldName );
 			if ( type.isCollectionType() ) {
 				// we have a condition where a collection attribute is being access via enhancement:
 				// 		we can circumvent all the rest and just return the PersistentCollection
 				final CollectionType collectionType = (CollectionType) type;
 				final CollectionPersister persister = factory.getCollectionPersister( collectionType.getRole() );
 
 				// Get/create the collection, and make sure it is initialized!  This initialized part is
 				// different from proxy-based scenarios where we have to create the PersistentCollection
 				// reference "ahead of time" to add as a reference to the proxy.  For bytecode solutions
 				// we are not creating the PersistentCollection ahead of time, but instead we are creating
 				// it on first request through the enhanced entity.
 
 				// see if there is already a collection instance associated with the session
 				// 		NOTE : can this ever happen?
 				final Serializable key = getCollectionKey( persister, entity, entry, session );
 				PersistentCollection collection = session.getPersistenceContext().getCollection( new CollectionKey( persister, key ) );
 				if ( collection == null ) {
 					collection = collectionType.instantiate( session, persister, key );
 					collection.setOwner( entity );
 					session.getPersistenceContext().addUninitializedCollection( persister, collection, key );
 				}
 
 				// Initialize it
 				session.initializeCollection( collection, false );
 
 				if ( collectionType.isArrayType() ) {
 					session.getPersistenceContext().addCollectionHolder( collection );
 				}
 
 				// EARLY EXIT!!!
 				return collection;
 			}
 		}
 
 		final Serializable id = session.getContextEntityIdentifier( entity );
 		if ( entry == null ) {
 			throw new HibernateException( "entity is not associated with the session: " + id );
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Initializing lazy properties of: {0}, field access: {1}", MessageHelper.infoString(
 							this,
 							id,
 							getFactory()
 					), fieldName
 			);
 		}
 
 		if ( session.getCacheMode().isGetEnabled() && hasCache() ) {
 			final EntityRegionAccessStrategy cache = getCacheAccessStrategy();
 			final Object cacheKey = cache.generateCacheKey(id, this, session.getFactory(), session.getTenantIdentifier() );
 			final Object ce = CacheHelper.fromSharedCache( session, cacheKey, cache );
 			if ( ce != null ) {
 				final CacheEntry cacheEntry = (CacheEntry) getCacheEntryStructure().destructure( ce, factory );
 				if ( !cacheEntry.areLazyPropertiesUnfetched() ) {
 					//note early exit here:
 					return initializeLazyPropertiesFromCache( fieldName, entity, session, entry, cacheEntry );
 				}
 			}
 		}
 
 		return initializeLazyPropertiesFromDatastore( fieldName, entity, session, id, entry );
 
 	}
 
 	protected Serializable getCollectionKey(
 			CollectionPersister persister,
 			Object owner,
 			EntityEntry ownerEntry,
 			SessionImplementor session) {
 		final CollectionType collectionType = persister.getCollectionType();
 
 		if ( ownerEntry != null ) {
 			// this call only works when the owner is associated with the Session, which is not always the case
 			return collectionType.getKeyOfOwner( owner, session );
 		}
 
 		if ( collectionType.getLHSPropertyName() == null ) {
 			// collection key is defined by the owning entity identifier
 			return persister.getOwnerEntityPersister().getIdentifier( owner, session );
 		}
 		else {
 			return (Serializable) persister.getOwnerEntityPersister().getPropertyValue( owner, collectionType.getLHSPropertyName() );
 		}
 	}
 
 	private Object initializeLazyPropertiesFromDatastore(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Serializable id,
 			final EntityEntry entry) {
 
 		if ( !hasLazyProperties() ) {
 			throw new AssertionFailure( "no lazy properties" );
 		}
 
 		LOG.trace( "Initializing lazy properties from datastore" );
 
 		try {
 
 			Object result = null;
 			PreparedStatement ps = null;
 			try {
 				final String lazySelect = getSQLLazySelectString();
 				ResultSet rs = null;
 				try {
 					if ( lazySelect != null ) {
 						// null sql means that the only lazy properties
 						// are shared PK one-to-one associations which are
 						// handled differently in the Type#nullSafeGet code...
 						ps = session.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( lazySelect );
 						getIdentifierType().nullSafeSet( ps, id, 1, session );
 						rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 						rs.next();
 					}
 					final Object[] snapshot = entry.getLoadedState();
 					for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 						Object propValue = lazyPropertyTypes[j].nullSafeGet(
 								rs,
 								lazyPropertyColumnAliases[j],
 								session,
 								entity
 						);
 						if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 							result = propValue;
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					session.getJdbcCoordinator().getResourceRegistry().release( ps );
 					session.getJdbcCoordinator().afterStatementExecution();
 				}
 			}
 
 			LOG.trace( "Done initializing lazy properties" );
 
 			return result;
 
 		}
 		catch (SQLException sqle) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize lazy properties: " +
 							MessageHelper.infoString( this, id, getFactory() ),
 					getSQLLazySelectString()
 			);
 		}
 	}
 
 	private Object initializeLazyPropertiesFromCache(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final EntityEntry entry,
 			final CacheEntry cacheEntry
 	) {
 
 		LOG.trace( "Initializing lazy properties from second-level cache" );
 
 		Object result = null;
 		Serializable[] disassembledValues = cacheEntry.getDisassembledState();
 		final Object[] snapshot = entry.getLoadedState();
 		for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 			final Object propValue = lazyPropertyTypes[j].assemble(
 					disassembledValues[lazyPropertyNumbers[j]],
 					session,
 					entity
 			);
 			if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 				result = propValue;
 			}
 		}
 
 		LOG.trace( "Done initializing lazy properties" );
 
 		return result;
 	}
 
 	private boolean initializeLazyProperty(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Object[] snapshot,
 			final int j,
 			final Object propValue) {
@@ -4115,1287 +4117,1298 @@ public abstract class AbstractEntityPersister
 				session
 		);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Which properties appear in the SQL update?
 	 * (Initialized, updateable ones!)
 	 */
 	protected boolean[] getPropertyUpdateability(Object entity) {
 		return hasUninitializedLazyProperties( entity )
 				? getNonLazyPropertyUpdateability()
 				: getPropertyUpdateability();
 	}
 
 	private void logDirtyProperties(int[] props) {
 		if ( LOG.isTraceEnabled() ) {
 			for ( int i = 0; i < props.length; i++ ) {
 				String propertyName = entityMetamodel.getProperties()[props[i]].getName();
 				LOG.trace( StringHelper.qualify( getEntityName(), propertyName ) + " is dirty" );
 			}
 		}
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	public EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryHelper.getCacheEntryStructure();
 	}
 
 	@Override
 	public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 		return cacheEntryHelper.buildCacheEntry( entity, state, version, session );
 	}
 
 	public boolean hasNaturalIdCache() {
 		return naturalIdRegionAccessStrategy != null;
 	}
 
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return naturalIdRegionAccessStrategy;
 	}
 
 	public Comparator getVersionComparator() {
 		return isVersioned() ? getVersionType().getComparator() : null;
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public final String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	public EntityType getEntityType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isPolymorphic() {
 		return entityMetamodel.isPolymorphic();
 	}
 
 	public boolean isInherited() {
 		return entityMetamodel.isInherited();
 	}
 
 	public boolean hasCascades() {
 		return entityMetamodel.hasCascades();
 	}
 
 	public boolean hasIdentifierProperty() {
 		return !entityMetamodel.getIdentifierProperty().isVirtual();
 	}
 
 	public VersionType getVersionType() {
 		return (VersionType) locateVersionType();
 	}
 
 	private Type locateVersionType() {
 		return entityMetamodel.getVersionProperty() == null ?
 				null :
 				entityMetamodel.getVersionProperty().getType();
 	}
 
 	public int getVersionProperty() {
 		return entityMetamodel.getVersionPropertyIndex();
 	}
 
 	public boolean isVersioned() {
 		return entityMetamodel.isVersioned();
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return entityMetamodel.getIdentifierProperty().isIdentifierAssignedByInsert();
 	}
 
 	public boolean hasLazyProperties() {
 		return entityMetamodel.hasLazyProperties();
 	}
 
 //	public boolean hasUninitializedLazyProperties(Object entity) {
 //		if ( hasLazyProperties() ) {
 //			InterceptFieldCallback callback = ( ( InterceptFieldEnabled ) entity ).getInterceptFieldCallback();
 //			return callback != null && !( ( FieldInterceptor ) callback ).isInitialized();
 //		}
 //		else {
 //			return false;
 //		}
 //	}
 
 	public void afterReassociate(Object entity, SessionImplementor session) {
 		if ( getEntityMetamodel().getInstrumentationMetadata().isInstrumented() ) {
 			FieldInterceptor interceptor = getEntityMetamodel().getInstrumentationMetadata()
 					.extractInterceptor( entity );
 			if ( interceptor != null ) {
 				interceptor.setSession( session );
 			}
 			else {
 				FieldInterceptor fieldInterceptor = getEntityMetamodel().getInstrumentationMetadata().injectInterceptor(
 						entity,
 						getEntityName(),
 						null,
 						session
 				);
 				fieldInterceptor.dirty();
 			}
 		}
 
 		if ( entity instanceof PersistentAttributeInterceptable ) {
 			PersistentAttributeInterceptor interceptor = ( (PersistentAttributeInterceptable) entity ).$$_hibernate_getInterceptor();
 			if ( interceptor != null && interceptor instanceof LazyAttributeLoader ) {
 				( (LazyAttributeLoader) interceptor ).setSession( session );
 			}
 		}
 
 		handleNaturalIdReattachment( entity, session );
 	}
 
 	private void handleNaturalIdReattachment(Object entity, SessionImplementor session) {
 		if ( !hasNaturalIdentifier() ) {
 			return;
 		}
 
 		if ( getEntityMetamodel().hasImmutableNaturalId() ) {
 			// we assume there were no changes to natural id during detachment for now, that is validated later
 			// during flush.
 			return;
 		}
 
 		final NaturalIdHelper naturalIdHelper = session.getPersistenceContext().getNaturalIdHelper();
 		final Serializable id = getIdentifier( entity, session );
 
 		// for reattachment of mutable natural-ids, we absolutely positively have to grab the snapshot from the
 		// database, because we have no other way to know if the state changed while detached.
 		final Object[] naturalIdSnapshot;
 		final Object[] entitySnapshot = session.getPersistenceContext().getDatabaseSnapshot( id, this );
 		if ( entitySnapshot == StatefulPersistenceContext.NO_ROW ) {
 			naturalIdSnapshot = null;
 		}
 		else {
 			naturalIdSnapshot = naturalIdHelper.extractNaturalIdValues( entitySnapshot, this );
 		}
 
 		naturalIdHelper.removeSharedNaturalIdCrossReference( this, id, naturalIdSnapshot );
 		naturalIdHelper.manageLocalNaturalIdCrossReference(
 				this,
 				id,
 				naturalIdHelper.extractNaturalIdValues( entity, this ),
 				naturalIdSnapshot,
 				CachedNaturalIdValueSource.UPDATE
 		);
 	}
 
 	public Boolean isTransient(Object entity, SessionImplementor session) throws HibernateException {
 		final Serializable id;
 		if ( canExtractIdOutOfEntity() ) {
 			id = getIdentifier( entity, session );
 		}
 		else {
 			id = null;
 		}
 		// we *always* assume an instance with a null
 		// identifier or no identifier property is unsaved!
 		if ( id == null ) {
 			return Boolean.TRUE;
 		}
 
 		// check the version unsaved-value, if appropriate
 		final Object version = getVersion( entity );
 		if ( isVersioned() ) {
 			// let this take precedence if defined, since it works for
 			// assigned identifiers
 			Boolean result = entityMetamodel.getVersionProperty()
 					.getUnsavedValue().isUnsaved( version );
 			if ( result != null ) {
 				return result;
 			}
 		}
 
 		// check the id unsaved-value
 		Boolean result = entityMetamodel.getIdentifierProperty()
 				.getUnsavedValue().isUnsaved( id );
 		if ( result != null ) {
 			return result;
 		}
 
 		// check to see if it is in the second-level cache
 		if ( session.getCacheMode().isGetEnabled() && hasCache() ) {
 			final EntityRegionAccessStrategy cache = getCacheAccessStrategy();
 			final Object ck = cache.generateCacheKey( id, this, session.getFactory(), session.getTenantIdentifier() );
 			final Object ce = CacheHelper.fromSharedCache( session, ck, getCacheAccessStrategy() );
 			if ( ce != null ) {
 				return Boolean.FALSE;
 			}
 		}
 
 		return null;
 	}
 
 	public boolean hasCollections() {
 		return entityMetamodel.hasCollections();
 	}
 
 	public boolean hasMutableProperties() {
 		return entityMetamodel.hasMutableProperties();
 	}
 
 	public boolean isMutable() {
 		return entityMetamodel.isMutable();
 	}
 
 	private boolean isModifiableEntity(EntityEntry entry) {
 		return ( entry == null ? isMutable() : entry.isModifiableEntity() );
 	}
 
 	public boolean isAbstract() {
 		return entityMetamodel.isAbstract();
 	}
 
 	public boolean hasSubclasses() {
 		return entityMetamodel.hasSubclasses();
 	}
 
 	public boolean hasProxy() {
 		// skip proxy instantiation if entity is bytecode enhanced
 		return entityMetamodel.isLazy() && !entityMetamodel.isLazyLoadingBytecodeEnhanced();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() throws HibernateException {
 		return entityMetamodel.getIdentifierProperty().getIdentifierGenerator();
 	}
 
 	public String getRootEntityName() {
 		return entityMetamodel.getRootName();
 	}
 
 	public ClassMetadata getClassMetadata() {
 		return this;
 	}
 
 	public String getMappedSuperclass() {
 		return entityMetamodel.getSuperclass();
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return entityMetamodel.isExplicitPolymorphism();
 	}
 
 	protected boolean useDynamicUpdate() {
 		return entityMetamodel.isDynamicUpdate();
 	}
 
 	protected boolean useDynamicInsert() {
 		return entityMetamodel.isDynamicInsert();
 	}
 
 	protected boolean hasEmbeddedCompositeIdentifier() {
 		return entityMetamodel.getIdentifierProperty().isEmbedded();
 	}
 
 	public boolean canExtractIdOutOfEntity() {
 		return hasIdentifierProperty() || hasEmbeddedCompositeIdentifier() || hasIdentifierMapper();
 	}
 
 	private boolean hasIdentifierMapper() {
 		return entityMetamodel.getIdentifierProperty().hasIdentifierMapper();
 	}
 
 	public String[] getKeyColumnNames() {
 		return getIdentifierColumnNames();
 	}
 
 	public String getName() {
 		return getEntityName();
 	}
 
 	public boolean isCollection() {
 		return false;
 	}
 
 	public boolean consumesEntityAlias() {
 		return true;
 	}
 
 	public boolean consumesCollectionAlias() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) throws MappingException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public Type getType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return entityMetamodel.isSelectBeforeUpdate();
 	}
 
 	protected final OptimisticLockStyle optimisticLockStyle() {
 		return entityMetamodel.getOptimisticLockStyle();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 		return entityMetamodel.getTuplizer().createProxy( id, session );
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) +
 				'(' + entityMetamodel.getName() + ')';
 	}
 
 	public final String selectFragment(
 			Joinable rhs,
 			String rhsAlias,
 			String lhsAlias,
 			String entitySuffix,
 			String collectionSuffix,
 			boolean includeCollectionColumns) {
 		return selectFragment( lhsAlias, entitySuffix );
 	}
 
 	public boolean isInstrumented() {
 		return entityMetamodel.isInstrumented();
 	}
 
 	public boolean hasInsertGeneratedProperties() {
 		return entityMetamodel.hasInsertGeneratedValues();
 	}
 
 	public boolean hasUpdateGeneratedProperties() {
 		return entityMetamodel.hasUpdateGeneratedValues();
 	}
 
 	public boolean isVersionPropertyGenerated() {
 		return isVersioned() && getEntityMetamodel().isVersionGenerated();
 	}
 
 	public boolean isVersionPropertyInsertable() {
 		return isVersioned() && getPropertyInsertability()[getVersionProperty()];
 	}
 
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		getEntityTuplizer().afterInitialize( entity, lazyPropertiesAreUnfetched, session );
 	}
 
 	public String[] getPropertyNames() {
 		return entityMetamodel.getPropertyNames();
 	}
 
 	public Type[] getPropertyTypes() {
 		return entityMetamodel.getPropertyTypes();
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return entityMetamodel.getPropertyLaziness();
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return entityMetamodel.getPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return entityMetamodel.getPropertyCheckability();
 	}
 
 	public boolean[] getNonLazyPropertyUpdateability() {
 		return entityMetamodel.getNonlazyPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return entityMetamodel.getPropertyInsertability();
 	}
 
 	/**
 	 * @deprecated no simple, direct replacement
 	 */
 	@Deprecated
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return null;
 	}
 
 	/**
 	 * @deprecated no simple, direct replacement
 	 */
 	@Deprecated
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return null;
 	}
 
 	public boolean[] getPropertyNullability() {
 		return entityMetamodel.getPropertyNullability();
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return entityMetamodel.getPropertyVersionability();
 	}
 
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return entityMetamodel.getCascadeStyles();
 	}
 
 	public final Class getMappedClass() {
 		return getEntityTuplizer().getMappedClass();
 	}
 
 	public boolean implementsLifecycle() {
 		return getEntityTuplizer().isLifecycleImplementor();
 	}
 
 	public Class getConcreteProxyClass() {
 		return getEntityTuplizer().getConcreteProxyClass();
 	}
 
 	public void setPropertyValues(Object object, Object[] values) {
 		getEntityTuplizer().setPropertyValues( object, values );
 	}
 
 	public void setPropertyValue(Object object, int i, Object value) {
 		getEntityTuplizer().setPropertyValue( object, i, value );
 	}
 
 	public Object[] getPropertyValues(Object object) {
 		return getEntityTuplizer().getPropertyValues( object );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) {
 		return getEntityTuplizer().getPropertyValue( object, i );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) {
 		return getEntityTuplizer().getPropertyValue( object, propertyName );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) {
 		return getEntityTuplizer().getIdentifier( object, null );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return getEntityTuplizer().getIdentifier( entity, session );
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		getEntityTuplizer().setIdentifier( entity, id, session );
 	}
 
 	@Override
 	public Object getVersion(Object object) {
 		return getEntityTuplizer().getVersion( object );
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		return getEntityTuplizer().instantiate( id, session );
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return getEntityTuplizer().isInstance( object );
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return getEntityTuplizer().hasUninitializedLazyProperties( object );
 	}
 
 	@Override
 	public void resetIdentifier(
 			Object entity,
 			Serializable currentId,
 			Object currentVersion,
 			SessionImplementor session) {
 		getEntityTuplizer().resetIdentifier( entity, currentId, currentVersion, session );
 	}
 
 	@Override
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		if ( !hasSubclasses() ) {
 			return this;
 		}
 		else {
 			final String concreteEntityName = getEntityTuplizer().determineConcreteSubclassEntityName(
 					instance,
 					factory
 			);
 			if ( concreteEntityName == null || getEntityName().equals( concreteEntityName ) ) {
 				// the contract of EntityTuplizer.determineConcreteSubclassEntityName says that returning null
 				// is an indication that the specified entity-name (this.getEntityName) should be used.
 				return this;
 			}
 			else {
 				return factory.getEntityPersister( concreteEntityName );
 			}
 		}
 	}
 
 	public boolean isMultiTable() {
 		return false;
 	}
 
 	protected int getPropertySpan() {
 		return entityMetamodel.getPropertySpan();
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
 			throws HibernateException {
 		return getEntityTuplizer().getPropertyValuesToInsert( object, mergeMap, session );
 	}
 
 	public void processInsertGeneratedProperties(
 			Serializable id,
 			Object entity,
 			Object[] state,
 			SessionImplementor session) {
 		if ( !hasInsertGeneratedProperties() ) {
 			throw new AssertionFailure( "no insert-generated properties" );
 		}
 		processGeneratedProperties(
 				id,
 				entity,
 				state,
 				session,
 				sqlInsertGeneratedValuesSelectString,
 				GenerationTiming.INSERT
 		);
 	}
 
 	public void processUpdateGeneratedProperties(
 			Serializable id,
 			Object entity,
 			Object[] state,
 			SessionImplementor session) {
 		if ( !hasUpdateGeneratedProperties() ) {
 			throw new AssertionFailure( "no update-generated properties" );
 		}
 		processGeneratedProperties(
 				id,
 				entity,
 				state,
 				session,
 				sqlUpdateGeneratedValuesSelectString,
 				GenerationTiming.ALWAYS
 		);
 	}
 
 	private void processGeneratedProperties(
 			Serializable id,
 			Object entity,
 			Object[] state,
 			SessionImplementor session,
 			String selectionSQL,
 			GenerationTiming matchTiming) {
 		// force immediate execution of the insert batch (if one)
 		session.getJdbcCoordinator().executeBatch();
 
 		try {
 			PreparedStatement ps = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( selectionSQL );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					if ( !rs.next() ) {
 						throw new HibernateException(
 								"Unable to locate row for retrieval of generated properties: " +
 										MessageHelper.infoString( this, id, getFactory() )
 						);
 					}
 					int propertyIndex = -1;
 					for ( NonIdentifierAttribute attribute : entityMetamodel.getProperties() ) {
 						propertyIndex++;
 						final ValueGeneration valueGeneration = attribute.getValueGenerationStrategy();
 						if ( isReadRequired( valueGeneration, matchTiming ) ) {
 							final Object hydratedState = attribute.getType().hydrate(
 									rs, getPropertyAliases(
 											"",
 											propertyIndex
 									), session, entity
 							);
 							state[propertyIndex] = attribute.getType().resolve( hydratedState, session, entity );
 							setPropertyValue( entity, propertyIndex, state[propertyIndex] );
 						}
 					}
 //					for ( int i = 0; i < getPropertySpan(); i++ ) {
 //						if ( includeds[i] != ValueInclusion.NONE ) {
 //							Object hydratedState = getPropertyTypes()[i].hydrate( rs, getPropertyAliases( "", i ), session, entity );
 //							state[i] = getPropertyTypes()[i].resolve( hydratedState, session, entity );
 //							setPropertyValue( entity, i, state[i] );
 //						}
 //					}
 				}
 				finally {
 					if ( rs != null ) {
 						session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( ps );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"unable to select generated column values",
 					selectionSQL
 			);
 		}
 
 	}
 
 	/**
 	 * Whether the given value generation strategy requires to read the value from the database or not.
 	 */
 	private boolean isReadRequired(ValueGeneration valueGeneration, GenerationTiming matchTiming) {
 		return valueGeneration != null &&
 				valueGeneration.getValueGenerator() == null &&
 				timingsMatch( valueGeneration.getGenerationTiming(), matchTiming );
 	}
 
 	private boolean timingsMatch(GenerationTiming timing, GenerationTiming matchTiming) {
 		return
 				( matchTiming == GenerationTiming.INSERT && timing.includesInsert() ) ||
 						( matchTiming == GenerationTiming.ALWAYS && timing.includesUpdate() );
 	}
 
 	public String getIdentifierPropertyName() {
 		return entityMetamodel.getIdentifierProperty().getName();
 	}
 
 	public Type getIdentifierType() {
 		return entityMetamodel.getIdentifierProperty().getType();
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return entityMetamodel.getNaturalIdentifierProperties();
 	}
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session)
 			throws HibernateException {
 		if ( !hasNaturalIdentifier() ) {
 			throw new MappingException(
 					"persistent class did not define a natural-id : " + MessageHelper.infoString(
 							this
 					)
 			);
 		}
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Getting current natural-id snapshot state for: {0}",
 					MessageHelper.infoString( this, id, getFactory() )
 			);
 		}
 
 		int[] naturalIdPropertyIndexes = getNaturalIdentifierProperties();
 		int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
 		boolean[] naturalIdMarkers = new boolean[getPropertySpan()];
 		Type[] extractionTypes = new Type[naturalIdPropertyCount];
 		for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 			extractionTypes[i] = getPropertyTypes()[naturalIdPropertyIndexes[i]];
 			naturalIdMarkers[naturalIdPropertyIndexes[i]] = true;
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// TODO : look at perhaps caching this...
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id state " + getEntityName() );
 		}
 		select.setSelectClause( concretePropertySelectFragmentSansLeadingComma( getRootAlias(), naturalIdMarkers ) );
 		select.setFromClause( fromTableFragment( getRootAlias() ) + fromJoinFragment( getRootAlias(), true, false ) );
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String whereClause = new StringBuilder()
 				.append(
 						StringHelper.join(
 								"=? and ",
 								aliasedIdColumns
 						)
 				)
 				.append( "=?" )
 				.append( whereJoinFragment( getRootAlias(), true, false ) )
 				.toString();
 
 		String sql = select.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 		///////////////////////////////////////////////////////////////////////
 
 		Object[] snapshot = new Object[naturalIdPropertyCount];
 		try {
 			PreparedStatement ps = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					final EntityKey key = session.generateEntityKey( id, this );
 					Object owner = session.getPersistenceContext().getEntity( key );
 					for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 						snapshot[i] = extractionTypes[i].hydrate(
 								rs, getPropertyAliases(
 										"",
 										naturalIdPropertyIndexes[i]
 								), session, null
 						);
 						if ( extractionTypes[i].isEntityType() ) {
 							snapshot[i] = extractionTypes[i].resolve( snapshot[i], session, owner );
 						}
 					}
 					return snapshot;
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( ps );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 					sql
 			);
 		}
 	}
 
 	@Override
 	public Serializable loadEntityIdByNaturalId(
 			Object[] naturalIdValues,
 			LockOptions lockOptions,
 			SessionImplementor session) {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"Resolving natural-id [%s] to id : %s ",
 					naturalIdValues,
 					MessageHelper.infoString( this )
 			);
 		}
 
 		final boolean[] valueNullness = determineValueNullness( naturalIdValues );
 		final String sqlEntityIdByNaturalIdString = determinePkByNaturalIdQuery( valueNullness );
 
 		try {
 			PreparedStatement ps = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlEntityIdByNaturalIdString );
 			try {
 				int positions = 1;
 				int loop = 0;
 				for ( int idPosition : getNaturalIdentifierProperties() ) {
 					final Object naturalIdValue = naturalIdValues[loop++];
 					if ( naturalIdValue != null ) {
 						final Type type = getPropertyTypes()[idPosition];
 						type.nullSafeSet( ps, naturalIdValue, positions, session );
 						positions += type.getColumnSpan( session.getFactory() );
 					}
 				}
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					// if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 
 					final Object hydratedId = getIdentifierType().hydrate( rs, getIdentifierAliases(), session, null );
 					return (Serializable) getIdentifierType().resolve( hydratedId, session, null );
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( ps );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					String.format(
 							"could not resolve natural-id [%s] to id : %s",
 							naturalIdValues,
 							MessageHelper.infoString( this )
 					),
 					sqlEntityIdByNaturalIdString
 			);
 		}
 	}
 
 	private boolean[] determineValueNullness(Object[] naturalIdValues) {
 		boolean[] nullness = new boolean[naturalIdValues.length];
 		for ( int i = 0; i < naturalIdValues.length; i++ ) {
 			nullness[i] = naturalIdValues[i] == null;
 		}
 		return nullness;
 	}
 
 	private Boolean naturalIdIsNonNullable;
 	private String cachedPkByNonNullableNaturalIdQuery;
 
 	private String determinePkByNaturalIdQuery(boolean[] valueNullness) {
 		if ( !hasNaturalIdentifier() ) {
 			throw new HibernateException(
 					"Attempt to build natural-id -> PK resolution query for entity that does not define natural id"
 			);
 		}
 
 		// performance shortcut for cases where the natural-id is defined as completely non-nullable
 		if ( isNaturalIdNonNullable() ) {
 			if ( valueNullness != null && !ArrayHelper.isAllFalse( valueNullness ) ) {
 				throw new HibernateException( "Null value(s) passed to lookup by non-nullable natural-id" );
 			}
 			if ( cachedPkByNonNullableNaturalIdQuery == null ) {
 				cachedPkByNonNullableNaturalIdQuery = generateEntityIdByNaturalIdSql( null );
 			}
 			return cachedPkByNonNullableNaturalIdQuery;
 		}
 
 		// Otherwise, regenerate it each time
 		return generateEntityIdByNaturalIdSql( valueNullness );
 	}
 
 	protected boolean isNaturalIdNonNullable() {
 		if ( naturalIdIsNonNullable == null ) {
 			naturalIdIsNonNullable = determineNaturalIdNullability();
 		}
 		return naturalIdIsNonNullable;
 	}
 
 	private boolean determineNaturalIdNullability() {
 		boolean[] nullability = getPropertyNullability();
 		for ( int position : getNaturalIdentifierProperties() ) {
 			// if any individual property is nullable, return false
 			if ( nullability[position] ) {
 				return false;
 			}
 		}
 		// return true if we found no individually nullable properties
 		return true;
 	}
 
 	private String generateEntityIdByNaturalIdSql(boolean[] valueNullness) {
 		EntityPersister rootPersister = getFactory().getEntityPersister( getRootEntityName() );
 		if ( rootPersister != this ) {
 			if ( rootPersister instanceof AbstractEntityPersister ) {
 				return ( (AbstractEntityPersister) rootPersister ).generateEntityIdByNaturalIdSql( valueNullness );
 			}
 		}
 
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id->entity-id state " + getEntityName() );
 		}
 
 		final String rootAlias = getRootAlias();
 
 		select.setSelectClause( identifierSelectFragment( rootAlias, "" ) );
 		select.setFromClause( fromTableFragment( rootAlias ) + fromJoinFragment( rootAlias, true, false ) );
 
 		final StringBuilder whereClause = new StringBuilder();
 		final int[] propertyTableNumbers = getPropertyTableNumbers();
 		final int[] naturalIdPropertyIndexes = this.getNaturalIdentifierProperties();
 		int valuesIndex = -1;
 		for ( int propIdx = 0; propIdx < naturalIdPropertyIndexes.length; propIdx++ ) {
 			valuesIndex++;
 			if ( propIdx > 0 ) {
 				whereClause.append( " and " );
 			}
 
 			final int naturalIdIdx = naturalIdPropertyIndexes[propIdx];
 			final String tableAlias = generateTableAlias( rootAlias, propertyTableNumbers[naturalIdIdx] );
 			final String[] propertyColumnNames = getPropertyColumnNames( naturalIdIdx );
 			final String[] aliasedPropertyColumns = StringHelper.qualify( tableAlias, propertyColumnNames );
 
 			if ( valueNullness != null && valueNullness[valuesIndex] ) {
 				whereClause.append( StringHelper.join( " is null and ", aliasedPropertyColumns ) ).append( " is null" );
 			}
 			else {
 				whereClause.append( StringHelper.join( "=? and ", aliasedPropertyColumns ) ).append( "=?" );
 			}
 		}
 
 		whereClause.append( whereJoinFragment( getRootAlias(), true, false ) );
 
 		return select.setOuterJoins( "", "" ).setWhereClause( whereClause.toString() ).toStatementString();
 	}
 
 	protected String concretePropertySelectFragmentSansLeadingComma(String alias, boolean[] include) {
 		String concretePropertySelectFragment = concretePropertySelectFragment( alias, include );
 		int firstComma = concretePropertySelectFragment.indexOf( ", " );
 		if ( firstComma == 0 ) {
 			concretePropertySelectFragment = concretePropertySelectFragment.substring( 2 );
 		}
 		return concretePropertySelectFragment;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return entityMetamodel.hasNaturalIdentifier();
 	}
 
 	public void setPropertyValue(Object object, String propertyName, Object value) {
 		getEntityTuplizer().setPropertyValue( object, propertyName, value );
 	}
 
 	public static int getTableId(String tableName, String[] tables) {
 		for ( int j = 0; j < tables.length; j++ ) {
 			if ( tableName.equalsIgnoreCase( tables[j] ) ) {
 				return j;
 			}
 		}
 		throw new AssertionFailure( "Table " + tableName + " not found" );
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return entityMetamodel.getEntityMode();
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return entityTuplizer;
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return entityMetamodel.getInstrumentationMetadata();
 	}
 
 	@Override
 	public String getTableAliasForColumn(String columnName, String rootAlias) {
 		return generateTableAlias( rootAlias, determineTableNumberForColumn( columnName ) );
 	}
 
 	public int determineTableNumberForColumn(String columnName) {
 		return 0;
 	}
 
+	protected String determineTableName(Table table, JdbcEnvironment jdbcEnvironment) {
+		if ( table.getSubselect() != null ) {
+			return "( " + table.getSubselect() + " )";
+		}
+
+		return jdbcEnvironment.getQualifiedObjectNameFormatter().format(
+				table.getQualifiedTableName(),
+				jdbcEnvironment.getDialect()
+		);
+	}
+
 	@Override
 	public EntityEntryFactory getEntityEntryFactory() {
 		return this.entityEntryFactory;
 	}
 
 	/**
 	 * Consolidated these onto a single helper because the 2 pieces work in tandem.
 	 */
 	public interface CacheEntryHelper {
 		CacheEntryStructure getCacheEntryStructure();
 
 		CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session);
 	}
 
 	private static class StandardCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private StandardCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class ReferenceCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private ReferenceCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new ReferenceCacheEntryImpl( entity, persister );
 		}
 	}
 
 	private static class StructuredCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 		private final StructuredCacheEntry structure;
 
 		private StructuredCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 			this.structure = new StructuredCacheEntry( persister );
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return structure;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class NoopCacheEntryHelper implements CacheEntryHelper {
 		public static final NoopCacheEntryHelper INSTANCE = new NoopCacheEntryHelper();
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			throw new HibernateException( "Illegal attempt to build cache entry for non-cached entity" );
 		}
 	}
 
 
 	// EntityDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private EntityIdentifierDefinition entityIdentifierDefinition;
 	private Iterable<AttributeDefinition> embeddedCompositeIdentifierAttributes;
 	private Iterable<AttributeDefinition> attributeDefinitions;
 
 	@Override
 	public void generateEntityDefinition() {
 		prepareEntityIdentifierDefinition();
 		collectAttributeDefinitions();
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return this;
 	}
 
 	@Override
 	public EntityIdentifierDefinition getEntityKeyDefinition() {
 		return entityIdentifierDefinition;
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		return attributeDefinitions;
 	}
 
 
 	private void prepareEntityIdentifierDefinition() {
 		if ( entityIdentifierDefinition != null ) {
 			return;
 		}
 		final Type idType = getIdentifierType();
 
 		if ( !idType.isComponentType() ) {
 			entityIdentifierDefinition =
 					EntityIdentifierDefinitionHelper.buildSimpleEncapsulatedIdentifierDefinition( this );
 			return;
 		}
 
 		final CompositeType cidType = (CompositeType) idType;
 		if ( !cidType.isEmbedded() ) {
 			entityIdentifierDefinition =
 					EntityIdentifierDefinitionHelper.buildEncapsulatedCompositeIdentifierDefinition( this );
 			return;
 		}
 
 		entityIdentifierDefinition =
 				EntityIdentifierDefinitionHelper.buildNonEncapsulatedCompositeIdentifierDefinition( this );
 	}
 
 	private void collectAttributeDefinitions(
 			Map<String, AttributeDefinition> attributeDefinitionsByName,
 			EntityMetamodel metamodel) {
 		for ( int i = 0; i < metamodel.getPropertySpan(); i++ ) {
 			final AttributeDefinition attributeDefinition = metamodel.getProperties()[i];
 			// Don't replace an attribute definition if it is already in attributeDefinitionsByName
 			// because the new value will be from a subclass.
 			final AttributeDefinition oldAttributeDefinition = attributeDefinitionsByName.get(
 					attributeDefinition.getName()
 			);
 			if ( oldAttributeDefinition != null ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracef(
 							"Ignoring subclass attribute definition [%s.%s] because it is defined in a superclass ",
 							entityMetamodel.getName(),
 							attributeDefinition.getName()
 					);
 				}
 			}
 			else {
 				attributeDefinitionsByName.put( attributeDefinition.getName(), attributeDefinition );
 			}
 		}
 
 		// see if there are any subclass persisters...
 		final Set<String> subClassEntityNames = metamodel.getSubclassEntityNames();
 		if ( subClassEntityNames == null ) {
 			return;
 		}
 
 		// see if we can find the persisters...
 		for ( String subClassEntityName : subClassEntityNames ) {
 			if ( metamodel.getName().equals( subClassEntityName ) ) {
 				// skip it
 				continue;
 			}
 			try {
 				final EntityPersister subClassEntityPersister = factory.getEntityPersister( subClassEntityName );
 				collectAttributeDefinitions( attributeDefinitionsByName, subClassEntityPersister.getEntityMetamodel() );
 			}
 			catch (MappingException e) {
 				throw new IllegalStateException(
 						String.format(
 								"Could not locate subclass EntityPersister [%s] while processing EntityPersister [%s]",
 								subClassEntityName,
 								metamodel.getName()
 						),
 						e
 				);
 			}
 		}
 	}
 
 	private void collectAttributeDefinitions() {
 		// todo : I think this works purely based on luck atm
 		// 		specifically in terms of the sub/super class entity persister(s) being available.  Bit of chicken-egg
 		// 		problem there:
 		//			* If I do this during postConstruct (as it is now), it works as long as the
 		//			super entity persister is already registered, but I don't think that is necessarily true.
 		//			* If I do this during postInstantiate then lots of stuff in postConstruct breaks if we want
 		//			to try and drive SQL generation on these (which we do ultimately).  A possible solution there
 		//			would be to delay all SQL generation until postInstantiate
 
 		Map<String, AttributeDefinition> attributeDefinitionsByName = new LinkedHashMap<String, AttributeDefinition>();
 		collectAttributeDefinitions( attributeDefinitionsByName, getEntityMetamodel() );
 
 
 //		EntityMetamodel currentEntityMetamodel = this.getEntityMetamodel();
 //		while ( currentEntityMetamodel != null ) {
 //			for ( int i = 0; i < currentEntityMetamodel.getPropertySpan(); i++ ) {
 //				attributeDefinitions.add( currentEntityMetamodel.getProperties()[i] );
 //			}
 //			// see if there is a super class EntityMetamodel
 //			final String superEntityName = currentEntityMetamodel.getSuperclass();
 //			if ( superEntityName != null ) {
 //				currentEntityMetamodel = factory.getEntityPersister( superEntityName ).getEntityMetamodel();
 //			}
 //			else {
 //				currentEntityMetamodel = null;
 //			}
 //		}
 
 		this.attributeDefinitions = Collections.unmodifiableList(
 				new ArrayList<AttributeDefinition>( attributeDefinitionsByName.values() )
 		);
 //		// todo : leverage the attribute definitions housed on EntityMetamodel
 //		// 		for that to work, we'd have to be able to walk our super entity persister(s)
 //		this.attributeDefinitions = new Iterable<AttributeDefinition>() {
 //			@Override
 //			public Iterator<AttributeDefinition> iterator() {
 //				return new Iterator<AttributeDefinition>() {
 ////					private final int numberOfAttributes = countSubclassProperties();
 ////					private final int numberOfAttributes = entityMetamodel.getPropertySpan();
 //
 //					EntityMetamodel currentEntityMetamodel = entityMetamodel;
 //					int numberOfAttributesInCurrentEntityMetamodel = currentEntityMetamodel.getPropertySpan();
 //
 //					private int currentAttributeNumber;
 //
 //					@Override
 //					public boolean hasNext() {
 //						return currentEntityMetamodel != null
 //								&& currentAttributeNumber < numberOfAttributesInCurrentEntityMetamodel;
 //					}
 //
 //					@Override
 //					public AttributeDefinition next() {
 //						final int attributeNumber = currentAttributeNumber;
 //						currentAttributeNumber++;
 //						final AttributeDefinition next = currentEntityMetamodel.getProperties()[ attributeNumber ];
 //
 //						if ( currentAttributeNumber >= numberOfAttributesInCurrentEntityMetamodel ) {
 //							// see if there is a super class EntityMetamodel
 //							final String superEntityName = currentEntityMetamodel.getSuperclass();
 //							if ( superEntityName != null ) {
 //								currentEntityMetamodel = factory.getEntityPersister( superEntityName ).getEntityMetamodel();
 //								if ( currentEntityMetamodel != null ) {
 //									numberOfAttributesInCurrentEntityMetamodel = currentEntityMetamodel.getPropertySpan();
 //									currentAttributeNumber = 0;
 //								}
 //							}
 //						}
 //
 //						return next;
 //					}
 //
 //					@Override
 //					public void remove() {
 //						throw new UnsupportedOperationException( "Remove operation not supported here" );
 //					}
 //				};
 //			}
 //		};
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
index 377586908b..4c8f96b770 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
@@ -1,1130 +1,1116 @@
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
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
+import org.hibernate.boot.model.relational.Database;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.engine.OptimisticLockStyle;
+import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.DynamicFilterAliasGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.Value;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.InFragment;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.type.DiscriminatorType;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 /**
  * An <tt>EntityPersister</tt> implementing the normalized "table-per-subclass"
  * mapping strategy
  *
  * @author Gavin King
  */
 public class JoinedSubclassEntityPersister extends AbstractEntityPersister {
 	private static final Logger log = Logger.getLogger( JoinedSubclassEntityPersister.class );
 
 	private static final String IMPLICIT_DISCRIMINATOR_ALIAS = "clazz_";
 	private static final Object NULL_DISCRIMINATOR = new MarkerObject("<null discriminator>");
 	private static final Object NOT_NULL_DISCRIMINATOR = new MarkerObject("<not null discriminator>");
 	private static final String NULL_STRING = "null";
 	private static final String NOT_NULL_STRING = "not null";
 
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
 
 	private final Object discriminatorValue;
 	private final String discriminatorSQLString;
 	private final DiscriminatorType discriminatorType;
 	private final String explicitDiscriminatorColumnName;
 	private final String discriminatorAlias;
 
 	// Span of the tables directly mapped by this entity and super-classes, if any
 	private final int coreTableSpan;
 	// only contains values for SecondaryTables, ie. not tables part of the "coreTableSpan"
 	private final boolean[] isNullableTable;
 
 	//INITIALIZATION:
 
 	public JoinedSubclassEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final PersisterCreationContext creationContext) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, creationContext );
 
 		final SessionFactoryImplementor factory = creationContext.getSessionFactory();
+		final Database database = creationContext.getMetadata().getDatabase();
+		final JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
 
 		// DISCRIMINATOR
 
 		if ( persistentClass.isPolymorphic() ) {
 			final Value discriminatorMapping = persistentClass.getDiscriminator();
 			if ( discriminatorMapping != null ) {
 				log.debug( "Encountered explicit discriminator mapping for joined inheritance" );
 
 				final Selectable selectable = discriminatorMapping.getColumnIterator().next();
 				if ( Formula.class.isInstance( selectable ) ) {
 					throw new MappingException( "Discriminator formulas on joined inheritance hierarchies not supported at this time" );
 				}
 				else {
 					final Column column = (Column) selectable;
 					explicitDiscriminatorColumnName = column.getQuotedName( factory.getDialect() );
 					discriminatorAlias = column.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 				}
 				discriminatorType = (DiscriminatorType) persistentClass.getDiscriminator().getType();
 				if ( persistentClass.isDiscriminatorValueNull() ) {
 					discriminatorValue = NULL_DISCRIMINATOR;
 					discriminatorSQLString = InFragment.NULL;
 				}
 				else if ( persistentClass.isDiscriminatorValueNotNull() ) {
 					discriminatorValue = NOT_NULL_DISCRIMINATOR;
 					discriminatorSQLString = InFragment.NOT_NULL;
 				}
 				else {
 					try {
 						discriminatorValue = discriminatorType.stringToObject( persistentClass.getDiscriminatorValue() );
 						discriminatorSQLString = discriminatorType.objectToSQLString( discriminatorValue, factory.getDialect() );
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
 				explicitDiscriminatorColumnName = null;
 				discriminatorAlias = IMPLICIT_DISCRIMINATOR_ALIAS;
 				discriminatorType = StandardBasicTypes.INTEGER;
 				try {
 					discriminatorValue = persistentClass.getSubclassId();
 					discriminatorSQLString = discriminatorValue.toString();
 				}
 				catch ( Exception e ) {
 					throw new MappingException( "Could not format discriminator value to SQL string", e );
 				}
 			}
 		}
 		else {
 			explicitDiscriminatorColumnName = null;
 			discriminatorAlias = IMPLICIT_DISCRIMINATOR_ALIAS;
 			discriminatorType = StandardBasicTypes.INTEGER;
 			discriminatorValue = null;
 			discriminatorSQLString = null;
 		}
 
 		if ( optimisticLockStyle() == OptimisticLockStyle.ALL || optimisticLockStyle() == OptimisticLockStyle.DIRTY ) {
 			throw new MappingException( "optimistic-lock=all|dirty not supported for joined-subclass mappings [" + getEntityName() + "]" );
 		}
 
 		//MULTITABLES
 
 		final int idColumnSpan = getIdentifierColumnSpan();
 
-		ArrayList tables = new ArrayList();
-		ArrayList keyColumns = new ArrayList();
-		ArrayList keyColumnReaders = new ArrayList();
-		ArrayList keyColumnReaderTemplates = new ArrayList();
-		ArrayList cascadeDeletes = new ArrayList();
-		Iterator titer = persistentClass.getTableClosureIterator();
-		Iterator kiter = persistentClass.getKeyClosureIterator();
-		while ( titer.hasNext() ) {
-			Table tab = (Table) titer.next();
-			KeyValue key = (KeyValue) kiter.next();
-			String tabname = tab.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			tables.add( tabname );
+		ArrayList<String> tableNames = new ArrayList<String>();
+		ArrayList<String[]> keyColumns = new ArrayList<String[]>();
+		ArrayList<String[]> keyColumnReaders = new ArrayList<String[]>();
+		ArrayList<String[]> keyColumnReaderTemplates = new ArrayList<String[]>();
+		ArrayList<Boolean> cascadeDeletes = new ArrayList<Boolean>();
+		Iterator tItr = persistentClass.getTableClosureIterator();
+		Iterator kItr = persistentClass.getKeyClosureIterator();
+		while ( tItr.hasNext() ) {
+			final Table table = (Table) tItr.next();
+			final KeyValue key = (KeyValue) kItr.next();
+			final String tableName = determineTableName( table, jdbcEnvironment );
+			tableNames.add( tableName );
 			String[] keyCols = new String[idColumnSpan];
 			String[] keyColReaders = new String[idColumnSpan];
 			String[] keyColReaderTemplates = new String[idColumnSpan];
-			Iterator citer = key.getColumnIterator();
+			Iterator cItr = key.getColumnIterator();
 			for ( int k = 0; k < idColumnSpan; k++ ) {
-				Column column = (Column) citer.next();
+				Column column = (Column) cItr.next();
 				keyCols[k] = column.getQuotedName( factory.getDialect() );
 				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
 				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			}
 			keyColumns.add( keyCols );
 			keyColumnReaders.add( keyColReaders );
 			keyColumnReaderTemplates.add( keyColReaderTemplates );
 			cascadeDeletes.add( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() );
 		}
 
-		//Span of the tables directly mapped by this entity and super-classes, if any
-		coreTableSpan = tables.size();
+		//Span of the tableNames directly mapped by this entity and super-classes, if any
+		coreTableSpan = tableNames.size();
 
 		isNullableTable = new boolean[persistentClass.getJoinClosureSpan()];
 
 		int tableIndex = 0;
-		Iterator joinIter = persistentClass.getJoinClosureIterator();
-		while ( joinIter.hasNext() ) {
-			Join join = (Join) joinIter.next();
+		Iterator joinItr = persistentClass.getJoinClosureIterator();
+		while ( joinItr.hasNext() ) {
+			Join join = (Join) joinItr.next();
 
 			isNullableTable[tableIndex++] = join.isOptional();
 
 			Table table = join.getTable();
-
-			String tableName = table.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			tables.add( tableName );
+			final String tableName = determineTableName( table, jdbcEnvironment );
+			tableNames.add( tableName );
 
 			KeyValue key = join.getKey();
 			int joinIdColumnSpan = key.getColumnSpan();
 
 			String[] keyCols = new String[joinIdColumnSpan];
 			String[] keyColReaders = new String[joinIdColumnSpan];
 			String[] keyColReaderTemplates = new String[joinIdColumnSpan];
 
-			Iterator citer = key.getColumnIterator();
+			Iterator cItr = key.getColumnIterator();
 
 			for ( int k = 0; k < joinIdColumnSpan; k++ ) {
-				Column column = (Column) citer.next();
+				Column column = (Column) cItr.next();
 				keyCols[k] = column.getQuotedName( factory.getDialect() );
 				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
 				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			}
 			keyColumns.add( keyCols );
 			keyColumnReaders.add( keyColReaders );
 			keyColumnReaderTemplates.add( keyColReaderTemplates );
 			cascadeDeletes.add( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() );
 		}
 
-		naturalOrderTableNames = ArrayHelper.toStringArray( tables );
+		naturalOrderTableNames = ArrayHelper.toStringArray( tableNames );
 		naturalOrderTableKeyColumns = ArrayHelper.to2DStringArray( keyColumns );
 		naturalOrderTableKeyColumnReaders = ArrayHelper.to2DStringArray( keyColumnReaders );
 		naturalOrderTableKeyColumnReaderTemplates = ArrayHelper.to2DStringArray( keyColumnReaderTemplates );
 		naturalOrderCascadeDeleteEnabled = ArrayHelper.toBooleanArray( cascadeDeletes );
 
-		ArrayList subtables = new ArrayList();
-		ArrayList isConcretes = new ArrayList();
-		ArrayList isDeferreds = new ArrayList();
-		ArrayList isLazies = new ArrayList();
+		ArrayList<String> subclassTableNames = new ArrayList<String>();
+		ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
+		ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
+		ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
 
-		keyColumns = new ArrayList();
-		titer = persistentClass.getSubclassTableClosureIterator();
-		while ( titer.hasNext() ) {
-			Table tab = (Table) titer.next();
+		keyColumns = new ArrayList<String[]>();
+		tItr = persistentClass.getSubclassTableClosureIterator();
+		while ( tItr.hasNext() ) {
+			Table tab = (Table) tItr.next();
 			isConcretes.add( persistentClass.isClassOrSuperclassTable( tab ) );
 			isDeferreds.add( Boolean.FALSE );
 			isLazies.add( Boolean.FALSE );
-			String tabname = tab.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			subtables.add( tabname );
+			final String tableName = determineTableName( tab, jdbcEnvironment );
+			subclassTableNames.add( tableName );
 			String[] key = new String[idColumnSpan];
-			Iterator citer = tab.getPrimaryKey().getColumnIterator();
+			Iterator cItr = tab.getPrimaryKey().getColumnIterator();
 			for ( int k = 0; k < idColumnSpan; k++ ) {
-				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
+				key[k] = ( (Column) cItr.next() ).getQuotedName( factory.getDialect() );
 			}
 			keyColumns.add( key );
 		}
 
 		//Add joins
-		joinIter = persistentClass.getSubclassJoinClosureIterator();
-		while ( joinIter.hasNext() ) {
-			Join join = (Join) joinIter.next();
-
-			Table tab = join.getTable();
+		joinItr = persistentClass.getSubclassJoinClosureIterator();
+		while ( joinItr.hasNext() ) {
+			final Join join = (Join) joinItr.next();
+			final Table joinTable = join.getTable();
 
-			isConcretes.add( persistentClass.isClassOrSuperclassTable( tab ) );
+			isConcretes.add( persistentClass.isClassOrSuperclassTable( joinTable ) );
 			isDeferreds.add( join.isSequentialSelect() );
 			isLazies.add( join.isLazy() );
 
-			String tabname = tab.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			subtables.add( tabname );
+			String joinTableName = determineTableName( joinTable, jdbcEnvironment );
+			subclassTableNames.add( joinTableName );
 			String[] key = new String[idColumnSpan];
-			Iterator citer = tab.getPrimaryKey().getColumnIterator();
+			Iterator citer = joinTable.getPrimaryKey().getColumnIterator();
 			for ( int k = 0; k < idColumnSpan; k++ ) {
 				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
 			}
 			keyColumns.add( key );
 		}
 
-		String[] naturalOrderSubclassTableNameClosure = ArrayHelper.toStringArray( subtables );
+		String[] naturalOrderSubclassTableNameClosure = ArrayHelper.toStringArray( subclassTableNames );
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
-		 * Suppose an entity Client extends Person, mapped to the tables CLIENT and PERSON respectively.
+		 * Suppose an entity Client extends Person, mapped to the tableNames CLIENT and PERSON respectively.
 		 * For the Client entity:
-		 * naturalOrderTableNames -> PERSON, CLIENT; this reflects the sequence in which the tables are 
+		 * naturalOrderTableNames -> PERSON, CLIENT; this reflects the sequence in which the tableNames are
 		 * added to the meta-data when the annotated entities are processed.
 		 * However, in some instances, for example when generating joins, the CLIENT table needs to be 
 		 * the first table as it will the driving table.
 		 * tableNames -> CLIENT, PERSON
 		 */
 
 		tableSpan = naturalOrderTableNames.length;
-		tableNames = reverse( naturalOrderTableNames, coreTableSpan );
+		this.tableNames = reverse( naturalOrderTableNames, coreTableSpan );
 		tableKeyColumns = reverse( naturalOrderTableKeyColumns, coreTableSpan );
 		tableKeyColumnReaders = reverse( naturalOrderTableKeyColumnReaders, coreTableSpan );
 		tableKeyColumnReaderTemplates = reverse( naturalOrderTableKeyColumnReaderTemplates, coreTableSpan );
 		subclassTableNameClosure = reverse( naturalOrderSubclassTableNameClosure, coreTableSpan );
 		subclassTableKeyColumnClosure = reverse( naturalOrderSubclassTableKeyColumnClosure, coreTableSpan );
 
 		spaces = ArrayHelper.join(
-				tableNames,
+				this.tableNames,
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
 
-		joinIter = persistentClass.getJoinClosureIterator();
+		joinItr = persistentClass.getJoinClosureIterator();
 		int j = coreTableSpan;
-		while ( joinIter.hasNext() ) {
-			Join join = (Join) joinIter.next();
+		while ( joinItr.hasNext() ) {
+			Join join = (Join) joinItr.next();
 
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
-			propertyTableNumbers[i] = getTableId( tabname, tableNames );
+			propertyTableNumbers[i] = getTableId( tabname, this.tableNames );
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
 			Integer tabnum = getTableId( tabname, subclassTableNameClosure );
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
 					final Object discriminatorValue;
 					if ( explicitDiscriminatorColumnName != null ) {
 						if ( sc.isDiscriminatorValueNull() ) {
 							discriminatorValue = NULL_DISCRIMINATOR;
 						}
 						else if ( sc.isDiscriminatorValueNotNull() ) {
 							discriminatorValue = NOT_NULL_DISCRIMINATOR;
 						}
 						else {
 							try {
 								discriminatorValue = discriminatorType.stringToObject( sc.getDiscriminatorValue() );
 							}
 							catch (ClassCastException cce) {
 								throw new MappingException( "Illegal discriminator type: " + discriminatorType.getName() );
 							}
 							catch (Exception e) {
 								throw new MappingException( "Could not format discriminator value to SQL string", e);
 							}
 						}
 					}
 					else {
 						// we now use subclass ids that are consistent across all
 						// persisters for a class hierarchy, so that the use of
 						// "foo.class = Bar" works in HQL
 						discriminatorValue = sc.getSubclassId();
 					}
 
 					subclassesByDiscriminatorValue.put( discriminatorValue, sc.getEntityName() );
 					discriminatorValues[k] = discriminatorValue.toString();
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
 
 		subclassNamesBySubclassTable = buildSubclassNamesBySubclassTableMapping( persistentClass, factory );
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap( persistentClass );
 
 		postConstruct( creationContext.getMetadata() );
 
 	}
 
 
 	/**
 	 * Used to hold the name of subclasses that each "subclass table" is part of.  For example, given a hierarchy like:
 	 * {@code JoinedEntity <- JoinedEntitySubclass <- JoinedEntitySubSubclass}..
 	 * <p/>
 	 * For the persister for JoinedEntity, we'd have:
 	 * <pre>
 	 *     subclassClosure[0] = "JoinedEntitySubSubclass"
 	 *     subclassClosure[1] = "JoinedEntitySubclass"
 	 *     subclassClosure[2] = "JoinedEntity"
 	 *
 	 *     subclassTableNameClosure[0] = "T_JoinedEntity"
 	 *     subclassTableNameClosure[1] = "T_JoinedEntitySubclass"
 	 *     subclassTableNameClosure[2] = "T_JoinedEntitySubSubclass"
 	 *
 	 *     subclassNameClosureBySubclassTable[0] = ["JoinedEntitySubSubclass", "JoinedEntitySubclass"]
 	 *     subclassNameClosureBySubclassTable[1] = ["JoinedEntitySubSubclass"]
 	 * </pre>
 	 * Note that there are only 2 entries in subclassNameClosureBySubclassTable.  That is because there are really only
 	 * 2 tables here that make up the subclass mapping, the others make up the class/superclass table mappings.  We
 	 * do not need to account for those here.  The "offset" is defined by the value of {@link #getTableSpan()}.
 	 * Therefore the corresponding row in subclassNameClosureBySubclassTable for a given row in subclassTableNameClosure
 	 * is calculated as {@code subclassTableNameClosureIndex - getTableSpan()}.
 	 * <p/>
 	 * As we consider each subclass table we can look into this array based on the subclass table's index and see
 	 * which subclasses would require it to be included.  E.g., given {@code TREAT( x AS JoinedEntitySubSubclass )},
 	 * when trying to decide whether to include join to "T_JoinedEntitySubclass" (subclassTableNameClosureIndex = 1),
 	 * we'd look at {@code subclassNameClosureBySubclassTable[0]} and see if the TREAT-AS subclass name is included in
 	 * its values.  Since {@code subclassNameClosureBySubclassTable[1]} includes "JoinedEntitySubSubclass", we'd
 	 * consider it included.
 	 * <p/>
 	 * {@link #subclassTableNameClosure} also accounts for secondary tables and we properly handle those as we
 	 * build the subclassNamesBySubclassTable array and they are therefore properly handled when we use it
 	 */
 	private final String[][] subclassNamesBySubclassTable;
 
 	/**
 	 * Essentially we are building a mapping that we can later use to determine whether a given "subclass table"
 	 * should be included in joins when JPA TREAT-AS is used.
 	 *
 	 * @param persistentClass
 	 * @param factory
 	 * @return
 	 */
 	private String[][] buildSubclassNamesBySubclassTableMapping(PersistentClass persistentClass, SessionFactoryImplementor factory) {
 		// this value represents the number of subclasses (and not the class itself)
 		final int numberOfSubclassTables = subclassTableNameClosure.length - coreTableSpan;
 		if ( numberOfSubclassTables == 0 ) {
 			return new String[0][];
 		}
 
 		final String[][] mapping = new String[numberOfSubclassTables][];
 		processPersistentClassHierarchy( persistentClass, true, factory, mapping );
 		return mapping;
 	}
 
 	private Set<String> processPersistentClassHierarchy(
 			PersistentClass persistentClass,
 			boolean isBase,
 			SessionFactoryImplementor factory,
 			String[][] mapping) {
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// collect all the class names that indicate that the "main table" of the given PersistentClass should be
 		// included when one of the collected class names is used in TREAT
 		final Set<String> classNames = new HashSet<String>();
 
 		final Iterator itr = persistentClass.getDirectSubclasses();
 		while ( itr.hasNext() ) {
 			final Subclass subclass = (Subclass) itr.next();
 			final Set<String> subclassSubclassNames = processPersistentClassHierarchy(
 					subclass,
 					false,
 					factory,
 					mapping
 			);
 			classNames.addAll( subclassSubclassNames );
 		}
 
 		classNames.add( persistentClass.getEntityName() );
 
 		if ( ! isBase ) {
 			MappedSuperclass msc = persistentClass.getSuperMappedSuperclass();
 			while ( msc != null ) {
 				classNames.add( msc.getMappedClass().getName() );
 				msc = msc.getSuperMappedSuperclass();
 			}
 
 			associateSubclassNamesToSubclassTableIndexes( persistentClass, classNames, mapping, factory );
 		}
 
 		return classNames;
 	}
 
 	private void associateSubclassNamesToSubclassTableIndexes(
 			PersistentClass persistentClass,
 			Set<String> classNames,
 			String[][] mapping,
 			SessionFactoryImplementor factory) {
 
 		final String tableName = persistentClass.getTable().getQualifiedName(
 				factory.getDialect(),
 				factory.getSettings().getDefaultCatalogName(),
 				factory.getSettings().getDefaultSchemaName()
 		);
 
 		associateSubclassNamesToSubclassTableIndex( tableName, classNames, mapping );
 
 		Iterator itr = persistentClass.getJoinIterator();
 		while ( itr.hasNext() ) {
 			final Join join = (Join) itr.next();
 			final String secondaryTableName = join.getTable().getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			associateSubclassNamesToSubclassTableIndex( secondaryTableName, classNames, mapping );
 		}
 	}
 
 	private void associateSubclassNamesToSubclassTableIndex(
 			String tableName,
 			Set<String> classNames,
 			String[][] mapping) {
 		// find the table's entry in the subclassTableNameClosure array
 		boolean found = false;
 		for ( int i = 0; i < subclassTableNameClosure.length; i++ ) {
 			if ( subclassTableNameClosure[i].equals( tableName ) ) {
 				found = true;
 				final int index = i - coreTableSpan;
 				if ( index < 0 || index >= mapping.length ) {
 					throw new IllegalStateException(
 							String.format(
 									"Encountered 'subclass table index' [%s] was outside expected range ( [%s] < i < [%s] )",
 									index,
 									0,
 									mapping.length
 							)
 					);
 				}
 				mapping[index] = classNames.toArray( new String[ classNames.size() ] );
 				break;
 			}
 		}
 		if ( !found ) {
 			throw new IllegalStateException(
 					String.format(
 							"Was unable to locate subclass table [%s] in 'subclassTableNameClosure'",
 							tableName
 					)
 			);
 		}
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
 		return discriminatorType;
 	}
 
 	public Object getDiscriminatorValue() {
 		return discriminatorValue;
 	}
 
 	@Override
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLString;
 	}
 
 	@Override
 	public String getDiscriminatorColumnName() {
 		return explicitDiscriminatorColumnName == null
 				? super.getDiscriminatorColumnName()
 				: explicitDiscriminatorColumnName;
 	}
 
 	@Override
 	public String getDiscriminatorColumnReaders() {
 		return getDiscriminatorColumnName();
 	}
 
 	@Override
 	public String getDiscriminatorColumnReaderTemplate() {
 		return getDiscriminatorColumnName();
 	}
 
 	protected String getDiscriminatorAlias() {
 		return discriminatorAlias;
 	}
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		return (String) subclassesByDiscriminatorValue.get( value );
 	}
 
 	@Override
 	protected void addDiscriminatorToInsert(Insert insert) {
 		if ( explicitDiscriminatorColumnName != null ) {
 			insert.addColumn( explicitDiscriminatorColumnName, getDiscriminatorSQLValue() );
 		}
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
 
 	public void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		if ( hasSubclasses() ) {
 			if ( explicitDiscriminatorColumnName == null ) {
 				select.setExtraSelectList( discriminatorFragment( name ), getDiscriminatorAlias() );
 			}
 			else {
 				select.addColumn( name, explicitDiscriminatorColumnName, discriminatorAlias );
 			}
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
 
 	@Override
 	public String filterFragment(String alias) {
 		return hasWhere()
 				? " and " + getSQLWhereString( generateFilterConditionAlias( alias ) )
 				: "";
 	}
 
 	@Override
 	public String filterFragment(String alias, Set<String> treatAsDeclarations) {
 		return filterFragment( alias );
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
 
 	@Override
 	protected boolean isSubclassTableIndicatedByTreatAsDeclarations(
 			int subclassTableNumber,
 			Set<String> treatAsDeclarations) {
 		if ( treatAsDeclarations == null || treatAsDeclarations.isEmpty() ) {
 			return false;
 		}
 
 		final String[] inclusionSubclassNameClosure = getSubclassNameClosureBySubclassTable( subclassTableNumber );
 
 		// NOTE : we assume the entire hierarchy is joined-subclass here
 		for ( String subclassName : treatAsDeclarations ) {
 			for ( String inclusionSubclassName : inclusionSubclassNameClosure ) {
 				if ( inclusionSubclassName.equals( subclassName ) ) {
 					return true;
 				}
 			}
 		}
 
 		return false;
 	}
 
 	private String[] getSubclassNameClosureBySubclassTable(int subclassTableNumber) {
 		final int index = subclassTableNumber - getTableSpan();
 
 		if ( index > subclassNamesBySubclassTable.length ) {
 			throw new IllegalArgumentException(
 					"Given subclass table number is outside expected range [" + subclassNamesBySubclassTable.length
 							+ "] as defined by subclassTableNameClosure/subclassClosure"
 			);
 		}
 
 		return subclassNamesBySubclassTable[index];
 	}
 
 	public String getPropertyTableName(String propertyName) {
 		Integer index = getEntityMetamodel().getPropertyIndexOrNull( propertyName );
 		if ( index == null ) {
 			return null;
 		}
 		return tableNames[propertyTableNumbers[index]];
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
 			// special case where we need to force include all subclass joins
 			return Declarer.SUBCLASS;
 		}
 		return super.getSubclassPropertyDeclarer( propertyPath );
 	}
 
 	@Override
 	public int determineTableNumberForColumn(String columnName) {
 		// HHH-7630: In case the naturalOrder/identifier column is explicitly given in the ordering, check here.
 		for ( int i = 0, max = naturalOrderTableKeyColumns.length; i < max; i++ ) {
 			final String[] keyColumns = naturalOrderTableKeyColumns[i];
 			if ( ArrayHelper.contains( keyColumns, columnName ) ) {
 				return naturalOrderPropertyTableNumbers[i];
 			}
 		}
 		
 		final String[] subclassColumnNameClosure = getSubclassColumnClosure();
 		for ( int i = 0, max = subclassColumnNameClosure.length; i < max; i++ ) {
 			final boolean quoted = subclassColumnNameClosure[i].startsWith( "\"" )
 					&& subclassColumnNameClosure[i].endsWith( "\"" );
 			if ( quoted ) {
 				if ( subclassColumnNameClosure[i].equals( columnName ) ) {
 					return getSubclassColumnTableNumberClosure()[i];
 				}
 			}
 			else {
 				if ( subclassColumnNameClosure[i].equalsIgnoreCase( columnName ) ) {
 					return getSubclassColumnTableNumberClosure()[i];
 				}
 			}
 		}
 		throw new HibernateException( "Could not locate table which owns column [" + columnName + "] referenced in order-by mapping" );
 	}
 
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new DynamicFilterAliasGenerator(subclassTableNameClosure, rootAlias);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
index 66d3341f25..85db93a138 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
@@ -1,827 +1,825 @@
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
+import org.hibernate.boot.model.relational.Database;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
+import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
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
 
+		final Database database = creationContext.getMetadata().getDatabase();
+		final JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
+
 		// CLASS + TABLE
 
 		joinSpan = persistentClass.getJoinClosureSpan() + 1;
 		qualifiedTableNames = new String[joinSpan];
 		isInverseTable = new boolean[joinSpan];
 		isNullableTable = new boolean[joinSpan];
 		keyColumnNames = new String[joinSpan][];
 		final Table table = persistentClass.getRootTable();
-		qualifiedTableNames[0] = table.getQualifiedName(
-				factory.getDialect(),
-				factory.getSettings().getDefaultCatalogName(),
-				factory.getSettings().getDefaultSchemaName()
-		);
+		qualifiedTableNames[0] = determineTableName( table, jdbcEnvironment );
+
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
-			qualifiedTableNames[j] = join.getTable().getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
+			qualifiedTableNames[j] = determineTableName( join.getTable(), jdbcEnvironment );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
index 4146f6d784..47c6912af2 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
@@ -1,501 +1,486 @@
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
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
+import org.hibernate.boot.model.relational.Database;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cfg.Settings;
 import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.StaticFilterAliasGenerator;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.collections.SingletonIterator;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.persister.spi.PersisterCreationContext;
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
 	private final Object discriminatorValue;
 	private final String discriminatorSQLValue;
 	private final Map subclassByDiscriminatorValue = new HashMap();
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	//INITIALIZATION:
 
 	public UnionSubclassEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final PersisterCreationContext creationContext) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, creationContext );
 
-		final SessionFactoryImplementor factory = creationContext.getSessionFactory();
-
 		if ( getIdentifierGenerator() instanceof IdentityGenerator ) {
 			throw new MappingException(
 					"Cannot use identity column key generation with <union-subclass> mapping for: " +
 							getEntityName()
 			);
 		}
 
+		final SessionFactoryImplementor factory = creationContext.getSessionFactory();
+		final Database database = creationContext.getMetadata().getDatabase();
+		final JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
+
 		// TABLE
 
-		tableName = persistentClass.getTable().getQualifiedName(
-				factory.getDialect(),
-				factory.getSettings().getDefaultCatalogName(),
-				factory.getSettings().getDefaultSchemaName()
-		);
-		/*rootTableName = persistentClass.getRootTable().getQualifiedName( 
-				factory.getDialect(), 
-				factory.getDefaultCatalog(), 
-				factory.getDefaultSchema() 
-		);*/
+		tableName = determineTableName( persistentClass.getTable(), jdbcEnvironment );
 
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
 		customSQLInsert = new String[] {sql};
 		insertCallable = new boolean[] {callable};
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[] {checkStyle};
 
 		sql = persistentClass.getCustomSQLUpdate();
 		callable = sql != null && persistentClass.isCustomUpdateCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 				: persistentClass.getCustomSQLUpdateCheckStyle() == null
 				? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 				: persistentClass.getCustomSQLUpdateCheckStyle();
 		customSQLUpdate = new String[] {sql};
 		updateCallable = new boolean[] {callable};
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[] {checkStyle};
 
 		sql = persistentClass.getCustomSQLDelete();
 		callable = sql != null && persistentClass.isCustomDeleteCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 				: persistentClass.getCustomSQLDeleteCheckStyle() == null
 				? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 				: persistentClass.getCustomSQLDeleteCheckStyle();
 		customSQLDelete = new String[] {sql};
 		deleteCallable = new boolean[] {callable};
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[] {checkStyle};
 
 		discriminatorValue = persistentClass.getSubclassId();
 		discriminatorSQLValue = String.valueOf( persistentClass.getSubclassId() );
 
 		// PROPERTIES
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 
 		// SUBCLASSES
 		subclassByDiscriminatorValue.put(
 				persistentClass.getSubclassId(),
 				persistentClass.getEntityName()
 		);
 		if ( persistentClass.isPolymorphic() ) {
 			Iterator iter = persistentClass.getSubclassIterator();
 			int k = 1;
 			while ( iter.hasNext() ) {
 				Subclass sc = (Subclass) iter.next();
 				subclassClosure[k++] = sc.getEntityName();
 				subclassByDiscriminatorValue.put( sc.getSubclassId(), sc.getEntityName() );
 			}
 		}
 
 		//SPACES
 		//TODO: i'm not sure, but perhaps we should exclude
 		//      abstract denormalized tables?
 
 		int spacesSize = 1 + persistentClass.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = tableName;
 		Iterator iter = persistentClass.getSynchronizedTables().iterator();
 		for ( int i = 1; i < spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 
 		HashSet subclassTables = new HashSet();
 		iter = persistentClass.getSubclassTableClosureIterator();
 		while ( iter.hasNext() ) {
-			Table table = (Table) iter.next();
-			subclassTables.add(
-					table.getQualifiedName(
-							factory.getDialect(),
-							factory.getSettings().getDefaultCatalogName(),
-							factory.getSettings().getDefaultSchemaName()
-					)
-			);
+			final Table table = (Table) iter.next();
+			subclassTables.add( determineTableName( table, jdbcEnvironment ) );
 		}
 		subclassSpaces = ArrayHelper.toStringArray( subclassTables );
 
 		subquery = generateSubquery( persistentClass, creationContext.getMetadata() );
 
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
 				Table tab = (Table) iter.next();
 				if ( !tab.isAbstractUnionTable() ) {
-					String tableName = tab.getQualifiedName(
-							factory.getDialect(),
-							factory.getSettings().getDefaultCatalogName(),
-							factory.getSettings().getDefaultSchemaName()
-					);
+					final String tableName = determineTableName( tab, jdbcEnvironment );
 					tableNames.add( tableName );
 					String[] key = new String[idColumnSpan];
 					Iterator citer = tab.getPrimaryKey().getColumnIterator();
 					for ( int k = 0; k < idColumnSpan; k++ ) {
 						key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
 					}
 					keyColumns.add( key );
 				}
 			}
 
 			constraintOrderedTableNames = ArrayHelper.toStringArray( tableNames );
 			constraintOrderedKeyColumnNames = ArrayHelper.to2DStringArray( keyColumns );
 		}
 		else {
 			constraintOrderedTableNames = new String[] {tableName};
 			constraintOrderedKeyColumnNames = new String[][] {getIdentifierColumnNames()};
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap( persistentClass );
 
 		postConstruct( creationContext.getMetadata() );
 
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
 		return (String) subclassByDiscriminatorValue.get( value );
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
 				.setLockMode( lockMode )
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
 		return getTableName() + ' ' + name;
 	}
 
 	@Override
 	public String filterFragment(String name) {
 		return hasWhere()
 				? " and " + getSQLWhereString( name )
 				: "";
 	}
 
 	@Override
 	protected String filterFragment(String alias, Set<String> treatAsDeclarations) {
 		return filterFragment( alias );
 	}
 
 	public String getSubclassPropertyTableName(int i) {
 		return getTableName();//ie. the subquery! yuck!
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		select.addColumn( name, getDiscriminatorColumnName(), getDiscriminatorAlias() );
 	}
 
 	protected int[] getPropertyTableNumbersInSelect() {
 		return new int[getPropertySpan()];
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
 		return new int[getSubclassColumnClosure().length];
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return new int[getSubclassFormulaClosure().length];
 	}
 
 	protected boolean[] getTableHasColumns() {
 		return new boolean[] {true};
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return new int[getPropertySpan()];
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
 				while ( citer.hasNext() ) {
 					columns.add( citer.next() );
 				}
 			}
 		}
 
 		StringBuilder buf = new StringBuilder()
 				.append( "( " );
 
 		Iterator siter = new JoinedIterator(
 				new SingletonIterator( model ),
 				model.getSubclassIterator()
 		);
 
 		while ( siter.hasNext() ) {
 			PersistentClass clazz = (PersistentClass) siter.next();
 			Table table = clazz.getTable();
 			if ( !table.isAbstractUnionTable() ) {
 				//TODO: move to .sql package!!
 				buf.append( "select " );
 				Iterator citer = columns.iterator();
 				while ( citer.hasNext() ) {
 					Column col = (Column) citer.next();
 					if ( !table.containsColumn( col ) ) {
 						int sqlType = col.getSqlTypeCode( mapping );
 						buf.append( dialect.getSelectClauseNullString( sqlType ) )
 								.append( " as " );
 					}
 					buf.append( col.getQuotedName( dialect ) );
 					buf.append( ", " );
 				}
 				buf.append( clazz.getSubclassId() )
 						.append( " as clazz_" );
 				buf.append( " from " )
 						.append(
 								table.getQualifiedName(
 										dialect,
 										settings.getDefaultCatalogName(),
 										settings.getDefaultSchemaName()
 								)
 						);
 				buf.append( " union " );
 				if ( dialect.supportsUnionAll() ) {
 					buf.append( "all " );
 				}
 			}
 		}
 
 		if ( buf.length() > 2 ) {
 			//chop the last union (all)
 			buf.setLength( buf.length() - ( dialect.supportsUnionAll() ? 11 : 7 ) );
 		}
 
 		return buf.append( " )" ).toString();
 	}
 
 	protected String[] getSubclassTableKeyColumns(int j) {
 		if ( j != 0 ) {
 			throw new AssertionFailure( "only one table" );
 		}
 		return getIdentifierColumnNames();
 	}
 
 	public String getSubclassTableName(int j) {
 		if ( j != 0 ) {
 			throw new AssertionFailure( "only one table" );
 		}
 		return tableName;
 	}
 
 	public int getSubclassTableSpan() {
 		return 1;
 	}
 
 	protected boolean isClassOrSuperclassTable(int j) {
 		if ( j != 0 ) {
 			throw new AssertionFailure( "only one table" );
 		}
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
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new StaticFilterAliasGenerator( rootAlias );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/boot/database/qualfiedTableNaming/JdbcMocks.java b/hibernate-core/src/test/java/org/hibernate/test/boot/database/qualfiedTableNaming/JdbcMocks.java
new file mode 100644
index 0000000000..54d9a801c1
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/boot/database/qualfiedTableNaming/JdbcMocks.java
@@ -0,0 +1,209 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.boot.database.qualfiedTableNaming;
+
+import java.lang.reflect.InvocationHandler;
+import java.lang.reflect.Method;
+import java.lang.reflect.Proxy;
+import java.sql.Connection;
+import java.sql.DatabaseMetaData;
+import java.sql.SQLException;
+
+/**
+ * @author Steve Ebersole
+ */
+public class JdbcMocks {
+
+	public static Connection createConnection(String databaseName, int majorVersion) {
+		return createConnection( databaseName, majorVersion, -9999 );
+	}
+
+	public static Connection createConnection(String databaseName, int majorVersion, int minorVersion) {
+		DatabaseMetaDataHandler metadataHandler = new DatabaseMetaDataHandler( databaseName, majorVersion, minorVersion );
+		ConnectionHandler connectionHandler = new ConnectionHandler();
+
+		DatabaseMetaData metadataProxy = ( DatabaseMetaData ) Proxy.newProxyInstance(
+				ClassLoader.getSystemClassLoader(),
+				new Class[] {DatabaseMetaData.class},
+				metadataHandler
+		);
+
+		Connection connectionProxy = ( Connection ) Proxy.newProxyInstance(
+				ClassLoader.getSystemClassLoader(),
+				new Class[] { Connection.class },
+				connectionHandler
+		);
+
+		metadataHandler.setConnectionProxy( connectionProxy );
+		connectionHandler.setMetadataProxy( metadataProxy );
+
+		return connectionProxy;
+	}
+
+	private static class ConnectionHandler implements InvocationHandler {
+		private DatabaseMetaData metadataProxy;
+
+		public void setMetadataProxy(DatabaseMetaData metadataProxy) {
+			this.metadataProxy = metadataProxy;
+		}
+
+		@Override
+		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
+			final String methodName = method.getName();
+			if ( "getMetaData".equals( methodName ) ) {
+				return metadataProxy;
+			}
+
+			if ( "toString".equals( methodName ) ) {
+				return "Connection proxy [@" + hashCode() + "]";
+			}
+
+			if ( "hashCode".equals( methodName ) ) {
+				return Integer.valueOf( this.hashCode() );
+			}
+
+			if ( "getCatalog".equals( methodName ) ) {
+				return "DB1";
+			}
+
+			if ( "supportsRefCursors".equals( methodName ) ) {
+				return false;
+			}
+
+			if ( canThrowSQLException( method ) ) {
+				throw new SQLException();
+			}
+			else {
+				throw new UnsupportedOperationException();
+			}
+		}
+	}
+
+	private static class DatabaseMetaDataHandler implements InvocationHandler {
+		private final String databaseName;
+		private final int majorVersion;
+		private final int minorVersion;
+
+		private Connection connectionProxy;
+
+		public void setConnectionProxy(Connection connectionProxy) {
+			this.connectionProxy = connectionProxy;
+		}
+
+		private DatabaseMetaDataHandler(String databaseName, int majorVersion) {
+			this( databaseName, majorVersion, -9999 );
+		}
+
+		private DatabaseMetaDataHandler(String databaseName, int majorVersion, int minorVersion) {
+			this.databaseName = databaseName;
+			this.majorVersion = majorVersion;
+			this.minorVersion = minorVersion;
+		}
+
+		@Override
+		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
+			final String methodName = method.getName();
+			if ( "getDatabaseProductName".equals( methodName ) ) {
+				return databaseName;
+			}
+
+			if ( "getDatabaseMajorVersion".equals( methodName ) ) {
+				return Integer.valueOf( majorVersion );
+			}
+
+			if ( "getDatabaseMinorVersion".equals( methodName ) ) {
+				return Integer.valueOf( minorVersion );
+			}
+
+			if ( "getConnection".equals( methodName ) ) {
+				return connectionProxy;
+			}
+
+			if ( "toString".equals( methodName ) ) {
+				return "DatabaseMetaData proxy [db-name=" + databaseName + ", version=" + majorVersion + "]";
+			}
+
+			if ( "hashCode".equals( methodName ) ) {
+				return Integer.valueOf( this.hashCode() );
+			}
+
+			if ( "supportsNamedParameters".equals( methodName ) ) {
+				return true ;
+			}
+
+			if ( "supportsResultSetType".equals( methodName ) ) {
+				return true ;
+			}
+
+			if ( "supportsGetGeneratedKeys".equals( methodName ) ) {
+				return true ;
+			}
+
+			if ( "supportsBatchUpdates".equals( methodName ) ) {
+				return true ;
+			}
+
+			if ( "dataDefinitionIgnoredInTransactions".equals( methodName ) ) {
+				return false ;
+			}
+
+			if ( "dataDefinitionCausesTransactionCommit".equals( methodName ) ) {
+				return false ;
+			}
+
+			if ( "getSQLKeywords".equals( methodName ) ) {
+				return "after,ansi,append,attach,audit,before,bitmap,boolean,buffered,byte,cache,call,cluster,clustersize,codeset,database,datafiles,dataskip,datetime,dba,dbdate,dbmoney,debug,define,delimiter,deluxe,detach,dirty,distributions,document,each,elif,exclusive,exit,explain,express,expression,extend,extent,file,fillfactor,foreach,format,fraction,fragment,gk,hash,high,hold,hybrid,if,index,init,labeleq,labelge,labelgt,labelle,labellt,let,listing,lock,log,low,matches,maxerrors,medium,mode,modify,money,mounting,new,nvarchar,off,old,operational,optical,optimization,page,pdqpriority,pload,private,raise,range,raw,recordend,recover,referencing,rejectfile,release,remainder,rename,reserve,resolution,resource,resume,return,returning,returns,ridlist,robin,rollforward,round,row,rowids,sameas,samples,schedule,scratch,serial,share,skall,skinhibit,skshow,smallfloat,stability,standard,start,static,statistics,stdev,step,sync,synonym,system,temp,text,timeout,trace,trigger,units,unlock,variance,wait,while,xload,xunload" ;
+			}
+
+			if ( "getSQLStateType".equals( methodName ) ) {
+				return DatabaseMetaData.sqlStateXOpen ;
+			}
+
+			if ( "locatorsUpdateCopy".equals( methodName ) ) {
+				return false ;
+			}
+
+			if ( "getTypeInfo".equals( methodName ) ) {
+				com.sun.rowset.CachedRowSetImpl rowSet = new com.sun.rowset.CachedRowSetImpl();
+				return rowSet ;
+			}
+
+			if ( "storesLowerCaseIdentifiers".equals( methodName ) ) {
+				return true ;
+			}
+
+			if ( "storesUpperCaseIdentifiers".equals( methodName ) ) {
+				return false ;
+			}
+
+			if ( "getCatalogSeparator".equals( methodName ) ) {
+				return ":" ;
+			}
+
+			if ( "isCatalogAtStart".equals( methodName ) ) {
+				return true ;
+			}
+
+			if ( canThrowSQLException( method ) ) {
+				throw new SQLException();
+			}
+			else {
+				throw new UnsupportedOperationException();
+			}
+		}
+	}
+
+	private static boolean canThrowSQLException(Method method) {
+		final Class[] exceptions = method.getExceptionTypes();
+		for ( Class exceptionType : exceptions ) {
+			if ( SQLException.class.isAssignableFrom( exceptionType ) ) {
+				return true;
+			}
+		}
+		return false;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/boot/database/qualfiedTableNaming/QualifiedTableNamingTest.java b/hibernate-core/src/test/java/org/hibernate/test/boot/database/qualfiedTableNaming/QualifiedTableNamingTest.java
new file mode 100644
index 0000000000..c02271213d
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/boot/database/qualfiedTableNaming/QualifiedTableNamingTest.java
@@ -0,0 +1,129 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.boot.database.qualfiedTableNaming;
+
+import java.sql.Connection;
+import java.sql.SQLException;
+import java.util.Map;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+
+import org.hibernate.boot.model.naming.Identifier;
+import org.hibernate.boot.model.relational.Namespace;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
+import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
+import org.hibernate.mapping.Table;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.SingleTableEntityPersister;
+import org.hibernate.tool.schema.internal.StandardTableExporter;
+
+import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
+import org.junit.Test;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertNotNull;
+import static org.junit.Assert.fail;
+
+/**
+ * @author Steve Ebersole
+ */
+public class QualifiedTableNamingTest extends BaseNonConfigCoreFunctionalTestCase {
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] { Box.class };
+	}
+
+	@Override
+	protected boolean createSchema() {
+		return false;
+	}
+
+	@Override
+	protected void addSettings(Map settings) {
+		super.addSettings( settings );
+		settings.put( AvailableSettings.DIALECT, TestDialect.class );
+		settings.put( AvailableSettings.CONNECTION_PROVIDER, MockedConnectionProvider.class.getName() );
+	}
+
+	@Test
+	public void testQualifiedNameSeparator() throws Exception {
+		Namespace.Name namespaceName = new Namespace.Name(
+				Identifier.toIdentifier( "DB1" ),
+				Identifier.toIdentifier( "PUBLIC" )
+		);
+
+		String expectedName = null;
+
+		for ( Namespace namespace : metadata().getDatabase().getNamespaces() ) {
+			if ( !namespace.getName().equals( namespaceName ) ) {
+				continue;
+			}
+
+			assertEquals( 1, namespace.getTables().size() );
+
+			expectedName = metadata().getDatabase().getJdbcEnvironment().getQualifiedObjectNameFormatter().format(
+					namespace.getTables().iterator().next().getQualifiedTableName(),
+					getDialect()
+			);
+		}
+
+		assertNotNull( expectedName );
+
+		SingleTableEntityPersister persister = (SingleTableEntityPersister) sessionFactory().getEntityPersister( Box.class.getName() );
+		assertEquals( expectedName, persister.getTableName() );
+	}
+
+	@Entity(name = "Box")
+	@javax.persistence.Table(name = "Box", schema = "PUBLIC", catalog = "DB1")
+	public static class Box {
+		@Id
+		public Integer id;
+		public String value;
+	}
+
+	public static class TestDialect extends Dialect {
+		@Override
+		public NameQualifierSupport getNameQualifierSupport() {
+			return NameQualifierSupport.BOTH;
+		}
+	}
+
+	public static class MockedConnectionProvider implements ConnectionProvider {
+		private Connection connection;
+
+		@Override
+		public Connection getConnection() throws SQLException {
+			if (connection == null) {
+				connection = JdbcMocks.createConnection( "db1", 0 );
+			}
+			return connection;
+		}
+
+		@Override
+		public void closeConnection(Connection conn) throws SQLException {
+		}
+
+		@Override
+		public boolean supportsAggressiveRelease() {
+			return false;
+		}
+
+
+		@Override
+		public boolean isUnwrappableAs(Class unwrapType) {
+			return false;
+		}
+
+		@Override
+		public <T> T unwrap(Class<T> unwrapType) {
+			return null;
+		}
+	}
+
+}
