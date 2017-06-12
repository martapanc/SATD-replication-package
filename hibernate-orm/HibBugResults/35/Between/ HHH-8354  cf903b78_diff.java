diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/CollectionTracker.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/CollectionTracker.java
new file mode 100644
index 0000000000..f3b2373210
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/CollectionTracker.java
@@ -0,0 +1,51 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.bytecode.enhance.spi;
+
+import java.util.HashMap;
+import java.util.Map;
+
+/**
+ * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
+ */
+public class CollectionTracker {
+
+    private Map<String, Integer> tracker;
+
+    public CollectionTracker() {
+        tracker = new HashMap<String, Integer>();
+    }
+
+    public void add(String name, int size) {
+        tracker.put(name, size);
+    }
+
+    public int getSize(String name) {
+        Integer size = tracker.get(name);
+        if(size == null)
+            return -1;
+        else
+            return size;
+    }
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/CompositeOwnerTracker.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/CompositeOwnerTracker.java
new file mode 100644
index 0000000000..f52c6cc64d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/CompositeOwnerTracker.java
@@ -0,0 +1,85 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.bytecode.enhance.spi;
+
+import org.hibernate.engine.spi.CompositeOwner;
+
+/**
+ * small low memory class to keep references to composite owners
+ *
+ * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
+ */
+public class CompositeOwnerTracker {
+
+    private String[] names;
+    private CompositeOwner[] owners;
+    private int size = 0;
+
+    public CompositeOwnerTracker() {
+        names = new String[1];
+        owners = new CompositeOwner[1];
+    }
+
+    public void add(String name, CompositeOwner owner) {
+        for(int i=0; i<size;i++) {
+            if(names[i].equals(name)) {
+                owners[i] = owner;
+                return;
+            }
+        }
+        if ( size >= names.length) {
+            String[] tmpNames = new String[size+1];
+            System.arraycopy(names, 0, tmpNames, 0, size);
+            names = tmpNames;
+            CompositeOwner[] tmpOwners = new CompositeOwner[size+1];
+            System.arraycopy(owners, 0, tmpOwners, 0, size);
+            owners = tmpOwners;
+        }
+        names[size] = name;
+        owners[size] = owner;
+        size++;
+    }
+
+    public void callOwner(String fieldName) {
+        for(int i=0; i < size;i++) {
+            owners[i].$$_hibernate_trackChange(names[i]+fieldName);
+        }
+    }
+
+    public void removeOwner(String name) {
+        for(int i=0; i<size;i++) {
+            if(names[i].equals(name)) {
+                if(i < size) {
+                    for(int j=i; j < size-1;j++) {
+                        names[j] = names[j+1];
+                        owners[j] = owners[j+1];
+                    }
+                    names[size-1] = null;
+                    owners[size-1] = null;
+                    size--;
+                }
+            }
+        }
+    }
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancementContext.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancementContext.java
index dc9cd7479d..1a8f34507e 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancementContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancementContext.java
@@ -1,124 +1,131 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.enhance.spi;
 
 import javassist.CtClass;
 import javassist.CtField;
 
 /**
  * The context for performing an enhancement.  Enhancement can happen in any number of ways:<ul>
  *     <li>Build time, via Ant</li>
  *     <li>Build time, via Maven</li>
  *     <li>Build time, via Gradle</li>
  *     <li>Runtime, via agent</li>
  *     <li>Runtime, via JPA constructs</li>
  * </ul>
  *
  * This interface isolates the code that actually does the enhancement from the underlying context in which
  * the enhancement is being performed.
  *
  * @todo Not sure its a great idea to expose Javassist classes this way.  maybe wrap them in our own contracts?
  *
  * @author Steve Ebersole
  */
 public interface EnhancementContext {
 	/**
 	 * Obtain access to the ClassLoader that can be used to load Class references.  In JPA SPI terms, this
 	 * should be a "temporary class loader" as defined by
 	 * {@link javax.persistence.spi.PersistenceUnitInfo#getNewTempClassLoader()}
 	 *
 	 * @return The class loader that the enhancer can use.
 	 */
 	public ClassLoader getLoadingClassLoader();
 
 	/**
 	 * Does the given class descriptor represent a entity class?
 	 *
 	 * @param classDescriptor The descriptor of the class to check.
 	 *
 	 * @return {@code true} if the class is an entity; {@code false} otherwise.
 	 */
 	public boolean isEntityClass(CtClass classDescriptor);
 
 	/**
 	 * Does the given class name represent an embeddable/component class?
 	 *
 	 * @param classDescriptor The descriptor of the class to check.
 	 *
 	 * @return {@code true} if the class is an embeddable/component; {@code false} otherwise.
 	 */
 	public boolean isCompositeClass(CtClass classDescriptor);
 
 	/**
 	 * Should we in-line dirty checking for persistent attributes for this class?
 	 *
 	 * @param classDescriptor The descriptor of the class to check.
 	 *
 	 * @return {@code true} indicates that dirty checking should be in-lined within the entity; {@code false}
 	 * indicates it should not.  In-lined is more easily serializable and probably more performant.
 	 */
 	public boolean doDirtyCheckingInline(CtClass classDescriptor);
 
 	/**
 	 * Does the given class define any lazy loadable attributes?
 	 *
 	 * @param classDescriptor The class to check
 	 *
 	 * @return true/false
 	 */
 	public boolean hasLazyLoadableAttributes(CtClass classDescriptor);
 
 	// todo : may be better to invert these 2 such that the context is asked for an ordered list of persistent fields for an entity/composite
 
 	/**
 	 * Does the field represent persistent state?  Persistent fields will be "enhanced".
 	 * <p/>
 	 * may be better to perform basic checks in the caller (non-static, etc) and call out with just the
 	 * Class name and field name...
 	 *
 	 * @param ctField The field reference.
 	 *
 	 * @return {@code true} if the field is ; {@code false} otherwise.
 	 */
 	public boolean isPersistentField(CtField ctField);
 
 	/**
 	 * For fields which are persistent (according to {@link #isPersistentField}), determine the corresponding ordering
 	 * maintained within the Hibernate metamodel.
 
 	 * @param persistentFields The persistent field references.
 	 *
 	 * @return The ordered references.
 	 */
 	public CtField[] order(CtField[] persistentFields);
 
 	/**
 	 * Determine if a field is lazy loadable.
 	 *
 	 * @param field The field to check
 	 *
 	 * @return {@code true} if the field is lazy loadable; {@code false} otherwise.
 	 */
 	public boolean isLazyLoadable(CtField field);
+
+    /**
+     *
+     * @param field the field to check
+     * @return {@code true} if the field is mapped
+     */
+    public boolean isMappedCollection(CtField field);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/Enhancer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/Enhancer.java
index b3455b6734..7a8ccdf3df 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/Enhancer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/Enhancer.java
@@ -1,971 +1,1416 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.enhance.spi;
 
+import javax.persistence.ElementCollection;
+import javax.persistence.Embedded;
+import javax.persistence.Id;
+import javax.persistence.ManyToMany;
+import javax.persistence.OneToMany;
 import javax.persistence.Transient;
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.DataOutputStream;
 import java.io.IOException;
 import java.util.ArrayList;
+import java.util.HashMap;
 import java.util.IdentityHashMap;
 import java.util.List;
+import java.util.Map;
 
 import javassist.CannotCompileException;
 import javassist.ClassPool;
 import javassist.CtClass;
 import javassist.CtField;
 import javassist.CtMethod;
 import javassist.CtNewMethod;
 import javassist.LoaderClassPath;
 import javassist.Modifier;
 import javassist.NotFoundException;
 import javassist.bytecode.AnnotationsAttribute;
 import javassist.bytecode.BadBytecode;
 import javassist.bytecode.CodeAttribute;
 import javassist.bytecode.CodeIterator;
 import javassist.bytecode.ConstPool;
 import javassist.bytecode.FieldInfo;
 import javassist.bytecode.MethodInfo;
 import javassist.bytecode.Opcode;
+import javassist.bytecode.SignatureAttribute;
 import javassist.bytecode.StackMapTable;
 import javassist.bytecode.annotation.Annotation;
 import javassist.bytecode.stackmap.MapMaker;
 
+import org.hibernate.engine.spi.SelfDirtinessTracker;
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.bytecode.enhance.EnhancementException;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.ManagedComposite;
 import org.hibernate.engine.spi.ManagedEntity;
 import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 import org.hibernate.engine.spi.PersistentAttributeInterceptor;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Class responsible for performing enhancement.
  *
  * @author Steve Ebersole
  * @author Jason Greene
  */
 public class Enhancer  {
 	private static final CoreMessageLogger log = Logger.getMessageLogger( CoreMessageLogger.class, Enhancer.class.getName() );
 
 	private final EnhancementContext enhancementContext;
 
 	private final ClassPool classPool;
 	private final CtClass managedEntityCtClass;
 	private final CtClass managedCompositeCtClass;
 	private final CtClass attributeInterceptorCtClass;
 	private final CtClass attributeInterceptableCtClass;
 	private final CtClass entityEntryCtClass;
 	private final CtClass objectCtClass;
+    private boolean isComposite;
 
-	/**
+    /**
 	 * Constructs the Enhancer, using the given context.
 	 *
 	 * @param enhancementContext Describes the context in which enhancement will occur so as to give access
 	 * to contextual/environmental information.
 	 */
 	public Enhancer(EnhancementContext enhancementContext) {
 		this.enhancementContext = enhancementContext;
 		this.classPool = buildClassPool( enhancementContext );
 
 		try {
 			// add ManagedEntity contract
 			this.managedEntityCtClass = classPool.makeClass(
 					ManagedEntity.class.getClassLoader().getResourceAsStream(
 							ManagedEntity.class.getName().replace( '.', '/' ) + ".class"
 					)
 			);
 
 			// add ManagedComposite contract
 			this.managedCompositeCtClass = classPool.makeClass(
 					ManagedComposite.class.getClassLoader().getResourceAsStream(
 							ManagedComposite.class.getName().replace( '.', '/' ) + ".class"
 					)
 			);
 
 			// add PersistentAttributeInterceptable contract
 			this.attributeInterceptableCtClass = classPool.makeClass(
 					PersistentAttributeInterceptable.class.getClassLoader().getResourceAsStream(
 							PersistentAttributeInterceptable.class.getName().replace( '.', '/' ) + ".class"
 					)
 			);
 
 			// add PersistentAttributeInterceptor contract
 			this.attributeInterceptorCtClass = classPool.makeClass(
 					PersistentAttributeInterceptor.class.getClassLoader().getResourceAsStream(
 							PersistentAttributeInterceptor.class.getName().replace( '.', '/' ) + ".class"
 					)
 			);
 
 			// "add" EntityEntry
 			this.entityEntryCtClass = classPool.makeClass( EntityEntry.class.getName() );
 		}
 		catch (IOException e) {
 			throw new EnhancementException( "Could not prepare Javassist ClassPool", e );
 		}
 
 		try {
 			this.objectCtClass = classPool.getCtClass( Object.class.getName() );
 		}
 		catch (NotFoundException e) {
 			throw new EnhancementException( "Could not prepare Javassist ClassPool", e );
 		}
 	}
 
 	private ClassPool buildClassPool(EnhancementContext enhancementContext) {
 		final ClassPool classPool = new ClassPool( false );
 		final ClassLoader loadingClassLoader = enhancementContext.getLoadingClassLoader();
 		if ( loadingClassLoader != null ) {
 			classPool.appendClassPath( new LoaderClassPath( loadingClassLoader ) );
 		}
 		return classPool;
 	}
 
 	/**
 	 * Performs the enhancement.
 	 *
 	 * @param className The name of the class whose bytecode is being enhanced.
 	 * @param originalBytes The class's original (pre-enhancement) byte code
 	 *
 	 * @return The enhanced bytecode.  Could be the same as the original bytecode if the original was
 	 * already enhanced or we could not enhance it for some reason.
 	 *
 	 * @throws EnhancementException Indicates a problem performing the enhancement
 	 */
 	public byte[] enhance(String className, byte[] originalBytes) throws EnhancementException {
 		final CtClass managedCtClass;
 		try {
 			managedCtClass = classPool.makeClassIfNew( new ByteArrayInputStream( originalBytes ) );
 		}
 		catch (IOException e) {
-			log.unableToBuildEnhancementMetamodel( className );
+			log.unableToBuildEnhancementMetamodel(className);
+			return originalBytes;
+		}
+
+		enhance(managedCtClass, false);
+
+        return getByteCode(managedCtClass);
+	}
+
+    public byte[] enhanceComposite(String className, byte[] originalBytes) throws EnhancementException {
+		final CtClass managedCtClass;
+		try {
+			managedCtClass = classPool.makeClassIfNew( new ByteArrayInputStream( originalBytes ) );
+		}
+		catch (IOException e) {
+			log.unableToBuildEnhancementMetamodel(className);
 			return originalBytes;
 		}
 
-		enhance( managedCtClass );
+		enhance(managedCtClass, true);
+
+        return getByteCode(managedCtClass);
+    }
 
+    private byte[] getByteCode(CtClass managedCtClass) {
 		final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
 		final DataOutputStream out;
 		try {
 			out = new DataOutputStream( byteStream );
 			try {
 				managedCtClass.toBytecode( out );
 				return byteStream.toByteArray();
 			}
 			finally {
 				try {
 					out.close();
 				}
 				catch (IOException e) {
 					//swallow
 				}
 			}
 		}
 		catch (Exception e) {
 			log.unableToTransformClass( e.getMessage() );
 			throw new HibernateException( "Unable to transform class: " + e.getMessage() );
 		}
-	}
+    }
 
-	private void enhance(CtClass managedCtClass) {
+	private void enhance(CtClass managedCtClass, boolean isComposite) {
+        this.isComposite = isComposite;
 		final String className = managedCtClass.getName();
 		log.debugf( "Enhancing %s", className );
 
 		// can't effectively enhance interfaces
 		if ( managedCtClass.isInterface() ) {
 			log.debug( "skipping enhancement : interface" );
 			return;
 		}
 
 		// skip already enhanced classes
 		final String[] interfaceNames = managedCtClass.getClassFile2().getInterfaces();
 		for ( String interfaceName : interfaceNames ) {
 			if ( ManagedEntity.class.getName().equals( interfaceName )
 					|| ManagedComposite.class.getName().equals( interfaceName ) ) {
 				log.debug( "skipping enhancement : already enhanced" );
 				return;
 			}
 		}
 
-		if ( enhancementContext.isEntityClass( managedCtClass ) ) {
+		if (!isComposite && enhancementContext.isEntityClass( managedCtClass ) ) {
 			enhanceAsEntity( managedCtClass );
 		}
-		else if ( enhancementContext.isCompositeClass( managedCtClass ) ) {
+		else if (isComposite || enhancementContext.isCompositeClass( managedCtClass ) ) {
 			enhanceAsComposite( managedCtClass );
 		}
 		else {
 			log.debug( "skipping enhancement : not entity or composite" );
 		}
 	}
 
 	private void enhanceAsEntity(CtClass managedCtClass) {
 		// add the ManagedEntity interface
 		managedCtClass.addInterface( managedEntityCtClass );
 
 		enhancePersistentAttributes( managedCtClass );
 
 		addEntityInstanceHandling( managedCtClass );
 		addEntityEntryHandling( managedCtClass );
 		addLinkedPreviousHandling( managedCtClass );
 		addLinkedNextHandling( managedCtClass );
 	}
 
 	private void enhanceAsComposite(CtClass managedCtClass) {
 		enhancePersistentAttributes( managedCtClass );
 	}
 
 	private void addEntityInstanceHandling(CtClass managedCtClass) {
 		// add the ManagedEntity#$$_hibernate_getEntityInstance method
 		try {
 			managedCtClass.addMethod(
 					CtNewMethod.make(
 							objectCtClass,
 							EnhancerConstants.ENTITY_INSTANCE_GETTER_NAME,
 							new CtClass[0],
 							new CtClass[0],
 							"{ return this; }",
 							managedCtClass
 					)
 			);
 		}
 		catch (CannotCompileException e) {
 			throw new EnhancementException(
 					String.format(
 							"Could not enhance entity class [%s] to add EntityEntry getter",
 							managedCtClass.getName()
 					),
 					e
 			);
 		}
 	}
 
 	private void addEntityEntryHandling(CtClass managedCtClass) {
 		addFieldWithGetterAndSetter(
 				managedCtClass,
 				entityEntryCtClass,
 				EnhancerConstants.ENTITY_ENTRY_FIELD_NAME,
 				EnhancerConstants.ENTITY_ENTRY_GETTER_NAME,
 				EnhancerConstants.ENTITY_ENTRY_SETTER_NAME
 		);
 	}
 
 	private void addLinkedPreviousHandling(CtClass managedCtClass) {
 		addFieldWithGetterAndSetter(
 				managedCtClass,
 				managedEntityCtClass,
 				EnhancerConstants.PREVIOUS_FIELD_NAME,
 				EnhancerConstants.PREVIOUS_GETTER_NAME,
 				EnhancerConstants.PREVIOUS_SETTER_NAME
 		);
 	}
 
 	private void addLinkedNextHandling(CtClass managedCtClass) {
 		addFieldWithGetterAndSetter(
 				managedCtClass,
 				managedEntityCtClass,
 				EnhancerConstants.NEXT_FIELD_NAME,
 				EnhancerConstants.NEXT_GETTER_NAME,
 				EnhancerConstants.NEXT_SETTER_NAME
 		);
 	}
 
 	private AnnotationsAttribute getVisibleAnnotations(FieldInfo fieldInfo) {
 		AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) fieldInfo.getAttribute( AnnotationsAttribute.visibleTag );
 		if ( annotationsAttribute == null ) {
 			annotationsAttribute = new AnnotationsAttribute( fieldInfo.getConstPool(), AnnotationsAttribute.visibleTag );
 			fieldInfo.addAttribute( annotationsAttribute );
 		}
 		return annotationsAttribute;
 	}
 
-	private void enhancePersistentAttributes(CtClass managedCtClass) {
+	private void enhancePersistentAttributes(CtClass managedCtClass ) {
 		addInterceptorHandling( managedCtClass );
 		if ( enhancementContext.doDirtyCheckingInline( managedCtClass ) ) {
 			addInLineDirtyHandling( managedCtClass );
 		}
 
 		final IdentityHashMap<String,PersistentAttributeDescriptor> attrDescriptorMap
 				= new IdentityHashMap<String, PersistentAttributeDescriptor>();
 
 		for ( CtField persistentField : collectPersistentFields( managedCtClass ) ) {
 			attrDescriptorMap.put(
 					persistentField.getName(),
 					enhancePersistentAttribute( managedCtClass, persistentField )
 			);
 		}
 
 		// lastly, find all references to the transformed fields and replace with calls to the added reader/writer
 		transformFieldAccessesIntoReadsAndWrites( managedCtClass, attrDescriptorMap );
 	}
 
 	private PersistentAttributeDescriptor enhancePersistentAttribute(CtClass managedCtClass, CtField persistentField) {
 		try {
 			final AttributeTypeDescriptor typeDescriptor = resolveAttributeTypeDescriptor( persistentField );
 			return new PersistentAttributeDescriptor(
 					persistentField,
 					generateFieldReader( managedCtClass, persistentField, typeDescriptor ),
 					generateFieldWriter( managedCtClass, persistentField, typeDescriptor ),
 					typeDescriptor
 			);
 		}
 		catch (Exception e) {
 			throw new EnhancementException(
 					String.format(
 							"Unable to enhance persistent attribute [%s:%s]",
 							managedCtClass.getName(),
 							persistentField.getName()
 					),
 					e
 			);
 		}
 	}
 
 	private CtField[] collectPersistentFields(CtClass managedCtClass) {
 		// todo : drive this from the Hibernate metamodel instance...
 
 		final List<CtField> persistentFieldList = new ArrayList<CtField>();
 		for ( CtField ctField : managedCtClass.getDeclaredFields() ) {
 			// skip static fields
 			if ( Modifier.isStatic( ctField.getModifiers() ) ) {
 				continue;
 			}
 			// skip fields added by enhancement
 			if ( ctField.getName().startsWith( "$" ) ) {
 				continue;
 			}
 			if ( enhancementContext.isPersistentField( ctField ) ) {
 				persistentFieldList.add( ctField );
 			}
 		}
 
 		return enhancementContext.order( persistentFieldList.toArray( new CtField[persistentFieldList.size()]) );
 	}
 
+    private List<CtField> collectCollectionFields(CtClass managedCtClass) {
+
+        final List<CtField> collectionList = new ArrayList<CtField>();
+        try {
+            for ( CtField ctField : managedCtClass.getDeclaredFields() ) {
+                // skip static fields
+                if ( Modifier.isStatic( ctField.getModifiers() ) ) {
+                    continue;
+                }
+                // skip fields added by enhancement
+                if ( ctField.getName().startsWith( "$" ) ) {
+                    continue;
+                }
+                if ( enhancementContext.isPersistentField( ctField ) ) {
+                    for(CtClass ctClass : ctField.getType().getInterfaces()) {
+                        if(ctClass.getName().equals("java.util.Collection")) {
+                            collectionList.add(ctField);
+                            break;
+                        }
+                    }
+                }
+            }
+        }
+        catch (NotFoundException ignored) {  }
+
+        return collectionList;
+    }
+
 	private void addInterceptorHandling(CtClass managedCtClass) {
 		// interceptor handling is only needed if either:
 		//		a) in-line dirty checking has *not* been requested
 		//		b) class has lazy-loadable attributes
 		if ( enhancementContext.doDirtyCheckingInline( managedCtClass )
 				&& ! enhancementContext.hasLazyLoadableAttributes( managedCtClass ) ) {
 			return;
 		}
 
 		log.debug( "Weaving in PersistentAttributeInterceptable implementation" );
 
 
 		// add in the PersistentAttributeInterceptable contract
 		managedCtClass.addInterface( attributeInterceptableCtClass );
 
 		addFieldWithGetterAndSetter(
 				managedCtClass,
 				attributeInterceptorCtClass,
 				EnhancerConstants.INTERCEPTOR_FIELD_NAME,
 				EnhancerConstants.INTERCEPTOR_GETTER_NAME,
 				EnhancerConstants.INTERCEPTOR_SETTER_NAME
 		);
 	}
 
-	private void addInLineDirtyHandling(CtClass managedCtClass) {
-		// todo : implement
-	}
+    private boolean isClassAlreadyTrackingDirtyStatus(CtClass managedCtClass) {
+        try {
+            for(CtClass ctInterface : managedCtClass.getInterfaces()) {
+                if(ctInterface.getName().equals(SelfDirtinessTracker.class.getName()))
+                    return true;
+            }
+        }
+        catch (NotFoundException e) {
+            e.printStackTrace();
+        }
+        return false;
+    }
+
+	private void addInLineDirtyHandling(CtClass managedCtClass ) {
+        try {
+
+            //create composite methods
+            if(isComposite) {
+                managedCtClass.addInterface(classPool.get("org.hibernate.engine.spi.CompositeTracker"));
+                CtClass compositeCtType = classPool.get("org.hibernate.bytecode.enhance.spi.CompositeOwnerTracker");
+                addField(managedCtClass, compositeCtType, EnhancerConstants.TRACKER_COMPOSITE_FIELD_NAME, true);
+                createCompositeTrackerMethod(managedCtClass);
+            }
+            // "normal" entity
+            else {
+                managedCtClass.addInterface(classPool.get("org.hibernate.engine.spi.SelfDirtinessTracker"));
+                CtClass trackerCtType = classPool.get("java.util.Set");
+                addField(managedCtClass, trackerCtType, EnhancerConstants.TRACKER_FIELD_NAME, true);
+
+                CtClass collectionTrackerCtType = classPool.get("org.hibernate.bytecode.enhance.spi.CollectionTracker");
+                addField(managedCtClass, collectionTrackerCtType, EnhancerConstants.TRACKER_COLLECTION_NAME, true);
+
+                createDirtyTrackerMethods(managedCtClass);
+            }
+
+
+        }
+        catch (NotFoundException e) {
+            e.printStackTrace();
+        }
+    }
+
+    /**
+     * Create all dirty tracker methods
+     */
+    private void createDirtyTrackerMethods(CtClass managedCtClass) {
+        try {
+            String trackerChangeMethod =
+                    "public void "+EnhancerConstants.TRACKER_CHANGER_NAME+"(String name) {" +
+                            "  if(" +EnhancerConstants.TRACKER_FIELD_NAME+ " == null) {" +
+                            "    "+EnhancerConstants.TRACKER_FIELD_NAME+ " = new java.util.HashSet();" +
+                            "  }" +
+                            "  if(!" +EnhancerConstants.TRACKER_FIELD_NAME+ ".contains(name)) {" +
+                            "    "+EnhancerConstants.TRACKER_FIELD_NAME+".add(name);" +
+                            "  }"+
+                            "}";
+            managedCtClass.addMethod(CtNewMethod.make(trackerChangeMethod, managedCtClass));
+
+            createCollectionDirtyCheckMethod(managedCtClass);
+            createCollectionDirtyCheckGetFieldsMethod(managedCtClass);
+            //createCompositeFieldsDirtyCheckMethod(managedCtClass);
+            //createGetCompositeDirtyFieldsMethod(managedCtClass);
+
+            createHasDirtyAttributesMethod(managedCtClass);
+
+            createClearDirtyCollectionMethod(managedCtClass);
+            createClearDirtyMethod(managedCtClass);
+
+           String trackerGetMethod =
+                    "public java.util.List "+EnhancerConstants.TRACKER_GET_NAME+"() { "+
+                            "if("+ EnhancerConstants.TRACKER_FIELD_NAME+" == null) "+
+                            EnhancerConstants.TRACKER_FIELD_NAME+" = new java.util.HashSet();"+
+                            EnhancerConstants.TRACKER_COLLECTION_CHANGED_FIELD_NAME+"("+
+                            EnhancerConstants.TRACKER_FIELD_NAME+");"+
+                            "return "+EnhancerConstants.TRACKER_FIELD_NAME+"; }";
+            CtMethod getMethod =  CtNewMethod.make(trackerGetMethod, managedCtClass);
+
+            MethodInfo methodInfo = getMethod.getMethodInfo();
+            SignatureAttribute signatureAttribute =
+                    new SignatureAttribute(methodInfo.getConstPool(), "()Ljava/util/Set<Ljava/lang/String;>;");
+            methodInfo.addAttribute(signatureAttribute);
+            managedCtClass.addMethod(getMethod);
+
+        }
+        catch (CannotCompileException e) {
+            e.printStackTrace();
+        }
+        catch (ClassNotFoundException e) {
+            e.printStackTrace();
+        }
+    }
+
+    private void createTrackChangeCompositeMethod(CtClass managedCtClass) {
+        StringBuilder builder = new StringBuilder();
+        builder.append("public void ")
+                .append(EnhancerConstants.TRACKER_CHANGER_NAME)
+                .append("(String name) {")
+                .append("if (")
+                .append(EnhancerConstants.TRACKER_COMPOSITE_FIELD_NAME)
+                .append(" != null) ")
+                .append(EnhancerConstants.TRACKER_COMPOSITE_FIELD_NAME)
+                .append(".callOwner(\".\"+name); }");
+
+        System.out.println("COMPOSITE METHOD: "+builder.toString());
+
+        try {
+            managedCtClass.addMethod(CtNewMethod.make(builder.toString(), managedCtClass));
+        }
+        catch (CannotCompileException e) {
+        }
+    }
+
+    private void createCompositeTrackerMethod(CtClass managedCtClass) {
+        try {
+            StringBuilder builder = new StringBuilder();
+            builder.append("public void ")
+                    .append(EnhancerConstants.TRACKER_COMPOSITE_SET_OWNER)
+                    .append("(String name, org.hibernate.engine.spi.CompositeOwner tracker) {")
+                    .append("if(")
+                    .append(EnhancerConstants.TRACKER_COMPOSITE_FIELD_NAME)
+                    .append(" == null) ")
+                    .append(EnhancerConstants.TRACKER_COMPOSITE_FIELD_NAME)
+                    .append(" = new org.hibernate.bytecode.enhance.spi.CompositeOwnerTracker();")
+                    .append(EnhancerConstants.TRACKER_COMPOSITE_FIELD_NAME)
+                    .append(".add(name, tracker); }");
+
+            managedCtClass.addMethod(CtNewMethod.make(builder.toString(), managedCtClass));
+
+            builder = new StringBuilder();
+            builder.append("public void ")
+                    .append(EnhancerConstants.TRACKER_COMPOSITE_CLEAR_OWNER)
+                    .append("(String name) {")
+                    .append(" if(")
+                    .append(EnhancerConstants.TRACKER_COMPOSITE_FIELD_NAME)
+                    .append(" != null)")
+                    .append(EnhancerConstants.TRACKER_COMPOSITE_FIELD_NAME)
+                    .append(".removeOwner(name);}");
+
+            managedCtClass.addMethod(CtNewMethod.make(builder.toString(), managedCtClass));
+        }
+        catch (CannotCompileException e) {
+            e.printStackTrace();
+        }
+    }
+
+    private void createHasDirtyAttributesMethod(CtClass managedCtClass) throws CannotCompileException {
+        String trackerHasChangedMethod =
+                "public boolean "+EnhancerConstants.TRACKER_HAS_CHANGED_NAME+"() { return ("+
+                        EnhancerConstants.TRACKER_FIELD_NAME+" != null && !" +
+                        EnhancerConstants.TRACKER_FIELD_NAME+".isEmpty()) || "+
+                        EnhancerConstants.TRACKER_COLLECTION_CHANGED_NAME+"(); } ";
+
+        managedCtClass.addMethod(CtNewMethod.make(trackerHasChangedMethod, managedCtClass));
+    }
+
+    /**
+     * Creates _clearDirtyAttributes
+     */
+    private void createClearDirtyMethod(CtClass managedCtClass) throws CannotCompileException, ClassNotFoundException {
+        StringBuilder builder = new StringBuilder();
+        builder.append("public void ")
+                .append( EnhancerConstants.TRACKER_CLEAR_NAME)
+                .append("() {")
+                .append("if (")
+                .append(EnhancerConstants.TRACKER_FIELD_NAME)
+                .append(" != null) ")
+                .append(EnhancerConstants.TRACKER_FIELD_NAME)
+                .append(".clear(); ")
+                .append(EnhancerConstants.TRACKER_COLLECTION_CLEAR_NAME)
+                .append("(); }");
+
+        managedCtClass.addMethod(CtNewMethod.make(builder.toString(), managedCtClass));
+    }
+
+    private void createClearDirtyCollectionMethod(CtClass managedCtClass) throws CannotCompileException {
+        StringBuilder builder = new StringBuilder();
+        builder.append("private void ")
+                .append(EnhancerConstants.TRACKER_COLLECTION_CLEAR_NAME)
+                .append("() { if(")
+                .append(EnhancerConstants.TRACKER_COLLECTION_NAME)
+                .append(" == null)")
+                .append(EnhancerConstants.TRACKER_COLLECTION_NAME)
+                .append(" = new org.hibernate.bytecode.enhance.spi.CollectionTracker();");
+
+        for(CtField ctField : collectCollectionFields(managedCtClass)) {
+            if(!enhancementContext.isMappedCollection(ctField)) {
+                builder.append("if(")
+                        .append(ctField.getName())
+                        .append(" != null) ")
+                        .append(EnhancerConstants.TRACKER_COLLECTION_NAME)
+                        .append(".add(\"")
+                        .append(ctField.getName())
+                        .append("\", ")
+                        .append(ctField.getName())
+                        .append(".size());");
+            }
+        }
+
+        builder.append("}");
+
+        managedCtClass.addMethod(CtNewMethod.make(builder.toString(), managedCtClass));
+    }
+
+    /**
+     * create _areCollectionFieldsDirty
+     */
+    private void createCollectionDirtyCheckMethod(CtClass managedCtClass) throws CannotCompileException {
+        StringBuilder builder = new StringBuilder("private boolean ")
+                .append(EnhancerConstants.TRACKER_COLLECTION_CHANGED_NAME)
+                .append("() { if ($$_hibernate_getInterceptor() == null || ")
+                .append(EnhancerConstants.TRACKER_COLLECTION_NAME)
+                .append(" == null) return false; ");
+
+        for(CtField ctField : collectCollectionFields(managedCtClass)) {
+            if(!enhancementContext.isMappedCollection(ctField)) {
+                builder.append("if(")
+                        .append(EnhancerConstants.TRACKER_COLLECTION_NAME)
+                        .append(".getSize(\"")
+                        .append(ctField.getName())
+                        .append("\") != ")
+                        .append(ctField.getName())
+                        .append(".size()) return true;");
+            }
+        }
+
+        builder.append("return false; }");
+
+        managedCtClass.addMethod(CtNewMethod.make(builder.toString(), managedCtClass));
+    }
+
+    /**
+     * create _getCollectionFieldDirtyNames
+     */
+    private void createCollectionDirtyCheckGetFieldsMethod(CtClass managedCtClass) throws CannotCompileException {
+              StringBuilder collectionFieldDirtyFieldMethod = new StringBuilder("private void ")
+                      .append(EnhancerConstants.TRACKER_COLLECTION_CHANGED_FIELD_NAME)
+                      .append("(java.util.Set trackerSet) { if(")
+                      .append(EnhancerConstants.TRACKER_COLLECTION_NAME)
+                      .append(" == null) return; else {");
+
+        for(CtField ctField : collectCollectionFields(managedCtClass)) {
+            if(!ctField.getName().startsWith("$$_hibernate") &&
+                    !enhancementContext.isMappedCollection(ctField)) {
+                collectionFieldDirtyFieldMethod
+                        .append("if(")
+                        .append(EnhancerConstants.TRACKER_COLLECTION_NAME)
+                        .append(".getSize(\"")
+                        .append(ctField.getName())
+                        .append("\") != ")
+                        .append(ctField.getName())
+                        .append(".size()) trackerSet.add(\"")
+                        .append(ctField.getName())
+                        .append("\");");
+            }
+        }
+
+        collectionFieldDirtyFieldMethod.append("}}");
+
+        managedCtClass.addMethod(CtNewMethod.make(collectionFieldDirtyFieldMethod.toString(), managedCtClass));
+    }
 
 	private void addFieldWithGetterAndSetter(
 			CtClass targetClass,
 			CtClass fieldType,
 			String fieldName,
 			String getterName,
 			String setterName) {
 		final CtField theField = addField( targetClass, fieldType, fieldName, true );
 		addGetter( targetClass, theField, getterName );
 		addSetter( targetClass, theField, setterName );
 	}
 
 	private CtField addField(CtClass targetClass, CtClass fieldType, String fieldName, boolean makeTransient) {
 		final ConstPool constPool = targetClass.getClassFile().getConstPool();
 
 		final CtField theField;
 		try {
 			theField = new CtField( fieldType, fieldName, targetClass );
 			targetClass.addField( theField );
 		}
 		catch (CannotCompileException e) {
 			throw new EnhancementException(
 					String.format(
 							"Could not enhance class [%s] to add field [%s]",
 							targetClass.getName(),
 							fieldName
 					),
 					e
 			);
 		}
 
 		// make that new field (1) private, (2) transient and (3) @Transient
 		if ( makeTransient ) {
 			theField.setModifiers( theField.getModifiers() | Modifier.TRANSIENT );
 		}
 		theField.setModifiers( Modifier.setPrivate( theField.getModifiers() ) );
 
 		final AnnotationsAttribute annotationsAttribute = getVisibleAnnotations( theField.getFieldInfo() );
 		annotationsAttribute.addAnnotation( new Annotation( Transient.class.getName(), constPool ) );
 		return theField;
 	}
 
 	private void addGetter(CtClass targetClass, CtField theField, String getterName) {
 		try {
 			targetClass.addMethod( CtNewMethod.getter( getterName, theField ) );
 		}
 		catch (CannotCompileException e) {
 			throw new EnhancementException(
 					String.format(
 							"Could not enhance entity class [%s] to add getter method [%s]",
 							targetClass.getName(),
 							getterName
 					),
 					e
 			);
 		}
 	}
 
 	private void addSetter(CtClass targetClass, CtField theField, String setterName) {
 		try {
 			targetClass.addMethod( CtNewMethod.setter( setterName, theField ) );
 		}
 		catch (CannotCompileException e) {
 			throw new EnhancementException(
 					String.format(
 							"Could not enhance entity class [%s] to add setter method [%s]",
 							targetClass.getName(),
 							setterName
 					),
 					e
 			);
 		}
 	}
 
 	private CtMethod generateFieldReader(
 			CtClass managedCtClass,
 			CtField persistentField,
 			AttributeTypeDescriptor typeDescriptor)
 			throws BadBytecode, CannotCompileException {
 
 		final FieldInfo fieldInfo = persistentField.getFieldInfo();
 		final String fieldName = fieldInfo.getName();
 		final String readerName = EnhancerConstants.PERSISTENT_FIELD_READER_PREFIX + fieldName;
 
 		// read attempts only have to deal lazy-loading support, not dirty checking; so if the field
 		// is not enabled as lazy-loadable return a plain simple getter as the reader
 		if ( ! enhancementContext.isLazyLoadable( persistentField ) ) {
 			// not lazy-loadable...
 			// EARLY RETURN!!!
 			try {
 				final CtMethod reader = CtNewMethod.getter( readerName, persistentField );
 				managedCtClass.addMethod( reader );
 				return reader;
 			}
 			catch (CannotCompileException e) {
 				throw new EnhancementException(
 						String.format(
 								"Could not enhance entity class [%s] to add field reader method [%s]",
 								managedCtClass.getName(),
 								readerName
 						),
 						e
 				);
 			}
 		}
 
 		// temporary solution...
 		final String methodBody = typeDescriptor.buildReadInterceptionBodyFragment( fieldName )
 				+ " return this." + fieldName + ";";
 
 		try {
 			final CtMethod reader = CtNewMethod.make(
 					Modifier.PRIVATE,
 					persistentField.getType(),
 					readerName,
 					null,
 					null,
 					"{" + methodBody + "}",
 					managedCtClass
 			);
 			managedCtClass.addMethod( reader );
 			return reader;
 		}
 		catch (Exception e) {
 			throw new EnhancementException(
 					String.format(
 							"Could not enhance entity class [%s] to add field reader method [%s]",
 							managedCtClass.getName(),
 							readerName
 					),
 					e
 			);
 		}
 	}
 
 	private CtMethod generateFieldWriter(
 			CtClass managedCtClass,
 			CtField persistentField,
 			AttributeTypeDescriptor typeDescriptor) {
 
 		final FieldInfo fieldInfo = persistentField.getFieldInfo();
 		final String fieldName = fieldInfo.getName();
 		final String writerName = EnhancerConstants.PERSISTENT_FIELD_WRITER_PREFIX + fieldName;
 
 		final CtMethod writer;
 
 		try {
 			if ( ! enhancementContext.isLazyLoadable( persistentField ) ) {
 				// not lazy-loadable...
 				writer = CtNewMethod.setter( writerName, persistentField );
 			}
 			else {
 				final String methodBody = typeDescriptor.buildWriteInterceptionBodyFragment( fieldName );
 				writer = CtNewMethod.make(
 						Modifier.PRIVATE,
 						CtClass.voidType,
 						writerName,
 						new CtClass[] { persistentField.getType() },
 						null,
 						"{" + methodBody + "}",
 						managedCtClass
 				);
 			}
 
-			if ( enhancementContext.doDirtyCheckingInline( managedCtClass ) ) {
-				writer.insertBefore( typeDescriptor.buildInLineDirtyCheckingBodyFragment( fieldName ) );
+			if ( enhancementContext.doDirtyCheckingInline( managedCtClass ) && !isComposite ) {
+                writer.insertBefore( typeDescriptor.buildInLineDirtyCheckingBodyFragment( persistentField  ));
 			}
 
+            if( isComposite) {
+                StringBuilder builder = new StringBuilder();
+                builder.append(" if(  ")
+                        .append(EnhancerConstants.TRACKER_COMPOSITE_FIELD_NAME)
+                        .append(" != null) ")
+                        .append(EnhancerConstants.TRACKER_COMPOSITE_FIELD_NAME)
+                        .append(".callOwner(\".")
+                        .append(persistentField.getName())
+                        .append("\");");
+
+                writer.insertBefore( builder.toString() );
+            }
+
+            //composite types
+            if(persistentField.getAnnotation(Embedded.class) != null) {
+                //make sure to add the CompositeOwner interface
+                if(!doClassInheritCompositeOwner(managedCtClass)) {
+                    managedCtClass.addInterface(classPool.get("org.hibernate.engine.spi.CompositeOwner"));
+                }
+                //if a composite have a embedded field we need to implement the method as well
+                if(isComposite)
+                    createTrackChangeCompositeMethod(managedCtClass);
+
+
+                writer.insertBefore( cleanupPreviousOwner(persistentField));
+
+                writer.insertAfter( compositeMethodBody(persistentField));
+            }
+
 			managedCtClass.addMethod( writer );
 			return writer;
 		}
 		catch (Exception e) {
 			throw new EnhancementException(
 					String.format(
 							"Could not enhance entity class [%s] to add field writer method [%s]",
 							managedCtClass.getName(),
 							writerName
 					),
 					e
 			);
 		}
 	}
 
+    private boolean doClassInheritCompositeOwner(CtClass managedCtClass) {
+        try {
+            for(CtClass ctClass : managedCtClass.getInterfaces())
+                if(ctClass.getName().equals("org.hibernate.engine.spi.CompositeOwner"))
+                    return true;
+
+            return false;
+        }
+        catch (NotFoundException e) {
+            return false;
+        }
+    }
+
+    private String cleanupPreviousOwner(CtField currentValue) {
+        StringBuilder builder = new StringBuilder();
+        builder.append("if (")
+                .append(currentValue.getName())
+                .append(" != null) ")
+                .append("((org.hibernate.engine.spi.CompositeTracker)")
+                .append(currentValue.getName())
+                .append(").")
+                .append(EnhancerConstants.TRACKER_COMPOSITE_CLEAR_OWNER)
+                .append("(\"")
+                .append(currentValue.getName())
+                .append("\");");
+
+        return  builder.toString();
+    }
+
+    private String compositeMethodBody(CtField currentValue) {
+        StringBuilder builder = new StringBuilder();
+        builder.append("((org.hibernate.engine.spi.CompositeTracker) ")
+                .append(currentValue.getName())
+                .append(").$$_hibernate_setOwner(\"")
+                .append(currentValue.getName())
+                .append("\",(org.hibernate.engine.spi.CompositeOwner) this);")
+                .append(EnhancerConstants.TRACKER_CHANGER_NAME + "(\"" + currentValue.getName() + "\");");
+
+        return builder.toString();
+    }
+
 	private void transformFieldAccessesIntoReadsAndWrites(
 			CtClass managedCtClass,
 			IdentityHashMap<String, PersistentAttributeDescriptor> attributeDescriptorMap) {
 
 		final ConstPool constPool = managedCtClass.getClassFile().getConstPool();
 
 		for ( Object oMethod : managedCtClass.getClassFile().getMethods() ) {
 			final MethodInfo methodInfo = (MethodInfo) oMethod;
 			final String methodName = methodInfo.getName();
 
 			// skip methods added by enhancement
 			if ( methodName.startsWith( EnhancerConstants.PERSISTENT_FIELD_READER_PREFIX )
 					|| methodName.startsWith( EnhancerConstants.PERSISTENT_FIELD_WRITER_PREFIX )
 					|| methodName.equals( EnhancerConstants.ENTITY_INSTANCE_GETTER_NAME )
 					|| methodName.equals( EnhancerConstants.ENTITY_ENTRY_GETTER_NAME )
 					|| methodName.equals( EnhancerConstants.ENTITY_ENTRY_SETTER_NAME )
 					|| methodName.equals( EnhancerConstants.PREVIOUS_GETTER_NAME )
 					|| methodName.equals( EnhancerConstants.PREVIOUS_SETTER_NAME )
 					|| methodName.equals( EnhancerConstants.NEXT_GETTER_NAME )
 					|| methodName.equals( EnhancerConstants.NEXT_SETTER_NAME ) ) {
 				continue;
 			}
 
 			final CodeAttribute codeAttr = methodInfo.getCodeAttribute();
 			if ( codeAttr == null ) {
 				// would indicate an abstract method, continue to next method
 				continue;
 			}
 
 			try {
 				final CodeIterator itr = codeAttr.iterator();
 				while ( itr.hasNext() ) {
 					final int index = itr.next();
 					final int op = itr.byteAt( index );
 					if ( op != Opcode.PUTFIELD && op != Opcode.GETFIELD ) {
 						continue;
 					}
 
 					final int constIndex = itr.u16bitAt( index+1 );
 
 					final String fieldName = constPool.getFieldrefName( constIndex );
 					final PersistentAttributeDescriptor attributeDescriptor = attributeDescriptorMap.get( fieldName );
 
 					if ( attributeDescriptor == null ) {
 						// its not a field we have enhanced for interception, so skip it
 						continue;
 					}
 
 					log.tracef(
 							"Transforming access to field [%s] from method [%s]",
 							fieldName,
 							methodName
 					);
 
 					if ( op == Opcode.GETFIELD ) {
 						final int readMethodIndex = constPool.addMethodrefInfo(
 								constPool.getThisClassInfo(),
 								attributeDescriptor.getReader().getName(),
 								attributeDescriptor.getReader().getSignature()
 						);
 						itr.writeByte( Opcode.INVOKESPECIAL, index );
 						itr.write16bit( readMethodIndex, index+1 );
 					}
 					else {
 						final int writeMethodIndex = constPool.addMethodrefInfo(
 								constPool.getThisClassInfo(),
 								attributeDescriptor.getWriter().getName(),
 								attributeDescriptor.getWriter().getSignature()
 						);
 						itr.writeByte( Opcode.INVOKESPECIAL, index );
 						itr.write16bit( writeMethodIndex, index+1 );
 					}
 				}
 
 				final StackMapTable smt = MapMaker.make( classPool, methodInfo );
 				methodInfo.getCodeAttribute().setAttribute( smt );
 			}
 			catch (BadBytecode e) {
 				throw new EnhancementException(
 						"Unable to perform field access transformation in method : " + methodName,
 						e
 				);
 			}
 		}
 	}
 
 	private static class PersistentAttributeDescriptor {
 		private final CtField field;
 		private final CtMethod reader;
 		private final CtMethod writer;
 		private final AttributeTypeDescriptor typeDescriptor;
 
 		private PersistentAttributeDescriptor(
 				CtField field,
 				CtMethod reader,
 				CtMethod writer,
 				AttributeTypeDescriptor typeDescriptor) {
 			this.field = field;
 			this.reader = reader;
 			this.writer = writer;
 			this.typeDescriptor = typeDescriptor;
 		}
 
 		public CtField getField() {
 			return field;
 		}
 
 		public CtMethod getReader() {
 			return reader;
 		}
 
 		public CtMethod getWriter() {
 			return writer;
 		}
 
 		public AttributeTypeDescriptor getTypeDescriptor() {
 			return typeDescriptor;
 		}
 	}
 
 	private static interface AttributeTypeDescriptor {
 		public String buildReadInterceptionBodyFragment(String fieldName);
 		public String buildWriteInterceptionBodyFragment(String fieldName);
-		public String buildInLineDirtyCheckingBodyFragment(String fieldName);
+		public String buildInLineDirtyCheckingBodyFragment(CtField currentField);
 	}
 
 	private AttributeTypeDescriptor resolveAttributeTypeDescriptor(CtField persistentField) throws NotFoundException {
 		// for now cheat... we know we only have Object fields
 		if ( persistentField.getType() == CtClass.booleanType ) {
 			return BOOLEAN_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.byteType ) {
 			return BYTE_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.charType ) {
 			return CHAR_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.shortType ) {
 			return SHORT_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.intType ) {
 			return INT_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.longType ) {
 			return LONG_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.doubleType ) {
 			return DOUBLE_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.floatType ) {
 			return FLOAT_DESCRIPTOR;
 		}
 		else {
 			return new ObjectAttributeTypeDescriptor( persistentField.getType() );
 		}
 	}
 
 	private abstract static class AbstractAttributeTypeDescriptor implements AttributeTypeDescriptor {
 		@Override
-		public String buildInLineDirtyCheckingBodyFragment(String fieldName) {
-			// for now...
-			// todo : hook-in in-lined dirty checking
-			return String.format(
-					"System.out.println( \"DIRTY CHECK (%1$s) : \" + this.%1$s + \" -> \" + $1 + \" (dirty=\" + (this.%1$s != $1) +\")\" );",
-					fieldName
-			);
-		}
+		public String buildInLineDirtyCheckingBodyFragment(CtField currentValue) {
+            StringBuilder builder = new StringBuilder();
+            try {
+                //should ignore primary keys
+                for(Object o : currentValue.getType().getAnnotations()) {
+                    if(o instanceof Id)
+                        return "";
+                }
+
+                builder.append(entityMethodBody(currentValue));
+
+
+            }
+            catch (ClassNotFoundException e) {
+                e.printStackTrace();
+            }
+            catch (NotFoundException e) {
+                e.printStackTrace();
+            }
+            return builder.toString();
+		}
+
+        private String entityMethodBody(CtField currentValue) {
+            StringBuilder inlineBuilder = new StringBuilder();
+            try {
+                inlineBuilder.append("if ( $$_hibernate_getInterceptor() != null ");
+                //primitives || enums
+                if(currentValue.getType().isPrimitive() || currentValue.getType().isEnum()) {
+                    inlineBuilder.append("&& "+currentValue.getName()+" != $1)");
+                }
+                //simple data types
+                else if(currentValue.getType().getName().startsWith("java.lang") ||
+                        currentValue.getType().getName().startsWith("java.math.Big") ||
+                        currentValue.getType().getName().startsWith("java.sql.Time") ||
+                        currentValue.getType().getName().startsWith("java.sql.Date") ||
+                        currentValue.getType().getName().startsWith("java.util.Date") ||
+                        currentValue.getType().getName().startsWith("java.util.Calendar")
+                        ) {
+                    inlineBuilder.append("&& (("+currentValue.getName()+" == null) || (!" +currentValue.getName()+".equals( $1))))");
+                }
+                //all other objects
+                else {
+                    //if the field is a collection we return since we handle that in a separate method
+                    for(CtClass ctClass : currentValue.getType().getInterfaces()) {
+                        if(ctClass.getName().equals("java.util.Collection")) {
+
+                            //if the collection is not managed we should write it to the tracker
+                            //todo: should use EnhancementContext.isMappedCollection here instead
+                            if (currentValue.getAnnotation(OneToMany.class) != null ||
+                                    currentValue.getAnnotation(ManyToMany.class) != null ||
+                                    currentValue.getAnnotation(ElementCollection.class) != null) {
+                                return "";
+                            }
+                        }
+                    }
+
+                    //todo: for now just call equals, should probably do something else here
+                    inlineBuilder.append("&& (("+currentValue.getName()+" == null) || (!" +currentValue.getName()+".equals( $1))))");
+                }
+
+                inlineBuilder.append( EnhancerConstants.TRACKER_CHANGER_NAME+"(\""+currentValue.getName()+"\");");
+            } catch (NotFoundException e) {
+                e.printStackTrace();
+            } catch (ClassNotFoundException e) {
+                e.printStackTrace();
+            }
+            return inlineBuilder.toString();
+        }
 	}
 
 	private static class ObjectAttributeTypeDescriptor extends AbstractAttributeTypeDescriptor {
 		private final CtClass concreteType;
 
 		private ObjectAttributeTypeDescriptor(CtClass concreteType) {
 			this.concreteType = concreteType;
 		}
 
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 							"this.%1$s = (%2$s) $$_hibernate_getInterceptor().readObject(this, \"%1$s\", this.%1$s); " +
 							"}",
 					fieldName,
 					concreteType.getName()
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"%2$s localVar = $1;" +
 							"if ( $$_hibernate_getInterceptor() != null ) {" +
 							"localVar = (%2$s) $$_hibernate_getInterceptor().writeObject(this, \"%1$s\", this.%1$s, $1);" +
 							"}" +
 							"this.%1$s = localVar;",
 					fieldName,
 					concreteType.getName()
 			);
 		}
 	}
 
 	private static final AttributeTypeDescriptor BOOLEAN_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 							"this.%1$s = $$_hibernate_getInterceptor().readBoolean(this, \"%1$s\", this.%1$s); " +
 							"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"boolean localVar = $1;" +
 							"if ( $$_hibernate_getInterceptor() != null ) {" +
 							"localVar = $$_hibernate_getInterceptor().writeBoolean(this, \"%1$s\", this.%1$s, $1);" +
 							"}" +
 							"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor BYTE_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 							"this.%1$s = $$_hibernate_getInterceptor().readByte(this, \"%1$s\", this.%1$s); " +
 							"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"byte localVar = $1;" +
 							"if ( $$_hibernate_getInterceptor() != null ) {" +
 							"localVar = $$_hibernate_getInterceptor().writeByte(this, \"%1$s\", this.%1$s, $1);" +
 							"}" +
 							"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor CHAR_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 						"this.%1$s = $$_hibernate_getInterceptor().readChar(this, \"%1$s\", this.%1$s); " +
 					"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"char localVar = $1;" +
 					"if ( $$_hibernate_getInterceptor() != null ) {" +
 						"localVar = $$_hibernate_getInterceptor().writeChar(this, \"%1$s\", this.%1$s, $1);" +
 					"}" +
 					"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor SHORT_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 						"this.%1$s = $$_hibernate_getInterceptor().readShort(this, \"%1$s\", this.%1$s); " +
 					"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"short localVar = $1;" +
 					"if ( $$_hibernate_getInterceptor() != null ) {" +
 						"localVar = $$_hibernate_getInterceptor().writeShort(this, \"%1$s\", this.%1$s, $1);" +
 					"}" +
 					"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor INT_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 						"this.%1$s = $$_hibernate_getInterceptor().readInt(this, \"%1$s\", this.%1$s); " +
 					"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"int localVar = $1;" +
 					"if ( $$_hibernate_getInterceptor() != null ) {" +
 						"localVar = $$_hibernate_getInterceptor().writeInt(this, \"%1$s\", this.%1$s, $1);" +
 					"}" +
 					"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor LONG_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 						"this.%1$s = $$_hibernate_getInterceptor().readLong(this, \"%1$s\", this.%1$s); " +
 					"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"long localVar = $1;" +
 					"if ( $$_hibernate_getInterceptor() != null ) {" +
 						"localVar = $$_hibernate_getInterceptor().writeLong(this, \"%1$s\", this.%1$s, $1);" +
 					"}" +
 					"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor DOUBLE_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 						"this.%1$s = $$_hibernate_getInterceptor().readDouble(this, \"%1$s\", this.%1$s); " +
 					"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"double localVar = $1;" +
 					"if ( $$_hibernate_getInterceptor() != null ) {" +
 						"localVar = $$_hibernate_getInterceptor().writeDouble(this, \"%1$s\", this.%1$s, $1);" +
 					"}" +
 					"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor FLOAT_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 						"this.%1$s = $$_hibernate_getInterceptor().readFloat(this, \"%1$s\", this.%1$s); " +
 					"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"float localVar = $1;" +
 					"if ( $$_hibernate_getInterceptor() != null ) {" +
 						"localVar = $$_hibernate_getInterceptor().writeFloat(this, \"%1$s\", this.%1$s, $1);" +
 					"}" +
 					"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancerConstants.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancerConstants.java
index 5c80f7a068..89ad0dc9bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancerConstants.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancerConstants.java
@@ -1,133 +1,181 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.enhance.spi;
 
 /**
  * Constants used during enhancement.
  *
  * @author Steve Ebersole
  */
 public class EnhancerConstants {
 	/**
 	 * Prefix for persistent-field reader methods.
 	 */
 	public static final String PERSISTENT_FIELD_READER_PREFIX = "$$_hibernate_read_";
 
 	/**
 	 * Prefix for persistent-field writer methods.
 	 */
 	public static final String PERSISTENT_FIELD_WRITER_PREFIX = "$$_hibernate_write_";
 
 	/**
 	 * Name of the method used to get reference the the entity instance (this in the case of enhanced classes).
 	 */
 	public static final String ENTITY_INSTANCE_GETTER_NAME = "$$_hibernate_getEntityInstance";
 
 	/**
 	 * Name of the field used to hold the {@link org.hibernate.engine.spi.EntityEntry}
 	 */
 	public static final String ENTITY_ENTRY_FIELD_NAME = "$$_hibernate_entityEntryHolder";
 
 	/**
 	 * Name of the method used to read the {@link org.hibernate.engine.spi.EntityEntry} field.
 	 *
 	 * @see #ENTITY_ENTRY_FIELD_NAME
 	 */
 	public static final String ENTITY_ENTRY_GETTER_NAME = "$$_hibernate_getEntityEntry";
 
 	/**
 	 * Name of the method used to write the {@link org.hibernate.engine.spi.EntityEntry} field.
 	 *
 	 * @see #ENTITY_ENTRY_FIELD_NAME
 	 */
 	public static final String ENTITY_ENTRY_SETTER_NAME = "$$_hibernate_setEntityEntry";
 
 	/**
 	 * Name of the field used to hold the previous {@link org.hibernate.engine.spi.ManagedEntity}.
 	 *
 	 * Together, previous/next are used to define a "linked list"
 	 *
 	 * @see #NEXT_FIELD_NAME
 	 */
 	public static final String PREVIOUS_FIELD_NAME = "$$_hibernate_previousManagedEntity";
 
 	/**
 	 * Name of the method used to read the previous {@link org.hibernate.engine.spi.ManagedEntity} field
 	 *
 	 * @see #PREVIOUS_FIELD_NAME
 	 */
 	public static final String PREVIOUS_GETTER_NAME = "$$_hibernate_getPreviousManagedEntity";
 
 	/**
 	 * Name of the method used to write the previous {@link org.hibernate.engine.spi.ManagedEntity} field
 	 *
 	 * @see #PREVIOUS_FIELD_NAME
 	 */
 	public static final String PREVIOUS_SETTER_NAME = "$$_hibernate_setPreviousManagedEntity";
 
 	/**
 	 * Name of the field used to hold the previous {@link org.hibernate.engine.spi.ManagedEntity}.
 	 *
 	 * Together, previous/next are used to define a "linked list"
 	 *
 	 * @see #PREVIOUS_FIELD_NAME
 	 */
 	public static final String NEXT_FIELD_NAME = "$$_hibernate_nextManagedEntity";
 
 	/**
 	 * Name of the method used to read the next {@link org.hibernate.engine.spi.ManagedEntity} field
 	 *
 	 * @see #NEXT_FIELD_NAME
 	 */
 	public static final String NEXT_GETTER_NAME = "$$_hibernate_getNextManagedEntity";
 
 	/**
 	 * Name of the method used to write the next {@link org.hibernate.engine.spi.ManagedEntity} field
 	 *
 	 * @see #NEXT_FIELD_NAME
 	 */
 	public static final String NEXT_SETTER_NAME = "$$_hibernate_setNextManagedEntity";
 
 	/**
 	 * Name of the field used to store the {@link org.hibernate.engine.spi.PersistentAttributeInterceptable}.
 	 */
 	public static final String INTERCEPTOR_FIELD_NAME = "$$_hibernate_attributeInterceptor";
 
 	/**
 	 * Name of the method used to read the interceptor
 	 *
 	 * @see #INTERCEPTOR_FIELD_NAME
 	 */
 	public static final String INTERCEPTOR_GETTER_NAME = "$$_hibernate_getInterceptor";
 
 	/**
 	 * Name of the method used to write the interceptor
 	 *
 	 * @see #INTERCEPTOR_FIELD_NAME
 	 */
 	public static final String INTERCEPTOR_SETTER_NAME = "$$_hibernate_setInterceptor";
 
+    /**
+     * Name of tracker field
+     */
+    public static final String TRACKER_FIELD_NAME = "$$_hibernate_tracker";
+
+    /**
+     * Name of method that add changed fields
+     */
+    public static final String TRACKER_CHANGER_NAME = "$$_hibernate_trackChange";
+
+    /**
+     * Name of method to see if any fields has changed
+     */
+    public static final String TRACKER_HAS_CHANGED_NAME = "$$_hibernate_hasDirtyAttributes";
+
+    /**
+     * Name of method to fetch dirty attributes
+     */
+    public static final String TRACKER_GET_NAME = "$$_hibernate_getDirtyAttributes";
+
+    /**
+     * Name of method to clear stored dirty attributes
+     */
+    public static final String TRACKER_CLEAR_NAME = "$$_hibernate_clearDirtyAttributes";
+
+    /**
+     * Name of method to check if collection fields are dirty
+     */
+    public static final String TRACKER_COLLECTION_CHANGED_NAME = "$$_hibernate_areCollectionFieldsDirty";
+
+    public static final String TRACKER_COLLECTION_NAME = "$$_hibernate_collectionTracker";
+    /**
+     * Name of method to get dirty collection field names
+     */
+    public static final String TRACKER_COLLECTION_CHANGED_FIELD_NAME = "$$_hibernate_getCollectionFieldDirtyNames";
+
+    public static final String TRACKER_COLLECTION_CLEAR_NAME = "$$_hibernate_clearDirtyCollectionNames";
+
+    public static final String TRACKER_COMPOSITE_DIRTY_CHECK = "$$_hibernate_areCompositeFieldsDirty";
+
+    public static final String TRACKER_COMPOSITE_DIRTY_FIELDS_GETTER = "$$_hibernate_getCompositeDirtyFields";
+
+    public static final String TRACKER_COMPOSITE_FIELD_NAME = "$$_hibernate_compositeOwners";
+
+    public static final String TRACKER_COMPOSITE_SET_OWNER = "$$_hibernate_setOwner";
+
+    public static final String TRACKER_COMPOSITE_CLEAR_OWNER = "$$_hibernate_clearOwner";
+
 	private EnhancerConstants() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/CompositeOwner.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/CompositeOwner.java
new file mode 100644
index 0000000000..6aab35f972
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CompositeOwner.java
@@ -0,0 +1,35 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.engine.spi;
+
+/**
+ * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
+ */
+public interface CompositeOwner {
+
+    /**
+     * @param attributeName to be added to the dirty list
+     */
+    void $$_hibernate_trackChange(String attributeName);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/CompositeTracker.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/CompositeTracker.java
new file mode 100644
index 0000000000..a524d5fc7c
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CompositeTracker.java
@@ -0,0 +1,34 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.engine.spi;
+
+/**
+ * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
+ */
+public interface CompositeTracker {
+
+    void $$_hibernate_setOwner(String name, CompositeOwner tracker);
+
+    void $$_hibernate_clearOwner(String name);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java
index 05ee64c067..bd7be01a68 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java
@@ -1,451 +1,458 @@
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
 package org.hibernate.engine.spi;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.Session;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * We need an entry to tell us all about the current state of an object with respect to its persistent state
  * 
  * @author Gavin King
  */
 public final class EntityEntry implements Serializable {
 	private LockMode lockMode;
 	private Status status;
 	private Status previousStatus;
 	private final Serializable id;
 	private Object[] loadedState;
 	private Object[] deletedState;
 	private boolean existsInDatabase;
 	private Object version;
 	private transient EntityPersister persister; // for convenience to save some lookups
 	private final EntityMode entityMode;
 	private final String tenantId;
 	private final String entityName;
 	private transient EntityKey cachedEntityKey; // cached EntityKey (lazy-initialized)
 	private boolean isBeingReplicated;
 	private boolean loadedWithLazyPropertiesUnfetched; //NOTE: this is not updated when properties are fetched lazily!
 	private final transient Object rowId;
 	private final transient PersistenceContext persistenceContext;
 
 	public EntityEntry(
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final EntityMode entityMode,
 			final String tenantId,
 			final boolean disableVersionIncrement,
 			final boolean lazyPropertiesAreUnfetched,
 			final PersistenceContext persistenceContext) {
 		this.status = status;
 		this.previousStatus = null;
 		// only retain loaded state if the status is not Status.READ_ONLY
 		if ( status != Status.READ_ONLY ) {
 			this.loadedState = loadedState;
 		}
 		this.id=id;
 		this.rowId=rowId;
 		this.existsInDatabase=existsInDatabase;
 		this.version=version;
 		this.lockMode=lockMode;
 		this.isBeingReplicated=disableVersionIncrement;
 		this.loadedWithLazyPropertiesUnfetched = lazyPropertiesAreUnfetched;
 		this.persister=persister;
 		this.entityMode = entityMode;
 		this.tenantId = tenantId;
 		this.entityName = persister == null ? null : persister.getEntityName();
 		this.persistenceContext = persistenceContext;
 	}
 
 	/**
 	 * This for is used during custom deserialization handling
 	 */
 	@SuppressWarnings( {"JavaDoc"})
 	private EntityEntry(
 			final SessionFactoryImplementor factory,
 			final String entityName,
 			final Serializable id,
 			final EntityMode entityMode,
 			final String tenantId,
 			final Status status,
 			final Status previousStatus,
 			final Object[] loadedState,
 	        final Object[] deletedState,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final boolean isBeingReplicated,
 			final boolean loadedWithLazyPropertiesUnfetched,
 			final PersistenceContext persistenceContext) {
 		this.entityName = entityName;
 		this.persister = ( factory == null ? null : factory.getEntityPersister( entityName ) );
 		this.id = id;
 		this.entityMode = entityMode;
 		this.tenantId = tenantId;
 		this.status = status;
 		this.previousStatus = previousStatus;
 		this.loadedState = loadedState;
 		this.deletedState = deletedState;
 		this.version = version;
 		this.lockMode = lockMode;
 		this.existsInDatabase = existsInDatabase;
 		this.isBeingReplicated = isBeingReplicated;
 		this.loadedWithLazyPropertiesUnfetched = loadedWithLazyPropertiesUnfetched;
 		this.rowId = null; // this is equivalent to the old behavior...
 		this.persistenceContext = persistenceContext;
 	}
 
 	public LockMode getLockMode() {
 		return lockMode;
 	}
 
 	public void setLockMode(LockMode lockMode) {
 		this.lockMode = lockMode;
 	}
 
 	public Status getStatus() {
 		return status;
 	}
 
 	public void setStatus(Status status) {
 		if (status==Status.READ_ONLY) {
 			loadedState = null; //memory optimization
 		}
 		if ( this.status != status ) {
 			this.previousStatus = this.status;
 			this.status = status;
 		}
 	}
 
 	public Serializable getId() {
 		return id;
 	}
 
 	public Object[] getLoadedState() {
 		return loadedState;
 	}
 
 	public Object[] getDeletedState() {
 		return deletedState;
 	}
 
 	public void setDeletedState(Object[] deletedState) {
 		this.deletedState = deletedState;
 	}
 
 	public boolean isExistsInDatabase() {
 		return existsInDatabase;
 	}
 
 	public Object getVersion() {
 		return version;
 	}
 
 	public EntityPersister getPersister() {
 		return persister;
 	}
 
 	/**
 	 * Get the EntityKey based on this EntityEntry.
 	 * @return the EntityKey
 	 * @throws  IllegalStateException if getId() is null
 	 */
 	public EntityKey getEntityKey() {
 		if ( cachedEntityKey == null ) {
 			if ( getId() == null ) {
 				throw new IllegalStateException( "cannot generate an EntityKey when id is null.");
 			}
 			cachedEntityKey = new EntityKey( getId(), getPersister(), tenantId );
 		}
 		return cachedEntityKey;
 	}
 
 	public String getEntityName() {
 		return entityName;
 	}
 
 	public boolean isBeingReplicated() {
 		return isBeingReplicated;
 	}
 	
 	public Object getRowId() {
 		return rowId;
 	}
 	
 	/**
 	 * Handle updating the internal state of the entry after actually performing
 	 * the database update.  Specifically we update the snapshot information and
 	 * escalate the lock mode
 	 *
 	 * @param entity The entity instance
 	 * @param updatedState The state calculated after the update (becomes the
 	 * new {@link #getLoadedState() loaded state}.
 	 * @param nextVersion The new version.
 	 */
 	public void postUpdate(Object entity, Object[] updatedState, Object nextVersion) {
 		this.loadedState = updatedState;
 		setLockMode( LockMode.WRITE );
 
 		if ( getPersister().isVersioned() ) {
 			this.version = nextVersion;
 			getPersister().setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
 		}
 
 		if ( getPersister().getInstrumentationMetadata().isInstrumented() ) {
 			final FieldInterceptor interceptor = getPersister().getInstrumentationMetadata().extractInterceptor( entity );
 			if ( interceptor != null ) {
 				interceptor.clearDirty();
 			}
 		}
+        if( entity instanceof SelfDirtinessTracker)
+            ((SelfDirtinessTracker) entity).$$_hibernate_clearDirtyAttributes();
+
 		persistenceContext.getSession()
 				.getFactory()
 				.getCustomEntityDirtinessStrategy()
 				.resetDirty( entity, getPersister(), (Session) persistenceContext.getSession() );
 	}
 
 	/**
 	 * After actually deleting a row, record the fact that the instance no longer
 	 * exists in the database
 	 */
 	public void postDelete() {
 		previousStatus = status;
 		status = Status.GONE;
 		existsInDatabase = false;
 	}
 	
 	/**
 	 * After actually inserting a row, record the fact that the instance exists on the 
 	 * database (needed for identity-column key generation)
 	 */
 	public void postInsert(Object[] insertedState) {
 		existsInDatabase = true;
 	}
 	
 	public boolean isNullifiable(boolean earlyInsert, SessionImplementor session) {
 		return getStatus() == Status.SAVING || (
 				earlyInsert ?
 						!isExistsInDatabase() :
 						session.getPersistenceContext().getNullifiableEntityKeys()
 							.contains( getEntityKey() )
 				);
 	}
 	
 	public Object getLoadedValue(String propertyName) {
 		if ( loadedState == null ) {
 			return null;
 		}
 		else {
 			int propertyIndex = ( (UniqueKeyLoadable) persister )
 					.getPropertyIndex( propertyName );
 			return loadedState[propertyIndex];
 		}
 	}
 
 	/**
 	 * Not sure this is the best method name, but the general idea here is to return {@code true} if the entity can
 	 * possibly be dirty.  This can only be the case if it is in a modifiable state (not read-only/deleted) and it
 	 * either has mutable properties or field-interception is not telling us it is dirty.  Clear as mud? :/
 	 *
 	 * A name like canPossiblyBeDirty might be better
 	 *
 	 * @param entity The entity to test
 	 *
 	 * @return {@code true} indicates that the entity could possibly be dirty and that dirty check
 	 * should happen; {@code false} indicates there is no way the entity can be dirty
 	 */
 	public boolean requiresDirtyCheck(Object entity) {
 		return isModifiableEntity()
 				&& ( ! isUnequivocallyNonDirty( entity ) );
 	}
 
 	@SuppressWarnings( {"SimplifiableIfStatement"})
 	private boolean isUnequivocallyNonDirty(Object entity) {
+
+        if(entity instanceof SelfDirtinessTracker)
+            return ((SelfDirtinessTracker) entity).$$_hibernate_hasDirtyAttributes();
+
 		final CustomEntityDirtinessStrategy customEntityDirtinessStrategy =
 				persistenceContext.getSession().getFactory().getCustomEntityDirtinessStrategy();
 		if ( customEntityDirtinessStrategy.canDirtyCheck( entity, getPersister(), (Session) persistenceContext.getSession() ) ) {
 			return ! customEntityDirtinessStrategy.isDirty( entity, getPersister(), (Session) persistenceContext.getSession() );
 		}
 		
 		if ( getPersister().hasMutableProperties() ) {
 			return false;
 		}
 		
 		if ( getPersister().getInstrumentationMetadata().isInstrumented() ) {
 			// the entity must be instrumented (otherwise we cant check dirty flag) and the dirty flag is false
 			return ! getPersister().getInstrumentationMetadata().extractInterceptor( entity ).isDirty();
 		}
 		
 		return false;
 	}
 
 	/**
 	 * Can the entity be modified?
 	 *
 	 * The entity is modifiable if all of the following are true:
 	 * <ul>
 	 * <li>the entity class is mutable</li>
 	 * <li>the entity is not read-only</li>
 	 * <li>if the current status is Status.DELETED, then the entity was not read-only when it was deleted</li>
 	 * </ul>
 	 * @return true, if the entity is modifiable; false, otherwise,
 	 */
 	public boolean isModifiableEntity() {
 		return getPersister().isMutable()
 				&& status != Status.READ_ONLY
 				&& ! ( status == Status.DELETED && previousStatus == Status.READ_ONLY );
 	}
 
 	public void forceLocked(Object entity, Object nextVersion) {
 		version = nextVersion;
 		loadedState[ persister.getVersionProperty() ] = version;
 		//noinspection deprecation
 		setLockMode( LockMode.FORCE );  // TODO:  use LockMode.PESSIMISTIC_FORCE_INCREMENT
 		persister.setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
 	}
 
 	public boolean isReadOnly() {
 		if (status != Status.MANAGED && status != Status.READ_ONLY) {
 			throw new HibernateException("instance was not in a valid state");
 		}
 		return status == Status.READ_ONLY;
 	}
 
 	public void setReadOnly(boolean readOnly, Object entity) {
 		if ( readOnly == isReadOnly() ) {
 			// simply return since the status is not being changed
 			return;
 		}
 		if ( readOnly ) {
 			setStatus( Status.READ_ONLY );
 			loadedState = null;
 		}
 		else {
 			if ( ! persister.isMutable() ) {
 				throw new IllegalStateException( "Cannot make an immutable entity modifiable." );
 			}
 			setStatus( Status.MANAGED );
 			loadedState = getPersister().getPropertyValues( entity );
 			persistenceContext.getNaturalIdHelper().manageLocalNaturalIdCrossReference(
 					persister,
 					id,
 					loadedState,
 					null,
 					CachedNaturalIdValueSource.LOAD
 			);
 		}
 	}
 	
 	public String toString() {
 		return "EntityEntry" + 
 				MessageHelper.infoString(entityName, id) + 
 				'(' + status + ')';
 	}
 
 	public boolean isLoadedWithLazyPropertiesUnfetched() {
 		return loadedWithLazyPropertiesUnfetched;
 	}
 
 	/**
 	 * Custom serialization routine used during serialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param oos The stream to which we should write the serial data.
 	 *
 	 * @throws IOException If a stream error occurs
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeObject( entityName );
 		oos.writeObject( id );
 		oos.writeObject( entityMode.toString() );
 		oos.writeObject( tenantId );
 		oos.writeObject( status.name() );
 		oos.writeObject( (previousStatus == null ? "" : previousStatus.name()) );
 		// todo : potentially look at optimizing these two arrays
 		oos.writeObject( loadedState );
 		oos.writeObject( deletedState );
 		oos.writeObject( version );
 		oos.writeObject( lockMode.toString() );
 		oos.writeBoolean( existsInDatabase );
 		oos.writeBoolean( isBeingReplicated );
 		oos.writeBoolean( loadedWithLazyPropertiesUnfetched );
 	}
 
 	/**
 	 * Custom deserialization routine used during deserialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param ois The stream from which to read the entry.
 	 * @param persistenceContext The context being deserialized.
 	 *
 	 * @return The deserialized EntityEntry
 	 *
 	 * @throws IOException If a stream error occurs
 	 * @throws ClassNotFoundException If any of the classes declared in the stream
 	 * cannot be found
 	 */
 	public static EntityEntry deserialize(
 			ObjectInputStream ois,
 	        PersistenceContext persistenceContext) throws IOException, ClassNotFoundException {
 		String previousStatusString;
 		return new EntityEntry(
 				// this complexity comes from non-flushed changes, should really look at how that reattaches entries
 				( persistenceContext.getSession() == null ? null : persistenceContext.getSession().getFactory() ),
 		        (String) ois.readObject(),
 				( Serializable ) ois.readObject(),
 	            EntityMode.parse( (String) ois.readObject() ),
 				(String) ois.readObject(),
 				Status.valueOf( (String) ois.readObject() ),
 				( ( previousStatusString = ( String ) ois.readObject() ).length() == 0 ?
 							null :
 							Status.valueOf( previousStatusString )
 				),
 	            ( Object[] ) ois.readObject(),
 	            ( Object[] ) ois.readObject(),
 	            ois.readObject(),
 	            LockMode.valueOf( (String) ois.readObject() ),
 	            ois.readBoolean(),
 	            ois.readBoolean(),
 	            ois.readBoolean(),
 				persistenceContext
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SelfDirtinessTracker.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SelfDirtinessTracker.java
new file mode 100644
index 0000000000..0b1336966a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SelfDirtinessTracker.java
@@ -0,0 +1,49 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.engine.spi;
+
+import java.util.Set;
+
+/**
+ * Specify if an entity class is instrumented to track field changes
+ *
+ * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
+ */
+public interface SelfDirtinessTracker {
+
+    /**
+     * Return true if any fields has been changed
+     */
+    boolean $$_hibernate_hasDirtyAttributes();
+
+    /**
+     * Get the field names of all the fields thats been changed
+     */
+    Set<String> $$_hibernate_getDirtyAttributes();
+
+    /**
+     * Clear the stored dirty attributes
+     */
+    void $$_hibernate_clearDirtyAttributes();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
index cf99ea572e..ecf0e998fa 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
@@ -1,666 +1,674 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 import java.util.Arrays;
 
+import org.hibernate.engine.spi.SelfDirtinessTracker;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.action.internal.DelayedPostInsertIdentifier;
 import org.hibernate.action.internal.EntityUpdateAction;
 import org.hibernate.engine.internal.Nullability;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.FlushEntityEvent;
 import org.hibernate.event.spi.FlushEntityEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 
 /**
  * An event that occurs for each entity instance at flush time
  *
  * @author Gavin King
  */
 public class DefaultFlushEntityEventListener implements FlushEntityEventListener {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultFlushEntityEventListener.class.getName());
 
 	/**
 	 * make sure user didn't mangle the id
 	 */
 	public void checkId(Object object, EntityPersister persister, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( id != null && id instanceof DelayedPostInsertIdentifier ) {
 			// this is a situation where the entity id is assigned by a post-insert generator
 			// and was saved outside the transaction forcing it to be delayed
 			return;
 		}
 
 		if ( persister.canExtractIdOutOfEntity() ) {
 
 			Serializable oid = persister.getIdentifier( object, session );
 			if (id==null) {
 				throw new AssertionFailure("null id in " + persister.getEntityName() + " entry (don't flush the Session after an exception occurs)");
 			}
 			if ( !persister.getIdentifierType().isEqual( id, oid, session.getFactory() ) ) {
 				throw new HibernateException(
 						"identifier of an instance of " +
 						persister.getEntityName() +
 						" was altered from " + id +
 						" to " + oid
 					);
 			}
 		}
 
 	}
 
 	private void checkNaturalId(
 			EntityPersister persister,
 	        EntityEntry entry,
 	        Object[] current,
 	        Object[] loaded,
 	        SessionImplementor session) {
 		if ( persister.hasNaturalIdentifier() && entry.getStatus() != Status.READ_ONLY ) {
 			if ( ! persister.getEntityMetamodel().hasImmutableNaturalId() ) {
 				// SHORT-CUT: if the natural id is mutable (!immutable), no need to do the below checks
 				// EARLY EXIT!!!
 				return;
 			}
 
 			final int[] naturalIdentifierPropertiesIndexes = persister.getNaturalIdentifierProperties();
 			final Type[] propertyTypes = persister.getPropertyTypes();
 			final boolean[] propertyUpdateability = persister.getPropertyUpdateability();
 
 			final Object[] snapshot = loaded == null
 					? session.getPersistenceContext().getNaturalIdSnapshot( entry.getId(), persister )
 					: session.getPersistenceContext().getNaturalIdHelper().extractNaturalIdValues( loaded, persister );
 
 			for ( int i=0; i<naturalIdentifierPropertiesIndexes.length; i++ ) {
 				final int naturalIdentifierPropertyIndex = naturalIdentifierPropertiesIndexes[i];
 				if ( propertyUpdateability[ naturalIdentifierPropertyIndex ] ) {
 					// if the given natural id property is updatable (mutable), there is nothing to check
 					continue;
 				}
 
 				final Type propertyType = propertyTypes[naturalIdentifierPropertyIndex];
 				if ( ! propertyType.isEqual( current[naturalIdentifierPropertyIndex], snapshot[i] ) ) {
 					throw new HibernateException(
 							String.format(
 									"An immutable natural identifier of entity %s was altered from %s to %s",
 									persister.getEntityName(),
 									propertyTypes[naturalIdentifierPropertyIndex].toLoggableString(
 											snapshot[i],
 											session.getFactory()
 									),
 									propertyTypes[naturalIdentifierPropertyIndex].toLoggableString(
 											current[naturalIdentifierPropertyIndex],
 											session.getFactory()
 									)
 							)
 					);
 			   }
 			}
 		}
 	}
 
 	/**
 	 * Flushes a single entity's state to the database, by scheduling
 	 * an update action, if necessary
 	 */
 	public void onFlushEntity(FlushEntityEvent event) throws HibernateException {
 		final Object entity = event.getEntity();
 		final EntityEntry entry = event.getEntityEntry();
 		final EventSource session = event.getSession();
 		final EntityPersister persister = entry.getPersister();
 		final Status status = entry.getStatus();
 		final Type[] types = persister.getPropertyTypes();
 
 		final boolean mightBeDirty = entry.requiresDirtyCheck(entity);
 
 		final Object[] values = getValues( entity, entry, mightBeDirty, session );
 
 		event.setPropertyValues(values);
 
 		//TODO: avoid this for non-new instances where mightBeDirty==false
 		boolean substitute = wrapCollections( session, persister, types, values);
 
 		if ( isUpdateNecessary( event, mightBeDirty ) ) {
 			substitute = scheduleUpdate( event ) || substitute;
 		}
 
 		if ( status != Status.DELETED ) {
 			// now update the object .. has to be outside the main if block above (because of collections)
 			if (substitute) persister.setPropertyValues( entity, values );
 
 			// Search for collections by reachability, updating their role.
 			// We don't want to touch collections reachable from a deleted object
 			if ( persister.hasCollections() ) {
 				new FlushVisitor(session, entity).processEntityPropertyValues(values, types);
 			}
 		}
 
 	}
 
 	private Object[] getValues(Object entity, EntityEntry entry, boolean mightBeDirty, SessionImplementor session) {
 		final Object[] loadedState = entry.getLoadedState();
 		final Status status = entry.getStatus();
 		final EntityPersister persister = entry.getPersister();
 
 		final Object[] values;
 		if ( status == Status.DELETED ) {
 			//grab its state saved at deletion
 			values = entry.getDeletedState();
 		}
 		else if ( !mightBeDirty && loadedState!=null ) {
 			values = loadedState;
 		}
 		else {
 			checkId( entity, persister, entry.getId(), session );
 
 			// grab its current state
 			values = persister.getPropertyValues( entity );
 
 			checkNaturalId( persister, entry, values, loadedState, session );
 		}
 		return values;
 	}
 
 	private boolean wrapCollections(
 			EventSource session,
 			EntityPersister persister,
 			Type[] types,
 			Object[] values
 	) {
 		if ( persister.hasCollections() ) {
 
 			// wrap up any new collections directly referenced by the object
 			// or its components
 
 			// NOTE: we need to do the wrap here even if its not "dirty",
 			// because collections need wrapping but changes to _them_
 			// don't dirty the container. Also, for versioned data, we
 			// need to wrap before calling searchForDirtyCollections
 
 			WrapVisitor visitor = new WrapVisitor(session);
 			// substitutes into values by side-effect
 			visitor.processEntityPropertyValues(values, types);
 			return visitor.isSubstitutionRequired();
 		}
 		else {
 			return false;
 		}
 	}
 
 	private boolean isUpdateNecessary(final FlushEntityEvent event, final boolean mightBeDirty) {
 		final Status status = event.getEntityEntry().getStatus();
 		if ( mightBeDirty || status==Status.DELETED ) {
 			// compare to cached state (ignoring collections unless versioned)
 			dirtyCheck(event);
 			if ( isUpdateNecessary(event) ) {
 				return true;
 			}
 			else {
 				if ( event.getEntityEntry().getPersister().getInstrumentationMetadata().isInstrumented() ) {
 					event.getEntityEntry()
 							.getPersister()
 							.getInstrumentationMetadata()
 							.extractInterceptor( event.getEntity() )
 							.clearDirty();
 				}
 				event.getSession()
 						.getFactory()
 						.getCustomEntityDirtinessStrategy()
 						.resetDirty( event.getEntity(), event.getEntityEntry().getPersister(), event.getSession() );
 				return false;
 			}
 		}
 		else {
 			return hasDirtyCollections( event, event.getEntityEntry().getPersister(), status );
 		}
 	}
 
 	private boolean scheduleUpdate(final FlushEntityEvent event) {
 
 		final EntityEntry entry = event.getEntityEntry();
 		final EventSource session = event.getSession();
 		final Object entity = event.getEntity();
 		final Status status = entry.getStatus();
 		final EntityPersister persister = entry.getPersister();
 		final Object[] values = event.getPropertyValues();
 
 		if ( LOG.isTraceEnabled() ) {
 			if ( status == Status.DELETED ) {
 				if ( !persister.isMutable() ) {
 					LOG.tracev( "Updating immutable, deleted entity: {0}",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() ) );
 				}
 				else if ( !entry.isModifiableEntity() )
 					LOG.tracev( "Updating non-modifiable, deleted entity: {0}",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() ) );
 				else
 					LOG.tracev( "Updating deleted entity: ",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() ) );
 			}
 			else
 				LOG.tracev( "Updating entity: {0}",
 						MessageHelper.infoString( persister, entry.getId(), session.getFactory() ) );
 		}
 
 		final boolean intercepted = !entry.isBeingReplicated() && handleInterception( event );
 
 		// increment the version number (if necessary)
 		final Object nextVersion = getNextVersion(event);
 
 		// if it was dirtied by a collection only
 		int[] dirtyProperties = event.getDirtyProperties();
 		if ( event.isDirtyCheckPossible() && dirtyProperties == null ) {
 			if ( ! intercepted && !event.hasDirtyCollection() ) {
 				throw new AssertionFailure( "dirty, but no dirty properties" );
 			}
 			dirtyProperties = ArrayHelper.EMPTY_INT_ARRAY;
 		}
 
 		// check nullability but do not doAfterTransactionCompletion command execute
 		// we'll use scheduled updates for that.
 		new Nullability(session).checkNullability( values, persister, true );
 
 		// schedule the update
 		// note that we intentionally do _not_ pass in currentPersistentState!
 		session.getActionQueue().addAction(
 				new EntityUpdateAction(
 						entry.getId(),
 						values,
 						dirtyProperties,
 						event.hasDirtyCollection(),
 						( status == Status.DELETED && ! entry.isModifiableEntity() ?
 								persister.getPropertyValues( entity ) :
 								entry.getLoadedState() ),
 						entry.getVersion(),
 						nextVersion,
 						entity,
 						entry.getRowId(),
 						persister,
 						session
 					)
 			);
 
 		return intercepted;
 	}
 
 	protected boolean handleInterception(FlushEntityEvent event) {
 		SessionImplementor session = event.getSession();
 		EntityEntry entry = event.getEntityEntry();
 		EntityPersister persister = entry.getPersister();
 		Object entity = event.getEntity();
 
 		//give the Interceptor a chance to modify property values
 		final Object[] values = event.getPropertyValues();
 		final boolean intercepted = invokeInterceptor( session, entity, entry, values, persister );
 
 		//now we might need to recalculate the dirtyProperties array
 		if ( intercepted && event.isDirtyCheckPossible() && !event.isDirtyCheckHandledByInterceptor() ) {
 			int[] dirtyProperties;
 			if ( event.hasDatabaseSnapshot() ) {
 				dirtyProperties = persister.findModified( event.getDatabaseSnapshot(), values, entity, session );
 			}
 			else {
 				dirtyProperties = persister.findDirty( values, entry.getLoadedState(), entity, session );
 			}
 			event.setDirtyProperties(dirtyProperties);
 		}
 
 		return intercepted;
 	}
 
 	protected boolean invokeInterceptor(
 			SessionImplementor session,
 			Object entity,
 			EntityEntry entry,
 			final Object[] values,
 			EntityPersister persister) {
 		return session.getInterceptor().onFlushDirty(
 				entity,
 				entry.getId(),
 				values,
 				entry.getLoadedState(),
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 		);
 	}
 
 	/**
 	 * Convience method to retreive an entities next version value
 	 */
 	private Object getNextVersion(FlushEntityEvent event) throws HibernateException {
 
 		EntityEntry entry = event.getEntityEntry();
 		EntityPersister persister = entry.getPersister();
 		if ( persister.isVersioned() ) {
 
 			Object[] values = event.getPropertyValues();
 
 			if ( entry.isBeingReplicated() ) {
 				return Versioning.getVersion(values, persister);
 			}
 			else {
 				int[] dirtyProperties = event.getDirtyProperties();
 
 				final boolean isVersionIncrementRequired = isVersionIncrementRequired(
 						event,
 						entry,
 						persister,
 						dirtyProperties
 					);
 
 				final Object nextVersion = isVersionIncrementRequired ?
 						Versioning.increment( entry.getVersion(), persister.getVersionType(), event.getSession() ) :
 						entry.getVersion(); //use the current version
 
 				Versioning.setVersion(values, nextVersion, persister);
 
 				return nextVersion;
 			}
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private boolean isVersionIncrementRequired(
 			FlushEntityEvent event,
 			EntityEntry entry,
 			EntityPersister persister,
 			int[] dirtyProperties
 	) {
 		final boolean isVersionIncrementRequired = entry.getStatus()!=Status.DELETED && (
 				dirtyProperties==null ||
 				Versioning.isVersionIncrementRequired(
 						dirtyProperties,
 						event.hasDirtyCollection(),
 						persister.getPropertyVersionability()
 				)
 			);
 		return isVersionIncrementRequired;
 	}
 
 	/**
 	 * Performs all necessary checking to determine if an entity needs an SQL update
 	 * to synchronize its state to the database. Modifies the event by side-effect!
 	 * Note: this method is quite slow, avoid calling if possible!
 	 */
 	protected final boolean isUpdateNecessary(FlushEntityEvent event) throws HibernateException {
 
 		EntityPersister persister = event.getEntityEntry().getPersister();
 		Status status = event.getEntityEntry().getStatus();
 
 		if ( !event.isDirtyCheckPossible() ) {
 			return true;
 		}
 		else {
 
 			int[] dirtyProperties = event.getDirtyProperties();
 			if ( dirtyProperties!=null && dirtyProperties.length!=0 ) {
 				return true; //TODO: suck into event class
 			}
 			else {
 				return hasDirtyCollections( event, persister, status );
 			}
 
 		}
 	}
 
 	private boolean hasDirtyCollections(FlushEntityEvent event, EntityPersister persister, Status status) {
 		if ( isCollectionDirtyCheckNecessary(persister, status ) ) {
 			DirtyCollectionSearchVisitor visitor = new DirtyCollectionSearchVisitor(
 					event.getSession(),
 					persister.getPropertyVersionability()
 				);
 			visitor.processEntityPropertyValues( event.getPropertyValues(), persister.getPropertyTypes() );
 			boolean hasDirtyCollections = visitor.wasDirtyCollectionFound();
 			event.setHasDirtyCollection(hasDirtyCollections);
 			return hasDirtyCollections;
 		}
 		else {
 			return false;
 		}
 	}
 
 	private boolean isCollectionDirtyCheckNecessary(EntityPersister persister, Status status) {
 		return ( status == Status.MANAGED || status == Status.READ_ONLY ) &&
 				persister.isVersioned() &&
 				persister.hasCollections();
 	}
 
 	/**
 	 * Perform a dirty check, and attach the results to the event
 	 */
 	protected void dirtyCheck(final FlushEntityEvent event) throws HibernateException {
 
 		final Object entity = event.getEntity();
 		final Object[] values = event.getPropertyValues();
 		final SessionImplementor session = event.getSession();
 		final EntityEntry entry = event.getEntityEntry();
 		final EntityPersister persister = entry.getPersister();
 		final Serializable id = entry.getId();
 		final Object[] loadedState = entry.getLoadedState();
 
 		int[] dirtyProperties = session.getInterceptor().findDirty(
 				entity,
 				id,
 				values,
 				loadedState,
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 		);
 
 		if ( dirtyProperties == null ) {
-			// see if the custom dirtiness strategy can tell us...
-			class DirtyCheckContextImpl implements CustomEntityDirtinessStrategy.DirtyCheckContext {
-				int[] found = null;
-				@Override
-				public void doDirtyChecking(CustomEntityDirtinessStrategy.AttributeChecker attributeChecker) {
-					found = new DirtyCheckAttributeInfoImpl( event ).visitAttributes( attributeChecker );
-					if ( found != null && found.length == 0 ) {
-						found = null;
-					}
-				}
-			}
-			DirtyCheckContextImpl context = new DirtyCheckContextImpl();
-			session.getFactory().getCustomEntityDirtinessStrategy().findDirty(
-					entity,
-					persister,
-					(Session) session,
-					context
-			);
-			dirtyProperties = context.found;
+            if(entity instanceof SelfDirtinessTracker) {
+                if(((SelfDirtinessTracker) entity).$$_hibernate_hasDirtyAttributes()) {
+                   dirtyProperties = persister.resolveAttributeIndexes(((SelfDirtinessTracker) entity).$$_hibernate_getDirtyAttributes());
+                }
+            }
+            else {
+                // see if the custom dirtiness strategy can tell us...
+                class DirtyCheckContextImpl implements CustomEntityDirtinessStrategy.DirtyCheckContext {
+                    int[] found = null;
+                    @Override
+                    public void doDirtyChecking(CustomEntityDirtinessStrategy.AttributeChecker attributeChecker) {
+                        found = new DirtyCheckAttributeInfoImpl( event ).visitAttributes( attributeChecker );
+                        if ( found != null && found.length == 0 ) {
+                            found = null;
+                        }
+                    }
+                }
+                DirtyCheckContextImpl context = new DirtyCheckContextImpl();
+                session.getFactory().getCustomEntityDirtinessStrategy().findDirty(
+                        entity,
+                        persister,
+                        (Session) session,
+                        context
+                );
+                dirtyProperties = context.found;
+            }
 		}
 
 		event.setDatabaseSnapshot(null);
 
 		final boolean interceptorHandledDirtyCheck;
 		boolean cannotDirtyCheck;
 
 		if ( dirtyProperties==null ) {
 			// Interceptor returned null, so do the dirtycheck ourself, if possible
 			interceptorHandledDirtyCheck = false;
 
 			cannotDirtyCheck = loadedState==null; // object loaded by update()
 			if ( !cannotDirtyCheck ) {
 				// dirty check against the usual snapshot of the entity
 				dirtyProperties = persister.findDirty( values, loadedState, entity, session );
 			}
 			else if ( entry.getStatus() == Status.DELETED && ! event.getEntityEntry().isModifiableEntity() ) {
 				// A non-modifiable (e.g., read-only or immutable) entity needs to be have
 				// references to transient entities set to null before being deleted. No other
 				// fields should be updated.
 				if ( values != entry.getDeletedState() ) {
 					throw new IllegalStateException(
 							"Entity has status Status.DELETED but values != entry.getDeletedState"
 					);
 				}
 				// Even if loadedState == null, we can dirty-check by comparing currentState and
 				// entry.getDeletedState() because the only fields to be updated are those that
 				// refer to transient entities that are being set to null.
 				// - currentState contains the entity's current property values.
 				// - entry.getDeletedState() contains the entity's current property values with
 				//   references to transient entities set to null.
 				// - dirtyProperties will only contain properties that refer to transient entities
 				final Object[] currentState = persister.getPropertyValues( event.getEntity() );
 				dirtyProperties = persister.findDirty( entry.getDeletedState(), currentState, entity, session );
 				cannotDirtyCheck = false;
 			}
 			else {
 				// dirty check against the database snapshot, if possible/necessary
 				final Object[] databaseSnapshot = getDatabaseSnapshot(session, persister, id);
 				if ( databaseSnapshot != null ) {
 					dirtyProperties = persister.findModified(databaseSnapshot, values, entity, session);
 					cannotDirtyCheck = false;
 					event.setDatabaseSnapshot(databaseSnapshot);
 				}
 			}
 		}
 		else {
 			// the Interceptor handled the dirty checking
 			cannotDirtyCheck = false;
 			interceptorHandledDirtyCheck = true;
 		}
 
 		logDirtyProperties( id, dirtyProperties, persister );
 
 		event.setDirtyProperties(dirtyProperties);
 		event.setDirtyCheckHandledByInterceptor(interceptorHandledDirtyCheck);
 		event.setDirtyCheckPossible(!cannotDirtyCheck);
 
 	}
 
 	private class DirtyCheckAttributeInfoImpl implements CustomEntityDirtinessStrategy.AttributeInformation {
 		private final FlushEntityEvent event;
 		private final EntityPersister persister;
 		private final int numberOfAttributes;
 		private int index = 0;
 
 		private DirtyCheckAttributeInfoImpl(FlushEntityEvent event) {
 			this.event = event;
 			this.persister = event.getEntityEntry().getPersister();
 			this.numberOfAttributes = persister.getPropertyNames().length;
 		}
 
 		@Override
 		public EntityPersister getContainingPersister() {
 			return persister;
 		}
 
 		@Override
 		public int getAttributeIndex() {
 			return index;
 		}
 
 		@Override
 		public String getName() {
 			return persister.getPropertyNames()[ index ];
 		}
 
 		@Override
 		public Type getType() {
 			return persister.getPropertyTypes()[ index ];
 		}
 
 		@Override
 		public Object getCurrentValue() {
 			return event.getPropertyValues()[ index ];
 		}
 
 		Object[] databaseSnapshot;
 
 		@Override
 		public Object getLoadedValue() {
 			if ( databaseSnapshot == null ) {
 				databaseSnapshot = getDatabaseSnapshot( event.getSession(), persister, event.getEntityEntry().getId() );
 			}
 			return databaseSnapshot[ index ];
 		}
 
 		public int[] visitAttributes(CustomEntityDirtinessStrategy.AttributeChecker attributeChecker) {
 			databaseSnapshot = null;
 			index = 0;
 
 			final int[] indexes = new int[ numberOfAttributes ];
 			int count = 0;
 			for ( ; index < numberOfAttributes; index++ ) {
 				if ( attributeChecker.isDirty( this ) ) {
 					indexes[ count++ ] = index;
 				}
 			}
 			return Arrays.copyOf( indexes, count );
 		}
 	}
 
 	private void logDirtyProperties(Serializable id, int[] dirtyProperties, EntityPersister persister) {
 		if ( dirtyProperties != null && dirtyProperties.length > 0 && LOG.isTraceEnabled() ) {
 			final String[] allPropertyNames = persister.getPropertyNames();
 			final String[] dirtyPropertyNames = new String[ dirtyProperties.length ];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				dirtyPropertyNames[i] = allPropertyNames[ dirtyProperties[i]];
 			}
 			LOG.tracev( "Found dirty properties [{0}] : {1}",
 					MessageHelper.infoString( persister.getEntityName(), id ),
 					dirtyPropertyNames );
 		}
 	}
 
 	private Object[] getDatabaseSnapshot(SessionImplementor session, EntityPersister persister, Serializable id) {
 		if ( persister.isSelectBeforeUpdateRequired() ) {
 			Object[] snapshot = session.getPersistenceContext()
 					.getDatabaseSnapshot(id, persister);
 			if (snapshot==null) {
 				//do we even really need this? the update will fail anyway....
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor()
 							.optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), id );
 			}
 			return snapshot;
 		}
 		// TODO: optimize away this lookup for entities w/o unsaved-value="undefined"
 		final EntityKey entityKey = session.generateEntityKey( id, persister );
 		return session.getPersistenceContext().getCachedDatabaseSnapshot( entityKey );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index f21e11118b..0acfcf98b6 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1151,2000 +1151,2014 @@ public abstract class AbstractEntityPersister
 	protected static String getTemplateFromString(String string, SessionFactoryImplementor factory) {
 		return string == null ?
 				null :
 				Template.renderWhereStringTemplate( string, factory.getDialect(), factory.getSqlFunctionRegistry() );
 	}
 
 	public String getTemplateFromColumn(org.hibernate.metamodel.relational.Column column, SessionFactoryImplementor factory) {
 		String templateString;
 		if ( column.getReadFragment() != null ) {
 			templateString = getTemplateFromString( column.getReadFragment(), factory );
 		}
 		else {
 			String columnName = column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 			templateString = Template.TEMPLATE + '.' + columnName;
 		}
 		return templateString;
 	}
 
 	protected String generateLazySelectString() {
 
 		if ( !entityMetamodel.hasLazyProperties() ) {
 			return null;
 		}
 
 		HashSet tableNumbers = new HashSet();
 		ArrayList columnNumbers = new ArrayList();
 		ArrayList formulaNumbers = new ArrayList();
 		for ( int i = 0; i < lazyPropertyNames.length; i++ ) {
 			// all this only really needs to consider properties
 			// of this class, not its subclasses, but since we
 			// are reusing code used for sequential selects, we
 			// use the subclass closure
 			int propertyNumber = getSubclassPropertyIndex( lazyPropertyNames[i] );
 
 			int tableNumber = getSubclassPropertyTableNumber( propertyNumber );
 			tableNumbers.add(  tableNumber );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int j = 0; j < colNumbers.length; j++ ) {
 				if ( colNumbers[j]!=-1 ) {
 					columnNumbers.add( colNumbers[j] );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int j = 0; j < formNumbers.length; j++ ) {
 				if ( formNumbers[j]!=-1 ) {
 					formulaNumbers.add( formNumbers[j] );
 				}
 			}
 		}
 
 		if ( columnNumbers.size()==0 && formulaNumbers.size()==0 ) {
 			// only one-to-one is lazy fetched
 			return null;
 		}
 
 		return renderSelect( ArrayHelper.toIntArray( tableNumbers ),
 				ArrayHelper.toIntArray( columnNumbers ),
 				ArrayHelper.toIntArray( formulaNumbers ) );
 
 	}
 
 	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session)
 			throws HibernateException {
 
 		final Serializable id = session.getContextEntityIdentifier( entity );
 
 		final EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 		if ( entry == null ) {
 			throw new HibernateException( "entity is not associated with the session: " + id );
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Initializing lazy properties of: {0}, field access: {1}", MessageHelper.infoString( this, id, getFactory() ), fieldName );
 		}
 
 		if ( hasCache() ) {
 			CacheKey cacheKey = session.generateCacheKey( id, getIdentifierType(), getEntityName() );
 			Object ce = getCacheAccessStrategy().get( cacheKey, session.getTimestamp() );
 			if (ce!=null) {
 				CacheEntry cacheEntry = (CacheEntry) getCacheEntryStructure().destructure(ce, factory);
 				if ( !cacheEntry.areLazyPropertiesUnfetched() ) {
 					//note early exit here:
 					return initializeLazyPropertiesFromCache( fieldName, entity, session, entry, cacheEntry );
 				}
 			}
 		}
 
 		return initializeLazyPropertiesFromDatastore( fieldName, entity, session, id, entry );
 
 	}
 
 	private Object initializeLazyPropertiesFromDatastore(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Serializable id,
 			final EntityEntry entry) {
 
 		if ( !hasLazyProperties() ) throw new AssertionFailure( "no lazy properties" );
 
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
 						ps = session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( lazySelect );
 						getIdentifierType().nullSafeSet( ps, id, 1, session );
 						rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 						rs.next();
 					}
 					final Object[] snapshot = entry.getLoadedState();
 					for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 						Object propValue = lazyPropertyTypes[j].nullSafeGet( rs, lazyPropertyColumnAliases[j], session, entity );
 						if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 							result = propValue;
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 				}
 			}
 
 			LOG.trace( "Done initializing lazy properties" );
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
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
 					disassembledValues[ lazyPropertyNumbers[j] ],
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
 		setPropertyValue( entity, lazyPropertyNumbers[j], propValue );
 		if ( snapshot != null ) {
 			// object have been loaded with setReadOnly(true); HHH-2236
 			snapshot[ lazyPropertyNumbers[j] ] = lazyPropertyTypes[j].deepCopy( propValue, factory );
 		}
 		return fieldName.equals( lazyPropertyNames[j] );
 	}
 
 	public boolean isBatchable() {
 		return optimisticLockStyle() == OptimisticLockStyle.NONE
 				|| ( !isVersioned() && optimisticLockStyle() == OptimisticLockStyle.VERSION )
 				|| getFactory().getSettings().isJdbcBatchVersionedData();
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return getPropertySpaces();
 	}
 
 	protected Set getLazyProperties() {
 		return lazyProperties;
 	}
 
 	public boolean isBatchLoadable() {
 		return batchSize > 1;
 	}
 
 	public String[] getIdentifierColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	public String[] getIdentifierColumnReaders() {
 		return rootTableKeyColumnReaders;
 	}
 
 	public String[] getIdentifierColumnReaderTemplates() {
 		return rootTableKeyColumnReaderTemplates;
 	}
 
 	protected int getIdentifierColumnSpan() {
 		return identifierColumnSpan;
 	}
 
 	protected String[] getIdentifierAliases() {
 		return identifierAliases;
 	}
 
 	public String getVersionColumnName() {
 		return versionColumnName;
 	}
 
 	protected String getVersionedTableName() {
 		return getTableName( 0 );
 	}
 
 	protected boolean[] getSubclassColumnLazyiness() {
 		return subclassColumnLazyClosure;
 	}
 
 	protected boolean[] getSubclassFormulaLazyiness() {
 		return subclassFormulaLazyClosure;
 	}
 
 	/**
 	 * We can't immediately add to the cache if we have formulas
 	 * which must be evaluated, or if we have the possibility of
 	 * two concurrent updates to the same item being merged on
 	 * the database. This can happen if (a) the item is not
 	 * versioned and either (b) we have dynamic update enabled
 	 * or (c) we have multiple tables holding the state of the
 	 * item.
 	 */
 	public boolean isCacheInvalidationRequired() {
 		return hasFormulaProperties() ||
 				( !isVersioned() && ( entityMetamodel.isDynamicUpdate() || getTableSpan() > 1 ) );
 	}
 
 	public boolean isLazyPropertiesCacheable() {
 		return isLazyPropertiesCacheable;
 	}
 
 	public String selectFragment(String alias, String suffix) {
 		return identifierSelectFragment( alias, suffix ) +
 				propertySelectFragment( alias, suffix, false );
 	}
 
 	public String[] getIdentifierAliases(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getIdentiferColumnNames() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return new Alias( suffix ).toAliasStrings( getIdentifierAliases() );
 	}
 
 	public String[] getPropertyAliases(String suffix, int i) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		return new Alias( suffix ).toUnquotedAliasStrings( propertyColumnAliases[i] );
 	}
 
 	public String getDiscriminatorAlias(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getdiscriminatorColumnName() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return entityMetamodel.hasSubclasses() ?
 				new Alias( suffix ).toAliasString( getDiscriminatorAlias() ) :
 				null;
 	}
 
 	public String identifierSelectFragment(String name, String suffix) {
 		return new SelectFragment()
 				.setSuffix( suffix )
 				.addColumns( name, getIdentifierColumnNames(), getIdentifierAliases() )
 				.toFragmentString()
 				.substring( 2 ); //strip leading ", "
 	}
 
 
 	public String propertySelectFragment(String tableAlias, String suffix, boolean allProperties) {
 		return propertySelectFragmentFragment( tableAlias, suffix, allProperties ).toFragmentString();
 	}
 
 	public SelectFragment propertySelectFragmentFragment(
 			String tableAlias,
 			String suffix,
 			boolean allProperties) {
 		SelectFragment select = new SelectFragment()
 				.setSuffix( suffix )
 				.setUsedAliases( getIdentifierAliases() );
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < getSubclassColumnClosure().length; i++ ) {
 			boolean selectable = ( allProperties || !subclassColumnLazyClosure[i] ) &&
 				!isSubclassTableSequentialSelect( columnTableNumbers[i] ) &&
 				subclassColumnSelectableClosure[i];
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, columnTableNumbers[i] );
 				select.addColumnTemplate( subalias, columnReaderTemplates[i], columnAliases[i] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < getSubclassFormulaTemplateClosure().length; i++ ) {
 			boolean selectable = ( allProperties || !subclassFormulaLazyClosure[i] )
 				&& !isSubclassTableSequentialSelect( formulaTableNumbers[i] );
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, formulaTableNumbers[i] );
 				select.addFormula( subalias, formulaTemplates[i], formulaAliases[i] );
 			}
 		}
 
 		if ( entityMetamodel.hasSubclasses() ) {
 			addDiscriminatorToSelect( select, tableAlias, suffix );
 		}
 
 		if ( hasRowId() ) {
 			select.addColumn( tableAlias, rowIdName, ROWID_ALIAS );
 		}
 
 		return select;
 	}
 
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting current persistent state for: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getSQLSnapshotSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				//if ( isVersioned() ) getVersionType().nullSafeSet( ps, version, getIdentifierColumnSpan()+1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					//otherwise return the "hydrated" state (ie. associations are not resolved)
 					Type[] types = getPropertyTypes();
 					Object[] values = new Object[types.length];
 					boolean[] includeProperty = getPropertyUpdateability();
 					for ( int i = 0; i < types.length; i++ ) {
 						if ( includeProperty[i] ) {
 							values[i] = types[i].hydrate( rs, getPropertyAliases( "", i ), session, null ); //null owner ok??
 						}
 					}
 					return values;
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 			        getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	@Override
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) throws HibernateException {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"resolving unique key [%s] to identifier for entity [%s]",
 					key,
 					getEntityName()
 			);
 		}
 
 		int propertyIndex = getSubclassPropertyIndex( uniquePropertyName );
 		if ( propertyIndex < 0 ) {
 			throw new HibernateException(
 					"Could not determine Type for property [" + uniquePropertyName + "] on entity [" + getEntityName() + "]"
 			);
 		}
 		Type propertyType = getSubclassPropertyType( propertyIndex );
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( generateIdByUniqueKeySelectString( uniquePropertyName ) );
 			try {
 				propertyType.nullSafeSet( ps, key, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					return (Serializable) getIdentifierType().nullSafeGet( rs, getIdentifierAliases(), session, null );
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					String.format(
 							"could not resolve unique property [%s] to identifier for entity [%s]",
 							uniquePropertyName,
 							getEntityName()
 					),
 					getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	protected String generateIdByUniqueKeySelectString(String uniquePropertyName) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "resolve id by unique property [" + getEntityName() + "." + uniquePropertyName + "]" );
 		}
 
 		final String rooAlias = getRootAlias();
 
 		select.setFromClause( fromTableFragment( rooAlias ) + fromJoinFragment( rooAlias, true, false ) );
 
 		SelectFragment selectFragment = new SelectFragment();
 		selectFragment.addColumns( rooAlias, getIdentifierColumnNames(), getIdentifierAliases() );
 		select.setSelectClause( selectFragment );
 
 		StringBuilder whereClauseBuffer = new StringBuilder();
 		final int uniquePropertyIndex = getSubclassPropertyIndex( uniquePropertyName );
 		final String uniquePropertyTableAlias = generateTableAlias(
 				rooAlias,
 				getSubclassPropertyTableNumber( uniquePropertyIndex )
 		);
 		String sep = "";
 		for ( String columnTemplate : getSubclassPropertyColumnReaderTemplateClosure()[uniquePropertyIndex] ) {
 			if ( columnTemplate == null ) {
 				continue;
 			}
 			final String columnReference = StringHelper.replace( columnTemplate, Template.TEMPLATE, uniquePropertyTableAlias );
 			whereClauseBuffer.append( sep ).append( columnReference ).append( "=?" );
 			sep = " and ";
 		}
 		for ( String formulaTemplate : getSubclassPropertyFormulaTemplateClosure()[uniquePropertyIndex] ) {
 			if ( formulaTemplate == null ) {
 				continue;
 			}
 			final String formulaReference = StringHelper.replace( formulaTemplate, Template.TEMPLATE, uniquePropertyTableAlias );
 			whereClauseBuffer.append( sep ).append( formulaReference ).append( "=?" );
 			sep = " and ";
 		}
 		whereClauseBuffer.append( whereJoinFragment( rooAlias, true, false ) );
 
 		select.setWhereClause( whereClauseBuffer.toString() );
 
 		return select.setOuterJoins( "", "" ).toStatementString();
 	}
 
 
 	/**
 	 * Generate the SQL that selects the version number by id
 	 */
 	protected String generateSelectVersionString() {
 		SimpleSelect select = new SimpleSelect( getFactory().getDialect() )
 				.setTableName( getVersionedTableName() );
 		if ( isVersioned() ) {
 			select.addColumn( versionColumnName );
 		}
 		else {
 			select.addColumns( rootTableKeyColumnNames );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get version " + getEntityName() );
 		}
 		return select.addCondition( rootTableKeyColumnNames, "=?" ).toStatementString();
 	}
 
 	public boolean[] getPropertyUniqueness() {
 		return propertyUniqueness;
 	}
 
 	protected String generateInsertGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( getPropertyInsertGenerationInclusions() );
 	}
 
 	protected String generateUpdateGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( getPropertyUpdateGenerationInclusions() );
 	}
 
 	private String generateGeneratedValuesSelectString(ValueInclusion[] inclusions) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get generated state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 
 		// Here we render the select column list based on the properties defined as being generated.
 		// For partial component generation, we currently just re-select the whole component
 		// rather than trying to handle the individual generated portions.
 		String selectClause = concretePropertySelectFragment( getRootAlias(), inclusions );
 		selectClause = selectClause.substring( 2 );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuilder()
 			.append( StringHelper.join( "=? and ", aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	protected static interface InclusionChecker {
 		public boolean includeProperty(int propertyNumber);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final ValueInclusion[] inclusions) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					// TODO : currently we really do not handle ValueInclusion.PARTIAL...
 					// ValueInclusion.PARTIAL would indicate parts of a component need to
 					// be included in the select; currently we then just render the entire
 					// component into the select clause in that case.
 					public boolean includeProperty(int propertyNumber) {
 						return inclusions[propertyNumber] != ValueInclusion.NONE;
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final boolean[] includeProperty) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					public boolean includeProperty(int propertyNumber) {
 						return includeProperty[propertyNumber];
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, InclusionChecker inclusionChecker) {
 		int propertyCount = getPropertyNames().length;
 		int[] propertyTableNumbers = getPropertyTableNumbersInSelect();
 		SelectFragment frag = new SelectFragment();
 		for ( int i = 0; i < propertyCount; i++ ) {
 			if ( inclusionChecker.includeProperty( i ) ) {
 				frag.addColumnTemplates(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnReaderTemplates[i],
 						propertyColumnAliases[i]
 				);
 				frag.addFormulas(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnFormulaTemplates[i],
 						propertyColumnAliases[i]
 				);
 			}
 		}
 		return frag.toFragmentString();
 	}
 
 	protected String generateSnapshotSelectString() {
 
 		//TODO: should we use SELECT .. FOR UPDATE?
 
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String selectClause = StringHelper.join( ", ", aliasedIdColumns ) +
 				concretePropertySelectFragment( getRootAlias(), getPropertyUpdateability() );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuilder()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		/*if ( isVersioned() ) {
 			where.append(" and ")
 				.append( getVersionColumnName() )
 				.append("=?");
 		}*/
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 		if ( !isVersioned() ) {
 			throw new AssertionFailure( "cannot force version increment on non-versioned entity" );
 		}
 
 		if ( isVersionPropertyGenerated() ) {
 			// the difficulty here is exactly what do we update in order to
 			// force the version to be incremented in the db...
 			throw new HibernateException( "LockMode.FORCE is currently not supported for generated version properties" );
 		}
 
 		Object nextVersion = getVersionType().next( currentVersion, session );
         if (LOG.isTraceEnabled()) LOG.trace("Forcing version increment [" + MessageHelper.infoString(this, id, getFactory()) + "; "
                                             + getVersionType().toLoggableString(currentVersion, getFactory()) + " -> "
                                             + getVersionType().toLoggableString(nextVersion, getFactory()) + "]");
 
 		// todo : cache this sql...
 		String versionIncrementString = generateVersionIncrementUpdateString();
 		PreparedStatement st = null;
 		try {
 			st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( versionIncrementString, false );
 			try {
 				getVersionType().nullSafeSet( st, nextVersion, 1, session );
 				getIdentifierType().nullSafeSet( st, id, 2, session );
 				getVersionType().nullSafeSet( st, currentVersion, 2 + getIdentifierColumnSpan(), session );
 				int rows = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st );
 				if ( rows != 1 ) {
 					throw new StaleObjectStateException( getEntityName(), id );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve version: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 				);
 		}
 
 		return nextVersion;
 	}
 
 	private String generateVersionIncrementUpdateString() {
 		Update update = new Update( getFactory().getDialect() );
 		update.setTableName( getTableName( 0 ) );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "forced version increment" );
 		}
 		update.addColumn( getVersionColumnName() );
 		update.addPrimaryKeyColumns( getIdentifierColumnNames() );
 		update.setVersionColumnName( getVersionColumnName() );
 		return update.toStatementString();
 	}
 
 	/**
 	 * Retrieve the version number
 	 */
 	public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting version: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getVersionSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( st, id, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					if ( !rs.next() ) {
 						return null;
 					}
 					if ( !isVersioned() ) {
 						return this;
 					}
 					return getVersionType().nullSafeGet( rs, getVersionColumnName(), session, null );
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve version: " + MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 			);
 		}
 	}
 
 	protected void initLockers() {
 		lockers.put( LockMode.READ, generateLocker( LockMode.READ ) );
 		lockers.put( LockMode.UPGRADE, generateLocker( LockMode.UPGRADE ) );
 		lockers.put( LockMode.UPGRADE_NOWAIT, generateLocker( LockMode.UPGRADE_NOWAIT ) );
 		lockers.put( LockMode.UPGRADE_SKIPLOCKED, generateLocker( LockMode.UPGRADE_SKIPLOCKED ) );
 		lockers.put( LockMode.FORCE, generateLocker( LockMode.FORCE ) );
 		lockers.put( LockMode.PESSIMISTIC_READ, generateLocker( LockMode.PESSIMISTIC_READ ) );
 		lockers.put( LockMode.PESSIMISTIC_WRITE, generateLocker( LockMode.PESSIMISTIC_WRITE ) );
 		lockers.put( LockMode.PESSIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.PESSIMISTIC_FORCE_INCREMENT ) );
 		lockers.put( LockMode.OPTIMISTIC, generateLocker( LockMode.OPTIMISTIC ) );
 		lockers.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.OPTIMISTIC_FORCE_INCREMENT ) );
 	}
 
 	protected LockingStrategy generateLocker(LockMode lockMode) {
 		return factory.getDialect().getLockingStrategy( this, lockMode );
 	}
 
 	private LockingStrategy getLocker(LockMode lockMode) {
 		return ( LockingStrategy ) lockers.get( lockMode );
 	}
 
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockMode lockMode,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockMode ).lock( id, version, object, LockOptions.WAIT_FOREVER, session );
 	}
 
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockOptions lockOptions,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockOptions.getLockMode() ).lock( id, version, object, lockOptions.getTimeOut(), session );
 	}
 
 	public String getRootTableName() {
 		return getSubclassTableName( 0 );
 	}
 
 	public String getRootTableAlias(String drivingAlias) {
 		return drivingAlias;
 	}
 
 	public String[] getRootTableIdentifierColumnNames() {
 		return getRootTableKeyColumnNames();
 	}
 
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		return propertyMapping.toColumns( alias, propertyName );
 	}
 
 	public String[] toColumns(String propertyName) throws QueryException {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public String[] getPropertyColumnNames(String propertyName) {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	/**
 	 * Warning:
 	 * When there are duplicated property names in the subclasses
 	 * of the class, this method may return the wrong table
 	 * number for the duplicated subclass property (note that
 	 * SingleTableEntityPersister defines an overloaded form
 	 * which takes the entity name.
 	 */
 	public int getSubclassPropertyTableNumber(String propertyPath) {
 		String rootPropertyName = StringHelper.root(propertyPath);
 		Type type = propertyMapping.toType(rootPropertyName);
 		if ( type.isAssociationType() ) {
 			AssociationType assocType = ( AssociationType ) type;
 			if ( assocType.useLHSPrimaryKey() ) {
 				// performance op to avoid the array search
 				return 0;
 			}
 			else if ( type.isCollectionType() ) {
 				// properly handle property-ref-based associations
 				rootPropertyName = assocType.getLHSPropertyName();
 			}
 		}
 		//Enable for HHH-440, which we don't like:
 		/*if ( type.isComponentType() && !propertyName.equals(rootPropertyName) ) {
 			String unrooted = StringHelper.unroot(propertyName);
 			int idx = ArrayHelper.indexOf( getSubclassColumnClosure(), unrooted );
 			if ( idx != -1 ) {
 				return getSubclassColumnTableNumberClosure()[idx];
 			}
 		}*/
 		int index = ArrayHelper.indexOf( getSubclassPropertyNameClosure(), rootPropertyName); //TODO: optimize this better!
 		return index==-1 ? 0 : getSubclassPropertyTableNumber(index);
 	}
 
 	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
 		int tableIndex = getSubclassPropertyTableNumber( propertyPath );
 		if ( tableIndex == 0 ) {
 			return Declarer.CLASS;
 		}
 		else if ( isClassOrSuperclassTable( tableIndex ) ) {
 			return Declarer.SUPERCLASS;
 		}
 		else {
 			return Declarer.SUBCLASS;
 		}
 	}
 
 	private DiscriminatorMetadata discriminatorMetadata;
 
 	public DiscriminatorMetadata getTypeDiscriminatorMetadata() {
 		if ( discriminatorMetadata == null ) {
 			discriminatorMetadata = buildTypeDiscriminatorMetadata();
 		}
 		return discriminatorMetadata;
 	}
 
 	private DiscriminatorMetadata buildTypeDiscriminatorMetadata() {
 		return new DiscriminatorMetadata() {
 			public String getSqlFragment(String sqlQualificationAlias) {
 				return toColumns( sqlQualificationAlias, ENTITY_CLASS )[0];
 			}
 
 			public Type getResolutionType() {
 				return new DiscriminatorType( getDiscriminatorType(), AbstractEntityPersister.this );
 			}
 		};
 	}
 
 	public static String generateTableAlias(String rootAlias, int tableNumber) {
 		if ( tableNumber == 0 ) {
 			return rootAlias;
 		}
 		StringBuilder buf = new StringBuilder().append( rootAlias );
 		if ( !rootAlias.endsWith( "_" ) ) {
 			buf.append( '_' );
 		}
 		return buf.append( tableNumber ).append( '_' ).toString();
 	}
 
 	public String[] toColumns(String name, final int i) {
 		final String alias = generateTableAlias( name, getSubclassPropertyTableNumber( i ) );
 		String[] cols = getSubclassPropertyColumnNames( i );
 		String[] templates = getSubclassPropertyFormulaTemplateClosure()[i];
 		String[] result = new String[cols.length];
 		for ( int j = 0; j < cols.length; j++ ) {
 			if ( cols[j] == null ) {
 				result[j] = StringHelper.replace( templates[j], Template.TEMPLATE, alias );
 			}
 			else {
 				result[j] = StringHelper.qualify( alias, cols[j] );
 			}
 		}
 		return result;
 	}
 
 	private int getSubclassPropertyIndex(String propertyName) {
 		return ArrayHelper.indexOf(subclassPropertyNameClosure, propertyName);
 	}
 
 	protected String[] getPropertySubclassNames() {
 		return propertySubclassNames;
 	}
 
 	public String[] getPropertyColumnNames(int i) {
 		return propertyColumnNames[i];
 	}
 
 	public String[] getPropertyColumnWriters(int i) {
 		return propertyColumnWriters[i];
 	}
 
 	protected int getPropertyColumnSpan(int i) {
 		return propertyColumnSpans[i];
 	}
 
 	protected boolean hasFormulaProperties() {
 		return hasFormulaProperties;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return subclassPropertyFetchModeClosure[i];
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return subclassPropertyCascadeStyleClosure[i];
 	}
 
 	public Type getSubclassPropertyType(int i) {
 		return subclassPropertyTypeClosure[i];
 	}
 
 	public String getSubclassPropertyName(int i) {
 		return subclassPropertyNameClosure[i];
 	}
 
 	public int countSubclassProperties() {
 		return subclassPropertyTypeClosure.length;
 	}
 
 	public String[] getSubclassPropertyColumnNames(int i) {
 		return subclassPropertyColumnNameClosure[i];
 	}
 
 	public boolean isDefinedOnSubclass(int i) {
 		return propertyDefinedOnSubclass[i];
 	}
 
 	@Override
 	public String[][] getSubclassPropertyFormulaTemplateClosure() {
 		return subclassPropertyFormulaTemplateClosure;
 	}
 
 	protected Type[] getSubclassPropertyTypeClosure() {
 		return subclassPropertyTypeClosure;
 	}
 
 	protected String[][] getSubclassPropertyColumnNameClosure() {
 		return subclassPropertyColumnNameClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderClosure() {
 		return subclassPropertyColumnReaderClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderTemplateClosure() {
 		return subclassPropertyColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassPropertyNameClosure() {
 		return subclassPropertyNameClosure;
 	}
 
+    @Override
+    public int[] resolveAttributeIndexes(Set<String> properties) {
+        Iterator<String> iter = properties.iterator();
+        int[] fields = new int[properties.size()];
+        int counter = 0;
+        while(iter.hasNext()) {
+            Integer index = entityMetamodel.getPropertyIndexOrNull(iter.next());
+            if(index != null)
+                fields[counter++] = index;
+        }
+
+        return fields;
+    }
+
 	protected String[] getSubclassPropertySubclassNameClosure() {
 		return subclassPropertySubclassNameClosure;
 	}
 
 	protected String[] getSubclassColumnClosure() {
 		return subclassColumnClosure;
 	}
 
 	protected String[] getSubclassColumnAliasClosure() {
 		return subclassColumnAliasClosure;
 	}
 
 	public String[] getSubclassColumnReaderTemplateClosure() {
 		return subclassColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaClosure() {
 		return subclassFormulaClosure;
 	}
 
 	protected String[] getSubclassFormulaTemplateClosure() {
 		return subclassFormulaTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaAliasClosure() {
 		return subclassFormulaAliasClosure;
 	}
 
 	public String[] getSubclassPropertyColumnAliases(String propertyName, String suffix) {
 		String rawAliases[] = ( String[] ) subclassPropertyAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String result[] = new String[rawAliases.length];
 		for ( int i = 0; i < rawAliases.length; i++ ) {
 			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 
 	public String[] getSubclassPropertyColumnNames(String propertyName) {
 		//TODO: should we allow suffixes on these ?
 		return ( String[] ) subclassPropertyColumnNames.get( propertyName );
 	}
 
 
 
 	//This is really ugly, but necessary:
 	/**
 	 * Must be called by subclasses, at the end of their constructors
 	 */
 	protected void initSubclassPropertyAliasesMap(PersistentClass model) throws MappingException {
 
 		// ALIASES
 		internalInitSubclassPropertyAliasesMap( null, model.getSubclassPropertyClosureIterator() );
 
 		// aliases for identifier ( alias.id ); skip if the entity defines a non-id property named 'id'
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			subclassPropertyAliases.put( ENTITY_ID, getIdentifierAliases() );
 			subclassPropertyColumnNames.put( ENTITY_ID, getIdentifierColumnNames() );
 		}
 
 		// aliases named identifier ( alias.idname )
 		if ( hasIdentifierProperty() ) {
 			subclassPropertyAliases.put( getIdentifierPropertyName(), getIdentifierAliases() );
 			subclassPropertyColumnNames.put( getIdentifierPropertyName(), getIdentifierColumnNames() );
 		}
 
 		// aliases for composite-id's
 		if ( getIdentifierType().isComponentType() ) {
 			// Fetch embedded identifiers propertynames from the "virtual" identifier component
 			CompositeType componentId = ( CompositeType ) getIdentifierType();
 			String[] idPropertyNames = componentId.getPropertyNames();
 			String[] idAliases = getIdentifierAliases();
 			String[] idColumnNames = getIdentifierColumnNames();
 
 			for ( int i = 0; i < idPropertyNames.length; i++ ) {
 				if ( entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 					subclassPropertyAliases.put(
 							ENTITY_ID + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							ENTITY_ID + "." + getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 //				if (hasIdentifierProperty() && !ENTITY_ID.equals( getIdentifierPropertyName() ) ) {
 				if ( hasIdentifierProperty() ) {
 					subclassPropertyAliases.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 				else {
 					// embedded composite ids ( alias.idname1, alias.idname2 )
 					subclassPropertyAliases.put( idPropertyNames[i], new String[] { idAliases[i] } );
 					subclassPropertyColumnNames.put( idPropertyNames[i],  new String[] { idColumnNames[i] } );
 				}
 			}
 		}
 
 		if ( entityMetamodel.isPolymorphic() ) {
 			subclassPropertyAliases.put( ENTITY_CLASS, new String[] { getDiscriminatorAlias() } );
 			subclassPropertyColumnNames.put( ENTITY_CLASS, new String[] { getDiscriminatorColumnName() } );
 		}
 
 	}
 
 	/**
 	 * Must be called by subclasses, at the end of their constructors
 	 */
 	protected void initSubclassPropertyAliasesMap(EntityBinding model) throws MappingException {
 
 		// ALIASES
 
 		// TODO: Fix when subclasses are working (HHH-6337)
 		//internalInitSubclassPropertyAliasesMap( null, model.getSubclassPropertyClosureIterator() );
 
 		// aliases for identifier ( alias.id ); skip if the entity defines a non-id property named 'id'
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			subclassPropertyAliases.put( ENTITY_ID, getIdentifierAliases() );
 			subclassPropertyColumnNames.put( ENTITY_ID, getIdentifierColumnNames() );
 		}
 
 		// aliases named identifier ( alias.idname )
 		if ( hasIdentifierProperty() ) {
 			subclassPropertyAliases.put( getIdentifierPropertyName(), getIdentifierAliases() );
 			subclassPropertyColumnNames.put( getIdentifierPropertyName(), getIdentifierColumnNames() );
 		}
 
 		// aliases for composite-id's
 		if ( getIdentifierType().isComponentType() ) {
 			// Fetch embedded identifiers propertynames from the "virtual" identifier component
 			CompositeType componentId = ( CompositeType ) getIdentifierType();
 			String[] idPropertyNames = componentId.getPropertyNames();
 			String[] idAliases = getIdentifierAliases();
 			String[] idColumnNames = getIdentifierColumnNames();
 
 			for ( int i = 0; i < idPropertyNames.length; i++ ) {
 				if ( entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 					subclassPropertyAliases.put(
 							ENTITY_ID + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							ENTITY_ID + "." + getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 //				if (hasIdentifierProperty() && !ENTITY_ID.equals( getIdentifierPropertyName() ) ) {
 				if ( hasIdentifierProperty() ) {
 					subclassPropertyAliases.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 				else {
 					// embedded composite ids ( alias.idname1, alias.idname2 )
 					subclassPropertyAliases.put( idPropertyNames[i], new String[] { idAliases[i] } );
 					subclassPropertyColumnNames.put( idPropertyNames[i],  new String[] { idColumnNames[i] } );
 				}
 			}
 		}
 
 		if ( entityMetamodel.isPolymorphic() ) {
 			subclassPropertyAliases.put( ENTITY_CLASS, new String[] { getDiscriminatorAlias() } );
 			subclassPropertyColumnNames.put( ENTITY_CLASS, new String[] { getDiscriminatorColumnName() } );
 		}
 
 	}
 
 	private void internalInitSubclassPropertyAliasesMap(String path, Iterator propertyIterator) {
 		while ( propertyIterator.hasNext() ) {
 
 			Property prop = ( Property ) propertyIterator.next();
 			String propname = path == null ? prop.getName() : path + "." + prop.getName();
 			if ( prop.isComposite() ) {
 				Component component = ( Component ) prop.getValue();
 				Iterator compProps = component.getPropertyIterator();
 				internalInitSubclassPropertyAliasesMap( propname, compProps );
 			}
 			else {
 				String[] aliases = new String[prop.getColumnSpan()];
 				String[] cols = new String[prop.getColumnSpan()];
 				Iterator colIter = prop.getColumnIterator();
 				int l = 0;
 				while ( colIter.hasNext() ) {
 					Selectable thing = ( Selectable ) colIter.next();
 					aliases[l] = thing.getAlias( getFactory().getDialect(), prop.getValue().getTable() );
 					cols[l] = thing.getText( getFactory().getDialect() ); // TODO: skip formulas?
 					l++;
 				}
 
 				subclassPropertyAliases.put( propname, aliases );
 				subclassPropertyColumnNames.put( propname, cols );
 			}
 		}
 
 	}
 
 	public Object loadByUniqueKey(
 			String propertyName,
 			Object uniqueKey,
 			SessionImplementor session) throws HibernateException {
 		return getAppropriateUniqueKeyLoader( propertyName, session ).loadByUniqueKey( session, uniqueKey );
 	}
 
 	private EntityLoader getAppropriateUniqueKeyLoader(String propertyName, SessionImplementor session) {
 		final boolean useStaticLoader = !session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& !session.getLoadQueryInfluencers().hasEnabledFetchProfiles()
 				&& propertyName.indexOf('.')<0; //ugly little workaround for fact that createUniqueKeyLoaders() does not handle component properties
 
 		if ( useStaticLoader ) {
 			return ( EntityLoader ) uniqueKeyLoaders.get( propertyName );
 		}
 		else {
 			return createUniqueKeyLoader(
 					propertyMapping.toType( propertyName ),
 					propertyMapping.toColumns( propertyName ),
 					session.getLoadQueryInfluencers()
 			);
 		}
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		return entityMetamodel.getPropertyIndex(propertyName);
 	}
 
 	protected void createUniqueKeyLoaders() throws MappingException {
 		Type[] propertyTypes = getPropertyTypes();
 		String[] propertyNames = getPropertyNames();
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( propertyUniqueness[i] ) {
 				//don't need filters for the static loaders
 				uniqueKeyLoaders.put(
 						propertyNames[i],
 						createUniqueKeyLoader(
 								propertyTypes[i],
 								getPropertyColumnNames( i ),
 								LoadQueryInfluencers.NONE
 						)
 				);
 				//TODO: create uk loaders for component properties
 			}
 		}
 	}
 
 	private EntityLoader createUniqueKeyLoader(
 			Type uniqueKeyType,
 			String[] columns,
 			LoadQueryInfluencers loadQueryInfluencers) {
 		if ( uniqueKeyType.isEntityType() ) {
 			String className = ( ( EntityType ) uniqueKeyType ).getAssociatedEntityName();
 			uniqueKeyType = getFactory().getEntityPersister( className ).getIdentifierType();
 		}
 		return new EntityLoader(
 				this,
 				columns,
 				uniqueKeyType,
 				1,
 				LockMode.NONE,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	protected boolean hasWhere() {
 		return sqlWhereString != null;
 	}
 
 	private void initOrdinaryPropertyPaths(Mapping mapping) throws MappingException {
 		for ( int i = 0; i < getSubclassPropertyNameClosure().length; i++ ) {
 			propertyMapping.initPropertyPaths( getSubclassPropertyNameClosure()[i],
 					getSubclassPropertyTypeClosure()[i],
 					getSubclassPropertyColumnNameClosure()[i],
 					getSubclassPropertyColumnReaderClosure()[i],
 					getSubclassPropertyColumnReaderTemplateClosure()[i],
 					getSubclassPropertyFormulaTemplateClosure()[i],
 					mapping );
 		}
 	}
 
 	private void initIdentifierPropertyPaths(Mapping mapping) throws MappingException {
 		String idProp = getIdentifierPropertyName();
 		if ( idProp != null ) {
 			propertyMapping.initPropertyPaths( idProp, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			propertyMapping.initPropertyPaths( null, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			propertyMapping.initPropertyPaths( ENTITY_ID, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 	}
 
 	private void initDiscriminatorPropertyPath(Mapping mapping) throws MappingException {
 		propertyMapping.initPropertyPaths( ENTITY_CLASS,
 				getDiscriminatorType(),
 				new String[]{getDiscriminatorColumnName()},
 				new String[]{getDiscriminatorColumnReaders()},
 				new String[]{getDiscriminatorColumnReaderTemplate()},
 				new String[]{getDiscriminatorFormulaTemplate()},
 				getFactory() );
 	}
 
 	protected void initPropertyPaths(Mapping mapping) throws MappingException {
 		initOrdinaryPropertyPaths(mapping);
 		initOrdinaryPropertyPaths(mapping); //do two passes, for collection property-ref!
 		initIdentifierPropertyPaths(mapping);
 		if ( entityMetamodel.isPolymorphic() ) {
 			initDiscriminatorPropertyPath( mapping );
 		}
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockMode lockMode,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoaderBuilder.getBuilder( getFactory() )
 				.buildLoader( this, batchSize, lockMode, getFactory(), loadQueryInfluencers );
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockOptions lockOptions,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoaderBuilder.getBuilder( getFactory() )
 				.buildLoader( this, batchSize, lockOptions, getFactory(), loadQueryInfluencers );
 	}
 
 	/**
 	 * Used internally to create static loaders.  These are the default set of loaders used to handle get()/load()
 	 * processing.  lock() handling is done by the LockingStrategy instances (see {@link #getLocker})
 	 *
 	 * @param lockMode The lock mode to apply to the thing being loaded.
 	 * @return
 	 *
 	 * @throws MappingException
 	 */
 	protected UniqueEntityLoader createEntityLoader(LockMode lockMode) throws MappingException {
 		return createEntityLoader( lockMode, LoadQueryInfluencers.NONE );
 	}
 
 	protected boolean check(int rows, Serializable id, int tableNumber, Expectation expectation, PreparedStatement statement) throws HibernateException {
 		try {
 			expectation.verifyOutcome( rows, statement, -1 );
 		}
 		catch( StaleStateException e ) {
 			if ( !isNullableTable( tableNumber ) ) {
 				if ( getFactory().getStatistics().isStatisticsEnabled() ) {
 					getFactory().getStatisticsImplementor()
 							.optimisticFailure( getEntityName() );
 				}
 				throw new StaleObjectStateException( getEntityName(), id );
 			}
 			return false;
 		}
 		catch( TooManyRowsAffectedException e ) {
 			throw new HibernateException(
 					"Duplicate identifier in table for: " +
 					MessageHelper.infoString( this, id, getFactory() )
 			);
 		}
 		catch ( Throwable t ) {
 			return false;
 		}
 		return true;
 	}
 
 	protected String generateUpdateString(boolean[] includeProperty, int j, boolean useRowId) {
 		return generateUpdateString( includeProperty, j, null, useRowId );
 	}
 
 	/**
 	 * Generate the SQL that updates a row by id (and version)
 	 */
 	protected String generateUpdateString(final boolean[] includeProperty,
 										  final int j,
 										  final Object[] oldFields,
 										  final boolean useRowId) {
 
 		Update update = new Update( getFactory().getDialect() ).setTableName( getTableName( j ) );
 
 		// select the correct row by either pk or rowid
 		if ( useRowId ) {
 			update.addPrimaryKeyColumns( new String[]{rowIdName} ); //TODO: eventually, rowIdName[j]
 		}
 		else {
 			update.addPrimaryKeyColumns( getKeyColumns( j ) );
 		}
 
 		boolean hasColumns = false;
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) 
 					&& !lobProperties.contains( i ) ) {
 				// this is a property of the table, which we are updating
 				update.addColumns( getPropertyColumnNames(i),
 						propertyColumnUpdateable[i], propertyColumnWriters[i] );
 				hasColumns = hasColumns || getPropertyColumnSpan( i ) > 0;
 			}
 		}
 		
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this property belongs on the table and is to be inserted
 				update.addColumns( getPropertyColumnNames(i),
 						propertyColumnUpdateable[i], propertyColumnWriters[i] );
 				hasColumns = true;
 			}
 		}
 
 		if ( j == 0 && isVersioned() && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 			// this is the root (versioned) table, and we are using version-based
 			// optimistic locking;  if we are not updating the version, also don't
 			// check it (unless this is a "generated" version column)!
 			if ( checkVersion( includeProperty ) ) {
 				update.setVersionColumnName( getVersionColumnName() );
 				hasColumns = true;
 			}
 		}
 		else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 			// we are using "all" or "dirty" property-based optimistic locking
 
 			boolean[] includeInWhere = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
 					? getPropertyUpdateability() //optimistic-lock="all", include all updatable properties
 					: includeProperty; 			 //optimistic-lock="dirty", include all properties we are updating this time
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				boolean include = includeInWhere[i] &&
 						isPropertyOfTable( i, j ) &&
 						versionability[i];
 				if ( include ) {
 					// this property belongs to the table, and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					String[] propertyColumnWriters = getPropertyColumnWriters( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( oldFields[i], getFactory() );
 					for ( int k=0; k<propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							update.addWhereColumn( propertyColumnNames[k], "=" + propertyColumnWriters[k] );
 						}
 						else {
 							update.addWhereColumn( propertyColumnNames[k], " is null" );
 						}
 					}
 				}
 			}
 
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "update " + getEntityName() );
 		}
 
 		return hasColumns ? update.toStatementString() : null;
 	}
 
 	private boolean checkVersion(final boolean[] includeProperty) {
         return includeProperty[ getVersionProperty() ] ||
 				entityMetamodel.getPropertyUpdateGenerationInclusions()[ getVersionProperty() ] != ValueInclusion.NONE;
 	}
 
 	protected String generateInsertString(boolean[] includeProperty, int j) {
 		return generateInsertString( false, includeProperty, j );
 	}
 
 	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty) {
 		return generateInsertString( identityInsert, includeProperty, 0 );
 	}
 
 	/**
 	 * Generate the SQL that inserts a row
 	 */
 	protected String generateInsertString(boolean identityInsert,
 			boolean[] includeProperty, int j) {
 
 		// todo : remove the identityInsert param and variations;
 		//   identity-insert strings are now generated from generateIdentityInsertString()
 
 		Insert insert = new Insert( getFactory().getDialect() )
 				.setTableName( getTableName( j ) );
 
 		// add normal properties
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			
 			if ( includeProperty[i] && isPropertyOfTable( i, j )
 					&& !lobProperties.contains( i ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i),
 						propertyColumnInsertable[i],
 						propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		if ( j == 0 ) {
 			addDiscriminatorToInsert( insert );
 		}
 
 		// add the primary key
 		if ( j == 0 && identityInsert ) {
 			insert.addIdentityColumn( getKeyColumns( 0 )[0] );
 		}
 		else {
 			insert.addColumns( getKeyColumns( j ) );
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 		
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i),
 						propertyColumnInsertable[i],
 						propertyColumnWriters[i] );
 			}
 		}
 
 		String result = insert.toStatementString();
 
 		// append the SQL to return the generated identifier
 		if ( j == 0 && identityInsert && useInsertSelectIdentity() ) { //TODO: suck into Insert
 			result = getFactory().getDialect().appendIdentitySelectToInsert( result );
 		}
 
 		return result;
 	}
 
 	/**
 	 * Used to generate an insery statement against the root table in the
 	 * case of identifier generation strategies where the insert statement
 	 * executions actually generates the identifier value.
 	 *
 	 * @param includeProperty indices of the properties to include in the
 	 * insert statement.
 	 * @return The insert SQL statement string
 	 */
 	protected String generateIdentityInsertString(boolean[] includeProperty) {
 		Insert insert = identityDelegate.prepareIdentifierGeneratingInsert();
 		insert.setTableName( getTableName( 0 ) );
 
 		// add normal properties except lobs
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) && !lobProperties.contains( i ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// HHH-4635 & HHH-8103
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) ) {
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		addDiscriminatorToInsert( insert );
 
 		// delegate already handles PK columns
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 
 		return insert.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL that deletes a row by id (and version)
 	 */
 	protected String generateDeleteString(int j) {
 		Delete delete = new Delete()
 				.setTableName( getTableName( j ) )
 				.addPrimaryKeyColumns( getKeyColumns( j ) );
 		if ( j == 0 ) {
 			delete.setVersionColumnName( getVersionColumnName() );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( "delete " + getEntityName() );
 		}
 		return delete.toStatementString();
 	}
 
 	protected int dehydrate(
 			Serializable id,
 			Object[] fields,
 			boolean[] includeProperty,
 			boolean[][] includeColumns,
 			int j,
 			PreparedStatement st,
 			SessionImplementor session,
 			boolean isUpdate) throws HibernateException, SQLException {
 		return dehydrate( id, fields, null, includeProperty, includeColumns, j, st, session, 1, isUpdate );
 	}
 
 	/**
 	 * Marshall the fields of a persistent instance to a prepared statement
 	 */
 	protected int dehydrate(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final boolean[][] includeColumns,
 	        final int j,
 	        final PreparedStatement ps,
 	        final SessionImplementor session,
 	        int index,
 	        boolean isUpdate ) throws SQLException, HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Dehydrating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j )
 					&& !lobProperties.contains( i )) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 		
 		if ( !isUpdate ) {
 			index += dehydrateId( id, rowId, ps, session, index );
 		}
 		
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 		
 		if ( isUpdate ) {
 			index += dehydrateId( id, rowId, ps, session, index );
 		}
 
 		return index;
 
 	}
 	
 	private int dehydrateId( 
 			final Serializable id,
 			final Object rowId,
 			final PreparedStatement ps,
 	        final SessionImplementor session,
 			int index ) throws SQLException {
 		if ( rowId != null ) {
 			ps.setObject( index, rowId );
 			return 1;
 		} else if ( id != null ) {
 			getIdentifierType().nullSafeSet( ps, id, index, session );
 			return getIdentifierColumnSpan();
 		}
 		return 0;
 	}
 
 	/**
 	 * Unmarshall the fields of a persistent instance from a result set,
 	 * without resolving associations or collections. Question: should
 	 * this really be here, or should it be sent back to Loader?
 	 */
 	public Object[] hydrate(
 			final ResultSet rs,
 	        final Serializable id,
 	        final Object object,
 	        final Loadable rootLoadable,
 	        final String[][] suffixedPropertyColumns,
 	        final boolean allProperties,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Hydrating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		final AbstractEntityPersister rootPersister = (AbstractEntityPersister) rootLoadable;
 
 		final boolean hasDeferred = rootPersister.hasSequentialSelect();
 		PreparedStatement sequentialSelect = null;
 		ResultSet sequentialResultSet = null;
 		boolean sequentialSelectEmpty = false;
 		try {
 
 			if ( hasDeferred ) {
 				final String sql = rootPersister.getSequentialSelect( getEntityName() );
 				if ( sql != null ) {
 					//TODO: I am not so sure about the exception handling in this bit!
 					sequentialSelect = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql );
 					rootPersister.getIdentifierType().nullSafeSet( sequentialSelect, id, 1, session );
 					sequentialResultSet = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( sequentialSelect );
 					if ( !sequentialResultSet.next() ) {
 						// TODO: Deal with the "optional" attribute in the <join> mapping;
 						// this code assumes that optional defaults to "true" because it
 						// doesn't actually seem to work in the fetch="join" code
 						//
 						// Note that actual proper handling of optional-ality here is actually
 						// more involved than this patch assumes.  Remember that we might have
 						// multiple <join/> mappings associated with a single entity.  Really
 						// a couple of things need to happen to properly handle optional here:
 						//  1) First and foremost, when handling multiple <join/>s, we really
 						//      should be using the entity root table as the driving table;
 						//      another option here would be to choose some non-optional joined
 						//      table to use as the driving table.  In all likelihood, just using
 						//      the root table is much simplier
 						//  2) Need to add the FK columns corresponding to each joined table
 						//      to the generated select list; these would then be used when
 						//      iterating the result set to determine whether all non-optional
 						//      data is present
 						// My initial thoughts on the best way to deal with this would be
 						// to introduce a new SequentialSelect abstraction that actually gets
 						// generated in the persisters (ok, SingleTable...) and utilized here.
 						// It would encapsulated all this required optional-ality checking...
 						sequentialSelectEmpty = true;
 					}
 				}
 			}
 
 			final String[] propNames = getPropertyNames();
 			final Type[] types = getPropertyTypes();
 			final Object[] values = new Object[types.length];
 			final boolean[] laziness = getPropertyLaziness();
 			final String[] propSubclassNames = getSubclassPropertySubclassNameClosure();
 
 			for ( int i = 0; i < types.length; i++ ) {
 				if ( !propertySelectable[i] ) {
 					values[i] = BackrefPropertyAccessor.UNKNOWN;
 				}
 				else if ( allProperties || !laziness[i] ) {
 					//decide which ResultSet to get the property value from:
 					final boolean propertyIsDeferred = hasDeferred &&
 							rootPersister.isSubclassPropertyDeferred( propNames[i], propSubclassNames[i] );
 					if ( propertyIsDeferred && sequentialSelectEmpty ) {
 						values[i] = null;
 					}
 					else {
 						final ResultSet propertyResultSet = propertyIsDeferred ? sequentialResultSet : rs;
 						final String[] cols = propertyIsDeferred ? propertyColumnAliases[i] : suffixedPropertyColumns[i];
 						values[i] = types[i].hydrate( propertyResultSet, cols, session, object );
 					}
 				}
 				else {
 					values[i] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 				}
 			}
 
 			if ( sequentialResultSet != null ) {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( sequentialResultSet, sequentialSelect );
 			}
 
 			return values;
 
 		}
 		finally {
 			if ( sequentialSelect != null ) {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( sequentialSelect );
 			}
 		}
 	}
 
 	protected boolean useInsertSelectIdentity() {
 		return !useGetGeneratedKeys() && getFactory().getDialect().supportsInsertSelectIdentity();
 	}
 
 	protected boolean useGetGeneratedKeys() {
 		return getFactory().getSettings().isGetGeneratedKeysEnabled();
 	}
 
 	protected String getSequentialSelect(String entityName) {
 		throw new UnsupportedOperationException("no sequential selects");
 	}
 
 	/**
 	 * Perform an SQL INSERT, and then retrieve a generated identifier.
 	 * <p/>
 	 * This form is used for PostInsertIdentifierGenerator-style ids (IDENTITY,
 	 * select, etc).
 	 */
 	protected Serializable insert(
 			final Object[] fields,
 	        final boolean[] notNull,
 	        String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Inserting entity: {0} (native id)", getEntityName() );
 			if ( isVersioned() ) {
 				LOG.tracev( "Version: {0}", Versioning.getVersion( fields, this ) );
 			}
 		}
 
 		Binder binder = new Binder() {
 			public void bindValues(PreparedStatement ps) throws SQLException {
 				dehydrate( null, fields, notNull, propertyColumnInsertable, 0, ps, session, false );
 			}
 			public Object getEntity() {
 				return object;
 			}
 		};
 
 		return identityDelegate.performInsert( sql, session, binder );
 	}
 
 	public String getIdentitySelectString() {
 		//TODO: cache this in an instvar
 		return getFactory().getDialect().getIdentitySelectString(
 				getTableName(0),
 				getKeyColumns(0)[0],
 				getIdentifierType().sqlTypes( getFactory() )[0]
 		);
 	}
 
 	public String getSelectByUniqueKeyString(String propertyName) {
 		return new SimpleSelect( getFactory().getDialect() )
 			.setTableName( getTableName(0) )
 			.addColumns( getKeyColumns(0) )
 			.addCondition( getPropertyColumnNames(propertyName), "=?" )
 			.toStatementString();
 	}
 
 	private BasicBatchKey inserBatchKey;
 
 	/**
 	 * Perform an SQL INSERT.
 	 * <p/>
 	 * This for is used for all non-root tables as well as the root table
 	 * in cases where the identifier value is known before the insert occurs.
 	 */
 	protected void insert(
 			final Serializable id,
 	        final Object[] fields,
 	        final boolean[] notNull,
 	        final int j,
 	        final String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		//note: it is conceptually possible that a UserType could map null to
 		//	  a non-null value, so the following is arguable:
 		if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 			return;
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Inserting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( j == 0 && isVersioned() )
 				LOG.tracev( "Version: {0}", Versioning.getVersion( fields, this ) );
 		}
 
 		// TODO : shouldn't inserts be Expectations.NONE?
 		final Expectation expectation = Expectations.appropriateExpectation( insertResultCheckStyles[j] );
 		// we can't batch joined inserts, *especially* not if it is an identity insert;
 		// nor can we batch statements where the expectation is based on an output param
 		final boolean useBatch = j == 0 && expectation.canBeBatched();
 		if ( useBatch && inserBatchKey == null ) {
 			inserBatchKey = new BasicBatchKey(
 					getEntityName() + "#INSERT",
 					expectation
 			);
 		}
 		final boolean callable = isInsertCallable( j );
 
 		try {
 			// Render the SQL query
 			final PreparedStatement insert;
 			if ( useBatch ) {
 				insert = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( inserBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				insert = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				int index = 1;
 				index += expectation.prepare( insert );
 
 				// Write the values of fields onto the prepared statement - we MUST use the state at the time the
 				// insert was issued (cos of foreign key constraints). Not necessarily the object's current state
 
 				dehydrate( id, fields, null, notNull, propertyColumnInsertable, j, insert, session, index, false );
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( inserBatchKey ).addToBatch();
 				}
 				else {
 					expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( insert ), insert, -1 );
 				}
 
 			}
 			catch ( SQLException e ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw e;
 			}
 			finally {
 				if ( !useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( insert );
 				}
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not insert: " + MessageHelper.infoString( this ),
 					sql
 			);
 		}
 
 	}
 
 	/**
 	 * Perform an SQL UPDATE or SQL INSERT
 	 */
 	protected void updateOrInsert(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( !isInverseTable( j ) ) {
 
 			final boolean isRowToUpdate;
 			if ( isNullableTable( j ) && oldFields != null && isAllNull( oldFields, j ) ) {
 				//don't bother trying to update, we know there is no row there yet
 				isRowToUpdate = false;
 			}
 			else if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 				//if all fields are null, we might need to delete existing row
 				isRowToUpdate = true;
 				delete( id, oldVersion, j, object, getSQLDeleteStrings()[j], session, null );
 			}
 			else {
 				//there is probably a row there, so try to update
 				//if no rows were updated, we will find out
 				isRowToUpdate = update( id, fields, oldFields, rowId, includeProperty, j, oldVersion, object, sql, session );
 			}
 
 			if ( !isRowToUpdate && !isAllNull( fields, j ) ) {
 				// assume that the row was not there since it previously had only null
 				// values, so do an INSERT instead
 				//TODO: does not respect dynamic-insert
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 
 		}
 
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
index f29f55fa4e..1eb0fa6bdd 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
@@ -1,768 +1,772 @@
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
 import java.util.Map;
+import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.OptimisticCacheSource;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Contract describing mapping information and persistence logic for a particular strategy of entity mapping.  A given
  * persister instance corresponds to a given mapped entity class.
  * <p/>
  * Implementations must be thread-safe (preferably immutable).
  *
  * @author Gavin King
  * @author Steve Ebersole
  *
  * @see org.hibernate.persister.spi.PersisterFactory
  * @see org.hibernate.persister.spi.PersisterClassResolver
  */
 public interface EntityPersister extends OptimisticCacheSource, EntityDefinition {
 
 	/**
 	 * The property name of the "special" identifier property in HQL
 	 */
 	public static final String ENTITY_ID = "id";
 
 	/**
 	 * Generate the entity definition for this object. This must be done for all
 	 * entity persisters before calling {@link #postInstantiate()}.
 	 */
 	public void generateEntityDefinition();
 
 	/**
 	 * Finish the initialization of this object. {@link #generateEntityDefinition()}
 	 * must be called for all entity persisters before calling this method.
 	 * <p/>
 	 * Called only once per {@link org.hibernate.SessionFactory} lifecycle,
 	 * after all entity persisters have been instantiated.
 	 *
 	 * @throws org.hibernate.MappingException Indicates an issue in the metadata.
 	 */
 	public void postInstantiate() throws MappingException;
 
 	/**
 	 * Return the SessionFactory to which this persister "belongs".
 	 *
 	 * @return The owning SessionFactory.
 	 */
 	public SessionFactoryImplementor getFactory();
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     // stuff that is persister-centric and/or EntityInfo-centric ~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Returns an object that identifies the space in which identifiers of
 	 * this entity hierarchy are unique.  Might be a table name, a JNDI URL, etc.
 	 *
 	 * @return The root entity name.
 	 */
 	public String getRootEntityName();
 
 	/**
 	 * The entity name which this persister maps.
 	 *
 	 * @return The name of the entity which this persister maps.
 	 */
 	public String getEntityName();
 
 	/**
 	 * Retrieve the underlying entity metamodel instance...
 	 *
 	 *@return The metamodel
 	 */
 	public EntityMetamodel getEntityMetamodel();
 
 	/**
 	 * Determine whether the given name represents a subclass entity
 	 * (or this entity itself) of the entity mapped by this persister.
 	 *
 	 * @param entityName The entity name to be checked.
 	 * @return True if the given entity name represents either the entity
 	 * mapped by this persister or one of its subclass entities; false
 	 * otherwise.
 	 */
 	public boolean isSubclassEntityName(String entityName);
 
 	/**
 	 * Returns an array of objects that identify spaces in which properties of
 	 * this entity are persisted, for instances of this class only.
 	 * <p/>
 	 * For most implementations, this returns the complete set of table names
 	 * to which instances of the mapped entity are persisted (not accounting
 	 * for superclass entity mappings).
 	 *
 	 * @return The property spaces.
 	 */
 	public Serializable[] getPropertySpaces();
 
 	/**
 	 * Returns an array of objects that identify spaces in which properties of
 	 * this entity are persisted, for instances of this class and its subclasses.
 	 * <p/>
 	 * Much like {@link #getPropertySpaces()}, except that here we include subclass
 	 * entity spaces.
 	 *
 	 * @return The query spaces.
 	 */
 	public Serializable[] getQuerySpaces();
 
 	/**
 	 * Determine whether this entity supports dynamic proxies.
 	 *
 	 * @return True if the entity has dynamic proxy support; false otherwise.
 	 */
 	public boolean hasProxy();
 
 	/**
 	 * Determine whether this entity contains references to persistent collections.
 	 *
 	 * @return True if the entity does contain persistent collections; false otherwise.
 	 */
 	public boolean hasCollections();
 
 	/**
 	 * Determine whether any properties of this entity are considered mutable.
 	 *
 	 * @return True if any properties of the entity are mutable; false otherwise (meaning none are).
 	 */
 	public boolean hasMutableProperties();
 
 	/**
 	 * Determine whether this entity contains references to persistent collections
 	 * which are fetchable by subselect?
 	 *
 	 * @return True if the entity contains collections fetchable by subselect; false otherwise.
 	 */
 	public boolean hasSubselectLoadableCollections();
 
 	/**
 	 * Determine whether this entity has any non-none cascading.
 	 *
 	 * @return True if the entity has any properties with a cascade other than NONE;
 	 * false otherwise (aka, no cascading).
 	 */
 	public boolean hasCascades();
 
 	/**
 	 * Determine whether instances of this entity are considered mutable.
 	 *
 	 * @return True if the entity is considered mutable; false otherwise.
 	 */
 	public boolean isMutable();
 
 	/**
 	 * Determine whether the entity is inherited one or more other entities.
 	 * In other words, is this entity a subclass of other entities.
 	 *
 	 * @return True if other entities extend this entity; false otherwise.
 	 */
 	public boolean isInherited();
 
 	/**
 	 * Are identifiers of this entity assigned known before the insert execution?
 	 * Or, are they generated (in the database) by the insert execution.
 	 *
 	 * @return True if identifiers for this entity are generated by the insert
 	 * execution.
 	 */
 	public boolean isIdentifierAssignedByInsert();
 
 	/**
 	 * Get the type of a particular property by name.
 	 *
 	 * @param propertyName The name of the property for which to retrieve
 	 * the type.
 	 * @return The type.
 	 * @throws org.hibernate.MappingException Typically indicates an unknown
 	 * property name.
 	 */
 	public Type getPropertyType(String propertyName) throws MappingException;
 
 	/**
 	 * Compare the two snapshots to determine if they represent dirty state.
 	 *
 	 * @param currentState The current snapshot
 	 * @param previousState The baseline snapshot
 	 * @param owner The entity containing the state
 	 * @param session The originating session
 	 * @return The indices of all dirty properties, or null if no properties
 	 * were dirty.
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session);
 
 	/**
 	 * Compare the two snapshots to determine if they represent modified state.
 	 *
 	 * @param old The baseline snapshot
 	 * @param current The current snapshot
 	 * @param object The entity containing the state
 	 * @param session The originating session
 	 * @return The indices of all modified properties, or null if no properties
 	 * were modified.
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session);
 
 	/**
 	 * Determine whether the entity has a particular property holding
 	 * the identifier value.
 	 *
 	 * @return True if the entity has a specific property holding identifier value.
 	 */
 	public boolean hasIdentifierProperty();
 
 	/**
 	 * Determine whether detached instances of this entity carry their own
 	 * identifier value.
 	 * <p/>
 	 * The other option is the deprecated feature where users could supply
 	 * the id during session calls.
 	 *
 	 * @return True if either (1) {@link #hasIdentifierProperty()} or
 	 * (2) the identifier is an embedded composite identifier; false otherwise.
 	 */
 	public boolean canExtractIdOutOfEntity();
 
 	/**
 	 * Determine whether optimistic locking by column is enabled for this
 	 * entity.
 	 *
 	 * @return True if optimistic locking by column (i.e., <version/> or
 	 * <timestamp/>) is enabled; false otherwise.
 	 */
 	public boolean isVersioned();
 
 	/**
 	 * If {@link #isVersioned()}, then what is the type of the property
 	 * holding the locking value.
 	 *
 	 * @return The type of the version property; or null, if not versioned.
 	 */
 	public VersionType getVersionType();
 
 	/**
 	 * If {@link #isVersioned()}, then what is the index of the property
 	 * holding the locking value.
 	 *
 	 * @return The type of the version property; or -66, if not versioned.
 	 */
 	public int getVersionProperty();
 
 	/**
 	 * Determine whether this entity defines a natural identifier.
 	 *
 	 * @return True if the entity defines a natural id; false otherwise.
 	 */
 	public boolean hasNaturalIdentifier();
 
 	/**
 	 * If the entity defines a natural id ({@link #hasNaturalIdentifier()}), which
 	 * properties make up the natural id.
 	 *
 	 * @return The indices of the properties making of the natural id; or
 	 * null, if no natural id is defined.
 	 */
 	public int[] getNaturalIdentifierProperties();
 
 	/**
 	 * Retrieve the current state of the natural-id properties from the database.
 	 *
 	 * @param id The identifier of the entity for which to retrieve the natural-id values.
 	 * @param session The session from which the request originated.
 	 * @return The natural-id snapshot.
 	 */
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session);
 
 	/**
 	 * Determine which identifier generation strategy is used for this entity.
 	 *
 	 * @return The identifier generation strategy.
 	 */
 	public IdentifierGenerator getIdentifierGenerator();
 
 	/**
 	 * Determine whether this entity defines any lazy properties (ala
 	 * bytecode instrumentation).
 	 *
 	 * @return True if the entity has properties mapped as lazy; false otherwise.
 	 */
 	public boolean hasLazyProperties();
 
 	/**
 	 * Load the id for the entity based on the natural id.
 	 */
 	public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 			SessionImplementor session);
 
 	/**
 	 * Load an instance of the persistent class.
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Load an instance of the persistent class.
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Do a version check (optional operation)
 	 */
 	public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Do a version check (optional operation)
 	 */
 	public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Persist an instance
 	 */
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Persist an instance, using a natively generated identifier (optional operation)
 	 */
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Delete a persistent instance
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Update a persistent instance
 	 */
 	public void update(
 		Serializable id,
 		Object[] fields,
 		int[] dirtyFields,
 		boolean hasDirtyCollection,
 		Object[] oldFields,
 		Object oldVersion,
 		Object object,
 		Object rowId,
 		SessionImplementor session
 	) throws HibernateException;
 
 	/**
 	 * Get the Hibernate types of the class properties
 	 */
 	public Type[] getPropertyTypes();
 
 	/**
 	 * Get the names of the class properties - doesn't have to be the names of the
 	 * actual Java properties (used for XML generation only)
 	 */
 	public String[] getPropertyNames();
 
 	/**
 	 * Get the "insertability" of the properties of this class
 	 * (does the property appear in an SQL INSERT)
 	 */
 	public boolean[] getPropertyInsertability();
 
 	/**
 	 * Which of the properties of this class are database generated values on insert?
 	 */
 	public ValueInclusion[] getPropertyInsertGenerationInclusions();
 
 	/**
 	 * Which of the properties of this class are database generated values on update?
 	 */
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions();
 
 	/**
 	 * Get the "updateability" of the properties of this class
 	 * (does the property appear in an SQL UPDATE)
 	 */
 	public boolean[] getPropertyUpdateability();
 
 	/**
 	 * Get the "checkability" of the properties of this class
 	 * (is the property dirty checked, does the cache need
 	 * to be updated)
 	 */
 	public boolean[] getPropertyCheckability();
 
 	/**
 	 * Get the nullability of the properties of this class
 	 */
 	public boolean[] getPropertyNullability();
 
 	/**
 	 * Get the "versionability" of the properties of this class
 	 * (is the property optimistic-locked)
 	 */
 	public boolean[] getPropertyVersionability();
 	public boolean[] getPropertyLaziness();
 	/**
 	 * Get the cascade styles of the properties (optional operation)
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles();
 
 	/**
 	 * Get the identifier type
 	 */
 	public Type getIdentifierType();
 
 	/**
 	 * Get the name of the identifier property (or return null) - need not return the
 	 * name of an actual Java property
 	 */
 	public String getIdentifierPropertyName();
 
 	/**
 	 * Should we always invalidate the cache instead of
 	 * recaching updated state
 	 */
 	public boolean isCacheInvalidationRequired();
 	/**
 	 * Should lazy properties of this entity be cached?
 	 */
 	public boolean isLazyPropertiesCacheable();
 	/**
 	 * Does this class have a cache.
 	 */
 	public boolean hasCache();
 	/**
 	 * Get the cache (optional operation)
 	 */
 	public EntityRegionAccessStrategy getCacheAccessStrategy();
 	/**
 	 * Get the cache structure
 	 */
 	public CacheEntryStructure getCacheEntryStructure();
 
 	public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session);
 
 	/**
 	 * Does this class have a natural id cache
 	 */
 	public boolean hasNaturalIdCache();
 	
 	/**
 	 * Get the NaturalId cache (optional operation)
 	 */
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy();
 
 	/**
 	 * Get the user-visible metadata for the class (optional operation)
 	 */
 	public ClassMetadata getClassMetadata();
 
 	/**
 	 * Is batch loading enabled?
 	 */
 	public boolean isBatchLoadable();
 
 	/**
 	 * Is select snapshot before update enabled?
 	 */
 	public boolean isSelectBeforeUpdateRequired();
 
 	/**
 	 * Get the current database state of the object, in a "hydrated" form, without
 	 * resolving identifiers
 	 * @return null if there is no row in the database
 	 */
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session);
 
 	/**
 	 * Get the current version of the object, or return null if there is no row for
 	 * the given identifier. In the case of unversioned data, return any object
 	 * if the row exists.
 	 */
 	public Object getCurrentVersion(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Has the class actually been bytecode instrumented?
 	 */
 	public boolean isInstrumented();
 
 	/**
 	 * Does this entity define any properties as being database generated on insert?
 	 *
 	 * @return True if this entity contains at least one property defined
 	 * as generated (including version property, but not identifier).
 	 */
 	public boolean hasInsertGeneratedProperties();
 
 	/**
 	 * Does this entity define any properties as being database generated on update?
 	 *
 	 * @return True if this entity contains at least one property defined
 	 * as generated (including version property, but not identifier).
 	 */
 	public boolean hasUpdateGeneratedProperties();
 
 	/**
 	 * Does this entity contain a version property that is defined
 	 * to be database generated?
 	 *
 	 * @return true if this entity contains a version property and that
 	 * property has been marked as generated.
 	 */
 	public boolean isVersionPropertyGenerated();
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff that is tuplizer-centric, but is passed a session ~~~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Called just after the entities properties have been initialized
 	 */
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session);
 
 	/**
 	 * Called just after the entity has been reassociated with the session
 	 */
 	public void afterReassociate(Object entity, SessionImplementor session);
 
 	/**
 	 * Create a new proxy instance
 	 */
 	public Object createProxy(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Is this a new transient instance?
 	 */
 	public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Return the values of the insertable properties of the object (including backrefs)
 	 */
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Perform a select to retrieve the values of any generated properties
 	 * back from the database, injecting these generated values into the
 	 * given entity as well as writing this state to the
 	 * {@link org.hibernate.engine.spi.PersistenceContext}.
 	 * <p/>
 	 * Note, that because we update the PersistenceContext here, callers
 	 * need to take care that they have already written the initial snapshot
 	 * to the PersistenceContext before calling this method.
 	 *
 	 * @param id The entity's id value.
 	 * @param entity The entity for which to get the state.
 	 * @param state
 	 * @param session The session
 	 */
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session);
 	/**
 	 * Perform a select to retrieve the values of any generated properties
 	 * back from the database, injecting these generated values into the
 	 * given entity as well as writing this state to the
 	 * {@link org.hibernate.engine.spi.PersistenceContext}.
 	 * <p/>
 	 * Note, that because we update the PersistenceContext here, callers
 	 * need to take care that they have already written the initial snapshot
 	 * to the PersistenceContext before calling this method.
 	 *
 	 * @param id The entity's id value.
 	 * @param entity The entity for which to get the state.
 	 * @param state
 	 * @param session The session
 	 */
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session);
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff that is Tuplizer-centric ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The persistent class, or null
 	 */
 	public Class getMappedClass();
 
 	/**
 	 * Does the class implement the {@link org.hibernate.classic.Lifecycle} interface.
 	 */
 	public boolean implementsLifecycle();
 
 	/**
 	 * Get the proxy interface that instances of <em>this</em> concrete class will be
 	 * cast to (optional operation).
 	 */
 	public Class getConcreteProxyClass();
 
 	/**
 	 * Set the given values to the mapped properties of the given object
 	 */
 	public void setPropertyValues(Object object, Object[] values);
 
 	/**
 	 * Set the value of a particular property
 	 */
 	public void setPropertyValue(Object object, int i, Object value);
 
 	/**
 	 * Return the (loaded) values of the mapped properties of the object (not including backrefs)
 	 */
 	public Object[] getPropertyValues(Object object);
 
 	/**
 	 * Get the value of a particular property
 	 */
 	public Object getPropertyValue(Object object, int i) throws HibernateException;
 
 	/**
 	 * Get the value of a particular property
 	 */
 	public Object getPropertyValue(Object object, String propertyName);
 
 	/**
 	 * Get the identifier of an instance (throw an exception if no identifier property)
 	 *
 	 * @deprecated Use {@link #getIdentifier(Object,SessionImplementor)} instead
 	 */
 	@SuppressWarnings( {"JavaDoc"})
 	public Serializable getIdentifier(Object object) throws HibernateException;
 
 	/**
 	 * Get the identifier of an instance (throw an exception if no identifier property)
 	 *
 	 * @param entity The entity for which to get the identifier
 	 * @param session The session from which the request originated
 	 *
 	 * @return The identifier
 	 */
 	public Serializable getIdentifier(Object entity, SessionImplementor session);
 
     /**
      * Inject the identifier value into the given entity.
      *
      * @param entity The entity to inject with the identifier value.
      * @param id The value to be injected as the identifier.
 	 * @param session The session from which is requests originates
      */
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session);
 
 	/**
 	 * Get the version number (or timestamp) from the object's version property (or return null if not versioned)
 	 */
 	public Object getVersion(Object object) throws HibernateException;
 
 	/**
 	 * Create a class instance initialized with the given identifier
 	 *
 	 * @param id The identifier value to use (may be null to represent no value)
 	 * @param session The session from which the request originated.
 	 *
 	 * @return The instantiated entity.
 	 */
 	public Object instantiate(Serializable id, SessionImplementor session);
 
 	/**
 	 * Is the given object an instance of this entity?
 	 */
 	public boolean isInstance(Object object);
 
 	/**
 	 * Does the given instance have any uninitialized lazy properties?
 	 */
 	public boolean hasUninitializedLazyProperties(Object object);
 
 	/**
 	 * Set the identifier and version of the given instance back to its "unsaved" value.
 	 *
 	 * @param entity The entity instance
 	 * @param currentId The currently assigned identifier value.
 	 * @param currentVersion The currently assigned version value.
 	 * @param session The session from which the request originated.
 	 */
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session);
 
 	/**
 	 * A request has already identified the entity-name of this persister as the mapping for the given instance.
 	 * However, we still need to account for possible subclassing and potentially re-route to the more appropriate
 	 * persister.
 	 * <p/>
 	 * For example, a request names <tt>Animal</tt> as the entity-name which gets resolved to this persister.  But the
 	 * actual instance is really an instance of <tt>Cat</tt> which is a subclass of <tt>Animal</tt>.  So, here the
 	 * <tt>Animal</tt> persister is being asked to return the persister specific to <tt>Cat</tt>.
 	 * <p/>
 	 * It is also possible that the instance is actually an <tt>Animal</tt> instance in the above example in which
 	 * case we would return <tt>this</tt> from this method.
 	 *
 	 * @param instance The entity instance
 	 * @param factory Reference to the SessionFactory
 	 *
 	 * @return The appropriate persister
 	 *
 	 * @throws HibernateException Indicates that instance was deemed to not be a subclass of the entity mapped by
 	 * this persister.
 	 */
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory);
 
 	public EntityMode getEntityMode();
 	public EntityTuplizer getEntityTuplizer();
 
 	public EntityInstrumentationMetadata getInstrumentationMetadata();
 	
 	public FilterAliasGenerator getFilterAliasGenerator(final String rootAlias);
