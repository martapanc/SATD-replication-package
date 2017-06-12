diff --git a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
index 33a9bec9e0..b8a25c54ab 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
@@ -1,406 +1,527 @@
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
+import org.hibernate.EntityNameResolver;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
+import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
+import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.HibernateProxyHelper;
+import org.hibernate.proxy.LazyInitializer;
 
 /**
  * Handles "any" mappings
  * 
  * @author Gavin King
  */
 public class AnyType extends AbstractType implements CompositeType, AssociationType {
+	private final TypeFactory.TypeScope scope;
 	private final Type identifierType;
-	private final Type metaType;
+	private final Type discriminatorType;
 
-	public AnyType(Type metaType, Type identifierType) {
+	/**
+	 * Intended for use only from legacy {@link ObjectType} type definition
+	 *
+	 * @param discriminatorType
+	 * @param identifierType
+	 */
+	protected AnyType(Type discriminatorType, Type identifierType) {
+		this( null, discriminatorType, identifierType );
+	}
+
+	public AnyType(TypeFactory.TypeScope scope, Type discriminatorType, Type identifierType) {
+		this.scope = scope;
+		this.discriminatorType = discriminatorType;
 		this.identifierType = identifierType;
-		this.metaType = metaType;
 	}
 
-	public Object deepCopy(Object value, SessionFactoryImplementor factory)
-	throws HibernateException {
-		return value;
+	public Type getIdentifierType() {
+		return identifierType;
 	}
-	
-	public boolean isMethodOf(Method method) {
-		return false;
+
+	public Type getDiscriminatorType() {
+		return discriminatorType;
 	}
 
-	public boolean isSame(Object x, Object y) throws HibernateException {
-		return x==y;
+
+	// general Type metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public String getName() {
+		return "object";
 	}
 
-	public int compare(Object x, Object y) {
-		return 0; //TODO: entities CAN be compared, by PK and entity name, fix this!
+	@Override
+	public Class getReturnedClass() {
+		return Object.class;
 	}
 
-	public int getColumnSpan(Mapping session)
-	throws MappingException {
-		return 2;
+	@Override
+	public int[] sqlTypes(Mapping mapping) throws MappingException {
+		return ArrayHelper.join( discriminatorType.sqlTypes( mapping ), identifierType.sqlTypes( mapping ) );
 	}
 
-	public String getName() {
-		return "object";
+	@Override
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+		return ArrayHelper.join( discriminatorType.dictatedSizes( mapping ), identifierType.dictatedSizes( mapping ) );
 	}
 
-	public boolean isMutable() {
-		return false;
+	@Override
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+		return ArrayHelper.join( discriminatorType.defaultSizes( mapping ), identifierType.defaultSizes( mapping ) );
 	}
 
-	public Object nullSafeGet(ResultSet rs,	String name, SessionImplementor session, Object owner)
-	throws HibernateException, SQLException {
+	@Override
+	public Object[] getPropertyValues(Object component, EntityMode entityMode) {
+		throw new UnsupportedOperationException();
+	}
 
-		throw new UnsupportedOperationException("object is a multicolumn type");
+	@Override
+	public boolean isAnyType() {
+		return true;
 	}
 
-	public Object nullSafeGet(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
-	throws HibernateException, SQLException {
-		return resolveAny(
-				(String) metaType.nullSafeGet(rs, names[0], session, owner),
-				(Serializable) identifierType.nullSafeGet(rs, names[1], session, owner),
-				session
-			);
+	@Override
+	public boolean isAssociationType() {
+		return true;
 	}
 
-	public Object hydrate(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
-	throws HibernateException, SQLException {
-		String entityName = (String) metaType.nullSafeGet(rs, names[0], session, owner);
-		Serializable id = (Serializable) identifierType.nullSafeGet(rs, names[1], session, owner);
-		return new ObjectTypeCacheEntry(entityName, id);
+	@Override
+	public boolean isComponentType() {
+		return true;
 	}
 
-	public Object resolve(Object value, SessionImplementor session, Object owner)
-	throws HibernateException {
-		ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) value;
-		return resolveAny(holder.entityName, holder.id, session);
+	@Override
+	public boolean isEmbedded() {
+		return false;
 	}
 
-	public Object semiResolve(Object value, SessionImplementor session, Object owner)
-	throws HibernateException {
-		throw new UnsupportedOperationException("any mappings may not form part of a property-ref");
+	@Override
+	public boolean isMutable() {
+		return false;
 	}
-	
-	private Object resolveAny(String entityName, Serializable id, SessionImplementor session)
-	throws HibernateException {
-		return entityName==null || id==null ?
-				null : session.internalLoad( entityName, id, false, false );
+
+	@Override
+	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
+		return value;
 	}
 
-	public void nullSafeSet(PreparedStatement st, Object value,	int index, SessionImplementor session)
-	throws HibernateException, SQLException {
-		nullSafeSet(st, value, index, null, session);
+
+	// general Type functionality ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public int compare(Object x, Object y) {
+		if ( x == null ) {
+			// if y is also null, return that they are the same (no option for "UNKNOWN")
+			// if y is not null, return that y is "greater" (-1 because the result is from the perspective of
+			// 		the first arg: x)
+			return y == null ? 0 : -1;
+		}
+		else if ( y == null ) {
+			// x is not null, but y is.  return that x is "greater"
+			return 1;
+		}
+
+		// At this point we know both are non-null.
+		final Object xId = extractIdentifier( x );
+		final Object yId = extractIdentifier( y );
+
+		return getIdentifierType().compare( xId, yId );
 	}
-	
-	public void nullSafeSet(PreparedStatement st, Object value,	int index, boolean[] settable, SessionImplementor session)
-	throws HibernateException, SQLException {
 
-		Serializable id;
-		String entityName;
-		if (value==null) {
-			id=null;
-			entityName=null;
+	private Object extractIdentifier(Object entity) {
+		final EntityPersister concretePersister = guessEntityPersister( entity );
+		return concretePersister == null
+				? null
+				: concretePersister.getEntityTuplizer().getIdentifier( entity, null );
+	}
+
+	private EntityPersister guessEntityPersister(Object object) {
+		if ( scope == null ) {
+			return null;
 		}
-		else {
-			entityName = session.bestGuessEntityName(value);
-			id = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, value, session);
+
+		String entityName = null;
+
+		// this code is largely copied from Session's bestGuessEntityName
+		Object entity = object;
+		if ( entity instanceof HibernateProxy ) {
+			final LazyInitializer initializer = ( (HibernateProxy) entity ).getHibernateLazyInitializer();
+			if ( initializer.isUninitialized() ) {
+				entityName = initializer.getEntityName();
+			}
+			entity = initializer.getImplementation();
 		}
-		
-		// metaType is assumed to be single-column type
-		if ( settable==null || settable[0] ) {
-			metaType.nullSafeSet(st, entityName, index, session);
+
+		if ( entityName == null ) {
+			for ( EntityNameResolver resolver : scope.resolveFactory().iterateEntityNameResolvers() ) {
+				entityName = resolver.resolveEntityName( entity );
+				if ( entityName != null ) {
+					break;
+				}
+			}
 		}
-		if (settable==null) {
-			identifierType.nullSafeSet(st, id, index+1, session);
+
+		if ( entityName == null ) {
+			// the old-time stand-by...
+			entityName = object.getClass().getName();
 		}
-		else {
-			boolean[] idsettable = new boolean[ settable.length-1 ];
-			System.arraycopy(settable, 1, idsettable, 0, idsettable.length);
-			identifierType.nullSafeSet(st, id, index+1, idsettable, session);
+
+		return scope.resolveFactory().getEntityPersister( entityName );
+	}
+
+	@Override
+	public boolean isSame(Object x, Object y) throws HibernateException {
+		return x == y;
+	}
+
+	@Override
+	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
+			throws HibernateException {
+		if ( current == null ) {
+			return old != null;
 		}
+		else if ( old == null ) {
+			return true;
+		}
+
+		final ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) old;
+		final boolean[] idCheckable = new boolean[checkable.length-1];
+		System.arraycopy( checkable, 1, idCheckable, 0, idCheckable.length );
+		return ( checkable[0] && !holder.entityName.equals( session.bestGuessEntityName( current ) ) )
+				|| identifierType.isModified( holder.id, getIdentifier( current, session ), idCheckable, session );
 	}
 
-	public Class getReturnedClass() {
-		return Object.class;
+	@Override
+	public boolean[] toColumnNullness(Object value, Mapping mapping) {
+		final boolean[] result = new boolean[ getColumnSpan( mapping ) ];
+		if ( value != null ) {
+			Arrays.fill( result, true );
+		}
+		return result;
 	}
 
-	public int[] sqlTypes(Mapping mapping) throws MappingException {
-		return ArrayHelper.join(
-				metaType.sqlTypes( mapping ),
-				identifierType.sqlTypes( mapping )
-		);
+	@Override
+	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
+			throws HibernateException {
+		return isDirty( old, current, session );
 	}
 
 	@Override
-	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
-		return ArrayHelper.join(
-				metaType.dictatedSizes( mapping ),
-				identifierType.dictatedSizes( mapping )
-		);
+	public int getColumnSpan(Mapping session) {
+		return 2;
 	}
 
 	@Override
-	public Size[] defaultSizes(Mapping mapping) throws MappingException {
-		return ArrayHelper.join(
-				metaType.defaultSizes( mapping ),
-				identifierType.defaultSizes( mapping )
+	public Object nullSafeGet(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
+			throws HibernateException, SQLException {
+		return resolveAny(
+				(String) discriminatorType.nullSafeGet( rs, names[0], session, owner ),
+				(Serializable) identifierType.nullSafeGet( rs, names[1], session, owner ),
+				session
 		);
 	}
 
-	public void setToXMLNode(Node xml, Object value, SessionFactoryImplementor factory) {
-		throw new UnsupportedOperationException("any types cannot be stringified");
+	@Override
+	public Object hydrate(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
+			throws HibernateException, SQLException {
+		final String entityName = (String) discriminatorType.nullSafeGet( rs, names[0], session, owner );
+		final Serializable id = (Serializable) identifierType.nullSafeGet( rs, names[1], session, owner );
+		return new ObjectTypeCacheEntry( entityName, id );
 	}
 
-	public String toLoggableString(Object value, SessionFactoryImplementor factory) 
-	throws HibernateException {
-		//TODO: terrible implementation!
-		return value == null
-				? "null"
-				: factory.getTypeHelper()
-						.entity( HibernateProxyHelper.getClassWithoutInitializingProxy( value ) )
-						.toLoggableString( value, factory );
+	@Override
+	public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
+		final ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) value;
+		return resolveAny( holder.entityName, holder.id, session );
 	}
 
-	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
-		throw new UnsupportedOperationException(); //TODO: is this right??
+	private Object resolveAny(String entityName, Serializable id, SessionImplementor session)
+			throws HibernateException {
+		return entityName==null || id==null
+				? null
+				: session.internalLoad( entityName, id, false, false );
 	}
 
-	public static final class ObjectTypeCacheEntry implements Serializable {
-		String entityName;
-		Serializable id;
-		ObjectTypeCacheEntry(String entityName, Serializable id) {
-			this.entityName = entityName;
-			this.id = id;
-		}
+	@Override
+	public void nullSafeSet(PreparedStatement st, Object value,	int index, SessionImplementor session)
+			throws HibernateException, SQLException {
+		nullSafeSet( st, value, index, null, session );
 	}
 
-	public Object assemble(
-		Serializable cached,
-		SessionImplementor session,
-		Object owner)
-	throws HibernateException {
+	@Override
+	public void nullSafeSet(PreparedStatement st, Object value,	int index, boolean[] settable, SessionImplementor session)
+			throws HibernateException, SQLException {
+		Serializable id;
+		String entityName;
+		if ( value == null ) {
+			id = null;
+			entityName = null;
+		}
+		else {
+			entityName = session.bestGuessEntityName( value );
+			id = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, value, session );
+		}
 
-		ObjectTypeCacheEntry e = (ObjectTypeCacheEntry) cached;
-		return e==null ? null : session.internalLoad(e.entityName, e.id, false, false);
+		// discriminatorType is assumed to be single-column type
+		if ( settable == null || settable[0] ) {
+			discriminatorType.nullSafeSet( st, entityName, index, session );
+		}
+		if ( settable == null ) {
+			identifierType.nullSafeSet( st, id, index+1, session );
+		}
+		else {
+			final boolean[] idSettable = new boolean[ settable.length-1 ];
+			System.arraycopy( settable, 1, idSettable, 0, idSettable.length );
+			identifierType.nullSafeSet( st, id, index+1, idSettable, session );
+		}
 	}
 
-	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
-	throws HibernateException {
-		return value==null ?
-			null :
-			new ObjectTypeCacheEntry(
-						session.bestGuessEntityName(value),
-						ForeignKeys.getEntityIdentifierIfNotUnsaved( 
-								session.bestGuessEntityName(value), value, session 
-							)
-					);
+	@Override
+	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
+		//TODO: terrible implementation!
+		return value == null
+				? "null"
+				: factory.getTypeHelper()
+				.entity( HibernateProxyHelper.getClassWithoutInitializingProxy( value ) )
+				.toLoggableString( value, factory );
 	}
 
-	public boolean isAnyType() {
-		return true;
+	@Override
+	public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
+		final ObjectTypeCacheEntry e = (ObjectTypeCacheEntry) cached;
+		return e == null ? null : session.internalLoad( e.entityName, e.id, false, false );
 	}
 
-	public Object replace(
-			Object original, 
-			Object target,
-			SessionImplementor session, 
-			Object owner, 
-			Map copyCache)
-	throws HibernateException {
-		if (original==null) {
+	@Override
+	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
+		if ( value == null ) {
 			return null;
 		}
 		else {
-			String entityName = session.bestGuessEntityName(original);
-			Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved(
-					entityName,
-					original,
-					session
+			return new ObjectTypeCacheEntry(
+					session.bestGuessEntityName( value ),
+					ForeignKeys.getEntityIdentifierIfNotUnsaved(
+							session.bestGuessEntityName( value ),
+							value,
+							session
+					)
 			);
-			return session.internalLoad( 
-					entityName, 
-					id, 
-					false, 
-					false
-				);
 		}
 	}
-	public CascadeStyle getCascadeStyle(int i) {
-		return CascadeStyles.NONE;
+
+	@Override
+	public Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache)
+			throws HibernateException {
+		if ( original == null ) {
+			return null;
+		}
+		else {
+			final String entityName = session.bestGuessEntityName( original );
+			final Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, original, session );
+			return session.internalLoad( entityName, id, false, false );
+		}
 	}
 
-	public FetchMode getFetchMode(int i) {
-		return FetchMode.SELECT;
+	@Override
+	public Object nullSafeGet(ResultSet rs,	String name, SessionImplementor session, Object owner) {
+		throw new UnsupportedOperationException( "object is a multicolumn type" );
+	}
+
+	@Override
+	public Object semiResolve(Object value, SessionImplementor session, Object owner) {
+		throw new UnsupportedOperationException( "any mappings may not form part of a property-ref" );
+	}
+
+	@Override
+	public void setToXMLNode(Node xml, Object value, SessionFactoryImplementor factory) {
+		throw new UnsupportedOperationException("any types cannot be stringified");
+	}
+
+	@Override
+	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
+		throw new UnsupportedOperationException();
+	}
+
+
+
+	// CompositeType implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public boolean isMethodOf(Method method) {
+		return false;
 	}
 
 	private static final String[] PROPERTY_NAMES = new String[] { "class", "id" };
 
+	@Override
 	public String[] getPropertyNames() {
 		return PROPERTY_NAMES;
 	}
 
-	public Object getPropertyValue(Object component, int i, SessionImplementor session)
-		throws HibernateException {
-
-		return i==0 ?
-				session.bestGuessEntityName(component) :
-				getIdentifier(component, session);
+	@Override
+	public Object getPropertyValue(Object component, int i, SessionImplementor session) throws HibernateException {
+		return i==0
+				? session.bestGuessEntityName( component )
+				: getIdentifier( component, session );
 	}
 
-	public Object[] getPropertyValues(Object component, SessionImplementor session)
-		throws HibernateException {
-
-		return new Object[] { session.bestGuessEntityName(component), getIdentifier(component, session) };
+	@Override
+	public Object[] getPropertyValues(Object component, SessionImplementor session) throws HibernateException {
+		return new Object[] {
+				session.bestGuessEntityName( component ),
+				getIdentifier( component, session )
+		};
 	}
 
 	private Serializable getIdentifier(Object value, SessionImplementor session) throws HibernateException {
 		try {
-			return ForeignKeys.getEntityIdentifierIfNotUnsaved( session.bestGuessEntityName(value), value, session );
+			return ForeignKeys.getEntityIdentifierIfNotUnsaved(
+					session.bestGuessEntityName( value ),
+					value,
+					session
+			);
 		}
 		catch (TransientObjectException toe) {
 			return null;
 		}
 	}
 
-	public Type[] getSubtypes() {
-		return new Type[] { metaType, identifierType };
+	@Override
+	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) {
+		throw new UnsupportedOperationException();
 	}
 
-	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
-		throws HibernateException {
+	private static final boolean[] NULLABILITY = new boolean[] { false, false };
 
-		throw new UnsupportedOperationException();
+	@Override
+	public boolean[] getPropertyNullability() {
+		return NULLABILITY;
+	}
 
+	@Override
+	public Type[] getSubtypes() {
+		return new Type[] {discriminatorType, identifierType };
 	}
 
-	public Object[] getPropertyValues(Object component, EntityMode entityMode) {
-		throw new UnsupportedOperationException();
+	@Override
+	public CascadeStyle getCascadeStyle(int i) {
+		return CascadeStyles.NONE;
 	}
 
-	public boolean isComponentType() {
-		return true;
+	@Override
+	public FetchMode getFetchMode(int i) {
+		return FetchMode.SELECT;
 	}
 
+
+	// AssociationType implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
 	public ForeignKeyDirection getForeignKeyDirection() {
-		//return AssociationType.FOREIGN_KEY_TO_PARENT; //this is better but causes a transient object exception...
 		return ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT;
 	}
 
-	public boolean isAssociationType() {
-		return true;
-	}
-
+	@Override
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 
-	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) {
-		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
-	}
-
-	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
-	throws HibernateException {
-		if (current==null) return old!=null;
-		if (old==null) return current!=null;
-		ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) old;
-		boolean[] idcheckable = new boolean[checkable.length-1];
-		System.arraycopy(checkable, 1, idcheckable, 0, idcheckable.length);
-		return ( checkable[0] && !holder.entityName.equals( session.bestGuessEntityName(current) ) ) ||
-				identifierType.isModified(holder.id, getIdentifier(current, session), idcheckable, session);
-	}
-
-	public String getAssociatedEntityName(SessionFactoryImplementor factory)
-		throws MappingException {
-		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
-	}
-	
-	public boolean[] getPropertyNullability() {
+	@Override
+	public String getLHSPropertyName() {
 		return null;
 	}
 
-	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
-	throws MappingException {
-		throw new UnsupportedOperationException();
-	}
-	
 	public boolean isReferenceToPrimaryKey() {
 		return true;
 	}
-	
-	public String getRHSUniqueKeyPropertyName() {
-		return null;
-	}
 
-	public String getLHSPropertyName() {
+	@Override
+	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
+	@Override
 	public boolean isAlwaysDirtyChecked() {
 		return false;
 	}
 
+	@Override
 	public boolean isEmbeddedInXML() {
 		return false;
 	}
-	
-	public boolean[] toColumnNullness(Object value, Mapping mapping) {
-		boolean[] result = new boolean[ getColumnSpan(mapping) ];
-		if (value!=null) Arrays.fill(result, true);
-		return result;
+
+	@Override
+	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) {
+		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
-	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) 
-	throws HibernateException {
-		//TODO!!!
-		return isDirty(old, current, session);
+	@Override
+	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
+		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
-	public boolean isEmbedded() {
-		return false;
+	@Override
+	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
+		throw new UnsupportedOperationException();
+	}
+
+
+	/**
+	 * Used to externalize discrimination per a given identifier.  For example, when writing to
+	 * second level cache we write the discrimination resolved concrete type for each entity written.
+	 */
+	public static final class ObjectTypeCacheEntry implements Serializable {
+		final String entityName;
+		final Serializable id;
+
+		ObjectTypeCacheEntry(String entityName, Serializable id) {
+			this.entityName = entityName;
+			this.id = id;
+		}
 	}
 }
