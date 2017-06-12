diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/ArrayHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/ArrayHelper.java
index cf87f9db24..f25b8a65ef 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/ArrayHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/ArrayHelper.java
@@ -1,371 +1,382 @@
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
 package org.hibernate.internal.util.collections;
 
 import java.lang.reflect.Array;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
+
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.type.Type;
 
 public final class ArrayHelper {
 	
 	/*public static boolean contains(Object[] array, Object object) {
 		for ( int i=0; i<array.length; i++ ) {
 			if ( array[i].equals(object) ) return true;
 		}
 		return false;
 	}*/
 	
 	public static int indexOf(Object[] array, Object object) {
 		for ( int i=0; i<array.length; i++ ) {
 			if ( array[i].equals(object) ) return i;
 		}
 		return -1;
 	}
 	
 	/*public static Object[] clone(Class elementClass, Object[] array) {
 		Object[] result = (Object[]) Array.newInstance( elementClass, array.length );
 		System.arraycopy(array, 0, result, 0, array.length);
 		return result;
 	}*/
 
 	public static String[] toStringArray(Object[] objects) {
 		int length=objects.length;
 		String[] result = new String[length];
 		for (int i=0; i<length; i++) {
 			result[i] = objects[i].toString();
 		}
 		return result;
 	}
 
 	public static String[] fillArray(String value, int length) {
 		String[] result = new String[length];
 		Arrays.fill(result, value);
 		return result;
 	}
 
 	public static int[] fillArray(int value, int length) {
 		int[] result = new int[length];
 		Arrays.fill(result, value);
 		return result;
 	}
 
 	public static LockMode[] fillArray(LockMode lockMode, int length) {
 		LockMode[] array = new LockMode[length];
 		Arrays.fill(array, lockMode);
 		return array;
 	}
 
 	public static LockOptions[] fillArray(LockOptions lockOptions, int length) {
 		LockOptions[] array = new LockOptions[length];
 		Arrays.fill(array, lockOptions);
 		return array;
 	}
 
 	public static String[] toStringArray(Collection coll) {
 		return (String[]) coll.toArray( new String[coll.size()] );
 	}
 	
 	public static String[][] to2DStringArray(Collection coll) {
 		return (String[][]) coll.toArray( new String[ coll.size() ][] );
 	}
 	
 	public static int[][] to2DIntArray(Collection coll) {
 		return (int[][]) coll.toArray( new int[ coll.size() ][] );
 	}
 	
 	public static Type[] toTypeArray(Collection coll) {
 		return (Type[]) coll.toArray( new Type[coll.size()] );
 	}
 
 	public static int[] toIntArray(Collection coll) {
 		Iterator iter = coll.iterator();
 		int[] arr = new int[ coll.size() ];
 		int i=0;
 		while( iter.hasNext() ) {
 			arr[i++] = ( (Integer) iter.next() ).intValue();
 		}
 		return arr;
 	}
 
 	public static boolean[] toBooleanArray(Collection coll) {
 		Iterator iter = coll.iterator();
 		boolean[] arr = new boolean[ coll.size() ];
 		int i=0;
 		while( iter.hasNext() ) {
 			arr[i++] = ( (Boolean) iter.next() ).booleanValue();
 		}
 		return arr;
 	}
 
 	public static Object[] typecast(Object[] array, Object[] to) {
 		return java.util.Arrays.asList(array).toArray(to);
 	}
 
 	//Arrays.asList doesn't do primitive arrays
 	public static List toList(Object array) {
 		if ( array instanceof Object[] ) return Arrays.asList( (Object[]) array ); //faster?
 		int size = Array.getLength(array);
 		ArrayList list = new ArrayList(size);
 		for (int i=0; i<size; i++) {
 			list.add( Array.get(array, i) );
 		}
 		return list;
 	}
 
 	public static String[] slice(String[] strings, int begin, int length) {
 		String[] result = new String[length];
 		System.arraycopy( strings, begin, result, 0, length );
 		return result;
 	}
 
 	public static Object[] slice(Object[] objects, int begin, int length) {
 		Object[] result = new Object[length];
 		System.arraycopy( objects, begin, result, 0, length );
 		return result;
 	}
 
 	public static List toList(Iterator iter) {
 		List list = new ArrayList();
 		while ( iter.hasNext() ) {
 			list.add( iter.next() );
 		}
 		return list;
 	}
 
 	public static String[] join(String[] x, String[] y) {
 		String[] result = new String[ x.length + y.length ];
 		System.arraycopy( x, 0, result, 0, x.length );
 		System.arraycopy( y, 0, result, x.length, y.length );
 		return result;
 	}
 
 	public static String[] join(String[] x, String[] y, boolean[] use) {
 		String[] result = new String[ x.length + countTrue(use) ];
 		System.arraycopy( x, 0, result, 0, x.length );
 		int k = x.length;
 		for ( int i=0; i<y.length; i++ ) {
 			if ( use[i] ) {
 				result[k++] = y[i];
 			}
 		}
 		return result;
 	}
 
 	public static int[] join(int[] x, int[] y) {
 		int[] result = new int[ x.length + y.length ];
 		System.arraycopy( x, 0, result, 0, x.length );
 		System.arraycopy( y, 0, result, x.length, y.length );
 		return result;
 	}
 
+	@SuppressWarnings( {"unchecked"})
+	public static <T> T[] join(T[] x, T[] y) {
+		T[] result = (T[]) Array.newInstance( x.getClass().getComponentType(), x.length + y.length );
+		System.arraycopy( x, 0, result, 0, x.length );
+		System.arraycopy( y, 0, result, x.length, y.length );
+		return result;
+	}
+
 	public static final boolean[] TRUE = { true };
 	public static final boolean[] FALSE = { false };
 
 	private ArrayHelper() {}
 
 	public static String toString( Object[] array ) {
 		StringBuffer sb = new StringBuffer();
 		sb.append("[");
 		for (int i = 0; i < array.length; i++) {
 			sb.append( array[i] );
 			if( i<array.length-1 ) sb.append(",");
 		}
 		sb.append("]");
 		return sb.toString();
 	}
 
 	public static boolean isAllNegative(int[] array) {
 		for ( int i=0; i<array.length; i++ ) {
 			if ( array[i] >=0 ) return false;
 		}
 		return true;
 	}
 
 	public static boolean isAllTrue(boolean[] array) {
 		for ( int i=0; i<array.length; i++ ) {
 			if ( !array[i] ) return false;
 		}
 		return true;
 	}
 
 	public static int countTrue(boolean[] array) {
 		int result=0;
 		for ( int i=0; i<array.length; i++ ) {
 			if ( array[i] ) result++;
 		}
 		return result;
 	}
 
 	/*public static int countFalse(boolean[] array) {
 		int result=0;
 		for ( int i=0; i<array.length; i++ ) {
 			if ( !array[i] ) result++;
 		}
 		return result;
 	}*/
 
 	public static boolean isAllFalse(boolean[] array) {
 		for ( int i=0; i<array.length; i++ ) {
 			if ( array[i] ) return false;
 		}
 		return true;
 	}
 
 	public static void addAll(Collection collection, Object[] array) {
 		collection.addAll( Arrays.asList( array ) );
 	}
 
 	public static final String[] EMPTY_STRING_ARRAY = {};
 	public static final int[] EMPTY_INT_ARRAY = {};
 	public static final boolean[] EMPTY_BOOLEAN_ARRAY = {};
 	public static final Class[] EMPTY_CLASS_ARRAY = {};
 	public static final Object[] EMPTY_OBJECT_ARRAY = {};
 	public static final Type[] EMPTY_TYPE_ARRAY = {};
 	
 	public static int[] getBatchSizes(int maxBatchSize) {
 		int batchSize = maxBatchSize;
 		int n=1;
 		while ( batchSize>1 ) {
 			batchSize = getNextBatchSize(batchSize);
 			n++;
 		}
 		int[] result = new int[n];
 		batchSize = maxBatchSize;
 		for ( int i=0; i<n; i++ ) {
 			result[i] = batchSize;
 			batchSize = getNextBatchSize(batchSize);
 		}
 		return result;
 	}
 	
 	private static int getNextBatchSize(int batchSize) {
 		if (batchSize<=10) {
 			return batchSize-1; //allow 9,8,7,6,5,4,3,2,1
 		}
 		else if (batchSize/2 < 10) {
 			return 10;
 		}
 		else {
 			return batchSize / 2;
 		}
 	}
 
 	private static int SEED = 23;
 	private static int PRIME_NUMER = 37;
 
 	/**
 	 * calculate the array hash (only the first level)
 	 */
 	public static int hash(Object[] array) {
 		int length = array.length;
 		int seed = SEED;
 		for (int index = 0 ; index < length ; index++) {
 			seed = hash( seed, array[index] == null ? 0 : array[index].hashCode() );
 		}
 		return seed;
 	}
 
 	/**
 	 * calculate the array hash (only the first level)
 	 */
 	public static int hash(char[] array) {
 		int length = array.length;
 		int seed = SEED;
 		for (int index = 0 ; index < length ; index++) {
 			seed = hash( seed, array[index] ) ;
 		}
 		return seed;
 	}
 
 	/**
 	 * calculate the array hash (only the first level)
 	 */
 	public static int hash(byte[] bytes) {
 		int length = bytes.length;
 		int seed = SEED;
 		for (int index = 0 ; index < length ; index++) {
 			seed = hash( seed, bytes[index] ) ;
 		}
 		return seed;
 	}
 
 	private static int hash(int seed, int i) {
 		return PRIME_NUMER * seed + i;
 	}
 
 	/**
 	 * Compare 2 arrays only at the first level
 	 */
 	public static boolean isEquals(Object[] o1, Object[] o2) {
 		if (o1 == o2) return true;
 		if (o1 == null || o2 == null) return false;
 		int length = o1.length;
 		if (length != o2.length) return false;
 		for (int index = 0 ; index < length ; index++) {
 			if ( ! o1[index].equals( o2[index] ) ) return false;
 		}
         return true;
 	}
 
 	/**
 	 * Compare 2 arrays only at the first level
 	 */
 	public static boolean isEquals(char[] o1, char[] o2) {
 		if (o1 == o2) return true;
 		if (o1 == null || o2 == null) return false;
 		int length = o1.length;
 		if (length != o2.length) return false;
 		for (int index = 0 ; index < length ; index++) {
 			if ( ! ( o1[index] == o2[index] ) ) return false;
 		}
         return true;
 	}
 
 	/**
 	 * Compare 2 arrays only at the first level
 	 */
 	public static boolean isEquals(byte[] b1, byte[] b2) {
 		if (b1 == b2) return true;
 		if (b1 == null || b2 == null) return false;
 		int length = b1.length;
 		if (length != b2.length) return false;
 		for (int index = 0 ; index < length ; index++) {
 			if ( ! ( b1[index] == b2[index] ) ) return false;
 		}
         return true;
 	}
+
+
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Column.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Column.java
index 6da4cc43ad..4b6128abcc 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Column.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Column.java
@@ -1,230 +1,134 @@
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
 
 /**
  * Models a physical column
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class Column extends AbstractSimpleValue implements SimpleValue {
 	private final String name;
 	private boolean nullable;
 	private boolean unique;
 
 	private String defaultValue;
 	private String checkCondition;
 	private String sqlType;
 
 	private String readFragment;
 	private String writeFragment;
 
 	private String comment;
 
 	private Size size = new Size();
 
 	protected Column(TableSpecification table, int position, String name) {
 		super( table, position );
 		this.name = name;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public boolean isNullable() {
 		return nullable;
 	}
 
 	public void setNullable(boolean nullable) {
 		this.nullable = nullable;
 	}
 
 	public boolean isUnique() {
 		return unique;
 	}
 
 	public void setUnique(boolean unique) {
 		this.unique = unique;
 	}
 
 	public String getDefaultValue() {
 		return defaultValue;
 	}
 
 	public void setDefaultValue(String defaultValue) {
 		this.defaultValue = defaultValue;
 	}
 
 	public String getCheckCondition() {
 		return checkCondition;
 	}
 
 	public void setCheckCondition(String checkCondition) {
 		this.checkCondition = checkCondition;
 	}
 
 	public String getSqlType() {
 		return sqlType;
 	}
 
 	public void setSqlType(String sqlType) {
 		this.sqlType = sqlType;
 	}
 
 	public String getReadFragment() {
 		return readFragment;
 	}
 
 	public void setReadFragment(String readFragment) {
 		this.readFragment = readFragment;
 	}
 
 	public String getWriteFragment() {
 		return writeFragment;
 	}
 
 	public void setWriteFragment(String writeFragment) {
 		this.writeFragment = writeFragment;
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
 
 	public Size getSize() {
 		return size;
 	}
 
 	public void setSize(Size size) {
 		this.size = size;
 	}
 
 	@Override
 	public String toLoggableString() {
 		return getTable().getLoggableValueQualifier() + '.' + getName();
 	}
 
-	/**
-	 * Models size restrictions/requirements on a column's datatype.
-	 * <p/>
-	 * IMPL NOTE: since we do not necessarily know the datatype up front, and therefore do not necessarily know
-	 * whether length or precision/scale sizing is needed, we simply account for both here.  Additionally LOB
-	 * definitions, by standard, are allowed a "multiplier" consisting of 'K' (Kb), 'M' (Mb) or 'G' (Gb).
-	 */
-	public static class Size {
-		private static enum LobMultiplier {
-			NONE( 1 ),
-			K( NONE.factor * 1024 ),
-			M( K.factor * 1024 ),
-			G( M.factor * 1024 );
-
-			private long factor;
-
-			private LobMultiplier(long factor) {
-				this.factor = factor;
-			}
-
-			public long getFactor() {
-				return factor;
-			}
-		}
-
-		private int precision = - 1;
-		private int scale = -1;
-		private long length = -1;
-		private LobMultiplier lobMultiplier = LobMultiplier.NONE;
-
-		public Size() {
-		}
-
-		/**
-		 * Complete constructor.
-		 *
-		 * @param precision numeric precision
-		 * @param scale numeric scale
-		 * @param length type length
-		 * @param lobMultiplier LOB length multiplier
-		 */
-		public Size(int precision, int scale, long length, LobMultiplier lobMultiplier) {
-			this.precision = precision;
-			this.scale = scale;
-			this.length = length;
-			this.lobMultiplier = lobMultiplier;
-		}
-
-		public static Size precision(int precision) {
-			return new Size( precision, -1, -1, null );
-		}
-
-		public static Size precision(int precision, int scale) {
-			return new Size( precision, scale, -1, null );
-		}
-
-		public static Size length(long length) {
-			return new Size( -1, -1, length, null );
-		}
-
-		public static Size length(long length, LobMultiplier lobMultiplier) {
-			return new Size( -1, -1, length, lobMultiplier );
-		}
-
-		public int getPrecision() {
-			return precision;
-		}
-
-		public int getScale() {
-			return scale;
-		}
-
-		public long getLength() {
-			return length;
-		}
-
-		public LobMultiplier getLobMultiplier() {
-			return lobMultiplier;
-		}
-
-		public void setPrecision(int precision) {
-			this.precision = precision;
-		}
-
-		public void setScale(int scale) {
-			this.scale = scale;
-		}
-
-		public void setLength(long length) {
-			this.length = length;
-		}
-
-		public void setLobMultiplier(LobMultiplier lobMultiplier) {
-			this.lobMultiplier = lobMultiplier;
-		}
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Size.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Size.java
new file mode 100644
index 0000000000..b8d7d521f9
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Size.java
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
+package org.hibernate.metamodel.relational;
+
+import java.io.Serializable;
+
+/**
+ * Models size restrictions/requirements on a column's datatype.
+ * <p/>
+ * IMPL NOTE: since we do not necessarily know the datatype up front, and therefore do not necessarily know
+ * whether length or precision/scale sizing is needed, we simply account for both here.  Additionally LOB
+ * definitions, by standard, are allowed a "multiplier" consisting of 'K' (Kb), 'M' (Mb) or 'G' (Gb).
+ *
+ * @author Steve Ebersole
+ */
+public class Size implements Serializable {
+	public static enum LobMultiplier {
+		NONE( 1 ),
+		K( NONE.factor * 1024 ),
+		M( K.factor * 1024 ),
+		G( M.factor * 1024 );
+
+		private long factor;
+
+		private LobMultiplier(long factor) {
+			this.factor = factor;
+		}
+
+		public long getFactor() {
+			return factor;
+		}
+	}
+
+	private int precision = - 1;
+	private int scale = -1;
+	private long length = -1;
+	private LobMultiplier lobMultiplier = LobMultiplier.NONE;
+
+	public Size() {
+	}
+
+	/**
+	 * Complete constructor.
+	 *
+	 * @param precision numeric precision
+	 * @param scale numeric scale
+	 * @param length type length
+	 * @param lobMultiplier LOB length multiplier
+	 */
+	public Size(int precision, int scale, long length, LobMultiplier lobMultiplier) {
+		this.precision = precision;
+		this.scale = scale;
+		this.length = length;
+		this.lobMultiplier = lobMultiplier;
+	}
+
+	public static Size precision(int precision) {
+		return new Size( precision, -1, -1, null );
+	}
+
+	public static Size precision(int precision, int scale) {
+		return new Size( precision, scale, -1, null );
+	}
+
+	public static Size length(long length) {
+		return new Size( -1, -1, length, null );
+	}
+
+	public static Size length(long length, LobMultiplier lobMultiplier) {
+		return new Size( -1, -1, length, lobMultiplier );
+	}
+
+	public int getPrecision() {
+		return precision;
+	}
+
+	public int getScale() {
+		return scale;
+	}
+
+	public long getLength() {
+		return length;
+	}
+
+	public LobMultiplier getLobMultiplier() {
+		return lobMultiplier;
+	}
+
+	public void setPrecision(int precision) {
+		this.precision = precision;
+	}
+
+	public void setScale(int scale) {
+		this.scale = scale;
+	}
+
+	public void setLength(long length) {
+		this.length = length;
+	}
+
+	public void setLobMultiplier(LobMultiplier lobMultiplier) {
+		this.lobMultiplier = lobMultiplier;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
index 900a85914c..393b94e6f6 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
@@ -1,156 +1,170 @@
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
+
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
+
 import org.dom4j.Node;
+
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.compare.EqualsHelper;
+import org.hibernate.metamodel.relational.Size;
 import org.hibernate.type.AbstractType;
 import org.hibernate.type.Type;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class DiscriminatorType extends AbstractType {
 	private final Type underlyingType;
 	private final Loadable persister;
 
 	public DiscriminatorType(Type underlyingType, Loadable persister) {
 		this.underlyingType = underlyingType;
 		this.persister = persister;
 	}
 
 	public Class getReturnedClass() {
 		return Class.class;
 	}
 
 	public String getName() {
 		return getClass().getName();
 	}
 
 	public boolean isMutable() {
 		return false;
 	}
 
 	public Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		return nullSafeGet( rs, names[0], session, owner );
 	}
 
 	public Object nullSafeGet(
 			ResultSet rs,
 			String name,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		final Object discriminatorValue = underlyingType.nullSafeGet( rs, name, session, owner );
 		final String entityName = persister.getSubclassForDiscriminatorValue( discriminatorValue );
 		if ( entityName == null ) {
 			throw new HibernateException( "Unable to resolve discriminator value [" + discriminatorValue + "] to entity name" );
 		}
 		if ( EntityMode.POJO.equals( session.getEntityMode() ) ) {
 			return session.getEntityPersister( entityName, null ).getMappedClass( session.getEntityMode() );
 		}
 		else {
 			return entityName;
 		}
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		nullSafeSet( st, value, index, session );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 		throw new UnsupportedOperationException(
 				"At the moment this type is not the one actually used to map the discriminator."
 		);
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		return value == null ? "[null]" : value.toString();
 	}
 
 	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return value;
 	}
 
 	public Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache)
 			throws HibernateException {
 		return original;
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return value == null
 				? ArrayHelper.FALSE
 				: ArrayHelper.TRUE;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return EqualsHelper.equals( old, current );
 	}
 
 
 	// simple delegation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return underlyingType.sqlTypes( mapping );
 	}
 
+	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		return underlyingType.dictatedSizes( mapping );
+	}
+
+	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		return underlyingType.defaultSizes( mapping );
+	}
+
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		return underlyingType.getColumnSpan( mapping );
 	}
 
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		// todo : ???
 		return null;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AbstractLobType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractLobType.java
