diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Column.java b/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
index 26ec5398ff..657b9afa27 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
@@ -1,366 +1,367 @@
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
+        /* Envers passes 'name' parameter wrapped with '`' signs if quotation required. Set 'quoted' property accordingly. */
 		if (
 			name.charAt(0)=='`' ||
-			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually
+			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1
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
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/RevisionInfoConfiguration.java b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/RevisionInfoConfiguration.java
index 7f40cd7907..ba646ca3a0 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/RevisionInfoConfiguration.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/RevisionInfoConfiguration.java
@@ -1,314 +1,314 @@
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
 package org.hibernate.envers.configuration;
 import java.util.Date;
 import java.util.Iterator;
 import javax.persistence.Column;
 import org.dom4j.Document;
 import org.dom4j.DocumentHelper;
 import org.dom4j.Element;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.envers.Audited;
 import org.hibernate.envers.DefaultRevisionEntity;
 import org.hibernate.envers.RevisionEntity;
 import org.hibernate.envers.RevisionListener;
 import org.hibernate.envers.RevisionNumber;
 import org.hibernate.envers.RevisionTimestamp;
 import org.hibernate.envers.configuration.metadata.AuditTableData;
 import org.hibernate.envers.configuration.metadata.MetadataTools;
 import org.hibernate.envers.entities.PropertyData;
 import org.hibernate.envers.revisioninfo.DefaultRevisionInfoGenerator;
 import org.hibernate.envers.revisioninfo.RevisionInfoGenerator;
 import org.hibernate.envers.revisioninfo.RevisionInfoNumberReader;
 import org.hibernate.envers.revisioninfo.RevisionInfoQueryCreator;
 import org.hibernate.envers.tools.MutableBoolean;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.type.LongType;
 import org.hibernate.type.Type;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public class RevisionInfoConfiguration {
     private String revisionInfoEntityName;
     private PropertyData revisionInfoIdData;
     private PropertyData revisionInfoTimestampData;
     private Type revisionInfoTimestampType;
 
     private String revisionPropType;
     private String revisionPropSqlType;
 
     public RevisionInfoConfiguration() {
         revisionInfoEntityName = "org.hibernate.envers.DefaultRevisionEntity";
         revisionInfoIdData = new PropertyData("id", "id", "field", null);
         revisionInfoTimestampData = new PropertyData("timestamp", "timestamp", "field", null);
         revisionInfoTimestampType = new LongType();
 
         revisionPropType = "integer";
     }
 
     private Document generateDefaultRevisionInfoXmlMapping() {
         Document document = DocumentHelper.createDocument();
 
         Element class_mapping = MetadataTools.createEntity(document, new AuditTableData(null, null, null, null), null);
 
         class_mapping.addAttribute("name", revisionInfoEntityName);
         class_mapping.addAttribute("table", "REVINFO");
 
         Element idProperty = MetadataTools.addNativelyGeneratedId(class_mapping, revisionInfoIdData.getName(),
                 revisionPropType);
-        MetadataTools.addColumn(idProperty, "REV", null, 0, 0, null, null, null);
+        MetadataTools.addColumn(idProperty, "REV", null, 0, 0, null, null, null, false);
 
         Element timestampProperty = MetadataTools.addProperty(class_mapping, revisionInfoTimestampData.getName(),
                 revisionInfoTimestampType.getName(), true, false);
-        MetadataTools.addColumn(timestampProperty, "REVTSTMP", null, 0, 0, null, null, null);
+        MetadataTools.addColumn(timestampProperty, "REVTSTMP", null, 0, 0, null, null, null, false);
 
         return document;
     }
 
     private Element generateRevisionInfoRelationMapping() {
         Document document = DocumentHelper.createDocument();
         Element rev_rel_mapping = document.addElement("key-many-to-one");
         rev_rel_mapping.addAttribute("type", revisionPropType);
         rev_rel_mapping.addAttribute("class", revisionInfoEntityName);
 
         if (revisionPropSqlType != null) {
             // Putting a fake name to make Hibernate happy. It will be replaced later anyway.
-            MetadataTools.addColumn(rev_rel_mapping, "*" , null, 0, 0, revisionPropSqlType, null, null);
+            MetadataTools.addColumn(rev_rel_mapping, "*" , null, 0, 0, revisionPropSqlType, null, null, false);
         }
 
         return rev_rel_mapping;
     }
 
     private void searchForRevisionInfoCfgInProperties(XClass clazz, ReflectionManager reflectionManager,
                                     MutableBoolean revisionNumberFound, MutableBoolean revisionTimestampFound,
                                     String accessType) {
         for (XProperty property : clazz.getDeclaredProperties(accessType)) {
             RevisionNumber revisionNumber = property.getAnnotation(RevisionNumber.class);
             RevisionTimestamp revisionTimestamp = property.getAnnotation(RevisionTimestamp.class);
 
             if (revisionNumber != null) {
                 if (revisionNumberFound.isSet()) {
                     throw new MappingException("Only one property may be annotated with @RevisionNumber!");
                 }
 
                 XClass revisionNumberClass = property.getType();
                 if (reflectionManager.equals(revisionNumberClass, Integer.class) ||
                         reflectionManager.equals(revisionNumberClass, Integer.TYPE)) {
                     revisionInfoIdData = new PropertyData(property.getName(), property.getName(), accessType, null);
                     revisionNumberFound.set();
                 } else if (reflectionManager.equals(revisionNumberClass, Long.class) ||
                         reflectionManager.equals(revisionNumberClass, Long.TYPE)) {
                     revisionInfoIdData = new PropertyData(property.getName(), property.getName(), accessType, null);
                     revisionNumberFound.set();
 
                     // The default is integer
                     revisionPropType = "long";
                 } else {
                     throw new MappingException("The field annotated with @RevisionNumber must be of type " +
                             "int, Integer, long or Long");
                 }
 
                 // Getting the @Column definition of the revision number property, to later use that info to
                 // generate the same mapping for the relation from an audit table's revision number to the
                 // revision entity revision number.
                 Column revisionPropColumn = property.getAnnotation(Column.class);
                 if (revisionPropColumn != null) {
                     revisionPropSqlType = revisionPropColumn.columnDefinition();
                 }
             }
 
             if (revisionTimestamp != null) {
                 if (revisionTimestampFound.isSet()) {
                     throw new MappingException("Only one property may be annotated with @RevisionTimestamp!");
                 }
 
                 XClass revisionTimestampClass = property.getType();
                 if (reflectionManager.equals(revisionTimestampClass, Long.class) ||
                         reflectionManager.equals(revisionTimestampClass, Long.TYPE) ||
                         reflectionManager.equals(revisionTimestampClass, Date.class) ||
                         reflectionManager.equals(revisionTimestampClass, java.sql.Date.class)) {
                     revisionInfoTimestampData = new PropertyData(property.getName(), property.getName(), accessType, null);
                     revisionTimestampFound.set();
                 } else {
                     throw new MappingException("The field annotated with @RevisionTimestamp must be of type " +
                             "long, Long, java.util.Date or java.sql.Date");
                 }
             }
         }
     }
 
     private void searchForRevisionInfoCfg(XClass clazz, ReflectionManager reflectionManager,
                                           MutableBoolean revisionNumberFound, MutableBoolean revisionTimestampFound) {
         XClass superclazz = clazz.getSuperclass();
         if (!"java.lang.Object".equals(superclazz.getName())) {
             searchForRevisionInfoCfg(superclazz, reflectionManager, revisionNumberFound, revisionTimestampFound);
         }
 
         searchForRevisionInfoCfgInProperties(clazz, reflectionManager, revisionNumberFound, revisionTimestampFound,
                 "field");
         searchForRevisionInfoCfgInProperties(clazz, reflectionManager, revisionNumberFound, revisionTimestampFound,
                 "property");
     }
 
     public RevisionInfoConfigurationResult configure(Configuration cfg, ReflectionManager reflectionManager) {
         Iterator<PersistentClass> classes = (Iterator<PersistentClass>) cfg.getClassMappings();
         boolean revisionEntityFound = false;
         RevisionInfoGenerator revisionInfoGenerator = null;
 
         Class<?> revisionInfoClass = null;
 
         while (classes.hasNext()) {
             PersistentClass pc = classes.next();
             XClass clazz;
             try {
                 clazz = reflectionManager.classForName(pc.getClassName(), this.getClass());
             } catch (ClassNotFoundException e) {
                 throw new MappingException(e);
             }
 
             RevisionEntity revisionEntity = clazz.getAnnotation(RevisionEntity.class);
             if (revisionEntity != null) {
                 if (revisionEntityFound) {
                     throw new MappingException("Only one entity may be annotated with @RevisionEntity!");
                 }
 
                 // Checking if custom revision entity isn't audited
                 if (clazz.getAnnotation(Audited.class) != null) {
                     throw new MappingException("An entity annotated with @RevisionEntity cannot be audited!");
                 }
 
                 revisionEntityFound = true;
 
                 MutableBoolean revisionNumberFound = new MutableBoolean();
                 MutableBoolean revisionTimestampFound = new MutableBoolean();
 
                 searchForRevisionInfoCfg(clazz, reflectionManager, revisionNumberFound, revisionTimestampFound);
 
                 if (!revisionNumberFound.isSet()) {
                     throw new MappingException("An entity annotated with @RevisionEntity must have a field annotated " +
                             "with @RevisionNumber!");
                 }
 
                 if (!revisionTimestampFound.isSet()) {
                     throw new MappingException("An entity annotated with @RevisionEntity must have a field annotated " +
                             "with @RevisionTimestamp!");
                 }
 
                 revisionInfoEntityName = pc.getEntityName();
 
                 revisionInfoClass = pc.getMappedClass();
                 revisionInfoTimestampType = pc.getProperty(revisionInfoTimestampData.getName()).getType();
                 revisionInfoGenerator = new DefaultRevisionInfoGenerator(revisionInfoEntityName, revisionInfoClass,
                         revisionEntity.value(), revisionInfoTimestampData, isTimestampAsDate());
             }
         }
 
         // In case of a custom revision info generator, the mapping will be null.
         Document revisionInfoXmlMapping = null;
 
         if (revisionInfoGenerator == null) {
             revisionInfoClass = DefaultRevisionEntity.class;
             revisionInfoGenerator = new DefaultRevisionInfoGenerator(revisionInfoEntityName, revisionInfoClass,
                     RevisionListener.class, revisionInfoTimestampData, isTimestampAsDate());
             revisionInfoXmlMapping = generateDefaultRevisionInfoXmlMapping();
         }
 
         return new RevisionInfoConfigurationResult(
                 revisionInfoGenerator, revisionInfoXmlMapping,
                 new RevisionInfoQueryCreator(revisionInfoEntityName, revisionInfoIdData.getName(),
                         revisionInfoTimestampData.getName(), isTimestampAsDate()),
                 generateRevisionInfoRelationMapping(),
                 new RevisionInfoNumberReader(revisionInfoClass, revisionInfoIdData), revisionInfoEntityName, 
                 revisionInfoClass, revisionInfoTimestampData);
     }
     
     private boolean isTimestampAsDate() {
     	String typename = revisionInfoTimestampType.getName();
     	return "date".equals(typename) || "time".equals(typename) || "timestamp".equals(typename);
     }
 }
 
 class RevisionInfoConfigurationResult {
     private final RevisionInfoGenerator revisionInfoGenerator;
     private final Document revisionInfoXmlMapping;
     private final RevisionInfoQueryCreator revisionInfoQueryCreator;
     private final Element revisionInfoRelationMapping;
     private final RevisionInfoNumberReader revisionInfoNumberReader;
     private final String revisionInfoEntityName;
     private final Class<?> revisionInfoClass;
     private final PropertyData revisionInfoTimestampData;
 
     RevisionInfoConfigurationResult(RevisionInfoGenerator revisionInfoGenerator,
                                     Document revisionInfoXmlMapping, RevisionInfoQueryCreator revisionInfoQueryCreator,
                                     Element revisionInfoRelationMapping,
                                     RevisionInfoNumberReader revisionInfoNumberReader, String revisionInfoEntityName,  Class<?> revisionInfoClass,
                                     PropertyData revisionInfoTimestampData) {
         this.revisionInfoGenerator = revisionInfoGenerator;
         this.revisionInfoXmlMapping = revisionInfoXmlMapping;
         this.revisionInfoQueryCreator = revisionInfoQueryCreator;
         this.revisionInfoRelationMapping = revisionInfoRelationMapping;
         this.revisionInfoNumberReader = revisionInfoNumberReader;
         this.revisionInfoEntityName = revisionInfoEntityName;
         this.revisionInfoClass = revisionInfoClass;
         this.revisionInfoTimestampData = revisionInfoTimestampData;
     }
 
     public RevisionInfoGenerator getRevisionInfoGenerator() {
         return revisionInfoGenerator;
     }
 
     public Document getRevisionInfoXmlMapping() {
         return revisionInfoXmlMapping;
     }
 
     public RevisionInfoQueryCreator getRevisionInfoQueryCreator() {
         return revisionInfoQueryCreator;
     }
 
     public Element getRevisionInfoRelationMapping() {
         return revisionInfoRelationMapping;
     }
 
     public RevisionInfoNumberReader getRevisionInfoNumberReader() {
         return revisionInfoNumberReader;
     }
 
     public String getRevisionInfoEntityName() {
         return revisionInfoEntityName;
     }
 
 	public Class<?> getRevisionInfoClass() {
 		return revisionInfoClass;
 	}
 
 	public PropertyData getRevisionInfoTimestampData() {
 		return revisionInfoTimestampData;
 	}
     
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/MetadataTools.java b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/MetadataTools.java
index a860bfbd1b..c1f3898f2d 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/MetadataTools.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/MetadataTools.java
@@ -1,290 +1,303 @@
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
 package org.hibernate.envers.configuration.metadata;
 import java.util.Iterator;
 import javax.persistence.JoinColumn;
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.Element;
 import org.hibernate.envers.tools.StringTools;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class MetadataTools {
     public static Element addNativelyGeneratedId(Element parent, String name, String type) {
         Element id_mapping = parent.addElement("id");
         id_mapping.addAttribute("name", name).addAttribute("type", type);
 
         Element generator_mapping = id_mapping.addElement("generator");
         generator_mapping.addAttribute("class", "native");
         /*generator_mapping.addAttribute("class", "sequence");
         generator_mapping.addElement("param").addAttribute("name", "sequence").setText("custom");*/
 
         return id_mapping;
     }
 
     public static Element addProperty(Element parent, String name, String type, boolean insertable, boolean updateable, boolean key) {
         Element prop_mapping;
         if (key) {
             prop_mapping = parent.addElement("key-property");
         } else {
             prop_mapping = parent.addElement("property");
         }
 
         prop_mapping.addAttribute("name", name);
         prop_mapping.addAttribute("insert", Boolean.toString(insertable));
         prop_mapping.addAttribute("update", Boolean.toString(updateable));
 
         if (type != null) {
             prop_mapping.addAttribute("type", type);
         }
 
         return prop_mapping;
     }
 
     public static Element addProperty(Element parent, String name, String type, boolean insertable, boolean key) {
         return addProperty(parent, name, type, insertable, false, key);
     }
 
     private static void addOrModifyAttribute(Element parent, String name, String value) {
         Attribute attribute = parent.attribute(name);
         if (attribute == null) {
             parent.addAttribute(name, value);
         } else {
             attribute.setValue(value);
         }
     }
 
+    /**
+     * Column name shall be wrapped with '`' signs if quotation required.
+     */
     public static Element addOrModifyColumn(Element parent, String name) {
         Element column_mapping = parent.element("column");
 
         if (column_mapping == null) {
             return addColumn(parent, name, null, 0, 0, null, null, null);
         }
 
         if (!StringTools.isEmpty(name)) {
             addOrModifyAttribute(column_mapping, "name", name);
         }
 
         return column_mapping;
     }
 
+    /**
+     * Adds new <code>column</code> element. Method assumes that the value of <code>name</code> attribute is already
+     * wrapped with '`' signs if quotation required. It shall be invoked when column name is taken directly from configuration
+     * file and not from {@link org.hibernate.mapping.PersistentClass} descriptor.
+     */
     public static Element addColumn(Element parent, String name, Integer length, Integer scale, Integer precision,
 									String sqlType, String customRead, String customWrite) {
+        return addColumn(parent, name, length, scale, precision, sqlType, customRead, customWrite, false);
+    }
+
+    public static Element addColumn(Element parent, String name, Integer length, Integer scale, Integer precision,
+									String sqlType, String customRead, String customWrite, boolean quoted) {
         Element column_mapping = parent.addElement("column");
 
-        column_mapping.addAttribute("name", name);
+        column_mapping.addAttribute("name", quoted ? "`" + name + "`" : name);
         if (length != null) {
             column_mapping.addAttribute("length", length.toString());
         }
 		if (scale != 0) {
 			column_mapping.addAttribute("scale", Integer.toString(scale));
 		}
 		if (precision != 0) {
 			column_mapping.addAttribute("precision", Integer.toString(precision));
 		}
 		if (!StringTools.isEmpty(sqlType)) {
             column_mapping.addAttribute("sql-type", sqlType);
         }
 
         if (!StringTools.isEmpty(customRead)) {
             column_mapping.addAttribute("read", customRead);
         }
         if (!StringTools.isEmpty(customWrite)) {
             column_mapping.addAttribute("write", customWrite);
         }
 
         return column_mapping;
     }
 
     private static Element createEntityCommon(Document document, String type, AuditTableData auditTableData,
                                               String discriminatorValue) {
         Element hibernate_mapping = document.addElement("hibernate-mapping");
         hibernate_mapping.addAttribute("auto-import", "false");
 
         Element class_mapping = hibernate_mapping.addElement(type);
 
         if (auditTableData.getAuditEntityName() != null) {
             class_mapping.addAttribute("entity-name", auditTableData.getAuditEntityName());
         }
 
         if (discriminatorValue != null) {
             class_mapping.addAttribute("discriminator-value", discriminatorValue);
         }
 
         if (!StringTools.isEmpty(auditTableData.getAuditTableName())) {
             class_mapping.addAttribute("table", auditTableData.getAuditTableName());
         }
 
         if (!StringTools.isEmpty(auditTableData.getSchema())) {
             class_mapping.addAttribute("schema", auditTableData.getSchema());
         }
 
         if (!StringTools.isEmpty(auditTableData.getCatalog())) {
             class_mapping.addAttribute("catalog", auditTableData.getCatalog());
         }
 
         return class_mapping;
     }
 
     public static Element createEntity(Document document, AuditTableData auditTableData, String discriminatorValue) {
         return createEntityCommon(document, "class", auditTableData, discriminatorValue);
     }
 
     public static Element createSubclassEntity(Document document, String subclassType, AuditTableData auditTableData,
                                                String extendsEntityName, String discriminatorValue) {
         Element class_mapping = createEntityCommon(document, subclassType, auditTableData, discriminatorValue);
 
         class_mapping.addAttribute("extends", extendsEntityName);
 
         return class_mapping;
     }
 
     public static Element createJoin(Element parent, String tableName,
                                      String schema, String catalog) {
         Element join_mapping = parent.addElement("join");
 
         join_mapping.addAttribute("table", tableName);
 
         if (!StringTools.isEmpty(schema)) {
             join_mapping.addAttribute("schema", schema);
         }
 
         if (!StringTools.isEmpty(catalog)) {
             join_mapping.addAttribute("catalog", catalog);
         }
 
         return join_mapping;
     }
 
     public static void addColumns(Element any_mapping, Iterator<Column> columns) {
         while (columns.hasNext()) {
             addColumn(any_mapping, columns.next());
         }
     }
 
     /**
      * Adds <code>column</code> element with the following attributes (unless empty): <code>name</code>,
      * <code>length</code>, <code>scale</code>, <code>precision</code>, <code>sql-type</code>, <code>read</code>
      * and <code>write</code>.
      * @param any_mapping Parent element.
      * @param column Column descriptor.
      */
     public static void addColumn(Element any_mapping, Column column) {
         addColumn(any_mapping, column.getName(), column.getLength(), column.getScale(), column.getPrecision(),
-                  column.getSqlType(), column.getCustomRead(), column.getCustomWrite());
+                  column.getSqlType(), column.getCustomRead(), column.getCustomWrite(), column.isQuoted());
     }
 
     @SuppressWarnings({"unchecked"})
     private static void changeNamesInColumnElement(Element element, ColumnNameIterator columnNameIterator) {
         Iterator<Element> properties = element.elementIterator();
         while (properties.hasNext()) {
             Element property = properties.next();
 
             if ("column".equals(property.getName())) {
                 Attribute nameAttr = property.attribute("name");
                 if (nameAttr != null) {
                     nameAttr.setText(columnNameIterator.next());
                 }
             }
         }
     }
 
     @SuppressWarnings({"unchecked"})
     public static void prefixNamesInPropertyElement(Element element, String prefix, ColumnNameIterator columnNameIterator,
                                                     boolean changeToKey, boolean insertable) {
         Iterator<Element> properties = element.elementIterator();
         while (properties.hasNext()) {
             Element property = properties.next();
 
             if ("property".equals(property.getName())) {
                 Attribute nameAttr = property.attribute("name");
                 if (nameAttr != null) {
                     nameAttr.setText(prefix + nameAttr.getText());
                 }
 
                 changeNamesInColumnElement(property, columnNameIterator);
 
                 if (changeToKey) {
                     property.setName("key-property");
                 }
 
 				Attribute insert = property.attribute("insert");
 				insert.setText(Boolean.toString(insertable));
             }
         }
     }
 
     /**
      * Adds <code>formula</code> element.
      * @param element Parent element.
      * @param formula Formula descriptor.
      */
     public static void addFormula(Element element, Formula formula) {
         element.addElement("formula").setText(formula.getText());
     }
 
     /**
      * Adds all <code>column</code> or <code>formula</code> elements.
      * @param element Parent element.
      * @param columnIterator Iterator pointing at {@link org.hibernate.mapping.Column} and/or
      *                       {@link org.hibernate.mapping.Formula} objects.
      */
     public static void addColumnsOrFormulas(Element element, Iterator columnIterator) {
         while (columnIterator.hasNext()) {
             Object o = columnIterator.next();
             if (o instanceof Column) {
                 addColumn(element, (Column) o);
             } else if (o instanceof Formula) {
                 addFormula(element, (Formula) o);
             }
         }
     }
 
     /**
      * An iterator over column names.
      */
     public static abstract class ColumnNameIterator implements Iterator<String> { }
 
     public static ColumnNameIterator getColumnNameIterator(final Iterator<Column> columnIterator) {
         return new ColumnNameIterator() {
             public boolean hasNext() { return columnIterator.hasNext(); }
             public String next() { return columnIterator.next().getName(); }
             public void remove() { columnIterator.remove(); }
         };
     }
 
     public static ColumnNameIterator getColumnNameIterator(final JoinColumn[] joinColumns) {
         return new ColumnNameIterator() {
             int counter = 0;
             public boolean hasNext() { return counter < joinColumns.length; }
             public String next() { return joinColumns[counter++].name(); }
             public void remove() { throw new UnsupportedOperationException(); }
         };
     }
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/BasicNaming.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/BasicNaming.java
index 3145dc6516..5156c9253c 100644
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/BasicNaming.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/BasicNaming.java
@@ -1,115 +1,133 @@
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
+import org.hibernate.mapping.Column;
+import org.hibernate.mapping.Table;
 import org.junit.Test;
 
 import javax.persistence.EntityManager;
 import java.util.Arrays;
+import java.util.Iterator;
 
 /**
  * @author Adam Warski (adam at warski dot org)
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
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
+
+    @Test
+    public void testEscapeEntityField() {
+        Table table = getCfg().getClassMapping("org.hibernate.envers.test.integration.naming.NamingTestEntity1_AUD").getTable();
+        Iterator<Column> columnIterator = table.getColumnIterator();
+        while (columnIterator.hasNext()) {
+            Column column = columnIterator.next();
+            if ("nte_number#".equals(column.getName())) {
+                assert column.isQuoted();
+                return;
+            }
+        }
+        assert false;
+    }
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/NamingTestEntity1.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/NamingTestEntity1.java
index 2d3f1e3a8b..713222ea1f 100644
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/NamingTestEntity1.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/naming/NamingTestEntity1.java
@@ -1,95 +1,116 @@
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
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
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
 
+    @Column(name = "`nte_number#`")
+    @Audited
+    private Integer number;
+
     public NamingTestEntity1() {
     }
 
     public NamingTestEntity1(String data) {
         this.data = data;
     }
 
     public NamingTestEntity1(Integer id, String data) {
         this.id = id;
         this.data = data;
     }
 
+    public NamingTestEntity1(Integer id, String data, Integer number) {
+        this.id = id;
+        this.data = data;
+        this.number = number;
+    }
+
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
 
+    public Integer getNumber() {
+        return number;
+    }
+
+    public void setNumber(Integer number) {
+        this.number = number;
+    }
+
     public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof NamingTestEntity1)) return false;
 
         NamingTestEntity1 that = (NamingTestEntity1) o;
 
         if (data != null ? !data.equals(that.data) : that.data != null) return false;
+        if (number != null ? !number.equals(that.number) : that.number != null) return false;
         if (id != null ? !id.equals(that.id) : that.id != null) return false;
 
         return true;
     }
 
     public int hashCode() {
         int result;
         result = (id != null ? id.hashCode() : 0);
         result = 31 * result + (data != null ? data.hashCode() : 0);
+        result = 31 * result + (number != null ? number.hashCode() : 0);
         return result;
     }
 }
