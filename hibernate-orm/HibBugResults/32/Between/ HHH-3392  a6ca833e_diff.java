diff --git a/core/src/main/java/org/hibernate/cache/QueryKey.java b/core/src/main/java/org/hibernate/cache/QueryKey.java
index 30643e1b79..8175aebf61 100644
--- a/core/src/main/java/org/hibernate/cache/QueryKey.java
+++ b/core/src/main/java/org/hibernate/cache/QueryKey.java
@@ -1,141 +1,152 @@
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
 package org.hibernate.cache;
 
 import java.io.Serializable;
+import java.io.IOException;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 import org.hibernate.util.EqualsHelper;
 
 /**
  * A key that identifies a particular query with bound parameter values
  * @author Gavin King
  */
 public class QueryKey implements Serializable {
 	private final String sqlQueryString;
 	private final Type[] types;
 	private final Object[] values;
 	private final Integer firstRow;
 	private final Integer maxRows;
 	private final Map namedParameters;
 	private final EntityMode entityMode;
 	private final Set filters;
-	private final int hashCode;
 	
 	// the user provided resulttransformer, not the one used with "select new". Here to avoid mangling transformed/non-transformed results.
 	private final ResultTransformer customTransformer;
-	
+
+	/**
+	 * For performance reasons, the hashCode is cached; however, it is marked transient so that it can be
+	 * recalculated as part of the serialization process which allows distributed query caches to work properly.
+	 */
+	private transient int hashCode;
+
 	public QueryKey(String queryString, QueryParameters queryParameters, Set filters, EntityMode entityMode) {
 		this.sqlQueryString = queryString;
 		this.types = queryParameters.getPositionalParameterTypes();
 		this.values = queryParameters.getPositionalParameterValues();
 		RowSelection selection = queryParameters.getRowSelection();
 		if (selection!=null) {
 			firstRow = selection.getFirstRow();
 			maxRows = selection.getMaxRows();
 		}
 		else {
 			firstRow = null;
 			maxRows = null;
 		}
 		this.namedParameters = queryParameters.getNamedParameters();
 		this.entityMode = entityMode;
 		this.filters = filters;
 		this.customTransformer = queryParameters.getResultTransformer();
-		this.hashCode = getHashCode();
+		this.hashCode = generateHashCode();
+	}
+
+	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
+		in.defaultReadObject();
+		this.hashCode = generateHashCode();
+	}
+
+	private int generateHashCode() {
+		int result = 13;
+		result = 37 * result + ( firstRow==null ? 0 : firstRow.hashCode() );
+		result = 37 * result + ( maxRows==null ? 0 : maxRows.hashCode() );
+		for ( int i=0; i<values.length; i++ ) {
+			result = 37 * result + ( values[i]==null ? 0 : types[i].getHashCode( values[i], entityMode ) );
+		}
+		result = 37 * result + ( namedParameters==null ? 0 : namedParameters.hashCode() );
+		result = 37 * result + ( filters==null ? 0 : filters.hashCode() );
+		result = 37 * result + ( customTransformer==null ? 0 : customTransformer.hashCode() );
+		result = 37 * result + sqlQueryString.hashCode();
+		return result;
 	}
 	
 	public boolean equals(Object other) {
 		if (!(other instanceof QueryKey)) return false;
 		QueryKey that = (QueryKey) other;
 		if ( !sqlQueryString.equals(that.sqlQueryString) ) return false;
 		if ( !EqualsHelper.equals(firstRow, that.firstRow) || !EqualsHelper.equals(maxRows, that.maxRows) ) return false;
 		if ( !EqualsHelper.equals(customTransformer, that.customTransformer) ) return false;
 		if (types==null) {
 			if (that.types!=null) return false;
 		}
 		else {
 			if (that.types==null) return false;
 			if ( types.length!=that.types.length ) return false;
 			for ( int i=0; i<types.length; i++ ) {
 				if ( types[i].getReturnedClass() != that.types[i].getReturnedClass() ) return false;
 				if ( !types[i].isEqual( values[i], that.values[i], entityMode ) ) return false;
 			}
 		}
 		if ( !EqualsHelper.equals(filters, that.filters) ) return false;
 		if ( !EqualsHelper.equals(namedParameters, that.namedParameters) ) return false;
 		return true;
 	}
 	
 	public int hashCode() {
 		return hashCode;
 	}
-	
-	private int getHashCode() {
-		int result = 13;
-		result = 37 * result + ( firstRow==null ? 0 : firstRow.hashCode() );
-		result = 37 * result + ( maxRows==null ? 0 : maxRows.hashCode() );
-		for ( int i=0; i<values.length; i++ ) {
-			result = 37 * result + ( values[i]==null ? 0 : types[i].getHashCode( values[i], entityMode ) );
-		}
-		result = 37 * result + ( namedParameters==null ? 0 : namedParameters.hashCode() );
-		result = 37 * result + ( filters==null ? 0 : filters.hashCode() );
-		result = 37 * result + ( customTransformer==null ? 0 : customTransformer.hashCode() );
-		result = 37 * result + sqlQueryString.hashCode();
-		return result;
-	}
 
 	public String toString() {
 		StringBuffer buf = new StringBuffer()
 			.append("sql: ")
 			.append(sqlQueryString);
 		if (values!=null) {
 			buf.append("; parameters: ");
 			for (int i=0; i<values.length; i++) {
 				buf.append( values[i] )
 					.append(", ");
 			}
 		}
 		if (namedParameters!=null) {
 			buf.append("; named parameters: ")
 				.append(namedParameters);
 		}
 		if (filters!=null) {
 			buf.append("; filters: ")
 				.append(filters);
 		}
 		if (firstRow!=null) buf.append("; first row: ").append(firstRow);
 		if (maxRows!=null) buf.append("; max rows: ").append(maxRows);
 		if (customTransformer!=null) buf.append("; transformer: ").append(customTransformer);
 		return buf.toString();
 	}
 	
 }
