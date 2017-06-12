diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java
index fe9e7e71af..f13ea17765 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java
@@ -1,215 +1,215 @@
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
 package org.hibernate.engine.internal;
 
 import java.util.Iterator;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.PropertyValueException;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
-import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * Implements the algorithm for validating property values for illegal null values
  * 
  * @author Gavin King
  */
 public final class Nullability {
 	
 	private final SessionImplementor session;
 	private final boolean checkNullability;
 
 	public Nullability(SessionImplementor session) {
 		this.session = session;
 		this.checkNullability = session.getFactory().getSettings().isCheckNullability();
 	}
 	/**
 	 * Check nullability of the class persister properties
 	 *
 	 * @param values entity properties
 	 * @param persister class persister
 	 * @param isUpdate whether it is intended to be updated or saved
 	 * @throws org.hibernate.PropertyValueException Break the nullability of one property
 	 * @throws HibernateException error while getting Component values
 	 */
 	public void checkNullability(
 			final Object[] values,
 			final EntityPersister persister,
 			final boolean isUpdate) 
 	throws PropertyValueException, HibernateException {
 		/*
 		 * Typically when Bean Validation is on, we don't want to validate null values
 		 * at the Hibernate Core level. Hence the checkNullability setting.
 		 */
 		if ( checkNullability ) {
 			/*
 			  * Algorithm
 			  * Check for any level one nullability breaks
 			  * Look at non null components to
 			  *   recursively check next level of nullability breaks
 			  * Look at Collections contraining component to
 			  *   recursively check next level of nullability breaks
 			  *
 			  *
 			  * In the previous implementation, not-null stuffs where checked
 			  * filtering by level one only updateable
 			  * or insertable columns. So setting a sub component as update="false"
 			  * has no effect on not-null check if the main component had good checkeability
 			  * In this implementation, we keep this feature.
 			  * However, I never see any documentation mentioning that, but it's for
 			  * sure a limitation.
 			  */
 
 			final boolean[] nullability = persister.getPropertyNullability();
 			final boolean[] checkability = isUpdate ?
 				persister.getPropertyUpdateability() :
 				persister.getPropertyInsertability();
 			final Type[] propertyTypes = persister.getPropertyTypes();
 
 			for ( int i = 0; i < values.length; i++ ) {
 
 				if ( checkability[i] && values[i]!= LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 					final Object value = values[i];
 					if ( !nullability[i] && value == null ) {
 
 						//check basic level one nullablilty
 						throw new PropertyValueException(
 								"not-null property references a null or transient value",
 								persister.getEntityName(),
 								persister.getPropertyNames()[i]
 							);
 
 					}
 					else if ( value != null ) {
 
 						//values is not null and is checkable, we'll look deeper
 						String breakProperties = checkSubElementsNullability( propertyTypes[i], value );
 						if ( breakProperties != null ) {
 							throw new PropertyValueException(
 								"not-null property references a null or transient value",
 								persister.getEntityName(),
 								buildPropertyPath( persister.getPropertyNames()[i], breakProperties )
 							);
 						}
 
 					}
 				}
 
 			}
 		}
 	}
 
 	/**
 	 * check sub elements-nullability. Returns property path that break
 	 * nullability or null if none
 	 *
 	 * @param propertyType type to check
 	 * @param value value to check
 	 *
 	 * @return property path
 	 * @throws HibernateException error while getting subcomponent values
 	 */
 	private String checkSubElementsNullability(final Type propertyType, final Object value) 
 	throws HibernateException {
 		//for non null args, check for components and elements containing components
 		if ( propertyType.isComponentType() ) {
 			return checkComponentNullability( value, (CompositeType) propertyType );
 		}
 		else if ( propertyType.isCollectionType() ) {
 
 			//persistent collections may have components
 			CollectionType collectionType = (CollectionType) propertyType;
 			Type collectionElementType = collectionType.getElementType( session.getFactory() );
 			if ( collectionElementType.isComponentType() ) {
 				//check for all components values in the collection
 
 				CompositeType componentType = (CompositeType) collectionElementType;
-				Iterator iter = CascadingAction.getLoadedElementsIterator( session, collectionType, value );
+				Iterator iter = CascadingActions.getLoadedElementsIterator( session, collectionType, value );
 				while ( iter.hasNext() ) {
 					Object compValue = iter.next();
 					if (compValue != null) {
 						return checkComponentNullability(compValue, componentType);
 					}
 				}
 			}
 		}
 		return null;
 	}
 
 	/**
 	 * check component nullability. Returns property path that break
 	 * nullability or null if none
 	 *
 	 * @param value component properties
 	 * @param compType component not-nullable type
 	 *
 	 * @return property path
 	 * @throws HibernateException error while getting subcomponent values
 	 */
 	private String checkComponentNullability(final Object value, final CompositeType compType)
 	throws HibernateException {
 		/* will check current level if some of them are not null
 		 * or sublevels if they exist
 		 */
 		boolean[] nullability = compType.getPropertyNullability();
 		if ( nullability!=null ) {
 			//do the test
 			final Object[] values = compType.getPropertyValues( value, EntityMode.POJO );
 			final Type[] propertyTypes = compType.getSubtypes();
 			for ( int i=0; i<values.length; i++ ) {
 				final Object subvalue = values[i];
 				if ( !nullability[i] && subvalue==null ) {
 					return compType.getPropertyNames()[i];
 				}
 				else if ( subvalue != null ) {
 					String breakProperties = checkSubElementsNullability( propertyTypes[i], subvalue );
 					if ( breakProperties != null ) {
 						return buildPropertyPath( compType.getPropertyNames()[i], breakProperties );
 					}
 	 			}
 	 		}
 		}
 		return null;
 	}
 
 	/**
 	 * Return a well formed property path.
 	 * Basically, it will return parent.child
 	 *
 	 * @param parent parent in path
 	 * @param child child in path
 	 * @return parent-child path
 	 */
 	private static String buildPropertyPath(String parent, String child) {
 		return new StringBuilder( parent.length() + child.length() + 1 )
 			.append(parent).append('.').append(child).toString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadeStyle.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadeStyle.java
index c39f0f6840..3daccaeb89 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadeStyle.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadeStyle.java
@@ -1,322 +1,69 @@
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
 package org.hibernate.engine.spi;
 
 import java.io.Serializable;
-import java.util.HashMap;
-import java.util.Map;
-
-import org.hibernate.MappingException;
-import org.hibernate.internal.util.collections.ArrayHelper;
 
 /**
  * A contract for defining the aspects of cascading various persistence actions.
  *
  * @author Gavin King
+ * @author Steve Ebersole
+ *
  * @see CascadingAction
  */
-public abstract class CascadeStyle implements Serializable {
-
+public interface CascadeStyle extends Serializable {
 	/**
 	 * For this style, should the given action be cascaded?
 	 *
 	 * @param action The action to be checked for cascade-ability.
 	 *
 	 * @return True if the action should be cascaded under this style; false otherwise.
 	 */
-	public abstract boolean doCascade(CascadingAction action);
+	public boolean doCascade(CascadingAction action);
 
 	/**
 	 * Probably more aptly named something like doCascadeToCollectionElements(); it is
 	 * however used from both the collection and to-one logic branches...
 	 * <p/>
 	 * For this style, should the given action really be cascaded?  The default
 	 * implementation is simply to return {@link #doCascade}; for certain
 	 * styles (currently only delete-orphan), however, we need to be able to
 	 * control this separately.
 	 *
 	 * @param action The action to be checked for cascade-ability.
 	 *
 	 * @return True if the action should be really cascaded under this style;
 	 *         false otherwise.
 	 */
-	public boolean reallyDoCascade(CascadingAction action) {
-		return doCascade( action );
-	}
+	public boolean reallyDoCascade(CascadingAction action);
 
 	/**
 	 * Do we need to delete orphaned collection elements?
 	 *
 	 * @return True if this style need to account for orphan delete
 	 *         operations; false otherwise.
 	 */
-	public boolean hasOrphanDelete() {
-		return false;
-	}
-
-	public static final class MultipleCascadeStyle extends CascadeStyle {
-		private final CascadeStyle[] styles;
-
-		public MultipleCascadeStyle(CascadeStyle[] styles) {
-			this.styles = styles;
-		}
-
-		public boolean doCascade(CascadingAction action) {
-			for ( CascadeStyle style : styles ) {
-				if ( style.doCascade( action ) ) {
-					return true;
-				}
-			}
-			return false;
-		}
-
-		public boolean reallyDoCascade(CascadingAction action) {
-			for ( CascadeStyle style : styles ) {
-				if ( style.reallyDoCascade( action ) ) {
-					return true;
-				}
-			}
-			return false;
-		}
-
-		public boolean hasOrphanDelete() {
-			for ( CascadeStyle style : styles ) {
-				if ( style.hasOrphanDelete() ) {
-					return true;
-				}
-			}
-			return false;
-		}
-
-		public String toString() {
-			return ArrayHelper.toString( styles );
-		}
-	}
-
-	/**
-	 * save / delete / update / evict / lock / replicate / merge / persist + delete orphans
-	 */
-	public static final CascadeStyle ALL_DELETE_ORPHAN = new CascadeStyle() {
-		public boolean doCascade(CascadingAction action) {
-			return true;
-		}
-
-		public boolean hasOrphanDelete() {
-			return true;
-		}
-
-		public String toString() {
-			return "STYLE_ALL_DELETE_ORPHAN";
-		}
-	};
-
-	/**
-	 * save / delete / update / evict / lock / replicate / merge / persist
-	 */
-	public static final CascadeStyle ALL = new CascadeStyle() {
-		public boolean doCascade(CascadingAction action) {
-			return true;
-		}
-
-		public String toString() {
-			return "STYLE_ALL";
-		}
-	};
-
-	/**
-	 * save / update
-	 */
-	public static final CascadeStyle UPDATE = new CascadeStyle() {
-		public boolean doCascade(CascadingAction action) {
-			return action == CascadingAction.SAVE_UPDATE;
-		}
-
-		public String toString() {
-			return "STYLE_SAVE_UPDATE";
-		}
-	};
-
-	/**
-	 * lock
-	 */
-	public static final CascadeStyle LOCK = new CascadeStyle() {
-		public boolean doCascade(CascadingAction action) {
-			return action == CascadingAction.LOCK;
-		}
-
-		public String toString() {
-			return "STYLE_LOCK";
-		}
-	};
-
-	/**
-	 * refresh
-	 */
-	public static final CascadeStyle REFRESH = new CascadeStyle() {
-		public boolean doCascade(CascadingAction action) {
-			return action == CascadingAction.REFRESH;
-		}
-
-		public String toString() {
-			return "STYLE_REFRESH";
-		}
-	};
-
-	/**
-	 * evict
-	 */
-	public static final CascadeStyle EVICT = new CascadeStyle() {
-		public boolean doCascade(CascadingAction action) {
-			return action == CascadingAction.EVICT;
-		}
-
-		public String toString() {
-			return "STYLE_EVICT";
-		}
-	};
-
-	/**
-	 * replicate
-	 */
-	public static final CascadeStyle REPLICATE = new CascadeStyle() {
-		public boolean doCascade(CascadingAction action) {
-			return action == CascadingAction.REPLICATE;
-		}
-
-		public String toString() {
-			return "STYLE_REPLICATE";
-		}
-	};
-	/**
-	 * merge
-	 */
-	public static final CascadeStyle MERGE = new CascadeStyle() {
-		public boolean doCascade(CascadingAction action) {
-			return action == CascadingAction.MERGE;
-		}
-
-		public String toString() {
-			return "STYLE_MERGE";
-		}
-	};
-
-	/**
-	 * create
-	 */
-	public static final CascadeStyle PERSIST = new CascadeStyle() {
-		public boolean doCascade(CascadingAction action) {
-			return action == CascadingAction.PERSIST
-					|| action == CascadingAction.PERSIST_ON_FLUSH;
-		}
-
-		public String toString() {
-			return "STYLE_PERSIST";
-		}
-	};
-
-	/**
-	 * delete
-	 */
-	public static final CascadeStyle DELETE = new CascadeStyle() {
-		public boolean doCascade(CascadingAction action) {
-			return action == CascadingAction.DELETE;
-		}
-
-		public String toString() {
-			return "STYLE_DELETE";
-		}
-	};
-
-	/**
-	 * delete + delete orphans
-	 */
-	public static final CascadeStyle DELETE_ORPHAN = new CascadeStyle() {
-		public boolean doCascade(CascadingAction action) {
-			return action == CascadingAction.DELETE || action == CascadingAction.SAVE_UPDATE;
-		}
-
-		public boolean reallyDoCascade(CascadingAction action) {
-			return action == CascadingAction.DELETE;
-		}
-
-		public boolean hasOrphanDelete() {
-			return true;
-		}
-
-		public String toString() {
-			return "STYLE_DELETE_ORPHAN";
-		}
-	};
-
-	/**
-	 * no cascades
-	 */
-	public static final CascadeStyle NONE = new CascadeStyle() {
-		public boolean doCascade(CascadingAction action) {
-			return false;
-		}
-
-		public String toString() {
-			return "STYLE_NONE";
-		}
-	};
-
-	public CascadeStyle() {
-	}
-
-	static final Map<String, CascadeStyle> STYLES = new HashMap<String, CascadeStyle>();
-
-	static {
-		STYLES.put( "all", ALL );
-		STYLES.put( "all-delete-orphan", ALL_DELETE_ORPHAN );
-		STYLES.put( "save-update", UPDATE );
-		STYLES.put( "persist", PERSIST );
-		STYLES.put( "merge", MERGE );
-		STYLES.put( "lock", LOCK );
-		STYLES.put( "refresh", REFRESH );
-		STYLES.put( "replicate", REPLICATE );
-		STYLES.put( "evict", EVICT );
-		STYLES.put( "delete", DELETE );
-		STYLES.put( "remove", DELETE ); // adds remove as a sort-of alias for delete...
-		STYLES.put( "delete-orphan", DELETE_ORPHAN );
-		STYLES.put( "none", NONE );
-	}
-
-	/**
-	 * Factory method for obtaining named cascade styles
-	 *
-	 * @param cascade The named cascade style name.
-	 *
-	 * @return The appropriate CascadeStyle
-	 */
-	public static CascadeStyle getCascadeStyle(String cascade) {
-		CascadeStyle style = STYLES.get( cascade );
-		if ( style == null ) {
-			throw new MappingException( "Unsupported cascade style: " + cascade );
-		}
-		else {
-			return style;
-		}
-	}
+	public boolean hasOrphanDelete();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadeStyles.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadeStyles.java
new file mode 100644
index 0000000000..10ae5eefcd
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadeStyles.java
@@ -0,0 +1,348 @@
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
+import java.util.HashMap;
+import java.util.Map;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.MappingException;
+import org.hibernate.internal.util.collections.ArrayHelper;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CascadeStyles {
+	private static final Logger log = Logger.getLogger( CascadeStyles.class );
+
+	/**
+	 * Disallow instantiation
+	 */
+	private CascadeStyles() {
+	}
+
+	/**
+	 * save / delete / update / evict / lock / replicate / merge / persist + delete orphans
+	 */
+	public static final CascadeStyle ALL_DELETE_ORPHAN = new BaseCascadeStyle() {
+		@Override
+		public boolean doCascade(CascadingAction action) {
+			return true;
+		}
+
+		@Override
+		public boolean hasOrphanDelete() {
+			return true;
+		}
+
+		@Override
+		public String toString() {
+			return "STYLE_ALL_DELETE_ORPHAN";
+		}
+	};
+
+	/**
+	 * save / delete / update / evict / lock / replicate / merge / persist
+	 */
+	public static final CascadeStyle ALL = new BaseCascadeStyle() {
+		@Override
+		public boolean doCascade(CascadingAction action) {
+			return true;
+		}
+
+		@Override
+		public String toString() {
+			return "STYLE_ALL";
+		}
+	};
+
+	/**
+	 * save / update
+	 */
+	public static final CascadeStyle UPDATE = new BaseCascadeStyle() {
+		@Override
+		public boolean doCascade(CascadingAction action) {
+			return action == CascadingActions.SAVE_UPDATE;
+		}
+
+		@Override
+		public String toString() {
+			return "STYLE_SAVE_UPDATE";
+		}
+	};
+
+	/**
+	 * lock
+	 */
+	public static final CascadeStyle LOCK = new BaseCascadeStyle() {
+		@Override
+		public boolean doCascade(CascadingAction action) {
+			return action == CascadingActions.LOCK;
+		}
+
+		@Override
+		public String toString() {
+			return "STYLE_LOCK";
+		}
+	};
+
+	/**
+	 * refresh
+	 */
+	public static final CascadeStyle REFRESH = new BaseCascadeStyle() {
+		@Override
+		public boolean doCascade(CascadingAction action) {
+			return action == CascadingActions.REFRESH;
+		}
+
+		@Override
+		public String toString() {
+			return "STYLE_REFRESH";
+		}
+	};
+
+	/**
+	 * evict
+	 */
+	public static final CascadeStyle EVICT = new BaseCascadeStyle() {
+		@Override
+		public boolean doCascade(CascadingAction action) {
+			return action == CascadingActions.EVICT;
+		}
+
+		@Override
+		public String toString() {
+			return "STYLE_EVICT";
+		}
+	};
+
+	/**
+	 * replicate
+	 */
+	public static final CascadeStyle REPLICATE = new BaseCascadeStyle() {
+		@Override
+		public boolean doCascade(CascadingAction action) {
+			return action == CascadingActions.REPLICATE;
+		}
+
+		@Override
+		public String toString() {
+			return "STYLE_REPLICATE";
+		}
+	};
+
+	/**
+	 * merge
+	 */
+	public static final CascadeStyle MERGE = new BaseCascadeStyle() {
+		@Override
+		public boolean doCascade(CascadingAction action) {
+			return action == CascadingActions.MERGE;
+		}
+
+		@Override
+		public String toString() {
+			return "STYLE_MERGE";
+		}
+	};
+
+	/**
+	 * create
+	 */
+	public static final CascadeStyle PERSIST = new BaseCascadeStyle() {
+		@Override
+		public boolean doCascade(CascadingAction action) {
+			return action == CascadingActions.PERSIST
+					|| action == CascadingActions.PERSIST_ON_FLUSH;
+		}
+
+		@Override
+		public String toString() {
+			return "STYLE_PERSIST";
+		}
+	};
+
+	/**
+	 * delete
+	 */
+	public static final CascadeStyle DELETE = new BaseCascadeStyle() {
+		@Override
+		public boolean doCascade(CascadingAction action) {
+			return action == CascadingActions.DELETE;
+		}
+
+		@Override
+		public String toString() {
+			return "STYLE_DELETE";
+		}
+	};
+
+	/**
+	 * delete + delete orphans
+	 */
+	public static final CascadeStyle DELETE_ORPHAN = new BaseCascadeStyle() {
+		@Override
+		public boolean doCascade(CascadingAction action) {
+			return action == CascadingActions.DELETE || action == CascadingActions.SAVE_UPDATE;
+		}
+
+		@Override
+		public boolean reallyDoCascade(CascadingAction action) {
+			return action == CascadingActions.DELETE;
+		}
+
+		@Override
+		public boolean hasOrphanDelete() {
+			return true;
+		}
+
+		@Override
+		public String toString() {
+			return "STYLE_DELETE_ORPHAN";
+		}
+	};
+
+	/**
+	 * no cascades
+	 */
+	public static final CascadeStyle NONE = new BaseCascadeStyle() {
+		@Override
+		public boolean doCascade(CascadingAction action) {
+			return false;
+		}
+
+		@Override
+		public String toString() {
+			return "STYLE_NONE";
+		}
+	};
+
+	private static final Map<String, CascadeStyle> STYLES = buildBaseCascadeStyleMap();
+
+	private static Map<String, CascadeStyle> buildBaseCascadeStyleMap() {
+		final HashMap<String, CascadeStyle> base = new HashMap<String, CascadeStyle>();
+
+		base.put( "all", ALL );
+		base.put( "all-delete-orphan", ALL_DELETE_ORPHAN );
+		base.put( "save-update", UPDATE );
+		base.put( "persist", PERSIST );
+		base.put( "merge", MERGE );
+		base.put( "lock", LOCK );
+		base.put( "refresh", REFRESH );
+		base.put( "replicate", REPLICATE );
+		base.put( "evict", EVICT );
+		base.put( "delete", DELETE );
+		base.put( "remove", DELETE ); // adds remove as a sort-of alias for delete...
+		base.put( "delete-orphan", DELETE_ORPHAN );
+		base.put( "none", NONE );
+
+		return base;
+	}
+
+	/**
+	 * Factory method for obtaining named cascade styles
+	 *
+	 * @param cascade The named cascade style name.
+	 *
+	 * @return The appropriate CascadeStyle
+	 */
+	public static CascadeStyle getCascadeStyle(String cascade) {
+		CascadeStyle style = STYLES.get( cascade );
+		if ( style == null ) {
+			throw new MappingException( "Unsupported cascade style: " + cascade );
+		}
+		else {
+			return style;
+		}
+	}
+
+	public static void registerCascadeStyle(String name, BaseCascadeStyle cascadeStyle) {
+		log.tracef( "Registering external cascade style [%s : %s]", name, cascadeStyle );
+		final CascadeStyle old = STYLES.put( name, cascadeStyle );
+		if ( old != null ) {
+			log.debugf(
+					"External cascade style regsitration [%s : %s] overrode base registration [%s]",
+					name,
+					cascadeStyle,
+					old
+			);
+		}
+	}
+
+	public static abstract class BaseCascadeStyle implements CascadeStyle {
+		@Override
+		public boolean reallyDoCascade(CascadingAction action) {
+			return doCascade( action );
+		}
+
+		@Override
+		public boolean hasOrphanDelete() {
+			return false;
+		}
+	}
+
+	public static final class MultipleCascadeStyle extends BaseCascadeStyle {
+		private final CascadeStyle[] styles;
+
+		public MultipleCascadeStyle(CascadeStyle[] styles) {
+			this.styles = styles;
+		}
+
+		@Override
+		public boolean doCascade(CascadingAction action) {
+			for ( CascadeStyle style : styles ) {
+				if ( style.doCascade( action ) ) {
+					return true;
+				}
+			}
+			return false;
+		}
+
+		@Override
+		public boolean reallyDoCascade(CascadingAction action) {
+			for ( CascadeStyle style : styles ) {
+				if ( style.reallyDoCascade( action ) ) {
+					return true;
+				}
+			}
+			return false;
+		}
+
+		@Override
+		public boolean hasOrphanDelete() {
+			for ( CascadeStyle style : styles ) {
+				if ( style.hasOrphanDelete() ) {
+					return true;
+				}
+			}
+			return false;
+		}
+
+		@Override
+		public String toString() {
+			return ArrayHelper.toString( styles );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingAction.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingAction.java
index 5ed0c4012c..3a467f5691 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingAction.java
@@ -1,474 +1,104 @@
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
 package org.hibernate.engine.spi;
 
 import java.util.Iterator;
-import java.util.Map;
-import java.util.Set;
-
-import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
-import org.hibernate.LockMode;
-import org.hibernate.LockOptions;
-import org.hibernate.ReplicationMode;
-import org.hibernate.TransientPropertyValueException;
-import org.hibernate.collection.spi.PersistentCollection;
-import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.event.spi.EventSource;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.type.CollectionType;
-import org.hibernate.type.EntityType;
-import org.hibernate.type.Type;
 
 /**
  * A session action that may be cascaded from parent entity to its children
  *
  * @author Gavin King
+ * @author Steve Ebersole
  */
-public abstract class CascadingAction {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CascadingAction.class.getName());
-
-
-	// the CascadingAction contract ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public CascadingAction() {
-	}
+public interface CascadingAction {
 
 	/**
 	 * Cascade the action to the child object.
 	 *
 	 * @param session The session within which the cascade is occuring.
 	 * @param child The child to which cascading should be performed.
 	 * @param entityName The child's entity name
 	 * @param anything Anything ;)  Typically some form of cascade-local cache
 	 * which is specific to each CascadingAction type
 	 * @param isCascadeDeleteEnabled Are cascading deletes enabled.
 	 * @throws HibernateException
 	 */
-	public abstract void cascade(
+	public void cascade(
 			EventSource session,
 			Object child,
 			String entityName,
 			Object anything,
 			boolean isCascadeDeleteEnabled) throws HibernateException;
 
 	/**
 	 * Given a collection, get an iterator of the children upon which the
 	 * current cascading action should be visited.
 	 *
 	 * @param session The session within which the cascade is occuring.
 	 * @param collectionType The mapping type of the collection.
 	 * @param collection The collection instance.
 	 * @return The children iterator.
 	 */
-	public abstract Iterator getCascadableChildrenIterator(
+	public Iterator getCascadableChildrenIterator(
 			EventSource session,
 			CollectionType collectionType,
 			Object collection);
 
 	/**
 	 * Does this action potentially extrapolate to orphan deletes?
 	 *
 	 * @return True if this action can lead to deletions of orphans.
 	 */
-	public abstract boolean deleteOrphans();
+	public boolean deleteOrphans();
 
 
 	/**
 	 * Does the specified cascading action require verification of no cascade validity?
 	 *
 	 * @return True if this action requires no-cascade verification; false otherwise.
 	 */
-	public boolean requiresNoCascadeChecking() {
-		return false;
-	}
+	public boolean requiresNoCascadeChecking();
 
 	/**
 	 * Called (in the case of {@link #requiresNoCascadeChecking} returning true) to validate
 	 * that no cascade on the given property is considered a valid semantic.
 	 *
 	 * @param session The session witin which the cascade is occurring.
 	 * @param child The property value
 	 * @param parent The property value owner
 	 * @param persister The entity persister for the owner
 	 * @param propertyIndex The index of the property within the owner.
 	 */
-	public void noCascade(EventSource session, Object child, Object parent, EntityPersister persister, int propertyIndex) {
-	}
+	public void noCascade(EventSource session, Object child, Object parent, EntityPersister persister, int propertyIndex);
 
 	/**
 	 * Should this action be performed (or noCascade consulted) in the case of lazy properties.
 	 */
-	public boolean performOnLazyProperty() {
-		return true;
-	}
-
-
-	// the CascadingAction implementations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	/**
-	 * @see org.hibernate.Session#delete(Object)
-	 */
-	public static final CascadingAction DELETE = new CascadingAction() {
-		@Override
-        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
-		throws HibernateException {
-			LOG.tracev( "Cascading to delete: {0}", entityName );
-			session.delete( entityName, child, isCascadeDeleteEnabled, ( Set ) anything );
-		}
-		@Override
-        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
-			// delete does cascade to uninitialized collections
-			return CascadingAction.getAllElementsIterator(session, collectionType, collection);
-		}
-		@Override
-        public boolean deleteOrphans() {
-			// orphans should be deleted during delete
-			return true;
-		}
-		@Override
-        public String toString() {
-			return "ACTION_DELETE";
-		}
-	};
-
-	/**
-	 * @see org.hibernate.Session#lock(Object, LockMode)
-	 */
-	public static final CascadingAction LOCK = new CascadingAction() {
-		@Override
-        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
-		throws HibernateException {
-			LOG.tracev( "Cascading to lock: {0}", entityName );
-			LockMode lockMode = LockMode.NONE;
-			LockOptions lr = new LockOptions();
-			if ( anything instanceof LockOptions) {
-				LockOptions lockOptions = (LockOptions)anything;
-				lr.setTimeOut(lockOptions.getTimeOut());
-				lr.setScope( lockOptions.getScope());
-				if ( lockOptions.getScope() == true )	// cascade specified lockMode
-					lockMode = lockOptions.getLockMode();
-			}
-			lr.setLockMode(lockMode);
-			session.buildLockRequest(lr).lock(entityName, child);
-		}
-		@Override
-        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
-			// lock doesn't cascade to uninitialized collections
-			return getLoadedElementsIterator(session, collectionType, collection);
-		}
-		@Override
-        public boolean deleteOrphans() {
-			//TODO: should orphans really be deleted during lock???
-			return false;
-		}
-		@Override
-        public String toString() {
-			return "ACTION_LOCK";
-		}
-	};
-
-	/**
-	 * @see org.hibernate.Session#refresh(Object)
-	 */
-	public static final CascadingAction REFRESH = new CascadingAction() {
-		@Override
-        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
-		throws HibernateException {
-			LOG.tracev( "Cascading to refresh: {0}", entityName );
-			session.refresh( child, (Map) anything );
-		}
-		@Override
-        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
-			// refresh doesn't cascade to uninitialized collections
-			return getLoadedElementsIterator(session, collectionType, collection);
-		}
-		@Override
-        public boolean deleteOrphans() {
-			return false;
-		}
-		@Override
-        public String toString() {
-			return "ACTION_REFRESH";
-		}
-	};
-
-	/**
-	 * @see org.hibernate.Session#evict(Object)
-	 */
-	public static final CascadingAction EVICT = new CascadingAction() {
-		@Override
-        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
-		throws HibernateException {
-			LOG.tracev( "Cascading to evict: {0}", entityName );
-			session.evict(child);
-		}
-		@Override
-        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
-			// evicts don't cascade to uninitialized collections
-			return getLoadedElementsIterator(session, collectionType, collection);
-		}
-		@Override
-        public boolean deleteOrphans() {
-			return false;
-		}
-		@Override
-        public boolean performOnLazyProperty() {
-			return false;
-		}
-		@Override
-        public String toString() {
-			return "ACTION_EVICT";
-		}
-	};
-
-	/**
-	 * @see org.hibernate.Session#saveOrUpdate(Object)
-	 */
-	public static final CascadingAction SAVE_UPDATE = new CascadingAction() {
-		@Override
-        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
-		throws HibernateException {
-			LOG.tracev( "Cascading to save or update: {0}", entityName );
-			session.saveOrUpdate(entityName, child);
-		}
-		@Override
-        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
-			// saves / updates don't cascade to uninitialized collections
-			return getLoadedElementsIterator(session, collectionType, collection);
-		}
-		@Override
-        public boolean deleteOrphans() {
-			// orphans should be deleted during save/update
-			return true;
-		}
-		@Override
-        public boolean performOnLazyProperty() {
-			return false;
-		}
-		@Override
-        public String toString() {
-			return "ACTION_SAVE_UPDATE";
-		}
-	};
-
-	/**
-	 * @see org.hibernate.Session#merge(Object)
-	 */
-	public static final CascadingAction MERGE = new CascadingAction() {
-		@Override
-        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
-		throws HibernateException {
-			LOG.tracev( "Cascading to merge: {0}", entityName );
-			session.merge( entityName, child, (Map) anything );
-		}
-		@Override
-        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
-			// merges don't cascade to uninitialized collections
-//			//TODO: perhaps this does need to cascade after all....
-			return getLoadedElementsIterator(session, collectionType, collection);
-		}
-		@Override
-        public boolean deleteOrphans() {
-			// orphans should not be deleted during merge??
-			return false;
-		}
-		@Override
-        public String toString() {
-			return "ACTION_MERGE";
-		}
-	};
-
-	/**
-	 * @see org.hibernate.Session#persist(Object)
-	 */
-	public static final CascadingAction PERSIST = new CascadingAction() {
-		@Override
-        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
-		throws HibernateException {
-			LOG.tracev( "Cascading to persist: {0}" + entityName );
-			session.persist( entityName, child, (Map) anything );
-		}
-		@Override
-        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
-			// persists don't cascade to uninitialized collections
-			return CascadingAction.getAllElementsIterator(session, collectionType, collection);
-		}
-		@Override
-        public boolean deleteOrphans() {
-			return false;
-		}
-		@Override
-        public boolean performOnLazyProperty() {
-			return false;
-		}
-		@Override
-        public String toString() {
-			return "ACTION_PERSIST";
-		}
-	};
-
-	/**
-	 * Execute persist during flush time
-	 *
-	 * @see org.hibernate.Session#persist(Object)
-	 */
-	public static final CascadingAction PERSIST_ON_FLUSH = new CascadingAction() {
-		@Override
-        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
-		throws HibernateException {
-			LOG.tracev( "Cascading to persist on flush: {0}", entityName );
-			session.persistOnFlush( entityName, child, (Map) anything );
-		}
-		@Override
-        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
-			// persists don't cascade to uninitialized collections
-			return CascadingAction.getLoadedElementsIterator(session, collectionType, collection);
-		}
-		@Override
-        public boolean deleteOrphans() {
-			return true;
-		}
-		@Override
-        public boolean requiresNoCascadeChecking() {
-			return true;
-		}
-		@Override
-        public void noCascade(
-				EventSource session,
-				Object child,
-				Object parent,
-				EntityPersister persister,
-				int propertyIndex) {
-			if ( child == null ) {
-				return;
-			}
-			Type type = persister.getPropertyTypes()[propertyIndex];
-			if ( type.isEntityType() ) {
-				String childEntityName = ( ( EntityType ) type ).getAssociatedEntityName( session.getFactory() );
-
-				if ( ! isInManagedState( child, session )
-						&& ! ( child instanceof HibernateProxy ) //a proxy cannot be transient and it breaks ForeignKeys.isTransient
-						&& ForeignKeys.isTransient( childEntityName, child, null, session ) ) {
-					String parentEntiytName = persister.getEntityName();
-					String propertyName = persister.getPropertyNames()[propertyIndex];
-					throw new TransientPropertyValueException(
-							"object references an unsaved transient instance - save the transient instance before flushing",
-							childEntityName,
-							parentEntiytName,
-							propertyName
-					);
-
-				}
-			}
-		}
-		@Override
-        public boolean performOnLazyProperty() {
-			return false;
-		}
-
-		private boolean isInManagedState(Object child, EventSource session) {
-			EntityEntry entry = session.getPersistenceContext().getEntry( child );
-			return entry != null &&
-					(
-							entry.getStatus() == Status.MANAGED ||
-							entry.getStatus() == Status.READ_ONLY ||
-							entry.getStatus() == Status.SAVING
-					);
-		}
-
-		@Override
-        public String toString() {
-			return "ACTION_PERSIST_ON_FLUSH";
-		}
-	};
-
-	/**
-	 * @see org.hibernate.Session#replicate(Object, org.hibernate.ReplicationMode)
-	 */
-	public static final CascadingAction REPLICATE = new CascadingAction() {
-		@Override
-        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
-		throws HibernateException {
-			LOG.tracev( "Cascading to replicate: {0}", entityName );
-			session.replicate( entityName, child, (ReplicationMode) anything );
-		}
-		@Override
-        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
-			// replicate does cascade to uninitialized collections
-			return getLoadedElementsIterator(session, collectionType, collection);
-		}
-		@Override
-        public boolean deleteOrphans() {
-			return false; //I suppose?
-		}
-		@Override
-        public String toString() {
-			return "ACTION_REPLICATE";
-		}
-	};
-
-
-	// static helper methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	/**
-	 * Given a collection, get an iterator of all its children, loading them
-	 * from the database if necessary.
-	 *
-	 * @param session The session within which the cascade is occuring.
-	 * @param collectionType The mapping type of the collection.
-	 * @param collection The collection instance.
-	 * @return The children iterator.
-	 */
-	private static Iterator getAllElementsIterator(
-			EventSource session,
-			CollectionType collectionType,
-			Object collection) {
-		return collectionType.getElementsIterator( collection, session );
-	}
-
-	/**
-	 * Iterate just the elements of the collection that are already there. Don't load
-	 * any new elements from the database.
-	 */
-	public static Iterator getLoadedElementsIterator(SessionImplementor session, CollectionType collectionType, Object collection) {
-		if ( collectionIsInitialized(collection) ) {
-			// handles arrays and newly instantiated collections
-			return collectionType.getElementsIterator(collection, session);
-		}
-		else {
-			// does not handle arrays (thats ok, cos they can't be lazy)
-			// or newly instantiated collections, so we can do the cast
-			return ( (PersistentCollection) collection ).queuedAdditionIterator();
-		}
-	}
-
-	private static boolean collectionIsInitialized(Object collection) {
-		return !(collection instanceof PersistentCollection) || ( (PersistentCollection) collection ).wasInitialized();
-	}
+	public boolean performOnLazyProperty();
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingActions.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingActions.java
new file mode 100644
index 0000000000..a39014bb3d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingActions.java
@@ -0,0 +1,521 @@
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
+import java.util.Iterator;
+import java.util.Map;
+import java.util.Set;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.HibernateException;
+import org.hibernate.LockMode;
+import org.hibernate.LockOptions;
+import org.hibernate.ReplicationMode;
+import org.hibernate.TransientPropertyValueException;
+import org.hibernate.collection.spi.PersistentCollection;
+import org.hibernate.engine.internal.ForeignKeys;
+import org.hibernate.event.spi.EventSource;
+import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.proxy.HibernateProxy;
+import org.hibernate.type.CollectionType;
+import org.hibernate.type.EntityType;
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CascadingActions {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			CascadingAction.class.getName()
+	);
+
+	/**
+	 * Disallow instantiation
+	 */
+	private CascadingActions() {
+	}
+
+	/**
+	 * @see org.hibernate.Session#delete(Object)
+	 */
+	public static final CascadingAction DELETE = new BaseCascadingAction() {
+		@Override
+		public void cascade(
+				EventSource session,
+				Object child,
+				String entityName,
+				Object anything,
+				boolean isCascadeDeleteEnabled) {
+			LOG.tracev( "Cascading to delete: {0}", entityName );
+			session.delete( entityName, child, isCascadeDeleteEnabled, (Set) anything );
+		}
+
+		@Override
+		public Iterator getCascadableChildrenIterator(
+				EventSource session,
+				CollectionType collectionType,
+				Object collection) {
+			// delete does cascade to uninitialized collections
+			return getAllElementsIterator( session, collectionType, collection );
+		}
+
+		@Override
+		public boolean deleteOrphans() {
+			// orphans should be deleted during delete
+			return true;
+		}
+
+		@Override
+		public String toString() {
+			return "ACTION_DELETE";
+		}
+	};
+
+	/**
+	 * @see org.hibernate.Session#lock(Object, org.hibernate.LockMode)
+	 */
+	public static final CascadingAction LOCK = new BaseCascadingAction() {
+		@Override
+		public void cascade(
+				EventSource session,
+				Object child,
+				String entityName,
+				Object anything,
+				boolean isCascadeDeleteEnabled) {
+			LOG.tracev( "Cascading to lock: {0}", entityName );
+			LockMode lockMode = LockMode.NONE;
+			LockOptions lr = new LockOptions();
+			if ( anything instanceof LockOptions ) {
+				LockOptions lockOptions = (LockOptions) anything;
+				lr.setTimeOut( lockOptions.getTimeOut() );
+				lr.setScope( lockOptions.getScope() );
+				if ( lockOptions.getScope() ) {
+					lockMode = lockOptions.getLockMode();
+				}
+			}
+			lr.setLockMode( lockMode );
+			session.buildLockRequest( lr ).lock( entityName, child );
+		}
+
+		@Override
+		public Iterator getCascadableChildrenIterator(
+				EventSource session,
+				CollectionType collectionType,
+				Object collection) {
+			// lock doesn't cascade to uninitialized collections
+			return getLoadedElementsIterator( session, collectionType, collection );
+		}
+
+		@Override
+		public boolean deleteOrphans() {
+			//TODO: should orphans really be deleted during lock???
+			return false;
+		}
+
+		@Override
+		public String toString() {
+			return "ACTION_LOCK";
+		}
+	};
+
+	/**
+	 * @see org.hibernate.Session#refresh(Object)
+	 */
+	public static final CascadingAction REFRESH = new BaseCascadingAction() {
+		@Override
+		public void cascade(
+				EventSource session,
+				Object child,
+				String entityName,
+				Object anything,
+				boolean isCascadeDeleteEnabled)
+				throws HibernateException {
+			LOG.tracev( "Cascading to refresh: {0}", entityName );
+			session.refresh( child, (Map) anything );
+		}
+
+		@Override
+		public Iterator getCascadableChildrenIterator(
+				EventSource session,
+				CollectionType collectionType,
+				Object collection) {
+			// refresh doesn't cascade to uninitialized collections
+			return getLoadedElementsIterator( session, collectionType, collection );
+		}
+
+		@Override
+		public boolean deleteOrphans() {
+			return false;
+		}
+
+		@Override
+		public String toString() {
+			return "ACTION_REFRESH";
+		}
+	};
+
+	/**
+	 * @see org.hibernate.Session#evict(Object)
+	 */
+	public static final CascadingAction EVICT = new BaseCascadingAction() {
+		@Override
+		public void cascade(
+				EventSource session,
+				Object child,
+				String entityName,
+				Object anything,
+				boolean isCascadeDeleteEnabled)
+				throws HibernateException {
+			LOG.tracev( "Cascading to evict: {0}", entityName );
+			session.evict( child );
+		}
+
+		@Override
+		public Iterator getCascadableChildrenIterator(
+				EventSource session,
+				CollectionType collectionType,
+				Object collection) {
+			// evicts don't cascade to uninitialized collections
+			return getLoadedElementsIterator( session, collectionType, collection );
+		}
+
+		@Override
+		public boolean deleteOrphans() {
+			return false;
+		}
+
+		@Override
+		public boolean performOnLazyProperty() {
+			return false;
+		}
+
+		@Override
+		public String toString() {
+			return "ACTION_EVICT";
+		}
+	};
+
+	/**
+	 * @see org.hibernate.Session#saveOrUpdate(Object)
+	 */
+	public static final CascadingAction SAVE_UPDATE = new BaseCascadingAction() {
+		@Override
+		public void cascade(
+				EventSource session,
+				Object child,
+				String entityName,
+				Object anything,
+				boolean isCascadeDeleteEnabled)
+				throws HibernateException {
+			LOG.tracev( "Cascading to save or update: {0}", entityName );
+			session.saveOrUpdate( entityName, child );
+		}
+
+		@Override
+		public Iterator getCascadableChildrenIterator(
+				EventSource session,
+				CollectionType collectionType,
+				Object collection) {
+			// saves / updates don't cascade to uninitialized collections
+			return getLoadedElementsIterator( session, collectionType, collection );
+		}
+
+		@Override
+		public boolean deleteOrphans() {
+			// orphans should be deleted during save/update
+			return true;
+		}
+
+		@Override
+		public boolean performOnLazyProperty() {
+			return false;
+		}
+
+		@Override
+		public String toString() {
+			return "ACTION_SAVE_UPDATE";
+		}
+	};
+
+	/**
+	 * @see org.hibernate.Session#merge(Object)
+	 */
+	public static final CascadingAction MERGE = new BaseCascadingAction() {
+		@Override
+		public void cascade(
+				EventSource session,
+				Object child,
+				String entityName,
+				Object anything,
+				boolean isCascadeDeleteEnabled)
+				throws HibernateException {
+			LOG.tracev( "Cascading to merge: {0}", entityName );
+			session.merge( entityName, child, (Map) anything );
+		}
+
+		@Override
+		public Iterator getCascadableChildrenIterator(
+				EventSource session,
+				CollectionType collectionType,
+				Object collection) {
+			// merges don't cascade to uninitialized collections
+			return getLoadedElementsIterator( session, collectionType, collection );
+		}
+
+		@Override
+		public boolean deleteOrphans() {
+			// orphans should not be deleted during merge??
+			return false;
+		}
+
+		@Override
+		public String toString() {
+			return "ACTION_MERGE";
+		}
+	};
+
+	/**
+	 * @see org.hibernate.Session#persist(Object)
+	 */
+	public static final CascadingAction PERSIST = new BaseCascadingAction() {
+		@Override
+		public void cascade(
+				EventSource session,
+				Object child,
+				String entityName,
+				Object anything,
+				boolean isCascadeDeleteEnabled)
+				throws HibernateException {
+			LOG.tracev( "Cascading to persist: {0}" + entityName );
+			session.persist( entityName, child, (Map) anything );
+		}
+
+		@Override
+		public Iterator getCascadableChildrenIterator(
+				EventSource session,
+				CollectionType collectionType,
+				Object collection) {
+			// persists don't cascade to uninitialized collections
+			return getAllElementsIterator( session, collectionType, collection );
+		}
+
+		@Override
+		public boolean deleteOrphans() {
+			return false;
+		}
+
+		@Override
+		public boolean performOnLazyProperty() {
+			return false;
+		}
+
+		@Override
+		public String toString() {
+			return "ACTION_PERSIST";
+		}
+	};
+
+	/**
+	 * Execute persist during flush time
+	 *
+	 * @see org.hibernate.Session#persist(Object)
+	 */
+	public static final CascadingAction PERSIST_ON_FLUSH = new BaseCascadingAction() {
+		@Override
+		public void cascade(
+				EventSource session,
+				Object child,
+				String entityName,
+				Object anything,
+				boolean isCascadeDeleteEnabled)
+				throws HibernateException {
+			LOG.tracev( "Cascading to persist on flush: {0}", entityName );
+			session.persistOnFlush( entityName, child, (Map) anything );
+		}
+
+		@Override
+		public Iterator getCascadableChildrenIterator(
+				EventSource session,
+				CollectionType collectionType,
+				Object collection) {
+			// persists don't cascade to uninitialized collections
+			return getLoadedElementsIterator( session, collectionType, collection );
+		}
+
+		@Override
+		public boolean deleteOrphans() {
+			return true;
+		}
+
+		@Override
+		public boolean requiresNoCascadeChecking() {
+			return true;
+		}
+
+		@Override
+		public void noCascade(
+				EventSource session,
+				Object child,
+				Object parent,
+				EntityPersister persister,
+				int propertyIndex) {
+			if ( child == null ) {
+				return;
+			}
+			Type type = persister.getPropertyTypes()[propertyIndex];
+			if ( type.isEntityType() ) {
+				String childEntityName = ((EntityType) type).getAssociatedEntityName( session.getFactory() );
+
+				if ( !isInManagedState( child, session )
+						&& !(child instanceof HibernateProxy) //a proxy cannot be transient and it breaks ForeignKeys.isTransient
+						&& ForeignKeys.isTransient( childEntityName, child, null, session ) ) {
+					String parentEntiytName = persister.getEntityName();
+					String propertyName = persister.getPropertyNames()[propertyIndex];
+					throw new TransientPropertyValueException(
+							"object references an unsaved transient instance - save the transient instance before flushing",
+							childEntityName,
+							parentEntiytName,
+							propertyName
+					);
+
+				}
+			}
+		}
+
+		@Override
+		public boolean performOnLazyProperty() {
+			return false;
+		}
+
+		private boolean isInManagedState(Object child, EventSource session) {
+			EntityEntry entry = session.getPersistenceContext().getEntry( child );
+			return entry != null &&
+					(
+							entry.getStatus() == Status.MANAGED ||
+									entry.getStatus() == Status.READ_ONLY ||
+									entry.getStatus() == Status.SAVING
+					);
+		}
+
+		@Override
+		public String toString() {
+			return "ACTION_PERSIST_ON_FLUSH";
+		}
+	};
+
+	/**
+	 * @see org.hibernate.Session#replicate
+	 */
+	public static final CascadingAction REPLICATE = new BaseCascadingAction() {
+		@Override
+		public void cascade(
+				EventSource session,
+				Object child,
+				String entityName,
+				Object anything,
+				boolean isCascadeDeleteEnabled)
+				throws HibernateException {
+			LOG.tracev( "Cascading to replicate: {0}", entityName );
+			session.replicate( entityName, child, (ReplicationMode) anything );
+		}
+
+		@Override
+		public Iterator getCascadableChildrenIterator(
+				EventSource session,
+				CollectionType collectionType,
+				Object collection) {
+			// replicate does cascade to uninitialized collections
+			return getLoadedElementsIterator( session, collectionType, collection );
+		}
+
+		@Override
+		public boolean deleteOrphans() {
+			return false; //I suppose?
+		}
+
+		@Override
+		public String toString() {
+			return "ACTION_REPLICATE";
+		}
+	};
+
+	public abstract static class BaseCascadingAction implements CascadingAction {
+		@Override
+		public boolean requiresNoCascadeChecking() {
+			return false;
+		}
+
+		@Override
+		public void noCascade(EventSource session, Object child, Object parent, EntityPersister persister, int propertyIndex) {
+		}
+
+		@Override
+		public boolean performOnLazyProperty() {
+			return true;
+		}
+	}
+
+	/**
+	 * Given a collection, get an iterator of all its children, loading them
+	 * from the database if necessary.
+	 *
+	 * @param session The session within which the cascade is occuring.
+	 * @param collectionType The mapping type of the collection.
+	 * @param collection The collection instance.
+	 *
+	 * @return The children iterator.
+	 */
+	private static Iterator getAllElementsIterator(
+			EventSource session,
+			CollectionType collectionType,
+			Object collection) {
+		return collectionType.getElementsIterator( collection, session );
+	}
+
+	/**
+	 * Iterate just the elements of the collection that are already there. Don't load
+	 * any new elements from the database.
+	 */
+	public static Iterator getLoadedElementsIterator(
+			SessionImplementor session,
+			CollectionType collectionType,
+			Object collection) {
+		if ( collectionIsInitialized( collection ) ) {
+			// handles arrays and newly instantiated collections
+			return collectionType.getElementsIterator( collection, session );
+		}
+		else {
+			// does not handle arrays (thats ok, cos they can't be lazy)
+			// or newly instantiated collections, so we can do the cast
+			return ((PersistentCollection) collection).queuedAdditionIterator();
+		}
+	}
+
+	private static boolean collectionIsInitialized(Object collection) {
+		return !(collection instanceof PersistentCollection) || ((PersistentCollection) collection).wasInitialized();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
index cd4155f5b7..b64cd9a7ae 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
@@ -1,376 +1,377 @@
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
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.action.internal.CollectionRecreateAction;
 import org.hibernate.action.internal.CollectionRemoveAction;
 import org.hibernate.action.internal.CollectionUpdateAction;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.internal.Collections;
 import org.hibernate.engine.spi.ActionQueue;
 import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.FlushEntityEvent;
 import org.hibernate.event.spi.FlushEntityEventListener;
 import org.hibernate.event.spi.FlushEvent;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.EntityPrinter;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.internal.util.collections.LazyIterator;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * A convenience base class for listeners whose functionality results in flushing.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractFlushingEventListener implements Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, AbstractFlushingEventListener.class.getName() );
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Pre-flushing section
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Coordinates the processing necessary to get things ready for executions
 	 * as db calls by preping the session caches and moving the appropriate
 	 * entities and collections to their respective execution queues.
 	 *
 	 * @param event The flush event.
 	 * @throws HibernateException Error flushing caches to execution queues.
 	 */
 	protected void flushEverythingToExecutions(FlushEvent event) throws HibernateException {
 
 		LOG.trace( "Flushing session" );
 
 		EventSource session = event.getSession();
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		session.getInterceptor().preFlush( new LazyIterator( persistenceContext.getEntitiesByKey() ) );
 
 		prepareEntityFlushes( session, persistenceContext );
 		// we could move this inside if we wanted to
 		// tolerate collection initializations during
 		// collection dirty checking:
 		prepareCollectionFlushes( persistenceContext );
 		// now, any collections that are initialized
 		// inside this block do not get updated - they
 		// are ignored until the next flush
 
 		persistenceContext.setFlushing(true);
 		try {
 			flushEntities( event, persistenceContext );
 			flushCollections( session, persistenceContext );
 		}
 		finally {
 			persistenceContext.setFlushing(false);
 		}
 
 		//some statistics
 		logFlushResults( event );
 	}
 
 	@SuppressWarnings( value = {"unchecked"} )
 	private void logFlushResults(FlushEvent event) {
 		if ( !LOG.isDebugEnabled() ) {
 			return;
 		}
 		final EventSource session = event.getSession();
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		LOG.debugf(
 				"Flushed: %s insertions, %s updates, %s deletions to %s objects",
 				session.getActionQueue().numberOfInsertions(),
 				session.getActionQueue().numberOfUpdates(),
 				session.getActionQueue().numberOfDeletions(),
 				persistenceContext.getEntityEntries().size()
 		);
 		LOG.debugf(
 				"Flushed: %s (re)creations, %s updates, %s removals to %s collections",
 				session.getActionQueue().numberOfCollectionCreations(),
 				session.getActionQueue().numberOfCollectionUpdates(),
 				session.getActionQueue().numberOfCollectionRemovals(),
 				persistenceContext.getCollectionEntries().size()
 		);
 		new EntityPrinter( session.getFactory() ).toString(
 				persistenceContext.getEntitiesByKey().entrySet()
 		);
 	}
 
 	/**
 	 * process cascade save/update at the start of a flush to discover
 	 * any newly referenced entity that must be passed to saveOrUpdate(),
 	 * and also apply orphan delete
 	 */
 	private void prepareEntityFlushes(EventSource session, PersistenceContext persistenceContext) throws HibernateException {
 
 		LOG.debug( "Processing flush-time cascades" );
 
 		final Object anything = getAnything();
 		//safe from concurrent modification because of how concurrentEntries() is implemented on IdentityMap
 		for ( Map.Entry me : IdentityMap.concurrentEntries( persistenceContext.getEntityEntries() ) ) {
 			EntityEntry entry = (EntityEntry) me.getValue();
 			Status status = entry.getStatus();
 			if ( status == Status.MANAGED || status == Status.SAVING || status == Status.READ_ONLY ) {
 				cascadeOnFlush( session, entry.getPersister(), me.getKey(), anything );
 			}
 		}
 	}
 
 	private void cascadeOnFlush(EventSource session, EntityPersister persister, Object object, Object anything)
 	throws HibernateException {
 		session.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadingAction(), Cascade.BEFORE_FLUSH, session )
 			.cascade( persister, object, anything );
 		}
 		finally {
 			session.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	protected Object getAnything() { return null; }
 
 	protected CascadingAction getCascadingAction() {
-		return CascadingAction.SAVE_UPDATE;
+		return CascadingActions.SAVE_UPDATE;
 	}
 
 	/**
 	 * Initialize the flags of the CollectionEntry, including the
 	 * dirty check.
 	 */
 	private void prepareCollectionFlushes(PersistenceContext persistenceContext) throws HibernateException {
 
 		// Initialize dirty flags for arrays + collections with composite elements
 		// and reset reached, doupdate, etc.
 
 		LOG.debug( "Dirty checking collections" );
 
 		for ( Map.Entry<PersistentCollection,CollectionEntry> entry :
 				IdentityMap.concurrentEntries( (Map<PersistentCollection,CollectionEntry>) persistenceContext.getCollectionEntries() )) {
 			entry.getValue().preFlush( entry.getKey() );
 		}
 	}
 
 	/**
 	 * 1. detect any dirty entities
 	 * 2. schedule any entity updates
 	 * 3. search out any reachable collections
 	 */
 	private void flushEntities(final FlushEvent event, final PersistenceContext persistenceContext) throws HibernateException {
 
 		LOG.trace( "Flushing entities and processing referenced collections" );
 
 		final EventSource source = event.getSession();
 		final Iterable<FlushEntityEventListener> flushListeners = source
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( EventType.FLUSH_ENTITY )
 				.listeners();
 
 		// Among other things, updateReachables() will recursively load all
 		// collections that are moving roles. This might cause entities to
 		// be loaded.
 
 		// So this needs to be safe from concurrent modification problems.
 		// It is safe because of how IdentityMap implements entrySet()
 
 		for ( Map.Entry me : IdentityMap.concurrentEntries( persistenceContext.getEntityEntries() ) ) {
 
 			// Update the status of the object and if necessary, schedule an update
 
 			EntityEntry entry = (EntityEntry) me.getValue();
 			Status status = entry.getStatus();
 
 			if ( status != Status.LOADING && status != Status.GONE ) {
 				final FlushEntityEvent entityEvent = new FlushEntityEvent( source, me.getKey(), entry );
 				for ( FlushEntityEventListener listener : flushListeners ) {
 					listener.onFlushEntity( entityEvent );
 				}
 			}
 		}
 
 		source.getActionQueue().sortActions();
 	}
 
 	/**
 	 * process any unreferenced collections and then inspect all known collections,
 	 * scheduling creates/removes/updates
 	 */
 	private void flushCollections(final EventSource session, final PersistenceContext persistenceContext) throws HibernateException {
 
 		LOG.trace( "Processing unreferenced collections" );
 
 		for ( Map.Entry<PersistentCollection,CollectionEntry> me :
 				IdentityMap.concurrentEntries( (Map<PersistentCollection,CollectionEntry>) persistenceContext.getCollectionEntries() )) {
 			CollectionEntry ce = me.getValue();
 			if ( !ce.isReached() && !ce.isIgnore() ) {
 				Collections.processUnreachableCollection( me.getKey(), session );
 			}
 		}
 
 		// Schedule updates to collections:
 
 		LOG.trace( "Scheduling collection removes/(re)creates/updates" );
 
 		ActionQueue actionQueue = session.getActionQueue();
 		for ( Map.Entry<PersistentCollection,CollectionEntry> me :
 			IdentityMap.concurrentEntries( (Map<PersistentCollection,CollectionEntry>) persistenceContext.getCollectionEntries() )) {
 			PersistentCollection coll = me.getKey();
 			CollectionEntry ce = me.getValue();
 
 			if ( ce.isDorecreate() ) {
 				session.getInterceptor().onCollectionRecreate( coll, ce.getCurrentKey() );
 				actionQueue.addAction(
 						new CollectionRecreateAction(
 								coll,
 								ce.getCurrentPersister(),
 								ce.getCurrentKey(),
 								session
 							)
 					);
 			}
 			if ( ce.isDoremove() ) {
 				session.getInterceptor().onCollectionRemove( coll, ce.getLoadedKey() );
 				actionQueue.addAction(
 						new CollectionRemoveAction(
 								coll,
 								ce.getLoadedPersister(),
 								ce.getLoadedKey(),
 								ce.isSnapshotEmpty(coll),
 								session
 							)
 					);
 			}
 			if ( ce.isDoupdate() ) {
 				session.getInterceptor().onCollectionUpdate( coll, ce.getLoadedKey() );
 				actionQueue.addAction(
 						new CollectionUpdateAction(
 								coll,
 								ce.getLoadedPersister(),
 								ce.getLoadedKey(),
 								ce.isSnapshotEmpty(coll),
 								session
 							)
 					);
 			}
 
 		}
 
 		actionQueue.sortCollectionActions();
 
 	}
 
 	/**
 	 * Execute all SQL (and second-level cache updates) in a special order so that foreign-key constraints cannot
 	 * be violated: <ol>
 	 * <li> Inserts, in the order they were performed
 	 * <li> Updates
 	 * <li> Deletion of collection elements
 	 * <li> Insertion of collection elements
 	 * <li> Deletes, in the order they were performed
 	 * </ol>
 	 *
 	 * @param session The session being flushed
 	 */
 	protected void performExecutions(EventSource session) {
 		LOG.trace( "Executing flush" );
 
 		// IMPL NOTE : here we alter the flushing flag of the persistence context to allow
 		//		during-flush callbacks more leniency in regards to initializing proxies and
 		//		lazy collections during their processing.
 		// For more information, see HHH-2763
 		try {
 			session.getTransactionCoordinator().getJdbcCoordinator().flushBeginning();
 			session.getPersistenceContext().setFlushing( true );
 			// we need to lock the collection caches before executing entity inserts/updates in order to
 			// account for bi-directional associations
 			session.getActionQueue().prepareActions();
 			session.getActionQueue().executeActions();
 		}
 		finally {
 			session.getPersistenceContext().setFlushing( false );
 			session.getTransactionCoordinator().getJdbcCoordinator().flushEnding();
 		}
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Post-flushing section
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * 1. Recreate the collection key -> collection map
 	 * 2. rebuild the collection entries
 	 * 3. call Interceptor.postFlush()
 	 */
 	protected void postFlush(SessionImplementor session) throws HibernateException {
 
 		LOG.trace( "Post flush" );
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		persistenceContext.getCollectionsByKey().clear();
 		persistenceContext.getBatchFetchQueue()
 				.clearSubselects(); //the database has changed now, so the subselect results need to be invalidated
 
 		for ( Map.Entry<PersistentCollection, CollectionEntry> me : IdentityMap.concurrentEntries( persistenceContext.getCollectionEntries() ) ) {
 			CollectionEntry collectionEntry = me.getValue();
 			PersistentCollection persistentCollection = me.getKey();
 			collectionEntry.postFlush(persistentCollection);
 			if ( collectionEntry.getLoadedPersister() == null ) {
 				//if the collection is dereferenced, remove from the session cache
 				//iter.remove(); //does not work, since the entrySet is not backed by the set
 				persistenceContext.getCollectionEntries()
 						.remove(persistentCollection);
 			}
 			else {
 				//otherwise recreate the mapping between the collection and its key
 				CollectionKey collectionKey = new CollectionKey(
 						collectionEntry.getLoadedPersister(),
 						collectionEntry.getLoadedKey()
 				);
 				persistenceContext.getCollectionsByKey().put(collectionKey, persistentCollection);
 			}
 		}
 
 		session.getInterceptor().postFlush( new LazyIterator( persistenceContext.getEntitiesByKey() ) );
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDeleteEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDeleteEventListener.java
index 491b0820e0..e993d6b1cf 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDeleteEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDeleteEventListener.java
@@ -1,352 +1,352 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.CacheMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.TransientObjectException;
 import org.hibernate.action.internal.EntityDeleteAction;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.internal.Nullability;
-import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.DeleteEvent;
 import org.hibernate.event.spi.DeleteEventListener;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
  * Defines the default delete event listener used by hibernate for deleting entities
  * from the datastore in response to generated delete events.
  *
  * @author Steve Ebersole
  */
 public class DefaultDeleteEventListener implements DeleteEventListener {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultDeleteEventListener.class.getName());
 
 	/**
 	 * Handle the given delete event.
 	 *
 	 * @param event The delete event to be handled.
 	 *
 	 * @throws HibernateException
 	 */
 	public void onDelete(DeleteEvent event) throws HibernateException {
 		onDelete( event, new IdentitySet() );
 	}
 
 	/**
 	 * Handle the given delete event.  This is the cascaded form.
 	 *
 	 * @param event The delete event.
 	 * @param transientEntities The cache of entities already deleted
 	 *
 	 * @throws HibernateException
 	 */
 	public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
 
 		final EventSource source = event.getSession();
 
 		final PersistenceContext persistenceContext = source.getPersistenceContext();
 		Object entity = persistenceContext.unproxyAndReassociate( event.getObject() );
 
 		EntityEntry entityEntry = persistenceContext.getEntry( entity );
 		final EntityPersister persister;
 		final Serializable id;
 		final Object version;
 
 		if ( entityEntry == null ) {
 			LOG.trace( "Entity was not persistent in delete processing" );
 
 			persister = source.getEntityPersister( event.getEntityName(), entity );
 
 			if ( ForeignKeys.isTransient( persister.getEntityName(), entity, null, source ) ) {
 				deleteTransientEntity( source, entity, event.isCascadeDeleteEnabled(), persister, transientEntities );
 				// EARLY EXIT!!!
 				return;
 			}
 			performDetachedEntityDeletionCheck( event );
 
 			id = persister.getIdentifier( entity, source );
 
 			if ( id == null ) {
 				throw new TransientObjectException(
 						"the detached instance passed to delete() had a null identifier"
 				);
 			}
 
 			final EntityKey key = source.generateEntityKey( id, persister );
 
 			persistenceContext.checkUniqueness( key, entity );
 
 			new OnUpdateVisitor( source, id, entity ).process( entity, persister );
 
 			version = persister.getVersion( entity );
 
 			entityEntry = persistenceContext.addEntity(
 					entity,
 					( persister.isMutable() ? Status.MANAGED : Status.READ_ONLY ),
 					persister.getPropertyValues( entity ),
 					key,
 					version,
 					LockMode.NONE,
 					true,
 					persister,
 					false,
 					false
 			);
 		}
 		else {
 			LOG.trace( "Deleting a persistent instance" );
 
 			if ( entityEntry.getStatus() == Status.DELETED || entityEntry.getStatus() == Status.GONE ) {
 				LOG.trace( "Object was already deleted" );
 				return;
 			}
 			persister = entityEntry.getPersister();
 			id = entityEntry.getId();
 			version = entityEntry.getVersion();
 		}
 
 		/*if ( !persister.isMutable() ) {
 			throw new HibernateException(
 					"attempted to delete an object of immutable class: " +
 					MessageHelper.infoString(persister)
 				);
 		}*/
 
 		if ( invokeDeleteLifecycle( source, entity, persister ) ) {
 			return;
 		}
 
 		deleteEntity( source, entity, entityEntry, event.isCascadeDeleteEnabled(), persister, transientEntities );
 
 		if ( source.getFactory().getSettings().isIdentifierRollbackEnabled() ) {
 			persister.resetIdentifier( entity, id, version, source );
 		}
 	}
 
 	/**
 	 * Called when we have recognized an attempt to delete a detached entity.
 	 * <p/>
 	 * This is perfectly valid in Hibernate usage; JPA, however, forbids this.
 	 * Thus, this is a hook for HEM to affect this behavior.
 	 *
 	 * @param event The event.
 	 */
 	protected void performDetachedEntityDeletionCheck(DeleteEvent event) {
 		// ok in normal Hibernate usage to delete a detached entity; JPA however
 		// forbids it, thus this is a hook for HEM to affect this behavior
 	}
 
 	/**
 	 * We encountered a delete request on a transient instance.
 	 * <p/>
 	 * This is a deviation from historical Hibernate (pre-3.2) behavior to
 	 * align with the JPA spec, which states that transient entities can be
 	 * passed to remove operation in which case cascades still need to be
 	 * performed.
 	 *
 	 * @param session The session which is the source of the event
 	 * @param entity The entity being delete processed
 	 * @param cascadeDeleteEnabled Is cascading of deletes enabled
 	 * @param persister The entity persister
 	 * @param transientEntities A cache of already visited transient entities
 	 * (to avoid infinite recursion).
 	 */
 	protected void deleteTransientEntity(
 			EventSource session,
 			Object entity,
 			boolean cascadeDeleteEnabled,
 			EntityPersister persister,
 			Set transientEntities) {
 		LOG.handlingTransientEntity();
 		if ( transientEntities.contains( entity ) ) {
 			LOG.trace( "Already handled transient entity; skipping" );
 			return;
 		}
 		transientEntities.add( entity );
 		cascadeBeforeDelete( session, persister, entity, null, transientEntities );
 		cascadeAfterDelete( session, persister, entity, transientEntities );
 	}
 
 	/**
 	 * Perform the entity deletion.  Well, as with most operations, does not
 	 * really perform it; just schedules an action/execution with the
 	 * {@link org.hibernate.engine.spi.ActionQueue} for execution during flush.
 	 *
 	 * @param session The originating session
 	 * @param entity The entity to delete
 	 * @param entityEntry The entity's entry in the {@link PersistenceContext}
 	 * @param isCascadeDeleteEnabled Is delete cascading enabled?
 	 * @param persister The entity persister.
 	 * @param transientEntities A cache of already deleted entities.
 	 */
 	protected final void deleteEntity(
 			final EventSource session,
 			final Object entity,
 			final EntityEntry entityEntry,
 			final boolean isCascadeDeleteEnabled,
 			final EntityPersister persister,
 			final Set transientEntities) {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Deleting {0}", MessageHelper.infoString( persister, entityEntry.getId(), session.getFactory() ) );
 		}
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final Type[] propTypes = persister.getPropertyTypes();
 		final Object version = entityEntry.getVersion();
 
 		final Object[] currentState;
 		if ( entityEntry.getLoadedState() == null ) { //ie. the entity came in from update()
 			currentState = persister.getPropertyValues( entity );
 		}
 		else {
 			currentState = entityEntry.getLoadedState();
 		}
 
 		final Object[] deletedState = createDeletedState( persister, currentState, session );
 		entityEntry.setDeletedState( deletedState );
 
 		session.getInterceptor().onDelete(
 				entity,
 				entityEntry.getId(),
 				deletedState,
 				persister.getPropertyNames(),
 				propTypes
 		);
 
 		// before any callbacks, etc, so subdeletions see that this deletion happened first
 		persistenceContext.setEntryStatus( entityEntry, Status.DELETED );
 		final EntityKey key = session.generateEntityKey( entityEntry.getId(), persister );
 
 		cascadeBeforeDelete( session, persister, entity, entityEntry, transientEntities );
 
 		new ForeignKeys.Nullifier( entity, true, false, session )
 				.nullifyTransientReferences( entityEntry.getDeletedState(), propTypes );
 		new Nullability( session ).checkNullability( entityEntry.getDeletedState(), persister, true );
 		persistenceContext.getNullifiableEntityKeys().add( key );
 
 		// Ensures that containing deletions happen before sub-deletions
 		session.getActionQueue().addAction(
 				new EntityDeleteAction(
 						entityEntry.getId(),
 						deletedState,
 						version,
 						entity,
 						persister,
 						isCascadeDeleteEnabled,
 						session
 				)
 		);
 
 		cascadeAfterDelete( session, persister, entity, transientEntities );
 
 		// the entry will be removed after the flush, and will no longer
 		// override the stale snapshot
 		// This is now handled by removeEntity() in EntityDeleteAction
 		//persistenceContext.removeDatabaseSnapshot(key);
 	}
 
 	private Object[] createDeletedState(EntityPersister persister, Object[] currentState, EventSource session) {
 		Type[] propTypes = persister.getPropertyTypes();
 		final Object[] deletedState = new Object[propTypes.length];
 //		TypeFactory.deepCopy( currentState, propTypes, persister.getPropertyUpdateability(), deletedState, session );
 		boolean[] copyability = new boolean[propTypes.length];
 		java.util.Arrays.fill( copyability, true );
 		TypeHelper.deepCopy( currentState, propTypes, copyability, deletedState, session );
 		return deletedState;
 	}
 
 	protected boolean invokeDeleteLifecycle(EventSource session, Object entity, EntityPersister persister) {
 		if ( persister.implementsLifecycle() ) {
 			LOG.debug( "Calling onDelete()" );
 			if ( ( ( Lifecycle ) entity ).onDelete( session ) ) {
 				LOG.debug( "Deletion vetoed by onDelete()" );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	protected void cascadeBeforeDelete(
 			EventSource session,
 			EntityPersister persister,
 			Object entity,
 			EntityEntry entityEntry,
 			Set transientEntities) throws HibernateException {
 
 		CacheMode cacheMode = session.getCacheMode();
 		session.setCacheMode( CacheMode.GET );
 		session.getPersistenceContext().incrementCascadeLevel();
 		try {
 			// cascade-delete to collections BEFORE the collection owner is deleted
-			new Cascade( CascadingAction.DELETE, Cascade.AFTER_INSERT_BEFORE_DELETE, session )
+			new Cascade( CascadingActions.DELETE, Cascade.AFTER_INSERT_BEFORE_DELETE, session )
 					.cascade( persister, entity, transientEntities );
 		}
 		finally {
 			session.getPersistenceContext().decrementCascadeLevel();
 			session.setCacheMode( cacheMode );
 		}
 	}
 
 	protected void cascadeAfterDelete(
 			EventSource session,
 			EntityPersister persister,
 			Object entity,
 			Set transientEntities) throws HibernateException {
 
 		CacheMode cacheMode = session.getCacheMode();
 		session.setCacheMode( CacheMode.GET );
 		session.getPersistenceContext().incrementCascadeLevel();
 		try {
 			// cascade-delete to many-to-one AFTER the parent was deleted
-			new Cascade( CascadingAction.DELETE, Cascade.BEFORE_INSERT_AFTER_DELETE, session )
+			new Cascade( CascadingActions.DELETE, Cascade.BEFORE_INSERT_AFTER_DELETE, session )
 					.cascade( persister, entity, transientEntities );
 		}
 		finally {
 			session.getPersistenceContext().decrementCascadeLevel();
 			session.setCacheMode( cacheMode );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultEvictEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultEvictEventListener.java
index d6970ad9b3..8bf320502b 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultEvictEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultEvictEventListener.java
@@ -1,123 +1,123 @@
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.internal.Cascade;
-import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EvictEvent;
 import org.hibernate.event.spi.EvictEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * Defines the default evict event listener used by hibernate for evicting entities
  * in response to generated flush events.  In particular, this implementation will
  * remove any hard references to the entity that are held by the infrastructure
  * (references held by application or other persistent instances are okay)
  *
  * @author Steve Ebersole
  */
 public class DefaultEvictEventListener implements EvictEventListener {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultEvictEventListener.class.getName());
 
 	/**
 	 * Handle the given evict event.
 	 *
 	 * @param event The evict event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onEvict(EvictEvent event) throws HibernateException {
 		EventSource source = event.getSession();
 		final Object object = event.getObject();
 		final PersistenceContext persistenceContext = source.getPersistenceContext();
 
 		if ( object instanceof HibernateProxy ) {
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			Serializable id = li.getIdentifier();
 			EntityPersister persister = source.getFactory().getEntityPersister( li.getEntityName() );
 			if ( id == null ) {
 				throw new IllegalArgumentException("null identifier");
 			}
 
 			final EntityKey key = source.generateEntityKey( id, persister );
 			persistenceContext.removeProxy( key );
 
 			if ( !li.isUninitialized() ) {
 				final Object entity = persistenceContext.removeEntity( key );
 				if ( entity != null ) {
 					EntityEntry e = event.getSession().getPersistenceContext().removeEntry( entity );
 					doEvict( entity, key, e.getPersister(), event.getSession() );
 				}
 			}
 			li.unsetSession();
 		}
 		else {
 			EntityEntry e = persistenceContext.removeEntry( object );
 			if ( e != null ) {
 				persistenceContext.removeEntity( e.getEntityKey() );
 				doEvict( object, e.getEntityKey(), e.getPersister(), source );
 			}
 		}
 	}
 
 	protected void doEvict(
 		final Object object,
 		final EntityKey key,
 		final EntityPersister persister,
 		final EventSource session)
 	throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Evicting {0}", MessageHelper.infoString( persister ) );
 		}
 
 		// remove all collections for the entity from the session-level cache
 		if ( persister.hasCollections() ) {
 			new EvictVisitor( session ).process( object, persister );
 		}
 
 		// remove any snapshot, not really for memory management purposes, but
 		// rather because it might now be stale, and there is no longer any
 		// EntityEntry to take precedence
 		// This is now handled by removeEntity()
 		//session.getPersistenceContext().removeDatabaseSnapshot(key);
 
-		new Cascade( CascadingAction.EVICT, Cascade.AFTER_EVICT, session )
+		new Cascade( CascadingActions.EVICT, Cascade.AFTER_EVICT, session )
 				.cascade( persister, object );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLockEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLockEventListener.java
index 5e42c38973..3c2ca3e280 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLockEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLockEventListener.java
@@ -1,100 +1,100 @@
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
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.TransientObjectException;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.internal.ForeignKeys;
-import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.LockEvent;
 import org.hibernate.event.spi.LockEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Defines the default lock event listeners used by hibernate to lock entities
  * in response to generated lock events.
  *
  * @author Steve Ebersole
  */
 public class DefaultLockEventListener extends AbstractLockUpgradeEventListener implements LockEventListener {
 
 	/** Handle the given lock event.
 	 *
 	 * @param event The lock event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onLock(LockEvent event) throws HibernateException {
 
 		if ( event.getObject() == null ) {
 			throw new NullPointerException( "attempted to lock null" );
 		}
 
 		if ( event.getLockMode() == LockMode.WRITE ) {
 			throw new HibernateException( "Invalid lock mode for lock()" );
 		}
 
 		SessionImplementor source = event.getSession();
 		
 		Object entity = source.getPersistenceContext().unproxyAndReassociate( event.getObject() );
 		//TODO: if object was an uninitialized proxy, this is inefficient,
 		//      resulting in two SQL selects
 		
 		EntityEntry entry = source.getPersistenceContext().getEntry(entity);
 		if (entry==null) {
 			final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 			final Serializable id = persister.getIdentifier( entity, source );
 			if ( !ForeignKeys.isNotTransient( event.getEntityName(), entity, Boolean.FALSE, source ) ) {
 				throw new TransientObjectException(
 						"cannot lock an unsaved transient instance: " +
 						persister.getEntityName()
 				);
 			}
 
 			entry = reassociate(event, entity, id, persister);
 			cascadeOnLock(event, persister, entity);
 		}
 
 		upgradeLock( entity, entry, event.getLockOptions(), event.getSession() );
 	}
 	
 	private void cascadeOnLock(LockEvent event, EntityPersister persister, Object entity) {
 		EventSource source = event.getSession();
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
-			new Cascade(CascadingAction.LOCK, Cascade.AFTER_LOCK, source)
+			new Cascade( CascadingActions.LOCK, Cascade.AFTER_LOCK, source)
 					.cascade( persister, entity, event.getLockOptions() );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
index e9ee3a13fe..5d00d6afad 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
@@ -1,473 +1,474 @@
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
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.MergeEvent;
 import org.hibernate.event.spi.MergeEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.TypeHelper;
 
 /**
  * Defines the default copy event listener used by hibernate for copying entities
  * in response to generated copy events.
  *
  * @author Gavin King
  */
 public class DefaultMergeEventListener extends AbstractSaveEventListener implements MergeEventListener {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultMergeEventListener.class.getName());
 
 	@Override
     protected Map getMergeMap(Object anything) {
 		return ( ( EventCache ) anything ).invertMap();
 	}
 
 	/**
 	 * Handle the given merge event.
 	 *
 	 * @param event The merge event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onMerge(MergeEvent event) throws HibernateException {
 		EventCache copyCache = new EventCache();
 		onMerge( event, copyCache );
 		copyCache.clear();
 		copyCache = null;
 	}
 
 	/**
 	 * Handle the given merge event.
 	 *
 	 * @param event The merge event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onMerge(MergeEvent event, Map copiedAlready) throws HibernateException {
 
 		final EventCache copyCache = ( EventCache ) copiedAlready;
 		final EventSource source = event.getSession();
 		final Object original = event.getOriginal();
 
 		if ( original != null ) {
 
 			final Object entity;
 			if ( original instanceof HibernateProxy ) {
 				LazyInitializer li = ( (HibernateProxy) original ).getHibernateLazyInitializer();
 				if ( li.isUninitialized() ) {
 					LOG.trace( "Ignoring uninitialized proxy" );
 					event.setResult( source.load( li.getEntityName(), li.getIdentifier() ) );
 					return; //EARLY EXIT!
 				}
 				else {
 					entity = li.getImplementation();
 				}
 			}
 			else {
 				entity = original;
 			}
 
 			if ( copyCache.containsKey( entity ) &&
 					( copyCache.isOperatedOn( entity ) ) ) {
 				LOG.trace( "Already in merge process" );
 				event.setResult( entity );
 			}
 			else {
 				if ( copyCache.containsKey( entity ) ) {
 					LOG.trace( "Already in copyCache; setting in merge process" );
 					copyCache.setOperatedOn( entity, true );
 				}
 				event.setEntity( entity );
 				EntityState entityState = null;
 
 				// Check the persistence context for an entry relating to this
 				// entity to be merged...
 				EntityEntry entry = source.getPersistenceContext().getEntry( entity );
 				if ( entry == null ) {
 					EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 					Serializable id = persister.getIdentifier( entity, source );
 					if ( id != null ) {
 						final EntityKey key = source.generateEntityKey( id, persister );
 						final Object managedEntity = source.getPersistenceContext().getEntity( key );
 						entry = source.getPersistenceContext().getEntry( managedEntity );
 						if ( entry != null ) {
 							// we have specialized case of a detached entity from the
 							// perspective of the merge operation.  Specifically, we
 							// have an incoming entity instance which has a corresponding
 							// entry in the current persistence context, but registered
 							// under a different entity instance
 							entityState = EntityState.DETACHED;
 						}
 					}
 				}
 
 				if ( entityState == null ) {
 					entityState = getEntityState( entity, event.getEntityName(), entry, source );
 				}
 
 				switch (entityState) {
 					case DETACHED:
 						entityIsDetached(event, copyCache);
 						break;
 					case TRANSIENT:
 						entityIsTransient(event, copyCache);
 						break;
 					case PERSISTENT:
 						entityIsPersistent(event, copyCache);
 						break;
 					default: //DELETED
 						throw new ObjectDeletedException(
 								"deleted instance passed to merge",
 								null,
 								getLoggableName( event.getEntityName(), entity )
 							);
 				}
 			}
 
 		}
 
 	}
 
 	protected void entityIsPersistent(MergeEvent event, Map copyCache) {
 		LOG.trace( "Ignoring persistent instance" );
 
 		//TODO: check that entry.getIdentifier().equals(requestedId)
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 
 		( ( EventCache ) copyCache ).put( entity, entity, true  );  //before cascade!
 
 		cascadeOnMerge(source, persister, entity, copyCache);
 		copyValues(persister, entity, entity, source, copyCache);
 
 		event.setResult(entity);
 	}
 
 	protected void entityIsTransient(MergeEvent event, Map copyCache) {
 
 		LOG.trace( "Merging transient instance" );
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 
 		final String entityName = event.getEntityName();
 		final EntityPersister persister = source.getEntityPersister( entityName, entity );
 
 		final Serializable id = persister.hasIdentifierProperty() ?
 				persister.getIdentifier( entity, source ) :
 		        null;
 		if ( copyCache.containsKey( entity ) ) {
 			persister.setIdentifier( copyCache.get( entity ), id, source );
 		}
 		else {
 			( ( EventCache ) copyCache ).put( entity, source.instantiate( persister, id ), true ); //before cascade!
 		}
 		final Object copy = copyCache.get( entity );
 
 		// cascade first, so that all unsaved objects get their
 		// copy created before we actually copy
 		//cascadeOnMerge(event, persister, entity, copyCache, Cascades.CASCADE_BEFORE_MERGE);
 		super.cascadeBeforeSave(source, persister, entity, copyCache);
 		copyValues(persister, entity, copy, source, copyCache, ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT);
 
 		saveTransientEntity( copy, entityName, event.getRequestedId(), source, copyCache );
 
 		// cascade first, so that all unsaved objects get their
 		// copy created before we actually copy
 		super.cascadeAfterSave(source, persister, entity, copyCache);
 		copyValues(persister, entity, copy, source, copyCache, ForeignKeyDirection.FOREIGN_KEY_TO_PARENT);
 
 		event.setResult( copy );
 	}
 
 	private void saveTransientEntity(
 			Object entity,
 			String entityName,
 			Serializable requestedId,
 			EventSource source,
 			Map copyCache) {
 		//this bit is only *really* absolutely necessary for handling
 		//requestedId, but is also good if we merge multiple object
 		//graphs, since it helps ensure uniqueness
 		if (requestedId==null) {
 			saveWithGeneratedId( entity, entityName, copyCache, source, false );
 		}
 		else {
 			saveWithRequestedId( entity, requestedId, entityName, copyCache, source );
 		}
 	}
 
 	protected void entityIsDetached(MergeEvent event, Map copyCache) {
 
 		LOG.trace( "Merging detached instance" );
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 		final String entityName = persister.getEntityName();
 
 		Serializable id = event.getRequestedId();
 		if ( id == null ) {
 			id = persister.getIdentifier( entity, source );
 		}
 		else {
 			// check that entity id = requestedId
 			Serializable entityId = persister.getIdentifier( entity, source );
 			if ( !persister.getIdentifierType().isEqual( id, entityId, source.getFactory() ) ) {
 				throw new HibernateException( "merge requested with id not matching id of passed entity" );
 			}
 		}
 
 		String previousFetchProfile = source.getFetchProfile();
 		source.setFetchProfile("merge");
 		//we must clone embedded composite identifiers, or
 		//we will get back the same instance that we pass in
 		final Serializable clonedIdentifier = (Serializable) persister.getIdentifierType()
 				.deepCopy( id, source.getFactory() );
 		final Object result = source.get(entityName, clonedIdentifier);
 		source.setFetchProfile(previousFetchProfile);
 
 		if ( result == null ) {
 			//TODO: we should throw an exception if we really *know* for sure
 			//      that this is a detached instance, rather than just assuming
 			//throw new StaleObjectStateException(entityName, id);
 
 			// we got here because we assumed that an instance
 			// with an assigned id was detached, when it was
 			// really persistent
 			entityIsTransient(event, copyCache);
 		}
 		else {
 			( ( EventCache ) copyCache ).put( entity, result, true ); //before cascade!
 
 			final Object target = source.getPersistenceContext().unproxy(result);
 			if ( target == entity ) {
 				throw new AssertionFailure("entity was not detached");
 			}
 			else if ( !source.getEntityName(target).equals(entityName) ) {
 				throw new WrongClassException(
 						"class of the given object did not match class of persistent copy",
 						event.getRequestedId(),
 						entityName
 					);
 			}
 			else if ( isVersionChanged( entity, source, persister, target ) ) {
 				if ( source.getFactory().getStatistics().isStatisticsEnabled() ) {
 					source.getFactory().getStatisticsImplementor()
 							.optimisticFailure( entityName );
 				}
 				throw new StaleObjectStateException( entityName, id );
 			}
 
 			// cascade first, so that all unsaved objects get their
 			// copy created before we actually copy
 			cascadeOnMerge(source, persister, entity, copyCache);
 			copyValues(persister, entity, target, source, copyCache);
 
 			//copyValues works by reflection, so explicitly mark the entity instance dirty
 			markInterceptorDirty( entity, target, persister );
 
 			event.setResult(result);
 		}
 
 	}
 
 	private void markInterceptorDirty(final Object entity, final Object target, EntityPersister persister) {
 		if ( persister.getInstrumentationMetadata().isInstrumented() ) {
 			FieldInterceptor interceptor = persister.getInstrumentationMetadata().extractInterceptor( target );
 			if ( interceptor != null ) {
 				interceptor.dirty();
 			}
 		}
 	}
 
 	private boolean isVersionChanged(Object entity, EventSource source, EntityPersister persister, Object target) {
 		if ( ! persister.isVersioned() ) {
 			return false;
 		}
 		// for merging of versioned entities, we consider the version having
 		// been changed only when:
 		// 1) the two version values are different;
 		//      *AND*
 		// 2) The target actually represents database state!
 		//
 		// This second condition is a special case which allows
 		// an entity to be merged during the same transaction
 		// (though during a seperate operation) in which it was
 		// originally persisted/saved
 		boolean changed = ! persister.getVersionType().isSame(
 				persister.getVersion( target ),
 				persister.getVersion( entity )
 		);
 
 		// TODO : perhaps we should additionally require that the incoming entity
 		// version be equivalent to the defined unsaved-value?
 		return changed && existsInDatabase( target, source, persister );
 	}
 
 	private boolean existsInDatabase(Object entity, EventSource source, EntityPersister persister) {
 		EntityEntry entry = source.getPersistenceContext().getEntry( entity );
 		if ( entry == null ) {
 			Serializable id = persister.getIdentifier( entity, source );
 			if ( id != null ) {
 				final EntityKey key = source.generateEntityKey( id, persister );
 				final Object managedEntity = source.getPersistenceContext().getEntity( key );
 				entry = source.getPersistenceContext().getEntry( managedEntity );
 			}
 		}
 
 		return entry != null && entry.isExistsInDatabase();
 	}
 
 	protected void copyValues(
 			final EntityPersister persister,
 			final Object entity,
 			final Object target,
 			final SessionImplementor source,
 			final Map copyCache) {
 		final Object[] copiedValues = TypeHelper.replace(
 				persister.getPropertyValues( entity ),
 				persister.getPropertyValues( target ),
 				persister.getPropertyTypes(),
 				source,
 				target,
 				copyCache
 		);
 
 		persister.setPropertyValues( target, copiedValues );
 	}
 
 	protected void copyValues(
 			final EntityPersister persister,
 			final Object entity,
 			final Object target,
 			final SessionImplementor source,
 			final Map copyCache,
 			final ForeignKeyDirection foreignKeyDirection) {
 
 		final Object[] copiedValues;
 
 		if ( foreignKeyDirection == ForeignKeyDirection.FOREIGN_KEY_TO_PARENT ) {
 			// this is the second pass through on a merge op, so here we limit the
 			// replacement to associations types (value types were already replaced
 			// during the first pass)
 			copiedValues = TypeHelper.replaceAssociations(
 					persister.getPropertyValues( entity ),
 					persister.getPropertyValues( target ),
 					persister.getPropertyTypes(),
 					source,
 					target,
 					copyCache,
 					foreignKeyDirection
 			);
 		}
 		else {
 			copiedValues = TypeHelper.replace(
 					persister.getPropertyValues( entity ),
 					persister.getPropertyValues( target ),
 					persister.getPropertyTypes(),
 					source,
 					target,
 					copyCache,
 					foreignKeyDirection
 			);
 		}
 
 		persister.setPropertyValues( target, copiedValues );
 	}
 
 	/**
 	 * Perform any cascades needed as part of this copy event.
 	 *
 	 * @param source The merge event being processed.
 	 * @param persister The persister of the entity being copied.
 	 * @param entity The entity being copied.
 	 * @param copyCache A cache of already copied instance.
 	 */
 	protected void cascadeOnMerge(
 		final EventSource source,
 		final EntityPersister persister,
 		final Object entity,
 		final Map copyCache
 	) {
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadeAction(), Cascade.BEFORE_MERGE, source )
 					.cascade(persister, entity, copyCache);
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 
 	@Override
     protected CascadingAction getCascadeAction() {
-		return CascadingAction.MERGE;
+		return CascadingActions.MERGE;
 	}
 
 	@Override
     protected Boolean getAssumedUnsaved() {
 		return Boolean.FALSE;
 	}
 
 	/**
 	 * Cascade behavior is redefined by this subclass, disable superclass behavior
 	 */
 	@Override
     protected void cascadeAfterSave(EventSource source, EntityPersister persister, Object entity, Object anything)
 	throws HibernateException {
 	}
 
 	/**
 	 * Cascade behavior is redefined by this subclass, disable superclass behavior
 	 */
 	@Override
     protected void cascadeBeforeSave(EventSource source, EntityPersister persister, Object entity, Object anything)
 	throws HibernateException {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPersistEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPersistEventListener.java
index b68b69c105..69216478fc 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPersistEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPersistEventListener.java
@@ -1,232 +1,233 @@
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
 package org.hibernate.event.internal;
 
 import java.util.IdentityHashMap;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PersistEvent;
 import org.hibernate.event.spi.PersistEventListener;
 import org.hibernate.id.ForeignGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * Defines the default create event listener used by hibernate for creating
  * transient entities in response to generated create events.
  *
  * @author Gavin King
  */
 public class DefaultPersistEventListener extends AbstractSaveEventListener implements PersistEventListener {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			DefaultPersistEventListener.class.getName()
 	);
 
 	@Override
     protected CascadingAction getCascadeAction() {
-		return CascadingAction.PERSIST;
+		return CascadingActions.PERSIST;
 	}
 
 	@Override
     protected Boolean getAssumedUnsaved() {
 		return Boolean.TRUE;
 	}
 
 	/**
 	 * Handle the given create event.
 	 *
 	 * @param event The create event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onPersist(PersistEvent event) throws HibernateException {
 		onPersist( event, new IdentityHashMap(10) );
 	}
 
 	/**
 	 * Handle the given create event.
 	 *
 	 * @param event The create event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onPersist(PersistEvent event, Map createCache) throws HibernateException {
 		final SessionImplementor source = event.getSession();
 		final Object object = event.getObject();
 
 		final Object entity;
 		if ( object instanceof HibernateProxy ) {
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				if ( li.getSession() == source ) {
 					return; //NOTE EARLY EXIT!
 				}
 				else {
 					throw new PersistentObjectException( "uninitialized proxy passed to persist()" );
 				}
 			}
 			entity = li.getImplementation();
 		}
 		else {
 			entity = object;
 		}
 
 		final String entityName;
 		if ( event.getEntityName() != null ) {
 			entityName = event.getEntityName();
 		}
 		else {
 			entityName = source.bestGuessEntityName( entity );
 			event.setEntityName( entityName );
 		}
 
 		final EntityEntry entityEntry = source.getPersistenceContext().getEntry( entity );
 		EntityState entityState = getEntityState( entity, entityName, entityEntry, source );
 		if ( entityState == EntityState.DETACHED ) {
 			// JPA 2, in its version of a "foreign generated", allows the id attribute value
 			// to be manually set by the user, even though this manual value is irrelevant.
 			// The issue is that this causes problems with the Hibernate unsaved-value strategy
 			// which comes into play here in determining detached/transient state.
 			//
 			// Detect if we have this situation and if so null out the id value and calculate the
 			// entity state again.
 
 			// NOTE: entityEntry must be null to get here, so we cannot use any of its values
 			EntityPersister persister = source.getFactory().getEntityPersister( entityName );
 			if ( ForeignGenerator.class.isInstance( persister.getIdentifierGenerator() ) ) {
 				if ( LOG.isDebugEnabled() && persister.getIdentifier( entity, source ) != null ) {
 					LOG.debug( "Resetting entity id attribute to null for foreign generator" );
 				}
 				persister.setIdentifier( entity, null, source );
 				entityState = getEntityState( entity, entityName, entityEntry, source );
 			}
 		}
 
 		switch ( entityState ) {
 			case DETACHED: {
 				throw new PersistentObjectException(
 						"detached entity passed to persist: " +
 								getLoggableName( event.getEntityName(), entity )
 				);
 			}
 			case PERSISTENT: {
 				entityIsPersistent( event, createCache );
 				break;
 			}
 			case TRANSIENT: {
 				entityIsTransient( event, createCache );
 				break;
 			}
 			case DELETED: {
 				entityEntry.setStatus( Status.MANAGED );
 				entityEntry.setDeletedState( null );
 				event.getSession().getActionQueue().unScheduleDeletion( entityEntry, event.getObject() );
 				entityIsDeleted( event, createCache );
 				break;
 			}
 			default: {
 				throw new ObjectDeletedException(
 						"deleted entity passed to persist",
 						null,
 						getLoggableName( event.getEntityName(), entity )
 				);
 			}
 		}
 
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	protected void entityIsPersistent(PersistEvent event, Map createCache) {
 		LOG.trace( "Ignoring persistent instance" );
 		final EventSource source = event.getSession();
 
 		//TODO: check that entry.getIdentifier().equals(requestedId)
 
 		final Object entity = source.getPersistenceContext().unproxy( event.getObject() );
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 
 		if ( createCache.put(entity, entity)==null ) {
 			justCascade( createCache, source, entity, persister );
 
 		}
 	}
 
 	private void justCascade(Map createCache, EventSource source, Object entity, EntityPersister persister) {
 		//TODO: merge into one method!
 		cascadeBeforeSave(source, persister, entity, createCache);
 		cascadeAfterSave(source, persister, entity, createCache);
 	}
 
 	/**
 	 * Handle the given create event.
 	 *
 	 * @param event The save event to be handled.
 	 * @param createCache The copy cache of entity instance to merge/copy instance.
 	 */
 	@SuppressWarnings( {"unchecked"})
 	protected void entityIsTransient(PersistEvent event, Map createCache) {
 		LOG.trace( "Saving transient instance" );
 
 		final EventSource source = event.getSession();
 		final Object entity = source.getPersistenceContext().unproxy( event.getObject() );
 
 		if ( createCache.put( entity, entity ) == null ) {
 			saveWithGeneratedId( entity, event.getEntityName(), createCache, source, false );
 		}
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	private void entityIsDeleted(PersistEvent event, Map createCache) {
 		final EventSource source = event.getSession();
 
 		final Object entity = source.getPersistenceContext().unproxy( event.getObject() );
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 
 		LOG.tracef(
 				"un-scheduling entity deletion [%s]",
 				MessageHelper.infoString(
 						persister,
 						persister.getIdentifier( entity, source ),
 						source.getFactory()
 				)
 		);
 
 		if ( createCache.put( entity, entity ) == null ) {
 			justCascade( createCache, source, entity, persister );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPersistOnFlushEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPersistOnFlushEventListener.java
index 3f8095688e..58f7375b6d 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPersistOnFlushEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPersistOnFlushEventListener.java
@@ -1,36 +1,37 @@
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
 
 import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 
 /**
  * When persist is used as the cascade action, persistOnFlush should be used
  * @author Emmanuel Bernard
  */
 public class DefaultPersistOnFlushEventListener extends DefaultPersistEventListener {
 	protected CascadingAction getCascadeAction() {
-		return CascadingAction.PERSIST_ON_FLUSH;
+		return CascadingActions.PERSIST_ON_FLUSH;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultRefreshEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultRefreshEventListener.java
index 3f6c2c524a..d37818046d 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultRefreshEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultRefreshEventListener.java
@@ -1,178 +1,178 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 import java.util.IdentityHashMap;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.engine.internal.Cascade;
-import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.RefreshEvent;
 import org.hibernate.event.spi.RefreshEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * Defines the default refresh event listener used by hibernate for refreshing entities
  * in response to generated refresh events.
  *
  * @author Steve Ebersole
  */
 public class DefaultRefreshEventListener implements RefreshEventListener {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultRefreshEventListener.class.getName());
 
 	public void onRefresh(RefreshEvent event) throws HibernateException {
 		onRefresh( event, new IdentityHashMap(10) );
 	}
 
 	/**
 	 * Handle the given refresh event.
 	 *
 	 * @param event The refresh event to be handled.
 	 */
 	public void onRefresh(RefreshEvent event, Map refreshedAlready) {
 
 		final EventSource source = event.getSession();
 
 		boolean isTransient = ! source.contains( event.getObject() );
 		if ( source.getPersistenceContext().reassociateIfUninitializedProxy( event.getObject() ) ) {
 			if ( isTransient ) {
 				source.setReadOnly( event.getObject(), source.isDefaultReadOnly() );
 			}
 			return;
 		}
 
 		final Object object = source.getPersistenceContext().unproxyAndReassociate( event.getObject() );
 
 		if ( refreshedAlready.containsKey(object) ) {
 			LOG.trace( "Already refreshed" );
 			return;
 		}
 
 		final EntityEntry e = source.getPersistenceContext().getEntry( object );
 		final EntityPersister persister;
 		final Serializable id;
 
 		if ( e == null ) {
 			persister = source.getEntityPersister(event.getEntityName(), object); //refresh() does not pass an entityName
 			id = persister.getIdentifier( object, event.getSession() );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Refreshing transient {0}", MessageHelper.infoString( persister, id, source.getFactory() ) );
 			}
 			final EntityKey key = source.generateEntityKey( id, persister );
 			if ( source.getPersistenceContext().getEntry(key) != null ) {
 				throw new PersistentObjectException(
 						"attempted to refresh transient instance when persistent instance was already associated with the Session: " +
 						MessageHelper.infoString(persister, id, source.getFactory() )
 					);
 			}
 		}
 		else {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Refreshing ", MessageHelper.infoString( e.getPersister(), e.getId(), source.getFactory() ) );
 			}
 			if ( !e.isExistsInDatabase() ) {
 				throw new HibernateException( "this instance does not yet exist as a row in the database" );
 			}
 
 			persister = e.getPersister();
 			id = e.getId();
 		}
 
 		// cascade the refresh prior to refreshing this entity
 		refreshedAlready.put(object, object);
-		new Cascade( CascadingAction.REFRESH, Cascade.BEFORE_REFRESH, source)
+		new Cascade( CascadingActions.REFRESH, Cascade.BEFORE_REFRESH, source)
 				.cascade( persister, object, refreshedAlready );
 
 		if ( e != null ) {
 			final EntityKey key = source.generateEntityKey( id, persister );
 			source.getPersistenceContext().removeEntity(key);
 			if ( persister.hasCollections() ) new EvictVisitor( source ).process(object, persister);
 		}
 
 		if ( persister.hasCache() ) {
 			final CacheKey ck = source.generateCacheKey(
 					id,
 					persister.getIdentifierType(),
 					persister.getRootEntityName()
 			);
 			persister.getCacheAccessStrategy().evict( ck );
 		}
 
 		evictCachedCollections( persister, id, source.getFactory() );
 
 		String previousFetchProfile = source.getLoadQueryInfluencers().getInternalFetchProfile();
 		source.getLoadQueryInfluencers().setInternalFetchProfile( "refresh" );
 		Object result = persister.load( id, object, event.getLockOptions(), source );
 		// Keep the same read-only/modifiable setting for the entity that it had before refreshing;
 		// If it was transient, then set it to the default for the source.
 		if ( result != null ) {
 			if ( ! persister.isMutable() ) {
 				// this is probably redundant; it should already be read-only
 				source.setReadOnly( result, true );
 			}
 			else {
 				source.setReadOnly( result, ( e == null ? source.isDefaultReadOnly() : e.isReadOnly() ) );
 			}
 		}
 		source.getLoadQueryInfluencers().setInternalFetchProfile(previousFetchProfile);
 
 		UnresolvableObjectException.throwIfNull( result, id, persister.getEntityName() );
 
 	}
 
 	private void evictCachedCollections(EntityPersister persister, Serializable id, SessionFactoryImplementor factory) {
 		evictCachedCollections( persister.getPropertyTypes(), id, factory );
 	}
 
 	private void evictCachedCollections(Type[] types, Serializable id, SessionFactoryImplementor factory)
 	throws HibernateException {
         for ( Type type : types ) {
             if ( type.isCollectionType() ) {
                 factory.getCache().evictCollection( ( (CollectionType) type ).getRole(), id );
             }
             else if ( type.isComponentType() ) {
                 CompositeType actype = (CompositeType) type;
                 evictCachedCollections( actype.getSubtypes(), id, factory );
             }
         }
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultReplicateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultReplicateEventListener.java
index e319792cf6..7885ea6c4c 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultReplicateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultReplicateEventListener.java
@@ -1,222 +1,223 @@
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.ReplicationMode;
 import org.hibernate.TransientObjectException;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.ReplicateEvent;
 import org.hibernate.event.spi.ReplicateEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 
 /**
  * Defines the default replicate event listener used by Hibernate to replicate
  * entities in response to generated replicate events.
  *
  * @author Steve Ebersole
  */
 public class DefaultReplicateEventListener extends AbstractSaveEventListener implements ReplicateEventListener {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultReplicateEventListener.class.getName());
 
 	/**
 	 * Handle the given replicate event.
 	 *
 	 * @param event The replicate event to be handled.
 	 *
 	 * @throws TransientObjectException An invalid attempt to replicate a transient entity.
 	 */
 	public void onReplicate(ReplicateEvent event) {
 		final EventSource source = event.getSession();
 		if ( source.getPersistenceContext().reassociateIfUninitializedProxy( event.getObject() ) ) {
 			LOG.trace( "Uninitialized proxy passed to replicate()" );
 			return;
 		}
 
 		Object entity = source.getPersistenceContext().unproxyAndReassociate( event.getObject() );
 
 		if ( source.getPersistenceContext().isEntryFor( entity ) ) {
 			LOG.trace( "Ignoring persistent instance passed to replicate()" );
 			//hum ... should we cascade anyway? throw an exception? fine like it is?
 			return;
 		}
 
 		EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 
 		// get the id from the object
 		/*if ( persister.isUnsaved(entity, source) ) {
 			throw new TransientObjectException("transient instance passed to replicate()");
 		}*/
 		Serializable id = persister.getIdentifier( entity, source );
 		if ( id == null ) {
 			throw new TransientObjectException( "instance with null id passed to replicate()" );
 		}
 
 		final ReplicationMode replicationMode = event.getReplicationMode();
 
 		final Object oldVersion;
 		if ( replicationMode == ReplicationMode.EXCEPTION ) {
 			//always do an INSERT, and let it fail by constraint violation
 			oldVersion = null;
 		}
 		else {
 			//what is the version on the database?
 			oldVersion = persister.getCurrentVersion( id, source );
 		}
 
 		if ( oldVersion != null ) {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Found existing row for {0}", MessageHelper.infoString( persister, id, source.getFactory() ) );
 			}
 
 			/// HHH-2378
 			final Object realOldVersion = persister.isVersioned() ? oldVersion : null;
 
 			boolean canReplicate = replicationMode.shouldOverwriteCurrentVersion(
 					entity,
 					realOldVersion,
 					persister.getVersion( entity ),
 					persister.getVersionType()
 			);
 
 			// if can replicate, will result in a SQL UPDATE
 			// else do nothing (don't even reassociate object!)
 			if ( canReplicate )
 				performReplication( entity, id, realOldVersion, persister, replicationMode, source );
 			else
 				LOG.trace( "No need to replicate" );
 
 			//TODO: would it be better to do a refresh from db?
 		}
 		else {
 			// no existing row - do an insert
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "No existing row, replicating new instance {0}",
 						MessageHelper.infoString( persister, id, source.getFactory() ) );
 			}
 
 			final boolean regenerate = persister.isIdentifierAssignedByInsert(); // prefer re-generation of identity!
 			final EntityKey key = regenerate ? null : source.generateEntityKey( id, persister );
 
 			performSaveOrReplicate(
 					entity,
 					key,
 					persister,
 					regenerate,
 					replicationMode,
 					source,
 					true
 			);
 
 		}
 	}
 
 	@Override
     protected boolean visitCollectionsBeforeSave(Object entity, Serializable id, Object[] values, Type[] types, EventSource source) {
 		//TODO: we use two visitors here, inefficient!
 		OnReplicateVisitor visitor = new OnReplicateVisitor( source, id, entity, false );
 		visitor.processEntityPropertyValues( values, types );
 		return super.visitCollectionsBeforeSave( entity, id, values, types, source );
 	}
 
 	@Override
     protected boolean substituteValuesIfNecessary(
 			Object entity,
 			Serializable id,
 			Object[] values,
 			EntityPersister persister,
 			SessionImplementor source) {
 		return false;
 	}
 
 	@Override
     protected boolean isVersionIncrementDisabled() {
 		return true;
 	}
 
 	private void performReplication(
 			Object entity,
 			Serializable id,
 			Object version,
 			EntityPersister persister,
 			ReplicationMode replicationMode,
 			EventSource source) throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Replicating changes to {0}", MessageHelper.infoString( persister, id, source.getFactory() ) );
 		}
 
 		new OnReplicateVisitor( source, id, entity, true ).process( entity, persister );
 
 		source.getPersistenceContext().addEntity(
 				entity,
 				( persister.isMutable() ? Status.MANAGED : Status.READ_ONLY ),
 				null,
 				source.generateEntityKey( id, persister ),
 				version,
 				LockMode.NONE,
 				true,
 				persister,
 				true,
 				false
 		);
 
 		cascadeAfterReplicate( entity, persister, replicationMode, source );
 	}
 
 	private void cascadeAfterReplicate(
 			Object entity,
 			EntityPersister persister,
 			ReplicationMode replicationMode,
 			EventSource source) {
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
-			new Cascade( CascadingAction.REPLICATE, Cascade.AFTER_UPDATE, source )
+			new Cascade( CascadingActions.REPLICATE, Cascade.AFTER_UPDATE, source )
 					.cascade( persister, entity, replicationMode );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	@Override
     protected CascadingAction getCascadeAction() {
-		return CascadingAction.REPLICATE;
+		return CascadingActions.REPLICATE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java
index 1e8276f7c8..8fe131bc3d 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java
@@ -1,373 +1,374 @@
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.SaveOrUpdateEvent;
 import org.hibernate.event.spi.SaveOrUpdateEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 
 /**
  * Defines the default listener used by Hibernate for handling save-update
  * events.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public class DefaultSaveOrUpdateEventListener extends AbstractSaveEventListener implements SaveOrUpdateEventListener {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, DefaultSaveOrUpdateEventListener.class.getName() );
 
 	/**
 	 * Handle the given update event.
 	 *
 	 * @param event The update event to be handled.
 	 */
 	public void onSaveOrUpdate(SaveOrUpdateEvent event) {
 		final SessionImplementor source = event.getSession();
 		final Object object = event.getObject();
 		final Serializable requestedId = event.getRequestedId();
 
 		if ( requestedId != null ) {
 			//assign the requested id to the proxy, *before*
 			//reassociating the proxy
 			if ( object instanceof HibernateProxy ) {
 				( ( HibernateProxy ) object ).getHibernateLazyInitializer().setIdentifier( requestedId );
 			}
 		}
 
 		// For an uninitialized proxy, noop, don't even need to return an id, since it is never a save()
 		if ( reassociateIfUninitializedProxy( object, source ) ) {
 			LOG.trace( "Reassociated uninitialized proxy" );
 		}
 		else {
 			//initialize properties of the event:
 			final Object entity = source.getPersistenceContext().unproxyAndReassociate( object );
 			event.setEntity( entity );
 			event.setEntry( source.getPersistenceContext().getEntry( entity ) );
 			//return the id in the event object
 			event.setResultId( performSaveOrUpdate( event ) );
 		}
 
 	}
 
 	protected boolean reassociateIfUninitializedProxy(Object object, SessionImplementor source) {
 		return source.getPersistenceContext().reassociateIfUninitializedProxy( object );
 	}
 
 	protected Serializable performSaveOrUpdate(SaveOrUpdateEvent event) {
 		EntityState entityState = getEntityState(
 				event.getEntity(),
 				event.getEntityName(),
 				event.getEntry(),
 				event.getSession()
 		);
 
 		switch ( entityState ) {
 			case DETACHED:
 				entityIsDetached( event );
 				return null;
 			case PERSISTENT:
 				return entityIsPersistent( event );
 			default: //TRANSIENT or DELETED
 				return entityIsTransient( event );
 		}
 	}
 
 	protected Serializable entityIsPersistent(SaveOrUpdateEvent event) throws HibernateException {
 		LOG.trace( "Ignoring persistent instance" );
 
 		EntityEntry entityEntry = event.getEntry();
 		if ( entityEntry == null ) {
 			throw new AssertionFailure( "entity was transient or detached" );
 		}
 		else {
 
 			if ( entityEntry.getStatus() == Status.DELETED ) {
 				throw new AssertionFailure( "entity was deleted" );
 			}
 
 			final SessionFactoryImplementor factory = event.getSession().getFactory();
 
 			Serializable requestedId = event.getRequestedId();
 
 			Serializable savedId;
 			if ( requestedId == null ) {
 				savedId = entityEntry.getId();
 			}
 			else {
 
 				final boolean isEqual = !entityEntry.getPersister().getIdentifierType()
 						.isEqual( requestedId, entityEntry.getId(), factory );
 
 				if ( isEqual ) {
 					throw new PersistentObjectException(
 							"object passed to save() was already persistent: " +
 									MessageHelper.infoString( entityEntry.getPersister(), requestedId, factory )
 					);
 				}
 
 				savedId = requestedId;
 
 			}
 
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Object already associated with session: {0}", MessageHelper.infoString( entityEntry.getPersister(), savedId, factory ) );
 			}
 
 			return savedId;
 
 		}
 	}
 
 	/**
 	 * The given save-update event named a transient entity.
 	 * <p/>
 	 * Here, we will perform the save processing.
 	 *
 	 * @param event The save event to be handled.
 	 *
 	 * @return The entity's identifier after saving.
 	 */
 	protected Serializable entityIsTransient(SaveOrUpdateEvent event) {
 
 		LOG.trace( "Saving transient instance" );
 
 		final EventSource source = event.getSession();
 
 		EntityEntry entityEntry = event.getEntry();
 		if ( entityEntry != null ) {
 			if ( entityEntry.getStatus() == Status.DELETED ) {
 				source.forceFlush( entityEntry );
 			}
 			else {
 				throw new AssertionFailure( "entity was persistent" );
 			}
 		}
 
 		Serializable id = saveWithGeneratedOrRequestedId( event );
 
 		source.getPersistenceContext().reassociateProxy( event.getObject(), id );
 
 		return id;
 	}
 
 	/**
 	 * Save the transient instance, assigning the right identifier
 	 *
 	 * @param event The initiating event.
 	 *
 	 * @return The entity's identifier value after saving.
 	 */
 	protected Serializable saveWithGeneratedOrRequestedId(SaveOrUpdateEvent event) {
 		return saveWithGeneratedId(
 				event.getEntity(),
 				event.getEntityName(),
 				null,
 				event.getSession(),
 				true
 		);
 	}
 
 	/**
 	 * The given save-update event named a detached entity.
 	 * <p/>
 	 * Here, we will perform the update processing.
 	 *
 	 * @param event The update event to be handled.
 	 */
 	protected void entityIsDetached(SaveOrUpdateEvent event) {
 
 		LOG.trace( "Updating detached instance" );
 
 		if ( event.getSession().getPersistenceContext().isEntryFor( event.getEntity() ) ) {
 			//TODO: assertion only, could be optimized away
 			throw new AssertionFailure( "entity was persistent" );
 		}
 
 		Object entity = event.getEntity();
 
 		EntityPersister persister = event.getSession().getEntityPersister( event.getEntityName(), entity );
 
 		event.setRequestedId(
 				getUpdateId(
 						entity, persister, event.getRequestedId(), event.getSession()
 				)
 		);
 
 		performUpdate( event, entity, persister );
 
 	}
 
 	/**
 	 * Determine the id to use for updating.
 	 *
 	 * @param entity The entity.
 	 * @param persister The entity persister
 	 * @param requestedId The requested identifier
 	 * @param session The session
 	 *
 	 * @return The id.
 	 *
 	 * @throws TransientObjectException If the entity is considered transient.
 	 */
 	protected Serializable getUpdateId(
 			Object entity,
 			EntityPersister persister,
 			Serializable requestedId,
 			SessionImplementor session) {
 		// use the id assigned to the instance
 		Serializable id = persister.getIdentifier( entity, session );
 		if ( id == null ) {
 			// assume this is a newly instantiated transient object
 			// which should be saved rather than updated
 			throw new TransientObjectException(
 					"The given object has a null identifier: " +
 							persister.getEntityName()
 			);
 		}
 		else {
 			return id;
 		}
 
 	}
 
 	protected void performUpdate(
 			SaveOrUpdateEvent event,
 			Object entity,
 			EntityPersister persister) throws HibernateException {
 
 		if ( !persister.isMutable() ) {
 			LOG.trace( "Immutable instance passed to performUpdate()" );
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Updating {0}",
 					MessageHelper.infoString( persister, event.getRequestedId(), event.getSession().getFactory() ) );
 		}
 
 		final EventSource source = event.getSession();
 		final EntityKey key = source.generateEntityKey( event.getRequestedId(), persister );
 
 		source.getPersistenceContext().checkUniqueness(key, entity);
 
 		if (invokeUpdateLifecycle(entity, persister, source)) {
             reassociate(event, event.getObject(), event.getRequestedId(), persister);
             return;
         }
 
 		// this is a transient object with existing persistent state not loaded by the session
 
 		new OnUpdateVisitor(source, event.getRequestedId(), entity).process(entity, persister);
 
 		// TODO: put this stuff back in to read snapshot from
         // the second-level cache (needs some extra work)
         /*Object[] cachedState = null;
 
         if ( persister.hasCache() ) {
         	CacheEntry entry = (CacheEntry) persister.getCache()
         			.get( event.getRequestedId(), source.getTimestamp() );
             cachedState = entry==null ?
             		null :
             		entry.getState(); //TODO: half-assemble this stuff
         }*/
 
 		source.getPersistenceContext().addEntity(
 				entity,
 				(persister.isMutable() ? Status.MANAGED : Status.READ_ONLY),
 				null, // cachedState,
 				key,
 				persister.getVersion( entity ),
 				LockMode.NONE,
 				true,
 				persister,
 				false,
 				true // assume true, since we don't really know, and it doesn't matter
 				);
 
 		persister.afterReassociate(entity, source);
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Updating {0}", MessageHelper.infoString( persister, event.getRequestedId(), source.getFactory() ) );
 		}
 
 		cascadeOnUpdate( event, persister, entity );
 	}
 
 	protected boolean invokeUpdateLifecycle(Object entity, EntityPersister persister, EventSource source) {
 		if ( persister.implementsLifecycle() ) {
 			LOG.debug( "Calling onUpdate()" );
 			if ( ( (Lifecycle) entity ).onUpdate( source ) ) {
 				LOG.debug( "Update vetoed by onUpdate()" );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	/**
 	 * Handles the calls needed to perform cascades as part of an update request
 	 * for the given entity.
 	 *
 	 * @param event The event currently being processed.
 	 * @param persister The defined persister for the entity being updated.
 	 * @param entity The entity being updated.
 	 */
 	private void cascadeOnUpdate(SaveOrUpdateEvent event, EntityPersister persister, Object entity) {
 		EventSource source = event.getSession();
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
-			new Cascade( CascadingAction.SAVE_UPDATE, Cascade.AFTER_UPDATE, source )
+			new Cascade( CascadingActions.SAVE_UPDATE, Cascade.AFTER_UPDATE, source )
 					.cascade( persister, entity );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	@Override
     protected CascadingAction getCascadeAction() {
-		return CascadingAction.SAVE_UPDATE;
+		return CascadingActions.SAVE_UPDATE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Property.java b/hibernate-core/src/main/java/org/hibernate/mapping/Property.java
index cffdc68e2d..9b4af9da92 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Property.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Property.java
@@ -1,341 +1,342 @@
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
 import java.util.Iterator;
 import java.util.StringTokenizer;
 
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.property.Getter;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.property.PropertyAccessorFactory;
 import org.hibernate.property.Setter;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * Represents a property as part of an entity or a component.
  *
  * @author Gavin King
  */
 public class Property implements Serializable, MetaAttributable {
 	private String name;
 	private Value value;
 	private String cascade;
 	private boolean updateable = true;
 	private boolean insertable = true;
 	private boolean selectable = true;
 	private boolean optimisticLocked = true;
 	private PropertyGeneration generation = PropertyGeneration.NEVER;
 	private String propertyAccessorName;
 	private boolean lazy;
 	private boolean optional;
 	private String nodeName;
 	private java.util.Map metaAttributes;
 	private PersistentClass persistentClass;
 	private boolean naturalIdentifier;
 
 	public boolean isBackRef() {
 		return false;
 	}
 
 	/**
 	 * Does this property represent a synthetic property?  A synthetic property is one we create during
 	 * metamodel binding to represent a collection of columns but which does not represent a property
 	 * physically available on the entity.
 	 *
 	 * @return True if synthetic; false otherwise.
 	 */
 	public boolean isSynthetic() {
 		return false;
 	}
 
 	public Type getType() throws MappingException {
 		return value.getType();
 	}
 	
 	public int getColumnSpan() {
 		return value.getColumnSpan();
 	}
 	
 	public Iterator getColumnIterator() {
 		return value.getColumnIterator();
 	}
 	
 	public String getName() {
 		return name;
 	}
 	
 	public boolean isComposite() {
 		return value instanceof Component;
 	}
 
 	public Value getValue() {
 		return value;
 	}
 	
 	public boolean isPrimitive(Class clazz) {
 		return getGetter(clazz).getReturnType().isPrimitive();
 	}
 
 	public CascadeStyle getCascadeStyle() throws MappingException {
 		Type type = value.getType();
 		if ( type.isComponentType() ) {
 			return getCompositeCascadeStyle( (CompositeType) type, cascade );
 		}
 		else if ( type.isCollectionType() ) {
 			return getCollectionCascadeStyle( ( (Collection) value ).getElement().getType(), cascade );
 		}
 		else {
 			return getCascadeStyle( cascade );			
 		}
 	}
 
 	private static CascadeStyle getCompositeCascadeStyle(CompositeType compositeType, String cascade) {
 		if ( compositeType.isAnyType() ) {
 			return getCascadeStyle( cascade );
 		}
 		int length = compositeType.getSubtypes().length;
 		for ( int i=0; i<length; i++ ) {
-			if ( compositeType.getCascadeStyle(i) != CascadeStyle.NONE ) {
-				return CascadeStyle.ALL;
+			if ( compositeType.getCascadeStyle(i) != CascadeStyles.NONE ) {
+				return CascadeStyles.ALL;
 			}
 		}
 		return getCascadeStyle( cascade );
 	}
 
 	private static CascadeStyle getCollectionCascadeStyle(Type elementType, String cascade) {
 		if ( elementType.isComponentType() ) {
 			return getCompositeCascadeStyle( (CompositeType) elementType, cascade );
 		}
 		else {
 			return getCascadeStyle( cascade );
 		}
 	}
 	
 	private static CascadeStyle getCascadeStyle(String cascade) {
 		if ( cascade==null || cascade.equals("none") ) {
-			return CascadeStyle.NONE;
+			return CascadeStyles.NONE;
 		}
 		else {
 			StringTokenizer tokens = new StringTokenizer(cascade, ", ");
 			CascadeStyle[] styles = new CascadeStyle[ tokens.countTokens() ] ;
 			int i=0;
 			while ( tokens.hasMoreTokens() ) {
-				styles[i++] = CascadeStyle.getCascadeStyle( tokens.nextToken() );
+				styles[i++] = CascadeStyles.getCascadeStyle( tokens.nextToken() );
 			}
-			return new CascadeStyle.MultipleCascadeStyle(styles);
+			return new CascadeStyles.MultipleCascadeStyle(styles);
 		}		
 	}
 	
 	public String getCascade() {
 		return cascade;
 	}
 
 	public void setCascade(String cascade) {
 		this.cascade = cascade;
 	}
 
 	public void setName(String name) {
 		this.name = name==null ? null : name.intern();
 	}
 
 	public void setValue(Value value) {
 		this.value = value;
 	}
 
 	public boolean isUpdateable() {
 		// if the property mapping consists of all formulas,
 		// make it non-updateable
 		return updateable && !ArrayHelper.isAllFalse( value.getColumnUpdateability() );
 	}
 
 	public boolean isInsertable() {
 		// if the property mapping consists of all formulas, 
 		// make it non-insertable
 		final boolean[] columnInsertability = value.getColumnInsertability();
 		return insertable && (
 				columnInsertability.length==0 ||
 				!ArrayHelper.isAllFalse( columnInsertability )
 			);
 	}
 
     public PropertyGeneration getGeneration() {
         return generation;
     }
 
     public void setGeneration(PropertyGeneration generation) {
         this.generation = generation;
     }
 
     public void setUpdateable(boolean mutable) {
 		this.updateable = mutable;
 	}
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public String getPropertyAccessorName() {
 		return propertyAccessorName;
 	}
 
 	public void setPropertyAccessorName(String string) {
 		propertyAccessorName = string;
 	}
 
 	/**
 	 * Approximate!
 	 */
 	boolean isNullable() {
 		return value==null || value.isNullable();
 	}
 
 	public boolean isBasicPropertyAccessor() {
 		return propertyAccessorName==null || "property".equals(propertyAccessorName);
 	}
 
 	public java.util.Map getMetaAttributes() {
 		return metaAttributes;
 	}
 
 	public MetaAttribute getMetaAttribute(String attributeName) {
 		return metaAttributes==null?null:(MetaAttribute) metaAttributes.get(attributeName);
 	}
 
 	public void setMetaAttributes(java.util.Map metas) {
 		this.metaAttributes = metas;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return getValue().isValid(mapping);
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + name + ')';
 	}
 	
 	public void setLazy(boolean lazy) {
 		this.lazy=lazy;
 	}
 	
 	public boolean isLazy() {
 		if ( value instanceof ToOne ) {
 			// both many-to-one and one-to-one are represented as a
 			// Property.  EntityPersister is relying on this value to
 			// determine "lazy fetch groups" in terms of field-level
 			// interception.  So we need to make sure that we return
 			// true here for the case of many-to-one and one-to-one
 			// with lazy="no-proxy"
 			//
 			// * impl note - lazy="no-proxy" currently forces both
 			// lazy and unwrap to be set to true.  The other case we
 			// are extremely interested in here is that of lazy="proxy"
 			// where lazy is set to true, but unwrap is set to false.
 			// thus we use both here under the assumption that this
 			// return is really only ever used during persister
 			// construction to determine the lazy property/field fetch
 			// groupings.  If that assertion changes then this check
 			// needs to change as well.  Partially, this is an issue with
 			// the overloading of the term "lazy" here...
 			ToOne toOneValue = ( ToOne ) value;
 			return toOneValue.isLazy() && toOneValue.isUnwrapProxy();
 		}
 		return lazy;
 	}
 	
 	public boolean isOptimisticLocked() {
 		return optimisticLocked;
 	}
 
 	public void setOptimisticLocked(boolean optimisticLocked) {
 		this.optimisticLocked = optimisticLocked;
 	}
 	
 	public boolean isOptional() {
 		return optional || isNullable();
 	}
 	
 	public void setOptional(boolean optional) {
 		this.optional = optional;
 	}
 
 	public PersistentClass getPersistentClass() {
 		return persistentClass;
 	}
 
 	public void setPersistentClass(PersistentClass persistentClass) {
 		this.persistentClass = persistentClass;
 	}
 
 	public boolean isSelectable() {
 		return selectable;
 	}
 	
 	public void setSelectable(boolean selectable) {
 		this.selectable = selectable;
 	}
 
 	public String getNodeName() {
 		return nodeName;
 	}
 
 	public void setNodeName(String nodeName) {
 		this.nodeName = nodeName;
 	}
 
 	public String getAccessorPropertyName( EntityMode mode ) {
 		return getName();
 	}
 
 	// todo : remove
 	public Getter getGetter(Class clazz) throws PropertyNotFoundException, MappingException {
 		return getPropertyAccessor(clazz).getGetter( clazz, name );
 	}
 
 	// todo : remove
 	public Setter getSetter(Class clazz) throws PropertyNotFoundException, MappingException {
 		return getPropertyAccessor(clazz).getSetter(clazz, name);
 	}
 
 	// todo : remove
 	public PropertyAccessor getPropertyAccessor(Class clazz) throws MappingException {
 		return PropertyAccessorFactory.getPropertyAccessor( clazz, getPropertyAccessorName() );
 	}
 
 	public boolean isNaturalIdentifier() {
 		return naturalIdentifier;
 	}
 
 	public void setNaturalIdentifier(boolean naturalIdentifier) {
 		this.naturalIdentifier = naturalIdentifier;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java
index d047a61bb6..b80066a8a0 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java
@@ -1,393 +1,394 @@
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
 
 import java.util.ArrayList;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.metamodel.domain.PluralAttribute;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractPluralAttributeBinding extends AbstractAttributeBinding implements PluralAttributeBinding {
 	private final CollectionKey collectionKey;
 	private final AbstractCollectionElement collectionElement;
 
 	private Table collectionTable;
 
 	private FetchTiming fetchTiming;
 	private FetchStyle fetchStyle;
 	private int batchSize = -1;
 
 	private CascadeStyle cascadeStyle;
 	private boolean orphanDelete;
 
 	private Caching caching;
 
 	private boolean inverse;
 	private boolean mutable = true;
 
 	private Class<? extends CollectionPersister> collectionPersisterClass;
 
 	private String where;
 	private String orderBy;
 	private boolean sorted;
 	private Comparator comparator;
 	private String comparatorClassName;
 
 	private String customLoaderName;
 	private CustomSQL customSqlInsert;
 	private CustomSQL customSqlUpdate;
 	private CustomSQL customSqlDelete;
 	private CustomSQL customSqlDeleteAll;
 
 	private String referencedPropertyName;
 
 	private final java.util.Map filters = new HashMap();
 	private final java.util.Set<String> synchronizedTables = new HashSet<String>();
 
 	protected AbstractPluralAttributeBinding(
 			AttributeBindingContainer container,
 			PluralAttribute attribute,
 			CollectionElementNature collectionElementNature) {
 		super( container, attribute );
 		this.collectionKey = new CollectionKey( this );
 		this.collectionElement = interpretNature( collectionElementNature );
 	}
 
 	private AbstractCollectionElement interpretNature(CollectionElementNature collectionElementNature) {
 		switch ( collectionElementNature ) {
 			case BASIC: {
 				return new BasicCollectionElement( this );
 			}
 			case COMPOSITE: {
 				return new CompositeCollectionElement( this );
 			}
 			case ONE_TO_MANY: {
 				return new OneToManyCollectionElement( this );
 			}
 			case MANY_TO_MANY: {
 				return new ManyToManyCollectionElement( this );
 			}
 			case MANY_TO_ANY: {
 				return new ManyToAnyCollectionElement( this );
 			}
 			default: {
 				throw new AssertionFailure( "Unknown collection element nature : " + collectionElementNature );
 			}
 		}
 	}
 
 //	protected void initializeBinding(PluralAttributeBindingState state) {
 //		super.initialize( state );
 //		fetchMode = state.getFetchMode();
 //		extraLazy = state.isExtraLazy();
 //		collectionElement.setNodeName( state.getElementNodeName() );
 //		collectionElement.setTypeName( state.getElementTypeName() );
 //		inverse = state.isInverse();
 //		mutable = state.isMutable();
 //		subselectLoadable = state.isSubselectLoadable();
 //		if ( isSubselectLoadable() ) {
 //			getEntityBinding().setSubselectLoadableCollections( true );
 //		}
 //		cacheConcurrencyStrategy = state.getCacheConcurrencyStrategy();
 //		cacheRegionName = state.getCacheRegionName();
 //		orderBy = state.getOrderBy();
 //		where = state.getWhere();
 //		referencedPropertyName = state.getReferencedPropertyName();
 //		sorted = state.isSorted();
 //		comparator = state.getComparator();
 //		comparatorClassName = state.getComparatorClassName();
 //		orphanDelete = state.isOrphanDelete();
 //		batchSize = state.getBatchSize();
 //		embedded = state.isEmbedded();
 //		optimisticLocked = state.isOptimisticLocked();
 //		collectionPersisterClass = state.getCollectionPersisterClass();
 //		filters.putAll( state.getFilters() );
 //		synchronizedTables.addAll( state.getSynchronizedTables() );
 //		customSQLInsert = state.getCustomSQLInsert();
 //		customSQLUpdate = state.getCustomSQLUpdate();
 //		customSQLDelete = state.getCustomSQLDelete();
 //		customSQLDeleteAll = state.getCustomSQLDeleteAll();
 //		loaderName = state.getLoaderName();
 //	}
 
 	@Override
 	public PluralAttribute getAttribute() {
 		return (PluralAttribute) super.getAttribute();
 	}
 
 	@Override
 	public boolean isAssociation() {
 		return collectionElement.getCollectionElementNature() == CollectionElementNature.MANY_TO_ANY
 				|| collectionElement.getCollectionElementNature() == CollectionElementNature.MANY_TO_MANY
 				|| collectionElement.getCollectionElementNature() == CollectionElementNature.ONE_TO_MANY;
 	}
 
 	@Override
 	public TableSpecification getCollectionTable() {
 		return collectionTable;
 	}
 
 	public void setCollectionTable(Table collectionTable) {
 		this.collectionTable = collectionTable;
 	}
 
 	@Override
 	public CollectionKey getCollectionKey() {
 		return collectionKey;
 	}
 
 	@Override
 	public AbstractCollectionElement getCollectionElement() {
 		return collectionElement;
 	}
 
 	@Override
 	public CascadeStyle getCascadeStyle() {
 		return cascadeStyle;
 	}
 
 	@Override
 	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles) {
 		List<CascadeStyle> cascadeStyleList = new ArrayList<CascadeStyle>();
 		for ( CascadeStyle style : cascadeStyles ) {
-			if ( style != CascadeStyle.NONE ) {
+			if ( style != CascadeStyles.NONE ) {
 				cascadeStyleList.add( style );
 			}
-			if ( style == CascadeStyle.DELETE_ORPHAN ||
-					style == CascadeStyle.ALL_DELETE_ORPHAN ) {
+			if ( style == CascadeStyles.DELETE_ORPHAN ||
+					style == CascadeStyles.ALL_DELETE_ORPHAN ) {
 				orphanDelete = true;
 			}
 		}
 
 		if ( cascadeStyleList.isEmpty() ) {
-			cascadeStyle = CascadeStyle.NONE;
+			cascadeStyle = CascadeStyles.NONE;
 		}
 		else if ( cascadeStyleList.size() == 1 ) {
 			cascadeStyle = cascadeStyleList.get( 0 );
 		}
 		else {
-			cascadeStyle = new CascadeStyle.MultipleCascadeStyle(
+			cascadeStyle = new CascadeStyles.MultipleCascadeStyle(
 					cascadeStyleList.toArray( new CascadeStyle[ cascadeStyleList.size() ] )
 			);
 		}
 	}
 
 	@Override
 	public boolean isOrphanDelete() {
 		return orphanDelete;
 	}
 
 	@Override
 	public FetchMode getFetchMode() {
 		if ( getFetchStyle() == FetchStyle.JOIN ) {
 			return FetchMode.JOIN;
 		}
 		else {
 			return FetchMode.SELECT;
 		}
 	}
 
 	@Override
 	public FetchTiming getFetchTiming() {
 		return fetchTiming;
 	}
 
 	@Override
 	public void setFetchTiming(FetchTiming fetchTiming) {
 		this.fetchTiming = fetchTiming;
 	}
 
 	@Override
 	public FetchStyle getFetchStyle() {
 		return fetchStyle;
 	}
 
 	@Override
 	public void setFetchStyle(FetchStyle fetchStyle) {
 		this.fetchStyle = fetchStyle;
 	}
 
 	@Override
 	public String getCustomLoaderName() {
 		return customLoaderName;
 	}
 
 	public void setCustomLoaderName(String customLoaderName) {
 		this.customLoaderName = customLoaderName;
 	}
 
 	@Override
 	public CustomSQL getCustomSqlInsert() {
 		return customSqlInsert;
 	}
 
 	public void setCustomSqlInsert(CustomSQL customSqlInsert) {
 		this.customSqlInsert = customSqlInsert;
 	}
 
 	@Override
 	public CustomSQL getCustomSqlUpdate() {
 		return customSqlUpdate;
 	}
 
 	public void setCustomSqlUpdate(CustomSQL customSqlUpdate) {
 		this.customSqlUpdate = customSqlUpdate;
 	}
 
 	@Override
 	public CustomSQL getCustomSqlDelete() {
 		return customSqlDelete;
 	}
 
 	public void setCustomSqlDelete(CustomSQL customSqlDelete) {
 		this.customSqlDelete = customSqlDelete;
 	}
 
 	@Override
 	public CustomSQL getCustomSqlDeleteAll() {
 		return customSqlDeleteAll;
 	}
 
 	public void setCustomSqlDeleteAll(CustomSQL customSqlDeleteAll) {
 		this.customSqlDeleteAll = customSqlDeleteAll;
 	}
 
 	public Class<? extends CollectionPersister> getCollectionPersisterClass() {
 		return collectionPersisterClass;
 	}
 
 	public void setCollectionPersisterClass(Class<? extends CollectionPersister> collectionPersisterClass) {
 		this.collectionPersisterClass = collectionPersisterClass;
 	}
 
 	public Caching getCaching() {
 		return caching;
 	}
 
 	public void setCaching(Caching caching) {
 		this.caching = caching;
 	}
 
 	@Override
 	public String getOrderBy() {
 		return orderBy;
 	}
 
 	public void setOrderBy(String orderBy) {
 		this.orderBy = orderBy;
 	}
 
 	@Override
 	public String getWhere() {
 		return where;
 	}
 
 	public void setWhere(String where) {
 		this.where = where;
 	}
 
 	@Override
 	public boolean isInverse() {
 		return inverse;
 	}
 
 	public void setInverse(boolean inverse) {
 		this.inverse = inverse;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public void setMutable(boolean mutable) {
 		this.mutable = mutable;
 	}
 
 	@Override
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public void setBatchSize(int batchSize) {
 		this.batchSize = batchSize;
 	}
 
 
 
 
 
 
 
 
 
 
 
 	public String getReferencedPropertyName() {
 		return referencedPropertyName;
 	}
 
 	@Override
 	public boolean isSorted() {
 		return sorted;
 	}
 
 	@Override
 	public Comparator getComparator() {
 		return comparator;
 	}
 
 	public void setComparator(Comparator comparator) {
 		this.comparator = comparator;
 	}
 
 	public String getComparatorClassName() {
 		return comparatorClassName;
 	}
 
 	public void addFilter(String name, String condition) {
 		filters.put( name, condition );
 	}
 
 	@Override
 	public java.util.Map getFilterMap() {
 		return filters;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CascadeType.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CascadeType.java
index 1b414b4d6c..14a4f2aa0a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CascadeType.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CascadeType.java
@@ -1,172 +1,173 @@
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
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadeStyles;
 
 
 /**
  * @author Hardy Ferentschik
  * @todo integrate this w/ org.hibernate.engine.spi.CascadeStyle
  */
 public enum CascadeType {
 	/**
 	 * Cascades save, delete, update, evict, lock, replicate, merge, persist
 	 */
 	ALL,
 
 	/**
 	 * Cascades save, delete, update, evict, lock, replicate, merge, persist + delete orphans
 	 */
 	ALL_DELETE_ORPHAN,
 
 	/**
 	 * Cascades save and update
 	 */
 	UPDATE,
 
 	/**
 	 * Cascades persist
 	 */
 	PERSIST,
 
 	/**
 	 * Cascades merge
 	 */
 	MERGE,
 
 	/**
 	 * Cascades lock
 	 */
 	LOCK,
 
 	/**
 	 * Cascades refresh
 	 */
 	REFRESH,
 
 	/**
 	 * Cascades replicate
 	 */
 	REPLICATE,
 
 	/**
 	 * Cascades evict
 	 */
 	EVICT,
 
 	/**
 	 * Cascade delete
 	 */
 	DELETE,
 
 	/**
 	 * Cascade delete + delete orphans
 	 */
 	DELETE_ORPHAN,
 
 	/**
 	 * No cascading
 	 */
 	NONE;
 
 	private static final Map<String, CascadeType> hbmOptionToCascadeType = new HashMap<String, CascadeType>();
 
 	static {
 		hbmOptionToCascadeType.put( "all", ALL );
 		hbmOptionToCascadeType.put( "all-delete-orphan", ALL_DELETE_ORPHAN );
 		hbmOptionToCascadeType.put( "save-update", UPDATE );
 		hbmOptionToCascadeType.put( "persist", PERSIST );
 		hbmOptionToCascadeType.put( "merge", MERGE );
 		hbmOptionToCascadeType.put( "lock", LOCK );
 		hbmOptionToCascadeType.put( "refresh", REFRESH );
 		hbmOptionToCascadeType.put( "replicate", REPLICATE );
 		hbmOptionToCascadeType.put( "evict", EVICT );
 		hbmOptionToCascadeType.put( "delete", DELETE );
 		hbmOptionToCascadeType.put( "remove", DELETE ); // adds remove as a sort-of alias for delete...
 		hbmOptionToCascadeType.put( "delete-orphan", DELETE_ORPHAN );
 		hbmOptionToCascadeType.put( "none", NONE );
 	}
 
 	private static final Map<javax.persistence.CascadeType, CascadeType> jpaCascadeTypeToHibernateCascadeType = new HashMap<javax.persistence.CascadeType, CascadeType>();
 
 	static {
 		jpaCascadeTypeToHibernateCascadeType.put( javax.persistence.CascadeType.ALL, ALL );
 		jpaCascadeTypeToHibernateCascadeType.put( javax.persistence.CascadeType.PERSIST, PERSIST );
 		jpaCascadeTypeToHibernateCascadeType.put( javax.persistence.CascadeType.MERGE, MERGE );
 		jpaCascadeTypeToHibernateCascadeType.put( javax.persistence.CascadeType.REFRESH, REFRESH );
 		jpaCascadeTypeToHibernateCascadeType.put( javax.persistence.CascadeType.DETACH, EVICT );
 	}
 
 	private static final Map<CascadeType, CascadeStyle> cascadeTypeToCascadeStyle = new HashMap<CascadeType, CascadeStyle>();
 	static {
-		cascadeTypeToCascadeStyle.put( ALL, CascadeStyle.ALL );
-		cascadeTypeToCascadeStyle.put( ALL_DELETE_ORPHAN, CascadeStyle.ALL_DELETE_ORPHAN );
-		cascadeTypeToCascadeStyle.put( UPDATE, CascadeStyle.UPDATE );
-		cascadeTypeToCascadeStyle.put( PERSIST, CascadeStyle.PERSIST );
-		cascadeTypeToCascadeStyle.put( MERGE, CascadeStyle.MERGE );
-		cascadeTypeToCascadeStyle.put( LOCK, CascadeStyle.LOCK );
-		cascadeTypeToCascadeStyle.put( REFRESH, CascadeStyle.REFRESH );
-		cascadeTypeToCascadeStyle.put( REPLICATE, CascadeStyle.REPLICATE );
-		cascadeTypeToCascadeStyle.put( EVICT, CascadeStyle.EVICT );
-		cascadeTypeToCascadeStyle.put( DELETE, CascadeStyle.DELETE );
-		cascadeTypeToCascadeStyle.put( DELETE_ORPHAN, CascadeStyle.DELETE_ORPHAN );
-		cascadeTypeToCascadeStyle.put( NONE, CascadeStyle.NONE );
+		cascadeTypeToCascadeStyle.put( ALL, CascadeStyles.ALL );
+		cascadeTypeToCascadeStyle.put( ALL_DELETE_ORPHAN, CascadeStyles.ALL_DELETE_ORPHAN );
+		cascadeTypeToCascadeStyle.put( UPDATE, CascadeStyles.UPDATE );
+		cascadeTypeToCascadeStyle.put( PERSIST, CascadeStyles.PERSIST );
+		cascadeTypeToCascadeStyle.put( MERGE, CascadeStyles.MERGE );
+		cascadeTypeToCascadeStyle.put( LOCK, CascadeStyles.LOCK );
+		cascadeTypeToCascadeStyle.put( REFRESH, CascadeStyles.REFRESH );
+		cascadeTypeToCascadeStyle.put( REPLICATE, CascadeStyles.REPLICATE );
+		cascadeTypeToCascadeStyle.put( EVICT, CascadeStyles.EVICT );
+		cascadeTypeToCascadeStyle.put( DELETE, CascadeStyles.DELETE );
+		cascadeTypeToCascadeStyle.put( DELETE_ORPHAN, CascadeStyles.DELETE_ORPHAN );
+		cascadeTypeToCascadeStyle.put( NONE, CascadeStyles.NONE );
 	}
 
 	/**
 	 * @param hbmOptionName the cascading option as specified in the hbm mapping file
 	 *
 	 * @return Returns the {@code CascadeType} for a given hbm cascading option
 	 */
 	public static CascadeType getCascadeType(String hbmOptionName) {
 		return hbmOptionToCascadeType.get( hbmOptionName );
 	}
 
 	/**
 	 * @param jpaCascade the jpa cascade type
 	 *
 	 * @return Returns the Hibernate {@code CascadeType} for a given jpa cascade type
 	 */
 	public static CascadeType getCascadeType(javax.persistence.CascadeType jpaCascade) {
 		return jpaCascadeTypeToHibernateCascadeType.get( jpaCascade );
 	}
 
 	/**
 	 * @return Returns the {@code CascadeStyle} that corresponds to this {@code CascadeType}
 	 *
 	 * @throws MappingException if there is not corresponding {@code CascadeStyle}
 	 */
 	public CascadeStyle toCascadeStyle() {
 		CascadeStyle cascadeStyle = cascadeTypeToCascadeStyle.get( this );
 		if ( cascadeStyle == null ) {
 			throw new MappingException( "No CascadeStyle that corresponds with CascadeType=" + this.name() );
 		}
 		return cascadeStyle;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
index 93876ba1bd..798ff70184 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
@@ -1,240 +1,241 @@
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
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.metamodel.domain.SingularAttribute;
 
 /**
  * TODO : javadoc
  *
  * @author Gail Badner
  * @author Steve Ebersole
  */
 public class ManyToOneAttributeBinding extends BasicAttributeBinding implements SingularAssociationAttributeBinding {
 	private String referencedEntityName;
 	private String referencedAttributeName;
 	private AttributeBinding referencedAttributeBinding;
 
 	private boolean isLogicalOneToOne;
 	private String foreignKeyName;
 
 	private CascadeStyle cascadeStyle;
 	private FetchTiming fetchTiming;
 	private FetchStyle fetchStyle;
 
 	ManyToOneAttributeBinding(AttributeBindingContainer container, SingularAttribute attribute) {
 		super( container, attribute, false, false );
 	}
 
 	@Override
 	public boolean isAssociation() {
 		return true;
 	}
 
 	@Override
 	public final boolean isPropertyReference() {
 		return referencedAttributeName != null;
 	}
 
 	@Override
 	public final String getReferencedEntityName() {
 		return referencedEntityName;
 	}
 
 	@Override
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
 	@Override
 	public final String getReferencedAttributeName() {
 		return referencedAttributeName;
 	}
 
 	@Override
 	public void setReferencedAttributeName(String referencedEntityAttributeName) {
 		this.referencedAttributeName = referencedEntityAttributeName;
 	}
 
 	@Override
 	public CascadeStyle getCascadeStyle() {
 		return cascadeStyle;
 	}
 
 	@Override
 	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles) {
 		List<CascadeStyle> cascadeStyleList = new ArrayList<CascadeStyle>();
 		for ( CascadeStyle style : cascadeStyles ) {
-			if ( style != CascadeStyle.NONE ) {
+			if ( style != CascadeStyles.NONE ) {
 				cascadeStyleList.add( style );
 			}
 		}
 		if ( cascadeStyleList.isEmpty() ) {
-			cascadeStyle = CascadeStyle.NONE;
+			cascadeStyle = CascadeStyles.NONE;
 		}
 		else if ( cascadeStyleList.size() == 1 ) {
 			cascadeStyle = cascadeStyleList.get( 0 );
 		}
 		else {
-			cascadeStyle = new CascadeStyle.MultipleCascadeStyle(
+			cascadeStyle = new CascadeStyles.MultipleCascadeStyle(
 					cascadeStyleList.toArray( new CascadeStyle[ cascadeStyleList.size() ] )
 			);
 		}
 	}
 
 	@Override
 	public FetchTiming getFetchTiming() {
 		return fetchTiming;
 	}
 
 	@Override
 	public void setFetchTiming(FetchTiming fetchTiming) {
 		this.fetchTiming = fetchTiming;
 	}
 
 	@Override
 	public FetchStyle getFetchStyle() {
 		return fetchStyle;
 	}
 
 	@Override
 	public void setFetchStyle(FetchStyle fetchStyle) {
 		if ( fetchStyle == FetchStyle.SUBSELECT ) {
 			throw new AssertionFailure( "Subselect fetching not yet supported for singular associations" );
 		}
 		this.fetchStyle = fetchStyle;
 	}
 
 	@Override
 	public FetchMode getFetchMode() {
 		if ( fetchStyle == FetchStyle.JOIN ) {
 			return FetchMode.JOIN;
 		}
 		else if ( fetchStyle == FetchStyle.SELECT ) {
 			return FetchMode.SELECT;
 		}
 		else if ( fetchStyle == FetchStyle.BATCH ) {
 			// we need the subsequent select...
 			return FetchMode.SELECT;
 		}
 
 		throw new AssertionFailure( "Unexpected fetch style : " + fetchStyle.name() );
 	}
 
 	@Override
 	public final boolean isReferenceResolved() {
 		return referencedAttributeBinding != null;
 	}
 
 	@Override
 	public final void resolveReference(AttributeBinding referencedAttributeBinding) {
 		if ( ! EntityBinding.class.isInstance( referencedAttributeBinding.getContainer() ) ) {
 			throw new AssertionFailure( "Illegal attempt to resolve many-to-one reference based on non-entity attribute" );
 		}
 		final EntityBinding entityBinding = (EntityBinding) referencedAttributeBinding.getContainer();
 		if ( !referencedEntityName.equals( entityBinding.getEntity().getName() ) ) {
 			throw new IllegalStateException(
 					"attempt to set EntityBinding with name: [" +
 							entityBinding.getEntity().getName() +
 							"; entity name should be: " + referencedEntityName
 			);
 		}
 		if ( referencedAttributeName == null ) {
 			referencedAttributeName = referencedAttributeBinding.getAttribute().getName();
 		}
 		else if ( !referencedAttributeName.equals( referencedAttributeBinding.getAttribute().getName() ) ) {
 			throw new IllegalStateException(
 					"Inconsistent attribute name; expected: " + referencedAttributeName +
 							"actual: " + referencedAttributeBinding.getAttribute().getName()
 			);
 		}
 		this.referencedAttributeBinding = referencedAttributeBinding;
 //		buildForeignKey();
 	}
 
 	@Override
 	public AttributeBinding getReferencedAttributeBinding() {
 		if ( !isReferenceResolved() ) {
 			throw new IllegalStateException( "Referenced AttributeBiding has not been resolved." );
 		}
 		return referencedAttributeBinding;
 	}
 
 	@Override
 	public final EntityBinding getReferencedEntityBinding() {
 		return (EntityBinding) referencedAttributeBinding.getContainer();
 	}
 
 //	private void buildForeignKey() {
 //		// TODO: move this stuff to relational model
 //		ForeignKey foreignKey = getValue().getTable()
 //				.createForeignKey( referencedAttributeBinding.getValue().getTable(), foreignKeyName );
 //		Iterator<SimpleValue> referencingValueIterator = getSimpleValues().iterator();
 //		Iterator<SimpleValue> targetValueIterator = referencedAttributeBinding.getSimpleValues().iterator();
 //		while ( referencingValueIterator.hasNext() ) {
 //			if ( !targetValueIterator.hasNext() ) {
 //				// TODO: improve this message
 //				throw new MappingException(
 //						"number of values in many-to-one reference is greater than number of values in target"
 //				);
 //			}
 //			SimpleValue referencingValue = referencingValueIterator.next();
 //			SimpleValue targetValue = targetValueIterator.next();
 //			if ( Column.class.isInstance( referencingValue ) ) {
 //				if ( !Column.class.isInstance( targetValue ) ) {
 //					// TODO improve this message
 //					throw new MappingException( "referencing value is a column, but target is not a column" );
 //				}
 //				foreignKey.addColumnMapping( Column.class.cast( referencingValue ), Column.class.cast( targetValue ) );
 //			}
 //			else if ( Column.class.isInstance( targetValue ) ) {
 //				// TODO: improve this message
 //				throw new MappingException( "referencing value is not a column, but target is a column." );
 //			}
 //		}
 //		if ( targetValueIterator.hasNext() ) {
 //			throw new MappingException( "target value has more simple values than referencing value" );
 //		}
 //	}
 //
 //	public void validate() {
 //		// can't check this until both the domain and relational states are initialized...
 //		if ( getCascadeTypes().contains( CascadeType.DELETE_ORPHAN ) ) {
 //			if ( !isLogicalOneToOne ) {
 //				throw new MappingException(
 //						"many-to-one attribute [" + locateAttribute().getName() + "] does not support orphan delete as it is not unique"
 //				);
 //			}
 //		}
 //		//TODO: validate that the entity reference is resolved
 //	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/EnumConversionHelper.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/EnumConversionHelper.java
index b8f8b7da4d..98ae17a720 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/EnumConversionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/EnumConversionHelper.java
@@ -1,124 +1,125 @@
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
 
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Set;
 import javax.persistence.CascadeType;
 import javax.persistence.GenerationType;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.id.MultipleHiLoPerTableGenerator;
 import org.hibernate.internal.util.collections.CollectionHelper;
 
 /**
  * Helper class which converts between different enum types.
  *
  * @author Hardy Ferentschik
  */
 public class EnumConversionHelper {
 	private EnumConversionHelper() {
 	}
 
 	public static String generationTypeToGeneratorStrategyName(GenerationType generatorEnum, boolean useNewGeneratorMappings) {
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
 
 	public static CascadeStyle cascadeTypeToCascadeStyle(CascadeType cascadeType) {
 		switch ( cascadeType ) {
 			case ALL: {
-				return CascadeStyle.ALL;
+				return CascadeStyles.ALL;
 			}
 			case PERSIST: {
-				return CascadeStyle.PERSIST;
+				return CascadeStyles.PERSIST;
 			}
 			case MERGE: {
-				return CascadeStyle.MERGE;
+				return CascadeStyles.MERGE;
 			}
 			case REMOVE: {
-				return CascadeStyle.DELETE;
+				return CascadeStyles.DELETE;
 			}
 			case REFRESH: {
-				return CascadeStyle.REFRESH;
+				return CascadeStyles.REFRESH;
 			}
 			case DETACH: {
-				return CascadeStyle.EVICT;
+				return CascadeStyles.EVICT;
 			}
 			default: {
 				throw new AssertionFailure( "Unknown cascade type" );
 			}
 		}
 	}
 
 	public static FetchMode annotationFetchModeToHibernateFetchMode(org.hibernate.annotations.FetchMode annotationFetchMode) {
 		switch ( annotationFetchMode ) {
 			case JOIN: {
 				return FetchMode.JOIN;
 			}
 			case SELECT: {
 				return FetchMode.SELECT;
 			}
 			case SUBSELECT: {
 				// todo - is this correct? can the conversion be made w/o any additional information, eg
 				// todo - association nature
 				return FetchMode.SELECT;
 			}
 			default: {
 				throw new AssertionFailure( "Unknown fetch mode" );
 			}
 		}
 	}
 
 	public static Set<CascadeStyle> cascadeTypeToCascadeStyleSet(Set<CascadeType> cascadeTypes) {
 		if ( CollectionHelper.isEmpty( cascadeTypes ) ) {
 			return Collections.emptySet();
 		}
 		Set<CascadeStyle> cascadeStyleSet = new HashSet<CascadeStyle>();
 		for ( CascadeType cascadeType : cascadeTypes ) {
 			cascadeStyleSet.add( cascadeTypeToCascadeStyle( cascadeType ) );
 		}
 		return cascadeStyleSet;
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
index 71dd55af9d..a9c09233e4 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
@@ -1,377 +1,378 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.internal.jaxb.mapping.hbm.CustomSqlElement;
 import org.hibernate.internal.jaxb.mapping.hbm.EntityElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbColumnElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbJoinedSubclassElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbMetaElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbParamElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbSubclassElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbUnionSubclassElement;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.MetaAttribute;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 
 /**
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class Helper {
 	public static final ExplicitHibernateTypeSource TO_ONE_ATTRIBUTE_TYPE_SOURCE = new ExplicitHibernateTypeSource() {
 		@Override
 		public String getName() {
 			return null;
 		}
 
 		@Override
 		public Map<String, String> getParameters() {
 			return null;
 		}
 	};
 
 	public static InheritanceType interpretInheritanceType(EntityElement entityElement) {
 		if ( JaxbSubclassElement.class.isInstance( entityElement ) ) {
 			return InheritanceType.SINGLE_TABLE;
 		}
 		else if ( JaxbJoinedSubclassElement.class.isInstance( entityElement ) ) {
 			return InheritanceType.JOINED;
 		}
 		else if ( JaxbUnionSubclassElement.class.isInstance( entityElement ) ) {
 			return InheritanceType.TABLE_PER_CLASS;
 		}
 		else {
 			return InheritanceType.NO_INHERITANCE;
 		}
 	}
 
 	/**
 	 * Given a user-specified description of how to perform custom SQL, build the {@link CustomSQL} representation.
 	 *
 	 * @param customSqlElement User-specified description of how to perform custom SQL
 	 *
 	 * @return The {@link CustomSQL} representation
 	 */
 	public static CustomSQL buildCustomSql(CustomSqlElement customSqlElement) {
 		if ( customSqlElement == null ) {
 			return null;
 		}
 		final ExecuteUpdateResultCheckStyle checkStyle = customSqlElement.getCheck() == null
 				? customSqlElement.isCallable()
 						? ExecuteUpdateResultCheckStyle.NONE
 						: ExecuteUpdateResultCheckStyle.COUNT
 				: ExecuteUpdateResultCheckStyle.fromExternalName( customSqlElement.getCheck().value() );
 		return new CustomSQL( customSqlElement.getValue(), customSqlElement.isCallable(), checkStyle );
 	}
 
 	/**
 	 * Given the user-specified entity mapping, determine the appropriate entity name
 	 *
 	 * @param entityElement The user-specified entity mapping
 	 * @param unqualifiedClassPackage The package to use for unqualified class names
 	 *
 	 * @return The appropriate entity name
 	 */
 	public static String determineEntityName(EntityElement entityElement, String unqualifiedClassPackage) {
 		return entityElement.getEntityName() != null
 				? entityElement.getEntityName()
 				: qualifyIfNeeded( entityElement.getName(), unqualifiedClassPackage );
 	}
 
 	/**
 	 * Qualify a (supposed class) name with the unqualified-class package name if it is not already qualified
 	 *
 	 * @param name The name
 	 * @param unqualifiedClassPackage The unqualified-class package name
 	 *
 	 * @return {@code null} if the incoming name was {@code null}; or the qualified name.
 	 */
 	public static String qualifyIfNeeded(String name, String unqualifiedClassPackage) {
 		if ( name == null ) {
 			return null;
 		}
 		if ( name.indexOf( '.' ) < 0 && unqualifiedClassPackage != null ) {
 			return unqualifiedClassPackage + '.' + name;
 		}
 		return name;
 	}
 
 	public static String getPropertyAccessorName(String access, boolean isEmbedded, String defaultAccess) {
 		return getStringValue( access, isEmbedded ? "embedded" : defaultAccess );
 	}
 
 	public static MetaAttributeContext extractMetaAttributeContext(
 			List<JaxbMetaElement> metaElementList,
 			boolean onlyInheritable,
 			MetaAttributeContext parentContext) {
 		final MetaAttributeContext subContext = new MetaAttributeContext( parentContext );
 
 		for ( JaxbMetaElement metaElement : metaElementList ) {
 			if ( onlyInheritable & !metaElement.isInherit() ) {
 				continue;
 			}
 
 			final String name = metaElement.getAttribute();
 			final MetaAttribute inheritedMetaAttribute = parentContext.getMetaAttribute( name );
 			MetaAttribute metaAttribute = subContext.getLocalMetaAttribute( name );
 			if ( metaAttribute == null || metaAttribute == inheritedMetaAttribute ) {
 				metaAttribute = new MetaAttribute( name );
 				subContext.add( metaAttribute );
 			}
 			metaAttribute.addValue( metaElement.getValue() );
 		}
 
 		return subContext;
 	}
 
 	public static String getStringValue(String value, String defaultValue) {
 		return value == null ? defaultValue : value;
 	}
 
 	public static int getIntValue(String value, int defaultValue) {
 		return value == null ? defaultValue : Integer.parseInt( value );
 	}
 
 	public static long getLongValue(String value, long defaultValue) {
 		return value == null ? defaultValue : Long.parseLong( value );
 	}
 
 	public static boolean getBooleanValue(Boolean value, boolean defaultValue) {
 		return value == null ? defaultValue : value;
 	}
 
 	public static Iterable<CascadeStyle> interpretCascadeStyles(String cascades, LocalBindingContext bindingContext) {
 		final Set<CascadeStyle> cascadeStyles = new HashSet<CascadeStyle>();
 		if ( StringHelper.isEmpty( cascades ) ) {
 			cascades = bindingContext.getMappingDefaults().getCascadeStyle();
 		}
 		for ( String cascade : StringHelper.split( ",", cascades ) ) {
-			cascadeStyles.add( CascadeStyle.getCascadeStyle( cascade ) );
+			cascadeStyles.add( CascadeStyles.getCascadeStyle( cascade ) );
 		}
 		return cascadeStyles;
 	}
 
 	public static Map<String, String> extractParameters(List<JaxbParamElement> xmlParamElements) {
 		if ( xmlParamElements == null || xmlParamElements.isEmpty() ) {
 			return null;
 		}
 		final HashMap<String,String> params = new HashMap<String, String>();
 		for ( JaxbParamElement paramElement : xmlParamElements ) {
 			params.put( paramElement.getName(), paramElement.getValue() );
 		}
 		return params;
 	}
 
 	public static Iterable<MetaAttributeSource> buildMetaAttributeSources(List<JaxbMetaElement> metaElements) {
 		ArrayList<MetaAttributeSource> result = new ArrayList<MetaAttributeSource>();
 		if ( metaElements == null || metaElements.isEmpty() ) {
 			// do nothing
 		}
 		else {
 			for ( final JaxbMetaElement metaElement : metaElements ) {
 				result.add(
 						new MetaAttributeSource() {
 							@Override
 							public String getName() {
 								return metaElement.getAttribute();
 							}
 
 							@Override
 							public String getValue() {
 								return metaElement.getValue();
 							}
 
 							@Override
 							public boolean isInheritable() {
 								return metaElement.isInherit();
 							}
 						}
 				);
 			}
 		}
 		return result;
 	}
 
 	public static Schema.Name determineDatabaseSchemaName(
 			String explicitSchemaName,
 			String explicitCatalogName,
 			LocalBindingContext bindingContext) {
 		return new Schema.Name(
 				resolveIdentifier(
 						explicitSchemaName,
 						bindingContext.getMappingDefaults().getSchemaName(),
 						bindingContext.isGloballyQuotedIdentifiers()
 				),
 				resolveIdentifier(
 						explicitCatalogName,
 						bindingContext.getMappingDefaults().getCatalogName(),
 						bindingContext.isGloballyQuotedIdentifiers()
 				)
 		);
 	}
 
 	public static Identifier resolveIdentifier(String explicitName, String defaultName, boolean globalQuoting) {
 		String name = StringHelper.isNotEmpty( explicitName ) ? explicitName : defaultName;
 		if ( globalQuoting ) {
 			name = StringHelper.quote( name );
 		}
 		return Identifier.toIdentifier( name );
 	}
 
     public static class ValueSourcesAdapter {
         public String getContainingTableName() {
             return null;
         }
 
         public boolean isIncludedInInsertByDefault() {
             return false;
         }
 
         public boolean isIncludedInUpdateByDefault() {
             return false;
         }
 
         public String getColumnAttribute() {
             return null;
         }
 
         public String getFormulaAttribute() {
             return null;
         }
 
         public List getColumnOrFormulaElements() {
             return null;
         }
 
         public boolean isForceNotNull() {
             return false;
         }
     }
 
     public static List<RelationalValueSource> buildValueSources(
 			ValueSourcesAdapter valueSourcesAdapter,
 			LocalBindingContext bindingContext) {
 		List<RelationalValueSource> result = new ArrayList<RelationalValueSource>();
 
 		if ( StringHelper.isNotEmpty( valueSourcesAdapter.getColumnAttribute() ) ) {
 			if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
 					&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
 				throw new org.hibernate.metamodel.source.MappingException(
 						"column/formula attribute may not be used together with <column>/<formula> subelement",
 						bindingContext.getOrigin()
 				);
 			}
 			if ( StringHelper.isNotEmpty( valueSourcesAdapter.getFormulaAttribute() ) ) {
 				throw new org.hibernate.metamodel.source.MappingException(
 						"column and formula attributes may not be used together",
 						bindingContext.getOrigin()
 				);
 			}
 			result.add(
 					new ColumnAttributeSourceImpl(
 							valueSourcesAdapter.getContainingTableName(),
 							valueSourcesAdapter.getColumnAttribute(),
 							valueSourcesAdapter.isIncludedInInsertByDefault(),
 							valueSourcesAdapter.isIncludedInUpdateByDefault(),
                             valueSourcesAdapter.isForceNotNull()
 					)
 			);
 		}
 		else if ( StringHelper.isNotEmpty( valueSourcesAdapter.getFormulaAttribute() ) ) {
 			if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
 					&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
 				throw new org.hibernate.metamodel.source.MappingException(
 						"column/formula attribute may not be used together with <column>/<formula> subelement",
 						bindingContext.getOrigin()
 				);
 			}
 			// column/formula attribute combo checked already
 			result.add(
 					new FormulaImpl(
 							valueSourcesAdapter.getContainingTableName(),
 							valueSourcesAdapter.getFormulaAttribute()
 					)
 			);
 		}
 		else if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
 				&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
 			for ( Object columnOrFormulaElement : valueSourcesAdapter.getColumnOrFormulaElements() ) {
 				if ( JaxbColumnElement.class.isInstance( columnOrFormulaElement ) ) {
 					result.add(
 							new ColumnSourceImpl(
 									valueSourcesAdapter.getContainingTableName(),
 									(JaxbColumnElement) columnOrFormulaElement,
 									valueSourcesAdapter.isIncludedInInsertByDefault(),
 									valueSourcesAdapter.isIncludedInUpdateByDefault(),
                                     valueSourcesAdapter.isForceNotNull()
 							)
 					);
 				}
 				else {
 					result.add(
 							new FormulaImpl(
 									valueSourcesAdapter.getContainingTableName(),
 									(String) columnOrFormulaElement
 							)
 					);
 				}
 			}
 		}
 		return result;
 	}
 
 	// todo : remove this once the state objects are cleaned up
 
 	public static Class classForName(String className, ServiceRegistry serviceRegistry) {
 		ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		try {
 			return classLoaderService.classForName( className );
 		}
 		catch ( ClassLoadingException e ) {
 			throw new MappingException( "Could not find class: " + className );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 36dc800e8e..fddd909167 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,2057 +1,2057 @@
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
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
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
-import org.hibernate.PropertyAccessException;
 import org.hibernate.QueryException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StructuredCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CascadeStyle;
-import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadeStyles;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext.NaturalIdHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FilterConfiguration;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.jdbc.TooManyRowsAffectedException;
 import org.hibernate.loader.entity.BatchingEntityLoader;
 import org.hibernate.loader.entity.CascadeEntityLoader;
 import org.hibernate.loader.entity.EntityLoader;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metamodel.binding.AssociationAttributeBinding;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.SimpleValueBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.Value;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
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
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.hibernate.type.VersionType;
 import org.jboss.logging.Logger;
 
 /**
  * Basic functionality for persisting an entity via JDBC
  * through either generated or custom SQL
  *
  * @author Gavin King
  */
 public abstract class AbstractEntityPersister
 		implements OuterJoinLoadable, Queryable, ClassMetadata, UniqueKeyLoadable,
 				   SQLLoadable, LazyPropertyInitializer, PostInsertIdentityPersister, Lockable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractEntityPersister.class.getName());
 
 	public static final String ENTITY_CLASS = "class";
 
 	// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final SessionFactoryImplementor factory;
 	private final EntityRegionAccessStrategy cacheAccessStrategy;
 	private final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy;
 	private final boolean isLazyPropertiesCacheable;
 	private final CacheEntryStructure cacheEntryStructure;
 	private final EntityMetamodel entityMetamodel;
 	private final EntityTuplizer entityTuplizer;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private final String[] rootTableKeyColumnNames;
 	private final String[] rootTableKeyColumnReaders;
 	private final String[] rootTableKeyColumnReaderTemplates;
 	private final String[] identifierAliases;
 	private final int identifierColumnSpan;
 	private final String versionColumnName;
 	private final boolean hasFormulaProperties;
 	private final int batchSize;
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
 
 	private final Set affectingFetchProfileNames = new HashSet();
 
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
 
 	private final String temporaryIdTableName;
 	private final String temporaryIdTableDDL;
 
 	private final Map subclassPropertyAliases = new HashMap();
 	private final Map subclassPropertyColumnNames = new HashMap();
 
 	protected final BasicEntityPropertyMapping propertyMapping;
 
 	protected void addDiscriminatorToInsert(Insert insert) {}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {}
 
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
 		return entityMetamodel.getSubclassEntityNames().contains(entityName);
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
 		for ( int i = 1; i < getTableSpan(); i++ ) {
 			result[i] = sqlLazyUpdateStrings[i];
 		}
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
 			boolean[] tableUpdateNeeded = new boolean[ getTableSpan() ];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				int property = dirtyProperties[i];
 				int table = propertyTableNumbers[property];
 				tableUpdateNeeded[table] = tableUpdateNeeded[table] ||
 						( getPropertyColumnSpan(property) > 0 && updateability[property] );
 			}
 			if ( isVersioned() ) {
 				tableUpdateNeeded[0] = tableUpdateNeeded[0] ||
 					Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
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
 
 	public AbstractEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final SessionFactoryImplementor factory) throws HibernateException {
 
 		// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		this.naturalIdRegionAccessStrategy = naturalIdRegionAccessStrategy;
 		isLazyPropertiesCacheable = persistentClass.isLazyPropertiesCacheable();
 		this.cacheEntryStructure = factory.getSettings().isStructuredCacheEntriesEnabled() ?
 				(CacheEntryStructure) new StructuredCacheEntry(this) :
 				(CacheEntryStructure) new UnstructuredCacheEntry();
 
 		this.entityMetamodel = new EntityMetamodel( persistentClass, factory );
 		this.entityTuplizer = this.entityMetamodel.getTuplizer();
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		int batch = persistentClass.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
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
 			Column col = ( Column ) iter.next();
 			rootTableKeyColumnNames[i] = col.getQuotedName( factory.getDialect() );
 			rootTableKeyColumnReaders[i] = col.getReadExpr( factory.getDialect() );
 			rootTableKeyColumnReaderTemplates[i] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			identifierAliases[i] = col.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( persistentClass.isVersioned() ) {
 			versionColumnName = ( ( Column ) persistentClass.getVersion().getColumnIterator().next() ).getQuotedName( factory.getDialect() );
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( persistentClass.getWhere() ) ? "( " + persistentClass.getWhere() + ") " : null;
 		sqlWhereStringTemplate = sqlWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( sqlWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		// PROPERTIES
 
 		final boolean lazyAvailable = isInstrumented();
 
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
 			Property prop = ( Property ) iter.next();
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
 				Selectable thing = ( Selectable ) colIter.next();
 				colAliases[k] = thing.getAlias( factory.getDialect() , prop.getValue().getTable() );
 				if ( thing.isFormula() ) {
 					foundFormula = true;
 					formulaTemplates[k] = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				}
 				else {
 					Column col = (Column)thing;
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
 			Property prop = ( Property ) iter.next();
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
 				Selectable thing = ( Selectable ) colIter.next();
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
 					Column col = (Column)thing;
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
 			subclassPropertyCascadeStyleClosure[j++] = ( CascadeStyle ) iter.next();
 		}
 		subclassPropertyFetchModeClosure = new FetchMode[joinedFetchesList.size()];
 		iter = joinedFetchesList.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyFetchModeClosure[j++] = ( FetchMode ) iter.next();
 		}
 
 		propertyDefinedOnSubclass = new boolean[definedBySubclass.size()];
 		iter = definedBySubclass.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			propertyDefinedOnSubclass[j++] = ( ( Boolean ) iter.next() ).booleanValue();
 		}
 
 		// Handle any filters applied to the class level
 		filterHelper = new FilterHelper( persistentClass.getFilters(), factory );
 
 		temporaryIdTableName = persistentClass.getTemporaryIdTableName();
 		temporaryIdTableDDL = persistentClass.getTemporaryIdTableDDL();
 	}
 
 
 	public AbstractEntityPersister(
 			final EntityBinding entityBinding,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final SessionFactoryImplementor factory) throws HibernateException {
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		this.naturalIdRegionAccessStrategy = naturalIdRegionAccessStrategy;
 		this.isLazyPropertiesCacheable =
 				entityBinding.getHierarchyDetails().getCaching() == null ?
 						false :
 						entityBinding.getHierarchyDetails().getCaching().isCacheLazyProperties();
 		this.cacheEntryStructure =
 				factory.getSettings().isStructuredCacheEntriesEnabled() ?
 						new StructuredCacheEntry(this) :
 						new UnstructuredCacheEntry();
 		this.entityMetamodel = new EntityMetamodel( entityBinding, factory );
 		this.entityTuplizer = this.entityMetamodel.getTuplizer();
 		int batch = entityBinding.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 		hasSubselectLoadableCollections = entityBinding.hasSubselectLoadableCollections();
 
 		propertyMapping = new BasicEntityPropertyMapping( this );
 
 		// IDENTIFIER
 
 		identifierColumnSpan = entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding().getSimpleValueSpan();
 		rootTableKeyColumnNames = new String[identifierColumnSpan];
 		rootTableKeyColumnReaders = new String[identifierColumnSpan];
 		rootTableKeyColumnReaderTemplates = new String[identifierColumnSpan];
 		identifierAliases = new String[identifierColumnSpan];
 
 		rowIdName = entityBinding.getRowId();
 
 		loaderName = entityBinding.getCustomLoaderName();
 
 		int i = 0;
 		for ( org.hibernate.metamodel.relational.Column col : entityBinding.getPrimaryTable().getPrimaryKey().getColumns() ) {
 			rootTableKeyColumnNames[i] = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 			if ( col.getReadFragment() == null ) {
 				rootTableKeyColumnReaders[i] = rootTableKeyColumnNames[i];
 				rootTableKeyColumnReaderTemplates[i] = getTemplateFromColumn( col, factory );
 			}
 			else {
 				rootTableKeyColumnReaders[i] = col.getReadFragment();
 				rootTableKeyColumnReaderTemplates[i] = getTemplateFromString( col.getReadFragment(), factory );
 			}
 			identifierAliases[i] = col.getAlias( factory.getDialect() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( entityBinding.isVersioned() ) {
 			final Value versioningValue = entityBinding.getHierarchyDetails().getVersioningAttributeBinding().getValue();
 			if ( ! org.hibernate.metamodel.relational.Column.class.isInstance( versioningValue ) ) {
 				throw new AssertionFailure( "Bad versioning attribute binding : " + versioningValue );
 			}
 			org.hibernate.metamodel.relational.Column versionColumn = org.hibernate.metamodel.relational.Column.class.cast( versioningValue );
 			versionColumnName = versionColumn.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( entityBinding.getWhereFilter() ) ? "( " + entityBinding.getWhereFilter() + ") " : null;
 		sqlWhereStringTemplate = getTemplateFromString( sqlWhereString, factory );
 
 		// PROPERTIES
 
 		final boolean lazyAvailable = isInstrumented();
 
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
 
 		i = 0;
 		boolean foundFormula = false;
 		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() ) {
 				// entity identifier is not considered a "normal" property
 				continue;
 			}
 
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				// collections handled separately
 				continue;
 			}
 
 			final SingularAttributeBinding singularAttributeBinding = (SingularAttributeBinding) attributeBinding;
 
 			thisClassProperties.add( singularAttributeBinding );
 
 			propertySubclassNames[i] = ( (EntityBinding) singularAttributeBinding.getContainer() ).getEntity().getName();
 
 			int span = singularAttributeBinding.getSimpleValueSpan();
 			propertyColumnSpans[i] = span;
 
 			String[] colNames = new String[span];
 			String[] colAliases = new String[span];
 			String[] colReaderTemplates = new String[span];
 			String[] colWriters = new String[span];
 			String[] formulaTemplates = new String[span];
 			boolean[] propertyColumnInsertability = new boolean[span];
 			boolean[] propertyColumnUpdatability = new boolean[span];
 
 			int k = 0;
 
 			for ( SimpleValueBinding valueBinding : singularAttributeBinding.getSimpleValueBindings() ) {
 				colAliases[k] = valueBinding.getSimpleValue().getAlias( factory.getDialect() );
 				if ( valueBinding.isDerived() ) {
 					foundFormula = true;
 					formulaTemplates[ k ] = getTemplateFromString( ( (DerivedValue) valueBinding.getSimpleValue() ).getExpression(), factory );
 				}
 				else {
 					org.hibernate.metamodel.relational.Column col = ( org.hibernate.metamodel.relational.Column ) valueBinding.getSimpleValue();
 					colNames[k] = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 					colReaderTemplates[k] = getTemplateFromColumn( col, factory );
 					colWriters[k] = col.getWriteFragment() == null ? "?" : col.getWriteFragment();
 				}
 				propertyColumnInsertability[k] = valueBinding.isIncludeInInsert();
 				propertyColumnUpdatability[k] = valueBinding.isIncludeInUpdate();
 				k++;
 			}
 			propertyColumnNames[i] = colNames;
 			propertyColumnFormulaTemplates[i] = formulaTemplates;
 			propertyColumnReaderTemplates[i] = colReaderTemplates;
 			propertyColumnWriters[i] = colWriters;
 			propertyColumnAliases[i] = colAliases;
 
 			propertyColumnUpdateable[i] = propertyColumnUpdatability;
 			propertyColumnInsertable[i] = propertyColumnInsertability;
 
 			if ( lazyAvailable && singularAttributeBinding.isLazy() ) {
 				lazyProperties.add( singularAttributeBinding.getAttribute().getName() );
 				lazyNames.add( singularAttributeBinding.getAttribute().getName() );
 				lazyNumbers.add( i );
 				lazyTypes.add( singularAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping());
 				lazyColAliases.add( colAliases );
 			}
 
 
 			// TODO: fix this when backrefs are working
 			//propertySelectable[i] = singularAttributeBinding.isBackRef();
 			propertySelectable[i] = true;
 
 			propertyUniqueness[i] = singularAttributeBinding.isAlternateUniqueKey();
 
 			i++;
 
 		}
 		hasFormulaProperties = foundFormula;
 		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
 		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
 		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
 		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );
 
 		// SUBCLASS PROPERTY CLOSURE
 
 		List<String> columns = new ArrayList<String>();
 		List<Boolean> columnsLazy = new ArrayList<Boolean>();
 		List<String> columnReaderTemplates = new ArrayList<String>();
 		List<String> aliases = new ArrayList<String>();
 		List<String> formulas = new ArrayList<String>();
 		List<String> formulaAliases = new ArrayList<String>();
 		List<String> formulaTemplates = new ArrayList<String>();
 		List<Boolean> formulasLazy = new ArrayList<Boolean>();
 		List<Type> types = new ArrayList<Type>();
 		List<String> names = new ArrayList<String>();
 		List<String> classes = new ArrayList<String>();
 		List<String[]> templates = new ArrayList<String[]>();
 		List<String[]> propColumns = new ArrayList<String[]>();
 		List<String[]> propColumnReaders = new ArrayList<String[]>();
 		List<String[]> propColumnReaderTemplates = new ArrayList<String[]>();
 		List<FetchMode> joinedFetchesList = new ArrayList<FetchMode>();
 		List<CascadeStyle> cascades = new ArrayList<CascadeStyle>();
 		List<Boolean> definedBySubclass = new ArrayList<Boolean>();
 		List<int[]> propColumnNumbers = new ArrayList<int[]>();
 		List<int[]> propFormulaNumbers = new ArrayList<int[]>();
 		List<Boolean> columnSelectables = new ArrayList<Boolean>();
 		List<Boolean> propNullables = new ArrayList<Boolean>();
 
 		for ( AttributeBinding attributeBinding : entityBinding.getSubEntityAttributeBindingClosure() ) {
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() ) {
 				// entity identifier is not considered a "normal" property
 				continue;
 			}
 
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				// collections handled separately
 				continue;
 			}
 
 			final SingularAttributeBinding singularAttributeBinding = (SingularAttributeBinding) attributeBinding;
 
 			names.add( singularAttributeBinding.getAttribute().getName() );
 			classes.add( ( (EntityBinding) singularAttributeBinding.getContainer() ).getEntity().getName() );
 			boolean isDefinedBySubclass = ! thisClassProperties.contains( singularAttributeBinding );
 			definedBySubclass.add( isDefinedBySubclass );
 			propNullables.add( singularAttributeBinding.isNullable() || isDefinedBySubclass ); //TODO: is this completely correct?
 			types.add( singularAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 
 			final int span = singularAttributeBinding.getSimpleValueSpan();
 			String[] cols = new String[ span ];
 			String[] readers = new String[ span ];
 			String[] readerTemplates = new String[ span ];
 			String[] forms = new String[ span ];
 			int[] colnos = new int[ span ];
 			int[] formnos = new int[ span ];
 			int l = 0;
 			Boolean lazy = singularAttributeBinding.isLazy() && lazyAvailable;
 			for ( SimpleValueBinding valueBinding : singularAttributeBinding.getSimpleValueBindings() ) {
 				if ( valueBinding.isDerived() ) {
 					DerivedValue derivedValue = DerivedValue.class.cast( valueBinding.getSimpleValue() );
 					String template = getTemplateFromString( derivedValue.getExpression(), factory );
 					formnos[l] = formulaTemplates.size();
 					colnos[l] = -1;
 					formulaTemplates.add( template );
 					forms[l] = template;
 					formulas.add( derivedValue.getExpression() );
 					formulaAliases.add( derivedValue.getAlias( factory.getDialect() ) );
 					formulasLazy.add( lazy );
 				}
 				else {
 					org.hibernate.metamodel.relational.Column col = org.hibernate.metamodel.relational.Column.class.cast( valueBinding.getSimpleValue() );
 					String colName = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 					colnos[l] = columns.size(); //before add :-)
 					formnos[l] = -1;
 					columns.add( colName );
 					cols[l] = colName;
 					aliases.add( col.getAlias( factory.getDialect() ) );
 					columnsLazy.add( lazy );
 					// TODO: properties only selectable if they are non-plural???
 					columnSelectables.add( singularAttributeBinding.getAttribute().isSingular() );
 
 					readers[l] =
 							col.getReadFragment() == null ?
 									col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() ) :
 									col.getReadFragment();
 					String readerTemplate = getTemplateFromColumn( col, factory );
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
 
 			if ( singularAttributeBinding.isAssociation() ) {
 				AssociationAttributeBinding associationAttributeBinding =
 						( AssociationAttributeBinding ) singularAttributeBinding;
 				cascades.add( associationAttributeBinding.getCascadeStyle() );
 				joinedFetchesList.add( associationAttributeBinding.getFetchMode() );
 			}
 			else {
-				cascades.add( CascadeStyle.NONE );
+				cascades.add( CascadeStyles.NONE );
 				joinedFetchesList.add( FetchMode.SELECT );
 			}
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
 
 		subclassPropertyCascadeStyleClosure = cascades.toArray( new CascadeStyle[ cascades.size() ] );
 		subclassPropertyFetchModeClosure = joinedFetchesList.toArray( new FetchMode[ joinedFetchesList.size() ] );
 
 		propertyDefinedOnSubclass = ArrayHelper.toBooleanArray( definedBySubclass );
 
 		List<FilterConfiguration> filterDefaultConditions = new ArrayList<FilterConfiguration>();
 		for ( FilterDefinition filterDefinition : entityBinding.getFilterDefinitions() ) {
 			filterDefaultConditions.add(new FilterConfiguration(filterDefinition.getFilterName(), 
 						filterDefinition.getDefaultFilterCondition(), true, null, null, null));
 		}
 		filterHelper = new FilterHelper( filterDefaultConditions, factory);
 
 		temporaryIdTableName = null;
 		temporaryIdTableDDL = null;
 	}
 
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
 						rs = ps.executeQuery();
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
 						rs.close();
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					ps.close();
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
 				ResultSet rs = ps.executeQuery();
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
 					rs.close();
 				}
 			}
 			finally {
 				ps.close();
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
 				ResultSet rs = ps.executeQuery();
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					return (Serializable) getIdentifierType().nullSafeGet( rs, getIdentifierAliases(), session, null );
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				ps.close();
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
 				int rows = st.executeUpdate();
 				if ( rows != 1 ) {
 					throw new StaleObjectStateException( getEntityName(), id );
 				}
 			}
 			finally {
 				st.close();
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
 				ResultSet rs = st.executeQuery();
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
 					rs.close();
 				}
 			}
 			finally {
 				st.close();
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
 
@@ -2787,2005 +2787,2005 @@ public abstract class AbstractEntityPersister
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
 				sequentialResultSet.close();
 			}
 
 			return values;
 
 		}
 		finally {
 			if ( sequentialSelect != null ) {
 				sequentialSelect.close();
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
 				dehydrate( null, fields, notNull, propertyColumnInsertable, 0, ps, session );
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
 
 				dehydrate( id, fields, null, notNull, propertyColumnInsertable, j, insert, session, index );
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( inserBatchKey ).addToBatch();
 				}
 				else {
 					expectation.verifyOutcome( insert.executeUpdate(), insert, -1 );
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
 					insert.close();
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
 
 	}
 
 	private BasicBatchKey updateBatchKey;
 
 	protected boolean update(
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
 
 		final Expectation expectation = Expectations.appropriateExpectation( updateResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && expectation.canBeBatched() && isBatchable(); //note: updates to joined tables can't be batched...
 		if ( useBatch && updateBatchKey == null ) {
 			updateBatchKey = new BasicBatchKey(
 					getEntityName() + "#UPDATE",
 					expectation
 			);
 		}
 		final boolean callable = isUpdateCallable( j );
 		final boolean useVersion = j == 0 && isVersioned();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Updating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion )
 				LOG.tracev( "Existing version: {0} -> New version:{1}", oldVersion, fields[getVersionProperty()] );
 		}
 
 		try {
 			int index = 1; // starting index
 			final PreparedStatement update;
 			if ( useBatch ) {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( updateBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				index+= expectation.prepare( update );
 
 				//Now write the values of fields onto the prepared statement
 				index = dehydrate( id, fields, rowId, includeProperty, propertyColumnUpdateable, j, update, session, index );
 
 				// Write any appropriate versioning conditional parameters
 				if ( useVersion && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 					if ( checkVersion( includeProperty ) ) {
 						getVersionType().nullSafeSet( update, oldVersion, index, session );
 					}
 				}
 				else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 					boolean[] versionability = getPropertyVersionability(); //TODO: is this really necessary????
 					boolean[] includeOldField = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
 							? getPropertyUpdateability()
 							: includeProperty;
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						boolean include = includeOldField[i] &&
 								isPropertyOfTable( i, j ) &&
 								versionability[i]; //TODO: is this really necessary????
 						if ( include ) {
 							boolean[] settable = types[i].toColumnNullness( oldFields[i], getFactory() );
 							types[i].nullSafeSet(
 									update,
 									oldFields[i],
 									index,
 									settable,
 									session
 								);
 							index += ArrayHelper.countTrue(settable);
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( updateBatchKey ).addToBatch();
 					return true;
 				}
 				else {
 					return check( update.executeUpdate(), id, j, expectation, update );
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
 					update.close();
 				}
 			}
 
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not update: " + MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 		}
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	/**
 	 * Perform an SQL DELETE
 	 */
 	protected void delete(
 			final Serializable id,
 			final Object version,
 			final int j,
 			final Object object,
 			final String sql,
 			final SessionImplementor session,
 			final Object[] loadedState) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		final boolean useVersion = j == 0 && isVersioned();
 		final boolean callable = isDeleteCallable( j );
 		final Expectation expectation = Expectations.appropriateExpectation( deleteResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && isBatchable() && expectation.canBeBatched();
 		if ( useBatch && deleteBatchKey == null ) {
 			deleteBatchKey = new BasicBatchKey(
 					getEntityName() + "#DELETE",
 					expectation
 			);
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Deleting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion )
 				LOG.tracev( "Version: {0}", version );
 		}
 
 		if ( isTableCascadeDeleteEnabled( j ) ) {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Delete handled by foreign key constraint: {0}", getTableName( j ) );
 			}
 			return; //EARLY EXIT!
 		}
 
 		try {
 			//Render the SQL query
 			PreparedStatement delete;
 			int index = 1;
 			if ( useBatch ) {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( deleteBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 
 				index += expectation.prepare( delete );
 
 				// Do the key. The key is immutable so we can use the _current_ object state - not necessarily
 				// the state at the time the delete was issued
 				getIdentifierType().nullSafeSet( delete, id, index, session );
 				index += getIdentifierColumnSpan();
 
 				// We should use the _current_ object state (ie. after any updates that occurred during flush)
 
 				if ( useVersion ) {
 					getVersionType().nullSafeSet( delete, version, index, session );
 				}
 				else if ( isAllOrDirtyOptLocking() && loadedState != null ) {
 					boolean[] versionability = getPropertyVersionability();
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 							// this property belongs to the table and it is not specifically
 							// excluded from optimistic locking by optimistic-lock="false"
 							boolean[] settable = types[i].toColumnNullness( loadedState[i], getFactory() );
 							types[i].nullSafeSet( delete, loadedState[i], index, settable, session );
 							index += ArrayHelper.countTrue( settable );
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( deleteBatchKey ).addToBatch();
 				}
 				else {
 					check( delete.executeUpdate(), id, j, expectation, delete );
 				}
 
 			}
 			catch ( SQLException sqle ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw sqle;
 			}
 			finally {
 				if ( !useBatch ) {
 					delete.close();
 				}
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not delete: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 
 		}
 
 	}
 
 	private String[] getUpdateStrings(boolean byRowId, boolean lazy) {
 		if ( byRowId ) {
 			return lazy ? getSQLLazyUpdateByRowIdStrings() : getSQLUpdateByRowIdStrings();
 		}
 		else {
 			return lazy ? getSQLLazyUpdateStrings() : getSQLUpdateStrings();
 		}
 	}
 
 	/**
 	 * Update an object
 	 */
 	public void update(
 			final Serializable id,
 	        final Object[] fields,
 	        final int[] dirtyFields,
 	        final boolean hasDirtyCollection,
 	        final Object[] oldFields,
 	        final Object oldVersion,
 	        final Object object,
 	        final Object rowId,
 	        final SessionImplementor session) throws HibernateException {
 
 		//note: dirtyFields==null means we had no snapshot, and we couldn't get one using select-before-update
 		//	  oldFields==null just means we had no snapshot to begin with (we might have used select-before-update to get the dirtyFields)
 
 		final boolean[] tableUpdateNeeded = getTableUpdateNeeded( dirtyFields, hasDirtyCollection );
 		final int span = getTableSpan();
 
 		final boolean[] propsToUpdate;
 		final String[] updateStrings;
 		EntityEntry entry = session.getPersistenceContext().getEntry( object );
 
 		// Ensure that an immutable or non-modifiable entity is not being updated unless it is
 		// in the process of being deleted.
 		if ( entry == null && ! isMutable() ) {
 			throw new IllegalStateException( "Updating immutable entity that is not in session yet!" );
 		}
 		if ( ( entityMetamodel.isDynamicUpdate() && dirtyFields != null ) ) {
 			// We need to generate the UPDATE SQL when dynamic-update="true"
 			propsToUpdate = getPropertiesToUpdate( dirtyFields, hasDirtyCollection );
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else if ( ! isModifiableEntity( entry ) ) {
 			// We need to generate UPDATE SQL when a non-modifiable entity (e.g., read-only or immutable)
 			// needs:
 			// - to have references to transient entities set to null before being deleted
 			// - to have version incremented do to a "dirty" association
 			// If dirtyFields == null, then that means that there are no dirty properties to
 			// to be updated; an empty array for the dirty fields needs to be passed to
 			// getPropertiesToUpdate() instead of null.
 			propsToUpdate = getPropertiesToUpdate(
 					( dirtyFields == null ? ArrayHelper.EMPTY_INT_ARRAY : dirtyFields ),
 					hasDirtyCollection
 			);
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else {
 			// For the case of dynamic-update="false", or no snapshot, we use the static SQL
 			updateStrings = getUpdateStrings(
 					rowId != null,
 					hasUninitializedLazyProperties( object )
 			);
 			propsToUpdate = getPropertyUpdateability( object );
 		}
 
 		for ( int j = 0; j < span; j++ ) {
 			// Now update only the tables with dirty properties (and the table with the version number)
 			if ( tableUpdateNeeded[j] ) {
 				updateOrInsert(
 						id,
 						fields,
 						oldFields,
 						j == 0 ? rowId : null,
 						propsToUpdate,
 						j,
 						oldVersion,
 						object,
 						updateStrings[j],
 						session
 					);
 			}
 		}
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		final Serializable id;
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			id = insert( fields, notNull, generateInsertString( true, notNull ), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			id = insert( fields, getPropertyInsertability(), getSQLIdentityInsertString(), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 		return id;
 	}
 
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 	}
 
 	/**
 	 * Delete an object
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 			throws HibernateException {
 		final int span = getTableSpan();
 		boolean isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && isAllOrDirtyOptLocking();
 		Object[] loadedState = null;
 		if ( isImpliedOptimisticLocking ) {
 			// need to treat this as if it where optimistic-lock="all" (dirty does *not* make sense);
 			// first we need to locate the "loaded" state
 			//
 			// Note, it potentially could be a proxy, so doAfterTransactionCompletion the location the safe way...
 			final EntityKey key = session.generateEntityKey( id, this );
 			Object entity = session.getPersistenceContext().getEntity( key );
 			if ( entity != null ) {
 				EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 				loadedState = entry.getLoadedState();
 			}
 		}
 
 		final String[] deleteStrings;
 		if ( isImpliedOptimisticLocking && loadedState != null ) {
 			// we need to utilize dynamic delete statements
 			deleteStrings = generateSQLDeletStrings( loadedState );
 		}
 		else {
 			// otherwise, utilize the static delete statements
 			deleteStrings = getSQLDeleteStrings();
 		}
 
 		for ( int j = span - 1; j >= 0; j-- ) {
 			delete( id, version, j, object, deleteStrings[j], session, loadedState );
 		}
 
 	}
 
 	private boolean isAllOrDirtyOptLocking() {
 		return entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.DIRTY
 				|| entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL;
 	}
 
 	private String[] generateSQLDeletStrings(Object[] loadedState) {
 		int span = getTableSpan();
 		String[] deleteStrings = new String[span];
 		for ( int j = span - 1; j >= 0; j-- ) {
 			Delete delete = new Delete()
 					.setTableName( getTableName( j ) )
 					.addPrimaryKeyColumns( getKeyColumns( j ) );
 			if ( getFactory().getSettings().isCommentsEnabled() ) {
 				delete.setComment( "delete " + getEntityName() + " [" + j + "]" );
 			}
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 					// this property belongs to the table and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( loadedState[i], getFactory() );
 					for ( int k = 0; k < propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							delete.addWhereFragment( propertyColumnNames[k] + " = ?" );
 						}
 						else {
 							delete.addWhereFragment( propertyColumnNames[k] + " is null" );
 						}
 					}
 				}
 			}
 			deleteStrings[j] = delete.toStatementString();
 		}
 		return deleteStrings;
 	}
 
 	protected void logStaticSQL() {
         if ( LOG.isDebugEnabled() ) {
             LOG.debugf( "Static SQL for entity: %s", getEntityName() );
             if ( sqlLazySelectString != null ) {
 				LOG.debugf( " Lazy select: %s", sqlLazySelectString );
 			}
             if ( sqlVersionSelectString != null ) {
 				LOG.debugf( " Version select: %s", sqlVersionSelectString );
 			}
             if ( sqlSnapshotSelectString != null ) {
 				LOG.debugf( " Snapshot select: %s", sqlSnapshotSelectString );
 			}
 			for ( int j = 0; j < getTableSpan(); j++ ) {
                 LOG.debugf( " Insert %s: %s", j, getSQLInsertStrings()[j] );
                 LOG.debugf( " Update %s: %s", j, getSQLUpdateStrings()[j] );
                 LOG.debugf( " Delete %s: %s", j, getSQLDeleteStrings()[j] );
 			}
             if ( sqlIdentityInsertString != null ) {
 				LOG.debugf( " Identity insert: %s", sqlIdentityInsertString );
 			}
             if ( sqlUpdateByRowIdString != null ) {
 				LOG.debugf( " Update by row id (all fields): %s", sqlUpdateByRowIdString );
 			}
             if ( sqlLazyUpdateByRowIdString != null ) {
 				LOG.debugf( " Update by row id (non-lazy fields): %s", sqlLazyUpdateByRowIdString );
 			}
             if ( sqlInsertGeneratedValuesSelectString != null ) {
 				LOG.debugf( " Insert-generated property select: %s", sqlInsertGeneratedValuesSelectString );
 			}
             if ( sqlUpdateGeneratedValuesSelectString != null ) {
 				LOG.debugf( " Update-generated property select: %s", sqlUpdateGeneratedValuesSelectString );
 			}
 		}
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		final StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return rootAlias;
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toFromFragmentString();
 	}
 
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toWhereFragmentString();
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return false;
 	}
 
 	protected JoinFragment createJoin(String name, boolean innerJoin, boolean includeSubclasses) {
 		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() ); //all joins join to the pk of the driving table
 		final JoinFragment join = getFactory().getDialect().createOuterJoinFragment();
 		final int tableSpan = getSubclassTableSpan();
 		for ( int j = 1; j < tableSpan; j++ ) { //notice that we skip the first table; it is the driving table!
 			final boolean joinIsIncluded = isClassOrSuperclassTable( j ) ||
 					( includeSubclasses && !isSubclassTableSequentialSelect( j ) && !isSubclassTableLazy( j ) );
 			if ( joinIsIncluded ) {
 				join.addJoin( getSubclassTableName( j ),
 						generateTableAlias( name, j ),
 						idCols,
 						getSubclassTableKeyColumns( j ),
 						innerJoin && isClassOrSuperclassTable( j ) && !isInverseTable( j ) && !isNullableTable( j ) ?
 						JoinType.INNER_JOIN : //we can inner join to superclass tables (the row MUST be there)
 						JoinType.LEFT_OUTER_JOIN //we can never inner join to subclass tables
 					);
 			}
 		}
 		return join;
 	}
 
 	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
 		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
 		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
 		for ( int i = 1; i < tableNumbers.length; i++ ) { //skip the driving table
 			final int j = tableNumbers[i];
 			jf.addJoin( getSubclassTableName( j ),
 					generateTableAlias( getRootAlias(), j ),
 					keyCols,
 					getSubclassTableKeyColumns( j ),
 					isInverseSubclassTable( j ) || isNullableSubclassTable( j ) ?
 					JoinType.LEFT_OUTER_JOIN :
 					JoinType.INNER_JOIN );
 		}
 		return jf;
 	}
 
 	protected SelectFragment createSelect(final int[] subclassColumnNumbers,
 										  final int[] subclassFormulaNumbers) {
 
 		SelectFragment selectFragment = new SelectFragment();
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < subclassColumnNumbers.length; i++ ) {
 			int columnNumber = subclassColumnNumbers[i];
 			if ( subclassColumnSelectableClosure[columnNumber] ) {
 				final String subalias = generateTableAlias( getRootAlias(), columnTableNumbers[columnNumber] );
 				selectFragment.addColumnTemplate( subalias, columnReaderTemplates[columnNumber], columnAliases[columnNumber] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < subclassFormulaNumbers.length; i++ ) {
 			int formulaNumber = subclassFormulaNumbers[i];
 			final String subalias = generateTableAlias( getRootAlias(), formulaTableNumbers[formulaNumber] );
 			selectFragment.addFormula( subalias, formulaTemplates[formulaNumber], formulaAliases[formulaNumber] );
 		}
 
 		return selectFragment;
 	}
 
 	protected String createFrom(int tableNumber, String alias) {
 		return getSubclassTableName( tableNumber ) + ' ' + alias;
 	}
 
 	protected String createWhereByKey(int tableNumber, String alias) {
 		//TODO: move to .sql package, and refactor with similar things!
 		return StringHelper.join( "=? and ",
 				StringHelper.qualify( alias, getSubclassTableKeyColumns( tableNumber ) ) ) + "=?";
 	}
 
 	protected String renderSelect(
 			final int[] tableNumbers,
 	        final int[] columnNumbers,
 	        final int[] formulaNumbers) {
 
 		Arrays.sort( tableNumbers ); //get 'em in the right order (not that it really matters)
 
 		//render the where and from parts
 		int drivingTable = tableNumbers[0];
 		final String drivingAlias = generateTableAlias( getRootAlias(), drivingTable ); //we *could* regerate this inside each called method!
 		final String where = createWhereByKey( drivingTable, drivingAlias );
 		final String from = createFrom( drivingTable, drivingAlias );
 
 		//now render the joins
 		JoinFragment jf = createJoin( tableNumbers, drivingAlias );
 
 		//now render the select clause
 		SelectFragment selectFragment = createSelect( columnNumbers, formulaNumbers );
 
 		//now tie it all together
 		Select select = new Select( getFactory().getDialect() );
 		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 		select.setFromClause( from );
 		select.setWhereClause( where );
 		select.setOuterJoins( jf.toFromFragmentString(), jf.toWhereFragmentString() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "sequential select " + getEntityName() );
 		}
 		return select.toStatementString();
 	}
 
 	private String getRootAlias() {
 		return StringHelper.generateAlias( getEntityName() );
 	}
 
 	protected void postConstruct(Mapping mapping) throws MappingException {
 		initPropertyPaths(mapping);
 
 		//insert/update/delete SQL
 		final int joinSpan = getTableSpan();
 		sqlDeleteStrings = new String[joinSpan];
 		sqlInsertStrings = new String[joinSpan];
 		sqlUpdateStrings = new String[joinSpan];
 		sqlLazyUpdateStrings = new String[joinSpan];
 
 		sqlUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getPropertyUpdateability(), 0, true );
 		sqlLazyUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getNonLazyPropertyUpdateability(), 0, true );
 
 		for ( int j = 0; j < joinSpan; j++ ) {
 			sqlInsertStrings[j] = customSQLInsert[j] == null ?
 					generateInsertString( getPropertyInsertability(), j ) :
 					customSQLInsert[j];
 			sqlUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlLazyUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getNonLazyPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlDeleteStrings[j] = customSQLDelete[j] == null ?
 					generateDeleteString( j ) :
 					customSQLDelete[j];
 		}
 
 		tableHasColumns = new boolean[joinSpan];
 		for ( int j = 0; j < joinSpan; j++ ) {
 			tableHasColumns[j] = sqlUpdateStrings[j] != null;
 		}
 
 		//select SQL
 		sqlSnapshotSelectString = generateSnapshotSelectString();
 		sqlLazySelectString = generateLazySelectString();
 		sqlVersionSelectString = generateSelectVersionString();
 		if ( hasInsertGeneratedProperties() ) {
 			sqlInsertGeneratedValuesSelectString = generateInsertGeneratedValuesSelectString();
 		}
 		if ( hasUpdateGeneratedProperties() ) {
 			sqlUpdateGeneratedValuesSelectString = generateUpdateGeneratedValuesSelectString();
 		}
 		if ( isIdentifierAssignedByInsert() ) {
 			identityDelegate = ( ( PostInsertIdentifierGenerator ) getIdentifierGenerator() )
 					.getInsertGeneratedIdentifierDelegate( this, getFactory().getDialect(), useGetGeneratedKeys() );
 			sqlIdentityInsertString = customSQLInsert[0] == null
 					? generateIdentityInsertString( getPropertyInsertability() )
 					: customSQLInsert[0];
 		}
 		else {
 			sqlIdentityInsertString = null;
 		}
 
 		logStaticSQL();
 
 	}
 
 	public void postInstantiate() throws MappingException {
 		createLoaders();
 		createUniqueKeyLoaders();
 		createQueryLoader();
 
 	}
 
 	//needed by subclasses to override the createLoader strategy
 	protected Map getLoaders() {
 		return loaders;
 	}
 
 	//Relational based Persisters should be content with this implementation
 	protected void createLoaders() {
 		final Map loaders = getLoaders();
 		loaders.put( LockMode.NONE, createEntityLoader( LockMode.NONE ) );
 
 		UniqueEntityLoader readLoader = createEntityLoader( LockMode.READ );
 		loaders.put( LockMode.READ, readLoader );
 
 		//TODO: inexact, what we really need to know is: are any outer joins used?
 		boolean disableForUpdate = getSubclassTableSpan() > 1 &&
 				hasSubclasses() &&
 				!getFactory().getDialect().supportsOuterJoinForUpdate();
 
 		loaders.put(
 				LockMode.UPGRADE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE )
 			);
 		loaders.put(
 				LockMode.UPGRADE_NOWAIT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_NOWAIT )
 			);
 		loaders.put(
 				LockMode.FORCE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.FORCE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_READ,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_READ )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_WRITE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_WRITE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_FORCE_INCREMENT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_FORCE_INCREMENT )
 			);
 		loaders.put( LockMode.OPTIMISTIC, createEntityLoader( LockMode.OPTIMISTIC) );
 		loaders.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, createEntityLoader(LockMode.OPTIMISTIC_FORCE_INCREMENT) );
 
 		loaders.put(
 				"merge",
-				new CascadeEntityLoader( this, CascadingAction.MERGE, getFactory() )
+				new CascadeEntityLoader( this, CascadingActions.MERGE, getFactory() )
 			);
 		loaders.put(
 				"refresh",
-				new CascadeEntityLoader( this, CascadingAction.REFRESH, getFactory() )
+				new CascadeEntityLoader( this, CascadingActions.REFRESH, getFactory() )
 			);
 	}
 
 	protected void createQueryLoader() {
 		if ( loaderName != null ) {
 			queryLoader = new NamedQueryLoader( loaderName, this );
 		}
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 		return load( id, optionalObject, new LockOptions().setLockMode(lockMode), session );
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 			throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Fetching entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		final UniqueEntityLoader loader = getAppropriateLoader(lockOptions, session );
 		return loader.load( id, optionalObject, session, lockOptions );
 	}
 
 	public void registerAffectingFetchProfile(String fetchProfileName) {
 		affectingFetchProfileNames.add( fetchProfileName );
 	}
 
 	private boolean isAffectedByEnabledFetchProfiles(SessionImplementor session) {
 		Iterator itr = session.getLoadQueryInfluencers().getEnabledFetchProfileNames().iterator();
 		while ( itr.hasNext() ) {
 			if ( affectingFetchProfileNames.contains( itr.next() ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() );
 	}
 
 	private UniqueEntityLoader getAppropriateLoader(LockOptions lockOptions, SessionImplementor session) {
 		if ( queryLoader != null ) {
 			// if the user specified a custom query loader we need to that
 			// regardless of any other consideration
 			return queryLoader;
 		}
 		else if ( isAffectedByEnabledFilters( session ) ) {
 			// because filters affect the rows returned (because they add
 			// restrictions) these need to be next in precedence
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( session.getLoadQueryInfluencers().getInternalFetchProfile() != null && LockMode.UPGRADE.greaterThan( lockOptions.getLockMode() ) ) {
 			// Next, we consider whether an 'internal' fetch profile has been set.
 			// This indicates a special fetch profile Hibernate needs applied
 			// (for its merge loading process e.g.).
 			return ( UniqueEntityLoader ) getLoaders().get( session.getLoadQueryInfluencers().getInternalFetchProfile() );
 		}
 		else if ( isAffectedByEnabledFetchProfiles( session ) ) {
 			// If the session has associated influencers we need to adjust the
 			// SQL query used for loading based on those influencers
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else {
 			return ( UniqueEntityLoader ) getLoaders().get( lockOptions.getLockMode() );
 		}
 	}
 
 	private boolean isAllNull(Object[] array, int tableNumber) {
 		for ( int i = 0; i < array.length; i++ ) {
 			if ( isPropertyOfTable( i, tableNumber ) && array[i] != null ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	public boolean isSubclassPropertyNullable(int i) {
 		return subclassPropertyNullabilityClosure[i];
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is dirty
 	 */
 	protected final boolean[] getPropertiesToUpdate(final int[] dirtyProperties, final boolean hasDirtyCollection) {
 		final boolean[] propsToUpdate = new boolean[ entityMetamodel.getPropertySpan() ];
 		final boolean[] updateability = getPropertyUpdateability(); //no need to check laziness, dirty checking handles that
 		for ( int j = 0; j < dirtyProperties.length; j++ ) {
 			int property = dirtyProperties[j];
 			if ( updateability[property] ) {
 				propsToUpdate[property] = true;
 			}
 		}
 		if ( isVersioned() && updateability[getVersionProperty() ]) {
 			propsToUpdate[ getVersionProperty() ] =
 				Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
 		}
 		return propsToUpdate;
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is insertable and non-null
 	 */
 	protected boolean[] getPropertiesToInsert(Object[] fields) {
 		boolean[] notNull = new boolean[fields.length];
 		boolean[] insertable = getPropertyInsertability();
 		for ( int i = 0; i < fields.length; i++ ) {
 			notNull[i] = insertable[i] && fields[i] != null;
 		}
 		return notNull;
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param currentState The current state of the entity (the state to be checked).
 	 * @param previousState The previous state of the entity (the state to be checked against).
 	 * @param entity The entity for which we are checking state dirtiness.
 	 * @param session The session in which the check is occurring.
 	 * @return <tt>null</tt> or the indices of the dirty properties
 	 * @throws HibernateException
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findDirty(
 				entityMetamodel.getProperties(),
 				currentState,
 				previousState,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
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
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param old The old state of the entity.
 	 * @param current The current state of the entity.
 	 * @param entity The entity for which we are checking state modification.
 	 * @param session The session in which the check is occurring.
 	 * @return <tt>null</tt> or the indices of the modified properties
 	 * @throws HibernateException
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findModified(
 				entityMetamodel.getProperties(),
 				current,
 				old,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
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
 				String propertyName = entityMetamodel.getProperties()[ props[i] ].getName();
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
 
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
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
 		return ( VersionType ) locateVersionType();
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
 			FieldInterceptor interceptor = getEntityMetamodel().getInstrumentationMetadata().extractInterceptor( entity );
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
 
 		handleNaturalIdReattachment( entity, session );
 	}
 
 	private void handleNaturalIdReattachment(Object entity, SessionImplementor session) {
 		if ( ! hasNaturalIdentifier() ) {
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
 		if ( hasCache() ) {
 			CacheKey ck = session.generateCacheKey( id, getIdentifierType(), getRootEntityName() );
 			if ( getCacheAccessStrategy().get( ck, session.getTimestamp() ) != null ) {
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
 		return entityMetamodel.isLazy();
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
 		return isVersioned() && ( getPropertyUpdateGenerationInclusions() [ getVersionProperty() ] != ValueInclusion.NONE );
 	}
 
 	public boolean isVersionPropertyInsertable() {
 		return isVersioned() && getPropertyInsertability() [ getVersionProperty() ];
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
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return entityMetamodel.getPropertyInsertGenerationInclusions();
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return entityMetamodel.getPropertyUpdateGenerationInclusions();
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
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
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
 
 	public String getTemporaryIdTableName() {
 		return temporaryIdTableName;
 	}
 
 	public String getTemporaryIdTableDDL() {
 		return temporaryIdTableDDL;
 	}
 
 	protected int getPropertySpan() {
 		return entityMetamodel.getPropertySpan();
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException {
 		return getEntityTuplizer().getPropertyValuesToInsert( object, mergeMap, session );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasInsertGeneratedProperties() ) {
 			throw new AssertionFailure("no insert-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlInsertGeneratedValuesSelectString, getPropertyInsertGenerationInclusions() );
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasUpdateGeneratedProperties() ) {
 			throw new AssertionFailure("no update-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlUpdateGeneratedValuesSelectString, getPropertyUpdateGenerationInclusions() );
 	}
 
 	private void processGeneratedProperties(
 			Serializable id,
 	        Object entity,
 	        Object[] state,
 	        SessionImplementor session,
 	        String selectionSQL,
 	        ValueInclusion[] includeds) {
 		// force immediate execution of the insert batch (if one)
 		session.getTransactionCoordinator().getJdbcCoordinator().executeBatch();
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( selectionSQL );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = ps.executeQuery();
 				try {
 					if ( !rs.next() ) {
 						throw new HibernateException(
 								"Unable to locate row for retrieval of generated properties: " +
 								MessageHelper.infoString( this, id, getFactory() )
 							);
 					}
 					for ( int i = 0; i < getPropertySpan(); i++ ) {
 						if ( includeds[i] != ValueInclusion.NONE ) {
 							Object hydratedState = getPropertyTypes()[i].hydrate( rs, getPropertyAliases( "", i ), session, entity );
 							state[i] = getPropertyTypes()[i].resolve( hydratedState, session, entity );
 							setPropertyValue( entity, i, state[i] );
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						rs.close();
 					}
 				}
 			}
 			finally {
 				ps.close();
 			}
 		}
 		catch( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"unable to select generated column values",
 					selectionSQL
 			);
 		}
 
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
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !hasNaturalIdentifier() ) {
 			throw new MappingException( "persistent class did not define a natural-id : " + MessageHelper.infoString( this ) );
 		}
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting current natural-id snapshot state for: {0}",
 					MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		int[] naturalIdPropertyIndexes = getNaturalIdentifierProperties();
 		int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
 		boolean[] naturalIdMarkers = new boolean[ getPropertySpan() ];
 		Type[] extractionTypes = new Type[ naturalIdPropertyCount ];
 		for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 			extractionTypes[i] = getPropertyTypes()[ naturalIdPropertyIndexes[i] ];
 			naturalIdMarkers[ naturalIdPropertyIndexes[i] ] = true;
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// TODO : look at perhaps caching this...
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id state " + getEntityName() );
 		}
 		select.setSelectClause( concretePropertySelectFragmentSansLeadingComma( getRootAlias(), naturalIdMarkers ) );
 		select.setFromClause( fromTableFragment( getRootAlias() ) + fromJoinFragment( getRootAlias(), true, false ) );
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String whereClause = new StringBuilder()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		String sql = select.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 		///////////////////////////////////////////////////////////////////////
 
 		Object[] snapshot = new Object[ naturalIdPropertyCount ];
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = ps.executeQuery();
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					final EntityKey key = session.generateEntityKey( id, this );
 					Object owner = session.getPersistenceContext().getEntity( key );
 					for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 						snapshot[i] = extractionTypes[i].hydrate( rs, getPropertyAliases( "", naturalIdPropertyIndexes[i] ), session, null );
 						if (extractionTypes[i].isEntityType()) {
 							snapshot[i] = extractionTypes[i].resolve(snapshot[i], session, owner);
 						}
 					}
 					return snapshot;
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				ps.close();
 			}
 		}
 		catch ( SQLException e ) {
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
 			PreparedStatement ps = session.getTransactionCoordinator()
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
 				ResultSet rs = ps.executeQuery();
 				try {
 					// if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 
 					return (Serializable) getIdentifierType().hydrate( rs, getIdentifierAliases(), session, null );
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				ps.close();
 			}
 		}
 		catch ( SQLException e ) {
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
 		boolean[] nullness = new boolean[ naturalIdValues.length ];
 		for ( int i = 0; i < naturalIdValues.length; i++ ) {
 			nullness[i] = naturalIdValues[i] == null;
 		}
 		return nullness;
 	}
 
 	private Boolean naturalIdIsNonNullable;
 	private String cachedPkByNonNullableNaturalIdQuery;
 
 	private String determinePkByNaturalIdQuery(boolean[] valueNullness) {
 		if ( ! hasNaturalIdentifier() ) {
 			throw new HibernateException( "Attempt to build natural-id -> PK resolution query for entity that does not define natural id" );
 		}
 
 		// performance shortcut for cases where the natural-id is defined as completely non-nullable
 		if ( isNaturalIdNonNullable() ) {
 			if ( valueNullness != null && ! ArrayHelper.isAllFalse( valueNullness ) ) {
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
 
 	@SuppressWarnings("UnnecessaryUnboxing")
 	protected boolean isNaturalIdNonNullable() {
 		if ( naturalIdIsNonNullable == null ) {
 			naturalIdIsNonNullable = determineNaturalIdNullability();
 		}
 		return naturalIdIsNonNullable.booleanValue();
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
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id->entity-id state " + getEntityName() );
 		}
 
 		final String rootAlias = getRootAlias();
 
 		select.setSelectClause( identifierSelectFragment( rootAlias, "" ) );
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
index e4c6d2cf3f..8c1a51f931 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
@@ -1,406 +1,407 @@
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
 package org.hibernate.tuple;
 import java.lang.reflect.Constructor;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.engine.internal.UnsavedValueFactory;
 import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.IdentifierValue;
 import org.hibernate.engine.spi.VersionValue;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.AssociationAttributeBinding;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.BasicAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.SimpleValueBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.property.Getter;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.property.PropertyAccessorFactory;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Responsible for generation of runtime metamodel {@link Property} representations.
  * Makes distinction between identifier, version, and other (standard) properties.
  *
  * @author Steve Ebersole
  */
 public class PropertyFactory {
 
 	/**
 	 * Generates an IdentifierProperty representation of the for a given entity mapping.
 	 *
 	 * @param mappedEntity The mapping definition of the entity.
 	 * @param generator The identifier value generator to use for this identifier.
 	 * @return The appropriate IdentifierProperty definition.
 	 */
 	public static IdentifierProperty buildIdentifierProperty(PersistentClass mappedEntity, IdentifierGenerator generator) {
 
 		String mappedUnsavedValue = mappedEntity.getIdentifier().getNullValue();
 		Type type = mappedEntity.getIdentifier().getType();
 		Property property = mappedEntity.getIdentifierProperty();
 		
 		IdentifierValue unsavedValue = UnsavedValueFactory.getUnsavedIdentifierValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				type,
 				getConstructor(mappedEntity)
 			);
 
 		if ( property == null ) {
 			// this is a virtual id property...
 			return new IdentifierProperty(
 			        type,
 					mappedEntity.hasEmbeddedIdentifier(),
 					mappedEntity.hasIdentifierMapper(),
 					unsavedValue,
 					generator
 				);
 		}
 		else {
 			return new IdentifierProperty(
 					property.getName(),
 					property.getNodeName(),
 					type,
 					mappedEntity.hasEmbeddedIdentifier(),
 					unsavedValue,
 					generator
 				);
 		}
 	}
 
 	/**
 	 * Generates an IdentifierProperty representation of the for a given entity mapping.
 	 *
 	 * @param mappedEntity The mapping definition of the entity.
 	 * @param generator The identifier value generator to use for this identifier.
 	 * @return The appropriate IdentifierProperty definition.
 	 */
 	public static IdentifierProperty buildIdentifierProperty(EntityBinding mappedEntity, IdentifierGenerator generator) {
 
 		final BasicAttributeBinding property = mappedEntity.getHierarchyDetails().getEntityIdentifier().getValueBinding();
 
 		// TODO: the following will cause an NPE with "virtual" IDs; how should they be set?
 		// (steve) virtual attributes will still be attributes, they will simply be marked as virtual.
 		//		see org.hibernate.metamodel.domain.AbstractAttributeContainer.locateOrCreateVirtualAttribute()
 
 		final String mappedUnsavedValue = property.getUnsavedValue();
 		final Type type = property.getHibernateTypeDescriptor().getResolvedTypeMapping();
 
 		IdentifierValue unsavedValue = UnsavedValueFactory.getUnsavedIdentifierValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				type,
 				getConstructor( mappedEntity )
 			);
 
 		if ( property == null ) {
 			// this is a virtual id property...
 			return new IdentifierProperty(
 			        type,
 					mappedEntity.getHierarchyDetails().getEntityIdentifier().isEmbedded(),
 					mappedEntity.getHierarchyDetails().getEntityIdentifier().isIdentifierMapper(),
 					unsavedValue,
 					generator
 				);
 		}
 		else {
 			return new IdentifierProperty(
 					property.getAttribute().getName(),
 					null,
 					type,
 					mappedEntity.getHierarchyDetails().getEntityIdentifier().isEmbedded(),
 					unsavedValue,
 					generator
 				);
 		}
 	}
 
 	/**
 	 * Generates a VersionProperty representation for an entity mapping given its
 	 * version mapping Property.
 	 *
 	 * @param property The version mapping Property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate VersionProperty definition.
 	 */
 	public static VersionProperty buildVersionProperty(Property property, boolean lazyAvailable) {
 		String mappedUnsavedValue = ( (KeyValue) property.getValue() ).getNullValue();
 		
 		VersionValue unsavedValue = UnsavedValueFactory.getUnsavedVersionValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				(VersionType) property.getType(),
 				getConstructor( property.getPersistentClass() )
 			);
 
 		boolean lazy = lazyAvailable && property.isLazy();
 
 		return new VersionProperty(
 		        property.getName(),
 		        property.getNodeName(),
 		        property.getValue().getType(),
 		        lazy,
 				property.isInsertable(),
 				property.isUpdateable(),
 		        property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.isOptional(),
 				property.isUpdateable() && !lazy,
 				property.isOptimisticLocked(),
 		        property.getCascadeStyle(),
 		        unsavedValue
 			);
 	}
 
 	/**
 	 * Generates a VersionProperty representation for an entity mapping given its
 	 * version mapping Property.
 	 *
 	 * @param property The version mapping Property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate VersionProperty definition.
 	 */
 	public static VersionProperty buildVersionProperty(BasicAttributeBinding property, boolean lazyAvailable) {
 		String mappedUnsavedValue = ( (KeyValue) property.getValue() ).getNullValue();
 
 		VersionValue unsavedValue = UnsavedValueFactory.getUnsavedVersionValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				(VersionType) property.getHibernateTypeDescriptor().getResolvedTypeMapping(),
 				getConstructor( (EntityBinding) property.getContainer() )
 		);
 
 		boolean lazy = lazyAvailable && property.isLazy();
 
 		final CascadeStyle cascadeStyle = property.isAssociation()
 				? ( (AssociationAttributeBinding) property ).getCascadeStyle()
-				: CascadeStyle.NONE;
+				: CascadeStyles.NONE;
 
 		return new VersionProperty(
 		        property.getAttribute().getName(),
 		        null,
 		        property.getHibernateTypeDescriptor().getResolvedTypeMapping(),
 		        lazy,
 				true, // insertable
 				true, // updatable
 		        property.getGeneration() == PropertyGeneration.INSERT
 						|| property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.isNullable(),
 				!lazy,
 				property.isIncludedInOptimisticLocking(),
 				cascadeStyle,
 		        unsavedValue
 			);
 	}
 
 	/**
 	 * Generate a "standard" (i.e., non-identifier and non-version) based on the given
 	 * mapped property.
 	 *
 	 * @param property The mapped property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate StandardProperty definition.
 	 */
 	public static StandardProperty buildStandardProperty(Property property, boolean lazyAvailable) {
 		
 		final Type type = property.getValue().getType();
 		
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 		
 		// we need to dirty check many-to-ones with not-found="ignore" in order 
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 		
 		boolean alwaysDirtyCheck = type.isAssociationType() && 
 				( (AssociationType) type ).isAlwaysDirtyChecked(); 
 
 		return new StandardProperty(
 				property.getName(),
 				property.getNodeName(),
 				type,
 				lazyAvailable && property.isLazy(),
 				property.isInsertable(),
 				property.isUpdateable(),
 		        property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.isOptional(),
 				alwaysDirtyCheck || property.isUpdateable(),
 				property.isOptimisticLocked(),
 				property.getCascadeStyle(),
 		        property.getValue().getFetchMode()
 			);
 	}
 
 	/**
 	 * Generate a "standard" (i.e., non-identifier and non-version) based on the given
 	 * mapped property.
 	 *
 	 * @param property The mapped property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate StandardProperty definition.
 	 */
 	public static StandardProperty buildStandardProperty(AttributeBinding property, boolean lazyAvailable) {
 
 		final Type type = property.getHibernateTypeDescriptor().getResolvedTypeMapping();
 
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 
 		// we need to dirty check many-to-ones with not-found="ignore" in order
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 
 		final boolean alwaysDirtyCheck = type.isAssociationType() && ( (AssociationType) type ).isAlwaysDirtyChecked();
 
 		if ( property.getAttribute().isSingular() ) {
 			final SingularAttributeBinding singularAttributeBinding = ( SingularAttributeBinding ) property;
 			final CascadeStyle cascadeStyle = singularAttributeBinding.isAssociation()
 					? ( (AssociationAttributeBinding) singularAttributeBinding ).getCascadeStyle()
-					: CascadeStyle.NONE;
+					: CascadeStyles.NONE;
 			final FetchMode fetchMode = singularAttributeBinding.isAssociation()
 					? ( (AssociationAttributeBinding) singularAttributeBinding ).getFetchMode()
 					: FetchMode.DEFAULT;
 
 			return new StandardProperty(
 					singularAttributeBinding.getAttribute().getName(),
 					null,
 					type,
 					lazyAvailable && singularAttributeBinding.isLazy(),
 					true, // insertable
 					true, // updatable
 					singularAttributeBinding.getGeneration() == PropertyGeneration.INSERT
 							|| singularAttributeBinding.getGeneration() == PropertyGeneration.ALWAYS,
 					singularAttributeBinding.getGeneration() == PropertyGeneration.ALWAYS,
 					singularAttributeBinding.isNullable(),
 					alwaysDirtyCheck || areAllValuesIncludedInUpdate( singularAttributeBinding ),
 					singularAttributeBinding.isIncludedInOptimisticLocking(),
 					cascadeStyle,
 					fetchMode
 			);
 		}
 		else {
 			final AbstractPluralAttributeBinding pluralAttributeBinding = (AbstractPluralAttributeBinding) property;
 			final CascadeStyle cascadeStyle = pluralAttributeBinding.isAssociation()
 					? pluralAttributeBinding.getCascadeStyle()
-					: CascadeStyle.NONE;
+					: CascadeStyles.NONE;
 			final FetchMode fetchMode = pluralAttributeBinding.isAssociation()
 					? pluralAttributeBinding.getFetchMode()
 					: FetchMode.DEFAULT;
 
 			return new StandardProperty(
 					pluralAttributeBinding.getAttribute().getName(),
 					null,
 					type,
 					lazyAvailable && pluralAttributeBinding.isLazy(),
 					// TODO: fix this when HHH-6356 is fixed; for now assume AbstractPluralAttributeBinding is updatable and insertable
 					true, // pluralAttributeBinding.isInsertable(),
 					true, //pluralAttributeBinding.isUpdatable(),
 					false,
 					false,
 					false, // nullable - not sure what that means for a collection
 					// TODO: fix this when HHH-6356 is fixed; for now assume AbstractPluralAttributeBinding is updatable and insertable
 					//alwaysDirtyCheck || pluralAttributeBinding.isUpdatable(),
 					true,
 					pluralAttributeBinding.isIncludedInOptimisticLocking(),
 					cascadeStyle,
 					fetchMode
 				);
 		}
 	}
 
 	private static boolean areAllValuesIncludedInUpdate(SingularAttributeBinding attributeBinding) {
 		if ( attributeBinding.hasDerivedValue() ) {
 			return false;
 		}
 		for ( SimpleValueBinding valueBinding : attributeBinding.getSimpleValueBindings() ) {
 			if ( ! valueBinding.isIncludeInUpdate() ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	private static Constructor getConstructor(PersistentClass persistentClass) {
 		if ( persistentClass == null || !persistentClass.hasPojoRepresentation() ) {
 			return null;
 		}
 
 		try {
 			return ReflectHelper.getDefaultConstructor( persistentClass.getMappedClass() );
 		}
 		catch( Throwable t ) {
 			return null;
 		}
 	}
 
 	private static Constructor getConstructor(EntityBinding entityBinding) {
 		if ( entityBinding == null || entityBinding.getEntity() == null ) {
 			return null;
 		}
 
 		try {
 			return ReflectHelper.getDefaultConstructor( entityBinding.getEntity().getClassReference() );
 		}
 		catch( Throwable t ) {
 			return null;
 		}
 	}
 
 	private static Getter getGetter(Property mappingProperty) {
 		if ( mappingProperty == null || !mappingProperty.getPersistentClass().hasPojoRepresentation() ) {
 			return null;
 		}
 
 		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
 		return pa.getGetter( mappingProperty.getPersistentClass().getMappedClass(), mappingProperty.getName() );
 	}
 
 	private static Getter getGetter(AttributeBinding mappingProperty) {
 		if ( mappingProperty == null || mappingProperty.getContainer().getClassReference() == null ) {
 			return null;
 		}
 
 		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
 		return pa.getGetter(
 				mappingProperty.getContainer().getClassReference(),
 				mappingProperty.getAttribute().getName()
 		);
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
index 82742db83d..5db0051cc6 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
@@ -1,962 +1,963 @@
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
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.BasicAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.tuple.IdentifierProperty;
 import org.hibernate.tuple.PropertyFactory;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.VersionProperty;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Centralizes metamodel information about an entity.
  *
  * @author Steve Ebersole
  */
 public class EntityMetamodel implements Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityMetamodel.class.getName());
 
 	private static final int NO_VERSION_INDX = -66;
 
 	private final SessionFactoryImplementor sessionFactory;
 
 	private final String name;
 	private final String rootName;
 	private final EntityType entityType;
 
 	private final IdentifierProperty identifierProperty;
 	private final boolean versioned;
 
 	private final int propertySpan;
 	private final int versionPropertyIndex;
 	private final StandardProperty[] properties;
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyLaziness;
 	private final boolean[] propertyUpdateability;
 	private final boolean[] nonlazyPropertyUpdateability;
 	private final boolean[] propertyCheckability;
 	private final boolean[] propertyInsertability;
 	private final ValueInclusion[] insertInclusions;
 	private final ValueInclusion[] updateInclusions;
 	private final boolean[] propertyNullability;
 	private final boolean[] propertyVersionability;
 	private final CascadeStyle[] cascadeStyles;
 	private final boolean hasInsertGeneratedValues;
 	private final boolean hasUpdateGeneratedValues;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final Map<String, Integer> propertyIndexes = new HashMap<String, Integer>();
 	private final boolean hasCollections;
 	private final boolean hasMutableProperties;
 	private final boolean hasLazyProperties;
 	private final boolean hasNonIdentifierPropertyNamedId;
 
 	private final int[] naturalIdPropertyNumbers;
 	private final boolean hasImmutableNaturalId;
 	private final boolean hasCacheableNaturalId;
 
 	private boolean lazy; //not final because proxy factory creation can fail
 	private final boolean hasCascades;
 	private final boolean mutable;
 	private final boolean isAbstract;
 	private final boolean selectBeforeUpdate;
 	private final boolean dynamicUpdate;
 	private final boolean dynamicInsert;
 	private final OptimisticLockStyle optimisticLockStyle;
 
 	private final boolean polymorphic;
 	private final String superclass;  // superclass entity-name
 	private final boolean explicitPolymorphism;
 	private final boolean inherited;
 	private final boolean hasSubclasses;
 	private final Set subclassEntityNames = new HashSet();
 	private final Map entityNameByInheritenceClassMap = new HashMap();
 
 	private final EntityMode entityMode;
 	private final EntityTuplizer entityTuplizer;
 	private final EntityInstrumentationMetadata instrumentationMetadata;
 
 	public EntityMetamodel(PersistentClass persistentClass, SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 
 		name = persistentClass.getEntityName();
 		rootName = persistentClass.getRootClass().getEntityName();
 		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
 
 		identifierProperty = PropertyFactory.buildIdentifierProperty(
 		        persistentClass,
 		        sessionFactory.getIdentifierGenerator( rootName )
 			);
 
 		versioned = persistentClass.isVersioned();
 
 		instrumentationMetadata = persistentClass.hasPojoRepresentation()
 				? Environment.getBytecodeProvider().getEntityInstrumentationMetadata( persistentClass.getMappedClass() )
 				: new NonPojoInstrumentationMetadata( persistentClass.getEntityName() );
 
 		boolean hasLazy = false;
 
 		propertySpan = persistentClass.getPropertyClosureSpan();
 		properties = new StandardProperty[propertySpan];
 		List<Integer> naturalIdNumbers = new ArrayList<Integer>();
 		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		propertyNames = new String[propertySpan];
 		propertyTypes = new Type[propertySpan];
 		propertyUpdateability = new boolean[propertySpan];
 		propertyInsertability = new boolean[propertySpan];
 		insertInclusions = new ValueInclusion[propertySpan];
 		updateInclusions = new ValueInclusion[propertySpan];
 		nonlazyPropertyUpdateability = new boolean[propertySpan];
 		propertyCheckability = new boolean[propertySpan];
 		propertyNullability = new boolean[propertySpan];
 		propertyVersionability = new boolean[propertySpan];
 		propertyLaziness = new boolean[propertySpan];
 		cascadeStyles = new CascadeStyle[propertySpan];
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i = 0;
 		int tempVersionProperty = NO_VERSION_INDX;
 		boolean foundCascade = false;
 		boolean foundCollection = false;
 		boolean foundMutable = false;
 		boolean foundNonIdentifierPropertyNamedId = false;
 		boolean foundInsertGeneratedValue = false;
 		boolean foundUpdateGeneratedValue = false;
 		boolean foundUpdateableNaturalIdProperty = false;
 
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 
 			if ( prop == persistentClass.getVersion() ) {
 				tempVersionProperty = i;
 				properties[i] = PropertyFactory.buildVersionProperty( prop, instrumentationMetadata.isInstrumented() );
 			}
 			else {
 				properties[i] = PropertyFactory.buildStandardProperty( prop, instrumentationMetadata.isInstrumented() );
 			}
 
 			if ( prop.isNaturalIdentifier() ) {
 				naturalIdNumbers.add( i );
 				if ( prop.isUpdateable() ) {
 					foundUpdateableNaturalIdProperty = true;
 				}
 			}
 
 			if ( "id".equals( prop.getName() ) ) {
 				foundNonIdentifierPropertyNamedId = true;
 			}
 
 			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			boolean lazy = prop.isLazy() && instrumentationMetadata.isInstrumented();
 			if ( lazy ) hasLazy = true;
 			propertyLaziness[i] = lazy;
 
 			propertyNames[i] = properties[i].getName();
 			propertyTypes[i] = properties[i].getType();
 			propertyNullability[i] = properties[i].isNullable();
 			propertyUpdateability[i] = properties[i].isUpdateable();
 			propertyInsertability[i] = properties[i].isInsertable();
 			insertInclusions[i] = determineInsertValueGenerationType( prop, properties[i] );
 			updateInclusions[i] = determineUpdateValueGenerationType( prop, properties[i] );
 			propertyVersionability[i] = properties[i].isVersionable();
 			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
 			propertyCheckability[i] = propertyUpdateability[i] ||
 					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
 
 			cascadeStyles[i] = properties[i].getCascadeStyle();
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			if ( properties[i].isLazy() ) {
 				hasLazy = true;
 			}
 
-			if ( properties[i].getCascadeStyle() != CascadeStyle.NONE ) {
+			if ( properties[i].getCascadeStyle() != CascadeStyles.NONE ) {
 				foundCascade = true;
 			}
 
 			if ( indicatesCollection( properties[i].getType() ) ) {
 				foundCollection = true;
 			}
 
 			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
 				foundMutable = true;
 			}
 
 			if ( insertInclusions[i] != ValueInclusion.NONE ) {
 				foundInsertGeneratedValue = true;
 			}
 
 			if ( updateInclusions[i] != ValueInclusion.NONE ) {
 				foundUpdateGeneratedValue = true;
 			}
 
 			mapPropertyToIndex(prop, i);
 			i++;
 		}
 
 		if (naturalIdNumbers.size()==0) {
 			naturalIdPropertyNumbers = null;
 			hasImmutableNaturalId = false;
 			hasCacheableNaturalId = false;
 		}
 		else {
 			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
 			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
 			hasCacheableNaturalId = persistentClass.getNaturalIdCacheRegionName() != null;
 		}
 
 		hasInsertGeneratedValues = foundInsertGeneratedValue;
 		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
 
 		hasCascades = foundCascade;
 		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
 		versionPropertyIndex = tempVersionProperty;
 		hasLazyProperties = hasLazy;
         if (hasLazyProperties) LOG.lazyPropertyFetchingAvailable(name);
 
 		lazy = persistentClass.isLazy() && (
 				// TODO: this disables laziness even in non-pojo entity modes:
 				!persistentClass.hasPojoRepresentation() ||
 				!ReflectHelper.isFinalClass( persistentClass.getProxyInterface() )
 		);
 		mutable = persistentClass.isMutable();
 		if ( persistentClass.isAbstract() == null ) {
 			// legacy behavior (with no abstract attribute specified)
 			isAbstract = persistentClass.hasPojoRepresentation() &&
 			             ReflectHelper.isAbstractClass( persistentClass.getMappedClass() );
 		}
 		else {
 			isAbstract = persistentClass.isAbstract().booleanValue();
 			if ( !isAbstract && persistentClass.hasPojoRepresentation() &&
 			     ReflectHelper.isAbstractClass( persistentClass.getMappedClass() ) ) {
                 LOG.entityMappedAsNonAbstract(name);
 			}
 		}
 		selectBeforeUpdate = persistentClass.hasSelectBeforeUpdate();
 		dynamicUpdate = persistentClass.useDynamicUpdate();
 		dynamicInsert = persistentClass.useDynamicInsert();
 
 		polymorphic = persistentClass.isPolymorphic();
 		explicitPolymorphism = persistentClass.isExplicitPolymorphism();
 		inherited = persistentClass.isInherited();
 		superclass = inherited ?
 				persistentClass.getSuperclass().getEntityName() :
 				null;
 		hasSubclasses = persistentClass.hasSubclasses();
 
 		optimisticLockStyle = interpretOptLockMode( persistentClass.getOptimisticLockMode() );
 		final boolean isAllOrDirty =
 				optimisticLockStyle == OptimisticLockStyle.ALL
 						|| optimisticLockStyle == OptimisticLockStyle.DIRTY;
 		if ( isAllOrDirty && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && isAllOrDirty ) {
 			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
 		}
 
 		hasCollections = foundCollection;
 		hasMutableProperties = foundMutable;
 
 		iter = persistentClass.getSubclassIterator();
 		while ( iter.hasNext() ) {
 			subclassEntityNames.add( ( (PersistentClass) iter.next() ).getEntityName() );
 		}
 		subclassEntityNames.add( name );
 
 		if ( persistentClass.hasPojoRepresentation() ) {
 			entityNameByInheritenceClassMap.put( persistentClass.getMappedClass(), persistentClass.getEntityName() );
 			iter = persistentClass.getSubclassIterator();
 			while ( iter.hasNext() ) {
 				final PersistentClass pc = ( PersistentClass ) iter.next();
 				entityNameByInheritenceClassMap.put( pc.getMappedClass(), pc.getEntityName() );
 			}
 		}
 
 		entityMode = persistentClass.hasPojoRepresentation() ? EntityMode.POJO : EntityMode.MAP;
 		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
 		final String tuplizerClassName = persistentClass.getTuplizerImplClassName( entityMode );
 		if ( tuplizerClassName == null ) {
 			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, persistentClass );
 		}
 		else {
 			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClassName, this, persistentClass );
 		}
 	}
 
 	private OptimisticLockStyle interpretOptLockMode(int optimisticLockMode) {
 		switch ( optimisticLockMode ) {
 			case Versioning.OPTIMISTIC_LOCK_NONE: {
 				return OptimisticLockStyle.NONE;
 			}
 			case Versioning.OPTIMISTIC_LOCK_DIRTY: {
 				return OptimisticLockStyle.DIRTY;
 			}
 			case Versioning.OPTIMISTIC_LOCK_ALL: {
 				return OptimisticLockStyle.ALL;
 			}
 			default: {
 				return OptimisticLockStyle.VERSION;
 			}
 		}
 	}
 
 	public EntityMetamodel(EntityBinding entityBinding, SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 
 		name = entityBinding.getEntity().getName();
 
 		rootName = entityBinding.getHierarchyDetails().getRootEntityBinding().getEntity().getName();
 		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
 
 		identifierProperty = PropertyFactory.buildIdentifierProperty(
 		        entityBinding,
 		        sessionFactory.getIdentifierGenerator( rootName )
 		);
 
 		versioned = entityBinding.isVersioned();
 
 		boolean hasPojoRepresentation = false;
 		Class<?> mappedClass = null;
 		Class<?> proxyInterfaceClass = null;
 		if ( entityBinding.getEntity().getClassReferenceUnresolved() != null ) {
 			hasPojoRepresentation = true;
 			mappedClass = entityBinding.getEntity().getClassReference();
 			proxyInterfaceClass = entityBinding.getProxyInterfaceType().getValue();
 		}
 		instrumentationMetadata = Environment.getBytecodeProvider().getEntityInstrumentationMetadata( mappedClass );
 
 		boolean hasLazy = false;
 
 		// TODO: Fix after HHH-6337 is fixed; for now assume entityBinding is the root binding
 		BasicAttributeBinding rootEntityIdentifier = entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding();
 		// entityBinding.getAttributeClosureSpan() includes the identifier binding;
 		// "properties" here excludes the ID, so subtract 1 if the identifier binding is non-null
 		propertySpan = rootEntityIdentifier == null ?
 				entityBinding.getAttributeBindingClosureSpan() :
 				entityBinding.getAttributeBindingClosureSpan() - 1;
 
 		properties = new StandardProperty[propertySpan];
 		List naturalIdNumbers = new ArrayList();
 		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		propertyNames = new String[propertySpan];
 		propertyTypes = new Type[propertySpan];
 		propertyUpdateability = new boolean[propertySpan];
 		propertyInsertability = new boolean[propertySpan];
 		insertInclusions = new ValueInclusion[propertySpan];
 		updateInclusions = new ValueInclusion[propertySpan];
 		nonlazyPropertyUpdateability = new boolean[propertySpan];
 		propertyCheckability = new boolean[propertySpan];
 		propertyNullability = new boolean[propertySpan];
 		propertyVersionability = new boolean[propertySpan];
 		propertyLaziness = new boolean[propertySpan];
 		cascadeStyles = new CascadeStyle[propertySpan];
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 		int i = 0;
 		int tempVersionProperty = NO_VERSION_INDX;
 		boolean foundCascade = false;
 		boolean foundCollection = false;
 		boolean foundMutable = false;
 		boolean foundNonIdentifierPropertyNamedId = false;
 		boolean foundInsertGeneratedValue = false;
 		boolean foundUpdateGeneratedValue = false;
 		boolean foundUpdateableNaturalIdProperty = false;
 
 		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			if ( attributeBinding == rootEntityIdentifier ) {
 				// skip the identifier attribute binding
 				continue;
 			}
 
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getVersioningAttributeBinding() ) {
 				tempVersionProperty = i;
 				properties[i] = PropertyFactory.buildVersionProperty(
 						entityBinding.getHierarchyDetails().getVersioningAttributeBinding(),
 						instrumentationMetadata.isInstrumented()
 				);
 			}
 			else {
 				properties[i] = PropertyFactory.buildStandardProperty( attributeBinding, instrumentationMetadata.isInstrumented() );
 			}
 
 			// TODO: fix when natural IDs are added (HHH-6354)
 			//if ( attributeBinding.isNaturalIdentifier() ) {
 			//	naturalIdNumbers.add( i );
 			//	if ( attributeBinding.isUpdateable() ) {
 			//		foundUpdateableNaturalIdProperty = true;
 			//	}
 			//}
 
 			if ( "id".equals( attributeBinding.getAttribute().getName() ) ) {
 				foundNonIdentifierPropertyNamedId = true;
 			}
 
 			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			boolean lazy = attributeBinding.isLazy() && instrumentationMetadata.isInstrumented();
 			if ( lazy ) hasLazy = true;
 			propertyLaziness[i] = lazy;
 
 			propertyNames[i] = properties[i].getName();
 			propertyTypes[i] = properties[i].getType();
 			propertyNullability[i] = properties[i].isNullable();
 			propertyUpdateability[i] = properties[i].isUpdateable();
 			propertyInsertability[i] = properties[i].isInsertable();
 			insertInclusions[i] = determineInsertValueGenerationType( attributeBinding, properties[i] );
 			updateInclusions[i] = determineUpdateValueGenerationType( attributeBinding, properties[i] );
 			propertyVersionability[i] = properties[i].isVersionable();
 			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
 			propertyCheckability[i] = propertyUpdateability[i] ||
 					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
 
 			cascadeStyles[i] = properties[i].getCascadeStyle();
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			if ( properties[i].isLazy() ) {
 				hasLazy = true;
 			}
 
-			if ( properties[i].getCascadeStyle() != CascadeStyle.NONE ) {
+			if ( properties[i].getCascadeStyle() != CascadeStyles.NONE ) {
 				foundCascade = true;
 			}
 
 			if ( indicatesCollection( properties[i].getType() ) ) {
 				foundCollection = true;
 			}
 
 			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
 				foundMutable = true;
 			}
 
 			if ( insertInclusions[i] != ValueInclusion.NONE ) {
 				foundInsertGeneratedValue = true;
 			}
 
 			if ( updateInclusions[i] != ValueInclusion.NONE ) {
 				foundUpdateGeneratedValue = true;
 			}
 
 			mapPropertyToIndex(attributeBinding.getAttribute(), i);
 			i++;
 		}
 
 		if (naturalIdNumbers.size()==0) {
 			naturalIdPropertyNumbers = null;
 			hasImmutableNaturalId = false;
 			hasCacheableNaturalId = false;
 		}
 		else {
 			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
 			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
 			hasCacheableNaturalId = false; //See previous TODO and HHH-6354
 		}
 
 		hasInsertGeneratedValues = foundInsertGeneratedValue;
 		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
 
 		hasCascades = foundCascade;
 		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
 		versionPropertyIndex = tempVersionProperty;
 		hasLazyProperties = hasLazy;
 		if (hasLazyProperties) {
 			LOG.lazyPropertyFetchingAvailable( name );
 		}
 
 		lazy = entityBinding.isLazy() && (
 				// TODO: this disables laziness even in non-pojo entity modes:
 				! hasPojoRepresentation ||
 				! ReflectHelper.isFinalClass( proxyInterfaceClass )
 		);
 		mutable = entityBinding.isMutable();
 		if ( entityBinding.isAbstract() == null ) {
 			// legacy behavior (with no abstract attribute specified)
 			isAbstract = hasPojoRepresentation &&
 			             ReflectHelper.isAbstractClass( mappedClass );
 		}
 		else {
 			isAbstract = entityBinding.isAbstract().booleanValue();
 			if ( !isAbstract && hasPojoRepresentation &&
 					ReflectHelper.isAbstractClass( mappedClass ) ) {
 				LOG.entityMappedAsNonAbstract(name);
 			}
 		}
 		selectBeforeUpdate = entityBinding.isSelectBeforeUpdate();
 		dynamicUpdate = entityBinding.isDynamicUpdate();
 		dynamicInsert = entityBinding.isDynamicInsert();
 
 		hasSubclasses = entityBinding.hasSubEntityBindings();
 		polymorphic = entityBinding.isPolymorphic();
 
 		explicitPolymorphism = entityBinding.getHierarchyDetails().isExplicitPolymorphism();
 		inherited = ! entityBinding.isRoot();
 		superclass = inherited ?
 				entityBinding.getEntity().getSuperType().getName() :
 				null;
 
 		optimisticLockStyle = entityBinding.getHierarchyDetails().getOptimisticLockStyle();
 		final boolean isAllOrDirty =
 				optimisticLockStyle == OptimisticLockStyle.ALL
 						|| optimisticLockStyle == OptimisticLockStyle.DIRTY;
 		if ( isAllOrDirty && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && isAllOrDirty ) {
 			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
 		}
 
 		hasCollections = foundCollection;
 		hasMutableProperties = foundMutable;
 
 		for ( EntityBinding subEntityBinding : entityBinding.getPostOrderSubEntityBindingClosure() ) {
 			subclassEntityNames.add( subEntityBinding.getEntity().getName() );
 			if ( subEntityBinding.getEntity().getClassReference() != null ) {
 				entityNameByInheritenceClassMap.put(
 						subEntityBinding.getEntity().getClassReference(),
 						subEntityBinding.getEntity().getName() );
 			}
 		}
 		subclassEntityNames.add( name );
 		if ( mappedClass != null ) {
 			entityNameByInheritenceClassMap.put( mappedClass, name );
 		}
 
 		entityMode = hasPojoRepresentation ? EntityMode.POJO : EntityMode.MAP;
 		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
 		Class<? extends EntityTuplizer> tuplizerClass = entityBinding.getCustomEntityTuplizerClass();
 
 		if ( tuplizerClass == null ) {
 			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, entityBinding );
 		}
 		else {
 			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClass, this, entityBinding );
 		}
 	}
 
 	private ValueInclusion determineInsertValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isInsertGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialInsertComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
 	private ValueInclusion determineInsertValueGenerationType(AttributeBinding mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isInsertGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		// TODO: fix the following when components are working (HHH-6173)
 		//else if ( mappingProperty.getValue() instanceof ComponentAttributeBinding ) {
 		//	if ( hasPartialInsertComponentGeneration( ( ComponentAttributeBinding ) mappingProperty.getValue() ) ) {
 		//		return ValueInclusion.PARTIAL;
 		//	}
 		//}
 		return ValueInclusion.NONE;
 	}
 
 	private boolean hasPartialInsertComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			Property prop = ( Property ) subProperties.next();
 			if ( prop.getGeneration() == PropertyGeneration.ALWAYS || prop.getGeneration() == PropertyGeneration.INSERT ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialInsertComponentGeneration( ( Component ) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private ValueInclusion determineUpdateValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isUpdateGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialUpdateComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
 	private ValueInclusion determineUpdateValueGenerationType(AttributeBinding mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isUpdateGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		// TODO: fix the following when components are working (HHH-6173)
 		//else if ( mappingProperty.getValue() instanceof ComponentAttributeBinding ) {
 		//	if ( hasPartialUpdateComponentGeneration( ( ComponentAttributeBinding ) mappingProperty.getValue() ) ) {
 		//		return ValueInclusion.PARTIAL;
 		//	}
 		//}
 		return ValueInclusion.NONE;
 	}
 
 	private boolean hasPartialUpdateComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			Property prop = ( Property ) subProperties.next();
 			if ( prop.getGeneration() == PropertyGeneration.ALWAYS ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialUpdateComponentGeneration( ( Component ) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private void mapPropertyToIndex(Property prop, int i) {
 		propertyIndexes.put( prop.getName(), i );
 		if ( prop.getValue() instanceof Component ) {
 			Iterator iter = ( (Component) prop.getValue() ).getPropertyIterator();
 			while ( iter.hasNext() ) {
 				Property subprop = (Property) iter.next();
 				propertyIndexes.put(
 						prop.getName() + '.' + subprop.getName(),
 						i
 					);
 			}
 		}
 	}
 
 	private void mapPropertyToIndex(Attribute attribute, int i) {
 		propertyIndexes.put( attribute.getName(), i );
 		if ( attribute.isSingular() &&
 				( ( SingularAttribute ) attribute ).getSingularAttributeType().isComponent() ) {
 			org.hibernate.metamodel.domain.Component component =
 					( org.hibernate.metamodel.domain.Component ) ( ( SingularAttribute ) attribute ).getSingularAttributeType();
 			for ( Attribute subAttribute : component.attributes() ) {
 				propertyIndexes.put(
 						attribute.getName() + '.' + subAttribute.getName(),
 						i
 					);
 			}
 		}
 	}
 
 	public EntityTuplizer getTuplizer() {
 		return entityTuplizer;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return naturalIdPropertyNumbers;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return naturalIdPropertyNumbers!=null;
 	}
 	
 	public boolean isNaturalIdentifierCached() {
 		return hasNaturalIdentifier() && hasCacheableNaturalId;
 	}
 
 	public boolean hasImmutableNaturalId() {
 		return hasImmutableNaturalId;
 	}
 
 	public Set getSubclassEntityNames() {
 		return subclassEntityNames;
 	}
 
 	private boolean indicatesCollection(Type type) {
 		if ( type.isCollectionType() ) {
 			return true;
 		}
 		else if ( type.isComponentType() ) {
 			Type[] subtypes = ( (CompositeType) type ).getSubtypes();
 			for ( int i = 0; i < subtypes.length; i++ ) {
 				if ( indicatesCollection( subtypes[i] ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public String getRootName() {
 		return rootName;
 	}
 
 	public EntityType getEntityType() {
 		return entityType;
 	}
 
 	public IdentifierProperty getIdentifierProperty() {
 		return identifierProperty;
 	}
 
 	public int getPropertySpan() {
 		return propertySpan;
 	}
 
 	public int getVersionPropertyIndex() {
 		return versionPropertyIndex;
 	}
 
 	public VersionProperty getVersionProperty() {
 		if ( NO_VERSION_INDX == versionPropertyIndex ) {
 			return null;
 		}
 		else {
 			return ( VersionProperty ) properties[ versionPropertyIndex ];
 		}
 	}
 
 	public StandardProperty[] getProperties() {
 		return properties;
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		Integer index = getPropertyIndexOrNull(propertyName);
 		if ( index == null ) {
 			throw new HibernateException("Unable to resolve property: " + propertyName);
 		}
 		return index.intValue();
 	}
 
 	public Integer getPropertyIndexOrNull(String propertyName) {
 		return (Integer) propertyIndexes.get( propertyName );
 	}
 
 	public boolean hasCollections() {
 		return hasCollections;
 	}
 
 	public boolean hasMutableProperties() {
 		return hasMutableProperties;
 	}
 
 	public boolean hasNonIdentifierPropertyNamedId() {
 		return hasNonIdentifierPropertyNamedId;
 	}
 
 	public boolean hasLazyProperties() {
 		return hasLazyProperties;
 	}
 
 	public boolean hasCascades() {
 		return hasCascades;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		return optimisticLockStyle;
 	}
 
 	public boolean isPolymorphic() {
 		return polymorphic;
 	}
 
 	public String getSuperclass() {
 		return superclass;
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	public boolean isInherited() {
 		return inherited;
 	}
 
 	public boolean hasSubclasses() {
 		return hasSubclasses;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public boolean isVersioned() {
 		return versioned;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	/**
 	 * Return the entity-name mapped to the given class within our inheritance hierarchy, if any.
 	 *
 	 * @param inheritenceClass The class for which to resolve the entity-name.
 	 * @return The mapped entity-name, or null if no such mapping was found.
 	 */
 	public String findEntityNameByEntityClass(Class inheritenceClass) {
 		return ( String ) entityNameByInheritenceClassMap.get( inheritenceClass );
 	}
 
 	@Override
     public String toString() {
 		return "EntityMetamodel(" + name + ':' + ArrayHelper.toString(properties) + ')';
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
 
 	public Type[] getPropertyTypes() {
 		return propertyTypes;
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return propertyLaziness;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return propertyUpdateability;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return propertyCheckability;
 	}
 
 	public boolean[] getNonlazyPropertyUpdateability() {
 		return nonlazyPropertyUpdateability;
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return propertyInsertability;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return insertInclusions;
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return updateInclusions;
 	}
 
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return propertyVersionability;
 	}
 
 	public CascadeStyle[] getCascadeStyles() {
 		return cascadeStyles;
 	}
 
 	public boolean hasInsertGeneratedValues() {
 		return hasInsertGeneratedValues;
 	}
 
 	public boolean hasUpdateGeneratedValues() {
 		return hasUpdateGeneratedValues;
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	/**
 	 * Whether or not this class can be lazy (ie intercepted)
 	 */
 	public boolean isInstrumented() {
 		return instrumentationMetadata.isInstrumented();
 	}
 
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return instrumentationMetadata;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
index 547d10a120..33a9bec9e0 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
@@ -1,405 +1,406 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Map;
 
 import org.dom4j.Node;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.proxy.HibernateProxyHelper;
 
 /**
  * Handles "any" mappings
  * 
  * @author Gavin King
  */
 public class AnyType extends AbstractType implements CompositeType, AssociationType {
 	private final Type identifierType;
 	private final Type metaType;
 
 	public AnyType(Type metaType, Type identifierType) {
 		this.identifierType = identifierType;
 		this.metaType = metaType;
 	}
 
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 	throws HibernateException {
 		return value;
 	}
 	
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	public boolean isSame(Object x, Object y) throws HibernateException {
 		return x==y;
 	}
 
 	public int compare(Object x, Object y) {
 		return 0; //TODO: entities CAN be compared, by PK and entity name, fix this!
 	}
 
 	public int getColumnSpan(Mapping session)
 	throws MappingException {
 		return 2;
 	}
 
 	public String getName() {
 		return "object";
 	}
 
 	public boolean isMutable() {
 		return false;
 	}
 
 	public Object nullSafeGet(ResultSet rs,	String name, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException {
 
 		throw new UnsupportedOperationException("object is a multicolumn type");
 	}
 
 	public Object nullSafeGet(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
 	throws HibernateException, SQLException {
 		return resolveAny(
 				(String) metaType.nullSafeGet(rs, names[0], session, owner),
 				(Serializable) identifierType.nullSafeGet(rs, names[1], session, owner),
 				session
 			);
 	}
 
 	public Object hydrate(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
 	throws HibernateException, SQLException {
 		String entityName = (String) metaType.nullSafeGet(rs, names[0], session, owner);
 		Serializable id = (Serializable) identifierType.nullSafeGet(rs, names[1], session, owner);
 		return new ObjectTypeCacheEntry(entityName, id);
 	}
 
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 		ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) value;
 		return resolveAny(holder.entityName, holder.id, session);
 	}
 
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 		throw new UnsupportedOperationException("any mappings may not form part of a property-ref");
 	}
 	
 	private Object resolveAny(String entityName, Serializable id, SessionImplementor session)
 	throws HibernateException {
 		return entityName==null || id==null ?
 				null : session.internalLoad( entityName, id, false, false );
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, SessionImplementor session)
 	throws HibernateException, SQLException {
 		nullSafeSet(st, value, index, null, session);
 	}
 	
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, boolean[] settable, SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		Serializable id;
 		String entityName;
 		if (value==null) {
 			id=null;
 			entityName=null;
 		}
 		else {
 			entityName = session.bestGuessEntityName(value);
 			id = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, value, session);
 		}
 		
 		// metaType is assumed to be single-column type
 		if ( settable==null || settable[0] ) {
 			metaType.nullSafeSet(st, entityName, index, session);
 		}
 		if (settable==null) {
 			identifierType.nullSafeSet(st, id, index+1, session);
 		}
 		else {
 			boolean[] idsettable = new boolean[ settable.length-1 ];
 			System.arraycopy(settable, 1, idsettable, 0, idsettable.length);
 			identifierType.nullSafeSet(st, id, index+1, idsettable, session);
 		}
 	}
 
 	public Class getReturnedClass() {
 		return Object.class;
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join(
 				metaType.sqlTypes( mapping ),
 				identifierType.sqlTypes( mapping )
 		);
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join(
 				metaType.dictatedSizes( mapping ),
 				identifierType.dictatedSizes( mapping )
 		);
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join(
 				metaType.defaultSizes( mapping ),
 				identifierType.defaultSizes( mapping )
 		);
 	}
 
 	public void setToXMLNode(Node xml, Object value, SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types cannot be stringified");
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) 
 	throws HibernateException {
 		//TODO: terrible implementation!
 		return value == null
 				? "null"
 				: factory.getTypeHelper()
 						.entity( HibernateProxyHelper.getClassWithoutInitializingProxy( value ) )
 						.toLoggableString( value, factory );
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		throw new UnsupportedOperationException(); //TODO: is this right??
 	}
 
 	public static final class ObjectTypeCacheEntry implements Serializable {
 		String entityName;
 		Serializable id;
 		ObjectTypeCacheEntry(String entityName, Serializable id) {
 			this.entityName = entityName;
 			this.id = id;
 		}
 	}
 
 	public Object assemble(
 		Serializable cached,
 		SessionImplementor session,
 		Object owner)
 	throws HibernateException {
 
 		ObjectTypeCacheEntry e = (ObjectTypeCacheEntry) cached;
 		return e==null ? null : session.internalLoad(e.entityName, e.id, false, false);
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 		return value==null ?
 			null :
 			new ObjectTypeCacheEntry(
 						session.bestGuessEntityName(value),
 						ForeignKeys.getEntityIdentifierIfNotUnsaved( 
 								session.bestGuessEntityName(value), value, session 
 							)
 					);
 	}
 
 	public boolean isAnyType() {
 		return true;
 	}
 
 	public Object replace(
 			Object original, 
 			Object target,
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache)
 	throws HibernateException {
 		if (original==null) {
 			return null;
 		}
 		else {
 			String entityName = session.bestGuessEntityName(original);
 			Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved(
 					entityName,
 					original,
 					session
 			);
 			return session.internalLoad( 
 					entityName, 
 					id, 
 					false, 
 					false
 				);
 		}
 	}
 	public CascadeStyle getCascadeStyle(int i) {
-		return CascadeStyle.NONE;
+		return CascadeStyles.NONE;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return FetchMode.SELECT;
 	}
 
 	private static final String[] PROPERTY_NAMES = new String[] { "class", "id" };
 
 	public String[] getPropertyNames() {
 		return PROPERTY_NAMES;
 	}
 
 	public Object getPropertyValue(Object component, int i, SessionImplementor session)
 		throws HibernateException {
 
 		return i==0 ?
 				session.bestGuessEntityName(component) :
 				getIdentifier(component, session);
 	}
 
 	public Object[] getPropertyValues(Object component, SessionImplementor session)
 		throws HibernateException {
 
 		return new Object[] { session.bestGuessEntityName(component), getIdentifier(component, session) };
 	}
 
 	private Serializable getIdentifier(Object value, SessionImplementor session) throws HibernateException {
 		try {
 			return ForeignKeys.getEntityIdentifierIfNotUnsaved( session.bestGuessEntityName(value), value, session );
 		}
 		catch (TransientObjectException toe) {
 			return null;
 		}
 	}
 
 	public Type[] getSubtypes() {
 		return new Type[] { metaType, identifierType };
 	}
 
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
 		throws HibernateException {
 
 		throw new UnsupportedOperationException();
 
 	}
 
 	public Object[] getPropertyValues(Object component, EntityMode entityMode) {
 		throw new UnsupportedOperationException();
 	}
 
 	public boolean isComponentType() {
 		return true;
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		//return AssociationType.FOREIGN_KEY_TO_PARENT; //this is better but causes a transient object exception...
 		return ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT;
 	}
 
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
 	throws HibernateException {
 		if (current==null) return old!=null;
 		if (old==null) return current!=null;
 		ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) old;
 		boolean[] idcheckable = new boolean[checkable.length-1];
 		System.arraycopy(checkable, 1, idcheckable, 0, idcheckable.length);
 		return ( checkable[0] && !holder.entityName.equals( session.bestGuessEntityName(current) ) ) ||
 				identifierType.isModified(holder.id, getIdentifier(current, session), idcheckable, session);
 	}
 
 	public String getAssociatedEntityName(SessionFactoryImplementor factory)
 		throws MappingException {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 	
 	public boolean[] getPropertyNullability() {
 		return null;
 	}
 
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
 	throws MappingException {
 		throw new UnsupportedOperationException();
 	}
 	
 	public boolean isReferenceToPrimaryKey() {
 		return true;
 	}
 	
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	public String getLHSPropertyName() {
 		return null;
 	}
 
 	public boolean isAlwaysDirtyChecked() {
 		return false;
 	}
 
 	public boolean isEmbeddedInXML() {
 		return false;
 	}
 	
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan(mapping) ];
 		if (value!=null) Arrays.fill(result, true);
 		return result;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) 
 	throws HibernateException {
 		//TODO!!!
 		return isDirty(old, current, session);
 	}
 
 	public boolean isEmbedded() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java b/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
index 9a4c668044..fe00857f20 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
@@ -1,319 +1,320 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.LoggableUserType;
 
 /**
  * Adapts {@link CompositeUserType} to the {@link Type} interface
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class CompositeCustomType extends AbstractType implements CompositeType, BasicType {
 	private final CompositeUserType userType;
 	private final String[] registrationKeys;
 	private final String name;
 	private final boolean customLogging;
 
 	public CompositeCustomType(CompositeUserType userType) {
 		this( userType, ArrayHelper.EMPTY_STRING_ARRAY );
 	}
 
 	public CompositeCustomType(CompositeUserType userType, String[] registrationKeys) {
 		this.userType = userType;
 		this.name = userType.getClass().getName();
 		this.customLogging = LoggableUserType.class.isInstance( userType );
 		this.registrationKeys = registrationKeys;
 	}
 
 	public String[] getRegistrationKeys() {
 		return registrationKeys;
 	}
 
 	public CompositeUserType getUserType() {
 		return userType;
 	}
 
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	public Type[] getSubtypes() {
 		return userType.getPropertyTypes();
 	}
 
 	public String[] getPropertyNames() {
 		return userType.getPropertyNames();
 	}
 
 	public Object[] getPropertyValues(Object component, SessionImplementor session) throws HibernateException {
 		return getPropertyValues( component, EntityMode.POJO );
 	}
 
 	public Object[] getPropertyValues(Object component, EntityMode entityMode) throws HibernateException {
 
 		int len = getSubtypes().length;
 		Object[] result = new Object[len];
 		for ( int i=0; i<len; i++ ) {
 			result[i] = getPropertyValue(component, i);
 		}
 		return result;
 	}
 
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
 		throws HibernateException {
 
 		for (int i=0; i<values.length; i++) {
 			userType.setPropertyValue( component, i, values[i] );
 		}
 	}
 
 	public Object getPropertyValue(Object component, int i, SessionImplementor session)
 		throws HibernateException {
 		return getPropertyValue(component, i);
 	}
 
 	public Object getPropertyValue(Object component, int i) throws HibernateException {
 		return userType.getPropertyValue( component, i );
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
-		return CascadeStyle.NONE;
+		return CascadeStyles.NONE;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return FetchMode.DEFAULT;
 	}
 
 	public boolean isComponentType() {
 		return true;
 	}
 
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 	throws HibernateException {
 		return userType.deepCopy( value );
 	}
 
 	public Object assemble(
 		Serializable cached,
 		SessionImplementor session,
 		Object owner)
 		throws HibernateException {
 
 		return userType.assemble( cached, session, owner );
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 		return userType.disassemble(value, session);
 	}
 
 	public Object replace(
 			Object original, 
 			Object target,
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache)
 	throws HibernateException {
 		return userType.replace(original, target, session, owner);
 	}
 	
 	public boolean isEqual(Object x, Object y)
 	throws HibernateException {
 		return userType.equals(x, y);
 	}
 
 	public int getHashCode(Object x) {
 		return userType.hashCode(x);
 	}
 	
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		Type[] types = userType.getPropertyTypes();
 		int n=0;
 		for ( Type type : types ) {
 			n += type.getColumnSpan( mapping );
 		}
 		return n;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public Class getReturnedClass() {
 		return userType.returnedClass();
 	}
 
 	public boolean isMutable() {
 		return userType.isMutable();
 	}
 
 	public Object nullSafeGet(
 		ResultSet rs,
 		String columnName,
 		SessionImplementor session,
 		Object owner)
 		throws HibernateException, SQLException {
 
 		return userType.nullSafeGet( rs, new String[] {columnName}, session, owner );
 	}
 
 	public Object nullSafeGet(
 		ResultSet rs,
 		String[] names,
 		SessionImplementor session,
 		Object owner)
 		throws HibernateException, SQLException {
 
 		return userType.nullSafeGet(rs, names, session, owner);
 	}
 
 	public void nullSafeSet(
 		PreparedStatement st,
 		Object value,
 		int index,
 		SessionImplementor session)
 		throws HibernateException, SQLException {
 
 		userType.nullSafeSet(st, value, index, session);
 
 	}
 
 	public void nullSafeSet(
 		PreparedStatement st,
 		Object value,
 		int index,
 		boolean[] settable, 
 		SessionImplementor session)
 		throws HibernateException, SQLException {
 
 		userType.nullSafeSet(st, value, index, session);
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		int[] result = new int[ getColumnSpan(mapping) ];
 		int n=0;
 		for ( Type type : userType.getPropertyTypes() ) {
 			for ( int sqlType : type.sqlTypes( mapping ) ) {
 				result[n++] = sqlType;
 			}
 		}
 		return result;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
 		int soFar = 0;
 		for ( Type propertyType : userType.getPropertyTypes() ) {
 			final Size[] propertySizes = propertyType.dictatedSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
 		int soFar = 0;
 		for ( Type propertyType : userType.getPropertyTypes() ) {
 			final Size[] propertySizes = propertyType.defaultSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 	
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		else if ( customLogging ) {
 			return ( (LoggableUserType) userType ).toLoggableString( value, factory );
 		}
 		else {
 			return value.toString();
 		}
 	}
 
 	public boolean[] getPropertyNullability() {
 		return null;
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return xml;
 	}
 
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory)
 	throws HibernateException {
 		replaceNode( node, (Element) value );
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan(mapping) ];
 		if (value==null) return result;
 		Object[] values = getPropertyValues(value, EntityMode.POJO); //TODO!!!!!!!
 		int loc = 0;
 		Type[] propertyTypes = getSubtypes();
 		for ( int i=0; i<propertyTypes.length; i++ ) {
 			boolean[] propertyNullness = propertyTypes[i].toColumnNullness( values[i], mapping );
 			System.arraycopy(propertyNullness, 0, result, loc, propertyNullness.length);
 			loc += propertyNullness.length;
 		}
 		return result;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
 		return isDirty(old, current, session);
 	}
 	
 	public boolean isEmbedded() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java b/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
index 8c8e9b0c5e..40f8cb9eff 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
@@ -1,191 +1,192 @@
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
 package org.hibernate.test.jpa;
 
 import java.io.Serializable;
 import java.util.IdentityHashMap;
 import javax.persistence.EntityNotFoundException;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.internal.DefaultAutoFlushEventListener;
 import org.hibernate.event.internal.DefaultFlushEntityEventListener;
 import org.hibernate.event.internal.DefaultFlushEventListener;
 import org.hibernate.event.internal.DefaultPersistEventListener;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.AutoFlushEventListener;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.FlushEntityEventListener;
 import org.hibernate.event.spi.FlushEventListener;
 import org.hibernate.event.spi.PersistEventListener;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.BootstrapServiceRegistryBuilder;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * An abstract test for all JPA spec related tests.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractJPATest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "jpa/Part.hbm.xml", "jpa/Item.hbm.xml", "jpa/MyEntity.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.JPAQL_STRICT_COMPLIANCE, "true" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "false" );
 		cfg.setEntityNotFoundDelegate( new JPAEntityNotFoundDelegate() );
 	}
 
 	@Override
 	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryBuilder builder) {
 		builder.with(
 				new Integrator() {
 
 					@Override
 					public void integrate(
 							Configuration configuration,
 							SessionFactoryImplementor sessionFactory,
 							SessionFactoryServiceRegistry serviceRegistry) {
 						integrate( serviceRegistry );
 					}
 
 					@Override
 					public void integrate(
 							MetadataImplementor metadata,
 							SessionFactoryImplementor sessionFactory,
 							SessionFactoryServiceRegistry serviceRegistry) {
 						integrate( serviceRegistry );
 					}
 
 					private void integrate(SessionFactoryServiceRegistry serviceRegistry) {
 						EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 						eventListenerRegistry.setListeners( EventType.PERSIST, buildPersistEventListeners() );
 						eventListenerRegistry.setListeners(
 								EventType.PERSIST_ONFLUSH, buildPersisOnFlushEventListeners()
 						);
 						eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, buildAutoFlushEventListeners() );
 						eventListenerRegistry.setListeners( EventType.FLUSH, buildFlushEventListeners() );
 						eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, buildFlushEntityEventListeners() );
 					}
 
 					@Override
 					public void disintegrate(
 							SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 					}
 				}
 		);
 	}
 
 	@Override
 	public String getCacheConcurrencyStrategy() {
 		// no second level caching
 		return null;
 	}
 
 
 	// mimic specific exception aspects of the JPA environment ~~~~~~~~~~~~~~~~
 
 	private static class JPAEntityNotFoundDelegate implements EntityNotFoundDelegate {
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException("Unable to find " + entityName  + " with id " + id);
 		}
 	}
 
 	// mimic specific event aspects of the JPA environment ~~~~~~~~~~~~~~~~~~~~
 
 	protected PersistEventListener[] buildPersistEventListeners() {
 		return new PersistEventListener[] { new JPAPersistEventListener() };
 	}
 
 	protected PersistEventListener[] buildPersisOnFlushEventListeners() {
 		return new PersistEventListener[] { new JPAPersistOnFlushEventListener() };
 	}
 
 	protected AutoFlushEventListener[] buildAutoFlushEventListeners() {
 		return new AutoFlushEventListener[] { JPAAutoFlushEventListener.INSTANCE };
 	}
 
 	protected FlushEventListener[] buildFlushEventListeners() {
 		return new FlushEventListener[] { JPAFlushEventListener.INSTANCE };
 	}
 
 	protected FlushEntityEventListener[] buildFlushEntityEventListeners() {
 		return new FlushEntityEventListener[] { new JPAFlushEntityEventListener() };
 	}
 
 	public static class JPAPersistEventListener extends DefaultPersistEventListener {
 		// overridden in JPA impl for entity callbacks...
 	}
 
 	public static class JPAPersistOnFlushEventListener extends JPAPersistEventListener {
 		@Override
         protected CascadingAction getCascadeAction() {
-			return CascadingAction.PERSIST_ON_FLUSH;
+			return CascadingActions.PERSIST_ON_FLUSH;
 		}
 	}
 
 	public static class JPAAutoFlushEventListener extends DefaultAutoFlushEventListener {
 		// not sure why EM code has this ...
 		public static final AutoFlushEventListener INSTANCE = new JPAAutoFlushEventListener();
 
 		@Override
         protected CascadingAction getCascadingAction() {
-			return CascadingAction.PERSIST_ON_FLUSH;
+			return CascadingActions.PERSIST_ON_FLUSH;
 		}
 
 		@Override
         protected Object getAnything() {
 			return new IdentityHashMap( 10 );
 		}
 	}
 
 	public static class JPAFlushEventListener extends DefaultFlushEventListener {
 		// not sure why EM code has this ...
 		public static final FlushEventListener INSTANCE = new JPAFlushEventListener();
 
 		@Override
         protected CascadingAction getCascadingAction() {
-			return CascadingAction.PERSIST_ON_FLUSH;
+			return CascadingActions.PERSIST_ON_FLUSH;
 		}
 
 		@Override
         protected Object getAnything() {
 			return new IdentityHashMap( 10 );
 		}
 	}
 
 	public static class JPAFlushEntityEventListener extends DefaultFlushEntityEventListener {
 		// in JPA, used mainly for preUpdate callbacks...
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/engine/spi/JpaCascadeStyle.java b/hibernate-entitymanager/src/main/java/org/hibernate/engine/spi/JpaCascadeStyle.java
deleted file mode 100644
index 2b1b515161..0000000000
--- a/hibernate-entitymanager/src/main/java/org/hibernate/engine/spi/JpaCascadeStyle.java
+++ /dev/null
@@ -1,54 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2009-2012, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.engine.spi;
-
-/**
- * Because CascadeStyle is not opened and package protected,
- * I need to subclass and override the persist alias
- *
- * Note that This class has to be triggered by JpaPersistEventListener at class loading time
- *
- * TODO get rid of it for 3.3
- *
- * @author Emmanuel Bernard
- */
-public abstract class JpaCascadeStyle extends CascadeStyle {
-
-	/**
-	 * cascade using JpaCascadingAction
-	 */
-	public static final CascadeStyle PERSIST_JPA = new CascadeStyle() {
-		public boolean doCascade(CascadingAction action) {
-			return action== JpaCascadingAction.PERSIST_SKIPLAZY
-					|| action==CascadingAction.PERSIST_ON_FLUSH;
-		}
-		public String toString() {
-			return "STYLE_PERSIST_SKIPLAZY";
-		}
-	};
-
-	static {
-		STYLES.put( "persist", PERSIST_JPA );
-	}
-}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/engine/spi/JpaCascadingAction.java b/hibernate-entitymanager/src/main/java/org/hibernate/engine/spi/JpaCascadingAction.java
deleted file mode 100644
index 99da8ad2ba..0000000000
--- a/hibernate-entitymanager/src/main/java/org/hibernate/engine/spi/JpaCascadingAction.java
+++ /dev/null
@@ -1,71 +0,0 @@
-/*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.engine.spi;
-
-import java.util.Iterator;
-import java.util.Map;
-
-import org.jboss.logging.Logger;
-
-import org.hibernate.HibernateException;
-import org.hibernate.event.spi.EventSource;
-import org.hibernate.type.CollectionType;
-
-/**
- * Because of CascadingAction constructor visibility
- * I need a packaged friendly subclass
- * TODO Get rid of it for 3.3
- * @author Emmanuel Bernard
- */
-public abstract class JpaCascadingAction extends CascadingAction {
-    private static final Logger LOG = Logger.getLogger( JpaCascadingAction.class.getName() );
-
-	/**
-	 * @see org.hibernate.Session#persist(Object)
-	 */
-	public static final CascadingAction PERSIST_SKIPLAZY = new CascadingAction() {
-		@Override
-        public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
-		throws HibernateException {
-            LOG.trace("Cascading to persist: " + entityName);
-			session.persist( entityName, child, (Map) anything );
-		}
-		@Override
-        public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
-			// persists don't cascade to uninitialized collections
-			return CascadingAction.getLoadedElementsIterator( session, collectionType, collection );
-		}
-		@Override
-        public boolean deleteOrphans() {
-			return false;
-		}
-		@Override
-        public boolean performOnLazyProperty() {
-			return false;
-		}
-		@Override
-        public String toString() {
-			return "ACTION_PERSIST_SKIPLAZY";
-		}
-	};
-
-}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaAutoFlushEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaAutoFlushEventListener.java
index b55ca1a5e8..d95ce64369 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaAutoFlushEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaAutoFlushEventListener.java
@@ -1,54 +1,55 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.jpa.internal.event;
 
 import java.util.IdentityHashMap;
 
 import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.event.internal.DefaultAutoFlushEventListener;
 import org.hibernate.event.spi.AutoFlushEventListener;
 
 /**
  * In JPA, it is the create operation that is cascaded to unmanaged entities at flush time (instead of the save-update
  * operation in Hibernate).
  *
  * @author Gavin King
  */
 public class JpaAutoFlushEventListener
 		extends DefaultAutoFlushEventListener
 		implements HibernateEntityManagerEventListener {
 
 	public static final AutoFlushEventListener INSTANCE = new JpaAutoFlushEventListener();
 
 	@Override
 	protected CascadingAction getCascadingAction() {
-		return CascadingAction.PERSIST_ON_FLUSH;
+		return CascadingActions.PERSIST_ON_FLUSH;
 	}
 
 	@Override
 	protected Object getAnything() {
 		return new IdentityHashMap( 10 );
 	}
 
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaFlushEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaFlushEventListener.java
index 836489a259..4ebcdb5d35 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaFlushEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaFlushEventListener.java
@@ -1,51 +1,52 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.jpa.internal.event;
 
 import java.util.IdentityHashMap;
 
 import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.event.internal.DefaultFlushEventListener;
 import org.hibernate.event.spi.FlushEventListener;
 
 /**
  * In JPA, it is the create operation that is cascaded to unmanaged entities at flush time (instead of the
  * save-update operation in Hibernate).
  *
  * @author Gavin King
  */
 public class JpaFlushEventListener extends DefaultFlushEventListener implements HibernateEntityManagerEventListener {
 	public static final FlushEventListener INSTANCE = new JpaFlushEventListener();
 
 	@Override
 	protected CascadingAction getCascadingAction() {
-		return CascadingAction.PERSIST_ON_FLUSH;
+		return CascadingActions.PERSIST_ON_FLUSH;
 	}
 
 	@Override
 	protected Object getAnything() {
 		return new IdentityHashMap( 10 );
 	}
 
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaIntegrator.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaIntegrator.java
index cdc26a8780..b5ed16ad92 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaIntegrator.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaIntegrator.java
@@ -1,263 +1,285 @@
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
 package org.hibernate.jpa.internal.event;
 
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.ejb.AvailableSettings;
+import org.hibernate.engine.spi.CascadeStyles;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.service.spi.DuplicationStrategy;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.integrator.spi.Integrator;
+import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.secure.internal.JACCPreDeleteEventListener;
 import org.hibernate.secure.internal.JACCPreInsertEventListener;
 import org.hibernate.secure.internal.JACCPreLoadEventListener;
 import org.hibernate.secure.internal.JACCPreUpdateEventListener;
 import org.hibernate.secure.internal.JACCSecurityListener;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 /**
  * Prepare the HEM-specific event listeners.
  *
  * @author Steve Ebersole
  */
 public class JpaIntegrator implements Integrator {
 	private static final DuplicationStrategy JPA_DUPLICATION_STRATEGY = new DuplicationStrategy() {
 		@Override
 		public boolean areMatch(Object listener, Object original) {
 			return listener.getClass().equals( original.getClass() ) &&
 					HibernateEntityManagerEventListener.class.isInstance( original );
 		}
 
 		@Override
 		public Action getAction() {
 			return Action.KEEP_ORIGINAL;
 		}
 	};
 
 	private static final DuplicationStrategy JACC_DUPLICATION_STRATEGY = new DuplicationStrategy() {
 		@Override
 		public boolean areMatch(Object listener, Object original) {
 			return listener.getClass().equals( original.getClass() ) &&
 					JACCSecurityListener.class.isInstance( original );
 		}
 
 		@Override
 		public Action getAction() {
 			return Action.KEEP_ORIGINAL;
 		}
 	};
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public void integrate(
 			Configuration configuration,
 			SessionFactoryImplementor sessionFactory,
 			SessionFactoryServiceRegistry serviceRegistry) {
+		// first, register the JPA-specific persist cascade style
+		CascadeStyles.registerCascadeStyle(
+				"persist",
+				new CascadeStyles.BaseCascadeStyle() {
+					@Override
+					public boolean doCascade(CascadingAction action) {
+						return action == JpaPersistEventListener.PERSIST_SKIPLAZY
+								|| action == CascadingActions.PERSIST_ON_FLUSH;
+					}
+
+					@Override
+					public String toString() {
+						return "STYLE_PERSIST_SKIPLAZY";
+					}
+				}
+		);
+
+		// then prepare listeners
 		final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 
 		boolean isSecurityEnabled = configuration.getProperties().containsKey( AvailableSettings.JACC_ENABLED );
 
 		eventListenerRegistry.addDuplicationStrategy( JPA_DUPLICATION_STRATEGY );
 		eventListenerRegistry.addDuplicationStrategy( JACC_DUPLICATION_STRATEGY );
 
 		// op listeners
 		eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, JpaAutoFlushEventListener.INSTANCE );
 		eventListenerRegistry.setListeners( EventType.DELETE, new JpaDeleteEventListener() );
 		eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, new JpaFlushEntityEventListener() );
 		eventListenerRegistry.setListeners( EventType.FLUSH, JpaFlushEventListener.INSTANCE );
 		eventListenerRegistry.setListeners( EventType.MERGE, new JpaMergeEventListener() );
 		eventListenerRegistry.setListeners( EventType.PERSIST, new JpaPersistEventListener() );
 		eventListenerRegistry.setListeners( EventType.PERSIST_ONFLUSH, new JpaPersistOnFlushEventListener() );
 		eventListenerRegistry.setListeners( EventType.SAVE, new JpaSaveEventListener() );
 		eventListenerRegistry.setListeners( EventType.SAVE_UPDATE, new JpaSaveOrUpdateEventListener() );
 
 		// pre op listeners
 		if ( isSecurityEnabled ) {
 			final String jaccContextId = configuration.getProperty( Environment.JACC_CONTEXTID );
 			eventListenerRegistry.prependListeners( EventType.PRE_DELETE, new JACCPreDeleteEventListener(jaccContextId) );
 			eventListenerRegistry.prependListeners( EventType.PRE_INSERT, new JACCPreInsertEventListener(jaccContextId) );
 			eventListenerRegistry.prependListeners( EventType.PRE_UPDATE, new JACCPreUpdateEventListener(jaccContextId) );
 			eventListenerRegistry.prependListeners( EventType.PRE_LOAD, new JACCPreLoadEventListener(jaccContextId) );
 		}
 
 		// post op listeners
 		eventListenerRegistry.prependListeners( EventType.POST_DELETE, new JpaPostDeleteEventListener() );
 		eventListenerRegistry.prependListeners( EventType.POST_INSERT, new JpaPostInsertEventListener() );
 		eventListenerRegistry.prependListeners( EventType.POST_LOAD, new JpaPostLoadEventListener() );
 		eventListenerRegistry.prependListeners( EventType.POST_UPDATE, new JpaPostUpdateEventListener() );
 
 		for ( Map.Entry<?,?> entry : configuration.getProperties().entrySet() ) {
 			if ( ! String.class.isInstance( entry.getKey() ) ) {
 				continue;
 			}
 			final String propertyName = (String) entry.getKey();
 			if ( ! propertyName.startsWith( AvailableSettings.EVENT_LISTENER_PREFIX ) ) {
 				continue;
 			}
 			final String eventTypeName = propertyName.substring( AvailableSettings.EVENT_LISTENER_PREFIX.length() + 1 );
 			final EventType eventType = EventType.resolveEventTypeByName( eventTypeName );
 			final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
 			for ( String listenerImpl : ( (String) entry.getValue() ).split( " ," ) ) {
 				eventListenerGroup.appendListener( instantiate( listenerImpl, serviceRegistry ) );
 			}
 		}
 
 		final EntityCallbackHandler callbackHandler = new EntityCallbackHandler();
 		Iterator classes = configuration.getClassMappings();
 		ReflectionManager reflectionManager = configuration.getReflectionManager();
 		while ( classes.hasNext() ) {
 			PersistentClass clazz = (PersistentClass) classes.next();
 			if ( clazz.getClassName() == null ) {
 				//we can have non java class persisted by hibernate
 				continue;
 			}
 			try {
 				callbackHandler.add( reflectionManager.classForName( clazz.getClassName(), this.getClass() ), reflectionManager );
 			}
 			catch (ClassNotFoundException e) {
 				throw new MappingException( "entity class not found: " + clazz.getNodeName(), e );
 			}
 		}
 
 		for ( EventType eventType : EventType.values() ) {
 			final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
 			for ( Object listener : eventListenerGroup.listeners() ) {
 				if ( CallbackHandlerConsumer.class.isInstance( listener ) ) {
 					( (CallbackHandlerConsumer) listener ).setCallbackHandler( callbackHandler );
 				}
 			}
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @see org.hibernate.integrator.spi.Integrator#integrate(org.hibernate.metamodel.source.MetadataImplementor, org.hibernate.engine.spi.SessionFactoryImplementor, org.hibernate.service.spi.SessionFactoryServiceRegistry)
 	 */
 	@Override
 	public void integrate( MetadataImplementor metadata,
 	                       SessionFactoryImplementor sessionFactory,
 	                       SessionFactoryServiceRegistry serviceRegistry ) {
         final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 
         boolean isSecurityEnabled = sessionFactory.getProperties().containsKey( AvailableSettings.JACC_ENABLED );
 
         eventListenerRegistry.addDuplicationStrategy( JPA_DUPLICATION_STRATEGY );
         eventListenerRegistry.addDuplicationStrategy( JACC_DUPLICATION_STRATEGY );
 
         // op listeners
         eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, JpaAutoFlushEventListener.INSTANCE );
         eventListenerRegistry.setListeners( EventType.DELETE, new JpaDeleteEventListener() );
         eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, new JpaFlushEntityEventListener() );
         eventListenerRegistry.setListeners( EventType.FLUSH, JpaFlushEventListener.INSTANCE );
         eventListenerRegistry.setListeners( EventType.MERGE, new JpaMergeEventListener() );
         eventListenerRegistry.setListeners( EventType.PERSIST, new JpaPersistEventListener() );
         eventListenerRegistry.setListeners( EventType.PERSIST_ONFLUSH, new JpaPersistOnFlushEventListener() );
         eventListenerRegistry.setListeners( EventType.SAVE, new JpaSaveEventListener() );
         eventListenerRegistry.setListeners( EventType.SAVE_UPDATE, new JpaSaveOrUpdateEventListener() );
 
         // pre op listeners
         if ( isSecurityEnabled ) {
             final String jaccContextId = sessionFactory.getProperties().getProperty( Environment.JACC_CONTEXTID );
             eventListenerRegistry.prependListeners( EventType.PRE_DELETE, new JACCPreDeleteEventListener(jaccContextId) );
             eventListenerRegistry.prependListeners( EventType.PRE_INSERT, new JACCPreInsertEventListener(jaccContextId) );
             eventListenerRegistry.prependListeners( EventType.PRE_UPDATE, new JACCPreUpdateEventListener(jaccContextId) );
             eventListenerRegistry.prependListeners( EventType.PRE_LOAD, new JACCPreLoadEventListener(jaccContextId) );
         }
 
         // post op listeners
         eventListenerRegistry.prependListeners( EventType.POST_DELETE, new JpaPostDeleteEventListener() );
         eventListenerRegistry.prependListeners( EventType.POST_INSERT, new JpaPostInsertEventListener() );
         eventListenerRegistry.prependListeners( EventType.POST_LOAD, new JpaPostLoadEventListener() );
         eventListenerRegistry.prependListeners( EventType.POST_UPDATE, new JpaPostUpdateEventListener() );
 
         for ( Map.Entry<?,?> entry : sessionFactory.getProperties().entrySet() ) {
             if ( ! String.class.isInstance( entry.getKey() ) ) {
                 continue;
             }
             final String propertyName = (String) entry.getKey();
             if ( ! propertyName.startsWith( AvailableSettings.EVENT_LISTENER_PREFIX ) ) {
                 continue;
             }
             final String eventTypeName = propertyName.substring( AvailableSettings.EVENT_LISTENER_PREFIX.length() + 1 );
             final EventType eventType = EventType.resolveEventTypeByName( eventTypeName );
             final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
             for ( String listenerImpl : ( (String) entry.getValue() ).split( " ," ) ) {
                 eventListenerGroup.appendListener( instantiate( listenerImpl, serviceRegistry ) );
             }
         }
 
         final EntityCallbackHandler callbackHandler = new EntityCallbackHandler();
         ClassLoaderService classLoaderSvc = serviceRegistry.getService(ClassLoaderService.class);
         for (EntityBinding binding : metadata.getEntityBindings()) {
             String name = binding.getEntity().getName(); // Should this be getClassName()?
             if (name == null) {
                 //we can have non java class persisted by hibernate
                 continue;
             }
             try {
                 callbackHandler.add(classLoaderSvc.classForName(name), classLoaderSvc, binding);
-            } catch (ClassLoadingException error) {
+            }
+			catch (ClassLoadingException error) {
                 throw new MappingException( "entity class not found: " + name, error );
             }
         }
 //
 //        for ( EventType eventType : EventType.values() ) {
 //            final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
 //            for ( Object listener : eventListenerGroup.listeners() ) {
 //                if ( CallbackHandlerConsumer.class.isInstance( listener ) ) {
 //                    ( (CallbackHandlerConsumer) listener ).setCallbackHandler( callbackHandler );
 //                }
 //            }
 //        }
 	}
 
 	@Override
 	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 	}
 
 	private Object instantiate(String listenerImpl, ServiceRegistryImplementor serviceRegistry) {
 		try {
 			return serviceRegistry.getService( ClassLoaderService.class ).classForName( listenerImpl ).newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "Could not instantiate requested listener [" + listenerImpl + "]", e );
         }
     }
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaPersistEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaPersistEventListener.java
index 4f584d4a1b..ccb6235a99 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaPersistEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaPersistEventListener.java
@@ -1,86 +1,115 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.jpa.internal.event;
 
 import java.io.Serializable;
+import java.util.Iterator;
+import java.util.Map;
 
+import org.jboss.logging.Logger;
+
+import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.CascadingAction;
-import org.hibernate.engine.spi.JpaCascadeStyle;
-import org.hibernate.engine.spi.JpaCascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.event.internal.DefaultPersistEventListener;
 import org.hibernate.event.spi.EventSource;
+import org.hibernate.type.CollectionType;
 
 /**
  * Overrides the LifeCycle OnSave call to call the PrePersist operation
  *
  * @author Emmanuel Bernard
  */
 public class JpaPersistEventListener extends DefaultPersistEventListener implements CallbackHandlerConsumer {
-	static {
-		JpaCascadeStyle.PERSIST_JPA.hasOrphanDelete(); //triggers class loading to override persist with PERSIST_JPA
-	}
+	private static final Logger log = Logger.getLogger( JpaPersistEventListener.class );
 
 	private EntityCallbackHandler callbackHandler;
 
 	@Override
 	public void setCallbackHandler(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public JpaPersistEventListener() {
 		super();
 	}
 
 	public JpaPersistEventListener(EntityCallbackHandler callbackHandler) {
 		super();
 		this.callbackHandler = callbackHandler;
 	}
 
 	@Override
 	protected Serializable saveWithRequestedId(
 			Object entity,
 			Serializable requestedId,
 			String entityName,
 			Object anything,
 			EventSource source) {
 		callbackHandler.preCreate( entity );
 		return super.saveWithRequestedId( entity, requestedId, entityName, anything, source );
 	}
 
 	@Override
 	protected Serializable saveWithGeneratedId(
 			Object entity,
 			String entityName,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 		callbackHandler.preCreate( entity );
 		return super.saveWithGeneratedId( entity, entityName, anything, source, requiresImmediateIdAccess );
 	}
 
 	@Override
 	protected CascadingAction getCascadeAction() {
-		return JpaCascadingAction.PERSIST_SKIPLAZY;
+		return PERSIST_SKIPLAZY;
 	}
+
+	public static final CascadingAction PERSIST_SKIPLAZY = new CascadingActions.BaseCascadingAction() {
+		@Override
+		public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
+				throws HibernateException {
+			log.trace( "Cascading persist to : " + entityName );
+			session.persist( entityName, child, (Map) anything );
+		}
+		@Override
+		public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
+			// persists don't cascade to uninitialized collections
+			return CascadingActions.getLoadedElementsIterator( session, collectionType, collection );
+		}
+		@Override
+		public boolean deleteOrphans() {
+			return false;
+		}
+		@Override
+		public boolean performOnLazyProperty() {
+			return false;
+		}
+		@Override
+		public String toString() {
+			return "ACTION_PERSIST_SKIPLAZY";
+		}
+	};
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaPersistOnFlushEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaPersistOnFlushEventListener.java
index 88a91ed4ce..132c4aedb9 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaPersistOnFlushEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/event/JpaPersistOnFlushEventListener.java
@@ -1,36 +1,37 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.jpa.internal.event;
 
 import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CascadingActions;
 
 /**
  * @author Emmanuel Bernard
  */
 public class JpaPersistOnFlushEventListener extends JpaPersistEventListener {
 	@Override
 	protected CascadingAction getCascadeAction() {
-		return CascadingAction.PERSIST_ON_FLUSH;
+		return CascadingActions.PERSIST_ON_FLUSH;
 	}
 }
