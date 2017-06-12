diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Example.java b/hibernate-core/src/main/java/org/hibernate/criterion/Example.java
index d35d86deb7..44a9abc6fe 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Example.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Example.java
@@ -1,395 +1,500 @@
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
+
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.Criteria;
 import org.hibernate.EntityMode;
-import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * Support for query by example.
+ *
  * <pre>
  * List results = session.createCriteria(Parent.class)
  *     .add( Example.create(parent).ignoreCase() )
  *     .createCriteria("child")
  *         .add( Example.create( parent.getChild() ) )
  *     .list();
  * </pre>
- * "Examples" may be mixed and matched with "Expressions" in the same <tt>Criteria</tt>.
+ *
+ * "Examples" may be mixed and matched with "Expressions" in the same Criteria.
+ *
  * @see org.hibernate.Criteria
  * @author Gavin King
  */
 
 public class Example implements Criterion {
-
-	private final Object entity;
-	private final Set excludedProperties = new HashSet();
+	private final Object exampleEntity;
 	private PropertySelector selector;
+
 	private boolean isLikeEnabled;
 	private Character escapeCharacter;
 	private boolean isIgnoreCaseEnabled;
 	private MatchMode matchMode;
 
+	private final Set<String> excludedProperties = new HashSet<String>();
+
 	/**
-	 * A strategy for choosing property values for inclusion in the query
-	 * criteria
+	 * Create a new Example criterion instance, which includes all non-null properties by default
+	 *
+	 * @param exampleEntity The example bean to use.
+	 *
+	 * @return a new instance of Example
 	 */
-
-	public static interface PropertySelector extends Serializable {
-		public boolean include(Object propertyValue, String propertyName, Type type);
-	}
-
-	private static final PropertySelector NOT_NULL = new NotNullPropertySelector();
-	private static final PropertySelector ALL = new AllPropertySelector();
-	private static final PropertySelector NOT_NULL_OR_ZERO = new NotNullOrZeroPropertySelector();
-
-	static final class AllPropertySelector implements PropertySelector {
-		public boolean include(Object object, String propertyName, Type type) {
-			return true;
-		}
-		
-		private Object readResolve() {
-			return ALL;
+	public static Example create(Object exampleEntity) {
+		if ( exampleEntity == null ) {
+			throw new NullPointerException( "null example entity" );
 		}
+		return new Example( exampleEntity, NotNullPropertySelector.INSTANCE );
 	}
 
-	static final class NotNullPropertySelector implements PropertySelector {
-		public boolean include(Object object, String propertyName, Type type) {
-			return object!=null;
-		}
-		
-		private Object readResolve() {
-			return NOT_NULL;
-		}
-	}
-
-	static final class NotNullOrZeroPropertySelector implements PropertySelector {
-		public boolean include(Object object, String propertyName, Type type) {
-			return object!=null && (
-				!(object instanceof Number) || ( (Number) object ).longValue()!=0
-			);
-		}
-		
-		private Object readResolve() {
-			return NOT_NULL_OR_ZERO;
-		}
+	/**
+	 * Allow subclasses to instantiate as needed.
+	 *
+	 * @param exampleEntity The example bean
+	 * @param selector The property selector to use
+	 */
+	protected Example(Object exampleEntity, PropertySelector selector) {
+		this.exampleEntity = exampleEntity;
+		this.selector = selector;
 	}
 
 	/**
-	 * Set escape character for "like" clause
+	 * Set escape character for "like" clause if like matching was enabled
+	 *
+	 * @param escapeCharacter The escape character
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see #enableLike
 	 */
 	public Example setEscapeCharacter(Character escapeCharacter) {
 		this.escapeCharacter = escapeCharacter;
 		return this;
 	}
 
 	/**
-	 * Set the property selector
+	 * Use the "like" operator for all string-valued properties.  This form implicitly uses {@link MatchMode#EXACT}
+	 *
+	 * @return {@code this}, for method chaining
 	 */
-	public Example setPropertySelector(PropertySelector selector) {
-		this.selector = selector;
-		return this;
+	public Example enableLike() {
+		return enableLike( MatchMode.EXACT );
 	}
 
 	/**
-	 * Exclude zero-valued properties
+	 * Use the "like" operator for all string-valued properties
+	 *
+	 * @param matchMode The match mode to use.
+	 *
+	 * @return {@code this}, for method chaining
 	 */
-	public Example excludeZeroes() {
-		setPropertySelector(NOT_NULL_OR_ZERO);
+	public Example enableLike(MatchMode matchMode) {
+		this.isLikeEnabled = true;
+		this.matchMode = matchMode;
 		return this;
 	}
 
 	/**
-	 * Don't exclude null or zero-valued properties
+	 * Ignore case for all string-valued properties
+	 *
+	 * @return {@code this}, for method chaining
 	 */
-	public Example excludeNone() {
-		setPropertySelector(ALL);
+	public Example ignoreCase() {
+		this.isIgnoreCaseEnabled = true;
 		return this;
 	}
 
 	/**
-	 * Use the "like" operator for all string-valued properties
+	 * Set the property selector to use.
+	 *
+	 * The property selector operates separate from excluding a property.
+	 *
+	 * @param selector The selector to use
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see #excludeProperty
 	 */
-	public Example enableLike(MatchMode matchMode) {
-		isLikeEnabled = true;
-		this.matchMode = matchMode;
+	public Example setPropertySelector(PropertySelector selector) {
+		this.selector = selector;
 		return this;
 	}
 
 	/**
-	 * Use the "like" operator for all string-valued properties
+	 * Exclude zero-valued properties.
+	 *
+	 * Equivalent to calling {@link #setPropertySelector} passing in {@link NotNullOrZeroPropertySelector#INSTANCE}
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see #setPropertySelector
 	 */
-	public Example enableLike() {
-		return enableLike(MatchMode.EXACT);
+	public Example excludeZeroes() {
+		setPropertySelector( NotNullOrZeroPropertySelector.INSTANCE );
+		return this;
 	}
 
 	/**
-	 * Ignore case for all string-valued properties
+	 * Include all properties.
+	 *
+	 * Equivalent to calling {@link #setPropertySelector} passing in {@link AllPropertySelector#INSTANCE}
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see #setPropertySelector
 	 */
-	public Example ignoreCase() {
-		isIgnoreCaseEnabled = true;
+	public Example excludeNone() {
+		setPropertySelector( AllPropertySelector.INSTANCE );
 		return this;
 	}
 
 	/**
-	 * Exclude a particular named property
+	 * Exclude a particular property by name.
+	 *
+	 * @param name The name of the property to exclude
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see #setPropertySelector
 	 */
 	public Example excludeProperty(String name) {
-		excludedProperties.add(name);
+		excludedProperties.add( name );
 		return this;
 	}
 
-	/**
-	 * Create a new instance, which includes all non-null properties
-	 * by default
-	 * @param entity
-	 * @return a new instance of <tt>Example</tt>
-	 */
-	public static Example create(Object entity) {
-		if (entity==null) throw new NullPointerException("null example");
-		return new Example(entity, NOT_NULL);
-	}
-
-	protected Example(Object entity, PropertySelector selector) {
-		this.entity = entity;
-		this.selector = selector;
-	}
-
-	public String toString() {
-		return "example (" + entity + ')';
-	}
-
-	private boolean isPropertyIncluded(Object value, String name, Type type) {
-		return !excludedProperties.contains(name) &&
-			!type.isAssociationType() &&
-			selector.include(value, name, type);
-	}
-
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
-		throws HibernateException {
-
-		StringBuilder buf = new StringBuilder().append('(');
-		EntityPersister meta = criteriaQuery.getFactory().getEntityPersister( criteriaQuery.getEntityName(criteria) );
-		String[] propertyNames = meta.getPropertyNames();
-		Type[] propertyTypes = meta.getPropertyTypes();
-		//TODO: get all properties, not just the fetched ones!
-		Object[] propertyValues = meta.getPropertyValues( entity );
-		for (int i=0; i<propertyNames.length; i++) {
-			Object propertyValue = propertyValues[i];
-			String propertyName = propertyNames[i];
-
-			boolean isPropertyIncluded = i!=meta.getVersionProperty() &&
-				isPropertyIncluded( propertyValue, propertyName, propertyTypes[i] );
-			if (isPropertyIncluded) {
+	@Override
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
+		final StringBuilder buf = new StringBuilder().append( '(' );
+		final EntityPersister meta = criteriaQuery.getFactory().getEntityPersister(
+				criteriaQuery.getEntityName( criteria )
+		);
+		final String[] propertyNames = meta.getPropertyNames();
+		final Type[] propertyTypes = meta.getPropertyTypes();
+
+		final Object[] propertyValues = meta.getPropertyValues( exampleEntity );
+		for ( int i=0; i<propertyNames.length; i++ ) {
+			final Object propertyValue = propertyValues[i];
+			final String propertyName = propertyNames[i];
+
+			final boolean isVersionProperty = i == meta.getVersionProperty();
+			if ( ! isVersionProperty && isPropertyIncluded( propertyValue, propertyName, propertyTypes[i] ) ) {
 				if ( propertyTypes[i].isComponentType() ) {
 					appendComponentCondition(
 						propertyName,
 						propertyValue,
 						(CompositeType) propertyTypes[i],
 						criteria,
 						criteriaQuery,
 						buf
 					);
 				}
 				else {
 					appendPropertyCondition(
 						propertyName,
 						propertyValue,
 						criteria,
 						criteriaQuery,
 						buf
 					);
 				}
 			}
 		}
-		if ( buf.length()==1 ) buf.append("1=1"); //yuck!
-		return buf.append(')').toString();
+
+		if ( buf.length()==1 ) {
+			buf.append( "1=1" );
+		}
+
+		return buf.append( ')' ).toString();
 	}
 
-	private static final Object[] TYPED_VALUES = new TypedValue[0];
+	@SuppressWarnings("SimplifiableIfStatement")
+	private boolean isPropertyIncluded(Object value, String name, Type type) {
+		if ( excludedProperties.contains( name ) ) {
+			// was explicitly excluded
+			return false;
+		}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
+		if ( type.isAssociationType() ) {
+			// associations are implicitly excluded
+			return false;
+		}
 
-		EntityPersister meta = criteriaQuery.getFactory()
-				.getEntityPersister( criteriaQuery.getEntityName(criteria) );
-		String[] propertyNames = meta.getPropertyNames();
-		Type[] propertyTypes = meta.getPropertyTypes();
-		 //TODO: get all properties, not just the fetched ones!
-		Object[] values = meta.getPropertyValues( entity );
-		List list = new ArrayList();
-		for (int i=0; i<propertyNames.length; i++) {
-			Object value = values[i];
-			Type type = propertyTypes[i];
-			String name = propertyNames[i];
+		return selector.include( value, name, type );
+	}
+
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
+		final EntityPersister meta = criteriaQuery.getFactory().getEntityPersister(
+				criteriaQuery.getEntityName( criteria )
+		);
+		final String[] propertyNames = meta.getPropertyNames();
+		final Type[] propertyTypes = meta.getPropertyTypes();
 
-			boolean isPropertyIncluded = i!=meta.getVersionProperty() &&
-				isPropertyIncluded(value, name, type);
+		final Object[] values = meta.getPropertyValues( exampleEntity );
+		final List<TypedValue> list = new ArrayList<TypedValue>();
+		for ( int i=0; i<propertyNames.length; i++ ) {
+			final Object value = values[i];
+			final Type type = propertyTypes[i];
+			final String name = propertyNames[i];
 
-			if (isPropertyIncluded) {
+			final boolean isVersionProperty = i == meta.getVersionProperty();
+
+			if ( ! isVersionProperty && isPropertyIncluded( value, name, type ) ) {
 				if ( propertyTypes[i].isComponentType() ) {
-					addComponentTypedValues(name, value, (CompositeType) type, list, criteria, criteriaQuery);
+					addComponentTypedValues( name, value, (CompositeType) type, list, criteria, criteriaQuery );
 				}
 				else {
-					addPropertyTypedValue(value, type, list);
+					addPropertyTypedValue( value, type, list );
 				}
 			}
 		}
-		return (TypedValue[]) list.toArray(TYPED_VALUES);
-	}
-	
-	private EntityMode getEntityMode(Criteria criteria, CriteriaQuery criteriaQuery) {
-		EntityPersister meta = criteriaQuery.getFactory()
-				.getEntityPersister( criteriaQuery.getEntityName(criteria) );
-		EntityMode result = meta.getEntityMode();
-		if ( ! meta.getEntityMetamodel().getTuplizer().isInstance( entity ) ) {
-			throw new ClassCastException( entity.getClass().getName() );
-		}
-		return result;
+
+		return list.toArray( new TypedValue[ list.size() ] );
 	}
 
-	protected void addPropertyTypedValue(Object value, Type type, List list) {
-		if ( value!=null ) {
+	protected void addPropertyTypedValue(Object value, Type type, List<TypedValue> list) {
+		if ( value != null ) {
 			if ( value instanceof String ) {
 				String string = (String) value;
-				if (isIgnoreCaseEnabled) string = string.toLowerCase();
-				if (isLikeEnabled) string = matchMode.toMatchString(string);
+				if ( isIgnoreCaseEnabled ) {
+					string = string.toLowerCase();
+				}
+				if ( isLikeEnabled ) {
+					string = matchMode.toMatchString( string );
+				}
 				value = string;
 			}
-			list.add( new TypedValue(type, value, null) );
+			list.add( new TypedValue( type, value ) );
 		}
 	}
 
 	protected void addComponentTypedValues(
 			String path, 
 			Object component, 
 			CompositeType type,
-			List list, 
+			List<TypedValue> list,
 			Criteria criteria, 
-			CriteriaQuery criteriaQuery)
-	throws HibernateException {
-
-		if (component!=null) {
-			String[] propertyNames = type.getPropertyNames();
-			Type[] subtypes = type.getSubtypes();
-			Object[] values = type.getPropertyValues( component, getEntityMode(criteria, criteriaQuery) );
-			for (int i=0; i<propertyNames.length; i++) {
-				Object value = values[i];
-				Type subtype = subtypes[i];
-				String subpath = StringHelper.qualify( path, propertyNames[i] );
-				if ( isPropertyIncluded(value, subpath, subtype) ) {
+			CriteriaQuery criteriaQuery) {
+		if ( component != null ) {
+			final String[] propertyNames = type.getPropertyNames();
+			final Type[] subtypes = type.getSubtypes();
+			final Object[] values = type.getPropertyValues( component, getEntityMode( criteria, criteriaQuery ) );
+			for ( int i=0; i<propertyNames.length; i++ ) {
+				final Object value = values[i];
+				final Type subtype = subtypes[i];
+				final String subpath = StringHelper.qualify( path, propertyNames[i] );
+				if ( isPropertyIncluded( value, subpath, subtype ) ) {
 					if ( subtype.isComponentType() ) {
-						addComponentTypedValues(subpath, value, (CompositeType) subtype, list, criteria, criteriaQuery);
+						addComponentTypedValues( subpath, value, (CompositeType) subtype, list, criteria, criteriaQuery );
 					}
 					else {
-						addPropertyTypedValue(value, subtype, list);
+						addPropertyTypedValue( value, subtype, list );
 					}
 				}
 			}
 		}
 	}
 
+	private EntityMode getEntityMode(Criteria criteria, CriteriaQuery criteriaQuery) {
+		final EntityPersister meta = criteriaQuery.getFactory().getEntityPersister(
+				criteriaQuery.getEntityName( criteria )
+		);
+		final EntityMode result = meta.getEntityMode();
+		if ( ! meta.getEntityMetamodel().getTuplizer().isInstance( exampleEntity ) ) {
+			throw new ClassCastException( exampleEntity.getClass().getName() );
+		}
+		return result;
+	}
+
 	protected void appendPropertyCondition(
-		String propertyName,
-		Object propertyValue,
-		Criteria criteria,
-		CriteriaQuery cq,
-		StringBuilder buf)
-	throws HibernateException {
-		Criterion crit;
-		if ( propertyValue!=null ) {
-			boolean isString = propertyValue instanceof String;
+			String propertyName,
+			Object propertyValue,
+			Criteria criteria,
+			CriteriaQuery cq,
+			StringBuilder buf) {
+		final Criterion condition;
+		if ( propertyValue != null ) {
+			final boolean isString = propertyValue instanceof String;
 			if ( isLikeEnabled && isString ) {
-				crit = new LikeExpression(
+				condition = new LikeExpression(
 						propertyName,
-						( String ) propertyValue,
+						(String) propertyValue,
 						matchMode,
 						escapeCharacter,
 						isIgnoreCaseEnabled
 				);
 			}
 			else {
-				crit = new SimpleExpression( propertyName, propertyValue, "=", isIgnoreCaseEnabled && isString );
+				condition = new SimpleExpression( propertyName, propertyValue, "=", isIgnoreCaseEnabled && isString );
 			}
 		}
 		else {
-			crit = new NullExpression(propertyName);
+			condition = new NullExpression(propertyName);
+		}
+
+		final String conditionFragment = condition.toSqlString( criteria, cq );
+		if ( conditionFragment.trim().length() > 0 ) {
+			if ( buf.length() > 1 ) {
+				buf.append( " and " );
+			}
+			buf.append( conditionFragment );
 		}
-		String critCondition = crit.toSqlString(criteria, cq);
-		if ( buf.length()>1 && critCondition.trim().length()>0 ) buf.append(" and ");
-		buf.append(critCondition);
 	}
 
 	protected void appendComponentCondition(
-		String path,
-		Object component,
-		CompositeType type,
-		Criteria criteria,
-		CriteriaQuery criteriaQuery,
-		StringBuilder buf)
-	throws HibernateException {
-
-		if (component!=null) {
-			String[] propertyNames = type.getPropertyNames();
-			Object[] values = type.getPropertyValues( component, getEntityMode(criteria, criteriaQuery) );
-			Type[] subtypes = type.getSubtypes();
-			for (int i=0; i<propertyNames.length; i++) {
-				String subpath = StringHelper.qualify( path, propertyNames[i] );
-				Object value = values[i];
-				if ( isPropertyIncluded( value, subpath, subtypes[i] ) ) {
-					Type subtype = subtypes[i];
+			String path,
+			Object component,
+			CompositeType type,
+			Criteria criteria,
+			CriteriaQuery criteriaQuery,
+			StringBuilder buf) {
+		if ( component != null ) {
+			final String[] propertyNames = type.getPropertyNames();
+			final Object[] values = type.getPropertyValues( component, getEntityMode( criteria, criteriaQuery ) );
+			final Type[] subtypes = type.getSubtypes();
+			for ( int i=0; i<propertyNames.length; i++ ) {
+				final String subPath = StringHelper.qualify( path, propertyNames[i] );
+				final Object value = values[i];
+				if ( isPropertyIncluded( value, subPath, subtypes[i] ) ) {
+					final Type subtype = subtypes[i];
 					if ( subtype.isComponentType() ) {
 						appendComponentCondition(
-							subpath,
-							value,
-							(CompositeType) subtype,
-							criteria,
-							criteriaQuery,
-							buf
+								subPath,
+								value,
+								(CompositeType) subtype,
+								criteria,
+								criteriaQuery,
+								buf
 						);
 					}
 					else {
 						appendPropertyCondition(
-							subpath,
-							value,
-							criteria,
-							criteriaQuery,
-							buf
+								subPath,
+								value,
+								criteria,
+								criteriaQuery,
+								buf
 						);
 					}
 				}
 			}
 		}
 	}
-}
\ No newline at end of file
+
+	@Override
+	public String toString() {
+		return "example (" + exampleEntity + ')';
+	}
+
+
+	// PropertySelector definitions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	/**
+	 * A strategy for choosing property values for inclusion in the query criteria.  Note that
+	 * property selection (for inclusion) operates separately from excluding a property.  Excluded
+	 * properties are not even passed in to the PropertySelector for consideration.
+	 */
+	public static interface PropertySelector extends Serializable {
+		/**
+		 * Determine whether the given property should be used in the criteria.
+		 *
+		 * @param propertyValue The property value (from the example bean)
+		 * @param propertyName The name of the property
+		 * @param type The type of the property
+		 *
+		 * @return {@code true} indicates the property should be included; {@code false} indiates it should not.
+		 */
+		public boolean include(Object propertyValue, String propertyName, Type type);
+	}
+
+	/**
+	 * Property selector that includes all properties
+	 */
+	public static final class AllPropertySelector implements PropertySelector {
+		/**
+		 * Singleton access
+		 */
+		public static final AllPropertySelector INSTANCE = new AllPropertySelector();
+
+		@Override
+		public boolean include(Object object, String propertyName, Type type) {
+			return true;
+		}
+
+		private Object readResolve() {
+			return INSTANCE;
+		}
+	}
+
+	/**
+	 * Property selector that includes only properties that are not {@code null}
+	 */
+	public static final class NotNullPropertySelector implements PropertySelector {
+		/**
+		 * Singleton access
+		 */
+		public static final NotNullPropertySelector INSTANCE = new NotNullPropertySelector();
+
+		@Override
+		public boolean include(Object object, String propertyName, Type type) {
+			return object!=null;
+		}
+
+		private Object readResolve() {
+			return INSTANCE;
+		}
+	}
+
+	/**
+	 * Property selector that includes only properties that are not {@code null} and non-zero (if numeric)
+	 */
+	public static final class NotNullOrZeroPropertySelector implements PropertySelector {
+		/**
+		 * Singleton access
+		 */
+		public static final NotNullOrZeroPropertySelector INSTANCE = new NotNullOrZeroPropertySelector();
+
+		@Override
+		public boolean include(Object object, String propertyName, Type type) {
+			return object != null
+					&& ( !(object instanceof Number) || ( (Number) object ).longValue()!=0
+			);
+		}
+
+		private Object readResolve() {
+			return INSTANCE;
+		}
+	}
+}