index 5557a62dcf..f42e041a41 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractLobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractLobType.java
@@ -1,89 +1,100 @@
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
 package org.hibernate.type;
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.metamodel.relational.Size;
 
 /**
  * @author Emmanuel Bernard
  * @deprecated
  */
 @Deprecated
 public abstract class AbstractLobType extends AbstractType implements Serializable {
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return checkable[0] ? ! isEqual( old, current, session.getEntityMode() ) : false;
 	}
 
 	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		return new Size[] { LEGACY_DICTATED_SIZE };
+	}
+
+	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		return new Size[] { LEGACY_DEFAULT_SIZE };
+	}
+
+	@Override
 	public boolean isEqual(Object x, Object y, EntityMode entityMode) {
 		return isEqual( x, y, entityMode, null );
 	}
 
 	@Override
 	public int getHashCode(Object x, EntityMode entityMode) {
 		return getHashCode( x, entityMode, null );
 	}
 
 	public String getName() {
 		return this.getClass().getName();
 	}
 
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		return 1;
 	}
 
 	protected abstract Object get(ResultSet rs, String name) throws SQLException;
 
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return get( rs, names[0] );
 	}
 
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return get( rs, name );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session
 	) throws HibernateException, SQLException {
 		if ( settable[0] ) set( st, value, index, session );
 	}
 
 	protected abstract void set(PreparedStatement st, Object value, int index, SessionImplementor session)
 			throws SQLException;
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
 			throws HibernateException, SQLException {
 		set( st, value, index, session );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
index b8bbdb5caf..8099d5c5ef 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
@@ -1,371 +1,393 @@
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
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 import org.dom4j.Node;
 import org.hibernate.EntityMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.jdbc.LobCreator;
+import org.hibernate.metamodel.relational.Size;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.MutabilityPlan;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractStandardBasicType<T>
 		implements BasicType, StringRepresentableType<T>, XmlRepresentableType<T> {
 
+	private static final Size DEFAULT_SIZE = new Size( 19, 2, 255, Size.LobMultiplier.NONE ); // to match legacy behavior
+	private final Size dictatedSize = new Size();
+
 	private final SqlTypeDescriptor sqlTypeDescriptor;
 	private final JavaTypeDescriptor<T> javaTypeDescriptor;
 
 	public AbstractStandardBasicType(SqlTypeDescriptor sqlTypeDescriptor, JavaTypeDescriptor<T> javaTypeDescriptor) {
 		this.sqlTypeDescriptor = sqlTypeDescriptor;
 		this.javaTypeDescriptor = javaTypeDescriptor;
 	}
 
 	public T fromString(String string) {
 		return javaTypeDescriptor.fromString( string );
 	}
 
 	public String toString(T value) {
 		return javaTypeDescriptor.toString( value );
 	}
 
 	public T fromStringValue(String xml) throws HibernateException {
 		return fromString( xml );
 	}
 
 	public String toXMLString(T value, SessionFactoryImplementor factory) throws HibernateException {
 		return toString( value );
 	}
 
 	public T fromXMLString(String xml, Mapping factory) throws HibernateException {
 		return xml == null || xml.length() == 0 ? null : fromStringValue( xml );
 	}
 
 	protected MutabilityPlan<T> getMutabilityPlan() {
 		return javaTypeDescriptor.getMutabilityPlan();
 	}
 
 	protected T getReplacement(T original, T target) {
 		if ( !isMutable() ) {
 			return original;
 		}
 		else if ( isEqual( original, target ) ) {
 			return original;
 		}
 		else {
 			return deepCopy( original );
 		}
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return value == null ? ArrayHelper.FALSE : ArrayHelper.TRUE;
 	}
 
 	public String[] getRegistrationKeys() {
 		return registerUnderJavaType()
 				? new String[] { getName(), javaTypeDescriptor.getJavaTypeClass().getName() }
 				: new String[] { getName() };
 	}
 
 	protected boolean registerUnderJavaType() {
 		return false;
 	}
 
+	protected static Size getDefaultSize() {
+		return DEFAULT_SIZE;
+	}
+
+	protected Size getDictatedSize() {
+		return dictatedSize;
+	}
+
 
 	// final implementations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public final JavaTypeDescriptor<T> getJavaTypeDescriptor() {
 		return javaTypeDescriptor;
 	}
 
 	public final SqlTypeDescriptor getSqlTypeDescriptor() {
 		return sqlTypeDescriptor;
 	}
 
 	public final Class getReturnedClass() {
 		return javaTypeDescriptor.getJavaTypeClass();
 	}
 
+	public final int getColumnSpan(Mapping mapping) throws MappingException {
+		return sqlTypes( mapping ).length;
+	}
+
 	public final int[] sqlTypes(Mapping mapping) throws MappingException {
 		return new int[] { sqlTypeDescriptor.getSqlType() };
 	}
 
-	public final int getColumnSpan(Mapping mapping) throws MappingException {
-		return sqlTypes( mapping ).length;
+	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		return new Size[] { getDictatedSize() };
+	}
+
+	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		return new Size[] { getDefaultSize() };
 	}
 
 	public final boolean isAssociationType() {
 		return false;
 	}
 
 	public final boolean isCollectionType() {
 		return false;
 	}
 
 	public final boolean isComponentType() {
 		return false;
 	}
 
 	public final boolean isEntityType() {
 		return false;
 	}
 
 	public final boolean isAnyType() {
 		return false;
 	}
 
 	public final boolean isXMLElement() {
 		return false;
 	}
 
 	public final boolean isSame(Object x, Object y, EntityMode entityMode) {
 		return isSame( x, y );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected final boolean isSame(Object x, Object y) {
 		return isEqual( (T) x, (T) y );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isEqual(Object x, Object y, EntityMode entityMode) {
 		return isEqual( (T) x, (T) y );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isEqual(Object x, Object y, EntityMode entityMode, SessionFactoryImplementor factory) {
 		return isEqual( (T) x, (T) y );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isEqual(T one, T another) {
 		return javaTypeDescriptor.areEqual( one, another );
 	}
 
 	public final int getHashCode(Object x, EntityMode entityMode) {
 		return getHashCode( x );
 	}
 
 	public final int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor factory) {
 		return getHashCode( x );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected final int getHashCode(Object x) {
 		return javaTypeDescriptor.extractHashCode( (T) x );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final int compare(Object x, Object y, EntityMode entityMode) {
 		return javaTypeDescriptor.getComparator().compare( (T) x, (T) y );
 	}
 
 	public final boolean isDirty(Object old, Object current, SessionImplementor session) {
 		return isDirty( old, current );
 	}
 
 	public final boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) {
 		return checkable[0] && isDirty( old, current );
 	}
 
 	protected final boolean isDirty(Object old, Object current) {
 		return !isSame( old, current );
 	}
 
 	public final boolean isModified(
 			Object oldHydratedState,
 			Object currentState,
 			boolean[] checkable,
 			SessionImplementor session) {
 		return isDirty( oldHydratedState, currentState );
 	}
 
 	public final Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws SQLException {
 		return nullSafeGet( rs, names[0], session );
 	}
 
 	public final Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 			throws SQLException {
 		return nullSafeGet( rs, name, session );
 	}
 
 	public final T nullSafeGet(ResultSet rs, String name, final SessionImplementor session) throws SQLException {
 		// todo : have SessionImplementor extend WrapperOptions
 		final WrapperOptions options = new WrapperOptions() {
 			public boolean useStreamForLobBinding() {
 				return Environment.useStreamsForBinary();
 			}
 
 			public LobCreator getLobCreator() {
 				return Hibernate.getLobCreator( session );
 			}
 
 			public SqlTypeDescriptor resolveSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 				return session.getFactory().getTypeResolver().resolveSqlTypeDescriptor( sqlTypeDescriptor );
 			}
 		};
 
 		return nullSafeGet( rs, name, options );
 	}
 
 	protected final T nullSafeGet(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 		return resolveSqlTypeDescriptor( options ).getExtractor( javaTypeDescriptor ).extract( rs, name, options );
 	}
 
 	public Object get(ResultSet rs, String name, SessionImplementor session) throws HibernateException, SQLException {
 		return nullSafeGet( rs, name, session );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			final SessionImplementor session) throws SQLException {
 		// todo : have SessionImplementor extend WrapperOptions
 		final WrapperOptions options = new WrapperOptions() {
 			public boolean useStreamForLobBinding() {
 				return Environment.useStreamsForBinary();
 			}
 
 			public LobCreator getLobCreator() {
 				return Hibernate.getLobCreator( session );
 			}
 
 			public SqlTypeDescriptor resolveSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 				return session.getFactory().getTypeResolver().resolveSqlTypeDescriptor( sqlTypeDescriptor );
 			}
 		};
 
 		nullSafeSet( st, value, index, options );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected final void nullSafeSet(PreparedStatement st, Object value, int index, WrapperOptions options) throws SQLException {
 		resolveSqlTypeDescriptor( options ).getBinder( javaTypeDescriptor ).bind( st, ( T ) value, index, options );
 	}
 
 	private SqlTypeDescriptor resolveSqlTypeDescriptor(WrapperOptions options) {
 		return options.resolveSqlTypeDescriptor( sqlTypeDescriptor );
 	}
 
 	public void set(PreparedStatement st, T value, int index, SessionImplementor session) throws HibernateException, SQLException {
 		nullSafeSet( st, value, index, session );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final String toLoggableString(Object value, SessionFactoryImplementor factory) {
 		return javaTypeDescriptor.extractLoggableRepresentation( (T) value );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) {
 		node.setText( toString( (T) value ) );
 	}
 
 	public final Object fromXMLNode(Node xml, Mapping factory) {
 		return fromString( xml.getText() );
 	}
 
 	public final boolean isMutable() {
 		return getMutabilityPlan().isMutable();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory) {
 		return deepCopy( (T) value );
 	}
 
 	protected final T deepCopy(T value) {
 		return getMutabilityPlan().deepCopy( value );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return getMutabilityPlan().disassemble( (T) value );
 	}
 
 	public final Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
 		return getMutabilityPlan().assemble( cached );
 	}
 
 	public final void beforeAssemble(Serializable cached, SessionImplementor session) {
 	}
 
 	public final Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return nullSafeGet(rs, names, session, owner);
 	}
 
 	public final Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return value;
 	}
 
 	public final Object semiResolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return value;
 	}
 
 	public final Type getSemiResolvedType(SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache) {
 		return getReplacement( (T) original, (T) target );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache,
 			ForeignKeyDirection foreignKeyDirection) {
 		return ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT == foreignKeyDirection
 				? getReplacement( (T) original, (T) target )
 				: target;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
index 94f24bac2b..4ddabe1148 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
@@ -1,185 +1,188 @@
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
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 import org.dom4j.Element;
 import org.dom4j.Node;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.compare.EqualsHelper;
+import org.hibernate.metamodel.relational.Size;
 
 /**
  * Abstract superclass of the built in Type hierarchy.
  * 
  * @author Gavin King
  */
 public abstract class AbstractType implements Type {
+	protected static final Size LEGACY_DICTATED_SIZE = new Size();
+	protected static final Size LEGACY_DEFAULT_SIZE = new Size( 19, 2, 255, Size.LobMultiplier.NONE ); // to match legacy behavior
 
 	public boolean isAssociationType() {
 		return false;
 	}
 
 	public boolean isCollectionType() {
 		return false;
 	}
 
 	public boolean isComponentType() {
 		return false;
 	}
 
 	public boolean isEntityType() {
 		return false;
 	}
 	
 	public boolean isXMLElement() {
 		return false;
 	}
 
 	public int compare(Object x, Object y, EntityMode entityMode) {
 		return ( (Comparable) x ).compareTo(y);
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 
 		if (value==null) {
 			return null;
 		}
 		else {
 			return (Serializable) deepCopy( value, session.getEntityMode(), session.getFactory() );
 		}
 	}
 
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner) 
 	throws HibernateException {
 		if ( cached==null ) {
 			return null;
 		}
 		else {
 			return deepCopy( cached, session.getEntityMode(), session.getFactory() );
 		}
 	}
 
 	public boolean isDirty(Object old, Object current, SessionImplementor session) 
 	throws HibernateException {
 		return !isSame( old, current, session.getEntityMode() );
 	}
 
 	public Object hydrate(
 		ResultSet rs,
 		String[] names,
 		SessionImplementor session,
 		Object owner)
 	throws HibernateException, SQLException {
 		// TODO: this is very suboptimal for some subclasses (namely components),
 		// since it does not take advantage of two-phase-load
 		return nullSafeGet(rs, names, session, owner);
 	}
 
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 		return value;
 	}
 
 	public Object semiResolve(Object value, SessionImplementor session, Object owner) 
 	throws HibernateException {
 		return value;
 	}
 	
 	public boolean isAnyType() {
 		return false;
 	}
 
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
 	throws HibernateException {
 		return isDirty(old, current, session);
 	}
 	
 	public boolean isSame(Object x, Object y, EntityMode entityMode) throws HibernateException {
 		return isEqual(x, y, entityMode);
 	}
 
 	public boolean isEqual(Object x, Object y, EntityMode entityMode) {
 		return EqualsHelper.equals(x, y);
 	}
 	
 	public int getHashCode(Object x, EntityMode entityMode) {
 		return x.hashCode();
 	}
 
 	public boolean isEqual(Object x, Object y, EntityMode entityMode, SessionFactoryImplementor factory) {
 		return isEqual(x, y, entityMode);
 	}
 	
 	public int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor factory) {
 		return getHashCode(x, entityMode);
 	}
 	
 	protected static void replaceNode(Node container, Element value) {
 		if ( container!=value ) { //not really necessary, I guess...
 			Element parent = container.getParent();
 			container.detach();
 			value.setName( container.getName() );
 			value.detach();
 			parent.add(value);
 		}
 	}
 	
 	public Type getSemiResolvedType(SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	public Object replace(
 			Object original, 
 			Object target, 
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache, 
 			ForeignKeyDirection foreignKeyDirection) 
 	throws HibernateException {
 		boolean include;
 		if ( isAssociationType() ) {
 			AssociationType atype = (AssociationType) this;
 			include = atype.getForeignKeyDirection()==foreignKeyDirection;
 		}
 		else {
 			include = ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT==foreignKeyDirection;
 		}
 		return include ? replace(original, target, session, owner, copyCache) : target;
 	}
 
 	public void beforeAssemble(Serializable cached, SessionImplementor session) {}
 
 	/*public Object copy(Object original, Object target, SessionImplementor session, Object owner, Map copyCache)
 	throws HibernateException {
 		if (original==null) return null;
 		return assemble( disassemble(original, session), session, owner );
 	}*/
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
index f67950dcd8..c29e7942e0 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
@@ -1,385 +1,402 @@
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
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.ForeignKeys;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.metamodel.relational.Size;
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
 
 	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
 	throws HibernateException {
 		return value;
 	}
 	
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	public boolean isSame(Object x, Object y, EntityMode entityMode) throws HibernateException {
 		return x==y;
 	}
 
 	public int compare(Object x, Object y, EntityMode entityMode) {
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
 
+	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		return ArrayHelper.join(
+				metaType.dictatedSizes( mapping ),
+				identifierType.dictatedSizes( mapping )
+		);
+	}
+
+	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		return ArrayHelper.join(
+				metaType.defaultSizes( mapping ),
+				identifierType.defaultSizes( mapping )
+		);
+	}
+
 	public void setToXMLNode(Node xml, Object value, SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types cannot be stringified");
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) 
 	throws HibernateException {
 		//TODO: terrible implementation!
 		return value==null ?
 				"null" :
 				Hibernate.entity( HibernateProxyHelper.getClassWithoutInitializingProxy(value) )
 						.toLoggableString(value, factory);
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
 		return CascadeStyle.NONE;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
index 3357c7ceea..f0ec268a4a 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
@@ -1,705 +1,716 @@
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
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.dom4j.Element;
 import org.dom4j.Node;
 import org.hibernate.EntityMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.CollectionKey;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * A type that handles Hibernate <tt>PersistentCollection</tt>s (including arrays).
  * 
  * @author Gavin King
  */
 public abstract class CollectionType extends AbstractType implements AssociationType {
 
 	private static final Object NOT_NULL_COLLECTION = new MarkerObject( "NOT NULL COLLECTION" );
 	public static final Object UNFETCHED_COLLECTION = new MarkerObject( "UNFETCHED COLLECTION" );
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String role;
 	private final String foreignKeyPropertyName;
 	private final boolean isEmbeddedInXML;
 
 	public CollectionType(TypeFactory.TypeScope typeScope, String role, String foreignKeyPropertyName, boolean isEmbeddedInXML) {
 		this.typeScope = typeScope;
 		this.role = role;
 		this.foreignKeyPropertyName = foreignKeyPropertyName;
 		this.isEmbeddedInXML = isEmbeddedInXML;
 	}
 
 	public boolean isEmbeddedInXML() {
 		return isEmbeddedInXML;
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public Object indexOf(Object collection, Object element) {
 		throw new UnsupportedOperationException( "generic collections don't have indexes" );
 	}
 
 	public boolean contains(Object collection, Object childObject, SessionImplementor session) {
 		// we do not have to worry about queued additions to uninitialized
 		// collections, since they can only occur for inverse collections!
 		Iterator elems = getElementsIterator( collection, session );
 		while ( elems.hasNext() ) {
 			Object element = elems.next();
 			// worrying about proxies is perhaps a little bit of overkill here...
 			if ( element instanceof HibernateProxy ) {
 				LazyInitializer li = ( (HibernateProxy) element ).getHibernateLazyInitializer();
 				if ( !li.isUninitialized() ) element = li.getImplementation();
 			}
 			if ( element == childObject ) return true;
 		}
 		return false;
 	}
 
 	public boolean isCollectionType() {
 		return true;
 	}
 
 	public final boolean isEqual(Object x, Object y, EntityMode entityMode) {
 		return x == y
 			|| ( x instanceof PersistentCollection && ( (PersistentCollection) x ).isWrapper( y ) )
 			|| ( y instanceof PersistentCollection && ( (PersistentCollection) y ).isWrapper( x ) );
 	}
 
 	public int compare(Object x, Object y, EntityMode entityMode) {
 		return 0; // collections cannot be compared
 	}
 
 	public int getHashCode(Object x, EntityMode entityMode) {
 		throw new UnsupportedOperationException( "cannot doAfterTransactionCompletion lookups on collections" );
 	}
 
 	/**
 	 * Instantiate an uninitialized collection wrapper or holder. Callers MUST add the holder to the
 	 * persistence context!
 	 *
 	 * @param session The session from which the request is originating.
 	 * @param persister The underlying collection persister (metadata)
 	 * @param key The owner key.
 	 * @return The instantiated collection.
 	 */
 	public abstract PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key);
 
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner) throws SQLException {
 		return nullSafeGet( rs, new String[] { name }, session, owner );
 	}
 
 	public Object nullSafeGet(ResultSet rs, String[] name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( null, session, owner );
 	}
 
 	public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		//NOOP
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 	}
 
 	public int[] sqlTypes(Mapping session) throws MappingException {
 		return ArrayHelper.EMPTY_INT_ARRAY;
 	}
 
