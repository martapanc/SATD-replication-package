diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
index ea284898c8..b9578fb10e 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
@@ -1,709 +1,757 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.tuple.entity;
 
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
+import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PersistEvent;
 import org.hibernate.event.spi.PersistEventListener;
 import org.hibernate.id.Assigned;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
+import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.property.access.spi.Getter;
 import org.hibernate.property.access.spi.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.tuple.Instantiator;
 import org.hibernate.tuple.NonIdentifierAttribute;
+import org.hibernate.type.AssociationType;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 import static org.hibernate.internal.CoreLogging.messageLogger;
 
 
 /**
  * Support for tuplizers relating to entities.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public abstract class AbstractEntityTuplizer implements EntityTuplizer {
 	private static final CoreMessageLogger LOG = messageLogger( AbstractEntityTuplizer.class );
 
 	//TODO: currently keeps Getters and Setters (instead of PropertyAccessors) because of the way getGetter() and getSetter() are implemented currently; yuck!
 
 	private final EntityMetamodel entityMetamodel;
 
 	private final Getter idGetter;
 	private final Setter idSetter;
 
 	protected final Getter[] getters;
 	protected final Setter[] setters;
 	protected final int propertySpan;
 	protected final boolean hasCustomAccessors;
 	private final Instantiator instantiator;
 	private final ProxyFactory proxyFactory;
 	private final CompositeType identifierMapperType;
 
 	public Type getIdentifierMapperType() {
 		return identifierMapperType;
 	}
 
 	/**
 	 * Build an appropriate Getter for the given property.
 	 *
 	 * @param mappedProperty The property to be accessed via the built Getter.
 	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
 	 *
 	 * @return An appropriate Getter instance.
 	 */
 	protected abstract Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity);
 
 	/**
 	 * Build an appropriate Setter for the given property.
 	 *
 	 * @param mappedProperty The property to be accessed via the built Setter.
 	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
 	 *
 	 * @return An appropriate Setter instance.
 	 */
 	protected abstract Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity);
 
 	/**
 	 * Build an appropriate Instantiator for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 *
 	 * @return An appropriate Instantiator instance.
 	 */
 	protected abstract Instantiator buildInstantiator(EntityMetamodel entityMetamodel, PersistentClass mappingInfo);
 
 	/**
 	 * Build an appropriate ProxyFactory for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @param idGetter The constructed Getter relating to the entity's id property.
 	 * @param idSetter The constructed Setter relating to the entity's id property.
 	 *
 	 * @return An appropriate ProxyFactory instance.
 	 */
 	protected abstract ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter);
 
 	/**
 	 * Constructs a new AbstractEntityTuplizer instance.
 	 *
 	 * @param entityMetamodel The "interpreted" information relating to the mapped entity.
 	 * @param mappingInfo The parsed "raw" mapping data relating to the given entity.
 	 */
 	public AbstractEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappingInfo) {
 		this.entityMetamodel = entityMetamodel;
 
 		if ( !entityMetamodel.getIdentifierProperty().isVirtual() ) {
 			idGetter = buildPropertyGetter( mappingInfo.getIdentifierProperty(), mappingInfo );
 			idSetter = buildPropertySetter( mappingInfo.getIdentifierProperty(), mappingInfo );
 		}
 		else {
 			idGetter = null;
 			idSetter = null;
 		}
 
 		propertySpan = entityMetamodel.getPropertySpan();
 
 		getters = new Getter[propertySpan];
 		setters = new Setter[propertySpan];
 
 		Iterator itr = mappingInfo.getPropertyClosureIterator();
 		boolean foundCustomAccessor = false;
 		int i = 0;
 		while ( itr.hasNext() ) {
 			//TODO: redesign how PropertyAccessors are acquired...
 			Property property = (Property) itr.next();
 			getters[i] = buildPropertyGetter( property, mappingInfo );
 			setters[i] = buildPropertySetter( property, mappingInfo );
 			if ( !property.isBasicPropertyAccessor() ) {
 				foundCustomAccessor = true;
 			}
 			i++;
 		}
 		hasCustomAccessors = foundCustomAccessor;
 
 		instantiator = buildInstantiator( entityMetamodel, mappingInfo );
 
 		if ( entityMetamodel.isLazy() && !entityMetamodel.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading() ) {
 			proxyFactory = buildProxyFactory( mappingInfo, idGetter, idSetter );
 			if ( proxyFactory == null ) {
 				entityMetamodel.setLazy( false );
 			}
 		}
 		else {
 			proxyFactory = null;
 		}
 
 		Component mapper = mappingInfo.getIdentifierMapper();
 		if ( mapper == null ) {
 			identifierMapperType = null;
 			mappedIdentifierValueMarshaller = null;
 		}
 		else {
 			identifierMapperType = (CompositeType) mapper.getType();
 			mappedIdentifierValueMarshaller = buildMappedIdentifierValueMarshaller(
 					(ComponentType) entityMetamodel.getIdentifierProperty().getType(),
 					(ComponentType) identifierMapperType
 			);
 		}
 	}
 
 	/**
 	 * Retrieves the defined entity-name for the tuplized entity.
 	 *
 	 * @return The entity-name.
 	 */
 	protected String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	/**
 	 * Retrieves the defined entity-names for any subclasses defined for this
 	 * entity.
 	 *
 	 * @return Any subclass entity-names.
 	 */
 	protected Set getSubclassEntityNames() {
 		return entityMetamodel.getSubclassEntityNames();
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity) throws HibernateException {
 		return getIdentifier( entity, null );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SharedSessionContractImplementor session) {
 		final Object id;
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			id = entity;
 		}
 		else if ( HibernateProxy.class.isInstance( entity ) ) {
 			id = ( (HibernateProxy) entity ).getHibernateLazyInitializer().getIdentifier();
 		}
 		else {
 			if ( idGetter == null ) {
 				if ( identifierMapperType == null ) {
 					throw new HibernateException( "The class has no identifier property: " + getEntityName() );
 				}
 				else {
 					id = mappedIdentifierValueMarshaller.getIdentifier( entity, getEntityMode(), session );
 				}
 			}
 			else {
 				id = idGetter.get( entity );
 			}
 		}
 
 		try {
 			return (Serializable) id;
 		}
 		catch (ClassCastException cce) {
 			StringBuilder msg = new StringBuilder( "Identifier classes must be serializable. " );
 			if ( id != null ) {
 				msg.append( id.getClass().getName() ).append( " is not serializable. " );
 			}
 			if ( cce.getMessage() != null ) {
 				msg.append( cce.getMessage() );
 			}
 			throw new ClassCastException( msg.toString() );
 		}
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id) throws HibernateException {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		setIdentifier( entity, id, null );
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SharedSessionContractImplementor session) {
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			if ( entity != id ) {
 				CompositeType copier = (CompositeType) entityMetamodel.getIdentifierProperty().getType();
 				copier.setPropertyValues( entity, copier.getPropertyValues( id, getEntityMode() ), getEntityMode() );
 			}
 		}
 		else if ( idSetter != null ) {
 			idSetter.set( entity, id, getFactory() );
 		}
 		else if ( identifierMapperType != null ) {
 			mappedIdentifierValueMarshaller.setIdentifier( entity, id, getEntityMode(), session );
 		}
 	}
 
 	private static interface MappedIdentifierValueMarshaller {
 		public Object getIdentifier(Object entity, EntityMode entityMode, SharedSessionContractImplementor session);
 
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SharedSessionContractImplementor session);
 	}
 
 	private final MappedIdentifierValueMarshaller mappedIdentifierValueMarshaller;
 
 	private static MappedIdentifierValueMarshaller buildMappedIdentifierValueMarshaller(
 			ComponentType mappedIdClassComponentType,
 			ComponentType virtualIdComponent) {
 		// so basically at this point we know we have a "mapped" composite identifier
 		// which is an awful way to say that the identifier is represented differently
 		// in the entity and in the identifier value.  The incoming value should
 		// be an instance of the mapped identifier class (@IdClass) while the incoming entity
 		// should be an instance of the entity class as defined by metamodel.
 		//
 		// However, even within that we have 2 potential scenarios:
 		//		1) @IdClass types and entity @Id property types match
 		//			- return a NormalMappedIdentifierValueMarshaller
 		//		2) They do not match
 		//			- return a IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller
 		boolean wereAllEquivalent = true;
 		// the sizes being off is a much bigger problem that should have been caught already...
 		for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
 			if ( virtualIdComponent.getSubtypes()[i].isEntityType()
 					&& !mappedIdClassComponentType.getSubtypes()[i].isEntityType() ) {
 				wereAllEquivalent = false;
 				break;
 			}
 		}
 
 		return wereAllEquivalent
 				? new NormalMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType )
 				: new IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller(
 				virtualIdComponent,
 				mappedIdClassComponentType
 		);
 	}
 
 	private static class NormalMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
 		private final ComponentType virtualIdComponent;
 		private final ComponentType mappedIdentifierType;
 
 		private NormalMappedIdentifierValueMarshaller(
 				ComponentType virtualIdComponent,
 				ComponentType mappedIdentifierType) {
 			this.virtualIdComponent = virtualIdComponent;
 			this.mappedIdentifierType = mappedIdentifierType;
 		}
 
 		@Override
 		public Object getIdentifier(Object entity, EntityMode entityMode, SharedSessionContractImplementor session) {
 			Object id = mappedIdentifierType.instantiate( entityMode );
 			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
 			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
 			return id;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SharedSessionContractImplementor session) {
 			virtualIdComponent.setPropertyValues(
 					entity,
 					mappedIdentifierType.getPropertyValues( id, session ),
 					entityMode
 			);
 		}
 	}
 
 	private static class IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller
 			implements MappedIdentifierValueMarshaller {
 		private final ComponentType virtualIdComponent;
 		private final ComponentType mappedIdentifierType;
 
 		private IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller(
 				ComponentType virtualIdComponent,
 				ComponentType mappedIdentifierType) {
 			this.virtualIdComponent = virtualIdComponent;
 			this.mappedIdentifierType = mappedIdentifierType;
 		}
 
 		@Override
 		public Object getIdentifier(Object entity, EntityMode entityMode, SharedSessionContractImplementor session) {
 			final Object id = mappedIdentifierType.instantiate( entityMode );
 			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
 			final Type[] subTypes = virtualIdComponent.getSubtypes();
 			final Type[] copierSubTypes = mappedIdentifierType.getSubtypes();
 			final Iterable<PersistEventListener> persistEventListeners = persistEventListeners( session );
-			final PersistenceContext persistenceContext = session.getPersistenceContext();
 			final int length = subTypes.length;
 			for ( int i = 0; i < length; i++ ) {
 				if ( propertyValues[i] == null ) {
 					throw new HibernateException( "No part of a composite identifier may be null" );
 				}
 				//JPA 2 @MapsId + @IdClass points to the pk of the entity
 				if ( subTypes[i].isAssociationType() && !copierSubTypes[i].isAssociationType() ) {
 					// we need a session to handle this use case
 					if ( session == null ) {
 						throw new AssertionError(
 								"Deprecated version of getIdentifier (no session) was used but session was required"
 						);
 					}
-					final Object subId;
-					if ( HibernateProxy.class.isInstance( propertyValues[i] ) ) {
-						subId = ( (HibernateProxy) propertyValues[i] ).getHibernateLazyInitializer().getIdentifier();
-					}
-					else {
-						EntityEntry pcEntry = session.getPersistenceContext().getEntry( propertyValues[i] );
-						if ( pcEntry != null ) {
-							subId = pcEntry.getId();
-						}
-						else {
-							LOG.debug( "Performing implicit derived identity cascade" );
-							final PersistEvent event = new PersistEvent(
-									null,
-									propertyValues[i],
-									(EventSource) session
-							);
-							for ( PersistEventListener listener : persistEventListeners ) {
-								listener.onPersist( event );
-							}
-							pcEntry = persistenceContext.getEntry( propertyValues[i] );
-							if ( pcEntry == null || pcEntry.getId() == null ) {
-								throw new HibernateException( "Unable to process implicit derived identity cascade" );
-							}
-							else {
-								subId = pcEntry.getId();
-							}
-						}
-					}
-					propertyValues[i] = subId;
+					propertyValues[i] = determineEntityIdPersistIfNecessary(
+							propertyValues[i],
+							(AssociationType) subTypes[i],
+							session,
+							persistEventListeners
+					);
 				}
 			}
 			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
 			return id;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SharedSessionContractImplementor session) {
 			final Object[] extractedValues = mappedIdentifierType.getPropertyValues( id, entityMode );
 			final Object[] injectionValues = new Object[extractedValues.length];
 			final PersistenceContext persistenceContext = session.getPersistenceContext();
 			for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
 				final Type virtualPropertyType = virtualIdComponent.getSubtypes()[i];
 				final Type idClassPropertyType = mappedIdentifierType.getSubtypes()[i];
 				if ( virtualPropertyType.isEntityType() && !idClassPropertyType.isEntityType() ) {
 					if ( session == null ) {
 						throw new AssertionError(
 								"Deprecated version of getIdentifier (no session) was used but session was required"
 						);
 					}
 					final String associatedEntityName = ( (EntityType) virtualPropertyType ).getAssociatedEntityName();
 					final EntityKey entityKey = session.generateEntityKey(
 							(Serializable) extractedValues[i],
 							session.getFactory().getMetamodel().entityPersister( associatedEntityName )
 					);
 					// it is conceivable there is a proxy, so check that first
 					Object association = persistenceContext.getProxy( entityKey );
 					if ( association == null ) {
 						// otherwise look for an initialized version
 						association = persistenceContext.getEntity( entityKey );
 					}
 					injectionValues[i] = association;
 				}
 				else {
 					injectionValues[i] = extractedValues[i];
 				}
 			}
 			virtualIdComponent.setPropertyValues( entity, injectionValues, entityMode );
 		}
 	}
 
 	private static Iterable<PersistEventListener> persistEventListeners(SharedSessionContractImplementor session) {
 		return session
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( EventType.PERSIST )
 				.listeners();
 	}
 
