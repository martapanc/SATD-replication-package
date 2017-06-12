diff --git a/core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java b/core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
index 99f44bbcf7..17da40eb2a 100644
--- a/core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
+++ b/core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
@@ -1,77 +1,86 @@
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
 package org.hibernate.transform;
 
-import java.util.ArrayList;
-import java.util.HashSet;
 import java.util.List;
-import java.util.Set;
-
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import java.io.Serializable;
 
 /**
+ * Much like {@link RootEntityResultTransformer}, but we also distinct
+ * the entity in the final result.
+ * <p/>
+ * Since this transformer is stateless, all instances would be considered equal.
+ * So for optimization purposes we limit it to a single, singleton {@link #INSTANCE instance}.
+ *
  * @author Gavin King
+ * @author Steve Ebersole
  */
-public class DistinctRootEntityResultTransformer implements ResultTransformer {
+public class DistinctRootEntityResultTransformer implements ResultTransformer, Serializable {
 
-	private static final Logger log = LoggerFactory.getLogger(DistinctRootEntityResultTransformer.class);
+	public static final DistinctRootEntityResultTransformer INSTANCE = new DistinctRootEntityResultTransformer();
 
-	static final class Identity {
-		final Object entity;
-		Identity(Object entity) {
-			this.entity = entity;
-		}
-		public boolean equals(Object other) {
-			Identity that = (Identity) other;
-			return entity==that.entity;
-		}
-		public int hashCode() {
-			return System.identityHashCode(entity);
-		}
+	/**
+	 * Instantiate a DistinctRootEntityResultTransformer.
+	 *
+	 * @deprecated Use the {@link #INSTANCE} reference instead of explicitly creating a new one.
+	 */
+	public DistinctRootEntityResultTransformer() {
 	}
 
+	/**
+	 * Simply delegates to {@link RootEntityResultTransformer#transformTuple}.
+	 *
+	 * @param tuple The tuple to transform
+	 * @param aliases The tuple aliases
+	 * @return The transformed tuple row.
+	 */
 	public Object transformTuple(Object[] tuple, String[] aliases) {
-		return tuple[ tuple.length-1 ];
+		return RootEntityResultTransformer.INSTANCE.transformTuple( tuple, aliases );
 	}
 
+	/**
+	 * Simply delegates to {@link DistinctResultTransformer#transformList}.
+	 *
+	 * @param list The list to transform.
+	 * @return The transformed List.
+	 */
 	public List transformList(List list) {
-		List result = new ArrayList( list.size() );
-		Set distinct = new HashSet();
-		for ( int i=0; i<list.size(); i++ ) {
-			Object entity = list.get(i);
-			if ( distinct.add( new Identity(entity) ) ) {
-				result.add(entity);
-			}
-		}
-		if ( log.isDebugEnabled() ) log.debug(
-			"transformed: " +
-			list.size() + " rows to: " +
-			result.size() + " distinct results"
-		);
-		return result;
+		return DistinctResultTransformer.INSTANCE.transformList( list );
 	}
 
+	/**
+	 * Serialization hook for ensuring singleton uniqueing.
+	 *
+	 * @return The singleton instance : {@link #INSTANCE}
+	 */
+	private Object readResolve() {
+		return INSTANCE;
+	}
+
+	public boolean equals(Object obj) {
+		// todo : we can remove this once the deprecated ctor can be made private...
+		return DistinctRootEntityResultTransformer.class.isInstance( obj );
+	}
 }