+	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		return new Size[] { LEGACY_DICTATED_SIZE };
+	}
+
+	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		return new Size[] { LEGACY_DEFAULT_SIZE };
+	}
+
 	public int getColumnSpan(Mapping session) throws MappingException {
 		return 0;
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		else if ( !Hibernate.isInitialized( value ) ) {
 			return "<uninitialized>";
 		}
 		else {
 			return renderLoggableString( value, factory );
 		}
 	}
 
 	protected String renderLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( Element.class.isInstance( value ) ) {
 			// for DOM4J "collections" only
 			// TODO: it would be better if this was done at the higher level by Printer
 			return ( ( Element ) value ).asXML();
 		}
 		else {
 			List list = new ArrayList();
 			Type elemType = getElementType( factory );
 			Iterator iter = getElementsIterator( value );
 			while ( iter.hasNext() ) {
 				list.add( elemType.toLoggableString( iter.next(), factory ) );
 			}
 			return list.toString();
 		}
 	}
 
 	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return value;
 	}
 
 	public String getName() {
 		return getReturnedClass().getName() + '(' + getRole() + ')';
 	}
 
 	/**
 	 * Get an iterator over the element set of the collection, which may not yet be wrapped
 	 *
 	 * @param collection The collection to be iterated
 	 * @param session The session from which the request is originating.
 	 * @return The iterator.
 	 */
 	public Iterator getElementsIterator(Object collection, SessionImplementor session) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			final SessionFactoryImplementor factory = session.getFactory();
 			final CollectionPersister persister = factory.getCollectionPersister( getRole() );
 			final Type elementType = persister.getElementType();
 			
 			List elements = ( (Element) collection ).elements( persister.getElementNodeName() );
 			ArrayList results = new ArrayList();
 			for ( int i=0; i<elements.size(); i++ ) {
 				Element value = (Element) elements.get(i);
 				results.add( elementType.fromXMLNode( value, factory ) );
 			}
 			return results.iterator();
 		}
 		else {
 			return getElementsIterator(collection);
 		}
 	}
 
 	/**
 	 * Get an iterator over the element set of the collection in POJO mode
 	 *
 	 * @param collection The collection to be iterated
 	 * @return The iterator.
 	 */
 	protected Iterator getElementsIterator(Object collection) {
 		return ( (Collection) collection ).iterator();
 	}
 
 	public boolean isMutable() {
 		return false;
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//remember the uk value
 		
 		//This solution would allow us to eliminate the owner arg to disassemble(), but
 		//what if the collection was null, and then later had elements added? seems unsafe
 		//session.getPersistenceContext().getCollectionEntry( (PersistentCollection) value ).getKey();
 		
 		final Serializable key = getKeyOfOwner(owner, session);
 		if (key==null) {
 			return null;
 		}
 		else {
 			return getPersister(session)
 					.getKeyType()
 					.disassemble( key, session, owner );
 		}
 	}
 
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//we must use the "remembered" uk value, since it is 
 		//not available from the EntityEntry during assembly
 		if (cached==null) {
 			return null;
 		}
 		else {
 			final Serializable key = (Serializable) getPersister(session)
 					.getKeyType()
 					.assemble( cached, session, owner);
 			return resolveKey( key, session, owner );
 		}
 	}
 
 	/**
 	 * Is the owning entity versioned?
 	 *
 	 * @param session The session from which the request is originating.
 	 * @return True if the collection owner is versioned; false otherwise.
 	 * @throws org.hibernate.MappingException Indicates our persister could not be located.
 	 */
 	private boolean isOwnerVersioned(SessionImplementor session) throws MappingException {
 		return getPersister( session ).getOwnerEntityPersister().isVersioned();
 	}
 
 	/**
 	 * Get our underlying collection persister (using the session to access the
 	 * factory).
 	 *
 	 * @param session The session from which the request is originating.
 	 * @return The underlying collection persister
 	 */
 	private CollectionPersister getPersister(SessionImplementor session) {
 		return session.getFactory().getCollectionPersister( role );
 	}
 
 	public boolean isDirty(Object old, Object current, SessionImplementor session)
 			throws HibernateException {
 
 		// collections don't dirty an unversioned parent entity
 
 		// TODO: I don't really like this implementation; it would be better if
 		// this was handled by searchForDirtyCollections()
 		return isOwnerVersioned( session ) && super.isDirty( old, current, session );
 		// return false;
 
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return isDirty(old, current, session);
 	}
 
 	/**
 	 * Wrap the naked collection instance in a wrapper, or instantiate a
 	 * holder. Callers <b>MUST</b> add the holder to the persistence context!
 	 *
 	 * @param session The session from which the request is originating.
 	 * @param collection The bare collection to be wrapped.
 	 * @return The wrapped collection.
 	 */
 	public abstract PersistentCollection wrap(SessionImplementor session, Object collection);
 
 	/**
 	 * Note: return true because this type is castable to <tt>AssociationType</tt>. Not because
 	 * all collections are associations.
 	 */
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FOREIGN_KEY_TO_PARENT;
 	}
 
 	/**
 	 * Get the key value from the owning entity instance, usually the identifier, but might be some
 	 * other unique key, in the case of property-ref
 	 *
 	 * @param owner The collection owner
 	 * @param session The session from which the request is originating.
 	 * @return The collection owner's key
 	 */
 	public Serializable getKeyOfOwner(Object owner, SessionImplementor session) {
 		
 		EntityEntry entityEntry = session.getPersistenceContext().getEntry( owner );
 		if ( entityEntry == null ) return null; // This just handles a particular case of component
 									  // projection, perhaps get rid of it and throw an exception
 		
 		if ( foreignKeyPropertyName == null ) {
 			return entityEntry.getId();
 		}
 		else {
 			// TODO: at the point where we are resolving collection references, we don't
 			// know if the uk value has been resolved (depends if it was earlier or
 			// later in the mapping document) - now, we could try and use e.getStatus()
 			// to decide to semiResolve(), trouble is that initializeEntity() reuses
 			// the same array for resolved and hydrated values
 			Object id;
 			if ( entityEntry.getLoadedState() != null ) {
 				id = entityEntry.getLoadedValue( foreignKeyPropertyName );
 			}
 			else {
 				id = entityEntry.getPersister().getPropertyValue( owner, foreignKeyPropertyName, session.getEntityMode() );
 			}
 
 			// NOTE VERY HACKISH WORKAROUND!!
 			// TODO: Fix this so it will work for non-POJO entity mode
 			Type keyType = getPersister( session ).getKeyType();
 			if ( !keyType.getReturnedClass().isInstance( id ) ) {
 				id = (Serializable) keyType.semiResolve(
 						entityEntry.getLoadedValue( foreignKeyPropertyName ),
 						session,
 						owner 
 					);
 			}
 
 			return (Serializable) id;
 		}
 	}
 
 	/**
 	 * Get the id value from the owning entity key, usually the same as the key, but might be some
 	 * other property, in the case of property-ref
 	 *
 	 * @param key The collection owner key
 	 * @param session The session from which the request is originating.
 	 * @return The collection owner's id, if it can be obtained from the key;
 	 * otherwise, null is returned
 	 */
 	public Serializable getIdOfOwnerOrNull(Serializable key, SessionImplementor session) {
 		Serializable ownerId = null;
 		if ( foreignKeyPropertyName == null ) {
 			ownerId = key;
 		}
 		else {
 			Type keyType = getPersister( session ).getKeyType();
 			EntityPersister ownerPersister = getPersister( session ).getOwnerEntityPersister();
 			// TODO: Fix this so it will work for non-POJO entity mode
 			Class ownerMappedClass = ownerPersister.getMappedClass( session.getEntityMode() );
 			if ( ownerMappedClass.isAssignableFrom( keyType.getReturnedClass() ) &&
 					keyType.getReturnedClass().isInstance( key ) ) {
 				// the key is the owning entity itself, so get the ID from the key
 				ownerId = ownerPersister.getIdentifier( key, session );
 			}
 			else {
 				// TODO: check if key contains the owner ID
 			}
 		}
 		return ownerId;
 	}
 
 	public Object hydrate(ResultSet rs, String[] name, SessionImplementor session, Object owner) {
 		// can't just return null here, since that would
 		// cause an owning component to become null
 		return NOT_NULL_COLLECTION;
 	}
 
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		
 		return resolveKey( getKeyOfOwner( owner, session ), session, owner );
 	}
 	
 	private Object resolveKey(Serializable key, SessionImplementor session, Object owner) {
 		// if (key==null) throw new AssertionFailure("owner identifier unknown when re-assembling
 		// collection reference");
 		return key == null ? null : // TODO: can this case really occur??
 			getCollection( key, session, owner );
 	}
 
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		throw new UnsupportedOperationException(
 			"collection mappings may not form part of a property-ref" );
 	}
 
 	public boolean isArrayType() {
 		return false;
 	}
 
 	public boolean useLHSPrimaryKey() {
 		return foreignKeyPropertyName == null;
 	}
 
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory)
 			throws MappingException {
 		return (Joinable) factory.getCollectionPersister( role );
 	}
 
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
 		return false;
 	}
 
 	public String getAssociatedEntityName(SessionFactoryImplementor factory)
 			throws MappingException {
 		try {
 			
 			QueryableCollection collectionPersister = (QueryableCollection) factory
 					.getCollectionPersister( role );
 			
 			if ( !collectionPersister.getElementType().isEntityType() ) {
 				throw new MappingException( 
 						"collection was not an association: " + 
 						collectionPersister.getRole() 
 					);
 			}
 			
 			return collectionPersister.getElementPersister().getEntityName();
 			
 		}
 		catch (ClassCastException cce) {
 			throw new MappingException( "collection role is not queryable " + role );
 		}
 	}
 
 	/**
 	 * Replace the elements of a collection with the elements of another collection.
 	 *
 	 * @param original The 'source' of the replacement elements (where we copy from)
 	 * @param target The target of the replacement elements (where we copy to)
 	 * @param owner The owner of the collection being merged
 	 * @param copyCache The map of elements already replaced.
 	 * @param session The session from which the merge event originated.
 	 * @return The merged collection.
 	 */
 	public Object replaceElements(
 			Object original,
 			Object target,
 			Object owner,
 			Map copyCache,
 			SessionImplementor session) {
 		// TODO: does not work for EntityMode.DOM4J yet!
 		java.util.Collection result = ( java.util.Collection ) target;
 		result.clear();
 
 		// copy elements into newly empty target collection
 		Type elemType = getElementType( session.getFactory() );
 		Iterator iter = ( (java.util.Collection) original ).iterator();
 		while ( iter.hasNext() ) {
 			result.add( elemType.replace( iter.next(), null, session, owner, copyCache ) );
 		}
 
 		// if the original is a PersistentCollection, and that original
 		// was not flagged as dirty, then reset the target's dirty flag
 		// here after the copy operation.
 		// </p>
 		// One thing to be careful of here is a "bare" original collection
 		// in which case we should never ever ever reset the dirty flag
 		// on the target because we simply do not know...
 		if ( original instanceof PersistentCollection ) {
 			if ( result instanceof PersistentCollection ) {
 				if ( ! ( ( PersistentCollection ) original ).isDirty() ) {
 					( ( PersistentCollection ) result ).clearDirty();
 				}
 			}
 		}
 
 		return result;
 	}
 
 	/**
 	 * Instantiate a new "underlying" collection exhibiting the same capacity
 	 * charactersitcs and the passed "original".
 	 *
 	 * @param original The original collection.
 	 * @return The newly instantiated collection.
 	 */
 	protected Object instantiateResult(Object original) {
 		// by default just use an unanticipated capacity since we don't
 		// know how to extract the capacity to use from original here...
 		return instantiate( -1 );
 	}
 
 	/**
 	 * Instantiate an empty instance of the "underlying" collection (not a wrapper),
 	 * but with the given anticipated size (i.e. accounting for initial capacity
 	 * and perhaps load factor).
 	 *
 	 * @param anticipatedSize The anticipated size of the instaniated collection
 	 * after we are done populating it.
 	 * @return A newly instantiated collection to be wrapped.
 	 */
 	public abstract Object instantiate(int anticipatedSize);
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object replace(
 			final Object original,
 			final Object target,
 			final SessionImplementor session,
 			final Object owner,
 			final Map copyCache) throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		if ( !Hibernate.isInitialized( original ) ) {
 			return target;
 		}
 
 		// for a null target, or a target which is the same as the original, we
 		// need to put the merged elements in a new collection
 		Object result = target == null || target == original ? instantiateResult( original ) : target;
 		
 		//for arrays, replaceElements() may return a different reference, since
 		//the array length might not match
 		result = replaceElements( original, result, owner, copyCache, session );
 
 		if ( original == target ) {
 			// get the elements back into the target making sure to handle dirty flag
 			boolean wasClean = PersistentCollection.class.isInstance( target ) && !( ( PersistentCollection ) target ).isDirty();
 			//TODO: this is a little inefficient, don't need to do a whole
 			//      deep replaceElements() call
 			replaceElements( result, target, owner, copyCache, session );
 			if ( wasClean ) {
 				( ( PersistentCollection ) target ).clearDirty();
 			}
 			result = target;
 		}
 
 		return result;
 	}
 
 	/**
 	 * Get the Hibernate type of the collection elements
 	 *
 	 * @param factory The session factory.
 	 * @return The type of the collection elements
 	 * @throws MappingException Indicates the underlying persister could not be located.
 	 */
 	public final Type getElementType(SessionFactoryImplementor factory) throws MappingException {
 		return factory.getCollectionPersister( getRole() ).getElementType();
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
 			throws MappingException {
 		return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
 	}
 
 	/**
 	 * instantiate a collection wrapper (called when loading an object)
 	 *
 	 * @param key The collection owner key
 	 * @param session The session from which the request is originating.
 	 * @param owner The collection owner
 	 * @return The collection
 	 */
 	public Object getCollection(Serializable key, SessionImplementor session, Object owner) {
 
 		CollectionPersister persister = getPersister( session );
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityMode entityMode = session.getEntityMode();
 
 		if (entityMode==EntityMode.DOM4J && !isEmbeddedInXML) {
 			return UNFETCHED_COLLECTION;
 		}
 		
 		// check if collection is currently being loaded
 		PersistentCollection collection = persistenceContext.getLoadContexts().locateLoadingCollection( persister, key );
 		
 		if ( collection == null ) {
 			
 			// check if it is already completely loaded, but unowned
 			collection = persistenceContext.useUnownedCollection( new CollectionKey(persister, key, entityMode) );
 			
 			if ( collection == null ) {
 				// create a new collection wrapper, to be initialized later
 				collection = instantiate( session, persister, key );
 				collection.setOwner(owner);
 	
 				persistenceContext.addUninitializedCollection( persister, collection, key );
 	
 				// some collections are not lazy:
 				if ( initializeImmediately( entityMode ) ) {
 					session.initializeCollection( collection, false );
 				}
 				else if ( !persister.isLazy() ) {
 					persistenceContext.addNonLazyCollection( collection );
 				}
 	
 				if ( hasHolder( entityMode ) ) {
 					session.getPersistenceContext().addCollectionHolder( collection );
 				}
 				
 			}
 			
 		}
 		
 		collection.setOwner(owner);
 
 		return collection.getValue();
 	}
 
 	public boolean hasHolder(EntityMode entityMode) {
 		return entityMode == EntityMode.DOM4J;
 	}
 
 	protected boolean initializeImmediately(EntityMode entityMode) {
 		return entityMode == EntityMode.DOM4J;
 	}
 
 	public String getLHSPropertyName() {
 		return foreignKeyPropertyName;
 	}
 
 	public boolean isXMLElement() {
 		return true;
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return xml;
 	}
 
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) 
 	throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			node.detach();
 		}
 		else {
 			replaceNode( node, (Element) value );
 		}
 	}
 	
 	/**
 	 * We always need to dirty check the collection because we sometimes 
 	 * need to incremement version number of owner and also because of 
 	 * how assemble/disassemble is implemented for uks
 	 */
 	public boolean isAlwaysDirtyChecked() {
 		return true; 
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
index 0b53d28492..26f5a60026 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
@@ -1,695 +1,725 @@
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
 import java.util.HashMap;
 import java.util.Map;
+
 import org.dom4j.Element;
 import org.dom4j.Node;
+
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.metamodel.relational.Size;
 import org.hibernate.tuple.EntityModeToTuplizerMapping;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.tuple.component.ComponentTuplizer;
 
 /**
  * Handles "component" mappings
  *
  * @author Gavin King
  */
 public class ComponentType extends AbstractType implements CompositeType {
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyNullability;
 	protected final int propertySpan;
 	private final CascadeStyle[] cascade;
 	private final FetchMode[] joinedFetch;
 	private final boolean isKey;
 
 	protected final EntityModeToTuplizerMapping tuplizerMapping;
 
 	public ComponentType(TypeFactory.TypeScope typeScope, ComponentMetamodel metamodel) {
 		this.typeScope = typeScope;
 		// for now, just "re-flatten" the metamodel since this is temporary stuff anyway (HHH-1907)
 		this.isKey = metamodel.isKey();
 		this.propertySpan = metamodel.getPropertySpan();
 		this.propertyNames = new String[ propertySpan ];
 		this.propertyTypes = new Type[ propertySpan ];
 		this.propertyNullability = new boolean[ propertySpan ];
 		this.cascade = new CascadeStyle[ propertySpan ];
 		this.joinedFetch = new FetchMode[ propertySpan ];
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			StandardProperty prop = metamodel.getProperty( i );
 			this.propertyNames[i] = prop.getName();
 			this.propertyTypes[i] = prop.getType();
 			this.propertyNullability[i] = prop.isNullable();
 			this.cascade[i] = prop.getCascadeStyle();
 			this.joinedFetch[i] = prop.getFetchMode();
 		}
 
 		this.tuplizerMapping = metamodel.getTuplizerMapping();
 	}
 
 	public boolean isKey() {
 		return isKey;
 	}
 
 	public EntityModeToTuplizerMapping getTuplizerMapping() {
 		return tuplizerMapping;
 	}
 
+	public int getColumnSpan(Mapping mapping) throws MappingException {
+		int span = 0;
+		for ( int i = 0; i < propertySpan; i++ ) {
+			span += propertyTypes[i].getColumnSpan( mapping );
+		}
+		return span;
+	}
+
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		int[] sqlTypes = new int[getColumnSpan( mapping )];
 		int n = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int[] subtypes = propertyTypes[i].sqlTypes( mapping );
 			for ( int j = 0; j < subtypes.length; j++ ) {
 				sqlTypes[n++] = subtypes[j];
 			}
 		}
 		return sqlTypes;
 	}
 