+	private static Serializable determineEntityIdPersistIfNecessary(
+			Object entity,
+			AssociationType associationType,
+			SharedSessionContractImplementor session,
+			Iterable<PersistEventListener> persistEventListeners) {
+		if ( HibernateProxy.class.isInstance( entity ) ) {
+			// entity is a proxy, so we know it is not transient; just return ID from proxy
+			return ( (HibernateProxy) entity ).getHibernateLazyInitializer().getIdentifier();
+		}
+		else {
+			EntityEntry pcEntry = session.getPersistenceContext().getEntry( entity );
+			if ( pcEntry != null ) {
+				// entity managed; return ID.
+				return pcEntry.getId();
+			}
+			else {
+				final EntityPersister persister = session.getEntityPersister(
+						associationType.getAssociatedEntityName( session.getFactory() ),
+						entity
+				);
+				Serializable entityId = persister.getIdentifier( entity, session );
+				if ( entityId == null ) {
+					// entity is transient with no ID; we need to persist the entity to get the ID.
+					entityId = persistTransientEntity( entity, session, persistEventListeners );
+				}
+				else {
+					// entity has an ID.
+					final EntityKey entityKey = session.generateEntityKey( entityId, persister );
+					// if the entity is in the process of being merged, it may be stored in the
+					// PC already, but doesn't have an EntityEntry yet. If this is the case,
+					// then don't persist even if it is transient because doing so can lead
+					// to having 2 entities in the PC with the same ID (HHH-11328).
+					if ( session.getPersistenceContext().getEntity( entityKey ) == null &&
+							ForeignKeys.isTransient(
+									persister.getEntityName(),
+									entity,
+									null,
+									session
+							) ) {
+						// entity is transient and it is not in the PersistenceContext.
+						// entity needs to be persisted.
+						persistTransientEntity( entity, session, persistEventListeners );
+					}
+				}
+				return entityId;
+			}
+		}
+	}
+
+	private static Serializable persistTransientEntity(
+			Object entity,
+			SharedSessionContractImplementor session,
+			Iterable<PersistEventListener> persistEventListeners) {
+		LOG.debug( "Performing implicit derived identity cascade" );
+		final PersistEvent event = new PersistEvent(
+				null,
+				entity,
+				(EventSource) session
+		);
+		for ( PersistEventListener listener : persistEventListeners ) {
+			listener.onPersist( event );
+		}
+		final EntityEntry pcEntry = session.getPersistenceContext().getEntry( entity );
+		if ( pcEntry == null || pcEntry.getId() == null ) {
+			throw new HibernateException( "Unable to process implicit derived identity cascade" );
+		}
+		return pcEntry.getId();
+	}
+
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion) {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		resetIdentifier( entity, currentId, currentVersion, null );
 	}
 
 	@Override
 	public void resetIdentifier(
 			Object entity,
 			Serializable currentId,
 			Object currentVersion,
 			SharedSessionContractImplementor session) {
 		//noinspection StatementWithEmptyBody
 		if ( entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned ) {
 		}
 		else {
 			//reset the id
 			Serializable result = entityMetamodel.getIdentifierProperty()
 					.getUnsavedValue()
 					.getDefaultValue( currentId );
 			setIdentifier( entity, result, session );
 			//reset the version
 			VersionProperty versionProperty = entityMetamodel.getVersionProperty();
 			if ( entityMetamodel.isVersioned() ) {
 				setPropertyValue(
 						entity,
 						entityMetamodel.getVersionPropertyIndex(),
 						versionProperty.getUnsavedValue().getDefaultValue( currentVersion )
 				);
 			}
 		}
 	}
 
 	@Override
 	public Object getVersion(Object entity) throws HibernateException {
 		if ( !entityMetamodel.isVersioned() ) {
 			return null;
 		}
 		return getters[entityMetamodel.getVersionPropertyIndex()].get( entity );
 	}
 
 	protected boolean shouldGetAllProperties(Object entity) {
 		if ( !getEntityMetamodel().getBytecodeEnhancementMetadata().isEnhancedForLazyLoading() ) {
 			return true;
 		}
 
 		return !getEntityMetamodel().getBytecodeEnhancementMetadata().hasUnFetchedAttributes( entity );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object entity) {
 		final BytecodeEnhancementMetadata enhancementMetadata = entityMetamodel.getBytecodeEnhancementMetadata();
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
 			NonIdentifierAttribute property = entityMetamodel.getProperties()[j];
 			if ( !property.isLazy() || enhancementMetadata.isAttributeLoaded( entity, property.getName() ) ) {
 				result[j] = getters[j].get( entity );
 			}
 			else {
 				result[j] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 			}
 		}
 		return result;
 	}
 
 	@Override
 	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SharedSessionContractImplementor session) {
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
 			result[j] = getters[j].getForInsert( entity, mergeMap, session );
 		}
 		return result;
 	}
 
 	@Override
 	public Object getPropertyValue(Object entity, int i) throws HibernateException {
 		return getters[i].get( entity );
 	}
 
 	@Override
 	public Object getPropertyValue(Object entity, String propertyPath) throws HibernateException {
 		int loc = propertyPath.indexOf( '.' );
 		String basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		//final int index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		Integer index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		if ( index == null ) {
 			propertyPath = PropertyPath.IDENTIFIER_MAPPER_PROPERTY + "." + propertyPath;
 			loc = propertyPath.indexOf( '.' );
 			basePropertyName = loc > 0
 					? propertyPath.substring( 0, loc )
 					: propertyPath;
 		}
 		index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		final Object baseValue = getPropertyValue( entity, index );
 		if ( loc > 0 ) {
 			if ( baseValue == null ) {
 				return null;
 			}
 			return getComponentValue(
 					(ComponentType) entityMetamodel.getPropertyTypes()[index],
 					baseValue,
 					propertyPath.substring( loc + 1 )
 			);
 		}
 		else {
 			return baseValue;
 		}
 	}
 
 	/**
 	 * Extract a component property value.
 	 *
 	 * @param type The component property types.
 	 * @param component The component instance itself.
 	 * @param propertyPath The property path for the property to be extracted.
 	 *
 	 * @return The property value extracted.
 	 */
 	protected Object getComponentValue(ComponentType type, Object component, String propertyPath) {
 		final int loc = propertyPath.indexOf( '.' );
 		final String basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		final int index = findSubPropertyIndex( type, basePropertyName );
 		final Object baseValue = type.getPropertyValue( component, index );
 		if ( loc > 0 ) {
 			if ( baseValue == null ) {
 				return null;
 			}
 			return getComponentValue(
 					(ComponentType) type.getSubtypes()[index],
 					baseValue,
 					propertyPath.substring( loc + 1 )
 			);
 		}
 		else {
 			return baseValue;
 		}
 
 	}
 
 	private int findSubPropertyIndex(ComponentType type, String subPropertyName) {
 		final String[] propertyNames = type.getPropertyNames();
 		for ( int index = 0; index < propertyNames.length; index++ ) {
 			if ( subPropertyName.equals( propertyNames[index] ) ) {
 				return index;
 			}
 		}
 		throw new MappingException( "component property not found: " + subPropertyName );
 	}
 
 	@Override
 	public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
 		boolean setAll = !entityMetamodel.hasLazyProperties();
 
 		for ( int j = 0; j < entityMetamodel.getPropertySpan(); j++ ) {
 			if ( setAll || values[j] != LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 				setters[j].set( entity, values[j], getFactory() );
 			}
 		}
 	}
 
 	@Override
 	public void setPropertyValue(Object entity, int i, Object value) throws HibernateException {
 		setters[i].set( entity, value, getFactory() );
 	}
 
 	@Override
 	public void setPropertyValue(Object entity, String propertyName, Object value) throws HibernateException {
 		setters[entityMetamodel.getPropertyIndex( propertyName )].set( entity, value, getFactory() );
 	}
 
 	@Override
 	public final Object instantiate(Serializable id) throws HibernateException {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		return instantiate( id, null );
 	}
 
 	@Override
 	public final Object instantiate(Serializable id, SharedSessionContractImplementor session) {
 		Object result = getInstantiator().instantiate( id );
 		if ( id != null ) {
 			setIdentifier( result, id, session );
 		}
 		return result;
 	}
 
 	@Override
 	public final Object instantiate() throws HibernateException {
 		return instantiate( null, null );
 	}
 
 	@Override
 	public void afterInitialize(Object entity, SharedSessionContractImplementor session) {
 	}
 
 	@Override
 	public final boolean isInstance(Object object) {
 		return getInstantiator().isInstance( object );
 	}
 
 	@Override
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy() && !entityMetamodel.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading();
 	}
 
 	@Override
 	public final Object createProxy(Serializable id, SharedSessionContractImplementor session) {
 		return getProxyFactory().getProxy( id, session );
 	}
 
 	@Override
 	public boolean isLifecycleImplementor() {
 		return false;
 	}
 
 	protected final EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	protected final SessionFactoryImplementor getFactory() {
 		return entityMetamodel.getSessionFactory();
 	}
 
 	protected final Instantiator getInstantiator() {
 		return instantiator;
 	}
 
 	protected final ProxyFactory getProxyFactory() {
 		return proxyFactory;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getEntityMetamodel().getName() + ')';
 	}
 
 	@Override
 	public Getter getIdentifierGetter() {
 		return idGetter;
 	}
 
 	@Override
 	public Getter getVersionGetter() {
 		if ( getEntityMetamodel().isVersioned() ) {
 			return getGetter( getEntityMetamodel().getVersionPropertyIndex() );
 		}
 		return null;
 	}
 
 	@Override
 	public Getter getGetter(int i) {
 		return getters[i];
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/derivedidentities/bidirectional/CompositeIdDerivedIdWithIdClassTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/derivedidentities/bidirectional/CompositeIdDerivedIdWithIdClassTest.java
new file mode 100644
index 0000000000..f1c85aaacc
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/derivedidentities/bidirectional/CompositeIdDerivedIdWithIdClassTest.java
@@ -0,0 +1,211 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.annotations.derivedidentities.bidirectional;
+
+import java.io.Serializable;
+import java.util.ArrayList;
+import java.util.List;
+import java.util.Objects;
+import javax.persistence.CascadeType;
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.IdClass;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+import javax.persistence.OneToMany;
+
+import org.junit.After;
+import org.junit.Test;
+
+import org.hibernate.Session;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+import static org.junit.Assert.assertEquals;
+
+public class CompositeIdDerivedIdWithIdClassTest extends BaseCoreFunctionalTestCase {
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class<?>[] {
+				ShoppingCart.class,
+				LineItem.class
+		};
+	}
+
+	@After
+	public void cleanup() {
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.createQuery( "delete from LineItem" ).executeUpdate();
+		s.createQuery( "delete from Cart" ).executeUpdate();
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue(jiraKey = "HHH-11328")
+	public void testMergeTransientIdManyToOne() throws Exception {
+		ShoppingCart transientCart = new ShoppingCart( "cart1" );
+		transientCart.addLineItem( new LineItem( 0, "description2", transientCart ) );
+
+		// merge ID with transient many-to-one
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.merge( transientCart );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		ShoppingCart updatedCart = s.get( ShoppingCart.class, "cart1" );
+		assertEquals( 1, updatedCart.getLineItems().size() );
+		assertEquals( "description2", updatedCart.getLineItems().get( 0 ).getDescription() );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue(jiraKey = "HHH-10623")
+	public void testMergeDetachedIdManyToOne() throws Exception {
+		ShoppingCart cart = new ShoppingCart("cart1");
+
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.persist( cart );
+		s.getTransaction().commit();
+		s.close();
+
+		// cart is detached now
+		LineItem lineItem = new LineItem( 0, "description2", cart );
+		cart.addLineItem( lineItem );
+
+		// merge lineItem with an ID with detached many-to-one
+		s = openSession();
+		s.getTransaction().begin();
+		s.merge(lineItem);
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		ShoppingCart updatedCart = s.get( ShoppingCart.class, "cart1" );
+		assertEquals( 1, updatedCart.getLineItems().size() );
+		assertEquals("description2", updatedCart.getLineItems().get( 0 ).getDescription());
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Entity(name = "Cart")
+	public static class ShoppingCart {
+		@Id
+		@Column(name = "id", nullable = false)
+		private String id;
+
+		@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
+		private List<LineItem> lineItems = new ArrayList<>();
+
+		protected ShoppingCart() {
+		}
+
+		public ShoppingCart(String id) {
+			this.id = id;
+		}
+
+		public List<LineItem> getLineItems() {
+			return lineItems;
+		}
+
+		public void addLineItem(LineItem lineItem) {
+			lineItems.add(lineItem);
+		}
+
+		@Override
+		public boolean equals(Object o) {
+			if (this == o) return true;
+			if (o == null || getClass() != o.getClass()) return false;
+			ShoppingCart that = (ShoppingCart) o;
+			return Objects.equals( id, that.id );
+		}
+
+		@Override
+		public int hashCode() {
+			return Objects.hash(id);
+		}
+	}
+
+	@Entity(name = "LineItem")
+	@IdClass(LineItem.Pk.class)
+	public static class LineItem {
+
+		@Id
+		@Column(name = "item_seq_number", nullable = false)
+		private Integer sequenceNumber;
+
+		@Column(name = "description")
+		private String description;
+
+		@Id
+		@ManyToOne
+		@JoinColumn(name = "cart_id")
+		private ShoppingCart cart;
+
+		protected LineItem() {
+		}
+
+		public LineItem(Integer sequenceNumber, String description, ShoppingCart cart) {
+			this.sequenceNumber = sequenceNumber;
+			this.description = description;
+			this.cart = cart;
+		}
+
+		public Integer getSequenceNumber() {
+			return sequenceNumber;
+		}
+
+		public ShoppingCart getCart() {
+			return cart;
+		}
+
+		@Override
+		public boolean equals(Object o) {
+			if (this == o) return true;
+			if (!(o instanceof LineItem)) return false;
+			LineItem lineItem = (LineItem) o;
+			return Objects.equals(getSequenceNumber(), lineItem.getSequenceNumber()) &&
+					Objects.equals(getCart(), lineItem.getCart());
+		}
+
+		@Override
+		public int hashCode() {
+			return Objects.hash( getSequenceNumber(), getCart() );
+		}
+
+		public String getDescription() {
+			return description;
+		}
+
+		public static class Pk implements Serializable {
+			public Integer sequenceNumber;
+			public String cart;
+
+			@Override
+			public boolean equals(Object o) {
+				if (this == o) return true;
+				if (!(o instanceof Pk)) return false;
+				Pk pk = (Pk) o;
+				return Objects.equals(sequenceNumber, pk.sequenceNumber) &&
+						Objects.equals(cart, pk.cart);
+			}
+
+			@Override
+			public int hashCode() {
+				return Objects.hash(sequenceNumber, cart);
+			}
+		}
+	}
+}
