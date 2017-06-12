diff --git a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
index dbdab397b7..bbfdda0f99 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
@@ -1,800 +1,820 @@
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
-		else if ( !Hibernate.isInitialized( value ) ) {
+
+		if ( !getReturnedClass().isInstance( value ) && !PersistentCollection.class.isInstance( value ) ) {
+			// its most likely the collection-key
+			final CollectionPersister persister = getPersister( factory );
+			if ( persister.getKeyType().getReturnedClass().isInstance( value ) ) {
+				return getRole() + "#" + getPersister( factory ).getKeyType().toLoggableString( value, factory );
+			}
+			else {
+				// although it could also be the collection-id
+				if ( persister.getIdentifierType() != null
+						&& persister.getIdentifierType().getReturnedClass().isInstance( value ) ) {
+					return getRole() + "#" + getPersister( factory ).getIdentifierType().toLoggableString( value, factory );
+				}
+			}
+		}
+
+		if ( !Hibernate.isInitialized( value ) ) {
 			return "<uninitialized>";
 		}
 		else {
 			return renderLoggableString( value, factory );
 		}
 	}
 
 	protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		final List<String> list = new ArrayList<String>();
 		Type elemType = getElementType( factory );
 		Iterator itr = getElementsIterator( value );
 		while ( itr.hasNext() ) {
 			list.add( elemType.toLoggableString( itr.next(), factory ) );
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
-		return session.getFactory().getMetamodel().collectionPersister( role );
+		return getPersister( session.getFactory() );
+	}
+
+	private CollectionPersister getPersister(SessionFactoryImplementor factory) {
+		return factory.getMetamodel().collectionPersister( role );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
index 84c0e012a4..262bd6e09a 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
@@ -1,697 +1,704 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.proxy.HibernateProxy;
 
 /**
  * Base for types which map associations to persistent entities.
  *
  * @author Gavin King
  */
 public abstract class EntityType extends AbstractType implements AssociationType {
 
 	private final TypeFactory.TypeScope scope;
 	private final String associatedEntityName;
 	protected final String uniqueKeyPropertyName;
 	private final boolean eager;
 	private final boolean unwrapProxy;
 	private final boolean referenceToPrimaryKey;
 
 	/**
 	 * Cached because of performance
 	 *
 	 * @see #getIdentifierType
 	 * @see #getIdentifierType
 	 */
 	private transient volatile Type associatedIdentifierType;
 
 	/**
 	 * Cached because of performance
 	 *
 	 * @see #getAssociatedEntityPersister
 	 */
 	private transient volatile EntityPersister associatedEntityPersister;
 
 	private transient Class returnedClass;
 
 	/**
 	 * Constructs the requested entity type mapping.
 	 *
 	 * @param scope The type scope
 	 * @param entityName The name of the associated entity.
 	 * @param uniqueKeyPropertyName The property-ref name, or null if we
 	 * reference the PK of the associated entity.
 	 * @param eager Is eager fetching enabled.
 	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
 	 * says to return the "implementation target" of lazy prooxies; typically only possible
 	 * with lazy="no-proxy".
 	 *
 	 * @deprecated Use {@link #EntityType(org.hibernate.type.TypeFactory.TypeScope, String, boolean, String, boolean, boolean)} instead.
 	 */
 	@Deprecated
 	protected EntityType(
 			TypeFactory.TypeScope scope,
 			String entityName,
 			String uniqueKeyPropertyName,
 			boolean eager,
 			boolean unwrapProxy) {
 		this( scope, entityName, uniqueKeyPropertyName == null, uniqueKeyPropertyName, eager, unwrapProxy );
 	}
 
 	/**
 	 * Constructs the requested entity type mapping.
 	 *
 	 * @param scope The type scope
 	 * @param entityName The name of the associated entity.
 	 * @param referenceToPrimaryKey True if association references a primary key.
 	 * @param uniqueKeyPropertyName The property-ref name, or null if we
 	 * reference the PK of the associated entity.
 	 * @param eager Is eager fetching enabled.
 	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
 	 * says to return the "implementation target" of lazy prooxies; typically only possible
 	 * with lazy="no-proxy".
 	 */
 	protected EntityType(
 			TypeFactory.TypeScope scope,
 			String entityName,
 			boolean referenceToPrimaryKey,
 			String uniqueKeyPropertyName,
 			boolean eager,
 			boolean unwrapProxy) {
 		this.scope = scope;
 		this.associatedEntityName = entityName;
 		this.uniqueKeyPropertyName = uniqueKeyPropertyName;
 		this.eager = eager;
 		this.unwrapProxy = unwrapProxy;
 		this.referenceToPrimaryKey = referenceToPrimaryKey;
 	}
 
 	protected TypeFactory.TypeScope scope() {
 		return scope;
 	}
 
 	/**
 	 * An entity type is a type of association type
 	 *
 	 * @return True.
 	 */
 	@Override
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	/**
 	 * Explicitly, an entity type is an entity type ;)
 	 *
 	 * @return True.
 	 */
 	@Override
 	public final boolean isEntityType() {
 		return true;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	/**
 	 * Generates a string representation of this type.
 	 *
 	 * @return string rep
 	 */
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getAssociatedEntityName() + ')';
 	}
 
 	/**
 	 * For entity types, the name correlates to the associated entity name.
 	 */
 	@Override
 	public String getName() {
 		return associatedEntityName;
 	}
 
 	/**
 	 * Does this association foreign key reference the primary key of the other table?
 	 * Otherwise, it references a property-ref.
 	 *
 	 * @return True if this association reference the PK of the associated entity.
 	 */
 	public boolean isReferenceToPrimaryKey() {
 		return referenceToPrimaryKey;
 	}
 
 	@Override
 	public String getRHSUniqueKeyPropertyName() {
 		// Return null if this type references a PK.  This is important for
 		// associations' use of mappedBy referring to a derived ID.
 		return referenceToPrimaryKey ? null : uniqueKeyPropertyName;
 	}
 
 	@Override
 	public String getLHSPropertyName() {
 		return null;
 	}
 
 	public String getPropertyName() {
 		return null;
 	}
 
 	/**
 	 * The name of the associated entity.
 	 *
 	 * @return The associated entity name.
 	 */
 	public final String getAssociatedEntityName() {
 		return associatedEntityName;
 	}
 
 	/**
 	 * The name of the associated entity.
 	 *
 	 * @param factory The session factory, for resolution.
 	 *
 	 * @return The associated entity name.
 	 */
 	@Override
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
 		return getAssociatedEntityName();
 	}
 
 	/**
 	 * Retrieves the {@link Joinable} defining the associated entity.
 	 *
 	 * @param factory The session factory.
 	 *
 	 * @return The associated joinable
 	 *
 	 * @throws MappingException Generally indicates an invalid entity name.
 	 */
 	@Override
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) throws MappingException {
 		return (Joinable) getAssociatedEntityPersister( factory );
 	}
 
 	/**
 	 * This returns the wrong class for an entity with a proxy, or for a named
 	 * entity.  Theoretically it should return the proxy class, but it doesn't.
 	 * <p/>
 	 * The problem here is that we do not necessarily have a ref to the associated
 	 * entity persister (nor to the session factory, to look it up) which is really
 	 * needed to "do the right thing" here...
 	 *
 	 * @return The entiyt class.
 	 */
 	@Override
 	public final Class getReturnedClass() {
 		if ( returnedClass == null ) {
 			returnedClass = determineAssociatedEntityClass();
 		}
 		return returnedClass;
 	}
 
 	private Class determineAssociatedEntityClass() {
 		final String entityName = getAssociatedEntityName();
 		try {
 			return ReflectHelper.classForName( entityName );
 		}
 		catch (ClassNotFoundException cnfe) {
 			return this.scope.resolveFactory().getMetamodel().entityPersister( entityName ).
 					getEntityTuplizer().getMappedClass();
 		}
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	@Override
 	public final Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SharedSessionContractImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		return resolve( hydrate( rs, names, session, owner ), session, owner );
 	}
 
 	/**
 	 * Two entities are considered the same when their instances are the same.
 	 *
 	 * @param x One entity instance
 	 * @param y Another entity instance
 	 *
 	 * @return True if x == y; false otherwise.
 	 */
 	@Override
 	public final boolean isSame(Object x, Object y) {
 		return x == y;
 	}
 
 	@Override
 	public int compare(Object x, Object y) {
 		return 0; //TODO: entities CAN be compared, by PK, fix this! -> only if/when we can extract the id values....
 	}
 
 	@Override
 	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return value; //special case ... this is the leaf of the containment graph, even though not immutable
 	}
 
 	@Override
 	public Object replace(
 			Object original,
 			Object target,
 			SharedSessionContractImplementor session,
 			Object owner,
 			Map copyCache) throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		Object cached = copyCache.get( original );
 		if ( cached == null ) {
 			// Avoid creation of invalid managed -> managed mapping in copyCache when traversing
 			// cascade loop (@OneToMany(cascade=ALL) with associated @ManyToOne(cascade=ALL)) in entity graph
 			if ( copyCache.containsValue( original ) ) {
 				cached = original;
 			}
 		}
 		if ( cached != null ) {
 			return cached;
 		}
 		else {
 			if ( original == target ) {
 				return target;
 			}
 			if ( session.getContextEntityIdentifier( original ) == null &&
 					ForeignKeys.isTransient( associatedEntityName, original, Boolean.FALSE, session ) ) {
 				final Object copy = session.getEntityPersister( associatedEntityName, original )
 						.instantiate( null, session );
 				copyCache.put( original, copy );
 				return copy;
 			}
 			else {
 				Object id = getIdentifier( original, session );
 				if ( id == null ) {
 					throw new AssertionFailure(
 							"non-transient entity has a null id: " + original.getClass()
 									.getName()
 					);
 				}
 				id = getIdentifierOrUniqueKeyType( session.getFactory() )
 						.replace( id, null, session, owner, copyCache );
 				return resolve( id, session, owner );
 			}
 		}
 	}
 
 	@Override
 	public int getHashCode(Object x, SessionFactoryImplementor factory) {
 		EntityPersister persister = getAssociatedEntityPersister( factory );
 		if ( !persister.canExtractIdOutOfEntity() ) {
 			return super.getHashCode( x );
 		}
 
 		final Serializable id;
 		if ( x instanceof HibernateProxy ) {
 			id = ( (HibernateProxy) x ).getHibernateLazyInitializer().getIdentifier();
 		}
 		else {
 			final Class mappedClass = persister.getMappedClass();
 			if ( mappedClass.isAssignableFrom( x.getClass() ) ) {
 				id = persister.getIdentifier( x );
 			}
 			else {
 				id = (Serializable) x;
 			}
 		}
 		return persister.getIdentifierType().getHashCode( id, factory );
 	}
 
 	@Override
 	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
 		// associations (many-to-one and one-to-one) can be null...
 		if ( x == null || y == null ) {
 			return x == y;
 		}
 
 		EntityPersister persister = getAssociatedEntityPersister( factory );
 		if ( !persister.canExtractIdOutOfEntity() ) {
 			return super.isEqual( x, y );
 		}
 
 		final Class mappedClass = persister.getMappedClass();
 		Serializable xid;
 		if ( x instanceof HibernateProxy ) {
 			xid = ( (HibernateProxy) x ).getHibernateLazyInitializer()
 					.getIdentifier();
 		}
 		else {
 			if ( mappedClass.isAssignableFrom( x.getClass() ) ) {
 				xid = persister.getIdentifier( x );
 			}
 			else {
 				//JPA 2 case where @IdClass contains the id and not the associated entity
 				xid = (Serializable) x;
 			}
 		}
 
 		Serializable yid;
 		if ( y instanceof HibernateProxy ) {
 			yid = ( (HibernateProxy) y ).getHibernateLazyInitializer()
 					.getIdentifier();
 		}
 		else {
 			if ( mappedClass.isAssignableFrom( y.getClass() ) ) {
 				yid = persister.getIdentifier( y );
 			}
 			else {
 				//JPA 2 case where @IdClass contains the id and not the associated entity
 				yid = (Serializable) y;
 			}
 		}
 
 		return persister.getIdentifierType()
 				.isEqual( xid, yid, factory );
 	}
 
 	@Override
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
 		return getOnCondition( alias, factory, enabledFilters, null );
 	}
 
 	@Override
 	public String getOnCondition(
 			String alias,
 			SessionFactoryImplementor factory,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		if ( isReferenceToPrimaryKey() && ( treatAsDeclarations == null || treatAsDeclarations.isEmpty() ) ) {
 			return "";
 		}
 		else {
 			return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters, treatAsDeclarations );
 		}
 	}
 
 	/**
 	 * Resolve an identifier or unique key value
 	 */
 	@Override
 	public Object resolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
 		if ( value != null && !isNull( owner, session ) ) {
 			if ( isReferenceToPrimaryKey() ) {
 				return resolveIdentifier( (Serializable) value, session );
 			}
 			else if ( uniqueKeyPropertyName != null ) {
 				return loadByUniqueKey( getAssociatedEntityName(), uniqueKeyPropertyName, value, session );
 			}
 		}
 
 		return null;
 	}
 
 	@Override
 	public Type getSemiResolvedType(SessionFactoryImplementor factory) {
 		return getAssociatedEntityPersister( factory ).getIdentifierType();
 	}
 
 	protected EntityPersister getAssociatedEntityPersister(final SessionFactoryImplementor factory) {
 		final EntityPersister persister = associatedEntityPersister;
 		//The following branch implements a simple lazy-initialization, but rather than the canonical
 		//form it returns the local variable to avoid a second volatile read: associatedEntityPersister
 		//needs to be volatile as the initialization might happen by a different thread than the readers.
 		if ( persister == null ) {
 			associatedEntityPersister = factory.getMetamodel().entityPersister( getAssociatedEntityName() );
 			return associatedEntityPersister;
 		}
 		else {
 			return persister;
 		}
 	}
 
 	protected final Object getIdentifier(Object value, SharedSessionContractImplementor session) throws HibernateException {
 		if ( isReferenceToPrimaryKey() || uniqueKeyPropertyName == null ) {
 			return ForeignKeys.getEntityIdentifierIfNotUnsaved(
 					getAssociatedEntityName(),
 					value,
 					session
 			); //tolerates nulls
 		}
 		else if ( value == null ) {
 			return null;
 		}
 		else {
 			EntityPersister entityPersister = getAssociatedEntityPersister( session.getFactory() );
 			Object propertyValue = entityPersister.getPropertyValue( value, uniqueKeyPropertyName );
 			// We now have the value of the property-ref we reference.  However,
 			// we need to dig a little deeper, as that property might also be
 			// an entity type, in which case we need to resolve its identitifier
 			Type type = entityPersister.getPropertyType( uniqueKeyPropertyName );
 			if ( type.isEntityType() ) {
 				propertyValue = ( (EntityType) type ).getIdentifier( propertyValue, session );
 			}
 
 			return propertyValue;
 		}
 	}
 
 	/**
 	 * Generate a loggable representation of an instance of the value mapped by this type.
 	 *
 	 * @param value The instance to be logged.
 	 * @param factory The session factory.
 	 *
 	 * @return The loggable string.
 	 *
 	 * @throws HibernateException Generally some form of resolution problem.
 	 */
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) {
 		if ( value == null ) {
 			return "null";
 		}
 
-		EntityPersister persister = getAssociatedEntityPersister( factory );
-		StringBuilder result = new StringBuilder().append( associatedEntityName );
+		final EntityPersister persister = getAssociatedEntityPersister( factory );
+		if ( !persister.getEntityTuplizer().isInstance( value ) ) {
+			// it should be the id type...
+			if ( persister.getIdentifierType().getReturnedClass().isInstance( value ) ) {
+				return associatedEntityName + "#" + value;
+			}
+		}
+
+		final StringBuilder result = new StringBuilder().append( associatedEntityName );
 
 		if ( persister.hasIdentifierProperty() ) {
 			final Serializable id;
 			if ( value instanceof HibernateProxy ) {
 				HibernateProxy proxy = (HibernateProxy) value;
 				id = proxy.getHibernateLazyInitializer().getIdentifier();
 			}
 			else {
 				id = persister.getIdentifier( value );
 			}
 
 			result.append( '#' )
 					.append( persister.getIdentifierType().toLoggableString( id, factory ) );
 		}
 
 		return result.toString();
 	}
 
 	/**
 	 * Is the association modeled here defined as a 1-1 in the database (physical model)?
 	 *
 	 * @return True if a 1-1 in the database; false otherwise.
 	 */
 	public abstract boolean isOneToOne();
 
 	/**
 	 * Is the association modeled here a 1-1 according to the logical moidel?
 	 *
 	 * @return True if a 1-1 in the logical model; false otherwise.
 	 */
 	public boolean isLogicalOneToOne() {
 		return isOneToOne();
 	}
 
 	/**
 	 * Convenience method to locate the identifier type of the associated entity.
 	 *
 	 * @param factory The mappings...
 	 *
 	 * @return The identifier type
 	 */
 	Type getIdentifierType(final Mapping factory) {
 		final Type type = associatedIdentifierType;
 		//The following branch implements a simple lazy-initialization, but rather than the canonical
 		//form it returns the local variable to avoid a second volatile read: associatedIdentifierType
 		//needs to be volatile as the initialization might happen by a different thread than the readers.
 		if ( type == null ) {
 			associatedIdentifierType = factory.getIdentifierType( getAssociatedEntityName() );
 			return associatedIdentifierType;
 		}
 		else {
 			return type;
 		}
 	}
 
 	/**
 	 * Convenience method to locate the identifier type of the associated entity.
 	 *
 	 * @param session The originating session
 	 *
 	 * @return The identifier type
 	 */
 	Type getIdentifierType(final SharedSessionContractImplementor session) {
 		final Type type = associatedIdentifierType;
 		if ( type == null ) {
 			associatedIdentifierType = getIdentifierType( session.getFactory() );
 			return associatedIdentifierType;
 		}
 		else {
 			return type;
 		}
 	}
 
 	/**
 	 * Determine the type of either (1) the identifier if we reference the
 	 * associated entity's PK or (2) the unique key to which we refer (i.e.
 	 * the property-ref).
 	 *
 	 * @param factory The mappings...
 	 *
 	 * @return The appropriate type.
 	 *
 	 * @throws MappingException Generally, if unable to resolve the associated entity name
 	 * or unique key property name.
 	 */
 	public final Type getIdentifierOrUniqueKeyType(Mapping factory) throws MappingException {
 		if ( isReferenceToPrimaryKey() || uniqueKeyPropertyName == null ) {
 			return getIdentifierType( factory );
 		}
 		else {
 			Type type = factory.getReferencedPropertyType( getAssociatedEntityName(), uniqueKeyPropertyName );
 			if ( type.isEntityType() ) {
 				type = ( (EntityType) type ).getIdentifierOrUniqueKeyType( factory );
 			}
 			return type;
 		}
 	}
 
 	/**
 	 * The name of the property on the associated entity to which our FK
 	 * refers
 	 *
 	 * @param factory The mappings...
 	 *
 	 * @return The appropriate property name.
 	 *
 	 * @throws MappingException Generally, if unable to resolve the associated entity name
 	 */
 	public final String getIdentifierOrUniqueKeyPropertyName(Mapping factory)
 			throws MappingException {
 		if ( isReferenceToPrimaryKey() || uniqueKeyPropertyName == null ) {
 			return factory.getIdentifierPropertyName( getAssociatedEntityName() );
 		}
 		else {
 			return uniqueKeyPropertyName;
 		}
 	}
 
 	protected abstract boolean isNullable();
 
 	/**
 	 * Resolve an identifier via a load.
 	 *
 	 * @param id The entity id to resolve
 	 * @param session The orginating session.
 	 *
 	 * @return The resolved identifier (i.e., loaded entity).
 	 *
 	 * @throws org.hibernate.HibernateException Indicates problems performing the load.
 	 */
 	protected final Object resolveIdentifier(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
 		boolean isProxyUnwrapEnabled = unwrapProxy &&
 				getAssociatedEntityPersister( session.getFactory() )
 						.isInstrumented();
 
 		Object proxyOrEntity = session.internalLoad(
 				getAssociatedEntityName(),
 				id,
 				eager,
 				isNullable() && !isProxyUnwrapEnabled
 		);
 
 		if ( proxyOrEntity instanceof HibernateProxy ) {
 			( (HibernateProxy) proxyOrEntity ).getHibernateLazyInitializer()
 					.setUnwrap( isProxyUnwrapEnabled );
 		}
 
 		return proxyOrEntity;
 	}
 
 	protected boolean isNull(Object owner, SharedSessionContractImplementor session) {
 		return false;
 	}
 
 	/**
 	 * Load an instance by a unique key that is not the primary key.
 	 *
 	 * @param entityName The name of the entity to load
 	 * @param uniqueKeyPropertyName The name of the property defining the uniqie key.
 	 * @param key The unique key property value.
 	 * @param session The originating session.
 	 *
 	 * @return The loaded entity
 	 *
 	 * @throws HibernateException generally indicates problems performing the load.
 	 */
 	public Object loadByUniqueKey(
 			String entityName,
 			String uniqueKeyPropertyName,
 			Object key,
 			SharedSessionContractImplementor session) throws HibernateException {
 		final SessionFactoryImplementor factory = session.getFactory();
 		UniqueKeyLoadable persister = (UniqueKeyLoadable) factory.getMetamodel().entityPersister( entityName );
 
 		//TODO: implement caching?! proxies?!
 
 		EntityUniqueKey euk = new EntityUniqueKey(
 				entityName,
 				uniqueKeyPropertyName,
 				key,
 				getIdentifierOrUniqueKeyType( factory ),
 				persister.getEntityMode(),
 				session.getFactory()
 		);
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		Object result = persistenceContext.getEntity( euk );
 		if ( result == null ) {
 			result = persister.loadByUniqueKey( uniqueKeyPropertyName, key, session );
 		}
 		return result == null ? null : persistenceContext.proxyFor( result );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BlobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BlobTypeDescriptor.java
index 328a247ffa..5de59fbff6 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BlobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BlobTypeDescriptor.java
@@ -1,170 +1,170 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type.descriptor.java;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Serializable;
 import java.sql.Blob;
 import java.sql.SQLException;
 import java.util.Comparator;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.jdbc.BinaryStream;
 import org.hibernate.engine.jdbc.BlobImplementer;
 import org.hibernate.engine.jdbc.BlobProxy;
 import org.hibernate.engine.jdbc.WrappedBlob;
 import org.hibernate.engine.jdbc.internal.BinaryStreamImpl;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
  * Descriptor for {@link Blob} handling.
  * <p/>
  * Note, {@link Blob blobs} really are mutable (their internal state can in fact be mutated).  We simply
  * treat them as immutable because we cannot properly check them for changes nor deep copy them.
  *
  * @author Steve Ebersole
  * @author Brett Meyer
  */
 public class BlobTypeDescriptor extends AbstractTypeDescriptor<Blob> {
 	public static final BlobTypeDescriptor INSTANCE = new BlobTypeDescriptor();
 
 	public static class BlobMutabilityPlan implements MutabilityPlan<Blob> {
 		public static final BlobMutabilityPlan INSTANCE = new BlobMutabilityPlan();
 
 		@Override
 		public boolean isMutable() {
 			return false;
 		}
 
 		@Override
 		public Blob deepCopy(Blob value) {
 			return value;
 		}
 
 		@Override
 		public Serializable disassemble(Blob value) {
 			throw new UnsupportedOperationException( "Blobs are not cacheable" );
 		}
 
 		@Override
 		public Blob assemble(Serializable cached) {
 			throw new UnsupportedOperationException( "Blobs are not cacheable" );
 		}
 	}
 
 	public BlobTypeDescriptor() {
 		super( Blob.class, BlobMutabilityPlan.INSTANCE );
 	}
 
 	@Override
 	public String extractLoggableRepresentation(Blob value) {
-		return value == null ? "null" : "BLOB{...}";
+		return value == null ? "null" : "{blob}";
 	}
 
 	@Override
 	public String toString(Blob value) {
 		final byte[] bytes;
 		try {
 			bytes = DataHelper.extractBytes( value.getBinaryStream() );
 		}
 		catch ( SQLException e ) {
 			throw new HibernateException( "Unable to access blob stream", e );
 		}
 		return PrimitiveByteArrayTypeDescriptor.INSTANCE.toString( bytes );
 	}
 
 	@Override
 	public Blob fromString(String string) {
 		return BlobProxy.generateProxy( PrimitiveByteArrayTypeDescriptor.INSTANCE.fromString( string ) );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public Comparator<Blob> getComparator() {
 		return IncomparableComparator.INSTANCE;
 	}
 
 	@Override
 	public int extractHashCode(Blob value) {
 		return System.identityHashCode( value );
 	}
 
 	@Override
 	public boolean areEqual(Blob one, Blob another) {
 		return one == another;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(Blob value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 
 		try {
 			if ( BinaryStream.class.isAssignableFrom( type ) ) {
 				if ( BlobImplementer.class.isInstance( value ) ) {
 					// if the incoming Blob is a wrapper, just pass along its BinaryStream
 					return (X) ( (BlobImplementer) value ).getUnderlyingStream();
 				}
 				else {
 					// otherwise we need to build a BinaryStream...
 					return (X) new BinaryStreamImpl( DataHelper.extractBytes( value.getBinaryStream() ) );
 				}
 			}
 			else if ( byte[].class.isAssignableFrom( type )) {
 				if ( BlobImplementer.class.isInstance( value ) ) {
 					// if the incoming Blob is a wrapper, just grab the bytes from its BinaryStream
 					return (X) ( (BlobImplementer) value ).getUnderlyingStream().getBytes();
 				}
 				else {
 					// otherwise extract the bytes from the stream manually
 					return (X) DataHelper.extractBytes( value.getBinaryStream() );
 				}
 			}
 			else if (Blob.class.isAssignableFrom( type )) {
 				final Blob blob =  WrappedBlob.class.isInstance( value )
 						? ( (WrappedBlob) value ).getWrappedBlob()
 						: value;
 				return (X) blob;
 			}
 		}
 		catch ( SQLException e ) {
 			throw new HibernateException( "Unable to access blob stream", e );
 		}
 		
 		throw unknownUnwrap( type );
 	}
 
 	@Override
 	public <X> Blob wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 
 		// Support multiple return types from
 		// org.hibernate.type.descriptor.sql.BlobTypeDescriptor
 		if ( Blob.class.isAssignableFrom( value.getClass() ) ) {
 			return options.getLobCreator().wrap( (Blob) value );
 		}
 		else if ( byte[].class.isAssignableFrom( value.getClass() ) ) {
 			return options.getLobCreator().createBlob( ( byte[] ) value);
 		}
 		else if ( InputStream.class.isAssignableFrom( value.getClass() ) ) {
 			InputStream inputStream = ( InputStream ) value;
 			try {
 				return options.getLobCreator().createBlob( inputStream, inputStream.available() );
 			}
 			catch ( IOException e ) {
 				throw unknownWrap( value.getClass() );
 			}
 		}
 
 		throw unknownWrap( value.getClass() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClobTypeDescriptor.java
index e2f6a61fab..5d97cdd9a2 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClobTypeDescriptor.java
@@ -1,135 +1,135 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type.descriptor.java;
 
 import java.io.Reader;
 import java.io.Serializable;
 import java.sql.Clob;
 import java.sql.SQLException;
 import java.util.Comparator;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.jdbc.CharacterStream;
 import org.hibernate.engine.jdbc.ClobImplementer;
 import org.hibernate.engine.jdbc.ClobProxy;
 import org.hibernate.engine.jdbc.WrappedClob;
 import org.hibernate.engine.jdbc.internal.CharacterStreamImpl;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
  * Descriptor for {@link Clob} handling.
  * <p/>
  * Note, {@link Clob clobs} really are mutable (their internal state can in fact be mutated).  We simply
  * treat them as immutable because we cannot properly check them for changes nor deep copy them.
  *
  * @author Steve Ebersole
  */
 public class ClobTypeDescriptor extends AbstractTypeDescriptor<Clob> {
 	public static final ClobTypeDescriptor INSTANCE = new ClobTypeDescriptor();
 
 	public static class ClobMutabilityPlan implements MutabilityPlan<Clob> {
 		public static final ClobMutabilityPlan INSTANCE = new ClobMutabilityPlan();
 
 		public boolean isMutable() {
 			return false;
 		}
 
 		public Clob deepCopy(Clob value) {
 			return value;
 		}
 
 		public Serializable disassemble(Clob value) {
 			throw new UnsupportedOperationException( "Clobs are not cacheable" );
 		}
 
 		public Clob assemble(Serializable cached) {
 			throw new UnsupportedOperationException( "Clobs are not cacheable" );
 		}
 	}
 
 	public ClobTypeDescriptor() {
 		super( Clob.class, ClobMutabilityPlan.INSTANCE );
 	}
 
 	@Override
 	public String extractLoggableRepresentation(Clob value) {
-		return value == null ? "null" : "CLOB{...}";
+		return value == null ? "null" : "{clob}";
 	}
 
 	public String toString(Clob value) {
 		return DataHelper.extractString( value );
 	}
 
 	public Clob fromString(String string) {
 		return ClobProxy.generateProxy( string );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public Comparator<Clob> getComparator() {
 		return IncomparableComparator.INSTANCE;
 	}
 
 	@Override
 	public int extractHashCode(Clob value) {
 		return System.identityHashCode( value );
 	}
 
 	@Override
 	public boolean areEqual(Clob one, Clob another) {
 		return one == another;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(final Clob value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 
 		try {
 			if ( CharacterStream.class.isAssignableFrom( type ) ) {
 				if ( ClobImplementer.class.isInstance( value ) ) {
 					// if the incoming Clob is a wrapper, just pass along its CharacterStream
 					return (X) ( (ClobImplementer) value ).getUnderlyingStream();
 				}
 				else {
 					// otherwise we need to build a CharacterStream...
 					return (X) new CharacterStreamImpl( DataHelper.extractString( value.getCharacterStream() ) );
 				}
 			}
 			else if (Clob.class.isAssignableFrom( type )) {
 				final Clob clob =  WrappedClob.class.isInstance( value )
 						? ( (WrappedClob) value ).getWrappedClob()
 						: value;
 				return (X) clob;
 			}
 		}
 		catch ( SQLException e ) {
 			throw new HibernateException( "Unable to access clob stream", e );
 		}
 		
 		throw unknownUnwrap( type );
 	}
 
 	public <X> Clob wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 
 		// Support multiple return types from
 		// org.hibernate.type.descriptor.sql.ClobTypeDescriptor
 		if ( Clob.class.isAssignableFrom( value.getClass() ) ) {
 			return options.getLobCreator().wrap( (Clob) value );
 		}
 		else if ( Reader.class.isAssignableFrom( value.getClass() ) ) {
 			Reader reader = (Reader) value;
 			return options.getLobCreator().createClob( DataHelper.extractString( reader ) );
 		}
 
 		throw unknownWrap( value.getClass() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/NClobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/NClobTypeDescriptor.java
index 50107b695e..a706a0dcf1 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/NClobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/NClobTypeDescriptor.java
@@ -1,135 +1,135 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type.descriptor.java;
 
 import java.io.Reader;
 import java.io.Serializable;
 import java.sql.NClob;
 import java.sql.SQLException;
 import java.util.Comparator;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.jdbc.CharacterStream;
 import org.hibernate.engine.jdbc.NClobImplementer;
 import org.hibernate.engine.jdbc.NClobProxy;
 import org.hibernate.engine.jdbc.WrappedNClob;
 import org.hibernate.engine.jdbc.internal.CharacterStreamImpl;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
  * Descriptor for {@link java.sql.NClob} handling.
  * <p/>
  * Note, {@link java.sql.NClob nclobs} really are mutable (their internal state can in fact be mutated).  We simply
  * treat them as immutable because we cannot properly check them for changes nor deep copy them.
  *
  * @author Steve Ebersole
  */
 public class NClobTypeDescriptor extends AbstractTypeDescriptor<NClob> {
 	public static final NClobTypeDescriptor INSTANCE = new NClobTypeDescriptor();
 
 	public static class NClobMutabilityPlan implements MutabilityPlan<NClob> {
 		public static final NClobMutabilityPlan INSTANCE = new NClobMutabilityPlan();
 
 		public boolean isMutable() {
 			return false;
 		}
 
 		public NClob deepCopy(NClob value) {
 			return value;
 		}
 
 		public Serializable disassemble(NClob value) {
 			throw new UnsupportedOperationException( "Clobs are not cacheable" );
 		}
 
 		public NClob assemble(Serializable cached) {
 			throw new UnsupportedOperationException( "Clobs are not cacheable" );
 		}
 	}
 
 	public NClobTypeDescriptor() {
 		super( NClob.class, NClobMutabilityPlan.INSTANCE );
 	}
 
 	@Override
 	public String extractLoggableRepresentation(NClob value) {
-		return value == null ? "null" : "NCLOB{...}";
+		return value == null ? "null" : "{nclob}";
 	}
 
 	public String toString(NClob value) {
 		return DataHelper.extractString( value );
 	}
 
 	public NClob fromString(String string) {
 		return NClobProxy.generateProxy( string );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public Comparator<NClob> getComparator() {
 		return IncomparableComparator.INSTANCE;
 	}
 
 	@Override
 	public int extractHashCode(NClob value) {
 		return System.identityHashCode( value );
 	}
 
 	@Override
 	public boolean areEqual(NClob one, NClob another) {
 		return one == another;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(final NClob value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 
 		try {
 			if ( CharacterStream.class.isAssignableFrom( type ) ) {
 				if ( NClobImplementer.class.isInstance( value ) ) {
 					// if the incoming NClob is a wrapper, just pass along its BinaryStream
 					return (X) ( (NClobImplementer) value ).getUnderlyingStream();
 				}
 				else {
 					// otherwise we need to build a BinaryStream...
 					return (X) new CharacterStreamImpl( DataHelper.extractString( value.getCharacterStream() ) );
 				}
 			}
 			else if (NClob.class.isAssignableFrom( type )) {
 				final NClob nclob =  WrappedNClob.class.isInstance( value )
 						? ( (WrappedNClob) value ).getWrappedNClob()
 						: value;
 				return (X) nclob;
 			}
 		}
 		catch ( SQLException e ) {
 			throw new HibernateException( "Unable to access nclob stream", e );
 		}
 		
 		throw unknownUnwrap( type );
 	}
 
 	public <X> NClob wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 
 		// Support multiple return types from
 		// org.hibernate.type.descriptor.sql.ClobTypeDescriptor
 		if ( NClob.class.isAssignableFrom( value.getClass() ) ) {
 			return options.getLobCreator().wrap( (NClob) value );
 		}
 		else if ( Reader.class.isAssignableFrom( value.getClass() ) ) {
 			Reader reader = (Reader) value;
 			return options.getLobCreator().createNClob( DataHelper.extractString( reader ) );
 		}
 
 		throw unknownWrap( value.getClass() );
 	}
 }
