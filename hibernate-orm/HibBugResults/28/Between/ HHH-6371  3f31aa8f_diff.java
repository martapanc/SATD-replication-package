diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityDiscriminator.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityDiscriminator.java
index a1a23a543b..e39dd223b2 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityDiscriminator.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityDiscriminator.java
@@ -1,98 +1,102 @@
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
 
 import org.hibernate.metamodel.binding.state.DiscriminatorBindingState;
 import org.hibernate.metamodel.relational.state.ValueRelationalState;
 
 /**
  * Binding of the discriminator in a entity hierarchy
  *
  * @author Steve Ebersole
  */
 public class EntityDiscriminator {
 	private SimpleAttributeBinding valueBinding;
 	private String discriminatorValue;
 	private boolean forced;
 	private boolean inserted = true;
 
 	public EntityDiscriminator() {
 	}
 
 	public SimpleAttributeBinding getValueBinding() {
 		return valueBinding;
 	}
 
 	/* package-protected */
 	void setValueBinding(SimpleAttributeBinding valueBinding) {
 		this.valueBinding = valueBinding;
 	}
 
 	public EntityDiscriminator initialize(DiscriminatorBindingState state) {
 		if ( valueBinding == null ) {
 			throw new IllegalStateException( "Cannot bind state because the value binding has not been initialized." );
 		}
 		this.valueBinding.initialize( state );
 		this.discriminatorValue = state.getDiscriminatorValue();
 		this.forced = state.isForced();
 		this.inserted = state.isInserted();
 		return this;
 	}
 
 	public EntityDiscriminator initialize(ValueRelationalState state) {
 		valueBinding.initialize( state );
 		return this;
 	}
 
 	public String getDiscriminatorValue() {
 		return discriminatorValue;
 	}
 
+	public void setDiscriminatorValue(String discriminatorValue) {
+		this.discriminatorValue = discriminatorValue;
+	}
+
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
 		sb.append( "{valueBinding=" ).append( valueBinding );
 		sb.append( ", forced=" ).append( forced );
 		sb.append( ", inserted=" ).append( inserted );
 		sb.append( '}' );
 		return sb.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Component.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Component.java
index f885cb9d68..b8b90a802c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Component.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Component.java
@@ -1,49 +1,49 @@
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
 package org.hibernate.metamodel.domain;
 
 import org.hibernate.internal.util.Value;
 
 /**
  * Models the notion of a component (what JPA calls an Embeddable).
  * <p/>
  * NOTE : Components are not currently really hierarchical.  But that is a feature I want to add.
  *
  * @author Steve Ebersole
  */
-public class Component extends AbstractAttributeContainer implements Hierarchical {
+public class Component extends AbstractAttributeContainer {
 	public Component(String name, String className, Value<Class<?>> classReference, Hierarchical superType) {
 		super( name, className, classReference, superType );
 	}
 
 	@Override
 	public boolean isAssociation() {
 		return false;
 	}
 
 	@Override
 	public boolean isComponent() {
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/NonEntity.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/NonEntity.java
index c647eafb22..5a320e830a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/NonEntity.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/NonEntity.java
@@ -1,55 +1,55 @@
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
 package org.hibernate.metamodel.domain;
 
 import org.hibernate.internal.util.Value;
 
 /**
  * Models the concept class in the hierarchy with no persistent attributes.
  *
  * @author Hardy Ferentschik
  */
-public class NonEntity extends AbstractAttributeContainer implements Hierarchical {
+public class NonEntity extends AbstractAttributeContainer {
 	/**
 	 * Constructor for the non-entity
 	 *
 	 * @param entityName The name of the non-entity
 	 * @param className The name of this non-entity's java class
 	 * @param classReference The reference to this non-entity's {@link Class}
 	 * @param superType The super type for this non-entity. If there is not super type {@code null} needs to be passed.
 	 */
 	public NonEntity(String entityName, String className, Value<Class<?>> classReference, Hierarchical superType) {
 		super( entityName, className, classReference, superType );
 	}
 
 	@Override
 	public boolean isAssociation() {
 		return true;
 	}
 
 	@Override
 	public boolean isComponent() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Superclass.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Superclass.java
index c43c6b9fc2..54b504f440 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Superclass.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Superclass.java
@@ -1,55 +1,55 @@
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
 package org.hibernate.metamodel.domain;
 
 import org.hibernate.internal.util.Value;
 
 /**
  * Models the concept of a (intermediate) superclass
  *
  * @author Steve Ebersole
  */
-public class Superclass extends AbstractAttributeContainer implements Hierarchical {
+public class Superclass extends AbstractAttributeContainer {
 	/**
 	 * Constructor for the entity
 	 *
 	 * @param entityName The name of the entity
 	 * @param className The name of this entity's java class
 	 * @param classReference The reference to this entity's {@link Class}
 	 * @param superType The super type for this entity. If there is not super type {@code null} needs to be passed.
 	 */
 	public Superclass(String entityName, String className, Value<Class<?>> classReference, Hierarchical superType) {
 		super( entityName, className, classReference, superType );
 	}
 
 	@Override
 	public boolean isAssociation() {
 		return true;
 	}
 
 	@Override
 	public boolean isComponent() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationsBindingContext.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBindingContext.java
similarity index 77%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationsBindingContext.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBindingContext.java
index cb670afa76..600e10a4d4 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationsBindingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBindingContext.java
@@ -1,44 +1,49 @@
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
 package org.hibernate.metamodel.source.annotations;
 
 import com.fasterxml.classmate.ResolvedType;
 import com.fasterxml.classmate.ResolvedTypeWithMembers;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.Index;
 
 import org.hibernate.metamodel.source.BindingContext;
 
 /**
+ * Defines an interface for providing additional annotation related context information.
+ *
  * @author Steve Ebersole
+ * @author Hardy Ferentschik
  */
-public interface AnnotationsBindingContext extends BindingContext {
-	public Index getIndex();
-	public ClassInfo getClassInfo(String name);
+public interface AnnotationBindingContext extends BindingContext {
+	Index getIndex();
+
+	ClassInfo getClassInfo(String name);
+
+	void resolveAllTypes(String className);
 
-	public void resolveAllTypes(String className);
-	public ResolvedType getResolvedType(Class<?> clazz);
+	ResolvedType getResolvedType(Class<?> clazz);
 
-	public ResolvedTypeWithMembers resolveMemberTypes(ResolvedType type);
+	ResolvedTypeWithMembers resolveMemberTypes(ResolvedType type);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBindingContextImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBindingContextImpl.java
new file mode 100644
index 0000000000..f575cf4232
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBindingContextImpl.java
@@ -0,0 +1,156 @@
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
+package org.hibernate.metamodel.source.annotations;
+
+import java.util.HashMap;
+import java.util.Map;
+
+import com.fasterxml.classmate.MemberResolver;
+import com.fasterxml.classmate.ResolvedType;
+import com.fasterxml.classmate.ResolvedTypeWithMembers;
+import com.fasterxml.classmate.TypeResolver;
+import org.jboss.jandex.ClassInfo;
+import org.jboss.jandex.DotName;
+import org.jboss.jandex.Index;
+
+import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.internal.util.Value;
+import org.hibernate.metamodel.domain.Type;
+import org.hibernate.metamodel.source.MappingDefaults;
+import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.service.ServiceRegistry;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+
+/**
+ * @author Steve Ebersole
+ */
+public class AnnotationBindingContextImpl implements AnnotationBindingContext {
+	private final MetadataImplementor metadata;
+	private final Value<ClassLoaderService> classLoaderService;
+	private final Index index;
+	private final TypeResolver typeResolver = new TypeResolver();
+	private final Map<Class<?>, ResolvedType> resolvedTypeCache = new HashMap<Class<?>, ResolvedType>();
+
+	public AnnotationBindingContextImpl(MetadataImplementor metadata, Index index) {
+		this.metadata = metadata;
+		this.classLoaderService = new Value<ClassLoaderService>(
+				new Value.DeferredInitializer<ClassLoaderService>() {
+					@Override
+					public ClassLoaderService initialize() {
+						return AnnotationBindingContextImpl.this.metadata
+								.getServiceRegistry()
+								.getService( ClassLoaderService.class );
+					}
+				}
+		);
+		this.index = index;
+	}
+
+	@Override
+	public Index getIndex() {
+		return index;
+	}
+
+	@Override
+	public ClassInfo getClassInfo(String name) {
+		DotName dotName = DotName.createSimple( name );
+		return index.getClassByName( dotName );
+	}
+
+	@Override
+	public void resolveAllTypes(String className) {
+		// the resolved type for the top level class in the hierarchy
+		Class<?> clazz = classLoaderService.getValue().classForName( className );
+		ResolvedType resolvedType = typeResolver.resolve( clazz );
+		while ( resolvedType != null ) {
+			// todo - check whether there is already something in the map
+			resolvedTypeCache.put( clazz, resolvedType );
+			resolvedType = resolvedType.getParentClass();
+			if ( resolvedType != null ) {
+				clazz = resolvedType.getErasedType();
+			}
+		}
+	}
+
+	@Override
+	public ResolvedType getResolvedType(Class<?> clazz) {
+		// todo - error handling
+		return resolvedTypeCache.get( clazz );
+	}
+
+	@Override
+	public ResolvedTypeWithMembers resolveMemberTypes(ResolvedType type) {
+		// todo : is there a reason we create this resolver every time?
+		MemberResolver memberResolver = new MemberResolver( typeResolver );
+		return memberResolver.resolve( type, null, null );
+	}
+
+	@Override
+	public ServiceRegistry getServiceRegistry() {
+		return getMetadataImplementor().getServiceRegistry();
+	}
+
+	@Override
+	public NamingStrategy getNamingStrategy() {
+		return metadata.getNamingStrategy();
+	}
+
+	@Override
+	public MappingDefaults getMappingDefaults() {
+		return metadata.getMappingDefaults();
+	}
+
+	@Override
+	public MetadataImplementor getMetadataImplementor() {
+		return metadata;
+	}
+
+	@Override
+	public <T> Class<T> locateClassByName(String name) {
+		return classLoaderService.getValue().classForName( name );
+	}
+
+	private Map<String, Type> nameToJavaTypeMap = new HashMap<String, Type>();
+
+	@Override
+	public Type makeJavaType(String className) {
+		Type javaType = nameToJavaTypeMap.get( className );
+		if ( javaType == null ) {
+			javaType = metadata.makeJavaType( className );
+			nameToJavaTypeMap.put( className, javaType );
+		}
+		return javaType;
+	}
+
+	@Override
+	public Value<Class<?>> makeClassReference(String className) {
+		return new Value<Class<?>>( locateClassByName( className ) );
+	}
+
+	@Override
+	public boolean isGloballyQuotedIdentifiers() {
+		return metadata.isGloballyQuotedIdentifiers();
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationsSourceProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationProcessor.java
similarity index 60%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationsSourceProcessor.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationProcessor.java
index 1145302da6..3e056ec497 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationsSourceProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationProcessor.java
@@ -1,307 +1,204 @@
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
 package org.hibernate.metamodel.source.annotations;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
-import java.util.HashMap;
 import java.util.List;
-import java.util.Map;
 import java.util.Set;
 
-import com.fasterxml.classmate.MemberResolver;
-import com.fasterxml.classmate.ResolvedType;
-import com.fasterxml.classmate.ResolvedTypeWithMembers;
-import com.fasterxml.classmate.TypeResolver;
-import org.jboss.jandex.ClassInfo;
-import org.jboss.jandex.DotName;
 import org.jboss.jandex.Index;
 import org.jboss.jandex.Indexer;
 import org.jboss.logging.Logger;
 
+import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
-import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.MetadataSources;
-import org.hibernate.metamodel.source.MappingDefaults;
+import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.domain.Hierarchical;
+import org.hibernate.metamodel.domain.NonEntity;
+import org.hibernate.metamodel.domain.Superclass;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.SourceProcessor;
+import org.hibernate.metamodel.source.annotation.jaxb.XMLEntityMappings;
 import org.hibernate.metamodel.source.annotations.entity.ConfiguredClassHierarchy;
 import org.hibernate.metamodel.source.annotations.entity.ConfiguredClassType;
 import org.hibernate.metamodel.source.annotations.entity.EntityBinder;
+import org.hibernate.metamodel.source.annotations.entity.EntityClass;
 import org.hibernate.metamodel.source.annotations.global.FetchProfileBinder;
 import org.hibernate.metamodel.source.annotations.global.FilterDefBinder;
 import org.hibernate.metamodel.source.annotations.global.IdGeneratorBinder;
 import org.hibernate.metamodel.source.annotations.global.QueryBinder;
 import org.hibernate.metamodel.source.annotations.global.TableBinder;
 import org.hibernate.metamodel.source.annotations.global.TypeDefBinder;
 import org.hibernate.metamodel.source.annotations.xml.PseudoJpaDotNames;
 import org.hibernate.metamodel.source.annotations.xml.mocker.EntityMappingsMocker;
 import org.hibernate.metamodel.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
-import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.domain.Hierarchical;
-import org.hibernate.metamodel.domain.Type;
-import org.hibernate.metamodel.domain.NonEntity;
-import org.hibernate.metamodel.domain.Superclass;
-import org.hibernate.metamodel.source.annotation.jaxb.XMLEntityMappings;
-import org.hibernate.metamodel.source.annotations.entity.EntityClass;
-import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * Main class responsible to creating and binding the Hibernate meta-model from annotations.
  * This binder only has to deal with the (jandex) annotation index/repository. XML configuration is already processed
  * and pseudo annotations are created.
  *
  * @author Hardy Ferentschik
  * @author Steve Ebersole
  */
-public class AnnotationsSourceProcessor implements SourceProcessor, AnnotationsBindingContext {
-	private static final Logger LOG = Logger.getLogger( AnnotationsSourceProcessor.class );
+public class AnnotationProcessor implements SourceProcessor {
+	private static final Logger LOG = Logger.getLogger( AnnotationProcessor.class );
 
 	private final MetadataImplementor metadata;
-	private final Value<ClassLoaderService> classLoaderService;
-
-	private Index index;
-
-	private final TypeResolver typeResolver = new TypeResolver();
-	private final Map<Class<?>, ResolvedType> resolvedTypeCache = new HashMap<Class<?>, ResolvedType>();
+	private AnnotationBindingContext bindingContext;
 
-	public AnnotationsSourceProcessor(MetadataImpl metadata) {
+	public AnnotationProcessor(MetadataImpl metadata) {
 		this.metadata = metadata;
-		this.classLoaderService = new Value<ClassLoaderService>(
-				new Value.DeferredInitializer<ClassLoaderService>() {
-					@Override
-					public ClassLoaderService initialize() {
-						return AnnotationsSourceProcessor.this.metadata.getServiceRegistry().getService( ClassLoaderService.class );
-					}
-				}
-		);
 	}
 
 	@Override
 	@SuppressWarnings( { "unchecked" })
 	public void prepare(MetadataSources sources) {
 		// create a jandex index from the annotated classes
 		Indexer indexer = new Indexer();
 		for ( Class<?> clazz : sources.getAnnotatedClasses() ) {
 			indexClass( indexer, clazz.getName().replace( '.', '/' ) + ".class" );
 		}
 
 		// add package-info from the configured packages
 		for ( String packageName : sources.getAnnotatedPackages() ) {
 			indexClass( indexer, packageName.replace( '.', '/' ) + "/package-info.class" );
 		}
 
-		index = indexer.complete();
+		Index index = indexer.complete();
 
 		List<JaxbRoot<XMLEntityMappings>> mappings = new ArrayList<JaxbRoot<XMLEntityMappings>>();
 		for ( JaxbRoot<?> root : sources.getJaxbRootList() ) {
 			if ( root.getRoot() instanceof XMLEntityMappings ) {
 				mappings.add( (JaxbRoot<XMLEntityMappings>) root );
 			}
 		}
 		if ( !mappings.isEmpty() ) {
 			index = parseAndUpdateIndex( mappings, index );
 		}
 
-        if ( index.getAnnotations( PseudoJpaDotNames.DEFAULT_DELIMITED_IDENTIFIERS ) != null ) {
+		if ( index.getAnnotations( PseudoJpaDotNames.DEFAULT_DELIMITED_IDENTIFIERS ) != null ) {
 			// todo : this needs to move to AnnotationBindingContext
 			// what happens right now is that specifying this in an orm.xml causes it to effect all orm.xmls
-            metadata.setGloballyQuotedIdentifiers( true );
-        }
-	}
-
-	private Index parseAndUpdateIndex(List<JaxbRoot<XMLEntityMappings>> mappings, Index annotationIndex) {
-		List<XMLEntityMappings> list = new ArrayList<XMLEntityMappings>( mappings.size() );
-		for ( JaxbRoot<XMLEntityMappings> jaxbRoot : mappings ) {
-			list.add( jaxbRoot.getRoot() );
-		}
-		return new EntityMappingsMocker( list, annotationIndex, metadata.getServiceRegistry() ).mockNewIndex();
-	}
-
-	private void indexClass(Indexer indexer, String className) {
-		InputStream stream = classLoaderService.getValue().locateResourceStream( className );
-		try {
-			indexer.index( stream );
-		}
-		catch ( IOException e ) {
-			throw new HibernateException( "Unable to open input stream for class " + className, e );
+			metadata.setGloballyQuotedIdentifiers( true );
 		}
+		bindingContext = new AnnotationBindingContextImpl( metadata, index );
 	}
 
 	@Override
 	public void processIndependentMetadata(MetadataSources sources) {
-        TypeDefBinder.bind( metadata, index );
+		assertBindingContextExists();
+		TypeDefBinder.bind( bindingContext );
+	}
+
+	private void assertBindingContextExists() {
+		if ( bindingContext == null ) {
+			throw new AssertionFailure( "The binding context should exist. Has prepare been called!?" );
+		}
 	}
 
 	@Override
 	public void processTypeDependentMetadata(MetadataSources sources) {
-        IdGeneratorBinder.bind( metadata, index );
+		assertBindingContextExists();
+		IdGeneratorBinder.bind( bindingContext );
 	}
 
 	@Override
 	public void processMappingMetadata(MetadataSources sources, List<String> processedEntityNames) {
+		assertBindingContextExists();
 		// need to order our annotated entities into an order we can process
 		Set<ConfiguredClassHierarchy<EntityClass>> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
-				this
+				bindingContext
 		);
 
 		// now we process each hierarchy one at the time
 		Hierarchical parent = null;
 		for ( ConfiguredClassHierarchy<EntityClass> hierarchy : hierarchies ) {
 			for ( EntityClass entityClass : hierarchy ) {
 				// for classes annotated w/ @Entity we create a EntityBinding
 				if ( ConfiguredClassType.ENTITY.equals( entityClass.getConfiguredClassType() ) ) {
 					LOG.debugf( "Binding entity from annotated class: %s", entityClass.getName() );
-					EntityBinder entityBinder = new EntityBinder( entityClass, parent, this );
+					EntityBinder entityBinder = new EntityBinder( entityClass, parent, bindingContext );
 					EntityBinding binding = entityBinder.bind( processedEntityNames );
 					parent = binding.getEntity();
 				}
 				// for classes annotated w/ @MappedSuperclass we just create the domain instance
 				// the attribute bindings will be part of the first entity subclass
 				else if ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( entityClass.getConfiguredClassType() ) ) {
 					parent = new Superclass(
 							entityClass.getName(),
 							entityClass.getName(),
-							makeClassReference( entityClass.getName() ),
+							bindingContext.makeClassReference( entityClass.getName() ),
 							parent
 					);
 				}
 				// for classes which are not annotated at all we create the NonEntity domain class
 				// todo - not sure whether this is needed. It might be that we don't need this information (HF)
 				else {
 					parent = new NonEntity(
-							entityClass.getName(), 
 							entityClass.getName(),
-							makeClassReference( entityClass.getName() ),
+							entityClass.getName(),
+							bindingContext.makeClassReference( entityClass.getName() ),
 							parent
 					);
 				}
 			}
 		}
 	}
 
-	private Set<ConfiguredClassHierarchy<EntityClass>> createEntityHierarchies() {
-		return ConfiguredClassHierarchyBuilder.createEntityHierarchies( this );
-	}
-
 	@Override
 	public void processMappingDependentMetadata(MetadataSources sources) {
-		TableBinder.bind( metadata, index );
-		FetchProfileBinder.bind( metadata, index );
-		QueryBinder.bind( metadata, index );
-		FilterDefBinder.bind( metadata, index );
-	}
-
-	@Override
-	public Index getIndex() {
-		return index;
-	}
-
-	@Override
-	public ClassInfo getClassInfo(String name) {
-		DotName dotName = DotName.createSimple( name );
-		return index.getClassByName( dotName );
+		TableBinder.bind( bindingContext );
+		FetchProfileBinder.bind( bindingContext );
+		QueryBinder.bind( bindingContext );
+		FilterDefBinder.bind( bindingContext );
 	}
 
-	@Override
-	public void resolveAllTypes(String className) {
-		// the resolved type for the top level class in the hierarchy
-		Class<?> clazz = classLoaderService.getValue().classForName( className );
-		ResolvedType resolvedType = typeResolver.resolve( clazz );
-		while ( resolvedType != null ) {
-			// todo - check whether there is already something in the map
-			resolvedTypeCache.put( clazz, resolvedType );
-			resolvedType = resolvedType.getParentClass();
-			if ( resolvedType != null ) {
-				clazz = resolvedType.getErasedType();
-			}
+	private Index parseAndUpdateIndex(List<JaxbRoot<XMLEntityMappings>> mappings, Index annotationIndex) {
+		List<XMLEntityMappings> list = new ArrayList<XMLEntityMappings>( mappings.size() );
+		for ( JaxbRoot<XMLEntityMappings> jaxbRoot : mappings ) {
+			list.add( jaxbRoot.getRoot() );
 		}
+		return new EntityMappingsMocker( list, annotationIndex, metadata.getServiceRegistry() ).mockNewIndex();
 	}
 
-	@Override
-	public ResolvedType getResolvedType(Class<?> clazz) {
-		// todo - error handling
-		return resolvedTypeCache.get( clazz );
-	}
-
-	@Override
-	public ResolvedTypeWithMembers resolveMemberTypes(ResolvedType type) {
-		// todo : is there a reason we create this resolver every time?
-		MemberResolver memberResolver = new MemberResolver( typeResolver );
-		return memberResolver.resolve( type, null, null );
-	}
-
-	@Override
-	public ServiceRegistry getServiceRegistry() {
-		return getMetadataImplementor().getServiceRegistry();
-	}
-
-	@Override
-	public NamingStrategy getNamingStrategy() {
-		return metadata.getNamingStrategy();
-	}
-
-	@Override
-	public MappingDefaults getMappingDefaults() {
-		return metadata.getMappingDefaults();
-	}
-
-	@Override
-	public MetadataImplementor getMetadataImplementor() {
-		return metadata;
-	}
-
-	@Override
-	public <T> Class<T> locateClassByName(String name) {
-		return classLoaderService.getValue().classForName( name );
-	}
-
-	private Map<String,Type> nameToJavaTypeMap = new HashMap<String, Type>();
-
-	@Override
-	public Type makeJavaType(String className) {
-		Type javaType = nameToJavaTypeMap.get( className );
-		if ( javaType == null ) {
-			javaType = metadata.makeJavaType( className );
-			nameToJavaTypeMap.put( className, javaType );
+	private void indexClass(Indexer indexer, String className) {
+		InputStream stream = metadata.getServiceRegistry().getService( ClassLoaderService.class ).locateResourceStream(
+				className
+		);
+		try {
+			indexer.index( stream );
+		}
+		catch ( IOException e ) {
+			throw new HibernateException( "Unable to open input stream for class " + className, e );
 		}
-		return javaType;
-	}
-
-	@Override
-	public Value<Class<?>> makeClassReference(String className) {
-		return new Value<Class<?>>( locateClassByName( className ) );
-	}
-
-	@Override
-	public boolean isGloballyQuotedIdentifiers() {
-		return metadata.isGloballyQuotedIdentifiers();
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/ConfiguredClassHierarchyBuilder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/ConfiguredClassHierarchyBuilder.java
index 37c5769f56..00fa581a9f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/ConfiguredClassHierarchyBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/ConfiguredClassHierarchyBuilder.java
@@ -1,212 +1,212 @@
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
 package org.hibernate.metamodel.source.annotations;
 
 import javax.persistence.AccessType;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.metamodel.source.annotations.entity.ConfiguredClassHierarchy;
 import org.hibernate.metamodel.source.annotations.entity.EmbeddableClass;
 import org.hibernate.metamodel.source.annotations.entity.EntityClass;
 
 /**
  * Given a (jandex) annotation index build processes all classes with JPA relevant annotations and pre-orders
  * JPA entities respectively their inheritance hierarchy.
  *
  * @author Hardy Ferentschik
  */
 public class ConfiguredClassHierarchyBuilder {
 
 	/**
 	 * Pre-processes the annotated entities from the index and put them into a structure which can
 	 * bound to the Hibernate metamodel.
 	 *
 	 * @param bindingContext The binding context, giving access to needed services and information
 	 *
 	 * @return a set of {@code ConfiguredClassHierarchy}s. One for each "leaf" entity.
 	 */
-	public static Set<ConfiguredClassHierarchy<EntityClass>> createEntityHierarchies(AnnotationsBindingContext bindingContext) {
+	public static Set<ConfiguredClassHierarchy<EntityClass>> createEntityHierarchies(AnnotationBindingContext bindingContext) {
 		Map<ClassInfo, List<ClassInfo>> processedClassInfos = new HashMap<ClassInfo, List<ClassInfo>>();
 
 		for ( ClassInfo info : bindingContext.getIndex().getKnownClasses() ) {
 			if ( !isEntityClass( info ) ) {
 				continue;
 			}
 
 			if ( processedClassInfos.containsKey( info ) ) {
 				continue;
 			}
 
 			List<ClassInfo> configuredClassList = new ArrayList<ClassInfo>();
 			ClassInfo tmpClassInfo = info;
 			Class<?> clazz = bindingContext.locateClassByName( tmpClassInfo.toString() );
 			while ( clazz != null && !clazz.equals( Object.class ) ) {
 				tmpClassInfo = bindingContext.getIndex().getClassByName( DotName.createSimple( clazz.getName() ) );
 				clazz = clazz.getSuperclass();
 				if ( tmpClassInfo == null ) {
 					continue;
 				}
 
 				if ( existsHierarchyWithClassInfoAsLeaf( processedClassInfos, tmpClassInfo ) ) {
 					List<ClassInfo> classInfoList = processedClassInfos.get( tmpClassInfo );
 					for ( ClassInfo tmpInfo : configuredClassList ) {
 						classInfoList.add( tmpInfo );
 						processedClassInfos.put( tmpInfo, classInfoList );
 					}
 					break;
 				}
 				else {
 					configuredClassList.add( 0, tmpClassInfo );
 					processedClassInfos.put( tmpClassInfo, configuredClassList );
 				}
 			}
 		}
 
 		Set<ConfiguredClassHierarchy<EntityClass>> hierarchies = new HashSet<ConfiguredClassHierarchy<EntityClass>>();
 		List<List<ClassInfo>> processedList = new ArrayList<List<ClassInfo>>();
 		for ( List<ClassInfo> classInfoList : processedClassInfos.values() ) {
 			if ( !processedList.contains( classInfoList ) ) {
 				hierarchies.add( ConfiguredClassHierarchy.createEntityClassHierarchy( classInfoList, bindingContext ) );
 				processedList.add( classInfoList );
 			}
 		}
 
 		return hierarchies;
 	}
 
 	/**
 	 * Builds the configured class hierarchy for a an embeddable class.
 	 *
 	 * @param embeddableClass the top level embedded class
 	 * @param accessType the access type inherited from the class in which the embeddable gets embedded
 	 * @param context the annotation binding context with access to the service registry and the annotation index
 	 *
 	 * @return a set of {@code ConfiguredClassHierarchy}s. One for each "leaf" entity.
 	 */
-	public static ConfiguredClassHierarchy<EmbeddableClass> createEmbeddableHierarchy(Class<?> embeddableClass, AccessType accessType, AnnotationsBindingContext context) {
+	public static ConfiguredClassHierarchy<EmbeddableClass> createEmbeddableHierarchy(Class<?> embeddableClass, AccessType accessType, AnnotationBindingContext context) {
 
 		ClassInfo embeddableClassInfo = context.getClassInfo( embeddableClass.getName() );
 		if ( embeddableClassInfo == null ) {
 			throw new AssertionFailure(
 					String.format(
 							"The specified class %s cannot be found in the annotation index",
 							embeddableClass.getName()
 					)
 			);
 		}
 
 		if ( JandexHelper.getSingleAnnotation( embeddableClassInfo, JPADotNames.EMBEDDABLE ) == null ) {
 			throw new AssertionFailure(
 					String.format(
 							"The specified class %s is not annotated with @Embeddable",
 							embeddableClass.getName()
 					)
 			);
 		}
 
 		List<ClassInfo> classInfoList = new ArrayList<ClassInfo>();
 		ClassInfo tmpClassInfo;
 		Class<?> clazz = embeddableClass;
 		while ( clazz != null && !clazz.equals( Object.class ) ) {
 			tmpClassInfo = context.getIndex().getClassByName( DotName.createSimple( clazz.getName() ) );
 			clazz = clazz.getSuperclass();
 			if ( tmpClassInfo == null ) {
 				continue;
 			}
 
 			classInfoList.add( 0, tmpClassInfo );
 		}
 
 		return ConfiguredClassHierarchy.createEmbeddableClassHierarchy( classInfoList, accessType, context );
 	}
 
 	/**
 	 * Checks whether the passed jandex class info needs to be processed.
 	 *
 	 * @param info the jandex class info
 	 *
 	 * @return {@code true} if the class represented by {@code info} is relevant for the JPA mappings, {@code false} otherwise.
 	 */
 	private static boolean isEntityClass(ClassInfo info) {
 		boolean isConfiguredClass = true;
 		AnnotationInstance jpaEntityAnnotation = JandexHelper.getSingleAnnotation( info, JPADotNames.ENTITY );
 		AnnotationInstance mappedSuperClassAnnotation = JandexHelper.getSingleAnnotation(
 				info, JPADotNames.MAPPED_SUPERCLASS
 		);
 
 		// we are only interested in building the class hierarchies for @Entity or @MappedSuperclass
 		if ( jpaEntityAnnotation == null && mappedSuperClassAnnotation == null ) {
 			return false;
 		}
 
 		// some sanity checks
 		String className = info.toString();
 		assertNotEntityAndMappedSuperClass( jpaEntityAnnotation, mappedSuperClassAnnotation, className );
 
 		AnnotationInstance embeddableAnnotation = JandexHelper.getSingleAnnotation(
 				info, JPADotNames.EMBEDDABLE
 		);
 		assertNotEntityAndEmbeddable( jpaEntityAnnotation, embeddableAnnotation, className );
 
 		return isConfiguredClass;
 	}
 
 	private static boolean existsHierarchyWithClassInfoAsLeaf(Map<ClassInfo, List<ClassInfo>> processedClassInfos, ClassInfo tmpClassInfo) {
 		if ( !processedClassInfos.containsKey( tmpClassInfo ) ) {
 			return false;
 		}
 
 		List<ClassInfo> classInfoList = processedClassInfos.get( tmpClassInfo );
 		return classInfoList.get( classInfoList.size() - 1 ).equals( tmpClassInfo );
 	}
 
 	private static void assertNotEntityAndMappedSuperClass(AnnotationInstance jpaEntityAnnotation, AnnotationInstance mappedSuperClassAnnotation, String className) {
 		if ( jpaEntityAnnotation != null && mappedSuperClassAnnotation != null ) {
 			throw new AnnotationException(
 					"An entity cannot be annotated with both @Entity and @MappedSuperclass. " + className + " has both annotations."
 			);
 		}
 	}
 
 	private static void assertNotEntityAndEmbeddable(AnnotationInstance jpaEntityAnnotation, AnnotationInstance embeddableAnnotation, String className) {
 		if ( jpaEntityAnnotation != null && embeddableAnnotation != null ) {
 			throw new AnnotationException(
 					"An entity cannot be annotated with both @Entity and @Embeddable. " + className + " has both annotations."
 			);
 		}
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/SimpleAttribute.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/SimpleAttribute.java
index df7e8d4563..4054dba553 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/SimpleAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/SimpleAttribute.java
@@ -1,268 +1,351 @@
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
 
+import java.util.ArrayList;
+import java.util.Arrays;
 import java.util.List;
 import java.util.Map;
 import javax.persistence.DiscriminatorType;
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
 	 * Is this property a discriminator property.
 	 */
 	private final boolean isDiscriminator;
 
 	/**
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
 
+	private final String customWriteFragment;
+	private final String customReadFragment;
+	private final String checkCondition;
+
 	public static SimpleAttribute createSimpleAttribute(String name, Class<?> type, Map<DotName, List<AnnotationInstance>> annotations) {
 		return new SimpleAttribute( name, type, annotations, false );
 	}
 
 	public static SimpleAttribute createSimpleAttribute(SimpleAttribute simpleAttribute, ColumnValues columnValues) {
-		SimpleAttribute attribute = new SimpleAttribute( simpleAttribute.getName(), simpleAttribute.getJavaType(), simpleAttribute.annotations(), false );
+		SimpleAttribute attribute = new SimpleAttribute(
+				simpleAttribute.getName(),
+				simpleAttribute.getJavaType(),
+				simpleAttribute.annotations(),
+				false
+		);
 		attribute.columnValues = columnValues;
 		return attribute;
 	}
 
 	public static SimpleAttribute createDiscriminatorAttribute(Map<DotName, List<AnnotationInstance>> annotations) {
 		AnnotationInstance discriminatorOptionsAnnotation = JandexHelper.getSingleAnnotation(
 				annotations, JPADotNames.DISCRIMINATOR_COLUMN
 		);
 		String name = DiscriminatorColumnValues.DEFAULT_DISCRIMINATOR_COLUMN_NAME;
 		Class<?> type = String.class; // string is the discriminator default
 		if ( discriminatorOptionsAnnotation != null ) {
 			name = discriminatorOptionsAnnotation.value( "name" ).asString();
 
 			DiscriminatorType discriminatorType = Enum.valueOf(
 					DiscriminatorType.class, discriminatorOptionsAnnotation.value( "discriminatorType" ).asEnum()
 			);
 			switch ( discriminatorType ) {
 				case STRING: {
 					type = String.class;
 					break;
 				}
 				case CHAR: {
 					type = Character.class;
 					break;
 				}
 				case INTEGER: {
 					type = Integer.class;
 					break;
 				}
 				default: {
 					throw new AnnotationException( "Unsupported discriminator type: " + discriminatorType );
 				}
 			}
 		}
 		return new SimpleAttribute( name, type, annotations, true );
 	}
 
 	SimpleAttribute(String name, Class<?> type, Map<DotName, List<AnnotationInstance>> annotations, boolean isDiscriminator) {
 		super( name, type, annotations );
 
 		this.isDiscriminator = isDiscriminator;
 
-
 		AnnotationInstance idAnnotation = JandexHelper.getSingleAnnotation( annotations, JPADotNames.ID );
-        AnnotationInstance embeddedIdAnnotation = JandexHelper.getSingleAnnotation( annotations, JPADotNames.EMBEDDED_ID );
-		isId = !(idAnnotation == null && embeddedIdAnnotation == null);
+		AnnotationInstance embeddedIdAnnotation = JandexHelper.getSingleAnnotation(
+				annotations,
+				JPADotNames.EMBEDDED_ID
+		);
+		isId = !( idAnnotation == null && embeddedIdAnnotation == null );
 
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
-	}
 
+		String[] readWrite;
+		List<AnnotationInstance> columnTransformerAnnotations = getAllColumnTransformerAnnotations();
+		readWrite = createCustomReadWrite( columnTransformerAnnotations );
+		this.customReadFragment = readWrite[0];
+		this.customWriteFragment = readWrite[1];
+		this.checkCondition = parseCheckAnnotation();
+	}
 
 	public final ColumnValues getColumnValues() {
 		return columnValues;
 	}
 
 	public boolean isId() {
 		return isId;
 	}
 
 	public boolean isVersioned() {
 		return isVersioned;
 	}
 
 	public boolean isDiscriminator() {
 		return isDiscriminator;
 	}
 
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
 
+	public String getCustomWriteFragment() {
+		return customWriteFragment;
+	}
+
+	public String getCustomReadFragment() {
+		return customReadFragment;
+	}
+
+	public String getCheckCondition() {
+		return checkCondition;
+	}
+
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "SimpleAttribute" );
 		sb.append( "{isId=" ).append( isId );
 		sb.append( ", isVersioned=" ).append( isVersioned );
 		sb.append( ", isDiscriminator=" ).append( isDiscriminator );
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
+
+	private List<AnnotationInstance> getAllColumnTransformerAnnotations() {
+		List<AnnotationInstance> allColumnTransformerAnnotations = new ArrayList<AnnotationInstance>();
+
+		// not quite sure about the usefulness of @ColumnTransformers (HF)
+		AnnotationInstance columnTransformersAnnotations = getIfExists( HibernateDotNames.COLUMN_TRANSFORMERS );
+		if ( columnTransformersAnnotations != null ) {
+			AnnotationInstance[] annotationInstances = allColumnTransformerAnnotations.get( 0 ).value().asNestedArray();
+			allColumnTransformerAnnotations.addAll( Arrays.asList( annotationInstances ) );
+		}
+
+		AnnotationInstance columnTransformerAnnotation = getIfExists( HibernateDotNames.COLUMN_TRANSFORMER );
+		if ( columnTransformerAnnotation != null ) {
+			allColumnTransformerAnnotations.add( columnTransformerAnnotation );
+		}
+		return allColumnTransformerAnnotations;
+	}
+
+	private String[] createCustomReadWrite(List<AnnotationInstance> columnTransformerAnnotations) {
+		String[] readWrite = new String[2];
+
+		boolean alreadyProcessedForColumn = false;
+		for ( AnnotationInstance annotationInstance : columnTransformerAnnotations ) {
+			String forColumn = annotationInstance.value( "forColumn" ) == null ?
+					null : annotationInstance.value( "forColumn" ).asString();
+
+			if ( forColumn != null && !forColumn.equals( getName() ) ) {
+				continue;
+			}
+
+			if ( alreadyProcessedForColumn ) {
+				throw new AnnotationException( "Multiple definition of read/write conditions for column " + getName() );
+			}
+
+			readWrite[0] = annotationInstance.value( "read" ) == null ?
+					null : annotationInstance.value( "read" ).asString();
+			readWrite[1] = annotationInstance.value( "write" ) == null ?
+					null : annotationInstance.value( "write" ).asString();
+
+			alreadyProcessedForColumn = true;
+		}
+		return readWrite;
+	}
+
+	private String parseCheckAnnotation() {
+		String checkCondition = null;
+		AnnotationInstance checkAnnotation = getIfExists( HibernateDotNames.CHECK );
+		if ( checkAnnotation != null ) {
+			checkCondition = checkAnnotation.value( "constraints" ).toString();
+		}
+		return checkCondition;
+	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ConfiguredClass.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ConfiguredClass.java
index 7c2734db0b..1ae68ea151 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ConfiguredClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ConfiguredClass.java
@@ -1,633 +1,633 @@
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
 
 import javax.persistence.AccessType;
 import java.lang.reflect.Field;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.lang.reflect.Type;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.EnumMap;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.TreeMap;
 
 import com.fasterxml.classmate.ResolvedTypeWithMembers;
 import com.fasterxml.classmate.members.HierarchicType;
 import com.fasterxml.classmate.members.ResolvedMember;
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationTarget;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 import org.jboss.jandex.FieldInfo;
 import org.jboss.jandex.MethodInfo;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
-import org.hibernate.metamodel.source.annotations.AnnotationsBindingContext;
+import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.ConfiguredClassHierarchyBuilder;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.source.annotations.ReflectionHelper;
 import org.hibernate.metamodel.source.annotations.attribute.AssociationAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.AttributeOverride;
 import org.hibernate.metamodel.source.annotations.attribute.AttributeType;
 import org.hibernate.metamodel.source.annotations.attribute.MappedAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.SimpleAttribute;
 
 /**
  * Base class for a configured entity, mapped super class or embeddable
  *
  * @author Hardy Ferentschik
  */
 public class ConfiguredClass {
 	public static final Logger LOG = Logger.getLogger( ConfiguredClass.class.getName() );
 
 	/**
 	 * The parent of this configured class or {@code null} in case this configured class is the root of a hierarchy.
 	 */
 	private final ConfiguredClass parent;
 
 	/**
 	 * The Jandex class info for this configured class. Provides access to the annotation defined on this configured class.
 	 */
 	private final ClassInfo classInfo;
 
 	/**
 	 * The actual java type.
 	 */
 	private final Class<?> clazz;
 
 	/**
 	 * The default access type for this entity
 	 */
 	private final AccessType classAccessType;
 
 	/**
 	 * The type of configured class, entity, mapped super class, embeddable, ...
 	 */
 	private final ConfiguredClassType configuredClassType;
 
 	/**
 	 * The id attributes
 	 */
 	private final Map<String, SimpleAttribute> idAttributeMap;
 
 	/**
 	 * The mapped association attributes for this entity
 	 */
 	private final Map<String, AssociationAttribute> associationAttributeMap;
 
 	/**
 	 * The mapped simple attributes for this entity
 	 */
 	private final Map<String, SimpleAttribute> simpleAttributeMap;
 
 	/**
 	 * The embedded classes for this entity
 	 */
 	private final Map<String, EmbeddableClass> embeddedClasses = new HashMap<String, EmbeddableClass>();
 
 	/**
 	 * A map of all attribute overrides defined in this class. The override name is "normalised", meaning as if specified
 	 * on class level. If the override is specified on attribute level the attribute name is used as prefix.
 	 */
 	private final Map<String, AttributeOverride> attributeOverrideMap;
 
 	private final Set<String> transientFieldNames = new HashSet<String>();
 	private final Set<String> transientMethodNames = new HashSet<String>();
 
-	private final AnnotationsBindingContext context;
+	private final AnnotationBindingContext context;
 
 	public ConfiguredClass(
 			ClassInfo classInfo,
 			AccessType defaultAccessType,
 			ConfiguredClass parent,
-			AnnotationsBindingContext context) {
+			AnnotationBindingContext context) {
 		this.parent = parent;
 		this.context = context;
 		this.classInfo = classInfo;
 		this.clazz = context.locateClassByName( classInfo.toString() );
 		this.configuredClassType = determineType();
 		this.classAccessType = determineClassAccessType( defaultAccessType );
 		this.simpleAttributeMap = new TreeMap<String, SimpleAttribute>();
 		this.idAttributeMap = new TreeMap<String, SimpleAttribute>();
 		this.associationAttributeMap = new TreeMap<String, AssociationAttribute>();
 
 		collectAttributes();
 		attributeOverrideMap = Collections.unmodifiableMap( findAttributeOverrides() );
 	}
 
 	public String getName() {
 		return clazz.getName();
 	}
 
 	public Class<?> getConfiguredClass() {
 		return clazz;
 	}
 
 	public ClassInfo getClassInfo() {
 		return classInfo;
 	}
 
 	public ConfiguredClass getParent() {
 		return parent;
 	}
 
 	public ConfiguredClassType getConfiguredClassType() {
 		return configuredClassType;
 	}
 
 	public Iterable<SimpleAttribute> getSimpleAttributes() {
 		return simpleAttributeMap.values();
 	}
 
 	public Iterable<SimpleAttribute> getIdAttributes() {
 		return idAttributeMap.values();
 	}
 
 	public Iterable<AssociationAttribute> getAssociationAttributes() {
 		return associationAttributeMap.values();
 	}
 
 	public Map<String, EmbeddableClass> getEmbeddedClasses() {
 		return embeddedClasses;
 	}
 
 	public MappedAttribute getMappedAttribute(String propertyName) {
 		MappedAttribute attribute;
 		attribute = simpleAttributeMap.get( propertyName );
 		if ( attribute == null ) {
 			attribute = associationAttributeMap.get( propertyName );
 		}
 		if ( attribute == null ) {
 			attribute = idAttributeMap.get( propertyName );
 		}
 		return attribute;
 	}
 
 	public Map<String, AttributeOverride> getAttributeOverrideMap() {
 		return attributeOverrideMap;
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "ConfiguredClass" );
 		sb.append( "{clazz=" ).append( clazz.getSimpleName() );
 		sb.append( ", classAccessType=" ).append( classAccessType );
 		sb.append( ", configuredClassType=" ).append( configuredClassType );
 		sb.append( ", idAttributeMap=" ).append( idAttributeMap );
 		sb.append( ", simpleAttributeMap=" ).append( simpleAttributeMap );
 		sb.append( ", associationAttributeMap=" ).append( associationAttributeMap );
 		sb.append( '}' );
 		return sb.toString();
 	}
 
 	private ConfiguredClassType determineType() {
 		if ( classInfo.annotations().containsKey( JPADotNames.ENTITY ) ) {
 			return ConfiguredClassType.ENTITY;
 		}
 		else if ( classInfo.annotations().containsKey( JPADotNames.MAPPED_SUPERCLASS ) ) {
 			return ConfiguredClassType.MAPPED_SUPERCLASS;
 		}
 		else if ( classInfo.annotations().containsKey( JPADotNames.EMBEDDABLE ) ) {
 			return ConfiguredClassType.EMBEDDABLE;
 		}
 		else {
 			return ConfiguredClassType.NON_ENTITY;
 		}
 	}
 
 	private AccessType determineClassAccessType(AccessType defaultAccessType) {
 		// default to the hierarchy access type to start with
 		AccessType accessType = defaultAccessType;
 
 		AnnotationInstance accessAnnotation = JandexHelper.getSingleAnnotation( classInfo, JPADotNames.ACCESS );
 		if ( accessAnnotation != null ) {
 			accessType = JandexHelper.getValueAsEnum( accessAnnotation, "value", AccessType.class );
 		}
 
 		return accessType;
 	}
 
 	/**
 	 * Find all attributes for this configured class and add them to the corresponding map
 	 */
 	private void collectAttributes() {
 		// find transient field and method names
 		findTransientFieldAndMethodNames();
 
 		// use the class mate library to generic types
 		ResolvedTypeWithMembers resolvedType = context.resolveMemberTypes( context.getResolvedType( clazz ) );
 		for ( HierarchicType hierarchicType : resolvedType.allTypesAndOverrides() ) {
 			if ( hierarchicType.getType().getErasedType().equals( clazz ) ) {
 				resolvedType = context.resolveMemberTypes( hierarchicType.getType() );
 				break;
 			}
 		}
 
 		if ( resolvedType == null ) {
 			throw new AssertionFailure( "Unable to resolve types for " + clazz.getName() );
 		}
 
 		Set<String> explicitlyConfiguredMemberNames = createExplicitlyConfiguredAccessProperties( resolvedType );
 
 		if ( AccessType.FIELD.equals( classAccessType ) ) {
 			Field fields[] = clazz.getDeclaredFields();
 			Field.setAccessible( fields, true );
 			for ( Field field : fields ) {
 				if ( isPersistentMember( transientFieldNames, explicitlyConfiguredMemberNames, field ) ) {
 					createMappedProperty( field, resolvedType );
 				}
 			}
 		}
 		else {
 			Method[] methods = clazz.getDeclaredMethods();
 			Method.setAccessible( methods, true );
 			for ( Method method : methods ) {
 				if ( isPersistentMember( transientMethodNames, explicitlyConfiguredMemberNames, method ) ) {
 					createMappedProperty( method, resolvedType );
 				}
 			}
 		}
 	}
 
 	private boolean isPersistentMember(Set<String> transientNames, Set<String> explicitlyConfiguredMemberNames, Member member) {
 		if ( !ReflectionHelper.isProperty( member ) ) {
 			return false;
 		}
 
 		if ( transientNames.contains( member.getName() ) ) {
 			return false;
 		}
 
 		if ( explicitlyConfiguredMemberNames.contains( member.getName() ) ) {
 			return false;
 		}
 
 		return true;
 	}
 
 	/**
 	 * Creates {@code MappedProperty} instances for the explicitly configured persistent properties
 	 *
 	 * @param resolvedMembers the resolved type parameters for this class
 	 *
 	 * @return the property names of the explicitly configured attribute names in a set
 	 */
 	private Set<String> createExplicitlyConfiguredAccessProperties(ResolvedTypeWithMembers resolvedMembers) {
 		Set<String> explicitAccessMembers = new HashSet<String>();
 
 		List<AnnotationInstance> accessAnnotations = classInfo.annotations().get( JPADotNames.ACCESS );
 		if ( accessAnnotations == null ) {
 			return explicitAccessMembers;
 		}
 
 		// iterate over all @Access annotations defined on the current class
 		for ( AnnotationInstance accessAnnotation : accessAnnotations ) {
 			// we are only interested at annotations defined on fields and methods
 			AnnotationTarget annotationTarget = accessAnnotation.target();
 			if ( !( annotationTarget.getClass().equals( MethodInfo.class ) || annotationTarget.getClass()
 					.equals( FieldInfo.class ) ) ) {
 				continue;
 			}
 
 			AccessType accessType = JandexHelper.getValueAsEnum( accessAnnotation, "value", AccessType.class );
 
 			if ( !isExplicitAttributeAccessAnnotationPlacedCorrectly( annotationTarget, accessType ) ) {
 				continue;
 			}
 
 
 			// the placement is correct, get the member
 			Member member;
 			if ( annotationTarget instanceof MethodInfo ) {
 				Method m;
 				try {
 					m = clazz.getMethod( ( (MethodInfo) annotationTarget ).name() );
 				}
 				catch ( NoSuchMethodException e ) {
 					throw new HibernateException(
 							"Unable to load method "
 									+ ( (MethodInfo) annotationTarget ).name()
 									+ " of class " + clazz.getName()
 					);
 				}
 				member = m;
 			}
 			else {
 				Field f;
 				try {
 					f = clazz.getField( ( (FieldInfo) annotationTarget ).name() );
 				}
 				catch ( NoSuchFieldException e ) {
 					throw new HibernateException(
 							"Unable to load field "
 									+ ( (FieldInfo) annotationTarget ).name()
 									+ " of class " + clazz.getName()
 					);
 				}
 				member = f;
 			}
 			if ( ReflectionHelper.isProperty( member ) ) {
 				createMappedProperty( member, resolvedMembers );
 				explicitAccessMembers.add( member.getName() );
 			}
 		}
 		return explicitAccessMembers;
 	}
 
 	private boolean isExplicitAttributeAccessAnnotationPlacedCorrectly(AnnotationTarget annotationTarget, AccessType accessType) {
 		// when the access type of the class is FIELD
 		// overriding access annotations must be placed on properties AND have the access type PROPERTY
 		if ( AccessType.FIELD.equals( classAccessType ) ) {
 			if ( !( annotationTarget instanceof MethodInfo ) ) {
 				LOG.tracef(
 						"The access type of class %s is AccessType.FIELD. To override the access for an attribute " +
 								"@Access has to be placed on the property (getter)", classInfo.name().toString()
 				);
 				return false;
 			}
 
 			if ( !AccessType.PROPERTY.equals( accessType ) ) {
 				LOG.tracef(
 						"The access type of class %s is AccessType.FIELD. To override the access for an attribute " +
 								"@Access has to be placed on the property (getter) with an access type of AccessType.PROPERTY. " +
 								"Using AccessType.FIELD on the property has no effect",
 						classInfo.name().toString()
 				);
 				return false;
 			}
 		}
 
 		// when the access type of the class is PROPERTY
 		// overriding access annotations must be placed on fields and have the access type FIELD
 		if ( AccessType.PROPERTY.equals( classAccessType ) ) {
 			if ( !( annotationTarget instanceof FieldInfo ) ) {
 				LOG.tracef(
 						"The access type of class %s is AccessType.PROPERTY. To override the access for a field " +
 								"@Access has to be placed on the field ", classInfo.name().toString()
 				);
 				return false;
 			}
 
 			if ( !AccessType.FIELD.equals( accessType ) ) {
 				LOG.tracef(
 						"The access type of class %s is AccessType.PROPERTY. To override the access for a field " +
 								"@Access has to be placed on the field with an access type of AccessType.FIELD. " +
 								"Using AccessType.PROPERTY on the field has no effect",
 						classInfo.name().toString()
 				);
 				return false;
 			}
 		}
 		return true;
 	}
 
 	private void createMappedProperty(Member member, ResolvedTypeWithMembers resolvedType) {
 		final String attributeName = ReflectionHelper.getPropertyName( member );
 		ResolvedMember[] resolvedMembers;
 		if ( member instanceof Field ) {
 			resolvedMembers = resolvedType.getMemberFields();
 		}
 		else {
 			resolvedMembers = resolvedType.getMemberMethods();
 		}
 		final Class<?> type = (Class<?>) findResolvedType( member.getName(), resolvedMembers );
 		final Map<DotName, List<AnnotationInstance>> annotations = JandexHelper.getMemberAnnotations(
 				classInfo, member.getName()
 		);
 
 		AttributeType attributeType = determineAttributeType( annotations );
 		switch ( attributeType ) {
 			case BASIC: {
 				SimpleAttribute attribute = SimpleAttribute.createSimpleAttribute( attributeName, type, annotations );
 				if ( attribute.isId() ) {
 					idAttributeMap.put( attributeName, attribute );
 				}
 				else {
 					simpleAttributeMap.put( attributeName, attribute );
 				}
 				break;
 			}
 			case ELEMENT_COLLECTION:
 			case EMBEDDED_ID:
 
 			case EMBEDDED: {
 				resolveEmbeddable( attributeName, type );
 			}
 			// TODO handle the different association types
 			default: {
 				AssociationAttribute attribute = AssociationAttribute.createAssociationAttribute(
 						attributeName, type, attributeType, annotations
 				);
 				associationAttributeMap.put( attributeName, attribute );
 			}
 		}
 	}
 
 	private void resolveEmbeddable(String attributeName, Class<?> type) {
 		ClassInfo embeddableClassInfo = context.getClassInfo( type.getName() );
 		if ( classInfo == null ) {
 			String msg = String.format(
 					"Attribute %s of entity %s is annotated with @Embedded, but no embeddable configuration for type %s can be found.",
 					attributeName,
 					getName(),
 					type.getName()
 			);
 			throw new AnnotationException( msg );
 		}
 
 		context.resolveAllTypes( type.getName() );
 		ConfiguredClassHierarchy<EmbeddableClass> hierarchy = ConfiguredClassHierarchyBuilder.createEmbeddableHierarchy(
 				context.<Object>locateClassByName( embeddableClassInfo.toString() ),
 				classAccessType,
 				context
 		);
 		embeddedClasses.put( attributeName, hierarchy.getLeaf() );
 	}
 
 	/**
 	 * Given the annotations defined on a persistent attribute this methods determines the attribute type.
 	 *
 	 * @param annotations the annotations defined on the persistent attribute
 	 *
 	 * @return an instance of the {@code AttributeType} enum
 	 */
 	private AttributeType determineAttributeType(Map<DotName, List<AnnotationInstance>> annotations) {
 		EnumMap<AttributeType, AnnotationInstance> discoveredAttributeTypes =
 				new EnumMap<AttributeType, AnnotationInstance>( AttributeType.class );
 
 		AnnotationInstance oneToOne = JandexHelper.getSingleAnnotation( annotations, JPADotNames.ONE_TO_ONE );
 		if ( oneToOne != null ) {
 			discoveredAttributeTypes.put( AttributeType.ONE_TO_ONE, oneToOne );
 		}
 
 		AnnotationInstance oneToMany = JandexHelper.getSingleAnnotation( annotations, JPADotNames.ONE_TO_MANY );
 		if ( oneToMany != null ) {
 			discoveredAttributeTypes.put( AttributeType.ONE_TO_MANY, oneToMany );
 		}
 
 		AnnotationInstance manyToOne = JandexHelper.getSingleAnnotation( annotations, JPADotNames.MANY_TO_ONE );
 		if ( manyToOne != null ) {
 			discoveredAttributeTypes.put( AttributeType.MANY_TO_ONE, manyToOne );
 		}
 
 		AnnotationInstance manyToMany = JandexHelper.getSingleAnnotation( annotations, JPADotNames.MANY_TO_MANY );
 		if ( manyToMany != null ) {
 			discoveredAttributeTypes.put( AttributeType.MANY_TO_MANY, manyToMany );
 		}
 
 		AnnotationInstance embedded = JandexHelper.getSingleAnnotation( annotations, JPADotNames.EMBEDDED );
 		if ( embedded != null ) {
 			discoveredAttributeTypes.put( AttributeType.EMBEDDED, embedded );
 		}
 
 		AnnotationInstance embeddIded = JandexHelper.getSingleAnnotation( annotations, JPADotNames.EMBEDDED_ID );
 		if ( embeddIded != null ) {
 			discoveredAttributeTypes.put( AttributeType.EMBEDDED_ID, embeddIded );
 		}
 
 		AnnotationInstance elementCollection = JandexHelper.getSingleAnnotation(
 				annotations,
 				JPADotNames.ELEMENT_COLLECTION
 		);
 		if ( elementCollection != null ) {
 			discoveredAttributeTypes.put( AttributeType.ELEMENT_COLLECTION, elementCollection );
 		}
 
 		if ( discoveredAttributeTypes.size() == 0 ) {
 			return AttributeType.BASIC;
 		}
 		else if ( discoveredAttributeTypes.size() == 1 ) {
 			return discoveredAttributeTypes.keySet().iterator().next();
 		}
 		else {
 			throw new AnnotationException( "More than one association type configured for property  " + getName() + " of class " + getName() );
 		}
 	}
 
 	private Type findResolvedType(String name, ResolvedMember[] resolvedMembers) {
 		for ( ResolvedMember resolvedMember : resolvedMembers ) {
 			if ( resolvedMember.getName().equals( name ) ) {
 				return resolvedMember.getType().getErasedType();
 			}
 		}
 		throw new AssertionFailure(
 				String.format(
 						"Unable to resolve type of attribute %s of class %s",
 						name,
 						classInfo.name().toString()
 				)
 		);
 	}
 
 	/**
 	 * Populates the sets of transient field and method names.
 	 */
 	private void findTransientFieldAndMethodNames() {
 		List<AnnotationInstance> transientMembers = classInfo.annotations().get( JPADotNames.TRANSIENT );
 		if ( transientMembers == null ) {
 			return;
 		}
 
 		for ( AnnotationInstance transientMember : transientMembers ) {
 			AnnotationTarget target = transientMember.target();
 			if ( target instanceof FieldInfo ) {
 				transientFieldNames.add( ( (FieldInfo) target ).name() );
 			}
 			else {
 				transientMethodNames.add( ( (MethodInfo) target ).name() );
 			}
 		}
 	}
 
 	private Map<String, AttributeOverride> findAttributeOverrides() {
 		Map<String, AttributeOverride> attributeOverrideList = new HashMap<String, AttributeOverride>();
 
 		AnnotationInstance attributeOverrideAnnotation = JandexHelper.getSingleAnnotation(
 				classInfo,
 				JPADotNames.ATTRIBUTE_OVERRIDE
 		);
 		if ( attributeOverrideAnnotation != null ) {
 			String prefix = createPathPrefix( attributeOverrideAnnotation );
 			AttributeOverride override = new AttributeOverride( prefix, attributeOverrideAnnotation );
 			attributeOverrideList.put( override.getAttributePath(), override );
 		}
 
 		AnnotationInstance attributeOverridesAnnotation = JandexHelper.getSingleAnnotation(
 				classInfo,
 				JPADotNames.ATTRIBUTE_OVERRIDES
 		);
 		if ( attributeOverridesAnnotation != null ) {
 			AnnotationInstance[] annotationInstances = attributeOverridesAnnotation.value().asNestedArray();
 			for ( AnnotationInstance annotationInstance : annotationInstances ) {
 				String prefix = createPathPrefix( annotationInstance );
 				AttributeOverride override = new AttributeOverride( prefix, annotationInstance );
 				attributeOverrideList.put( override.getAttributePath(), override );
 			}
 		}
 		return attributeOverrideList;
 	}
 
 	private String createPathPrefix(AnnotationInstance attributeOverrideAnnotation) {
 		String prefix = null;
 		AnnotationTarget target = attributeOverrideAnnotation.target();
 		if ( target instanceof FieldInfo || target instanceof MethodInfo ) {
 			prefix = JandexHelper.getPropertyName( target );
 		}
 		return prefix;
 	}
 
 	private List<AnnotationInstance> findAssociationOverrides() {
 		List<AnnotationInstance> associationOverrideList = new ArrayList<AnnotationInstance>();
 
 		AnnotationInstance associationOverrideAnnotation = JandexHelper.getSingleAnnotation(
 				classInfo,
 				JPADotNames.ASSOCIATION_OVERRIDE
 		);
 		if ( associationOverrideAnnotation != null ) {
 			associationOverrideList.add( associationOverrideAnnotation );
 		}
 
 		AnnotationInstance associationOverridesAnnotation = JandexHelper.getSingleAnnotation(
 				classInfo,
 				JPADotNames.ASSOCIATION_OVERRIDES
 		);
 		if ( associationOverrideAnnotation != null ) {
 			AnnotationInstance[] attributeOverride = associationOverridesAnnotation.value().asNestedArray();
 			Collections.addAll( associationOverrideList, attributeOverride );
 		}
 
 		return associationOverrideList;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ConfiguredClassHierarchy.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ConfiguredClassHierarchy.java
index ead8cf76c9..4240c884ba 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ConfiguredClassHierarchy.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ConfiguredClassHierarchy.java
@@ -1,278 +1,272 @@
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
 
-import javax.persistence.AccessType;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
+import javax.persistence.AccessType;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.FieldInfo;
 import org.jboss.jandex.MethodInfo;
 
 import org.hibernate.AnnotationException;
-import org.hibernate.metamodel.source.annotations.AnnotationsBindingContext;
+import org.hibernate.metamodel.binding.InheritanceType;
+import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
-import org.hibernate.metamodel.binding.InheritanceType;
 
 /**
  * Contains information about the access and inheritance type for all classes within a class hierarchy.
  *
  * @author Hardy Ferentschik
  */
 public class ConfiguredClassHierarchy<T extends ConfiguredClass> implements Iterable<T> {
 	private final AccessType defaultAccessType;
 	private final InheritanceType inheritanceType;
 	private final List<T> configuredClasses;
 
-	public static ConfiguredClassHierarchy<EntityClass> createEntityClassHierarchy(List<ClassInfo> classInfoList, AnnotationsBindingContext context) {
+	public static ConfiguredClassHierarchy<EntityClass> createEntityClassHierarchy(List<ClassInfo> classInfoList, AnnotationBindingContext context) {
 		AccessType defaultAccessType = determineDefaultAccessType( classInfoList );
 		InheritanceType inheritanceType = determineInheritanceType( classInfoList );
 		return new ConfiguredClassHierarchy<EntityClass>(
 				classInfoList,
 				context,
 				defaultAccessType,
 				inheritanceType,
 				EntityClass.class
 		);
 	}
 
 	public static ConfiguredClassHierarchy<EmbeddableClass> createEmbeddableClassHierarchy(
 			List<ClassInfo> classes,
 			AccessType accessType,
-			AnnotationsBindingContext context) {
+			AnnotationBindingContext context) {
 		return new ConfiguredClassHierarchy<EmbeddableClass>(
 				classes,
 				context,
 				accessType,
 				InheritanceType.NO_INHERITANCE,
 				EmbeddableClass.class
 		);
 	}
 
+	@SuppressWarnings("unchecked")
 	private ConfiguredClassHierarchy(
 			List<ClassInfo> classInfoList,
-			AnnotationsBindingContext context,
+			AnnotationBindingContext context,
 			AccessType defaultAccessType,
 			InheritanceType inheritanceType,
 			Class<T> configuredClassType) {
 		this.defaultAccessType = defaultAccessType;
 		this.inheritanceType = inheritanceType;
 
 		// the resolved type for the top level class in the hierarchy
 		context.resolveAllTypes( classInfoList.get( classInfoList.size() - 1 ).name().toString() );
 
 		configuredClasses = new ArrayList<T>();
 		T parent = null;
 		for ( ClassInfo info : classInfoList ) {
 			T configuredClass;
 			if ( EntityClass.class.equals( configuredClassType ) ) {
 				configuredClass = (T) new EntityClass(
 						info, (EntityClass) parent, defaultAccessType, inheritanceType, context
 				);
 			}
 			else {
 				configuredClass = (T) new EmbeddableClass(
 						info, (EmbeddableClass) parent, defaultAccessType, context
 				);
 			}
 			configuredClasses.add( configuredClass );
 			parent = configuredClass;
 		}
 	}
 
 	public AccessType getDefaultAccessType() {
 		return defaultAccessType;
 	}
 
 	public InheritanceType getInheritanceType() {
 		return inheritanceType;
 	}
 
 	/**
 	 * @return An iterator iterating in top down manner over the configured classes in this hierarchy.
 	 */
 	public Iterator<T> iterator() {
 		return configuredClasses.iterator();
 	}
 
 	/**
 	 * @return Returns the top level configured class
 	 */
 	public T getRoot() {
 		return configuredClasses.get( 0 );
 	}
 
 	/**
 	 * @return Returns the leaf configured class
 	 */
 	public T getLeaf() {
 		return configuredClasses.get( configuredClasses.size() - 1 );
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "ConfiguredClassHierarchy" );
 		sb.append( "{defaultAccessType=" ).append( defaultAccessType );
 		sb.append( ", configuredClasses=" ).append( configuredClasses );
 		sb.append( '}' );
 		return sb.toString();
 	}
 
 	/**
 	 * @param classes the classes in the hierarchy
 	 *
 	 * @return Returns the default access type for the configured class hierarchy independent of explicit
 	 *         {@code AccessType} annotations. The default access type is determined by the placement of the
 	 *         annotations.
 	 */
 	private static AccessType determineDefaultAccessType(List<ClassInfo> classes) {
-        AccessType accessTypeByEmbeddedIdPlacement = null;
-        AccessType accessTypeByIdPlacement = null;
+		AccessType accessTypeByEmbeddedIdPlacement = null;
+		AccessType accessTypeByIdPlacement = null;
 		for ( ClassInfo info : classes ) {
 			List<AnnotationInstance> idAnnotations = info.annotations().get( JPADotNames.ID );
-            List<AnnotationInstance> embeddedIdAnnotations = info.annotations().get( JPADotNames.EMBEDDED_ID );
+			List<AnnotationInstance> embeddedIdAnnotations = info.annotations().get( JPADotNames.EMBEDDED_ID );
 
-            if ( embeddedIdAnnotations != null && !embeddedIdAnnotations.isEmpty() ) {
-                accessTypeByEmbeddedIdPlacement = determineAccessTypeByIdPlacement( embeddedIdAnnotations );
-            }
+			if ( embeddedIdAnnotations != null && !embeddedIdAnnotations.isEmpty() ) {
+				accessTypeByEmbeddedIdPlacement = determineAccessTypeByIdPlacement( embeddedIdAnnotations );
+			}
 			if ( idAnnotations != null && !idAnnotations.isEmpty() ) {
 				accessTypeByIdPlacement = determineAccessTypeByIdPlacement( idAnnotations );
 			}
 		}
-        if ( accessTypeByEmbeddedIdPlacement != null ) {
-            return accessTypeByEmbeddedIdPlacement;
-        } else if (accessTypeByIdPlacement != null ){
-            return accessTypeByIdPlacement;
-        } else {
-            return throwIdNotFoundAnnotationException( classes );
-        }
-
-
-//
-//
-//		if ( accessType == null ) {
-//			return throwIdNotFoundAnnotationException( classes );
-//		}
-//
-//		return accessType;
+		if ( accessTypeByEmbeddedIdPlacement != null ) {
+			return accessTypeByEmbeddedIdPlacement;
+		}
+		else if ( accessTypeByIdPlacement != null ) {
+			return accessTypeByIdPlacement;
+		}
+		else {
+			return throwIdNotFoundAnnotationException( classes );
+		}
 	}
 
 	private static AccessType determineAccessTypeByIdPlacement(List<AnnotationInstance> idAnnotations) {
 		AccessType accessType = null;
 		for ( AnnotationInstance annotation : idAnnotations ) {
 			AccessType tmpAccessType;
 			if ( annotation.target() instanceof FieldInfo ) {
 				tmpAccessType = AccessType.FIELD;
 			}
 			else if ( annotation.target() instanceof MethodInfo ) {
 				tmpAccessType = AccessType.PROPERTY;
 			}
 			else {
 				throw new AnnotationException( "Invalid placement of @Id annotation" );
 			}
 
 			if ( accessType == null ) {
 				accessType = tmpAccessType;
 			}
 			else {
 				if ( !accessType.equals( tmpAccessType ) ) {
 					throw new AnnotationException( "Inconsistent placement of @Id annotation within hierarchy " );
 				}
 			}
 		}
 		return accessType;
 	}
 
 	private static InheritanceType determineInheritanceType(List<ClassInfo> classes) {
 		if ( classes.size() == 1 ) {
 			return InheritanceType.NO_INHERITANCE;
 		}
 
 		InheritanceType inheritanceType = null;
 		for ( ClassInfo info : classes ) {
 			AnnotationInstance inheritanceAnnotation = JandexHelper.getSingleAnnotation(
 					info, JPADotNames.INHERITANCE
 			);
 			if ( inheritanceAnnotation == null ) {
 				continue;
 			}
 
 			javax.persistence.InheritanceType jpaInheritanceType = Enum.valueOf(
 					javax.persistence.InheritanceType.class, inheritanceAnnotation.value( "strategy" ).asEnum()
 			);
 			InheritanceType tmpInheritanceType = InheritanceType.get( jpaInheritanceType );
 			if ( tmpInheritanceType == null ) {
 				// default inheritance type is single table
 				inheritanceType = InheritanceType.SINGLE_TABLE;
 			}
 
 			if ( inheritanceType == null ) {
 				inheritanceType = tmpInheritanceType;
 			}
 			else {
 				if ( !inheritanceType.equals( tmpInheritanceType ) ) {
 					throw new AnnotationException(
 							"Multiple incompatible instances of @Inheritance specified within classes "
 									+ hierarchyListString( classes )
 					);
 				}
 			}
 		}
 
 		if ( inheritanceType == null ) {
 			// default inheritance type is single table
 			inheritanceType = InheritanceType.SINGLE_TABLE;
 		}
 
 		return inheritanceType;
 	}
 
 	private static AccessType throwIdNotFoundAnnotationException(List<ClassInfo> classes) {
 		StringBuilder builder = new StringBuilder();
 		builder.append( "Unable to determine identifier attribute for class hierarchy consisting of the classe(s) " );
 		builder.append( hierarchyListString( classes ) );
 		throw new AnnotationException( builder.toString() );
 	}
 
 	private static String hierarchyListString(List<ClassInfo> classes) {
 		StringBuilder builder = new StringBuilder();
 		builder.append( "[" );
 
 		int count = 0;
 		for ( ClassInfo info : classes ) {
 			builder.append( info.name().toString() );
 			if ( count < classes.size() - 1 ) {
 				builder.append( ", " );
 			}
 			count++;
 		}
 		builder.append( "]" );
 		return builder.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EmbeddableClass.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EmbeddableClass.java
index a3cc40b188..60791ea86a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EmbeddableClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EmbeddableClass.java
@@ -1,46 +1,46 @@
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
 
 import javax.persistence.AccessType;
 
 import org.jboss.jandex.ClassInfo;
 
-import org.hibernate.metamodel.source.annotations.AnnotationsBindingContext;
+import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 
 /**
  * @author Hardy Ferentschik
  */
 public class EmbeddableClass extends ConfiguredClass {
 	// todo - need to take care of the attribute path (HF)
 	public EmbeddableClass(
 			ClassInfo classInfo,
 			EmbeddableClass parent,
 			AccessType defaultAccessType,
-			AnnotationsBindingContext context) {
+			AnnotationBindingContext context) {
 		super( classInfo, defaultAccessType, parent, context );
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
index 32d875b7a3..31704f080f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
@@ -1,928 +1,994 @@
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
 
-import javax.persistence.GenerationType;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
+import javax.persistence.GenerationType;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.DotName;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.annotations.PolymorphismType;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.Value;
-import org.hibernate.metamodel.source.annotations.AnnotationsBindingContext;
-import org.hibernate.metamodel.source.annotations.HibernateDotNames;
-import org.hibernate.metamodel.source.annotations.JPADotNames;
-import org.hibernate.metamodel.source.annotations.JandexHelper;
-import org.hibernate.metamodel.source.annotations.UnknownInheritanceTypeException;
-import org.hibernate.metamodel.source.annotations.global.IdGeneratorBinder;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.EntityDiscriminator;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
-import org.hibernate.metamodel.binding.state.DiscriminatorBindingState;
 import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
-import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.AttributeContainer;
+import org.hibernate.metamodel.domain.Component;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.domain.SingularAttribute;
+import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
+import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.UniqueKey;
+import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
+import org.hibernate.metamodel.source.annotations.HibernateDotNames;
+import org.hibernate.metamodel.source.annotations.JPADotNames;
+import org.hibernate.metamodel.source.annotations.JandexHelper;
+import org.hibernate.metamodel.source.annotations.UnknownInheritanceTypeException;
 import org.hibernate.metamodel.source.annotations.attribute.AssociationAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.AttributeOverride;
+import org.hibernate.metamodel.source.annotations.attribute.ColumnValues;
 import org.hibernate.metamodel.source.annotations.attribute.DiscriminatorColumnValues;
 import org.hibernate.metamodel.source.annotations.attribute.MappedAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.SimpleAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.state.binding.AttributeBindingStateImpl;
-import org.hibernate.metamodel.source.annotations.attribute.state.binding.DiscriminatorBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.binding.ManyToOneBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.ColumnRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.ManyToOneRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.TupleRelationalStateImpl;
+import org.hibernate.metamodel.source.annotations.global.IdGeneratorBinder;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * Creates the domain and relational metamodel for a configured class and <i>binds</i> them together.
  *
  * @author Hardy Ferentschik
  */
 public class EntityBinder {
 	private final EntityClass entityClass;
 	private final Hierarchical superType;
-	private final AnnotationsBindingContext bindingContext;
+	private final AnnotationBindingContext bindingContext;
 
 	private final Schema.Name schemaName;
 
-	public EntityBinder(EntityClass entityClass, Hierarchical superType, AnnotationsBindingContext bindingContext) {
+	public EntityBinder(EntityClass entityClass, Hierarchical superType, AnnotationBindingContext bindingContext) {
 		this.entityClass = entityClass;
 		this.superType = superType;
 		this.bindingContext = bindingContext;
 		this.schemaName = determineSchemaName();
 	}
 
+	public EntityBinding bind(List<String> processedEntityNames) {
+		if ( processedEntityNames.contains( entityClass.getName() ) ) {
+			return bindingContext.getMetadataImplementor().getEntityBinding( entityClass.getName() );
+		}
+
+		final EntityBinding entityBinding = createEntityBinding();
+
+		bindingContext.getMetadataImplementor().addEntity( entityBinding );
+		processedEntityNames.add( entityBinding.getEntity().getName() );
+
+		return entityBinding;
+	}
+
 	private Schema.Name determineSchemaName() {
 		String schema = bindingContext.getMappingDefaults().getSchemaName();
 		String catalog = bindingContext.getMappingDefaults().getCatalogName();
 
 		final AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.TABLE
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
 
 		if ( bindingContext.isGloballyQuotedIdentifiers() ) {
 			schema = StringHelper.quote( schema );
 			catalog = StringHelper.quote( catalog );
 		}
 		return new Schema.Name( schema, catalog );
 	}
 
-	public EntityBinding bind(List<String> processedEntityNames) {
-		if ( processedEntityNames.contains( entityClass.getName() ) ) {
-			return bindingContext.getMetadataImplementor().getEntityBinding( entityClass.getName() );
-		}
-
-		final EntityBinding entityBinding = doEntityBindingCreation();
-
-		bindingContext.getMetadataImplementor().addEntity( entityBinding );
-		processedEntityNames.add( entityBinding.getEntity().getName() );
-
-		return entityBinding;
-	}
-
-	private EntityBinding doEntityBindingCreation() {
+	private EntityBinding createEntityBinding() {
 		final EntityBinding entityBinding = buildBasicEntityBinding();
 
 		// bind all attributes - simple as well as associations
 		bindAttributes( entityBinding );
 		bindEmbeddedAttributes( entityBinding );
 
 		bindTableUniqueConstraints( entityBinding );
 
 		return entityBinding;
 	}
 
 	private EntityBinding buildBasicEntityBinding() {
 		switch ( entityClass.getInheritanceType() ) {
 			case NO_INHERITANCE: {
 				return doRootEntityBindingCreation();
 			}
 			case SINGLE_TABLE: {
-				return doDiscriminatedSubclassBindingCreation();
+				return doRootEntityBindingCreation();
+				//return doDiscriminatedSubclassBindingCreation();
 			}
 			case JOINED: {
 				return doJoinedSubclassBindingCreation();
 			}
 			case TABLE_PER_CLASS: {
 				return doUnionSubclassBindingCreation();
 			}
 			default: {
 				throw new UnknownInheritanceTypeException( "Unknown InheritanceType : " + entityClass.getInheritanceType() );
 			}
 		}
 	}
 
 	private EntityBinding doRootEntityBindingCreation() {
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.NO_INHERITANCE );
-		entityBinding.setRoot( true );
+		entityBinding.setRoot( entityClass.isEntityRoot() );
 
 		doBasicEntityBinding( entityBinding );
 
 		// technically the rest of these binds should only apply to root entities, but they are really available on all
 		// because we do not currently subtype EntityBinding
 
 		final AnnotationInstance hibernateEntityAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ENTITY
 		);
 
 		// see HHH-6400
 		PolymorphismType polymorphism = PolymorphismType.IMPLICIT;
 		if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "polymorphism" ) != null ) {
 			polymorphism = PolymorphismType.valueOf( hibernateEntityAnnotation.value( "polymorphism" ).asEnum() );
 		}
 		entityBinding.setExplicitPolymorphism( polymorphism == PolymorphismType.EXPLICIT );
 
 		// see HHH-6401
 		OptimisticLockType optimisticLockType = OptimisticLockType.VERSION;
 		if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "optimisticLock" ) != null ) {
-			optimisticLockType = OptimisticLockType.valueOf( hibernateEntityAnnotation.value( "optimisticLock" ).asEnum() );
+			optimisticLockType = OptimisticLockType.valueOf(
+					hibernateEntityAnnotation.value( "optimisticLock" )
+							.asEnum()
+			);
 		}
 		entityBinding.setOptimisticLockStyle( OptimisticLockStyle.valueOf( optimisticLockType.name() ) );
 
 		final AnnotationInstance hibernateImmutableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.IMMUTABLE
 		);
 		final boolean mutable = hibernateImmutableAnnotation == null
 				&& hibernateEntityAnnotation != null
 				&& hibernateEntityAnnotation.value( "mutable" ) != null
 				&& hibernateEntityAnnotation.value( "mutable" ).asBoolean();
 		entityBinding.setMutable( mutable );
 
 		final AnnotationInstance whereAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.WHERE
 		);
 		entityBinding.setWhereFilter(
 				whereAnnotation != null && whereAnnotation.value( "clause" ) != null
 						? whereAnnotation.value( "clause" ).asString()
 						: null
 		);
 
 		final AnnotationInstance rowIdAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ROW_ID
 		);
 		entityBinding.setRowId(
 				rowIdAnnotation != null && rowIdAnnotation.value() != null
 						? rowIdAnnotation.value().asString()
 						: null
 		);
 
 		entityBinding.setCaching( interpretCaching( entityClass, bindingContext ) );
 
 		bindPrimaryTable( entityBinding );
 		bindId( entityBinding );
 
-		if ( entityBinding.getInheritanceType() == InheritanceType.SINGLE_TABLE ) {
+		if ( entityClass.getInheritanceType() == InheritanceType.SINGLE_TABLE ) {
 			bindDiscriminatorColumn( entityBinding );
 		}
 
 		// todo : version
 
 		return entityBinding;
 	}
 
-	private Caching interpretCaching(ConfiguredClass configuredClass, AnnotationsBindingContext bindingContext) {
+	private Caching interpretCaching(ConfiguredClass configuredClass, AnnotationBindingContext bindingContext) {
 		final AnnotationInstance hibernateCacheAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), HibernateDotNames.CACHE
 		);
 		if ( hibernateCacheAnnotation != null ) {
 			final AccessType accessType = hibernateCacheAnnotation.value( "usage" ) == null
 					? bindingContext.getMappingDefaults().getCacheAccessType()
-					: CacheConcurrencyStrategy.parse( hibernateCacheAnnotation.value( "usage" ).asEnum() ).toAccessType();
+					: CacheConcurrencyStrategy.parse( hibernateCacheAnnotation.value( "usage" ).asEnum() )
+					.toAccessType();
 			return new Caching(
 					hibernateCacheAnnotation.value( "region" ) == null
 							? configuredClass.getName()
 							: hibernateCacheAnnotation.value( "region" ).asString(),
 					accessType,
 					hibernateCacheAnnotation.value( "include" ) != null
 							&& "all".equals( hibernateCacheAnnotation.value( "include" ).asString() )
 			);
 		}
 
 		final AnnotationInstance jpaCacheableAnnotation = JandexHelper.getSingleAnnotation(
 				configuredClass.getClassInfo(), JPADotNames.CACHEABLE
 		);
 
 		boolean cacheable = true; // true is the default
 		if ( jpaCacheableAnnotation != null && jpaCacheableAnnotation.value() != null ) {
 			cacheable = jpaCacheableAnnotation.value().asBoolean();
 		}
 
 		final boolean doCaching;
 		switch ( bindingContext.getMetadataImplementor().getOptions().getSharedCacheMode() ) {
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
 
-		if ( ! doCaching ) {
+		if ( !doCaching ) {
 			return null;
 		}
 
 		return new Caching(
 				configuredClass.getName(),
 				bindingContext.getMappingDefaults().getCacheAccessType(),
 				true
 		);
 	}
 
 	private EntityBinding doDiscriminatedSubclassBindingCreation() {
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.SINGLE_TABLE );
 
 		doBasicEntityBinding( entityBinding );
 
 		// todo : bind discriminator-based subclassing specifics...
 
 		return entityBinding;
 	}
 
 	private EntityBinding doJoinedSubclassBindingCreation() {
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.JOINED );
 
 		doBasicEntityBinding( entityBinding );
 
 		// todo : bind join-based subclassing specifics...
 
 		return entityBinding;
 	}
 
 	private EntityBinding doUnionSubclassBindingCreation() {
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setInheritanceType( InheritanceType.TABLE_PER_CLASS );
 
 		doBasicEntityBinding( entityBinding );
 
 		// todo : bind union-based subclassing specifics...
 
 		return entityBinding;
 	}
 
 	private void doBasicEntityBinding(EntityBinding entityBinding) {
 		entityBinding.setEntityMode( EntityMode.POJO );
 
 		final Entity entity = new Entity(
 				entityClass.getName(),
 				entityClass.getName(),
 				bindingContext.makeClassReference( entityClass.getName() ),
 				superType
 		);
 		entityBinding.setEntity( entity );
 
 		final AnnotationInstance jpaEntityAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.ENTITY
 		);
 
 		final AnnotationValue explicitJpaEntityName = jpaEntityAnnotation.value( "name" );
 		if ( explicitJpaEntityName == null ) {
 			entityBinding.setJpaEntityName( entityClass.getName() );
 		}
 		else {
 			entityBinding.setJpaEntityName( explicitJpaEntityName.asString() );
 		}
 
 		final AnnotationInstance hibernateEntityAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ENTITY
 		);
 
 		// see HHH-6397
 		entityBinding.setDynamicInsert(
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "dynamicInsert" ) != null
 						&& hibernateEntityAnnotation.value( "dynamicInsert" ).asBoolean()
 		);
 
 		// see HHH-6398
 		entityBinding.setDynamicUpdate(
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "dynamicUpdate" ) != null
 						&& hibernateEntityAnnotation.value( "dynamicUpdate" ).asBoolean()
 		);
 
 		// see HHH-6399
 		entityBinding.setSelectBeforeUpdate(
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "selectBeforeUpdate" ) != null
 						&& hibernateEntityAnnotation.value( "selectBeforeUpdate" ).asBoolean()
 		);
 
 		// Custom sql loader
 		final AnnotationInstance sqlLoaderAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.LOADER
 		);
 		if ( sqlLoaderAnnotation != null ) {
 			entityBinding.setCustomLoaderName( sqlLoaderAnnotation.value( "namedQuery" ).asString() );
 		}
 
 		// Custom sql insert
 		final AnnotationInstance sqlInsertAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_INSERT
 		);
 		entityBinding.setCustomInsert( createCustomSQL( sqlInsertAnnotation ) );
 
 		// Custom sql update
 		final AnnotationInstance sqlUpdateAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_UPDATE
 		);
 		entityBinding.setCustomUpdate( createCustomSQL( sqlUpdateAnnotation ) );
 
 		// Custom sql delete
 		final AnnotationInstance sqlDeleteAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_DELETE
 		);
 		entityBinding.setCustomDelete( createCustomSQL( sqlDeleteAnnotation ) );
 
 		// Batch size
 		final AnnotationInstance batchSizeAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.BATCH_SIZE
 		);
-		entityBinding.setBatchSize( batchSizeAnnotation == null ? -1 : batchSizeAnnotation.value( "size" ).asInt());
+		entityBinding.setBatchSize( batchSizeAnnotation == null ? -1 : batchSizeAnnotation.value( "size" ).asInt() );
 
 		// Proxy generation
 		final boolean lazy;
 		final Value<Class<?>> proxyInterfaceType;
 		final AnnotationInstance hibernateProxyAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.PROXY
 		);
 		if ( hibernateProxyAnnotation != null ) {
 			lazy = hibernateProxyAnnotation.value( "lazy" ) == null
 					|| hibernateProxyAnnotation.value( "lazy" ).asBoolean();
 			if ( lazy ) {
 				final AnnotationValue proxyClassValue = hibernateProxyAnnotation.value( "proxyClass" );
 				if ( proxyClassValue == null ) {
 					proxyInterfaceType = entity.getClassReferenceUnresolved();
 				}
 				else {
 					proxyInterfaceType = bindingContext.makeClassReference( proxyClassValue.asString() );
 				}
 			}
 			else {
 				proxyInterfaceType = null;
 			}
 		}
 		else {
 			lazy = true;
 			proxyInterfaceType = entity.getClassReferenceUnresolved();
 		}
 		entityBinding.setLazy( lazy );
 		entityBinding.setProxyInterfaceType( proxyInterfaceType );
 
 		// Custom persister
 		final Class<? extends EntityPersister> entityPersisterClass;
 		final AnnotationInstance persisterAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.PERSISTER
 		);
 		if ( persisterAnnotation == null || persisterAnnotation.value( "impl" ) == null ) {
 			if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "persister" ) != null ) {
-				entityPersisterClass = bindingContext.locateClassByName( hibernateEntityAnnotation.value( "persister" ).asString() );
+				entityPersisterClass = bindingContext.locateClassByName(
+						hibernateEntityAnnotation.value( "persister" )
+								.asString()
+				);
 			}
 			else {
 				entityPersisterClass = null;
 			}
 		}
 		else {
 			if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "persister" ) != null ) {
 				// todo : error?
 			}
 			entityPersisterClass = bindingContext.locateClassByName( persisterAnnotation.value( "impl" ).asString() );
 		}
 		entityBinding.setCustomEntityPersisterClass( entityPersisterClass );
 
 		// Custom tuplizer
 		final AnnotationInstance pojoTuplizerAnnotation = locatePojoTuplizerAnnotation();
 		if ( pojoTuplizerAnnotation != null ) {
 			final Class<? extends EntityTuplizer> tuplizerClass =
 					bindingContext.locateClassByName( pojoTuplizerAnnotation.value( "impl" ).asString() );
 			entityBinding.setCustomEntityTuplizerClass( tuplizerClass );
 		}
 
 		// table synchronizations
 		final AnnotationInstance synchronizeAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SYNCHRONIZE
 		);
 		if ( synchronizeAnnotation != null ) {
 			final String[] tableNames = synchronizeAnnotation.value().asStringArray();
 			entityBinding.addSynchronizedTableNames( Arrays.asList( tableNames ) );
 		}
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
-						? ExecuteUpdateResultCheckStyle.NONE
-						: ExecuteUpdateResultCheckStyle.COUNT
+				? ExecuteUpdateResultCheckStyle.NONE
+				: ExecuteUpdateResultCheckStyle.COUNT
 				: ExecuteUpdateResultCheckStyle.valueOf( customSqlAnnotation.value( "check" ).asEnum() );
 
 		return new CustomSQL( sql, isCallable, checkStyle );
 	}
 
 	private void bindPrimaryTable(EntityBinding entityBinding) {
 		final Schema schema = bindingContext.getMetadataImplementor().getDatabase().getSchema( schemaName );
 
 		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(),
 				JPADotNames.TABLE
 		);
 
 		String tableName = null;
 		if ( tableAnnotation != null ) {
 			String explicitTableName = JandexHelper.getValue( tableAnnotation, "name", String.class );
 			if ( StringHelper.isNotEmpty( explicitTableName ) ) {
 				tableName = bindingContext.getNamingStrategy().tableName( explicitTableName );
 			}
 		}
 
 		// no explicit table name given, let's use the entity name as table name (taking inheritance into consideration
 		if ( StringHelper.isEmpty( tableName ) ) {
 			tableName = bindingContext.getNamingStrategy().classToTableName( entityClass.getClassNameForTable() );
 		}
 
-		if ( bindingContext.isGloballyQuotedIdentifiers() && ! Identifier.isQuoted( tableName ) ) {
+		if ( bindingContext.isGloballyQuotedIdentifiers() && !Identifier.isQuoted( tableName ) ) {
 			tableName = StringHelper.quote( tableName );
 		}
 		org.hibernate.metamodel.relational.Table table = schema.locateOrCreateTable( Identifier.toIdentifier( tableName ) );
 		entityBinding.setBaseTable( table );
 
 		AnnotationInstance checkAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.CHECK
 		);
 		if ( checkAnnotation != null ) {
 			table.addCheckConstraint( checkAnnotation.value( "constraints" ).asString() );
 		}
 	}
 
 	private AnnotationInstance locatePojoTuplizerAnnotation() {
 		final AnnotationInstance tuplizersAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.TUPLIZERS
 		);
 		if ( tuplizersAnnotation == null ) {
 			return null;
 		}
 
-		for ( AnnotationInstance tuplizerAnnotation : JandexHelper.getValue( tuplizersAnnotation, "value", AnnotationInstance[].class ) ) {
+		AnnotationInstance[] annotations = JandexHelper.getValue(
+				tuplizersAnnotation,
+				"value",
+				AnnotationInstance[].class
+		);
+		for ( AnnotationInstance tuplizerAnnotation : annotations ) {
 			if ( EntityMode.valueOf( tuplizerAnnotation.value( "entityModeType" ).asEnum() ) == EntityMode.POJO ) {
 				return tuplizerAnnotation;
 			}
 		}
 
 		return null;
 	}
 
 	private void bindDiscriminatorColumn(EntityBinding entityBinding) {
 		final Map<DotName, List<AnnotationInstance>> typeAnnotations = JandexHelper.getTypeAnnotations(
 				entityClass.getClassInfo()
 		);
 		SimpleAttribute discriminatorAttribute = SimpleAttribute.createDiscriminatorAttribute( typeAnnotations );
 		bindSingleMappedAttribute( entityBinding, entityBinding.getEntity(), discriminatorAttribute );
 
-		if ( !( discriminatorAttribute.getColumnValues() instanceof DiscriminatorColumnValues) ) {
+		if ( !( discriminatorAttribute.getColumnValues() instanceof DiscriminatorColumnValues ) ) {
 			throw new AssertionFailure( "Expected discriminator column values" );
 		}
 	}
 
 	private void bindTableUniqueConstraints(EntityBinding entityBinding) {
 		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(),
 				JPADotNames.TABLE
 		);
 		if ( tableAnnotation == null ) {
 			return;
 		}
 		TableSpecification table = entityBinding.getBaseTable();
 		bindUniqueConstraints( tableAnnotation, table );
 	}
 
 	private void bindUniqueConstraints(AnnotationInstance tableAnnotation, TableSpecification table) {
 		AnnotationValue value = tableAnnotation.value( "uniqueConstraints" );
 		if ( value == null ) {
 			return;
 		}
 		AnnotationInstance[] uniqueConstraints = value.asNestedArray();
 		for ( AnnotationInstance unique : uniqueConstraints ) {
 			String name = unique.value( "name" ).asString();
 			UniqueKey uniqueKey = table.getOrCreateUniqueKey( name );
 			String[] columnNames = unique.value( "columnNames" ).asStringArray();
 			if ( columnNames.length == 0 ) {
 				//todo throw exception?
 			}
 			for ( String columnName : columnNames ) {
 				uniqueKey.addColumn( table.locateOrCreateColumn( columnName ) );
 			}
 		}
 	}
 
 	private void bindId(EntityBinding entityBinding) {
 		switch ( entityClass.getIdType() ) {
 			case SIMPLE: {
 				bindSingleIdAnnotation( entityBinding );
 				break;
 			}
 			case COMPOSED: {
 				// todo
 				break;
 			}
 			case EMBEDDED: {
 				bindEmbeddedIdAnnotation( entityBinding );
 				break;
 			}
 			default: {
 			}
 		}
 	}
 
 	private void bindEmbeddedIdAnnotation(EntityBinding entityBinding) {
 		AnnotationInstance idAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.EMBEDDED_ID
 		);
 
 		String idName = JandexHelper.getPropertyName( idAnnotation.target() );
 		MappedAttribute idAttribute = entityClass.getMappedAttribute( idName );
 		if ( !( idAttribute instanceof SimpleAttribute ) ) {
 			throw new AssertionFailure( "Unexpected attribute type for id attribute" );
 		}
 
 		SingularAttribute attribute = entityBinding.getEntity().locateOrCreateComponentAttribute( idName );
 
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleIdAttributeBinding( attribute );
 
 		attributeBinding.initialize( new AttributeBindingStateImpl( (SimpleAttribute) idAttribute ) );
-		attributeBinding.initialize( new ColumnRelationalStateImpl( (SimpleAttribute) idAttribute, bindingContext.getMetadataImplementor() ) );
+		attributeBinding.initialize(
+				new ColumnRelationalStateImpl(
+						(SimpleAttribute) idAttribute,
+						bindingContext.getMetadataImplementor()
+				)
+		);
 		bindSingleIdGeneratedValue( entityBinding, idName );
 
 		TupleRelationalStateImpl state = new TupleRelationalStateImpl();
 		EmbeddableClass embeddableClass = entityClass.getEmbeddedClasses().get( idName );
 		for ( SimpleAttribute attr : embeddableClass.getSimpleAttributes() ) {
 			state.addValueState( new ColumnRelationalStateImpl( attr, bindingContext.getMetadataImplementor() ) );
 		}
 		attributeBinding.initialize( state );
-		Map<String, String> parms = new HashMap<String, String>( 1 );
-		parms.put( IdentifierGenerator.ENTITY_NAME, entityBinding.getEntity().getName() );
-		IdGenerator generator = new IdGenerator( "NAME", "assigned", parms );
+		Map<String, String> parameterMap = new HashMap<String, String>( 1 );
+		parameterMap.put( IdentifierGenerator.ENTITY_NAME, entityBinding.getEntity().getName() );
+		IdGenerator generator = new IdGenerator( "NAME", "assigned", parameterMap );
 		entityBinding.getEntityIdentifier().setIdGenerator( generator );
 		// entityBinding.getEntityIdentifier().createIdentifierGenerator( meta.getIdentifierGeneratorFactory() );
 	}
 
 	private void bindSingleIdAnnotation(EntityBinding entityBinding) {
 		// we know we are dealing w/ a single @Id, but potentially it is defined in a mapped super class
 		ConfiguredClass configuredClass = entityClass;
 		EntityClass superEntity = entityClass.getEntityParent();
 		Hierarchical container = entityBinding.getEntity();
 		Iterator<SimpleAttribute> iter = null;
 		while ( configuredClass != null && configuredClass != superEntity ) {
 			iter = configuredClass.getIdAttributes().iterator();
 			if ( iter.hasNext() ) {
 				break;
 			}
 			configuredClass = configuredClass.getParent();
 			container = container.getSuperType();
 		}
 
 		// if we could not find the attribute our assumptions were wrong
 		if ( iter == null || !iter.hasNext() ) {
 			throw new AnnotationException(
 					String.format(
 							"Unable to find id attribute for class %s",
 							entityClass.getName()
 					)
 			);
 		}
 
 		// now that we have the id attribute we can create the attribute and binding
 		MappedAttribute idAttribute = iter.next();
 		SingularAttribute attribute = container.locateOrCreateSingularAttribute( idAttribute.getName() );
 
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleIdAttributeBinding( attribute );
 		attributeBinding.initialize( new AttributeBindingStateImpl( (SimpleAttribute) idAttribute ) );
-		attributeBinding.initialize( new ColumnRelationalStateImpl( (SimpleAttribute) idAttribute, bindingContext.getMetadataImplementor() ) );
+		attributeBinding.initialize(
+				new ColumnRelationalStateImpl(
+						(SimpleAttribute) idAttribute,
+						bindingContext.getMetadataImplementor()
+				)
+		);
 		bindSingleIdGeneratedValue( entityBinding, idAttribute.getName() );
 
-		if ( ! attribute.isTypeResolved() ) {
-			attribute.resolveType( bindingContext.makeJavaType( attributeBinding.getHibernateTypeDescriptor().getJavaTypeName() ) );
+		if ( !attribute.isTypeResolved() ) {
+			attribute.resolveType(
+					bindingContext.makeJavaType(
+							attributeBinding.getHibernateTypeDescriptor()
+									.getJavaTypeName()
+					)
+			);
 		}
 	}
 
 	private void bindSingleIdGeneratedValue(EntityBinding entityBinding, String idPropertyName) {
 		AnnotationInstance generatedValueAnn = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.GENERATED_VALUE
 		);
 		if ( generatedValueAnn == null ) {
 			return;
 		}
 
 		String idName = JandexHelper.getPropertyName( generatedValueAnn.target() );
 		if ( !idPropertyName.equals( idName ) ) {
 			throw new AssertionFailure(
 					String.format(
 							"Attribute[%s.%s] with @GeneratedValue doesn't have a @Id.",
 							entityClass.getName(),
 							idPropertyName
 					)
 			);
 		}
 		String generator = JandexHelper.getValue( generatedValueAnn, "generator", String.class );
 		IdGenerator idGenerator = null;
 		if ( StringHelper.isNotEmpty( generator ) ) {
 			idGenerator = bindingContext.getMetadataImplementor().getIdGenerator( generator );
 			if ( idGenerator == null ) {
 				throw new MappingException(
 						String.format(
 								"@GeneratedValue on %s.%s referring an undefined generator [%s]",
 								entityClass.getName(),
 								idName,
 								generator
 						)
 				);
 			}
 			entityBinding.getEntityIdentifier().setIdGenerator( idGenerator );
 		}
 		GenerationType generationType = JandexHelper.getValueAsEnum(
 				generatedValueAnn,
 				"strategy",
 				GenerationType.class
 		);
 		String strategy = IdGeneratorBinder.generatorType(
 				generationType,
 				bindingContext.getMetadataImplementor().getOptions().useNewIdentifierGenerators()
 		);
 		if ( idGenerator != null && !strategy.equals( idGenerator.getStrategy() ) ) {
 			//todo how to ?
 			throw new MappingException(
 					String.format(
 							"Inconsistent Id Generation strategy of @GeneratedValue on %s.%s",
 							entityClass.getName(),
 							idName
 					)
 			);
 		}
 		if ( idGenerator == null ) {
 			idGenerator = new IdGenerator( "NAME", strategy, new HashMap<String, String>() );
 			entityBinding.getEntityIdentifier().setIdGenerator( idGenerator );
 		}
 //        entityBinding.getEntityIdentifier().createIdentifierGenerator( meta.getIdentifierGeneratorFactory() );
 	}
 
 	private void bindAttributes(EntityBinding entityBinding) {
 		// collect attribute overrides as we map the attributes
 		Map<String, AttributeOverride> attributeOverrideMap = new HashMap<String, AttributeOverride>();
 
 		// bind the attributes of this entity
 		AttributeContainer entity = entityBinding.getEntity();
 		bindAttributes( entityBinding, entity, entityClass, attributeOverrideMap );
 
 		// bind potential mapped super class attributes
 		attributeOverrideMap.putAll( entityClass.getAttributeOverrideMap() );
 		ConfiguredClass parent = entityClass.getParent();
 		Hierarchical superTypeContainer = entityBinding.getEntity().getSuperType();
 		while ( containsMappedSuperclassAttributes( parent ) ) {
 			bindAttributes( entityBinding, superTypeContainer, parent, attributeOverrideMap );
 			addNewOverridesToMap( parent, attributeOverrideMap );
 			parent = parent.getParent();
 			superTypeContainer = superTypeContainer.getSuperType();
 		}
 	}
 
 	private void addNewOverridesToMap(ConfiguredClass parent, Map<String, AttributeOverride> attributeOverrideMap) {
 		Map<String, AttributeOverride> overrides = parent.getAttributeOverrideMap();
 		for ( Map.Entry<String, AttributeOverride> entry : overrides.entrySet() ) {
 			if ( !attributeOverrideMap.containsKey( entry.getKey() ) ) {
 				attributeOverrideMap.put( entry.getKey(), entry.getValue() );
 			}
 		}
 	}
 
 	private boolean containsMappedSuperclassAttributes(ConfiguredClass parent) {
 		return parent != null && ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( parent.getConfiguredClassType() ) ||
 				ConfiguredClassType.NON_ENTITY.equals( parent.getConfiguredClassType() ) );
 	}
 
 	private void bindAttributes(
-				EntityBinding entityBinding,
-				AttributeContainer attributeContainer,
-				ConfiguredClass configuredClass,
-				Map<String,AttributeOverride> attributeOverrideMap) {
+			EntityBinding entityBinding,
+			AttributeContainer attributeContainer,
+			ConfiguredClass configuredClass,
+			Map<String, AttributeOverride> attributeOverrideMap) {
 		for ( SimpleAttribute simpleAttribute : configuredClass.getSimpleAttributes() ) {
 			String attributeName = simpleAttribute.getName();
 
 			// if there is a override apply it
 			AttributeOverride override = attributeOverrideMap.get( attributeName );
 			if ( override != null ) {
 				simpleAttribute = SimpleAttribute.createSimpleAttribute( simpleAttribute, override.getColumnValues() );
 			}
 
 			bindSingleMappedAttribute(
 					entityBinding,
 					attributeContainer,
 					simpleAttribute
 			);
 		}
 		for ( AssociationAttribute associationAttribute : configuredClass.getAssociationAttributes() ) {
 			bindAssociationAttribute(
 					entityBinding,
 					attributeContainer,
 					associationAttribute
 			);
 		}
 	}
 
 	private void bindEmbeddedAttributes(EntityBinding entityBinding) {
 		AttributeContainer entity = entityBinding.getEntity();
 		bindEmbeddedAttributes( entityBinding, entity, entityClass );
 
 		// bind potential mapped super class embeddables
 		ConfiguredClass parent = entityClass.getParent();
 		Hierarchical superTypeContainer = entityBinding.getEntity().getSuperType();
 		while ( containsMappedSuperclassAttributes( parent ) ) {
 			bindEmbeddedAttributes( entityBinding, superTypeContainer, parent );
 			parent = parent.getParent();
 			superTypeContainer = superTypeContainer.getSuperType();
 		}
 	}
 
 	private void bindEmbeddedAttributes(
-				EntityBinding entityBinding,
-				AttributeContainer attributeContainer,
-				ConfiguredClass configuredClass) {
+			EntityBinding entityBinding,
+			AttributeContainer attributeContainer,
+			ConfiguredClass configuredClass) {
 		for ( Map.Entry<String, EmbeddableClass> entry : configuredClass.getEmbeddedClasses().entrySet() ) {
 			String attributeName = entry.getKey();
 			EmbeddableClass embeddedClass = entry.getValue();
-			SingularAttribute component = attributeContainer.locateOrCreateComponentAttribute( attributeName );
+			SingularAttribute componentAttribute = attributeContainer.locateOrCreateComponentAttribute( attributeName );
+			// we have to resolve the type, if the attribute was just created
+			if ( !componentAttribute.isTypeResolved() ) {
+				Component c = new Component(
+						attributeName,
+						embeddedClass.getName(),
+						new Value<Class<?>>( embeddedClass.getConfiguredClass() ),
+						null
+				);
+				componentAttribute.resolveType( c );
+			}
 			for ( SimpleAttribute simpleAttribute : embeddedClass.getSimpleAttributes() ) {
 				bindSingleMappedAttribute(
 						entityBinding,
-						component.getAttributeContainer(),
+						componentAttribute.getAttributeContainer(),
 						simpleAttribute
 				);
 			}
 			for ( AssociationAttribute associationAttribute : embeddedClass.getAssociationAttributes() ) {
 				bindAssociationAttribute(
 						entityBinding,
-						component.getAttributeContainer(),
+						componentAttribute.getAttributeContainer(),
 						associationAttribute
 				);
 			}
 		}
 	}
 
 	private void bindAssociationAttribute(
-				EntityBinding entityBinding,
-				AttributeContainer container,
-				AssociationAttribute associationAttribute) {
+			EntityBinding entityBinding,
+			AttributeContainer container,
+			AssociationAttribute associationAttribute) {
 		switch ( associationAttribute.getAssociationType() ) {
 			case MANY_TO_ONE: {
-				Attribute attribute = entityBinding.getEntity().locateOrCreateSingularAttribute( associationAttribute.getName() );
-				ManyToOneAttributeBinding manyToOneAttributeBinding = entityBinding.makeManyToOneAttributeBinding( attribute );
+				Attribute attribute = entityBinding.getEntity()
+						.locateOrCreateSingularAttribute( associationAttribute.getName() );
+				ManyToOneAttributeBinding manyToOneAttributeBinding = entityBinding.makeManyToOneAttributeBinding(
+						attribute
+				);
 
 				ManyToOneAttributeBindingState bindingState = new ManyToOneBindingStateImpl( associationAttribute );
 				manyToOneAttributeBinding.initialize( bindingState );
 
 				ManyToOneRelationalStateImpl relationalState = new ManyToOneRelationalStateImpl();
 				if ( entityClass.hasOwnTable() ) {
 					ColumnRelationalStateImpl columnRelationsState = new ColumnRelationalStateImpl(
 							associationAttribute, bindingContext.getMetadataImplementor()
 					);
 					relationalState.addValueState( columnRelationsState );
 				}
 				manyToOneAttributeBinding.initialize( relationalState );
 				break;
 			}
 			default: {
 				// todo
 			}
 		}
 	}
 
 	private void bindSingleMappedAttribute(
-				EntityBinding entityBinding,
-				AttributeContainer container,
-				SimpleAttribute simpleAttribute) {
+			EntityBinding entityBinding,
+			AttributeContainer container,
+			SimpleAttribute simpleAttribute) {
 		if ( simpleAttribute.isId() ) {
 			return;
 		}
 
+		ColumnValues columnValues = simpleAttribute.getColumnValues();
+
 		String attributeName = simpleAttribute.getName();
 		SingularAttribute attribute = container.locateOrCreateSingularAttribute( attributeName );
 		SimpleAttributeBinding attributeBinding;
 
 		if ( simpleAttribute.isDiscriminator() ) {
 			EntityDiscriminator entityDiscriminator = entityBinding.makeEntityDiscriminator( attribute );
-			DiscriminatorBindingState bindingState = new DiscriminatorBindingStateImpl( simpleAttribute );
-			entityDiscriminator.initialize( bindingState );
+			entityDiscriminator.setDiscriminatorValue( ( (DiscriminatorColumnValues) columnValues ).getDiscriminatorValue() );
 			attributeBinding = entityDiscriminator.getValueBinding();
 		}
 		else if ( simpleAttribute.isVersioned() ) {
 			attributeBinding = entityBinding.makeVersionBinding( attribute );
-			SimpleAttributeBindingState bindingState = new AttributeBindingStateImpl( simpleAttribute );
-			attributeBinding.initialize( bindingState );
 		}
 		else {
 			attributeBinding = entityBinding.makeSimpleAttributeBinding( attribute );
-			SimpleAttributeBindingState bindingState = new AttributeBindingStateImpl( simpleAttribute );
-			attributeBinding.initialize( bindingState );
-		}
-
-		if ( entityClass.hasOwnTable() ) {
-			ColumnRelationalStateImpl columnRelationsState = new ColumnRelationalStateImpl(
-					simpleAttribute, bindingContext.getMetadataImplementor()
-			);
-//			TupleRelationalStateImpl relationalState = new TupleRelationalStateImpl();
-//			relationalState.addValueState( columnRelationsState );
-//
-//			attributeBinding.initialize( relationalState );
-			attributeBinding.initialize( columnRelationsState );
 		}
 
-		if ( ! attribute.isTypeResolved() ) {
-			attribute.resolveType( bindingContext.makeJavaType( attributeBinding.getHibernateTypeDescriptor().getJavaTypeName() ) );
-		}
+		attributeBinding.setInsertable( simpleAttribute.isInsertable() );
+		attributeBinding.setUpdatable( simpleAttribute.isUpdatable() );
+		attributeBinding.setGeneration( simpleAttribute.getPropertyGeneration() );
+		attributeBinding.setLazy( simpleAttribute.isLazy() );
+		attributeBinding.setIncludedInOptimisticLocking( simpleAttribute.isOptimisticLockable() );
+
+//		attributeBinding.setPropertyAccessorName(
+//				Helper.getPropertyAccessorName(
+//						simpleAttribute.getPropertyAccessorName(),
+//						false,
+//						bindingContext.getMappingDefaults().getPropertyAccessorName()
+//				)
+//		);
+
+		final TableSpecification valueSource = attributeBinding.getEntityBinding().getBaseTable();
+		String columnName = simpleAttribute.getColumnValues()
+				.getName()
+				.isEmpty() ? attribute.getName() : simpleAttribute.getColumnValues().getName();
+		Column column = valueSource.locateOrCreateColumn( columnName );
+		column.setNullable( columnValues.isNullable() );
+		column.setDefaultValue( null ); // todo
+		column.setSqlType( null ); // todo
+		Size size = new Size(
+				columnValues.getPrecision(),
+				columnValues.getScale(),
+				columnValues.getLength(),
+				Size.LobMultiplier.NONE
+		);
+		column.setSize( size );
+		column.setDatatype( null ); // todo : ???
+		column.setReadFragment( simpleAttribute.getCustomReadFragment() );
+		column.setWriteFragment( simpleAttribute.getCustomWriteFragment() );
+		column.setUnique( columnValues.isUnique() );
+		column.setCheckCondition( simpleAttribute.getCheckCondition() );
+		column.setComment( null ); // todo
+
+		attributeBinding.setValue( column );
+
+
+//		if ( ! attribute.isTypeResolved() ) {
+//			attribute.resolveType( bindingContext.makeJavaType( attributeBinding.getHibernateTypeDescriptor().getJavaTypeName() ) );
+//		}
 	}
-
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
index 782673e3e3..fd969795b3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
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
 package org.hibernate.metamodel.source.annotations.entity;
 
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.AccessType;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.MappingException;
-import org.hibernate.metamodel.source.annotations.AnnotationsBindingContext;
+import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.binding.InheritanceType;
 
 /**
  * Represents an entity or mapped superclass configured via annotations/xml.
  *
  * @author Hardy Ferentschik
  */
 public class EntityClass extends ConfiguredClass {
 	private final AccessType hierarchyAccessType;
 	private final InheritanceType inheritanceType;
 	private final boolean hasOwnTable;
 	private final String entityBasedTableName;
 	private final IdType idType;
 	private final EntityClass jpaEntityParent;
 
 	public EntityClass(
 			ClassInfo classInfo,
 			EntityClass parent,
 			AccessType hierarchyAccessType,
 			InheritanceType inheritanceType,
-			AnnotationsBindingContext context) {
+			AnnotationBindingContext context) {
 		super( classInfo, hierarchyAccessType, parent, context );
 		this.hierarchyAccessType = hierarchyAccessType;
 		this.inheritanceType = inheritanceType;
 		this.idType = determineIdType();
 		this.jpaEntityParent = findJpaEntitySuperClass();
 		this.hasOwnTable = definesItsOwnTable();
 		this.entityBasedTableName = determineEntityBasedTableName();
 	}
 
 	/**
 	 * @return Returns the next JPA super entity for this entity class or {@code null} in case there is none.
 	 */
 	public EntityClass getEntityParent() {
 		return jpaEntityParent;
 	}
 
 	/**
 	 * @return Returns {@code true} is this entity class is the root of the class hierarchy in the JPA sense, which
 	 *         means there are no more super classes which are annotated with @Entity. There can, however, be mapped superclasses
 	 *         or non entities in the actual java type hierarchy.
 	 */
 	public boolean isEntityRoot() {
 		return jpaEntityParent == null;
 	}
 
 	public InheritanceType getInheritanceType() {
 		return inheritanceType;
 	}
 
 	public IdType getIdType() {
 		return idType;
 	}
 
 	public boolean hasOwnTable() {
 		return hasOwnTable;
 	}
 
 	public String getClassNameForTable() {
 		return entityBasedTableName;
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "EntityClass" );
 		sb.append( "{name=" ).append( getConfiguredClass().getSimpleName() );
 		sb.append( ", hierarchyAccessType=" ).append( hierarchyAccessType );
 		sb.append( ", inheritanceType=" ).append( inheritanceType );
 		sb.append( ", hasOwnTable=" ).append( hasOwnTable );
 		sb.append( ", primaryTableName='" ).append( entityBasedTableName ).append( '\'' );
 		sb.append( ", idType=" ).append( idType );
 		sb.append( '}' );
 		return sb.toString();
 	}
 
 	private boolean definesItsOwnTable() {
 		// mapped super classes don't have their own tables
 		if ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( getConfiguredClassType() ) ) {
 			return false;
 		}
 
 		if ( InheritanceType.SINGLE_TABLE.equals( inheritanceType ) ) {
 			return isEntityRoot();
 		}
 		return true;
 	}
 
 	private EntityClass findJpaEntitySuperClass() {
 		ConfiguredClass tmpConfiguredClass = this.getParent();
 		while ( tmpConfiguredClass != null ) {
 			if ( ConfiguredClassType.ENTITY.equals( tmpConfiguredClass.getConfiguredClassType() ) ) {
 				return (EntityClass) tmpConfiguredClass;
 			}
 			tmpConfiguredClass = tmpConfiguredClass.getParent();
 		}
 		return null;
 	}
 
 	private String determineEntityBasedTableName() {
 		String tableName = null;
 		if ( hasOwnTable() ) {
 			tableName = getConfiguredClass().getSimpleName();
 		}
 		else if ( jpaEntityParent != null ) {
 			tableName = jpaEntityParent.getClassNameForTable();
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinder.java
index 1c0e5d5617..7b5bb19b5c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinder.java
@@ -1,93 +1,98 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
 import java.util.HashSet;
+import java.util.List;
 import java.util.Set;
 
 import org.jboss.jandex.AnnotationInstance;
-import org.jboss.jandex.Index;
 
 import org.hibernate.MappingException;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.FetchProfiles;
-import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.FetchProfile.Fetch;
+import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
+import org.hibernate.metamodel.source.annotations.JandexHelper;
 
 /**
  * Binds fetch profiles found in annotations.
  *
  * @author Hardy Ferentschik
  */
 public class FetchProfileBinder {
 
+	private FetchProfileBinder() {
+	}
+
 	/**
 	 * Binds all {@link FetchProfiles} and {@link org.hibernate.annotations.FetchProfile} annotations to the supplied metadata.
 	 *
-	 * @param metadata the global metadata
-	 * @param jandex the jandex index
+	 * @param bindingContext the context for annotation binding
 	 */
 	// TODO verify that association exists. See former VerifyFetchProfileReferenceSecondPass
-	public static void bind(MetadataImplementor metadata, Index jandex) {
-		for ( AnnotationInstance fetchProfile : jandex.getAnnotations( HibernateDotNames.FETCH_PROFILE ) ) {
-			bind( metadata, fetchProfile );
+	public static void bind(AnnotationBindingContext bindingContext) {
+
+		List<AnnotationInstance> annotations = bindingContext.getIndex()
+				.getAnnotations( HibernateDotNames.FETCH_PROFILE );
+		for ( AnnotationInstance fetchProfile : annotations ) {
+			bind( bindingContext.getMetadataImplementor(), fetchProfile );
 		}
-		for ( AnnotationInstance fetchProfiles : jandex.getAnnotations( HibernateDotNames.FETCH_PROFILES ) ) {
+
+		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.FETCH_PROFILES );
+		for ( AnnotationInstance fetchProfiles : annotations ) {
 			AnnotationInstance[] fetchProfileAnnotations = JandexHelper.getValue(
 					fetchProfiles,
 					"value",
 					AnnotationInstance[].class
 			);
 			for ( AnnotationInstance fetchProfile : fetchProfileAnnotations ) {
-				bind( metadata, fetchProfile );
+				bind( bindingContext.getMetadataImplementor(), fetchProfile );
 			}
 		}
 	}
 
 	private static void bind(MetadataImplementor metadata, AnnotationInstance fetchProfile) {
 		String name = JandexHelper.getValue( fetchProfile, "name", String.class );
 		Set<Fetch> fetches = new HashSet<Fetch>();
 		AnnotationInstance[] overrideAnnotations = JandexHelper.getValue(
 				fetchProfile,
 				"fetchOverrides",
 				AnnotationInstance[].class
 		);
 		for ( AnnotationInstance override : overrideAnnotations ) {
 			FetchMode fetchMode = JandexHelper.getValueAsEnum( override, "mode", FetchMode.class );
 			if ( !fetchMode.equals( org.hibernate.annotations.FetchMode.JOIN ) ) {
 				throw new MappingException( "Only FetchMode.JOIN is currently supported" );
 			}
 			final String entityName = JandexHelper.getValue( override, "entity", String.class );
 			final String associationName = JandexHelper.getValue( override, "association", String.class );
 			fetches.add( new Fetch( entityName, associationName, fetchMode.toString().toLowerCase() ) );
 		}
 		metadata.addFetchProfile( new FetchProfile( name, fetches ) );
 	}
-
-	private FetchProfileBinder() {
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FilterDefBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FilterDefBinder.java
index 2e83b3af10..b4418b4089 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FilterDefBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FilterDefBinder.java
@@ -1,92 +1,100 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
 import java.util.HashMap;
+import java.util.List;
 import java.util.Map;
 
 import org.jboss.jandex.AnnotationInstance;
-import org.jboss.jandex.Index;
 import org.jboss.logging.Logger;
 
 import org.hibernate.annotations.FilterDef;
 import org.hibernate.annotations.FilterDefs;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.type.Type;
 
+/**
+ * Binds {@link FilterDefs} and {@link FilterDef} annotations.
+ *
+ * @author Hardy Ferentschik
+ */
 public class FilterDefBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			FilterDefBinder.class.getName()
 	);
 
 	/**
 	 * Binds all {@link FilterDefs} and {@link FilterDef} annotations to the supplied metadata.
 	 *
-	 * @param metadata the global metadata
-	 * @param jandex the jandex index
+	 * @param bindingContext the context for annotation binding
 	 */
-	public static void bind(MetadataImplementor metadata, Index jandex) {
-		for ( AnnotationInstance filterDef : jandex.getAnnotations( HibernateDotNames.FILTER_DEF ) ) {
-			bind( metadata, filterDef );
+	public static void bind(AnnotationBindingContext bindingContext) {
+		List<AnnotationInstance> annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.FILTER_DEF );
+		for ( AnnotationInstance filterDef : annotations ) {
+			bind( bindingContext.getMetadataImplementor(), filterDef );
 		}
-		for ( AnnotationInstance filterDefs : jandex.getAnnotations( HibernateDotNames.FILTER_DEFS ) ) {
+
+		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.FILTER_DEFS );
+		for ( AnnotationInstance filterDefs : annotations ) {
 			AnnotationInstance[] filterDefAnnotations = JandexHelper.getValue(
 					filterDefs,
 					"value",
 					AnnotationInstance[].class
 			);
 			for ( AnnotationInstance filterDef : filterDefAnnotations ) {
-				bind( metadata, filterDef );
+				bind( bindingContext.getMetadataImplementor(), filterDef );
 			}
 		}
 	}
 
 	private static void bind(MetadataImplementor metadata, AnnotationInstance filterDef) {
 		String name = JandexHelper.getValue( filterDef, "name", String.class );
 		Map<String, Type> prms = new HashMap<String, Type>();
 		for ( AnnotationInstance prm : JandexHelper.getValue( filterDef, "parameters", AnnotationInstance[].class ) ) {
 			prms.put(
 					JandexHelper.getValue( prm, "name", String.class ),
 					metadata.getTypeResolver().heuristicType( JandexHelper.getValue( prm, "type", String.class ) )
 			);
 		}
 		metadata.addFilterDefinition(
 				new FilterDefinition(
 						name,
 						JandexHelper.getValue( filterDef, "defaultCondition", String.class ),
 						prms
 				)
 		);
 		LOG.debugf( "Binding filter definition: %s", name );
 	}
 
 	private FilterDefBinder() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/IdGeneratorBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/IdGeneratorBinder.java
index 15c32a3b6b..0ed792fc38 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/IdGeneratorBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/IdGeneratorBinder.java
@@ -1,216 +1,230 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
 import java.util.HashMap;
+import java.util.List;
 import java.util.Map;
 import javax.persistence.GenerationType;
 import javax.persistence.SequenceGenerator;
 
 import org.jboss.jandex.AnnotationInstance;
-import org.jboss.jandex.Index;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.GenericGenerator;
 import org.hibernate.annotations.GenericGenerators;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.id.MultipleHiLoPerTableGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.SequenceHiLoGenerator;
 import org.hibernate.id.TableHiLoGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.id.enhanced.TableGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
+import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
+import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
-import org.hibernate.metamodel.binding.IdGenerator;
-import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 
+/**
+ * Binds {@link SequenceGenerator}, {@link javax.persistence.TableGenerator}, {@link GenericGenerator}, and
+ * {@link GenericGenerators} annotations.
+ *
+ * @author Hardy Ferentschik
+ */
 public class IdGeneratorBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			IdGeneratorBinder.class.getName()
 	);
 
 	private IdGeneratorBinder() {
 	}
 
-	private static void addStringParameter(AnnotationInstance annotation,
-										   String element,
-										   Map<String, String> parameters,
-										   String parameter) {
-		String string = JandexHelper.getValue( annotation, element, String.class );
-		if ( StringHelper.isNotEmpty( string ) ) {
-			parameters.put( parameter, string );
-		}
-	}
-
 	/**
-	 * Binds all {@link SequenceGenerator}, {@link javax.persistence.TableGenerator}, {@link GenericGenerator}, and {
+	 * Binds all {@link SequenceGenerator}, {@link javax.persistence.TableGenerator}, {@link GenericGenerator}, and
 	 * {@link GenericGenerators} annotations to the supplied metadata.
 	 *
-	 * @param metadata the global metadata
-	 * @param jandex the jandex index
+	 * @param bindingContext the context for annotation binding
 	 */
-	public static void bind(MetadataImplementor metadata, Index jandex) {
-		for ( AnnotationInstance generator : jandex.getAnnotations( JPADotNames.SEQUENCE_GENERATOR ) ) {
-			bindSequenceGenerator( metadata, generator );
+	public static void bind(AnnotationBindingContext bindingContext) {
+		List<AnnotationInstance> annotations = bindingContext.getIndex()
+				.getAnnotations( JPADotNames.SEQUENCE_GENERATOR );
+		for ( AnnotationInstance generator : annotations ) {
+			bindSequenceGenerator( bindingContext.getMetadataImplementor(), generator );
 		}
-		for ( AnnotationInstance generator : jandex.getAnnotations( JPADotNames.TABLE_GENERATOR ) ) {
-			bindTableGenerator( metadata, generator );
+
+		annotations = bindingContext.getIndex().getAnnotations( JPADotNames.TABLE_GENERATOR );
+		for ( AnnotationInstance generator : annotations ) {
+			bindTableGenerator( bindingContext.getMetadataImplementor(), generator );
 		}
-		for ( AnnotationInstance generator : jandex.getAnnotations( HibernateDotNames.GENERIC_GENERATOR ) ) {
-			bindGenericGenerator( metadata, generator );
+
+		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.GENERIC_GENERATOR );
+		for ( AnnotationInstance generator : annotations ) {
+			bindGenericGenerator( bindingContext.getMetadataImplementor(), generator );
 		}
-		for ( AnnotationInstance generators : jandex.getAnnotations( HibernateDotNames.GENERIC_GENERATORS ) ) {
+
+		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.GENERIC_GENERATORS );
+		for ( AnnotationInstance generators : annotations ) {
 			for ( AnnotationInstance generator : JandexHelper.getValue(
 					generators,
 					"value",
 					AnnotationInstance[].class
 			) ) {
-				bindGenericGenerator( metadata, generator );
+				bindGenericGenerator( bindingContext.getMetadataImplementor(), generator );
 			}
 		}
 	}
 
+	private static void addStringParameter(AnnotationInstance annotation,
+										   String element,
+										   Map<String, String> parameters,
+										   String parameter) {
+		String string = JandexHelper.getValue( annotation, element, String.class );
+		if ( StringHelper.isNotEmpty( string ) ) {
+			parameters.put( parameter, string );
+		}
+	}
+
 	private static void bindGenericGenerator(MetadataImplementor metadata, AnnotationInstance generator) {
 		String name = JandexHelper.getValue( generator, "name", String.class );
 		Map<String, String> parameterMap = new HashMap<String, String>();
 		AnnotationInstance[] parameterAnnotations = JandexHelper.getValue(
 				generator,
 				"parameters",
 				AnnotationInstance[].class
 		);
 		for ( AnnotationInstance parameterAnnotation : parameterAnnotations ) {
 			parameterMap.put(
 					JandexHelper.getValue( parameterAnnotation, "name", String.class ),
 					JandexHelper.getValue( parameterAnnotation, "value", String.class )
 			);
 		}
 		metadata.addIdGenerator(
 				new IdGenerator(
 						name,
 						JandexHelper.getValue( generator, "strategy", String.class ),
 						parameterMap
 				)
 		);
 		LOG.tracef( "Add generic generator with name: %s", name );
 	}
 
 	private static void bindSequenceGenerator(MetadataImplementor metadata, AnnotationInstance generator) {
 		String name = JandexHelper.getValue( generator, "name", String.class );
 		String strategy;
 		Map<String, String> prms = new HashMap<String, String>();
 		addStringParameter( generator, "sequenceName", prms, SequenceStyleGenerator.SEQUENCE_PARAM );
 		boolean useNewIdentifierGenerators = metadata.getOptions().useNewIdentifierGenerators();
 		strategy = generatorType( GenerationType.SEQUENCE, useNewIdentifierGenerators );
 		if ( useNewIdentifierGenerators ) {
 			addStringParameter( generator, "catalog", prms, PersistentIdentifierGenerator.CATALOG );
 			addStringParameter( generator, "schema", prms, PersistentIdentifierGenerator.SCHEMA );
 			prms.put(
 					SequenceStyleGenerator.INCREMENT_PARAM,
 					String.valueOf( JandexHelper.getValue( generator, "allocationSize", Integer.class ) )
 			);
 			prms.put(
 					SequenceStyleGenerator.INITIAL_PARAM,
 					String.valueOf( JandexHelper.getValue( generator, "initialValue", Integer.class ) )
 			);
 		}
 		else {
 			if ( JandexHelper.getValue( generator, "initialValue", Integer.class ) != 1 ) {
 				LOG.unsupportedInitialValue( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS );
 			}
 			prms.put(
 					SequenceHiLoGenerator.MAX_LO,
 					String.valueOf( JandexHelper.getValue( generator, "allocationSize", Integer.class ) - 1 )
 			);
 		}
 		metadata.addIdGenerator( new IdGenerator( name, strategy, prms ) );
 		LOG.tracef( "Add sequence generator with name: %s", name );
 	}
 
 	private static void bindTableGenerator(MetadataImplementor metadata, AnnotationInstance generator) {
 		String name = JandexHelper.getValue( generator, "name", String.class );
 		String strategy;
 		Map<String, String> prms = new HashMap<String, String>();
 		addStringParameter( generator, "catalog", prms, PersistentIdentifierGenerator.CATALOG );
 		addStringParameter( generator, "schema", prms, PersistentIdentifierGenerator.SCHEMA );
 		boolean useNewIdentifierGenerators = metadata.getOptions().useNewIdentifierGenerators();
 		strategy = generatorType( GenerationType.TABLE, useNewIdentifierGenerators );
 		if ( useNewIdentifierGenerators ) {
 			prms.put( TableGenerator.CONFIG_PREFER_SEGMENT_PER_ENTITY, "true" );
 			addStringParameter( generator, "table", prms, TableGenerator.TABLE_PARAM );
 			addStringParameter( generator, "pkColumnName", prms, TableGenerator.SEGMENT_COLUMN_PARAM );
 			addStringParameter( generator, "pkColumnValue", prms, TableGenerator.SEGMENT_VALUE_PARAM );
 			addStringParameter( generator, "valueColumnName", prms, TableGenerator.VALUE_COLUMN_PARAM );
 			prms.put(
 					TableGenerator.INCREMENT_PARAM,
 					String.valueOf( JandexHelper.getValue( generator, "allocationSize", String.class ) )
 			);
 			prms.put(
 					TableGenerator.INITIAL_PARAM,
 					String.valueOf( JandexHelper.getValue( generator, "initialValue", String.class ) + 1 )
 			);
 		}
 		else {
 			addStringParameter( generator, "table", prms, MultipleHiLoPerTableGenerator.ID_TABLE );
 			addStringParameter( generator, "pkColumnName", prms, MultipleHiLoPerTableGenerator.PK_COLUMN_NAME );
 			addStringParameter( generator, "pkColumnValue", prms, MultipleHiLoPerTableGenerator.PK_VALUE_NAME );
 			addStringParameter( generator, "valueColumnName", prms, MultipleHiLoPerTableGenerator.VALUE_COLUMN_NAME );
 			prms.put(
 					TableHiLoGenerator.MAX_LO,
 					String.valueOf( JandexHelper.getValue( generator, "allocationSize", Integer.class ) - 1 )
 			);
 		}
 		if ( JandexHelper.getValue( generator, "uniqueConstraints", AnnotationInstance[].class ).length > 0 ) {
 			LOG.ignoringTableGeneratorConstraints( name );
 		}
 		metadata.addIdGenerator( new IdGenerator( name, strategy, prms ) );
 		LOG.tracef( "Add table generator with name: %s", name );
 	}
 
 	public static String generatorType(GenerationType generatorEnum, boolean useNewGeneratorMappings) {
 		switch ( generatorEnum ) {
 			case IDENTITY:
 				return "identity";
 			case AUTO:
 				return useNewGeneratorMappings
 						? "enhanced-sequence"
 						: "native";
 			case TABLE:
 				return useNewGeneratorMappings
 						? "enhanced-table"
 						: MultipleHiLoPerTableGenerator.class.getName();
 			case SEQUENCE:
 				return useNewGeneratorMappings
 						? "enhanced-sequence"
 						: "seqhilo";
 		}
 		throw new AssertionFailure( "Unknown GeneratorType: " + generatorEnum );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java
index bd0fef9bcd..e1aa54a4a9 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java
@@ -1,326 +1,348 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
 import java.util.HashMap;
+import java.util.List;
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.NamedQueries;
 import javax.persistence.NamedQuery;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
-import org.jboss.jandex.Index;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.LockMode;
 import org.hibernate.annotations.QueryHints;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 
+/**
+ * Binds {@link NamedQuery}, {@link NamedQueries}, {@link NamedNativeQuery}, {@link NamedNativeQueries},
+ * {@link org.hibernate.annotations.NamedQuery}, {@link org.hibernate.annotations.NamedQueries},
+ * {@link org.hibernate.annotations.NamedNativeQuery}, and {@link org.hibernate.annotations.NamedNativeQueries}.
+ *
+ * @author Hardy Ferentschik
+ */
 public class QueryBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			QueryBinder.class.getName()
 	);
 
 	private QueryBinder() {
 	}
 
 	/**
 	 * Binds all {@link NamedQuery}, {@link NamedQueries}, {@link NamedNativeQuery}, {@link NamedNativeQueries},
 	 * {@link org.hibernate.annotations.NamedQuery}, {@link org.hibernate.annotations.NamedQueries},
 	 * {@link org.hibernate.annotations.NamedNativeQuery}, and {@link org.hibernate.annotations.NamedNativeQueries}
 	 * annotations to the supplied metadata.
 	 *
-	 * @param metadata the global metadata
-	 * @param jandex the jandex index
+	 * @param bindingContext the context for annotation binding
 	 */
-	public static void bind(MetadataImplementor metadata, Index jandex) {
-		for ( AnnotationInstance query : jandex.getAnnotations( JPADotNames.NAMED_QUERY ) ) {
-			bindNamedQuery( metadata, query );
+	public static void bind(AnnotationBindingContext bindingContext) {
+		List<AnnotationInstance> annotations = bindingContext.getIndex().getAnnotations( JPADotNames.NAMED_QUERY );
+		for ( AnnotationInstance query : annotations ) {
+			bindNamedQuery( bindingContext.getMetadataImplementor(), query );
 		}
-		for ( AnnotationInstance queries : jandex.getAnnotations( JPADotNames.NAMED_QUERIES ) ) {
+
+		annotations = bindingContext.getIndex().getAnnotations( JPADotNames.NAMED_QUERIES );
+		for ( AnnotationInstance queries : annotations ) {
 			for ( AnnotationInstance query : JandexHelper.getValue( queries, "value", AnnotationInstance[].class ) ) {
-				bindNamedQuery( metadata, query );
+				bindNamedQuery( bindingContext.getMetadataImplementor(), query );
 			}
 		}
-		for ( AnnotationInstance query : jandex.getAnnotations( JPADotNames.NAMED_NATIVE_QUERY ) ) {
-			bindNamedNativeQuery( metadata, query );
+
+		annotations = bindingContext.getIndex().getAnnotations( JPADotNames.NAMED_NATIVE_QUERY );
+		for ( AnnotationInstance query : annotations ) {
+			bindNamedNativeQuery( bindingContext.getMetadataImplementor(), query );
 		}
-		for ( AnnotationInstance queries : jandex.getAnnotations( JPADotNames.NAMED_NATIVE_QUERIES ) ) {
+
+		annotations = bindingContext.getIndex().getAnnotations( JPADotNames.NAMED_NATIVE_QUERIES );
+		for ( AnnotationInstance queries : annotations ) {
 			for ( AnnotationInstance query : JandexHelper.getValue( queries, "value", AnnotationInstance[].class ) ) {
-				bindNamedNativeQuery( metadata, query );
+				bindNamedNativeQuery( bindingContext.getMetadataImplementor(), query );
 			}
 		}
-		for ( AnnotationInstance query : jandex.getAnnotations( HibernateDotNames.NAMED_QUERY ) ) {
-			bindNamedQuery( metadata, query );
+
+		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.NAMED_QUERY );
+		for ( AnnotationInstance query : annotations ) {
+			bindNamedQuery( bindingContext.getMetadataImplementor(), query );
 		}
-		for ( AnnotationInstance queries : jandex.getAnnotations( HibernateDotNames.NAMED_QUERIES ) ) {
+
+		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.NAMED_QUERIES );
+		for ( AnnotationInstance queries : annotations ) {
 			for ( AnnotationInstance query : JandexHelper.getValue( queries, "value", AnnotationInstance[].class ) ) {
-				bindNamedQuery( metadata, query );
+				bindNamedQuery( bindingContext.getMetadataImplementor(), query );
 			}
 		}
-		for ( AnnotationInstance query : jandex.getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERY ) ) {
-			bindNamedNativeQuery( metadata, query );
+
+		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERY );
+		for ( AnnotationInstance query : annotations ) {
+			bindNamedNativeQuery( bindingContext.getMetadataImplementor(), query );
 		}
-		for ( AnnotationInstance queries : jandex.getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERIES ) ) {
+
+		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERIES );
+		for ( AnnotationInstance queries : annotations ) {
 			for ( AnnotationInstance query : JandexHelper.getValue( queries, "value", AnnotationInstance[].class ) ) {
-				bindNamedNativeQuery( metadata, query );
+				bindNamedNativeQuery( bindingContext.getMetadataImplementor(), query );
 			}
 		}
 	}
 
 	/**
 	 * Binds {@link javax.persistence.NamedQuery} as well as {@link org.hibernate.annotations.NamedQuery}.
 	 *
 	 * @param metadata the current metadata
 	 * @param annotation the named query annotation
 	 */
 	private static void bindNamedQuery(MetadataImplementor metadata, AnnotationInstance annotation) {
 		String name = JandexHelper.getValue( annotation, "name", String.class );
 		if ( StringHelper.isEmpty( name ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 
 		String query = JandexHelper.getValue( annotation, "query", String.class );
 
 		AnnotationInstance[] hints = JandexHelper.getValue( annotation, "hints", AnnotationInstance[].class );
 
 		String cacheRegion = getString( hints, QueryHints.CACHE_REGION );
 		if ( StringHelper.isEmpty( cacheRegion ) ) {
 			cacheRegion = null;
 		}
 
 		Integer timeout = getTimeout( hints, query );
 		if ( timeout != null && timeout < 0 ) {
 			timeout = null;
 		}
 
 		Integer fetchSize = getInteger( hints, QueryHints.FETCH_SIZE, name );
 		if ( fetchSize != null && fetchSize < 0 ) {
 			fetchSize = null;
 		}
 
 		String comment = getString( hints, QueryHints.COMMENT );
 		if ( StringHelper.isEmpty( comment ) ) {
 			comment = null;
 		}
 
 		metadata.addNamedQuery(
 				new NamedQueryDefinition(
 						name,
 						query, getBoolean( hints, QueryHints.CACHEABLE, name ), cacheRegion,
 						timeout, fetchSize, getFlushMode( hints, QueryHints.FLUSH_MODE, name ),
 						getCacheMode( hints, QueryHints.CACHE_MODE, name ),
 						getBoolean( hints, QueryHints.READ_ONLY, name ), comment, null
 				)
 		);
 		LOG.debugf( "Binding named query: %s => %s", name, query );
 	}
 
 	private static void bindNamedNativeQuery(MetadataImplementor metadata, AnnotationInstance annotation) {
 		String name = JandexHelper.getValue( annotation, "name", String.class );
 		if ( StringHelper.isEmpty( name ) ) {
 			throw new AnnotationException( "A named native query must have a name when used in class or package level" );
 		}
 
 		String query = JandexHelper.getValue( annotation, "query", String.class );
 
 		String resultSetMapping = JandexHelper.getValue( annotation, "resultSetMapping", String.class );
 
 		AnnotationInstance[] hints = JandexHelper.getValue( annotation, "hints", AnnotationInstance[].class );
 
 		boolean cacheable = getBoolean( hints, "org.hibernate.cacheable", name );
 		String cacheRegion = getString( hints, QueryHints.CACHE_REGION );
 		if ( StringHelper.isEmpty( cacheRegion ) ) {
 			cacheRegion = null;
 		}
 
 		Integer timeout = getTimeout( hints, query );
 		if ( timeout != null && timeout < 0 ) {
 			timeout = null;
 		}
 
 		Integer fetchSize = getInteger( hints, QueryHints.FETCH_SIZE, name );
 		if ( fetchSize != null && fetchSize < 0 ) {
 			fetchSize = null;
 		}
 
 		FlushMode flushMode = getFlushMode( hints, QueryHints.FLUSH_MODE, name );
 		CacheMode cacheMode = getCacheMode( hints, QueryHints.CACHE_MODE, name );
 
 		boolean readOnly = getBoolean( hints, QueryHints.READ_ONLY, name );
 
 		String comment = getString( hints, QueryHints.COMMENT );
 		if ( StringHelper.isEmpty( comment ) ) {
 			comment = null;
 		}
 
 		boolean callable = getBoolean( hints, QueryHints.CALLABLE, name );
 		NamedSQLQueryDefinition def;
 		if ( StringHelper.isNotEmpty( resultSetMapping ) ) {
 			def = new NamedSQLQueryDefinition(
 					name,
 					query, resultSetMapping, null, cacheable,
 					cacheRegion, timeout, fetchSize,
 					flushMode, cacheMode, readOnly, comment,
 					null, callable
 			);
 		}
 		else {
 			AnnotationValue annotationValue = annotation.value( "resultClass" );
 			if ( annotationValue == null ) {
 				throw new NotYetImplementedException( "Pure native scalar queries are not yet supported" );
 			}
 			NativeSQLQueryRootReturn queryRoots[] = new NativeSQLQueryRootReturn[] {
 					new NativeSQLQueryRootReturn(
 							"alias1",
 							annotationValue.asString(),
 							new HashMap<String, String[]>(),
 							LockMode.READ
 					)
 			};
 			def = new NamedSQLQueryDefinition(
 					name,
 					query,
 					queryRoots,
 					null,
 					cacheable,
 					cacheRegion,
 					timeout,
 					fetchSize,
 					flushMode,
 					cacheMode,
 					readOnly,
 					comment,
 					null,
 					callable
 			);
 		}
 		metadata.addNamedNativeQuery( def );
 		LOG.debugf( "Binding named native query: %s => %s", name, query );
 	}
 
 	private static boolean getBoolean(AnnotationInstance[] hints, String element, String query) {
 		String val = getString( hints, element );
 		if ( val == null || val.equalsIgnoreCase( "false" ) ) {
 			return false;
 		}
 		if ( val.equalsIgnoreCase( "true" ) ) {
 			return true;
 		}
 		throw new AnnotationException( "Not a boolean in hint: " + query + ":" + element );
 	}
 
 	private static CacheMode getCacheMode(AnnotationInstance[] hints, String element, String query) {
 		String val = getString( hints, element );
 		if ( val == null ) {
 			return null;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.GET.toString() ) ) {
 			return CacheMode.GET;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.IGNORE.toString() ) ) {
 			return CacheMode.IGNORE;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.NORMAL.toString() ) ) {
 			return CacheMode.NORMAL;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.PUT.toString() ) ) {
 			return CacheMode.PUT;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.REFRESH.toString() ) ) {
 			return CacheMode.REFRESH;
 		}
 		throw new AnnotationException( "Unknown CacheMode in hint: " + query + ":" + element );
 	}
 
 	private static FlushMode getFlushMode(AnnotationInstance[] hints, String element, String query) {
 		String val = getString( hints, element );
 		if ( val == null ) {
 			return null;
 		}
 		if ( val.equalsIgnoreCase( FlushMode.ALWAYS.toString() ) ) {
 			return FlushMode.ALWAYS;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.AUTO.toString() ) ) {
 			return FlushMode.AUTO;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.COMMIT.toString() ) ) {
 			return FlushMode.COMMIT;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.NEVER.toString() ) ) {
 			return FlushMode.MANUAL;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.MANUAL.toString() ) ) {
 			return FlushMode.MANUAL;
 		}
 		else {
 			throw new AnnotationException( "Unknown FlushMode in hint: " + query + ":" + element );
 		}
 	}
 
 	private static Integer getInteger(AnnotationInstance[] hints, String element, String query) {
 		String val = getString( hints, element );
 		if ( val == null ) {
 			return null;
 		}
 		try {
 			return Integer.decode( val );
 		}
 		catch ( NumberFormatException nfe ) {
 			throw new AnnotationException( "Not an integer in hint: " + query + ":" + element, nfe );
 		}
 	}
 
 	private static String getString(AnnotationInstance[] hints, String element) {
 		for ( AnnotationInstance hint : hints ) {
 			if ( element.equals( JandexHelper.getValue( hint, "name", String.class ) ) ) {
 				return JandexHelper.getValue( hint, "value", String.class );
 			}
 		}
 		return null;
 	}
 
 	private static Integer getTimeout(AnnotationInstance[] hints, String query) {
 		Integer timeout = getInteger( hints, QueryHints.TIMEOUT_JPA, query );
 		if ( timeout == null ) {
 			return getInteger( hints, QueryHints.TIMEOUT_HIBERNATE, query ); // timeout is already in seconds
 		}
 		return ( ( timeout + 500 ) / 1000 ); // convert milliseconds to seconds (rounded)
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TableBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TableBinder.java
index 87e3bf86ed..433918b222 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TableBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TableBinder.java
@@ -1,126 +1,130 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
+import java.util.List;
+
 import org.jboss.jandex.AnnotationInstance;
-import org.jboss.jandex.Index;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.ObjectName;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Table;
+import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
+import org.hibernate.metamodel.source.annotations.JandexHelper;
 
 /**
  * Binds table related information. This binder is called after the entities are bound.
  *
  * @author Hardy Ferentschik
  */
 public class TableBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			TableBinder.class.getName()
 	);
 
+	private TableBinder() {
+	}
+
 	/**
 	 * Binds {@link org.hibernate.annotations.Tables} and {@link org.hibernate.annotations.Table} annotations to the supplied
 	 * metadata.
 	 *
-	 * @param metadata the global metadata
-	 * @param jandex the annotation index repository
+	 * @param bindingContext the context for annotation binding
 	 */
-	public static void bind(MetadataImplementor metadata, Index jandex) {
-		for ( AnnotationInstance tableAnnotation : jandex.getAnnotations( HibernateDotNames.TABLE ) ) {
-			bind( metadata, tableAnnotation );
+	public static void bind(AnnotationBindingContext bindingContext) {
+		List<AnnotationInstance> annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.TABLE );
+		for ( AnnotationInstance tableAnnotation : annotations ) {
+			bind( bindingContext.getMetadataImplementor(), tableAnnotation );
 		}
-		for ( AnnotationInstance tables : jandex.getAnnotations( HibernateDotNames.TABLES ) ) {
+
+		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.TABLES );
+		for ( AnnotationInstance tables : annotations ) {
 			for ( AnnotationInstance table : JandexHelper.getValue( tables, "value", AnnotationInstance[].class ) ) {
-				bind( metadata, table );
+				bind( bindingContext.getMetadataImplementor(), table );
 			}
 		}
 	}
 
 	private static void bind(MetadataImplementor metadata, AnnotationInstance tableAnnotation) {
 		String tableName = JandexHelper.getValue( tableAnnotation, "appliesTo", String.class );
 		ObjectName objectName = new ObjectName( tableName );
 		Schema schema = metadata.getDatabase().getSchema( objectName.getSchema(), objectName.getCatalog() );
 		Table table = schema.locateTable( objectName.getName() );
 		if ( table != null ) {
 			bindHibernateTableAnnotation( table, tableAnnotation );
 		}
 	}
 
 	private static void bindHibernateTableAnnotation(Table table, AnnotationInstance tableAnnotation) {
 		for ( AnnotationInstance indexAnnotation : JandexHelper.getValue(
 				tableAnnotation,
 				"indexes",
 				AnnotationInstance[].class
 		) ) {
 			bindIndexAnnotation( table, indexAnnotation );
 		}
 		String comment = JandexHelper.getValue( tableAnnotation, "comment", String.class );
 		if ( StringHelper.isNotEmpty( comment ) ) {
 			table.addComment( comment.trim() );
 		}
 	}
 
 	private static void bindIndexAnnotation(Table table, AnnotationInstance indexAnnotation) {
 		String indexName = JandexHelper.getValue( indexAnnotation, "appliesTo", String.class );
 		String[] columnNames = JandexHelper.getValue( indexAnnotation, "columnNames", String[].class );
 		if ( columnNames == null ) {
 			LOG.noColumnsSpecifiedForIndex( indexName, table.toLoggableString() );
 			return;
 		}
 		org.hibernate.metamodel.relational.Index index = table.getOrCreateIndex( indexName );
 		for ( String columnName : columnNames ) {
 			Column column = findColumn( table, columnName );
 			if ( column == null ) {
 				throw new AnnotationException( "@Index references a unknown column: " + columnName );
 			}
 			index.addColumn( column );
 		}
 	}
 
 	private static Column findColumn(Table table, String columnName) {
 		Column column = null;
 		for ( SimpleValue value : table.values() ) {
 			if ( value instanceof Column && ( (Column) value ).getColumnName().getName().equals( columnName ) ) {
 				column = (Column) value;
 				break;
 			}
 		}
 		return column;
 	}
-
-	private TableBinder() {
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TypeDefBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TypeDefBinder.java
index 378f92fbde..2b221b1f96 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TypeDefBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TypeDefBinder.java
@@ -1,118 +1,126 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
 import java.util.HashMap;
+import java.util.List;
 import java.util.Map;
 
 import org.jboss.jandex.AnnotationInstance;
-import org.jboss.jandex.Index;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.annotations.TypeDefs;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.binding.TypeDef;
+import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
+import org.hibernate.metamodel.source.annotations.JandexHelper;
 
+/**
+ * Binds {@link org.hibernate.annotations.TypeDef} and {@link TypeDefs}.
+ *
+ * @author Hardy Ferentschik
+ */
 public class TypeDefBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			TypeDefBinder.class.getName()
 	);
 
 	/**
 	 * Binds all {@link org.hibernate.annotations.TypeDef} and {@link TypeDefs} annotations to the supplied metadata.
 	 *
-	 * @param metadata the global metadata
-	 * @param jandex the jandex jandex
+	 * @param bindingContext the context for annotation binding
 	 */
-	public static void bind(MetadataImplementor metadata, Index jandex) {
-		for ( AnnotationInstance typeDef : jandex.getAnnotations( HibernateDotNames.TYPE_DEF ) ) {
-			bind( metadata, typeDef );
+	public static void bind(AnnotationBindingContext bindingContext) {
+		List<AnnotationInstance> annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.TYPE_DEF );
+		for ( AnnotationInstance typeDef : annotations ) {
+			bind( bindingContext.getMetadataImplementor(), typeDef );
 		}
-		for ( AnnotationInstance typeDefs : jandex.getAnnotations( HibernateDotNames.TYPE_DEFS ) ) {
+
+		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.TYPE_DEFS );
+		for ( AnnotationInstance typeDefs : annotations ) {
 			AnnotationInstance[] typeDefAnnotations = JandexHelper.getValue(
 					typeDefs,
 					"value",
 					AnnotationInstance[].class
 			);
 			for ( AnnotationInstance typeDef : typeDefAnnotations ) {
-				bind( metadata, typeDef );
+				bind( bindingContext.getMetadataImplementor(), typeDef );
 			}
 		}
 	}
 
 	private static void bind(MetadataImplementor metadata, AnnotationInstance typeDefAnnotation) {
 		String name = JandexHelper.getValue( typeDefAnnotation, "name", String.class );
 		String defaultForType = JandexHelper.getValue( typeDefAnnotation, "defaultForType", String.class );
 		String typeClass = JandexHelper.getValue( typeDefAnnotation, "typeClass", String.class );
 
 		boolean noName = StringHelper.isEmpty( name );
 		boolean noDefaultForType = defaultForType == null || defaultForType.equals( void.class.getName() );
 
 		if ( noName && noDefaultForType ) {
 			throw new AnnotationException(
 					"Either name or defaultForType (or both) attribute should be set in TypeDef having typeClass "
 							+ typeClass
 			);
 		}
 
 		Map<String, String> parameterMaps = new HashMap<String, String>();
 		AnnotationInstance[] parameterAnnotations = JandexHelper.getValue(
 				typeDefAnnotation,
 				"parameters",
 				AnnotationInstance[].class
 		);
 		for ( AnnotationInstance parameterAnnotation : parameterAnnotations ) {
 			parameterMaps.put(
 					JandexHelper.getValue( parameterAnnotation, "name", String.class ),
 					JandexHelper.getValue( parameterAnnotation, "value", String.class )
 			);
 		}
 
 		if ( !noName ) {
 			bind( name, typeClass, parameterMaps, metadata );
 		}
 		if ( !noDefaultForType ) {
 			bind( defaultForType, typeClass, parameterMaps, metadata );
 		}
 	}
 
 	private static void bind(
 			String name,
 			String typeClass,
 			Map<String, String> prms,
 			MetadataImplementor metadata) {
 		LOG.debugf( "Binding type definition: %s", name );
 		metadata.addTypeDefinition( new TypeDef( name, typeClass, prms ) );
 	}
 
 	private TypeDefBinder() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index 516cb447a1..c6ccd24d2a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,594 +1,594 @@
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
 package org.hibernate.metamodel.source.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.SourceProcessingOrder;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.SourceProcessor;
-import org.hibernate.metamodel.source.annotations.AnnotationsSourceProcessor;
+import org.hibernate.metamodel.source.annotations.AnnotationProcessor;
 import org.hibernate.metamodel.source.hbm.HbmSourceProcessorImpl;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.BasicType;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Container for configuration data collected during binding the metamodel.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  * @author Gail Badner
  */
 public class MetadataImpl implements MetadataImplementor, Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			MetadataImpl.class.getName()
 	);
 
 	private final BasicServiceRegistry serviceRegistry;
 	private final Options options;
 
 	private final Value<ClassLoaderService> classLoaderService;
 	private final Value<PersisterClassResolver> persisterClassResolverService;
 
 	private TypeResolver typeResolver = new TypeResolver();
 
 	private SessionFactoryBuilder sessionFactoryBuilder = new SessionFactoryBuilderImpl( this );
 
 	private DefaultIdentifierGeneratorFactory identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
 
 	private final Database database;
 
 	private final MappingDefaults mappingDefaults;
 
 	/**
 	 * Maps the fully qualified class name of an entity to its entity binding
 	 */
 	private Map<String, EntityBinding> entityBindingMap = new HashMap<String, EntityBinding>();
 	private Map<String, EntityBinding> rootEntityBindingMap = new HashMap<String, EntityBinding>();
 	private Map<String, PluralAttributeBinding> collectionBindingMap = new HashMap<String, PluralAttributeBinding>();
 	private Map<String, FetchProfile> fetchProfiles = new HashMap<String, FetchProfile>();
 	private Map<String, String> imports = new HashMap<String, String>();
 	private Map<String, TypeDef> typeDefs = new HashMap<String, TypeDef>();
 	private Map<String, IdGenerator> idGenerators = new HashMap<String, IdGenerator>();
 	private Map<String, NamedQueryDefinition> namedQueryDefs = new HashMap<String, NamedQueryDefinition>();
 	private Map<String, NamedSQLQueryDefinition> namedNativeQueryDefs = new HashMap<String, NamedSQLQueryDefinition>();
 	private Map<String, ResultSetMappingDefinition> resultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 	private Map<String, FilterDefinition> filterDefs = new HashMap<String, FilterDefinition>();
 
     private boolean globallyQuotedIdentifiers = false;
 
 	public MetadataImpl(MetadataSources metadataSources, Options options) {
 		this.serviceRegistry = metadataSources.getServiceRegistry();
 		this.options = options;
 		this.database = new Database( options );
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
 		final SourceProcessor[] sourceProcessors;
 		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
 			sourceProcessors = new SourceProcessor[] {
 					new HbmSourceProcessorImpl( this ),
-					new AnnotationsSourceProcessor( this )
+					new AnnotationProcessor( this )
 			};
 		}
 		else {
 			sourceProcessors = new SourceProcessor[] {
-					new AnnotationsSourceProcessor( this ),
+					new AnnotationProcessor( this ),
 					new HbmSourceProcessorImpl( this )
 			};
 		}
 
 		this.classLoaderService = new org.hibernate.internal.util.Value<ClassLoaderService>(
 				new org.hibernate.internal.util.Value.DeferredInitializer<ClassLoaderService>() {
 					@Override
 					public ClassLoaderService initialize() {
 						return serviceRegistry.getService( ClassLoaderService.class );
 					}
 				}
 		);
 		this.persisterClassResolverService = new org.hibernate.internal.util.Value<PersisterClassResolver>(
 				new org.hibernate.internal.util.Value.DeferredInitializer<PersisterClassResolver>() {
 					@Override
 					public PersisterClassResolver initialize() {
 						return serviceRegistry.getService( PersisterClassResolver.class );
 					}
 				}
 		);
 
 
 		final ArrayList<String> processedEntityNames = new ArrayList<String>();
 
 		prepare( sourceProcessors, metadataSources );
 		bindIndependentMetadata( sourceProcessors, metadataSources );
 		bindTypeDependentMetadata( sourceProcessors, metadataSources );
 		bindMappingMetadata( sourceProcessors, metadataSources, processedEntityNames );
 		bindMappingDependentMetadata( sourceProcessors, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
 		new EntityReferenceResolver( this ).resolve();
 		new AttributeTypeResolver( this ).resolve();
 	}
 
 	private void prepare(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.prepare( metadataSources );
 		}
 	}
 
 	private void bindIndependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.processIndependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindTypeDependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.processTypeDependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindMappingMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources, List<String> processedEntityNames) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.processMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
 	private void bindMappingDependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.processMappingDependentMetadata( metadataSources );
 		}
 	}
 
 	@Override
 	public void addFetchProfile(FetchProfile profile) {
 		if ( profile == null || profile.getName() == null ) {
 			throw new IllegalArgumentException( "Fetch profile object or name is null: " + profile );
 		}
 		fetchProfiles.put( profile.getName(), profile );
 	}
 
 	@Override
 	public void addFilterDefinition(FilterDefinition def) {
 		if ( def == null || def.getFilterName() == null ) {
 			throw new IllegalArgumentException( "Filter definition object or name is null: "  + def );
 		}
 		filterDefs.put( def.getFilterName(), def );
 	}
 
 	public Iterable<FilterDefinition> getFilterDefinitions() {
 		return filterDefs.values();
 	}
 
 	@Override
 	public void addIdGenerator(IdGenerator generator) {
 		if ( generator == null || generator.getName() == null ) {
 			throw new IllegalArgumentException( "ID generator object or name is null." );
 		}
 		idGenerators.put( generator.getName(), generator );
 	}
 
 	@Override
 	public IdGenerator getIdGenerator(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid generator name" );
 		}
 		return idGenerators.get( name );
 	}
 	@Override
 	public void registerIdentifierGenerator(String name, String generatorClassName) {
 		 identifierGeneratorFactory.register( name, classLoaderService().classForName( generatorClassName ) );
 	}
 
 	@Override
 	public void addNamedNativeQuery(NamedSQLQueryDefinition def) {
 		if ( def == null || def.getName() == null ) {
 			throw new IllegalArgumentException( "Named native query definition object or name is null: " + def.getQueryString() );
 		}
 		namedNativeQueryDefs.put( def.getName(), def );
 	}
 
 	public NamedSQLQueryDefinition getNamedNativeQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid native query name" );
 		}
 		return namedNativeQueryDefs.get( name );
 	}
 
 	@Override
 	public Iterable<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions() {
 		return namedNativeQueryDefs.values();
 	}
 
 	@Override
 	public void addNamedQuery(NamedQueryDefinition def) {
 		if ( def == null ) {
 			throw new IllegalArgumentException( "Named query definition is null" );
 		}
 		else if ( def.getName() == null ) {
 			throw new IllegalArgumentException( "Named query definition name is null: " + def.getQueryString() );
 		}
 		namedQueryDefs.put( def.getName(), def );
 	}
 
 	public NamedQueryDefinition getNamedQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid query name" );
 		}
 		return namedQueryDefs.get( name );
 	}
 
 	@Override
 	public Iterable<NamedQueryDefinition> getNamedQueryDefinitions() {
 		return namedQueryDefs.values();
 	}
 
 	@Override
 	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
 		if ( resultSetMappingDefinition == null || resultSetMappingDefinition.getName() == null ) {
 			throw new IllegalArgumentException( "Result-set mapping object or name is null: " + resultSetMappingDefinition );
 		}
 		resultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 	}
 
 	@Override
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions() {
 		return resultSetMappings.values();
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDef typeDef) {
 		if ( typeDef == null ) {
 			throw new IllegalArgumentException( "Type definition is null" );
 		}
 		else if ( typeDef.getName() == null ) {
 			throw new IllegalArgumentException( "Type definition name is null: " + typeDef.getTypeClass() );
 		}
 		final TypeDef previous = typeDefs.put( typeDef.getName(), typeDef );
 		if ( previous != null ) {
 			LOG.debugf( "Duplicate typedef name [%s] now -> %s", typeDef.getName(), typeDef.getTypeClass() );
 		}
 	}
 
 	@Override
 	public Iterable<TypeDef> getTypeDefinitions() {
 		return typeDefs.values();
 	}
 
 	@Override
 	public TypeDef getTypeDefinition(String name) {
 		return typeDefs.get( name );
 	}
 
 	private ClassLoaderService classLoaderService() {
 		return classLoaderService.getValue();
 	}
 
 	private PersisterClassResolver persisterClassResolverService() {
 		return persisterClassResolverService.getValue();
 	}
 
 	@Override
 	public Options getOptions() {
 		return options;
 	}
 
 	@Override
 	public SessionFactory buildSessionFactory() {
 		return sessionFactoryBuilder.buildSessionFactory();
 	}
 
 	@Override
 	public BasicServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> Class<T> locateClassByName(String name) {
 		return classLoaderService().classForName( name );
 	}
 
 	@Override
 	public Type makeJavaType(String className) {
 		// todo : have this perform some analysis of the incoming type name to determine appropriate return
 		return new BasicType( className, makeClassReference( className ) );
 	}
 
 	@Override
 	public Value<Class<?>> makeClassReference(final String className) {
 		return new Value<Class<?>>(
 				new Value.DeferredInitializer<Class<?>>() {
 					@Override
 					public Class<?> initialize() {
 						return classLoaderService.getValue().classForName( className );
 					}
 				}
 		);
 	}
 
 	@Override
 	public Database getDatabase() {
 		return database;
 	}
 
 	public EntityBinding getEntityBinding(String entityName) {
 		return entityBindingMap.get( entityName );
 	}
 
 	@Override
 	public EntityBinding getRootEntityBinding(String entityName) {
 		EntityBinding rootEntityBinding = rootEntityBindingMap.get( entityName );
 		if ( rootEntityBinding == null ) {
 			EntityBinding entityBinding = entityBindingMap.get( entityName );
 			if ( entityBinding == null ) {
 				throw new IllegalStateException( "Unknown entity binding: " + entityName );
 			}
 			if ( entityBinding.isRoot() ) {
 				rootEntityBinding = entityBinding;
 			}
 			else {
 				if ( entityBinding.getEntity().getSuperType() == null ) {
 					throw new IllegalStateException( "Entity binding has no root: " + entityName );
 				}
 				rootEntityBinding = getRootEntityBinding( entityBinding.getEntity().getSuperType().getName() );
 			}
 			rootEntityBindingMap.put( entityName, rootEntityBinding );
 		}
 		return rootEntityBinding;
 	}
 
 	public Iterable<EntityBinding> getEntityBindings() {
 		return entityBindingMap.values();
 	}
 
 	public void addEntity(EntityBinding entityBinding) {
 		final String entityName = entityBinding.getEntity().getName();
 		if ( entityBindingMap.containsKey( entityName ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, entityName );
 		}
 		entityBindingMap.put( entityName, entityBinding );
 	}
 
 	public PluralAttributeBinding getCollection(String collectionRole) {
 		return collectionBindingMap.get( collectionRole );
 	}
 
 	@Override
 	public Iterable<PluralAttributeBinding> getCollectionBindings() {
 		return collectionBindingMap.values();
 	}
 
 	public void addCollection(PluralAttributeBinding pluralAttributeBinding) {
 		final String owningEntityName = pluralAttributeBinding.getEntityBinding().getEntity().getName();
 		final String attributeName = pluralAttributeBinding.getAttribute().getName();
 		final String collectionRole = owningEntityName + '.' + attributeName;
 		if ( collectionBindingMap.containsKey( collectionRole ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, collectionRole );
 		}
 		collectionBindingMap.put( collectionRole, pluralAttributeBinding );
 	}
 
 	public void addImport(String importName, String entityName) {
 		if ( importName == null || entityName == null ) {
 			throw new IllegalArgumentException( "Import name or entity name is null" );
 		}
 		LOG.trace( "Import: " + importName + " -> " + entityName );
 		String old = imports.put( importName, entityName );
 		if ( old != null ) {
 			LOG.debug( "import name [" + importName + "] overrode previous [{" + old + "}]" );
 		}
 	}
 
 	public Iterable<Map.Entry<String, String>> getImports() {
 		return imports.entrySet();
 	}
 
 	public Iterable<FetchProfile> getFetchProfiles() {
 		return fetchProfiles.values();
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	@Override
 	public SessionFactoryBuilder getSessionFactoryBuilder() {
 		return sessionFactoryBuilder;
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return options.getNamingStrategy();
 	}
 
     @Override
     public boolean isGloballyQuotedIdentifiers() {
         return globallyQuotedIdentifiers || getOptions().isGloballyQuotedIdentifiers();
     }
 
     public void setGloballyQuotedIdentifiers(boolean globallyQuotedIdentifiers){
        this.globallyQuotedIdentifiers = globallyQuotedIdentifiers;
     }
 
     @Override
 	public MappingDefaults getMappingDefaults() {
 		return mappingDefaults;
 	}
 
 	private final MetaAttributeContext globalMetaAttributeContext = new MetaAttributeContext();
 
 	@Override
 	public MetaAttributeContext getGlobalMetaAttributeContext() {
 		return globalMetaAttributeContext;
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		return this;
 	}
 
 	private static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
 	private static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "class";
 	private static final String DEFAULT_CASCADE = "none";
 	private static final String DEFAULT_PROPERTY_ACCESS = "property";
 
 	@Override
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	@Override
 	public org.hibernate.type.Type getIdentifierType(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		return entityBinding
 				.getEntityIdentifier()
 				.getValueBinding()
 				.getHibernateTypeDescriptor()
 				.getResolvedTypeMapping();
 	}
 
 	@Override
 	public String getIdentifierPropertyName(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		AttributeBinding idBinding = entityBinding.getEntityIdentifier().getValueBinding();
 		return idBinding == null ? null : idBinding.getAttribute().getName();
 	}
 
 	@Override
 	public org.hibernate.type.Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
 		AttributeBinding attributeBinding = entityBinding.getAttributeBinding( propertyName );
 		if ( attributeBinding == null ) {
 			throw new MappingException( "unknown property: " + entityName + '.' + propertyName );
 		}
 		return attributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping();
 	}
 
 	private class MappingDefaultsImpl implements MappingDefaults {
 
 		@Override
 		public String getPackageName() {
 			return null;
 		}
 
 		@Override
 		public String getSchemaName() {
 			return options.getDefaultSchemaName();
 		}
 
 		@Override
 		public String getCatalogName() {
 			return options.getDefaultCatalogName();
 		}
 
 		@Override
 		public String getIdColumnName() {
 			return DEFAULT_IDENTIFIER_COLUMN_NAME;
 		}
 
 		@Override
 		public String getDiscriminatorColumnName() {
 			return DEFAULT_DISCRIMINATOR_COLUMN_NAME;
 		}
 
 		@Override
 		public String getCascadeStyle() {
 			return DEFAULT_CASCADE;
 		}
 
 		@Override
 		public String getPropertyAccessorName() {
 			return DEFAULT_PROPERTY_ACCESS;
 		}
 
 		@Override
 		public boolean areAssociationsLazy() {
 			return true;
 		}
 
 		private final Value<AccessType> regionFactorySpecifiedDefaultAccessType = new Value<AccessType>(
 				new Value.DeferredInitializer<AccessType>() {
 					@Override
 					public AccessType initialize() {
 						final RegionFactory regionFactory = getServiceRegistry().getService( RegionFactory.class );
 						return regionFactory.getDefaultAccessType();
 					}
 				}
 		);
 
 		@Override
 		public AccessType getCacheAccessType() {
 			return options.getDefaultAccessType() != null
 					? options.getDefaultAccessType()
 					: regionFactorySpecifiedDefaultAccessType.getValue();
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/TestAnnotationsBindingContextImpl.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/TestAnnotationsBindingContextImpl.java
index dcc2ba7946..87567e9910 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/TestAnnotationsBindingContextImpl.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/TestAnnotationsBindingContextImpl.java
@@ -1,140 +1,141 @@
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
 package org.hibernate.metamodel.source.annotations;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import com.fasterxml.classmate.MemberResolver;
 import com.fasterxml.classmate.ResolvedType;
 import com.fasterxml.classmate.ResolvedTypeWithMembers;
 import com.fasterxml.classmate.TypeResolver;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 import org.jboss.jandex.Index;
 
 import org.hibernate.cfg.EJB3NamingStrategy;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.internal.util.Value;
+import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.metamodel.domain.Type;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * @author Steve Ebersole
  */
-public class TestAnnotationsBindingContextImpl implements AnnotationsBindingContext {
+public class TestAnnotationsBindingContextImpl implements AnnotationBindingContext {
 	private Index index;
 	private ServiceRegistry serviceRegistry;
 
 	private NamingStrategy namingStrategy = EJB3NamingStrategy.INSTANCE;
 
 	private final TypeResolver typeResolver = new TypeResolver();
 	private final Map<Class<?>, ResolvedType> resolvedTypeCache = new HashMap<Class<?>, ResolvedType>();
 
 	public TestAnnotationsBindingContextImpl(Index index, ServiceRegistry serviceRegistry) {
 		this.index = index;
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	@Override
 	public Index getIndex() {
 		return index;
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
 	@Override
 	public MappingDefaults getMappingDefaults() {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public <T> Class<T> locateClassByName(String name) {
 		return serviceRegistry.getService( ClassLoaderService.class ).classForName( name );
 	}
 
 	@Override
 	public Type makeJavaType(String className) {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public Value<Class<?>> makeClassReference(String className) {
 		throw new NotYetImplementedException();
 	}
+
 	@Override
 	public ClassInfo getClassInfo(String name) {
 		DotName dotName = DotName.createSimple( name );
 		return index.getClassByName( dotName );
 	}
 
 	@Override
 	public void resolveAllTypes(String className) {
 		// the resolved type for the top level class in the hierarchy
 		Class<?> clazz = locateClassByName( className );
 		ResolvedType resolvedType = typeResolver.resolve( clazz );
 		while ( resolvedType != null ) {
 			// todo - check whether there is already something in the map
 			resolvedTypeCache.put( clazz, resolvedType );
 			resolvedType = resolvedType.getParentClass();
 			if ( resolvedType != null ) {
 				clazz = resolvedType.getErasedType();
 			}
 		}
 	}
 
 	@Override
 	public ResolvedType getResolvedType(Class<?> clazz) {
 		return resolvedTypeCache.get( clazz );
 	}
 
 	@Override
 	public ResolvedTypeWithMembers resolveMemberTypes(ResolvedType type) {
 		// todo : is there a reason we create this resolver every time?
 		MemberResolver memberResolver = new MemberResolver( typeResolver );
 		return memberResolver.resolve( type, null, null );
 	}
 
 	@Override
 	public boolean isGloballyQuotedIdentifiers() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/EmbeddableBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/EmbeddableBindingTests.java
index 4fe1459977..fb74002cc9 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/EmbeddableBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/EmbeddableBindingTests.java
@@ -1,78 +1,79 @@
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
 
 import javax.persistence.Embeddable;
 import javax.persistence.Embedded;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 
 import org.junit.Test;
 
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.domain.Attribute;
+
 import org.hibernate.metamodel.domain.Component;
+import org.hibernate.metamodel.domain.SingularAttribute;;
 
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * Tests for {@code j.p.Embeddable}.
  *
  * @author Hardy Ferentschik
  */
 public class EmbeddableBindingTests extends BaseAnnotationBindingTestCase {
 	@Test
 	public void testEmbeddable() {
 		buildMetadataSources( User.class, Address.class );
 		EntityBinding binding = getEntityBinding( User.class );
 		assertNotNull( binding.getAttributeBinding( "street" ) );
 		assertNotNull( binding.getAttributeBinding( "city" ) );
 		assertNotNull( binding.getAttributeBinding( "postCode" ) );
 
-		Attribute attribute = binding.getEntity().getAttribute( "address" );
+		SingularAttribute attribute = (SingularAttribute) binding.getEntity().getAttribute( "address" );
 		assertTrue(
 				"Wrong container type. Should be a component",
-				attribute.getAttributeContainer() instanceof Component
+				attribute.getSingularAttributeType() instanceof Component
 		);
 	}
 
 	@Entity
 	class User {
 		@Id
 		private int id;
 
 		@Embedded
 		private Address address;
 	}
 
 	@Embeddable
 	class Address {
 		String street;
 		String city;
 		String postCode;
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/InheritanceBindingTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/InheritanceBindingTest.java
index 39ce58febe..b88eafb4d7 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/InheritanceBindingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/InheritanceBindingTest.java
@@ -1,88 +1,87 @@
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
 
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 
 import org.junit.Test;
 
 import org.hibernate.metamodel.binding.EntityBinding;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertFalse;
 import static junit.framework.Assert.assertNull;
 import static junit.framework.Assert.assertSame;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * @author Hardy Ferentschik
  */
 public class InheritanceBindingTest extends BaseAnnotationBindingTestCase {
 	@Test
 	public void testNoInheritance() {
 		buildMetadataSources( SingleEntity.class );
 		EntityBinding entityBinding = getEntityBinding( SingleEntity.class );
 		assertNull( entityBinding.getEntityDiscriminator() );
 	}
 
 	@Test
 	public void testDiscriminatorValue() {
 		buildMetadataSources(
 				RootOfSingleTableInheritance.class, SubclassOfSingleTableInheritance.class
 		);
 		EntityBinding entityBinding = getEntityBinding( SubclassOfSingleTableInheritance.class );
 		assertEquals( "Wrong discriminator value", "foo", entityBinding.getDiscriminatorValue() );
 	}
 
 	@Test
 	public void testRootEntityBinding() {
 		buildMetadataSources(
 				SubclassOfSingleTableInheritance.class, SingleEntity.class, RootOfSingleTableInheritance.class
 		);
 
 		EntityBinding noInheritanceEntityBinding = getEntityBinding( SingleEntity.class );
-		EntityBinding subclassEntityBinding = getEntityBinding( SubclassOfSingleTableInheritance.class );
-		EntityBinding rootEntityBinding = getEntityBinding( RootOfSingleTableInheritance.class );
-
 		assertTrue( noInheritanceEntityBinding.isRoot() );
 		assertSame( noInheritanceEntityBinding, getRootEntityBinding( SingleEntity.class ) );
 
+		EntityBinding subclassEntityBinding = getEntityBinding( SubclassOfSingleTableInheritance.class );
+		EntityBinding rootEntityBinding = getEntityBinding( RootOfSingleTableInheritance.class );
 		assertFalse( subclassEntityBinding.isRoot() );
 		assertSame( rootEntityBinding, getRootEntityBinding( SubclassOfSingleTableInheritance.class ) );
 
 		assertTrue( rootEntityBinding.isRoot() );
 		assertSame( rootEntityBinding, getRootEntityBinding( RootOfSingleTableInheritance.class ));
 	}
 
 	@Entity
 	class SingleEntity {
 		@Id
 		@GeneratedValue
 		private int id;
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MappedSuperclassTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MappedSuperclassTests.java
index ff774febd3..ce9fa1ab4d 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MappedSuperclassTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MappedSuperclassTests.java
@@ -1,126 +1,120 @@
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
 package org.hibernate.metamodel.source.annotations.entity;
 
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.MappedSuperclass;
 
 import org.junit.Test;
 
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.domain.NonEntity;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Tuple;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * Tests for {@link javax.persistence.MappedSuperclass} {@link javax.persistence.AttributeOverrides}
  * and {@link javax.persistence.AttributeOverride}.
  *
  * @author Hardy Ferentschik
  */
 public class MappedSuperclassTests extends BaseAnnotationBindingTestCase {
 	@Test
 	public void testSimpleAttributeOverrideInMappedSuperclass() {
 		buildMetadataSources( MyMappedSuperClass.class, MyEntity.class, MyMappedSuperClassBase.class );
 
 		EntityBinding binding = getEntityBinding( MyEntity.class );
 		AttributeBinding nameBinding = binding.getAttributeBinding( "name" );
 		assertNotNull( "the name attribute should be bound to MyEntity", nameBinding );
 
-		Tuple tuple = (Tuple) nameBinding.getValue();
-		SimpleValue value = tuple.values().iterator().next();
-		assertTrue( value instanceof Column );
-		Column column = (Column) value;
-		assertEquals( "Wrong column name", "`MY_NAME`", column.getColumnName().toString() );
+		Column column = (Column) nameBinding.getValue();
+		assertEquals( "Wrong column name", "MY_NAME", column.getColumnName().toString() );
 	}
 
 	@Test
 	public void testLastAttributeOverrideWins() {
 		buildMetadataSources( MyMappedSuperClass.class, MyEntity.class, MyMappedSuperClassBase.class );
 
 		EntityBinding binding = getEntityBinding( MyEntity.class );
 		AttributeBinding fooBinding = binding.getAttributeBinding( "foo" );
 		assertNotNull( "the foo attribute should be bound to MyEntity", fooBinding );
 
-		Tuple tuple = (Tuple) fooBinding.getValue();
-		SimpleValue value = tuple.values().iterator().next();
-		assertTrue( value instanceof Column );
-		Column column = (Column) value;
-		assertEquals( "Wrong column name", "`MY_FOO`", column.getColumnName().toString() );
+		Column column = (Column) fooBinding.getValue();
+		assertEquals( "Wrong column name", "MY_FOO", column.getColumnName().toString() );
 	}
 
 	@Test
 	public void testNonEntityBaseClass() {
 		buildMetadataSources( SubclassOfNoEntity.class, NoEntity.class );
 		EntityBinding binding = getEntityBinding( SubclassOfNoEntity.class );
 		assertEquals( "Wrong entity name", SubclassOfNoEntity.class.getName(), binding.getEntity().getName() );
 		assertEquals( "Wrong entity name", NoEntity.class.getName(), binding.getEntity().getSuperType().getName() );
 		assertTrue( binding.getEntity().getSuperType() instanceof NonEntity );
 	}
 
 	@MappedSuperclass
 	class MyMappedSuperClassBase {
 		@Id
 		private int id;
 		String foo;
 	}
 
 	@MappedSuperclass
 	@AttributeOverride(name = "foo", column = @javax.persistence.Column(name = "SUPER_FOO"))
 	class MyMappedSuperClass extends MyMappedSuperClassBase {
 		String name;
 	}
 
 	@Entity
 	@AttributeOverrides( {
 			@AttributeOverride(name = "name", column = @javax.persistence.Column(name = "MY_NAME")),
 			@AttributeOverride(name = "foo", column = @javax.persistence.Column(name = "MY_FOO"))
 	})
 	class MyEntity extends MyMappedSuperClass {
 		private Long count;
 
 	}
 
 	class NoEntity {
 		String name;
 		int age;
 	}
 
 	@Entity
 	class SubclassOfNoEntity extends NoEntity {
 		@Id
 		private int id;
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
index ea1ed2fbc1..cb099d15d9 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
@@ -1,147 +1,146 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
 import java.util.Iterator;
 
 import org.jboss.jandex.Index;
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
 
 import org.hibernate.MappingException;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.FetchProfile;
 import org.hibernate.annotations.FetchProfiles;
 import org.hibernate.metamodel.MetadataSources;
+import org.hibernate.metamodel.source.annotations.AnnotationBindingContextImpl;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
-
-import org.junit.After;
-import org.junit.Before;
-import org.junit.Test;
-
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.fail;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Hardy Ferentschik
  */
 public class FetchProfileBinderTest extends BaseUnitTestCase {
 
 	private BasicServiceRegistryImpl serviceRegistry;
 	private ClassLoaderService service;
 	private MetadataImpl meta;
 
 	@Before
 	public void setUp() {
 		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 		service = serviceRegistry.getService( ClassLoaderService.class );
 		meta = (MetadataImpl) new MetadataSources( serviceRegistry ).buildMetadata();
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testSingleFetchProfile() {
 		@FetchProfile(name = "foo", fetchOverrides = {
 				@FetchProfile.FetchOverride(entity = Foo.class, association = "bar", mode = FetchMode.JOIN)
 		})
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 
-		FetchProfileBinder.bind( meta, index );
+		FetchProfileBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 
 		Iterator<org.hibernate.metamodel.binding.FetchProfile> mappedFetchProfiles = meta.getFetchProfiles().iterator();
 		assertTrue( mappedFetchProfiles.hasNext() );
 		org.hibernate.metamodel.binding.FetchProfile profile = mappedFetchProfiles.next();
 		assertEquals( "Wrong fetch profile name", "foo", profile.getName() );
 		org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 		assertEquals( "Wrong association name", "bar", fetch.getAssociation() );
 		assertEquals( "Wrong association type", Foo.class.getName(), fetch.getEntity() );
 	}
 
 	@Test
 	public void testFetchProfiles() {
 		Index index = JandexHelper.indexForClass( service, FooBar.class );
-		FetchProfileBinder.bind( meta, index );
+		FetchProfileBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 
 		Iterator<org.hibernate.metamodel.binding.FetchProfile> mappedFetchProfiles = meta.getFetchProfiles().iterator();
 		assertTrue( mappedFetchProfiles.hasNext() );
 		org.hibernate.metamodel.binding.FetchProfile profile = mappedFetchProfiles.next();
 		assertProfiles( profile );
 
 		assertTrue( mappedFetchProfiles.hasNext() );
 		profile = mappedFetchProfiles.next();
 		assertProfiles( profile );
 	}
 
 	private void assertProfiles(org.hibernate.metamodel.binding.FetchProfile profile) {
 		if ( profile.getName().equals( "foobar" ) ) {
 			org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 			assertEquals( "Wrong association name", "foobar", fetch.getAssociation() );
 			assertEquals( "Wrong association type", FooBar.class.getName(), fetch.getEntity() );
 		}
 		else if ( profile.getName().equals( "fubar" ) ) {
 			org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 			assertEquals( "Wrong association name", "fubar", fetch.getAssociation() );
 			assertEquals( "Wrong association type", FooBar.class.getName(), fetch.getEntity() );
 		}
 		else {
 			fail( "Wrong fetch name:" + profile.getName() );
 		}
 	}
 
 	@Test(expected = MappingException.class)
 	public void testNonJoinFetchThrowsException() {
 		@FetchProfile(name = "foo", fetchOverrides = {
 				@FetchProfile.FetchOverride(entity = Foo.class, association = "bar", mode = FetchMode.SELECT)
 		})
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 
-		FetchProfileBinder.bind( meta, index );
+		FetchProfileBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 	}
 
 	@FetchProfiles( {
 			@FetchProfile(name = "foobar", fetchOverrides = {
 					@FetchProfile.FetchOverride(entity = FooBar.class, association = "foobar", mode = FetchMode.JOIN)
 			}),
 			@FetchProfile(name = "fubar", fetchOverrides = {
 					@FetchProfile.FetchOverride(entity = FooBar.class, association = "fubar", mode = FetchMode.JOIN)
 			})
 	})
 	class FooBar {
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/QueryBinderTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/QueryBinderTest.java
index c1cac6f3df..f30d389e11 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/QueryBinderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/QueryBinderTest.java
@@ -1,97 +1,98 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
 import javax.persistence.NamedNativeQuery;
 
 import org.jboss.jandex.Index;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.MetadataSources;
+import org.hibernate.metamodel.source.annotations.AnnotationBindingContextImpl;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * @author Hardy Ferentschik
  */
 public class QueryBinderTest extends BaseUnitTestCase {
 
 	private BasicServiceRegistryImpl serviceRegistry;
 	private ClassLoaderService service;
 	private MetadataImpl meta;
 
 	@Before
 	public void setUp() {
 		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 		service = serviceRegistry.getService( ClassLoaderService.class );
 		meta = (MetadataImpl) new MetadataSources( serviceRegistry ).buildMetadata();
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	@Test(expected = NotYetImplementedException.class)
 	public void testNoResultClass() {
 		@NamedNativeQuery(name = "fubar", query = "SELECT * FROM FOO")
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
-		QueryBinder.bind( meta, index );
+		QueryBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 	}
 
 	@Test
 	public void testResultClass() {
 		@NamedNativeQuery(name = "fubar", query = "SELECT * FROM FOO", resultClass = Foo.class)
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
-		QueryBinder.bind( meta, index );
+		QueryBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 
 		NamedSQLQueryDefinition namedQuery = meta.getNamedNativeQuery( "fubar" );
 		assertNotNull( namedQuery );
 		NativeSQLQueryReturn queryReturns[] = namedQuery.getQueryReturns();
 		assertTrue( "Wrong number of returns", queryReturns.length == 1 );
 		assertTrue( "Wrong query return type", queryReturns[0] instanceof NativeSQLQueryRootReturn );
 		NativeSQLQueryRootReturn rootReturn = (NativeSQLQueryRootReturn) queryReturns[0];
 		assertEquals( "Wrong result class", Foo.class.getName(), rootReturn.getReturnEntityName() );
 	}
 }
 
 