+
+    public int[] resolveAttributeIndexes(Set<String> properties);
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/enhance/EnhancementTask.java b/hibernate-core/src/main/java/org/hibernate/tool/enhance/EnhancementTask.java
index ae36dd0285..1040f4aa71 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/enhance/EnhancementTask.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/enhance/EnhancementTask.java
@@ -1,192 +1,228 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.tool.enhance;
 
+import javax.persistence.ElementCollection;
+import javax.persistence.Embeddable;
 import javax.persistence.Entity;
+import javax.persistence.ManyToMany;
+import javax.persistence.OneToMany;
 import javax.persistence.Transient;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.List;
 
 import javassist.ClassPool;
 import javassist.CtClass;
 import javassist.CtField;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.types.FileSet;
 
 import org.hibernate.bytecode.enhance.spi.EnhancementContext;
 import org.hibernate.bytecode.enhance.spi.Enhancer;
 
 /**
  * Ant task for performing build-time enhancement of entities and component/embeddable classes.
  * <p/>
  * IMPL NOTE : currently makes numerous assumptions, the most "horrific" being that all entities are
  * annotated @Entity which precludes {@code hbm.xml} mappings as well as complete {@code orm.xml} mappings.  This is
  * just a PoC though...
  *
  * @author Steve Ebersole
  *
  * @see org.hibernate.engine.spi.Managed
  */
 public class EnhancementTask extends Task implements EnhancementContext {
 	private List<FileSet> filesets = new ArrayList<FileSet>();
 
 	// Enhancer also builds CtClass instances.  Might make sense to share these (ClassPool).
 	private final ClassPool classPool = new ClassPool( false );
 	private final Enhancer enhancer = new Enhancer( this );
 
 	public void addFileset(FileSet set) {
 		this.filesets.add( set );
 	}
 
 	@Override
 	public void execute() throws BuildException {
 		log( "Starting Hibernate EnhancementTask execution", Project.MSG_INFO );
 
 		// we use the CtClass stuff here just as a simple vehicle for obtaining low level information about
 		// the class(es) contained in a file while still maintaining easy access to the underlying byte[]
 		final Project project = getProject();
 
 		for ( FileSet fileSet : filesets ) {
 			final File fileSetBaseDir = fileSet.getDir( project );
 			final DirectoryScanner directoryScanner = fileSet.getDirectoryScanner( project );
 			for ( String relativeIncludedFileName : directoryScanner.getIncludedFiles() ) {
 				final File javaClassFile = new File( fileSetBaseDir, relativeIncludedFileName );
 				if ( ! javaClassFile.exists() ) {
 					continue;
 				}
 
-				processClassFile( javaClassFile );
+				processClassFile( javaClassFile);
 			}
 		}
+
 	}
 
-	private void processClassFile(File javaClassFile) {
+    /**
+     * Atm only process files annotated with either @Entity or @Embeddable
+     * @param javaClassFile
+     */
+    private void processClassFile(File javaClassFile) {
 		try {
 			final CtClass ctClass = classPool.makeClass( new FileInputStream( javaClassFile ) );
-			if ( ! shouldInclude( ctClass ) ) {
-				return;
-			}
-
-			final byte[] enhancedBytecode;
-			try {
-				enhancedBytecode = enhancer.enhance( ctClass.getName(), ctClass.toBytecode() );
-			}
-			catch (Exception e) {
-				log( "Unable to enhance class [" + ctClass.getName() + "]", e, Project.MSG_WARN );
-				return;
-			}
-
+            if(this.isEntityClass(ctClass))
+                processEntityClassFile(javaClassFile, ctClass);
+            else if(this.isCompositeClass(ctClass))
+                processCompositeClassFile(javaClassFile, ctClass);
+
+        }
+        catch (IOException e) {
+            throw new BuildException(
+                    String.format( "Error processing included file [%s]", javaClassFile.getAbsolutePath() ), e );
+        }
+    }
+
+    private void processEntityClassFile(File javaClassFile, CtClass ctClass ) {
+        try {
+            byte[] result = enhancer.enhance( ctClass.getName(), ctClass.toBytecode() );
+            if(result != null)
+                writeEnhancedClass(javaClassFile, result);
+        }
+        catch (Exception e) {
+            log( "Unable to enhance class [" + ctClass.getName() + "]", e, Project.MSG_WARN );
+            return;
+        }
+    }
+
+    private void processCompositeClassFile(File javaClassFile, CtClass ctClass) {
+        try {
+            byte[] result = enhancer.enhanceComposite(ctClass.getName(), ctClass.toBytecode());
+            if(result != null)
+                writeEnhancedClass(javaClassFile, result);
+        }
+        catch (Exception e) {
+            log( "Unable to enhance class [" + ctClass.getName() + "]", e, Project.MSG_WARN );
+            return;
+        }
+    }
+
+    private void writeEnhancedClass(File javaClassFile, byte[] result) {
+        try {
 			if ( javaClassFile.delete() ) {
-				if ( ! javaClassFile.createNewFile() ) {
-					log( "Unable to recreate class file [" + ctClass.getName() + "]", Project.MSG_INFO );
-				}
-			}
+                    if ( ! javaClassFile.createNewFile() ) {
+                        log( "Unable to recreate class file [" + javaClassFile.getName() + "]", Project.MSG_INFO );
+                    }
+            }
 			else {
-				log( "Unable to delete class file [" + ctClass.getName() + "]", Project.MSG_INFO );
+				log( "Unable to delete class file [" + javaClassFile.getName() + "]", Project.MSG_INFO );
 			}
 
 			FileOutputStream outputStream = new FileOutputStream( javaClassFile, false );
 			try {
-				outputStream.write( enhancedBytecode );
+				outputStream.write( result);
 				outputStream.flush();
 			}
 			finally {
 				try {
 					outputStream.close();
 				}
 				catch ( IOException ignore) {
 				}
 			}
-		}
-		catch (FileNotFoundException ignore) {
-			// should not ever happen because of explicit checks
-		}
-		catch (IOException e) {
-			throw new BuildException(
-					String.format( "Error processing included file [%s]", javaClassFile.getAbsolutePath() ),
-					e
-			);
-		}
-	}
-
-	private boolean shouldInclude(CtClass ctClass) {
-		// we currently only handle entity enhancement
-		return ctClass.hasAnnotation( Entity.class );
-	}
-
+        }
+        catch (FileNotFoundException ignore) {
+            // should not ever happen because of explicit checks
+        }
+        catch (IOException e) {
+            throw new BuildException(
+                    String.format( "Error processing included file [%s]", javaClassFile.getAbsolutePath() ), e );
+        }
+    }
 
 	// EnhancementContext impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
 	@Override
 	public ClassLoader getLoadingClassLoader() {
 		return getClass().getClassLoader();
 	}
 
 	@Override
 	public boolean isEntityClass(CtClass classDescriptor) {
-		// currently we only call enhance on the classes with @Entity, so here we always return true
-		return true;
-	}
+        return classDescriptor.hasAnnotation(Entity.class);
+    }
 
 	@Override
 	public boolean isCompositeClass(CtClass classDescriptor) {
-		return false;
+        return classDescriptor.hasAnnotation(Embeddable.class);
 	}
 
 	@Override
 	public boolean doDirtyCheckingInline(CtClass classDescriptor) {
-		return false;
+		return true;
 	}
 
 	@Override
 	public boolean hasLazyLoadableAttributes(CtClass classDescriptor) {
 		return true;
 	}
 
 	@Override
 	public boolean isLazyLoadable(CtField field) {
 		return true;
 	}
 
 	@Override
 	public boolean isPersistentField(CtField ctField) {
 		// current check is to look for @Transient
 		return ! ctField.hasAnnotation( Transient.class );
 	}
 