diff --git a/core/src/main/java/org/hibernate/criterion/CriteriaSpecification.java b/core/src/main/java/org/hibernate/criterion/CriteriaSpecification.java
index 90b20a42ad..43872cd2ce 100755
--- a/core/src/main/java/org/hibernate/criterion/CriteriaSpecification.java
+++ b/core/src/main/java/org/hibernate/criterion/CriteriaSpecification.java
@@ -1,78 +1,78 @@
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
 package org.hibernate.criterion;
 
 import org.hibernate.transform.AliasToEntityMapResultTransformer;
 import org.hibernate.transform.DistinctRootEntityResultTransformer;
 import org.hibernate.transform.PassThroughResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.transform.RootEntityResultTransformer;
 
 /**
  * @author Gavin King
  */
 public interface CriteriaSpecification {
 
 	/**
 	 * The alias that refers to the "root" entity of the criteria query.
 	 */
 	public static final String ROOT_ALIAS = "this";
 
 	/**
 	 * Each row of results is a <tt>Map</tt> from alias to entity instance
 	 */
-	public static final ResultTransformer ALIAS_TO_ENTITY_MAP = new AliasToEntityMapResultTransformer();
+	public static final ResultTransformer ALIAS_TO_ENTITY_MAP = AliasToEntityMapResultTransformer.INSTANCE;
 
 	/**
 	 * Each row of results is an instance of the root entity
 	 */
-	public static final ResultTransformer ROOT_ENTITY = new RootEntityResultTransformer();
+	public static final ResultTransformer ROOT_ENTITY = RootEntityResultTransformer.INSTANCE;
 
 	/**
 	 * Each row of results is a distinct instance of the root entity
 	 */
-	public static final ResultTransformer DISTINCT_ROOT_ENTITY = new DistinctRootEntityResultTransformer();
+	public static final ResultTransformer DISTINCT_ROOT_ENTITY = DistinctRootEntityResultTransformer.INSTANCE;
 
 	/**
 	 * This result transformer is selected implicitly by calling <tt>setProjection()</tt>
 	 */
-	public static final ResultTransformer PROJECTION = new PassThroughResultTransformer();
+	public static final ResultTransformer PROJECTION = PassThroughResultTransformer.INSTANCE;
 
 	/**
 	 * Specifies joining to an entity based on an inner join.
 	 */
 	public static final int INNER_JOIN = org.hibernate.sql.JoinFragment.INNER_JOIN;
 
 	/**
 	 * Specifies joining to an entity based on a full join.
 	 */
 	public static final int FULL_JOIN = org.hibernate.sql.JoinFragment.FULL_JOIN;
 
 	/**
 	 * Specifies joining to an entity based on a left outer join.
 	 */
 	public static final int LEFT_JOIN = org.hibernate.sql.JoinFragment.LEFT_OUTER_JOIN;
 	
 }
diff --git a/core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java b/core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java
index 0301e4d34a..e6adf665ec 100644
--- a/core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java
+++ b/core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java
@@ -1,119 +1,119 @@
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
 
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.property.ChainedPropertyAccessor;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.property.PropertyAccessorFactory;
 import org.hibernate.property.Setter;
 
 /**
  * Result transformer that allows to transform a result to
  * a user specified class which will be populated via setter
  * methods or fields matching the alias names.
  * <p/>
  * <pre>
  * List resultWithAliasedBean = s.createCriteria(Enrolment.class)
  * 			.createAlias("student", "st")
  * 			.createAlias("course", "co")
  * 			.setProjection( Projections.projectionList()
  * 					.add( Projections.property("co.description"), "courseDescription" )
  * 			)
  * 			.setResultTransformer( new AliasToBeanResultTransformer(StudentDTO.class) )
  * 			.list();
  * <p/>
  *  StudentDTO dto = (StudentDTO)resultWithAliasedBean.get(0);
  * 	</pre>
  *
  * @author max
  */
 public class AliasToBeanResultTransformer implements ResultTransformer {
 
 	// IMPL NOTE : due to the delayed population of setters (setters cached
-	// 		for performance), we really cannot pro0perly define equality for
+	// 		for performance), we really cannot properly define equality for
 	// 		this transformer
 
 	private final Class resultClass;
 	private final PropertyAccessor propertyAccessor;
 	private Setter[] setters;
 
 	public AliasToBeanResultTransformer(Class resultClass) {
 		if ( resultClass == null ) {
 			throw new IllegalArgumentException( "resultClass cannot be null" );
 		}
 		this.resultClass = resultClass;
 		propertyAccessor = new ChainedPropertyAccessor(
 				new PropertyAccessor[] {
 						PropertyAccessorFactory.getPropertyAccessor( resultClass, null ),
 						PropertyAccessorFactory.getPropertyAccessor( "field" )
 				}
 		);
 	}
 
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		Object result;
 
 		try {
 			if ( setters == null ) {
 				setters = new Setter[aliases.length];
 				for ( int i = 0; i < aliases.length; i++ ) {
 					String alias = aliases[i];
 					if ( alias != null ) {
 						setters[i] = propertyAccessor.getSetter( resultClass, alias );
 					}
 				}
 			}
 			result = resultClass.newInstance();
 
 			for ( int i = 0; i < aliases.length; i++ ) {
 				if ( setters[i] != null ) {
 					setters[i].set( result, tuple[i], null );
 				}
 			}
 		}
 		catch ( InstantiationException e ) {
 			throw new HibernateException( "Could not instantiate resultclass: " + resultClass.getName() );
 		}
 		catch ( IllegalAccessException e ) {
 			throw new HibernateException( "Could not instantiate resultclass: " + resultClass.getName() );
 		}
 
 		return result;
 	}
 
 	public List transformList(List collection) {
 		return collection;
 	}
 
 	public int hashCode() {
 		int result;
 		result = resultClass.hashCode();
 		result = 31 * result + propertyAccessor.hashCode();
 		return result;
 	}
 }
