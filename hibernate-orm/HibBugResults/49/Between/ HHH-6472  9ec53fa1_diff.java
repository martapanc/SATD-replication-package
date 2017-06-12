diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityDiscriminator.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityDiscriminator.java
index e0860db60d..724d194d8f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityDiscriminator.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityDiscriminator.java
@@ -1,74 +1,82 @@
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
 package org.hibernate.metamodel.binding;
 
+import org.hibernate.metamodel.relational.SimpleValue;
+
 /**
  * Binding of the discriminator in a entity hierarchy
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public class EntityDiscriminator {
-	private SimpleSingularAttributeBinding valueBinding;
+	private final HibernateTypeDescriptor explicitHibernateTypeDescriptor = new HibernateTypeDescriptor();
+
+	private SimpleValue boundValue;
 	private boolean forced;
 	private boolean inserted = true;
 
 	public EntityDiscriminator() {
 	}
 
-	public SimpleSingularAttributeBinding getValueBinding() {
-		return valueBinding;
+	public SimpleValue getBoundValue() {
+		return boundValue;
+	}
+
+	public void setBoundValue(SimpleValue boundValue) {
+		this.boundValue = boundValue;
 	}
 
-	public void setValueBinding(SimpleSingularAttributeBinding valueBinding) {
-		this.valueBinding = valueBinding;
+	public HibernateTypeDescriptor getExplicitHibernateTypeDescriptor() {
+		return explicitHibernateTypeDescriptor;
 	}
 
 	public boolean isForced() {
 		return forced;
 	}
 
 	public void setForced(boolean forced) {
 		this.forced = forced;
 	}
 
 	public boolean isInserted() {
 		return inserted;
 	}
 
 	public void setInserted(boolean inserted) {
 		this.inserted = inserted;
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "EntityDiscriminator" );
-		sb.append( "{valueBinding=" ).append( valueBinding );
+		sb.append( "{boundValue=" ).append( boundValue );
 		sb.append( ", forced=" ).append( forced );
 		sb.append( ", inserted=" ).append( inserted );
 		sb.append( '}' );
 		return sb.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ColumnSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ColumnSourceImpl.java
index 37e6f730a6..dc3307e4bc 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ColumnSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ColumnSourceImpl.java
@@ -1,122 +1,58 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.metamodel.source.annotations.attribute;
 
-import org.hibernate.metamodel.relational.Datatype;
-import org.hibernate.metamodel.relational.Size;
-import org.hibernate.metamodel.source.binder.ColumnSource;
-
 /**
  * @author Hardy Ferentschik
  */
