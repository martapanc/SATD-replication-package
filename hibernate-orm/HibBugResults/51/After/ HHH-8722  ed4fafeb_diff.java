diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/AbstractLoadPlanBuildingAssociationVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/AbstractLoadPlanBuildingAssociationVisitationStrategy.java
index 2ea06b497f..f779f290bb 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/AbstractLoadPlanBuildingAssociationVisitationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/AbstractLoadPlanBuildingAssociationVisitationStrategy.java
@@ -1,957 +1,960 @@
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
 package org.hibernate.loader.plan2.build.spi;
 
 import java.util.ArrayDeque;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 import org.jboss.logging.MDC;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan2.build.internal.spaces.QuerySpacesImpl;
 import org.hibernate.loader.plan2.build.internal.returns.CollectionReturnImpl;
 import org.hibernate.loader.plan2.build.internal.returns.EntityReturnImpl;
 import org.hibernate.loader.plan2.spi.AttributeFetch;
 import org.hibernate.loader.plan2.spi.CollectionAttributeFetch;
 import org.hibernate.loader.plan2.spi.CollectionFetchableElement;
 import org.hibernate.loader.plan2.spi.CollectionFetchableIndex;
 import org.hibernate.loader.plan2.spi.CollectionReference;
 import org.hibernate.loader.plan2.spi.CollectionReturn;
 import org.hibernate.loader.plan2.spi.CompositeAttributeFetch;
 import org.hibernate.loader.plan2.spi.CompositeFetch;
 import org.hibernate.loader.plan2.spi.EntityFetch;
 import org.hibernate.loader.plan2.spi.EntityIdentifierDescription;
 import org.hibernate.loader.plan2.spi.EntityReference;
 import org.hibernate.loader.plan2.spi.FetchSource;
 import org.hibernate.loader.plan2.spi.Return;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.walking.spi.AnyMappingDefinition;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
 import org.hibernate.type.Type;
 
 /**
  * A LoadPlanBuilderStrategy is a strategy for building a LoadPlan.  LoadPlanBuilderStrategy is also a
  * AssociationVisitationStrategy, which is used in conjunction with visiting associations via walking
  * metamodel definitions.
  * <p/>
  * So this strategy defines a AssociationVisitationStrategy that walks the metamodel defined associations after
  * which is can then build a LoadPlan based on the visited associations.  {@link #determineFetchStrategy} Is the
  * main decision point
  *
  * @author Steve Ebersole
  *
  * @see org.hibernate.loader.plan2.build.spi.LoadPlanBuildingAssociationVisitationStrategy
  * @see org.hibernate.persister.walking.spi.AssociationVisitationStrategy
  */
 public abstract class AbstractLoadPlanBuildingAssociationVisitationStrategy
 		implements LoadPlanBuildingAssociationVisitationStrategy, LoadPlanBuildingContext {
 	private static final Logger log = Logger.getLogger( AbstractLoadPlanBuildingAssociationVisitationStrategy.class );
 	private static final String MDC_KEY = "hibernateLoadPlanWalkPath";
 
 	private final SessionFactoryImplementor sessionFactory;
 	private final QuerySpacesImpl querySpaces;
 
+	//TODO: I don't see propertyPathStack used anywhere. Can it be deleted?
 	private final PropertyPathStack propertyPathStack = new PropertyPathStack();
 
 	private final ArrayDeque<ExpandingFetchSource> fetchSourceStack = new ArrayDeque<ExpandingFetchSource>();
 
 	protected AbstractLoadPlanBuildingAssociationVisitationStrategy(SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 		this.querySpaces = new QuerySpacesImpl( sessionFactory );
 	}
 
 	public SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	@Override
 	public ExpandingQuerySpaces getQuerySpaces() {
 		return querySpaces;
 	}
 
 
 	// stack management ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static interface FetchStackAware {
 		public void poppedFromStack();
 	}
 
 	private void pushToStack(ExpandingFetchSource fetchSource) {
 		log.trace( "Pushing fetch source to stack : " + fetchSource );
 		propertyPathStack.push( fetchSource.getPropertyPath() );
 		fetchSourceStack.addFirst( fetchSource );
 	}
 
 	private ExpandingFetchSource popFromStack() {
 		final ExpandingFetchSource last = fetchSourceStack.removeFirst();
 		log.trace( "Popped fetch owner from stack : " + last );
 		propertyPathStack.pop();
 		if ( FetchStackAware.class.isInstance( last ) ) {
 			( (FetchStackAware) last ).poppedFromStack();
 		}
 
 		return last;
 	}
 
 	private ExpandingFetchSource currentSource() {
 		return fetchSourceStack.peekFirst();
 	}
 
 	// top-level AssociationVisitationStrategy hooks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void start() {
 		if ( ! fetchSourceStack.isEmpty() ) {
 			throw new WalkingException(
 					"Fetch owner stack was not empty on start; " +
 							"be sure to not use LoadPlanBuilderStrategy instances concurrently"
 			);
 		}
 		propertyPathStack.push( new PropertyPath() );
 	}
 
 	@Override
 	public void finish() {
 		propertyPathStack.pop();
 		MDC.remove( MDC_KEY );
 		fetchSourceStack.clear();
 	}
 
 
 	protected abstract void addRootReturn(Return rootReturn);
 
 
 	// Entity-level AssociationVisitationStrategy hooks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected boolean supportsRootEntityReturns() {
 		return true;
 	}
 
 	// Entities  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void startingEntity(EntityDefinition entityDefinition) {
 		log.tracef(
 				"%s Starting entity : %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				entityDefinition.getEntityPersister().getEntityName()
 		);
 
 		// see if the EntityDefinition is a root...
 		final boolean isRoot = fetchSourceStack.isEmpty();
 		if ( ! isRoot ) {
 			// if not, this call should represent a fetch which should have been handled in #startingAttribute
 			return;
 		}
 
 		// if we get here, it is a root
 		if ( !supportsRootEntityReturns() ) {
 			throw new HibernateException( "This strategy does not support root entity returns" );
 		}
 
 		final EntityReturnImpl entityReturn = new EntityReturnImpl( entityDefinition, querySpaces );
 		addRootReturn( entityReturn );
 		pushToStack( entityReturn );
 
 		// also add an AssociationKey for the root so we can later on recognize circular references back to the root.
 		final Joinable entityPersister = (Joinable) entityDefinition.getEntityPersister();
 		associationKeyRegistered(
 				new AssociationKey( entityPersister.getTableName(), entityPersister.getKeyColumnNames() )
 		);
 	}
 
 	@Override
 	public void finishingEntity(EntityDefinition entityDefinition) {
 		final boolean isRoot = fetchSourceStack.size() == 1 && collectionReferenceStack.isEmpty();
 		if ( !isRoot ) {
 			return;
 		}
 
 		popEntityFromStack( entityDefinition );
 	}
 
 	private void popEntityFromStack(EntityDefinition entityDefinition) {
 		// pop the current fetch owner, and make sure what we just popped represents this entity
 		final ExpandingFetchSource fetchSource = popFromStack();
 
 		if ( ! EntityReference.class.isInstance( fetchSource ) ) {
 			throw new WalkingException(
 					String.format(
 							"Mismatched FetchSource from stack on pop.  Expecting EntityReference(%s), but found %s",
 							entityDefinition.getEntityPersister().getEntityName(),
 							fetchSource
 					)
 			);
 		}
 
 		final EntityReference entityReference = (EntityReference) fetchSource;
 		// NOTE : this is not the most exhaustive of checks because of hierarchical associations (employee/manager)
 		if ( ! entityReference.getEntityPersister().equals( entityDefinition.getEntityPersister() ) ) {
 			throw new WalkingException( "Mismatched FetchSource from stack on pop" );
 		}
 
 		log.tracef(
 				"%s Finished entity : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				entityDefinition.getEntityPersister().getEntityName()
 		);
 	}
 
 
 	// entity identifiers ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 		log.tracef(
 				"%s Starting entity identifier : %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 		);
 
 		final EntityReference entityReference = (EntityReference) currentSource();
 
 		// perform some stack validation
 		if ( ! entityReference.getEntityPersister().equals( entityIdentifierDefinition.getEntityDefinition().getEntityPersister() ) ) {
 			throw new WalkingException(
 					String.format(
 							"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
 							entityReference.getEntityPersister().getEntityName(),
 							entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 					)
 			);
 		}
 
 		// todo : handle AssociationKeys here?  is that why we get the duplicate joins and fetches?
 
 		if ( ExpandingEntityIdentifierDescription.class.isInstance( entityReference.getIdentifierDescription() ) ) {
 			pushToStack( (ExpandingEntityIdentifierDescription) entityReference.getIdentifierDescription() );
 		}
 	}
 
 	@Override
 	public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 		// peek at the current stack element...
 		final ExpandingFetchSource current = currentSource();
 		if ( ! EntityIdentifierDescription.class.isInstance( current ) ) {
 			return;
 		}
 
 		final ExpandingFetchSource popped = popFromStack();
 
 		// perform some stack validation on exit, first on the current stack element we want to pop
 		if ( ! ExpandingEntityIdentifierDescription.class.isInstance( popped ) ) {
 			throw new WalkingException( "Unexpected state in FetchSource stack" );
 		}
 
 		final ExpandingEntityIdentifierDescription identifierDescription = (ExpandingEntityIdentifierDescription) popped;
 
 		// and then on the node before it (which should be the entity that owns the identifier being described)
 		final ExpandingFetchSource entitySource = currentSource();
 		if ( ! EntityReference.class.isInstance( entitySource ) ) {
 			throw new WalkingException( "Unexpected state in FetchSource stack" );
 		}
 		final EntityReference entityReference = (EntityReference) entitySource;
 		if ( entityReference.getIdentifierDescription() != identifierDescription ) {
 			throw new WalkingException(
 					String.format(
 							"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
 							entityReference.getEntityPersister().getEntityName(),
 							entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 					)
 			);
 		}
 
 		log.tracef(
 				"%s Finished entity identifier : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 		);
 	}
 
 
 	// Collections ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private ArrayDeque<CollectionReference> collectionReferenceStack = new ArrayDeque<CollectionReference>();
 
 	private void pushToCollectionStack(CollectionReference collectionReference) {
 		log.trace( "Pushing collection reference to stack : " + collectionReference );
 		propertyPathStack.push( collectionReference.getPropertyPath() );
 		collectionReferenceStack.addFirst( collectionReference );
 	}
 
 	private CollectionReference popFromCollectionStack() {
 		final CollectionReference last = collectionReferenceStack.removeFirst();
 		log.trace( "Popped collection reference from stack : " + last );
 		propertyPathStack.pop();
 		if ( FetchStackAware.class.isInstance( last ) ) {
 			( (FetchStackAware) last ).poppedFromStack();
 		}
 		return last;
 	}
 
 	@Override
 	public void startingCollection(CollectionDefinition collectionDefinition) {
 		log.tracef(
 				"%s Starting collection : %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				collectionDefinition.getCollectionPersister().getRole()
 		);
 
 		// see if the EntityDefinition is a root...
 		final boolean isRoot = fetchSourceStack.isEmpty();
 		if ( ! isRoot ) {
 			// if not, this call should represent a fetch which should have been handled in #startingAttribute
 			return;
 		}
 
 		// if we get here, it is a root
 		if ( ! supportsRootCollectionReturns() ) {
 			throw new HibernateException( "This strategy does not support root collection returns" );
 		}
 
 		final CollectionReturn collectionReturn = new CollectionReturnImpl( collectionDefinition, querySpaces );
 		pushToCollectionStack( collectionReturn );
 		addRootReturn( collectionReturn );
 
 		//if ( collectionDefinition.getCollectionPersister().isOneToMany() ) {
 			associationKeyRegistered(
 					new AssociationKey(
 							( (Joinable) collectionDefinition.getCollectionPersister() ).getTableName(),
 							( (Joinable) collectionDefinition.getCollectionPersister() ).getKeyColumnNames()
 					)
 			);
 		//}
 
 		// also add an AssociationKey for the root so we can later on recognize circular references back to the root.
 		// for a collection, the circularity would always be to an entity element...
 		/*
 		if ( collectionReturn.getElementGraph() != null ) {
 			if ( EntityReference.class.isInstance( collectionReturn.getElementGraph() ) ) {
 				final EntityReference entityReference = (EntityReference) collectionReturn.getElementGraph();
 				final Joinable entityPersister = (Joinable) entityReference.getEntityPersister();
 				associationKeyRegistered(
 						new AssociationKey( entityPersister.getTableName(), entityPersister.getKeyColumnNames() )
 				);
 			}
 		}
 		*/
 	}
 
 	protected boolean supportsRootCollectionReturns() {
 		return true;
 	}
 
 	@Override
 	public void finishingCollection(CollectionDefinition collectionDefinition) {
 		final boolean isRoot = fetchSourceStack.isEmpty() && collectionReferenceStack.size() == 1;
 		if ( !isRoot ) {
 			return;
 		}
 
 		popFromCollectionStack( collectionDefinition );
 	}
 
 	private void popFromCollectionStack(CollectionDefinition collectionDefinition) {
 		// pop the current fetch owner, and make sure what we just popped represents this collection
 		final CollectionReference collectionReference = popFromCollectionStack();
 		if ( ! collectionReference.getCollectionPersister().equals( collectionDefinition.getCollectionPersister() ) ) {
 			throw new WalkingException( "Mismatched CollectionReference from stack on pop" );
 		}
 
 		log.tracef(
 				"%s Finished collection : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				collectionDefinition.getCollectionPersister().getRole()
 		);
 	}
 
 	@Override
 	public void startingCollectionIndex(CollectionIndexDefinition indexDefinition) {
 		final Type indexType = indexDefinition.getType();
 		if ( indexType.isAnyType() ) {
 			return;
 		}
 
 		log.tracef(
 				"%s Starting collection index graph : %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				indexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 		);
 
 		final CollectionReference collectionReference = collectionReferenceStack.peekFirst();
 		final CollectionFetchableIndex indexGraph = collectionReference.getIndexGraph();
 
 		if ( indexType.isEntityType() || indexType.isComponentType() ) {
 			if ( indexGraph == null ) {
 				throw new WalkingException(
 						"CollectionReference did not return an expected index graph : " +
 								indexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				);
 			}
 			pushToStack( (ExpandingFetchSource) indexGraph );
 		}
 		else {
 			if ( indexGraph != null ) {
 				throw new WalkingException(
 						"CollectionReference returned an unexpected index graph : " +
 								indexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				);
 			}
 		}
 	}
 
 	@Override
 	public void finishingCollectionIndex(CollectionIndexDefinition indexDefinition) {
 		final Type indexType = indexDefinition.getType();
 
 		if ( indexType.isAnyType() ) {
 			return;
 		}
 
 		if ( indexType.isEntityType() || indexType.isComponentType() ) {
 			// todo : validate the stack?
 			final ExpandingFetchSource fetchSource = popFromStack();
 			if ( !CollectionFetchableIndex.class.isInstance( fetchSource ) ) {
 				throw new WalkingException(
 						"CollectionReference did not return an expected index graph : " +
 								indexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				);
 			}
 		}
 
 		log.tracef(
 				"%s Finished collection index graph : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				indexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 		);
 	}
 
 	@Override
 	public void startingCollectionElements(CollectionElementDefinition elementDefinition) {
 		final Type elementType = elementDefinition.getType();
 		log.tracef(
 				"%s Starting collection element graph : %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				elementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 		);
 
 		final CollectionReference collectionReference = collectionReferenceStack.peekFirst();
 		final CollectionFetchableElement elementGraph = collectionReference.getElementGraph();
 
 		if ( elementType.isAssociationType() || elementType.isComponentType() ) {
 			if ( elementGraph == null ) {
 				throw new IllegalStateException(
 						"CollectionReference did not return an expected element graph : " +
 								elementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				);
 			}
 			if ( !elementType.isAnyType() ) {
 				pushToStack( (ExpandingFetchSource) elementGraph );
 			}
 		}
 		else {
 			if ( elementGraph != null ) {
 				throw new IllegalStateException(
 						"CollectionReference returned an unexpected element graph : " +
 								elementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				);
 			}
 		}
 	}
 
 	@Override
 	public void finishingCollectionElements(CollectionElementDefinition elementDefinition) {
 		final Type elementType = elementDefinition.getType();
 
 		if ( elementType.isAnyType() ) {
 			// nothing to do because the element graph was not pushed
 		}
 		else if ( elementType.isComponentType() || elementType.isAssociationType()) {
 			// pop it from the stack
 			final ExpandingFetchSource popped = popFromStack();
 
 			// validation
 			if ( ! CollectionFetchableElement.class.isInstance( popped ) ) {
 				throw new WalkingException( "Mismatched FetchSource from stack on pop" );
 			}
 		}
 
 		log.tracef(
 				"%s Finished collection element graph : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				elementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 		);
 	}
 
 	@Override
 	public void startingComposite(CompositionDefinition compositionDefinition) {
 		log.tracef(
 				"%s Starting composite : %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				compositionDefinition.getName()
 		);
 
 		if ( fetchSourceStack.isEmpty() && collectionReferenceStack.isEmpty() ) {
 			throw new HibernateException( "A component cannot be the root of a walk nor a graph" );
 		}
 
 		//final CompositeFetch compositeFetch = currentSource().buildCompositeFetch( compositionDefinition );
 		//pushToStack( (ExpandingFetchSource) compositeFetch );
 	}
 
 	@Override
 	public void finishingComposite(CompositionDefinition compositionDefinition) {
 		// pop the current fetch owner, and make sure what we just popped represents this composition
 		//final ExpandingFetchSource popped = popFromStack();
 
 		//if ( ! CompositeFetch.class.isInstance( popped ) ) {
 		//	throw new WalkingException( "Mismatched FetchSource from stack on pop" );
 		//}
 
 		log.tracef(
 				"%s Finishing composite : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				compositionDefinition.getName()
 		);
 	}
 	protected PropertyPath currentPropertyPath = new PropertyPath( "" );
 	@Override
 	public boolean startingAttribute(AttributeDefinition attributeDefinition) {
 		log.tracef(
 				"%s Starting attribute %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				attributeDefinition
 		);
 
 		final Type attributeType = attributeDefinition.getType();
 
 		final boolean isAnyType = attributeType.isAnyType();
 		final boolean isComponentType = attributeType.isComponentType();
 		final boolean isAssociationType = attributeType.isAssociationType();
 		final boolean isBasicType = ! ( isComponentType || isAssociationType );
 		currentPropertyPath = currentPropertyPath.append( attributeDefinition.getName() );
 		if ( isBasicType ) {
 			return true;
 		}
 		else if ( isAnyType ) {
 			// If isAnyType is true, then isComponentType and isAssociationType will also be true
 			// so need to check isAnyType first so that it is handled properly.
 			return handleAnyAttribute( (AssociationAttributeDefinition) attributeDefinition );
 		}
 		else if ( isAssociationType ) {
 			return handleAssociationAttribute( (AssociationAttributeDefinition) attributeDefinition );
 		}
 		else {
 			return handleCompositeAttribute( attributeDefinition );
 		}
 	}
 
 	@Override
 	public void finishingAttribute(AttributeDefinition attributeDefinition) {
 		final Type attributeType = attributeDefinition.getType();
 
 		if ( attributeType.isAnyType() ) {
 			// If attributeType.isAnyType() is true, then attributeType.isComponentType() and
 			// attributeType.isAssociationType() will also be true, so need to
 			// check attributeType.isAnyType() first.
 
 			// Nothing to do because AnyFetch does not implement ExpandingFetchSource (i.e., it cannot be pushed/popped).
 		}
 		else if ( attributeType.isComponentType() ) {
 			// CompositeFetch is always pushed, during #startingAttribute(),
 			// so pop the current fetch owner, and make sure what we just popped represents this composition
 			final ExpandingFetchSource popped = popFromStack();
 			if ( !CompositeAttributeFetch.class.isInstance( popped ) ) {
 				throw new WalkingException(
 						String.format(
 								"Mismatched FetchSource from stack on pop; expected: CompositeAttributeFetch; actual: [%s]",
 								popped
 						)
 				);
 			}
 			final CompositeAttributeFetch poppedAsCompositeAttributeFetch = (CompositeAttributeFetch) popped;
 			if ( !attributeDefinition.equals( poppedAsCompositeAttributeFetch.getFetchedAttributeDefinition() ) ) {
 				throw new WalkingException(
 						String.format(
 								"Mismatched CompositeAttributeFetch from stack on pop; expected fetch for attribute: [%s]; actual: [%s]",
 								attributeDefinition,
 								poppedAsCompositeAttributeFetch.getFetchedAttributeDefinition()
 						)
 				);
 			}
 		}
 		else if ( attributeType.isEntityType() ) {
 			final ExpandingFetchSource source = currentSource();
 			if ( AttributeFetch.class.isInstance( source ) &&
 					attributeDefinition.equals( AttributeFetch.class.cast( source ).getFetchedAttributeDefinition() ) ) {
 				popEntityFromStack( ( (AssociationAttributeDefinition) attributeDefinition ).toEntityDefinition() );
 			}
 		}
 		else if ( attributeType.isCollectionType() ) {
 			final CollectionReference currentCollection = collectionReferenceStack.peekFirst();
 			if ( currentCollection != null &&
 					AttributeFetch.class.isInstance( currentCollection ) &&
 					attributeDefinition.equals( AttributeFetch.class.cast( currentCollection ).getFetchedAttributeDefinition() ) ) {
 				popFromCollectionStack( ( (AssociationAttributeDefinition) attributeDefinition ).toCollectionDefinition() );
 			}
 		}
 
 		log.tracef(
 				"%s Finishing up attribute : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				attributeDefinition
 		);
 		currentPropertyPath = currentPropertyPath.getParent();
 	}
 
 	private Map<AssociationKey,FetchSource> fetchedAssociationKeySourceMap = new HashMap<AssociationKey, FetchSource>();
 
 	@Override
 	public boolean isDuplicateAssociationKey(AssociationKey associationKey) {
 		return fetchedAssociationKeySourceMap.containsKey( associationKey );
 	}
 
 	@Override
 	public void associationKeyRegistered(AssociationKey associationKey) {
 		// todo : use this information to maintain a map of AssociationKey->FetchSource mappings (associationKey + current FetchSource stack entry)
 		//		that mapping can then be used in #foundCircularAssociationKey to build the proper BiDirectionalEntityFetch
 		//		based on the mapped owner
 		log.tracef(
 				"%s Registering AssociationKey : %s -> %s",
 				StringHelper.repeat( "..", fetchSourceStack.size() ),
 				associationKey,
 				currentSource()
 		);
 		fetchedAssociationKeySourceMap.put( associationKey, currentSource() );
 	}
 
 	@Override
 	public FetchSource registeredFetchSource(AssociationKey associationKey) {
 		return fetchedAssociationKeySourceMap.get( associationKey );
 	}
 
 	@Override
 	public void foundCircularAssociation(AssociationAttributeDefinition attributeDefinition) {
 		final FetchStrategy fetchStrategy = determineFetchStrategy( attributeDefinition );
 		if ( fetchStrategy.getStyle() != FetchStyle.JOIN ) {
 			return; // nothing to do
 		}
 
 		final AssociationKey associationKey = attributeDefinition.getAssociationKey();
 
 		// go ahead and build the bidirectional fetch
 		if ( attributeDefinition.getAssociationNature() == AssociationAttributeDefinition.AssociationNature.ENTITY ) {
 			final Joinable currentEntityPersister = (Joinable) currentSource().resolveEntityReference().getEntityPersister();
 			final AssociationKey currentEntityReferenceAssociationKey =
 					new AssociationKey( currentEntityPersister.getTableName(), currentEntityPersister.getKeyColumnNames() );
 			// if associationKey is equal to currentEntityReferenceAssociationKey
 			// that means that the current EntityPersister has a single primary key attribute
 			// (i.e., derived attribute) which is mapped by attributeDefinition.
 			// This is not a bidirectional association.
 			// TODO: AFAICT, to avoid an overflow, the associated entity must already be loaded into the session, or
 			// it must be loaded when the ID for the dependent entity is resolved. Is there some other way to
 			// deal with this???
 			final FetchSource registeredFetchSource = registeredFetchSource( associationKey );
 			if ( registeredFetchSource != null && ! associationKey.equals( currentEntityReferenceAssociationKey ) ) {
 				currentSource().buildBidirectionalEntityReference(
 						attributeDefinition,
 						fetchStrategy,
 						registeredFetchSource( associationKey ).resolveEntityReference()
 				);
 			}
 		}
 		else {
 			// Collection
 			//currentSource().buildCollectionFetch( attributeDefinition, fetchStrategy, this );
 		}
 	}
 
 //	@Override
 //	public void foundCircularAssociationKey(AssociationKey associationKey, AttributeDefinition attributeDefinition) {
 //		// use this information to create the bi-directional EntityReference (as EntityFetch) instances
 //		final FetchSource owningFetchSource = fetchedAssociationKeySourceMap.get( associationKey );
 //		if ( owningFetchSource == null ) {
 //			throw new IllegalStateException(
 //					String.format(
 //							"Expecting AssociationKey->FetchSource mapping for %s",
 //							associationKey.toString()
 //					)
 //			);
 //		}
 //
 //		final FetchSource currentFetchSource = currentSource();
 //		( (ExpandingFetchSource) currentFetchSource ).addCircularFetch( new CircularFetch(  ))
 //
 //		currentFetchOwner().addFetch( new CircularFetch( currentSource(), fetchSource, attributeDefinition ) );
 //	}
 //
 //	public static class CircularFetch implements EntityFetch, EntityReference {
 //		private final FetchOwner circularFetchOwner;
 //		private final FetchOwner associationOwner;
 //		private final AttributeDefinition attributeDefinition;
 //
 //		private final EntityReference targetEntityReference;
 //
 //		private final FetchStrategy fetchStrategy = new FetchStrategy(
 //				FetchTiming.IMMEDIATE,
 //				FetchStyle.JOIN
 //		);
 //
 //		public CircularFetch(FetchOwner circularFetchOwner, FetchOwner associationOwner, AttributeDefinition attributeDefinition) {
 //			this.circularFetchOwner = circularFetchOwner;
 //			this.associationOwner = associationOwner;
 //			this.attributeDefinition = attributeDefinition;
 //			this.targetEntityReference = resolveEntityReference( associationOwner );
 //		}
 //
 //		@Override
 //		public EntityReference getTargetEntityReference() {
 //			return targetEntityReference;
 //		}
 //
 //		protected static EntityReference resolveEntityReference(FetchOwner owner) {
 //			if ( EntityReference.class.isInstance( owner ) ) {
 //				return (EntityReference) owner;
 //			}
 //			if ( CompositeFetch.class.isInstance( owner ) ) {
 //				return resolveEntityReference( ( (CompositeFetch) owner ).getOwner() );
 //			}
 //			// todo : what others?
 //
 //			throw new UnsupportedOperationException(
 //					"Unexpected FetchOwner type [" + owner + "] encountered trying to build circular fetch"
 //			);
 //
 //		}
 //
 //		@Override
 //		public FetchOwner getSource() {
 //			return circularFetchOwner;
 //		}
 //
 //		@Override
 //		public PropertyPath getPropertyPath() {
 //			return null;  //To change body of implemented methods use File | Settings | File Templates.
 //		}
 //
 //		@Override
 //		public Type getFetchedType() {
 //			return attributeDefinition.getType();
 //		}
 //
 //		@Override
 //		public FetchStrategy getFetchStrategy() {
 //			return fetchStrategy;
 //		}
 //
 //		@Override
 //		public boolean isNullable() {
 //			return attributeDefinition.isNullable();
 //		}
 //
 //		@Override
 //		public String getAdditionalJoinConditions() {
 //			return null;
 //		}
 //
 //		@Override
 //		public String[] toSqlSelectFragments(String alias) {
 //			return new String[0];
 //		}
 //
 //		@Override
 //		public Fetch makeCopy(CopyContext copyContext, FetchOwner fetchSourceCopy) {
 //			// todo : will need this implemented
 //			return null;
 //		}
 //
 //		@Override
 //		public LockMode getLockMode() {
 //			return targetEntityReference.getLockMode();
 //		}
 //
 //		@Override
 //		public EntityReference getEntityReference() {
 //			return targetEntityReference;
 //		}
 //
 //		@Override
 //		public EntityPersister getEntityPersister() {
 //			return targetEntityReference.getEntityPersister();
 //		}
 //
 //		@Override
 //		public IdentifierDescription getIdentifierDescription() {
 //			return targetEntityReference.getIdentifierDescription();
 //		}
 //
 //		@Override
 //		public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
 //			throw new IllegalStateException( "IdentifierDescription should never be injected from circular fetch side" );
 //		}
 //	}
 
 	@Override
 	public void foundAny(AnyMappingDefinition anyDefinition) {
 		// do nothing.
 	}
 
 	protected boolean handleAnyAttribute(AssociationAttributeDefinition attributeDefinition) {
 		// for ANY mappings we need to build a Fetch:
 		//		1) fetch type is SELECT, timing might be IMMEDIATE or DELAYED depending on whether it was defined as lazy
 		//		2) (because the fetch cannot be a JOIN...) do not push it to the stack
 		final FetchStrategy fetchStrategy = determineFetchStrategy( attributeDefinition );
 		if ( fetchStrategy.getTiming() != FetchTiming.IMMEDIATE ) {
 			return false;
 		}
 
 		final ExpandingFetchSource currentSource = currentSource();
 		currentSource.validateFetchPlan( fetchStrategy, attributeDefinition );
 
 //		final FetchOwner fetchSource = currentFetchOwner();
 //		fetchOwner.validateFetchPlan( fetchStrategy, attributeDefinition );
 //
 //		fetchOwner.buildAnyFetch(
 //				attributeDefinition,
 //				anyDefinition,
 //				fetchStrategy,
 //				this
 //		);
 		return false;
 	}
 
 	protected boolean handleCompositeAttribute(AttributeDefinition attributeDefinition) {
 		final CompositeFetch compositeFetch = currentSource().buildCompositeAttributeFetch( attributeDefinition );
 		pushToStack( (ExpandingFetchSource) compositeFetch );
 		return true;
 	}
 
 	protected boolean handleAssociationAttribute(AssociationAttributeDefinition attributeDefinition) {
 		// todo : this seems to not be correct for one-to-one
 		final FetchStrategy fetchStrategy = determineFetchStrategy( attributeDefinition );
 		if ( fetchStrategy.getTiming() != FetchTiming.IMMEDIATE ) {
 			return false;
 		}
 
 		final ExpandingFetchSource currentSource = currentSource();
 		currentSource.validateFetchPlan( fetchStrategy, attributeDefinition );
 
 		final AssociationAttributeDefinition.AssociationNature nature = attributeDefinition.getAssociationNature();
 		if ( nature == AssociationAttributeDefinition.AssociationNature.ANY ) {
 			currentSource.buildAnyAttributeFetch(
 					attributeDefinition,
 					fetchStrategy
 			);
 			return false;
 		}
 		else if ( nature == AssociationAttributeDefinition.AssociationNature.ENTITY ) {
 			EntityFetch fetch = currentSource.buildEntityAttributeFetch(
 					attributeDefinition,
 					fetchStrategy
 			);
 			if ( fetchStrategy.getStyle() == FetchStyle.JOIN ) {
 				pushToStack( (ExpandingFetchSource) fetch );
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		else {
 			// Collection
 			CollectionAttributeFetch fetch = currentSource.buildCollectionAttributeFetch( attributeDefinition, fetchStrategy );
 			if ( fetchStrategy.getStyle() == FetchStyle.JOIN ) {
 				pushToCollectionStack( fetch );
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 	}
 
 	protected abstract FetchStrategy determineFetchStrategy(AssociationAttributeDefinition attributeDefinition);
 
 	protected int currentDepth() {
 		return fetchSourceStack.size();
 	}
 
 	protected boolean isTooManyCollections() {
 		return false;
 	}
 
 //	protected abstract EntityReturn buildRootEntityReturn(EntityDefinition entityDefinition);
 //
 //	protected abstract CollectionReturn buildRootCollectionReturn(CollectionDefinition collectionDefinition);
 
 
 
 	// LoadPlanBuildingContext impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory();
 	}
 
 	/**
 	 * Maintains stack information for the property paths we are processing for logging purposes.  Because of the
 	 * recursive calls it is often useful (while debugging) to be able to see the "property path" as part of the
 	 * logging output.
+	 *
+	 * TODO: I don't see PropertyPathStack used anywhere. Can it be deleted?
 	 */
 	public static class PropertyPathStack {
 		private ArrayDeque<PropertyPath> pathStack = new ArrayDeque<PropertyPath>();
 
 		public void push(PropertyPath path) {
 			pathStack.addFirst( path );
 			MDC.put( MDC_KEY, extractFullPath( path ) );
 		}
 
 		private String extractFullPath(PropertyPath path) {
 			return path == null ? "<no-path>" : path.getFullPath();
 		}
 
 		public void pop() {
 			pathStack.removeFirst();
 			PropertyPath newHead = pathStack.peekFirst();
 			MDC.put( MDC_KEY, extractFullPath( newHead ) );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index ce5f1015a2..b064f4d773 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1015,1121 +1015,1130 @@ public abstract class AbstractCollectionPersister
 	}
 
 	protected String generateSelectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumns( getElementColumnNames(), elementColumnAliases )
 				.addColumns( indexFormulas, indexColumnAliases )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByElementString() {
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getElementColumnNames(), "=?" )
 				.addCondition( elementFormulas, "=?" )
 				.addColumn( "1" )
 				.toStatementString();
 	}
 
 	protected SelectFragment generateSelectFragment(String alias, String columnSuffix) {
 		return new SelectFragment()
 				.setSuffix( columnSuffix )
 				.addColumns( alias, keyColumnNames, keyColumnAliases );
 	}
 
 	protected void appendElementColumns(SelectFragment frag, String elemAlias) {
 		for ( int i = 0; i < elementColumnIsSettable.length; i++ ) {
 			if ( elementColumnIsSettable[i] ) {
 				frag.addColumnTemplate( elemAlias, elementColumnReaderTemplates[i], elementColumnAliases[i] );
 			}
 			else {
 				frag.addFormula( elemAlias, elementFormulaTemplates[i], elementColumnAliases[i] );
 			}
 		}
 	}
 
 	protected void appendIndexColumns(SelectFragment frag, String alias) {
 		if ( hasIndex ) {
 			for ( int i = 0; i < indexColumnIsSettable.length; i++ ) {
 				if ( indexColumnIsSettable[i] ) {
 					frag.addColumn( alias, indexColumnNames[i], indexColumnAliases[i] );
 				}
 				else {
 					frag.addFormula( alias, indexFormulaTemplates[i], indexColumnAliases[i] );
 				}
 			}
 		}
 	}
 
 	protected void appendIdentifierColumns(SelectFragment frag, String alias) {
 		if ( hasIdentifier ) {
 			frag.addColumn( alias, identifierColumnName, identifierColumnAlias );
 		}
 	}
 
 	public String[] getIndexColumnNames() {
 		return indexColumnNames;
 	}
 
 	public String[] getIndexFormulas() {
 		return indexFormulas;
 	}
 
 	public String[] getIndexColumnNames(String alias) {
 		return qualify( alias, indexColumnNames, indexFormulaTemplates );
 
 	}
 
 	public String[] getElementColumnNames(String alias) {
 		return qualify( alias, elementColumnNames, elementFormulaTemplates );
 	}
 
 	private static String[] qualify(String alias, String[] columnNames, String[] formulaTemplates) {
 		int span = columnNames.length;
 		String[] result = new String[span];
 		for ( int i = 0; i < span; i++ ) {
 			if ( columnNames[i] == null ) {
 				result[i] = StringHelper.replace( formulaTemplates[i], Template.TEMPLATE, alias );
 			}
 			else {
 				result[i] = StringHelper.qualify( alias, columnNames[i] );
 			}
 		}
 		return result;
 	}
 
 	public String[] getElementColumnNames() {
 		return elementColumnNames; // TODO: something with formulas...
 	}
 
 	public String[] getKeyColumnNames() {
 		return keyColumnNames;
 	}
 
 	public boolean hasIndex() {
 		return hasIndex;
 	}
 
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public boolean isInverse() {
 		return isInverse;
 	}
 
 	public String getTableName() {
 		return qualifiedTableName;
 	}
 
 	private BasicBatchKey removeBatchKey;
 
 	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Deleting collection: %s",
 						MessageHelper.collectionInfoString( this, id, getFactory() ) );
 			}
 
 			// Remove all the old entries
 
 			try {
 				int offset = 1;
 				PreparedStatement st = null;
 				Expectation expectation = Expectations.appropriateExpectation( getDeleteAllCheckStyle() );
 				boolean callable = isDeleteAllCallable();
 				boolean useBatch = expectation.canBeBatched();
 				String sql = getSQLDeleteString();
 				if ( useBatch ) {
 					if ( removeBatchKey == null ) {
 						removeBatchKey = new BasicBatchKey(
 								getRole() + "#REMOVE",
 								expectation
 								);
 					}
 					st = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getBatch( removeBatchKey )
 							.getBatchStatement( sql, callable );
 				}
 				else {
 					st = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql, callable );
 				}
 
 				try {
 					offset += expectation.prepare( st );
 
 					writeKey( st, id, offset, session );
 					if ( useBatch ) {
 						session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getBatch( removeBatchKey )
 								.addToBatch();
 					}
 					else {
 						expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 					}
 				}
 				catch ( SQLException sqle ) {
 					if ( useBatch ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 					}
 					throw sqle;
 				}
 				finally {
 					if ( !useBatch ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 					}
 				}
 
 				LOG.debug( "Done deleting collection" );
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not delete collection: " +
 								MessageHelper.collectionInfoString( this, id, getFactory() ),
 						getSQLDeleteString()
 						);
 			}
 
 		}
 
 	}
 
 	protected BasicBatchKey recreateBatchKey;
 
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Inserting collection: %s",
 						MessageHelper.collectionInfoString( this, collection, id, session ) );
 			}
 
 			try {
 				// create all the new entries
 				Iterator entries = collection.entries( this );
 				if ( entries.hasNext() ) {
 					Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 					collection.preInsert( this );
 					int i = 0;
 					int count = 0;
 					while ( entries.hasNext() ) {
 
 						final Object entry = entries.next();
 						if ( collection.entryExists( entry, i ) ) {
 							int offset = 1;
 							PreparedStatement st = null;
 							boolean callable = isInsertCallable();
 							boolean useBatch = expectation.canBeBatched();
 							String sql = getSQLInsertRowString();
 
 							if ( useBatch ) {
 								if ( recreateBatchKey == null ) {
 									recreateBatchKey = new BasicBatchKey(
 											getRole() + "#RECREATE",
 											expectation
 											);
 								}
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( recreateBatchKey )
 										.getBatchStatement( sql, callable );
 							}
 							else {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getStatementPreparer()
 										.prepareStatement( sql, callable );
 							}
 
 							try {
 								offset += expectation.prepare( st );
 
 								// TODO: copy/paste from insertRows()
 								int loc = writeKey( st, id, offset, session );
 								if ( hasIdentifier ) {
 									loc = writeIdentifier( st, collection.getIdentifier( entry, i ), loc, session );
 								}
 								if ( hasIndex /* && !indexIsFormula */) {
 									loc = writeIndex( st, collection.getIndex( entry, i, this ), loc, session );
 								}
 								loc = writeElement( st, collection.getElement( entry ), loc, session );
 
 								if ( useBatch ) {
 									session.getTransactionCoordinator()
 											.getJdbcCoordinator()
 											.getBatch( recreateBatchKey )
 											.addToBatch();
 								}
 								else {
 									expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 								}
 
 								collection.afterRowInsert( this, entry, i );
 								count++;
 							}
 							catch ( SQLException sqle ) {
 								if ( useBatch ) {
 									session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 								}
 								throw sqle;
 							}
 							finally {
 								if ( !useBatch ) {
 									session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 								}
 							}
 
 						}
 						i++;
 					}
 
 					LOG.debugf( "Done inserting collection: %s rows inserted", count );
 
 				}
 				else {
 					LOG.debug( "Collection was empty" );
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not insert collection: " +
 								MessageHelper.collectionInfoString( this, collection, id, session ),
 						getSQLInsertRowString()
 						);
 			}
 		}
 	}
 
 	protected boolean isRowDeleteEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	public void deleteRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Deleting rows of collection: %s",
 						MessageHelper.collectionInfoString( this, collection, id, session ) );
 			}
 
 			boolean deleteByIndex = !isOneToMany() && hasIndex && !indexContainsFormula;
 			final Expectation expectation = Expectations.appropriateExpectation( getDeleteCheckStyle() );
 			try {
 				// delete all the deleted entries
 				Iterator deletes = collection.getDeletes( this, !deleteByIndex );
 				if ( deletes.hasNext() ) {
 					int offset = 1;
 					int count = 0;
 					while ( deletes.hasNext() ) {
 						PreparedStatement st = null;
 						boolean callable = isDeleteCallable();
 						boolean useBatch = expectation.canBeBatched();
 						String sql = getSQLDeleteRowString();
 
 						if ( useBatch ) {
 							if ( deleteBatchKey == null ) {
 								deleteBatchKey = new BasicBatchKey(
 										getRole() + "#DELETE",
 										expectation
 										);
 							}
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getBatch( deleteBatchKey )
 									.getBatchStatement( sql, callable );
 						}
 						else {
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getStatementPreparer()
 									.prepareStatement( sql, callable );
 						}
 
 						try {
 							expectation.prepare( st );
 
 							Object entry = deletes.next();
 							int loc = offset;
 							if ( hasIdentifier ) {
 								writeIdentifier( st, entry, loc, session );
 							}
 							else {
 								loc = writeKey( st, id, loc, session );
 								if ( deleteByIndex ) {
 									writeIndexToWhere( st, entry, loc, session );
 								}
 								else {
 									writeElementToWhere( st, entry, loc, session );
 								}
 							}
 
 							if ( useBatch ) {
 								session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( deleteBatchKey )
 										.addToBatch();
 							}
 							else {
 								expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 							}
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 							}
 							throw sqle;
 						}
 						finally {
 							if ( !useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 							}
 						}
 
 						LOG.debugf( "Done deleting collection rows: %s deleted", count );
 					}
 				}
 				else {
 					LOG.debug( "No rows to delete" );
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not delete collection rows: " +
 								MessageHelper.collectionInfoString( this, collection, id, session ),
 						getSQLDeleteRowString()
 						);
 			}
 		}
 	}
 
 	protected boolean isRowInsertEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey insertBatchKey;
 
 	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) LOG.debugf( "Inserting rows of collection: %s",
 					MessageHelper.collectionInfoString( this, collection, id, session ) );
 
 			try {
 				// insert all the new entries
 				collection.preInsert( this );
 				Iterator entries = collection.entries( this );
 				Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 				boolean callable = isInsertCallable();
 				boolean useBatch = expectation.canBeBatched();
 				String sql = getSQLInsertRowString();
 				int i = 0;
 				int count = 0;
 				while ( entries.hasNext() ) {
 					int offset = 1;
 					Object entry = entries.next();
 					PreparedStatement st = null;
 					if ( collection.needsInserting( entry, i, elementType ) ) {
 
 						if ( useBatch ) {
 							if ( insertBatchKey == null ) {
 								insertBatchKey = new BasicBatchKey(
 										getRole() + "#INSERT",
 										expectation
 										);
 							}
 							if ( st == null ) {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( insertBatchKey )
 										.getBatchStatement( sql, callable );
 							}
 						}
 						else {
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getStatementPreparer()
 									.prepareStatement( sql, callable );
 						}
 
 						try {
 							offset += expectation.prepare( st );
 							// TODO: copy/paste from recreate()
 							offset = writeKey( st, id, offset, session );
 							if ( hasIdentifier ) {
 								offset = writeIdentifier( st, collection.getIdentifier( entry, i ), offset, session );
 							}
 							if ( hasIndex /* && !indexIsFormula */) {
 								offset = writeIndex( st, collection.getIndex( entry, i, this ), offset, session );
 							}
 							writeElement( st, collection.getElement( entry ), offset, session );
 
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().getBatch( insertBatchKey ).addToBatch();
 							}
 							else {
 								expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 							}
 							collection.afterRowInsert( this, entry, i );
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 							}
 							throw sqle;
 						}
 						finally {
 							if ( !useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 							}
 						}
 					}
 					i++;
 				}
 				LOG.debugf( "Done inserting rows: %s inserted", count );
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not insert collection rows: " +
 								MessageHelper.collectionInfoString( this, collection, id, session ),
 						getSQLInsertRowString()
 						);
 			}
 
 		}
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public String getOwnerEntityName() {
 		return entityName;
 	}
 
 	public EntityPersister getOwnerEntityPersister() {
 		return ownerPersister;
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 	public boolean hasOrphanDelete() {
 		return hasOrphanDelete;
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return indexType;
 		}
 		return elementPropertyMapping.toType( propertyName );
 	}
 
 	public abstract boolean isManyToMany();
 
 	public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 		StringBuilder buffer = new StringBuilder();
 		manyToManyFilterHelper.render( buffer, elementPersister.getFilterAliasGenerator(alias), enabledFilters );
 
 		if ( manyToManyWhereString != null ) {
 			buffer.append( " and " )
 					.append( StringHelper.replace( manyToManyWhereTemplate, Template.TEMPLATE, alias ) );
 		}
 
 		return buffer.toString();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return qualify( alias, indexColumnNames, indexFormulaTemplates );
 		}
 		return elementPropertyMapping.toColumns( alias, propertyName );
 	}
 
 	private String[] indexFragments;
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String[] toColumns(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			if ( indexFragments == null ) {
 				String[] tmp = new String[indexColumnNames.length];
 				for ( int i = 0; i < indexColumnNames.length; i++ ) {
 					tmp[i] = indexColumnNames[i] == null
 							? indexFormulas[i]
 							: indexColumnNames[i];
 					indexFragments = tmp;
 				}
 			}
 			return indexFragments;
 		}
 
 		return elementPropertyMapping.toColumns( propertyName );
 	}
 
 	public Type getType() {
 		return elementPropertyMapping.getType(); // ==elementType ??
 	}
 
 	public String getName() {
 		return getRole();
 	}
 
 	public EntityPersister getElementPersister() {
 		if ( elementPersister == null ) {
 			throw new AssertionFailure( "not an association" );
 		}
 		return elementPersister;
 	}
 
 	public boolean isCollection() {
 		return true;
 	}
 
 	public Serializable[] getCollectionSpaces() {
 		return spaces;
 	}
 
 	protected abstract String generateDeleteString();
 
 	protected abstract String generateDeleteRowString();
 
 	protected abstract String generateUpdateRowString();
 
 	protected abstract String generateInsertRowString();
 
 	public void updateRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && collection.isRowUpdatePossible() ) {
 
 			LOG.debugf( "Updating rows of collection: %s#%s", role, id );
 
 			// update all the modified entries
 			int count = doUpdateRows( id, collection, session );
 
 			LOG.debugf( "Done updating rows: %s updated", count );
 		}
 	}
 
 	protected abstract int doUpdateRows(Serializable key, PersistentCollection collection, SessionImplementor session)
 			throws HibernateException;
 
 	public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 			throws HibernateException {
 		if ( collection.hasQueuedOperations() ) {
 			doProcessQueuedOps( collection, key, session );
 		}
 	}
 
 	protected abstract void doProcessQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 			throws HibernateException;
 
 	public CollectionMetadata getCollectionMetadata() {
 		return this;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected String filterFragment(String alias) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
 	protected String filterFragment(String alias, Set<String> treatAsDeclarations) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	@Override
 	public String filterFragment(
 			String alias,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias, treatAsDeclarations ) ).toString();
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
 		return oneToManyFilterFragment( alias );
 	}
 
 	protected boolean isInsertCallable() {
 		return insertCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getInsertCheckStyle() {
 		return insertCheckStyle;
 	}
 
 	protected boolean isUpdateCallable() {
 		return updateCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getUpdateCheckStyle() {
 		return updateCheckStyle;
 	}
 
 	protected boolean isDeleteCallable() {
 		return deleteCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteCheckStyle() {
 		return deleteCheckStyle;
 	}
 
 	protected boolean isDeleteAllCallable() {
 		return deleteAllCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteAllCheckStyle() {
 		return deleteAllCheckStyle;
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + role + ')';
 	}
 
 	public boolean isVersioned() {
 		return isVersioned && getOwnerEntityPersister().isVersioned();
 	}
 
 	public String getNodeName() {
 		return nodeName;
 	}
 
 	public String getElementNodeName() {
 		return elementNodeName;
 	}
 
 	public String getIndexNodeName() {
 		return indexNodeName;
 	}
 
 	// TODO: deprecate???
 	protected SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	// TODO: needed???
 	protected SqlExceptionHelper getSQLExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
 	}
 
 	public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return filterHelper.isAffectedBy( session.getEnabledFilters() ) ||
 				( isManyToMany() && manyToManyFilterHelper.isAffectedBy( session.getEnabledFilters() ) );
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 
 	public boolean isMutable() {
 		return isMutable;
 	}
 
 	public String[] getCollectionPropertyColumnAliases(String propertyName, String suffix) {
 		String rawAliases[] = (String[]) collectionPropertyColumnAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String result[] = new String[rawAliases.length];
 		for ( int i = 0; i < rawAliases.length; i++ ) {
 			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 
 	// TODO: formulas ?
 	public void initCollectionPropertyMap() {
 
 		initCollectionPropertyMap( "key", keyType, keyColumnAliases, keyColumnNames );
 		initCollectionPropertyMap( "element", elementType, elementColumnAliases, elementColumnNames );
 		if ( hasIndex ) {
 			initCollectionPropertyMap( "index", indexType, indexColumnAliases, indexColumnNames );
 		}
 		if ( hasIdentifier ) {
 			initCollectionPropertyMap(
 					"id",
 					identifierType,
 					new String[] { identifierColumnAlias },
 					new String[] { identifierColumnName } );
 		}
 	}
 
 	private void initCollectionPropertyMap(String aliasName, Type type, String[] columnAliases, String[] columnNames) {
 
 		collectionPropertyColumnAliases.put( aliasName, columnAliases );
 		collectionPropertyColumnNames.put( aliasName, columnNames );
 
 		if ( type.isComponentType() ) {
 			CompositeType ct = (CompositeType) type;
 			String[] propertyNames = ct.getPropertyNames();
 			for ( int i = 0; i < propertyNames.length; i++ ) {
 				String name = propertyNames[i];
 				collectionPropertyColumnAliases.put( aliasName + "." + name, columnAliases[i] );
 				collectionPropertyColumnNames.put( aliasName + "." + name, columnNames[i] );
 			}
 		}
 
 	}
 
 	public int getSize(Serializable key, SessionImplementor session) {
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectSizeString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next() ? rs.getInt( 1 ) - baseIndex : 0;
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve collection size: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 			);
 		}
 	}
 
 	public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 		return exists( key, incrementIndexByBase( index ), getIndexType(), sqlDetectRowByIndexString, session );
 	}
 
 	public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 		return exists( key, element, getElementType(), sqlDetectRowByElementString, session );
 	}
 
 	private boolean exists(Serializable key, Object indexOrElement, Type indexOrElementType, String sql, SessionImplementor session) {
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				indexOrElementType.nullSafeSet( st, indexOrElement, keyColumnNames.length + 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next();
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			catch ( TransientObjectException e ) {
 				return false;
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not check row existence: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 			);
 		}
 	}
 
 	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectRowByIndexString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				getIndexType().nullSafeSet( st, incrementIndexByBase( index ), keyColumnNames.length + 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					if ( rs.next() ) {
 						return getElementType().nullSafeGet( rs, elementColumnAliases, session, owner );
 					}
 					else {
 						return null;
 					}
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not read row: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 			);
 		}
 	}
 
 	public boolean isExtraLazy() {
 		return isExtraLazy;
 	}
 
 	protected Dialect getDialect() {
 		return dialect;
 	}
 
 	/**
 	 * Intended for internal use only. In fact really only currently used from
 	 * test suite for assertion purposes.
 	 *
 	 * @return The default collection initializer for this persister/collection.
 	 */
 	public CollectionInitializer getInitializer() {
 		return initializer;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public String getMappedByProperty() {
 		return mappedByProperty;
 	}
 
 	private class StandardOrderByAliasResolver implements OrderByAliasResolver {
 		private final String rootAlias;
 
 		private StandardOrderByAliasResolver(String rootAlias) {
 			this.rootAlias = rootAlias;
 		}
 
 		@Override
 		public String resolveTableAlias(String columnReference) {
 			if ( elementPersister == null ) {
 				// we have collection of non-entity elements...
 				return rootAlias;
 			}
 			else {
 				return ( (Loadable) elementPersister ).getTableAliasForColumn( columnReference, rootAlias );
 			}
 		}
 	}
 
 	public abstract FilterAliasGenerator getFilterAliasGenerator(final String rootAlias);
 
 	// ColectionDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public CollectionPersister getCollectionPersister() {
 		return this;
 	}
 
 	@Override
 	public CollectionIndexDefinition getIndexDefinition() {
 		if ( ! hasIndex() ) {
 			return null;
 		}
 
 		return new CollectionIndexDefinition() {
 			@Override
 			public CollectionDefinition getCollectionDefinition() {
 				return AbstractCollectionPersister.this;
 			}
 
 			@Override
 			public Type getType() {
 				return getIndexType();
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
-				if ( getType().isComponentType() ) {
-					throw new IllegalStateException( "Cannot treat composite collection index type as entity" );
+				if ( !getType().isEntityType() ) {
+					throw new IllegalStateException( "Cannot treat collection index type as entity" );
 				}
 				return (EntityPersister) ( (AssociationType) getIndexType() ).getAssociatedJoinable( getFactory() );
 			}
 
 			@Override
 			public CompositionDefinition toCompositeDefinition() {
 				if ( ! getType().isComponentType() ) {
-					throw new IllegalStateException( "Cannot treat entity collection index type as composite" );
+					throw new IllegalStateException( "Cannot treat collection index type as composite" );
 				}
 				return new CompositeCollectionElementDefinition() {
 					@Override
 					public String getName() {
 						return "index";
 					}
 
 					@Override
 					public CompositeType getType() {
 						return (CompositeType) getIndexType();
 					}
 
 					@Override
 					public boolean isNullable() {
 						return false;
 					}
 
 					@Override
 					public AttributeSource getSource() {
 						// TODO: what if this is a collection w/in an encapsulated composition attribute?
 						// should return the encapsulated composition attribute instead???
 						return getOwnerEntityPersister();
 					}
 
 					@Override
 					public Iterable<AttributeDefinition> getAttributes() {
 						return CompositionSingularSubAttributesHelper.getCompositeCollectionIndexSubAttributes( this );
 					}
 					@Override
 					public CollectionDefinition getCollectionDefinition() {
 						return AbstractCollectionPersister.this;
 					}
 				};
 			}
+
+			@Override
+			public AnyMappingDefinition toAnyMappingDefinition() {
+				final Type type = getType();
+				if ( ! type.isAnyType() ) {
+					throw new IllegalStateException( "Cannot treat collection index type as ManyToAny" );
+				}
+				return new StandardAnyTypeDefinition( (AnyType) type, isLazy() || isExtraLazy() );
+			}
 		};
 	}
 
 	@Override
 	public CollectionElementDefinition getElementDefinition() {
 		return new CollectionElementDefinition() {
 			@Override
 			public CollectionDefinition getCollectionDefinition() {
 				return AbstractCollectionPersister.this;
 			}
 
 			@Override
 			public Type getType() {
 				return getElementType();
 			}
 
 			@Override
 			public AnyMappingDefinition toAnyMappingDefinition() {
 				final Type type = getType();
 				if ( ! type.isAnyType() ) {
-					throw new WalkingException( "Cannot treat collection element type as ManyToAny" );
+					throw new IllegalStateException( "Cannot treat collection element type as ManyToAny" );
 				}
 				return new StandardAnyTypeDefinition( (AnyType) type, isLazy() || isExtraLazy() );
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
-				if ( getType().isComponentType() ) {
-					throw new WalkingException( "Cannot treat composite collection element type as entity" );
+				if ( !getType().isEntityType() ) {
+					throw new IllegalStateException( "Cannot treat collection element type as entity" );
 				}
 				return getElementPersister();
 			}
 
 			@Override
 			public CompositeCollectionElementDefinition toCompositeElementDefinition() {
 
 				if ( ! getType().isComponentType() ) {
-					throw new WalkingException( "Cannot treat entity collection element type as composite" );
+					throw new IllegalStateException( "Cannot treat entity collection element type as composite" );
 				}
 
 				return new CompositeCollectionElementDefinition() {
 					@Override
 					public String getName() {
 						return "";
 					}
 
 					@Override
 					public CompositeType getType() {
 						return (CompositeType) getElementType();
 					}
 
 					@Override
 					public boolean isNullable() {
 						return false;
 					}
 
 					@Override
 					public AttributeSource getSource() {
 						// TODO: what if this is a collection w/in an encapsulated composition attribute?
 						// should return the encapsulated composition attribute instead???
 						return getOwnerEntityPersister();
 					}
 
 					@Override
 					public Iterable<AttributeDefinition> getAttributes() {
 						return CompositionSingularSubAttributesHelper.getCompositeCollectionElementSubAttributes( this );
 					}
 
 					@Override
 					public CollectionDefinition getCollectionDefinition() {
 						return AbstractCollectionPersister.this;
 					}
 				};
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
index bb685d1816..759abd0930 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
@@ -1,88 +1,88 @@
 /*
  * jDocBook, processing of DocBook sources
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
 package org.hibernate.persister.walking.spi;
 
 import org.hibernate.type.Type;
 
 /**
  * Represents a collection element.
  *
  * @author Steve Ebersole
  */
 public interface CollectionElementDefinition {
 
 	/**
 	 * Returns the collection definition.
 	 * @return  the collection definition.
 	 */
 	public CollectionDefinition getCollectionDefinition();
 
 	/**
 	 * Returns the collection element type.
 	 * @return the collection element type
 	 */
 	public Type getType();
 
 	/**
 	 * If the element type returned by {@link #getType()} is an
-	 * {@link org.hibernate.type.EntityType}, then the entity
+	 * {@link org.hibernate.type.AnyType}, then the any mapping
 	 * definition for the collection element is returned;
 	 * otherwise, IllegalStateException is thrown.
 	 *
-	 * @return the entity definition for the collection element.
+	 * @return the any mapping definition for the collection element.
 	 *
 	 * @throws IllegalStateException if the collection element type
 	 * returned by {@link #getType()} is not of type
-	 * {@link org.hibernate.type.EntityType}.
+	 * {@link org.hibernate.type.AnyType}.
 	 */
 	public AnyMappingDefinition toAnyMappingDefinition();
 
 	/**
 	 * If the element type returned by {@link #getType()} is an
 	 * {@link org.hibernate.type.EntityType}, then the entity
 	 * definition for the collection element is returned;
 	 * otherwise, IllegalStateException is thrown.
 	 *
 	 * @return the entity definition for the collection element.
 	 *
 	 * @throws IllegalStateException if the collection element type
 	 * returned by {@link #getType()} is not of type
 	 * {@link org.hibernate.type.EntityType}.
 	 */
 	public EntityDefinition toEntityDefinition();
 
 	/**
 	 * If the element type returned by {@link #getType()} is a
 	 * {@link org.hibernate.type.CompositeType}, then the composite
 	 * element definition for the collection element is returned;
 	 * otherwise, IllegalStateException is thrown.
 	 *
 	 * @return the composite element definition for the collection element.
 	 *
 	 * @throws IllegalStateException if the collection element type
 	 * returned by {@link #getType()} is not of type
 	 * {@link org.hibernate.type.CompositeType}.
 	 */
 	public CompositeCollectionElementDefinition toCompositeElementDefinition();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionIndexDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionIndexDefinition.java
index a3c7d753cd..4908d5d24a 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionIndexDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionIndexDefinition.java
@@ -1,68 +1,82 @@
 /*
  * jDocBook, processing of DocBook sources
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
 package org.hibernate.persister.walking.spi;
 
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public interface CollectionIndexDefinition {
 	/**
 	 * Returns the collection definition.
 	 * @return  the collection definition.
 	 */
 	public CollectionDefinition getCollectionDefinition();
 	/**
 	 * Returns the collection index type.
 	 * @return the collection index type
 	 */
 	public Type getType();
 	/**
 	 * If the index type returned by {@link #getType()} is an
 	 * {@link org.hibernate.type.EntityType}, then the entity
 	 * definition for the collection index is returned;
 	 * otherwise, IllegalStateException is thrown.
 	 *
 	 * @return the entity definition for the collection index.
 	 *
 	 * @throws IllegalStateException if the collection index type
 	 * returned by {@link #getType()} is not of type
 	 * {@link org.hibernate.type.EntityType}.
 	 */
 	public EntityDefinition toEntityDefinition();
 	/**
 	 * If the index type returned by {@link #getType()} is a
 	 * {@link org.hibernate.type.CompositeType}, then the composite
 	 * index definition for the collection index is returned;
 	 * otherwise, IllegalStateException is thrown.
 	 *
 	 * @return the composite index definition for the collection index.
 	 *
 	 * @throws IllegalStateException if the collection index type
 	 * returned by {@link #getType()} is not of type
 	 * {@link org.hibernate.type.CompositeType}.
 	 */
 	public CompositionDefinition toCompositeDefinition();
+
+	/**
+	 * If the index type returned by {@link #getType()} is an
+	 * {@link org.hibernate.type.AnyType}, then the any mapping
+	 * definition for the collection index is returned;
+	 * otherwise, IllegalStateException is thrown.
+	 *
+	 * @return the any mapping definition for the collection index.
+	 *
+	 * @throws IllegalStateException if the collection index type
+	 * returned by {@link #getType()} is not of type
+	 * {@link org.hibernate.type.AnyType}.
+	 */
+	public AnyMappingDefinition toAnyMappingDefinition();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetamodelGraphWalker.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetamodelGraphWalker.java
index 840b0c39b4..efd17e9bfe 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetamodelGraphWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetamodelGraphWalker.java
@@ -1,306 +1,313 @@
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
 package org.hibernate.persister.walking.spi;
 
 import java.util.HashSet;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * Implements metamodel graph walking.  In layman terms, we are walking the graph of the users domain model as
  * defined/understood by mapped associations.
  * <p/>
- * Initially grew as a part of the re-implementation of the legacy JoinWalker functional to instead build LoadPlans.
+ * Initially grew as a part of the re-implementation of the legacy JoinWalker functionality to instead build LoadPlans.
  * But this is really quite simple walking.  Interesting events are handled by calling out to
  * implementations of {@link AssociationVisitationStrategy} which really provide the real functionality of what we do
  * as we walk.
  * <p/>
  * The visitor will walk the entire metamodel graph (the parts reachable from the given root)!!!  It is up to the
  * provided AssociationVisitationStrategy to tell it when to stop.  The walker provides the walking; the strategy
  * provides the semantics of what happens at certain points.  Its really very similar to parsers and how parsing is
  * generally split between syntax and semantics.  Walker walks the syntax (associations, identifiers, etc) and when it
  * calls out to the strategy the strategy then decides the semantics (literally, the meaning).
  * <p/>
  * The visitor will, however, stop if it sees a "duplicate" AssociationKey.  In such a case, the walker would call
  * {@link AssociationVisitationStrategy#foundCircularAssociation} and stop walking any further down that graph any
  * further.
  *
  * @author Steve Ebersole
  */
 public class MetamodelGraphWalker {
 	private static final Logger log = Logger.getLogger( MetamodelGraphWalker.class );
 
 	/**
 	 * Entry point into walking the model graph of an entity according to its defined metamodel.
 	 *
 	 * @param strategy The semantics strategy
 	 * @param persister The persister describing the entity to start walking from
 	 */
 	public static void visitEntity(AssociationVisitationStrategy strategy, EntityPersister persister) {
 		strategy.start();
 		try {
 			new MetamodelGraphWalker( strategy, persister.getFactory() )
 					.visitEntityDefinition( persister );
 		}
 		finally {
 			strategy.finish();
 		}
 	}
 
 	/**
 	 * Entry point into walking the model graph of a collection according to its defined metamodel.
 	 *
 	 * @param strategy The semantics strategy
 	 * @param persister The persister describing the collection to start walking from
 	 */
 	public static void visitCollection(AssociationVisitationStrategy strategy, CollectionPersister persister) {
 		strategy.start();
 		try {
 			new MetamodelGraphWalker( strategy, persister.getFactory() )
 					.visitCollectionDefinition( persister );
 		}
 		finally {
 			strategy.finish();
 		}
 	}
 
 	private final AssociationVisitationStrategy strategy;
 	private final SessionFactoryImplementor factory;
 
 	// todo : add a getDepth() method to PropertyPath
 	private PropertyPath currentPropertyPath = new PropertyPath();
 
 	public MetamodelGraphWalker(AssociationVisitationStrategy strategy, SessionFactoryImplementor factory) {
 		this.strategy = strategy;
 		this.factory = factory;
 	}
 
 	private void visitEntityDefinition(EntityDefinition entityDefinition) {
 		strategy.startingEntity( entityDefinition );
 
 		visitIdentifierDefinition( entityDefinition.getEntityKeyDefinition() );
 		visitAttributes( entityDefinition );
 
 		strategy.finishingEntity( entityDefinition );
 	}
 
 	private void visitIdentifierDefinition(EntityIdentifierDefinition identifierDefinition) {
 		strategy.startingEntityIdentifier( identifierDefinition );
 
 		// to make encapsulated and non-encapsulated composite identifiers work the same here, we "cheat" here a
 		// little bit and simply walk the attributes of the composite id in both cases.
 
 		// this works because the LoadPlans already build the top-level composite for composite ids
 
 		if ( identifierDefinition.isEncapsulated() ) {
 			// in the encapsulated composite id case that means we have a little bit of duplication between here and
 			// visitCompositeDefinition, but in the spirit of consistently handling composite ids, that is much better
 			// solution...
 			final EncapsulatedEntityIdentifierDefinition idAsEncapsulated = (EncapsulatedEntityIdentifierDefinition) identifierDefinition;
 			final AttributeDefinition idAttr = idAsEncapsulated.getAttributeDefinition();
 			if ( CompositionDefinition.class.isInstance( idAttr ) ) {
 				visitCompositeDefinition( (CompositionDefinition) idAttr );
 			}
 		}
 		else {
 			// NonEncapsulatedEntityIdentifierDefinition itself is defined as a CompositionDefinition
 			visitCompositeDefinition( (NonEncapsulatedEntityIdentifierDefinition) identifierDefinition );
 		}
 
 		strategy.finishingEntityIdentifier( identifierDefinition );
 	}
 
 	private void visitAttributes(AttributeSource attributeSource) {
 		final Iterable<AttributeDefinition> attributeDefinitions = attributeSource.getAttributes();
 		if ( attributeDefinitions == null ) {
 			return;
 		}
 		for ( AttributeDefinition attributeDefinition : attributeSource.getAttributes() ) {
 			visitAttributeDefinition( attributeDefinition );
 		}
 	}
 
 	private void visitAttributeDefinition(AttributeDefinition attributeDefinition) {
 		final PropertyPath subPath = currentPropertyPath.append( attributeDefinition.getName() );
 		log.debug( "Visiting attribute path : " + subPath.getFullPath() );
 
 
 		if ( attributeDefinition.getType().isAssociationType() ) {
 			final AssociationAttributeDefinition associationAttributeDefinition =
 					(AssociationAttributeDefinition) attributeDefinition;
 			final AssociationKey associationKey = associationAttributeDefinition.getAssociationKey();
 			if ( isDuplicateAssociationKey( associationKey ) ) {
 				log.debug( "Property path deemed to be circular : " + subPath.getFullPath() );
 				strategy.foundCircularAssociation( associationAttributeDefinition );
 				// EARLY EXIT!!!
 				return;
 			}
 		}
 
 
 		boolean continueWalk = strategy.startingAttribute( attributeDefinition );
 		if ( continueWalk ) {
 			final PropertyPath old = currentPropertyPath;
 			currentPropertyPath = subPath;
 			try {
 				final Type attributeType = attributeDefinition.getType();
 				if ( attributeType.isAssociationType() ) {
 					visitAssociation( (AssociationAttributeDefinition) attributeDefinition );
 				}
 				else if ( attributeType.isComponentType() ) {
 					visitCompositeDefinition( (CompositionDefinition) attributeDefinition );
 				}
 			}
 			finally {
 				currentPropertyPath = old;
 			}
 		}
 		strategy.finishingAttribute( attributeDefinition );
 	}
 
 	private void visitAssociation(AssociationAttributeDefinition attribute) {
 		// todo : do "too deep" checks; but see note about adding depth to PropertyPath
 		//
 		// may also need to better account for "composite fetches" in terms of "depth".
 
 		addAssociationKey( attribute.getAssociationKey() );
 
 		final AssociationAttributeDefinition.AssociationNature nature = attribute.getAssociationNature();
 		if ( nature == AssociationAttributeDefinition.AssociationNature.ANY ) {
 			visitAnyDefinition( attribute.toAnyDefinition() );
 		}
 		else if ( nature == AssociationAttributeDefinition.AssociationNature.COLLECTION ) {
 			visitCollectionDefinition( attribute.toCollectionDefinition() );
 		}
 		else {
 			visitEntityDefinition( attribute.toEntityDefinition() );
 		}
 	}
 
 	private void visitAnyDefinition(AnyMappingDefinition anyDefinition) {
 		strategy.foundAny( anyDefinition );
 	}
 
 	private void visitCompositeDefinition(CompositionDefinition compositionDefinition) {
 		strategy.startingComposite( compositionDefinition );
 
 		visitAttributes( compositionDefinition );
 
 		strategy.finishingComposite( compositionDefinition );
 	}
 
 	private void visitCollectionDefinition(CollectionDefinition collectionDefinition) {
 		strategy.startingCollection( collectionDefinition );
 
 		visitCollectionIndex( collectionDefinition );
 		visitCollectionElements( collectionDefinition );
 
 		strategy.finishingCollection( collectionDefinition );
 	}
 
 	private void visitCollectionIndex(CollectionDefinition collectionDefinition) {
 		final CollectionIndexDefinition collectionIndexDefinition = collectionDefinition.getIndexDefinition();
 		if ( collectionIndexDefinition == null ) {
 			return;
 		}
 
 		strategy.startingCollectionIndex( collectionIndexDefinition );
 
 		log.debug( "Visiting index for collection :  " + currentPropertyPath.getFullPath() );
 		currentPropertyPath = currentPropertyPath.append( "<index>" );
 
 		try {
 			final Type collectionIndexType = collectionIndexDefinition.getType();
-			if ( collectionIndexType.isComponentType() ) {
+			if ( collectionIndexType.isAnyType() ) {
+				visitAnyDefinition( collectionIndexDefinition.toAnyMappingDefinition() );
+			}
+			else if ( collectionIndexType.isComponentType() ) {
 				visitCompositeDefinition( collectionIndexDefinition.toCompositeDefinition() );
 			}
 			else if ( collectionIndexType.isAssociationType() ) {
 				visitEntityDefinition( collectionIndexDefinition.toEntityDefinition() );
 			}
 		}
 		finally {
 			currentPropertyPath = currentPropertyPath.getParent();
 		}
 
 		strategy.finishingCollectionIndex( collectionIndexDefinition );
 	}
 
 	private void visitCollectionElements(CollectionDefinition collectionDefinition) {
 		final CollectionElementDefinition elementDefinition = collectionDefinition.getElementDefinition();
 		strategy.startingCollectionElements( elementDefinition );
 
-		if ( elementDefinition.getType().isComponentType() ) {
+		final Type collectionElementType = elementDefinition.getType();
+		if ( collectionElementType.isAnyType() ) {
+			visitAnyDefinition( elementDefinition.toAnyMappingDefinition() );
+		}
+		else if ( collectionElementType.isComponentType() ) {
 			visitCompositeDefinition( elementDefinition.toCompositeElementDefinition() );
 		}
-		else if ( elementDefinition.getType().isEntityType() ) {
+		else if ( collectionElementType.isEntityType() ) {
 			if ( ! collectionDefinition.getCollectionPersister().isOneToMany() ) {
 				final QueryableCollection queryableCollection = (QueryableCollection) collectionDefinition.getCollectionPersister();
 				addAssociationKey(
 						new AssociationKey(
 								queryableCollection.getTableName(),
 								queryableCollection.getElementColumnNames()
 						)
 				);
 			}
 			visitEntityDefinition( elementDefinition.toEntityDefinition() );
 		}
 
 		strategy.finishingCollectionElements( elementDefinition );
 	}
 
 	private final Set<AssociationKey> visitedAssociationKeys = new HashSet<AssociationKey>();
 
 	/**
 	 * Add association key to indicate the association is being visited.
 	 * @param associationKey - the association key.
 	 * @throws WalkingException if the association with the specified association key
 	 *                          has already been visited.
 	 */
 	protected void addAssociationKey(AssociationKey associationKey) {
 		if ( ! visitedAssociationKeys.add( associationKey ) ) {
 			throw new WalkingException(
 					String.format( "Association has already been visited: %s", associationKey )
 			);
 		}
 		strategy.associationKeyRegistered( associationKey );
 	}
 
 	/**
 	 * Has an association with the specified key been visited already?
 	 * @param associationKey - the association key.
 	 * @return true, if the association with the specified association key has already been visited;
 	 *         false, otherwise.
 	 */
 	protected boolean isDuplicateAssociationKey(AssociationKey associationKey) {
 		return visitedAssociationKeys.contains( associationKey ) || strategy.isDuplicateAssociationKey( associationKey );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/walking/LoggingAssociationVisitationStrategy.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/walking/LoggingAssociationVisitationStrategy.java
index 1412e8c5c1..8467fad1a3 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/walking/LoggingAssociationVisitationStrategy.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/walking/LoggingAssociationVisitationStrategy.java
@@ -1,271 +1,246 @@
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
 package org.hibernate.test.loadplans.walking;
 
 import org.hibernate.annotations.common.util.StringHelper;
 import org.hibernate.loader.plan2.spi.FetchSource;
 import org.hibernate.persister.walking.spi.AnyMappingDefinition;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.AssociationVisitationStrategy;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 
 /**
  * @author Steve Ebersole
  */
 public class LoggingAssociationVisitationStrategy implements AssociationVisitationStrategy {
 	private int depth = 1;
 
 	@Override
 	public void start() {
 		System.out.println( ">> Start" );
 	}
 
 	@Override
 	public void finish() {
 		System.out.println( "<< Finish" );
 	}
 
 	@Override
 	public void startingEntity(EntityDefinition entityDefinition) {
 		System.out.println(
 				String.format(
 						"%s Starting entity (%s)",
 						StringHelper.repeat( ">>", ++depth ),
 						entityDefinition.getEntityPersister().getEntityName()
 				)
 		);
 	}
 
 	@Override
 	public void finishingEntity(EntityDefinition entityDefinition) {
 		System.out.println(
 				String.format(
 						"%s Finishing entity (%s)",
 						StringHelper.repeat( "<<", depth-- ),
 						entityDefinition.getEntityPersister().getEntityName()
 				)
 		);
 	}
 
 	@Override
 	public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 		System.out.println(
 				String.format(
 						"%s Starting [%s] entity identifier (%s)",
 						StringHelper.repeat( ">>", ++depth ),
 						entityIdentifierDefinition.isEncapsulated() ? "encapsulated" : "non-encapsulated",
 						entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 				)
 		);
 	}
 
 	@Override
 	public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 		System.out.println(
 				String.format(
 						"%s Finishing entity identifier (%s)",
 						StringHelper.repeat( "<<", depth-- ),
 						entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 				)
 		);
 	}
 
 	@Override
 	public boolean startingAttribute(AttributeDefinition attributeDefinition) {
 		System.out.println(
 				String.format(
 						"%s Handling attribute (%s)",
 						StringHelper.repeat( ">>", depth + 1 ),
 						attributeDefinition.getName()
 				)
 		);
 		return true;
 	}
 
 	@Override
 	public void finishingAttribute(AttributeDefinition attributeDefinition) {
 		// nothing to do
 	}
 
 	@Override
 	public void startingComposite(CompositionDefinition compositionDefinition) {
 		System.out.println(
 				String.format(
 						"%s Starting composite (%s)",
 						StringHelper.repeat( ">>", ++depth ),
 						compositionDefinition.getName()
 				)
 		);
 	}
 
 	@Override
 	public void finishingComposite(CompositionDefinition compositionDefinition) {
 		System.out.println(
 				String.format(
 						"%s Finishing composite (%s)",
 						StringHelper.repeat( "<<", depth-- ),
 						compositionDefinition.getName()
 				)
 		);
 	}
 
 	@Override
 	public void startingCollection(CollectionDefinition collectionDefinition) {
 		System.out.println(
 				String.format(
 						"%s Starting collection (%s)",
 						StringHelper.repeat( ">>", ++depth ),
 						collectionDefinition.getCollectionPersister().getRole()
 				)
 		);
 	}
 
 	@Override
 	public void finishingCollection(CollectionDefinition collectionDefinition) {
 		System.out.println(
 				String.format(
 						"%s Finishing collection (%s)",
 						StringHelper.repeat( ">>", depth-- ),
 						collectionDefinition.getCollectionPersister().getRole()
 				)
 		);
 	}
 
 
 	@Override
 	public void startingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
 		System.out.println(
 				String.format(
 						"%s Starting collection index (%s)",
 						StringHelper.repeat( ">>", ++depth ),
 						collectionIndexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				)
 		);
 	}
 
 	@Override
 	public void finishingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
 		System.out.println(
 				String.format(
 						"%s Finishing collection index (%s)",
 						StringHelper.repeat( "<<", depth-- ),
 						collectionIndexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				)
 		);
 	}
 
 	@Override
 	public void startingCollectionElements(CollectionElementDefinition elementDefinition) {
 		System.out.println(
 				String.format(
 						"%s Starting collection elements (%s)",
 						StringHelper.repeat( ">>", ++depth ),
 						elementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				)
 		);
 	}
 
 	@Override
 	public void finishingCollectionElements(CollectionElementDefinition elementDefinition) {
 		System.out.println(
 				String.format(
 						"%s Finishing collection elements (%s)",
 						StringHelper.repeat( "<<", depth-- ),
 						elementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				)
 		);
 	}
 
-
-	// why do we have these + startingCollectionElements/finishingCollectionElements ???
-//
-//	@Override
-//	public void startingCompositeCollectionElement(CompositeCollectionElementDefinition compositionElementDefinition) {
-//		System.out.println(
-//				String.format(
-//						"%s Starting composite (%s)",
-//						StringHelper.repeat( ">>", ++depth ),
-//						compositionElementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
-//				)
-//		);
-//	}
-//
-//	@Override
-//	public void finishingCompositeCollectionElement(CompositeCollectionElementDefinition compositionElementDefinition) {
-//		System.out.println(
-//				String.format(
-//						"%s Finishing composite (%s)",
-//						StringHelper.repeat( "<<", depth-- ),
-//						compositionElementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
-//				)
-//		);
-//	}
-
 	@Override
 	public void foundAny(AnyMappingDefinition anyDefinition) {
 		// nothing to do
 	}
 
 	@Override
 	public void associationKeyRegistered(AssociationKey associationKey) {
 		System.out.println(
 				String.format(
 						"%s AssociationKey registered : %s",
 						StringHelper.repeat( ">>", depth + 1 ),
 						associationKey.toString()
 				)
 		);
 	}
 
 	@Override
 	public FetchSource registeredFetchSource(AssociationKey associationKey) {
 		return null;
 	}
 
 	@Override
 	public void foundCircularAssociation(
 			AssociationAttributeDefinition attributeDefinition) {
 		System.out.println(
 				String.format(
 						"%s Handling circular association attribute (%s) : %s",
 						StringHelper.repeat( ">>", depth + 1 ),
 						attributeDefinition.toString(),
 						attributeDefinition.getAssociationKey().toString()
 				)
 		);
 	}
 
 	@Override
 	public boolean isDuplicateAssociationKey(AssociationKey associationKey) {
 		return false;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 }
