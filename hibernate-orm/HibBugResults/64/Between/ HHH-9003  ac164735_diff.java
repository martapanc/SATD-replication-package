diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
index 55546ccd94..67ec5d9791 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
@@ -1,817 +1,817 @@
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
 package org.hibernate.tuple.entity;
 
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
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
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.property.Getter;
 import org.hibernate.property.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.tuple.Instantiator;
 import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 
 /**
  * Support for tuplizers relating to entities.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public abstract class AbstractEntityTuplizer implements EntityTuplizer {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			AbstractEntityTuplizer.class.getName()
 	);
 
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
 	 * @return An appropriate Getter instance.
 	 */
 	protected abstract Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity);
 
 	/**
 	 * Build an appropriate Setter for the given property.
 	 *
 	 * @param mappedProperty The property to be accessed via the built Setter.
 	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
 	 * @return An appropriate Setter instance.
 	 */
 	protected abstract Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity);
 
 	/**
 	 * Build an appropriate Instantiator for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @return An appropriate Instantiator instance.
 	 */
 	protected abstract Instantiator buildInstantiator(PersistentClass mappingInfo);
 
 	/**
 	 * Build an appropriate ProxyFactory for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @param idGetter The constructed Getter relating to the entity's id property.
 	 * @param idSetter The constructed Setter relating to the entity's id property.
 	 * @return An appropriate ProxyFactory instance.
 	 */
 	protected abstract ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter);
 
 	/**
 	 * Build an appropriate Getter for the given property.
 	 *
 	 *
 	 * @param mappedProperty The property to be accessed via the built Getter.
 	 * @return An appropriate Getter instance.
 	 */
 	protected abstract Getter buildPropertyGetter(AttributeBinding mappedProperty);
 
 	/**
 	 * Build an appropriate Setter for the given property.
 	 *
 	 *
 	 * @param mappedProperty The property to be accessed via the built Setter.
 	 * @return An appropriate Setter instance.
 	 */
 	protected abstract Setter buildPropertySetter(AttributeBinding mappedProperty);
 
 	/**
 	 * Build an appropriate Instantiator for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @return An appropriate Instantiator instance.
 	 */
 	protected abstract Instantiator buildInstantiator(EntityBinding mappingInfo);
 
 	/**
 	 * Build an appropriate ProxyFactory for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @param idGetter The constructed Getter relating to the entity's id property.
 	 * @param idSetter The constructed Setter relating to the entity's id property.
 	 * @return An appropriate ProxyFactory instance.
 	 */
 	protected abstract ProxyFactory buildProxyFactory(EntityBinding mappingInfo, Getter idGetter, Setter idSetter);
 
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
 		boolean foundCustomAccessor=false;
 		int i=0;
 		while ( itr.hasNext() ) {
 			//TODO: redesign how PropertyAccessors are acquired...
 			Property property = (Property) itr.next();
 			getters[i] = buildPropertyGetter(property, mappingInfo);
 			setters[i] = buildPropertySetter(property, mappingInfo);
 			if ( !property.isBasicPropertyAccessor() ) {
 				foundCustomAccessor = true;
 			}
 			i++;
 		}
 		hasCustomAccessors = foundCustomAccessor;
 
         instantiator = buildInstantiator( mappingInfo );
 
 		if ( entityMetamodel.isLazy() ) {
 			proxyFactory = buildProxyFactory( mappingInfo, idGetter, idSetter );
 			if (proxyFactory == null) {
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
 	 * Constructs a new AbstractEntityTuplizer instance.
 	 *
 	 * @param entityMetamodel The "interpreted" information relating to the mapped entity.
 	 * @param mappingInfo The parsed "raw" mapping data relating to the given entity.
 	 */
 	public AbstractEntityTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappingInfo) {
 		this.entityMetamodel = entityMetamodel;
 
 		if ( !entityMetamodel.getIdentifierProperty().isVirtual() ) {
 			idGetter = buildPropertyGetter( mappingInfo.getHierarchyDetails().getEntityIdentifier().getValueBinding() );
 			idSetter = buildPropertySetter( mappingInfo.getHierarchyDetails().getEntityIdentifier().getValueBinding() );
 		}
 		else {
 			idGetter = null;
 			idSetter = null;
 		}
 
 		propertySpan = entityMetamodel.getPropertySpan();
 
 		getters = new Getter[ propertySpan ];
 		setters = new Setter[ propertySpan ];
 
 		boolean foundCustomAccessor = false;
 		int i = 0;
 		for ( AttributeBinding property : mappingInfo.getAttributeBindingClosure() ) {
 			if ( property == mappingInfo.getHierarchyDetails().getEntityIdentifier().getValueBinding() ) {
 				continue; // ID binding processed above
 			}
 
 			//TODO: redesign how PropertyAccessors are acquired...
 			getters[ i ] = buildPropertyGetter( property );
 			setters[ i ] = buildPropertySetter( property );
 			if ( ! property.isBasicPropertyAccessor() ) {
 				foundCustomAccessor = true;
 			}
 			i++;
 		}
 		hasCustomAccessors = foundCustomAccessor;
 
 		instantiator = buildInstantiator( mappingInfo );
 
 		if ( entityMetamodel.isLazy() ) {
 			proxyFactory = buildProxyFactory( mappingInfo, idGetter, idSetter );
 			if ( proxyFactory == null ) {
 				entityMetamodel.setLazy( false );
 			}
 		}
 		else {
 			proxyFactory = null;
 		}
 
 
 		// TODO: Fix this when components are working (HHH-6173)
 		//Component mapper = mappingInfo.getEntityIdentifier().getIdentifierMapper();
 		Component mapper = null;
 		if ( mapper == null ) {
 			identifierMapperType = null;
 			mappedIdentifierValueMarshaller = null;
 		}
 		else {
 			identifierMapperType = ( CompositeType ) mapper.getType();
 			mappedIdentifierValueMarshaller = buildMappedIdentifierValueMarshaller(
 					( ComponentType ) entityMetamodel.getIdentifierProperty().getType(),
 					( ComponentType ) identifierMapperType
 			);
 		}
 	}
 
 	/** Retreives the defined entity-name for the tuplized entity.
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
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		final Object id;
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			id = entity;
 		}
 		else if ( HibernateProxy.class.isInstance( entity ) ) {
 			id = ( (HibernateProxy) entity ).getHibernateLazyInitializer().getIdentifier();
 		}
 		else {
 			if ( idGetter == null ) {
 				if (identifierMapperType==null) {
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
 		catch ( ClassCastException cce ) {
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
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
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
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session);
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session);
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
 					&& ! mappedIdClassComponentType.getSubtypes()[i].isEntityType() ) {
 				wereAllEquivalent = false;
 				break;
 			}
 		}
 
 		return wereAllEquivalent
 				? new NormalMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType )
 				: new IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType );
 	}
 
 	private static class NormalMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
 		private final ComponentType virtualIdComponent;
 		private final ComponentType mappedIdentifierType;
 
 		private NormalMappedIdentifierValueMarshaller(ComponentType virtualIdComponent, ComponentType mappedIdentifierType) {
 			this.virtualIdComponent = virtualIdComponent;
 			this.mappedIdentifierType = mappedIdentifierType;
 		}
 
 		@Override
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
 			Object id = mappedIdentifierType.instantiate( entityMode );
 			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
 			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
 			return id;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
 			virtualIdComponent.setPropertyValues(
 					entity,
 					mappedIdentifierType.getPropertyValues( id, session ),
 					entityMode
 			);
 		}
 	}
 
 	private static class IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
 		private final ComponentType virtualIdComponent;
 		private final ComponentType mappedIdentifierType;
 
 		private IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller(ComponentType virtualIdComponent, ComponentType mappedIdentifierType) {
 			this.virtualIdComponent = virtualIdComponent;
 			this.mappedIdentifierType = mappedIdentifierType;
 		}
 
 		@Override
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
 			final Object id = mappedIdentifierType.instantiate( entityMode );
 			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
 			final Type[] subTypes = virtualIdComponent.getSubtypes();
 			final Type[] copierSubTypes = mappedIdentifierType.getSubtypes();
 			final Iterable<PersistEventListener> persistEventListeners = persistEventListeners( session );
 			final PersistenceContext persistenceContext = session.getPersistenceContext();
 			final int length = subTypes.length;
 			for ( int i = 0 ; i < length; i++ ) {
 				if ( propertyValues[i] == null ) {
 					throw new HibernateException( "No part of a composite identifier may be null" );
 				}
 				//JPA 2 @MapsId + @IdClass points to the pk of the entity
 				if ( subTypes[i].isAssociationType() && ! copierSubTypes[i].isAssociationType() ) {
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
 							final PersistEvent event = new PersistEvent( null, propertyValues[i], (EventSource) session );
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
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
 			final Object[] extractedValues = mappedIdentifierType.getPropertyValues( id, entityMode );
 			final Object[] injectionValues = new Object[ extractedValues.length ];
 			final PersistenceContext persistenceContext = session.getPersistenceContext();
 			for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
 				final Type virtualPropertyType = virtualIdComponent.getSubtypes()[i];
 				final Type idClassPropertyType = mappedIdentifierType.getSubtypes()[i];
 				if ( virtualPropertyType.isEntityType() && ! idClassPropertyType.isEntityType() ) {
 					if ( session == null ) {
 						throw new AssertionError(
 								"Deprecated version of getIdentifier (no session) was used but session was required"
 						);
 					}
 					final String associatedEntityName = ( (EntityType) virtualPropertyType ).getAssociatedEntityName();
 					final EntityKey entityKey = session.generateEntityKey(
 							(Serializable) extractedValues[i],
 							session.getFactory().getEntityPersister( associatedEntityName )
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
 
 	private static Iterable<PersistEventListener> persistEventListeners(SessionImplementor session) {
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
 			SessionImplementor session) {
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
 		if ( !entityMetamodel.isVersioned() ) return null;
 		return getters[ entityMetamodel.getVersionPropertyIndex() ].get( entity );
 	}
 
 	protected boolean shouldGetAllProperties(Object entity) {
 		return !hasUninitializedLazyProperties( entity );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object entity) throws HibernateException {
 		boolean getAll = shouldGetAllProperties( entity );
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
 			NonIdentifierAttribute property = entityMetamodel.getProperties()[j];
 			if ( getAll || !property.isLazy() ) {
 				result[j] = getters[j].get( entity );
 			}
 			else {
 				result[j] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 			}
 		}
 		return result;
 	}
 
 	@Override
 	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
 			throws HibernateException {
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
 		int loc = propertyPath.indexOf('.');
 		String basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		//final int index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		Integer index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		if (index == null) {
 			propertyPath = PropertyPath.IDENTIFIER_MAPPER_PROPERTY + "." + propertyPath;
 			loc = propertyPath.indexOf('.');
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
 					propertyPath.substring(loc+1)
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
 	 * @return The property value extracted.
 	 */
 	protected Object getComponentValue(ComponentType type, Object component, String propertyPath) {
 		final int loc = propertyPath.indexOf( '.' );
 		final String basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		final int index = findSubPropertyIndex( type, basePropertyName );
-		final Object baseValue = type.getPropertyValue( component, index, getEntityMode() );
+		final Object baseValue = type.getPropertyValue( component, index );
 		if ( loc > 0 ) {
 			if ( baseValue == null ) {
 				return null;
 			}
 			return getComponentValue(
 					(ComponentType) type.getSubtypes()[index],
 					baseValue,
 					propertyPath.substring(loc+1)
 			);
 		}
 		else {
 			return baseValue;
 		}
 
 	}
 
 	private int findSubPropertyIndex(ComponentType type, String subPropertyName) {
 		final String[] propertyNames = type.getPropertyNames();
 		for ( int index = 0; index<propertyNames.length; index++ ) {
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
 		setters[ entityMetamodel.getPropertyIndex( propertyName ) ].set( entity, value, getFactory() );
 	}
 
 	@Override
 	public final Object instantiate(Serializable id) throws HibernateException {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		return instantiate( id, null );
 	}
 
 	@Override
 	public final Object instantiate(Serializable id, SessionImplementor session) {
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
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object entity) {
 		// the default is to simply not lazy fetch properties for now...
 		return false;
 	}
 
 	@Override
 	public final boolean isInstance(Object object) {
         return getInstantiator().isInstance( object );
 	}
 
 	@Override
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy();
 	}
 
 	@Override
 	public final Object createProxy(Serializable id, SessionImplementor session)
 	throws HibernateException {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
index f706f3cc89..6ed37bf5a4 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
@@ -1,814 +1,808 @@
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
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.tuple.component.ComponentTuplizer;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 
 /**
  * Handles "component" mappings
  *
  * @author Gavin King
  */
 public class ComponentType extends AbstractType implements CompositeType, ProcedureParameterExtractionAware {
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyNullability;
 	protected final int propertySpan;
 	private final CascadeStyle[] cascade;
 	private final FetchMode[] joinedFetch;
 	private final boolean isKey;
 
 	protected final EntityMode entityMode;
 	protected final ComponentTuplizer componentTuplizer;
 
 	public ComponentType(TypeFactory.TypeScope typeScope, ComponentMetamodel metamodel) {
 		this.typeScope = typeScope;
 		// for now, just "re-flatten" the metamodel since this is temporary stuff anyway (HHH-1907)
 		this.isKey = metamodel.isKey();
 		this.propertySpan = metamodel.getPropertySpan();
 		this.propertyNames = new String[ propertySpan ];
 		this.propertyTypes = new Type[ propertySpan ];
 		this.propertyNullability = new boolean[ propertySpan ];
 		this.cascade = new CascadeStyle[ propertySpan ];
 		this.joinedFetch = new FetchMode[ propertySpan ];
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			StandardProperty prop = metamodel.getProperty( i );
 			this.propertyNames[i] = prop.getName();
 			this.propertyTypes[i] = prop.getType();
 			this.propertyNullability[i] = prop.isNullable();
 			this.cascade[i] = prop.getCascadeStyle();
 			this.joinedFetch[i] = prop.getFetchMode();
 		}
 
 		this.entityMode = metamodel.getEntityMode();
 		this.componentTuplizer = metamodel.getComponentTuplizer();
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
 		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
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
 		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
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
 		if ( x == null || y == null ) {
 			return false;
 		}
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
-    public boolean isEqual(Object x, Object y)
-			throws HibernateException {
+	public boolean isEqual(final Object x, final Object y) throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
-		Object[] xvalues = getPropertyValues( x, entityMode );
-		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
-			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i] ) ) {
+			if ( !propertyTypes[i].isEqual( getPropertyValue( x, i ), getPropertyValue( y, i ) ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
-    public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory)
-			throws HibernateException {
+	public boolean isEqual(final Object x, final Object y, final SessionFactoryImplementor factory) throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
-		Object[] xvalues = getPropertyValues( x, entityMode );
-		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
-			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i], factory ) ) {
+			if ( !propertyTypes[i].isEqual( getPropertyValue( x, i ), getPropertyValue( y, i ), factory ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
-    public int compare(Object x, Object y) {
+	public int compare(final Object x, final Object y) {
 		if ( x == y ) {
 			return 0;
 		}
-		Object[] xvalues = getPropertyValues( x, entityMode );
-		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
-			int propertyCompare = propertyTypes[i].compare( xvalues[i], yvalues[i] );
+			int propertyCompare = propertyTypes[i].compare( getPropertyValue( x, i ), getPropertyValue( y, i ) );
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
-    public int getHashCode(Object x) {
+	public int getHashCode(final Object x) {
 		int result = 17;
-		Object[] values = getPropertyValues( x, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
-			Object y = values[i];
+			Object y = getPropertyValue( x, i );
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y );
 			}
 		}
 		return result;
 	}
 
 	@Override
-    public int getHashCode(Object x, SessionFactoryImplementor factory) {
+	public int getHashCode(final Object x, final SessionFactoryImplementor factory) {
 		int result = 17;
-		Object[] values = getPropertyValues( x, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
-			Object y = values[i];
+			Object y = getPropertyValue( x, i );
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y, factory );
 			}
 		}
 		return result;
 	}
 
 	@Override
-    public boolean isDirty(Object x, Object y, SessionImplementor session)
-			throws HibernateException {
+	public boolean isDirty(final Object x, final Object y, final SessionImplementor session) throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
-		Object[] xvalues = getPropertyValues( x, entityMode );
-		Object[] yvalues = getPropertyValues( y, entityMode );
-		for ( int i = 0; i < xvalues.length; i++ ) {
-			if ( propertyTypes[i].isDirty( xvalues[i], yvalues[i], session ) ) {
+		for ( int i = 0; i < propertySpan; i++ ) {
+			if ( propertyTypes[i].isDirty( getPropertyValue( x, i ), getPropertyValue( y, i ), session ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
-	public boolean isDirty(Object x, Object y, boolean[] checkable, SessionImplementor session)
-			throws HibernateException {
+	public boolean isDirty(final Object x, final Object y, final boolean[] checkable, final SessionImplementor session) throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
-		Object[] xvalues = getPropertyValues( x, entityMode );
-		Object[] yvalues = getPropertyValues( y, entityMode );
 		int loc = 0;
-		for ( int i = 0; i < xvalues.length; i++ ) {
+		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			if ( len <= 1 ) {
 				final boolean dirty = ( len == 0 || checkable[loc] ) &&
-				                      propertyTypes[i].isDirty( xvalues[i], yvalues[i], session );
+				                      propertyTypes[i].isDirty( getPropertyValue( x, i ), getPropertyValue( y, i ), session );
 				if ( dirty ) {
 					return true;
 				}
 			}
 			else {
 				boolean[] subcheckable = new boolean[len];
 				System.arraycopy( checkable, loc, subcheckable, 0, len );
-				final boolean dirty = propertyTypes[i].isDirty( xvalues[i], yvalues[i], subcheckable, session );
+				final boolean dirty = propertyTypes[i].isDirty( getPropertyValue( x, i ), getPropertyValue( y, i ), subcheckable, session );
 				if ( dirty ) {
 					return true;
 				}
 			}
 			loc += len;
 		}
 		return false;
 	}
 
 	@Override
-    public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
-			throws HibernateException {
-
+	public boolean isModified(final Object old, final Object current, final boolean[] checkable, final SessionImplementor session) throws HibernateException {
 		if ( current == null ) {
 			return old != null;
 		}
 		if ( old == null ) {
 			return true;
 		}
-		Object[] currentValues = getPropertyValues( current, session );
 		Object[] oldValues = ( Object[] ) old;
 		int loc = 0;
-		for ( int i = 0; i < currentValues.length; i++ ) {
+		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			boolean[] subcheckable = new boolean[len];
 			System.arraycopy( checkable, loc, subcheckable, 0, len );
-			if ( propertyTypes[i].isModified( oldValues[i], currentValues[i], subcheckable, session ) ) {
+			if ( propertyTypes[i].isModified( oldValues[i], getPropertyValue( current, i ), subcheckable, session ) ) {
 				return true;
 			}
 			loc += len;
 		}
 		return false;
 
 	}
 	@Override
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( hydrate( rs, names, session, owner ), session, owner );
 	}
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value, int begin, SessionImplementor session)
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
 			SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, entityMode );
 
 		int loc = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
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
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 	@Override
 	public Object getPropertyValue(Object component, int i, SessionImplementor session)
 			throws HibernateException {
-		return getPropertyValue( component, i, entityMode );
+		return getPropertyValue( component, i );
 	}
-
 	public Object getPropertyValue(Object component, int i, EntityMode entityMode)
 			throws HibernateException {
-		return componentTuplizer.getPropertyValue( component, i );
+		return getPropertyValue( component, i );
 	}
+
+	public Object getPropertyValue(Object component, int i)
+			throws HibernateException {
+		if ( component instanceof Object[] ) {
+			// A few calls to hashCode pass the property values already in an
+			// Object[] (ex: QueryKey hash codes for cached queries).
+			// It's easiest to just check for the condition here prior to
+			// trying reflection.
+			return (( Object[] ) component)[i];
+		} else {
+			return componentTuplizer.getPropertyValue( component, i );
+		}
+	}
+
 	@Override
 	public Object[] getPropertyValues(Object component, SessionImplementor session)
 			throws HibernateException {
 		return getPropertyValues( component, entityMode );
 	}
 	@Override
 	public Object[] getPropertyValues(Object component, EntityMode entityMode)
 			throws HibernateException {
 		if ( component instanceof Object[] ) {
 			// A few calls to hashCode pass the property values already in an 
 			// Object[] (ex: QueryKey hash codes for cached queries).
 			// It's easiest to just check for the condition here prior to
 			// trying reflection.
 			return ( Object[] ) component;
 		} else {
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
 		Map<String,String> result = new HashMap<String, String>();
 		Object[] values = getPropertyValues( value, entityMode );
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			result.put( propertyNames[i], propertyTypes[i].toLoggableString( values[i], factory ) );
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
 			SessionImplementor session,
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
 			SessionImplementor session,
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
 
 	public Object instantiate(Object parent, SessionImplementor session)
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
     public Serializable disassemble(Object value, SessionImplementor session, Object owner)
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
     public Object assemble(Serializable object, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( object == null ) {
 			return null;
 		}
 		else {
 			Object[] values = ( Object[] ) object;
 			Object[] assembled = new Object[values.length];
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				assembled[i] = propertyTypes[i].assemble( ( Serializable ) values[i], session, owner );
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
 			final SessionImplementor session,
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
     public Object resolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( value != null ) {
 			Object result = instantiate( owner, session );
 			Object[] values = ( Object[] ) value;
 			Object[] resolvedValues = new Object[values.length]; //only really need new array during semiresolve!
 			for ( int i = 0; i < values.length; i++ ) {
 				resolvedValues[i] = propertyTypes[i].resolve( values[i], session, owner );
 			}
 			setPropertyValues( result, resolvedValues, entityMode );
 			return result;
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
     public Object semiResolve(Object value, SessionImplementor session, Object owner)
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
     public boolean isXMLElement() {
 		return true;
 	}
 	@Override
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return xml;
 	}
 	@Override
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
 		replaceNode( node, ( Element ) value );
 	}
 	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan( mapping ) ];
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
 			if ( ! ProcedureParameterExtractionAware.class.isInstance( propertyType ) ) {
 				return false;
 			}
 			if ( ! ( (ProcedureParameterExtractionAware) propertyType ).canDoExtraction() ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public Object extract(CallableStatement statement, int startIndex, SessionImplementor session) throws SQLException {
 		Object[] values = new Object[propertySpan];
 
 		int currentIndex = startIndex;
 		boolean notNull = false;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			// we know this cast is safe from canDoExtraction
 			final ProcedureParameterExtractionAware propertyType = (ProcedureParameterExtractionAware) propertyTypes[i];
 			final Object value = propertyType.extract( statement, currentIndex, session );
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
 
 		if ( ! notNull ) {
 			values = null;
 		}
 
 		return resolve( values, session, null );
 	}
 
 	@Override
 	public Object extract(CallableStatement statement, String[] paramNames, SessionImplementor session) throws SQLException {
 		// for this form to work all sub-property spans must be one (1)...
 
 		Object[] values = new Object[propertySpan];
 
 		int indx = 0;
 		boolean notNull = false;
 		for ( String paramName : paramNames ) {
 			// we know this cast is safe from canDoExtraction
 			final ProcedureParameterExtractionAware propertyType = (ProcedureParameterExtractionAware) propertyTypes[indx];
 			final Object value = propertyType.extract( statement, new String[] { paramName }, session );
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
 
 		if ( ! notNull ) {
 			values = null;
 		}
 
 		return resolve( values, session, null );
 	}
 }
