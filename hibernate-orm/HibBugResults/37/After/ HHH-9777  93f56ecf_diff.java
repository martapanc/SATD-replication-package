diff --git a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
index 7a9bcd25dc..a9ad4a1b7f 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
@@ -1,833 +1,833 @@
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
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
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
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 
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
 	private final boolean isEmbeddedInXML;
 
 	/**
 	 * @deprecated Use {@link #CollectionType(TypeFactory.TypeScope, String, String)} instead
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public CollectionType(TypeFactory.TypeScope typeScope, String role, String foreignKeyPropertyName, boolean isEmbeddedInXML) {
 		this.typeScope = typeScope;
 		this.role = role;
 		this.foreignKeyPropertyName = foreignKeyPropertyName;
 		this.isEmbeddedInXML = isEmbeddedInXML;
 	}
 
 	public CollectionType(TypeFactory.TypeScope typeScope, String role, String foreignKeyPropertyName) {
 		this.typeScope = typeScope;
 		this.role = role;
 		this.foreignKeyPropertyName = foreignKeyPropertyName;
 		this.isEmbeddedInXML = true;
 	}
 
 	@Override
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
 	public abstract PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key);
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner) throws SQLException {
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String[] name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( null, session, owner );
 	}
 
 	@Override
 	public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		//NOOP
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value, int index,
 			SessionImplementor session) throws HibernateException, SQLException {
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
 		else if ( !Hibernate.isInitialized( value ) ) {
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
 	public Iterator getElementsIterator(Object collection, SessionImplementor session) {
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
 
 	@Override
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
 
 	@Override
 	public boolean isDirty(Object old, Object current, SessionImplementor session)
 			throws HibernateException {
 
 		// collections don't dirty an unversioned parent entity
 
 		// TODO: I don't really like this implementation; it would be better if
 		// this was handled by searchForDirtyCollections()
-		return isOwnerVersioned( session ) && super.isDirty( old, current, session );
+		return super.isDirty( old, current, session );
 		// return false;
 
 	}
 
 	@Override
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
 	public Serializable getKeyOfOwner(Object owner, SessionImplementor session) {
 		
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
 	public Serializable getIdOfOwnerOrNull(Serializable key, SessionImplementor session) {
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
 	public Object hydrate(ResultSet rs, String[] name, SessionImplementor session, Object owner) {
 		// can't just return null here, since that would
 		// cause an owning component to become null
 		return NOT_NULL_COLLECTION;
 	}
 
 	@Override
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
 
 	@Override
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
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
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
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
 			SessionImplementor session) {
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
 	 * after we are done populating it.
 	 * @return A newly instantiated collection to be wrapped.
 	 */
 	public abstract Object instantiate(int anticipatedSize);
 
 	@Override
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
 	public Object getCollection(Serializable key, SessionImplementor session, Object owner) {
 
 		CollectionPersister persister = getPersister( session );
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityMode entityMode = persister.getOwnerEntityPersister().getEntityMode();
 
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
 
 	@Override
 	public boolean isXMLElement() {
 		return true;
 	}
 
 	@Override
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return xml;
 	}
 
 	@Override
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
 	@Override
 	public boolean isAlwaysDirtyChecked() {
 		return true; 
 	}
 
 	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/dereferenced/AbstractDereferencedCollectionTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/dereferenced/AbstractDereferencedCollectionTest.java
