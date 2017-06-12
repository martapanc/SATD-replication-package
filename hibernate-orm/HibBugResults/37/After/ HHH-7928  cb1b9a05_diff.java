diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/CollectionEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/CollectionEntry.java
index fcd269acba..0f722ed446 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/CollectionEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CollectionEntry.java
@@ -1,439 +1,446 @@
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
 package org.hibernate.engine.spi;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.Collection;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.collection.internal.AbstractPersistentCollection;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * We need an entry to tell us all about the current state
  * of a collection with respect to its persistent state
  *
  * @author Gavin King
  */
 public final class CollectionEntry implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionEntry.class.getName());
 
 	//ATTRIBUTES MAINTAINED BETWEEN FLUSH CYCLES
 
 	// session-start/post-flush persistent state
 	private Serializable snapshot;
 	// allow the CollectionSnapshot to be serialized
 	private String role;
 
 	// "loaded" means the reference that is consistent
 	// with the current database state
 	private transient CollectionPersister loadedPersister;
 	private Serializable loadedKey;
 
 	// ATTRIBUTES USED ONLY DURING FLUSH CYCLE
 
 	// during flush, we navigate the object graph to
 	// collections and decide what to do with them
 	private transient boolean reached;
 	private transient boolean processed;
 	private transient boolean doupdate;
 	private transient boolean doremove;
 	private transient boolean dorecreate;
 	// if we instantiate a collection during the flush() process,
 	// we must ignore it for the rest of the flush()
 	private transient boolean ignore;
 
 	// "current" means the reference that was found during flush()
 	private transient CollectionPersister currentPersister;
 	private transient Serializable currentKey;
 
 	/**
 	 * For newly wrapped collections, or dereferenced collection wrappers
 	 */
 	public CollectionEntry(CollectionPersister persister, PersistentCollection collection) {
 		// new collections that get found + wrapped
 		// during flush shouldn't be ignored
 		ignore = false;
 
 		collection.clearDirty(); //a newly wrapped collection is NOT dirty (or we get unnecessary version updates)
 
 		snapshot = persister.isMutable() ?
 				collection.getSnapshot(persister) :
 				null;
 		collection.setSnapshot(loadedKey, role, snapshot);
 	}
 
 	/**
 	 * For collections just loaded from the database
 	 */
 	public CollectionEntry(
 			final PersistentCollection collection,
 			final CollectionPersister loadedPersister,
 			final Serializable loadedKey,
 			final boolean ignore
 	) {
 		this.ignore=ignore;
 
 		//collection.clearDirty()
 
 		this.loadedKey = loadedKey;
 		setLoadedPersister(loadedPersister);
 
 		collection.setSnapshot(loadedKey, role, null);
 
 		//postInitialize() will be called after initialization
 	}
 
 	/**
 	 * For uninitialized detached collections
 	 */
 	public CollectionEntry(CollectionPersister loadedPersister, Serializable loadedKey) {
 		// detached collection wrappers that get found + reattached
 		// during flush shouldn't be ignored
 		ignore = false;
 
 		//collection.clearDirty()
 
 		this.loadedKey = loadedKey;
 		setLoadedPersister(loadedPersister);
 	}
 
 	/**
 	 * For initialized detached collections
 	 */
 	public CollectionEntry(PersistentCollection collection, SessionFactoryImplementor factory) throws MappingException {
 		// detached collections that get found + reattached
 		// during flush shouldn't be ignored
 		ignore = false;
 
 		loadedKey = collection.getKey();
 		setLoadedPersister( factory.getCollectionPersister( collection.getRole() ) );
 
 		snapshot = collection.getStoredSnapshot();
 	}
 
 	/**
 	 * Used from custom serialization.
 	 *
 	 * @see #serialize
 	 * @see #deserialize
 	 */
 	private CollectionEntry(
 			String role,
 	        Serializable snapshot,
 	        Serializable loadedKey,
 	        SessionFactoryImplementor factory) {
 		this.role = role;
 		this.snapshot = snapshot;
 		this.loadedKey = loadedKey;
 		if ( role != null ) {
 			afterDeserialize( factory );
 		}
 	}
 
 	/**
 	 * Determine if the collection is "really" dirty, by checking dirtiness
 	 * of the collection elements, if necessary
 	 */
 	private void dirty(PersistentCollection collection) throws HibernateException {
 
 		boolean forceDirty = collection.wasInitialized() &&
 				!collection.isDirty() && //optimization
 				getLoadedPersister() != null &&
 				getLoadedPersister().isMutable() && //optimization
 				( collection.isDirectlyAccessible() || getLoadedPersister().getElementType().isMutable() ) && //optimization
 				!collection.equalsSnapshot( getLoadedPersister() );
 
 		if ( forceDirty ) {
 			collection.dirty();
 		}
 
 	}
 
 	public void preFlush(PersistentCollection collection) throws HibernateException {
 		if ( loadedKey == null && collection.getKey() != null ) {
 			loadedKey = collection.getKey();
 		}
 
 		boolean nonMutableChange = collection.isDirty() &&
 				getLoadedPersister()!=null &&
 				!getLoadedPersister().isMutable();
 		if (nonMutableChange) {
 			throw new HibernateException(
 					"changed an immutable collection instance: " +
 					MessageHelper.collectionInfoString( getLoadedPersister().getRole(), getLoadedKey() )
 				);
 		}
 
 		dirty(collection);
 
 		if ( LOG.isDebugEnabled() && collection.isDirty() && getLoadedPersister() != null ) {
 			LOG.debugf( "Collection dirty: %s",
 					MessageHelper.collectionInfoString( getLoadedPersister().getRole(), getLoadedKey() ) );
 		}
 
 		setDoupdate(false);
 		setDoremove(false);
 		setDorecreate(false);
 		setReached(false);
 		setProcessed(false);
 	}
 
 	public void postInitialize(PersistentCollection collection) throws HibernateException {
 		snapshot = getLoadedPersister().isMutable() ?
 				collection.getSnapshot( getLoadedPersister() ) :
 				null;
 		collection.setSnapshot(loadedKey, role, snapshot);
 		if (getLoadedPersister().getBatchSize() > 1) {
 			((AbstractPersistentCollection) collection).getSession().getPersistenceContext().getBatchFetchQueue().removeBatchLoadableCollection(this); 
 		}
 	}
 
 	/**
 	 * Called after a successful flush
 	 */
 	public void postFlush(PersistentCollection collection) throws HibernateException {
 		if ( isIgnore() ) {
 			ignore = false;
 		}
 		else if ( !isProcessed() ) {
 			throw new AssertionFailure( "collection [" + collection.getRole() + "] was not processed by flush()" );
 		}
 		collection.setSnapshot(loadedKey, role, snapshot);
 	}
 
 	/**
 	 * Called after execution of an action
 	 */
 	public void afterAction(PersistentCollection collection) {
 		loadedKey = getCurrentKey();
 		setLoadedPersister( getCurrentPersister() );
 
 		boolean resnapshot = collection.wasInitialized() &&
 				( isDoremove() || isDorecreate() || isDoupdate() );
 		if ( resnapshot ) {
 			snapshot = loadedPersister==null || !loadedPersister.isMutable() ?
 					null :
 					collection.getSnapshot(loadedPersister); //re-snapshot
 		}
 
 		collection.postAction();
 	}
 
 	public Serializable getKey() {
 		return getLoadedKey();
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public Serializable getSnapshot() {
 		return snapshot;
 	}
 
+	private boolean fromMerge = false;
+
 	/**
 	 * Reset the stored snapshot for both the persistent collection and this collection entry. 
 	 * Used during the merge of detached collections.
 	 * 
 	 * @param collection the persistentcollection to be updated
 	 * @param storedSnapshot the new stored snapshot
 	 */
 	public void resetStoredSnapshot(PersistentCollection collection, Serializable storedSnapshot) {
 		LOG.debugf("Reset storedSnapshot to %s for %s", storedSnapshot, this);
-		
+
+		if ( fromMerge ) {
+			return; // EARLY EXIT!
+		}
+
 		snapshot = storedSnapshot;
-		collection.setSnapshot(loadedKey, role, snapshot);
+		collection.setSnapshot( loadedKey, role, snapshot );
+		fromMerge = true;
 	}
 
 	private void setLoadedPersister(CollectionPersister persister) {
 		loadedPersister = persister;
 		setRole( persister == null ? null : persister.getRole() );
 	}
 
 	void afterDeserialize(SessionFactoryImplementor factory) {
 		loadedPersister = ( factory == null ? null : factory.getCollectionPersister(role) );
 	}
 
 	public boolean wasDereferenced() {
 		return getLoadedKey() == null;
 	}
 
 	public boolean isReached() {
 		return reached;
 	}
 
 	public void setReached(boolean reached) {
 		this.reached = reached;
 	}
 
 	public boolean isProcessed() {
 		return processed;
 	}
 
 	public void setProcessed(boolean processed) {
 		this.processed = processed;
 	}
 
 	public boolean isDoupdate() {
 		return doupdate;
 	}
 
 	public void setDoupdate(boolean doupdate) {
 		this.doupdate = doupdate;
 	}
 
 	public boolean isDoremove() {
 		return doremove;
 	}
 
 	public void setDoremove(boolean doremove) {
 		this.doremove = doremove;
 	}
 
 	public boolean isDorecreate() {
 		return dorecreate;
 	}
 
 	public void setDorecreate(boolean dorecreate) {
 		this.dorecreate = dorecreate;
 	}
 
 	public boolean isIgnore() {
 		return ignore;
 	}
 
 	public CollectionPersister getCurrentPersister() {
 		return currentPersister;
 	}
 
 	public void setCurrentPersister(CollectionPersister currentPersister) {
 		this.currentPersister = currentPersister;
 	}
 
 	/**
 	 * This is only available late during the flush
 	 * cycle
 	 */
 	public Serializable getCurrentKey() {
 		return currentKey;
 	}
 
 	public void setCurrentKey(Serializable currentKey) {
 		this.currentKey = currentKey;
 	}
 
 	/**
 	 * This is only available late during the flush cycle
 	 */
 	public CollectionPersister getLoadedPersister() {
 		return loadedPersister;
 	}
 
 	public Serializable getLoadedKey() {
 		return loadedKey;
 	}
 
 	public void setRole(String role) {
 		this.role = role;
 	}
 
 	@Override
     public String toString() {
 		String result = "CollectionEntry" +
 				MessageHelper.collectionInfoString( loadedPersister.getRole(), loadedKey );
 		if (currentPersister!=null) {
 			result += "->" +
 					MessageHelper.collectionInfoString( currentPersister.getRole(), currentKey );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the collection orphans (entities which were removed from the collection)
 	 */
 	public Collection getOrphans(String entityName, PersistentCollection collection)
 	throws HibernateException {
 		if (snapshot==null) {
 			throw new AssertionFailure("no collection snapshot for orphan delete");
 		}
 		return collection.getOrphans( snapshot, entityName );
 	}
 
 	public boolean isSnapshotEmpty(PersistentCollection collection) {
 		//TODO: does this really need to be here?
 		//      does the collection already have
 		//      it's own up-to-date snapshot?
 		return collection.wasInitialized() &&
 			( getLoadedPersister()==null || getLoadedPersister().isMutable() ) &&
 			collection.isSnapshotEmpty( getSnapshot() );
 	}
 
 
 
 	/**
 	 * Custom serialization routine used during serialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param oos The stream to which we should write the serial data.
 	 * @throws java.io.IOException
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeObject( role );
 		oos.writeObject( snapshot );
 		oos.writeObject( loadedKey );
 	}
 
 	/**
 	 * Custom deserialization routine used during deserialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param ois The stream from which to read the entry.
 	 * @param session The session being deserialized.
 	 * @return The deserialized CollectionEntry
 	 * @throws IOException
 	 * @throws ClassNotFoundException
 	 */
 	public static CollectionEntry deserialize(
 			ObjectInputStream ois,
 	        SessionImplementor session) throws IOException, ClassNotFoundException {
 		return new CollectionEntry(
 				( String ) ois.readObject(),
 		        ( Serializable ) ois.readObject(),
 		        ( Serializable ) ois.readObject(),
 		        ( session == null ? null : session.getFactory() )
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
index 028370d83a..5fb404e3ef 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
@@ -1,800 +1,796 @@
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
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.SortedMap;
 import java.util.TreeMap;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 import org.hibernate.EntityMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.collection.spi.PersistentCollection;
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
 import org.hibernate.metamodel.relational.Size;
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
 
 	public final boolean isEqual(Object x, Object y) {
 		return x == y
 			|| ( x instanceof PersistentCollection && ( (PersistentCollection) x ).isWrapper( y ) )
 			|| ( y instanceof PersistentCollection && ( (PersistentCollection) y ).isWrapper( x ) );
 	}
 
 	public int compare(Object x, Object y) {
 		return 0; // collections cannot be compared
 	}
 
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
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return new Size[] { LEGACY_DICTATED_SIZE };
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return new Size[] { LEGACY_DEFAULT_SIZE };
 	}
 
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
 
 	protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		final List<String> list = new ArrayList<String>();
 		Type elemType = getElementType( factory );
 		Iterator itr = getElementsIterator( value );
 		while ( itr.hasNext() ) {
 			list.add( elemType.toLoggableString( itr.next(), factory ) );
 		}
 		return list.toString();
 	}
 
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
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
 		return getElementsIterator(collection);
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
-				if ( ! ( ( PersistentCollection ) original ).isDirty() ) {
-					( ( PersistentCollection ) result ).clearDirty();
-				}
+				final PersistentCollection originalPersistentCollection = (PersistentCollection) original;
+				final PersistentCollection resultPersistentCollection = (PersistentCollection) result;
+
+				preserveSnapshot( originalPersistentCollection, resultPersistentCollection, elemType, owner, copyCache, session );
 
-				if ( elemType instanceof AssociationType ) {
-					preserveSnapshot( (PersistentCollection) original,
-							(PersistentCollection) result,
-							(AssociationType) elemType, owner, copyCache,
-							session );
+				if ( ! originalPersistentCollection.isDirty() ) {
+					resultPersistentCollection.clearDirty();
 				}
 			}
 		}
 
 		return result;
 	}
 
-	private void preserveSnapshot(PersistentCollection original,
-			PersistentCollection result, AssociationType elemType,
-			Object owner, Map copyCache, SessionImplementor session) {
+	private void preserveSnapshot(
+			PersistentCollection original,
+			PersistentCollection result,
+			Type elemType,
+			Object owner,
+			Map copyCache,
+			SessionImplementor session) {
 		Serializable originalSnapshot = original.getStoredSnapshot();
 		Serializable resultSnapshot = result.getStoredSnapshot();
 		Serializable targetSnapshot;
 
 		if ( originalSnapshot instanceof List ) {
 			targetSnapshot = new ArrayList(
 					( (List) originalSnapshot ).size() );
 			for ( Object obj : (List) originalSnapshot ) {
-				( (List) targetSnapshot ).add( elemType.replace(
-						obj, null, session, owner, copyCache ) );
+				( (List) targetSnapshot ).add( elemType.replace( obj, null, session, owner, copyCache ) );
 			}
 
 		}
 		else if ( originalSnapshot instanceof Map ) {
 			if ( originalSnapshot instanceof SortedMap ) {
-				targetSnapshot = new TreeMap(
-						( (SortedMap) originalSnapshot ).comparator() );
+				targetSnapshot = new TreeMap( ( (SortedMap) originalSnapshot ).comparator() );
 			}
 			else {
 				targetSnapshot = new HashMap(
-						CollectionHelper.determineProperSizing(
-								( (Map) originalSnapshot ).size() ),
-						CollectionHelper.LOAD_FACTOR );
+						CollectionHelper.determineProperSizing( ( (Map) originalSnapshot ).size() ),
+						CollectionHelper.LOAD_FACTOR
+				);
 			}
 
-			for ( Map.Entry<Object, Object> entry : (
-					(Map<Object, Object>) originalSnapshot ).entrySet() ) {
+			for ( Map.Entry<Object, Object> entry : ( (Map<Object, Object>) originalSnapshot ).entrySet() ) {
 				Object key = entry.getKey();
 				Object value = entry.getValue();
-				Object resultSnapshotValue = ( resultSnapshot == null ) ? null
+				Object resultSnapshotValue = ( resultSnapshot == null )
+						? null
 						: ( (Map<Object, Object>) resultSnapshot ).get( key );
 
+				Object newValue = elemType.replace( value, resultSnapshotValue, session, owner, copyCache );
+
 				if ( key == value ) {
-					Object newValue = elemType.replace( value,
-							resultSnapshotValue, session, owner, copyCache );
 					( (Map) targetSnapshot ).put( newValue, newValue );
 
 				}
 				else {
-					Object newValue = elemType.replace( value,
-							resultSnapshotValue, session, owner, copyCache );
 					( (Map) targetSnapshot ).put( key, newValue );
 				}
 
 			}
 
 		}
 		else if ( originalSnapshot instanceof Object[] ) {
 			Object[] arr = (Object[]) originalSnapshot;
 			for ( int i = 0; i < arr.length; i++ ) {
-				arr[i] = elemType.replace(
-						arr[i], null, session, owner, copyCache );
+				arr[i] = elemType.replace( arr[i], null, session, owner, copyCache );
 			}
 			targetSnapshot = originalSnapshot;
 
 		}
 		else {
 			// retain the same snapshot
 			targetSnapshot = resultSnapshot;
 
 		}
 
-		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry(
-				result );
+		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( result );
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/AggregatedCollectionEventListener.java b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/AggregatedCollectionEventListener.java
new file mode 100644
index 0000000000..1f2b2cd7e0
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/AggregatedCollectionEventListener.java
@@ -0,0 +1,202 @@
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
+package org.hibernate.test.event.collection.detached;
+
+import java.io.Serializable;
+import java.util.ArrayList;
+import java.util.List;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.HibernateException;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.engine.spi.CollectionEntry;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.event.service.spi.EventListenerRegistry;
+import org.hibernate.event.spi.AbstractCollectionEvent;
+import org.hibernate.event.spi.EventType;
+import org.hibernate.event.spi.InitializeCollectionEvent;
+import org.hibernate.event.spi.InitializeCollectionEventListener;
+import org.hibernate.event.spi.PostCollectionRecreateEvent;
+import org.hibernate.event.spi.PostCollectionRecreateEventListener;
+import org.hibernate.event.spi.PostCollectionRemoveEvent;
+import org.hibernate.event.spi.PostCollectionRemoveEventListener;
+import org.hibernate.event.spi.PostCollectionUpdateEvent;
+import org.hibernate.event.spi.PostCollectionUpdateEventListener;
+import org.hibernate.event.spi.PreCollectionRecreateEvent;
+import org.hibernate.event.spi.PreCollectionRecreateEventListener;
+import org.hibernate.event.spi.PreCollectionRemoveEvent;
+import org.hibernate.event.spi.PreCollectionRemoveEventListener;
+import org.hibernate.event.spi.PreCollectionUpdateEvent;
+import org.hibernate.event.spi.PreCollectionUpdateEventListener;
+import org.hibernate.integrator.spi.Integrator;
+import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.service.spi.SessionFactoryServiceRegistry;
+
+/**
+ * @author Steve Ebersole
+ * @author Gail Badner
+ */
+public class AggregatedCollectionEventListener
+		implements InitializeCollectionEventListener,
+				   PreCollectionRecreateEventListener,
+				   PostCollectionRecreateEventListener,
+				   PreCollectionRemoveEventListener,
+				   PostCollectionRemoveEventListener,
+				   PreCollectionUpdateEventListener,
+				   PostCollectionUpdateEventListener {
+
+	private static final Logger log = Logger.getLogger( AggregatedCollectionEventListener.class );
+
+	private final List<EventEntry> eventEntryList = new ArrayList<EventEntry>();
+
+	public void reset() {
+		eventEntryList.clear();
+	}
+
+	public List<EventEntry> getEventEntryList() {
+		return eventEntryList;
+	}
+
+	@Override
+	public void onInitializeCollection(InitializeCollectionEvent event) throws HibernateException {
+		addEvent( event );
+	}
+
+	protected void addEvent(AbstractCollectionEvent event) {
+		log.debugf( "Added collection event : %s", event );
+		eventEntryList.add( new EventEntry( event ) );
+	}
+
+
+	// recreate ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public void onPreRecreateCollection(PreCollectionRecreateEvent event) {
+		addEvent( event );
+	}
+
+	@Override
+	public void onPostRecreateCollection(PostCollectionRecreateEvent event) {
+		addEvent( event );
+	}
+
+
+	// remove ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public void onPreRemoveCollection(PreCollectionRemoveEvent event) {
+		addEvent( event );
+	}
+
+	@Override
+	public void onPostRemoveCollection(PostCollectionRemoveEvent event) {
+		addEvent( event );
+	}
+
+
+	// update ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
+		addEvent( event );
+	}
+
+	@Override
+	public void onPostUpdateCollection(PostCollectionUpdateEvent event) {
+		addEvent( event );
+	}
+
+	public static class EventEntry {
+		private final AbstractCollectionEvent event;
+		private final Serializable snapshotAtTimeOfEventHandling;
+
+		public EventEntry(AbstractCollectionEvent event) {
+			this.event = event;
+			// make a copy of the collection?
+			this.snapshotAtTimeOfEventHandling = event.getSession()
+					.getPersistenceContext()
+					.getCollectionEntry( event.getCollection() )
+					.getSnapshot();
+		}
+
+		public AbstractCollectionEvent getEvent() {
+			return event;
+		}
+
+		public Serializable getSnapshotAtTimeOfEventHandling() {
+			return snapshotAtTimeOfEventHandling;
+		}
+	}
+
+	public static class IntegratorImpl implements Integrator {
+		private AggregatedCollectionEventListener listener;
+
+		public AggregatedCollectionEventListener getListener() {
+			if ( listener == null ) {
+				throw new HibernateException( "Integrator not yet processed" );
+			}
+			return listener;
+		}
+
+		@Override
+		public void integrate(
+				Configuration configuration,
+				SessionFactoryImplementor sessionFactory,
+				SessionFactoryServiceRegistry serviceRegistry) {
+			integrate( serviceRegistry );
+		}
+
+		protected void integrate(SessionFactoryServiceRegistry serviceRegistry) {
+			if ( listener != null ) {
+				log.warn( "integrate called second time on testing collection listener Integrator (could be result of rebuilding SF on test failure)" );
+			}
+			listener = new AggregatedCollectionEventListener();
+
+			final EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
+			listenerRegistry.appendListeners( EventType.INIT_COLLECTION, listener );
+			listenerRegistry.appendListeners( EventType.PRE_COLLECTION_RECREATE, listener );
+			listenerRegistry.appendListeners( EventType.POST_COLLECTION_RECREATE, listener );
+			listenerRegistry.appendListeners( EventType.PRE_COLLECTION_REMOVE, listener );
+			listenerRegistry.appendListeners( EventType.POST_COLLECTION_REMOVE, listener );
+			listenerRegistry.appendListeners( EventType.PRE_COLLECTION_UPDATE, listener );
+			listenerRegistry.appendListeners( EventType.POST_COLLECTION_UPDATE, listener );
+		}
+
+
+		@Override
+		public void integrate(
+				MetadataImplementor metadata,
+				SessionFactoryImplementor sessionFactory,
+				SessionFactoryServiceRegistry serviceRegistry) {
+			integrate( serviceRegistry );
+		}
+
+		@Override
+		public void disintegrate(
+				SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
+			//To change body of implemented methods use File | Settings | File Templates.
+		}
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/Alias.java b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/Alias.java
new file mode 100644
index 0000000000..ecab38368a
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/Alias.java
@@ -0,0 +1,85 @@
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
+package org.hibernate.test.event.collection.detached;
+
+import javax.persistence.CascadeType;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.JoinTable;
+import javax.persistence.ManyToMany;
+import java.util.ArrayList;
+import java.util.List;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+public class Alias implements Identifiable {
+	private Integer id;
+	private String alias;
+	private List<Character> characters = new ArrayList<Character>();
+
+	public Alias() {
+	}
+
+	public Alias(Integer id, String alias) {
+		this.id = id;
+		this.alias = alias;
+	}
+
+	@Id
+	@Override
+	public Integer getId() {
+		return id;
+	}
+
+	public void setId(Integer id) {
+		this.id = id;
+	}
+
+	public String getAlias() {
+		return alias;
+	}
+
+	public void setAlias(String alias) {
+		this.alias = alias;
+	}
+
+	@ManyToMany( cascade = CascadeType.ALL )
+	@JoinTable( name = "CHARACTER_ALIAS" )
+//	@JoinTable(
+//			name = "CHARACTER_ALIAS",
+//			joinColumns = @JoinColumn(name="ALIAS_ID", referencedColumnName="ID"),
+//			inverseJoinColumns = @JoinColumn(name="CHARACTER_ID", referencedColumnName="ID")
+//	)
+	public List<Character> getCharacters() {
+		return characters;
+	}
+
+	public void setCharacters(List<Character> characters) {
+		this.characters = characters;
+	}
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/BadMergeHandlingTest.java b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/BadMergeHandlingTest.java
index d9a1e69216..103ad483cf 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/BadMergeHandlingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/BadMergeHandlingTest.java
@@ -1,174 +1,108 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.event.collection.detached;
 
-import javax.persistence.CascadeType;
-import javax.persistence.Entity;
-import javax.persistence.Id;
-import javax.persistence.JoinColumn;
-import javax.persistence.JoinTable;
-import javax.persistence.ManyToMany;
-import javax.persistence.ManyToOne;
-import javax.persistence.OneToMany;
-
-import java.util.ArrayList;
-import java.util.Collection;
 import java.util.List;
 
 import org.hibernate.Session;
 
 import org.junit.Test;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Steve Ebersole
  */
 @TestForIssue( jiraKey = "HHH-7928" )
 public class BadMergeHandlingTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
-		return new Class[] { Customer.class, Alias.class, CreditCard.class };
+		return new Class[] { Character.class, Alias.class };
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-7928" )
 	public void testMergeAndHold() {
 		Session s = openSession();
 		s.beginTransaction();
-		Customer paul = new Customer( 1, "Paul Atreides" );
+
+		Character paul = new Character( 1, "Paul Atreides" );
 		s.persist( paul );
+		Character paulo = new Character( 2, "Paulo Atreides" );
+		s.persist( paulo );
 
 		Alias alias1 = new Alias( 1, "Paul Muad'Dib" );
 		s.persist( alias1 );
 
 		Alias alias2 = new Alias( 2, "Usul" );
 		s.persist( alias2 );
 
 		Alias alias3 = new Alias( 3, "The Preacher" );
 		s.persist( alias3 );
 
-		CreditCard cc1 = new CreditCard( 1 );
-		s.persist( cc1 );
-
-		CreditCard cc2 = new CreditCard( 2 );
-		s.persist( cc2 );
-
 		s.getTransaction().commit();
 		s.close();
 
 		// set up relationships
 		s = openSession();
 		s.beginTransaction();
 
-		alias1.customers.add( paul );
+		// customer 1
+		alias1.getCharacters().add( paul );
 		s.merge( alias1 );
-		alias2.customers.add( paul );
+		alias2.getCharacters().add( paul );
 		s.merge( alias2 );
-		alias3.customers.add( paul );
+		alias3.getCharacters().add( paul );
 		s.merge( alias3 );
 
-		cc1.customer = paul;
-		s.merge( cc1 );
-		cc2.customer = paul;
-		s.merge( cc2 );
+		s.flush();
+
+		// customer 2
+		alias1.getCharacters().add( paulo );
+		s.merge( alias1 );
+		alias2.getCharacters().add( paulo );
+		s.merge( alias2 );
+		alias3.getCharacters().add( paulo );
+		s.merge( alias3 );
+		s.flush();
 
 		s.getTransaction().commit();
 		s.close();
 
 		// now try to read them back (I guess)
 		s = openSession();
 		s.beginTransaction();
-		List results = s.createQuery( "select c from Customer c join c.aliases a where a.alias = :aParam" )
+		List results = s.createQuery( "select c from Character c join c.aliases a where a.alias = :aParam" )
 				.setParameter( "aParam", "Usul" )
 				.list();
-		assertEquals( 1, results.size() );
+		assertEquals( 2, results.size() );
 		s.getTransaction().commit();
 		s.close();
 	}
 
-	@Entity( name="Customer" )
-	public static class Customer {
-		@Id
-		private Integer id;
-		private String name;
-		@ManyToMany( cascade= CascadeType.ALL, mappedBy="customers" )
-		private Collection<Alias> aliases = new ArrayList<Alias>();
-		@OneToMany(cascade=CascadeType.ALL, mappedBy="customer")
-		private Collection<CreditCard> creditCards = new ArrayList<CreditCard>();
-
-		public Customer() {
-		}
-
-		public Customer(Integer id, String name) {
-			this.id = id;
-			this.name = name;
-		}
-	}
-
-	@Entity( name="Alias" )
-	public static class Alias {
-		@Id
-		private Integer id;
-		private String alias;
-		@ManyToMany(cascade=CascadeType.ALL)
-		@JoinTable(name="FKS_ALIAS_CUSTOMER",
-				   joinColumns=
-				   @JoinColumn(
-						   name="FK_FOR_ALIAS_TABLE", referencedColumnName="ID"),
-				   inverseJoinColumns=
-				   @JoinColumn(
-						   name="FK_FOR_CUSTOMER_TABLE", referencedColumnName="ID")
-		)
-		private Collection<Customer> customers = new ArrayList<Customer>();
-
-		public Alias() {
-		}
-
-		public Alias(Integer id, String alias) {
-			this.id = id;
-			this.alias = alias;
-		}
-	}
-
-	@Entity( name="CreditCard" )
-	public static class CreditCard {
-		@Id
-		private Integer id;
-		@ManyToOne(cascade=CascadeType.ALL)
-		@JoinColumn (name="FK3_FOR_CUSTOMER_TABLE")
-		private Customer customer;
-
-		public CreditCard() {
-		}
-
-		public CreditCard(Integer id) {
-			this.id = id;
-		}
-	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/Character.java b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/Character.java
new file mode 100644
index 0000000000..8fb59524a7
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/Character.java
@@ -0,0 +1,81 @@
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
+package org.hibernate.test.event.collection.detached;
+
+import javax.persistence.CascadeType;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.ManyToMany;
+import java.util.ArrayList;
+import java.util.List;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+public class Character implements Identifiable {
+	private Integer id;
+	private String name;
+	private List<Alias> aliases = new ArrayList<Alias>();
+
+	public Character() {
+	}
+
+	public Character(Integer id, String name) {
+		this.id = id;
+		this.name = name;
+	}
+
+	@Id
+	@Override
+	public Integer getId() {
+		return id;
+	}
+
+	public void setId(Integer id) {
+		this.id = id;
+	}
+
+	public String getName() {
+		return name;
+	}
+
+	public void setName(String name) {
+		this.name = name;
+	}
+
+	@ManyToMany( cascade= CascadeType.ALL, mappedBy="characters" )
+	public List<Alias> getAliases() {
+		return aliases;
+	}
+
+	public void setAliases(List<Alias> aliases) {
+		this.aliases = aliases;
+	}
+
+	public void associateAlias(Alias alias) {
+		alias.getCharacters().add( this );
+		getAliases().add( alias );
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/Identifiable.java b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/Identifiable.java
new file mode 100644
index 0000000000..9db49a911e
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/Identifiable.java
@@ -0,0 +1,31 @@
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
+package org.hibernate.test.event.collection.detached;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface Identifiable {
+	public Integer getId();
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/MergeCollectionEventTest.java b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/MergeCollectionEventTest.java
new file mode 100644
index 0000000000..2cf8e8322e
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/MergeCollectionEventTest.java
@@ -0,0 +1,251 @@
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
+package org.hibernate.test.event.collection.detached;
+
+import java.io.Serializable;
+import java.util.ArrayList;
+import java.util.Collections;
+import java.util.List;
+
+import org.hibernate.Session;
+import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
+import org.hibernate.event.spi.AbstractCollectionEvent;
+import org.hibernate.event.spi.PostCollectionRecreateEvent;
+import org.hibernate.event.spi.PostCollectionUpdateEvent;
+import org.hibernate.event.spi.PreCollectionRecreateEvent;
+import org.hibernate.event.spi.PreCollectionRemoveEvent;
+import org.hibernate.event.spi.PreCollectionUpdateEvent;
+import org.hibernate.internal.util.SerializationHelper;
+
+import org.junit.Before;
+import org.junit.Test;
+
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertSame;
+
+/**
+ * @author Steve Ebersole
+ */
+@TestForIssue( jiraKey = "HHH-7928" )
+public class MergeCollectionEventTest extends BaseCoreFunctionalTestCase {
+
+	private AggregatedCollectionEventListener.IntegratorImpl collectionListenerIntegrator =
+			new AggregatedCollectionEventListener.IntegratorImpl();
+
+	@Before
+	public void resetListener() {
+		collectionListenerIntegrator.getListener().reset();
+	}
+
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] { Character.class, Alias.class };
+	}
+
+	@Override
+	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryBuilder builder) {
+		super.prepareBootstrapRegistryBuilder( builder );
+		builder.with( collectionListenerIntegrator );
+	}
+
+	@Override
+	protected void cleanupTestData() throws Exception {
+		Session s = openSession();
+		s.beginTransaction();
+		List<Alias> aliases = s.createQuery( "from Alias" ).list();
+		for ( Alias alias : aliases ) {
+			for ( Character character : alias.getCharacters() ) {
+				character.getAliases().clear();
+			}
+			alias.getCharacters().clear();
+		}
+		s.flush();
+		s.createQuery( "delete Alias" ).executeUpdate();
+		s.createQuery( "delete Character" ).executeUpdate();
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	public void testCollectionEventHandlingOnMerge() {
+		final AggregatedCollectionEventListener listener = collectionListenerIntegrator.getListener();
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// This first bit really is just preparing the entities.  There is generally no collection
+		// events of real interest during this part
+
+		Session s = openSession();
+		s.beginTransaction();
+		Character paul = new Character( 1, "Paul Atreides" );
+		s.save( paul );
+		s.getTransaction().commit();
+		s.close();
+
+		assertEquals( 2, listener.getEventEntryList().size() );
+		checkListener( 0, PreCollectionRecreateEvent.class, paul, Collections.EMPTY_LIST );
+		checkListener( 1, PostCollectionRecreateEvent.class, paul, Collections.EMPTY_LIST );
+
+		listener.reset();
+
+		s = openSession();
+		s.beginTransaction();
+		Character paulo = new Character( 2, "Paulo Atreides" );
+		s.save( paulo );
+		s.getTransaction().commit();
+		s.close();
+
+		assertEquals( 2, listener.getEventEntryList().size() );
+		checkListener( 0, PreCollectionRecreateEvent.class, paulo, Collections.EMPTY_LIST );
+		checkListener( 1, PostCollectionRecreateEvent.class, paulo, Collections.EMPTY_LIST );
+
+		listener.reset();
+
+		s = openSession();
+		s.beginTransaction();
+		Alias alias1 = new Alias( 1, "Paul Muad'Dib" );
+		s.save( alias1 );
+		s.getTransaction().commit();
+		s.close();
+
+		assertEquals( 2, listener.getEventEntryList().size() );
+		checkListener( 0, PreCollectionRecreateEvent.class, alias1, Collections.EMPTY_LIST );
+		checkListener( 1, PostCollectionRecreateEvent.class, alias1, Collections.EMPTY_LIST );
+
+		listener.reset();
+
+		s = openSession();
+		s.beginTransaction();
+		Alias alias2 = new Alias( 2, "Usul" );
+		s.save( alias2 );
+		s.getTransaction().commit();
+		s.close();
+
+		assertEquals( 2, listener.getEventEntryList().size() );
+		checkListener( 0, PreCollectionRecreateEvent.class, alias2, Collections.EMPTY_LIST );
+		checkListener( 1, PostCollectionRecreateEvent.class, alias2, Collections.EMPTY_LIST );
+
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// at this point we can start setting up the associations and checking collection events
+		// of "real interest"
+
+		listener.reset();
+
+		paul.associateAlias( alias1 );
+		paul.associateAlias( alias2 );
+
+		paulo.associateAlias( alias1 );
+		paulo.associateAlias( alias2 );
+
+		s = openSession();
+		s.beginTransaction();
+		s.merge( alias1 );
+
+		assertEquals( 0, listener.getEventEntryList().size() );
+
+		// this is where HHH-7928 (problem with HHH-6361 fix) shows up...
+		s.flush();
+
+		assertEquals( 8, listener.getEventEntryList().size() ); // 4 collections x 2 events per
+		checkListener( 0, PreCollectionUpdateEvent.class, alias1, Collections.EMPTY_LIST );
+		checkListener( 1, PostCollectionUpdateEvent.class, alias1, alias1.getCharacters() );
+		checkListener( 2, PreCollectionUpdateEvent.class, paul, Collections.EMPTY_LIST );
+		checkListener( 3, PostCollectionUpdateEvent.class, paul, paul.getAliases() );
+		checkListener( 4, PreCollectionUpdateEvent.class, alias2, Collections.EMPTY_LIST );
+		checkListener( 5, PostCollectionUpdateEvent.class, alias2, alias2.getCharacters() );
+		checkListener( 6, PreCollectionUpdateEvent.class, paulo, Collections.EMPTY_LIST );
+		checkListener( 7, PostCollectionUpdateEvent.class, paulo, paul.getAliases() );
+
+		List<Character> alias1CharactersSnapshot = copy( alias1.getCharacters() );
+		List<Character> alias2CharactersSnapshot = copy( alias2.getCharacters() );
+
+		listener.reset();
+
+		s.merge( alias2 );
+
+		assertEquals( 0, listener.getEventEntryList().size() );
+
+		s.flush();
+
+		assertEquals( 8, listener.getEventEntryList().size() ); // 4 collections x 2 events per
+		checkListener( 0, PreCollectionUpdateEvent.class, alias1, alias1CharactersSnapshot );
+		checkListener( 1, PostCollectionUpdateEvent.class, alias1, alias1CharactersSnapshot );
+//		checkListener( 2, PreCollectionUpdateEvent.class, paul, Collections.EMPTY_LIST );
+//		checkListener( 3, PostCollectionUpdateEvent.class, paul, paul.getAliases() );
+		checkListener( 4, PreCollectionUpdateEvent.class, alias2, Collections.EMPTY_LIST );
+		checkListener( 5, PostCollectionUpdateEvent.class, alias2, alias2.getCharacters() );
+//		checkListener( 6, PreCollectionUpdateEvent.class, paulo, Collections.EMPTY_LIST );
+//		checkListener( 7, PostCollectionUpdateEvent.class, paulo, paul.getAliases() );
+
+		s.getTransaction().commit();
+		s.close();
+
+//
+//		checkListener(listeners, listeners.getInitializeCollectionListener(),
+//					  mce, null, eventCount++);
+//		checkListener(listeners, listeners.getPreCollectionUpdateListener(),
+//					  mce, oldRefentities1, eventCount++);
+//		checkListener(listeners, listeners.getPostCollectionUpdateListener(),
+//					  mce, mce.getRefEntities1(), eventCount++);
+
+	}
+
+
+	protected void checkListener(
+			int eventIndex,
+			Class<? extends AbstractCollectionEvent> expectedEventType,
+			Identifiable expectedOwner,
+			List<? extends Identifiable> expectedCollectionEntrySnapshot) {
+		final AggregatedCollectionEventListener.EventEntry eventEntry
+				= collectionListenerIntegrator.getListener().getEventEntryList().get( eventIndex );
+		final AbstractCollectionEvent event = eventEntry.getEvent();
+
+		assertTyping( expectedEventType, event );
+
+// because of the merge graphs, the instances are likely different.  just base check on type and id
+//		assertEquals( expectedOwner, event.getAffectedOwnerOrNull() );
+		assertEquals( expectedOwner.getClass().getName(), event.getAffectedOwnerEntityName() );
+		assertEquals( expectedOwner.getId(), event.getAffectedOwnerIdOrNull() );
+
+		if ( event instanceof PreCollectionUpdateEvent
+				|| event instanceof PreCollectionRemoveEvent
+				|| event instanceof PostCollectionRecreateEvent ) {
+			List<Identifiable> snapshot = (List) eventEntry.getSnapshotAtTimeOfEventHandling();
+			for ( Identifiable element : snapshot ) {
+				assertEquals( expectedOwner.getClass().getName(), event.getAffectedOwnerEntityName() );
+				assertEquals( expectedOwner.getId(), event.getAffectedOwnerIdOrNull() );
+			}
+		}
+	}
+
+	private <T> List<T> copy(List<T> source) {
+		ArrayList<T> copy = new ArrayList<T>( source.size() );
+		copy.addAll( source );
+		return copy;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/MultipleCollectionListeners.java b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/MultipleCollectionListeners.java
index 333da328dc..6d10287564 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/MultipleCollectionListeners.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/event/collection/detached/MultipleCollectionListeners.java
@@ -1,265 +1,265 @@
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
 package org.hibernate.test.event.collection.detached;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.internal.DefaultInitializeCollectionEventListener;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.AbstractCollectionEvent;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.InitializeCollectionEvent;
 import org.hibernate.event.spi.PostCollectionRecreateEvent;
 import org.hibernate.event.spi.PostCollectionRecreateEventListener;
 import org.hibernate.event.spi.PostCollectionRemoveEvent;
 import org.hibernate.event.spi.PostCollectionRemoveEventListener;
 import org.hibernate.event.spi.PostCollectionUpdateEvent;
 import org.hibernate.event.spi.PostCollectionUpdateEventListener;
 import org.hibernate.event.spi.PreCollectionRecreateEvent;
 import org.hibernate.event.spi.PreCollectionRecreateEventListener;
 import org.hibernate.event.spi.PreCollectionRemoveEvent;
 import org.hibernate.event.spi.PreCollectionRemoveEventListener;
 import org.hibernate.event.spi.PreCollectionUpdateEvent;
 import org.hibernate.event.spi.PreCollectionUpdateEventListener;
 import org.jboss.logging.Logger;
 
 /**
  * Support listeners for Test HHH-6361: Collection events may contain wrong
  * stored snapshot after merging a detached entity into the persistencecontext.
  * 
  * @author Erik-Berndt Scheper
  */
 public class MultipleCollectionListeners {
 
 	private final Logger log = Logger.getLogger(MultipleCollectionListeners.class);
 
 	public interface Listener extends Serializable {
 		void addEvent(AbstractCollectionEvent event, Listener listener);
 	}
 
 	public static abstract class AbstractListener implements Listener {
 
 		private final MultipleCollectionListeners listeners;
 
 		protected AbstractListener(MultipleCollectionListeners listeners) {
 			this.listeners = listeners;
 		}
 
 		public void addEvent(AbstractCollectionEvent event, Listener listener) {
 			listeners.addEvent(event, listener);
 		}
 	}
 
 	public static class InitializeCollectionListener extends
 			DefaultInitializeCollectionEventListener implements Listener {
 		private final MultipleCollectionListeners listeners;
 
 		private InitializeCollectionListener(
 				MultipleCollectionListeners listeners) {
 			this.listeners = listeners;
 		}
 
 		public void onInitializeCollection(InitializeCollectionEvent event) {
 			super.onInitializeCollection(event);
 			addEvent(event, this);
 		}
 
 		public void addEvent(AbstractCollectionEvent event, Listener listener) {
 			listeners.addEvent(event, listener);
 		}
 	}
 
 	public static class PreCollectionRecreateListener extends AbstractListener
 			implements PreCollectionRecreateEventListener {
 		private PreCollectionRecreateListener(
 				MultipleCollectionListeners listeners) {
 			super(listeners);
 		}
 
 		public void onPreRecreateCollection(PreCollectionRecreateEvent event) {
 			addEvent(event, this);
 		}
 	}
 
 	public static class PostCollectionRecreateListener extends AbstractListener
 			implements PostCollectionRecreateEventListener {
 		private PostCollectionRecreateListener(
 				MultipleCollectionListeners listeners) {
 			super(listeners);
 		}
 
 		public void onPostRecreateCollection(PostCollectionRecreateEvent event) {
 			addEvent(event, this);
 		}
 	}
 
 	public static class PreCollectionRemoveListener extends AbstractListener
 			implements PreCollectionRemoveEventListener {
 		private PreCollectionRemoveListener(
 				MultipleCollectionListeners listeners) {
 			super(listeners);
 		}
 
 		public void onPreRemoveCollection(PreCollectionRemoveEvent event) {
 			addEvent(event, this);
 		}
 	}
 
 	public static class PostCollectionRemoveListener extends AbstractListener
 			implements PostCollectionRemoveEventListener {
 		private PostCollectionRemoveListener(
 				MultipleCollectionListeners listeners) {
 			super(listeners);
 		}
 
 		public void onPostRemoveCollection(PostCollectionRemoveEvent event) {
 			addEvent(event, this);
 		}
 	}
 
 	public static class PreCollectionUpdateListener extends AbstractListener
 			implements PreCollectionUpdateEventListener {
 		private PreCollectionUpdateListener(
 				MultipleCollectionListeners listeners) {
 			super(listeners);
 		}
 
 		public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
 			addEvent(event, this);
 		}
 	}
 
 	public static class PostCollectionUpdateListener extends AbstractListener
 			implements PostCollectionUpdateEventListener {
 		private PostCollectionUpdateListener(
 				MultipleCollectionListeners listeners) {
 			super(listeners);
 		}
 
 		public void onPostUpdateCollection(PostCollectionUpdateEvent event) {
 			addEvent(event, this);
 		}
 	}
 
 	private final PreCollectionRecreateListener preCollectionRecreateListener;
 	private final InitializeCollectionListener initializeCollectionListener;
 	private final PreCollectionRemoveListener preCollectionRemoveListener;
 	private final PreCollectionUpdateListener preCollectionUpdateListener;
 	private final PostCollectionRecreateListener postCollectionRecreateListener;
 	private final PostCollectionRemoveListener postCollectionRemoveListener;
 	private final PostCollectionUpdateListener postCollectionUpdateListener;
 
 	private List<Listener> listenersCalled = new ArrayList<Listener>();
 	private List<AbstractCollectionEvent> events = new ArrayList<AbstractCollectionEvent>();
 	private List<Serializable> snapshots = new ArrayList<Serializable>();
 
 	public MultipleCollectionListeners(SessionFactory sf) {
 		preCollectionRecreateListener = new PreCollectionRecreateListener(this);
 		initializeCollectionListener = new InitializeCollectionListener(this);
 		preCollectionRemoveListener = new PreCollectionRemoveListener(this);
 		preCollectionUpdateListener = new PreCollectionUpdateListener(this);
 		postCollectionRecreateListener = new PostCollectionRecreateListener(
 				this);
 		postCollectionRemoveListener = new PostCollectionRemoveListener(this);
 		postCollectionUpdateListener = new PostCollectionUpdateListener(this);
 		EventListenerRegistry registry = ( (SessionFactoryImplementor) sf ).getServiceRegistry().getService( EventListenerRegistry.class );
 		registry.setListeners( EventType.INIT_COLLECTION, initializeCollectionListener );
 
 		registry.setListeners( EventType.PRE_COLLECTION_RECREATE, preCollectionRecreateListener );
 		registry.setListeners( EventType.POST_COLLECTION_RECREATE, postCollectionRecreateListener );
 
 		registry.setListeners( EventType.PRE_COLLECTION_REMOVE, preCollectionRemoveListener );
 		registry.setListeners( EventType.POST_COLLECTION_REMOVE, postCollectionRemoveListener );
 
 		registry.setListeners( EventType.PRE_COLLECTION_UPDATE, preCollectionUpdateListener );
 		registry.setListeners( EventType.POST_COLLECTION_UPDATE, postCollectionUpdateListener );
 	}
 
 	public void addEvent(AbstractCollectionEvent event, Listener listener) {
 
+
 		CollectionEntry collectionEntry = event.getSession()
 				.getPersistenceContext()
 				.getCollectionEntry(event.getCollection());
-
 		Serializable snapshot = collectionEntry.getSnapshot();
 
 		log.debug("add Event: " + event.getClass() + "; listener = "
 				+ listener.getClass() + "; snapshot = " + snapshot);
 
 		listenersCalled.add(listener);
 		events.add(event);
 		snapshots.add(snapshot);
 	}
 
 	public List<Listener> getListenersCalled() {
 		return listenersCalled;
 	}
 
 	public List<AbstractCollectionEvent> getEvents() {
 		return events;
 	}
 
 	public List<Serializable> getSnapshots() {
 		return snapshots;
 	}
 
 	public void clear() {
 		listenersCalled.clear();
 		events.clear();
 		snapshots.clear();
 	}
 
 	public PreCollectionRecreateListener getPreCollectionRecreateListener() {
 		return preCollectionRecreateListener;
 	}
 
 	public InitializeCollectionListener getInitializeCollectionListener() {
 		return initializeCollectionListener;
 	}
 
 	public PreCollectionRemoveListener getPreCollectionRemoveListener() {
 		return preCollectionRemoveListener;
 	}
 
 	public PreCollectionUpdateListener getPreCollectionUpdateListener() {
 		return preCollectionUpdateListener;
 	}
 
 	public PostCollectionRecreateListener getPostCollectionRecreateListener() {
 		return postCollectionRecreateListener;
 	}
 
 	public PostCollectionRemoveListener getPostCollectionRemoveListener() {
 		return postCollectionRemoveListener;
 	}
 
 	public PostCollectionUpdateListener getPostCollectionUpdateListener() {
 		return postCollectionUpdateListener;
 	}
 }