-	public int getColumnSpan(Mapping mapping) throws MappingException {
-		int span = 0;
-		for ( int i = 0; i < propertySpan; i++ ) {
-			span += propertyTypes[i].getColumnSpan( mapping );
+	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		//Not called at runtime so doesn't matter if its slow :)
+		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
+		int soFar = 0;
+		for ( Type propertyType : propertyTypes ) {
+			final Size[] propertySizes = propertyType.dictatedSizes( mapping );
+			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
+			soFar += propertySizes.length;
 		}
-		return span;
+		return sizes;
 	}
 
 	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		//Not called at runtime so doesn't matter if its slow :)
+		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
+		int soFar = 0;
+		for ( Type propertyType : propertyTypes ) {
+			final Size[] propertySizes = propertyType.defaultSizes( mapping );
+			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
+			soFar += propertySizes.length;
+		}
+		return sizes;
+	}
+
+
+	@Override
     public final boolean isComponentType() {
 		return true;
 	}
 
 	public Class getReturnedClass() {
 		return tuplizerMapping.getTuplizer( EntityMode.POJO ).getMappedClass(); //TODO
 	}
 
 	@Override
     public boolean isSame(Object x, Object y, EntityMode entityMode) throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isSame( xvalues[i], yvalues[i], entityMode ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
     public boolean isEqual(Object x, Object y, EntityMode entityMode)
 			throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i], entityMode ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
     public boolean isEqual(Object x, Object y, EntityMode entityMode, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i], entityMode, factory ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
     public int compare(Object x, Object y, EntityMode entityMode) {
 		if ( x == y ) {
 			return 0;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int propertyCompare = propertyTypes[i].compare( xvalues[i], yvalues[i], entityMode );
 			if ( propertyCompare != 0 ) {
 				return propertyCompare;
 			}
 		}
 		return 0;
 	}
 
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	@Override
     public int getHashCode(Object x, EntityMode entityMode) {
 		int result = 17;
 		Object[] values = getPropertyValues( x, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = values[i];
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y, entityMode );
 			}
 		}
 		return result;
 	}
 
 	@Override
     public int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor factory) {
 		int result = 17;
 		Object[] values = getPropertyValues( x, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = values[i];
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y, entityMode, factory );
 			}
 		}
 		return result;
 	}
 
 	@Override
     public boolean isDirty(Object x, Object y, SessionImplementor session)
 			throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
 		EntityMode entityMode = session.getEntityMode();
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < xvalues.length; i++ ) {
 			if ( propertyTypes[i].isDirty( xvalues[i], yvalues[i], session ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public boolean isDirty(Object x, Object y, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
 		EntityMode entityMode = session.getEntityMode();
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		int loc = 0;
 		for ( int i = 0; i < xvalues.length; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			if ( len <= 1 ) {
 				final boolean dirty = ( len == 0 || checkable[loc] ) &&
 				                      propertyTypes[i].isDirty( xvalues[i], yvalues[i], session );
 				if ( dirty ) {
 					return true;
 				}
 			}
 			else {
 				boolean[] subcheckable = new boolean[len];
 				System.arraycopy( checkable, loc, subcheckable, 0, len );
 				final boolean dirty = propertyTypes[i].isDirty( xvalues[i], yvalues[i], subcheckable, session );
 				if ( dirty ) {
 					return true;
 				}
 			}
 			loc += len;
 		}
 		return false;
 	}
 
 	@Override
     public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 
 		if ( current == null ) {
 			return old != null;
 		}
 		if ( old == null ) {
 			return current != null;
 		}
 		Object[] currentValues = getPropertyValues( current, session );
 		Object[] oldValues = ( Object[] ) old;
 		int loc = 0;
 		for ( int i = 0; i < currentValues.length; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			boolean[] subcheckable = new boolean[len];
 			System.arraycopy( checkable, loc, subcheckable, 0, len );
 			if ( propertyTypes[i].isModified( oldValues[i], currentValues[i], subcheckable, session ) ) {
 				return true;
 			}
 			loc += len;
 		}
 		return false;
 
 	}
 
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( hydrate( rs, names, session, owner ), session, owner );
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int begin, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, session.getEntityMode() );
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			propertyTypes[i].nullSafeSet( st, subvalues[i], begin, session );
 			begin += propertyTypes[i].getColumnSpan( session.getFactory() );
 		}
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int begin,
 			boolean[] settable,
 			SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, session.getEntityMode() );
 
 		int loc = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			if ( len == 0 ) {
 				//noop
 			}
 			else if ( len == 1 ) {
 				if ( settable[loc] ) {
 					propertyTypes[i].nullSafeSet( st, subvalues[i], begin, session );
 					begin++;
 				}
 			}
 			else {
 				boolean[] subsettable = new boolean[len];
 				System.arraycopy( settable, loc, subsettable, 0, len );
 				propertyTypes[i].nullSafeSet( st, subvalues[i], begin, subsettable, session );
 				begin += ArrayHelper.countTrue( subsettable );
 			}
 			loc += len;
 		}
 	}
 
 	private Object[] nullSafeGetValues(Object value, EntityMode entityMode) throws HibernateException {
 		if ( value == null ) {
 			return new Object[propertySpan];
 		}
 		else {
 			return getPropertyValues( value, entityMode );
 		}
 	}
 
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	public Object getPropertyValue(Object component, int i, SessionImplementor session)
 			throws HibernateException {
 		return getPropertyValue( component, i, session.getEntityMode() );
 	}
 
 	public Object getPropertyValue(Object component, int i, EntityMode entityMode)
 			throws HibernateException {
 		return tuplizerMapping.getTuplizer( entityMode ).getPropertyValue( component, i );
 	}
 
 	public Object[] getPropertyValues(Object component, SessionImplementor session)
 			throws HibernateException {
 		return getPropertyValues( component, session.getEntityMode() );
 	}
 
 	public Object[] getPropertyValues(Object component, EntityMode entityMode)
 			throws HibernateException {
 		return tuplizerMapping.getTuplizer( entityMode ).getPropertyValues( component );
 	}
 
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
 			throws HibernateException {
 		tuplizerMapping.getTuplizer( entityMode ).setPropertyValues( component, values );
 	}
 
 	public Type[] getSubtypes() {
 		return propertyTypes;
 	}
 
 	public String getName() {
 		return "component" + ArrayHelper.toString( propertyNames );
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		Map result = new HashMap();
 		EntityMode entityMode = tuplizerMapping.guessEntityMode( value );
 		if ( entityMode == null ) {
 			throw new ClassCastException( value.getClass().getName() );
 		}
 		Object[] values = getPropertyValues( value, entityMode );
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			result.put( propertyNames[i], propertyTypes[i].toLoggableString( values[i], factory ) );
 		}
 		return StringHelper.unqualify( getName() ) + result.toString();
 	}
 
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
 
 	public Object deepCopy(Object component, EntityMode entityMode, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( component == null ) {
 			return null;
 		}
 
 		Object[] values = getPropertyValues( component, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			values[i] = propertyTypes[i].deepCopy( values[i], entityMode, factory );
 		}
 
 		Object result = instantiate( entityMode );
 		setPropertyValues( result, values, entityMode );
 
 		//not absolutely necessary, but helps for some
 		//equals()/hashCode() implementations
 		ComponentTuplizer ct = ( ComponentTuplizer ) tuplizerMapping.getTuplizer( entityMode );
 		if ( ct.hasParentProperty() ) {
 			ct.setParent( result, ct.getParent( component ), factory );
 		}
 
 		return result;
 	}
 
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache)
 			throws HibernateException {
 
 		if ( original == null ) {
 			return null;
 		}
 		//if ( original == target ) return target;
 
 		final Object result = target == null
 				? instantiate( owner, session )
 				: target;
 
 		final EntityMode entityMode = session.getEntityMode();
 		Object[] values = TypeHelper.replace(
 				getPropertyValues( original, entityMode ),
 				getPropertyValues( result, entityMode ),
 				propertyTypes,
 				session,
 				owner,
 				copyCache
 		);
 
 		setPropertyValues( result, values, entityMode );
 		return result;
 	}
 
 	@Override
     public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache,
 			ForeignKeyDirection foreignKeyDirection)
 			throws HibernateException {
 
 		if ( original == null ) {
 			return null;
 		}
 		//if ( original == target ) return target;
 
 		final Object result = target == null ?
 				instantiate( owner, session ) :
 				target;
 
 		final EntityMode entityMode = session.getEntityMode();
 		Object[] values = TypeHelper.replace(
 				getPropertyValues( original, entityMode ),
 				getPropertyValues( result, entityMode ),
 				propertyTypes,
 				session,
 				owner,
 				copyCache,
 				foreignKeyDirection
 		);
 
 		setPropertyValues( result, values, entityMode );
 		return result;
 	}
 
 	/**
 	 * This method does not populate the component parent
 	 */
 	public Object instantiate(EntityMode entityMode) throws HibernateException {
 		return tuplizerMapping.getTuplizer( entityMode ).instantiate();
 	}
 
 	public Object instantiate(Object parent, SessionImplementor session)
 			throws HibernateException {
 
 		Object result = instantiate( session.getEntityMode() );
 
 		ComponentTuplizer ct = ( ComponentTuplizer ) tuplizerMapping.getTuplizer( session.getEntityMode() );
 		if ( ct.hasParentProperty() && parent != null ) {
 			ct.setParent(
 					result,
 					session.getPersistenceContext().proxyFor( parent ),
 					session.getFactory()
 			);
 		}
 
 		return result;
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return cascade[i];
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	@Override
     public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			Object[] values = getPropertyValues( value, session.getEntityMode() );
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				values[i] = propertyTypes[i].disassemble( values[i], session, owner );
 			}
 			return values;
 		}
 	}
 
 	@Override
     public Object assemble(Serializable object, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( object == null ) {
 			return null;
 		}
 		else {
 			Object[] values = ( Object[] ) object;
 			Object[] assembled = new Object[values.length];
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				assembled[i] = propertyTypes[i].assemble( ( Serializable ) values[i], session, owner );
 			}
 			Object result = instantiate( owner, session );
 			setPropertyValues( result, assembled, session.getEntityMode() );
 			return result;
 		}
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return joinedFetch[i];
 	}
 
 	@Override
     public Object hydrate(
 			final ResultSet rs,
 			final String[] names,
 			final SessionImplementor session,
 			final Object owner)
 			throws HibernateException, SQLException {
 
 		int begin = 0;
 		boolean notNull = false;
 		Object[] values = new Object[propertySpan];
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int length = propertyTypes[i].getColumnSpan( session.getFactory() );
 			String[] range = ArrayHelper.slice( names, begin, length ); //cache this
 			Object val = propertyTypes[i].hydrate( rs, range, session, owner );
 			if ( val == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[i] = val;
 			begin += length;
 		}
 
 		return notNull ? values : null;
 	}
 
 	@Override
     public Object resolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( value != null ) {
 			Object result = instantiate( owner, session );
 			Object[] values = ( Object[] ) value;
 			Object[] resolvedValues = new Object[values.length]; //only really need new array during semiresolve!
 			for ( int i = 0; i < values.length; i++ ) {
 				resolvedValues[i] = propertyTypes[i].resolve( values[i], session, owner );
 			}
 			setPropertyValues( result, resolvedValues, session.getEntityMode() );
 			return result;
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
     public Object semiResolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//note that this implementation is kinda broken
 		//for components with many-to-one associations
 		return resolve( value, session, owner );
 	}
 
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	@Override
     public boolean isXMLElement() {
 		return true;
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return xml;
 	}
 
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
 		replaceNode( node, ( Element ) value );
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan( mapping ) ];
 		if ( value == null ) {
 			return result;
 		}
 		Object[] values = getPropertyValues( value, EntityMode.POJO ); //TODO!!!!!!!
 		int loc = 0;
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			boolean[] propertyNullness = propertyTypes[i].toColumnNullness( values[i], mapping );
 			System.arraycopy( propertyNullness, 0, result, loc, propertyNullness.length );
 			loc += propertyNullness.length;
 		}
 		return result;
 	}
 
 	public boolean isEmbedded() {
 		return false;
 	}
 
 	public int getPropertyIndex(String name) {
 		String[] names = getPropertyNames();
 		for ( int i = 0, max = names.length; i < max; i++ ) {
 			if ( names[i].equals( name ) ) {
 				return i;
 			}
 		}
 		throw new PropertyNotFoundException(
 				"Unable to locate property named " + name + " on " + getReturnedClass().getName()
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java b/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
index 32555a8f8e..f8e5ac4909 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
@@ -1,293 +1,319 @@
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
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.metamodel.relational.Size;
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
 
 	public Object[] getPropertyValues(Object component, SessionImplementor session)
 		throws HibernateException {
 		return getPropertyValues( component, session.getEntityMode() );
 	}
 
 	public Object[] getPropertyValues(Object component, EntityMode entityMode)
 		throws HibernateException {
 
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
 
 	public Object getPropertyValue(Object component, int i)
 		throws HibernateException {
 		return userType.getPropertyValue(component, i);
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return CascadeStyle.NONE;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return FetchMode.DEFAULT;
 	}
 
 	public boolean isComponentType() {
 		return true;
 	}
 
 	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory) 
 	throws HibernateException {
 		return userType.deepCopy(value);
 	}
 
 	public Object assemble(
 		Serializable cached,
 		SessionImplementor session,
 		Object owner)
 		throws HibernateException {
 
 		return userType.assemble(cached, session, owner);
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
 	
 	public boolean isEqual(Object x, Object y, EntityMode entityMode) 
 	throws HibernateException {
 		return userType.equals(x, y);
 	}
 
 	public int getHashCode(Object x, EntityMode entityMode) {
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
 
-		return userType.nullSafeGet(rs, new String[] {columnName}, session, owner);
+		return userType.nullSafeGet( rs, new String[] {columnName}, session, owner );
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
-
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
+
+	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		//Not called at runtime so doesn't matter if its slow :)
+		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
+		int soFar = 0;
+		for ( Type propertyType : userType.getPropertyTypes() ) {
+			final Size[] propertySizes = propertyType.dictatedSizes( mapping );
+			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
+			soFar += propertySizes.length;
+		}
+		return sizes;
+	}
+
+	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		//Not called at runtime so doesn't matter if its slow :)
+		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
+		int soFar = 0;
+		for ( Type propertyType : userType.getPropertyTypes() ) {
+			final Size[] propertySizes = propertyType.defaultSizes( mapping );
+			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
+			soFar += propertySizes.length;
+		}
+		return sizes;
+	}
 	
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
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CustomType.java b/hibernate-core/src/main/java/org/hibernate/type/CustomType.java
index cf948d5fd0..522b53541c 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CustomType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CustomType.java
@@ -1,231 +1,251 @@
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
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.Map;
 import org.dom4j.Node;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.metamodel.relational.Size;
 import org.hibernate.usertype.EnhancedUserType;
 import org.hibernate.usertype.LoggableUserType;
+import org.hibernate.usertype.Sized;
 import org.hibernate.usertype.UserType;
 import org.hibernate.usertype.UserVersionType;
 
 /**
  * Adapts {@link UserType} to the generic {@link Type} interface, in order
  * to isolate user code from changes in the internal Type contracts.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class CustomType extends AbstractType implements IdentifierType, DiscriminatorType, VersionType, BasicType {
 	private final UserType userType;
 	private final String name;
 	private final int[] types;
+	private final Size[] dictatedSizes;
+	private final Size[] defaultSizes;
 	private final boolean customLogging;
 	private final String[] registrationKeys;
 
 	public CustomType(UserType userType) throws MappingException {
 		this( userType, ArrayHelper.EMPTY_STRING_ARRAY );
 	}
 
 	public CustomType(UserType userType, String[] registrationKeys) throws MappingException {
 		this.userType = userType;
 		this.name = userType.getClass().getName();
 		this.types = userType.sqlTypes();
+		this.dictatedSizes = Sized.class.isInstance( userType )
+				? ( (Sized) userType ).dictatedSizes()
+				: new Size[ types.length ];
+		this.defaultSizes = Sized.class.isInstance( userType )
+				? ( (Sized) userType ).defaultSizes()
+				: new Size[ types.length ];
 		this.customLogging = LoggableUserType.class.isInstance( userType );
 		this.registrationKeys = registrationKeys;
 	}
 
 	public UserType getUserType() {
 		return userType;
 	}
 
 	public String[] getRegistrationKeys() {
 		return registrationKeys;
 	}
 
 	public int[] sqlTypes(Mapping pi) {
 		return types;
 	}
 
+	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		return dictatedSizes;
+	}
+
+	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		return defaultSizes;
+	}
+
 	public int getColumnSpan(Mapping session) {
 		return types.length;
 	}
 
 	public Class getReturnedClass() {
 		return userType.returnedClass();
 	}
 
 	public boolean isEqual(Object x, Object y) throws HibernateException {
 		return userType.equals(x, y);
 	}
 
 	public boolean isEqual(Object x, Object y, EntityMode entityMode) throws HibernateException {
 		return isEqual(x, y);
 	}
 
 	public int getHashCode(Object x, EntityMode entityMode) {
 		return userType.hashCode(x);
 	}
 
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return userType.nullSafeGet(rs, names, session, owner);
 	}
 
 	public Object nullSafeGet(ResultSet rs, String columnName, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return nullSafeGet(rs, new String[] { columnName }, session, owner);
 	}
 
 
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 			throws HibernateException {
 		return userType.assemble(cached, owner);
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		return userType.disassemble(value);
 	}
 
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache) throws HibernateException {
 		return userType.replace(original, target, owner);
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( settable[0] ) {
 			userType.nullSafeSet( st, value, index, session );
 		}
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
 			throws HibernateException, SQLException {
 		userType.nullSafeSet( st, value, index, session );
 	}
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public String toXMLString(Object value, SessionFactoryImplementor factory) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( userType instanceof EnhancedUserType ) {
 			return ( (EnhancedUserType) userType ).toXMLString( value );
 		}
 		else {
 			return value.toString();
 		}
 	}
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public Object fromXMLString(String xml, Mapping factory) {
 		return ( (EnhancedUserType) userType ).fromXMLString(xml);
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return userType.deepCopy(value);
 	}
 
 	public boolean isMutable() {
 		return userType.isMutable();
 	}
 
 	public Object stringToObject(String xml) {
 		return ( (EnhancedUserType) userType ).fromXMLString(xml);
 	}
 
 	public String objectToSQLString(Object value, Dialect dialect) throws Exception {
 		return ( (EnhancedUserType) userType ).objectToSQLString(value);
 	}
 
 	public Comparator getComparator() {
 		return (Comparator) userType;
 	}
 
 	public Object next(Object current, SessionImplementor session) {
 		return ( (UserVersionType) userType ).next( current, session );
 	}
 
 	public Object seed(SessionImplementor session) {
 		return ( (UserVersionType) userType ).seed( session );
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return fromXMLString( xml.getText(), factory );
 	}
 
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		node.setText( toXMLString(value, factory) );
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		else if ( customLogging ) {
 			return ( ( LoggableUserType ) userType ).toLoggableString( value, factory );
 		}
 		else {
 			return toXMLString( value, factory );
 		}
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan(mapping) ];
 		if ( value != null ) {
 			Arrays.fill(result, true);
 		}
 		return result;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return checkable[0] && isDirty(old, current, session);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
index 8e1bce21f8..4e61f3fc17 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
@@ -1,286 +1,297 @@
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
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.ForeignKeys;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * A many-to-one association to an entity.
  *
  * @author Gavin King
  */
 public class ManyToOneType extends EntityType {
 	private final boolean ignoreNotFound;
 	private boolean isLogicalOneToOne;
 
 	/**
 	 * Creates a many-to-one association type with the given referenced entity.
 	 *
 	 * @param scope The scope for this instance.
 	 * @param referencedEntityName The name iof the referenced entity
 	 */
 	public ManyToOneType(TypeFactory.TypeScope scope, String referencedEntityName) {
 		this( scope, referencedEntityName, false );
 	}
 
 	/**
 	 * Creates a many-to-one association type with the given referenced entity and the
 	 * given laziness characteristic
 	 *
 	 * @param scope The scope for this instance.
 	 * @param referencedEntityName The name iof the referenced entity
 	 * @param lazy Should the association be handled lazily
 	 */
 	public ManyToOneType(TypeFactory.TypeScope scope, String referencedEntityName, boolean lazy) {
 		this( scope, referencedEntityName, null, lazy, true, false, false, false );
 	}
 
 	public ManyToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			boolean ignoreNotFound,
 			boolean isLogicalOneToOne) {
 		super( scope, referencedEntityName, uniqueKeyPropertyName, !lazy, isEmbeddedInXML, unwrapProxy );
 		this.ignoreNotFound = ignoreNotFound;
 		this.isLogicalOneToOne = isLogicalOneToOne;
 	}
 
 	protected boolean isNullable() {
 		return ignoreNotFound;
 	}
 
 	public boolean isAlwaysDirtyChecked() {
 		// always need to dirty-check, even when non-updateable;
 		// this ensures that when the association is updated,
 		// the entity containing this association will be updated
 		// in the cache
 		return true;
 	}
 
 	public boolean isOneToOne() {
 		return false;
 	}
 
 	public boolean isLogicalOneToOne() {
 		return isLogicalOneToOne;
 	}
 
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		// our column span is the number of columns in the PK
 		return getIdentifierOrUniqueKeyType( mapping ).getColumnSpan( mapping );
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return getIdentifierOrUniqueKeyType( mapping ).sqlTypes( mapping );
 	}
 
+	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		return getIdentifierOrUniqueKeyType( mapping ).dictatedSizes( mapping );
+	}
+
+	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		return getIdentifierOrUniqueKeyType( mapping ).defaultSizes( mapping );
+	}
+
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		getIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeSet( st, getIdentifier( value, session ), index, settable, session );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 		getIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeSet( st, getIdentifier( value, session ), index, session );
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT;
 	}
 
 	public Object hydrate(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		// return the (fully resolved) identifier value, but do not resolve
 		// to the actual referenced entity instance
 		// NOTE: the owner of the association is not really the owner of the id!
 		Serializable id = (Serializable) getIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeGet( rs, names, session, null );
 		scheduleBatchLoadIfNeeded( id, session );
 		return id;
 	}
 
 	/**
 	 * Register the entity as batch loadable, if enabled
 	 */
 	@SuppressWarnings({ "JavaDoc" })
 	private void scheduleBatchLoadIfNeeded(Serializable id, SessionImplementor session) throws MappingException {
 		//cannot batch fetch by unique key (property-ref associations)
 		if ( uniqueKeyPropertyName == null && id != null ) {
 			EntityPersister persister = session.getFactory().getEntityPersister( getAssociatedEntityName() );
 			EntityKey entityKey = new EntityKey( id, persister, session.getEntityMode() );
 			if ( !session.getPersistenceContext().containsEntity( entityKey ) ) {
 				session.getPersistenceContext().getBatchFetchQueue().addBatchLoadableEntityKey( entityKey );
 			}
 		}
 	}
 	
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 
 	public boolean isModified(
 			Object old,
 			Object current,
 			boolean[] checkable,
 			SessionImplementor session) throws HibernateException {
 		if ( current == null ) {
 			return old!=null;
 		}
 		if ( old == null ) {
 			// we already know current is not null...
 			return true;
 		}
 		// the ids are fully resolved, so compare them with isDirty(), not isModified()
 		return getIdentifierOrUniqueKeyType( session.getFactory() )
 				.isDirty( old, getIdentifier( current, session ), session );
 	}
 
 	public Serializable disassemble(
 			Object value,
 			SessionImplementor session,
 			Object owner) throws HibernateException {
 
 		if ( isNotEmbedded( session ) ) {
 			return getIdentifierType( session ).disassemble( value, session, owner );
 		}
 		
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			// cache the actual id of the object, not the value of the
 			// property-ref, which might not be initialized
 			Object id = ForeignKeys.getEntityIdentifierIfNotUnsaved( 
 					getAssociatedEntityName(), 
 					value, 
 					session
 			);
 			if ( id == null ) {
 				throw new AssertionFailure(
 						"cannot cache a reference to an object with a null id: " + 
 						getAssociatedEntityName()
 				);
 			}
 			return getIdentifierType( session ).disassemble( id, session, owner );
 		}
 	}
 
 	public Object assemble(
 			Serializable oid,
 			SessionImplementor session,
 			Object owner) throws HibernateException {
 		
 		//TODO: currently broken for unique-key references (does not detect
 		//      change to unique key property of the associated object)
 		
 		Serializable id = assembleId( oid, session );
 
 		if ( isNotEmbedded( session ) ) {
 			return id;
 		}
 		
 		if ( id == null ) {
 			return null;
 		}
 		else {
 			return resolveIdentifier( id, session );
 		}
 	}
 
 	private Serializable assembleId(Serializable oid, SessionImplementor session) {
 		//the owner of the association is not the owner of the id
 		return ( Serializable ) getIdentifierType( session ).assemble( oid, session, null );
 	}
 
 	public void beforeAssemble(Serializable oid, SessionImplementor session) {
 		scheduleBatchLoadIfNeeded( assembleId( oid, session ), session );
 	}
 	
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan( mapping ) ];
 		if ( value != null ) {
 			Arrays.fill( result, true );
 		}
 		return result;
 	}
 	
 	public boolean isDirty(
 			Object old,
 			Object current,
 			SessionImplementor session) throws HibernateException {
 		if ( isSame( old, current, session.getEntityMode() ) ) {
 			return false;
 		}
 		Object oldid = getIdentifier( old, session );
 		Object newid = getIdentifier( current, session );
 		return getIdentifierType( session ).isDirty( oldid, newid, session );
 	}
 
 	public boolean isDirty(
 			Object old,
 			Object current,
 			boolean[] checkable,
 			SessionImplementor session) throws HibernateException {
 		if ( isAlwaysDirtyChecked() ) {
 			return isDirty( old, current, session );
 		}
 		else {
 			if ( isSame( old, current, session.getEntityMode() ) ) {
 				return false;
 			}
 			Object oldid = getIdentifier( old, session );
 			Object newid = getIdentifier( current, session );
 			return getIdentifierType( session ).isDirty( oldid, newid, checkable, session );
 		}
 		
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/MetaType.java b/hibernate-core/src/main/java/org/hibernate/type/MetaType.java
index c16cf4ae6e..c6eb81c9d3 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/MetaType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/MetaType.java
@@ -1,164 +1,175 @@
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
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import org.dom4j.Node;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.metamodel.relational.Size;
 
 /**
  * @author Gavin King
  */
 public class MetaType extends AbstractType {
 	public static final String[] REGISTRATION_KEYS = new String[0];
 
 	private final Map values;
 	private final Map keys;
 	private final Type baseType;
 
 	public MetaType(Map values, Type baseType) {
 		this.baseType = baseType;
 		this.values = values;
 		keys = new HashMap();
 		Iterator iter = values.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			keys.put( me.getValue(), me.getKey() );
 		}
 	}
 
 	public String[] getRegistrationKeys() {
 		return REGISTRATION_KEYS;
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return baseType.sqlTypes(mapping);
 	}
 
+	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		return baseType.dictatedSizes( mapping );
+	}
+
+	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		return baseType.defaultSizes( mapping );
+	}
+
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		return baseType.getColumnSpan(mapping);
 	}
 
 	public Class getReturnedClass() {
 		return String.class;
 	}
 
 	public Object nullSafeGet(
 		ResultSet rs,
 		String[] names,
 		SessionImplementor session,
 		Object owner)
 	throws HibernateException, SQLException {
 		Object key = baseType.nullSafeGet(rs, names, session, owner);
 		return key==null ? null : values.get(key);
 	}
 
 	public Object nullSafeGet(
 		ResultSet rs,
 		String name,
 		SessionImplementor session,
 		Object owner)
 	throws HibernateException, SQLException {
 		Object key = baseType.nullSafeGet(rs, name, session, owner);
 		return key==null ? null : values.get(key);
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
 	throws HibernateException, SQLException {
 		baseType.nullSafeSet(st, value==null ? null : keys.get(value), index, session);
 	}
 	
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			boolean[] settable, 
 			SessionImplementor session)
 	throws HibernateException, SQLException {
 		if ( settable[0] ) nullSafeSet(st, value, index, session);
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		return toXMLString(value, factory);
 	}
 	
 	public String toXMLString(Object value, SessionFactoryImplementor factory)
 		throws HibernateException {
 		return (String) value; //value is the entity name
 	}
 
 	public Object fromXMLString(String xml, Mapping factory)
 		throws HibernateException {
 		return xml; //xml is the entity name
 	}
 
 	public String getName() {
 		return baseType.getName(); //TODO!
 	}
 
 	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory) 
 	throws HibernateException {
 		return value;
 	}
 
 	public Object replace(
 			Object original, 
 			Object target,
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache
 	) {
 		return original;
 	}
 	
 	public boolean isMutable() {
 		return false;
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return fromXMLString( xml.getText(), factory );
 	}
 
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
 		node.setText( toXMLString(value, factory) );
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		throw new UnsupportedOperationException();
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
 		return checkable[0] && isDirty(old, current, session);
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/NullableType.java b/hibernate-core/src/main/java/org/hibernate/type/NullableType.java
index 12f5dd18d9..bc4ae70e21 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/NullableType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/NullableType.java
@@ -1,233 +1,268 @@
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
+
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
+
 import org.dom4j.Node;
