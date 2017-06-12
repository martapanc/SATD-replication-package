diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMapElementHolder.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMapElementHolder.java
index d532ae3485..83d8076c99 100755
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMapElementHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMapElementHolder.java
@@ -1,113 +1,113 @@
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
 package org.hibernate.collection.internal;
 
 import java.io.Serializable;
 import java.util.List;
 
 import org.dom4j.Element;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 import org.hibernate.type.XmlRepresentableType;
 
 /**
  * Wraps a collection of DOM sub-elements as a Map
  *
  * @author Gavin King
  *
  * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.  See Jira issues
  * <a href="https://hibernate.onjira.com/browse/HHH-7782">HHH-7782</a> and
  * <a href="https://hibernate.onjira.com/browse/HHH-7783">HHH-7783</a> for more information.
  */
 @SuppressWarnings({"UnusedDeclaration", "deprecation"})
 @Deprecated
 public class PersistentMapElementHolder extends PersistentIndexedElementHolder {
 
 	/**
 	 * Constructs a PersistentMapElementHolder.
 	 *
 	 * @param session The session
 	 * @param element The owning DOM element
 	 */
 	public PersistentMapElementHolder(SessionImplementor session, Element element) {
 		super( session, element );
 	}
 
 	/**
 	 * Constructs a PersistentMapElementHolder.
 	 *
 	 * @param session The session
 	 * @param persister The collection persister
 	 * @param key The collection key (fk value)
 	 */
 	public PersistentMapElementHolder(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		super( session, persister, key );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner) {
 		final Type elementType = persister.getElementType();
 		final Type indexType = persister.getIndexType();
 		final String indexNodeName = getIndexAttributeName( persister );
 
 		final Serializable[] cached = (Serializable[]) disassembled;
 		int i = 0;
 		while ( i < cached.length ) {
 			final Object index = indexType.assemble( cached[i++], getSession(), owner );
 			final Object object = elementType.assemble( cached[i++], getSession(), owner );
 
 			final Element subElement = element.addElement( persister.getElementNodeName() );
 			elementType.setToXMLNode( subElement, object, persister.getFactory() );
 
 			final String indexString = ( (XmlRepresentableType) indexType ).toXMLString( index, persister.getFactory() );
 			setIndex( subElement, indexNodeName, indexString );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Serializable disassemble(CollectionPersister persister) throws HibernateException {
 		final Type elementType = persister.getElementType();
 		final Type indexType = persister.getIndexType();
 		final String indexNodeName = getIndexAttributeName( persister );
 
 		final List elements =  element.elements( persister.getElementNodeName() );
 		final int length = elements.size();
 		final Serializable[] result = new Serializable[length*2];
 		int i = 0;
 		while ( i < length*2 ) {
-			final Element elem = (Element) elements.get(i/2);
+			final Element elem = (Element) elements.get( i/2 );
 			final Object object = elementType.fromXMLNode( elem, persister.getFactory() );
 			final String indexString = getIndex( elem, indexNodeName, i );
 			final Object index = ( (XmlRepresentableType) indexType ).fromXMLString( indexString, persister.getFactory() );
 			result[i++] = indexType.disassemble( index, getSession(), null );
 			result[i++] = elementType.disassemble( object, getSession(), null );
 		}
 		return result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedSet.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedSet.java
index 3e07821b2d..286dd59857 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedSet.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedSet.java
@@ -1,175 +1,175 @@
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
 package org.hibernate.collection.internal;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.SortedSet;
 import java.util.TreeMap;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.BasicCollectionPersister;
 
 /**
  * A persistent wrapper for a <tt>java.util.SortedSet</tt>. Underlying
  * collection is a <tt>TreeSet</tt>.
  *
  * @see java.util.TreeSet
  * @author <a href="mailto:doug.currie@alum.mit.edu">e</a>
  */
 public class PersistentSortedSet extends PersistentSet implements SortedSet {
 	protected Comparator comparator;
 
 	/**
 	 * Constructs a PersistentSortedSet.  This form needed for SOAP libraries, etc
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public PersistentSortedSet() {
 	}
 
 	/**
 	 * Constructs a PersistentSortedSet
 	 *
 	 * @param session The session
 	 */
 	public PersistentSortedSet(SessionImplementor session) {
 		super( session );
 	}
 
 	/**
 	 * Constructs a PersistentSortedSet
 	 *
 	 * @param session The session
 	 * @param set The underlying set data
 	 */
 	public PersistentSortedSet(SessionImplementor session, SortedSet set) {
 		super( session, set );
 		comparator = set.comparator();
 	}
 
 	@SuppressWarnings({"unchecked", "UnusedParameters"})
 	protected Serializable snapshot(BasicCollectionPersister persister, EntityMode entityMode)
 			throws HibernateException {
 		final TreeMap clonedSet = new TreeMap( comparator );
 		for ( Object setElement : set ) {
 			final Object copy = persister.getElementType().deepCopy( setElement, persister.getFactory() );
 			clonedSet.put( copy, copy );
 		}
 		return clonedSet;
 	}
 
 	public void setComparator(Comparator comparator) {
 		this.comparator = comparator;
 	}
 
 	@Override
 	public Comparator comparator() {
 		return comparator;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public SortedSet subSet(Object fromElement, Object toElement) {
 		read();
 		final SortedSet subSet = ( (SortedSet) set ).subSet( fromElement, toElement );
 		return new SubSetProxy( subSet );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public SortedSet headSet(Object toElement) {
 		read();
-		final SortedSet headSet = ( (SortedSet) set ).headSet(toElement);
+		final SortedSet headSet = ( (SortedSet) set ).headSet( toElement );
 		return new SubSetProxy( headSet );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public SortedSet tailSet(Object fromElement) {
 		read();
 		final SortedSet tailSet = ( (SortedSet) set ).tailSet( fromElement );
 		return new SubSetProxy( tailSet );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object first() {
 		read();
 		return ( (SortedSet) set ).first();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object last() {
 		read();
 		return ( (SortedSet) set ).last();
 	}
 
 	/**
 	 * wrapper for subSets to propagate write to its backing set
 	 */
 	class SubSetProxy extends SetProxy implements SortedSet {
 		SubSetProxy(SortedSet s) {
 			super( s );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public Comparator comparator() {
 			return ( (SortedSet) this.set ).comparator();
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public Object first() {
 			return ( (SortedSet) this.set ).first();
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public SortedSet headSet(Object toValue) {
 			return new SubSetProxy( ( (SortedSet) this.set ).headSet( toValue ) );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public Object last() {
 			return ( (SortedSet) this.set ).last();
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public SortedSet subSet(Object fromValue, Object toValue) {
 			return new SubSetProxy( ( (SortedSet) this.set ).subSet( fromValue, toValue ) );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public SortedSet tailSet(Object fromValue) {
 			return new SubSetProxy( ( (SortedSet) this.set ).tailSet( fromValue ) );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java
index c7bd8a45db..24f5b11f00 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java
@@ -1,115 +1,115 @@
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
 package org.hibernate.criterion;
 
 import java.util.ArrayList;
 
 import org.hibernate.Criteria;
 import org.hibernate.EntityMode;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * Constrains the property to a specified list of values
  *
  * @author Gavin King
  */
 public class InExpression implements Criterion {
 	private final String propertyName;
 	private final Object[] values;
 
 	/**
 	 * Constructs an InExpression
 	 *
 	 * @param propertyName The property name to check
 	 * @param values The values to check against
 	 *
 	 * @see Restrictions#in(String, java.util.Collection)
 	 * @see Restrictions#in(String, Object[])
 	 */
 	protected InExpression(String propertyName, Object[] values) {
 		this.propertyName = propertyName;
 		this.values = values;
 	}
 
 	@Override
 	public String toSqlString( Criteria criteria, CriteriaQuery criteriaQuery ) {
 		final String[] columns = criteriaQuery.findColumns( propertyName, criteria );
 		if ( criteriaQuery.getFactory().getDialect().supportsRowValueConstructorSyntaxInInList() || columns.length <= 1 ) {
 			String singleValueParam = StringHelper.repeat( "?, ", columns.length - 1 ) + "?";
 			if ( columns.length > 1 ) {
 				singleValueParam = '(' + singleValueParam + ')';
 			}
 			final String params = values.length > 0
 					? StringHelper.repeat( singleValueParam + ", ", values.length - 1 ) + singleValueParam
 					: "";
 			String cols = StringHelper.join( ", ", columns );
 			if ( columns.length > 1 ) {
 				cols = '(' + cols + ')';
 			}
 			return cols + " in (" + params + ')';
 		}
 		else {
 			String cols = " ( " + StringHelper.join( " = ? and ", columns ) + "= ? ) ";
 			cols = values.length > 0
 					? StringHelper.repeat( cols + "or ", values.length - 1 ) + cols
 					: "";
 			cols = " ( " + cols + " ) ";
 			return cols;
 		}
 	}
 
 	@Override
 	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
 		final ArrayList<TypedValue> list = new ArrayList<TypedValue>();
 		final Type type = criteriaQuery.getTypeUsingProjection( criteria, propertyName );
 		if ( type.isComponentType() ) {
 			final CompositeType compositeType = (CompositeType) type;
 			final Type[] subTypes = compositeType.getSubtypes();
 			for ( Object value : values ) {
 				for ( int i = 0; i < subTypes.length; i++ ) {
 					final Object subValue = value == null
 							? null
 							: compositeType.getPropertyValues( value, EntityMode.POJO )[i];
 					list.add( new TypedValue( subTypes[i], subValue ) );
 				}
 			}
 		}
 		else {
 			for ( Object value : values ) {
 				list.add( new TypedValue( type, value ) );
 			}
 		}
 
 		return list.toArray( new TypedValue[ list.size() ] );
 	}
 
 	@Override
 	public String toString() {
-		return propertyName + " in (" + StringHelper.toString(values) + ')';
+		return propertyName + " in (" + StringHelper.toString( values ) + ')';
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Order.java b/hibernate-core/src/main/java/org/hibernate/criterion/Order.java
index c7feea418c..dd3adccccb 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Order.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Order.java
@@ -1,172 +1,172 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.criterion;
 
 import java.io.Serializable;
 import java.sql.Types;
 
 import org.hibernate.Criteria;
 import org.hibernate.NullPrecedence;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Represents an ordering imposed upon the results of a Criteria
  * 
  * @author Gavin King
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class Order implements Serializable {
 	private boolean ascending;
 	private boolean ignoreCase;
 	private String propertyName;
 	private NullPrecedence nullPrecedence;
 
 	/**
 	 * Ascending order
 	 *
 	 * @param propertyName The property to order on
 	 *
 	 * @return The build Order instance
 	 */
 	public static Order asc(String propertyName) {
 		return new Order( propertyName, true );
 	}
 
 	/**
 	 * Descending order.
 	 *
 	 * @param propertyName The property to order on
 	 *
 	 * @return The build Order instance
 	 */
 	public static Order desc(String propertyName) {
 		return new Order( propertyName, false );
 	}
 
 	/**
 	 * Constructor for Order.  Order instances are generally created by factory methods.
 	 *
 	 * @see #asc
 	 * @see #desc
 	 */
 	protected Order(String propertyName, boolean ascending) {
 		this.propertyName = propertyName;
 		this.ascending = ascending;
 	}
 
 	/**
 	 * Should this ordering ignore case?  Has no effect on non-character properties.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Order ignoreCase() {
 		ignoreCase = true;
 		return this;
 	}
 
 	/**
 	 * Defines precedence for nulls.
 	 *
 	 * @param nullPrecedence The null precedence to use
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Order nulls(NullPrecedence nullPrecedence) {
 		this.nullPrecedence = nullPrecedence;
 		return this;
 	}
 
 	public String getPropertyName() {
 		return propertyName;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public boolean isAscending() {
 		return ascending;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public boolean isIgnoreCase() {
 		return ignoreCase;
 	}
 
 
 	/**
 	 * Render the SQL fragment
 	 *
 	 * @param criteria The criteria
 	 * @param criteriaQuery The overall query
 	 *
 	 * @return The ORDER BY fragment for this ordering
 	 */
 	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
 		final String[] columns = criteriaQuery.getColumnsUsingProjection( criteria, propertyName );
 		final Type type = criteriaQuery.getTypeUsingProjection( criteria, propertyName );
 		final SessionFactoryImplementor factory = criteriaQuery.getFactory();
 		final int[] sqlTypes = type.sqlTypes( factory );
 
 		final StringBuilder fragment = new StringBuilder();
 		for ( int i=0; i<columns.length; i++ ) {
 			final StringBuilder expression = new StringBuilder();
 			boolean lower = false;
 			if ( ignoreCase ) {
-				int sqlType = sqlTypes[i];
+				final int sqlType = sqlTypes[i];
 				lower = sqlType == Types.VARCHAR
 						|| sqlType == Types.CHAR
 						|| sqlType == Types.LONGVARCHAR;
 			}
 			
 			if ( lower ) {
 				expression.append( factory.getDialect().getLowercaseFunction() )
 						.append( '(' );
 			}
 			expression.append( columns[i] );
 			if ( lower ) {
 				expression.append( ')' );
 			}
 
 			fragment.append(
 					factory.getDialect().renderOrderByElement(
 							expression.toString(),
 							null,
 							ascending ? "asc" : "desc",
 							nullPrecedence != null ? nullPrecedence : factory.getSettings().getDefaultNullPrecedence()
 					)
 			);
 			if ( i < columns.length-1 ) {
 				fragment.append( ", " );
 			}
 		}
 
 		return fragment.toString();
 	}
 	
 	@Override
 	public String toString() {
 		return propertyName + ' '
 				+ ( ascending ? "asc" : "desc" )
 				+ ( nullPrecedence != null ? ' ' + nullPrecedence.name().toLowerCase() : "" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/PropertyProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/PropertyProjection.java
index 44832d7928..46c1b28916 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/PropertyProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/PropertyProjection.java
@@ -1,95 +1,95 @@
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
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.Type;
 
 /**
  * A property value, or grouped property value
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class PropertyProjection extends SimpleProjection {
 	private String propertyName;
 	private boolean grouped;
 
 	protected PropertyProjection(String prop, boolean grouped) {
 		this.propertyName = prop;
 		this.grouped = grouped;
 	}
 
 	protected PropertyProjection(String prop) {
-		this(prop, false);
+		this( prop, false );
 	}
 
 	@Override
 	public boolean isGrouped() {
 		return grouped;
 	}
 
 	public String getPropertyName() {
 		return propertyName;
 	}
 
 	@Override
 	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		return new Type[] { criteriaQuery.getType( criteria, propertyName ) };
 	}
 
 	@Override
 	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
 		final StringBuilder buf = new StringBuilder();
 		final String[] cols = criteriaQuery.getColumns( propertyName, criteria );
 		for ( int i=0; i<cols.length; i++ ) {
 			buf.append( cols[i] )
 					.append( " as y" )
 					.append( position + i )
 					.append( '_' );
 			if (i < cols.length -1) {
 				buf.append( ", " );
 			}
 		}
 		return buf.toString();
 	}
 
 	@Override
 	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		if ( !grouped ) {
 			return super.toGroupSqlString( criteria, criteriaQuery );
 		}
 		else {
 			return StringHelper.join( ", ", criteriaQuery.getColumns( propertyName, criteria ) );
 		}
 	}
 
 	@Override
 	public String toString() {
 		return propertyName;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
index 2e3a6f199f..915c492f43 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
@@ -1,267 +1,304 @@
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
 package org.hibernate.dialect;
+
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.CharIndexFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An abstract base class for Sybase and MS SQL Server dialects.
  *
  * @author Gavin King
  */
 abstract class AbstractTransactSQLDialect extends Dialect {
 	public AbstractTransactSQLDialect() {
 		super();
-        registerColumnType( Types.BINARY, "binary($l)" );
+		registerColumnType( Types.BINARY, "binary($l)" );
 		registerColumnType( Types.BIT, "tinyint" );
 		registerColumnType( Types.BIGINT, "numeric(19,0)" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "smallint" );
 		registerColumnType( Types.INTEGER, "int" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "datetime" );
 		registerColumnType( Types.TIME, "datetime" );
 		registerColumnType( Types.TIMESTAMP, "datetime" );
 		registerColumnType( Types.VARBINARY, "varbinary($l)" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.BLOB, "image" );
 		registerColumnType( Types.CLOB, "text" );
 
-		registerFunction( "ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
-		registerFunction( "char", new StandardSQLFunction("char", StandardBasicTypes.CHARACTER) );
-		registerFunction( "len", new StandardSQLFunction("len", StandardBasicTypes.LONG) );
-		registerFunction( "lower", new StandardSQLFunction("lower") );
-		registerFunction( "upper", new StandardSQLFunction("upper") );
-		registerFunction( "str", new StandardSQLFunction("str", StandardBasicTypes.STRING) );
-		registerFunction( "ltrim", new StandardSQLFunction("ltrim") );
-		registerFunction( "rtrim", new StandardSQLFunction("rtrim") );
-		registerFunction( "reverse", new StandardSQLFunction("reverse") );
-		registerFunction( "space", new StandardSQLFunction("space", StandardBasicTypes.STRING) );
-
-		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING) );
-
-		registerFunction( "current_timestamp", new NoArgSQLFunction("getdate", StandardBasicTypes.TIMESTAMP) );
-		registerFunction( "current_time", new NoArgSQLFunction("getdate", StandardBasicTypes.TIME) );
-		registerFunction( "current_date", new NoArgSQLFunction("getdate", StandardBasicTypes.DATE) );
-
-		registerFunction( "getdate", new NoArgSQLFunction("getdate", StandardBasicTypes.TIMESTAMP) );
-		registerFunction( "getutcdate", new NoArgSQLFunction("getutcdate", StandardBasicTypes.TIMESTAMP) );
-		registerFunction( "day", new StandardSQLFunction("day", StandardBasicTypes.INTEGER) );
-		registerFunction( "month", new StandardSQLFunction("month", StandardBasicTypes.INTEGER) );
-		registerFunction( "year", new StandardSQLFunction("year", StandardBasicTypes.INTEGER) );
-		registerFunction( "datename", new StandardSQLFunction("datename", StandardBasicTypes.STRING) );
-
-		registerFunction( "abs", new StandardSQLFunction("abs") );
-		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
-
-		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
-		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
-		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
-		registerFunction( "cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
-		registerFunction( "cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE) );
-		registerFunction( "exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
-		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE) );
-		registerFunction( "log10", new StandardSQLFunction("log10", StandardBasicTypes.DOUBLE) );
-		registerFunction( "sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
-		registerFunction( "sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
-		registerFunction( "tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
-		registerFunction( "pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE) );
-		registerFunction( "square", new StandardSQLFunction("square") );
-		registerFunction( "rand", new StandardSQLFunction("rand", StandardBasicTypes.FLOAT) );
-
-		registerFunction("radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE) );
-		registerFunction("degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE) );
-
-		registerFunction( "round", new StandardSQLFunction("round") );
-		registerFunction( "ceiling", new StandardSQLFunction("ceiling") );
-		registerFunction( "floor", new StandardSQLFunction("floor") );
-
-		registerFunction( "isnull", new StandardSQLFunction("isnull") );
-
-		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(","+",")" ) );
+		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
+		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
+		registerFunction( "len", new StandardSQLFunction( "len", StandardBasicTypes.LONG ) );
+		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
+		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
+		registerFunction( "str", new StandardSQLFunction( "str", StandardBasicTypes.STRING ) );
+		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
+		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
+		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
+		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
+
+		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING ) );
+
+		registerFunction( "current_timestamp", new NoArgSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "current_time", new NoArgSQLFunction( "getdate", StandardBasicTypes.TIME ) );
+		registerFunction( "current_date", new NoArgSQLFunction( "getdate", StandardBasicTypes.DATE ) );
+
+		registerFunction( "getdate", new NoArgSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "getutcdate", new NoArgSQLFunction( "getutcdate", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
+		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
+		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
+		registerFunction( "datename", new StandardSQLFunction( "datename", StandardBasicTypes.STRING ) );
+
+		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
+		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
+
+		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "square", new StandardSQLFunction( "square" ) );
+		registerFunction( "rand", new StandardSQLFunction( "rand", StandardBasicTypes.FLOAT ) );
+
+		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
+
+		registerFunction( "round", new StandardSQLFunction( "round" ) );
+		registerFunction( "ceiling", new StandardSQLFunction( "ceiling" ) );
+		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
+
+		registerFunction( "isnull", new StandardSQLFunction( "isnull" ) );
+
+		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "+", ")" ) );
 
 		registerFunction( "length", new StandardSQLFunction( "len", StandardBasicTypes.INTEGER ) );
-		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "ltrim(rtrim(?1))") );
+		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "ltrim(rtrim(?1))" ) );
 		registerFunction( "locate", new CharIndexFunction() );
 
-		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
+		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, NO_BATCH );
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
+
+	@Override
 	public String getNullColumnString() {
 		return "";
 	}
+
+	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
+	@Override
 	public String getForUpdateString() {
 		return "";
 	}
 
+	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
+
+	@Override
 	public String getIdentitySelectString() {
 		return "select @@identity";
 	}
+
+	@Override
 	public String getIdentityColumnString() {
-		return "identity not null"; //starts with 1, implicitly
+		//starts with 1, implicitly
+		return "identity not null";
 	}
 
+	@Override
 	public boolean supportsInsertSelectIdentity() {
 		return true;
 	}
 
+	@Override
 	public String appendIdentitySelectToInsert(String insertSQL) {
 		return insertSQL + "\nselect @@identity";
 	}
 
 	@Override
 	public String appendLockHint(LockOptions lockOptions, String tableName) {
 		return lockOptions.getLockMode().greaterThan( LockMode.READ ) ? tableName + " holdlock" : tableName;
 	}
 
+	@Override
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
 		// TODO:  merge additional lockoptions support in Dialect.applyLocksToSql
-		Iterator itr = aliasedLockOptions.getAliasLockIterator();
-		StringBuilder buffer = new StringBuilder( sql );
+		final Iterator itr = aliasedLockOptions.getAliasLockIterator();
+		final StringBuilder buffer = new StringBuilder( sql );
 		int correction = 0;
 		while ( itr.hasNext() ) {
-			final Map.Entry entry = ( Map.Entry ) itr.next();
-			final LockMode lockMode = ( LockMode ) entry.getValue();
+			final Map.Entry entry = (Map.Entry) itr.next();
+			final LockMode lockMode = (LockMode) entry.getValue();
 			if ( lockMode.greaterThan( LockMode.READ ) ) {
-				final String alias = ( String ) entry.getKey();
-				int start = -1, end = -1;
+				final String alias = (String) entry.getKey();
+				int start = -1;
+				int end = -1;
 				if ( sql.endsWith( " " + alias ) ) {
 					start = ( sql.length() - alias.length() ) + correction;
 					end = start + alias.length();
 				}
 				else {
 					int position = sql.indexOf( " " + alias + " " );
 					if ( position <= -1 ) {
 						position = sql.indexOf( " " + alias + "," );
 					}
 					if ( position > -1 ) {
 						start = position + correction + 1;
 						end = start + alias.length();
 					}
 				}
 
 				if ( start > -1 ) {
 					final String lockHint = appendLockHint( lockMode, alias );
 					buffer.replace( start, end, lockHint );
 					correction += ( lockHint.length() - alias.length() );
 				}
 			}
 		}
 		return buffer.toString();
 	}
 
+	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
-		return col; // sql server just returns automatically
+		// sql server just returns automatically
+		return col;
 	}
 
+	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		boolean isResultSet = ps.execute();
-//		 This assumes you will want to ignore any update counts
+		// This assumes you will want to ignore any update counts
 		while ( !isResultSet && ps.getUpdateCount() != -1 ) {
 			isResultSet = ps.getMoreResults();
 		}
-//		 You may still have other ResultSets or update counts left to process here
-//		 but you can't do it now or the ResultSet you just got will be closed
+
+		// You may still have other ResultSets or update counts left to process here
+		// but you can't do it now or the ResultSet you just got will be closed
 		return ps.getResultSet();
 	}
 
+	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
+	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
+	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select getdate()";
 	}
 
+	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
+	@Override
 	public String generateTemporaryTableName(String baseTableName) {
 		return "#" + baseTableName;
 	}
 
+	@Override
 	public boolean dropTemporaryTableAfterUse() {
-		return true;  // sql-server, at least needed this dropped after use; strange!
+		// sql-server, at least needed this dropped after use; strange!
+		return true;
 	}
+
+	@Override
 	public String getSelectGUIDString() {
 		return "select newid()";
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
+	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return true;
 	}
 
+	@Override
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return true;
 	}
+
+	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/CUBRIDDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/CUBRIDDialect.java
index 85aacdef71..a213582ca8 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/CUBRIDDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/CUBRIDDialect.java
@@ -1,345 +1,381 @@
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
-
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
-import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
-import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.CUBRIDLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for CUBRID (8.3.x and later).
  *
  * @author Seok Jeong Il
  */
 public class CUBRIDDialect extends Dialect {
-    public CUBRIDDialect() {
-        super();
+	/**
+	 * Constructs a CUBRIDDialect
+	 */
+	public CUBRIDDialect() {
+		super();
 
 		registerColumnType( Types.BIGINT, "bigint" );
-        registerColumnType( Types.BIT, "bit(8)" );    
-		registerColumnType( Types.BLOB, "bit varying(65535)" );	
-		registerColumnType( Types.BOOLEAN, "bit(8)");
+		registerColumnType( Types.BIT, "bit(8)" );
+		registerColumnType( Types.BLOB, "bit varying(65535)" );
+		registerColumnType( Types.BOOLEAN, "bit(8)" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.CLOB, "string" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "decimal" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.INTEGER, "int" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
-		registerColumnType( Types.REAL,    "double"        );
-        registerColumnType( Types.SMALLINT, "short" );
-        registerColumnType( Types.TIME,     "time" );
-        registerColumnType( Types.TIMESTAMP, "timestamp" );
-        registerColumnType( Types.TINYINT, "short" );
+		registerColumnType( Types.REAL, "double" );
+		registerColumnType( Types.SMALLINT, "short" );
+		registerColumnType( Types.TIME, "time" );
+		registerColumnType( Types.TIMESTAMP, "timestamp" );
+		registerColumnType( Types.TINYINT, "short" );
 		registerColumnType( Types.VARBINARY, 2000, "bit varying($l)" );
-	    registerColumnType( Types.VARCHAR, "string" );
-	    registerColumnType( Types.VARCHAR, 2000, "varchar($l)" );
-	    registerColumnType( Types.VARCHAR, 255, "varchar($l)" );
-
-        getDefaultProperties().setProperty(Environment.USE_STREAMS_FOR_BINARY, "true");
-        getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
-
-        registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER)  );
-        registerFunction("bin", new StandardSQLFunction("bin", StandardBasicTypes.STRING)       );
-        registerFunction("char_length", new StandardSQLFunction("char_length", StandardBasicTypes.LONG) );
-        registerFunction("character_length", new StandardSQLFunction("character_length", StandardBasicTypes.LONG) );
-        registerFunction("lengthb", new StandardSQLFunction("lengthb", StandardBasicTypes.LONG) );
-        registerFunction("lengthh", new StandardSQLFunction("lengthh", StandardBasicTypes.LONG) );
-        registerFunction("lcase", new StandardSQLFunction("lcase") );
-        registerFunction("lower", new StandardSQLFunction("lower") );
-        registerFunction("ltrim", new StandardSQLFunction("ltrim") );
-        registerFunction("reverse", new StandardSQLFunction("reverse") );
-        registerFunction("rtrim", new StandardSQLFunction("rtrim") );
-        registerFunction("trim",      new StandardSQLFunction("trim")                             );
-        registerFunction("space", new StandardSQLFunction("space", StandardBasicTypes.STRING) );
-        registerFunction("ucase", new StandardSQLFunction("ucase") );
-        registerFunction("upper", new StandardSQLFunction("upper") );
-
-        registerFunction("abs", new StandardSQLFunction("abs") );
-        registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
-
-        registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
-        registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
-        registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
-        registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
-        registerFunction("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE) );
-        registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
-        registerFunction("ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
-        registerFunction("log2", new StandardSQLFunction("log2", StandardBasicTypes.DOUBLE) );
-        registerFunction("log10", new StandardSQLFunction("log10", StandardBasicTypes.DOUBLE) );
-        registerFunction("pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE) );
-        registerFunction("rand", new NoArgSQLFunction("rand", StandardBasicTypes.DOUBLE) );
-        registerFunction("random", new NoArgSQLFunction("random", StandardBasicTypes.DOUBLE) );
-        registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
-        registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
-        registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
-
-        registerFunction("radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE) );
-        registerFunction("degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE) );
-
-        registerFunction("ceil", new StandardSQLFunction("ceil", StandardBasicTypes.INTEGER) );
-        registerFunction("floor", new StandardSQLFunction("floor", StandardBasicTypes.INTEGER) );
-        registerFunction("round", new StandardSQLFunction("round") );
-
-        registerFunction("datediff", new StandardSQLFunction("datediff", StandardBasicTypes.INTEGER) );
-        registerFunction("timediff", new StandardSQLFunction("timediff", StandardBasicTypes.TIME) );
-
-        registerFunction("date", new StandardSQLFunction("date", StandardBasicTypes.DATE) );
-        registerFunction("curdate", new NoArgSQLFunction("curdate", StandardBasicTypes.DATE) );
-        registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
-        registerFunction("sys_date", new NoArgSQLFunction("sys_date", StandardBasicTypes.DATE, false) );
-        registerFunction("sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.DATE, false) );
-
-        registerFunction("time", new StandardSQLFunction("time", StandardBasicTypes.TIME) );
-        registerFunction("curtime", new NoArgSQLFunction("curtime", StandardBasicTypes.TIME) );
-        registerFunction("current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false) );
-        registerFunction("sys_time", new NoArgSQLFunction("sys_time", StandardBasicTypes.TIME, false) );
-        registerFunction("systime", new NoArgSQLFunction("systime", StandardBasicTypes.TIME, false) );
-
-        registerFunction("timestamp", new StandardSQLFunction("timestamp", StandardBasicTypes.TIMESTAMP) );
-        registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
-        registerFunction("sys_timestamp", new NoArgSQLFunction("sys_timestamp", StandardBasicTypes.TIMESTAMP, false) );
-        registerFunction("systimestamp", new NoArgSQLFunction("systimestamp", StandardBasicTypes.TIMESTAMP, false) );
-        registerFunction("localtime", new NoArgSQLFunction("localtime", StandardBasicTypes.TIMESTAMP, false) );
-        registerFunction("localtimestamp", new NoArgSQLFunction("localtimestamp", StandardBasicTypes.TIMESTAMP, false) );
-
-        registerFunction("day", new StandardSQLFunction("day", StandardBasicTypes.INTEGER) );
-        registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", StandardBasicTypes.INTEGER) );
-        registerFunction("dayofweek", new StandardSQLFunction("dayofweek", StandardBasicTypes.INTEGER) );
-        registerFunction("dayofyear", new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER) );
-        registerFunction("from_days", new StandardSQLFunction("from_days", StandardBasicTypes.DATE) );
-        registerFunction("from_unixtime", new StandardSQLFunction("from_unixtime", StandardBasicTypes.TIMESTAMP) );
-        registerFunction("last_day", new StandardSQLFunction("last_day", StandardBasicTypes.DATE) );
-        registerFunction("minute", new StandardSQLFunction("minute", StandardBasicTypes.INTEGER) );
-        registerFunction("month", new StandardSQLFunction("month", StandardBasicTypes.INTEGER) );
-        registerFunction("months_between", new StandardSQLFunction("months_between", StandardBasicTypes.DOUBLE) );
-        registerFunction("now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP) );
-        registerFunction("quarter", new StandardSQLFunction("quarter", StandardBasicTypes.INTEGER) );
-        registerFunction("second", new StandardSQLFunction("second", StandardBasicTypes.INTEGER) );
-        registerFunction("sec_to_time", new StandardSQLFunction("sec_to_time", StandardBasicTypes.TIME) );
-        registerFunction("time_to_sec", new StandardSQLFunction("time_to_sec", StandardBasicTypes.INTEGER) );
-        registerFunction("to_days", new StandardSQLFunction("to_days", StandardBasicTypes.LONG) );
-        registerFunction("unix_timestamp", new StandardSQLFunction("unix_timestamp", StandardBasicTypes.LONG) );
-        registerFunction("utc_date", new NoArgSQLFunction("utc_date", StandardBasicTypes.STRING) );
-        registerFunction("utc_time", new NoArgSQLFunction("utc_time", StandardBasicTypes.STRING) );
-        registerFunction("week", new StandardSQLFunction("week", StandardBasicTypes.INTEGER) );
-        registerFunction("weekday", new StandardSQLFunction("weekday", StandardBasicTypes.INTEGER) );
-        registerFunction("year", new StandardSQLFunction("year", StandardBasicTypes.INTEGER) );
-
-        registerFunction("hex", new StandardSQLFunction("hex", StandardBasicTypes.STRING) );
-
-        registerFunction("octet_length", new StandardSQLFunction("octet_length", StandardBasicTypes.LONG) );
-        registerFunction("bit_length", new StandardSQLFunction("bit_length", StandardBasicTypes.LONG) );
-
-        registerFunction("bit_count", new StandardSQLFunction("bit_count", StandardBasicTypes.LONG) );
-        registerFunction("md5", new StandardSQLFunction("md5", StandardBasicTypes.STRING) );
-
-        registerFunction( "concat", new StandardSQLFunction( "concat", StandardBasicTypes.STRING ) );
-
-        registerFunction("substring", new StandardSQLFunction("substring",    StandardBasicTypes.STRING)   );
-        registerFunction("substr",    new StandardSQLFunction("substr",       StandardBasicTypes.STRING)   );
-
-        registerFunction("length",    new StandardSQLFunction("length",       StandardBasicTypes.INTEGER)  );
-        registerFunction("bit_length",new StandardSQLFunction("bit_length",   StandardBasicTypes.INTEGER)  );
-        registerFunction("coalesce",  new StandardSQLFunction("coalesce")                         );
-        registerFunction("nullif",    new StandardSQLFunction("nullif")                           );
-        registerFunction("mod",       new StandardSQLFunction("mod")                              );
-
-        registerFunction("power",     new StandardSQLFunction("power")                            );
-        registerFunction("stddev",    new StandardSQLFunction("stddev")                           );
-        registerFunction("variance",  new StandardSQLFunction("variance")                         );
-        registerFunction("trunc",     new StandardSQLFunction("trunc")                            );
-        registerFunction("nvl",       new StandardSQLFunction("nvl")                              );
-        registerFunction("nvl2",      new StandardSQLFunction("nvl2")                             );
-        registerFunction("chr",       new StandardSQLFunction("chr",          StandardBasicTypes.CHARACTER));
-        registerFunction("to_char",   new StandardSQLFunction("to_char",      StandardBasicTypes.STRING)   );
-        registerFunction("to_date",   new StandardSQLFunction("to_date",      StandardBasicTypes.TIMESTAMP));
-        registerFunction("instr",     new StandardSQLFunction("instr",        StandardBasicTypes.INTEGER)  );
-        registerFunction("instrb",    new StandardSQLFunction("instrb",       StandardBasicTypes.INTEGER)  );
-        registerFunction("lpad",      new StandardSQLFunction("lpad",         StandardBasicTypes.STRING)   );
-        registerFunction("replace",   new StandardSQLFunction("replace",      StandardBasicTypes.STRING)   );
-        registerFunction("rpad",      new StandardSQLFunction("rpad",         StandardBasicTypes.STRING)   );
-        registerFunction("translate", new StandardSQLFunction("translate",    StandardBasicTypes.STRING)   );
-
-        registerFunction("add_months",        new StandardSQLFunction("add_months",       StandardBasicTypes.DATE)             );
-        registerFunction("user",              new NoArgSQLFunction("user",                StandardBasicTypes.STRING,   false)  );
-        registerFunction("rownum",            new NoArgSQLFunction("rownum",              StandardBasicTypes.LONG,     false)  );
-        registerFunction("concat",            new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", ""));
-
-        registerKeyword( "TYPE" );
-        registerKeyword( "YEAR" );
-        registerKeyword( "MONTH" );
-        registerKeyword( "ALIAS" );
-        registerKeyword( "VALUE" );
-        registerKeyword( "FIRST" );
-        registerKeyword( "ROLE" );
-        registerKeyword( "CLASS" );
-        registerKeyword( "BIT" );
-        registerKeyword( "TIME" );
-        registerKeyword( "QUERY" );
-        registerKeyword( "DATE" );
-        registerKeyword( "USER" );
-        registerKeyword( "ACTION" );
-        registerKeyword( "SYS_USER" );
-        registerKeyword( "ZONE" );
-        registerKeyword( "LANGUAGE" );
-        registerKeyword( "DICTIONARY" );
-        registerKeyword( "DATA" );
-        registerKeyword( "TEST" );
-        registerKeyword( "SUPERCLASS" );
-        registerKeyword( "SECTION" );
-        registerKeyword( "LOWER" );
-        registerKeyword( "LIST" );
-        registerKeyword( "OID" );
-        registerKeyword( "DAY" );
-        registerKeyword( "IF" );
-        registerKeyword( "ATTRIBUTE" );
-        registerKeyword( "STRING" );
-        registerKeyword( "SEARCH" );
-    }
-	
+		registerColumnType( Types.VARCHAR, "string" );
+		registerColumnType( Types.VARCHAR, 2000, "varchar($l)" );
+		registerColumnType( Types.VARCHAR, 255, "varchar($l)" );
+
+		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
+		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
+
+		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
+		registerFunction( "bin", new StandardSQLFunction( "bin", StandardBasicTypes.STRING ) );
+		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.LONG ) );
+		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.LONG ) );
+		registerFunction( "lengthb", new StandardSQLFunction( "lengthb", StandardBasicTypes.LONG ) );
+		registerFunction( "lengthh", new StandardSQLFunction( "lengthh", StandardBasicTypes.LONG ) );
+		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
+		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
+		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
+		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
+		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
+		registerFunction( "trim", new StandardSQLFunction( "trim" ) );
+		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
+		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
+		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
+
+		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
+		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
+
+		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "log2", new StandardSQLFunction( "log2", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "random", new NoArgSQLFunction( "random", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
+
+		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
+
+		registerFunction( "ceil", new StandardSQLFunction( "ceil", StandardBasicTypes.INTEGER ) );
+		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.INTEGER ) );
+		registerFunction( "round", new StandardSQLFunction( "round" ) );
+
+		registerFunction( "datediff", new StandardSQLFunction( "datediff", StandardBasicTypes.INTEGER ) );
+		registerFunction( "timediff", new StandardSQLFunction( "timediff", StandardBasicTypes.TIME ) );
+
+		registerFunction( "date", new StandardSQLFunction( "date", StandardBasicTypes.DATE ) );
+		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
+		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
+		registerFunction( "sys_date", new NoArgSQLFunction( "sys_date", StandardBasicTypes.DATE, false ) );
+		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.DATE, false ) );
+
+		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
+		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
+		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
+		registerFunction( "sys_time", new NoArgSQLFunction( "sys_time", StandardBasicTypes.TIME, false ) );
+		registerFunction( "systime", new NoArgSQLFunction( "systime", StandardBasicTypes.TIME, false ) );
+
+		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction(
+				"current_timestamp", new NoArgSQLFunction(
+				"current_timestamp",
+				StandardBasicTypes.TIMESTAMP,
+				false
+		)
+		);
+		registerFunction(
+				"sys_timestamp", new NoArgSQLFunction(
+				"sys_timestamp",
+				StandardBasicTypes.TIMESTAMP,
+				false
+		)
+		);
+		registerFunction( "systimestamp", new NoArgSQLFunction( "systimestamp", StandardBasicTypes.TIMESTAMP, false ) );
+		registerFunction( "localtime", new NoArgSQLFunction( "localtime", StandardBasicTypes.TIMESTAMP, false ) );
+		registerFunction(
+				"localtimestamp", new NoArgSQLFunction(
+				"localtimestamp",
+				StandardBasicTypes.TIMESTAMP,
+				false
+		)
+		);
+
+		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
+		registerFunction( "from_days", new StandardSQLFunction( "from_days", StandardBasicTypes.DATE ) );
+		registerFunction( "from_unixtime", new StandardSQLFunction( "from_unixtime", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "last_day", new StandardSQLFunction( "last_day", StandardBasicTypes.DATE ) );
+		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
+		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
+		registerFunction( "months_between", new StandardSQLFunction( "months_between", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
+		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
+		registerFunction( "sec_to_time", new StandardSQLFunction( "sec_to_time", StandardBasicTypes.TIME ) );
+		registerFunction( "time_to_sec", new StandardSQLFunction( "time_to_sec", StandardBasicTypes.INTEGER ) );
+		registerFunction( "to_days", new StandardSQLFunction( "to_days", StandardBasicTypes.LONG ) );
+		registerFunction( "unix_timestamp", new StandardSQLFunction( "unix_timestamp", StandardBasicTypes.LONG ) );
+		registerFunction( "utc_date", new NoArgSQLFunction( "utc_date", StandardBasicTypes.STRING ) );
+		registerFunction( "utc_time", new NoArgSQLFunction( "utc_time", StandardBasicTypes.STRING ) );
+		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
+		registerFunction( "weekday", new StandardSQLFunction( "weekday", StandardBasicTypes.INTEGER ) );
+		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
+
+		registerFunction( "hex", new StandardSQLFunction( "hex", StandardBasicTypes.STRING ) );
+
+		registerFunction( "octet_length", new StandardSQLFunction( "octet_length", StandardBasicTypes.LONG ) );
+		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.LONG ) );
+
+		registerFunction( "bit_count", new StandardSQLFunction( "bit_count", StandardBasicTypes.LONG ) );
+		registerFunction( "md5", new StandardSQLFunction( "md5", StandardBasicTypes.STRING ) );
+
+		registerFunction( "concat", new StandardSQLFunction( "concat", StandardBasicTypes.STRING ) );
+
+		registerFunction( "substring", new StandardSQLFunction( "substring", StandardBasicTypes.STRING ) );
+		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
+
+		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.INTEGER ) );
+		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.INTEGER ) );
+		registerFunction( "coalesce", new StandardSQLFunction( "coalesce" ) );
+		registerFunction( "nullif", new StandardSQLFunction( "nullif" ) );
+		registerFunction( "mod", new StandardSQLFunction( "mod" ) );
+
+		registerFunction( "power", new StandardSQLFunction( "power" ) );
+		registerFunction( "stddev", new StandardSQLFunction( "stddev" ) );
+		registerFunction( "variance", new StandardSQLFunction( "variance" ) );
+		registerFunction( "trunc", new StandardSQLFunction( "trunc" ) );
+		registerFunction( "nvl", new StandardSQLFunction( "nvl" ) );
+		registerFunction( "nvl2", new StandardSQLFunction( "nvl2" ) );
+		registerFunction( "chr", new StandardSQLFunction( "chr", StandardBasicTypes.CHARACTER ) );
+		registerFunction( "to_char", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
+		registerFunction( "to_date", new StandardSQLFunction( "to_date", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "instr", new StandardSQLFunction( "instr", StandardBasicTypes.INTEGER ) );
+		registerFunction( "instrb", new StandardSQLFunction( "instrb", StandardBasicTypes.INTEGER ) );
+		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
+		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
+		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
+		registerFunction( "translate", new StandardSQLFunction( "translate", StandardBasicTypes.STRING ) );
+
+		registerFunction( "add_months", new StandardSQLFunction( "add_months", StandardBasicTypes.DATE ) );
+		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING, false ) );
+		registerFunction( "rownum", new NoArgSQLFunction( "rownum", StandardBasicTypes.LONG, false ) );
+		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
+
+		registerKeyword( "TYPE" );
+		registerKeyword( "YEAR" );
+		registerKeyword( "MONTH" );
+		registerKeyword( "ALIAS" );
+		registerKeyword( "VALUE" );
+		registerKeyword( "FIRST" );
+		registerKeyword( "ROLE" );
+		registerKeyword( "CLASS" );
+		registerKeyword( "BIT" );
+		registerKeyword( "TIME" );
+		registerKeyword( "QUERY" );
+		registerKeyword( "DATE" );
+		registerKeyword( "USER" );
+		registerKeyword( "ACTION" );
+		registerKeyword( "SYS_USER" );
+		registerKeyword( "ZONE" );
+		registerKeyword( "LANGUAGE" );
+		registerKeyword( "DICTIONARY" );
+		registerKeyword( "DATA" );
+		registerKeyword( "TEST" );
+		registerKeyword( "SUPERCLASS" );
+		registerKeyword( "SECTION" );
+		registerKeyword( "LOWER" );
+		registerKeyword( "LIST" );
+		registerKeyword( "OID" );
+		registerKeyword( "DAY" );
+		registerKeyword( "IF" );
+		registerKeyword( "ATTRIBUTE" );
+		registerKeyword( "STRING" );
+		registerKeyword( "SEARCH" );
+	}
+
+	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
+	@Override
 	public String getIdentityInsertString() {
 		return "NULL";
 	}
-	
+
+	@Override
 	public boolean supportsColumnCheck() {
 		return false;
 	}
-	
+
+	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
-    public String getIdentitySelectString() {
-        return "select last_insert_id()";
-    }
+	@Override
+	public String getIdentitySelectString() {
+		return "select last_insert_id()";
+	}
 
-    protected String getIdentityColumnString() {
-        return "not null auto_increment"; //starts with 1, implicitly
-    }
+	@Override
+	protected String getIdentityColumnString() {
+		//starts with 1, implicitly
+		return "not null auto_increment";
+	}
 
-    /*
-     * CUBRID supports "ADD [COLUMN | ATTRIBUTE]"
-    */
-    public String getAddColumnString() {
-        return "add";
-    }
+	@Override
+	public String getAddColumnString() {
+		return "add";
+	}
 
-    public String getSequenceNextValString(String sequenceName) {
-        return "select " + sequenceName + ".next_value from table({1}) as T(X)";
-    }
+	@Override
+	public String getSequenceNextValString(String sequenceName) {
+		return "select " + sequenceName + ".next_value from table({1}) as T(X)";
+	}
 
-    public String getCreateSequenceString(String sequenceName) {
-        return "create serial " + sequenceName;
-    }
+	@Override
+	public String getCreateSequenceString(String sequenceName) {
+		return "create serial " + sequenceName;
+	}
 
-    public String getDropSequenceString(String sequenceName) {
-        return "drop serial " + sequenceName;
-    }
+	@Override
+	public String getDropSequenceString(String sequenceName) {
+		return "drop serial " + sequenceName;
+	}
 
+	@Override
 	public String getDropForeignKeyString() {
 		return " drop foreign key ";
 	}
-	
+
+	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
-    public boolean supportsSequences() {
-        return true;
-    }
+	@Override
+	public boolean supportsSequences() {
+		return true;
+	}
 
+	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
-    public String getQuerySequencesString() {
-        return "select name from db_serial";
-    }
-
-    /**
-     * The character specific to this dialect used to close a quoted identifier.
-     * CUBRID supports square brackets (MSSQL style), backticks (MySQL style),
-     * as well as double quotes (Oracle style).
-     *
-     * @return The dialect's specific open quote character.
-     */
-    public char openQuote() {
-        return '[';
-    }
-
-    public char closeQuote() {
-        return ']';
-    }
-
-    public String getForUpdateString() {
-        return " ";
-    }
-
-    public boolean supportsUnionAll() {
-        return true;
-    }
-
-    public boolean supportsCurrentTimestampSelection() {
-        return true;
-    }
-
-    public String getCurrentTimestampSelectString() {
-        return "select now()";
-    }
-
-    public boolean isCurrentTimestampSelectStringCallable() {
-        return false;
-    }
-
-    public boolean supportsEmptyInList() {
-        return false;
-    }
-	
+	@Override
+	public String getQuerySequencesString() {
+		return "select name from db_serial";
+	}
+
+	@Override
+	public char openQuote() {
+		return '[';
+	}
+
+	@Override
+	public char closeQuote() {
+		return ']';
+	}
+
+	@Override
+	public String getForUpdateString() {
+		return " ";
+	}
+
+	@Override
+	public boolean supportsUnionAll() {
+		return true;
+	}
+
+	@Override
+	public boolean supportsCurrentTimestampSelection() {
+		return true;
+	}
+
+	@Override
+	public String getCurrentTimestampSelectString() {
+		return "select now()";
+	}
+
+	@Override
+	public boolean isCurrentTimestampSelectStringCallable() {
+		return false;
+	}
+
+	@Override
+	public boolean supportsEmptyInList() {
+		return false;
+	}
+
+	@Override
 	public boolean supportsIfExistsBeforeTableName() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
+	@Override
 	public LimitHandler buildLimitHandler(String sql, RowSelection selection) {
-        return new CUBRIDLimitHandler( this, sql, selection );
-    }
+		return new CUBRIDLimitHandler( this, sql, selection );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
index c2811e3b42..747fe4b4e2 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
@@ -1,694 +1,711 @@
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
 package org.hibernate.dialect;
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.ConditionalParenthesisFunction;
 import org.hibernate.dialect.function.ConvertFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardJDBCEscapeFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.exception.internal.CacheSQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.CacheJoinFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
- * Cach&eacute; 2007.1 dialect. This class is required in order to use Hibernate with Intersystems Cach&eacute; SQL.<br>
- * <br>
- * Compatible with Cach&eacute; 2007.1.
- * <br>
- * <head>
- * <title>Cach&eacute; and Hibernate</title>
- * </head>
- * <body>
- * <h1>Cach&eacute; and Hibernate</h1>
+ * Cach&eacute; 2007.1 dialect.
+ *
+ * This class is required in order to use Hibernate with Intersystems Cach&eacute; SQL.  Compatible with
+ * Cach&eacute; 2007.1.
+ *
  * <h2>PREREQUISITES</h2>
  * These setup instructions assume that both Cach&eacute; and Hibernate are installed and operational.
  * <br>
  * <h2>HIBERNATE DIRECTORIES AND FILES</h2>
  * JBoss distributes the InterSystems Cache' dialect for Hibernate 3.2.1
  * For earlier versions of Hibernate please contact
  * <a href="http://www.intersystems.com/support/cache-support.html">InterSystems Worldwide Response Center</A> (WRC)
  * for the appropriate source files.
  * <br>
  * <h2>CACH&Eacute; DOCUMENTATION</h2>
  * Documentation for Cach&eacute; is available online when Cach&eacute; is running.
  * It can also be obtained from the
  * <a href="http://www.intersystems.com/cache/downloads/documentation.html">InterSystems</A> website.
  * The book, "Object-oriented Application Development Using the Cach&eacute; Post-relational Database:
  * is also available from Springer-Verlag.
  * <br>
  * <h2>HIBERNATE DOCUMENTATION</h2>
  * Hibernate comes with extensive electronic documentation.
  * In addition, several books on Hibernate are available from
  * <a href="http://www.manning.com">Manning Publications Co</a>.
  * Three available titles are "Hibernate Quickly", "Hibernate in Action", and "Java Persistence with Hibernate".
  * <br>
  * <h2>TO SET UP HIBERNATE FOR USE WITH CACH&Eacute;</h2>
  * The following steps assume that the directory where Cach&eacute; was installed is C:\CacheSys.
  * This is the default installation directory for  Cach&eacute;.
  * The default installation directory for Hibernate is assumed to be C:\Hibernate.
  * <p/>
  * If either product is installed in a different location, the pathnames that follow should be modified appropriately.
  * <p/>
  * Cach&eacute; version 2007.1 and above is recommended for use with
  * Hibernate.  The next step depends on the location of your
  * CacheDB.jar depending on your version of Cach&eacute;.
  * <ol>
  * <li>Copy C:\CacheSys\dev\java\lib\JDK15\CacheDB.jar to C:\Hibernate\lib\CacheDB.jar.</li>
  * <p/>
  * <li>Insert the following files into your Java classpath:
  * <p/>
  * <ul>
  * <li>All jar files in the directory C:\Hibernate\lib</li>
  * <li>The directory (or directories) where hibernate.properties and/or hibernate.cfg.xml are kept.</li>
  * </ul>
  * </li>
  * <p/>
  * <li>In the file, hibernate.properties (or hibernate.cfg.xml),
  * specify the Cach&eacute; dialect and the Cach&eacute; version URL settings.</li>
  * </ol>
  * <p/>
  * For example, in Hibernate 3.2, typical entries in hibernate.properties would have the following
  * "name=value" pairs:
  * <p/>
  * <table cols=3 border cellpadding=5 cellspacing=0>
  * <tr>
  * <th>Property Name</th>
  * <th>Property Value</th>
  * </tr>
  * <tr>
  * <td>hibernate.dialect</td>
  * <td>org.hibernate.dialect.Cache71Dialect</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.driver_class</td>
  * <td>com.intersys.jdbc.CacheDriver</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.username</td>
  * <td>(see note 1)</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.password</td>
  * <td>(see note 1)</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.url</td>
  * <td>jdbc:Cache://127.0.0.1:1972/USER</td>
  * </tr>
  * </table>
  * <p/>
- * <dl>
- * <dt><b>Note 1</b></dt>
- * <dd>Please contact your administrator for the userid and password you should use when attempting access via JDBC.
- * By default, these are chosen to be "_SYSTEM" and "SYS" respectively as noted in the SQL standard.</dd>
- * </dl>
+ * <b>NOTE:</b> Please contact your administrator for the userid and password you should use when
+ *         attempting access via JDBC.  By default, these are chosen to be "_SYSTEM" and "SYS" respectively
+ *         as noted in the SQL standard.
  * <br>
  * <h2>CACH&Eacute; VERSION URL</h2>
  * This is the standard URL for the JDBC driver.
  * For a JDBC driver on the machine hosting Cach&eacute;, use the IP "loopback" address, 127.0.0.1.
  * For 1972, the default port, specify the super server port of your Cach&eacute; instance.
  * For USER, substitute the NAMESPACE which contains your Cach&eacute; database data.
  * <br>
  * <h2>CACH&Eacute; DIALECTS</h2>
  * Choices for Dialect are:
  * <br>
  * <p/>
  * <ol>
  * <li>org.hibernate.dialect.Cache71Dialect (requires Cach&eacute;
  * 2007.1 or above)</li>
  * <p/>
  * </ol>
  * <br>
  * <h2>SUPPORT FOR IDENTITY COLUMNS</h2>
  * Cach&eacute; 2007.1 or later supports identity columns.  For
  * Hibernate to use identity columns, specify "native" as the
  * generator.
  * <br>
  * <h2>SEQUENCE DIALECTS SUPPORT SEQUENCES</h2>
  * <p/>
  * To use Hibernate sequence support with Cach&eacute; in a namespace, you must FIRST load the following file into that namespace:
  * <pre>
  *     etc\CacheSequences.xml
  * </pre>
  * For example, at the COS terminal prompt in the namespace, run the
  * following command:
  * <p>
  * d LoadFile^%apiOBJ("c:\hibernate\etc\CacheSequences.xml","ck")
  * <p>
  * In your Hibernate mapping you can specify sequence use.
  * <p>
  * For example, the following shows the use of a sequence generator in a Hibernate mapping:
  * <pre>
  *     &lt;id name="id" column="uid" type="long" unsaved-value="null"&gt;
  *         &lt;generator class="sequence"/&gt;
  *     &lt;/id&gt;
  * </pre>
  * <br>
  * <p/>
  * Some versions of Hibernate under some circumstances call
  * getSelectSequenceNextValString() in the dialect.  If this happens
  * you will receive the error message: new MappingException( "Dialect
  * does not support sequences" ).
  * <br>
  * <h2>HIBERNATE FILES ASSOCIATED WITH CACH&Eacute; DIALECT</h2>
  * The following files are associated with Cach&eacute; dialect:
  * <p/>
  * <ol>
  * <li>src\org\hibernate\dialect\Cache71Dialect.java</li>
  * <li>src\org\hibernate\dialect\function\ConditionalParenthesisFunction.java</li>
  * <li>src\org\hibernate\dialect\function\ConvertFunction.java</li>
  * <li>src\org\hibernate\exception\CacheSQLStateConverter.java</li>
  * <li>src\org\hibernate\sql\CacheJoinFragment.java</li>
  * </ol>
  * Cache71Dialect ships with Hibernate 3.2.  All other dialects are distributed by InterSystems and subclass Cache71Dialect.
  *
  * @author Jonathan Levinson
  */
 
 public class Cache71Dialect extends Dialect {
 
 	/**
 	 * Creates new <code>Cache71Dialect</code> instance. Sets up the JDBC /
 	 * Cach&eacute; type mappings.
 	 */
 	public Cache71Dialect() {
 		super();
 		commonRegistration();
 		register71Functions();
 	}
 
 	protected final void commonRegistration() {
 		// Note: For object <-> SQL datatype mappings see:
 		//	 Configuration Manager | Advanced | SQL | System DDL Datatype Mappings
 		//
 		//	TBD	registerColumnType(Types.BINARY,        "binary($1)");
 		// changed 08-11-2005, jsl
 		registerColumnType( Types.BINARY, "varbinary($1)" );
 		registerColumnType( Types.BIGINT, "BigInt" );
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "decimal" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.INTEGER, "integer" );
-		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );	// binary %Stream
-		registerColumnType( Types.LONGVARCHAR, "longvarchar" );		// character %Stream
+		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
+		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.REAL, "real" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TINYINT, "tinyint" );
-		// TBD should this be varbinary($1)?
-		//		registerColumnType(Types.VARBINARY,     "binary($1)");
 		registerColumnType( Types.VARBINARY, "longvarbinary" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.BLOB, "longvarbinary" );
 		registerColumnType( Types.CLOB, "longvarchar" );
 
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "false" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
-		//getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
 
 		getDefaultProperties().setProperty( Environment.USE_SQL_COMMENTS, "false" );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "acos", new StandardJDBCEscapeFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "%alphaup", new StandardSQLFunction( "%alphaup", StandardBasicTypes.STRING ) );
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.STRING ) );
 		registerFunction( "asin", new StandardJDBCEscapeFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardJDBCEscapeFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "($length(?1)*8)" ) );
-		// hibernate impelemnts cast in Dialect.java
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardJDBCEscapeFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "cos", new StandardJDBCEscapeFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardJDBCEscapeFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "coalesce", new VarArgsSQLFunction( "coalesce(", ",", ")" ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
 		registerFunction( "convert", new ConvertFunction() );
 		registerFunction( "curdate", new StandardJDBCEscapeFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
 		registerFunction(
 				"current_timestamp", new ConditionalParenthesisFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP )
 		);
 		registerFunction( "curtime", new StandardJDBCEscapeFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "database", new StandardJDBCEscapeFunction( "database", StandardBasicTypes.STRING ) );
 		registerFunction( "dateadd", new VarArgsSQLFunction( StandardBasicTypes.TIMESTAMP, "dateadd(", ",", ")" ) );
 		registerFunction( "datediff", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "datediff(", ",", ")" ) );
 		registerFunction( "datename", new VarArgsSQLFunction( StandardBasicTypes.STRING, "datename(", ",", ")" ) );
 		registerFunction( "datepart", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "datepart(", ",", ")" ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardJDBCEscapeFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofmonth", new StandardJDBCEscapeFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek", new StandardJDBCEscapeFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardJDBCEscapeFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		// is it necessary to register %exact since it can only appear in a where clause?
 		registerFunction( "%exact", new StandardSQLFunction( "%exact", StandardBasicTypes.STRING ) );
 		registerFunction( "exp", new StandardJDBCEscapeFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "%external", new StandardSQLFunction( "%external", StandardBasicTypes.STRING ) );
 		registerFunction( "$extract", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "$extract(", ",", ")" ) );
 		registerFunction( "$find", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "$find(", ",", ")" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.INTEGER ) );
 		registerFunction( "getdate", new StandardSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "hour", new StandardJDBCEscapeFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "ifnull", new VarArgsSQLFunction( "ifnull(", ",", ")" ) );
 		registerFunction( "%internal", new StandardSQLFunction( "%internal" ) );
 		registerFunction( "isnull", new VarArgsSQLFunction( "isnull(", ",", ")" ) );
 		registerFunction( "isnumeric", new StandardSQLFunction( "isnumeric", StandardBasicTypes.INTEGER ) );
 		registerFunction( "lcase", new StandardJDBCEscapeFunction( "lcase", StandardBasicTypes.STRING ) );
 		registerFunction( "left", new StandardJDBCEscapeFunction( "left", StandardBasicTypes.STRING ) );
 		registerFunction( "len", new StandardSQLFunction( "len", StandardBasicTypes.INTEGER ) );
 		registerFunction( "$length", new VarArgsSQLFunction( "$length(", ",", ")" ) );
-		// aggregate functions shouldn't be registered, right?
-		//registerFunction( "list", new StandardSQLFunction("list",StandardBasicTypes.STRING) );
-		// stopped on $list
 		registerFunction( "$list", new VarArgsSQLFunction( "$list(", ",", ")" ) );
 		registerFunction( "$listdata", new VarArgsSQLFunction( "$listdata(", ",", ")" ) );
 		registerFunction( "$listfind", new VarArgsSQLFunction( "$listfind(", ",", ")" ) );
 		registerFunction( "$listget", new VarArgsSQLFunction( "$listget(", ",", ")" ) );
 		registerFunction( "$listlength", new StandardSQLFunction( "$listlength", StandardBasicTypes.INTEGER ) );
 		registerFunction( "locate", new StandardSQLFunction( "$FIND", StandardBasicTypes.INTEGER ) );
 		registerFunction( "log", new StandardJDBCEscapeFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardJDBCEscapeFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "minute", new StandardJDBCEscapeFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "mod", new StandardJDBCEscapeFunction( "mod", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "month", new StandardJDBCEscapeFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardJDBCEscapeFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "now", new StandardJDBCEscapeFunction( "monthname", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "nullif", new VarArgsSQLFunction( "nullif(", ",", ")" ) );
 		registerFunction( "nvl", new NvlFunction() );
 		registerFunction( "%odbcin", new StandardSQLFunction( "%odbcin" ) );
 		registerFunction( "%odbcout", new StandardSQLFunction( "%odbcin" ) );
 		registerFunction( "%pattern", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "%pattern", "" ) );
 		registerFunction( "pi", new StandardJDBCEscapeFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "$piece", new VarArgsSQLFunction( StandardBasicTypes.STRING, "$piece(", ",", ")" ) );
 		registerFunction( "position", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "position(", " in ", ")" ) );
 		registerFunction( "power", new VarArgsSQLFunction( StandardBasicTypes.STRING, "power(", ",", ")" ) );
 		registerFunction( "quarter", new StandardJDBCEscapeFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "repeat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "repeat(", ",", ")" ) );
 		registerFunction( "replicate", new VarArgsSQLFunction( StandardBasicTypes.STRING, "replicate(", ",", ")" ) );
 		registerFunction( "right", new StandardJDBCEscapeFunction( "right", StandardBasicTypes.STRING ) );
 		registerFunction( "round", new VarArgsSQLFunction( StandardBasicTypes.FLOAT, "round(", ",", ")" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
 		registerFunction( "second", new StandardJDBCEscapeFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sin", new StandardJDBCEscapeFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "%sqlstring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%sqlstring(", ",", ")" ) );
 		registerFunction( "%sqlupper", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%sqlupper(", ",", ")" ) );
 		registerFunction( "sqrt", new StandardJDBCEscapeFunction( "SQRT", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "%startswith", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "%startswith", "" ) );
 		// below is for Cache' that don't have str in 2007.1 there is str and we register str directly
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "cast(?1 as char varying)" ) );
 		registerFunction( "string", new VarArgsSQLFunction( StandardBasicTypes.STRING, "string(", ",", ")" ) );
 		// note that %string is deprecated
 		registerFunction( "%string", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%string(", ",", ")" ) );
 		registerFunction( "substr", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substr(", ",", ")" ) );
 		registerFunction( "substring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substring(", ",", ")" ) );
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "tan", new StandardJDBCEscapeFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "timestampadd", new StandardJDBCEscapeFunction( "timestampadd", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "timestampdiff", new StandardJDBCEscapeFunction( "timestampdiff", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tochar", new VarArgsSQLFunction( StandardBasicTypes.STRING, "tochar(", ",", ")" ) );
 		registerFunction( "to_char", new VarArgsSQLFunction( StandardBasicTypes.STRING, "to_char(", ",", ")" ) );
 		registerFunction( "todate", new VarArgsSQLFunction( StandardBasicTypes.STRING, "todate(", ",", ")" ) );
 		registerFunction( "to_date", new VarArgsSQLFunction( StandardBasicTypes.STRING, "todate(", ",", ")" ) );
 		registerFunction( "tonumber", new StandardSQLFunction( "tonumber" ) );
 		registerFunction( "to_number", new StandardSQLFunction( "tonumber" ) );
 		// TRIM(end_keyword string-expression-1 FROM string-expression-2)
 		// use Hibernate implementation "From" is one of the parameters they pass in position ?3
 		//registerFunction( "trim", new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?1 ?2 from ?3)") );
 		registerFunction( "truncate", new StandardJDBCEscapeFunction( "truncate", StandardBasicTypes.STRING ) );
 		registerFunction( "ucase", new StandardJDBCEscapeFunction( "ucase", StandardBasicTypes.STRING ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		// %upper is deprecated
 		registerFunction( "%upper", new StandardSQLFunction( "%upper" ) );
 		registerFunction( "user", new StandardJDBCEscapeFunction( "user", StandardBasicTypes.STRING ) );
 		registerFunction( "week", new StandardJDBCEscapeFunction( "user", StandardBasicTypes.INTEGER ) );
 		registerFunction( "xmlconcat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "xmlconcat(", ",", ")" ) );
 		registerFunction( "xmlelement", new VarArgsSQLFunction( StandardBasicTypes.STRING, "xmlelement(", ",", ")" ) );
 		// xmlforest requires a new kind of function constructor
 		registerFunction( "year", new StandardJDBCEscapeFunction( "year", StandardBasicTypes.INTEGER ) );
 	}
 
 	protected final void register71Functions() {
 		this.registerFunction( "str", new VarArgsSQLFunction( StandardBasicTypes.STRING, "str(", ",", ")" ) );
 	}
 
 	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public boolean hasAlterTable() {
 		// Does this dialect support the ALTER TABLE syntax?
 		return true;
 	}
 
+	@Override
 	public boolean qualifyIndexName() {
 		// Do we need to qualify index names with the schema name?
 		return false;
 	}
 
-	/**
-	 * The syntax used to add a foreign key constraint to a table.
-	 *
-	 * @return String
-	 */
+	@Override
+	@SuppressWarnings("StringBufferReplaceableByString")
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		// The syntax used to add a foreign key constraint to a table.
 		return new StringBuilder( 300 )
 				.append( " ADD CONSTRAINT " )
 				.append( constraintName )
 				.append( " FOREIGN KEY " )
 				.append( constraintName )
 				.append( " (" )
-				.append( StringHelper.join( ", ", foreignKey ) )	// identifier-commalist
+				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") REFERENCES " )
 				.append( referencedTable )
 				.append( " (" )
-				.append( StringHelper.join( ", ", primaryKey ) ) // identifier-commalist
+				.append( StringHelper.join( ", ", primaryKey ) )
 				.append( ") " )
 				.toString();
 	}
 
+	/**
+	 * Does this dialect support check constraints?
+	 *
+	 * @return {@code false} (Cache does not support check constraints)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public boolean supportsCheck() {
-		// Does this dialect support check constraints?
 		return false;
 	}
 
+	@Override
 	public String getAddColumnString() {
 		// The syntax used to add a column to a table
 		return " add column";
 	}
 
+	@Override
 	public String getCascadeConstraintsString() {
 		// Completely optional cascading drop clause.
 		return "";
 	}
 
+	@Override
 	public boolean dropConstraints() {
 		// Do we need to drop constraints before dropping tables in this dialect?
 		return true;
 	}
 
+	@Override
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
+	@Override
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return true;
 	}
 
+
 	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
+	@Override
 	public String generateTemporaryTableName(String baseTableName) {
-		String name = super.generateTemporaryTableName( baseTableName );
+		final String name = super.generateTemporaryTableName( baseTableName );
 		return name.length() > 25 ? name.substring( 1, 25 ) : name;
 	}
 
+	@Override
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
+	@Override
 	public Boolean performTemporaryTableDDLInIsolation() {
 		return Boolean.FALSE;
 	}
 
+	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "";
 	}
 
+	@Override
 	public boolean dropTemporaryTableAfterUse() {
 		return true;
 	}
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
+	@Override
 	public Class getNativeIdentifierGeneratorClass() {
 		return IdentityGenerator.class;
 	}
 
+	@Override
 	public boolean hasDataTypeInIdentityColumn() {
 		// Whether this dialect has an Identity clause added to the data type or a completely seperate identity
 		// data type
 		return true;
 	}
 
+	@Override
 	public String getIdentityColumnString() throws MappingException {
 		// The keyword used to specify an identity column, if identity column key generation is supported.
 		return "identity";
 	}
 
+	@Override
 	public String getIdentitySelectString() {
 		return "SELECT LAST_IDENTITY() FROM %TSQL_sys.snf";
 	}
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public boolean supportsSequences() {
 		return false;
 	}
 
 // It really does support sequences, but InterSystems elects to suggest usage of IDENTITY instead :/
 // Anyway, below are the actual support overrides for users wanting to use this combo...
 //
 //	public String getSequenceNextValString(String sequenceName) {
 //		return "select InterSystems.Sequences_GetNext('" + sequenceName + "') from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "')";
 //	}
 //
 //	public String getSelectSequenceNextValString(String sequenceName) {
 //		return "(select InterSystems.Sequences_GetNext('" + sequenceName + "') from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "'))";
 //	}
 //
 //	public String getCreateSequenceString(String sequenceName) {
 //		return "insert into InterSystems.Sequences(Name) values (ucase('" + sequenceName + "'))";
 //	}
 //
 //	public String getDropSequenceString(String sequenceName) {
 //		return "delete from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "')";
 //	}
 //
 //	public String getQuerySequencesString() {
 //		return "select name from InterSystems.Sequences";
 //	}
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	public boolean supportsForUpdate() {
-		// Does this dialect support the FOR UPDATE syntax?
-		return false;
-	}
-
-	public boolean supportsForUpdateOf() {
-		// Does this dialect support FOR UPDATE OF, allowing particular rows to be locked?
-		return false;
-	}
-
-	public boolean supportsForUpdateNowait() {
-		// Does this dialect support the Oracle-style FOR UPDATE NOWAIT syntax?
-		return false;
-	}
-
+	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
+	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// InterSystems Cache' does not current support "SELECT ... FOR UPDATE" syntax...
 		// Set your transaction mode to READ_COMMITTED before using
 		if ( lockMode==LockMode.PESSIMISTIC_FORCE_INCREMENT) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_WRITE) {
 			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_READ) {
 			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC) {
 			return new OptimisticLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC_FORCE_INCREMENT) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
 	// LIMIT support (ala TOP) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
+	@SuppressWarnings("deprecation")
 	public boolean supportsLimit() {
 		return true;
 	}
 
+	@Override
+	@SuppressWarnings("deprecation")
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
+	@Override
+	@SuppressWarnings("deprecation")
 	public boolean supportsVariableLimit() {
 		return true;
 	}
 
+	@Override
+	@SuppressWarnings("deprecation")
 	public boolean bindLimitParametersFirst() {
 		// Does the LIMIT clause come at the start of the SELECT statement, rather than at the end?
 		return true;
 	}
 
+	@Override
+	@SuppressWarnings("deprecation")
 	public boolean useMaxForLimit() {
 		// Does the LIMIT clause take a "maximum" row number instead of a total number of returned rows?
 		return true;
 	}
 
+	@Override
+	@SuppressWarnings("deprecation")
 	public String getLimitString(String sql, boolean hasOffset) {
 		if ( hasOffset ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 
 		// This does not support the Cache SQL 'DISTINCT BY (comma-list)' extensions,
 		// but this extension is not supported through Hibernate anyway.
-		int insertionPoint = sql.startsWith( "select distinct" ) ? 15 : 6;
+		final int insertionPoint = sql.startsWith( "select distinct" ) ? 15 : 6;
 
 		return new StringBuilder( sql.length() + 8 )
 				.append( sql )
 				.insert( insertionPoint, " TOP ? " )
 				.toString();
 	}
 
 	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		return col;
 	}
 
+	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
-		return ( ResultSet ) ps.getObject( 1 );
+		return (ResultSet) ps.getObject( 1 );
 	}
 
 	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public String getLowercaseFunction() {
 		// The name of the SQL function that transforms a string to lowercase
 		return "lower";
 	}
 
+	@Override
 	public String getNullColumnString() {
 		// The keyword used to specify a nullable column.
 		return " null";
 	}
 
+	@Override
 	public JoinFragment createOuterJoinFragment() {
 		// Create an OuterJoinGenerator for this dialect.
 		return new CacheJoinFragment();
 	}
 
+	@Override
 	public String getNoColumnsInsertString() {
 		// The keyword used to insert a row without specifying
 		// any column values
 		return " default values";
 	}
 
+	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new CacheSQLExceptionConversionDelegate( this );
 	}
 
+	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
+	/**
+	 * The Cache ViolatedConstraintNameExtracter.
+	 */
 	public static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
-		/**
-		 * Extract the name of the violated constraint from the given SQLException.
-		 *
-		 * @param sqle The exception that was the result of the constraint violation.
-		 * @return The extracted constraint name.
-		 */
+		@Override
 		public String extractConstraintName(SQLException sqle) {
 			return extractUsingTemplate( "constraint (", ") violated", sqle.getMessage() );
 		}
 	};
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
+	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/ColumnAliasExtractor.java b/hibernate-core/src/main/java/org/hibernate/dialect/ColumnAliasExtractor.java
index e00d6522d3..0ac51f6dad 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/ColumnAliasExtractor.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/ColumnAliasExtractor.java
@@ -1,67 +1,70 @@
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
 package org.hibernate.dialect;
 
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 
 /**
  * Strategy for extracting the unique column alias out of a {@link ResultSetMetaData}.  This is used during the
  * "auto discovery" phase of native SQL queries.
  * <p/>
  * Generally this should be done via {@link ResultSetMetaData#getColumnLabel}, but not all drivers do this correctly.
  *
  * @author Steve Ebersole
  */
 public interface ColumnAliasExtractor {
 	/**
 	 * Extract the unique column alias.
 	 *
 	 * @param metaData The result set metadata
 	 * @param position The column position
 	 *
 	 * @return The alias
+	 *
+	 * @throws SQLException Indicates a problem accessing the JDBC ResultSetMetaData
 	 */
 	public String extractColumnAlias(ResultSetMetaData metaData, int position) throws SQLException;
 
 	/**
 	 * An extractor which uses {@link ResultSetMetaData#getColumnLabel}
 	 */
 	public static final ColumnAliasExtractor COLUMN_LABEL_EXTRACTOR = new ColumnAliasExtractor() {
 		@Override
 		public String extractColumnAlias(ResultSetMetaData metaData, int position) throws SQLException {
 			return metaData.getColumnLabel( position );
 		}
 	};
 
 	/**
 	 * An extractor which uses {@link ResultSetMetaData#getColumnName}
 	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public static final ColumnAliasExtractor COLUMN_NAME_EXTRACTOR = new ColumnAliasExtractor() {
 		@Override
 		public String extractColumnAlias(ResultSetMetaData metaData, int position) throws SQLException {
 			return metaData.getColumnName( position );
 		}
 	};
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
index d4cfb0ff0b..d4fdf523a0 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
@@ -1,74 +1,76 @@
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
 package org.hibernate.dialect;
 
 
 /**
  * An SQL dialect for DB2/390. This class provides support for
  * DB2 Universal Database for OS/390, also known as DB2/390.
  *
  * @author Kristoffer Dyrkorn
  */
 public class DB2390Dialect extends DB2Dialect {
-
+	@Override
 	public boolean supportsSequences() {
 		return false;
 	}
 
+	@Override
 	public String getIdentitySelectString() {
 		return "select identity_val_local() from sysibm.sysdummy1";
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
+	@Override
+	@SuppressWarnings("deprecation")
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
+	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
+	@Override
 	public String getLimitString(String sql, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		if ( limit == 0 ) {
 			return sql;
 		}
-		return new StringBuilder( sql.length() + 40 )
-				.append( sql )
-				.append( " fetch first " )
-				.append( limit )
-				.append( " rows only " )
-				.toString();
+		return sql + " fetch first " + limit + " rows only ";
 	}
 
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java
index 4f93f7af25..5aa87d9d7c 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java
@@ -1,77 +1,79 @@
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
 package org.hibernate.dialect;
 
-
 /**
  * An SQL dialect for DB2/400.  This class provides support for DB2 Universal Database for iSeries,
  * also known as DB2/400.
  *
  * @author Peter DeGregorio (pdegregorio)
  */
 public class DB2400Dialect extends DB2Dialect {
-
+	@Override
 	public boolean supportsSequences() {
 		return false;
 	}
 
+	@Override
 	public String getIdentitySelectString() {
 		return "select identity_val_local() from sysibm.sysdummy1";
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
+	@Override
+	@SuppressWarnings("deprecation")
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
+	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
+	@Override
 	public String getLimitString(String sql, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		if ( limit == 0 ) {
 			return sql;
 		}
-		return new StringBuilder( sql.length() + 40 )
-				.append( sql )
-				.append( " fetch first " )
-				.append( limit )
-				.append( " rows only " )
-				.toString();
+		return sql + " fetch first " + limit + " rows only ";
 	}
 
+	@Override
 	public String getForUpdateString() {
 		return " for update with rs";
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
index 2e3397c464..f70de19015 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
@@ -1,471 +1,488 @@
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
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.JDBCException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.unique.DB2UniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * An SQL dialect for DB2.
  *
  * @author Gavin King
  */
 public class DB2Dialect extends Dialect {
-	
 	private final UniqueDelegate uniqueDelegate;
 
+	/**
+	 * Constructs a DB2Dialect
+	 */
 	public DB2Dialect() {
 		super();
 		registerColumnType( Types.BIT, "smallint" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "varchar($l) for bit data" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.BLOB, "blob($l)" );
 		registerColumnType( Types.CLOB, "clob($l)" );
 		registerColumnType( Types.LONGVARCHAR, "long varchar" );
 		registerColumnType( Types.LONGVARBINARY, "long varchar for bit data" );
 		registerColumnType( Types.BINARY, "varchar($l) for bit data" );
 		registerColumnType( Types.BINARY, 254, "char($l) for bit data" );
 		registerColumnType( Types.BOOLEAN, "smallint" );
 
 		registerFunction( "avg", new AvgWithArgumentCastFunction( "double" ) );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "absval", new StandardSQLFunction( "absval" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling" ) );
 		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "float", new StandardSQLFunction( "float", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "hex", new StandardSQLFunction( "hex", StandardBasicTypes.STRING ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "stddev", new StandardSQLFunction( "stddev", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "variance", new StandardSQLFunction( "variance", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "julian_day", new StandardSQLFunction( "julian_day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "microsecond", new StandardSQLFunction( "microsecond", StandardBasicTypes.INTEGER ) );
 		registerFunction(
 				"midnight_seconds",
 				new StandardSQLFunction( "midnight_seconds", StandardBasicTypes.INTEGER )
 		);
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "date", new StandardSQLFunction( "date", StandardBasicTypes.DATE ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek_iso", new StandardSQLFunction( "dayofweek_iso", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "days", new StandardSQLFunction( "days", StandardBasicTypes.LONG ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current time", StandardBasicTypes.TIME, false ) );
 		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
 		registerFunction(
 				"current_timestamp",
 				new NoArgSQLFunction( "current timestamp", StandardBasicTypes.TIMESTAMP, false )
 		);
 		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "timestamp_iso", new StandardSQLFunction( "timestamp_iso", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 		registerFunction( "week_iso", new StandardSQLFunction( "week_iso", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "double", new StandardSQLFunction( "double", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "varchar", new StandardSQLFunction( "varchar", StandardBasicTypes.STRING ) );
 		registerFunction( "real", new StandardSQLFunction( "real", StandardBasicTypes.FLOAT ) );
 		registerFunction( "bigint", new StandardSQLFunction( "bigint", StandardBasicTypes.LONG ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "integer", new StandardSQLFunction( "integer", StandardBasicTypes.INTEGER ) );
 		registerFunction( "smallint", new StandardSQLFunction( "smallint", StandardBasicTypes.SHORT ) );
 
 		registerFunction( "digits", new StandardSQLFunction( "digits", StandardBasicTypes.STRING ) );
 		registerFunction( "chr", new StandardSQLFunction( "chr", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "posstr", new StandardSQLFunction( "posstr", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "length(?1)*8" ) );
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "trim(?1 ?2 ?3 ?4)" ) );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
 
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "rtrim(char(?1))" ) );
 
 		registerKeyword( "current" );
 		registerKeyword( "date" );
 		registerKeyword( "time" );
 		registerKeyword( "timestamp" );
 		registerKeyword( "fetch" );
 		registerKeyword( "first" );
 		registerKeyword( "rows" );
 		registerKeyword( "only" );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, NO_BATCH );
 		
 		uniqueDelegate = new DB2UniqueDelegate( this );
 	}
+
 	@Override
 	public String getLowercaseFunction() {
 		return "lcase";
 	}
+
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
+
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
+
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
+
 	@Override
 	public String getIdentitySelectString() {
 		return "values identity_val_local()";
 	}
+
 	@Override
 	public String getIdentityColumnString() {
-		return "generated by default as identity"; //not null ... (start with 1) is implicit
+		return "generated by default as identity";
 	}
+
 	@Override
 	public String getIdentityInsertString() {
 		return "default";
 	}
+
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "values nextval for " + sequenceName;
 	}
+
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
+
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
+
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
+
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
+
 	@Override
 	public String getQuerySequencesString() {
 		return "select seqname from sysibm.syssequences";
 	}
+
 	@Override
+	@SuppressWarnings("deprecation")
 	public boolean supportsLimit() {
 		return true;
 	}
+
 	@Override
+	@SuppressWarnings("deprecation")
 	public boolean supportsVariableLimit() {
 		return false;
 	}
+
 	@Override
+	@SuppressWarnings("deprecation")
 	public String getLimitString(String sql, int offset, int limit) {
 		if ( offset == 0 ) {
 			return sql + " fetch first " + limit + " rows only";
 		}
-		StringBuilder pagingSelect = new StringBuilder( sql.length() + 200 )
-				.append(
-						"select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( "
-				)
-				.append( sql )  //nest the main query in an outer select
-				.append( " fetch first " )
-				.append( limit )
-				.append( " rows only ) as inner2_ ) as inner1_ where rownumber_ > " )
-				.append( offset )
-				.append( " order by rownumber_" );
-		return pagingSelect.toString();
+		//nest the main query in an outer select
+		return "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( "
+				+ sql + " fetch first " + limit + " rows only ) as inner2_ ) as inner1_ where rownumber_ > "
+				+ offset + " order by rownumber_";
 	}
 
 	/**
+	 * {@inheritDoc}
+	 * <p/>
+	 *
 	 * DB2 does have a one-based offset, however this was actually already handled in the limit string building
 	 * (the '?+1' bit).  To not mess up inheritors, I'll leave that part alone and not touch the offset here.
-	 *
-	 * @param zeroBasedFirstResult The user-supplied, zero-based offset
-	 *
-	 * @return zeroBasedFirstResult
 	 */
 	@Override
+	@SuppressWarnings("deprecation")
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
+
 	@Override
+	@SuppressWarnings("deprecation")
 	public String getForUpdateString() {
 		return " for read only with rs use and keep update locks";
 	}
+
 	@Override
+	@SuppressWarnings("deprecation")
 	public boolean useMaxForLimit() {
 		return true;
 	}
+
 	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
+
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
-	//as far as I know, DB2 doesn't support this
+
 	@Override
 	public boolean supportsLockTimeouts() {
+		//as far as I know, DB2 doesn't support this
 		return false;
 	}
+
 	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		String literal;
 		switch ( sqlType ) {
 			case Types.VARCHAR:
 			case Types.CHAR:
 				literal = "'x'";
 				break;
 			case Types.DATE:
 				literal = "'2000-1-1'";
 				break;
 			case Types.TIMESTAMP:
 				literal = "'2000-1-1 00:00:00'";
 				break;
 			case Types.TIME:
 				literal = "'00:00:00'";
 				break;
 			default:
 				literal = "0";
 		}
 		return "nullif(" + literal + ',' + literal + ')';
 	}
 
-	public static void main(String[] args) {
-		System.out.println( new DB2Dialect().getLimitString( "/*foo*/ select * from foos", true ) );
-		System.out.println( new DB2Dialect().getLimitString( "/*foo*/ select distinct * from foos", true ) );
-		System.out
-				.println(
-						new DB2Dialect().getLimitString(
-								"/*foo*/ select * from foos foo order by foo.bar, foo.baz",
-								true
-						)
-				);
-		System.out
-				.println(
-						new DB2Dialect().getLimitString(
-								"/*foo*/ select distinct * from foos foo order by foo.bar, foo.baz",
-								true
-						)
-				);
-	}
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
+
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		return col;
 	}
+
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		boolean isResultSet = ps.execute();
 		// This assumes you will want to ignore any update counts 
 		while ( !isResultSet && ps.getUpdateCount() != -1 ) {
 			isResultSet = ps.getMoreResults();
 		}
-		ResultSet rs = ps.getResultSet();
-		// You may still have other ResultSets or update counts left to process here 
-		// but you can't do it now or the ResultSet you just got will be closed 
-		return rs;
+
+		return ps.getResultSet();
 	}
+
 	@Override
 	public boolean supportsCommentOn() {
 		return true;
 	}
+
 	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
+
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "declare global temporary table";
 	}
+
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "not logged";
 	}
+
 	@Override
 	public String generateTemporaryTableName(String baseTableName) {
 		return "session." + super.generateTemporaryTableName( baseTableName );
 	}
+
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
+
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "values current timestamp";
 	}
+
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
-	 * DB2 is know to support parameters in the <tt>SELECT</tt> clause, but only in casted form
+	 * NOTE : DB2 is know to support parameters in the <tt>SELECT</tt> clause, but only in casted form
 	 * (see {@link #requiresCastingOfParametersInSelectClause()}).
-	 *
-	 * @return True.
 	 */
 	@Override
 	public boolean supportsParametersInInsertSelect() {
 		return true;
 	}
 
 	/**
+	 * {@inheritDoc}
+	 * <p/>
 	 * DB2 in fact does require that parameters appearing in the select clause be wrapped in cast() calls
 	 * to tell the DB parser the type of the select value.
-	 *
-	 * @return True.
 	 */
 	@Override
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return true;
 	}
+
 	@Override
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return false;
 	}
 
-	//DB2 v9.1 doesn't support 'cross join' syntax
 	@Override
 	public String getCrossJoinSeparator() {
+		//DB2 v9.1 doesn't support 'cross join' syntax
 		return ", ";
 	}
+
+
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
+
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
+
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return true;
 	}
+
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	@Override
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		return sqlCode == Types.BOOLEAN ? SmallIntTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride( sqlCode );
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				final String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 
 				if( -952 == errorCode && "57014".equals( sqlState )){
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				return null;
 			}
 		};
 	}
 	
 	@Override
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
 	
 	@Override
 	public String getNotExpression( String expression ) {
 		return "not (" + expression + ")";
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DataDirectOracle9Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DataDirectOracle9Dialect.java
index 65d99bd684..90f9e3af80 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DataDirectOracle9Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DataDirectOracle9Dialect.java
@@ -1,47 +1,50 @@
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
 package org.hibernate.dialect;
+
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
+/**
+ * A Dialect for accessing Oracle through DataDirect driver
+ */
+@SuppressWarnings("deprecation")
 public class DataDirectOracle9Dialect extends Oracle9Dialect {
-	
+	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
-		return col; // sql server just returns automatically
+		return col;
 	}
-	
+
+	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
-		boolean isResultSet = ps.execute(); 
-//		 This assumes you will want to ignore any update counts 
+		boolean isResultSet = ps.execute();
+		// This assumes you will want to ignore any update counts
 		while (!isResultSet && ps.getUpdateCount() != -1) { 
-		    isResultSet = ps.getMoreResults(); 
-		} 
-		ResultSet rs = ps.getResultSet(); 
-//		 You may still have other ResultSets or update counts left to process here 
-//		 but you can't do it now or the ResultSet you just got will be closed 
-		return rs;
-	}
+			isResultSet = ps.getMoreResults();
+		}
 
+		return ps.getResultSet();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DerbyDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DerbyDialect.java
index deab2055b5..5a2b793e45 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DerbyDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DerbyDialect.java
@@ -1,256 +1,261 @@
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
 package org.hibernate.dialect;
 
 import java.lang.reflect.Method;
 import java.sql.Types;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.MappingException;
 import org.hibernate.dialect.function.AnsiTrimFunction;
 import org.hibernate.dialect.function.DerbyConcatFunction;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DerbyCaseFragment;
 
 /**
  * Hibernate Dialect for Cloudscape 10 - aka Derby. This implements both an
  * override for the identity column generator as well as for the case statement
  * issue documented at:
  * http://www.jroller.com/comments/kenlars99/Weblog/cloudscape_soon_to_be_derby
  *
  * @author Simon Johnston
  *
  * @deprecated HHH-6073
  */
 @Deprecated
 public class DerbyDialect extends DB2Dialect {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, DerbyDialect.class.getName());
+	@SuppressWarnings("deprecation")
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			DerbyDialect.class.getName()
+	);
 
 	private int driverVersionMajor;
 	private int driverVersionMinor;
 
+	/**
+	 * Constructs a DerbyDialect
+	 */
+	@SuppressWarnings("deprecation")
 	public DerbyDialect() {
 		super();
-		if (this.getClass() == DerbyDialect.class) {
+		if ( this.getClass() == DerbyDialect.class ) {
 			LOG.deprecatedDerbyDialect();
 		}
+
 		registerFunction( "concat", new DerbyConcatFunction() );
 		registerFunction( "trim", new AnsiTrimFunction() );
-        registerColumnType( Types.BLOB, "blob" );
-        determineDriverVersion();
+		registerColumnType( Types.BLOB, "blob" );
+		determineDriverVersion();
 
-        if ( driverVersionMajor > 10 || ( driverVersionMajor == 10 && driverVersionMinor >= 7 ) ) {
-            registerColumnType( Types.BOOLEAN, "boolean" );
-        }
+		if ( driverVersionMajor > 10 || ( driverVersionMajor == 10 && driverVersionMinor >= 7 ) ) {
+			registerColumnType( Types.BOOLEAN, "boolean" );
+		}
 	}
 
-	@SuppressWarnings({ "UnnecessaryUnboxing" })
+	@SuppressWarnings({"UnnecessaryUnboxing", "unchecked"})
 	private void determineDriverVersion() {
 		try {
 			// locate the derby sysinfo class and query its version info
 			final Class sysinfoClass = ReflectHelper.classForName( "org.apache.derby.tools.sysinfo", this.getClass() );
 			final Method majorVersionGetter = sysinfoClass.getMethod( "getMajorVersion", ReflectHelper.NO_PARAM_SIGNATURE );
 			final Method minorVersionGetter = sysinfoClass.getMethod( "getMinorVersion", ReflectHelper.NO_PARAM_SIGNATURE );
 			driverVersionMajor = ( (Integer) majorVersionGetter.invoke( null, ReflectHelper.NO_PARAMS ) ).intValue();
 			driverVersionMinor = ( (Integer) minorVersionGetter.invoke( null, ReflectHelper.NO_PARAMS ) ).intValue();
 		}
 		catch ( Exception e ) {
 			LOG.unableToLoadDerbyDriver( e.getMessage() );
 			driverVersionMajor = -1;
 			driverVersionMinor = -1;
 		}
 	}
 
 	private boolean isTenPointFiveReleaseOrNewer() {
 		return driverVersionMajor > 10 || ( driverVersionMajor == 10 && driverVersionMinor >= 5 );
 	}
 
 	@Override
-    public String getCrossJoinSeparator() {
+	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
-	/**
-	 * Return the case statement modified for Cloudscape.
-	 */
 	@Override
-    public CaseFragment createCaseFragment() {
+	public CaseFragment createCaseFragment() {
 		return new DerbyCaseFragment();
 	}
 
 	@Override
-    public boolean dropConstraints() {
-	      return true;
+	public boolean dropConstraints() {
+		return true;
 	}
 
 	@Override
-    public boolean supportsSequences() {
+	public boolean supportsSequences() {
 		// technically sequence support was added in 10.6.1.0...
 		//
 		// The problem though is that I am not exactly sure how to differentiate 10.6.1.0 from any other 10.6.x release.
 		//
 		// http://db.apache.org/derby/docs/10.0/publishedapi/org/apache/derby/tools/sysinfo.html seems incorrect.  It
 		// states that derby's versioning scheme is major.minor.maintenance, but obviously 10.6.1.0 has 4 components
 		// to it, not 3.
 		//
 		// Let alone the fact that it states that versions with the matching major.minor are 'feature
 		// compatible' which is clearly not the case here (sequence support is a new feature...)
 		return driverVersionMajor > 10 || ( driverVersionMajor == 10 && driverVersionMinor >= 6 );
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		if ( supportsSequences() ) {
 			return "values next value for " + sequenceName;
 		}
 		else {
 			throw new MappingException( "Derby does not support sequence prior to release 10.6.1.0" );
 		}
 	}
 
 	@Override
-    public boolean supportsLimit() {
+	public boolean supportsLimit() {
 		return isTenPointFiveReleaseOrNewer();
 	}
 
-	//HHH-4531
 	@Override
-    public boolean supportsCommentOn() {
+	public boolean supportsCommentOn() {
+		//HHH-4531
 		return false;
 	}
 
 	@Override
-    public boolean supportsLimitOffset() {
+	@SuppressWarnings("deprecation")
+	public boolean supportsLimitOffset() {
 		return isTenPointFiveReleaseOrNewer();
 	}
 
-   @Override
-public String getForUpdateString() {
+	@Override
+	public String getForUpdateString() {
 		return " for update with rs";
-   }
+	}
 
 	@Override
-    public String getWriteLockString(int timeout) {
+	public String getWriteLockString(int timeout) {
 		return " for update with rs";
 	}
 
 	@Override
-    public String getReadLockString(int timeout) {
+	public String getReadLockString(int timeout) {
 		return " for read only with rs";
 	}
 
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * From Derby 10.5 Docs:
 	 * <pre>
 	 * Query
 	 * [ORDER BY clause]
 	 * [result offset clause]
 	 * [fetch first clause]
 	 * [FOR UPDATE clause]
 	 * [WITH {RR|RS|CS|UR}]
 	 * </pre>
 	 */
 	@Override
-    public String getLimitString(String query, final int offset, final int limit) {
-		StringBuilder sb = new StringBuilder(query.length() + 50);
-
+	public String getLimitString(String query, final int offset, final int limit) {
+		final StringBuilder sb = new StringBuilder(query.length() + 50);
 		final String normalizedSelect = query.toLowerCase().trim();
 		final int forUpdateIndex = normalizedSelect.lastIndexOf( "for update") ;
 
 		if ( hasForUpdateClause( forUpdateIndex ) ) {
 			sb.append( query.substring( 0, forUpdateIndex-1 ) );
 		}
 		else if ( hasWithClause( normalizedSelect ) ) {
 			sb.append( query.substring( 0, getWithIndex( query ) - 1 ) );
 		}
 		else {
 			sb.append( query );
 		}
 
 		if ( offset == 0 ) {
 			sb.append( " fetch first " );
 		}
 		else {
 			sb.append( " offset " ).append( offset ).append( " rows fetch next " );
 		}
 
 		sb.append( limit ).append( " rows only" );
 
 		if ( hasForUpdateClause( forUpdateIndex ) ) {
 			sb.append(' ');
 			sb.append( query.substring( forUpdateIndex ) );
 		}
 		else if ( hasWithClause( normalizedSelect ) ) {
 			sb.append( ' ' ).append( query.substring( getWithIndex( query ) ) );
 		}
 		return sb.toString();
 	}
 
 	@Override
-    public boolean supportsVariableLimit() {
+	public boolean supportsVariableLimit() {
 		// we bind the limit and offset values directly into the sql...
 		return false;
 	}
 
 	private boolean hasForUpdateClause(int forUpdateIndex) {
 		return forUpdateIndex >= 0;
 	}
 
 	private boolean hasWithClause(String normalizedSelect){
 		return normalizedSelect.startsWith( "with ", normalizedSelect.length()-7 );
 	}
 
 	private int getWithIndex(String querySelect) {
 		int i = querySelect.lastIndexOf( "with " );
 		if ( i < 0 ) {
 			i = querySelect.lastIndexOf( "WITH " );
 		}
 		return i;
 	}
 
 	@Override
-    public String getQuerySequencesString() {
-	   return null ;
+	public String getQuerySequencesString() {
+		return null ;
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
-    public boolean supportsLobValueChangePropogation() {
+	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
-    public boolean supportsUnboundedLobLocatorMaterialization() {
+	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DerbyTenFiveDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DerbyTenFiveDialect.java
index 0038f099c1..8cfc26f40f 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DerbyTenFiveDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DerbyTenFiveDialect.java
@@ -1,60 +1,64 @@
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
 package org.hibernate.dialect;
 
 import org.hibernate.dialect.function.AnsiTrimFunction;
 import org.hibernate.dialect.function.DerbyConcatFunction;
 
 
 /**
  * Hibernate Dialect for Cloudscape 10 - aka Derby. This implements both an
  * override for the identity column generator as well as for the case statement
  * issue documented at:
  * http://www.jroller.com/comments/kenlars99/Weblog/cloudscape_soon_to_be_derby
  *
  * @author Simon Johnston
  * @author Scott Marlow
  */
+@SuppressWarnings("deprecation")
 public class DerbyTenFiveDialect extends DerbyDialect {
+	/**
+	 * Constructs a DerbyTenFiveDialect
+	 */
 	public DerbyTenFiveDialect() {
 		super();
 		registerFunction( "concat", new DerbyConcatFunction() );
 		registerFunction( "trim", new AnsiTrimFunction() );
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DerbyTenSevenDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DerbyTenSevenDialect.java
index 4601276171..4a3d7d8940 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DerbyTenSevenDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DerbyTenSevenDialect.java
@@ -1,39 +1,46 @@
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
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 /**
+ * Dialect for Derby 10.7
+ *
  * @author Strong Liu
  */
-public class DerbyTenSevenDialect extends DerbyTenSixDialect{
-    public DerbyTenSevenDialect() {
-         registerColumnType( Types.BOOLEAN, "boolean" );
-    }
+public class DerbyTenSevenDialect extends DerbyTenSixDialect {
+	/**
+	 * Constructs a DerbyTenSevenDialect
+	 */
+	public DerbyTenSevenDialect() {
+		super();
+		registerColumnType( Types.BOOLEAN, "boolean" );
+	}
 
-    public String toBooleanValueString(boolean bool) {
-        return String.valueOf(bool);
-    }
+	@Override
+	public String toBooleanValueString(boolean bool) {
+		return String.valueOf( bool );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DerbyTenSixDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DerbyTenSixDialect.java
index a498610b2c..8b94f77f99 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DerbyTenSixDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DerbyTenSixDialect.java
@@ -1,44 +1,47 @@
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
 package org.hibernate.dialect;
 
 /**
  * Hibernate Dialect for Cloudscape 10 - aka Derby. This implements both an
  * override for the identity column generator as well as for the case statement
  * issue documented at:
  * http://www.jroller.com/comments/kenlars99/Weblog/cloudscape_soon_to_be_derby
  *
  * @author Simon Johnston
  * @author Scott Marlow
  */
 public class DerbyTenSixDialect extends DerbyTenFiveDialect {
+	/**
+	 * Constructs a DerbyTenSixDialect
+	 */
 	public DerbyTenSixDialect() {
 		super();
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index d9b5d6fc96..ea5522f253 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1,2477 +1,2625 @@
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
 package org.hibernate.dialect;
 
+import java.io.InputStream;
+import java.io.OutputStream;
+import java.sql.Blob;
+import java.sql.CallableStatement;
+import java.sql.Clob;
+import java.sql.NClob;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+import java.util.HashMap;
+import java.util.HashSet;
+import java.util.Iterator;
+import java.util.Map;
+import java.util.Properties;
+import java.util.Set;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.cfg.Environment;
-import org.hibernate.dialect.function.*;
-import org.hibernate.dialect.lock.*;
+import org.hibernate.dialect.function.CastFunction;
+import org.hibernate.dialect.function.SQLFunction;
+import org.hibernate.dialect.function.SQLFunctionTemplate;
+import org.hibernate.dialect.function.StandardAnsiSqlAggregationFunctions;
+import org.hibernate.dialect.function.StandardSQLFunction;
+import org.hibernate.dialect.lock.LockingStrategy;
+import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
+import org.hibernate.dialect.lock.OptimisticLockingStrategy;
+import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
+import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
+import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
+import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.pagination.LegacyLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.DefaultUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.spi.ConversionContext;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.id.TableHiLoGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.io.StreamCopier;
 import org.hibernate.mapping.Column;
 import org.hibernate.persister.entity.Lockable;
-import org.hibernate.sql.*;
+import org.hibernate.sql.ANSICaseFragment;
+import org.hibernate.sql.ANSIJoinFragment;
+import org.hibernate.sql.CaseFragment;
+import org.hibernate.sql.ForUpdateFragment;
+import org.hibernate.sql.JoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
-import org.jboss.logging.Logger;
-
-import java.io.InputStream;
-import java.io.OutputStream;
-import java.sql.*;
-import java.util.*;
 
 /**
- * Represents a dialect of SQL implemented by a particular RDBMS.
- * Subclasses implement Hibernate compatibility with different systems.<br>
- * <br>
- * Subclasses should provide a public default constructor that <tt>register()</tt>
- * a set of type mappings and default Hibernate properties.<br>
- * <br>
- * Subclasses should be immutable.
+ * Represents a dialect of SQL implemented by a particular RDBMS.  Subclasses implement Hibernate compatibility
+ * with different systems.  Subclasses should provide a public default constructor that register a set of type
+ * mappings and default Hibernate properties.  Subclasses should be immutable.
  *
  * @author Gavin King, David Channon
  */
+@SuppressWarnings("deprecation")
 public abstract class Dialect implements ConversionContext {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			Dialect.class.getName()
+	);
 
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Dialect.class.getName());
-
+	/**
+	 * Defines a default batch size constant
+	 */
 	public static final String DEFAULT_BATCH_SIZE = "15";
+
+	/**
+	 * Defines a "no batching" batch size constant
+	 */
 	public static final String NO_BATCH = "0";
 
 	/**
-	 * Characters used for quoting SQL identifiers
+	 * Characters used as opening for quoting SQL identifiers
 	 */
 	public static final String QUOTE = "`\"[";
+
+	/**
+	 * Characters used as closing for quoting SQL identifiers
+	 */
 	public static final String CLOSED_QUOTE = "`\"]";
 
 	private final TypeNames typeNames = new TypeNames();
 	private final TypeNames hibernateTypeNames = new TypeNames();
 
 	private final Properties properties = new Properties();
 	private final Map<String, SQLFunction> sqlFunctions = new HashMap<String, SQLFunction>();
 	private final Set<String> sqlKeywords = new HashSet<String>();
-	
+
 	private final UniqueDelegate uniqueDelegate;
 
 
 	// constructors and factory methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected Dialect() {
 		LOG.usingDialect( this );
 		StandardAnsiSqlAggregationFunctions.primeFunctionMap( sqlFunctions );
 
 		// standard sql92 functions (can be overridden by subclasses)
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1, ?2, ?3)" ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "locate(?1, ?2, ?3)" ) );
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "trim(?1 ?2 ?3 ?4)" ) );
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "coalesce", new StandardSQLFunction( "coalesce" ) );
 		registerFunction( "nullif", new StandardSQLFunction( "nullif" ) );
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "cast", new CastFunction() );
 		registerFunction( "extract", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(?1 ?2 ?3)") );
 
 		//map second/minute/hour/day/month/year to ANSI extract(), override on subclasses
 		registerFunction( "second", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(second from ?1)") );
 		registerFunction( "minute", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(minute from ?1)") );
 		registerFunction( "hour", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(hour from ?1)") );
 		registerFunction( "day", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(day from ?1)") );
 		registerFunction( "month", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(month from ?1)") );
 		registerFunction( "year", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(year from ?1)") );
 
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as char)") );
 
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BOOLEAN, "boolean" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.FLOAT, "float($p)" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.REAL, "real" );
 
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 
 		registerColumnType( Types.VARBINARY, "bit varying($l)" );
 		registerColumnType( Types.LONGVARBINARY, "bit varying($l)" );
 		registerColumnType( Types.BLOB, "blob" );
 
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.LONGVARCHAR, "varchar($l)" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.NCHAR, "nchar($l)" );
 		registerColumnType( Types.NVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.LONGNVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.NCLOB, "nclob" );
 
-        // register hibernate types for default use in scalar sqlquery type auto detection
+		// register hibernate types for default use in scalar sqlquery type auto detection
 		registerHibernateType( Types.BIGINT, StandardBasicTypes.BIG_INTEGER.getName() );
 		registerHibernateType( Types.BINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.BIT, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.BOOLEAN, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.CHAR, StandardBasicTypes.CHARACTER.getName() );
-        registerHibernateType( Types.CHAR, 1, StandardBasicTypes.CHARACTER.getName() );
-        registerHibernateType( Types.CHAR, 255, StandardBasicTypes.STRING.getName() );
+		registerHibernateType( Types.CHAR, 1, StandardBasicTypes.CHARACTER.getName() );
+		registerHibernateType( Types.CHAR, 255, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.DATE, StandardBasicTypes.DATE.getName() );
 		registerHibernateType( Types.DOUBLE, StandardBasicTypes.DOUBLE.getName() );
 		registerHibernateType( Types.FLOAT, StandardBasicTypes.FLOAT.getName() );
 		registerHibernateType( Types.INTEGER, StandardBasicTypes.INTEGER.getName() );
 		registerHibernateType( Types.SMALLINT, StandardBasicTypes.SHORT.getName() );
 		registerHibernateType( Types.TINYINT, StandardBasicTypes.BYTE.getName() );
 		registerHibernateType( Types.TIME, StandardBasicTypes.TIME.getName() );
 		registerHibernateType( Types.TIMESTAMP, StandardBasicTypes.TIMESTAMP.getName() );
 		registerHibernateType( Types.VARCHAR, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.VARBINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.LONGVARCHAR, StandardBasicTypes.TEXT.getName() );
 		registerHibernateType( Types.LONGVARBINARY, StandardBasicTypes.IMAGE.getName() );
 		registerHibernateType( Types.NUMERIC, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.DECIMAL, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.BLOB, StandardBasicTypes.BLOB.getName() );
 		registerHibernateType( Types.CLOB, StandardBasicTypes.CLOB.getName() );
 		registerHibernateType( Types.REAL, StandardBasicTypes.FLOAT.getName() );
-		
+
 		uniqueDelegate = new DefaultUniqueDelegate( this );
 	}
 
 	/**
 	 * Get an instance of the dialect specified by the current <tt>System</tt> properties.
 	 *
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect() throws HibernateException {
-		String dialectName = Environment.getProperties().getProperty( Environment.DIALECT );
-		return instantiateDialect( dialectName );
+		return instantiateDialect( Environment.getProperties().getProperty( Environment.DIALECT ) );
 	}
 
 
 	/**
 	 * Get an instance of the dialect specified by the given properties or by
 	 * the current <tt>System</tt> properties.
 	 *
 	 * @param props The properties to use for finding the dialect class to use.
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect(Properties props) throws HibernateException {
-		String dialectName = props.getProperty( Environment.DIALECT );
+		final String dialectName = props.getProperty( Environment.DIALECT );
 		if ( dialectName == null ) {
 			return getDialect();
 		}
 		return instantiateDialect( dialectName );
 	}
 
 	private static Dialect instantiateDialect(String dialectName) throws HibernateException {
 		if ( dialectName == null ) {
 			throw new HibernateException( "The dialect was not set. Set the property hibernate.dialect." );
 		}
 		try {
-			return ( Dialect ) ReflectHelper.classForName( dialectName ).newInstance();
+			return (Dialect) ReflectHelper.classForName( dialectName ).newInstance();
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new HibernateException( "Dialect class not found: " + dialectName );
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Could not instantiate given dialect class: " + dialectName, e );
 		}
 	}
 
 	/**
 	 * Retrieve a set of default Hibernate properties for this database.
 	 *
 	 * @return a set of Hibernate properties
 	 */
 	public final Properties getDefaultProperties() {
 		return properties;
 	}
 
 	@Override
-    public String toString() {
+	public String toString() {
 		return getClass().getName();
 	}
 
 
 	// database type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code) throws HibernateException {
-		String result = typeNames.get( code );
+		final String result = typeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No default type mapping for (java.sql.Types) " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode with the given storage specification
 	 * parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code, long length, int precision, int scale) throws HibernateException {
-		String result = typeNames.get( code, length, precision, scale );
+		final String result = typeNames.get( code, length, precision, scale );
 		if ( result == null ) {
-			throw new HibernateException(String.format( "No type mapping for java.sql.Types code: %s, length: %s", code, length ));
+			throw new HibernateException(
+					String.format( "No type mapping for java.sql.Types code: %s, length: %s", code, length )
+			);
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type appropriate for casting operations
 	 * (via the CAST() SQL function) for the given {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return The database type name
 	 */
 	public String getCastTypeName(int code) {
 		return getTypeName( code, Column.DEFAULT_LENGTH, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
+	/**
+	 * Return an expression casting the value to the specified type
+	 *
+	 * @param value The value to cast
+	 * @param jdbcTypeCode The JDBC type code to cast to
+	 * @param length The type length
+	 * @param precision The type precision
+	 * @param scale The type scale
+	 *
+	 * @return The cast expression
+	 */
 	public String cast(String value, int jdbcTypeCode, int length, int precision, int scale) {
 		if ( jdbcTypeCode == Types.CHAR ) {
 			return "cast(" + value + " as char(" + length + "))";
 		}
 		else {
 			return "cast(" + value + "as " + getTypeName( jdbcTypeCode, length, precision, scale ) + ")";
 		}
 	}
 
+	/**
+	 * Return an expression casting the value to the specified type.  Simply calls
+	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_PRECISION} and
+	 * {@link Column#DEFAULT_SCALE} as the precision/scale.
+	 *
+	 * @param value The value to cast
+	 * @param jdbcTypeCode The JDBC type code to cast to
+	 * @param length The type length
+	 *
+	 * @return The cast expression
+	 */
 	public String cast(String value, int jdbcTypeCode, int length) {
 		return cast( value, jdbcTypeCode, length, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
+	/**
+	 * Return an expression casting the value to the specified type.  Simply calls
+	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_LENGTH} as the length
+	 *
+	 * @param value The value to cast
+	 * @param jdbcTypeCode The JDBC type code to cast to
+	 * @param precision The type precision
+	 * @param scale The type scale
+	 *
+	 * @return The cast expression
+	 */
 	public String cast(String value, int jdbcTypeCode, int precision, int scale) {
 		return cast( value, jdbcTypeCode, Column.DEFAULT_LENGTH, precision, scale );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code and maximum
 	 * column length. <tt>$l</tt> in the type name with be replaced by the
 	 * column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, long capacity, String name) {
 		typeNames.put( code, capacity, name );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code. <tt>$l</tt> in
 	 * the type name with be replaced by the column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, String name) {
 		typeNames.put( code, name );
 	}
 
 	/**
 	 * Allows the dialect to override a {@link SqlTypeDescriptor}.
 	 * <p/>
 	 * If the passed {@code sqlTypeDescriptor} allows itself to be remapped (per
 	 * {@link org.hibernate.type.descriptor.sql.SqlTypeDescriptor#canBeRemapped()}), then this method uses
 	 * {@link #getSqlTypeDescriptorOverride}  to get an optional override based on the SQL code returned by
 	 * {@link SqlTypeDescriptor#getSqlType()}.
 	 * <p/>
 	 * If this dialect does not provide an override or if the {@code sqlTypeDescriptor} doe not allow itself to be
 	 * remapped, then this method simply returns the original passed {@code sqlTypeDescriptor}
 	 *
 	 * @param sqlTypeDescriptor The {@link SqlTypeDescriptor} to override
 	 * @return The {@link SqlTypeDescriptor} that should be used for this dialect;
 	 *         if there is no override, then original {@code sqlTypeDescriptor} is returned.
 	 * @throws IllegalArgumentException if {@code sqlTypeDescriptor} is null.
 	 *
 	 * @see #getSqlTypeDescriptorOverride
 	 */
 	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 		if ( sqlTypeDescriptor == null ) {
 			throw new IllegalArgumentException( "sqlTypeDescriptor is null" );
 		}
 		if ( ! sqlTypeDescriptor.canBeRemapped() ) {
 			return sqlTypeDescriptor;
 		}
 
 		final SqlTypeDescriptor overridden = getSqlTypeDescriptorOverride( sqlTypeDescriptor.getSqlType() );
 		return overridden == null ? sqlTypeDescriptor : overridden;
 	}
 
 	/**
 	 * Returns the {@link SqlTypeDescriptor} that should be used to handle the given JDBC type code.  Returns
 	 * {@code null} if there is no override.
 	 *
 	 * @param sqlCode A {@link Types} constant indicating the SQL column type
 	 * @return The {@link SqlTypeDescriptor} to use as an override, or {@code null} if there is no override.
 	 */
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.CLOB: {
 				descriptor = useInputStreamToInsertBlob() ? ClobTypeDescriptor.STREAM_BINDING : null;
 				break;
 			}
 			default: {
 				descriptor = null;
 				break;
 			}
 		}
 		return descriptor;
 	}
 
 	/**
 	 * The legacy behavior of Hibernate.  LOBs are not processed by merge
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy LEGACY_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			return target;
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			return target;
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			return target;
 		}
 	};
 
 	/**
 	 * Merge strategy based on transferring contents based on streams.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy STREAM_XFER_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
-					OutputStream connectedStream = target.setBinaryStream( 1L );  // the BLOB just read during the load phase of merge
-					InputStream detachedStream = original.getBinaryStream();      // the BLOB from the detached state
+					// the BLOB just read during the load phase of merge
+					final OutputStream connectedStream = target.setBinaryStream( 1L );
+					// the BLOB from the detached state
+					final InputStream detachedStream = original.getBinaryStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeBlob( original, target, session );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
-					OutputStream connectedStream = target.setAsciiStream( 1L );  // the CLOB just read during the load phase of merge
-					InputStream detachedStream = original.getAsciiStream();      // the CLOB from the detached state
+					// the CLOB just read during the load phase of merge
+					final OutputStream connectedStream = target.setAsciiStream( 1L );
+					// the CLOB from the detached state
+					final InputStream detachedStream = original.getAsciiStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeClob( original, target, session );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
-					OutputStream connectedStream = target.setAsciiStream( 1L );  // the NCLOB just read during the load phase of merge
-					InputStream detachedStream = original.getAsciiStream();      // the NCLOB from the detached state
+					// the NCLOB just read during the load phase of merge
+					final OutputStream connectedStream = target.setAsciiStream( 1L );
+					// the NCLOB from the detached state
+					final InputStream detachedStream = original.getAsciiStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeNClob( original, target, session );
 			}
 		}
 	};
 
 	/**
 	 * Merge strategy based on creating a new LOB locator.
 	 */
 	protected static final LobMergeStrategy NEW_LOCATOR_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
-				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
+				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createBlob( ArrayHelper.EMPTY_BYTE_ARRAY )
 						: lobCreator.createBlob( original.getBinaryStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
-				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
+				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createClob( "" )
 						: lobCreator.createClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
-				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
+				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createNClob( "" )
 						: lobCreator.createNClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 			}
 		}
 	};
 
 	public LobMergeStrategy getLobMergeStrategy() {
 		return NEW_LOCATOR_LOB_MERGE_STRATEGY;
 	}
 
 
 	// hibernate type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated with the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} type code
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getHibernateTypeName(int code) throws HibernateException {
-		String result = hibernateTypeNames.get( code );
+		final String result = hibernateTypeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No Hibernate type mapping for java.sql.Types code: " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated
 	 * with the given {@link java.sql.Types} typecode with the given storage
 	 * specification parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getHibernateTypeName(int code, int length, int precision, int scale) throws HibernateException {
-		String result = hibernateTypeNames.get( code, length, precision, scale );
+		final String result = hibernateTypeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
-					"No Hibernate type mapping for java.sql.Types code: " +
-					code +
-					", length: " +
-					length
+					String.format(
+							"No Hibernate type mapping for type [code=%s, length=%s]",
+							code,
+							length
+					)
 			);
 		}
 		return result;
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code and maximum column length.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, long capacity, String name) {
 		hibernateTypeNames.put( code, capacity, name);
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, String name) {
 		hibernateTypeNames.put( code, name);
 	}
 
 
 	// function support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerFunction(String name, SQLFunction function) {
 		// HHH-7721: SQLFunctionRegistry expects all lowercase.  Enforce,
 		// just in case a user's customer dialect uses mixed cases.
 		sqlFunctions.put( name.toLowerCase(), function );
 	}
 
 	/**
 	 * Retrieves a map of the dialect's registered functions
 	 * (functionName => {@link org.hibernate.dialect.function.SQLFunction}).
 	 *
 	 * @return The map of registered functions.
 	 */
 	public final Map<String, SQLFunction> getFunctions() {
 		return sqlFunctions;
 	}
 
 
 	// keyword support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerKeyword(String word) {
 		sqlKeywords.add(word);
 	}
 
 	public Set<String> getKeywords() {
 		return sqlKeywords;
 	}
 
 
 	// native identifier generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The class (which implements {@link org.hibernate.id.IdentifierGenerator})
 	 * which acts as this dialects native generation strategy.
 	 * <p/>
 	 * Comes into play whenever the user specifies the native generator.
 	 *
 	 * @return The native generator class.
 	 */
 	public Class getNativeIdentifierGeneratorClass() {
 		if ( supportsIdentityColumns() ) {
 			return IdentityGenerator.class;
 		}
 		else if ( supportsSequences() ) {
 			return SequenceGenerator.class;
 		}
 		else {
 			return TableHiLoGenerator.class;
 		}
 	}
 
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support identity column key generation?
 	 *
 	 * @return True if IDENTITY columns are supported; false otherwise.
 	 */
 	public boolean supportsIdentityColumns() {
 		return false;
 	}
 
 	/**
 	 * Does the dialect support some form of inserting and selecting
 	 * the generated IDENTITY value all in the same statement.
 	 *
 	 * @return True if the dialect supports selecting the just
 	 * generated IDENTITY in the insert statement.
 	 */
 	public boolean supportsInsertSelectIdentity() {
 		return false;
 	}
 
 	/**
 	 * Whether this dialect have an Identity clause added to the data type or a
 	 * completely separate identity data type
 	 *
 	 * @return boolean
 	 */
 	public boolean hasDataTypeInIdentityColumn() {
 		return true;
 	}
 
 	/**
 	 * Provided we {@link #supportsInsertSelectIdentity}, then attach the
 	 * "select identity" clause to the  insert statement.
 	 *  <p/>
 	 * Note, if {@link #supportsInsertSelectIdentity} == false then
 	 * the insert-string should be returned without modification.
 	 *
 	 * @param insertString The insert command
 	 * @return The insert command with any necessary identity select
 	 * clause attached.
 	 */
 	public String appendIdentitySelectToInsert(String insertString) {
 		return insertString;
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value for a particular table
 	 *
 	 * @param table The table into which the insert was done
 	 * @param column The PK column.
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentitySelectString(String table, String column, int type) throws MappingException {
 		return getIdentitySelectString();
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value.
 	 *
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentitySelectString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY of
 	 * a particular type.
 	 *
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentityColumnString(int type) throws MappingException {
 		return getIdentityColumnString();
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY.
 	 *
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentityColumnString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The keyword used to insert a generated value into an identity column (or null).
 	 * Need if the dialect does not support inserts that specify no column values.
 	 *
 	 * @return The appropriate keyword.
 	 */
 	public String getIdentityInsertString() {
 		return null;
 	}
 
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support sequences?
 	 *
 	 * @return True if sequences supported; false otherwise.
 	 */
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support "pooled" sequences.  Not aware of a better
 	 * name for this.  Essentially can we specify the initial and increment values?
 	 *
 	 * @return True if such "pooled" sequences are supported; false otherwise.
 	 * @see #getCreateSequenceStrings(String, int, int)
 	 * @see #getCreateSequenceString(String, int, int)
 	 */
 	public boolean supportsPooledSequences() {
 		return false;
 	}
 
 	/**
 	 * Generate the appropriate select statement to to retrieve the next value
 	 * of a sequence.
 	 * <p/>
 	 * This should be a "stand alone" select statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return String The "nextval" select string.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Generate the select expression fragment that will retrieve the next
 	 * value of a sequence as part of another (typically DML) statement.
 	 * <p/>
 	 * This differs from {@link #getSequenceNextValString(String)} in that this
 	 * should return an expression usable within another statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return The "nextval" fragment.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSelectSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * The multiline script used to create a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 * @deprecated Use {@link #getCreateSequenceString(String, int, int)} instead
 	 */
 	@Deprecated
-    public String[] getCreateSequenceStrings(String sequenceName) throws MappingException {
+	public String[] getCreateSequenceStrings(String sequenceName) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName ) };
 	}
 
 	/**
 	 * An optional multi-line form for databases which {@link #supportsPooledSequences()}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getCreateSequenceStrings(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName, initialValue, incrementSize ) };
 	}
 
 	/**
 	 * Typically dialects which support sequences can create a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getCreateSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can create a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to create
 	 * a sequence should instead override {@link #getCreateSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Overloaded form of {@link #getCreateSequenceString(String)}, additionally
 	 * taking the initial value and increment size to be applied to the sequence
 	 * definition.
 	 * </p>
 	 * The default definition is to suffix {@link #getCreateSequenceString(String)}
 	 * with the string: " start with {initialValue} increment by {incrementSize}" where
 	 * {initialValue} and {incrementSize} are replacement placeholders.  Generally
 	 * dialects should only need to override this method if different key phrases
 	 * are used to apply the allocation information.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		if ( supportsPooledSequences() ) {
 			return getCreateSequenceString( sequenceName ) + " start with " + initialValue + " increment by " + incrementSize;
 		}
 		throw new MappingException( getClass().getName() + " does not support pooled sequences" );
 	}
 
 	/**
 	 * The multiline script used to drop a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getDropSequenceStrings(String sequenceName) throws MappingException {
 		return new String[]{getDropSequenceString( sequenceName )};
 	}
 
 	/**
 	 * Typically dialects which support sequences can drop a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getDropSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can drop a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to drop
 	 * a sequence should instead override {@link #getDropSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getDropSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Get the select command used retrieve the names of all sequences.
 	 *
 	 * @return The select command; or null if sequences are not supported.
 	 * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
 	 */
 	public String getQuerySequencesString() {
 		return null;
 	}
 
 
 	// GUID support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the command used to select a GUID from the underlying database.
 	 * <p/>
 	 * Optional operation.
 	 *
 	 * @return The appropriate command.
 	 */
 	public String getSelectGUIDString() {
 		throw new UnsupportedOperationException( getClass().getName() + " does not support GUIDs" );
 	}
 
 
 	// limit/offset support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this dialect supports some form of LIMIT.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimit() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect's LIMIT support (if any) additionally
 	 * support specifying an offset?
 	 *
 	 * @return True if the dialect supports an offset within the limit support.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimitOffset() {
 		return supportsLimit();
 	}
 
 	/**
 	 * Does this dialect support bind variables (i.e., prepared statement
 	 * parameters) for its limit/offset?
 	 *
 	 * @return True if bind variables can be used; false otherwise.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsVariableLimit() {
 		return supportsLimit();
 	}
 
 	/**
 	 * ANSI SQL defines the LIMIT clause to be in the form LIMIT offset, limit.
 	 * Does this dialect require us to bind the parameters in reverse order?
 	 *
 	 * @return true if the correct order is limit, offset
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause come at the start of the
 	 * <tt>SELECT</tt> statement, rather than at the end?
 	 *
 	 * @return true if limit parameters should come before other parameters
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause take a "maximum" row number instead
 	 * of a total number of returned rows?
 	 * <p/>
 	 * This is easiest understood via an example.  Consider you have a table
 	 * with 20 rows, but you only want to retrieve rows number 11 through 20.
 	 * Generally, a limit with offset would say that the offset = 11 and the
 	 * limit = 10 (we only want 10 rows at a time); this is specifying the
 	 * total number of returned rows.  Some dialects require that we instead
 	 * specify offset = 11 and limit = 20, where 20 is the "last" row we want
 	 * relative to offset (i.e. total number of rows = 20 - 11 = 9)
 	 * <p/>
 	 * So essentially, is limit relative from offset?  Or is limit absolute?
 	 *
 	 * @return True if limit is relative from offset; false otherwise.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean useMaxForLimit() {
 		return false;
 	}
 
 	/**
 	 * Generally, if there is no limit applied to a Hibernate query we do not apply any limits
 	 * to the SQL query.  This option forces that the limit be written to the SQL query.
 	 *
 	 * @return True to force limit into SQL query even if none specified in Hibernate query; false otherwise.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean forceLimitUsage() {
 		return false;
 	}
 
 	/**
 	 * Given a limit and an offset, apply the limit clause to the query.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param offset The offset of the limit
 	 * @param limit The limit of the limit ;)
 	 * @return The modified query statement with the limit applied.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public String getLimitString(String query, int offset, int limit) {
 		return getLimitString( query, ( offset > 0 || forceLimitUsage() )  );
 	}
 
 	/**
 	 * Apply s limit clause to the query.
 	 * <p/>
 	 * Typically dialects utilize {@link #supportsVariableLimit() variable}
 	 * limit clauses when they support limits.  Thus, when building the
 	 * select command we do not actually need to know the limit or the offest
 	 * since we will just be using placeholders.
 	 * <p/>
 	 * Here we do still pass along whether or not an offset was specified
 	 * so that dialects not supporting offsets can generate proper exceptions.
 	 * In general, dialects will override one or the other of this method and
 	 * {@link #getLimitString(String, int, int)}.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param hasOffset Is the query requesting an offset?
 	 * @return the modified SQL
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	protected String getLimitString(String query, boolean hasOffset) {
 		throw new UnsupportedOperationException( "Paged queries not supported by " + getClass().getName());
 	}
 
 	/**
 	 * Hibernate APIs explicitly state that setFirstResult() should be a zero-based offset. Here we allow the
 	 * Dialect a chance to convert that value based on what the underlying db or driver will expect.
 	 * <p/>
 	 * NOTE: what gets passed into {@link #getLimitString(String,int,int)} is the zero-based offset.  Dialects which
 	 * do not {@link #supportsVariableLimit} should take care to perform any needed first-row-conversion calls prior
 	 * to injecting the limit values into the SQL string.
 	 *
 	 * @param zeroBasedFirstResult The user-supplied, zero-based first row offset.
 	 * @return The corresponding db/dialect specific offset.
 	 * @see org.hibernate.Query#setFirstResult
 	 * @see org.hibernate.Criteria#setFirstResult
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
 	/**
 	 * Build delegate managing LIMIT clause.
 	 *
 	 * @param sql SQL query.
 	 * @param selection Selection criteria. {@code null} in case of unlimited number of rows.
 	 * @return LIMIT clause delegate.
 	 */
 	public LimitHandler buildLimitHandler(String sql, RowSelection selection) {
 		return new LegacyLimitHandler( this, sql, selection );
 	}
 
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Informational metadata about whether this dialect is known to support
 	 * specifying timeouts for requested lock acquisitions.
 	 *
 	 * @return True is this dialect supports specifying lock timeouts.
 	 */
 	public boolean supportsLockTimeouts() {
 		return true;
 
 	}
 
 	/**
 	 * If this dialect supports specifying lock timeouts, are those timeouts
 	 * rendered into the <tt>SQL</tt> string as parameters.  The implication
 	 * is that Hibernate will need to bind the timeout value as a parameter
 	 * in the {@link java.sql.PreparedStatement}.  If true, the param position
 	 * is always handled as the last parameter; if the dialect specifies the
 	 * lock timeout elsewhere in the <tt>SQL</tt> statement then the timeout
 	 * value should be directly rendered into the statement and this method
 	 * should return false.
 	 *
 	 * @return True if the lock timeout is rendered into the <tt>SQL</tt>
 	 * string as a parameter; false otherwise.
 	 */
 	public boolean isLockTimeoutParameterized() {
 		return false;
 	}
 
 	/**
 	 * Get a strategy instance which knows how to acquire a database-level lock
 	 * of the specified mode for this dialect.
 	 *
 	 * @param lockable The persister for the entity to be locked.
 	 * @param lockMode The type of lock to be acquired.
 	 * @return The appropriate locking strategy.
 	 * @since 3.2
 	 */
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
-        switch ( lockMode ) {
-            case PESSIMISTIC_FORCE_INCREMENT:
-                return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
-            case PESSIMISTIC_WRITE:
-                return new PessimisticWriteSelectLockingStrategy( lockable, lockMode );
-            case PESSIMISTIC_READ:
-                return new PessimisticReadSelectLockingStrategy( lockable, lockMode );
-            case OPTIMISTIC:
-                return new OptimisticLockingStrategy( lockable, lockMode );
-            case OPTIMISTIC_FORCE_INCREMENT:
-                return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
-            default:
-                return new SelectLockingStrategy( lockable, lockMode );
-        }
+		switch ( lockMode ) {
+			case PESSIMISTIC_FORCE_INCREMENT:
+				return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
+			case PESSIMISTIC_WRITE:
+				return new PessimisticWriteSelectLockingStrategy( lockable, lockMode );
+			case PESSIMISTIC_READ:
+				return new PessimisticReadSelectLockingStrategy( lockable, lockMode );
+			case OPTIMISTIC:
+				return new OptimisticLockingStrategy( lockable, lockMode );
+			case OPTIMISTIC_FORCE_INCREMENT:
+				return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
+			default:
+				return new SelectLockingStrategy( lockable, lockMode );
+		}
 	}
 
 	/**
 	 * Given LockOptions (lockMode, timeout), determine the appropriate for update fragment to use.
 	 *
 	 * @param lockOptions contains the lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockOptions lockOptions) {
-        LockMode lockMode = lockOptions.getLockMode();
-        return getForUpdateString( lockMode, lockOptions.getTimeOut() );
+		final LockMode lockMode = lockOptions.getLockMode();
+		return getForUpdateString( lockMode, lockOptions.getTimeOut() );
 	}
 
-    @SuppressWarnings( {"deprecation"})
+	@SuppressWarnings( {"deprecation"})
 	private String getForUpdateString(LockMode lockMode, int timeout){
-       switch ( lockMode ) {
-            case UPGRADE:
-                return getForUpdateString();
-            case PESSIMISTIC_READ:
-                return getReadLockString( timeout );
-            case PESSIMISTIC_WRITE:
-                return getWriteLockString( timeout );
-            case UPGRADE_NOWAIT:
-            case FORCE:
-            case PESSIMISTIC_FORCE_INCREMENT:
-                return getForUpdateNowaitString();
-            case UPGRADE_SKIPLOCKED:
-                return getForUpdateSkipLockedString();
-            default:
-                return "";
-        }
-    }
+		switch ( lockMode ) {
+			case UPGRADE:
+				return getForUpdateString();
+			case PESSIMISTIC_READ:
+				return getReadLockString( timeout );
+			case PESSIMISTIC_WRITE:
+				return getWriteLockString( timeout );
+			case UPGRADE_NOWAIT:
+			case FORCE:
+			case PESSIMISTIC_FORCE_INCREMENT:
+				return getForUpdateNowaitString();
+			case UPGRADE_SKIPLOCKED:
+				return getForUpdateSkipLockedString();
+			default:
+				return "";
+		}
+	}
 
 	/**
 	 * Given a lock mode, determine the appropriate for update fragment to use.
 	 *
 	 * @param lockMode The lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockMode lockMode) {
 		return getForUpdateString( lockMode, LockOptions.WAIT_FOREVER );
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire locks
 	 * for this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE</tt> clause string.
 	 */
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getWriteLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getReadLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 
 	/**
 	 * Is <tt>FOR UPDATE OF</tt> syntax supported?
 	 *
 	 * @return True if the database supports <tt>FOR UPDATE OF</tt> syntax;
 	 * false otherwise.
 	 */
 	public boolean forUpdateOfColumns() {
 		// by default we report no support
 		return false;
 	}
 
 	/**
 	 * Does this dialect support <tt>FOR UPDATE</tt> in conjunction with
 	 * outer joined rows?
 	 *
 	 * @return True if outer joined rows can be locked via <tt>FOR UPDATE</tt>.
 	 */
 	public boolean supportsOuterJoinForUpdate() {
 		return true;
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	public String getForUpdateString(String aliases) {
 		// by default we simply return the getForUpdateString() result since
 		// the default is to say no support for "FOR UPDATE OF ..."
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @param lockOptions the lock options to apply
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	@SuppressWarnings( {"unchecked"})
 	public String getForUpdateString(String aliases, LockOptions lockOptions) {
 		LockMode lockMode = lockOptions.getLockMode();
-		Iterator<Map.Entry<String, LockMode>> itr = lockOptions.getAliasLockIterator();
+		final Iterator<Map.Entry<String, LockMode>> itr = lockOptions.getAliasLockIterator();
 		while ( itr.hasNext() ) {
 			// seek the highest lock mode
 			final Map.Entry<String, LockMode>entry = itr.next();
 			final LockMode lm = entry.getValue();
-			if ( lm.greaterThan(lockMode) ) {
+			if ( lm.greaterThan( lockMode ) ) {
 				lockMode = lm;
 			}
 		}
 		lockOptions.setLockMode( lockMode );
 		return getForUpdateString( lockOptions );
 	}
 
 	/**
 	 * Retrieves the <tt>FOR UPDATE NOWAIT</tt> syntax specific to this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString() {
 		// by default we report no support for NOWAIT lock semantics
 		return getForUpdateString();
 	}
 
 	/**
-	* Retrieves the <tt>FOR UPDATE SKIP LOCKED</tt> syntax specific to this dialect.
-	*
-	* @return The appropriate <tt>FOR UPDATE SKIP LOCKED</tt> clause string.
-	*/
+	 * Retrieves the <tt>FOR UPDATE SKIP LOCKED</tt> syntax specific to this dialect.
+	 *
+	 * @return The appropriate <tt>FOR UPDATE SKIP LOCKED</tt> clause string.
+	 */
 	public String getForUpdateSkipLockedString() {
 		// by default we report no support for SKIP_LOCKED lock semantics
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list NOWAIT</tt> fragment appropriate
 	 * for this dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF colunm_list NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString( aliases );
 	}
 
- 	/**
+	/**
 	 * Get the <tt>FOR UPDATE OF column_list SKIP LOCKED</tt> fragment appropriate
 	 * for this dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE colunm_list SKIP LOCKED</tt> clause string.
 	 */
 	public String getForUpdateSkipLockedString(String aliases) {
 		return getForUpdateString( aliases );
 	}
 
 	/**
 	 * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
 	 * whereby a "lock hint" is appends to the table name in the from clause.
 	 * <p/>
 	 * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
 	 *
 	 * @param mode The lock mode to apply
 	 * @param tableName The name of the table to which to apply the lock hint.
 	 * @return The table with any required lock hints.
 	 * @deprecated use {@code appendLockHint(LockOptions,String)} instead
 	 */
 	@Deprecated
 	public String appendLockHint(LockMode mode, String tableName) {
 		return appendLockHint( new LockOptions( mode ), tableName );
 	}
 	/**
 	 * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
 	 * whereby a "lock hint" is appends to the table name in the from clause.
 	 * <p/>
 	 * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
 	 *
 	 * @param lockOptions The lock options to apply
 	 * @param tableName The name of the table to which to apply the lock hint.
 	 * @return The table with any required lock hints.
 	 */
 	public String appendLockHint(LockOptions lockOptions, String tableName){
 		return tableName;
 	}
 
 	/**
 	 * Modifies the given SQL by applying the appropriate updates for the specified
 	 * lock modes and key columns.
 	 * <p/>
 	 * The behavior here is that of an ANSI SQL <tt>SELECT FOR UPDATE</tt>.  This
 	 * method is really intended to allow dialects which do not support
 	 * <tt>SELECT FOR UPDATE</tt> to achieve this in their own fashion.
 	 *
 	 * @param sql the SQL string to modify
 	 * @param aliasedLockOptions lock options indexed by aliased table names.
 	 * @param keyColumnNames a map of key columns indexed by aliased table names.
 	 * @return the modified SQL string.
 	 */
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
 		return sql + new ForUpdateFragment( this, aliasedLockOptions, keyColumnNames ).toFragmentString();
 	}
 
 
 	// table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Command used to create a table.
 	 *
 	 * @return The command used to create a table.
 	 */
 	public String getCreateTableString() {
 		return "create table";
 	}
 
 	/**
 	 * Slight variation on {@link #getCreateTableString}.  Here, we have the
 	 * command used to create a table when there is no primary key and
 	 * duplicate rows are expected.
 	 * <p/>
 	 * Most databases do not care about the distinction; originally added for
 	 * Teradata support which does care.
 	 *
 	 * @return The command used to create a multiset table.
 	 */
 	public String getCreateMultisetTableString() {
 		return getCreateTableString();
 	}
 
 
 	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support temporary tables?
 	 *
 	 * @return True if temp tables are supported; false otherwise.
 	 */
 	public boolean supportsTemporaryTables() {
 		return false;
 	}
 
 	/**
 	 * Generate a temporary table name given the base table.
 	 *
 	 * @param baseTableName The table name from which to base the temp table name.
 	 * @return The generated temp table name.
 	 */
 	public String generateTemporaryTableName(String baseTableName) {
 		return "HT_" + baseTableName;
 	}
 
 	/**
 	 * Command used to create a temporary table.
 	 *
 	 * @return The command used to create a temporary table.
 	 */
 	public String getCreateTemporaryTableString() {
 		return "create table";
 	}
 
 	/**
 	 * Get any fragments needing to be postfixed to the command for
 	 * temporary table creation.
 	 *
 	 * @return Any required postfix.
 	 */
 	public String getCreateTemporaryTablePostfix() {
 		return "";
 	}
 
 	/**
 	 * Command used to drop a temporary table.
 	 *
 	 * @return The command used to drop a temporary table.
 	 */
 	public String getDropTemporaryTableString() {
 		return "drop table";
 	}
 
 	/**
 	 * Does the dialect require that temporary table DDL statements occur in
 	 * isolation from other statements?  This would be the case if the creation
 	 * would cause any current transaction to get committed implicitly.
 	 * <p/>
 	 * JDBC defines a standard way to query for this information via the
 	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}
 	 * method.  However, that does not distinguish between temporary table
 	 * DDL and other forms of DDL; MySQL, for example, reports DDL causing a
 	 * transaction commit via its driver, even though that is not the case for
 	 * temporary table DDL.
 	 * <p/>
 	 * Possible return values and their meanings:<ul>
 	 * <li>{@link Boolean#TRUE} - Unequivocally, perform the temporary table DDL
 	 * in isolation.</li>
 	 * <li>{@link Boolean#FALSE} - Unequivocally, do <b>not</b> perform the
 	 * temporary table DDL in isolation.</li>
 	 * <li><i>null</i> - defer to the JDBC driver response in regards to
 	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}</li>
 	 * </ul>
 	 *
 	 * @return see the result matrix above.
 	 */
 	public Boolean performTemporaryTableDDLInIsolation() {
 		return null;
 	}
 
 	/**
 	 * Do we need to drop the temporary table after use?
 	 *
 	 * @return True if the table should be dropped.
 	 */
 	public boolean dropTemporaryTableAfterUse() {
 		return true;
 	}
 
 
 	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Registers a parameter (either OUT, or the new REF_CURSOR param type available in Java 8) capable of
 	 * returning {@link java.sql.ResultSet} *by position*.  Pre-Java 8, registering such ResultSet-returning
 	 * parameters varied greatly across database and drivers; hence its inclusion as part of the Dialect contract.
 	 *
 	 * @param statement The callable statement.
 	 * @param position The bind position at which to register the output param.
 	 *
 	 * @return The number of (contiguous) bind positions used.
 	 *
 	 * @throws SQLException Indicates problems registering the param.
 	 */
 	public int registerResultSetOutParameter(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
-				" does not support resultsets via stored procedures"
-			);
+						" does not support resultsets via stored procedures"
+		);
 	}
 
 	/**
 	 * Registers a parameter (either OUT, or the new REF_CURSOR param type available in Java 8) capable of
 	 * returning {@link java.sql.ResultSet} *by name*.  Pre-Java 8, registering such ResultSet-returning
 	 * parameters varied greatly across database and drivers; hence its inclusion as part of the Dialect contract.
 	 *
 	 * @param statement The callable statement.
 	 * @param name The parameter name (for drivers which support named parameters).
 	 *
 	 * @return The number of (contiguous) bind positions used.
 	 *
 	 * @throws SQLException Indicates problems registering the param.
 	 */
 	public int registerResultSetOutParameter(CallableStatement statement, String name) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 						" does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
 	 *
 	 * @param statement The callable statement.
 	 * @return The extracted result set.
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	public ResultSet getResultSet(CallableStatement statement) throws SQLException {
 		throw new UnsupportedOperationException(
-				getClass().getName() +
-				" does not support resultsets via stored procedures"
-			);
+				getClass().getName() + " does not support resultsets via stored procedures"
+		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet}.
 	 *
 	 * @param statement The callable statement.
 	 * @param position The bind position at which to register the output param.
 	 *
 	 * @return The extracted result set.
 	 *
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	public ResultSet getResultSet(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
-				getClass().getName() +
-						" does not support resultsets via stored procedures"
+				getClass().getName() + " does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
 	 *
 	 * @param statement The callable statement.
 	 * @param name The parameter name (for drivers which support named parameters).
 	 *
 	 * @return The extracted result set.
 	 *
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
 		throw new UnsupportedOperationException(
-				getClass().getName() +
-						" does not support resultsets via stored procedures"
+				getClass().getName() + " does not support resultsets via stored procedures"
 		);
 	}
 
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support a way to retrieve the database's current
 	 * timestamp value?
 	 *
 	 * @return True if the current timestamp can be retrieved; false otherwise.
 	 */
 	public boolean supportsCurrentTimestampSelection() {
 		return false;
 	}
 
 	/**
 	 * Should the value returned by {@link #getCurrentTimestampSelectString}
 	 * be treated as callable.  Typically this indicates that JDBC escape
 	 * syntax is being used...
 	 *
 	 * @return True if the {@link #getCurrentTimestampSelectString} return
 	 * is callable; false otherwise.
 	 */
 	public boolean isCurrentTimestampSelectStringCallable() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * Retrieve the command used to retrieve the current timestamp from the
 	 * database.
 	 *
 	 * @return The command.
 	 */
 	public String getCurrentTimestampSelectString() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * The name of the database-specific SQL function for retrieving the
 	 * current timestamp.
 	 *
 	 * @return The function name.
 	 */
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
 	}
 
 
 	// SQLException support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Build an instance of the SQLExceptionConverter preferred by this dialect for
 	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.
 	 * <p/>
 	 * The preferred method is to not override this method; if possible,
 	 * {@link #buildSQLExceptionConversionDelegate()} should be overridden
 	 * instead.
 	 *
 	 * If this method is not overridden, the default SQLExceptionConverter
 	 * implementation executes 3 SQLException converter delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>the vendor-specific delegate returned by {@link #buildSQLExceptionConversionDelegate()};
 	 *         (it is strongly recommended that specific Dialect implementations
 	 *         override {@link #buildSQLExceptionConversionDelegate()})</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * If this method is overridden, it is strongly recommended that the
 	 * returned {@link SQLExceptionConverter} interpret SQL errors based on
 	 * vendor-specific error codes rather than the SQLState since the
 	 * interpretation is more accurate when using vendor-specific ErrorCodes.
 	 *
 	 * @return The Dialect's preferred SQLExceptionConverter, or null to
 	 * indicate that the default {@link SQLExceptionConverter} should be used.
 	 *
 	 * @see {@link #buildSQLExceptionConversionDelegate()}
 	 * @deprecated {@link #buildSQLExceptionConversionDelegate()} should be
 	 * overridden instead.
 	 */
 	@Deprecated
 	public SQLExceptionConverter buildSQLExceptionConverter() {
 		return null;
 	}
 
 	/**
 	 * Build an instance of a {@link SQLExceptionConversionDelegate} for
 	 * interpreting dialect-specific error or SQLState codes.
 	 * <p/>
 	 * When {@link #buildSQLExceptionConverter} returns null, the default 
 	 * {@link SQLExceptionConverter} is used to interpret SQLState and
 	 * error codes. If this method is overridden to return a non-null value,
 	 * the default {@link SQLExceptionConverter} will use the returned
 	 * {@link SQLExceptionConversionDelegate} in addition to the following 
 	 * standard delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * It is strongly recommended that specific Dialect implementations override this
 	 * method, since interpretation of a SQL error is much more accurate when based on
 	 * the a vendor-specific ErrorCode rather than the SQLState.
 	 * <p/>
 	 * Specific Dialects may override to return whatever is most appropriate for that vendor.
+	 *
+	 * @return The SQLExceptionConversionDelegate for this dialect
 	 */
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return null;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new ViolatedConstraintNameExtracter() {
 		public String extractConstraintName(SQLException sqle) {
 			return null;
 		}
 	};
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 
 	// union subclass support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Given a {@link java.sql.Types} type code, determine an appropriate
 	 * null value to use in a select clause.
 	 * <p/>
 	 * One thing to consider here is that certain databases might
 	 * require proper casting for the nulls here since the select here
 	 * will be part of a UNION/UNION ALL.
 	 *
 	 * @param sqlType The {@link java.sql.Types} type code.
 	 * @return The appropriate select clause value fragment.
 	 */
 	public String getSelectClauseNullString(int sqlType) {
 		return "null";
 	}
 
 	/**
 	 * Does this dialect support UNION ALL, which is generally a faster
 	 * variant of UNION?
 	 *
 	 * @return True if UNION ALL is supported; false otherwise.
 	 */
 	public boolean supportsUnionAll() {
 		return false;
 	}
 
 
 	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 	/**
 	 * Create a {@link org.hibernate.sql.JoinFragment} strategy responsible
 	 * for handling this dialect's variations in how joins are handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.JoinFragment} strategy.
 	 */
 	public JoinFragment createOuterJoinFragment() {
 		return new ANSIJoinFragment();
 	}
 
 	/**
 	 * Create a {@link org.hibernate.sql.CaseFragment} strategy responsible
 	 * for handling this dialect's variations in how CASE statements are
 	 * handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.CaseFragment} strategy.
 	 */
 	public CaseFragment createCaseFragment() {
 		return new ANSICaseFragment();
 	}
 
 	/**
 	 * The fragment used to insert a row without specifying any column values.
 	 * This is not possible on some databases.
 	 *
 	 * @return The appropriate empty values clause.
 	 */
 	public String getNoColumnsInsertString() {
 		return "values ( )";
 	}
 
 	/**
 	 * The name of the SQL function that transforms a string to
 	 * lowercase
 	 *
 	 * @return The dialect-specific lowercase function.
 	 */
 	public String getLowercaseFunction() {
 		return "lower";
 	}
 
 	/**
 	 * The name of the SQL function that can do case insensitive <b>like</b> comparison.
+	 *
 	 * @return  The dialect-specific "case insensitive" like function.
 	 */
 	public String getCaseInsensitiveLike(){
 		return "like";
 	}
 
 	/**
-	 * @return {@code true} if the underlying Database supports case insensitive like comparison, {@code false} otherwise.
-	 * The default is {@code false}.
+	 * Does this dialect support case insensitive LIKE restrictions?
+	 *
+	 * @return {@code true} if the underlying database supports case insensitive like comparison,
+	 * {@code false} otherwise.  The default is {@code false}.
 	 */
 	public boolean supportsCaseInsensitiveLike(){
 		return false;
 	}
 
 	/**
 	 * Meant as a means for end users to affect the select strings being sent
 	 * to the database and perhaps manipulate them in some fashion.
 	 * <p/>
 	 * The recommend approach is to instead use
 	 * {@link org.hibernate.Interceptor#onPrepareStatement(String)}.
 	 *
 	 * @param select The select command
 	 * @return The mutated select command, or the same as was passed in.
 	 */
 	public String transformSelectString(String select) {
 		return select;
 	}
 
 	/**
 	 * What is the maximum length Hibernate can use for generated aliases?
 	 *
 	 * @return The maximum length.
 	 */
 	public int getMaxAliasLength() {
 		return 10;
 	}
 
 	/**
 	 * The SQL literal value to which this database maps boolean values.
 	 *
 	 * @param bool The boolean value
 	 * @return The appropriate SQL literal.
 	 */
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "1" : "0";
 	}
 
 
 	// identifier quoting support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The character specific to this dialect used to begin a quoted identifier.
 	 *
 	 * @return The dialect's specific open quote character.
 	 */
 	public char openQuote() {
 		return '"';
 	}
 
 	/**
 	 * The character specific to this dialect used to close a quoted identifier.
 	 *
 	 * @return The dialect's specific close quote character.
 	 */
 	public char closeQuote() {
 		return '"';
 	}
 
 	/**
 	 * Apply dialect-specific quoting.
 	 * <p/>
 	 * By default, the incoming value is checked to see if its first character
 	 * is the back-tick (`).  If so, the dialect specific quoting is applied.
 	 *
 	 * @param name The value to be quoted.
 	 * @return The quoted (or unmodified, if not starting with back-tick) value.
 	 * @see #openQuote()
 	 * @see #closeQuote()
 	 */
 	public final String quote(String name) {
 		if ( name == null ) {
 			return null;
 		}
 
 		if ( name.charAt( 0 ) == '`' ) {
 			return openQuote() + name.substring( 1, name.length() - 1 ) + closeQuote();
 		}
 		else {
 			return name;
 		}
 	}
 
 
 	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the SQL command used to create the named schema
 	 *
 	 * @param schemaName The name of the schema to be created.
 	 *
 	 * @return The creation command
 	 */
 	public String getCreateSchemaCommand(String schemaName) {
 		return "create schema " + schemaName;
 	}
 
 	/**
 	 * Get the SQL command used to drop the named schema
 	 *
 	 * @param schemaName The name of the schema to be dropped.
 	 *
 	 * @return The drop command
 	 */
 	public String getDropSchemaCommand(String schemaName) {
 		return "drop schema " + schemaName;
 	}
 
 	/**
 	 * Does this dialect support the <tt>ALTER TABLE</tt> syntax?
 	 *
 	 * @return True if we support altering of tables; false otherwise.
 	 */
 	public boolean hasAlterTable() {
 		return true;
 	}
 
 	/**
 	 * Do we need to drop constraints before dropping tables in this dialect?
 	 *
 	 * @return True if constraints must be dropped prior to dropping
 	 * the table; false otherwise.
 	 */
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	/**
 	 * Do we need to qualify index names with the schema name?
 	 *
 	 * @return boolean
 	 */
 	public boolean qualifyIndexName() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a column to a table (optional).
 	 *
 	 * @return The "add column" fragment.
 	 */
 	public String getAddColumnString() {
 		throw new UnsupportedOperationException( "No add column syntax supported by " + getClass().getName() );
 	}
 
 	public String getDropForeignKeyString() {
 		return " drop constraint ";
 	}
 
 	public String getTableTypeString() {
 		// grrr... for differentiation of mysql storage engines
 		return "";
 	}
 
 	/**
 	 * The syntax used to add a foreign key constraint to a table.
 	 *
 	 * @param constraintName The FK constraint name.
 	 * @param foreignKey The names of the columns comprising the FK
 	 * @param referencedTable The table referenced by the FK
 	 * @param primaryKey The explicit columns in the referencedTable referenced
 	 * by this FK.
 	 * @param referencesPrimaryKey if false, constraint should be
 	 * explicit about which column names the constraint refers to
 	 *
 	 * @return the "add FK" fragment
 	 */
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
-		StringBuilder res = new StringBuilder( 30 );
+		final StringBuilder res = new StringBuilder( 30 );
 
 		res.append( " add constraint " )
 				.append( constraintName )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			res.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		return res.toString();
 	}
 
 	/**
 	 * The syntax used to add a primary key constraint to a table.
 	 *
 	 * @param constraintName The name of the PK constraint.
 	 * @return The "add PK" fragment
 	 */
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " primary key ";
 	}
 
+	/**
+	 * Does the database/driver have bug in deleting rows that refer to other rows being deleted in the same query?
+	 *
+	 * @return {@code true} if the database/driver has this bug
+	 */
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return false;
 	}
 
 	/**
 	 * The keyword used to specify a nullable column.
 	 *
 	 * @return String
 	 */
 	public String getNullColumnString() {
 		return "";
 	}
 
+	/**
+	 * Does this dialect/database support commenting on tables, columns, etc?
+	 *
+	 * @return {@code true} if commenting is supported
+	 */
 	public boolean supportsCommentOn() {
 		return false;
 	}
 
+	/**
+	 * Get the comment into a form supported for table definition.
+	 *
+	 * @param comment The comment to apply
+	 *
+	 * @return The comment fragment
+	 */
 	public String getTableComment(String comment) {
 		return "";
 	}
 
+	/**
+	 * Get the comment into a form supported for column definition.
+	 *
+	 * @param comment The comment to apply
+	 *
+	 * @return The comment fragment
+	 */
 	public String getColumnComment(String comment) {
 		return "";
 	}
 
+	/**
+	 * For dropping a table, can the phrase "if exists" be applied before the table name?
+	 * <p/>
+	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterTableName} should return true
+	 *
+	 * @return {@code true} if the "if exists" can be applied before the table name
+	 */
 	public boolean supportsIfExistsBeforeTableName() {
 		return false;
 	}
 
+	/**
+	 * For dropping a table, can the phrase "if exists" be applied after the table name?
+	 * <p/>
+	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeTableName} should return true
+	 *
+	 * @return {@code true} if the "if exists" can be applied after the table name
+	 */
 	public boolean supportsIfExistsAfterTableName() {
 		return false;
 	}
-	
-	public String getDropTableString( String tableName ) {
-		StringBuilder buf = new StringBuilder( "drop table " );
+
+	/**
+	 * Generate a DROP TABLE statement
+	 *
+	 * @param tableName The name of the table to drop
+	 *
+	 * @return The DROP TABLE command
+	 */
+	public String getDropTableString(String tableName) {
+		final StringBuilder buf = new StringBuilder( "drop table " );
 		if ( supportsIfExistsBeforeTableName() ) {
 			buf.append( "if exists " );
 		}
 		buf.append( tableName ).append( getCascadeConstraintsString() );
 		if ( supportsIfExistsAfterTableName() ) {
 			buf.append( " if exists" );
 		}
 		return buf.toString();
 	}
 
 	/**
 	 * Does this dialect support column-level check constraints?
 	 *
 	 * @return True if column-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsColumnCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support table-level check constraints?
 	 *
 	 * @return True if table-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsTableCheck() {
 		return true;
 	}
 
+	/**
+	 * Does this dialect support cascaded delete on foreign key definitions?
+	 *
+	 * @return {@code true} indicates that the dialect does support cascaded delete on foreign keys.
+	 */
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	/**
 	 * Completely optional cascading drop clause
 	 *
 	 * @return String
 	 */
 	public String getCascadeConstraintsString() {
 		return "";
 	}
 
 	/**
-	 * @return Returns the separator to use for defining cross joins when translating HQL queries.
+	 * Returns the separator to use for defining cross joins when translating HQL queries.
 	 * <p/>
 	 * Typically this will be either [<tt> cross join </tt>] or [<tt>, </tt>]
 	 * <p/>
 	 * Note that the spaces are important!
 	 *
+	 * @return The cross join separator
 	 */
 	public String getCrossJoinSeparator() {
 		return " cross join ";
 	}
 
 	public ColumnAliasExtractor getColumnAliasExtractor() {
 		return ColumnAliasExtractor.COLUMN_LABEL_EXTRACTOR;
 	}
 
 
 	// Informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support empty IN lists?
 	 * <p/>
 	 * For example, is [where XYZ in ()] a supported construct?
 	 *
 	 * @return True if empty in lists are supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsEmptyInList() {
 		return true;
 	}
 
 	/**
 	 * Are string comparisons implicitly case insensitive.
 	 * <p/>
 	 * In other words, does [where 'XYZ' = 'xyz'] resolve to true?
 	 *
 	 * @return True if comparisons are case insensitive.
 	 * @since 3.2
 	 */
 	public boolean areStringComparisonsCaseInsensitive() {
 		return false;
 	}
 
 	/**
 	 * Is this dialect known to support what ANSI-SQL terms "row value
 	 * constructor" syntax; sometimes called tuple syntax.
 	 * <p/>
 	 * Basically, does it support syntax like
 	 * "... where (FIRST_NAME, LAST_NAME) = ('Steve', 'Ebersole') ...".
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntax() {
 		// return false here, as most databases do not properly support this construct...
 		return false;
 	}
 
 	/**
 	 * If the dialect supports {@link #supportsRowValueConstructorSyntax() row values},
 	 * does it offer such support in IN lists as well?
 	 * <p/>
 	 * For example, "... where (FIRST_NAME, LAST_NAME) IN ( (?, ?), (?, ?) ) ..."
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax in the IN list; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return false;
 	}
 
 	/**
 	 * Should LOBs (both BLOB and CLOB) be bound using stream operations (i.e.
 	 * {@link java.sql.PreparedStatement#setBinaryStream}).
 	 *
 	 * @return True if BLOBs and CLOBs should be bound using stream operations.
 	 * @since 3.2
 	 */
 	public boolean useInputStreamToInsertBlob() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support parameters within the <tt>SELECT</tt> clause of
 	 * <tt>INSERT ... SELECT ...</tt> statements?
 	 *
 	 * @return True if this is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsParametersInInsertSelect() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect require that references to result variables
 	 * (i.e, select expresssion aliases) in an ORDER BY clause be
 	 * replaced by column positions (1-origin) as defined
 	 * by the select clause?
 
 	 * @return true if result variable references in the ORDER BY
 	 *              clause should be replaced by column positions;
 	 *         false otherwise.
 	 */
 	public boolean replaceResultVariableInOrderByClauseWithPosition() {
 		return false;
 	}
 
 	/**
+	 * Renders an ordering fragment
+	 *
 	 * @param expression The SQL order expression. In case of {@code @OrderBy} annotation user receives property placeholder
 	 * (e.g. attribute name enclosed in '{' and '}' signs).
 	 * @param collation Collation string in format {@code collate IDENTIFIER}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param order Order direction. Possible values: {@code asc}, {@code desc}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param nulls Nulls precedence. Default value: {@link NullPrecedence#NONE}.
 	 * @return Renders single element of {@code ORDER BY} clause.
 	 */
 	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
 		final StringBuilder orderByElement = new StringBuilder( expression );
 		if ( collation != null ) {
 			orderByElement.append( " " ).append( collation );
 		}
 		if ( order != null ) {
 			orderByElement.append( " " ).append( order );
 		}
 		if ( nulls != NullPrecedence.NONE ) {
 			orderByElement.append( " nulls " ).append( nulls.name().toLowerCase() );
 		}
 		return orderByElement.toString();
 	}
 
 	/**
 	 * Does this dialect require that parameters appearing in the <tt>SELECT</tt> clause be wrapped in <tt>cast()</tt>
 	 * calls to tell the db parser the expected type.
 	 *
 	 * @return True if select clause parameter must be cast()ed
 	 * @since 3.2
 	 */
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support asking the result set its positioning
 	 * information on forward only cursors.  Specifically, in the case of
 	 * scrolling fetches, Hibernate needs to use
 	 * {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst}.  Certain drivers do not
 	 * allow access to these methods for forward only cursors.
 	 * <p/>
 	 * NOTE : this is highly driver dependent!
 	 *
 	 * @return True if methods like {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst} are supported for forward
 	 * only cursors; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support definition of cascade delete constraints
 	 * which can cause circular chains?
 	 *
 	 * @return True if circular cascade delete constraints are supported; false
 	 * otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		return true;
 	}
 
 	/**
 	 * Are subselects supported as the left-hand-side (LHS) of
 	 * IN-predicates.
 	 * <p/>
 	 * In other words, is syntax like "... <subquery> IN (1, 2, 3) ..." supported?
 	 *
 	 * @return True if subselects can appear as the LHS of an in-predicate;
 	 * false otherwise.
 	 * @since 3.2
 	 */
 	public boolean  supportsSubselectAsInPredicateLHS() {
 		return true;
 	}
 
 	/**
 	 * Expected LOB usage pattern is such that I can perform an insert
 	 * via prepared statement with a parameter binding for a LOB value
 	 * without crazy casting to JDBC driver implementation-specific classes...
 	 * <p/>
 	 * Part of the trickiness here is the fact that this is largely
 	 * driver dependent.  For example, Oracle (which is notoriously bad with
 	 * LOB support in their drivers historically) actually does a pretty good
 	 * job with LOB support as of the 10.2.x versions of their drivers...
 	 *
 	 * @return True if normal LOB usage patterns can be used with this driver;
 	 * false if driver-specific hookiness needs to be applied.
 	 * @since 3.2
 	 */
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support propagating changes to LOB
 	 * values back to the database?  Talking about mutating the
 	 * internal value of the locator as opposed to supplying a new
 	 * locator instance...
 	 * <p/>
 	 * For BLOBs, the internal value might be changed by:
 	 * {@link java.sql.Blob#setBinaryStream},
 	 * {@link java.sql.Blob#setBytes(long, byte[])},
 	 * {@link java.sql.Blob#setBytes(long, byte[], int, int)},
 	 * or {@link java.sql.Blob#truncate(long)}.
 	 * <p/>
 	 * For CLOBs, the internal value might be changed by:
 	 * {@link java.sql.Clob#setAsciiStream(long)},
 	 * {@link java.sql.Clob#setCharacterStream(long)},
 	 * {@link java.sql.Clob#setString(long, String)},
 	 * {@link java.sql.Clob#setString(long, String, int, int)},
 	 * or {@link java.sql.Clob#truncate(long)}.
 	 * <p/>
 	 * NOTE : I do not know the correct answer currently for
 	 * databases which (1) are not part of the cruise control process
 	 * or (2) do not {@link #supportsExpectedLobUsagePattern}.
 	 *
 	 * @return True if the changes are propagated back to the
 	 * database; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsLobValueChangePropogation() {
 		// todo : pretty sure this is the same as the java.sql.DatabaseMetaData.locatorsUpdateCopy method added in JDBC 4, see HHH-6046
 		return true;
 	}
 
 	/**
 	 * Is it supported to materialize a LOB locator outside the transaction in
 	 * which it was created?
 	 * <p/>
 	 * Again, part of the trickiness here is the fact that this is largely
 	 * driver dependent.
 	 * <p/>
 	 * NOTE: all database I have tested which {@link #supportsExpectedLobUsagePattern()}
 	 * also support the ability to materialize a LOB outside the owning transaction...
 	 *
 	 * @return True if unbounded materialization is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support referencing the table being mutated in
 	 * a subquery.  The "table being mutated" is the table referenced in
 	 * an UPDATE or a DELETE query.  And so can that table then be
 	 * referenced in a subquery of said UPDATE/DELETE query.
 	 * <p/>
 	 * For example, would the following two syntaxes be supported:<ul>
 	 * <li>delete from TABLE_A where ID not in ( select ID from TABLE_A )</li>
 	 * <li>update TABLE_A set NON_ID = 'something' where ID in ( select ID from TABLE_A)</li>
 	 * </ul>
 	 *
 	 * @return True if this dialect allows references the mutating table from
 	 * a subquery.
 	 */
 	public boolean supportsSubqueryOnMutatingTable() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support an exists statement in the select clause?
 	 *
 	 * @return True if exists checks are allowed in the select clause; false otherwise.
 	 */
 	public boolean supportsExistsInSelect() {
 		return true;
 	}
 
 	/**
 	 * For the underlying database, is READ_COMMITTED isolation implemented by
 	 * forcing readers to wait for write locks to be released?
 	 *
 	 * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
 	 */
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return false;
 	}
 
 	/**
 	 * For the underlying database, is REPEATABLE_READ isolation implemented by
 	 * forcing writers to wait for read locks to be released?
 	 *
 	 * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
 	 */
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support using a JDBC bind parameter as an argument
 	 * to a function or procedure call?
 	 *
 	 * @return Returns {@code true} if the database supports accepting bind params as args, {@code false} otherwise. The
 	 * default is {@code true}.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean supportsBindAsCallableArgument() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support `count(a,b)`?
 	 *
 	 * @return True if the database supports counting tuples; false otherwise.
 	 */
 	public boolean supportsTupleCounts() {
 		return false;
 	}
 
-    /**
-     * Does this dialect support `count(distinct a,b)`?
-     *
-     * @return True if the database supports counting distinct tuples; false otherwise.
-     */
+	/**
+	 * Does this dialect support `count(distinct a,b)`?
+	 *
+	 * @return True if the database supports counting distinct tuples; false otherwise.
+	 */
 	public boolean supportsTupleDistinctCounts() {
 		// oddly most database in fact seem to, so true is the default.
 		return true;
 	}
 
 	/**
 	 * Return the limit that the underlying database places on the number elements in an {@code IN} predicate.
 	 * If the database defines no such limits, simply return zero or less-than-zero.
-	 * 
+	 *
 	 * @return int The limit, or zero-or-less to indicate no limit.
 	 */
 	public int getInExpressionCountLimit() {
 		return 0;
 	}
-	
+
 	/**
 	 * HHH-4635
 	 * Oracle expects all Lob values to be last in inserts and updates.
-	 * 
+	 *
 	 * @return boolean True of Lob values should be last, false if it
 	 * does not matter.
 	 */
 	public boolean forceLobAsLastValue() {
 		return false;
 	}
 
 	/**
 	 * Some dialects have trouble applying pessimistic locking depending upon what other query options are
 	 * specified (paging, ordering, etc).  This method allows these dialects to request that locking be applied
 	 * by subsequent selects.
 	 *
 	 * @return {@code true} indicates that the dialect requests that locking be applied by subsequent select;
 	 * {@code false} (the default) indicates that locking should be applied to the main SQL statement..
 	 */
 	public boolean useFollowOnLocking() {
 		return false;
 	}
-	
-	public UniqueDelegate getUniqueDelegate() {
-		return uniqueDelegate;
-	}
 
-	public String getNotExpression( String expression ) {
+	/**
+	 * Negate an expression
+	 *
+	 * @param expression The expression to negate
+	 *
+	 * @return The negated expression
+	 */
+	public String getNotExpression(String expression) {
 		return "not " + expression;
 	}
 
 	/**
+	 * Get the UniqueDelegate supported by this dialect
+	 *
+	 * @return The UniqueDelegate
+	 */
+	public UniqueDelegate getUniqueDelegate() {
+		return uniqueDelegate;
+	}
+
+	/**
 	 * Does this dialect support the <tt>UNIQUE</tt> column syntax?
 	 *
 	 * @return boolean
-	 * 
+	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsUnique() {
 		return true;
 	}
 
-    /**
-     * Does this dialect support adding Unique constraints via create and alter table ?
-     * 
-     * @return boolean
-     * 
-     * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
-     */
+	/**
+	 * Does this dialect support adding Unique constraints via create and alter table ?
+	 *
+	 * @return boolean
+	 *
+	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
+	 */
 	@Deprecated
 	public boolean supportsUniqueConstraintInCreateAlterTable() {
-	    return true;
+		return true;
 	}
 
-    /**
-     * The syntax used to add a unique constraint to a table.
-     *
-     * @param constraintName The name of the unique constraint.
-     * @return The "add unique" fragment
-     * 
-     * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
-     */
+	/**
+	 * The syntax used to add a unique constraint to a table.
+	 *
+	 * @param constraintName The name of the unique constraint.
+	 * @return The "add unique" fragment
+	 *
+	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
+	 */
 	@Deprecated
 	public String getAddUniqueConstraintString(String constraintName) {
-        return " add constraint " + constraintName + " unique ";
-    }
+		return " add constraint " + constraintName + " unique ";
+	}
 
 	/**
+	 * Is the combination of not-null and unique supported?
+	 *
+	 * @return deprecated
+	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsNotNullUnique() {
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/FirebirdDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/FirebirdDialect.java
index ddc6a787fe..c45d30b515 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/FirebirdDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/FirebirdDialect.java
@@ -1,53 +1,54 @@
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
 package org.hibernate.dialect;
 
-
 /**
  * An SQL dialect for Firebird.
  *
  * @author Reha CENANI
  */
 public class FirebirdDialect extends InterbaseDialect {
-
+	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop generator " + sequenceName;
 	}
 
+	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		return new StringBuilder( sql.length() + 20 )
 				.append( sql )
 				.insert( 6, hasOffset ? " first ? skip ?" : " first ?" )
 				.toString();
 	}
 
+	@Override
 	public boolean bindLimitParametersFirst() {
 		return true;
 	}
 
+	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/FrontBaseDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/FrontBaseDialect.java
index d6808c7a25..035fffd8d4 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/FrontBaseDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/FrontBaseDialect.java
@@ -1,132 +1,141 @@
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
 package org.hibernate.dialect;
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.persister.entity.Lockable;
 
 /**
  * An SQL Dialect for Frontbase.  Assumes you're using the latest version
  * of the FrontBase JDBC driver, available from <tt>http://frontbase.com/</tt>
  * <p>
  * <b>NOTE</b>: The latest JDBC driver is not always included with the
  * latest release of FrontBase.  Download the driver separately, and enjoy
  * the informative release notes.
  * <p>
  * This dialect was tested with JDBC driver version 2.3.1.  This driver
  * contains a bug that causes batches of updates to fail.  (The bug should be
  * fixed in the next release of the JDBC driver.)  If you are using JDBC driver
  * 2.3.1, you can work-around this problem by setting the following in your
  * <tt>hibernate.properties</tt> file: <tt>hibernate.jdbc.batch_size=15</tt>
  *
  * @author Ron Lussier <tt>rlussier@lenscraft.com</tt>
  */
 public class FrontBaseDialect extends Dialect {
 
+	/**
+	 * Constructs a FrontBaseDialect
+	 */
 	public FrontBaseDialect() {
 		super();
 
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BIGINT, "longint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "bit varying($l)" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
+	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade";
 	}
 
+	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	/**
-	 * Does this dialect support the <tt>FOR UPDATE</tt> syntax. No!
-	 *
-	 * @return false always. FrontBase doesn't support this syntax,
-	 * which was dropped with SQL92
+	 * FrontBase doesn't support this syntax, which was dropped with SQL92.
+	 * <p/>
+	 * {@inheritDoc}
 	 */
+	@Override
 	public String getForUpdateString() {
 		return "";
 	}
 
-	public String getCurrentTimestampCallString() {
+	@Override
+	public String getCurrentTimestampSelectString() {
 		// TODO : not sure this is correct, could not find docs on how to do this.
 		return "{?= call current_timestamp}";
 	}
 
+	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return true;
 	}
 
+	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// Frontbase has no known variation of a "SELECT ... FOR UPDATE" syntax...
 		if ( lockMode==LockMode.PESSIMISTIC_FORCE_INCREMENT) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_WRITE) {
 			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_READ) {
 			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC) {
 			return new OptimisticLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC_FORCE_INCREMENT) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
index 288e205985..e085a9ba18 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
@@ -1,393 +1,419 @@
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
 package org.hibernate.dialect;
 
 import org.hibernate.JDBCException;
 import org.hibernate.PessimisticLockException;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.StandardBasicTypes;
 import org.jboss.logging.Logger;
 
 import java.sql.SQLException;
 import java.sql.Types;
 
 /**
  * A dialect compatible with the H2 database.
  *
  * @author Thomas Mueller
  */
 public class H2Dialect extends Dialect {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, H2Dialect.class.getName());
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			H2Dialect.class.getName()
+	);
 
 	private final String querySequenceString;
 
+	/**
+	 * Constructs a H2Dialect
+	 */
 	public H2Dialect() {
 		super();
 
 		String querySequenceString = "select sequence_name from information_schema.sequences";
 		try {
 			// HHH-2300
 			final Class h2ConstantsClass = ReflectHelper.classForName( "org.h2.engine.Constants" );
-			final int majorVersion = ( Integer ) h2ConstantsClass.getDeclaredField( "VERSION_MAJOR" ).get( null );
-			final int minorVersion = ( Integer ) h2ConstantsClass.getDeclaredField( "VERSION_MINOR" ).get( null );
-			final int buildId = ( Integer ) h2ConstantsClass.getDeclaredField( "BUILD_ID" ).get( null );
+			final int majorVersion = (Integer) h2ConstantsClass.getDeclaredField( "VERSION_MAJOR" ).get( null );
+			final int minorVersion = (Integer) h2ConstantsClass.getDeclaredField( "VERSION_MINOR" ).get( null );
+			final int buildId = (Integer) h2ConstantsClass.getDeclaredField( "BUILD_ID" ).get( null );
 			if ( buildId < 32 ) {
 				querySequenceString = "select name from information_schema.sequences";
 			}
-            if ( ! ( majorVersion > 1 || minorVersion > 2 || buildId >= 139 ) ) {
+			if ( ! ( majorVersion > 1 || minorVersion > 2 || buildId >= 139 ) ) {
 				LOG.unsupportedMultiTableBulkHqlJpaql( majorVersion, minorVersion, buildId );
 			}
 		}
 		catch ( Exception e ) {
 			// probably H2 not in the classpath, though in certain app server environments it might just mean we are
 			// not using the correct classloader
 			LOG.undeterminedH2Version();
 		}
 
 		this.querySequenceString = querySequenceString;
 
 		registerColumnType( Types.BOOLEAN, "boolean" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.BINARY, "binary" );
 		registerColumnType( Types.BIT, "boolean" );
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "decimal($p,$s)" );
 		registerColumnType( Types.NUMERIC, "decimal($p,$s)" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
 		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
 		registerColumnType( Types.REAL, "real" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.VARBINARY, "binary($l)" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		// Aggregations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "avg", new AvgWithArgumentCastFunction( "double" ) );
 
 		// select topic, syntax from information_schema.help
 		// where section like 'Function%' order by section, topic
 		//
 		// see also ->  http://www.h2database.com/html/functions.html
 
 		// Numeric Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan2", new StandardSQLFunction( "atan2", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bitand", new StandardSQLFunction( "bitand", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bitor", new StandardSQLFunction( "bitor", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bitxor", new StandardSQLFunction( "bitxor", StandardBasicTypes.INTEGER ) );
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "compress", new StandardSQLFunction( "compress", StandardBasicTypes.BINARY ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "decrypt", new StandardSQLFunction( "decrypt", StandardBasicTypes.BINARY ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "encrypt", new StandardSQLFunction( "encrypt", StandardBasicTypes.BINARY ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "expand", new StandardSQLFunction( "compress", StandardBasicTypes.BINARY ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "hash", new StandardSQLFunction( "hash", StandardBasicTypes.BINARY ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "round", new StandardSQLFunction( "round", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "roundmagic", new StandardSQLFunction( "roundmagic", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "truncate", new StandardSQLFunction( "truncate", StandardBasicTypes.DOUBLE ) );
 
 		// String Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		registerFunction( "difference", new StandardSQLFunction( "difference", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hextoraw", new StandardSQLFunction( "hextoraw", StandardBasicTypes.STRING ) );
 		registerFunction( "insert", new StandardSQLFunction( "lower", StandardBasicTypes.STRING ) );
 		registerFunction( "left", new StandardSQLFunction( "left", StandardBasicTypes.STRING ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase", StandardBasicTypes.STRING ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim", StandardBasicTypes.STRING ) );
 		registerFunction( "octet_length", new StandardSQLFunction( "octet_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "position", new StandardSQLFunction( "position", StandardBasicTypes.INTEGER ) );
 		registerFunction( "rawtohex", new StandardSQLFunction( "rawtohex", StandardBasicTypes.STRING ) );
 		registerFunction( "repeat", new StandardSQLFunction( "repeat", StandardBasicTypes.STRING ) );
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 		registerFunction( "right", new StandardSQLFunction( "right", StandardBasicTypes.STRING ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "stringencode", new StandardSQLFunction( "stringencode", StandardBasicTypes.STRING ) );
 		registerFunction( "stringdecode", new StandardSQLFunction( "stringdecode", StandardBasicTypes.STRING ) );
 		registerFunction( "stringtoutf8", new StandardSQLFunction( "stringtoutf8", StandardBasicTypes.BINARY ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase", StandardBasicTypes.STRING ) );
 		registerFunction( "utf8tostring", new StandardSQLFunction( "utf8tostring", StandardBasicTypes.STRING ) );
 
 		// Time and Date Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "curtimestamp", new NoArgSQLFunction( "curtimestamp", StandardBasicTypes.TIME ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME ) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "datediff", new StandardSQLFunction( "datediff", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 
 		// System Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "database", new NoArgSQLFunction( "database", StandardBasicTypes.STRING ) );
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING ) );
 
 		getDefaultProperties().setProperty( AvailableSettings.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
-		getDefaultProperties().setProperty( AvailableSettings.NON_CONTEXTUAL_LOB_CREATION, "true" );  // http://code.google.com/p/h2database/issues/detail?id=235
+		// http://code.google.com/p/h2database/issues/detail?id=235
+		getDefaultProperties().setProperty( AvailableSettings.NON_CONTEXTUAL_LOB_CREATION, "true" );
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
+	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
+	@Override
 	public String getIdentityColumnString() {
-		return "generated by default as identity"; // not null is implicit
+		// not null is implicit
+		return "generated by default as identity";
 	}
 
+	@Override
 	public String getIdentitySelectString() {
 		return "call identity()";
 	}
 
+	@Override
 	public String getIdentityInsertString() {
 		return "null";
 	}
 
+	@Override
 	public String getForUpdateString() {
 		return " for update";
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
+	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
-		return new StringBuilder( sql.length() + 20 )
-				.append( sql )
-				.append( hasOffset ? " limit ? offset ?" : " limit ?" )
-				.toString();
+		return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
 	}
 
+	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
+	@Override
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsIfExistsAfterTableName() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
+	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
+	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
+	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "next value for " + sequenceName;
 	}
 
+	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "call next value for " + sequenceName;
 	}
 
+	@Override
 	public String getQuerySequencesString() {
 		return querySequenceString;
 	}
 
+	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
-	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
+	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 			// 23000: Check constraint violation: {0}
 			// 23001: Unique index or primary key violation: {0}
 			if ( sqle.getSQLState().startsWith( "23" ) ) {
 				final String message = sqle.getMessage();
-				int idx = message.indexOf( "violation: " );
+				final int idx = message.indexOf( "violation: " );
 				if ( idx > 0 ) {
 					constraintName = message.substring( idx + "violation: ".length() );
 				}
 			}
 			return constraintName;
 		}
 	};
-	
+
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		SQLExceptionConversionDelegate delegate = super.buildSQLExceptionConversionDelegate();
 		if (delegate == null) {
 			delegate = new SQLExceptionConversionDelegate() {
-
 				@Override
 				public JDBCException convert(SQLException sqlException, String message, String sql) {
-                    JDBCException exception = null;
+					final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 
-                    int errorCode = JdbcExceptionHelper.extractErrorCode(sqlException);
-
-                    if (40001 == errorCode) { // DEADLOCK DETECTED
-                        exception = new LockAcquisitionException(message, sqlException, sql);
-                    }
+					if (40001 == errorCode) {
+						// DEADLOCK DETECTED
+						return new LockAcquisitionException(message, sqlException, sql);
+					}
 
-                    if (50200 == errorCode) { // LOCK NOT AVAILABLE
-                        exception = new PessimisticLockException(message, sqlException, sql);
-                    }
+					if (50200 == errorCode) {
+						// LOCK NOT AVAILABLE
+						return new PessimisticLockException(message, sqlException, sql);
+					}
 
 					if ( 90006 == errorCode ) {
 						// NULL not allowed for column [90006-145]
 						final String constraintName = getViolatedConstraintNameExtracter().extractConstraintName( sqlException );
-						exception = new ConstraintViolationException( message, sqlException, sql, constraintName );
+						return new ConstraintViolationException( message, sqlException, sql, constraintName );
 					}
 
-					return exception;
+					return null;
 				}
 			};
 		}
 		return delegate;
 	}
 
 	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "create cached local temporary table if not exists";
 	}
 
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		// actually 2 different options are specified here:
 		//		1) [on commit drop] - says to drop the table on transaction commit
 		//		2) [transactional] - says to not perform an implicit commit of any current transaction
 		return "on commit drop transactional";
 	}
 
 	@Override
 	public Boolean performTemporaryTableDDLInIsolation() {
 		// explicitly create the table using the same connection and transaction
 		return Boolean.FALSE;
 	}
 
 	@Override
 	public boolean dropTemporaryTableAfterUse() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
+	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
+	@Override
 	public String getCurrentTimestampSelectString() {
 		return "call current_timestamp()";
 	}
 
+	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		// see http://groups.google.com/group/h2-database/browse_thread/thread/562d8a49e2dabe99?hl=en
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
index 10435c2aa9..18bc9b3cbd 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
@@ -1,710 +1,644 @@
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
 package org.hibernate.dialect;
 
 import java.io.Serializable;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.type.StandardBasicTypes;
 import org.jboss.logging.Logger;
 
 /**
  * An SQL dialect compatible with HSQLDB (HyperSQL).
  * <p/>
  * Note this version supports HSQLDB version 1.8 and higher, only.
  * <p/>
  * Enhancements to version 3.5.0 GA to provide basic support for both HSQLDB 1.8.x and 2.x
  * Does not works with Hibernate 3.2 - 3.4 without alteration.
  *
  * @author Christoph Sturm
  * @author Phillip Baird
  * @author Fred Toussi
  */
 public class HSQLDialect extends Dialect {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, HSQLDialect.class.getName());
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			HSQLDialect.class.getName()
+	);
 
 	/**
 	 * version is 18 for 1.8 or 20 for 2.0
 	 */
 	private int hsqldbVersion = 18;
 
 
+	/**
+	 * Constructs a HSQLDialect
+	 */
 	public HSQLDialect() {
 		super();
 
 		try {
-			Class props = ReflectHelper.classForName( "org.hsqldb.persist.HsqlDatabaseProperties" );
-			String versionString = (String) props.getDeclaredField( "THIS_VERSION" ).get( null );
+			final Class props = ReflectHelper.classForName( "org.hsqldb.persist.HsqlDatabaseProperties" );
+			final String versionString = (String) props.getDeclaredField( "THIS_VERSION" ).get( null );
 
 			hsqldbVersion = Integer.parseInt( versionString.substring( 0, 1 ) ) * 10;
 			hsqldbVersion += Integer.parseInt( versionString.substring( 2, 3 ) );
 		}
 		catch ( Throwable e ) {
 			// must be a very old version
 		}
 
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.BINARY, "binary($l)" );
 		registerColumnType( Types.BIT, "bit" );
-        registerColumnType( Types.BOOLEAN, "boolean" );
+		registerColumnType( Types.BOOLEAN, "boolean" );
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.DATE, "date" );
 
 		registerColumnType( Types.DECIMAL, "decimal($p,$s)" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
 		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.VARBINARY, "varbinary($l)" );
 
 		if ( hsqldbVersion < 20 ) {
 			registerColumnType( Types.NUMERIC, "numeric" );
 		}
 		else {
 			registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		}
 
 		//HSQL has no Blob/Clob support .... but just put these here for now!
 		if ( hsqldbVersion < 20 ) {
 			registerColumnType( Types.BLOB, "longvarbinary" );
 			registerColumnType( Types.CLOB, "longvarchar" );
 		}
 		else {
 			registerColumnType( Types.BLOB, "blob($l)" );
 			registerColumnType( Types.CLOB, "clob($l)" );
 		}
 
 		// aggregate functions
 		registerFunction( "avg", new AvgWithArgumentCastFunction( "double" ) );
 
 		// string functions
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "cast(?1 as varchar(256))" ) );
 		registerFunction( "to_char", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
 		registerFunction( "rawtohex", new StandardSQLFunction( "rawtohex" ) );
 		registerFunction( "hextoraw", new StandardSQLFunction( "hextoraw" ) );
 
 		// system functions
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING ) );
 		registerFunction( "database", new NoArgSQLFunction( "database", StandardBasicTypes.STRING ) );
 
 		// datetime functions
 		if ( hsqldbVersion < 20 ) {
-		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.DATE, false ) );
+			registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.DATE, false ) );
 		} else {
-		    registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
+			registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
 		}
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction(
 				"current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP, false )
 		);
 		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
 		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "second", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "cast(second(?1) as int)" ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 
 		// numeric functions
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new StandardSQLFunction( "rand", StandardBasicTypes.FLOAT ) );
 
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 		registerFunction( "roundmagic", new StandardSQLFunction( "roundmagic" ) );
 		registerFunction( "truncate", new StandardSQLFunction( "truncate" ) );
 
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 
 		// special functions
 		// from v. 2.2.0 ROWNUM() is supported in all modes as the equivalent of Oracle ROWNUM
 		if ( hsqldbVersion > 21 ) {
-		    registerFunction("rownum",
-				     new NoArgSQLFunction("rownum", StandardBasicTypes.INTEGER));
+			registerFunction( "rownum", new NoArgSQLFunction( "rownum", StandardBasicTypes.INTEGER ) );
 		}
 
 		// function templates
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
+	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
+	@Override
 	public String getIdentityColumnString() {
-		return "generated by default as identity (start with 1)"; //not null is implicit
+		//not null is implicit
+		return "generated by default as identity (start with 1)";
 	}
 
+	@Override
 	public String getIdentitySelectString() {
 		return "call identity()";
 	}
 
+	@Override
 	public String getIdentityInsertString() {
 		return hsqldbVersion < 20 ? "null" : "default";
 	}
 
+	@Override
 	public boolean supportsLockTimeouts() {
 		return false;
 	}
 
+	@Override
 	public String getForUpdateString() {
 		if ( hsqldbVersion >= 20 ) {
 			return " for update";
 		}
 		else {
 			return "";
 		}
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
+	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		if ( hsqldbVersion < 20 ) {
 			return new StringBuilder( sql.length() + 10 )
 					.append( sql )
 					.insert(
 							sql.toLowerCase().indexOf( "select" ) + 6,
 							hasOffset ? " limit ? ?" : " top ?"
 					)
 					.toString();
 		}
 		else {
-			return new StringBuilder( sql.length() + 20 )
-					.append( sql )
-					.append( hasOffset ? " offset ? limit ?" : " limit ?" )
-					.toString();
+			return sql + (hasOffset ? " offset ? limit ?" : " limit ?");
 		}
 	}
 
+	@Override
 	public boolean bindLimitParametersFirst() {
 		return hsqldbVersion < 20;
 	}
 
+	@Override
 	public boolean supportsIfExistsAfterTableName() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsColumnCheck() {
 		return hsqldbVersion >= 20;
 	}
 
+	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
+	@Override
 	protected String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
+	@Override
 	protected String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
+	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "next value for " + sequenceName;
 	}
 
+	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "call next value for " + sequenceName;
 	}
 
+	@Override
 	public String getQuerySequencesString() {
 		// this assumes schema support, which is present in 1.8.0 and later...
 		return "select sequence_name from information_schema.system_sequences";
 	}
 
+	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return hsqldbVersion < 20 ? EXTRACTER_18 : EXTRACTER_20;
 	}
 
-	private static ViolatedConstraintNameExtracter EXTRACTER_18 = new TemplatedViolatedConstraintNameExtracter() {
-
-		/**
-		 * Extract the name of the violated constraint from the given SQLException.
-		 *
-		 * @param sqle The exception that was the result of the constraint violation.
-		 * @return The extracted constraint name.
-		 */
+	private static final ViolatedConstraintNameExtracter EXTRACTER_18 = new TemplatedViolatedConstraintNameExtracter() {
+		@Override
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 
-			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
+			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
 			if ( errorCode == -8 ) {
 				constraintName = extractUsingTemplate(
 						"Integrity constraint violation ", " table:", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -9 ) {
 				constraintName = extractUsingTemplate(
 						"Violation of unique index: ", " in statement [", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -104 ) {
 				constraintName = extractUsingTemplate(
 						"Unique constraint violation: ", " in statement [", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -177 ) {
 				constraintName = extractUsingTemplate(
 						"Integrity constraint violation - no parent ", " table:",
 						sqle.getMessage()
 				);
 			}
 			return constraintName;
 		}
 
 	};
 
 	/**
 	 * HSQLDB 2.0 messages have changed
 	 * messages may be localized - therefore use the common, non-locale element " table: "
 	 */
-	private static ViolatedConstraintNameExtracter EXTRACTER_20 = new TemplatedViolatedConstraintNameExtracter() {
-
+	private static final ViolatedConstraintNameExtracter EXTRACTER_20 = new TemplatedViolatedConstraintNameExtracter() {
+		@Override
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 
-			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
+			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
 			if ( errorCode == -8 ) {
 				constraintName = extractUsingTemplate(
 						"; ", " table: ", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -9 ) {
 				constraintName = extractUsingTemplate(
 						"; ", " table: ", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -104 ) {
 				constraintName = extractUsingTemplate(
 						"; ", " table: ", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -177 ) {
 				constraintName = extractUsingTemplate(
 						"; ", " table: ", sqle.getMessage()
 				);
 			}
 			return constraintName;
 		}
 	};
 
+	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		String literal;
 		switch ( sqlType ) {
-		        case Types.LONGVARCHAR:
+			case Types.LONGVARCHAR:
 			case Types.VARCHAR:
 			case Types.CHAR:
 				literal = "cast(null as varchar(100))";
 				break;
-		        case Types.LONGVARBINARY:
-		        case Types.VARBINARY:
-		        case Types.BINARY:
+			case Types.LONGVARBINARY:
+			case Types.VARBINARY:
+			case Types.BINARY:
 				literal = "cast(null as varbinary(100))";
 				break;
-		        case Types.CLOB:
+			case Types.CLOB:
 				literal = "cast(null as clob)";
 				break;
-		        case Types.BLOB:
+			case Types.BLOB:
 				literal = "cast(null as blob)";
 				break;
 			case Types.DATE:
 				literal = "cast(null as date)";
 				break;
 			case Types.TIMESTAMP:
 				literal = "cast(null as timestamp)";
 				break;
-		        case Types.BOOLEAN:
+			case Types.BOOLEAN:
 				literal = "cast(null as boolean)";
 				break;
-		        case Types.BIT:
+			case Types.BIT:
 				literal = "cast(null as bit)";
 				break;
 			case Types.TIME:
 				literal = "cast(null as time)";
 				break;
 			default:
 				literal = "cast(null as int)";
 		}
 		return literal;
 	}
 
-    public boolean supportsUnionAll() {
-        return true;
-    }
+	@Override
+	public boolean supportsUnionAll() {
+		return true;
+	}
 
 	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
 	// Hibernate uses this information for temporary tables that it uses for its own operations
 	// therefore the appropriate strategy is taken with different versions of HSQLDB
 
 	// All versions of HSQLDB support GLOBAL TEMPORARY tables where the table
 	// definition is shared by all users but data is private to the session
 	// HSQLDB 2.0 also supports session-based LOCAL TEMPORARY tables where
 	// the definition and data is private to the session and table declaration
 	// can happen in the middle of a transaction
 
-	/**
-	 * Does this dialect support temporary tables?
-	 *
-	 * @return True if temp tables are supported; false otherwise.
-	 */
+	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
-	/**
-	 * With HSQLDB 2.0, the table name is qualified with MODULE to assist the drop
-	 * statement (in-case there is a global name beginning with HT_)
-	 *
-	 * @param baseTableName The table name from which to base the temp table name.
-	 *
-	 * @return The generated temp table name.
-	 */
+	@Override
 	public String generateTemporaryTableName(String baseTableName) {
 		if ( hsqldbVersion < 20 ) {
 			return "HT_" + baseTableName;
 		}
 		else {
+			// With HSQLDB 2.0, the table name is qualified with MODULE to assist the drop
+			// statement (in-case there is a global name beginning with HT_)
 			return "MODULE.HT_" + baseTableName;
 		}
 	}
 
-	/**
-	 * Command used to create a temporary table.
-	 *
-	 * @return The command used to create a temporary table.
-	 */
+	@Override
 	public String getCreateTemporaryTableString() {
 		if ( hsqldbVersion < 20 ) {
 			return "create global temporary table";
 		}
 		else {
 			return "declare local temporary table";
 		}
 	}
 
-	/**
-	 * No fragment is needed if data is not needed beyond commit, otherwise
-	 * should add "on commit preserve rows"
-	 *
-	 * @return Any required postfix.
-	 */
+	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "";
 	}
 
-	/**
-	 * Command used to drop a temporary table.
-	 *
-	 * @return The command used to drop a temporary table.
-	 */
+	@Override
 	public String getDropTemporaryTableString() {
 		return "drop table";
 	}
 
-	/**
-	 * Different behavior for GLOBAL TEMPORARY (1.8) and LOCAL TEMPORARY (2.0)
-	 * <p/>
-	 * Possible return values and their meanings:<ul>
-	 * <li>{@link Boolean#TRUE} - Unequivocally, perform the temporary table DDL
-	 * in isolation.</li>
-	 * <li>{@link Boolean#FALSE} - Unequivocally, do <b>not</b> perform the
-	 * temporary table DDL in isolation.</li>
-	 * <li><i>null</i> - defer to the JDBC driver response in regards to
-	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}</li>
-	 * </ul>
-	 *
-	 * @return see the result matrix above.
-	 */
+	@Override
 	public Boolean performTemporaryTableDDLInIsolation() {
+		// Different behavior for GLOBAL TEMPORARY (1.8) and LOCAL TEMPORARY (2.0)
 		if ( hsqldbVersion < 20 ) {
 			return Boolean.TRUE;
 		}
 		else {
 			return Boolean.FALSE;
 		}
 	}
 
-	/**
-	 * Do we need to drop the temporary table after use?
-	 *
-	 * todo - clarify usage by Hibernate
-	 *
-	 * Version 1.8 GLOBAL TEMPORARY table definitions persist beyond the end
-	 * of the session (by default, data is cleared at commit).<p>
-	 *
-	 * Version 2.x LOCAL TEMPORARY table definitions do not persist beyond
-	 * the end of the session (by default, data is cleared at commit).
-	 *
-	 * @return True if the table should be dropped.
-	 */
+	@Override
 	public boolean dropTemporaryTableAfterUse() {
+		// Version 1.8 GLOBAL TEMPORARY table definitions persist beyond the end
+		// of the session (by default, data is cleared at commit).<p>
+		//
+		// Version 2.x LOCAL TEMPORARY table definitions do not persist beyond
+		// the end of the session (by default, data is cleared at commit).
 		return true;
 	}
 
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * HSQLDB 1.8.x requires CALL CURRENT_TIMESTAMP but this should not
 	 * be treated as a callable statement. It is equivalent to
 	 * "select current_timestamp from dual" in some databases.
 	 * HSQLDB 2.0 also supports VALUES CURRENT_TIMESTAMP
-	 *
-	 * @return True if the current timestamp can be retrieved; false otherwise.
+	 * <p/>
+	 * {@inheritDoc}
 	 */
+	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
-	/**
-	 * Should the value returned by {@link #getCurrentTimestampSelectString}
-	 * be treated as callable.  Typically this indicates that JDBC escape
-	 * syntax is being used...<p>
-	 *
-	 * CALL CURRENT_TIMESTAMP is used but this should not
-	 * be treated as a callable statement.
-	 *
-	 * @return True if the {@link #getCurrentTimestampSelectString} return
-	 *         is callable; false otherwise.
-	 */
+	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
-	/**
-	 * Retrieve the command used to retrieve the current timestamp from the
-	 * database.
-	 *
-	 * @return The command.
-	 */
+	@Override
 	public String getCurrentTimestampSelectString() {
 		return "call current_timestamp";
 	}
 
-	/**
-	 * The name of the database-specific SQL function for retrieving the
-	 * current timestamp.
-	 *
-	 * @return The function name.
-	 */
+	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
 	}
 
 	/**
 	 * For HSQLDB 2.0, this is a copy of the base class implementation.
 	 * For HSQLDB 1.8, only READ_UNCOMMITTED is supported.
-	 *
-	 * @param lockable The persister for the entity to be locked.
-	 * @param lockMode The type of lock to be acquired.
-	 *
-	 * @return The appropriate locking strategy.
-	 *
-	 * @since 3.2
+	 * <p/>
+	 * {@inheritDoc}
 	 */
+	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT ) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_WRITE ) {
 			return new PessimisticWriteSelectLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
 			return new PessimisticReadSelectLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC ) {
 			return new OptimisticLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT ) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 
 		if ( hsqldbVersion < 20 ) {
 			return new ReadUncommittedLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
-	public static class ReadUncommittedLockingStrategy extends SelectLockingStrategy {
+	private static class ReadUncommittedLockingStrategy extends SelectLockingStrategy {
 		public ReadUncommittedLockingStrategy(Lockable lockable, LockMode lockMode) {
 			super( lockable, lockMode );
 		}
 
 		public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session)
 				throws StaleObjectStateException, JDBCException {
 			if ( getLockMode().greaterThan( LockMode.READ ) ) {
 				LOG.hsqldbSupportsOnlyReadCommittedIsolation();
 			}
 			super.lock( id, version, object, timeout, session );
 		}
 	}
 
+	@Override
 	public boolean supportsCommentOn() {
 		return hsqldbVersion >= 20;
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
-	/**
-	 * todo - needs usage clarification
-	 *
-	 * If the SELECT statement is always part of a UNION, then the type of
-	 * parameter is resolved by v. 2.0, but not v. 1.8 (assuming the other
-	 * SELECT in the UNION has a column reference in the same position and
-	 * can be type-resolved).
-	 *
-	 * On the other hand if the SELECT statement is isolated, all versions of
-	 * HSQLDB require casting for "select ? from .." to work.
-	 *
-	 * @return True if select clause parameter must be cast()ed
-	 *
-	 * @since 3.2
-	 */
+	@Override
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return true;
 	}
 
-	/**
-	 * For the underlying database, is READ_COMMITTED isolation implemented by
-	 * forcing readers to wait for write locks to be released?
-	 *
-	 * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
-	 */
+	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return hsqldbVersion >= 20;
 	}
 
-	/**
-	 * For the underlying database, is REPEATABLE_READ isolation implemented by
-	 * forcing writers to wait for read locks to be released?
-	 *
-	 * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
-	 */
+	@Override
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return hsqldbVersion >= 20;
 	}
 
-
+	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
-    public String toBooleanValueString(boolean bool) {
-        return String.valueOf( bool );
-    }
+	@Override
+	public String toBooleanValueString(boolean bool) {
+		return String.valueOf( bool );
+	}
 
+	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
index 614658cc05..6a665bdd4a 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
@@ -1,274 +1,289 @@
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
 package org.hibernate.dialect;
+
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.MappingException;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * Informix dialect.<br>
  * <br>
  * Seems to work with Informix Dynamic Server Version 7.31.UD3,  Informix JDBC driver version 2.21JC3.
  *
  * @author Steve Molitor
  */
 public class InformixDialect extends Dialect {
 
 	/**
 	 * Creates new <code>InformixDialect</code> instance. Sets up the JDBC /
 	 * Informix type mappings.
 	 */
 	public InformixDialect() {
 		super();
 
-		registerColumnType(Types.BIGINT, "int8");
-		registerColumnType(Types.BINARY, "byte");
-		registerColumnType(Types.BIT, "smallint"); // Informix doesn't have a bit type
-		registerColumnType(Types.CHAR, "char($l)");
-		registerColumnType(Types.DATE, "date");
-		registerColumnType(Types.DECIMAL, "decimal");
-        registerColumnType(Types.DOUBLE, "float");
-        registerColumnType(Types.FLOAT, "smallfloat");
-		registerColumnType(Types.INTEGER, "integer");
-		registerColumnType(Types.LONGVARBINARY, "blob"); // or BYTE
-		registerColumnType(Types.LONGVARCHAR, "clob"); // or TEXT?
-		registerColumnType(Types.NUMERIC, "decimal"); // or MONEY
-		registerColumnType(Types.REAL, "smallfloat");
-		registerColumnType(Types.SMALLINT, "smallint");
-		registerColumnType(Types.TIMESTAMP, "datetime year to fraction(5)");
-		registerColumnType(Types.TIME, "datetime hour to second");
-		registerColumnType(Types.TINYINT, "smallint");
-		registerColumnType(Types.VARBINARY, "byte");
-		registerColumnType(Types.VARCHAR, "varchar($l)");
-		registerColumnType(Types.VARCHAR, 255, "varchar($l)");
-		registerColumnType(Types.VARCHAR, 32739, "lvarchar($l)");
+		registerColumnType( Types.BIGINT, "int8" );
+		registerColumnType( Types.BINARY, "byte" );
+		// Informix doesn't have a bit type
+		registerColumnType( Types.BIT, "smallint" );
+		registerColumnType( Types.CHAR, "char($l)" );
+		registerColumnType( Types.DATE, "date" );
+		registerColumnType( Types.DECIMAL, "decimal" );
+		registerColumnType( Types.DOUBLE, "float" );
+		registerColumnType( Types.FLOAT, "smallfloat" );
+		registerColumnType( Types.INTEGER, "integer" );
+		// or BYTE
+		registerColumnType( Types.LONGVARBINARY, "blob" );
+		// or TEXT?
+		registerColumnType( Types.LONGVARCHAR, "clob" );
+		// or MONEY
+		registerColumnType( Types.NUMERIC, "decimal" );
+		registerColumnType( Types.REAL, "smallfloat" );
+		registerColumnType( Types.SMALLINT, "smallint" );
+		registerColumnType( Types.TIMESTAMP, "datetime year to fraction(5)" );
+		registerColumnType( Types.TIME, "datetime hour to second" );
+		registerColumnType( Types.TINYINT, "smallint" );
+		registerColumnType( Types.VARBINARY, "byte" );
+		registerColumnType( Types.VARCHAR, "varchar($l)" );
+		registerColumnType( Types.VARCHAR, 255, "varchar($l)" );
+		registerColumnType( Types.VARCHAR, 32739, "lvarchar($l)" );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
+	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
-	public String getIdentitySelectString(String table, String column, int type) 
-	throws MappingException {
-		return type==Types.BIGINT ?
-			"select dbinfo('serial8') from informix.systables where tabid=1" :
-			"select dbinfo('sqlca.sqlerrd1') from informix.systables where tabid=1";
+	@Override
+	public String getIdentitySelectString(String table, String column, int type)
+			throws MappingException {
+		return type == Types.BIGINT
+				? "select dbinfo('serial8') from informix.systables where tabid=1"
+				: "select dbinfo('sqlca.sqlerrd1') from informix.systables where tabid=1";
 	}
 
+	@Override
 	public String getIdentityColumnString(int type) throws MappingException {
-		return type==Types.BIGINT ?
-			"serial8 not null" :
-			"serial not null";
+		return type == Types.BIGINT ?
+				"serial8 not null" :
+				"serial not null";
 	}
 
+	@Override
 	public boolean hasDataTypeInIdentityColumn() {
 		return false;
 	}
 
 	/**
-	 * The syntax used to add a foreign key constraint to a table.
 	 * Informix constraint name must be at the end.
-	 * @return String
+	 * <p/>
+	 * {@inheritDoc}
 	 */
+	@Override
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
-		StringBuilder result = new StringBuilder( 30 )
+		final StringBuilder result = new StringBuilder( 30 )
 				.append( " add constraint " )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			result.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		result.append( " constraint " ).append( constraintName );
 
 		return result.toString();
 	}
 
 	/**
-	 * The syntax used to add a primary key constraint to a table.
 	 * Informix constraint name must be at the end.
-	 * @return String
+	 * <p/>
+	 * {@inheritDoc}
 	 */
+	@Override
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint primary key constraint " + constraintName + " ";
 	}
 
+	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
+
+	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
 
+	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from informix.systables where tabid=1";
 	}
 
+	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
+	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
+	@Override
 	public String getQuerySequencesString() {
 		return "select tabname from informix.systables where tabtype='Q'";
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
+	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
+	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( querySelect.toLowerCase().indexOf( "select" ) + 6, " first " + limit )
 				.toString();
 	}
 
+	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
+	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
-        return EXTRACTER;
+		return EXTRACTER;
 	}
 
-	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
-
-		/**
-		 * Extract the name of the violated constraint from the given SQLException.
-		 *
-		 * @param sqle The exception that was the result of the constraint violation.
-		 * @return The extracted constraint name.
-		 */
+	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
+		@Override
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
-			
-			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
+			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
+
 			if ( errorCode == -268 ) {
 				constraintName = extractUsingTemplate( "Unique constraint (", ") violated.", sqle.getMessage() );
 			}
 			else if ( errorCode == -691 ) {
-				constraintName = extractUsingTemplate( "Missing key in referenced table for referential constraint (", ").", sqle.getMessage() );
+				constraintName = extractUsingTemplate(
+						"Missing key in referenced table for referential constraint (",
+						").",
+						sqle.getMessage()
+				);
 			}
 			else if ( errorCode == -692 ) {
-				constraintName = extractUsingTemplate( "Key value for constraint (", ") is still being referenced.", sqle.getMessage() );
+				constraintName = extractUsingTemplate(
+						"Key value for constraint (",
+						") is still being referenced.",
+						sqle.getMessage()
+				);
 			}
-			
-			if (constraintName != null) {
+
+			if ( constraintName != null ) {
 				// strip table-owner because Informix always returns constraint names as "<table-owner>.<constraint-name>"
-				int i = constraintName.indexOf('.');
-				if (i != -1) {
-					constraintName = constraintName.substring(i + 1);
+				final int i = constraintName.indexOf( '.' );
+				if ( i != -1 ) {
+					constraintName = constraintName.substring( i + 1 );
 				}
 			}
 
 			return constraintName;
 		}
 
 	};
 
+	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
+	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
+	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select distinct current timestamp from informix.systables";
 	}
 
-	/**
-	 * Overrides {@link Dialect#supportsTemporaryTables()} to return
-	 * {@code true} when invoked.
-	 *
-	 * @return {@code true} when invoked
-	 */
+	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
-	/**
-	 * Overrides {@link Dialect#getCreateTemporaryTableString()} to
-	 * return "{@code create temp table}" when invoked.
-	 *
-	 * @return "{@code create temp table}" when invoked
-	 */
+	@Override
 	public String getCreateTemporaryTableString() {
 		return "create temp table";
 	}
 
-	/**
-	 * Overrides {@link Dialect#getCreateTemporaryTablePostfix()} to
-	 * return "{@code with no log}" when invoked.
-	 *
-	 * @return "{@code with no log}" when invoked
-	 */
+	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "with no log";
 	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Ingres10Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Ingres10Dialect.java
index 4f69d6f7f6..07433fdb2b 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Ingres10Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Ingres10Dialect.java
@@ -1,101 +1,102 @@
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
 package org.hibernate.dialect;
+
 import java.sql.Types;
 import java.util.Properties;
 
 import org.hibernate.cfg.Environment;
 
 /**
  * A SQL dialect for Ingres 10 and later versions.
  * <p/>
  * Changes:
  * <ul>
  * <li>Add native BOOLEAN type support</li>
  * <li>Add identity column support</li>
  * </ul>
  *
  * @author Raymond Fan
  */
 public class Ingres10Dialect extends Ingres9Dialect {
-    public Ingres10Dialect() {
-        super();
-        registerBooleanSupport();
-    }
-
-    // Boolean column type support
-
-    /**
-     * The SQL literal value to which this database maps boolean values.
-     *
-     * @param bool The boolean value
-     * @return The appropriate SQL literal.
-     */
-    public String toBooleanValueString(boolean bool) {
-        return bool ? "true" : "false";
-    }
-
-    protected void registerBooleanSupport() {
-        // Column type
-
-        // Boolean type (mapping/BooleanType) mapping maps SQL BIT to Java
-        // Boolean. In order to create a boolean column, BIT needs to be mapped
-        // to boolean as well, similar to H2Dialect.
-        registerColumnType( Types.BIT, "boolean" );
-        registerColumnType( Types.BOOLEAN, "boolean" );
+	/**
+	 * Constructs a Ingres10Dialect
+	 */
+	public Ingres10Dialect() {
+		super();
+		registerBooleanSupport();
+		registerDefaultProperties();
+	}
 
-        // Functions
+	protected void registerBooleanSupport() {
+		// Boolean type (mapping/BooleanType) mapping maps SQL BIT to Java
+		// Boolean. In order to create a boolean column, BIT needs to be mapped
+		// to boolean as well, similar to H2Dialect.
+		registerColumnType( Types.BIT, "boolean" );
+		registerColumnType( Types.BOOLEAN, "boolean" );
+	}
 
-        // true, false and unknown are now valid values
-        // Remove the query substitutions previously added in IngresDialect.
-        Properties properties = getDefaultProperties();
-        String querySubst = properties.getProperty(Environment.QUERY_SUBSTITUTIONS);
-        if (querySubst != null) {
-            String newQuerySubst = querySubst.replace("true=1,false=0","");
-            properties.setProperty(Environment.QUERY_SUBSTITUTIONS, newQuerySubst);
-        }
-    }
+	private void registerDefaultProperties() {
+		// true, false and unknown are now valid values
+		// Remove the query substitutions previously added in IngresDialect.
+		final Properties properties = getDefaultProperties();
+		final String querySubst = properties.getProperty( Environment.QUERY_SUBSTITUTIONS );
+		if ( querySubst != null ) {
+			final String newQuerySubst = querySubst.replace( "true=1,false=0", "" );
+			properties.setProperty( Environment.QUERY_SUBSTITUTIONS, newQuerySubst );
+		}
+	}
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
+	public String toBooleanValueString(boolean bool) {
+		return bool ? "true" : "false";
+	}
+
+	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
+	@Override
 	public boolean hasDataTypeInIdentityColumn() {
 		return true;
 	}
 
+	@Override
 	public String getIdentitySelectString() {
 		return "select last_identity()";
 	}
 
+	@Override
 	public String getIdentityColumnString() {
 		return "not null generated by default as identity";
 	}
 
+	@Override
 	public String getIdentityInsertString() {
 		return "default";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java
index a9de59008a..b335bdf50e 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java
@@ -1,252 +1,184 @@
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
 package org.hibernate.dialect;
+
 import java.sql.Types;
 
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A SQL dialect for Ingres 9.3 and later versions.
- * <p />
+ * <p/>
  * Changes:
  * <ul>
  * <li>Support for the SQL functions current_time, current_timestamp and current_date added</li>
  * <li>Type mapping of <code>Types.TIMESTAMP</code> changed from "timestamp with time zone" to "timestamp(9) with time zone"</li>
  * <li>Improved handling of "SELECT...FOR UPDATE" statements</li>
  * <li>Added support for pooled sequences</li>
  * <li>Added support for SELECT queries with limit and offset</li>
  * <li>Added getIdentitySelectString</li>
  * <li>Modified concatination operator</li>
  * </ul>
- * 
+ *
  * @author Enrico Schenk
  * @author Raymond Fan
  */
 public class Ingres9Dialect extends IngresDialect {
-    public Ingres9Dialect() {
-        super();
-        registerDateTimeFunctions();
-        registerDateTimeColumnTypes();
-        registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
-    }
+	/**
+	 * Constructs a Ingres9Dialect
+	 */
+	public Ingres9Dialect() {
+		super();
+		registerDateTimeFunctions();
+		registerDateTimeColumnTypes();
+		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
+	}
 
 	/**
 	 * Register functions current_time, current_timestamp, current_date
 	 */
 	protected void registerDateTimeFunctions() {
-		registerFunction("current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false));
-		registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false));
-		registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false));
+		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
+		registerFunction(
+				"current_timestamp", new NoArgSQLFunction(
+				"current_timestamp",
+				StandardBasicTypes.TIMESTAMP,
+				false
+		)
+		);
+		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 	}
 
 	/**
 	 * Register column types date, time, timestamp
 	 */
 	protected void registerDateTimeColumnTypes() {
-		registerColumnType(Types.DATE, "ansidate");
-		registerColumnType(Types.TIMESTAMP, "timestamp(9) with time zone");
+		registerColumnType( Types.DATE, "ansidate" );
+		registerColumnType( Types.TIMESTAMP, "timestamp(9) with time zone" );
 	}
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	/**
-	 * Does this dialect support <tt>FOR UPDATE</tt> in conjunction with outer
-	 * joined rows?
-	 * 
-	 * @return True if outer joined rows can be locked via <tt>FOR UPDATE</tt>.
-	 */
+	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
-	/**
-	 * Is <tt>FOR UPDATE OF</tt> syntax supported?
-	 * 
-	 * @return True if the database supports <tt>FOR UPDATE OF</tt> syntax;
-	 *         false otherwise.
-	 */
+	@Override
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-    /**
-     * Get the select command used to retrieve the last generated sequence
-     * value.
-     *
-     * @return Statement to retrieve last generated sequence value
-     */
-    public String getIdentitySelectString() {
-         return "select last_identity()";
-    }
+	@Override
+	public String getIdentitySelectString() {
+		return "select last_identity()";
+	}
 
-	/**
-	 * Get the select command used retrieve the names of all sequences.
-	 * 
-	 * @return The select command; or null if sequences are not supported.
-	 * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
-	 */
+	@Override
 	public String getQuerySequencesString() {
 		return "select seq_name from iisequences";
 	}
 
-	/**
-	 * Does this dialect support "pooled" sequences. Not aware of a better name
-	 * for this. Essentially can we specify the initial and increment values?
-	 * 
-	 * @return True if such "pooled" sequences are supported; false otherwise.
-	 * @see #getCreateSequenceStrings(String, int, int)
-	 * @see #getCreateSequenceString(String, int, int)
-	 */
+	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	/**
-	 * Should the value returned by {@link #getCurrentTimestampSelectString} be
-	 * treated as callable. Typically this indicates that JDBC escape sytnax is
-	 * being used...
-	 * 
-	 * @return True if the {@link #getCurrentTimestampSelectString} return is
-	 *         callable; false otherwise.
-	 */
+	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
-	/**
-	 * Does this dialect support a way to retrieve the database's current
-	 * timestamp value?
-	 * 
-	 * @return True if the current timestamp can be retrieved; false otherwise.
-	 */
+	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
-	/**
-	 * Retrieve the command used to retrieve the current timestammp from the
-	 * database.
-	 * 
-	 * @return The command.
-	 */
+	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select current_timestamp";
 	}
 
-	/**
-	 * Expression for current_timestamp
-	 */
+	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		return "current_timestamp";
 	}
 
 	// union subclass support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	/**
-	 * Does this dialect support UNION ALL, which is generally a faster variant
-	 * of UNION?
-	 * 
-	 * @return True if UNION ALL is supported; false otherwise.
-	 */
+	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	// Informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	/**
-	 * For the underlying database, is READ_COMMITTED isolation implemented by
-	 * forcing readers to wait for write locks to be released?
-	 * 
-	 * @return true
-	 */
+	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return true;
 	}
 
-	/**
-	 * For the underlying database, is REPEATABLE_READ isolation implemented by
-	 * forcing writers to wait for read locks to be released?
-	 * 
-	 * @return true
-	 */
+	@Override
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return true;
 	}
 
 	// limit/offset support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	/**
-	 * Does this dialect's LIMIT support (if any) additionally support
-	 * specifying an offset?
-	 * 
-	 * @return true
-	 */
+	@Override
 	public boolean supportsLimitOffset() {
 		return true;
 	}
 
-	/**
-	 * Does this dialect support bind variables (i.e., prepared statememnt
-	 * parameters) for its limit/offset?
-	 * 
-	 * @return false
-	 */
+	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
-	/**
-	 * Does the <tt>LIMIT</tt> clause take a "maximum" row number instead
-	 * of a total number of returned rows?
-	 */
+	@Override
 	public boolean useMaxForLimit() {
 		return false;
 	}
 
-	/**
-	 * Add a <tt>LIMIT</tt> clause to the given SQL <tt>SELECT</tt>
-	 * 
-	 * @return the modified SQL
-	 */
+	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
-		StringBuilder soff = new StringBuilder(" offset " + offset);
-		StringBuilder slim = new StringBuilder(" fetch first " + limit + " rows only");
-		StringBuilder sb = new StringBuilder(querySelect.length() +
-            soff.length() + slim.length()).append(querySelect);
-		if (offset > 0) {
-			sb.append(soff);
+		final StringBuilder soff = new StringBuilder( " offset " + offset );
+		final StringBuilder slim = new StringBuilder( " fetch first " + limit + " rows only" );
+		final StringBuilder sb = new StringBuilder( querySelect.length() + soff.length() + slim.length() )
+				.append( querySelect );
+		if ( offset > 0 ) {
+			sb.append( soff );
+		}
+		if ( limit > 0 ) {
+			sb.append( slim );
 		}
-        if (limit > 0) {
-            sb.append(slim);
-        }
 		return sb.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
index e5b9f8a20e..b22cf43218 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
@@ -1,359 +1,310 @@
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
 package org.hibernate.dialect;
+
 import java.sql.Types;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for Ingres 9.2.
  * <p/>
- * Known limitations:
- * <ul>
- * <li> Only supports simple constants or columns on the left side of an IN, making (1,2,3) in (...) or (&lt;subselect&gt;) in (...) non-supported.
- * <li> Supports only 39 digits in decimal.
- * <li> Explicitly set USE_GET_GENERATED_KEYS property to false.
- * <li> Perform string casts to varchar; removes space padding.
+ * Known limitations: <ul>
+ *     <li>
+ *         Only supports simple constants or columns on the left side of an IN,
+ *         making {@code (1,2,3) in (...)} or {@code (subselect) in (...)} non-supported.
+ *     </li>
+ *     <li>
+ *         Supports only 39 digits in decimal.
+ *     </li>
+ *     <li>
+ *         Explicitly set USE_GET_GENERATED_KEYS property to false.
+ *     </li>
+ *     <li>
+ *         Perform string casts to varchar; removes space padding.
+ *     </li>
  * </ul>
  * 
  * @author Ian Booth
  * @author Bruce Lunsford
  * @author Max Rydahl Andersen
  * @author Raymond Fan
  */
+@SuppressWarnings("deprecation")
 public class IngresDialect extends Dialect {
-
+	/**
+	 * Constructs a IngresDialect
+	 */
 	public IngresDialect() {
 		super();
 		registerColumnType( Types.BIT, "tinyint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.REAL, "real" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "float" );
 		registerColumnType( Types.NUMERIC, "decimal($p, $s)" );
 		registerColumnType( Types.DECIMAL, "decimal($p, $s)" );
 		registerColumnType( Types.BINARY, 32000, "byte($l)" );
 		registerColumnType( Types.BINARY, "long byte" );
 		registerColumnType( Types.VARBINARY, 32000, "varbyte($l)" );
 		registerColumnType( Types.VARBINARY, "long byte" );
 		registerColumnType( Types.LONGVARBINARY, "long byte" );
 		registerColumnType( Types.CHAR, 32000, "char($l)" );
 		registerColumnType( Types.VARCHAR, 32000, "varchar($l)" );
 		registerColumnType( Types.VARCHAR, "long varchar" );
 		registerColumnType( Types.LONGVARCHAR, "long varchar" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time with time zone" );
 		registerColumnType( Types.TIMESTAMP, "timestamp with time zone" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bit_add", new StandardSQLFunction( "bit_add" ) );
 		registerFunction( "bit_and", new StandardSQLFunction( "bit_and" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "octet_length(hex(?1))*4" ) );
 		registerFunction( "bit_not", new StandardSQLFunction( "bit_not" ) );
 		registerFunction( "bit_or", new StandardSQLFunction( "bit_or" ) );
 		registerFunction( "bit_xor", new StandardSQLFunction( "bit_xor" ) );
 		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.LONG ) );
 		registerFunction( "charextract", new StandardSQLFunction( "charextract", StandardBasicTypes.STRING ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "+", ")" ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "current_user", new NoArgSQLFunction( "current_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "date('now')", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "date('now')", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "date('now')", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "date_trunc", new StandardSQLFunction( "date_trunc", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dba", new NoArgSQLFunction( "dba", StandardBasicTypes.STRING, true ) );
 		registerFunction( "dow", new StandardSQLFunction( "dow", StandardBasicTypes.STRING ) );
 		registerFunction( "extract", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "date_part('?1', ?3)" ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "gmt_timestamp", new StandardSQLFunction( "gmt_timestamp", StandardBasicTypes.STRING ) );
 		registerFunction( "hash", new StandardSQLFunction( "hash", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hex", new StandardSQLFunction( "hex", StandardBasicTypes.STRING ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "initial_user", new NoArgSQLFunction( "initial_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "intextract", new StandardSQLFunction( "intextract", StandardBasicTypes.INTEGER ) );
 		registerFunction( "left", new StandardSQLFunction( "left", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.LONG, "locate(?1, ?2)" ) );
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.LONG ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "lowercase", new StandardSQLFunction( "lowercase" ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "octet_length", new StandardSQLFunction( "octet_length", StandardBasicTypes.LONG ) );
 		registerFunction( "pad", new StandardSQLFunction( "pad", StandardBasicTypes.STRING ) );
 		registerFunction( "position", new StandardSQLFunction( "position", StandardBasicTypes.LONG ) );
 		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "random", new NoArgSQLFunction( "random", StandardBasicTypes.LONG, true ) );
 		registerFunction( "randomf", new NoArgSQLFunction( "randomf", StandardBasicTypes.DOUBLE, true ) );
 		registerFunction( "right", new StandardSQLFunction( "right", StandardBasicTypes.STRING ) );
 		registerFunction( "session_user", new NoArgSQLFunction( "session_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "size", new NoArgSQLFunction( "size", StandardBasicTypes.LONG, true ) );
 		registerFunction( "squeeze", new StandardSQLFunction( "squeeze" ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1 FROM ?2 FOR ?3)" ) );
 		registerFunction( "system_user", new NoArgSQLFunction( "system_user", StandardBasicTypes.STRING, false ) );
 		//registerFunction( "trim", new StandardSQLFunction( "trim", StandardBasicTypes.STRING ) );
 		registerFunction( "unhex", new StandardSQLFunction( "unhex", StandardBasicTypes.STRING ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "uppercase", new StandardSQLFunction( "uppercase" ) );
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "usercode", new NoArgSQLFunction( "usercode", StandardBasicTypes.STRING, true ) );
 		registerFunction( "username", new NoArgSQLFunction( "username", StandardBasicTypes.STRING, true ) );
 		registerFunction( "uuid_create", new StandardSQLFunction( "uuid_create", StandardBasicTypes.BYTE ) );
 		registerFunction( "uuid_compare", new StandardSQLFunction( "uuid_compare", StandardBasicTypes.INTEGER ) );
 		registerFunction( "uuid_from_char", new StandardSQLFunction( "uuid_from_char", StandardBasicTypes.BYTE ) );
 		registerFunction( "uuid_to_char", new StandardSQLFunction( "uuid_to_char", StandardBasicTypes.STRING ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 		// Casting to char of numeric values introduces space padding up to the
 		// maximum width of a value for that return type.  Casting to varchar
 		// does not introduce space padding.
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as varchar)") );
-        // Ingres driver supports getGeneratedKeys but only in the following
-        // form:
-        // The Ingres DBMS returns only a single table key or a single object
-        // key per insert statement. Ingres does not return table and object
-        // keys for INSERT AS SELECT statements. Depending on the keys that are
-        // produced by the statement executed, auto-generated key parameters in
-        // execute(), executeUpdate(), and prepareStatement() methods are
-        // ignored and getGeneratedKeys() returns a result-set containing no
-        // rows, a single row with one column, or a single row with two columns.
-        // Ingres JDBC Driver returns table and object keys as BINARY values.
-        getDefaultProperties().setProperty(Environment.USE_GET_GENERATED_KEYS, "false");
-        // There is no support for a native boolean type that accepts values
-        // of true, false or unknown. Using the tinyint type requires
-        // substitions of true and false.
-        getDefaultProperties().setProperty(Environment.QUERY_SUBSTITUTIONS, "true=1,false=0");
+		// Ingres driver supports getGeneratedKeys but only in the following
+		// form:
+		// The Ingres DBMS returns only a single table key or a single object
+		// key per insert statement. Ingres does not return table and object
+		// keys for INSERT AS SELECT statements. Depending on the keys that are
+		// produced by the statement executed, auto-generated key parameters in
+		// execute(), executeUpdate(), and prepareStatement() methods are
+		// ignored and getGeneratedKeys() returns a result-set containing no
+		// rows, a single row with one column, or a single row with two columns.
+		// Ingres JDBC Driver returns table and object keys as BINARY values.
+		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "false" );
+		// There is no support for a native boolean type that accepts values
+		// of true, false or unknown. Using the tinyint type requires
+		// substitions of true and false.
+		getDefaultProperties().setProperty( Environment.QUERY_SUBSTITUTIONS, "true=1,false=0" );
 	}
-	/**
-	 * Expression for created UUID string
-	 */
+
+	@Override
 	public String getSelectGUIDString() {
 		return "select uuid_to_char(uuid_create())";
 	}
-	/**
-	 * Do we need to drop constraints before dropping tables in this dialect?
-	 *
-	 * @return boolean
-	 */
+
+	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
-	/**
-	 * Does this dialect support <tt>FOR UPDATE OF</tt>, allowing
-	 * particular rows to be locked?
-	 *
-	 * @return True (Ingres does support "for update of" syntax...)
-	 */
-	public boolean supportsForUpdateOf() {
-		return true;
-	}
-
-	/**
-	 * The syntax used to add a column to a table (optional).
-	 */
+	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
-	/**
-	 * The keyword used to specify a nullable column.
-	 *
-	 * @return String
-	 */
+	@Override
 	public String getNullColumnString() {
 		return " with null";
 	}
 
-	/**
-	 * Does this dialect support sequences?
-	 *
-	 * @return boolean
-	 */
+	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
-	/**
-	 * The syntax that fetches the next value of a sequence, if sequences are supported.
-	 *
-	 * @param sequenceName the name of the sequence
-	 *
-	 * @return String
-	 */
+	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select nextval for " + sequenceName;
 	}
 
+	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
-	/**
-	 * The syntax used to create a sequence, if sequences are supported.
-	 *
-	 * @param sequenceName the name of the sequence
-	 *
-	 * @return String
-	 */
+	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
-	/**
-	 * The syntax used to drop a sequence, if sequences are supported.
-	 *
-	 * @param sequenceName the name of the sequence
-	 *
-	 * @return String
-	 */
+	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
 
-	/**
-	 * A query used to find all sequences
-	 */
+	@Override
 	public String getQuerySequencesString() {
 		return "select seq_name from iisequence";
 	}
 
-	/**
-	 * The name of the SQL function that transforms a string to
-	 * lowercase
-	 *
-	 * @return String
-	 */
+	@Override
 	public String getLowercaseFunction() {
 		return "lowercase";
 	}
 
-	/**
-	 * Does this <tt>Dialect</tt> have some kind of <tt>LIMIT</tt> syntax?
-	 */
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
-	/**
-	 * Does this dialect support an offset?
-	 */
+	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
-	/**
-	 * Add a <tt>LIMIT</tt> clause to the given SQL <tt>SELECT</tt>
-	 *
-	 * @return the modified SQL
-	 */
+	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 16 )
 				.append( querySelect )
 				.insert( 6, " first " + limit )
 				.toString();
 	}
 
+	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
-	/**
-	 * Does the <tt>LIMIT</tt> clause take a "maximum" row number instead
-	 * of a total number of returned rows?
-	 */
+	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
-	/**
-	 * Does this dialect support temporary tables?
-	 */
+	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
+	@Override
 	public String getCreateTemporaryTableString() {
 		return "declare global temporary table";
 	}
 
+	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit preserve rows with norecovery";
 	}
 
+	@Override
 	public String generateTemporaryTableName(String baseTableName) {
 		return "session." + super.generateTemporaryTableName( baseTableName );
 	}
 
-
-	/**
-	 * Expression for current_timestamp
-	 */
+	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		return "date(now)";
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public boolean supportsSubselectAsInPredicateLHS() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsExpectedLobUsagePattern () {
 		return false;
 	}
 
-	/**
-	 * Ingres does not support the syntax `count(distinct a,b)`?
-	 *
-	 * @return False, not supported.
-	 */
+	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/InterbaseDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/InterbaseDialect.java
index 7e0a0b89d1..b6dd691323 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/InterbaseDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/InterbaseDialect.java
@@ -1,126 +1,146 @@
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
 package org.hibernate.dialect;
 import java.sql.Types;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for Interbase.
  *
  * @author Gavin King
  */
+@SuppressWarnings("deprecation")
 public class InterbaseDialect extends Dialect {
 
+	/**
+	 * Constructs a InterbaseDialect
+	 */
 	public InterbaseDialect() {
 		super();
 		registerColumnType( Types.BIT, "smallint" );
 		registerColumnType( Types.BIGINT, "numeric(18,0)" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "blob" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "blob sub_type 1" );
 		
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(","||",")" ) );
-		registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
+		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 
-		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
+		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, NO_BATCH );
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
+	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from RDB$DATABASE";
 	}
 
+	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "gen_id( " + sequenceName + ", 1 )";
 	}
 
+	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create generator " + sequenceName;
 	}
 
+	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "delete from RDB$GENERATORS where RDB$GENERATOR_NAME = '" + sequenceName.toUpperCase() + "'";
 	}
 
+	@Override
 	public String getQuerySequencesString() {
 		return "select RDB$GENERATOR_NAME from RDB$GENERATORS";
 	}
-	
+
+	@Override
 	public String getForUpdateString() {
 		return " with lock";
 	}
+
+	@Override
 	public String getForUpdateString(String aliases) {
 		return " for update of " + aliases + " with lock";
 	}
 
+	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
+	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		return hasOffset ? sql + " rows ? to ?" : sql + " rows ?";
 	}
 
+	@Override
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
+	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
-	public String getCurrentTimestampCallString() {
+	@Override
+	public String getCurrentTimestampSelectString() {
 		// TODO : not sure which (either?) is correct, could not find docs on how to do this.
 		// did find various blogs and forums mentioning that select CURRENT_TIMESTAMP
 		// does not work...
 		return "{?= call CURRENT_TIMESTAMP }";
 //		return "select CURRENT_TIMESTAMP from RDB$DATABASE";
 	}
 
+	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return true;
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/JDataStoreDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/JDataStoreDialect.java
index a2b83305f8..37566cc2ad 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/JDataStoreDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/JDataStoreDialect.java
@@ -1,99 +1,109 @@
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
 package org.hibernate.dialect;
+
 import java.sql.Types;
 
 import org.hibernate.cfg.Environment;
 
 /**
- * A <tt>Dialect</tt> for JDataStore.
+ * A Dialect for JDataStore.
  * 
  * @author Vishy Kasar
  */
 public class JDataStoreDialect extends Dialect {
-
 	/**
 	 * Creates new JDataStoreDialect
 	 */
 	public JDataStoreDialect() {
 		super();
 
 		registerColumnType( Types.BIT, "tinyint" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "varbinary($l)" );
 		registerColumnType( Types.NUMERIC, "numeric($p, $s)" );
 
 		registerColumnType( Types.BLOB, "varbinary" );
 		registerColumnType( Types.CLOB, "varchar" );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
+	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
+	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade";
 	}
 
+	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
+	@Override
 	public String getIdentitySelectString() {
-		return null; // NOT_SUPPORTED_SHOULD_USE_JDBC3_PreparedStatement.getGeneratedKeys_method
+		// NOT_SUPPORTED_SHOULD_USE_JDBC3_PreparedStatement.getGeneratedKeys_method
+		return null;
 	}
 
+	@Override
 	public String getIdentityColumnString() {
 		return "autoincrement";
 	}
 
+	@Override
 	public String getNoColumnsInsertString() {
 		return "default values";
 	}
 
+	@Override
 	public boolean supportsColumnCheck() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsTableCheck() {
 		return false;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/MckoiDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MckoiDialect.java
index 151ed7917a..1143a56245 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/MckoiDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/MckoiDialect.java
@@ -1,140 +1,153 @@
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
 package org.hibernate.dialect;
+
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.MckoiCaseFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect compatible with McKoi SQL
  *
  * @author Doug Currie
  * @author Gabe Hicks
  */
 public class MckoiDialect extends Dialect {
+	/**
+	 * Constructs a MckoiDialect
+	 */
 	public MckoiDialect() {
 		super();
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "varbinary" );
 		registerColumnType( Types.NUMERIC, "numeric" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "abs", new StandardSQLFunction("abs") );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 		registerFunction( "round", new StandardSQLFunction( "round", StandardBasicTypes.INTEGER ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER ) );
 		registerFunction( "least", new StandardSQLFunction("least") );
 		registerFunction( "greatest", new StandardSQLFunction("greatest") );
 		registerFunction( "user", new StandardSQLFunction( "user", StandardBasicTypes.STRING ) );
 		registerFunction( "concat", new StandardSQLFunction( "concat", StandardBasicTypes.STRING ) );
 
-		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
+		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, NO_BATCH );
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
+	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName );
 	}
 
+	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "nextval('" + sequenceName + "')";
 	}
 
+	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
+	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
+	@Override
 	public String getForUpdateString() {
 		return "";
 	}
 
+	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
+	@Override
 	public CaseFragment createCaseFragment() {
 		return new MckoiCaseFragment();
 	}
 
+	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// Mckoi has no known variation of a "SELECT ... FOR UPDATE" syntax...
 		if ( lockMode==LockMode.PESSIMISTIC_FORCE_INCREMENT) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_WRITE) {
 			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_READ) {
 			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC) {
 			return new OptimisticLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC_FORCE_INCREMENT) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/MySQL5Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MySQL5Dialect.java
index c5001f1557..6065a7dada 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/MySQL5Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/MySQL5Dialect.java
@@ -1,49 +1,46 @@
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
 package org.hibernate.dialect;
+
 import java.sql.Types;
 
 /**
  * An SQL dialect for MySQL 5.x specific features.
  *
  * @author Steve Ebersole
  */
 public class MySQL5Dialect extends MySQLDialect {
+	@Override
 	protected void registerVarcharTypes() {
 		registerColumnType( Types.VARCHAR, "longtext" );
 //		registerColumnType( Types.VARCHAR, 16777215, "mediumtext" );
 		registerColumnType( Types.VARCHAR, 65535, "varchar($l)" );
 		registerColumnType( Types.LONGVARCHAR, "longtext" );
 	}
-	
-	/**
-	 * Does this dialect support column-level check constraints?
-	 *
-	 * @return True if column-level CHECK constraints are supported; false
-	 * otherwise.
-	 */
+
+	@Override
 	public boolean supportsColumnCheck() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/MySQL5InnoDBDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MySQL5InnoDBDialect.java
index 81cbe4146b..474aa9bef1 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/MySQL5InnoDBDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/MySQL5InnoDBDialect.java
@@ -1,44 +1,46 @@
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
 package org.hibernate.dialect;
 
-
-/**
- * @author Gavin King, Scott Marlow
+/** A Dialect for MySQL 5 using InnoDB engine
+ *
+ * @author Gavin King,
+ * @author Scott Marlow
  */
 public class MySQL5InnoDBDialect extends MySQL5Dialect {
-
+	@Override
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
-	
+
+	@Override
 	public String getTableTypeString() {
 		return " ENGINE=InnoDB";
 	}
 
+	@Override
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return true;
 	}
-	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
index 5d9d881577..801c7d1b9b 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
@@ -1,423 +1,457 @@
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
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.JDBCException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for MySQL (prior to 5.x).
  *
  * @author Gavin King
  */
+@SuppressWarnings("deprecation")
 public class MySQLDialect extends Dialect {
 
+	/**
+	 * Constructs a MySQLDialect
+	 */
 	public MySQLDialect() {
 		super();
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "datetime" );
 		registerColumnType( Types.VARBINARY, "longblob" );
 		registerColumnType( Types.VARBINARY, 16777215, "mediumblob" );
 		registerColumnType( Types.VARBINARY, 65535, "blob" );
 		registerColumnType( Types.VARBINARY, 255, "tinyblob" );
-        registerColumnType( Types.BINARY, "binary($l)" );
+		registerColumnType( Types.BINARY, "binary($l)" );
 		registerColumnType( Types.LONGVARBINARY, "longblob" );
 		registerColumnType( Types.LONGVARBINARY, 16777215, "mediumblob" );
 		registerColumnType( Types.NUMERIC, "decimal($p,$s)" );
 		registerColumnType( Types.BLOB, "longblob" );
 //		registerColumnType( Types.BLOB, 16777215, "mediumblob" );
 //		registerColumnType( Types.BLOB, 65535, "blob" );
 		registerColumnType( Types.CLOB, "longtext" );
 //		registerColumnType( Types.CLOB, 16777215, "mediumtext" );
 //		registerColumnType( Types.CLOB, 65535, "text" );
 		registerVarcharTypes();
 
-		registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
-		registerFunction("bin", new StandardSQLFunction("bin", StandardBasicTypes.STRING) );
-		registerFunction("char_length", new StandardSQLFunction("char_length", StandardBasicTypes.LONG) );
-		registerFunction("character_length", new StandardSQLFunction("character_length", StandardBasicTypes.LONG) );
-		registerFunction("lcase", new StandardSQLFunction("lcase") );
-		registerFunction("lower", new StandardSQLFunction("lower") );
-		registerFunction("ltrim", new StandardSQLFunction("ltrim") );
-		registerFunction("ord", new StandardSQLFunction("ord", StandardBasicTypes.INTEGER) );
-		registerFunction("quote", new StandardSQLFunction("quote") );
-		registerFunction("reverse", new StandardSQLFunction("reverse") );
-		registerFunction("rtrim", new StandardSQLFunction("rtrim") );
-		registerFunction("soundex", new StandardSQLFunction("soundex") );
-		registerFunction("space", new StandardSQLFunction("space", StandardBasicTypes.STRING) );
-		registerFunction("ucase", new StandardSQLFunction("ucase") );
-		registerFunction("upper", new StandardSQLFunction("upper") );
-		registerFunction("unhex", new StandardSQLFunction("unhex", StandardBasicTypes.STRING) );
-
-		registerFunction("abs", new StandardSQLFunction("abs") );
-		registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
-
-		registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
-		registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
-		registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
-		registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
-		registerFunction("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE) );
-		registerFunction("crc32", new StandardSQLFunction("crc32", StandardBasicTypes.LONG) );
-		registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
-		registerFunction("ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
-		registerFunction("log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE) );
-		registerFunction("log2", new StandardSQLFunction("log2", StandardBasicTypes.DOUBLE) );
-		registerFunction("log10", new StandardSQLFunction("log10", StandardBasicTypes.DOUBLE) );
-		registerFunction("pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE) );
-		registerFunction("rand", new NoArgSQLFunction("rand", StandardBasicTypes.DOUBLE) );
-		registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
-		registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
-		registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
-
-		registerFunction("radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE) );
-		registerFunction("degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE) );
-
-		registerFunction("ceiling", new StandardSQLFunction("ceiling", StandardBasicTypes.INTEGER) );
-		registerFunction("ceil", new StandardSQLFunction("ceil", StandardBasicTypes.INTEGER) );
-		registerFunction("floor", new StandardSQLFunction("floor", StandardBasicTypes.INTEGER) );
-		registerFunction("round", new StandardSQLFunction("round") );
-
-		registerFunction("datediff", new StandardSQLFunction("datediff", StandardBasicTypes.INTEGER) );
-		registerFunction("timediff", new StandardSQLFunction("timediff", StandardBasicTypes.TIME) );
-		registerFunction("date_format", new StandardSQLFunction("date_format", StandardBasicTypes.STRING) );
-
-		registerFunction("curdate", new NoArgSQLFunction("curdate", StandardBasicTypes.DATE) );
-		registerFunction("curtime", new NoArgSQLFunction("curtime", StandardBasicTypes.TIME) );
-		registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
-		registerFunction("current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false) );
-		registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
-		registerFunction("date", new StandardSQLFunction("date", StandardBasicTypes.DATE) );
-		registerFunction("day", new StandardSQLFunction("day", StandardBasicTypes.INTEGER) );
-		registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", StandardBasicTypes.INTEGER) );
-		registerFunction("dayname", new StandardSQLFunction("dayname", StandardBasicTypes.STRING) );
-		registerFunction("dayofweek", new StandardSQLFunction("dayofweek", StandardBasicTypes.INTEGER) );
-		registerFunction("dayofyear", new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER) );
-		registerFunction("from_days", new StandardSQLFunction("from_days", StandardBasicTypes.DATE) );
-		registerFunction("from_unixtime", new StandardSQLFunction("from_unixtime", StandardBasicTypes.TIMESTAMP) );
-		registerFunction("hour", new StandardSQLFunction("hour", StandardBasicTypes.INTEGER) );
-		registerFunction("last_day", new StandardSQLFunction("last_day", StandardBasicTypes.DATE) );
-		registerFunction("localtime", new NoArgSQLFunction("localtime", StandardBasicTypes.TIMESTAMP) );
-		registerFunction("localtimestamp", new NoArgSQLFunction("localtimestamp", StandardBasicTypes.TIMESTAMP) );
-		registerFunction("microseconds", new StandardSQLFunction("microseconds", StandardBasicTypes.INTEGER) );
-		registerFunction("minute", new StandardSQLFunction("minute", StandardBasicTypes.INTEGER) );
-		registerFunction("month", new StandardSQLFunction("month", StandardBasicTypes.INTEGER) );
-		registerFunction("monthname", new StandardSQLFunction("monthname", StandardBasicTypes.STRING) );
-		registerFunction("now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP) );
-		registerFunction("quarter", new StandardSQLFunction("quarter", StandardBasicTypes.INTEGER) );
-		registerFunction("second", new StandardSQLFunction("second", StandardBasicTypes.INTEGER) );
-		registerFunction("sec_to_time", new StandardSQLFunction("sec_to_time", StandardBasicTypes.TIME) );
-		registerFunction("sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.TIMESTAMP) );
-		registerFunction("time", new StandardSQLFunction("time", StandardBasicTypes.TIME) );
-		registerFunction("timestamp", new StandardSQLFunction("timestamp", StandardBasicTypes.TIMESTAMP) );
-		registerFunction("time_to_sec", new StandardSQLFunction("time_to_sec", StandardBasicTypes.INTEGER) );
-		registerFunction("to_days", new StandardSQLFunction("to_days", StandardBasicTypes.LONG) );
-		registerFunction("unix_timestamp", new StandardSQLFunction("unix_timestamp", StandardBasicTypes.LONG) );
-		registerFunction("utc_date", new NoArgSQLFunction("utc_date", StandardBasicTypes.STRING) );
-		registerFunction("utc_time", new NoArgSQLFunction("utc_time", StandardBasicTypes.STRING) );
-		registerFunction("utc_timestamp", new NoArgSQLFunction("utc_timestamp", StandardBasicTypes.STRING) );
-		registerFunction("week", new StandardSQLFunction("week", StandardBasicTypes.INTEGER) );
-		registerFunction("weekday", new StandardSQLFunction("weekday", StandardBasicTypes.INTEGER) );
-		registerFunction("weekofyear", new StandardSQLFunction("weekofyear", StandardBasicTypes.INTEGER) );
-		registerFunction("year", new StandardSQLFunction("year", StandardBasicTypes.INTEGER) );
-		registerFunction("yearweek", new StandardSQLFunction("yearweek", StandardBasicTypes.INTEGER) );
-
-		registerFunction("hex", new StandardSQLFunction("hex", StandardBasicTypes.STRING) );
-		registerFunction("oct", new StandardSQLFunction("oct", StandardBasicTypes.STRING) );
-
-		registerFunction("octet_length", new StandardSQLFunction("octet_length", StandardBasicTypes.LONG) );
-		registerFunction("bit_length", new StandardSQLFunction("bit_length", StandardBasicTypes.LONG) );
-
-		registerFunction("bit_count", new StandardSQLFunction("bit_count", StandardBasicTypes.LONG) );
-		registerFunction("encrypt", new StandardSQLFunction("encrypt", StandardBasicTypes.STRING) );
-		registerFunction("md5", new StandardSQLFunction("md5", StandardBasicTypes.STRING) );
-		registerFunction("sha1", new StandardSQLFunction("sha1", StandardBasicTypes.STRING) );
-		registerFunction("sha", new StandardSQLFunction("sha", StandardBasicTypes.STRING) );
+		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
+		registerFunction( "bin", new StandardSQLFunction( "bin", StandardBasicTypes.STRING ) );
+		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.LONG ) );
+		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.LONG ) );
+		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
+		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
+		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
+		registerFunction( "ord", new StandardSQLFunction( "ord", StandardBasicTypes.INTEGER ) );
+		registerFunction( "quote", new StandardSQLFunction( "quote" ) );
+		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
+		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
+		registerFunction( "soundex", new StandardSQLFunction( "soundex" ) );
+		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
+		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
+		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
+		registerFunction( "unhex", new StandardSQLFunction( "unhex", StandardBasicTypes.STRING ) );
+
+		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
+		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
+
+		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "crc32", new StandardSQLFunction( "crc32", StandardBasicTypes.LONG ) );
+		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "log2", new StandardSQLFunction( "log2", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
+
+		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
+
+		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", StandardBasicTypes.INTEGER ) );
+		registerFunction( "ceil", new StandardSQLFunction( "ceil", StandardBasicTypes.INTEGER ) );
+		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.INTEGER ) );
+		registerFunction( "round", new StandardSQLFunction( "round" ) );
+
+		registerFunction( "datediff", new StandardSQLFunction( "datediff", StandardBasicTypes.INTEGER ) );
+		registerFunction( "timediff", new StandardSQLFunction( "timediff", StandardBasicTypes.TIME ) );
+		registerFunction( "date_format", new StandardSQLFunction( "date_format", StandardBasicTypes.STRING ) );
+
+		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
+		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
+		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
+		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
+		registerFunction( "current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP, false ) );
+		registerFunction( "date", new StandardSQLFunction( "date", StandardBasicTypes.DATE ) );
+		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
+		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
+		registerFunction( "from_days", new StandardSQLFunction( "from_days", StandardBasicTypes.DATE ) );
+		registerFunction( "from_unixtime", new StandardSQLFunction( "from_unixtime", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
+		registerFunction( "last_day", new StandardSQLFunction( "last_day", StandardBasicTypes.DATE ) );
+		registerFunction( "localtime", new NoArgSQLFunction( "localtime", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "localtimestamp", new NoArgSQLFunction( "localtimestamp", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "microseconds", new StandardSQLFunction( "microseconds", StandardBasicTypes.INTEGER ) );
+		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
+		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
+		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
+		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
+		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
+		registerFunction( "sec_to_time", new StandardSQLFunction( "sec_to_time", StandardBasicTypes.TIME ) );
+		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
+		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "time_to_sec", new StandardSQLFunction( "time_to_sec", StandardBasicTypes.INTEGER ) );
+		registerFunction( "to_days", new StandardSQLFunction( "to_days", StandardBasicTypes.LONG ) );
+		registerFunction( "unix_timestamp", new StandardSQLFunction( "unix_timestamp", StandardBasicTypes.LONG ) );
+		registerFunction( "utc_date", new NoArgSQLFunction( "utc_date", StandardBasicTypes.STRING ) );
+		registerFunction( "utc_time", new NoArgSQLFunction( "utc_time", StandardBasicTypes.STRING ) );
+		registerFunction( "utc_timestamp", new NoArgSQLFunction( "utc_timestamp", StandardBasicTypes.STRING ) );
+		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
+		registerFunction( "weekday", new StandardSQLFunction( "weekday", StandardBasicTypes.INTEGER ) );
+		registerFunction( "weekofyear", new StandardSQLFunction( "weekofyear", StandardBasicTypes.INTEGER ) );
+		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
+		registerFunction( "yearweek", new StandardSQLFunction( "yearweek", StandardBasicTypes.INTEGER ) );
+
+		registerFunction( "hex", new StandardSQLFunction( "hex", StandardBasicTypes.STRING ) );
+		registerFunction( "oct", new StandardSQLFunction( "oct", StandardBasicTypes.STRING ) );
+
+		registerFunction( "octet_length", new StandardSQLFunction( "octet_length", StandardBasicTypes.LONG ) );
+		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.LONG ) );
+
+		registerFunction( "bit_count", new StandardSQLFunction( "bit_count", StandardBasicTypes.LONG ) );
+		registerFunction( "encrypt", new StandardSQLFunction( "encrypt", StandardBasicTypes.STRING ) );
+		registerFunction( "md5", new StandardSQLFunction( "md5", StandardBasicTypes.STRING ) );
+		registerFunction( "sha1", new StandardSQLFunction( "sha1", StandardBasicTypes.STRING ) );
+		registerFunction( "sha", new StandardSQLFunction( "sha", StandardBasicTypes.STRING ) );
 
 		registerFunction( "concat", new StandardSQLFunction( "concat", StandardBasicTypes.STRING ) );
 
-		getDefaultProperties().setProperty(Environment.MAX_FETCH_DEPTH, "2");
-		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
+		getDefaultProperties().setProperty( Environment.MAX_FETCH_DEPTH, "2" );
+		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 	}
 
 	protected void registerVarcharTypes() {
 		registerColumnType( Types.VARCHAR, "longtext" );
 //		registerColumnType( Types.VARCHAR, 16777215, "mediumtext" );
 //		registerColumnType( Types.VARCHAR, 65535, "text" );
 		registerColumnType( Types.VARCHAR, 255, "varchar($l)" );
 		registerColumnType( Types.LONGVARCHAR, "longtext" );
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
-	
+
+	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
-	
+
+	@Override
 	public String getIdentitySelectString() {
 		return "select last_insert_id()";
 	}
 
+	@Override
 	public String getIdentityColumnString() {
-		return "not null auto_increment"; //starts with 1, implicitly
+		//starts with 1, implicitly
+		return "not null auto_increment";
 	}
 
+	@Override
 	public String getAddForeignKeyConstraintString(
-			String constraintName, 
-			String[] foreignKey, 
-			String referencedTable, 
-			String[] primaryKey, boolean referencesPrimaryKey
-	) {
-		String cols = StringHelper.join(", ", foreignKey);
-		return new StringBuilder(30)
-			.append(" add index ")
-			.append(constraintName)
-			.append(" (")
-			.append(cols)
-			.append("), add constraint ")
-			.append(constraintName)
-			.append(" foreign key (")
-			.append(cols)
-			.append(") references ")
-			.append(referencedTable)
-			.append(" (")
-			.append( StringHelper.join(", ", primaryKey) )
-			.append(')')
-			.toString();
+			String constraintName,
+			String[] foreignKey,
+			String referencedTable,
+			String[] primaryKey,
+			boolean referencesPrimaryKey) {
+		final String cols = StringHelper.join( ", ", foreignKey );
+		final String referencedCols = StringHelper.join( ", ", primaryKey );
+		return String.format(
+				" add index %s (%s), add constraint %s foreign key (%s) references %s (%s)",
+				constraintName,
+				cols,
+				constraintName,
+				cols,
+				referencedTable,
+				referencedCols
+		);
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
-	
+
+	@Override
 	public String getDropForeignKeyString() {
 		return " drop foreign key ";
 	}
 
+	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		return sql + (hasOffset ? " limit ?, ?" : " limit ?");
 	}
 
+	@Override
 	public char closeQuote() {
 		return '`';
 	}
 
+	@Override
 	public char openQuote() {
 		return '`';
 	}
 
+	@Override
 	public boolean supportsIfExistsBeforeTableName() {
 		return true;
 	}
 
+	@Override
 	public String getSelectGUIDString() {
 		return "select uuid()";
 	}
 
+	@Override
 	public boolean supportsCascadeDelete() {
 		return false;
 	}
-	
+
+	@Override
 	public String getTableComment(String comment) {
 		return " comment='" + comment + "'";
 	}
 
+	@Override
 	public String getColumnComment(String comment) {
 		return " comment '" + comment + "'";
 	}
 
+	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
+	@Override
 	public String getCreateTemporaryTableString() {
 		return "create temporary table if not exists";
 	}
 
+	@Override
 	public String getDropTemporaryTableString() {
 		return "drop temporary table";
 	}
 
+	@Override
 	public Boolean performTemporaryTableDDLInIsolation() {
 		// because we [drop *temporary* table...] we do not
 		// have to doAfterTransactionCompletion these in isolation.
 		return Boolean.FALSE;
 	}
 
+	@Override
 	public String getCastTypeName(int code) {
-		switch ( code ){
+		switch ( code ) {
 			case Types.INTEGER:
 				return "signed";
 			case Types.VARCHAR:
 				return "char";
 			case Types.VARBINARY:
 				return "binary";
 			default:
 				return super.getCastTypeName( code );
 		}
 	}
 
+	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
+	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
+	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select now()";
 	}
 
+	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		return col;
-	} 
-	
+	}
+
+	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
-		boolean isResultSet = ps.execute(); 
-		while (!isResultSet && ps.getUpdateCount() != -1) { 
-			isResultSet = ps.getMoreResults(); 
-		} 
+		boolean isResultSet = ps.execute();
+		while ( !isResultSet && ps.getUpdateCount() != -1 ) {
+			isResultSet = ps.getMoreResults();
+		}
 		return ps.getResultSet();
 	}
 
+	@Override
 	public boolean supportsRowValueConstructorSyntax() {
 		return true;
 	}
 
 	@Override
 	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
 		final StringBuilder orderByElement = new StringBuilder();
 		if ( nulls != NullPrecedence.NONE ) {
 			// Workaround for NULLS FIRST / LAST support.
 			orderByElement.append( "case when " ).append( expression ).append( " is null then " );
 			if ( nulls == NullPrecedence.FIRST ) {
 				orderByElement.append( "0 else 1" );
 			}
 			else {
 				orderByElement.append( "1 else 0" );
 			}
 			orderByElement.append( " end, " );
 		}
 		// Nulls precedence has already been handled so passing NONE value.
 		orderByElement.append( super.renderOrderByElement( expression, collation, order, NullPrecedence.NONE ) );
 		return orderByElement.toString();
 	}
 
 	// locking support
 
+	@Override
 	public String getForUpdateString() {
 		return " for update";
 	}
 
+	@Override
 	public String getWriteLockString(int timeout) {
 		return " for update";
 	}
 
+	@Override
 	public String getReadLockString(int timeout) {
 		return " lock in share mode";
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
+	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsLobValueChangePropogation() {
 		// note: at least my local MySQL 5.1 install shows this not working...
 		return false;
 	}
 
+	@Override
 	public boolean supportsSubqueryOnMutatingTable() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsLockTimeouts() {
 		// yes, we do handle "lock timeout" conditions in the exception conversion delegate,
 		// but that's a hardcoded lock timeout period across the whole entire MySQL database.
 		// MySQL does not support specifying lock timeouts as part of the SQL statement, which is really
 		// what this meta method is asking.
 		return false;
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				final String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 
 				if ( "41000".equals( sqlState ) ) {
 					return new LockTimeoutException( message, sqlException, sql );
 				}
 
 				if ( "40001".equals( sqlState ) ) {
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 				return null;
 			}
 		};
 	}
-	
+
 	@Override
-	public String getNotExpression( String expression ) {
+	public String getNotExpression(String expression) {
 		return "not (" + expression + ")";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/MySQLInnoDBDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLInnoDBDialect.java
index 37a13d271b..7b7ffb58c0 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/MySQLInnoDBDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLInnoDBDialect.java
@@ -1,44 +1,46 @@
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
 package org.hibernate.dialect;
 
-
 /**
+ * A Dialect for MySQL using InnoDB engine
+ *
  * @author Gavin King
  */
 public class MySQLInnoDBDialect extends MySQLDialect {
-
+	@Override
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
-	
+
+	@Override
 	public String getTableTypeString() {
 		return " type=InnoDB";
 	}
 
+	@Override
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return true;
 	}
-	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/MySQLMyISAMDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLMyISAMDialect.java
index d2f53be4e9..3a3840b7d3 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/MySQLMyISAMDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLMyISAMDialect.java
@@ -1,40 +1,41 @@
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
 package org.hibernate.dialect;
 
-
 /**
+ * A Dialect for MySQL using the MyISAM engine
+ *
  * @author Gavin King
  */
 public class MySQLMyISAMDialect extends MySQLDialect {
-
+	@Override
 	public String getTableTypeString() {
 		return " type=MyISAM";
 	}
 
+	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java
index 8ec1d401a9..978a5be158 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java
@@ -1,66 +1,70 @@
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
 package org.hibernate.dialect;
+
 import org.hibernate.LockOptions;
 import org.hibernate.sql.ANSIJoinFragment;
 import org.hibernate.sql.JoinFragment;
 
-
 /**
  * A dialect specifically for use with Oracle 10g.
  * <p/>
  * The main difference between this dialect and {@link Oracle9iDialect}
- * is the use of "ANSI join syntax".  This dialect also retires the use
- * of the <tt>oracle.jdbc.driver</tt> package in favor of 
- * <tt>oracle.jdbc</tt>.
+ * is the use of "ANSI join syntax".
  *
  * @author Steve Ebersole
  */
 public class Oracle10gDialect extends Oracle9iDialect {
-
+	/**
+	 * Constructs a Oracle10gDialect
+	 */
 	public Oracle10gDialect() {
 		super();
 	}
 
+	@Override
 	public JoinFragment createOuterJoinFragment() {
 		return new ANSIJoinFragment();
 	}
 
+	@Override
 	public String getWriteLockString(int timeout) {
 		if ( timeout == LockOptions.SKIP_LOCKED ) {
 			return  getForUpdateSkipLockedString();
 		}
 		else {
 			return super.getWriteLockString(timeout);
 		}
 	}
 
+	@Override
 	public String getForUpdateSkipLockedString() {
 		return " for update skip locked";
 	}
 
+	@Override
 	public String getForUpdateSkipLockedString(String aliases) {
 		return getForUpdateString() + " of " + aliases + " skip locked";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
index 5a0d243feb..9b83d3fdbc 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
@@ -1,598 +1,587 @@
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
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.QueryTimeoutException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.OracleJoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.BitTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * A dialect for Oracle 8i.
  *
  * @author Steve Ebersole
  */
+@SuppressWarnings("deprecation")
 public class Oracle8iDialect extends Dialect {
-	
 	private static final int PARAM_LIST_SIZE_LIMIT = 1000;
 
+	/**
+	 * Constructs a Oracle8iDialect
+	 */
 	public Oracle8iDialect() {
 		super();
 		registerCharacterTypeMappings();
 		registerNumericTypeMappings();
 		registerDateTimeTypeMappings();
 		registerLargeObjectTypeMappings();
 		registerReverseHibernateTypeMappings();
 		registerFunctions();
 		registerDefaultProperties();
 	}
 
 	protected void registerCharacterTypeMappings() {
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, 4000, "varchar2($l)" );
 		registerColumnType( Types.VARCHAR, "long" );
 	}
 
 	protected void registerNumericTypeMappings() {
 		registerColumnType( Types.BIT, "number(1,0)" );
 		registerColumnType( Types.BIGINT, "number(19,0)" );
 		registerColumnType( Types.SMALLINT, "number(5,0)" );
 		registerColumnType( Types.TINYINT, "number(3,0)" );
 		registerColumnType( Types.INTEGER, "number(10,0)" );
 
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.NUMERIC, "number($p,$s)" );
 		registerColumnType( Types.DECIMAL, "number($p,$s)" );
 
-        registerColumnType( Types.BOOLEAN, "number(1,0)" );
+		registerColumnType( Types.BOOLEAN, "number(1,0)" );
 	}
 
 	protected void registerDateTimeTypeMappings() {
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "date" );
 		registerColumnType( Types.TIMESTAMP, "date" );
 	}
 
 	protected void registerLargeObjectTypeMappings() {
 		registerColumnType( Types.BINARY, 2000, "raw($l)" );
 		registerColumnType( Types.BINARY, "long raw" );
 
 		registerColumnType( Types.VARBINARY, 2000, "raw($l)" );
 		registerColumnType( Types.VARBINARY, "long raw" );
 
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.LONGVARCHAR, "long" );
 		registerColumnType( Types.LONGVARBINARY, "long raw" );
 	}
 
 	protected void registerReverseHibernateTypeMappings() {
 	}
 
 	protected void registerFunctions() {
 		registerFunction( "abs", new StandardSQLFunction("abs") );
 		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "bitand", new StandardSQLFunction("bitand") );
 		registerFunction( "cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
 		registerFunction( "ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "round", new StandardSQLFunction("round") );
 		registerFunction( "trunc", new StandardSQLFunction("trunc") );
 		registerFunction( "ceil", new StandardSQLFunction("ceil") );
 		registerFunction( "floor", new StandardSQLFunction("floor") );
 
 		registerFunction( "chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER) );
 		registerFunction( "initcap", new StandardSQLFunction("initcap") );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "ltrim", new StandardSQLFunction("ltrim") );
 		registerFunction( "rtrim", new StandardSQLFunction("rtrim") );
 		registerFunction( "soundex", new StandardSQLFunction("soundex") );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
 		registerFunction( "to_date", new StandardSQLFunction("to_date", StandardBasicTypes.TIMESTAMP) );
 
 		registerFunction( "current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
 		registerFunction( "current_time", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIME, false) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
 
 		registerFunction( "last_day", new StandardSQLFunction("last_day", StandardBasicTypes.DATE) );
 		registerFunction( "sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.DATE, false) );
 		registerFunction( "systimestamp", new NoArgSQLFunction("systimestamp", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "uid", new NoArgSQLFunction("uid", StandardBasicTypes.INTEGER, false) );
 		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false) );
 
 		registerFunction( "rowid", new NoArgSQLFunction("rowid", StandardBasicTypes.LONG, false) );
 		registerFunction( "rownum", new NoArgSQLFunction("rownum", StandardBasicTypes.LONG, false) );
 
 		// Multi-param string dialect functions...
 		registerFunction( "concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", "") );
 		registerFunction( "instr", new StandardSQLFunction("instr", StandardBasicTypes.INTEGER) );
 		registerFunction( "instrb", new StandardSQLFunction("instrb", StandardBasicTypes.INTEGER) );
 		registerFunction( "lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING) );
 		registerFunction( "replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING) );
 		registerFunction( "rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING) );
 		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
 		registerFunction( "substrb", new StandardSQLFunction("substrb", StandardBasicTypes.STRING) );
 		registerFunction( "translate", new StandardSQLFunction("translate", StandardBasicTypes.STRING) );
 
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "instr(?2,?1)" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "vsize(?1)*8" ) );
 		registerFunction( "coalesce", new NvlFunction() );
 
 		// Multi-param numeric dialect functions...
 		registerFunction( "atan2", new StandardSQLFunction("atan2", StandardBasicTypes.FLOAT) );
 		registerFunction( "log", new StandardSQLFunction("log", StandardBasicTypes.INTEGER) );
 		registerFunction( "mod", new StandardSQLFunction("mod", StandardBasicTypes.INTEGER) );
 		registerFunction( "nvl", new StandardSQLFunction("nvl") );
 		registerFunction( "nvl2", new StandardSQLFunction("nvl2") );
 		registerFunction( "power", new StandardSQLFunction("power", StandardBasicTypes.FLOAT) );
 
 		// Multi-param date dialect functions...
 		registerFunction( "add_months", new StandardSQLFunction("add_months", StandardBasicTypes.DATE) );
 		registerFunction( "months_between", new StandardSQLFunction("months_between", StandardBasicTypes.FLOAT) );
 		registerFunction( "next_day", new StandardSQLFunction("next_day", StandardBasicTypes.DATE) );
 
 		registerFunction( "str", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
 	}
 
 	protected void registerDefaultProperties() {
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		// Oracle driver reports to support getGeneratedKeys(), but they only
 		// support the version taking an array of the names of the columns to
 		// be returned (via its RETURNING clause).  No other driver seems to
 		// support this overloaded version.
 		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "false" );
 	}
 
 	@Override
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		return sqlCode == Types.BOOLEAN ? BitTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride( sqlCode );
 	}
 
 
 	// features which change between 8i, 9i, and 10g ~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	/**
-	 * Support for the oracle proprietary join syntax...
-	 *
-	 * @return The orqacle join fragment
-	 */
 	@Override
 	public JoinFragment createOuterJoinFragment() {
 		return new OracleJoinFragment();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
 	/**
 	 * Map case support to the Oracle DECODE function.  Oracle did not
 	 * add support for CASE until 9i.
-	 *
-	 * @return The oracle CASE -> DECODE fragment
+	 * <p/>
+	 * {@inheritDoc}
 	 */
 	@Override
 	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
+
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		sql = sql.trim();
 		boolean isForUpdate = false;
-		if ( sql.toLowerCase().endsWith(" for update") ) {
+		if ( sql.toLowerCase().endsWith( " for update" ) ) {
 			sql = sql.substring( 0, sql.length()-11 );
 			isForUpdate = true;
 		}
 
-		StringBuilder pagingSelect = new StringBuilder( sql.length()+100 );
+		final StringBuilder pagingSelect = new StringBuilder( sql.length()+100 );
 		if (hasOffset) {
-			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
+			pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
 		}
 		else {
 			pagingSelect.append("select * from ( ");
 		}
-		pagingSelect.append(sql);
+		pagingSelect.append( sql );
 		if (hasOffset) {
-			pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?");
+			pagingSelect.append( " ) row_ ) where rownum_ <= ? and rownum_ > ?" );
 		}
 		else {
-			pagingSelect.append(" ) where rownum <= ?");
+			pagingSelect.append( " ) where rownum <= ?" );
 		}
 
 		if ( isForUpdate ) {
 			pagingSelect.append( " for update" );
 		}
 
 		return pagingSelect.toString();
 	}
 
 	/**
 	 * Allows access to the basic {@link Dialect#getSelectClauseNullString}
 	 * implementation...
 	 *
 	 * @param sqlType The {@link java.sql.Types} mapping type code
 	 * @return The appropriate select cluse fragment
 	 */
 	public String getBasicSelectClauseNullString(int sqlType) {
 		return super.getSelectClauseNullString( sqlType );
 	}
+
 	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		switch(sqlType) {
 			case Types.VARCHAR:
 			case Types.CHAR:
 				return "to_char(null)";
 			case Types.DATE:
 			case Types.TIMESTAMP:
 			case Types.TIME:
 				return "to_date(null)";
 			default:
 				return "to_number(null)";
 		}
 	}
+
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select sysdate from dual";
 	}
+
 	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		return "sysdate";
 	}
 
 
 	// features which remain constant across 8i, 9i, and 10g ~~~~~~~~~~~~~~~~~~
+
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
+
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dual";
 	}
+
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
+
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
-		return "create sequence " + sequenceName; //starts with 1, implicitly
+		//starts with 1, implicitly
+		return "create sequence " + sequenceName;
 	}
+
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
+
 	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade constraints";
 	}
+
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
+
 	@Override
 	public String getForUpdateNowaitString() {
 		return " for update nowait";
 	}
+
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
+
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
+
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
+
 	@Override
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
+
 	@Override
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString() + " of " + aliases + " nowait";
 	}
+
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
+
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
+
 	@Override
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
+
 	@Override
 	public String getQuerySequencesString() {
 		return    " select sequence_name from all_sequences"
 				+ "  union"
 				+ " select synonym_name"
 				+ "   from all_synonyms us, all_sequences asq"
 				+ "  where asq.sequence_name = us.table_name"
 				+ "    and asq.sequence_owner = us.table_owner";
 	}
+
 	@Override
 	public String getSelectGUIDString() {
 		return "select rawtohex(sys_guid()) from dual";
 	}
+
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
-        return EXTRACTER;
+		return EXTRACTER;
 	}
 
-	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
+	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		public String extractConstraintName(SQLException sqle) {
-			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
+			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 			if ( errorCode == 1 || errorCode == 2291 || errorCode == 2292 ) {
 				return extractUsingTemplate( "(", ")", sqle.getMessage() );
 			}
 			else if ( errorCode == 1400 ) {
 				// simple nullability constraint
 				return null;
 			}
 			else {
 				return null;
 			}
 		}
 
 	};
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				// interpreting Oracle exceptions is much much more precise based on their specific vendor codes.
 
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 
 
 				// lock timeouts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( errorCode == 30006 ) {
 					// ORA-30006: resource busy; acquire with WAIT timeout expired
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				else if ( errorCode == 54 ) {
 					// ORA-00054: resource busy and acquire with NOWAIT specified or timeout expired
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				else if ( 4021 == errorCode ) {
 					// ORA-04021 timeout occurred while waiting to lock object
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 
 
 				// deadlocks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 60 == errorCode ) {
 					// ORA-00060: deadlock detected while waiting for resource
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 				else if ( 4020 == errorCode ) {
 					// ORA-04020 deadlock detected while trying to lock object
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 
 				// query cancelled ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 1013 == errorCode ) {
 					// ORA-01013: user requested cancel of current operation
 					throw new QueryTimeoutException(  message, sqlException, sql );
 				}
 
 
 				// data integrity violation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 1407 == errorCode ) {
 					// ORA-01407: cannot update column to NULL
 					final String constraintName = getViolatedConstraintNameExtracter().extractConstraintName( sqlException );
 					return new ConstraintViolationException( message, sqlException, sql, constraintName );
 				}
 
 				return null;
 			}
 		};
 	}
 
-	public static final String ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.OracleTypes";
-	public static final String DEPRECATED_ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.driver.OracleTypes";
-
-	public static final int INIT_ORACLETYPES_CURSOR_VALUE = -99;
-
-	// not final-static to avoid possible classcast exceptions if using different oracle drivers.
-	private int oracleCursorTypeSqlType = INIT_ORACLETYPES_CURSOR_VALUE;
-
-	public int getOracleCursorTypeSqlType() {
-		if ( oracleCursorTypeSqlType == INIT_ORACLETYPES_CURSOR_VALUE ) {
-			// todo : is there really any reason to kkeep trying if this fails once?
-			oracleCursorTypeSqlType = extractOracleCursorTypeValue();
-		}
-		return oracleCursorTypeSqlType;
-	}
-
-	protected int extractOracleCursorTypeValue() {
-		Class oracleTypesClass;
-		try {
-			oracleTypesClass = ReflectHelper.classForName( ORACLE_TYPES_CLASS_NAME );
-		}
-		catch ( ClassNotFoundException cnfe ) {
-			try {
-				oracleTypesClass = ReflectHelper.classForName( DEPRECATED_ORACLE_TYPES_CLASS_NAME );
-			}
-			catch ( ClassNotFoundException e ) {
-				throw new HibernateException( "Unable to locate OracleTypes class", e );
-			}
-		}
-
-		try {
-			return oracleTypesClass.getField( "CURSOR" ).getInt( null );
-		}
-		catch ( Exception se ) {
-			throw new HibernateException( "Unable to access OracleTypes.CURSOR value", se );
-		}
-	}
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		//	register the type of the out param - an Oracle specific type
-		statement.registerOutParameter( col, getOracleCursorTypeSqlType() );
+		statement.registerOutParameter( col, OracleTypesHelper.INSTANCE.getOracleCursorTypeSqlType() );
 		col++;
 		return col;
 	}
+
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
-		return ( ResultSet ) ps.getObject( 1 );
+		return (ResultSet) ps.getObject( 1 );
 	}
+
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
+
 	@Override
 	public boolean supportsCommentOn() {
 		return true;
 	}
+
 	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
+
 	@Override
 	public String generateTemporaryTableName(String baseTableName) {
-		String name = super.generateTemporaryTableName(baseTableName);
+		final String name = super.generateTemporaryTableName( baseTableName );
 		return name.length() > 30 ? name.substring( 1, 30 ) : name;
 	}
+
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
+
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit delete rows";
 	}
+
 	@Override
 	public boolean dropTemporaryTableAfterUse() {
 		return false;
 	}
+
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
+
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 	
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
-	/* (non-Javadoc)
-		 * @see org.hibernate.dialect.Dialect#getInExpressionCountLimit()
-		 */
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
 	
 	@Override
 	public boolean forceLobAsLastValue() {
 		return true;
 	}
 
 	@Override
 	public boolean useFollowOnLocking() {
 		return true;
 	}
 	
 	@Override
 	public String getNotExpression( String expression ) {
 		return "not (" + expression + ")";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
index 22c11f6e01..624dc962b9 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
@@ -1,385 +1,413 @@
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
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.jboss.logging.Logger;
 
-import org.hibernate.HibernateException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
-import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for Oracle 9 (uses ANSI-style syntax where possible).
  *
+ * @author Gavin King
+ * @author David Channon
+ *
  * @deprecated Use either Oracle9iDialect or Oracle10gDialect instead
- * @author Gavin King, David Channon
  */
+@SuppressWarnings("deprecation")
 @Deprecated
 public class Oracle9Dialect extends Dialect {
-	
+
 	private static final int PARAM_LIST_SIZE_LIMIT = 1000;
 
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Oracle9Dialect.class.getName());
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			Oracle9Dialect.class.getName()
+	);
 
+	/**
+	 * Constructs a Oracle9Dialect
+	 */
 	public Oracle9Dialect() {
 		super();
 		LOG.deprecatedOracle9Dialect();
 		registerColumnType( Types.BIT, "number(1,0)" );
 		registerColumnType( Types.BIGINT, "number(19,0)" );
 		registerColumnType( Types.SMALLINT, "number(5,0)" );
 		registerColumnType( Types.TINYINT, "number(3,0)" );
 		registerColumnType( Types.INTEGER, "number(10,0)" );
 		registerColumnType( Types.CHAR, "char(1 char)" );
 		registerColumnType( Types.VARCHAR, 4000, "varchar2($l char)" );
 		registerColumnType( Types.VARCHAR, "long" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "date" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, 2000, "raw($l)" );
 		registerColumnType( Types.VARBINARY, "long raw" );
 		registerColumnType( Types.NUMERIC, "number($p,$s)" );
 		registerColumnType( Types.DECIMAL, "number($p,$s)" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		// Oracle driver reports to support getGeneratedKeys(), but they only
 		// support the version taking an array of the names of the columns to
 		// be returned (via its RETURNING clause).  No other driver seems to
 		// support this overloaded version.
-		getDefaultProperties().setProperty(Environment.USE_GET_GENERATED_KEYS, "false");
-		getDefaultProperties().setProperty(Environment.USE_STREAMS_FOR_BINARY, "true");
-		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
-
-		registerFunction( "abs", new StandardSQLFunction("abs") );
-		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
-
-		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
-		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
-		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
-		registerFunction( "cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
-		registerFunction( "cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE) );
-		registerFunction( "exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
-		registerFunction( "ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
-		registerFunction( "sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
-		registerFunction( "sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE) );
-		registerFunction( "stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE) );
-		registerFunction( "sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
-		registerFunction( "tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
-		registerFunction( "tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE) );
-		registerFunction( "variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE) );
-
-		registerFunction( "round", new StandardSQLFunction("round") );
-		registerFunction( "trunc", new StandardSQLFunction("trunc") );
-		registerFunction( "ceil", new StandardSQLFunction("ceil") );
-		registerFunction( "floor", new StandardSQLFunction("floor") );
-
-		registerFunction( "chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER) );
-		registerFunction( "initcap", new StandardSQLFunction("initcap") );
-		registerFunction( "lower", new StandardSQLFunction("lower") );
-		registerFunction( "ltrim", new StandardSQLFunction("ltrim") );
-		registerFunction( "rtrim", new StandardSQLFunction("rtrim") );
-		registerFunction( "soundex", new StandardSQLFunction("soundex") );
-		registerFunction( "upper", new StandardSQLFunction("upper") );
-		registerFunction( "ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
-
-		registerFunction( "to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
-		registerFunction( "to_date", new StandardSQLFunction("to_date", StandardBasicTypes.TIMESTAMP) );
-
-		registerFunction( "current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
-		registerFunction( "current_time", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIME, false) );
-		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
-
-		registerFunction( "last_day", new StandardSQLFunction("last_day", StandardBasicTypes.DATE) );
-		registerFunction( "sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.DATE, false) );
-		registerFunction( "systimestamp", new NoArgSQLFunction("systimestamp", StandardBasicTypes.TIMESTAMP, false) );
-		registerFunction( "uid", new NoArgSQLFunction("uid", StandardBasicTypes.INTEGER, false) );
-		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false) );
-
-		registerFunction( "rowid", new NoArgSQLFunction("rowid", StandardBasicTypes.LONG, false) );
-		registerFunction( "rownum", new NoArgSQLFunction("rownum", StandardBasicTypes.LONG, false) );
+		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "false" );
+		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
+		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
+
+		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
+		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
+
+		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cosh", new StandardSQLFunction( "cosh", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sinh", new StandardSQLFunction( "sinh", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "stddev", new StandardSQLFunction( "stddev", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "tanh", new StandardSQLFunction( "tanh", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "variance", new StandardSQLFunction( "variance", StandardBasicTypes.DOUBLE ) );
+
+		registerFunction( "round", new StandardSQLFunction( "round" ) );
+		registerFunction( "trunc", new StandardSQLFunction( "trunc" ) );
+		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
+		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
+
+		registerFunction( "chr", new StandardSQLFunction( "chr", StandardBasicTypes.CHARACTER ) );
+		registerFunction( "initcap", new StandardSQLFunction( "initcap" ) );
+		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
+		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
+		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
+		registerFunction( "soundex", new StandardSQLFunction( "soundex" ) );
+		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
+		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
+
+		registerFunction( "to_char", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
+		registerFunction( "to_date", new StandardSQLFunction( "to_date", StandardBasicTypes.TIMESTAMP ) );
+
+		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
+		registerFunction( "current_time", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIME, false ) );
+		registerFunction(
+				"current_timestamp", new NoArgSQLFunction(
+				"current_timestamp",
+				StandardBasicTypes.TIMESTAMP,
+				false
+		)
+		);
+
+		registerFunction( "last_day", new StandardSQLFunction( "last_day", StandardBasicTypes.DATE ) );
+		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.DATE, false ) );
+		registerFunction( "systimestamp", new NoArgSQLFunction( "systimestamp", StandardBasicTypes.TIMESTAMP, false ) );
+		registerFunction( "uid", new NoArgSQLFunction( "uid", StandardBasicTypes.INTEGER, false ) );
+		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING, false ) );
+
+		registerFunction( "rowid", new NoArgSQLFunction( "rowid", StandardBasicTypes.LONG, false ) );
+		registerFunction( "rownum", new NoArgSQLFunction( "rownum", StandardBasicTypes.LONG, false ) );
 
 		// Multi-param string dialect functions...
-		registerFunction( "concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", "") );
-		registerFunction( "instr", new StandardSQLFunction("instr", StandardBasicTypes.INTEGER) );
-		registerFunction( "instrb", new StandardSQLFunction("instrb", StandardBasicTypes.INTEGER) );
-		registerFunction( "lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING) );
-		registerFunction( "replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING) );
-		registerFunction( "rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING) );
-		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
-		registerFunction( "substrb", new StandardSQLFunction("substrb", StandardBasicTypes.STRING) );
-		registerFunction( "translate", new StandardSQLFunction("translate", StandardBasicTypes.STRING) );
+		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
+		registerFunction( "instr", new StandardSQLFunction( "instr", StandardBasicTypes.INTEGER ) );
+		registerFunction( "instrb", new StandardSQLFunction( "instrb", StandardBasicTypes.INTEGER ) );
+		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
+		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
+		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
+		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
+		registerFunction( "substrb", new StandardSQLFunction( "substrb", StandardBasicTypes.STRING ) );
+		registerFunction( "translate", new StandardSQLFunction( "translate", StandardBasicTypes.STRING ) );
 
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "instr(?2,?1)" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "vsize(?1)*8" ) );
 		registerFunction( "coalesce", new NvlFunction() );
 
 		// Multi-param numeric dialect functions...
-		registerFunction( "atan2", new StandardSQLFunction("atan2", StandardBasicTypes.FLOAT) );
-		registerFunction( "log", new StandardSQLFunction("log", StandardBasicTypes.INTEGER) );
-		registerFunction( "mod", new StandardSQLFunction("mod", StandardBasicTypes.INTEGER) );
-		registerFunction( "nvl", new StandardSQLFunction("nvl") );
-		registerFunction( "nvl2", new StandardSQLFunction("nvl2") );
-		registerFunction( "power", new StandardSQLFunction("power", StandardBasicTypes.FLOAT) );
+		registerFunction( "atan2", new StandardSQLFunction( "atan2", StandardBasicTypes.FLOAT ) );
+		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.INTEGER ) );
+		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER ) );
+		registerFunction( "nvl", new StandardSQLFunction( "nvl" ) );
+		registerFunction( "nvl2", new StandardSQLFunction( "nvl2" ) );
+		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.FLOAT ) );
 
 		// Multi-param date dialect functions...
-		registerFunction( "add_months", new StandardSQLFunction("add_months", StandardBasicTypes.DATE) );
-		registerFunction( "months_between", new StandardSQLFunction("months_between", StandardBasicTypes.FLOAT) );
-		registerFunction( "next_day", new StandardSQLFunction("next_day", StandardBasicTypes.DATE) );
+		registerFunction( "add_months", new StandardSQLFunction( "add_months", StandardBasicTypes.DATE ) );
+		registerFunction( "months_between", new StandardSQLFunction( "months_between", StandardBasicTypes.FLOAT ) );
+		registerFunction( "next_day", new StandardSQLFunction( "next_day", StandardBasicTypes.DATE ) );
 
-		registerFunction( "str", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
+		registerFunction( "str", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
+	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dual";
 	}
 
+	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
+	@Override
 	public String getCreateSequenceString(String sequenceName) {
-		return "create sequence " + sequenceName; //starts with 1, implicitly
+		//starts with 1, implicitly
+		return "create sequence " + sequenceName;
 	}
 
+	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
+	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade constraints";
 	}
 
+	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
+	@Override
 	public String getForUpdateNowaitString() {
 		return " for update nowait";
 	}
 
+	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
+	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 
 		sql = sql.trim();
 		boolean isForUpdate = false;
-		if ( sql.toLowerCase().endsWith(" for update") ) {
-			sql = sql.substring( 0, sql.length()-11 );
+		if ( sql.toLowerCase().endsWith( " for update" ) ) {
+			sql = sql.substring( 0, sql.length() - 11 );
 			isForUpdate = true;
 		}
 
-		StringBuilder pagingSelect = new StringBuilder( sql.length()+100 );
-		if (hasOffset) {
-			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
+		final StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
+		if ( hasOffset ) {
+			pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
 		}
 		else {
-			pagingSelect.append("select * from ( ");
+			pagingSelect.append( "select * from ( " );
 		}
-		pagingSelect.append(sql);
-		if (hasOffset) {
-			pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
+		pagingSelect.append( sql );
+		if ( hasOffset ) {
+			pagingSelect.append( " ) row_ where rownum <= ?) where rownum_ > ?" );
 		}
 		else {
-			pagingSelect.append(" ) where rownum <= ?");
+			pagingSelect.append( " ) where rownum <= ?" );
 		}
 
 		if ( isForUpdate ) {
 			pagingSelect.append( " for update" );
 		}
 
 		return pagingSelect.toString();
 	}
 
+	@Override
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
+	@Override
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString() + " of " + aliases + " nowait";
 	}
 
+	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
+	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
+	@Override
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
 
+	@Override
 	public String getQuerySequencesString() {
 		return "select sequence_name from user_sequences";
 	}
 
+	@Override
 	public String getSelectGUIDString() {
 		return "select rawtohex(sys_guid()) from dual";
 	}
 
+	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
-        return EXTRACTER;
+		return EXTRACTER;
 	}
 
-	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
-
-		/**
-		 * Extract the name of the violated constraint from the given SQLException.
-		 *
-		 * @param sqle The exception that was the result of the constraint violation.
-		 * @return The extracted constraint name.
-		 */
+	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
+		@Override
 		public String extractConstraintName(SQLException sqle) {
-			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
+			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 			if ( errorCode == 1 || errorCode == 2291 || errorCode == 2292 ) {
 				return extractUsingTemplate( "constraint (", ") violated", sqle.getMessage() );
 			}
 			else if ( errorCode == 1400 ) {
 				// simple nullability constraint
 				return null;
 			}
 			else {
 				return null;
 			}
 		}
-
 	};
 
-	// not final-static to avoid possible classcast exceptions if using different oracle drivers.
-	int oracletypes_cursor_value = 0;
-	public int registerResultSetOutParameter(java.sql.CallableStatement statement,int col) throws SQLException {
-		if(oracletypes_cursor_value==0) {
-			try {
-				Class types = ReflectHelper.classForName("oracle.jdbc.driver.OracleTypes");
-				oracletypes_cursor_value = types.getField("CURSOR").getInt(types.newInstance());
-			} catch (Exception se) {
-				throw new HibernateException("Problem while trying to load or access OracleTypes.CURSOR value",se);
-			}
-		}
+	@Override
+	public int registerResultSetOutParameter(java.sql.CallableStatement statement, int col) throws SQLException {
 		//	register the type of the out param - an Oracle specific type
-		statement.registerOutParameter(col, oracletypes_cursor_value);
+		statement.registerOutParameter( col, OracleTypesHelper.INSTANCE.getOracleCursorTypeSqlType() );
 		col++;
 		return col;
 	}
 
+	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
-		return ( ResultSet ) ps.getObject( 1 );
+		return (ResultSet) ps.getObject( 1 );
 	}
 
+	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
+	@Override
 	public String generateTemporaryTableName(String baseTableName) {
-		String name = super.generateTemporaryTableName(baseTableName);
+		final String name = super.generateTemporaryTableName( baseTableName );
 		return name.length() > 30 ? name.substring( 1, 30 ) : name;
 	}
 
+	@Override
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
+	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit delete rows";
 	}
 
+	@Override
 	public boolean dropTemporaryTableAfterUse() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
+	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select systimestamp from dual";
 	}
 
+	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
-	/* (non-Javadoc)
-		 * @see org.hibernate.dialect.Dialect#getInExpressionCountLimit()
-		 */
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
-	
+
 	@Override
-	public String getNotExpression( String expression ) {
+	public String getNotExpression(String expression) {
 		return "not (" + expression + ")";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java
index d062ffed75..6a390cf734 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java
@@ -1,140 +1,153 @@
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
 package org.hibernate.dialect;
+
 import java.sql.Types;
 
 import org.hibernate.LockOptions;
 import org.hibernate.sql.ANSICaseFragment;
 import org.hibernate.sql.CaseFragment;
 
 /**
  * A dialect for Oracle 9i databases.
  * <p/>
  * Specifies to not use "ANSI join syntax" because 9i does not seem to properly handle it in all cases.
  *
  * @author Steve Ebersole
  */
 public class Oracle9iDialect extends Oracle8iDialect {
 	@Override
 	protected void registerCharacterTypeMappings() {
 		registerColumnType( Types.CHAR, "char(1 char)" );
 		registerColumnType( Types.VARCHAR, 4000, "varchar2($l char)" );
 		registerColumnType( Types.VARCHAR, "long" );
 	}
+
 	@Override
 	protected void registerDateTimeTypeMappings() {
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "date" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 	}
+
 	@Override
 	public CaseFragment createCaseFragment() {
 		// Oracle did add support for ANSI CASE statements in 9i
 		return new ANSICaseFragment();
 	}
+
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		sql = sql.trim();
 		String forUpdateClause = null;
 		boolean isForUpdate = false;
 		final int forUpdateIndex = sql.toLowerCase().lastIndexOf( "for update") ;
 		if ( forUpdateIndex > -1 ) {
 			// save 'for update ...' and then remove it
 			forUpdateClause = sql.substring( forUpdateIndex );
 			sql = sql.substring( 0, forUpdateIndex-1 );
 			isForUpdate = true;
 		}
 
-		StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
+		final StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
 		if (hasOffset) {
-			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
+			pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
 		}
 		else {
-			pagingSelect.append("select * from ( ");
+			pagingSelect.append( "select * from ( " );
 		}
-		pagingSelect.append(sql);
+		pagingSelect.append( sql );
 		if (hasOffset) {
-			pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
+			pagingSelect.append( " ) row_ where rownum <= ?) where rownum_ > ?" );
 		}
 		else {
-			pagingSelect.append(" ) where rownum <= ?");
+			pagingSelect.append( " ) where rownum <= ?" );
 		}
 
 		if ( isForUpdate ) {
 			pagingSelect.append( " " );
 			pagingSelect.append( forUpdateClause );
 		}
 
 		return pagingSelect.toString();
 	}
+
 	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		return getBasicSelectClauseNullString( sqlType );
 	}
+
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select systimestamp from dual";
 	}
+
 	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
 	}
 
-	// locking support
 	@Override
 	public String getForUpdateString() {
 		return " for update";
 	}
+
 	@Override
 	public String getWriteLockString(int timeout) {
 		if ( timeout == LockOptions.NO_WAIT ) {
 			return " for update nowait";
 		}
 		else if ( timeout > 0 ) {
 			// convert from milliseconds to seconds
-			float seconds = timeout / 1000.0f;
+			final float seconds = timeout / 1000.0f;
 			timeout = Math.round(seconds);
 			return " for update wait " + timeout;
 		}
-		else
+		else {
 			return " for update";
+		}
 	}
+
 	@Override
 	public String getReadLockString(int timeout) {
 		return getWriteLockString( timeout );
 	}
+
 	/**
 	 * HHH-4907, I don't know if oracle 8 supports this syntax, so I'd think it is better add this 
 	 * method here. Reopen this issue if you found/know 8 supports it.
+	 * <p/>
+	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return true;
 	}
+
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/OracleDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/OracleDialect.java
index a34dab7190..5823290088 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/OracleDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/OracleDialect.java
@@ -1,121 +1,128 @@
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
 package org.hibernate.dialect;
 import java.sql.Types;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.OracleJoinFragment;
 
 /**
  * An SQL dialect for Oracle, compatible with Oracle 8.
  *
  * @deprecated Use Oracle8iDialect instead.
  * @author Gavin King
  */
+@SuppressWarnings("deprecation")
 @Deprecated
 public class OracleDialect extends Oracle9Dialect {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			OracleDialect.class.getName()
+	);
 
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, OracleDialect.class.getName());
-
+	/**
+	 * Constructs a (DEPRECATED) Oracle9Dialect
+	 */
 	public OracleDialect() {
 		super();
 		LOG.deprecatedOracleDialect();
 		// Oracle8 and previous define only a "DATE" type which
 		//      is used to represent all aspects of date/time
 		registerColumnType( Types.TIMESTAMP, "date" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, 4000, "varchar2($l)" );
 	}
 
 	@Override
-    public JoinFragment createOuterJoinFragment() {
+	public JoinFragment createOuterJoinFragment() {
 		return new OracleJoinFragment();
 	}
+
 	@Override
-    public CaseFragment createCaseFragment() {
+	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
 
 	@Override
-    public String getLimitString(String sql, boolean hasOffset) {
+	public String getLimitString(String sql, boolean hasOffset) {
 
 		sql = sql.trim();
 		boolean isForUpdate = false;
-		if ( sql.toLowerCase().endsWith(" for update") ) {
+		if ( sql.toLowerCase().endsWith( " for update" ) ) {
 			sql = sql.substring( 0, sql.length()-11 );
 			isForUpdate = true;
 		}
 
-		StringBuilder pagingSelect = new StringBuilder( sql.length()+100 );
+		final StringBuilder pagingSelect = new StringBuilder( sql.length()+100 );
 		if (hasOffset) {
-			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
+			pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
 		}
 		else {
-			pagingSelect.append("select * from ( ");
+			pagingSelect.append( "select * from ( " );
 		}
-		pagingSelect.append(sql);
+		pagingSelect.append( sql );
 		if (hasOffset) {
-			pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?");
+			pagingSelect.append( " ) row_ ) where rownum_ <= ? and rownum_ > ?" );
 		}
 		else {
-			pagingSelect.append(" ) where rownum <= ?");
+			pagingSelect.append( " ) where rownum <= ?" );
 		}
 
 		if ( isForUpdate ) {
 			pagingSelect.append( " for update" );
 		}
 
 		return pagingSelect.toString();
 	}
 
 	@Override
-    public String getSelectClauseNullString(int sqlType) {
+	public String getSelectClauseNullString(int sqlType) {
 		switch(sqlType) {
 			case Types.VARCHAR:
 			case Types.CHAR:
 				return "to_char(null)";
 			case Types.DATE:
 			case Types.TIMESTAMP:
 			case Types.TIME:
 				return "to_date(null)";
 			default:
 				return "to_number(null)";
 		}
 	}
 
 	@Override
-    public String getCurrentTimestampSelectString() {
+	public String getCurrentTimestampSelectString() {
 		return "select sysdate from dual";
 	}
 
 	@Override
-    public String getCurrentTimestampSQLFunctionName() {
+	public String getCurrentTimestampSQLFunctionName() {
 		return "sysdate";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/OracleTypesHelper.java b/hibernate-core/src/main/java/org/hibernate/dialect/OracleTypesHelper.java
new file mode 100644
index 0000000000..7446af9560
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/OracleTypesHelper.java
@@ -0,0 +1,128 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.dialect;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.HibernateException;
+import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.internal.util.ReflectHelper;
+
+/**
+ * A Helper for dealing with the OracleTypes class
+ *
+ * @author Steve Ebersole
+ */
+public class OracleTypesHelper {
+	private static final CoreMessageLogger log = Logger.getMessageLogger( CoreMessageLogger.class, OracleTypesHelper.class.getName() );
+
+	/**
+	 * Singleton access
+	 */
+	public static final OracleTypesHelper INSTANCE = new OracleTypesHelper();
+
+	private static final String ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.OracleTypes";
+	private static final String DEPRECATED_ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.driver.OracleTypes";
+
+	private final int oracleCursorTypeSqlType;
+
+	private OracleTypesHelper() {
+		int typeCode = -99;
+		try {
+			typeCode = extractOracleCursorTypeValue();
+		}
+		catch (Exception e) {
+			log.warn( "Unable to resolve Oracle CURSOR JDBC type code", e );
+		}
+		oracleCursorTypeSqlType = typeCode;
+	}
+
+	private int extractOracleCursorTypeValue() {
+		try {
+			return locateOracleTypesClass().getField( "CURSOR" ).getInt( null );
+		}
+		catch ( Exception se ) {
+			throw new HibernateException( "Unable to access OracleTypes.CURSOR value", se );
+		}
+	}
+
+	private Class locateOracleTypesClass() {
+		try {
+			return ReflectHelper.classForName( ORACLE_TYPES_CLASS_NAME );
+		}
+		catch (ClassNotFoundException e) {
+			try {
+				return ReflectHelper.classForName( DEPRECATED_ORACLE_TYPES_CLASS_NAME );
+			}
+			catch (ClassNotFoundException e2) {
+				throw new HibernateException(
+						String.format(
+								"Unable to locate OracleTypes class using either known FQN [%s, %s]",
+								ORACLE_TYPES_CLASS_NAME,
+								DEPRECATED_ORACLE_TYPES_CLASS_NAME
+						),
+						e
+				);
+			}
+		}
+	}
+
+	public int getOracleCursorTypeSqlType() {
+		return oracleCursorTypeSqlType;
+	}
+
+// initial code as copied from Oracle8iDialect
+//
+//	private int oracleCursorTypeSqlType = INIT_ORACLETYPES_CURSOR_VALUE;
+//
+//	public int getOracleCursorTypeSqlType() {
+//		if ( oracleCursorTypeSqlType == INIT_ORACLETYPES_CURSOR_VALUE ) {
+//			// todo : is there really any reason to keep trying if this fails once?
+//			oracleCursorTypeSqlType = extractOracleCursorTypeValue();
+//		}
+//		return oracleCursorTypeSqlType;
+//	}
+//
+//	private int extractOracleCursorTypeValue() {
+//		Class oracleTypesClass;
+//		try {
+//			oracleTypesClass = ReflectHelper.classForName( ORACLE_TYPES_CLASS_NAME );
+//		}
+//		catch ( ClassNotFoundException cnfe ) {
+//			try {
+//				oracleTypesClass = ReflectHelper.classForName( DEPRECATED_ORACLE_TYPES_CLASS_NAME );
+//			}
+//			catch ( ClassNotFoundException e ) {
+//				throw new HibernateException( "Unable to locate OracleTypes class", e );
+//			}
+//		}
+//
+//		try {
+//			return oracleTypesClass.getField( "CURSOR" ).getInt( null );
+//		}
+//		catch ( Exception se ) {
+//			throw new HibernateException( "Unable to access OracleTypes.CURSOR value", se );
+//		}
+//	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/PointbaseDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/PointbaseDialect.java
index fb6a47e01a..b0865aa3b0 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/PointbaseDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/PointbaseDialect.java
@@ -1,109 +1,117 @@
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
 package org.hibernate.dialect;
+
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.persister.entity.Lockable;
 
 /**
- * A <tt>Dialect</tt> for Pointbase.
+ * A Dialect for Pointbase.
+ *
  * @author  Ed Mackenzie
  */
 public class PointbaseDialect extends org.hibernate.dialect.Dialect {
-
 	/**
 	 * Creates new PointbaseDialect
 	 */
 	public PointbaseDialect() {
 		super();
-		registerColumnType( Types.BIT, "smallint" ); //no pointbase BIT
+		//no pointbase BIT
+		registerColumnType( Types.BIT, "smallint" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
-		registerColumnType( Types.TINYINT, "smallint" ); //no pointbase TINYINT
+		//no pointbase TINYINT
+		registerColumnType( Types.TINYINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		//the BLOB type requires a size arguement - this defaults to
 		//bytes - no arg defaults to 1 whole byte!
 		//other argument mods include K - kilobyte, M - megabyte, G - gigabyte.
 		//refer to the PBdevelopers guide for more info.
 		registerColumnType( Types.VARBINARY, "blob($l)" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
+	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
+	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade";
 	}
 
+	@Override
 	public String getForUpdateString() {
 		return "";
 	}
 
+	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// Pointbase has no known variation of a "SELECT ... FOR UPDATE" syntax...
 		if ( lockMode==LockMode.PESSIMISTIC_FORCE_INCREMENT) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_WRITE) {
 			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_READ) {
 			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC) {
 			return new OptimisticLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC_FORCE_INCREMENT) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java
index 446ffab574..0b9d5334bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java
@@ -1,480 +1,517 @@
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
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockOptions;
 import org.hibernate.PessimisticLockException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.PositionSubstringFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * An SQL dialect for Postgres
  * <p/>
  * For discussion of BLOB support in Postgres, as of 8.4, have a peek at
  * <a href="http://jdbc.postgresql.org/documentation/84/binary-data.html">http://jdbc.postgresql.org/documentation/84/binary-data.html</a>.
  * For the effects in regards to Hibernate see <a href="http://in.relation.to/15492.lace">http://in.relation.to/15492.lace</a>
  *
  * @author Gavin King
  */
+@SuppressWarnings("deprecation")
 public class PostgreSQL81Dialect extends Dialect {
 
+	/**
+	 * Constructs a PostgreSQL81Dialect
+	 */
 	public PostgreSQL81Dialect() {
 		super();
 		registerColumnType( Types.BIT, "bool" );
 		registerColumnType( Types.BIGINT, "int8" );
 		registerColumnType( Types.SMALLINT, "int2" );
 		registerColumnType( Types.TINYINT, "int2" );
 		registerColumnType( Types.INTEGER, "int4" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float4" );
 		registerColumnType( Types.DOUBLE, "float8" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "bytea" );
 		registerColumnType( Types.BINARY, "bytea" );
 		registerColumnType( Types.LONGVARCHAR, "text" );
 		registerColumnType( Types.LONGVARBINARY, "bytea" );
 		registerColumnType( Types.CLOB, "text" );
 		registerColumnType( Types.BLOB, "oid" );
 		registerColumnType( Types.NUMERIC, "numeric($p, $s)" );
 		registerColumnType( Types.OTHER, "uuid" );
 
 		registerFunction( "abs", new StandardSQLFunction("abs") );
 		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE) );
 		registerFunction( "exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
 		registerFunction( "ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
 		registerFunction( "log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cbrt", new StandardSQLFunction("cbrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE) );
 		registerFunction( "degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE) );
 		registerFunction( "variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "random", new NoArgSQLFunction("random", StandardBasicTypes.DOUBLE) );
 		registerFunction( "rand", new NoArgSQLFunction("random", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "round", new StandardSQLFunction("round") );
 		registerFunction( "trunc", new StandardSQLFunction("trunc") );
 		registerFunction( "ceil", new StandardSQLFunction("ceil") );
 		registerFunction( "floor", new StandardSQLFunction("floor") );
 
 		registerFunction( "chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER) );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
 		registerFunction( "initcap", new StandardSQLFunction("initcap") );
 		registerFunction( "to_ascii", new StandardSQLFunction("to_ascii") );
 		registerFunction( "quote_ident", new StandardSQLFunction("quote_ident", StandardBasicTypes.STRING) );
 		registerFunction( "quote_literal", new StandardSQLFunction("quote_literal", StandardBasicTypes.STRING) );
 		registerFunction( "md5", new StandardSQLFunction("md5", StandardBasicTypes.STRING) );
 		registerFunction( "ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
 		registerFunction( "char_length", new StandardSQLFunction("char_length", StandardBasicTypes.LONG) );
 		registerFunction( "bit_length", new StandardSQLFunction("bit_length", StandardBasicTypes.LONG) );
 		registerFunction( "octet_length", new StandardSQLFunction("octet_length", StandardBasicTypes.LONG) );
 
 		registerFunction( "age", new StandardSQLFunction("age") );
 		registerFunction( "current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
 		registerFunction( "current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "date_trunc", new StandardSQLFunction( "date_trunc", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "localtime", new NoArgSQLFunction("localtime", StandardBasicTypes.TIME, false) );
 		registerFunction( "localtimestamp", new NoArgSQLFunction("localtimestamp", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP) );
 		registerFunction( "timeofday", new NoArgSQLFunction("timeofday", StandardBasicTypes.STRING) );
 
 		registerFunction( "current_user", new NoArgSQLFunction("current_user", StandardBasicTypes.STRING, false) );
 		registerFunction( "session_user", new NoArgSQLFunction("session_user", StandardBasicTypes.STRING, false) );
 		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false) );
 		registerFunction( "current_database", new NoArgSQLFunction("current_database", StandardBasicTypes.STRING, true) );
 		registerFunction( "current_schema", new NoArgSQLFunction("current_schema", StandardBasicTypes.STRING, true) );
 		
 		registerFunction( "to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
 		registerFunction( "to_date", new StandardSQLFunction("to_date", StandardBasicTypes.DATE) );
 		registerFunction( "to_timestamp", new StandardSQLFunction("to_timestamp", StandardBasicTypes.TIMESTAMP) );
 		registerFunction( "to_number", new StandardSQLFunction("to_number", StandardBasicTypes.BIG_DECIMAL) );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(","||",")" ) );
 
 		registerFunction( "locate", new PositionSubstringFunction() );
 
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as varchar)") );
 
-		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
+		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		getDefaultProperties().setProperty( Environment.NON_CONTEXTUAL_LOB_CREATION, "true" );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
 	public SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.BLOB: {
 				// Force BLOB binding.  Otherwise, byte[] fields annotated
 				// with @Lob will attempt to use
 				// BlobTypeDescriptor.PRIMITIVE_ARRAY_BINDING.  Since the
 				// dialect uses oid for Blobs, byte arrays cannot be used.
 				descriptor = BlobTypeDescriptor.BLOB_BINDING;
 				break;
 			}
 			case Types.CLOB: {
 				descriptor = ClobTypeDescriptor.CLOB_BINDING;
 				break;
 			}
 			default: {
 				descriptor = super.getSqlTypeDescriptorOverride( sqlCode );
 				break;
 			}
 		}
 		return descriptor;
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
+	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName );
 	}
 
+	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "nextval ('" + sequenceName + "')";
 	}
 
+	@Override
 	public String getCreateSequenceString(String sequenceName) {
-		return "create sequence " + sequenceName; //starts with 1, implicitly
+		//starts with 1, implicitly
+		return "create sequence " + sequenceName;
 	}
 
+	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
+	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade";
 	}
+
+	@Override
 	public boolean dropConstraints() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
+	@Override
 	public String getQuerySequencesString() {
 		return "select relname from pg_class where relkind='S'";
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
+	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
-		return new StringBuilder( sql.length()+20 )
-				.append( sql )
-				.append( hasOffset ? " limit ? offset ?" : " limit ?" )
-				.toString();
+		return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
 	}
 
+	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
+	@Override
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
+	@Override
 	public String getIdentitySelectString(String table, String column, int type) {
-		return new StringBuilder().append("select currval('")
-			.append(table)
-			.append('_')
-			.append(column)
-			.append("_seq')")
-			.toString();
+		return "select currval('" + table + '_' + column + "_seq')";
 	}
 
+	@Override
 	public String getIdentityColumnString(int type) {
 		return type==Types.BIGINT ?
 			"bigserial not null" :
 			"serial not null";
 	}
 
+	@Override
 	public boolean hasDataTypeInIdentityColumn() {
 		return false;
 	}
 
+	@Override
 	public String getNoColumnsInsertString() {
 		return "default values";
 	}
 
+	@Override
 	public String getCaseInsensitiveLike(){
 		return "ilike";
 	}
 
 	@Override
 	public boolean supportsCaseInsensitiveLike() {
 		return true;
 	}
 
+	@Override
 	public Class getNativeIdentifierGeneratorClass() {
 		return SequenceGenerator.class;
 	}
 
+	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
-	
+
+	@Override
 	public boolean useInputStreamToInsertBlob() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	/**
 	 * Workaround for postgres bug #1453
+	 * <p/>
+	 * {@inheritDoc}
 	 */
+	@Override
 	public String getSelectClauseNullString(int sqlType) {
-		String typeName = getTypeName(sqlType, 1, 1, 0);
+		String typeName = getTypeName( sqlType, 1, 1, 0 );
 		//trim off the length/precision/scale
-		int loc = typeName.indexOf('(');
-		if (loc>-1) {
-			typeName = typeName.substring(0, loc);
+		final int loc = typeName.indexOf( '(' );
+		if ( loc > -1 ) {
+			typeName = typeName.substring( 0, loc );
 		}
 		return "null::" + typeName;
 	}
 
+	@Override
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
+	@Override
 	public String getCreateTemporaryTableString() {
 		return "create temporary table";
 	}
 
+	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit drop";
 	}
 
-	/*public boolean dropTemporaryTableAfterUse() {
-		//we have to, because postgres sets current tx
-		//to rollback only after a failed create table
-		return true;
-	}*/
-
+	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
+	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
+	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select now()";
 	}
 
+	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
+	@Override
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "true" : "false";
 	}
 
+	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	/**
 	 * Constraint-name extractor for Postgres constraint violation exceptions.
 	 * Orginally contributed by Denny Bartelt.
 	 */
-	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
+	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		public String extractConstraintName(SQLException sqle) {
 			try {
-				int sqlState = Integer.valueOf( JdbcExceptionHelper.extractSqlState( sqle ) );
+				final int sqlState = Integer.valueOf( JdbcExceptionHelper.extractSqlState( sqle ) );
 				switch (sqlState) {
 					// CHECK VIOLATION
-					case 23514: return extractUsingTemplate("violates check constraint \"","\"", sqle.getMessage());
+					case 23514: return extractUsingTemplate( "violates check constraint \"","\"", sqle.getMessage() );
 					// UNIQUE VIOLATION
-					case 23505: return extractUsingTemplate("violates unique constraint \"","\"", sqle.getMessage());
+					case 23505: return extractUsingTemplate( "violates unique constraint \"","\"", sqle.getMessage() );
 					// FOREIGN KEY VIOLATION
-					case 23503: return extractUsingTemplate("violates foreign key constraint \"","\"", sqle.getMessage());
+					case 23503: return extractUsingTemplate( "violates foreign key constraint \"","\"", sqle.getMessage() );
 					// NOT NULL VIOLATION
-					case 23502: return extractUsingTemplate("null value in column \"","\" violates not-null constraint", sqle.getMessage());
+					case 23502: return extractUsingTemplate( "null value in column \"","\" violates not-null constraint", sqle.getMessage() );
 					// TODO: RESTRICT VIOLATION
 					case 23001: return null;
 					// ALL OTHER
 					default: return null;
 				}
-			} catch (NumberFormatException nfe) {
+			}
+			catch (NumberFormatException nfe) {
 				return null;
 			}
 		}
 	};
 	
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				final String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 
-				if ( "40P01".equals( sqlState ) ) { // DEADLOCK DETECTED
+				if ( "40P01".equals( sqlState ) ) {
+					// DEADLOCK DETECTED
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
-				if ( "55P03".equals( sqlState ) ) { // LOCK NOT AVAILABLE
+				if ( "55P03".equals( sqlState ) ) {
+					// LOCK NOT AVAILABLE
 					return new PessimisticLockException( message, sqlException, sql );
 				}
 
 				// returning null allows other delegates to operate
 				return null;
 			}
 		};
 	}
-	
+
+	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		// Register the type of the out param - PostgreSQL uses Types.OTHER
-		statement.registerOutParameter(col++, Types.OTHER);
+		statement.registerOutParameter( col++, Types.OTHER );
 		return col;
 	}
 
+	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
-		return (ResultSet) ps.getObject(1);
+		return (ResultSet) ps.getObject( 1 );
 	}
 
+	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
-	//only necessary for postgre < 7.4
-	//http://anoncvs.postgresql.org/cvsweb.cgi/pgsql/doc/src/sgml/ref/create_sequence.sgml
+	/**
+	 * only necessary for postgre < 7.4  See http://anoncvs.postgresql.org/cvsweb.cgi/pgsql/doc/src/sgml/ref/create_sequence.sgml
+	 * <p/>
+	 * {@inheritDoc}
+	 */
+	@Override
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) {
 		return getCreateSequenceString( sequenceName ) + " start " + initialValue + " increment " + incrementSize;
 	}
 	
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-// seems to not really...
-//	public boolean supportsRowValueConstructorSyntax() {
-//		return true;
-//	}
-
-
-    public boolean supportsEmptyInList() {
+	@Override
+	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return false;
 	}
 
-	// locking support
+	@Override
 	public String getForUpdateString() {
 		return " for update";
 	}
 
+	@Override
 	public String getWriteLockString(int timeout) {
-		if ( timeout == LockOptions.NO_WAIT )
+		if ( timeout == LockOptions.NO_WAIT ) {
 			return " for update nowait";
-		else
+		}
+		else {
 			return " for update";
+		}
 	}
 
+	@Override
 	public String getReadLockString(int timeout) {
-		if ( timeout == LockOptions.NO_WAIT )
+		if ( timeout == LockOptions.NO_WAIT ) {
 			return " for share nowait";
-		else
+		}
+		else {
 			return " for share";
+		}
 	}
 
+	@Override
 	public boolean supportsRowValueConstructorSyntax() {
 		return true;
 	}
 	
 	@Override
 	public String getForUpdateNowaitString() {
 		return getForUpdateString() + " nowait ";
 	}
 	
 	@Override
 	public String getForUpdateNowaitString(String aliases) {
-		return getForUpdateString(aliases) + " nowait ";
+		return getForUpdateString( aliases ) + " nowait ";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL82Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL82Dialect.java
index 50b84be0da..c4fc513639 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL82Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL82Dialect.java
@@ -1,36 +1,36 @@
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
 package org.hibernate.dialect;
 
 /**
  * An SQL dialect for Postgres 8.2 and later, adds support for "if exists" when dropping tables
  * 
  * @author edalquist
  */
 public class PostgreSQL82Dialect extends PostgreSQL81Dialect {
 	@Override
-    public boolean supportsIfExistsBeforeTableName() {
-        return true;
-    }
+	public boolean supportsIfExistsBeforeTableName() {
+		return true;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
index 4a8a3e3819..bf70aa08f6 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
@@ -1,39 +1,39 @@
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
 package org.hibernate.dialect;
 
-
 /**
  * An SQL dialect for Postgres
  * <p/>
  * For discussion of BLOB support in Postgres, as of 8.4, have a peek at
  * <a href="http://jdbc.postgresql.org/documentation/84/binary-data.html">http://jdbc.postgresql.org/documentation/84/binary-data.html</a>.
  * For the effects in regards to Hibernate see <a href="http://in.relation.to/15492.lace">http://in.relation.to/15492.lace</a>
  *
  * @author Gavin King
+ *
  * @deprecated use {@link PostgreSQL82Dialect} instead
  */
 @Deprecated
 public class PostgreSQLDialect extends PostgreSQL82Dialect {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/PostgresPlusDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/PostgresPlusDialect.java
index 565f74f78d..60c2eeb131 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/PostgresPlusDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/PostgresPlusDialect.java
@@ -1,91 +1,99 @@
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
 package org.hibernate.dialect;
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for Postgres Plus
  *
  * @author Jim Mlodgenski
  */
+@SuppressWarnings("deprecation")
 public class PostgresPlusDialect extends PostgreSQLDialect {
-
+	/**
+	 * Constructs a PostgresPlusDialect
+	 */
 	public PostgresPlusDialect() {
 		super();
 
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex" ) );
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.DATE, false ) );
 		registerFunction( "rowid", new NoArgSQLFunction( "rowid", StandardBasicTypes.LONG, false ) );
 		registerFunction( "rownum", new NoArgSQLFunction( "rownum", StandardBasicTypes.LONG, false ) );
 		registerFunction( "instr", new StandardSQLFunction( "instr", StandardBasicTypes.INTEGER ) );
 		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
 		registerFunction( "translate", new StandardSQLFunction( "translate", StandardBasicTypes.STRING ) );
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "coalesce", new NvlFunction() );
 		registerFunction( "atan2", new StandardSQLFunction( "atan2", StandardBasicTypes.FLOAT ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER ) );
 		registerFunction( "nvl", new StandardSQLFunction( "nvl" ) );
 		registerFunction( "nvl2", new StandardSQLFunction( "nvl2" ) );
 		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.FLOAT ) );
 		registerFunction( "add_months", new StandardSQLFunction( "add_months", StandardBasicTypes.DATE ) );
 		registerFunction( "months_between", new StandardSQLFunction( "months_between", StandardBasicTypes.FLOAT ) );
 		registerFunction( "next_day", new StandardSQLFunction( "next_day", StandardBasicTypes.DATE ) );
 	}
 
+	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select sysdate";
 	}
 
+	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		return "sysdate";
 	}
 
+	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		statement.registerOutParameter( col, Types.REF );
 		col++;
 		return col;
 	}
 
+	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
-		return ( ResultSet ) ps.getObject( 1 );
+		return (ResultSet) ps.getObject( 1 );
 	}
 
+	@Override
 	public String getSelectGUIDString() {
 		return "select uuid_generate_v1";
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/ProgressDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/ProgressDialect.java
index cae9befaad..aff93818a0 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/ProgressDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/ProgressDialect.java
@@ -1,73 +1,80 @@
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
 package org.hibernate.dialect;
+
 import java.sql.Types;
 
 /**
  * An SQL dialect compatible with Progress 9.1C<br>
  *<br>
  * Connection Parameters required:
  *<ul>
  * <li>hibernate.dialect org.hibernate.sql.ProgressDialect
  * <li>hibernate.driver com.progress.sql.jdbc.JdbcProgressDriver
  * <li>hibernate.url jdbc:JdbcProgress:T:host:port:dbname;WorkArounds=536870912
  * <li>hibernate.username username
  * <li>hibernate.password password
  *</ul>
  * The WorkArounds parameter in the URL is required to avoid an error
  * in the Progress 9.1C JDBC driver related to PreparedStatements.
  * @author Phillip Baird
  *
  */
 public class ProgressDialect extends Dialect {
+	/**
+	 * Constructs a ProgressDialect
+	 */
 	public ProgressDialect() {
 		super();
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BIGINT, "numeric" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "character(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "real" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "varbinary($l)" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 	}
 
+	@Override
 	public boolean hasAlterTable(){
 		return false;
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
+	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
index ccec37a7c7..d168165a2f 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
@@ -1,358 +1,385 @@
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
 package org.hibernate.dialect;
+
 import java.sql.Types;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.LockMode;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * This is the Hibernate dialect for the Unisys 2200 Relational Database (RDMS).
  * This dialect was developed for use with Hibernate 3.0.5. Other versions may
  * require modifications to the dialect.
- *
+ * <p/>
  * Version History:
  * Also change the version displayed below in the constructor
  * 1.1
  * 1.0  2005-10-24  CDH - First dated version for use with CP 11
  *
  * @author Ploski and Hanson
  */
+@SuppressWarnings("deprecation")
 public class RDMSOS2200Dialect extends Dialect {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			RDMSOS2200Dialect.class.getName()
+	);
 
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, RDMSOS2200Dialect.class.getName());
-
+	/**
+	 * Constructs a RDMSOS2200Dialect
+	 */
 	public RDMSOS2200Dialect() {
 		super();
 		// Display the dialect version.
 		LOG.rdmsOs2200Dialect();
 
-        /**
-         * This section registers RDMS Built-in Functions (BIFs) with Hibernate.
-         * The first parameter is the 'register' function name with Hibernate.
-         * The second parameter is the defined RDMS SQL Function and it's
-         * characteristics. If StandardSQLFunction(...) is used, the RDMS BIF
-         * name and the return type (if any) is specified.  If
-         * SQLFunctionTemplate(...) is used, the return type and a template
-         * string is provided, plus an optional hasParenthesesIfNoArgs flag.
-         */
-		registerFunction( "abs", new StandardSQLFunction("abs") );
-		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
+		/**
+		 * This section registers RDMS Built-in Functions (BIFs) with Hibernate.
+		 * The first parameter is the 'register' function name with Hibernate.
+		 * The second parameter is the defined RDMS SQL Function and it's
+		 * characteristics. If StandardSQLFunction(...) is used, the RDMS BIF
+		 * name and the return type (if any) is specified.  If
+		 * SQLFunctionTemplate(...) is used, the return type and a template
+		 * string is provided, plus an optional hasParenthesesIfNoArgs flag.
+		 */
+		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
+		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
-		registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
-		registerFunction("char_length", new StandardSQLFunction("char_length", StandardBasicTypes.INTEGER) );
-		registerFunction("character_length", new StandardSQLFunction("character_length", StandardBasicTypes.INTEGER) );
+		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
+		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.INTEGER ) );
+		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.INTEGER ) );
 
 		// The RDMS concat() function only supports 2 parameters
-		registerFunction( "concat", new SQLFunctionTemplate(StandardBasicTypes.STRING, "concat(?1, ?2)") );
-		registerFunction( "instr", new StandardSQLFunction("instr", StandardBasicTypes.STRING) );
-		registerFunction( "lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING) );
-		registerFunction( "replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING) );
-		registerFunction( "rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING) );
-		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
-
-		registerFunction("lcase", new StandardSQLFunction("lcase") );
-		registerFunction("lower", new StandardSQLFunction("lower") );
-		registerFunction("ltrim", new StandardSQLFunction("ltrim") );
-		registerFunction("reverse", new StandardSQLFunction("reverse") );
-		registerFunction("rtrim", new StandardSQLFunction("rtrim") );
+		registerFunction( "concat", new SQLFunctionTemplate( StandardBasicTypes.STRING, "concat(?1, ?2)" ) );
+		registerFunction( "instr", new StandardSQLFunction( "instr", StandardBasicTypes.STRING ) );
+		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
+		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
+		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
+		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
+
+		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
+		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
+		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
+		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
+		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 
 		// RDMS does not directly support the trim() function, we use rtrim() and ltrim()
-		registerFunction("trim", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "ltrim(rtrim(?1))" ) );
-		registerFunction("soundex", new StandardSQLFunction("soundex") );
-		registerFunction("space", new StandardSQLFunction("space", StandardBasicTypes.STRING) );
-		registerFunction("ucase", new StandardSQLFunction("ucase") );
-		registerFunction("upper", new StandardSQLFunction("upper") );
-
-		registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
-		registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
-		registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
-		registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
-		registerFunction("cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE) );
-		registerFunction("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE) );
-		registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
-		registerFunction("ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
-		registerFunction("log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE) );
-		registerFunction("log10", new StandardSQLFunction("log10", StandardBasicTypes.DOUBLE) );
-		registerFunction("pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE) );
-		registerFunction("rand", new NoArgSQLFunction("rand", StandardBasicTypes.DOUBLE) );
-		registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
-		registerFunction("sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE) );
-		registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
-		registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
-		registerFunction("tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE) );
-
-		registerFunction( "round", new StandardSQLFunction("round") );
-		registerFunction( "trunc", new StandardSQLFunction("trunc") );
-		registerFunction( "ceil", new StandardSQLFunction("ceil") );
-		registerFunction( "floor", new StandardSQLFunction("floor") );
-
-		registerFunction( "chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER) );
-		registerFunction( "initcap", new StandardSQLFunction("initcap") );
-
-		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false) );
-
-		registerFunction( "current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
-		registerFunction( "current_time", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIME, false) );
-		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
-		registerFunction("curdate", new NoArgSQLFunction("curdate",StandardBasicTypes.DATE) );
-		registerFunction("curtime", new NoArgSQLFunction("curtime",StandardBasicTypes.TIME) );
-		registerFunction("days", new StandardSQLFunction("days",StandardBasicTypes.INTEGER) );
-		registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth",StandardBasicTypes.INTEGER) );
-		registerFunction("dayname", new StandardSQLFunction("dayname",StandardBasicTypes.STRING) );
-		registerFunction("dayofweek", new StandardSQLFunction("dayofweek",StandardBasicTypes.INTEGER) );
-		registerFunction("dayofyear", new StandardSQLFunction("dayofyear",StandardBasicTypes.INTEGER) );
-		registerFunction("hour", new StandardSQLFunction("hour",StandardBasicTypes.INTEGER) );
-		registerFunction("last_day", new StandardSQLFunction("last_day",StandardBasicTypes.DATE) );
-		registerFunction("microsecond", new StandardSQLFunction("microsecond",StandardBasicTypes.INTEGER) );
-		registerFunction("minute", new StandardSQLFunction("minute",StandardBasicTypes.INTEGER) );
-		registerFunction("month", new StandardSQLFunction("month",StandardBasicTypes.INTEGER) );
-		registerFunction("monthname", new StandardSQLFunction("monthname",StandardBasicTypes.STRING) );
-		registerFunction("now", new NoArgSQLFunction("now",StandardBasicTypes.TIMESTAMP) );
-		registerFunction("quarter", new StandardSQLFunction("quarter",StandardBasicTypes.INTEGER) );
-		registerFunction("second", new StandardSQLFunction("second",StandardBasicTypes.INTEGER) );
-		registerFunction("time", new StandardSQLFunction("time",StandardBasicTypes.TIME) );
-		registerFunction("timestamp", new StandardSQLFunction("timestamp",StandardBasicTypes.TIMESTAMP) );
-		registerFunction("week", new StandardSQLFunction("week",StandardBasicTypes.INTEGER) );
-		registerFunction("year", new StandardSQLFunction("year",StandardBasicTypes.INTEGER) );
-
-		registerFunction("atan2", new StandardSQLFunction("atan2",StandardBasicTypes.DOUBLE) );
-		registerFunction( "mod", new StandardSQLFunction("mod",StandardBasicTypes.INTEGER) );
-		registerFunction( "nvl", new StandardSQLFunction("nvl") );
-		registerFunction( "power", new StandardSQLFunction("power", StandardBasicTypes.DOUBLE) );
+		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "ltrim(rtrim(?1))" ) );
+		registerFunction( "soundex", new StandardSQLFunction( "soundex" ) );
+		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
+		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
+		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
+
+		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cosh", new StandardSQLFunction( "cosh", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sinh", new StandardSQLFunction( "sinh", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "tanh", new StandardSQLFunction( "tanh", StandardBasicTypes.DOUBLE ) );
+
+		registerFunction( "round", new StandardSQLFunction( "round" ) );
+		registerFunction( "trunc", new StandardSQLFunction( "trunc" ) );
+		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
+		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
+
+		registerFunction( "chr", new StandardSQLFunction( "chr", StandardBasicTypes.CHARACTER ) );
+		registerFunction( "initcap", new StandardSQLFunction( "initcap" ) );
+
+		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING, false ) );
+
+		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
+		registerFunction( "current_time", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIME, false ) );
+		registerFunction( "current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP, false ) );
+		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
+		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
+		registerFunction( "days", new StandardSQLFunction( "days", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
+		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
+		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
+		registerFunction( "last_day", new StandardSQLFunction( "last_day", StandardBasicTypes.DATE ) );
+		registerFunction( "microsecond", new StandardSQLFunction( "microsecond", StandardBasicTypes.INTEGER ) );
+		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
+		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
+		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
+		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
+		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
+		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
+		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
+		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
+
+		registerFunction( "atan2", new StandardSQLFunction( "atan2", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER ) );
+		registerFunction( "nvl", new StandardSQLFunction( "nvl" ) );
+		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.DOUBLE ) );
 
 		/**
 		 * For a list of column types to register, see section A-1
 		 * in 7862 7395, the Unisys JDBC manual.
 		 *
 		 * Here are column sizes as documented in Table A-1 of
 		 * 7831 0760, "Enterprise Relational Database Server
 		 * for ClearPath OS2200 Administration Guide"
 		 * Numeric - 21
 		 * Decimal - 22 (21 digits plus one for sign)
 		 * Float   - 60 bits
 		 * Char    - 28000
 		 * NChar   - 14000
 		 * BLOB+   - 4294967296 (4 Gb)
 		 * + RDMS JDBC driver does not support BLOBs
 		 *
 		 * DATE, TIME and TIMESTAMP literal formats are
 		 * are all described in section 2.3.4 DATE Literal Format
 		 * in 7830 8160.
 		 * The DATE literal format is: YYYY-MM-DD
 		 * The TIME literal format is: HH:MM:SS[.[FFFFFF]]
 		 * The TIMESTAMP literal format is: YYYY-MM-DD HH:MM:SS[.[FFFFFF]]
 		 *
 		 * Note that $l (dollar-L) will use the length value if provided.
 		 * Also new for Hibernate3 is the $p percision and $s (scale) parameters
 		 */
-		registerColumnType(Types.BIT, "SMALLINT");
-		registerColumnType(Types.TINYINT, "SMALLINT");
-		registerColumnType(Types.BIGINT, "NUMERIC(21,0)");
-		registerColumnType(Types.SMALLINT, "SMALLINT");
-		registerColumnType(Types.CHAR, "CHARACTER(1)");
-		registerColumnType(Types.DOUBLE, "DOUBLE PRECISION");
-		registerColumnType(Types.FLOAT, "FLOAT");
-		registerColumnType(Types.REAL, "REAL");
-		registerColumnType(Types.INTEGER, "INTEGER");
-		registerColumnType(Types.NUMERIC, "NUMERIC(21,$l)");
-		registerColumnType(Types.DECIMAL, "NUMERIC(21,$l)");
-		registerColumnType(Types.DATE, "DATE");
-		registerColumnType(Types.TIME, "TIME");
-		registerColumnType(Types.TIMESTAMP, "TIMESTAMP");
-		registerColumnType(Types.VARCHAR, "CHARACTER($l)");
-        registerColumnType(Types.BLOB, "BLOB($l)" );
-        /*
+		registerColumnType( Types.BIT, "SMALLINT" );
+		registerColumnType( Types.TINYINT, "SMALLINT" );
+		registerColumnType( Types.BIGINT, "NUMERIC(21,0)" );
+		registerColumnType( Types.SMALLINT, "SMALLINT" );
+		registerColumnType( Types.CHAR, "CHARACTER(1)" );
+		registerColumnType( Types.DOUBLE, "DOUBLE PRECISION" );
+		registerColumnType( Types.FLOAT, "FLOAT" );
+		registerColumnType( Types.REAL, "REAL" );
+		registerColumnType( Types.INTEGER, "INTEGER" );
+		registerColumnType( Types.NUMERIC, "NUMERIC(21,$l)" );
+		registerColumnType( Types.DECIMAL, "NUMERIC(21,$l)" );
+		registerColumnType( Types.DATE, "DATE" );
+		registerColumnType( Types.TIME, "TIME" );
+		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
+		registerColumnType( Types.VARCHAR, "CHARACTER($l)" );
+		registerColumnType( Types.BLOB, "BLOB($l)" );
+		/*
          * The following types are not supported in RDMS/JDBC and therefore commented out.
          * However, in some cases, mapping them to CHARACTER columns works
          * for many applications, but does not work for all cases.
          */
-        // registerColumnType(Types.VARBINARY, "CHARACTER($l)");
-        // registerColumnType(Types.BLOB, "CHARACTER($l)" );  // For use prior to CP 11.0
-        // registerColumnType(Types.CLOB, "CHARACTER($l)" );
+		// registerColumnType(Types.VARBINARY, "CHARACTER($l)");
+		// registerColumnType(Types.BLOB, "CHARACTER($l)" );  // For use prior to CP 11.0
+		// registerColumnType(Types.CLOB, "CHARACTER($l)" );
 	}
 
 
 	// Dialect method overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-    /**
-     * RDMS does not support qualifing index names with the schema name.
-     */
+	/**
+	 * RDMS does not support qualifing index names with the schema name.
+	 * <p/>
+	 * {@inheritDoc}
+	 */
+	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
 	/**
 	 * The RDMS DB supports the 'FOR UPDATE OF' clause. However, the RDMS-JDBC
-     * driver does not support this feature, so a false is return.
-     * The base dialect also returns a false, but we will leave this over-ride
-     * in to make sure it stays false.
+	 * driver does not support this feature, so a false is return.
+	 * The base dialect also returns a false, but we will leave this over-ride
+	 * in to make sure it stays false.
+	 * <p/>
+	 * {@inheritDoc}
 	 */
+	@Override
 	public boolean forUpdateOfColumns() {
 		return false;
 	}
 
 	/**
 	 * Since the RDMS-JDBC driver does not support for updates, this string is
-     * set to an empty string. Whenever, the driver does support this feature,
-     * the returned string should be " FOR UPDATE OF". Note that RDMS does not
-     * support the string 'FOR UPDATE' string.
+	 * set to an empty string. Whenever, the driver does support this feature,
+	 * the returned string should be " FOR UPDATE OF". Note that RDMS does not
+	 * support the string 'FOR UPDATE' string.
+	 * <p/>
+	 * {@inheritDoc}
 	 */
+	@Override
 	public String getForUpdateString() {
-		return ""; // Original Dialect.java returns " for update";
+		// Original Dialect.java returns " for update";
+		return "";
 	}
 
 	// Verify the state of this new method in Hibernate 3.0 Dialect.java
-    /**
-     * RDMS does not support Cascade Deletes.
-     * Need to review this in the future when support is provided.
-     */
+
+	/**
+	 * RDMS does not support Cascade Deletes.
+	 * Need to review this in the future when support is provided.
+	 * <p/>
+	 * {@inheritDoc}
+	 */
+	@Override
 	public boolean supportsCascadeDelete() {
-		return false; // Origial Dialect.java returns true;
+		return false;
 	}
 
 	/**
-     * Currently, RDMS-JDBC does not support ForUpdate.
-     * Need to review this in the future when support is provided.
+	 * Currently, RDMS-JDBC does not support ForUpdate.
+	 * Need to review this in the future when support is provided.
+	 * <p/>
+	 * {@inheritDoc}
 	 */
-    public boolean supportsOuterJoinForUpdate() {
+	@Override
+	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
+	@Override
 	public String getNullColumnString() {
 		// The keyword used to specify a nullable column.
 		return " null";
 	}
 
-    // *** Sequence methods - start. The RDMS dialect needs these
-
-    // methods to make it possible to use the Native Id generator
-
+	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
+	@Override
 	public String getSequenceNextValString(String sequenceName) {
-	    // The where clause was added to eliminate this statement from Brute Force Searches.
-        return  "select permuted_id('NEXT',31) from rdms.rdms_dummy where key_col = 1 ";
+		// The where clause was added to eliminate this statement from Brute Force Searches.
+		return "select permuted_id('NEXT',31) from rdms.rdms_dummy where key_col = 1 ";
 	}
 
+	@Override
 	public String getCreateSequenceString(String sequenceName) {
-        // We must return a valid RDMS/RSA command from this method to
-        // prevent RDMS/RSA from issuing *ERROR 400
-        return "";
+		// We must return a valid RDMS/RSA command from this method to
+		// prevent RDMS/RSA from issuing *ERROR 400
+		return "";
 	}
 
+	@Override
 	public String getDropSequenceString(String sequenceName) {
-        // We must return a valid RDMS/RSA command from this method to
-        // prevent RDMS/RSA from issuing *ERROR 400
-        return "";
+		// We must return a valid RDMS/RSA command from this method to
+		// prevent RDMS/RSA from issuing *ERROR 400
+		return "";
 	}
 
-	// *** Sequence methods - end
-
-    public String getCascadeConstraintsString() {
-        // Used with DROP TABLE to delete all records in the table.
-        return " including contents";
-    }
+	@Override
+	public String getCascadeConstraintsString() {
+		// Used with DROP TABLE to delete all records in the table.
+		return " including contents";
+	}
 
+	@Override
 	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
-    public String getLimitString(String sql, int offset, int limit) {
+	@Override
+	public String getLimitString(String sql, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
-		return new StringBuilder( sql.length() + 40 )
-				.append( sql )
-				.append( " fetch first " )
-				.append( limit )
-				.append( " rows only " )
-				.toString();
+		return sql + " fetch first " + limit + " rows only ";
 	}
 
+	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsUnionAll() {
 		// RDMS supports the UNION ALL clause.
-          return true;
+		return true;
 	}
 
+	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// RDMS has no known variation of a "SELECT ... FOR UPDATE" syntax...
-		if ( lockMode==LockMode.PESSIMISTIC_FORCE_INCREMENT) {
-			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode);
+		if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT ) {
+			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
-		else if ( lockMode==LockMode.PESSIMISTIC_WRITE) {
-			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode);
+		else if ( lockMode == LockMode.PESSIMISTIC_WRITE ) {
+			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode );
 		}
-		else if ( lockMode==LockMode.PESSIMISTIC_READ) {
-			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode);
+		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
+			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode );
 		}
-		else if ( lockMode==LockMode.OPTIMISTIC) {
-			return new OptimisticLockingStrategy( lockable, lockMode);
+		else if ( lockMode == LockMode.OPTIMISTIC ) {
+			return new OptimisticLockingStrategy( lockable, lockMode );
 		}
-		else if ( lockMode==LockMode.OPTIMISTIC_FORCE_INCREMENT) {
-			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode);
+		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT ) {
+			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/ResultColumnReferenceStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/ResultColumnReferenceStrategy.java
index 4e599e9337..fa007e435b 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/ResultColumnReferenceStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/ResultColumnReferenceStrategy.java
@@ -1,89 +1,76 @@
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
-package org.hibernate.dialect;
-import java.io.ObjectStreamException;
-import java.io.Serializable;
-import java.util.HashMap;
-import java.util.Map;
+package org.hibernate.dialect;
 
 /**
  * Defines how we need to reference columns in the group-by, having, and order-by
  * clauses.
  *
  * @author Steve Ebersole
  */
-public class ResultColumnReferenceStrategy implements Serializable {
-
-	private static final Map INSTANCES = new HashMap();
-
+public enum ResultColumnReferenceStrategy {
 	/**
 	 * This strategy says to reference the result columns by the qualified column name
 	 * found in the result source.  This strategy is not strictly allowed by ANSI SQL
 	 * but is Hibernate's legacy behavior and is also the fastest of the strategies; thus
 	 * it should be used if supported by the underlying database.
 	 */
-	public static final ResultColumnReferenceStrategy SOURCE = new ResultColumnReferenceStrategy( "source");
-
+	SOURCE,
 	/**
 	 * For databases which do not support {@link #SOURCE}, ANSI SQL defines two allowable
 	 * approaches.  One is to reference the result column by the alias it is given in the
 	 * result source (if it is given an alias).  This strategy says to use this approach.
 	 * <p/>
 	 * The other QNSI SQL compliant approach is {@link #ORDINAL}.
 	 */
-	public static final ResultColumnReferenceStrategy ALIAS = new ResultColumnReferenceStrategy( "alias" );
-
+	ALIAS,
 	/**
 	 * For databases which do not support {@link #SOURCE}, ANSI SQL defines two allowable
 	 * approaches.  One is to reference the result column by the ordinal position at which
 	 * it appears in the result source.  This strategy says to use this approach.
 	 * <p/>
 	 * The other QNSI SQL compliant approach is {@link #ALIAS}.
 	 */
-	public static final ResultColumnReferenceStrategy ORDINAL = new ResultColumnReferenceStrategy( "ordinal" );
+	ORDINAL;
 
-	static {
-		ResultColumnReferenceStrategy.INSTANCES.put( ResultColumnReferenceStrategy.SOURCE.name, ResultColumnReferenceStrategy.SOURCE );
-		ResultColumnReferenceStrategy.INSTANCES.put( ResultColumnReferenceStrategy.ALIAS.name, ResultColumnReferenceStrategy.ALIAS );
-		ResultColumnReferenceStrategy.INSTANCES.put( ResultColumnReferenceStrategy.ORDINAL.name, ResultColumnReferenceStrategy.ORDINAL );
-	}
-
-	private final String name;
-
-	public ResultColumnReferenceStrategy(String name) {
-		this.name = name;
-	}
-
-	public String toString() {
-		return name;
-	}
-
-	private Object readResolve() throws ObjectStreamException {
-		return parse( name );
-	}
-
-	public static ResultColumnReferenceStrategy parse(String name) {
-		return ( ResultColumnReferenceStrategy ) ResultColumnReferenceStrategy.INSTANCES.get( name );
+	/**
+	 * Resolves the strategy by name, in a case insensitive manner.  If the name cannot be resolved, {@link #SOURCE}
+	 * is returned as the default.
+	 *
+	 * @param name The strategy name to resolve
+	 *
+	 * @return The resolved strategy
+	 */
+	public static ResultColumnReferenceStrategy resolveByName(String name) {
+		if ( ALIAS.name().equalsIgnoreCase( name ) ) {
+			return ALIAS;
+		}
+		else if ( ORDINAL.name().equalsIgnoreCase( name ) ) {
+			return ORDINAL;
+		}
+		else {
+			return SOURCE;
+		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
index 5cf6eda7db..2c6448498b 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
@@ -1,219 +1,237 @@
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
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect compatible with SAP DB.
+ *
  * @author Brad Clow
  */
 public class SAPDBDialect extends Dialect {
-
+	/**
+	 * Constructs a SAPDBDialect
+	 */
 	public SAPDBDialect() {
 		super();
 		registerColumnType( Types.BIT, "boolean" );
 		registerColumnType( Types.BIGINT, "fixed(19,0)" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "fixed(3,0)" );
 		registerColumnType( Types.INTEGER, "int" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "long byte" );
 		registerColumnType( Types.NUMERIC, "fixed($p,$s)" );
 		registerColumnType( Types.CLOB, "long varchar" );
 		registerColumnType( Types.BLOB, "long byte" );
 
-		registerFunction( "abs", new StandardSQLFunction("abs") );
-		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
-
-		registerFunction( "exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
-		registerFunction( "ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
-		registerFunction( "log", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
-		registerFunction( "pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE) );
-		registerFunction( "power", new StandardSQLFunction("power") );
-		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
-		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
-		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
-		registerFunction( "cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
-		registerFunction( "cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE) );
-		registerFunction( "cot", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
-		registerFunction( "sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
-		registerFunction( "sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE) );
-		registerFunction( "tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
-		registerFunction( "tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE) );
-		registerFunction( "radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE) );
-		registerFunction( "degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE) );
-		registerFunction( "atan2", new StandardSQLFunction("atan2", StandardBasicTypes.DOUBLE) );
-
-		registerFunction( "round", new StandardSQLFunction("round") );
-		registerFunction( "trunc", new StandardSQLFunction("trunc") );
-		registerFunction( "ceil", new StandardSQLFunction("ceil") );
-		registerFunction( "floor", new StandardSQLFunction("floor") );
-		registerFunction( "greatest", new StandardSQLFunction("greatest") );
-		registerFunction( "least", new StandardSQLFunction("least") );
-
-		registerFunction("time", new StandardSQLFunction("time", StandardBasicTypes.TIME) );
-		registerFunction("timestamp", new StandardSQLFunction("timestamp", StandardBasicTypes.TIMESTAMP) );
-		registerFunction("date", new StandardSQLFunction("date", StandardBasicTypes.DATE) );
-		registerFunction("microsecond", new StandardSQLFunction("microsecond", StandardBasicTypes.INTEGER) );
-
-		registerFunction( "second", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "second(?1)") );
-		registerFunction( "minute", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "minute(?1)") );
-		registerFunction( "hour", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "hour(?1)") );
-		registerFunction( "day", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "day(?1)") );
-		registerFunction( "month", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "month(?1)") );
-		registerFunction( "year", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "year(?1)") );
-
-		registerFunction( "extract", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "?1(?3)") );
-
-		registerFunction("dayname", new StandardSQLFunction("dayname", StandardBasicTypes.STRING) );
-		registerFunction("monthname", new StandardSQLFunction("monthname", StandardBasicTypes.STRING) );
-		registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", StandardBasicTypes.INTEGER) );
-		registerFunction("dayofweek", new StandardSQLFunction("dayofweek", StandardBasicTypes.INTEGER) );
-		registerFunction("dayofyear", new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER) );
-		registerFunction("weekofyear", new StandardSQLFunction("weekofyear", StandardBasicTypes.INTEGER) );
-
-		registerFunction( "replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING) );
-		registerFunction( "translate", new StandardSQLFunction("translate", StandardBasicTypes.STRING) );
-		registerFunction( "lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING) );
-		registerFunction( "rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING) );
-		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
-		registerFunction( "initcap", new StandardSQLFunction("initcap", StandardBasicTypes.STRING) );
-		registerFunction( "lower", new StandardSQLFunction("lower", StandardBasicTypes.STRING) );
-		registerFunction( "ltrim", new StandardSQLFunction("ltrim", StandardBasicTypes.STRING) );
-		registerFunction( "rtrim", new StandardSQLFunction("rtrim", StandardBasicTypes.STRING) );
-		registerFunction( "lfill", new StandardSQLFunction("ltrim", StandardBasicTypes.STRING) );
-		registerFunction( "rfill", new StandardSQLFunction("rtrim", StandardBasicTypes.STRING) );
-		registerFunction( "soundex", new StandardSQLFunction("soundex", StandardBasicTypes.STRING) );
-		registerFunction( "upper", new StandardSQLFunction("upper", StandardBasicTypes.STRING) );
-		registerFunction( "ascii", new StandardSQLFunction("ascii", StandardBasicTypes.STRING) );
-		registerFunction( "index", new StandardSQLFunction("index", StandardBasicTypes.INTEGER) );
+		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
+		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
+
+		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "log", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "power", new StandardSQLFunction( "power" ) );
+		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cosh", new StandardSQLFunction( "cosh", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cot", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "sinh", new StandardSQLFunction( "sinh", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "tanh", new StandardSQLFunction( "tanh", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "atan2", new StandardSQLFunction( "atan2", StandardBasicTypes.DOUBLE ) );
+
+		registerFunction( "round", new StandardSQLFunction( "round" ) );
+		registerFunction( "trunc", new StandardSQLFunction( "trunc" ) );
+		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
+		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
+		registerFunction( "greatest", new StandardSQLFunction( "greatest" ) );
+		registerFunction( "least", new StandardSQLFunction( "least" ) );
+
+		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
+		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "date", new StandardSQLFunction( "date", StandardBasicTypes.DATE ) );
+		registerFunction( "microsecond", new StandardSQLFunction( "microsecond", StandardBasicTypes.INTEGER ) );
+
+		registerFunction( "second", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "second(?1)" ) );
+		registerFunction( "minute", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "minute(?1)" ) );
+		registerFunction( "hour", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "hour(?1)" ) );
+		registerFunction( "day", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "day(?1)" ) );
+		registerFunction( "month", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "month(?1)" ) );
+		registerFunction( "year", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "year(?1)" ) );
+
+		registerFunction( "extract", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "?1(?3)" ) );
+
+		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
+		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
+		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
+		registerFunction( "weekofyear", new StandardSQLFunction( "weekofyear", StandardBasicTypes.INTEGER ) );
+
+		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
+		registerFunction( "translate", new StandardSQLFunction( "translate", StandardBasicTypes.STRING ) );
+		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
+		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
+		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
+		registerFunction( "initcap", new StandardSQLFunction( "initcap", StandardBasicTypes.STRING ) );
+		registerFunction( "lower", new StandardSQLFunction( "lower", StandardBasicTypes.STRING ) );
+		registerFunction( "ltrim", new StandardSQLFunction( "ltrim", StandardBasicTypes.STRING ) );
+		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
+		registerFunction( "lfill", new StandardSQLFunction( "ltrim", StandardBasicTypes.STRING ) );
+		registerFunction( "rfill", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
+		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
+		registerFunction( "upper", new StandardSQLFunction( "upper", StandardBasicTypes.STRING ) );
+		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.STRING ) );
+		registerFunction( "index", new StandardSQLFunction( "index", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "value", new StandardSQLFunction( "value" ) );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
-		registerFunction( "locate", new StandardSQLFunction("index", StandardBasicTypes.INTEGER) );
+		registerFunction( "locate", new StandardSQLFunction( "index", StandardBasicTypes.INTEGER ) );
 		registerFunction( "coalesce", new StandardSQLFunction( "value" ) );
 
-		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
+		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 
 	}
 
+	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
+	@Override
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
-			String[] primaryKey, boolean referencesPrimaryKey
-	) {
-		StringBuilder res = new StringBuilder(30)
-			.append(" foreign key ")
-			.append(constraintName)
-			.append(" (")
-			.append( StringHelper.join( ", ", foreignKey ) )
-			.append(") references ")
-			.append(referencedTable);
-
-		if(!referencesPrimaryKey) {
-			res.append(" (")
-			   .append( StringHelper.join(", ", primaryKey) )
-			   .append(')');
+			String[] primaryKey,
+			boolean referencesPrimaryKey) {
+		final StringBuilder res = new StringBuilder( 30 )
+				.append( " foreign key " )
+				.append( constraintName )
+				.append( " (" )
+				.append( StringHelper.join( ", ", foreignKey ) )
+				.append( ") references " )
+				.append( referencedTable );
+
+		if ( !referencesPrimaryKey ) {
+			res.append( " (" )
+					.append( StringHelper.join( ", ", primaryKey ) )
+					.append( ')' );
 		}
 
 		return res.toString();
 	}
 
+	@Override
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " primary key ";
 	}
 
+	@Override
 	public String getNullColumnString() {
 		return " null";
 	}
 
+	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dual";
 	}
 
+	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
+	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
+	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
+	@Override
 	public String getQuerySequencesString() {
 		return "select sequence_name from domain.sequences";
 	}
 
+	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
+	@Override
 	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
 
+	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
+	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "ignore rollback";
 	}
 
+	@Override
 	public String generateTemporaryTableName(String baseTableName) {
-		return "temp." + super.generateTemporaryTableName(baseTableName);
+		return "temp." + super.generateTemporaryTableName( baseTableName );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2005Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2005Dialect.java
index 342fbf8a77..1ce703ce5f 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2005Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2005Dialect.java
@@ -1,116 +1,122 @@
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
 package org.hibernate.dialect;
 
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryTimeoutException;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.SQLServer2005LimitHandler;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A dialect for Microsoft SQL 2005. (HHH-3936 fix)
  *
  * @author Yoryos Valotasios
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
+@SuppressWarnings("deprecation")
 public class SQLServer2005Dialect extends SQLServerDialect {
 	private static final int MAX_LENGTH = 8000;
 
+	/**
+	 * Constructs a SQLServer2005Dialect
+	 */
 	public SQLServer2005Dialect() {
 		// HHH-3965 fix
 		// As per http://www.sql-server-helper.com/faq/sql-server-2005-varchar-max-p01.aspx
 		// use varchar(max) and varbinary(max) instead of TEXT and IMAGE types
 		registerColumnType( Types.BLOB, "varbinary(MAX)" );
 		registerColumnType( Types.VARBINARY, "varbinary(MAX)" );
 		registerColumnType( Types.VARBINARY, MAX_LENGTH, "varbinary($l)" );
 		registerColumnType( Types.LONGVARBINARY, "varbinary(MAX)" );
 
 		registerColumnType( Types.CLOB, "varchar(MAX)" );
 		registerColumnType( Types.LONGVARCHAR, "varchar(MAX)" );
 		registerColumnType( Types.VARCHAR, "varchar(MAX)" );
 		registerColumnType( Types.VARCHAR, MAX_LENGTH, "varchar($l)" );
 
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.BIT, "bit" );
 
 
 		registerFunction( "row_number", new NoArgSQLFunction( "row_number", StandardBasicTypes.INTEGER, true ) );
 	}
 
 	@Override
 	public LimitHandler buildLimitHandler(String sql, RowSelection selection) {
 		return new SQLServer2005LimitHandler( sql, selection );
 	}
 
-	@Override // since SQLServer2005 the nowait hint is supported
+	@Override
 	public String appendLockHint(LockOptions lockOptions, String tableName) {
+		// NOTE : since SQLServer2005 the nowait hint is supported
 		if ( lockOptions.getLockMode() == LockMode.UPGRADE_NOWAIT ) {
 			return tableName + " with (updlock, rowlock, nowait)";
 		}
-		LockMode mode = lockOptions.getLockMode();
-		boolean isNoWait = lockOptions.getTimeOut() == LockOptions.NO_WAIT;
-		String noWaitStr = isNoWait? ", nowait" :"";
+
+		final LockMode mode = lockOptions.getLockMode();
+		final boolean isNoWait = lockOptions.getTimeOut() == LockOptions.NO_WAIT;
+		final String noWaitStr = isNoWait ? ", nowait" : "";
 		switch ( mode ) {
 			case UPGRADE_NOWAIT:
-				 return tableName + " with (updlock, rowlock, nowait)";
+				return tableName + " with (updlock, rowlock, nowait)";
 			case UPGRADE:
 			case PESSIMISTIC_WRITE:
 			case WRITE:
-				return tableName + " with (updlock, rowlock"+noWaitStr+" )";
+				return tableName + " with (updlock, rowlock" + noWaitStr + " )";
 			case PESSIMISTIC_READ:
-				return tableName + " with (holdlock, rowlock"+noWaitStr+" )";
+				return tableName + " with (holdlock, rowlock" + noWaitStr + " )";
 			default:
 				return tableName;
 		}
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				final String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 				if ( "HY008".equals( sqlState ) ) {
 					throw new QueryTimeoutException( message, sqlException, sql );
 				}
 				if (1222 == errorCode ) {
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				return null;
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2008Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2008Dialect.java
index 812d9eebc8..a76c782606 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2008Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2008Dialect.java
@@ -1,45 +1,49 @@
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
 package org.hibernate.dialect;
+
 import java.sql.Types;
 
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A dialect for Microsoft SQL Server 2008 with JDBC Driver 3.0 and above
  *
  * @author Gavin King
  */
 public class SQLServer2008Dialect extends SQLServer2005Dialect {
+	/**
+	 * Constructs a SQLServer2008Dialect
+	 */
 	public SQLServer2008Dialect() {
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "datetime2" );
 
 		registerFunction(
 				"current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP, false )
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
index 0ae137cb5f..c8dba6137c 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
@@ -1,205 +1,212 @@
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
 package org.hibernate.dialect;
+
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.function.AnsiTrimEmulationFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * A dialect for Microsoft SQL Server 2000
  *
  * @author Gavin King
  */
+@SuppressWarnings("deprecation")
 public class SQLServerDialect extends AbstractTransactSQLDialect {
-	
 	private static final int PARAM_LIST_SIZE_LIMIT = 2100;
 
+	/**
+	 * Constructs a SQLServerDialect
+	 */
 	public SQLServerDialect() {
 		registerColumnType( Types.VARBINARY, "image" );
 		registerColumnType( Types.VARBINARY, 8000, "varbinary($l)" );
 		registerColumnType( Types.LONGVARBINARY, "image" );
 		registerColumnType( Types.LONGVARCHAR, "text" );
 		registerColumnType( Types.BOOLEAN, "bit" );
 
 		registerFunction( "second", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(second, ?1)" ) );
 		registerFunction( "minute", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(minute, ?1)" ) );
 		registerFunction( "hour", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(hour, ?1)" ) );
 		registerFunction( "locate", new StandardSQLFunction( "charindex", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "extract", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(?1, ?3)" ) );
 		registerFunction( "mod", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "?1 % ?2" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datalength(?1) * 8" ) );
 
 		registerFunction( "trim", new AnsiTrimEmulationFunction() );
 
 		registerKeyword( "top" );
 	}
 
 	@Override
-    public String getNoColumnsInsertString() {
+	public String getNoColumnsInsertString() {
 		return "default values";
 	}
 
 	static int getAfterSelectInsertPoint(String sql) {
-		int selectIndex = sql.toLowerCase().indexOf( "select" );
+		final int selectIndex = sql.toLowerCase().indexOf( "select" );
 		final int selectDistinctIndex = sql.toLowerCase().indexOf( "select distinct" );
-		return selectIndex + ( selectDistinctIndex == selectIndex ? 15 : 6 );
+		return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
 	}
 
 	@Override
-    public String getLimitString(String querySelect, int offset, int limit) {
+	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( getAfterSelectInsertPoint( querySelect ), " top " + limit )
 				.toString();
 	}
 
 	/**
 	 * Use <tt>insert table(...) values(...) select SCOPE_IDENTITY()</tt>
+	 * <p/>
+	 * {@inheritDoc}
 	 */
 	@Override
-    public String appendIdentitySelectToInsert(String insertSQL) {
+	public String appendIdentitySelectToInsert(String insertSQL) {
 		return insertSQL + " select scope_identity()";
 	}
 
 	@Override
-    public boolean supportsLimit() {
+	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
-    public boolean useMaxForLimit() {
+	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
-    public boolean supportsLimitOffset() {
+	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
-    public boolean supportsVariableLimit() {
+	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
-    public char closeQuote() {
+	public char closeQuote() {
 		return ']';
 	}
 
 	@Override
-    public char openQuote() {
+	public char openQuote() {
 		return '[';
 	}
 
 	@Override
-    public String appendLockHint(LockOptions lockOptions, String tableName) {
-		LockMode mode = lockOptions.getLockMode();
+	public String appendLockHint(LockOptions lockOptions, String tableName) {
+		final LockMode mode = lockOptions.getLockMode();
 		switch ( mode ) {
 			case UPGRADE:
 			case UPGRADE_NOWAIT:
 			case PESSIMISTIC_WRITE:
 			case WRITE:
 				return tableName + " with (updlock, rowlock)";
 			case PESSIMISTIC_READ:
 				return tableName + " with (holdlock, rowlock)";
-            case UPGRADE_SKIPLOCKED:
-                return tableName + " with (updlock, rowlock, readpast)";
+			case UPGRADE_SKIPLOCKED:
+				return tableName + " with (updlock, rowlock, readpast)";
 			default:
 				return tableName;
 		}
 	}
 
-	// The current_timestamp is more accurate, but only known to be supported
-	// in SQL Server 7.0 and later (i.e., Sybase not known to support it at all)
+
+	/**
+	 * The current_timestamp is more accurate, but only known to be supported in SQL Server 7.0 and later and
+	 * Sybase not known to support it at all
+	 * <p/>
+	 * {@inheritDoc}
+	 */
 	@Override
-    public String getCurrentTimestampSelectString() {
+	public String getCurrentTimestampSelectString() {
 		return "select current_timestamp";
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
-    public boolean areStringComparisonsCaseInsensitive() {
+	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
 	@Override
-    public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
+	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return false;
 	}
 
 	@Override
-    public boolean supportsCircularCascadeDeleteConstraints() {
+	public boolean supportsCircularCascadeDeleteConstraints() {
 		// SQL Server (at least up through 2005) does not support defining
 		// cascade delete constraints which can circle back to the mutating
 		// table
 		return false;
 	}
 
 	@Override
-    public boolean supportsLobValueChangePropogation() {
+	public boolean supportsLobValueChangePropogation() {
 		// note: at least my local SQL Server 2005 Express shows this not working...
 		return false;
 	}
 
 	@Override
-    public boolean doesReadCommittedCauseWritersToBlockReaders() {
-		return false; // here assume SQLServer2005 using snapshot isolation, which does not have this problem
+	public boolean doesReadCommittedCauseWritersToBlockReaders() {
+		// here assume SQLServer2005 using snapshot isolation, which does not have this problem
+		return false;
 	}
 
 	@Override
-    public boolean doesRepeatableReadCauseReadersToBlockWriters() {
-		return false; // here assume SQLServer2005 using snapshot isolation, which does not have this problem
+	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
+		// here assume SQLServer2005 using snapshot isolation, which does not have this problem
+		return false;
 	}
 
-    /**
-     * {@inheritDoc}
-     *
-     * @see org.hibernate.dialect.Dialect#getSqlTypeDescriptorOverride(int)
-     */
-    @Override
-    protected SqlTypeDescriptor getSqlTypeDescriptorOverride( int sqlCode ) {
-        return sqlCode == Types.TINYINT ? SmallIntTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride(sqlCode);
-    }
+	@Override
+	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
+		return sqlCode == Types.TINYINT ?
+				SmallIntTypeDescriptor.INSTANCE :
+				super.getSqlTypeDescriptorOverride( sqlCode );
+	}
 
-	/* (non-Javadoc)
-		 * @see org.hibernate.dialect.Dialect#getInExpressionCountLimit()
-		 */
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Sybase11Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Sybase11Dialect.java
index bb94974c5a..c92c73e9e1 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Sybase11Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Sybase11Dialect.java
@@ -1,44 +1,51 @@
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
 package org.hibernate.dialect;
+
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.Sybase11JoinFragment;
 
 /**
  * A SQL dialect suitable for use with Sybase 11.9.2 (specifically: avoids ANSI JOIN syntax)
+ *
  * @author Colm O' Flaherty
  */
 public class Sybase11Dialect extends SybaseDialect  {
+	/**
+	 * Constructs a Sybase11Dialect
+	 */
 	public Sybase11Dialect() {
 		super();
 	}
 
+	@Override
 	public JoinFragment createOuterJoinFragment() {
 		return new Sybase11JoinFragment();
 	}
 
+	@Override
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE157Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE157Dialect.java
index 1096672ecd..9e164b9e05 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE157Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE157Dialect.java
@@ -1,123 +1,122 @@
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
 package org.hibernate.dialect;
 
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.hibernate.JDBCException;
-import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
-import org.hibernate.QueryTimeoutException;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.exception.ConstraintViolationException;
-import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
-import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
-import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect targeting Sybase Adaptive Server Enterprise (ASE) 15.7 and higher.
  * <p/>
  *
  * @author Junyan Ren
  */
 public class SybaseASE157Dialect extends SybaseASE15Dialect {
 
+	/**
+	 * Constructs a SybaseASE157Dialect
+	 */
 	public SybaseASE157Dialect() {
 		super();
 
 		registerFunction( "create_locator", new SQLFunctionTemplate( StandardBasicTypes.BINARY, "create_locator(?1, ?2)" ) );
 		registerFunction( "locator_literal", new SQLFunctionTemplate( StandardBasicTypes.BINARY, "locator_literal(?1, ?2)" ) );
 		registerFunction( "locator_valid", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "locator_valid(?1)" ) );
 		registerFunction( "return_lob", new SQLFunctionTemplate( StandardBasicTypes.BINARY, "return_lob(?1, ?2)" ) );
 		registerFunction( "setdata", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "setdata(?1, ?2, ?3)" ) );
 		registerFunction( "charindex", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "charindex(?1, ?2, ?3)" ) );
 	}
 
-	//HHH-7298 I don't know if this would break something or cause some side affects
-	//but it is required to use 'select for update'
 	@Override
 	public String getTableTypeString() {
+		//HHH-7298 I don't know if this would break something or cause some side affects
+		//but it is required to use 'select for update'
 		return " lock datarows";
 	}
 
-	// support Lob Locator
 	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
+
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
-	// support 'select ... for update [of columns]'
 	@Override
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
+
 	@Override
 	public String getForUpdateString() {
 		return " for update";
 	}
+
 	@Override
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
+
 	@Override
 	public String appendLockHint(LockOptions mode, String tableName) {
 		return tableName;
 	}
+
 	@Override
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
 		return sql + new ForUpdateFragment( this, aliasedLockOptions, keyColumnNames ).toFragmentString();
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				final String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 				if("JZ0TO".equals( sqlState ) || "JZ006".equals( sqlState )){
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				if ( 515 == errorCode && "ZZZZZ".equals( sqlState ) ) {
 					// Attempt to insert NULL value into column; column does not allow nulls.
 					final String constraintName = getViolatedConstraintNameExtracter().extractConstraintName( sqlException );
 					return new ConstraintViolationException( message, sqlException, sql, constraintName );
 				}
 				return null;
 			}
 		};
 	}
-
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE15Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE15Dialect.java
index 01f8d25776..5dc35c0055 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE15Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE15Dialect.java
@@ -1,438 +1,451 @@
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
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.dialect.function.AnsiTrimEmulationFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.TinyIntTypeDescriptor;
 
 /**
  * An SQL dialect targeting Sybase Adaptive Server Enterprise (ASE) 15 and higher.
  * <p/>
  * TODO : verify if this also works with 12/12.5
  *
  * @author Gavin King
  */
 public class SybaseASE15Dialect extends SybaseDialect {
+	/**
+	 * Constructs a SybaseASE15Dialect
+	 */
 	public SybaseASE15Dialect() {
 		super();
 
 		registerColumnType( Types.LONGVARBINARY, "image" );
 		registerColumnType( Types.LONGVARCHAR, "text" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "numeric($p,$s)" );
 		registerColumnType( Types.TIME, "time" );
-        registerColumnType( Types.REAL, "real" );
-        registerColumnType( Types.BOOLEAN, "tinyint" );
+		registerColumnType( Types.REAL, "real" );
+		registerColumnType( Types.BOOLEAN, "tinyint" );
 
 		registerFunction( "second", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(second, ?1)" ) );
 		registerFunction( "minute", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(minute, ?1)" ) );
 		registerFunction( "hour", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(hour, ?1)" ) );
 		registerFunction( "extract", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(?1, ?3)" ) );
 		registerFunction( "mod", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "?1 % ?2" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datalength(?1) * 8" ) );
 		registerFunction(
 				"trim", new AnsiTrimEmulationFunction(
 						AnsiTrimEmulationFunction.LTRIM, AnsiTrimEmulationFunction.RTRIM, "str_replace"
 				)
 		);
 
 		registerFunction( "atan2", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "atn2(?1, ?2" ) );
 		registerFunction( "atn2", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "atn2(?1, ?2" ) );
 
 		registerFunction( "biginttohex", new SQLFunctionTemplate( StandardBasicTypes.STRING, "biginttohext(?1)" ) );
 		registerFunction( "char_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "char_length(?1)" ) );
 		registerFunction( "charindex", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "charindex(?1, ?2)" ) );
 		registerFunction( "coalesce", new VarArgsSQLFunction( "coalesce(", ",", ")" ) );
 		registerFunction( "col_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "col_length(?1, ?2)" ) );
 		registerFunction( "col_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "col_name(?1, ?2)" ) );
 		// Sybase has created current_date and current_time inplace of getdate()
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE ) );
 
 
 		registerFunction( "data_pages", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "data_pages(?1, ?2)" ) );
 		registerFunction(
 				"data_pages", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "data_pages(?1, ?2, ?3)" )
 		);
 		registerFunction(
 				"data_pages", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "data_pages(?1, ?2, ?3, ?4)" )
 		);
 		registerFunction( "datalength", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datalength(?1)" ) );
 		registerFunction( "dateadd", new SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "dateadd" ) );
 		registerFunction( "datediff", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datediff" ) );
 		registerFunction( "datepart", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart" ) );
 		registerFunction( "datetime", new SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "datetime" ) );
 		registerFunction( "db_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "db_id(?1)" ) );
 		registerFunction( "difference", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "difference(?1,?2)" ) );
 		registerFunction( "db_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "db_name(?1)" ) );
 		registerFunction( "has_role", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "has_role(?1, ?2)" ) );
 		registerFunction( "hextobigint", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "hextobigint(?1)" ) );
 		registerFunction( "hextoint", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "hextoint(?1)" ) );
 		registerFunction( "host_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "host_id" ) );
 		registerFunction( "host_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "host_name" ) );
 		registerFunction( "inttohex", new SQLFunctionTemplate( StandardBasicTypes.STRING, "inttohex(?1)" ) );
 		registerFunction( "is_quiesced", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "is_quiesced(?1)" ) );
 		registerFunction(
 				"is_sec_service_on", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "is_sec_service_on(?1)" )
 		);
 		registerFunction( "object_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "object_id(?1)" ) );
 		registerFunction( "object_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "object_name(?1)" ) );
 		registerFunction( "pagesize", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "pagesize(?1)" ) );
 		registerFunction( "pagesize", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "pagesize(?1, ?2)" ) );
 		registerFunction( "pagesize", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "pagesize(?1, ?2, ?3)" ) );
 		registerFunction(
 				"partition_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "partition_id(?1, ?2)" )
 		);
 		registerFunction(
 				"partition_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "partition_id(?1, ?2, ?3)" )
 		);
 		registerFunction(
 				"partition_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "partition_name(?1, ?2)" )
 		);
 		registerFunction(
 				"partition_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "partition_name(?1, ?2, ?3)" )
 		);
 		registerFunction( "patindex", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "patindex" ) );
 		registerFunction( "proc_role", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "proc_role" ) );
 		registerFunction( "role_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "role_name" ) );
 		// check return type
 		registerFunction( "row_count", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "row_count" ) );
 		registerFunction( "rand2", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "rand2(?1)" ) );
 		registerFunction( "rand2", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "rand2" ) );
 		registerFunction( "replicate", new SQLFunctionTemplate( StandardBasicTypes.STRING, "replicate(?1,?2)" ) );
 		registerFunction( "role_contain", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "role_contain" ) );
 		registerFunction( "role_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "role_id" ) );
 		registerFunction( "reserved_pages", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "reserved_pages" ) );
 		registerFunction( "right", new SQLFunctionTemplate( StandardBasicTypes.STRING, "right" ) );
 		registerFunction( "show_role", new SQLFunctionTemplate( StandardBasicTypes.STRING, "show_role" ) );
 		registerFunction(
 				"show_sec_services", new SQLFunctionTemplate( StandardBasicTypes.STRING, "show_sec_services" )
 		);
 		registerFunction( "sortkey", new VarArgsSQLFunction( StandardBasicTypes.BINARY, "sortkey(", ",", ")" ) );
 		registerFunction( "soundex", new SQLFunctionTemplate( StandardBasicTypes.STRING, "sounded" ) );
 		registerFunction( "stddev", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "stddev" ) );
 		registerFunction( "stddev_pop", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "stddev_pop" ) );
 		registerFunction( "stddev_samp", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "stddev_samp" ) );
 		registerFunction( "stuff", new SQLFunctionTemplate( StandardBasicTypes.STRING, "stuff" ) );
 		registerFunction( "substring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substring(", ",", ")" ) );
 		registerFunction( "suser_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "suser_id" ) );
 		registerFunction( "suser_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "suser_name" ) );
 		registerFunction( "tempdb_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "tempdb_id" ) );
 		registerFunction( "textvalid", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "textvalid" ) );
 		registerFunction( "to_unichar", new SQLFunctionTemplate( StandardBasicTypes.STRING, "to_unichar(?1)" ) );
 		registerFunction(
 				"tran_dumptable_status",
 				new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "ran_dumptable_status(?1)" )
 		);
 		registerFunction( "uhighsurr", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "uhighsurr" ) );
 		registerFunction( "ulowsurr", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "ulowsurr" ) );
 		registerFunction( "uscalar", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "uscalar" ) );
 		registerFunction( "used_pages", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "used_pages" ) );
 		registerFunction( "user_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "user_id" ) );
 		registerFunction( "user_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "user_name" ) );
 		registerFunction( "valid_name", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "valid_name" ) );
 		registerFunction( "valid_user", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "valid_user" ) );
 		registerFunction( "variance", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "variance" ) );
 		registerFunction( "var_pop", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "var_pop" ) );
 		registerFunction( "var_samp", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "var_samp" ) );
-        registerFunction( "sysdate", new NoArgSQLFunction("getdate", StandardBasicTypes.TIMESTAMP) );
+		registerFunction( "sysdate", new NoArgSQLFunction("getdate", StandardBasicTypes.TIMESTAMP) );
 
 		registerSybaseKeywords();
 	}
 
 	private void registerSybaseKeywords() {
 		registerKeyword( "add" );
 		registerKeyword( "all" );
 		registerKeyword( "alter" );
 		registerKeyword( "and" );
 		registerKeyword( "any" );
 		registerKeyword( "arith_overflow" );
 		registerKeyword( "as" );
 		registerKeyword( "asc" );
 		registerKeyword( "at" );
 		registerKeyword( "authorization" );
 		registerKeyword( "avg" );
 		registerKeyword( "begin" );
 		registerKeyword( "between" );
 		registerKeyword( "break" );
 		registerKeyword( "browse" );
 		registerKeyword( "bulk" );
 		registerKeyword( "by" );
 		registerKeyword( "cascade" );
 		registerKeyword( "case" );
 		registerKeyword( "char_convert" );
 		registerKeyword( "check" );
 		registerKeyword( "checkpoint" );
 		registerKeyword( "close" );
 		registerKeyword( "clustered" );
 		registerKeyword( "coalesce" );
 		registerKeyword( "commit" );
 		registerKeyword( "compute" );
 		registerKeyword( "confirm" );
 		registerKeyword( "connect" );
 		registerKeyword( "constraint" );
 		registerKeyword( "continue" );
 		registerKeyword( "controlrow" );
 		registerKeyword( "convert" );
 		registerKeyword( "count" );
 		registerKeyword( "count_big" );
 		registerKeyword( "create" );
 		registerKeyword( "current" );
 		registerKeyword( "cursor" );
 		registerKeyword( "database" );
 		registerKeyword( "dbcc" );
 		registerKeyword( "deallocate" );
 		registerKeyword( "declare" );
 		registerKeyword( "decrypt" );
 		registerKeyword( "default" );
 		registerKeyword( "delete" );
 		registerKeyword( "desc" );
 		registerKeyword( "determnistic" );
 		registerKeyword( "disk" );
 		registerKeyword( "distinct" );
 		registerKeyword( "drop" );
 		registerKeyword( "dummy" );
 		registerKeyword( "dump" );
 		registerKeyword( "else" );
 		registerKeyword( "encrypt" );
 		registerKeyword( "end" );
 		registerKeyword( "endtran" );
 		registerKeyword( "errlvl" );
 		registerKeyword( "errordata" );
 		registerKeyword( "errorexit" );
 		registerKeyword( "escape" );
 		registerKeyword( "except" );
 		registerKeyword( "exclusive" );
 		registerKeyword( "exec" );
 		registerKeyword( "execute" );
 		registerKeyword( "exist" );
 		registerKeyword( "exit" );
 		registerKeyword( "exp_row_size" );
 		registerKeyword( "external" );
 		registerKeyword( "fetch" );
 		registerKeyword( "fillfactor" );
 		registerKeyword( "for" );
 		registerKeyword( "foreign" );
 		registerKeyword( "from" );
 		registerKeyword( "goto" );
 		registerKeyword( "grant" );
 		registerKeyword( "group" );
 		registerKeyword( "having" );
 		registerKeyword( "holdlock" );
 		registerKeyword( "identity" );
 		registerKeyword( "identity_gap" );
 		registerKeyword( "identity_start" );
 		registerKeyword( "if" );
 		registerKeyword( "in" );
 		registerKeyword( "index" );
 		registerKeyword( "inout" );
 		registerKeyword( "insensitive" );
 		registerKeyword( "insert" );
 		registerKeyword( "install" );
 		registerKeyword( "intersect" );
 		registerKeyword( "into" );
 		registerKeyword( "is" );
 		registerKeyword( "isolation" );
 		registerKeyword( "jar" );
 		registerKeyword( "join" );
 		registerKeyword( "key" );
 		registerKeyword( "kill" );
 		registerKeyword( "level" );
 		registerKeyword( "like" );
 		registerKeyword( "lineno" );
 		registerKeyword( "load" );
 		registerKeyword( "lock" );
 		registerKeyword( "materialized" );
 		registerKeyword( "max" );
 		registerKeyword( "max_rows_per_page" );
 		registerKeyword( "min" );
 		registerKeyword( "mirror" );
 		registerKeyword( "mirrorexit" );
 		registerKeyword( "modify" );
 		registerKeyword( "national" );
 		registerKeyword( "new" );
 		registerKeyword( "noholdlock" );
 		registerKeyword( "nonclustered" );
 		registerKeyword( "nonscrollable" );
 		registerKeyword( "non_sensitive" );
 		registerKeyword( "not" );
 		registerKeyword( "null" );
 		registerKeyword( "nullif" );
 		registerKeyword( "numeric_truncation" );
 		registerKeyword( "of" );
 		registerKeyword( "off" );
 		registerKeyword( "offsets" );
 		registerKeyword( "on" );
 		registerKeyword( "once" );
 		registerKeyword( "online" );
 		registerKeyword( "only" );
 		registerKeyword( "open" );
 		registerKeyword( "option" );
 		registerKeyword( "or" );
 		registerKeyword( "order" );
 		registerKeyword( "out" );
 		registerKeyword( "output" );
 		registerKeyword( "over" );
 		registerKeyword( "artition" );
 		registerKeyword( "perm" );
 		registerKeyword( "permanent" );
 		registerKeyword( "plan" );
 		registerKeyword( "prepare" );
 		registerKeyword( "primary" );
 		registerKeyword( "print" );
 		registerKeyword( "privileges" );
 		registerKeyword( "proc" );
 		registerKeyword( "procedure" );
 		registerKeyword( "processexit" );
 		registerKeyword( "proxy_table" );
 		registerKeyword( "public" );
 		registerKeyword( "quiesce" );
 		registerKeyword( "raiserror" );
 		registerKeyword( "read" );
 		registerKeyword( "readpast" );
 		registerKeyword( "readtext" );
 		registerKeyword( "reconfigure" );
 		registerKeyword( "references" );
 		registerKeyword( "remove" );
 		registerKeyword( "reorg" );
 		registerKeyword( "replace" );
 		registerKeyword( "replication" );
 		registerKeyword( "reservepagegap" );
 		registerKeyword( "return" );
 		registerKeyword( "returns" );
 		registerKeyword( "revoke" );
 		registerKeyword( "role" );
 		registerKeyword( "rollback" );
 		registerKeyword( "rowcount" );
 		registerKeyword( "rows" );
 		registerKeyword( "rule" );
 		registerKeyword( "save" );
 		registerKeyword( "schema" );
 		registerKeyword( "scroll" );
 		registerKeyword( "scrollable" );
 		registerKeyword( "select" );
 		registerKeyword( "semi_sensitive" );
 		registerKeyword( "set" );
 		registerKeyword( "setuser" );
 		registerKeyword( "shared" );
 		registerKeyword( "shutdown" );
 		registerKeyword( "some" );
 		registerKeyword( "statistics" );
 		registerKeyword( "stringsize" );
 		registerKeyword( "stripe" );
 		registerKeyword( "sum" );
 		registerKeyword( "syb_identity" );
 		registerKeyword( "syb_restree" );
 		registerKeyword( "syb_terminate" );
 		registerKeyword( "top" );
 		registerKeyword( "table" );
 		registerKeyword( "temp" );
 		registerKeyword( "temporary" );
 		registerKeyword( "textsize" );
 		registerKeyword( "to" );
 		registerKeyword( "tracefile" );
 		registerKeyword( "tran" );
 		registerKeyword( "transaction" );
 		registerKeyword( "trigger" );
 		registerKeyword( "truncate" );
 		registerKeyword( "tsequal" );
 		registerKeyword( "union" );
 		registerKeyword( "unique" );
 		registerKeyword( "unpartition" );
 		registerKeyword( "update" );
 		registerKeyword( "use" );
 		registerKeyword( "user" );
 		registerKeyword( "user_option" );
 		registerKeyword( "using" );
 		registerKeyword( "values" );
 		registerKeyword( "varying" );
 		registerKeyword( "view" );
 		registerKeyword( "waitfor" );
 		registerKeyword( "when" );
 		registerKeyword( "where" );
 		registerKeyword( "while" );
 		registerKeyword( "with" );
 		registerKeyword( "work" );
 		registerKeyword( "writetext" );
 		registerKeyword( "xmlextract" );
 		registerKeyword( "xmlparse" );
 		registerKeyword( "xmltest" );
 		registerKeyword( "xmlvalidate" );
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public boolean supportsCascadeDelete() {
 		return false;
 	}
 
+	@Override
 	public int getMaxAliasLength() {
 		return 30;
 	}
 
 	/**
 	 * By default, Sybase string comparisons are case-insensitive.
 	 * <p/>
 	 * If the DB is configured to be case-sensitive, then this return
 	 * value will be incorrect.
+	 * <p/>
+	 * {@inheritDoc}
 	 */
+	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
+	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		return "getdate()";
 	}
 
 	/**
 	 * Actually Sybase does not support LOB locators at al.
 	 *
 	 * @return false.
+	 * <p/>
+	 * {@inheritDoc}
 	 */
+	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		return false;
 	}
 
+	@Override
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
-    @Override
-    protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
-        return sqlCode == Types.BOOLEAN ? TinyIntTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride( sqlCode );
-    }
+	@Override
+	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
+		return sqlCode == Types.BOOLEAN ? TinyIntTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride( sqlCode );
+	}
 
 	@Override
 	public boolean supportsLockTimeouts() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseAnywhereDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseAnywhereDialect.java
index 688c917c06..26dc33de45 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseAnywhereDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseAnywhereDialect.java
@@ -1,56 +1,62 @@
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
 package org.hibernate.dialect;
 
 
 /**
  * SQL Dialect for Sybase Anywhere
  * extending Sybase (Enterprise) Dialect
  * (Tested on ASA 8.x)
- * @author ?
  */
 public class SybaseAnywhereDialect extends SybaseDialect {
 	/**
 	 * Sybase Anywhere syntax would require a "DEFAULT" for each column specified,
 	 * but I suppose Hibernate use this syntax only with tables with just 1 column
+	 * <p/>
+	 * {@inheritDoc}
 	 */
+	@Override
 	public String getNoColumnsInsertString() {
 		return "values (default)";
 	}
 
 	/**
 	 * ASA does not require to drop constraint before dropping tables, so disable it.
 	 * <p/>
 	 * NOTE : Also, the DROP statement syntax used by Hibernate to drop constraints is 
 	 * not compatible with ASA.
+	 * <p/>
+	 * {@inheritDoc}
 	 */
+	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsInsertSelectIdentity() {
 		return false;
 	}
 	
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseDialect.java
index 90f1c35d20..5f7a3ef65d 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseDialect.java
@@ -1,67 +1,63 @@
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
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 
 /**
  * All Sybase dialects share an IN list size limit.
  *
  * @author Brett Meyer
  */
 public class SybaseDialect extends AbstractTransactSQLDialect {
-	
 	private static final int PARAM_LIST_SIZE_LIMIT = 250000;
 
-	/* (non-Javadoc)
-		 * @see org.hibernate.dialect.Dialect#getInExpressionCountLimit()
-		 */
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
 	
 	@Override
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		switch (sqlCode) {
 		case Types.BLOB:
 			return BlobTypeDescriptor.PRIMITIVE_ARRAY_BINDING;
 		case Types.CLOB:
 			// Some Sybase drivers cannot support getClob.  See HHH-7889
 			return ClobTypeDescriptor.STREAM_BINDING_EXTRACTING;
 		default:
 			return super.getSqlTypeDescriptorOverride( sqlCode );
 		}
 	}
 	
 	@Override
 	public String getNullColumnString() {
 		return " null";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java
index 942057aa64..5c01957402 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java
@@ -1,269 +1,273 @@
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
 package org.hibernate.dialect;
 import java.sql.Types;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A dialect for the Teradata database created by MCR as part of the
  * dialect certification process.
  *
  * @author Jay Nance
  */
 public class TeradataDialect extends Dialect {
-	
 	private static final int PARAM_LIST_SIZE_LIMIT = 1024;
 
 	/**
 	 * Constructor
 	 */
 	public TeradataDialect() {
 		super();
 		//registerColumnType data types
 		registerColumnType( Types.NUMERIC, "NUMERIC($p,$s)" );
 		registerColumnType( Types.DOUBLE, "DOUBLE PRECISION" );
 		registerColumnType( Types.BIGINT, "NUMERIC(18,0)" );
 		registerColumnType( Types.BIT, "BYTEINT" );
 		registerColumnType( Types.TINYINT, "BYTEINT" );
 		registerColumnType( Types.VARBINARY, "VARBYTE($l)" );
 		registerColumnType( Types.BINARY, "BYTEINT" );
 		registerColumnType( Types.LONGVARCHAR, "LONG VARCHAR" );
 		registerColumnType( Types.CHAR, "CHAR(1)" );
 		registerColumnType( Types.DECIMAL, "DECIMAL" );
 		registerColumnType( Types.INTEGER, "INTEGER" );
 		registerColumnType( Types.SMALLINT, "SMALLINT" );
 		registerColumnType( Types.FLOAT, "FLOAT" );
 		registerColumnType( Types.VARCHAR, "VARCHAR($l)" );
 		registerColumnType( Types.DATE, "DATE" );
 		registerColumnType( Types.TIME, "TIME" );
 		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
-		registerColumnType( Types.BOOLEAN, "BYTEINT" );  // hibernate seems to ignore this type...
+		// hibernate seems to ignore this type...
+		registerColumnType( Types.BOOLEAN, "BYTEINT" );
 		registerColumnType( Types.BLOB, "BLOB" );
 		registerColumnType( Types.CLOB, "CLOB" );
 
 		registerFunction( "year", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "extract(year from ?1)" ) );
 		registerFunction( "length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "character_length(?1)" ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1 from ?2 for ?3)" ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.STRING, "position(?1 in ?2)" ) );
 		registerFunction( "mod", new SQLFunctionTemplate( StandardBasicTypes.STRING, "?1 mod ?2" ) );
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "cast(?1 as varchar(255))" ) );
 
 		// bit_length feels a bit broken to me. We have to cast to char in order to
 		// pass when a numeric value is supplied. But of course the answers given will
 		// be wildly different for these two datatypes. 1234.5678 will be 9 bytes as
 		// a char string but will be 8 or 16 bytes as a true numeric.
 		// Jay Nance 2006-09-22
 		registerFunction(
 				"bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "octet_length(cast(?1 as char))*4" )
 		);
 
 		// The preference here would be
 		//   SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "current_timestamp(?1)", false)
 		// but this appears not to work.
 		// Jay Nance 2006-09-22
 		registerFunction( "current_timestamp", new SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "current_timestamp" ) );
 		registerFunction( "current_time", new SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "current_time" ) );
 		registerFunction( "current_date", new SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "current_date" ) );
 		// IBID for current_time and current_date
 
 		registerKeyword( "password" );
 		registerKeyword( "type" );
 		registerKeyword( "title" );
 		registerKeyword( "year" );
 		registerKeyword( "month" );
 		registerKeyword( "summary" );
 		registerKeyword( "alias" );
 		registerKeyword( "value" );
 		registerKeyword( "first" );
 		registerKeyword( "role" );
 		registerKeyword( "account" );
 		registerKeyword( "class" );
 
 		// Tell hibernate to use getBytes instead of getBinaryStream
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "false" );
 		// No batch statements
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, NO_BATCH );
 	}
 
 	/**
-	 * Does this dialect support the <tt>FOR UPDATE</tt> syntax?
-	 *
-	 * @return empty string ... Teradata does not support <tt>FOR UPDATE<tt> syntax
+	 * Teradata does not support <tt>FOR UPDATE</tt> syntax
+	 * <p/>
+	 * {@inheritDoc}
 	 */
+	@Override
 	public String getForUpdateString() {
 		return "";
 	}
 
+	@Override
 	public boolean supportsIdentityColumns() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsSequences() {
 		return false;
 	}
 
+	@Override
 	public String getAddColumnString() {
 		return "Add Column";
 	}
 
+	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
+	@Override
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
+	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return " on commit preserve rows";
 	}
 
+	@Override
 	public Boolean performTemporaryTableDDLInIsolation() {
 		return Boolean.TRUE;
 	}
 
+	@Override
 	public boolean dropTemporaryTableAfterUse() {
 		return false;
 	}
 
-	/**
-	 * Get the name of the database type associated with the given
-	 * <tt>java.sql.Types</tt> typecode.
-	 *
-	 * @param code <tt>java.sql.Types</tt> typecode
-	 * @param length the length or precision of the column
-	 * @param precision the precision of the column
-	 * @param scale the scale of the column
-	 *
-	 * @return the database type name
-	 *
-	 * @throws HibernateException
-	 */
-	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException {
-		/*
-		 * We might want a special case for 19,2. This is very common for money types
-		 * and here it is converted to 18,1
-		 */
-		float f = precision > 0 ? ( float ) scale / ( float ) precision : 0;
-		int p = ( precision > 18 ? 18 : precision );
-		int s = ( precision > 18 ? ( int ) ( 18.0 * f ) : ( scale > 18 ? 18 : scale ) );
+	@Override
+	public String getTypeName(int code, long length, int precision, int scale) throws HibernateException {
+		// We might want a special case for 19,2. This is very common for money types
+		// and here it is converted to 18,1
+		final float f = precision > 0 ? (float) scale / (float) precision : 0;
+		final int p = ( precision > 18 ? 18 : precision );
+		final int s = ( precision > 18 ? (int) ( 18.0 * f ) : ( scale > 18 ? 18 : scale ) );
 
 		return super.getTypeName( code, length, p, s );
 	}
 
+	@Override
 	public boolean supportsCascadeDelete() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		return false;
 	}
 
+	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
+	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		String v = "null";
 
 		switch ( sqlType ) {
 			case Types.BIT:
 			case Types.TINYINT:
 			case Types.SMALLINT:
 			case Types.INTEGER:
 			case Types.BIGINT:
 			case Types.FLOAT:
 			case Types.REAL:
 			case Types.DOUBLE:
 			case Types.NUMERIC:
 			case Types.DECIMAL:
 				v = "cast(null as decimal)";
 				break;
 			case Types.CHAR:
 			case Types.VARCHAR:
 			case Types.LONGVARCHAR:
 				v = "cast(null as varchar(255))";
 				break;
 			case Types.DATE:
 			case Types.TIME:
 			case Types.TIMESTAMP:
 				v = "cast(null as timestamp)";
 				break;
 			case Types.BINARY:
 			case Types.VARBINARY:
 			case Types.LONGVARBINARY:
 			case Types.NULL:
 			case Types.OTHER:
 			case Types.JAVA_OBJECT:
 			case Types.DISTINCT:
 			case Types.STRUCT:
 			case Types.ARRAY:
 			case Types.BLOB:
 			case Types.CLOB:
 			case Types.REF:
 			case Types.DATALINK:
 			case Types.BOOLEAN:
 				break;
+			default:
+				break;
 		}
 		return v;
 	}
 
+	@Override
 	public String getCreateMultisetTableString() {
 		return "create multiset table ";
 	}
 
+	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
+	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return true;
 	}
 
+	@Override
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return true;
 	}
 
+	@Override
 	public boolean supportsBindAsCallableArgument() {
 		return false;
 	}
 
-	/* (non-Javadoc)
-		 * @see org.hibernate.dialect.Dialect#getInExpressionCountLimit()
-		 */
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
index f76e3099a1..bbd6545dab 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
@@ -1,244 +1,270 @@
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
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.OracleJoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A SQL dialect for TimesTen 5.1.
- * 
+ * <p/>
  * Known limitations:
  * joined-subclass support because of no CASE support in TimesTen
  * No support for subqueries that includes aggregation
- *  - size() in HQL not supported
- *  - user queries that does subqueries with aggregation
- * No CLOB/BLOB support 
+ * - size() in HQL not supported
+ * - user queries that does subqueries with aggregation
+ * No CLOB/BLOB support
  * No cascade delete support.
  * No Calendar support
  * No support for updating primary keys.
- * 
+ *
  * @author Sherry Listgarten and Max Andersen
  */
+@SuppressWarnings("deprecation")
 public class TimesTenDialect extends Dialect {
-	
+	/**
+	 * Constructs a TimesTenDialect
+	 */
 	public TimesTenDialect() {
 		super();
 		registerColumnType( Types.BIT, "TINYINT" );
 		registerColumnType( Types.BIGINT, "BIGINT" );
 		registerColumnType( Types.SMALLINT, "SMALLINT" );
 		registerColumnType( Types.TINYINT, "TINYINT" );
 		registerColumnType( Types.INTEGER, "INTEGER" );
 		registerColumnType( Types.CHAR, "CHAR(1)" );
 		registerColumnType( Types.VARCHAR, "VARCHAR($l)" );
 		registerColumnType( Types.FLOAT, "FLOAT" );
 		registerColumnType( Types.DOUBLE, "DOUBLE" );
 		registerColumnType( Types.DATE, "DATE" );
 		registerColumnType( Types.TIME, "TIME" );
 		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
 		registerColumnType( Types.VARBINARY, "VARBINARY($l)" );
 		registerColumnType( Types.NUMERIC, "DECIMAL($p, $s)" );
 		// TimesTen has no BLOB/CLOB support, but these types may be suitable 
 		// for some applications. The length is limited to 4 million bytes.
-        registerColumnType( Types.BLOB, "VARBINARY(4000000)" ); 
-        registerColumnType( Types.CLOB, "VARCHAR(4000000)" );
-	
-		getDefaultProperties().setProperty(Environment.USE_STREAMS_FOR_BINARY, "true");
-		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
-		registerFunction( "lower", new StandardSQLFunction("lower") );
-		registerFunction( "upper", new StandardSQLFunction("upper") );
-		registerFunction( "rtrim", new StandardSQLFunction("rtrim") );
-		registerFunction( "concat", new StandardSQLFunction("concat", StandardBasicTypes.STRING) );
-		registerFunction( "mod", new StandardSQLFunction("mod") );
-		registerFunction( "to_char", new StandardSQLFunction("to_char",StandardBasicTypes.STRING) );
-		registerFunction( "to_date", new StandardSQLFunction("to_date",StandardBasicTypes.TIMESTAMP) );
-		registerFunction( "sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.TIMESTAMP, false) );
-		registerFunction( "getdate", new NoArgSQLFunction("getdate", StandardBasicTypes.TIMESTAMP, false) );
-		registerFunction( "nvl", new StandardSQLFunction("nvl") );
-
-	}
-	
+		registerColumnType( Types.BLOB, "VARBINARY(4000000)" );
+		registerColumnType( Types.CLOB, "VARCHAR(4000000)" );
+
+		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
+		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
+		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
+		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
+		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
+		registerFunction( "concat", new StandardSQLFunction( "concat", StandardBasicTypes.STRING ) );
+		registerFunction( "mod", new StandardSQLFunction( "mod" ) );
+		registerFunction( "to_char", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
+		registerFunction( "to_date", new StandardSQLFunction( "to_date", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
+		registerFunction( "getdate", new NoArgSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP, false ) );
+		registerFunction( "nvl", new StandardSQLFunction( "nvl" ) );
+
+	}
+
+	@Override
 	public boolean dropConstraints() {
-            return true;
+		return true;
 	}
-	
+
+	@Override
 	public boolean qualifyIndexName() {
-            return false;
+		return false;
 	}
-	
-    public String getAddColumnString() {
-            return "add";
+
+	@Override
+	public String getAddColumnString() {
+		return "add";
 	}
 
+	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
+	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
+	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select first 1 " + sequenceName + ".nextval from sys.tables";
 	}
 
+	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
+	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
+	@Override
 	public String getQuerySequencesString() {
 		return "select NAME from sys.sequences";
 	}
 
+	@Override
 	public JoinFragment createOuterJoinFragment() {
 		return new OracleJoinFragment();
 	}
 
+	@Override
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
-	// new methods in dialect3
-	/*public boolean supportsForUpdateNowait() {
-		return false;
-	}*/
-	
+	@Override
 	public String getForUpdateString() {
 		return "";
 	}
-	
+
+	@Override
 	public boolean supportsColumnCheck() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsTableCheck() {
 		return false;
 	}
-	
+
+	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
+	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
+	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( 6, " first " + limit )
 				.toString();
 	}
 
+	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
+	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select first 1 sysdate from sys.tables";
 	}
 
+	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
+	@Override
 	public String generateTemporaryTableName(String baseTableName) {
-		String name = super.generateTemporaryTableName(baseTableName);
+		final String name = super.generateTemporaryTableName( baseTableName );
 		return name.length() > 30 ? name.substring( 1, 30 ) : name;
 	}
 
+	@Override
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
+	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit delete rows";
 	}
 
+	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// TimesTen has no known variation of a "SELECT ... FOR UPDATE" syntax...
-		if ( lockMode==LockMode.PESSIMISTIC_FORCE_INCREMENT) {
-			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode);
+		if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT ) {
+			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
-		else if ( lockMode==LockMode.PESSIMISTIC_WRITE) {
-			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode);
+		else if ( lockMode == LockMode.PESSIMISTIC_WRITE ) {
+			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode );
 		}
-		else if ( lockMode==LockMode.PESSIMISTIC_READ) {
-			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode);
+		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
+			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode );
 		}
-		else if ( lockMode==LockMode.OPTIMISTIC) {
-			return new OptimisticLockingStrategy( lockable, lockMode);
+		else if ( lockMode == LockMode.OPTIMISTIC ) {
+			return new OptimisticLockingStrategy( lockable, lockMode );
 		}
-		else if ( lockMode==LockMode.OPTIMISTIC_FORCE_INCREMENT) {
-			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode);
+		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT ) {
+			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/TypeNames.java b/hibernate-core/src/main/java/org/hibernate/dialect/TypeNames.java
index 8f1584f257..0bf0ab95bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/TypeNames.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/TypeNames.java
@@ -1,134 +1,159 @@
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
 package org.hibernate.dialect;
+
 import java.util.HashMap;
 import java.util.Map;
 import java.util.TreeMap;
 
 import org.hibernate.MappingException;
 import org.hibernate.internal.util.StringHelper;
 
 /**
- * This class maps a type to names. Associations
- * may be marked with a capacity. Calling the get()
- * method with a type and actual size n will return
- * the associated name with smallest capacity >= n,
+ * This class maps a type to names.  Associations may be marked with a capacity. Calling the get()
+ * method with a type and actual size n will return  the associated name with smallest capacity >= n,
  * if available and an unmarked default type otherwise.
  * Eg, setting
  * <pre>
- *	names.put(type,        "TEXT" );
- *	names.put(type,   255, "VARCHAR($l)" );
- *	names.put(type, 65534, "LONGVARCHAR($l)" );
+ *	names.put( type,        "TEXT" );
+ *	names.put( type,   255, "VARCHAR($l)" );
+ *	names.put( type, 65534, "LONGVARCHAR($l)" );
  * </pre>
  * will give you back the following:
  * <pre>
- *  names.get(type)         // --> "TEXT" (default)
- *  names.get(type,    100) // --> "VARCHAR(100)" (100 is in [0:255])
- *  names.get(type,   1000) // --> "LONGVARCHAR(1000)" (1000 is in [256:65534])
- *  names.get(type, 100000) // --> "TEXT" (default)
+ *  names.get( type )         // --> "TEXT" (default)
+ *  names.get( type,    100 ) // --> "VARCHAR(100)" (100 is in [0:255])
+ *  names.get( type,   1000 ) // --> "LONGVARCHAR(1000)" (1000 is in [256:65534])
+ *  names.get( type, 100000 ) // --> "TEXT" (default)
  * </pre>
  * On the other hand, simply putting
  * <pre>
- *	names.put(type, "VARCHAR($l)" );
+ *	names.put( type, "VARCHAR($l)" );
  * </pre>
  * would result in
  * <pre>
- *  names.get(type)        // --> "VARCHAR($l)" (will cause trouble)
- *  names.get(type, 100)   // --> "VARCHAR(100)"
- *  names.get(type, 10000) // --> "VARCHAR(10000)"
+ *  names.get( type )        // --> "VARCHAR($l)" (will cause trouble)
+ *  names.get( type, 100 )   // --> "VARCHAR(100)"
+ *  names.get( type, 10000 ) // --> "VARCHAR(10000)"
  * </pre>
  *
  * @author Christoph Beck
  */
 public class TypeNames {
+	/**
+	 * Holds default type mappings for a typeCode.  This is the non-sized mapping
+	 */
+	private Map<Integer, String> defaults = new HashMap<Integer, String>();
 
+	/**
+	 * Holds the weighted mappings for a typeCode.  The nested map is a TreeMap to sort its contents
+	 * based on the key (the weighting) to ensure proper iteration ordering during {@link #get(int, long, int, int)}
+	 */
 	private Map<Integer, Map<Long, String>> weighted = new HashMap<Integer, Map<Long, String>>();
-	private Map<Integer, String> defaults = new HashMap<Integer, String>();
 
 	/**
 	 * get default type name for specified type
-	 * @param typecode the type key
+	 *
+	 * @param typeCode the type key
+	 *
 	 * @return the default type name associated with specified key
+	 *
+	 * @throws MappingException Indicates that no registrations were made for that typeCode
 	 */
-	public String get(int typecode) throws MappingException {
-		String result = defaults.get( typecode );
-		if (result==null) throw new MappingException("No Dialect mapping for JDBC type: " + typecode);
+	public String get(int typeCode) throws MappingException {
+		final String result = defaults.get( typeCode );
+		if ( result == null ) {
+			throw new MappingException( "No Dialect mapping for JDBC type: " + typeCode );
+		}
 		return result;
 	}
 
 	/**
 	 * get type name for specified type and size
+	 *
 	 * @param typeCode the type key
 	 * @param size the SQL length
 	 * @param scale the SQL scale
 	 * @param precision the SQL precision
-	 * @return the associated name with smallest capacity >= size,
-	 * if available and the default type name otherwise
+	 *
+	 * @return the associated name with smallest capacity >= size, if available and the default type name otherwise
+	 *
+	 * @throws MappingException Indicates that no registrations were made for that typeCode
 	 */
 	public String get(int typeCode, long size, int precision, int scale) throws MappingException {
-		Map<Long, String> map = weighted.get( typeCode );
-		if ( map!=null && map.size()>0 ) {
+		final Map<Long, String> map = weighted.get( typeCode );
+		if ( map != null && map.size() > 0 ) {
 			// iterate entries ordered by capacity to find first fit
-			for (Map.Entry<Long, String> entry: map.entrySet()) {
+			for ( Map.Entry<Long, String> entry: map.entrySet() ) {
 				if ( size <= entry.getKey() ) {
 					return replace( entry.getValue(), size, precision, scale );
 				}
 			}
 		}
-		return replace( get(typeCode), size, precision, scale );
+
+		// if we get here one of 2 things happened:
+		//		1) There was no weighted registration for that typeCode
+		//		2) There was no weighting whose max capacity was big enough to contain size
+		return replace( get( typeCode ), size, precision, scale );
 	}
-	
+
 	private static String replace(String type, long size, int precision, int scale) {
-		type = StringHelper.replaceOnce(type, "$s", Integer.toString(scale) );
-		type = StringHelper.replaceOnce(type, "$l", Long.toString(size) );
-		return StringHelper.replaceOnce(type, "$p", Integer.toString(precision) );
+		type = StringHelper.replaceOnce( type, "$s", Integer.toString( scale ) );
+		type = StringHelper.replaceOnce( type, "$l", Long.toString( size ) );
+		return StringHelper.replaceOnce( type, "$p", Integer.toString( precision ) );
 	}
 
 	/**
-	 * set a type name for specified type key and capacity
-	 * @param typecode the type key
+	 * Register a weighted typeCode mapping
+	 *
+	 * @param typeCode the JDBC type code
+	 * @param capacity The capacity for this weighting
+	 * @param value The mapping (type name)
 	 */
-	public void put(int typecode, long capacity, String value) {
-		Map<Long, String> map = weighted.get( typecode );
-		if (map == null) {// add new ordered map
+	public void put(int typeCode, long capacity, String value) {
+		Map<Long, String> map = weighted.get( typeCode );
+		if ( map == null ) {
+			// add new ordered map
 			map = new TreeMap<Long, String>();
-			weighted.put( typecode, map );
+			weighted.put( typeCode, map );
 		}
-		map.put(capacity, value);
+		map.put( capacity, value );
 	}
 
 	/**
-	 * set a default type name for specified type key
-	 * @param typecode the type key
+	 * Register a default (non-weighted) typeCode mapping
+	 *
+	 * @param typeCode the type key
+	 * @param value The mapping (type name)
 	 */
-	public void put(int typecode, String value) {
-		defaults.put( typecode, value );
+	public void put(int typeCode, String value) {
+		defaults.put( typeCode, value );
 	}
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
index 698ba346a7..0a0e726d6c 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
@@ -1,219 +1,225 @@
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
 package org.hibernate.dialect.function;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * A {@link org.hibernate.dialect.function.SQLFunction} providing support for implementing TRIM functionality
  * (as defined by both the ANSI SQL and JPA specs) in cases where the dialect may not support the full <tt>trim</tt>
  * function itself.
  * <p/>
  * Follows the <a href="http://en.wikipedia.org/wiki/Template_method_pattern">template</a> pattern in order to implement
  * the {@link #render} method.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractAnsiTrimEmulationFunction implements SQLFunction {
 	@Override
 	public final boolean hasArguments() {
 		return true;
 	}
 
 	@Override
 	public final boolean hasParenthesesIfNoArguments() {
 		return false;
 	}
 
 	@Override
 	public final Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {
 		return StandardBasicTypes.STRING;
 	}
 
 	@Override
 	public final String render(Type argumentType, List args, SessionFactoryImplementor factory) throws QueryException {
 		// According to both the ANSI-SQL and JPA specs, trim takes a variable number of parameters between 1 and 4.
 		// at least one paramer (trimSource) is required.  From the SQL spec:
 		//
 		// <trim function> ::=
 		//      TRIM <left paren> <trim operands> <right paren>
 		//
 		// <trim operands> ::=
 		//      [ [ <trim specification> ] [ <trim character> ] FROM ] <trim source>
 		//
 		// <trim specification> ::=
 		//      LEADING
 		//      | TRAILING
 		//      | BOTH
 		//
 		// If <trim specification> is omitted, BOTH is assumed.
 		// If <trim character> is omitted, space is assumed
 		if ( args.size() == 1 ) {
 			// we have the form: trim(trimSource)
 			//      so we trim leading and trailing spaces
-			return resolveBothSpaceTrimFunction().render( argumentType, args, factory );			// EARLY EXIT!!!!
+			return resolveBothSpaceTrimFunction().render( argumentType, args, factory );
 		}
-		else if ( "from".equalsIgnoreCase( ( String ) args.get( 0 ) ) ) {
+		else if ( "from".equalsIgnoreCase( (String) args.get( 0 ) ) ) {
 			// we have the form: trim(from trimSource).
 			//      This is functionally equivalent to trim(trimSource)
-			return resolveBothSpaceTrimFromFunction().render( argumentType, args, factory );  		// EARLY EXIT!!!!
+			return resolveBothSpaceTrimFromFunction().render( argumentType, args, factory );
 		}
 		else {
 			// otherwise, a trim-specification and/or a trim-character
 			// have been specified;  we need to decide which options
 			// are present and "do the right thing"
-			boolean leading = true;         // should leading trim-characters be trimmed?
-			boolean trailing = true;        // should trailing trim-characters be trimmed?
-			String trimCharacter;    		// the trim-character (what is to be trimmed off?)
-			String trimSource;       		// the trim-source (from where should it be trimmed?)
+
+			// should leading trim-characters be trimmed?
+			boolean leading = true;
+			// should trailing trim-characters be trimmed?
+			boolean trailing = true;
+			// the trim-character (what is to be trimmed off?)
+			String trimCharacter;
+			// the trim-source (from where should it be trimmed?)
+			String trimSource;
 
 			// potentialTrimCharacterArgIndex = 1 assumes that a
 			// trim-specification has been specified.  we handle the
 			// exception to that explicitly
 			int potentialTrimCharacterArgIndex = 1;
-			String firstArg = ( String ) args.get( 0 );
+			final String firstArg = (String) args.get( 0 );
 			if ( "leading".equalsIgnoreCase( firstArg ) ) {
 				trailing = false;
 			}
 			else if ( "trailing".equalsIgnoreCase( firstArg ) ) {
 				leading = false;
 			}
 			else if ( "both".equalsIgnoreCase( firstArg ) ) {
+				// nothing to do here
 			}
 			else {
 				potentialTrimCharacterArgIndex = 0;
 			}
 
-			String potentialTrimCharacter = ( String ) args.get( potentialTrimCharacterArgIndex );
+			final String potentialTrimCharacter = (String) args.get( potentialTrimCharacterArgIndex );
 			if ( "from".equalsIgnoreCase( potentialTrimCharacter ) ) { 
 				trimCharacter = "' '";
-				trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 1 );
+				trimSource = (String) args.get( potentialTrimCharacterArgIndex + 1 );
 			}
 			else if ( potentialTrimCharacterArgIndex + 1 >= args.size() ) {
 				trimCharacter = "' '";
 				trimSource = potentialTrimCharacter;
 			}
 			else {
 				trimCharacter = potentialTrimCharacter;
-				if ( "from".equalsIgnoreCase( ( String ) args.get( potentialTrimCharacterArgIndex + 1 ) ) ) {
-					trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 2 );
+				if ( "from".equalsIgnoreCase( (String) args.get( potentialTrimCharacterArgIndex + 1 ) ) ) {
+					trimSource = (String) args.get( potentialTrimCharacterArgIndex + 2 );
 				}
 				else {
-					trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 1 );
+					trimSource = (String) args.get( potentialTrimCharacterArgIndex + 1 );
 				}
 			}
 
-			List<String> argsToUse = new ArrayList<String>();
+			final List<String> argsToUse = new ArrayList<String>();
 			argsToUse.add( trimSource );
 			argsToUse.add( trimCharacter );
 
 			if ( trimCharacter.equals( "' '" ) ) {
 				if ( leading && trailing ) {
 					return resolveBothSpaceTrimFunction().render( argumentType, argsToUse, factory );
 				}
 				else if ( leading ) {
 					return resolveLeadingSpaceTrimFunction().render( argumentType, argsToUse, factory );
 				}
 				else {
 					return resolveTrailingSpaceTrimFunction().render( argumentType, argsToUse, factory );
 				}
 			}
 			else {
 				if ( leading && trailing ) {
 					return resolveBothTrimFunction().render( argumentType, argsToUse, factory );
 				}
 				else if ( leading ) {
 					return resolveLeadingTrimFunction().render( argumentType, argsToUse, factory );
 				}
 				else {
 					return resolveTrailingTrimFunction().render( argumentType, argsToUse, factory );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Resolve the function definition which should be used to trim both leading and trailing spaces.
 	 * <p/>
 	 * In this form, the imput arguments is missing the <tt>FROM</tt> keyword.
 	 *
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveBothSpaceTrimFunction();
 
 	/**
 	 * Resolve the function definition which should be used to trim both leading and trailing spaces.
 	 * <p/>
 	 * The same as {#link resolveBothSpaceTrimFunction} except that here the<tt>FROM</tt> is included and
 	 * will need to be accounted for during {@link SQLFunction#render} processing.
 	 * 
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveBothSpaceTrimFromFunction();
 
 	/**
 	 * Resolve the function definition which should be used to trim leading spaces.
 	 *
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveLeadingSpaceTrimFunction();
 
 	/**
 	 * Resolve the function definition which should be used to trim trailing spaces.
 	 *
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveTrailingSpaceTrimFunction();
 
 	/**
 	 * Resolve the function definition which should be used to trim the specified character from both the
 	 * beginning (leading) and end (trailing) of the trim source.
 	 *
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveBothTrimFunction();
 
 	/**
 	 * Resolve the function definition which should be used to trim the specified character from the
 	 * beginning (leading) of the trim source.
 	 *
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveLeadingTrimFunction();
 
 	/**
 	 * Resolve the function definition which should be used to trim the specified character from the
 	 * end (trailing) of the trim source.
 	 *
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveTrailingTrimFunction();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimEmulationFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimEmulationFunction.java
index ab566bbcc0..3514d84516 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimEmulationFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimEmulationFunction.java
@@ -1,254 +1,292 @@
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
-package org.hibernate.dialect.function;
+package org.hibernate.dialect.function;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A {@link SQLFunction} implementation that emulates the ANSI SQL trim function
  * on dialects which do not support the full definition.  However, this function
  * definition does assume the availability of ltrim, rtrim, and replace functions
  * which it uses in various combinations to emulate the desired ANSI trim()
  * functionality.
  *
  * @author Steve Ebersole
  */
 public class AnsiTrimEmulationFunction extends AbstractAnsiTrimEmulationFunction {
+	/**
+	 * The default {@code ltrim} function name
+	 */
 	public static final String LTRIM = "ltrim";
+
+	/**
+	 * The default {@code rtrim} function name
+	 */
 	public static final String RTRIM = "rtrim";
+
+	/**
+	 * The default {@code replace} function name
+	 */
 	public static final String REPLACE = "replace";
+
+	/**
+	 * The placeholder used to represent whitespace
+	 */
 	public static final String SPACE_PLACEHOLDER = "${space}$";
 
+	/**
+	 * The SQLFunctionTemplate pattern for the trimming leading spaces
+	 */
 	public static final String LEADING_SPACE_TRIM_TEMPLATE = LTRIM + "(?1)";
+
+	/**
+	 * The SQLFunctionTemplate pattern for the trimming trailing spaces
+	 */
 	public static final String TRAILING_SPACE_TRIM_TEMPLATE = RTRIM + "(?1)";
+
+	/**
+	 * The SQLFunctionTemplate pattern for the trimming both leading and trailing spaces
+	 */
 	public static final String BOTH_SPACE_TRIM_TEMPLATE = LTRIM + "(" + RTRIM + "(?1))";
-	public static final String BOTH_SPACE_TRIM_FROM_TEMPLATE = LTRIM + "(" + RTRIM + "(?2))"; //skip the FROM keyword in params
+
+	/**
+	 * The SQLFunctionTemplate pattern for the trimming both leading and trailing spaces, with the optional FROM keyword.
+	 * Different because we need to skip the FROM keyword in the SQLFunctionTemplate processing
+	 */
+	public static final String BOTH_SPACE_TRIM_FROM_TEMPLATE = LTRIM + "(" + RTRIM + "(?2))";
 
 	/**
 	 * A template for the series of calls required to trim non-space chars from the beginning of text.
 	 * <p/>
-	 * NOTE : essentially we:</ol>
+	 * NOTE : essentially we:<ol>
 	 * <li>replace all space chars with the text '${space}$'</li>
 	 * <li>replace all the actual replacement chars with space chars</li>
 	 * <li>perform left-trimming (that removes any of the space chars we just added which occur at the beginning of the text)</li>
 	 * <li>replace all space chars with the replacement char</li>
 	 * <li>replace all the '${space}$' text with space chars</li>
 	 * </ol>
 	 */
 	public static final String LEADING_TRIM_TEMPLATE =
 			REPLACE + "(" +
 				REPLACE + "(" +
 					LTRIM + "(" +
 						REPLACE + "(" +
 							REPLACE + "(" +
 								"?1," +
 								"' '," +
 								"'" + SPACE_PLACEHOLDER + "'" +
 							")," +
 							"?2," +
 							"' '" +
 						")" +
 					")," +
 					"' '," +
 					"?2" +
 				")," +
 				"'" + SPACE_PLACEHOLDER + "'," +
 				"' '" +
 			")";
 
 	/**
 	 * A template for the series of calls required to trim non-space chars from the end of text.
 	 * <p/>
 	 * NOTE: essentially the same series of calls as outlined in {@link #LEADING_TRIM_TEMPLATE} except that here,
 	 * instead of left-trimming the added spaces, we right-trim them to remove them from the end of the text.
 	 */
 	public static final String TRAILING_TRIM_TEMPLATE =
 			REPLACE + "(" +
 				REPLACE + "(" +
 					RTRIM + "(" +
 						REPLACE + "(" +
 							REPLACE + "(" +
 								"?1," +
 								"' '," +
 								"'" + SPACE_PLACEHOLDER + "'" +
 							")," +
 							"?2," +
 							"' '" +
 						")" +
 					")," +
 					"' '," +
 					"?2" +
 				")," +
 				"'" + SPACE_PLACEHOLDER + "'," +
 				"' '" +
 			")";
 
 	/**
 	 * A template for the series of calls required to trim non-space chars from both the beginning and the end of text.
 	 * <p/>
 	 * NOTE: again, we have a series of calls that is essentially the same as outlined in {@link #LEADING_TRIM_TEMPLATE}
 	 * except that here we perform both left (leading) and right (trailing) trimming.
 	 */
 	public static final String BOTH_TRIM_TEMPLATE =
 			REPLACE + "(" +
 				REPLACE + "(" +
 					LTRIM + "(" +
 						RTRIM + "(" +
 							REPLACE + "(" +
 								REPLACE + "(" +
 									"?1," +
 									"' '," +
 									"'" + SPACE_PLACEHOLDER + "'" +
 								")," +
 								"?2," +
 								"' '" +
 							")" +
 						")" +
 					")," +
 					"' '," +
 					"?2" +
 				")," +
 				"'" + SPACE_PLACEHOLDER + "'," +
 				"' '" +
 			")";
 
 	private final SQLFunction leadingSpaceTrim;
 	private final SQLFunction trailingSpaceTrim;
 	private final SQLFunction bothSpaceTrim;
 	private final SQLFunction bothSpaceTrimFrom;
 
 	private final SQLFunction leadingTrim;
 	private final SQLFunction trailingTrim;
 	private final SQLFunction bothTrim;
 
 	/**
 	 * Constructs a new AnsiTrimEmulationFunction using {@link #LTRIM}, {@link #RTRIM}, and {@link #REPLACE}
 	 * respectively.
 	 *
 	 * @see #AnsiTrimEmulationFunction(String,String,String)
 	 */
 	public AnsiTrimEmulationFunction() {
 		this( LTRIM, RTRIM, REPLACE );
 	}
 
 	/**
 	 * Constructs a <tt>trim()</tt> emulation function definition using the specified function calls.
 	 *
 	 * @param ltrimFunctionName The <tt>left trim</tt> function to use.
 	 * @param rtrimFunctionName The <tt>right trim</tt> function to use.
 	 * @param replaceFunctionName The <tt>replace</tt> function to use.
 	 */
 	public AnsiTrimEmulationFunction(String ltrimFunctionName, String rtrimFunctionName, String replaceFunctionName) {
 		leadingSpaceTrim = new SQLFunctionTemplate(
 				StandardBasicTypes.STRING,
 				LEADING_SPACE_TRIM_TEMPLATE.replaceAll( LTRIM, ltrimFunctionName )
 		);
 
 		trailingSpaceTrim = new SQLFunctionTemplate(
 				StandardBasicTypes.STRING,
 				TRAILING_SPACE_TRIM_TEMPLATE.replaceAll( RTRIM, rtrimFunctionName )
 		);
 
 		bothSpaceTrim = new SQLFunctionTemplate(
 				StandardBasicTypes.STRING,
 				BOTH_SPACE_TRIM_TEMPLATE.replaceAll( LTRIM, ltrimFunctionName )
 						.replaceAll( RTRIM, rtrimFunctionName )
 		);
 
 		bothSpaceTrimFrom = new SQLFunctionTemplate(
 				StandardBasicTypes.STRING,
 				BOTH_SPACE_TRIM_FROM_TEMPLATE.replaceAll( LTRIM, ltrimFunctionName )
 						.replaceAll( RTRIM, rtrimFunctionName )
 		);
 
 		leadingTrim = new SQLFunctionTemplate(
 				StandardBasicTypes.STRING,
 				LEADING_TRIM_TEMPLATE.replaceAll( LTRIM, ltrimFunctionName )
 						.replaceAll( RTRIM, rtrimFunctionName )
 						.replaceAll( REPLACE,replaceFunctionName )
 		);
 
 		trailingTrim = new SQLFunctionTemplate(
 				StandardBasicTypes.STRING,
 				TRAILING_TRIM_TEMPLATE.replaceAll( LTRIM, ltrimFunctionName )
 						.replaceAll( RTRIM, rtrimFunctionName )
 						.replaceAll( REPLACE,replaceFunctionName )
 		);
 
 		bothTrim = new SQLFunctionTemplate(
 				StandardBasicTypes.STRING,
 				BOTH_TRIM_TEMPLATE.replaceAll( LTRIM, ltrimFunctionName )
 						.replaceAll( RTRIM, rtrimFunctionName )
 						.replaceAll( REPLACE,replaceFunctionName )
 		);
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
+	@Override
 	protected SQLFunction resolveBothSpaceTrimFunction() {
 		return bothSpaceTrim;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
+	@Override
 	protected SQLFunction resolveBothSpaceTrimFromFunction() {
 		return bothSpaceTrimFrom;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
+	@Override
 	protected SQLFunction resolveLeadingSpaceTrimFunction() {
 		return leadingSpaceTrim;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
+	@Override
 	protected SQLFunction resolveTrailingSpaceTrimFunction() {
 		return trailingSpaceTrim;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
+	@Override
 	protected SQLFunction resolveBothTrimFunction() {
 		return bothTrim;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
+	@Override
 	protected SQLFunction resolveLeadingTrimFunction() {
 		return leadingTrim;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
+	@Override
 	protected SQLFunction resolveTrailingTrimFunction() {
 		return trailingTrim;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimFunction.java
index e8427b8bf1..8d989fb012 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimFunction.java
@@ -1,44 +1,42 @@
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
 package org.hibernate.dialect.function;
+
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 
 /**
  * Defines support for rendering according to ANSI SQL <tt>TRIM</tt> function specification.
  *
  * @author Steve Ebersole
  */
 public class AnsiTrimFunction extends TrimFunctionTemplate {
 	protected String render(Options options, String trimSource, SessionFactoryImplementor factory) {
-		return new StringBuilder()
-				.append( "trim(" )
-				.append( options.getTrimSpecification().getName() )
-				.append( ' ' )
-				.append( options.getTrimCharacter() )
-				.append( " from " )
-				.append( trimSource )
-				.append( ')' )
-				.toString();
+		return String.format(
+				"trim(%s %s from %s)",
+				options.getTrimSpecification().getName(),
+				options.getTrimCharacter(),
+				trimSource
+		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/AvgWithArgumentCastFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/AvgWithArgumentCastFunction.java
index 81415cb87b..08afb4af06 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/AvgWithArgumentCastFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/AvgWithArgumentCastFunction.java
@@ -1,50 +1,56 @@
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
 package org.hibernate.dialect.function;
+
 import java.sql.Types;
 
 /**
  * Some databases strictly return the type of the of the aggregation value for <tt>AVG</tt> which is
  * problematic in the case of averaging integers because the decimals will be dropped.  The usual workaround
  * is to cast the integer argument as some form of double/decimal.
  *
  * @author Steve Ebersole
  */
 public class AvgWithArgumentCastFunction extends StandardAnsiSqlAggregationFunctions.AvgFunction {
 	private final String castType;
 
+	/**
+	 * Constructs a AvgWithArgumentCastFunction
+	 *
+	 * @param castType The type to cast the avg argument to
+	 */
 	public AvgWithArgumentCastFunction(String castType) {
 		this.castType = castType;
 	}
 
 	@Override
 	protected String renderArgument(String argument, int firstArgumentJdbcType) {
 		if ( firstArgumentJdbcType == Types.DOUBLE || firstArgumentJdbcType == Types.FLOAT ) {
 			return argument;
 		}
 		else {
 			return "cast(" + argument + " as " + castType + ")";
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/CastFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/CastFunction.java
index cd124a3a5c..d145854bc7 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/CastFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/CastFunction.java
@@ -1,74 +1,73 @@
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
 package org.hibernate.dialect.function;
+
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.Type;
 
 /**
- * ANSI-SQL style <tt>cast(foo as type)</tt> where the type is
- * a Hibernate type
+ * ANSI-SQL style {@code cast(foo as type)} where the type is a Hibernate type
+ *
  * @author Gavin King
  */
 public class CastFunction implements SQLFunction {
+	@Override
 	public boolean hasArguments() {
 		return true;
 	}
 
+	@Override
 	public boolean hasParenthesesIfNoArguments() {
 		return true;
 	}
 
+	@Override
 	public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
-		return columnType; // this is really just a guess, unless the caller properly identifies the 'type' argument here
+		// this is really just a guess, unless the caller properly identifies the 'type' argument here
+		return columnType;
 	}
 
+	@Override
 	public String render(Type columnType, List args, SessionFactoryImplementor factory) throws QueryException {
 		if ( args.size()!=2 ) {
 			throw new QueryException("cast() requires two arguments");
 		}
-		String type = (String) args.get(1);
-		int[] sqlTypeCodes = factory.getTypeResolver().heuristicType(type).sqlTypes(factory);
+		final String type = (String) args.get( 1 );
+		final int[] sqlTypeCodes = factory.getTypeResolver().heuristicType( type ).sqlTypes( factory );
 		if ( sqlTypeCodes.length!=1 ) {
 			throw new QueryException("invalid Hibernate type for cast()");
 		}
 		String sqlType = factory.getDialect().getCastTypeName( sqlTypeCodes[0] );
-		if (sqlType==null) {
+		if ( sqlType == null ) {
 			//TODO: never reached, since getExplicitHibernateTypeName() actually throws an exception!
 			sqlType = type;
 		}
-		/*else {
-			//trim off the length/precision/scale
-			int loc = sqlType.indexOf('(');
-			if (loc>-1) {
-				sqlType = sqlType.substring(0, loc);
-			}
-		}*/
-		return "cast(" + args.get(0) + " as " + sqlType + ')';
+		return "cast(" + args.get( 0 ) + " as " + sqlType + ')';
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/CharIndexFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/CharIndexFunction.java
index 5b29a650a3..2ee38af192 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/CharIndexFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/CharIndexFunction.java
@@ -1,65 +1,74 @@
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
 package org.hibernate.dialect.function;
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Emulation of <tt>locate()</tt> on Sybase
+ *
  * @author Nathan Moon
  */
 public class CharIndexFunction implements SQLFunction {
+	@Override
 	public boolean hasArguments() {
 		return true;
 	}
 
+	@Override
 	public boolean hasParenthesesIfNoArguments() {
 		return true;
 	}
 
+	@Override
 	public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
 		return StandardBasicTypes.INTEGER;
 	}
 
+	@Override
 	public String render(Type columnType, List args, SessionFactoryImplementor factory) throws QueryException {
-		boolean threeArgs = args.size() > 2;
-		Object pattern = args.get(0);
-		Object string = args.get(1);
-		Object start = threeArgs ? args.get(2) : null;
+		final boolean threeArgs = args.size() > 2;
+		final Object pattern = args.get( 0 );
+		final Object string = args.get( 1 );
+		final Object start = threeArgs ? args.get( 2 ) : null;
 
-		StringBuilder buf = new StringBuilder();
-		buf.append("charindex(").append( pattern ).append(", ");
-		if (threeArgs) buf.append( "right(");
+		final StringBuilder buf = new StringBuilder();
+		buf.append( "charindex(" ).append( pattern ).append( ", " );
+		if (threeArgs) {
+			buf.append( "right(" );
+		}
 		buf.append( string );
-		if (threeArgs) buf.append( ", char_length(" ).append( string ).append(")-(").append( start ).append("-1))");
-		buf.append(')');
+		if (threeArgs) {
+			buf.append( ", char_length(" ).append( string ).append( ")-(" ).append( start ).append( "-1))" );
+		}
+		buf.append( ')' );
 		return buf.toString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicAvgFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicAvgFunction.java
index e75f9a4286..cd3b5fdfcb 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicAvgFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicAvgFunction.java
@@ -1,61 +1,68 @@
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
 package org.hibernate.dialect.function;
 import java.sql.Types;
 
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Classic AVG sqlfunction that return types as it was done in Hibernate 3.1 
  * 
  * @author Max Rydahl Andersen
- *
  */
 public class ClassicAvgFunction extends StandardSQLFunction {
+	/**
+	 * Constructs a ClassicAvgFunction
+	 */
 	public ClassicAvgFunction() {
 		super( "avg" );
 	}
 
+	@Override
 	public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
 		int[] sqlTypes;
 		try {
 			sqlTypes = columnType.sqlTypes( mapping );
 		}
 		catch ( MappingException me ) {
 			throw new QueryException( me );
 		}
-		if ( sqlTypes.length != 1 ) throw new QueryException( "multi-column type in avg()" );
-		int sqlType = sqlTypes[0];
+
+		if ( sqlTypes.length != 1 ) {
+			throw new QueryException( "multi-column type in avg()" );
+		}
+
+		final int sqlType = sqlTypes[0];
 		if ( sqlType == Types.INTEGER || sqlType == Types.BIGINT || sqlType == Types.TINYINT ) {
 			return StandardBasicTypes.FLOAT;
 		}
 		else {
 			return columnType;
 		}
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicCountFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicCountFunction.java
index 6b7804291a..f26fc62cee 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicCountFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicCountFunction.java
@@ -1,43 +1,47 @@
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
 package org.hibernate.dialect.function;
+
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Classic COUNT sqlfunction that return types as it was done in Hibernate 3.1 
  * 
  * @author Max Rydahl Andersen
- *
  */
 public class ClassicCountFunction extends StandardSQLFunction {
+	/**
+	 * Constructs a ClassicCountFunction
+	 */
 	public ClassicCountFunction() {
 		super( "count" );
 	}
 
+	@Override
 	public Type getReturnType(Type columnType, Mapping mapping) {
 		return StandardBasicTypes.INTEGER;
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicSumFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicSumFunction.java
index 1da180f9b0..6bfed870de 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicSumFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicSumFunction.java
@@ -1,37 +1,39 @@
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
-package org.hibernate.dialect.function;
-
+package org.hibernate.dialect.function;
 
 /**
  * Classic SUM sqlfunction that return types as it was done in Hibernate 3.1 
  * 
  * @author Max Rydahl Andersen
  *
  */
 public class ClassicSumFunction extends StandardSQLFunction {
+	/**
+	 * Constructs a ClassicSumFunction
+	 */
 	public ClassicSumFunction() {
 		super( "sum" );
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/ConditionalParenthesisFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/ConditionalParenthesisFunction.java
index 4f35062f40..b47e96058c 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/ConditionalParenthesisFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/ConditionalParenthesisFunction.java
@@ -1,66 +1,77 @@
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
 package org.hibernate.dialect.function;
 import java.util.List;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Essentially the same as {@link org.hibernate.dialect.function.StandardSQLFunction},
  * except that here the parentheses are not included when no arguments are given.
  *
  * @author Jonathan Levinson
  */
 public class ConditionalParenthesisFunction extends StandardSQLFunction {
-
+	/**
+	 * Constructs a ConditionalParenthesisFunction with the given name
+	 *
+	 * @param name The function name
+	 */
 	public ConditionalParenthesisFunction(String name) {
 		super( name );
 	}
 
+	/**
+	 * Constructs a ConditionalParenthesisFunction with the given name
+	 *
+	 * @param name The function name
+	 * @param type The function return type
+	 */
 	public ConditionalParenthesisFunction(String name, Type type) {
 		super( name, type );
 	}
 
+	@Override
 	public boolean hasParenthesesIfNoArguments() {
 		return false;
 	}
 
-	public String render(List args, SessionFactoryImplementor factory) {
-		final boolean hasArgs = !args.isEmpty();
-		StringBuilder buf = new StringBuilder();
-		buf.append( getName() );
+	@Override
+	public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor sessionFactory) {
+		final boolean hasArgs = !arguments.isEmpty();
+		final StringBuilder buf = new StringBuilder( getName() );
 		if ( hasArgs ) {
 			buf.append( "(" );
-			for ( int i = 0; i < args.size(); i++ ) {
-				buf.append( args.get( i ) );
-				if ( i < args.size() - 1 ) {
+			for ( int i = 0; i < arguments.size(); i++ ) {
+				buf.append( arguments.get( i ) );
+				if ( i < arguments.size() - 1 ) {
 					buf.append( ", " );
 				}
 			}
 			buf.append( ")" );
 		}
 		return buf.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/ConvertFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/ConvertFunction.java
index 6af5b5dd51..801ef44b40 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/ConvertFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/ConvertFunction.java
@@ -1,66 +1,70 @@
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
 package org.hibernate.dialect.function;
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * A Cach&eacute; defintion of a convert function.
  *
  * @author Jonathan Levinson
  */
 public class ConvertFunction implements SQLFunction {
-
+	@Override
 	public boolean hasArguments() {
 		return true;
 	}
 
+	@Override
 	public boolean hasParenthesesIfNoArguments() {
 		return true;
 	}
 
+	@Override
 	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
 		return StandardBasicTypes.STRING;
 	}
 
+	@Override
 	public String render(Type firstArgumentType, List args, SessionFactoryImplementor factory) throws QueryException {
 		if ( args.size() != 2 && args.size() != 3 ) {
 			throw new QueryException( "convert() requires two or three arguments" );
 		}
-		String type = ( String ) args.get( 1 );
+
+		final String type = (String) args.get( 1 );
 
 		if ( args.size() == 2 ) {
 			return "{fn convert(" + args.get( 0 ) + " , " + type + ")}";
 		}
 		else {
 			return "convert(" + args.get( 0 ) + " , " + type + "," + args.get( 2 ) + ")";
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java
index 786933fdcb..3cf7e847f9 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java
@@ -1,178 +1,198 @@
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
 package org.hibernate.dialect.function;
+
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * A specialized concat() function definition in which:<ol>
- * <li>we translate to use the concat operator ('||')</li>
- * <li>wrap dynamic parameters in CASTs to VARCHAR</li>
+ *     <li>we translate to use the concat operator ('||')</li>
+ *     <li>wrap dynamic parameters in CASTs to VARCHAR</li>
  * </ol>
  * <p/>
  * This last spec is to deal with a limitation on DB2 and variants (e.g. Derby)
  * where dynamic parameters cannot be used in concatenation unless they are being
  * concatenated with at least one non-dynamic operand.  And even then, the rules
  * are so convoluted as to what is allowed and when the CAST is needed and when
  * it is not that we just go ahead and do the CASTing.
  *
  * @author Steve Ebersole
  */
 public class DerbyConcatFunction implements SQLFunction {
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Here we always return <tt>true</tt>
 	 */
+	@Override
 	public boolean hasArguments() {
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Here we always return <tt>true</tt>
 	 */
+	@Override
 	public boolean hasParenthesesIfNoArguments() {
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Here we always return {@link StandardBasicTypes#STRING}.
 	 */
+	@Override
 	public Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {
 		return StandardBasicTypes.STRING;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Here's the meat..  The whole reason we have a separate impl for this for Derby is to re-define
 	 * this method.  The logic here says that if not all the incoming args are dynamic parameters
 	 * (i.e. <tt>?</tt>) then we simply use the Derby concat operator (<tt>||</tt>) on the unchanged
 	 * arg elements.  However, if all the args are dynamic parameters, then we need to wrap the individual
 	 * arg elements in <tt>cast</tt> function calls, use the concatenation operator on the <tt>cast</tt>
 	 * returns, and then wrap that whole thing in a call to the Derby <tt>varchar</tt> function.
 	 */
+	@Override
 	public String render(Type argumentType, List args, SessionFactoryImplementor factory) throws QueryException {
-		boolean areAllArgsParams = true;
-		Iterator itr = args.iterator();
-		while ( itr.hasNext() ) {
-			final String arg = ( String ) itr.next();
-			if ( ! "?".equals( arg ) ) {
-				areAllArgsParams = false;
+		// first figure out if all arguments are dynamic (jdbc parameters) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		boolean areAllArgumentsDynamic = true;
+		for ( Object arg1 : args ) {
+			final String arg = (String) arg1;
+			if ( !"?".equals( arg ) ) {
+				// we found a non-dynamic argument
+				areAllArgumentsDynamic = false;
 				break;
 			}
 		}
 
-		if ( areAllArgsParams ) {
+		if ( areAllArgumentsDynamic ) {
 			return join(
 					args.iterator(),
-					new StringTransformer() {
-						public String transform(String string) {
-							return "cast( ? as varchar(32672) )";
-						}
-					},
+					CAST_STRING_TRANSFORMER,
 					new StringJoinTemplate() {
 						public String getBeginning() {
 							return "varchar( ";
 						}
 						public String getSeparator() {
 							return " || ";
 						}
 						public String getEnding() {
 							return " )";
 						}
 					}
 			);
 		}
 		else {
 			return join(
 					args.iterator(),
-					new StringTransformer() {
-						public String transform(String string) {
-							return string;
-						}
-					},
+					NO_TRANSFORM_STRING_TRANSFORMER,
 					new StringJoinTemplate() {
 						public String getBeginning() {
 							return "(";
 						}
 						public String getSeparator() {
 							return "||";
 						}
 						public String getEnding() {
 							return ")";
 						}
 					}
 			);
 		}
 	}
 
 	private static interface StringTransformer {
+		/**
+		 * Transform a string to another
+		 *
+		 * @param string The String to be transformed
+		 *
+		 * @return The transformed form
+		 */
 		public String transform(String string);
 	}
 
+	private static final StringTransformer CAST_STRING_TRANSFORMER = new StringTransformer() {
+		@Override
+		public String transform(String string) {
+			// expectation is that incoming string is "?"
+			return "cast( ? as varchar(32672) )";
+		}
+	};
+
+	private static final StringTransformer NO_TRANSFORM_STRING_TRANSFORMER = new StringTransformer() {
+		@Override
+		public String transform(String string) {
+			return string;
+		}
+	};
+
 	private static interface StringJoinTemplate {
 		/**
 		 * Getter for property 'beginning'.
 		 *
 		 * @return Value for property 'beginning'.
 		 */
 		public String getBeginning();
 		/**
 		 * Getter for property 'separator'.
 		 *
 		 * @return Value for property 'separator'.
 		 */
 		public String getSeparator();
 		/**
 		 * Getter for property 'ending'.
 		 *
 		 * @return Value for property 'ending'.
 		 */
 		public String getEnding();
 	}
 
 	private static String join(Iterator/*<String>*/ elements, StringTransformer elementTransformer, StringJoinTemplate template) {
 		// todo : make this available via StringHelper?
-		StringBuilder buffer = new StringBuilder( template.getBeginning() );
+		final StringBuilder buffer = new StringBuilder( template.getBeginning() );
 		while ( elements.hasNext() ) {
-			final String element = ( String ) elements.next();
+			final String element = (String) elements.next();
 			buffer.append( elementTransformer.transform( element ) );
 			if ( elements.hasNext() ) {
 				buffer.append( template.getSeparator() );
 			}
 		}
 		return buffer.append( template.getEnding() ).toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/NoArgSQLFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/NoArgSQLFunction.java
index 366ae1a35e..dac23a2310 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/NoArgSQLFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/NoArgSQLFunction.java
@@ -1,70 +1,88 @@
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
 package org.hibernate.dialect.function;
+
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.Type;
 
 /**
  * A function which takes no arguments
- * 
+ *
  * @author Michi
  */
 public class NoArgSQLFunction implements SQLFunction {
-    private Type returnType;
-    private boolean hasParenthesesIfNoArguments;
-    private String name;
+	private Type returnType;
+	private boolean hasParenthesesIfNoArguments;
+	private String name;
 
-    public NoArgSQLFunction(String name, Type returnType) {
-        this(name, returnType, true);
-    }
+	/**
+	 * Constructs a NoArgSQLFunction
+	 *
+	 * @param name The function name
+	 * @param returnType The function return type
+	 */
+	public NoArgSQLFunction(String name, Type returnType) {
+		this( name, returnType, true );
+	}
 
-    public NoArgSQLFunction(String name, Type returnType, boolean hasParenthesesIfNoArguments) {
-        this.returnType = returnType;
-        this.hasParenthesesIfNoArguments = hasParenthesesIfNoArguments;
-        this.name = name;
-    }
+	/**
+	 * Constructs a NoArgSQLFunction
+	 *
+	 * @param name The function name
+	 * @param returnType The function return type
+	 * @param hasParenthesesIfNoArguments Does the function call need parenthesis if there are no arguments?
+	 */
+	public NoArgSQLFunction(String name, Type returnType, boolean hasParenthesesIfNoArguments) {
+		this.returnType = returnType;
+		this.hasParenthesesIfNoArguments = hasParenthesesIfNoArguments;
+		this.name = name;
+	}
 
+	@Override
 	public boolean hasArguments() {
 		return false;
 	}
 
+	@Override
 	public boolean hasParenthesesIfNoArguments() {
 		return hasParenthesesIfNoArguments;
 	}
 
-    public Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {
-        return returnType;
-    }
+	@Override
+	public Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {
+		return returnType;
+	}
 
-    public String render(Type argumentType, List args, SessionFactoryImplementor factory) throws QueryException {
-    	if ( args.size()>0 ) {
-    		throw new QueryException("function takes no arguments: " + name);
-    	}
-    	return hasParenthesesIfNoArguments ? name + "()" : name;
-    }
+	@Override
+	public String render(Type argumentType, List args, SessionFactoryImplementor factory) throws QueryException {
+		if ( args.size() > 0 ) {
+			throw new QueryException( "function takes no arguments: " + name );
+		}
+		return hasParenthesesIfNoArguments ? name + "()" : name;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/NvlFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/NvlFunction.java
index fbc09f0e9a..3f8d79d4a8 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/NvlFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/NvlFunction.java
@@ -1,64 +1,67 @@
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
 package org.hibernate.dialect.function;
+
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Emulation of <tt>coalesce()</tt> on Oracle, using multiple <tt>nvl()</tt> calls
  *
  * @author Gavin King
  */
 public class NvlFunction implements SQLFunction {
+	@Override
 	public boolean hasArguments() {
 		return true;
 	}
 
+	@Override
 	public boolean hasParenthesesIfNoArguments() {
 		return true;
 	}
 
+	@Override
 	public Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {
 		return argumentType;
 	}
 
+	@Override
+	@SuppressWarnings("unchecked")
 	public String render(Type argumentType, List args, SessionFactoryImplementor factory) throws QueryException {
-		int lastIndex = args.size()-1;
-		Object last = args.remove(lastIndex);
+		final int lastIndex = args.size()-1;
+		final Object last = args.remove( lastIndex );
 		if ( lastIndex==0 ) {
 			return last.toString();
 		}
-		Object secondLast = args.get(lastIndex-1);
-		String nvl = "nvl(" + secondLast + ", " + last + ")";
-		args.set(lastIndex-1, nvl);
+		final Object secondLast = args.get( lastIndex-1 );
+		final String nvl = "nvl(" + secondLast + ", " + last + ")";
+		args.set( lastIndex-1, nvl );
 		return render( argumentType, args, factory );
 	}
-
-	
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/PositionSubstringFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/PositionSubstringFunction.java
index 0e188bb71a..1561f6264b 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/PositionSubstringFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/PositionSubstringFunction.java
@@ -1,68 +1,81 @@
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
 package org.hibernate.dialect.function;
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Emulation of <tt>locate()</tt> on PostgreSQL
+ *
  * @author Gavin King
  */
 public class PositionSubstringFunction implements SQLFunction {
+	@Override
 	public boolean hasArguments() {
 		return true;
 	}
 
+	@Override
 	public boolean hasParenthesesIfNoArguments() {
 		return true;
 	}
 
+	@Override
 	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
 		return StandardBasicTypes.INTEGER;
 	}
 
+	@Override
 	public String render(Type firstArgumentType, List args, SessionFactoryImplementor factory) throws QueryException {
-		boolean threeArgs = args.size() > 2;
-		Object pattern = args.get(0);
-		Object string = args.get(1);
-		Object start = threeArgs ? args.get(2) : null;
+		final boolean threeArgs = args.size() > 2;
+		final Object pattern = args.get( 0 );
+		final Object string = args.get( 1 );
+		final Object start = threeArgs ? args.get( 2 ) : null;
 
-		StringBuilder buf = new StringBuilder();
-		if (threeArgs) buf.append('(');
-		buf.append("position(").append( pattern ).append(" in ");
-		if (threeArgs) buf.append( "substring(");
+		final StringBuilder buf = new StringBuilder();
+		if (threeArgs) {
+			buf.append( '(' );
+		}
+		buf.append( "position(" ).append( pattern ).append( " in " );
+		if (threeArgs) {
+			buf.append( "substring(");
+		}
 		buf.append( string );
-		if (threeArgs) buf.append( ", " ).append( start ).append(')');
-		buf.append(')');
-		if (threeArgs) buf.append('+').append( start ).append("-1)");
+		if (threeArgs) {
+			buf.append( ", " ).append( start ).append( ')' );
+		}
+		buf.append( ')' );
+		if (threeArgs) {
+			buf.append( '+' ).append( start ).append( "-1)" );
+		}
 		return buf.toString();
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunction.java
index 1da29778b1..052913e1c0 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunction.java
@@ -1,89 +1,89 @@
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
 package org.hibernate.dialect.function;
+
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Provides support routines for the HQL functions as used
  * in the various SQL Dialects
  *
  * Provides an interface for supporting various HQL functions that are
  * translated to SQL. The Dialect and its sub-classes use this interface to
  * provide details required for processing of the function.
  *
  * @author David Channon
  * @author Steve Ebersole
  */
 public interface SQLFunction {
 	/**
 	 * Does this function have any arguments?
 	 *
 	 * @return True if the function expects to have parameters; false otherwise.
 	 */
 	public boolean hasArguments();
 
 	/**
 	 * If there are no arguments, are parentheses required?
 	 *
 	 * @return True if a no-arg call of this function requires parentheses.
 	 */
 	public boolean hasParenthesesIfNoArguments();
 
 	/**
 	 * The return type of the function.  May be either a concrete type which is preset, or variable depending upon
 	 * the type of the first function argument.
 	 * <p/>
 	 * Note, the 'firstArgumentType' parameter should match the one passed into {@link #render}
 	 *
 	 * @param firstArgumentType The type of the first argument
 	 * @param mapping The mapping source.
 	 *
 	 * @return The type to be expected as a return.
 	 *
 	 * @throws org.hibernate.QueryException Indicates an issue resolving the return type.
 	 */
 	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException;
 
-
 	/**
 	 * Render the function call as SQL fragment.
 	 * <p/>
 	 * Note, the 'firstArgumentType' parameter should match the one passed into {@link #getReturnType}
 	 *
 	 * @param firstArgumentType The type of the first argument
 	 * @param arguments The function arguments
 	 * @param factory The SessionFactory
 	 *
 	 * @return The rendered function call
 	 *
 	 * @throws org.hibernate.QueryException Indicates a problem rendering the
 	 * function call.
 	 */
 	public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionRegistry.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionRegistry.java
index 0414770be6..c3bc29dedf 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionRegistry.java
@@ -1,53 +1,79 @@
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
 package org.hibernate.dialect.function;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.dialect.Dialect;
 
+/**
+ * Defines a registry for SQLFunction instances
+ *
+ * @author Steve Ebersole
+ */
 public class SQLFunctionRegistry {
 	private final Dialect dialect;
 	private final Map<String, SQLFunction> userFunctions;
 
+	/**
+	 * Constructs a SQLFunctionRegistry
+	 *
+	 * @param dialect The dialect
+	 * @param userFunctions Any application-supplied function definitions
+	 */
 	public SQLFunctionRegistry(Dialect dialect, Map<String, SQLFunction> userFunctions) {
 		this.dialect = dialect;
 		this.userFunctions = new HashMap<String, SQLFunction>( userFunctions );
 	}
 
+	/**
+	 * Find a SQLFunction by name
+	 *
+	 * @param functionName The name of the function to locate
+	 *
+	 * @return The located function, maye return {@code null}
+	 */
 	public SQLFunction findSQLFunction(String functionName) {
-		String name = functionName.toLowerCase();
-		SQLFunction userFunction = userFunctions.get( name );
+		final String name = functionName.toLowerCase();
+		final SQLFunction userFunction = userFunctions.get( name );
 		return userFunction != null
 				? userFunction
 				: dialect.getFunctions().get( name );
 	}
 
+	/**
+	 * Does this registry contain the named function
+	 *
+	 * @param functionName The name of the function to attempt to locate
+	 *
+	 * @return {@code true} if the registry contained that function
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public boolean hasFunction(String functionName) {
-		String name = functionName.toLowerCase();
+		final String name = functionName.toLowerCase();
 		return userFunctions.containsKey( name ) || dialect.getFunctions().containsKey( name );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionTemplate.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionTemplate.java
index 52e46789f2..16d13dccb3 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionTemplate.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionTemplate.java
@@ -1,91 +1,97 @@
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
 package org.hibernate.dialect.function;
+
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.Type;
 
 /**
- * Represents HQL functions that can have different representations in different SQL dialects.
- * E.g. in HQL we can define function <code>concat(?1, ?2)</code> to concatenate two strings 
- * p1 and p2. Target SQL function will be dialect-specific, e.g. <code>(?1 || ?2)</code> for 
- * Oracle, <code>concat(?1, ?2)</code> for MySql, <code>(?1 + ?2)</code> for MS SQL.
- * Each dialect will define a template as a string (exactly like above) marking function 
+ * Represents HQL functions that can have different representations in different SQL dialects where that
+ * difference can be handled via a template/pattern.
+ * <p/>
+ * E.g. in HQL we can define function <code>concat(?1, ?2)</code> to concatenate two strings
+ * p1 and p2.  Dialects would register different versions of this class *using the same name* (concat) but with
+ * different templates or patterns; <code>(?1 || ?2)</code> for Oracle, <code>concat(?1, ?2)</code> for MySql,
+ * <code>(?1 + ?2)</code> for MS SQL.  Each dialect will define a template as a string (exactly like above) marking function
  * parameters with '?' followed by parameter's index (first index is 1).
  *
  * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
  */
 public class SQLFunctionTemplate implements SQLFunction {
 	private final Type type;
 	private final TemplateRenderer renderer;
 	private final boolean hasParenthesesIfNoArgs;
 
+	/**
+	 * Constructs a SQLFunctionTemplate
+	 *
+	 * @param type The functions return type
+	 * @param template The function template
+	 */
 	public SQLFunctionTemplate(Type type, String template) {
 		this( type, template, true );
 	}
 
+	/**
+	 * Constructs a SQLFunctionTemplate
+	 *
+	 * @param type The functions return type
+	 * @param template The function template
+	 * @param hasParenthesesIfNoArgs If there are no arguments, are parentheses required?
+	 */
 	public SQLFunctionTemplate(Type type, String template, boolean hasParenthesesIfNoArgs) {
 		this.type = type;
 		this.renderer = new TemplateRenderer( template );
 		this.hasParenthesesIfNoArgs = hasParenthesesIfNoArgs;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String render(Type argumentType, List args, SessionFactoryImplementor factory) {
 		return renderer.render( args, factory );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {
 		return type;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean hasArguments() {
 		return renderer.getAnticipatedNumberOfArguments() > 0;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean hasParenthesesIfNoArguments() {
 		return hasParenthesesIfNoArgs;
 	}
 	
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String toString() {
 		return renderer.getTemplate();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java
index 34712c321f..862173fcba 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java
@@ -1,205 +1,238 @@
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
 package org.hibernate.dialect.function;
 
 import java.sql.Types;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Centralized definition of standard ANSI SQL aggregation functions
  *
  * @author Steve Ebersole
  */
 public class StandardAnsiSqlAggregationFunctions {
 	/**
 	 * Definition of a standard ANSI SQL compliant <tt>COUNT</tt> function
 	 */
 	public static class CountFunction extends StandardSQLFunction {
+		/**
+		 * Singleton access
+		 */
 		public static final CountFunction INSTANCE = new CountFunction();
 
-		public CountFunction() {
+		protected CountFunction() {
 			super( "count", StandardBasicTypes.LONG );
 		}
 
 		@Override
 		public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) {
 			if ( arguments.size() > 1 ) {
 				if ( "distinct".equalsIgnoreCase( arguments.get( 0 ).toString() ) ) {
 					return renderCountDistinct( arguments );
 				}
 			}
 			return super.render( firstArgumentType, arguments, factory );
 		}
 
 		private String renderCountDistinct(List arguments) {
-			StringBuilder buffer = new StringBuilder();
+			final StringBuilder buffer = new StringBuilder();
 			buffer.append( "count(distinct " );
 			String sep = "";
-			Iterator itr = arguments.iterator();
-			itr.next(); // intentionally skip first
+			final Iterator itr = arguments.iterator();
+			// intentionally skip first
+			itr.next();
 			while ( itr.hasNext() ) {
-				buffer.append( sep )
-						.append( itr.next() );
+				buffer.append( sep ).append( itr.next() );
 				sep = ", ";
 			}
 			return buffer.append( ")" ).toString();
 		}
 	}
 
-
 	/**
 	 * Definition of a standard ANSI SQL compliant <tt>AVG</tt> function
 	 */
 	public static class AvgFunction extends StandardSQLFunction {
+		/**
+		 * Singleton access
+		 */
 		public static final AvgFunction INSTANCE = new AvgFunction();
 
-		public AvgFunction() {
+		protected AvgFunction() {
 			super( "avg", StandardBasicTypes.DOUBLE );
 		}
 
 		@Override
 		public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException {
-			int jdbcTypeCode = determineJdbcTypeCode( firstArgumentType, factory );
-			return render( jdbcTypeCode, arguments.get(0).toString(), factory );
+			final int jdbcTypeCode = determineJdbcTypeCode( firstArgumentType, factory );
+			return render( jdbcTypeCode, arguments.get( 0 ).toString(), factory );
 		}
 
 		protected final int determineJdbcTypeCode(Type firstArgumentType, SessionFactoryImplementor factory) throws QueryException {
 			try {
 				final int[] jdbcTypeCodes = firstArgumentType.sqlTypes( factory );
 				if ( jdbcTypeCodes.length != 1 ) {
 					throw new QueryException( "multiple-column type in avg()" );
 				}
 				return jdbcTypeCodes[0];
 			}
 			catch ( MappingException me ) {
 				throw new QueryException( me );
 			}
 		}
 
+		@SuppressWarnings("UnusedParameters")
 		protected String render(int firstArgumentJdbcType, String argument, SessionFactoryImplementor factory) {
 			return "avg(" + renderArgument( argument, firstArgumentJdbcType ) + ")";
 		}
 
 		protected String renderArgument(String argument, int firstArgumentJdbcType) {
 			return argument;
 		}
 	}
 
-
+	/**
+	 * Definition of a standard ANSI SQL compliant <tt>MAX</tt> function
+	 */
 	public static class MaxFunction extends StandardSQLFunction {
+		/**
+		 * Singleton access
+		 */
 		public static final MaxFunction INSTANCE = new MaxFunction();
 
-		public MaxFunction() {
+		protected MaxFunction() {
 			super( "max" );
 		}
 	}
 
+	/**
+	 * Definition of a standard ANSI SQL compliant <tt>MIN</tt> function
+	 */
 	public static class MinFunction extends StandardSQLFunction {
+		/**
+		 * Singleton access
+		 */
 		public static final MinFunction INSTANCE = new MinFunction();
 
-		public MinFunction() {
+		protected MinFunction() {
 			super( "min" );
 		}
 	}
 
 
+	/**
+	 * Definition of a standard ANSI SQL compliant <tt>SUM</tt> function
+	 */
 	public static class SumFunction extends StandardSQLFunction {
+		/**
+		 * Singleton access
+		 */
 		public static final SumFunction INSTANCE = new SumFunction();
 
-		public SumFunction() {
+		protected SumFunction() {
 			super( "sum" );
 		}
 
-		protected final int determineJdbcTypeCode(Type type, Mapping mapping) throws QueryException {
-			try {
-				final int[] jdbcTypeCodes = type.sqlTypes( mapping );
-				if ( jdbcTypeCodes.length != 1 ) {
-					throw new QueryException( "multiple-column type in sum()" );
-				}
-				return jdbcTypeCodes[0];
-			}
-			catch ( MappingException me ) {
-				throw new QueryException( me );
-			}
-		}
-
+		@Override
 		public Type getReturnType(Type firstArgumentType, Mapping mapping) {
 			final int jdbcType = determineJdbcTypeCode( firstArgumentType, mapping );
 
 			// First allow the actual type to control the return value; the underlying sqltype could
 			// actually be different
 			if ( firstArgumentType == StandardBasicTypes.BIG_INTEGER ) {
 				return StandardBasicTypes.BIG_INTEGER;
 			}
 			else if ( firstArgumentType == StandardBasicTypes.BIG_DECIMAL ) {
 				return StandardBasicTypes.BIG_DECIMAL;
 			}
 			else if ( firstArgumentType == StandardBasicTypes.LONG
 					|| firstArgumentType == StandardBasicTypes.SHORT
 					|| firstArgumentType == StandardBasicTypes.INTEGER ) {
 				return StandardBasicTypes.LONG;
 			}
 			else if ( firstArgumentType == StandardBasicTypes.FLOAT || firstArgumentType == StandardBasicTypes.DOUBLE)  {
 				return StandardBasicTypes.DOUBLE;
 			}
 
 			// finally use the jdbcType if == on Hibernate types did not find a match.
 			//
 			//	IMPL NOTE : we do not match on Types.NUMERIC because it could be either, so we fall-through to the
 			// 		first argument type
 			if ( jdbcType == Types.FLOAT
 					|| jdbcType == Types.DOUBLE
 					|| jdbcType == Types.DECIMAL
 					|| jdbcType == Types.REAL) {
 				return StandardBasicTypes.DOUBLE;
 			}
 			else if ( jdbcType == Types.BIGINT
 					|| jdbcType == Types.INTEGER
 					|| jdbcType == Types.SMALLINT
 					|| jdbcType == Types.TINYINT ) {
 				return StandardBasicTypes.LONG;
 			}
 
 			// as a last resort, return the type of the first argument
 			return firstArgumentType;
 		}
+
+		protected final int determineJdbcTypeCode(Type type, Mapping mapping) throws QueryException {
+			try {
+				final int[] jdbcTypeCodes = type.sqlTypes( mapping );
+				if ( jdbcTypeCodes.length != 1 ) {
+					throw new QueryException( "multiple-column type in sum()" );
+				}
+				return jdbcTypeCodes[0];
+			}
+			catch ( MappingException me ) {
+				throw new QueryException( me );
+			}
+		}
+
 	}
 
+	/**
+	 * Push the functions defined on StandardAnsiSqlAggregationFunctions into the given map
+	 *
+	 * @param functionMap The map of functions to push to
+	 */
 	public static void primeFunctionMap(Map<String, SQLFunction> functionMap) {
 		functionMap.put( AvgFunction.INSTANCE.getName(), AvgFunction.INSTANCE );
 		functionMap.put( CountFunction.INSTANCE.getName(), CountFunction.INSTANCE );
 		functionMap.put( MaxFunction.INSTANCE.getName(), MaxFunction.INSTANCE );
 		functionMap.put( MinFunction.INSTANCE.getName(), MinFunction.INSTANCE );
 		functionMap.put( SumFunction.INSTANCE.getName(), SumFunction.INSTANCE );
 	}
+
+	private StandardAnsiSqlAggregationFunctions() {
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardJDBCEscapeFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardJDBCEscapeFunction.java
index b37ce6183d..ec9794d8c1 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardJDBCEscapeFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardJDBCEscapeFunction.java
@@ -1,53 +1,57 @@
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
 package org.hibernate.dialect.function;
 import java.util.List;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Analogous to {@link org.hibernate.dialect.function.StandardSQLFunction}
  * except that standard JDBC escape sequences (i.e. {fn blah}) are used when
  * rendering the SQL.
  *
  * @author Steve Ebersole
  */
 public class StandardJDBCEscapeFunction extends StandardSQLFunction {
-	public StandardJDBCEscapeFunction(String name) {
-		super( name );
-	}
-
+	/**
+	 * Constructs a StandardJDBCEscapeFunction
+	 *
+	 * @param name The function name
+	 * @param typeValue The function return type
+	 */
 	public StandardJDBCEscapeFunction(String name, Type typeValue) {
 		super( name, typeValue );
 	}
 
+	@Override
 	public String render(Type argumentType, List args, SessionFactoryImplementor factory) {
 		return "{fn " + super.render( argumentType, args, factory ) + "}";
 	}
 
+	@Override
 	public String toString() {
 		return "{fn " + getName() + "...}";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardSQLFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardSQLFunction.java
index 56bcdb61ce..e8eccf530d 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardSQLFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardSQLFunction.java
@@ -1,127 +1,121 @@
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
 package org.hibernate.dialect.function;
+
 import java.util.List;
 
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Provides a standard implementation that supports the majority of the HQL
  * functions that are translated to SQL. The Dialect and its sub-classes use
  * this class to provide details required for processing of the associated
  * function.
  *
  * @author David Channon
  */
 public class StandardSQLFunction implements SQLFunction {
 	private final String name;
 	private final Type registeredType;
 
 	/**
 	 * Construct a standard SQL function definition with a variable return type;
 	 * the actual return type will depend on the types to which the function
 	 * is applied.
 	 * <p/>
 	 * Using this form, the return type is considered non-static and assumed
 	 * to be the type of the first argument.
 	 *
 	 * @param name The name of the function.
 	 */
 	public StandardSQLFunction(String name) {
 		this( name, null );
 	}
 
 	/**
 	 * Construct a standard SQL function definition with a static return type.
 	 *
 	 * @param name The name of the function.
 	 * @param registeredType The static return type.
 	 */
 	public StandardSQLFunction(String name, Type registeredType) {
 		this.name = name;
 		this.registeredType = registeredType;
 	}
 
 	/**
 	 * Function name accessor
 	 *
 	 * @return The function name.
 	 */
 	public String getName() {
 		return name;
 	}
 
 	/**
 	 * Function static return type accessor.
 	 *
 	 * @return The static function return type; or null if return type is
 	 * not static.
 	 */
 	public Type getType() {
 		return registeredType;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean hasArguments() {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean hasParenthesesIfNoArguments() {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type getReturnType(Type firstArgumentType, Mapping mapping) {
 		return registeredType == null ? firstArgumentType : registeredType;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor sessionFactory) {
-		StringBuilder buf = new StringBuilder();
+		final StringBuilder buf = new StringBuilder();
 		buf.append( name ).append( '(' );
 		for ( int i = 0; i < arguments.size(); i++ ) {
 			buf.append( arguments.get( i ) );
 			if ( i < arguments.size() - 1 ) {
 				buf.append( ", " );
 			}
 		}
 		return buf.append( ')' ).toString();
 	}
 
+	@Override
 	public String toString() {
 		return name;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/TemplateRenderer.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/TemplateRenderer.java
index d88f70cfe0..f8a64137c3 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/TemplateRenderer.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/TemplateRenderer.java
@@ -1,120 +1,135 @@
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
 package org.hibernate.dialect.function;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Delegate for handling function "templates".
  *
  * @author Steve Ebersole
  */
 public class TemplateRenderer {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TemplateRenderer.class.getName());
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			TemplateRenderer.class.getName()
+	);
 
 	private final String template;
 	private final String[] chunks;
 	private final int[] paramIndexes;
 
+	/**
+	 * Constructs a template renderer
+	 *
+	 * @param template The template
+	 */
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	public TemplateRenderer(String template) {
 		this.template = template;
 
-		List<String> chunkList = new ArrayList<String>();
-		List<Integer> paramList = new ArrayList<Integer>();
-		StringBuilder chunk = new StringBuilder( 10 );
-		StringBuilder index = new StringBuilder( 2 );
+		final List<String> chunkList = new ArrayList<String>();
+		final List<Integer> paramList = new ArrayList<Integer>();
+		final StringBuilder chunk = new StringBuilder( 10 );
+		final StringBuilder index = new StringBuilder( 2 );
 
 		for ( int i = 0; i < template.length(); ++i ) {
 			char c = template.charAt( i );
 			if ( c == '?' ) {
 				chunkList.add( chunk.toString() );
 				chunk.delete( 0, chunk.length() );
 
 				while ( ++i < template.length() ) {
 					c = template.charAt( i );
 					if ( Character.isDigit( c ) ) {
 						index.append( c );
 					}
 					else {
 						chunk.append( c );
 						break;
 					}
 				}
 
 				paramList.add( Integer.valueOf( index.toString() ) );
 				index.delete( 0, index.length() );
 			}
 			else {
 				chunk.append( c );
 			}
 		}
 
 		if ( chunk.length() > 0 ) {
 			chunkList.add( chunk.toString() );
 		}
 
 		chunks = chunkList.toArray( new String[chunkList.size()] );
 		paramIndexes = new int[paramList.size()];
 		for ( int i = 0; i < paramIndexes.length; ++i ) {
 			paramIndexes[i] = paramList.get( i );
 		}
 	}
 
 	public String getTemplate() {
 		return template;
 	}
 
 	public int getAnticipatedNumberOfArguments() {
 		return paramIndexes.length;
 	}
 
+	/**
+	 * The rendering code.
+	 *
+	 * @param args The arguments to inject into the template
+	 * @param factory The SessionFactory
+	 *
+	 * @return The rendered template with replacements
+	 */
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public String render(List args, SessionFactoryImplementor factory) {
-		int numberOfArguments = args.size();
+		final int numberOfArguments = args.size();
 		if ( getAnticipatedNumberOfArguments() > 0 && numberOfArguments != getAnticipatedNumberOfArguments() ) {
 			LOG.missingArguments( getAnticipatedNumberOfArguments(), numberOfArguments );
 		}
-		StringBuilder buf = new StringBuilder();
+		final StringBuilder buf = new StringBuilder();
 		for ( int i = 0; i < chunks.length; ++i ) {
 			if ( i < paramIndexes.length ) {
 				final int index = paramIndexes[i] - 1;
 				final Object arg =  index < numberOfArguments ? args.get( index ) : null;
 				if ( arg != null ) {
 					buf.append( chunks[i] ).append( arg );
 				}
 			}
 			else {
 				buf.append( chunks[i] );
 			}
 		}
 		return buf.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/TrimFunctionTemplate.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/TrimFunctionTemplate.java
index b82847616f..71f22477d9 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/TrimFunctionTemplate.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/TrimFunctionTemplate.java
@@ -1,147 +1,151 @@
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
 package org.hibernate.dialect.function;
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Defines the basic template support for <tt>TRIM</tt> functions
  *
  * @author Steve Ebersole
  */
 public abstract class TrimFunctionTemplate implements SQLFunction {
+	@Override
 	public boolean hasArguments() {
 		return true;
 	}
 
+	@Override
 	public boolean hasParenthesesIfNoArguments() {
 		return false;
 	}
 
+	@Override
 	public Type getReturnType(Type firstArgument, Mapping mapping) throws QueryException {
 		return StandardBasicTypes.STRING;
 	}
 
+	@Override
 	public String render(Type firstArgument, List args, SessionFactoryImplementor factory) throws QueryException {
 		final Options options = new Options();
 		final String trimSource;
 
 		if ( args.size() == 1 ) {
 			// we have the form: trim(trimSource)
-			trimSource = ( String ) args.get( 0 );
+			trimSource = (String) args.get( 0 );
 		}
-		else if ( "from".equalsIgnoreCase( ( String ) args.get( 0 ) ) ) {
+		else if ( "from".equalsIgnoreCase( (String) args.get( 0 ) ) ) {
 			// we have the form: trim(from trimSource).
 			//      This is functionally equivalent to trim(trimSource)
-			trimSource = ( String ) args.get( 1 );
+			trimSource = (String) args.get( 1 );
 		}
 		else {
 			// otherwise, a trim-specification and/or a trim-character
 			// have been specified;  we need to decide which options
 			// are present and "do the right thing"
 			//
 			// potentialTrimCharacterArgIndex = 1 assumes that a
 			// trim-specification has been specified.  we handle the
 			// exception to that explicitly
 			int potentialTrimCharacterArgIndex = 1;
-			String firstArg = ( String ) args.get( 0 );
+			final String firstArg = (String) args.get( 0 );
 			if ( "leading".equalsIgnoreCase( firstArg ) ) {
 				options.setTrimSpecification( Specification.LEADING );
 			}
 			else if ( "trailing".equalsIgnoreCase( firstArg ) ) {
 				options.setTrimSpecification( Specification.TRAILING );
 			}
 			else if ( "both".equalsIgnoreCase( firstArg ) ) {
 				// already the default in Options
 			}
 			else {
 				potentialTrimCharacterArgIndex = 0;
 			}
 
-			String potentialTrimCharacter = ( String ) args.get( potentialTrimCharacterArgIndex );
+			final String potentialTrimCharacter = (String) args.get( potentialTrimCharacterArgIndex );
 			if ( "from".equalsIgnoreCase( potentialTrimCharacter ) ) {
-				trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 1 );
+				trimSource = (String) args.get( potentialTrimCharacterArgIndex + 1 );
 			}
 			else if ( potentialTrimCharacterArgIndex + 1 >= args.size() ) {
 				trimSource = potentialTrimCharacter;
 			}
 			else {
 				options.setTrimCharacter( potentialTrimCharacter );
-				if ( "from".equalsIgnoreCase( ( String ) args.get( potentialTrimCharacterArgIndex + 1 ) ) ) {
-					trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 2 );
+				if ( "from".equalsIgnoreCase( (String) args.get( potentialTrimCharacterArgIndex + 1 ) ) ) {
+					trimSource = (String) args.get( potentialTrimCharacterArgIndex + 2 );
 				}
 				else {
-					trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 1 );
+					trimSource = (String) args.get( potentialTrimCharacterArgIndex + 1 );
 				}
 			}
 		}
 		return render( options, trimSource, factory );
 	}
 
 	protected abstract String render(Options options, String trimSource, SessionFactoryImplementor factory);
 
-	public static class Options {
+	protected static class Options {
 		public static final String DEFAULT_TRIM_CHARACTER = "' '";
 
 		private String trimCharacter = DEFAULT_TRIM_CHARACTER;
 		private Specification trimSpecification = Specification.BOTH;
 
 		public String getTrimCharacter() {
 			return trimCharacter;
 		}
 
 		public void setTrimCharacter(String trimCharacter) {
 			this.trimCharacter = trimCharacter;
 		}
 
 		public Specification getTrimSpecification() {
 			return trimSpecification;
 		}
 
 		public void setTrimSpecification(Specification trimSpecification) {
 			this.trimSpecification = trimSpecification;
 		}
 	}
 
-	public static class Specification {
+	protected static class Specification {
 		public static final Specification LEADING = new Specification( "leading" );
 		public static final Specification TRAILING = new Specification( "trailing" );
 		public static final Specification BOTH = new Specification( "both" );
 
 		private final String name;
 
 		private Specification(String name) {
 			this.name = name;
 		}
 
 		public String getName() {
 			return name;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/VarArgsSQLFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/VarArgsSQLFunction.java
index 3b894e9d0f..e12ab9c8ad 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/VarArgsSQLFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/VarArgsSQLFunction.java
@@ -1,122 +1,114 @@
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
 package org.hibernate.dialect.function;
+
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Support for slightly more general templating than {@link StandardSQLFunction}, with an unlimited number of arguments.
  *
  * @author Gavin King
  */
 public class VarArgsSQLFunction implements SQLFunction {
 	private final String begin;
 	private final String sep;
 	private final String end;
 	private final Type registeredType;
 
 	/**
 	 * Constructs a VarArgsSQLFunction instance with a 'static' return type.  An example of a 'static'
 	 * return type would be something like an <tt>UPPER</tt> function which is always returning
 	 * a SQL VARCHAR and thus a string type.
 	 *
 	 * @param registeredType The return type.
 	 * @param begin The beginning of the function templating.
 	 * @param sep The separator for each individual function argument.
 	 * @param end The end of the function templating.
 	 */
 	public VarArgsSQLFunction(Type registeredType, String begin, String sep, String end) {
 		this.registeredType = registeredType;
 		this.begin = begin;
 		this.sep = sep;
 		this.end = end;
 	}
 
 	/**
 	 * Constructs a VarArgsSQLFunction instance with a 'dynamic' return type.  For a dynamic return type,
 	 * the type of the arguments are used to resolve the type.  An example of a function with a
 	 * 'dynamic' return would be <tt>MAX</tt> or <tt>MIN</tt> which return a double or an integer etc
 	 * based on the types of the arguments.
 	 *
 	 * @param begin The beginning of the function templating.
 	 * @param sep The separator for each individual function argument.
 	 * @param end The end of the function templating.
 	 *
 	 * @see #getReturnType Specifically, the 'firstArgumentType' argument is the 'dynamic' type.
 	 */
 	public VarArgsSQLFunction(String begin, String sep, String end) {
 		this( null, begin, sep, end );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 * <p/>
-	 * Always returns true here.
-	 */
+	@Override
 	public boolean hasArguments() {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 * <p/>
-	 * Always returns true here.
-	 */
+	@Override
 	public boolean hasParenthesesIfNoArguments() {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
 		return registeredType == null ? firstArgumentType : registeredType;
 	}
 
+	@Override
 	public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) {
-		StringBuilder buf = new StringBuilder().append( begin );
+		final StringBuilder buf = new StringBuilder().append( begin );
 		for ( int i = 0; i < arguments.size(); i++ ) {
-			buf.append( transformArgument( ( String ) arguments.get( i ) ) );
+			buf.append( transformArgument( (String) arguments.get( i ) ) );
 			if ( i < arguments.size() - 1 ) {
 				buf.append( sep );
 			}
 		}
 		return buf.append( end ).toString();
 	}
 
 	/**
 	 * Called from {@link #render} to allow applying a change or transformation
 	 * to each individual argument.
 	 *
 	 * @param argument The argument being processed.
 	 * @return The transformed argument; may be the same, though should never be null.
 	 */
 	protected String transformArgument(String argument) {
 		return argument;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/AbstractSelectLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/AbstractSelectLockingStrategy.java
index daea92d3d4..b341232432 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/AbstractSelectLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/AbstractSelectLockingStrategy.java
@@ -1,88 +1,89 @@
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
 package org.hibernate.dialect.lock;
+
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.persister.entity.Lockable;
 
 /**
  * Base {@link LockingStrategy} implementation to support implementations
  * based on issuing <tt>SQL</tt> <tt>SELECT</tt> statements
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractSelectLockingStrategy implements LockingStrategy {
 	private final Lockable lockable;
 	private final LockMode lockMode;
 	private final String waitForeverSql;
 
 	protected AbstractSelectLockingStrategy(Lockable lockable, LockMode lockMode) {
 		this.lockable = lockable;
 		this.lockMode = lockMode;
 		this.waitForeverSql = generateLockString( LockOptions.WAIT_FOREVER );
 	}
 
 	protected Lockable getLockable() {
 		return lockable;
 	}
 
 	protected LockMode getLockMode() {
 		return lockMode;
 	}
 
 	protected abstract String generateLockString(int lockTimeout);
 
 	protected String determineSql(int timeout) {
 		if ( timeout == LockOptions.WAIT_FOREVER) {
 			return waitForeverSql;
 		}
 		else if ( timeout == LockOptions.NO_WAIT) {
 			return getNoWaitSql();
 		}
 		else if ( timeout == LockOptions.SKIP_LOCKED) {
 			return getSkipLockedSql();
 		}
 		else {
 			return generateLockString( timeout );
 		}
 	}
 
 	private String noWaitSql;
 
-	public String getNoWaitSql() {
+	protected String getNoWaitSql() {
 		if ( noWaitSql == null ) {
 			noWaitSql = generateLockString( LockOptions.NO_WAIT );
 		}
 		return noWaitSql;
 	}
 
 	private String skipLockedSql;
 
-	public String getSkipLockedSql() {
+	protected String getSkipLockedSql() {
 		if ( skipLockedSql == null ) {
 			skipLockedSql = generateLockString( LockOptions.SKIP_LOCKED );
 		}
 		return skipLockedSql;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/LockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/LockingStrategy.java
index 418f74a82f..2eb3ea6498 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/LockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/LockingStrategy.java
@@ -1,60 +1,61 @@
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
 package org.hibernate.dialect.lock;
 
 import java.io.Serializable;
 
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * A strategy abstraction for how locks are obtained in the underlying database.
  * <p/>
  * All locking provided implementations assume the underlying database supports
  * (and that the connection is in) at least read-committed transaction isolation.
  * The most glaring exclusion to this is HSQLDB which only offers support for
  * READ_UNCOMMITTED isolation.
  *
  * @see org.hibernate.dialect.Dialect#getLockingStrategy
  * @since 3.2
  *
  * @author Steve Ebersole
  */
 public interface LockingStrategy {
 	/**
 	 * Acquire an appropriate type of lock on the underlying data that will
 	 * endure until the end of the current transaction.
 	 *
 	 * @param id The id of the row to be locked
 	 * @param version The current version (or null if not versioned)
 	 * @param object The object logically being locked (currently not used)
 	 * @param timeout timeout in milliseconds, 0 = no wait, -1 = wait indefinitely
 	 * @param session The session from which the lock request originated
+	 *
 	 * @throws StaleObjectStateException Indicates an inability to locate the database row as part of acquiring
 	 * the requested lock.
 	 * @throws LockingStrategyException Indicates a failure in the lock attempt
 	 */
 	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session)
-	throws StaleObjectStateException, LockingStrategyException;
+			throws StaleObjectStateException, LockingStrategyException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/LockingStrategyException.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/LockingStrategyException.java
index 05a346d702..44963d6fb1 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/LockingStrategyException.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/LockingStrategyException.java
@@ -1,49 +1,62 @@
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
 package org.hibernate.dialect.lock;
 
 import org.hibernate.HibernateException;
 
 /**
  * Represents an error trying to apply a {@link LockingStrategy} to an entity
  *
  * @author Steve Ebersole
  */
 public abstract class LockingStrategyException extends HibernateException {
 	private final Object entity;
 
+	/**
+	 * Constructs a LockingStrategyException
+	 *
+	 * @param entity The entity we were trying to lock
+	 * @param message Message explaining the condition
+	 */
 	public LockingStrategyException(Object entity, String message) {
 		super( message );
 		this.entity = entity;
 	}
 
-	public LockingStrategyException(Object entity, String message, Throwable root) {
-		super( message, root );
+	/**
+	 * Constructs a LockingStrategyException
+	 *
+	 * @param entity The entity we were trying to lock
+	 * @param message Message explaining the condition
+	 * @param cause The underlying cause
+	 */
+	public LockingStrategyException(Object entity, String message, Throwable cause) {
+		super( message, cause );
 		this.entity = entity;
 	}
 
 	public Object getEntity() {
 		return entity;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticEntityLockException.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticEntityLockException.java
index a5a2552688..5aaf966e1a 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticEntityLockException.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticEntityLockException.java
@@ -1,39 +1,52 @@
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
 package org.hibernate.dialect.lock;
 
 /**
  * Represents an error trying to apply an optimistic {@link LockingStrategy} to an entity
  *
  * @author Steve Ebersole
  */
 public class OptimisticEntityLockException extends LockingStrategyException {
+	/**
+	 * Constructs a OptimisticEntityLockException
+	 *
+	 * @param entity The entity we were trying to lock
+	 * @param message Message explaining the condition
+	 */
 	public OptimisticEntityLockException(Object entity, String message) {
 		super( entity, message );
 	}
 
-	public OptimisticEntityLockException(Object entity, String message, Throwable root) {
-		super( entity, message, root );
+	/**
+	 * Constructs a OptimisticEntityLockException
+	 *
+	 * @param entity The entity we were trying to lock
+	 * @param message Message explaining the condition
+	 * @param cause The underlying cause
+	 */
+	public OptimisticEntityLockException(Object entity, String message, Throwable cause) {
+		super( entity, message, cause );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticForceIncrementLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticForceIncrementLockingStrategy.java
index 506b262254..07fadc153a 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticForceIncrementLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticForceIncrementLockingStrategy.java
@@ -1,78 +1,76 @@
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
 package org.hibernate.dialect.lock;
 
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.action.internal.EntityIncrementVersionProcess;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.persister.entity.Lockable;
 
 /**
  * An optimistic locking strategy that forces an increment of the version (after verifying that version hasn't changed).
  * This takes place just prior to transaction commit.
  * <p/>
  * This strategy is valid for LockMode.OPTIMISTIC_FORCE_INCREMENT
  *
  * @author Scott Marlow
  * @since 3.5
  */
 public class OptimisticForceIncrementLockingStrategy implements LockingStrategy {
 	private final Lockable lockable;
 	private final LockMode lockMode;
 
 	/**
 	 * Construct locking strategy.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indicates the type of lock to be acquired.
 	 */
 	public OptimisticForceIncrementLockingStrategy(Lockable lockable, LockMode lockMode) {
 		this.lockable = lockable;
 		this.lockMode = lockMode;
 		if ( lockMode.lessThan( LockMode.OPTIMISTIC_FORCE_INCREMENT ) ) {
 			throw new HibernateException( "[" + lockMode + "] not valid for [" + lockable.getEntityName() + "]" );
 		}
 	}
 
 	@Override
 	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
 		if ( !lockable.isVersioned() ) {
 			throw new HibernateException( "[" + lockMode + "] not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
 		}
-		EntityEntry entry = session.getPersistenceContext().getEntry( object );
-		EntityIncrementVersionProcess incrementVersion = new EntityIncrementVersionProcess( object, entry );
-		EventSource source = (EventSource) session;
-		// Register the EntityIncrementVersionProcess action to run just prior to transaction commit. 
-		source.getActionQueue().registerProcess( incrementVersion );
+		final EntityEntry entry = session.getPersistenceContext().getEntry( object );
+		// Register the EntityIncrementVersionProcess action to run just prior to transaction commit.
+		( (EventSource) session ).getActionQueue().registerProcess( new EntityIncrementVersionProcess( object, entry ) );
 	}
 
 	protected LockMode getLockMode() {
 		return lockMode;
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticLockingStrategy.java
index ecaa6d2e00..2b200526d5 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticLockingStrategy.java
@@ -1,79 +1,76 @@
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
 package org.hibernate.dialect.lock;
 
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.OptimisticLockException;
 import org.hibernate.action.internal.EntityVerifyVersionProcess;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.persister.entity.Lockable;
 
 /**
  * An optimistic locking strategy that verifies that the version hasn't changed (prior to transaction commit).
  * <p/>
  * This strategy is valid for LockMode.OPTIMISTIC
  *
  * @author Scott Marlow
  * @since 3.5
  */
 public class OptimisticLockingStrategy implements LockingStrategy {
-
 	private final Lockable lockable;
 	private final LockMode lockMode;
 
 	/**
 	 * Construct locking strategy.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indicates the type of lock to be acquired.
 	 */
 	public OptimisticLockingStrategy(Lockable lockable, LockMode lockMode) {
 		this.lockable = lockable;
 		this.lockMode = lockMode;
 		if ( lockMode.lessThan( LockMode.OPTIMISTIC ) ) {
 			throw new HibernateException( "[" + lockMode + "] not valid for [" + lockable.getEntityName() + "]" );
 		}
 	}
 
 	@Override
 	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
 		if ( !lockable.isVersioned() ) {
 			throw new OptimisticLockException( object, "[" + lockMode + "] not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
 		}
-		EntityEntry entry = session.getPersistenceContext().getEntry(object);
-		EventSource source = (EventSource)session;
-		EntityVerifyVersionProcess verifyVersion = new EntityVerifyVersionProcess(object, entry);
+		final EntityEntry entry = session.getPersistenceContext().getEntry(object);
 		// Register the EntityVerifyVersionProcess action to run just prior to transaction commit.
-		source.getActionQueue().registerProcess(verifyVersion);
+		( (EventSource) session ).getActionQueue().registerProcess( new EntityVerifyVersionProcess( object, entry ) );
 	}
 
 	protected LockMode getLockMode() {
 		return lockMode;
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticEntityLockException.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticEntityLockException.java
index 5929143ef7..2ce792d60a 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticEntityLockException.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticEntityLockException.java
@@ -1,37 +1,44 @@
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
 package org.hibernate.dialect.lock;
 
 import org.hibernate.JDBCException;
 
 /**
  * Represents an error trying to apply a pessimistic {@link LockingStrategy} to an entity
  *
  * @author Steve Ebersole
  */
 public class PessimisticEntityLockException extends LockingStrategyException {
-	public PessimisticEntityLockException(Object entity, String message, JDBCException root) {
-		super( entity, message, root );
+	/**
+	 * Constructs a PessimisticEntityLockException
+	 *
+	 * @param entity The entity we were trying to lock
+	 * @param message Message explaining the condition
+	 * @param cause The underlying cause
+	 */
+	public PessimisticEntityLockException(Object entity, String message, JDBCException cause) {
+		super( entity, message, cause );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticForceIncrementLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticForceIncrementLockingStrategy.java
index c74fde749f..1b743fbc96 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticForceIncrementLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticForceIncrementLockingStrategy.java
@@ -1,81 +1,81 @@
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
 package org.hibernate.dialect.lock;
 
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Lockable;
 
 /**
  * A pessimistic locking strategy that increments the version immediately (obtaining an exclusive write lock).
  * <p/>
  * This strategy is valid for LockMode.PESSIMISTIC_FORCE_INCREMENT
  *
  * @author Scott Marlow
  * @since 3.5
  */
 public class PessimisticForceIncrementLockingStrategy implements LockingStrategy {
 	private final Lockable lockable;
 	private final LockMode lockMode;
 
 	/**
 	 * Construct locking strategy.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indicates the type of lock to be acquired.
 	 */
 	public PessimisticForceIncrementLockingStrategy(Lockable lockable, LockMode lockMode) {
 		this.lockable = lockable;
 		this.lockMode = lockMode;
 		// ForceIncrement can be used for PESSIMISTIC_READ, PESSIMISTIC_WRITE or PESSIMISTIC_FORCE_INCREMENT
 		if ( lockMode.lessThan( LockMode.PESSIMISTIC_READ ) ) {
 			throw new HibernateException( "[" + lockMode + "] not valid for [" + lockable.getEntityName() + "]" );
 		}
 	}
 
 	@Override
 	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
 		if ( !lockable.isVersioned() ) {
 			throw new HibernateException( "[" + lockMode + "] not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
 		}
-		EntityEntry entry = session.getPersistenceContext().getEntry( object );
+		final EntityEntry entry = session.getPersistenceContext().getEntry( object );
 		final EntityPersister persister = entry.getPersister();
-		Object nextVersion = persister.forceVersionIncrement( entry.getId(), entry.getVersion(), session );
+		final Object nextVersion = persister.forceVersionIncrement( entry.getId(), entry.getVersion(), session );
 		entry.forceLocked( object, nextVersion );
 	}
 
 	/**
 	 * Retrieve the specific lock mode defined.
 	 *
 	 * @return The specific lock mode.
 	 */
 	protected LockMode getLockMode() {
 		return lockMode;
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java
index d12a80cece..5f52a37ddb 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java
@@ -1,137 +1,137 @@
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
 package org.hibernate.dialect.lock;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.SimpleSelect;
 
 /**
  * A pessimistic locking strategy where the locks are obtained through select statements.
  * <p/>
  * For non-read locks, this is achieved through the Dialect's specific
  * SELECT ... FOR UPDATE syntax.
  *
  * This strategy is valid for LockMode.PESSIMISTIC_READ
  *
  * This class is a clone of SelectLockingStrategy.
  *
  * @author Steve Ebersole
  * @author Scott Marlow
  *
  * @see org.hibernate.dialect.Dialect#getForUpdateString(org.hibernate.LockMode)
  * @see org.hibernate.dialect.Dialect#appendLockHint(org.hibernate.LockMode, String)
  *
  * @since 3.5
  */
 public class PessimisticReadSelectLockingStrategy extends AbstractSelectLockingStrategy {
 	/**
 	 * Construct a locking strategy based on SQL SELECT statements.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indicates the type of lock to be acquired.
 	 */
 	public PessimisticReadSelectLockingStrategy(Lockable lockable, LockMode lockMode) {
 		super( lockable, lockMode );
 	}
 
 	@Override
 	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
 		final String sql = determineSql( timeout );
-		SessionFactoryImplementor factory = session.getFactory();
+		final SessionFactoryImplementor factory = session.getFactory();
 		try {
 			try {
-				PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
+				final PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
 				try {
 					getLockable().getIdentifierType().nullSafeSet( st, id, 1, session );
 					if ( getLockable().isVersioned() ) {
 						getLockable().getVersionType().nullSafeSet(
 								st,
 								version,
 								getLockable().getIdentifierType().getColumnSpan( factory ) + 1,
 								session
 						);
 					}
 
-					ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
+					final ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 					try {
 						if ( !rs.next() ) {
 							if ( factory.getStatistics().isStatisticsEnabled() ) {
 								factory.getStatisticsImplementor()
 										.optimisticFailure( getLockable().getEntityName() );
 							}
 							throw new StaleObjectStateException( getLockable().getEntityName(), id );
 						}
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
 				throw session.getFactory().getSQLExceptionHelper().convert(
 						e,
 						"could not lock: " + MessageHelper.infoString( getLockable(), id, session.getFactory() ),
 						sql
 				);
 			}
 		}
 		catch (JDBCException e) {
 			throw new PessimisticEntityLockException( object, "could not obtain pessimistic lock", e );
 		}
 	}
 
 	protected String generateLockString(int lockTimeout) {
-		SessionFactoryImplementor factory = getLockable().getFactory();
-		LockOptions lockOptions = new LockOptions( getLockMode() );
+		final SessionFactoryImplementor factory = getLockable().getFactory();
+		final LockOptions lockOptions = new LockOptions( getLockMode() );
 		lockOptions.setTimeOut( lockTimeout );
-		SimpleSelect select = new SimpleSelect( factory.getDialect() )
+		final SimpleSelect select = new SimpleSelect( factory.getDialect() )
 				.setLockOptions( lockOptions )
 				.setTableName( getLockable().getRootTableName() )
 				.addColumn( getLockable().getRootTableIdentifierColumnNames()[0] )
 				.addCondition( getLockable().getRootTableIdentifierColumnNames(), "=?" );
 		if ( getLockable().isVersioned() ) {
 			select.addCondition( getLockable().getVersionColumnName(), "=?" );
 		}
 		if ( factory.getSettings().isCommentsEnabled() ) {
 			select.setComment( getLockMode() + " lock " + getLockable().getEntityName() );
 		}
 		return select.toStatementString();
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java
index 435defc92d..fba6b93853 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java
@@ -1,150 +1,151 @@
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
 package org.hibernate.dialect.lock;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Update;
 
 /**
  * A pessimistic locking strategy where the locks are obtained through update statements.
  * <p/>
  * This strategy is valid for LockMode.PESSIMISTIC_READ
  *
  * This class is a clone of UpdateLockingStrategy.
  *
  * @author Steve Ebersole
  * @author Scott Marlow
  * @since 3.5
  */
 public class PessimisticReadUpdateLockingStrategy implements LockingStrategy {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			PessimisticReadUpdateLockingStrategy.class.getName()
 	);
 
 	private final Lockable lockable;
 	private final LockMode lockMode;
 	private final String sql;
 
 	/**
 	 * Construct a locking strategy based on SQL UPDATE statements.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indicates the type of lock to be acquired.  Note that
 	 * read-locks are not valid for this strategy.
 	 */
 	public PessimisticReadUpdateLockingStrategy(Lockable lockable, LockMode lockMode) {
 		this.lockable = lockable;
 		this.lockMode = lockMode;
 		if ( lockMode.lessThan( LockMode.PESSIMISTIC_READ ) ) {
 			throw new HibernateException( "[" + lockMode + "] not valid for update statement" );
 		}
 		if ( !lockable.isVersioned() ) {
 			LOG.writeLocksNotSupported( lockable.getEntityName() );
 			this.sql = null;
 		}
 		else {
 			this.sql = generateLockString();
 		}
 	}
 
 	@Override
 	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
 		if ( !lockable.isVersioned() ) {
 			throw new HibernateException( "write locks via update not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
 		}
-		SessionFactoryImplementor factory = session.getFactory();
+
+		final SessionFactoryImplementor factory = session.getFactory();
 		try {
 			try {
-				PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
+				final PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
 				try {
 					lockable.getVersionType().nullSafeSet( st, version, 1, session );
 					int offset = 2;
 
 					lockable.getIdentifierType().nullSafeSet( st, id, offset, session );
 					offset += lockable.getIdentifierType().getColumnSpan( factory );
 
 					if ( lockable.isVersioned() ) {
 						lockable.getVersionType().nullSafeSet( st, version, offset, session );
 					}
 
-					int affected = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st );
-					if ( affected < 0 ) {  // todo:  should this instead check for exactly one row modified?
+					final int affected = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st );
+					// todo:  should this instead check for exactly one row modified?
+					if ( affected < 0 ) {
 						if (factory.getStatistics().isStatisticsEnabled()) {
 							factory.getStatisticsImplementor().optimisticFailure( lockable.getEntityName() );
 						}
 						throw new StaleObjectStateException( lockable.getEntityName(), id );
 					}
 
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 				}
 
 			}
 			catch ( SQLException e ) {
 				throw session.getFactory().getSQLExceptionHelper().convert(
 						e,
 						"could not lock: " + MessageHelper.infoString( lockable, id, session.getFactory() ),
 						sql
 				);
 			}
 		}
 		catch (JDBCException e) {
 			throw new PessimisticEntityLockException( object, "could not obtain pessimistic lock", e );
 		}
 	}
 
 	protected String generateLockString() {
-		SessionFactoryImplementor factory = lockable.getFactory();
-		Update update = new Update( factory.getDialect() );
+		final SessionFactoryImplementor factory = lockable.getFactory();
+		final Update update = new Update( factory.getDialect() );
 		update.setTableName( lockable.getRootTableName() );
 		update.addPrimaryKeyColumns( lockable.getRootTableIdentifierColumnNames() );
 		update.setVersionColumnName( lockable.getVersionColumnName() );
 		update.addColumn( lockable.getVersionColumnName() );
 		if ( factory.getSettings().isCommentsEnabled() ) {
 			update.setComment( lockMode + " lock " + lockable.getEntityName() );
 		}
 		return update.toStatementString();
 	}
 
 	protected LockMode getLockMode() {
 		return lockMode;
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java
index d810aa2eec..e5267a10f4 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java
@@ -1,135 +1,135 @@
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
 package org.hibernate.dialect.lock;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.SimpleSelect;
 
 /**
  * A pessimistic locking strategy where the locks are obtained through select statements.
  * <p/>
  * For non-read locks, this is achieved through the Dialect's specific
  * SELECT ... FOR UPDATE syntax.
  *
  * This strategy is valid for LockMode.PESSIMISTIC_WRITE
  *
  * This class is a clone of SelectLockingStrategy.
  *
  * @see org.hibernate.dialect.Dialect#getForUpdateString(org.hibernate.LockMode)
  * @see org.hibernate.dialect.Dialect#appendLockHint(org.hibernate.LockMode, String)
  *
  * @author Steve Ebersole
  * @author Scott Marlow
  * @since 3.5
  */
 public class PessimisticWriteSelectLockingStrategy extends AbstractSelectLockingStrategy {
 	/**
 	 * Construct a locking strategy based on SQL SELECT statements.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indicates the type of lock to be acquired.
 	 */
 	public PessimisticWriteSelectLockingStrategy(Lockable lockable, LockMode lockMode) {
 		super( lockable, lockMode );
 	}
 
 	@Override
 	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
 		final String sql = determineSql( timeout );
-		SessionFactoryImplementor factory = session.getFactory();
+		final SessionFactoryImplementor factory = session.getFactory();
 		try {
 			try {
-				PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
+				final PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
 				try {
 					getLockable().getIdentifierType().nullSafeSet( st, id, 1, session );
 					if ( getLockable().isVersioned() ) {
 						getLockable().getVersionType().nullSafeSet(
 								st,
 								version,
 								getLockable().getIdentifierType().getColumnSpan( factory ) + 1,
 								session
 						);
 					}
 
-					ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
+					final ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 					try {
 						if ( !rs.next() ) {
 							if ( factory.getStatistics().isStatisticsEnabled() ) {
 								factory.getStatisticsImplementor()
 										.optimisticFailure( getLockable().getEntityName() );
 							}
 							throw new StaleObjectStateException( getLockable().getEntityName(), id );
 						}
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
 				throw session.getFactory().getSQLExceptionHelper().convert(
 						e,
 						"could not lock: " + MessageHelper.infoString( getLockable(), id, session.getFactory() ),
 						sql
 				);
 			}
 		}
 		catch (JDBCException e) {
 			throw new PessimisticEntityLockException( object, "could not obtain pessimistic lock", e );
 		}
 	}
 
 	protected String generateLockString(int lockTimeout) {
-		SessionFactoryImplementor factory = getLockable().getFactory();
-		LockOptions lockOptions = new LockOptions( getLockMode() );
+		final SessionFactoryImplementor factory = getLockable().getFactory();
+		final LockOptions lockOptions = new LockOptions( getLockMode() );
 		lockOptions.setTimeOut( lockTimeout );
-		SimpleSelect select = new SimpleSelect( factory.getDialect() )
+		final SimpleSelect select = new SimpleSelect( factory.getDialect() )
 				.setLockOptions( lockOptions )
 				.setTableName( getLockable().getRootTableName() )
 				.addColumn( getLockable().getRootTableIdentifierColumnNames()[0] )
 				.addCondition( getLockable().getRootTableIdentifierColumnNames(), "=?" );
 		if ( getLockable().isVersioned() ) {
 			select.addCondition( getLockable().getVersionColumnName(), "=?" );
 		}
 		if ( factory.getSettings().isCommentsEnabled() ) {
 			select.setComment( getLockMode() + " lock " + getLockable().getEntityName() );
 		}
 		return select.toStatementString();
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java
index 3f47ffa176..184d23c823 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java
@@ -1,148 +1,149 @@
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
 package org.hibernate.dialect.lock;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Update;
 
 /**
  * A pessimistic locking strategy where the locks are obtained through update statements.
  * <p/>
  * This strategy is valid for LockMode.PESSIMISTIC_WRITE
  *
  * This class is a clone of UpdateLockingStrategy.
  *
  * @author Steve Ebersole
  * @author Scott Marlow
  * @since 3.5
  */
 public class PessimisticWriteUpdateLockingStrategy implements LockingStrategy {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			PessimisticWriteUpdateLockingStrategy.class.getName()
 	);
 
 	private final Lockable lockable;
 	private final LockMode lockMode;
 	private final String sql;
 
 	/**
 	 * Construct a locking strategy based on SQL UPDATE statements.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indicates the type of lock to be acquired.  Note that read-locks are not valid for this strategy.
 	 */
 	public PessimisticWriteUpdateLockingStrategy(Lockable lockable, LockMode lockMode) {
 		this.lockable = lockable;
 		this.lockMode = lockMode;
 		if ( lockMode.lessThan( LockMode.PESSIMISTIC_READ ) ) {
 			throw new HibernateException( "[" + lockMode + "] not valid for update statement" );
 		}
 		if ( !lockable.isVersioned() ) {
 			LOG.writeLocksNotSupported( lockable.getEntityName() );
 			this.sql = null;
 		}
 		else {
 			this.sql = generateLockString();
 		}
 	}
 
 	@Override
 	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
 		if ( !lockable.isVersioned() ) {
 			throw new HibernateException( "write locks via update not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
 		}
-		SessionFactoryImplementor factory = session.getFactory();
+
+		final SessionFactoryImplementor factory = session.getFactory();
 		try {
 			try {
-				PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
+				final PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
 				try {
 					lockable.getVersionType().nullSafeSet( st, version, 1, session );
 					int offset = 2;
 
 					lockable.getIdentifierType().nullSafeSet( st, id, offset, session );
 					offset += lockable.getIdentifierType().getColumnSpan( factory );
 
 					if ( lockable.isVersioned() ) {
 						lockable.getVersionType().nullSafeSet( st, version, offset, session );
 					}
 
-					int affected = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st );
-					if ( affected < 0 ) {  // todo:  should this instead check for exactly one row modified?
+					final int affected = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st );
+					// todo:  should this instead check for exactly one row modified?
+					if ( affected < 0 ) {
 						if (factory.getStatistics().isStatisticsEnabled()) {
 							factory.getStatisticsImplementor().optimisticFailure( lockable.getEntityName() );
 						}
 						throw new StaleObjectStateException( lockable.getEntityName(), id );
 					}
 
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 				}
 			}
 			catch ( SQLException e ) {
 				throw session.getFactory().getSQLExceptionHelper().convert(
 						e,
 						"could not lock: " + MessageHelper.infoString( lockable, id, session.getFactory() ),
 						sql
 				);
 			}
 		}
 		catch (JDBCException e) {
 			throw new PessimisticEntityLockException( object, "could not obtain pessimistic lock", e );
 		}
 	}
 
 	protected String generateLockString() {
-		SessionFactoryImplementor factory = lockable.getFactory();
-		Update update = new Update( factory.getDialect() );
+		final SessionFactoryImplementor factory = lockable.getFactory();
+		final Update update = new Update( factory.getDialect() );
 		update.setTableName( lockable.getRootTableName() );
 		update.addPrimaryKeyColumns( lockable.getRootTableIdentifierColumnNames() );
 		update.setVersionColumnName( lockable.getVersionColumnName() );
 		update.addColumn( lockable.getVersionColumnName() );
 		if ( factory.getSettings().isCommentsEnabled() ) {
 			update.setComment( lockMode + " lock " + lockable.getEntityName() );
 		}
 		return update.toStatementString();
 	}
 
 	protected LockMode getLockMode() {
 		return lockMode;
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java
index 8e44214a99..eaca2bca66 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java
@@ -1,133 +1,131 @@
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
 package org.hibernate.dialect.lock;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.SimpleSelect;
 
 /**
  * A locking strategy where the locks are obtained through select statements.
  * <p/>
  * For non-read locks, this is achieved through the Dialect's specific
  * SELECT ... FOR UPDATE syntax.
  *
  * @see org.hibernate.dialect.Dialect#getForUpdateString(org.hibernate.LockMode)
  * @see org.hibernate.dialect.Dialect#appendLockHint(org.hibernate.LockMode, String)
  *
  * @author Steve Ebersole
  * @since 3.2
  */
 public class SelectLockingStrategy extends AbstractSelectLockingStrategy {
 	/**
 	 * Construct a locking strategy based on SQL SELECT statements.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indictates the type of lock to be acquired.
 	 */
 	public SelectLockingStrategy(Lockable lockable, LockMode lockMode) {
 		super( lockable, lockMode );
 	}
 
-	/**
-	 * @see LockingStrategy#lock
-	 */
+	@Override
 	public void lock(
-	        Serializable id,
-	        Object version,
-	        Object object,
-	        int timeout, 
-	        SessionImplementor session) throws StaleObjectStateException, JDBCException {
+			Serializable id,
+			Object version,
+			Object object,
+			int timeout,
+			SessionImplementor session) throws StaleObjectStateException, JDBCException {
 		final String sql = determineSql( timeout );
-		SessionFactoryImplementor factory = session.getFactory();
+		final SessionFactoryImplementor factory = session.getFactory();
 		try {
-			PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
+			final PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
 			try {
 				getLockable().getIdentifierType().nullSafeSet( st, id, 1, session );
 				if ( getLockable().isVersioned() ) {
 					getLockable().getVersionType().nullSafeSet(
 							st,
 							version,
 							getLockable().getIdentifierType().getColumnSpan( factory ) + 1,
 							session
 					);
 				}
 
-				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
+				final ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					if ( !rs.next() ) {
 						if ( factory.getStatistics().isStatisticsEnabled() ) {
 							factory.getStatisticsImplementor()
 									.optimisticFailure( getLockable().getEntityName() );
 						}
 						throw new StaleObjectStateException( getLockable().getEntityName(), id );
 					}
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not lock: " + MessageHelper.infoString( getLockable(), id, session.getFactory() ),
 					sql
 				);
 		}
 	}
 
 	protected String generateLockString(int timeout) {
-		SessionFactoryImplementor factory = getLockable().getFactory();
-		LockOptions lockOptions = new LockOptions( getLockMode() );
+		final SessionFactoryImplementor factory = getLockable().getFactory();
+		final LockOptions lockOptions = new LockOptions( getLockMode() );
 		lockOptions.setTimeOut( timeout );
-		SimpleSelect select = new SimpleSelect( factory.getDialect() )
+		final SimpleSelect select = new SimpleSelect( factory.getDialect() )
 				.setLockOptions( lockOptions )
 				.setTableName( getLockable().getRootTableName() )
 				.addColumn( getLockable().getRootTableIdentifierColumnNames()[0] )
 				.addCondition( getLockable().getRootTableIdentifierColumnNames(), "=?" );
 		if ( getLockable().isVersioned() ) {
 			select.addCondition( getLockable().getVersionColumnName(), "=?" );
 		}
 		if ( factory.getSettings().isCommentsEnabled() ) {
 			select.setComment( getLockMode() + " lock " + getLockable().getEntityName() );
 		}
 		return select.toStatementString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java
index 5e8fdfe263..038773b962 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java
@@ -1,148 +1,148 @@
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
 package org.hibernate.dialect.lock;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Update;
 
 /**
  * A locking strategy where the locks are obtained through update statements.
  * <p/>
  * This strategy is not valid for read style locks.
  *
  * @author Steve Ebersole
  * @since 3.2
  */
 public class UpdateLockingStrategy implements LockingStrategy {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			UpdateLockingStrategy.class.getName()
 	);
 
 	private final Lockable lockable;
 	private final LockMode lockMode;
 	private final String sql;
 
 	/**
 	 * Construct a locking strategy based on SQL UPDATE statements.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indictates the type of lock to be acquired.  Note that
 	 * read-locks are not valid for this strategy.
 	 */
 	public UpdateLockingStrategy(Lockable lockable, LockMode lockMode) {
 		this.lockable = lockable;
 		this.lockMode = lockMode;
 		if ( lockMode.lessThan( LockMode.UPGRADE ) ) {
 			throw new HibernateException( "[" + lockMode + "] not valid for update statement" );
 		}
 		if ( !lockable.isVersioned() ) {
 			LOG.writeLocksNotSupported( lockable.getEntityName() );
 			this.sql = null;
 		}
 		else {
 			this.sql = generateLockString();
 		}
 	}
 
 	@Override
 	public void lock(
-	        Serializable id,
-	        Object version,
-	        Object object,
-	        int timeout,
-	        SessionImplementor session) throws StaleObjectStateException, JDBCException {
+			Serializable id,
+			Object version,
+			Object object,
+			int timeout,
+			SessionImplementor session) throws StaleObjectStateException, JDBCException {
 		if ( !lockable.isVersioned() ) {
 			throw new HibernateException( "write locks via update not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
 		}
+
 		// todo : should we additionally check the current isolation mode explicitly?
-		SessionFactoryImplementor factory = session.getFactory();
+		final SessionFactoryImplementor factory = session.getFactory();
 		try {
-			PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
+			final PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
 			try {
 				lockable.getVersionType().nullSafeSet( st, version, 1, session );
 				int offset = 2;
 
 				lockable.getIdentifierType().nullSafeSet( st, id, offset, session );
 				offset += lockable.getIdentifierType().getColumnSpan( factory );
 
 				if ( lockable.isVersioned() ) {
 					lockable.getVersionType().nullSafeSet( st, version, offset, session );
 				}
 
-				int affected = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st );
+				final int affected = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st );
 				if ( affected < 0 ) {
 					if (factory.getStatistics().isStatisticsEnabled()) {
 						factory.getStatisticsImplementor().optimisticFailure( lockable.getEntityName() );
 					}
 					throw new StaleObjectStateException( lockable.getEntityName(), id );
 				}
 
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
-			        sqle,
-			        "could not lock: " + MessageHelper.infoString( lockable, id, session.getFactory() ),
-			        sql
+					sqle,
+					"could not lock: " + MessageHelper.infoString( lockable, id, session.getFactory() ),
+					sql
 			);
 		}
 	}
 
 	protected String generateLockString() {
-		SessionFactoryImplementor factory = lockable.getFactory();
-		Update update = new Update( factory.getDialect() );
+		final SessionFactoryImplementor factory = lockable.getFactory();
+		final Update update = new Update( factory.getDialect() );
 		update.setTableName( lockable.getRootTableName() );
 		update.addPrimaryKeyColumns( lockable.getRootTableIdentifierColumnNames() );
 		update.setVersionColumnName( lockable.getVersionColumnName() );
 		update.addColumn( lockable.getVersionColumnName() );
 		if ( factory.getSettings().isCommentsEnabled() ) {
 			update.setComment( lockMode + " lock " + lockable.getEntityName() );
 		}
 		return update.toStatementString();
 	}
 
 	protected LockMode getLockMode() {
 		return lockMode;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/package-info.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/package-info.java
new file mode 100644
index 0000000000..370f427861
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Support for Dialect-specific locking strategies
+ */
+package org.hibernate.dialect.lock;
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/AbstractLimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/AbstractLimitHandler.java
index f517b46482..8b80ea170b 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/AbstractLimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/AbstractLimitHandler.java
@@ -1,168 +1,174 @@
 package org.hibernate.dialect.pagination;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.engine.spi.RowSelection;
 
 /**
  * Default implementation of {@link LimitHandler} interface. 
  *
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public abstract class AbstractLimitHandler implements LimitHandler {
 	protected final String sql;
 	protected final RowSelection selection;
 
 	/**
 	 * Default constructor. SQL query and selection criteria required to allow LIMIT clause pre-processing.
 	 *
 	 * @param sql SQL query.
 	 * @param selection Selection criteria. {@code null} in case of unlimited number of rows.
 	 */
 	public AbstractLimitHandler(String sql, RowSelection selection) {
 		this.sql = sql;
 		this.selection = selection;
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return false;
 	}
 
+	@Override
 	public boolean supportsLimitOffset() {
 		return supportsLimit();
 	}
 
 	/**
 	 * Does this handler support bind variables (i.e., prepared statement
 	 * parameters) for its limit/offset?
 	 *
 	 * @return True if bind variables can be used; false otherwise.
 	 */
 	public boolean supportsVariableLimit() {
 		return supportsLimit();
 	}
 
 	/**
 	 * ANSI SQL defines the LIMIT clause to be in the form LIMIT offset, limit.
 	 * Does this dialect require us to bind the parameters in reverse order?
 	 *
 	 * @return true if the correct order is limit, offset
 	 */
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause come at the start of the
 	 * <tt>SELECT</tt> statement, rather than at the end?
 	 *
 	 * @return true if limit parameters should come before other parameters
 	 */
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause take a "maximum" row number instead
 	 * of a total number of returned rows?
 	 * <p/>
 	 * This is easiest understood via an example.  Consider you have a table
 	 * with 20 rows, but you only want to retrieve rows number 11 through 20.
 	 * Generally, a limit with offset would say that the offset = 11 and the
 	 * limit = 10 (we only want 10 rows at a time); this is specifying the
 	 * total number of returned rows.  Some dialects require that we instead
 	 * specify offset = 11 and limit = 20, where 20 is the "last" row we want
 	 * relative to offset (i.e. total number of rows = 20 - 11 = 9)
 	 * <p/>
 	 * So essentially, is limit relative from offset?  Or is limit absolute?
 	 *
 	 * @return True if limit is relative from offset; false otherwise.
 	 */
 	public boolean useMaxForLimit() {
 		return false;
 	}
 
 	/**
 	 * Generally, if there is no limit applied to a Hibernate query we do not apply any limits
 	 * to the SQL query.  This option forces that the limit be written to the SQL query.
 	 *
 	 * @return True to force limit into SQL query even if none specified in Hibernate query; false otherwise.
 	 */
 	public boolean forceLimitUsage() {
 		return false;
 	}
 
 	/**
 	 * Hibernate APIs explicitly state that setFirstResult() should be a zero-based offset. Here we allow the
 	 * Dialect a chance to convert that value based on what the underlying db or driver will expect.
 	 * <p/>
 	 * NOTE: what gets passed into {@link #AbstractLimitHandler(String, RowSelection)} is the zero-based offset.
 	 * Dialects which do not {@link #supportsVariableLimit} should take care to perform any needed first-row-conversion
 	 * calls prior to injecting the limit values into the SQL string.
 	 *
 	 * @param zeroBasedFirstResult The user-supplied, zero-based first row offset.
 	 *
 	 * @return The corresponding db/dialect specific offset.
 	 *
 	 * @see org.hibernate.Query#setFirstResult
 	 * @see org.hibernate.Criteria#setFirstResult
 	 */
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
+	@Override
 	public String getProcessedSql() {
 		throw new UnsupportedOperationException( "Paged queries not supported by " + getClass().getName() );
 	}
 
+	@Override
 	public int bindLimitParametersAtStartOfQuery(PreparedStatement statement, int index)
 			throws SQLException {
 		return bindLimitParametersFirst() ? bindLimitParameters( statement, index ) : 0;
 	}
 
+	@Override
 	public int bindLimitParametersAtEndOfQuery(PreparedStatement statement, int index)
 			throws SQLException {
 		return !bindLimitParametersFirst() ? bindLimitParameters( statement, index ) : 0;
 	}
 
+	@Override
 	public void setMaxRows(PreparedStatement statement) throws SQLException {
 	}
 
 	/**
 	 * Default implementation of binding parameter values needed by the LIMIT clause.
 	 *
 	 * @param statement Statement to which to bind limit parameter values.
 	 * @param index Index from which to start binding.
 	 * @return The number of parameter values bound.
 	 * @throws SQLException Indicates problems binding parameter values.
 	 */
 	protected int bindLimitParameters(PreparedStatement statement, int index)
 			throws SQLException {
 		if ( !supportsVariableLimit() || !LimitHelper.hasMaxRows( selection ) ) {
 			return 0;
 		}
-		int firstRow = convertToFirstRowValue( LimitHelper.getFirstRow( selection ) );
-		int lastRow = getMaxOrLimit();
-		boolean hasFirstRow = supportsLimitOffset() && ( firstRow > 0 || forceLimitUsage() );
-		boolean reverse = bindLimitParametersInReverseOrder();
+		final int firstRow = convertToFirstRowValue( LimitHelper.getFirstRow( selection ) );
+		final int lastRow = getMaxOrLimit();
+		final boolean hasFirstRow = supportsLimitOffset() && ( firstRow > 0 || forceLimitUsage() );
+		final boolean reverse = bindLimitParametersInReverseOrder();
 		if ( hasFirstRow ) {
 			statement.setInt( index + ( reverse ? 1 : 0 ), firstRow );
 		}
 		statement.setInt( index + ( reverse || !hasFirstRow ? 0 : 1 ), lastRow );
 		return hasFirstRow ? 2 : 1;
 	}
 
 	/**
 	 * Some dialect-specific LIMIT clauses require the maximum last row number
 	 * (aka, first_row_number + total_row_count), while others require the maximum
 	 * returned row count (the total maximum number of rows to return).
 	 *
 	 * @return The appropriate value to bind into the limit clause.
 	 */
 	protected int getMaxOrLimit() {
 		final int firstRow = convertToFirstRowValue( LimitHelper.getFirstRow( selection ) );
 		final int lastRow = selection.getMaxRows();
 		return useMaxForLimit() ? lastRow + firstRow : lastRow;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/CUBRIDLimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/CUBRIDLimitHandler.java
index 4ee34f42ae..c26db45396 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/CUBRIDLimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/CUBRIDLimitHandler.java
@@ -1,37 +1,69 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.dialect.pagination;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.RowSelection;
 
 /**
- * Limit handler that delegates all operations to the underlying dialect.
+ * Limit handler for CUBRID
  *
  * @author Esen Sagynov (kadishmal at gmail dot com)
  */
 public class CUBRIDLimitHandler extends AbstractLimitHandler {
+	@SuppressWarnings("FieldCanBeLocal")
 	private final Dialect dialect;
 
+	/**
+	 * Constructs a CUBRIDLimitHandler
+	 *
+	 * @param dialect Currently not used
+	 * @param sql The SQL
+	 * @param selection The row selection options
+	 */
 	public CUBRIDLimitHandler(Dialect dialect, String sql, RowSelection selection) {
 		super( sql, selection );
 		this.dialect = dialect;
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
+	@Override
 	public String getProcessedSql() {
-		if (LimitHelper.useLimit(this, selection)) {
+		if ( LimitHelper.useLimit( this, selection ) ) {
 			// useLimitOffset: whether "offset" is set or not;
 			// if set, use "LIMIT offset, row_count" syntax;
 			// if not, use "LIMIT row_count"
-			boolean useLimitOffset = LimitHelper.hasFirstRow(selection);
-
-			return new StringBuilder(sql.length() + 20).append(sql)
-							.append(useLimitOffset ? " limit ?, ?" : " limit ?").toString();
+			final boolean useLimitOffset = LimitHelper.hasFirstRow( selection );
+			return sql + (useLimitOffset ? " limit ?, ?" : " limit ?");
 		}
 		else {
-			return sql; // or return unaltered SQL
+			// or return unaltered SQL
+			return sql;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LegacyLimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LegacyLimitHandler.java
index e3545177d7..dceba667bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LegacyLimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LegacyLimitHandler.java
@@ -1,58 +1,98 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.dialect.pagination;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.RowSelection;
 
 /**
  * Limit handler that delegates all operations to the underlying dialect.
  *
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
+@SuppressWarnings("deprecation")
 public class LegacyLimitHandler extends AbstractLimitHandler {
 	private final Dialect dialect;
 
+	/**
+	 * Constructs a LegacyLimitHandler
+	 *
+	 * @param dialect The dialect
+	 * @param sql The sql
+	 * @param selection The row selection
+	 */
 	public LegacyLimitHandler(Dialect dialect, String sql, RowSelection selection) {
 		super( sql, selection );
 		this.dialect = dialect;
 	}
 
+	@Override
 	public boolean supportsLimit() {
 		return dialect.supportsLimit();
 	}
 
+	@Override
 	public boolean supportsLimitOffset() {
 		return dialect.supportsLimitOffset();
 	}
 
+	@Override
 	public boolean supportsVariableLimit() {
 		return dialect.supportsVariableLimit();
 	}
 
+	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return dialect.bindLimitParametersInReverseOrder();
 	}
 
+	@Override
 	public boolean bindLimitParametersFirst() {
 		return dialect.bindLimitParametersFirst();
 	}
 
+	@Override
 	public boolean useMaxForLimit() {
 		return dialect.useMaxForLimit();
 	}
 
+	@Override
 	public boolean forceLimitUsage() {
 		return dialect.forceLimitUsage();
 	}
 
+	@Override
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return dialect.convertToFirstRowValue( zeroBasedFirstResult );
 	}
 
+	@Override
 	public String getProcessedSql() {
 		boolean useLimitOffset = supportsLimit() && supportsLimitOffset()
 				&& LimitHelper.hasFirstRow( selection ) && LimitHelper.hasMaxRows( selection );
 		return dialect.getLimitString(
 				sql, useLimitOffset ? LimitHelper.getFirstRow( selection ) : 0, getMaxOrLimit()
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LimitHandler.java
index ea0b0dbf4b..3a28fab606 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LimitHandler.java
@@ -1,66 +1,87 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.dialect.pagination;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
-import org.hibernate.engine.spi.RowSelection;
-
 /**
  * Contract defining dialect-specific LIMIT clause handling. Typically implementers might consider extending
  * {@link AbstractLimitHandler} class.
  *
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public interface LimitHandler {
 	/**
 	 * Does this handler support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this handler supports some form of LIMIT.
 	 */
 	public boolean supportsLimit();
 
 	/**
 	 * Does this handler's LIMIT support (if any) additionally
 	 * support specifying an offset?
 	 *
 	 * @return True if the handler supports an offset within the limit support.
 	 */
 	public boolean supportsLimitOffset();
 
 	/**
 	 * Return processed SQL query.
 	 *
 	 * @return Query statement with LIMIT clause applied.
 	 */
 	public String getProcessedSql();
 
 	/**
 	 * Bind parameter values needed by the LIMIT clause before original SELECT statement.
 	 *
 	 * @param statement Statement to which to bind limit parameter values.
 	 * @param index Index from which to start binding.
 	 * @return The number of parameter values bound.
 	 * @throws SQLException Indicates problems binding parameter values.
 	 */
 	public int bindLimitParametersAtStartOfQuery(PreparedStatement statement, int index) throws SQLException;
 
 	/**
 	 * Bind parameter values needed by the LIMIT clause after original SELECT statement.
 	 *
 	 * @param statement Statement to which to bind limit parameter values.
 	 * @param index Index from which to start binding.
 	 * @return The number of parameter values bound.
 	 * @throws SQLException Indicates problems binding parameter values.
 	 */
 	public int bindLimitParametersAtEndOfQuery(PreparedStatement statement, int index) throws SQLException;
 
 	/**
 	 * Use JDBC API to limit the number of rows returned by the SQL query. Typically handlers that do not
 	 * support LIMIT clause should implement this method.
 	 *
 	 * @param statement Statement which number of returned rows shall be limited.
 	 * @throws SQLException Indicates problems while limiting maximum rows returned.
 	 */
 	public void setMaxRows(PreparedStatement statement) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LimitHelper.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LimitHelper.java
index 4a43735dca..9cf6dae797 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LimitHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LimitHelper.java
@@ -1,24 +1,81 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.dialect.pagination;
 
 import org.hibernate.engine.spi.RowSelection;
 
 /**
+ * A helper for dealing with LimitHandler implementations
+ *
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class LimitHelper {
+	/**
+	 * Is a max row limit indicated?
+	 *
+	 * @param selection The row selection options
+	 *
+	 * @return Whether a max row limit was indicated
+	 */
+	public static boolean hasMaxRows(RowSelection selection) {
+		return selection != null && selection.getMaxRows() != null && selection.getMaxRows() > 0;
+	}
+
+	/**
+	 * Should limit be applied?
+	 *
+	 * @param limitHandler The limit handler
+	 * @param selection The row selection
+	 *
+	 * @return Whether limiting is indicated
+	 */
 	public static boolean useLimit(LimitHandler limitHandler, RowSelection selection) {
 		return limitHandler.supportsLimit() && hasMaxRows( selection );
 	}
 
+	/**
+	 * Is a first row limit indicated?
+	 *
+	 * @param selection The row selection options
+	 *
+	 * @return Whether a first row limit in indicated
+	 */
 	public static boolean hasFirstRow(RowSelection selection) {
 		return getFirstRow( selection ) > 0;
 	}
 
+	/**
+	 * Retrieve the indicated first row for pagination
+	 *
+	 * @param selection The row selection options
+	 *
+	 * @return The first row
+	 */
 	public static int getFirstRow(RowSelection selection) {
 		return ( selection == null || selection.getFirstRow() == null ) ? 0 : selection.getFirstRow();
 	}
 
-	public static boolean hasMaxRows(RowSelection selection) {
-		return selection != null && selection.getMaxRows() != null && selection.getMaxRows() > 0;
+	private LimitHelper() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/NoopLimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/NoopLimitHandler.java
index 203902768a..b5dc87fec3 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/NoopLimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/NoopLimitHandler.java
@@ -1,35 +1,68 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.dialect.pagination;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.engine.spi.RowSelection;
 
 /**
  * Handler not supporting query LIMIT clause. JDBC API is used to set maximum number of returned rows.
  *
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class NoopLimitHandler extends AbstractLimitHandler {
+	/**
+	 * Constructs a NoopLimitHandler
+	 *
+	 * @param sql The SQL
+	 * @param selection The row selection options
+	 */
 	public NoopLimitHandler(String sql, RowSelection selection) {
 		super( sql, selection );
 	}
 
+	@Override
 	public String getProcessedSql() {
 		return sql;
 	}
 
+	@Override
 	public int bindLimitParametersAtStartOfQuery(PreparedStatement statement, int index) {
 		return 0;
 	}
 
+	@Override
 	public int bindLimitParametersAtEndOfQuery(PreparedStatement statement, int index) {
 		return 0;
 	}
 
+	@Override
 	public void setMaxRows(PreparedStatement statement) throws SQLException {
 		if ( LimitHelper.hasMaxRows( selection ) ) {
 			statement.setMaxRows( selection.getMaxRows() + convertToFirstRowValue( LimitHelper.getFirstRow( selection ) ) );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/SQLServer2005LimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/SQLServer2005LimitHandler.java
index b16d22639e..173bf5dde9 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/SQLServer2005LimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/SQLServer2005LimitHandler.java
@@ -1,289 +1,326 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.dialect.pagination;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * LIMIT clause handler compatible with SQL Server 2005 and later.
  *
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class SQLServer2005LimitHandler extends AbstractLimitHandler {
 	private static final String SELECT = "select";
 	private static final String SELECT_WITH_SPACE = SELECT + ' ';
 	private static final String FROM = "from";
 	private static final String DISTINCT = "distinct";
 	private static final String ORDER_BY = "order by";
 
 	private static final Pattern ALIAS_PATTERN = Pattern.compile( "(?i)\\sas\\s(.)+$" );
 
-	private boolean topAdded = false; // Flag indicating whether TOP(?) expression has been added to the original query.
-	private boolean hasOffset = true; // True if offset greater than 0.
+	// Flag indicating whether TOP(?) expression has been added to the original query.
+	private boolean topAdded;
+	// True if offset greater than 0.
+	private boolean hasOffset = true;
 
+	/**
+	 * Constructs a SQLServer2005LimitHandler
+	 *
+	 * @param sql The SQL
+	 * @param selection The row selection options
+	 */
 	public SQLServer2005LimitHandler(String sql, RowSelection selection) {
 		super( sql, selection );
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return true;
 	}
 
 	@Override
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		// Our dialect paginated results aren't zero based. The first row should get the number 1 and so on
 		return zeroBasedFirstResult + 1;
 	}
 
 	/**
 	 * Add a LIMIT clause to the given SQL SELECT (HHH-2655: ROW_NUMBER for Paging)
 	 *
 	 * The LIMIT SQL will look like:
 	 *
 	 * <pre>
 	 * WITH query AS (
 	 *   SELECT inner_query.*
 	 *        , ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __hibernate_row_nr__
 	 *     FROM ( original_query_with_top_if_order_by_present_and_all_aliased_columns ) inner_query
 	 * )
 	 * SELECT alias_list FROM query WHERE __hibernate_row_nr__ >= offset AND __hibernate_row_nr__ < offset + last
 	 * </pre>
 	 *
-	 * When offset equals {@literal 0}, only {@literal TOP(?)} expression is added to the original query.
+	 * When offset equals {@literal 0}, only <code>TOP(?)</code> expression is added to the original query.
 	 *
 	 * @return A new SQL statement with the LIMIT clause applied.
 	 */
 	@Override
 	public String getProcessedSql() {
-		StringBuilder sb = new StringBuilder( sql );
+		final StringBuilder sb = new StringBuilder( sql );
 		if ( sb.charAt( sb.length() - 1 ) == ';' ) {
 			sb.setLength( sb.length() - 1 );
 		}
 
 		if ( LimitHelper.hasFirstRow( selection ) ) {
 			final String selectClause = fillAliasInSelectClause( sb );
 
-			int orderByIndex = shallowIndexOfWord( sb, ORDER_BY, 0 );
+			final int orderByIndex = shallowIndexOfWord( sb, ORDER_BY, 0 );
 			if ( orderByIndex > 0 ) {
 				// ORDER BY requires using TOP.
 				addTopExpression( sb );
 			}
 
 			encloseWithOuterQuery( sb );
 
 			// Wrap the query within a with statement:
 			sb.insert( 0, "WITH query AS (" ).append( ") SELECT " ).append( selectClause ).append( " FROM query " );
 			sb.append( "WHERE __hibernate_row_nr__ >= ? AND __hibernate_row_nr__ < ?" );
 		}
 		else {
 			hasOffset = false;
 			addTopExpression( sb );
 		}
 
 		return sb.toString();
 	}
 
 	@Override
 	public int bindLimitParametersAtStartOfQuery(PreparedStatement statement, int index) throws SQLException {
 		if ( topAdded ) {
-			statement.setInt( index, getMaxOrLimit() - 1 ); // Binding TOP(?).
+			// Binding TOP(?)
+			statement.setInt( index, getMaxOrLimit() - 1 );
 			return 1;
 		}
 		return 0;
 	}
 
 	@Override
 	public int bindLimitParametersAtEndOfQuery(PreparedStatement statement, int index) throws SQLException {
 		return hasOffset ? super.bindLimitParametersAtEndOfQuery( statement, index ) : 0;
 	}
 
 	/**
 	 * Adds missing aliases in provided SELECT clause and returns coma-separated list of them.
 	 * If query takes advantage of expressions like {@literal *} or {@literal {table}.*} inside SELECT clause,
 	 * method returns {@literal *}.
 	 *
 	 * @param sb SQL query.
 	 *
 	 * @return List of aliases separated with comas or {@literal *}.
 	 */
 	protected String fillAliasInSelectClause(StringBuilder sb) {
 		final List<String> aliases = new LinkedList<String>();
 		final int startPos = shallowIndexOf( sb, SELECT_WITH_SPACE, 0 );
 		int endPos = shallowIndexOfWord( sb, FROM, startPos );
 		int nextComa = startPos;
 		int prevComa = startPos;
 		int unique = 0;
 		boolean selectsMultipleColumns = false;
 
 		while ( nextComa != -1 ) {
 			prevComa = nextComa;
 			nextComa = shallowIndexOf( sb, ",", nextComa );
 			if ( nextComa > endPos ) {
 				break;
 			}
 			if ( nextComa != -1 ) {
-				String expression = sb.substring( prevComa, nextComa );
+				final String expression = sb.substring( prevComa, nextComa );
 				if ( selectsMultipleColumns( expression ) ) {
 					selectsMultipleColumns = true;
 				}
 				else {
 					String alias = getAlias( expression );
 					if ( alias == null ) {
 						// Inserting alias. It is unlikely that we would have to add alias, but just in case.
 						alias = StringHelper.generateAlias( "page", unique );
 						sb.insert( nextComa, " as " + alias );
 						++unique;
 						nextComa += ( " as " + alias ).length();
 					}
 					aliases.add( alias );
 				}
 				++nextComa;
 			}
 		}
 		// Processing last column.
-		endPos = shallowIndexOfWord( sb, FROM, startPos ); // Refreshing end position, because we might have inserted new alias.
-		String expression = sb.substring( prevComa, endPos );
+		// Refreshing end position, because we might have inserted new alias.
+		endPos = shallowIndexOfWord( sb, FROM, startPos );
+		final String expression = sb.substring( prevComa, endPos );
 		if ( selectsMultipleColumns( expression ) ) {
 			selectsMultipleColumns = true;
 		}
 		else {
 			String alias = getAlias( expression );
 			if ( alias == null ) {
 				// Inserting alias. It is unlikely that we would have to add alias, but just in case.
 				alias = StringHelper.generateAlias( "page", unique );
 				sb.insert( endPos - 1, " as " + alias );
 			}
 			aliases.add( alias );
 		}
 
 		// In case of '*' or '{table}.*' expressions adding an alias breaks SQL syntax, returning '*'.
 		return selectsMultipleColumns ? "*" : StringHelper.join( ", ", aliases.iterator() );
 	}
 
 	/**
 	 * @param expression Select expression.
 	 *
 	 * @return {@code true} when expression selects multiple columns, {@code false} otherwise.
 	 */
 	private boolean selectsMultipleColumns(String expression) {
 		final String lastExpr = expression.trim().replaceFirst( "(?i)(.)*\\s", "" );
 		return "*".equals( lastExpr ) || lastExpr.endsWith( ".*" );
 	}
 
 	/**
 	 * Returns alias of provided single column selection or {@code null} if not found.
 	 * Alias should be preceded with {@code AS} keyword.
 	 *
 	 * @param expression Single column select expression.
 	 *
 	 * @return Column alias.
 	 */
 	private String getAlias(String expression) {
-		Matcher matcher = ALIAS_PATTERN.matcher( expression );
+		final Matcher matcher = ALIAS_PATTERN.matcher( expression );
 		if ( matcher.find() ) {
 			// Taking advantage of Java regular expressions greedy behavior while extracting the last AS keyword.
 			// Note that AS keyword can appear in CAST operator, e.g. 'cast(tab1.col1 as varchar(255)) as col1'.
 			return matcher.group( 0 ).replaceFirst( "(?i)(.)*\\sas\\s", "" ).trim();
 		}
 		return null;
 	}
 
 	/**
 	 * Encloses original SQL statement with outer query that provides {@literal __hibernate_row_nr__} column.
 	 *
 	 * @param sql SQL query.
 	 */
 	protected void encloseWithOuterQuery(StringBuilder sql) {
 		sql.insert( 0, "SELECT inner_query.*, ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __hibernate_row_nr__ FROM ( " );
 		sql.append( " ) inner_query " );
 	}
 
 	/**
 	 * Adds {@code TOP} expression. Parameter value is bind in
 	 * {@link #bindLimitParametersAtStartOfQuery(PreparedStatement, int)} method.
 	 *
 	 * @param sql SQL query.
 	 */
 	protected void addTopExpression(StringBuilder sql) {
 		final int distinctStartPos = shallowIndexOfWord( sql, DISTINCT, 0 );
 		if ( distinctStartPos > 0 ) {
 			// Place TOP after DISTINCT.
 			sql.insert( distinctStartPos + DISTINCT.length(), " TOP(?)" );
 		}
 		else {
 			final int selectStartPos = shallowIndexOf( sql, SELECT_WITH_SPACE, 0 );
 			// Place TOP after SELECT.
 			sql.insert( selectStartPos + SELECT.length(), " TOP(?)" );
 		}
 		topAdded = true;
 	}
 
 	/**
 	 * Returns index of the first case-insensitive match of search term surrounded by spaces
 	 * that is not enclosed in parentheses.
 	 *
 	 * @param sb String to search.
 	 * @param search Search term.
 	 * @param fromIndex The index from which to start the search.
 	 *
 	 * @return Position of the first match, or {@literal -1} if not found.
 	 */
 	private static int shallowIndexOfWord(final StringBuilder sb, final String search, int fromIndex) {
 		final int index = shallowIndexOf( sb, ' ' + search + ' ', fromIndex );
-		return index != -1 ? ( index + 1 ) : -1; // In case of match adding one because of space placed in front of search term.
+		// In case of match adding one because of space placed in front of search term.
+		return index != -1 ? ( index + 1 ) : -1;
 	}
 
 	/**
 	 * Returns index of the first case-insensitive match of search term that is not enclosed in parentheses.
 	 *
 	 * @param sb String to search.
 	 * @param search Search term.
 	 * @param fromIndex The index from which to start the search.
 	 *
 	 * @return Position of the first match, or {@literal -1} if not found.
 	 */
 	private static int shallowIndexOf(StringBuilder sb, String search, int fromIndex) {
-		final String lowercase = sb.toString().toLowerCase(); // case-insensitive match
+		// case-insensitive match
+		final String lowercase = sb.toString().toLowerCase();
 		final int len = lowercase.length();
 		final int searchlen = search.length();
-		int pos = -1, depth = 0, cur = fromIndex;
+		int pos = -1;
+		int depth = 0;
+		int cur = fromIndex;
 		do {
 			pos = lowercase.indexOf( search, cur );
 			if ( pos != -1 ) {
 				for ( int iter = cur; iter < pos; iter++ ) {
 					char c = sb.charAt( iter );
 					if ( c == '(' ) {
 						depth = depth + 1;
 					}
 					else if ( c == ')' ) {
 						depth = depth - 1;
 					}
 				}
 				cur = pos + searchlen;
 			}
 		} while ( cur < len && depth != 0 && pos != -1 );
 		return depth == 0 ? pos : -1;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/package-info.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/package-info.java
new file mode 100644
index 0000000000..5dddd0af88
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Support for Dialect-specific pagination strategies
+ */
+package org.hibernate.dialect.pagination;
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/unique/DB2UniqueDelegate.java b/hibernate-core/src/main/java/org/hibernate/dialect/unique/DB2UniqueDelegate.java
index 15c2ae9646..920e885a5e 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/unique/DB2UniqueDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/unique/DB2UniqueDelegate.java
@@ -1,108 +1,133 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
 package org.hibernate.dialect.unique;
 
 import java.util.Iterator;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Index;
 import org.hibernate.metamodel.relational.UniqueKey;
 
 /**
  * DB2 does not allow unique constraints on nullable columns.  Rather than
  * forcing "not null", use unique *indexes* instead.
  * 
  * @author Brett Meyer
  */
 public class DB2UniqueDelegate extends DefaultUniqueDelegate {
-	
+	/**
+	 * Constructs a DB2UniqueDelegate
+	 *
+	 * @param dialect The dialect
+	 */
 	public DB2UniqueDelegate( Dialect dialect ) {
 		super( dialect );
 	}
-	
+
 	@Override
-	public String applyUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
-			String defaultCatalog, String defaultSchema ) {
+	public String getAlterTableToAddUniqueKeyCommand(
+			org.hibernate.mapping.UniqueKey uniqueKey,
+			String defaultCatalog,
+			String defaultSchema) {
 		if ( hasNullable( uniqueKey ) ) {
 			return org.hibernate.mapping.Index.buildSqlCreateIndexString(
-					dialect, uniqueKey.getName(), uniqueKey.getTable(),
-					uniqueKey.columnIterator(), uniqueKey.getColumnOrderMap(), true, defaultCatalog,
-					defaultSchema );
-		} else {
-			return super.applyUniquesOnAlter(
-					uniqueKey, defaultCatalog, defaultSchema );
+					dialect,
+					uniqueKey.getName(),
+					uniqueKey.getTable(),
+					uniqueKey.columnIterator(),
+					uniqueKey.getColumnOrderMap(),
+					true,
+					defaultCatalog,
+					defaultSchema
+			);
+		}
+		else {
+			return super.getAlterTableToAddUniqueKeyCommand( uniqueKey, defaultCatalog, defaultSchema );
 		}
 	}
 	
 	@Override
-	public String applyUniquesOnAlter( UniqueKey uniqueKey ) {
+	public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey) {
 		if ( hasNullable( uniqueKey ) ) {
 			return Index.buildSqlCreateIndexString(
-					dialect, uniqueKey.getName(), uniqueKey.getTable(),
-					uniqueKey.getColumns(), true );
-		} else {
-			return super.applyUniquesOnAlter( uniqueKey );
+					dialect,
+					uniqueKey.getName(),
+					uniqueKey.getTable(),
+					uniqueKey.getColumns(),
+					true
+			);
+		}
+		else {
+			return super.getAlterTableToAddUniqueKeyCommand( uniqueKey );
 		}
 	}
 	
 	@Override
-	public String dropUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
-			String defaultCatalog, String defaultSchema ) {
+	public String getAlterTableToDropUniqueKeyCommand(
+			org.hibernate.mapping.UniqueKey uniqueKey,
+			String defaultCatalog,
+			String defaultSchema) {
 		if ( hasNullable( uniqueKey ) ) {
 			return org.hibernate.mapping.Index.buildSqlDropIndexString(
-					dialect, uniqueKey.getTable(), uniqueKey.getName(),
-					defaultCatalog, defaultSchema );
-		} else {
-			return super.dropUniquesOnAlter(
-					uniqueKey, defaultCatalog, defaultSchema );
+					dialect,
+					uniqueKey.getTable(),
+					uniqueKey.getName(),
+					defaultCatalog,
+					defaultSchema
+			);
+		}
+		else {
+			return super.getAlterTableToDropUniqueKeyCommand(
+					uniqueKey, defaultCatalog, defaultSchema
+			);
 		}
 	}
 	
 	@Override
-	public String dropUniquesOnAlter( UniqueKey uniqueKey ) {
+	public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey) {
 		if ( hasNullable( uniqueKey ) ) {
-			return Index.buildSqlDropIndexString(
-					dialect, uniqueKey.getTable(), uniqueKey.getName() );
-		} else {
-			return super.dropUniquesOnAlter( uniqueKey );
+			return Index.buildSqlDropIndexString( dialect, uniqueKey.getTable(), uniqueKey.getName() );
+		}
+		else {
+			return super.getAlterTableToDropUniqueKeyCommand( uniqueKey );
 		}
 	}
 	
-	private boolean hasNullable( org.hibernate.mapping.UniqueKey uniqueKey ) {
-		Iterator<org.hibernate.mapping.Column> iter = uniqueKey.columnIterator();
+	private boolean hasNullable(org.hibernate.mapping.UniqueKey uniqueKey) {
+		final Iterator<org.hibernate.mapping.Column> iter = uniqueKey.columnIterator();
 		while ( iter.hasNext() ) {
 			if ( iter.next().isNullable() ) {
 				return true;
 			}
 		}
 		return false;
 	}
-	
-	private boolean hasNullable( UniqueKey uniqueKey ) {
+
+	private boolean hasNullable(UniqueKey uniqueKey) {
 		for ( Column column : uniqueKey.getColumns() ) {
 			if ( column.isNullable() ) {
 				return true;
 			}
 		}
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java b/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java
index cf67311f01..4529760fd1 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java
@@ -1,151 +1,151 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
 package org.hibernate.dialect.unique;
 
 import java.util.Iterator;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.UniqueKey;
 
 /**
  * The default UniqueDelegate implementation for most dialects.  Uses
  * separate create/alter statements to apply uniqueness to a column.
  * 
  * @author Brett Meyer
  */
 public class DefaultUniqueDelegate implements UniqueDelegate {
-	
 	protected final Dialect dialect;
-	
+
+	/**
+	 * Constructs DefaultUniqueDelegate
+	 *
+	 * @param dialect The dialect for which we are handling unique constraints
+	 */
 	public DefaultUniqueDelegate( Dialect dialect ) {
 		this.dialect = dialect;
 	}
-	
-	@Override
-	public String applyUniqueToColumn( org.hibernate.mapping.Column column ) {
-		return "";
-	}
-	
-	@Override
-	public String applyUniqueToColumn( Column column ) {
-		return "";
-	}
+
+	// legacy model ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
-	public String applyUniquesToTable( org.hibernate.mapping.Table table ) {
+	public String getColumnDefinitionUniquenessFragment(org.hibernate.mapping.Column column) {
 		return "";
 	}
 
 	@Override
-	public String applyUniquesToTable( Table table ) {
+	public String getTableCreationUniqueConstraintsFragment(org.hibernate.mapping.Table table) {
 		return "";
 	}
-	
-	@Override
-	public String applyUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
-			String defaultCatalog, String defaultSchema ) {
-		// Do this here, rather than allowing UniqueKey/Constraint to do it.
-		// We need full, simplified control over whether or not it happens.
-		return new StringBuilder( "alter table " )
-				.append( uniqueKey.getTable().getQualifiedName(
-						dialect, defaultCatalog, defaultSchema ) )
-				.append( " add constraint " )
-				.append( uniqueKey.getName() )
-				.append( uniqueConstraintSql( uniqueKey ) )
-				.toString();
-	}
-	
-	@Override
-	public String applyUniquesOnAlter( UniqueKey uniqueKey  ) {
-		// Do this here, rather than allowing UniqueKey/Constraint to do it.
-		// We need full, simplified control over whether or not it happens.
-		return new StringBuilder( "alter table " )
-				.append( uniqueKey.getTable().getQualifiedName( dialect ) )
-				.append( " add constraint " )
-				.append( uniqueKey.getName() )
-				.append( uniqueConstraintSql( uniqueKey ) )
-				.toString();
-	}
-	
-	@Override
-	public String dropUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
-			String defaultCatalog, String defaultSchema ) {
-		// Do this here, rather than allowing UniqueKey/Constraint to do it.
-		// We need full, simplified control over whether or not it happens.
-		return new StringBuilder( "alter table " )
-				.append( uniqueKey.getTable().getQualifiedName(
-						dialect, defaultCatalog, defaultSchema ) )
-				.append( " drop constraint " )
-				.append( dialect.quote( uniqueKey.getName() ) )
-				.toString();
-	}
-	
+
 	@Override
-	public String dropUniquesOnAlter( UniqueKey uniqueKey  ) {
+	public String getAlterTableToAddUniqueKeyCommand(
+			org.hibernate.mapping.UniqueKey uniqueKey,
+			String defaultCatalog,
+			String defaultSchema) {
 		// Do this here, rather than allowing UniqueKey/Constraint to do it.
 		// We need full, simplified control over whether or not it happens.
-		return new StringBuilder( "alter table " )
-				.append( uniqueKey.getTable().getQualifiedName( dialect ) )
-				.append( " drop constraint " )
-				.append( dialect.quote( uniqueKey.getName() ) )
-				.toString();
+		final String tableName = uniqueKey.getTable().getQualifiedName( dialect, defaultCatalog, defaultSchema );
+		final String constraintName = dialect.quote( uniqueKey.getName() );
+		return "alter table " + tableName + " add constraint " + constraintName + " " + uniqueConstraintSql( uniqueKey );
 	}
-	
-	@Override
-	public String uniqueConstraintSql( org.hibernate.mapping.UniqueKey uniqueKey ) {
-		StringBuilder sb = new StringBuilder();
+
+	protected String uniqueConstraintSql( org.hibernate.mapping.UniqueKey uniqueKey ) {
+		final StringBuilder sb = new StringBuilder();
 		sb.append( " unique (" );
-		Iterator<org.hibernate.mapping.Column> columnIterator = uniqueKey.columnIterator();
+		final Iterator<org.hibernate.mapping.Column> columnIterator = uniqueKey.columnIterator();
 		while ( columnIterator.hasNext() ) {
-			org.hibernate.mapping.Column column
-					= columnIterator.next();
+			final org.hibernate.mapping.Column column = columnIterator.next();
 			sb.append( column.getQuotedName( dialect ) );
 			if ( uniqueKey.getColumnOrderMap().containsKey( column ) ) {
 				sb.append( " " ).append( uniqueKey.getColumnOrderMap().get( column ) );
 			}
 			if ( columnIterator.hasNext() ) {
 				sb.append( ", " );
 			}
 		}
-		
+
 		return sb.append( ')' ).toString();
 	}
+
+	@Override
+	public String getAlterTableToDropUniqueKeyCommand(
+			org.hibernate.mapping.UniqueKey uniqueKey,
+			String defaultCatalog,
+			String defaultSchema) {
+		// Do this here, rather than allowing UniqueKey/Constraint to do it.
+		// We need full, simplified control over whether or not it happens.
+		final String tableName = uniqueKey.getTable().getQualifiedName( dialect, defaultCatalog, defaultSchema );
+		final String constraintName = dialect.quote( uniqueKey.getName() );
+		return "alter table " + tableName + " drop constraint " + constraintName;
+	}
+
+
+	// new model ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public String getColumnDefinitionUniquenessFragment(Column column) {
+		return "";
+	}
+
+	@Override
+	public String getTableCreationUniqueConstraintsFragment(Table table) {
+		return "";
+	}
 	
+
 	@Override
-	public String uniqueConstraintSql( UniqueKey uniqueKey ) {
-		StringBuilder sb = new StringBuilder();
-		sb.append( " unique (" );
-		Iterator columnIterator = uniqueKey.getColumns().iterator();
+	public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey) {
+		// Do this here, rather than allowing UniqueKey/Constraint to do it.
+		// We need full, simplified control over whether or not it happens.
+		final String tableName = uniqueKey.getTable().getQualifiedName( dialect );
+		final String constraintName = dialect.quote( uniqueKey.getName() );
+
+		return "alter table " + tableName + " add constraint " + constraintName + uniqueConstraintSql( uniqueKey );
+	}
+
+	protected String uniqueConstraintSql( UniqueKey uniqueKey ) {
+		final StringBuilder sb = new StringBuilder( " unique (" );
+		final Iterator columnIterator = uniqueKey.getColumns().iterator();
 		while ( columnIterator.hasNext() ) {
-			org.hibernate.mapping.Column column
-					= (org.hibernate.mapping.Column) columnIterator.next();
+			final org.hibernate.mapping.Column column = (org.hibernate.mapping.Column) columnIterator.next();
 			sb.append( column.getQuotedName( dialect ) );
 			if ( columnIterator.hasNext() ) {
 				sb.append( ", " );
 			}
 		}
-		
+
 		return sb.append( ')' ).toString();
 	}
 
+	@Override
+	public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey) {
+		// Do this here, rather than allowing UniqueKey/Constraint to do it.
+		// We need full, simplified control over whether or not it happens.
+		final String tableName = uniqueKey.getTable().getQualifiedName( dialect );
+		final String constraintName = dialect.quote( uniqueKey.getName() );
+
+		return "alter table " + tableName + " drop constraint " + constraintName;
+	}
+
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/unique/UniqueDelegate.java b/hibernate-core/src/main/java/org/hibernate/dialect/unique/UniqueDelegate.java
index 4cd3c346d6..156ee17144 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/unique/UniqueDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/unique/UniqueDelegate.java
@@ -1,146 +1,150 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
 package org.hibernate.dialect.unique;
 
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.UniqueKey;
 
 /**
- * Dialect-level delegate in charge of applying "uniqueness" to a column.
- * Uniqueness can be defined in 1 of 3 ways:
- * 
- * 1.) Add a unique constraint via separate alter table statements.
- * 2.) Add a unique constraint via dialect-specific syntax in table create statement.
- * 3.) Add "unique" syntax to the column itself.
- * 
- * #1 & #2 are preferred, if possible -- #3 should be solely a fall-back.
- * 
- * TODO: This could eventually be simplified.  With AST, 1 "applyUniqueness"
- * method might be possible. But due to .cfg and .mapping still resolving
- * around StringBuilders, separate methods were needed.
+ * Dialect-level delegate in charge of applying "uniqueness" to a column.  Uniqueness can be defined
+ * in 1 of 3 ways:<ol>
+ *     <li>
+ *         Add a unique constraint via separate alter table statements.  See {@link #getAlterTableToAddUniqueKeyCommand}.
+ *         Also, see {@link #getAlterTableToDropUniqueKeyCommand}
+ *     </li>
+ *     <li>
+ *			Add a unique constraint via dialect-specific syntax in table create statement.  See
+ *			{@link #getTableCreationUniqueConstraintsFragment}
+ *     </li>
+ *     <li>
+ *         Add "unique" syntax to the column itself.  See {@link #getColumnDefinitionUniquenessFragment}
+ *     </li>
+ * </ol>
+ *
+ * #1 & #2 are preferred, if possible; #3 should be solely a fall-back.
  * 
  * See HHH-7797.
  * 
  * @author Brett Meyer
  */
 public interface UniqueDelegate {
-	
-	/**
-	 * If the dialect does not supports unique constraints, this method should
-	 * return the syntax necessary to mutate the column definition
-	 * (usually "unique").
-	 * 
-	 * @param column
-	 * @return String
-	 */
-	public String applyUniqueToColumn( org.hibernate.mapping.Column column );
-	
-	/**
-	 * If the dialect does not supports unique constraints, this method should
-	 * return the syntax necessary to mutate the column definition
-	 * (usually "unique").
-	 * 
-	 * @param column
-	 * @return String
-	 */
-	public String applyUniqueToColumn( Column column );
-	
 	/**
-	 * If constraints are supported, but not in seperate alter statements,
-	 * return uniqueConstraintSql in order to add the constraint to the
-	 * original table definition.
+	 * Get the fragment that can be used to make a column unique as part of its column definition.
+	 * <p/>
+	 * This is intended for dialects which do not support unique constraints
 	 * 
-	 * @param table
-	 * @return String
+	 * @param column The column to which to apply the unique
+	 *
+	 * @return The fragment (usually "unique"), empty string indicates the uniqueness will be indicated using a
+	 * different approach
 	 */
-	public String applyUniquesToTable( org.hibernate.mapping.Table table );
-	
+	public String getColumnDefinitionUniquenessFragment(org.hibernate.mapping.Column column);
+
 	/**
-	 * If constraints are supported, but not in seperate alter statements,
-	 * return uniqueConstraintSql in order to add the constraint to the
-	 * original table definition.
-	 * 
-	 * @param table
-	 * @return String
+	 * Get the fragment that can be used to make a column unique as part of its column definition.
+	 * <p/>
+	 * This is intended for dialects which do not support unique constraints
+	 *
+	 * @param column The column to which to apply the unique
+	 *
+	 * @return The fragment (usually "unique"), empty string indicates the uniqueness will be indicated using a
+	 * different approach
 	 */
-	public String applyUniquesToTable( Table table );
-	
+	public String getColumnDefinitionUniquenessFragment(Column column);
+
 	/**
-	 * If creating unique constraints in separate alter statements is
-	 * supported, generate the necessary "alter" syntax for the given key.
-	 * 
-	 * @param uniqueKey
-	 * @param defaultCatalog
-	 * @param defaultSchema
-	 * @return String
+	 * Get the fragment that can be used to apply unique constraints as part of table creation.  The implementation
+	 * should iterate over the {@link org.hibernate.mapping.UniqueKey} instances for the given table (see
+	 * {@link org.hibernate.mapping.Table#getUniqueKeyIterator()} and generate the whole fragment for all
+	 * unique keys
+	 * <p/>
+	 * Intended for Dialects which support unique constraint definitions, but just not in separate ALTER statements.
+	 *
+	 * @param table The table for which to generate the unique constraints fragment
+	 *
+	 * @return The fragment, typically in the form {@code ", unique(col1, col2), unique( col20)"}.  NOTE: The leading
+	 * comma is important!
 	 */
-	public String applyUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
-			String defaultCatalog, String defaultSchema );
+	public String getTableCreationUniqueConstraintsFragment(org.hibernate.mapping.Table table);
 	
 	/**
-	 * If creating unique constraints in separate alter statements is
-	 * supported, generate the necessary "alter" syntax for the given key.
-	 * 
-	 * @param uniqueKey
-	 * @return String
+	 * Get the fragment that can be used to apply unique constraints as part of table creation.  The implementation
+	 * should iterate over the {@link org.hibernate.mapping.UniqueKey} instances for the given table (see
+	 * {@link org.hibernate.mapping.Table#getUniqueKeyIterator()} and generate the whole fragment for all
+	 * unique keys
+	 * <p/>
+	 * Intended for Dialects which support unique constraint definitions, but just not in separate ALTER statements.
+	 *
+	 * @param table The table for which to generate the unique constraints fragment
+	 *
+	 * @return The fragment, typically in the form {@code ", unique(col1, col2), unique( col20)"}.  NOTE: The leading
+	 * comma is important!
 	 */
-	public String applyUniquesOnAlter( UniqueKey uniqueKey );
-	
+	public String getTableCreationUniqueConstraintsFragment(Table table);
+
 	/**
-	 * If dropping unique constraints in separate alter statements is
-	 * supported, generate the necessary "alter" syntax for the given key.
-	 * 
-	 * @param uniqueKey
-	 * @param defaultCatalog
-	 * @param defaultSchema
-	 * @return String
+	 * Get the SQL ALTER TABLE command to be used to create the given UniqueKey.
+	 *
+	 * @param uniqueKey The UniqueKey instance.  Contains all information about the columns
+	 * @param defaultCatalog The default catalog
+	 * @param defaultSchema The default schema
+	 *
+	 * @return The ALTER TABLE command
 	 */
-	public String dropUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
-			String defaultCatalog, String defaultSchema );
-	
+	public String getAlterTableToAddUniqueKeyCommand(
+			org.hibernate.mapping.UniqueKey uniqueKey,
+			String defaultCatalog,
+			String defaultSchema);
+
 	/**
-	 * If dropping unique constraints in separate alter statements is
-	 * supported, generate the necessary "alter" syntax for the given key.
-	 * 
-	 * @param uniqueKey
-	 * @return String
+	 * Get the SQL ALTER TABLE command to be used to create the given UniqueKey.
+	 *
+	 * @param uniqueKey The UniqueKey instance.  Contains all information about the columns, as well as
+	 * schema/catalog
+	 *
+	 * @return The ALTER TABLE command
 	 */
-	public String dropUniquesOnAlter( UniqueKey uniqueKey );
-	
+	public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey);
+
 	/**
-	 * Generates the syntax necessary to create the unique constraint (reused
-	 * by all methods).  Ex: "unique (column1, column2, ...)"
-	 * 
-	 * @param uniqueKey
-	 * @return String
+	 * Get the SQL ALTER TABLE command to be used to drop the given UniqueKey.
+	 *
+	 * @param uniqueKey The UniqueKey instance.  Contains all information about the columns
+	 * @param defaultCatalog The default catalog
+	 * @param defaultSchema The default schema
+	 *
+	 * @return The ALTER TABLE command
 	 */
-	public String uniqueConstraintSql( org.hibernate.mapping.UniqueKey uniqueKey );
-	
+	public String getAlterTableToDropUniqueKeyCommand(
+			org.hibernate.mapping.UniqueKey uniqueKey,
+			String defaultCatalog, String defaultSchema);
+
 	/**
-	 * Generates the syntax necessary to create the unique constraint (reused
-	 * by all methods).  Ex: "unique (column1, column2, ...)"
-	 * 
-	 * @param uniqueKey
-	 * @return String
+	 * Get the SQL ALTER TABLE command to be used to drop the given UniqueKey.
+	 *
+	 * @param uniqueKey The UniqueKey instance.  Contains all information about the columns, as well as
+	 * schema/catalog
+	 *
+	 * @return The ALTER TABLE command
 	 */
-	public String uniqueConstraintSql( UniqueKey uniqueKey );
+	public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/unique/package-info.java b/hibernate-core/src/main/java/org/hibernate/dialect/unique/package-info.java
new file mode 100644
index 0000000000..231e7630e7
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/unique/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Support for Dialect-specific unique constraint definition
+ */
+package org.hibernate.dialect.unique;
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/internal/StandardDatabaseMetaDataDialectResolver.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/internal/StandardDatabaseMetaDataDialectResolver.java
index 2cf8b5cd97..c54bfd8ef7 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/internal/StandardDatabaseMetaDataDialectResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/internal/StandardDatabaseMetaDataDialectResolver.java
@@ -1,92 +1,92 @@
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
 package org.hibernate.engine.jdbc.dialect.internal;
 
 import java.sql.DatabaseMetaData;
 import java.sql.SQLException;
 
 import org.hibernate.dialect.Dialect;
-import org.hibernate.dialect.resolver.BasicSQLExceptionConverter;
+import org.hibernate.engine.jdbc.dialect.spi.BasicSQLExceptionConverter;
 import org.hibernate.engine.jdbc.dialect.spi.AbstractDatabaseMetaDataDialectResolver;
 import org.hibernate.engine.jdbc.dialect.spi.DatabaseInfoDialectResolver;
 
 /**
  * The standard Hibernate Dialect resolver.
  *
  * @author Steve Ebersole
  */
 public class StandardDatabaseMetaDataDialectResolver extends AbstractDatabaseMetaDataDialectResolver {
 	private final DatabaseInfoDialectResolver infoResolver;
 
 	public StandardDatabaseMetaDataDialectResolver(DatabaseInfoDialectResolver infoResolver) {
 		this.infoResolver = infoResolver;
 	}
 
 	public static final class DatabaseInfoImpl implements DatabaseInfoDialectResolver.DatabaseInfo {
 		private final DatabaseMetaData databaseMetaData;
 
 		public DatabaseInfoImpl(DatabaseMetaData databaseMetaData) {
 			this.databaseMetaData = databaseMetaData;
 		}
 
 		@Override
 		public String getDatabaseName() {
 			try {
 				return databaseMetaData.getDatabaseProductName();
 			}
 			catch (SQLException e) {
 				throw BasicSQLExceptionConverter.INSTANCE.convert( e );
 			}
 		}
 
 		@Override
 		public int getDatabaseMajorVersion() {
 			try {
 				return databaseMetaData.getDatabaseMajorVersion();
 			}
 			catch (SQLException e) {
 				throw BasicSQLExceptionConverter.INSTANCE.convert( e );
 			}
 		}
 
 		@Override
 		public int getDatabaseMinorVersion() {
 			try {
 				return databaseMetaData.getDatabaseMinorVersion();
 			}
 			catch (SQLException e) {
 				throw BasicSQLExceptionConverter.INSTANCE.convert( e );
 			}
 		}
 	}
 
 	@Override
     protected Dialect resolveDialectInternal(DatabaseMetaData metaData) throws SQLException {
 		if ( infoResolver == null ) {
 			return null;
 		}
 
 		return infoResolver.resolve( new DatabaseInfoImpl( metaData ) );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/AbstractDatabaseMetaDataDialectResolver.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/AbstractDatabaseMetaDataDialectResolver.java
index b6db70494a..65785c5cde 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/AbstractDatabaseMetaDataDialectResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/AbstractDatabaseMetaDataDialectResolver.java
@@ -1,83 +1,82 @@
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
 package org.hibernate.engine.jdbc.dialect.spi;
 
 import java.sql.DatabaseMetaData;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.JDBCException;
 import org.hibernate.dialect.Dialect;
-import org.hibernate.dialect.resolver.BasicSQLExceptionConverter;
 import org.hibernate.exception.JDBCConnectionException;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * A templated resolver impl which delegates to the {@link #resolveDialectInternal} method
  * and handles any thrown {@link SQLException SQL errors}.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractDatabaseMetaDataDialectResolver implements DialectResolver {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			AbstractDatabaseMetaDataDialectResolver.class.getName()
 	);
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Here we template the resolution, delegating to {@link #resolveDialectInternal} and handling
 	 * {@link java.sql.SQLException}s properly.
 	 */
 	public final Dialect resolveDialect(DatabaseMetaData metaData) {
 		try {
 			return resolveDialectInternal( metaData );
 		}
 		catch ( SQLException sqlException ) {
 			JDBCException jdbcException = BasicSQLExceptionConverter.INSTANCE.convert( sqlException );
             if (jdbcException instanceof JDBCConnectionException) {
 				throw jdbcException;
 			}
 
             LOG.warnf( "%s : %s", BasicSQLExceptionConverter.MSG, sqlException.getMessage() );
             return null;
 		}
 		catch ( Throwable t ) {
             LOG.unableToExecuteResolver( this, t.getMessage() );
 			return null;
 		}
 	}
 
 	/**
 	 * Perform the actual resolution without caring about handling {@link SQLException}s.
 	 *
 	 * @param metaData The database metadata
 	 * @return The resolved dialect, or null if we could not resolve.
 	 * @throws SQLException Indicates problems accessing the metadata.
 	 */
 	protected abstract Dialect resolveDialectInternal(DatabaseMetaData metaData) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicSQLExceptionConverter.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/BasicSQLExceptionConverter.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicSQLExceptionConverter.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/BasicSQLExceptionConverter.java
index 52defca1de..b0a4bc5ebe 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicSQLExceptionConverter.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/BasicSQLExceptionConverter.java
@@ -1,65 +1,77 @@
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
-package org.hibernate.dialect.resolver;
+package org.hibernate.engine.jdbc.dialect.spi;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.JDBCException;
 import org.hibernate.exception.internal.SQLStateConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * A helper to centralize conversion of {@link java.sql.SQLException}s to {@link org.hibernate.JDBCException}s.
+ * <p/>
+ * Used while querying JDBC metadata during bootstrapping
  *
  * @author Steve Ebersole
  */
 public class BasicSQLExceptionConverter {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			BasicSQLExceptionConverter.class.getName()
+	);
 
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, BasicSQLExceptionConverter.class.getName() );
+	/**
+	 * Singleton access
+	 */
 	public static final BasicSQLExceptionConverter INSTANCE = new BasicSQLExceptionConverter();
+
+	/**
+	 *
+	 */
 	public static final String MSG = LOG.unableToQueryDatabaseMetadata();
 
 	private static final SQLStateConverter CONVERTER = new SQLStateConverter( new ConstraintNameExtracter() );
 
 	/**
 	 * Perform a conversion.
 	 *
 	 * @param sqlException The exception to convert.
 	 * @return The converted exception.
 	 */
 	public JDBCException convert(SQLException sqlException) {
 		return CONVERTER.convert( sqlException, MSG, null );
 	}
 
 	private static class ConstraintNameExtracter implements ViolatedConstraintNameExtracter {
 		/**
 		 * {@inheritDoc}
 		 */
 		public String extractConstraintName(SQLException sqle) {
 			return "???";
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
index 15aeee8f87..6c3eee99c0 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
@@ -1,868 +1,868 @@
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
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.tool.hbm2ddl.ColumnMetadata;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 
 /**
  * A relational table
  *
  * @author Gavin King
  */
 public class Table implements RelationalModel, Serializable {
 
 	private String name;
 	private String schema;
 	private String catalog;
 	/**
 	 * contains all columns, including the primary key
 	 */
 	private Map columns = new LinkedHashMap();
 	private KeyValue idValue;
 	private PrimaryKey primaryKey;
 	private Map<String, Index> indexes = new LinkedHashMap<String, Index>();
 	private Map foreignKeys = new LinkedHashMap();
 	private Map<String,UniqueKey> uniqueKeys = new LinkedHashMap<String,UniqueKey>();
 	private int uniqueInteger;
 	private boolean quoted;
 	private boolean schemaQuoted;
 	private boolean catalogQuoted;
 	private List checkConstraints = new ArrayList();
 	private String rowId;
 	private String subselect;
 	private boolean isAbstract;
 	private boolean hasDenormalizedTables = false;
 	private String comment;
 	
 	/**
 	 * Natural ID columns must reside in one single UniqueKey within the Table.
 	 * To prevent separate UniqueKeys from being created, this keeps track of
 	 * a sole name used for all of them.  It's necessary since
 	 * AnnotationBinder#processElementAnnotations (static) creates the
 	 * UniqueKeys on a second pass using randomly-generated names.
 	 */
 	private final String naturalIdUniqueKeyName = StringHelper.randomFixedLengthHex( "UK_" );
 
 	static class ForeignKeyKey implements Serializable {
 		String referencedClassName;
 		List columns;
 		List referencedColumns;
 
 		ForeignKeyKey(List columns, String referencedClassName, List referencedColumns) {
 			this.referencedClassName = referencedClassName;
 			this.columns = new ArrayList();
 			this.columns.addAll( columns );
 			if ( referencedColumns != null ) {
 				this.referencedColumns = new ArrayList();
 				this.referencedColumns.addAll( referencedColumns );
 			}
 			else {
 				this.referencedColumns = Collections.EMPTY_LIST;
 			}
 		}
 
 		public int hashCode() {
 			return columns.hashCode() + referencedColumns.hashCode();
 		}
 
 		public boolean equals(Object other) {
 			ForeignKeyKey fkk = (ForeignKeyKey) other;
 			return fkk.columns.equals( columns ) &&
 					fkk.referencedClassName.equals( referencedClassName ) && fkk.referencedColumns
 					.equals( referencedColumns );
 		}
 	}
 
 	public Table() { }
 
 	public Table(String name) {
 		this();
 		setName( name );
 	}
 
 	public String getQualifiedName(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		if ( subselect != null ) {
 			return "( " + subselect + " )";
 		}
 		String quotedName = getQuotedName( dialect );
 		String usedSchema = schema == null ?
 				defaultSchema :
 				getQuotedSchema( dialect );
 		String usedCatalog = catalog == null ?
 				defaultCatalog :
 				getQuotedCatalog( dialect );
 		return qualify( usedCatalog, usedSchema, quotedName );
 	}
 
 	public static String qualify(String catalog, String schema, String table) {
 		StringBuilder qualifiedName = new StringBuilder();
 		if ( catalog != null ) {
 			qualifiedName.append( catalog ).append( '.' );
 		}
 		if ( schema != null ) {
 			qualifiedName.append( schema ).append( '.' );
 		}
 		return qualifiedName.append( table ).toString();
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	/**
 	 * returns quoted name as it would be in the mapping file.
 	 */
 	public String getQuotedName() {
 		return quoted ?
 				"`" + name + "`" :
 				name;
 	}
 
 	public String getQuotedName(Dialect dialect) {
 		return quoted ?
 				dialect.openQuote() + name + dialect.closeQuote() :
 				name;
 	}
 
 	/**
 	 * returns quoted name as it is in the mapping file.
 	 */
 	public String getQuotedSchema() {
 		return schemaQuoted ?
 				"`" + schema + "`" :
 				schema;
 	}
 
 	public String getQuotedSchema(Dialect dialect) {
 		return schemaQuoted ?
 				dialect.openQuote() + schema + dialect.closeQuote() :
 				schema;
 	}
 
 	public String getQuotedCatalog() {
 		return catalogQuoted ?
 				"`" + catalog + "`" :
 				catalog;
 	}
 
 	public String getQuotedCatalog(Dialect dialect) {
 		return catalogQuoted ?
 				dialect.openQuote() + catalog + dialect.closeQuote() :
 				catalog;
 	}
 
 	public void setName(String name) {
 		if ( name.charAt( 0 ) == '`' ) {
 			quoted = true;
 			this.name = name.substring( 1, name.length() - 1 );
 		}
 		else {
 			this.name = name;
 		}
 	}
 
 	/**
 	 * Return the column which is identified by column provided as argument.
 	 *
 	 * @param column column with atleast a name.
 	 * @return the underlying column or null if not inside this table. Note: the instance *can* be different than the input parameter, but the name will be the same.
 	 */
 	public Column getColumn(Column column) {
 		if ( column == null ) {
 			return null;
 		}
 
 		Column myColumn = (Column) columns.get( column.getCanonicalName() );
 
 		return column.equals( myColumn ) ?
 				myColumn :
 				null;
 	}
 
 	public Column getColumn(int n) {
 		Iterator iter = columns.values().iterator();
 		for ( int i = 0; i < n - 1; i++ ) {
 			iter.next();
 		}
 		return (Column) iter.next();
 	}
 
 	public void addColumn(Column column) {
 		Column old = getColumn( column );
 		if ( old == null ) {
 			columns.put( column.getCanonicalName(), column );
 			column.uniqueInteger = columns.size();
 		}
 		else {
 			column.uniqueInteger = old.uniqueInteger;
 		}
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
 	public Iterator getColumnIterator() {
 		return columns.values().iterator();
 	}
 
 	public Iterator<Index> getIndexIterator() {
 		return indexes.values().iterator();
 	}
 
 	public Iterator getForeignKeyIterator() {
 		return foreignKeys.values().iterator();
 	}
 
 	public Iterator<UniqueKey> getUniqueKeyIterator() {
 		return getUniqueKeys().values().iterator();
 	}
 
 	Map<String, UniqueKey> getUniqueKeys() {
 		cleanseUniqueKeyMapIfNeeded();
 		return uniqueKeys;
 	}
 
 	private int sizeOfUniqueKeyMapOnLastCleanse = 0;
 
 	private void cleanseUniqueKeyMapIfNeeded() {
 		if ( uniqueKeys.size() == sizeOfUniqueKeyMapOnLastCleanse ) {
 			// nothing to do
 			return;
 		}
 		cleanseUniqueKeyMap();
 		sizeOfUniqueKeyMapOnLastCleanse = uniqueKeys.size();
 	}
 
 	private void cleanseUniqueKeyMap() {
 		// We need to account for a few conditions here...
 		// 	1) If there are multiple unique keys contained in the uniqueKeys Map, we need to deduplicate
 		// 		any sharing the same columns as other defined unique keys; this is needed for the annotation
 		// 		processor since it creates unique constraints automagically for the user
 		//	2) Remove any unique keys that share the same columns as the primary key; again, this is
 		//		needed for the annotation processor to handle @Id @OneToOne cases.  In such cases the
 		//		unique key is unnecessary because a primary key is already unique by definition.  We handle
 		//		this case specifically because some databases fail if you try to apply a unique key to
 		//		the primary key columns which causes schema export to fail in these cases.
 		if ( uniqueKeys.isEmpty() ) {
 			// nothing to do
 			return;
 		}
 		else if ( uniqueKeys.size() == 1 ) {
 			// we have to worry about condition 2 above, but not condition 1
 			final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeys.entrySet().iterator().next();
 			if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 				uniqueKeys.remove( uniqueKeyEntry.getKey() );
 			}
 		}
 		else {
 			// we have to check both conditions 1 and 2
 			final Iterator<Map.Entry<String,UniqueKey>> uniqueKeyEntries = uniqueKeys.entrySet().iterator();
 			while ( uniqueKeyEntries.hasNext() ) {
 				final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeyEntries.next();
 				final UniqueKey uniqueKey = uniqueKeyEntry.getValue();
 				boolean removeIt = false;
 
 				// condition 1 : check against other unique keys
 				for ( UniqueKey otherUniqueKey : uniqueKeys.values() ) {
 					// make sure its not the same unique key
 					if ( uniqueKeyEntry.getValue() == otherUniqueKey ) {
 						continue;
 					}
 					if ( otherUniqueKey.getColumns().containsAll( uniqueKey.getColumns() )
 							&& uniqueKey.getColumns().containsAll( otherUniqueKey.getColumns() ) ) {
 						removeIt = true;
 						break;
 					}
 				}
 
 				// condition 2 : check against pk
 				if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 					removeIt = true;
 				}
 
 				if ( removeIt ) {
 					//uniqueKeys.remove( uniqueKeyEntry.getKey() );
 					uniqueKeyEntries.remove();
 				}
 			}
 
 		}
 	}
 
 	private boolean isSameAsPrimaryKeyColumns(UniqueKey uniqueKey) {
 		if ( primaryKey == null || ! primaryKey.columnIterator().hasNext() ) {
 			// happens for many-to-many tables
 			return false;
 		}
 		return primaryKey.getColumns().containsAll( uniqueKey.getColumns() )
 				&& uniqueKey.getColumns().containsAll( primaryKey.getColumns() );
 	}
 
 	@Override
 	public int hashCode() {
 		final int prime = 31;
 		int result = 1;
 		result = prime * result
 			+ ((catalog == null) ? 0 : isCatalogQuoted() ? catalog.hashCode() : catalog.toLowerCase().hashCode());
 		result = prime * result + ((name == null) ? 0 : isQuoted() ? name.hashCode() : name.toLowerCase().hashCode());
 		result = prime * result
 			+ ((schema == null) ? 0 : isSchemaQuoted() ? schema.hashCode() : schema.toLowerCase().hashCode());
 		return result;
 	}
 
 	@Override
 	public boolean equals(Object object) {
 		return object instanceof Table && equals((Table) object);
 	}
 
 	public boolean equals(Table table) {
 		if (null == table) {
 			return false;
 		}
 		if (this == table) {
 			return true;
 		}
 
 		return isQuoted() ? name.equals(table.getName()) : name.equalsIgnoreCase(table.getName())
 			&& ((schema == null && table.getSchema() != null) ? false : (schema == null) ? true : isSchemaQuoted() ? schema.equals(table.getSchema()) : schema.equalsIgnoreCase(table.getSchema()))
 			&& ((catalog == null && table.getCatalog() != null) ? false : (catalog == null) ? true : isCatalogQuoted() ? catalog.equals(table.getCatalog()) : catalog.equalsIgnoreCase(table.getCatalog()));
 	}
 	
 	public void validateColumns(Dialect dialect, Mapping mapping, TableMetadata tableInfo) {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			ColumnMetadata columnInfo = tableInfo.getColumnMetadata( col.getName() );
 
 			if ( columnInfo == null ) {
 				throw new HibernateException( "Missing column: " + col.getName() + " in " + Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()));
 			}
 			else {
 				final boolean typesMatch = col.getSqlType( dialect, mapping ).toLowerCase()
 						.startsWith( columnInfo.getTypeName().toLowerCase() )
 						|| columnInfo.getTypeCode() == col.getSqlTypeCode( mapping );
 				if ( !typesMatch ) {
 					throw new HibernateException(
 							"Wrong column type in " +
 							Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()) +
 							" for column " + col.getName() +
 							". Found: " + columnInfo.getTypeName().toLowerCase() +
 							", expected: " + col.getSqlType( dialect, mapping )
 					);
 				}
 			}
 		}
 
 	}
 
 	public Iterator sqlAlterStrings(Dialect dialect, Mapping p, TableMetadata tableInfo, String defaultCatalog,
 									String defaultSchema)
 			throws HibernateException {
 
 		StringBuilder root = new StringBuilder( "alter table " )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( ' ' )
 				.append( dialect.getAddColumnString() );
 
 		Iterator iter = getColumnIterator();
 		List results = new ArrayList();
 		
 		while ( iter.hasNext() ) {
 			Column column = (Column) iter.next();
 
 			ColumnMetadata columnInfo = tableInfo.getColumnMetadata( column.getName() );
 
 			if ( columnInfo == null ) {
 				// the column doesnt exist at all.
 				StringBuilder alter = new StringBuilder( root.toString() )
 						.append( ' ' )
 						.append( column.getQuotedName( dialect ) )
 						.append( ' ' )
 						.append( column.getSqlType( dialect, p ) );
 
 				String defaultValue = column.getDefaultValue();
 				if ( defaultValue != null ) {
 					alter.append( " default " ).append( defaultValue );
 				}
 
 				if ( column.isNullable() ) {
 					alter.append( dialect.getNullColumnString() );
 				}
 				else {
 					alter.append( " not null" );
 				}
 
 				if ( column.isUnique() ) {
 					UniqueKey uk = getOrCreateUniqueKey( 
 							StringHelper.randomFixedLengthHex("UK_"));
 					uk.addColumn( column );
 					alter.append( dialect.getUniqueDelegate()
-							.applyUniqueToColumn( column ) );
+							.getColumnDefinitionUniquenessFragment( column ) );
 				}
 
 				if ( column.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 					alter.append( " check(" )
 							.append( column.getCheckConstraint() )
 							.append( ")" );
 				}
 
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					alter.append( dialect.getColumnComment( columnComment ) );
 				}
 
 				results.add( alter.toString() );
 			}
 
 		}
 
 		return results.iterator();
 	}
 
 	public boolean hasPrimaryKey() {
 		return getPrimaryKey() != null;
 	}
 
 	public String sqlTemporaryTableCreateString(Dialect dialect, Mapping mapping) throws HibernateException {
 		StringBuilder buffer = new StringBuilder( dialect.getCreateTemporaryTableString() )
 				.append( ' ' )
 				.append( name )
 				.append( " (" );
 		Iterator itr = getColumnIterator();
 		while ( itr.hasNext() ) {
 			final Column column = (Column) itr.next();
 			buffer.append( column.getQuotedName( dialect ) ).append( ' ' );
 			buffer.append( column.getSqlType( dialect, mapping ) );
 			if ( column.isNullable() ) {
 				buffer.append( dialect.getNullColumnString() );
 			}
 			else {
 				buffer.append( " not null" );
 			}
 			if ( itr.hasNext() ) {
 				buffer.append( ", " );
 			}
 		}
 		buffer.append( ") " );
 		buffer.append( dialect.getCreateTemporaryTablePostfix() );
 		return buffer.toString();
 	}
 
 	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
 		StringBuilder buf = new StringBuilder( hasPrimaryKey() ? dialect.getCreateTableString() : dialect.getCreateMultisetTableString() )
 				.append( ' ' )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( " (" );
 
 		boolean identityColumn = idValue != null && idValue.isIdentityColumn( p.getIdentifierGeneratorFactory(), dialect );
 
 		// Try to find out the name of the primary key to create it as identity if the IdentityGenerator is used
 		String pkname = null;
 		if ( hasPrimaryKey() && identityColumn ) {
 			pkname = ( (Column) getPrimaryKey().getColumnIterator().next() ).getQuotedName( dialect );
 		}
 
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			buf.append( col.getQuotedName( dialect ) )
 					.append( ' ' );
 
 			if ( identityColumn && col.getQuotedName( dialect ).equals( pkname ) ) {
 				// to support dialects that have their own identity data type
 				if ( dialect.hasDataTypeInIdentityColumn() ) {
 					buf.append( col.getSqlType( dialect, p ) );
 				}
 				buf.append( ' ' )
 						.append( dialect.getIdentityColumnString( col.getSqlTypeCode( p ) ) );
 			}
 			else {
 
 				buf.append( col.getSqlType( dialect, p ) );
 
 				String defaultValue = col.getDefaultValue();
 				if ( defaultValue != null ) {
 					buf.append( " default " ).append( defaultValue );
 				}
 
 				if ( col.isNullable() ) {
 					buf.append( dialect.getNullColumnString() );
 				}
 				else {
 					buf.append( " not null" );
 				}
 
 			}
 			
 			if ( col.isUnique() ) {
 				UniqueKey uk = getOrCreateUniqueKey( 
 						StringHelper.randomFixedLengthHex("UK_"));
 				uk.addColumn( col );
 				buf.append( dialect.getUniqueDelegate()
-						.applyUniqueToColumn( col ) );
+						.getColumnDefinitionUniquenessFragment( col ) );
 			}
 				
 			if ( col.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 				buf.append( " check (" )
 						.append( col.getCheckConstraint() )
 						.append( ")" );
 			}
 
 			String columnComment = col.getComment();
 			if ( columnComment != null ) {
 				buf.append( dialect.getColumnComment( columnComment ) );
 			}
 
 			if ( iter.hasNext() ) {
 				buf.append( ", " );
 			}
 
 		}
 		if ( hasPrimaryKey() ) {
 			buf.append( ", " )
 					.append( getPrimaryKey().sqlConstraintString( dialect ) );
 		}
 
-		buf.append( dialect.getUniqueDelegate().applyUniquesToTable( this ) );
+		buf.append( dialect.getUniqueDelegate().getTableCreationUniqueConstraintsFragment( this ) );
 
 		if ( dialect.supportsTableCheck() ) {
 			Iterator chiter = checkConstraints.iterator();
 			while ( chiter.hasNext() ) {
 				buf.append( ", check (" )
 						.append( chiter.next() )
 						.append( ')' );
 			}
 		}
 
 		buf.append( ')' );
 
 		if ( comment != null ) {
 			buf.append( dialect.getTableComment( comment ) );
 		}
 
 		return buf.append( dialect.getTableTypeString() ).toString();
 	}
 
 	public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		return dialect.getDropTableString( getQualifiedName( dialect, defaultCatalog, defaultSchema ) );
 	}
 
 	public PrimaryKey getPrimaryKey() {
 		return primaryKey;
 	}
 
 	public void setPrimaryKey(PrimaryKey primaryKey) {
 		this.primaryKey = primaryKey;
 	}
 
 	public Index getOrCreateIndex(String indexName) {
 
 		Index index =  indexes.get( indexName );
 
 		if ( index == null ) {
 			index = new Index();
 			index.setName( indexName );
 			index.setTable( this );
 			indexes.put( indexName, index );
 		}
 
 		return index;
 	}
 
 	public Index getIndex(String indexName) {
 		return  indexes.get( indexName );
 	}
 
 	public Index addIndex(Index index) {
 		Index current =  indexes.get( index.getName() );
 		if ( current != null ) {
 			throw new MappingException( "Index " + index.getName() + " already exists!" );
 		}
 		indexes.put( index.getName(), index );
 		return index;
 	}
 
 	public UniqueKey addUniqueKey(UniqueKey uniqueKey) {
 		UniqueKey current = uniqueKeys.get( uniqueKey.getName() );
 		if ( current != null ) {
 			throw new MappingException( "UniqueKey " + uniqueKey.getName() + " already exists!" );
 		}
 		uniqueKeys.put( uniqueKey.getName(), uniqueKey );
 		return uniqueKey;
 	}
 
 	public UniqueKey createUniqueKey(List keyColumns) {
 		String keyName = StringHelper.randomFixedLengthHex("UK_");
 		UniqueKey uk = getOrCreateUniqueKey( keyName );
 		uk.addColumns( keyColumns.iterator() );
 		return uk;
 	}
 
 	public UniqueKey getUniqueKey(String keyName) {
 		return uniqueKeys.get( keyName );
 	}
 
 	public UniqueKey getOrCreateUniqueKey(String keyName) {
 		UniqueKey uk = uniqueKeys.get( keyName );
 
 		if ( uk == null ) {
 			uk = new UniqueKey();
 			uk.setName( keyName );
 			uk.setTable( this );
 			uniqueKeys.put( keyName, uk );
 		}
 		return uk;
 	}
 
 	public void createForeignKeys() {
 	}
 
 	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName) {
 		return createForeignKey( keyName, keyColumns, referencedEntityName, null );
 	}
 
 	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName,
 									   List referencedColumns) {
 		Object key = new ForeignKeyKey( keyColumns, referencedEntityName, referencedColumns );
 
 		ForeignKey fk = (ForeignKey) foreignKeys.get( key );
 		if ( fk == null ) {
 			fk = new ForeignKey();
 			if ( keyName != null ) {
 				fk.setName( keyName );
 			}
 			else {
 				fk.setName( StringHelper.randomFixedLengthHex("FK_") );
 			}
 			fk.setTable( this );
 			foreignKeys.put( key, fk );
 			fk.setReferencedEntityName( referencedEntityName );
 			fk.addColumns( keyColumns.iterator() );
 			if ( referencedColumns != null ) {
 				fk.addReferencedColumns( referencedColumns.iterator() );
 			}
 		}
 
 		if ( keyName != null ) {
 			fk.setName( keyName );
 		}
 
 		return fk;
 	}
 
 
 
 	public String getSchema() {
 		return schema;
 	}
 
 	public void setSchema(String schema) {
 		if ( schema != null && schema.charAt( 0 ) == '`' ) {
 			schemaQuoted = true;
 			this.schema = schema.substring( 1, schema.length() - 1 );
 		}
 		else {
 			this.schema = schema;
 		}
 	}
 
 	public String getCatalog() {
 		return catalog;
 	}
 
 	public void setCatalog(String catalog) {
 		if ( catalog != null && catalog.charAt( 0 ) == '`' ) {
 			catalogQuoted = true;
 			this.catalog = catalog.substring( 1, catalog.length() - 1 );
 		}
 		else {
 			this.catalog = catalog;
 		}
 	}
 
 	// This must be done outside of Table, rather than statically, to ensure
 	// deterministic alias names.  See HHH-2448.
 	public void setUniqueInteger( int uniqueInteger ) {
 		this.uniqueInteger = uniqueInteger;
 	}
 
 	public int getUniqueInteger() {
 		return uniqueInteger;
 	}
 
 	public void setIdentifierValue(KeyValue idValue) {
 		this.idValue = idValue;
 	}
 
 	public KeyValue getIdentifierValue() {
 		return idValue;
 	}
 
 	public boolean isSchemaQuoted() {
 		return schemaQuoted;
 	}
 	public boolean isCatalogQuoted() {
 		return catalogQuoted;
 	}
 
 	public boolean isQuoted() {
 		return quoted;
 	}
 
 	public void setQuoted(boolean quoted) {
 		this.quoted = quoted;
 	}
 
 	public void addCheckConstraint(String constraint) {
 		checkConstraints.add( constraint );
 	}
 
 	public boolean containsColumn(Column column) {
 		return columns.containsValue( column );
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public String toString() {
 		StringBuilder buf = new StringBuilder().append( getClass().getName() )
 				.append( '(' );
 		if ( getCatalog() != null ) {
 			buf.append( getCatalog() + "." );
 		}
 		if ( getSchema() != null ) {
 			buf.append( getSchema() + "." );
 		}
 		buf.append( getName() ).append( ')' );
 		return buf.toString();
 	}
 
 	public String getSubselect() {
 		return subselect;
 	}
 
 	public void setSubselect(String subselect) {
 		this.subselect = subselect;
 	}
 
 	public boolean isSubselect() {
 		return subselect != null;
 	}
 
 	public boolean isAbstractUnionTable() {
 		return hasDenormalizedTables() && isAbstract;
 	}
 
 	public boolean hasDenormalizedTables() {
 		return hasDenormalizedTables;
 	}
 
 	void setHasDenormalizedTables() {
 		hasDenormalizedTables = true;
 	}
 
 	public void setAbstract(boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public boolean isPhysicalTable() {
 		return !isSubselect() && !isAbstractUnionTable();
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
 
 	public Iterator getCheckConstraintsIterator() {
 		return checkConstraints.iterator();
 	}
 	
 	public String getNaturalIdUniqueKeyName() {
 		return naturalIdUniqueKeyName;
 	}
 
 	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		List comments = new ArrayList();
 		if ( dialect.supportsCommentOn() ) {
 			String tableName = getQualifiedName( dialect, defaultCatalog, defaultSchema );
 			if ( comment != null ) {
 				StringBuilder buf = new StringBuilder()
 						.append( "comment on table " )
 						.append( tableName )
 						.append( " is '" )
 						.append( comment )
 						.append( "'" );
 				comments.add( buf.toString() );
 			}
 			Iterator iter = getColumnIterator();
 			while ( iter.hasNext() ) {
 				Column column = (Column) iter.next();
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					StringBuilder buf = new StringBuilder()
 							.append( "comment on column " )
 							.append( tableName )
 							.append( '.' )
 							.append( column.getQuotedName( dialect ) )
 							.append( " is '" )
 							.append( columnComment )
 							.append( "'" );
 					comments.add( buf.toString() );
 				}
 			}
 		}
 		return comments.iterator();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java b/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
index 8dbceb47a6..06ee87093a 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
@@ -1,75 +1,77 @@
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
 import java.util.*;
 import java.util.Map;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * A relational unique key constraint
  *
  * @author Brett Meyer
  */
 public class UniqueKey extends Constraint {
 	private java.util.Map<Column, String> columnOrderMap = new HashMap<Column, String>(  );
 
 	@Override
     public String sqlConstraintString(
 			Dialect dialect,
 			String constraintName,
 			String defaultCatalog,
 			String defaultSchema) {
 //		return dialect.getUniqueDelegate().uniqueConstraintSql( this );
 		// Not used.
 		return "";
 	}
 
 	@Override
     public String sqlCreateString(Dialect dialect, Mapping p,
     		String defaultCatalog, String defaultSchema) {
-		return dialect.getUniqueDelegate().applyUniquesOnAlter(
-				this, defaultCatalog, defaultSchema );
+		return dialect.getUniqueDelegate().getAlterTableToAddUniqueKeyCommand(
+				this, defaultCatalog, defaultSchema
+		);
 	}
 
 	@Override
     public String sqlDropString(Dialect dialect, String defaultCatalog,
     		String defaultSchema) {
-		return dialect.getUniqueDelegate().dropUniquesOnAlter(
-				this, defaultCatalog, defaultSchema );
+		return dialect.getUniqueDelegate().getAlterTableToDropUniqueKeyCommand(
+				this, defaultCatalog, defaultSchema
+		);
 	}
 
 	public void addColumn(Column column, String order) {
 		addColumn( column );
 		if ( StringHelper.isNotEmpty( order ) ) {
 			columnOrderMap.put( column, order );
 		}
 	}
 
 	public Map<Column, String> getColumnOrderMap() {
 		return columnOrderMap;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Table.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Table.java
index b252c01951..f88c2fdf08 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Table.java
@@ -1,278 +1,278 @@
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
 
 import java.util.ArrayList;
 import java.util.LinkedHashMap;
 import java.util.List;
 
 import org.hibernate.dialect.Dialect;
 
 /**
  * Models the concept of a relational <tt>TABLE</tt> (or <tt>VIEW</tt>).
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class Table extends AbstractTableSpecification implements Exportable {
 	private final Schema database;
 	private final Identifier tableName;
 	private final ObjectName objectName;
 	private final String qualifiedName;
 
 	private final LinkedHashMap<String,Index> indexes = new LinkedHashMap<String,Index>();
 	private final LinkedHashMap<String,UniqueKey> uniqueKeys = new LinkedHashMap<String,UniqueKey>();
 	private final List<CheckConstraint> checkConstraints = new ArrayList<CheckConstraint>();
 	private final List<String> comments = new ArrayList<String>();
 
 	public Table(Schema database, String tableName) {
 		this( database, Identifier.toIdentifier( tableName ) );
 	}
 
 	public Table(Schema database, Identifier tableName) {
 		this.database = database;
 		this.tableName = tableName;
 		objectName = new ObjectName( database.getName().getSchema(), database.getName().getCatalog(), tableName );
 		this.qualifiedName = objectName.toText();
 	}
 
 	@Override
 	public Schema getSchema() {
 		return database;
 	}
 
 	public Identifier getTableName() {
 		return tableName;
 	}
 
 	@Override
 	public String getLoggableValueQualifier() {
 		return qualifiedName;
 	}
 
 	@Override
 	public String getExportIdentifier() {
 		return qualifiedName;
 	}
 
 	@Override
 	public String toLoggableString() {
 		return qualifiedName;
 	}
 
 	@Override
 	public Iterable<Index> getIndexes() {
 		return indexes.values();
 	}
 
 	public Index getOrCreateIndex(String name) {
 		if( indexes.containsKey( name ) ){
 			return indexes.get( name );
 		}
 		Index index = new Index( this, name );
 		indexes.put(name, index );
 		return index;
 	}
 
 	@Override
 	public Iterable<UniqueKey> getUniqueKeys() {
 		return uniqueKeys.values();
 	}
 
 	public UniqueKey getOrCreateUniqueKey(String name) {
 		if( uniqueKeys.containsKey( name ) ){
 			return uniqueKeys.get( name );
 		}
 		UniqueKey uniqueKey = new UniqueKey( this, name );
 		uniqueKeys.put(name, uniqueKey );
 		return uniqueKey;
 	}
 
 	@Override
 	public Iterable<CheckConstraint> getCheckConstraints() {
 		return checkConstraints;
 	}
 
 	@Override
 	public void addCheckConstraint(String checkCondition) {
         //todo ? StringHelper.isEmpty( checkCondition );
         //todo default name?
 		checkConstraints.add( new CheckConstraint( this, "", checkCondition ) );
 	}
 
 	@Override
 	public Iterable<String> getComments() {
 		return comments;
 	}
 
 	@Override
 	public void addComment(String comment) {
 		comments.add( comment );
 	}
 
 	@Override
 	public String getQualifiedName(Dialect dialect) {
 		return objectName.toText( dialect );
 	}
 
 	public String[] sqlCreateStrings(Dialect dialect) {
 		boolean hasPrimaryKey = getPrimaryKey().getColumns().iterator().hasNext();
 		StringBuilder buf =
 				new StringBuilder(
 						hasPrimaryKey ? dialect.getCreateTableString() : dialect.getCreateMultisetTableString() )
 				.append( ' ' )
 				.append( objectName.toText( dialect ) )
 				.append( " (" );
 
 
 		// TODO: fix this when identity columns are supported by new metadata (HHH-6436)
 		// for now, assume false
 		//boolean identityColumn = idValue != null && idValue.isIdentityColumn( metadata.getIdentifierGeneratorFactory(), dialect );
 		boolean isPrimaryKeyIdentity = false;
 
 		// Try to find out the name of the primary key to create it as identity if the IdentityGenerator is used
 		String pkColName = null;
 		if ( hasPrimaryKey && isPrimaryKeyIdentity ) {
 			Column pkColumn = getPrimaryKey().getColumns().iterator().next();
 			pkColName = pkColumn.getColumnName().encloseInQuotesIfQuoted( dialect );
 		}
 
 		boolean isFirst = true;
 		for ( SimpleValue simpleValue : values() ) {
 			if ( ! Column.class.isInstance( simpleValue ) ) {
 				continue;
 			}
 			if ( isFirst ) {
 				isFirst = false;
 			}
 			else {
 				buf.append( ", " );
 			}
 			Column col = ( Column ) simpleValue;
 			String colName = col.getColumnName().encloseInQuotesIfQuoted( dialect );
 
 			buf.append( colName ).append( ' ' );
 
 			if ( isPrimaryKeyIdentity && colName.equals( pkColName ) ) {
 				// to support dialects that have their own identity data type
 				if ( dialect.hasDataTypeInIdentityColumn() ) {
 					buf.append( getTypeString( col, dialect ) );
 				}
 				buf.append( ' ' )
 						.append( dialect.getIdentityColumnString( col.getDatatype().getTypeCode() ) );
 			}
 			else {
 				buf.append( getTypeString( col, dialect ) );
 
 				String defaultValue = col.getDefaultValue();
 				if ( defaultValue != null ) {
 					buf.append( " default " ).append( defaultValue );
 				}
 
 				if ( col.isNullable() ) {
 					buf.append( dialect.getNullColumnString() );
 				}
 				else {
 					buf.append( " not null" );
 				}
 
 			}
 
 			if ( col.isUnique() ) {
 				UniqueKey uk = getOrCreateUniqueKey( col.getColumnName()
 						.encloseInQuotesIfQuoted( dialect ) + '_' );
 				uk.addColumn( col );
 				buf.append( dialect.getUniqueDelegate()
-						.applyUniqueToColumn( col ) );
+						.getColumnDefinitionUniquenessFragment( col ) );
 			}
 
 			if ( col.getCheckCondition() != null && dialect.supportsColumnCheck() ) {
 				buf.append( " check (" )
 						.append( col.getCheckCondition() )
 						.append( ")" );
 			}
 
 			String columnComment = col.getComment();
 			if ( columnComment != null ) {
 				buf.append( dialect.getColumnComment( columnComment ) );
 			}
 		}
 		if ( hasPrimaryKey ) {
 			buf.append( ", " )
 					.append( getPrimaryKey().sqlConstraintStringInCreateTable( dialect ) );
 		}
 
-		buf.append( dialect.getUniqueDelegate().applyUniquesToTable( this ) );
+		buf.append( dialect.getUniqueDelegate().getTableCreationUniqueConstraintsFragment( this ) );
 
 		if ( dialect.supportsTableCheck() ) {
 			for ( CheckConstraint checkConstraint : checkConstraints ) {
 				buf.append( ", check (" )
 						.append( checkConstraint )
 						.append( ')' );
 			}
 		}
 
 		buf.append( ')' );
 		buf.append( dialect.getTableTypeString() );
 
 		String[] sqlStrings = new String[ comments.size() + 1 ];
 		sqlStrings[ 0 ] = buf.toString();
 
 		for ( int i = 0 ; i < comments.size(); i++ ) {
 			sqlStrings[ i + 1 ] = dialect.getTableComment( comments.get( i ) );
 		}
 
 		return sqlStrings;
 	}
 
 	private static String getTypeString(Column col, Dialect dialect) {
 		String typeString = null;
 		if ( col.getSqlType() != null ) {
 			typeString = col.getSqlType();
 		}
 		else {
 			Size size = col.getSize() == null ?
 					new Size( ) :
 					col.getSize();
 
 			typeString = dialect.getTypeName(
 						col.getDatatype().getTypeCode(),
 						size.getLength(),
 						size.getPrecision(),
 						size.getScale()
 			);
 		}
 		return typeString;
 	}
 
 	@Override
 	public String[] sqlDropStrings(Dialect dialect) {
 		return new String[] { dialect.getDropTableString( getQualifiedName( dialect ) ) };
 	}
 
 	@Override
 	public String toString() {
 		return "Table{name=" + qualifiedName + '}';
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java
index f11cb2ee89..f130f2ebe9 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java
@@ -1,67 +1,67 @@
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
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Models a SQL <tt>INDEX</tt> defined as UNIQUE
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class UniqueKey extends AbstractConstraint implements Constraint {
 	protected UniqueKey(Table table, String name) {
 		super( table, name );
 	}
 
 	@Override
 	public String getExportIdentifier() {
 		StringBuilder sb = new StringBuilder( getTable().getLoggableValueQualifier() );
 		sb.append( ".UK" );
 		for ( Column column : getColumns() ) {
 			sb.append( '_' ).append( column.getColumnName().getName() );
 		}
 		return sb.toString();
 	}
 
 	@Override
 	public String[] sqlCreateStrings(Dialect dialect) {
-		String s = dialect.getUniqueDelegate().applyUniquesOnAlter(this);
+		String s = dialect.getUniqueDelegate().getAlterTableToAddUniqueKeyCommand( this );
 		return StringHelper.toArrayElement( s );
 	}
 
 	@Override
 	public String[] sqlDropStrings(Dialect dialect) {
-		String s = dialect.getUniqueDelegate().dropUniquesOnAlter(this);
+		String s = dialect.getUniqueDelegate().getAlterTableToDropUniqueKeyCommand( this );
 		return StringHelper.toArrayElement( s );
 	}
 
 	@Override
     protected String sqlConstraintStringInAlterTable(Dialect dialect) {
 		// not used
 		return "";
 	}
 }