diff --git a/core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java b/core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
index 2d23710f5e..3c4babff00 100644
--- a/core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
+++ b/core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
@@ -1,99 +1,73 @@
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
 
 import java.util.HashMap;
 import java.util.Map;
 import java.io.Serializable;
 
 /**
  * {@link ResultTransformer} implementation which builds a map for each "row",
  * made up  of each aliased value where the alias is the map key.
  * <p/>
  * Since this transformer is stateless, all instances would be considered equal.
  * So for optimization purposes we limit it to a single, singleton {@link #INSTANCE instance}.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class AliasToEntityMapResultTransformer extends BasicTransformerAdapter implements Serializable {
 
 	public static final AliasToEntityMapResultTransformer INSTANCE = new AliasToEntityMapResultTransformer();
 
 	/**
-	 * Instantiate AliasToEntityMapResultTransformer.
-	 *
-	 * @deprecated Use the {@link #INSTANCE} reference instead of explicitly creating a new one.
+	 * Disallow instantiation of AliasToEntityMapResultTransformer.
 	 */
-	public AliasToEntityMapResultTransformer() {
-		// todo : make private
+	private AliasToEntityMapResultTransformer() {
 	}
 
+	/**
+	 * {@inheritDoc}
+	 */
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		Map result = new HashMap(tuple.length);
 		for ( int i=0; i<tuple.length; i++ ) {
 			String alias = aliases[i];
 			if ( alias!=null ) {
 				result.put( alias, tuple[i] );
 			}
 		}
 		return result;
 	}
 
 	/**
 	 * Serialization hook for ensuring singleton uniqueing.
 	 *
 	 * @return The singleton instance : {@link #INSTANCE}
 	 */
 	private Object readResolve() {
 		return INSTANCE;
 	}
-
-
-	// all AliasToEntityMapResultTransformer are considered equal ~~~~~~~~~~~~~
-
-	/**
-	 * All AliasToEntityMapResultTransformer are considered equal
-	 *
-	 * @param other The other instance to check for equality
-	 * @return True if (non-null) other is a instance of
-	 * AliasToEntityMapResultTransformer.
-	 */
-	public boolean equals(Object other) {
-		// todo : we can remove this once the deprecated ctor can be made private...
-		return other != null && AliasToEntityMapResultTransformer.class.isInstance( other );
-	}
-
-	/**
-	 * All AliasToEntityMapResultTransformer are considered equal
-	 *
-	 * @return We simply return the hashCode of the
-	 * AliasToEntityMapResultTransformer class name string.
-	 */
-	public int hashCode() {
-		// todo : we can remove this once the deprecated ctor can be made private...
-		return getClass().getName().hashCode();
-	}
 }
diff --git a/core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java b/core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
index 17da40eb2a..656b39b0ff 100644
--- a/core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
+++ b/core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
@@ -1,86 +1,80 @@
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
 
 import java.util.List;
 import java.io.Serializable;
 
 /**
  * Much like {@link RootEntityResultTransformer}, but we also distinct
  * the entity in the final result.
  * <p/>
  * Since this transformer is stateless, all instances would be considered equal.
  * So for optimization purposes we limit it to a single, singleton {@link #INSTANCE instance}.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class DistinctRootEntityResultTransformer implements ResultTransformer, Serializable {
 
 	public static final DistinctRootEntityResultTransformer INSTANCE = new DistinctRootEntityResultTransformer();
 
 	/**
-	 * Instantiate a DistinctRootEntityResultTransformer.
-	 *
-	 * @deprecated Use the {@link #INSTANCE} reference instead of explicitly creating a new one.
+	 * Disallow instantiation of DistinctRootEntityResultTransformer.
 	 */
-	public DistinctRootEntityResultTransformer() {
+	private DistinctRootEntityResultTransformer() {
 	}
 
 	/**
 	 * Simply delegates to {@link RootEntityResultTransformer#transformTuple}.
 	 *
 	 * @param tuple The tuple to transform
 	 * @param aliases The tuple aliases
 	 * @return The transformed tuple row.
 	 */
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		return RootEntityResultTransformer.INSTANCE.transformTuple( tuple, aliases );
 	}
 
 	/**
 	 * Simply delegates to {@link DistinctResultTransformer#transformList}.
 	 *
 	 * @param list The list to transform.
 	 * @return The transformed List.
 	 */
 	public List transformList(List list) {
 		return DistinctResultTransformer.INSTANCE.transformList( list );
 	}
 
 	/**
 	 * Serialization hook for ensuring singleton uniqueing.
 	 *
 	 * @return The singleton instance : {@link #INSTANCE}
 	 */
 	private Object readResolve() {
 		return INSTANCE;
 	}
 
-	public boolean equals(Object obj) {
-		// todo : we can remove this once the deprecated ctor can be made private...
-		return DistinctRootEntityResultTransformer.class.isInstance( obj );
-	}
 }