+import org.jboss.logging.Logger;
+
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
+import org.hibernate.MappingException;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.compare.EqualsHelper;
-import org.jboss.logging.Logger;
+import org.hibernate.metamodel.relational.Size;
 
 /**
  * Superclass of single-column nullable types.
  *
  * @author Gavin King
  *
  * @deprecated Use the {@link AbstractStandardBasicType} approach instead
  */
 @Deprecated
 public abstract class NullableType extends AbstractType implements StringRepresentableType, XmlRepresentableType {
+    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, NullableType.class.getName());
 
+	private final Size dictatedSize = new Size();
 
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, NullableType.class.getName());
+	/**
+	 * A convenience form of {@link #sqlTypes(org.hibernate.engine.Mapping)}, returning
+	 * just a single type value since these are explicitly dealing with single column
+	 * mappings.
+	 *
+	 * @return The {@link java.sql.Types} mapping value.
+	 */
+	public abstract int sqlType();
+
+	/**
+	 * A convenience form of {@link #dictatedSizes}, returning just a single size since we are explicitly dealing with
+	 * single column mappings here.
+	 *
+	 * @return The {@link java.sql.Types} mapping value.
+	 */
+	public Size dictatedSize() {
+		return dictatedSize;
+	}
+
+	/**
+	 * A convenience form of {@link #defaultSizes}, returning just a single size since we are explicitly dealing with
+	 * single column mappings here.
+	 *
+	 * @return The {@link java.sql.Types} mapping value.
+	 */
+	public Size defaultSize() {
+		return LEGACY_DEFAULT_SIZE;
+	}
 
 	/**
 	 * Get a column value from a result set, without worrying about the
 	 * possibility of null values.  Called from {@link #nullSafeGet} after
 	 * nullness checks have been performed.
 	 *
 	 * @param rs The result set from which to extract the value.
 	 * @param name The name of the value to extract.
 	 *
 	 * @return The extracted value.
 	 *
 	 * @throws org.hibernate.HibernateException Generally some form of mismatch error.
 	 * @throws java.sql.SQLException Indicates problem making the JDBC call(s).
 	 */
 	public abstract Object get(ResultSet rs, String name) throws HibernateException, SQLException;
 
 	/**
 	 * Set a parameter value without worrying about the possibility of null
 	 * values.  Called from {@link #nullSafeSet} after nullness checks have
 	 * been performed.
 	 *
 	 * @param st The statement into which to bind the parameter value.
 	 * @param value The parameter value to bind.
 	 * @param index The position or index at which to bind the param value.
 	 *
 	 * @throws org.hibernate.HibernateException Generally some form of mismatch error.
 	 * @throws java.sql.SQLException Indicates problem making the JDBC call(s).
 	 */
 	public abstract void set(PreparedStatement st, Object value, int index) throws HibernateException, SQLException;
 
 	/**
-	 * A convenience form of {@link #sqlTypes(org.hibernate.engine.Mapping)}, returning
-	 * just a single type value since these are explicitly dealing with single column
-	 * mappings.
-	 *
-	 * @return The {@link java.sql.Types} mapping value.
-	 */
-	public abstract int sqlType();
-
-	/**
 	 * A null-safe version of {@link #toString(Object)}.  Specifically we are
 	 * worried about null safeness in regards to the incoming value parameter,
 	 * not the return.
 	 *
 	 * @param value The value to convert to a string representation; may be null.
 	 * @return The string representation; may be null.
 	 * @throws HibernateException Thrown by {@link #toString(Object)}, which this calls.
 	 */
 	public String nullSafeToString(Object value) throws HibernateException {
 		return value == null ? null : toString( value );
 	}
 
 	public abstract String toString(Object value) throws HibernateException;
 
 	public abstract Object fromStringValue(String xml) throws HibernateException;
 
 	public final void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			boolean[] settable,
 			SessionImplementor session)
 	throws HibernateException, SQLException {
 		if ( settable[0] ) nullSafeSet(st, value, index);
 	}
 
 	public final void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
 	throws HibernateException, SQLException {
 		nullSafeSet(st, value, index);
 	}
 
 	public final void nullSafeSet(PreparedStatement st, Object value, int index)
 	throws HibernateException, SQLException {
 		try {
 			if ( value == null ) {
                 if (LOG.isTraceEnabled()) LOG.trace("Binding null to parameter: " + index);
 
 				st.setNull( index, sqlType() );
 			}
 			else {
                 if (LOG.isTraceEnabled()) LOG.trace("Binding '" + toString(value) + "' to parameter: " + index);
 
 				set( st, value, index );
 			}
 		}
 		catch ( RuntimeException re ) {
             LOG.unableToBindValueToParameter(nullSafeToString(value), index, re.getMessage());
 			throw re;
 		}
 		catch ( SQLException se ) {
             LOG.unableToBindValueToParameter(nullSafeToString(value), index, se.getMessage());
 			throw se;
 		}
 	}
 
 	public final Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner)
 	throws HibernateException, SQLException {
 		return nullSafeGet(rs, names[0]);
 	}
 
 	public final Object nullSafeGet(ResultSet rs, String[] names)
 	throws HibernateException, SQLException {
 		return nullSafeGet(rs, names[0]);
 	}
 
 	public final Object nullSafeGet(ResultSet rs, String name)
 	throws HibernateException, SQLException {
 		try {
 			Object value = get(rs, name);
 			if ( value == null || rs.wasNull() ) {
                 if (LOG.isTraceEnabled()) LOG.trace("Returning null as column " + name);
 				return null;
 			}
             if (LOG.isTraceEnabled()) LOG.trace("Returning '" + toString(value) + "' as column " + name);
             return value;
 		}
 		catch ( RuntimeException re ) {
             LOG.unableToReadColumnValueFromResultSet(name, re.getMessage());
 			throw re;
 		}
 		catch ( SQLException se ) {
             LOG.unableToReadColumnValueFromResultSet(name, se.getMessage());
 			throw se;
 		}
 	}
 
 	public final Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException {
 		return nullSafeGet(rs, name);
 	}
 
 	public final String toXMLString(Object value, SessionFactoryImplementor pc)
 	throws HibernateException {
 		return toString(value);
 	}
 
 	public final Object fromXMLString(String xml, Mapping factory) throws HibernateException {
 		return xml==null || xml.length()==0 ? null : fromStringValue(xml);
 	}
 
 	public final int getColumnSpan(Mapping session) {
 		return 1;
 	}
 
 	public final int[] sqlTypes(Mapping session) {
 		return new int[] { sqlType() };
 	}
 
 	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		return new Size[] { dictatedSize() };
