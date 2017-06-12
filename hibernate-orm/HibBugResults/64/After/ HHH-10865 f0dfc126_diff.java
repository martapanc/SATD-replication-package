diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeEnhancementMetadata.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeEnhancementMetadata.java
index 20b9626cb4..b71ba5aad2 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeEnhancementMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeEnhancementMetadata.java
@@ -1,62 +1,64 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.bytecode.spi;
 
 import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
 import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributesMetadata;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 
 /**
  * Encapsulates bytecode enhancement information about a particular entity.
  *
  * @author Steve Ebersole
  */
 public interface BytecodeEnhancementMetadata {
 	/**
 	 * The name of the entity to which this metadata applies.
 	 *
 	 * @return The entity name
 	 */
 	String getEntityName();
 
 	/**
 	 * Has the entity class been bytecode enhanced for lazy loading?
 	 *
 	 * @return {@code true} indicates the entity class is enhanced for Hibernate use
 	 * in lazy loading; {@code false} indicates it is not
 	 */
 	boolean isEnhancedForLazyLoading();
 
 	LazyAttributesMetadata getLazyAttributesMetadata();
 
 	/**
 	 * Build and inject an interceptor instance into the enhanced entity.
 	 *
 	 * @param entity The entity into which built interceptor should be injected
 	 * @param session The session to which the entity instance belongs.
 	 *
 	 * @return The built and injected interceptor
 	 *
 	 * @throws NotInstrumentedException Thrown if {@link #isEnhancedForLazyLoading()} returns {@code false}
 	 */
 	LazyAttributeLoadingInterceptor injectInterceptor(
 			Object entity,
 			SharedSessionContractImplementor session) throws NotInstrumentedException;
 
 	/**
 	 * Extract the field interceptor instance from the enhanced entity.
 	 *
 	 * @param entity The entity from which to extract the interceptor
 	 *
 	 * @return The extracted interceptor
 	 *
 	 * @throws NotInstrumentedException Thrown if {@link #isEnhancedForLazyLoading()} returns {@code false}
 	 */
 	LazyAttributeLoadingInterceptor extractInterceptor(Object entity) throws NotInstrumentedException;
 
 	boolean hasUnFetchedAttributes(Object entity);
+
+	boolean isAttributeLoaded(Object entity, String attributeName);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
index 0b195e4f4b..ea284898c8 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
@@ -1,708 +1,709 @@
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
+import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
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
 import org.hibernate.property.access.spi.Getter;
 import org.hibernate.property.access.spi.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.tuple.Instantiator;
 import org.hibernate.tuple.NonIdentifierAttribute;
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
 			final PersistenceContext persistenceContext = session.getPersistenceContext();
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
 					final Object subId;
 					if ( HibernateProxy.class.isInstance( propertyValues[i] ) ) {
 						subId = ( (HibernateProxy) propertyValues[i] ).getHibernateLazyInitializer().getIdentifier();
 					}
 					else {
 						EntityEntry pcEntry = session.getPersistenceContext().getEntry( propertyValues[i] );
 						if ( pcEntry != null ) {
 							subId = pcEntry.getId();
 						}
 						else {
 							LOG.debug( "Performing implicit derived identity cascade" );
 							final PersistEvent event = new PersistEvent(
 									null,
 									propertyValues[i],
 									(EventSource) session
 							);
 							for ( PersistEventListener listener : persistEventListeners ) {
 								listener.onPersist( event );
 							}
 							pcEntry = persistenceContext.getEntry( propertyValues[i] );
 							if ( pcEntry == null || pcEntry.getId() == null ) {
 								throw new HibernateException( "Unable to process implicit derived identity cascade" );
 							}
 							else {
 								subId = pcEntry.getId();
 							}
 						}
 					}
 					propertyValues[i] = subId;
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
-		boolean getAll = shouldGetAllProperties( entity );
+		final BytecodeEnhancementMetadata enhancementMetadata = entityMetamodel.getBytecodeEnhancementMetadata();
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
 			NonIdentifierAttribute property = entityMetamodel.getProperties()[j];
-			if ( getAll || !property.isLazy() ) {
+			if ( !property.isLazy() || enhancementMetadata.isAttributeLoaded( entity, property.getName() ) ) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataNonPojoImpl.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataNonPojoImpl.java
index 38e53c95e8..10286c23a6 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataNonPojoImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataNonPojoImpl.java
@@ -1,60 +1,66 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.tuple.entity;
 
 import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
 import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributesMetadata;
 import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
 import org.hibernate.bytecode.spi.NotInstrumentedException;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 
 /**
  * @author Steve Ebersole
  */
 public class BytecodeEnhancementMetadataNonPojoImpl implements BytecodeEnhancementMetadata {
 	private final String entityName;
 	private final LazyAttributesMetadata lazyAttributesMetadata;
 	private final String errorMsg;
 
 	public BytecodeEnhancementMetadataNonPojoImpl(String entityName) {
 		this.entityName = entityName;
 		this.lazyAttributesMetadata = LazyAttributesMetadata.nonEnhanced( entityName );
 		this.errorMsg = "Entity [" + entityName + "] is non-pojo, and therefore not instrumented";
 	}
 
 	@Override
 	public String getEntityName() {
 		return entityName;
 	}
 
 	@Override
 	public boolean isEnhancedForLazyLoading() {
 		return false;
 	}
 
 	@Override
 	public LazyAttributesMetadata getLazyAttributesMetadata() {
 		return lazyAttributesMetadata;
 	}
 
 	@Override
 	public LazyAttributeLoadingInterceptor injectInterceptor(
 			Object entity,
 			SharedSessionContractImplementor session) throws NotInstrumentedException {
 		throw new NotInstrumentedException( errorMsg );
 	}
 
 	@Override
 	public LazyAttributeLoadingInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
 		throw new NotInstrumentedException( errorMsg );
 	}
 
 	@Override
 	public boolean hasUnFetchedAttributes(Object entity) {
 		return false;
 	}
+
+	@Override
+	public boolean isAttributeLoaded(Object entity, String attributeName) {
+		return true;
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataPojoImpl.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataPojoImpl.java
index 621b91bbc4..48134c0cee 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataPojoImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataPojoImpl.java
@@ -1,124 +1,128 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.tuple.entity;
 
 import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
 import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributesMetadata;
 import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
 import org.hibernate.bytecode.spi.NotInstrumentedException;
 import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 import org.hibernate.engine.spi.PersistentAttributeInterceptor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.mapping.PersistentClass;
 
 /**
  * @author Steve Ebersole
  */
 public class BytecodeEnhancementMetadataPojoImpl implements BytecodeEnhancementMetadata {
 	public static BytecodeEnhancementMetadata from(PersistentClass persistentClass) {
 		final Class mappedClass = persistentClass.getMappedClass();
 		final boolean enhancedForLazyLoading = PersistentAttributeInterceptable.class.isAssignableFrom( mappedClass );
 		final LazyAttributesMetadata lazyAttributesMetadata = enhancedForLazyLoading
 				? LazyAttributesMetadata.from( persistentClass )
 				: LazyAttributesMetadata.nonEnhanced( persistentClass.getEntityName() );
 
 		return new BytecodeEnhancementMetadataPojoImpl(
 				persistentClass.getEntityName(),
 				mappedClass,
 				enhancedForLazyLoading,
 				lazyAttributesMetadata
 		);
 	}
 
 	private final String entityName;
 	private final Class entityClass;
 	private final boolean enhancedForLazyLoading;
 	private final LazyAttributesMetadata lazyAttributesMetadata;
 
 	public BytecodeEnhancementMetadataPojoImpl(
 			String entityName,
 			Class entityClass,
 			boolean enhancedForLazyLoading,
 			LazyAttributesMetadata lazyAttributesMetadata) {
 		this.entityName = entityName;
 		this.entityClass = entityClass;
 		this.enhancedForLazyLoading = enhancedForLazyLoading;
 		this.lazyAttributesMetadata = lazyAttributesMetadata;
 	}
 
 	@Override
 	public String getEntityName() {
 		return entityName;
 	}
 
 	@Override
 	public boolean isEnhancedForLazyLoading() {
 		return enhancedForLazyLoading;
 	}
 
 	@Override
 	public LazyAttributesMetadata getLazyAttributesMetadata() {
 		return lazyAttributesMetadata;
 	}
 
 	@Override
 	public boolean hasUnFetchedAttributes(Object entity) {
-		LazyAttributeLoadingInterceptor interceptor = isEnhancedForLazyLoading()
-				? extractInterceptor( entity )
-				: null;
+		LazyAttributeLoadingInterceptor interceptor = enhancedForLazyLoading ? extractInterceptor( entity ) : null;
 		return interceptor != null && interceptor.hasAnyUninitializedAttributes();
 	}
 
 	@Override
+	public boolean isAttributeLoaded(Object entity, String attributeName) {
+		LazyAttributeLoadingInterceptor interceptor = enhancedForLazyLoading ? extractInterceptor( entity ) : null;
+		return interceptor == null || interceptor.isAttributeLoaded( attributeName );
+	}
+
+	@Override
 	public LazyAttributeLoadingInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
 		if ( !enhancedForLazyLoading ) {
 			throw new NotInstrumentedException( "Entity class [" + entityClass.getName() + "] is not enhanced for lazy loading" );
 		}
 
 		if ( !entityClass.isInstance( entity ) ) {
 			throw new IllegalArgumentException(
 					String.format(
 							"Passed entity instance [%s] is not of expected type [%s]",
 							entity,
 							getEntityName()
 					)
 			);
 		}
 
 		final PersistentAttributeInterceptor interceptor = ( (PersistentAttributeInterceptable) entity ).$$_hibernate_getInterceptor();
 		if ( interceptor == null ) {
 			return null;
 		}
 
 		return (LazyAttributeLoadingInterceptor) interceptor;
 	}
 
 	@Override
 	public LazyAttributeLoadingInterceptor injectInterceptor(Object entity, SharedSessionContractImplementor session) {
 		if ( !enhancedForLazyLoading ) {
 			throw new NotInstrumentedException( "Entity class [" + entityClass.getName() + "] is not enhanced for lazy loading" );
 		}
 
 		if ( !entityClass.isInstance( entity ) ) {
 			throw new IllegalArgumentException(
 					String.format(
 							"Passed entity instance [%s] is not of expected type [%s]",
 							entity,
 							getEntityName()
 					)
 			);
 		}
 
 		final LazyAttributeLoadingInterceptor interceptor = new LazyAttributeLoadingInterceptor(
 				getEntityName(),
 				lazyAttributesMetadata.getLazyAttributeNames(),
 				session
 		);
 		( (PersistentAttributeInterceptable) entity ).$$_hibernate_setInterceptor( interceptor );
 		return interceptor;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
index a0f5cbc350..3273667412 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
@@ -1,175 +1,177 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement;
 
 import org.hibernate.test.bytecode.enhancement.eviction.EvictionTestTask;
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.bytecode.enhancement.association.ManyToManyAssociationTestTask;
 import org.hibernate.test.bytecode.enhancement.association.OneToManyAssociationTestTask;
 import org.hibernate.test.bytecode.enhancement.association.OneToOneAssociationTestTask;
 import org.hibernate.test.bytecode.enhancement.basic.BasicEnhancementTestTask;
 import org.hibernate.test.bytecode.enhancement.basic.HHH9529TestTask;
 import org.hibernate.test.bytecode.enhancement.cascade.CascadeDeleteTestTask;
 import org.hibernate.test.bytecode.enhancement.dirty.DirtyTrackingTestTask;
 import org.hibernate.test.bytecode.enhancement.extended.ExtendedAssociationManagementTestTasK;
 import org.hibernate.test.bytecode.enhancement.extended.ExtendedEnhancementTestTask;
 import org.hibernate.test.bytecode.enhancement.join.HHH3949TestTask1;
 import org.hibernate.test.bytecode.enhancement.join.HHH3949TestTask2;
 import org.hibernate.test.bytecode.enhancement.join.HHH3949TestTask3;
 import org.hibernate.test.bytecode.enhancement.join.HHH3949TestTask4;
 import org.hibernate.test.bytecode.enhancement.lazy.HHH_10708.UnexpectedDeleteOneTestTask;
+import org.hibernate.test.bytecode.enhancement.lazy.HHH_10708.UnexpectedDeleteThreeTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.HHH_10708.UnexpectedDeleteTwoTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyBasicFieldNotInitializedTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyCollectionLoadingTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyLoadingIntegrationTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyLoadingTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.basic.LazyBasicFieldAccessTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.basic.LazyBasicPropertyAccessTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.group.LazyGroupAccessTestTask;
 import org.hibernate.test.bytecode.enhancement.lazyCache.InitFromCacheTestTask;
 import org.hibernate.test.bytecode.enhancement.mapped.MappedSuperclassTestTask;
 import org.hibernate.test.bytecode.enhancement.merge.CompositeMergeTestTask;
 import org.hibernate.test.bytecode.enhancement.ondemandload.LazyCollectionWithClearedSessionTestTask;
 import org.hibernate.test.bytecode.enhancement.ondemandload.LazyCollectionWithClosedSessionTestTask;
 import org.hibernate.test.bytecode.enhancement.ondemandload.LazyEntityLoadingWithClosedSessionTestTask;
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
 	public void testDirty() {
 		EnhancerTestUtils.runEnhancerTestTask( DirtyTrackingTestTask.class );
 	}
 
 	@Test
 	public void testEviction() {
 		EnhancerTestUtils.runEnhancerTestTask( EvictionTestTask.class );
 	}
 
 	@Test
 	public void testAssociation() {
 		EnhancerTestUtils.runEnhancerTestTask( OneToOneAssociationTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( OneToManyAssociationTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( ManyToManyAssociationTestTask.class );
 	}
 
 	@Test
 	public void testLazy() {
 		EnhancerTestUtils.runEnhancerTestTask( LazyLoadingTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( LazyLoadingIntegrationTestTask.class );
 
 		EnhancerTestUtils.runEnhancerTestTask( LazyBasicPropertyAccessTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( LazyBasicFieldAccessTestTask.class );
 	}
 
 	@Test
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
+		EnhancerTestUtils.runEnhancerTestTask( UnexpectedDeleteThreeTestTask.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-10646" )
 	public void testMappedSuperclass() {
 		EnhancerTestUtils.runEnhancerTestTask( MappedSuperclassTestTask.class );
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
 	public void testInitFromCache() {
 		EnhancerTestUtils.runEnhancerTestTask( InitFromCacheTestTask.class );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/HHH_10708/UnexpectedDeleteThreeTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/HHH_10708/UnexpectedDeleteThreeTestTask.java
new file mode 100644
index 0000000000..f847f382ff
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/HHH_10708/UnexpectedDeleteThreeTestTask.java
@@ -0,0 +1,152 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.bytecode.enhancement.lazy.HHH_10708;
+
+import org.hibernate.Session;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.cfg.Environment;
+import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
+import org.junit.Assert;
+
+import javax.persistence.Column;
+import javax.persistence.ElementCollection;
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.Id;
+import javax.persistence.ManyToMany;
+import java.util.Collections;
+import java.util.HashSet;
+import java.util.Set;
+
+public class UnexpectedDeleteThreeTestTask extends AbstractEnhancerTestTask {
+
+	@Override
+	public Class<?>[] getAnnotatedClasses() {
+		return new Class<?>[] { Parent.class, Child.class };
+	}
+
+	@Override
+	public void prepare() {
+		Configuration cfg = new Configuration();
+		cfg.setProperty( Environment.ENABLE_LAZY_LOAD_NO_TRANS, "true" );
+		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "false" );
+		prepare( cfg );
+
+		Session s = getFactory().openSession();
+		s.beginTransaction();
+
+		Child child = new Child();
+		child.setId( 2L );
+		s.save(child);
+
+		Parent parent = new Parent();
+		parent.setId( 1L );
+		parent.setNames( Collections.singleton( "name" ) );
+
+		Set<Child> children = new HashSet<Child>();
+		children.add(child);
+		parent.setChildren( children );
+
+		s.save( parent );
+
+		s.getTransaction().commit();
+		s.clear();
+		s.close();
+	}
+
+	@Override
+	public void execute() {
+		Session s = getFactory().openSession();
+		s.beginTransaction();
+
+		Parent parent = s.get( Parent.class, 1L );
+		Set<Child> children = parent.getChildren();
+		if (children == null) {
+			children = new HashSet<Child>();
+			parent.setChildren( children );
+		}
+		Child child = new Child();
+		child.setId( 1L );
+		s.save(child);
+		children.add(child);
+
+		// We need to leave at least one attribute unfetchd
+		//parent.getNames().size();
+		s.save(parent);
+
+		s.getTransaction().commit();
+		s.close();
+
+		s = getFactory().openSession();
+		s.beginTransaction();
+
+		Parent application = s.get( Parent.class, 1L );
+		Assert.assertEquals( "Loaded Children collection has unexpected size", 2, application.getChildren().size() );
+
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Override
+	protected void cleanup() {
+	}
+
+	// --- //
+
+	@Entity public static class Child {
+
+		private Long id;
+
+		@Id
+		@Column(name = "id", unique = true, nullable = false)
+		public Long getId() {
+			return id;
+		}
+
+		public void setId(Long id)
+		{
+			this.id = id;
+		}
+
+	}
+
+	@Entity public static class Parent {
+
+		private Long id;
+		private Set<String> names;
+		private Set<Child> children;
+
+		@Id @Column(name = "id", unique = true, nullable = false)
+		public Long getId() {
+			return id;
+		}
+
+		public void setId(Long id) {
+			this.id = id;
+		}
+
+		@ElementCollection
+		public Set<String> getNames() {
+			return names;
+		}
+
+		public void setNames(Set<String> secrets) {
+			this.names = secrets;
+		}
+
+		@ManyToMany(fetch = FetchType.LAZY, targetEntity = Child.class)
+		public Set<Child> getChildren() {
+			return children;
+		}
+
+		public void setChildren(Set<Child> children) {
+			this.children = children;
+		}
+
+	}
+
+}
\ No newline at end of file