diff --git a/core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java b/core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
index bcc536e0d0..44cb9c41ee 100644
--- a/core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
+++ b/core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
@@ -1,64 +1,60 @@
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
 
 import java.io.Serializable;
 
 /**
  * ???
  *
  * @author max
  */
 public class PassThroughResultTransformer extends BasicTransformerAdapter implements Serializable {
 
 	public static final PassThroughResultTransformer INSTANCE = new PassThroughResultTransformer();
 
 	/**
-	 * Instamtiate a PassThroughResultTransformer.
-	 *
-	 * @deprecated Use the {@link #INSTANCE} reference instead of explicitly creating a new one.
+	 * Disallow instantiation of PassThroughResultTransformer.
 	 */
-	public PassThroughResultTransformer() {
+	private PassThroughResultTransformer() {
 	}
 
+	/**
+	 * {@inheritDoc}
+	 */
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		return tuple.length==1 ? tuple[0] : tuple;
 	}
 
 	/**
 	 * Serialization hook for ensuring singleton uniqueing.
 	 *
 	 * @return The singleton instance : {@link #INSTANCE}
 	 */
 	private Object readResolve() {
 		return INSTANCE;
 	}
 
-	public boolean equals(Object obj) {
-		// todo : we can remove this once the deprecated ctor can be made private...
-		return PassThroughResultTransformer.class.isInstance( obj );
-	}
-
 }
diff --git a/core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java b/core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
index 8034985f8a..66a982951f 100644
--- a/core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+++ b/core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
@@ -1,72 +1,64 @@
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
 
-import java.util.List;
 import java.io.Serializable;
 
 /**
  * {@link ResultTransformer} implementation which limits the result tuple
  * to only the "root entity".
  * <p/>
  * Since this transformer is stateless, all instances would be considered equal.
  * So for optimization purposes we limit it to a single, singleton {@link #INSTANCE instance}.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class RootEntityResultTransformer extends BasicTransformerAdapter implements Serializable {
 
 	public static final RootEntityResultTransformer INSTANCE = new RootEntityResultTransformer();
 
 	/**
-	 * Instantiate RootEntityResultTransformer.
-	 *
-	 * @deprecated Use the {@link #INSTANCE} reference instead of explicitly creating a new one.
+	 * Disallow instantiation of RootEntityResultTransformer.
 	 */
-	public RootEntityResultTransformer() {
+	private RootEntityResultTransformer() {
 	}
 
 	/**
 	 * Return just the root entity from the row tuple.
 	 */
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		return tuple[ tuple.length-1 ];
 	}
 
 	/**
 	 * Serialization hook for ensuring singleton uniqueing.
 	 *
 	 * @return The singleton instance : {@link #INSTANCE}
 	 */
 	private Object readResolve() {
 		return INSTANCE;
 	}
-
-	public boolean equals(Object obj) {
-		// todo : we can remove this once the deprecated ctor can be made private...
-		return RootEntityResultTransformer.class.isInstance( obj );
-	}
 }
diff --git a/core/src/test/java/org/hibernate/cache/QueryKeyTest.java b/core/src/test/java/org/hibernate/cache/QueryKeyTest.java
new file mode 100644
index 0000000000..d9c6b3d368
--- /dev/null
+++ b/core/src/test/java/org/hibernate/cache/QueryKeyTest.java
@@ -0,0 +1,96 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.cache;
+
+import java.util.Collections;
+import java.util.HashMap;
+
+import junit.framework.TestCase;
+
+import org.hibernate.engine.QueryParameters;
+import org.hibernate.EntityMode;
+import org.hibernate.transform.RootEntityResultTransformer;
+import org.hibernate.transform.ResultTransformer;
+import org.hibernate.transform.DistinctRootEntityResultTransformer;
+import org.hibernate.transform.AliasToEntityMapResultTransformer;
+import org.hibernate.transform.PassThroughResultTransformer;
+import org.hibernate.transform.DistinctResultTransformer;
+import org.hibernate.util.SerializationHelper;
+import org.hibernate.util.ArrayHelper;
+
+/**
+ * Tests relating to {@link QueryKey} instances.
+ *
+ * @author Steve Ebersole
+ */
+public class QueryKeyTest extends TestCase {
+	private static final String QUERY_STRING = "the query string";
+
+	public void testSerializedEquality() {
+		doTest( buildBasicKey( new QueryParameters() ) );
+	}
+
+	public void testSerializedEqualityWithResultTransformer() {
+		doTest( buildBasicKey( buildQueryParameters( RootEntityResultTransformer.INSTANCE ) ) );
+		doTest( buildBasicKey( buildQueryParameters( DistinctRootEntityResultTransformer.INSTANCE ) ) );
+		doTest( buildBasicKey( buildQueryParameters( DistinctResultTransformer.INSTANCE ) ) );
+		doTest( buildBasicKey( buildQueryParameters( AliasToEntityMapResultTransformer.INSTANCE ) ) );
+		doTest( buildBasicKey( buildQueryParameters( PassThroughResultTransformer.INSTANCE ) ) );
+	}
+
+	private QueryParameters buildQueryParameters(ResultTransformer resultTransformer) {
+		return new QueryParameters(
+				ArrayHelper.EMPTY_TYPE_ARRAY, 		// param types
+				ArrayHelper.EMPTY_OBJECT_ARRAY,		// param values
+				Collections.EMPTY_MAP,				// lock modes
+				null,								// row selection
+				false,								// cacheable?
+				"",									// cache region
+				"", 								// SQL comment
+				false,								// is natural key lookup?
+				resultTransformer					// the result transformer, duh! ;)
+		);
+	}
+
+	private QueryKey buildBasicKey(QueryParameters queryParameters) {
+		return new QueryKey( QUERY_STRING, queryParameters, Collections.EMPTY_SET, EntityMode.POJO );
+	}
+
+	private void doTest(QueryKey key) {
+		HashMap map = new HashMap();
+
+		map.put( key, "" );
+		assert map.size() == 1 : "really messed up";
+
+		Object old = map.put( key, "value" );
+		assert old != null && map.size() == 1 : "apparent QueryKey equals/hashCode issue";
+
+		// finally, lets serialize it and see what happens
+		QueryKey key2 = ( QueryKey ) SerializationHelper.clone( key );
+		assert key != key2 : "deep copy issue";
+		old = map.put( key2, "new value" );
+		assert old != null && map.size() == 1 : "deserialization did not set hashCode or equals properly";
+	}
+}
diff --git a/testsuite/src/test/java/org/hibernate/test/sql/hand/query/NativeSQLQueriesTest.java b/testsuite/src/test/java/org/hibernate/test/sql/hand/query/NativeSQLQueriesTest.java
index 1f619d235f..959c86a749 100644
--- a/testsuite/src/test/java/org/hibernate/test/sql/hand/query/NativeSQLQueriesTest.java
+++ b/testsuite/src/test/java/org/hibernate/test/sql/hand/query/NativeSQLQueriesTest.java
@@ -1,620 +1,625 @@
 package org.hibernate.test.sql.hand.query;
 
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
+import java.util.HashMap;
 
 import junit.framework.Test;
 
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.test.sql.hand.Organization;
 import org.hibernate.test.sql.hand.Person;
 import org.hibernate.test.sql.hand.Employment;
 import org.hibernate.test.sql.hand.Product;
 import org.hibernate.test.sql.hand.Order;
 import org.hibernate.test.sql.hand.Dimension;
 import org.hibernate.test.sql.hand.SpaceShip;
 import org.hibernate.test.sql.hand.Speech;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.junit.functional.FunctionalTestCase;
 import org.hibernate.junit.functional.FunctionalTestClassTestSuite;
 import org.hibernate.transform.DistinctRootEntityResultTransformer;
 import org.hibernate.transform.Transformers;
 import org.hibernate.transform.AliasToEntityMapResultTransformer;