+	}
+
+	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		return new Size[] { defaultSize() };
+	}
+
+	@Override
     public final boolean isEqual(Object x, Object y, EntityMode entityMode) {
 		return isEqual(x, y);
 	}
 
 	public boolean isEqual(Object x, Object y) {
 		return EqualsHelper.equals(x, y);
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) {
 		return value == null ? "null" : toString(value);
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return fromXMLString( xml.getText(), factory );
 	}
 
 	public void setToXMLNode(Node xml, Object value, SessionFactoryImplementor factory)
 	throws HibernateException {
 		xml.setText( toXMLString(value, factory) );
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return value==null ? ArrayHelper.FALSE : ArrayHelper.TRUE;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 	throws HibernateException {
 		return checkable[0] && isDirty(old, current, session);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
index a72af60b47..4edb7a97a1 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
@@ -1,169 +1,182 @@
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
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * A one-to-one association to an entity
  * @author Gavin King
  */
 public class OneToOneType extends EntityType {
 
 	private final ForeignKeyDirection foreignKeyType;
 	private final String propertyName;
 	private final String entityName;
 
 	public OneToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			ForeignKeyDirection foreignKeyType,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			String entityName,
 			String propertyName) {
 		super( scope, referencedEntityName, uniqueKeyPropertyName, !lazy, isEmbeddedInXML, unwrapProxy );
 		this.foreignKeyType = foreignKeyType;
 		this.propertyName = propertyName;
 		this.entityName = entityName;
 	}
 	
 	public String getPropertyName() {
 		return propertyName;
 	}
 	
 	public boolean isNull(Object owner, SessionImplementor session) {
 		
 		if ( propertyName != null ) {
 			
 			EntityPersister ownerPersister = session.getFactory()
 					.getEntityPersister(entityName); 
 			Serializable id = session.getContextEntityIdentifier(owner);
 
 			EntityKey entityKey = new EntityKey( id, ownerPersister, session.getEntityMode() );
 			
 			return session.getPersistenceContext()
 					.isPropertyNull( entityKey, getPropertyName() );
 			
 		}
 		else {
 			return false;
 		}
 
 	}
 
 	public int getColumnSpan(Mapping session) throws MappingException {
 		return 0;
 	}
 
 	public int[] sqlTypes(Mapping session) throws MappingException {
 		return ArrayHelper.EMPTY_INT_ARRAY;
 	}
 
+	private static final Size[] SIZES = new Size[0];
+
+	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		return SIZES;
+	}
+
+	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		return SIZES;
+	}
+
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session) {
 		//nothing to do
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) {
 		//nothing to do
 	}
 
 	public boolean isOneToOne() {
 		return true;
 	}
 
 	public boolean isDirty(Object old, Object current, SessionImplementor session) {
 		return false;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) {
 		return false;
 	}
 
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) {
 		return false;
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return foreignKeyType;
 	}
 
 	public Object hydrate(
 		ResultSet rs,
 		String[] names,
 		SessionImplementor session,
 		Object owner)
 	throws HibernateException, SQLException {
 
 		return session.getContextEntityIdentifier(owner);
 	}
 
 	protected boolean isNullable() {
 		return foreignKeyType==ForeignKeyDirection.FOREIGN_KEY_TO_PARENT;
 	}
 
 	public boolean useLHSPrimaryKey() {
 		return true;
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 		return null;
 	}
 
 	public Object assemble(Serializable oid, SessionImplementor session, Object owner)
 	throws HibernateException {
 		//this should be a call to resolve(), not resolveIdentifier(), 
 		//'cos it might be a property-ref, and we did not cache the
 		//referenced value
 		return resolve( session.getContextEntityIdentifier(owner), session, owner );
 	}
 	
 	/**
 	 * We don't need to dirty check one-to-one because of how 
 	 * assemble/disassemble is implemented and because a one-to-one 
 	 * association is never dirty
 	 */
 	public boolean isAlwaysDirtyChecked() {
 		//TODO: this is kinda inconsistent with CollectionType
 		return false; 
 	}
 	
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
index 8d827fcfdb..50abd7cbed 100755
--- a/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
@@ -1,127 +1,138 @@
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
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.ForeignKeys;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.metamodel.relational.Size;
 
 /**
  * A one-to-one association that maps to specific formula(s)
  * instead of the primary key column of the owning entity.
  * 
  * @author Gavin King
  */
 public class SpecialOneToOneType extends OneToOneType {
 	
 	public SpecialOneToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			ForeignKeyDirection foreignKeyType, 
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			String entityName,
 			String propertyName) {
 		super(
 				scope,
 				referencedEntityName, 
 				foreignKeyType, 
 				uniqueKeyPropertyName, 
 				lazy,
 				unwrapProxy,
 				true, 
 				entityName, 
 				propertyName
 			);
 	}
 	
 	public int getColumnSpan(Mapping mapping) throws MappingException {
-		return super.getIdentifierOrUniqueKeyType(mapping).getColumnSpan(mapping);
+		return super.getIdentifierOrUniqueKeyType( mapping ).getColumnSpan( mapping );
 	}
 	
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
-		return super.getIdentifierOrUniqueKeyType(mapping).sqlTypes(mapping);
+		return super.getIdentifierOrUniqueKeyType( mapping ).sqlTypes( mapping );
+	}
+
+	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		return super.getIdentifierOrUniqueKeyType( mapping ).dictatedSizes( mapping );
+	}
+
+	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		return super.getIdentifierOrUniqueKeyType( mapping ).defaultSizes( mapping );
 	}
 
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 	
 	public Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException {
 		return super.getIdentifierOrUniqueKeyType( session.getFactory() )
 			.nullSafeGet(rs, names, session, owner);
 	}
 	
 	// TODO: copy/paste from ManyToOneType
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 
 		if ( isNotEmbedded(session) ) {
 			return getIdentifierType(session).disassemble(value, session, owner);
 		}
 		
 		if (value==null) {
 			return null;
 		}
 		else {
 			// cache the actual id of the object, not the value of the
 			// property-ref, which might not be initialized
 			Object id = ForeignKeys.getEntityIdentifierIfNotUnsaved( getAssociatedEntityName(), value, session );
 			if (id==null) {
 				throw new AssertionFailure(
 						"cannot cache a reference to an object with a null id: " + 
 						getAssociatedEntityName() 
 				);
 			}
 			return getIdentifierType(session).disassemble(id, session, owner);
 		}
 	}
 
 	public Object assemble(Serializable oid, SessionImplementor session, Object owner)
 	throws HibernateException {
 		//TODO: currently broken for unique-key references (does not detect
 		//      change to unique key property of the associated object)
 		Serializable id = (Serializable) getIdentifierType(session).assemble(oid, session, null); //the owner of the association is not the owner of the id
 
 		if ( isNotEmbedded(session) ) return id;
 		
 		if (id==null) {
 			return null;
 		}
 		else {
 			return resolveIdentifier(id, session);
 		}
 	}
 	
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/Type.java b/hibernate-core/src/main/java/org/hibernate/type/Type.java
index 80b47e0547..c0961b6220 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/Type.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/Type.java
@@ -1,519 +1,555 @@
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
+
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
+
 import org.dom4j.Node;
