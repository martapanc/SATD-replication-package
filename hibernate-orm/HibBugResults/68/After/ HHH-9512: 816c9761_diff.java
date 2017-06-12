diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/MergeContext.java b/hibernate-core/src/main/java/org/hibernate/event/internal/MergeContext.java
index 59e7e45bd8..950387da2c 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/MergeContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/MergeContext.java
@@ -1,383 +1,394 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.event.internal;
 
 import java.util.Collection;
 import java.util.Collections;
 import java.util.IdentityHashMap;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.event.spi.EntityCopyObserver;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.pretty.MessageHelper;
 
 import org.jboss.logging.Logger;
 
 /**
  * MergeContext is a Map implementation that is intended to be used by a merge
  * event listener to keep track of each entity being merged and their corresponding
  * managed result. Entities to be merged may to be added to the MergeContext beforeQuery
  * the merge operation has cascaded to that entity.
  *
  * "Merge entity" and "mergeEntity" method parameter refer to an entity that is (or will be)
  * merged via {@link org.hibernate.event.spi.EventSource#merge(Object mergeEntity)}.
  *
  * "Managed entity" and "managedEntity" method parameter refer to the managed entity that is
  * the result of merging an entity.
  *
  * A merge entity can be transient, detached, or managed. If it is managed, then it must be
  * the same as its associated entity result.
  *
  * If {@link #put(Object mergeEntity, Object managedEntity)} is called, and this
  * MergeContext already contains an entry with a different entity as the key, but
  * with the same (managedEntity) value, this means that multiple entity representations
  * for the same persistent entity are being merged. If this happens,
  * {@link org.hibernate.event.spi.EntityCopyObserver#entityCopyDetected(
  * Object managedEntity, Object mergeEntity1, Object mergeEntity2, org.hibernate.event.spi.EventSource)}
  * will be called. It is up to that method to determine the property course of
  * action for this situation.
  *
  * There are several restrictions.
  * <ul>
  *     <li>Methods that return collections (e.g., {@link #keySet()},
  *          {@link #values()}, {@link #entrySet()}) return an
  *          unnmodifiable view of the collection;</li>
  *     <li>If {@link #put(Object mergeEntity, Object) managedEntity} or
  *         {@link #put(Object mergeEntity, Object managedEntity, boolean isOperatedOn)}
  *         is executed and this MergeMap already contains a cross-reference for
  *         <code>mergeEntity</code>, then <code>managedEntity</code> must be the
  *         same as what is already associated with <code>mergeEntity</code> in this
  *         MergeContext.
  *     </li>
  *     <li>If {@link #putAll(Map map)} is executed, the previous restriction
  *         applies to each entry in the Map;</li>
  *     <li>The {@link #remove(Object)} operation is not supported;
  *         The only way to remove data from a MergeContext is by calling
  *         {@link #clear()};</li>
  *      <li>the Map returned by {@link #invertMap()} will only contain the
  *          managed-to-merge entity cross-reference to its "newest"
  *          (most recently added) merge entity.</li>
  * </ul>
  * <p>
  * The following method is intended to be used by a merge event listener (and other
  * classes) in the same package to add a merge entity and its corresponding
  * managed entity to a MergeContext and indicate if the merge operation is
  * being performed on the merge entity yet.<p/>
  * {@link MergeContext#put(Object mergeEntity, Object managedEntity, boolean isOperatedOn)}
  * <p/>
  * The following method is intended to be used by a merge event listener (and other
  * classes) in the same package to indicate whether the merge operation is being
  * performed on a merge entity already in the MergeContext:
  * {@link MergeContext#setOperatedOn(Object mergeEntity, boolean isOperatedOn)
  *
  * @author Gail Badner
  */
 class MergeContext implements Map {
 	private static final Logger LOG = Logger.getLogger( MergeContext.class );
 
 	private final EventSource session;
 	private final EntityCopyObserver entityCopyObserver;
 
 	private Map<Object,Object> mergeToManagedEntityXref = new IdentityHashMap<Object,Object>(10);
 		// key is an entity to be merged;
 		// value is the associated managed entity (result) in the persistence context.
 
 	private Map<Object,Object> managedToMergeEntityXref = new IdentityHashMap<Object,Object>( 10 );
 		// maintains the inverse of the mergeToManagedEntityXref for performance reasons.
 		// key is the managed entity result in the persistence context.
 		// value is the associated entity to be merged; if multiple
 		// representations of the same persistent entity are added to the MergeContext,
 		// value will be the most recently added merge entity that is
 		// associated with the managed entity.
 
 	// TODO: merge mergeEntityToOperatedOnFlagMap into mergeToManagedEntityXref, since they have the same key.
 	//       need to check if this would hurt performance.
 	private Map<Object,Boolean> mergeEntityToOperatedOnFlagMap = new IdentityHashMap<Object,Boolean>( 10 );
 	    // key is a merge entity;
 	    // value is a flag indicating if the merge entity is currently in the merge process.
 
 	MergeContext(EventSource session, EntityCopyObserver entityCopyObserver){
 		this.session = session;
 		this.entityCopyObserver = entityCopyObserver;
 	}
 
 	/**
 	 * Clears the MergeContext.
 	 */
 	public void clear() {
 		mergeToManagedEntityXref.clear();
 		managedToMergeEntityXref.clear();
 		mergeEntityToOperatedOnFlagMap.clear();
 	}
 
 	/**
 	 * Returns true if this MergeContext contains a cross-reference for the specified merge entity
 	 * to a managed entity result.
 	 *
 	 * @param mergeEntity must be non-null
 	 * @return true if this MergeContext contains a cross-reference for the specified merge entity
 	 * @throws NullPointerException if mergeEntity is null
 	 */
 	public boolean containsKey(Object mergeEntity) {
 		if ( mergeEntity == null ) {
 			throw new NullPointerException( "null entities are not supported by " + getClass().getName() );
 		}
 		return mergeToManagedEntityXref.containsKey( mergeEntity );
 	}
 
 	/**
 	 * Returns true if this MergeContext contains a cross-reference from the specified managed entity
 	 * to a merge entity.
 	 * @param managedEntity must be non-null
 	 * @return true if this MergeContext contains a cross-reference from the specified managed entity
 	 * to a merge entity
 	 * @throws NullPointerException if managedEntity is null
 	 */
 	public boolean containsValue(Object managedEntity) {
 		if ( managedEntity == null ) {
 			throw new NullPointerException( "null copies are not supported by " + getClass().getName() );
 		}
 		return managedToMergeEntityXref.containsKey( managedEntity );
 	}
 
 	/**
 	 * Returns an unmodifiable set view of the merge-to-managed entity cross-references contained in this MergeContext.
 	 * @return an unmodifiable set view of the merge-to-managed entity cross-references contained in this MergeContext
 	 *
 	 * @see {@link Collections#unmodifiableSet(java.util.Set)}
 	 */
 	public Set entrySet() {
 		return Collections.unmodifiableSet( mergeToManagedEntityXref.entrySet() );
 	}
 
 	/**
 	 * Returns the managed entity associated with the specified merge Entity.
 	 * @param mergeEntity the merge entity; must be non-null
 	 * @return  the managed entity associated with the specified merge Entity
 	 * @throws NullPointerException if mergeEntity is null
 	 */
 	public Object get(Object mergeEntity) {
 		if ( mergeEntity == null ) {
 			throw new NullPointerException( "null entities are not supported by " + getClass().getName() );
 		}
 		return mergeToManagedEntityXref.get( mergeEntity );
 	}
 
 	/**
 	 * Returns true if this MergeContext contains no merge-to-managed entity cross-references.
 	 * @return true if this MergeContext contains no merge-to-managed entity cross-references.
 	 */
 	public boolean isEmpty() {
 		return mergeToManagedEntityXref.isEmpty();
 	}
 
 	/**
 	 * Returns an unmodifiable set view of the merge entities contained in this MergeContext
 	 * @return an unmodifiable set view of the merge entities contained in this MergeContext
 	 *
 	 * @see {@link Collections#unmodifiableSet(java.util.Set)}
 	 */
 	public Set keySet() {
 		return Collections.unmodifiableSet( mergeToManagedEntityXref.keySet() );
 	}
 
 	/**
 	 * Associates the specified merge entity with the specified managed entity result in this MergeContext.
 	 * If this MergeContext already contains a cross-reference for <code>mergeEntity</code> when this
 	 * method is called, then <code>managedEntity</code> must be the same as what is already associated
 	 * with <code>mergeEntity</code>.
 	 * <p/>
 	 * This method assumes that the merge process is not yet operating on <code>mergeEntity</code>.
 	 * Later when <code>mergeEntity</code> enters the merge process, {@link #setOperatedOn(Object, boolean)}
 	 * should be called.
 	 * <p/>
 	 * @param mergeEntity the merge entity; must be non-null
 	 * @param managedEntity the managed entity result; must be non-null
 	 * @return previous managed entity associated with specified merge entity, or null if
 	 * there was no mapping for mergeEntity.
 	 * @throws NullPointerException if mergeEntity or managedEntity is null
 	 * @throws IllegalArgumentException if <code>managedEntity</code> is not the same as the previous
 	 * managed entity associated with <code>merge entity</code>
 	 * @throws IllegalStateException if internal cross-references are out of sync,
 	 */
 	public Object put(Object mergeEntity, Object managedEntity) {
 		return put( mergeEntity, managedEntity, Boolean.FALSE );
 	}
 
 	/**
 	 * Associates the specified merge entity with the specified managed entity in this MergeContext.
 	 * If this MergeContext already contains a cross-reference for <code>mergeEntity</code> when this
 	 * method is called, then <code>managedEntity</code> must be the same as what is already associated
 	 * with <code>mergeEntity</code>.
 	 *
 	 * @param mergeEntity the mergge entity; must be non-null
 	 * @param managedEntity the managed entity; must be non-null
 	 * @param isOperatedOn indicates if the merge operation is performed on the mergeEntity.
 	 *
 	 * @return previous managed entity associated with specified merge entity, or null if
 	 * there was no mapping for mergeEntity.
 	 * @throws NullPointerException if mergeEntity or managedEntity is null
 	 * @throws IllegalArgumentException if <code>managedEntity</code> is not the same as the previous
 	 * managed entity associated with <code>mergeEntity</code>
 	 * @throws IllegalStateException if internal cross-references are out of sync,
 	 */
 	/* package-private */ Object put(Object mergeEntity, Object managedEntity, boolean isOperatedOn) {
 		if ( mergeEntity == null || managedEntity == null ) {
 			throw new NullPointerException( "null merge and managed entities are not supported by " + getClass().getName() );
 		}
 
+		// Detect invalid 'managed entity' -> 'managed entity' mappings where key != value
+		if ( managedToMergeEntityXref.containsKey( mergeEntity ) ) {
+			if ( managedToMergeEntityXref.get( mergeEntity ) != mergeEntity ) {
+				throw new IllegalStateException(
+						"MergeContext#attempt to create managed -> managed mapping with different entities: "
+								+ printEntity( mergeEntity ) + "; " + printEntity(
+								managedEntity )
+				);
+			}
+		}
+
 		Object oldManagedEntity = mergeToManagedEntityXref.put( mergeEntity, managedEntity );
 		Boolean oldOperatedOn = mergeEntityToOperatedOnFlagMap.put( mergeEntity, isOperatedOn );
 		// If managedEntity already corresponds with a different merge entity, that means
 		// that there are multiple entities being merged that correspond with managedEntity.
 		// In the following, oldMergeEntity will be replaced with mergeEntity in managedToMergeEntityXref.
 		Object oldMergeEntity = managedToMergeEntityXref.put( managedEntity, mergeEntity );
 
 		if ( oldManagedEntity == null ) {
 			// this is a new mapping for mergeEntity in mergeToManagedEntityXref
 			if  ( oldMergeEntity != null ) {
 				// oldMergeEntity was a different merge entity with the same corresponding managed entity;
 				entityCopyObserver.entityCopyDetected(
 						managedEntity,
 						mergeEntity,
 						oldMergeEntity,
 						session
 				);
 			}
 			if ( oldOperatedOn != null ) {
 				throw new IllegalStateException(
 						"MergeContext#mergeEntityToOperatedOnFlagMap contains an merge entity " + printEntity( mergeEntity )
 								+ ", but MergeContext#mergeToManagedEntityXref does not."
 				);
 			}
 		}
 		else {
 			// mergeEntity was already mapped in mergeToManagedEntityXref
 			if ( oldManagedEntity != managedEntity ) {
 				throw new IllegalArgumentException(
 						"Error occurred while storing a merge Entity " + printEntity( mergeEntity )
 								+ ". It was previously associated with managed entity " + printEntity( oldManagedEntity )
 								+ ". Attempted to replace managed entity with " + printEntity( managedEntity )
 				);
 			}
 			if ( oldOperatedOn == null ) {
 				throw new IllegalStateException(
 						"MergeContext#mergeToManagedEntityXref contained an mergeEntity " + printEntity( mergeEntity )
 								+ ", but MergeContext#mergeEntityToOperatedOnFlagMap did not."
 				);
 			}
 		}
 
 		return oldManagedEntity;
 	}
 
 	/**
 	 * Copies all of the mappings from the specified Map to this MergeContext.
 	 * The key and value for each entry in <code>map</code> is subject to the same
 	 * restrictions as {@link #put(Object mergeEntity, Object managedEntity)}.
 	 *
 	 * This method assumes that the merge process is not yet operating on any merge entity
 	 *
 	 * @param map keys and values must be non-null
 	 * @throws NullPointerException if any key or value is null
 	 * @throws IllegalArgumentException if a key in <code>map</code> was already in this MergeContext
 	 * but associated value in <code>map</code> is different from the previous value in this MergeContext.
 	 * @throws IllegalStateException if internal cross-references are out of sync,
 	 */
 	public void putAll(Map map) {
 		for ( Object o : map.entrySet() ) {
 			Entry entry = (Entry) o;
 			put( entry.getKey(), entry.getValue() );
 		}
 	}
 
 	/**
 	 * The remove operation is not supported.
 	 * @param mergeEntity the merge entity.
 	 * @throws UnsupportedOperationException if called.
 	 */
 	public Object remove(Object mergeEntity) {
 		throw new UnsupportedOperationException(
 				String.format( "Operation not supported: %s.remove()", getClass().getName() )
 		);
 	}
 
 	/**
 	 * Returns the number of merge-to-managed entity cross-references in this MergeContext
 	 * @return the number of merge-to-managed entity cross-references in this MergeContext
 	 */
 	public int size() {
 		return mergeToManagedEntityXref.size();
 	}
 
 	/**
 	 * Returns an unmodifiable Set view of managed entities contained in this MergeContext.
 	 * @return an unmodifiable Set view of managed entities contained in this MergeContext
 	 *
 	 * @see {@link Collections#unmodifiableSet(java.util.Set)}
 	 */
 	public Collection values() {
 		return Collections.unmodifiableSet( managedToMergeEntityXref.keySet() );
 	}
 
 	/**
 	 * Returns true if the listener is performing the merge operation on the specified merge entity.
 	 * @param mergeEntity the merge entity; must be non-null
 	 * @return true if the listener is performing the merge operation on the specified merge entity;
 	 * false, if there is no mapping for mergeEntity.
 	 * @throws NullPointerException if mergeEntity is null
 	 */
 	public boolean isOperatedOn(Object mergeEntity) {
 		if ( mergeEntity == null ) {
 			throw new NullPointerException( "null merge entities are not supported by " + getClass().getName() );
 		}
 		final Boolean isOperatedOn = mergeEntityToOperatedOnFlagMap.get( mergeEntity );
 		return isOperatedOn == null ? false : isOperatedOn;
 	}
 
 	/**
 	 * Set flag to indicate if the listener is performing the merge operation on the specified merge entity.
 	 * @param mergeEntity must be non-null and this MergeContext must contain a cross-reference for mergeEntity
 	 *                       to a managed entity
 	 * @throws NullPointerException if mergeEntity is null
 	 * @throws IllegalStateException if this MergeContext does not contain a a cross-reference for mergeEntity
 	 */
 	/* package-private */ void setOperatedOn(Object mergeEntity, boolean isOperatedOn) {
 		if ( mergeEntity == null ) {
 			throw new NullPointerException( "null entities are not supported by " + getClass().getName() );
 		}
 		if ( ! mergeEntityToOperatedOnFlagMap.containsKey( mergeEntity ) ||
 			! mergeToManagedEntityXref.containsKey( mergeEntity ) ) {
 			throw new IllegalStateException( "called MergeContext#setOperatedOn() for mergeEntity not found in MergeContext" );
 		}
 		mergeEntityToOperatedOnFlagMap.put( mergeEntity, isOperatedOn );
 	}
 
 	/**
 	 * Returns an unmodifiable map view of the managed-to-merge entity
 	 * cross-references.
 	 *
 	 * The returned Map will contain a cross-reference from each managed entity
 	 * to the most recently associated merge entity that was most recently put in the MergeContext.
 	 *
 	 * @return an unmodifiable map view of the managed-to-merge entity cross-references.
 	 *
 	 * @see {@link Collections#unmodifiableMap(java.util.Map)}
 	 */
 	public Map invertMap() {
 		return Collections.unmodifiableMap( managedToMergeEntityXref );
 	}
 
 	private String printEntity(Object entity) {
 		if ( session.getPersistenceContext().getEntry( entity ) != null ) {
 			return MessageHelper.infoString( session.getEntityName( entity ), session.getIdentifier( entity ) );
 		}
 		// Entity was not found in current persistence context. Use Object#toString() method.
 		return "[" + entity + "]";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
index 9cbfa6b7dd..84c0e012a4 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
@@ -1,690 +1,697 @@
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
+		if ( cached == null ) {
+			// Avoid creation of invalid managed -> managed mapping in copyCache when traversing
+			// cascade loop (@OneToMany(cascade=ALL) with associated @ManyToOne(cascade=ALL)) in entity graph
+			if ( copyCache.containsValue( original ) ) {
+				cached = original;
+			}
+		}
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
 
 		EntityPersister persister = getAssociatedEntityPersister( factory );
 		StringBuilder result = new StringBuilder().append( associatedEntityName );
 
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/CascadeManagedAndTransient.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/CascadeManagedAndTransient.hbm.xml
new file mode 100644
index 0000000000..975c333814
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/CascadeManagedAndTransient.hbm.xml
@@ -0,0 +1,121 @@
+<?xml version="1.0"?>
+<!--
+~ Hibernate, Relational Persistence for Idiomatic Java
+~
+~ License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+~ See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+-->
+<!DOCTYPE hibernate-mapping SYSTEM "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd" >
+
+<hibernate-mapping package="org.hibernate.test.cascade.circle">
+
+    <class name="Route" table="HB_Route">
+
+        <id name="routeID" type="long"><generator class="native"/></id>
+        <version name="version" column="VERS" type="long" />
+
+        <property name="name" type="string" not-null="true"/>
+
+        <set name="nodes" inverse="true" cascade="persist,merge,refresh,save-update">
+            <key column="routeID"/>
+            <one-to-many class="Node"/>
+        </set>
+        <set name="vehicles" inverse="true" cascade="persist,merge,refresh,save-update">
+            <key column="routeID"/>
+            <one-to-many class="Vehicle"/>
+        </set>
+    </class>
+
+    <class name="Tour" table="HB_Tour">
+
+        <id name="tourID" type="long"><generator class="native"/></id>
+        <version name="version" column="VERS" type="long" />
+
+        <property name="name" type="string" not-null="true"/>
+
+        <set name="nodes" inverse="true" lazy="true" cascade="merge,refresh,persist,save-update">
+            <key column="tourID"/>
+            <one-to-many class="Node"/>
+        </set>
+    </class>
+
+    <class name="Transport" table="HB_Transport">
+
+        <id name="transportID" type="long"><generator class="native"/></id>
+        <version name="version" column="VERS" type="long" />
+
+        <property name="name" type="string" not-null="true"/>
+
+        <many-to-one name="pickupNode"
+                     column="pickupNodeID"
+                     unique="true"
+                     not-null="true"
+                     cascade="merge,refresh,persist,save-update"
+                     lazy="false"/>
+
+        <many-to-one name="deliveryNode"
+                     column="deliveryNodeID"
+                     unique="true"
+                     not-null="true"
+                     cascade="merge,refresh,persist,save-update"
+                     lazy="false"/>
+
+        <many-to-one name="vehicle"
+                     column="vehicleID"
+                     unique="false"
+                     not-null="true"
+                     cascade="merge,refresh,persist,save-update"
+                     lazy="false"/>
+    </class>
+
+    <class name="Vehicle" table="HB_Vehicle">
+        <id name="vehicleID" type="long"><generator class="native"/></id>
+        <version name="version" column="VERS" type="long" />
+
+        <property name="name"/>
+        <set name="transports" inverse="false" lazy="true" cascade="merge,refresh,persist,save-update">
+            <key column="vehicleID"/>
+            <one-to-many class="Transport" not-found="exception"/>
+        </set>
+        <many-to-one name="route"
+                     column="routeID"
+                     unique="false"
+                     not-null="false"
+                     cascade="merge,refresh,persist,save-update,save-update"
+                     lazy="false"/>
+    </class>
+
+
+    <class name="Node" table="HB_Node">
+
+        <id name="nodeID" type="long"><generator class="native"/></id>
+        <version name="version" column="VERS" type="long" />
+
+        <property name="name" type="string" not-null="true"/>
+
+        <set name="deliveryTransports" inverse="true" lazy="true" cascade="merge,refresh,persist,save-update">
+            <key column="deliveryNodeID"/>
+            <one-to-many class="Transport"/>
+        </set>
+
+        <set name="pickupTransports" inverse="true" lazy="true" cascade="merge,refresh,persist,save-update">
+            <key column="pickupNodeID"/>
+            <one-to-many class="Transport"/>
+        </set>
+
+        <many-to-one name="route"
+                     column="routeID"
+                     unique="false"
+                     not-null="true"
+                     cascade="merge,refresh,persist,save-update"
+                     lazy="false"/>
+
+        <many-to-one name="tour"
+                     column="tourID"
+                     unique="false"
+                     not-null="true"
+                     cascade="merge,refresh,persist,save-update"
+                     lazy="false"/>
+    </class>
+
+</hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/CascadeManagedAndTransientTest.java b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/CascadeManagedAndTransientTest.java
new file mode 100644
index 0000000000..023bf5f899
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/CascadeManagedAndTransientTest.java
@@ -0,0 +1,164 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.cascade.circle;
+
+import org.hibernate.Session;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.cfg.Environment;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.junit.Test;
+import static org.junit.Assert.*;
+
+import java.util.ArrayList;
+import java.util.Arrays;
+import java.util.HashSet;
+import java.util.List;
+
+/**
+ * The test case uses the following model:
+ *
+ *                          <-    ->
+ *                      -- (N : 0,1) -- Tour
+ *                      |    <-   ->
+ *                      | -- (1 : N) -- (pickup) ----
+ *          <-   ->     | |                          |
+ * Route -- (1 : N) -- Node                      Transport
+ *                      |  <-   ->                |
+ *                      -- (1 : N) -- (delivery) --
+ *
+ *  Arrows indicate the direction of cascade-merge, cascade-save, cascade-refresh and cascade-save-or-update
+ *
+ * It reproduces the following issues:
+ * https://hibernate.atlassian.net/browse/HHH-9512
+ * <p/>
+ * This tests that cascades are done properly from each entity.
+ *
+ * @author Alex Belyaev (based on code by Pavol Zibrita and Gail Badner)
+ */
+public class CascadeManagedAndTransientTest extends BaseCoreFunctionalTestCase  {
+
+    @Override
+    public String[] getMappings() {
+        return new String[] {
+                "cascade/circle/CascadeManagedAndTransient.hbm.xml"
+        };
+    }
+
+    @Override
+    public void configure(Configuration cfg) {
+        super.configure( cfg );
+        cfg.setProperty( Environment.CHECK_NULLABILITY, "true" );
+    }
+
+    @Override
+    protected void cleanupTest() {
+        Session s = openSession();
+        s.beginTransaction();
+        s.createQuery( "delete from Transport" );
+        s.createQuery( "delete from Tour" );
+        s.createQuery( "delete from Node" );
+        s.createQuery( "delete from Route" );
+        s.createQuery( "delete from Vehicle" );
+    }
+
+    private void checkNewVehicle(Vehicle newVehicle) {
+        assertEquals("Bus", newVehicle.getName());
+        assertEquals(1, newVehicle.getTransports().size());
+        Transport t = (Transport) newVehicle.getTransports().iterator().next();
+        assertEquals("Transport 2 -> 3", t.getName());
+        assertEquals("Node 2", t.getPickupNode().getName());
+        assertEquals("Node 3", t.getDeliveryNode().getName());
+    }
+
+    @Test
+    public void testAttachedChildInMerge() {
+        fillInitialData();
+
+        Session s = openSession();
+        s.beginTransaction();
+
+        Route route = (Route) s.createQuery("FROM Route WHERE name = :name").setString("name", "Route 1").uniqueResult();
+        Node n2 = (Node) s.createQuery("FROM Node WHERE name = :name").setString("name", "Node 2").uniqueResult();
+        Node n3 = (Node) s.createQuery("FROM Node WHERE name = :name").setString("name", "Node 3").uniqueResult();
+
+        Vehicle vehicle = new Vehicle();
+        vehicle.setName("Bus");
+        vehicle.setRoute(route);
+
+        Transport $2to3 = new Transport();
+        $2to3.setName("Transport 2 -> 3");
+        $2to3.setPickupNode(n2); n2.getPickupTransports().add($2to3);
+        $2to3.setDeliveryNode(n3); n3.getDeliveryTransports().add($2to3);
+        $2to3.setVehicle(vehicle);
+
+        vehicle.setTransports(new HashSet<Transport>(Arrays.asList($2to3)));
+
+        // Try to save graph of transient entities (vehicle, transport) which contains attached entities (node2, node3)
+        Vehicle managedVehicle = (Vehicle) s.merge(vehicle);
+        checkNewVehicle(managedVehicle);
+
+        s.flush();
+        s.clear();
+
+        assertEquals(3, s.createQuery("FROM Transport").list().size());
+        assertEquals(2, s.createQuery("FROM Vehicle").list().size());
+        assertEquals(4, s.createQuery("FROM Node").list().size());
+
+        Vehicle newVehicle = (Vehicle) s.createQuery("FROM Vehicle WHERE name = :name").setParameter("name", "Bus").uniqueResult();
+        checkNewVehicle(newVehicle);
+
+        s.getTransaction().commit();
+        s.close();
+    }
+
+    private void fillInitialData() {
+        Tour tour = new Tour();
+        tour.setName("Tour 1");
+
+        Route route = new Route();
+        route.setName("Route 1");
+
+        ArrayList<Node> nodes = new ArrayList<Node>();
+        for (int i = 0; i < 4; i++) {
+            Node n = new Node();
+            n.setName("Node " + i);
+            n.setTour(tour);
+            n.setRoute(route);
+            nodes.add(n);
+        }
+
+        tour.setNodes(new HashSet<Node>(nodes));
+        route.setNodes(new HashSet<Node>(Arrays.asList(nodes.get(0), nodes.get(1), nodes.get(2))));
+
+        Vehicle vehicle = new Vehicle();
+        vehicle.setName("Car");
+        route.setVehicles(new HashSet<Vehicle>(Arrays.asList(vehicle)));
+        vehicle.setRoute(route);
+
+        Transport $0to1 = new Transport();
+        $0to1.setName("Transport 0 -> 1");
+        $0to1.setPickupNode(nodes.get(0));
+        $0to1.setDeliveryNode(nodes.get(1));
+        $0to1.setVehicle(vehicle);
+
+        Transport $1to2 = new Transport();
+        $1to2.setName("Transport 1 -> 2");
+        $1to2.setPickupNode(nodes.get(1));
+        $1to2.setDeliveryNode(nodes.get(2));
+        $1to2.setVehicle(vehicle);
+
+        vehicle.setTransports(new HashSet<Transport>(Arrays.asList($0to1, $1to2)));
+
+        Session s = openSession();
+        s.beginTransaction();
+
+        s.persist(tour);
+
+        s.getTransaction().commit();
+        s.close();
+    }
+}