-public class ColumnSourceImpl implements ColumnSource {
+public class ColumnSourceImpl extends ColumnValuesSourceImpl {
 	private final SimpleAttribute attribute;
-	private final ColumnValues columnValues;
 
 	ColumnSourceImpl(SimpleAttribute attribute) {
+		super( attribute.getColumnValues() );
 		this.attribute = attribute;
-		this.columnValues = attribute.getColumnValues();
 	}
 
 	@Override
 	public String getName() {
-		return columnValues.getName().isEmpty() ? attribute.getName() : columnValues.getName();
-	}
-
-	@Override
-	public boolean isNullable() {
-		return columnValues.isNullable();
-	}
-
-	@Override
-	public String getDefaultValue() {
-		// todo
-		return null;
-	}
-
-	@Override
-	public String getSqlType() {
-		// todo
-		return null;
-	}
-
-	@Override
-	public Datatype getDatatype() {
-		// todo
-		return null;
-	}
-
-	@Override
-	public Size getSize() {
-		return new Size(
-				columnValues.getPrecision(),
-				columnValues.getScale(),
-				columnValues.getLength(),
-				Size.LobMultiplier.NONE
-		);
+		return super.getName().isEmpty() ? attribute.getName() : super.getName();
 	}
 
 	@Override
 	public String getReadFragment() {
 		return attribute.getCustomReadFragment();
 	}
 
 	@Override
 	public String getWriteFragment() {
 		return attribute.getCustomWriteFragment();
 	}
 
 	@Override
-	public boolean isUnique() {
-		return columnValues.isUnique();
-	}
-
-	@Override
 	public String getCheckCondition() {
 		return attribute.getCheckCondition();
 	}
-
-	@Override
-	public String getComment() {
-		// todo
-		return null;
-	}
-
-	@Override
-	public boolean isIncludedInInsert() {
-		return columnValues.isInsertable();
-	}
-
-	@Override
-	public boolean isIncludedInUpdate() {
-		return columnValues.isUpdatable();
-	}
-
-	@Override
-	public String getContainingTableName() {
-		return columnValues.getTable();
-	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ColumnValuesSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ColumnValuesSourceImpl.java
new file mode 100644
index 0000000000..4cda56b3be
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ColumnValuesSourceImpl.java
@@ -0,0 +1,125 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.metamodel.source.annotations.attribute;
+
+import org.hibernate.metamodel.relational.Datatype;
+import org.hibernate.metamodel.relational.Size;
+import org.hibernate.metamodel.source.binder.ColumnSource;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ColumnValuesSourceImpl implements ColumnSource {
+	private final ColumnValues columnValues;
+
+	public ColumnValuesSourceImpl(ColumnValues columnValues) {
+		this.columnValues = columnValues;
+	}
+
+	protected ColumnValues columnValues() {
+		return columnValues;
+	}
+
+	@Override
+	public String getName() {
+		return columnValues.getName();
+	}
+
+	@Override
+	public boolean isNullable() {
+		return columnValues.isNullable();
+	}
+
+	@Override
+	public String getDefaultValue() {
+		// todo
+		return null;
+	}
+
+	@Override
+	public String getSqlType() {
+		// todo
+		return null;
+	}
+
+	@Override
+	public Datatype getDatatype() {
+		// todo
+		return null;
+	}
+
+	@Override
+	public Size getSize() {
+		return new Size(
+				columnValues.getPrecision(),
+				columnValues.getScale(),
+				columnValues.getLength(),
+				Size.LobMultiplier.NONE
+		);
+	}
+
+	@Override
+	public boolean isUnique() {
+		return columnValues.isUnique();
+	}
+
+	@Override
+	public String getComment() {
+		// todo
+		return null;
+	}
+
+	@Override
+	public boolean isIncludedInInsert() {
+		return columnValues.isInsertable();
+	}
+
+	@Override
+	public boolean isIncludedInUpdate() {
+		return columnValues.isUpdatable();
+	}
+
+	@Override
+	public String getContainingTableName() {
+		return columnValues.getTable();
+	}
+
+
+	// these come from attribute ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public String getReadFragment() {
+		return null;
+	}
+
+	@Override
+	public String getWriteFragment() {
+		return null;
+	}
+
+	@Override
+	public String getCheckCondition() {
+		return null;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/DiscriminatorSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/DiscriminatorSourceImpl.java
index b7de45094c..275ac378cb 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/DiscriminatorSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/DiscriminatorSourceImpl.java
@@ -1,50 +1,63 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.metamodel.source.annotations.attribute;
 
+import org.hibernate.metamodel.source.annotations.entity.EntityClass;
 import org.hibernate.metamodel.source.binder.DiscriminatorSource;
+import org.hibernate.metamodel.source.binder.RelationalValueSource;
 
 /**
  * @author Hardy Ferentschik
  */
-public class DiscriminatorSourceImpl extends SingularAttributeSourceImpl implements DiscriminatorSource {
+public class DiscriminatorSourceImpl implements DiscriminatorSource {
 	private final DiscriminatorColumnValues discriminatorColumnValues;
+	private final Class<?> discriminatorType;
 
-	public DiscriminatorSourceImpl(SimpleAttribute attribute) {
-		super( attribute );
-		discriminatorColumnValues = (DiscriminatorColumnValues)attribute.getColumnValues();
+	public DiscriminatorSourceImpl(EntityClass entityClass) {
+		this.discriminatorColumnValues = entityClass.getDiscriminatorColumnValues();
+		this.discriminatorType = entityClass.getDiscriminatorType();
 	}
 
 	@Override
 	public boolean isForced() {
 		return discriminatorColumnValues.isForced();
 	}
 
 	@Override
 	public boolean isInserted() {
 		return discriminatorColumnValues.isIncludedInSql();
 	}
+
+	@Override
+	public RelationalValueSource getDiscriminatorRelationalValueSource() {
+		return new ColumnValuesSourceImpl( discriminatorColumnValues );
+	}
+
+	@Override
+	public String getExplicitHibernateTypeName() {
+		return discriminatorType.getName();
+	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/SimpleAttribute.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/SimpleAttribute.java
index 4054dba553..831fe5321a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/SimpleAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/SimpleAttribute.java
@@ -1,351 +1,305 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.metamodel.source.annotations.attribute;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 import java.util.Map;
-import javax.persistence.DiscriminatorType;
 import javax.persistence.FetchType;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.DotName;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.annotations.GenerationTime;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 
 /**
  * Represent a mapped attribute (explicitly or implicitly mapped). Also used for synthetic attributes like a
  * discriminator column.
  *
  * @author Hardy Ferentschik
  */
 public class SimpleAttribute extends MappedAttribute {
 	/**
 	 * Is this property an id property (or part thereof).
 	 */
 	private final boolean isId;
 
 	/**
 	 * Is this a versioned property (annotated w/ {@code @Version}.
 	 */
 	private final boolean isVersioned;
 
 	/**
-	 * Is this property a discriminator property.
-	 */
-	private final boolean isDiscriminator;
-
-	/**
 	 * Whether a change of the property's value triggers a version increment of the entity (in case of optimistic
 	 * locking).
 	 */
 	private final boolean isOptimisticLockable;
 
 	/**
 	 * Is this property lazy loaded (see {@link javax.persistence.Basic}).
 	 */
 	private boolean isLazy = false;
 
 	/**
 	 * Is this property optional  (see {@link javax.persistence.Basic}).
 	 */
 	private boolean isOptional = true;
 
 	private PropertyGeneration propertyGeneration;
 	private boolean isInsertable = true;
 	private boolean isUpdatable = true;
 
 	/**
 	 * Defines the column values (relational values) for this property.
 	 */
 	private ColumnValues columnValues;
 
 	private final String customWriteFragment;
 	private final String customReadFragment;
 	private final String checkCondition;
 
 	public static SimpleAttribute createSimpleAttribute(String name, Class<?> type, Map<DotName, List<AnnotationInstance>> annotations) {
 		return new SimpleAttribute( name, type, annotations, false );
 	}
 
 	public static SimpleAttribute createSimpleAttribute(SimpleAttribute simpleAttribute, ColumnValues columnValues) {
 		SimpleAttribute attribute = new SimpleAttribute(
 				simpleAttribute.getName(),
 				simpleAttribute.getJavaType(),
 				simpleAttribute.annotations(),
 				false
 		);
 		attribute.columnValues = columnValues;
 		return attribute;
 	}
 
-	public static SimpleAttribute createDiscriminatorAttribute(Map<DotName, List<AnnotationInstance>> annotations) {
-		AnnotationInstance discriminatorOptionsAnnotation = JandexHelper.getSingleAnnotation(
-				annotations, JPADotNames.DISCRIMINATOR_COLUMN
-		);
-		String name = DiscriminatorColumnValues.DEFAULT_DISCRIMINATOR_COLUMN_NAME;
-		Class<?> type = String.class; // string is the discriminator default
-		if ( discriminatorOptionsAnnotation != null ) {
-			name = discriminatorOptionsAnnotation.value( "name" ).asString();
-
-			DiscriminatorType discriminatorType = Enum.valueOf(
-					DiscriminatorType.class, discriminatorOptionsAnnotation.value( "discriminatorType" ).asEnum()
-			);
-			switch ( discriminatorType ) {
-				case STRING: {
-					type = String.class;
-					break;
-				}
-				case CHAR: {
-					type = Character.class;
-					break;
-				}
-				case INTEGER: {
-					type = Integer.class;
-					break;
-				}
-				default: {
-					throw new AnnotationException( "Unsupported discriminator type: " + discriminatorType );
-				}
-			}
-		}
-		return new SimpleAttribute( name, type, annotations, true );
-	}
-
 	SimpleAttribute(String name, Class<?> type, Map<DotName, List<AnnotationInstance>> annotations, boolean isDiscriminator) {
 		super( name, type, annotations );
 
-		this.isDiscriminator = isDiscriminator;
-
 		AnnotationInstance idAnnotation = JandexHelper.getSingleAnnotation( annotations, JPADotNames.ID );
 		AnnotationInstance embeddedIdAnnotation = JandexHelper.getSingleAnnotation(
 				annotations,
 				JPADotNames.EMBEDDED_ID
 		);
 		isId = !( idAnnotation == null && embeddedIdAnnotation == null );
 
 		AnnotationInstance versionAnnotation = JandexHelper.getSingleAnnotation( annotations, JPADotNames.VERSION );
 		isVersioned = versionAnnotation != null;
 
 		if ( isDiscriminator ) {
 			columnValues = new DiscriminatorColumnValues( annotations );
 		}
 		else {
 			AnnotationInstance columnAnnotation = JandexHelper.getSingleAnnotation( annotations, JPADotNames.COLUMN );
 			columnValues = new ColumnValues( columnAnnotation );
 		}
 
 		if ( isId ) {
 			// an id must be unique and cannot be nullable
 			columnValues.setUnique( true );
 			columnValues.setNullable( false );
 		}
 
 		this.isOptimisticLockable = checkOptimisticLockAnnotation();
 
 		checkBasicAnnotation();
 		checkGeneratedAnnotation();
 
 		String[] readWrite;
 		List<AnnotationInstance> columnTransformerAnnotations = getAllColumnTransformerAnnotations();
 		readWrite = createCustomReadWrite( columnTransformerAnnotations );
 		this.customReadFragment = readWrite[0];
 		this.customWriteFragment = readWrite[1];
 		this.checkCondition = parseCheckAnnotation();
 	}
 
 	public final ColumnValues getColumnValues() {
 		return columnValues;
 	}
 
 	public boolean isId() {
 		return isId;
 	}
 
 	public boolean isVersioned() {
 		return isVersioned;
 	}
 
-	public boolean isDiscriminator() {
-		return isDiscriminator;
-	}
-
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public boolean isOptional() {
 		return isOptional;
 	}
 
 	public boolean isInsertable() {
 		return isInsertable;
 	}
 
 	public boolean isUpdatable() {
 		return isUpdatable;
 	}
 
 	public PropertyGeneration getPropertyGeneration() {
 		return propertyGeneration;
 	}
 
 	public boolean isOptimisticLockable() {
 		return isOptimisticLockable;
 	}
 
 	public String getCustomWriteFragment() {
 		return customWriteFragment;
 	}
 
 	public String getCustomReadFragment() {
 		return customReadFragment;
 	}
 
 	public String getCheckCondition() {
 		return checkCondition;
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "SimpleAttribute" );
 		sb.append( "{isId=" ).append( isId );
 		sb.append( ", isVersioned=" ).append( isVersioned );
-		sb.append( ", isDiscriminator=" ).append( isDiscriminator );
 		sb.append( ", isOptimisticLockable=" ).append( isOptimisticLockable );
 		sb.append( ", isLazy=" ).append( isLazy );
 		sb.append( ", isOptional=" ).append( isOptional );
 		sb.append( ", propertyGeneration=" ).append( propertyGeneration );
 		sb.append( ", isInsertable=" ).append( isInsertable );
 		sb.append( ", isUpdatable=" ).append( isUpdatable );
 		sb.append( '}' );
 		return sb.toString();
 	}
 
 	private boolean checkOptimisticLockAnnotation() {
 		boolean triggersVersionIncrement = true;
 		AnnotationInstance optimisticLockAnnotation = getIfExists( HibernateDotNames.OPTIMISTIC_LOCK );
 		if ( optimisticLockAnnotation != null ) {
 			boolean exclude = optimisticLockAnnotation.value( "excluded" ).asBoolean();
 			triggersVersionIncrement = !exclude;
 		}
 		return triggersVersionIncrement;
 	}
 
 	private void checkBasicAnnotation() {
 		AnnotationInstance basicAnnotation = getIfExists( JPADotNames.BASIC );
 		if ( basicAnnotation != null ) {
 			FetchType fetchType = FetchType.LAZY;
 			AnnotationValue fetchValue = basicAnnotation.value( "fetch" );
 			if ( fetchValue != null ) {
 				fetchType = Enum.valueOf( FetchType.class, fetchValue.asEnum() );
 			}
 			this.isLazy = fetchType == FetchType.LAZY;
 
 			AnnotationValue optionalValue = basicAnnotation.value( "optional" );
 			if ( optionalValue != null ) {
 				this.isOptional = optionalValue.asBoolean();
 			}
 		}
 	}
 
 	// TODO - there is more todo for updatable and insertable. Checking the @Generated annotation is only one part (HF)
 	private void checkGeneratedAnnotation() {
 		AnnotationInstance generatedAnnotation = getIfExists( HibernateDotNames.GENERATED );
 		if ( generatedAnnotation != null ) {
 			this.isInsertable = false;
 
 			AnnotationValue generationTimeValue = generatedAnnotation.value();
 			if ( generationTimeValue != null ) {
 				GenerationTime genTime = Enum.valueOf( GenerationTime.class, generationTimeValue.asEnum() );
 				if ( GenerationTime.ALWAYS.equals( genTime ) ) {
 					this.isUpdatable = false;
 					this.propertyGeneration = PropertyGeneration.parse( genTime.toString().toLowerCase() );
 				}
 			}
 		}
 	}
 
 	private List<AnnotationInstance> getAllColumnTransformerAnnotations() {
 		List<AnnotationInstance> allColumnTransformerAnnotations = new ArrayList<AnnotationInstance>();
 
 		// not quite sure about the usefulness of @ColumnTransformers (HF)
 		AnnotationInstance columnTransformersAnnotations = getIfExists( HibernateDotNames.COLUMN_TRANSFORMERS );
 		if ( columnTransformersAnnotations != null ) {
 			AnnotationInstance[] annotationInstances = allColumnTransformerAnnotations.get( 0 ).value().asNestedArray();
 			allColumnTransformerAnnotations.addAll( Arrays.asList( annotationInstances ) );
 		}
 
 		AnnotationInstance columnTransformerAnnotation = getIfExists( HibernateDotNames.COLUMN_TRANSFORMER );
 		if ( columnTransformerAnnotation != null ) {
 			allColumnTransformerAnnotations.add( columnTransformerAnnotation );
 		}
 		return allColumnTransformerAnnotations;
 	}
 
 	private String[] createCustomReadWrite(List<AnnotationInstance> columnTransformerAnnotations) {
 		String[] readWrite = new String[2];
 
 		boolean alreadyProcessedForColumn = false;
 		for ( AnnotationInstance annotationInstance : columnTransformerAnnotations ) {
 			String forColumn = annotationInstance.value( "forColumn" ) == null ?
 					null : annotationInstance.value( "forColumn" ).asString();
 
 			if ( forColumn != null && !forColumn.equals( getName() ) ) {
 				continue;
 			}
 
 			if ( alreadyProcessedForColumn ) {
 				throw new AnnotationException( "Multiple definition of read/write conditions for column " + getName() );
 			}
 
 			readWrite[0] = annotationInstance.value( "read" ) == null ?
 					null : annotationInstance.value( "read" ).asString();
 			readWrite[1] = annotationInstance.value( "write" ) == null ?
 					null : annotationInstance.value( "write" ).asString();
 
 			alreadyProcessedForColumn = true;
 		}
 		return readWrite;
 	}
 
 	private String parseCheckAnnotation() {
 		String checkCondition = null;
 		AnnotationInstance checkAnnotation = getIfExists( HibernateDotNames.CHECK );
 		if ( checkAnnotation != null ) {
 			checkCondition = checkAnnotation.value( "constraints" ).toString();
 		}
 		return checkCondition;
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
index 04c63091a7..33ad9572ed 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
@@ -1,653 +1,702 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.metamodel.source.annotations.entity;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;
 import javax.persistence.AccessType;
+import javax.persistence.DiscriminatorType;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.annotations.PolymorphismType;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
-import org.hibernate.metamodel.source.annotations.attribute.SimpleAttribute;
+import org.hibernate.metamodel.source.annotations.attribute.DiscriminatorColumnValues;
 import org.hibernate.metamodel.source.binder.TableSource;
 
 /**
  * Represents an entity or mapped superclass configured via annotations/xml.
  *
  * @author Hardy Ferentschik
  */
 public class EntityClass extends ConfiguredClass {
 	private final IdType idType;
 	private final InheritanceType inheritanceType;
 	private final TableSource tableSource;
 	private final boolean hasOwnTable;
 	private final String explicitEntityName;
 	private final String customLoaderQueryName;
 	private final List<String> synchronizedTableNames;
 	private final String customTuplizer;
 	private final int batchSize;
 
 	private boolean isMutable;
 	private boolean isExplicitPolymorphism;
 	private OptimisticLockStyle optimisticLockStyle;
 	private String whereClause;
 	private String rowId;
 	private Caching caching;
 	private boolean isDynamicInsert;
 	private boolean isDynamicUpdate;
 	private boolean isSelectBeforeUpdate;
 	private String customPersister;
 
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private boolean isLazy;
 	private String proxy;
 
-	/**
-	 * The discriminator attribute or {@code null} in case none exists.
-	 */
-	private SimpleAttribute discriminatorAttribute;
+	private DiscriminatorColumnValues discriminatorColumnValues;
+	private Class<?> discriminatorType;
+	private String discriminatorMatchValue;
 
 	public EntityClass(
 			ClassInfo classInfo,
 			EntityClass parent,
 			AccessType hierarchyAccessType,
 			InheritanceType inheritanceType,
 			AnnotationBindingContext context) {
 		super( classInfo, hierarchyAccessType, parent, context );
 		this.inheritanceType = inheritanceType;
 		this.idType = determineIdType();
 		this.hasOwnTable = definesItsOwnTable();
 		this.explicitEntityName = determineExplicitEntityName();
 		this.tableSource = createTableSource();
 		this.customLoaderQueryName = determineCustomLoader();
 		this.synchronizedTableNames = determineSynchronizedTableNames();
 		this.customTuplizer = determineCustomTuplizer();
 		this.batchSize = determineBatchSize();
 
 		processHibernateEntitySpecificAnnotations();
 		processCustomSqlAnnotations();
 		processProxyGeneration();
 
-		if ( InheritanceType.SINGLE_TABLE.equals( inheritanceType ) ) {
-			discriminatorAttribute = SimpleAttribute.createDiscriminatorAttribute( classInfo.annotations() );
-		}
+		processDiscriminator();
+	}
+
+	public DiscriminatorColumnValues getDiscriminatorColumnValues() {
+		return discriminatorColumnValues;
 	}
 
-	public SimpleAttribute getDiscriminatorAttribute() {
-		return discriminatorAttribute;
+	public Class<?> getDiscriminatorType() {
+		return discriminatorType;
 	}
 
 	public IdType getIdType() {
 		return idType;
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return isExplicitPolymorphism;
 	}
 
 	public boolean isMutable() {
 		return isMutable;
 	}
 
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		return optimisticLockStyle;
 	}
 
 	public String getWhereClause() {
 		return whereClause;
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public Caching getCaching() {
 		return caching;
 	}
 
 	public TableSource getTableSource() {
 		if ( definesItsOwnTable() ) {
 			return tableSource;
 		}
 		else {
 			return ( (EntityClass) getParent() ).getTableSource();
 		}
 	}
 
 	public String getExplicitEntityName() {
 		return explicitEntityName;
 	}
 
 	public String getEntityName() {
 		return getConfiguredClass().getSimpleName();
 	}
 
 	public boolean isDynamicInsert() {
 		return isDynamicInsert;
 	}
 
 	public boolean isDynamicUpdate() {
 		return isDynamicUpdate;
 	}
 
 	public boolean isSelectBeforeUpdate() {
 		return isSelectBeforeUpdate;
 	}
 
 	public String getCustomLoaderQueryName() {
 		return customLoaderQueryName;
 	}
 
 	public CustomSQL getCustomInsert() {
 		return customInsert;
 	}
 
 	public CustomSQL getCustomUpdate() {
 		return customUpdate;
 	}
 
 	public CustomSQL getCustomDelete() {
 		return customDelete;
 	}
 
 	public List<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
 
 	public String getCustomPersister() {
 		return customPersister;
 	}
 
 	public String getCustomTuplizer() {
 		return customTuplizer;
 	}
 
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public String getProxy() {
 		return proxy;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public boolean isEntityRoot() {
 		return getParent() == null;
 	}
 
 	private String determineExplicitEntityName() {
 		final AnnotationInstance jpaEntityAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), JPADotNames.ENTITY
 		);
 
 		return JandexHelper.getValue( jpaEntityAnnotation, "name", String.class );
 	}
 
 
 	private boolean definesItsOwnTable() {
 		return !InheritanceType.SINGLE_TABLE.equals( inheritanceType ) || isEntityRoot();
 	}
 
 	private String processTableAnnotation() {
 		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(),
 				JPADotNames.TABLE
 		);
 
 		String tableName = null;
 		if ( tableAnnotation != null ) {
 			String explicitTableName = JandexHelper.getValue( tableAnnotation, "name", String.class );
 			if ( StringHelper.isNotEmpty( explicitTableName ) ) {
 				tableName = getContext().getNamingStrategy().tableName( explicitTableName );
 				if ( getContext().isGloballyQuotedIdentifiers() && !Identifier.isQuoted( explicitTableName ) ) {
 					tableName = StringHelper.quote( tableName );
 				}
 			}
 		}
 		return tableName;
 	}
 
 	private IdType determineIdType() {
 		List<AnnotationInstance> idAnnotations = findIdAnnotations( JPADotNames.ID );
 		List<AnnotationInstance> embeddedIdAnnotations = findIdAnnotations( JPADotNames.EMBEDDED_ID );
 
 		if ( !idAnnotations.isEmpty() && !embeddedIdAnnotations.isEmpty() ) {
 			throw new MappingException(
 					"@EmbeddedId and @Id cannot be used together. Check the configuration for " + getName() + "."
 			);
 		}
 
 		if ( !embeddedIdAnnotations.isEmpty() ) {
 			if ( embeddedIdAnnotations.size() == 1 ) {
 				return IdType.EMBEDDED;
 			}
 			else {
 				throw new AnnotationException( "Multiple @EmbeddedId annotations are not allowed" );
 			}
 		}
 
 		if ( !idAnnotations.isEmpty() ) {
 			if ( idAnnotations.size() == 1 ) {
 				return IdType.SIMPLE;
 			}
 			else {
 				return IdType.COMPOSED;
 			}
 		}
 		return IdType.NONE;
 	}
 
 	private List<AnnotationInstance> findIdAnnotations(DotName idAnnotationType) {
 		List<AnnotationInstance> idAnnotationList = new ArrayList<AnnotationInstance>();
 		if ( getClassInfo().annotations().get( idAnnotationType ) != null ) {
 			idAnnotationList.addAll( getClassInfo().annotations().get( idAnnotationType ) );
 		}
 		ConfiguredClass parent = getParent();
 		while ( parent != null && ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( parent.getConfiguredClassType() ) ||
 				ConfiguredClassType.NON_ENTITY.equals( parent.getConfiguredClassType() ) ) ) {
 			if ( parent.getClassInfo().annotations().get( idAnnotationType ) != null ) {
 				idAnnotationList.addAll( parent.getClassInfo().annotations().get( idAnnotationType ) );
 			}
 			parent = parent.getParent();
 
 		}
 		return idAnnotationList;
 	}
 
+	private void processDiscriminator() {
+		if ( !InheritanceType.SINGLE_TABLE.equals( inheritanceType ) ) {
+			return;
+		}
+
+		final AnnotationInstance discriminatorValueAnnotation = JandexHelper.getSingleAnnotation(
+				getClassInfo(), JPADotNames.DISCRIMINATOR_VALUE
+		);
+		if ( discriminatorValueAnnotation != null ) {
+			this.discriminatorMatchValue = discriminatorValueAnnotation.value().asString();
+		}
+
+		final AnnotationInstance discriminatorOptionsAnnotation = JandexHelper.getSingleAnnotation(
+				getClassInfo(), JPADotNames.DISCRIMINATOR_COLUMN
+		);
+		Class<?> type = String.class; // string is the discriminator default
+		if ( discriminatorOptionsAnnotation != null ) {
+			DiscriminatorType discriminatorType = Enum.valueOf(
+					DiscriminatorType.class, discriminatorOptionsAnnotation.value( "discriminatorType" ).asEnum()
+			);
+			switch ( discriminatorType ) {
+				case STRING: {
+					type = String.class;
+					break;
+				}
+				case CHAR: {
+					type = Character.class;
+					break;
+				}
+				case INTEGER: {
+					type = Integer.class;
+					break;
+				}
+				default: {
+					throw new AnnotationException( "Unsupported discriminator type: " + discriminatorType );
+				}
+			}
+		}
+
+		discriminatorColumnValues = new DiscriminatorColumnValues( getClassInfo().annotations() );
+		discriminatorType = type;
+	}
+
 	private void processHibernateEntitySpecificAnnotations() {
 		final AnnotationInstance hibernateEntityAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.ENTITY
 		);
 
 		// see HHH-6400
 		PolymorphismType polymorphism = PolymorphismType.IMPLICIT;
 		if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "polymorphism" ) != null ) {
 			polymorphism = PolymorphismType.valueOf( hibernateEntityAnnotation.value( "polymorphism" ).asEnum() );
 		}
 		isExplicitPolymorphism = polymorphism == PolymorphismType.EXPLICIT;
 
 		// see HHH-6401
 		OptimisticLockType optimisticLockType = OptimisticLockType.VERSION;
 		if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "optimisticLock" ) != null ) {
 			optimisticLockType = OptimisticLockType.valueOf(
 					hibernateEntityAnnotation.value( "optimisticLock" )
 							.asEnum()
 			);
 		}
 		optimisticLockStyle = OptimisticLockStyle.valueOf( optimisticLockType.name() );
 
 		final AnnotationInstance hibernateImmutableAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.IMMUTABLE
 		);
 		isMutable = hibernateImmutableAnnotation == null
 				&& hibernateEntityAnnotation != null
 				&& hibernateEntityAnnotation.value( "mutable" ) != null
 				&& hibernateEntityAnnotation.value( "mutable" ).asBoolean();
 
 
 		final AnnotationInstance whereAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.WHERE
 		);
 		whereClause = whereAnnotation != null && whereAnnotation.value( "clause" ) != null ?
 				whereAnnotation.value( "clause" ).asString() : null;
 
 		final AnnotationInstance rowIdAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.ROW_ID
 		);
 		rowId = rowIdAnnotation != null && rowIdAnnotation.value() != null
 				? rowIdAnnotation.value().asString() : null;
 
 		caching = determineCachingSettings();
 
 		// see HHH-6397
 		isDynamicInsert =
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "dynamicInsert" ) != null
 						&& hibernateEntityAnnotation.value( "dynamicInsert" ).asBoolean();
 
 		// see HHH-6398
 		isDynamicUpdate =
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "dynamicUpdate" ) != null
 						&& hibernateEntityAnnotation.value( "dynamicUpdate" ).asBoolean();
 
 
 		// see HHH-6399
 		isSelectBeforeUpdate =
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "selectBeforeUpdate" ) != null
 						&& hibernateEntityAnnotation.value( "selectBeforeUpdate" ).asBoolean();
 
 		// Custom persister
 		final String entityPersisterClass;
 		final AnnotationInstance persisterAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.PERSISTER
 		);
 		if ( persisterAnnotation == null || persisterAnnotation.value( "impl" ) == null ) {
 			if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "persister" ) != null ) {
 				entityPersisterClass = hibernateEntityAnnotation.value( "persister" ).asString();
 			}
 			else {
 				entityPersisterClass = null;
 			}
 		}
 		else {
 			if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "persister" ) != null ) {
 				// todo : error?
 			}
 			entityPersisterClass = persisterAnnotation.value( "impl" ).asString();
 		}
 		this.customPersister = entityPersisterClass;
 	}
 
 	private Caching determineCachingSettings() {
 		final AnnotationInstance hibernateCacheAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.CACHE
 		);
 		if ( hibernateCacheAnnotation != null ) {
 			final org.hibernate.cache.spi.access.AccessType accessType = hibernateCacheAnnotation.value( "usage" ) == null
 					? getContext().getMappingDefaults().getCacheAccessType()
 					: CacheConcurrencyStrategy.parse( hibernateCacheAnnotation.value( "usage" ).asEnum() )
 					.toAccessType();
 			return new Caching(
 					hibernateCacheAnnotation.value( "region" ) == null
 							? getName()
 							: hibernateCacheAnnotation.value( "region" ).asString(),
 					accessType,
 					hibernateCacheAnnotation.value( "include" ) != null
 							&& "all".equals( hibernateCacheAnnotation.value( "include" ).asString() )
 			);
 		}
 
 		final AnnotationInstance jpaCacheableAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), JPADotNames.CACHEABLE
 		);
 
 		boolean cacheable = true; // true is the default
 		if ( jpaCacheableAnnotation != null && jpaCacheableAnnotation.value() != null ) {
 			cacheable = jpaCacheableAnnotation.value().asBoolean();
 		}
 
 		final boolean doCaching;
 		switch ( getContext().getMetadataImplementor().getOptions().getSharedCacheMode() ) {
 			case ALL: {
 				doCaching = true;
 				break;
 			}
 			case ENABLE_SELECTIVE: {
 				doCaching = cacheable;
 				break;
 			}
 			case DISABLE_SELECTIVE: {
 				doCaching = jpaCacheableAnnotation == null || cacheable;
 				break;
 			}
 			default: {
 				// treat both NONE and UNSPECIFIED the same
 				doCaching = false;
 				break;
 			}
 		}
 
 		if ( !doCaching ) {
 			return null;
 		}
 
 		return new Caching(
 				getName(),
 				getContext().getMappingDefaults().getCacheAccessType(),
 				true
 		);
 	}
 
 	private TableSource createTableSource() {
 		if ( !hasOwnTable ) {
 			return null;
 		}
 
 		String schema = getContext().getMappingDefaults().getSchemaName();
 		String catalog = getContext().getMappingDefaults().getCatalogName();
 
 		final AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), JPADotNames.TABLE
 		);
 		if ( tableAnnotation != null ) {
 			final AnnotationValue schemaValue = tableAnnotation.value( "schema" );
 			if ( schemaValue != null ) {
 				schema = schemaValue.asString();
 			}
 
 			final AnnotationValue catalogValue = tableAnnotation.value( "catalog" );
 			if ( catalogValue != null ) {
 				catalog = catalogValue.asString();
 			}
 		}
 
 		if ( getContext().isGloballyQuotedIdentifiers() ) {
 			schema = StringHelper.quote( schema );
 			catalog = StringHelper.quote( catalog );
 		}
 
 		String tableName = processTableAnnotation();
 		// use the simple table name as default in case there was no table annotation
 		if ( tableName == null ) {
 			if ( explicitEntityName == null ) {
 				tableName = getConfiguredClass().getSimpleName();
 			}
 			else {
 				tableName = explicitEntityName;
 			}
 		}
 		return new TableSourceImpl( schema, catalog, tableName );
 	}
 
 	private String determineCustomLoader() {
 		String customLoader = null;
 		// Custom sql loader
 		final AnnotationInstance sqlLoaderAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.LOADER
 		);
 		if ( sqlLoaderAnnotation != null ) {
 			customLoader = sqlLoaderAnnotation.value( "namedQuery" ).asString();
 		}
 		return customLoader;
 	}
 
 	private CustomSQL createCustomSQL(AnnotationInstance customSqlAnnotation) {
 		if ( customSqlAnnotation == null ) {
 			return null;
 		}
 
 		final String sql = customSqlAnnotation.value( "sql" ).asString();
 		final boolean isCallable = customSqlAnnotation.value( "callable" ) != null
 				&& customSqlAnnotation.value( "callable" ).asBoolean();
 
 		final ExecuteUpdateResultCheckStyle checkStyle = customSqlAnnotation.value( "check" ) == null
 				? isCallable
 				? ExecuteUpdateResultCheckStyle.NONE
 				: ExecuteUpdateResultCheckStyle.COUNT
 				: ExecuteUpdateResultCheckStyle.valueOf( customSqlAnnotation.value( "check" ).asEnum() );
 
 		return new CustomSQL( sql, isCallable, checkStyle );
 	}
 
 	private void processCustomSqlAnnotations() {
 		// Custom sql insert
 		final AnnotationInstance sqlInsertAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.SQL_INSERT
 		);
 		customInsert = createCustomSQL( sqlInsertAnnotation );
 
 		// Custom sql update
 		final AnnotationInstance sqlUpdateAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.SQL_UPDATE
 		);
 		customUpdate = createCustomSQL( sqlUpdateAnnotation );
 
 		// Custom sql delete
 		final AnnotationInstance sqlDeleteAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.SQL_DELETE
 		);
 		customDelete = createCustomSQL( sqlDeleteAnnotation );
 	}
 
 	private List<String> determineSynchronizedTableNames() {
 		final AnnotationInstance synchronizeAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.SYNCHRONIZE
 		);
 		if ( synchronizeAnnotation != null ) {
 			final String[] tableNames = synchronizeAnnotation.value().asStringArray();
 			return Arrays.asList( tableNames );
 		}
 		else {
 			return Collections.emptyList();
 		}
 	}
 
 	private String determineCustomTuplizer() {
 		// Custom tuplizer
 		String customTuplizer = null;
 		final AnnotationInstance pojoTuplizerAnnotation = locatePojoTuplizerAnnotation();
 		if ( pojoTuplizerAnnotation != null ) {
 			customTuplizer = pojoTuplizerAnnotation.value( "impl" ).asString();
 		}
 		return customTuplizer;
 	}
 
 	private AnnotationInstance locatePojoTuplizerAnnotation() {
 		final AnnotationInstance tuplizersAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.TUPLIZERS
 		);
 		if ( tuplizersAnnotation == null ) {
 			return null;
 		}
 
 		AnnotationInstance[] annotations = JandexHelper.getValue(
 				tuplizersAnnotation,
 				"value",
 				AnnotationInstance[].class
 		);
 		for ( AnnotationInstance tuplizerAnnotation : annotations ) {
 			if ( EntityMode.valueOf( tuplizerAnnotation.value( "entityModeType" ).asEnum() ) == EntityMode.POJO ) {
 				return tuplizerAnnotation;
 			}
 		}
 		return null;
 	}
 
 	private void processProxyGeneration() {
 		// Proxy generation
 		final AnnotationInstance hibernateProxyAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.PROXY
 		);
 		if ( hibernateProxyAnnotation != null ) {
 			isLazy = hibernateProxyAnnotation.value( "lazy" ) == null
 					|| hibernateProxyAnnotation.value( "lazy" ).asBoolean();
 			if ( isLazy ) {
 				final AnnotationValue proxyClassValue = hibernateProxyAnnotation.value( "proxyClass" );
 				if ( proxyClassValue == null ) {
 					proxy = getName();
 				}
 				else {
 					proxy = proxyClassValue.asString();
 				}
 			}
 			else {
 				proxy = null;
 			}
 		}
 		else {
 			isLazy = true;
 			proxy = getName();
 		}
 	}
 
 	private int determineBatchSize() {
 		final AnnotationInstance batchSizeAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.BATCH_SIZE
 		);
 		return batchSizeAnnotation == null ? -1 : batchSizeAnnotation.value( "size" ).asInt();
 	}
 