+
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.metamodel.relational.Size;
 
 /**
  * Defines a mapping between a Java type and one or more JDBC {@linkplain java.sql.Types types}, as well
  * as describing the in-memory semantics of the given java type (how do we check it for 'dirtiness', how do
  * we copy values, etc).
  * <p/>
  * Application developers needing custom types can implement this interface (either directly or via subclassing an
  * existing impl) or by the (slightly more stable, though more limited) {@link org.hibernate.usertype.UserType}
  * interface.
  * <p/>
  * Implementations of this interface must certainly be thread-safe.  It is recommended that they be immutable as
  * well, though that is difficult to achieve completely given the no-arg constructor requirement for custom types.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface Type extends Serializable {
 	/**
 	 * Return true if the implementation is castable to {@link AssociationType}. This does not necessarily imply that
 	 * the type actually represents an association.  Essentially a polymorphic version of
 	 * {@code (type instanceof AssociationType.class)}
 	 *
 	 * @return True if this type is also an {@link AssociationType} implementor; false otherwise.
 	 */
 	public boolean isAssociationType();
 
 	/**
 	 * Return true if the implementation is castable to {@link CollectionType}. Essentially a polymorphic version of
 	 * {@code (type instanceof CollectionType.class)}
 	 * <p/>
 	 * A {@link CollectionType} is additionally an {@link AssociationType}; so if this method returns true,
 	 * {@link #isAssociationType()} should also return true.
 	 *
 	 * @return True if this type is also an {@link CollectionType} implementor; false otherwise.
 	 */
 	public boolean isCollectionType();
 
 	/**
 	 * Return true if the implementation is castable to {@link EntityType}. Essentially a polymorphic
 	 * version of {@code (type instanceof EntityType.class)}.
 	 * <p/>
 	 * An {@link EntityType} is additionally an {@link AssociationType}; so if this method returns true,
 	 * {@link #isAssociationType()} should also return true.
 	 *
 	 * @return True if this type is also an {@link EntityType} implementor; false otherwise.
 	 */
 	public boolean isEntityType();
 
 	/**
 	 * Return true if the implementation is castable to {@link AnyType}. Essentially a polymorphic
 	 * version of {@code (type instanceof AnyType.class)}.
 	 * <p/>
 	 * An {@link AnyType} is additionally an {@link AssociationType}; so if this method returns true,
 	 * {@link #isAssociationType()} should also return true.
 	 *
 	 * @return True if this type is also an {@link AnyType} implementor; false otherwise.
 	 */
 	public boolean isAnyType();
 
 	/**
 	 * Return true if the implementation is castable to {@link CompositeType}. Essentially a polymorphic
 	 * version of {@code (type instanceof CompositeType.class)}.  A component type may own collections or
 	 * associations and hence must provide certain extra functionality.
 	 *
 	 * @return True if this type is also an {@link CompositeType} implementor; false otherwise.
 	 */
 	public boolean isComponentType();
 
 	/**
+	 * How many columns are used to persist this type.  Always the same as {@code sqlTypes(mapping).length}
+	 *
+	 * @param mapping The mapping object :/
+	 *
+	 * @return The number of columns
+	 *
+	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
+	 */
+	public int getColumnSpan(Mapping mapping) throws MappingException;
+
+	/**
 	 * Return the JDBC types codes (per {@link java.sql.Types}) for the columns mapped by this type.
+	 * <p/>
+	 * NOTE: The number of elements in this array matches the return from {@link #getColumnSpan}.
 	 *
 	 * @param mapping The mapping object :/
 	 *
 	 * @return The JDBC type codes.
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
 	public int[] sqlTypes(Mapping mapping) throws MappingException;
 
 	/**
-	 * How many columns are used to persist this type.  Always the same as {@code sqlTypes(mapping).length}
+	 * Return the column sizes dictated by this type.  For example, the mapping for a {@code char}/{@link Character} would
+	 * have a dictated length limit of 1; for a string-based {@link java.util.UUID} would have a size limit of 36; etc.
+	 * <p/>
+	 * NOTE: The number of elements in this array matches the return from {@link #getColumnSpan}.
 	 *
 	 * @param mapping The mapping object :/
+	 * @todo Would be much much better to have this aware of Dialect once the service/metamodel split is done
 	 *
-	 * @return The number of columns
+	 * @return The dictated sizes.
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
-	public int getColumnSpan(Mapping mapping) throws MappingException;
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException;
+
+	/**
+	 * Defines the column sizes to use according to this type if the user did not explicitly say (and if no
+	 * {@link #dictatedSizes} were given).
+	 * <p/>
+	 * NOTE: The number of elements in this array matches the return from {@link #getColumnSpan}.
+	 *
+	 * @param mapping The mapping object :/
+	 * @todo Would be much much better to have this aware of Dialect once the service/metamodel split is done
+	 *
+	 * @return The default sizes.
+	 *
+	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
+	 */
+	public Size[] defaultSizes(Mapping mapping) throws MappingException;
 
 	/**
 	 * The class returned by {@link #nullSafeGet} methods. This is used to  establish the class of an array of
 	 * this type.
 	 *
 	 * @return The java type class handled by this type.
 	 */
 	public Class getReturnedClass();
 	
 	public boolean isXMLElement();
 
 	/**
 	 * Compare two instances of the class mapped by this type for persistence "equality" (equality of persistent
 	 * state) taking a shortcut for entity references.
 	 * <p/>
 	 * For most types this should equate to {@link #equals} check on the values.  For associations the implication
 	 * is a bit different.  For most types it is conceivable to simply delegate to {@link #isEqual}
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 * @param entityMode The entity mode of the values.
 	 *
 	 * @return True if there are considered the same (see discussion above).
 	 *
 	 * @throws HibernateException A problem occurred performing the comparison
 	 */
 	public boolean isSame(Object x, Object y, EntityMode entityMode) throws HibernateException;
 
 	/**
 	 * Compare two instances of the class mapped by this type for persistence "equality" (equality of persistent
 	 * state).
 	 * <p/>
 	 * This should always equate to some form of comparison of the value's internal state.  As an example, for
 	 * something like a date the comparison should be based on its internal "time" state based on the specific portion
 	 * it is meant to represent (timestamp, date, time).
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 * @param entityMode The entity mode of the values.
 	 *
 	 * @return True if there are considered equal (see discussion above).
 	 *
 	 * @throws HibernateException A problem occurred performing the comparison
 	 */
 	public boolean isEqual(Object x, Object y, EntityMode entityMode) throws HibernateException;
 
 	/**
 	 * Compare two instances of the class mapped by this type for persistence "equality" (equality of persistent
 	 * state).
 	 * <p/>
 	 * This should always equate to some form of comparison of the value's internal state.  As an example, for
 	 * something like a date the comparison should be based on its internal "time" state based on the specific portion
 	 * it is meant to represent (timestamp, date, time).
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 * @param entityMode The entity mode of the values.
 	 * @param factory The session factory
 	 *
 	 * @return True if there are considered equal (see discussion above).
 	 *
 	 * @throws HibernateException A problem occurred performing the comparison
 	 */
 	public boolean isEqual(Object x, Object y, EntityMode entityMode, SessionFactoryImplementor factory)
 			throws HibernateException;
 
 	/**
 	 * Get a hash code, consistent with persistence "equality".  Again for most types the normal usage is to
 	 * delegate to the value's {@link #hashCode}.
 	 *
 	 * @param x The value for which to retrieve a hash code
 	 * @param entityMode The entity mode of the value.
 	 *
 	 * @return The hash code
 	 *
 	 * @throws HibernateException A problem occurred calculating the hash code
 	 */
 	public int getHashCode(Object x, EntityMode entityMode) throws HibernateException;
 
 	/**
 	 * Get a hash code, consistent with persistence "equality".  Again for most types the normal usage is to
 	 * delegate to the value's {@link #hashCode}.
 	 *
 	 * @param x The value for which to retrieve a hash code
 	 * @param entityMode The entity mode of the value.
 	 * @param factory The session factory
 	 *
 	 * @return The hash code
 	 *
 	 * @throws HibernateException A problem occurred calculating the hash code
 	 */
 	public int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor factory) throws HibernateException;
 	
 	/**
 	 * Perform a {@link java.util.Comparator} style comparison between values
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 * @param entityMode The entity mode of the values.
 	 *
 	 * @return The comparison result.  See {@link java.util.Comparator#compare} for a discussion.
 	 */
 	public int compare(Object x, Object y, EntityMode entityMode);
 
 	/**
 	 * Should the parent be considered dirty, given both the old and current value?
 	 * 
 	 * @param old the old value
 	 * @param current the current value
 	 * @param session The session from which the request originated.
 	 *
 	 * @return true if the field is dirty
 	 *
 	 * @throws HibernateException A problem occurred performing the checking
 	 */
 	public boolean isDirty(Object old, Object current, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Should the parent be considered dirty, given both the old and current value?
 	 *
 	 * @param oldState the old value
 	 * @param currentState the current value
 	 * @param checkable An array of booleans indicating which columns making up the value are actually checkable
 	 * @param session The session from which the request originated.
 	 *
 	 * @return true if the field is dirty
 	 *
 	 * @throws HibernateException A problem occurred performing the checking
 	 */
 	public boolean isDirty(Object oldState, Object currentState, boolean[] checkable, SessionImplementor session)
 			throws HibernateException;
 
 	/**
 	 * Has the value been modified compared to the current database state?  The difference between this
 	 * and the {@link #isDirty} methods is that here we need to account for "partially" built values.  This is really
 	 * only an issue with association types.  For most type implementations it is enough to simply delegate to
 	 * {@link #isDirty} here/
 	 *
 	 * @param dbState the database state, in a "hydrated" form, with identifiers unresolved
 	 * @param currentState the current state of the object
 	 * @param checkable which columns are actually updatable
 	 * @param session The session from which the request originated.
 	 *
 	 * @return true if the field has been modified
 	 *
 	 * @throws HibernateException A problem occurred performing the checking
 	 */
 	public boolean isModified(Object dbState, Object currentState, boolean[] checkable, SessionImplementor session)
 			throws HibernateException;
 
 	/**
 	 * Retrieve an instance of the mapped class from a JDBC resultset. Implementors
 	 * should handle possibility of null values.
 	 *
 	 * @see Type#hydrate(ResultSet, String[], SessionImplementor, Object) alternative, 2-phase property initialization
 	 * @param rs
 	 * @param names the column names
 	 * @param session
 	 * @param owner the parent entity
 	 * @return Object
 	 * @throws HibernateException
 	 * @throws SQLException
 	 */
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException;
 
 	/**
 	 * Retrieve an instance of the mapped class from a JDBC resultset. Implementations
 	 * should handle possibility of null values. This method might be called if the
 	 * type is known to be a single-column type.
 	 *
 	 * @param rs
 	 * @param name the column name
 	 * @param session
 	 * @param owner the parent entity
 	 * @return Object
 	 * @throws HibernateException
 	 * @throws SQLException
 	 */
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException;
 
 	/**
 	 * Write an instance of the mapped class to a prepared statement, ignoring some columns. 
 	 * Implementors should handle possibility of null values. A multi-column type should be 
 	 * written to parameters starting from <tt>index</tt>.
 	 * @param st
 	 * @param value the object to write
 	 * @param index statement parameter index
 	 * @param settable an array indicating which columns to ignore
 	 * @param session
 	 *
 	 * @throws HibernateException
 	 * @throws SQLException
 	 */
 	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session)
 	throws HibernateException, SQLException;
 
 	/**
 	 * Write an instance of the mapped class to a prepared statement. Implementors
 	 * should handle possibility of null values. A multi-column type should be written
 	 * to parameters starting from <tt>index</tt>.
 	 * @param st
 	 * @param value the object to write
 	 * @param index statement parameter index
 	 * @param session
 	 *
 	 * @throws HibernateException
 	 * @throws SQLException
 	 */
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
 	throws HibernateException, SQLException;
 
 	/**
 	 * A representation of the value to be embedded in an XML element.
 	 *
 	 * @param value
 	 * @param factory
 	 * @return String
 	 * @throws HibernateException
 	 */
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory)
 	throws HibernateException;
 
 	/**
 	 * A representation of the value to be embedded in a log file.
 	 *
 	 * @param value
 	 * @param factory
 	 * @return String
 	 * @throws HibernateException
 	 */
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 	throws HibernateException;
 
 	/**
 	 * Parse the XML representation of an instance.
 	 * @param xml
 	 * @param factory
 	 *
 	 * @return an instance of the type
 	 * @throws HibernateException
 	 */
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException;
 
 	/**
 	 * Returns the abbreviated name of the type.
 	 *
 	 * @return String the Hibernate type name
 	 */
 	public String getName();
 
 	/**
 	 * Return a deep copy of the persistent state, stopping at entities and at
 	 * collections.
 	 * @param value generally a collection element or entity field
 	 * @param entityMode 
 	 * @param factory
 	 * @return Object a copy
 	 */
 	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory) 
 	throws HibernateException;
 
 	/**
 	 * Are objects of this type mutable. (With respect to the referencing object ...
 	 * entities and collections are considered immutable because they manage their
 	 * own internal state.)
 	 *
 	 * @return boolean
 	 */
 	public boolean isMutable();
 
 	/**
 	 * Return a cacheable "disassembled" representation of the object.
 	 * @param value the value to cache
 	 * @param session the session
 	 * @param owner optional parent entity object (needed for collections)
 	 * @return the disassembled, deep cloned state
 	 */
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException;
 
 	/**
 	 * Reconstruct the object from its cached "disassembled" state.
 	 * @param cached the disassembled state from the cache
 	 * @param session the session
 	 * @param owner the parent entity object
 	 * @return the the object
 	 */
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 	throws HibernateException;
 	
 	/**
 	 * Called before assembling a query result set from the query cache, to allow batch fetching
 	 * of entities missing from the second-level cache.
 	 */
 	public void beforeAssemble(Serializable cached, SessionImplementor session);
 
 	/**
 	 * Retrieve an instance of the mapped class, or the identifier of an entity or collection, 
 	 * from a JDBC resultset. This is useful for 2-phase property initialization - the second 
 	 * phase is a call to <tt>resolveIdentifier()</tt>.
 	 * 
 	 * @see Type#resolve(Object, SessionImplementor, Object)
 	 * @param rs
 	 * @param names the column names
 	 * @param session the session
 	 * @param owner the parent entity
 	 * @return Object an identifier or actual value
 	 * @throws HibernateException
 	 * @throws SQLException
 	 */
 	public Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException;
 
 	/**
 	 * Map identifiers to entities or collections. This is the second phase of 2-phase property 
 	 * initialization.
 	 * 
 	 * @see Type#hydrate(ResultSet, String[], SessionImplementor, Object)
 	 * @param value an identifier or value returned by <tt>hydrate()</tt>
 	 * @param owner the parent entity
 	 * @param session the session
 	 * @return the given value, or the value associated with the identifier
 	 * @throws HibernateException
 	 */
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException;
 	
 	/**
 	 * Given a hydrated, but unresolved value, return a value that may be used to
 	 * reconstruct property-ref associations.
 	 */
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException;
 	
 	/**
 	 * Get the type of a semi-resolved value.
 	 */
 	public Type getSemiResolvedType(SessionFactoryImplementor factory);
 
 	/**
 	 * During merge, replace the existing (target) value in the entity we are merging to
 	 * with a new (original) value from the detached entity we are merging. For immutable
 	 * objects, or null values, it is safe to simply return the first parameter. For
 	 * mutable objects, it is safe to return a copy of the first parameter. For objects
 	 * with component values, it might make sense to recursively replace component values.
 	 *
 	 * @param original the value from the detached entity being merged
 	 * @param target the value in the managed entity
 	 * @return the value to be merged
 	 */
 	public Object replace(
 			Object original, 
 			Object target, 
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache)
 	throws HibernateException;
 	
 	/**
 	 * During merge, replace the existing (target) value in the entity we are merging to
 	 * with a new (original) value from the detached entity we are merging. For immutable
 	 * objects, or null values, it is safe to simply return the first parameter. For
 	 * mutable objects, it is safe to return a copy of the first parameter. For objects
 	 * with component values, it might make sense to recursively replace component values.
 	 *
 	 * @param original the value from the detached entity being merged
 	 * @param target the value in the managed entity
 	 * @return the value to be merged
 	 */
 	public Object replace(
 			Object original, 
 			Object target, 
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache, 
 			ForeignKeyDirection foreignKeyDirection)
 	throws HibernateException;
 	
 	/**
 	 * Given an instance of the type, return an array of boolean, indicating
 	 * which mapped columns would be null.
 	 * 
 	 * @param value an instance of the type
 	 */
 	public boolean[] toColumnNullness(Object value, Mapping mapping);
 	
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java
index ce69c43e3e..92a6c8e452 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java
@@ -1,46 +1,48 @@
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
 package org.hibernate.type.descriptor.sql;
