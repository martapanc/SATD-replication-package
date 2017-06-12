diff --git a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
old mode 100644
new mode 100755
index 0451e09dcc..4ee8ed7744
--- a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
@@ -1,707 +1,704 @@
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
 
-import java.io.Serializable;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.util.Map;
-
 import org.dom4j.Element;
 import org.dom4j.Node;
-
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.internal.ForeignKeys;
-import org.hibernate.engine.spi.EntityUniqueKey;
-import org.hibernate.engine.spi.Mapping;
-import org.hibernate.engine.spi.PersistenceContext;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.*;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.tuple.ElementWrapper;
 
+import java.io.Serializable;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.util.Map;
+
 /**
  * Base for types which map associations to persistent entities.
  *
  * @author Gavin King
  */
 public abstract class EntityType extends AbstractType implements AssociationType {
 
 	private final TypeFactory.TypeScope scope;
 	private final String associatedEntityName;
 	protected final String uniqueKeyPropertyName;
 	protected final boolean isEmbeddedInXML;
 	private final boolean eager;
 	private final boolean unwrapProxy;
 
 	private transient Class returnedClass;
 
 	/**
 	 * Constructs the requested entity type mapping.
 	 *
 	 * @param scope The type scope
 	 * @param entityName The name of the associated entity.
 	 * @param uniqueKeyPropertyName The property-ref name, or null if we
 	 * reference the PK of the associated entity.
 	 * @param eager Is eager fetching enabled.
 	 * @param isEmbeddedInXML Should values of this mapping be embedded in XML modes?
 	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
 	 * says to return the "implementation target" of lazy prooxies; typically only possible
 	 * with lazy="no-proxy".
 	 *
 	 * @deprecated Use {@link #EntityType(TypeFactory.TypeScope, String, String, boolean, boolean )} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	protected EntityType(
 			TypeFactory.TypeScope scope,
 			String entityName,
 			String uniqueKeyPropertyName,
 			boolean eager,
 			boolean isEmbeddedInXML,
 			boolean unwrapProxy) {
 		this.scope = scope;
 		this.associatedEntityName = entityName;
 		this.uniqueKeyPropertyName = uniqueKeyPropertyName;
 		this.isEmbeddedInXML = isEmbeddedInXML;
 		this.eager = eager;
 		this.unwrapProxy = unwrapProxy;
 	}
 
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
 	 */
 	protected EntityType(
 			TypeFactory.TypeScope scope,
 			String entityName,
 			String uniqueKeyPropertyName,
 			boolean eager,
 			boolean unwrapProxy) {
 		this.scope = scope;
 		this.associatedEntityName = entityName;
 		this.uniqueKeyPropertyName = uniqueKeyPropertyName;
 		this.isEmbeddedInXML = true;
 		this.eager = eager;
 		this.unwrapProxy = unwrapProxy;
 	}
 
 	protected TypeFactory.TypeScope scope() {
 		return scope;
 	}
 
 	/**
 	 * An entity type is a type of association type
 	 *
 	 * @return True.
 	 */
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	/**
 	 * Explicitly, an entity type is an entity type ;)
 	 *
 	 * @return True.
 	 */
 	public final boolean isEntityType() {
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isMutable() {
 		return false;
 	}
 
 	/**
 	 * Generates a string representation of this type.
 	 *
 	 * @return string rep
 	 */
 	public String toString() {
 		return getClass().getName() + '(' + getAssociatedEntityName() + ')';
 	}
 
 	/**
 	 * For entity types, the name correlates to the associated entity name.
 	 */
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
 		return uniqueKeyPropertyName==null;
 	}
 
 	public String getRHSUniqueKeyPropertyName() {
 		return uniqueKeyPropertyName;
 	}
 
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
 	 * @return The associated entity name.
 	 */
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
 		return getAssociatedEntityName();
 	}
 
 	/**
 	 * Retrieves the {@link Joinable} defining the associated entity.
 	 *
 	 * @param factory The session factory.
 	 * @return The associated joinable
 	 * @throws MappingException Generally indicates an invalid entity name.
 	 */
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) throws MappingException {
 		return ( Joinable ) factory.getEntityPersister( associatedEntityName );
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
 	public final Class getReturnedClass() {
 		if ( returnedClass == null ) {
 			returnedClass = determineAssociatedEntityClass();
 		}
 		return returnedClass;
 	}
 
-	private Class determineAssociatedEntityClass() {
-		try {
-			return ReflectHelper.classForName( getAssociatedEntityName() );
-		}
-		catch ( ClassNotFoundException cnfe ) {
-			return java.util.Map.class;
-		}
-	}
+    private Class determineAssociatedEntityClass() {
+        final String entityName = getAssociatedEntityName();
+        try {
+            return ReflectHelper.classForName(entityName);
+        }
+        catch ( ClassNotFoundException cnfe ) {
+            return this.scope.resolveFactory().getEntityPersister(entityName).
+                getEntityTuplizer().getMappedClass();
+        }
+    }
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException {
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public final Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		return resolve( hydrate(rs, names, session, owner), session, owner );
 	}
 
 	/**
 	 * Two entities are considered the same when their instances are the same.
 	 *
 	 *
 	 * @param x One entity instance
 	 * @param y Another entity instance
 	 * @return True if x == y; false otherwise.
 	 */
 	public final boolean isSame(Object x, Object y) {
 		return x == y;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public int compare(Object x, Object y) {
 		return 0; //TODO: entities CAN be compared, by PK, fix this! -> only if/when we can extract the id values....
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return value; //special case ... this is the leaf of the containment graph, even though not immutable
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache) throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		Object cached = copyCache.get(original);
 		if ( cached != null ) {
 			return cached;
 		}
 		else {
 			if ( original == target ) {
 				return target;
 			}
 			if ( session.getContextEntityIdentifier( original ) == null  &&
 					ForeignKeys.isTransient( associatedEntityName, original, Boolean.FALSE, session ) ) {
 				final Object copy = session.getFactory().getEntityPersister( associatedEntityName )
 						.instantiate( null, session );
 				//TODO: should this be Session.instantiate(Persister, ...)?
 				copyCache.put( original, copy );
 				return copy;
 			}
 			else {
 				Object id = getIdentifier( original, session );
 				if ( id == null ) {
 					throw new AssertionFailure("non-transient entity has a null id");
 				}
 				id = getIdentifierOrUniqueKeyType( session.getFactory() )
 						.replace(id, null, session, owner, copyCache);
 				return resolve( id, session, owner );
 			}
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public int getHashCode(Object x, SessionFactoryImplementor factory) {
 		EntityPersister persister = factory.getEntityPersister(associatedEntityName);
 		if ( !persister.canExtractIdOutOfEntity() ) {
 			return super.getHashCode( x );
 		}
 
 		final Serializable id;
 		if (x instanceof HibernateProxy) {
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
 
 		EntityPersister persister = factory.getEntityPersister(associatedEntityName);
 		if ( !persister.canExtractIdOutOfEntity() ) {
 			return super.isEqual(x, y );
 		}
 
 		final Class mappedClass = persister.getMappedClass();
 		Serializable xid;
 		if (x instanceof HibernateProxy) {
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
 		if (y instanceof HibernateProxy) {
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
 				.isEqual(xid, yid, factory);
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isEmbeddedInXML() {
 		return isEmbeddedInXML;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isXMLElement() {
 		return isEmbeddedInXML;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			return getIdentifierType(factory).fromXMLNode(xml, factory);
 		}
 		else {
 			return xml;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			getIdentifierType(factory).setToXMLNode(node, value, factory);
 		}
 		else {
 			Element elt = (Element) value;
 			replaceNode( node, new ElementWrapper(elt) );
 		}
 	}
 
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
 	throws MappingException {
 		if ( isReferenceToPrimaryKey() ) { //TODO: this is a bit arbitrary, expose a switch to the user?
 			return "";
 		}
 		else {
 			return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
 		}
 	}
 
 	/**
 	 * Resolve an identifier or unique key value
 	 */
 	public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		if ( isNotEmbedded( session ) ) {
 			return value;
 		}
 
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			if ( isNull( owner, session ) ) {
 				return null; //EARLY EXIT!
 			}
 
 			if ( isReferenceToPrimaryKey() ) {
 				return resolveIdentifier( (Serializable) value, session );
 			}
 			else {
 				return loadByUniqueKey( getAssociatedEntityName(), uniqueKeyPropertyName, value, session );
 			}
 		}
 	}
 
 	public Type getSemiResolvedType(SessionFactoryImplementor factory) {
 		return factory.getEntityPersister( associatedEntityName ).getIdentifierType();
 	}
 
 	protected final Object getIdentifier(Object value, SessionImplementor session) throws HibernateException {
 		if ( isNotEmbedded(session) ) {
 			return value;
 		}
 
 		if ( isReferenceToPrimaryKey() ) {
 			return ForeignKeys.getEntityIdentifierIfNotUnsaved( getAssociatedEntityName(), value, session ); //tolerates nulls
 		}
 		else if ( value == null ) {
 			return null;
 		}
 		else {
 			EntityPersister entityPersister = session.getFactory().getEntityPersister( getAssociatedEntityName() );
 			Object propertyValue = entityPersister.getPropertyValue( value, uniqueKeyPropertyName );
 			// We now have the value of the property-ref we reference.  However,
 			// we need to dig a little deeper, as that property might also be
 			// an entity type, in which case we need to resolve its identitifier
 			Type type = entityPersister.getPropertyType( uniqueKeyPropertyName );
 			if ( type.isEntityType() ) {
 				propertyValue = ( ( EntityType ) type ).getIdentifier( propertyValue, session );
 			}
 
 			return propertyValue;
 		}
 	}
 
 	/**
 	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	protected boolean isNotEmbedded(SessionImplementor session) {
 //		return !isEmbeddedInXML;
 		return false;
 	}
 
 	/**
 	 * Generate a loggable representation of an instance of the value mapped by this type.
 	 *
 	 * @param value The instance to be logged.
 	 * @param factory The session factory.
 	 * @return The loggable string.
 	 * @throws HibernateException Generally some form of resolution problem.
 	 */
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) {
 		if ( value == null ) {
 			return "null";
 		}
 		
 		EntityPersister persister = factory.getEntityPersister( associatedEntityName );
 		StringBuilder result = new StringBuilder().append( associatedEntityName );
 
 		if ( persister.hasIdentifierProperty() ) {
 			final EntityMode entityMode = persister.getEntityMode();
 			final Serializable id;
 			if ( entityMode == null ) {
 				if ( isEmbeddedInXML ) {
 					throw new ClassCastException( value.getClass().getName() );
 				}
 				id = ( Serializable ) value;
 			} else if ( value instanceof HibernateProxy ) {
 				HibernateProxy proxy = ( HibernateProxy ) value;
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
 	 * @return The identifier type
 	 */
 	Type getIdentifierType(Mapping factory) {
 		return factory.getIdentifierType( getAssociatedEntityName() );
 	}
 
 	/**
 	 * Convenience method to locate the identifier type of the associated entity.
 	 *
 	 * @param session The originating session
 	 * @return The identifier type
 	 */
 	Type getIdentifierType(SessionImplementor session) {
 		return getIdentifierType( session.getFactory() );
 	}
 
 	/**
 	 * Determine the type of either (1) the identifier if we reference the
 	 * associated entity's PK or (2) the unique key to which we refer (i.e.
 	 * the property-ref).
 	 *
 	 * @param factory The mappings...
 	 * @return The appropriate type.
 	 * @throws MappingException Generally, if unable to resolve the associated entity name
 	 * or unique key property name.
 	 */
 	public final Type getIdentifierOrUniqueKeyType(Mapping factory) throws MappingException {
 		if ( isReferenceToPrimaryKey() ) {
 			return getIdentifierType(factory);
 		}
 		else {
 			Type type = factory.getReferencedPropertyType( getAssociatedEntityName(), uniqueKeyPropertyName );
 			if ( type.isEntityType() ) {
 				type = ( ( EntityType ) type).getIdentifierOrUniqueKeyType( factory );
 			}
 			return type;
 		}
 	}
 
 	/**
 	 * The name of the property on the associated entity to which our FK
 	 * refers
 	 *
 	 * @param factory The mappings...
 	 * @return The appropriate property name.
 	 * @throws MappingException Generally, if unable to resolve the associated entity name
 	 */
 	public final String getIdentifierOrUniqueKeyPropertyName(Mapping factory)
 	throws MappingException {
 		if ( isReferenceToPrimaryKey() ) {
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
 	 * @return The resolved identifier (i.e., loaded entity).
 	 * @throws org.hibernate.HibernateException Indicates problems performing the load.
 	 */
 	protected final Object resolveIdentifier(Serializable id, SessionImplementor session) throws HibernateException {
 		boolean isProxyUnwrapEnabled = unwrapProxy &&
 				session.getFactory()
 						.getEntityPersister( getAssociatedEntityName() )
 						.isInstrumented();
 
 		Object proxyOrEntity = session.internalLoad(
 				getAssociatedEntityName(),
 				id,
 				eager,
 				isNullable() && !isProxyUnwrapEnabled
 		);
 
 		if ( proxyOrEntity instanceof HibernateProxy ) {
 			( ( HibernateProxy ) proxyOrEntity ).getHibernateLazyInitializer()
 					.setUnwrap( isProxyUnwrapEnabled );
 		}
 
 		return proxyOrEntity;
 	}
 
 	protected boolean isNull(Object owner, SessionImplementor session) {
 		return false;
 	}
 
 	/**
 	 * Load an instance by a unique key that is not the primary key.
 	 *
 	 * @param entityName The name of the entity to load
 	 * @param uniqueKeyPropertyName The name of the property defining the uniqie key.
 	 * @param key The unique key property value.
 	 * @param session The originating session.
 	 * @return The loaded entity
 	 * @throws HibernateException generally indicates problems performing the load.
 	 */
 	public Object loadByUniqueKey(
 			String entityName, 
 			String uniqueKeyPropertyName, 
 			Object key, 
 			SessionImplementor session) throws HibernateException {
 		final SessionFactoryImplementor factory = session.getFactory();
 		UniqueKeyLoadable persister = ( UniqueKeyLoadable ) factory.getEntityPersister( entityName );
 
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
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/CriteriaQueryImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/CriteriaQueryImpl.java
old mode 100644
new mode 100755
index 1f5e1151f6..b44ae088bc
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/CriteriaQueryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/CriteriaQueryImpl.java
@@ -1,436 +1,437 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.jpa.criteria;
 
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.persistence.Query;
 import javax.persistence.Tuple;
 import javax.persistence.criteria.CriteriaQuery;
 import javax.persistence.criteria.Expression;
 import javax.persistence.criteria.Order;
 import javax.persistence.criteria.ParameterExpression;
 import javax.persistence.criteria.Predicate;
 import javax.persistence.criteria.Root;
 import javax.persistence.criteria.Selection;
 import javax.persistence.criteria.Subquery;
 import javax.persistence.metamodel.EntityType;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.jpa.internal.QueryImpl;
 import org.hibernate.jpa.criteria.compile.CompilableCriteria;
 import org.hibernate.jpa.criteria.compile.CriteriaInterpretation;
 import org.hibernate.jpa.criteria.compile.CriteriaQueryTypeQueryAdapter;
 import org.hibernate.jpa.criteria.compile.ImplicitParameterBinding;
 import org.hibernate.jpa.criteria.compile.InterpretedParameterMetadata;
 import org.hibernate.jpa.criteria.compile.RenderingContext;
 import org.hibernate.jpa.spi.HibernateEntityManagerImplementor;
 import org.hibernate.type.Type;
 
 /**
  * The Hibernate implementation of the JPA {@link CriteriaQuery} contract.  Mostly a set of delegation to its
  * internal {@link QueryStructure}.
  *
  * @author Steve Ebersole
  */
 public class CriteriaQueryImpl<T> extends AbstractNode implements CriteriaQuery<T>, CompilableCriteria, Serializable {
 	private static final Logger log = Logger.getLogger( CriteriaQueryImpl.class );
 
 	private final Class<T> returnType;
 
 	private final QueryStructure<T> queryStructure;
 	private List<Order> orderSpecs = Collections.emptyList();
 
 
 	public CriteriaQueryImpl(
 			CriteriaBuilderImpl criteriaBuilder,
 			Class<T> returnType) {
 		super( criteriaBuilder );
 		this.returnType = returnType;
 		this.queryStructure = new QueryStructure<T>( this, criteriaBuilder );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Class<T> getResultType() {
 		return returnType;
 	}
 
 
 	// SELECTION ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public CriteriaQuery<T> distinct(boolean applyDistinction) {
 		queryStructure.setDistinct( applyDistinction );
 		return this;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isDistinct() {
 		return queryStructure.isDistinct();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Selection<T> getSelection() {
 		return ( Selection<T> ) queryStructure.getSelection();
 	}
 
 	public void applySelection(Selection<? extends T> selection) {
 		queryStructure.setSelection( selection );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public CriteriaQuery<T> select(Selection<? extends T> selection) {
 		applySelection( selection );
 		return this;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public CriteriaQuery<T> multiselect(Selection<?>... selections) {
 		return multiselect( Arrays.asList( selections ) );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public CriteriaQuery<T> multiselect(List<Selection<?>> selections) {
 		final Selection<? extends T> selection;
 
 		if ( Tuple.class.isAssignableFrom( getResultType() ) ) {
 			selection = ( Selection<? extends T> ) criteriaBuilder().tuple( selections );
 		}
 		else if ( getResultType().isArray() ) {
 			selection = ( Selection<? extends T> )  criteriaBuilder().array(
 					( Class<? extends Object[]> ) getResultType(),
 					selections
 			);
 		}
 		else if ( Object.class.equals( getResultType() ) ) {
 			switch ( selections.size() ) {
 				case 0: {
 					throw new IllegalArgumentException(
 							"empty selections passed to criteria query typed as Object"
 					);
 				}
 				case 1: {
 					selection = ( Selection<? extends T> ) selections.get( 0 );
 					break;
 				}
 				default: {
 					selection = ( Selection<? extends T> ) criteriaBuilder().array( selections );
 				}
 			}
 		}
 		else {
 			selection = criteriaBuilder().construct( getResultType(), selections );
 		}
 		applySelection( selection );
 		return this;
 	}
 
 
 	// ROOTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Set<Root<?>> getRoots() {
 		return queryStructure.getRoots();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public <X> Root<X> from(EntityType<X> entityType) {
 		return queryStructure.from( entityType );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public <X> Root<X> from(Class<X> entityClass) {
 		return queryStructure.from( entityClass );
 	}
 
 
 	// RESTRICTION ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Predicate getRestriction() {
 		return queryStructure.getRestriction();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public CriteriaQuery<T> where(Expression<Boolean> expression) {
 		queryStructure.setRestriction( criteriaBuilder().wrap( expression ) );
 		return this;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public CriteriaQuery<T> where(Predicate... predicates) {
 		// TODO : assuming this should be a conjuntion, but the spec does not say specifically...
 		queryStructure.setRestriction( criteriaBuilder().and( predicates ) );
 		return this;
 	}
 
 
 	// GROUPING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public List<Expression<?>> getGroupList() {
 		return queryStructure.getGroupings();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public CriteriaQuery<T> groupBy(Expression<?>... groupings) {
 		queryStructure.setGroupings( groupings );
 		return this;
 	}
 
 	public CriteriaQuery<T> groupBy(List<Expression<?>> groupings) {
 		queryStructure.setGroupings( groupings );
 		return this;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Predicate getGroupRestriction() {
 		return queryStructure.getHaving();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public CriteriaQuery<T> having(Expression<Boolean> expression) {
 		queryStructure.setHaving( criteriaBuilder().wrap( expression ) );
 		return this;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public CriteriaQuery<T> having(Predicate... predicates) {
 		queryStructure.setHaving( criteriaBuilder().and( predicates ) );
 		return this;
 	}
 
 
 	// ORDERING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public List<Order> getOrderList() {
 		return orderSpecs;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public CriteriaQuery<T> orderBy(Order... orders) {
 		if ( orders != null && orders.length > 0 ) {
 			orderSpecs = Arrays.asList( orders );
 		}
 		else {
 			orderSpecs = Collections.emptyList();
 		}
 		return this;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public CriteriaQuery<T> orderBy(List<Order> orders) {
 		orderSpecs = orders;
 		return this;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Set<ParameterExpression<?>> getParameters() {
 		return queryStructure.getParameters();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public <U> Subquery<U> subquery(Class<U> subqueryType) {
 		return queryStructure.subquery( subqueryType );
 	}
 
 	public void validate() {
 		// getRoots() is explicitly supposed to return empty if none defined, no need to check for null
 		if ( getRoots().isEmpty() ) {
 			throw new IllegalStateException( "No criteria query roots were specified" );
 		}
 
 		// if there is not an explicit selection, there is an *implicit* selection of the root entity provided only
 		// a single query root was defined.
 		if ( getSelection() == null && !hasImplicitSelection() ) {
 			throw new IllegalStateException( "No explicit selection and an implicit one cold not be determined" );
 		}
 	}
 
 	/**
 	 * If no explicit selection was defined, we have a condition called an implicit selection if the query specified
 	 * a single {@link Root} and the java type of that {@link Root root's} model is the same as this criteria's
 	 * {@link #getResultType() result type}.
 	 *
 	 * @return True if there is an explicit selection; false otherwise.
 	 */
 	private boolean hasImplicitSelection() {
 		if ( getRoots().size() != 1 ) {
 			return false;
 		}
 
 		Root root = getRoots().iterator().next();
-		if ( root.getModel().getJavaType() != returnType ) {
+        Class<?> javaType = root.getModel().getJavaType();
+        if ( javaType != null && javaType != returnType ) {
 			return false;
 		}
 
 		// if we get here, the query defined no selection but defined a single root of the same type as the
 		// criteria query return, so we use that as the implicit selection
 		//
 		// todo : should we put an implicit marker in the selection to this fact to make later processing easier?
 		return true;
 	}
 
 	@Override
 	public CriteriaInterpretation interpret(RenderingContext renderingContext) {
 		final StringBuilder jpaqlBuffer = new StringBuilder();
 
 		queryStructure.render( jpaqlBuffer, renderingContext );
 
 		if ( ! getOrderList().isEmpty() ) {
 			jpaqlBuffer.append( " order by " );
 			String sep = "";
 			for ( Order orderSpec : getOrderList() ) {
 				jpaqlBuffer.append( sep )
 						.append( ( ( Renderable ) orderSpec.getExpression() ).render( renderingContext ) )
 						.append( orderSpec.isAscending() ? " asc" : " desc" );
 				sep = ", ";
 			}
 		}
 
 		final String jpaqlString = jpaqlBuffer.toString();
 
 		log.debugf( "Rendered criteria query -> %s", jpaqlString );
 
 		return new CriteriaInterpretation() {
 			@Override
 			@SuppressWarnings("unchecked")
 			public Query buildCompiledQuery(HibernateEntityManagerImplementor entityManager, final InterpretedParameterMetadata parameterMetadata) {
 
 				QueryImpl jpaqlQuery = entityManager.createQuery(
 						jpaqlString,
 						getResultType(),
 						getSelection(),
 						new HibernateEntityManagerImplementor.Options() {
 							@Override
 							public List<ValueHandlerFactory.ValueHandler> getValueHandlers() {
 								SelectionImplementor selection = (SelectionImplementor) queryStructure.getSelection();
 								return selection == null
 										? null
 										: selection.getValueHandlers();
 							}
 
 							@Override
 							public Map<String, Class> getNamedParameterExplicitTypes() {
 								return parameterMetadata.implicitParameterTypes();
 							}
 
 							@Override
 							public ResultMetadataValidator getResultMetadataValidator() {
 								return new HibernateEntityManagerImplementor.Options.ResultMetadataValidator() {
 									@Override
 									public void validate(Type[] returnTypes) {
 										SelectionImplementor selection = (SelectionImplementor) queryStructure.getSelection();
 										if ( selection != null ) {
 											if ( selection.isCompoundSelection() ) {
 												if ( returnTypes.length != selection.getCompoundSelectionItems().size() ) {
 													throw new IllegalStateException(
 															"Number of return values [" + returnTypes.length +
 																	"] did not match expected [" +
 																	selection.getCompoundSelectionItems().size() + "]"
 													);
 												}
 											}
 											else {
 												if ( returnTypes.length > 1 ) {
 													throw new IllegalStateException(
 															"Number of return values [" + returnTypes.length +
 																	"] did not match expected [1]"
 													);
 												}
 											}
 										}
 									}
 								};							}
 						}
 				);
 
 				for ( ImplicitParameterBinding implicitParameterBinding : parameterMetadata.implicitParameterBindings() ) {
 					implicitParameterBinding.bind( jpaqlQuery );
 				}
 
 				return new CriteriaQueryTypeQueryAdapter(
 						jpaqlQuery,
 						parameterMetadata.explicitParameterMapping(),
 						parameterMetadata.explicitParameterNameMapping()
 				);
 			}
 		};
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/predicate/InPredicate.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/predicate/InPredicate.java
old mode 100644
new mode 100755
index 3291575b08..5344c32354
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/predicate/InPredicate.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/predicate/InPredicate.java
@@ -1,194 +1,195 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.jpa.criteria.predicate;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.List;
 import javax.persistence.criteria.Expression;
 import javax.persistence.criteria.Subquery;
 
 import org.hibernate.jpa.criteria.CriteriaBuilderImpl;
 import org.hibernate.jpa.criteria.ParameterRegistry;
 import org.hibernate.jpa.criteria.Renderable;
 import org.hibernate.jpa.criteria.ValueHandlerFactory;
 import org.hibernate.jpa.criteria.compile.RenderingContext;
 import org.hibernate.jpa.criteria.expression.LiteralExpression;
 
 /**
  * Models an <tt>[NOT] IN</tt> restriction
  *
  * @author Steve Ebersole
  */
 public class InPredicate<T>
 		extends AbstractSimplePredicate
 		implements CriteriaBuilderImpl.In<T>, Serializable {
 	private final Expression<? extends T> expression;
 	private final List<Expression<? extends T>> values;
 
 	/**
 	 * Constructs an <tt>IN</tt> predicate against a given expression with an empty list of values.
 	 *
 	 * @param criteriaBuilder The query builder from which this originates.
 	 * @param expression The expression.
 	 */
 	public InPredicate(
 			CriteriaBuilderImpl criteriaBuilder,
 			Expression<? extends T> expression) {
 		this( criteriaBuilder, expression, new ArrayList<Expression<? extends T>>() );
 	}
 
 	/**
 	 * Constructs an <tt>IN</tt> predicate against a given expression with the given list of expression values.
 	 *
 	 * @param criteriaBuilder The query builder from which this originates.
 	 * @param expression The expression.
 	 * @param values The value list.
 	 */
 	public InPredicate(
 			CriteriaBuilderImpl criteriaBuilder,
 			Expression<? extends T> expression,
 			Expression<? extends T>... values) {
 		this( criteriaBuilder, expression, Arrays.asList( values ) );
 	}
 
 	/**
 	 * Constructs an <tt>IN</tt> predicate against a given expression with the given list of expression values.
 	 *
 	 * @param criteriaBuilder The query builder from which this originates.
 	 * @param expression The expression.
 	 * @param values The value list.
 	 */
 	public InPredicate(
 			CriteriaBuilderImpl criteriaBuilder,
 			Expression<? extends T> expression,
 			List<Expression<? extends T>> values) {
 		super( criteriaBuilder );
 		this.expression = expression;
 		this.values = values;
 	}
 
 	/**
 	 * Constructs an <tt>IN</tt> predicate against a given expression with the given given literal value list.
 	 *
 	 * @param criteriaBuilder The query builder from which this originates.
 	 * @param expression The expression.
 	 * @param values The value list.
 	 */
 	public InPredicate(
 			CriteriaBuilderImpl criteriaBuilder,
 			Expression<? extends T> expression,
 			T... values) {
 		this( criteriaBuilder, expression, Arrays.asList( values ) );
 	}
 
 	/**
 	 * Constructs an <tt>IN</tt> predicate against a given expression with the given literal value list.
 	 *
 	 * @param criteriaBuilder The query builder from which this originates.
 	 * @param expression The expression.
 	 * @param values The value list.
 	 */
 	public InPredicate(
 			CriteriaBuilderImpl criteriaBuilder,
 			Expression<? extends T> expression,
 			Collection<T> values) {
 		super( criteriaBuilder );
 		this.expression = expression;
 		this.values = new ArrayList<Expression<? extends T>>( values.size() );
-		ValueHandlerFactory.ValueHandler<? extends T> valueHandler = ValueHandlerFactory.isNumeric( expression.getJavaType() )
-				? ValueHandlerFactory.determineAppropriateHandler( (Class<? extends T>) expression.getJavaType() )
+        final Class<? extends T> javaType = expression.getJavaType();
+        ValueHandlerFactory.ValueHandler<? extends T> valueHandler = javaType != null && ValueHandlerFactory.isNumeric(javaType)
+            ? ValueHandlerFactory.determineAppropriateHandler((Class<? extends T>) javaType)
 				: new ValueHandlerFactory.NoOpValueHandler<T>();
 		for ( T value : values ) {
 			this.values.add(
 					new LiteralExpression<T>( criteriaBuilder, valueHandler.convert( value ) )
 			);
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	public Expression<T> getExpression() {
 		return ( Expression<T> ) expression;
 	}
 
 	public Expression<? extends T> getExpressionInternal() {
 		return expression;
 	}
 
 	public List<Expression<? extends T>> getValues() {
 		return values;
 	}
 
 	public InPredicate<T> value(T value) {
 		return value( new LiteralExpression<T>( criteriaBuilder(), value ) );
 	}
 
 	public InPredicate<T> value(Expression<? extends T> value) {
 		values.add( value );
 		return this;
 	}
 
 	public void registerParameters(ParameterRegistry registry) {
 		Helper.possibleParameter( getExpressionInternal(), registry );
 		for ( Expression value : getValues() ) {
 			Helper.possibleParameter(value, registry);
 		}
 	}
 
 	public String render(RenderingContext renderingContext) {
 		StringBuilder buffer = new StringBuilder();
 
 		buffer.append( ( (Renderable) getExpression() ).render( renderingContext ) );
 
 		if ( isNegated() ) {
 			buffer.append( " not" );
 		}
 		buffer.append( " in " );
 
 		// subquery expressions are already wrapped in parenthesis, so we only need to
 		// render the parenthesis here if the values represent an explicit value list
 		boolean isInSubqueryPredicate = getValues().size() == 1
 				&& Subquery.class.isInstance( getValues().get( 0 ) );
 		if ( isInSubqueryPredicate ) {
 			buffer.append( ( (Renderable) getValues().get(0) ).render( renderingContext ) );
 		}
 		else {
 			buffer.append( '(' );
 			String sep = "";
 			for ( Expression value : getValues() ) {
 				buffer.append( sep )
 						.append( ( (Renderable) value ).render( renderingContext ) );
 				sep = ", ";
 			}
 			buffer.append( ')' );
 		}
 		return buffer.toString();
 	}
 
 	public String renderProjection(RenderingContext renderingContext) {
 		return render( renderingContext );
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractIdentifiableType.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractIdentifiableType.java
index 774a994985..1d9f85deb2 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractIdentifiableType.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractIdentifiableType.java
@@ -1,317 +1,318 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.jpa.internal.metamodel;
 import java.io.Serializable;
 import java.util.HashSet;
 import java.util.Set;
 import javax.persistence.metamodel.Attribute;
 import javax.persistence.metamodel.IdentifiableType;
 import javax.persistence.metamodel.SingularAttribute;
 import javax.persistence.metamodel.Type;
 
 /**
  * Defines commonality for the JPA {@link IdentifiableType} types.  JPA defines
  * identifiable types as entities or mapped-superclasses.  Basically things to which an
  * identifier can be attached.
  * <p/>
  * NOTE : Currently we only really have support for direct entities in the Hibernate metamodel
  * as the information for them is consumed into the closest actual entity subclass(es) in the
  * internal Hibernate mapping-metamodel.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractIdentifiableType<X>
 		extends AbstractManagedType<X>
 		implements IdentifiableType<X>, Serializable {
 
 	private final boolean hasIdentifierProperty;
 	private final boolean isVersioned;
 
 	private SingularAttributeImpl<X, ?> id;
 	private SingularAttributeImpl<X, ?> version;
 	private Set<SingularAttribute<? super X,?>> idClassAttributes;
 
 	public AbstractIdentifiableType(
 			Class<X> javaType,
+			String typeName,
 			AbstractIdentifiableType<? super X> superType,
 			boolean hasIdentifierProperty,
 			boolean versioned) {
-		super( javaType, superType );
+		super( javaType, typeName, superType );
 		this.hasIdentifierProperty = hasIdentifierProperty;
 		isVersioned = versioned;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public AbstractIdentifiableType<? super X> getSupertype() {
 		return ( AbstractIdentifiableType<? super X> ) super.getSupertype();
 	}
 
 	/**
 	 * Indicates if a non-null super type is required to provide the
 	 * identifier attribute(s) if this object does not have a declared
 	 * identifier.
 	 * .
 	 * @return true, if a non-null super type is required to provide
 	 * the identifier attribute(s) if this object does not have a
 	 * declared identifier; false, otherwise.
 	 */
 	protected abstract boolean requiresSupertypeForNonDeclaredIdentifier();
 
 	protected AbstractIdentifiableType<? super X> requireSupertype() {
 		if ( getSupertype() == null ) {
 			throw new IllegalStateException( "No supertype found" );
 		}
 		return getSupertype();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean hasSingleIdAttribute() {
 		return hasIdentifierProperty;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <Y> SingularAttribute<? super X, Y> getId(Class<Y> javaType) {
 		final SingularAttribute<? super X, Y> id_;
 		if ( id != null ) {
 			checkSimpleId();
 			id_ = ( SingularAttribute<? super X, Y> ) id;
 			if ( javaType != id.getJavaType() ) {
 				throw new IllegalArgumentException( "Id attribute was not of specified type : " + javaType.getName() );
 			}
 		}
 		else {
 			//yuk yuk bad me
 			if ( ! requiresSupertypeForNonDeclaredIdentifier()) {
 				final AbstractIdentifiableType<? super X> supertype = getSupertype();
 				if (supertype != null) {
 					id_ = supertype.getId( javaType );
 				}
 				else {
 					id_ = null;
 				}
 			}
 			else {
 				id_ = requireSupertype().getId( javaType );
 			}
 		}
 		return id_;
 	}
 
 	/**
 	 * Centralized check to ensure the id for this hierarchy is a simple one (i.e., does not use
 	 * an id-class).
 	 *
 	 * @see #checkIdClass()
 	 */
 	protected void checkSimpleId() {
 		if ( ! hasIdentifierProperty ) {
 			throw new IllegalStateException( "This class uses an @IdClass" );
 		}
 	}
 
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> javaType) {
 		checkDeclaredId();
 		checkSimpleId();
 		if ( javaType != id.getJavaType() ) {
 			throw new IllegalArgumentException( "Id attribute was not of specified type : " + javaType.getName() );
 		}
 		return (SingularAttribute<X, Y>) id;
 	}
 
 	/**
 	 * Centralized check to ensure the id is actually declared on the class mapped here, as opposed to a
 	 * super class.
 	 */
 	protected void checkDeclaredId() {
 		if ( id == null ) {
 			throw new IllegalArgumentException( "The id attribute is not declared on this type" );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Type<?> getIdType() {
 		if ( id != null ) {
 			checkSimpleId();
 			return id.getType();
 		}
 		else {
 			return requireSupertype().getIdType();
 		}
 	}
 
 	private boolean hasIdClassAttributesDefined() {
 		return idClassAttributes != null ||
 				( getSupertype() != null && getSupertype().hasIdClassAttributesDefined() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
 		if ( idClassAttributes != null ) {
 			checkIdClass();
 		}
 		else {
 			// Java does not allow casting requireSupertype().getIdClassAttributes()
 			// to Set<SingularAttribute<? super X, ?>> because the
 			// superclass X is a different Java type from this X
 			// (i.e, getSupertype().getJavaType() != getJavaType()).
 			// It will, however, allow a Set<SingularAttribute<? super X, ?>>
 			// to be initialized with requireSupertype().getIdClassAttributes(),
 			// since getSupertype().getJavaType() is a superclass of getJavaType()
 			if ( requiresSupertypeForNonDeclaredIdentifier() ) {
 				idClassAttributes = new HashSet<SingularAttribute<? super X, ?>>( requireSupertype().getIdClassAttributes() );
 			}
 			else if ( getSupertype() != null && hasIdClassAttributesDefined() ) {
 				idClassAttributes = new HashSet<SingularAttribute<? super X, ?>>( getSupertype().getIdClassAttributes() );
 			}
 		}
 		return idClassAttributes;
 	}
 
 	/**
 	 * Centralized check to ensure the id for this hierarchy uses an id-class.
 	 *
 	 * @see #checkSimpleId()
 	 */
 	private void checkIdClass() {
 		if ( hasIdentifierProperty ) {
 			throw new IllegalArgumentException( "This class does not use @IdClass" );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean hasVersionAttribute() {
 		return isVersioned;
 	}
 
 	public boolean hasDeclaredVersionAttribute() {
 		return isVersioned && version != null;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <Y> SingularAttribute<? super X, Y> getVersion(Class<Y> javaType) {
 		if ( ! hasVersionAttribute() ) {
 			return null;
 		}
 		final SingularAttribute<? super X, Y> version_;
 		if ( version != null ) {
 			version_ = ( SingularAttribute<? super X, Y> ) version;
 			if ( javaType != version.getJavaType() ) {
 				throw new IllegalArgumentException( "Version attribute was not of specified type : " + javaType.getName() );
 			}
 		}
 		else {
 			version_ = requireSupertype().getVersion( javaType );
 		}
 		return version_;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> javaType) {
 		checkDeclaredVersion();
 		if ( javaType != version.getJavaType() ) {
 			throw new IllegalArgumentException( "Version attribute was not of specified type : " + javaType.getName() );
 		}
 		return ( SingularAttribute<X, Y> ) version;
 	}
 
 	/**
 	 * For used to retrieve the declared version when populating the static metamodel.
 	 *
 	 * @return The declared
 	 */
 	public SingularAttribute<X, ?> getDeclaredVersion() {
 		checkDeclaredVersion();
 		return version;
 	}
 
 	/**
 	 * Centralized check to ensure the version (if one) is actually declared on the class mapped here, as opposed to a
 	 * super class.
 	 */
 	protected void checkDeclaredVersion() {
 		if ( version == null || ( getSupertype() != null && getSupertype().hasVersionAttribute() )) {
 			throw new IllegalArgumentException( "The version attribute is not declared on this type" );
 		}
 	}
 
 	public Builder<X> getBuilder() {
 		final AbstractManagedType.Builder<X> managedBuilder = super.getBuilder();
 		return new Builder<X>() {
 			public void applyIdAttribute(SingularAttributeImpl<X, ?> idAttribute) {
 				AbstractIdentifiableType.this.id = idAttribute;
 				managedBuilder.addAttribute( idAttribute );
 			}
 
 			public void applyIdClassAttributes(Set<SingularAttribute<? super X,?>> idClassAttributes) {
 				for ( SingularAttribute<? super X,?> idClassAttribute : idClassAttributes ) {
 					if ( AbstractIdentifiableType.this == idClassAttribute.getDeclaringType() ) {
 						@SuppressWarnings({ "unchecked" })
 						SingularAttribute<X,?> declaredAttribute = ( SingularAttribute<X,?> ) idClassAttribute;
 						addAttribute( declaredAttribute );
 					}
 				}
 				AbstractIdentifiableType.this.idClassAttributes = idClassAttributes;
 			}
 
 			public void applyVersionAttribute(SingularAttributeImpl<X, ?> versionAttribute) {
 				AbstractIdentifiableType.this.version = versionAttribute;
 				managedBuilder.addAttribute( versionAttribute );
 			}
 
 			public void addAttribute(Attribute<X, ?> attribute) {
 				managedBuilder.addAttribute( attribute );
 			}
 		};
 	}
 
 	public static interface Builder<X> extends AbstractManagedType.Builder<X> {
 		public void applyIdAttribute(SingularAttributeImpl<X,?> idAttribute);
 		public void applyIdClassAttributes(Set<SingularAttribute<? super X,?>> idClassAttributes);
 		public void applyVersionAttribute(SingularAttributeImpl<X,?> versionAttribute);
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractManagedType.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractManagedType.java
index b7f846bee7..effc185a7f 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractManagedType.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractManagedType.java
@@ -1,529 +1,529 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.jpa.internal.metamodel;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 import javax.persistence.metamodel.Attribute;
 import javax.persistence.metamodel.Bindable;
 import javax.persistence.metamodel.CollectionAttribute;
 import javax.persistence.metamodel.ListAttribute;
 import javax.persistence.metamodel.ManagedType;
 import javax.persistence.metamodel.MapAttribute;
 import javax.persistence.metamodel.PluralAttribute;
 import javax.persistence.metamodel.SetAttribute;
 import javax.persistence.metamodel.SingularAttribute;
 
 import org.hibernate.annotations.common.AssertionFailure;
 
 /**
  * Defines commonality for the JPA {@link ManagedType} hierarchy of interfaces.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractManagedType<X> 
 		extends AbstractType<X>
 		implements ManagedType<X>, Serializable {
 
 	private final  AbstractManagedType<? super X> superType;
 
 	private final Map<String,Attribute<X, ?>> declaredAttributes
 			= new HashMap<String, Attribute<X,?>>();
 	private final Map<String, SingularAttribute<X, ?>> declaredSingularAttributes
 			= new HashMap<String, SingularAttribute<X,?>>();
 	private final Map<String, PluralAttribute<X, ?, ?>> declaredPluralAttributes
 			= new HashMap<String, PluralAttribute<X,?,?>>();
 
-	protected AbstractManagedType(Class<X> javaType, AbstractManagedType<? super X> superType) {
-		super( javaType );
+	protected AbstractManagedType(Class<X> javaType, String typeName, AbstractManagedType<? super X> superType) {
+		super( javaType, typeName );
 		this.superType = superType;
 	}
 
 	protected AbstractManagedType<? super X> getSupertype() {
 		return superType;
 	}
 
 	private boolean locked = false;
 
 	public Builder<X> getBuilder() {
 		if ( locked ) {
 			throw new IllegalStateException( "Type has been locked" );
 		}
 		return new Builder<X>() {
 			public void addAttribute(Attribute<X,?> attribute) {
 				declaredAttributes.put( attribute.getName(), attribute );
 				final Bindable.BindableType bindableType = ( ( Bindable ) attribute ).getBindableType();
 				switch ( bindableType ) {
 					case SINGULAR_ATTRIBUTE : {
 						declaredSingularAttributes.put( attribute.getName(), (SingularAttribute<X,?>) attribute );
 						break;
 					}
 					case PLURAL_ATTRIBUTE : {
 						declaredPluralAttributes.put(attribute.getName(), (PluralAttribute<X,?,?>) attribute );
 						break;
 					}
 					default : {
 						throw new AssertionFailure( "unknown bindable type: " + bindableType );
 					}
 				}
 			}
 		};
 	}
 
 	public void lock() {
 		locked = true;
 	}
 
 	public static interface Builder<X> {
 		public void addAttribute(Attribute<X,?> attribute);
 	}
 
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Set<Attribute<? super X, ?>> getAttributes() {
 		HashSet attributes = new HashSet<Attribute<X, ?>>( declaredAttributes.values() );
 		if ( getSupertype() != null ) {
 			attributes.addAll( getSupertype().getAttributes() );
 		}
 		return attributes;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Set<Attribute<X, ?>> getDeclaredAttributes() {
 		return new HashSet<Attribute<X, ?>>( declaredAttributes.values() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Attribute<? super X, ?> getAttribute(String name) {
 		Attribute<? super X, ?> attribute = declaredAttributes.get( name );
 		if ( attribute == null && getSupertype() != null ) {
 			attribute = getSupertype().getAttribute( name );
 		}
 		return attribute;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Attribute<X, ?> getDeclaredAttribute(String name) {
 		Attribute<X, ?> attr = declaredAttributes.get( name );
 		checkNotNull( "Attribute ", attr, name );
 		return attr;
 	}
 
 	private void checkNotNull(String attributeType, Attribute<?,?> attribute, String name) {
 		if ( attribute == null ) {
 			throw new IllegalArgumentException( attributeType + " named " + name + " is not present" );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Set<SingularAttribute<? super X, ?>> getSingularAttributes() {
 		HashSet attributes = new HashSet<SingularAttribute<X, ?>>( declaredSingularAttributes.values() );
 		if ( getSupertype() != null ) {
 			attributes.addAll( getSupertype().getSingularAttributes() );
 		}
 		return attributes;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes() {
 		return new HashSet<SingularAttribute<X, ?>>( declaredSingularAttributes.values() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public SingularAttribute<? super X, ?> getSingularAttribute(String name) {
 		SingularAttribute<? super X, ?> attribute = declaredSingularAttributes.get( name );
 		if ( attribute == null && getSupertype() != null ) {
 			attribute = getSupertype().getSingularAttribute( name );
 		}
 		return attribute;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public SingularAttribute<X, ?> getDeclaredSingularAttribute(String name) {
 		final SingularAttribute<X, ?> attr = declaredSingularAttributes.get( name );
 		checkNotNull( "SingularAttribute ", attr, name );
 		return attr;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <Y> SingularAttribute<? super X, Y> getSingularAttribute(String name, Class<Y> type) {
 		SingularAttribute<? super X, ?> attribute = declaredSingularAttributes.get( name );
 		if ( attribute == null && getSupertype() != null ) {
 			attribute = getSupertype().getSingularAttribute( name );
 		}
 		checkTypeForSingleAttribute( "SingularAttribute ", attribute, name, type );
 		return ( SingularAttribute<? super X, Y> ) attribute;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings( "unchecked")
 	public <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String name, Class<Y> javaType) {
 		final SingularAttribute<X, ?> attr = declaredSingularAttributes.get( name );
 		checkTypeForSingleAttribute( "SingularAttribute ", attr, name, javaType );
 		return ( SingularAttribute<X, Y> ) attr;
 	}
 
 	private <Y> void checkTypeForSingleAttribute(
 			String attributeType,
 			SingularAttribute<?,?> attribute,
 			String name,
 			Class<Y> javaType) {
 		if ( attribute == null || ( javaType != null && !attribute.getBindableJavaType().equals( javaType ) ) ) {
 			if ( isPrimitiveVariant( attribute, javaType ) ) {
 				return;
 			}
 			throw new IllegalArgumentException(
 					attributeType + " named " + name
 					+ ( javaType != null ? " and of type " + javaType.getName() : "" )
 					+ " is not present"
 			);
 		}
 	}
 
 	@SuppressWarnings({ "SimplifiableIfStatement" })
 	protected <Y> boolean isPrimitiveVariant(SingularAttribute<?,?> attribute, Class<Y> javaType) {
 		if ( attribute == null ) {
 			return false;
 		}
 		Class declaredType = attribute.getBindableJavaType();
 
 		if ( declaredType.isPrimitive() ) {
 			return ( Boolean.class.equals( javaType ) && Boolean.TYPE.equals( declaredType ) )
 					|| ( Character.class.equals( javaType ) && Character.TYPE.equals( declaredType ) )
 					|| ( Byte.class.equals( javaType ) && Byte.TYPE.equals( declaredType ) )
 					|| ( Short.class.equals( javaType ) && Short.TYPE.equals( declaredType ) )
 					|| ( Integer.class.equals( javaType ) && Integer.TYPE.equals( declaredType ) )
 					|| ( Long.class.equals( javaType ) && Long.TYPE.equals( declaredType ) )
 					|| ( Float.class.equals( javaType ) && Float.TYPE.equals( declaredType ) )
 					|| ( Double.class.equals( javaType ) && Double.TYPE.equals( declaredType ) );
 		}
 
 		if ( javaType.isPrimitive() ) {
 			return ( Boolean.class.equals( declaredType ) && Boolean.TYPE.equals( javaType ) )
 					|| ( Character.class.equals( declaredType ) && Character.TYPE.equals( javaType ) )
 					|| ( Byte.class.equals( declaredType ) && Byte.TYPE.equals( javaType ) )
 					|| ( Short.class.equals( declaredType ) && Short.TYPE.equals( javaType ) )
 					|| ( Integer.class.equals( declaredType ) && Integer.TYPE.equals( javaType ) )
 					|| ( Long.class.equals( declaredType ) && Long.TYPE.equals( javaType ) )
 					|| ( Float.class.equals( declaredType ) && Float.TYPE.equals( javaType ) )
 					|| ( Double.class.equals( declaredType ) && Double.TYPE.equals( javaType ) );
 		}
 
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Set<PluralAttribute<? super X, ?, ?>> getPluralAttributes() {
 		HashSet attributes = new HashSet<PluralAttribute<? super X, ?, ?>>( declaredPluralAttributes.values() );
 		if ( getSupertype() != null ) {
 			attributes.addAll( getSupertype().getPluralAttributes() );
 		}
 		return attributes;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Set<PluralAttribute<X, ?, ?>> getDeclaredPluralAttributes() {
 		return new HashSet<PluralAttribute<X,?,?>>( declaredPluralAttributes.values() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public CollectionAttribute<? super X, ?> getCollection(String name) {
 		PluralAttribute<? super X, ?, ?> attribute = getPluralAttribute( name );
 		if ( attribute == null && getSupertype() != null ) {
 			attribute = getSupertype().getPluralAttribute( name );
 		}
 		basicCollectionCheck( attribute, name );
 		return ( CollectionAttribute<X, ?> ) attribute;
 	}
 
 	private PluralAttribute<? super X, ?, ?> getPluralAttribute(String name) {
 		return declaredPluralAttributes.get( name );
 	}
 
 	private void basicCollectionCheck(PluralAttribute<? super X, ?, ?> attribute, String name) {
 		checkNotNull( "CollectionAttribute", attribute, name );
 		if ( ! CollectionAttribute.class.isAssignableFrom( attribute.getClass() ) ) {
 			throw new IllegalArgumentException( name + " is not a CollectionAttribute: " + attribute.getClass() );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings( "unchecked")
 	public CollectionAttribute<X, ?> getDeclaredCollection(String name) {
 		final PluralAttribute<X,?,?> attribute = declaredPluralAttributes.get( name );
 		basicCollectionCheck( attribute, name );
 		return ( CollectionAttribute<X, ?> ) attribute;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public SetAttribute<? super X, ?> getSet(String name) {
 		PluralAttribute<? super X, ?, ?> attribute = getPluralAttribute( name );
 		if ( attribute == null && getSupertype() != null ) {
 			attribute = getSupertype().getPluralAttribute( name );
 		}
 		basicSetCheck( attribute, name );
 		return (SetAttribute<? super X, ?>) attribute;
 	}
 
 	private void basicSetCheck(PluralAttribute<? super X, ?, ?> attribute, String name) {
 		checkNotNull( "SetAttribute", attribute, name );
 		if ( ! SetAttribute.class.isAssignableFrom( attribute.getClass() ) ) {
 			throw new IllegalArgumentException( name + " is not a SetAttribute: " + attribute.getClass() );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings( "unchecked")
 	public SetAttribute<X, ?> getDeclaredSet(String name) {
 		final PluralAttribute<X,?,?> attribute = declaredPluralAttributes.get( name );
 		basicSetCheck( attribute, name );
 		return ( SetAttribute<X, ?> ) attribute;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public ListAttribute<? super X, ?> getList(String name) {
 		PluralAttribute<? super X, ?, ?> attribute = getPluralAttribute( name );
 		if ( attribute == null && getSupertype() != null ) {
 			attribute = getSupertype().getPluralAttribute( name );
 		}
 		basicListCheck( attribute, name );
 		return (ListAttribute<? super X, ?>) attribute;
 	}
 
 	private void basicListCheck(PluralAttribute<? super X, ?, ?> attribute, String name) {
 		checkNotNull( "ListAttribute", attribute, name );
 		if ( ! ListAttribute.class.isAssignableFrom( attribute.getClass() ) ) {
 			throw new IllegalArgumentException( name + " is not a ListAttribute: " + attribute.getClass() );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public ListAttribute<X, ?> getDeclaredList(String name) {
 		final PluralAttribute<X,?,?> attribute = declaredPluralAttributes.get( name );
 		basicListCheck( attribute, name );
 		return ( ListAttribute<X, ?> ) attribute;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public MapAttribute<? super X, ?, ?> getMap(String name) {
 		PluralAttribute<? super X, ?, ?> attribute = getPluralAttribute( name );
 		if ( attribute == null && getSupertype() != null ) {
 			attribute = getSupertype().getPluralAttribute( name );
 		}
 		basicMapCheck( attribute, name );
 		return (MapAttribute<? super X, ?, ?>) attribute;
 	}
 
 	private void basicMapCheck(PluralAttribute<? super X, ?, ?> attribute, String name) {
 		checkNotNull( "MapAttribute", attribute, name );
 		if ( ! MapAttribute.class.isAssignableFrom( attribute.getClass() ) ) {
 			throw new IllegalArgumentException( name + " is not a MapAttribute: " + attribute.getClass() );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public MapAttribute<X, ?, ?> getDeclaredMap(String name) {
 		final PluralAttribute<X,?,?> attribute = declaredPluralAttributes.get( name );
 		basicMapCheck( attribute, name );
 		return ( MapAttribute<X,?,?> ) attribute;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <E> CollectionAttribute<? super X, E> getCollection(String name, Class<E> elementType) {
 		PluralAttribute<? super X, ?, ?> attribute = declaredPluralAttributes.get( name );
 		if ( attribute == null && getSupertype() != null ) {
 			attribute = getSupertype().getPluralAttribute( name );
 		}
 		checkCollectionElementType( attribute, name, elementType );
 		return ( CollectionAttribute<? super X, E> ) attribute;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public <E> CollectionAttribute<X, E> getDeclaredCollection(String name, Class<E> elementType) {
 		final PluralAttribute<X,?,?> attribute = declaredPluralAttributes.get( name );
 		checkCollectionElementType( attribute, name, elementType );
 		return ( CollectionAttribute<X, E> ) attribute;
 	}
 
 	private <E> void checkCollectionElementType(PluralAttribute<?,?,?> attribute, String name, Class<E> elementType) {
 		checkTypeForPluralAttributes( "CollectionAttribute", attribute, name, elementType, PluralAttribute.CollectionType.COLLECTION );
 	}
 
 	private <E> void checkTypeForPluralAttributes(
 			String attributeType,
 			PluralAttribute<?,?,?> attribute,
 			String name,
 			Class<E> elementType,
 			PluralAttribute.CollectionType collectionType) {
 		if ( attribute == null
 				|| ( elementType != null && !attribute.getBindableJavaType().equals( elementType ) )
 				|| attribute.getCollectionType() != collectionType ) {
 			throw new IllegalArgumentException(
 					attributeType + " named " + name
 					+ ( elementType != null ? " and of element type " + elementType : "" )
 					+ " is not present"
 			);
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <E> SetAttribute<? super X, E> getSet(String name, Class<E> elementType) {
 		PluralAttribute<? super X, ?, ?> attribute = declaredPluralAttributes.get( name );
 		if ( attribute == null && getSupertype() != null ) {
 			attribute = getSupertype().getPluralAttribute( name );
 		}
 		checkSetElementType( attribute, name, elementType );
 		return ( SetAttribute<? super X, E> ) attribute;
 	}
 
 	private <E> void checkSetElementType(PluralAttribute<? super X, ?, ?> attribute, String name, Class<E> elementType) {
 		checkTypeForPluralAttributes( "SetAttribute", attribute, name, elementType, PluralAttribute.CollectionType.SET );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public <E> SetAttribute<X, E> getDeclaredSet(String name, Class<E> elementType) {
 		final PluralAttribute<X,?,?> attribute = declaredPluralAttributes.get( name );
 		checkSetElementType( attribute, name, elementType );
 		return ( SetAttribute<X, E> ) attribute;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <E> ListAttribute<? super X, E> getList(String name, Class<E> elementType) {
 		PluralAttribute<? super X, ?, ?> attribute = declaredPluralAttributes.get( name );
 		if ( attribute == null && getSupertype() != null ) {
 			attribute = getSupertype().getPluralAttribute( name );
 		}
 		checkListElementType( attribute, name, elementType );
 		return ( ListAttribute<? super X, E> ) attribute;
 	}
 
 	private <E> void checkListElementType(PluralAttribute<? super X, ?, ?> attribute, String name, Class<E> elementType) {
 		checkTypeForPluralAttributes( "ListAttribute", attribute, name, elementType, PluralAttribute.CollectionType.LIST );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public <E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType) {
 		final PluralAttribute<X,?,?> attribute = declaredPluralAttributes.get( name );
 		checkListElementType( attribute, name, elementType );
 		return ( ListAttribute<X, E> ) attribute;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <K, V> MapAttribute<? super X, K, V> getMap(String name, Class<K> keyType, Class<V> valueType) {
 		PluralAttribute<? super X, ?, ?> attribute = getPluralAttribute( name );
 		if ( attribute == null && getSupertype() != null ) {
 			attribute = getSupertype().getPluralAttribute( name );
 		}
 		checkMapValueType( attribute, name, valueType );
 		final MapAttribute<? super X, K, V> mapAttribute = ( MapAttribute<? super X, K, V> ) attribute;
 		checkMapKeyType( mapAttribute, name, keyType );
 		return mapAttribute;
 	}
 
 	private <V> void checkMapValueType(PluralAttribute<? super X, ?, ?> attribute, String name, Class<V> valueType) {
 		checkTypeForPluralAttributes( "MapAttribute", attribute, name, valueType, PluralAttribute.CollectionType.MAP);
 	}
 
 	private <K,V> void checkMapKeyType(MapAttribute<? super X, K, V> mapAttribute, String name, Class<K> keyType) {
 		if ( mapAttribute.getKeyJavaType() != keyType ) {
 			throw new IllegalArgumentException( "MapAttribute named " + name + " does not support a key of type " + keyType );
 		}
 	}
 
 	public <K, V> MapAttribute<X, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType) {
 		final PluralAttribute<X,?,?> attribute = declaredPluralAttributes.get( name );
 		checkMapValueType( attribute, name, valueType );
 		final MapAttribute<X, K, V> mapAttribute = ( MapAttribute<X, K, V> ) attribute;
 		checkMapKeyType( mapAttribute, name, keyType );
 		return mapAttribute;
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractType.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractType.java
old mode 100644
new mode 100755
index 4e391bdf31..84cb83723b
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractType.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractType.java
@@ -1,43 +1,79 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.jpa.internal.metamodel;
+
 import java.io.Serializable;
 import javax.persistence.metamodel.Type;
 
 /**
  * Defines commonality for the JPA {@link Type} hierarchy of interfaces.
  *
  * @author Steve Ebersole
+ * @author Brad Koehn
  */
 public abstract class AbstractType<X> implements Type<X>, Serializable {
-	private final Class<X> javaType;
+    private final Class<X> javaType;
+    private final String typeName;
+
+	/**
+	 * Instantiates the type based on the given Java type.
+	 *
+	 * @param javaType The Java type of the JPA model type.
+	 */
+    protected AbstractType(Class<X> javaType) {
+		this( javaType, javaType != null ? javaType.getName() : null );
+    }
 
-	public AbstractType(Class<X> javaType) {
+	/**
+	 * Instantiates the type based on the given Java type.
+	 *
+	 * @param javaType
+	 * @param typeName
+	 */
+	protected AbstractType(Class<X> javaType, String typeName) {
 		this.javaType = javaType;
+		this.typeName = typeName == null ? "unknown" : typeName;
 	}
 
-	public Class<X> getJavaType() {
-		return javaType;
-	}
+	/**
+	 * {@inheritDoc}
+	 * <p/>
+	 * IMPL NOTE : The Hibernate version may return {@code null} here in the case of either dynamic models or
+	 * entity classes mapped multiple times using entity-name.  In these cases, the {@link #getTypeName()} value
+	 * should be used.
+	 */
+	@Override
+    public Class<X> getJavaType() {
+        return javaType;
+    }
+
+	/**
+	 * Obtains the type name.  See notes on {@link #getJavaType()} for details
+	 *
+	 * @return The type name
+	 */
+    public String getTypeName() {
+        return typeName;
+    }
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AttributeFactory.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AttributeFactory.java
old mode 100644
new mode 100755
index d3156df97d..52fd1cddc3
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AttributeFactory.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AttributeFactory.java
@@ -1,979 +1,997 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.jpa.internal.metamodel;
 
 import java.lang.reflect.Field;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.lang.reflect.ParameterizedType;
 import java.lang.reflect.TypeVariable;
 import java.util.Iterator;
 import javax.persistence.ManyToMany;
 import javax.persistence.OneToOne;
 import javax.persistence.metamodel.Attribute;
 import javax.persistence.metamodel.IdentifiableType;
 import javax.persistence.metamodel.PluralAttribute;
 import javax.persistence.metamodel.Type;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.annotations.common.AssertionFailure;
 import org.hibernate.jpa.internal.EntityManagerMessageLogger;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.Map;
 import org.hibernate.mapping.OneToMany;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Value;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.EmbeddedComponentType;
 import org.hibernate.type.EntityType;
 
 /**
  * A factory for building {@link Attribute} instances.  Exposes 3 main services for building<ol>
  * <li>{@link #buildAttribute normal attributes}</li>
  * <li>{@link #buildIdAttribute id attributes}</li>
  * <li>{@link #buildVersionAttribute version attributes}</li>
  * <ol>
  *
  * @author Steve Ebersole
  * @author Emmanuel Bernard
  */
 public class AttributeFactory {
 
     private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
                                                                            AttributeFactory.class.getName());
 
 	private final MetadataContext context;
 
 	public AttributeFactory(MetadataContext context) {
 		this.context = context;
 	}
 
 	/**
 	 * Build a normal attribute.
 	 *
 	 * @param ownerType The descriptor of the attribute owner (aka declarer).
 	 * @param property The Hibernate property descriptor for the attribute
 	 * @param <X> The type of the owner
 	 * @param <Y> The attribute type
 	 * @return The built attribute descriptor or null if the attribute is not part of the JPA 2 model (eg backrefs)
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <X, Y> AttributeImplementor<X, Y> buildAttribute(AbstractManagedType<X> ownerType, Property property) {
 		if ( property.isSynthetic() ) {
 			// hide synthetic/virtual properties (fabricated by Hibernate) from the JPA metamodel.
             LOG.tracef(
 					"Skipping synthetic property %s(%s)",
-					ownerType.getJavaType().getName(),
+                    ownerType.getTypeName(),
 					property.getName()
 			);
 			return null;
 		}
-        LOG.trace("Building attribute [" + ownerType.getJavaType().getName() + "." + property.getName() + "]");
+        LOG.trace("Building attribute [" + ownerType.getTypeName() + "." + property.getName() + "]");
 		final AttributeContext<X> attributeContext = wrap( ownerType, property );
 		final AttributeMetadata<X,Y> attributeMetadata =
 				determineAttributeMetadata( attributeContext, NORMAL_MEMBER_RESOLVER );
         if (attributeMetadata == null) {
 			return null;
 		}
         if (attributeMetadata.isPlural()) {
 			return buildPluralAttribute((PluralAttributeMetadata)attributeMetadata);
 		}
         final SingularAttributeMetadata<X, Y> singularAttributeMetadata = (SingularAttributeMetadata<X, Y>)attributeMetadata;
         final Type<Y> metaModelType = getMetaModelType(singularAttributeMetadata.getValueContext());
         return new SingularAttributeImpl<X, Y>(
 				attributeMetadata.getName(),
 				attributeMetadata.getJavaType(),
 				ownerType,
 				attributeMetadata.getMember(),
 				false,
 				false,
 				property.isOptional(),
 				metaModelType,
 				attributeMetadata.getPersistentAttributeType()
 		);
 	}
 
 	private <X> AttributeContext<X> wrap(final AbstractManagedType<X> ownerType, final Property property) {
 		return new AttributeContext<X>() {
 			public AbstractManagedType<X> getOwnerType() {
 				return ownerType;
 			}
 
 			public Property getPropertyMapping() {
 				return property;
 			}
 		};
 	}
 
 	/**
 	 * Build the identifier attribute descriptor
 	 *
 	 * @param ownerType The descriptor of the attribute owner (aka declarer).
 	 * @param property The Hibernate property descriptor for the identifier attribute
 	 * @param <X> The type of the owner
 	 * @param <Y> The attribute type
 	 * @return The built attribute descriptor
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <X, Y> SingularAttributeImpl<X, Y> buildIdAttribute(AbstractIdentifiableType<X> ownerType, Property property) {
-        LOG.trace("Building identifier attribute [" + ownerType.getJavaType().getName() + "." + property.getName() + "]");
+        LOG.trace("Building identifier attribute [" + ownerType.getTypeName() + "." + property.getName() + "]");
 		final AttributeContext<X> attributeContext = wrap( ownerType, property );
 		final SingularAttributeMetadata<X,Y> attributeMetadata =
 				(SingularAttributeMetadata<X, Y>) determineAttributeMetadata( attributeContext, IDENTIFIER_MEMBER_RESOLVER );
 		final Type<Y> metaModelType = getMetaModelType( attributeMetadata.getValueContext() );
 		return new SingularAttributeImpl.Identifier(
 				property.getName(),
 				attributeMetadata.getJavaType(),
 				ownerType,
 				attributeMetadata.getMember(),
 				metaModelType,
 				attributeMetadata.getPersistentAttributeType()
 		);
 	}
 
 	/**
 	 * Build the version attribute descriptor
 	 *
 	 * @param ownerType The descriptor of the attribute owner (aka declarer).
 	 * @param property The Hibernate property descriptor for the version attribute
 	 * @param <X> The type of the owner
 	 * @param <Y> The attribute type
 	 * @return The built attribute descriptor
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <X, Y> SingularAttributeImpl<X, Y> buildVersionAttribute(AbstractIdentifiableType<X> ownerType, Property property) {
-        LOG.trace("Building version attribute [ownerType.getJavaType().getName()" + "." + "property.getName()]");
+        LOG.trace("Building version attribute [ownerType.getTypeName()" + "." + "property.getName()]");
 		final AttributeContext<X> attributeContext = wrap( ownerType, property );
 		final SingularAttributeMetadata<X,Y> attributeMetadata =
 				(SingularAttributeMetadata<X, Y>) determineAttributeMetadata( attributeContext, VERSION_MEMBER_RESOLVER );
 		final Type<Y> metaModelType = getMetaModelType( attributeMetadata.getValueContext() );
 		return new SingularAttributeImpl.Version(
 				property.getName(),
 				attributeMetadata.getJavaType(),
 				ownerType,
 				attributeMetadata.getMember(),
 				metaModelType,
 				attributeMetadata.getPersistentAttributeType()
 		);
 	}
 
 	@SuppressWarnings( "unchecked" )
 	private <X, Y, E, K> AttributeImplementor<X, Y> buildPluralAttribute(PluralAttributeMetadata<X,Y,E> attributeMetadata) {
 		final Type<E> elementType = getMetaModelType( attributeMetadata.getElementValueContext() );
 		if ( java.util.Map.class.isAssignableFrom( attributeMetadata.getJavaType() ) ) {
 			final Type<K> keyType = getMetaModelType( attributeMetadata.getMapKeyValueContext() );
 			return PluralAttributeImpl.create( attributeMetadata.getOwnerType(), elementType, attributeMetadata.getJavaType(), keyType )
 					.member( attributeMetadata.getMember() )
 					.property( attributeMetadata.getPropertyMapping() )
 					.persistentAttributeType( attributeMetadata.getPersistentAttributeType() )
 					.build();
 		}
         return PluralAttributeImpl.create(attributeMetadata.getOwnerType(), elementType, attributeMetadata.getJavaType(), null).member(attributeMetadata.getMember()).property(attributeMetadata.getPropertyMapping()).persistentAttributeType(attributeMetadata.getPersistentAttributeType()).build();
 	}
 
 	@SuppressWarnings( "unchecked" )
 	private <Y> Type<Y> getMetaModelType(ValueContext typeContext) {
 		switch ( typeContext.getValueClassification() ) {
 			case BASIC: {
 				return new BasicTypeImpl<Y>(
 						typeContext.getBindableType(),
 						Type.PersistenceType.BASIC
 				);
 			}
 			case ENTITY: {
 				final org.hibernate.type.EntityType type = (EntityType) typeContext.getValue().getType();
 				return (Type<Y>) context.locateEntityType( type.getAssociatedEntityName() );
 			}
 			case EMBEDDABLE: {
 				final Component component = (Component) typeContext.getValue();
 				final EmbeddableTypeImpl<Y> embeddableType = new EmbeddableTypeImpl<Y>(
 						typeContext.getBindableType(),
 						typeContext.getAttributeMetadata().getOwnerType(),
 						(ComponentType) typeContext.getValue().getType()
 				);
 				context.registerEmbeddedableType( embeddableType );
 				final Iterator<Property> subProperties = component.getPropertyIterator();
 				while ( subProperties.hasNext() ) {
 					final Property property = subProperties.next();
 					final AttributeImplementor<Y, Object> attribute = buildAttribute( embeddableType, property );
 					if ( attribute != null ) {
 						embeddableType.getBuilder().addAttribute( attribute );
 					}
 				}
 				embeddableType.lock();
 				return embeddableType;
 			}
 			default: {
 				throw new AssertionFailure( "Unknown type : " + typeContext.getValueClassification() );
 			}
 		}
 	}
 
-	private EntityMetamodel getDeclarerEntityMetamodel(IdentifiableType<?> ownerType) {
+	private EntityMetamodel getDeclarerEntityMetamodel(AbstractIdentifiableType<?> ownerType) {
 		final Type.PersistenceType persistenceType = ownerType.getPersistenceType();
 		if ( persistenceType == Type.PersistenceType.ENTITY) {
 			return context.getSessionFactory()
-					.getEntityPersister( ownerType.getJavaType().getName() )
+					.getEntityPersister( ownerType.getTypeName() )
 					.getEntityMetamodel();
 		}
 		else if ( persistenceType == Type.PersistenceType.MAPPED_SUPERCLASS) {
 			PersistentClass persistentClass =
 					context.getPersistentClassHostingProperties( (MappedSuperclassTypeImpl<?>) ownerType );
 			return context.getSessionFactory()
 				.getEntityPersister( persistentClass.getClassName() )
 				.getEntityMetamodel();
 		}
 		else {
 			throw new AssertionFailure( "Cannot get the metamodel for PersistenceType: " + persistenceType );
 		}
 	}
 
 	/**
 	 * A contract for defining the meta information about a {@link Value}
 	 */
 	private interface ValueContext {
 		/**
 		 * Enum of the simplified types a value might be.  These relate more to the Hibernate classification
 		 * then the JPA classification
 		 */
 		enum ValueClassification {
 			EMBEDDABLE,
 			ENTITY,
 			BASIC
 		}
 
 		/**
 		 * Retrieve the value itself
 		 *
 		 * @return The value
 		 */
 		public Value getValue();
 
 		public Class getBindableType();
 
 		/**
 		 * Retrieve the simplified value classification
 		 *
 		 * @return The value type
 		 */
 		public ValueClassification getValueClassification();
 
 		/**
 		 * Retrieve the metadata about the attribute from which this value comes
 		 *
 		 * @return The "containing" attribute metadata.
 		 */
 		public AttributeMetadata getAttributeMetadata();
 	}
 
 	/**
 	 * Basic contract for describing an attribute.  The "description" is partially in terms
 	 * of JPA ({@link #getPersistentAttributeType} and {@link #getOwnerType}), partially in
 	 * terms of Hibernate ({@link #getPropertyMapping}) and partially just in terms of the java
 	 * model itself ({@link #getName}, {@link #getMember} and {@link #getJavaType}).
 	 *
 	 * @param <X> The attribute owner type
 	 * @param <Y> The attribute type.
 	 */
 	private interface AttributeMetadata<X,Y> {
 		/**
 		 * Retrieve the name of the attribute
 		 *
 		 * @return The attribute name
 		 */
 		public String getName();
 
 		/**
 		 * Retrieve the member defining the attribute
 		 *
 		 * @return The attribute member
 		 */
 		public Member getMember();
 
 		/**
 		 * Retrieve the attribute java type.
 		 *
 		 * @return The java type of the attribute.
 		 */
 		public Class<Y> getJavaType();
 
 		/**
 		 * Get the JPA attribute type classification for this attribute.
 		 *
 		 * @return The JPA attribute type classification
 		 */
 		public Attribute.PersistentAttributeType getPersistentAttributeType();
 
 		/**
 		 * Retrieve the attribute owner's metamodel information
 		 *
 		 * @return The metamodel information for the attribute owner
 		 */
 		public AbstractManagedType<X> getOwnerType();
 
 		/**
 		 * Retrieve the Hibernate property mapping related to this attribute.
 		 *
 		 * @return The Hibernate property mapping
 		 */
 		public Property getPropertyMapping();
 
 		/**
 		 * Is the attribute plural (a collection)?
 		 *
 		 * @return True if it is plural, false otherwise.
 		 */
 		public boolean isPlural();
 	}
 
 	/**
 	 * Attribute metadata contract for a non-plural attribute.
 	 * @param <X> The owner type
 	 * @param <Y> The attribute type
 	 */
 	private interface SingularAttributeMetadata<X,Y>  extends AttributeMetadata<X,Y> {
 		/**
 		 * Retrieve the value context for this attribute
 		 *
 		 * @return The attributes value context
 		 */
 		public ValueContext getValueContext();
 	}
 
 	/**
 	 * Attribute metadata contract for a plural attribute.
 	 * @param <X> The owner type
 	 * @param <Y> The attribute type (the collection type)
 	 * @param <E> The collection element type
 	 */
 	private interface PluralAttributeMetadata<X,Y,E> extends AttributeMetadata<X,Y> {
 		/**
 		 * Retrieve the JPA collection type classification for this attribute
 		 *
 		 * @return The JPA collection type classification
 		 */
 		public PluralAttribute.CollectionType getAttributeCollectionType();
 
 		/**
 		 * Retrieve the value context for the collection's elements.
 		 *
 		 * @return The value context for the collection's elements.
 		 */
 		public ValueContext getElementValueContext();
 
 		/**
 		 * Retrieve the value context for the collection's keys (if a map, null otherwise).
 		 *
 		 * @return The value context for the collection's keys (if a map, null otherwise).
 		 */
 		public ValueContext getMapKeyValueContext();
 	}
 
 	/**
 	 * Bundle's a Hibernate property mapping together with the JPA metamodel information
 	 * of the attribute owner.
 	 *
 	 * @param <X> The owner type.
 	 */
 	private interface AttributeContext<X> {
 		/**
 		 * Retrieve the attribute owner.
 		 *
 		 * @return The owner.
 		 */
 		public AbstractManagedType<X> getOwnerType();
 
 		/**
 		 * Retrieve the Hibernate property mapping.
 		 *
 		 * @return The Hibvernate property mapping.
 		 */
 		public Property getPropertyMapping();
 	}
 
 	/**
 	 * Contract for how we resolve the {@link Member} for a give attribute context.
 	 */
 	private interface MemberResolver {
 		public Member resolveMember(AttributeContext attributeContext);
 	}
 
 	/**
 	 * Here is most of the nuts and bolts of this factory, where we interpret the known JPA metadata
 	 * against the known Hibernate metadata and build a descriptor for the attribute.
 	 *
 	 * @param attributeContext The attribute to be described
 	 * @param memberResolver Strategy for how to resolve the member defining the attribute.
 	 * @param <X> The owner type
 	 * @param <Y> The attribute type
 	 *
 	 * @return The attribute description
 	 */
 	@SuppressWarnings({ "unchecked" })
 	private <X,Y> AttributeMetadata<X,Y> determineAttributeMetadata(
 			AttributeContext<X> attributeContext,
 			MemberResolver memberResolver) {
         LOG.trace("Starting attribute metadata determination [" + attributeContext.getPropertyMapping().getName() + "]");
 		final Member member = memberResolver.resolveMember( attributeContext );
         LOG.trace("    Determined member [" + member + "]");
 
 		final Value value = attributeContext.getPropertyMapping().getValue();
 		final org.hibernate.type.Type type = value.getType();
         LOG.trace("    Determined type [name=" + type.getName() + ", class=" + type.getClass().getName() + "]");
 
 		if ( type.isAnyType() ) {
 			// ANY mappings are currently not supported in the JPA metamodel; see HHH-6589
             if ( context.isIgnoreUnsupported() ) {
                 return null;
             }
 			else {
                 throw new UnsupportedOperationException( "ANY not supported" );
             }
 		}
 		else if ( type.isAssociationType() ) {
 			// collection or entity
 			if ( type.isEntityType() ) {
 				// entity
 				return new SingularAttributeMetadataImpl<X,Y>(
 						attributeContext.getPropertyMapping(),
 						attributeContext.getOwnerType(),
 						member,
 						determineSingularAssociationAttributeType( member )
 				);
 			}
             // collection
             if (value instanceof Collection) {
                 final Collection collValue = (Collection)value;
                 final Value elementValue = collValue.getElement();
                 final org.hibernate.type.Type elementType = elementValue.getType();
 
                 // First, determine the type of the elements and use that to help determine the
                 // collection type)
                 final Attribute.PersistentAttributeType elementPersistentAttributeType;
                 final Attribute.PersistentAttributeType persistentAttributeType;
                 if (elementType.isAnyType()) {
                     throw new UnsupportedOperationException("collection of any not supported yet");
                 }
                 final boolean isManyToMany = isManyToMany(member);
                 if (elementValue instanceof Component) {
                     elementPersistentAttributeType = Attribute.PersistentAttributeType.EMBEDDED;
                     persistentAttributeType = Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
                 } else if (elementType.isAssociationType()) {
                     elementPersistentAttributeType = isManyToMany ? Attribute.PersistentAttributeType.MANY_TO_MANY : Attribute.PersistentAttributeType.ONE_TO_MANY;
                     persistentAttributeType = elementPersistentAttributeType;
                 } else {
                     elementPersistentAttributeType = Attribute.PersistentAttributeType.BASIC;
                     persistentAttributeType = Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
                 }
 
                 final Attribute.PersistentAttributeType keyPersistentAttributeType;
 
                 // Finally, we determine the type of the map key (if needed)
                 if (value instanceof Map) {
                     final Value keyValue = ((Map)value).getIndex();
                     final org.hibernate.type.Type keyType = keyValue.getType();
 
                     if (keyType.isAnyType()) throw new UnsupportedOperationException("collection of any not supported yet");
                     if (keyValue instanceof Component) keyPersistentAttributeType = Attribute.PersistentAttributeType.EMBEDDED;
                     else if (keyType.isAssociationType()) keyPersistentAttributeType = Attribute.PersistentAttributeType.MANY_TO_ONE;
                     else keyPersistentAttributeType = Attribute.PersistentAttributeType.BASIC;
                 } else keyPersistentAttributeType = null;
                 return new PluralAttributeMetadataImpl(attributeContext.getPropertyMapping(), attributeContext.getOwnerType(),
                                                        member, persistentAttributeType, elementPersistentAttributeType,
                                                        keyPersistentAttributeType);
             } else if (value instanceof OneToMany) {
                 // TODO : is this even possible??? Really OneToMany should be describing the
                 // element value within a o.h.mapping.Collection (see logic branch above)
                 throw new IllegalArgumentException("HUH???");
 //					final boolean isManyToMany = isManyToMany( member );
 //					//one to many with FK => entity
 //					return new PluralAttributeMetadataImpl(
 //							attributeContext.getPropertyMapping(),
 //							attributeContext.getOwnerType(),
 //							member,
 //							isManyToMany
 //									? Attribute.PersistentAttributeType.MANY_TO_MANY
 //									: Attribute.PersistentAttributeType.ONE_TO_MANY
 //							value,
 //							AttributeContext.TypeStatus.ENTITY,
 //							Attribute.PersistentAttributeType.ONE_TO_MANY,
 //							null, null, null
 //					);
 			}
 		}
 		else if ( attributeContext.getPropertyMapping().isComposite() ) {
 			// component
 			return new SingularAttributeMetadataImpl<X,Y>(
 					attributeContext.getPropertyMapping(),
 					attributeContext.getOwnerType(),
 					member,
 					Attribute.PersistentAttributeType.EMBEDDED
 			);
 		}
 		else {
 			// basic type
 			return new SingularAttributeMetadataImpl<X,Y>(
 					attributeContext.getPropertyMapping(),
 					attributeContext.getOwnerType(),
 					member,
 					Attribute.PersistentAttributeType.BASIC
 			);
 		}
 		throw new UnsupportedOperationException( "oops, we are missing something: " + attributeContext.getPropertyMapping() );
 	}
 
 	public static Attribute.PersistentAttributeType determineSingularAssociationAttributeType(Member member) {
 		if ( Field.class.isInstance( member ) ) {
 			return ( (Field) member ).getAnnotation( OneToOne.class ) != null
 					? Attribute.PersistentAttributeType.ONE_TO_ONE
 					: Attribute.PersistentAttributeType.MANY_TO_ONE;
 		}
+        else if (MapMember.class.isInstance( member ))  {
+            return  Attribute.PersistentAttributeType.MANY_TO_ONE; // curious to see how this works for non-annotated methods
+        }
 		else {
 			return ( (Method) member ).getAnnotation( OneToOne.class ) != null
 					? Attribute.PersistentAttributeType.ONE_TO_ONE
 					: Attribute.PersistentAttributeType.MANY_TO_ONE;
 		}
 	}
 
 	private abstract class BaseAttributeMetadata<X,Y> implements AttributeMetadata<X,Y> {
 		private final Property propertyMapping;
 		private final AbstractManagedType<X> ownerType;
 		private final Member member;
 		private final Class<Y> javaType;
 		private final Attribute.PersistentAttributeType persistentAttributeType;
 
 		@SuppressWarnings({ "unchecked" })
 		protected BaseAttributeMetadata(
 				Property propertyMapping,
 				AbstractManagedType<X> ownerType,
 				Member member,
 				Attribute.PersistentAttributeType persistentAttributeType) {
 			this.propertyMapping = propertyMapping;
 			this.ownerType = ownerType;
 			this.member = member;
 			this.persistentAttributeType = persistentAttributeType;
 			final Class declaredType;
 			// we can support method or field members here.  Is there really any other valid type?
 			if ( Field.class.isInstance( member ) ) {
 				declaredType = ( (Field) member ).getType();
 			}
 			else if ( Method.class.isInstance( member ) ) {
 				declaredType = ( (Method) member ).getReturnType();
 			}
+            else if ( MapMember.class.isInstance( member ) ) {
+                declaredType = ((MapMember) member).getType();
+            }
 			else {
 				throw new IllegalArgumentException( "Cannot determine java-type from given member [" + member + "]" );
 			}
 			this.javaType = accountForPrimitiveTypes( declaredType );
 		}
 
 		public String getName() {
 			return propertyMapping.getName();
 		}
 
 		public Member getMember() {
 			return member;
 		}
 
 		public String getMemberDescription() {
 			return determineMemberDescription( getMember() );
 		}
 
 		public String determineMemberDescription(Member member) {
 			return member.getDeclaringClass().getName() + '#' + member.getName();
 		}
 
 		public Class<Y> getJavaType() {
 			return javaType;
 		}
 
 		public Attribute.PersistentAttributeType getPersistentAttributeType() {
 			return persistentAttributeType;
 		}
 
 		public AbstractManagedType<X> getOwnerType() {
 			return ownerType;
 		}
 
 		public boolean isPlural() {
 			return propertyMapping.getType().isCollectionType();
 		}
 
 		public Property getPropertyMapping() {
 			return propertyMapping;
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected <Y> Class<Y> accountForPrimitiveTypes(Class<Y> declaredType) {
 //		if ( !declaredType.isPrimitive() ) {
 //			return declaredType;
 //		}
 //
 //		if ( Boolean.TYPE.equals( declaredType ) ) {
 //			return (Class<Y>) Boolean.class;
 //		}
 //		if ( Character.TYPE.equals( declaredType ) ) {
 //			return (Class<Y>) Character.class;
 //		}
 //		if( Byte.TYPE.equals( declaredType ) ) {
 //			return (Class<Y>) Byte.class;
 //		}
 //		if ( Short.TYPE.equals( declaredType ) ) {
 //			return (Class<Y>) Short.class;
 //		}
 //		if ( Integer.TYPE.equals( declaredType ) ) {
 //			return (Class<Y>) Integer.class;
 //		}
 //		if ( Long.TYPE.equals( declaredType ) ) {
 //			return (Class<Y>) Long.class;
 //		}
 //		if ( Float.TYPE.equals( declaredType ) ) {
 //			return (Class<Y>) Float.class;
 //		}
 //		if ( Double.TYPE.equals( declaredType ) ) {
 //			return (Class<Y>) Double.class;
 //		}
 //
 //		throw new IllegalArgumentException( "Unexpected type [" + declaredType + "]" );
 		// if the field is defined as int, return int not Integer...
 		return declaredType;
 	}
 
 	private class SingularAttributeMetadataImpl<X,Y>
 			extends BaseAttributeMetadata<X,Y>
 			implements SingularAttributeMetadata<X,Y> {
 		private final ValueContext valueContext;
 
 		private SingularAttributeMetadataImpl(
 				Property propertyMapping,
 				AbstractManagedType<X> ownerType,
 				Member member,
 				Attribute.PersistentAttributeType persistentAttributeType) {
 			super( propertyMapping, ownerType, member, persistentAttributeType );
 			valueContext = new ValueContext() {
 				public Value getValue() {
 					return getPropertyMapping().getValue();
 				}
 
 				public Class getBindableType() {
 					return getAttributeMetadata().getJavaType();
 				}
 
 				public ValueClassification getValueClassification() {
 					switch ( getPersistentAttributeType() ) {
 						case EMBEDDED: {
 							return ValueClassification.EMBEDDABLE;
 						}
 						case BASIC: {
 							return ValueClassification.BASIC;
 						}
 						default: {
 							return ValueClassification.ENTITY;
 						}
 					}
 				}
 
 				public AttributeMetadata getAttributeMetadata() {
 					return SingularAttributeMetadataImpl.this;
 				}
 			};
 		}
 
 		public ValueContext getValueContext() {
 			return valueContext;
 		}
 	}
 
 	private class PluralAttributeMetadataImpl<X,Y,E>
 			extends BaseAttributeMetadata<X,Y>
 			implements PluralAttributeMetadata<X,Y,E> {
 		private final PluralAttribute.CollectionType attributeCollectionType;
 		private final Attribute.PersistentAttributeType elementPersistentAttributeType;
 		private final Attribute.PersistentAttributeType keyPersistentAttributeType;
 		private final Class elementJavaType;
 		private final Class keyJavaType;
 		private final ValueContext elementValueContext;
 		private final ValueContext keyValueContext;
 
 		private PluralAttributeMetadataImpl(
 				Property propertyMapping,
 				AbstractManagedType<X> ownerType,
 				Member member,
 				Attribute.PersistentAttributeType persistentAttributeType,
 				Attribute.PersistentAttributeType elementPersistentAttributeType,
 				Attribute.PersistentAttributeType keyPersistentAttributeType) {
 			super( propertyMapping, ownerType, member, persistentAttributeType );
 			this.attributeCollectionType = determineCollectionType( getJavaType() );
 			this.elementPersistentAttributeType = elementPersistentAttributeType;
 			this.keyPersistentAttributeType = keyPersistentAttributeType;
 
 			ParameterizedType signatureType = getSignatureType( member );
 			if ( keyPersistentAttributeType == null ) {
 				elementJavaType = signatureType != null ?
 						getClassFromGenericArgument( signatureType.getActualTypeArguments()[0] ) :
 						Object.class; //FIXME and honor targetEntity?
 				keyJavaType = null;
 			}
 			else {
 				keyJavaType = signatureType != null ?
 						getClassFromGenericArgument( signatureType.getActualTypeArguments()[0] ) :
 						Object.class; //FIXME and honor targetEntity?
 				elementJavaType = signatureType != null ?
 						getClassFromGenericArgument( signatureType.getActualTypeArguments()[1] ) :
 						Object.class; //FIXME and honor targetEntity?
 			}
 
 			this.elementValueContext = new ValueContext() {
 				public Value getValue() {
 					return ( (Collection) getPropertyMapping().getValue() ).getElement();
 				}
 
 				public Class getBindableType() {
 					return elementJavaType;
 				}
 
 				public ValueClassification getValueClassification() {
 					switch ( PluralAttributeMetadataImpl.this.elementPersistentAttributeType ) {
 						case EMBEDDED: {
 							return ValueClassification.EMBEDDABLE;
 						}
 						case BASIC: {
 							return ValueClassification.BASIC;
 						}
 						default: {
 							return ValueClassification.ENTITY;
 						}
 					}
 				}
 
 				public AttributeMetadata getAttributeMetadata() {
 					return PluralAttributeMetadataImpl.this;
 				}
 			};
 
 			// interpret the key, if one
 			if ( keyPersistentAttributeType != null ) {
 				this.keyValueContext = new ValueContext() {
 					public Value getValue() {
 						return ( (Map) getPropertyMapping().getValue() ).getIndex();
 					}
 
 					public Class getBindableType() {
 						return keyJavaType;
 					}
 
 					public ValueClassification getValueClassification() {
 						switch ( PluralAttributeMetadataImpl.this.keyPersistentAttributeType ) {
 							case EMBEDDED: {
 								return ValueClassification.EMBEDDABLE;
 							}
 							case BASIC: {
 								return ValueClassification.BASIC;
 							}
 							default: {
 								return ValueClassification.ENTITY;
 							}
 						}
 					}
 
 					public AttributeMetadata getAttributeMetadata() {
 						return PluralAttributeMetadataImpl.this;
 					}
 				};
 			}
 			else {
 				keyValueContext = null;
 			}
 		}
 
 		private Class<?> getClassFromGenericArgument(java.lang.reflect.Type type) {
 			if ( type instanceof Class ) {
 				return (Class) type;
 			}
 			else if ( type instanceof TypeVariable ) {
 				final java.lang.reflect.Type upperBound = ( ( TypeVariable ) type ).getBounds()[0];
 				return getClassFromGenericArgument( upperBound );
 			}
 			else if ( type instanceof ParameterizedType ) {
 				final java.lang.reflect.Type rawType = ( (ParameterizedType) type ).getRawType();
 				return getClassFromGenericArgument( rawType );
 			}
 			else {
 				throw new AssertionFailure(
 						"Fail to process type argument in a generic declaration. Member : " + getMemberDescription()
 								+ " Type: " + type.getClass()
 				);
 			}
 		}
 
 		public ValueContext getElementValueContext() {
 			return elementValueContext;
 		}
 
 		public PluralAttribute.CollectionType getAttributeCollectionType() {
 			return attributeCollectionType;
 		}
 
 		public ValueContext getMapKeyValueContext() {
 			return keyValueContext;
 		}
 	}
 
-	public static ParameterizedType getSignatureType(Member member) {
-		final java.lang.reflect.Type type = Field.class.isInstance( member )
-				? ( ( Field ) member ).getGenericType()
-				: ( ( Method ) member ).getGenericReturnType();
-		//this is a raw type
-		if ( type instanceof Class ) return null;
-		return (ParameterizedType) type;
-	}
+    public static ParameterizedType getSignatureType(Member member) {
+        final java.lang.reflect.Type type;
+        if (Field.class.isInstance( member )) {
+            type =  ( ( Field ) member ).getGenericType();
+        }
+        else if ( Method.class.isInstance( member ) ) {
+            type = ( ( Method ) member ).getGenericReturnType();
+        }
+        else {
+            type = ( (MapMember) member ).getType();
+        }
+        //this is a raw type
+        if ( type instanceof Class ) return null;
+        return (ParameterizedType) type;
+    }
 
 	public static PluralAttribute.CollectionType determineCollectionType(Class javaType) {
 		if ( java.util.List.class.isAssignableFrom( javaType ) ) {
 			return PluralAttribute.CollectionType.LIST;
 		}
 		else if ( java.util.Set.class.isAssignableFrom( javaType ) ) {
 			return PluralAttribute.CollectionType.SET;
 		}
 		else if ( java.util.Map.class.isAssignableFrom( javaType ) ) {
 			return PluralAttribute.CollectionType.MAP;
 		}
 		else if ( java.util.Collection.class.isAssignableFrom( javaType ) ) {
 			return PluralAttribute.CollectionType.COLLECTION;
 		}
 		else {
 			throw new IllegalArgumentException( "Expecting collection type [" + javaType.getName() + "]" );
 		}
 	}
 
-	public static boolean isManyToMany(Member member) {
-		return Field.class.isInstance( member )
-				? ( (Field) member ).getAnnotation( ManyToMany.class ) != null
-				: ( (Method) member ).getAnnotation( ManyToMany.class ) != null;
-	}
+    public static boolean isManyToMany(Member member) {
+        if ( Field.class.isInstance( member ) ) {
+            return ( (Field) member ).getAnnotation( ManyToMany.class ) != null;
+        }
+        else if ( Method.class.isInstance( member ) ) {
+            return ( (Method) member ).getAnnotation( ManyToMany.class ) != null;
+        }
+
+        return false;
+    }
 
 	private final MemberResolver EMBEDDED_MEMBER_RESOLVER = new MemberResolver() {
 		/**
 		 * {@inheritDoc}
 		 */
 		public Member resolveMember(AttributeContext attributeContext) {
 			final EmbeddableTypeImpl embeddableType = ( EmbeddableTypeImpl<?> ) attributeContext.getOwnerType();
 			final String attributeName = attributeContext.getPropertyMapping().getName();
 			return embeddableType.getHibernateType()
 					.getComponentTuplizer()
 					.getGetter( embeddableType.getHibernateType().getPropertyIndex( attributeName ) )
 					.getMember();
 		}
 	};
 
 
 	private final MemberResolver VIRTUAL_IDENTIFIER_MEMBER_RESOLVER = new MemberResolver() {
 		/**
 		 * {@inheritDoc}
 		 */
 		public Member resolveMember(AttributeContext attributeContext) {
-			final IdentifiableType identifiableType = (IdentifiableType) attributeContext.getOwnerType();
+            final AbstractIdentifiableType identifiableType = (AbstractIdentifiableType) attributeContext.getOwnerType();
 			final EntityMetamodel entityMetamodel = getDeclarerEntityMetamodel( identifiableType );
 			if ( ! entityMetamodel.getIdentifierProperty().isVirtual() ) {
 				throw new IllegalArgumentException( "expecting IdClass mapping" );
 			}
 			org.hibernate.type.Type type = entityMetamodel.getIdentifierProperty().getType();
 			if ( ! EmbeddedComponentType.class.isInstance( type ) ) {
 				throw new IllegalArgumentException( "expecting IdClass mapping" );
 			}
 
 			final EmbeddedComponentType componentType = (EmbeddedComponentType) type;
 			final String attributeName = attributeContext.getPropertyMapping().getName();
 			return componentType.getComponentTuplizer()
 					.getGetter( componentType.getPropertyIndex( attributeName ) )
 					.getMember();
 		}
 	};
 
 	/**
 	 * A {@link Member} resolver for normal attributes.
 	 */
 	private final MemberResolver NORMAL_MEMBER_RESOLVER = new MemberResolver() {
 		/**
 		 * {@inheritDoc}
 		 */
 		public Member resolveMember(AttributeContext attributeContext) {
 			final AbstractManagedType ownerType = attributeContext.getOwnerType();
 			final Property property = attributeContext.getPropertyMapping();
 			final Type.PersistenceType persistenceType = ownerType.getPersistenceType();
 			if ( Type.PersistenceType.EMBEDDABLE == persistenceType ) {
 				return EMBEDDED_MEMBER_RESOLVER.resolveMember( attributeContext );
 			}
 			else if ( Type.PersistenceType.ENTITY == persistenceType
 					|| Type.PersistenceType.MAPPED_SUPERCLASS == persistenceType ) {
-				final IdentifiableType identifiableType = (IdentifiableType) ownerType;
+                final AbstractIdentifiableType identifiableType = (AbstractIdentifiableType) ownerType;
 				final EntityMetamodel entityMetamodel = getDeclarerEntityMetamodel( identifiableType );
 				final String propertyName = property.getName();
 				final Integer index = entityMetamodel.getPropertyIndexOrNull( propertyName );
 				if ( index == null ) {
 					// just like in #determineIdentifierJavaMember , this *should* indicate we have an IdClass mapping
 					return VIRTUAL_IDENTIFIER_MEMBER_RESOLVER.resolveMember( attributeContext );
 				}
 				else {
 					return entityMetamodel.getTuplizer()
 							.getGetter( index )
 							.getMember();
 				}
 			}
 			else {
 				throw new IllegalArgumentException( "Unexpected owner type : " + persistenceType );
 			}
 		}
 	};
 
 	private final MemberResolver IDENTIFIER_MEMBER_RESOLVER = new MemberResolver() {
 		public Member resolveMember(AttributeContext attributeContext) {
-			final IdentifiableType identifiableType = (IdentifiableType) attributeContext.getOwnerType();
+            final AbstractIdentifiableType identifiableType = (AbstractIdentifiableType) attributeContext.getOwnerType();
 			final EntityMetamodel entityMetamodel = getDeclarerEntityMetamodel( identifiableType );
 			if ( ! attributeContext.getPropertyMapping().getName()
 					.equals( entityMetamodel.getIdentifierProperty().getName() ) ) {
 				// this *should* indicate processing part of an IdClass...
 				return VIRTUAL_IDENTIFIER_MEMBER_RESOLVER.resolveMember( attributeContext );
 			}
 			return entityMetamodel.getTuplizer().getIdentifierGetter().getMember();
 		}
 	};
 
 	private final MemberResolver VERSION_MEMBER_RESOLVER = new MemberResolver() {
 		public Member resolveMember(AttributeContext attributeContext) {
-			final IdentifiableType identifiableType = (IdentifiableType) attributeContext.getOwnerType();
+            final AbstractIdentifiableType identifiableType = (AbstractIdentifiableType) attributeContext.getOwnerType();
 			final EntityMetamodel entityMetamodel = getDeclarerEntityMetamodel( identifiableType );
 			final String versionPropertyName = attributeContext.getPropertyMapping().getName();
 			if ( ! versionPropertyName.equals( entityMetamodel.getVersionProperty().getName() ) ) {
 				// this should never happen, but to be safe...
 				throw new IllegalArgumentException( "Given property did not match declared version property" );
 			}
 			return entityMetamodel.getTuplizer().getVersionGetter().getMember();
 		}
 	};
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/EmbeddableTypeImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/EmbeddableTypeImpl.java
index cf4660df23..662ae99a68 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/EmbeddableTypeImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/EmbeddableTypeImpl.java
@@ -1,56 +1,56 @@
 /*
  * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Middleware LLC.
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
 package org.hibernate.jpa.internal.metamodel;
 
 import java.io.Serializable;
 import javax.persistence.metamodel.EmbeddableType;
 
 import org.hibernate.type.ComponentType;
 
 /**
  * @author Emmanuel Bernard
  */
 public class EmbeddableTypeImpl<X>
 		extends AbstractManagedType<X>
 		implements EmbeddableType<X>, Serializable {
 
 	private final AbstractManagedType parent;
 	private final ComponentType hibernateType;
 
 	public EmbeddableTypeImpl(Class<X> javaType, AbstractManagedType parent, ComponentType hibernateType) {
-		super( javaType, null );
+		super( javaType, null, null );
 		this.parent = parent;
 		this.hibernateType = hibernateType;
 	}
 
 	public PersistenceType getPersistenceType() {
 		return PersistenceType.EMBEDDABLE;
 	}
 
 	public AbstractManagedType getParent() {
 		return parent;
 	}
 
 	public ComponentType getHibernateType() {
 		return hibernateType;
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/EntityTypeImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/EntityTypeImpl.java
index 0f900427e6..d4ff574337 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/EntityTypeImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/EntityTypeImpl.java
@@ -1,67 +1,73 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009, 2013, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.jpa.internal.metamodel;
+
 import java.io.Serializable;
 import javax.persistence.metamodel.EntityType;
 
+import org.hibernate.mapping.PersistentClass;
+
 /**
  * Defines the Hibernate implementation of the JPA {@link EntityType} contract.
  *
  * @author Steve Ebersole
  * @author Emmanuel Bernard
  */
 public class EntityTypeImpl<X> 
 		extends AbstractIdentifiableType<X>
 		implements EntityType<X>, Serializable {
 	private final String jpaEntityName;
 
-	public EntityTypeImpl(
-			Class<X> javaType,
-			AbstractIdentifiableType<? super X> superType, 
-			String jpaEntityName,
-			boolean hasIdentifierProperty,
-			boolean isVersioned) {
-		super( javaType, superType, hasIdentifierProperty, isVersioned );
-		this.jpaEntityName = jpaEntityName;
+	public EntityTypeImpl(Class javaType, AbstractIdentifiableType<? super X> superType, PersistentClass persistentClass) {
+		super(
+				javaType,
+				persistentClass.getEntityName(),
+				superType,
+				persistentClass.hasIdentifierProperty(),
+				persistentClass.isVersioned()
+		);
+		this.jpaEntityName = persistentClass.getJpaEntityName();
 	}
 
 	public String getName() {
 		return jpaEntityName;
 	}
 
 	public BindableType getBindableType() {
 		return BindableType.ENTITY_TYPE;
 	}
 
 	public Class<X> getBindableJavaType() {
 		return getJavaType();
 	}
 
 	public PersistenceType getPersistenceType() {
 		return PersistenceType.ENTITY;
 	}
 
 	@Override
 	protected boolean requiresSupertypeForNonDeclaredIdentifier() {
 		return true;
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MapMember.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MapMember.java
new file mode 100755
index 0000000000..1f66c166e3
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MapMember.java
@@ -0,0 +1,39 @@
+package org.hibernate.jpa.internal.metamodel;
+
+import java.lang.reflect.Member;
+import java.lang.reflect.Modifier;
+
+/**
+ * Acts as a virtual Member definition for dynamic (Map-based) models.
+ *
+ * @author Brad Koehn
+ */
+public class MapMember implements Member {
+	private String name;
+	private final Class<?> type;
+
+	public MapMember(String name, Class<?> type) {
+		this.name = name;
+		this.type = type;
+	}
+
+	public Class<?> getType() {
+		return type;
+	}
+
+	public int getModifiers() {
+		return Modifier.PUBLIC;
+	}
+
+	public boolean isSynthetic() {
+		return false;
+	}
+
+	public String getName() {
+		return name;
+	}
+
+	public Class<?> getDeclaringClass() {
+		return null;
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MappedSuperclassTypeImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MappedSuperclassTypeImpl.java
index 9f3fcd5bbd..a6448ceb33 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MappedSuperclassTypeImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MappedSuperclassTypeImpl.java
@@ -1,24 +1,50 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.jpa.internal.metamodel;
+
 import javax.persistence.metamodel.MappedSuperclassType;
 
+import org.hibernate.mapping.MappedSuperclass;
+
 /**
  * @author Emmanuel Bernard
+ * @author Steve Ebersole
  */
 public class MappedSuperclassTypeImpl<X> extends AbstractIdentifiableType<X> implements MappedSuperclassType<X> {
 	public MappedSuperclassTypeImpl(
 			Class<X> javaType,
-			AbstractIdentifiableType<? super X> superType,
-			boolean hasIdentifierProperty,
-			boolean versioned) {
-		super( javaType, superType, hasIdentifierProperty, versioned );
+			MappedSuperclass mappedSuperclass,
+			AbstractIdentifiableType<? super X> superType) {
+		super( javaType, null, superType, mappedSuperclass.hasIdentifierProperty(), mappedSuperclass.isVersioned() );
 	}
 
 	public PersistenceType getPersistenceType() {
 		return PersistenceType.MAPPED_SUPERCLASS;
 	}
 
 	@Override
 	protected boolean requiresSupertypeForNonDeclaredIdentifier() {
 		return false;
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetadataContext.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetadataContext.java
old mode 100644
new mode 100755
index 876da1ea98..c8caed32ab
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetadataContext.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetadataContext.java
@@ -1,483 +1,489 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.jpa.internal.metamodel;
 
+import javax.persistence.metamodel.Attribute;
+import javax.persistence.metamodel.IdentifiableType;
+import javax.persistence.metamodel.MappedSuperclassType;
+import javax.persistence.metamodel.SingularAttribute;
 import java.lang.reflect.Field;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
-import javax.persistence.metamodel.Attribute;
-import javax.persistence.metamodel.IdentifiableType;
-import javax.persistence.metamodel.MappedSuperclassType;
-import javax.persistence.metamodel.SingularAttribute;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.annotations.common.AssertionFailure;
-import org.hibernate.jpa.internal.EntityManagerMessageLogger;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.collections.CollectionHelper;
+import org.hibernate.jpa.internal.EntityManagerMessageLogger;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 
 /**
  * Defines a context for storing information during the building of the {@link MetamodelImpl}.
  * <p/>
  * This contextual information includes data needing to be processed in a second pass as well as
  * cross-references into the built metamodel classes.
  * <p/>
  * At the end of the day, clients are interested in the {@link #getEntityTypeMap} and {@link #getEmbeddableTypeMap}
  * results, which represent all the registered {@linkplain #registerEntityType entities} and
  *  {@linkplain #registerEmbeddedableType embeddables} respectively.
  *
  * @author Steve Ebersole
  * @author Emmanuel Bernard
  */
 class MetadataContext {
 
     private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
                                                                            MetadataContext.class.getName());
 
 	private final SessionFactoryImplementor sessionFactory;
     private final boolean ignoreUnsupported;
 	private final AttributeFactory attributeFactory = new AttributeFactory( this );
 
 	private Map<Class<?>,EntityTypeImpl<?>> entityTypes
 			= new HashMap<Class<?>, EntityTypeImpl<?>>();
 	private Map<String,EntityTypeImpl<?>> entityTypesByEntityName
 			= new HashMap<String, EntityTypeImpl<?>>();
 	private Map<PersistentClass,EntityTypeImpl<?>> entityTypesByPersistentClass
 			= new HashMap<PersistentClass,EntityTypeImpl<?>>();
 	private Map<Class<?>, EmbeddableTypeImpl<?>> embeddables
 			= new HashMap<Class<?>, EmbeddableTypeImpl<?>>();
 	private Map<MappedSuperclass, MappedSuperclassTypeImpl<?>> mappedSuperclassByMappedSuperclassMapping
 			= new HashMap<MappedSuperclass,MappedSuperclassTypeImpl<?>>();
 	//this list contains MappedSuperclass and EntityTypes ordered by superclass first
 	private List<Object> orderedMappings = new ArrayList<Object>();
 	/**
 	 * Stack of PersistentClass being process. Last in the list is the highest in the stack.
 	 *
 	 */
 	private List<PersistentClass> stackOfPersistentClassesBeingProcessed
 			= new ArrayList<PersistentClass>();
 	private Map<MappedSuperclassTypeImpl<?>, PersistentClass> mappedSuperClassTypeToPersistentClass
 			= new HashMap<MappedSuperclassTypeImpl<?>, PersistentClass>();
 
 	public MetadataContext(SessionFactoryImplementor sessionFactory, boolean ignoreUnsupported) {
 		this.sessionFactory = sessionFactory;
         this.ignoreUnsupported = ignoreUnsupported;
 	}
 
 	/*package*/ SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
     /*package*/ boolean isIgnoreUnsupported() {
         return ignoreUnsupported;
     }
 
     /**
 	 * Retrieves the {@linkplain Class java type} to {@link EntityTypeImpl} map.
 	 *
 	 * @return The {@linkplain Class java type} to {@link EntityTypeImpl} map.
 	 */
 	public Map<Class<?>, EntityTypeImpl<?>> getEntityTypeMap() {
 		return Collections.unmodifiableMap( entityTypes );
 	}
 
 	public Map<Class<?>, EmbeddableTypeImpl<?>> getEmbeddableTypeMap() {
 		return Collections.unmodifiableMap( embeddables );
 	}
 
 	public Map<Class<?>,MappedSuperclassType<?>> getMappedSuperclassTypeMap() {
 		// we need to actually build this map...
 		final Map<Class<?>,MappedSuperclassType<?>> mappedSuperClassTypeMap = CollectionHelper.mapOfSize(
 				mappedSuperclassByMappedSuperclassMapping.size()
 		);
 
 		for ( MappedSuperclassTypeImpl mappedSuperclassType : mappedSuperclassByMappedSuperclassMapping.values() ) {
 			mappedSuperClassTypeMap.put(
 					mappedSuperclassType.getJavaType(),
 					mappedSuperclassType
 			);
 		}
 
 		return mappedSuperClassTypeMap;
 	}
 
 	/*package*/ void registerEntityType(PersistentClass persistentClass, EntityTypeImpl<?> entityType) {
 		entityTypes.put( entityType.getBindableJavaType(), entityType );
 		entityTypesByEntityName.put( persistentClass.getEntityName(), entityType );
 		entityTypesByPersistentClass.put( persistentClass, entityType );
 		orderedMappings.add( persistentClass );
 	}
 
 	/*package*/ void registerEmbeddedableType(EmbeddableTypeImpl<?> embeddableType) {
 		embeddables.put( embeddableType.getJavaType(), embeddableType );
 	}
 
 	/*package*/ void registerMappedSuperclassType(MappedSuperclass mappedSuperclass,
 												  MappedSuperclassTypeImpl<?> mappedSuperclassType) {
 		mappedSuperclassByMappedSuperclassMapping.put( mappedSuperclass, mappedSuperclassType );
 		orderedMappings.add( mappedSuperclass );
 		mappedSuperClassTypeToPersistentClass.put( mappedSuperclassType, getEntityWorkedOn() );
 	}
 
 	/**
 	 * Given a Hibernate {@link PersistentClass}, locate the corresponding JPA {@link org.hibernate.type.EntityType}
 	 * implementation.  May retur null if the given {@link PersistentClass} has not yet been processed.
 	 *
 	 * @param persistentClass The Hibernate (config time) metamodel instance representing an entity.
 	 * @return Tne corresponding JPA {@link org.hibernate.type.EntityType}, or null if not yet processed.
 	 */
 	public EntityTypeImpl<?> locateEntityType(PersistentClass persistentClass) {
 		return entityTypesByPersistentClass.get( persistentClass );
 	}
 
 	/**
 	 * Given a Java {@link Class}, locate the corresponding JPA {@link org.hibernate.type.EntityType}.  May
 	 * return null which could means that no such mapping exists at least at this time.
 	 *
 	 * @param javaType The java class.
 	 * @return The corresponding JPA {@link org.hibernate.type.EntityType}, or null.
 	 */
 	public EntityTypeImpl<?> locateEntityType(Class<?> javaType) {
 		return entityTypes.get( javaType );
 	}
 
 	/**
 	 * Given an entity-name, locate the corresponding JPA {@link org.hibernate.type.EntityType}.  May
 	 * return null which could means that no such mapping exists at least at this time.
 	 *
 	 * @param entityName The entity-name.
 	 * @return The corresponding JPA {@link org.hibernate.type.EntityType}, or null.
 	 */
 	public EntityTypeImpl<?> locateEntityType(String entityName) {
 		return entityTypesByEntityName.get( entityName );
 	}
 
-	@SuppressWarnings({ "unchecked" })
+    public Map<String, EntityTypeImpl<?>> getEntityTypesByEntityName() {
+        return Collections.unmodifiableMap( entityTypesByEntityName );
+    }
+
+    @SuppressWarnings({ "unchecked" })
 	public void wrapUp() {
         LOG.trace("Wrapping up metadata context...");
 		//we need to process types from superclasses to subclasses
 		for (Object mapping : orderedMappings) {
 			if ( PersistentClass.class.isAssignableFrom( mapping.getClass() ) ) {
 				@SuppressWarnings( "unchecked" )
 				final PersistentClass safeMapping = (PersistentClass) mapping;
                 LOG.trace("Starting entity [" + safeMapping.getEntityName() + "]");
 				try {
 					final EntityTypeImpl<?> jpa2Mapping = entityTypesByPersistentClass.get( safeMapping );
 					applyIdMetadata( safeMapping, jpa2Mapping );
 					applyVersionAttribute( safeMapping, jpa2Mapping );
 					Iterator<Property> properties = safeMapping.getDeclaredPropertyIterator();
 					while ( properties.hasNext() ) {
 						final Property property = properties.next();
 						if ( property.getValue() == safeMapping.getIdentifierMapper() ) {
 							// property represents special handling for id-class mappings but we have already
 							// accounted for the embedded property mappings in #applyIdMetadata &&
 							// #buildIdClassAttributes
 							continue;
 						}
 						if ( safeMapping.isVersioned() && property == safeMapping.getVersion() ) {
 							// skip the version property, it was already handled previously.
 							continue;
 						}
 						final Attribute attribute = attributeFactory.buildAttribute( jpa2Mapping, property );
 						if ( attribute != null ) {
 							jpa2Mapping.getBuilder().addAttribute( attribute );
 						}
 					}
 					jpa2Mapping.lock();
 					populateStaticMetamodel( jpa2Mapping );
 				}
 				finally {
                     LOG.trace("Completed entity [" + safeMapping.getEntityName() + "]");
 				}
 			}
 			else if ( MappedSuperclass.class.isAssignableFrom( mapping.getClass() ) ) {
 				@SuppressWarnings( "unchecked" )
 				final MappedSuperclass safeMapping = (MappedSuperclass) mapping;
                 LOG.trace("Starting mapped superclass [" + safeMapping.getMappedClass().getName() + "]");
 				try {
 					final MappedSuperclassTypeImpl<?> jpa2Mapping = mappedSuperclassByMappedSuperclassMapping.get(
 							safeMapping
 					);
 					applyIdMetadata( safeMapping, jpa2Mapping );
 					applyVersionAttribute( safeMapping, jpa2Mapping );
 					Iterator<Property> properties = safeMapping.getDeclaredPropertyIterator();
 					while ( properties.hasNext() ) {
 						final Property property = properties.next();
 						if ( safeMapping.isVersioned() && property == safeMapping.getVersion() ) {
 							// skip the version property, it was already handled previously.
 							continue;
 						}
 						final Attribute attribute = attributeFactory.buildAttribute( jpa2Mapping, property );
 						if ( attribute != null ) {
 							jpa2Mapping.getBuilder().addAttribute( attribute );
 						}
 					}
 					jpa2Mapping.lock();
 					populateStaticMetamodel( jpa2Mapping );
 				}
 				finally {
                     LOG.trace("Completed mapped superclass [" + safeMapping.getMappedClass().getName() + "]");
 				}
 			}
 			else {
 				throw new AssertionFailure( "Unexpected mapping type: " + mapping.getClass() );
 			}
 		}
 
 		for ( EmbeddableTypeImpl embeddable : embeddables.values() ) {
 			populateStaticMetamodel( embeddable );
 		}
 	}
 
 
 	private <X> void applyIdMetadata(PersistentClass persistentClass, EntityTypeImpl<X> jpaEntityType) {
 		if ( persistentClass.hasIdentifierProperty() ) {
 			final Property declaredIdentifierProperty = persistentClass.getDeclaredIdentifierProperty();
 			if (declaredIdentifierProperty != null) {
 				jpaEntityType.getBuilder().applyIdAttribute(
 						attributeFactory.buildIdAttribute( jpaEntityType, declaredIdentifierProperty )
 				);
 			}
 		}
 		else if ( persistentClass.hasIdentifierMapper() ) {
 			@SuppressWarnings( "unchecked")
 			Iterator<Property> propertyIterator = persistentClass.getIdentifierMapper().getPropertyIterator();
 			Set<SingularAttribute<? super X, ?>> attributes = buildIdClassAttributes( jpaEntityType, propertyIterator );
 			jpaEntityType.getBuilder().applyIdClassAttributes( attributes );
 		}
 		else {
 			final KeyValue value = persistentClass.getIdentifier();
 			if (value instanceof Component ) {
 				final Component component = ( Component ) value;
 				if ( component.getPropertySpan() > 1 ) {
 					//FIXME we are an Hibernate embedded id (ie not type)
 				}
 				else {
 					//FIXME take care of declared vs non declared property
 					jpaEntityType.getBuilder().applyIdAttribute(
 						attributeFactory.buildIdAttribute(
 								jpaEntityType,
 								(Property) component.getPropertyIterator().next() )
 					);
 				}
 			}
 		}
 	}
 
 	private <X> void applyIdMetadata(MappedSuperclass mappingType, MappedSuperclassTypeImpl<X> jpaMappingType) {
 		if ( mappingType.hasIdentifierProperty() ) {
 			final Property declaredIdentifierProperty = mappingType.getDeclaredIdentifierProperty();
 			if (declaredIdentifierProperty != null) {
 				jpaMappingType.getBuilder().applyIdAttribute(
 						attributeFactory.buildIdAttribute( jpaMappingType, declaredIdentifierProperty )
 				);
 			}
 		}
 		//an MappedSuperclass can have no identifier if the id is set below in the hierarchy
 		else if ( mappingType.getIdentifierMapper() != null ){
 			@SuppressWarnings( "unchecked")
 			Iterator<Property> propertyIterator = mappingType.getIdentifierMapper().getPropertyIterator();
 			Set<SingularAttribute<? super X, ?>> attributes = buildIdClassAttributes( jpaMappingType, propertyIterator );
 			jpaMappingType.getBuilder().applyIdClassAttributes( attributes );
 		}
 	}
 
 	private <X> void applyVersionAttribute(PersistentClass persistentClass, EntityTypeImpl<X> jpaEntityType) {
 		final Property declaredVersion = persistentClass.getDeclaredVersion();
 		if (declaredVersion != null) {
 			jpaEntityType.getBuilder().applyVersionAttribute(
 					attributeFactory.buildVersionAttribute( jpaEntityType, declaredVersion )
 			);
 		}
 	}
 
 	private <X> void applyVersionAttribute(MappedSuperclass mappingType, MappedSuperclassTypeImpl<X> jpaMappingType) {
 		final Property declaredVersion = mappingType.getDeclaredVersion();
 		if ( declaredVersion != null ) {
 			jpaMappingType.getBuilder().applyVersionAttribute(
 					attributeFactory.buildVersionAttribute( jpaMappingType, declaredVersion )
 			);
 		}
 	}
 
 	private <X> Set<SingularAttribute<? super X, ?>> buildIdClassAttributes(
 			AbstractIdentifiableType<X> ownerType,
 			Iterator<Property> propertyIterator) {
 		LOG.trace("Building old-school composite identifier [" + ownerType.getJavaType().getName() + "]");
 		Set<SingularAttribute<? super X, ?>> attributes	= new HashSet<SingularAttribute<? super X, ?>>();
 		while ( propertyIterator.hasNext() ) {
 			attributes.add( attributeFactory.buildIdAttribute( ownerType, propertyIterator.next() ) );
 		}
 		return attributes;
 	}
 
 	private <X> void populateStaticMetamodel(AbstractManagedType<X> managedType) {
 		final Class<X> managedTypeClass = managedType.getJavaType();
 		final String metamodelClassName = managedTypeClass.getName() + "_";
 		try {
 			final Class metamodelClass = Class.forName( metamodelClassName, true, managedTypeClass.getClassLoader() );
 			// we found the class; so populate it...
 			registerAttributes( metamodelClass, managedType );
 		}
 		catch ( ClassNotFoundException ignore ) {
 			// nothing to do...
 		}
 
 		// todo : this does not account for @MappeSuperclass, mainly because this is not being tracked in our
 		// internal metamodel as populated from the annotatios properly
 		AbstractManagedType<? super X> superType = managedType.getSupertype();
 		if ( superType != null ) {
 			populateStaticMetamodel( superType );
 		}
 	}
 
 	private final Set<Class> processedMetamodelClasses = new HashSet<Class>();
 
 	private <X> void registerAttributes(Class metamodelClass, AbstractManagedType<X> managedType) {
 		if ( ! processedMetamodelClasses.add( metamodelClass ) ) {
 			return;
 		}
 
 		// push the attributes on to the metamodel class...
 		for ( Attribute<X, ?> attribute : managedType.getDeclaredAttributes() ) {
 			registerAttribute( metamodelClass, attribute );
 		}
 
 		if ( IdentifiableType.class.isInstance( managedType ) ) {
 			final AbstractIdentifiableType<X> entityType = ( AbstractIdentifiableType<X> ) managedType;
 
 			// handle version
 			if ( entityType.hasDeclaredVersionAttribute() ) {
 				registerAttribute( metamodelClass, entityType.getDeclaredVersion() );
 			}
 
 			// handle id-class mappings specially
 			if ( ! entityType.hasSingleIdAttribute() ) {
 				final Set<SingularAttribute<? super X, ?>> attributes = entityType.getIdClassAttributes();
 				if ( attributes != null ) {
 					for ( SingularAttribute<? super X, ?> attribute : attributes ) {
 						registerAttribute( metamodelClass, attribute );
 					}
 				}
 			}
 		}
 	}
 
 	private <X> void registerAttribute(Class metamodelClass, Attribute<X, ?> attribute) {
 		final String name = attribute.getName();
 		try {
 			// there is a shortcoming in the existing Hibernate code in terms of the way MappedSuperclass
 			// support was bolted on which comes to bear right here when the attribute is an embeddable type
 			// defined on a MappedSuperclass.  We do not have the correct information to determine the
 			// appropriate attribute declarer in such cases and so the incoming metamodelClass most likely
 			// does not represent the declarer in such cases.
 			//
 			// As a result, in the case of embeddable classes we simply use getField rather than get
 			// getDeclaredField
 			final Field field = attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED
 					? metamodelClass.getField( name )
 					: metamodelClass.getDeclaredField( name );
 			try {
 				if ( ! field.isAccessible() ) {
 					// should be public anyway, but to be sure...
 					field.setAccessible( true );
 				}
 				field.set( null, attribute );
 			}
 			catch ( IllegalAccessException e ) {
 				// todo : exception type?
 				throw new AssertionFailure(
 						"Unable to inject static metamodel attribute : " + metamodelClass.getName() + '#' + name,
 						e
 				);
 			}
 			catch ( IllegalArgumentException e ) {
 				// most likely a mismatch in the type we are injecting and the defined field; this represents a
 				// mismatch in how the annotation processor interpretted the attribute and how our metamodel
 				// and/or annotation binder did.
 
 //              This is particularly the case as arrays are nto handled propery by the StaticMetamodel generator
 
 //				throw new AssertionFailure(
 //						"Illegal argument on static metamodel field injection : " + metamodelClass.getName() + '#' + name
 //								+ "; expected type :  " + attribute.getClass().getName()
 //								+ "; encountered type : " + field.getType().getName()
 //				);
                 LOG.illegalArgumentOnStaticMetamodelFieldInjection(metamodelClass.getName(),
                                                                    name,
                                                                    attribute.getClass().getName(),
                                                                    field.getType().getName());
 			}
 		}
 		catch ( NoSuchFieldException e ) {
             LOG.unableToLocateStaticMetamodelField(metamodelClass.getName(), name);
 //			throw new AssertionFailure(
 //					"Unable to locate static metamodel field : " + metamodelClass.getName() + '#' + name
 //			);
 		}
 	}
 
 	public MappedSuperclassTypeImpl<?> locateMappedSuperclassType(MappedSuperclass mappedSuperclass) {
 		return mappedSuperclassByMappedSuperclassMapping.get(mappedSuperclass);
 	}
 
 	public void pushEntityWorkedOn(PersistentClass persistentClass) {
 		stackOfPersistentClassesBeingProcessed.add(persistentClass);
 	}
 
 	public void popEntityWorkedOn(PersistentClass persistentClass) {
 		final PersistentClass stackTop = stackOfPersistentClassesBeingProcessed.remove(
 				stackOfPersistentClassesBeingProcessed.size() - 1
 		);
 		if (stackTop != persistentClass) {
 			throw new AssertionFailure( "Inconsistent popping: "
 				+ persistentClass.getEntityName() + " instead of " + stackTop.getEntityName() );
 		}
 	}
 
 	private PersistentClass getEntityWorkedOn() {
 		return stackOfPersistentClassesBeingProcessed.get(
 					stackOfPersistentClassesBeingProcessed.size() - 1
 			);
 	}
 
 	public PersistentClass getPersistentClassHostingProperties(MappedSuperclassTypeImpl<?> mappedSuperclassType) {
 		final PersistentClass persistentClass = mappedSuperClassTypeToPersistentClass.get( mappedSuperclassType );
 		if (persistentClass == null) {
 			throw new AssertionFailure( "Could not find PersistentClass for MappedSuperclassType: "
 					+ mappedSuperclassType.getJavaType() );
 		}
 		return persistentClass;
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetamodelImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetamodelImpl.java
old mode 100644
new mode 100755
index d8502b05d2..5306889b97
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetamodelImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetamodelImpl.java
@@ -1,236 +1,232 @@
 /*
  * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Middleware LLC.
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
 package org.hibernate.jpa.internal.metamodel;
 
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.util.collections.CollectionHelper;
+import org.hibernate.mapping.MappedSuperclass;
+import org.hibernate.mapping.PersistentClass;
+
 import java.io.Serializable;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
-import javax.persistence.metamodel.EmbeddableType;
-import javax.persistence.metamodel.EntityType;
-import javax.persistence.metamodel.ManagedType;
-import javax.persistence.metamodel.MappedSuperclassType;
-import javax.persistence.metamodel.Metamodel;
-
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.mapping.MappedSuperclass;
-import org.hibernate.mapping.PersistentClass;
+import javax.persistence.metamodel.*;
 
 /**
  * Hibernate implementation of the JPA {@link Metamodel} contract.
  *
  * @author Steve Ebersole
  * @author Emmanuel Bernard
  */
 public class MetamodelImpl implements Metamodel, Serializable {
 	private final Map<Class<?>,EntityTypeImpl<?>> entities;
 	private final Map<Class<?>, EmbeddableTypeImpl<?>> embeddables;
 	private final Map<Class<?>, MappedSuperclassType<?>> mappedSuperclassTypeMap;
+    private final Map<String, EntityTypeImpl<?>> entityTypesByEntityName;
 
     /**
    	 * Build the metamodel using the information from the collection of Hibernate
    	 * {@link PersistentClass} models as well as the Hibernate {@link org.hibernate.SessionFactory}.
    	 *
    	 * @param persistentClasses Iterator over the Hibernate (config-time) metamodel
    	 * @param sessionFactory The Hibernate session factory.
    	 * @return The built metamodel
 	 * 
 	 * @deprecated use {@link #buildMetamodel(java.util.Iterator,org.hibernate.engine.spi.SessionFactoryImplementor,boolean)} instead
    	 */
 	@Deprecated
    	public static MetamodelImpl buildMetamodel(
    			Iterator<PersistentClass> persistentClasses,
    			SessionFactoryImplementor sessionFactory) {
         return buildMetamodel(persistentClasses, sessionFactory, false);
    	}
 
 	/**
 	 * Build the metamodel using the information from the collection of Hibernate
 	 * {@link PersistentClass} models as well as the Hibernate {@link org.hibernate.SessionFactory}.
 	 *
 	 * @param persistentClasses Iterator over the Hibernate (config-time) metamodel
 	 * @param sessionFactory The Hibernate session factory.
      * @param ignoreUnsupported ignore unsupported/unknown annotations (like @Any)
 	 * @return The built metamodel
 	 */
 	public static MetamodelImpl buildMetamodel(
 			Iterator<PersistentClass> persistentClasses,
 			SessionFactoryImplementor sessionFactory,
             boolean ignoreUnsupported) {
 		MetadataContext context = new MetadataContext( sessionFactory, ignoreUnsupported );
 		while ( persistentClasses.hasNext() ) {
 			PersistentClass pc = persistentClasses.next();
-			if ( pc.getMappedClass() != null ) {
-				locateOrBuildEntityType( pc, context );
-			}
+			locateOrBuildEntityType( pc, context );
 		}
 		context.wrapUp();
-		return new MetamodelImpl( context.getEntityTypeMap(), context.getEmbeddableTypeMap(), context.getMappedSuperclassTypeMap() );
+		return new MetamodelImpl( context.getEntityTypeMap(), context.getEmbeddableTypeMap(), context.getMappedSuperclassTypeMap(), context.getEntityTypesByEntityName() );
 	}
 
 	private static EntityTypeImpl<?> locateOrBuildEntityType(PersistentClass persistentClass, MetadataContext context) {
 		EntityTypeImpl<?> entityType = context.locateEntityType( persistentClass );
 		if ( entityType == null ) {
 			entityType = buildEntityType( persistentClass, context );
 		}
 		return entityType;
 	}
 
 	//TODO remove / reduce @SW scope
 	@SuppressWarnings( "unchecked" )
 	private static EntityTypeImpl<?> buildEntityType(PersistentClass persistentClass, MetadataContext context) {
 		final Class javaType = persistentClass.getMappedClass();
 		context.pushEntityWorkedOn(persistentClass);
 		final MappedSuperclass superMappedSuperclass = persistentClass.getSuperMappedSuperclass();
 		AbstractIdentifiableType<?> superType = superMappedSuperclass == null
 				? null
 				: locateOrBuildMappedsuperclassType( superMappedSuperclass, context );
 		//no mappedSuperclass, check for a super entity
 		if (superType == null) {
 			final PersistentClass superPersistentClass = persistentClass.getSuperclass();
 			superType = superPersistentClass == null
 					? null
 					: locateOrBuildEntityType( superPersistentClass, context );
 		}
 		EntityTypeImpl entityType = new EntityTypeImpl(
 				javaType,
 				superType,
-				persistentClass.getJpaEntityName(),
-				persistentClass.hasIdentifierProperty(),
-				persistentClass.isVersioned()
+				persistentClass
 		);
-		context.registerEntityType( persistentClass, entityType );
+
+        context.registerEntityType( persistentClass, entityType );
 		context.popEntityWorkedOn(persistentClass);
 		return entityType;
 	}
 	
 	private static MappedSuperclassTypeImpl<?> locateOrBuildMappedsuperclassType(
 			MappedSuperclass mappedSuperclass, MetadataContext context) {
 		MappedSuperclassTypeImpl<?> mappedSuperclassType = context.locateMappedSuperclassType( mappedSuperclass );
 		if ( mappedSuperclassType == null ) {
 			mappedSuperclassType = buildMappedSuperclassType(mappedSuperclass, context);
 		}
 		return mappedSuperclassType;
 	}
 
 	//TODO remove / reduce @SW scope
 	@SuppressWarnings( "unchecked" )
-	private static MappedSuperclassTypeImpl<?> buildMappedSuperclassType(MappedSuperclass mappedSuperclass,
-																		 MetadataContext context) {
+	private static MappedSuperclassTypeImpl<?> buildMappedSuperclassType(
+			MappedSuperclass mappedSuperclass,
+			MetadataContext context) {
 		final MappedSuperclass superMappedSuperclass = mappedSuperclass.getSuperMappedSuperclass();
 		AbstractIdentifiableType<?> superType = superMappedSuperclass == null
 				? null
 				: locateOrBuildMappedsuperclassType( superMappedSuperclass, context );
 		//no mappedSuperclass, check for a super entity
 		if (superType == null) {
 			final PersistentClass superPersistentClass = mappedSuperclass.getSuperPersistentClass();
 			superType = superPersistentClass == null
 					? null
 					: locateOrBuildEntityType( superPersistentClass, context );
 		}
 		final Class javaType = mappedSuperclass.getMappedClass();
 		MappedSuperclassTypeImpl mappedSuperclassType = new MappedSuperclassTypeImpl(
 				javaType,
-				superType,
-				mappedSuperclass.hasIdentifierProperty(),
-				mappedSuperclass.isVersioned()
+				mappedSuperclass,
+				superType
 		);
 		context.registerMappedSuperclassType( mappedSuperclass, mappedSuperclassType );
 		return mappedSuperclassType;
 	}
 
 	/**
 	 * Instantiate the metamodel.
 	 *
 	 * @param entities The entity mappings.
 	 * @param embeddables The embeddable (component) mappings.
 	 * @param mappedSuperclassTypeMap The {@link javax.persistence.MappedSuperclass} mappings
 	 */
 	private MetamodelImpl(
 			Map<Class<?>, EntityTypeImpl<?>> entities,
 			Map<Class<?>, EmbeddableTypeImpl<?>> embeddables,
-			Map<Class<?>, MappedSuperclassType<?>> mappedSuperclassTypeMap) {
+            Map<Class<?>, MappedSuperclassType<?>> mappedSuperclassTypeMap,
+            Map<String, EntityTypeImpl<?>> entityTypesByEntityName) {
 		this.entities = entities;
 		this.embeddables = embeddables;
 		this.mappedSuperclassTypeMap = mappedSuperclassTypeMap;
+        this.entityTypesByEntityName = entityTypesByEntityName;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X> EntityType<X> entity(Class<X> cls) {
 		final EntityType<?> entityType = entities.get( cls );
 		if ( entityType == null ) {
 			throw new IllegalArgumentException( "Not an entity: " + cls );
 		}
 		return (EntityType<X>) entityType;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X> ManagedType<X> managedType(Class<X> cls) {
 		ManagedType<?> type = entities.get( cls );
 		if ( type == null ) {
 			type = mappedSuperclassTypeMap.get( cls );
 		}
 		if ( type == null ) {
 			type = embeddables.get( cls );
 		}
 		if ( type == null ) {
 			throw new IllegalArgumentException( "Not an managed type: " + cls );
 		}
 		return (ManagedType<X>) type;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X> EmbeddableType<X> embeddable(Class<X> cls) {
 		final EmbeddableType<?> embeddableType = embeddables.get( cls );
 		if ( embeddableType == null ) {
 			throw new IllegalArgumentException( "Not an embeddable: " + cls );
 		}
 		return (EmbeddableType<X>) embeddableType;
 	}
 
 	@Override
 	public Set<ManagedType<?>> getManagedTypes() {
 		final int setSize = CollectionHelper.determineProperSizing(
 				entities.size() + mappedSuperclassTypeMap.size() + embeddables.size()
 		);
 		final Set<ManagedType<?>> managedTypes = new HashSet<ManagedType<?>>( setSize );
 		managedTypes.addAll( entities.values() );
 		managedTypes.addAll( mappedSuperclassTypeMap.values() );
 		managedTypes.addAll( embeddables.values() );
 		return managedTypes;
 	}
 
 	@Override
 	public Set<EntityType<?>> getEntities() {
-		return new HashSet<EntityType<?>>( entities.values() );
+		return new HashSet<EntityType<?>>( entityTypesByEntityName.values() );
 	}
 
 	@Override
 	public Set<EmbeddableType<?>> getEmbeddables() {
 		return new HashSet<EmbeddableType<?>>( embeddables.values() );
 	}
 }