index 8689bd0a88..35125edca1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/collection/dereferenced/AbstractDereferencedCollectionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/dereferenced/AbstractDereferencedCollectionTest.java
@@ -1,358 +1,361 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.collection.dereferenced;
 
 import org.junit.Test;
 
 import org.hibernate.*;
 import org.hibernate.collection.internal.AbstractPersistentCollection;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import java.lang.InstantiationException;
 import java.util.HashSet;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gail Badner
  */
 public abstract class AbstractDereferencedCollectionTest extends BaseCoreFunctionalTestCase {
 
 	@Test
+	@TestForIssue( jiraKey = "HHH-9777" )
 	public void testMergeNullCollection() {
 		Session s = openSession();
 		s.getTransaction().begin();
 		One one = createOwner();
 		assertNull( one.getManies() );
 		s.save( one );
 		assertNull( one.getManies() );
 		EntityEntry eeOne = getEntityEntry( s, one  );
 		assertNull( eeOne.getLoadedValue( "manies" ) );
 		s.flush();
 		assertNull( one.getManies() );
 		assertNull( eeOne.getLoadedValue( "manies" ) );
 		s.getTransaction().commit();
 		s.close();
 
 		final String role = getCollectionOwnerClass().getName() + ".manies";
 
 		s = openSession();
 		s.getTransaction().begin();
 		one = (One) s.merge( one );
 
 		// after merging, one.getManies() should still be null;
 		// the EntityEntry loaded state should contain a PersistentCollection though.
 
 		assertNull( one.getManies() );
 		eeOne = getEntityEntry( s, one  );
 		AbstractPersistentCollection maniesEEOneStateOrig = (AbstractPersistentCollection) eeOne.getLoadedValue( "manies" );
 		assertNotNull( maniesEEOneStateOrig );
 
 		// Ensure maniesEEOneStateOrig has role, key, and session properly defined (even though one.manies == null)
 		assertEquals( role, maniesEEOneStateOrig.getRole() );
 		assertEquals( one.getId(), maniesEEOneStateOrig.getKey() );
 		assertSame( s, maniesEEOneStateOrig.getSession() );
 
 		// Ensure there is a CollectionEntry for maniesEEOneStateOrig and that the role, persister, and key are set properly.
 		CollectionEntry ceManiesOrig = getCollectionEntry( s, maniesEEOneStateOrig );
 		assertNotNull( ceManiesOrig );
 		assertEquals( role, ceManiesOrig.getRole() );
 		assertSame( sessionFactory().getCollectionPersister( role ), ceManiesOrig.getLoadedPersister() );
 		assertEquals( one.getId(), ceManiesOrig.getKey() );
 
 		s.flush();
 
 		// Ensure the same EntityEntry is being used.
 		assertSame( eeOne, getEntityEntry( s, one ) );
 
 		// Ensure one.getManies() is still null.
 		assertNull( one.getManies() );
 
 		// Ensure CollectionEntry for maniesEEOneStateOrig is no longer in the PersistenceContext.
 		assertNull( getCollectionEntry( s, maniesEEOneStateOrig ) );
 
 		// Ensure the original CollectionEntry has role, persister, and key set to null.
 		assertNull( ceManiesOrig.getRole() );
 		assertNull( ceManiesOrig.getLoadedPersister() );
 		assertNull( ceManiesOrig.getKey() );
 
 		// Ensure the PersistentCollection (that was previously returned by eeOne.getLoadedState())
 		// has key and role set to null.
 		assertNull( maniesEEOneStateOrig.getKey() );
 		assertNull( maniesEEOneStateOrig.getRole() );
 
 		// Ensure eeOne.getLoadedState() returns null for collection after flush.
 		assertNull( eeOne.getLoadedValue( "manies" ) );
 
 		// Ensure the session in maniesEEOneStateOrig has been unset.
 		assertNull( maniesEEOneStateOrig.getSession() );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
+	@TestForIssue( jiraKey = "HHH-9777" )
 	public void testGetAndNullifyCollection() {
 		Session s = openSession();
 		s.getTransaction().begin();
 		One one = createOwner();
 		assertNull( one.getManies() );
 		s.save( one );
 		assertNull( one.getManies() );
 		EntityEntry eeOne = getEntityEntry( s, one  );
 		assertNull( eeOne.getLoadedValue( "manies" ) );
 		s.flush();
 		assertNull( one.getManies() );
 		assertNull( eeOne.getLoadedValue( "manies" ) );
 		s.getTransaction().commit();
 		s.close();
 
 		final String role = getCollectionOwnerClass().getName() + ".manies";
 
 		s = openSession();
 		s.getTransaction().begin();
 		one = (One) s.get( getCollectionOwnerClass(), one.getId() );
 
 		// When returned by Session.get(), one.getManies() will return a PersistentCollection;
 		// the EntityEntry loaded state should contain the same PersistentCollection.
 
 		eeOne = getEntityEntry( s, one  );
 		assertNotNull( one.getManies() );
 		AbstractPersistentCollection maniesEEOneStateOrig = (AbstractPersistentCollection) eeOne.getLoadedValue( "manies" );
 		assertSame( one.getManies(), maniesEEOneStateOrig );
 
 		// Ensure maniesEEOneStateOrig has role, key, and session properly defined (even though one.manies == null)
 		assertEquals( role, maniesEEOneStateOrig.getRole() );
 		assertEquals( one.getId(), maniesEEOneStateOrig.getKey() );
 		assertSame( s, maniesEEOneStateOrig.getSession() );
 
 		// Ensure there is a CollectionEntry for maniesEEOneStateOrig and that the role, persister, and key are set properly.
 		CollectionEntry ceManies = getCollectionEntry( s, maniesEEOneStateOrig );
 		assertNotNull( ceManies );
 		assertEquals( role, ceManies.getRole() );
 		assertSame( sessionFactory().getCollectionPersister( role ), ceManies.getLoadedPersister() );
 		assertEquals( one.getId(), ceManies.getKey() );
 
 		// nullify collection
 		one.setManies( null );
 
 		s.flush();
 
 		// Ensure the same EntityEntry is being used.
 		assertSame( eeOne, getEntityEntry( s, one ) );
 
 		// Ensure one.getManies() is still null.
 		assertNull( one.getManies() );
 
 		// Ensure CollectionEntry for maniesEEOneStateOrig is no longer in the PersistenceContext.
 		assertNull( getCollectionEntry( s, maniesEEOneStateOrig ) );
 
 		// Ensure the original CollectionEntry has role, persister, and key set to null.
 		assertNull( ceManies.getRole() );
 		assertNull( ceManies.getLoadedPersister() );
 		assertNull( ceManies.getKey() );
 
 		// Ensure the PersistentCollection (that was previously returned by eeOne.getLoadedState())
 		// has key and role set to null.
 		assertNull( maniesEEOneStateOrig.getKey() );
 		assertNull( maniesEEOneStateOrig.getRole() );
 
 		// Ensure eeOne.getLoadedState() returns null for collection after flush.
 		assertNull( eeOne.getLoadedValue( "manies" ) );
 
 		// Ensure the session in maniesEEOneStateOrig has been unset.
 		assertNull( maniesEEOneStateOrig.getSession() );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
-	//@FailureExpected( jiraKey = "HHH-9777")
+	@TestForIssue( jiraKey = "HHH-9777" )
 	public void testGetAndReplaceCollection() {
 		Session s = openSession();
 		s.getTransaction().begin();
 		One one = createOwner();
 		assertNull( one.getManies() );
 		s.save( one );
 		assertNull( one.getManies() );
 		EntityEntry eeOne = getEntityEntry( s, one  );
 		assertNull( eeOne.getLoadedValue( "manies" ) );
 		s.flush();
 		assertNull( one.getManies() );
 		assertNull( eeOne.getLoadedValue( "manies" ) );
 		s.getTransaction().commit();
 		s.close();
 
 		final String role = getCollectionOwnerClass().getName() + ".manies";
 
 		s = openSession();
 		s.getTransaction().begin();
 		one = (One) s.get( getCollectionOwnerClass(), one.getId() );
 
 		// When returned by Session.get(), one.getManies() will return a PersistentCollection;
 		// the EntityEntry loaded state should contain the same PersistentCollection.
 
 		eeOne = getEntityEntry( s, one );
 		assertNotNull( one.getManies() );
 		AbstractPersistentCollection maniesEEOneStateOrig = (AbstractPersistentCollection) eeOne.getLoadedValue( "manies" );
 		assertSame( one.getManies(), maniesEEOneStateOrig );
 
 		// Ensure maniesEEOneStateOrig has role, key, and session properly defined (even though one.manies == null)
 		assertEquals( role, maniesEEOneStateOrig.getRole() );
 		assertEquals( one.getId(), maniesEEOneStateOrig.getKey() );
 		assertSame( s, maniesEEOneStateOrig.getSession() );
 
 		// Ensure there is a CollectionEntry for maniesEEOneStateOrig and that the role, persister, and key are set properly.
 		CollectionEntry ceManiesOrig = getCollectionEntry( s, maniesEEOneStateOrig );
 		assertNotNull( ceManiesOrig );
 		assertEquals( role, ceManiesOrig.getRole() );
 		assertSame( sessionFactory().getCollectionPersister( role ), ceManiesOrig.getLoadedPersister() );
 		assertEquals( one.getId(), ceManiesOrig.getKey() );
 
 		// replace collection
 		one.setManies( new HashSet<Many>() );
 
 		s.flush();
 
 		// Ensure the same EntityEntry is being used.
 		assertSame( eeOne, getEntityEntry( s, one ) );
 
 		// Ensure CollectionEntry for maniesEEOneStateOrig is no longer in the PersistenceContext.
 		assertNull( getCollectionEntry( s, maniesEEOneStateOrig ) );
 
 		// Ensure the original CollectionEntry has role, persister, and key set to null.
 		assertNull( ceManiesOrig.getRole() );
 		assertNull( ceManiesOrig.getLoadedPersister() );
 		assertNull( ceManiesOrig.getKey() );
 
 		// Ensure the PersistentCollection (that was previously returned by eeOne.getLoadedState())
 		// has key and role set to null.
 		assertNull( maniesEEOneStateOrig.getKey() );
 		assertNull( maniesEEOneStateOrig.getRole() );
 
 		// one.getManies() should be "wrapped" by a PersistentCollection now; role, key, and session should be set properly.
 		assertTrue( PersistentCollection.class.isInstance( one.getManies() ) );
 		assertEquals( role, ( (PersistentCollection) one.getManies() ).getRole() );
 		assertEquals( one.getId(), ( (PersistentCollection) one.getManies() ).getKey() );
 		assertSame( s, ( (AbstractPersistentCollection) one.getManies() ).getSession() );
 
 		// Ensure eeOne.getLoadedState() contains the new collection.
 		assertSame( one.getManies(), eeOne.getLoadedValue( "manies" ) );
 
 		// Ensure there is a new CollectionEntry for the new collection and that role, persister, and key are set properly.
 		CollectionEntry ceManiesAfterReplace = getCollectionEntry( s, (PersistentCollection) one.getManies() );
 		assertNotNull( ceManiesAfterReplace );
 		assertEquals( role, ceManiesAfterReplace.getRole() );
 		assertSame( sessionFactory().getCollectionPersister( role ), ceManiesAfterReplace.getLoadedPersister() );
 		assertEquals( one.getId(), ceManiesAfterReplace.getKey() );
 
 		// Ensure the session in maniesEEOneStateOrig has been unset.
 		assertNull( maniesEEOneStateOrig.getSession() );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveOrUpdateNullCollection() {
 		Session s = openSession();
 		s.getTransaction().begin();
 		One one = createOwner();
 		assertNull( one.getManies() );
 		s.save( one );
 		assertNull( one.getManies() );
 		EntityEntry eeOne = getEntityEntry( s, one  );
 		assertNull( eeOne.getLoadedValue( "manies" ) );
 		s.flush();
 		assertNull( one.getManies() );
 		assertNull( eeOne.getLoadedValue( "manies" ) );
 		s.getTransaction().commit();
 		s.close();
 
 		final String role = getCollectionOwnerClass().getName() + ".manies";
 
 		s = openSession();
 		s.getTransaction().begin();
 		s.saveOrUpdate( one );
 
 		// Ensure one.getManies() is still null.
 		assertNull( one.getManies() );
 
 		// Ensure the EntityEntry loaded state contains null for the manies collection.
 		eeOne = getEntityEntry( s, one  );
 		assertNull( eeOne.getLoadedValue( "manies" ) );
 
 		s.flush();
 
 		// Ensure one.getManies() is still null.
 		assertNull( one.getManies() );
 
 		// Ensure the same EntityEntry is being used.
 		assertSame( eeOne, getEntityEntry( s, one ) );
 
 		// Ensure the EntityEntry loaded state still contains null for the manies collection.
 		assertNull( eeOne.getLoadedValue( "manies" ) );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	protected abstract Class<?> getCollectionOwnerClass();
 
 	protected final One createOwner() {
 		try {
 			return (One) getCollectionOwnerClass().newInstance();
 		}
 		catch (InstantiationException ex) {
 			throw new RuntimeException( ex );
 		}
 		catch (IllegalAccessException ex) {
 			throw new RuntimeException( ex );
 		}
 	}
 
 	private EntityEntry getEntityEntry(Session s, Object entity) {
 		return ( (SessionImplementor) s ).getPersistenceContext().getEntry( entity );
 	}
 
 	private CollectionEntry getCollectionEntry(Session s, PersistentCollection collection) {
 		return ( (SessionImplementor) s ).getPersistenceContext().getCollectionEntry( collection );
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				getCollectionOwnerClass(),
 				Many.class
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/dereferenced/UnversionedCascadeDereferencedCollectionTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/dereferenced/UnversionedCascadeDereferencedCollectionTest.java
index 66191f4ff6..3b9392a564 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/collection/dereferenced/UnversionedCascadeDereferencedCollectionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/dereferenced/UnversionedCascadeDereferencedCollectionTest.java
@@ -1,60 +1,35 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.collection.dereferenced;
 
-import org.junit.Test;
-
-import org.hibernate.testing.FailureExpected;
-
 /**
  * @author Gail Badner
  */
 public class UnversionedCascadeDereferencedCollectionTest extends AbstractDereferencedCollectionTest {
 
 	@Override
 	protected Class<?> getCollectionOwnerClass() {
 		return UnversionedCascadeOne.class;
 	}
-
-	@Override
-	@Test
-	@FailureExpected(jiraKey = "HHH-9777")
-	public void testMergeNullCollection() {
-		super.testMergeNullCollection();
-	}
-
-	@Override
-	@Test
-	@FailureExpected(jiraKey = "HHH-9777")
-	public void testGetAndNullifyCollection() {
-		super.testGetAndNullifyCollection();
-	}
-
-	@Override
-	@Test
-	@FailureExpected(jiraKey = "HHH-9777")
-	public void testGetAndReplaceCollection() {
-		super.testGetAndReplaceCollection();
-	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/dereferenced/UnversionedNoCascadeDereferencedCollectionTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/dereferenced/UnversionedNoCascadeDereferencedCollectionTest.java
index e6c01a491b..655e36d81e 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/collection/dereferenced/UnversionedNoCascadeDereferencedCollectionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/dereferenced/UnversionedNoCascadeDereferencedCollectionTest.java
@@ -1,60 +1,39 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.collection.dereferenced;
 
 import org.junit.Test;
 
 import org.hibernate.testing.FailureExpected;
 
 /**
  * @author Gail Badner
  */
 public class UnversionedNoCascadeDereferencedCollectionTest extends AbstractDereferencedCollectionTest {
 
 	@Override
 	protected Class<?> getCollectionOwnerClass() {
 		return UnversionedNoCascadeOne.class;
 	}
-
-	@Override
-	@Test
-	@FailureExpected(jiraKey = "HHH-9777")
-	public void testMergeNullCollection() {
-		super.testMergeNullCollection();
-	}
-
-	@Override
-	@Test
-	@FailureExpected(jiraKey = "HHH-9777")
-	public void testGetAndNullifyCollection() {
-		super.testGetAndNullifyCollection();
-	}
-
-	@Override
-	@Test
-	@FailureExpected(jiraKey = "HHH-9777")
-	public void testGetAndReplaceCollection() {
-		super.testGetAndReplaceCollection();
-	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
index 23aa444b52..05d397779f 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
@@ -1,1121 +1,1120 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.legacy;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.sql.Time;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Set;
 import java.util.SortedSet;
 import java.util.TimeZone;
 import java.util.TreeMap;
 import java.util.TreeSet;
 
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.FlushMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LazyInitializationException;
 import org.hibernate.LockMode;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.TransactionException;
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.criterion.Example;
 import org.hibernate.criterion.MatchMode;
 import org.hibernate.criterion.Order;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.DerbyDialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.AbstractHANADialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.InterbaseDialect;
 import org.hibernate.dialect.MckoiDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.PointbaseDialect;
 import org.hibernate.dialect.PostgreSQL81Dialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.dialect.SAPDBDialect;
 import org.hibernate.dialect.Sybase11Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseDialect;
 import org.hibernate.dialect.TeradataDialect;
 import org.hibernate.dialect.TimesTenDialect;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.jdbc.AbstractReturningWork;
 import org.hibernate.jdbc.AbstractWork;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.testing.DialectChecks;
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.RequiresDialect;
 import org.hibernate.testing.RequiresDialectFeature;
 import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.type.StandardBasicTypes;
 import org.jboss.logging.Logger;
 import org.junit.Test;
 
 public class FooBarTest extends LegacyTestCase {
 	private static final Logger log = Logger.getLogger( FooBarTest.class );
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 			"legacy/FooBar.hbm.xml",
 			"legacy/Baz.hbm.xml",
 			"legacy/Qux.hbm.xml",
 			"legacy/Glarch.hbm.xml",
 			"legacy/Fum.hbm.xml",
 			"legacy/Fumm.hbm.xml",
 			"legacy/Fo.hbm.xml",
 			"legacy/One.hbm.xml",
 			"legacy/Many.hbm.xml",
 			"legacy/Immutable.hbm.xml",
 			"legacy/Fee.hbm.xml",
 			"legacy/Vetoer.hbm.xml",
 			"legacy/Holder.hbm.xml",
 			"legacy/Location.hbm.xml",
 			"legacy/Stuff.hbm.xml",
 			"legacy/Container.hbm.xml",
 			"legacy/Simple.hbm.xml",
 			"legacy/XY.hbm.xml"
 		};
 	}
 
 	@Test
-	@FailureExpected(jiraKey = "HHH-9777")
 	public void testSaveOrUpdateCopyAny() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Bar bar = new Bar();
 		One one = new One();
 		bar.setObject(one);
 		s.save(bar);
 		GlarchProxy g = bar.getComponent().getGlarch();
 		bar.getComponent().setGlarch(null);
 		s.delete(g);
 		s.flush();
 		assertTrue( s.contains(one) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Bar bar2 = (Bar) s.merge( bar );
 		s.flush();
 		s.delete(bar2);
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testRefreshProxy() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Glarch g = new Glarch();
 		Serializable gid = s.save(g);
 		s.flush();
 		s.clear();
 		GlarchProxy gp = (GlarchProxy) s.load(Glarch.class, gid);
 		gp.getName(); //force init
 		s.refresh(gp);
 		s.delete(gp);
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@RequiresDialectFeature(
 			value = DialectChecks.SupportsCircularCascadeDeleteCheck.class,
 			comment = "db/dialect does not support circular cascade delete constraints"
 	)
 	public void testOnCascadeDelete() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.subs = new ArrayList();
 		Baz sub = new Baz();
 		sub.superBaz = baz;
 		baz.subs.add(sub);
 		s.save(baz);
 		s.flush();
 		assertTrue( s.createQuery("from Baz").list().size()==2 );
 		s.getTransaction().commit();
 		s.beginTransaction();
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.beginTransaction();
 		assertTrue( s.createQuery("from Baz").list().size()==0 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testRemoveFromIdbag() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setByteBag( new ArrayList() );
 		byte[] bytes = { 12, 13 };
 		baz.getByteBag().add( new byte[] { 10, 45 } );
 		baz.getByteBag().add(bytes);
 		baz.getByteBag().add( new byte[] { 1, 11 } );
 		baz.getByteBag().add( new byte[] { 12 } );
 		s.save(baz);
 		s.flush();
 		baz.getByteBag().remove(bytes);
 		s.flush();
 		baz.getByteBag().add(bytes);
 		s.flush();
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLoad() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Qux q = new Qux();
 		s.save(q);
 		BarProxy b = new Bar();
 		s.save(b);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		q = (Qux) s.load(Qux.class, q.getKey() );
 		b = (BarProxy) s.load( Foo.class, b.getKey() );
 		b.getKey();
 		assertFalse( Hibernate.isInitialized(b) );
 		b.getBarString();
 		assertTrue( Hibernate.isInitialized(b) );
 		BarProxy b2 = (BarProxy) s.load( Bar.class, b.getKey() );
 		Qux q2 = (Qux) s.load( Qux.class, q.getKey() );
 		assertTrue( "loaded same object", q==q2 );
 		assertTrue( "loaded same object", b==b2 );
 		assertTrue( Math.round( b.getFormula() ) == b.getInt() / 2 );
 		s.delete(q2);
 		s.delete( b2 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testJoin() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		foo.setJoinedProp("foo");
 		s.save( foo );
 		s.flush();
 		foo.setJoinedProp("bar");
 		s.flush();
 		String fid = foo.getKey();
 		s.delete( foo );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Foo foo2 = new Foo();
 		foo2.setJoinedProp("foo");
 		s.save(foo2);
 		s.createQuery( "select foo.id from Foo foo where foo.joinedProp = 'foo'" ).list();
 		assertNull( s.get(Foo.class, fid) );
 		s.delete(foo2);
 		s.getTransaction().commit();
 		s.close();
 
 	}
 
 	@Test
 	public void testDereferenceLazyCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setFooSet( new HashSet() );
 		Foo foo = new Foo();
 		baz.getFooSet().add(foo);
 		s.save(foo);
 		s.save(baz);
 		foo.setBytes( "foobar".getBytes() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (Foo) s.get( Foo.class, foo.getKey() );
 		assertTrue( Hibernate.isInitialized( foo.getBytes() ) );
 		assertTrue( foo.getBytes().length==6 );
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertTrue( baz.getFooSet().size()==1 );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getCache().evictCollectionRegion("org.hibernate.test.legacy.Baz.fooSet");
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertFalse( Hibernate.isInitialized( baz.getFooSet() ) );
 		baz.setFooSet(null);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (Foo) s.get( Foo.class, foo.getKey() );
 		assertTrue( foo.getBytes().length==6 );
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertFalse( Hibernate.isInitialized( baz.getFooSet() ) );
 		assertTrue( baz.getFooSet().size()==0 );
 		s.delete(baz);
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testMoveLazyCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Baz baz2 = new Baz();
 		baz.setFooSet( new HashSet() );
 		Foo foo = new Foo();
 		baz.getFooSet().add(foo);
 		s.save(foo);
 		s.save(baz);
 		s.save(baz2);
 		foo.setBytes( "foobar".getBytes() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (Foo) s.get( Foo.class, foo.getKey() );
 		assertTrue( Hibernate.isInitialized( foo.getBytes() ) );
 		assertTrue( foo.getBytes().length==6 );
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertTrue( baz.getFooSet().size()==1 );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getCache().evictCollectionRegion("org.hibernate.test.legacy.Baz.fooSet");
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertFalse( Hibernate.isInitialized( baz.getFooSet() ) );
 		baz2 = (Baz) s.get( Baz.class, baz2.getCode() );
 		baz2.setFooSet( baz.getFooSet() );
 		baz.setFooSet(null);
 		assertFalse( Hibernate.isInitialized( baz2.getFooSet() ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (Foo) s.get( Foo.class, foo.getKey() );
 		assertTrue( foo.getBytes().length==6 );
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		baz2 = (Baz) s.get( Baz.class, baz2.getCode() );
 		assertFalse( Hibernate.isInitialized( baz.getFooSet() ) );
 		assertTrue( baz.getFooSet().size()==0 );
 		assertTrue( Hibernate.isInitialized( baz2.getFooSet() ) ); //fooSet has batching enabled
 		assertTrue( baz2.getFooSet().size()==1 );
 		s.delete(baz);
 		s.delete(baz2);
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCriteriaCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz bb = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( bb == null );
 		Baz baz = new Baz();
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Baz b = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( Hibernate.isInitialized( b.getTopGlarchez() ) );
 		assertTrue( b.getTopGlarchez().size() == 0 );
 		s.delete( b );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQuery() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Foo foo = new Foo();
 		s.save(foo);
 		Foo foo2 = new Foo();
 		s.save(foo2);
 		foo.setFoo(foo2);
 
 		List list = s.createQuery( "from Foo foo inner join fetch foo.foo" ).list();
 		Foo foof = (Foo) list.get(0);
 		assertTrue( Hibernate.isInitialized( foof.getFoo() ) );
 
 		s.createQuery( "from Baz baz left outer join fetch baz.fooToGlarch" ).list();
 
 		list = s.createQuery( "select foo, bar from Foo foo left outer join foo.foo bar where foo = ?" )
 				.setParameter( 0, foo, s.getTypeHelper().entity(Foo.class) )
 				.list();
 		Object[] row1 = (Object[]) list.get(0);
 		assertTrue( row1[0]==foo && row1[1]==foo2 );
 
 		s.createQuery( "select foo.foo.foo.string from Foo foo where foo.foo = 'bar'" ).list();
 		s.createQuery( "select foo.foo.foo.foo.string from Foo foo where foo.foo = 'bar'" ).list();
 		s.createQuery( "select foo from Foo foo where foo.foo.foo = 'bar'" ).list();
 		s.createQuery( "select foo.foo.foo.foo.string from Foo foo where foo.foo.foo = 'bar'" ).list();
 		s.createQuery( "select foo.foo.foo.string from Foo foo where foo.foo.foo.foo.string = 'bar'" ).list();
 		if ( ! (getDialect() instanceof HSQLDialect) )
 			s.createQuery( "select foo.string from Foo foo where foo.foo.foo.foo = foo.foo.foo" ).list();
 		s.createQuery( "select foo.string from Foo foo where foo.foo.foo = 'bar' and foo.foo.foo.foo = 'baz'" ).list();
 		s.createQuery( "select foo.string from Foo foo where foo.foo.foo.foo.string = 'a' and foo.foo.string = 'b'" )
 				.list();
 
 		s.createQuery( "from Bar bar, foo in elements(bar.baz.fooArray)" ).list();
 
 		//s.find("from Baz as baz where baz.topComponents[baz].name = 'bazzz'");
 
 		if ( (getDialect() instanceof DB2Dialect) && !(getDialect() instanceof DerbyDialect) ) {
 			s.createQuery( "from Foo foo where lower( foo.foo.string ) = 'foo'" ).list();
 			s.createQuery( "from Foo foo where lower( (foo.foo.string || 'foo') || 'bar' ) = 'foo'" ).list();
 			s.createQuery( "from Foo foo where repeat( (foo.foo.string || 'foo') || 'bar', 2 ) = 'foo'" ).list();
 			s.createQuery(
 					"from Bar foo where foo.foo.integer is not null and repeat( (foo.foo.string || 'foo') || 'bar', (5+5)/2 ) = 'foo'"
 			).list();
 			s.createQuery(
 					"from Bar foo where foo.foo.integer is not null or repeat( (foo.foo.string || 'foo') || 'bar', (5+5)/2 ) = 'foo'"
 			).list();
 		}
 		if (getDialect() instanceof SybaseDialect) {
 			s.createQuery( "select baz from Baz as baz join baz.fooArray foo group by baz order by sum(foo.float)" )
 					.iterate();
 		}
 
 		s.createQuery( "from Foo as foo where foo.component.glarch.name is not null" ).list();
 		s.createQuery( "from Foo as foo left outer join foo.component.glarch as glarch where glarch.name = 'foo'" )
 				.list();
 
 		list = s.createQuery( "from Foo" ).list();
 		assertTrue( list.size()==2 && list.get(0) instanceof FooProxy );
 		list = s.createQuery( "from Foo foo left outer join foo.foo" ).list();
 		assertTrue( list.size()==2 && ( (Object[]) list.get(0) )[0] instanceof FooProxy );
 
 		s.createQuery("from Bar, Bar").list();
 		s.createQuery("from Foo, Bar").list();
 		s.createQuery( "from Baz baz left join baz.fooToGlarch, Bar bar join bar.foo" ).list();
 		s.createQuery( "from Baz baz left join baz.fooToGlarch join baz.fooSet" ).list();
 		s.createQuery( "from Baz baz left join baz.fooToGlarch join fetch baz.fooSet foo left join fetch foo.foo" )
 				.list();
 
 		list = s.createQuery(
 				"from Foo foo where foo.string='osama bin laden' and foo.boolean = true order by foo.string asc, foo.component.count desc"
 		).list();
 		assertTrue( "empty query", list.size()==0 );
 		Iterator iter = s.createQuery(
 				"from Foo foo where foo.string='osama bin laden' order by foo.string asc, foo.component.count desc"
 		).iterate();
 		assertTrue( "empty iterator", !iter.hasNext() );
 
 		list = s.createQuery( "select foo.foo from Foo foo" ).list();
 		assertTrue( "query", list.size()==1 );
 		assertTrue( "returned object", list.get(0)==foo.getFoo() );
 		foo.getFoo().setFoo(foo);
 		foo.setString("fizard");
 		//The following test is disabled for databases with no subselects...also for Interbase (not sure why).
 		if (
 				!(getDialect() instanceof MySQLDialect) &&
 				!(getDialect() instanceof HSQLDialect) &&
 				!(getDialect() instanceof MckoiDialect) &&
 				!(getDialect() instanceof SAPDBDialect) &&
 				!(getDialect() instanceof PointbaseDialect) &&
 				!(getDialect() instanceof DerbyDialect)
 		)  {
 			// && !db.equals("weblogic") {
 			if ( !( getDialect() instanceof InterbaseDialect ) ) {
 				list = s.createQuery( "from Foo foo where ? = some elements(foo.component.importantDates)" )
 						.setParameter( 0, new Date(), StandardBasicTypes.DATE )
 						.list();
 				assertTrue( "component query", list.size()==2 );
 			}
 			if( !( getDialect() instanceof TimesTenDialect)) {
 				list = s.createQuery( "from Foo foo where size(foo.component.importantDates) = 3" ).list(); //WAS: 4
 				assertTrue( "component query", list.size()==2 );
 				list = s.createQuery( "from Foo foo where 0 = size(foo.component.importantDates)" ).list();
 				assertTrue( "component query", list.size()==0 );
 			}
 			list = s.createQuery( "from Foo foo where exists elements(foo.component.importantDates)" ).list();
 			assertTrue( "component query", list.size()==2 );
 			s.createQuery( "from Foo foo where not exists (from Bar bar where bar.id = foo.id)" ).list();
 
 			s.createQuery(
 					"select foo.foo from Foo foo where foo = some(select x from Foo x where x.long > foo.foo.long)"
 			).list();
 			s.createQuery( "select foo.foo from Foo foo where foo = some(from Foo x where (x.long > foo.foo.long))" )
 					.list();
 			if ( !( getDialect() instanceof TimesTenDialect)) {
 				s.createQuery(
 						"select foo.foo from Foo foo where foo.long = some( select max(x.long) from Foo x where (x.long > foo.foo.long) group by x.foo )"
 				).list();
 			}
 			s.createQuery(
 					"from Foo foo where foo = some(select x from Foo x where x.long > foo.foo.long) and foo.foo.string='baz'"
 			).list();
 			s.createQuery(
 					"from Foo foo where foo.foo.string='baz' and foo = some(select x from Foo x where x.long > foo.foo.long)"
 			).list();
 			s.createQuery( "from Foo foo where foo = some(select x from Foo x where x.long > foo.foo.long)" ).list();
 
 			s.createQuery(
 					"select foo.string, foo.date, foo.foo.string, foo.id from Foo foo, Baz baz where foo in elements(baz.fooArray) and foo.string like 'foo'"
 			).iterate();
 		}
 		list = s.createQuery( "from Foo foo where foo.component.count is null order by foo.component.count" ).list();
 		assertTrue( "component query", list.size()==0 );
 		list = s.createQuery( "from Foo foo where foo.component.name='foo'" ).list();
 		assertTrue( "component query", list.size()==2 );
 		list = s.createQuery(
 				"select distinct foo.component.name, foo.component.name from Foo foo where foo.component.name='foo'"
 		).list();
 		assertTrue( "component query", list.size()==1 );
 		list = s.createQuery( "select distinct foo.component.name, foo.id from Foo foo where foo.component.name='foo'" )
 				.list();
 		assertTrue( "component query", list.size()==2 );
 		list = s.createQuery( "select foo.foo from Foo foo" ).list();
 		assertTrue( "query", list.size()==2 );
 		list = s.createQuery( "from Foo foo where foo.id=?" )
 				.setParameter( 0, foo.getKey(), StandardBasicTypes.STRING )
 				.list();
 		assertTrue( "id query", list.size()==1 );
 		list = s.createQuery( "from Foo foo where foo.key=?" )
 				.setParameter( 0, foo.getKey(), StandardBasicTypes.STRING )
 				.list();
 		assertTrue( "named id query", list.size()==1 );
 		assertTrue( "id query", list.get(0)==foo );
 		list = s.createQuery( "select foo.foo from Foo foo where foo.string='fizard'" ).list();
 		assertTrue( "query", list.size()==1 );
 		assertTrue( "returned object", list.get(0)==foo.getFoo() );
 		list = s.createQuery( "from Foo foo where foo.component.subcomponent.name='bar'" ).list();
 		assertTrue( "components of components", list.size()==2 );
 		list = s.createQuery( "select foo.foo from Foo foo where foo.foo.id=?" )
 				.setParameter( 0, foo.getFoo().getKey(), StandardBasicTypes.STRING )
 				.list();
 		assertTrue( "by id query", list.size()==1 );
 		assertTrue( "by id returned object", list.get(0)==foo.getFoo() );
 
 		s.createQuery( "from Foo foo where foo.foo = ?" ).setParameter( 0, foo.getFoo(), s.getTypeHelper().entity(Foo.class) ).list();
 
 		assertTrue( !s.createQuery( "from Bar bar where bar.string='a string' or bar.string='a string'" )
 				.iterate()
 				.hasNext() );
 
 		iter = s.createQuery( "select foo.component.name, elements(foo.component.importantDates) from Foo foo where foo.foo.id=?" )
 				.setParameter( 0, foo.getFoo().getKey(), StandardBasicTypes.STRING )
 				.iterate();
 		int i=0;
 		while ( iter.hasNext() ) {
 			i++;
 			Object[] row = (Object[]) iter.next();
 			assertTrue( row[0] instanceof String && ( row[1]==null || row[1] instanceof Date ) );
 		}
 		assertTrue(i==3); //WAS: 4
 		iter = s.createQuery( "select max( elements(foo.component.importantDates) ) from Foo foo group by foo.id" )
 				.iterate();
 		assertTrue( iter.next() instanceof Date );
 
 		list = s.createQuery(
 				"select foo.foo.foo.foo from Foo foo, Foo foo2 where"
 						+ " foo = foo2.foo and not not ( not foo.string='fizard' )"
 						+ " and foo2.string between 'a' and (foo.foo.string)"
 						+ ( ( getDialect() instanceof HSQLDialect || getDialect() instanceof InterbaseDialect || getDialect() instanceof TimesTenDialect || getDialect() instanceof TeradataDialect) ?
 						" and ( foo2.string in ( 'fiz', 'blah') or 1=1 )"
 						:
 						" and ( foo2.string in ( 'fiz', 'blah', foo.foo.string, foo.string, foo2.string ) )"
 				)
 		).list();
 		assertTrue( "complex query", list.size()==1 );
 		assertTrue( "returned object", list.get(0)==foo );
 		foo.setString("from BoogieDown  -tinsel town  =!@#$^&*())");
 		list = s.createQuery( "from Foo foo where foo.string='from BoogieDown  -tinsel town  =!@#$^&*())'" ).list();
 		assertTrue( "single quotes", list.size()==1 );
 		list = s.createQuery( "from Foo foo where not foo.string='foo''bar'" ).list();
 		assertTrue( "single quotes", list.size()==2 );
 		list = s.createQuery( "from Foo foo where foo.component.glarch.next is null" ).list();
 		assertTrue( "query association in component", list.size()==2 );
 		Bar bar = new Bar();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		bar.setBaz(baz);
 		baz.setManyToAny( new ArrayList() );
 		baz.getManyToAny().add(bar);
 		baz.getManyToAny().add(foo);
 		s.save(bar);
 		s.save(baz);
 		list = s.createQuery(
 				" from Bar bar where bar.baz.count=667 and bar.baz.count!=123 and not bar.baz.name='1-E-1'"
 		).list();
 		assertTrue( "query many-to-one", list.size()==1 );
 		list = s.createQuery( " from Bar i where i.baz.name='Bazza'" ).list();
 		assertTrue( "query many-to-one", list.size()==1 );
 
 		Iterator rs = s.createQuery( "select count(distinct foo.foo) from Foo foo" ).iterate();
 		assertTrue( "count", ( (Long) rs.next() ).longValue()==2 );
 		assertTrue( !rs.hasNext() );
 		rs = s.createQuery( "select count(foo.foo.boolean) from Foo foo" ).iterate();
 		assertTrue( "count", ( (Long) rs.next() ).longValue()==2 );
 		assertTrue( !rs.hasNext() );
 		rs = s.createQuery( "select count(*), foo.int from Foo foo group by foo.int" ).iterate();
 		assertTrue( "count(*) group by", ( (Object[]) rs.next() )[0].equals( new Long(3) ) );
 		assertTrue( !rs.hasNext() );
 		rs = s.createQuery( "select sum(foo.foo.int) from Foo foo" ).iterate();
 		assertTrue( "sum", ( (Long) rs.next() ).longValue()==4 );
 		assertTrue( !rs.hasNext() );
 		rs = s.createQuery( "select count(foo) from Foo foo where foo.id=?" )
 				.setParameter( 0, foo.getKey(), StandardBasicTypes.STRING )
 				.iterate();
 		assertTrue( "id query count", ( (Long) rs.next() ).longValue()==1 );
 		assertTrue( !rs.hasNext() );
 
 		s.createQuery( "from Foo foo where foo.boolean = ?" )
 				.setParameter( 0, new Boolean(true), StandardBasicTypes.BOOLEAN )
 				.list();
 
 		s.createQuery( "select new Foo(fo.x) from Fo fo" ).list();
 		s.createQuery( "select new Foo(fo.integer) from Foo fo" ).list();
 
 		list = s.createQuery("select new Foo(fo.x) from Foo fo")
 			//.setComment("projection test")
 			.setCacheable(true)
 			.list();
 		assertTrue(list.size()==3);
 		list = s.createQuery("select new Foo(fo.x) from Foo fo")
 			//.setComment("projection test 2")
 			.setCacheable(true)
 			.list();
 		assertTrue(list.size()==3);
 
 		rs = s.createQuery( "select new Foo(fo.x) from Foo fo" ).iterate();
 		assertTrue( "projection iterate (results)", rs.hasNext() );
 		assertTrue( "projection iterate (return check)", Foo.class.isAssignableFrom( rs.next().getClass() ) );
 
 		ScrollableResults sr = s.createQuery("select new Foo(fo.x) from Foo fo").scroll();
 		assertTrue( "projection scroll (results)", sr.next() );
 		assertTrue( "projection scroll (return check)", Foo.class.isAssignableFrom( sr.get(0).getClass() ) );
 
 		list = s.createQuery( "select foo.long, foo.component.name, foo, foo.foo from Foo foo" ).list();
 		rs = list.iterator();
 		int count=0;
 		while ( rs.hasNext() ) {
 			count++;
 			Object[] row = (Object[]) rs.next();
 			assertTrue( row[0] instanceof Long );
 			assertTrue( row[1] instanceof String );
 			assertTrue( row[2] instanceof Foo );
 			assertTrue( row[3] instanceof Foo );
 		}
 		assertTrue(count!=0);
 		list = s.createQuery( "select avg(foo.float), max(foo.component.name), count(distinct foo.id) from Foo foo" )
 				.list();
 		rs = list.iterator();
 		count=0;
 		while ( rs.hasNext() ) {
 			count++;
 			Object[] row = (Object[]) rs.next();
 			assertTrue( row[0] instanceof Double );
 			assertTrue( row[1] instanceof String );
 			assertTrue( row[2] instanceof Long );
 		}
 		assertTrue(count!=0);
 		list = s.createQuery( "select foo.long, foo.component, foo, foo.foo from Foo foo" ).list();
 		rs = list.iterator();
 		count=0;
 		while ( rs.hasNext() ) {
 			count++;
 			Object[] row = (Object[]) rs.next();
 			assertTrue( row[0] instanceof Long );
 			assertTrue( row[1] instanceof FooComponent );
 			assertTrue( row[2] instanceof Foo );
 			assertTrue( row[3] instanceof Foo );
 		}
 		assertTrue(count!=0);
 
 		s.save( new Holder("ice T") );
 		s.save( new Holder("ice cube") );
 
 		assertTrue( s.createQuery( "from java.lang.Object as o" ).list().size()==15 );
 		assertTrue( s.createQuery( "from Named" ).list().size()==7 );
 		assertTrue( s.createQuery( "from Named n where n.name is not null" ).list().size()==4 );
 		iter = s.createQuery( "from Named n" ).iterate();
 		while ( iter.hasNext() ) {
 			assertTrue( iter.next() instanceof Named );
 		}
 
 		s.save( new Holder("bar") );
 		iter = s.createQuery( "from Named n0, Named n1 where n0.name = n1.name" ).iterate();
 		int cnt = 0;
 		while ( iter.hasNext() ) {
 			Object[] row = (Object[]) iter.next();
 			if ( row[0]!=row[1] ) cnt++;
 		}
 		if ( !(getDialect() instanceof HSQLDialect) ) {
 			assertTrue(cnt==2);
 			assertTrue( s.createQuery( "from Named n0, Named n1 where n0.name = n1.name" ).list().size()==7 );
 		}
 
 		Query qu = s.createQuery("from Named n where n.name = :name");
 		qu.getReturnTypes();
 		qu.getNamedParameters();
 
 		iter = s.createQuery( "from java.lang.Object" ).iterate();
 		int c = 0;
 		while ( iter.hasNext() ) {
 			iter.next();
 			c++;
 		}
 		assertTrue(c==16);
 
 		s.createQuery( "select baz.code, min(baz.count) from Baz baz group by baz.code" ).iterate();
 
 		iter = s.createQuery( "selecT baz from Baz baz where baz.stringDateMap['foo'] is not null or baz.stringDateMap['bar'] = ?" )
 				.setParameter( 0, new Date(), StandardBasicTypes.DATE )
 				.iterate();
 		assertFalse( iter.hasNext() );
 		list = s.createQuery( "select baz from Baz baz where baz.stringDateMap['now'] is not null" ).list();
 		assertTrue( list.size()==1 );
 		list = s.createQuery(
 				"select baz from Baz baz where baz.stringDateMap['now'] is not null and baz.stringDateMap['big bang'] < baz.stringDateMap['now']"
 		).list();
 		assertTrue( list.size()==1 );
 		list = s.createQuery( "select index(date) from Baz baz join baz.stringDateMap date" ).list();
 		System.out.println(list);
 		assertTrue( list.size()==2 );
 
 		s.createQuery(
 				"from Foo foo where foo.integer not between 1 and 5 and foo.string not in ('cde', 'abc') and foo.string is not null and foo.integer<=3"
 		).list();
 
 		s.createQuery( "from Baz baz inner join baz.collectionComponent.nested.foos foo where foo.string is null" )
 				.list();
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof MckoiDialect) && !(getDialect() instanceof SAPDBDialect) && !(getDialect() instanceof PointbaseDialect) )  {
 			s.createQuery(
 					"from Baz baz inner join baz.fooSet where '1' in (from baz.fooSet foo where foo.string is not null)"
 			).list();
 			s.createQuery(
 					"from Baz baz where 'a' in elements(baz.collectionComponent.nested.foos) and 1.0 in elements(baz.collectionComponent.nested.floats)"
 			).list();
 			s.createQuery(
 					"from Baz baz where 'b' in elements(baz.collectionComponent.nested.foos) and 1.0 in elements(baz.collectionComponent.nested.floats)"
 			).list();
 		}
 
 		s.createQuery( "from Foo foo join foo.foo where foo.foo in ('1','2','3')" ).list();
 		if ( !(getDialect() instanceof HSQLDialect) )
 			s.createQuery( "from Foo foo left join foo.foo where foo.foo in ('1','2','3')" ).list();
 		s.createQuery( "select foo.foo from Foo foo where foo.foo in ('1','2','3')" ).list();
 		s.createQuery( "select foo.foo.string from Foo foo where foo.foo in ('1','2','3')" ).list();
 		s.createQuery( "select foo.foo.string from Foo foo where foo.foo.string in ('1','2','3')" ).list();
 		s.createQuery( "select foo.foo.long from Foo foo where foo.foo.string in ('1','2','3')" ).list();
 		s.createQuery( "select count(*) from Foo foo where foo.foo.string in ('1','2','3') or foo.foo.long in (1,2,3)" )
 				.list();
 		s.createQuery( "select count(*) from Foo foo where foo.foo.string in ('1','2','3') group by foo.foo.long" )
 				.list();
 
 		s.createQuery( "from Foo foo1 left join foo1.foo foo2 left join foo2.foo where foo1.string is not null" )
 				.list();
 		s.createQuery( "from Foo foo1 left join foo1.foo.foo where foo1.string is not null" ).list();
 		s.createQuery( "from Foo foo1 left join foo1.foo foo2 left join foo1.foo.foo foo3 where foo1.string is not null" )
 				.list();
 
 		s.createQuery( "select foo.formula from Foo foo where foo.formula > 0" ).list();
 
 		int len = s.createQuery( "from Foo as foo join foo.foo as foo2 where foo2.id >'a' or foo2.id <'a'" ).list().size();
 		assertTrue(len==2);
 
 		for ( Object entity : s.createQuery( "from Holder" ).list() ) {
 			s.delete( entity );
 		}
 
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		baz = (Baz) s.createQuery("from Baz baz left outer join fetch baz.manyToAny").uniqueResult();
 		assertTrue( Hibernate.isInitialized( baz.getManyToAny() ) );
 		assertTrue( baz.getManyToAny().size()==2 );
 		BarProxy barp = (BarProxy) baz.getManyToAny().get(0);
 		s.createQuery( "from Baz baz join baz.manyToAny" ).list();
 		assertTrue( s.createQuery( "select baz from Baz baz join baz.manyToAny a where index(a) = 0" ).list().size()==1 );
 
 		FooProxy foop = (FooProxy) s.get( Foo.class, foo.getKey() );
 		assertTrue( foop == baz.getManyToAny().get(1) );
 
 		barp.setBaz(baz);
 		assertTrue(
 				s.createQuery( "select bar from Bar bar where bar.baz.stringDateMap['now'] is not null" ).list().size()==1 );
 		assertTrue(
 				s.createQuery(
 						"select bar from Bar bar join bar.baz b where b.stringDateMap['big bang'] < b.stringDateMap['now'] and b.stringDateMap['now'] is not null"
 				).list()
 						.size()==1 );
 		assertTrue(
 				s.createQuery(
 						"select bar from Bar bar where bar.baz.stringDateMap['big bang'] < bar.baz.stringDateMap['now'] and bar.baz.stringDateMap['now'] is not null"
 				).list()
 						.size()==1 );
 
 		list = s.createQuery( "select foo.string, foo.component, foo.id from Bar foo" ).list();
 		assertTrue ( ( (FooComponent) ( (Object[]) list.get(0) )[1] ).getName().equals("foo") );
 		list = s.createQuery( "select elements(baz.components) from Baz baz" ).list();
 		assertTrue( list.size()==2 );
 		list = s.createQuery( "select bc.name from Baz baz join baz.components bc" ).list();
 		assertTrue( list.size()==2 );
 		//list = s.find("select bc from Baz baz join baz.components bc");
 
 		s.createQuery("from Foo foo where foo.integer < 10 order by foo.string").setMaxResults(12).list();
 
 		s.delete(barp);
 		s.delete(baz);
 		s.delete( foop.getFoo() );
 		s.delete(foop);
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCascadeDeleteDetached() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		List list = new ArrayList();
 		list.add( new Fee() );
 		baz.setFees( list );
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		s.getTransaction().commit();
 		s.close();
 
 		assertFalse( Hibernate.isInitialized( baz.getFees() ) );
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( baz );
 		s.flush();
 		assertFalse( s.createQuery( "from Fee" ).iterate().hasNext() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = new Baz();
 		list = new ArrayList();
 		list.add( new Fee() );
 		list.add( new Fee() );
 		baz.setFees(list);
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		Hibernate.initialize( baz.getFees() );
 		s.getTransaction().commit();
 		s.close();
 
 		assertTrue( baz.getFees().size() == 2 );
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete(baz);
 		s.flush();
 		assertFalse( s.createQuery( "from Fee" ).iterate().hasNext() );
 		s.getTransaction().commit();
 		s.close();
 
 	}
 
 	@Test
 	public void testForeignKeys() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Foo foo = new Foo();
 		List bag = new ArrayList();
 		bag.add(foo);
 		baz.setIdFooBag(bag);
 		baz.setFoo(foo);
 		s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNonlazyCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.createCriteria(Baz.class)
 			//.setComment("criteria test")
 			.setFetchMode( "stringDateMap", FetchMode.JOIN )
 			.uniqueResult();
 		assertTrue( Hibernate.isInitialized( baz.getFooToGlarch() ) );
 		assertTrue( Hibernate.isInitialized( baz.getFooComponentToFoo() ) );
 		assertTrue( !Hibernate.isInitialized( baz.getStringSet() ) );
 		assertTrue( Hibernate.isInitialized( baz.getStringDateMap() ) );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testReuseDeletedCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		s.flush();
 		s.delete(baz);
 		Baz baz2 = new Baz();
 		baz2.setStringArray( new String[] {"x-y-z"} );
 		s.save(baz2);
 		s.getTransaction().commit();
 		s.close();
 
 		baz2.setStringSet( baz.getStringSet() );
 		baz2.setStringArray( baz.getStringArray() );
 		baz2.setFooArray( baz.getFooArray() );
 
 		s = openSession();
 		s.beginTransaction();
 		s.update(baz2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		assertTrue( baz2.getStringArray().length==3 );
 		assertTrue( baz2.getStringSet().size()==3 );
 		s.delete(baz2);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testPropertyRef() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Holder h = new Holder();
 		h.setName("foo");
 		Holder h2 = new Holder();
 		h2.setName("bar");
 		h.setOtherHolder(h2);
 		Serializable hid = s.save(h);
 		Qux q = new Qux();
 		q.setHolder(h2);
 		Serializable qid = s.save(q);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		h = (Holder) s.load(Holder.class, hid);
 		assertEquals( h.getName(), "foo");
 		assertEquals( h.getOtherHolder().getName(), "bar");
 		Object[] res = (Object[]) s.createQuery( "from Holder h join h.otherHolder oh where h.otherHolder.name = 'bar'" )
 				.list()
 				.get(0);
 		assertTrue( res[0]==h );
 		q = (Qux) s.get(Qux.class, qid);
 		assertTrue( q.getHolder() == h.getOtherHolder() );
 		s.delete(h);
 		s.delete(q);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQueryCollectionOfValues() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		Glarch g = new Glarch();
 		Serializable gid = s.save(g);
 
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) /*&& !(dialect instanceof MckoiDialect)*/ && !(getDialect() instanceof SAPDBDialect) && !(getDialect() instanceof PointbaseDialect) && !(getDialect() instanceof TimesTenDialect) ) {
 			s.createFilter( baz.getFooArray(), "where size(this.bytes) > 0" ).list();
 			s.createFilter( baz.getFooArray(), "where 0 in elements(this.bytes)" ).list();
 		}
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Baz baz join baz.fooSet foo join foo.foo.foo foo2 where foo2.string = 'foo'" ).list();
 		s.createQuery( "from Baz baz join baz.fooArray foo join foo.foo.foo foo2 where foo2.string = 'foo'" ).list();
 		s.createQuery( "from Baz baz join baz.stringDateMap date where index(date) = 'foo'" ).list();
 		s.createQuery( "from Baz baz join baz.topGlarchez g where index(g) = 'A'" ).list();
 		s.createQuery( "select index(g) from Baz baz join baz.topGlarchez g" ).list();
 
 		assertTrue( s.createQuery( "from Baz baz left join baz.stringSet" ).list().size()==3 );
 		baz = (Baz) s.createQuery( "from Baz baz join baz.stringSet str where str='foo'" ).list().get(0);
 		assertTrue( !Hibernate.isInitialized( baz.getStringSet() ) );
 		baz = (Baz) s.createQuery( "from Baz baz left join fetch baz.stringSet" ).list().get(0);
 		assertTrue( Hibernate.isInitialized( baz.getStringSet() ) );
 		assertTrue( s.createQuery( "from Baz baz join baz.stringSet string where string='foo'" ).list().size()==1 );
 		assertTrue( s.createQuery( "from Baz baz inner join baz.components comp where comp.name='foo'" ).list().size()==1 );
 		//List bss = s.find("select baz, ss from Baz baz inner join baz.stringSet ss");
 		s.createQuery( "from Glarch g inner join g.fooComponents comp where comp.fee is not null" ).list();
 		s.createQuery( "from Glarch g inner join g.fooComponents comp join comp.fee fee where fee.count > 0" ).list();
 		s.createQuery( "from Glarch g inner join g.fooComponents comp where comp.fee.count is not null" ).list();
 
 		s.delete(baz);
 		s.delete( s.get(Glarch.class, gid) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testBatchLoad() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		SortedSet stringSet = new TreeSet();
 		stringSet.add("foo");
 		stringSet.add("bar");
 		Set fooSet = new HashSet();
 		for (int i=0; i<3; i++) {
 			Foo foo = new Foo();
 			s.save(foo);
 			fooSet.add(foo);
 		}
 		baz.setFooSet(fooSet);
 		baz.setStringSet(stringSet);
 		s.save(baz);
 		Baz baz2 = new Baz();
 		fooSet = new HashSet();
 		for (int i=0; i<2; i++) {
 			Foo foo = new Foo();
 			s.save(foo);
 			fooSet.add(foo);
 		}
 		baz2.setFooSet(fooSet);
 		s.save(baz2);
 		Baz baz3 = new Baz();
 		stringSet = new TreeSet();
 		stringSet.add("foo");
 		stringSet.add("baz");
 		baz3.setStringSet(stringSet);
 		s.save(baz3);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		baz3 = (Baz) s.load( Baz.class, baz3.getCode() );
 		assertFalse( Hibernate.isInitialized(baz.getFooSet()) || Hibernate.isInitialized(baz2.getFooSet()) || Hibernate.isInitialized(baz3.getFooSet()) );
 		assertFalse( Hibernate.isInitialized(baz.getStringSet()) || Hibernate.isInitialized(baz2.getStringSet()) || Hibernate.isInitialized(baz3.getStringSet()) );
 		assertTrue( baz.getFooSet().size()==3 );
 		assertTrue( Hibernate.isInitialized(baz.getFooSet()) && Hibernate.isInitialized(baz2.getFooSet()) && Hibernate.isInitialized(baz3.getFooSet()));
 		assertTrue( baz2.getFooSet().size()==2 );
 		assertTrue( baz3.getStringSet().contains("baz") );
 		assertTrue( Hibernate.isInitialized(baz.getStringSet()) && Hibernate.isInitialized(baz2.getStringSet()) && Hibernate.isInitialized(baz3.getStringSet()));
 		assertTrue( baz.getStringSet().size()==2 && baz2.getStringSet().size()==0 );
 		s.delete(baz);
 		s.delete(baz2);
 		s.delete(baz3);
 		Iterator iter = new JoinedIterator( new Iterator[] { baz.getFooSet().iterator(), baz2.getFooSet().iterator() } );
 		while ( iter.hasNext() ) s.delete( iter.next() );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetchInitializedCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Collection fooBag = new ArrayList();
 		fooBag.add( new Foo() );
 		fooBag.add( new Foo() );
 		baz.setFooBag( fooBag );
 		s.save(baz);
 		s.flush();
 		fooBag = baz.getFooBag();
 		s.createQuery( "from Baz baz left join fetch baz.fooBag" ).list();
