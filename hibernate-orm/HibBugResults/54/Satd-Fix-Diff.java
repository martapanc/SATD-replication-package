diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
index 5c62161421..ba755db02c 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
@@ -1,69 +1,67 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.criterion;
+
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Constrains a property to between two values
+ *
  * @author Gavin King
  */
 public class BetweenExpression implements Criterion {
-
 	private final String propertyName;
 	private final Object lo;
 	private final Object hi;
 
 	protected BetweenExpression(String propertyName, Object lo, Object hi) {
 		this.propertyName = propertyName;
 		this.lo = lo;
 		this.hi = hi;
 	}
 
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
-		return StringHelper.join(
-			" and ",
-			StringHelper.suffix( criteriaQuery.findColumns(propertyName, criteria), " between ? and ?" )
-		);
-
-		//TODO: get SQL rendering out of this package!
+	@Override
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		final String[] columns = criteriaQuery.findColumns( propertyName, criteria );
+		final String[] expressions = StringHelper.suffix( columns, " between ? and ?" );
+		return StringHelper.join( " and ", expressions );
 	}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		return new TypedValue[] {
-				criteriaQuery.getTypedValue(criteria, propertyName, lo),
-				criteriaQuery.getTypedValue(criteria, propertyName, hi)
+				criteriaQuery.getTypedValue( criteria, propertyName, lo ),
+				criteriaQuery.getTypedValue( criteria, propertyName, hi )
 		};
 	}
 
+	@Override
 	public String toString() {
 		return propertyName + " between " + lo + " and " + hi;
 	}
 
 }