+    @Override
+    public boolean isMappedCollection(CtField field) {
+        try {
+            return (field.getAnnotation(OneToMany.class) != null ||
+                    field.getAnnotation(ManyToMany.class) != null ||
+                    field.getAnnotation(ElementCollection.class) != null);
+        }
+        catch (ClassNotFoundException e) {
+            return false;
+        }
+    }
+
 	@Override
 	public CtField[] order(CtField[] persistentFields) {
 		// for now...
 		return persistentFields;
 		// eventually needs to consult the Hibernate metamodel for proper ordering
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
index db0fabcfcb..beae187cfb 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
@@ -1,563 +1,568 @@
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
 package org.hibernate.tuple.entity;
 
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.spi.ReflectionOptimizer;
 import org.hibernate.cfg.Environment;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.property.Getter;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.property.PropertyAccessorFactory;
 import org.hibernate.property.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.tuple.Instantiator;
 import org.hibernate.tuple.PojoInstantiator;
 import org.hibernate.type.CompositeType;
 
 /**
  * An {@link EntityTuplizer} specific to the pojo entity mode.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public class PojoEntityTuplizer extends AbstractEntityTuplizer {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, PojoEntityTuplizer.class.getName());
 
 	private final Class mappedClass;
 	private final Class proxyInterface;
 	private final boolean lifecycleImplementor;
 	private final Set lazyPropertyNames = new HashSet();
 	private final ReflectionOptimizer optimizer;
 	private final boolean isInstrumented;
 
 	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
 		super( entityMetamodel, mappedEntity );
 		this.mappedClass = mappedEntity.getMappedClass();
 		this.proxyInterface = mappedEntity.getProxyInterface();
 		this.lifecycleImplementor = Lifecycle.class.isAssignableFrom( mappedClass );
 		this.isInstrumented = entityMetamodel.isInstrumented();
 
 		Iterator iter = mappedEntity.getPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property property = (Property) iter.next();
 			if ( property.isLazy() ) {
 				lazyPropertyNames.add( property.getName() );
 			}
 		}
 
 		String[] getterNames = new String[propertySpan];
 		String[] setterNames = new String[propertySpan];
 		Class[] propTypes = new Class[propertySpan];
 		for ( int i = 0; i < propertySpan; i++ ) {
 			getterNames[i] = getters[i].getMethodName();
 			setterNames[i] = setters[i].getMethodName();
 			propTypes[i] = getters[i].getReturnType();
 		}
 
 		if ( hasCustomAccessors || !Environment.useReflectionOptimizer() ) {
 			optimizer = null;
 		}
 		else {
 			// todo : YUCK!!!
 			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer( mappedClass, getterNames, setterNames, propTypes );
 //			optimizer = getFactory().getSettings().getBytecodeProvider().getReflectionOptimizer(
 //					mappedClass, getterNames, setterNames, propTypes
 //			);
 		}
 
 	}
 
 	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappedEntity) {
 		super( entityMetamodel, mappedEntity );
 		this.mappedClass = mappedEntity.getEntity().getClassReference();
 		this.proxyInterface = mappedEntity.getProxyInterfaceType().getValue();
 		this.lifecycleImplementor = Lifecycle.class.isAssignableFrom( mappedClass );
 		this.isInstrumented = entityMetamodel.isInstrumented();
 
 		for ( AttributeBinding property : mappedEntity.getAttributeBindingClosure() ) {
 			if ( property.isLazy() ) {
 				lazyPropertyNames.add( property.getAttribute().getName() );
 			}
 		}
 
 		String[] getterNames = new String[propertySpan];
 		String[] setterNames = new String[propertySpan];
 		Class[] propTypes = new Class[propertySpan];
 		for ( int i = 0; i < propertySpan; i++ ) {
 			getterNames[i] = getters[ i ].getMethodName();
 			setterNames[i] = setters[ i ].getMethodName();
 			propTypes[i] = getters[ i ].getReturnType();
 		}
 
 		if ( hasCustomAccessors || ! Environment.useReflectionOptimizer() ) {
 			optimizer = null;
 		}
 		else {
 			// todo : YUCK!!!
 			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer(
 					mappedClass, getterNames, setterNames, propTypes
 			);
 //			optimizer = getFactory().getSettings().getBytecodeProvider().getReflectionOptimizer(
 //					mappedClass, getterNames, setterNames, propTypes
 //			);
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected ProxyFactory buildProxyFactory(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
 		// determine the id getter and setter methods from the proxy interface (if any)
         // determine all interfaces needed by the resulting proxy
 		
 		/*
 		 * We need to preserve the order of the interfaces they were put into the set, since javassist will choose the
 		 * first one's class-loader to construct the proxy class with. This is also the reason why HibernateProxy.class
 		 * should be the last one in the order (on JBossAS7 its class-loader will be org.hibernate module's class-
 		 * loader, which will not see the classes inside deployed apps.  See HHH-3078
 		 */
 		Set<Class> proxyInterfaces = new java.util.LinkedHashSet<Class>();
 
 		Class mappedClass = persistentClass.getMappedClass();
 		Class proxyInterface = persistentClass.getProxyInterface();
 
 		if ( proxyInterface!=null && !mappedClass.equals( proxyInterface ) ) {
 			if ( !proxyInterface.isInterface() ) {
 				throw new MappingException(
 						"proxy must be either an interface, or the class itself: " + getEntityName()
 				);
 			}
 			proxyInterfaces.add( proxyInterface );
 		}
 
 		if ( mappedClass.isInterface() ) {
 			proxyInterfaces.add( mappedClass );
 		}
 
 		Iterator subclasses = persistentClass.getSubclassIterator();
 		while ( subclasses.hasNext() ) {
 			final Subclass subclass = ( Subclass ) subclasses.next();
 			final Class subclassProxy = subclass.getProxyInterface();
 			final Class subclassClass = subclass.getMappedClass();
 			if ( subclassProxy!=null && !subclassClass.equals( subclassProxy ) ) {
 				if ( !subclassProxy.isInterface() ) {
 					throw new MappingException(
 							"proxy must be either an interface, or the class itself: " + subclass.getEntityName()
 					);
 				}
 				proxyInterfaces.add( subclassProxy );
 			}
 		}
 
 		proxyInterfaces.add( HibernateProxy.class );
 
 		Iterator properties = persistentClass.getPropertyIterator();
 		Class clazz = persistentClass.getMappedClass();
 		while ( properties.hasNext() ) {
 			Property property = (Property) properties.next();
 			Method method = property.getGetter(clazz).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
                 LOG.gettersOfLazyClassesCannotBeFinal(persistentClass.getEntityName(), property.getName());
 			}
 			method = property.getSetter(clazz).getMethod();
             if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
                 LOG.settersOfLazyClassesCannotBeFinal(persistentClass.getEntityName(), property.getName());
 			}
 		}
 
 		Method idGetterMethod = idGetter==null ? null : idGetter.getMethod();
 		Method idSetterMethod = idSetter==null ? null : idSetter.getMethod();
 
 		Method proxyGetIdentifierMethod = idGetterMethod==null || proxyInterface==null ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idGetterMethod);
 		Method proxySetIdentifierMethod = idSetterMethod==null || proxyInterface==null  ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idSetterMethod);
 
 		ProxyFactory pf = buildProxyFactoryInternal( persistentClass, idGetter, idSetter );
 		try {
 			pf.postInstantiate(
 					getEntityName(),
 					mappedClass,
 					proxyInterfaces,
 					proxyGetIdentifierMethod,
 					proxySetIdentifierMethod,
 					persistentClass.hasEmbeddedIdentifier() ?
 			                (CompositeType) persistentClass.getIdentifier().getType() :
 			                null
 			);
 		}
 		catch ( HibernateException he ) {
             LOG.unableToCreateProxyFactory(getEntityName(), he);
 			pf = null;
 		}
 		return pf;
 	}
 
 	protected ProxyFactory buildProxyFactoryInternal(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
 		// TODO : YUCK!!!  fix after HHH-1907 is complete
 		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 //		return getFactory().getSettings().getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected Instantiator buildInstantiator(PersistentClass persistentClass) {
 		if ( optimizer == null ) {
 			return new PojoInstantiator( persistentClass, null );
 		}
 		else {
 			return new PojoInstantiator( persistentClass, optimizer.getInstantiationOptimizer() );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	protected ProxyFactory buildProxyFactory(EntityBinding entityBinding, Getter idGetter, Setter idSetter) {
 		// determine the id getter and setter methods from the proxy interface (if any)
 		// determine all interfaces needed by the resulting proxy
 		HashSet<Class> proxyInterfaces = new HashSet<Class>();
 		proxyInterfaces.add( HibernateProxy.class );
 
 		Class mappedClass = entityBinding.getEntity().getClassReference();
 		Class proxyInterface = entityBinding.getProxyInterfaceType().getValue();
 
 		if ( proxyInterface!=null && !mappedClass.equals( proxyInterface ) ) {
 			if ( ! proxyInterface.isInterface() ) {
 				throw new MappingException(
 						"proxy must be either an interface, or the class itself: " + getEntityName()
 				);
 			}
 			proxyInterfaces.add( proxyInterface );
 		}
 
 		if ( mappedClass.isInterface() ) {
 			proxyInterfaces.add( mappedClass );
 		}
 
 		for ( EntityBinding subEntityBinding : entityBinding.getPostOrderSubEntityBindingClosure() ) {
 			final Class subclassProxy = subEntityBinding.getProxyInterfaceType().getValue();
 			final Class subclassClass = subEntityBinding.getClassReference();
 			if ( subclassProxy!=null && !subclassClass.equals( subclassProxy ) ) {
 				if ( ! subclassProxy.isInterface() ) {
 					throw new MappingException(
 							"proxy must be either an interface, or the class itself: " + subEntityBinding.getEntity().getName()
 					);
 				}
 				proxyInterfaces.add( subclassProxy );
 			}
 		}
 
 		for ( AttributeBinding property : entityBinding.attributeBindings() ) {
 			Method method = getGetter( property ).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
 				LOG.gettersOfLazyClassesCannotBeFinal(entityBinding.getEntity().getName(), property.getAttribute().getName());
 			}
 			method = getSetter( property ).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
 				LOG.settersOfLazyClassesCannotBeFinal(entityBinding.getEntity().getName(), property.getAttribute().getName());
 			}
 		}
 
 		Method idGetterMethod = idGetter==null ? null : idGetter.getMethod();
 		Method idSetterMethod = idSetter==null ? null : idSetter.getMethod();
 
 		Method proxyGetIdentifierMethod = idGetterMethod==null || proxyInterface==null ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idGetterMethod);
 		Method proxySetIdentifierMethod = idSetterMethod==null || proxyInterface==null  ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idSetterMethod);
 
 		ProxyFactory pf = buildProxyFactoryInternal( entityBinding, idGetter, idSetter );
 		try {
 			pf.postInstantiate(
 					getEntityName(),
 					mappedClass,
 					proxyInterfaces,
 					proxyGetIdentifierMethod,
 					proxySetIdentifierMethod,
 					entityBinding.getHierarchyDetails().getEntityIdentifier().isEmbedded()
 							? ( CompositeType ) entityBinding
 									.getHierarchyDetails()
 									.getEntityIdentifier()
 									.getValueBinding()
 									.getHibernateTypeDescriptor()
 									.getResolvedTypeMapping()
 							: null
 			);
 		}
 		catch ( HibernateException he ) {
 			LOG.unableToCreateProxyFactory(getEntityName(), he);
 			pf = null;
 		}
 		return pf;
 	}
 
 	protected ProxyFactory buildProxyFactoryInternal(EntityBinding entityBinding, Getter idGetter, Setter idSetter) {
 		// TODO : YUCK!!!  fix after HHH-1907 is complete
 		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 //		return getFactory().getSettings().getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	protected Instantiator buildInstantiator(EntityBinding entityBinding) {
 		if ( optimizer == null ) {
 			return new PojoInstantiator( entityBinding, null );
 		}
 		else {
 			return new PojoInstantiator( entityBinding, optimizer.getInstantiationOptimizer() );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
 		if ( !getEntityMetamodel().hasLazyProperties() && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			setPropertyValuesWithOptimizer( entity, values );
 		}
 		else {
 			super.setPropertyValues( entity, values );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public Object[] getPropertyValues(Object entity) throws HibernateException {
 		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			return getPropertyValuesWithOptimizer( entity );
 		}
 		else {
 			return super.getPropertyValues( entity );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session) throws HibernateException {
 		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			return getPropertyValuesWithOptimizer( entity );
 		}
 		else {
 			return super.getPropertyValuesToInsert( entity, mergeMap, session );
 		}
 	}
 
 	protected void setPropertyValuesWithOptimizer(Object object, Object[] values) {
 		optimizer.getAccessOptimizer().setPropertyValues( object, values );
 	}
 
 	protected Object[] getPropertyValuesWithOptimizer(Object object) {
 		return optimizer.getAccessOptimizer().getPropertyValues( object );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Class getMappedClass() {
 		return mappedClass;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public boolean isLifecycleImplementor() {
 		return lifecycleImplementor;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return mappedProperty.getGetter( mappedEntity.getMappedClass() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return mappedProperty.getSetter( mappedEntity.getMappedClass() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	protected Getter buildPropertyGetter(AttributeBinding mappedProperty) {
 		return getGetter( mappedProperty );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	protected Setter buildPropertySetter(AttributeBinding mappedProperty) {
 		return getSetter( mappedProperty );
 	}
 
 	private Getter getGetter(AttributeBinding mappedProperty)  throws PropertyNotFoundException, MappingException {
 		return getPropertyAccessor( mappedProperty ).getGetter(
 				mappedProperty.getContainer().getClassReference(),
 				mappedProperty.getAttribute().getName()
 		);
 	}
 
 	private Setter getSetter(AttributeBinding mappedProperty) throws PropertyNotFoundException, MappingException {
 		return getPropertyAccessor( mappedProperty ).getSetter(
 				mappedProperty.getContainer().getClassReference(),
 				mappedProperty.getAttribute().getName()
 		);
 	}
 
 	private PropertyAccessor getPropertyAccessor(AttributeBinding mappedProperty) throws MappingException {
 		// TODO: Fix this then backrefs are working in new metamodel
 		return PropertyAccessorFactory.getPropertyAccessor(
 				mappedProperty.getContainer().getClassReference(),
 				mappedProperty.getPropertyAccessorName()
 		);
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Class getConcreteProxyClass() {
 		return proxyInterface;
 	}
 
     //TODO: need to make the majority of this functionality into a top-level support class for custom impl support
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		if ( isInstrumented() ) {
 			Set lazyProps = lazyPropertiesAreUnfetched && getEntityMetamodel().hasLazyProperties() ?
 					lazyPropertyNames : null;
 			//TODO: if we support multiple fetch groups, we would need
 			//      to clone the set of lazy properties!
 			FieldInterceptionHelper.injectFieldInterceptor( entity, getEntityName(), lazyProps, session );
+
+            //also clear the fields that are marked as dirty in the dirtyness tracker
+            if(entity instanceof org.hibernate.engine.spi.SelfDirtinessTracker) {
+                ((org.hibernate.engine.spi.SelfDirtinessTracker) entity).$$_hibernate_clearDirtyAttributes();
+            }
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public boolean hasUninitializedLazyProperties(Object entity) {
 		if ( getEntityMetamodel().hasLazyProperties() ) {
 			FieldInterceptor callback = FieldInterceptionHelper.extractFieldInterceptor( entity );
 			return callback != null && !callback.isInitialized();
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isInstrumented() {
 		return isInstrumented;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
 		final Class concreteEntityClass = entityInstance.getClass();
 		if ( concreteEntityClass == getMappedClass() ) {
 			return getEntityName();
 		}
 		else {
 			String entityName = getEntityMetamodel().findEntityNameByEntityClass( concreteEntityClass );
 			if ( entityName == null ) {
 				throw new HibernateException(
 						"Unable to resolve entity name from Class [" + concreteEntityClass.getName() + "]"
 								+ " expected instance/subclass of [" + getEntityName() + "]"
 				);
 			}
 			return entityName;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public EntityNameResolver[] getEntityNameResolvers() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/Address.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/Address.java
new file mode 100644
index 0000000000..fd3b425581
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/Address.java
@@ -0,0 +1,88 @@
+/*
+ * Copyright 2012 Red Hat, Inc. and/or its affiliates.
+ *
+ * Licensed under the Eclipse Public License version 1.0, available at
+ * http://www.eclipse.org/legal/epl-v10.html
+ */
+package org.hibernate.test.bytecode.enhancement;
+
+import javax.persistence.Embeddable;
+import javax.persistence.Embedded;
+import java.io.Serializable;
+
+/**
+ * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
+ */
+@Embeddable
+public class Address implements Serializable {
+
+    private String street1;
+    private String street2;
+    private String city;
+    private String state;
+
+    @Embedded
+    private Country country;
+    private String zip;
+    private String phone;
+
+    public Address() {
+    }
+
+    public String getStreet1() {
+        return street1;
+    }
+
+    public void setStreet1(String street1) {
+        this.street1 = street1;
+    }
+
+    public String getStreet2() {
+        return street2;
+    }
+
+    public void setStreet2(String street2) {
+        this.street2 = street2;
+    }
+
+    public String getCity() {
+        return city;
+    }
+
+    public void setCity(String city) {
+        this.city = city;
+    }
+
+    public String getState() {
+        return state;
+    }
+
+    public void setState(String state) {
+        this.state = state;
+    }
+
+    public Country getCountry() {
+        return country;
+    }
+
+    public void setCountry(Country country) {
+        this.country = country;
+    }
+
+    public String getZip() {
+        return zip;
+    }
+
+    public void setZip(String zip) {
+        this.zip = zip;
+    }
+
+    public String getPhone() {
+        return phone;
+    }
+
+    public void setPhone(String phone) {
+        this.phone = phone;
+    }
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/CompositeOwnerTrackerTest.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/CompositeOwnerTrackerTest.java
new file mode 100644
index 0000000000..cca6bea0f4
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/CompositeOwnerTrackerTest.java
@@ -0,0 +1,84 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.bytecode.enhancement;
+
+import org.hibernate.bytecode.enhance.spi.CompositeOwnerTracker;
+import org.hibernate.engine.spi.CompositeOwner;
+import org.junit.Test;
+
+import static org.junit.Assert.assertEquals;
+
+/**
+ * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
+ */
+
+public class CompositeOwnerTrackerTest {
+
+    private int counter = 0;
+
+    @Test
+    public void testCompositeOwnerTracker() {
+
+        CompositeOwnerTracker tracker = new CompositeOwnerTracker();
+        tracker.add("foo", new TestCompositeOwner());
+
+        tracker.callOwner(".street1");
+        assertEquals(1, counter);
+        tracker.add("bar", new TestCompositeOwner());
+        tracker.callOwner(".city");
+        assertEquals(3, counter);
+
+        tracker.removeOwner("foo");
+
+        tracker.callOwner(".country");
+        assertEquals(4, counter);
+        tracker.removeOwner("bar");
+
+        tracker.callOwner(".country");
+
+        tracker.add("moo", new TestCompositeOwner());
+        tracker.callOwner(".country");
+        assertEquals(5, counter);
+    }
+
+    class TestCompositeOwner implements CompositeOwner {
+
+        @Override
+        public void $$_hibernate_trackChange(String attributeName) {
+            if(counter == 0)
+                assertEquals("foo.street1", attributeName);
+            if(counter == 1)
+                assertEquals("foo.city", attributeName);
+            if(counter == 2)
+                assertEquals("bar.city", attributeName);
+            if(counter == 3)
+                assertEquals("bar.country", attributeName);
+            if(counter == 4)
+                assertEquals("moo.country", attributeName);
+            counter++;
+        }
+    }
+}
+
+
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/Country.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/Country.java
new file mode 100644
index 0000000000..d1de3a6c77
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/Country.java
@@ -0,0 +1,26 @@
+/*
+ * Copyright 2012 Red Hat, Inc. and/or its affiliates.
+ *
+ * Licensed under the Eclipse Public License version 1.0, available at
+ * http://www.eclipse.org/legal/epl-v10.html
+ */
+package org.hibernate.test.bytecode.enhancement;
+
+import javax.persistence.Embeddable;
+
+/**
+ * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
+ */
+@Embeddable
+public class Country {
+
+    private String name;
+
+    public String getName() {
+        return name;
+    }
+
+    public void setName(String name) {
+        this.name = name;
+    }
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
index 05014bccb0..15f84e0fdd 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
@@ -1,307 +1,425 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.bytecode.enhancement;
 
 import java.io.ByteArrayInputStream;
 import java.lang.reflect.Method;
+import java.util.ArrayList;
 import java.util.Arrays;
+import java.util.HashSet;
+import java.util.Iterator;
+import java.util.List;
+import java.util.Set;
 
 import javassist.ClassPool;
 import javassist.CtClass;
 import javassist.CtField;
 import javassist.LoaderClassPath;
 
 import org.hibernate.EntityMode;
 import org.hibernate.LockMode;
 import org.hibernate.bytecode.enhance.spi.EnhancementContext;
 import org.hibernate.bytecode.enhance.spi.Enhancer;
 import org.hibernate.bytecode.enhance.spi.EnhancerConstants;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.ManagedEntity;
 import org.hibernate.engine.spi.PersistentAttributeInterceptor;
 import org.hibernate.engine.spi.Status;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
+import javax.persistence.ElementCollection;
+import javax.persistence.ManyToMany;
+import javax.persistence.OneToMany;
+
 import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
+import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
+import static org.junit.Assert.assertTrue;
 
 /**
  * @author Steve Ebersole
  */
 public class EnhancerTest extends BaseUnitTestCase {
 	private static EnhancementContext enhancementContext = new EnhancementContext() {
 		@Override
 		public ClassLoader getLoadingClassLoader() {
 			return getClass().getClassLoader();
 		}
 
 		@Override
 		public boolean isEntityClass(CtClass classDescriptor) {
 			return true;
 		}
 
 		@Override
 		public boolean isCompositeClass(CtClass classDescriptor) {
 			return false;
 		}
 
 		@Override
 		public boolean doDirtyCheckingInline(CtClass classDescriptor) {
 			return true;
 		}
 
 		@Override
 		public boolean hasLazyLoadableAttributes(CtClass classDescriptor) {
 			return true;
 		}
 
 		@Override
 		public boolean isLazyLoadable(CtField field) {
 			return true;
 		}
 
-		@Override
+        @Override
+        public boolean isMappedCollection(CtField field) {
+            try {
+                return (field.getAnnotation(OneToMany.class) != null ||
+                        field.getAnnotation(ManyToMany.class) != null ||
+                        field.getAnnotation(ElementCollection.class) != null);
+            }
+            catch (ClassNotFoundException e) {
+                return false;
+            }
+        }
+
+        @Override
 		public boolean isPersistentField(CtField ctField) {
 			return true;
 		}
 
 		@Override
 		public CtField[] order(CtField[] persistentFields) {
 			return persistentFields;
 		}
 	};
 
 	@Test
 	public void testEnhancement() throws Exception {
 		testFor( SimpleEntity.class );
 		testFor( SubEntity.class );
 	}
 
 	private void testFor(Class entityClassToEnhance) throws Exception {
 		Enhancer enhancer = new Enhancer( enhancementContext );
 		CtClass entityCtClass = generateCtClassForAnEntity( entityClassToEnhance );
 		byte[] original = entityCtClass.toBytecode();
-		byte[] enhanced = enhancer.enhance( entityCtClass.getName(), original );
+		//byte[] enhanced = enhancer.enhance( entityCtClass.getName(), original );
+        byte[] enhanced = enhancer.enhance( entityCtClass.getName(), original );
 		assertFalse( "entity was not enhanced", Arrays.equals( original, enhanced ) );
 
 		ClassLoader cl = new ClassLoader() { };
 		ClassPool cp = new ClassPool( false );
 		cp.appendClassPath( new LoaderClassPath( cl ) );
 		CtClass enhancedCtClass = cp.makeClass( new ByteArrayInputStream( enhanced ) );
+        enhancedCtClass.debugWriteFile("/tmp");
+        //just for debugging
+        Class addressClass = null;
+        Class countryClass = null;
+        if(entityClassToEnhance.getName().endsWith("SimpleEntity")) {
+            CtClass addressCtClass = generateCtClassForAnEntity( Address.class );
+            byte[] enhancedAddress = enhancer.enhanceComposite(Address.class.getName(), addressCtClass.toBytecode());
+            CtClass enhancedCtClassAddress = cp.makeClass( new ByteArrayInputStream( enhancedAddress ) );
+            enhancedCtClassAddress.debugWriteFile("/tmp");
+            addressClass = enhancedCtClassAddress.toClass( cl, this.getClass().getProtectionDomain() );
+
+            CtClass countryCtClass = generateCtClassForAnEntity( Country.class );
+            byte[] enhancedCountry = enhancer.enhanceComposite(Country.class.getName(), countryCtClass.toBytecode());
+            CtClass enhancedCtClassCountry = cp.makeClass( new ByteArrayInputStream( enhancedCountry ) );
+            enhancedCtClassCountry.debugWriteFile("/tmp");
+            countryClass = enhancedCtClassCountry.toClass( cl, this.getClass().getProtectionDomain() );
+
+        }
 		Class entityClass = enhancedCtClass.toClass( cl, this.getClass().getProtectionDomain() );
 		Object entityInstance = entityClass.newInstance();
 
 		assertTyping( ManagedEntity.class, entityInstance );
 
 		// call the new methods
 		//
 		Method setter = entityClass.getMethod( EnhancerConstants.ENTITY_ENTRY_SETTER_NAME, EntityEntry.class );
 		Method getter = entityClass.getMethod( EnhancerConstants.ENTITY_ENTRY_GETTER_NAME );
 		assertNull( getter.invoke( entityInstance ) );
 		setter.invoke( entityInstance, makeEntityEntry() );
 		assertNotNull( getter.invoke( entityInstance ) );
 		setter.invoke( entityInstance, new Object[] {null} );
 		assertNull( getter.invoke( entityInstance ) );
 
 		Method entityInstanceGetter = entityClass.getMethod( EnhancerConstants.ENTITY_INSTANCE_GETTER_NAME );
 		assertSame( entityInstance, entityInstanceGetter.invoke( entityInstance ) );
 
 		Method previousGetter = entityClass.getMethod( EnhancerConstants.PREVIOUS_GETTER_NAME );
 		Method previousSetter = entityClass.getMethod( EnhancerConstants.PREVIOUS_SETTER_NAME, ManagedEntity.class );
 		previousSetter.invoke( entityInstance, entityInstance );
 		assertSame( entityInstance, previousGetter.invoke( entityInstance ) );
 
 		Method nextGetter = entityClass.getMethod( EnhancerConstants.PREVIOUS_GETTER_NAME );
 		Method nextSetter = entityClass.getMethod( EnhancerConstants.PREVIOUS_SETTER_NAME, ManagedEntity.class );
 		nextSetter.invoke( entityInstance, entityInstance );
 		assertSame( entityInstance, nextGetter.invoke( entityInstance ) );
 
 		// add an attribute interceptor...
 		Method interceptorGetter = entityClass.getMethod( EnhancerConstants.INTERCEPTOR_GETTER_NAME );
 		Method interceptorSetter = entityClass.getMethod( EnhancerConstants.INTERCEPTOR_SETTER_NAME, PersistentAttributeInterceptor.class );
 
 		assertNull( interceptorGetter.invoke( entityInstance ) );
 		entityClass.getMethod( "getId" ).invoke( entityInstance );
 
 		interceptorSetter.invoke( entityInstance, new LocalPersistentAttributeInterceptor() );
-		assertNotNull( interceptorGetter.invoke( entityInstance ) );
+		assertNotNull(interceptorGetter.invoke(entityInstance));
 
 		// dirty checking is unfortunately just printlns for now... just verify the test output
 		entityClass.getMethod( "getId" ).invoke( entityInstance );
 		entityClass.getMethod( "setId", Long.class ).invoke( entityInstance, entityClass.getMethod( "getId" ).invoke( entityInstance ) );
 		entityClass.getMethod( "setId", Long.class ).invoke( entityInstance, 1L );
+        assertTrue((Boolean) entityClass.getMethod("$$_hibernate_hasDirtyAttributes").invoke(entityInstance));
 
 		entityClass.getMethod( "isActive" ).invoke( entityInstance );
 		entityClass.getMethod( "setActive", boolean.class ).invoke( entityInstance, entityClass.getMethod( "isActive" ).invoke( entityInstance ) );
-		entityClass.getMethod( "setActive", boolean.class ).invoke( entityInstance, true );
+		entityClass.getMethod( "setActive", boolean.class ).invoke(entityInstance, true);
 
 		entityClass.getMethod( "getSomeNumber" ).invoke( entityInstance );
 		entityClass.getMethod( "setSomeNumber", long.class ).invoke( entityInstance, entityClass.getMethod( "getSomeNumber" ).invoke( entityInstance ) );
-		entityClass.getMethod( "setSomeNumber", long.class ).invoke( entityInstance, 1L );
+		entityClass.getMethod( "setSomeNumber", long.class ).invoke(entityInstance, 1L);
+        assertEquals(3, ((Set) entityClass.getMethod("$$_hibernate_getDirtyAttributes").invoke(entityInstance)).size());
+
+        entityClass.getMethod("$$_hibernate_clearDirtyAttributes").invoke(entityInstance);
+        //setting the same value should not make it dirty
+        entityClass.getMethod( "setSomeNumber", long.class ).invoke(entityInstance, 1L);
+        //assertEquals(0, ((Set) entityClass.getMethod("$$_hibernate_getDirtyAttributes").invoke(entityInstance)).size());
+        //assertFalse((Boolean) entityClass.getMethod("$$_hibernate_hasDirtyAttributes").invoke(entityInstance));
+
+        if(entityClass.getName().endsWith("SimpleEntity")) {
+
+            List<String> strings = new ArrayList<String>();
+            strings.add("FooBar");
+
+            entityClass.getMethod( "setSomeStrings", java.util.List.class ).invoke(entityInstance, strings);
+
+            assertTrue((Boolean) entityClass.getMethod("$$_hibernate_hasDirtyAttributes").invoke(entityInstance));
+
+            Set tracked = (Set) entityClass.getMethod(EnhancerConstants.TRACKER_GET_NAME).invoke(entityInstance);
+            assertEquals(1, tracked.size());
+            assertEquals("someStrings", tracked.iterator().next());
+
+            entityClass.getMethod("$$_hibernate_clearDirtyAttributes").invoke(entityInstance);
+
+            ((List) entityClass.getMethod( "getSomeStrings").invoke(entityInstance)).add("JADA!");
+            Boolean isDirty = (Boolean) entityClass.getMethod("$$_hibernate_hasDirtyAttributes").invoke(entityInstance);
+            assertTrue(isDirty);
+            assertEquals("someStrings",
+                    ((Set) entityClass.getMethod(EnhancerConstants.TRACKER_GET_NAME).invoke(entityInstance)).iterator().next());
+            entityClass.getMethod("$$_hibernate_clearDirtyAttributes").invoke(entityInstance);
+
+            //this should not set the entity to dirty
+            Set<Integer> ints = new HashSet<Integer>();
+            ints.add(42);
+            entityClass.getMethod( "setSomeInts", java.util.Set.class ).invoke(entityInstance, ints);
+            isDirty = (Boolean) entityClass.getMethod("$$_hibernate_hasDirtyAttributes").invoke(entityInstance);
+            assertFalse(isDirty);
+
+            //testing composite object
+            assert addressClass != null;
+            Object address =  addressClass.newInstance();
+
+            assert countryClass != null;
+            Object country =  countryClass.newInstance();
+
+            //Method adrInterceptorGetter = addressClass.getMethod( EnhancerConstants.INTERCEPTOR_GETTER_NAME );
+            //Method adrInterceptorSetter = addressClass.getMethod( EnhancerConstants.INTERCEPTOR_SETTER_NAME, PersistentAttributeInterceptor.class );
+            //adrInterceptorSetter.invoke( address, new LocalPersistentAttributeInterceptor() );
+
+            entityClass.getMethod("setAddress", addressClass).invoke(entityInstance, address);
+
+            addressClass.getMethod("setCity", String.class).invoke(address, "Arendal");
+
+            tracked = (Set) entityClass.getMethod(EnhancerConstants.TRACKER_GET_NAME).invoke(entityInstance);
+            assertEquals(2, tracked.size());
+            Iterator iter = tracked.iterator();
+            assertEquals("address", iter.next());
+            assertEquals("address.city", iter.next());
+
+            entityClass.getMethod("$$_hibernate_clearDirtyAttributes").invoke(entityInstance);
+
+            //make sure that new composite instances are cleared
+            Object address2 =  addressClass.newInstance();
+
+            entityClass.getMethod("setAddress", addressClass).invoke(entityInstance, address2);
+            addressClass.getMethod("setStreet1", String.class).invoke(address, "Heggedalveien");
+
+            tracked = (Set) entityClass.getMethod(EnhancerConstants.TRACKER_GET_NAME).invoke(entityInstance);
+            assertEquals(1, tracked.size());
+
+            addressClass.getMethod("setCountry", countryClass).invoke(address2, country);
+            countryClass.getMethod("setName", String.class).invoke(country, "Norway");
+
+            tracked = (Set) entityClass.getMethod(EnhancerConstants.TRACKER_GET_NAME).invoke(entityInstance);
+            assertEquals(3, tracked.size());
+        }
 	}
 
 	private CtClass generateCtClassForAnEntity(Class entityClassToEnhance) throws Exception {
 		ClassPool cp = new ClassPool( false );
 		return cp.makeClass(
 				getClass().getClassLoader().getResourceAsStream(
 						entityClassToEnhance.getName().replace( '.', '/' ) + ".class"
 				)
 		);
 	}
 
 	private EntityEntry makeEntityEntry() {
 		return new EntityEntry(
 				Status.MANAGED,
 				null,
 				null,
 				new Long(1),
 				null,
 				LockMode.NONE,
 				false,
 				null,
 				EntityMode.POJO,
 				null,
 				false,
 				false,
 				null
 		);
 	}
 
 
 	private class LocalPersistentAttributeInterceptor implements PersistentAttributeInterceptor {
 		@Override
 		public boolean readBoolean(Object obj, String name, boolean oldValue) {
 			System.out.println( "Reading boolean [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public boolean writeBoolean(Object obj, String name, boolean oldValue, boolean newValue) {
 			System.out.println( "Writing boolean [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public byte readByte(Object obj, String name, byte oldValue) {
 			System.out.println( "Reading byte [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public byte writeByte(Object obj, String name, byte oldValue, byte newValue) {
 			System.out.println( "Writing byte [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public char readChar(Object obj, String name, char oldValue) {
 			System.out.println( "Reading char [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public char writeChar(Object obj, String name, char oldValue, char newValue) {
 			System.out.println( "Writing char [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public short readShort(Object obj, String name, short oldValue) {
 			System.out.println( "Reading short [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public short writeShort(Object obj, String name, short oldValue, short newValue) {
 			System.out.println( "Writing short [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public int readInt(Object obj, String name, int oldValue) {
 			System.out.println( "Reading int [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public int writeInt(Object obj, String name, int oldValue, int newValue) {
 			System.out.println( "Writing int [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public float readFloat(Object obj, String name, float oldValue) {
 			System.out.println( "Reading float [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public float writeFloat(Object obj, String name, float oldValue, float newValue) {
 			System.out.println( "Writing float [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public double readDouble(Object obj, String name, double oldValue) {
 			System.out.println( "Reading double [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public double writeDouble(Object obj, String name, double oldValue, double newValue) {
 			System.out.println( "Writing double [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public long readLong(Object obj, String name, long oldValue) {
 			System.out.println( "Reading long [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public long writeLong(Object obj, String name, long oldValue, long newValue) {
 			System.out.println( "Writing long [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public Object readObject(Object obj, String name, Object oldValue) {
 			System.out.println( "Reading Object [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public Object writeObject(Object obj, String name, Object oldValue, Object newValue) {
 			System.out.println( "Writing Object [" + name + "]" );
 			return newValue;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/SimpleEntity.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/SimpleEntity.java
index 158d3a4fec..f9115be5f8 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/SimpleEntity.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/SimpleEntity.java
@@ -1,71 +1,106 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.bytecode.enhancement;
 
+import javax.persistence.Embedded;
 import javax.persistence.Entity;
 import javax.persistence.Id;
+import javax.persistence.OneToMany;
+import java.util.List;
+import java.util.Set;
 
 /**
  * @author Steve Ebersole
  */
 @Entity
 public class SimpleEntity {
 	private Long id;
 	private String name;
 	private boolean active;
 	private long someNumber;
+    private List<String> someStrings;
+
+    @OneToMany
+    private Set<Integer> someInts;
+
+    @Embedded
+    private Address address;
 
 	@Id
 	public Long getId() {
 		return id;
 	}
 
 	public void setId(Long id) {
 		this.id = id;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	public boolean isActive() {
 		return active;
 	}
 
 	public void setActive(boolean active) {
 		this.active = active;
 	}
 
 	public long getSomeNumber() {
 		return someNumber;
 	}
 
 	public void setSomeNumber(long someNumber) {
 		this.someNumber = someNumber;
 	}
+
+    public List<String> getSomeStrings() {
+        return someStrings;
+    }
+
+    public void setSomeStrings(List<String> someStrings) {
+        this.someStrings = someStrings;
+    }
+
+    public Address getAddress() {
+        return address;
+    }
+
+    public void setAddress(Address address) {
+        this.address = address;
+    }
+
+    public Set<Integer> getSomeInts() {
+        return someInts;
+    }
+
+    public void setSomeInts(Set<Integer> someInts) {
+        this.someInts = someInts;
+    }
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/Address.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/Address.java
new file mode 100644
index 0000000000..2605958ff4
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/Address.java
@@ -0,0 +1,122 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+ */package org.hibernate.test.bytecode.enhancement.customer;
+
+import java.io.Serializable;
+
+/**
+ * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
+ */
+public class Address implements Serializable {
+    private String street1;
+    private String street2;
+    private String city;
+    private String state;
+    private String country;
+    private String zip;
+    private String phone;
+
+    public Address() {
+    }
+    public Address(String street1, String street2, String city, String state,
+                   String country, String zip, String phone) {
+        this.street1 = street1;
+        this.street2 = street2;
+        this.city    = city;
+        this.state   = state;
+        this.country = country;
+        setZip(zip);
+        setPhone(phone);
+    }
+
+    public String toString() {
+        return street1 + "\n" + street2 + "\n" + city + "," + state + " " + zip + "\n" + phone;
+    }
+
+    public String getStreet1() {
+        return street1;
+    }
+
+    public void setStreet1(String street1) {
+        this.street1 = street1;
+    }
+
+    public String getStreet2() {
+        return street2;
+    }
+
+    public void setStreet2(String street2) {
+        this.street2 = street2;
+    }
+
+    public String getCity() {
+        return city;
+    }
+
+    public void setCity(String city) {
+        this.city = city;
+    }
+
+    public String getState() {
+        return state;
+    }
+
+    public void setState(String state) {
+        this.state = state;
+    }
+
+    public String getCountry() {
+        return country;
+    }
+
+    public void setCountry(String country) {
+        this.country = country;
+    }
+
+    public String getZip() {
+        return zip;
+    }
+
+    public void setZip(String zip) {
+        assertNumeric(zip, "Non-numeric zip ");
+        this.zip = zip;
+
+    }
+
+    public String getPhone() {
+        return phone;
+    }
+
+    public void setPhone(String phone) {
+        assertNumeric(zip, "Non-numeric phone ");
+        this.phone = phone;
+    }
+
+    void assertNumeric(String s, String error) {
+        for (int i=0; i<s.length(); i++) {
+            if (!Character.isDigit(s.charAt(i))) {
+                throw new IllegalArgumentException(error + s);
+            }
+        }
+    }
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/Customer.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/Customer.java
new file mode 100644
index 0000000000..b5fe1c557d
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/Customer.java
@@ -0,0 +1,249 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.bytecode.enhancement.customer;
+
+import javax.persistence.AttributeOverride;
+import javax.persistence.AttributeOverrides;
+import javax.persistence.CascadeType;
+import javax.persistence.Column;
+import javax.persistence.Embedded;
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.Id;
+import javax.persistence.OneToMany;
+import javax.persistence.Table;
+import javax.persistence.Temporal;
+import javax.persistence.TemporalType;
+import javax.persistence.Version;
+import java.math.BigDecimal;
+import java.util.ArrayList;
+import java.util.Calendar;
+import java.util.List;
+
+/**
+ * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
+ */
+@Entity
+@Table(name="O_CUSTOMER")
+public class Customer {
+    public static final String QUERY_ALL = "Customer.selectAll";
+    public static final String QUERY_COUNT = "Customer.count";
+    public static final String QUERY_BY_CREDIT = "Customer.selectByCreditLimit";
+
+    public static final String BAD_CREDIT = "BC";
+
+    @Id
+    @Column(name="C_ID")
+    private int id;
+
+    @Column(name="C_FIRST")
+    private String firstName;
+
+    @Column(name="C_LAST")
+    private String lastName;
+
+    @Column(name="C_CONTACT")
+    private String contact;
+
+    @Column(name="C_CREDIT")
+    private String credit;
+
+    @Column(name="C_CREDIT_LIMIT")
+    private BigDecimal creditLimit;
+
+    @Column(name="C_SINCE")
+    @Temporal(TemporalType.DATE)
+    private Calendar since;
+
+    @Column(name="C_BALANCE")
+    private BigDecimal balance;
+
+    @Column(name="C_YTD_PAYMENT")
+    private BigDecimal ytdPayment;
+
+    @OneToMany(mappedBy="customer", cascade= CascadeType.ALL, fetch= FetchType.EAGER)
+    private List<CustomerInventory> customerInventories;
+
+    @Embedded
+    @AttributeOverrides(
+            {@AttributeOverride(name="street1",column=@Column(name="C_STREET1")),
+                    @AttributeOverride(name="street2",column=@Column(name="C_STREET2")),
+                    @AttributeOverride(name="city",   column=@Column(name="C_CITY")),
+                    @AttributeOverride(name="state",  column=@Column(name="C_STATE")),
+                    @AttributeOverride(name="country",column=@Column(name="C_COUNTRY")),
+                    @AttributeOverride(name="zip",    column=@Column(name="C_ZIP")),
+                    @AttributeOverride(name="phone",  column=@Column(name="C_PHONE"))})
+    private Address       address;
+
+    @Version
+    @Column(name = "C_VERSION")
+    private int version;
+
+    public Customer() {
+    }
+
+    public Customer(String first, String last, Address address,
+                    String contact, String credit, BigDecimal creditLimit,
+                    BigDecimal balance, BigDecimal YtdPayment) {
+
+        this.firstName   = first;
+        this.lastName    = last;
+        this.address     = address;
+        this.contact     = contact;
+        this.since       = Calendar.getInstance();
+        this.credit      = credit;
+        this.creditLimit = creditLimit;
+        this.balance     = balance;
+        this.ytdPayment  = YtdPayment;
+    }
+
+    public Integer getId() {
+        return id;
+    }
+
+    public void setId(Integer customerId) {
+        this.id = customerId;
+    }
+
+    public String getFirstName() {
+        return firstName;
+    }
+
+    public void setFirstName(String firstName) {
+        this.firstName = firstName;
+    }
+
+    public String getLastName() {
+        return lastName;
+    }
+
+    public void setLastName(String lastName) {
+        this.lastName = lastName;
+    }
+
+    public Address getAddress() {
+        return address;
+    }
+
+    public void setAddress(Address address) {
+        this.address = address;
+    }
+
+    public String getContact() {
+        return contact;
+    }
+
+    public void setContact(String contact) {
+        this.contact = contact;
+    }
+
+    public String getCredit() {
+        return credit;
+    }
+
+    public void setCredit(String credit) {
+        this.credit = credit;
+    }
+
+    public BigDecimal getCreditLimit() {
+        return creditLimit;
+    }
+
+    public void setCreditLimit(BigDecimal creditLimit) {
+        this.creditLimit = creditLimit;
+    }
+
+    public Calendar getSince() {
+        return since;
+    }
+
+    public void setSince(Calendar since) {
+        this.since = since;
+    }
+
+    public BigDecimal getBalance() {
+        return balance;
+    }
+
+    public void setBalance(BigDecimal balance) {
+        this.balance = balance;
+    }
+
+    public void changeBalance(BigDecimal change) {
+        setBalance(balance.add(change).setScale(2, BigDecimal.ROUND_DOWN));
+    }
+
+    public BigDecimal getYtdPayment() {
+        return ytdPayment;
+    }
+
+    public void setYtdPayment(BigDecimal ytdPayment) {
+        this.ytdPayment = ytdPayment;
+    }
+
+    public List<CustomerInventory> getInventories() {
+        if (customerInventories == null){
+            customerInventories = new ArrayList<CustomerInventory>();
+        }
+        return customerInventories;
+    }
+
+    public CustomerInventory addInventory(String item, int quantity,
+                                          BigDecimal totalValue) {
+
+        CustomerInventory inventory = new CustomerInventory(this, item,
+                quantity, totalValue);
+        getInventories().add(inventory);
+        return inventory;
+    }
+
+    public int getVersion() {
+        return version;
+    }
+
+    public boolean hasSufficientCredit(BigDecimal amount) {
+        return !BAD_CREDIT.equals(getCredit())
+                && creditLimit != null
+                && creditLimit.compareTo(amount) >= 0;
+    }
+
+    @Override
+    public boolean equals(Object o) {
+        if (this == o)
+            return true;
+        if (o == null || getClass() != o.getClass())
+            return false;
+        return id == ((Customer) o).id;
+    }
+
+    @Override
+    public int hashCode() {
+        return new Integer(id).hashCode();
+    }
+
+    @Override
+    public String toString() {
+        return this.getFirstName() + " " + this.getLastName();
+    }
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/CustomerEnhancerTest.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/CustomerEnhancerTest.java
new file mode 100644
index 0000000000..122bd5eeea
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/CustomerEnhancerTest.java
@@ -0,0 +1,384 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.bytecode.enhancement.customer;
+
+import javassist.ClassPool;
+import javassist.CtClass;
+import javassist.CtField;
+import javassist.LoaderClassPath;
+import org.hibernate.EntityMode;
+import org.hibernate.LockMode;
+import org.hibernate.bytecode.enhance.spi.EnhancementContext;
+import org.hibernate.bytecode.enhance.spi.Enhancer;
+import org.hibernate.bytecode.enhance.spi.EnhancerConstants;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.ManagedEntity;
+import org.hibernate.engine.spi.PersistentAttributeInterceptor;
+import org.hibernate.engine.spi.Status;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+import org.junit.Test;
+
+import javax.persistence.ElementCollection;
+import javax.persistence.ManyToMany;
+import javax.persistence.OneToMany;
+import java.io.ByteArrayInputStream;
+import java.lang.reflect.Method;
+import java.util.ArrayList;
+import java.util.Arrays;
+import java.util.HashSet;
+import java.util.Iterator;
+import java.util.List;
+import java.util.Set;
+
+import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertNotNull;
+import static org.junit.Assert.assertNull;
+import static org.junit.Assert.assertSame;
+import static org.junit.Assert.assertTrue;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CustomerEnhancerTest extends BaseUnitTestCase {
+	private static EnhancementContext enhancementContext = new EnhancementContext() {
+		@Override
+		public ClassLoader getLoadingClassLoader() {
+			return getClass().getClassLoader();
+		}
+
+		@Override
+		public boolean isEntityClass(CtClass classDescriptor) {
+			return true;
+		}
+
+		@Override
+		public boolean isCompositeClass(CtClass classDescriptor) {
+			return false;
+		}
+
+		@Override
+		public boolean doDirtyCheckingInline(CtClass classDescriptor) {
+			return true;
+		}
+
+		@Override
+		public boolean hasLazyLoadableAttributes(CtClass classDescriptor) {
+			return true;
+		}
+
+		@Override
+		public boolean isLazyLoadable(CtField field) {
+			return true;
+		}
+
+        @Override
+        public boolean isMappedCollection(CtField field) {
+            try {
+                return (field.getAnnotation(OneToMany.class) != null ||
+                        field.getAnnotation(ManyToMany.class) != null ||
+                        field.getAnnotation(ElementCollection.class) != null);
+            }
+            catch (ClassNotFoundException e) {
+                return false;
+            }
+        }
+
+        @Override
+		public boolean isPersistentField(CtField ctField) {
+			return true;
+		}
+
+		@Override
+		public CtField[] order(CtField[] persistentFields) {
+			return persistentFields;
+		}
+	};
+
+	@Test
+	public void testEnhancement() throws Exception {
+		testFor( Customer.class );
+	}
+
+	private void testFor(Class entityClassToEnhance) throws Exception {
+		Enhancer enhancer = new Enhancer( enhancementContext );
+		CtClass entityCtClass = generateCtClassForAnEntity( entityClassToEnhance );
+		byte[] original = entityCtClass.toBytecode();
+		//byte[] enhanced = enhancer.enhance( entityCtClass.getName(), original );
+        byte[] enhanced = enhancer.enhance( entityCtClass.getName(), original );
+		assertFalse( "entity was not enhanced", Arrays.equals( original, enhanced ) );
+
+		ClassLoader cl = new ClassLoader() { };
+		ClassPool cp = new ClassPool( false );
+		cp.appendClassPath( new LoaderClassPath( cl ) );
+		CtClass enhancedCtClass = cp.makeClass( new ByteArrayInputStream( enhanced ) );
+        enhancedCtClass.debugWriteFile("/tmp");
+        //just for debugging
+        Class addressClass = null;
+        Class customerInventoryClass = null;
+        Class supplierComponentPKClass = null;
+
+        CtClass addressCtClass = generateCtClassForAnEntity( org.hibernate.test.bytecode.enhancement.customer.Address.class );
+        byte[] enhancedAddress = enhancer.enhanceComposite(Address.class.getName(), addressCtClass.toBytecode());
+        CtClass enhancedCtClassAddress = cp.makeClass( new ByteArrayInputStream( enhancedAddress ) );
+        enhancedCtClassAddress.debugWriteFile("/tmp");
+        addressClass = enhancedCtClassAddress.toClass( cl, this.getClass().getProtectionDomain() );
+
+        CtClass customerInventoryCtClass = generateCtClassForAnEntity( CustomerInventory.class );
+        byte[] enhancedCustomerInventory = enhancer.enhance(CustomerInventory.class.getName(), customerInventoryCtClass.toBytecode());
+        CtClass enhancedCtClassCustomerInventory = cp.makeClass( new ByteArrayInputStream( enhancedCustomerInventory ) );
+        enhancedCtClassCustomerInventory.debugWriteFile("/tmp");
+        customerInventoryClass = enhancedCtClassCustomerInventory.toClass( cl, this.getClass().getProtectionDomain() );
+
+
+        CtClass supplierComponentPKCtClass = generateCtClassForAnEntity( SupplierComponentPK.class );
+        byte[] enhancedSupplierComponentPK = enhancer.enhanceComposite(SupplierComponentPK.class.getName(), supplierComponentPKCtClass.toBytecode());
+        CtClass enhancedCtClassSupplierComponentPK = cp.makeClass( new ByteArrayInputStream( enhancedSupplierComponentPK ) );
+        enhancedCtClassSupplierComponentPK.debugWriteFile("/tmp");
+        supplierComponentPKClass = enhancedCtClassSupplierComponentPK.toClass( cl, this.getClass().getProtectionDomain() );
+
+		Class entityClass = enhancedCtClass.toClass( cl, this.getClass().getProtectionDomain() );
+		Object entityInstance = entityClass.newInstance();
+
+		assertTyping( ManagedEntity.class, entityInstance );
+
+		// call the new methods
+		//
+		Method setter = entityClass.getMethod( EnhancerConstants.ENTITY_ENTRY_SETTER_NAME, EntityEntry.class );
+		Method getter = entityClass.getMethod( EnhancerConstants.ENTITY_ENTRY_GETTER_NAME );
+		assertNull( getter.invoke( entityInstance ) );
+		setter.invoke( entityInstance, makeEntityEntry() );
+		assertNotNull( getter.invoke( entityInstance ) );
+		setter.invoke( entityInstance, new Object[] {null} );
+		assertNull( getter.invoke( entityInstance ) );
+
+		Method entityInstanceGetter = entityClass.getMethod( EnhancerConstants.ENTITY_INSTANCE_GETTER_NAME );
+		assertSame( entityInstance, entityInstanceGetter.invoke( entityInstance ) );
+
+		Method previousGetter = entityClass.getMethod( EnhancerConstants.PREVIOUS_GETTER_NAME );
+		Method previousSetter = entityClass.getMethod( EnhancerConstants.PREVIOUS_SETTER_NAME, ManagedEntity.class );
+		previousSetter.invoke( entityInstance, entityInstance );
+		assertSame( entityInstance, previousGetter.invoke( entityInstance ) );
+
+		Method nextGetter = entityClass.getMethod( EnhancerConstants.PREVIOUS_GETTER_NAME );
+		Method nextSetter = entityClass.getMethod( EnhancerConstants.PREVIOUS_SETTER_NAME, ManagedEntity.class );
+		nextSetter.invoke( entityInstance, entityInstance );
+		assertSame( entityInstance, nextGetter.invoke( entityInstance ) );
+
+		// add an attribute interceptor...
+		Method interceptorGetter = entityClass.getMethod( EnhancerConstants.INTERCEPTOR_GETTER_NAME );
+		Method interceptorSetter = entityClass.getMethod( EnhancerConstants.INTERCEPTOR_SETTER_NAME, PersistentAttributeInterceptor.class );
+
+		assertNull( interceptorGetter.invoke( entityInstance ) );
+		entityClass.getMethod( "getId" ).invoke( entityInstance );
+
+		interceptorSetter.invoke( entityInstance, new LocalPersistentAttributeInterceptor() );
+		assertNotNull(interceptorGetter.invoke(entityInstance));
+
+		// dirty checking is unfortunately just printlns for now... just verify the test output
+		entityClass.getMethod( "getId" ).invoke( entityInstance );
+		entityClass.getMethod( "setId", Integer.class ).invoke( entityInstance, entityClass.getMethod( "getId" ).invoke( entityInstance ) );
+		entityClass.getMethod( "setId", Integer.class ).invoke( entityInstance, 1 );
+        assertTrue((Boolean) entityClass.getMethod("$$_hibernate_hasDirtyAttributes").invoke(entityInstance));
+
+		entityClass.getMethod( "setFirstName", String.class ).invoke(entityInstance, "Erik");
+
+		entityClass.getMethod( "setLastName", String.class ).invoke(entityInstance, "Mykland");
+        assertEquals(3, ((Set) entityClass.getMethod("$$_hibernate_getDirtyAttributes").invoke(entityInstance)).size());
+
+        entityClass.getMethod("$$_hibernate_clearDirtyAttributes").invoke(entityInstance);
+
+        //testing composite object
+        assert addressClass != null;
+        Object address =  addressClass.newInstance();
+
+        assert customerInventoryClass != null;
+        Object customerInventory =  customerInventoryClass.newInstance();
+
+        //Method adrInterceptorGetter = addressClass.getMethod( EnhancerConstants.INTERCEPTOR_GETTER_NAME );
+        //Method adrInterceptorSetter = addressClass.getMethod( EnhancerConstants.INTERCEPTOR_SETTER_NAME, PersistentAttributeInterceptor.class );
+        //adrInterceptorSetter.invoke( address, new LocalPersistentAttributeInterceptor() );
+
+        entityClass.getMethod("setAddress", addressClass).invoke(entityInstance, address);
+
+        addressClass.getMethod("setCity", String.class).invoke(address, "Arendal");
+
+        Set tracked = (Set) entityClass.getMethod(EnhancerConstants.TRACKER_GET_NAME).invoke(entityInstance);
+        assertEquals(2, tracked.size());
+        Iterator iter = tracked.iterator();
+        assertEquals("address", iter.next());
+        assertEquals("address.city", iter.next());
+
+        entityClass.getMethod("$$_hibernate_clearDirtyAttributes").invoke(entityInstance);
+
+        //make sure that new composite instances are cleared
+        Object address2 =  addressClass.newInstance();
+
+        entityClass.getMethod("setAddress", addressClass).invoke(entityInstance, address2);
+        addressClass.getMethod("setStreet1", String.class).invoke(address, "Heggedalveien");
+
+        tracked = (Set) entityClass.getMethod(EnhancerConstants.TRACKER_GET_NAME).invoke(entityInstance);
+        assertEquals(1, tracked.size());
+
+    }
+
+	private CtClass generateCtClassForAnEntity(Class entityClassToEnhance) throws Exception {
+		ClassPool cp = new ClassPool( false );
+		return cp.makeClass(
+				getClass().getClassLoader().getResourceAsStream(
+						entityClassToEnhance.getName().replace( '.', '/' ) + ".class"
+				)
+		);
+	}
+
+	private EntityEntry makeEntityEntry() {
+		return new EntityEntry(
+				Status.MANAGED,
+				null,
+				null,
+				new Long(1),
+				null,
+				LockMode.NONE,
+				false,
+				null,
+				EntityMode.POJO,
+				null,
+				false,
+				false,
+				null
+		);
+	}
+
+
+	private class LocalPersistentAttributeInterceptor implements PersistentAttributeInterceptor {
+		@Override
+		public boolean readBoolean(Object obj, String name, boolean oldValue) {
+			System.out.println( "Reading boolean [" + name + "]" );
+			return oldValue;
+		}
+
+		@Override
+		public boolean writeBoolean(Object obj, String name, boolean oldValue, boolean newValue) {
+			System.out.println( "Writing boolean [" + name + "]" );
+			return newValue;
+		}
+
+		@Override
+		public byte readByte(Object obj, String name, byte oldValue) {
+			System.out.println( "Reading byte [" + name + "]" );
+			return oldValue;
+		}
+
+		@Override
+		public byte writeByte(Object obj, String name, byte oldValue, byte newValue) {
+			System.out.println( "Writing byte [" + name + "]" );
+			return newValue;
+		}
+
+		@Override
+		public char readChar(Object obj, String name, char oldValue) {
+			System.out.println( "Reading char [" + name + "]" );
+			return oldValue;
+		}
+
+		@Override
+		public char writeChar(Object obj, String name, char oldValue, char newValue) {
+			System.out.println( "Writing char [" + name + "]" );
+			return newValue;
+		}
+
+		@Override
+		public short readShort(Object obj, String name, short oldValue) {
+			System.out.println( "Reading short [" + name + "]" );
+			return oldValue;
+		}
+
+		@Override
+		public short writeShort(Object obj, String name, short oldValue, short newValue) {
+			System.out.println( "Writing short [" + name + "]" );
+			return newValue;
+		}
+
+		@Override
+		public int readInt(Object obj, String name, int oldValue) {
+			System.out.println( "Reading int [" + name + "]" );
+			return oldValue;
+		}
+
+		@Override
+		public int writeInt(Object obj, String name, int oldValue, int newValue) {
+			System.out.println( "Writing int [" + name + "]" );
+			return newValue;
+		}
+
+		@Override
+		public float readFloat(Object obj, String name, float oldValue) {
+			System.out.println( "Reading float [" + name + "]" );
+			return oldValue;
+		}
+
+		@Override
+		public float writeFloat(Object obj, String name, float oldValue, float newValue) {
+			System.out.println( "Writing float [" + name + "]" );
+			return newValue;
+		}
+
+		@Override
+		public double readDouble(Object obj, String name, double oldValue) {
+			System.out.println( "Reading double [" + name + "]" );
+			return oldValue;
+		}
+
+		@Override
+		public double writeDouble(Object obj, String name, double oldValue, double newValue) {
+			System.out.println( "Writing double [" + name + "]" );
+			return newValue;
+		}
+
+		@Override
+		public long readLong(Object obj, String name, long oldValue) {
+			System.out.println( "Reading long [" + name + "]" );
+			return oldValue;
+		}
+
+		@Override
+		public long writeLong(Object obj, String name, long oldValue, long newValue) {
+			System.out.println( "Writing long [" + name + "]" );
+			return newValue;
+		}
+
+		@Override
+		public Object readObject(Object obj, String name, Object oldValue) {
+			System.out.println( "Reading Object [" + name + "]" );
+			return oldValue;
+		}
+
+		@Override
+		public Object writeObject(Object obj, String name, Object oldValue, Object newValue) {
+			System.out.println( "Writing Object [" + name + "]" );
+			return newValue;
+		}
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/CustomerInventory.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/CustomerInventory.java
new file mode 100644
index 0000000000..8bad23e747
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/CustomerInventory.java
@@ -0,0 +1,152 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+ */package org.hibernate.test.bytecode.enhancement.customer;
+
+/**
+ * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
+ */
+
+import javax.persistence.CascadeType;
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.GenerationType;
+import javax.persistence.Id;
+import javax.persistence.IdClass;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+import javax.persistence.Table;
+import javax.persistence.TableGenerator;
+import javax.persistence.Version;
+import java.io.Serializable;
+import java.math.BigDecimal;
+import java.util.Comparator;
+
+@SuppressWarnings("serial")
+@Entity
+@Table(name="O_CUSTINVENTORY")
+@IdClass(CustomerInventoryPK.class)
+public class CustomerInventory implements Serializable, Comparator<CustomerInventory> {
+
+        public static final String QUERY_COUNT = "CustomerInventory.count";
+
+        @Id
+        @TableGenerator(name="inventory",
+            table="U_SEQUENCES",
+            pkColumnName="S_ID",
+            valueColumnName="S_NEXTNUM",
+            pkColumnValue="inventory",
+            allocationSize=1000)
+    @GeneratedValue(strategy= GenerationType.TABLE,generator="inventory")
+    @Column(name="CI_ID")
+    private Long         id;
+
+    @Id
+    @Column(name = "CI_CUSTOMERID", insertable = false, updatable = false)
+    private int             custId;
+
+    @ManyToOne(cascade= CascadeType.MERGE)
+    @JoinColumn(name="CI_CUSTOMERID")
+    private Customer        customer;
+
+    @ManyToOne(cascade=CascadeType.MERGE)
+    @JoinColumn(name = "CI_ITEMID")
+    private String            vehicle;
+
+    @Column(name="CI_VALUE")
+    private BigDecimal totalCost;
+
+    @Column(name="CI_QUANTITY")
+    private int             quantity;
+
+    @Version
+    @Column(name = "CI_VERSION")
+    private int             version;
+
+    public CustomerInventory() {
+    }
+
+        CustomerInventory(Customer customer, String vehicle, int quantity,
+                      BigDecimal totalValue) {
+        this.customer = customer;
+        this.vehicle = vehicle;
+        this.quantity = quantity;
+        this.totalCost = totalValue;
+    }
+
+    public String getVehicle() {
+        return vehicle;
+    }
+
+    public BigDecimal getTotalCost() {
+        return totalCost;
+    }
+
+    public int getQuantity() {
+        return quantity;
+    }
+
+    public Long getId() {
+        return id;
+    }
+
+    public Customer getCustomer() {
+        return customer;
+    }
+
+    public int getCustId() {
+        return custId;
+    }
+
+    public int getVersion() {
+        return version;
+    }
+
+    public int compare(CustomerInventory cdb1, CustomerInventory cdb2) {
+        return cdb1.id.compareTo(cdb2.id);
+    }
+
+            @Override
+    public boolean equals(Object obj) {
+        if (obj == this)
+            return true;
+        if (obj == null || !(obj instanceof CustomerInventory))
+            return false;
+        if (this.id == ((CustomerInventory)obj).id)
+            return true;
+        if (this.id != null && ((CustomerInventory)obj).id == null)
+            return false;
+        if (this.id == null && ((CustomerInventory)obj).id != null)
+            return false;
+
+        return this.id.equals(((CustomerInventory)obj).id);
+    }
+
+    @Override
+    public int hashCode() {
+        int result = id.hashCode();
+        result = 31 * result + custId;
+        return result;
+    }
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/CustomerInventoryPK.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/CustomerInventoryPK.java
new file mode 100644
index 0000000000..773547c645
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/CustomerInventoryPK.java
@@ -0,0 +1,50 @@
+/*
+ * Copyright 2012 Red Hat, Inc. and/or its affiliates.
+ *
+ * Licensed under the Eclipse Public License version 1.0, available at
+ * http://www.eclipse.org/legal/epl-v10.html
+ */
+package org.hibernate.test.bytecode.enhancement.customer;
+
+import java.io.Serializable;
+
+/**
+ * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
+ */
+public class CustomerInventoryPK implements Serializable {
+
+    private Long id;
+    private int custId;
+
+    public CustomerInventoryPK() {
+    }
+
+    public CustomerInventoryPK(Long id, int custId) {
+        this.id = id;
+        this.custId = custId;
+    }
+
+    public boolean equals(Object other) {
+        if (other == this) {
+            return true;
+        }
+        if (other == null || getClass() != other.getClass()) {
+            return false;
+        }
+        CustomerInventoryPK cip = (CustomerInventoryPK) other;
+        return (custId == cip.custId && (id == cip.id ||
+                ( id != null && id.equals(cip.id))));
+    }
+
+    public int hashCode() {
+        return (id == null ? 0 : id.hashCode()) ^ custId;
+    }
+
+    public Long getId() {
+        return id;
+    }
+
+    public int getCustId() {
+        return custId;
+    }
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/SupplierComponentPK.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/SupplierComponentPK.java
new file mode 100644
index 0000000000..45467d7bc9
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/customer/SupplierComponentPK.java
@@ -0,0 +1,71 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.bytecode.enhancement.customer;
+
+import javax.persistence.Embeddable;
+
+/**
+ * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
+ */
+@Embeddable
+public class SupplierComponentPK {
+
+    String componentID;
+    int supplierID;
+
+    public SupplierComponentPK() {
+    }
+
+    public SupplierComponentPK(String suppCompID, int suppCompSuppID) {
+        this.componentID = suppCompID;
+        this.supplierID = suppCompSuppID;
+    }
+
+    public String getComponentID() {
+        return componentID;
+    }
+
+    public int getSupplierID() {
+        return supplierID;
+    }
+
+    @Override
+    public int hashCode() {
+        final int PRIME = 31;
+        int result = 1;
+        result = PRIME * result + componentID.hashCode();
+        result = PRIME * result + supplierID;
+        return result;
+    }
+
+    @Override
+    public boolean equals(Object obj) {
+        if (this == obj)
+            return true;
+        if (obj == null || getClass() != obj.getClass())
+            return false;
+        final SupplierComponentPK other = (SupplierComponentPK) obj;
+        return componentID.equals(other.componentID);
+    }
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index ee2766c018..03f099a290 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
@@ -1,867 +1,873 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * JBoss, Home of Professional Open Source
  * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
 package org.hibernate.test.cfg.persister;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Comparator;
 import java.util.Map;
+import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class GoofyPersisterClassProvider implements PersisterClassResolver {
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	public static class NoopEntityPersister implements EntityPersister {
 
 		public NoopEntityPersister(org.hibernate.mapping.PersistentClass persistentClass,
 								   org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
 								   NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 								   SessionFactoryImplementor sf,
 								   Mapping mapping) {
 			throw new GoofyException(NoopEntityPersister.class);
 		}
 
 		@Override
 		public EntityMode getEntityMode() {
 			return null;
 		}
 
 		@Override
 		public EntityTuplizer getEntityTuplizer() {
 			return null;
 		}
 
 		@Override
 		public EntityInstrumentationMetadata getInstrumentationMetadata() {
 			return new NonPojoInstrumentationMetadata( null );
 		}
 
 		@Override
 		public void generateEntityDefinition() {
 		}
 
 		@Override
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		@Override
 		public SessionFactoryImplementor getFactory() {
 			return null;
 		}
 
 		@Override
 		public String getRootEntityName() {
 			return null;
 		}
 
 		@Override
 		public String getEntityName() {
 			return null;
 		}
 
 		@Override
 		public EntityMetamodel getEntityMetamodel() {
 			return null;
 		}
 
 		@Override
 		public boolean isSubclassEntityName(String entityName) {
 			return false;
 		}
 
 		@Override
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public boolean hasProxy() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasMutableProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasSubselectLoadableCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCascades() {
 			return false;
 		}
 
 		@Override
 		public boolean isMutable() {
 			return false;
 		}
 
 		@Override
 		public boolean isInherited() {
 			return false;
 		}
 
 		@Override
 		public boolean isIdentifierAssignedByInsert() {
 			return false;
 		}
 
 		@Override
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;
 		}
 
 		@Override
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean hasIdentifierProperty() {
 			return false;
 		}
 
 		@Override
 		public boolean canExtractIdOutOfEntity() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersioned() {
 			return false;
 		}
 
 		@Override
 		public Comparator getVersionComparator() {
 			return null;
 		}
 
 		@Override
 		public VersionType getVersionType() {
 			return null;
 		}
 
 		@Override
 		public int getVersionProperty() {
 			return 0;
 		}
 
 		@Override
 		public boolean hasNaturalIdentifier() {
 			return false;
 		}
 
 		@Override
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];
 		}
 
 		@Override
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 				SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean hasLazyProperties() {
 			return false;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
 		}
 
 		@Override
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
 		}
 
 		@Override
 		public Type[] getPropertyTypes() {
 			return new Type[0];
 		}
 
 		@Override
 		public String[] getPropertyNames() {
 			return new String[0];
 		}
 
 		@Override
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];
 		}
 
 		@Override
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];
 		}
 
 		@Override
 		public Type getIdentifierType() {
 			return null;
 		}
 
 		@Override
 		public String getIdentifierPropertyName() {
 			return null;
 		}
 
 		@Override
 		public boolean isCacheInvalidationRequired() {
 			return false;
 		}
 
 		@Override
 		public boolean isLazyPropertiesCacheable() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCache() {
 			return false;
 		}
 
 		@Override
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;
 		}
 		
 		@Override
 		public boolean hasNaturalIdCache() {
 			return false;
 		}
 
 		@Override
 		public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 			return null;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(
 				Object entity, Object[] state, Object version, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public ClassMetadata getClassMetadata() {
 			return null;
 		}
 
 		@Override
 		public boolean isBatchLoadable() {
 			return false;
 		}
 
 		@Override
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;
 		}
 
 		@Override
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 			throw new UnsupportedOperationException( "not supported" );
 		}
 
 		@Override
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return false;
 		}
 
 		@Override
 		public boolean hasInsertGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasUpdateGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersionPropertyGenerated() {
 			return false;
 		}
 
 		@Override
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		}
 
 		@Override
 		public void afterReassociate(Object entity, SessionImplementor session) {
 		}
 
 		@Override
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public Class getMappedClass() {
 			return null;
 		}
 
 		@Override
 		public boolean implementsLifecycle() {
 			return false;
 		}
 
 		@Override
 		public Class getConcreteProxyClass() {
 			return null;
 		}
 
 		@Override
 		public void setPropertyValues(Object object, Object[] values) {
 		}
 
 		@Override
 		public void setPropertyValue(Object object, int i, Object value) {
 		}
 
 		@Override
 		public Object[] getPropertyValues(Object object) {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, int i) {
 			return null;
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, String propertyName) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object object) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		}
 
 		@Override
 		public Object getVersion(Object object) {
 			return null;
 		}
 
 		@Override
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstance(Object object) {
 			return false;
 		}
 
 		@Override
 		public boolean hasUninitializedLazyProperties(Object object) {
 			return false;
 		}
 
 		@Override
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		}
 
 		@Override
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 			return null;
 		}
 
 		@Override
 		public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 			// TODO Auto-generated method stub
 			return null;
 		}
 
 		@Override
 		public EntityPersister getEntityPersister() {
 			return this;
 		}
 
 		@Override
 		public EntityIdentifierDefinition getEntityKeyDefinition() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public Iterable<AttributeDefinition> getAttributes() {
 			throw new NotYetImplementedException();
 		}
+
+        @Override
+        public int[] resolveAttributeIndexes(Set<String> attributes) {
+            return null;
+        }
 	}
 
 	public static class NoopCollectionPersister implements CollectionPersister {
 
 		public NoopCollectionPersister(org.hibernate.mapping.Collection collection,
 									   org.hibernate.cache.spi.access.CollectionRegionAccessStrategy strategy,
 									   org.hibernate.cfg.Configuration configuration,
 									   SessionFactoryImplementor sf) {
 			throw new GoofyException(NoopCollectionPersister.class);
 		}
 
 		public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public CollectionPersister getCollectionPersister() {
 			return this;
 		}
 
 		public CollectionType getCollectionType() {
 			throw new NotYetImplementedException();
 		}
 
 		@Override
 		public CollectionIndexDefinition getIndexDefinition() {
 			throw new NotYetImplementedException();
 		}
 
 		@Override
 		public CollectionElementDefinition getElementDefinition() {
 			throw new NotYetImplementedException();
 		}
 
 		public Type getKeyType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIndexType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getElementType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getElementClass() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readElement(ResultSet rs, Object owner, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIdentifier(ResultSet rs, String columnAlias, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isPrimitiveArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isOneToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isManyToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIndex() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInverse() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void recreate(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void deleteRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void updateRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insertRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRole() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getOwnerEntityPersister() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrphanDelete() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasManyToManyOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getCollectionSpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionMetadata getCollectionMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCascadeDeleteEnabled() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getElementNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIndexNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void postInstantiate() throws MappingException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getKeyColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getIndexColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getElementColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierColumnAlias(String suffix) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isExtraLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getSize(Serializable key, SessionImplementor session) {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public int getBatchSize() {
 			return 0;
 		}
 
 		@Override
 		public String getMappedByProperty() {
 			return null;
 		}
 
 		@Override
 		public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
index 4333a97774..1b66e39f2d 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
@@ -1,696 +1,702 @@
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Hashtable;
 import java.util.Map;
+import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDHexGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.StaticFilterAliasGenerator;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 public class CustomPersister implements EntityPersister {
 
 	private static final Hashtable INSTANCES = new Hashtable();
 	private static final IdentifierGenerator GENERATOR = new UUIDHexGenerator();
 
 	private SessionFactoryImplementor factory;
 
 	public CustomPersister(
 			PersistentClass model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping mapping) {
 		this.factory = factory;
 	}
 
 	public boolean hasLazyProperties() {
 		return false;
 	}
 
 	public boolean isInherited() {
 		return false;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public Class getMappedClass() {
 		return Custom.class;
 	}
 
 	@Override
 	public void generateEntityDefinition() {
 	}
 
 	public void postInstantiate() throws MappingException {}
 
 	public String getEntityName() {
 		return Custom.class.getName();
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return Custom.class.getName().equals(entityName);
 	}
 
 	public boolean hasProxy() {
 		return false;
 	}
 
 	public boolean hasCollections() {
 		return false;
 	}
 
 	public boolean hasCascades() {
 		return false;
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return false;
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return false;
 	}
 
 	public Boolean isTransient(Object object, SessionImplementor session) {
 		return ( (Custom) object ).id==null;
 	}
 
 	@Override
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 		return getPropertyValues( object );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void retrieveGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean implementsLifecycle() {
 		return false;
 	}
 
 	@Override
 	public Class getConcreteProxyClass() {
 		return Custom.class;
 	}
 
 	@Override
 	public void setPropertyValues(Object object, Object[] values) {
 		setPropertyValue( object, 0, values[0] );
 	}
 
 	@Override
 	public void setPropertyValue(Object object, int i, Object value) {
 		( (Custom) object ).setName( (String) value );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object object) throws HibernateException {
 		Custom c = (Custom) object;
 		return new Object[] { c.getName() };
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		return ( (Custom) object ).id;
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return ( (Custom) entity ).id;
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		( (Custom) entity ).id = (String) id;
 	}
 
 	@Override
 	public Object getVersion(Object object) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		Custom c = new Custom();
 		c.id = (String) id;
 		return c;
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return object instanceof Custom;
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return false;
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		( ( Custom ) entity ).id = ( String ) currentId;
 	}
 
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	public int[] findDirty(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	public int[] findModified(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * @see EntityPersister#hasIdentifierProperty()
 	 */
 	public boolean hasIdentifierProperty() {
 		return true;
 	}
 
 	/**
 	 * @see EntityPersister#isVersioned()
 	 */
 	public boolean isVersioned() {
 		return false;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionType()
 	 */
 	public VersionType getVersionType() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionProperty()
 	 */
 	public int getVersionProperty() {
 		return 0;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierGenerator()
 	 */
 	public IdentifierGenerator getIdentifierGenerator()
 	throws HibernateException {
 		return GENERATOR;
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, org.hibernate.LockOptions , SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 		return load(id, optionalObject, lockOptions.getLockMode(), session);
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, LockMode, SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		// fails when optional object is supplied
 
 		Custom clone = null;
 		Custom obj = (Custom) INSTANCES.get(id);
 		if (obj!=null) {
 			clone = (Custom) obj.clone();
 			TwoPhaseLoad.addUninitializedEntity(
 					session.generateEntityKey( id, this ),
 					clone,
 					this,
 					LockMode.NONE,
 					false,
 					session
 			);
 			TwoPhaseLoad.postHydrate(
 					this, id,
 					new String[] { obj.getName() },
 					null,
 					clone,
 					LockMode.NONE,
 					false,
 					session
 			);
 			TwoPhaseLoad.initializeEntity(
 					clone,
 					false,
 					session,
 					new PreLoadEvent( (EventSource) session )
 			);
 			TwoPhaseLoad.postLoad( clone, session, new PostLoadEvent( (EventSource) session ) );
 		}
 		return clone;
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void insert(
 		Serializable id,
 		Object[] fields,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put(id, ( (Custom) object ).clone() );
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void delete(
 		Serializable id,
 		Object version,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.remove(id);
 	}
 
 	/**
 	 * @see EntityPersister
 	 */
 	public void update(
 		Serializable id,
 		Object[] fields,
 		int[] dirtyFields,
 		boolean hasDirtyCollection,
 		Object[] oldFields,
 		Object oldVersion,
 		Object object,
 		Object rowId,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put( id, ( (Custom) object ).clone() );
 
 	}
 
 	private static final Type[] TYPES = new Type[] { StandardBasicTypes.STRING };
 	private static final String[] NAMES = new String[] { "name" };
 	private static final boolean[] MUTABILITY = new boolean[] { true };
 	private static final boolean[] GENERATION = new boolean[] { false };
 
 	/**
 	 * @see EntityPersister#getPropertyTypes()
 	 */
 	public Type[] getPropertyTypes() {
 		return TYPES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyNames()
 	 */
 	public String[] getPropertyNames() {
 		return NAMES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyCascadeStyles()
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierType()
 	 */
 	public Type getIdentifierType() {
 		return StandardBasicTypes.STRING;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierPropertyName()
 	 */
 	public String getIdentifierPropertyName() {
 		return "id";
 	}
 
 	public boolean hasCache() {
 		return false;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return null;
 	}
 	
 	public boolean hasNaturalIdCache() {
 		return false;
 	}
 
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return null;
 	}
 
 	public String getRootEntityName() {
 		return "CUSTOMS";
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	/**
 	 * @see EntityPersister#getClassMetadata()
 	 */
 	public ClassMetadata getClassMetadata() {
 		return null;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return MUTABILITY;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return MUTABILITY;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyInsertability()
 	 */
 	public boolean[] getPropertyInsertability() {
 		return MUTABILITY;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 
 	public boolean canExtractIdOutOfEntity() {
 		return true;
 	}
 
 	public boolean isBatchLoadable() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session)
 		throws HibernateException {
 		throw new UnsupportedOperationException("no proxy for this class");
 	}
 
 	public Object getCurrentVersion(
 		Serializable id,
 		SessionImplementor session)
 		throws HibernateException {
 
 		return INSTANCES.get(id);
 	}
 
 	@Override
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 			throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public boolean isCacheInvalidationRequired() {
 		return false;
 	}
 
 	@Override
 	public void afterInitialize(Object entity, boolean fetched, SessionImplementor session) {
 	}
 
 	@Override
 	public void afterReassociate(Object entity, SessionImplementor session) {
 	}
 
 	@Override
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 		throw new UnsupportedOperationException( "not supported" );
 	}
 
 	@Override
 	public boolean[] getPropertyVersionability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return UnstructuredCacheEntry.INSTANCE;
 	}
 
 	@Override
 	public CacheEntry buildCacheEntry(
 			Object entity, Object[] state, Object version, SessionImplementor session) {
 		return new StandardCacheEntryImpl(
 				state,
 				this,
 				this.hasUninitializedLazyProperties( entity ),
 				version,
 				session,
 				entity
 		);
 	}
 
 	@Override
 	public boolean hasSubselectLoadableCollections() {
 		return false;
 	}
 
 	@Override
 	public int[] getNaturalIdentifierProperties() {
 		return null;
 	}
 
 	@Override
 	public boolean hasNaturalIdentifier() {
 		return false;
 	}
 
 	@Override
 	public boolean hasMutableProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean isInstrumented() {
 		return false;
 	}
 
 	@Override
 	public boolean hasInsertGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean hasUpdateGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean[] getPropertyLaziness() {
 		return null;
 	}
 
 	@Override
 	public boolean isLazyPropertiesCacheable() {
 		return true;
 	}
 
 	@Override
 	public boolean isVersionPropertyGenerated() {
 		return false;
 	}
 
 	@Override
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 			SessionImplementor session) {
 		return null;
 	}
 
 	@Override
 	public Comparator getVersionComparator() {
 		return null;
 	}
 
 	@Override
 	public EntityMetamodel getEntityMetamodel() {
 		return null;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return null;
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return new NonPojoInstrumentationMetadata( getEntityName() );
 	}
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new StaticFilterAliasGenerator(rootAlias);
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return this;
 	}
 
 	@Override
 	public EntityIdentifierDefinition getEntityKeyDefinition() {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		throw new NotYetImplementedException();
 	}
+
+    @Override
+    public int[] resolveAttributeIndexes(Set<String> attributes) {
+        return null;
+    }
 }
diff --git a/hibernate-gradle-plugin/src/main/groovy/org/hibernate/tooling/gradle/EnhancerTask.groovy b/hibernate-gradle-plugin/src/main/groovy/org/hibernate/tooling/gradle/EnhancerTask.groovy
index 5f77c383e7..a13f59b8b1 100644
--- a/hibernate-gradle-plugin/src/main/groovy/org/hibernate/tooling/gradle/EnhancerTask.groovy
+++ b/hibernate-gradle-plugin/src/main/groovy/org/hibernate/tooling/gradle/EnhancerTask.groovy
@@ -1,156 +1,170 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.tooling.gradle
 
 import javassist.ClassPool
 import javassist.CtClass
 import javassist.CtField
 import org.gradle.api.DefaultTask
 import org.gradle.api.file.FileTree
 import org.gradle.api.tasks.TaskAction
 import org.hibernate.bytecode.enhance.spi.EnhancementContext
 import org.hibernate.bytecode.enhance.spi.Enhancer
 
+import javax.persistence.ElementCollection
 import javax.persistence.Entity
+import javax.persistence.ManyToMany
+import javax.persistence.OneToMany
 import javax.persistence.Transient
 
 /**
  * Gradle Task to apply Hibernate's bytecode Enhancer
  *
  * @author Jeremy Whiting
  */
 public class EnhancerTask extends DefaultTask implements EnhancementContext {
 
     private ClassLoader overridden
 
     public EnhancerTask() {
         super()
         setDescription( 'Enhances Entity classes for efficient association referencing.' )
     }
 
     @TaskAction
     def enhance() {
         logger.info( 'enhance task started' )
         ext.pool = new ClassPool( false )
         ext.enhancer = new Enhancer( this )
         FileTree tree = project.fileTree( dir: project.sourceSets.main.output.classesDir )
         tree.include '**/*.class'
         tree.each( { File file ->
             final byte[] enhancedBytecode;
             InputStream is = null;
             CtClass clas = null;
             try {
                 is = new FileInputStream( file.toString() )
                 clas = ext.pool.makeClass( is )
                 // Enhancer already does this check to see if it should enhance, why are we doing it again here?
                 if ( !clas.hasAnnotation( Entity.class ) ) {
                     logger.debug( "Class $file not an annotated Entity class. skipping..." )
                 }
                 else {
                     enhancedBytecode = ext.enhancer.enhance( clas.getName(), clas.toBytecode() );
                 }
             }
             catch (Exception e) {
                 logger.error( "Unable to enhance class [${file.toString()}]", e )
                 return
             }
             finally {
                 try {
                     if ( null != is ) {
                         is.close()
                     };
                 }
                 finally {}
             }
             if ( null != enhancedBytecode ) {
                 if ( file.delete() ) {
                     if ( !file.createNewFile() ) {
                         logger.error( "Unable to recreate class file [" + clas.getName() + "]" )
                     }
                 }
                 else {
                     logger.error( "Unable to delete class file [" + clas.getName() + "]" )
                 }
                 FileOutputStream outputStream = new FileOutputStream( file, false )
                 try {
                     outputStream.write( enhancedBytecode )
                     outputStream.flush()
                 }
                 finally {
                     try {
                         if ( outputStream != null ) {
                             outputStream.close()
                         }
                         clas.detach()//release memory
                     }
                     catch (IOException ignore) {
                     }
                 }
             }
         } )
         logger.info( 'enhance task finished' )
     }
 
     public ClassLoader getLoadingClassLoader() {
         if ( null == this.overridden ) {
             return getClass().getClassLoader();
         }
         else {
             return this.overridden;
         }
     }
 
     public void setClassLoader(ClassLoader loader) {
         this.overridden = loader;
     }
 
     public boolean isEntityClass(CtClass classDescriptor) {
         return true;
     }
 
     public boolean hasLazyLoadableAttributes(CtClass classDescriptor) {
         return true;
     }
 
     public boolean isLazyLoadable(CtField field) {
         return true;
     }
 
     public boolean isCompositeClass(CtClass classDescriptor) {
         return false;
     }
 
     public boolean doDirtyCheckingInline(CtClass classDescriptor) {
-        return false;
+        return true;
     }
 
     public CtField[] order(CtField[] fields) {
         // TODO: load ordering from configuration.
         return fields;
     }
 
+    public boolean isMappedCollection(CtField field) {
+        try {
+            return (field.getAnnotation(OneToMany.class) != null ||
+                    field.getAnnotation(ManyToMany.class) != null ||
+                    field.getAnnotation(ElementCollection.class) != null);
+        }
+        catch (ClassNotFoundException e) {
+            return false;
+        }
+    }
+
     public boolean isPersistentField(CtField ctField) {
         return !ctField.hasAnnotation( Transient.class );
     }
 } 
diff --git a/hibernate-maven-plugin/src/main/java/org/hibernate/bytecode/enhance/plugins/HibernateEnhancementMojo.java b/hibernate-maven-plugin/src/main/java/org/hibernate/bytecode/enhance/plugins/HibernateEnhancementMojo.java
index 81336c821d..4d8874f941 100644
--- a/hibernate-maven-plugin/src/main/java/org/hibernate/bytecode/enhance/plugins/HibernateEnhancementMojo.java
+++ b/hibernate-maven-plugin/src/main/java/org/hibernate/bytecode/enhance/plugins/HibernateEnhancementMojo.java
@@ -1,234 +1,266 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.enhance.plugins;
 
 import java.io.File;
 import java.io.FileFilter;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.List;
 
 import javassist.ClassPool;
 import javassist.CtClass;
 import javassist.CtField;
 
+import javax.persistence.Embeddable;
 import javax.persistence.Entity;
+import javax.persistence.ElementCollection;
+import javax.persistence.ManyToMany;
+import javax.persistence.OneToMany;
 import javax.persistence.Transient;
 
 import org.hibernate.bytecode.enhance.spi.EnhancementContext;
 import org.hibernate.bytecode.enhance.spi.Enhancer;
 
 import org.apache.maven.plugin.AbstractMojo;
 import org.apache.maven.plugins.annotations.Mojo;
 import org.apache.maven.plugin.MojoExecutionException;
 import org.apache.maven.plugin.MojoFailureException;
 import org.apache.maven.plugins.annotations.Parameter;
 
 /**
  * This plugin will enhance Entity objects.
  * 
  * @author Jeremy Whiting
  */
 @Mojo(name = "enhance")
-public class HibernateEnhancementMojo extends AbstractMojo {
+public class HibernateEnhancementMojo extends AbstractMojo implements EnhancementContext {
 
 	/**
 	 * The contexts to use during enhancement.
 	 */
 	private List<File> classes = new ArrayList<File>();
 	private ClassPool pool = new ClassPool( false );
+    private final Enhancer enhancer = new Enhancer( this);
 
 	private static final String CLASS_EXTENSION = ".class";
 
 	@Parameter(property="dir", defaultValue="${project.build.outputDirectory}")
 	private String dir = null;
 
 	public void execute() throws MojoExecutionException, MojoFailureException {
 		getLog().info( "Started enhance plugin....." );
 		/** Perform a depth first search for files. */
 		File root = new File( this.dir ); 
 		walkDir( root );
 
-		Enhancer enhancer = new Enhancer( new EnhancementContext() {
-
-			private ClassLoader overridden;
-
-			public ClassLoader getLoadingClassLoader() {
-				if ( null == this.overridden ) {
-					return getClass().getClassLoader();
-				}
-				else {
-					return this.overridden;
-				}
-			}
-
-			public void setClassLoader(ClassLoader loader) {
-				this.overridden = loader;
-			}
-
-			public boolean isEntityClass(CtClass classDescriptor) {
-				return true;
-			}
-
-			public boolean hasLazyLoadableAttributes(CtClass classDescriptor) {
-				return true;
-			}
-
-			public boolean isLazyLoadable(CtField field) {
-				return true;
-			}
-
-			public boolean isCompositeClass(CtClass classDescriptor) {
-				return false;
-			}
-
-			public boolean doDirtyCheckingInline(CtClass classDescriptor) {
-				return false;
-			}
-
-			public CtField[] order(CtField[] fields) {
-				// TODO: load ordering from configuration.
-				return fields;
-			}
-
-			public boolean isPersistentField(CtField ctField) {
-				return !ctField.hasAnnotation( Transient.class );
-			}
-
-		} );
-
 		if ( 0 < classes.size() ) {
 			for ( File file : classes ) {
-				enhanceClass( enhancer, file );
+				processClassFile(file);
 			}
 		}
 
 		getLog().info( "Enhance plugin completed." );
 	}
 
 	/**
 	 * Expects a directory.
 	 */
 	private void walkDir(File dir) {
 
 		walkDir( dir, new FileFilter() {
 
 			@Override
 			public boolean accept(File pathname) {
 				return ( pathname.isFile() && pathname.getName().endsWith( CLASS_EXTENSION ) );
 			}
 		}, new FileFilter() {
 
 			@Override
 			public boolean accept(File pathname) {
 				return ( pathname.isDirectory() );
 			}
 		} );
 	}
 
 	private void walkDir(File dir, FileFilter classesFilter, FileFilter dirFilter) {
+
 		File[] dirs = dir.listFiles( dirFilter );
 		for ( int i = 0; i < dirs.length; i++ ) {
 			walkDir( dirs[i], classesFilter, dirFilter );
 		}
 		dirs = null;
 		File[] files = dir.listFiles( classesFilter );
 		for ( int i = 0; i < files.length; i++ ) {
 			this.classes.add( files[i] );
 		}
 	}
 
-	private void enhanceClass(Enhancer enhancer, File file) {
-		byte[] enhancedBytecode = null;
-		InputStream is = null;
-		CtClass clas = null;
+
+    /**
+     * Atm only process files annotated with either @Entity or @Embeddable
+     * @param javaClassFile
+     */
+    private void processClassFile(File javaClassFile)
+            throws MojoExecutionException {
 		try {
-			is = new FileInputStream( file.toString() );
-			clas = getClassPool().makeClass( is );
-			if ( !clas.hasAnnotation( Entity.class ) ) {
-				getLog().debug( "Class $file not an annotated Entity class. skipping..." );
-			}
+			final CtClass ctClass = getClassPool().makeClass( new FileInputStream( javaClassFile ) );
+            if(this.isEntityClass(ctClass))
+                processEntityClassFile(javaClassFile, ctClass);
+            else if(this.isCompositeClass(ctClass))
+                processCompositeClassFile(javaClassFile, ctClass);
+
+        }
+        catch (IOException e) {
+            throw new MojoExecutionException(
+                    String.format( "Error processing included file [%s]", javaClassFile.getAbsolutePath() ), e );
+        }
+    }
+
+    private void processEntityClassFile(File javaClassFile, CtClass ctClass ) {
+        try {
+            byte[] result = enhancer.enhance( ctClass.getName(), ctClass.toBytecode() );
+            if(result != null)
+                writeEnhancedClass(javaClassFile, result);
+        }
+        catch (Exception e) {
+            getLog().error( "Unable to enhance class [" + ctClass.getName() + "]", e);
+            return;
+        }
+    }
+
+    private void processCompositeClassFile(File javaClassFile, CtClass ctClass) {
+        try {
+            byte[] result = enhancer.enhanceComposite(ctClass.getName(), ctClass.toBytecode());
+            if(result != null)
+                writeEnhancedClass(javaClassFile, result);
+        }
+        catch (Exception e) {
+            getLog().error( "Unable to enhance class [" + ctClass.getName() + "]", e);
+            return;
+        }
+    }
+
+    private void writeEnhancedClass(File javaClassFile, byte[] result)
+            throws MojoExecutionException {
+        try {
+			if ( javaClassFile.delete() ) {
+                    if ( ! javaClassFile.createNewFile() ) {
+                        getLog().error( "Unable to recreate class file [" + javaClassFile.getName() + "]");
+                    }
+            }
 			else {
-				enhancedBytecode = enhancer.enhance( clas.getName(), clas.toBytecode() );
+				getLog().error( "Unable to delete class file [" + javaClassFile.getName() + "]");
 			}
-		}
-		catch (Exception e) {
-			getLog().error( "Unable to enhance class [${file.toString()}]", e );
-			return;
-		}
-		finally {
-			try {
-				if ( null != is )
-					is.close();
-			}
-			catch (IOException ioe) {}
-		}
-		if ( null != enhancedBytecode ) {
-			if ( file.delete() ) {
-				try {
-					if ( !file.createNewFile() ) {
-						getLog().error( "Unable to recreate class file [" + clas.getName() + "]" );
-					}
-				}
-				catch (IOException ioe) {
-				}
-			}
-			else {
-				getLog().error( "Unable to delete class file [" + clas.getName() + "]" );
-			}
-			FileOutputStream outputStream = null;
+
+			FileOutputStream outputStream = new FileOutputStream( javaClassFile, false );
 			try {
-				outputStream = new FileOutputStream( file, false );
-				outputStream.write( enhancedBytecode );
+				outputStream.write( result);
 				outputStream.flush();
 			}
-			catch (IOException ioe) {
-			}
 			finally {
 				try {
-					if ( outputStream != null )
-						outputStream.close();
-					clas.detach();// release memory
+					outputStream.close();
 				}
-				catch (IOException ignore) {
+				catch ( IOException ignore) {
 				}
 			}
-		}
+        }
+        catch (FileNotFoundException ignore) {
+            // should not ever happen because of explicit checks
+        }
+        catch (IOException e) {
+            throw new MojoExecutionException(
+                    String.format( "Error processing included file [%s]", javaClassFile.getAbsolutePath() ), e );
+        }
+    }
+
+	private ClassPool getClassPool() {
+		return this.pool;
 	}
 
-	public void setDir(String dir) {
-		if ( null != dir && !"".equals( dir.trim() ) ) {
-			this.dir = dir;
-		}
+    private boolean shouldInclude(CtClass ctClass) {
+		// we currently only handle entity enhancement
+		return ctClass.hasAnnotation( Entity.class );
 	}
 
-	private ClassPool getClassPool() {
-		return this.pool;
+	@Override
+	public ClassLoader getLoadingClassLoader() {
+		return getClass().getClassLoader();
+	}
+
+	@Override
+	public boolean isEntityClass(CtClass classDescriptor) {
+        return classDescriptor.hasAnnotation(Entity.class);
+    }
+
+	@Override
+	public boolean isCompositeClass(CtClass classDescriptor) {
+        return classDescriptor.hasAnnotation(Embeddable.class);
+	}
+
+	@Override
+	public boolean doDirtyCheckingInline(CtClass classDescriptor) {
+		return true;
 	}
 
+	@Override
+	public boolean hasLazyLoadableAttributes(CtClass classDescriptor) {
+		return true;
+	}
+
+	@Override
+	public boolean isLazyLoadable(CtField field) {
+		return true;
+	}
+
+	@Override
+	public boolean isPersistentField(CtField ctField) {
+		// current check is to look for @Transient
+		return ! ctField.hasAnnotation( Transient.class );
+	}
+
+    @Override
+    public boolean isMappedCollection(CtField field) {
+        try {
+            return (field.getAnnotation(OneToMany.class) != null ||
+                    field.getAnnotation(ManyToMany.class) != null ||
+                    field.getAnnotation(ElementCollection.class) != null);
+        }
+        catch (ClassNotFoundException e) {
+            return false;
+        }
+    }
+
+	@Override
+	public CtField[] order(CtField[] persistentFields) {
+		// for now...
+		return persistentFields;
+		// eventually needs to consult the Hibernate metamodel for proper ordering
+	}
 }
