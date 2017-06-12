diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Column.java b/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
index 657b9afa27..26ec5398ff 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
@@ -1,367 +1,366 @@
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
 import java.io.Serializable;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.Mapping;
 import org.hibernate.sql.Template;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * A column of a relational database table
  * @author Gavin King
  */
 public class Column implements Selectable, Serializable, Cloneable {
 
 	public static final int DEFAULT_LENGTH = 255;
 	public static final int DEFAULT_PRECISION = 19;
 	public static final int DEFAULT_SCALE = 2;
 
 	private int length=DEFAULT_LENGTH;
 	private int precision=DEFAULT_PRECISION;
 	private int scale=DEFAULT_SCALE;
 	private Value value;
 	private int typeIndex = 0;
 	private String name;
 	private boolean nullable=true;
 	private boolean unique=false;
 	private String sqlType;
 	private Integer sqlTypeCode;
 	private boolean quoted=false;
 	int uniqueInteger;
 	private String checkConstraint;
 	private String comment;
 	private String defaultValue;
 	private String customWrite;
 	private String customRead;
 
 	public Column() {
 	}
 
 	public Column(String columnName) {
 		setName(columnName);
 	}
 
 	public int getLength() {
 		return length;
 	}
 	public void setLength(int length) {
 		this.length = length;
 	}
 	public Value getValue() {
 		return value;
 	}
 	public void setValue(Value value) {
 		this.value= value;
 	}
 	public String getName() {
 		return name;
 	}
 	public void setName(String name) {
-        /* Envers passes 'name' parameter wrapped with '`' signs if quotation required. Set 'quoted' property accordingly. */
 		if (
 			name.charAt(0)=='`' ||
-			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1
+			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually
 		) {
 			quoted=true;
 			this.name=name.substring( 1, name.length()-1 );
 		}
 		else {
 			this.name = name;
 		}
 	}
 
 	/** returns quoted name as it would be in the mapping file. */
 	public String getQuotedName() {
 		return quoted ?
 				"`" + name + "`" :
 				name;
 	}
 
 	public String getQuotedName(Dialect d) {
 		return quoted ?
 			d.openQuote() + name + d.closeQuote() :
 			name;
 	}
 	
 	/**
 	 * For any column name, generate an alias that is unique
 	 * to that column name, and also 10 characters or less
 	 * in length.
 	 */
 	public String getAlias(Dialect dialect) {
 		String alias = name;
 		String unique = Integer.toString(uniqueInteger) + '_';
 		int lastLetter = StringHelper.lastIndexOfLetter(name);
 		if ( lastLetter == -1 ) {
 			alias = "column";
 		}
 		else if ( lastLetter < name.length()-1 ) {
 			alias = name.substring(0, lastLetter+1);
 		}
 		if ( alias.length() > dialect.getMaxAliasLength() ) {
 			alias = alias.substring( 0, dialect.getMaxAliasLength() - unique.length() );
 		}
 		boolean useRawName = name.equals(alias) && 
 			!quoted && 
 			!name.toLowerCase().equals("rowid");
 		if ( useRawName ) {
 			return alias;
 		}
 		else {
 			return alias + unique;
 		}
 	}
 	
 	/**
 	 * Generate a column alias that is unique across multiple tables
 	 */
 	public String getAlias(Dialect dialect, Table table) {
 		return getAlias(dialect) + table.getUniqueInteger() + '_';
 	}
 
 	public boolean isNullable() {
 		return nullable;
 	}
 
 	public void setNullable(boolean nullable) {
 		this.nullable=nullable;
 	}
 
 	public int getTypeIndex() {
 		return typeIndex;
 	}
 	public void setTypeIndex(int typeIndex) {
 		this.typeIndex = typeIndex;
 	}
 
 	public int getSqlTypeCode(Mapping mapping) throws MappingException {
 		org.hibernate.type.Type type = getValue().getType();
 		try {
 			int sqlTypeCode = type.sqlTypes(mapping)[ getTypeIndex() ];
 			if(getSqlTypeCode()!=null && getSqlTypeCode().intValue()!=sqlTypeCode) {
 				throw new MappingException("SQLType code's does not match. mapped as " + sqlTypeCode + " but is " + getSqlTypeCode() );
 			}
 			return sqlTypeCode;
 		}
 		catch (Exception e) {
 			throw new MappingException(
 					"Could not determine type for column " +
 					name +
 					" of type " +
 					type.getClass().getName() +
 					": " +
 					e.getClass().getName(),
 					e
 				);
 		}
 	}
 
 	/**
 	 * Returns the underlying columns sqltypecode.
 	 * If null, it is because the sqltype code is unknown.
 	 * 
 	 * Use #getSqlTypeCode(Mapping) to retreive the sqltypecode used
 	 * for the columns associated Value/Type.
 	 * 
 	 * @return sqltypecode if it is set, otherwise null.
 	 */
 	public Integer getSqlTypeCode() {
 		return sqlTypeCode;
 	}
 	
 	public void setSqlTypeCode(Integer typecode) {
 		sqlTypeCode=typecode;
 	}
 	
 	public boolean isUnique() {
 		return unique;
 	}
 
 
 	public String getSqlType(Dialect dialect, Mapping mapping) throws HibernateException {
 		return sqlType==null ?
 			dialect.getTypeName( getSqlTypeCode(mapping), getLength(), getPrecision(), getScale() ) :
 			sqlType;
 	}
 
 	public boolean equals(Object object) {
 		return object instanceof Column && equals( (Column) object );
 	}
 
 	public boolean equals(Column column) {
 		if (null == column) return false;
 		if (this == column) return true;
 
 		return isQuoted() ? 
 			name.equals(column.name) :
 			name.equalsIgnoreCase(column.name);
 	}
 
 	//used also for generation of FK names!
 	public int hashCode() {
 		return isQuoted() ? 
 			name.hashCode() : 
 			name.toLowerCase().hashCode();
 	}
 
 	public String getSqlType() {
 		return sqlType;
 	}
 
 	public void setSqlType(String sqlType) {
 		this.sqlType = sqlType;
 	}
 
 	public void setUnique(boolean unique) {
 		this.unique = unique;
 	}
 
 	public boolean isQuoted() {
 		return quoted;
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + getName() + ')';
 	}
 
 	public String getCheckConstraint() {
 		return checkConstraint;
 	}
 
 	public void setCheckConstraint(String checkConstraint) {
 		this.checkConstraint = checkConstraint;
 	}
 
 	public boolean hasCheckConstraint() {
 		return checkConstraint!=null;
 	}
 
 	public String getTemplate(Dialect dialect, SQLFunctionRegistry functionRegistry) {
 		return hasCustomRead()
 				? Template.renderWhereStringTemplate( customRead, dialect, functionRegistry )
 				: Template.TEMPLATE + '.' + getQuotedName( dialect );
 	}
 
 	public boolean hasCustomRead() {
 		return ( customRead != null && customRead.length() > 0 );
 	}
 
 	public String getReadExpr(Dialect dialect) {
 		return hasCustomRead() ? customRead : getQuotedName( dialect );
 	}
 	
 	public String getWriteExpr() {
 		return ( customWrite != null && customWrite.length() > 0 ) ? customWrite : "?";
 	}
 	
 	public boolean isFormula() {
 		return false;
 	}
 
 	public String getText(Dialect d) {
 		return getQuotedName(d);
 	}
 	public String getText() {
 		return getName();
 	}
 	
 	public int getPrecision() {
 		return precision;
 	}
 	public void setPrecision(int scale) {
 		this.precision = scale;
 	}
 
 	public int getScale() {
 		return scale;
 	}
 	public void setScale(int scale) {
 		this.scale = scale;
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
 
 	public String getDefaultValue() {
 		return defaultValue;
 	}
 
 	public void setDefaultValue(String defaultValue) {
 		this.defaultValue = defaultValue;
 	}
 
 	public String getCustomWrite() {
 		return customWrite;
 	}
 
 	public void setCustomWrite(String customWrite) {
 		this.customWrite = customWrite;
 	}
 
 	public String getCustomRead() {
 		return customRead;
 	}
 
 	public void setCustomRead(String customRead) {
 		this.customRead = customRead;
 	}
 
 	public String getCanonicalName() {
 		return quoted ? name : name.toLowerCase();
 	}
 
 	/**
 	 * Shallow copy, the value is not copied
 	 */
 	protected Object clone() {
 		Column copy = new Column();
 		copy.setLength( length );
 		copy.setScale( scale );
 		copy.setValue( value );
 		copy.setTypeIndex( typeIndex );
 		copy.setName( getQuotedName() );
 		copy.setNullable( nullable );
 		copy.setPrecision( precision );
 		copy.setUnique( unique );
 		copy.setSqlType( sqlType );
 		copy.setSqlTypeCode( sqlTypeCode );
 		copy.uniqueInteger = uniqueInteger; //usually useless
 		copy.setCheckConstraint( checkConstraint );
 		copy.setComment( comment );
 		copy.setDefaultValue( defaultValue );
 		copy.setCustomRead( customRead );
 		copy.setCustomWrite( customWrite );
 		return copy;
 	}
 
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/BasicNaming.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/BasicNaming.java
index 5156c9253c..1e5db978bc 100644
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/BasicNaming.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/BasicNaming.java
@@ -1,133 +1,118 @@
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
  */
 package org.hibernate.envers.test.integration.naming;
 
 import org.hibernate.ejb.Ejb3Configuration;
 import org.hibernate.envers.test.AbstractEntityTest;
 import org.hibernate.envers.test.Priority;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Table;
 import org.junit.Test;
 
 import javax.persistence.EntityManager;
 import java.util.Arrays;
 import java.util.Iterator;
 
 /**
  * @author Adam Warski (adam at warski dot org)
- * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class BasicNaming extends AbstractEntityTest {
     private Integer id1;
     private Integer id2;
 
     public void configure(Ejb3Configuration cfg) {
         cfg.addAnnotatedClass(NamingTestEntity1.class);
     }
 
     @Test
     @Priority(10)
     public void initData() {
         NamingTestEntity1 nte1 = new NamingTestEntity1("data1");
         NamingTestEntity1 nte2 = new NamingTestEntity1("data2");
 
         // Revision 1
         EntityManager em = getEntityManager();
         em.getTransaction().begin();
 
         em.persist(nte1);
         em.persist(nte2);
 
         em.getTransaction().commit();
 
         // Revision 2
         em.getTransaction().begin();
 
         nte1 = em.find(NamingTestEntity1.class, nte1.getId());
         nte1.setData("data1'");
 
         em.getTransaction().commit();
 
         // Revision 3
         em.getTransaction().begin();
 
         nte2 = em.find(NamingTestEntity1.class, nte2.getId());
         nte2.setData("data2'");
 
         em.getTransaction().commit();
 
         //
 
         id1 = nte1.getId();
         id2 = nte2.getId();
     }
 
     @Test
     public void testRevisionsCounts() {
         assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(NamingTestEntity1.class, id1));
 
         assert Arrays.asList(1, 3).equals(getAuditReader().getRevisions(NamingTestEntity1.class, id2));
     }
 
     @Test
     public void testHistoryOfId1() {
         NamingTestEntity1 ver1 = new NamingTestEntity1(id1, "data1");
         NamingTestEntity1 ver2 = new NamingTestEntity1(id1, "data1'");
 
         assert getAuditReader().find(NamingTestEntity1.class, id1, 1).equals(ver1);
         assert getAuditReader().find(NamingTestEntity1.class, id1, 2).equals(ver2);
         assert getAuditReader().find(NamingTestEntity1.class, id1, 3).equals(ver2);
     }
 
     @Test
     public void testHistoryOfId2() {
         NamingTestEntity1 ver1 = new NamingTestEntity1(id2, "data2");
         NamingTestEntity1 ver2 = new NamingTestEntity1(id2, "data2'");
 
         assert getAuditReader().find(NamingTestEntity1.class, id2, 1).equals(ver1);
         assert getAuditReader().find(NamingTestEntity1.class, id2, 2).equals(ver1);
         assert getAuditReader().find(NamingTestEntity1.class, id2, 3).equals(ver2);
     }
 
     @Test
     public void testTableName() {
         assert "naming_test_entity_1_versions".equals(
                 getCfg().getClassMapping("org.hibernate.envers.test.integration.naming.NamingTestEntity1_AUD")
                         .getTable().getName());
     }
-
-    @Test
-    public void testEscapeEntityField() {
-        Table table = getCfg().getClassMapping("org.hibernate.envers.test.integration.naming.NamingTestEntity1_AUD").getTable();
-        Iterator<Column> columnIterator = table.getColumnIterator();
-        while (columnIterator.hasNext()) {
-            Column column = columnIterator.next();
-            if ("nte_number#".equals(column.getName())) {
-                assert column.isQuoted();
-                return;
-            }
-        }
-        assert false;
-    }
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/NamingTestEntity1.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/NamingTestEntity1.java
index 713222ea1f..2d3f1e3a8b 100644
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/NamingTestEntity1.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/NamingTestEntity1.java
@@ -1,116 +1,95 @@
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
  */
 package org.hibernate.envers.test.integration.naming;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Table;
 import org.hibernate.envers.AuditTable;
 import org.hibernate.envers.Audited;
 
 /**
  * @author Adam Warski (adam at warski dot org)
- * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 @Entity
 @Table(name="naming_test_entity_1")
 @AuditTable("naming_test_entity_1_versions")
 public class NamingTestEntity1 {
     @Id
     @GeneratedValue
     @Column(name = "nte_id")
     private Integer id;
 
     @Column(name = "nte_data")
     @Audited
     private String data;
 
-    @Column(name = "`nte_number#`")
-    @Audited
-    private Integer number;
-
     public NamingTestEntity1() {
     }
 
     public NamingTestEntity1(String data) {
         this.data = data;
     }
 
     public NamingTestEntity1(Integer id, String data) {
         this.id = id;
         this.data = data;
     }
 
-    public NamingTestEntity1(Integer id, String data, Integer number) {
-        this.id = id;
-        this.data = data;
-        this.number = number;
-    }
-
     public Integer getId() {
         return id;
     }
 
     public void setId(Integer id) {
         this.id = id;
     }
 
     public String getData() {
         return data;
     }
 
     public void setData(String data) {
         this.data = data;
     }
 
-    public Integer getNumber() {
-        return number;
-    }
-
-    public void setNumber(Integer number) {
-        this.number = number;
-    }
-
     public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof NamingTestEntity1)) return false;
 
         NamingTestEntity1 that = (NamingTestEntity1) o;
 
         if (data != null ? !data.equals(that.data) : that.data != null) return false;
-        if (number != null ? !number.equals(that.number) : that.number != null) return false;
         if (id != null ? !id.equals(that.id) : that.id != null) return false;
 
         return true;
     }
 
     public int hashCode() {
         int result;
         result = (id != null ? id.hashCode() : 0);
         result = 31 * result + (data != null ? data.hashCode() : 0);
-        result = 31 * result + (number != null ? number.hashCode() : 0);
         return result;
     }
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/quotation/QuotedFieldsEntity.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/quotation/QuotedFieldsEntity.java
new file mode 100644
index 0000000000..56f3635993
--- /dev/null
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/quotation/QuotedFieldsEntity.java
@@ -0,0 +1,83 @@
+package org.hibernate.envers.test.integration.naming.quotation;
+
+import org.hibernate.envers.Audited;
+
+import javax.persistence.*;
+
+/**
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
+ */
+@Entity
+public class QuotedFieldsEntity {
+    @Id
+    @GeneratedValue
+    @Column(name = "`id#`")
+    private Long id;
+
+    @Column(name = "`#data1`")
+    @Audited
+    private String data1;
+
+    @Column(name = "`#data2`")
+    @Audited
+    private Integer data2;
+
+    public QuotedFieldsEntity() {
+    }
+
+    public QuotedFieldsEntity(String data1, Integer data2) {
+        this.data1 = data1;
+        this.data2 = data2;
+    }
+
+    public QuotedFieldsEntity(Long id, String data1, Integer data2) {
+        this.id = id;
+        this.data1 = data1;
+        this.data2 = data2;
+    }
+
+    public Long getId() {
+        return id;
+    }
+
+    public void setId(Long id) {
+        this.id = id;
+    }
+
+    public String getData1() {
+        return data1;
+    }
+
+    public void setData1(String data1) {
+        this.data1 = data1;
+    }
+
+    public Integer getData2() {
+        return data2;
+    }
+
+    public void setData2(Integer data2) {
+        this.data2 = data2;
+    }
+
+    public boolean equals(Object o) {
+        if (this == o) return true;
+        if (!(o instanceof QuotedFieldsEntity)) return false;
+
+        QuotedFieldsEntity that = (QuotedFieldsEntity) o;
+
+        if (id != null ? !id.equals(that.id) : that.id != null) return false;
+        if (data1 != null ? !data1.equals(that.data1) : that.data1 != null) return false;
+        if (data2 != null ? !data2.equals(that.data2) : that.data2 != null) return false;
+
+        return true;
+    }
+
+    public int hashCode() {
+        int result;
+        result = (id != null ? id.hashCode() : 0);
+        result = 31 * result + (data1 != null ? data1.hashCode() : 0);
+        result = 31 * result + (data2 != null ? data2.hashCode() : 0);
+        return result;
+    }
+}
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/quotation/QuotedFieldsTest.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/quotation/QuotedFieldsTest.java
new file mode 100644
index 0000000000..b95cec295e
--- /dev/null
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/quotation/QuotedFieldsTest.java
@@ -0,0 +1,104 @@
+package org.hibernate.envers.test.integration.naming.quotation;
+
+import org.hibernate.ejb.Ejb3Configuration;
+import org.hibernate.envers.test.AbstractEntityTest;
+import org.hibernate.envers.test.Priority;
+import org.hibernate.mapping.Column;
+import org.hibernate.mapping.Table;
+import org.junit.Test;
+
+import javax.persistence.EntityManager;
+import java.util.Arrays;
+import java.util.Iterator;
+
+/**
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
+ */
+public class QuotedFieldsTest extends AbstractEntityTest {
+    private Long qfeId1 = null;
+    private Long qfeId2 = null;
+
+    public void configure(Ejb3Configuration cfg) {
+        cfg.addAnnotatedClass(QuotedFieldsEntity.class);
+    }
+
+    @Test
+    @Priority(10)
+    public void initData() {
+        QuotedFieldsEntity qfe1 = new QuotedFieldsEntity("data1", 1);
+        QuotedFieldsEntity qfe2 = new QuotedFieldsEntity("data2", 2);
+
+        // Revision 1
+        EntityManager em = getEntityManager();
+        em.getTransaction().begin();
+        em.persist(qfe1);
+        em.persist(qfe2);
+        em.getTransaction().commit();
+
+        // Revision 2
+        em.getTransaction().begin();
+        qfe1 = em.find(QuotedFieldsEntity.class, qfe1.getId());
+        qfe1.setData1("data1 changed");
+        em.getTransaction().commit();
+
+        // Revision 3
+        em.getTransaction().begin();
+        qfe2 = em.find(QuotedFieldsEntity.class, qfe2.getId());
+        qfe2.setData2(3);
+        em.getTransaction().commit();
+
+        qfeId1 = qfe1.getId();
+        qfeId2 = qfe2.getId();
+    }
+
+    @Test
+    public void testRevisionsCounts() {
+        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(QuotedFieldsEntity.class, qfeId1));
+        assert Arrays.asList(1, 3).equals(getAuditReader().getRevisions(QuotedFieldsEntity.class, qfeId2));
+    }
+
+    @Test
+    public void testHistoryOfId1() {
+        QuotedFieldsEntity ver1 = new QuotedFieldsEntity(qfeId1, "data2", 1);
+        QuotedFieldsEntity ver2 = new QuotedFieldsEntity(qfeId1, "data1 changed", 1);
+
+        assert getAuditReader().find(QuotedFieldsEntity.class, qfeId1, 1).equals(ver1);
+        assert getAuditReader().find(QuotedFieldsEntity.class, qfeId1, 2).equals(ver2);
+        assert getAuditReader().find(QuotedFieldsEntity.class, qfeId1, 3).equals(ver2);
+    }
+
+    @Test
+    public void testHistoryOfId2() {
+        QuotedFieldsEntity ver1 = new QuotedFieldsEntity(qfeId2, "data2", 2);
+        QuotedFieldsEntity ver2 = new QuotedFieldsEntity(qfeId2, "data2", 3);
+
+        assert getAuditReader().find(QuotedFieldsEntity.class, qfeId2, 1).equals(ver1);
+        assert getAuditReader().find(QuotedFieldsEntity.class, qfeId2, 2).equals(ver1);
+        assert getAuditReader().find(QuotedFieldsEntity.class, qfeId2, 3).equals(ver2);
+    }
+
+    @Test
+    public void testEscapeEntityField() {
+        Table table = getCfg().getClassMapping("org.hibernate.envers.test.integration.naming.quotation.QuotedFieldsEntity_AUD").getTable();
+        Column column1 = getColumnByName(table, "id#");
+        Column column2 = getColumnByName(table, "#data1");
+        Column column3 = getColumnByName(table, "#data2");
+        assert column1 != null;
+        assert column2 != null;
+        assert column3 != null;
+        assert column1.isQuoted();
+        assert column2.isQuoted();
+        assert column3.isQuoted();
+    }
+
+    private Column getColumnByName(Table table, String columnName) {
+        Iterator<Column> columnIterator = table.getColumnIterator();
+        while (columnIterator.hasNext()) {
+            Column column = columnIterator.next();
+            if (columnName.equals(column.getName())) {
+                return column;
+            }
+        }
+        return null;
+    }
+}
