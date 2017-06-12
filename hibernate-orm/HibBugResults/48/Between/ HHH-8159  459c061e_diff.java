diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java
index 7423691c76..a40e491f97 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java
@@ -1,264 +1,264 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.action.spi.Executable;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Queryable;
 
 /**
  * An {@link org.hibernate.engine.spi.ActionQueue} {@link org.hibernate.action.spi.Executable} for ensuring
  * shared cache cleanup in relation to performed bulk HQL queries.
  * <p/>
  * NOTE: currently this executes for <tt>INSERT</tt> queries as well as
  * <tt>UPDATE</tt> and <tt>DELETE</tt> queries.  For <tt>INSERT</tt> it is
  * really not needed as we'd have no invalid entity/collection data to
  * cleanup (we'd still nee to invalidate the appropriate update-timestamps
  * regions) as a result of this query.
  *
  * @author Steve Ebersole
  */
 public class BulkOperationCleanupAction implements Executable, Serializable {
 	private final Serializable[] affectedTableSpaces;
 
 	private final Set<EntityCleanup> entityCleanups = new HashSet<EntityCleanup>();
 	private final Set<CollectionCleanup> collectionCleanups = new HashSet<CollectionCleanup>();
 	private final Set<NaturalIdCleanup> naturalIdCleanups = new HashSet<NaturalIdCleanup>();
 
 	/**
 	 * Constructs an action to cleanup "affected cache regions" based on the
 	 * affected entity persisters.  The affected regions are defined as the
 	 * region (if any) of the entity persisters themselves, plus the
 	 * collection regions for any collection in which those entity
 	 * persisters participate as elements/keys/etc.
 	 *
 	 * @param session The session to which this request is tied.
 	 * @param affectedQueryables The affected entity persisters.
 	 */
 	public BulkOperationCleanupAction(SessionImplementor session, Queryable... affectedQueryables) {
 		final SessionFactoryImplementor factory = session.getFactory();
 		final LinkedHashSet<String> spacesList = new LinkedHashSet<String>();
 		for ( Queryable persister : affectedQueryables ) {
 			spacesList.addAll( Arrays.asList( (String[]) persister.getQuerySpaces() ) );
 
 			if ( persister.hasCache() ) {
 				entityCleanups.add( new EntityCleanup( persister.getCacheAccessStrategy() ) );
 			}
 			if ( persister.hasNaturalIdentifier() && persister.hasNaturalIdCache() ) {
 				naturalIdCleanups.add( new NaturalIdCleanup( persister.getNaturalIdCacheAccessStrategy() ) );
 			}
 
 			final Set<String> roles = factory.getCollectionRolesByEntityParticipant( persister.getEntityName() );
 			if ( roles != null ) {
 				for ( String role : roles ) {
 					final CollectionPersister collectionPersister = factory.getCollectionPersister( role );
 					if ( collectionPersister.hasCache() ) {
 						collectionCleanups.add( new CollectionCleanup( collectionPersister.getCacheAccessStrategy() ) );
 					}
 				}
 			}
 		}
 
 		this.affectedTableSpaces = spacesList.toArray( new String[ spacesList.size() ] );
 	}
 
 	/**
 	 * Constructs an action to cleanup "affected cache regions" based on a
 	 * set of affected table spaces.  This differs from {@link #BulkOperationCleanupAction(SessionImplementor, Queryable[])}
 	 * in that here we have the affected <strong>table names</strong>.  From those
 	 * we deduce the entity persisters which are affected based on the defined
 	 * {@link EntityPersister#getQuerySpaces() table spaces}; and from there, we
 	 * determine the affected collection regions based on any collections
 	 * in which those entity persisters participate as elements/keys/etc.
 	 *
 	 * @param session The session to which this request is tied.
 	 * @param tableSpaces The table spaces.
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public BulkOperationCleanupAction(SessionImplementor session, Set tableSpaces) {
 		final LinkedHashSet<String> spacesList = new LinkedHashSet<String>();
 		spacesList.addAll( tableSpaces );
 
 		final SessionFactoryImplementor factory = session.getFactory();
 		for ( String entityName : factory.getAllClassMetadata().keySet() ) {
 			final EntityPersister persister = factory.getEntityPersister( entityName );
 			final String[] entitySpaces = (String[]) persister.getQuerySpaces();
 			if ( affectedEntity( tableSpaces, entitySpaces ) ) {
 				spacesList.addAll( Arrays.asList( entitySpaces ) );
 
 				if ( persister.hasCache() ) {
 					entityCleanups.add( new EntityCleanup( persister.getCacheAccessStrategy() ) );
 				}
 				if ( persister.hasNaturalIdentifier() && persister.hasNaturalIdCache() ) {
 					naturalIdCleanups.add( new NaturalIdCleanup( persister.getNaturalIdCacheAccessStrategy() ) );
 				}
 
 				final Set<String> roles = session.getFactory().getCollectionRolesByEntityParticipant( persister.getEntityName() );
 				if ( roles != null ) {
 					for ( String role : roles ) {
 						final CollectionPersister collectionPersister = factory.getCollectionPersister( role );
 						if ( collectionPersister.hasCache() ) {
 							collectionCleanups.add(
 									new CollectionCleanup( collectionPersister.getCacheAccessStrategy() )
 							);
 						}
 					}
 				}
 			}
 		}
 
 		this.affectedTableSpaces = spacesList.toArray( new String[ spacesList.size() ] );
 	}
 
 
 	/**
 	 * Check to determine whether the table spaces reported by an entity
 	 * persister match against the defined affected table spaces.
 	 *
 	 * @param affectedTableSpaces The table spaces reported to be affected by
 	 * the query.
 	 * @param checkTableSpaces The table spaces (from the entity persister)
 	 * to check against the affected table spaces.
 	 *
 	 * @return True if there are affected table spaces and any of the incoming
 	 * check table spaces occur in that set.
 	 */
 	private boolean affectedEntity(Set affectedTableSpaces, Serializable[] checkTableSpaces) {
 		if ( affectedTableSpaces == null || affectedTableSpaces.isEmpty() ) {
 			return true;
 		}
 
 		for ( Serializable checkTableSpace : checkTableSpaces ) {
 			if ( affectedTableSpaces.contains( checkTableSpace ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	@Override
 	public Serializable[] getPropertySpaces() {
 		return affectedTableSpaces;
 	}
 
 	@Override
 	public BeforeTransactionCompletionProcess getBeforeTransactionCompletionProcess() {
 		return null;
 	}
 
 	@Override
 	public AfterTransactionCompletionProcess getAfterTransactionCompletionProcess() {
 		return new AfterTransactionCompletionProcess() {
 			@Override
 			public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
 				for ( EntityCleanup cleanup : entityCleanups ) {
 					cleanup.release();
 				}
 				entityCleanups.clear();
 
 				for ( NaturalIdCleanup cleanup : naturalIdCleanups ) {
 					cleanup.release();
 
 				}
 				entityCleanups.clear();
 
 				for ( CollectionCleanup cleanup : collectionCleanups ) {
 					cleanup.release();
 				}
 				collectionCleanups.clear();
 			}
 		};
 	}
 
 	@Override
 	public void beforeExecutions() throws HibernateException {
 		// nothing to do
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		// nothing to do		
 	}
 
-	private static class EntityCleanup {
+	private static class EntityCleanup implements Serializable {
 		private final EntityRegionAccessStrategy cacheAccess;
 		private final SoftLock cacheLock;
 
 		private EntityCleanup(EntityRegionAccessStrategy cacheAccess) {
 			this.cacheAccess = cacheAccess;
 			this.cacheLock = cacheAccess.lockRegion();
 			cacheAccess.removeAll();
 		}
 
 		private void release() {
 			cacheAccess.unlockRegion( cacheLock );
 		}
 	}
 
-	private static class CollectionCleanup {
+	private static class CollectionCleanup implements Serializable {
 		private final CollectionRegionAccessStrategy cacheAccess;
 		private final SoftLock cacheLock;
 
 		private CollectionCleanup(CollectionRegionAccessStrategy cacheAccess) {
 			this.cacheAccess = cacheAccess;
 			this.cacheLock = cacheAccess.lockRegion();
 			cacheAccess.removeAll();
 		}
 
 		private void release() {
 			cacheAccess.unlockRegion( cacheLock );
 		}
 	}
 
-	private class NaturalIdCleanup {
+	private class NaturalIdCleanup implements Serializable {
 		private final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy;
 		private final SoftLock cacheLock;
 
 		public NaturalIdCleanup(NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy) {
 			this.naturalIdCacheAccessStrategy = naturalIdCacheAccessStrategy;
 			this.cacheLock = naturalIdCacheAccessStrategy.lockRegion();
 			naturalIdCacheAccessStrategy.removeAll();
 		}
 
 		private void release() {
 			naturalIdCacheAccessStrategy.unlockRegion( cacheLock );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 0062fad513..f3ac7b91eb 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -1972,1250 +1972,1250 @@ public final class HbmBinder {
 				value = new OneToOne( mappings, component.getTable(), component.getOwner() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindOneToOne( subnode, (OneToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, component.getTable() );
 				bindAny( subnode, (Any) value, isNullable, mappings );
 			}
 			else if ( "property".equals( name ) || "key-property".equals( name ) ) {
 				value = new SimpleValue( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindSimpleValue( subnode, (SimpleValue) value, isNullable, relativePath, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "nested-composite-element".equals( name ) ) {
 				value = new Component( mappings, component ); // a nested composite element
 				bindComponent(
 						subnode,
 						(Component) value,
 						component.getComponentClassName(),
 						propertyName,
 						subpath,
 						isNullable,
 						isEmbedded,
 						mappings,
 						inheritedMetas,
 						isIdentifierMapper
 					);
 			}
 			else if ( "parent".equals( name ) ) {
 				component.setParentProperty( propertyName );
 			}
 
 			if ( value != null ) {
 				Property property = createProperty( value, propertyName, component
 					.getComponentClassName(), subnode, mappings, inheritedMetas );
 				if (isIdentifierMapper) {
 					property.setInsertable(false);
 					property.setUpdateable(false);
 				}
 				component.addProperty( property );
 			}
 		}
 
 		if ( "true".equals( node.attributeValue( "unique" ) ) ) {
 			iter = component.getColumnIterator();
 			ArrayList cols = new ArrayList();
 			while ( iter.hasNext() ) {
 				cols.add( iter.next() );
 			}
 			component.getOwner().getTable().createUniqueKey( cols );
 		}
 
 		iter = node.elementIterator( "tuplizer" );
 		while ( iter.hasNext() ) {
 			final Element tuplizerElem = ( Element ) iter.next();
 			EntityMode mode = EntityMode.parse( tuplizerElem.attributeValue( "entity-mode" ) );
 			component.addTuplizer( mode, tuplizerElem.attributeValue( "class" ) );
 		}
 	}
 
 	public static String getTypeFromXML(Element node) throws MappingException {
 		// TODO: handle TypeDefs
 		Attribute typeNode = node.attribute( "type" );
 		if ( typeNode == null ) typeNode = node.attribute( "id-type" ); // for an any
 		if ( typeNode == null ) return null; // we will have to use reflection
 		return typeNode.getValue();
 	}
 
 	private static void initOuterJoinFetchSetting(Element node, Fetchable model) {
 		Attribute fetchNode = node.attribute( "fetch" );
 		final FetchMode fetchStyle;
 		boolean lazy = true;
 		if ( fetchNode == null ) {
 			Attribute jfNode = node.attribute( "outer-join" );
 			if ( jfNode == null ) {
 				if ( "many-to-many".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// default to join and non-lazy for the "second join"
 					// of the many-to-many
 					lazy = false;
 					fetchStyle = FetchMode.JOIN;
 				}
 				else if ( "one-to-one".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// one-to-one constrained=false cannot be proxied,
 					// so default to join and non-lazy
 					lazy = ( (OneToOne) model ).isConstrained();
 					fetchStyle = lazy ? FetchMode.DEFAULT : FetchMode.JOIN;
 				}
 				else {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 			}
 			else {
 				// use old (HB 2.1) defaults if outer-join is specified
 				String eoj = jfNode.getValue();
 				if ( "auto".equals( eoj ) ) {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 				else {
 					boolean join = "true".equals( eoj );
 					fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 				}
 			}
 		}
 		else {
 			boolean join = "join".equals( fetchNode.getValue() );
 			//lazy = !join;
 			fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 		}
 		model.setFetchMode( fetchStyle );
 		model.setLazy(lazy);
 	}
 
 	private static void makeIdentifier(Element node, SimpleValue model, Mappings mappings) {
 
 		// GENERATOR
 		Element subnode = node.element( "generator" );
 		if ( subnode != null ) {
 			final String generatorClass = subnode.attributeValue( "class" );
 			model.setIdentifierGeneratorStrategy( generatorClass );
 
 			Properties params = new Properties();
 			// YUCK!  but cannot think of a clean way to do this given the string-config based scheme
 			params.put( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, mappings.getObjectNameNormalizer() );
 
 			if ( mappings.getSchemaName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.SCHEMA,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getSchemaName() )
 				);
 			}
 			if ( mappings.getCatalogName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.CATALOG,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getCatalogName() )
 				);
 			}
 
 			Iterator iter = subnode.elementIterator( "param" );
 			while ( iter.hasNext() ) {
 				Element childNode = (Element) iter.next();
 				params.setProperty( childNode.attributeValue( "name" ), childNode.getTextTrim() );
 			}
 
 			model.setIdentifierGeneratorProperties( params );
 		}
 
 		model.getTable().setIdentifierValue( model );
 
 		// ID UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			if ( "assigned".equals( model.getIdentifierGeneratorStrategy() ) ) {
 				model.setNullValue( "undefined" );
 			}
 			else {
 				model.setNullValue( null );
 			}
 		}
 	}
 
 	private static final void makeVersion(Element node, SimpleValue model) {
 
 		// VERSION UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			model.setNullValue( "undefined" );
 		}
 
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 		createClassProperties(node, persistentClass, mappings, inheritedMetas, null, true, true, false);
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas, UniqueKey uniqueKey,
 			boolean mutable, boolean nullable, boolean naturalId) throws MappingException {
 
 		String entityName = persistentClass.getEntityName();
 		Table table = persistentClass.getTable();
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = subnode.attributeValue( "name" );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						StringHelper.qualify( entityName, propertyName ),
 						persistentClass,
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, table );
 				bindManyToOne( subnode, (ManyToOne) value, propertyName, nullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, table );
 				bindAny( subnode, (Any) value, nullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, table, persistentClass );
 				bindOneToOne( subnode, (OneToOne) value, propertyName, true, mappings );
 			}
 			else if ( "property".equals( name ) ) {
 				value = new SimpleValue( mappings, table );
 				bindSimpleValue( subnode, (SimpleValue) value, nullable, propertyName, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "properties".equals( name ) ) {
 				String subpath = StringHelper.qualify( entityName, propertyName );
 				value = new Component( mappings, persistentClass );
 
 				bindComponent(
 						subnode,
 						(Component) value,
 						persistentClass.getClassName(),
 						propertyName,
 						subpath,
 						true,
 						"properties".equals( name ),
 						mappings,
 						inheritedMetas,
 						false
 					);
 			}
 			else if ( "join".equals( name ) ) {
 				Join join = new Join();
 				join.setPersistentClass( persistentClass );
 				bindJoin( subnode, join, mappings, inheritedMetas );
 				persistentClass.addJoin( join );
 			}
 			else if ( "subclass".equals( name ) ) {
 				handleSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "joined-subclass".equals( name ) ) {
 				handleJoinedSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "union-subclass".equals( name ) ) {
 				handleUnionSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "filter".equals( name ) ) {
 				parseFilter( subnode, persistentClass, mappings );
 			}
 			else if ( "natural-id".equals( name ) ) {
 				UniqueKey uk = new UniqueKey();
 				uk.setName(StringHelper.randomFixedLengthHex("UK_"));
 				uk.setTable(table);
 				//by default, natural-ids are "immutable" (constant)
 				boolean mutableId = "true".equals( subnode.attributeValue("mutable") );
 				createClassProperties(
 						subnode,
 						persistentClass,
 						mappings,
 						inheritedMetas,
 						uk,
 						mutableId,
 						false,
 						true
 					);
 				table.addUniqueKey(uk);
 			}
 			else if ( "query".equals(name) ) {
 				bindNamedQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "sql-query".equals(name) ) {
 				bindNamedSQLQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "resultset".equals(name) ) {
 				bindResultSetMappingDefinition( subnode, persistentClass.getEntityName(), mappings );
 			}
 
 			if ( value != null ) {
 				final Property property = createProperty(
 						value,
 						propertyName,
 						persistentClass.getClassName(),
 						subnode,
 						mappings,
 						inheritedMetas
 				);
 				if ( !mutable ) {
 					property.setUpdateable(false);
 				}
 				if ( naturalId ) {
 					property.setNaturalIdentifier( true );
 				}
 				persistentClass.addProperty( property );
 				if ( uniqueKey!=null ) {
 					uniqueKey.addColumns( property.getColumnIterator() );
 				}
 			}
 
 		}
 	}
 
 	private static Property createProperty(
 			final Value value,
 	        final String propertyName,
 			final String className,
 	        final Element subnode,
 	        final Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		if ( StringHelper.isEmpty( propertyName ) ) {
 			throw new MappingException( subnode.getName() + " mapping must defined a name attribute [" + className + "]" );
 		}
 
 		value.setTypeUsingReflection( className, propertyName );
 
 		// this is done here 'cos we might only know the type here (ugly!)
 		// TODO: improve this a lot:
 		if ( value instanceof ToOne ) {
 			ToOne toOne = (ToOne) value;
 			String propertyRef = toOne.getReferencedPropertyName();
 			if ( propertyRef != null ) {
 				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
 			}
 		}
 		else if ( value instanceof Collection ) {
 			Collection coll = (Collection) value;
 			String propertyRef = coll.getReferencedPropertyName();
 			// not necessarily a *unique* property reference
 			if ( propertyRef != null ) {
 				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
 			}
 		}
 
 		value.createForeignKey();
 		Property prop = new Property();
 		prop.setValue( value );
 		bindProperty( subnode, prop, mappings, inheritedMetas );
 		return prop;
 	}
 
 	private static void handleUnionSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		UnionSubclass subclass = new UnionSubclass( model );
 		bindUnionSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleJoinedSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		JoinedSubclass subclass = new JoinedSubclass( model );
 		bindJoinedSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleSubclass(PersistentClass model, Mappings mappings, Element subnode,
 			java.util.Map inheritedMetas) throws MappingException {
 		Subclass subclass = new SingleTableSubclass( model );
 		bindSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	/**
 	 * Called for Lists, arrays, primitive arrays
 	 */
 	public static void bindListSecondPass(Element node, List list, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, list, classes, mappings, inheritedMetas );
 
 		Element subnode = node.element( "list-index" );
 		if ( subnode == null ) subnode = node.element( "index" );
 		SimpleValue iv = new SimpleValue( mappings, list.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				iv,
 				list.isOneToMany(),
 				IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 				mappings
 		);
 		iv.setTypeName( "integer" );
 		list.setIndex( iv );
 		String baseIndex = subnode.attributeValue( "base" );
 		if ( baseIndex != null ) list.setBaseIndex( Integer.parseInt( baseIndex ) );
 		list.setIndexNodeName( subnode.attributeValue("node") );
 
 		if ( list.isOneToMany() && !list.getKey().isNullable() && !list.isInverse() ) {
 			String entityName = ( (OneToMany) list.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + list.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( list.getRole() );
 			ib.setEntityName( list.getOwner().getEntityName() );
 			ib.setValue( list.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	public static void bindIdentifierCollectionSecondPass(Element node,
 			IdentifierCollection collection, java.util.Map persistentClasses, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, collection, persistentClasses, mappings, inheritedMetas );
 
 		Element subnode = node.element( "collection-id" );
 		SimpleValue id = new SimpleValue( mappings, collection.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				id,
 				false,
 				IdentifierCollection.DEFAULT_IDENTIFIER_COLUMN_NAME,
 				mappings
 			);
 		collection.setIdentifier( id );
 		makeIdentifier( subnode, id, mappings );
 
 	}
 
 	/**
 	 * Called for Maps
 	 */
 	public static void bindMapSecondPass(Element node, Map map, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, map, classes, mappings, inheritedMetas );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "index".equals( name ) || "map-key".equals( name ) ) {
 				SimpleValue value = new SimpleValue( mappings, map.getCollectionTable() );
 				bindSimpleValue(
 						subnode,
 						value,
 						map.isOneToMany(),
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						mappings
 					);
 				if ( !value.isTypeSpecified() ) {
 					throw new MappingException( "map index element must specify a type: "
 						+ map.getRole() );
 				}
 				map.setIndex( value );
 				map.setIndexNodeName( subnode.attributeValue("node") );
 			}
 			else if ( "index-many-to-many".equals( name ) || "map-key-many-to-many".equals( name ) ) {
 				ManyToOne mto = new ManyToOne( mappings, map.getCollectionTable() );
 				bindManyToOne(
 						subnode,
 						mto,
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						map.isOneToMany(),
 						mappings
 					);
 				map.setIndex( mto );
 
 			}
 			else if ( "composite-index".equals( name ) || "composite-map-key".equals( name ) ) {
 				Component component = new Component( mappings, map );
 				bindComposite(
 						subnode,
 						component,
 						map.getRole() + ".index",
 						map.isOneToMany(),
 						mappings,
 						inheritedMetas
 					);
 				map.setIndex( component );
 			}
 			else if ( "index-many-to-any".equals( name ) ) {
 				Any any = new Any( mappings, map.getCollectionTable() );
 				bindAny( subnode, any, map.isOneToMany(), mappings );
 				map.setIndex( any );
 			}
 		}
 
 		// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
 		boolean indexIsFormula = false;
 		Iterator colIter = map.getIndex().getColumnIterator();
 		while ( colIter.hasNext() ) {
 			if ( ( (Selectable) colIter.next() ).isFormula() ) indexIsFormula = true;
 		}
 
 		if ( map.isOneToMany() && !map.getKey().isNullable() && !map.isInverse() && !indexIsFormula ) {
 			String entityName = ( (OneToMany) map.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + map.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( map.getRole() );
 			ib.setEntityName( map.getOwner().getEntityName() );
 			ib.setValue( map.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollectionSecondPass(Element node, Collection collection,
 			java.util.Map persistentClasses, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 
 		if ( collection.isOneToMany() ) {
 			OneToMany oneToMany = (OneToMany) collection.getElement();
 			String assocClass = oneToMany.getReferencedEntityName();
 			PersistentClass persistentClass = (PersistentClass) persistentClasses.get( assocClass );
 			if ( persistentClass == null ) {
 				throw new MappingException( "Association references unmapped class: " + assocClass );
 			}
 			oneToMany.setAssociatedClass( persistentClass );
 			collection.setCollectionTable( persistentClass.getTable() );
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 			}
 		}
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) {
 			collection.getCollectionTable().addCheckConstraint( chNode.getValue() );
 		}
 
 		// contained elements:
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "key".equals( name ) ) {
 				KeyValue keyVal;
 				String propRef = collection.getReferencedPropertyName();
 				if ( propRef == null ) {
 					keyVal = collection.getOwner().getIdentifier();
 				}
 				else {
 					keyVal = (KeyValue) collection.getOwner().getRecursiveProperty( propRef ).getValue();
 				}
 				SimpleValue key = new DependantValue( mappings, collection.getCollectionTable(), keyVal );
 				key.setCascadeDeleteEnabled( "cascade"
 					.equals( subnode.attributeValue( "on-delete" ) ) );
 				bindSimpleValue(
 						subnode,
 						key,
 						collection.isOneToMany(),
 						Collection.DEFAULT_KEY_COLUMN_NAME,
 						mappings
 					);
 				collection.setKey( key );
 
 				Attribute notNull = subnode.attribute( "not-null" );
 				( (DependantValue) key ).setNullable( notNull == null
 					|| notNull.getValue().equals( "false" ) );
 				Attribute updateable = subnode.attribute( "update" );
 				( (DependantValue) key ).setUpdateable( updateable == null
 					|| updateable.getValue().equals( "true" ) );
 
 			}
 			else if ( "element".equals( name ) ) {
 				SimpleValue elt = new SimpleValue( mappings, collection.getCollectionTable() );
 				collection.setElement( elt );
 				bindSimpleValue(
 						subnode,
 						elt,
 						true,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						mappings
 					);
 			}
 			else if ( "many-to-many".equals( name ) ) {
 				ManyToOne element = new ManyToOne( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindManyToOne(
 						subnode,
 						element,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						false,
 						mappings
 					);
 				bindManyToManySubelements( collection, subnode, mappings );
 			}
 			else if ( "composite-element".equals( name ) ) {
 				Component element = new Component( mappings, collection );
 				collection.setElement( element );
 				bindComposite(
 						subnode,
 						element,
 						collection.getRole() + ".element",
 						true,
 						mappings,
 						inheritedMetas
 					);
 			}
 			else if ( "many-to-any".equals( name ) ) {
 				Any element = new Any( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindAny( subnode, element, true, mappings );
 			}
 			else if ( "cache".equals( name ) ) {
 				collection.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
 				collection.setCacheRegionName( subnode.attributeValue( "region" ) );
 			}
 
 			String nodeName = subnode.attributeValue( "node" );
 			if ( nodeName != null ) collection.setElementNodeName( nodeName );
 
 		}
 
 		if ( collection.isOneToMany()
 			&& !collection.isInverse()
 			&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = ( (OneToMany) collection.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + collection.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 	private static void bindManyToManySubelements(
 	        Collection collection,
 	        Element manyToManyNode,
 	        Mappings model) throws MappingException {
 		// Bind the where
 		Attribute where = manyToManyNode.attribute( "where" );
 		String whereCondition = where == null ? null : where.getValue();
 		collection.setManyToManyWhere( whereCondition );
 
 		// Bind the order-by
 		Attribute order = manyToManyNode.attribute( "order-by" );
 		String orderFragment = order == null ? null : order.getValue();
 		collection.setManyToManyOrdering( orderFragment );
 
 		// Bind the filters
 		Iterator filters = manyToManyNode.elementIterator( "filter" );
 		if ( ( filters.hasNext() || whereCondition != null ) &&
 		        collection.getFetchMode() == FetchMode.JOIN &&
 		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 			throw new MappingException(
 			        "many-to-many defining filter or where without join fetching " +
 			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 				);
 		}
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		while ( filters.hasNext() ) {
 			final Element filterElement = ( Element ) filters.next();
 			final String name = filterElement.attributeValue( "name" );
 			String condition = filterElement.getTextTrim();
 			if ( StringHelper.isEmpty(condition) ) condition = filterElement.attributeValue( "condition" );
 			if ( StringHelper.isEmpty(condition) ) {
 				condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 			}
 			if ( condition==null) {
 				throw new MappingException("no filter condition found for filter: " + name);
 			}
 			Iterator aliasesIterator = filterElement.elementIterator("aliases");
 			java.util.Map<String, String> aliasTables = new HashMap<String, String>();
 			while (aliasesIterator.hasNext()){
 				Element alias = (Element) aliasesIterator.next();
 				aliasTables.put(alias.attributeValue("alias"), alias.attributeValue("table"));
 			}
 			if ( debugEnabled ) {
 				LOG.debugf( "Applying many-to-many filter [%s] as [%s] to role [%s]", name, condition, collection.getRole() );
 			}
 			String autoAliasInjectionText = filterElement.attributeValue("autoAliasInjection");
 			boolean autoAliasInjection = StringHelper.isEmpty(autoAliasInjectionText) ? true : Boolean.parseBoolean(autoAliasInjectionText);
 			collection.addManyToManyFilter(name, condition, autoAliasInjection, aliasTables, null);
 		}
 	}
 
 	private static void bindNamedQuery(Element queryElem, String path, Mappings mappings) {
 		String queryName = queryElem.attributeValue( "name" );
 		if (path!=null) queryName = path + '.' + queryName;
 		String query = queryElem.getText();
 		LOG.debugf( "Named query: %s -> %s", queryName, query );
 
 		boolean cacheable = "true".equals( queryElem.attributeValue( "cacheable" ) );
 		String region = queryElem.attributeValue( "cache-region" );
 		Attribute tAtt = queryElem.attribute( "timeout" );
 		Integer timeout = tAtt == null ? null : Integer.valueOf( tAtt.getValue() );
 		Attribute fsAtt = queryElem.attribute( "fetch-size" );
 		Integer fetchSize = fsAtt == null ? null : Integer.valueOf( fsAtt.getValue() );
 		Attribute roAttr = queryElem.attribute( "read-only" );
 		boolean readOnly = roAttr != null && "true".equals( roAttr.getValue() );
 		Attribute cacheModeAtt = queryElem.attribute( "cache-mode" );
 		String cacheMode = cacheModeAtt == null ? null : cacheModeAtt.getValue();
 		Attribute cmAtt = queryElem.attribute( "comment" );
 		String comment = cmAtt == null ? null : cmAtt.getValue();
 
 		NamedQueryDefinition namedQuery = new NamedQueryDefinitionBuilder().setName( queryName )
 				.setQuery( query )
 				.setCacheable( cacheable )
 				.setCacheRegion( region )
 				.setTimeout( timeout )
 				.setFetchSize( fetchSize )
 				.setFlushMode( FlushMode.interpretExternalSetting( queryElem.attributeValue( "flush-mode" ) ) )
 				.setCacheMode( CacheMode.interpretExternalSetting( cacheMode ) )
 				.setReadOnly( readOnly )
 				.setComment( comment )
 				.setParameterTypes( getParameterTypes( queryElem ) )
 				.createNamedQueryDefinition();
 
 		mappings.addQuery( namedQuery.getName(), namedQuery );
 	}
 
 	public static java.util.Map getParameterTypes(Element queryElem) {
 		java.util.Map result = new java.util.LinkedHashMap();
 		Iterator iter = queryElem.elementIterator("query-param");
 		while ( iter.hasNext() ) {
 			Element element = (Element) iter.next();
 			result.put( element.attributeValue("name"), element.attributeValue("type") );
 		}
 		return result;
 	}
 
 	private static void bindResultSetMappingDefinition(Element resultSetElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new ResultSetMappingSecondPass( resultSetElem, path, mappings ) );
 	}
 
 	private static void bindNamedSQLQuery(Element queryElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new NamedSQLQuerySecondPass( queryElem, path, mappings ) );
 	}
 
 	private static String getPropertyName(Element node) {
 		return node.attributeValue( "name" );
 	}
 
 	private static PersistentClass getSuperclass(Mappings mappings, Element subnode)
 			throws MappingException {
 		String extendsName = subnode.attributeValue( "extends" );
 		PersistentClass superModel = mappings.getClass( extendsName );
 		if ( superModel == null ) {
 			String qualifiedExtendsName = getClassName( extendsName, mappings );
 			superModel = mappings.getClass( qualifiedExtendsName );
 		}
 
 		if ( superModel == null ) {
 			throw new MappingException( "Cannot extend unmapped class " + extendsName );
 		}
 		return superModel;
 	}
 
 	static class CollectionSecondPass extends org.hibernate.cfg.CollectionSecondPass {
 		Element node;
 
 		CollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super(mappings, collection, inheritedMetas);
 			this.node = node;
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindCollectionSecondPass(
 					node,
 					collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 	}
 
 	static class IdentifierCollectionSecondPass extends CollectionSecondPass {
 		IdentifierCollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindIdentifierCollectionSecondPass(
 					node,
 					(IdentifierCollection) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	static class MapSecondPass extends CollectionSecondPass {
 		MapSecondPass(Element node, Mappings mappings, Map collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindMapSecondPass(
 					node,
 					(Map) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 
 	static class ManyToOneSecondPass implements SecondPass {
 		private final ManyToOne manyToOne;
 
 		ManyToOneSecondPass(ManyToOne manyToOne) {
 			this.manyToOne = manyToOne;
 		}
 
 		public void doSecondPass(java.util.Map persistentClasses) throws MappingException {
 			manyToOne.createPropertyRefConstraints(persistentClasses);
 		}
 
 	}
 
 	static class ListSecondPass extends CollectionSecondPass {
 		ListSecondPass(Element node, Mappings mappings, List collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindListSecondPass(
 					node,
 					(List) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	// This inner class implements a case statement....perhaps im being a bit over-clever here
 	abstract static class CollectionType {
 		private String xmlTag;
 
 		public abstract Collection create(Element node, String path, PersistentClass owner,
 				Mappings mappings, java.util.Map inheritedMetas) throws MappingException;
 
 		CollectionType(String xmlTag) {
 			this.xmlTag = xmlTag;
 		}
 
 		public String toString() {
 			return xmlTag;
 		}
 
 		private static final CollectionType MAP = new CollectionType( "map" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Map map = new Map( mappings, owner );
 				bindCollection( node, map, owner.getEntityName(), path, mappings, inheritedMetas );
 				return map;
 			}
 		};
 		private static final CollectionType SET = new CollectionType( "set" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Set set = new Set( mappings, owner );
 				bindCollection( node, set, owner.getEntityName(), path, mappings, inheritedMetas );
 				return set;
 			}
 		};
 		private static final CollectionType LIST = new CollectionType( "list" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				List list = new List( mappings, owner );
 				bindCollection( node, list, owner.getEntityName(), path, mappings, inheritedMetas );
 				return list;
 			}
 		};
 		private static final CollectionType BAG = new CollectionType( "bag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Bag bag = new Bag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType IDBAG = new CollectionType( "idbag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				IdentifierBag bag = new IdentifierBag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType ARRAY = new CollectionType( "array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Array array = new Array( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final CollectionType PRIMITIVE_ARRAY = new CollectionType( "primitive-array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				PrimitiveArray array = new PrimitiveArray( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final HashMap INSTANCES = new HashMap();
 
 		static {
 			INSTANCES.put( MAP.toString(), MAP );
 			INSTANCES.put( BAG.toString(), BAG );
 			INSTANCES.put( IDBAG.toString(), IDBAG );
 			INSTANCES.put( SET.toString(), SET );
 			INSTANCES.put( LIST.toString(), LIST );
 			INSTANCES.put( ARRAY.toString(), ARRAY );
 			INSTANCES.put( PRIMITIVE_ARRAY.toString(), PRIMITIVE_ARRAY );
 		}
 
 		public static CollectionType collectionTypeFromString(String xmlTagName) {
 			return (CollectionType) INSTANCES.get( xmlTagName );
 		}
 	}
 
 	private static OptimisticLockStyle getOptimisticLockStyle(Attribute olAtt) throws MappingException {
 		if ( olAtt == null ) {
 			return OptimisticLockStyle.VERSION;
 		}
 
 		final String olMode = olAtt.getValue();
 		if ( olMode == null || "version".equals( olMode ) ) {
 			return OptimisticLockStyle.VERSION;
 		}
 		else if ( "dirty".equals( olMode ) ) {
 			return OptimisticLockStyle.DIRTY;
 		}
 		else if ( "all".equals( olMode ) ) {
 			return OptimisticLockStyle.ALL;
 		}
 		else if ( "none".equals( olMode ) ) {
 			return OptimisticLockStyle.NONE;
 		}
 		else {
 			throw new MappingException( "Unsupported optimistic-lock style: " + olMode );
 		}
 	}
 
 	private static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta) {
 		return getMetas( node, inheritedMeta, false );
 	}
 
 	public static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta,
 			boolean onlyInheritable) {
 		java.util.Map map = new HashMap();
 		map.putAll( inheritedMeta );
 
 		Iterator iter = node.elementIterator( "meta" );
 		while ( iter.hasNext() ) {
 			Element metaNode = (Element) iter.next();
 			boolean inheritable = Boolean
 				.valueOf( metaNode.attributeValue( "inherit" ) )
 				.booleanValue();
-			if ( onlyInheritable & !inheritable ) {
+			if ( onlyInheritable && !inheritable ) {
 				continue;
 			}
 			String name = metaNode.attributeValue( "attribute" );
 
 			MetaAttribute meta = (MetaAttribute) map.get( name );
 			MetaAttribute inheritedAttribute = (MetaAttribute) inheritedMeta.get( name );
 			if ( meta == null  ) {
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			} else if (meta == inheritedAttribute) { // overriding inherited meta attribute. HBX-621 & HBX-793
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			}
 			meta.addValue( metaNode.getText() );
 		}
 		return map;
 	}
 
 	public static String getEntityName(Element elem, Mappings model) {
 		String entityName = elem.attributeValue( "entity-name" );
 		return entityName == null ? getClassName( elem.attribute( "class" ), model ) : entityName;
 	}
 
 	private static String getClassName(Attribute att, Mappings model) {
 		if ( att == null ) return null;
 		return getClassName( att.getValue(), model );
 	}
 
 	public static String getClassName(String unqualifiedName, Mappings model) {
 		return getClassName( unqualifiedName, model.getDefaultPackage() );
 	}
 
 	public static String getClassName(String unqualifiedName, String defaultPackage) {
 		if ( unqualifiedName == null ) return null;
 		if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
 			return defaultPackage + '.' + unqualifiedName;
 		}
 		return unqualifiedName;
 	}
 
 	private static void parseFilterDef(Element element, Mappings mappings) {
 		String name = element.attributeValue( "name" );
 		LOG.debugf( "Parsing filter-def [%s]", name );
 		String defaultCondition = element.getTextTrim();
 		if ( StringHelper.isEmpty( defaultCondition ) ) {
 			defaultCondition = element.attributeValue( "condition" );
 		}
 		HashMap paramMappings = new HashMap();
 		Iterator params = element.elementIterator( "filter-param" );
 		while ( params.hasNext() ) {
 			final Element param = (Element) params.next();
 			final String paramName = param.attributeValue( "name" );
 			final String paramType = param.attributeValue( "type" );
 			LOG.debugf( "Adding filter parameter : %s -> %s", paramName, paramType );
 			final Type heuristicType = mappings.getTypeResolver().heuristicType( paramType );
 			LOG.debugf( "Parameter heuristic type : %s", heuristicType );
 			paramMappings.put( paramName, heuristicType );
 		}
 		LOG.debugf( "Parsed filter-def [%s]", name );
 		FilterDefinition def = new FilterDefinition( name, defaultCondition, paramMappings );
 		mappings.addFilterDefinition( def );
 	}
 
 	private static void parseFilter(Element filterElement, Filterable filterable, Mappings model) {
 		final String name = filterElement.attributeValue( "name" );
 		String condition = filterElement.getTextTrim();
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = filterElement.attributeValue( "condition" );
 		}
 		//TODO: bad implementation, cos it depends upon ordering of mapping doc
 		//      fixing this requires that Collection/PersistentClass gain access
 		//      to the Mappings reference from Configuration (or the filterDefinitions
 		//      map directly) sometime during Configuration.build
 		//      (after all the types/filter-defs are known and before building
 		//      persisters).
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 		}
 		if ( condition==null) {
 			throw new MappingException("no filter condition found for filter: " + name);
 		}
 		Iterator aliasesIterator = filterElement.elementIterator("aliases");
 		java.util.Map<String, String> aliasTables = new HashMap<String, String>();
 		while (aliasesIterator.hasNext()){
 			Element alias = (Element) aliasesIterator.next();
 			aliasTables.put(alias.attributeValue("alias"), alias.attributeValue("table"));
 		}
 		LOG.debugf( "Applying filter [%s] as [%s]", name, condition );
 		String autoAliasInjectionText = filterElement.attributeValue("autoAliasInjection");
 		boolean autoAliasInjection = StringHelper.isEmpty(autoAliasInjectionText) ? true : Boolean.parseBoolean(autoAliasInjectionText);
 		filterable.addFilter(name, condition, autoAliasInjection, aliasTables, null);
 	}
 
 	private static void parseFetchProfile(Element element, Mappings mappings, String containingEntityName) {
 		String profileName = element.attributeValue( "name" );
 		FetchProfile profile = mappings.findOrCreateFetchProfile( profileName, MetadataSource.HBM );
 		Iterator itr = element.elementIterator( "fetch" );
 		while ( itr.hasNext() ) {
 			final Element fetchElement = ( Element ) itr.next();
 			final String association = fetchElement.attributeValue( "association" );
 			final String style = fetchElement.attributeValue( "style" );
 			String entityName = fetchElement.attributeValue( "entity" );
 			if ( entityName == null ) {
 				entityName = containingEntityName;
 			}
 			if ( entityName == null ) {
 				throw new MappingException( "could not determine entity for fetch-profile fetch [" + profileName + "]:[" + association + "]" );
 			}
 			profile.addFetch( entityName, association, style );
 		}
 	}
 
 	private static String getSubselect(Element element) {
 		String subselect = element.attributeValue( "subselect" );
 		if ( subselect != null ) {
 			return subselect;
 		}
 		else {
 			Element subselectElement = element.element( "subselect" );
 			return subselectElement == null ? null : subselectElement.getText();
 		}
 	}
 
 	/**
 	 * For the given document, locate all extends attributes which refer to
 	 * entities (entity-name or class-name) not defined within said document.
 	 *
 	 * @param metadataXml The document to check
 	 * @param mappings The already processed mappings.
 	 * @return The list of unresolved extends names.
 	 */
 	public static java.util.List<String> getExtendsNeeded(XmlDocument metadataXml, Mappings mappings) {
 		java.util.List<String> extendz = new ArrayList<String>();
 		Iterator[] subclasses = new Iterator[3];
 		final Element hmNode = metadataXml.getDocumentTree().getRootElement();
 
 		Attribute packNode = hmNode.attribute( "package" );
 		final String packageName = packNode == null ? null : packNode.getValue();
 		if ( packageName != null ) {
 			mappings.setDefaultPackage( packageName );
 		}
 
 		// first, iterate over all elements capable of defining an extends attribute
 		// collecting all found extends references if they cannot be resolved
 		// against the already processed mappings.
 		subclasses[0] = hmNode.elementIterator( "subclass" );
 		subclasses[1] = hmNode.elementIterator( "joined-subclass" );
 		subclasses[2] = hmNode.elementIterator( "union-subclass" );
 
 		Iterator iterator = new JoinedIterator( subclasses );
 		while ( iterator.hasNext() ) {
 			final Element element = (Element) iterator.next();
 			final String extendsName = element.attributeValue( "extends" );
 			// mappings might contain either the "raw" extends name (in the case of
 			// an entity-name mapping) or a FQN (in the case of a POJO mapping).
 			if ( mappings.getClass( extendsName ) == null && mappings.getClass( getClassName( extendsName, mappings ) ) == null ) {
 				extendz.add( extendsName );
 			}
 		}
 
 		if ( !extendz.isEmpty() ) {
 			// we found some extends attributes referencing entities which were
 			// not already processed.  here we need to locate all entity-names
 			// and class-names contained in this document itself, making sure
 			// that these get removed from the extendz list such that only
 			// extends names which require us to delay processing (i.e.
 			// external to this document and not yet processed) are contained
 			// in the returned result
 			final java.util.Set<String> set = new HashSet<String>( extendz );
 			EntityElementHandler handler = new EntityElementHandler() {
 				public void handleEntity(String entityName, String className, Mappings mappings) {
 					if ( entityName != null ) {
 						set.remove( entityName );
 					}
 					else {
 						String fqn = getClassName( className, packageName );
 						set.remove( fqn );
 						if ( packageName != null ) {
 							set.remove( StringHelper.unqualify( fqn ) );
 						}
 					}
 				}
 			};
 			recognizeEntities( mappings, hmNode, handler );
 			extendz.clear();
 			extendz.addAll( set );
 		}
 
 		return extendz;
 	}
 
 	/**
 	 * Given an entity-containing-element (startNode) recursively locate all
 	 * entity names defined within that element.
 	 *
 	 * @param mappings The already processed mappings
 	 * @param startNode The containing element
 	 * @param handler The thing that knows what to do whenever we recognize an
 	 * entity-name
 	 */
 	private static void recognizeEntities(
 			Mappings mappings,
 	        final Element startNode,
 			EntityElementHandler handler) {
 		Iterator[] classes = new Iterator[4];
 		classes[0] = startNode.elementIterator( "class" );
 		classes[1] = startNode.elementIterator( "subclass" );
 		classes[2] = startNode.elementIterator( "joined-subclass" );
 		classes[3] = startNode.elementIterator( "union-subclass" );
 
 		Iterator classIterator = new JoinedIterator( classes );
 		while ( classIterator.hasNext() ) {
 			Element element = (Element) classIterator.next();
 			handler.handleEntity(
 					element.attributeValue( "entity-name" ),
 		            element.attributeValue( "name" ),
 			        mappings
 			);
 			recognizeEntities( mappings, element, handler );
 		}
 	}
 
 	private static interface EntityElementHandler {
 		public void handleEntity(String entityName, String className, Mappings mappings);
 	}
 	
 	private static class ResolveUserTypeMappingSecondPass implements SecondPass{
 
 		private SimpleValue simpleValue;
 		private String typeName;
 		private Mappings mappings;
 		private Properties parameters;
 
 		public ResolveUserTypeMappingSecondPass(SimpleValue simpleValue,
 				String typeName, Mappings mappings, Properties parameters) {
 			this.simpleValue=simpleValue;
 			this.typeName=typeName;
 			this.parameters=parameters;
 			this.mappings=mappings;
 		}
 
 		@Override
 		public void doSecondPass(java.util.Map persistentClasses)
 				throws MappingException {
 			resolveAndBindTypeDef(simpleValue, mappings, typeName, parameters);		
 		}
 		
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
index 03cf238d6b..cf3cdd2e4c 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
@@ -114,1414 +114,1420 @@ import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TypeDef;
 import org.jboss.logging.Logger;
 
 /**
  * Base class for binding different types of collections to Hibernate configuration objects.
  *
  * @author inger
  * @author Emmanuel Bernard
  */
 @SuppressWarnings({"unchecked", "serial"})
 public abstract class CollectionBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionBinder.class.getName());
 
 	protected Collection collection;
 	protected String propertyName;
 	PropertyHolder propertyHolder;
 	int batchSize;
 	private String mappedBy;
 	private XClass collectionType;
 	private XClass targetEntity;
 	private Mappings mappings;
 	private Ejb3JoinColumn[] inverseJoinColumns;
 	private String cascadeStrategy;
 	String cacheConcurrencyStrategy;
 	String cacheRegionName;
 	private boolean oneToMany;
 	protected IndexColumn indexColumn;
 	protected boolean cascadeDeleteEnabled;
 	protected String mapKeyPropertyName;
 	private boolean insertable = true;
 	private boolean updatable = true;
 	private Ejb3JoinColumn[] fkJoinColumns;
 	private boolean isExplicitAssociationTable;
 	private Ejb3Column[] elementColumns;
 	private boolean isEmbedded;
 	private XProperty property;
 	private boolean ignoreNotFound;
 	private TableBinder tableBinder;
 	private Ejb3Column[] mapKeyColumns;
 	private Ejb3JoinColumn[] mapKeyManyToManyColumns;
 	protected HashMap<String, IdGenerator> localGenerators;
 	protected Map<XClass, InheritanceState> inheritanceStatePerClass;
 	private XClass declaringClass;
 	private boolean declaringClassSet;
 	private AccessType accessType;
 	private boolean hibernateExtensionMapping;
 
 	private boolean isSortedCollection;
 	private javax.persistence.OrderBy jpaOrderBy;
 	private OrderBy sqlOrderBy;
 	private Sort deprecatedSort;
 	private SortNatural naturalSort;
 	private SortComparator comparatorSort;
 
 	private String explicitType;
 	private Properties explicitTypeParameters = new Properties();
 
 	protected Mappings getMappings() {
 		return mappings;
 	}
 
 	public boolean isMap() {
 		return false;
 	}
 
 	public void setIsHibernateExtensionMapping(boolean hibernateExtensionMapping) {
 		this.hibernateExtensionMapping = hibernateExtensionMapping;
 	}
 
 	protected boolean isHibernateExtensionMapping() {
 		return hibernateExtensionMapping;
 	}
 
 	public void setUpdatable(boolean updatable) {
 		this.updatable = updatable;
 	}
 
 	public void setInheritanceStatePerClass(Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		this.inheritanceStatePerClass = inheritanceStatePerClass;
 	}
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public void setCascadeStrategy(String cascadeStrategy) {
 		this.cascadeStrategy = cascadeStrategy;
 	}
 
 	public void setAccessType(AccessType accessType) {
 		this.accessType = accessType;
 	}
 
 	public void setInverseJoinColumns(Ejb3JoinColumn[] inverseJoinColumns) {
 		this.inverseJoinColumns = inverseJoinColumns;
 	}
 
 	public void setJoinColumns(Ejb3JoinColumn[] joinColumns) {
 		this.joinColumns = joinColumns;
 	}
 
 	private Ejb3JoinColumn[] joinColumns;
 
 	public void setPropertyHolder(PropertyHolder propertyHolder) {
 		this.propertyHolder = propertyHolder;
 	}
 
 	public void setBatchSize(BatchSize batchSize) {
 		this.batchSize = batchSize == null ? -1 : batchSize.size();
 	}
 
 	public void setJpaOrderBy(javax.persistence.OrderBy jpaOrderBy) {
 		this.jpaOrderBy = jpaOrderBy;
 	}
 
 	public void setSqlOrderBy(OrderBy sqlOrderBy) {
 		this.sqlOrderBy = sqlOrderBy;
 	}
 
 	public void setSort(Sort deprecatedSort) {
 		this.deprecatedSort = deprecatedSort;
 	}
 
 	public void setNaturalSort(SortNatural naturalSort) {
 		this.naturalSort = naturalSort;
 	}
 
 	public void setComparatorSort(SortComparator comparatorSort) {
 		this.comparatorSort = comparatorSort;
 	}
 
 	/**
 	 * collection binder factory
 	 */
 	public static CollectionBinder getCollectionBinder(
 			String entityName,
 			XProperty property,
 			boolean isIndexed,
 			boolean isHibernateExtensionMapping,
 			Mappings mappings) {
 		final CollectionBinder result;
 		if ( property.isArray() ) {
 			if ( property.getElementClass().isPrimitive() ) {
 				result = new PrimitiveArrayBinder();
 			}
 			else {
 				result = new ArrayBinder();
 			}
 		}
 		else if ( property.isCollection() ) {
 			//TODO consider using an XClass
 			Class returnedClass = property.getCollectionClass();
 			if ( java.util.Set.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Set do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new SetBinder( false );
 			}
 			else if ( java.util.SortedSet.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Set do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new SetBinder( true );
 			}
 			else if ( java.util.Map.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Map do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new MapBinder( false );
 			}
 			else if ( java.util.SortedMap.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Map do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new MapBinder( true );
 			}
 			else if ( java.util.Collection.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					result = new IdBagBinder();
 				}
 				else {
 					result = new BagBinder();
 				}
 			}
 			else if ( java.util.List.class.equals( returnedClass ) ) {
 				if ( isIndexed ) {
 					if ( property.isAnnotationPresent( CollectionId.class ) ) {
 						throw new AnnotationException(
 								"List do not support @CollectionId and @OrderColumn (or @IndexColumn) at the same time: "
 								+ StringHelper.qualify( entityName, property.getName() ) );
 					}
 					result = new ListBinder();
 				}
 				else if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					result = new IdBagBinder();
 				}
 				else {
 					result = new BagBinder();
 				}
 			}
 			else {
 				throw new AnnotationException(
 						returnedClass.getName() + " collection not yet supported: "
 								+ StringHelper.qualify( entityName, property.getName() )
 				);
 			}
 		}
 		else {
 			throw new AnnotationException(
 					"Illegal attempt to map a non collection as a @OneToMany, @ManyToMany or @CollectionOfElements: "
 							+ StringHelper.qualify( entityName, property.getName() )
 			);
 		}
 		result.setIsHibernateExtensionMapping( isHibernateExtensionMapping );
 
 		final CollectionType typeAnnotation = property.getAnnotation( CollectionType.class );
 		if ( typeAnnotation != null ) {
 			final String typeName = typeAnnotation.type();
 			// see if it names a type-def
 			final TypeDef typeDef = mappings.getTypeDef( typeName );
 			if ( typeDef != null ) {
 				result.explicitType = typeDef.getTypeClass();
 				result.explicitTypeParameters.putAll( typeDef.getParameters() );
 			}
 			else {
 				result.explicitType = typeName;
 				for ( Parameter param : typeAnnotation.parameters() ) {
 					result.explicitTypeParameters.setProperty( param.name(), param.value() );
 				}
 			}
 		}
 
 		return result;
 	}
 
 	protected CollectionBinder(boolean isSortedCollection) {
 		this.isSortedCollection = isSortedCollection;
 	}
 
 	public void setMappedBy(String mappedBy) {
 		this.mappedBy = mappedBy;
 	}
 
 	public void setTableBinder(TableBinder tableBinder) {
 		this.tableBinder = tableBinder;
 	}
 
 	public void setCollectionType(XClass collectionType) {
 		// NOTE: really really badly named.  This is actually NOT the collection-type, but rather the collection-element-type!
 		this.collectionType = collectionType;
 	}
 
 	public void setTargetEntity(XClass targetEntity) {
 		this.targetEntity = targetEntity;
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	protected abstract Collection createCollection(PersistentClass persistentClass);
 
 	public Collection getCollection() {
 		return collection;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public void setDeclaringClass(XClass declaringClass) {
 		this.declaringClass = declaringClass;
 		this.declaringClassSet = true;
 	}
 
 	public void bind() {
 		this.collection = createCollection( propertyHolder.getPersistentClass() );
 		String role = StringHelper.qualify( propertyHolder.getPath(), propertyName );
 		LOG.debugf( "Collection role: %s", role );
 		collection.setRole( role );
 		collection.setNodeName( propertyName );
 
 		if ( property.isAnnotationPresent( MapKeyColumn.class )
 			&& mapKeyPropertyName != null ) {
 			throw new AnnotationException(
 					"Cannot mix @javax.persistence.MapKey and @MapKeyColumn or @org.hibernate.annotations.MapKey "
 							+ "on the same collection: " + StringHelper.qualify(
 							propertyHolder.getPath(), propertyName
 					)
 			);
 		}
 
 		// set explicit type information
 		if ( explicitType != null ) {
 			final TypeDef typeDef = mappings.getTypeDef( explicitType );
 			if ( typeDef == null ) {
 				collection.setTypeName( explicitType );
 				collection.setTypeParameters( explicitTypeParameters );
 			}
 			else {
 				collection.setTypeName( typeDef.getTypeClass() );
 				collection.setTypeParameters( typeDef.getParameters() );
 			}
 		}
 
 		//set laziness
 		defineFetchingStrategy();
 		collection.setBatchSize( batchSize );
 
 		collection.setMutable( !property.isAnnotationPresent( Immutable.class ) );
 
 		//work on association
 		boolean isMappedBy = !BinderHelper.isEmptyAnnotationValue( mappedBy );
 
 		final OptimisticLock lockAnn = property.getAnnotation( OptimisticLock.class );
 		final boolean includeInOptimisticLockChecks = ( lockAnn != null )
 				? ! lockAnn.excluded()
 				: ! isMappedBy;
 		collection.setOptimisticLocked( includeInOptimisticLockChecks );
 
 		Persister persisterAnn = property.getAnnotation( Persister.class );
 		if ( persisterAnn != null ) {
 			collection.setCollectionPersisterClass( persisterAnn.impl() );
 		}
 
 		applySortingAndOrdering( collection );
 
 		//set cache
 		if ( StringHelper.isNotEmpty( cacheConcurrencyStrategy ) ) {
 			collection.setCacheConcurrencyStrategy( cacheConcurrencyStrategy );
 			collection.setCacheRegionName( cacheRegionName );
 		}
 
 		//SQL overriding
 		SQLInsert sqlInsert = property.getAnnotation( SQLInsert.class );
 		SQLUpdate sqlUpdate = property.getAnnotation( SQLUpdate.class );
 		SQLDelete sqlDelete = property.getAnnotation( SQLDelete.class );
 		SQLDeleteAll sqlDeleteAll = property.getAnnotation( SQLDeleteAll.class );
 		Loader loader = property.getAnnotation( Loader.class );
 		if ( sqlInsert != null ) {
 			collection.setCustomSQLInsert( sqlInsert.sql().trim(), sqlInsert.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlInsert.check().toString().toLowerCase() )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			collection.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlUpdate.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDelete != null ) {
 			collection.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDelete.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			collection.setCustomSQLDeleteAll( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase() )
 			);
 		}
 		if ( loader != null ) {
 			collection.setLoaderName( loader.namedQuery() );
 		}
 
 		if (isMappedBy
 				&& (property.isAnnotationPresent( JoinColumn.class )
 					|| property.isAnnotationPresent( JoinColumns.class )
 					|| propertyHolder.getJoinTable( property ) != null ) ) {
 			String message = "Associations marked as mappedBy must not define database mappings like @JoinTable or @JoinColumn: ";
 			message += StringHelper.qualify( propertyHolder.getPath(), propertyName );
 			throw new AnnotationException( message );
 		}
 
 		collection.setInverse( isMappedBy );
 
 		//many to many may need some second pass informations
 		if ( !oneToMany && isMappedBy ) {
 			mappings.addMappedBy( getCollectionType().getName(), mappedBy, propertyName );
 		}
 		//TODO reducce tableBinder != null and oneToMany
 		XClass collectionType = getCollectionType();
 		if ( inheritanceStatePerClass == null) throw new AssertionFailure( "inheritanceStatePerClass not set" );
 		SecondPass sp = getSecondPass(
 				fkJoinColumns,
 				joinColumns,
 				inverseJoinColumns,
 				elementColumns,
 				mapKeyColumns, mapKeyManyToManyColumns, isEmbedded,
 				property, collectionType,
 				ignoreNotFound, oneToMany,
 				tableBinder, mappings
 		);
 		if ( collectionType.isAnnotationPresent( Embeddable.class )
 				|| property.isAnnotationPresent( ElementCollection.class ) //JPA 2
 				) {
 			// do it right away, otherwise @ManyToOne on composite element call addSecondPass
 			// and raise a ConcurrentModificationException
 			//sp.doSecondPass( CollectionHelper.EMPTY_MAP );
 			mappings.addSecondPass( sp, !isMappedBy );
 		}
 		else {
 			mappings.addSecondPass( sp, !isMappedBy );
 		}
 
 		mappings.addCollection( collection );
 
 		//property building
 		PropertyBinder binder = new PropertyBinder();
 		binder.setName( propertyName );
 		binder.setValue( collection );
 		binder.setCascade( cascadeStrategy );
 		if ( cascadeStrategy != null && cascadeStrategy.indexOf( "delete-orphan" ) >= 0 ) {
 			collection.setOrphanDelete( true );
 		}
 		binder.setAccessType( accessType );
 		binder.setProperty( property );
 		binder.setInsertable( insertable );
 		binder.setUpdatable( updatable );
 		Property prop = binder.makeProperty();
 		//we don't care about the join stuffs because the column is on the association table.
 		if (! declaringClassSet) throw new AssertionFailure( "DeclaringClass is not set in CollectionBinder while binding" );
 		propertyHolder.addProperty( prop, declaringClass );
 	}
 
 	private void applySortingAndOrdering(Collection collection) {
 		boolean isSorted = isSortedCollection;
 
 		boolean hadOrderBy = false;
 		boolean hadExplicitSort = false;
 
 		Class<? extends Comparator> comparatorClass = null;
 
 		if ( jpaOrderBy == null && sqlOrderBy == null ) {
 			if ( deprecatedSort != null ) {
 				LOG.debug( "Encountered deprecated @Sort annotation; use @SortNatural or @SortComparator instead." );
 				if ( naturalSort != null || comparatorSort != null ) {
 					throw buildIllegalSortCombination();
 				}
 				hadExplicitSort = deprecatedSort.type() != SortType.UNSORTED;
 				if ( deprecatedSort.type() == SortType.NATURAL ) {
 					isSorted = true;
 				}
 				else if ( deprecatedSort.type() == SortType.COMPARATOR ) {
 					isSorted = true;
 					comparatorClass = deprecatedSort.comparator();
 				}
 			}
 			else if ( naturalSort != null ) {
 				if ( comparatorSort != null ) {
 					throw buildIllegalSortCombination();
 				}
 				hadExplicitSort = true;
 			}
 			else if ( comparatorSort != null ) {
 				hadExplicitSort = true;
 				comparatorClass = comparatorSort.value();
 			}
 		}
 		else {
 			if ( jpaOrderBy != null && sqlOrderBy != null ) {
 				throw new AnnotationException(
 						String.format(
 								"Illegal combination of @%s and @%s on %s",
 								javax.persistence.OrderBy.class.getName(),
 								OrderBy.class.getName(),
 								safeCollectionRole()
 						)
 				);
 			}
 
 			hadOrderBy = true;
 			hadExplicitSort = false;
 
 			// we can only apply the sql-based order by up front.  The jpa order by has to wait for second pass
 			if ( sqlOrderBy != null ) {
 				collection.setOrderBy( sqlOrderBy.clause() );
 			}
 		}
 
 		if ( isSortedCollection ) {
 			if ( ! hadExplicitSort && !hadOrderBy ) {
 				throw new AnnotationException(
 						"A sorted collection must define and ordering or sorting : " + safeCollectionRole()
 				);
 			}
 		}
 
 		collection.setSorted( isSortedCollection || hadExplicitSort );
 
 		if ( comparatorClass != null ) {
 			try {
 				collection.setComparator( comparatorClass.newInstance() );
 			}
 			catch (Exception e) {
 				throw new AnnotationException(
 						String.format(
 								"Could not instantiate comparator class [%s] for %s",
 								comparatorClass.getName(),
 								safeCollectionRole()
 						)
 				);
 			}
 		}
 	}
 
 	private AnnotationException buildIllegalSortCombination() {
 		return new AnnotationException(
 				String.format(
 						"Illegal combination of annotations on %s.  Only one of @%s, @%s and @%s can be used",
 						safeCollectionRole(),
 						Sort.class.getName(),
 						SortNatural.class.getName(),
 						SortComparator.class.getName()
 				)
 		);
 	}
 
 	private void defineFetchingStrategy() {
 		LazyCollection lazy = property.getAnnotation( LazyCollection.class );
 		Fetch fetch = property.getAnnotation( Fetch.class );
 		OneToMany oneToMany = property.getAnnotation( OneToMany.class );
 		ManyToMany manyToMany = property.getAnnotation( ManyToMany.class );
 		ElementCollection elementCollection = property.getAnnotation( ElementCollection.class ); //jpa 2
 		ManyToAny manyToAny = property.getAnnotation( ManyToAny.class );
 		FetchType fetchType;
 		if ( oneToMany != null ) {
 			fetchType = oneToMany.fetch();
 		}
 		else if ( manyToMany != null ) {
 			fetchType = manyToMany.fetch();
 		}
 		else if ( elementCollection != null ) {
 			fetchType = elementCollection.fetch();
 		}
 		else if ( manyToAny != null ) {
 			fetchType = FetchType.LAZY;
 		}
 		else {
 			throw new AssertionFailure(
 					"Define fetch strategy on a property not annotated with @ManyToOne nor @OneToMany nor @CollectionOfElements"
 			);
 		}
 		if ( lazy != null ) {
 			collection.setLazy( !( lazy.value() == LazyCollectionOption.FALSE ) );
 			collection.setExtraLazy( lazy.value() == LazyCollectionOption.EXTRA );
 		}
 		else {
 			collection.setLazy( fetchType == FetchType.LAZY );
 			collection.setExtraLazy( false );
 		}
 		if ( fetch != null ) {
 			if ( fetch.value() == org.hibernate.annotations.FetchMode.JOIN ) {
 				collection.setFetchMode( FetchMode.JOIN );
 				collection.setLazy( false );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SELECT ) {
 				collection.setFetchMode( FetchMode.SELECT );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SUBSELECT ) {
 				collection.setFetchMode( FetchMode.SELECT );
 				collection.setSubselectLoadable( true );
 				collection.getOwner().setSubselectLoadableCollections( true );
 			}
 			else {
 				throw new AssertionFailure( "Unknown FetchMode: " + fetch.value() );
 			}
 		}
 		else {
 			collection.setFetchMode( AnnotationBinder.getFetchMode( fetchType ) );
 		}
 	}
 
 	private XClass getCollectionType() {
 		if ( AnnotationBinder.isDefault( targetEntity, mappings ) ) {
 			if ( collectionType != null ) {
 				return collectionType;
 			}
 			else {
 				String errorMsg = "Collection has neither generic type or OneToMany.targetEntity() defined: "
 						+ safeCollectionRole();
 				throw new AnnotationException( errorMsg );
 			}
 		}
 		else {
 			return targetEntity;
 		}
 	}
 
 	public SecondPass getSecondPass(
 			final Ejb3JoinColumn[] fkJoinColumns,
 			final Ejb3JoinColumn[] keyColumns,
 			final Ejb3JoinColumn[] inverseColumns,
 			final Ejb3Column[] elementColumns,
 			final Ejb3Column[] mapKeyColumns,
 			final Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			final boolean isEmbedded,
 			final XProperty property,
 			final XClass collType,
 			final boolean ignoreNotFound,
 			final boolean unique,
 			final TableBinder assocTableBinder,
 			final Mappings mappings) {
 		return new CollectionSecondPass( mappings, collection ) {
 			@Override
             public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas) throws MappingException {
 				bindStarToManySecondPass(
 						persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns,
 						isEmbedded, property, unique, assocTableBinder, ignoreNotFound, mappings
 				);
 			}
 		};
 	}
 
 	/**
 	 * return true if it's a Fk, false if it's an association table
 	 */
 	protected boolean bindStarToManySecondPass(
 			Map persistentClasses,
 			XClass collType,
 			Ejb3JoinColumn[] fkJoinColumns,
 			Ejb3JoinColumn[] keyColumns,
 			Ejb3JoinColumn[] inverseColumns,
 			Ejb3Column[] elementColumns,
 			boolean isEmbedded,
 			XProperty property,
 			boolean unique,
 			TableBinder associationTableBinder,
 			boolean ignoreNotFound,
 			Mappings mappings) {
 		PersistentClass persistentClass = (PersistentClass) persistentClasses.get( collType.getName() );
 		boolean reversePropertyInJoin = false;
 		if ( persistentClass != null && StringHelper.isNotEmpty( this.mappedBy ) ) {
 			try {
 				reversePropertyInJoin = 0 != persistentClass.getJoinNumber(
 						persistentClass.getRecursiveProperty( this.mappedBy )
 				);
 			}
 			catch (MappingException e) {
 				StringBuilder error = new StringBuilder( 80 );
 				error.append( "mappedBy reference an unknown target entity property: " )
 						.append( collType ).append( "." ).append( this.mappedBy )
 						.append( " in " )
 						.append( collection.getOwnerEntityName() )
 						.append( "." )
 						.append( property.getName() );
 				throw new AnnotationException( error.toString() );
 			}
 		}
 		if ( persistentClass != null
 				&& !reversePropertyInJoin
 				&& oneToMany
 				&& !this.isExplicitAssociationTable
 				&& ( joinColumns[0].isImplicit() && !BinderHelper.isEmptyAnnotationValue( this.mappedBy ) //implicit @JoinColumn
 				|| !fkJoinColumns[0].isImplicit() ) //this is an explicit @JoinColumn
 				) {
 			//this is a Foreign key
 			bindOneToManySecondPass(
 					getCollection(),
 					persistentClasses,
 					fkJoinColumns,
 					collType,
 					cascadeDeleteEnabled,
 					ignoreNotFound,
 					mappings,
 					inheritanceStatePerClass
 			);
 			return true;
 		}
 		else {
 			//this is an association table
 			bindManyToManySecondPass(
 					this.collection,
 					persistentClasses,
 					keyColumns,
 					inverseColumns,
 					elementColumns,
 					isEmbedded, collType,
 					ignoreNotFound, unique,
 					cascadeDeleteEnabled,
 					associationTableBinder,
 					property,
 					propertyHolder,
 					mappings
 			);
 			return false;
 		}
 	}
 
 	protected void bindOneToManySecondPass(
 			Collection collection,
 			Map persistentClasses,
 			Ejb3JoinColumn[] fkJoinColumns,
 			XClass collectionType,
 			boolean cascadeDeleteEnabled,
 			boolean ignoreNotFound,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debugf( "Binding a OneToMany: %s.%s through a foreign key", propertyHolder.getEntityName(), propertyName );
 		}
 		org.hibernate.mapping.OneToMany oneToMany = new org.hibernate.mapping.OneToMany( mappings, collection.getOwner() );
 		collection.setElement( oneToMany );
 		oneToMany.setReferencedEntityName( collectionType.getName() );
 		oneToMany.setIgnoreNotFound( ignoreNotFound );
 
 		String assocClass = oneToMany.getReferencedEntityName();
 		PersistentClass associatedClass = (PersistentClass) persistentClasses.get( assocClass );
 		if ( jpaOrderBy != null ) {
 			final String jpaOrderByFragment = jpaOrderBy.value();
 			if ( StringHelper.isNotEmpty( jpaOrderByFragment ) ) {
 				final String orderByFragment = buildOrderByClauseFromHql(
 						jpaOrderBy.value(),
 						associatedClass,
 						collection.getRole()
 				);
 				if ( StringHelper.isNotEmpty( orderByFragment ) ) {
 					collection.setOrderBy( orderByFragment );
 				}
 			}
 		}
 
 		if ( mappings == null ) {
 			throw new AssertionFailure(
 					"CollectionSecondPass for oneToMany should not be called with null mappings"
 			);
 		}
 		Map<String, Join> joins = mappings.getJoins( assocClass );
 		if ( associatedClass == null ) {
 			throw new MappingException(
 					"Association references unmapped class: " + assocClass
 			);
 		}
 		oneToMany.setAssociatedClass( associatedClass );
 		for (Ejb3JoinColumn column : fkJoinColumns) {
 			column.setPersistentClass( associatedClass, joins, inheritanceStatePerClass );
 			column.setJoins( joins );
 			collection.setCollectionTable( column.getTable() );
 		}
 		if ( debugEnabled ) {
 			LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 		}
 		bindFilters( false );
 		bindCollectionSecondPass( collection, null, fkJoinColumns, cascadeDeleteEnabled, property, mappings );
 		if ( !collection.isInverse()
 				&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = oneToMany.getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + fkJoinColumns[0].getPropertyName() + '_' + fkJoinColumns[0].getLogicalColumnName() + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 
 	private void bindFilters(boolean hasAssociationTable) {
 		Filter simpleFilter = property.getAnnotation( Filter.class );
 		//set filtering
 		//test incompatible choices
 		//if ( StringHelper.isNotEmpty( where ) ) collection.setWhere( where );
 		if ( simpleFilter != null ) {
 			if ( hasAssociationTable ) {
 				collection.addManyToManyFilter(simpleFilter.name(), getCondition(simpleFilter), simpleFilter.deduceAliasInjectionPoints(),
 						toAliasTableMap(simpleFilter.aliases()), toAliasEntityMap(simpleFilter.aliases()));
 			}
 			else {
 				collection.addFilter(simpleFilter.name(), getCondition(simpleFilter), simpleFilter.deduceAliasInjectionPoints(),
 						toAliasTableMap(simpleFilter.aliases()), toAliasEntityMap(simpleFilter.aliases()));
 			}
 		}
 		Filters filters = property.getAnnotation( Filters.class );
 		if ( filters != null ) {
 			for (Filter filter : filters.value()) {
 				if ( hasAssociationTable ) {
 					collection.addManyToManyFilter( filter.name(), getCondition(filter), filter.deduceAliasInjectionPoints(),
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 				else {
 					collection.addFilter(filter.name(), getCondition(filter), filter.deduceAliasInjectionPoints(),
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 			}
 		}
 		FilterJoinTable simpleFilterJoinTable = property.getAnnotation( FilterJoinTable.class );
 		if ( simpleFilterJoinTable != null ) {
 			if ( hasAssociationTable ) {
 				collection.addFilter(simpleFilterJoinTable.name(), simpleFilterJoinTable.condition(), 
 						simpleFilterJoinTable.deduceAliasInjectionPoints(), 
 						toAliasTableMap(simpleFilterJoinTable.aliases()), toAliasEntityMap(simpleFilterJoinTable.aliases()));
 					}
 			else {
 				throw new AnnotationException(
 						"Illegal use of @FilterJoinTable on an association without join table:"
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 		FilterJoinTables filterJoinTables = property.getAnnotation( FilterJoinTables.class );
 		if ( filterJoinTables != null ) {
 			for (FilterJoinTable filter : filterJoinTables.value()) {
 				if ( hasAssociationTable ) {
 					collection.addFilter(filter.name(), filter.condition(), 
 							filter.deduceAliasInjectionPoints(), 
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 				else {
 					throw new AnnotationException(
 							"Illegal use of @FilterJoinTable on an association without join table:"
 									+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 					);
 				}
 			}
 		}
 
 		Where where = property.getAnnotation( Where.class );
 		String whereClause = where == null ? null : where.clause();
 		if ( StringHelper.isNotEmpty( whereClause ) ) {
 			if ( hasAssociationTable ) {
 				collection.setManyToManyWhere( whereClause );
 			}
 			else {
 				collection.setWhere( whereClause );
 			}
 		}
 
 		WhereJoinTable whereJoinTable = property.getAnnotation( WhereJoinTable.class );
 		String whereJoinTableClause = whereJoinTable == null ? null : whereJoinTable.clause();
 		if ( StringHelper.isNotEmpty( whereJoinTableClause ) ) {
 			if ( hasAssociationTable ) {
 				collection.setWhere( whereJoinTableClause );
 			}
 			else {
 				throw new AnnotationException(
 						"Illegal use of @WhereJoinTable on an association without join table:"
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 //		This cannot happen in annotations since the second fetch is hardcoded to join
 //		if ( ( ! collection.getManyToManyFilterMap().isEmpty() || collection.getManyToManyWhere() != null ) &&
 //		        collection.getFetchMode() == FetchMode.JOIN &&
 //		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 //			throw new MappingException(
 //			        "association with join table  defining filter or where without join fetching " +
 //			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 //				);
 //		}
 	}
 	
 	private String getCondition(FilterJoinTable filter) {
 		//set filtering
 		String name = filter.name();
 		String cond = filter.condition();
 		return getCondition( cond, name );
 	}
 	
 	private String getCondition(Filter filter) {
 		//set filtering
 		String name = filter.name();
 		String cond = filter.condition();
 		return getCondition( cond, name );
 	}
 
 	private String getCondition(String cond, String name) {
 		if ( BinderHelper.isEmptyAnnotationValue( cond ) ) {
 			cond = mappings.getFilterDefinition( name ).getDefaultFilterCondition();
 			if ( StringHelper.isEmpty( cond ) ) {
 				throw new AnnotationException(
 						"no filter condition found for filter " + name + " in "
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 		return cond;
 	}
 
 	public void setCache(Cache cacheAnn) {
 		if ( cacheAnn != null ) {
 			cacheRegionName = BinderHelper.isEmptyAnnotationValue( cacheAnn.region() ) ? null : cacheAnn.region();
 			cacheConcurrencyStrategy = EntityBinder.getCacheConcurrencyStrategy( cacheAnn.usage() );
 		}
 		else {
 			cacheConcurrencyStrategy = null;
 			cacheRegionName = null;
 		}
 	}
 
 	public void setOneToMany(boolean oneToMany) {
 		this.oneToMany = oneToMany;
 	}
 
 	public void setIndexColumn(IndexColumn indexColumn) {
 		this.indexColumn = indexColumn;
 	}
 
 	public void setMapKey(MapKey key) {
 		if ( key != null ) {
 			mapKeyPropertyName = key.name();
 		}
 	}
 
 	private static String buildOrderByClauseFromHql(String orderByFragment, PersistentClass associatedClass, String role) {
 		if ( orderByFragment != null ) {
 			if ( orderByFragment.length() == 0 ) {
 				//order by id
 				return "id asc";
 			}
 			else if ( "desc".equals( orderByFragment ) ) {
 				return "id desc";
 			}
 		}
 		return orderByFragment;
 	}
 
 	private static String adjustUserSuppliedValueCollectionOrderingFragment(String orderByFragment) {
 		if ( orderByFragment != null ) {
 			// NOTE: "$element$" is a specially recognized collection property recognized by the collection persister
 			if ( orderByFragment.length() == 0 ) {
 				//order by element
 				return "$element$ asc";
 			}
 			else if ( "desc".equals( orderByFragment ) ) {
 				return "$element$ desc";
 			}
 		}
 		return orderByFragment;
 	}
 
 	private static SimpleValue buildCollectionKey(
 			Collection collValue,
 			Ejb3JoinColumn[] joinColumns,
 			boolean cascadeDeleteEnabled,
 			XProperty property,
 			Mappings mappings) {
 		//binding key reference using column
 		KeyValue keyVal;
 		//give a chance to override the referenced property name
 		//has to do that here because the referencedProperty creation happens in a FKSecondPass for Many to one yuk!
 		if ( joinColumns.length > 0 && StringHelper.isNotEmpty( joinColumns[0].getMappedBy() ) ) {
 			String entityName = joinColumns[0].getManyToManyOwnerSideEntityName() != null ?
 					"inverse__" + joinColumns[0].getManyToManyOwnerSideEntityName() :
 					joinColumns[0].getPropertyHolder().getEntityName();
 			String propRef = mappings.getPropertyReferencedAssociation(
 					entityName,
 					joinColumns[0].getMappedBy()
 			);
 			if ( propRef != null ) {
 				collValue.setReferencedPropertyName( propRef );
 				mappings.addPropertyReference( collValue.getOwnerEntityName(), propRef );
 			}
 		}
 		String propRef = collValue.getReferencedPropertyName();
 		if ( propRef == null ) {
 			keyVal = collValue.getOwner().getIdentifier();
 		}
 		else {
 			keyVal = (KeyValue) collValue.getOwner()
 					.getReferencedProperty( propRef )
 					.getValue();
 		}
 		DependantValue key = new DependantValue( mappings, collValue.getCollectionTable(), keyVal );
 		key.setTypeName( null );
 		Ejb3Column.checkPropertyConsistency( joinColumns, collValue.getOwnerEntityName() );
 		key.setNullable( joinColumns.length == 0 || joinColumns[0].isNullable() );
 		key.setUpdateable( joinColumns.length == 0 || joinColumns[0].isUpdatable() );
 		key.setCascadeDeleteEnabled( cascadeDeleteEnabled );
 		collValue.setKey( key );
 		ForeignKey fk = property != null ? property.getAnnotation( ForeignKey.class ) : null;
 		String fkName = fk != null ? fk.name() : "";
 		if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) key.setForeignKeyName( fkName );
 		return key;
 	}
 
 	protected void bindManyToManySecondPass(
 			Collection collValue,
 			Map persistentClasses,
 			Ejb3JoinColumn[] joinColumns,
 			Ejb3JoinColumn[] inverseJoinColumns,
 			Ejb3Column[] elementColumns,
 			boolean isEmbedded,
 			XClass collType,
 			boolean ignoreNotFound, boolean unique,
 			boolean cascadeDeleteEnabled,
 			TableBinder associationTableBinder,
 			XProperty property,
 			PropertyHolder parentPropertyHolder,
 			Mappings mappings) throws MappingException {
-		PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( collType.getName() );
+		if ( property == null ) {
+			throw new IllegalArgumentException( "null was passed for argument property" );
+		}
+
+		final PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( collType.getName() );
 		final String hqlOrderBy = extractHqlOrderBy( jpaOrderBy );
 
 		boolean isCollectionOfEntities = collectionEntity != null;
 		ManyToAny anyAnn = property.getAnnotation( ManyToAny.class );
         if (LOG.isDebugEnabled()) {
 			String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
             if (isCollectionOfEntities && unique) LOG.debugf("Binding a OneToMany: %s through an association table", path);
             else if (isCollectionOfEntities) LOG.debugf("Binding as ManyToMany: %s", path);
             else if (anyAnn != null) LOG.debugf("Binding a ManyToAny: %s", path);
             else LOG.debugf("Binding a collection of element: %s", path);
 		}
 		//check for user error
 		if ( !isCollectionOfEntities ) {
 			if ( property.isAnnotationPresent( ManyToMany.class ) || property.isAnnotationPresent( OneToMany.class ) ) {
 				String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 				throw new AnnotationException(
 						"Use of @OneToMany or @ManyToMany targeting an unmapped class: " + path + "[" + collType + "]"
 				);
 			}
 			else if ( anyAnn != null ) {
 				if ( parentPropertyHolder.getJoinTable( property ) == null ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					throw new AnnotationException(
 							"@JoinTable is mandatory when @ManyToAny is used: " + path
 					);
 				}
 			}
 			else {
 				JoinTable joinTableAnn = parentPropertyHolder.getJoinTable( property );
 				if ( joinTableAnn != null && joinTableAnn.inverseJoinColumns().length > 0 ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					throw new AnnotationException(
 							"Use of @JoinTable.inverseJoinColumns targeting an unmapped class: " + path + "[" + collType + "]"
 					);
 				}
 			}
 		}
 
 		boolean mappedBy = !BinderHelper.isEmptyAnnotationValue( joinColumns[0].getMappedBy() );
 		if ( mappedBy ) {
 			if ( !isCollectionOfEntities ) {
 				StringBuilder error = new StringBuilder( 80 )
 						.append(
 								"Collection of elements must not have mappedBy or association reference an unmapped entity: "
 						)
 						.append( collValue.getOwnerEntityName() )
 						.append( "." )
 						.append( joinColumns[0].getPropertyName() );
 				throw new AnnotationException( error.toString() );
 			}
 			Property otherSideProperty;
 			try {
 				otherSideProperty = collectionEntity.getRecursiveProperty( joinColumns[0].getMappedBy() );
 			}
 			catch (MappingException e) {
 				StringBuilder error = new StringBuilder( 80 );
 				error.append( "mappedBy reference an unknown target entity property: " )
 						.append( collType ).append( "." ).append( joinColumns[0].getMappedBy() )
 						.append( " in " )
 						.append( collValue.getOwnerEntityName() )
 						.append( "." )
 						.append( joinColumns[0].getPropertyName() );
 				throw new AnnotationException( error.toString() );
 			}
 			Table table;
 			if ( otherSideProperty.getValue() instanceof Collection ) {
 				//this is a collection on the other side
 				table = ( (Collection) otherSideProperty.getValue() ).getCollectionTable();
 			}
 			else {
 				//This is a ToOne with a @JoinTable or a regular property
 				table = otherSideProperty.getValue().getTable();
 			}
 			collValue.setCollectionTable( table );
 			String entityName = collectionEntity.getEntityName();
 			for (Ejb3JoinColumn column : joinColumns) {
 				//column.setDefaultColumnHeader( joinColumns[0].getMappedBy() ); //seems not to be used, make sense
 				column.setManyToManyOwnerSideEntityName( entityName );
 			}
 		}
 		else {
 			//TODO: only for implicit columns?
 			//FIXME NamingStrategy
 			for (Ejb3JoinColumn column : joinColumns) {
 				String mappedByProperty = mappings.getFromMappedBy(
 						collValue.getOwnerEntityName(), column.getPropertyName()
 				);
 				Table ownerTable = collValue.getOwner().getTable();
 				column.setMappedBy(
 						collValue.getOwner().getEntityName(), mappings.getLogicalTableName( ownerTable ),
 						mappedByProperty
 				);
 //				String header = ( mappedByProperty == null ) ? mappings.getLogicalTableName( ownerTable ) : mappedByProperty;
 //				column.setDefaultColumnHeader( header );
 			}
 			if ( StringHelper.isEmpty( associationTableBinder.getName() ) ) {
 				//default value
 				associationTableBinder.setDefaultName(
 						collValue.getOwner().getEntityName(),
 						mappings.getLogicalTableName( collValue.getOwner().getTable() ),
 						collectionEntity != null ? collectionEntity.getEntityName() : null,
 						collectionEntity != null ? mappings.getLogicalTableName( collectionEntity.getTable() ) : null,
 						joinColumns[0].getPropertyName()
 				);
 			}
 			associationTableBinder.setJPA2ElementCollection( !isCollectionOfEntities && property.isAnnotationPresent( ElementCollection.class ));
 			collValue.setCollectionTable( associationTableBinder.bind() );
 		}
 		bindFilters( isCollectionOfEntities );
 		bindCollectionSecondPass( collValue, collectionEntity, joinColumns, cascadeDeleteEnabled, property, mappings );
 
 		ManyToOne element = null;
 		if ( isCollectionOfEntities ) {
 			element =
 					new ManyToOne( mappings,  collValue.getCollectionTable() );
 			collValue.setElement( element );
 			element.setReferencedEntityName( collType.getName() );
 			//element.setFetchMode( fetchMode );
 			//element.setLazy( fetchMode != FetchMode.JOIN );
 			//make the second join non lazy
 			element.setFetchMode( FetchMode.JOIN );
 			element.setLazy( false );
 			element.setIgnoreNotFound( ignoreNotFound );
 			// as per 11.1.38 of JPA 2.0 spec, default to primary key if no column is specified by @OrderBy.
 			if ( hqlOrderBy != null ) {
 				collValue.setManyToManyOrdering(
 						buildOrderByClauseFromHql( hqlOrderBy, collectionEntity, collValue.getRole() )
 				);
 			}
-			ForeignKey fk = property != null ? property.getAnnotation( ForeignKey.class ) : null;
+			final ForeignKey fk = property.getAnnotation( ForeignKey.class );
 			String fkName = fk != null ? fk.inverseName() : "";
-			if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) element.setForeignKeyName( fkName );
+			if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) {
+				element.setForeignKeyName( fkName );
+			}
 		}
 		else if ( anyAnn != null ) {
 			//@ManyToAny
 			//Make sure that collTyp is never used during the @ManyToAny branch: it will be set to void.class
 			PropertyData inferredData = new PropertyInferredData(null, property, "unsupported", mappings.getReflectionManager() );
 			//override the table
 			for (Ejb3Column column : inverseJoinColumns) {
 				column.setTable( collValue.getCollectionTable() );
 			}
 			Any any = BinderHelper.buildAnyValue( anyAnn.metaDef(), inverseJoinColumns, anyAnn.metaColumn(),
 					inferredData, cascadeDeleteEnabled, Nullability.NO_CONSTRAINT,
 					propertyHolder, new EntityBinder(), true, mappings );
 			collValue.setElement( any );
 		}
 		else {
 			XClass elementClass;
 			AnnotatedClassType classType;
 
 			PropertyHolder holder = null;
 			if ( BinderHelper.PRIMITIVE_NAMES.contains( collType.getName() ) ) {
 				classType = AnnotatedClassType.NONE;
 				elementClass = null;
 			}
 			else {
 				elementClass = collType;
 				classType = mappings.getClassType( elementClass );
 
 				holder = PropertyHolderBuilder.buildPropertyHolder(
 						collValue,
 						collValue.getRole(),
 						elementClass,
 						property, parentPropertyHolder, mappings
 				);
 				//force in case of attribute override
 				boolean attributeOverride = property.isAnnotationPresent( AttributeOverride.class )
 						|| property.isAnnotationPresent( AttributeOverrides.class );
 				if ( isEmbedded || attributeOverride ) {
 					classType = AnnotatedClassType.EMBEDDABLE;
 				}
 			}
 
 			if ( AnnotatedClassType.EMBEDDABLE.equals( classType ) ) {
 				EntityBinder entityBinder = new EntityBinder();
 				PersistentClass owner = collValue.getOwner();
 				boolean isPropertyAnnotated;
 				//FIXME support @Access for collection of elements
 				//String accessType = access != null ? access.value() : null;
 				if ( owner.getIdentifierProperty() != null ) {
 					isPropertyAnnotated = owner.getIdentifierProperty().getPropertyAccessorName().equals( "property" );
 				}
 				else if ( owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0 ) {
 					Property prop = (Property) owner.getIdentifierMapper().getPropertyIterator().next();
 					isPropertyAnnotated = prop.getPropertyAccessorName().equals( "property" );
 				}
 				else {
 					throw new AssertionFailure( "Unable to guess collection property accessor name" );
 				}
 
 				PropertyData inferredData;
 				if ( isMap() ) {
 					//"value" is the JPA 2 prefix for map values (used to be "element")
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "element", elementClass );
 					}
 					else {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "value", elementClass );
 					}
 				}
 				else {
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "element", elementClass );
 					}
 					else {
 						//"collection&&element" is not a valid property name => placeholder
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "collection&&element", elementClass );
 					}
 				}
 				//TODO be smart with isNullable
 				Component component = AnnotationBinder.fillComponent(
 						holder, inferredData, isPropertyAnnotated ? AccessType.PROPERTY : AccessType.FIELD, true,
 						entityBinder, false, false,
 						true, mappings, inheritanceStatePerClass
 				);
 
 				collValue.setElement( component );
 
 				if ( StringHelper.isNotEmpty( hqlOrderBy ) ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					String orderBy = adjustUserSuppliedValueCollectionOrderingFragment( hqlOrderBy );
 					if ( orderBy != null ) {
 						collValue.setOrderBy( orderBy );
 					}
 				}
 			}
 			else {
 				SimpleValueBinder elementBinder = new SimpleValueBinder();
 				elementBinder.setMappings( mappings );
 				elementBinder.setReturnedClassName( collType.getName() );
 				if ( elementColumns == null || elementColumns.length == 0 ) {
 					elementColumns = new Ejb3Column[1];
 					Ejb3Column column = new Ejb3Column();
 					column.setImplicit( false );
 					//not following the spec but more clean
 					column.setNullable( true );
 					column.setLength( Ejb3Column.DEFAULT_COLUMN_LENGTH );
 					column.setLogicalColumnName( Collection.DEFAULT_ELEMENT_COLUMN_NAME );
 					//TODO create an EMPTY_JOINS collection
 					column.setJoins( new HashMap<String, Join>() );
 					column.setMappings( mappings );
 					column.bind();
 					elementColumns[0] = column;
 				}
 				//override the table
 				for (Ejb3Column column : elementColumns) {
 					column.setTable( collValue.getCollectionTable() );
 				}
 				elementBinder.setColumns( elementColumns );
 				elementBinder.setType( property, elementClass, collValue.getOwnerEntityName() );
 				elementBinder.setPersistentClassName( propertyHolder.getEntityName() );
 				elementBinder.setAccessType( accessType );
 				collValue.setElement( elementBinder.make() );
 				String orderBy = adjustUserSuppliedValueCollectionOrderingFragment( hqlOrderBy );
 				if ( orderBy != null ) {
 					collValue.setOrderBy( orderBy );
 				}
 			}
 		}
 
 		checkFilterConditions( collValue );
 
 		//FIXME: do optional = false
 		if ( isCollectionOfEntities ) {
 			bindManytoManyInverseFk( collectionEntity, inverseJoinColumns, element, unique, mappings );
 		}
 
 	}
 
 	private String extractHqlOrderBy(javax.persistence.OrderBy jpaOrderBy) {
 		if ( jpaOrderBy != null ) {
 			final String jpaOrderByFragment = jpaOrderBy.value();
 			return StringHelper.isNotEmpty( jpaOrderByFragment )
 					? jpaOrderByFragment
 					: null;
 		}
 		return null;
 	}
 
 	private static void checkFilterConditions(Collection collValue) {
 		//for now it can't happen, but sometime soon...
 		if ( ( collValue.getFilters().size() != 0 || StringHelper.isNotEmpty( collValue.getWhere() ) ) &&
 				collValue.getFetchMode() == FetchMode.JOIN &&
 				!( collValue.getElement() instanceof SimpleValue ) && //SimpleValue (CollectionOfElements) are always SELECT but it does not matter
 				collValue.getElement().getFetchMode() != FetchMode.JOIN ) {
 			throw new MappingException(
 					"@ManyToMany or @CollectionOfElements defining filter or where without join fetching "
 							+ "not valid within collection using join fetching[" + collValue.getRole() + "]"
 			);
 		}
 	}
 
 	private static void bindCollectionSecondPass(
 			Collection collValue,
 			PersistentClass collectionEntity,
 			Ejb3JoinColumn[] joinColumns,
 			boolean cascadeDeleteEnabled,
 			XProperty property,
 			Mappings mappings) {
 		BinderHelper.createSyntheticPropertyReference(
 				joinColumns, collValue.getOwner(), collectionEntity, collValue, false, mappings
 		);
 		SimpleValue key = buildCollectionKey( collValue, joinColumns, cascadeDeleteEnabled, property, mappings );
 		if ( property.isAnnotationPresent( ElementCollection.class ) && joinColumns.length > 0 ) {
 			joinColumns[0].setJPA2ElementCollection( true );
 		}
 		TableBinder.bindFk( collValue.getOwner(), collectionEntity, joinColumns, key, false, mappings );
 	}
 
 	public void setCascadeDeleteEnabled(boolean onDeleteCascade) {
 		this.cascadeDeleteEnabled = onDeleteCascade;
 	}
 
 	private String safeCollectionRole() {
 		if ( propertyHolder != null ) {
 			return propertyHolder.getEntityName() + "." + propertyName;
 		}
 		else {
 			return "";
 		}
 	}
 
 
 	/**
 	 * bind the inverse FK of a ManyToMany
 	 * If we are in a mappedBy case, read the columns from the associated
 	 * collection element
 	 * Otherwise delegates to the usual algorithm
 	 */
 	public static void bindManytoManyInverseFk(
 			PersistentClass referencedEntity,
 			Ejb3JoinColumn[] columns,
 			SimpleValue value,
 			boolean unique,
 			Mappings mappings) {
 		final String mappedBy = columns[0].getMappedBy();
 		if ( StringHelper.isNotEmpty( mappedBy ) ) {
 			final Property property = referencedEntity.getRecursiveProperty( mappedBy );
 			Iterator mappedByColumns;
 			if ( property.getValue() instanceof Collection ) {
 				mappedByColumns = ( (Collection) property.getValue() ).getKey().getColumnIterator();
 			}
 			else {
 				//find the appropriate reference key, can be in a join
 				Iterator joinsIt = referencedEntity.getJoinIterator();
 				KeyValue key = null;
 				while ( joinsIt.hasNext() ) {
 					Join join = (Join) joinsIt.next();
 					if ( join.containsProperty( property ) ) {
 						key = join.getKey();
 						break;
 					}
 				}
 				if ( key == null ) key = property.getPersistentClass().getIdentifier();
 				mappedByColumns = key.getColumnIterator();
 			}
 			while ( mappedByColumns.hasNext() ) {
 				Column column = (Column) mappedByColumns.next();
 				columns[0].linkValueUsingAColumnCopy( column, value );
 			}
 			String referencedPropertyName =
 					mappings.getPropertyReferencedAssociation(
 							"inverse__" + referencedEntity.getEntityName(), mappedBy
 					);
 			if ( referencedPropertyName != null ) {
 				//TODO always a many to one?
 				( (ManyToOne) value ).setReferencedPropertyName( referencedPropertyName );
 				mappings.addUniquePropertyReference( referencedEntity.getEntityName(), referencedPropertyName );
 			}
 			value.createForeignKey();
 		}
 		else {
 			BinderHelper.createSyntheticPropertyReference( columns, referencedEntity, null, value, true, mappings );
 			TableBinder.bindFk( referencedEntity, null, columns, value, unique, mappings );
 		}
 	}
 
 	public void setFkJoinColumns(Ejb3JoinColumn[] ejb3JoinColumns) {
 		this.fkJoinColumns = ejb3JoinColumns;
 	}
 
 	public void setExplicitAssociationTable(boolean explicitAssocTable) {
 		this.isExplicitAssociationTable = explicitAssocTable;
 	}
 
 	public void setElementColumns(Ejb3Column[] elementColumns) {
 		this.elementColumns = elementColumns;
 	}
 
 	public void setEmbedded(boolean annotationPresent) {
 		this.isEmbedded = annotationPresent;
 	}
 
 	public void setProperty(XProperty property) {
 		this.property = property;
 	}
 
 	public void setIgnoreNotFound(boolean ignoreNotFound) {
 		this.ignoreNotFound = ignoreNotFound;
 	}
 
 	public void setMapKeyColumns(Ejb3Column[] mapKeyColumns) {
 		this.mapKeyColumns = mapKeyColumns;
 	}
 
 	public void setMapKeyManyToManyColumns(Ejb3JoinColumn[] mapJoinColumns) {
 		this.mapKeyManyToManyColumns = mapJoinColumns;
 	}
 
 	public void setLocalGenerators(HashMap<String, IdGenerator> localGenerators) {
 		this.localGenerators = localGenerators;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java
index 4b4da35a4f..29dee5d946 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java
@@ -1,505 +1,505 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.engine.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * Maintains a {@link org.hibernate.engine.spi.PersistenceContext}-level 2-way cross-reference (xref) between the 
  * identifiers and natural ids of entities associated with the PersistenceContext.
  * <p/>
  * Most operations resolve the proper {@link NaturalIdResolutionCache} to use based on the persister and 
  * simply delegate calls there.
  * 
  * @author Steve Ebersole
  */
 public class NaturalIdXrefDelegate {
 	private static final Logger LOG = Logger.getLogger( NaturalIdXrefDelegate.class );
 
 	private final StatefulPersistenceContext persistenceContext;
 	private final Map<EntityPersister, NaturalIdResolutionCache> naturalIdResolutionCacheMap = new ConcurrentHashMap<EntityPersister, NaturalIdResolutionCache>();
 
 	/**
 	 * Constructs a NaturalIdXrefDelegate
 	 *
 	 * @param persistenceContext The persistence context that owns this delegate
 	 */
 	public NaturalIdXrefDelegate(StatefulPersistenceContext persistenceContext) {
 		this.persistenceContext = persistenceContext;
 	}
 
 	/**
 	 * Access to the session (via the PersistenceContext) to which this delegate ultimately belongs.
 	 *
 	 * @return The session
 	 */
 	protected SessionImplementor session() {
 		return persistenceContext.getSession();
 	}
 
 	/**
 	 * Creates needed cross-reference entries between the given primary (pk) and natural (naturalIdValues) key values
 	 * for the given persister.  Returns an indication of whether entries were actually made.  If those values already
 	 * existed as an entry, {@code false} would be returned here.
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param pk The primary key value
 	 * @param naturalIdValues The natural id value(s)
 	 *
 	 * @return {@code true} if a new entry was actually added; {@code false} otherwise.
 	 */
 	public boolean cacheNaturalIdCrossReference(EntityPersister persister, Serializable pk, Object[] naturalIdValues) {
 		validateNaturalId( persister, naturalIdValues );
 
 		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		if ( entityNaturalIdResolutionCache == null ) {
 			entityNaturalIdResolutionCache = new NaturalIdResolutionCache( persister );
 			naturalIdResolutionCacheMap.put( persister, entityNaturalIdResolutionCache );
 		}
 		return entityNaturalIdResolutionCache.cache( pk, naturalIdValues );
 	}
 
 	/**
 	 * Handle removing cross reference entries for the given natural-id/pk combo
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param pk The primary key value
 	 * @param naturalIdValues The natural id value(s)
 	 * 
 	 * @return The cached values, if any.  May be different from incoming values.
 	 */
 	public Object[] removeNaturalIdCrossReference(EntityPersister persister, Serializable pk, Object[] naturalIdValues) {
 		persister = locatePersisterForKey( persister );
 		validateNaturalId( persister, naturalIdValues );
 
 		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		Object[] sessionCachedNaturalIdValues = null;
 		if ( entityNaturalIdResolutionCache != null ) {
 			final CachedNaturalId cachedNaturalId = entityNaturalIdResolutionCache.pkToNaturalIdMap
 					.remove( pk );
 			if ( cachedNaturalId != null ) {
 				entityNaturalIdResolutionCache.naturalIdToPkMap.remove( cachedNaturalId );
 				sessionCachedNaturalIdValues = cachedNaturalId.getValues();
 			}
 		}
 
 		if ( persister.hasNaturalIdCache() ) {
 			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister
 					.getNaturalIdCacheAccessStrategy();
 			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session() );
 			naturalIdCacheAccessStrategy.evict( naturalIdCacheKey );
 
 			if ( sessionCachedNaturalIdValues != null
 					&& !Arrays.equals( sessionCachedNaturalIdValues, naturalIdValues ) ) {
 				final NaturalIdCacheKey sessionNaturalIdCacheKey = new NaturalIdCacheKey( sessionCachedNaturalIdValues, persister, session() );
 				naturalIdCacheAccessStrategy.evict( sessionNaturalIdCacheKey );
 			}
 		}
 
 		return sessionCachedNaturalIdValues;
 	}
 
 	/**
 	 * Are the naturals id values cached here (if any) for the given persister+pk combo the same as the given values?
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param pk The primary key value
 	 * @param naturalIdValues The natural id value(s) to check
 	 * 
 	 * @return {@code true} if the given naturalIdValues match the current cached values; {@code false} otherwise.
 	 */
 	public boolean sameAsCached(EntityPersister persister, Serializable pk, Object[] naturalIdValues) {
 		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		return entityNaturalIdResolutionCache != null
 				&& entityNaturalIdResolutionCache.sameAsCached( pk, naturalIdValues );
 	}
 
 	/**
 	 * It is only valid to define natural ids at the root of an entity hierarchy.  This method makes sure we are 
 	 * using the root persister.
 	 *
 	 * @param persister The persister representing the entity type.
 	 * 
 	 * @return The root persister.
 	 */
 	protected EntityPersister locatePersisterForKey(EntityPersister persister) {
 		return persistenceContext.getSession().getFactory().getEntityPersister( persister.getRootEntityName() );
 	}
 
 	/**
 	 * Invariant validate of the natural id.  Checks include<ul>
 	 *     <li>that the entity defines a natural id</li>
 	 *     <li>the number of natural id values matches the expected number</li>
 	 * </ul>
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param naturalIdValues The natural id values
 	 */
 	protected void validateNaturalId(EntityPersister persister, Object[] naturalIdValues) {
 		if ( !persister.hasNaturalIdentifier() ) {
 			throw new IllegalArgumentException( "Entity did not define a natrual-id" );
 		}
 		if ( persister.getNaturalIdentifierProperties().length != naturalIdValues.length ) {
 			throw new IllegalArgumentException( "Mismatch between expected number of natural-id values and found." );
 		}
 	}
 
 	/**
 	 * Given a persister and primary key, find the locally cross-referenced natural id.
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param pk The entity primary key
 	 * 
 	 * @return The corresponding cross-referenced natural id values, or {@code null} if none 
 	 */
 	public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk) {
 		persister = locatePersisterForKey( persister );
 		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		if ( entityNaturalIdResolutionCache == null ) {
 			return null;
 		}
 
 		final CachedNaturalId cachedNaturalId = entityNaturalIdResolutionCache.pkToNaturalIdMap.get( pk );
 		if ( cachedNaturalId == null ) {
 			return null;
 		}
 
 		return cachedNaturalId.getValues();
 	}
 
 	/**
 	 * Given a persister and natural-id value(s), find the locally cross-referenced primary key.  Will return
 	 * {@link PersistenceContext.NaturalIdHelper#INVALID_NATURAL_ID_REFERENCE} if the given natural ids are known to
 	 * be invalid (see {@link #stashInvalidNaturalIdReference}).
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param naturalIdValues The natural id value(s)
 	 * 
 	 * @return The corresponding cross-referenced primary key, 
 	 * 		{@link PersistenceContext.NaturalIdHelper#INVALID_NATURAL_ID_REFERENCE},
 	 * 		or {@code null} if none 
 	 */
 	public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues) {
 		persister = locatePersisterForKey( persister );
 		validateNaturalId( persister, naturalIdValues );
 
 		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 
 		Serializable pk;
 		final CachedNaturalId cachedNaturalId = new CachedNaturalId( persister, naturalIdValues );
 		if ( entityNaturalIdResolutionCache != null ) {
 			pk = entityNaturalIdResolutionCache.naturalIdToPkMap.get( cachedNaturalId );
 
 			// Found in session cache
 			if ( pk != null ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.trace(
 							"Resolved natural key -> primary key resolution in session cache: " +
 									persister.getRootEntityName() + "#[" +
 									Arrays.toString( naturalIdValues ) + "]"
 					);
 				}
 
 				return pk;
 			}
 
 			// if we did not find a hit, see if we know about these natural ids as invalid...
 			if ( entityNaturalIdResolutionCache.containsInvalidNaturalIdReference( naturalIdValues ) ) {
 				return PersistenceContext.NaturalIdHelper.INVALID_NATURAL_ID_REFERENCE;
 			}
 		}
 
 		// Session cache miss, see if second-level caching is enabled
 		if ( !persister.hasNaturalIdCache() ) {
 			return null;
 		}
 
 		// Try resolution from second-level cache
 		final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session() );
 
 		final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
 		pk = (Serializable) naturalIdCacheAccessStrategy.get( naturalIdCacheKey, session().getTimestamp() );
 
 		// Found in second-level cache, store in session cache
 		final SessionFactoryImplementor factory = session().getFactory();
 		if ( pk != null ) {
 			if ( factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor().naturalIdCacheHit(
 						naturalIdCacheAccessStrategy.getRegion().getName()
 				);
 			}
 
 			if ( LOG.isTraceEnabled() ) {
 				// protected to avoid Arrays.toString call unless needed
 				LOG.tracef(
 						"Found natural key [%s] -> primary key [%s] xref in second-level cache for %s",
 						Arrays.toString( naturalIdValues ),
 						pk,
 						persister.getRootEntityName()
 				);
 			}
 
 			if ( entityNaturalIdResolutionCache == null ) {
 				entityNaturalIdResolutionCache = new NaturalIdResolutionCache( persister );
 				naturalIdResolutionCacheMap.put( persister, entityNaturalIdResolutionCache );
 			}
 
 			entityNaturalIdResolutionCache.pkToNaturalIdMap.put( pk, cachedNaturalId );
 			entityNaturalIdResolutionCache.naturalIdToPkMap.put( cachedNaturalId, pk );
 		}
 		else if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().naturalIdCacheMiss( naturalIdCacheAccessStrategy.getRegion().getName() );
 		}
 
 		return pk;
 	}
 
 	/**
 	 * Return all locally cross-referenced primary keys for the given persister.  Used as part of load
 	 * synchronization process.
 	 *
 	 * @param persister The persister representing the entity type.
 	 * 
 	 * @return The primary keys
 	 * 
 	 * @see org.hibernate.NaturalIdLoadAccess#setSynchronizationEnabled
 	 */
 	public Collection<Serializable> getCachedPkResolutions(EntityPersister persister) {
 		persister = locatePersisterForKey( persister );
 
 		Collection<Serializable> pks = null;
 
 		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		if ( entityNaturalIdResolutionCache != null ) {
 			pks = entityNaturalIdResolutionCache.pkToNaturalIdMap.keySet();
 		}
 
 		if ( pks == null || pks.isEmpty() ) {
 			return java.util.Collections.emptyList();
 		}
 		else {
 			return java.util.Collections.unmodifiableCollection( pks );
 		}
 	}
 
 	/**
 	 * As part of "load synchronization process", if a particular natural id is found to have changed we need to track
 	 * its invalidity until after the next flush.  This method lets the "load synchronization process" indicate
 	 * when it has encountered such changes.
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param invalidNaturalIdValues The "old" natural id values.
 	 *
 	 * @see org.hibernate.NaturalIdLoadAccess#setSynchronizationEnabled
 	 */
 	public void stashInvalidNaturalIdReference(EntityPersister persister, Object[] invalidNaturalIdValues) {
 		persister = locatePersisterForKey( persister );
 
 		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		if ( entityNaturalIdResolutionCache == null ) {
 			throw new AssertionFailure( "Expecting NaturalIdResolutionCache to exist already for entity " + persister.getEntityName() );
 		}
 
 		entityNaturalIdResolutionCache.stashInvalidNaturalIdReference( invalidNaturalIdValues );
 	}
 
 	/**
 	 * Again, as part of "load synchronization process" we need to also be able to clear references to these
 	 * known-invalid natural-ids after flush.  This method exposes that capability.
 	 */
 	public void unStashInvalidNaturalIdReferences() {
 		for ( NaturalIdResolutionCache naturalIdResolutionCache : naturalIdResolutionCacheMap.values() ) {
 			naturalIdResolutionCache.unStashInvalidNaturalIdReferences();
 		}
 	}
 
 	/**
 	 * Used to put natural id values into collections.  Useful mainly to apply equals/hashCode implementations.
 	 */
-	private static class CachedNaturalId {
+	private static class CachedNaturalId implements Serializable {
 		private final EntityPersister persister;
 		private final Object[] values;
 		private final Type[] naturalIdTypes;
 		private int hashCode;
 
 		public CachedNaturalId(EntityPersister persister, Object[] values) {
 			this.persister = persister;
 			this.values = values;
 
 			final int prime = 31;
 			int hashCodeCalculation = 1;
 			hashCodeCalculation = prime * hashCodeCalculation + persister.hashCode();
 
 			final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 			naturalIdTypes = new Type[ naturalIdPropertyIndexes.length ];
 			int i = 0;
 			for ( int naturalIdPropertyIndex : naturalIdPropertyIndexes ) {
 				final Type type = persister.getPropertyType( persister.getPropertyNames()[ naturalIdPropertyIndex ] );
 				naturalIdTypes[i] = type;
 				final int elementHashCode = values[i] == null ? 0 :type.getHashCode( values[i], persister.getFactory() );
 				hashCodeCalculation = prime * hashCodeCalculation + elementHashCode;
 				i++;
 			}
 
 			this.hashCode = hashCodeCalculation;
 		}
 
 		public Object[] getValues() {
 			return values;
 		}
 
 		@Override
 		public int hashCode() {
 			return this.hashCode;
 		}
 
 		@Override
 		public boolean equals(Object obj) {
 			if ( this == obj ) {
 				return true;
 			}
 			if ( obj == null ) {
 				return false;
 			}
 			if ( getClass() != obj.getClass() ) {
 				return false;
 			}
 
 			final CachedNaturalId other = (CachedNaturalId) obj;
 			return persister.equals( other.persister ) && isSame( other.values );
 		}
 
 		private boolean isSame(Object[] otherValues) {
 			// lengths have already been verified at this point
 			for ( int i = 0; i < naturalIdTypes.length; i++ ) {
 				if ( ! naturalIdTypes[i].isEqual( values[i], otherValues[i], persister.getFactory() ) ) {
 					return false;
 				}
 			}
 			return true;
 		}
 	}
 
 	/**
 	 * Represents the persister-specific cross-reference cache.
 	 */
 	private static class NaturalIdResolutionCache implements Serializable {
 		private final EntityPersister persister;
 		private final Type[] naturalIdTypes;
 
 		private Map<Serializable, CachedNaturalId> pkToNaturalIdMap = new ConcurrentHashMap<Serializable, CachedNaturalId>();
 		private Map<CachedNaturalId, Serializable> naturalIdToPkMap = new ConcurrentHashMap<CachedNaturalId, Serializable>();
 
 		private List<CachedNaturalId> invalidNaturalIdList;
 
 		private NaturalIdResolutionCache(EntityPersister persister) {
 			this.persister = persister;
 
 			final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 			naturalIdTypes = new Type[ naturalIdPropertyIndexes.length ];
 			int i = 0;
 			for ( int naturalIdPropertyIndex : naturalIdPropertyIndexes ) {
 				naturalIdTypes[i++] = persister.getPropertyType( persister.getPropertyNames()[ naturalIdPropertyIndex ] );
 			}
 		}
 
 		public EntityPersister getPersister() {
 			return persister;
 		}
 
 		public boolean sameAsCached(Serializable pk, Object[] naturalIdValues) {
 			if ( pk == null ) {
 				return false;
 			}
 			final CachedNaturalId initial = pkToNaturalIdMap.get( pk );
 			if ( initial != null ) {
 				if ( initial.isSame( naturalIdValues ) ) {
 					return true;
 				}
 			}
 			return false;
 		}
 
 		public boolean cache(Serializable pk, Object[] naturalIdValues) {
 			if ( pk == null ) {
 				return false;
 			}
 			final CachedNaturalId initial = pkToNaturalIdMap.get( pk );
 			if ( initial != null ) {
 				if ( initial.isSame( naturalIdValues ) ) {
 					return false;
 				}
 				naturalIdToPkMap.remove( initial );
 			}
 
 			final CachedNaturalId cachedNaturalId = new CachedNaturalId( persister, naturalIdValues );
 			pkToNaturalIdMap.put( pk, cachedNaturalId );
 			naturalIdToPkMap.put( cachedNaturalId, pk );
 			
 			return true;
 		}
 
 		public void stashInvalidNaturalIdReference(Object[] invalidNaturalIdValues) {
 			if ( invalidNaturalIdList == null ) {
 				invalidNaturalIdList = new ArrayList<CachedNaturalId>();
 			}
 			invalidNaturalIdList.add( new CachedNaturalId( persister, invalidNaturalIdValues ) );
 		}
 
 		public boolean containsInvalidNaturalIdReference(Object[] naturalIdValues) {
 			return invalidNaturalIdList != null
 					&& invalidNaturalIdList.contains( new CachedNaturalId( persister, naturalIdValues ) );
 		}
 
 		public void unStashInvalidNaturalIdReferences() {
 			if ( invalidNaturalIdList != null ) {
 				invalidNaturalIdList.clear();
 			}
 		}
 	}
 
 	/**
 	 * Clear the resolution cache
 	 */
 	public void clear() {
 		naturalIdResolutionCacheMap.clear();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
index d7ebff1261..5c10005a1b 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
@@ -1,1351 +1,1351 @@
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
 package org.hibernate.engine.internal;
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Map.Entry;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
 import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.loading.internal.LoadContexts;
 import org.hibernate.engine.spi.AssociationKey;
 import org.hibernate.engine.spi.BatchFetchQueue;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.ConcurrentReferenceHashMap;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.tuple.ElementWrapper;
 import org.hibernate.type.CollectionType;
 
 /**
  * A <strong>stateful</strong> implementation of the {@link PersistenceContext} contract meaning that we maintain this
  * state throughout the life of the persistence context.
  * <p/>
  * IMPL NOTE: There is meant to be a one-to-one correspondence between a {@link org.hibernate.internal.SessionImpl}
  * and a PersistentContext.  Event listeners and other Session collaborators then use the PersistentContext to drive
  * their processing.
  *
  * @author Steve Ebersole
  */
 public class StatefulPersistenceContext implements PersistenceContext {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			StatefulPersistenceContext.class.getName()
 	);
 
 	private static final boolean TRACE_ENABLED = LOG.isTraceEnabled();
 	private static final int INIT_COLL_SIZE = 8;
 
 	private SessionImplementor session;
 
 	// Loaded entity instances, by EntityKey
 	private Map<EntityKey, Object> entitiesByKey;
 
 	// Loaded entity instances, by EntityUniqueKey
 	private Map<EntityUniqueKey, Object> entitiesByUniqueKey;
 
 	private EntityEntryContext entityEntryContext;
 //	private Map<Object,EntityEntry> entityEntries;
 
 	// Entity proxies, by EntityKey
 	private Map<EntityKey, Object> proxiesByKey;
 
 	// Snapshots of current database state for entities
 	// that have *not* been loaded
 	private Map<EntityKey, Object> entitySnapshotsByKey;
 
 	// Identity map of array holder ArrayHolder instances, by the array instance
 	private Map<Object, PersistentCollection> arrayHolders;
 
 	// Identity map of CollectionEntry instances, by the collection wrapper
 	private IdentityMap<PersistentCollection, CollectionEntry> collectionEntries;
 
 	// Collection wrappers, by the CollectionKey
 	private Map<CollectionKey, PersistentCollection> collectionsByKey;
 
 	// Set of EntityKeys of deleted objects
 	private HashSet<EntityKey> nullifiableEntityKeys;
 
 	// properties that we have tried to load, and not found in the database
 	private HashSet<AssociationKey> nullAssociations;
 
 	// A list of collection wrappers that were instantiating during result set
 	// processing, that we will need to initialize at the end of the query
 	private List<PersistentCollection> nonlazyCollections;
 
 	// A container for collections we load up when the owning entity is not
 	// yet loaded ... for now, this is purely transient!
 	private Map<CollectionKey,PersistentCollection> unownedCollections;
 
 	// Parent entities cache by their child for cascading
 	// May be empty or not contains all relation
 	private Map<Object,Object> parentsByChild;
 
 	private int cascading;
 	private int loadCounter;
 	private boolean flushing;
 
 	private boolean defaultReadOnly;
 	private boolean hasNonReadOnlyEntities;
 
 	private LoadContexts loadContexts;
 	private BatchFetchQueue batchFetchQueue;
 
 
 	/**
 	 * Constructs a PersistentContext, bound to the given session.
 	 *
 	 * @param session The session "owning" this context.
 	 */
 	public StatefulPersistenceContext(SessionImplementor session) {
 		this.session = session;
 
 		entitiesByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
 		entitiesByUniqueKey = new HashMap<EntityUniqueKey, Object>( INIT_COLL_SIZE );
 		//noinspection unchecked
 		proxiesByKey = new ConcurrentReferenceHashMap<EntityKey, Object>( INIT_COLL_SIZE, .75f, 1, ConcurrentReferenceHashMap.ReferenceType.STRONG, ConcurrentReferenceHashMap.ReferenceType.WEAK, null );
 		entitySnapshotsByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
 
 		entityEntryContext = new EntityEntryContext();
 //		entityEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		collectionEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		parentsByChild = new IdentityHashMap<Object,Object>( INIT_COLL_SIZE );
 
 		collectionsByKey = new HashMap<CollectionKey, PersistentCollection>( INIT_COLL_SIZE );
 		arrayHolders = new IdentityHashMap<Object, PersistentCollection>( INIT_COLL_SIZE );
 
 		nullifiableEntityKeys = new HashSet<EntityKey>();
 
 		initTransientState();
 	}
 
 	private void initTransientState() {
 		nullAssociations = new HashSet<AssociationKey>( INIT_COLL_SIZE );
 		nonlazyCollections = new ArrayList<PersistentCollection>( INIT_COLL_SIZE );
 	}
 
 	@Override
 	public boolean isStateless() {
 		return false;
 	}
 
 	@Override
 	public SessionImplementor getSession() {
 		return session;
 	}
 
 	@Override
 	public LoadContexts getLoadContexts() {
 		if ( loadContexts == null ) {
 			loadContexts = new LoadContexts( this );
 		}
 		return loadContexts;
 	}
 
 	@Override
 	public void addUnownedCollection(CollectionKey key, PersistentCollection collection) {
 		if (unownedCollections==null) {
 			unownedCollections = new HashMap<CollectionKey,PersistentCollection>(INIT_COLL_SIZE);
 		}
 		unownedCollections.put( key, collection );
 	}
 
 	@Override
 	public PersistentCollection useUnownedCollection(CollectionKey key) {
 		return ( unownedCollections == null ) ? null : unownedCollections.remove( key );
 	}
 
 	@Override
 	public BatchFetchQueue getBatchFetchQueue() {
 		if (batchFetchQueue==null) {
 			batchFetchQueue = new BatchFetchQueue(this);
 		}
 		return batchFetchQueue;
 	}
 
 	@Override
 	public void clear() {
 		for ( Object o : proxiesByKey.values() ) {
 			if ( o == null ) {
 				//entry may be GCd
 				continue;
 			}
 			((HibernateProxy) o).getHibernateLazyInitializer().unsetSession();
 		}
 		for ( Map.Entry<PersistentCollection, CollectionEntry> aCollectionEntryArray : IdentityMap.concurrentEntries( collectionEntries ) ) {
 			aCollectionEntryArray.getKey().unsetSession( getSession() );
 		}
 		arrayHolders.clear();
 		entitiesByKey.clear();
 		entitiesByUniqueKey.clear();
 		entityEntryContext.clear();
 //		entityEntries.clear();
 		parentsByChild.clear();
 		entitySnapshotsByKey.clear();
 		collectionsByKey.clear();
 		collectionEntries.clear();
 		if ( unownedCollections != null ) {
 			unownedCollections.clear();
 		}
 		proxiesByKey.clear();
 		nullifiableEntityKeys.clear();
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.clear();
 		}
 		// defaultReadOnly is unaffected by clear()
 		hasNonReadOnlyEntities = false;
 		if ( loadContexts != null ) {
 			loadContexts.cleanup();
 		}
 		naturalIdXrefDelegate.clear();
 	}
 
 	@Override
 	public boolean isDefaultReadOnly() {
 		return defaultReadOnly;
 	}
 
 	@Override
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		this.defaultReadOnly = defaultReadOnly;
 	}
 
 	@Override
 	public boolean hasNonReadOnlyEntities() {
 		return hasNonReadOnlyEntities;
 	}
 
 	@Override
 	public void setEntryStatus(EntityEntry entry, Status status) {
 		entry.setStatus( status );
 		setHasNonReadOnlyEnties( status );
 	}
 
 	private void setHasNonReadOnlyEnties(Status status) {
 		if ( status==Status.DELETED || status==Status.MANAGED || status==Status.SAVING ) {
 			hasNonReadOnlyEntities = true;
 		}
 	}
 
 	@Override
 	public void afterTransactionCompletion() {
 		cleanUpInsertedKeysAfterTransaction();
 		entityEntryContext.downgradeLocks();
 //		// Downgrade locks
 //		for ( EntityEntry o : entityEntries.values() ) {
 //			o.setLockMode( LockMode.NONE );
 //		}
 	}
 
 	/**
 	 * Get the current state of the entity as known to the underlying
 	 * database, or null if there is no corresponding row
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister) throws HibernateException {
 		final EntityKey key = session.generateEntityKey( id, persister );
 		final Object cached = entitySnapshotsByKey.get( key );
 		if ( cached != null ) {
 			return cached == NO_ROW ? null : (Object[]) cached;
 		}
 		else {
 			final Object[] snapshot = persister.getDatabaseSnapshot( id, session );
 			entitySnapshotsByKey.put( key, snapshot == null ? NO_ROW : snapshot );
 			return snapshot;
 		}
 	}
 
 	@Override
 	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister) throws HibernateException {
 		if ( !persister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		persister = locateProperPersister( persister );
 
 		// let's first see if it is part of the natural id cache...
 		final Object[] cachedValue = naturalIdHelper.findCachedNaturalId( persister, id );
 		if ( cachedValue != null ) {
 			return cachedValue;
 		}
 
 		// check to see if the natural id is mutable/immutable
 		if ( persister.getEntityMetamodel().hasImmutableNaturalId() ) {
 			// an immutable natural-id is not retrieved during a normal database-snapshot operation...
 			final Object[] dbValue = persister.getNaturalIdentifierSnapshot( id, session );
 			naturalIdHelper.cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					id,
 					dbValue
 			);
 			return dbValue;
 		}
 		else {
 			// for a mutable natural there is a likelihood that the the information will already be
 			// snapshot-cached.
 			final int[] props = persister.getNaturalIdentifierProperties();
 			final Object[] entitySnapshot = getDatabaseSnapshot( id, persister );
-			if ( entitySnapshot == NO_ROW ) {
+			if ( entitySnapshot == NO_ROW || entitySnapshot == null ) {
 				return null;
 			}
 
 			final Object[] naturalIdSnapshotSubSet = new Object[ props.length ];
 			for ( int i = 0; i < props.length; i++ ) {
 				naturalIdSnapshotSubSet[i] = entitySnapshot[ props[i] ];
 			}
 			naturalIdHelper.cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					id,
 					naturalIdSnapshotSubSet
 			);
 			return naturalIdSnapshotSubSet;
 		}
 	}
 
 	private EntityPersister locateProperPersister(EntityPersister persister) {
 		return session.getFactory().getEntityPersister( persister.getRootEntityName() );
 	}
 
 	@Override
 	public Object[] getCachedDatabaseSnapshot(EntityKey key) {
 		final Object snapshot = entitySnapshotsByKey.get( key );
 		if ( snapshot == NO_ROW ) {
 			throw new IllegalStateException(
 					"persistence context reported no row snapshot for "
 							+ MessageHelper.infoString( key.getEntityName(), key.getIdentifier() )
 			);
 		}
 		return (Object[]) snapshot;
 	}
 
 	@Override
 	public void addEntity(EntityKey key, Object entity) {
 		entitiesByKey.put( key, entity );
 		getBatchFetchQueue().removeBatchLoadableEntityKey( key );
 	}
 
 	@Override
 	public Object getEntity(EntityKey key) {
 		return entitiesByKey.get( key );
 	}
 
 	@Override
 	public boolean containsEntity(EntityKey key) {
 		return entitiesByKey.containsKey( key );
 	}
 
 	@Override
 	public Object removeEntity(EntityKey key) {
 		final Object entity = entitiesByKey.remove( key );
 		final Iterator itr = entitiesByUniqueKey.values().iterator();
 		while ( itr.hasNext() ) {
 			if ( itr.next() == entity ) {
 				itr.remove();
 			}
 		}
 		// Clear all parent cache
 		parentsByChild.clear();
 		entitySnapshotsByKey.remove( key );
 		nullifiableEntityKeys.remove( key );
 		getBatchFetchQueue().removeBatchLoadableEntityKey( key );
 		getBatchFetchQueue().removeSubselect( key );
 		return entity;
 	}
 
 	@Override
 	public Object getEntity(EntityUniqueKey euk) {
 		return entitiesByUniqueKey.get( euk );
 	}
 
 	@Override
 	public void addEntity(EntityUniqueKey euk, Object entity) {
 		entitiesByUniqueKey.put( euk, entity );
 	}
 
 	@Override
 	public EntityEntry getEntry(Object entity) {
 		return entityEntryContext.getEntityEntry( entity );
 	}
 
 	@Override
 	public EntityEntry removeEntry(Object entity) {
 		return entityEntryContext.removeEntityEntry( entity );
 	}
 
 	@Override
 	public boolean isEntryFor(Object entity) {
 		return entityEntryContext.hasEntityEntry( entity );
 	}
 
 	@Override
 	public CollectionEntry getCollectionEntry(PersistentCollection coll) {
 		return collectionEntries.get( coll );
 	}
 
 	@Override
 	public EntityEntry addEntity(
 			final Object entity,
 			final Status status,
 			final Object[] loadedState,
 			final EntityKey entityKey,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			boolean lazyPropertiesAreUnfetched) {
 		addEntity( entityKey, entity );
 		return addEntry(
 				entity,
 				status,
 				loadedState,
 				null,
 				entityKey.getIdentifier(),
 				version,
 				lockMode,
 				existsInDatabase,
 				persister,
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched
 		);
 	}
 
 	@Override
 	public EntityEntry addEntry(
 			final Object entity,
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			boolean lazyPropertiesAreUnfetched) {
 
 		final EntityEntry e = new EntityEntry(
 				status,
 				loadedState,
 				rowId,
 				id,
 				version,
 				lockMode,
 				existsInDatabase,
 				persister,
 				persister.getEntityMode(),
 				session.getTenantIdentifier(),
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched,
 				this
 		);
 
 		entityEntryContext.addEntityEntry( entity, e );
 //		entityEntries.put(entity, e);
 
 		setHasNonReadOnlyEnties( status );
 		return e;
 	}
 
 	@Override
 	public boolean containsCollection(PersistentCollection collection) {
 		return collectionEntries.containsKey( collection );
 	}
 
 	@Override
 	public boolean containsProxy(Object entity) {
 		return proxiesByKey.containsValue( entity );
 	}
 
 	@Override
 	public boolean reassociateIfUninitializedProxy(Object value) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
 		if ( !Hibernate.isInitialized( value ) ) {
 			final HibernateProxy proxy = (HibernateProxy) value;
 			final LazyInitializer li = proxy.getHibernateLazyInitializer();
 			reassociateProxy( li, proxy );
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	@Override
 	public void reassociateProxy(Object value, Serializable id) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
 		if ( value instanceof HibernateProxy ) {
 			LOG.debugf( "Setting proxy identifier: %s", id );
 			final HibernateProxy proxy = (HibernateProxy) value;
 			final LazyInitializer li = proxy.getHibernateLazyInitializer();
 			li.setIdentifier( id );
 			reassociateProxy( li, proxy );
 		}
 	}
 
 	/**
 	 * Associate a proxy that was instantiated by another session with this session
 	 *
 	 * @param li The proxy initializer.
 	 * @param proxy The proxy to reassociate.
 	 */
 	private void reassociateProxy(LazyInitializer li, HibernateProxy proxy) {
 		if ( li.getSession() != this.getSession() ) {
 			final EntityPersister persister = session.getFactory().getEntityPersister( li.getEntityName() );
 			final EntityKey key = session.generateEntityKey( li.getIdentifier(), persister );
 		  	// any earlier proxy takes precedence
 			if ( !proxiesByKey.containsKey( key ) ) {
 				proxiesByKey.put( key, proxy );
 			}
 			proxy.getHibernateLazyInitializer().setSession( session );
 		}
 	}
 
 	@Override
 	public Object unproxy(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof ElementWrapper ) {
 			maybeProxy = ( (ElementWrapper) maybeProxy ).getElement();
 		}
 
 		if ( maybeProxy instanceof HibernateProxy ) {
 			final HibernateProxy proxy = (HibernateProxy) maybeProxy;
 			final LazyInitializer li = proxy.getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				throw new PersistentObjectException(
 						"object was an uninitialized proxy for " + li.getEntityName()
 				);
 			}
 			//unwrap the object and return
 			return li.getImplementation();
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
 	@Override
 	public Object unproxyAndReassociate(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof ElementWrapper ) {
 			maybeProxy = ( (ElementWrapper) maybeProxy ).getElement();
 		}
 
 		if ( maybeProxy instanceof HibernateProxy ) {
 			final HibernateProxy proxy = (HibernateProxy) maybeProxy;
 			final LazyInitializer li = proxy.getHibernateLazyInitializer();
 			reassociateProxy( li, proxy );
 			//initialize + unwrap the object and return it
 			return li.getImplementation();
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
 	@Override
 	public void checkUniqueness(EntityKey key, Object object) throws HibernateException {
 		final Object entity = getEntity( key );
 		if ( entity == object ) {
 			throw new AssertionFailure( "object already associated, but no entry was found" );
 		}
 		if ( entity != null ) {
 			throw new NonUniqueObjectException( key.getIdentifier(), key.getEntityName() );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object)
 			throws HibernateException {
 
 		final Class concreteProxyClass = persister.getConcreteProxyClass();
 		final boolean alreadyNarrow = concreteProxyClass.isAssignableFrom( proxy.getClass() );
 
 		if ( !alreadyNarrow ) {
 			LOG.narrowingProxy( concreteProxyClass );
 
 			if ( object != null ) {
 				proxiesByKey.remove( key );
 				//return the proxied object
 				return object;
 			}
 			else {
 				proxy = persister.createProxy( key.getIdentifier(), session );
 				//overwrite old proxy
 				final Object proxyOrig = proxiesByKey.put( key, proxy );
 				if ( proxyOrig != null ) {
 					if ( ! ( proxyOrig instanceof HibernateProxy ) ) {
 						throw new AssertionFailure(
 								"proxy not of type HibernateProxy; it is " + proxyOrig.getClass()
 						);
 					}
 					// set the read-only/modifiable mode in the new proxy to what it was in the original proxy
 					final boolean readOnlyOrig = ( (HibernateProxy) proxyOrig ).getHibernateLazyInitializer().isReadOnly();
 					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setReadOnly( readOnlyOrig );
 				}
 				return proxy;
 			}
 		}
 		else {
 
 			if ( object != null ) {
 				final LazyInitializer li = ( (HibernateProxy) proxy ).getHibernateLazyInitializer();
 				li.setImplementation( object );
 			}
 			return proxy;
 		}
 	}
 
 	@Override
 	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl) throws HibernateException {
 		if ( !persister.hasProxy() ) {
 			return impl;
 		}
 		final Object proxy = proxiesByKey.get( key );
 		return ( proxy != null ) ? narrowProxy( proxy, persister, key, impl ) : impl;
 	}
 
 	@Override
 	public Object proxyFor(Object impl) throws HibernateException {
 		final EntityEntry e = getEntry( impl );
 		return proxyFor( e.getPersister(), e.getEntityKey(), impl );
 	}
 
 	@Override
 	public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister) throws MappingException {
 		// todo : we really just need to add a split in the notions of:
 		//		1) collection key
 		//		2) collection owner key
 		// these 2 are not always the same.  Same is true in the case of ToOne associations with property-ref...
 		final EntityPersister ownerPersister = collectionPersister.getOwnerEntityPersister();
 		if ( ownerPersister.getIdentifierType().getReturnedClass().isInstance( key ) ) {
 			return getEntity( session.generateEntityKey( key, collectionPersister.getOwnerEntityPersister() ) );
 		}
 
 		// we have a property-ref type mapping for the collection key.  But that could show up a few ways here...
 		//
 		//		1) The incoming key could be the entity itself...
 		if ( ownerPersister.isInstance( key ) ) {
 			final Serializable owenerId = ownerPersister.getIdentifier( key, session );
 			if ( owenerId == null ) {
 				return null;
 			}
 			return getEntity( session.generateEntityKey( owenerId, ownerPersister ) );
 		}
 
 		final CollectionType collectionType = collectionPersister.getCollectionType();
 
 		//		2) The incoming key is most likely the collection key which we need to resolve to the owner key
 		//			find the corresponding owner instance
 		//			a) try by EntityUniqueKey
 		if ( collectionType.getLHSPropertyName() != null ) {
 			final Object owner = getEntity(
 					new EntityUniqueKey(
 							ownerPersister.getEntityName(),
 							collectionType.getLHSPropertyName(),
 							key,
 							collectionPersister.getKeyType(),
 							ownerPersister.getEntityMode(),
 							session.getFactory()
 					)
 			);
 			if ( owner != null ) {
 				return owner;
 			}
 
 			//		b) try by EntityKey, which means we need to resolve owner-key -> collection-key
 			//			IMPL NOTE : yes if we get here this impl is very non-performant, but PersistenceContext
 			//					was never designed to handle this case; adding that capability for real means splitting
 			//					the notions of:
 			//						1) collection key
 			//						2) collection owner key
 			// 					these 2 are not always the same (same is true in the case of ToOne associations with
 			// 					property-ref).  That would require changes to (at least) CollectionEntry and quite
 			//					probably changes to how the sql for collection initializers are generated
 			//
 			//			We could also possibly see if the referenced property is a natural id since we already have caching
 			//			in place of natural id snapshots.  BUt really its better to just do it the right way ^^ if we start
 			// 			going that route
 			final Serializable ownerId = ownerPersister.getIdByUniqueKey( key, collectionType.getLHSPropertyName(), session );
 			return getEntity( session.generateEntityKey( ownerId, ownerPersister ) );
 		}
 
 		// as a last resort this is what the old code did...
 		return getEntity( session.generateEntityKey( key, collectionPersister.getOwnerEntityPersister() ) );
 	}
 
 	@Override
 	public Object getLoadedCollectionOwnerOrNull(PersistentCollection collection) {
 		final CollectionEntry ce = getCollectionEntry( collection );
 		if ( ce.getLoadedPersister() == null ) {
 			return null;
 		}
 
 		Object loadedOwner = null;
 		// TODO: an alternative is to check if the owner has changed; if it hasn't then
 		// return collection.getOwner()
 		final Serializable entityId = getLoadedCollectionOwnerIdOrNull( ce );
 		if ( entityId != null ) {
 			loadedOwner = getCollectionOwner( entityId, ce.getLoadedPersister() );
 		}
 		return loadedOwner;
 	}
 
 	@Override
 	public Serializable getLoadedCollectionOwnerIdOrNull(PersistentCollection collection) {
 		return getLoadedCollectionOwnerIdOrNull( getCollectionEntry( collection ) );
 	}
 
 	/**
 	 * Get the ID for the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param ce The collection entry
 	 * @return the owner ID if available from the collection's loaded key; otherwise, returns null
 	 */
 	private Serializable getLoadedCollectionOwnerIdOrNull(CollectionEntry ce) {
 		if ( ce == null || ce.getLoadedKey() == null || ce.getLoadedPersister() == null ) {
 			return null;
 		}
 		// TODO: an alternative is to check if the owner has changed; if it hasn't then
 		// get the ID from collection.getOwner()
 		return ce.getLoadedPersister().getCollectionType().getIdOfOwnerOrNull( ce.getLoadedKey(), session );
 	}
 
 	@Override
 	public void addUninitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) {
 		final CollectionEntry ce = new CollectionEntry( collection, persister, id, flushing );
 		addCollection( collection, ce, id );
 		if ( persister.getBatchSize() > 1 ) {
 			getBatchFetchQueue().addBatchLoadableCollection( collection, ce );
 		}
 	}
 
 	@Override
 	public void addUninitializedDetachedCollection(CollectionPersister persister, PersistentCollection collection) {
 		final CollectionEntry ce = new CollectionEntry( persister, collection.getKey() );
 		addCollection( collection, ce, collection.getKey() );
 		if ( persister.getBatchSize() > 1 ) {
 			getBatchFetchQueue().addBatchLoadableCollection( collection, ce );
 		}
 	}
 
 	@Override
 	public void addNewCollection(CollectionPersister persister, PersistentCollection collection)
 			throws HibernateException {
 		addCollection( collection, persister );
 	}
 
 	/**
 	 * Add an collection to the cache, with a given collection entry.
 	 *
 	 * @param coll The collection for which we are adding an entry.
 	 * @param entry The entry representing the collection.
 	 * @param key The key of the collection's entry.
 	 */
 	private void addCollection(PersistentCollection coll, CollectionEntry entry, Serializable key) {
 		collectionEntries.put( coll, entry );
 		final CollectionKey collectionKey = new CollectionKey( entry.getLoadedPersister(), key );
 		final PersistentCollection old = collectionsByKey.put( collectionKey, coll );
 		if ( old != null ) {
 			if ( old == coll ) {
 				throw new AssertionFailure( "bug adding collection twice" );
 			}
 			// or should it actually throw an exception?
 			old.unsetSession( session );
 			collectionEntries.remove( old );
 			// watch out for a case where old is still referenced
 			// somewhere in the object graph! (which is a user error)
 		}
 	}
 
 	/**
 	 * Add a collection to the cache, creating a new collection entry for it
 	 *
 	 * @param collection The collection for which we are adding an entry.
 	 * @param persister The collection persister
 	 */
 	private void addCollection(PersistentCollection collection, CollectionPersister persister) {
 		final CollectionEntry ce = new CollectionEntry( persister, collection );
 		collectionEntries.put( collection, ce );
 	}
 
 	@Override
 	public void addInitializedDetachedCollection(CollectionPersister collectionPersister, PersistentCollection collection)
 			throws HibernateException {
 		if ( collection.isUnreferenced() ) {
 			//treat it just like a new collection
 			addCollection( collection, collectionPersister );
 		}
 		else {
 			final CollectionEntry ce = new CollectionEntry( collection, session.getFactory() );
 			addCollection( collection, ce, collection.getKey() );
 		}
 	}
 
 	@Override
 	public CollectionEntry addInitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id)
 			throws HibernateException {
 		final CollectionEntry ce = new CollectionEntry( collection, persister, id, flushing );
 		ce.postInitialize( collection );
 		addCollection( collection, ce, id );
 		return ce;
 	}
 
 	@Override
 	public PersistentCollection getCollection(CollectionKey collectionKey) {
 		return collectionsByKey.get( collectionKey );
 	}
 
 	@Override
 	public void addNonLazyCollection(PersistentCollection collection) {
 		nonlazyCollections.add( collection );
 	}
 
 	@Override
 	public void initializeNonLazyCollections() throws HibernateException {
 		if ( loadCounter == 0 ) {
 			if ( TRACE_ENABLED ) {
 				LOG.trace( "Initializing non-lazy collections" );
 			}
 
 			//do this work only at the very highest level of the load
 			//don't let this method be called recursively
 			loadCounter++;
 			try {
 				int size;
 				while ( ( size = nonlazyCollections.size() ) > 0 ) {
 					//note that each iteration of the loop may add new elements
 					nonlazyCollections.remove( size - 1 ).forceInitialization();
 				}
 			}
 			finally {
 				loadCounter--;
 				clearNullProperties();
 			}
 		}
 	}
 
 	@Override
 	public PersistentCollection getCollectionHolder(Object array) {
 		return arrayHolders.get( array );
 	}
 
 	@Override
 	public void addCollectionHolder(PersistentCollection holder) {
 		//TODO:refactor + make this method private
 		arrayHolders.put( holder.getValue(), holder );
 	}
 
 	@Override
 	public PersistentCollection removeCollectionHolder(Object array) {
 		return arrayHolders.remove( array );
 	}
 
 	@Override
 	public Serializable getSnapshot(PersistentCollection coll) {
 		return getCollectionEntry( coll ).getSnapshot();
 	}
 
 	@Override
 	public CollectionEntry getCollectionEntryOrNull(Object collection) {
 		PersistentCollection coll;
 		if ( collection instanceof PersistentCollection ) {
 			coll = (PersistentCollection) collection;
 			//if (collection==null) throw new TransientObjectException("Collection was not yet persistent");
 		}
 		else {
 			coll = getCollectionHolder( collection );
 			if ( coll == null ) {
 				//it might be an unwrapped collection reference!
 				//try to find a wrapper (slowish)
 				final Iterator<PersistentCollection> wrappers = collectionEntries.keyIterator();
 				while ( wrappers.hasNext() ) {
 					final PersistentCollection pc = wrappers.next();
 					if ( pc.isWrapper( collection ) ) {
 						coll = pc;
 						break;
 					}
 				}
 			}
 		}
 
 		return (coll == null) ? null : getCollectionEntry( coll );
 	}
 
 	@Override
 	public Object getProxy(EntityKey key) {
 		return proxiesByKey.get( key );
 	}
 
 	@Override
 	public void addProxy(EntityKey key, Object proxy) {
 		proxiesByKey.put( key, proxy );
 	}
 
 	@Override
 	public Object removeProxy(EntityKey key) {
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.removeBatchLoadableEntityKey( key );
 			batchFetchQueue.removeSubselect( key );
 		}
 		return proxiesByKey.remove( key );
 	}
 
 	@Override
 	public HashSet getNullifiableEntityKeys() {
 		return nullifiableEntityKeys;
 	}
 
 	@Override
 	public Map getEntitiesByKey() {
 		return entitiesByKey;
 	}
 
 	public Map getProxiesByKey() {
 		return proxiesByKey;
 	}
 
 	@Override
 	public int getNumberOfManagedEntities() {
 		return entityEntryContext.getNumberOfManagedEntities();
 	}
 
 	@Override
 	public Map getEntityEntries() {
 		return null;
 	}
 
 	@Override
 	public Map getCollectionEntries() {
 		return collectionEntries;
 	}
 
 	@Override
 	public Map getCollectionsByKey() {
 		return collectionsByKey;
 	}
 
 	@Override
 	public int getCascadeLevel() {
 		return cascading;
 	}
 
 	@Override
 	public int incrementCascadeLevel() {
 		return ++cascading;
 	}
 
 	@Override
 	public int decrementCascadeLevel() {
 		return --cascading;
 	}
 
 	@Override
 	public boolean isFlushing() {
 		return flushing;
 	}
 
 	@Override
 	public void setFlushing(boolean flushing) {
 		final boolean afterFlush = this.flushing && ! flushing;
 		this.flushing = flushing;
 		if ( afterFlush ) {
 			getNaturalIdHelper().cleanupFromSynchronizations();
 		}
 	}
 
 	/**
 	 * Call this before beginning a two-phase load
 	 */
 	@Override
 	public void beforeLoad() {
 		loadCounter++;
 	}
 
 	/**
 	 * Call this after finishing a two-phase load
 	 */
 	@Override
 	public void afterLoad() {
 		loadCounter--;
 	}
 
 	@Override
 	public boolean isLoadFinished() {
 		return loadCounter == 0;
 	}
 
 	@Override
 	public String toString() {
 		return "PersistenceContext[entityKeys=" + entitiesByKey.keySet()
 				+ ",collectionKeys=" + collectionsByKey.keySet() + "]";
 	}
 
 	@Override
 	public Entry<Object,EntityEntry>[] reentrantSafeEntityEntries() {
 		return entityEntryContext.reentrantSafeEntityEntries();
 	}
 
 	@Override
 	public Serializable getOwnerId(String entityName, String propertyName, Object childEntity, Map mergeMap) {
 		final String collectionRole = entityName + '.' + propertyName;
 		final EntityPersister persister = session.getFactory().getEntityPersister( entityName );
 		final CollectionPersister collectionPersister = session.getFactory().getCollectionPersister( collectionRole );
 
 	    // try cache lookup first
 		final Object parent = parentsByChild.get( childEntity );
 		if ( parent != null ) {
 			final EntityEntry entityEntry = entityEntryContext.getEntityEntry( parent );
 			//there maybe more than one parent, filter by type
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() )
 					&& isFoundInParent( propertyName, childEntity, persister, collectionPersister, parent ) ) {
 				return getEntry( parent ).getId();
 			}
 			else {
 				// remove wrong entry
 				parentsByChild.remove( childEntity );
 			}
 		}
 
 		//not found in case, proceed
 		// iterate all the entities currently associated with the persistence context.
 		for ( Entry<Object,EntityEntry> me : reentrantSafeEntityEntries() ) {
 			final EntityEntry entityEntry = me.getValue();
 			// does this entity entry pertain to the entity persister in which we are interested (owner)?
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() ) ) {
 				final Object entityEntryInstance = me.getKey();
 
 				//check if the managed object is the parent
 				boolean found = isFoundInParent(
 						propertyName,
 						childEntity,
 						persister,
 						collectionPersister,
 						entityEntryInstance
 				);
 
 				if ( !found && mergeMap != null ) {
 					//check if the detached object being merged is the parent
 					final Object unmergedInstance = mergeMap.get( entityEntryInstance );
 					final Object unmergedChild = mergeMap.get( childEntity );
 					if ( unmergedInstance != null && unmergedChild != null ) {
 						found = isFoundInParent(
 								propertyName,
 								unmergedChild,
 								persister,
 								collectionPersister,
 								unmergedInstance
 						);
 					}
 				}
 
 				if ( found ) {
 					return entityEntry.getId();
 				}
 
 			}
 		}
 
 		// if we get here, it is possible that we have a proxy 'in the way' of the merge map resolution...
 		// 		NOTE: decided to put this here rather than in the above loop as I was nervous about the performance
 		//		of the loop-in-loop especially considering this is far more likely the 'edge case'
 		if ( mergeMap != null ) {
 			for ( Object o : mergeMap.entrySet() ) {
 				final Entry mergeMapEntry = (Entry) o;
 				if ( mergeMapEntry.getKey() instanceof HibernateProxy ) {
 					final HibernateProxy proxy = (HibernateProxy) mergeMapEntry.getKey();
 					if ( persister.isSubclassEntityName( proxy.getHibernateLazyInitializer().getEntityName() ) ) {
 						boolean found = isFoundInParent(
 								propertyName,
 								childEntity,
 								persister,
 								collectionPersister,
 								mergeMap.get( proxy )
 						);
 						if ( !found ) {
 							found = isFoundInParent(
 									propertyName,
 									mergeMap.get( childEntity ),
 									persister,
 									collectionPersister,
 									mergeMap.get( proxy )
 							);
 						}
 						if ( found ) {
 							return proxy.getHibernateLazyInitializer().getIdentifier();
 						}
 					}
 				}
 			}
 		}
 
 		return null;
 	}
 
 	private boolean isFoundInParent(
 			String property,
 			Object childEntity,
 			EntityPersister persister,
 			CollectionPersister collectionPersister,
 			Object potentialParent) {
 		final Object collection = persister.getPropertyValue( potentialParent, property );
 		return collection != null
 				&& Hibernate.isInitialized( collection )
 				&& collectionPersister.getCollectionType().contains( collection, childEntity, session );
 	}
 
 	@Override
 	public Object getIndexInOwner(String entity, String property, Object childEntity, Map mergeMap) {
 		final EntityPersister persister = session.getFactory().getEntityPersister( entity );
 		final CollectionPersister cp = session.getFactory().getCollectionPersister( entity + '.' + property );
 
 	    // try cache lookup first
 		final Object parent = parentsByChild.get( childEntity );
 		if ( parent != null ) {
 			final EntityEntry entityEntry = entityEntryContext.getEntityEntry( parent );
 			//there maybe more than one parent, filter by type
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() ) ) {
 				Object index = getIndexInParent( property, childEntity, persister, cp, parent );
 
 				if (index==null && mergeMap!=null) {
 					final Object unMergedInstance = mergeMap.get( parent );
 					final Object unMergedChild = mergeMap.get( childEntity );
 					if ( unMergedInstance != null && unMergedChild != null ) {
 						index = getIndexInParent( property, unMergedChild, persister, cp, unMergedInstance );
 					}
 				}
 				if ( index != null ) {
 					return index;
 				}
 			}
 			else {
 				// remove wrong entry
 				parentsByChild.remove( childEntity );
 			}
 		}
 
 		//Not found in cache, proceed
 		for ( Entry<Object, EntityEntry> me : reentrantSafeEntityEntries() ) {
 			final EntityEntry ee = me.getValue();
 			if ( persister.isSubclassEntityName( ee.getEntityName() ) ) {
 				final Object instance = me.getKey();
 
 				Object index = getIndexInParent( property, childEntity, persister, cp, instance );
 				if ( index==null && mergeMap!=null ) {
 					final Object unMergedInstance = mergeMap.get( instance );
 					final Object unMergedChild = mergeMap.get( childEntity );
 					if ( unMergedInstance != null && unMergedChild!=null ) {
 						index = getIndexInParent( property, unMergedChild, persister, cp, unMergedInstance );
 					}
 				}
 
 				if ( index != null ) {
 					return index;
 				}
 			}
 		}
 		return null;
 	}
 
 	private Object getIndexInParent(
 			String property,
 			Object childEntity,
 			EntityPersister persister,
 			CollectionPersister collectionPersister,
 			Object potentialParent){
 		final Object collection = persister.getPropertyValue( potentialParent, property );
 		if ( collection != null && Hibernate.isInitialized( collection ) ) {
 			return collectionPersister.getCollectionType().indexOf( collection, childEntity );
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
 	public void addNullProperty(EntityKey ownerKey, String propertyName) {
 		nullAssociations.add( new AssociationKey( ownerKey, propertyName ) );
 	}
 
 	@Override
 	public boolean isPropertyNull(EntityKey ownerKey, String propertyName) {
 		return nullAssociations.contains( new AssociationKey( ownerKey, propertyName ) );
 	}
 
 	private void clearNullProperties() {
 		nullAssociations.clear();
 	}
 
 	@Override
 	public boolean isReadOnly(Object entityOrProxy) {
 		if ( entityOrProxy == null ) {
 			throw new AssertionFailure( "object must be non-null." );
 		}
 		boolean isReadOnly;
 		if ( entityOrProxy instanceof HibernateProxy ) {
 			isReadOnly = ( (HibernateProxy) entityOrProxy ).getHibernateLazyInitializer().isReadOnly();
 		}
 		else {
 			final EntityEntry ee =  getEntry( entityOrProxy );
 			if ( ee == null ) {
 				throw new TransientObjectException("Instance was not associated with this persistence context" );
 			}
 			isReadOnly = ee.isReadOnly();
 		}
 		return isReadOnly;
 	}
 
 	@Override
 	public void setReadOnly(Object object, boolean readOnly) {
 		if ( object == null ) {
 			throw new AssertionFailure( "object must be non-null." );
 		}
 		if ( isReadOnly( object ) == readOnly ) {
 			return;
 		}
 		if ( object instanceof HibernateProxy ) {
 			final HibernateProxy proxy = (HibernateProxy) object;
 			setProxyReadOnly( proxy, readOnly );
 			if ( Hibernate.isInitialized( proxy ) ) {
 				setEntityReadOnly(
 						proxy.getHibernateLazyInitializer().getImplementation(),
 						readOnly
 				);
 			}
 		}
 		else {
 			setEntityReadOnly( object, readOnly );
 			// PersistenceContext.proxyFor( entity ) returns entity if there is no proxy for that entity
 			// so need to check the return value to be sure it is really a proxy
 			final Object maybeProxy = getSession().getPersistenceContext().proxyFor( object );
 			if ( maybeProxy instanceof HibernateProxy ) {
 				setProxyReadOnly( (HibernateProxy) maybeProxy, readOnly );
 			}
 		}
 	}
 
 	private void setProxyReadOnly(HibernateProxy proxy, boolean readOnly) {
 		if ( proxy.getHibernateLazyInitializer().getSession() != getSession() ) {
 			throw new AssertionFailure(
 					"Attempt to set a proxy to read-only that is associated with a different session" );
 		}
 		proxy.getHibernateLazyInitializer().setReadOnly( readOnly );
 	}
 
 	private void setEntityReadOnly(Object entity, boolean readOnly) {
 		final EntityEntry entry = getEntry( entity );
 		if ( entry == null ) {
 			throw new TransientObjectException( "Instance was not associated with this persistence context" );
 		}
 		entry.setReadOnly( readOnly, entity );
 		hasNonReadOnlyEntities = hasNonReadOnlyEntities || ! readOnly;
 	}
 
 	@Override
 	public void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId) {
 		final Object entity = entitiesByKey.remove( oldKey );
 		final EntityEntry oldEntry = entityEntryContext.removeEntityEntry( entity );
 		parentsByChild.clear();
 
 		final EntityKey newKey = session.generateEntityKey( generatedId, oldEntry.getPersister() );
 		addEntity( newKey, entity );
 		addEntry(
 				entity,
 				oldEntry.getStatus(),
 				oldEntry.getLoadedState(),
 				oldEntry.getRowId(),
 				generatedId,
 				oldEntry.getVersion(),
 				oldEntry.getLockMode(),
 				oldEntry.isExistsInDatabase(),
 				oldEntry.getPersister(),
 				oldEntry.isBeingReplicated(),
 				oldEntry.isLoadedWithLazyPropertiesUnfetched()
 		);
 	}
 
 	/**
 	 * Used by the owning session to explicitly control serialization of the
 	 * persistence context.
 	 *
 	 * @param oos The stream to which the persistence context should get written
 	 * @throws IOException serialization errors.
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		final boolean tracing = LOG.isTraceEnabled();
 		if ( tracing ) {
 			LOG.trace( "Serializing persistence-context" );
 		}
 
 		oos.writeBoolean( defaultReadOnly );
 		oos.writeBoolean( hasNonReadOnlyEntities );
 
 		oos.writeInt( entitiesByKey.size() );
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
index 1b2d710344..4dc5bbd544 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
@@ -1,576 +1,576 @@
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
 package org.hibernate.engine.jdbc.internal;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.sql.Connection;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.TransactionException;
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
 import org.hibernate.engine.jdbc.batch.spi.BatchKey;
 import org.hibernate.engine.jdbc.spi.InvalidatableWrapper;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.JdbcWrapper;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.jdbc.spi.ResultSetReturn;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.StatementPreparer;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.jboss.logging.Logger;
 import org.jboss.logging.Logger.Level;
 
 /**
  * Standard Hibernate implementation of {@link JdbcCoordinator}
  * <p/>
  * IMPL NOTE : Custom serialization handling!
  *
  * @author Steve Ebersole
  * @author Brett Meyer
  */
 public class JdbcCoordinatorImpl implements JdbcCoordinator {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			JdbcCoordinatorImpl.class.getName()
 	);
 
 	private transient TransactionCoordinator transactionCoordinator;
 	private final transient LogicalConnectionImpl logicalConnection;
 
 	private transient Batch currentBatch;
 
 	private transient long transactionTimeOutInstant = -1;
 
 	private final HashMap<Statement,Set<ResultSet>> xref = new HashMap<Statement,Set<ResultSet>>();
 	private final Set<ResultSet> unassociatedResultSets = new HashSet<ResultSet>();
-	private final SqlExceptionHelper exceptionHelper;
+	private final transient SqlExceptionHelper exceptionHelper;
 
 	private Statement lastQuery;
 
 	/**
 	 * If true, manually (and temporarily) circumvent aggressive release processing.
 	 */
 	private boolean releasesEnabled = true;
 
 	/**
 	 * Constructs a JdbcCoordinatorImpl
 	 *
 	 * @param userSuppliedConnection The user supplied connection (may be null)
 	 * @param transactionCoordinator The transaction coordinator
 	 */
 	public JdbcCoordinatorImpl(
 			Connection userSuppliedConnection,
 			TransactionCoordinator transactionCoordinator) {
 		this.transactionCoordinator = transactionCoordinator;
 		this.logicalConnection = new LogicalConnectionImpl(
 				userSuppliedConnection,
 				transactionCoordinator.getTransactionContext().getConnectionReleaseMode(),
 				transactionCoordinator.getTransactionContext().getTransactionEnvironment().getJdbcServices(),
 				transactionCoordinator.getTransactionContext().getJdbcConnectionAccess()
 		);
 		this.exceptionHelper = logicalConnection.getJdbcServices().getSqlExceptionHelper();
 	}
 
 	/**
 	 * Constructs a JdbcCoordinatorImpl
 	 *
 	 * @param logicalConnection The logical JDBC connection
 	 * @param transactionCoordinator The transaction coordinator
 	 */
 	public JdbcCoordinatorImpl(
 			LogicalConnectionImpl logicalConnection,
 			TransactionCoordinator transactionCoordinator) {
 		this.transactionCoordinator = transactionCoordinator;
 		this.logicalConnection = logicalConnection;
 		this.exceptionHelper = logicalConnection.getJdbcServices().getSqlExceptionHelper();
 	}
 
 	private JdbcCoordinatorImpl(LogicalConnectionImpl logicalConnection) {
 		this.logicalConnection = logicalConnection;
 		this.exceptionHelper = logicalConnection.getJdbcServices().getSqlExceptionHelper();
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		return transactionCoordinator;
 	}
 
 	@Override
 	public LogicalConnectionImplementor getLogicalConnection() {
 		return logicalConnection;
 	}
 
 	protected TransactionEnvironment transactionEnvironment() {
 		return getTransactionCoordinator().getTransactionContext().getTransactionEnvironment();
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return transactionEnvironment().getSessionFactory();
 	}
 
 	protected BatchBuilder batchBuilder() {
 		return sessionFactory().getServiceRegistry().getService( BatchBuilder.class );
 	}
 
 	/**
 	 * Access to the SqlExceptionHelper
 	 *
 	 * @return The SqlExceptionHelper
 	 */
 	public SqlExceptionHelper sqlExceptionHelper() {
 		return transactionEnvironment().getJdbcServices().getSqlExceptionHelper();
 	}
 
 
 	private int flushDepth;
 
 	@Override
 	public void flushBeginning() {
 		if ( flushDepth == 0 ) {
 			releasesEnabled = false;
 		}
 		flushDepth++;
 	}
 
 	@Override
 	public void flushEnding() {
 		flushDepth--;
 		if ( flushDepth < 0 ) {
 			throw new HibernateException( "Mismatched flush handling" );
 		}
 		if ( flushDepth == 0 ) {
 			releasesEnabled = true;
 		}
 		
 		afterStatementExecution();
 	}
 
 	@Override
 	public Connection close() {
 		LOG.tracev( "Closing JDBC container [{0}]", this );
 		if ( currentBatch != null ) {
 			LOG.closingUnreleasedBatch();
 			currentBatch.release();
 		}
 		cleanup();
 		return logicalConnection.close();
 	}
 
 	@Override
 	public Batch getBatch(BatchKey key) {
 		if ( currentBatch != null ) {
 			if ( currentBatch.getKey().equals( key ) ) {
 				return currentBatch;
 			}
 			else {
 				currentBatch.execute();
 				currentBatch.release();
 			}
 		}
 		currentBatch = batchBuilder().buildBatch( key, this );
 		return currentBatch;
 	}
 
 	@Override
 	public void executeBatch() {
 		if ( currentBatch != null ) {
 			currentBatch.execute();
 			// needed?
 			currentBatch.release();
 		}
 	}
 
 	@Override
 	public void abortBatch() {
 		if ( currentBatch != null ) {
 			currentBatch.release();
 		}
 	}
 
 	private transient StatementPreparer statementPreparer;
 
 	@Override
 	public StatementPreparer getStatementPreparer() {
 		if ( statementPreparer == null ) {
 			statementPreparer = new StatementPreparerImpl( this );
 		}
 		return statementPreparer;
 	}
 
 	private transient ResultSetReturn resultSetExtractor;
 
 	@Override
 	public ResultSetReturn getResultSetReturn() {
 		if ( resultSetExtractor == null ) {
 			resultSetExtractor = new ResultSetReturnImpl( this );
 		}
 		return resultSetExtractor;
 	}
 
 	@Override
 	public void setTransactionTimeOut(int seconds) {
 		transactionTimeOutInstant = System.currentTimeMillis() + ( seconds * 1000 );
 	}
 
 	@Override
 	public int determineRemainingTransactionTimeOutPeriod() {
 		if ( transactionTimeOutInstant < 0 ) {
 			return -1;
 		}
 		final int secondsRemaining = (int) ((transactionTimeOutInstant - System.currentTimeMillis()) / 1000);
 		if ( secondsRemaining <= 0 ) {
 			throw new TransactionException( "transaction timeout expired" );
 		}
 		return secondsRemaining;
 	}
 
 	@Override
 	public void afterStatementExecution() {
 		LOG.tracev( "Starting after statement execution processing [{0}]", connectionReleaseMode() );
 		if ( connectionReleaseMode() == ConnectionReleaseMode.AFTER_STATEMENT ) {
 			if ( ! releasesEnabled ) {
 				LOG.debug( "Skipping aggressive release due to manual disabling" );
 				return;
 			}
 			if ( hasRegisteredResources() ) {
 				LOG.debug( "Skipping aggressive release due to registered resources" );
 				return;
 			}
 			getLogicalConnection().releaseConnection();
 		}
 	}
 
 	@Override
 	public void afterTransaction() {
 		transactionTimeOutInstant = -1;
 		if ( connectionReleaseMode() == ConnectionReleaseMode.AFTER_STATEMENT ||
 				connectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION ) {
 			if ( hasRegisteredResources() ) {
 				LOG.forcingContainerResourceCleanup();
 				releaseResources();
 			}
 			getLogicalConnection().aggressiveRelease();
 		}
 	}
 	
 	private ConnectionReleaseMode connectionReleaseMode() {
 		return getLogicalConnection().getConnectionReleaseMode();
 	}
 
 	@Override
 	public <T> T coordinateWork(WorkExecutorVisitable<T> work) {
 		final Connection connection = getLogicalConnection().getConnection();
 		try {
 			final T result = work.accept( new WorkExecutor<T>(), connection );
 			afterStatementExecution();
 			return result;
 		}
 		catch ( SQLException e ) {
 			throw sqlExceptionHelper().convert( e, "error executing work" );
 		}
 	}
 
 	@Override
 	public boolean isReadyForSerialization() {
 		return getLogicalConnection().isUserSuppliedConnection()
 				? ! getLogicalConnection().isPhysicallyConnected()
 				: ! hasRegisteredResources();
 	}
 
 	/**
 	 * JDK serialization hook
 	 *
 	 * @param oos The stream into which to write our state
 	 *
 	 * @throws IOException Trouble accessing the stream
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		if ( ! isReadyForSerialization() ) {
 			throw new HibernateException( "Cannot serialize Session while connected" );
 		}
 		logicalConnection.serialize( oos );
 	}
 
 	/**
 	 * JDK deserialization hook
 	 *
 	 * @param ois The stream into which to write our state
 	 * @param transactionContext The transaction context which owns the JdbcCoordinatorImpl to be deserialized.
 	 *
 	 * @return The deserialized JdbcCoordinatorImpl
 	 *
 	 * @throws IOException Trouble accessing the stream
 	 * @throws ClassNotFoundException Trouble reading the stream
 	 */
 	public static JdbcCoordinatorImpl deserialize(
 			ObjectInputStream ois,
 			TransactionContext transactionContext) throws IOException, ClassNotFoundException {
 		return new JdbcCoordinatorImpl( LogicalConnectionImpl.deserialize( ois, transactionContext ) );
 	}
 
 	/**
 	 * Callback after deserialization from Session is done
 	 *
 	 * @param transactionCoordinator The transaction coordinator
 	 */
 	public void afterDeserialize(TransactionCoordinatorImpl transactionCoordinator) {
 		this.transactionCoordinator = transactionCoordinator;
 	}
 
 	@Override
 	public void register(Statement statement) {
 		LOG.tracev( "Registering statement [{0}]", statement );
 		if ( xref.containsKey( statement ) ) {
 			throw new HibernateException( "statement already registered with JDBCContainer" );
 		}
 		xref.put( statement, null );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public void registerLastQuery(Statement statement) {
 		LOG.tracev( "Registering last query statement [{0}]", statement );
 		if ( statement instanceof JdbcWrapper ) {
 			final JdbcWrapper<Statement> wrapper = (JdbcWrapper<Statement>) statement;
 			registerLastQuery( wrapper.getWrappedObject() );
 			return;
 		}
 		lastQuery = statement;
 	}
 
 	@Override
 	public void cancelLastQuery() {
 		try {
 			if (lastQuery != null) {
 				lastQuery.cancel();
 			}
 		}
 		catch (SQLException sqle) {
 			throw exceptionHelper.convert( sqle, "Cannot cancel query" );
 		}
 		finally {
 			lastQuery = null;
 		}
 	}
 
 	@Override
 	public void release(Statement statement) {
 		LOG.tracev( "Releasing statement [{0}]", statement );
 		final Set<ResultSet> resultSets = xref.get( statement );
 		if ( resultSets != null ) {
 			for ( ResultSet resultSet : resultSets ) {
 				close( resultSet );
 			}
 			resultSets.clear();
 		}
 		xref.remove( statement );
 		close( statement );
 		
 		afterStatementExecution();
 	}
 
 	@Override
 	public void register(ResultSet resultSet, Statement statement) {
 		LOG.tracev( "Registering result set [{0}]", resultSet );
 		if ( statement == null ) {
 			try {
 				statement = resultSet.getStatement();
 			}
 			catch ( SQLException e ) {
 				throw exceptionHelper.convert( e, "unable to access statement from resultset" );
 			}
 		}
 		if ( statement != null ) {
 			if ( LOG.isEnabled( Level.WARN ) && !xref.containsKey( statement ) ) {
 				LOG.unregisteredStatement();
 			}
 			Set<ResultSet> resultSets = xref.get( statement );
 			if ( resultSets == null ) {
 				resultSets = new HashSet<ResultSet>();
 				xref.put( statement, resultSets );
 			}
 			resultSets.add( resultSet );
 		}
 		else {
 			unassociatedResultSets.add( resultSet );
 		}
 	}
 
 	@Override
 	public void release(ResultSet resultSet, Statement statement) {
 		LOG.tracev( "Releasing result set [{0}]", resultSet );
 		if ( statement == null ) {
 			try {
 				statement = resultSet.getStatement();
 			}
 			catch ( SQLException e ) {
 				throw exceptionHelper.convert( e, "unable to access statement from resultset" );
 			}
 		}
 		if ( statement != null ) {
 			if ( LOG.isEnabled( Level.WARN ) && !xref.containsKey( statement ) ) {
 				LOG.unregisteredStatement();
 			}
 			final Set<ResultSet> resultSets = xref.get( statement );
 			if ( resultSets != null ) {
 				resultSets.remove( resultSet );
 				if ( resultSets.isEmpty() ) {
 					xref.remove( statement );
 				}
 			}
 		}
 		else {
 			final boolean removed = unassociatedResultSets.remove( resultSet );
 			if ( !removed ) {
 				LOG.unregisteredResultSetWithoutStatement();
 			}
 		}
 		close( resultSet );
 	}
 
 	@Override
 	public boolean hasRegisteredResources() {
 		return ! xref.isEmpty() || ! unassociatedResultSets.isEmpty();
 	}
 
 	@Override
 	public void releaseResources() {
 		LOG.tracev( "Releasing JDBC container resources [{0}]", this );
 		cleanup();
 	}
 	
 	@Override
 	public void enableReleases() {
 		releasesEnabled = true;
 	}
 	
 	@Override
 	public void disableReleases() {
 		releasesEnabled = false;
 	}
 
 	private void cleanup() {
 		for ( Map.Entry<Statement,Set<ResultSet>> entry : xref.entrySet() ) {
 			if ( entry.getValue() != null ) {
 				closeAll( entry.getValue() );
 			}
 			close( entry.getKey() );
 		}
 		xref.clear();
 
 		closeAll( unassociatedResultSets );
 	}
 
 	protected void closeAll(Set<ResultSet> resultSets) {
 		for ( ResultSet resultSet : resultSets ) {
 			close( resultSet );
 		}
 		resultSets.clear();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected void close(Statement statement) {
 		LOG.tracev( "Closing prepared statement [{0}]", statement );
 
 		if ( statement instanceof InvalidatableWrapper ) {
 			final InvalidatableWrapper<Statement> wrapper = (InvalidatableWrapper<Statement>) statement;
 			close( wrapper.getWrappedObject() );
 			wrapper.invalidate();
 			return;
 		}
 
 		try {
 			// if we are unable to "clean" the prepared statement,
 			// we do not close it
 			try {
 				if ( statement.getMaxRows() != 0 ) {
 					statement.setMaxRows( 0 );
 				}
 				if ( statement.getQueryTimeout() != 0 ) {
 					statement.setQueryTimeout( 0 );
 				}
 			}
 			catch( SQLException sqle ) {
 				// there was a problem "cleaning" the prepared statement
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Exception clearing maxRows/queryTimeout [%s]", sqle.getMessage() );
 				}
 				// EARLY EXIT!!!
 				return;
 			}
 			statement.close();
 			if ( lastQuery == statement ) {
 				lastQuery = null;
 			}
 		}
 		catch( SQLException e ) {
 			LOG.debugf( "Unable to release JDBC statement [%s]", e.getMessage() );
 		}
 		catch ( Exception e ) {
 			// try to handle general errors more elegantly
 			LOG.debugf( "Unable to release JDBC statement [%s]", e.getMessage() );
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected void close(ResultSet resultSet) {
 		LOG.tracev( "Closing result set [{0}]", resultSet );
 
 		if ( resultSet instanceof InvalidatableWrapper ) {
 			final InvalidatableWrapper<ResultSet> wrapper = (InvalidatableWrapper<ResultSet>) resultSet;
 			close( wrapper.getWrappedObject() );
 			wrapper.invalidate();
 			return;
 		}
 
 		try {
 			resultSet.close();
 		}
 		catch( SQLException e ) {
 			LOG.debugf( "Unable to release JDBC result set [%s]", e.getMessage() );
 		}
 		catch ( Exception e ) {
 			// try to handle general errors more elegantly
 			LOG.debugf( "Unable to release JDBC result set [%s]", e.getMessage() );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ResultSetWrapperImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ResultSetWrapperImpl.java
index 11d7ee3d49..c5cf9d1bd2 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ResultSetWrapperImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ResultSetWrapperImpl.java
@@ -1,51 +1,52 @@
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
 package org.hibernate.engine.jdbc.internal;
 
 import java.sql.ResultSet;
 
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.engine.jdbc.ResultSetWrapperProxy;
 import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
 
 /**
  * Standard Hibernate implementation for wrapping a {@link ResultSet} in a
  " column name cache" wrapper.
  *
+ * @author Steve Ebersole
  * @author Gail Badner
  */
 public class ResultSetWrapperImpl implements ResultSetWrapper {
 	/**
 	 * Singleton access
 	 */
-	public static ResultSetWrapper INSTANCE = new ResultSetWrapperImpl();
+	public static final ResultSetWrapper INSTANCE = new ResultSetWrapperImpl();
 
 	private ResultSetWrapperImpl() {
 	}
 
 	@Override
 	public ResultSet wrap(ResultSet resultSet, ColumnNameCache columnNameCache) {
 		return ResultSetWrapperProxy.generateProxy( resultSet, columnNameCache );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
index f1d56fb263..26d8b6587c 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
@@ -1,298 +1,330 @@
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
 package org.hibernate.engine.jdbc.spi;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.sql.Statement;
 
 import org.jboss.logging.Logger;
 import org.jboss.logging.Logger.Level;
 
 import org.hibernate.JDBCException;
 import org.hibernate.exception.internal.SQLStateConverter;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Helper for handling SQLExceptions in various manners.
  *
  * @author Steve Ebersole
  */
 public class SqlExceptionHelper {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			SqlExceptionHelper.class.getName()
+	);
 
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SqlExceptionHelper.class.getName());
-
-	public static final String DEFAULT_EXCEPTION_MSG = "SQL Exception";
-	public static final String DEFAULT_WARNING_MSG = "SQL Warning";
+	private static final String DEFAULT_EXCEPTION_MSG = "SQL Exception";
+	private static final String DEFAULT_WARNING_MSG = "SQL Warning";
 
-	public static final SQLExceptionConverter DEFAULT_CONVERTER = new SQLStateConverter(
+	private static final SQLExceptionConverter DEFAULT_CONVERTER = new SQLStateConverter(
 			new ViolatedConstraintNameExtracter() {
 				public String extractConstraintName(SQLException e) {
 					return null;
 				}
 			}
 	);
 
 	private SQLExceptionConverter sqlExceptionConverter;
 
 	/**
 	 * Create an exception helper with a default exception converter.
 	 */
 	public SqlExceptionHelper() {
 		sqlExceptionConverter = DEFAULT_CONVERTER;
 	}
 
 	/**
 	 * Create an exception helper with a specific exception converter.
 	 *
 	 * @param sqlExceptionConverter The exception converter to use.
 	 */
 	public SqlExceptionHelper(SQLExceptionConverter sqlExceptionConverter) {
 		this.sqlExceptionConverter = sqlExceptionConverter;
 	}
 
 	/**
 	 * Access the current exception converter being used internally.
 	 *
 	 * @return The current exception converter.
 	 */
 	public SQLExceptionConverter getSqlExceptionConverter() {
 		return sqlExceptionConverter;
 	}
 
 	/**
 	 * Inject the exception converter to use.
 	 * <p/>
 	 * NOTE : <tt>null</tt> is allowed and signifies to use the default.
 	 *
 	 * @param sqlExceptionConverter The converter to use.
 	 */
 	public void setSqlExceptionConverter(SQLExceptionConverter sqlExceptionConverter) {
-		this.sqlExceptionConverter = ( sqlExceptionConverter == null ? DEFAULT_CONVERTER : sqlExceptionConverter );
+		this.sqlExceptionConverter = (sqlExceptionConverter == null ? DEFAULT_CONVERTER : sqlExceptionConverter);
+	}
+
+	// SQLException ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	/**
+	 * Convert an SQLException using the current converter, doing some logging first.
+	 *
+	 * @param sqlException The exception to convert
+	 * @param message An error message.
+	 *
+	 * @return The converted exception
+	 */
+	public JDBCException convert(SQLException sqlException, String message) {
+		return convert( sqlException, message, "n/a" );
+	}
+
+	/**
+	 * Convert an SQLException using the current converter, doing some logging first.
+	 *
+	 * @param sqlException The exception to convert
+	 * @param message An error message.
+	 * @param sql The SQL being executed when the exception occurred
+	 *
+	 * @return The converted exception
+	 */
+	public JDBCException convert(SQLException sqlException, String message, String sql) {
+		logExceptions( sqlException, message + " [" + sql + "]" );
+		return sqlExceptionConverter.convert( sqlException, message, sql );
 	}
 
-    // SQLException ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-    /**
-     * Convert an SQLException using the current converter, doing some logging first.
-     *
-     * @param sqlException The exception to convert
-     * @param message An error message.
-     * @return The converted exception
-     */
-    public JDBCException convert( SQLException sqlException,
-                                  String message ) {
-        return convert(sqlException, message, "n/a");
-    }
-
-    /**
-     * Convert an SQLException using the current converter, doing some logging first.
-     *
-     * @param sqlException The exception to convert
-     * @param message An error message.
-     * @param sql The SQL being executed when the exception occurred
-     * @return The converted exception
-     */
-    public JDBCException convert( SQLException sqlException,
-                                  String message,
-                                  String sql ) {
-        logExceptions(sqlException, message + " [" + sql + "]");
-        return sqlExceptionConverter.convert(sqlException, message, sql);
-    }
-
-    /**
-     * Log the given (and any nested) exception.
-     *
-     * @param sqlException The exception to log
-     * @param message The message text to use as a preamble.
-     */
-    public void logExceptions( SQLException sqlException,
-                               String message ) {
-        if (LOG.isEnabled(Level.ERROR)) {
-            if (LOG.isDebugEnabled()) {
-                message = StringHelper.isNotEmpty(message) ? message : DEFAULT_EXCEPTION_MSG;
-                LOG.debug( message, sqlException );
-            }
-            final boolean warnEnabled = LOG.isEnabled( Level.WARN );
-            while (sqlException != null) {
-                if ( warnEnabled ) {
-                    StringBuilder buf = new StringBuilder(30).append("SQL Error: ").append(sqlException.getErrorCode()).append(", SQLState: ").append(sqlException.getSQLState());
-                    LOG.warn(buf.toString());
-                }
-                LOG.error(sqlException.getMessage());
-                sqlException = sqlException.getNextException();
-            }
-        }
-    }
-
-    // SQLWarning ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-    /**
-     * Contract for handling {@link SQLWarning warnings}
-     */
-    public static interface WarningHandler {
-        /**
-         * Should processing be done? Allows short-circuiting if not.
-         *
-         * @return True to process warnings, false otherwise.
-         */
-        public boolean doProcess();
-
-        /**
-         * Prepare for processing of a {@link SQLWarning warning} stack.
-         * <p/>
-         * Note that the warning here is also the first passed to {@link #handleWarning}
-         *
-         * @param warning The first warning in the stack.
-         */
-        public void prepare( SQLWarning warning );
-
-        /**
-         * Handle an individual warning in the stack.
-         *
-         * @param warning The warning to handle.
-         */
-        public void handleWarning( SQLWarning warning );
-    }
-
-    /**
-     * Basic support for {@link WarningHandler} implementations which log
-     */
-    public static abstract class WarningHandlerLoggingSupport implements WarningHandler {
-        public final void handleWarning( SQLWarning warning ) {
-        	StringBuilder buf = new StringBuilder(30).append("SQL Warning Code: ").append(warning.getErrorCode()).append(", SQLState: ").append(warning.getSQLState());
-            logWarning(buf.toString(), warning.getMessage());
-        }
-
-        /**
-         * Delegate to log common details of a {@link SQLWarning warning}
-         *
-         * @param description A description of the warning
-         * @param message The warning message
-         */
-        protected abstract void logWarning( String description,
-                                            String message );
-    }
-
-    public static class StandardWarningHandler extends WarningHandlerLoggingSupport {
-        private final String introMessage;
-
-        public StandardWarningHandler( String introMessage ) {
-            this.introMessage = introMessage;
-        }
-
-        public boolean doProcess() {
-            return LOG.isEnabled(Level.WARN);
-        }
-
-        public void prepare( SQLWarning warning ) {
-            LOG.debug(introMessage, warning);
-        }
-
-        @Override
-        protected void logWarning( String description,
-                                   String message ) {
-            LOG.warn(description);
-            LOG.warn(message);
-        }
-    }
-
-    public static StandardWarningHandler STANDARD_WARNING_HANDLER = new StandardWarningHandler(DEFAULT_WARNING_MSG);
-
-    public void walkWarnings( SQLWarning warning,
-                              WarningHandler handler ) {
-        if (warning == null || handler.doProcess()) {
-            return;
-        }
-        handler.prepare(warning);
-        while (warning != null) {
-            handler.handleWarning(warning);
-            warning = warning.getNextWarning();
-        }
-    }
-
-    /**
-     * Standard (legacy) behavior for logging warnings associated with a JDBC {@link Connection} and clearing them.
-     * <p/>
-     * Calls {@link #handleAndClearWarnings(Connection, WarningHandler)} using {@link #STANDARD_WARNING_HANDLER}
-     *
-     * @param connection The JDBC connection potentially containing warnings
-     */
-    public void logAndClearWarnings( Connection connection ) {
-        handleAndClearWarnings(connection, STANDARD_WARNING_HANDLER);
-    }
-
-    /**
-     * General purpose handling of warnings associated with a JDBC {@link Connection}.
-     *
-     * @param connection The JDBC connection potentially containing warnings
-     * @param handler The handler for each individual warning in the stack.
-     * @see #walkWarnings
-     */
-    @SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"} )
-    public void handleAndClearWarnings( Connection connection,
-                                        WarningHandler handler ) {
-        try {
-            walkWarnings(connection.getWarnings(), handler);
-        } catch (SQLException sqle) {
-            // workaround for WebLogic
-            LOG.debug("could not log warnings", sqle);
-        }
-        try {
-            // Sybase fail if we don't do that, sigh...
-            connection.clearWarnings();
-        } catch (SQLException sqle) {
-            LOG.debug("could not clear warnings", sqle);
-        }
-    }
-
-    /**
-     * General purpose handling of warnings associated with a JDBC {@link Statement}.
-     *
-     * @param statement The JDBC statement potentially containing warnings
-     * @param handler The handler for each individual warning in the stack.
-     * @see #walkWarnings
-     */
-    @SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"} )
-    public void handleAndClearWarnings( Statement statement,
-                                        WarningHandler handler ) {
-        try {
-            walkWarnings(statement.getWarnings(), handler);
-        } catch (SQLException sqlException) {
-            // workaround for WebLogic
-            LOG.debug("could not log warnings", sqlException);
-        }
-        try {
-            // Sybase fail if we don't do that, sigh...
-            statement.clearWarnings();
-        } catch (SQLException sqle) {
-            LOG.debug("could not clear warnings", sqle);
-        }
-    }
+	/**
+	 * Log the given (and any nested) exception.
+	 *
+	 * @param sqlException The exception to log
+	 * @param message The message text to use as a preamble.
+	 */
+	public void logExceptions(
+			SQLException sqlException,
+			String message) {
+		if ( LOG.isEnabled( Level.ERROR ) ) {
+			if ( LOG.isDebugEnabled() ) {
+				message = StringHelper.isNotEmpty( message ) ? message : DEFAULT_EXCEPTION_MSG;
+				LOG.debug( message, sqlException );
+			}
+			final boolean warnEnabled = LOG.isEnabled( Level.WARN );
+			while ( sqlException != null ) {
+				if ( warnEnabled ) {
+					LOG.warn( "SQL Error: " + sqlException.getErrorCode() + ", SQLState: " + sqlException.getSQLState() );
+				}
+				LOG.error( sqlException.getMessage() );
+				sqlException = sqlException.getNextException();
+			}
+		}
+	}
+
+	// SQLWarning ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	/**
+	 * Contract for handling {@link SQLWarning warnings}
+	 */
+	public static interface WarningHandler {
+		/**
+		 * Should processing be done? Allows short-circuiting if not.
+		 *
+		 * @return True to process warnings, false otherwise.
+		 */
+		public boolean doProcess();
+
+		/**
+		 * Prepare for processing of a {@link SQLWarning warning} stack.
+		 * <p/>
+		 * Note that the warning here is also the first passed to {@link #handleWarning}
+		 *
+		 * @param warning The first warning in the stack.
+		 */
+		public void prepare(SQLWarning warning);
+
+		/**
+		 * Handle an individual warning in the stack.
+		 *
+		 * @param warning The warning to handle.
+		 */
+		public void handleWarning(SQLWarning warning);
+	}
+
+	/**
+	 * Basic support for {@link WarningHandler} implementations which handle {@link SQLWarning warnings}
+	 */
+	public abstract static class WarningHandlerLoggingSupport implements WarningHandler {
+		@Override
+		public final void handleWarning(SQLWarning warning) {
+			logWarning(
+					"SQL Warning Code: " + warning.getErrorCode() + ", SQLState: " + warning.getSQLState(),
+					warning.getMessage()
+			);
+		}
+
+		/**
+		 * Delegate to log common details of a {@link SQLWarning warning}
+		 *
+		 * @param description A description of the warning
+		 * @param message The warning message
+		 */
+		protected abstract void logWarning(String description, String message);
+	}
+
+	/**
+	 * Standard SQLWarning handler for logging warnings
+	 */
+	public static class StandardWarningHandler extends WarningHandlerLoggingSupport {
+		private final String introMessage;
+
+		/**
+		 * Creates a StandardWarningHandler
+		 *
+		 * @param introMessage The introduction message for the hierarchy
+		 */
+		public StandardWarningHandler(String introMessage) {
+			this.introMessage = introMessage;
+		}
+
+		@Override
+		public boolean doProcess() {
+			return LOG.isEnabled( Level.WARN );
+		}
+
+		@Override
+		public void prepare(SQLWarning warning) {
+			LOG.debug( introMessage, warning );
+		}
+
+		@Override
+		protected void logWarning(
+				String description,
+				String message) {
+			LOG.warn( description );
+			LOG.warn( message );
+		}
+	}
+
+	/**
+	 * Static access to the standard handler for logging warnings
+	 */
+	public static final StandardWarningHandler STANDARD_WARNING_HANDLER = new StandardWarningHandler( DEFAULT_WARNING_MSG );
+
+	/**
+	 * Generic algorithm to walk the hierarchy of SQLWarnings
+	 *
+	 * @param warning The warning to walk
+	 * @param handler The handler
+	 */
+	public void walkWarnings(
+			SQLWarning warning,
+			WarningHandler handler) {
+		if ( warning == null || handler.doProcess() ) {
+			return;
+		}
+		handler.prepare( warning );
+		while ( warning != null ) {
+			handler.handleWarning( warning );
+			warning = warning.getNextWarning();
+		}
+	}
+
+	/**
+	 * Standard (legacy) behavior for logging warnings associated with a JDBC {@link Connection} and clearing them.
+	 * <p/>
+	 * Calls {@link #handleAndClearWarnings(Connection, WarningHandler)} using {@link #STANDARD_WARNING_HANDLER}
+	 *
+	 * @param connection The JDBC connection potentially containing warnings
+	 */
+	public void logAndClearWarnings(Connection connection) {
+		handleAndClearWarnings( connection, STANDARD_WARNING_HANDLER );
+	}
+
+	/**
+	 * General purpose handling of warnings associated with a JDBC {@link Connection}.
+	 *
+	 * @param connection The JDBC connection potentially containing warnings
+	 * @param handler The handler for each individual warning in the stack.
+	 *
+	 * @see #walkWarnings
+	 */
+	@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
+	public void handleAndClearWarnings(
+			Connection connection,
+			WarningHandler handler) {
+		try {
+			walkWarnings( connection.getWarnings(), handler );
+		}
+		catch (SQLException sqle) {
+			// workaround for WebLogic
+			LOG.debug( "could not log warnings", sqle );
+		}
+		try {
+			// Sybase fail if we don't do that, sigh...
+			connection.clearWarnings();
+		}
+		catch (SQLException sqle) {
+			LOG.debug( "could not clear warnings", sqle );
+		}
+	}
+
+	/**
+	 * General purpose handling of warnings associated with a JDBC {@link Statement}.
+	 *
+	 * @param statement The JDBC statement potentially containing warnings
+	 * @param handler The handler for each individual warning in the stack.
+	 *
+	 * @see #walkWarnings
+	 */
+	@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
+	public void handleAndClearWarnings(
+			Statement statement,
+			WarningHandler handler) {
+		try {
+			walkWarnings( statement.getWarnings(), handler );
+		}
+		catch (SQLException sqlException) {
+			// workaround for WebLogic
+			LOG.debug( "could not log warnings", sqlException );
+		}
+		try {
+			// Sybase fail if we don't do that, sigh...
+			statement.clearWarnings();
+		}
+		catch (SQLException sqle) {
+			LOG.debug( "could not clear warnings", sqle );
+		}
+	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/NativeSQLQueryPlan.java b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/NativeSQLQueryPlan.java
index e661bfbf4c..84867ca1f4 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/NativeSQLQueryPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/NativeSQLQueryPlan.java
@@ -1,219 +1,226 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
- *
  */
 package org.hibernate.engine.query.spi;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.QueryException;
 import org.hibernate.action.internal.BulkOperationCleanupAction;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.custom.sql.SQLCustomQuery;
 import org.hibernate.type.Type;
 
 /**
  * Defines a query execution plan for a native-SQL query.
  *
  * @author Steve Ebersole
  */
 public class NativeSQLQueryPlan implements Serializable {
-	private final String sourceQuery;
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			NativeSQLQueryPlan.class.getName()
+	);
 
+	private final String sourceQuery;
 	private final SQLCustomQuery customQuery;
 
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, NativeSQLQueryPlan.class.getName());
-
+	/**
+	 * Constructs a NativeSQLQueryPlan
+	 *
+	 * @param specification The query spec
+	 * @param factory The SessionFactory
+	 */
 	public NativeSQLQueryPlan(
 			NativeSQLQuerySpecification specification,
 			SessionFactoryImplementor factory) {
 		this.sourceQuery = specification.getQueryString();
-
-		customQuery = new SQLCustomQuery(
+		this.customQuery = new SQLCustomQuery(
 				specification.getQueryString(),
 				specification.getQueryReturns(),
 				specification.getQuerySpaces(),
-				factory );
+				factory
+		);
 	}
 
 	public String getSourceQuery() {
 		return sourceQuery;
 	}
 
 	public SQLCustomQuery getCustomQuery() {
 		return customQuery;
 	}
 
 	private int[] getNamedParameterLocs(String name) throws QueryException {
 		Object loc = customQuery.getNamedParameterBindPoints().get( name );
 		if ( loc == null ) {
 			throw new QueryException(
 					"Named parameter does not appear in Query: " + name,
 					customQuery.getSQL() );
 		}
 		if ( loc instanceof Integer ) {
 			return new int[] { (Integer) loc };
 		}
 		else {
 			return ArrayHelper.toIntArray( (List) loc );
 		}
 	}
 
 	/**
 	 * Perform binding of all the JDBC bind parameter values based on the user-defined
 	 * positional query parameters (these are the '?'-style hibernate query
 	 * params) into the JDBC {@link PreparedStatement}.
 	 *
 	 * @param st The prepared statement to which to bind the parameter values.
 	 * @param queryParameters The query parameters specified by the application.
 	 * @param start JDBC paramer binds are positional, so this is the position
 	 * from which to start binding.
 	 * @param session The session from which the query originated.
 	 *
 	 * @return The number of JDBC bind positions accounted for during execution.
 	 *
 	 * @throws SQLException Some form of JDBC error binding the values.
 	 * @throws HibernateException Generally indicates a mapping problem or type mismatch.
 	 */
 	private int bindPositionalParameters(
 			final PreparedStatement st,
 			final QueryParameters queryParameters,
 			final int start,
 			final SessionImplementor session) throws SQLException {
 		final Object[] values = queryParameters.getFilteredPositionalParameterValues();
 		final Type[] types = queryParameters.getFilteredPositionalParameterTypes();
 		int span = 0;
 		for (int i = 0; i < values.length; i++) {
 			types[i].nullSafeSet( st, values[i], start + span, session );
 			span += types[i].getColumnSpan( session.getFactory() );
 		}
 		return span;
 	}
 
 	/**
 	 * Perform binding of all the JDBC bind parameter values based on the user-defined
 	 * named query parameters into the JDBC {@link PreparedStatement}.
 	 *
 	 * @param ps The prepared statement to which to bind the parameter values.
 	 * @param namedParams The named query parameters specified by the application.
 	 * @param start JDBC paramer binds are positional, so this is the position
 	 * from which to start binding.
 	 * @param session The session from which the query originated.
 	 *
 	 * @return The number of JDBC bind positions accounted for during execution.
 	 *
 	 * @throws SQLException Some form of JDBC error binding the values.
 	 * @throws HibernateException Generally indicates a mapping problem or type mismatch.
 	 */
 	private int bindNamedParameters(
 			final PreparedStatement ps,
 			final Map namedParams,
 			final int start,
 			final SessionImplementor session) throws SQLException {
 		if ( namedParams != null ) {
 			// assumes that types are all of span 1
 			Iterator iter = namedParams.entrySet().iterator();
 			int result = 0;
 			while ( iter.hasNext() ) {
 				Map.Entry e = (Map.Entry) iter.next();
 				String name = (String) e.getKey();
 				TypedValue typedval = (TypedValue) e.getValue();
 				int[] locs = getNamedParameterLocs( name );
 				for (int i = 0; i < locs.length; i++) {
                     LOG.debugf("bindNamedParameters() %s -> %s [%s]", typedval.getValue(), name, locs[i] + start);
 					typedval.getType().nullSafeSet( ps, typedval.getValue(),
 							locs[i] + start, session );
 				}
 				result += locs.length;
 			}
 			return result;
 		}
         return 0;
 	}
 
 	protected void coordinateSharedCacheCleanup(SessionImplementor session) {
 		BulkOperationCleanupAction action = new BulkOperationCleanupAction( session, getCustomQuery().getQuerySpaces() );
 
 		if ( session.isEventSource() ) {
 			( ( EventSource ) session ).getActionQueue().addAction( action );
 		}
 		else {
 			action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion( true, session );
 		}
 	}
 
 	public int performExecuteUpdate(QueryParameters queryParameters,
 			SessionImplementor session) throws HibernateException {
 
 		coordinateSharedCacheCleanup( session );
 
 		if(queryParameters.isCallable()) {
 			throw new IllegalArgumentException("callable not yet supported for native queries");
 		}
 
 		int result = 0;
 		PreparedStatement ps;
 		try {
 			queryParameters.processFilters( this.customQuery.getSQL(),
 					session );
 			String sql = queryParameters.getFilteredSQL();
 
 			ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
 
 			try {
 				int col = 1;
 				col += bindPositionalParameters( ps, queryParameters, col,
 						session );
 				col += bindNamedParameters( ps, queryParameters
 						.getNamedParameters(), col, session );
 				result = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
 			}
 			finally {
 				if ( ps != null ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 				}
 			}
 		}
 		catch (SQLException sqle) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle, "could not execute native bulk manipulation query", this.sourceQuery );
 		}
 
 		return result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/AssociationKey.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/AssociationKey.java
index 5bf26b72b7..90bb0d380d 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/AssociationKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/AssociationKey.java
@@ -1,53 +1,70 @@
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
 
 import java.io.Serializable;
 
 /**
  * Identifies a named association belonging to a particular
  * entity instance. Used to record the fact that an association
  * is null during loading.
  * 
  * @author Gavin King
  */
 public final class AssociationKey implements Serializable {
 	private EntityKey ownerKey;
 	private String propertyName;
-	
+
+	/**
+	 * Constructs an AssociationKey
+	 *
+	 * @param ownerKey The EntityKey of the association owner
+	 * @param propertyName The name of the property on the owner which defines the association
+	 */
 	public AssociationKey(EntityKey ownerKey, String propertyName) {
 		this.ownerKey = ownerKey;
 		this.propertyName = propertyName;
 	}
-	
-	public boolean equals(Object that) {
-		AssociationKey key = (AssociationKey) that;
-		return key.propertyName.equals(propertyName) && 
-			key.ownerKey.equals(ownerKey);
+
+	@Override
+	public boolean equals(Object o) {
+		if ( this == o ) {
+			return true;
+		}
+		if ( o == null || getClass() != o.getClass() ) {
+			return false;
+		}
+
+		final AssociationKey that = (AssociationKey) o;
+		return ownerKey.equals( that.ownerKey )
+				&& propertyName.equals( that.propertyName );
 	}
-	
+
+	@Override
 	public int hashCode() {
-		return ownerKey.hashCode() + propertyName.hashCode();
+		int result = ownerKey.hashCode();
+		result = 31 * result + propertyName.hashCode();
+		return result;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/CollectionKey.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/CollectionKey.java
index a815db8fcd..9df614a053 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/CollectionKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CollectionKey.java
@@ -1,142 +1,158 @@
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
 
 import org.hibernate.EntityMode;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 
 /**
  * Uniquely identifies a collection instance in a particular session.
  *
  * @author Gavin King
  */
 public final class CollectionKey implements Serializable {
 	private final String role;
 	private final Serializable key;
 	private final Type keyType;
 	private final SessionFactoryImplementor factory;
 	private final int hashCode;
 	private EntityMode entityMode;
 
 	public CollectionKey(CollectionPersister persister, Serializable key) {
 		this(
 				persister.getRole(),
 				key,
 				persister.getKeyType(),
 				persister.getOwnerEntityPersister().getEntityMetamodel().getEntityMode(),
 				persister.getFactory()
 		);
 	}
 
 	public CollectionKey(CollectionPersister persister, Serializable key, EntityMode em) {
 		this( persister.getRole(), key, persister.getKeyType(), em, persister.getFactory() );
 	}
 
 	private CollectionKey(
 			String role,
-	        Serializable key,
-	        Type keyType,
-	        EntityMode entityMode,
-	        SessionFactoryImplementor factory) {
+			Serializable key,
+			Type keyType,
+			EntityMode entityMode,
+			SessionFactoryImplementor factory) {
 		this.role = role;
 		this.key = key;
 		this.keyType = keyType;
 		this.entityMode = entityMode;
 		this.factory = factory;
-		this.hashCode = generateHashCode(); //cache the hashcode
+		//cache the hash-code
+		this.hashCode = generateHashCode();
 	}
 
-	public boolean equals(Object other) {
-		CollectionKey that = (CollectionKey) other;
-		return that.role.equals(role) &&
-		       keyType.isEqual(that.key, key, factory);
-	}
-
-	public int generateHashCode() {
+	private int generateHashCode() {
 		int result = 17;
 		result = 37 * result + role.hashCode();
-		result = 37 * result + keyType.getHashCode(key, factory);
+		result = 37 * result + keyType.getHashCode( key, factory );
 		return result;
 	}
 
-	public int hashCode() {
-		return hashCode;
-	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public Serializable getKey() {
 		return key;
 	}
 
+	@Override
 	public String toString() {
-		return "CollectionKey" +
-		       MessageHelper.collectionInfoString( factory.getCollectionPersister(role), key, factory );
+		return "CollectionKey"
+				+ MessageHelper.collectionInfoString( factory.getCollectionPersister( role ), key, factory );
+	}
+
+	@Override
+	public boolean equals(Object other) {
+		if ( this == other ) {
+			return true;
+		}
+		if ( other == null || getClass() != other.getClass() ) {
+			return false;
+		}
+
+		final CollectionKey that = (CollectionKey) other;
+		return that.role.equals( role )
+				&& keyType.isEqual( that.key, key, factory );
+	}
+
+	@Override
+	public int hashCode() {
+		return hashCode;
 	}
 
+
 	/**
 	 * Custom serialization routine used during serialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param oos The stream to which we should write the serial data.
+	 *
 	 * @throws java.io.IOException
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeObject( role );
 		oos.writeObject( key );
 		oos.writeObject( keyType );
 		oos.writeObject( entityMode.toString() );
 	}
 
 	/**
 	 * Custom deserialization routine used during deserialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param ois The stream from which to read the entry.
 	 * @param session The session being deserialized.
+	 *
 	 * @return The deserialized CollectionKey
+	 *
 	 * @throws IOException
 	 * @throws ClassNotFoundException
 	 */
 	public static CollectionKey deserialize(
 			ObjectInputStream ois,
-	        SessionImplementor session) throws IOException, ClassNotFoundException {
+			SessionImplementor session) throws IOException, ClassNotFoundException {
 		return new CollectionKey(
-				( String ) ois.readObject(),
-		        ( Serializable ) ois.readObject(),
-		        ( Type ) ois.readObject(),
-		        EntityMode.parse( ( String ) ois.readObject() ),
-		        ( session == null ? null : session.getFactory() )
+				(String) ois.readObject(),
+				(Serializable) ois.readObject(),
+				(Type) ois.readObject(),
+				EntityMode.parse( (String) ois.readObject() ),
+				(session == null ? null : session.getFactory())
 		);
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityKey.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityKey.java
index 61c1409bf7..b78bebf323 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityKey.java
@@ -1,191 +1,198 @@
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
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 
 /**
  * Uniquely identifies of an entity instance in a particular session by identifier.
  * <p/>
  * Information used to determine uniqueness consists of the entity-name and the identifier value (see {@link #equals}).
  *
  * @author Gavin King
  */
 public final class EntityKey implements Serializable {
 	private final Serializable identifier;
 	private final String entityName;
 	private final String rootEntityName;
 	private final String tenantId;
 
 	private final int hashCode;
 
 	private final Type identifierType;
 	private final boolean isBatchLoadable;
 	private final SessionFactoryImplementor factory;
 
 	/**
 	 * Construct a unique identifier for an entity class instance.
-	 * <p>
+	 * <p/>
 	 * NOTE : This signature has changed to accommodate both entity mode and multi-tenancy, both of which relate to
 	 * the Session to which this key belongs.  To help minimize the impact of these changes in the future, the
 	 * {@link SessionImplementor#generateEntityKey} method was added to hide the session-specific changes.
 	 *
 	 * @param id The entity id
 	 * @param persister The entity persister
 	 * @param tenantId The tenant identifier of the session to which this key belongs
 	 */
 	public EntityKey(Serializable id, EntityPersister persister, String tenantId) {
 		if ( id == null ) {
 			throw new AssertionFailure( "null identifier" );
 		}
-		this.identifier = id; 
+		this.identifier = id;
 		this.rootEntityName = persister.getRootEntityName();
 		this.entityName = persister.getEntityName();
 		this.tenantId = tenantId;
 
 		this.identifierType = persister.getIdentifierType();
 		this.isBatchLoadable = persister.isBatchLoadable();
 		this.factory = persister.getFactory();
 		this.hashCode = generateHashCode();
 	}
 
 	/**
 	 * Used to reconstruct an EntityKey during deserialization.
 	 *
 	 * @param identifier The identifier value
 	 * @param rootEntityName The root entity name
 	 * @param entityName The specific entity name
 	 * @param identifierType The type of the identifier value
 	 * @param batchLoadable Whether represented entity is eligible for batch loading
 	 * @param factory The session factory
 	 * @param tenantId The entity's tenant id (from the session that loaded it).
 	 */
 	private EntityKey(
 			Serializable identifier,
-	        String rootEntityName,
-	        String entityName,
-	        Type identifierType,
-	        boolean batchLoadable,
-	        SessionFactoryImplementor factory,
+			String rootEntityName,
+			String entityName,
+			Type identifierType,
+			boolean batchLoadable,
+			SessionFactoryImplementor factory,
 			String tenantId) {
 		this.identifier = identifier;
 		this.rootEntityName = rootEntityName;
 		this.entityName = entityName;
 		this.identifierType = identifierType;
 		this.isBatchLoadable = batchLoadable;
 		this.factory = factory;
 		this.tenantId = tenantId;
 		this.hashCode = generateHashCode();
 	}
 
 	private int generateHashCode() {
 		int result = 17;
 		result = 37 * result + rootEntityName.hashCode();
 		result = 37 * result + identifierType.getHashCode( identifier, factory );
 		return result;
 	}
 
 	public boolean isBatchLoadable() {
 		return isBatchLoadable;
 	}
 
 	public Serializable getIdentifier() {
 		return identifier;
 	}
 
 	public String getEntityName() {
 		return entityName;
 	}
 
 	@Override
 	public boolean equals(Object other) {
-		EntityKey otherKey = (EntityKey) other;
-		return otherKey.rootEntityName.equals(this.rootEntityName) &&
-				identifierType.isEqual(otherKey.identifier, this.identifier, factory) &&
-				EqualsHelper.equals( tenantId, otherKey.tenantId );
+		if ( this == other ) {
+			return true;
+		}
+		if ( other == null || getClass() != other.getClass() ) {
+			return false;
+		}
+
+		final EntityKey otherKey = (EntityKey) other;
+		return otherKey.rootEntityName.equals( this.rootEntityName )
+				&& identifierType.isEqual( otherKey.identifier, this.identifier, factory )
+				&& EqualsHelper.equals( tenantId, otherKey.tenantId );
 	}
 
 	@Override
 	public int hashCode() {
 		return hashCode;
 	}
 
 	@Override
 	public String toString() {
-		return "EntityKey" + 
-			MessageHelper.infoString( factory.getEntityPersister( entityName ), identifier, factory );
+		return "EntityKey" +
+				MessageHelper.infoString( factory.getEntityPersister( entityName ), identifier, factory );
 	}
 
 	/**
 	 * Custom serialization routine used during serialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param oos The stream to which we should write the serial data.
 	 *
 	 * @throws IOException Thrown by Java I/O
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeObject( identifier );
 		oos.writeObject( rootEntityName );
 		oos.writeObject( entityName );
 		oos.writeObject( identifierType );
 		oos.writeBoolean( isBatchLoadable );
 		oos.writeObject( tenantId );
 	}
 
 	/**
 	 * Custom deserialization routine used during deserialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param ois The stream from which to read the entry.
 	 * @param session The session being deserialized.
 	 *
 	 * @return The deserialized EntityEntry
 	 *
 	 * @throws IOException Thrown by Java I/O
 	 * @throws ClassNotFoundException Thrown by Java I/O
 	 */
 	public static EntityKey deserialize(
 			ObjectInputStream ois,
-	        SessionImplementor session) throws IOException, ClassNotFoundException {
+			SessionImplementor session) throws IOException, ClassNotFoundException {
 		return new EntityKey(
-				( Serializable ) ois.readObject(),
-		        (String) ois.readObject(),
+				(Serializable) ois.readObject(),
+				(String) ois.readObject(),
 				(String) ois.readObject(),
-		        ( Type ) ois.readObject(),
-		        ois.readBoolean(),
-		        ( session == null ? null : session.getFactory() ),
+				(Type) ois.readObject(),
+				ois.readBoolean(),
+				(session == null ? null : session.getFactory()),
 				(String) ois.readObject()
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/LoadQueryInfluencers.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/LoadQueryInfluencers.java
index f7f1bc0ab2..be0c1d76ef 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/LoadQueryInfluencers.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/LoadQueryInfluencers.java
@@ -1,198 +1,198 @@
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
 
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.Filter;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.internal.FilterImpl;
 import org.hibernate.type.Type;
 
 /**
  * Centralize all options which can influence the SQL query needed to load an
  * entity.  Currently such influencers are defined as:<ul>
  * <li>filters</li>
  * <li>fetch profiles</li>
  * <li>internal fetch profile (merge profile, etc)</li>
  * </ul>
  *
  * @author Steve Ebersole
  */
 public class LoadQueryInfluencers implements Serializable {
 	/**
 	 * Static reference useful for cases where we are creating load SQL
 	 * outside the context of any influencers.  One such example is
 	 * anything created by the session factory.
 	 */
-	public static LoadQueryInfluencers NONE = new LoadQueryInfluencers();
+	public static final LoadQueryInfluencers NONE = new LoadQueryInfluencers();
 
 	private final SessionFactoryImplementor sessionFactory;
 	private String internalFetchProfile;
 	private final Map<String,Filter> enabledFilters;
 	private final Set<String> enabledFetchProfileNames;
 
 	public LoadQueryInfluencers() {
 		this( null, Collections.<String, Filter>emptyMap(), Collections.<String>emptySet() );
 	}
 
 	public LoadQueryInfluencers(SessionFactoryImplementor sessionFactory) {
 		this( sessionFactory, new HashMap<String,Filter>(), new HashSet<String>() );
 	}
 
 	private LoadQueryInfluencers(SessionFactoryImplementor sessionFactory, Map<String,Filter> enabledFilters, Set<String> enabledFetchProfileNames) {
 		this.sessionFactory = sessionFactory;
 		this.enabledFilters = enabledFilters;
 		this.enabledFetchProfileNames = enabledFetchProfileNames;
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 
 	// internal fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public String getInternalFetchProfile() {
 		return internalFetchProfile;
 	}
 
 	public void setInternalFetchProfile(String internalFetchProfile) {
 		if ( sessionFactory == null ) {
 			// thats the signal that this is the immutable, context-less
 			// variety
 			throw new IllegalStateException( "Cannot modify context-less LoadQueryInfluencers" );
 		}
 		this.internalFetchProfile = internalFetchProfile;
 	}
 
 
 	// filter support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean hasEnabledFilters() {
 		return !enabledFilters.isEmpty();
 	}
 
 	public Map<String,Filter> getEnabledFilters() {
 		// First, validate all the enabled filters...
 		//TODO: this implementation has bad performance
 		for ( Filter filter : enabledFilters.values() ) {
 			filter.validate();
 		}
 		return enabledFilters;
 	}
 
 	/**
 	 * Returns an unmodifiable Set of enabled filter names.
 	 * @return an unmodifiable Set of enabled filter names.
 	 */
 	public Set<String> getEnabledFilterNames() {
 		return java.util.Collections.unmodifiableSet( enabledFilters.keySet() );
 	}
 
 	public Filter getEnabledFilter(String filterName) {
 		return enabledFilters.get( filterName );
 	}
 
 	public Filter enableFilter(String filterName) {
 		FilterImpl filter = new FilterImpl( sessionFactory.getFilterDefinition( filterName ) );
 		enabledFilters.put( filterName, filter );
 		return filter;
 	}
 
 	public void disableFilter(String filterName) {
 		enabledFilters.remove( filterName );
 	}
 
 	public Object getFilterParameterValue(String filterParameterName) {
 		String[] parsed = parseFilterParameterName( filterParameterName );
 		FilterImpl filter = ( FilterImpl ) enabledFilters.get( parsed[0] );
 		if ( filter == null ) {
 			throw new IllegalArgumentException( "Filter [" + parsed[0] + "] currently not enabled" );
 		}
 		return filter.getParameter( parsed[1] );
 	}
 
 	public Type getFilterParameterType(String filterParameterName) {
 		String[] parsed = parseFilterParameterName( filterParameterName );
 		FilterDefinition filterDef = sessionFactory.getFilterDefinition( parsed[0] );
 		if ( filterDef == null ) {
 			throw new IllegalArgumentException( "Filter [" + parsed[0] + "] not defined" );
 		}
 		Type type = filterDef.getParameterType( parsed[1] );
 		if ( type == null ) {
 			// this is an internal error of some sort...
 			throw new InternalError( "Unable to locate type for filter parameter" );
 		}
 		return type;
 	}
 
 	public static String[] parseFilterParameterName(String filterParameterName) {
 		int dot = filterParameterName.indexOf( '.' );
 		if ( dot <= 0 ) {
 			throw new IllegalArgumentException( "Invalid filter-parameter name format" );
 		}
 		String filterName = filterParameterName.substring( 0, dot );
 		String parameterName = filterParameterName.substring( dot + 1 );
 		return new String[] { filterName, parameterName };
 	}
 
 
 	// fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean hasEnabledFetchProfiles() {
 		return !enabledFetchProfileNames.isEmpty();
 	}
 
 	public Set getEnabledFetchProfileNames() {
 		return enabledFetchProfileNames;
 	}
 
 	private void checkFetchProfileName(String name) {
 		if ( !sessionFactory.containsFetchProfileDefinition( name ) ) {
 			throw new UnknownProfileException( name );
 		}
 	}
 
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		checkFetchProfileName( name );
 		return enabledFetchProfileNames.contains( name );
 	}
 
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		checkFetchProfileName( name );
 		enabledFetchProfileNames.add( name );
 	}
 
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		checkFetchProfileName( name );
 		enabledFetchProfileNames.remove( name );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/TypedValue.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/TypedValue.java
index bb8ed0b594..bf360d646e 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/TypedValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/TypedValue.java
@@ -1,90 +1,93 @@
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
 
 import java.io.Serializable;
 
 import org.hibernate.EntityMode;
 import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.type.Type;
 
 /**
  * An ordered pair of a value and its Hibernate type.
  * 
  * @see org.hibernate.type.Type
  * @author Gavin King
  */
 public final class TypedValue implements Serializable {
 	private final Type type;
 	private final Object value;
 	private final ValueHolder<Integer> hashcode;
 
 	public TypedValue(final Type type, final Object value) {
 		this.type = type;
 		this.value = value;
 		this.hashcode = new ValueHolder<Integer>(
 				new ValueHolder.DeferredInitializer<Integer>() {
 					@Override
 					public Integer initialize() {
 						return value == null ? 0 : type.getHashCode( value );
 					}
 				}
 		);
 	}
 	@Deprecated
 	public TypedValue(Type type, Object value, EntityMode entityMode) {
 		this(type, value);
 	}
 
 	public Object getValue() {
 		return value;
 	}
 
 	public Type getType() {
 		return type;
 	}
 	@Override
 	public String toString() {
 		return value==null ? "null" : value.toString();
 	}
 	@Override
 	public int hashCode() {
 		return hashcode.getValue();
 	}
 	@Override
 	public boolean equals(Object other) {
-		if ( !(other instanceof TypedValue) ) return false;
-		TypedValue that = (TypedValue) other;
-		/*return that.type.equals(type) && 
-			EqualsHelper.equals(that.value, value);*/
-		return type.getReturnedClass() == that.type.getReturnedClass() &&
-			type.isEqual(that.value, value );
+		if ( this == other ) {
+			return true;
+		}
+		if ( other == null || getClass() != other.getClass() ) {
+			return false;
+		}
+		final TypedValue that = (TypedValue) other;
+		return type.getReturnedClass() == that.type.getReturnedClass()
+				&& type.isEqual( that.value, value );
 	}
 
 }
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLCustomQuery.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLCustomQuery.java
index d495b6574a..dea83d007e 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLCustomQuery.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLCustomQuery.java
@@ -1,255 +1,255 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
- *
  */
 package org.hibernate.loader.custom.sql;
+import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.collection.SQLLoadableCollection;
 import org.hibernate.persister.entity.SQLLoadable;
 
 /**
  * Implements Hibernate's built-in support for native SQL queries.
  * <p/>
  * This support is built on top of the notion of "custom queries"...
  *
  * @author Gavin King
  * @author Max Andersen
  * @author Steve Ebersole
  */
-public class SQLCustomQuery implements CustomQuery {
+public class SQLCustomQuery implements CustomQuery, Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, SQLCustomQuery.class.getName() );
 
 	private final String sql;
 	private final Set querySpaces = new HashSet();
 	private final Map namedParameterBindPoints = new HashMap();
 	private final List customQueryReturns = new ArrayList();
 
 
 	public String getSQL() {
 		return sql;
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	public Map getNamedParameterBindPoints() {
 		return namedParameterBindPoints;
 	}
 
 	public List getCustomQueryReturns() {
 		return customQueryReturns;
 	}
 
 	public SQLCustomQuery(
 			final String sqlQuery,
 			final NativeSQLQueryReturn[] queryReturns,
 			final Collection additionalQuerySpaces,
 			final SessionFactoryImplementor factory) throws HibernateException {
 
 		LOG.tracev( "Starting processing of sql query [{0}]", sqlQuery );
 		SQLQueryReturnProcessor processor = new SQLQueryReturnProcessor(queryReturns, factory);
 		SQLQueryReturnProcessor.ResultAliasContext aliasContext = processor.process();
 
 
 //		Map[] propertyResultMaps =  (Map[]) processor.getPropertyResults().toArray( new Map[0] );
 //		Map[] collectionResultMaps =  (Map[]) processor.getCollectionPropertyResults().toArray( new Map[0] );
 //
 //		List collectionSuffixes = new ArrayList();
 //		List collectionOwnerAliases = processor.getCollectionOwnerAliases();
 //		List collectionPersisters = processor.getCollectionPersisters();
 //		int size = collectionPersisters.size();
 //		if (size!=0) {
 //			collectionOwners = new int[size];
 //			collectionRoles = new String[size];
 //			//collectionDescriptors = new CollectionAliases[size];
 //			for ( int i=0; i<size; i++ ) {
 //				CollectionPersister collectionPersister = (CollectionPersister) collectionPersisters.get(i);
 //				collectionRoles[i] = ( collectionPersister ).getRole();
 //				collectionOwners[i] = processor.getAliases().indexOf( collectionOwnerAliases.get(i) );
 //				String suffix = i + "__";
 //				collectionSuffixes.add(suffix);
 //				//collectionDescriptors[i] = new GeneratedCollectionAliases( collectionResultMaps[i], collectionPersister, suffix );
 //			}
 //		}
 //		else {
 //			collectionRoles = null;
 //			//collectionDescriptors = null;
 //			collectionOwners = null;
 //		}
 //
 //		String[] aliases = ArrayHelper.toStringArray( processor.getAliases() );
 //		String[] collAliases = ArrayHelper.toStringArray( processor.getCollectionAliases() );
 //		String[] collSuffixes = ArrayHelper.toStringArray(collectionSuffixes);
 //
 //		SQLLoadable[] entityPersisters = (SQLLoadable[]) processor.getPersisters().toArray( new SQLLoadable[0] );
 //		SQLLoadableCollection[] collPersisters = (SQLLoadableCollection[]) collectionPersisters.toArray( new SQLLoadableCollection[0] );
 //        lockModes = (LockMode[]) processor.getLockModes().toArray( new LockMode[0] );
 //
 //        scalarColumnAliases = ArrayHelper.toStringArray( processor.getScalarColumnAliases() );
 //		scalarTypes = ArrayHelper.toTypeArray( processor.getScalarTypes() );
 //
 //		// need to match the "sequence" of what we return. scalar first, entity last.
 //		returnAliases = ArrayHelper.join(scalarColumnAliases, aliases);
 //
 //		String[] suffixes = BasicLoader.generateSuffixes(entityPersisters.length);
 
 		SQLQueryParser parser = new SQLQueryParser( sqlQuery, new ParserContext( aliasContext ), factory );
 		this.sql = parser.process();
 		this.namedParameterBindPoints.putAll( parser.getNamedParameters() );
 
 //		SQLQueryParser parser = new SQLQueryParser(
 //				sqlQuery,
 //				processor.getAlias2Persister(),
 //				processor.getAlias2Return(),
 //				aliases,
 //				collAliases,
 //				collPersisters,
 //				suffixes,
 //				collSuffixes
 //		);
 //
 //		sql = parser.process();
 //
 //		namedParameterBindPoints = parser.getNamedParameters();
 
 
 		customQueryReturns.addAll( processor.generateCustomReturns( parser.queryHasAliases() ) );
 
 //		// Populate entityNames, entityDescrptors and querySpaces
 //		entityNames = new String[entityPersisters.length];
 //		entityDescriptors = new EntityAliases[entityPersisters.length];
 //		for (int i = 0; i < entityPersisters.length; i++) {
 //			SQLLoadable persister = entityPersisters[i];
 //			//alias2Persister.put( aliases[i], persister );
 //			//TODO: Does not consider any other tables referenced in the query
 //			ArrayHelper.addAll( querySpaces, persister.getQuerySpaces() );
 //			entityNames[i] = persister.getEntityName();
 //			if ( parser.queryHasAliases() ) {
 //				entityDescriptors[i] = new DefaultEntityAliases(
 //						propertyResultMaps[i],
 //						entityPersisters[i],
 //						suffixes[i]
 //					);
 //			}
 //			else {
 //				entityDescriptors[i] = new ColumnEntityAliases(
 //						propertyResultMaps[i],
 //						entityPersisters[i],
 //						suffixes[i]
 //					);
 //			}
 //		}
 		if ( additionalQuerySpaces != null ) {
 			querySpaces.addAll( additionalQuerySpaces );
 		}
 
 //		if (size!=0) {
 //			collectionDescriptors = new CollectionAliases[size];
 //			for ( int i=0; i<size; i++ ) {
 //				CollectionPersister collectionPersister = (CollectionPersister) collectionPersisters.get(i);
 //				String suffix = i + "__";
 //				if( parser.queryHasAliases() ) {
 //					collectionDescriptors[i] = new GeneratedCollectionAliases( collectionResultMaps[i], collectionPersister, suffix );
 //				} else {
 //					collectionDescriptors[i] = new ColumnCollectionAliases( collectionResultMaps[i], (SQLLoadableCollection) collectionPersister );
 //				}
 //			}
 //		}
 //		else {
 //			collectionDescriptors = null;
 //		}
 //
 //
 //		// Resolve owners
 //		Map alias2OwnerAlias = processor.getAlias2OwnerAlias();
 //		int[] ownersArray = new int[entityPersisters.length];
 //		for ( int j=0; j < aliases.length; j++ ) {
 //			String ownerAlias = (String) alias2OwnerAlias.get( aliases[j] );
 //			if ( StringHelper.isNotEmpty(ownerAlias) ) {
 //				ownersArray[j] =  processor.getAliases().indexOf( ownerAlias );
 //			}
 //			else {
 //				ownersArray[j] = -1;
 //			}
 //		}
 //		if ( ArrayHelper.isAllNegative(ownersArray) ) {
 //			ownersArray = null;
 //		}
 //		this.entityOwners = ownersArray;
 
 	}
 
 
 	private static class ParserContext implements SQLQueryParser.ParserContext {
 
 		private final SQLQueryReturnProcessor.ResultAliasContext aliasContext;
 
 		public ParserContext(SQLQueryReturnProcessor.ResultAliasContext aliasContext) {
 			this.aliasContext = aliasContext;
 		}
 
 		public boolean isEntityAlias(String alias) {
 			return getEntityPersisterByAlias( alias ) != null;
 		}
 
 		public SQLLoadable getEntityPersisterByAlias(String alias) {
 			return aliasContext.getEntityPersister( alias );
 		}
 
 		public String getEntitySuffixByAlias(String alias) {
 			return aliasContext.getEntitySuffix( alias );
 		}
 
 		public boolean isCollectionAlias(String alias) {
 			return getCollectionPersisterByAlias( alias ) != null;
 		}
 
 		public SQLLoadableCollection getCollectionPersisterByAlias(String alias) {
 			return aliasContext.getCollectionPersister( alias );
 		}
 
 		public String getCollectionSuffixByAlias(String alias) {
 			return aliasContext.getCollectionSuffix( alias );
 		}
 
 		public Map getPropertyResultsMapByAlias(String alias) {
 			return aliasContext.getPropertyResultsMap( alias );
 		}
 	}
 }