+	public String getDiscriminatorMatchValue() {
+		return discriminatorMatchValue;
+	}
+
 	class TableSourceImpl implements TableSource {
 		private final String schema;
 		private final String catalog;
 		private final String tableName;
 
 		TableSourceImpl(String schema, String catalog, String tableName) {
 			this.schema = schema;
 			this.catalog = catalog;
 			this.tableName = tableName;
 		}
 
 		@Override
 		public String getExplicitSchemaName() {
 			return schema;
 		}
 
 		@Override
 		public String getExplicitCatalogName() {
 			return catalog;
 		}
 
 		@Override
 		public String getExplicitTableName() {
 			return tableName;
 		}
 
 		@Override
 		public String getLogicalName() {
 			// todo : (steve) hardy, not sure what to use here... null is ok for the primary table name.  this is part of the secondary table support.
 			return null;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntitySourceImpl.java
index 9c331d8de3..342bfefda6 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntitySourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntitySourceImpl.java
@@ -1,265 +1,264 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.metamodel.source.annotations.entity;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.Origin;
 import org.hibernate.metamodel.source.SourceType;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.attribute.AssociationAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.DiscriminatorColumnValues;
 import org.hibernate.metamodel.source.annotations.attribute.SimpleAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.SingularAttributeSourceImpl;
 import org.hibernate.metamodel.source.annotations.attribute.ToOneAttributeSourceImpl;
 import org.hibernate.metamodel.source.binder.AttributeSource;
 import org.hibernate.metamodel.source.binder.EntitySource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.SubclassEntitySource;
 import org.hibernate.metamodel.source.binder.TableSource;
 import org.hibernate.service.ServiceRegistry;
 
 /**
  * @author Hardy Ferentschik
  */
 public class EntitySourceImpl implements EntitySource {
 	private final EntityClass entityClass;
 	private final Set<SubclassEntitySource> subclassEntitySources;
 	private final Origin origin;
 
 	public EntitySourceImpl(EntityClass entityClass) {
 		this.entityClass = entityClass;
 		this.subclassEntitySources = new HashSet<SubclassEntitySource>();
 		this.origin = new Origin( SourceType.ANNOTATION, entityClass.getName() );
 	}
 
 	public EntityClass getEntityClass() {
 		return entityClass;
 	}
 
 	@Override
 	public Origin getOrigin() {
 		return origin;
 	}
 
 	@Override
 	public LocalBindingContext getBindingContext() {
 		return new LocalBindingContextImpl( entityClass.getContext() );
 	}
 
 	@Override
 	public String getEntityName() {
 		return entityClass.getName();
 	}
 
 	@Override
 	public String getClassName() {
 		return entityClass.getName();
 	}
 
 	@Override
 	public String getJpaEntityName() {
 		return entityClass.getExplicitEntityName();
 	}
 
 	@Override
 	public TableSource getPrimaryTable() {
 		return entityClass.getTableSource();
 	}
 
 	@Override
 	public boolean isAbstract() {
 		// todo - check if this is correct for annotations
 		return false;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return entityClass.isLazy();
 	}
 
 	@Override
 	public String getProxy() {
 		return entityClass.getProxy();
 	}
 
 	@Override
 	public int getBatchSize() {
 		return entityClass.getBatchSize();
 	}
 
 	@Override
 	public boolean isDynamicInsert() {
 		return entityClass.isDynamicInsert();
 	}
 
 	@Override
 	public boolean isDynamicUpdate() {
 		return entityClass.isDynamicUpdate();
 	}
 
 	@Override
 	public boolean isSelectBeforeUpdate() {
 		return entityClass.isSelectBeforeUpdate();
 	}
 
 	@Override
 	public String getCustomTuplizerClassName() {
 		return entityClass.getCustomTuplizer();
 	}
 
 	@Override
 	public String getCustomPersisterClassName() {
 		return entityClass.getCustomPersister();
 	}
 
 	@Override
 	public String getCustomLoaderName() {
 		return entityClass.getCustomLoaderQueryName();
 	}
 
 	@Override
 	public CustomSQL getCustomSqlInsert() {
 		return entityClass.getCustomInsert();
 	}
 
 	@Override
 	public CustomSQL getCustomSqlUpdate() {
 		return entityClass.getCustomUpdate();
 	}
 
 	@Override
 	public CustomSQL getCustomSqlDelete() {
 		return entityClass.getCustomDelete();
 	}
 
 	@Override
 	public List<String> getSynchronizedTableNames() {
 		return entityClass.getSynchronizedTableNames();
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Collections.emptySet();
 	}
 
 	@Override
 	public Iterable<AttributeSource> attributeSources() {
 		List<AttributeSource> attributeList = new ArrayList<AttributeSource>();
 		for ( SimpleAttribute attribute : entityClass.getSimpleAttributes() ) {
 			attributeList.add( new SingularAttributeSourceImpl( attribute ) );
 		}
 		for ( AssociationAttribute associationAttribute : entityClass.getAssociationAttributes() ) {
 			attributeList.add( new ToOneAttributeSourceImpl( associationAttribute ) );
 		}
 		return attributeList;
 	}
 
 	@Override
 	public void add(SubclassEntitySource subclassEntitySource) {
 		subclassEntitySources.add( subclassEntitySource );
 	}
 
 	@Override
 	public Iterable<SubclassEntitySource> subclassEntitySources() {
 		return subclassEntitySources;
 	}
 
 	@Override
-	public String getDiscriminatorValue() {
-		return ( (DiscriminatorColumnValues) entityClass.getDiscriminatorAttribute()
-				.getColumnValues() ).getDiscriminatorValue();
+	public String getDiscriminatorMatchValue() {
+		return entityClass.getDiscriminatorMatchValue();
 	}
 
 	class LocalBindingContextImpl implements LocalBindingContext {
 		private final AnnotationBindingContext contextDelegate;
 
 		LocalBindingContextImpl(AnnotationBindingContext context) {
 			this.contextDelegate = context;
 		}
 
 		@Override
 		public Origin getOrigin() {
 			return origin;
 		}
 
 		@Override
 		public ServiceRegistry getServiceRegistry() {
 			return contextDelegate.getServiceRegistry();
 		}
 
 		@Override
 		public NamingStrategy getNamingStrategy() {
 			return contextDelegate.getNamingStrategy();
 		}
 
 		@Override
 		public MappingDefaults getMappingDefaults() {
 			return contextDelegate.getMappingDefaults();
 		}
 
 		@Override
 		public MetadataImplementor getMetadataImplementor() {
 			return contextDelegate.getMetadataImplementor();
 		}
 
 		@Override
 		public <T> Class<T> locateClassByName(String name) {
 			return contextDelegate.locateClassByName( name );
 		}
 
 		@Override
 		public Type makeJavaType(String className) {
 			return contextDelegate.makeJavaType( className );
 		}
 
 		@Override
 		public boolean isGloballyQuotedIdentifiers() {
 			return contextDelegate.isGloballyQuotedIdentifiers();
 		}
 
 		@Override
 		public Value<Class<?>> makeClassReference(String className) {
 			return contextDelegate.makeClassReference( className );
 		}
 
 		@Override
 		public String qualifyClassName(String name) {
 			return contextDelegate.qualifyClassName( name );
 		}
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/RootEntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/RootEntitySourceImpl.java
index 97fc4642f9..559223334a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/RootEntitySourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/RootEntitySourceImpl.java
@@ -1,123 +1,123 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.metamodel.source.annotations.entity;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.source.annotations.attribute.DiscriminatorSourceImpl;
 import org.hibernate.metamodel.source.annotations.attribute.SimpleAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.SimpleIdentifierSourceImpl;
 import org.hibernate.metamodel.source.annotations.attribute.SingularAttributeSourceImpl;
 import org.hibernate.metamodel.source.binder.DiscriminatorSource;
 import org.hibernate.metamodel.source.binder.IdentifierSource;
 import org.hibernate.metamodel.source.binder.RootEntitySource;
 import org.hibernate.metamodel.source.binder.SingularAttributeSource;
 
 /**
  * @author Hardy Ferentschik
  */
 public class RootEntitySourceImpl extends EntitySourceImpl implements RootEntitySource {
 	public RootEntitySourceImpl(EntityClass entityClass) {
 		super( entityClass );
 	}
 
 	@Override
 	public IdentifierSource getIdentifierSource() {
 		IdType idType = getEntityClass().getIdType();
 		switch ( idType ) {
 			case SIMPLE: {
 				SimpleAttribute attribute = getEntityClass().getIdAttributes().iterator().next();
 				return new SimpleIdentifierSourceImpl( attribute );
 			}
 			case COMPOSED: {
 				break;
 			}
 			case EMBEDDED: {
 				break;
 			}
 			default: {
 				throw new AssertionFailure( "The root entity needs to specify an identifier" );
 			}
 		}
 
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public SingularAttributeSource getVersioningAttributeSource() {
 		SingularAttributeSource attributeSource = null;
 		if ( getEntityClass().getVersionAttribute() != null ) {
 			attributeSource = new SingularAttributeSourceImpl( getEntityClass().getVersionAttribute() );
 		}
 		return attributeSource;
 	}
 
 	@Override
 	public DiscriminatorSource getDiscriminatorSource() {
-		DiscriminatorSource attributeSource = null;
-		if ( getEntityClass().getDiscriminatorAttribute() != null ) {
-			attributeSource = new DiscriminatorSourceImpl( getEntityClass().getDiscriminatorAttribute() );
+		DiscriminatorSource discriminatorSource = null;
+		if ( getEntityClass().getDiscriminatorColumnValues() != null ) {
+			discriminatorSource = new DiscriminatorSourceImpl( getEntityClass() );
 		}
-		return attributeSource;
+		return discriminatorSource;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return getEntityClass().isMutable();
 	}
 
 	@Override
 	public boolean isExplicitPolymorphism() {
 		return getEntityClass().isExplicitPolymorphism();
 	}
 
 	@Override
 	public String getWhere() {
 		return getEntityClass().getWhereClause();
 	}
 
 	@Override
 	public String getRowId() {
 		return getEntityClass().getRowId();
 	}
 
 	@Override
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		return getEntityClass().getOptimisticLockStyle();
 	}
 
 	@Override
 	public Caching getCaching() {
 		return getEntityClass().getCaching();
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
index 8842b516e4..f8d88478b4 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
@@ -1,686 +1,717 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.metamodel.source.binder;
 
 import java.beans.BeanInfo;
 import java.beans.PropertyDescriptor;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.beans.BeanInfoHelper;
 import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.CollectionElementNature;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.EntityDiscriminator;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.MetaAttribute;
 import org.hibernate.metamodel.binding.SimpleSingularAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleValueBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.PluralAttribute;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
+import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.Tuple;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.hbm.Helper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * The common binder shared between annotations and {@code hbm.xml} processing.
  * <p/>
  * The API consists of {@link #Binder} and {@link #processEntityHierarchy}
  *
  * @author Steve Ebersole
  */
 public class Binder {
 	private final MetadataImplementor metadata;
 	private final List<String> processedEntityNames;
 
 	private InheritanceType currentInheritanceType;
 	private EntityMode currentHierarchyEntityMode;
 	private LocalBindingContext currentBindingContext;
 
 	public Binder(MetadataImplementor metadata, List<String> processedEntityNames) {
 		this.metadata = metadata;
 		this.processedEntityNames = processedEntityNames;
 	}
 
 	/**
 	 * Process an entity hierarchy.
 	 *
 	 * @param entityHierarchy THe hierarchy to process.
 	 */
 	public void processEntityHierarchy(EntityHierarchy entityHierarchy) {
 		currentInheritanceType = entityHierarchy.getHierarchyInheritanceType();
 		EntityBinding rootEntityBinding = createEntityBinding( entityHierarchy.getRootEntitySource(), null );
 		if ( currentInheritanceType != InheritanceType.NO_INHERITANCE ) {
 			processHierarchySubEntities( entityHierarchy.getRootEntitySource(), rootEntityBinding );
 		}
 		currentHierarchyEntityMode = null;
 	}
 
 	private void processHierarchySubEntities(SubclassEntityContainer subclassEntitySource, EntityBinding superEntityBinding) {
 		for ( SubclassEntitySource subEntity : subclassEntitySource.subclassEntitySources() ) {
 			EntityBinding entityBinding = createEntityBinding( subEntity, superEntityBinding );
 			processHierarchySubEntities( subEntity, entityBinding );
 		}
 	}
 
 
 	// Entities ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private EntityBinding createEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		if ( processedEntityNames.contains( entitySource.getEntityName() ) ) {
 			return metadata.getEntityBinding( entitySource.getEntityName() );
 		}
 
 		currentBindingContext = entitySource.getBindingContext();
 		try {
 			final EntityBinding entityBinding = doCreateEntityBinding( entitySource, superEntityBinding );
 
 			metadata.addEntity( entityBinding );
 			processedEntityNames.add( entityBinding.getEntity().getName() );
 
 			processFetchProfiles( entitySource, entityBinding );
 
 			return entityBinding;
 		}
 		finally {
 			currentBindingContext = null;
 		}
 	}
 
 	private EntityBinding doCreateEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = createBasicEntityBinding( entitySource, superEntityBinding );
 
 		bindSecondaryTables( entitySource, entityBinding );
 		bindAttributes( entitySource, entityBinding );
 
 		bindTableUniqueConstraints( entitySource, entityBinding );
 
 		return entityBinding;
 	}
 
 	private EntityBinding createBasicEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		if ( superEntityBinding == null ) {
 			return makeRootEntityBinding( (RootEntitySource) entitySource );
 		}
 		else {
 			if ( currentInheritanceType == InheritanceType.SINGLE_TABLE ) {
 				return makeDiscriminatedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else if ( currentInheritanceType == InheritanceType.JOINED ) {
 				return makeJoinedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else if ( currentInheritanceType == InheritanceType.TABLE_PER_CLASS ) {
 				return makeUnionedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else {
 				// extreme internal error!
 				throw new AssertionFailure( "Internal condition failure" );
 			}
 		}
 	}
 
 	private EntityBinding makeRootEntityBinding(RootEntitySource entitySource) {
 		currentHierarchyEntityMode = entitySource.getEntityMode();
 
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, null );
 
 		bindPrimaryTable( entitySource, entityBinding );
 
 		bindIdentifier( entitySource, entityBinding );
 		bindVersion( entityBinding, entitySource );
 		bindDiscriminator( entitySource, entityBinding );
 
 		entityBinding.getHierarchyDetails().setCaching( entitySource.getCaching() );
 		entityBinding.getHierarchyDetails().setExplicitPolymorphism( entitySource.isExplicitPolymorphism() );
 		entityBinding.getHierarchyDetails().setOptimisticLockStyle( entitySource.getOptimisticLockStyle() );
 
 		entityBinding.setMutable( entitySource.isMutable() );
 		entityBinding.setWhereFilter( entitySource.getWhere() );
 		entityBinding.setRowId( entitySource.getRowId() );
 
 		return entityBinding;
 	}
 
 
 	private EntityBinding buildBasicEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = superEntityBinding == null
 				? new EntityBinding( currentInheritanceType, currentHierarchyEntityMode )
 				: new EntityBinding( superEntityBinding );
 
 		final String entityName = entitySource.getEntityName();
 		final String className = currentHierarchyEntityMode == EntityMode.POJO ? entitySource.getClassName() : null;
 
 		final Entity entity = new Entity(
 				entityName,
 				className,
 				currentBindingContext.makeClassReference( className ),
 				null
 		);
 		entityBinding.setEntity( entity );
 
 		entityBinding.setJpaEntityName( entitySource.getJpaEntityName() );
 
 		if ( currentHierarchyEntityMode == EntityMode.POJO ) {
 			final String proxy = entitySource.getProxy();
 			if ( proxy != null ) {
 				entityBinding.setProxyInterfaceType(
 						currentBindingContext.makeClassReference(
 								currentBindingContext.qualifyClassName( proxy )
 						)
 				);
 				entityBinding.setLazy( true );
 			}
 			else if ( entitySource.isLazy() ) {
 				entityBinding.setProxyInterfaceType( entityBinding.getEntity().getClassReferenceUnresolved() );
 				entityBinding.setLazy( true );
 			}
 		}
 		else {
 			entityBinding.setProxyInterfaceType( null );
 			entityBinding.setLazy( entitySource.isLazy() );
 		}
 
 		final String customTuplizerClassName = entitySource.getCustomTuplizerClassName();
 		if ( customTuplizerClassName != null ) {
 			entityBinding.setCustomEntityTuplizerClass(
 					currentBindingContext.<EntityTuplizer>locateClassByName(
 							customTuplizerClassName
 					)
 			);
 		}
 
 		final String customPersisterClassName = entitySource.getCustomPersisterClassName();
 		if ( customPersisterClassName != null ) {
 			entityBinding.setCustomEntityPersisterClass(
 					currentBindingContext.<EntityPersister>locateClassByName(
 							customPersisterClassName
 					)
 			);
 		}
 
 		entityBinding.setMetaAttributeContext( buildMetaAttributeContext( entitySource ) );
 
 		entityBinding.setDynamicUpdate( entitySource.isDynamicUpdate() );
 		entityBinding.setDynamicInsert( entitySource.isDynamicInsert() );
 		entityBinding.setBatchSize( entitySource.getBatchSize() );
 		entityBinding.setSelectBeforeUpdate( entitySource.isSelectBeforeUpdate() );
 		entityBinding.setAbstract( entitySource.isAbstract() );
 
 		entityBinding.setCustomLoaderName( entitySource.getCustomLoaderName() );
 		entityBinding.setCustomInsert( entitySource.getCustomSqlInsert() );
 		entityBinding.setCustomUpdate( entitySource.getCustomSqlUpdate() );
 		entityBinding.setCustomDelete( entitySource.getCustomSqlDelete() );
 
 		if ( entitySource.getSynchronizedTableNames() != null ) {
 			entityBinding.addSynchronizedTableNames( entitySource.getSynchronizedTableNames() );
 		}
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeDiscriminatedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
 
 		entityBinding.setBaseTable( superEntityBinding.getBaseTable() );
 
 		bindDiscriminatorValue( entitySource, entityBinding );
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeJoinedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
 
 		bindPrimaryTable( entitySource, entityBinding );
 
 		// todo : join
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeUnionedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
 
 		bindPrimaryTable( entitySource, entityBinding );
 
 		// todo : ??
 
 		return entityBinding;
 	}
 
 
 	// Attributes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private void bindIdentifier(RootEntitySource entitySource, EntityBinding entityBinding) {
 		if ( entitySource.getIdentifierSource() == null ) {
 			throw new AssertionFailure( "Expecting identifier information on root entity descriptor" );
 		}
 		switch ( entitySource.getIdentifierSource().getNature() ) {
 			case SIMPLE: {
 				bindSimpleIdentifier( (SimpleIdentifierSource) entitySource.getIdentifierSource(), entityBinding );
 			}
 			case AGGREGATED_COMPOSITE: {
 				// composite id with an actual component class
 			}
 			case COMPOSITE: {
 				// what we used to term an "embedded composite identifier", which is not tobe confused with the JPA
 				// term embedded. Specifically a composite id where there is no component class, though there may
 				// be a @IdClass :/
 			}
 		}
 	}
 
 	private void bindSimpleIdentifier(SimpleIdentifierSource identifierSource, EntityBinding entityBinding) {
 		final SimpleSingularAttributeBinding idAttributeBinding = doBasicSingularAttributeBindingCreation(
 				identifierSource.getIdentifierAttributeSource(), entityBinding
 		);
 
 		entityBinding.getHierarchyDetails().getEntityIdentifier().setValueBinding( idAttributeBinding );
 		entityBinding.getHierarchyDetails().getEntityIdentifier().setIdGenerator( identifierSource.getIdentifierGeneratorDescriptor() );
 
 		final org.hibernate.metamodel.relational.Value relationalValue = idAttributeBinding.getValue();
 
 		if ( SimpleValue.class.isInstance( relationalValue ) ) {
 			if ( !Column.class.isInstance( relationalValue ) ) {
 				// this should never ever happen..
 				throw new AssertionFailure( "Simple-id was not a column." );
 			}
 			entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( relationalValue ) );
 		}
 		else {
 			for ( SimpleValue subValue : ( (Tuple) relationalValue ).values() ) {
 				if ( Column.class.isInstance( subValue ) ) {
 					entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( subValue ) );
 				}
 			}
 		}
 	}
 
 	private void bindVersion(EntityBinding entityBinding, RootEntitySource entitySource) {
 		final SingularAttributeSource versioningAttributeSource = entitySource.getVersioningAttributeSource();
 		if ( versioningAttributeSource == null ) {
 			return;
 		}
 
 		SimpleSingularAttributeBinding attributeBinding = doBasicSingularAttributeBindingCreation(
 				versioningAttributeSource, entityBinding
 		);
 		entityBinding.getHierarchyDetails().setVersioningAttributeBinding( attributeBinding );
 	}
 
 	private void bindDiscriminator(RootEntitySource entitySource, EntityBinding entityBinding) {
 		final DiscriminatorSource discriminatorSource = entitySource.getDiscriminatorSource();
 		if ( discriminatorSource == null ) {
 			return;
 		}
 
-		SimpleSingularAttributeBinding attributeBinding = doBasicSingularAttributeBindingCreation(
-				discriminatorSource, entityBinding
-		);
 		EntityDiscriminator discriminator = new EntityDiscriminator();
-		discriminator.setValueBinding( attributeBinding );
+		SimpleValue relationalValue = makeSimpleValue(
+				entityBinding,
+				discriminatorSource.getDiscriminatorRelationalValueSource()
+		);
+		discriminator.setBoundValue( relationalValue );
+
+		discriminator.getExplicitHibernateTypeDescriptor().setExplicitTypeName(
+				discriminatorSource.getExplicitHibernateTypeName() != null
+						? discriminatorSource.getExplicitHibernateTypeName()
+						: "string"
+		);
+
 		discriminator.setInserted( discriminatorSource.isInserted() );
 		discriminator.setForced( discriminatorSource.isForced() );
+
 		entityBinding.getHierarchyDetails().setEntityDiscriminator( discriminator );
 	}
 
 	private void bindDiscriminatorValue(SubclassEntitySource entitySource, EntityBinding entityBinding) {
-		final String discriminatorValue = entitySource.getDiscriminatorValue();
+		final String discriminatorValue = entitySource.getDiscriminatorMatchValue();
 		if ( discriminatorValue == null ) {
 			return;
 		}
 		entityBinding.setDiscriminatorMatchValue( discriminatorValue );
 	}
 
 	private void bindAttributes(AttributeSourceContainer attributeSourceContainer, EntityBinding entityBinding) {
 		// todo : we really need the notion of a Stack here for the table from which the columns come for binding these attributes.
 		// todo : adding the concept (interface) of a source of attribute metadata would allow reuse of this method for entity, component, unique-key, etc
 		// for now, simply assume all columns come from the base table....
 
 		for ( AttributeSource attributeSource : attributeSourceContainer.attributeSources() ) {
 			if ( attributeSource.isSingular() ) {
 				final SingularAttributeSource singularAttributeSource = (SingularAttributeSource) attributeSource;
 				if ( singularAttributeSource.getNature() == SingularAttributeNature.COMPONENT ) {
 					throw new NotYetImplementedException( "Component binding not yet implemented :(" );
 				}
 				else {
 					doBasicSingularAttributeBindingCreation( singularAttributeSource, entityBinding );
 				}
 			}
 			else {
 				bindPersistentCollection( (PluralAttributeSource) attributeSource, entityBinding );
 			}
 		}
 	}
 
 	private void bindPersistentCollection(PluralAttributeSource attributeSource, EntityBinding entityBinding) {
 		final AbstractPluralAttributeBinding pluralAttributeBinding;
 		if ( attributeSource.getPluralAttributeNature() == PluralAttributeNature.BAG ) {
 			final PluralAttribute pluralAttribute = entityBinding.getEntity()
 					.locateOrCreateBag( attributeSource.getName() );
 			pluralAttributeBinding = entityBinding.makeBagAttributeBinding(
 					pluralAttribute,
 					convert( attributeSource.getPluralAttributeElementNature() )
 			);
 		}
 		else {
 			// todo : implement other collection types
 			throw new NotYetImplementedException( "Collections other than bag not yet implmented :(" );
 		}
 
 		doBasicAttributeBinding( attributeSource, pluralAttributeBinding );
 	}
 
 	private void doBasicAttributeBinding(AttributeSource attributeSource, AttributeBinding attributeBinding) {
 		attributeBinding.setPropertyAccessorName( attributeSource.getPropertyAccessorName() );
 		attributeBinding.setIncludedInOptimisticLocking( attributeSource.isIncludedInOptimisticLocking() );
 	}
 
 	private CollectionElementNature convert(PluralAttributeElementNature pluralAttributeElementNature) {
 		return CollectionElementNature.valueOf( pluralAttributeElementNature.name() );
 	}
 
 	private SimpleSingularAttributeBinding doBasicSingularAttributeBindingCreation(
 			SingularAttributeSource attributeSource,
 			EntityBinding entityBinding) {
 		final SingularAttribute attribute = attributeSource.isVirtualAttribute()
 				? entityBinding.getEntity().locateOrCreateVirtualAttribute( attributeSource.getName() )
 				: entityBinding.getEntity().locateOrCreateSingularAttribute( attributeSource.getName() );
 
 		final SimpleSingularAttributeBinding attributeBinding;
 		if ( attributeSource.getNature() == SingularAttributeNature.BASIC ) {
 			attributeBinding = entityBinding.makeSimpleAttributeBinding( attribute );
 			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
 		}
 		else if ( attributeSource.getNature() == SingularAttributeNature.MANY_TO_ONE ) {
 			attributeBinding = entityBinding.makeManyToOneAttributeBinding( attribute );
 			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
 			resolveToOneInformation(
 					(ToOneAttributeSource) attributeSource,
 					(ManyToOneAttributeBinding) attributeBinding
 			);
 		}
 		else {
 			throw new NotYetImplementedException();
 		}
 
 		attributeBinding.setGeneration( attributeSource.getGeneration() );
 		attributeBinding.setLazy( attributeSource.isLazy() );
 		attributeBinding.setIncludedInOptimisticLocking( attributeSource.isIncludedInOptimisticLocking() );
 
 		attributeBinding.setPropertyAccessorName(
 				Helper.getPropertyAccessorName(
 						attributeSource.getPropertyAccessorName(),
 						false,
 						currentBindingContext.getMappingDefaults().getPropertyAccessorName()
 				)
 		);
 
 		bindRelationalValues( attributeSource, attributeBinding );
 
 		attributeBinding.setMetaAttributeContext(
 				buildMetaAttributeContext( attributeSource.metaAttributes(), entityBinding.getMetaAttributeContext() )
 		);
 
 		return attributeBinding;
 	}
 
 	private void resolveTypeInformation(ExplicitHibernateTypeSource typeSource, SimpleSingularAttributeBinding attributeBinding) {
 		final Class<?> attributeJavaType = determineJavaType( attributeBinding.getAttribute() );
 		if ( attributeJavaType != null ) {
 			attributeBinding.getHibernateTypeDescriptor().setJavaTypeName( attributeJavaType.getName() );
 			attributeBinding.getAttribute()
 					.resolveType( currentBindingContext.makeJavaType( attributeJavaType.getName() ) );
 		}
 
 		final String explicitTypeName = typeSource.getName();
 		if ( explicitTypeName != null ) {
 			final TypeDef typeDef = currentBindingContext.getMetadataImplementor()
 					.getTypeDefinition( explicitTypeName );
 			if ( typeDef != null ) {
 				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( typeDef.getTypeClass() );
 				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().putAll( typeDef.getParameters() );
 			}
 			else {
 				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( explicitTypeName );
 			}
 			final Map<String, String> parameters = typeSource.getParameters();
 			if ( parameters != null ) {
 				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().putAll( parameters );
 			}
 		}
 		else {
 			if ( attributeJavaType == null ) {
 				// we will have problems later determining the Hibernate Type to use.  Should we throw an
 				// exception now?  Might be better to get better contextual info
 			}
 		}
 	}
 
 	private Class<?> determineJavaType(final Attribute attribute) {
 		try {
 			final Class ownerClass = attribute.getAttributeContainer().getClassReference();
 			AttributeJavaTypeDeterminerDelegate delegate = new AttributeJavaTypeDeterminerDelegate( attribute.getName() );
 			BeanInfoHelper.visitBeanInfo( ownerClass, delegate );
 			return delegate.javaType;
 		}
 		catch ( Exception ignore ) {
 			// todo : log it?
 		}
 		return null;
 	}
 
 	private static class AttributeJavaTypeDeterminerDelegate implements BeanInfoHelper.BeanInfoDelegate {
 		private final String attributeName;
 		private Class<?> javaType = null;
 
 		private AttributeJavaTypeDeterminerDelegate(String attributeName) {
 			this.attributeName = attributeName;
 		}
 
 		@Override
 		public void processBeanInfo(BeanInfo beanInfo) throws Exception {
 			for ( PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors() ) {
 				if ( propertyDescriptor.getName().equals( attributeName ) ) {
 					javaType = propertyDescriptor.getPropertyType();
 					break;
 				}
 			}
 		}
 	}
 
 	private void resolveToOneInformation(ToOneAttributeSource attributeSource, ManyToOneAttributeBinding attributeBinding) {
 		final String referencedEntityName = attributeSource.getReferencedEntityName() != null
 				? attributeSource.getReferencedEntityName()
 				: attributeBinding.getAttribute().getSingularAttributeType().getClassName();
 		attributeBinding.setReferencedEntityName( referencedEntityName );
 		// todo : we should consider basing references on columns instead of property-ref, which would require a resolution (later) of property-ref to column names
 		attributeBinding.setReferencedAttributeName( attributeSource.getReferencedEntityAttributeName() );
 
 		attributeBinding.setCascadeStyles( attributeSource.getCascadeStyles() );
 		attributeBinding.setFetchMode( attributeSource.getFetchMode() );
 	}
 
 	private MetaAttributeContext buildMetaAttributeContext(EntitySource entitySource) {
 		return buildMetaAttributeContext(
 				entitySource.metaAttributes(),
 				true,
 				currentBindingContext.getMetadataImplementor().getGlobalMetaAttributeContext()
 		);
 	}
 
 	private static MetaAttributeContext buildMetaAttributeContext(
 			Iterable<MetaAttributeSource> metaAttributeSources,
 			MetaAttributeContext parentContext) {
 		return buildMetaAttributeContext( metaAttributeSources, false, parentContext );
 	}
 
 	private static MetaAttributeContext buildMetaAttributeContext(
 			Iterable<MetaAttributeSource> metaAttributeSources,
 			boolean onlyInheritable,
 			MetaAttributeContext parentContext) {
 		final MetaAttributeContext subContext = new MetaAttributeContext( parentContext );
 
 		for ( MetaAttributeSource metaAttributeSource : metaAttributeSources ) {
 			if ( onlyInheritable & !metaAttributeSource.isInheritable() ) {
 				continue;
 			}
 
 			final String name = metaAttributeSource.getName();
 			final MetaAttribute inheritedMetaAttribute = parentContext.getMetaAttribute( name );
 			MetaAttribute metaAttribute = subContext.getLocalMetaAttribute( name );
 			if ( metaAttribute == null || metaAttribute == inheritedMetaAttribute ) {
 				metaAttribute = new MetaAttribute( name );
 				subContext.add( metaAttribute );
 			}
 			metaAttribute.addValue( metaAttributeSource.getValue() );
 		}
 
 		return subContext;
 	}
 
 
 	// Relational ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private void bindPrimaryTable(EntitySource entitySource, EntityBinding entityBinding) {
 		final TableSource tableSource = entitySource.getPrimaryTable();
 		final String schemaName = StringHelper.isEmpty( tableSource.getExplicitSchemaName() )
 				? currentBindingContext.getMappingDefaults().getSchemaName()
 				: currentBindingContext.getMetadataImplementor().getOptions().isGloballyQuotedIdentifiers()
 				? StringHelper.quote( tableSource.getExplicitSchemaName() )
 				: tableSource.getExplicitSchemaName();
 		final String catalogName = StringHelper.isEmpty( tableSource.getExplicitCatalogName() )
 				? currentBindingContext.getMappingDefaults().getCatalogName()
 				: currentBindingContext.getMetadataImplementor().getOptions().isGloballyQuotedIdentifiers()
 				? StringHelper.quote( tableSource.getExplicitCatalogName() )
 				: tableSource.getExplicitCatalogName();
 
 		String tableName = tableSource.getExplicitTableName();
 		if ( StringHelper.isEmpty( tableName ) ) {
 			tableName = currentBindingContext.getNamingStrategy()
 					.classToTableName( entityBinding.getEntity().getClassName() );
 		}
 		else {
 			tableName = currentBindingContext.getNamingStrategy().tableName( tableName );
 		}
 		if ( currentBindingContext.isGloballyQuotedIdentifiers() ) {
 			tableName = StringHelper.quote( tableName );
 		}
 
 		final org.hibernate.metamodel.relational.Table table = currentBindingContext.getMetadataImplementor()
 				.getDatabase()
 				.getSchema( new Schema.Name( schemaName, catalogName ) )
 				.locateOrCreateTable( Identifier.toIdentifier( tableName ) );
 
 		entityBinding.setBaseTable( table );
 	}
 
 	private void bindSecondaryTables(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
 	private void bindTableUniqueConstraints(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
 	private void bindRelationalValues(
 			RelationalValueSourceContainer relationalValueSourceContainer,
 			SingularAttributeBinding attributeBinding) {
 
 		List<SimpleValueBinding> valueBindings = new ArrayList<SimpleValueBinding>();
 
 		if ( relationalValueSourceContainer.relationalValueSources().size() > 0 ) {
 			for ( RelationalValueSource valueSource : relationalValueSourceContainer.relationalValueSources() ) {
 				final TableSpecification table = attributeBinding.getEntityBinding()
 						.getTable( valueSource.getContainingTableName() );
 
 				if ( ColumnSource.class.isInstance( valueSource ) ) {
 					final ColumnSource columnSource = ColumnSource.class.cast( valueSource );
-					final Column column = table.locateOrCreateColumn( columnSource.getName() );
-					column.setNullable( columnSource.isNullable() );
-					column.setDefaultValue( columnSource.getDefaultValue() );
-					column.setSqlType( columnSource.getSqlType() );
-					column.setSize( columnSource.getSize() );
-					column.setDatatype( columnSource.getDatatype() );
-					column.setReadFragment( columnSource.getReadFragment() );
-					column.setWriteFragment( columnSource.getWriteFragment() );
-					column.setUnique( columnSource.isUnique() );
-					column.setCheckCondition( columnSource.getCheckCondition() );
-					column.setComment( columnSource.getComment() );
+					final Column column = makeColumn( (ColumnSource) valueSource, table );
 					valueBindings.add(
 							new SimpleValueBinding(
 									column,
 									columnSource.isIncludedInInsert(),
 									columnSource.isIncludedInUpdate()
 							)
 					);
 				}
 				else {
 					valueBindings.add(
 							new SimpleValueBinding(
-									table.locateOrCreateDerivedValue( ( (DerivedValueSource) valueSource ).getExpression() )
+									makeDerivedValue( ((DerivedValueSource) valueSource), table )
 							)
 					);
 				}
 			}
 		}
 		else {
 			final String name = metadata.getOptions()
 					.getNamingStrategy()
 					.propertyToColumnName( attributeBinding.getAttribute().getName() );
-			final SimpleValueBinding valueBinding = new SimpleValueBinding();
 			valueBindings.add(
 					new SimpleValueBinding(
 							attributeBinding.getEntityBinding().getBaseTable().locateOrCreateColumn( name )
 					)
 			);
 		}
 		attributeBinding.setSimpleValueBindings( valueBindings );
 	}
 
+	private SimpleValue makeSimpleValue(
+			EntityBinding entityBinding,
+			RelationalValueSource valueSource) {
+		final TableSpecification table = entityBinding.getTable( valueSource.getContainingTableName() );
+
+		if ( ColumnSource.class.isInstance( valueSource ) ) {
+			return makeColumn( (ColumnSource) valueSource, table );
+		}
+		else {
+			return makeDerivedValue( (DerivedValueSource) valueSource, table );
+		}
+	}
+
+	private Column makeColumn(ColumnSource columnSource, TableSpecification table) {
+		final Column column = table.locateOrCreateColumn( columnSource.getName() );
+		column.setNullable( columnSource.isNullable() );
+		column.setDefaultValue( columnSource.getDefaultValue() );
+		column.setSqlType( columnSource.getSqlType() );
+		column.setSize( columnSource.getSize() );
+		column.setDatatype( columnSource.getDatatype() );
+		column.setReadFragment( columnSource.getReadFragment() );
+		column.setWriteFragment( columnSource.getWriteFragment() );
+		column.setUnique( columnSource.isUnique() );
+		column.setCheckCondition( columnSource.getCheckCondition() );
+		column.setComment( columnSource.getComment() );
+		return column;
+	}
+
+	private DerivedValue makeDerivedValue(DerivedValueSource derivedValueSource, TableSpecification table) {
+		return table.locateOrCreateDerivedValue( derivedValueSource.getExpression() );
+	}
+
 	private void processFetchProfiles(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : process the entity-local fetch-profile declaration
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/DiscriminatorSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/DiscriminatorSource.java
index 1d43661600..1689bf79a4 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/DiscriminatorSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/DiscriminatorSource.java
@@ -1,45 +1,55 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.metamodel.source.binder;
 
 /**
+ * Source for discriminator metadata.
+ * <p/>
+ * <b>NOTE</b> : extends the notion of {@link SingularAttributeSource} only for convenience in that both share many of
+ * the same source options.  However, a discriminator is <b>NEVER</b> a physical attribute on the domain model.
+ *
  * @author Hardy Ferentschik
+ * @author Steve Ebersole
  */
-public interface DiscriminatorSource extends SingularAttributeSource {
+public interface DiscriminatorSource {
+	public RelationalValueSource getDiscriminatorRelationalValueSource();
+
+	public String getExplicitHibernateTypeName();
+
 	/**
 	 * "Forces" Hibernate to specify the allowed discriminator values, even when retrieving all instances of the root class.
 	 *
 	 * @return {@code true} in case the discriminator value should be forces, {@code false} otherwise. Default is {@code false}.
 	 */
 	boolean isForced();
 
 	/**
 	 * Set this to {@code false}, if your discriminator column is also part of a mapped composite identifier.
 	 * It tells Hibernate not to include the column in SQL INSERTs.
 	 *
 	 * @return {@code true} in case the discriminator value should be included in inserts, {@code false} otherwise.
 	 *         Default is {@code true}.
 	 */
 	boolean isInserted();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java
index a37a87a754..c8cf698a55 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java
@@ -1,194 +1,194 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.metamodel.source.binder;
 
 import java.util.List;
 
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.Origin;
 
 /**
  * Contract describing source of an entity
  *
  * @author Steve Ebersole
  */
 public interface EntitySource extends SubclassEntityContainer, AttributeSourceContainer {
 	/**
 	 * Obtain the origin of this source.
 	 *
 	 * @return The origin of this source.
 	 */
 	public Origin getOrigin();
 
 	/**
 	 * Obtain the binding context local to this entity source.
 	 *
 	 * @return The local binding context
 	 */
 	public LocalBindingContext getBindingContext();
 
 	/**
 	 * Obtain the entity name
 	 *
 	 * @return The entity name
 	 */
 	public String getEntityName();
 
 	/**
 	 * Obtain the name of the entity {@link Class}
 	 *
 	 * @return THe entity class name
 	 */
 	public String getClassName();
 
 	/**
 	 * Obtain the JPA name of the entity
 	 *
 	 * @return THe JPA-specific entity name
 	 */
 	public String getJpaEntityName();
 
 	/**
 	 * Obtain the primary table for this entity.
 	 *
 	 * @return The primary table.
 	 */
 	public TableSource getPrimaryTable();
 
 	/**
 	 * Obtain the name of a custom tuplizer class to be used.
 	 *
 	 * @return The custom tuplizer class name
 	 */
 	public String getCustomTuplizerClassName();
 
 	/**
 	 * Obtain the name of a custom persister class to be used.
 	 *
 	 * @return The custom persister class name
 	 */
 	public String getCustomPersisterClassName();
 
 	/**
 	 * Is this entity lazy (proxyable)?
 	 *
 	 * @return {@code true} indicates the entity is lazy; {@code false} non-lazy.
 	 */
 	public boolean isLazy();
 
 	/**
 	 * For {@link #isLazy() lazy} entities, obtain the interface to use in constructing its proxies.
 	 *
 	 * @return The proxy interface name
 	 */
 	public String getProxy();
 
 	/**
 	 * Obtain the batch-size to be applied when initializing proxies of this entity.
 	 *
 	 * @return THe batch-size.
 	 */
 	public int getBatchSize();
 
 	/**
 	 * Is the entity abstract?
 	 * <p/>
 	 * The implication is whether the entity maps to a database table.
 	 *
 	 * @return {@code true} indicates the entity is abstract; {@code false} non-abstract.
 	 */
 	public boolean isAbstract();
 
 	/**
 	 * Did the source specify dynamic inserts?
 	 *
 	 * @return {@code true} indicates dynamic inserts will be used; {@code false} otherwise.
 	 */
 	public boolean isDynamicInsert();
 
 	/**
 	 * Did the source specify dynamic updates?
 	 *
 	 * @return {@code true} indicates dynamic updates will be used; {@code false} otherwise.
 	 */
 	public boolean isDynamicUpdate();
 
 	/**
 	 * Did the source specify to perform selects to decide whether to perform (detached) updates?
 	 *
 	 * @return {@code true} indicates selects will be done; {@code false} otherwise.
 	 */
 	public boolean isSelectBeforeUpdate();
 
 	/**
 	 * Obtain the name of a named-query that will be used for loading this entity
 	 *
 	 * @return THe custom loader query name
 	 */
 	public String getCustomLoaderName();
 
 	/**
 	 * Obtain the custom SQL to be used for inserts for this entity
 	 *
 	 * @return The custom insert SQL
 	 */
 	public CustomSQL getCustomSqlInsert();
 
 	/**
 	 * Obtain the custom SQL to be used for updates for this entity
 	 *
 	 * @return The custom update SQL
 	 */
 	public CustomSQL getCustomSqlUpdate();
 
 	/**
 	 * Obtain the custom SQL to be used for deletes for this entity
 	 *
 	 * @return The custom delete SQL
 	 */
 	public CustomSQL getCustomSqlDelete();
 
 	/**
 	 * Obtain any additional table names on which to synchronize (auto flushing) this entity.
 	 *
 	 * @return Additional synchronized table names.
 	 */
 	public List<String> getSynchronizedTableNames();
 
 	/**
 	 * Obtain the meta-attribute sources associated with this entity.
 	 *
 	 * @return The meta-attribute sources.
 	 */
 	public Iterable<MetaAttributeSource> metaAttributes();
 
 	/**
 	 * Get the actual discriminator value in case of a single table inheritance
 	 *
 	 * @return the actual discriminator value in case of a single table inheritance or {@code null} in case there is no
 	 * explicit value or a different inheritance scheme
 	 */
-	public String getDiscriminatorValue();
+	public String getDiscriminatorMatchValue();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java
index a5e2dcf6c5..e35bb07874 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java
@@ -1,250 +1,250 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.EntityMode;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.Origin;
 import org.hibernate.metamodel.source.binder.AttributeSource;
 import org.hibernate.metamodel.source.binder.EntitySource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.SubclassEntitySource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLAnyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToManyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToManyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToOneElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSynchronizeElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLTuplizerElement;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractEntitySourceImpl implements EntitySource {
 	private final MappingDocument sourceMappingDocument;
 	private final EntityElement entityElement;
 
 	private List<SubclassEntitySource> subclassEntitySources = new ArrayList<SubclassEntitySource>();
 
 	protected AbstractEntitySourceImpl(MappingDocument sourceMappingDocument, EntityElement entityElement) {
 		this.sourceMappingDocument = sourceMappingDocument;
 		this.entityElement = entityElement;
 	}
 
 	protected EntityElement entityElement() {
 		return entityElement;
 	}
 
 	protected MappingDocument sourceMappingDocument() {
 		return sourceMappingDocument;
 	}
 
 	@Override
 	public Origin getOrigin() {
 		return sourceMappingDocument.getOrigin();
 	}
 
 	@Override
 	public LocalBindingContext getBindingContext() {
 		return sourceMappingDocument.getMappingLocalBindingContext();
 	}
 
 	@Override
 	public String getEntityName() {
 		return StringHelper.isNotEmpty( entityElement.getEntityName() )
 				? entityElement.getEntityName()
 				: getClassName();
 	}
 
 	@Override
 	public String getClassName() {
 		return getBindingContext().qualifyClassName( entityElement.getName() );
 	}
 
 	@Override
 	public String getJpaEntityName() {
 		return null;
 	}
 
 	@Override
 	public boolean isAbstract() {
 		return Helper.getBooleanValue( entityElement.isAbstract(), false );
 	}
 
 	@Override
 	public boolean isLazy() {
 		return Helper.getBooleanValue( entityElement.isAbstract(), true );
 	}
 
 	@Override
 	public String getProxy() {
 		return entityElement.getProxy();
 	}
 
 	@Override
 	public int getBatchSize() {
 		return Helper.getIntValue( entityElement.getBatchSize(), -1 );
 	}
 
 	@Override
 	public boolean isDynamicInsert() {
 		return entityElement.isDynamicInsert();
 	}
 
 	@Override
 	public boolean isDynamicUpdate() {
 		return entityElement.isDynamicUpdate();
 	}
 
 	@Override
 	public boolean isSelectBeforeUpdate() {
 		return entityElement.isSelectBeforeUpdate();
 	}
 
 	protected EntityMode determineEntityMode() {
 		return StringHelper.isNotEmpty( getClassName() ) ? EntityMode.POJO : EntityMode.MAP;
 	}
 
 	@Override
 	public String getCustomTuplizerClassName() {
 		if ( entityElement.getTuplizer() == null ) {
 			return null;
 		}
 		final EntityMode entityMode = determineEntityMode();
 		for ( XMLTuplizerElement tuplizerElement : entityElement.getTuplizer() ) {
 			if ( entityMode == EntityMode.parse( tuplizerElement.getEntityMode() ) ) {
 				return tuplizerElement.getClazz();
 			}
 		}
 		return null;
 	}
 
 	@Override
 	public String getCustomPersisterClassName() {
 		return getBindingContext().qualifyClassName( entityElement.getPersister() );
 	}
 
 	@Override
 	public String getCustomLoaderName() {
 		return entityElement.getLoader() != null ? entityElement.getLoader().getQueryRef() : null;
 	}
 
 	@Override
 	public CustomSQL getCustomSqlInsert() {
 		return Helper.buildCustomSql( entityElement.getSqlInsert() );
 	}
 
 	@Override
 	public CustomSQL getCustomSqlUpdate() {
 		return Helper.buildCustomSql( entityElement.getSqlUpdate() );
 	}
 
 	@Override
 	public CustomSQL getCustomSqlDelete() {
 		return Helper.buildCustomSql( entityElement.getSqlDelete() );
 	}
 
 	@Override
 	public List<String> getSynchronizedTableNames() {
 		List<String> tableNames = new ArrayList<String>();
 		for ( XMLSynchronizeElement synchronizeElement : entityElement.getSynchronize() ) {
 			tableNames.add( synchronizeElement.getTable() );
 		}
 		return tableNames;
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Helper.buildMetaAttributeSources( entityElement.getMeta() );
 	}
 
 	@Override
 	public Iterable<AttributeSource> attributeSources() {
 		List<AttributeSource> attributeSources = new ArrayList<AttributeSource>();
 		for ( Object attributeElement : entityElement.getPropertyOrManyToOneOrOneToOne() ) {
 			if ( XMLPropertyElement.class.isInstance( attributeElement ) ) {
 				attributeSources.add(
 						new PropertyAttributeSourceImpl(
 								XMLPropertyElement.class.cast( attributeElement ),
 								sourceMappingDocument().getMappingLocalBindingContext()
 						)
 				);
 			}
 			else if ( XMLManyToOneElement.class.isInstance( attributeElement ) ) {
 				attributeSources.add(
 						new ManyToOneAttributeSourceImpl(
 								XMLManyToOneElement.class.cast( attributeElement ),
 								sourceMappingDocument().getMappingLocalBindingContext()
 						)
 				);
 			}
 			else if ( XMLOneToOneElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 			else if ( XMLAnyElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 			else if ( XMLOneToManyElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 			else if ( XMLManyToManyElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 		}
 		return attributeSources;
 	}
 
 	private EntityHierarchyImpl entityHierarchy;
 
 	public void injectHierarchy(EntityHierarchyImpl entityHierarchy) {
 		this.entityHierarchy = entityHierarchy;
 	}
 
 	@Override
 	public void add(SubclassEntitySource subclassEntitySource) {
 		add( (SubclassEntitySourceImpl) subclassEntitySource );
 	}
 
 	public void add(SubclassEntitySourceImpl subclassEntitySource) {
 		entityHierarchy.processSubclass( subclassEntitySource );
 		subclassEntitySources.add( subclassEntitySource );
 	}
 
 	@Override
 	public Iterable<SubclassEntitySource> subclassEntitySources() {
 		return subclassEntitySources;
 	}
 
 	@Override
-	public String getDiscriminatorValue() {
+	public String getDiscriminatorMatchValue() {
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java
index b3a139b2e5..b29a2ae9fd 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java
@@ -1,194 +1,244 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.metamodel.source.hbm;
 
 import org.hibernate.EntityMode;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.engine.OptimisticLockStyle;
+import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.binder.DiscriminatorSource;
 import org.hibernate.metamodel.source.binder.IdentifierSource;
+import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.RootEntitySource;
 import org.hibernate.metamodel.source.binder.SimpleIdentifierSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeSource;
 import org.hibernate.metamodel.source.binder.TableSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLCacheElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
 
 /**
  * @author Steve Ebersole
  */
 public class RootEntitySourceImpl extends AbstractEntitySourceImpl implements RootEntitySource {
 	protected RootEntitySourceImpl(MappingDocument sourceMappingDocument, XMLHibernateMapping.XMLClass entityElement) {
 		super( sourceMappingDocument, entityElement );
 	}
 
 	@Override
 	protected XMLHibernateMapping.XMLClass entityElement() {
 		return (XMLHibernateMapping.XMLClass) super.entityElement();
 	}
 
 	@Override
 	public IdentifierSource getIdentifierSource() {
 		if ( entityElement().getId() != null ) {
 			return new SimpleIdentifierSource() {
 				@Override
 				public SingularAttributeSource getIdentifierAttributeSource() {
 					return new SingularIdentifierAttributeSourceImpl(
 							entityElement().getId(),
 							sourceMappingDocument().getMappingLocalBindingContext()
 					);
 				}
 
 				@Override
 				public IdGenerator getIdentifierGeneratorDescriptor() {
 					if ( entityElement().getId().getGenerator() != null ) {
 						final String generatorName = entityElement().getId().getGenerator().getClazz();
 						IdGenerator idGenerator = sourceMappingDocument().getMappingLocalBindingContext()
 								.getMetadataImplementor()
 								.getIdGenerator( generatorName );
 						if ( idGenerator == null ) {
 							idGenerator = new IdGenerator(
 									getEntityName() + generatorName,
 									generatorName,
 									Helper.extractParameters( entityElement().getId().getGenerator().getParam() )
 							);
 						}
 						return idGenerator;
 					}
 					return null;
 				}
 
 				@Override
 				public Nature getNature() {
 					return Nature.SIMPLE;
 				}
 			};
 		}
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public SingularAttributeSource getVersioningAttributeSource() {
 		if ( entityElement().getVersion() != null ) {
 			return new VersionAttributeSourceImpl(
 					entityElement().getVersion(),
 					sourceMappingDocument().getMappingLocalBindingContext()
 			);
 		}
 		else if ( entityElement().getTimestamp() != null ) {
 			return new TimestampAttributeSourceImpl(
 					entityElement().getTimestamp(),
 					sourceMappingDocument().getMappingLocalBindingContext()
 			);
 		}
 		return null;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return determineEntityMode();
 	}
 
 	@Override
 	public boolean isMutable() {
 		return entityElement().isMutable();
 	}
 
 
 	@Override
 	public boolean isExplicitPolymorphism() {
 		return "explicit".equals( entityElement().getPolymorphism() );
 	}
 
 	@Override
 	public String getWhere() {
 		return entityElement().getWhere();
 	}
 
 	@Override
 	public String getRowId() {
 		return entityElement().getRowid();
 	}
 
 	@Override
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		final String optimisticLockModeString = Helper.getStringValue( entityElement().getOptimisticLock(), "version" );
 		try {
 			return OptimisticLockStyle.valueOf( optimisticLockModeString.toUpperCase() );
 		}
 		catch ( Exception e ) {
 			throw new MappingException(
 					"Unknown optimistic-lock value : " + optimisticLockModeString,
 					sourceMappingDocument().getOrigin()
 			);
 		}
 	}
 
 	@Override
 	public Caching getCaching() {
 		final XMLCacheElement cache = entityElement().getCache();
 		if ( cache == null ) {
 			return null;
 		}
 		final String region = cache.getRegion() != null ? cache.getRegion() : getEntityName();
 		final AccessType accessType = Enum.valueOf( AccessType.class, cache.getUsage() );
 		final boolean cacheLazyProps = !"non-lazy".equals( cache.getInclude() );
 		return new Caching( region, accessType, cacheLazyProps );
 	}
 
 	@Override
 	public TableSource getPrimaryTable() {
 		return new TableSource() {
 			@Override
 			public String getExplicitSchemaName() {
 				return entityElement().getSchema();
 			}
 
 			@Override
 			public String getExplicitCatalogName() {
 				return entityElement().getCatalog();
 			}
 
 			@Override
 			public String getExplicitTableName() {
 				return entityElement().getTable();
 			}
 
 			@Override
 			public String getLogicalName() {
 				// logical name for the primary table is null
 				return null;
 			}
 		};
 	}
 
 	@Override
 	public DiscriminatorSource getDiscriminatorSource() {
-		// todo : implement
-		return null;
+		final XMLHibernateMapping.XMLClass.XMLDiscriminator discriminatorElement = entityElement().getDiscriminator();
+		if ( discriminatorElement == null ) {
+			return null;
+		}
+
+		return new DiscriminatorSource() {
+			@Override
+			public RelationalValueSource getDiscriminatorRelationalValueSource() {
+				if ( StringHelper.isNotEmpty( discriminatorElement.getColumnAttribute() ) ) {
+					return new ColumnAttributeSourceImpl(
+							null, // root table
+							discriminatorElement.getColumnAttribute(),
+							discriminatorElement.isInsert(),
+							discriminatorElement.isInsert()
+					);
+				}
+				else if ( StringHelper.isNotEmpty( discriminatorElement.getFormulaAttribute() ) ) {
+					return new FormulaImpl( null, discriminatorElement.getFormulaAttribute() );
+				}
+				else if ( discriminatorElement.getColumn() != null ) {
+					return new ColumnSourceImpl(
+							null, // root table
+							discriminatorElement.getColumn(),
+							discriminatorElement.isInsert(),
+							discriminatorElement.isInsert()
+					);
+				}
+				else if ( StringHelper.isNotEmpty( discriminatorElement.getFormula() ) ) {
+					return new FormulaImpl( null, discriminatorElement.getFormula() );
+				}
+				else {
+					throw new MappingException( "could not determine source of discriminator mapping", getOrigin() );
+				}
+			}
+
+			@Override
+			public String getExplicitHibernateTypeName() {
+				return discriminatorElement.getType();
+			}
+
+			@Override
+			public boolean isForced() {
+				return discriminatorElement.isForce();
+			}
+
+			@Override
+			public boolean isInserted() {
+				return discriminatorElement.isInsert();
+			}
+		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
index 6c7e33d7cc..7935f32e5b 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
@@ -1,1035 +1,1025 @@
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
 
 		final Object discriminatorValue;
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
 
 		// TODO: fix when EntityBinhding.getRootEntityBinding() exists (HHH-6337)
 		//final Table table = entityBinding.getRootEntityBinding().getBaseTable();
 		final TableSpecification table = entityBinding.getBaseTable();
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
 
 		// TODO: fix this when can get subclass info from EntityBinding (HHH-6337)
 		//  for now set hasSubclasses to false
 		//hasSubclasses = entityBinding.hasSubclasses();
 		boolean hasSubclasses = false;
 
 		//polymorphic = ! entityBinding.isRoot() || entityBinding.hasSubclasses();
 		boolean isPolymorphic = ! entityBinding.isRoot() || hasSubclasses;
 		final Object discriminatorValue;
 		if ( isPolymorphic ) {
-			org.hibernate.metamodel.relational.Value discrimValue =
-					entityBinding.getHierarchyDetails().getEntityDiscriminator().getValueBinding().getValue();
-			if (discrimValue==null) {
+			SimpleValue discriminatorRelationalValue = entityBinding.getHierarchyDetails().getEntityDiscriminator().getBoundValue();
+			if ( discriminatorRelationalValue == null ) {
 				throw new MappingException("discriminator mapping required for single table polymorphic persistence");
 			}
 			forceDiscriminator = entityBinding.getHierarchyDetails().getEntityDiscriminator().isForced();
-			if ( ! SimpleValue.class.isInstance(  discrimValue ) ) {
-				throw new MappingException( "discriminator must be mapped to a single column or formula." );
-			}
-			if ( DerivedValue.class.isInstance( discrimValue ) ) {
-				DerivedValue formula = ( DerivedValue ) discrimValue;
+			if ( DerivedValue.class.isInstance( discriminatorRelationalValue ) ) {
+				DerivedValue formula = ( DerivedValue ) discriminatorRelationalValue;
 				discriminatorFormula = formula.getExpression();
 				discriminatorFormulaTemplate = getTemplateFromString( formula.getExpression(), factory );
 				discriminatorColumnName = null;
 				discriminatorColumnReaders = null;
 				discriminatorColumnReaderTemplate = null;
 				discriminatorAlias = "clazz_";
 			}
-			else if ( org.hibernate.metamodel.relational.Column.class.isInstance( discrimValue ) ) {
-				org.hibernate.metamodel.relational.Column column = ( org.hibernate.metamodel.relational.Column ) discrimValue;
+			else {
+				org.hibernate.metamodel.relational.Column column = ( org.hibernate.metamodel.relational.Column ) discriminatorRelationalValue;
 				discriminatorColumnName = column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 				discriminatorColumnReaders =
 						column.getReadFragment() == null ?
 								column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() ) :
 								column.getReadFragment();
 				discriminatorColumnReaderTemplate = getTemplateFromColumn( column, factory );
 				// TODO: fix this when EntityBinding.getRootEntityBinding() is implemented;
 				// for now, assume entityBinding is the root
 				//discriminatorAlias = column.getAlias( factory.getDialect(), entityBinding.getRootEntityBinding().getBaseTable );
 				discriminatorAlias = column.getAlias( factory.getDialect() );
 				discriminatorFormula = null;
 				discriminatorFormulaTemplate = null;
 			}
-			else {
-				throw new MappingException( "Unknown discriminator value type:" + discrimValue.toLoggableString() );
-			}
-			discriminatorType =
-					entityBinding
-							.getHierarchyDetails()
-							.getEntityDiscriminator()
-							.getValueBinding()
-							.getHibernateTypeDescriptor()
-							.getResolvedTypeMapping();
+
+			discriminatorType = entityBinding.getHierarchyDetails()
+					.getEntityDiscriminator()
+					.getExplicitHibernateTypeDescriptor()
+					.getResolvedTypeMapping();
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
-				discriminatorInsertable =
-						entityBinding.getHierarchyDetails().getEntityDiscriminator().isInserted() &&
-								! DerivedValue.class.isInstance( discrimValue );
+				discriminatorInsertable = entityBinding.getHierarchyDetails().getEntityDiscriminator().isInserted()
+						&& ! DerivedValue.class.isInstance( discriminatorRelationalValue );
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
 
 		// TODO: fix when subclasses are working (HHH-6337)
 		//for ( AttributeBinding prop : entityBinding.getSubclassAttributeBindingClosure() ) {
 		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
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
 					singularAttributeBinding.getEntityBinding().getEntity().getName() + '.' + singularAttributeBinding.getAttribute().getName(),
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
 
 		// TODO; fix when subclasses are working (HHH-6337)
 		//int subclassSpan = entityBinding.getSubclassSpan() + 1;
 		int subclassSpan = 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 		if ( isPolymorphic ) {
 			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
 		}
 
 		// SUBCLASSES
 
 		// TODO; fix when subclasses are working (HHH-6337)
 
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
