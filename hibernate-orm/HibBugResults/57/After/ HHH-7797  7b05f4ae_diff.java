diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java b/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java
index a9c8ac33fc..5d9a34881e 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java
@@ -1,137 +1,229 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.dialect.unique;
 
 import java.util.Iterator;
 
 import org.hibernate.dialect.Dialect;
-import org.hibernate.mapping.Column;
-import org.hibernate.mapping.Table;
-import org.hibernate.mapping.UniqueKey;
+import org.hibernate.metamodel.relational.Column;
+import org.hibernate.metamodel.relational.Table;
+import org.hibernate.metamodel.relational.UniqueKey;
 
 /**
  * The default UniqueDelegate implementation for most dialects.  Uses
  * separate create/alter statements to apply uniqueness to a column.
  * 
  * @author Brett Meyer
  */
 public class DefaultUniqueDelegate implements UniqueDelegate {
 	
 	private final Dialect dialect;
 	
 	public DefaultUniqueDelegate( Dialect dialect ) {
 		this.dialect = dialect;
 	}
 
 	@Override
-	public String applyUniqueToColumn( Table table, Column column ) {
+	public String applyUniqueToColumn( org.hibernate.mapping.Table table,
+			org.hibernate.mapping.Column column ) {
 //		if ( column.isUnique()
 //				&& ( column.isNullable()
 //						|| dialect.supportsNotNullUnique() ) ) {
 //			if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
 //				// If the constraint is supported, do not add to the column syntax.
 //				UniqueKey uk = getOrCreateUniqueKey( column.getQuotedName( dialect ) + '_' );
 //				uk.addColumn( column );
 //			}
 //			else if ( dialect.supportsUnique() ) {
 //				// Otherwise, add to the column syntax if supported.
 //				sb.append( " unique" );
 //			}
 //		}
 		
-		UniqueKey uk = table.getOrCreateUniqueKey(
+		org.hibernate.mapping.UniqueKey uk = table.getOrCreateUniqueKey(
 				column.getQuotedName( dialect ) + '_' );
 		uk.addColumn( column );
 		return "";
 	}
 
 	@Override
+	public String applyUniqueToColumn( Table table, Column column ) {
+//		if ( column.isUnique()
+//				&& ( column.isNullable()
+//						|| dialect.supportsNotNullUnique() ) ) {
+//			if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
+//				// If the constraint is supported, do not add to the column syntax.
+//				UniqueKey uk = getOrCreateUniqueKey( column.getQuotedName( dialect ) + '_' );
+//				uk.addColumn( column );
+//			}
+//			else if ( dialect.supportsUnique() ) {
+//				// Otherwise, add to the column syntax if supported.
+//				sb.append( " unique" );
+//			}
+//		}
+		
+		UniqueKey uk = table.getOrCreateUniqueKey( column.getColumnName()
+				.encloseInQuotesIfQuoted( dialect ) + '_' );
+		uk.addColumn( column );
+		return "";
+	}
+
+	@Override
+	public String applyUniquesToTable( org.hibernate.mapping.Table table ) {
+		// TODO: Am I correct that this shouldn't be done unless the constraint
+		// isn't created in an alter table?
+//		Iterator uniqueKeyIterator = table.getUniqueKeyIterator();
+//		while ( uniqueKeyIterator.hasNext() ) {
+//			UniqueKey uniqueKey = (UniqueKey) uniqueKeyIterator.next();
+//			
+//			sb.append( ", " ).append( createUniqueConstraint( uniqueKey) );
+//		}
+		return "";
+	}
+
+	@Override
 	public String applyUniquesToTable( Table table ) {
 		// TODO: Am I correct that this shouldn't be done unless the constraint
 		// isn't created in an alter table?
 //		Iterator uniqueKeyIterator = table.getUniqueKeyIterator();
 //		while ( uniqueKeyIterator.hasNext() ) {
 //			UniqueKey uniqueKey = (UniqueKey) uniqueKeyIterator.next();
 //			
 //			sb.append( ", " ).append( createUniqueConstraint( uniqueKey) );
 //		}
 		return "";
 	}
 	
 	@Override
-	public String applyUniquesOnAlter( UniqueKey uniqueKey,
+	public String applyUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
 			String defaultCatalog, String defaultSchema ) {
 //		if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
 //			return super.sqlCreateString( dialect, p, defaultCatalog, defaultSchema );
 //		}
 //		else {
 //			return Index.buildSqlCreateIndexString( dialect, getName(), getTable(), getColumnIterator(), true,
 //					defaultCatalog, defaultSchema );
 //		}
 		
 		return new StringBuilder( "alter table " )
 				.append( uniqueKey.getTable().getQualifiedName(
 						dialect, defaultCatalog, defaultSchema ) )
 				.append( " add constraint " )
 				.append( uniqueKey.getName() )
 				.append( uniqueConstraintSql( uniqueKey ) )
 				.toString();
 	}
 	
 	@Override
-	public String dropUniquesOnAlter( UniqueKey uniqueKey,
+	public String applyUniquesOnAlter( UniqueKey uniqueKey  ) {
+//		if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
+//			return super.sqlCreateString( dialect, p, defaultCatalog, defaultSchema );
+//		}
+//		else {
+//			return Index.buildSqlCreateIndexString( dialect, getName(), getTable(), getColumnIterator(), true,
+//					defaultCatalog, defaultSchema );
+//		}
+		
+		return new StringBuilder( "alter table " )
+				.append( uniqueKey.getTable().getQualifiedName( dialect ) )
+				.append( " add constraint " )
+				.append( uniqueKey.getName() )
+				.append( uniqueConstraintSql( uniqueKey ) )
+				.toString();
+	}
+	
+	@Override
+	public String dropUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
 			String defaultCatalog, String defaultSchema ) {
 //		if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
 //			return super.sqlDropString( dialect, defaultCatalog, defaultSchema );
 //		}
 //		else {
 //			return Index.buildSqlDropIndexString( dialect, getTable(), getName(), defaultCatalog, defaultSchema );
 //		}
 		
 		return new StringBuilder( "alter table " )
 				.append( uniqueKey.getTable().getQualifiedName(
 						dialect, defaultCatalog, defaultSchema ) )
 				.append( " drop constraint " )
 				.append( dialect.quote( uniqueKey.getName() ) )
 				.toString();
 	}
 	
 	@Override
-	public String uniqueConstraintSql( UniqueKey uniqueKey ) {
+	public String dropUniquesOnAlter( UniqueKey uniqueKey  ) {
+//		if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
+//			return super.sqlDropString( dialect, defaultCatalog, defaultSchema );
+//		}
+//		else {
+//			return Index.buildSqlDropIndexString( dialect, getTable(), getName(), defaultCatalog, defaultSchema );
+//		}
+		
+		return new StringBuilder( "alter table " )
+				.append( uniqueKey.getTable().getQualifiedName( dialect ) )
+				.append( " drop constraint " )
+				.append( dialect.quote( uniqueKey.getName() ) )
+				.toString();
+	}
+	
+	@Override
+	public String uniqueConstraintSql( org.hibernate.mapping.UniqueKey uniqueKey ) {
 		// TODO: This may not be necessary, but not all callers currently
 		// check it on their own.  Go through their logic.
 //		if ( !isGenerated( dialect ) ) return null;
 		
 		StringBuilder sb = new StringBuilder();
 		sb.append( " unique (" );
 		Iterator columnIterator = uniqueKey.getColumnIterator();
 		while ( columnIterator.hasNext() ) {
-			Column column = (Column) columnIterator.next();
+			org.hibernate.mapping.Column column
+					= (org.hibernate.mapping.Column) columnIterator.next();
+			sb.append( column.getQuotedName( dialect ) );
+			if ( columnIterator.hasNext() ) {
+				sb.append( ", " );
+			}
+		}
+		
+		return sb.append( ')' ).toString();
+	}
+	
+	@Override
+	public String uniqueConstraintSql( UniqueKey uniqueKey ) {
+		// TODO: This may not be necessary, but not all callers currently
+		// check it on their own.  Go through their logic.
+//		if ( !isGenerated( dialect ) ) return null;
+		
+		StringBuilder sb = new StringBuilder();
+		sb.append( " unique (" );
+		Iterator columnIterator = uniqueKey.getColumns().iterator();
+		while ( columnIterator.hasNext() ) {
+			org.hibernate.mapping.Column column
+					= (org.hibernate.mapping.Column) columnIterator.next();
 			sb.append( column.getQuotedName( dialect ) );
 			if ( columnIterator.hasNext() ) {
 				sb.append( ", " );
 			}
 		}
 		
 		return sb.append( ')' ).toString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/unique/UniqueDelegate.java b/hibernate-core/src/main/java/org/hibernate/dialect/unique/UniqueDelegate.java
index cbe2e19667..042f0c8c3c 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/unique/UniqueDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/unique/UniqueDelegate.java
@@ -1,96 +1,145 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.dialect.unique;
 
-import org.hibernate.mapping.Column;
-import org.hibernate.mapping.Table;
-import org.hibernate.mapping.UniqueKey;
+import org.hibernate.metamodel.relational.Column;
+import org.hibernate.metamodel.relational.Table;
+import org.hibernate.metamodel.relational.UniqueKey;
 
 /**
  * Dialect-level delegate in charge of applying "uniqueness" to a column.
  * Uniqueness can be defined in 1 of 3 ways:
  * 
  * 1.) Add a unique constraint via separate create/alter table statements.
  * 2.) Add a unique constraint via dialect-specific syntax in table create statement.
  * 3.) Add "unique" syntax to the column itself.
  * 
  * #1 & #2 are preferred, if possible -- #3 should be solely a fall-back.
  * 
  * See HHH-7797.
  * 
  * @author Brett Meyer
  */
 public interface UniqueDelegate {
 	
 	/**
 	 * If the delegate supports unique constraints, this method should simply
 	 * create the UniqueKey on the Table.  Otherwise, the constraint isn't
 	 * supported and "unique" should be added to the column definition.
 	 * 
 	 * @param table
 	 * @param column
 	 * @return String
 	 */
+	public String applyUniqueToColumn( org.hibernate.mapping.Table table,
+			org.hibernate.mapping.Column column );
+	
+	/**
+	 * If the delegate supports unique constraints, this method should simply
+	 * create the UniqueKey on the Table.  Otherwise, the constraint isn't
+	 * supported and "unique" should be added to the column definition.
+	 * 
+	 * @param table
+	 * @param column
+	 * @return String
+	 */
 	public String applyUniqueToColumn( Table table, Column column );
 	
 	/**
 	 * If creating unique constraints in separate alter statements are not
 	 * supported, this method should return the syntax necessary to create
 	 * the constraint on the original create table statement.
 	 * 
 	 * @param table
 	 * @return String
 	 */
+	public String applyUniquesToTable( org.hibernate.mapping.Table table );
+	
+	/**
+	 * If creating unique constraints in separate alter statements are not
+	 * supported, this method should return the syntax necessary to create
+	 * the constraint on the original create table statement.
+	 * 
+	 * @param table
+	 * @return String
+	 */
 	public String applyUniquesToTable( Table table );
 	
 	/**
 	 * If creating unique constraints in separate alter statements is
 	 * supported, generate the necessary "alter" syntax for the given key.
 	 * 
 	 * @param uniqueKey
 	 * @param defaultCatalog
 	 * @param defaultSchema
 	 * @return String
 	 */
-	public String applyUniquesOnAlter( UniqueKey uniqueKey,
+	public String applyUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
 			String defaultCatalog, String defaultSchema );
 	
 	/**
+	 * If creating unique constraints in separate alter statements is
+	 * supported, generate the necessary "alter" syntax for the given key.
+	 * 
+	 * @param uniqueKey
+	 * @return String
+	 */
+	public String applyUniquesOnAlter( UniqueKey uniqueKey );
+	
+	/**
 	 * If dropping unique constraints in separate alter statements is
 	 * supported, generate the necessary "alter" syntax for the given key.
 	 * 
 	 * @param uniqueKey
 	 * @param defaultCatalog
 	 * @param defaultSchema
 	 * @return String
 	 */
-	public String dropUniquesOnAlter( UniqueKey uniqueKey,
+	public String dropUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
 			String defaultCatalog, String defaultSchema );
 	
 	/**
+	 * If dropping unique constraints in separate alter statements is
+	 * supported, generate the necessary "alter" syntax for the given key.
+	 * 
+	 * @param uniqueKey
+	 * @return String
+	 */
+	public String dropUniquesOnAlter( UniqueKey uniqueKey );
+	
+	/**
+	 * Generates the syntax necessary to create the unique constraint (reused
+	 * by all methods).  Ex: "unique (column1, column2, ...)"
+	 * 
+	 * @param uniqueKey
+	 * @return String
+	 */
+	public String uniqueConstraintSql( org.hibernate.mapping.UniqueKey uniqueKey );
+	
+	/**
 	 * Generates the syntax necessary to create the unique constraint (reused
 	 * by all methods).  Ex: "unique (column1, column2, ...)"
 	 * 
 	 * @param uniqueKey
 	 * @return String
 	 */
 	public String uniqueConstraintSql( UniqueKey uniqueKey );
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java b/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
index 4368c7e293..bf4bf07eda 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
@@ -1,71 +1,73 @@
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
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 
 /**
  * A relational unique key constraint
  *
  * @author Brett Meyer
  */
 public class UniqueKey extends Constraint {
 
 	@Override
     public String sqlConstraintString(
 			Dialect dialect,
 			String constraintName,
 			String defaultCatalog,
 			String defaultSchema) {
-		return dialect.getUniqueDelegate().uniqueConstraintSql( this );
+//		return dialect.getUniqueDelegate().uniqueConstraintSql( this );
+		// Not used.
+		return "";
 	}
 
 	@Override
     public String sqlCreateString(Dialect dialect, Mapping p,
     		String defaultCatalog, String defaultSchema) {
 		return dialect.getUniqueDelegate().applyUniquesOnAlter(
 				this, defaultCatalog, defaultSchema );
 	}
 
 	@Override
     public String sqlDropString(Dialect dialect, String defaultCatalog,
     		String defaultSchema) {
 		return dialect.getUniqueDelegate().dropUniquesOnAlter(
 				this, defaultCatalog, defaultSchema );
 	}
 
 //	@Override
 //    public boolean isGenerated(Dialect dialect) {
 //		if ( !dialect.supportsUniqueConstraintInCreateAlterTable() ) return false;
 //		if ( dialect.supportsNotNullUnique() ) return true;
 //		
 //		Iterator iter = getColumnIterator();
 //		while ( iter.hasNext() ) {
 //			// Dialect does not support "not null unique" and this column is not null.
 //			if ( ! ( (Column) iter.next() ).isNullable() ) return false;
 //		}
 //		return true;
 //	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Database.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Database.java
index f15f565790..3291b967ed 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Database.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Database.java
@@ -1,226 +1,224 @@
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
 package org.hibernate.metamodel.relational;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.Metadata;
 
 /**
  * Represents a database and manages the named schema/catalog pairs defined within.
  *
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class Database {
 	private final Schema.Name implicitSchemaName;
 
 	private final Map<Schema.Name,Schema> schemaMap = new HashMap<Schema.Name, Schema>();
 	private final List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects = new ArrayList<AuxiliaryDatabaseObject>();
 
 	public Database(Metadata.Options options) {
 		String schemaName = options.getDefaultSchemaName();
 		String catalogName = options.getDefaultCatalogName();
 		if ( options.isGloballyQuotedIdentifiers() ) {
 			schemaName = StringHelper.quote( schemaName );
 			catalogName = StringHelper.quote( catalogName );
 		}
 		implicitSchemaName = new Schema.Name( schemaName, catalogName );
 		makeSchema( implicitSchemaName );
 	}
 
 	public Schema getDefaultSchema() {
 		return schemaMap.get( implicitSchemaName );
 	}
 
 	public Schema locateSchema(Schema.Name name) {
 		if ( name.getSchema() == null && name.getCatalog() == null ) {
 			return getDefaultSchema();
 		}
 		Schema schema = schemaMap.get( name );
 		if ( schema == null ) {
 			schema = makeSchema( name );
 		}
 		return schema;
 	}
 
 	private Schema makeSchema(Schema.Name name) {
 		Schema schema;
 		schema = new Schema( name );
 		schemaMap.put( name, schema );
 		return schema;
 	}
 
 	public Schema getSchema(Identifier schema, Identifier catalog) {
 		return locateSchema( new Schema.Name( schema, catalog ) );
 	}
 
 	public Schema getSchema(String schema, String catalog) {
 		return locateSchema( new Schema.Name( Identifier.toIdentifier( schema ), Identifier.toIdentifier( catalog ) ) );
 	}
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
 		if ( auxiliaryDatabaseObject == null ) {
 			throw new IllegalArgumentException( "Auxiliary database object is null." );
 		}
 		auxiliaryDatabaseObjects.add( auxiliaryDatabaseObject );
 	}
 
 	public Iterable<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjects() {
 		return auxiliaryDatabaseObjects;
 	}
 
 	public String[] generateSchemaCreationScript(Dialect dialect) {
 		Set<String> exportIdentifiers = new HashSet<String>( 50 );
 		List<String> script = new ArrayList<String>( 50 );
 
 		for ( Schema schema : schemaMap.values() ) {
 			// TODO: create schema/catalog???
 			for ( Table table : schema.getTables() ) {
 				addSqlCreateStrings( dialect, exportIdentifiers, script, table );
 			}
 		}
 
 		for ( Schema schema : schemaMap.values() ) {
 			for ( Table table : schema.getTables() ) {
 
-				if ( ! dialect.supportsUniqueConstraintInCreateAlterTable() ) {
-					for  ( UniqueKey uniqueKey : table.getUniqueKeys() ) {
-						addSqlCreateStrings( dialect, exportIdentifiers, script, uniqueKey );
-					}
+				for  ( UniqueKey uniqueKey : table.getUniqueKeys() ) {
+					addSqlCreateStrings( dialect, exportIdentifiers, script, uniqueKey );
 				}
 
 				for ( Index index : table.getIndexes() ) {
 					addSqlCreateStrings( dialect, exportIdentifiers, script, index );
 				}
 
 				if ( dialect.hasAlterTable() ) {
 					for ( ForeignKey foreignKey : table.getForeignKeys() ) {
 						// only add the foreign key if its target is a physical table
 						if ( Table.class.isInstance( foreignKey.getTargetTable() ) ) {
 							addSqlCreateStrings( dialect, exportIdentifiers, script, foreignKey );
 						}
 					}
 				}
 
 			}
 		}
 
 		// TODO: add sql create strings from PersistentIdentifierGenerator.sqlCreateStrings()
 
 		for ( AuxiliaryDatabaseObject auxiliaryDatabaseObject : auxiliaryDatabaseObjects ) {
 			if ( auxiliaryDatabaseObject.appliesToDialect( dialect ) ) {
 				addSqlCreateStrings( dialect, exportIdentifiers, script, auxiliaryDatabaseObject );
 			}
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	public String[] generateDropSchemaScript(Dialect dialect) {
 		Set<String> exportIdentifiers = new HashSet<String>( 50 );
 		List<String> script = new ArrayList<String>( 50 );
 
 
 		// drop them in reverse order in case db needs it done that way...
 		for ( int i = auxiliaryDatabaseObjects.size() - 1 ; i >= 0 ; i-- ) {
 			AuxiliaryDatabaseObject object = auxiliaryDatabaseObjects.get( i );
 			if ( object.appliesToDialect( dialect ) ) {
 				addSqlDropStrings( dialect, exportIdentifiers, script, object );
 			}
 		}
 
 		if ( dialect.dropConstraints() ) {
 			for ( Schema schema : schemaMap.values() ) {
 				for ( Table table : schema.getTables() ) {
 					for ( ForeignKey foreignKey : table.getForeignKeys() ) {
 						// only include foreign key if the target table is physical
 						if ( foreignKey.getTargetTable() instanceof Table ) {
 							addSqlDropStrings( dialect, exportIdentifiers, script, foreignKey );
 						}
 					}
 				}
 			}
 		}
 
 		for ( Schema schema : schemaMap.values() ) {
 			for ( Table table : schema.getTables() ) {
 				addSqlDropStrings( dialect, exportIdentifiers, script, table );
 			}
 		}
 
 		// TODO: add sql drop strings from PersistentIdentifierGenerator.sqlCreateStrings()
 
 		// TODO: drop schemas/catalogs???
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	private static void addSqlDropStrings(
 			Dialect dialect,
 			Set<String> exportIdentifiers,
 			List<String> script,
 			Exportable exportable) {
 		addSqlStrings(
 				exportIdentifiers, script, exportable.getExportIdentifier(), exportable.sqlDropStrings( dialect )
 		);
 	}
 
 	private static void addSqlCreateStrings(
 			Dialect dialect,
 			Set<String> exportIdentifiers,
 			List<String> script,
 			Exportable exportable) {
 		addSqlStrings(
 				exportIdentifiers, script, exportable.getExportIdentifier(), exportable.sqlCreateStrings( dialect )
 		);
 	}
 
 	private static void addSqlStrings(
 			Set<String> exportIdentifiers,
 			List<String> script,
 			String exportIdentifier,
 			String[] sqlStrings) {
 		if ( sqlStrings == null ) {
 			return;
 		}
 		if ( exportIdentifiers.contains( exportIdentifier ) ) {
 			throw new MappingException(
 					"SQL strings added more than once for: " + exportIdentifier
 			);
 		}
 		exportIdentifiers.add( exportIdentifier );
 		script.addAll( Arrays.asList( sqlStrings ) );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Table.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Table.java
index 72ce0df0f2..1c70ac1cef 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Table.java
@@ -1,302 +1,283 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.metamodel.relational;
 
 import java.util.ArrayList;
 import java.util.LinkedHashMap;
 import java.util.List;
 
 import org.hibernate.dialect.Dialect;
 
 /**
  * Models the concept of a relational <tt>TABLE</tt> (or <tt>VIEW</tt>).
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class Table extends AbstractTableSpecification implements Exportable {
 	private final Schema database;
 	private final Identifier tableName;
 	private final ObjectName objectName;
 	private final String qualifiedName;
 
 	private final LinkedHashMap<String,Index> indexes = new LinkedHashMap<String,Index>();
 	private final LinkedHashMap<String,UniqueKey> uniqueKeys = new LinkedHashMap<String,UniqueKey>();
 	private final List<CheckConstraint> checkConstraints = new ArrayList<CheckConstraint>();
 	private final List<String> comments = new ArrayList<String>();
 
 	public Table(Schema database, String tableName) {
 		this( database, Identifier.toIdentifier( tableName ) );
 	}
 
 	public Table(Schema database, Identifier tableName) {
 		this.database = database;
 		this.tableName = tableName;
 		objectName = new ObjectName( database.getName().getSchema(), database.getName().getCatalog(), tableName );
 		this.qualifiedName = objectName.toText();
 	}
 
 	@Override
 	public Schema getSchema() {
 		return database;
 	}
 
 	public Identifier getTableName() {
 		return tableName;
 	}
 
 	@Override
 	public String getLoggableValueQualifier() {
 		return qualifiedName;
 	}
 
 	@Override
 	public String getExportIdentifier() {
 		return qualifiedName;
 	}
 
 	@Override
 	public String toLoggableString() {
 		return qualifiedName;
 	}
 
 	@Override
 	public Iterable<Index> getIndexes() {
 		return indexes.values();
 	}
 
 	public Index getOrCreateIndex(String name) {
 		if( indexes.containsKey( name ) ){
 			return indexes.get( name );
 		}
 		Index index = new Index( this, name );
 		indexes.put(name, index );
 		return index;
 	}
 
 	@Override
 	public Iterable<UniqueKey> getUniqueKeys() {
 		return uniqueKeys.values();
 	}
 
 	public UniqueKey getOrCreateUniqueKey(String name) {
 		if( uniqueKeys.containsKey( name ) ){
 			return uniqueKeys.get( name );
 		}
 		UniqueKey uniqueKey = new UniqueKey( this, name );
 		uniqueKeys.put(name, uniqueKey );
 		return uniqueKey;
 	}
 
 	@Override
 	public Iterable<CheckConstraint> getCheckConstraints() {
 		return checkConstraints;
 	}
 
 	@Override
 	public void addCheckConstraint(String checkCondition) {
         //todo ? StringHelper.isEmpty( checkCondition );
         //todo default name?
 		checkConstraints.add( new CheckConstraint( this, "", checkCondition ) );
 	}
 
 	@Override
 	public Iterable<String> getComments() {
 		return comments;
 	}
 
 	@Override
 	public void addComment(String comment) {
 		comments.add( comment );
 	}
 
 	@Override
 	public String getQualifiedName(Dialect dialect) {
 		return objectName.toText( dialect );
 	}
 
 	public String[] sqlCreateStrings(Dialect dialect) {
 		boolean hasPrimaryKey = getPrimaryKey().getColumns().iterator().hasNext();
 		StringBuilder buf =
 				new StringBuilder(
 						hasPrimaryKey ? dialect.getCreateTableString() : dialect.getCreateMultisetTableString() )
 				.append( ' ' )
 				.append( objectName.toText( dialect ) )
 				.append( " (" );
 
 
 		// TODO: fix this when identity columns are supported by new metadata (HHH-6436)
 		// for now, assume false
 		//boolean identityColumn = idValue != null && idValue.isIdentityColumn( metadata.getIdentifierGeneratorFactory(), dialect );
 		boolean isPrimaryKeyIdentity = false;
 
 		// Try to find out the name of the primary key to create it as identity if the IdentityGenerator is used
 		String pkColName = null;
 		if ( hasPrimaryKey && isPrimaryKeyIdentity ) {
 			Column pkColumn = getPrimaryKey().getColumns().iterator().next();
 			pkColName = pkColumn.getColumnName().encloseInQuotesIfQuoted( dialect );
 		}
 
 		boolean isFirst = true;
 		for ( SimpleValue simpleValue : values() ) {
 			if ( ! Column.class.isInstance( simpleValue ) ) {
 				continue;
 			}
 			if ( isFirst ) {
 				isFirst = false;
 			}
 			else {
 				buf.append( ", " );
 			}
 			Column col = ( Column ) simpleValue;
 			String colName = col.getColumnName().encloseInQuotesIfQuoted( dialect );
 
 			buf.append( colName ).append( ' ' );
 
 			if ( isPrimaryKeyIdentity && colName.equals( pkColName ) ) {
 				// to support dialects that have their own identity data type
 				if ( dialect.hasDataTypeInIdentityColumn() ) {
 					buf.append( getTypeString( col, dialect ) );
 				}
 				buf.append( ' ' )
 						.append( dialect.getIdentityColumnString( col.getDatatype().getTypeCode() ) );
 			}
 			else {
 				buf.append( getTypeString( col, dialect ) );
 
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
 
-			// If the column is 1.) unique and nullable or 2.) unique,
-			// not null, and the dialect supports unique not null			
-			if ( col.isUnique()
-					&& ( col.isNullable()
-							|| dialect.supportsNotNullUnique() ) ) {
-				if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
-					// If the constraint is supported, do not add to the column syntax.
-					UniqueKey uk = getOrCreateUniqueKey( col.getColumnName().encloseInQuotesIfQuoted( dialect ) + '_' );
-					uk.addColumn( col );
-				}
-				else if ( dialect.supportsUnique() ) {
-					// Otherwise, add to the column syntax if supported.
-					buf.append( " unique" );
-				}
+			if ( col.isUnique() ) {
+				buf.append( dialect.getUniqueDelegate().applyUniqueToColumn( this, col ) );
 			}
 
 			if ( col.getCheckCondition() != null && dialect.supportsColumnCheck() ) {
 				buf.append( " check (" )
 						.append( col.getCheckCondition() )
 						.append( ")" );
 			}
 
 			String columnComment = col.getComment();
 			if ( columnComment != null ) {
 				buf.append( dialect.getColumnComment( columnComment ) );
 			}
 		}
 		if ( hasPrimaryKey ) {
 			buf.append( ", " )
 					.append( getPrimaryKey().sqlConstraintStringInCreateTable( dialect ) );
 		}
 
-		if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
-			for ( UniqueKey uk : uniqueKeys.values() ) {
-				String constraint = uk.sqlConstraintStringInCreateTable( dialect );
-				if ( constraint != null ) {
-					buf.append( ", " ).append( constraint );
-				}
-			}
-		}
+		buf.append( dialect.getUniqueDelegate().applyUniquesToTable( this ) );
 
 		if ( dialect.supportsTableCheck() ) {
 			for ( CheckConstraint checkConstraint : checkConstraints ) {
 				buf.append( ", check (" )
 						.append( checkConstraint )
 						.append( ')' );
 			}
 		}
 
 		buf.append( ')' );
 		buf.append( dialect.getTableTypeString() );
 
 		String[] sqlStrings = new String[ comments.size() + 1 ];
 		sqlStrings[ 0 ] = buf.toString();
 
 		for ( int i = 0 ; i < comments.size(); i++ ) {
 			sqlStrings[ i + 1 ] = dialect.getTableComment( comments.get( i ) );
 		}
 
 		return sqlStrings;
 	}
 
 	private static String getTypeString(Column col, Dialect dialect) {
 		String typeString = null;
 		if ( col.getSqlType() != null ) {
 			typeString = col.getSqlType();
 		}
 		else {
 			Size size = col.getSize() == null ?
 					new Size( ) :
 					col.getSize();
 
 			typeString = dialect.getTypeName(
 						col.getDatatype().getTypeCode(),
 						size.getLength(),
 						size.getPrecision(),
 						size.getScale()
 			);
 		}
 		return typeString;
 	}
 
 	@Override
 	public String[] sqlDropStrings(Dialect dialect) {
 		StringBuilder buf = new StringBuilder( "drop table " );
 		if ( dialect.supportsIfExistsBeforeTableName() ) {
 			buf.append( "if exists " );
 		}
 		buf.append( getQualifiedName( dialect ) )
 				.append( dialect.getCascadeConstraintsString() );
 		if ( dialect.supportsIfExistsAfterTableName() ) {
 			buf.append( " if exists" );
 		}
 		return new String[] { buf.toString() };
 	}
 
 	@Override
 	public String toString() {
 		return "Table{name=" + qualifiedName + '}';
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java
index 1c5413e473..339ba1d20e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java
@@ -1,100 +1,68 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.metamodel.relational;
 
 import org.hibernate.dialect.Dialect;
 
 /**
  * Models a SQL <tt>INDEX</tt> defined as UNIQUE
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class UniqueKey extends AbstractConstraint implements Constraint {
 	protected UniqueKey(Table table, String name) {
 		super( table, name );
 	}
 
 	@Override
 	public String getExportIdentifier() {
 		StringBuilder sb = new StringBuilder( getTable().getLoggableValueQualifier() );
 		sb.append( ".UK" );
 		for ( Column column : getColumns() ) {
 			sb.append( '_' ).append( column.getColumnName().getName() );
 		}
 		return sb.toString();
 	}
 
 	@Override
-    public boolean isCreationVetoed(Dialect dialect) {
-		if ( !dialect.supportsUniqueConstraintInCreateAlterTable() ) return true;
-		if ( dialect.supportsNotNullUnique() ) return false;
-		
-		for ( Column column : getColumns() ) {
-			// Dialect does not support "not null unique" and this column is not null.
-			if ( ! column.isNullable() ) return true;
-		}
-		return false;
+	public String[] sqlCreateStrings(Dialect dialect) {
+		return new String[] {
+				dialect.getUniqueDelegate().applyUniquesOnAlter( this )
+		};
 	}
 
-	public String sqlConstraintStringInCreateTable(Dialect dialect) {
-		// TODO: This may not be necessary, but not all callers currently
-		// check it on their own.  Go through their logic.
-		if ( isCreationVetoed( dialect ) ) return null;
-				
-		StringBuilder buf = new StringBuilder( "unique (" );
-		boolean first = true;
-		for ( Column column : getColumns() ) {
-			if ( first ) {
-				first = false;
-			}
-			else {
-				buf.append( ", " );
-			}
-			buf.append( column.getColumnName().encloseInQuotesIfQuoted( dialect ) );
-		}
-		return buf.append( ')' ).toString();
+	@Override
+	public String[] sqlDropStrings(Dialect dialect) {
+		return new String[] {
+				dialect.getUniqueDelegate().dropUniquesOnAlter( this )
+		};
 	}
 
 	@Override
-    public String sqlConstraintStringInAlterTable(Dialect dialect) {
-		// TODO: This may not be necessary, but not all callers currently
-		// check it on their own.  Go through their logic.
-		if ( isCreationVetoed( dialect ) ) return null;
-				
-		StringBuilder buf = new StringBuilder(
-		dialect.getAddUniqueConstraintString( getName() ) ).append( '(' );
-		boolean first = true;
-		for ( Column column : getColumns() ) {
-			if ( first ) {
-				first = false;
-			}
-			else {
-				buf.append( ", " );
-			}
-			buf.append( column.getColumnName().encloseInQuotesIfQuoted( dialect ) );
-		}
-		return buf.append( ')' ).toString();
+    protected String sqlConstraintStringInAlterTable(Dialect dialect) {
+		// not used
+		return "";
 	}
 }
