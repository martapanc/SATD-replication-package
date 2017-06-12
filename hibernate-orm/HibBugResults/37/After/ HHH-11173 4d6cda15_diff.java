diff --git a/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
index e53f5a47f1..e9b65bdd34 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
@@ -1,402 +1,407 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
+import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.MutabilityPlan;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * Convenience base class for {@link BasicType} implementations
  *
  * @author Steve Ebersole
  * @author Brett Meyer
  */
 public abstract class AbstractStandardBasicType<T>
 		implements BasicType, StringRepresentableType<T>, ProcedureParameterExtractionAware<T>, ProcedureParameterNamedBinder {
 
 	private static final Size DEFAULT_SIZE = new Size( 19, 2, 255, Size.LobMultiplier.NONE ); // to match legacy behavior
 	private final Size dictatedSize = new Size();
 
 	// Don't use final here.  Need to initialize afterQuery-the-fact
 	// by DynamicParameterizedTypes.
 	private SqlTypeDescriptor sqlTypeDescriptor;
 	private JavaTypeDescriptor<T> javaTypeDescriptor;
 	// sqlTypes need always to be in sync with sqlTypeDescriptor
 	private int[] sqlTypes;
 
 	public AbstractStandardBasicType(SqlTypeDescriptor sqlTypeDescriptor, JavaTypeDescriptor<T> javaTypeDescriptor) {
 		this.sqlTypeDescriptor = sqlTypeDescriptor;
 		this.sqlTypes = new int[] { sqlTypeDescriptor.getSqlType() };
 		this.javaTypeDescriptor = javaTypeDescriptor;
 	}
 
 	public T fromString(String string) {
 		return javaTypeDescriptor.fromString( string );
 	}
 
 	@Override
 	public String toString(T value) {
 		return javaTypeDescriptor.toString( value );
 	}
 
 	@Override
 	public T fromStringValue(String xml) throws HibernateException {
 		return fromString( xml );
 	}
 
 	protected MutabilityPlan<T> getMutabilityPlan() {
 		return javaTypeDescriptor.getMutabilityPlan();
 	}
 
 	protected T getReplacement(T original, T target, SharedSessionContractImplementor session) {
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
 
 	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return value == null ? ArrayHelper.FALSE : ArrayHelper.TRUE;
 	}
 
 	@Override
 	public String[] getRegistrationKeys() {
 		return registerUnderJavaType()
 				? new String[] { getName(), javaTypeDescriptor.getJavaTypeClass().getName() }
 				: new String[] { getName() };
 	}
 
 	protected boolean registerUnderJavaType() {
 		return false;
 	}
 
 	protected static Size getDefaultSize() {
 		return DEFAULT_SIZE;
 	}
 
 	protected Size getDictatedSize() {
 		return dictatedSize;
 	}
 	
 	// final implementations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public final JavaTypeDescriptor<T> getJavaTypeDescriptor() {
 		return javaTypeDescriptor;
 	}
 	
 	public final void setJavaTypeDescriptor( JavaTypeDescriptor<T> javaTypeDescriptor ) {
 		this.javaTypeDescriptor = javaTypeDescriptor;
 	}
 
 	public final SqlTypeDescriptor getSqlTypeDescriptor() {
 		return sqlTypeDescriptor;
 	}
 
 	public final void setSqlTypeDescriptor( SqlTypeDescriptor sqlTypeDescriptor ) {
 		this.sqlTypeDescriptor = sqlTypeDescriptor;
 		this.sqlTypes = new int[] { sqlTypeDescriptor.getSqlType() };
 	}
 
 	@Override
 	public final Class getReturnedClass() {
 		return javaTypeDescriptor.getJavaTypeClass();
 	}
 
 	@Override
 	public final int getColumnSpan(Mapping mapping) throws MappingException {
 		return 1;
 	}
 
 	@Override
 	public final int[] sqlTypes(Mapping mapping) throws MappingException {
 		return sqlTypes;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return new Size[] { getDictatedSize() };
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return new Size[] { getDefaultSize() };
 	}
 
 	@Override
 	public final boolean isAssociationType() {
 		return false;
 	}
 
 	@Override
 	public final boolean isCollectionType() {
 		return false;
 	}
 
 	@Override
 	public final boolean isComponentType() {
 		return false;
 	}
 
 	@Override
 	public final boolean isEntityType() {
 		return false;
 	}
 
 	@Override
 	public final boolean isAnyType() {
 		return false;
 	}
 
 	public final boolean isXMLElement() {
 		return false;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isSame(Object x, Object y) {
 		return isEqual( x, y );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
 		return isEqual( x, y );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isEqual(Object one, Object another) {
 		return javaTypeDescriptor.areEqual( (T) one, (T) another );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public final int getHashCode(Object x) {
 		return javaTypeDescriptor.extractHashCode( (T) x );
 	}
 
 	@Override
 	public final int getHashCode(Object x, SessionFactoryImplementor factory) {
 		return getHashCode( x );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public final int compare(Object x, Object y) {
 		return javaTypeDescriptor.getComparator().compare( (T) x, (T) y );
 	}
 
 	@Override
 	public final boolean isDirty(Object old, Object current, SharedSessionContractImplementor session) {
 		return isDirty( old, current );
 	}
 
 	@Override
 	public final boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) {
 		return checkable[0] && isDirty( old, current );
 	}
 
 	protected final boolean isDirty(Object old, Object current) {
 		return !isSame( old, current );
 	}
 
 	@Override
 	public final boolean isModified(
 			Object oldHydratedState,
 			Object currentState,
 			boolean[] checkable,
 			SharedSessionContractImplementor session) {
 		return isDirty( oldHydratedState, currentState );
 	}
 
 	@Override
 	public final Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SharedSessionContractImplementor session,
 			Object owner) throws SQLException {
 		return nullSafeGet( rs, names[0], session );
 	}
 
 	@Override
 	public final Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner)
 			throws SQLException {
 		return nullSafeGet( rs, name, session );
 	}
 
 	public final T nullSafeGet(ResultSet rs, String name, final SharedSessionContractImplementor session) throws SQLException {
 		return nullSafeGet( rs, name, (WrapperOptions) session );
 	}
 
 	protected final T nullSafeGet(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 		return remapSqlTypeDescriptor( options ).getExtractor( javaTypeDescriptor ).extract( rs, name, options );
 	}
 
 	public Object get(ResultSet rs, String name, SharedSessionContractImplementor session) throws HibernateException, SQLException {
 		return nullSafeGet( rs, name, session );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public final void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			final SharedSessionContractImplementor session) throws SQLException {
 		nullSafeSet( st, value, index, (WrapperOptions) session );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected final void nullSafeSet(PreparedStatement st, Object value, int index, WrapperOptions options) throws SQLException {
 		remapSqlTypeDescriptor( options ).getBinder( javaTypeDescriptor ).bind( st, ( T ) value, index, options );
 	}
 
 	protected SqlTypeDescriptor remapSqlTypeDescriptor(WrapperOptions options) {
 		return options.remapSqlTypeDescriptor( sqlTypeDescriptor );
 	}
 
 	public void set(PreparedStatement st, T value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
 		nullSafeSet( st, value, index, session );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public final String toLoggableString(Object value, SessionFactoryImplementor factory) {
+		if ( value == LazyPropertyInitializer.UNFETCHED_PROPERTY || !Hibernate.isInitialized( value ) ) {
+			return  "<uninitialized>";
+		}
 		return javaTypeDescriptor.extractLoggableRepresentation( (T) value );
 	}
 
 	@Override
 	public final boolean isMutable() {
 		return getMutabilityPlan().isMutable();
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public final Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return deepCopy( (T) value );
 	}
 
 	protected final T deepCopy(T value) {
 		return getMutabilityPlan().deepCopy( value );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public final Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
 		return getMutabilityPlan().disassemble( (T) value );
 	}
 
 	@Override
 	public final Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
 		return getMutabilityPlan().assemble( cached );
 	}
 
 	@Override
 	public final void beforeAssemble(Serializable cached, SharedSessionContractImplementor session) {
 	}
 
 	@Override
 	public final Object hydrate(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return nullSafeGet(rs, names, session, owner);
 	}
 
 	@Override
 	public final Object resolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
 		return value;
 	}
 
 	@Override
 	public final Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
 		return value;
 	}
 
 	@Override
 	public final Type getSemiResolvedType(SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public final Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache) {
 		return getReplacement( (T) original, (T) target, session );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public Object replace(
 			Object original,
 			Object target,
 			SharedSessionContractImplementor session,
 			Object owner,
 			Map copyCache,
 			ForeignKeyDirection foreignKeyDirection) {
 		return ForeignKeyDirection.FROM_PARENT == foreignKeyDirection
 				? getReplacement( (T) original, (T) target, session )
 				: target;
 	}
 
 	@Override
 	public boolean canDoExtraction() {
 		return true;
 	}
 
 	@Override
 	public T extract(CallableStatement statement, int startIndex, final SharedSessionContractImplementor session) throws SQLException {
 		return remapSqlTypeDescriptor( session ).getExtractor( javaTypeDescriptor ).extract(
 				statement,
 				startIndex,
 				session
 		);
 	}
 
 	@Override
 	public T extract(CallableStatement statement, String[] paramNames, final SharedSessionContractImplementor session) throws SQLException {
 		return remapSqlTypeDescriptor( session ).getExtractor( javaTypeDescriptor ).extract(
 				statement,
 				paramNames,
 				session
 		);
 	}
 
 	@Override
 	public void nullSafeSet(CallableStatement st, Object value, String name, SharedSessionContractImplementor session) throws SQLException {
 		nullSafeSet( st, value, name, (WrapperOptions) session );
 	}
 
 	@SuppressWarnings("unchecked")
 	protected final void nullSafeSet(CallableStatement st, Object value, String name, WrapperOptions options) throws SQLException {
 		remapSqlTypeDescriptor( options ).getBinder( javaTypeDescriptor ).bind( st, (T) value, name, options );
 	}
 
 	@Override
 	public boolean canDoSetting() {
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
index 29289f10bd..cbdd59c411 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
@@ -1,516 +1,521 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.FetchMode;
+import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.TransientObjectException;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.HibernateProxyHelper;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * Handles "any" mappings
  * 
  * @author Gavin King
  */
 public class AnyType extends AbstractType implements CompositeType, AssociationType {
 	private final TypeFactory.TypeScope scope;
 	private final Type identifierType;
 	private final Type discriminatorType;
 
 	/**
 	 * Intended for use only from legacy {@link ObjectType} type definition
 	 */
 	protected AnyType(Type discriminatorType, Type identifierType) {
 		this( null, discriminatorType, identifierType );
 	}
 
 	public AnyType(TypeFactory.TypeScope scope, Type discriminatorType, Type identifierType) {
 		this.scope = scope;
 		this.discriminatorType = discriminatorType;
 		this.identifierType = identifierType;
 	}
 
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 	public Type getDiscriminatorType() {
 		return discriminatorType;
 	}
 
 
 	// general Type metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public String getName() {
 		return "object";
 	}
 
 	@Override
 	public Class getReturnedClass() {
 		return Object.class;
 	}
 
 	@Override
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.sqlTypes( mapping ), identifierType.sqlTypes( mapping ) );
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.dictatedSizes( mapping ), identifierType.dictatedSizes( mapping ) );
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.defaultSizes( mapping ), identifierType.defaultSizes( mapping ) );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, EntityMode entityMode) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean isAnyType() {
 		return true;
 	}
 
 	@Override
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	@Override
 	public boolean isComponentType() {
 		return true;
 	}
 
 	@Override
 	public boolean isEmbedded() {
 		return false;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	@Override
 	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return value;
 	}
 
 
 	// general Type functionality ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public int compare(Object x, Object y) {
 		if ( x == null ) {
 			// if y is also null, return that they are the same (no option for "UNKNOWN")
 			// if y is not null, return that y is "greater" (-1 because the result is from the perspective of
 			// 		the first arg: x)
 			return y == null ? 0 : -1;
 		}
 		else if ( y == null ) {
 			// x is not null, but y is.  return that x is "greater"
 			return 1;
 		}
 
 		// At this point we know both are non-null.
 		final Object xId = extractIdentifier( x );
 		final Object yId = extractIdentifier( y );
 
 		return getIdentifierType().compare( xId, yId );
 	}
 
 	private Object extractIdentifier(Object entity) {
 		final EntityPersister concretePersister = guessEntityPersister( entity );
 		return concretePersister == null
 				? null
 				: concretePersister.getEntityTuplizer().getIdentifier( entity, null );
 	}
 
 	private EntityPersister guessEntityPersister(Object object) {
 		if ( scope == null ) {
 			return null;
 		}
 
 		String entityName = null;
 
 		// this code is largely copied from Session's bestGuessEntityName
 		Object entity = object;
 		if ( entity instanceof HibernateProxy ) {
 			final LazyInitializer initializer = ( (HibernateProxy) entity ).getHibernateLazyInitializer();
 			if ( initializer.isUninitialized() ) {
 				entityName = initializer.getEntityName();
 			}
 			entity = initializer.getImplementation();
 		}
 
 		if ( entityName == null ) {
 			for ( EntityNameResolver resolver : scope.resolveFactory().getMetamodel().getEntityNameResolvers() ) {
 				entityName = resolver.resolveEntityName( entity );
 				if ( entityName != null ) {
 					break;
 				}
 			}
 		}
 
 		if ( entityName == null ) {
 			// the old-time stand-by...
 			entityName = object.getClass().getName();
 		}
 
 		return scope.resolveFactory().getMetamodel().entityPersister( entityName );
 	}
 
 	@Override
 	public boolean isSame(Object x, Object y) throws HibernateException {
 		return x == y;
 	}
 
 	@Override
 	public boolean isModified(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session)
 			throws HibernateException {
 		if ( current == null ) {
 			return old != null;
 		}
 		else if ( old == null ) {
 			return true;
 		}
 
 		final ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) old;
 		final boolean[] idCheckable = new boolean[checkable.length-1];
 		System.arraycopy( checkable, 1, idCheckable, 0, idCheckable.length );
 		return ( checkable[0] && !holder.entityName.equals( session.bestGuessEntityName( current ) ) )
 				|| identifierType.isModified( holder.id, getIdentifier( current, session ), idCheckable, session );
 	}
 
 	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		final boolean[] result = new boolean[ getColumnSpan( mapping ) ];
 		if ( value != null ) {
 			Arrays.fill( result, true );
 		}
 		return result;
 	}
 
 	@Override
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session)
 			throws HibernateException {
 		return isDirty( old, current, session );
 	}
 
 	@Override
 	public int getColumnSpan(Mapping session) {
 		return 2;
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs,	String[] names,	SharedSessionContractImplementor session,	Object owner)
 			throws HibernateException, SQLException {
 		return resolveAny(
 				(String) discriminatorType.nullSafeGet( rs, names[0], session, owner ),
 				(Serializable) identifierType.nullSafeGet( rs, names[1], session, owner ),
 				session
 		);
 	}
 
 	@Override
 	public Object hydrate(ResultSet rs,	String[] names,	SharedSessionContractImplementor session,	Object owner)
 			throws HibernateException, SQLException {
 		final String entityName = (String) discriminatorType.nullSafeGet( rs, names[0], session, owner );
 		final Serializable id = (Serializable) identifierType.nullSafeGet( rs, names[1], session, owner );
 		return new ObjectTypeCacheEntry( entityName, id );
 	}
 
 	@Override
 	public Object resolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
 		final ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) value;
 		return resolveAny( holder.entityName, holder.id, session );
 	}
 
 	private Object resolveAny(String entityName, Serializable id, SharedSessionContractImplementor session)
 			throws HibernateException {
 		return entityName==null || id==null
 				? null
 				: session.internalLoad( entityName, id, false, false );
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, SharedSessionContractImplementor session)
 			throws HibernateException, SQLException {
 		nullSafeSet( st, value, index, null, session );
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, boolean[] settable, SharedSessionContractImplementor session)
 			throws HibernateException, SQLException {
 		Serializable id;
 		String entityName;
 		if ( value == null ) {
 			id = null;
 			entityName = null;
 		}
 		else {
 			entityName = session.bestGuessEntityName( value );
 			id = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, value, session );
 		}
 
 		// discriminatorType is assumed to be single-column type
 		if ( settable == null || settable[0] ) {
 			discriminatorType.nullSafeSet( st, entityName, index, session );
 		}
 		if ( settable == null ) {
 			identifierType.nullSafeSet( st, id, index+1, session );
 		}
 		else {
 			final boolean[] idSettable = new boolean[ settable.length-1 ];
 			System.arraycopy( settable, 1, idSettable, 0, idSettable.length );
 			identifierType.nullSafeSet( st, id, index+1, idSettable, session );
 		}
 	}
 
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		//TODO: terrible implementation!
-		return value == null
-				? "null"
-				: factory.getTypeHelper()
-				.entity( HibernateProxyHelper.getClassWithoutInitializingProxy( value ) )
-				.toLoggableString( value, factory );
+		if ( value == null ) {
+			return "null";
+		}
+		if ( value == LazyPropertyInitializer.UNFETCHED_PROPERTY || !Hibernate.isInitialized( value ) ) {
+			return  "<uninitialized>";
+		}
+		Class valueClass = HibernateProxyHelper.getClassWithoutInitializingProxy( value );
+		return factory.getTypeHelper().entity( valueClass ).toLoggableString( value, factory );
 	}
 
 	@Override
 	public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
 		final ObjectTypeCacheEntry e = (ObjectTypeCacheEntry) cached;
 		return e == null ? null : session.internalLoad( e.entityName, e.id, false, false );
 	}
 
 	@Override
 	public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			return new ObjectTypeCacheEntry(
 					session.bestGuessEntityName( value ),
 					ForeignKeys.getEntityIdentifierIfNotUnsaved(
 							session.bestGuessEntityName( value ),
 							value,
 							session
 					)
 			);
 		}
 	}
 
 	@Override
 	public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache)
 			throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		else {
 			final String entityName = session.bestGuessEntityName( original );
 			final Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, original, session );
 			return session.internalLoad( entityName, id, false, false );
 		}
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs,	String name, SharedSessionContractImplementor session, Object owner) {
 		throw new UnsupportedOperationException( "object is a multicolumn type" );
 	}
 
 	@Override
 	public Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner) {
 		throw new UnsupportedOperationException( "any mappings may not form part of a property-ref" );
 	}
 
 	// CompositeType implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	private static final String[] PROPERTY_NAMES = new String[] { "class", "id" };
 
 	@Override
 	public String[] getPropertyNames() {
 		return PROPERTY_NAMES;
 	}
 
 	@Override
 	public int getPropertyIndex(String name) {
 		if ( PROPERTY_NAMES[0].equals( name ) ) {
 			return 0;
 		}
 		else if ( PROPERTY_NAMES[1].equals( name ) ) {
 			return 1;
 		}
 
 		throw new PropertyNotFoundException( "Unable to locate property named " + name + " on AnyType" );
 	}
 
 	@Override
 	public Object getPropertyValue(Object component, int i, SharedSessionContractImplementor session) throws HibernateException {
 		return i==0
 				? session.bestGuessEntityName( component )
 				: getIdentifier( component, session );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, SharedSessionContractImplementor session) throws HibernateException {
 		return new Object[] {
 				session.bestGuessEntityName( component ),
 				getIdentifier( component, session )
 		};
 	}
 
 	private Serializable getIdentifier(Object value, SharedSessionContractImplementor session) throws HibernateException {
 		try {
 			return ForeignKeys.getEntityIdentifierIfNotUnsaved(
 					session.bestGuessEntityName( value ),
 					value,
 					session
 			);
 		}
 		catch (TransientObjectException toe) {
 			return null;
 		}
 	}
 
 	@Override
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) {
 		throw new UnsupportedOperationException();
 	}
 
 	private static final boolean[] NULLABILITY = new boolean[] { false, false };
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return NULLABILITY;
 	}
 
 	@Override
 	public boolean hasNotNullProperty() {
 		// both are non-nullable
 		return true;
 	}
 
 	@Override
 	public Type[] getSubtypes() {
 		return new Type[] {discriminatorType, identifierType };
 	}
 
 	@Override
 	public CascadeStyle getCascadeStyle(int i) {
 		return CascadeStyles.NONE;
 	}
 
 	@Override
 	public FetchMode getFetchMode(int i) {
 		return FetchMode.SELECT;
 	}
 
 
 	// AssociationType implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FROM_PARENT;
 	}
 
 	@Override
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 
 	@Override
 	public String getLHSPropertyName() {
 		return null;
 	}
 
 	public boolean isReferenceToPrimaryKey() {
 		return true;
 	}
 
 	@Override
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	@Override
 	public boolean isAlwaysDirtyChecked() {
 		return false;
 	}
 
 	@Override
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
 	@Override
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
 	@Override
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public String getOnCondition(
 			String alias,
 			SessionFactoryImplementor factory,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * Used to externalize discrimination per a given identifier.  For example, when writing to
 	 * second level cache we write the discrimination resolved concrete type for each entity written.
 	 */
 	public static final class ObjectTypeCacheEntry implements Serializable {
 		final String entityName;
 		final Serializable id;
 
 		ObjectTypeCacheEntry(String entityName, Serializable id) {
 			this.entityName = entityName;
 			this.id = id;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java b/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
index b76a47ed2a..22c44d43db 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
@@ -1,137 +1,145 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Array;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
+import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.collection.internal.PersistentArrayHolder;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * A type for persistent arrays.
  * @author Gavin King
  */
 public class ArrayType extends CollectionType {
 
 	private final Class elementClass;
 	private final Class arrayClass;
 
 	public ArrayType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Class elementClass) {
 		super( typeScope, role, propertyRef );
 		this.elementClass = elementClass;
 		arrayClass = Array.newInstance(elementClass, 0).getClass();
 	}
 
 	@Override
 	public Class getReturnedClass() {
 		return arrayClass;
 	}
 
 	@Override
 	public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key)
 	throws HibernateException {
 		return new PersistentArrayHolder(session, persister);
 	}
 
 	/**
 	 * Not defined for collections of primitive type
 	 */
 	@Override
 	public Iterator getElementsIterator(Object collection) {
 		return Arrays.asList( (Object[]) collection ).iterator();
 	}
 
 	@Override
 	public PersistentCollection wrap(SharedSessionContractImplementor session, Object array) {
 		return new PersistentArrayHolder(session, array);
 	}
 
 	@Override
 	public boolean isArrayType() {
 		return true;
 	}
 
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		int length = Array.getLength(value);
 		List list = new ArrayList(length);
 		Type elemType = getElementType(factory);
 		for ( int i=0; i<length; i++ ) {
-			list.add( elemType.toLoggableString( Array.get(value, i), factory ) );
+			Object element = Array.get(value, i);
+			if ( element == LazyPropertyInitializer.UNFETCHED_PROPERTY || !Hibernate.isInitialized( element ) ) {
+				list.add( "<uninitialized>" );
+			}
+			else {
+				list.add( elemType.toLoggableString( element, factory ) );
+			}
 		}
 		return list.toString();
 	}
 
 	@Override
 	public Object instantiateResult(Object original) {
 		return Array.newInstance( elementClass, Array.getLength(original) );
 	}
 
 	@Override
 	public Object replaceElements(
 		Object original,
 		Object target,
 		Object owner, 
 		Map copyCache,
 		SharedSessionContractImplementor session) throws HibernateException {
 		
 		int length = Array.getLength(original);
 		if ( length!=Array.getLength(target) ) {
 			//note: this affects the return value!
 			target=instantiateResult(original);
 		}
 		
 		Type elemType = getElementType( session.getFactory() );
 		for ( int i=0; i<length; i++ ) {
 			Array.set( target, i, elemType.replace( Array.get(original, i), null, session, owner, copyCache ) );
 		}
 		
 		return target;
 	
 	}
 
 	@Override
 	public Object instantiate(int anticipatedSize) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public Object indexOf(Object array, Object element) {
 		int length = Array.getLength(array);
 		for ( int i=0; i<length; i++ ) {
 			//TODO: proxies!
 			if ( Array.get(array, i)==element ) {
 				return i;
 			}
 		}
 		return null;
 	}
 
 	@Override
 	protected boolean initializeImmediately() {
 		return true;
 	}
 
 	@Override
 	public boolean hasHolder() {
 		return true;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
index bbfdda0f99..78cfb88ed0 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
@@ -1,820 +1,821 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.SortedMap;
 import java.util.TreeMap;
 
 import org.hibernate.EntityMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.collection.internal.AbstractPersistentCollection;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 import org.jboss.logging.Logger;
 
 /**
  * A type that handles Hibernate <tt>PersistentCollection</tt>s (including arrays).
  * 
  * @author Gavin King
  */
 public abstract class CollectionType extends AbstractType implements AssociationType {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionType.class.getName());
 
 	private static final Object NOT_NULL_COLLECTION = new MarkerObject( "NOT NULL COLLECTION" );
 	public static final Object UNFETCHED_COLLECTION = new MarkerObject( "UNFETCHED COLLECTION" );
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String role;
 	private final String foreignKeyPropertyName;
 
 	public CollectionType(TypeFactory.TypeScope typeScope, String role, String foreignKeyPropertyName) {
 		this.typeScope = typeScope;
 		this.role = role;
 		this.foreignKeyPropertyName = foreignKeyPropertyName;
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public Object indexOf(Object collection, Object element) {
 		throw new UnsupportedOperationException( "generic collections don't have indexes" );
 	}
 
 	public boolean contains(Object collection, Object childObject, SharedSessionContractImplementor session) {
 		// we do not have to worry about queued additions to uninitialized
 		// collections, since they can only occur for inverse collections!
 		Iterator elems = getElementsIterator( collection, session );
 		while ( elems.hasNext() ) {
 			Object element = elems.next();
 			// worrying about proxies is perhaps a little bit of overkill here...
 			if ( element instanceof HibernateProxy ) {
 				LazyInitializer li = ( (HibernateProxy) element ).getHibernateLazyInitializer();
 				if ( !li.isUninitialized() ) {
 					element = li.getImplementation();
 				}
 			}
 			if ( element == childObject ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	@Override
 	public boolean isCollectionType() {
 		return true;
 	}
 
 	@Override
 	public final boolean isEqual(Object x, Object y) {
 		return x == y
 			|| ( x instanceof PersistentCollection && ( (PersistentCollection) x ).wasInitialized() && ( (PersistentCollection) x ).isWrapper( y ) )
 			|| ( y instanceof PersistentCollection && ( (PersistentCollection) y ).wasInitialized() && ( (PersistentCollection) y ).isWrapper( x ) );
 	}
 
 	@Override
 	public int compare(Object x, Object y) {
 		return 0; // collections cannot be compared
 	}
 
 	@Override
 	public int getHashCode(Object x) {
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
 	public abstract PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key);
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner) throws SQLException {
 		return nullSafeGet( rs, new String[] { name }, session, owner );
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String[] name, SharedSessionContractImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( null, session, owner );
 	}
 
 	@Override
 	public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable,
 			SharedSessionContractImplementor session) throws HibernateException, SQLException {
 		//NOOP
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value, int index,
 			SharedSessionContractImplementor session) throws HibernateException, SQLException {
 	}
 
 	@Override
 	public int[] sqlTypes(Mapping session) throws MappingException {
 		return ArrayHelper.EMPTY_INT_ARRAY;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return new Size[] { LEGACY_DICTATED_SIZE };
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return new Size[] { LEGACY_DEFAULT_SIZE };
 	}
 
 	@Override
 	public int getColumnSpan(Mapping session) throws MappingException {
 		return 0;
 	}
 
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 
 		if ( !getReturnedClass().isInstance( value ) && !PersistentCollection.class.isInstance( value ) ) {
 			// its most likely the collection-key
 			final CollectionPersister persister = getPersister( factory );
 			if ( persister.getKeyType().getReturnedClass().isInstance( value ) ) {
 				return getRole() + "#" + getPersister( factory ).getKeyType().toLoggableString( value, factory );
 			}
 			else {
 				// although it could also be the collection-id
 				if ( persister.getIdentifierType() != null
 						&& persister.getIdentifierType().getReturnedClass().isInstance( value ) ) {
 					return getRole() + "#" + getPersister( factory ).getIdentifierType().toLoggableString( value, factory );
 				}
 			}
 		}
-
-		if ( !Hibernate.isInitialized( value ) ) {
-			return "<uninitialized>";
-		}
-		else {
-			return renderLoggableString( value, factory );
-		}
+		return renderLoggableString( value, factory );
 	}
 
 	protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		final List<String> list = new ArrayList<String>();
 		Type elemType = getElementType( factory );
 		Iterator itr = getElementsIterator( value );
 		while ( itr.hasNext() ) {
-			list.add( elemType.toLoggableString( itr.next(), factory ) );
+			Object element = itr.next();
+			if ( element == LazyPropertyInitializer.UNFETCHED_PROPERTY || !Hibernate.isInitialized( element ) ) {
+				list.add( "<uninitialized>" );
+			}
+			else {
+				list.add( elemType.toLoggableString( element, factory ) );
+			}
 		}
 		return list.toString();
 	}
 
 	@Override
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return value;
 	}
 
 	@Override
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
 	public Iterator getElementsIterator(Object collection, SharedSessionContractImplementor session) {
 		return getElementsIterator( collection );
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
 
 	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	@Override
 	public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner)
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
 
 	@Override
 	public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner)
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
 	private boolean isOwnerVersioned(SharedSessionContractImplementor session) throws MappingException {
 		return getPersister( session ).getOwnerEntityPersister().isVersioned();
 	}
 
 	/**
 	 * Get our underlying collection persister (using the session to access the
 	 * factory).
 	 *
 	 * @param session The session from which the request is originating.
 	 * @return The underlying collection persister
 	 */
 	private CollectionPersister getPersister(SharedSessionContractImplementor session) {
 		return getPersister( session.getFactory() );
 	}
 
 	private CollectionPersister getPersister(SessionFactoryImplementor factory) {
 		return factory.getMetamodel().collectionPersister( role );
 	}
 
 	@Override
 	public boolean isDirty(Object old, Object current, SharedSessionContractImplementor session)
 			throws HibernateException {
 
 		// collections don't dirty an unversioned parent entity
 
 		// TODO: I don't really like this implementation; it would be better if
 		// this was handled by searchForDirtyCollections()
 		return super.isDirty( old, current, session );
 		// return false;
 
 	}
 
 	@Override
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session)
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
 	public abstract PersistentCollection wrap(SharedSessionContractImplementor session, Object collection);
 
 	/**
 	 * Note: return true because this type is castable to <tt>AssociationType</tt>. Not because
 	 * all collections are associations.
 	 */
 	@Override
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	@Override
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.TO_PARENT;
 	}
 
 	/**
 	 * Get the key value from the owning entity instance, usually the identifier, but might be some
 	 * other unique key, in the case of property-ref
 	 *
 	 * @param owner The collection owner
 	 * @param session The session from which the request is originating.
 	 * @return The collection owner's key
 	 */
 	public Serializable getKeyOfOwner(Object owner, SharedSessionContractImplementor session) {
 		
 		EntityEntry entityEntry = session.getPersistenceContext().getEntry( owner );
 		if ( entityEntry == null ) {
 			// This just handles a particular case of component
 			// projection, perhaps get rid of it and throw an exception
 			return null;
 		}
 		
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
 				id = entityEntry.getPersister().getPropertyValue( owner, foreignKeyPropertyName );
 			}
 
 			// NOTE VERY HACKISH WORKAROUND!!
 			// TODO: Fix this so it will work for non-POJO entity mode
 			Type keyType = getPersister( session ).getKeyType();
 			if ( !keyType.getReturnedClass().isInstance( id ) ) {
 				id = keyType.semiResolve(
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
 	public Serializable getIdOfOwnerOrNull(Serializable key, SharedSessionContractImplementor session) {
 		Serializable ownerId = null;
 		if ( foreignKeyPropertyName == null ) {
 			ownerId = key;
 		}
 		else {
 			Type keyType = getPersister( session ).getKeyType();
 			EntityPersister ownerPersister = getPersister( session ).getOwnerEntityPersister();
 			// TODO: Fix this so it will work for non-POJO entity mode
 			Class ownerMappedClass = ownerPersister.getMappedClass();
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
 
 	@Override
 	public Object hydrate(ResultSet rs, String[] name, SharedSessionContractImplementor session, Object owner) {
 		// can't just return null here, since that would
 		// cause an owning component to become null
 		return NOT_NULL_COLLECTION;
 	}
 
 	@Override
 	public Object resolve(Object value, SharedSessionContractImplementor session, Object owner)
 			throws HibernateException {
 		
 		return resolveKey( getKeyOfOwner( owner, session ), session, owner );
 	}
 	
 	private Object resolveKey(Serializable key, SharedSessionContractImplementor session, Object owner) {
 		// if (key==null) throw new AssertionFailure("owner identifier unknown when re-assembling
 		// collection reference");
 		return key == null ? null : // TODO: can this case really occur??
 			getCollection( key, session, owner );
 	}
 
 	@Override
 	public Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner)
 			throws HibernateException {
 		throw new UnsupportedOperationException(
 			"collection mappings may not form part of a property-ref" );
 	}
 
 	public boolean isArrayType() {
 		return false;
 	}
 
 	@Override
 	public boolean useLHSPrimaryKey() {
 		return foreignKeyPropertyName == null;
 	}
 
 	@Override
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	@Override
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory)
 			throws MappingException {
 		return (Joinable) factory.getCollectionPersister( role );
 	}
 
 	@Override
 	public boolean isModified(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
 		return false;
 	}
 
 	@Override
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
 			SharedSessionContractImplementor session) {
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
 		// here afterQuery the copy operation.
 		// </p>
 		// One thing to be careful of here is a "bare" original collection
 		// in which case we should never ever ever reset the dirty flag
 		// on the target because we simply do not know...
 		if ( original instanceof PersistentCollection ) {
 			if ( result instanceof PersistentCollection ) {
 				final PersistentCollection originalPersistentCollection = (PersistentCollection) original;
 				final PersistentCollection resultPersistentCollection = (PersistentCollection) result;
 
 				preserveSnapshot( originalPersistentCollection, resultPersistentCollection, elemType, owner, copyCache, session );
 
 				if ( ! originalPersistentCollection.isDirty() ) {
 					resultPersistentCollection.clearDirty();
 				}
 			}
 		}
 
 		return result;
 	}
 
 	private void preserveSnapshot(
 			PersistentCollection original,
 			PersistentCollection result,
 			Type elemType,
 			Object owner,
 			Map copyCache,
 			SharedSessionContractImplementor session) {
 		Serializable originalSnapshot = original.getStoredSnapshot();
 		Serializable resultSnapshot = result.getStoredSnapshot();
 		Serializable targetSnapshot;
 
 		if ( originalSnapshot instanceof List ) {
 			targetSnapshot = new ArrayList(
 					( (List) originalSnapshot ).size() );
 			for ( Object obj : (List) originalSnapshot ) {
 				( (List) targetSnapshot ).add( elemType.replace( obj, null, session, owner, copyCache ) );
 			}
 
 		}
 		else if ( originalSnapshot instanceof Map ) {
 			if ( originalSnapshot instanceof SortedMap ) {
 				targetSnapshot = new TreeMap( ( (SortedMap) originalSnapshot ).comparator() );
 			}
 			else {
 				targetSnapshot = new HashMap(
 						CollectionHelper.determineProperSizing( ( (Map) originalSnapshot ).size() ),
 						CollectionHelper.LOAD_FACTOR
 				);
 			}
 
 			for ( Map.Entry<Object, Object> entry : ( (Map<Object, Object>) originalSnapshot ).entrySet() ) {
 				Object key = entry.getKey();
 				Object value = entry.getValue();
 				Object resultSnapshotValue = ( resultSnapshot == null )
 						? null
 						: ( (Map<Object, Object>) resultSnapshot ).get( key );
 
 				Object newValue = elemType.replace( value, resultSnapshotValue, session, owner, copyCache );
 
 				if ( key == value ) {
 					( (Map) targetSnapshot ).put( newValue, newValue );
 
 				}
 				else {
 					( (Map) targetSnapshot ).put( key, newValue );
 				}
 
 			}
 
 		}
 		else if ( originalSnapshot instanceof Object[] ) {
 			Object[] arr = (Object[]) originalSnapshot;
 			for ( int i = 0; i < arr.length; i++ ) {
 				arr[i] = elemType.replace( arr[i], null, session, owner, copyCache );
 			}
 			targetSnapshot = originalSnapshot;
 
 		}
 		else {
 			// retain the same snapshot
 			targetSnapshot = resultSnapshot;
 
 		}
 
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( result );
 		if ( ce != null ) {
 			ce.resetStoredSnapshot( result, targetSnapshot );
 		}
 
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
 	 * afterQuery we are done populating it.
 	 * @return A newly instantiated collection to be wrapped.
 	 */
 	public abstract Object instantiate(int anticipatedSize);
 
 	@Override
 	public Object replace(
 			final Object original,
 			final Object target,
 			final SharedSessionContractImplementor session,
 			final Object owner,
 			final Map copyCache) throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		if ( !Hibernate.isInitialized( original ) ) {
 			if ( ( (PersistentCollection) original ).hasQueuedOperations() ) {
 				final AbstractPersistentCollection pc = (AbstractPersistentCollection) original;
 				pc.replaceQueuedOperationValues( getPersister( session ), copyCache );
 			}
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
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
 	@Override
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
 			throws MappingException {
 		return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
 	}
 
 	@Override
 	public String getOnCondition(
 			String alias,
 			SessionFactoryImplementor factory,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters, treatAsDeclarations );
 	}
 
 	/**
 	 * instantiate a collection wrapper (called when loading an object)
 	 *
 	 * @param key The collection owner key
 	 * @param session The session from which the request is originating.
 	 * @param owner The collection owner
 	 * @return The collection
 	 */
 	public Object getCollection(Serializable key, SharedSessionContractImplementor session, Object owner) {
 
 		CollectionPersister persister = getPersister( session );
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityMode entityMode = persister.getOwnerEntityPersister().getEntityMode();
 
 		// check if collection is currently being loaded
 		PersistentCollection collection = persistenceContext.getLoadContexts().locateLoadingCollection( persister, key );
 		
 		if ( collection == null ) {
 			
 			// check if it is already completely loaded, but unowned
 			collection = persistenceContext.useUnownedCollection( new CollectionKey(persister, key, entityMode) );
 			
 			if ( collection == null ) {
 
 				collection = persistenceContext.getCollection( new CollectionKey(persister, key, entityMode) );
 
 				if ( collection == null ) {
 					// create a new collection wrapper, to be initialized later
 					collection = instantiate( session, persister, key );
 
 					collection.setOwner( owner );
 
 					persistenceContext.addUninitializedCollection( persister, collection, key );
 
 					// some collections are not lazy:
 					if ( initializeImmediately() ) {
 						session.initializeCollection( collection, false );
 					}
 					else if ( !persister.isLazy() ) {
 						persistenceContext.addNonLazyCollection( collection );
 					}
 
 					if ( hasHolder() ) {
 						session.getPersistenceContext().addCollectionHolder( collection );
 					}
 				}
 
 			}
 			
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracef( "Created collection wrapper: %s",
 						MessageHelper.collectionInfoString( persister, collection,
 								key, session ) );
 			}
 			
 		}
 		
 		collection.setOwner(owner);
 
 		return collection.getValue();
 	}
 
 	public boolean hasHolder() {
 		return false;
 	}
 
 	protected boolean initializeImmediately() {
 		return false;
 	}
 
 	@Override
 	public String getLHSPropertyName() {
 		return foreignKeyPropertyName;
 	}
 
 	/**
 	 * We always need to dirty check the collection because we sometimes 
 	 * need to incremement version number of owner and also because of 
 	 * how assemble/disassemble is implemented for uks
 	 */
 	@Override
 	public boolean isAlwaysDirtyChecked() {
 		return true; 
 	}
 
 	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
index 5fc704bd83..7454117171 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
@@ -1,839 +1,845 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.ValueGeneration;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.tuple.component.ComponentTuplizer;
 
 /**
  * Handles "component" mappings
  *
  * @author Gavin King
  */
 public class ComponentType extends AbstractType implements CompositeType, ProcedureParameterExtractionAware {
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final ValueGeneration[] propertyValueGenerationStrategies;
 	private final boolean[] propertyNullability;
 	protected final int propertySpan;
 	private final CascadeStyle[] cascade;
 	private final FetchMode[] joinedFetch;
 	private final boolean isKey;
 	private boolean hasNotNullProperty;
 	private final boolean createEmptyCompositesEnabled;
 
 	protected final EntityMode entityMode;
 	protected final ComponentTuplizer componentTuplizer;
 
 	public ComponentType(TypeFactory.TypeScope typeScope, ComponentMetamodel metamodel) {
 		this.typeScope = typeScope;
 		// for now, just "re-flatten" the metamodel since this is temporary stuff anyway (HHH-1907)
 		this.isKey = metamodel.isKey();
 		this.propertySpan = metamodel.getPropertySpan();
 		this.propertyNames = new String[propertySpan];
 		this.propertyTypes = new Type[propertySpan];
 		this.propertyValueGenerationStrategies = new ValueGeneration[propertySpan];
 		this.propertyNullability = new boolean[propertySpan];
 		this.cascade = new CascadeStyle[propertySpan];
 		this.joinedFetch = new FetchMode[propertySpan];
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			StandardProperty prop = metamodel.getProperty( i );
 			this.propertyNames[i] = prop.getName();
 			this.propertyTypes[i] = prop.getType();
 			this.propertyNullability[i] = prop.isNullable();
 			this.cascade[i] = prop.getCascadeStyle();
 			this.joinedFetch[i] = prop.getFetchMode();
 			if ( !prop.isNullable() ) {
 				hasNotNullProperty = true;
 			}
 			this.propertyValueGenerationStrategies[i] = prop.getValueGenerationStrategy();
 		}
 
 		this.entityMode = metamodel.getEntityMode();
 		this.componentTuplizer = metamodel.getComponentTuplizer();
 		this.createEmptyCompositesEnabled = metamodel.isCreateEmptyCompositesEnabled();
 	}
 
 	public boolean isKey() {
 		return isKey;
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	public ComponentTuplizer getComponentTuplizer() {
 		return componentTuplizer;
 	}
 
 	@Override
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		int span = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			span += propertyTypes[i].getColumnSpan( mapping );
 		}
 		return span;
 	}
 
 	@Override
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		int[] sqlTypes = new int[getColumnSpan( mapping )];
 		int n = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int[] subtypes = propertyTypes[i].sqlTypes( mapping );
 			for ( int subtype : subtypes ) {
 				sqlTypes[n++] = subtype;
 			}
 		}
 		return sqlTypes;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[getColumnSpan( mapping )];
 		int soFar = 0;
 		for ( Type propertyType : propertyTypes ) {
 			final Size[] propertySizes = propertyType.dictatedSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[getColumnSpan( mapping )];
 		int soFar = 0;
 		for ( Type propertyType : propertyTypes ) {
 			final Size[] propertySizes = propertyType.defaultSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 
 	@Override
 	public final boolean isComponentType() {
 		return true;
 	}
 
 	public Class getReturnedClass() {
 		return componentTuplizer.getMappedClass();
 	}
 
 	@Override
 	public boolean isSame(Object x, Object y) throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		// null value and empty component are considered equivalent
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isSame( xvalues[i], yvalues[i] ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public boolean isEqual(final Object x, final Object y) throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		// null value and empty component are considered equivalent
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( getPropertyValue( x, i ), getPropertyValue( y, i ) ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public boolean isEqual(final Object x, final Object y, final SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		// null value and empty component are considered equivalent
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( getPropertyValue( x, i ), getPropertyValue( y, i ), factory ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public int compare(final Object x, final Object y) {
 		if ( x == y ) {
 			return 0;
 		}
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int propertyCompare = propertyTypes[i].compare( getPropertyValue( x, i ), getPropertyValue( y, i ) );
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
 	public int getHashCode(final Object x) {
 		int result = 17;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = getPropertyValue( x, i );
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y );
 			}
 		}
 		return result;
 	}
 
 	@Override
 	public int getHashCode(final Object x, final SessionFactoryImplementor factory) {
 		int result = 17;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = getPropertyValue( x, i );
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y, factory );
 			}
 		}
 		return result;
 	}
 
 	@Override
 	public boolean isDirty(final Object x, final Object y, final SharedSessionContractImplementor session) throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		// null value and empty component are considered equivalent
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( propertyTypes[i].isDirty( getPropertyValue( x, i ), getPropertyValue( y, i ), session ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public boolean isDirty(final Object x, final Object y, final boolean[] checkable, final SharedSessionContractImplementor session)
 			throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		// null value and empty component are considered equivalent
 		int loc = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			if ( len <= 1 ) {
 				final boolean dirty = ( len == 0 || checkable[loc] ) &&
 						propertyTypes[i].isDirty( getPropertyValue( x, i ), getPropertyValue( y, i ), session );
 				if ( dirty ) {
 					return true;
 				}
 			}
 			else {
 				boolean[] subcheckable = new boolean[len];
 				System.arraycopy( checkable, loc, subcheckable, 0, len );
 				final boolean dirty = propertyTypes[i].isDirty(
 						getPropertyValue( x, i ),
 						getPropertyValue( y, i ),
 						subcheckable,
 						session
 				);
 				if ( dirty ) {
 					return true;
 				}
 			}
 			loc += len;
 		}
 		return false;
 	}
 
 	@Override
 	public boolean isModified(
 			final Object old,
 			final Object current,
 			final boolean[] checkable,
 			final SharedSessionContractImplementor session) throws HibernateException {
 		if ( current == null ) {
 			return old != null;
 		}
 		if ( old == null ) {
 			return true;
 		}
 		Object[] oldValues = (Object[]) old;
 		int loc = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			boolean[] subcheckable = new boolean[len];
 			System.arraycopy( checkable, loc, subcheckable, 0, len );
 			if ( propertyTypes[i].isModified( oldValues[i], getPropertyValue( current, i ), subcheckable, session ) ) {
 				return true;
 			}
 			loc += len;
 		}
 		return false;
 
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( hydrate( rs, names, session, owner ), session, owner );
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value, int begin, SharedSessionContractImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, entityMode );
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			propertyTypes[i].nullSafeSet( st, subvalues[i], begin, session );
 			begin += propertyTypes[i].getColumnSpan( session.getFactory() );
 		}
 	}
 
 	@Override
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int begin,
 			boolean[] settable,
 			SharedSessionContractImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, entityMode );
 
 		int loc = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			//noinspection StatementWithEmptyBody
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
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner)
 			throws HibernateException, SQLException {
 
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	@Override
 	public Object getPropertyValue(Object component, int i, SharedSessionContractImplementor session)
 			throws HibernateException {
 		return getPropertyValue( component, i );
 	}
 
 	public Object getPropertyValue(Object component, int i, EntityMode entityMode)
 			throws HibernateException {
 		return getPropertyValue( component, i );
 	}
 
 	public Object getPropertyValue(Object component, int i)
 			throws HibernateException {
 		if (component == null) {
 			component = new Object[propertySpan];
 		}
 		if ( component instanceof Object[] ) {
 			// A few calls to hashCode pass the property values already in an
 			// Object[] (ex: QueryKey hash codes for cached queries).
 			// It's easiest to just check for the condition here prior to
 			// trying reflection.
 			return ( (Object[]) component )[i];
 		}
 		else {
 			return componentTuplizer.getPropertyValue( component, i );
 		}
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, SharedSessionContractImplementor session)
 			throws HibernateException {
 		return getPropertyValues( component, entityMode );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, EntityMode entityMode)
 			throws HibernateException {
 		if (component == null) {
 			component = new Object[propertySpan];
 		}
 		if ( component instanceof Object[] ) {
 			// A few calls to hashCode pass the property values already in an 
 			// Object[] (ex: QueryKey hash codes for cached queries).
 			// It's easiest to just check for the condition here prior to
 			// trying reflection.
 			return (Object[]) component;
 		}
 		else {
 			return componentTuplizer.getPropertyValues( component );
 		}
 	}
 
 	@Override
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
 			throws HibernateException {
 		componentTuplizer.setPropertyValues( component, values );
 	}
 
 	@Override
 	public Type[] getSubtypes() {
 		return propertyTypes;
 	}
 
 	public ValueGeneration[] getPropertyValueGenerationStrategies() {
 		return propertyValueGenerationStrategies;
 	}
 
 	@Override
 	public String getName() {
 		return "component" + ArrayHelper.toString( propertyNames );
 	}
 
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 
 		if ( entityMode == null ) {
 			throw new ClassCastException( value.getClass().getName() );
 		}
 		Map<String, String> result = new HashMap<>();
 		Object[] values = getPropertyValues( value, entityMode );
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
-			result.put( propertyNames[i], propertyTypes[i].toLoggableString( values[i], factory ) );
+			if ( values[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
+				result.put( propertyNames[i], "<uninitialized>" );
+			}
+			else {
+				result.put( propertyNames[i], propertyTypes[i].toLoggableString( values[i], factory ) );
+			}
 		}
 		return StringHelper.unqualify( getName() ) + result.toString();
 	}
 
 	@Override
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
 
 	@Override
 	public Object deepCopy(Object component, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( component == null ) {
 			return null;
 		}
 
 		Object[] values = getPropertyValues( component, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			values[i] = propertyTypes[i].deepCopy( values[i], factory );
 		}
 
 		Object result = instantiate( entityMode );
 		setPropertyValues( result, values, entityMode );
 
 		//not absolutely necessary, but helps for some
 		//equals()/hashCode() implementations
 		if ( componentTuplizer.hasParentProperty() ) {
 			componentTuplizer.setParent( result, componentTuplizer.getParent( component ), factory );
 		}
 
 		return result;
 	}
 
 	@Override
 	public Object replace(
 			Object original,
 			Object target,
 			SharedSessionContractImplementor session,
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
 			SharedSessionContractImplementor session,
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
 		return componentTuplizer.instantiate();
 	}
 
 	public Object instantiate(Object parent, SharedSessionContractImplementor session)
 			throws HibernateException {
 
 		Object result = instantiate( entityMode );
 
 		if ( componentTuplizer.hasParentProperty() && parent != null ) {
 			componentTuplizer.setParent(
 					result,
 					session.getPersistenceContext().proxyFor( parent ),
 					session.getFactory()
 			);
 		}
 
 		return result;
 	}
 
 	@Override
 	public CascadeStyle getCascadeStyle(int i) {
 		return cascade[i];
 	}
 
 	@Override
 	public boolean isMutable() {
 		return true;
 	}
 
 	@Override
 	public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			Object[] values = getPropertyValues( value, entityMode );
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				values[i] = propertyTypes[i].disassemble( values[i], session, owner );
 			}
 			return values;
 		}
 	}
 
 	@Override
 	public Object assemble(Serializable object, SharedSessionContractImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( object == null ) {
 			return null;
 		}
 		else {
 			Object[] values = (Object[]) object;
 			Object[] assembled = new Object[values.length];
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				assembled[i] = propertyTypes[i].assemble( (Serializable) values[i], session, owner );
 			}
 			Object result = instantiate( owner, session );
 			setPropertyValues( result, assembled, entityMode );
 			return result;
 		}
 	}
 
 	@Override
 	public FetchMode getFetchMode(int i) {
 		return joinedFetch[i];
 	}
 
 	@Override
 	public Object hydrate(
 			final ResultSet rs,
 			final String[] names,
 			final SharedSessionContractImplementor session,
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
 	public Object resolve(Object value, SharedSessionContractImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( value != null ) {
 			Object result = instantiate( owner, session );
 			Object[] values = (Object[]) value;
 			Object[] resolvedValues = new Object[values.length]; //only really need new array during semiresolve!
 			for ( int i = 0; i < values.length; i++ ) {
 				resolvedValues[i] = propertyTypes[i].resolve( values[i], session, owner );
 			}
 			setPropertyValues( result, resolvedValues, entityMode );
 			return result;
 		}
 		else if ( isCreateEmptyCompositesEnabled() ) {
 			return instantiate( owner, session );
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
 	public Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner)
 			throws HibernateException {
 		//note that this implementation is kinda broken
 		//for components with many-to-one associations
 		return resolve( value, session, owner );
 	}
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[getColumnSpan( mapping )];
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
 
 	@Override
 	public boolean isEmbedded() {
 		return false;
 	}
 
 	@Override
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
 
 	private Boolean canDoExtraction;
 
 	@Override
 	public boolean canDoExtraction() {
 		if ( canDoExtraction == null ) {
 			canDoExtraction = determineIfProcedureParamExtractionCanBePerformed();
 		}
 		return canDoExtraction;
 	}
 
 	private boolean determineIfProcedureParamExtractionCanBePerformed() {
 		for ( Type propertyType : propertyTypes ) {
 			if ( !ProcedureParameterExtractionAware.class.isInstance( propertyType ) ) {
 				return false;
 			}
 			if ( !( (ProcedureParameterExtractionAware) propertyType ).canDoExtraction() ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public Object extract(CallableStatement statement, int startIndex, SharedSessionContractImplementor session) throws SQLException {
 		Object[] values = new Object[propertySpan];
 
 		int currentIndex = startIndex;
 		boolean notNull = false;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			// we know this cast is safe from canDoExtraction
 			final Type propertyType = propertyTypes[i];
 			final Object value = ((ProcedureParameterExtractionAware) propertyType).extract(
 					statement,
 					currentIndex,
 					session
 			);
 			if ( value == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[i] = value;
 			currentIndex += propertyType.getColumnSpan( session.getFactory() );
 		}
 
 		if ( !notNull ) {
 			values = null;
 		}
 
 		return resolve( values, session, null );
 	}
 
 	@Override
 	public Object extract(CallableStatement statement, String[] paramNames, SharedSessionContractImplementor session)
 			throws SQLException {
 		// for this form to work all sub-property spans must be one (1)...
 
 		Object[] values = new Object[propertySpan];
 
 		int indx = 0;
 		boolean notNull = false;
 		for ( String paramName : paramNames ) {
 			// we know this cast is safe from canDoExtraction
 			final ProcedureParameterExtractionAware propertyType = (ProcedureParameterExtractionAware) propertyTypes[indx];
 			final Object value = propertyType.extract( statement, new String[] {paramName}, session );
 			if ( value == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[indx] = value;
 		}
 
 		if ( !notNull ) {
 			values = null;
 		}
 
 		return resolve( values, session, null );
 	}
 
 	@Override
 	public boolean hasNotNullProperty() {
 		return hasNotNullProperty;
 	}
 
 	private boolean isCreateEmptyCompositesEnabled() {
 		return createEmptyCompositesEnabled;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java b/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
index 25bb503edc..cef4709595 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
@@ -1,381 +1,389 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.Map;
 
+import org.hibernate.Hibernate;
 import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.property.access.internal.PropertyAccessStrategyBackRefImpl;
 import org.hibernate.tuple.NonIdentifierAttribute;
 
 /**
  * Collection of convenience methods relating to operations across arrays of types...
  *
  * @author Steve Ebersole
  */
 public class TypeHelper {
 	/**
 	 * Disallow instantiation
 	 */
 	private TypeHelper() {
 	}
 
 	/**
 	 * Deep copy a series of values from one array to another...
 	 *
 	 * @param values The values to copy (the source)
 	 * @param types The value types
 	 * @param copy an array indicating which values to include in the copy
 	 * @param target The array into which to copy the values
 	 * @param session The originating session
 	 */
 	public static void deepCopy(
 			final Object[] values,
 			final Type[] types,
 			final boolean[] copy,
 			final Object[] target,
 			final SharedSessionContractImplementor session) {
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( copy[i] ) {
 				if ( values[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY
 					|| values[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 					target[i] = values[i];
 				}
 				else {
 					target[i] = types[i].deepCopy( values[i], session
 						.getFactory() );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Apply the {@link Type#beforeAssemble} operation across a series of values.
 	 *
 	 * @param row The values
 	 * @param types The value types
 	 * @param session The originating session
 	 */
 	public static void beforeAssemble(
 			final Serializable[] row,
 			final Type[] types,
 			final SharedSessionContractImplementor session) {
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( row[i] != LazyPropertyInitializer.UNFETCHED_PROPERTY
 				&& row[i] != PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				types[i].beforeAssemble( row[i], session );
 			}
 		}
 	}
 
 	/**
 	 * Apply the {@link Type#assemble} operation across a series of values.
 	 *
 	 * @param row The values
 	 * @param types The value types
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 * @return The assembled state
 	 */
 	public static Object[] assemble(
 			final Serializable[] row,
 			final Type[] types,
 			final SharedSessionContractImplementor session,
 			final Object owner) {
 		Object[] assembled = new Object[row.length];
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( row[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || row[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				assembled[i] = row[i];
 			}
 			else {
 				assembled[i] = types[i].assemble( row[i], session, owner );
 			}
 		}
 		return assembled;
 	}
 
 	/**
 	 * Apply the {@link Type#disassemble} operation across a series of values.
 	 *
 	 * @param row The values
 	 * @param types The value types
 	 * @param nonCacheable An array indicating which values to include in the disassembled state
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 *
 	 * @return The disassembled state
 	 */
 	public static Serializable[] disassemble(
 			final Object[] row,
 			final Type[] types,
 			final boolean[] nonCacheable,
 			final SharedSessionContractImplementor session,
 			final Object owner) {
 		Serializable[] disassembled = new Serializable[row.length];
 		for ( int i = 0; i < row.length; i++ ) {
 			if ( nonCacheable!=null && nonCacheable[i] ) {
 				disassembled[i] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 			}
 			else if ( row[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || row[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				disassembled[i] = (Serializable) row[i];
 			}
 			else {
 				disassembled[i] = types[i].disassemble( row[i], session, owner );
 			}
 		}
 		return disassembled;
 	}
 
 	/**
 	 * Apply the {@link Type#replace} operation across a series of values.
 	 *
 	 * @param original The source of the state
 	 * @param target The target into which to replace the source values.
 	 * @param types The value types
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 * @param copyCache A map representing a cache of already replaced state
 	 *
 	 * @return The replaced state
 	 */
 	public static Object[] replace(
 			final Object[] original,
 			final Object[] target,
 			final Type[] types,
 			final SharedSessionContractImplementor session,
 			final Object owner,
 			final Map copyCache) {
 		Object[] copied = new Object[original.length];
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( original[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY
 				|| original[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				copied[i] = target[i];
 			}
 			else if ( target[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 				// Should be no need to check for target[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN
 				// because PropertyAccessStrategyBackRefImpl.get( object ) returns
 				// PropertyAccessStrategyBackRefImpl.UNKNOWN, so target[i] == original[i].
 				//
 				// We know from above that original[i] != LazyPropertyInitializer.UNFETCHED_PROPERTY &&
 				// original[i] != PropertyAccessStrategyBackRefImpl.UNKNOWN;
 				// This is a case where the entity being merged has a lazy property
 				// that has been initialized. Copy the initialized value from original.
 				if ( types[i].isMutable() ) {
 					copied[i] = types[i].deepCopy( original[i], session.getFactory() );
 				}
 				else {
 					copied[i] = original[i];
 				}
 			}
 			else {
 				copied[i] = types[i].replace( original[i], target[i], session, owner, copyCache );
 			}
 		}
 		return copied;
 	}
 
 	/**
 	 * Apply the {@link Type#replace} operation across a series of values.
 	 *
 	 * @param original The source of the state
 	 * @param target The target into which to replace the source values.
 	 * @param types The value types
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 * @param copyCache A map representing a cache of already replaced state
 	 * @param foreignKeyDirection FK directionality to be applied to the replacement
 	 *
 	 * @return The replaced state
 	 */
 	public static Object[] replace(
 			final Object[] original,
 			final Object[] target,
 			final Type[] types,
 			final SharedSessionContractImplementor session,
 			final Object owner,
 			final Map copyCache,
 			final ForeignKeyDirection foreignKeyDirection) {
 		Object[] copied = new Object[original.length];
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( original[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY
 					|| original[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				copied[i] = target[i];
 			}
 			else {
 				copied[i] = types[i].replace( original[i], target[i], session, owner, copyCache, foreignKeyDirection );
 			}
 		}
 		return copied;
 	}
 
 	/**
 	 * Apply the {@link Type#replace} operation across a series of values, as long as the corresponding
 	 * {@link Type} is an association.
 	 * <p/>
 	 * If the corresponding type is a component type, then apply {@link Type#replace} across the component
 	 * subtypes but do not replace the component value itself.
 	 *
 	 * @param original The source of the state
 	 * @param target The target into which to replace the source values.
 	 * @param types The value types
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 * @param copyCache A map representing a cache of already replaced state
 	 * @param foreignKeyDirection FK directionality to be applied to the replacement
 	 *
 	 * @return The replaced state
 	 */
 	public static Object[] replaceAssociations(
 			final Object[] original,
 			final Object[] target,
 			final Type[] types,
 			final SharedSessionContractImplementor session,
 			final Object owner,
 			final Map copyCache,
 			final ForeignKeyDirection foreignKeyDirection) {
 		Object[] copied = new Object[original.length];
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( original[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY
 					|| original[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				copied[i] = target[i];
 			}
 			else if ( types[i].isComponentType() ) {
 				// need to extract the component values and check for subtype replacements...
 				CompositeType componentType = ( CompositeType ) types[i];
 				Type[] subtypes = componentType.getSubtypes();
 				Object[] origComponentValues = original[i] == null ? new Object[subtypes.length] : componentType.getPropertyValues( original[i], session );
 				Object[] targetComponentValues = target[i] == null ? new Object[subtypes.length] : componentType.getPropertyValues( target[i], session );
 				replaceAssociations( origComponentValues, targetComponentValues, subtypes, session, null, copyCache, foreignKeyDirection );
 				copied[i] = target[i];
 			}
 			else if ( !types[i].isAssociationType() ) {
 				copied[i] = target[i];
 			}
 			else {
 				copied[i] = types[i].replace( original[i], target[i], session, owner, copyCache, foreignKeyDirection );
 			}
 		}
 		return copied;
 	}
 
 	/**
 	 * Determine if any of the given field values are dirty, returning an array containing
 	 * indices of the dirty fields.
 	 * <p/>
 	 * If it is determined that no fields are dirty, null is returned.
 	 *
 	 * @param properties The property definitions
 	 * @param currentState The current state of the entity
 	 * @param previousState The baseline state of the entity
 	 * @param includeColumns Columns to be included in the dirty checking, per property
 	 * @param anyUninitializedProperties Does the entity currently hold any uninitialized property values?
 	 * @param session The session from which the dirty check request originated.
 	 * 
 	 * @return Array containing indices of the dirty properties, or null if no properties considered dirty.
 	 */
 	public static int[] findDirty(
 			final NonIdentifierAttribute[] properties,
 			final Object[] currentState,
 			final Object[] previousState,
 			final boolean[][] includeColumns,
 			final boolean anyUninitializedProperties,
 			final SharedSessionContractImplementor session) {
 		int[] results = null;
 		int count = 0;
 		int span = properties.length;
 
 		for ( int i = 0; i < span; i++ ) {
 			final boolean dirty = currentState[i] != LazyPropertyInitializer.UNFETCHED_PROPERTY
 					&& properties[i].isDirtyCheckable( anyUninitializedProperties )
 					&& properties[i].getType().isDirty( previousState[i], currentState[i], includeColumns[i], session );
 			if ( dirty ) {
 				if ( results == null ) {
 					results = new int[span];
 				}
 				results[count++] = i;
 			}
 		}
 
 		if ( count == 0 ) {
 			return null;
 		}
 		else {
 			int[] trimmed = new int[count];
 			System.arraycopy( results, 0, trimmed, 0, count );
 			return trimmed;
 		}
 	}
 
 	/**
 	 * Determine if any of the given field values are modified, returning an array containing
 	 * indices of the modified fields.
 	 * <p/>
 	 * If it is determined that no fields are dirty, null is returned.
 	 *
 	 * @param properties The property definitions
 	 * @param currentState The current state of the entity
 	 * @param previousState The baseline state of the entity
 	 * @param includeColumns Columns to be included in the mod checking, per property
 	 * @param includeProperties Array of property indices that identify which properties participate in check
 	 * @param anyUninitializedProperties Does the entity currently hold any uninitialized property values?
 	 * @param session The session from which the dirty check request originated.
 	 *
 	 * @return Array containing indices of the modified properties, or null if no properties considered modified.
 	 */
 	public static int[] findModified(
 			final NonIdentifierAttribute[] properties,
 			final Object[] currentState,
 			final Object[] previousState,
 			final boolean[][] includeColumns,
 			final boolean[] includeProperties,
 			final boolean anyUninitializedProperties,
 			final SharedSessionContractImplementor session) {
 		int[] results = null;
 		int count = 0;
 		int span = properties.length;
 
 		for ( int i = 0; i < span; i++ ) {
 			final boolean modified = currentState[ i ] != LazyPropertyInitializer.UNFETCHED_PROPERTY
 					&& includeProperties[ i ]
 					&& properties[ i ].isDirtyCheckable( anyUninitializedProperties )
 					&& properties[ i ].getType().isModified( previousState[ i ], currentState[ i ], includeColumns[ i ], session );
 			if ( modified ) {
 				if ( results == null ) {
 					results = new int[ span ];
 				}
 				results[ count++ ] = i;
 			}
 		}
 
 		if ( count == 0 ) {
 			return null;
 		}
 		else {
 			int[] trimmed = new int[ count ];
 			System.arraycopy( results, 0, trimmed, 0, count );
 			return trimmed;
 		}
 	}
 
 	public static String toLoggableString(
 			Object[] state,
 			Type[] types,
 			SessionFactoryImplementor factory) {
 		final StringBuilder buff = new StringBuilder();
 		for ( int i = 0; i < state.length; i++ ) {
 			if ( i > 0 ) {
 				buff.append( ", " );
 			}
-			buff.append( types[i].toLoggableString( state[i], factory ) );
+
+			// HHH-11173 - Instead of having to account for unfectched lazy properties in all types, it's done here
+			if ( state[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || !Hibernate.isInitialized( state[i] ) ) {
+				buff.append( "<uninitialized>" );
+			}
+			else {
+				buff.append( types[i].toLoggableString( state[i], factory ) );
+			}
 		}
 		return buff.toString();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
index c6138e2c1b..5c1b29b4a0 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
@@ -1,241 +1,248 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement;
 
 import org.hibernate.bytecode.enhance.spi.UnloadedClass;
 
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.bytecode.enhancement.EnhancerTestContext;
 import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.bytecode.enhancement.access.MixedAccessTestTask;
 import org.hibernate.test.bytecode.enhancement.association.InheritedAttributeAssociationTestTask;
 import org.hibernate.test.bytecode.enhancement.association.ManyToManyAssociationTestTask;
 import org.hibernate.test.bytecode.enhancement.association.OneToManyAssociationTestTask;
 import org.hibernate.test.bytecode.enhancement.association.OneToOneAssociationTestTask;
 import org.hibernate.test.bytecode.enhancement.basic.BasicEnhancementTestTask;
 import org.hibernate.test.bytecode.enhancement.basic.HHH9529TestTask;
 import org.hibernate.test.bytecode.enhancement.cascade.CascadeDeleteTestTask;
 import org.hibernate.test.bytecode.enhancement.dirty.DirtyTrackingCollectionTestTask;
 import org.hibernate.test.bytecode.enhancement.dirty.DirtyTrackingTestTask;
 import org.hibernate.test.bytecode.enhancement.eviction.EvictionTestTask;
 import org.hibernate.test.bytecode.enhancement.extended.ExtendedAssociationManagementTestTasK;
 import org.hibernate.test.bytecode.enhancement.extended.ExtendedEnhancementTestTask;
 import org.hibernate.test.bytecode.enhancement.join.HHH3949TestTask1;
 import org.hibernate.test.bytecode.enhancement.join.HHH3949TestTask2;
 import org.hibernate.test.bytecode.enhancement.join.HHH3949TestTask3;
 import org.hibernate.test.bytecode.enhancement.join.HHH3949TestTask4;
 import org.hibernate.test.bytecode.enhancement.lazy.HHH_10708.UnexpectedDeleteOneTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.HHH_10708.UnexpectedDeleteThreeTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.HHH_10708.UnexpectedDeleteTwoTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyBasicFieldNotInitializedTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyCollectionLoadingTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyCollectionNoTransactionLoadingTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyLoadingIntegrationTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyLoadingTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyProxyOnEnhancedEntityTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.basic.LazyBasicFieldAccessTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.basic.LazyBasicPropertyAccessTestTask;
+import org.hibernate.test.bytecode.enhancement.lazy.cache.LazyInCacheTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.group.LazyGroupAccessTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.group.LazyGroupUpdateTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.group.SimpleLazyGroupUpdateTestTask;
 import org.hibernate.test.bytecode.enhancement.lazyCache.InitFromCacheTestTask;
 import org.hibernate.test.bytecode.enhancement.mapped.MappedSuperclassTestTask;
 import org.hibernate.test.bytecode.enhancement.merge.CompositeMergeTestTask;
 import org.hibernate.test.bytecode.enhancement.ondemandload.LazyCollectionWithClearedSessionTestTask;
 import org.hibernate.test.bytecode.enhancement.ondemandload.LazyCollectionWithClosedSessionTestTask;
 import org.hibernate.test.bytecode.enhancement.ondemandload.LazyEntityLoadingWithClosedSessionTestTask;
 import org.hibernate.test.bytecode.enhancement.otherentityentrycontext.OtherEntityEntryContextTestTask;
 import org.hibernate.test.bytecode.enhancement.pk.EmbeddedPKTestTask;
 import org.junit.Test;
 
 /**
  * @author Luis Barreiro
  */
 public class EnhancerTest extends BaseUnitTestCase {
 
 	@Test
 	public void testBasic() {
 		EnhancerTestUtils.runEnhancerTestTask( BasicEnhancementTestTask.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9529" )
 	public void testFieldHHH9529() {
 		EnhancerTestUtils.runEnhancerTestTask( HHH9529TestTask.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-10851" )
 	public void testAccess() {
 		EnhancerTestUtils.runEnhancerTestTask( MixedAccessTestTask.class );
 	}
 
 	@Test
 	public void testDirty() {
 		EnhancerTestUtils.runEnhancerTestTask( DirtyTrackingTestTask.class );
 	}
 
 	@Test
 	public void testEviction() {
 		EnhancerTestUtils.runEnhancerTestTask( EvictionTestTask.class );
 	}
 
 	@Test
 	public void testOtherPersistenceContext() {
 		EnhancerTestUtils.runEnhancerTestTask( OtherEntityEntryContextTestTask.class );
 	}
 
 	@Test
 	public void testAssociation() {
 		EnhancerTestUtils.runEnhancerTestTask( OneToOneAssociationTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( OneToManyAssociationTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( ManyToManyAssociationTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( InheritedAttributeAssociationTestTask.class );
 	}
 
 	@Test
 	public void testLazy() {
 		EnhancerTestUtils.runEnhancerTestTask( LazyLoadingTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( LazyLoadingIntegrationTestTask.class );
 
 		EnhancerTestUtils.runEnhancerTestTask( LazyBasicPropertyAccessTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( LazyBasicFieldAccessTestTask.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-10922" )
 	public void testLazyProxyOnEnhancedEntity() {
 		EnhancerTestUtils.runEnhancerTestTask( LazyProxyOnEnhancedEntityTestTask.class, new EnhancerTestContext() {
 			@Override
 			public boolean hasLazyLoadableAttributes(UnloadedClass classDescriptor) {
 				return false;
 			}
 		} );
 	}
 
 	@Test
+	@TestForIssue( jiraKey = "HHH-11173" )
+	public void testLazyCache() {
+		EnhancerTestUtils.runEnhancerTestTask( LazyInCacheTestTask.class );
+	}
+
+	@Test
 	@TestForIssue( jiraKey = "HHH-10252" )
 	public void testCascadeDelete() {
 		EnhancerTestUtils.runEnhancerTestTask( CascadeDeleteTestTask.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-10055" )
 	public void testLazyCollectionHandling() {
 		EnhancerTestUtils.runEnhancerTestTask( LazyCollectionLoadingTestTask.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-10267" )
 	public void testLazyGroups() {
 		EnhancerTestUtils.runEnhancerTestTask( LazyGroupAccessTestTask.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-11155" )
 	public void testLazyGroupsUpdate() {
 		EnhancerTestUtils.runEnhancerTestTask( LazyGroupUpdateTestTask.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-11155" )
 	public void testLazyGroupsUpdateSimple() {
 		EnhancerTestUtils.runEnhancerTestTask( SimpleLazyGroupUpdateTestTask.class );
 	}
 
 	@Test
 	public void testLazyCollectionNoTransactionHandling() {
 		EnhancerTestUtils.runEnhancerTestTask( LazyCollectionNoTransactionLoadingTestTask.class );
 	}
 
 	@Test(timeout = 10000)
 	@TestForIssue( jiraKey = "HHH-10055" )
 	@FailureExpected( jiraKey = "HHH-10055" )
 	public void testOnDemand() {
 		EnhancerTestUtils.runEnhancerTestTask( LazyCollectionWithClearedSessionTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( LazyCollectionWithClosedSessionTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( LazyEntityLoadingWithClosedSessionTestTask.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-10708" )
 	public void testLazyUnexpectedDelete() {
 		EnhancerTestUtils.runEnhancerTestTask( UnexpectedDeleteOneTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( UnexpectedDeleteTwoTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( UnexpectedDeleteThreeTestTask.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-10646" )
 	public void testMappedSuperclass() {
 		EnhancerTestUtils.runEnhancerTestTask( MappedSuperclassTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( MappedSuperclassTestTask.class, new EnhancerTestContext() {
 			@Override
 			public boolean hasLazyLoadableAttributes(UnloadedClass classDescriptor) {
 				// HHH-10981 - Without lazy loading, the generation of getters and setters has a different code path
 				return false;
 			}
 		} );
 	}
 
 	@Test
 	public void testMerge() {
 		EnhancerTestUtils.runEnhancerTestTask( CompositeMergeTestTask.class );
 	}
 
 	@Test
 	public void testEmbeddedPK() {
 		EnhancerTestUtils.runEnhancerTestTask( EmbeddedPKTestTask.class );
 	}
 
 	@Test
 	public void testExtendedEnhancement() {
 		EnhancerTestUtils.runEnhancerTestTask( ExtendedEnhancementTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( ExtendedAssociationManagementTestTasK.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-3949" )
 	@FailureExpected( jiraKey = "HHH-3949" )
 	public void testJoinFetchLazyToOneAttributeHql() {
 		EnhancerTestUtils.runEnhancerTestTask( HHH3949TestTask1.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-3949" )
 	@FailureExpected( jiraKey = "HHH-3949" )
 	public void testJoinFetchLazyToOneAttributeHql2() {
 		EnhancerTestUtils.runEnhancerTestTask( HHH3949TestTask2.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-3949" )
 	@FailureExpected( jiraKey = "HHH-3949" )
 	public void testHHH3949() {
 		EnhancerTestUtils.runEnhancerTestTask( HHH3949TestTask3.class );
 		EnhancerTestUtils.runEnhancerTestTask( HHH3949TestTask4.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9937")
 	public void testLazyBasicFieldNotInitialized() {
 		EnhancerTestUtils.runEnhancerTestTask( LazyBasicFieldNotInitializedTestTask.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-11293")
 	public void testDirtyCollection() {
 		EnhancerTestUtils.runEnhancerTestTask( DirtyTrackingCollectionTestTask.class );
 	}
 
 	@Test
 	public void testInitFromCache() {
 		EnhancerTestUtils.runEnhancerTestTask( InitFromCacheTestTask.class );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/cache/LazyInCacheTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/cache/LazyInCacheTestTask.java
new file mode 100644
index 0000000000..38589e05f5
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/cache/LazyInCacheTestTask.java
@@ -0,0 +1,111 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.bytecode.enhancement.lazy.cache;
+
+import org.hibernate.annotations.Cache;
+import org.hibernate.annotations.CacheConcurrencyStrategy;
+import org.hibernate.annotations.Type;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.cfg.Environment;
+import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
+import org.junit.Assert;
+
+import javax.persistence.Basic;
+import javax.persistence.Cacheable;
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.EntityManager;
+import javax.persistence.FetchType;
+import javax.persistence.GeneratedValue;
+import javax.persistence.GenerationType;
+import javax.persistence.Id;
+import javax.persistence.OneToMany;
+import java.util.ArrayList;
+import java.util.List;
+
+/**
+ * @author Luis Barreiro
+ */
+public class LazyInCacheTestTask extends AbstractEnhancerTestTask {
+
+	public Class<?>[] getAnnotatedClasses() {
+		return new Class<?>[]{Order.class, Product.class, Tag.class};
+	}
+
+	public void prepare() {
+		Configuration cfg = new Configuration();
+		cfg.setProperty( Environment.ENABLE_LAZY_LOAD_NO_TRANS, "true" );
+		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
+		prepare( cfg );
+	}
+
+	public void execute() {
+		EntityManager entityManager = getFactory().createEntityManager();
+		Order order = new Order();
+		Product product = new Product();
+		order.products.add( product );
+		order.data = "some data".getBytes();
+		entityManager.getTransaction().begin();
+		entityManager.persist( product );
+		entityManager.persist( order );
+		entityManager.getTransaction().commit();
+
+		long orderId = order.id;
+
+		entityManager = getFactory().createEntityManager();
+		order = entityManager.find( Order.class, orderId );
+		Assert.assertEquals( 1, order.products.size() );
+		entityManager.close();
+
+	}
+
+	protected void cleanup() {
+	}
+
+	@Entity
+	@Cache( usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE )
+	public static class Order {
+
+		@Id
+		@GeneratedValue( strategy = GenerationType.IDENTITY )
+		long id;
+
+		@OneToMany
+		List<Product> products = new ArrayList<>();
+
+		@OneToMany
+		List<Tag> tags = new ArrayList<>();
+
+		@Basic( fetch = FetchType.LAZY )
+		@Column
+		@Type( type = "org.hibernate.type.BinaryType" )
+		private byte[] data;
+
+	}
+
+	@Entity
+	public static class Product {
+
+		@Id
+		@GeneratedValue( strategy = GenerationType.IDENTITY )
+		long id;
+
+		String name;
+
+	}
+
+	@Entity
+	public class Tag {
+
+		@Id
+		@GeneratedValue( strategy = GenerationType.IDENTITY )
+		long id;
+
+		String name;
+
+	}
+}