+import org.hibernate.transform.BasicTransformerAdapter;
 
 /**
  * Tests of various features of native SQL queries.
  *
  * @author Steve Ebersole
  */
 public class NativeSQLQueriesTest extends FunctionalTestCase {
 
 	public NativeSQLQueriesTest(String x) {
 		super( x );
 	}
 
 	public String[] getMappings() {
 		return new String[] { "sql/hand/query/NativeSQLQueries.hbm.xml" };
 	}
 
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 	}
 
 	public static Test suite() {
 		return new FunctionalTestClassTestSuite( NativeSQLQueriesTest.class );
 	}
 
 	protected String getOrganizationFetchJoinEmploymentSQL() {
 		return "SELECT org.ORGID as {org.id}, " +
 		       "        org.NAME as {org.name}, " +
 		       "        emp.EMPLOYER as {emp.key}, " +
 		       "        emp.EMPID as {emp.element}, " +
 		       "        {emp.element.*}  " +
 		       "FROM ORGANIZATION org " +
 		       "    LEFT OUTER JOIN EMPLOYMENT emp ON org.ORGID = emp.EMPLOYER";
 	}
 
 	protected String getOrganizationJoinEmploymentSQL() {
 		return "SELECT org.ORGID as {org.id}, " +
 		       "        org.NAME as {org.name}, " +
 		       "        {emp.*}  " +
 		       "FROM ORGANIZATION org " +
 		       "    LEFT OUTER JOIN EMPLOYMENT emp ON org.ORGID = emp.EMPLOYER";
 	}
 
 	protected String getEmploymentSQL() {
 		return "SELECT * FROM EMPLOYMENT";
 	}
 
 	protected String getEmploymentSQLMixedScalarEntity() {
 		return "SELECT e.*, e.employer as employerid  FROM EMPLOYMENT e" ;
 	}
 
 	protected String getOrgEmpRegionSQL() {
 		return "select {org.*}, {emp.*}, emp.REGIONCODE " +
 		       "from ORGANIZATION org " +
 		       "     left outer join EMPLOYMENT emp on org.ORGID = emp.EMPLOYER";
 	}
 
 	protected String getOrgEmpPersonSQL() {
 		return "select {org.*}, {emp.*}, {pers.*} " +
 		       "from ORGANIZATION org " +
 		       "    join EMPLOYMENT emp on org.ORGID = emp.EMPLOYER " +
 		       "    join PERSON pers on pers.PERID = emp.EMPLOYEE ";
 	}
 
 	public void testFailOnNoAddEntityOrScalar() {
 		// Note: this passes, but for the wrong reason.
 		//      there is actually an exception thrown, but it is the database
 		//      throwing a sql exception because the SQL gets passed
 		//      "un-processed"...
 		Session s = openSession();
 		s.beginTransaction();
 		try {
 			String sql = "select {org.*} " +
 			             "from organization org";
 			s.createSQLQuery( sql ).list();
 			fail( "Should throw an exception since no addEntity nor addScalar has been performed." );
 		}
 		catch( HibernateException he) {
 			// expected behavior
 		}
 		finally {
 			s.getTransaction().rollback();
 			s.close();
 		}
 	}
 
 	public void testManualSynchronization() {
 		Session s = openSession();
 		s.beginTransaction();
 
 		sfi().getStatistics().clear();
 
 		// create an Organization...
 		Organization jboss = new Organization( "JBoss" );
 		s.persist( jboss );
 
 		// now query on Employment, this should not cause an auto-flush
 		s.createSQLQuery( getEmploymentSQL() ).list();
 		assertEquals( 0, sfi().getStatistics().getEntityInsertCount() );
 
 		// now try to query on Employment but this time add Organization as a synchronized query space...
 		s.createSQLQuery( getEmploymentSQL() ).addSynchronizedEntityClass( Organization.class ).list();
 		assertEquals( 1, sfi().getStatistics().getEntityInsertCount() );
 
 		// clean up
 		s.delete( jboss );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testSQLQueryInterface() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Organization ifa = new Organization("IFA");
 		Organization jboss = new Organization("JBoss");
 		Person gavin = new Person("Gavin");
 		Employment emp = new Employment(gavin, jboss, "AU");
 
 		s.persist(ifa);
 		s.persist(jboss);
 		s.persist(gavin);
 		s.persist(emp);
 
 		List l = s.createSQLQuery( getOrgEmpRegionSQL() )
 				.addEntity("org", Organization.class)
 				.addJoin("emp", "org.employments")
 				.addScalar("regionCode", Hibernate.STRING)
 				.list();
 		assertEquals( 2, l.size() );
 
 		l = s.createSQLQuery( getOrgEmpPersonSQL() )
 				.addEntity("org", Organization.class)
 				.addJoin("emp", "org.employments")
 				.addJoin("pers", "emp.employee")
 				.list();
 		assertEquals( l.size(), 1 );
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		l = s.createSQLQuery( "select {org.*}, {emp.*} " +
 			       "from ORGANIZATION org " +
 			       "     left outer join EMPLOYMENT emp on org.ORGID = emp.EMPLOYER, ORGANIZATION org2" )
 		.addEntity("org", Organization.class)
 		.addJoin("emp", "org.employments")
-		.setResultTransformer(new DistinctRootEntityResultTransformer())
+		.setResultTransformer( DistinctRootEntityResultTransformer.INSTANCE )
 		.list();
 		assertEquals( l.size(), 2 );
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		s.delete(emp);
 		s.delete(gavin);
 		s.delete(ifa);
 		s.delete(jboss);
 
 		t.commit();
 		s.close();
 	}
 
 	public void testResultSetMappingDefinition() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Organization ifa = new Organization("IFA");
 		Organization jboss = new Organization("JBoss");
 		Person gavin = new Person("Gavin");
 		Employment emp = new Employment(gavin, jboss, "AU");
 
 		s.persist(ifa);
 		s.persist(jboss);
 		s.persist(gavin);
 		s.persist(emp);
 
 		List l = s.createSQLQuery( getOrgEmpRegionSQL() )
 				.setResultSetMapping( "org-emp-regionCode" )
 				.list();
 		assertEquals( l.size(), 2 );
 
 		l = s.createSQLQuery( getOrgEmpPersonSQL() )
 				.setResultSetMapping( "org-emp-person" )
 				.list();
 		assertEquals( l.size(), 1 );
 
 		s.delete(emp);
 		s.delete(gavin);
 		s.delete(ifa);
 		s.delete(jboss);
 
 		t.commit();
 		s.close();
 	}
 
 	public void testScalarValues() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Organization ifa = new Organization( "IFA" );
 		Organization jboss = new Organization( "JBoss" );
 
 		Serializable idIfa = s.save( ifa );
 		Serializable idJBoss = s.save( jboss );
 
 		s.flush();
 
 		List result = s.getNamedQuery( "orgNamesOnly" ).list();
 		assertTrue( result.contains( "IFA" ) );
 		assertTrue( result.contains( "JBoss" ) );
 
 		result = s.getNamedQuery( "orgNamesOnly" ).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
 		Map m = (Map) result.get(0);
 		assertEquals( 2, result.size() );
 		assertEquals( 1, m.size() );
 		assertTrue( m.containsKey("NAME") );
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		Iterator iter = s.getNamedQuery( "orgNamesAndOrgs" ).list().iterator();
 		Object[] o = ( Object[] ) iter.next();
 		assertEquals( o[0], "IFA" );
 		assertEquals( ( ( Organization ) o[1] ).getName(), "IFA" );
 		o = ( Object[] ) iter.next();
 		assertEquals( o[0], "JBoss" );
 		assertEquals( ( ( Organization ) o[1] ).getName(), "JBoss" );
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		// test that the ordering of the results is truly based on the order in which they were defined
 		iter = s.getNamedQuery( "orgsAndOrgNames" ).list().iterator();
 		Object[] row = ( Object[] ) iter.next();
 		assertEquals( "expecting non-scalar result first", Organization.class, row[0].getClass() );
 		assertEquals( "expecting scalar result second", String.class, row[1].getClass() );
 		assertEquals( ( ( Organization ) row[0] ).getName(), "IFA" );
 		assertEquals( row[1], "IFA" );
 		row = ( Object[] ) iter.next();
 		assertEquals( "expecting non-scalar result first", Organization.class, row[0].getClass() );
 		assertEquals( "expecting scalar result second", String.class, row[1].getClass() );
 		assertEquals( ( ( Organization ) row[0] ).getName(), "JBoss" );
 		assertEquals( row[1], "JBoss" );
 		assertFalse( iter.hasNext() );
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		iter = s.getNamedQuery( "orgIdsAndOrgNames" ).list().iterator();
 		o = ( Object[] ) iter.next();
 		assertEquals( o[1], "IFA" );
 		assertEquals( o[0], idIfa );
 		o = ( Object[] ) iter.next();
 		assertEquals( o[1], "JBoss" );
 		assertEquals( o[0], idJBoss );
 
 		s.delete( ifa );
 		s.delete( jboss );
 		t.commit();
 		s.close();
 	}
 
 	public void testMappedAliasStrategy() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Organization ifa = new Organization("IFA");
 		Organization jboss = new Organization("JBoss");
 		Person gavin = new Person("Gavin");
 		Employment emp = new Employment(gavin, jboss, "AU");
 		Serializable orgId = s.save(jboss);
 		Serializable orgId2 = s.save(ifa);
 		s.save(gavin);
 		s.save(emp);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Query namedQuery = s.getNamedQuery("AllEmploymentAsMapped");
 		List list = namedQuery.list();
 		assertEquals(1,list.size());
 		Employment emp2 = (Employment) list.get(0);
 		assertEquals(emp2.getEmploymentId(), emp.getEmploymentId() );
 		assertEquals(emp2.getStartDate().getDate(), emp.getStartDate().getDate() );
 		assertEquals(emp2.getEndDate(), emp.getEndDate() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Query sqlQuery = s.getNamedQuery("EmploymentAndPerson");
 		sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
 		list = sqlQuery.list();
 		assertEquals(1,list.size() );
 		Object res = list.get(0);
 		assertClassAssignability(res.getClass(),Map.class);
 		Map m = (Map) res;
 		assertEquals(2,m.size());
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		sqlQuery = s.getNamedQuery("organizationreturnproperty");
 		sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
 		list = sqlQuery.list();
 		assertEquals(2,list.size() );
 		m = (Map) list.get(0);
 		assertTrue(m.containsKey("org"));
 		assertClassAssignability(m.get("org").getClass(), Organization.class);
 		assertTrue(m.containsKey("emp"));
 		assertClassAssignability(m.get("emp").getClass(), Employment.class);
 		assertEquals(2, m.size());
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		namedQuery = s.getNamedQuery("EmploymentAndPerson");
 		list = namedQuery.list();
 		assertEquals(1,list.size() );
 		Object[] objs = (Object[]) list.get(0);
 		assertEquals(2, objs.length);
 		emp2 = (Employment) objs[0];
 		gavin = (Person) objs[1];
 		s.delete(emp2);
 		s.delete(jboss);
 		s.delete(gavin);
 		s.delete(ifa);
 		t.commit();
 		s.close();
 	}
 
 	/* test for native sql composite id joins which has never been implemented */
 	public void testCompositeIdJoinsFailureExpected() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Person person = new Person();
 		person.setName( "Noob" );
 
 		Product product = new Product();
 		product.setProductId( new Product.ProductId() );
 		product.getProductId().setOrgid( "x" );
 		product.getProductId().setProductnumber( "1234" );
 		product.setName( "Hibernate 3" );
 
 		Order order = new Order();
 		order.setOrderId( new Order.OrderId() );
 		order.getOrderId().setOrdernumber( "1" );
 		order.getOrderId().setOrgid( "y" );
 
 		product.getOrders().add( order );
 		order.setProduct( product );
 		order.setPerson( person );
 
 		s.save( product );
 		s.save( order);
 		s.save( person );
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Product p = (Product) s.createQuery( "from Product p join fetch p.orders" ).list().get(0);
 		assertTrue(Hibernate.isInitialized( p.getOrders()));
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Object[] o =  (Object[]) s.createSQLQuery( "select\r\n" +
 				"        product.orgid as {product.id.orgid}," +
 				"        product.productnumber as {product.id.productnumber}," +
 				"        {prod_orders}.orgid as orgid3_1_,\r\n" +
 				"        {prod_orders}.ordernumber as ordernum2_3_1_,\r\n" +
 				"        product.name as {product.name}," +
 				"        {prod_orders.element.*}" +
 				/*"        orders.PROD_NO as PROD4_3_1_,\r\n" +
 				"        orders.person as person3_1_,\r\n" +
 				"        orders.PROD_ORGID as PROD3_0__,\r\n" +
 				"        orders.PROD_NO as PROD4_0__,\r\n" +
 				"        orders.orgid as orgid0__,\r\n" +
 				"        orders.ordernumber as ordernum2_0__ \r\n" +*/
 				"    from\r\n" +
 				"        Product product \r\n" +
 				"    inner join\r\n" +
 				"        TBL_ORDER {prod_orders} \r\n" +
 				"            on product.orgid={prod_orders}.PROD_ORGID \r\n" +
 				"            and product.productnumber={prod_orders}.PROD_NO" )
 				.addEntity( "product", Product.class )
 				.addJoin( "prod_orders", "product.orders" )
 				.list().get(0);
 
 		p = (Product) o[0];
 		assertTrue(Hibernate.isInitialized( p.getOrders() ));
 		assertNotNull(p.getOrders().iterator().next());
 		t.commit();
 		s.close();
 	}
 
 	public void testAutoDetectAliasing() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Organization ifa = new Organization("IFA");
 		Organization jboss = new Organization("JBoss");
 		Person gavin = new Person("Gavin");
 		Employment emp = new Employment(gavin, jboss, "AU");
 		Serializable orgId = s.save(jboss);
 		Serializable orgId2 = s.save(ifa);
 		s.save(gavin);
 		s.save(emp);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		List list = s.createSQLQuery( getEmploymentSQL() )
 				.addEntity( Employment.class.getName() )
 				.list();
 		assertEquals( 1,list.size() );
 
 		Employment emp2 = (Employment) list.get(0);
 		assertEquals(emp2.getEmploymentId(), emp.getEmploymentId() );
 		assertEquals(emp2.getStartDate().getDate(), emp.getStartDate().getDate() );
 		assertEquals(emp2.getEndDate(), emp.getEndDate() );
 
 		s.clear();
 
 		list = s.createSQLQuery( getEmploymentSQL() )
 		.addEntity( Employment.class.getName() )
 		.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
 		.list();
 		assertEquals( 1,list.size() );
 		Map m = (Map) list.get(0);
 		assertTrue(m.containsKey("Employment"));
 		assertEquals(1,m.size());
 
 		list = s.createSQLQuery(getEmploymentSQL()).list();
 		assertEquals(1, list.size());
 		Object[] o = (Object[]) list.get(0);
 		assertEquals(8, o.length);
 
 		list = s.createSQLQuery( getEmploymentSQL() ).setResultTransformer( new UpperCasedAliasToEntityMapResultTransformer() ).list();
 		assertEquals(1, list.size());
 		m = (Map) list.get(0);
 		assertTrue(m.containsKey("EMPID"));
 		assertTrue(m.containsKey("VALUE"));
 		assertTrue(m.containsKey("ENDDATE"));
 		assertEquals(8, m.size());
 
 		list = s.createSQLQuery( getEmploymentSQLMixedScalarEntity() ).addScalar( "employerid" ).addEntity( Employment.class ).list();
 		assertEquals(1, list.size());
 		o = (Object[]) list.get(0);
 		assertEquals(2, o.length);
 		assertClassAssignability( o[0].getClass(), Number.class);
 		assertClassAssignability( o[1].getClass(), Employment.class);
 
 
 
 		Query queryWithCollection = s.getNamedQuery("organizationEmploymentsExplicitAliases");
 		queryWithCollection.setLong("id",  jboss.getId() );
 		list = queryWithCollection.list();
 		assertEquals(list.size(),1);
 
 		s.clear();
 
 		list = s.createSQLQuery( getOrganizationJoinEmploymentSQL() )
 				.addEntity( "org", Organization.class )
 				.addJoin( "emp", "org.employments" )
 				.list();
 		assertEquals( 2,list.size() );
 
 		s.clear();
 
 		list = s.createSQLQuery( getOrganizationFetchJoinEmploymentSQL() )
 				.addEntity( "org", Organization.class )
 				.addJoin( "emp", "org.employments" )
 				.list();
 		assertEquals( 2,list.size() );
 
 		s.clear();
 
 		// TODO : why twice?
 		s.getNamedQuery( "organizationreturnproperty" ).list();
 		list = s.getNamedQuery( "organizationreturnproperty" ).list();
 		assertEquals( 2,list.size() );
 
 		s.clear();
 
 		list = s.getNamedQuery( "organizationautodetect" ).list();
 		assertEquals( 2,list.size() );
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete(emp2);
 
 		s.delete(jboss);
 		s.delete(gavin);
 		s.delete(ifa);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Dimension dim = new Dimension( 3, Integer.MAX_VALUE );
 		s.save( dim );
 		list = s.createSQLQuery( "select d_len * d_width as surface, d_len * d_width * 10 as volume from Dimension" ).list();
 		s.delete( dim );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		SpaceShip enterprise = new SpaceShip();
 		enterprise.setModel( "USS" );
 		enterprise.setName( "Entreprise" );
 		enterprise.setSpeed( 50d );
 		Dimension d = new Dimension(45, 10);
 		enterprise.setDimensions( d );
 		s.save( enterprise );
 		Object[] result = (Object[]) s.getNamedQuery( "spaceship" ).uniqueResult();
 		enterprise = ( SpaceShip ) result[0];
 		assertTrue(50d == enterprise.getSpeed() );
 		assertTrue( 450d == extractDoubleValue( result[1] ) );
 		assertTrue( 4500d == extractDoubleValue( result[2] ) );
 		s.delete( enterprise );
 		t.commit();
 		s.close();
 
 	}
 
 	public void testMixAndMatchEntityScalar() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Speech speech = new Speech();
 		speech.setLength( new Double( 23d ) );
 		speech.setName( "Mine" );
 		s.persist( speech );
 		s.flush();
 		s.clear();
 
 		List l = s.createSQLQuery( "select name, id, flength, name as scalarName from Speech" )
 				.setResultSetMapping( "speech" )
 				.list();
 		assertEquals( l.size(), 1 );
 
 		t.rollback();
 		s.close();
 	}
 
 	private double extractDoubleValue(Object value) {
 		if ( value instanceof BigInteger ) {
 			return ( ( BigInteger ) value ).doubleValue();
 		}
 		else if ( value instanceof BigDecimal ) {
 			return ( ( BigDecimal ) value ).doubleValue();
 		}
 		else {
 			return Double.valueOf( value.toString() ).doubleValue();
 		}
 	}
 
-	private static class UpperCasedAliasToEntityMapResultTransformer extends AliasToEntityMapResultTransformer {
+	private static class UpperCasedAliasToEntityMapResultTransformer extends BasicTransformerAdapter implements Serializable {
 		public Object transformTuple(Object[] tuple, String[] aliases) {
-			String[] ucAliases = new String[aliases.length];
-			for ( int i = 0; i < aliases.length; i++ ) {
-				ucAliases[i] = aliases[i].toUpperCase();
+			Map result = new HashMap( tuple.length );
+			for ( int i = 0; i < tuple.length; i++ ) {
+				String alias = aliases[i];
+				if ( alias != null ) {
+					result.put( alias.toUpperCase(), tuple[i] );
+				}
 			}
-			return super.transformTuple( tuple, ucAliases );
+			return result;
 		}
 	}
 }