+
 import java.io.Serializable;
+
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for the <tt>SQL</tt>/<tt>JDBC</tt> side of a value mapping.
  *
  * @author Steve Ebersole
  */
 public interface SqlTypeDescriptor extends Serializable {
 	/**
 	 * Return the {@linkplain java.sql.Types JDBC type-code} for the column mapped by this type.
 	 *
 	 * @return The JDBC type-code
 	 */
 	public int getSqlType();
 
 	public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor);
 
 	public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/usertype/Sized.java b/hibernate-core/src/main/java/org/hibernate/usertype/Sized.java
new file mode 100644
index 0000000000..3f3e63080f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/usertype/Sized.java
@@ -0,0 +1,58 @@
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
+package org.hibernate.usertype;
+
+import org.hibernate.metamodel.relational.Size;
+
+/**
+ * Extends dictated/default column size declarations from {@link org.hibernate.type.Type} to the {@link UserType}
+ * hierarchy as well via an optional interface.
+ *
+ * @author Steve Ebersole
+ */
+public interface Sized {
+	/**
+	 * Return the column sizes dictated by this type.  For example, the mapping for a {@code char}/{@link Character} would
+	 * have a dictated length limit of 1; for a string-based {@link java.util.UUID} would have a size limit of 36; etc.
+	 *
+	 * @todo Would be much much better to have this aware of Dialect once the service/metamodel split is done
+	 *
+	 * @return The dictated sizes.
+	 *
+	 * @see org.hibernate.type.Type#dictatedSizes
+	 */
+	public Size[] dictatedSizes();
+
+	/**
+	 * Defines the column sizes to use according to this type if the user did not explicitly say (and if no
+	 * {@link #dictatedSizes} were given).
+	 *
+	 * @todo Would be much much better to have this aware of Dialect once the service/metamodel split is done
+	 *
+	 * @return The default sizes.
+	 *
+	 * @see org.hibernate.type.Type#defaultSizes
+	 */
+	public Size[] defaultSizes();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/usertype/UserType.java b/hibernate-core/src/main/java/org/hibernate/usertype/UserType.java
index c3f3b627e2..7ad95b1ceb 100644
--- a/hibernate-core/src/main/java/org/hibernate/usertype/UserType.java
+++ b/hibernate-core/src/main/java/org/hibernate/usertype/UserType.java
@@ -1,185 +1,188 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.usertype;
+
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
+
 import org.hibernate.HibernateException;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.metamodel.relational.Size;
 
 /**
  * This interface should be implemented by user-defined "types".
  * A "type" class is <em>not</em> the actual property type - it
  * is a class that knows how to serialize instances of another
  * class to and from JDBC.<br>
  * <br>
  * This interface
  * <ul>
  * <li>abstracts user code from future changes to the <tt>Type</tt>
  * interface,</li>
  * <li>simplifies the implementation of custom types and</li>
  * <li>hides certain "internal" interfaces from user code.</li>
  * </ul>
  * <br>
  * Implementors must be immutable and must declare a public
  * default constructor.<br>
  * <br>
  * The actual class mapped by a <tt>UserType</tt> may be just
  * about anything.<br>
  * <br>
  * <tt>CompositeUserType</tt> provides an extended version of
  * this interface that is useful for more complex cases.<br>
  * <br>
  * Alternatively, custom types could implement <tt>Type</tt>
  * directly or extend one of the abstract classes in
  * <tt>org.hibernate.type</tt>. This approach risks future
  * incompatible changes to classes or interfaces in that
  * package.
  *
  * @see CompositeUserType for more complex cases
  * @see org.hibernate.type.Type
  * @author Gavin King
  */
 public interface UserType {
 
 	/**
 	 * Return the SQL type codes for the columns mapped by this type. The
 	 * codes are defined on <tt>java.sql.Types</tt>.
 	 * @see java.sql.Types
 	 * @return int[] the typecodes
 	 */
 	public int[] sqlTypes();
 
 	/**
 	 * The class returned by <tt>nullSafeGet()</tt>.
 	 *
 	 * @return Class
 	 */
 	public Class returnedClass();
 
 	/**
 	 * Compare two instances of the class mapped by this type for persistence "equality".
 	 * Equality of the persistent state.
 	 *
 	 * @param x
 	 * @param y
 	 * @return boolean
 	 */
 	public boolean equals(Object x, Object y) throws HibernateException;
 
 	/**
 	 * Get a hashcode for the instance, consistent with persistence "equality"
 	 */
 	public int hashCode(Object x) throws HibernateException;
 
 	/**
 	 * Retrieve an instance of the mapped class from a JDBC resultset. Implementors
 	 * should handle possibility of null values.
 	 *
 	 *
 	 * @param rs a JDBC result set
 	 * @param names the column names
 	 * @param session
 	 *@param owner the containing entity  @return Object
 	 * @throws HibernateException
 	 * @throws SQLException
 	 */
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException;
 
 	/**
 	 * Write an instance of the mapped class to a prepared statement. Implementors
 	 * should handle possibility of null values. A multi-column type should be written
 	 * to parameters starting from <tt>index</tt>.
 	 *
 	 *
 	 * @param st a JDBC prepared statement
 	 * @param value the object to write
 	 * @param index statement parameter index
 	 * @param session
 	 * @throws HibernateException
 	 * @throws SQLException
 	 */
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException;
 
 	/**
 	 * Return a deep copy of the persistent state, stopping at entities and at
 	 * collections. It is not necessary to copy immutable objects, or null
 	 * values, in which case it is safe to simply return the argument.
 	 *
 	 * @param value the object to be cloned, which may be null
 	 * @return Object a copy
 	 */
 	public Object deepCopy(Object value) throws HibernateException;
 
 	/**
 	 * Are objects of this type mutable?
 	 *
 	 * @return boolean
 	 */
 	public boolean isMutable();
 
 	/**
 	 * Transform the object into its cacheable representation. At the very least this
 	 * method should perform a deep copy if the type is mutable. That may not be enough
 	 * for some implementations, however; for example, associations must be cached as
 	 * identifier values. (optional operation)
 	 *
 	 * @param value the object to be cached
 	 * @return a cachable representation of the object
 	 * @throws HibernateException
 	 */
 	public Serializable disassemble(Object value) throws HibernateException;
 
 	/**
 	 * Reconstruct an object from the cacheable representation. At the very least this
 	 * method should perform a deep copy if the type is mutable. (optional operation)
 	 *
 	 * @param cached the object to be cached
 	 * @param owner the owner of the cached object
 	 * @return a reconstructed object from the cachable representation
 	 * @throws HibernateException
 	 */
 	public Object assemble(Serializable cached, Object owner) throws HibernateException;
 
 	/**
 	 * During merge, replace the existing (target) value in the entity we are merging to
 	 * with a new (original) value from the detached entity we are merging. For immutable
 	 * objects, or null values, it is safe to simply return the first parameter. For
 	 * mutable objects, it is safe to return a copy of the first parameter. For objects
 	 * with component values, it might make sense to recursively replace component values.
 	 *
 	 * @param original the value from the detached entity being merged
 	 * @param target the value in the managed entity
 	 * @return the value to be merged
 	 */
 	public Object replace(Object original, Object target, Object owner) throws HibernateException;
+
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
index aaa7a09d2d..ca0f2f5211 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
@@ -1,72 +1,73 @@
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
 
 import java.sql.Types;
 
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.Schema;
+import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.relational.Table;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertSame;
 
 /**
  * Basic binding "smoke" tests
  *
  * @author Steve Ebersole
  */
 public class SimpleValueBindingTests extends BaseUnitTestCase {
 	public static final Datatype BIGINT = new Datatype( Types.BIGINT, "BIGINT", Long.class );
 	public static final Datatype VARCHAR = new Datatype( Types.VARCHAR, "VARCHAR", String.class );
 
 	@Test
 	public void testBasicMiddleOutBuilding() {
 		Table table = new Table( new Schema( null, null ), "the_table" );
 		Entity entity = new Entity( "TheEntity", null );
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setEntity( entity );
 		entityBinding.setBaseTable( table );
 
 		SingularAttribute idAttribute = entity.getOrCreateSingularAttribute( "id" );
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleAttributeBinding( "id" );
 		attributeBinding.getHibernateTypeDescriptor().setTypeName( "long" );
 		assertSame( idAttribute, attributeBinding.getAttribute() );
 
 		entityBinding.getEntityIdentifier().setValueBinding( attributeBinding );
 
 		Column idColumn = table.createColumn( "id" );
 		idColumn.setDatatype( BIGINT );
-		idColumn.setSize( Column.Size.precision( 18, 0 ) );
+		idColumn.setSize( Size.precision( 18, 0 ) );
 		table.getPrimaryKey().addColumn( idColumn );
 		table.getPrimaryKey().setName( "my_table_pk" );
 		attributeBinding.setValue( idColumn );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/relational/TableManipulationTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/relational/TableManipulationTests.java
index 5ba8957399..d2b7d44175 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/relational/TableManipulationTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/relational/TableManipulationTests.java
@@ -1,116 +1,116 @@
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
 
 import java.sql.Types;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Steve Ebersole
  */
 public class TableManipulationTests extends BaseUnitTestCase {
 	public static final Datatype VARCHAR = new Datatype( Types.VARCHAR, "VARCHAR", String.class );
 	public static final Datatype INTEGER = new Datatype( Types.INTEGER, "INTEGER", Long.class );
 
 	@Test
 	public void testTableCreation() {
 		Schema schema = new Schema( null, null );
 		Table table = schema.createTable( Identifier.toIdentifier( "my_table" ) );
 		assertNull( table.getSchema().getName().getSchema() );
 		assertNull( table.getSchema().getName().getCatalog() );
 		assertEquals( "my_table", table.getTableName().toString() );
 		assertEquals( "my_table", table.getExportIdentifier() );
 		assertNull( table.getPrimaryKey().getName() );
 		assertFalse( table.values().iterator().hasNext() );
 
 		Column idColumn = table.createColumn( "id" );
 		idColumn.setDatatype( INTEGER );
-		idColumn.setSize( Column.Size.precision( 18, 0 ) );
+		idColumn.setSize( Size.precision( 18, 0 ) );
 		table.getPrimaryKey().addColumn( idColumn );
 		table.getPrimaryKey().setName( "my_table_pk" );
 		assertEquals( "my_table_pk", table.getPrimaryKey().getName() );
 		assertEquals( "my_table.PK", table.getPrimaryKey().getExportIdentifier() );
 
 		Column col_1 = table.createColumn( "col_1" );
 		col_1.setDatatype( VARCHAR );
-		col_1.setSize( Column.Size.length( 512 ) );
+		col_1.setSize( Size.length( 512 ) );
 
 		for ( Value value : table.values() ) {
 			assertTrue( Column.class.isInstance( value ) );
 			Column column = ( Column ) value;
 			if ( column.getName().equals( "id" ) ) {
 				assertEquals( INTEGER, column.getDatatype() );
 				assertEquals( 18, column.getSize().getPrecision() );
 				assertEquals( 0, column.getSize().getScale() );
 				assertEquals( -1, column.getSize().getLength() );
 				assertNull( column.getSize().getLobMultiplier() );
 			}
 			else {
 				assertEquals( "col_1", column.getName() );
 				assertEquals( VARCHAR, column.getDatatype() );
 				assertEquals( -1, column.getSize().getPrecision() );
 				assertEquals( -1, column.getSize().getScale() );
 				assertEquals( 512, column.getSize().getLength() );
 				assertNull( column.getSize().getLobMultiplier() );
 			}
 		}
 	}
 
 	@Test
 	public void testBasicForeignKeyDefinition() {
 		Schema schema = new Schema( null, null );
 		Table book = schema.createTable( Identifier.toIdentifier( "BOOK" ) );
 
 		Column bookId = book.createColumn( "id" );
 		bookId.setDatatype( INTEGER );
-		bookId.setSize( Column.Size.precision( 18, 0 ) );
+		bookId.setSize( Size.precision( 18, 0 ) );
 		book.getPrimaryKey().addColumn( bookId );
 		book.getPrimaryKey().setName( "BOOK_PK" );
 
 		Table page = schema.createTable( Identifier.toIdentifier( "PAGE" ) );
 
 		Column pageId = page.createColumn( "id" );
 		pageId.setDatatype( INTEGER );
-		pageId.setSize( Column.Size.precision( 18, 0 ) );
+		pageId.setSize( Size.precision( 18, 0 ) );
 		page.getPrimaryKey().addColumn( pageId );
 		page.getPrimaryKey().setName( "PAGE_PK" );
 
 		Column pageBookId = page.createColumn( "BOOK_ID" );
 		pageId.setDatatype( INTEGER );
-		pageId.setSize( Column.Size.precision( 18, 0 ) );
+		pageId.setSize( Size.precision( 18, 0 ) );
 		ForeignKey pageBookFk = page.createForeignKey( book, "PAGE_BOOK_FK" );
 		pageBookFk.addColumn( pageBookId );
 
 		assertEquals( page, pageBookFk.getSourceTable() );
 		assertEquals( book, pageBookFk.getTargetTable() );
 	}
 }
