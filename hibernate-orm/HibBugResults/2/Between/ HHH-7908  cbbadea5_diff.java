diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/UnresolvedEntityInsertActions.java b/hibernate-core/src/main/java/org/hibernate/action/internal/UnresolvedEntityInsertActions.java
index 09b694ea1d..e6e18e9acf 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/UnresolvedEntityInsertActions.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/UnresolvedEntityInsertActions.java
@@ -1,327 +1,329 @@
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
 package org.hibernate.action.internal;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.IdentityHashMap;
 import java.util.Map;
 import java.util.Set;
 import java.util.TreeSet;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.PropertyValueException;
 import org.hibernate.TransientPropertyValueException;
 import org.hibernate.engine.internal.NonNullableTransientDependencies;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * Tracks unresolved entity insert actions.
  *
  * An entity insert action is unresolved if the entity
  * to be inserted has at least one non-nullable association with
  * an unsaved transient entity, and the foreign key points to that
  * unsaved transient entity.
  *
  * These references must be resolved before an insert action can be
  * executed.
  *
  * @author Gail Badner
  */
 public class UnresolvedEntityInsertActions {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 				CoreMessageLogger.class,
 				UnresolvedEntityInsertActions.class.getName()
 		);
 	private static final int INIT_SIZE = 5;
 
 	private final Map<AbstractEntityInsertAction,NonNullableTransientDependencies> dependenciesByAction =
 			new IdentityHashMap<AbstractEntityInsertAction,NonNullableTransientDependencies>( INIT_SIZE );
 	private final Map<Object,Set<AbstractEntityInsertAction>> dependentActionsByTransientEntity =
 			new IdentityHashMap<Object,Set<AbstractEntityInsertAction>>( INIT_SIZE );
 
 	/**
 	 * Add an unresolved insert action.
 	 *
 	 * @param insert - unresolved insert action.
 	 * @param dependencies - non-nullable transient dependencies
 	 *                       (must be non-null and non-empty).
 	 *
 	 * @throws IllegalArgumentException if {@code dependencies is null or empty}.
 	 */
 	public void addUnresolvedEntityInsertAction(AbstractEntityInsertAction insert, NonNullableTransientDependencies dependencies) {
 		if ( dependencies == null || dependencies.isEmpty() ) {
 			throw new IllegalArgumentException(
 					"Attempt to add an unresolved insert action that has no non-nullable transient entities."
 			);
 		}
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Adding insert with non-nullable, transient entities; insert=[{0}], dependencies=[{1}]",
 					insert,
 					dependencies.toLoggableString( insert.getSession() )
 			);
 		}
 		dependenciesByAction.put( insert, dependencies );
 		addDependenciesByTransientEntity( insert, dependencies );
 	}
 
 	/**
 	 * Returns the unresolved insert actions.
 	 * @return the unresolved insert actions.
 	 */
 	public Iterable<AbstractEntityInsertAction> getDependentEntityInsertActions() {
 		return dependenciesByAction.keySet();
 	}
 
 	/**
 	 * Throws {@link org.hibernate.PropertyValueException} if there are any unresolved
 	 * entity insert actions that depend on non-nullable associations with
 	 * a transient entity. This method should be called on completion of
 	 * an operation (after all cascades are completed) that saves an entity.
 	 *
 	 * @throws org.hibernate.PropertyValueException if there are any unresolved entity
 	 * insert actions; {@link org.hibernate.PropertyValueException#getEntityName()}
 	 * and {@link org.hibernate.PropertyValueException#getPropertyName()} will
 	 * return the entity name and property value for the first unresolved
 	 * entity insert action.
 	 */
 	public void checkNoUnresolvedActionsAfterOperation() throws PropertyValueException {
 		if ( isEmpty() ) {
 			LOG.trace( "No entity insert actions have non-nullable, transient entity dependencies." );
 		}
 		else {
 			AbstractEntityInsertAction firstDependentAction =
 					dependenciesByAction.keySet().iterator().next();
 
 			logCannotResolveNonNullableTransientDependencies( firstDependentAction.getSession() );
 
 			NonNullableTransientDependencies nonNullableTransientDependencies =
 					dependenciesByAction.get( firstDependentAction );
 			Object firstTransientDependency =
 					nonNullableTransientDependencies.getNonNullableTransientEntities().iterator().next();
 			String firstPropertyPath =
 					nonNullableTransientDependencies.getNonNullableTransientPropertyPaths( firstTransientDependency ).iterator().next();
 			throw new TransientPropertyValueException(
 					"Not-null property references a transient value - transient instance must be saved before current operation",
 					firstDependentAction.getSession().guessEntityName( firstTransientDependency ),
 					firstDependentAction.getEntityName(),
 					firstPropertyPath
 			);
 		}
 	}
 
 	private void logCannotResolveNonNullableTransientDependencies(SessionImplementor session) {
 		for ( Map.Entry<Object,Set<AbstractEntityInsertAction>> entry : dependentActionsByTransientEntity.entrySet() ) {
 			Object transientEntity = entry.getKey();
 			String transientEntityName = session.guessEntityName( transientEntity );
 			Serializable transientEntityId = session.getFactory().getEntityPersister( transientEntityName ).getIdentifier( transientEntity, session );
 			String transientEntityString = MessageHelper.infoString( transientEntityName, transientEntityId );
 			Set<String> dependentEntityStrings = new TreeSet<String>();
 			Set<String> nonNullableTransientPropertyPaths = new TreeSet<String>();
 			for ( AbstractEntityInsertAction dependentAction : entry.getValue() ) {
 				dependentEntityStrings.add( MessageHelper.infoString( dependentAction.getEntityName(), dependentAction.getId() ) );
 				for ( String path : dependenciesByAction.get( dependentAction ).getNonNullableTransientPropertyPaths( transientEntity ) ) {
 					String fullPath = new StringBuilder( dependentAction.getEntityName().length() + path.length() + 1 )
 							.append( dependentAction.getEntityName() )
 							.append( '.' )
 							.append( path )
 							.toString();
 					nonNullableTransientPropertyPaths.add( fullPath );
 				}
 			}
 			LOG.cannotResolveNonNullableTransientDependencies(
 					transientEntityString,
 					dependentEntityStrings,
 					nonNullableTransientPropertyPaths
 			);
 		}
 	}
 
 	/**
 	 * Returns true if there are no unresolved entity insert actions.
 	 * @return true, if there are no unresolved entity insert actions; false, otherwise.
 	 */
 	public boolean isEmpty() {
 		return dependenciesByAction.isEmpty();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void addDependenciesByTransientEntity(AbstractEntityInsertAction insert, NonNullableTransientDependencies dependencies) {
 		for ( Object transientEntity : dependencies.getNonNullableTransientEntities() ) {
 			Set<AbstractEntityInsertAction> dependentActions = dependentActionsByTransientEntity.get( transientEntity );
 			if ( dependentActions == null ) {
 				dependentActions = new IdentitySet();
 				dependentActionsByTransientEntity.put( transientEntity, dependentActions );
 			}
 			dependentActions.add( insert );
 		}
 	}
 
 	/**
 	 * Resolve any dependencies on {@code managedEntity}.
 	 *
 	 * @param managedEntity - the managed entity name
 	 * @param session - the session
 	 *
 	 * @return the insert actions that depended only on the specified entity.
 	 *
 	 * @throws IllegalArgumentException if {@code managedEntity} did not have managed or read-only status.
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Set<AbstractEntityInsertAction> resolveDependentActions(Object managedEntity, SessionImplementor session) {
 		EntityEntry entityEntry = session.getPersistenceContext().getEntry( managedEntity );
 		if ( entityEntry.getStatus() != Status.MANAGED && entityEntry.getStatus() != Status.READ_ONLY ) {
 			throw new IllegalArgumentException( "EntityEntry did not have status MANAGED or READ_ONLY: " + entityEntry );
 		}
+
+		final boolean traceEnabled = LOG.isTraceEnabled();
 		// Find out if there are any unresolved insertions that are waiting for the
 		// specified entity to be resolved.
 		Set<AbstractEntityInsertAction> dependentActions = dependentActionsByTransientEntity.remove( managedEntity );
 		if ( dependentActions == null ) {
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev(
 						"No unresolved entity inserts that depended on [{0}]",
 						MessageHelper.infoString( entityEntry.getEntityName(), entityEntry.getId() )
 				);
 			}
 			return Collections.emptySet();  //NOTE EARLY EXIT!
 		}
 		Set<AbstractEntityInsertAction> resolvedActions = new IdentitySet(  );
-		if ( LOG.isTraceEnabled()  ) {
+		if ( traceEnabled  ) {
 			LOG.tracev(
 					"Unresolved inserts before resolving [{0}]: [{1}]",
 					MessageHelper.infoString( entityEntry.getEntityName(), entityEntry.getId() ),
 					toString()
 			);
 		}
 		for ( AbstractEntityInsertAction dependentAction : dependentActions ) {
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev(
 						"Resolving insert [{0}] dependency on [{1}]",
 						MessageHelper.infoString( dependentAction.getEntityName(), dependentAction.getId() ),
 						MessageHelper.infoString( entityEntry.getEntityName(), entityEntry.getId() )
 				);
 			}
 			NonNullableTransientDependencies dependencies = dependenciesByAction.get( dependentAction );
 			dependencies.resolveNonNullableTransientEntity( managedEntity );
 			if ( dependencies.isEmpty() ) {
-				if ( LOG.isTraceEnabled() ) {
+				if ( traceEnabled ) {
 					LOG.tracev(
 							"Resolving insert [{0}] (only depended on [{1}])",
 							dependentAction,
 							MessageHelper.infoString( entityEntry.getEntityName(), entityEntry.getId() )
 					);
 				}
 				// dependentAction only depended on managedEntity..
 				dependenciesByAction.remove( dependentAction );
 				resolvedActions.add( dependentAction );
 			}
 		}
-		if ( LOG.isTraceEnabled()  ) {
+		if ( traceEnabled  ) {
 			LOG.tracev(
 					"Unresolved inserts after resolving [{0}]: [{1}]",
 					MessageHelper.infoString( entityEntry.getEntityName(), entityEntry.getId() ),
 					toString()
 			);
 		}
 		return resolvedActions;
 	}
 
 	/**
 	 * Clear this {@link UnresolvedEntityInsertActions}.
 	 */
 	public void clear() {
 		dependenciesByAction.clear();
 		dependentActionsByTransientEntity.clear();
 	}
 
 	@Override
 	public String toString() {
 		StringBuilder sb = new StringBuilder( getClass().getSimpleName() )
 				.append( '[' );
 		for ( Map.Entry<AbstractEntityInsertAction,NonNullableTransientDependencies> entry : dependenciesByAction.entrySet() ) {
 			AbstractEntityInsertAction insert = entry.getKey();
 			NonNullableTransientDependencies dependencies = entry.getValue();
 			sb.append( "[insert=" )
 					.append( insert )
 					.append( " dependencies=[" )
 					.append( dependencies.toLoggableString( insert.getSession() ) )
 					.append( "]" );
 		}
 		sb.append( ']');
 		return sb.toString();
 	}
 
 	/**
 	 * Serialize this {@link UnresolvedEntityInsertActions} object.
 	 * @param oos - the output stream
 	 * @throws IOException if there is an error writing this object to the output stream.
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		int queueSize = dependenciesByAction.size();
 		LOG.tracev( "Starting serialization of [{0}] unresolved insert entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( AbstractEntityInsertAction unresolvedAction : dependenciesByAction.keySet() ) {
 			oos.writeObject( unresolvedAction );
 		}
 	}
 
 	/**
 	 * Deerialize a {@link UnresolvedEntityInsertActions} object.
 	 *
 	 * @param ois - the input stream.
 	 * @param session - the session.
 	 *
 	 * @return the deserialized  {@link UnresolvedEntityInsertActions} object
 	 * @throws IOException if there is an error writing this object to the output stream.
 	 * @throws ClassNotFoundException if there is a class that cannot be loaded.
 	 */
 	public static UnresolvedEntityInsertActions deserialize(
 			ObjectInputStream ois,
 			SessionImplementor session) throws IOException, ClassNotFoundException {
 
 		UnresolvedEntityInsertActions rtn = new UnresolvedEntityInsertActions();
 
 		int queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] unresolved insert entries", queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			AbstractEntityInsertAction unresolvedAction = ( AbstractEntityInsertAction ) ois.readObject();
 			unresolvedAction.afterDeserialize( session );
 			rtn.addUnresolvedEntityInsertAction(
 					unresolvedAction,
 					unresolvedAction.findNonNullableTransientEntities()
 			);
 		}
 		return rtn;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index 3e11ef67bf..aa940d9c27 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -479,2093 +479,2094 @@ public final class AnnotationBinder {
 				}
 			}
 		}
 		else if ( ann instanceof GenericGenerator ) {
 			GenericGenerator genGen = ( GenericGenerator ) ann;
 			idGen.setName( genGen.name() );
 			idGen.setIdentifierGeneratorStrategy( genGen.strategy() );
 			Parameter[] params = genGen.parameters();
 			for ( Parameter parameter : params ) {
 				idGen.addParam( parameter.name(), parameter.value() );
 			}
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Add generic generator with name: {0}", idGen.getName() );
 			}
 		}
 		else {
 			throw new AssertionFailure( "Unknown Generator annotation: " + ann );
 		}
 		return idGen;
 	}
 
 	/**
 	 * Bind a class having JSR175 annotations. Subclasses <b>have to</b> be bound after its parent class.
 	 *
 	 * @param clazzToProcess entity to bind as {@code XClass} instance
 	 * @param inheritanceStatePerClass Meta data about the inheritance relationships for all mapped classes
 	 * @param mappings Mapping meta data
 	 *
 	 * @throws MappingException in case there is an configuration error
 	 */
 	public static void bindClass(
 			XClass clazzToProcess,
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			Mappings mappings) throws MappingException {
 		//@Entity and @MappedSuperclass on the same class leads to a NPE down the road
 		if ( clazzToProcess.isAnnotationPresent( Entity.class )
 				&&  clazzToProcess.isAnnotationPresent( MappedSuperclass.class ) ) {
 			throw new AnnotationException( "An entity cannot be annotated with both @Entity and @MappedSuperclass: "
 					+ clazzToProcess.getName() );
 		}
 
 		//TODO: be more strict with secondarytable allowance (not for ids, not for secondary table join columns etc)
 		InheritanceState inheritanceState = inheritanceStatePerClass.get( clazzToProcess );
 		AnnotatedClassType classType = mappings.getClassType( clazzToProcess );
 
 		//Queries declared in MappedSuperclass should be usable in Subclasses
 		if ( AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals( classType ) ) {
 			bindQueries( clazzToProcess, mappings );
 			bindTypeDefs( clazzToProcess, mappings );
 			bindFilterDefs( clazzToProcess, mappings );
 		}
 
 		if ( !isEntityClassType( clazzToProcess, classType ) ) {
 			return;
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding entity from annotated class: %s", clazzToProcess.getName() );
 		}
 
 		PersistentClass superEntity = getSuperEntity(
 				clazzToProcess, inheritanceStatePerClass, mappings, inheritanceState
 		);
 
 		PersistentClass persistentClass = makePersistentClass( inheritanceState, superEntity );
 		Entity entityAnn = clazzToProcess.getAnnotation( Entity.class );
 		org.hibernate.annotations.Entity hibEntityAnn = clazzToProcess.getAnnotation(
 				org.hibernate.annotations.Entity.class
 		);
 		EntityBinder entityBinder = new EntityBinder(
 				entityAnn, hibEntityAnn, clazzToProcess, persistentClass, mappings
 		);
 		entityBinder.setInheritanceState( inheritanceState );
 
 		bindQueries( clazzToProcess, mappings );
 		bindFilterDefs( clazzToProcess, mappings );
 		bindTypeDefs( clazzToProcess, mappings );
 		bindFetchProfiles( clazzToProcess, mappings );
 		BinderHelper.bindAnyMetaDefs( clazzToProcess, mappings );
 
 		String schema = "";
 		String table = ""; //might be no @Table annotation on the annotated class
 		String catalog = "";
 		List<UniqueConstraintHolder> uniqueConstraints = new ArrayList<UniqueConstraintHolder>();
 		javax.persistence.Table tabAnn = null;
 		if ( clazzToProcess.isAnnotationPresent( javax.persistence.Table.class ) ) {
 			tabAnn = clazzToProcess.getAnnotation( javax.persistence.Table.class );
 			table = tabAnn.name();
 			schema = tabAnn.schema();
 			catalog = tabAnn.catalog();
 			uniqueConstraints = TableBinder.buildUniqueConstraintHolders( tabAnn.uniqueConstraints() );
 		}
 
 		Ejb3JoinColumn[] inheritanceJoinedColumns = makeInheritanceJoinColumns(
 				clazzToProcess, mappings, inheritanceState, superEntity
 		);
 		Ejb3DiscriminatorColumn discriminatorColumn = null;
 		if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.getType() ) ) {
 			discriminatorColumn = processDiscriminatorProperties(
 					clazzToProcess, mappings, inheritanceState, entityBinder
 			);
 		}
 
 		entityBinder.setProxy( clazzToProcess.getAnnotation( Proxy.class ) );
 		entityBinder.setBatchSize( clazzToProcess.getAnnotation( BatchSize.class ) );
 		entityBinder.setWhere( clazzToProcess.getAnnotation( Where.class ) );
 	    entityBinder.setCache( determineCacheSettings( clazzToProcess, mappings ) );
 	    entityBinder.setNaturalIdCache( clazzToProcess, clazzToProcess.getAnnotation( NaturalIdCache.class ) );
 
 		bindFilters( clazzToProcess, entityBinder, mappings );
 
 		entityBinder.bindEntity();
 
 		if ( inheritanceState.hasTable() ) {
 			Check checkAnn = clazzToProcess.getAnnotation( Check.class );
 			String constraints = checkAnn == null ?
 					null :
 					checkAnn.constraints();
 			entityBinder.bindTable(
 					schema, catalog, table, uniqueConstraints,
 					constraints, inheritanceState.hasDenormalizedTable() ?
 							superEntity.getTable() :
 							null
 			);
 		}
 		else if ( clazzToProcess.isAnnotationPresent( Table.class ) ) {
 			LOG.invalidTableAnnotation( clazzToProcess.getName() );
 		}
 
 
 		PropertyHolder propertyHolder = PropertyHolderBuilder.buildPropertyHolder(
 				clazzToProcess,
 				persistentClass,
 				entityBinder, mappings, inheritanceStatePerClass
 		);
 
 		javax.persistence.SecondaryTable secTabAnn = clazzToProcess.getAnnotation(
 				javax.persistence.SecondaryTable.class
 		);
 		javax.persistence.SecondaryTables secTabsAnn = clazzToProcess.getAnnotation(
 				javax.persistence.SecondaryTables.class
 		);
 		entityBinder.firstLevelSecondaryTablesBinding( secTabAnn, secTabsAnn );
 
 		OnDelete onDeleteAnn = clazzToProcess.getAnnotation( OnDelete.class );
 		boolean onDeleteAppropriate = false;
 		if ( InheritanceType.JOINED.equals( inheritanceState.getType() ) && inheritanceState.hasParents() ) {
 			onDeleteAppropriate = true;
 			final JoinedSubclass jsc = ( JoinedSubclass ) persistentClass;
 			SimpleValue key = new DependantValue( mappings, jsc.getTable(), jsc.getIdentifier() );
 			jsc.setKey( key );
 			ForeignKey fk = clazzToProcess.getAnnotation( ForeignKey.class );
 			if ( fk != null && !BinderHelper.isEmptyAnnotationValue( fk.name() ) ) {
 				key.setForeignKeyName( fk.name() );
 			}
 			if ( onDeleteAnn != null ) {
 				key.setCascadeDeleteEnabled( OnDeleteAction.CASCADE.equals( onDeleteAnn.action() ) );
 			}
 			else {
 				key.setCascadeDeleteEnabled( false );
 			}
 			//we are never in a second pass at that stage, so queue it
 			SecondPass sp = new JoinedSubclassFkSecondPass( jsc, inheritanceJoinedColumns, key, mappings );
 			mappings.addSecondPass( sp );
 			mappings.addSecondPass( new CreateKeySecondPass( jsc ) );
 
 		}
 		else if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.getType() ) ) {
 			if ( ! inheritanceState.hasParents() ) {
 				if ( inheritanceState.hasSiblings() || !discriminatorColumn.isImplicit() ) {
 					//need a discriminator column
 					bindDiscriminatorToPersistentClass(
 							( RootClass ) persistentClass,
 							discriminatorColumn,
 							entityBinder.getSecondaryTables(),
 							propertyHolder,
 							mappings
 					);
 					entityBinder.bindDiscriminatorValue();//bind it again since the type might have changed
 				}
 			}
 		}
 		else if ( InheritanceType.TABLE_PER_CLASS.equals( inheritanceState.getType() ) ) {
 			//nothing to do
 		}
         if (onDeleteAnn != null && !onDeleteAppropriate) LOG.invalidOnDeleteAnnotation(propertyHolder.getEntityName());
 
 		// try to find class level generators
 		HashMap<String, IdGenerator> classGenerators = buildLocalGenerators( clazzToProcess, mappings );
 
 		// check properties
 		final InheritanceState.ElementsToProcess elementsToProcess = inheritanceState.getElementsToProcess();
 		inheritanceState.postProcess( persistentClass, entityBinder );
 
 		final boolean subclassAndSingleTableStrategy = inheritanceState.getType() == InheritanceType.SINGLE_TABLE
 				&& inheritanceState.hasParents();
 		Set<String> idPropertiesIfIdClass = new HashSet<String>();
 		boolean isIdClass = mapAsIdClass(
 				inheritanceStatePerClass,
 				inheritanceState,
 				persistentClass,
 				entityBinder,
 				propertyHolder,
 				elementsToProcess,
 				idPropertiesIfIdClass,
 				mappings
 		);
 
 		if ( !isIdClass ) {
 			entityBinder.setWrapIdsInEmbeddedComponents( elementsToProcess.getIdPropertyCount() > 1 );
 		}
 
 		processIdPropertiesIfNotAlready(
 				inheritanceStatePerClass,
 				mappings,
 				persistentClass,
 				entityBinder,
 				propertyHolder,
 				classGenerators,
 				elementsToProcess,
 				subclassAndSingleTableStrategy,
 				idPropertiesIfIdClass
 		);
 
 		if ( !inheritanceState.hasParents() ) {
 			final RootClass rootClass = ( RootClass ) persistentClass;
 			mappings.addSecondPass( new CreateKeySecondPass( rootClass ) );
 		}
 		else {
 			superEntity.addSubclass( ( Subclass ) persistentClass );
 		}
 
 		mappings.addClass( persistentClass );
 
 		//Process secondary tables and complementary definitions (ie o.h.a.Table)
 		mappings.addSecondPass( new SecondaryTableSecondPass( entityBinder, propertyHolder, clazzToProcess ) );
 
 		//add process complementary Table definition (index & all)
 		entityBinder.processComplementaryTableDefinitions( clazzToProcess.getAnnotation( org.hibernate.annotations.Table.class ) );
 		entityBinder.processComplementaryTableDefinitions( clazzToProcess.getAnnotation( org.hibernate.annotations.Tables.class ) );
 		entityBinder.processComplementaryTableDefinitions( tabAnn );
 	}
 
 	// parse everything discriminator column relevant in case of single table inheritance
 	private static Ejb3DiscriminatorColumn processDiscriminatorProperties(XClass clazzToProcess, Mappings mappings, InheritanceState inheritanceState, EntityBinder entityBinder) {
 		Ejb3DiscriminatorColumn discriminatorColumn = null;
 		javax.persistence.DiscriminatorColumn discAnn = clazzToProcess.getAnnotation(
 				javax.persistence.DiscriminatorColumn.class
 		);
 		DiscriminatorType discriminatorType = discAnn != null ?
 				discAnn.discriminatorType() :
 				DiscriminatorType.STRING;
 
 		org.hibernate.annotations.DiscriminatorFormula discFormulaAnn = clazzToProcess.getAnnotation(
 				org.hibernate.annotations.DiscriminatorFormula.class
 		);
 		if ( !inheritanceState.hasParents() ) {
 			discriminatorColumn = Ejb3DiscriminatorColumn.buildDiscriminatorColumn(
 					discriminatorType, discAnn, discFormulaAnn, mappings
 			);
 		}
 		if ( discAnn != null && inheritanceState.hasParents() ) {
 			LOG.invalidDiscriminatorAnnotation( clazzToProcess.getName() );
 		}
 
 		String discrimValue = clazzToProcess.isAnnotationPresent( DiscriminatorValue.class ) ?
 				clazzToProcess.getAnnotation( DiscriminatorValue.class ).value() :
 				null;
 		entityBinder.setDiscriminatorValue( discrimValue );
 
 		DiscriminatorOptions discriminatorOptions = clazzToProcess.getAnnotation( DiscriminatorOptions.class );
 		if ( discriminatorOptions != null) {
 			entityBinder.setForceDiscriminator( discriminatorOptions.force() );
 			entityBinder.setInsertableDiscriminator( discriminatorOptions.insert() );
 		}
 
 		return discriminatorColumn;
 	}
 
 	private static void processIdPropertiesIfNotAlready(
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			Mappings mappings,
 			PersistentClass persistentClass,
 			EntityBinder entityBinder,
 			PropertyHolder propertyHolder,
 			HashMap<String, IdGenerator> classGenerators,
 			InheritanceState.ElementsToProcess elementsToProcess,
 			boolean subclassAndSingleTableStrategy,
 			Set<String> idPropertiesIfIdClass) {
 		Set<String> missingIdProperties = new HashSet<String>( idPropertiesIfIdClass );
 		for ( PropertyData propertyAnnotatedElement : elementsToProcess.getElements() ) {
 			String propertyName = propertyAnnotatedElement.getPropertyName();
 			if ( !idPropertiesIfIdClass.contains( propertyName ) ) {
 				processElementAnnotations(
 						propertyHolder,
 						subclassAndSingleTableStrategy ?
 								Nullability.FORCED_NULL :
 								Nullability.NO_CONSTRAINT,
 						propertyAnnotatedElement, classGenerators, entityBinder,
 						false, false, false, mappings, inheritanceStatePerClass
 				);
 			}
 			else {
 				missingIdProperties.remove( propertyName );
 			}
 		}
 
 		if ( missingIdProperties.size() != 0 ) {
 			StringBuilder missings = new StringBuilder();
 			for ( String property : missingIdProperties ) {
 				missings.append( property ).append( ", " );
 			}
 			throw new AnnotationException(
 					"Unable to find properties ("
 							+ missings.substring( 0, missings.length() - 2 )
 							+ ") in entity annotated with @IdClass:" + persistentClass.getEntityName()
 			);
 		}
 	}
 
 	private static boolean mapAsIdClass(
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			InheritanceState inheritanceState,
 			PersistentClass persistentClass,
 			EntityBinder entityBinder,
 			PropertyHolder propertyHolder,
 			InheritanceState.ElementsToProcess elementsToProcess,
 			Set<String> idPropertiesIfIdClass,
 			Mappings mappings) {
 		/*
 		 * We are looking for @IdClass
 		 * In general we map the id class as identifier using the mapping metadata of the main entity's properties
 		 * and we create an identifier mapper containing the id properties of the main entity
 		 *
 		 * In JPA 2, there is a shortcut if the id class is the Pk of the associated class pointed to by the id
 		 * it ought to be treated as an embedded and not a real IdClass (at least in the Hibernate's internal way
 		 */
 		XClass classWithIdClass = inheritanceState.getClassWithIdClass( false );
 		if ( classWithIdClass != null ) {
 			IdClass idClass = classWithIdClass.getAnnotation( IdClass.class );
 			XClass compositeClass = mappings.getReflectionManager().toXClass( idClass.value() );
 			PropertyData inferredData = new PropertyPreloadedData(
 					entityBinder.getPropertyAccessType(), "id", compositeClass
 			);
 			PropertyData baseInferredData = new PropertyPreloadedData(
 					entityBinder.getPropertyAccessType(), "id", classWithIdClass
 			);
 			AccessType propertyAccessor = entityBinder.getPropertyAccessor( compositeClass );
 			//In JPA 2, there is a shortcut if the IdClass is the Pk of the associated class pointed to by the id
 			//it ought to be treated as an embedded and not a real IdClass (at least in the Hibernate's internal way
 			final boolean isFakeIdClass = isIdClassPkOfTheAssociatedEntity(
 					elementsToProcess,
 					compositeClass,
 					inferredData,
 					baseInferredData,
 					propertyAccessor,
 					inheritanceStatePerClass,
 					mappings
 			);
 
 			if ( isFakeIdClass ) {
 				return false;
 			}
 
 			boolean isComponent = true;
 			String generatorType = "assigned";
 			String generator = BinderHelper.ANNOTATION_STRING_DEFAULT;
 
 			boolean ignoreIdAnnotations = entityBinder.isIgnoreIdAnnotations();
 			entityBinder.setIgnoreIdAnnotations( true );
 			propertyHolder.setInIdClass( true );
 			bindIdClass(
 					generatorType,
 					generator,
 					inferredData,
 					baseInferredData,
 					null,
 					propertyHolder,
 					isComponent,
 					propertyAccessor,
 					entityBinder,
 					true,
 					false,
 					mappings,
 					inheritanceStatePerClass
 			);
 			propertyHolder.setInIdClass( null );
 			inferredData = new PropertyPreloadedData(
 					propertyAccessor, "_identifierMapper", compositeClass
 			);
 			Component mapper = fillComponent(
 					propertyHolder,
 					inferredData,
 					baseInferredData,
 					propertyAccessor,
 					false,
 					entityBinder,
 					true,
 					true,
 					false,
 					mappings,
 					inheritanceStatePerClass
 			);
 			entityBinder.setIgnoreIdAnnotations( ignoreIdAnnotations );
 			persistentClass.setIdentifierMapper( mapper );
 
 			//If id definition is on a mapped superclass, update the mapping
 			final org.hibernate.mapping.MappedSuperclass superclass =
 					BinderHelper.getMappedSuperclassOrNull(
 							inferredData.getDeclaringClass(),
 							inheritanceStatePerClass,
 							mappings
 					);
 			if ( superclass != null ) {
 				superclass.setDeclaredIdentifierMapper( mapper );
 			}
 			else {
 				//we are for sure on the entity
 				persistentClass.setDeclaredIdentifierMapper( mapper );
 			}
 
 			Property property = new Property();
 			property.setName( "_identifierMapper" );
 			property.setNodeName( "id" );
 			property.setUpdateable( false );
 			property.setInsertable( false );
 			property.setValue( mapper );
 			property.setPropertyAccessorName( "embedded" );
 			persistentClass.addProperty( property );
 			entityBinder.setIgnoreIdAnnotations( true );
 
 			Iterator properties = mapper.getPropertyIterator();
 			while ( properties.hasNext() ) {
 				idPropertiesIfIdClass.add( ( ( Property ) properties.next() ).getName() );
 			}
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	private static boolean isIdClassPkOfTheAssociatedEntity(
 			InheritanceState.ElementsToProcess elementsToProcess,
 			XClass compositeClass,
 			PropertyData inferredData,
 			PropertyData baseInferredData,
 			AccessType propertyAccessor,
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			Mappings mappings) {
 		if ( elementsToProcess.getIdPropertyCount() == 1 ) {
 			final PropertyData idPropertyOnBaseClass = getUniqueIdPropertyFromBaseClass(
 					inferredData, baseInferredData, propertyAccessor, mappings
 			);
 			final InheritanceState state = inheritanceStatePerClass.get( idPropertyOnBaseClass.getClassOrElement() );
 			if ( state == null ) {
 				return false; //while it is likely a user error, let's consider it is something that might happen
 			}
 			final XClass associatedClassWithIdClass = state.getClassWithIdClass( true );
 			if ( associatedClassWithIdClass == null ) {
 				//we cannot know for sure here unless we try and find the @EmbeddedId
 				//Let's not do this thorough checking but do some extra validation
 				final XProperty property = idPropertyOnBaseClass.getProperty();
 				return property.isAnnotationPresent( ManyToOne.class )
 						|| property.isAnnotationPresent( OneToOne.class );
 
 			}
 			else {
 				final XClass idClass = mappings.getReflectionManager().toXClass(
 						associatedClassWithIdClass.getAnnotation( IdClass.class ).value()
 				);
 				return idClass.equals( compositeClass );
 			}
 		}
 		else {
 			return false;
 		}
 	}
 
 	private static Cache determineCacheSettings(XClass clazzToProcess, Mappings mappings) {
 		Cache cacheAnn = clazzToProcess.getAnnotation( Cache.class );
 		if ( cacheAnn != null ) {
 			return cacheAnn;
 		}
 
 		Cacheable cacheableAnn = clazzToProcess.getAnnotation( Cacheable.class );
 		SharedCacheMode mode = determineSharedCacheMode( mappings );
 		switch ( mode ) {
 			case ALL: {
 				cacheAnn = buildCacheMock( clazzToProcess.getName(), mappings );
 				break;
 			}
 			case ENABLE_SELECTIVE: {
 				if ( cacheableAnn != null && cacheableAnn.value() ) {
 					cacheAnn = buildCacheMock( clazzToProcess.getName(), mappings );
 				}
 				break;
 			}
 			case DISABLE_SELECTIVE: {
 				if ( cacheableAnn == null || cacheableAnn.value() ) {
 					cacheAnn = buildCacheMock( clazzToProcess.getName(), mappings );
 				}
 				break;
 			}
 			default: {
 				// treat both NONE and UNSPECIFIED the same
 				break;
 			}
 		}
 		return cacheAnn;
 	}
 
 	private static SharedCacheMode determineSharedCacheMode(Mappings mappings) {
 		SharedCacheMode mode;
 		final Object value = mappings.getConfigurationProperties().get( "javax.persistence.sharedCache.mode" );
 		if ( value == null ) {
 			LOG.debug( "No value specified for 'javax.persistence.sharedCache.mode'; using UNSPECIFIED" );
 			mode = SharedCacheMode.UNSPECIFIED;
 		}
 		else {
 			if ( SharedCacheMode.class.isInstance( value ) ) {
 				mode = ( SharedCacheMode ) value;
 			}
 			else {
 				try {
 					mode = SharedCacheMode.valueOf( value.toString() );
 				}
 				catch ( Exception e ) {
 					LOG.debugf( "Unable to resolve given mode name [%s]; using UNSPECIFIED : %s", value, e );
 					mode = SharedCacheMode.UNSPECIFIED;
 				}
 			}
 		}
 		return mode;
 	}
 
 	private static Cache buildCacheMock(String region, Mappings mappings) {
 		return new LocalCacheAnnotationImpl( region, determineCacheConcurrencyStrategy( mappings ) );
 	}
 
 	private static CacheConcurrencyStrategy DEFAULT_CACHE_CONCURRENCY_STRATEGY;
 
 	static void prepareDefaultCacheConcurrencyStrategy(Properties properties) {
 		if ( DEFAULT_CACHE_CONCURRENCY_STRATEGY != null ) {
 			LOG.trace( "Default cache concurrency strategy already defined" );
 			return;
 		}
 
 		if ( !properties.containsKey( AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY ) ) {
 			LOG.trace( "Given properties did not contain any default cache concurrency strategy setting" );
 			return;
 		}
 
 		final String strategyName = properties.getProperty( AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY );
 		LOG.tracev( "Discovered default cache concurrency strategy via config [{0}]", strategyName );
 		CacheConcurrencyStrategy strategy = CacheConcurrencyStrategy.parse( strategyName );
 		if ( strategy == null ) {
 			LOG.trace( "Discovered default cache concurrency strategy specified nothing" );
 			return;
 		}
 
 		LOG.debugf( "Setting default cache concurrency strategy via config [%s]", strategy.name() );
 		DEFAULT_CACHE_CONCURRENCY_STRATEGY = strategy;
 	}
 
 	private static CacheConcurrencyStrategy determineCacheConcurrencyStrategy(Mappings mappings) {
 		if ( DEFAULT_CACHE_CONCURRENCY_STRATEGY == null ) {
 			final RegionFactory cacheRegionFactory = SettingsFactory.createRegionFactory(
 					mappings.getConfigurationProperties(), true
 			);
 			DEFAULT_CACHE_CONCURRENCY_STRATEGY = CacheConcurrencyStrategy.fromAccessType( cacheRegionFactory.getDefaultAccessType() );
 		}
 		return DEFAULT_CACHE_CONCURRENCY_STRATEGY;
 	}
 
 	@SuppressWarnings({ "ClassExplicitlyAnnotation" })
 	private static class LocalCacheAnnotationImpl implements Cache {
 		private final String region;
 		private final CacheConcurrencyStrategy usage;
 
 		private LocalCacheAnnotationImpl(String region, CacheConcurrencyStrategy usage) {
 			this.region = region;
 			this.usage = usage;
 		}
 
 		public CacheConcurrencyStrategy usage() {
 			return usage;
 		}
 
 		public String region() {
 			return region;
 		}
 
 		public String include() {
 			return "all";
 		}
 
 		public Class<? extends Annotation> annotationType() {
 			return Cache.class;
 		}
 	}
 
 	private static PersistentClass makePersistentClass(InheritanceState inheritanceState, PersistentClass superEntity) {
 		//we now know what kind of persistent entity it is
 		PersistentClass persistentClass;
 		//create persistent class
 		if ( !inheritanceState.hasParents() ) {
 			persistentClass = new RootClass();
 		}
 		else if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.getType() ) ) {
 			persistentClass = new SingleTableSubclass( superEntity );
 		}
 		else if ( InheritanceType.JOINED.equals( inheritanceState.getType() ) ) {
 			persistentClass = new JoinedSubclass( superEntity );
 		}
 		else if ( InheritanceType.TABLE_PER_CLASS.equals( inheritanceState.getType() ) ) {
 			persistentClass = new UnionSubclass( superEntity );
 		}
 		else {
 			throw new AssertionFailure( "Unknown inheritance type: " + inheritanceState.getType() );
 		}
 		return persistentClass;
 	}
 
 	private static Ejb3JoinColumn[] makeInheritanceJoinColumns(
 			XClass clazzToProcess,
 			Mappings mappings,
 			InheritanceState inheritanceState,
 			PersistentClass superEntity) {
 		Ejb3JoinColumn[] inheritanceJoinedColumns = null;
 		final boolean hasJoinedColumns = inheritanceState.hasParents()
 				&& InheritanceType.JOINED.equals( inheritanceState.getType() );
 		if ( hasJoinedColumns ) {
 			//@Inheritance(JOINED) subclass need to link back to the super entity
 			PrimaryKeyJoinColumns jcsAnn = clazzToProcess.getAnnotation( PrimaryKeyJoinColumns.class );
 			boolean explicitInheritanceJoinedColumns = jcsAnn != null && jcsAnn.value().length != 0;
 			if ( explicitInheritanceJoinedColumns ) {
 				int nbrOfInhJoinedColumns = jcsAnn.value().length;
 				PrimaryKeyJoinColumn jcAnn;
 				inheritanceJoinedColumns = new Ejb3JoinColumn[nbrOfInhJoinedColumns];
 				for ( int colIndex = 0; colIndex < nbrOfInhJoinedColumns; colIndex++ ) {
 					jcAnn = jcsAnn.value()[colIndex];
 					inheritanceJoinedColumns[colIndex] = Ejb3JoinColumn.buildJoinColumn(
 							jcAnn, null, superEntity.getIdentifier(),
 							null, null, mappings
 					);
 				}
 			}
 			else {
 				PrimaryKeyJoinColumn jcAnn = clazzToProcess.getAnnotation( PrimaryKeyJoinColumn.class );
 				inheritanceJoinedColumns = new Ejb3JoinColumn[1];
 				inheritanceJoinedColumns[0] = Ejb3JoinColumn.buildJoinColumn(
 						jcAnn, null, superEntity.getIdentifier(),
 						null, null, mappings
 				);
 			}
 			LOG.trace( "Subclass joined column(s) created" );
 		}
 		else {
 			if ( clazzToProcess.isAnnotationPresent( PrimaryKeyJoinColumns.class )
 					|| clazzToProcess.isAnnotationPresent( PrimaryKeyJoinColumn.class ) ) {
 				LOG.invalidPrimaryKeyJoinColumnAnnotation();
 			}
 		}
 		return inheritanceJoinedColumns;
 	}
 
 	private static PersistentClass getSuperEntity(XClass clazzToProcess, Map<XClass, InheritanceState> inheritanceStatePerClass, Mappings mappings, InheritanceState inheritanceState) {
 		InheritanceState superEntityState = InheritanceState.getInheritanceStateOfSuperEntity(
 				clazzToProcess, inheritanceStatePerClass
 		);
 		PersistentClass superEntity = superEntityState != null ?
 				mappings.getClass(
 						superEntityState.getClazz().getName()
 				) :
 				null;
 		if ( superEntity == null ) {
 			//check if superclass is not a potential persistent class
 			if ( inheritanceState.hasParents() ) {
 				throw new AssertionFailure(
 						"Subclass has to be binded after it's mother class: "
 								+ superEntityState.getClazz().getName()
 				);
 			}
 		}
 		return superEntity;
 	}
 
 	private static boolean isEntityClassType(XClass clazzToProcess, AnnotatedClassType classType) {
 		if ( AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals( classType ) //will be processed by their subentities
 				|| AnnotatedClassType.NONE.equals( classType ) //to be ignored
 				|| AnnotatedClassType.EMBEDDABLE.equals( classType ) //allow embeddable element declaration
 				) {
 			if ( AnnotatedClassType.NONE.equals( classType )
 					&& clazzToProcess.isAnnotationPresent( org.hibernate.annotations.Entity.class ) ) {
 				LOG.missingEntityAnnotation( clazzToProcess.getName() );
 			}
 			return false;
 		}
 
 		if ( !classType.equals( AnnotatedClassType.ENTITY ) ) {
 			throw new AnnotationException(
 					"Annotated class should have a @javax.persistence.Entity, @javax.persistence.Embeddable or @javax.persistence.EmbeddedSuperclass annotation: " + clazzToProcess
 							.getName()
 			);
 		}
 
 		return true;
 	}
 
 	/*
 	 * Process the filters defined on the given class, as well as all filters defined
 	 * on the MappedSuperclass(s) in the inheritance hierarchy
 	 */
 
 	private static void bindFilters(XClass annotatedClass, EntityBinder entityBinder,
 									Mappings mappings) {
 
 		bindFilters( annotatedClass, entityBinder );
 
 		XClass classToProcess = annotatedClass.getSuperclass();
 		while ( classToProcess != null ) {
 			AnnotatedClassType classType = mappings.getClassType( classToProcess );
 			if ( AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals( classType ) ) {
 				bindFilters( classToProcess, entityBinder );
 			}
 			classToProcess = classToProcess.getSuperclass();
 		}
 
 	}
 
 	private static void bindFilters(XAnnotatedElement annotatedElement, EntityBinder entityBinder) {
 
 		Filters filtersAnn = annotatedElement.getAnnotation( Filters.class );
 		if ( filtersAnn != null ) {
 			for ( Filter filter : filtersAnn.value() ) {
 				entityBinder.addFilter(filter);
 			}
 		}
 
 		Filter filterAnn = annotatedElement.getAnnotation( Filter.class );
 		if ( filterAnn != null ) {
 			entityBinder.addFilter(filterAnn);
 		}
 	}
 
 	private static void bindFilterDefs(XAnnotatedElement annotatedElement, Mappings mappings) {
 		FilterDef defAnn = annotatedElement.getAnnotation( FilterDef.class );
 		FilterDefs defsAnn = annotatedElement.getAnnotation( FilterDefs.class );
 		if ( defAnn != null ) {
 			bindFilterDef( defAnn, mappings );
 		}
 		if ( defsAnn != null ) {
 			for ( FilterDef def : defsAnn.value() ) {
 				bindFilterDef( def, mappings );
 			}
 		}
 	}
 
 	private static void bindFilterDef(FilterDef defAnn, Mappings mappings) {
 		Map<String, org.hibernate.type.Type> params = new HashMap<String, org.hibernate.type.Type>();
 		for ( ParamDef param : defAnn.parameters() ) {
 			params.put( param.name(), mappings.getTypeResolver().heuristicType( param.type() ) );
 		}
 		FilterDefinition def = new FilterDefinition( defAnn.name(), defAnn.defaultCondition(), params );
 		LOG.debugf( "Binding filter definition: %s", def.getFilterName() );
 		mappings.addFilterDefinition( def );
 	}
 
 	private static void bindTypeDefs(XAnnotatedElement annotatedElement, Mappings mappings) {
 		TypeDef defAnn = annotatedElement.getAnnotation( TypeDef.class );
 		TypeDefs defsAnn = annotatedElement.getAnnotation( TypeDefs.class );
 		if ( defAnn != null ) {
 			bindTypeDef( defAnn, mappings );
 		}
 		if ( defsAnn != null ) {
 			for ( TypeDef def : defsAnn.value() ) {
 				bindTypeDef( def, mappings );
 			}
 		}
 	}
 
 	private static void bindFetchProfiles(XAnnotatedElement annotatedElement, Mappings mappings) {
 		FetchProfile fetchProfileAnnotation = annotatedElement.getAnnotation( FetchProfile.class );
 		FetchProfiles fetchProfileAnnotations = annotatedElement.getAnnotation( FetchProfiles.class );
 		if ( fetchProfileAnnotation != null ) {
 			bindFetchProfile( fetchProfileAnnotation, mappings );
 		}
 		if ( fetchProfileAnnotations != null ) {
 			for ( FetchProfile profile : fetchProfileAnnotations.value() ) {
 				bindFetchProfile( profile, mappings );
 			}
 		}
 	}
 
 	private static void bindFetchProfile(FetchProfile fetchProfileAnnotation, Mappings mappings) {
 		for ( FetchProfile.FetchOverride fetch : fetchProfileAnnotation.fetchOverrides() ) {
 			org.hibernate.annotations.FetchMode mode = fetch.mode();
 			if ( !mode.equals( org.hibernate.annotations.FetchMode.JOIN ) ) {
 				throw new MappingException( "Only FetchMode.JOIN is currently supported" );
 			}
 
 			SecondPass sp = new VerifyFetchProfileReferenceSecondPass( fetchProfileAnnotation.name(), fetch, mappings );
 			mappings.addSecondPass( sp );
 		}
 	}
 
 	private static void bindTypeDef(TypeDef defAnn, Mappings mappings) {
 		Properties params = new Properties();
 		for ( Parameter param : defAnn.parameters() ) {
 			params.setProperty( param.name(), param.value() );
 		}
 
 		if ( BinderHelper.isEmptyAnnotationValue( defAnn.name() ) && defAnn.defaultForType().equals( void.class ) ) {
 			throw new AnnotationException(
 					"Either name or defaultForType (or both) attribute should be set in TypeDef having typeClass " +
 							defAnn.typeClass().getName()
 			);
 		}
 
 		final String typeBindMessageF = "Binding type definition: %s";
 		if ( !BinderHelper.isEmptyAnnotationValue( defAnn.name() ) ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( typeBindMessageF, defAnn.name() );
 			}
 			mappings.addTypeDef( defAnn.name(), defAnn.typeClass().getName(), params );
 		}
 		if ( !defAnn.defaultForType().equals( void.class ) ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( typeBindMessageF, defAnn.defaultForType().getName() );
 			}
 			mappings.addTypeDef( defAnn.defaultForType().getName(), defAnn.typeClass().getName(), params );
 		}
 
 	}
 
 
 	private static void bindDiscriminatorToPersistentClass(
 			RootClass rootClass,
 			Ejb3DiscriminatorColumn discriminatorColumn,
 			Map<String, Join> secondaryTables,
 			PropertyHolder propertyHolder,
 			Mappings mappings) {
 		if ( rootClass.getDiscriminator() == null ) {
 			if ( discriminatorColumn == null ) {
 				throw new AssertionFailure( "discriminator column should have been built" );
 			}
 			discriminatorColumn.setJoins( secondaryTables );
 			discriminatorColumn.setPropertyHolder( propertyHolder );
 			SimpleValue discrim = new SimpleValue( mappings, rootClass.getTable() );
 			rootClass.setDiscriminator( discrim );
 			discriminatorColumn.linkWithValue( discrim );
 			discrim.setTypeName( discriminatorColumn.getDiscriminatorTypeName() );
 			rootClass.setPolymorphic( true );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Setting discriminator for entity {0}", rootClass.getEntityName() );
 			}
 		}
 	}
 
 	/**
 	 * @param elements List of {@code ProperyData} instances
 	 * @param defaultAccessType The default value access strategy which has to be used in case no explicit local access
 	 * strategy is used
 	 * @param propertyContainer Metadata about a class and its properties
 	 * @param mappings Mapping meta data
 	 *
 	 * @return the number of id properties found while iterating the elements of {@code annotatedClass} using
 	 *         the determined access strategy, {@code false} otherwise.
 	 */
 	static int addElementsOfClass(
 			List<PropertyData> elements,
 			AccessType defaultAccessType,
 			PropertyContainer propertyContainer,
 			Mappings mappings) {
 		int idPropertyCounter = 0;
 		AccessType accessType = defaultAccessType;
 
 		if ( propertyContainer.hasExplicitAccessStrategy() ) {
 			accessType = propertyContainer.getExplicitAccessStrategy();
 		}
 
 		Collection<XProperty> properties = propertyContainer.getProperties( accessType );
 		for ( XProperty p : properties ) {
 			final int currentIdPropertyCounter = addProperty(
 					propertyContainer, p, elements, accessType.getType(), mappings
 			);
 			idPropertyCounter += currentIdPropertyCounter;
 		}
 		return idPropertyCounter;
 	}
 
 	private static int addProperty(
 			PropertyContainer propertyContainer,
 			XProperty property,
 			List<PropertyData> annElts,
 			String propertyAccessor,
 			Mappings mappings) {
 		final XClass declaringClass = propertyContainer.getDeclaringClass();
 		final XClass entity = propertyContainer.getEntityAtStake();
 		int idPropertyCounter = 0;
 		PropertyData propertyAnnotatedElement = new PropertyInferredData(
 				declaringClass, property, propertyAccessor,
 				mappings.getReflectionManager()
 		);
 
 		/*
 		 * put element annotated by @Id in front
 		 * since it has to be parsed before any association by Hibernate
 		 */
 		final XAnnotatedElement element = propertyAnnotatedElement.getProperty();
 		if ( element.isAnnotationPresent( Id.class ) || element.isAnnotationPresent( EmbeddedId.class ) ) {
 			annElts.add( 0, propertyAnnotatedElement );
 			/**
 			 * The property must be put in hibernate.properties as it's a system wide property. Fixable?
 			 * TODO support true/false/default on the property instead of present / not present
 			 * TODO is @Column mandatory?
 			 * TODO add method support
 			 */
 			if ( mappings.isSpecjProprietarySyntaxEnabled() ) {
 				if ( element.isAnnotationPresent( Id.class ) && element.isAnnotationPresent( Column.class ) ) {
 					String columnName = element.getAnnotation( Column.class ).name();
 					for ( XProperty prop : declaringClass.getDeclaredProperties( AccessType.FIELD.getType() ) ) {
 						if ( !prop.isAnnotationPresent( MapsId.class ) ) {
 							/**
 							 * The detection of a configured individual JoinColumn differs between Annotation
 							 * and XML configuration processing.
 							 */
 							boolean isRequiredAnnotationPresent = false;
 							JoinColumns groupAnnotation = prop.getAnnotation( JoinColumns.class );
 							if ( (prop.isAnnotationPresent( JoinColumn.class )
 									&& prop.getAnnotation( JoinColumn.class ).name().equals( columnName )) ) {
 								isRequiredAnnotationPresent = true;
 							}
 							else if ( prop.isAnnotationPresent( JoinColumns.class ) ) {
 								for ( JoinColumn columnAnnotation : groupAnnotation.value() ) {
 									if ( columnName.equals( columnAnnotation.name() ) ) {
 										isRequiredAnnotationPresent = true;
 										break;
 									}
 								}
 							}
 							if ( isRequiredAnnotationPresent ) {
 								//create a PropertyData fpr the specJ property holding the mapping
 								PropertyData specJPropertyData = new PropertyInferredData(
 										declaringClass,
 										//same dec
 										prop,
 										// the actual @XToOne property
 										propertyAccessor,
 										//TODO we should get the right accessor but the same as id would do
 										mappings.getReflectionManager()
 								);
 								mappings.addPropertyAnnotatedWithMapsIdSpecj(
 										entity,
 										specJPropertyData,
 										element.toString()
 								);
 							}
 						}
 					}
 				}
 			}
 
 			if ( element.isAnnotationPresent( ManyToOne.class ) || element.isAnnotationPresent( OneToOne.class ) ) {
 				mappings.addToOneAndIdProperty( entity, propertyAnnotatedElement );
 			}
 			idPropertyCounter++;
 		}
 		else {
 			annElts.add( propertyAnnotatedElement );
 		}
 		if ( element.isAnnotationPresent( MapsId.class ) ) {
 			mappings.addPropertyAnnotatedWithMapsId( entity, propertyAnnotatedElement );
 		}
 
 		return idPropertyCounter;
 	}
 
 	/*
 	 * Process annotation of a particular property
 	 */
 
 	private static void processElementAnnotations(
 			PropertyHolder propertyHolder,
 			Nullability nullability,
 			PropertyData inferredData,
 			HashMap<String, IdGenerator> classGenerators,
 			EntityBinder entityBinder,
 			boolean isIdentifierMapper,
 			boolean isComponentEmbedded,
 			boolean inSecondPass,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) throws MappingException {
 		/**
 		 * inSecondPass can only be used to apply right away the second pass of a composite-element
 		 * Because it's a value type, there is no bidirectional association, hence second pass
 		 * ordering does not matter
 		 */
 
-		if ( LOG.isTraceEnabled() ) {
+		final boolean traceEnabled = LOG.isTraceEnabled();
+		if ( traceEnabled ) {
 			LOG.tracev( "Processing annotations of {0}.{1}" , propertyHolder.getEntityName(), inferredData.getPropertyName() );
 		}
 
 		final XProperty property = inferredData.getProperty();
 		if ( property.isAnnotationPresent( Parent.class ) ) {
 			if ( propertyHolder.isComponent() ) {
 				propertyHolder.setParentProperty( property.getName() );
 			}
 			else {
 				throw new AnnotationException(
 						"@Parent cannot be applied outside an embeddable object: "
 								+ BinderHelper.getPath( propertyHolder, inferredData )
 				);
 			}
 			return;
 		}
 
 		ColumnsBuilder columnsBuilder = new ColumnsBuilder(
 				propertyHolder, nullability, property, inferredData, entityBinder, mappings
 		).extractMetadata();
 		Ejb3Column[] columns = columnsBuilder.getColumns();
 		Ejb3JoinColumn[] joinColumns = columnsBuilder.getJoinColumns();
 
 		final XClass returnedClass = inferredData.getClassOrElement();
 
 		//prepare PropertyBinder
 		PropertyBinder propertyBinder = new PropertyBinder();
 		propertyBinder.setName( inferredData.getPropertyName() );
 		propertyBinder.setReturnedClassName( inferredData.getTypeName() );
 		propertyBinder.setAccessType( inferredData.getDefaultAccess() );
 		propertyBinder.setHolder( propertyHolder );
 		propertyBinder.setProperty( property );
 		propertyBinder.setReturnedClass( inferredData.getPropertyClass() );
 		propertyBinder.setMappings( mappings );
 		if ( isIdentifierMapper ) {
 			propertyBinder.setInsertable( false );
 			propertyBinder.setUpdatable( false );
 		}
 		propertyBinder.setDeclaringClass( inferredData.getDeclaringClass() );
 		propertyBinder.setEntityBinder( entityBinder );
 		propertyBinder.setInheritanceStatePerClass( inheritanceStatePerClass );
 
 		boolean isId = !entityBinder.isIgnoreIdAnnotations() &&
 				( property.isAnnotationPresent( Id.class )
 						|| property.isAnnotationPresent( EmbeddedId.class ) );
 		propertyBinder.setId( isId );
 
 		if ( property.isAnnotationPresent( Version.class ) ) {
 			if ( isIdentifierMapper ) {
 				throw new AnnotationException(
 						"@IdClass class should not have @Version property"
 				);
 			}
 			if ( !( propertyHolder.getPersistentClass() instanceof RootClass ) ) {
 				throw new AnnotationException(
 						"Unable to define/override @Version on a subclass: "
 								+ propertyHolder.getEntityName()
 				);
 			}
 			if ( !propertyHolder.isEntity() ) {
 				throw new AnnotationException(
 						"Unable to define @Version on an embedded class: "
 								+ propertyHolder.getEntityName()
 				);
 			}
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev( "{0} is a version property", inferredData.getPropertyName() );
 			}
 			RootClass rootClass = ( RootClass ) propertyHolder.getPersistentClass();
 			propertyBinder.setColumns( columns );
 			Property prop = propertyBinder.makePropertyValueAndBind();
 			setVersionInformation( property, propertyBinder );
 			rootClass.setVersion( prop );
 
 			//If version is on a mapped superclass, update the mapping
 			final org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(
 					inferredData.getDeclaringClass(),
 					inheritanceStatePerClass,
 					mappings
 			);
 			if ( superclass != null ) {
 				superclass.setDeclaredVersion( prop );
 			}
 			else {
 				//we know the property is on the actual entity
 				rootClass.setDeclaredVersion( prop );
 			}
 
 			SimpleValue simpleValue = ( SimpleValue ) prop.getValue();
 			simpleValue.setNullValue( "undefined" );
 			rootClass.setOptimisticLockMode( Versioning.OPTIMISTIC_LOCK_VERSION );
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev( "Version name: {0}, unsavedValue: {1}", rootClass.getVersion().getName(),
 						( (SimpleValue) rootClass.getVersion().getValue() ).getNullValue() );
 			}
 		}
 		else {
 			final boolean forcePersist = property.isAnnotationPresent( MapsId.class )
 					|| property.isAnnotationPresent( Id.class );
 			if ( property.isAnnotationPresent( ManyToOne.class ) ) {
 				ManyToOne ann = property.getAnnotation( ManyToOne.class );
 
 				//check validity
 				if ( property.isAnnotationPresent( Column.class )
 						|| property.isAnnotationPresent( Columns.class ) ) {
 					throw new AnnotationException(
 							"@Column(s) not allowed on a @ManyToOne property: "
 									+ BinderHelper.getPath( propertyHolder, inferredData )
 					);
 				}
 
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				NotFound notFound = property.getAnnotation( NotFound.class );
 				boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				JoinTable assocTable = propertyHolder.getJoinTable( property );
 				if ( assocTable != null ) {
 					Join join = propertyHolder.addJoin( assocTable, false );
 					for ( Ejb3JoinColumn joinColumn : joinColumns ) {
 						joinColumn.setSecondaryTableName( join.getTable().getName() );
 					}
 				}
 				final boolean mandatory = !ann.optional() || forcePersist;
 				bindManyToOne(
 						getCascadeStrategy( ann.cascade(), hibernateCascade, false, forcePersist ),
 						joinColumns,
 						!mandatory,
 						ignoreNotFound, onDeleteCascade,
 						ToOneBinder.getTargetEntity( inferredData, mappings ),
 						propertyHolder,
 						inferredData, false, isIdentifierMapper,
 						inSecondPass, propertyBinder, mappings
 				);
 			}
 			else if ( property.isAnnotationPresent( OneToOne.class ) ) {
 				OneToOne ann = property.getAnnotation( OneToOne.class );
 
 				//check validity
 				if ( property.isAnnotationPresent( Column.class )
 						|| property.isAnnotationPresent( Columns.class ) ) {
 					throw new AnnotationException(
 							"@Column(s) not allowed on a @OneToOne property: "
 									+ BinderHelper.getPath( propertyHolder, inferredData )
 					);
 				}
 
 				//FIXME support a proper PKJCs
 				boolean trueOneToOne = property.isAnnotationPresent( PrimaryKeyJoinColumn.class )
 						|| property.isAnnotationPresent( PrimaryKeyJoinColumns.class );
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				NotFound notFound = property.getAnnotation( NotFound.class );
 				boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				JoinTable assocTable = propertyHolder.getJoinTable( property );
 				if ( assocTable != null ) {
 					Join join = propertyHolder.addJoin( assocTable, false );
 					for ( Ejb3JoinColumn joinColumn : joinColumns ) {
 						joinColumn.setSecondaryTableName( join.getTable().getName() );
 					}
 				}
 				//MapsId means the columns belong to the pk => not null
 				//@OneToOne with @PKJC can still be optional
 				final boolean mandatory = !ann.optional() || forcePersist;
 				bindOneToOne(
 						getCascadeStrategy( ann.cascade(), hibernateCascade, ann.orphanRemoval(), forcePersist ),
 						joinColumns,
 						!mandatory,
 						getFetchMode( ann.fetch() ),
 						ignoreNotFound, onDeleteCascade,
 						ToOneBinder.getTargetEntity( inferredData, mappings ),
 						propertyHolder,
 						inferredData,
 						ann.mappedBy(),
 						trueOneToOne,
 						isIdentifierMapper,
 						inSecondPass,
 						propertyBinder,
 						mappings
 				);
 			}
 			else if ( property.isAnnotationPresent( org.hibernate.annotations.Any.class ) ) {
 
 				//check validity
 				if ( property.isAnnotationPresent( Column.class )
 						|| property.isAnnotationPresent( Columns.class ) ) {
 					throw new AnnotationException(
 							"@Column(s) not allowed on a @Any property: "
 									+ BinderHelper.getPath( propertyHolder, inferredData )
 					);
 				}
 
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				JoinTable assocTable = propertyHolder.getJoinTable( property );
 				if ( assocTable != null ) {
 					Join join = propertyHolder.addJoin( assocTable, false );
 					for ( Ejb3JoinColumn joinColumn : joinColumns ) {
 						joinColumn.setSecondaryTableName( join.getTable().getName() );
 					}
 				}
 				bindAny(
 						getCascadeStrategy( null, hibernateCascade, false, forcePersist ),
 						//@Any has not cascade attribute
 						joinColumns,
 						onDeleteCascade,
 						nullability,
 						propertyHolder,
 						inferredData,
 						entityBinder,
 						isIdentifierMapper,
 						mappings
 				);
 			}
 			else if ( property.isAnnotationPresent( OneToMany.class )
 					|| property.isAnnotationPresent( ManyToMany.class )
 					|| property.isAnnotationPresent( ElementCollection.class )
 					|| property.isAnnotationPresent( ManyToAny.class ) ) {
 				OneToMany oneToManyAnn = property.getAnnotation( OneToMany.class );
 				ManyToMany manyToManyAnn = property.getAnnotation( ManyToMany.class );
 				ElementCollection elementCollectionAnn = property.getAnnotation( ElementCollection.class );
 
 				final IndexColumn indexColumn;
 
 				if ( property.isAnnotationPresent( OrderColumn.class ) ) {
 					indexColumn = IndexColumn.buildColumnFromAnnotation(
 							property.getAnnotation( OrderColumn.class ),
 							propertyHolder,
 							inferredData,
 							entityBinder.getSecondaryTables(),
 							mappings
 					);
 					if ( property.isAnnotationPresent( ListIndexBase.class ) ) {
 						indexColumn.setBase( ( property.getAnnotation( ListIndexBase.class ) ).value() );
 					}
 				}
 				else {
 					//if @IndexColumn is not there, the generated IndexColumn is an implicit column and not used.
 					//so we can leave the legacy processing as the default
 					indexColumn = IndexColumn.buildColumnFromAnnotation(
 							property.getAnnotation( org.hibernate.annotations.IndexColumn.class ),
 							propertyHolder,
 							inferredData,
 							mappings
 					);
 				}
 				CollectionBinder collectionBinder = CollectionBinder.getCollectionBinder(
 						propertyHolder.getEntityName(),
 						property,
 						!indexColumn.isImplicit(),
 						property.isAnnotationPresent( MapKeyType.class ),
 						mappings
 				);
 				collectionBinder.setIndexColumn( indexColumn );
 				collectionBinder.setMapKey( property.getAnnotation( MapKey.class ) );
 				collectionBinder.setPropertyName( inferredData.getPropertyName() );
 
 				collectionBinder.setBatchSize( property.getAnnotation( BatchSize.class ) );
 
 				collectionBinder.setJpaOrderBy( property.getAnnotation( javax.persistence.OrderBy.class ) );
 				collectionBinder.setSqlOrderBy( property.getAnnotation( OrderBy.class ) );
 
 				collectionBinder.setSort( property.getAnnotation( Sort.class ) );
 				collectionBinder.setNaturalSort( property.getAnnotation( SortNatural.class ) );
 				collectionBinder.setComparatorSort( property.getAnnotation( SortComparator.class ) );
 
 				Cache cachAnn = property.getAnnotation( Cache.class );
 				collectionBinder.setCache( cachAnn );
 				collectionBinder.setPropertyHolder( propertyHolder );
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				NotFound notFound = property.getAnnotation( NotFound.class );
 				boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
 				collectionBinder.setIgnoreNotFound( ignoreNotFound );
 				collectionBinder.setCollectionType( inferredData.getProperty().getElementClass() );
 				collectionBinder.setMappings( mappings );
 				collectionBinder.setAccessType( inferredData.getDefaultAccess() );
 
 				Ejb3Column[] elementColumns;
 				//do not use "element" if you are a JPA 2 @ElementCollection only for legacy Hibernate mappings
 				boolean isJPA2ForValueMapping = property.isAnnotationPresent( ElementCollection.class );
 				PropertyData virtualProperty = isJPA2ForValueMapping ? inferredData : new WrappedInferredData(
 						inferredData, "element"
 				);
 				if ( property.isAnnotationPresent( Column.class ) || property.isAnnotationPresent(
 						Formula.class
 				) ) {
 					Column ann = property.getAnnotation( Column.class );
 					Formula formulaAnn = property.getAnnotation( Formula.class );
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							new Column[] { ann },
 							formulaAnn,
 							nullability,
 							propertyHolder,
 							virtualProperty,
 							entityBinder.getSecondaryTables(),
 							mappings
 					);
 				}
 				else if ( property.isAnnotationPresent( Columns.class ) ) {
 					Columns anns = property.getAnnotation( Columns.class );
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							anns.columns(), null, nullability, propertyHolder, virtualProperty,
 							entityBinder.getSecondaryTables(), mappings
 					);
 				}
 				else {
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							null,
 							null,
 							nullability,
 							propertyHolder,
 							virtualProperty,
 							entityBinder.getSecondaryTables(),
 							mappings
 					);
 				}
 				{
 					Column[] keyColumns = null;
 					//JPA 2 has priority and has different default column values, differenciate legacy from JPA 2
 					Boolean isJPA2 = null;
 					if ( property.isAnnotationPresent( MapKeyColumn.class ) ) {
 						isJPA2 = Boolean.TRUE;
 						keyColumns = new Column[] { new MapKeyColumnDelegator( property.getAnnotation( MapKeyColumn.class ) ) };
 					}
 
 					//not explicitly legacy
 					if ( isJPA2 == null ) {
 						isJPA2 = Boolean.TRUE;
 					}
 
 					//nullify empty array
 					keyColumns = keyColumns != null && keyColumns.length > 0 ? keyColumns : null;
 
 					//"mapkey" is the legacy column name of the key column pre JPA 2
 					PropertyData mapKeyVirtualProperty = new WrappedInferredData( inferredData, "mapkey" );
 					Ejb3Column[] mapColumns = Ejb3Column.buildColumnFromAnnotation(
 							keyColumns,
 							null,
 							Nullability.FORCED_NOT_NULL,
 							propertyHolder,
 							isJPA2 ? inferredData : mapKeyVirtualProperty,
 							isJPA2 ? "_KEY" : null,
 							entityBinder.getSecondaryTables(),
 							mappings
 					);
 					collectionBinder.setMapKeyColumns( mapColumns );
 				}
 				{
 					JoinColumn[] joinKeyColumns = null;
 					//JPA 2 has priority and has different default column values, differenciate legacy from JPA 2
 					Boolean isJPA2 = null;
 					if ( property.isAnnotationPresent( MapKeyJoinColumns.class ) ) {
 						isJPA2 = Boolean.TRUE;
 						final MapKeyJoinColumn[] mapKeyJoinColumns = property.getAnnotation( MapKeyJoinColumns.class )
 								.value();
 						joinKeyColumns = new JoinColumn[mapKeyJoinColumns.length];
 						int index = 0;
 						for ( MapKeyJoinColumn joinColumn : mapKeyJoinColumns ) {
 							joinKeyColumns[index] = new MapKeyJoinColumnDelegator( joinColumn );
 							index++;
 						}
 						if ( property.isAnnotationPresent( MapKeyJoinColumn.class ) ) {
 							throw new AnnotationException(
 									"@MapKeyJoinColumn and @MapKeyJoinColumns used on the same property: "
 											+ BinderHelper.getPath( propertyHolder, inferredData )
 							);
 						}
 					}
 					else if ( property.isAnnotationPresent( MapKeyJoinColumn.class ) ) {
 						isJPA2 = Boolean.TRUE;
 						joinKeyColumns = new JoinColumn[] {
 								new MapKeyJoinColumnDelegator(
 										property.getAnnotation(
 												MapKeyJoinColumn.class
 										)
 								)
 						};
 					}
 					//not explicitly legacy
 					if ( isJPA2 == null ) {
 						isJPA2 = Boolean.TRUE;
 					}
 
 					PropertyData mapKeyVirtualProperty = new WrappedInferredData( inferredData, "mapkey" );
 					Ejb3JoinColumn[] mapJoinColumns = Ejb3JoinColumn.buildJoinColumnsWithDefaultColumnSuffix(
 							joinKeyColumns,
 							null,
 							entityBinder.getSecondaryTables(),
 							propertyHolder,
 							isJPA2 ? inferredData.getPropertyName() : mapKeyVirtualProperty.getPropertyName(),
 							isJPA2 ? "_KEY" : null,
 							mappings
 					);
 					collectionBinder.setMapKeyManyToManyColumns( mapJoinColumns );
 				}
 
 				//potential element
 				collectionBinder.setEmbedded( property.isAnnotationPresent( Embedded.class ) );
 				collectionBinder.setElementColumns( elementColumns );
 				collectionBinder.setProperty( property );
 
 				//TODO enhance exception with @ManyToAny and @CollectionOfElements
 				if ( oneToManyAnn != null && manyToManyAnn != null ) {
 					throw new AnnotationException(
 							"@OneToMany and @ManyToMany on the same property is not allowed: "
 									+ propertyHolder.getEntityName() + "." + inferredData.getPropertyName()
 					);
 				}
 				String mappedBy = null;
 				if ( oneToManyAnn != null ) {
 					for ( Ejb3JoinColumn column : joinColumns ) {
 						if ( column.isSecondary() ) {
 							throw new NotYetImplementedException( "Collections having FK in secondary table" );
 						}
 					}
 					collectionBinder.setFkJoinColumns( joinColumns );
 					mappedBy = oneToManyAnn.mappedBy();
 					collectionBinder.setTargetEntity(
 							mappings.getReflectionManager().toXClass( oneToManyAnn.targetEntity() )
 					);
 					collectionBinder.setCascadeStrategy(
 							getCascadeStrategy(
 									oneToManyAnn.cascade(), hibernateCascade, oneToManyAnn.orphanRemoval(), false
 							)
 					);
 					collectionBinder.setOneToMany( true );
 				}
 				else if ( elementCollectionAnn != null ) {
 					for ( Ejb3JoinColumn column : joinColumns ) {
 						if ( column.isSecondary() ) {
 							throw new NotYetImplementedException( "Collections having FK in secondary table" );
 						}
 					}
 					collectionBinder.setFkJoinColumns( joinColumns );
 					mappedBy = "";
 					final Class<?> targetElement = elementCollectionAnn.targetClass();
 					collectionBinder.setTargetEntity(
 							mappings.getReflectionManager().toXClass( targetElement )
 					);
 					//collectionBinder.setCascadeStrategy( getCascadeStrategy( embeddedCollectionAnn.cascade(), hibernateCascade ) );
 					collectionBinder.setOneToMany( true );
 				}
 				else if ( manyToManyAnn != null ) {
 					mappedBy = manyToManyAnn.mappedBy();
 					collectionBinder.setTargetEntity(
 							mappings.getReflectionManager().toXClass( manyToManyAnn.targetEntity() )
 					);
 					collectionBinder.setCascadeStrategy(
 							getCascadeStrategy(
 									manyToManyAnn.cascade(), hibernateCascade, false, false
 							)
 					);
 					collectionBinder.setOneToMany( false );
 				}
 				else if ( property.isAnnotationPresent( ManyToAny.class ) ) {
 					mappedBy = "";
 					collectionBinder.setTargetEntity(
 							mappings.getReflectionManager().toXClass( void.class )
 					);
 					collectionBinder.setCascadeStrategy( getCascadeStrategy( null, hibernateCascade, false, false ) );
 					collectionBinder.setOneToMany( false );
 				}
 				collectionBinder.setMappedBy( mappedBy );
 
 				bindJoinedTableAssociation(
 						property, mappings, entityBinder, collectionBinder, propertyHolder, inferredData, mappedBy
 				);
 
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				collectionBinder.setCascadeDeleteEnabled( onDeleteCascade );
 				if ( isIdentifierMapper ) {
 					collectionBinder.setInsertable( false );
 					collectionBinder.setUpdatable( false );
 				}
 				if ( property.isAnnotationPresent( CollectionId.class ) ) { //do not compute the generators unless necessary
 					HashMap<String, IdGenerator> localGenerators = ( HashMap<String, IdGenerator> ) classGenerators.clone();
 					localGenerators.putAll( buildLocalGenerators( property, mappings ) );
 					collectionBinder.setLocalGenerators( localGenerators );
 
 				}
 				collectionBinder.setInheritanceStatePerClass( inheritanceStatePerClass );
 				collectionBinder.setDeclaringClass( inferredData.getDeclaringClass() );
 				collectionBinder.bind();
 
 			}
 			//Either a regular property or a basic @Id or @EmbeddedId while not ignoring id annotations
 			else if ( !isId || !entityBinder.isIgnoreIdAnnotations() ) {
 				//define whether the type is a component or not
 
 				boolean isComponent = false;
 
 				//Overrides from @MapsId if needed
 				boolean isOverridden = false;
 				if ( isId || propertyHolder.isOrWithinEmbeddedId() || propertyHolder.isInIdClass() ) {
 					//the associated entity could be using an @IdClass making the overridden property a component
 					final PropertyData overridingProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 							isId, propertyHolder, property.getName(), mappings
 					);
 					if ( overridingProperty != null ) {
 						isOverridden = true;
 						final InheritanceState state = inheritanceStatePerClass.get( overridingProperty.getClassOrElement() );
 						if ( state != null ) {
 							isComponent = isComponent || state.hasIdClassOrEmbeddedId();
 						}
 						//Get the new column
 						columns = columnsBuilder.overrideColumnFromMapperOrMapsIdProperty( isId );
 					}
 				}
 
 				isComponent = isComponent
 						|| property.isAnnotationPresent( Embedded.class )
 						|| property.isAnnotationPresent( EmbeddedId.class )
 						|| returnedClass.isAnnotationPresent( Embeddable.class );
 
 
 				if ( isComponent ) {
 					String referencedEntityName = null;
 					if ( isOverridden ) {
 						final PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 								isId, propertyHolder, property.getName(), mappings
 						);
 						referencedEntityName = mapsIdProperty.getClassOrElementName();
 					}
 					AccessType propertyAccessor = entityBinder.getPropertyAccessor( property );
 					propertyBinder = bindComponent(
 							inferredData,
 							propertyHolder,
 							propertyAccessor,
 							entityBinder,
 							isIdentifierMapper,
 							mappings,
 							isComponentEmbedded,
 							isId,
 							inheritanceStatePerClass,
 							referencedEntityName,
 							isOverridden ? ( Ejb3JoinColumn[] ) columns : null
 					);
 				}
 				else {
 					//provide the basic property mapping
 					boolean optional = true;
 					boolean lazy = false;
 					if ( property.isAnnotationPresent( Basic.class ) ) {
 						Basic ann = property.getAnnotation( Basic.class );
 						optional = ann.optional();
 						lazy = ann.fetch() == FetchType.LAZY;
 					}
 					//implicit type will check basic types and Serializable classes
 					if ( isId || ( !optional && nullability != Nullability.FORCED_NULL ) ) {
 						//force columns to not null
 						for ( Ejb3Column col : columns ) {
 							col.forceNotNull();
 						}
 					}
 
 					propertyBinder.setLazy( lazy );
 					propertyBinder.setColumns( columns );
 					if ( isOverridden ) {
 						final PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 								isId, propertyHolder, property.getName(), mappings
 						);
 						propertyBinder.setReferencedEntityName( mapsIdProperty.getClassOrElementName() );
 					}
 
 					propertyBinder.makePropertyValueAndBind();
 
 				}
 				if ( isOverridden ) {
 					final PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 							isId, propertyHolder, property.getName(), mappings
 					);
 					Map<String, IdGenerator> localGenerators = ( HashMap<String, IdGenerator> ) classGenerators.clone();
 					final IdGenerator foreignGenerator = new IdGenerator();
 					foreignGenerator.setIdentifierGeneratorStrategy( "assigned" );
 					foreignGenerator.setName( "Hibernate-local--foreign generator" );
 					foreignGenerator.setIdentifierGeneratorStrategy( "foreign" );
 					foreignGenerator.addParam( "property", mapsIdProperty.getPropertyName() );
 					localGenerators.put( foreignGenerator.getName(), foreignGenerator );
 
 					BinderHelper.makeIdGenerator(
 							( SimpleValue ) propertyBinder.getValue(),
 							foreignGenerator.getIdentifierGeneratorStrategy(),
 							foreignGenerator.getName(),
 							mappings,
 							localGenerators
 					);
 				}
 				if ( isId ) {
 					//components and regular basic types create SimpleValue objects
 					final SimpleValue value = ( SimpleValue ) propertyBinder.getValue();
 					if ( !isOverridden ) {
 						processId(
 								propertyHolder,
 								inferredData,
 								value,
 								classGenerators,
 								isIdentifierMapper,
 								mappings
 						);
 					}
 				}
 			}
 		}
 		//init index
 		//process indexes after everything: in second pass, many to one has to be done before indexes
 		Index index = property.getAnnotation( Index.class );
 		if ( index != null ) {
 			if ( joinColumns != null ) {
 
 				for ( Ejb3Column column : joinColumns ) {
 					column.addIndex( index, inSecondPass );
 				}
 			}
 			else {
 				if ( columns != null ) {
 					for ( Ejb3Column column : columns ) {
 						column.addIndex( index, inSecondPass );
 					}
 				}
 			}
 		}
 
 		NaturalId naturalIdAnn = property.getAnnotation( NaturalId.class );
 		if ( naturalIdAnn != null ) {
 			if ( joinColumns != null ) {
 				for ( Ejb3Column column : joinColumns ) {
 					column.addUniqueKey( column.getTable().getNaturalIdUniqueKeyName(), inSecondPass );
 				}
 			}
 			else {
 				for ( Ejb3Column column : columns ) {
 					column.addUniqueKey( column.getTable().getNaturalIdUniqueKeyName(), inSecondPass );
 				}
 			}
 		}
 	}
 
 	private static void setVersionInformation(XProperty property, PropertyBinder propertyBinder) {
 		propertyBinder.getSimpleValueBinder().setVersion( true );
 		if(property.isAnnotationPresent( Source.class )) {
 			Source source = property.getAnnotation( Source.class );
 			propertyBinder.getSimpleValueBinder().setTimestampVersionType( source.value().typeName() );
 		}
 	}
 
 	private static void processId(
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			SimpleValue idValue,
 			HashMap<String, IdGenerator> classGenerators,
 			boolean isIdentifierMapper,
 			Mappings mappings) {
 		if ( isIdentifierMapper ) {
 			throw new AnnotationException(
 					"@IdClass class should not have @Id nor @EmbeddedId properties: "
 							+ BinderHelper.getPath( propertyHolder, inferredData )
 			);
 		}
 		XClass returnedClass = inferredData.getClassOrElement();
 		XProperty property = inferredData.getProperty();
 		//clone classGenerator and override with local values
 		HashMap<String, IdGenerator> localGenerators = ( HashMap<String, IdGenerator> ) classGenerators.clone();
 		localGenerators.putAll( buildLocalGenerators( property, mappings ) );
 
 		//manage composite related metadata
 		//guess if its a component and find id data access (property, field etc)
 		final boolean isComponent = returnedClass.isAnnotationPresent( Embeddable.class )
 				|| property.isAnnotationPresent( EmbeddedId.class );
 
 		GeneratedValue generatedValue = property.getAnnotation( GeneratedValue.class );
 		String generatorType = generatedValue != null ?
 				generatorType( generatedValue.strategy(), mappings ) :
 				"assigned";
 		String generatorName = generatedValue != null ?
 				generatedValue.generator() :
 				BinderHelper.ANNOTATION_STRING_DEFAULT;
 		if ( isComponent ) {
 			generatorType = "assigned";
 		} //a component must not have any generator
 		BinderHelper.makeIdGenerator( idValue, generatorType, generatorName, mappings, localGenerators );
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Bind {0} on {1}", ( isComponent ? "@EmbeddedId" : "@Id" ), inferredData.getPropertyName() );
 		}
 	}
 
 	//TODO move that to collection binder?
 
 	private static void bindJoinedTableAssociation(
 			XProperty property,
 			Mappings mappings,
 			EntityBinder entityBinder,
 			CollectionBinder collectionBinder,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			String mappedBy) {
 		TableBinder associationTableBinder = new TableBinder();
 		JoinColumn[] annJoins;
 		JoinColumn[] annInverseJoins;
 		JoinTable assocTable = propertyHolder.getJoinTable( property );
 		CollectionTable collectionTable = property.getAnnotation( CollectionTable.class );
 		if ( assocTable != null || collectionTable != null ) {
 
 			final String catalog;
 			final String schema;
 			final String tableName;
 			final UniqueConstraint[] uniqueConstraints;
 			final JoinColumn[] joins;
 			final JoinColumn[] inverseJoins;
 			final javax.persistence.Index[] jpaIndexes;
 
 
 			//JPA 2 has priority
 			if ( collectionTable != null ) {
 				catalog = collectionTable.catalog();
 				schema = collectionTable.schema();
 				tableName = collectionTable.name();
 				uniqueConstraints = collectionTable.uniqueConstraints();
 				joins = collectionTable.joinColumns();
 				inverseJoins = null;
 				jpaIndexes = collectionTable.indexes();
 			}
 			else {
 				catalog = assocTable.catalog();
 				schema = assocTable.schema();
 				tableName = assocTable.name();
 				uniqueConstraints = assocTable.uniqueConstraints();
 				joins = assocTable.joinColumns();
 				inverseJoins = assocTable.inverseJoinColumns();
 				jpaIndexes = assocTable.indexes();
 			}
 
 			collectionBinder.setExplicitAssociationTable( true );
 			if ( jpaIndexes != null && jpaIndexes.length > 0 ) {
 				associationTableBinder.setJpaIndex( jpaIndexes );
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( schema ) ) {
 				associationTableBinder.setSchema( schema );
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( catalog ) ) {
 				associationTableBinder.setCatalog( catalog );
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( tableName ) ) {
 				associationTableBinder.setName( tableName );
 			}
 			associationTableBinder.setUniqueConstraints( uniqueConstraints );
 			associationTableBinder.setJpaIndex( jpaIndexes );
 			//set check constaint in the second pass
 			annJoins = joins.length == 0 ? null : joins;
 			annInverseJoins = inverseJoins == null || inverseJoins.length == 0 ? null : inverseJoins;
 		}
 		else {
 			annJoins = null;
 			annInverseJoins = null;
 		}
 		Ejb3JoinColumn[] joinColumns = Ejb3JoinColumn.buildJoinTableJoinColumns(
 				annJoins, entityBinder.getSecondaryTables(), propertyHolder, inferredData.getPropertyName(), mappedBy,
 				mappings
 		);
 		Ejb3JoinColumn[] inverseJoinColumns = Ejb3JoinColumn.buildJoinTableJoinColumns(
 				annInverseJoins, entityBinder.getSecondaryTables(), propertyHolder, inferredData.getPropertyName(),
 				mappedBy, mappings
 		);
 		associationTableBinder.setMappings( mappings );
 		collectionBinder.setTableBinder( associationTableBinder );
 		collectionBinder.setJoinColumns( joinColumns );
 		collectionBinder.setInverseJoinColumns( inverseJoinColumns );
 	}
 
 	private static PropertyBinder bindComponent(
 			PropertyData inferredData,
 			PropertyHolder propertyHolder,
 			AccessType propertyAccessor,
 			EntityBinder entityBinder,
 			boolean isIdentifierMapper,
 			Mappings mappings,
 			boolean isComponentEmbedded,
 			boolean isId, //is a identifier
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			String referencedEntityName, //is a component who is overridden by a @MapsId
 			Ejb3JoinColumn[] columns) {
 		Component comp;
 		if ( referencedEntityName != null ) {
 			comp = createComponent( propertyHolder, inferredData, isComponentEmbedded, isIdentifierMapper, mappings );
 			SecondPass sp = new CopyIdentifierComponentSecondPass(
 					comp,
 					referencedEntityName,
 					columns,
 					mappings
 			);
 			mappings.addSecondPass( sp );
 		}
 		else {
 			comp = fillComponent(
 					propertyHolder, inferredData, propertyAccessor, !isId, entityBinder,
 					isComponentEmbedded, isIdentifierMapper,
 					false, mappings, inheritanceStatePerClass
 			);
 		}
 		if ( isId ) {
 			comp.setKey( true );
 			if ( propertyHolder.getPersistentClass().getIdentifier() != null ) {
 				throw new AnnotationException(
 						comp.getComponentClassName()
 								+ " must not have @Id properties when used as an @EmbeddedId: "
 								+ BinderHelper.getPath( propertyHolder, inferredData )
 				);
 			}
 			if ( referencedEntityName == null && comp.getPropertySpan() == 0 ) {
 				throw new AnnotationException(
 						comp.getComponentClassName()
 								+ " has no persistent id property: "
 								+ BinderHelper.getPath( propertyHolder, inferredData )
 				);
 			}
 		}
 		XProperty property = inferredData.getProperty();
 		setupComponentTuplizer( property, comp );
 		PropertyBinder binder = new PropertyBinder();
 		binder.setName( inferredData.getPropertyName() );
 		binder.setValue( comp );
 		binder.setProperty( inferredData.getProperty() );
 		binder.setAccessType( inferredData.getDefaultAccess() );
 		binder.setEmbedded( isComponentEmbedded );
 		binder.setHolder( propertyHolder );
 		binder.setId( isId );
 		binder.setEntityBinder( entityBinder );
 		binder.setInheritanceStatePerClass( inheritanceStatePerClass );
 		binder.setMappings( mappings );
 		binder.makePropertyAndBind();
 		return binder;
 	}
 
 	public static Component fillComponent(
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			AccessType propertyAccessor,
 			boolean isNullable,
 			EntityBinder entityBinder,
 			boolean isComponentEmbedded,
 			boolean isIdentifierMapper,
 			boolean inSecondPass,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		return fillComponent(
 				propertyHolder, inferredData, null, propertyAccessor,
 				isNullable, entityBinder, isComponentEmbedded, isIdentifierMapper, inSecondPass, mappings,
 				inheritanceStatePerClass
 		);
 	}
 
 	public static Component fillComponent(
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			PropertyData baseInferredData, //base inferred data correspond to the entity reproducing inferredData's properties (ie IdClass)
 			AccessType propertyAccessor,
 			boolean isNullable,
 			EntityBinder entityBinder,
 			boolean isComponentEmbedded,
 			boolean isIdentifierMapper,
 			boolean inSecondPass,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		/**
 		 * inSecondPass can only be used to apply right away the second pass of a composite-element
 		 * Because it's a value type, there is no bidirectional association, hence second pass
 		 * ordering does not matter
 		 */
 		Component comp = createComponent( propertyHolder, inferredData, isComponentEmbedded, isIdentifierMapper, mappings );
 		String subpath = BinderHelper.getPath( propertyHolder, inferredData );
 		LOG.tracev( "Binding component with path: {0}", subpath );
 		PropertyHolder subHolder = PropertyHolderBuilder.buildPropertyHolder(
 				comp, subpath,
 				inferredData, propertyHolder, mappings
 		);
 
 		final XClass xClassProcessed = inferredData.getPropertyClass();
 		List<PropertyData> classElements = new ArrayList<PropertyData>();
 		XClass returnedClassOrElement = inferredData.getClassOrElement();
 
 		List<PropertyData> baseClassElements = null;
 		Map<String, PropertyData> orderedBaseClassElements = new HashMap<String, PropertyData>();
 		XClass baseReturnedClassOrElement;
 		if ( baseInferredData != null ) {
 			baseClassElements = new ArrayList<PropertyData>();
 			baseReturnedClassOrElement = baseInferredData.getClassOrElement();
 			bindTypeDefs( baseReturnedClassOrElement, mappings );
 			PropertyContainer propContainer = new PropertyContainer( baseReturnedClassOrElement, xClassProcessed );
 			addElementsOfClass( baseClassElements, propertyAccessor, propContainer, mappings );
 			for ( PropertyData element : baseClassElements ) {
 				orderedBaseClassElements.put( element.getPropertyName(), element );
 			}
 		}
 
 		//embeddable elements can have type defs
 		bindTypeDefs( returnedClassOrElement, mappings );
 		PropertyContainer propContainer = new PropertyContainer( returnedClassOrElement, xClassProcessed );
 		addElementsOfClass( classElements, propertyAccessor, propContainer, mappings );
 
 		//add elements of the embeddable superclass
 		XClass superClass = xClassProcessed.getSuperclass();
 		while ( superClass != null && superClass.isAnnotationPresent( MappedSuperclass.class ) ) {
 			//FIXME: proper support of typevariables incl var resolved at upper levels
 			propContainer = new PropertyContainer( superClass, xClassProcessed );
 			addElementsOfClass( classElements, propertyAccessor, propContainer, mappings );
 			superClass = superClass.getSuperclass();
 		}
 		if ( baseClassElements != null ) {
 			//useful to avoid breaking pre JPA 2 mappings
 			if ( !hasAnnotationsOnIdClass( xClassProcessed ) ) {
 				for ( int i = 0; i < classElements.size(); i++ ) {
 					final PropertyData idClassPropertyData = classElements.get( i );
 					final PropertyData entityPropertyData = orderedBaseClassElements.get( idClassPropertyData.getPropertyName() );
 					if ( propertyHolder.isInIdClass() ) {
 						if ( entityPropertyData == null ) {
 							throw new AnnotationException(
 									"Property of @IdClass not found in entity "
 											+ baseInferredData.getPropertyClass().getName() + ": "
 											+ idClassPropertyData.getPropertyName()
 							);
 						}
 						final boolean hasXToOneAnnotation = entityPropertyData.getProperty()
 								.isAnnotationPresent( ManyToOne.class )
 								|| entityPropertyData.getProperty().isAnnotationPresent( OneToOne.class );
 						final boolean isOfDifferentType = !entityPropertyData.getClassOrElement()
 								.equals( idClassPropertyData.getClassOrElement() );
 						if ( hasXToOneAnnotation && isOfDifferentType ) {
 							//don't replace here as we need to use the actual original return type
 							//the annotation overriding will be dealt with by a mechanism similar to @MapsId
 						}
 						else {
 							classElements.set( i, entityPropertyData );  //this works since they are in the same order
 						}
 					}
 					else {
 						classElements.set( i, entityPropertyData );  //this works since they are in the same order
 					}
 				}
 			}
 		}
 		for ( PropertyData propertyAnnotatedElement : classElements ) {
 			processElementAnnotations(
 					subHolder, isNullable ?
 							Nullability.NO_CONSTRAINT :
 							Nullability.FORCED_NOT_NULL,
 					propertyAnnotatedElement,
 					new HashMap<String, IdGenerator>(), entityBinder, isIdentifierMapper, isComponentEmbedded,
 					inSecondPass, mappings, inheritanceStatePerClass
 			);
 
 			XProperty property = propertyAnnotatedElement.getProperty();
 			if ( property.isAnnotationPresent( GeneratedValue.class ) &&
 					property.isAnnotationPresent( Id.class ) ) {
 				//clone classGenerator and override with local values
 				Map<String, IdGenerator> localGenerators = new HashMap<String, IdGenerator>();
 				localGenerators.putAll( buildLocalGenerators( property, mappings ) );
 
 				GeneratedValue generatedValue = property.getAnnotation( GeneratedValue.class );
 				String generatorType = generatedValue != null ? generatorType(
 						generatedValue.strategy(), mappings
 				) : "assigned";
 				String generator = generatedValue != null ? generatedValue.generator() : BinderHelper.ANNOTATION_STRING_DEFAULT;
 
 				BinderHelper.makeIdGenerator(
 						( SimpleValue ) comp.getProperty( property.getName() ).getValue(),
 						generatorType,
 						generator,
 						mappings,
 						localGenerators
 				);
 			}
 
 		}
 		return comp;
 	}
 
 	public static Component createComponent(
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			boolean isComponentEmbedded,
 			boolean isIdentifierMapper,
 			Mappings mappings) {
 		Component comp = new Component( mappings, propertyHolder.getPersistentClass() );
 		comp.setEmbedded( isComponentEmbedded );
 		//yuk
 		comp.setTable( propertyHolder.getTable() );
 		//FIXME shouldn't identifier mapper use getClassOrElementName? Need to be checked.
 		if ( isIdentifierMapper || ( isComponentEmbedded && inferredData.getPropertyName() == null ) ) {
 			comp.setComponentClassName( comp.getOwner().getClassName() );
 		}
 		else {
 			comp.setComponentClassName( inferredData.getClassOrElementName() );
 		}
 		comp.setNodeName( inferredData.getPropertyName() );
 		return comp;
 	}
 
 	private static void bindIdClass(
 			String generatorType,
 			String generatorName,
 			PropertyData inferredData,
 			PropertyData baseInferredData,
 			Ejb3Column[] columns,
 			PropertyHolder propertyHolder,
 			boolean isComposite,
 			AccessType propertyAccessor,
 			EntityBinder entityBinder,
 			boolean isEmbedded,
 			boolean isIdentifierMapper,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 
 		/*
 		 * Fill simple value and property since and Id is a property
 		 */
 		PersistentClass persistentClass = propertyHolder.getPersistentClass();
 		if ( !( persistentClass instanceof RootClass ) ) {
 			throw new AnnotationException(
 					"Unable to define/override @Id(s) on a subclass: "
 							+ propertyHolder.getEntityName()
 			);
 		}
 		RootClass rootClass = ( RootClass ) persistentClass;
 		String persistentClassName = rootClass.getClassName();
 		SimpleValue id;
 		final String propertyName = inferredData.getPropertyName();
 		HashMap<String, IdGenerator> localGenerators = new HashMap<String, IdGenerator>();
 		if ( isComposite ) {
 			id = fillComponent(
 					propertyHolder, inferredData, baseInferredData, propertyAccessor,
 					false, entityBinder, isEmbedded, isIdentifierMapper, false, mappings, inheritanceStatePerClass
 			);
 			Component componentId = ( Component ) id;
 			componentId.setKey( true );
 			if ( rootClass.getIdentifier() != null ) {
 				throw new AnnotationException( componentId.getComponentClassName() + " must not have @Id properties when used as an @EmbeddedId" );
 			}
 			if ( componentId.getPropertySpan() == 0 ) {
 				throw new AnnotationException( componentId.getComponentClassName() + " has no persistent id property" );
 			}
 			//tuplizers
 			XProperty property = inferredData.getProperty();
 			setupComponentTuplizer( property, componentId );
 		}
 		else {
 			//TODO I think this branch is never used. Remove.
 
 			for ( Ejb3Column column : columns ) {
 				column.forceNotNull(); //this is an id
 			}
 			SimpleValueBinder value = new SimpleValueBinder();
 			value.setPropertyName( propertyName );
 			value.setReturnedClassName( inferredData.getTypeName() );
 			value.setColumns( columns );
 			value.setPersistentClassName( persistentClassName );
 			value.setMappings( mappings );
 			value.setType( inferredData.getProperty(), inferredData.getClassOrElement(), persistentClassName );
 			value.setAccessType( propertyAccessor );
 			id = value.make();
 		}
 		rootClass.setIdentifier( id );
 		BinderHelper.makeIdGenerator( id, generatorType, generatorName, mappings, localGenerators );
 		if ( isEmbedded ) {
 			rootClass.setEmbeddedIdentifier( inferredData.getPropertyClass() == null );
 		}
 		else {
 			PropertyBinder binder = new PropertyBinder();
 			binder.setName( propertyName );
 			binder.setValue( id );
 			binder.setAccessType( inferredData.getDefaultAccess() );
 			binder.setProperty( inferredData.getProperty() );
 			Property prop = binder.makeProperty();
 			rootClass.setIdentifierProperty( prop );
 			//if the id property is on a superclass, update the metamodel
 			final org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(
 					inferredData.getDeclaringClass(),
 					inheritanceStatePerClass,
 					mappings
 			);
 			if ( superclass != null ) {
 				superclass.setDeclaredIdentifierProperty( prop );
 			}
 			else {
 				//we know the property is on the actual entity
 				rootClass.setDeclaredIdentifierProperty( prop );
 			}
 		}
 	}
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/CollectionSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/CollectionSecondPass.java
index 89a6a676ee..d7a3efac6b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/CollectionSecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/CollectionSecondPass.java
@@ -1,96 +1,99 @@
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
 package org.hibernate.cfg;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.MappingException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.OneToMany;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Value;
 
 /**
  * Collection second pass
  *
  * @author Emmanuel Bernard
  */
 public abstract class CollectionSecondPass implements SecondPass {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionSecondPass.class.getName());
 
 	Mappings mappings;
 	Collection collection;
 	private Map localInheritedMetas;
 
 	public CollectionSecondPass(Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 		this.collection = collection;
 		this.mappings = mappings;
 		this.localInheritedMetas = inheritedMetas;
 	}
 
 	public CollectionSecondPass(Mappings mappings, Collection collection) {
 		this(mappings, collection, Collections.EMPTY_MAP);
 	}
 
 	public void doSecondPass(java.util.Map persistentClasses)
 			throws MappingException {
-		LOG.debugf( "Second pass for collection: %s", collection.getRole() );
+		final boolean debugEnabled = LOG.isDebugEnabled();
+		if ( debugEnabled ) {
+			LOG.debugf( "Second pass for collection: %s", collection.getRole() );
+		}
 
 		secondPass( persistentClasses, localInheritedMetas ); // using local since the inheritedMetas at this point is not the correct map since it is always the empty map
 		collection.createAllKeys();
 
-		if ( LOG.isDebugEnabled() ) {
+		if ( debugEnabled ) {
 			String msg = "Mapped collection key: " + columns( collection.getKey() );
 			if ( collection.isIndexed() )
 				msg += ", index: " + columns( ( (IndexedCollection) collection ).getIndex() );
 			if ( collection.isOneToMany() ) {
 				msg += ", one-to-many: "
 					+ ( (OneToMany) collection.getElement() ).getReferencedEntityName();
 			}
 			else {
 				msg += ", element: " + columns( collection.getElement() );
 			}
 			LOG.debug( msg );
 		}
 	}
 
 	abstract public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 			throws MappingException;
 
 	private static String columns(Value val) {
 		StringBuilder columns = new StringBuilder();
 		Iterator iter = val.getColumnIterator();
 		while ( iter.hasNext() ) {
 			columns.append( ( (Selectable) iter.next() ).getText() );
 			if ( iter.hasNext() ) columns.append( ", " );
 		}
 		return columns.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 472fbcfd34..8422356dd3 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -1660,1560 +1660,1561 @@ public final class HbmBinder {
 						"many-to-one attribute [" + path + "] does not support orphan delete as it is not unique"
 				);
 			}
 		}
 	}
 
 	public static void bindAny(Element node, Any any, boolean isNullable, Mappings mappings)
 			throws MappingException {
 		any.setIdentifierType( getTypeFromXML( node ) );
 		Attribute metaAttribute = node.attribute( "meta-type" );
 		if ( metaAttribute != null ) {
 			any.setMetaType( metaAttribute.getValue() );
 
 			Iterator iter = node.elementIterator( "meta-value" );
 			if ( iter.hasNext() ) {
 				HashMap values = new HashMap();
 				org.hibernate.type.Type metaType = mappings.getTypeResolver().heuristicType( any.getMetaType() );
 				while ( iter.hasNext() ) {
 					Element metaValue = (Element) iter.next();
 					try {
 						Object value = ( (DiscriminatorType) metaType ).stringToObject( metaValue
 							.attributeValue( "value" ) );
 						String entityName = getClassName( metaValue.attribute( "class" ), mappings );
 						values.put( value, entityName );
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException( "meta-type was not a DiscriminatorType: "
 							+ metaType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException( "could not interpret meta-value", e );
 					}
 				}
 				any.setMetaValues( values );
 			}
 
 		}
 
 		bindColumns( node, any, isNullable, false, null, mappings );
 	}
 
 	public static void bindOneToOne(Element node, OneToOne oneToOne, String path, boolean isNullable,
 			Mappings mappings) throws MappingException {
 
 		bindColumns( node, oneToOne, isNullable, false, null, mappings );
 
 		Attribute constrNode = node.attribute( "constrained" );
 		boolean constrained = constrNode != null && constrNode.getValue().equals( "true" );
 		oneToOne.setConstrained( constrained );
 
 		oneToOne.setForeignKeyType( constrained ?
 				ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT :
 				ForeignKeyDirection.FOREIGN_KEY_TO_PARENT );
 
 		initOuterJoinFetchSetting( node, oneToOne );
 		initLaziness( node, oneToOne, mappings, true );
 
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
 		oneToOne.setEmbedded( "true".equals( embed ) );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) oneToOne.setForeignKeyName( fkNode.getValue() );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) oneToOne.setReferencedPropertyName( ukName.getValue() );
 
 		oneToOne.setPropertyName( node.attributeValue( "name" ) );
 
 		oneToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String cascade = node.attributeValue( "cascade" );
 		if ( cascade != null && cascade.indexOf( "delete-orphan" ) >= 0 ) {
 			if ( oneToOne.isConstrained() ) {
 				throw new MappingException(
 						"one-to-one attribute [" + path + "] does not support orphan delete as it is constrained"
 				);
 			}
 		}
 	}
 
 	public static void bindOneToMany(Element node, OneToMany oneToMany, Mappings mappings)
 			throws MappingException {
 
 		oneToMany.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
 		oneToMany.setEmbedded( embed == null || "true".equals( embed ) );
 
 		String notFound = node.attributeValue( "not-found" );
 		oneToMany.setIgnoreNotFound( "ignore".equals( notFound ) );
 
 	}
 
 	public static void bindColumn(Element node, Column column, boolean isNullable) throws MappingException {
 		Attribute lengthNode = node.attribute( "length" );
 		if ( lengthNode != null ) column.setLength( Integer.parseInt( lengthNode.getValue() ) );
 		Attribute scalNode = node.attribute( "scale" );
 		if ( scalNode != null ) column.setScale( Integer.parseInt( scalNode.getValue() ) );
 		Attribute precNode = node.attribute( "precision" );
 		if ( precNode != null ) column.setPrecision( Integer.parseInt( precNode.getValue() ) );
 
 		Attribute nullNode = node.attribute( "not-null" );
 		column.setNullable( nullNode == null ? isNullable : nullNode.getValue().equals( "false" ) );
 
 		Attribute unqNode = node.attribute( "unique" );
 		if ( unqNode != null ) column.setUnique( unqNode.getValue().equals( "true" ) );
 
 		column.setCheckConstraint( node.attributeValue( "check" ) );
 		column.setDefaultValue( node.attributeValue( "default" ) );
 
 		Attribute typeNode = node.attribute( "sql-type" );
 		if ( typeNode != null ) column.setSqlType( typeNode.getValue() );
 
 		String customWrite = node.attributeValue( "write" );
 		if(customWrite != null && !customWrite.matches("[^?]*\\?[^?]*")) {
 			throw new MappingException("write expression must contain exactly one value placeholder ('?') character");
 		}
 		column.setCustomWrite( customWrite );
 		column.setCustomRead( node.attributeValue( "read" ) );
 
 		Element comment = node.element("comment");
 		if (comment!=null) column.setComment( comment.getTextTrim() );
 
 	}
 
 	/**
 	 * Called for arrays and primitive arrays
 	 */
 	public static void bindArray(Element node, Array array, String prefix, String path,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollection( node, array, prefix, path, mappings, inheritedMetas );
 
 		Attribute att = node.attribute( "element-class" );
 		if ( att != null ) array.setElementClassName( getClassName( att, mappings ) );
 
 	}
 
 	private static Class reflectedPropertyClass(String className, String propertyName)
 			throws MappingException {
 		if ( className == null ) return null;
 		return ReflectHelper.reflectedPropertyClass( className, propertyName );
 	}
 
 	public static void bindComposite(Element node, Component component, String path,
 			boolean isNullable, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 		bindComponent(
 				node,
 				component,
 				null,
 				null,
 				path,
 				isNullable,
 				false,
 				mappings,
 				inheritedMetas,
 				false
 		);
 	}
 
 	public static void bindCompositeId(Element node, Component component,
 			PersistentClass persistentClass, String propertyName, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		component.setKey( true );
 
 		String path = StringHelper.qualify(
 				persistentClass.getEntityName(),
 				propertyName == null ? "id" : propertyName );
 
 		bindComponent(
 				node,
 				component,
 				persistentClass.getClassName(),
 				propertyName,
 				path,
 				false,
 				node.attribute( "class" ) == null
 						&& propertyName == null,
 				mappings,
 				inheritedMetas,
 				false
 			);
 
 		if ( "true".equals( node.attributeValue("mapped") ) ) {
 			if ( propertyName!=null ) {
 				throw new MappingException("cannot combine mapped=\"true\" with specified name");
 			}
 			Component mapper = new Component( mappings, persistentClass );
 			bindComponent(
 					node,
 					mapper,
 					persistentClass.getClassName(),
 					null,
 					path,
 					false,
 					true,
 					mappings,
 					inheritedMetas,
 					true
 				);
 			persistentClass.setIdentifierMapper(mapper);
 			Property property = new Property();
 			property.setName("_identifierMapper");
 			property.setNodeName("id");
 			property.setUpdateable(false);
 			property.setInsertable(false);
 			property.setValue(mapper);
 			property.setPropertyAccessorName( "embedded" );
 			persistentClass.addProperty(property);
 		}
 
 	}
 
 	public static void bindComponent(
 			Element node,
 			Component component,
 			String ownerClassName,
 			String parentProperty,
 			String path,
 			boolean isNullable,
 			boolean isEmbedded,
 			Mappings mappings,
 			java.util.Map inheritedMetas,
 			boolean isIdentifierMapper) throws MappingException {
 
 		component.setEmbedded( isEmbedded );
 		component.setRoleName( path );
 
 		inheritedMetas = getMetas( node, inheritedMetas );
 		component.setMetaAttributes( inheritedMetas );
 
 		Attribute classNode = isIdentifierMapper ? null : node.attribute( "class" );
 		if ( classNode != null ) {
 			component.setComponentClassName( getClassName( classNode, mappings ) );
 		}
 		else if ( "dynamic-component".equals( node.getName() ) ) {
 			component.setDynamic( true );
 		}
 		else if ( isEmbedded ) {
 			// an "embedded" component (composite ids and unique)
 			// note that this does not handle nested components
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				component.setComponentClassName( component.getOwner().getClassName() );
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 		else {
 			// todo : again, how *should* this work for non-pojo entities?
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				Class reflectedClass = reflectedPropertyClass( ownerClassName, parentProperty );
 				if ( reflectedClass != null ) {
 					component.setComponentClassName( reflectedClass.getName() );
 				}
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		if ( nodeName == null ) nodeName = component.getOwner().getNodeName();
 		component.setNodeName( nodeName );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = getPropertyName( subnode );
 			String subpath = propertyName == null ? null : StringHelper
 				.qualify( path, propertyName );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						subpath,
 						component.getOwner(),
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) || "key-many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindManyToOne( subnode, (ManyToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
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
+		final boolean debugEnabled = LOG.isDebugEnabled();
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
-			if ( LOG.isDebugEnabled() ) {
+			if ( debugEnabled ) {
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
 
 	private static int getOptimisticLockMode(Attribute olAtt) throws MappingException {
 
 		if ( olAtt == null ) return Versioning.OPTIMISTIC_LOCK_VERSION;
 		String olMode = olAtt.getValue();
 		if ( olMode == null || "version".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_VERSION;
 		}
 		else if ( "dirty".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_DIRTY;
 		}
 		else if ( "all".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_ALL;
 		}
 		else if ( "none".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_NONE;
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
 			if ( onlyInheritable & !inheritable ) {
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
index 8d5d5be9f2..03cf238d6b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
@@ -1,1525 +1,1527 @@
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
 package org.hibernate.cfg.annotations;
 
 import static org.hibernate.cfg.BinderHelper.toAliasEntityMap;
 import static org.hibernate.cfg.BinderHelper.toAliasTableMap;
 
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.ElementCollection;
 import javax.persistence.Embeddable;
 import javax.persistence.FetchType;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinColumns;
 import javax.persistence.JoinTable;
 import javax.persistence.ManyToMany;
 import javax.persistence.MapKey;
 import javax.persistence.MapKeyColumn;
 import javax.persistence.OneToMany;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.BatchSize;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CollectionId;
 import org.hibernate.annotations.CollectionType;
 import org.hibernate.annotations.Fetch;
 import org.hibernate.annotations.Filter;
 import org.hibernate.annotations.FilterJoinTable;
 import org.hibernate.annotations.FilterJoinTables;
 import org.hibernate.annotations.Filters;
 import org.hibernate.annotations.ForeignKey;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.LazyCollection;
 import org.hibernate.annotations.LazyCollectionOption;
 import org.hibernate.annotations.Loader;
 import org.hibernate.annotations.ManyToAny;
 import org.hibernate.annotations.OptimisticLock;
 import org.hibernate.annotations.OrderBy;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Persister;
 import org.hibernate.annotations.SQLDelete;
 import org.hibernate.annotations.SQLDeleteAll;
 import org.hibernate.annotations.SQLInsert;
 import org.hibernate.annotations.SQLUpdate;
 import org.hibernate.annotations.Sort;
 import org.hibernate.annotations.SortComparator;
 import org.hibernate.annotations.SortNatural;
 import org.hibernate.annotations.SortType;
 import org.hibernate.annotations.Where;
 import org.hibernate.annotations.WhereJoinTable;
 import org.hibernate.annotations.common.AssertionFailure;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.CollectionSecondPass;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.IndexColumn;
 import org.hibernate.cfg.InheritanceState;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.PropertyHolderBuilder;
 import org.hibernate.cfg.PropertyInferredData;
 import org.hibernate.cfg.PropertyPreloadedData;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Backref;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
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
-		if ( LOG.isDebugEnabled() ) {
+
+		final boolean debugEnabled = LOG.isDebugEnabled();
+		if ( debugEnabled ) {
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
-		if ( LOG.isDebugEnabled() ) {
+		if ( debugEnabled ) {
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
 		PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( collType.getName() );
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
 			ForeignKey fk = property != null ? property.getAnnotation( ForeignKey.class ) : null;
 			String fkName = fk != null ? fk.inverseName() : "";
 			if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) element.setForeignKeyName( fkName );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/Cascade.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/Cascade.java
index 1b9b2e29bf..e4776059fe 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/Cascade.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Cascade.java
@@ -1,478 +1,481 @@
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
 package org.hibernate.engine.internal;
 
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Stack;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Delegate responsible for, in conjunction with the various
  * {@link org.hibernate.engine.spi.CascadingAction actions}, implementing cascade processing.
  *
  * @author Gavin King
  * @see org.hibernate.engine.spi.CascadingAction
  */
 public final class Cascade {
 
 	/**
 	 * A cascade point that occurs just after the insertion of the parent entity and
 	 * just before deletion
 	 */
 	public static final int AFTER_INSERT_BEFORE_DELETE = 1;
 	/**
 	 * A cascade point that occurs just before the insertion of the parent entity and
 	 * just after deletion
 	 */
 	public static final int BEFORE_INSERT_AFTER_DELETE = 2;
 	/**
 	 * A cascade point that occurs just after the insertion of the parent entity and
 	 * just before deletion, inside a collection
 	 */
 	public static final int AFTER_INSERT_BEFORE_DELETE_VIA_COLLECTION = 3;
 	/**
 	 * A cascade point that occurs just after update of the parent entity
 	 */
 	public static final int AFTER_UPDATE = 0;
 	/**
 	 * A cascade point that occurs just before the session is flushed
 	 */
 	public static final int BEFORE_FLUSH = 0;
 	/**
 	 * A cascade point that occurs just after eviction of the parent entity from the
 	 * session cache
 	 */
 	public static final int AFTER_EVICT = 0;
 	/**
 	 * A cascade point that occurs just after locking a transient parent entity into the
 	 * session cache
 	 */
 	public static final int BEFORE_REFRESH = 0;
 	/**
 	 * A cascade point that occurs just after refreshing a parent entity
 	 */
 	public static final int AFTER_LOCK = 0;
 	/**
 	 * A cascade point that occurs just before merging from a transient parent entity into
 	 * the object in the session cache
 	 */
 	public static final int BEFORE_MERGE = 0;
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Cascade.class.getName());
 
 
 	private int cascadeTo;
 	private EventSource eventSource;
 	private CascadingAction action;
 
 	public Cascade(final CascadingAction action, final int cascadeTo, final EventSource eventSource) {
 		this.cascadeTo = cascadeTo;
 		this.eventSource = eventSource;
 		this.action = action;
 	}
 
 	private SessionFactoryImplementor getFactory() {
 		return eventSource.getFactory();
 	}
 
 	/**
 	 * Cascade an action from the parent entity instance to all its children.
 	 *
 	 * @param persister The parent's entity persister
 	 * @param parent The parent reference.
 	 * @throws HibernateException
 	 */
 	public void cascade(final EntityPersister persister, final Object parent)
 	throws HibernateException {
 		cascade( persister, parent, null );
 	}
 
 	/**
 	 * Cascade an action from the parent entity instance to all its children.  This
 	 * form is typically called from within cascade actions.
 	 *
 	 * @param persister The parent's entity persister
 	 * @param parent The parent reference.
 	 * @param anything Anything ;)   Typically some form of cascade-local cache
 	 * which is specific to each CascadingAction type
 	 * @throws HibernateException
 	 */
 	public void cascade(final EntityPersister persister, final Object parent, final Object anything)
 			throws HibernateException {
 
 		if ( persister.hasCascades() || action.requiresNoCascadeChecking() ) { // performance opt
-			if ( LOG.isTraceEnabled() ) {
+			final boolean traceEnabled = LOG.isTraceEnabled();
+			if ( traceEnabled ) {
 				LOG.tracev( "Processing cascade {0} for: {1}", action, persister.getEntityName() );
 			}
 
 			Type[] types = persister.getPropertyTypes();
 			CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();
 			boolean hasUninitializedLazyProperties = persister.hasUninitializedLazyProperties( parent );
 			for ( int i=0; i<types.length; i++) {
 				final CascadeStyle style = cascadeStyles[i];
 				final String propertyName = persister.getPropertyNames()[i];
 				if ( hasUninitializedLazyProperties && persister.getPropertyLaziness()[i] && ! action.performOnLazyProperty() ) {
 					//do nothing to avoid a lazy property initialization
 					continue;
 				}
 
 				if ( style.doCascade( action ) ) {
 					cascadeProperty(
 						    parent,
 					        persister.getPropertyValue( parent, i ),
 					        types[i],
 					        style,
 							propertyName,
 					        anything,
 					        false
 					);
 				}
 				else if ( action.requiresNoCascadeChecking() ) {
 					action.noCascade(
 							eventSource,
 							persister.getPropertyValue( parent, i ),
 							parent,
 							persister,
 							i
 					);
 				}
 			}
 
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev( "Done processing cascade {0} for: {1}", action, persister.getEntityName() );
 			}
 		}
 	}
 
 	/**
 	 * Cascade an action to the child or children
 	 */
 	private void cascadeProperty(
 			final Object parent,
 			final Object child,
 			final Type type,
 			final CascadeStyle style,
 			final String propertyName,
 			final Object anything,
 			final boolean isCascadeDeleteEnabled) throws HibernateException {
 
 		if (child!=null) {
 			if ( type.isAssociationType() ) {
 				AssociationType associationType = (AssociationType) type;
 				if ( cascadeAssociationNow( associationType ) ) {
 					cascadeAssociation(
 							parent,
 							child,
 							type,
 							style,
 							anything,
 							isCascadeDeleteEnabled
 						);
 				}
 			}
 			else if ( type.isComponentType() ) {
 				cascadeComponent( parent, child, (CompositeType) type, propertyName, anything );
 			}
 		}
 		else {
 			// potentially we need to handle orphan deletes for one-to-ones here...
 			if ( isLogicalOneToOne( type ) ) {
 				// We have a physical or logical one-to-one and from previous checks we know we
 				// have a null value.  See if the attribute cascade settings and action-type require
 				// orphan checking
 				if ( style.hasOrphanDelete() && action.deleteOrphans() ) {
 					// value is orphaned if loaded state for this property shows not null
 					// because it is currently null.
 					final EntityEntry entry = eventSource.getPersistenceContext().getEntry( parent );
 					if ( entry != null && entry.getStatus() != Status.SAVING ) {
 						final Object loadedValue;
 						if ( componentPathStack.isEmpty() ) {
 							// association defined on entity
 							loadedValue = entry.getLoadedValue( propertyName );
 						}
 						else {
 							// association defined on component
 							// 		todo : this is currently unsupported because of the fact that
 							//		we do not know the loaded state of this value properly
 							//		and doing so would be very difficult given how components and
 							//		entities are loaded (and how 'loaded state' is put into the
 							//		EntityEntry).  Solutions here are to either:
 							//			1) properly account for components as a 2-phase load construct
 							//			2) just assume the association was just now orphaned and
 							// 				issue the orphan delete.  This would require a special
 							//				set of SQL statements though since we do not know the
 							//				orphaned value, something a delete with a subquery to
 							// 				match the owner.
 //							final EntityType entityType = (EntityType) type;
 //							final String getPropertyPath = composePropertyPath( entityType.getPropertyName() );
 							loadedValue = null;
 						}
 						if ( loadedValue != null ) {
 							final EntityEntry valueEntry = eventSource
 									.getPersistenceContext().getEntry( 
 											loadedValue );
 							// Need to check this in case the context has
 							// already been flushed.  See HHH-7829.
 							if ( valueEntry != null ) {
 								final String entityName = valueEntry.getPersister().getEntityName();
 								if ( LOG.isTraceEnabled() ) {
 									final Serializable id = valueEntry.getPersister().getIdentifier( loadedValue, eventSource );
 									final String description = MessageHelper.infoString( entityName, id );
 									LOG.tracev( "Deleting orphaned entity instance: {0}", description );
 								}
 								eventSource.delete( entityName, loadedValue, false, new HashSet() );
 							}
 						}
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Check if the association is a one to one in the logical model (either a shared-pk
 	 * or unique fk).
 	 *
 	 * @param type The type representing the attribute metadata
 	 *
 	 * @return True if the attribute represents a logical one to one association
 	 */
 	private boolean isLogicalOneToOne(Type type) {
 		return type.isEntityType() && ( (EntityType) type ).isLogicalOneToOne();
 	}
 
 	private Stack componentPathStack = new Stack();
 
 	private boolean cascadeAssociationNow(AssociationType associationType) {
 		return associationType.getForeignKeyDirection().cascadeNow(cascadeTo);
 	}
 
 	private void cascadeComponent(
 			final Object parent,
 			final Object child,
 			final CompositeType componentType,
 			final String componentPropertyName,
 			final Object anything) {
 		componentPathStack.push( componentPropertyName );
 		Object[] children = componentType.getPropertyValues( child, eventSource );
 		Type[] types = componentType.getSubtypes();
 		for ( int i=0; i<types.length; i++ ) {
 			final CascadeStyle componentPropertyStyle = componentType.getCascadeStyle(i);
 			final String subPropertyName = componentType.getPropertyNames()[i];
 			if ( componentPropertyStyle.doCascade(action) ) {
 				cascadeProperty(
 						parent,
 						children[i],
 						types[i],
 						componentPropertyStyle,
 						subPropertyName,
 						anything,
 						false
 					);
 			}
 		}
 		componentPathStack.pop();
 	}
 
 	private void cascadeAssociation(
 			final Object parent,
 			final Object child,
 			final Type type,
 			final CascadeStyle style,
 			final Object anything,
 			final boolean isCascadeDeleteEnabled) {
 		if ( type.isEntityType() || type.isAnyType() ) {
 			cascadeToOne( parent, child, type, style, anything, isCascadeDeleteEnabled );
 		}
 		else if ( type.isCollectionType() ) {
 			cascadeCollection( parent, child, style, anything, (CollectionType) type );
 		}
 	}
 
 	/**
 	 * Cascade an action to a collection
 	 */
 	private void cascadeCollection(
 			final Object parent,
 			final Object child,
 			final CascadeStyle style,
 			final Object anything,
 			final CollectionType type) {
 		CollectionPersister persister = eventSource.getFactory()
 				.getCollectionPersister( type.getRole() );
 		Type elemType = persister.getElementType();
 
 		final int oldCascadeTo = cascadeTo;
 		if ( cascadeTo==AFTER_INSERT_BEFORE_DELETE) {
 			cascadeTo = AFTER_INSERT_BEFORE_DELETE_VIA_COLLECTION;
 		}
 
 		//cascade to current collection elements
 		if ( elemType.isEntityType() || elemType.isAnyType() || elemType.isComponentType() ) {
 			cascadeCollectionElements(
 				parent,
 				child,
 				type,
 				style,
 				elemType,
 				anything,
 				persister.isCascadeDeleteEnabled()
 			);
 		}
 
 		cascadeTo = oldCascadeTo;
 	}
 
 	/**
 	 * Cascade an action to a to-one association or any type
 	 */
 	private void cascadeToOne(
 			final Object parent,
 			final Object child,
 			final Type type,
 			final CascadeStyle style,
 			final Object anything,
 			final boolean isCascadeDeleteEnabled) {
 		final String entityName = type.isEntityType()
 				? ( (EntityType) type ).getAssociatedEntityName()
 				: null;
 		if ( style.reallyDoCascade(action) ) { //not really necessary, but good for consistency...
 			eventSource.getPersistenceContext().addChildParent(child, parent);
 			try {
 				action.cascade(eventSource, child, entityName, anything, isCascadeDeleteEnabled);
 			}
 			finally {
 				eventSource.getPersistenceContext().removeChildParent(child);
 			}
 		}
 	}
 
 	/**
 	 * Cascade to the collection elements
 	 */
 	private void cascadeCollectionElements(
 			final Object parent,
 			final Object child,
 			final CollectionType collectionType,
 			final CascadeStyle style,
 			final Type elemType,
 			final Object anything,
 			final boolean isCascadeDeleteEnabled) throws HibernateException {
 
 		boolean reallyDoCascade = style.reallyDoCascade(action) && child!=CollectionType.UNFETCHED_COLLECTION;
 
 		if ( reallyDoCascade ) {
-			if ( LOG.isTraceEnabled() ) {
+            final boolean traceEnabled = LOG.isTraceEnabled();
+			if ( traceEnabled ) {
 				LOG.tracev( "Cascade {0} for collection: {1}", action, collectionType.getRole() );
 			}
 
 			Iterator iter = action.getCascadableChildrenIterator(eventSource, collectionType, child);
 			while ( iter.hasNext() ) {
 				cascadeProperty(
 						parent,
 						iter.next(),
 						elemType,
 						style,
 						null,
 						anything,
 						isCascadeDeleteEnabled
 					);
 			}
 
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev( "Done cascade {0} for collection: {1}", action, collectionType.getRole() );
 			}
 		}
 
 		final boolean deleteOrphans = style.hasOrphanDelete() &&
 				action.deleteOrphans() &&
 				elemType.isEntityType() &&
 				child instanceof PersistentCollection; //a newly instantiated collection can't have orphans
 
 		if ( deleteOrphans ) { // handle orphaned entities!!
-			if ( LOG.isTraceEnabled() ) {
+			final boolean traceEnabled = LOG.isTraceEnabled();
+			if ( traceEnabled ) {
 				LOG.tracev( "Deleting orphans for collection: {0}", collectionType.getRole() );
 			}
 			// we can do the cast since orphan-delete does not apply to:
 			// 1. newly instantiated collections
 			// 2. arrays (we can't track orphans for detached arrays)
 			final String entityName = collectionType.getAssociatedEntityName( eventSource.getFactory() );
 			deleteOrphans( entityName, (PersistentCollection) child );
 
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev( "Done deleting orphans for collection: {0}", collectionType.getRole() );
 			}
 		}
 	}
 
 	/**
 	 * Delete any entities that were removed from the collection
 	 */
 	private void deleteOrphans(String entityName, PersistentCollection pc) throws HibernateException {
 		//TODO: suck this logic into the collection!
 		final Collection orphans;
 		if ( pc.wasInitialized() ) {
 			CollectionEntry ce = eventSource.getPersistenceContext().getCollectionEntry(pc);
 			orphans = ce==null ?
 					java.util.Collections.EMPTY_LIST :
 					ce.getOrphans(entityName, pc);
 		}
 		else {
 			orphans = pc.getQueuedOrphans(entityName);
 		}
 
 		final Iterator orphanIter = orphans.iterator();
 		while ( orphanIter.hasNext() ) {
 			Object orphan = orphanIter.next();
 			if (orphan!=null) {
 				LOG.tracev( "Deleting orphaned entity instance: {0}", entityName );
 				eventSource.delete( entityName, orphan, false, new HashSet() );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/Collections.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/Collections.java
index 2c6a840ba3..9e2f3baf73 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/Collections.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Collections.java
@@ -1,267 +1,267 @@
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
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.CollectionType;
 import org.jboss.logging.Logger;
 
 /**
  * Implements book-keeping for the collection persistence by reachability algorithm
  *
  * @author Gavin King
  */
 public final class Collections {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Collections.class.getName());
 
 	private Collections() {
 	}
 
 	/**
 	 * record the fact that this collection was dereferenced
 	 *
 	 * @param coll The collection to be updated by un-reachability.
 	 */
 	@SuppressWarnings( {"JavaDoc"})
 	public static void processUnreachableCollection(PersistentCollection coll, SessionImplementor session) {
 		if ( coll.getOwner()==null ) {
 			processNeverReferencedCollection(coll, session);
 		}
 		else {
 			processDereferencedCollection(coll, session);
 		}
 	}
 
 	private static void processDereferencedCollection(PersistentCollection coll, SessionImplementor session) {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		CollectionEntry entry = persistenceContext.getCollectionEntry(coll);
 		final CollectionPersister loadedPersister = entry.getLoadedPersister();
 
-		if ( LOG.isDebugEnabled() && loadedPersister != null ) {
+		if ( loadedPersister != null && LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Collection dereferenced: %s",
 					MessageHelper.collectionInfoString( loadedPersister, 
 							coll, entry.getLoadedKey(), session
 					)
 			);
 		}
 
 		// do a check
 		boolean hasOrphanDelete = loadedPersister != null && loadedPersister.hasOrphanDelete();
 		if (hasOrphanDelete) {
 			Serializable ownerId = loadedPersister.getOwnerEntityPersister().getIdentifier( coll.getOwner(), session );
 			if ( ownerId == null ) {
 				// the owning entity may have been deleted and its identifier unset due to
 				// identifier-rollback; in which case, try to look up its identifier from
 				// the persistence context
 				if ( session.getFactory().getSettings().isIdentifierRollbackEnabled() ) {
 					EntityEntry ownerEntry = persistenceContext.getEntry( coll.getOwner() );
 					if ( ownerEntry != null ) {
 						ownerId = ownerEntry.getId();
 					}
 				}
 				if ( ownerId == null ) {
 					throw new AssertionFailure( "Unable to determine collection owner identifier for orphan-delete processing" );
 				}
 			}
 			EntityKey key = session.generateEntityKey( ownerId, loadedPersister.getOwnerEntityPersister() );
 			Object owner = persistenceContext.getEntity(key);
 			if ( owner == null ) {
 				throw new AssertionFailure(
 						"collection owner not associated with session: " +
 						loadedPersister.getRole()
 				);
 			}
 			EntityEntry e = persistenceContext.getEntry(owner);
 			//only collections belonging to deleted entities are allowed to be dereferenced in the case of orphan delete
 			if ( e != null && e.getStatus() != Status.DELETED && e.getStatus() != Status.GONE ) {
 				throw new HibernateException(
 						"A collection with cascade=\"all-delete-orphan\" was no longer referenced by the owning entity instance: " +
 						loadedPersister.getRole()
 				);
 			}
 		}
 
 		// do the work
 		entry.setCurrentPersister(null);
 		entry.setCurrentKey(null);
 		prepareCollectionForUpdate( coll, entry, session.getFactory() );
 
 	}
 
 	private static void processNeverReferencedCollection(PersistentCollection coll, SessionImplementor session)
 	throws HibernateException {
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		CollectionEntry entry = persistenceContext.getCollectionEntry(coll);
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Found collection with unloaded owner: %s",
 					MessageHelper.collectionInfoString( 
 							entry.getLoadedPersister(), coll,
 							entry.getLoadedKey(), session ) );
 		}
 
 		entry.setCurrentPersister( entry.getLoadedPersister() );
 		entry.setCurrentKey( entry.getLoadedKey() );
 
 		prepareCollectionForUpdate( coll, entry, session.getFactory() );
 
 	}
 
     /**
      * Initialize the role of the collection.
      *
      * @param collection The collection to be updated by reachability.
      * @param type The type of the collection.
      * @param entity The owner of the collection.
 	 * @param session The session from which this request originates
      */
 	public static void processReachableCollection(
 			PersistentCollection collection,
 	        CollectionType type,
 	        Object entity,
 	        SessionImplementor session) {
 
 		collection.setOwner(entity);
 
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry(collection);
 
 		if ( ce == null ) {
 			// refer to comment in StatefulPersistenceContext.addCollection()
 			throw new HibernateException(
 					"Found two representations of same collection: " +
 					type.getRole()
 			);
 		}
 
 		// The CollectionEntry.isReached() stuff is just to detect any silly users
 		// who set up circular or shared references between/to collections.
 		if ( ce.isReached() ) {
 			// We've been here before
 			throw new HibernateException(
 					"Found shared references to a collection: " +
 					type.getRole()
 			);
 		}
 		ce.setReached(true);
 
 		SessionFactoryImplementor factory = session.getFactory();
 		CollectionPersister persister = factory.getCollectionPersister( type.getRole() );
 		ce.setCurrentPersister(persister);
 		ce.setCurrentKey( type.getKeyOfOwner(entity, session) ); //TODO: better to pass the id in as an argument?
 
         if (LOG.isDebugEnabled()) {
             if (collection.wasInitialized()) LOG.debugf("Collection found: %s, was: %s (initialized)",
                                                         MessageHelper.collectionInfoString(persister, collection, ce.getCurrentKey(), session),
                                                         MessageHelper.collectionInfoString(ce.getLoadedPersister(), collection, 
                                                                                            ce.getLoadedKey(),
                                                                                            session));
             else LOG.debugf("Collection found: %s, was: %s (uninitialized)",
                             MessageHelper.collectionInfoString(persister, collection, ce.getCurrentKey(), session),
                             MessageHelper.collectionInfoString(ce.getLoadedPersister(), collection, ce.getLoadedKey(), session));
         }
 
 		prepareCollectionForUpdate( collection, ce, factory );
 
 	}
 
 	/**
 	 * 1. record the collection role that this collection is referenced by
 	 * 2. decide if the collection needs deleting/creating/updating (but
 	 *	don't actually schedule the action yet)
 	 */
 	@SuppressWarnings( {"JavaDoc"})
 	private static void prepareCollectionForUpdate(
 			PersistentCollection collection,
 	        CollectionEntry entry,
 	        SessionFactoryImplementor factory) {
 
 		if ( entry.isProcessed() ) {
 			throw new AssertionFailure( "collection was processed twice by flush()" );
 		}
 		entry.setProcessed( true );
 
 		final CollectionPersister loadedPersister = entry.getLoadedPersister();
 		final CollectionPersister currentPersister = entry.getCurrentPersister();
 		if ( loadedPersister != null || currentPersister != null ) {					// it is or was referenced _somewhere_
 
 			boolean ownerChanged = loadedPersister != currentPersister ||				// if either its role changed,
 			                       !currentPersister
 					                       .getKeyType().isEqual(                       // or its key changed
 													entry.getLoadedKey(),
 			                                        entry.getCurrentKey(),
 			                                        factory
 			                       );
 
 			if (ownerChanged) {
 
 				// do a check
 				final boolean orphanDeleteAndRoleChanged = loadedPersister != null &&
 				                                           currentPersister != null &&
 				                                           loadedPersister.hasOrphanDelete();
 
 				if (orphanDeleteAndRoleChanged) {
 					throw new HibernateException(
 							"Don't change the reference to a collection with cascade=\"all-delete-orphan\": " +
 							loadedPersister.getRole()
 					);
 				}
 
 				// do the work
 				if ( currentPersister != null ) {
 					entry.setDorecreate( true );	// we will need to create new entries
 				}
 
 				if ( loadedPersister != null ) {
 					entry.setDoremove( true );		// we will need to remove ye olde entries
 					if ( entry.isDorecreate() ) {
 						LOG.trace( "Forcing collection initialization" );
 						collection.forceInitialization();
 					}
 				}
 			}
 			else if ( collection.isDirty() ) {
 				// the collection's elements have changed
 				entry.setDoupdate( true );
 			}
 
 		}
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/ParameterBinder.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/ParameterBinder.java
index e4cc3d5745..1926cf3d84 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/ParameterBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/ParameterBinder.java
@@ -1,135 +1,136 @@
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
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.Type;
 
 /**
  * Centralizes the commonality regarding binding of parameter values into PreparedStatements as this logic is
  * used in many places.
  * <p/>
  * Ideally would like to move to the parameter handling as it is done in the hql.ast package.
  *
  * @author Steve Ebersole
  */
 public class ParameterBinder {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ParameterBinder.class.getName());
 
 	public static interface NamedParameterSource {
 		public int[] getNamedParameterLocations(String name);
 	}
 
 	private ParameterBinder() {
 	}
 
 	public static int bindQueryParameters(
 	        final PreparedStatement st,
 	        final QueryParameters queryParameters,
 	        final int start,
 	        final NamedParameterSource source,
 	        SessionImplementor session) throws SQLException, HibernateException {
 		int col = start;
 		col += bindPositionalParameters( st, queryParameters, col, session );
 		col += bindNamedParameters( st, queryParameters, col, source, session );
 		return col;
 	}
 
 	public static int bindPositionalParameters(
 	        final PreparedStatement st,
 	        final QueryParameters queryParameters,
 	        final int start,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 		return bindPositionalParameters(
 		        st,
 		        queryParameters.getPositionalParameterValues(),
 		        queryParameters.getPositionalParameterTypes(),
 		        start,
 		        session
 		);
 	}
 
 	public static int bindPositionalParameters(
 	        final PreparedStatement st,
 	        final Object[] values,
 	        final Type[] types,
 	        final int start,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 		int span = 0;
 		for ( int i = 0; i < values.length; i++ ) {
 			types[i].nullSafeSet( st, values[i], start + span, session );
 			span += types[i].getColumnSpan( session.getFactory() );
 		}
 		return span;
 	}
 
 	public static int bindNamedParameters(
 	        final PreparedStatement ps,
 	        final QueryParameters queryParameters,
 	        final int start,
 	        final NamedParameterSource source,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 		return bindNamedParameters( ps, queryParameters.getNamedParameters(), start, source, session );
 	}
 
 	public static int bindNamedParameters(
 	        final PreparedStatement ps,
 	        final Map namedParams,
 	        final int start,
 	        final NamedParameterSource source,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 		if ( namedParams != null ) {
+			final boolean debugEnabled = LOG.isDebugEnabled();
 			// assumes that types are all of span 1
 			Iterator iter = namedParams.entrySet().iterator();
 			int result = 0;
 			while ( iter.hasNext() ) {
 				Map.Entry e = ( Map.Entry ) iter.next();
 				String name = ( String ) e.getKey();
 				TypedValue typedval = (TypedValue) e.getValue();
 				int[] locations = source.getNamedParameterLocations( name );
 				for ( int i = 0; i < locations.length; i++ ) {
-					if ( LOG.isDebugEnabled() ) {
+					if ( debugEnabled ) {
 						LOG.debugf("bindNamedParameters() %s -> %s [%s]", typedval.getValue(), name, locations[i] + start);
 					}
 					typedval.getType().nullSafeSet( ps, typedval.getValue(), locations[i] + start, session );
 				}
 				result += locations.length;
 			}
 			return result;
 		}
         return 0;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
index e5ac56a056..2cdf7f9c88 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
@@ -1,385 +1,386 @@
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
 package org.hibernate.engine.internal;
 
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PostLoadEventListener;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.event.spi.PreLoadEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
  * Functionality relating to Hibernate's two-phase loading process,
  * that may be reused by persisters that do not use the Loader
  * framework
  *
  * @author Gavin King
  */
 public final class TwoPhaseLoad {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, TwoPhaseLoad.class.getName() );
 
 	private TwoPhaseLoad() {}
 
 	/**
 	 * Register the "hydrated" state of an entity instance, after the first step of 2-phase loading.
 	 *
 	 * Add the "hydrated state" (an array) of an uninitialized entity to the session. We don't try
 	 * to resolve any associations yet, because there might be other entities waiting to be
 	 * read from the JDBC result set we are currently processing
 	 */
 	public static void postHydrate(
 		final EntityPersister persister,
 		final Serializable id,
 		final Object[] values,
 		final Object rowId,
 		final Object object,
 		final LockMode lockMode,
 		final boolean lazyPropertiesAreUnfetched,
 		final SessionImplementor session)
 	throws HibernateException {
 
 		Object version = Versioning.getVersion( values, persister );
 		session.getPersistenceContext().addEntry(
 				object,
 				Status.LOADING,
 				values,
 				rowId,
 				id,
 				version,
 				lockMode,
 				true,
 				persister,
 				false,
 				lazyPropertiesAreUnfetched
 			);
 
-		if ( LOG.isTraceEnabled() && version != null ) {
+		if ( version != null && LOG.isTraceEnabled() ) {
 			String versionStr = persister.isVersioned()
 					? persister.getVersionType().toLoggableString( version, session.getFactory() )
 					: "null";
 			LOG.tracev( "Version: {0}", versionStr );
 		}
 
 	}
 
 	/**
 	 * Perform the second step of 2-phase load. Fully initialize the entity
 	 * instance.
 	 *
 	 * After processing a JDBC result set, we "resolve" all the associations
 	 * between the entities which were instantiated and had their state
 	 * "hydrated" into an array
 	 */
 	public static void initializeEntity(
 			final Object entity,
 			final boolean readOnly,
 			final SessionImplementor session,
 			final PreLoadEvent preLoadEvent,
 			final PostLoadEvent postLoadEvent) throws HibernateException {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityEntry entityEntry = persistenceContext.getEntry(entity);
 		if ( entityEntry == null ) {
 			throw new AssertionFailure( "possible non-threadsafe access to the session" );
 		}
 		final EntityPersister persister = entityEntry.getPersister();
 		final Serializable id = entityEntry.getId();
 
 //		persistenceContext.getNaturalIdHelper().startingLoad( persister, id );
 //		try {
 			doInitializeEntity( entity, entityEntry, readOnly, session, preLoadEvent, postLoadEvent );
 //		}
 //		finally {
 //			persistenceContext.getNaturalIdHelper().endingLoad( persister, id );
 //		}
 	}
 
 	private static void doInitializeEntity(
 			final Object entity,
 			final EntityEntry entityEntry,
 			final boolean readOnly,
 			final SessionImplementor session,
 			final PreLoadEvent preLoadEvent,
 			final PostLoadEvent postLoadEvent) throws HibernateException {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		EntityPersister persister = entityEntry.getPersister();
 		Serializable id = entityEntry.getId();
 		Object[] hydratedState = entityEntry.getLoadedState();
 
-		if ( LOG.isDebugEnabled() ) {
+		final boolean debugEnabled = LOG.isDebugEnabled();
+		if ( debugEnabled ) {
 			LOG.debugf(
 					"Resolving associations for %s",
 					MessageHelper.infoString( persister, id, session.getFactory() )
 			);
 		}
 
 		Type[] types = persister.getPropertyTypes();
 		for ( int i = 0; i < hydratedState.length; i++ ) {
 			final Object value = hydratedState[i];
 			if ( value!=LazyPropertyInitializer.UNFETCHED_PROPERTY && value!=BackrefPropertyAccessor.UNKNOWN ) {
 				hydratedState[i] = types[i].resolve( value, session, entity );
 			}
 		}
 
 		//Must occur after resolving identifiers!
 		if ( session.isEventSource() ) {
 			preLoadEvent.setEntity( entity ).setState( hydratedState ).setId( id ).setPersister( persister );
 
 			final EventListenerGroup<PreLoadEventListener> listenerGroup = session
 					.getFactory()
 					.getServiceRegistry()
 					.getService( EventListenerRegistry.class )
 					.getEventListenerGroup( EventType.PRE_LOAD );
 			for ( PreLoadEventListener listener : listenerGroup.listeners() ) {
 				listener.onPreLoad( preLoadEvent );
 			}
 		}
 
 		persister.setPropertyValues( entity, hydratedState );
 
 		final SessionFactoryImplementor factory = session.getFactory();
 		if ( persister.hasCache() && session.getCacheMode().isPutEnabled() ) {
 
-			if ( LOG.isDebugEnabled() ) {
+			if ( debugEnabled ) {
 				LOG.debugf(
 						"Adding entity to second-level cache: %s",
 						MessageHelper.infoString( persister, id, session.getFactory() )
 				);
 			}
 
 			Object version = Versioning.getVersion(hydratedState, persister);
 			CacheEntry entry = persister.buildCacheEntry( entity, hydratedState, version, session );
 			CacheKey cacheKey = session.generateCacheKey( id, persister.getIdentifierType(), persister.getRootEntityName() );
 
 			// explicit handling of caching for rows just inserted and then somehow forced to be read
 			// from the database *within the same transaction*.  usually this is done by
 			// 		1) Session#refresh, or
 			// 		2) Session#clear + some form of load
 			//
 			// we need to be careful not to clobber the lock here in the cache so that it can be rolled back if need be
 			if ( session.getPersistenceContext().wasInsertedDuringTransaction( persister, id ) ) {
 				persister.getCacheAccessStrategy().update(
 						cacheKey,
 						persister.getCacheEntryStructure().structure( entry ),
 						version,
 						version
 				);
 			}
 			else {
 				boolean put = persister.getCacheAccessStrategy().putFromLoad(
 						cacheKey,
 						persister.getCacheEntryStructure().structure( entry ),
 						session.getTimestamp(),
 						version,
 						useMinimalPuts( session, entityEntry )
 				);
 
 				if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 					factory.getStatisticsImplementor().secondLevelCachePut( persister.getCacheAccessStrategy().getRegion().getName() );
 				}
 			}
 		}
 
 		if ( persister.hasNaturalIdentifier() ) {
 			persistenceContext.getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					id,
 					persistenceContext.getNaturalIdHelper().extractNaturalIdValues( hydratedState, persister )
 			);
 		}
 
 		boolean isReallyReadOnly = readOnly;
 		if ( !persister.isMutable() ) {
 			isReallyReadOnly = true;
 		}
 		else {
 			Object proxy = persistenceContext.getProxy( entityEntry.getEntityKey() );
 			if ( proxy != null ) {
 				// there is already a proxy for this impl
 				// only set the status to read-only if the proxy is read-only
 				isReallyReadOnly = ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().isReadOnly();
 			}
 		}
 		if ( isReallyReadOnly ) {
 			//no need to take a snapshot - this is a
 			//performance optimization, but not really
 			//important, except for entities with huge
 			//mutable property values
 			persistenceContext.setEntryStatus(entityEntry, Status.READ_ONLY);
 		}
 		else {
 			//take a snapshot
 			TypeHelper.deepCopy(
 					hydratedState,
 					persister.getPropertyTypes(),
 					persister.getPropertyUpdateability(),
 					hydratedState,  //after setting values to object, entityMode
 					session
 			);
 			persistenceContext.setEntryStatus(entityEntry, Status.MANAGED);
 		}
 
 		persister.afterInitialize(
 				entity,
 				entityEntry.isLoadedWithLazyPropertiesUnfetched(),
 				session
 		);
 
-		if ( LOG.isDebugEnabled() ) {
+		if ( debugEnabled ) {
 			LOG.debugf(
 					"Done materializing entity %s",
 					MessageHelper.infoString( persister, id, session.getFactory() )
 			);
 		}
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().loadEntity( persister.getEntityName() );
 		}
 	}
 	
 	/**
 	 * PostLoad cannot occur during initializeEntity, as that call occurs *before*
 	 * the Set collections are added to the persistence context by Loader.
 	 * Without the split, LazyInitializationExceptions can occur in the Entity's
 	 * postLoad if it acts upon the collection.
 	 * 
 	 * 
 	 * HHH-6043
 	 * 
 	 * @param entity
 	 * @param session
 	 * @param postLoadEvent
 	 */
 	public static void postLoad(
 			final Object entity,
 			final SessionImplementor session,
 			final PostLoadEvent postLoadEvent) {
 		
 		if ( session.isEventSource() ) {
 			final PersistenceContext persistenceContext
 					= session.getPersistenceContext();
 			final EntityEntry entityEntry = persistenceContext.getEntry(entity);
 			final Serializable id = entityEntry.getId();
 			
 			postLoadEvent.setEntity( entity ).setId( entityEntry.getId() )
 					.setPersister( entityEntry.getPersister() );
 
 			final EventListenerGroup<PostLoadEventListener> listenerGroup
 					= session
 							.getFactory()
 							.getServiceRegistry()
 							.getService( EventListenerRegistry.class )
 							.getEventListenerGroup( EventType.POST_LOAD );
 			for ( PostLoadEventListener listener : listenerGroup.listeners() ) {
 				listener.onPostLoad( postLoadEvent );
 			}
 		}
 	}
 
 	private static boolean useMinimalPuts(SessionImplementor session, EntityEntry entityEntry) {
 		return ( session.getFactory().getSettings().isMinimalPutsEnabled() &&
 						session.getCacheMode()!=CacheMode.REFRESH ) ||
 				( entityEntry.getPersister().hasLazyProperties() &&
 						entityEntry.isLoadedWithLazyPropertiesUnfetched() &&
 						entityEntry.getPersister().isLazyPropertiesCacheable() );
 	}
 
 	/**
 	 * Add an uninitialized instance of an entity class, as a placeholder to ensure object
 	 * identity. Must be called before <tt>postHydrate()</tt>.
 	 *
 	 * Create a "temporary" entry for a newly instantiated entity. The entity is uninitialized,
 	 * but we need the mapping from id to instance in order to guarantee uniqueness.
 	 */
 	public static void addUninitializedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnfetched,
 			final SessionImplementor session
 	) {
 		session.getPersistenceContext().addEntity(
 				object,
 				Status.LOADING,
 				null,
 				key,
 				null,
 				lockMode,
 				true,
 				persister,
 				false,
 				lazyPropertiesAreUnfetched
 			);
 	}
 
 	public static void addUninitializedCachedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnfetched,
 			final Object version,
 			final SessionImplementor session
 	) {
 		session.getPersistenceContext().addEntity(
 				object,
 				Status.LOADING,
 				null,
 				key,
 				version,
 				lockMode,
 				true,
 				persister,
 				false,
 				lazyPropertiesAreUnfetched
 			);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
index 0581f82b1d..f1d56fb263 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
@@ -1,295 +1,298 @@
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SqlExceptionHelper.class.getName());
 
 	public static final String DEFAULT_EXCEPTION_MSG = "SQL Exception";
 	public static final String DEFAULT_WARNING_MSG = "SQL Warning";
 
 	public static final SQLExceptionConverter DEFAULT_CONVERTER = new SQLStateConverter(
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
 		this.sqlExceptionConverter = ( sqlExceptionConverter == null ? DEFAULT_CONVERTER : sqlExceptionConverter );
 	}
 
     // SQLException ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
     /**
      * Convert an SQLException using the current converter, doing some logging first.
      *
      * @param sqlException The exception to convert
      * @param message An error message.
      * @return The converted exception
      */
     public JDBCException convert( SQLException sqlException,
                                   String message ) {
         return convert(sqlException, message, "n/a");
     }
 
     /**
      * Convert an SQLException using the current converter, doing some logging first.
      *
      * @param sqlException The exception to convert
      * @param message An error message.
      * @param sql The SQL being executed when the exception occurred
      * @return The converted exception
      */
     public JDBCException convert( SQLException sqlException,
                                   String message,
                                   String sql ) {
         logExceptions(sqlException, message + " [" + sql + "]");
         return sqlExceptionConverter.convert(sqlException, message, sql);
     }
 
     /**
      * Log the given (and any nested) exception.
      *
      * @param sqlException The exception to log
      * @param message The message text to use as a preamble.
      */
     public void logExceptions( SQLException sqlException,
                                String message ) {
         if (LOG.isEnabled(Level.ERROR)) {
             if (LOG.isDebugEnabled()) {
                 message = StringHelper.isNotEmpty(message) ? message : DEFAULT_EXCEPTION_MSG;
-				LOG.debug( message, sqlException );
+                LOG.debug( message, sqlException );
             }
+            final boolean warnEnabled = LOG.isEnabled( Level.WARN );
             while (sqlException != null) {
-                StringBuilder buf = new StringBuilder(30).append("SQL Error: ").append(sqlException.getErrorCode()).append(", SQLState: ").append(sqlException.getSQLState());
-                LOG.warn(buf.toString());
+                if ( warnEnabled ) {
+                    StringBuilder buf = new StringBuilder(30).append("SQL Error: ").append(sqlException.getErrorCode()).append(", SQLState: ").append(sqlException.getSQLState());
+                    LOG.warn(buf.toString());
+                }
                 LOG.error(sqlException.getMessage());
                 sqlException = sqlException.getNextException();
             }
         }
     }
 
     // SQLWarning ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
     /**
      * Contract for handling {@link SQLWarning warnings}
      */
     public static interface WarningHandler {
         /**
          * Should processing be done? Allows short-circuiting if not.
          *
          * @return True to process warnings, false otherwise.
          */
         public boolean doProcess();
 
         /**
          * Prepare for processing of a {@link SQLWarning warning} stack.
          * <p/>
          * Note that the warning here is also the first passed to {@link #handleWarning}
          *
          * @param warning The first warning in the stack.
          */
         public void prepare( SQLWarning warning );
 
         /**
          * Handle an individual warning in the stack.
          *
          * @param warning The warning to handle.
          */
         public void handleWarning( SQLWarning warning );
     }
 
     /**
      * Basic support for {@link WarningHandler} implementations which log
      */
     public static abstract class WarningHandlerLoggingSupport implements WarningHandler {
         public final void handleWarning( SQLWarning warning ) {
         	StringBuilder buf = new StringBuilder(30).append("SQL Warning Code: ").append(warning.getErrorCode()).append(", SQLState: ").append(warning.getSQLState());
             logWarning(buf.toString(), warning.getMessage());
         }
 
         /**
          * Delegate to log common details of a {@link SQLWarning warning}
          *
          * @param description A description of the warning
          * @param message The warning message
          */
         protected abstract void logWarning( String description,
                                             String message );
     }
 
     public static class StandardWarningHandler extends WarningHandlerLoggingSupport {
         private final String introMessage;
 
         public StandardWarningHandler( String introMessage ) {
             this.introMessage = introMessage;
         }
 
         public boolean doProcess() {
             return LOG.isEnabled(Level.WARN);
         }
 
         public void prepare( SQLWarning warning ) {
             LOG.debug(introMessage, warning);
         }
 
         @Override
         protected void logWarning( String description,
                                    String message ) {
             LOG.warn(description);
             LOG.warn(message);
         }
     }
 
     public static StandardWarningHandler STANDARD_WARNING_HANDLER = new StandardWarningHandler(DEFAULT_WARNING_MSG);
 
     public void walkWarnings( SQLWarning warning,
                               WarningHandler handler ) {
         if (warning == null || handler.doProcess()) {
             return;
         }
         handler.prepare(warning);
         while (warning != null) {
             handler.handleWarning(warning);
             warning = warning.getNextWarning();
         }
     }
 
     /**
      * Standard (legacy) behavior for logging warnings associated with a JDBC {@link Connection} and clearing them.
      * <p/>
      * Calls {@link #handleAndClearWarnings(Connection, WarningHandler)} using {@link #STANDARD_WARNING_HANDLER}
      *
      * @param connection The JDBC connection potentially containing warnings
      */
     public void logAndClearWarnings( Connection connection ) {
         handleAndClearWarnings(connection, STANDARD_WARNING_HANDLER);
     }
 
     /**
      * General purpose handling of warnings associated with a JDBC {@link Connection}.
      *
      * @param connection The JDBC connection potentially containing warnings
      * @param handler The handler for each individual warning in the stack.
      * @see #walkWarnings
      */
     @SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"} )
     public void handleAndClearWarnings( Connection connection,
                                         WarningHandler handler ) {
         try {
             walkWarnings(connection.getWarnings(), handler);
         } catch (SQLException sqle) {
             // workaround for WebLogic
             LOG.debug("could not log warnings", sqle);
         }
         try {
             // Sybase fail if we don't do that, sigh...
             connection.clearWarnings();
         } catch (SQLException sqle) {
             LOG.debug("could not clear warnings", sqle);
         }
     }
 
     /**
      * General purpose handling of warnings associated with a JDBC {@link Statement}.
      *
      * @param statement The JDBC statement potentially containing warnings
      * @param handler The handler for each individual warning in the stack.
      * @see #walkWarnings
      */
     @SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"} )
     public void handleAndClearWarnings( Statement statement,
                                         WarningHandler handler ) {
         try {
             walkWarnings(statement.getWarnings(), handler);
         } catch (SQLException sqlException) {
             // workaround for WebLogic
             LOG.debug("could not log warnings", sqlException);
         }
         try {
             // Sybase fail if we don't do that, sigh...
             statement.clearWarnings();
         } catch (SQLException sqle) {
             LOG.debug("could not clear warnings", sqle);
         }
     }
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
index 831711265a..a23b662f30 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
@@ -1,363 +1,367 @@
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
 package org.hibernate.engine.loading.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.entry.CollectionCacheEntry;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.jboss.logging.Logger;
 
 /**
  * Represents state associated with the processing of a given {@link ResultSet}
  * in regards to loading collections.
  * <p/>
  * Another implementation option to consider is to not expose {@link ResultSet}s
  * directly (in the JDBC redesign) but to always "wrap" them and apply a
  * [series of] context[s] to that wrapper.
  *
  * @author Steve Ebersole
  */
 public class CollectionLoadContext {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionLoadContext.class.getName());
 
 	private final LoadContexts loadContexts;
 	private final ResultSet resultSet;
 	private Set localLoadingCollectionKeys = new HashSet();
 
 	/**
 	 * Creates a collection load context for the given result set.
 	 *
 	 * @param loadContexts Callback to other collection load contexts.
 	 * @param resultSet The result set this is "wrapping".
 	 */
 	public CollectionLoadContext(LoadContexts loadContexts, ResultSet resultSet) {
 		this.loadContexts = loadContexts;
 		this.resultSet = resultSet;
 	}
 
 	public ResultSet getResultSet() {
 		return resultSet;
 	}
 
 	public LoadContexts getLoadContext() {
 		return loadContexts;
 	}
 
 	/**
 	 * Retrieve the collection that is being loaded as part of processing this
 	 * result set.
 	 * <p/>
 	 * Basically, there are two valid return values from this method:<ul>
 	 * <li>an instance of {@link org.hibernate.collection.spi.PersistentCollection} which indicates to
 	 * continue loading the result set row data into that returned collection
 	 * instance; this may be either an instance already associated and in the
 	 * midst of being loaded, or a newly instantiated instance as a matching
 	 * associated collection was not found.</li>
 	 * <li><i>null</i> indicates to ignore the corresponding result set row
 	 * data relating to the requested collection; this indicates that either
 	 * the collection was found to already be associated with the persistence
 	 * context in a fully loaded state, or it was found in a loading state
 	 * associated with another result set processing context.</li>
 	 * </ul>
 	 *
 	 * @param persister The persister for the collection being requested.
 	 * @param key The key of the collection being requested.
 	 *
 	 * @return The loading collection (see discussion above).
 	 */
 	public PersistentCollection getLoadingCollection(final CollectionPersister persister, final Serializable key) {
 		final EntityMode em = persister.getOwnerEntityPersister().getEntityMetamodel().getEntityMode();
 		final CollectionKey collectionKey = new CollectionKey( persister, key, em );
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Starting attempt to find loading collection [{0}]",
 					MessageHelper.collectionInfoString( persister.getRole(), key ) );
 		}
 		final LoadingCollectionEntry loadingCollectionEntry = loadContexts.locateLoadingCollectionEntry( collectionKey );
 		if ( loadingCollectionEntry == null ) {
 			// look for existing collection as part of the persistence context
 			PersistentCollection collection = loadContexts.getPersistenceContext().getCollection( collectionKey );
 			if ( collection != null ) {
 				if ( collection.wasInitialized() ) {
 					LOG.trace( "Collection already initialized; ignoring" );
 					return null; // ignore this row of results! Note the early exit
 				}
 				LOG.trace( "Collection not yet initialized; initializing" );
 			}
 			else {
 				Object owner = loadContexts.getPersistenceContext().getCollectionOwner( key, persister );
 				final boolean newlySavedEntity = owner != null
 						&& loadContexts.getPersistenceContext().getEntry( owner ).getStatus() != Status.LOADING;
 				if ( newlySavedEntity ) {
 					// important, to account for newly saved entities in query
 					// todo : some kind of check for new status...
 					LOG.trace( "Owning entity already loaded; ignoring" );
 					return null;
 				}
 				// create one
 				LOG.tracev( "Instantiating new collection [key={0}, rs={1}]", key, resultSet );
 				collection = persister.getCollectionType().instantiate(
 						loadContexts.getPersistenceContext().getSession(), persister, key );
 			}
 			collection.beforeInitialize( persister, -1 );
 			collection.beginRead();
 			localLoadingCollectionKeys.add( collectionKey );
 			loadContexts.registerLoadingCollectionXRef( collectionKey, new LoadingCollectionEntry( resultSet, persister, key, collection ) );
 			return collection;
 		}
 		if ( loadingCollectionEntry.getResultSet() == resultSet ) {
 			LOG.trace( "Found loading collection bound to current result set processing; reading row" );
 			return loadingCollectionEntry.getCollection();
 		}
 		// ignore this row, the collection is in process of
 		// being loaded somewhere further "up" the stack
 		LOG.trace( "Collection is already being initialized; ignoring row" );
 		return null;
 	}
 
 	/**
 	 * Finish the process of collection-loading for this bound result set.  Mainly this
 	 * involves cleaning up resources and notifying the collections that loading is
 	 * complete.
 	 *
 	 * @param persister The persister for which to complete loading.
 	 */
 	public void endLoadingCollections(CollectionPersister persister) {
 		SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 		if ( !loadContexts.hasLoadingCollectionEntries()
 				&& localLoadingCollectionKeys.isEmpty() ) {
 			return;
 		}
 
 		// in an effort to avoid concurrent-modification-exceptions (from
 		// potential recursive calls back through here as a result of the
 		// eventual call to PersistentCollection#endRead), we scan the
 		// internal loadingCollections map for matches and store those matches
 		// in a temp collection.  the temp collection is then used to "drive"
 		// the #endRead processing.
 		List matches = null;
 		Iterator iter = localLoadingCollectionKeys.iterator();
 		while ( iter.hasNext() ) {
 			final CollectionKey collectionKey = (CollectionKey) iter.next();
 			final LoadingCollectionEntry lce = loadContexts.locateLoadingCollectionEntry( collectionKey );
 			if ( lce == null ) {
 				LOG.loadingCollectionKeyNotFound( collectionKey );
 			}
 			else if ( lce.getResultSet() == resultSet && lce.getPersister() == persister ) {
 				if ( matches == null ) {
 					matches = new ArrayList();
 				}
 				matches.add( lce );
 				if ( lce.getCollection().getOwner() == null ) {
 					session.getPersistenceContext().addUnownedCollection(
 							new CollectionKey(
 									persister,
 									lce.getKey(),
 									persister.getOwnerEntityPersister().getEntityMetamodel().getEntityMode()
 							),
 							lce.getCollection()
 					);
 				}
 				LOG.tracev( "Removing collection load entry [{0}]", lce );
 
 				// todo : i'd much rather have this done from #endLoadingCollection(CollectionPersister,LoadingCollectionEntry)...
 				loadContexts.unregisterLoadingCollectionXRef( collectionKey );
 				iter.remove();
 			}
 		}
 
 		endLoadingCollections( persister, matches );
 		if ( localLoadingCollectionKeys.isEmpty() ) {
 			// todo : hack!!!
 			// NOTE : here we cleanup the load context when we have no more local
 			// LCE entries.  This "works" for the time being because really
 			// only the collection load contexts are implemented.  Long term,
 			// this cleanup should become part of the "close result set"
 			// processing from the (sandbox/jdbc) jdbc-container code.
 			loadContexts.cleanup( resultSet );
 		}
 	}
 
 	private void endLoadingCollections(CollectionPersister persister, List matchedCollectionEntries) {
+		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( matchedCollectionEntries == null ) {
-			if ( LOG.isDebugEnabled()) LOG.debugf( "No collections were found in result set for role: %s", persister.getRole() );
+			if ( debugEnabled ) LOG.debugf( "No collections were found in result set for role: %s", persister.getRole() );
 			return;
 		}
 
 		final int count = matchedCollectionEntries.size();
-		if ( LOG.isDebugEnabled()) LOG.debugf("%s collections were found in result set for role: %s", count, persister.getRole());
+		if ( debugEnabled ) LOG.debugf("%s collections were found in result set for role: %s", count, persister.getRole());
 
 		for ( int i = 0; i < count; i++ ) {
 			LoadingCollectionEntry lce = ( LoadingCollectionEntry ) matchedCollectionEntries.get( i );
 			endLoadingCollection( lce, persister );
 		}
 
-		if ( LOG.isDebugEnabled() ) LOG.debugf( "%s collections initialized for role: %s", count, persister.getRole() );
+		if ( debugEnabled ) LOG.debugf( "%s collections initialized for role: %s", count, persister.getRole() );
 	}
 
 	private void endLoadingCollection(LoadingCollectionEntry lce, CollectionPersister persister) {
 		LOG.tracev( "Ending loading collection [{0}]", lce );
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 
 		boolean hasNoQueuedAdds = lce.getCollection().endRead(); // warning: can cause a recursive calls! (proxy initialization)
 
 		if ( persister.getCollectionType().hasHolder() ) {
 			getLoadContext().getPersistenceContext().addCollectionHolder( lce.getCollection() );
 		}
 
 		CollectionEntry ce = getLoadContext().getPersistenceContext().getCollectionEntry( lce.getCollection() );
 		if ( ce == null ) {
 			ce = getLoadContext().getPersistenceContext().addInitializedCollection( persister, lce.getCollection(), lce.getKey() );
 		}
 		else {
 			ce.postInitialize( lce.getCollection() );
 //			if (ce.getLoadedPersister().getBatchSize() > 1) { // not the best place for doing this, moved into ce.postInitialize
 //				getLoadContext().getPersistenceContext().getBatchFetchQueue().removeBatchLoadableCollection(ce); 
 //			}
 		}
 		
 
 
 		boolean addToCache = hasNoQueuedAdds && // there were no queued additions
 				persister.hasCache() &&             // and the role has a cache
 				session.getCacheMode().isPutEnabled() &&
 				!ce.isDoremove();                   // and this is not a forced initialization during flush
 		if ( addToCache ) {
 			addCollectionToCache( lce, persister );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Collection fully initialized: %s",
 					MessageHelper.collectionInfoString(persister, lce.getCollection(), lce.getKey(), session)
 			);
 		}
 		if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 			session.getFactory().getStatisticsImplementor().loadCollection(persister.getRole());
 		}
 	}
 
 	/**
 	 * Add the collection to the second-level cache
 	 *
 	 * @param lce The entry representing the collection to add
 	 * @param persister The persister
 	 */
 	private void addCollectionToCache(LoadingCollectionEntry lce, CollectionPersister persister) {
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 		final SessionFactoryImplementor factory = session.getFactory();
 
-		if ( LOG.isDebugEnabled() ) {
+		final boolean debugEnabled = LOG.isDebugEnabled();
+		if ( debugEnabled ) {
 			LOG.debugf( "Caching collection: %s", MessageHelper.collectionInfoString( persister, lce.getCollection(), lce.getKey(), session ) );
 		}
 
 		if ( !session.getEnabledFilters().isEmpty() && persister.isAffectedByEnabledFilters( session ) ) {
 			// some filters affecting the collection are enabled on the session, so do not do the put into the cache.
-			LOG.debug( "Refusing to add to cache due to enabled filters" );
+			if ( debugEnabled ) {
+				LOG.debug( "Refusing to add to cache due to enabled filters" );
+			}
 			// todo : add the notion of enabled filters to the CacheKey to differentiate filtered collections from non-filtered;
 			//      but CacheKey is currently used for both collections and entities; would ideally need to define two seperate ones;
 			//      currently this works in conjuction with the check on
 			//      DefaultInitializeCollectionEventHandler.initializeCollectionFromCache() (which makes sure to not read from
 			//      cache with enabled filters).
 			return; // EARLY EXIT!!!!!
 		}
 
 		final Object version;
 		if ( persister.isVersioned() ) {
 			Object collectionOwner = getLoadContext().getPersistenceContext().getCollectionOwner( lce.getKey(), persister );
 			if ( collectionOwner == null ) {
 				// generally speaking this would be caused by the collection key being defined by a property-ref, thus
 				// the collection key and the owner key would not match up.  In this case, try to use the key of the
 				// owner instance associated with the collection itself, if one.  If the collection does already know
 				// about its owner, that owner should be the same instance as associated with the PC, but we do the
 				// resolution against the PC anyway just to be safe since the lookup should not be costly.
 				if ( lce.getCollection() != null ) {
 					Object linkedOwner = lce.getCollection().getOwner();
 					if ( linkedOwner != null ) {
 						final Serializable ownerKey = persister.getOwnerEntityPersister().getIdentifier( linkedOwner, session );
 						collectionOwner = getLoadContext().getPersistenceContext().getCollectionOwner( ownerKey, persister );
 					}
 				}
 				if ( collectionOwner == null ) {
 					throw new HibernateException(
 							"Unable to resolve owner of loading collection [" +
 									MessageHelper.collectionInfoString( persister, lce.getCollection(), lce.getKey(), session ) +
 									"] for second level caching"
 					);
 				}
 			}
 			version = getLoadContext().getPersistenceContext().getEntry( collectionOwner ).getVersion();
 		}
 		else {
 			version = null;
 		}
 
 		CollectionCacheEntry entry = new CollectionCacheEntry( lce.getCollection(), persister );
 		CacheKey cacheKey = session.generateCacheKey( lce.getKey(), persister.getKeyType(), persister.getRole() );
 		boolean put = persister.getCacheAccessStrategy().putFromLoad(
 				cacheKey,
 				persister.getCacheEntryStructure().structure(entry),
 				session.getTimestamp(),
 				version,
 				factory.getSettings().isMinimalPutsEnabled() && session.getCacheMode()!= CacheMode.REFRESH
 		);
 
 		if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().secondLevelCachePut( persister.getCacheAccessStrategy().getRegion().getName() );
 		}
 	}
 
 	void cleanup() {
 		if ( !localLoadingCollectionKeys.isEmpty() ) {
 			LOG.localLoadingCollectionKeysCount( localLoadingCollectionKeys.size() );
 		}
 		loadContexts.cleanupCollectionXRefs( localLoadingCollectionKeys );
 		localLoadingCollectionKeys.clear();
 	}
 
 
 	@Override
     public String toString() {
 		return super.toString() + "<rs=" + resultSet + ">";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java
index 235e1711ce..3f23c01350 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java
@@ -1,515 +1,516 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 import java.util.Map;
 
 import org.hibernate.LockMode;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.action.internal.AbstractEntityInsertAction;
 import org.hibernate.action.internal.EntityIdentityInsertAction;
 import org.hibernate.action.internal.EntityInsertAction;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.id.IdentifierGenerationException;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.jboss.logging.Logger;
 
 /**
  * A convenience bas class for listeners responding to save events.
  *
  * @author Steve Ebersole.
  */
 public abstract class AbstractSaveEventListener extends AbstractReassociateEventListener {
     public enum EntityState{
         PERSISTENT, TRANSIENT, DETACHED, DELETED
     }
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractSaveEventListener.class.getName());
 
 	/**
 	 * Prepares the save call using the given requested id.
 	 *
 	 * @param entity The entity to be saved.
 	 * @param requestedId The id to which to associate the entity.
 	 * @param entityName The name of the entity being saved.
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of this save event.
 	 *
 	 * @return The id used to save the entity.
 	 */
 	protected Serializable saveWithRequestedId(
 			Object entity,
 			Serializable requestedId,
 			String entityName,
 			Object anything,
 			EventSource source) {
 		return performSave(
 				entity,
 				requestedId,
 				source.getEntityPersister( entityName, entity ),
 				false,
 				anything,
 				source,
 				true
 		);
 	}
 
 	/**
 	 * Prepares the save call using a newly generated id.
 	 *
 	 * @param entity The entity to be saved
 	 * @param entityName The entity-name for the entity to be saved
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of this save event.
 	 * @param requiresImmediateIdAccess does the event context require
 	 * access to the identifier immediately after execution of this method (if
 	 * not, post-insert style id generators may be postponed if we are outside
 	 * a transaction).
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable saveWithGeneratedId(
 			Object entity,
 			String entityName,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 		EntityPersister persister = source.getEntityPersister( entityName, entity );
 		Serializable generatedId = persister.getIdentifierGenerator().generate( source, entity );
 		if ( generatedId == null ) {
 			throw new IdentifierGenerationException( "null id generated for:" + entity.getClass() );
 		}
 		else if ( generatedId == IdentifierGeneratorHelper.SHORT_CIRCUIT_INDICATOR ) {
 			return source.getIdentifier( entity );
 		}
 		else if ( generatedId == IdentifierGeneratorHelper.POST_INSERT_INDICATOR ) {
 			return performSave( entity, null, persister, true, anything, source, requiresImmediateIdAccess );
 		}
 		else {
 			// TODO: define toString()s for generators
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Generated identifier: %s, using strategy: %s",
 						persister.getIdentifierType().toLoggableString( generatedId, source.getFactory() ),
 						persister.getIdentifierGenerator().getClass().getName() );
 			}
 
 			return performSave( entity, generatedId, persister, false, anything, source, true );
 		}
 	}
 
 	/**
 	 * Prepares the save call by checking the session caches for a pre-existing
 	 * entity and performing any lifecycle callbacks.
 	 *
 	 * @param entity The entity to be saved.
 	 * @param id The id by which to save the entity.
 	 * @param persister The entity's persister instance.
 	 * @param useIdentityColumn Is an identity column being used?
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session from which the event originated.
 	 * @param requiresImmediateIdAccess does the event context require
 	 * access to the identifier immediately after execution of this method (if
 	 * not, post-insert style id generators may be postponed if we are outside
 	 * a transaction).
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable performSave(
 			Object entity,
 			Serializable id,
 			EntityPersister persister,
 			boolean useIdentityColumn,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Saving {0}", MessageHelper.infoString( persister, id, source.getFactory() ) );
 		}
 
 		final EntityKey key;
 		if ( !useIdentityColumn ) {
 			key = source.generateEntityKey( id, persister );
 			Object old = source.getPersistenceContext().getEntity( key );
 			if ( old != null ) {
 				if ( source.getPersistenceContext().getEntry( old ).getStatus() == Status.DELETED ) {
 					source.forceFlush( source.getPersistenceContext().getEntry( old ) );
 				}
 				else {
 					throw new NonUniqueObjectException( id, persister.getEntityName() );
 				}
 			}
 			persister.setIdentifier( entity, id, source );
 		}
 		else {
 			key = null;
 		}
 
 		if ( invokeSaveLifecycle( entity, persister, source ) ) {
 			return id; //EARLY EXIT
 		}
 
 		return performSaveOrReplicate(
 				entity,
 				key,
 				persister,
 				useIdentityColumn,
 				anything,
 				source,
 				requiresImmediateIdAccess
 		);
 	}
 
 	protected boolean invokeSaveLifecycle(Object entity, EntityPersister persister, EventSource source) {
 		// Sub-insertions should occur before containing insertion so
 		// Try to do the callback now
 		if ( persister.implementsLifecycle() ) {
 			LOG.debug( "Calling onSave()" );
 			if ( ( ( Lifecycle ) entity ).onSave( source ) ) {
 				LOG.debug( "Insertion vetoed by onSave()" );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	/**
 	 * Performs all the actual work needed to save an entity (well to get the save moved to
 	 * the execution queue).
 	 *
 	 * @param entity The entity to be saved
 	 * @param key The id to be used for saving the entity (or null, in the case of identity columns)
 	 * @param persister The entity's persister instance.
 	 * @param useIdentityColumn Should an identity column be used for id generation?
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of the current event.
 	 * @param requiresImmediateIdAccess Is access to the identifier required immediately
 	 * after the completion of the save?  persist(), for example, does not require this...
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable performSaveOrReplicate(
 			Object entity,
 			EntityKey key,
 			EntityPersister persister,
 			boolean useIdentityColumn,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 
 		Serializable id = key == null ? null : key.getIdentifier();
 
 		boolean inTxn = source.getTransactionCoordinator().isTransactionInProgress();
 		boolean shouldDelayIdentityInserts = !inTxn && !requiresImmediateIdAccess;
 
 		// Put a placeholder in entries, so we don't recurse back and try to save() the
 		// same object again. QUESTION: should this be done before onSave() is called?
 		// likewise, should it be done before onUpdate()?
 		source.getPersistenceContext().addEntry(
 				entity,
 				Status.SAVING,
 				null,
 				null,
 				id,
 				null,
 				LockMode.WRITE,
 				useIdentityColumn,
 				persister,
 				false,
 				false
 		);
 
 		cascadeBeforeSave( source, persister, entity, anything );
 
 		Object[] values = persister.getPropertyValuesToInsert( entity, getMergeMap( anything ), source );
 		Type[] types = persister.getPropertyTypes();
 
 		boolean substitute = substituteValuesIfNecessary( entity, id, values, persister, source );
 
 		if ( persister.hasCollections() ) {
 			substitute = substitute || visitCollectionsBeforeSave( entity, id, values, types, source );
 		}
 
 		if ( substitute ) {
 			persister.setPropertyValues( entity, values );
 		}
 
 		TypeHelper.deepCopy(
 				values,
 				types,
 				persister.getPropertyUpdateability(),
 				values,
 				source
 		);
 
 		AbstractEntityInsertAction insert = addInsertAction(
 				values, id, entity, persister, useIdentityColumn, source, shouldDelayIdentityInserts
 		);
 
 		// postpone initializing id in case the insert has non-nullable transient dependencies
 		// that are not resolved until cascadeAfterSave() is executed
 		cascadeAfterSave( source, persister, entity, anything );
 		if ( useIdentityColumn && insert.isEarlyInsert() ) {
 			if ( ! EntityIdentityInsertAction.class.isInstance( insert ) ) {
 				throw new IllegalStateException(
 						"Insert should be using an identity column, but action is of unexpected type: " +
 								insert.getClass().getName() );
 			}
 			id = ( ( EntityIdentityInsertAction ) insert ).getGeneratedId();
 			
 			insert.handleNaturalIdPostSaveNotifications(id);
 		}
 
 		markInterceptorDirty( entity, persister, source );
 
 		return id;
 	}
 
 	private AbstractEntityInsertAction addInsertAction(
 			Object[] values,
 			Serializable id,
 			Object entity,
 			EntityPersister persister,
 			boolean useIdentityColumn,
 			EventSource source,
 			boolean shouldDelayIdentityInserts) {
 		if ( useIdentityColumn ) {
 			EntityIdentityInsertAction insert = new EntityIdentityInsertAction(
 					values, entity, persister, isVersionIncrementDisabled(), source, shouldDelayIdentityInserts
 			);
 			source.getActionQueue().addAction( insert );
 			return insert;
 		}
 		else {
 			Object version = Versioning.getVersion( values, persister );
 			EntityInsertAction insert = new EntityInsertAction(
 					id, values, entity, version, persister, isVersionIncrementDisabled(), source
 			);
 			source.getActionQueue().addAction( insert );
 			return insert;
 		}
 	}
 
 	private void markInterceptorDirty(Object entity, EntityPersister persister, EventSource source) {
 		if ( persister.getInstrumentationMetadata().isInstrumented() ) {
 			FieldInterceptor interceptor = persister.getInstrumentationMetadata().injectInterceptor(
 					entity,
 					persister.getEntityName(),
 					null,
 					source
 			);
 			interceptor.dirty();
 		}
 	}
 
 	protected Map getMergeMap(Object anything) {
 		return null;
 	}
 
 	/**
 	 * After the save, will te version number be incremented
 	 * if the instance is modified?
 	 *
 	 * @return True if the version will be incremented on an entity change after save;
 	 *         false otherwise.
 	 */
 	protected boolean isVersionIncrementDisabled() {
 		return false;
 	}
 
 	protected boolean visitCollectionsBeforeSave(Object entity, Serializable id, Object[] values, Type[] types, EventSource source) {
 		WrapVisitor visitor = new WrapVisitor( source );
 		// substitutes into values by side-effect
 		visitor.processEntityPropertyValues( values, types );
 		return visitor.isSubstitutionRequired();
 	}
 
 	/**
 	 * Perform any property value substitution that is necessary
 	 * (interceptor callback, version initialization...)
 	 *
 	 * @param entity The entity
 	 * @param id The entity identifier
 	 * @param values The snapshot entity state
 	 * @param persister The entity persister
 	 * @param source The originating session
 	 *
 	 * @return True if the snapshot state changed such that
 	 * reinjection of the values into the entity is required.
 	 */
 	protected boolean substituteValuesIfNecessary(
 			Object entity,
 			Serializable id,
 			Object[] values,
 			EntityPersister persister,
 			SessionImplementor source) {
 		boolean substitute = source.getInterceptor().onSave(
 				entity,
 				id,
 				values,
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 		);
 
 		//keep the existing version number in the case of replicate!
 		if ( persister.isVersioned() ) {
 			substitute = Versioning.seedVersion(
 					values,
 					persister.getVersionProperty(),
 					persister.getVersionType(),
 					source
 			) || substitute;
 		}
 		return substitute;
 	}
 
 	/**
 	 * Handles the calls needed to perform pre-save cascades for the given entity.
 	 *
 	 * @param source The session from whcih the save event originated.
 	 * @param persister The entity's persister instance.
 	 * @param entity The entity to be saved.
 	 * @param anything Generally cascade-specific data
 	 */
 	protected void cascadeBeforeSave(
 			EventSource source,
 			EntityPersister persister,
 			Object entity,
 			Object anything) {
 
 		// cascade-save to many-to-one BEFORE the parent is saved
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadeAction(), Cascade.BEFORE_INSERT_AFTER_DELETE, source )
 					.cascade( persister, entity, anything );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	/**
 	 * Handles to calls needed to perform post-save cascades.
 	 *
 	 * @param source The session from which the event originated.
 	 * @param persister The entity's persister instance.
 	 * @param entity The entity beng saved.
 	 * @param anything Generally cascade-specific data
 	 */
 	protected void cascadeAfterSave(
 			EventSource source,
 			EntityPersister persister,
 			Object entity,
 			Object anything) {
 
 		// cascade-save to collections AFTER the collection owner was saved
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadeAction(), Cascade.AFTER_INSERT_BEFORE_DELETE, source )
 					.cascade( persister, entity, anything );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	protected abstract CascadingAction getCascadeAction();
 
 	/**
 	 * Determine whether the entity is persistent, detached, or transient
 	 *
 	 * @param entity The entity to check
 	 * @param entityName The name of the entity
 	 * @param entry The entity's entry in the persistence context
 	 * @param source The originating session.
 	 *
 	 * @return The state.
 	 */
 	protected EntityState getEntityState(
 			Object entity,
 			String entityName,
 			EntityEntry entry, //pass this as an argument only to avoid double looking
 			SessionImplementor source) {
 
+		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( entry != null ) { // the object is persistent
 
 			//the entity is associated with the session, so check its status
 			if ( entry.getStatus() != Status.DELETED ) {
 				// do nothing for persistent instances
-				if ( LOG.isTraceEnabled() ) {
+				if ( traceEnabled ) {
 					LOG.tracev( "Persistent instance of: {0}", getLoggableName( entityName, entity ) );
 				}
 				return EntityState.PERSISTENT;
 			}
 			// ie. e.status==DELETED
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev( "Deleted instance of: {0}", getLoggableName( entityName, entity ) );
 			}
 			return EntityState.DELETED;
 		}
 		// the object is transient or detached
 
 		// the entity is not associated with the session, so
 		// try interceptor and unsaved-value
 
 		if ( ForeignKeys.isTransient( entityName, entity, getAssumedUnsaved(), source )) {
-			if ( LOG.isTraceEnabled() ) {
+			if (  traceEnabled ) {
 				LOG.tracev( "Transient instance of: {0}", getLoggableName( entityName, entity ) );
 			}
 			return EntityState.TRANSIENT;
 		}
-		if ( LOG.isTraceEnabled() ) {
+		if ( traceEnabled ) {
 			LOG.tracev( "Detached instance of: {0}", getLoggableName( entityName, entity ) );
 		}
 		return EntityState.DETACHED;
 	}
 
 	protected String getLoggableName(String entityName, Object entity) {
 		return entityName == null ? entity.getClass().getName() : entityName;
 	}
 
 	protected Boolean getAssumedUnsaved() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
index c40942ea64..cf99ea572e 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
@@ -1,666 +1,666 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 import java.util.Arrays;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.action.internal.DelayedPostInsertIdentifier;
 import org.hibernate.action.internal.EntityUpdateAction;
 import org.hibernate.engine.internal.Nullability;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.FlushEntityEvent;
 import org.hibernate.event.spi.FlushEntityEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 
 /**
  * An event that occurs for each entity instance at flush time
  *
  * @author Gavin King
  */
 public class DefaultFlushEntityEventListener implements FlushEntityEventListener {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultFlushEntityEventListener.class.getName());
 
 	/**
 	 * make sure user didn't mangle the id
 	 */
 	public void checkId(Object object, EntityPersister persister, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( id != null && id instanceof DelayedPostInsertIdentifier ) {
 			// this is a situation where the entity id is assigned by a post-insert generator
 			// and was saved outside the transaction forcing it to be delayed
 			return;
 		}
 
 		if ( persister.canExtractIdOutOfEntity() ) {
 
 			Serializable oid = persister.getIdentifier( object, session );
 			if (id==null) {
 				throw new AssertionFailure("null id in " + persister.getEntityName() + " entry (don't flush the Session after an exception occurs)");
 			}
 			if ( !persister.getIdentifierType().isEqual( id, oid, session.getFactory() ) ) {
 				throw new HibernateException(
 						"identifier of an instance of " +
 						persister.getEntityName() +
 						" was altered from " + id +
 						" to " + oid
 					);
 			}
 		}
 
 	}
 
 	private void checkNaturalId(
 			EntityPersister persister,
 	        EntityEntry entry,
 	        Object[] current,
 	        Object[] loaded,
 	        SessionImplementor session) {
 		if ( persister.hasNaturalIdentifier() && entry.getStatus() != Status.READ_ONLY ) {
 			if ( ! persister.getEntityMetamodel().hasImmutableNaturalId() ) {
 				// SHORT-CUT: if the natural id is mutable (!immutable), no need to do the below checks
 				// EARLY EXIT!!!
 				return;
 			}
 
 			final int[] naturalIdentifierPropertiesIndexes = persister.getNaturalIdentifierProperties();
 			final Type[] propertyTypes = persister.getPropertyTypes();
 			final boolean[] propertyUpdateability = persister.getPropertyUpdateability();
 
 			final Object[] snapshot = loaded == null
 					? session.getPersistenceContext().getNaturalIdSnapshot( entry.getId(), persister )
 					: session.getPersistenceContext().getNaturalIdHelper().extractNaturalIdValues( loaded, persister );
 
 			for ( int i=0; i<naturalIdentifierPropertiesIndexes.length; i++ ) {
 				final int naturalIdentifierPropertyIndex = naturalIdentifierPropertiesIndexes[i];
 				if ( propertyUpdateability[ naturalIdentifierPropertyIndex ] ) {
 					// if the given natural id property is updatable (mutable), there is nothing to check
 					continue;
 				}
 
 				final Type propertyType = propertyTypes[naturalIdentifierPropertyIndex];
 				if ( ! propertyType.isEqual( current[naturalIdentifierPropertyIndex], snapshot[i] ) ) {
 					throw new HibernateException(
 							String.format(
 									"An immutable natural identifier of entity %s was altered from %s to %s",
 									persister.getEntityName(),
 									propertyTypes[naturalIdentifierPropertyIndex].toLoggableString(
 											snapshot[i],
 											session.getFactory()
 									),
 									propertyTypes[naturalIdentifierPropertyIndex].toLoggableString(
 											current[naturalIdentifierPropertyIndex],
 											session.getFactory()
 									)
 							)
 					);
 			   }
 			}
 		}
 	}
 
 	/**
 	 * Flushes a single entity's state to the database, by scheduling
 	 * an update action, if necessary
 	 */
 	public void onFlushEntity(FlushEntityEvent event) throws HibernateException {
 		final Object entity = event.getEntity();
 		final EntityEntry entry = event.getEntityEntry();
 		final EventSource session = event.getSession();
 		final EntityPersister persister = entry.getPersister();
 		final Status status = entry.getStatus();
 		final Type[] types = persister.getPropertyTypes();
 
 		final boolean mightBeDirty = entry.requiresDirtyCheck(entity);
 
 		final Object[] values = getValues( entity, entry, mightBeDirty, session );
 
 		event.setPropertyValues(values);
 
 		//TODO: avoid this for non-new instances where mightBeDirty==false
 		boolean substitute = wrapCollections( session, persister, types, values);
 
 		if ( isUpdateNecessary( event, mightBeDirty ) ) {
 			substitute = scheduleUpdate( event ) || substitute;
 		}
 
 		if ( status != Status.DELETED ) {
 			// now update the object .. has to be outside the main if block above (because of collections)
 			if (substitute) persister.setPropertyValues( entity, values );
 
 			// Search for collections by reachability, updating their role.
 			// We don't want to touch collections reachable from a deleted object
 			if ( persister.hasCollections() ) {
 				new FlushVisitor(session, entity).processEntityPropertyValues(values, types);
 			}
 		}
 
 	}
 
 	private Object[] getValues(Object entity, EntityEntry entry, boolean mightBeDirty, SessionImplementor session) {
 		final Object[] loadedState = entry.getLoadedState();
 		final Status status = entry.getStatus();
 		final EntityPersister persister = entry.getPersister();
 
 		final Object[] values;
 		if ( status == Status.DELETED ) {
 			//grab its state saved at deletion
 			values = entry.getDeletedState();
 		}
 		else if ( !mightBeDirty && loadedState!=null ) {
 			values = loadedState;
 		}
 		else {
 			checkId( entity, persister, entry.getId(), session );
 
 			// grab its current state
 			values = persister.getPropertyValues( entity );
 
 			checkNaturalId( persister, entry, values, loadedState, session );
 		}
 		return values;
 	}
 
 	private boolean wrapCollections(
 			EventSource session,
 			EntityPersister persister,
 			Type[] types,
 			Object[] values
 	) {
 		if ( persister.hasCollections() ) {
 
 			// wrap up any new collections directly referenced by the object
 			// or its components
 
 			// NOTE: we need to do the wrap here even if its not "dirty",
 			// because collections need wrapping but changes to _them_
 			// don't dirty the container. Also, for versioned data, we
 			// need to wrap before calling searchForDirtyCollections
 
 			WrapVisitor visitor = new WrapVisitor(session);
 			// substitutes into values by side-effect
 			visitor.processEntityPropertyValues(values, types);
 			return visitor.isSubstitutionRequired();
 		}
 		else {
 			return false;
 		}
 	}
 
 	private boolean isUpdateNecessary(final FlushEntityEvent event, final boolean mightBeDirty) {
 		final Status status = event.getEntityEntry().getStatus();
 		if ( mightBeDirty || status==Status.DELETED ) {
 			// compare to cached state (ignoring collections unless versioned)
 			dirtyCheck(event);
 			if ( isUpdateNecessary(event) ) {
 				return true;
 			}
 			else {
 				if ( event.getEntityEntry().getPersister().getInstrumentationMetadata().isInstrumented() ) {
 					event.getEntityEntry()
 							.getPersister()
 							.getInstrumentationMetadata()
 							.extractInterceptor( event.getEntity() )
 							.clearDirty();
 				}
 				event.getSession()
 						.getFactory()
 						.getCustomEntityDirtinessStrategy()
 						.resetDirty( event.getEntity(), event.getEntityEntry().getPersister(), event.getSession() );
 				return false;
 			}
 		}
 		else {
 			return hasDirtyCollections( event, event.getEntityEntry().getPersister(), status );
 		}
 	}
 
 	private boolean scheduleUpdate(final FlushEntityEvent event) {
 
 		final EntityEntry entry = event.getEntityEntry();
 		final EventSource session = event.getSession();
 		final Object entity = event.getEntity();
 		final Status status = entry.getStatus();
 		final EntityPersister persister = entry.getPersister();
 		final Object[] values = event.getPropertyValues();
 
 		if ( LOG.isTraceEnabled() ) {
 			if ( status == Status.DELETED ) {
 				if ( !persister.isMutable() ) {
 					LOG.tracev( "Updating immutable, deleted entity: {0}",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() ) );
 				}
 				else if ( !entry.isModifiableEntity() )
 					LOG.tracev( "Updating non-modifiable, deleted entity: {0}",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() ) );
 				else
 					LOG.tracev( "Updating deleted entity: ",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() ) );
 			}
 			else
 				LOG.tracev( "Updating entity: {0}",
 						MessageHelper.infoString( persister, entry.getId(), session.getFactory() ) );
 		}
 
 		final boolean intercepted = !entry.isBeingReplicated() && handleInterception( event );
 
 		// increment the version number (if necessary)
 		final Object nextVersion = getNextVersion(event);
 
 		// if it was dirtied by a collection only
 		int[] dirtyProperties = event.getDirtyProperties();
 		if ( event.isDirtyCheckPossible() && dirtyProperties == null ) {
 			if ( ! intercepted && !event.hasDirtyCollection() ) {
 				throw new AssertionFailure( "dirty, but no dirty properties" );
 			}
 			dirtyProperties = ArrayHelper.EMPTY_INT_ARRAY;
 		}
 
 		// check nullability but do not doAfterTransactionCompletion command execute
 		// we'll use scheduled updates for that.
 		new Nullability(session).checkNullability( values, persister, true );
 
 		// schedule the update
 		// note that we intentionally do _not_ pass in currentPersistentState!
 		session.getActionQueue().addAction(
 				new EntityUpdateAction(
 						entry.getId(),
 						values,
 						dirtyProperties,
 						event.hasDirtyCollection(),
 						( status == Status.DELETED && ! entry.isModifiableEntity() ?
 								persister.getPropertyValues( entity ) :
 								entry.getLoadedState() ),
 						entry.getVersion(),
 						nextVersion,
 						entity,
 						entry.getRowId(),
 						persister,
 						session
 					)
 			);
 
 		return intercepted;
 	}
 
 	protected boolean handleInterception(FlushEntityEvent event) {
 		SessionImplementor session = event.getSession();
 		EntityEntry entry = event.getEntityEntry();
 		EntityPersister persister = entry.getPersister();
 		Object entity = event.getEntity();
 
 		//give the Interceptor a chance to modify property values
 		final Object[] values = event.getPropertyValues();
 		final boolean intercepted = invokeInterceptor( session, entity, entry, values, persister );
 
 		//now we might need to recalculate the dirtyProperties array
 		if ( intercepted && event.isDirtyCheckPossible() && !event.isDirtyCheckHandledByInterceptor() ) {
 			int[] dirtyProperties;
 			if ( event.hasDatabaseSnapshot() ) {
 				dirtyProperties = persister.findModified( event.getDatabaseSnapshot(), values, entity, session );
 			}
 			else {
 				dirtyProperties = persister.findDirty( values, entry.getLoadedState(), entity, session );
 			}
 			event.setDirtyProperties(dirtyProperties);
 		}
 
 		return intercepted;
 	}
 
 	protected boolean invokeInterceptor(
 			SessionImplementor session,
 			Object entity,
 			EntityEntry entry,
 			final Object[] values,
 			EntityPersister persister) {
 		return session.getInterceptor().onFlushDirty(
 				entity,
 				entry.getId(),
 				values,
 				entry.getLoadedState(),
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 		);
 	}
 
 	/**
 	 * Convience method to retreive an entities next version value
 	 */
 	private Object getNextVersion(FlushEntityEvent event) throws HibernateException {
 
 		EntityEntry entry = event.getEntityEntry();
 		EntityPersister persister = entry.getPersister();
 		if ( persister.isVersioned() ) {
 
 			Object[] values = event.getPropertyValues();
 
 			if ( entry.isBeingReplicated() ) {
 				return Versioning.getVersion(values, persister);
 			}
 			else {
 				int[] dirtyProperties = event.getDirtyProperties();
 
 				final boolean isVersionIncrementRequired = isVersionIncrementRequired(
 						event,
 						entry,
 						persister,
 						dirtyProperties
 					);
 
 				final Object nextVersion = isVersionIncrementRequired ?
 						Versioning.increment( entry.getVersion(), persister.getVersionType(), event.getSession() ) :
 						entry.getVersion(); //use the current version
 
 				Versioning.setVersion(values, nextVersion, persister);
 
 				return nextVersion;
 			}
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private boolean isVersionIncrementRequired(
 			FlushEntityEvent event,
 			EntityEntry entry,
 			EntityPersister persister,
 			int[] dirtyProperties
 	) {
 		final boolean isVersionIncrementRequired = entry.getStatus()!=Status.DELETED && (
 				dirtyProperties==null ||
 				Versioning.isVersionIncrementRequired(
 						dirtyProperties,
 						event.hasDirtyCollection(),
 						persister.getPropertyVersionability()
 				)
 			);
 		return isVersionIncrementRequired;
 	}
 
 	/**
 	 * Performs all necessary checking to determine if an entity needs an SQL update
 	 * to synchronize its state to the database. Modifies the event by side-effect!
 	 * Note: this method is quite slow, avoid calling if possible!
 	 */
 	protected final boolean isUpdateNecessary(FlushEntityEvent event) throws HibernateException {
 
 		EntityPersister persister = event.getEntityEntry().getPersister();
 		Status status = event.getEntityEntry().getStatus();
 
 		if ( !event.isDirtyCheckPossible() ) {
 			return true;
 		}
 		else {
 
 			int[] dirtyProperties = event.getDirtyProperties();
 			if ( dirtyProperties!=null && dirtyProperties.length!=0 ) {
 				return true; //TODO: suck into event class
 			}
 			else {
 				return hasDirtyCollections( event, persister, status );
 			}
 
 		}
 	}
 
 	private boolean hasDirtyCollections(FlushEntityEvent event, EntityPersister persister, Status status) {
 		if ( isCollectionDirtyCheckNecessary(persister, status ) ) {
 			DirtyCollectionSearchVisitor visitor = new DirtyCollectionSearchVisitor(
 					event.getSession(),
 					persister.getPropertyVersionability()
 				);
 			visitor.processEntityPropertyValues( event.getPropertyValues(), persister.getPropertyTypes() );
 			boolean hasDirtyCollections = visitor.wasDirtyCollectionFound();
 			event.setHasDirtyCollection(hasDirtyCollections);
 			return hasDirtyCollections;
 		}
 		else {
 			return false;
 		}
 	}
 
 	private boolean isCollectionDirtyCheckNecessary(EntityPersister persister, Status status) {
 		return ( status == Status.MANAGED || status == Status.READ_ONLY ) &&
 				persister.isVersioned() &&
 				persister.hasCollections();
 	}
 
 	/**
 	 * Perform a dirty check, and attach the results to the event
 	 */
 	protected void dirtyCheck(final FlushEntityEvent event) throws HibernateException {
 
 		final Object entity = event.getEntity();
 		final Object[] values = event.getPropertyValues();
 		final SessionImplementor session = event.getSession();
 		final EntityEntry entry = event.getEntityEntry();
 		final EntityPersister persister = entry.getPersister();
 		final Serializable id = entry.getId();
 		final Object[] loadedState = entry.getLoadedState();
 
 		int[] dirtyProperties = session.getInterceptor().findDirty(
 				entity,
 				id,
 				values,
 				loadedState,
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 		);
 
 		if ( dirtyProperties == null ) {
 			// see if the custom dirtiness strategy can tell us...
 			class DirtyCheckContextImpl implements CustomEntityDirtinessStrategy.DirtyCheckContext {
 				int[] found = null;
 				@Override
 				public void doDirtyChecking(CustomEntityDirtinessStrategy.AttributeChecker attributeChecker) {
 					found = new DirtyCheckAttributeInfoImpl( event ).visitAttributes( attributeChecker );
 					if ( found != null && found.length == 0 ) {
 						found = null;
 					}
 				}
 			}
 			DirtyCheckContextImpl context = new DirtyCheckContextImpl();
 			session.getFactory().getCustomEntityDirtinessStrategy().findDirty(
 					entity,
 					persister,
 					(Session) session,
 					context
 			);
 			dirtyProperties = context.found;
 		}
 
 		event.setDatabaseSnapshot(null);
 
 		final boolean interceptorHandledDirtyCheck;
 		boolean cannotDirtyCheck;
 
 		if ( dirtyProperties==null ) {
 			// Interceptor returned null, so do the dirtycheck ourself, if possible
 			interceptorHandledDirtyCheck = false;
 
 			cannotDirtyCheck = loadedState==null; // object loaded by update()
 			if ( !cannotDirtyCheck ) {
 				// dirty check against the usual snapshot of the entity
 				dirtyProperties = persister.findDirty( values, loadedState, entity, session );
 			}
 			else if ( entry.getStatus() == Status.DELETED && ! event.getEntityEntry().isModifiableEntity() ) {
 				// A non-modifiable (e.g., read-only or immutable) entity needs to be have
 				// references to transient entities set to null before being deleted. No other
 				// fields should be updated.
 				if ( values != entry.getDeletedState() ) {
 					throw new IllegalStateException(
 							"Entity has status Status.DELETED but values != entry.getDeletedState"
 					);
 				}
 				// Even if loadedState == null, we can dirty-check by comparing currentState and
 				// entry.getDeletedState() because the only fields to be updated are those that
 				// refer to transient entities that are being set to null.
 				// - currentState contains the entity's current property values.
 				// - entry.getDeletedState() contains the entity's current property values with
 				//   references to transient entities set to null.
 				// - dirtyProperties will only contain properties that refer to transient entities
 				final Object[] currentState = persister.getPropertyValues( event.getEntity() );
 				dirtyProperties = persister.findDirty( entry.getDeletedState(), currentState, entity, session );
 				cannotDirtyCheck = false;
 			}
 			else {
 				// dirty check against the database snapshot, if possible/necessary
 				final Object[] databaseSnapshot = getDatabaseSnapshot(session, persister, id);
 				if ( databaseSnapshot != null ) {
 					dirtyProperties = persister.findModified(databaseSnapshot, values, entity, session);
 					cannotDirtyCheck = false;
 					event.setDatabaseSnapshot(databaseSnapshot);
 				}
 			}
 		}
 		else {
 			// the Interceptor handled the dirty checking
 			cannotDirtyCheck = false;
 			interceptorHandledDirtyCheck = true;
 		}
 
 		logDirtyProperties( id, dirtyProperties, persister );
 
 		event.setDirtyProperties(dirtyProperties);
 		event.setDirtyCheckHandledByInterceptor(interceptorHandledDirtyCheck);
 		event.setDirtyCheckPossible(!cannotDirtyCheck);
 
 	}
 
 	private class DirtyCheckAttributeInfoImpl implements CustomEntityDirtinessStrategy.AttributeInformation {
 		private final FlushEntityEvent event;
 		private final EntityPersister persister;
 		private final int numberOfAttributes;
 		private int index = 0;
 
 		private DirtyCheckAttributeInfoImpl(FlushEntityEvent event) {
 			this.event = event;
 			this.persister = event.getEntityEntry().getPersister();
 			this.numberOfAttributes = persister.getPropertyNames().length;
 		}
 
 		@Override
 		public EntityPersister getContainingPersister() {
 			return persister;
 		}
 
 		@Override
 		public int getAttributeIndex() {
 			return index;
 		}
 
 		@Override
 		public String getName() {
 			return persister.getPropertyNames()[ index ];
 		}
 
 		@Override
 		public Type getType() {
 			return persister.getPropertyTypes()[ index ];
 		}
 
 		@Override
 		public Object getCurrentValue() {
 			return event.getPropertyValues()[ index ];
 		}
 
 		Object[] databaseSnapshot;
 
 		@Override
 		public Object getLoadedValue() {
 			if ( databaseSnapshot == null ) {
 				databaseSnapshot = getDatabaseSnapshot( event.getSession(), persister, event.getEntityEntry().getId() );
 			}
 			return databaseSnapshot[ index ];
 		}
 
 		public int[] visitAttributes(CustomEntityDirtinessStrategy.AttributeChecker attributeChecker) {
 			databaseSnapshot = null;
 			index = 0;
 
 			final int[] indexes = new int[ numberOfAttributes ];
 			int count = 0;
 			for ( ; index < numberOfAttributes; index++ ) {
 				if ( attributeChecker.isDirty( this ) ) {
 					indexes[ count++ ] = index;
 				}
 			}
 			return Arrays.copyOf( indexes, count );
 		}
 	}
 
 	private void logDirtyProperties(Serializable id, int[] dirtyProperties, EntityPersister persister) {
-		if ( LOG.isTraceEnabled() && dirtyProperties != null && dirtyProperties.length > 0 ) {
+		if ( dirtyProperties != null && dirtyProperties.length > 0 && LOG.isTraceEnabled() ) {
 			final String[] allPropertyNames = persister.getPropertyNames();
 			final String[] dirtyPropertyNames = new String[ dirtyProperties.length ];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				dirtyPropertyNames[i] = allPropertyNames[ dirtyProperties[i]];
 			}
 			LOG.tracev( "Found dirty properties [{0}] : {1}",
 					MessageHelper.infoString( persister.getEntityName(), id ),
 					dirtyPropertyNames );
 		}
 	}
 
 	private Object[] getDatabaseSnapshot(SessionImplementor session, EntityPersister persister, Serializable id) {
 		if ( persister.isSelectBeforeUpdateRequired() ) {
 			Object[] snapshot = session.getPersistenceContext()
 					.getDatabaseSnapshot(id, persister);
 			if (snapshot==null) {
 				//do we even really need this? the update will fail anyway....
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor()
 							.optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), id );
 			}
 			return snapshot;
 		}
 		// TODO: optimize away this lookup for entities w/o unsaved-value="undefined"
 		final EntityKey entityKey = session.generateEntityKey( id, persister );
 		return session.getPersistenceContext().getCachedDatabaseSnapshot( entityKey );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java
index 033682d5df..4ce8666ddf 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java
@@ -1,146 +1,151 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.entry.CollectionCacheEntry;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.InitializeCollectionEvent;
 import org.hibernate.event.spi.InitializeCollectionEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.jboss.logging.Logger;
 
 /**
  * @author Gavin King
  */
 public class DefaultInitializeCollectionEventListener implements InitializeCollectionEventListener {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, DefaultInitializeCollectionEventListener.class.getName() );
 
 	/**
 	 * called by a collection that wants to initialize itself
 	 */
 	public void onInitializeCollection(InitializeCollectionEvent event)
 	throws HibernateException {
 
 		PersistentCollection collection = event.getCollection();
 		SessionImplementor source = event.getSession();
 
 		CollectionEntry ce = source.getPersistenceContext().getCollectionEntry(collection);
 		if (ce==null) throw new HibernateException("collection was evicted");
 		if ( !collection.wasInitialized() ) {
-			if ( LOG.isTraceEnabled() ) {
+			final boolean traceEnabled = LOG.isTraceEnabled();
+			if ( traceEnabled ) {
 				LOG.tracev( "Initializing collection {0}",
 						MessageHelper.collectionInfoString( ce.getLoadedPersister(), collection, ce.getLoadedKey(), source ) );
+				LOG.trace( "Checking second-level cache" );
 			}
 
-			LOG.trace( "Checking second-level cache" );
 			final boolean foundInCache = initializeCollectionFromCache(
 					ce.getLoadedKey(),
 					ce.getLoadedPersister(),
 					collection,
 					source
 				);
 
-			if ( foundInCache ) {
+			if ( foundInCache && traceEnabled ) {
 				LOG.trace( "Collection initialized from cache" );
 			}
 			else {
-				LOG.trace( "Collection not cached" );
+				if ( traceEnabled ) {
+					LOG.trace( "Collection not cached" );
+				}
 				ce.getLoadedPersister().initialize( ce.getLoadedKey(), source );
-				LOG.trace( "Collection initialized" );
+				if ( traceEnabled ) {
+					LOG.trace( "Collection initialized" );
+				}
 
 				if ( source.getFactory().getStatistics().isStatisticsEnabled() ) {
 					source.getFactory().getStatisticsImplementor().fetchCollection(
 							ce.getLoadedPersister().getRole()
 						);
 				}
 			}
 		}
 	}
 
 	/**
 	 * Try to initialize a collection from the cache
 	 *
 	 * @param id The id of the collection of initialize
 	 * @param persister The collection persister
 	 * @param collection The collection to initialize
 	 * @param source The originating session
 	 * @return true if we were able to initialize the collection from the cache;
 	 * false otherwise.
 	 */
 	private boolean initializeCollectionFromCache(
 			Serializable id,
 			CollectionPersister persister,
 			PersistentCollection collection,
 			SessionImplementor source) {
 
 		if ( !source.getLoadQueryInfluencers().getEnabledFilters().isEmpty() && persister.isAffectedByEnabledFilters( source ) ) {
 			LOG.trace( "Disregarding cached version (if any) of collection due to enabled filters" );
 			return false;
 		}
 
 		final boolean useCache = persister.hasCache() &&
 				source.getCacheMode().isGetEnabled();
 
         if (!useCache) return false;
 
         final SessionFactoryImplementor factory = source.getFactory();
 
         final CacheKey ck = source.generateCacheKey( id, persister.getKeyType(), persister.getRole() );
         Object ce = persister.getCacheAccessStrategy().get(ck, source.getTimestamp());
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
             if (ce == null) {
                 factory.getStatisticsImplementor()
 						.secondLevelCacheMiss( persister.getCacheAccessStrategy().getRegion().getName() );
             }
 			else {
                 factory.getStatisticsImplementor()
 						.secondLevelCacheHit( persister.getCacheAccessStrategy().getRegion().getName() );
             }
 		}
 
         if ( ce == null ) {
 			return false;
 		}
 
 		CollectionCacheEntry cacheEntry = (CollectionCacheEntry)persister.getCacheEntryStructure().destructure(ce, factory);
 
 		final PersistenceContext persistenceContext = source.getPersistenceContext();
         cacheEntry.assemble(collection, persister, persistenceContext.getCollectionOwner(id, persister));
         persistenceContext.getCollectionEntry(collection).postInitialize(collection);
         // addInitializedCollection(collection, persister, id);
         return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultReplicateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultReplicateEventListener.java
index 7885ea6c4c..910faabf3c 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultReplicateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultReplicateEventListener.java
@@ -1,223 +1,224 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.ReplicationMode;
 import org.hibernate.TransientObjectException;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.ReplicateEvent;
 import org.hibernate.event.spi.ReplicateEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 
 /**
  * Defines the default replicate event listener used by Hibernate to replicate
  * entities in response to generated replicate events.
  *
  * @author Steve Ebersole
  */
 public class DefaultReplicateEventListener extends AbstractSaveEventListener implements ReplicateEventListener {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultReplicateEventListener.class.getName());
 
 	/**
 	 * Handle the given replicate event.
 	 *
 	 * @param event The replicate event to be handled.
 	 *
 	 * @throws TransientObjectException An invalid attempt to replicate a transient entity.
 	 */
 	public void onReplicate(ReplicateEvent event) {
 		final EventSource source = event.getSession();
 		if ( source.getPersistenceContext().reassociateIfUninitializedProxy( event.getObject() ) ) {
 			LOG.trace( "Uninitialized proxy passed to replicate()" );
 			return;
 		}
 
 		Object entity = source.getPersistenceContext().unproxyAndReassociate( event.getObject() );
 
 		if ( source.getPersistenceContext().isEntryFor( entity ) ) {
 			LOG.trace( "Ignoring persistent instance passed to replicate()" );
 			//hum ... should we cascade anyway? throw an exception? fine like it is?
 			return;
 		}
 
 		EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 
 		// get the id from the object
 		/*if ( persister.isUnsaved(entity, source) ) {
 			throw new TransientObjectException("transient instance passed to replicate()");
 		}*/
 		Serializable id = persister.getIdentifier( entity, source );
 		if ( id == null ) {
 			throw new TransientObjectException( "instance with null id passed to replicate()" );
 		}
 
 		final ReplicationMode replicationMode = event.getReplicationMode();
 
 		final Object oldVersion;
 		if ( replicationMode == ReplicationMode.EXCEPTION ) {
 			//always do an INSERT, and let it fail by constraint violation
 			oldVersion = null;
 		}
 		else {
 			//what is the version on the database?
 			oldVersion = persister.getCurrentVersion( id, source );
 		}
 
+		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( oldVersion != null ) {
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev( "Found existing row for {0}", MessageHelper.infoString( persister, id, source.getFactory() ) );
 			}
 
 			/// HHH-2378
 			final Object realOldVersion = persister.isVersioned() ? oldVersion : null;
 
 			boolean canReplicate = replicationMode.shouldOverwriteCurrentVersion(
 					entity,
 					realOldVersion,
 					persister.getVersion( entity ),
 					persister.getVersionType()
 			);
 
 			// if can replicate, will result in a SQL UPDATE
 			// else do nothing (don't even reassociate object!)
 			if ( canReplicate )
 				performReplication( entity, id, realOldVersion, persister, replicationMode, source );
-			else
+			else if ( traceEnabled )
 				LOG.trace( "No need to replicate" );
 
 			//TODO: would it be better to do a refresh from db?
 		}
 		else {
 			// no existing row - do an insert
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev( "No existing row, replicating new instance {0}",
 						MessageHelper.infoString( persister, id, source.getFactory() ) );
 			}
 
 			final boolean regenerate = persister.isIdentifierAssignedByInsert(); // prefer re-generation of identity!
 			final EntityKey key = regenerate ? null : source.generateEntityKey( id, persister );
 
 			performSaveOrReplicate(
 					entity,
 					key,
 					persister,
 					regenerate,
 					replicationMode,
 					source,
 					true
 			);
 
 		}
 	}
 
 	@Override
     protected boolean visitCollectionsBeforeSave(Object entity, Serializable id, Object[] values, Type[] types, EventSource source) {
 		//TODO: we use two visitors here, inefficient!
 		OnReplicateVisitor visitor = new OnReplicateVisitor( source, id, entity, false );
 		visitor.processEntityPropertyValues( values, types );
 		return super.visitCollectionsBeforeSave( entity, id, values, types, source );
 	}
 
 	@Override
     protected boolean substituteValuesIfNecessary(
 			Object entity,
 			Serializable id,
 			Object[] values,
 			EntityPersister persister,
 			SessionImplementor source) {
 		return false;
 	}
 
 	@Override
     protected boolean isVersionIncrementDisabled() {
 		return true;
 	}
 
 	private void performReplication(
 			Object entity,
 			Serializable id,
 			Object version,
 			EntityPersister persister,
 			ReplicationMode replicationMode,
 			EventSource source) throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Replicating changes to {0}", MessageHelper.infoString( persister, id, source.getFactory() ) );
 		}
 
 		new OnReplicateVisitor( source, id, entity, true ).process( entity, persister );
 
 		source.getPersistenceContext().addEntity(
 				entity,
 				( persister.isMutable() ? Status.MANAGED : Status.READ_ONLY ),
 				null,
 				source.generateEntityKey( id, persister ),
 				version,
 				LockMode.NONE,
 				true,
 				persister,
 				true,
 				false
 		);
 
 		cascadeAfterReplicate( entity, persister, replicationMode, source );
 	}
 
 	private void cascadeAfterReplicate(
 			Object entity,
 			EntityPersister persister,
 			ReplicationMode replicationMode,
 			EventSource source) {
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( CascadingActions.REPLICATE, Cascade.AFTER_UPDATE, source )
 					.cascade( persister, entity, replicationMode );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	@Override
     protected CascadingAction getCascadeAction() {
 		return CascadingActions.REPLICATE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java
index 8fe131bc3d..7948e31c4a 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java
@@ -1,374 +1,377 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.SaveOrUpdateEvent;
 import org.hibernate.event.spi.SaveOrUpdateEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 
 /**
  * Defines the default listener used by Hibernate for handling save-update
  * events.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public class DefaultSaveOrUpdateEventListener extends AbstractSaveEventListener implements SaveOrUpdateEventListener {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, DefaultSaveOrUpdateEventListener.class.getName() );
 
 	/**
 	 * Handle the given update event.
 	 *
 	 * @param event The update event to be handled.
 	 */
 	public void onSaveOrUpdate(SaveOrUpdateEvent event) {
 		final SessionImplementor source = event.getSession();
 		final Object object = event.getObject();
 		final Serializable requestedId = event.getRequestedId();
 
 		if ( requestedId != null ) {
 			//assign the requested id to the proxy, *before*
 			//reassociating the proxy
 			if ( object instanceof HibernateProxy ) {
 				( ( HibernateProxy ) object ).getHibernateLazyInitializer().setIdentifier( requestedId );
 			}
 		}
 
 		// For an uninitialized proxy, noop, don't even need to return an id, since it is never a save()
 		if ( reassociateIfUninitializedProxy( object, source ) ) {
 			LOG.trace( "Reassociated uninitialized proxy" );
 		}
 		else {
 			//initialize properties of the event:
 			final Object entity = source.getPersistenceContext().unproxyAndReassociate( object );
 			event.setEntity( entity );
 			event.setEntry( source.getPersistenceContext().getEntry( entity ) );
 			//return the id in the event object
 			event.setResultId( performSaveOrUpdate( event ) );
 		}
 
 	}
 
 	protected boolean reassociateIfUninitializedProxy(Object object, SessionImplementor source) {
 		return source.getPersistenceContext().reassociateIfUninitializedProxy( object );
 	}
 
 	protected Serializable performSaveOrUpdate(SaveOrUpdateEvent event) {
 		EntityState entityState = getEntityState(
 				event.getEntity(),
 				event.getEntityName(),
 				event.getEntry(),
 				event.getSession()
 		);
 
 		switch ( entityState ) {
 			case DETACHED:
 				entityIsDetached( event );
 				return null;
 			case PERSISTENT:
 				return entityIsPersistent( event );
 			default: //TRANSIENT or DELETED
 				return entityIsTransient( event );
 		}
 	}
 
 	protected Serializable entityIsPersistent(SaveOrUpdateEvent event) throws HibernateException {
-		LOG.trace( "Ignoring persistent instance" );
-
+		final boolean traceEnabled = LOG.isTraceEnabled();
+		if ( traceEnabled ) {
+			LOG.trace( "Ignoring persistent instance" );
+		}
 		EntityEntry entityEntry = event.getEntry();
 		if ( entityEntry == null ) {
 			throw new AssertionFailure( "entity was transient or detached" );
 		}
 		else {
 
 			if ( entityEntry.getStatus() == Status.DELETED ) {
 				throw new AssertionFailure( "entity was deleted" );
 			}
 
 			final SessionFactoryImplementor factory = event.getSession().getFactory();
 
 			Serializable requestedId = event.getRequestedId();
 
 			Serializable savedId;
 			if ( requestedId == null ) {
 				savedId = entityEntry.getId();
 			}
 			else {
 
 				final boolean isEqual = !entityEntry.getPersister().getIdentifierType()
 						.isEqual( requestedId, entityEntry.getId(), factory );
 
 				if ( isEqual ) {
 					throw new PersistentObjectException(
 							"object passed to save() was already persistent: " +
 									MessageHelper.infoString( entityEntry.getPersister(), requestedId, factory )
 					);
 				}
 
 				savedId = requestedId;
 
 			}
 
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev( "Object already associated with session: {0}", MessageHelper.infoString( entityEntry.getPersister(), savedId, factory ) );
 			}
 
 			return savedId;
 
 		}
 	}
 
 	/**
 	 * The given save-update event named a transient entity.
 	 * <p/>
 	 * Here, we will perform the save processing.
 	 *
 	 * @param event The save event to be handled.
 	 *
 	 * @return The entity's identifier after saving.
 	 */
 	protected Serializable entityIsTransient(SaveOrUpdateEvent event) {
 
 		LOG.trace( "Saving transient instance" );
 
 		final EventSource source = event.getSession();
 
 		EntityEntry entityEntry = event.getEntry();
 		if ( entityEntry != null ) {
 			if ( entityEntry.getStatus() == Status.DELETED ) {
 				source.forceFlush( entityEntry );
 			}
 			else {
 				throw new AssertionFailure( "entity was persistent" );
 			}
 		}
 
 		Serializable id = saveWithGeneratedOrRequestedId( event );
 
 		source.getPersistenceContext().reassociateProxy( event.getObject(), id );
 
 		return id;
 	}
 
 	/**
 	 * Save the transient instance, assigning the right identifier
 	 *
 	 * @param event The initiating event.
 	 *
 	 * @return The entity's identifier value after saving.
 	 */
 	protected Serializable saveWithGeneratedOrRequestedId(SaveOrUpdateEvent event) {
 		return saveWithGeneratedId(
 				event.getEntity(),
 				event.getEntityName(),
 				null,
 				event.getSession(),
 				true
 		);
 	}
 
 	/**
 	 * The given save-update event named a detached entity.
 	 * <p/>
 	 * Here, we will perform the update processing.
 	 *
 	 * @param event The update event to be handled.
 	 */
 	protected void entityIsDetached(SaveOrUpdateEvent event) {
 
 		LOG.trace( "Updating detached instance" );
 
 		if ( event.getSession().getPersistenceContext().isEntryFor( event.getEntity() ) ) {
 			//TODO: assertion only, could be optimized away
 			throw new AssertionFailure( "entity was persistent" );
 		}
 
 		Object entity = event.getEntity();
 
 		EntityPersister persister = event.getSession().getEntityPersister( event.getEntityName(), entity );
 
 		event.setRequestedId(
 				getUpdateId(
 						entity, persister, event.getRequestedId(), event.getSession()
 				)
 		);
 
 		performUpdate( event, entity, persister );
 
 	}
 
 	/**
 	 * Determine the id to use for updating.
 	 *
 	 * @param entity The entity.
 	 * @param persister The entity persister
 	 * @param requestedId The requested identifier
 	 * @param session The session
 	 *
 	 * @return The id.
 	 *
 	 * @throws TransientObjectException If the entity is considered transient.
 	 */
 	protected Serializable getUpdateId(
 			Object entity,
 			EntityPersister persister,
 			Serializable requestedId,
 			SessionImplementor session) {
 		// use the id assigned to the instance
 		Serializable id = persister.getIdentifier( entity, session );
 		if ( id == null ) {
 			// assume this is a newly instantiated transient object
 			// which should be saved rather than updated
 			throw new TransientObjectException(
 					"The given object has a null identifier: " +
 							persister.getEntityName()
 			);
 		}
 		else {
 			return id;
 		}
 
 	}
 
 	protected void performUpdate(
 			SaveOrUpdateEvent event,
 			Object entity,
 			EntityPersister persister) throws HibernateException {
 
-		if ( !persister.isMutable() ) {
+        final boolean traceEnabled = LOG.isTraceEnabled();
+		if ( traceEnabled && !persister.isMutable() ) {
 			LOG.trace( "Immutable instance passed to performUpdate()" );
 		}
 
-		if ( LOG.isTraceEnabled() ) {
+		if ( traceEnabled ) {
 			LOG.tracev( "Updating {0}",
 					MessageHelper.infoString( persister, event.getRequestedId(), event.getSession().getFactory() ) );
 		}
 
 		final EventSource source = event.getSession();
 		final EntityKey key = source.generateEntityKey( event.getRequestedId(), persister );
 
 		source.getPersistenceContext().checkUniqueness(key, entity);
 
 		if (invokeUpdateLifecycle(entity, persister, source)) {
             reassociate(event, event.getObject(), event.getRequestedId(), persister);
             return;
         }
 
 		// this is a transient object with existing persistent state not loaded by the session
 
 		new OnUpdateVisitor(source, event.getRequestedId(), entity).process(entity, persister);
 
 		// TODO: put this stuff back in to read snapshot from
         // the second-level cache (needs some extra work)
         /*Object[] cachedState = null;
 
         if ( persister.hasCache() ) {
         	CacheEntry entry = (CacheEntry) persister.getCache()
         			.get( event.getRequestedId(), source.getTimestamp() );
             cachedState = entry==null ?
             		null :
             		entry.getState(); //TODO: half-assemble this stuff
         }*/
 
 		source.getPersistenceContext().addEntity(
 				entity,
 				(persister.isMutable() ? Status.MANAGED : Status.READ_ONLY),
 				null, // cachedState,
 				key,
 				persister.getVersion( entity ),
 				LockMode.NONE,
 				true,
 				persister,
 				false,
 				true // assume true, since we don't really know, and it doesn't matter
 				);
 
 		persister.afterReassociate(entity, source);
 
-		if ( LOG.isTraceEnabled() ) {
+		if ( traceEnabled ) {
 			LOG.tracev( "Updating {0}", MessageHelper.infoString( persister, event.getRequestedId(), source.getFactory() ) );
 		}
 
 		cascadeOnUpdate( event, persister, entity );
 	}
 
 	protected boolean invokeUpdateLifecycle(Object entity, EntityPersister persister, EventSource source) {
 		if ( persister.implementsLifecycle() ) {
 			LOG.debug( "Calling onUpdate()" );
 			if ( ( (Lifecycle) entity ).onUpdate( source ) ) {
 				LOG.debug( "Update vetoed by onUpdate()" );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	/**
 	 * Handles the calls needed to perform cascades as part of an update request
 	 * for the given entity.
 	 *
 	 * @param event The event currently being processed.
 	 * @param persister The defined persister for the entity being updated.
 	 * @param entity The entity being updated.
 	 */
 	private void cascadeOnUpdate(SaveOrUpdateEvent event, EntityPersister persister, Object entity) {
 		EventSource source = event.getSession();
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( CascadingActions.SAVE_UPDATE, Cascade.AFTER_UPDATE, source )
 					.cascade( persister, entity );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	@Override
     protected CascadingAction getCascadeAction() {
 		return CascadingActions.SAVE_UPDATE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElement.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElement.java
index 177eb0c189..ceb1b87ab8 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElement.java
@@ -1,696 +1,696 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
  *
  */
 package org.hibernate.hql.internal.ast.tree;
 
 import java.util.ArrayList;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.hql.internal.CollectionProperties;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.TypeDiscriminatorMetadata;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.spi.QueryTranslator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.DiscriminatorMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Represents a single mapped class mentioned in an HQL FROM clause.  Each
  * class reference will have the following symbols:
  * <ul>
  * <li>A class name - This is the name of the Java class that is mapped by Hibernate.</li>
  * <li>[optional] an HQL alias for the mapped class.</li>
  * <li>A table name - The name of the table that is mapped to the Java class.</li>
  * <li>A table alias - The alias for the table that will be used in the resulting SQL.</li>
  * </ul>
  *
  * @author josh
  */
 public class FromElement extends HqlSqlWalkerNode implements DisplayableNode, ParameterContainer {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, FromElement.class.getName());
 
 	private String className;
 	private String classAlias;
 	private String tableAlias;
 	private String collectionTableAlias;
 	private FromClause fromClause;
 	private boolean includeSubclasses = true;
 	private boolean collectionJoin = false;
 	private FromElement origin;
 	private String[] columns;
 	private String role;
 	private boolean fetch;
 	private boolean isAllPropertyFetch;
 	private boolean filter = false;
 	private int sequence = -1;
 	private boolean useFromFragment = false;
 	private boolean initialized = false;
 	private FromElementType elementType;
 	private boolean useWhereFragment = true;
 	private List destinations = new LinkedList();
 	private boolean manyToMany = false;
 	private String withClauseFragment = null;
 	private String withClauseJoinAlias;
 	private boolean dereferencedBySuperclassProperty;
 	private boolean dereferencedBySubclassProperty;
 
 	public FromElement() {
 	}
 
 	/**
 	 * Constructor form used to initialize {@link ComponentJoin}
 	 *
 	 * @param fromClause The FROM clause to which this element belongs
 	 * @param origin The origin (LHS) of this element
 	 * @param alias The alias applied to this element
 	 */
 	protected FromElement(
 			FromClause fromClause,
 			FromElement origin,
 			String alias) {
 		this.fromClause = fromClause;
 		this.origin = origin;
 		this.classAlias = alias;
 		this.tableAlias = origin.getTableAlias();
 		super.initialize( fromClause.getWalker() );
 	}
 
 	protected void initializeComponentJoin(FromElementType elementType) {
 		this.elementType = elementType;
 		fromClause.registerFromElement( this );
 		initialized = true;
 	}
 
 	public String getCollectionSuffix() {
 		return elementType.getCollectionSuffix();
 	}
 
 	public void setCollectionSuffix(String suffix) {
 		elementType.setCollectionSuffix(suffix);
 	}
 
 	public void initializeCollection(FromClause fromClause, String classAlias, String tableAlias) {
 		doInitialize( fromClause, tableAlias, null, classAlias, null, null );
 		initialized = true;
 	}
 
 	public void initializeEntity(
 	        FromClause fromClause,
 	        String className,
 	        EntityPersister persister,
 	        EntityType type,
 	        String classAlias,
 	        String tableAlias) {
 		doInitialize( fromClause, tableAlias, className, classAlias, persister, type );
 		this.sequence = fromClause.nextFromElementCounter();
 		initialized = true;
 	}
 
 	private void doInitialize(FromClause fromClause, String tableAlias, String className, String classAlias,
 							  EntityPersister persister, EntityType type) {
 		if ( initialized ) {
 			throw new IllegalStateException( "Already initialized!!" );
 		}
 		this.fromClause = fromClause;
 		this.tableAlias = tableAlias;
 		this.className = className;
 		this.classAlias = classAlias;
 		this.elementType = new FromElementType( this, persister, type );
 		// Register the FromElement with the FROM clause, now that we have the names and aliases.
 		fromClause.registerFromElement( this );
 		LOG.debugf( "%s : %s (%s) -> %s", fromClause, className, classAlias == null ? "<no alias>" : classAlias, tableAlias );
 	}
 
 	public EntityPersister getEntityPersister() {
 		return elementType.getEntityPersister();
 	}
 
 	@Override
     public Type getDataType() {
 		return elementType.getDataType();
 	}
 
 	public Type getSelectType() {
 		return elementType.getSelectType();
 	}
 
 	public Queryable getQueryable() {
 		return elementType.getQueryable();
 	}
 
 	public String getClassName() {
 		return className;
 	}
 
 	public String getClassAlias() {
 		return classAlias;
 		//return classAlias == null ? className : classAlias;
 	}
 
 	private String getTableName() {
 		Queryable queryable = getQueryable();
 		return ( queryable != null ) ? queryable.getTableName() : "{none}";
 	}
 
 	public String getTableAlias() {
 		return tableAlias;
 	}
 
 	/**
 	 * Render the identifier select, but in a 'scalar' context (i.e. generate the column alias).
 	 *
 	 * @param i the sequence of the returned type
 	 * @return the identifier select with the column alias.
 	 */
 	String renderScalarIdentifierSelect(int i) {
 		return elementType.renderScalarIdentifierSelect( i );
 	}
 
 	void checkInitialized() {
 		if ( !initialized ) {
 			throw new IllegalStateException( "FromElement has not been initialized!" );
 		}
 	}
 
 	/**
 	 * Returns the identifier select SQL fragment.
 	 *
 	 * @param size The total number of returned types.
 	 * @param k    The sequence of the current returned type.
 	 * @return the identifier select SQL fragment.
 	 */
 	String renderIdentifierSelect(int size, int k) {
 		return elementType.renderIdentifierSelect( size, k );
 	}
 
 	/**
 	 * Returns the property select SQL fragment.
 	 *
 	 * @param size The total number of returned types.
 	 * @param k    The sequence of the current returned type.
 	 * @return the property select SQL fragment.
 	 */
 	String renderPropertySelect(int size, int k) {
 		return elementType.renderPropertySelect( size, k, isAllPropertyFetch );
 	}
 
 	String renderCollectionSelectFragment(int size, int k) {
 		return elementType.renderCollectionSelectFragment( size, k );
 	}
 
 	String renderValueCollectionSelectFragment(int size, int k) {
 		return elementType.renderValueCollectionSelectFragment( size, k );
 	}
 
 	public FromClause getFromClause() {
 		return fromClause;
 	}
 
 	/**
 	 * Returns true if this FromElement was implied by a path, or false if this FROM element is explicitly declared in
 	 * the FROM clause.
 	 *
 	 * @return true if this FromElement was implied by a path, or false if this FROM element is explicitly declared
 	 */
 	public boolean isImplied() {
 		return false;	// This is an explicit FROM element.
 	}
 
 	/**
 	 * Returns additional display text for the AST node.
 	 *
 	 * @return String - The additional display text.
 	 */
 	public String getDisplayText() {
 		StringBuilder buf = new StringBuilder();
 		buf.append( "FromElement{" );
 		appendDisplayText( buf );
 		buf.append( "}" );
 		return buf.toString();
 	}
 
 	protected void appendDisplayText(StringBuilder buf) {
 		buf.append( isImplied() ? (
 				isImpliedInFromClause() ? "implied in FROM clause" : "implied" )
 				: "explicit" );
 		buf.append( "," ).append( isCollectionJoin() ? "collection join" : "not a collection join" );
 		buf.append( "," ).append( fetch ? "fetch join" : "not a fetch join" );
 		buf.append( "," ).append( isAllPropertyFetch ? "fetch all properties" : "fetch non-lazy properties" );
 		buf.append( ",classAlias=" ).append( getClassAlias() );
 		buf.append( ",role=" ).append( role );
 		buf.append( ",tableName=" ).append( getTableName() );
 		buf.append( ",tableAlias=" ).append( getTableAlias() );
 		FromElement origin = getRealOrigin();
 		buf.append( ",origin=" ).append( origin == null ? "null" : origin.getText() );
 		buf.append( ",columns={" );
 		if ( columns != null ) {
 			for ( int i = 0; i < columns.length; i++ ) {
 				buf.append( columns[i] );
 				if ( i < columns.length ) {
 					buf.append( " " );
 				}
 			}
 		}
 		buf.append( ",className=" ).append( className );
 		buf.append( "}" );
 	}
 
 	@Override
     public int hashCode() {
 		return super.hashCode();
 	}
 
 	@Override
     public boolean equals(Object obj) {
 		return super.equals( obj );
 	}
 
 
 	public void setJoinSequence(JoinSequence joinSequence) {
 		elementType.setJoinSequence( joinSequence );
 	}
 
 	public JoinSequence getJoinSequence() {
 		return elementType.getJoinSequence();
 	}
 
 	public void setIncludeSubclasses(boolean includeSubclasses) {
-		if ( LOG.isTraceEnabled() && isDereferencedBySuperclassOrSubclassProperty() && !includeSubclasses )
+		if ( !includeSubclasses && isDereferencedBySuperclassOrSubclassProperty() && LOG.isTraceEnabled() )
 			LOG.trace( "Attempt to disable subclass-inclusions : ", new Exception( "Stack-trace source" ) );
 		this.includeSubclasses = includeSubclasses;
 	}
 
 	public boolean isIncludeSubclasses() {
 		return includeSubclasses;
 	}
 
 	public boolean isDereferencedBySuperclassOrSubclassProperty() {
 		return dereferencedBySubclassProperty || dereferencedBySuperclassProperty;
 	}
 
 	public String getIdentityColumn() {
 		final String[] cols = getIdentityColumns();
 		if ( cols.length == 1 ) {
 			return cols[0];
 		}
 		else {
 			return "(" + StringHelper.join( ", ", cols ) + ")";
 		}
 	}
 
 	public String[] getIdentityColumns() {
 		checkInitialized();
 		final String table = getTableAlias();
 		if ( table == null ) {
 			throw new IllegalStateException( "No table alias for node " + this );
 		}
 
 		final String propertyName;
 		if ( getEntityPersister() != null && getEntityPersister().getEntityMetamodel() != null
 				&& getEntityPersister().getEntityMetamodel().hasNonIdentifierPropertyNamedId() ) {
 			propertyName = getEntityPersister().getIdentifierPropertyName();
 		}
 		else {
 			propertyName = EntityPersister.ENTITY_ID;
 		}
 
 		if ( getWalker().getStatementType() == HqlSqlTokenTypes.SELECT ) {
 			return getPropertyMapping( propertyName ).toColumns( table, propertyName );
 		}
 		else {
 			return getPropertyMapping( propertyName ).toColumns( propertyName );
 		}
 	}
 
 	public void setCollectionJoin(boolean collectionJoin) {
 		this.collectionJoin = collectionJoin;
 	}
 
 	public boolean isCollectionJoin() {
 		return collectionJoin;
 	}
 
 	public void setRole(String role) {
 		this.role = role;
 	}
 
 	public void setQueryableCollection(QueryableCollection queryableCollection) {
 		elementType.setQueryableCollection( queryableCollection );
 	}
 
 	public QueryableCollection getQueryableCollection() {
 		return elementType.getQueryableCollection();
 	}
 
 	public void setColumns(String[] columns) {
 		this.columns = columns;
 	}
 
 	public void setOrigin(FromElement origin, boolean manyToMany) {
 		this.origin = origin;
 		this.manyToMany = manyToMany;
 		origin.addDestination( this );
 		if ( origin.getFromClause() == this.getFromClause() ) {
 			// TODO: Figure out a better way to get the FROM elements in a proper tree structure.
 			// If this is not the destination of a many-to-many, add it as a child of the origin.
 			if ( manyToMany ) {
 				ASTUtil.appendSibling( origin, this );
 			}
 			else {
 				if ( !getWalker().isInFrom() && !getWalker().isInSelect() ) {
 					getFromClause().addChild( this );
 				}
 				else {
 					origin.addChild( this );
 				}
 			}
 		}
 		else if ( !getWalker().isInFrom() ) {
 			// HHH-276 : implied joins in a subselect where clause - The destination needs to be added
 			// to the destination's from clause.
 			getFromClause().addChild( this );	// Not sure if this is will fix everything, but it works.
 		}
 		else {
 			// Otherwise, the destination node was implied by the FROM clause and the FROM clause processor
 			// will automatically add it in the right place.
 		}
 	}
 
 	public boolean isManyToMany() {
 		return manyToMany;
 	}
 
 	private void addDestination(FromElement fromElement) {
 		destinations.add( fromElement );
 	}
 
 	public List getDestinations() {
 		return destinations;
 	}
 
 	public FromElement getOrigin() {
 		return origin;
 	}
 
 	public FromElement getRealOrigin() {
 		if ( origin == null ) {
 			return null;
 		}
 		if ( origin.getText() == null || "".equals( origin.getText() ) ) {
 			return origin.getRealOrigin();
 		}
 		return origin;
 	}
 
 	public static final String DISCRIMINATOR_PROPERTY_NAME = "class";
 	private TypeDiscriminatorMetadata typeDiscriminatorMetadata;
 
 	private static class TypeDiscriminatorMetadataImpl implements TypeDiscriminatorMetadata {
 		private final DiscriminatorMetadata persisterDiscriminatorMetadata;
 		private final String alias;
 
 		private TypeDiscriminatorMetadataImpl(
 				DiscriminatorMetadata persisterDiscriminatorMetadata,
 				String alias) {
 			this.persisterDiscriminatorMetadata = persisterDiscriminatorMetadata;
 			this.alias = alias;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public String getSqlFragment() {
 			return persisterDiscriminatorMetadata.getSqlFragment( alias );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Type getResolutionType() {
 			return persisterDiscriminatorMetadata.getResolutionType();
 		}
 	}
 
 	public TypeDiscriminatorMetadata getTypeDiscriminatorMetadata() {
 		if ( typeDiscriminatorMetadata == null ) {
 			typeDiscriminatorMetadata = buildTypeDiscriminatorMetadata();
 		}
 		return typeDiscriminatorMetadata;
 	}
 
 	private TypeDiscriminatorMetadata buildTypeDiscriminatorMetadata() {
 		final String aliasToUse = getTableAlias();
 		Queryable queryable = getQueryable();
 		if ( queryable == null ) {
 			QueryableCollection collection = getQueryableCollection();
 			if ( ! collection.getElementType().isEntityType() ) {
 				throw new QueryException( "type discrimination cannot be applied to value collection [" + collection.getRole() + "]" );
 			}
 			queryable = (Queryable) collection.getElementPersister();
 		}
 
 		handlePropertyBeingDereferenced( getDataType(), DISCRIMINATOR_PROPERTY_NAME );
 
 		return new TypeDiscriminatorMetadataImpl( queryable.getTypeDiscriminatorMetadata(), aliasToUse );
 	}
 
 	public Type getPropertyType(String propertyName, String propertyPath) {
 		return elementType.getPropertyType( propertyName, propertyPath );
 	}
 
 	public String[] toColumns(String tableAlias, String path, boolean inSelect) {
 		return elementType.toColumns( tableAlias, path, inSelect );
 	}
 
 	public String[] toColumns(String tableAlias, String path, boolean inSelect, boolean forceAlias) {
 		return elementType.toColumns( tableAlias, path, inSelect, forceAlias );
 	}
 
 	public PropertyMapping getPropertyMapping(String propertyName) {
 		return elementType.getPropertyMapping( propertyName );
 	}
 
 	public void setFetch(boolean fetch) {
 		this.fetch = fetch;
 		// Fetch can't be used with scroll() or iterate().
 		if ( fetch && getWalker().isShallowQuery() ) {
 			throw new QueryException( QueryTranslator.ERROR_CANNOT_FETCH_WITH_ITERATE );
 		}
 	}
 
 	public boolean isFetch() {
 		return fetch;
 	}
 
 	public int getSequence() {
 		return sequence;
 	}
 
 	public void setFilter(boolean b) {
 		filter = b;
 	}
 
 	public boolean isFilter() {
 		return filter;
 	}
 
 	public boolean useFromFragment() {
 		checkInitialized();
 		// If it's not implied or it is implied and it's a many to many join where the target wasn't found.
 		return !isImplied() || this.useFromFragment;
 	}
 
 	public void setUseFromFragment(boolean useFromFragment) {
 		this.useFromFragment = useFromFragment;
 	}
 
 	public boolean useWhereFragment() {
 		return useWhereFragment;
 	}
 
 	public void setUseWhereFragment(boolean b) {
 		useWhereFragment = b;
 	}
 
 
 	public void setCollectionTableAlias(String collectionTableAlias) {
 		this.collectionTableAlias = collectionTableAlias;
 	}
 
 	public String getCollectionTableAlias() {
 		return collectionTableAlias;
 	}
 
 	public boolean isCollectionOfValuesOrComponents() {
 		return elementType.isCollectionOfValuesOrComponents();
 	}
 
 	public boolean isEntity() {
 		return elementType.isEntity();
 	}
 
 	public void setImpliedInFromClause(boolean flag) {
 		throw new UnsupportedOperationException( "Explicit FROM elements can't be implied in the FROM clause!" );
 	}
 
 	public boolean isImpliedInFromClause() {
 		return false;	// Since this is an explicit FROM element, it can't be implied in the FROM clause.
 	}
 
 	public void setInProjectionList(boolean inProjectionList) {
 		// Do nothing, eplicit from elements are *always* in the projection list.
 	}
 
 	public boolean inProjectionList() {
 		return !isImplied() && isFromOrJoinFragment();
 	}
 
 	public boolean isFromOrJoinFragment() {
 		return getType() == SqlTokenTypes.FROM_FRAGMENT || getType() == SqlTokenTypes.JOIN_FRAGMENT;
 	}
 
 	public boolean isAllPropertyFetch() {
 		return isAllPropertyFetch;
 	}
 
 	public void setAllPropertyFetch(boolean fetch) {
 		isAllPropertyFetch = fetch;
 	}
 
 	public String getWithClauseFragment() {
 		return withClauseFragment;
 	}
 
 	public String getWithClauseJoinAlias() {
 		return withClauseJoinAlias;
 	}
 
 	public void setWithClauseFragment(String withClauseJoinAlias, String withClauseFragment) {
 		this.withClauseJoinAlias = withClauseJoinAlias;
 		this.withClauseFragment = withClauseFragment;
 	}
 
 	public boolean hasCacheablePersister() {
 		if ( getQueryableCollection() != null ) {
 			return getQueryableCollection().hasCache();
 		}
 		else {
 			return getQueryable().hasCache();
 		}
 	}
 
 	public void handlePropertyBeingDereferenced(Type propertySource, String propertyName) {
 		if ( getQueryableCollection() != null && CollectionProperties.isCollectionProperty( propertyName ) ) {
 			// propertyName refers to something like collection.size...
 			return;
 		}
 		if ( propertySource.isComponentType() ) {
 			// property name is a sub-path of a component...
 			return;
 		}
 
 		Queryable persister = getQueryable();
 		if ( persister != null ) {
 			try {
 				Queryable.Declarer propertyDeclarer = persister.getSubclassPropertyDeclarer( propertyName );
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracev( "Handling property dereference [{0} ({1}) -> {2} ({3})]",
 							persister.getEntityName(), getClassAlias(), propertyName, propertyDeclarer );
 				}
 				if ( propertyDeclarer == Queryable.Declarer.SUBCLASS ) {
 					dereferencedBySubclassProperty = true;
 					includeSubclasses = true;
 				}
 				else if ( propertyDeclarer == Queryable.Declarer.SUPERCLASS ) {
 					dereferencedBySuperclassProperty = true;
 				}
 			}
 			catch( QueryException ignore ) {
 				// ignore it; the incoming property could not be found so we
 				// cannot be sure what to do here.  At the very least, the
 				// safest is to simply not apply any dereference toggling...
 
 			}
 		}
 	}
 
 	public boolean isDereferencedBySuperclassProperty() {
 		return dereferencedBySuperclassProperty;
 	}
 
 	public boolean isDereferencedBySubclassProperty() {
 		return dereferencedBySubclassProperty;
 	}
 
 
 	// ParameterContainer impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private List embeddedParameters;
 
 	public void addEmbeddedParameter(ParameterSpecification specification) {
 		if ( embeddedParameters == null ) {
 			embeddedParameters = new ArrayList();
 		}
 		embeddedParameters.add( specification );
 	}
 
 	public boolean hasEmbeddedParameters() {
 		return embeddedParameters != null && ! embeddedParameters.isEmpty();
 	}
 
 	public ParameterSpecification[] getEmbeddedParameters() {
 		return ( ParameterSpecification[] ) embeddedParameters.toArray( new ParameterSpecification[ embeddedParameters.size() ] );
 	}
 
 	public ParameterSpecification getIndexCollectionSelectorParamSpec() {
 		return elementType.getIndexCollectionSelectorParamSpec();
 	}
 
 	public void setIndexCollectionSelectorParamSpec(ParameterSpecification indexCollectionSelectorParamSpec) {
 		if ( indexCollectionSelectorParamSpec == null ) {
 			if ( elementType.getIndexCollectionSelectorParamSpec() != null ) {
 				embeddedParameters.remove( elementType.getIndexCollectionSelectorParamSpec() );
 				elementType.setIndexCollectionSelectorParamSpec( null );
 			}
 		}
 		else {
 			elementType.setIndexCollectionSelectorParamSpec( indexCollectionSelectorParamSpec );
 			addEmbeddedParameter( indexCollectionSelectorParamSpec );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java
index 584a84d390..7e1694ca91 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java
@@ -1,154 +1,157 @@
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
 package org.hibernate.id;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.Type;
 
 /**
  * <b>increment</b><br>
  * <br>
  * An <tt>IdentifierGenerator</tt> that returns a <tt>long</tt>, constructed by
  * counting from the maximum primary key value at startup. Not safe for use in a
  * cluster!<br>
  * <br>
  * Mapping parameters supported, but not usually needed: tables, column.
  * (The tables parameter specified a comma-separated list of table names.)
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Brett Meyer
  */
 public class IncrementGenerator implements IdentifierGenerator, Configurable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, IncrementGenerator.class.getName());
 
 	private Class returnClass;
 	private String sql;
 
 	private IntegralDataTypeHolder previousValueHolder;
 
 	public synchronized Serializable generate(SessionImplementor session, Object object) throws HibernateException {
 		if ( sql != null ) {
 			initializePreviousValueHolder( session );
 		}
 		return previousValueHolder.makeValueThenIncrement();
 	}
 
 	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
 		returnClass = type.getReturnedClass();
 
 		ObjectNameNormalizer normalizer =
 				( ObjectNameNormalizer ) params.get( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER );
 
 		String column = params.getProperty( "column" );
 		if ( column == null ) {
 			column = params.getProperty( PersistentIdentifierGenerator.PK );
 		}
 		column = dialect.quote( normalizer.normalizeIdentifierQuoting( column ) );
 
 		String tableList = params.getProperty( "tables" );
 		if ( tableList == null ) {
 			tableList = params.getProperty( PersistentIdentifierGenerator.TABLES );
 		}
 		String[] tables = StringHelper.split( ", ", tableList );
 
 		final String schema = dialect.quote(
 				normalizer.normalizeIdentifierQuoting(
 						params.getProperty( PersistentIdentifierGenerator.SCHEMA )
 				)
 		);
 		final String catalog = dialect.quote(
 				normalizer.normalizeIdentifierQuoting(
 						params.getProperty( PersistentIdentifierGenerator.CATALOG )
 				)
 		);
 
 		StringBuilder buf = new StringBuilder();
 		for ( int i=0; i < tables.length; i++ ) {
 			final String tableName = dialect.quote( normalizer.normalizeIdentifierQuoting( tables[i] ) );
 			if ( tables.length > 1 ) {
 				buf.append( "select max(" ).append( column ).append( ") as mx from " );
 			}
 			buf.append( Table.qualify( catalog, schema, tableName ) );
 			if ( i < tables.length-1 ) {
 				buf.append( " union " );
 			}
 		}
 		if ( tables.length > 1 ) {
 			buf.insert( 0, "( " ).append( " ) ids_" );
 			column = "ids_.mx";
 		}
 
 		sql = "select max(" + column + ") from " + buf.toString();
 	}
 
 	private void initializePreviousValueHolder(SessionImplementor session) {
 		previousValueHolder = IdentifierGeneratorHelper.getIntegralDataTypeHolder( returnClass );
 
-		LOG.debugf( "Fetching initial value: %s", sql );
+		final boolean debugEnabled = LOG.isDebugEnabled();
+		if ( debugEnabled ) {
+			LOG.debugf( "Fetching initial value: %s", sql );
+		}
 		try {
 			PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql );
 			try {
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
                     if (rs.next()) previousValueHolder.initialize(rs, 0L).increment();
                     else previousValueHolder.initialize(1L);
 					sql = null;
-					if ( LOG.isDebugEnabled() ) {
+					if ( debugEnabled ) {
 						LOG.debugf( "First free id: %s", previousValueHolder.makeValue() );
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
 		catch (SQLException sqle) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not fetch initial value for increment generator",
 					sql
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index a1376df623..3f5549efd8 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1746 +1,1752 @@
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
 package org.hibernate.internal;
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.ObjectNotFoundException;
-import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.TypeHelper;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.cfg.SettingsFactory;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jndi.spi.JndiService;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
-import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.CacheImplementor;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionBuilderImplementor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.SchemaValidator;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.jboss.logging.Logger;
 
 
 /**
  * Concrete implementation of the <tt>SessionFactory</tt> interface. Has the following
  * responsibilities
  * <ul>
  * <li>caches configuration settings (immutably)
  * <li>caches "compiled" mappings ie. <tt>EntityPersister</tt>s and
  *     <tt>CollectionPersister</tt>s (immutable)
  * <li>caches "compiled" queries (memory sensitive cache)
  * <li>manages <tt>PreparedStatement</tt>s
  * <li> delegates JDBC <tt>Connection</tt> management to the <tt>ConnectionProvider</tt>
  * <li>factory for instances of <tt>SessionImpl</tt>
  * </ul>
  * This class must appear immutable to clients, even if it does all kinds of caching
  * and pooling under the covers. It is crucial that the class is not only thread
  * safe, but also highly concurrent. Synchronization must be used extremely sparingly.
  *
  * @see org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
  * @see org.hibernate.Session
  * @see org.hibernate.hql.spi.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl
 		implements SessionFactoryImplementor {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionFactoryImpl.class.getName());
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private final String name;
 	private final String uuid;
 
 	private final transient Map<String,EntityPersister> entityPersisters;
 	private final transient Map<String,ClassMetadata> classMetadata;
 	private final transient Map<String,CollectionPersister> collectionPersisters;
 	private final transient Map<String,CollectionMetadata> collectionMetadata;
 	private final transient Map<String,Set<String>> collectionRolesByEntityParticipant;
 	private final transient Map<String,IdentifierGenerator> identifierGenerators;
 	private final transient NamedQueryRepository namedQueryRepository;
 	private final transient Map<String, FilterDefinition> filters;
 	private final transient Map<String, FetchProfile> fetchProfiles;
 	private final transient Map<String,String> imports;
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
 	private final transient JdbcServices jdbcServices;
 	private final transient Dialect dialect;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
 	private final transient ConcurrentHashMap<EntityNameResolver,Object> entityNameResolvers = new ConcurrentHashMap<EntityNameResolver, Object>();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient CacheImplementor cacheAccess;
 	private transient boolean isClosed = false;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 	private final transient TransactionEnvironment transactionEnvironment;
 	private final transient SessionFactoryOptions sessionFactoryOptions;
 	private final transient CustomEntityDirtinessStrategy customEntityDirtinessStrategy;
 	private final transient CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 
 	@SuppressWarnings( {"unchecked", "ThrowableResultOfMethodCallIgnored"})
 	public SessionFactoryImpl(
 			final Configuration cfg,
 			Mapping mapping,
 			final ServiceRegistry serviceRegistry,
 			Settings settings,
 			SessionFactoryObserver observer) throws HibernateException {
 			LOG.debug( "Building session factory" );
 
 		sessionFactoryOptions = new SessionFactoryOptions() {
 			private EntityNotFoundDelegate entityNotFoundDelegate;
 
 			@Override
 			public StandardServiceRegistry getServiceRegistry() {
 				return (StandardServiceRegistry) serviceRegistry;
 			}
 
 			@Override
 			public Interceptor getInterceptor() {
 				return cfg.getInterceptor();
 			}
 
 			@Override
 			public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 				if ( entityNotFoundDelegate == null ) {
 					if ( cfg.getEntityNotFoundDelegate() != null ) {
 						entityNotFoundDelegate = cfg.getEntityNotFoundDelegate();
 					}
 					else {
 						entityNotFoundDelegate = new EntityNotFoundDelegate() {
 							public void handleEntityNotFound(String entityName, Serializable id) {
 								throw new ObjectNotFoundException( id, entityName );
 							}
 						};
 					}
 				}
 				return entityNotFoundDelegate;
 			}
 		};
 
 		this.settings = settings;
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 
 		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
 				this,
 				cfg
 		);
         this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
         this.dialect = this.jdbcServices.getDialect();
 		this.cacheAccess = this.serviceRegistry.getService( CacheImplementor.class );
 		final RegionFactory regionFactory = cacheAccess.getRegionFactory();
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = cfg.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		this.filters.putAll( cfg.getFilterDefinitions() );
 
 		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 		LOG.debugf( "Instantiating session factory with properties: %s", properties );
 
 
 		this.queryPlanCache = new QueryPlanCache( this );
 
 		// todo : everything above here consider implementing as standard SF service.  specifically: stats, caches, types, function-reg
 
 		class IntegratorObserver implements SessionFactoryObserver {
 			private ArrayList<Integrator> integrators = new ArrayList<Integrator>();
 
 			@Override
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 
 			@Override
 			public void sessionFactoryClosed(SessionFactory factory) {
 				for ( Integrator integrator : integrators ) {
 					integrator.disintegrate( SessionFactoryImpl.this, SessionFactoryImpl.this.serviceRegistry );
 				}
 			}
 		}
 
 		final IntegratorObserver integratorObserver = new IntegratorObserver();
 		this.observer.addObserver( integratorObserver );
 		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
 			integrator.integrate( cfg, this, this.serviceRegistry );
 			integratorObserver.integrators.add( integrator );
 		}
 
 		//Generators:
 
 		identifierGenerators = new HashMap();
 		Iterator classes = cfg.getClassMappings();
 		while ( classes.hasNext() ) {
 			PersistentClass model = (PersistentClass) classes.next();
 			if ( !model.isInherited() ) {
 				IdentifierGenerator generator = model.getIdentifier().createIdentifierGenerator(
 						cfg.getIdentifierGeneratorFactory(),
 						getDialect(),
 				        settings.getDefaultCatalogName(),
 				        settings.getDefaultSchemaName(),
 				        (RootClass) model
 				);
 				identifierGenerators.put( model.getEntityName(), generator );
 			}
 		}
 
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		final String cacheRegionPrefix = settings.getCacheRegionPrefix() == null ? "" : settings.getCacheRegionPrefix() + ".";
 
 		final PersisterFactory persisterFactory = serviceRegistry.getService( PersisterFactory.class );
 
 		entityPersisters = new HashMap();
 		Map entityAccessStrategies = new HashMap();
 		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
 		classes = cfg.getClassMappings();
 		while ( classes.hasNext() ) {
 			final PersistentClass model = (PersistentClass) classes.next();
 			model.prepareTemporaryTables( mapping, getDialect() );
 			final String cacheRegionName = cacheRegionPrefix + model.getRootClass().getCacheRegionName();
 			// cache region is defined by the root-class in the hierarchy...
 			EntityRegionAccessStrategy accessStrategy = ( EntityRegionAccessStrategy ) entityAccessStrategies.get( cacheRegionName );
 			if ( accessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 				final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 				if ( accessType != null ) {
 					LOG.tracef( "Building shared cache region for entity data [%s]", model.getEntityName() );
 					EntityRegion entityRegion = regionFactory.buildEntityRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					cacheAccess.addCacheRegion( cacheRegionName, entityRegion );
 				}
 			}
 			
 			NaturalIdRegionAccessStrategy naturalIdAccessStrategy = null;
 			if ( model.hasNaturalId() && model.getNaturalIdCacheRegionName() != null ) {
 				final String naturalIdCacheRegionName = cacheRegionPrefix + model.getNaturalIdCacheRegionName();
 				naturalIdAccessStrategy = ( NaturalIdRegionAccessStrategy ) entityAccessStrategies.get( naturalIdCacheRegionName );
 				
 				if ( naturalIdAccessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 					final CacheDataDescriptionImpl cacheDataDescription = CacheDataDescriptionImpl.decode( model );
 					
 					NaturalIdRegion naturalIdRegion = null;
 					try {
 						naturalIdRegion = regionFactory.buildNaturalIdRegion( naturalIdCacheRegionName, properties,
 								cacheDataDescription );
 					}
 					catch ( UnsupportedOperationException e ) {
 						LOG.warnf(
 								"Shared cache region factory [%s] does not support natural id caching; " +
 										"shared NaturalId caching will be disabled for not be enabled for %s",
 								regionFactory.getClass().getName(),
 								model.getEntityName()
 						);
 					}
 					
 					if (naturalIdRegion != null) {
 						naturalIdAccessStrategy = naturalIdRegion.buildAccessStrategy( regionFactory.getDefaultAccessType() );
 						entityAccessStrategies.put( naturalIdCacheRegionName, naturalIdAccessStrategy );
 						cacheAccess.addCacheRegion(  naturalIdCacheRegionName, naturalIdRegion );
 					}
 				}
 			}
 			
 			EntityPersister cp = persisterFactory.createEntityPersister(
 					model,
 					accessStrategy,
 					naturalIdAccessStrategy,
 					this,
 					mapping
 			);
 			entityPersisters.put( model.getEntityName(), cp );
 			classMeta.put( model.getEntityName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap<String,CollectionPersister>();
 		Map<String,CollectionMetadata> tmpCollectionMetadata = new HashMap<String,CollectionMetadata>();
 		Iterator collections = cfg.getCollectionMappings();
 		while ( collections.hasNext() ) {
 			Collection model = (Collection) collections.next();
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				LOG.tracev( "Building shared cache region for collection data [{0}]", model.getRole() );
 				CollectionRegion collectionRegion = regionFactory.buildCollectionRegion( cacheRegionName, properties, CacheDataDescriptionImpl
 						.decode( model ) );
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = persisterFactory.createCollectionPersister(
 					cfg,
 					model,
 					accessStrategy,
 					this
 			) ;
 			collectionPersisters.put( model.getRole(), persister );
 			tmpCollectionMetadata.put( model.getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		collectionMetadata = Collections.unmodifiableMap( tmpCollectionMetadata );
 		Iterator itr = tmpEntityToCollectionRoleMap.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			entry.setValue( Collections.unmodifiableSet( ( Set ) entry.getValue() ) );
 		}
 		collectionRolesByEntityParticipant = Collections.unmodifiableMap( tmpEntityToCollectionRoleMap );
 
 		//Named Queries:
 		this.namedQueryRepository = new NamedQueryRepository(
 				cfg.getNamedQueries().values(),
 				cfg.getNamedSQLQueries().values(),
 				cfg.getSqlResultSetMappings().values()
 		);
 		imports = new HashMap<String,String>( cfg.getImports() );
 
 		// after *all* persisters and named queries are registered
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final EntityPersister persister = ( ( EntityPersister ) iter.next() );
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister );
 
 		}
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final CollectionPersister persister = ( ( CollectionPersister ) iter.next() );
 			persister.postInstantiate();
 		}
 
 		//JNDI + Serialization:
 
 		name = settings.getSessionFactoryName();
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 		SessionFactoryRegistry.INSTANCE.addSessionFactory(
 				uuid,
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				this,
 				serviceRegistry.getService( JndiService.class )
 		);
 
 		LOG.debug( "Instantiated session factory" );
 
 		settings.getMultiTableBulkIdStrategy().prepare(
 				jdbcServices,
 				buildLocalConnectionAccess(),
 				cfg.createMappings(),
 				cfg.buildMapping(),
 				properties
 		);
 
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( serviceRegistry, cfg )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( serviceRegistry, cfg ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( serviceRegistry, cfg ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( serviceRegistry, cfg )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			final Map<String,HibernateException> errors = checkNamedQueries();
 			if ( ! errors.isEmpty() ) {
 				StringBuilder failingQueries = new StringBuilder( "Errors in named queries: " );
 				String sep = "";
 				for ( Map.Entry<String,HibernateException> entry : errors.entrySet() ) {
 					LOG.namedQueryError( entry.getKey(), entry.getValue() );
 					failingQueries.append( sep ).append( entry.getKey() );
 					sep = ", ";
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap();
 		itr = cfg.iterateFetchProfiles();
 		while ( itr.hasNext() ) {
 			final org.hibernate.mapping.FetchProfile mappingProfile =
 					( org.hibernate.mapping.FetchProfile ) itr.next();
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			for ( org.hibernate.mapping.FetchProfile.Fetch mappingFetch : mappingProfile.getFetches() ) {
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = entityName == null
 						? null
 						: entityPersisters.get( entityName );
 				if ( owner == null ) {
 					throw new HibernateException(
 							"Unable to resolve entity reference [" + mappingFetch.getEntity()
 									+ "] in fetch profile [" + fetchProfile.getName() + "]"
 					);
 				}
 
 				// validate the specified association fetch
 				Type associationType = owner.getPropertyType( mappingFetch.getAssociation() );
 				if ( associationType == null || !associationType.isAssociationType() ) {
 					throw new HibernateException( "Fetch profile [" + fetchProfile.getName() + "] specified an invalid association" );
 				}
 
 				// resolve the style
 				final Fetch.Style fetchStyle = Fetch.Style.parse( mappingFetch.getStyle() );
 
 				// then construct the fetch instance...
 				fetchProfile.addFetch( new Association( owner, mappingFetch.getAssociation() ), fetchStyle );
 				((Loadable) owner).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.customEntityDirtinessStrategy = determineCustomEntityDirtinessStrategy();
 		this.currentTenantIdentifierResolver = determineCurrentTenantIdentifierResolver( cfg.getCurrentTenantIdentifierResolver() );
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
 	}
 
 	private JdbcConnectionAccess buildLocalConnectionAccess() {
 		return new JdbcConnectionAccess() {
 			@Override
 			public Connection obtainConnection() throws SQLException {
 				return settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE
 						? serviceRegistry.getService( ConnectionProvider.class ).getConnection()
 						: serviceRegistry.getService( MultiTenantConnectionProvider.class ).getAnyConnection();
 			}
 
 			@Override
 			public void releaseConnection(Connection connection) throws SQLException {
 				if ( settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE ) {
 					serviceRegistry.getService( ConnectionProvider.class ).closeConnection( connection );
 				}
 				else {
 					serviceRegistry.getService( MultiTenantConnectionProvider.class ).releaseAnyConnection( connection );
 				}
 			}
 
 			@Override
 			public boolean supportsAggressiveRelease() {
 				return false;
 			}
 		};
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private CustomEntityDirtinessStrategy determineCustomEntityDirtinessStrategy() {
 		CustomEntityDirtinessStrategy defaultValue = new CustomEntityDirtinessStrategy() {
 			@Override
 			public boolean canDirtyCheck(Object entity, EntityPersister persister, Session session) {
 				return false;
 			}
 
 			@Override
 			public boolean isDirty(Object entity, EntityPersister persister, Session session) {
 				return false;
 			}
 
 			@Override
 			public void resetDirty(Object entity, EntityPersister persister, Session session) {
 			}
 
 			@Override
 			public void findDirty(
 					Object entity,
 					EntityPersister persister,
 					Session session,
 					DirtyCheckContext dirtyCheckContext) {
 				// todo : implement proper method body
 			}
 		};
 		return serviceRegistry.getService( ConfigurationService.class ).getSetting(
 				AvailableSettings.CUSTOM_ENTITY_DIRTINESS_STRATEGY,
 				CustomEntityDirtinessStrategy.class,
 				defaultValue
 		);
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private CurrentTenantIdentifierResolver determineCurrentTenantIdentifierResolver(
 			CurrentTenantIdentifierResolver explicitResolver) {
 		if ( explicitResolver != null ) {
 			return explicitResolver;
 		}
 		return serviceRegistry.getService( ConfigurationService.class )
 				.getSetting(
 						AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER,
 						CurrentTenantIdentifierResolver.class,
 						null
 				);
 
 	}
 
 	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
 	public SessionFactoryImpl(
 			MetadataImplementor metadata,
 			SessionFactoryOptions sessionFactoryOptions,
 			SessionFactoryObserver observer) throws HibernateException {
-		LOG.debug( "Building session factory" );
+
+		final boolean traceEnabled = LOG.isTraceEnabled();
+		final boolean debugEnabled = traceEnabled || LOG.isDebugEnabled();
+		if ( debugEnabled ) {
+			LOG.debug( "Building session factory" );
+		}
 
 		this.sessionFactoryOptions = sessionFactoryOptions;
 
 		this.properties = createPropertiesFromMap(
 				metadata.getServiceRegistry().getService( ConfigurationService.class ).getSettings()
 		);
 
 		// TODO: these should be moved into SessionFactoryOptions
 		this.settings = new SettingsFactory().buildSettings(
 				properties,
 				metadata.getServiceRegistry()
 		);
 
 		this.serviceRegistry =
 				sessionFactoryOptions.getServiceRegistry()
 						.getService( SessionFactoryServiceRegistryFactory.class )
 						.buildServiceRegistry( this, metadata );
 
 		this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
 		this.dialect = this.jdbcServices.getDialect();
 		this.cacheAccess = this.serviceRegistry.getService( CacheImplementor.class );
 
 		// TODO: get SQL functions from JdbcServices (HHH-6559)
 		//this.sqlFunctionRegistry = new SQLFunctionRegistry( this.jdbcServices.getSqlFunctions() );
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( this.dialect, new HashMap<String, SQLFunction>() );
 
 		// TODO: get SQL functions from a new service
 		// this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = metadata.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		for ( FilterDefinition filterDefinition : metadata.getFilterDefinitions() ) {
 			filters.put( filterDefinition.getFilterName(), filterDefinition );
 		}
 
-		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
-		LOG.debugf( "Instantiating session factory with properties: %s", properties );
-
+		if ( debugEnabled ) {
+			LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
+			LOG.debugf( "Instantiating session factory with properties: %s", properties );
+		}
 		this.queryPlanCache = new QueryPlanCache( this );
 
 		class IntegratorObserver implements SessionFactoryObserver {
 			private ArrayList<Integrator> integrators = new ArrayList<Integrator>();
 
 			@Override
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 
 			@Override
 			public void sessionFactoryClosed(SessionFactory factory) {
 				for ( Integrator integrator : integrators ) {
 					integrator.disintegrate( SessionFactoryImpl.this, SessionFactoryImpl.this.serviceRegistry );
 				}
 			}
 		}
 
         final IntegratorObserver integratorObserver = new IntegratorObserver();
         this.observer.addObserver(integratorObserver);
         for (Integrator integrator : serviceRegistry.getService(IntegratorService.class).getIntegrators()) {
             integrator.integrate(metadata, this, this.serviceRegistry);
             integratorObserver.integrators.add(integrator);
         }
 
 
 		//Generators:
 
 		identifierGenerators = new HashMap<String,IdentifierGenerator>();
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
 			if ( entityBinding.isRoot() ) {
 				identifierGenerators.put(
 						entityBinding.getEntity().getName(),
 						entityBinding.getHierarchyDetails().getEntityIdentifier().getIdentifierGenerator()
 				);
 			}
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		StringBuilder stringBuilder = new StringBuilder();
 		if ( settings.getCacheRegionPrefix() != null) {
 			stringBuilder
 					.append( settings.getCacheRegionPrefix() )
 					.append( '.' );
 		}
 		final String cacheRegionPrefix = stringBuilder.toString();
 
 		entityPersisters = new HashMap<String,EntityPersister>();
 		Map<String, RegionAccessStrategy> entityAccessStrategies = new HashMap<String, RegionAccessStrategy>();
 		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
 		for ( EntityBinding model : metadata.getEntityBindings() ) {
 			// TODO: should temp table prep happen when metadata is being built?
 			//model.prepareTemporaryTables( metadata, getDialect() );
 			// cache region is defined by the root-class in the hierarchy...
 			EntityBinding rootEntityBinding = metadata.getRootEntityBinding( model.getEntity().getName() );
 			EntityRegionAccessStrategy accessStrategy = null;
 			if ( settings.isSecondLevelCacheEnabled() &&
 					rootEntityBinding.getHierarchyDetails().getCaching() != null &&
 					model.getHierarchyDetails().getCaching() != null &&
 					model.getHierarchyDetails().getCaching().getAccessType() != null ) {
 				final String cacheRegionName = cacheRegionPrefix + rootEntityBinding.getHierarchyDetails().getCaching().getRegion();
 				accessStrategy = EntityRegionAccessStrategy.class.cast( entityAccessStrategies.get( cacheRegionName ) );
 				if ( accessStrategy == null ) {
 					final AccessType accessType = model.getHierarchyDetails().getCaching().getAccessType();
-					if ( LOG.isTraceEnabled() ) {
+					if ( traceEnabled ) {
 						LOG.tracev( "Building cache for entity data [{0}]", model.getEntity().getName() );
 					}
 					EntityRegion entityRegion = settings.getRegionFactory().buildEntityRegion(
 							cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 					);
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					cacheAccess.addCacheRegion( cacheRegionName, entityRegion );
 				}
 			}
 			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
 					model, accessStrategy, this, metadata
 			);
 			entityPersisters.put( model.getEntity().getName(), cp );
 			classMeta.put( model.getEntity().getName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap<String,CollectionPersister>();
 		Map<String, CollectionMetadata> tmpCollectionMetadata = new HashMap<String, CollectionMetadata>();
 		for ( PluralAttributeBinding model : metadata.getCollectionBindings() ) {
 			if ( model.getAttribute() == null ) {
 				throw new IllegalStateException( "No attribute defined for a AbstractPluralAttributeBinding: " +  model );
 			}
 			if ( model.getAttribute().isSingular() ) {
 				throw new IllegalStateException(
 						"AbstractPluralAttributeBinding has a Singular attribute defined: " + model.getAttribute().getName()
 				);
 			}
 			final String cacheRegionName = cacheRegionPrefix + model.getCaching().getRegion();
 			final AccessType accessType = model.getCaching().getAccessType();
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
-				if ( LOG.isTraceEnabled() ) {
+				if ( traceEnabled ) {
 					LOG.tracev( "Building cache for collection data [{0}]", model.getAttribute().getRole() );
 				}
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion(
 						cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 				);
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion(  cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = serviceRegistry
 					.getService( PersisterFactory.class )
 					.createCollectionPersister( metadata, model, accessStrategy, this );
 			collectionPersisters.put( model.getAttribute().getRole(), persister );
 			tmpCollectionMetadata.put( model.getAttribute().getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set<String> roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set<String> roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		collectionMetadata = Collections.unmodifiableMap( tmpCollectionMetadata );
 		for ( Map.Entry<String, Set<String>> entry : tmpEntityToCollectionRoleMap.entrySet() ) {
 			entry.setValue( Collections.unmodifiableSet( entry.getValue() ) );
 		}
 		collectionRolesByEntityParticipant = Collections.unmodifiableMap( tmpEntityToCollectionRoleMap );
 
 
 		//Named Queries:
 		namedQueryRepository = new NamedQueryRepository(
 				metadata.getNamedQueryDefinitions(),
 				metadata.getNamedNativeQueryDefinitions(),
 				metadata.getResultSetMappingDefinitions()
 		);
 
 		imports = new HashMap<String,String>();
 		for ( Map.Entry<String,String> importEntry : metadata.getImports() ) {
 			imports.put( importEntry.getKey(), importEntry.getValue() );
 		}
 
 		// after *all* persisters and named queries are registered
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final EntityPersister persister = ( ( EntityPersister ) iter.next() );
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister );
 
 		}
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final CollectionPersister persister = ( ( CollectionPersister ) iter.next() );
 			persister.postInstantiate();
 		}
 
 		//JNDI + Serialization:
 
 		name = settings.getSessionFactoryName();
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 		SessionFactoryRegistry.INSTANCE.addSessionFactory(
 				uuid, 
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				this,
 				serviceRegistry.getService( JndiService.class )
 		);
 
-		LOG.debug("Instantiated session factory");
+		if ( debugEnabled ) {
+			LOG.debug("Instantiated session factory");
+		}
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
 
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			final Map<String,HibernateException> errors = checkNamedQueries();
 			if ( ! errors.isEmpty() ) {
 				StringBuilder failingQueries = new StringBuilder( "Errors in named queries: " );
 				String sep = "";
 				for ( Map.Entry<String,HibernateException> entry : errors.entrySet() ) {
 					LOG.namedQueryError( entry.getKey(), entry.getValue() );
 					failingQueries.append( entry.getKey() ).append( sep );
 					sep = ", ";
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap<String,FetchProfile>();
 		for ( org.hibernate.metamodel.binding.FetchProfile mappingProfile : metadata.getFetchProfiles() ) {
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			for ( org.hibernate.metamodel.binding.FetchProfile.Fetch mappingFetch : mappingProfile.getFetches() ) {
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = entityName == null ? null : entityPersisters.get( entityName );
 				if ( owner == null ) {
 					throw new HibernateException(
 							"Unable to resolve entity reference [" + mappingFetch.getEntity()
 									+ "] in fetch profile [" + fetchProfile.getName() + "]"
 					);
 				}
 
 				// validate the specified association fetch
 				Type associationType = owner.getPropertyType( mappingFetch.getAssociation() );
 				if ( associationType == null || ! associationType.isAssociationType() ) {
 					throw new HibernateException( "Fetch profile [" + fetchProfile.getName() + "] specified an invalid association" );
 				}
 
 				// resolve the style
 				final Fetch.Style fetchStyle = Fetch.Style.parse( mappingFetch.getStyle() );
 
 				// then construct the fetch instance...
 				fetchProfile.addFetch( new Association( owner, mappingFetch.getAssociation() ), fetchStyle );
 				( ( Loadable ) owner ).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.customEntityDirtinessStrategy = determineCustomEntityDirtinessStrategy();
 		this.currentTenantIdentifierResolver = determineCurrentTenantIdentifierResolver( null );
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
 	}
 
 	@SuppressWarnings( {"unchecked"} )
 	private static Properties createPropertiesFromMap(Map map) {
 		Properties properties = new Properties();
 		properties.putAll( map );
 		return properties;
 	}
 
 	public Session openSession() throws HibernateException {
 		return withOptions().openSession();
 	}
 
 	public Session openTemporarySession() throws HibernateException {
 		return withOptions()
 				.autoClose( false )
 				.flushBeforeCompletion( false )
 				.connectionReleaseMode( ConnectionReleaseMode.AFTER_STATEMENT )
 				.openSession();
 	}
 
 	public Session getCurrentSession() throws HibernateException {
 		if ( currentSessionContext == null ) {
 			throw new HibernateException( "No CurrentSessionContext configured!" );
 		}
 		return currentSessionContext.currentSession();
 	}
 
 	@Override
 	public SessionBuilderImplementor withOptions() {
 		return new SessionBuilderImpl( this );
 	}
 
 	@Override
 	public StatelessSessionBuilder withStatelessOptions() {
 		return new StatelessSessionBuilderImpl( this );
 	}
 
 	public StatelessSession openStatelessSession() {
 		return withStatelessOptions().openStatelessSession();
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return withStatelessOptions().connection( connection ).openStatelessSession();
 	}
 
 	@Override
 	public void addObserver(SessionFactoryObserver observer) {
 		this.observer.addObserver( observer );
 	}
 
 	public TransactionEnvironment getTransactionEnvironment() {
 		return transactionEnvironment;
 	}
 
 	public Properties getProperties() {
 		return properties;
 	}
 
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return null;
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	private void registerEntityNameResolvers(EntityPersister persister) {
 		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizer() == null ) {
 			return;
 		}
 		registerEntityNameResolvers( persister.getEntityMetamodel().getTuplizer() );
 	}
 
 	private void registerEntityNameResolvers(EntityTuplizer tuplizer) {
 		EntityNameResolver[] resolvers = tuplizer.getEntityNameResolvers();
 		if ( resolvers == null ) {
 			return;
 		}
 
 		for ( EntityNameResolver resolver : resolvers ) {
 			registerEntityNameResolver( resolver );
 		}
 	}
 
 	private static final Object ENTITY_NAME_RESOLVER_MAP_VALUE = new Object();
 
 	public void registerEntityNameResolver(EntityNameResolver resolver) {
 		entityNameResolvers.put( resolver, ENTITY_NAME_RESOLVER_MAP_VALUE );
 	}
 
 	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
 		return entityNameResolvers.keySet();
 	}
 
 	public QueryPlanCache getQueryPlanCache() {
 		return queryPlanCache;
 	}
 
 	private Map<String,HibernateException> checkNamedQueries() throws HibernateException {
 		return namedQueryRepository.checkNamedQueries( queryPlanCache );
 	}
 
 	public EntityPersister getEntityPersister(String entityName) throws MappingException {
 		EntityPersister result = entityPersisters.get(entityName);
 		if ( result == null ) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 	@Override
 	public Map<String, CollectionPersister> getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	@Override
 	public Map<String, EntityPersister> getEntityPersisters() {
 		return entityPersisters;
 	}
 
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
 		CollectionPersister result = collectionPersisters.get(role);
 		if ( result == null ) {
 			throw new MappingException( "Unknown collection role: " + role );
 		}
 		return result;
 	}
 
 	public Settings getSettings() {
 		return settings;
 	}
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return sessionFactoryOptions;
 	}
 
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
 	}
 
 	public Dialect getDialect() {
 		if ( serviceRegistry == null ) {
 			throw new IllegalStateException( "Cannot determine dialect because serviceRegistry is null." );
 		}
 		return dialect;
 	}
 
 	public Interceptor getInterceptor() {
 		return sessionFactoryOptions.getInterceptor();
 	}
 
 	public SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	public SqlExceptionHelper getSQLExceptionHelper() {
 		return getJdbcServices().getSqlExceptionHelper();
 	}
 
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
 		return collectionRolesByEntityParticipant.get( entityName );
 	}
 
 	@Override
 	public Reference getReference() {
 		// from javax.naming.Referenceable
         LOG.debug( "Returning a Reference to the SessionFactory" );
 		return new Reference(
 				SessionFactoryImpl.class.getName(),
 				new StringRefAddr("uuid", uuid),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	@Override
 	public NamedQueryRepository getNamedQueryRepository() {
 		return namedQueryRepository;
 	}
 
 	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
 		namedQueryRepository.registerNamedQueryDefinition( name, definition );
 	}
 
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return namedQueryRepository.getNamedQueryDefinition( queryName );
 	}
 
 	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
 		namedQueryRepository.registerNamedSQLQueryDefinition( name, definition );
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return namedQueryRepository.getNamedSQLQueryDefinition( queryName );
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String mappingName) {
 		return namedQueryRepository.getResultSetMappingDefinition( mappingName );
 	}
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierPropertyName();
 	}
 
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata()
 				.getReturnTypes();
 	}
 
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata()
 				.getReturnAliases();
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getClassMetadata( persistentClass.getName() );
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return collectionMetadata.get(roleName);
 	}
 
 	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
 		return classMetadata.get( entityName );
 	}
 
 	/**
 	 * Given the name of an entity class, determine all the class and interface names by which it can be
 	 * referenced in an HQL query.
 	 *
      * @param className The name of the entity class
 	 *
 	 * @return the names of all persistent (mapped) classes that extend or implement the
 	 *     given class or interface, accounting for implicit/explicit polymorphism settings
 	 *     and excluding mapped subclasses/joined-subclasses of other classes in the result.
 	 * @throws MappingException
 	 */
 	public String[] getImplementors(String className) throws MappingException {
 
 		final Class clazz;
 		try {
 			clazz = serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
 		}
 		catch (ClassLoadingException cnfe) {
 			return new String[] { className }; //for a dynamic-class
 		}
 
 		ArrayList<String> results = new ArrayList<String>();
 		for ( EntityPersister checkPersister : entityPersisters.values() ) {
 			if ( ! Queryable.class.isInstance( checkPersister ) ) {
 				continue;
 			}
 			final Queryable checkQueryable = Queryable.class.cast( checkPersister );
 			final String checkQueryableEntityName = checkQueryable.getEntityName();
 			final boolean isMappedClass = className.equals( checkQueryableEntityName );
 			if ( checkQueryable.isExplicitPolymorphism() ) {
 				if ( isMappedClass ) {
 					return new String[] { className }; //NOTE EARLY EXIT
 				}
 			}
 			else {
 				if ( isMappedClass ) {
 					results.add( checkQueryableEntityName );
 				}
 				else {
 					final Class mappedClass = checkQueryable.getMappedClass();
 					if ( mappedClass != null && clazz.isAssignableFrom( mappedClass ) ) {
 						final boolean assignableSuperclass;
 						if ( checkQueryable.isInherited() ) {
 							Class mappedSuperclass = getEntityPersister( checkQueryable.getMappedSuperclass() ).getMappedClass();
 							assignableSuperclass = clazz.isAssignableFrom( mappedSuperclass );
 						}
 						else {
 							assignableSuperclass = false;
 						}
 						if ( !assignableSuperclass ) {
 							results.add( checkQueryableEntityName );
 						}
 					}
 				}
 			}
 		}
 		return results.toArray( new String[results.size()] );
 	}
 
 	@Override
 	public String getImportedClassName(String className) {
 		String result = imports.get( className );
 		if ( result == null ) {
 			try {
 				serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
 				imports.put( className, className );
 				return className;
 			}
 			catch ( ClassLoadingException cnfe ) {
 				return null;
 			}
 		}
 		else {
 			return result;
 		}
 	}
 
 	public Map<String,ClassMetadata> getAllClassMetadata() throws HibernateException {
 		return classMetadata;
 	}
 
 	public Map getAllCollectionMetadata() throws HibernateException {
 		return collectionMetadata;
 	}
 
 	public Type getReferencedPropertyType(String className, String propertyName)
 		throws MappingException {
 		return getEntityPersister( className ).getPropertyType( propertyName );
 	}
 
 	public ConnectionProvider getConnectionProvider() {
 		return jdbcServices.getConnectionProvider();
 	}
 
 	/**
 	 * Closes the session factory, releasing all held resources.
 	 *
 	 * <ol>
 	 * <li>cleans up used cache regions and "stops" the cache provider.
 	 * <li>close the JDBC connection
 	 * <li>remove the JNDI binding
 	 * </ol>
 	 *
 	 * Note: Be aware that the sessionFactory instance still can
 	 * be a "heavy" object memory wise after close() has been called.  Thus
 	 * it is important to not keep referencing the instance to let the garbage
 	 * collector release the memory.
 	 * @throws HibernateException
 	 */
 	public void close() throws HibernateException {
 
 		if ( isClosed ) {
 			LOG.trace( "Already closed" );
 			return;
 		}
 
 		LOG.closing();
 
 		isClosed = true;
 
 		settings.getMultiTableBulkIdStrategy().release( jdbcServices, buildLocalConnectionAccess() );
 
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			EntityPersister p = (EntityPersister) iter.next();
 			if ( p.hasCache() ) {
 				p.getCacheAccessStrategy().getRegion().destroy();
 			}
 		}
 
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			CollectionPersister p = (CollectionPersister) iter.next();
 			if ( p.hasCache() ) {
 				p.getCacheAccessStrategy().getRegion().destroy();
 			}
 		}
 
 		cacheAccess.close();
 
 		queryPlanCache.cleanup();
 
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport.drop( false, true );
 		}
 
 		SessionFactoryRegistry.INSTANCE.removeSessionFactory(
 				uuid,
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				serviceRegistry.getService( JndiService.class )
 		);
 
 		observer.sessionFactoryClosed( this );
 		serviceRegistry.destroy();
 	}
 
 	public Cache getCache() {
 		return cacheAccess;
 	}
 
 	public void evictEntity(String entityName, Serializable id) throws HibernateException {
 		getCache().evictEntity( entityName, id );
 	}
 
 	public void evictEntity(String entityName) throws HibernateException {
 		getCache().evictEntityRegion( entityName );
 	}
 
 	public void evict(Class persistentClass, Serializable id) throws HibernateException {
 		getCache().evictEntity( persistentClass, id );
 	}
 
 	public void evict(Class persistentClass) throws HibernateException {
 		getCache().evictEntityRegion( persistentClass );
 	}
 
 	public void evictCollection(String roleName, Serializable id) throws HibernateException {
 		getCache().evictCollection( roleName, id );
 	}
 
 	public void evictCollection(String roleName) throws HibernateException {
 		getCache().evictCollectionRegion( roleName );
 	}
 
 	public void evictQueries() throws HibernateException {
 		cacheAccess.evictQueries();
 	}
 
 	public void evictQueries(String regionName) throws HibernateException {
 		getCache().evictQueryRegion( regionName );
 	}
 
 	public UpdateTimestampsCache getUpdateTimestampsCache() {
 		return cacheAccess.getUpdateTimestampsCache();
 	}
 
 	public QueryCache getQueryCache() {
 		return cacheAccess.getQueryCache();
 	}
 
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		return cacheAccess.getQueryCache( regionName );
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return cacheAccess.getSecondLevelCacheRegion( regionName );
 	}
 
 	public Region getNaturalIdCacheRegion(String regionName) {
 		return cacheAccess.getNaturalIdCacheRegion( regionName );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public Map getAllSecondLevelCacheRegions() {
 		return cacheAccess.getAllSecondLevelCacheRegions();
 	}
 
 	public boolean isClosed() {
 		return isClosed;
 	}
 
 	public Statistics getStatistics() {
 		return getStatisticsImplementor();
 	}
 
 	public StatisticsImplementor getStatisticsImplementor() {
 		return serviceRegistry.getService( StatisticsImplementor.class );
 	}
 
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		FilterDefinition def = filters.get( filterName );
 		if ( def == null ) {
 			throw new HibernateException( "No such filter configured [" + filterName + "]" );
 		}
 		return def;
 	}
 
 	public boolean containsFetchProfileDefinition(String name) {
 		return fetchProfiles.containsKey( name );
 	}
 
 	public Set getDefinedFilterNames() {
 		return filters.keySet();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
 		return identifierGenerators.get(rootEntityName);
 	}
 
 	private TransactionFactory transactionFactory() {
 		return serviceRegistry.getService( TransactionFactory.class );
 	}
 
 	private boolean canAccessTransactionManager() {
 		try {
 			return serviceRegistry.getService( JtaPlatform.class ).retrieveTransactionManager() != null;
 		}
 		catch (Exception e) {
 			return false;
 		}
 	}
 
 	private CurrentSessionContext buildCurrentSessionContext() {
 		String impl = properties.getProperty( Environment.CURRENT_SESSION_CONTEXT_CLASS );
 		// for backward-compatibility
 		if ( impl == null ) {
 			if ( canAccessTransactionManager() ) {
 				impl = "jta";
 			}
 			else {
 				return null;
 			}
 		}
 
 		if ( "jta".equals( impl ) ) {
 			if ( ! transactionFactory().compatibleWithJtaSynchronization() ) {
 				LOG.autoFlushWillNotWork();
 			}
 			return new JTASessionContext( this );
 		}
 		else if ( "thread".equals( impl ) ) {
 			return new ThreadLocalSessionContext( this );
 		}
 		else if ( "managed".equals( impl ) ) {
 			return new ManagedSessionContext( this );
 		}
 		else {
 			try {
 				Class implClass = serviceRegistry.getService( ClassLoaderService.class ).classForName( impl );
 				return ( CurrentSessionContext ) implClass
 						.getConstructor( new Class[] { SessionFactoryImplementor.class } )
 						.newInstance( this );
 			}
 			catch( Throwable t ) {
 				LOG.unableToConstructCurrentSessionContext( impl, t );
 				return null;
 			}
 		}
 	}
 
 	@Override
 	public ServiceRegistryImplementor getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return sessionFactoryOptions.getEntityNotFoundDelegate();
 	}
 
 	public SQLFunctionRegistry getSqlFunctionRegistry() {
 		return sqlFunctionRegistry;
 	}
 
 	public FetchProfile getFetchProfile(String name) {
 		return fetchProfiles.get( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return typeHelper;
 	}
 
 	static class SessionBuilderImpl implements SessionBuilderImplementor {
 		private final SessionFactoryImpl sessionFactory;
 		private SessionOwner sessionOwner;
 		private Interceptor interceptor;
 		private Connection connection;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private boolean autoClose;
 		private boolean autoJoinTransactions = true;
 		private boolean flushBeforeCompletion;
 		private String tenantIdentifier;
 
 		SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 			this.sessionOwner = null;
 			final Settings settings = sessionFactory.settings;
 
 			// set up default builder values...
 			this.interceptor = sessionFactory.getInterceptor();
 			this.connectionReleaseMode = settings.getConnectionReleaseMode();
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
 		}
 
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			return new SessionImpl(
 					connection,
 					sessionFactory,
 					sessionOwner,
 					getTransactionCoordinator(),
 					autoJoinTransactions,
 					sessionFactory.settings.getRegionFactory().nextTimestamp(),
 					interceptor,
 					flushBeforeCompletion,
 					autoClose,
 					connectionReleaseMode,
 					tenantIdentifier
 			);
 		}
 
 		@Override
 		public SessionBuilder owner(SessionOwner sessionOwner) {
 			this.sessionOwner = sessionOwner;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder interceptor(Interceptor interceptor) {
 			this.interceptor = interceptor;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder noInterceptor() {
 			this.interceptor = EmptyInterceptor.INSTANCE;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 			this.connectionReleaseMode = connectionReleaseMode;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
 			this.autoJoinTransactions = autoJoinTransactions;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoClose(boolean autoClose) {
 			this.autoClose = autoClose;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
 			this.flushBeforeCompletion = flushBeforeCompletion;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 
 	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Connection connection;
 		private String tenantIdentifier;
 
 		public StatelessSessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 		}
 
 		@Override
 		public StatelessSession openStatelessSession() {
 			return new StatelessSessionImpl( connection, tenantIdentifier, sessionFactory );
 		}
 
 		@Override
 		public StatelessSessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public StatelessSessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 
 	@Override
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 		return customEntityDirtinessStrategy;
 	}
 
 	@Override
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return currentTenantIdentifierResolver;
 	}
 
 
 	// Serialization handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly serialized
 	 *
 	 * @param out The stream into which the object is being serialized.
 	 *
 	 * @throws IOException Can be thrown by the stream
 	 */
 	private void writeObject(ObjectOutputStream out) throws IOException {
 		LOG.debugf( "Serializing: %s", uuid );
 		out.defaultWriteObject();
 		LOG.trace( "Serialized" );
 	}
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized
 	 *
 	 * @param in The stream from which the object is being deserialized.
 	 *
 	 * @throws IOException Can be thrown by the stream
 	 * @throws ClassNotFoundException Again, can be thrown by the stream
 	 */
 	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
 		LOG.trace( "Deserializing" );
 		in.defaultReadObject();
 		LOG.debugf( "Deserialized: %s", uuid );
 	}
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized.
 	 * Here we resolve the uuid/name read from the stream previously to resolve the SessionFactory
 	 * instance to use based on the registrations with the {@link SessionFactoryRegistry}
 	 *
 	 * @return The resolved factory to use.
 	 *
 	 * @throws InvalidObjectException Thrown if we could not resolve the factory by uuid/name.
 	 */
 	private Object readResolve() throws InvalidObjectException {
 		LOG.trace( "Resolving serialized SessionFactory" );
 		return locateSessionFactoryOnDeserialization( uuid, name );
 	}
 
 	private static SessionFactory locateSessionFactoryOnDeserialization(String uuid, String name) throws InvalidObjectException{
 		final SessionFactory uuidResult = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 		if ( uuidResult != null ) {
 			LOG.debugf( "Resolved SessionFactory by UUID [%s]", uuid );
 			return uuidResult;
 		}
 
 		// in case we were deserialized in a different JVM, look for an instance with the same name
 		// (provided we were given a name)
 		if ( name != null ) {
 			final SessionFactory namedResult = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
 			if ( namedResult != null ) {
 				LOG.debugf( "Resolved SessionFactory by name [%s]", name );
 				return namedResult;
 			}
 		}
 
 		throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
 	}
 
 	/**
 	 * Custom serialization hook used during Session serialization.
 	 *
 	 * @param oos The stream to which to write the factory
 	 * @throws IOException Indicates problems writing out the serial data stream
 	 */
 	void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeUTF( uuid );
 		oos.writeBoolean( name != null );
 		if ( name != null ) {
 			oos.writeUTF( name );
 		}
 	}
 
 	/**
 	 * Custom deserialization hook used during Session deserialization.
 	 *
 	 * @param ois The stream from which to "read" the factory
 	 * @return The deserialized factory
 	 * @throws IOException indicates problems reading back serial data stream
 	 * @throws ClassNotFoundException indicates problems reading back serial data stream
 	 */
 	static SessionFactoryImpl deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		LOG.trace( "Deserializing SessionFactory from Session" );
 		final String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		final String name = isNamed ? ois.readUTF() : null;
 		return (SessionFactoryImpl) locateSessionFactoryOnDeserialization( uuid, name );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
index 59e9dba423..63e24f69ec 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
@@ -1577,1172 +1577,1173 @@ public final class SessionImpl extends AbstractSessionImpl implements EventSourc
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
 	public Criteria createCriteria(String entityName, String alias) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, alias, this);
 	}
 
 	public Criteria createCriteria(Class persistentClass) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
 	public Criteria createCriteria(String entityName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, this);
 	}
 
 	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 		
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String entityName = criteriaImpl.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable(entityName),
 				factory,
 				criteriaImpl,
 				entityName,
 				getLoadQueryInfluencers()
 		);
 		autoFlushIfRequired( loader.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return loader.scroll(this, scrollMode);
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
 	public List list(Criteria criteria) throws HibernateException {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 				
 		final NaturalIdLoadAccess naturalIdLoadAccess = this.tryNaturalIdLoadAccess( criteriaImpl );
 		if ( naturalIdLoadAccess != null ) {
 			// EARLY EXIT!
 			return Arrays.asList( naturalIdLoadAccess.load() );
 		}
 
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String[] implementors = factory.getImplementors( criteriaImpl.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		Set spaces = new HashSet();
 		for( int i=0; i <size; i++ ) {
 
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 					factory,
 					criteriaImpl,
 					implementors[i],
 					getLoadQueryInfluencers()
 				);
 
 			spaces.addAll( loaders[i].getQuerySpaces() );
 
 		}
 
 		autoFlushIfRequired(spaces);
 
 		List results = Collections.EMPTY_LIST;
 		dontFlushFromFind++;
 		boolean success = false;
 		try {
 			for( int i=0; i<size; i++ ) {
 				final List currentResults = loaders[i].list(this);
 				currentResults.addAll(results);
 				results = currentResults;
 			}
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 			delayedAfterCompletion();
 		}
 
 		return results;
 	}
 
 	/**
 	 * Checks to see if the CriteriaImpl is a naturalId lookup that can be done via
 	 * NaturalIdLoadAccess
 	 *
 	 * @param criteria The criteria to check as a complete natural identifier lookup.
 	 *
 	 * @return A fully configured NaturalIdLoadAccess or null, if null is returned the standard CriteriaImpl execution
 	 *         should be performed
 	 */
 	private NaturalIdLoadAccess tryNaturalIdLoadAccess(CriteriaImpl criteria) {
 		// See if the criteria lookup is by naturalId
 		if ( !criteria.isLookupByNaturalKey() ) {
 			return null;
 		}
 
 		final String entityName = criteria.getEntityOrClassName();
 		final EntityPersister entityPersister = factory.getEntityPersister( entityName );
 
 		// Verify the entity actually has a natural id, needed for legacy support as NaturalIdentifier criteria
 		// queries did no natural id validation
 		if ( !entityPersister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		// Since isLookupByNaturalKey is true there can be only one CriterionEntry and getCriterion() will
 		// return an instanceof NaturalIdentifier
 		final CriterionEntry criterionEntry = (CriterionEntry) criteria.iterateExpressionEntries().next();
 		final NaturalIdentifier naturalIdentifier = (NaturalIdentifier) criterionEntry.getCriterion();
 
 		final Map<String, Object> naturalIdValues = naturalIdentifier.getNaturalIdValues();
 		final int[] naturalIdentifierProperties = entityPersister.getNaturalIdentifierProperties();
 
 		// Verify the NaturalIdentifier criterion includes all naturalId properties, first check that the property counts match
 		if ( naturalIdentifierProperties.length != naturalIdValues.size() ) {
 			return null;
 		}
 
 		final String[] propertyNames = entityPersister.getPropertyNames();
 		final NaturalIdLoadAccess naturalIdLoader = this.byNaturalId( entityName );
 
 		// Build NaturalIdLoadAccess and in the process verify all naturalId properties were specified
 		for ( int i = 0; i < naturalIdentifierProperties.length; i++ ) {
 			final String naturalIdProperty = propertyNames[naturalIdentifierProperties[i]];
 			final Object naturalIdValue = naturalIdValues.get( naturalIdProperty );
 
 			if ( naturalIdValue == null ) {
 				// A NaturalId property is missing from the critera query, can't use NaturalIdLoadAccess
 				return null;
 			}
 
 			naturalIdLoader.using( naturalIdProperty, naturalIdValue );
 		}
 
 		// Critera query contains a valid naturalId, use the new API
 		LOG.warn( "Session.byNaturalId(" + entityName
 				+ ") should be used for naturalId queries instead of Restrictions.naturalId() from a Criteria" );
 
 		return naturalIdLoader;
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
 		EntityPersister persister = factory.getEntityPersister(entityName);
 		if ( !(persister instanceof OuterJoinLoadable) ) {
 			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
 		}
 		return ( OuterJoinLoadable ) persister;
 	}
 
 	public boolean contains(Object object) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object instanceof HibernateProxy ) {
 			//do not use proxiesByKey, since not all
 			//proxies that point to this session's
 			//instances are in that collection!
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				//if it is an uninitialized proxy, pointing
 				//with this session, then when it is accessed,
 				//the underlying instance will be "contained"
 				return li.getSession()==this;
 			}
 			else {
 				//if it is initialized, see if the underlying
 				//instance is contained, since we need to
 				//account for the fact that it might have been
 				//evicted
 				object = li.getImplementation();
 			}
 		}
 		// A session is considered to contain an entity only if the entity has
 		// an entry in the session's persistence context and the entry reports
 		// that the entity has not been removed
 		EntityEntry entry = persistenceContext.getEntry( object );
 		delayedAfterCompletion();
 		return entry != null && entry.getStatus() != Status.DELETED && entry.getStatus() != Status.GONE;
 	}
 
 	public Query createQuery(String queryString) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createQuery( queryString );
 	}
 
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createSQLQuery( sql );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName, resultSetMappings );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName, resultClasses );
 	}
 
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Scroll SQL query: {0}", customQuery.getSQL() );
 		}
 
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		autoFlushIfRequired( loader.getQuerySpaces() );
 
 		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
 		try {
 			return loader.scroll(queryParameters, this);
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
 	// basically just an adapted copy of find(CriteriaImpl)
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "SQL query: {0}", customQuery.getSQL() );
 		}
 
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		autoFlushIfRequired( loader.getQuerySpaces() );
 
 		dontFlushFromFind++;
 		boolean success = false;
 		try {
 			List results = loader.list(this, queryParameters);
 			success = true;
 			return results;
 		}
 		finally {
 			dontFlushFromFind--;
 			delayedAfterCompletion();
 			afterOperation(success);
 		}
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		checkTransactionSynchStatus();
 		return factory;
 	}
 
 	public void initializeCollection(PersistentCollection collection, boolean writing)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		InitializeCollectionEvent event = new InitializeCollectionEvent( collection, this );
 		for ( InitializeCollectionEventListener listener : listeners( EventType.INIT_COLLECTION ) ) {
 			listener.onInitializeCollection( event );
 		}
 		delayedAfterCompletion();
 	}
 
 	public String bestGuessEntityName(Object object) {
 		if (object instanceof HibernateProxy) {
 			LazyInitializer initializer = ( ( HibernateProxy ) object ).getHibernateLazyInitializer();
 			// it is possible for this method to be called during flush processing,
 			// so make certain that we do not accidentally initialize an uninitialized proxy
 			if ( initializer.isUninitialized() ) {
 				return initializer.getEntityName();
 			}
 			object = initializer.getImplementation();
 		}
 		EntityEntry entry = persistenceContext.getEntry(object);
 		if (entry==null) {
 			return guessEntityName(object);
 		}
 		else {
 			return entry.getPersister().getEntityName();
 		}
 	}
 
 	public String getEntityName(Object object) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if (object instanceof HibernateProxy) {
 			if ( !persistenceContext.containsProxy( object ) ) {
 				throw new TransientObjectException("proxy was not associated with the session");
 			}
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation();
 		}
 
 		EntityEntry entry = persistenceContext.getEntry(object);
 		if ( entry == null ) {
 			throwTransientObjectException( object );
 		}
 		return entry.getPersister().getEntityName();
 	}
 
 	private void throwTransientObjectException(Object object) throws HibernateException {
 		throw new TransientObjectException(
 				"object references an unsaved transient instance - save the transient instance before flushing: " +
 				guessEntityName(object)
 			);
 	}
 
 	public String guessEntityName(Object object) throws HibernateException {
 		errorIfClosed();
 		return entityNameResolver.resolveEntityName( object );
 	}
 
 	public void cancelQuery() throws HibernateException {
 		errorIfClosed();
 		getTransactionCoordinator().getJdbcCoordinator().cancelLastQuery();
 	}
 
 	public Interceptor getInterceptor() {
 		checkTransactionSynchStatus();
 		return interceptor;
 	}
 
 	public int getDontFlushFromFind() {
 		return dontFlushFromFind;
 	}
 
 	public String toString() {
 		StringBuilder buf = new StringBuilder(500)
 			.append( "SessionImpl(" );
 		if ( !isClosed() ) {
 			buf.append(persistenceContext)
 				.append(";")
 				.append(actionQueue);
 		}
 		else {
 			buf.append("<closed>");
 		}
 		return buf.append(')').toString();
 	}
 
 	public ActionQueue getActionQueue() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return actionQueue;
 	}
 
 	public PersistenceContext getPersistenceContext() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext;
 	}
 
 	public SessionStatistics getStatistics() {
 		checkTransactionSynchStatus();
 		return new SessionStatisticsImpl(this);
 	}
 
 	public boolean isEventSource() {
 		checkTransactionSynchStatus();
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isDefaultReadOnly() {
 		return persistenceContext.isDefaultReadOnly();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		persistenceContext.setDefaultReadOnly( defaultReadOnly );
 	}
 
 	public boolean isReadOnly(Object entityOrProxy) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext.isReadOnly( entityOrProxy );
 	}
 
 	public void setReadOnly(Object entity, boolean readOnly) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		persistenceContext.setReadOnly( entity, readOnly );
 	}
 
 	public void doWork(final Work work) throws HibernateException {
 		WorkExecutorVisitable<Void> realWork = new WorkExecutorVisitable<Void>() {
 			@Override
 			public Void accept(WorkExecutor<Void> workExecutor, Connection connection) throws SQLException {
 				workExecutor.executeWork( work, connection );
 				return null;
 			}
 		};
 		doWork( realWork );
 	}
 
 	public <T> T doReturningWork(final ReturningWork<T> work) throws HibernateException {
 		WorkExecutorVisitable<T> realWork = new WorkExecutorVisitable<T>() {
 			@Override
 			public T accept(WorkExecutor<T> workExecutor, Connection connection) throws SQLException {
 				return workExecutor.executeReturningWork( work, connection );
 			}
 		};
 		return doWork( realWork );
 	}
 
 	private <T> T doWork(WorkExecutorVisitable<T> work) throws HibernateException {
 		return transactionCoordinator.getJdbcCoordinator().coordinateWork( work );
 	}
 
 	public void afterScrollOperation() {
 		// nothing to do in a stateful session
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		errorIfClosed();
 		return transactionCoordinator;
 	}
 
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return loadQueryInfluencers;
 	}
 
 	// filter support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Filter getEnabledFilter(String filterName) {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Filter enableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.enableFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void disableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.disableFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object getFilterParameterValue(String filterParameterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getFilterParameterValue( filterParameterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Type getFilterParameterType(String filterParameterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getFilterParameterType( filterParameterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Map getEnabledFilters() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilters();
 	}
 
 
 	// internal fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String getFetchProfile() {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getInternalFetchProfile();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setFetchProfile(String fetchProfile) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.setInternalFetchProfile( fetchProfile );
 	}
 
 
 	// fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		return loadQueryInfluencers.isFetchProfileEnabled( name );
 	}
 
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.enableFetchProfile( name );
 	}
 
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.disableFetchProfile( name );
 	}
 
 	private void checkTransactionSynchStatus() {
 		pulseTransactionCoordinator();
 		delayedAfterCompletion();
 	}
 
 	private void pulseTransactionCoordinator() {
 		if ( !isClosed() ) {
 			transactionCoordinator.pulse();
 		}
 	}
 
 	/**
 	 * Used by JDK serialization...
 	 *
 	 * @param ois The input stream from which we are being read...
 	 * @throws IOException Indicates a general IO stream exception
 	 * @throws ClassNotFoundException Indicates a class resolution issue
 	 */
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		LOG.trace( "Deserializing session" );
 
 		ois.defaultReadObject();
 
 		entityNameResolver = new CoordinatingEntityNameResolver();
 
 		connectionReleaseMode = ConnectionReleaseMode.parse( ( String ) ois.readObject() );
 		autoClear = ois.readBoolean();
 		autoJoinTransactions = ois.readBoolean();
 		flushMode = FlushMode.valueOf( ( String ) ois.readObject() );
 		cacheMode = CacheMode.valueOf( ( String ) ois.readObject() );
 		flushBeforeCompletionEnabled = ois.readBoolean();
 		autoCloseSessionEnabled = ois.readBoolean();
 		interceptor = ( Interceptor ) ois.readObject();
 
 		factory = SessionFactoryImpl.deserialize( ois );
 		sessionOwner = ( SessionOwner ) ois.readObject();
 
 		transactionCoordinator = TransactionCoordinatorImpl.deserialize( ois, this );
 
 		persistenceContext = StatefulPersistenceContext.deserialize( ois, this );
 		actionQueue = ActionQueue.deserialize( ois, this );
 
 		loadQueryInfluencers = (LoadQueryInfluencers) ois.readObject();
 
 		// LoadQueryInfluencers.getEnabledFilters() tries to validate each enabled
 		// filter, which will fail when called before FilterImpl.afterDeserialize( factory );
 		// Instead lookup the filter by name and then call FilterImpl.afterDeserialize( factory ).
 		for ( String filterName : loadQueryInfluencers.getEnabledFilterNames() ) {
 			((FilterImpl) loadQueryInfluencers.getEnabledFilter( filterName )).afterDeserialize( factory );
 		}
 	}
 
 	/**
 	 * Used by JDK serialization...
 	 *
 	 * @param oos The output stream to which we are being written...
 	 * @throws IOException Indicates a general IO stream exception
 	 */
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		if ( ! transactionCoordinator.getJdbcCoordinator().isReadyForSerialization() ) {
 			throw new IllegalStateException( "Cannot serialize a session while connected" );
 		}
 
 		LOG.trace( "Serializing session" );
 
 		oos.defaultWriteObject();
 
 		oos.writeObject( connectionReleaseMode.toString() );
 		oos.writeBoolean( autoClear );
 		oos.writeBoolean( autoJoinTransactions );
 		oos.writeObject( flushMode.toString() );
 		oos.writeObject( cacheMode.name() );
 		oos.writeBoolean( flushBeforeCompletionEnabled );
 		oos.writeBoolean( autoCloseSessionEnabled );
 		// we need to writeObject() on this since interceptor is user defined
 		oos.writeObject( interceptor );
 
 		factory.serialize( oos );
 		oos.writeObject( sessionOwner );
 
 		transactionCoordinator.serialize( oos );
 
 		persistenceContext.serialize( oos );
 		actionQueue.serialize( oos );
 
 		// todo : look at optimizing these...
 		oos.writeObject( loadQueryInfluencers );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public TypeHelper getTypeHelper() {
 		return getSessionFactory().getTypeHelper();
 	}
 
 	@Override
 	public LobHelper getLobHelper() {
 		if ( lobHelper == null ) {
 			lobHelper = new LobHelperImpl( this );
 		}
 		return lobHelper;
 	}
 
 	private transient LobHelperImpl lobHelper;
 
 	private static class LobHelperImpl implements LobHelper {
 		private final SessionImpl session;
 
 		private LobHelperImpl(SessionImpl session) {
 			this.session = session;
 		}
 
 		@Override
 		public Blob createBlob(byte[] bytes) {
 			return lobCreator().createBlob( bytes );
 		}
 
 		private LobCreator lobCreator() {
 			// Always use NonContextualLobCreator.  If ContextualLobCreator is
 			// used both here and in WrapperOptions, 
 			return NonContextualLobCreator.INSTANCE;
 		}
 
 		@Override
 		public Blob createBlob(InputStream stream, long length) {
 			return lobCreator().createBlob( stream, length );
 		}
 
 		@Override
 		public Clob createClob(String string) {
 			return lobCreator().createClob( string );
 		}
 
 		@Override
 		public Clob createClob(Reader reader, long length) {
 			return lobCreator().createClob( reader, length );
 		}
 
 		@Override
 		public NClob createNClob(String string) {
 			return lobCreator().createNClob( string );
 		}
 
 		@Override
 		public NClob createNClob(Reader reader, long length) {
 			return lobCreator().createNClob( reader, length );
 		}
 	}
 
 	private static class SharedSessionBuilderImpl extends SessionFactoryImpl.SessionBuilderImpl implements SharedSessionBuilder {
 		private final SessionImpl session;
 		private boolean shareTransactionContext;
 
 		private SharedSessionBuilderImpl(SessionImpl session) {
 			super( session.factory );
 			this.session = session;
 			super.owner( session.sessionOwner );
 			super.tenantIdentifier( session.getTenantIdentifier() );
 		}
 
 		@Override
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			// todo : is this always true?  Or just in the case of sharing JDBC resources?
 			throw new SessionException( "Cannot redefine tenant identifier on child session" );
 		}
 
 		@Override
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return shareTransactionContext ? session.transactionCoordinator : super.getTransactionCoordinator();
 		}
 
 		@Override
 		public SharedSessionBuilder interceptor() {
 			return interceptor( session.interceptor );
 		}
 
 		@Override
 		public SharedSessionBuilder connection() {
 			this.shareTransactionContext = true;
 			return this;
 		}
 
 		@Override
 		public SharedSessionBuilder connectionReleaseMode() {
 			return connectionReleaseMode( session.connectionReleaseMode );
 		}
 
 		@Override
 		public SharedSessionBuilder autoJoinTransactions() {
 			return autoJoinTransactions( session.autoJoinTransactions );
 		}
 
 		@Override
 		public SharedSessionBuilder autoClose() {
 			return autoClose( session.autoCloseSessionEnabled );
 		}
 
 		@Override
 		public SharedSessionBuilder flushBeforeCompletion() {
 			return flushBeforeCompletion( session.flushBeforeCompletionEnabled );
 		}
 
 		/**
 		 * @deprecated Use {@link #connection()} instead
 		 */
 		@Override
 		@Deprecated
 		public SharedSessionBuilder transactionContext() {
 			return connection();
 		}
 
 		@Override
 		public SharedSessionBuilder interceptor(Interceptor interceptor) {
 			return (SharedSessionBuilder) super.interceptor( interceptor );
 		}
 
 		@Override
 		public SharedSessionBuilder noInterceptor() {
 			return (SharedSessionBuilder) super.noInterceptor();
 		}
 
 		@Override
 		public SharedSessionBuilder connection(Connection connection) {
 			return (SharedSessionBuilder) super.connection( connection );
 		}
 
 		@Override
 		public SharedSessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 			return (SharedSessionBuilder) super.connectionReleaseMode( connectionReleaseMode );
 		}
 
 		@Override
 		public SharedSessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
 			return (SharedSessionBuilder) super.autoJoinTransactions( autoJoinTransactions );
 		}
 
 		@Override
 		public SharedSessionBuilder autoClose(boolean autoClose) {
 			return (SharedSessionBuilder) super.autoClose( autoClose );
 		}
 
 		@Override
 		public SharedSessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
 			return (SharedSessionBuilder) super.flushBeforeCompletion( flushBeforeCompletion );
 		}
 	}
 
 	private class CoordinatingEntityNameResolver implements EntityNameResolver {
 		public String resolveEntityName(Object entity) {
 			String entityName = interceptor.getEntityName( entity );
 			if ( entityName != null ) {
 				return entityName;
 			}
 
 			for ( EntityNameResolver resolver : factory.iterateEntityNameResolvers() ) {
 				entityName = resolver.resolveEntityName( entity );
 				if ( entityName != null ) {
 					break;
 				}
 			}
 
 			if ( entityName != null ) {
 				return entityName;
 			}
 
 			// the old-time stand-by...
 			return entity.getClass().getName();
 		}
 	}
 
 	private class LockRequestImpl implements LockRequest {
 		private final LockOptions lockOptions;
 		private LockRequestImpl(LockOptions lo) {
 			lockOptions = new LockOptions();
 			LockOptions.copy(lo, lockOptions);
 		}
 
 		public LockMode getLockMode() {
 			return lockOptions.getLockMode();
 		}
 
 		public LockRequest setLockMode(LockMode lockMode) {
 			lockOptions.setLockMode(lockMode);
 			return this;
 		}
 
 		public int getTimeOut() {
 			return lockOptions.getTimeOut();
 		}
 
 		public LockRequest setTimeOut(int timeout) {
 			lockOptions.setTimeOut(timeout);
 			return this;
 		}
 
 		public boolean getScope() {
 			return lockOptions.getScope();
 		}
 
 		public LockRequest setScope(boolean scope) {
 			lockOptions.setScope(scope);
 			return this;
 		}
 
 		public void lock(String entityName, Object object) throws HibernateException {
 			fireLock( entityName, object, lockOptions );
 		}
 		public void lock(Object object) throws HibernateException {
 			fireLock( object, lockOptions );
 		}
 	}
 
 	private class IdentifierLoadAccessImpl implements IdentifierLoadAccess {
 		private final EntityPersister entityPersister;
 		private LockOptions lockOptions;
 
 		private IdentifierLoadAccessImpl(EntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 		}
 
 		private IdentifierLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private IdentifierLoadAccessImpl(Class entityClass) {
 			this( entityClass.getName() );
 		}
 
 		@Override
 		public final IdentifierLoadAccessImpl with(LockOptions lockOptions) {
 			this.lockOptions = lockOptions;
 			return this;
 		}
 
 		@Override
 		public final Object getReference(Serializable id) {
 			if ( this.lockOptions != null ) {
 				LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), lockOptions, SessionImpl.this );
 				fireLoad( event, LoadEventListener.LOAD );
 				return event.getResult();
 			}
 
 			LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), false, SessionImpl.this );
 			boolean success = false;
 			try {
 				fireLoad( event, LoadEventListener.LOAD );
 				if ( event.getResult() == null ) {
 					getFactory().getEntityNotFoundDelegate().handleEntityNotFound( entityPersister.getEntityName(), id );
 				}
 				success = true;
 				return event.getResult();
 			}
 			finally {
 				afterOperation( success );
 			}
 		}
 
 		@Override
 		public final Object load(Serializable id) {
 			if ( this.lockOptions != null ) {
 				LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), lockOptions, SessionImpl.this );
 				fireLoad( event, LoadEventListener.GET );
 				return event.getResult();
 			}
 
 			LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), false, SessionImpl.this );
 			boolean success = false;
 			try {
 				fireLoad( event, LoadEventListener.GET );
 				success = true;
 				return event.getResult();
 			}
 			finally {
 				afterOperation( success );
 			}
 		}
 	}
 
 	private EntityPersister locateEntityPersister(String entityName) {
 		final EntityPersister entityPersister = factory.getEntityPersister( entityName );
 		if ( entityPersister == null ) {
 			throw new HibernateException( "Unable to locate persister: " + entityName );
 		}
 		return entityPersister;
 	}
 
 	private abstract class BaseNaturalIdLoadAccessImpl  {
 		private final EntityPersister entityPersister;
 		private LockOptions lockOptions;
 		private boolean synchronizationEnabled = true;
 
 		private BaseNaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 
 			if ( ! entityPersister.hasNaturalIdentifier() ) {
 				throw new HibernateException(
 						String.format( "Entity [%s] did not define a natural id", entityPersister.getEntityName() )
 				);
 			}
 		}
 
 		private BaseNaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private BaseNaturalIdLoadAccessImpl(Class entityClass) {
 			this( entityClass.getName() );
 		}
 
 		public BaseNaturalIdLoadAccessImpl with(LockOptions lockOptions) {
 			this.lockOptions = lockOptions;
 			return this;
 		}
 
 		protected void synchronizationEnabled(boolean synchronizationEnabled) {
 			this.synchronizationEnabled = synchronizationEnabled;
 		}
 
 		protected final Serializable resolveNaturalId(Map<String, Object> naturalIdParameters) {
 			performAnyNeededCrossReferenceSynchronizations();
 
 			final ResolveNaturalIdEvent event =
 					new ResolveNaturalIdEvent( naturalIdParameters, entityPersister, SessionImpl.this );
 			fireResolveNaturalId( event );
 
 			if ( event.getEntityId() == PersistenceContext.NaturalIdHelper.INVALID_NATURAL_ID_REFERENCE ) {
 				return null;
 			}
 			else {
 				return event.getEntityId();
 			}
 		}
 
 		protected void performAnyNeededCrossReferenceSynchronizations() {
 			if ( ! synchronizationEnabled ) {
 				// synchronization (this process) was disabled
 				return;
 			}
 			if ( entityPersister.getEntityMetamodel().hasImmutableNaturalId() ) {
 				// only mutable natural-ids need this processing
 				return;
 			}
 			if ( ! isTransactionInProgress() ) {
 				// not in a transaction so skip synchronization
 				return;
 			}
 
+			final boolean debugEnabled = LOG.isDebugEnabled();
 			for ( Serializable pk : getPersistenceContext().getNaturalIdHelper().getCachedPkResolutions( entityPersister ) ) {
 				final EntityKey entityKey = generateEntityKey( pk, entityPersister );
 				final Object entity = getPersistenceContext().getEntity( entityKey );
 				final EntityEntry entry = getPersistenceContext().getEntry( entity );
 
 				if ( entry == null ) {
-					if ( LOG.isDebugEnabled() ) {
+					if ( debugEnabled ) {
 						LOG.debug(
 								"Cached natural-id/pk resolution linked to null EntityEntry in persistence context : "
 										+ MessageHelper.infoString( entityPersister, pk, getFactory() )
 						);
 					}
 					continue;
 				}
 
 				if ( !entry.requiresDirtyCheck( entity ) ) {
 					continue;
 				}
 
 				// MANAGED is the only status we care about here...
 				if ( entry.getStatus() != Status.MANAGED ) {
 					continue;
 				}
 
 				getPersistenceContext().getNaturalIdHelper().handleSynchronization(
 						entityPersister,
 						pk,
 						entity
 				);
 			}
 		}
 
 		protected final IdentifierLoadAccess getIdentifierLoadAccess() {
 			final IdentifierLoadAccessImpl identifierLoadAccess = new IdentifierLoadAccessImpl( entityPersister );
 			if ( this.lockOptions != null ) {
 				identifierLoadAccess.with( lockOptions );
 			}
 			return identifierLoadAccess;
 		}
 
 		protected EntityPersister entityPersister() {
 			return entityPersister;
 		}
 	}
 
 	private class NaturalIdLoadAccessImpl extends BaseNaturalIdLoadAccessImpl implements NaturalIdLoadAccess {
 		private final Map<String, Object> naturalIdParameters = new LinkedHashMap<String, Object>();
 
 		private NaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			super(entityPersister);
 		}
 
 		private NaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private NaturalIdLoadAccessImpl(Class entityClass) {
 			this( entityClass.getName() );
 		}
 		
 		@Override
 		public NaturalIdLoadAccessImpl with(LockOptions lockOptions) {
 			return (NaturalIdLoadAccessImpl) super.with( lockOptions );
 		}
 
 		@Override
 		public NaturalIdLoadAccess using(String attributeName, Object value) {
 			naturalIdParameters.put( attributeName, value );
 			return this;
 		}
 
 		@Override
 		public NaturalIdLoadAccessImpl setSynchronizationEnabled(boolean synchronizationEnabled) {
 			super.synchronizationEnabled( synchronizationEnabled );
 			return this;
 		}
 
 		@Override
 		public final Object getReference() {
 			final Serializable entityId = resolveNaturalId( this.naturalIdParameters );
 			if ( entityId == null ) {
 				return null;
 			}
 			return this.getIdentifierLoadAccess().getReference( entityId );
 		}
 
 		@Override
 		public final Object load() {
 			final Serializable entityId = resolveNaturalId( this.naturalIdParameters );
 			if ( entityId == null ) {
 				return null;
 			}
 			try {
 				return this.getIdentifierLoadAccess().load( entityId );
 			}
 			catch (EntityNotFoundException enf) {
 				// OK
 			}
 			catch (ObjectNotFoundException nf) {
 				// OK
 			}
 			return null;
 		}
 	}
 
 	private class SimpleNaturalIdLoadAccessImpl extends BaseNaturalIdLoadAccessImpl implements SimpleNaturalIdLoadAccess {
 		private final String naturalIdAttributeName;
 
 		private SimpleNaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			super(entityPersister);
 
 			if ( entityPersister.getNaturalIdentifierProperties().length != 1 ) {
 				throw new HibernateException(
 						String.format( "Entity [%s] did not define a simple natural id", entityPersister.getEntityName() )
 				);
 			}
 
 			final int naturalIdAttributePosition = entityPersister.getNaturalIdentifierProperties()[0];
 			this.naturalIdAttributeName = entityPersister.getPropertyNames()[ naturalIdAttributePosition ];
 		}
 
 		private SimpleNaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private SimpleNaturalIdLoadAccessImpl(Class entityClass) {
 			this( entityClass.getName() );
 		}
 
 		@Override
 		public final SimpleNaturalIdLoadAccessImpl with(LockOptions lockOptions) {
 			return (SimpleNaturalIdLoadAccessImpl) super.with( lockOptions );
 		}
 		
 		private Map<String, Object> getNaturalIdParameters(Object naturalIdValue) {
 			return Collections.singletonMap( naturalIdAttributeName, naturalIdValue );
 		}
 
 		@Override
 		public SimpleNaturalIdLoadAccessImpl setSynchronizationEnabled(boolean synchronizationEnabled) {
 			super.synchronizationEnabled( synchronizationEnabled );
 			return this;
 		}
 
 		@Override
 		public Object getReference(Object naturalIdValue) {
 			final Serializable entityId = resolveNaturalId( getNaturalIdParameters( naturalIdValue ) );
 			if ( entityId == null ) {
 				return null;
 			}
 			return this.getIdentifierLoadAccess().getReference( entityId );
 		}
 
 		@Override
 		public Object load(Object naturalIdValue) {
 			final Serializable entityId = resolveNaturalId( getNaturalIdParameters( naturalIdValue ) );
 			if ( entityId == null ) {
 				return null;
 			}
 			try {
 				return this.getIdentifierLoadAccess().load( entityId );
 			}
 			catch (EntityNotFoundException enf) {
 				// OK
 			}
 			catch (ObjectNotFoundException nf) {
 				// OK
 			}
 			return null;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 1430604082..0b78c1bd63 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -349,2010 +349,2010 @@ public abstract class Loader {
 			persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 		}
 		return result;
 	}
 
 	/**
 	 * Loads a single row from the result set.  This is the processing used from the
 	 * ScrollableResults where no collection fetches were encountered.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSingleRow(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		final Object result;
 		try {
 			result = getRowFromResultSet(
 			        resultSet,
 					session,
 					queryParameters,
 					getLockModes( queryParameters.getLockOptions() ),
 					null,
 					hydratedObjects,
 					new EntityKey[entitySpan],
 					returnProxies
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not read next row of results",
 			        getSQLString()
 				);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 		);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	private Object sequentialLoad(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies,
 	        final EntityKey keyToRead) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		Object result = null;
 		final EntityKey[] loadedKeys = new EntityKey[entitySpan];
 
 		try {
 			do {
 				Object loaded = getRowFromResultSet(
 						resultSet,
 						session,
 						queryParameters,
 						getLockModes( queryParameters.getLockOptions() ),
 						null,
 						hydratedObjects,
 						loadedKeys,
 						returnProxies
 				);
 				if ( ! keyToRead.equals( loadedKeys[0] ) ) {
 					throw new AssertionFailure(
 							String.format(
 									"Unexpected key read for row; expected [%s]; actual [%s]",
 									keyToRead,
 									loadedKeys[0] )
 					);
 				}
 				if ( result == null ) {
 					result = loaded;
 				}
 			}
 			while ( resultSet.next() &&
 					isCurrentRowForSameEntity( keyToRead, 0, resultSet, session ) );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 		);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	private boolean isCurrentRowForSameEntity(
 			final EntityKey keyToRead,
 			final int persisterIndex,
 			final ResultSet resultSet,
 			final SessionImplementor session) throws SQLException {
 		EntityKey currentRowKey = getKeyFromResultSet(
 				persisterIndex, getEntityPersisters()[persisterIndex], null, resultSet, session
 		);
 		return keyToRead.equals( currentRowKey );
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsForward(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isAfterLast() ) {
 				// don't even bother trying to read further
 				return null;
 			}
 
 			if ( resultSet.isBeforeFirst() ) {
 				resultSet.next();
 			}
 
 			// We call getKeyFromResultSet() here so that we can know the
 			// key value upon which to perform the breaking logic.  However,
 			// it is also then called from getRowFromResultSet() which is certainly
 			// not the most efficient.  But the call here is needed, and there
 			// currently is no other way without refactoring of the doQuery()/getRowFromResultSet()
 			// methods
 			final EntityKey currentKey = getKeyFromResultSet(
 					0,
 					getEntityPersisters()[0],
 					null,
 					resultSet,
 					session
 				);
 
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, currentKey );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not perform sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsReverse(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies,
 	        final boolean isLogicallyAfterLast) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isFirst() ) {
 				// don't even bother trying to read any further
 				return null;
 			}
 
 			EntityKey keyToRead = null;
 			// This check is needed since processing leaves the cursor
 			// after the last physical row for the current logical row;
 			// thus if we are after the last physical row, this might be
 			// caused by either:
 			//      1) scrolling to the last logical row
 			//      2) scrolling past the last logical row
 			// In the latter scenario, the previous logical row
 			// really is the last logical row.
 			//
 			// In all other cases, we should process back two
 			// logical records (the current logic row, plus the
 			// previous logical row).
 			if ( resultSet.isAfterLast() && isLogicallyAfterLast ) {
 				// position cursor to the last row
 				resultSet.last();
 				keyToRead = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 			}
 			else {
 				// Since the result set cursor is always left at the first
 				// physical row after the "last processed", we need to jump
 				// back one position to get the key value we are interested
 				// in skipping
 				resultSet.previous();
 
 				// sequentially read the result set in reverse until we recognize
 				// a change in the key value.  At that point, we are pointed at
 				// the last physical sequential row for the logical row in which
 				// we are interested in processing
 				boolean firstPass = true;
 				final EntityKey lastKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 				while ( resultSet.previous() ) {
 					EntityKey checkKey = getKeyFromResultSet(
 							0,
 							getEntityPersisters()[0],
 							null,
 							resultSet,
 							session
 						);
 
 					if ( firstPass ) {
 						firstPass = false;
 						keyToRead = checkKey;
 					}
 
 					if ( !lastKey.equals( checkKey ) ) {
 						break;
 					}
 				}
 
 			}
 
 			// Read backwards until we read past the first physical sequential
 			// row with the key we are interested in loading
 			while ( resultSet.previous() ) {
 				EntityKey checkKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 
 				if ( !keyToRead.equals( checkKey ) ) {
 					break;
 				}
 			}
 
 			// Finally, read ahead one row to position result set cursor
 			// at the first physical row we are interested in loading
 			resultSet.next();
 
 			// and doAfterTransactionCompletion the load
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, keyToRead );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 	}
 
 	private static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SessionImplementor session) {
 		final Object optionalObject = queryParameters.getOptionalObject();
 		final Serializable optionalId = queryParameters.getOptionalId();
 		final String optionalEntityName = queryParameters.getOptionalEntityName();
 
 		if ( optionalObject != null && optionalEntityName != null ) {
 			return session.generateEntityKey( optionalId, session.getEntityPersister( optionalEntityName, optionalObject ) );
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private Object getRowFromResultSet(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final LockMode[] lockModesArray,
 	        final EntityKey optionalObjectKey,
 	        final List hydratedObjects,
 	        final EntityKey[] keys,
 	        boolean returnProxies) throws SQLException, HibernateException {
 		return getRowFromResultSet(
 				resultSet,
 				session,
 				queryParameters,
 				lockModesArray,
 				optionalObjectKey,
 				hydratedObjects,
 				keys,
 				returnProxies,
 				null
 		);
 	}
 
 	private Object getRowFromResultSet(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final LockMode[] lockModesArray,
 	        final EntityKey optionalObjectKey,
 	        final List hydratedObjects,
 	        final EntityKey[] keys,
 	        boolean returnProxies,
 	        ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 		final Loadable[] persisters = getEntityPersisters();
 		final int entitySpan = persisters.length;
 		extractKeysFromResultSet( persisters, queryParameters, resultSet, session, keys, lockModesArray, hydratedObjects );
 
 		registerNonExists( keys, persisters, session );
 
 		// this call is side-effecty
 		Object[] row = getRow(
 		        resultSet,
 				persisters,
 				keys,
 				queryParameters.getOptionalObject(),
 				optionalObjectKey,
 				lockModesArray,
 				hydratedObjects,
 				session
 		);
 
 		readCollectionElements( row, resultSet, session );
 
 		if ( returnProxies ) {
 			// now get an existing proxy for each row element (if there is one)
 			for ( int i = 0; i < entitySpan; i++ ) {
 				Object entity = row[i];
 				Object proxy = session.getPersistenceContext().proxyFor( persisters[i], keys[i], entity );
 				if ( entity != proxy ) {
 					// force the proxy to resolve itself
 					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setImplementation(entity);
 					row[i] = proxy;
 				}
 			}
 		}
 
 		applyPostLoadLocks( row, lockModesArray, session );
 
 		return forcedResultTransformer == null
 				? getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session )
 				: forcedResultTransformer.transformTuple( getResultRow( row, resultSet, session ), getResultRowAliases() )
 		;
 	}
 
 	protected void extractKeysFromResultSet(
 			Loadable[] persisters,
 			QueryParameters queryParameters,
 			ResultSet resultSet,
 			SessionImplementor session,
 			EntityKey[] keys,
 			LockMode[] lockModes,
 			List hydratedObjects) throws SQLException {
 		final int entitySpan = persisters.length;
 
 		final int numberOfPersistersToProcess;
 		final Serializable optionalId = queryParameters.getOptionalId();
 		if ( isSingleRowLoader() && optionalId != null ) {
 			keys[ entitySpan - 1 ] = session.generateEntityKey( optionalId, persisters[ entitySpan - 1 ] );
 			// skip the last persister below...
 			numberOfPersistersToProcess = entitySpan - 1;
 		}
 		else {
 			numberOfPersistersToProcess = entitySpan;
 		}
 
 		final Object[] hydratedKeyState = new Object[numberOfPersistersToProcess];
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			hydratedKeyState[i] = idType.hydrate( resultSet, getEntityAliases()[i].getSuffixedKeyAliases(), session, null );
 		}
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			if ( idType.isComponentType() && getCompositeKeyManyToOneTargetIndices() != null ) {
 				// we may need to force resolve any key-many-to-one(s)
 				int[] keyManyToOneTargetIndices = getCompositeKeyManyToOneTargetIndices()[i];
 				// todo : better solution is to order the index processing based on target indices
 				//		that would account for multiple levels whereas this scheme does not
 				if ( keyManyToOneTargetIndices != null ) {
 					for ( int targetIndex : keyManyToOneTargetIndices ) {
 						if ( targetIndex < numberOfPersistersToProcess ) {
 							final Type targetIdType = persisters[targetIndex].getIdentifierType();
 							final Serializable targetId = (Serializable) targetIdType.resolve(
 									hydratedKeyState[targetIndex],
 									session,
 									null
 							);
 							// todo : need a way to signal that this key is resolved and its data resolved
 							keys[targetIndex] = session.generateEntityKey( targetId, persisters[targetIndex] );
 						}
 
 						// this part copied from #getRow, this section could be refactored out
 						Object object = session.getEntityUsingInterceptor( keys[targetIndex] );
 						if ( object != null ) {
 							//its already loaded so don't need to hydrate it
 							instanceAlreadyLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									keys[targetIndex],
 									object,
 									lockModes[targetIndex],
 									session
 							);
 						}
 						else {
 							instanceNotYetLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									getEntityAliases()[targetIndex].getRowIdAlias(),
 									keys[targetIndex],
 									lockModes[targetIndex],
 									getOptionalObjectKey( queryParameters, session ),
 									queryParameters.getOptionalObject(),
 									hydratedObjects,
 									session
 							);
 						}
 					}
 				}
 			}
 			final Serializable resolvedId = (Serializable) idType.resolve( hydratedKeyState[i], session, null );
 			keys[i] = resolvedId == null ? null : session.generateEntityKey( resolvedId, persisters[i] );
 		}
 	}
 
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SessionImplementor session) {
 	}
 
 	/**
 	 * Read any collection elements contained in a single row of the result set
 	 */
 	private void readCollectionElements(Object[] row, ResultSet resultSet, SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		//TODO: make this handle multiple collection roles!
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 
 			final CollectionAliases[] descriptors = getCollectionAliases();
 			final int[] collectionOwners = getCollectionOwners();
 
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 
 				final boolean hasCollectionOwners = collectionOwners !=null &&
 						collectionOwners[i] > -1;
 				//true if this is a query and we are loading multiple instances of the same collection role
 				//otherwise this is a CollectionInitializer and we are loading up a single collection or batch
 
 				final Object owner = hasCollectionOwners ?
 						row[ collectionOwners[i] ] :
 						null; //if null, owner will be retrieved from session
 
 				final CollectionPersister collectionPersister = collectionPersisters[i];
 				final Serializable key;
 				if ( owner == null ) {
 					key = null;
 				}
 				else {
 					key = collectionPersister.getCollectionType().getKeyOfOwner( owner, session );
 					//TODO: old version did not require hashmap lookup:
 					//keys[collectionOwner].getIdentifier()
 				}
 
 				readCollectionElement(
 						owner,
 						key,
 						collectionPersister,
 						descriptors[i],
 						resultSet,
 						session
 					);
 
 			}
 
 		}
 	}
 
 	private List doQuery(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 
 		final RowSelection selection = queryParameters.getRowSelection();
 		final int maxRows = LimitHelper.hasMaxRows( selection ) ?
 				selection.getMaxRows() :
 				Integer.MAX_VALUE;
 
 		final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 
 		final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, false, afterLoadActions, session );
 		final ResultSet rs = wrapper.getResultSet();
 		final Statement st = wrapper.getStatement();
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		try {
 			return processResultSet( rs, queryParameters, session, returnProxies, forcedResultTransformer, maxRows, afterLoadActions );
 		}
 		finally {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 		}
 
 	}
 
 	protected List processResultSet(
 			ResultSet rs,
 			QueryParameters queryParameters,
 			SessionImplementor session,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer,
 			int maxRows,
 			List<AfterLoadAction> afterLoadActions) throws SQLException {
 		final int entitySpan = getEntityPersisters().length;
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
 		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final List results = new ArrayList();
 
 		handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
 		EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
 		LOG.trace( "Processing result set" );
 		int count;
 		boolean isDebugEnabled = LOG.isDebugEnabled();
 		for ( count = 0; count < maxRows && rs.next(); count++ ) {
 		   if ( isDebugEnabled ) 
 			   LOG.debugf( "Result set row: %s", count );
 			Object result = getRowFromResultSet(
 					rs,
 					session,
 					queryParameters,
 					lockModesArray,
 					optionalObjectKey,
 					hydratedObjects,
 					keys,
 					returnProxies,
 					forcedResultTransformer
 			);
 			results.add( result );
 			if ( createSubselects ) {
 				subselectResultKeys.add(keys);
 				keys = new EntityKey[entitySpan]; //can't reuse in this case
 			}
 		}
 
 		if ( LOG.isTraceEnabled() )
 		   LOG.tracev( "Done processing result set ({0} rows)", count );
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				rs,
 				session,
 				queryParameters.isReadOnly( session ),
 				afterLoadActions
 		);
 		if ( createSubselects ) {
 			createSubselects( subselectResultKeys, queryParameters, session );
 		}
 		return results;
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return false;
 	}
 
 	protected boolean hasSubselectLoadableCollections() {
 		final Loadable[] loadables = getEntityPersisters();
 		for (int i=0; i<loadables.length; i++ ) {
 			if ( loadables[i].hasSubselectLoadableCollections() ) return true;
 		}
 		return false;
 	}
 
 	private static Set[] transpose( List keys ) {
 		Set[] result = new Set[ ( ( EntityKey[] ) keys.get(0) ).length ];
 		for ( int j=0; j<result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( int i=0; i<keys.size(); i++ ) {
 				result[j].add( ( ( EntityKey[] ) keys.get(i) ) [j] );
 			}
 		}
 		return result;
 	}
 
 	private void createSubselects(List keys, QueryParameters queryParameters, SessionImplementor session) {
 		if ( keys.size() > 1 ) { //if we only returned one entity, query by key is more efficient
 
 			Set[] keySets = transpose(keys);
 
 			Map namedParameterLocMap = buildNamedParameterLocMap( queryParameters );
 
 			final Loadable[] loadables = getEntityPersisters();
 			final String[] aliases = getAliases();
 			final Iterator iter = keys.iterator();
 			while ( iter.hasNext() ) {
 
 				final EntityKey[] rowKeys = (EntityKey[]) iter.next();
 				for ( int i=0; i<rowKeys.length; i++ ) {
 
 					if ( rowKeys[i]!=null && loadables[i].hasSubselectLoadableCollections() ) {
 
 						SubselectFetch subselectFetch = new SubselectFetch(
 								//getSQLString(),
 								aliases[i],
 								loadables[i],
 								queryParameters,
 								keySets[i],
 								namedParameterLocMap
 							);
 
 						session.getPersistenceContext()
 								.getBatchFetchQueue()
 								.addSubselect( rowKeys[i], subselectFetch );
 					}
 
 				}
 
 			}
 		}
 	}
 
 	private Map buildNamedParameterLocMap(QueryParameters queryParameters) {
 		if ( queryParameters.getNamedParameters()!=null ) {
 			final Map namedParameterLocMap = new HashMap();
 			Iterator piter = queryParameters.getNamedParameters().keySet().iterator();
 			while ( piter.hasNext() ) {
 				String name = (String) piter.next();
 				namedParameterLocMap.put(
 						name,
 						getNamedParameterLocs(name)
 					);
 			}
 			return namedParameterLocMap;
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SessionImplementor session,
 			final boolean readOnly) throws HibernateException {
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSetId,
 				session,
 				readOnly,
 				Collections.<AfterLoadAction>emptyList()
 		);
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SessionImplementor session,
 			final boolean readOnly,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( collectionPersisters[i].isArray() ) {
 					//for arrays, we should end the collection load before resolving
 					//the entities, since the actual array instances are not instantiated
 					//during loading
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
 				}
 			}
 		}
 
 		//important: reuse the same event instances for performance!
 		final PreLoadEvent pre;
 		final PostLoadEvent post;
 		if ( session.isEventSource() ) {
 			pre = new PreLoadEvent( (EventSource) session );
 			post = new PostLoadEvent( (EventSource) session );
 		}
 		else {
 			pre = null;
 			post = null;
 		}
 
 		if ( hydratedObjects!=null ) {
 			int hydratedObjectsSize = hydratedObjects.size();
 			if ( LOG.isTraceEnabled() )
 			   LOG.tracev( "Total objects hydrated: {0}", hydratedObjectsSize );
 			for ( int i = 0; i < hydratedObjectsSize; i++ ) {
 				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre, post );
 			}
 		}
 
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( !collectionPersisters[i].isArray() ) {
 					//for sets, we should end the collection load after resolving
 					//the entities, since we might call hashCode() on the elements
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
 				}
 			}
 		}
 		
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur after
 		// endCollectionLoad to ensure the collection is in the
 		// persistence context.
 		if ( hydratedObjects != null ) {
 			for ( Object hydratedObject : hydratedObjects ) {
 				TwoPhaseLoad.postLoad( hydratedObject, session, post );
 				if ( afterLoadActions != null ) {
 					for ( AfterLoadAction afterLoadAction : afterLoadActions ) {
 						final EntityEntry entityEntry = session.getPersistenceContext().getEntry( hydratedObject );
 						if ( entityEntry == null ) {
 							// big problem
 							throw new HibernateException( "Could not locate EntityEntry immediately after two-phase load" );
 						}
 						afterLoadAction.afterLoad( session, hydratedObject, (Loadable) entityEntry.getPersister() );
 					}
 				}
 			}
 		}
 	}
 
 	private void endCollectionLoad(
 			final Object resultSetId,
 			final SessionImplementor session,
 			final CollectionPersister collectionPersister) {
 		//this is a query and we are loading multiple instances of the same collection role
 		session.getPersistenceContext()
 				.getLoadContexts()
 				.getCollectionLoadContext( ( ResultSet ) resultSetId )
 				.endLoadingCollections( collectionPersister );
 	}
 
 	/**
 	 * Determine the actual ResultTransformer that will be used to
 	 * transform query results.
 	 *
 	 * @param resultTransformer the specified result transformer
 	 * @return the actual result transformer
 	 */
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return resultTransformer;
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		return results;
 	}
 
 	/**
 	 * Are rows transformed immediately after being read from the ResultSet?
 	 * @return true, if getResultColumnOrRow() transforms the results; false, otherwise
 	 */
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return false;
 	}
 
 	/**
 	 * Returns the aliases that corresponding to a result row.
 	 * @return Returns the aliases that corresponding to a result row.
 	 */
 	protected String[] getResultRowAliases() {
 		 return null;
 	}
 
 	/**
 	 * Get the actual object that is returned in the user-visible result list.
 	 * This empty implementation merely returns its first argument. This is
 	 * overridden by some subclasses.
 	 */
 	protected Object getResultColumnOrRow(
 			Object[] row,
 			ResultTransformer transformer,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(
 			Object[] row,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	/**
 	 * For missing objects associated by one-to-one with another object in the
 	 * result set, register the fact that the the object is missing with the
 	 * session.
 	 */
 	private void registerNonExists(
 	        final EntityKey[] keys,
 	        final Loadable[] persisters,
 	        final SessionImplementor session) {
 
 		final int[] owners = getOwners();
 		if ( owners != null ) {
 
 			EntityType[] ownerAssociationTypes = getOwnerAssociationTypes();
 			for ( int i = 0; i < keys.length; i++ ) {
 
 				int owner = owners[i];
 				if ( owner > -1 ) {
 					EntityKey ownerKey = keys[owner];
 					if ( keys[i] == null && ownerKey != null ) {
 
 						final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 						/*final boolean isPrimaryKey;
 						final boolean isSpecialOneToOne;
 						if ( ownerAssociationTypes == null || ownerAssociationTypes[i] == null ) {
 							isPrimaryKey = true;
 							isSpecialOneToOne = false;
 						}
 						else {
 							isPrimaryKey = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName()==null;
 							isSpecialOneToOne = ownerAssociationTypes[i].getLHSPropertyName()!=null;
 						}*/
 
 						//TODO: can we *always* use the "null property" approach for everything?
 						/*if ( isPrimaryKey && !isSpecialOneToOne ) {
 							persistenceContext.addNonExistantEntityKey(
 									new EntityKey( ownerKey.getIdentifier(), persisters[i], session.getEntityMode() )
 							);
 						}
 						else if ( isSpecialOneToOne ) {*/
 						boolean isOneToOneAssociation = ownerAssociationTypes!=null &&
 								ownerAssociationTypes[i]!=null &&
 								ownerAssociationTypes[i].isOneToOne();
 						if ( isOneToOneAssociation ) {
 							persistenceContext.addNullProperty( ownerKey,
 									ownerAssociationTypes[i].getPropertyName() );
 						}
 						/*}
 						else {
 							persistenceContext.addNonExistantEntityUniqueKey( new EntityUniqueKey(
 									persisters[i].getEntityName(),
 									ownerAssociationTypes[i].getRHSUniqueKeyPropertyName(),
 									ownerKey.getIdentifier(),
 									persisters[owner].getIdentifierType(),
 									session.getEntityMode()
 							) );
 						}*/
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Read one collection element from the current row of the JDBC result set
 	 */
 	private void readCollectionElement(
 	        final Object optionalOwner,
 	        final Serializable optionalKey,
 	        final CollectionPersister persister,
 	        final CollectionAliases descriptor,
 	        final ResultSet rs,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 		final Serializable collectionRowKey = (Serializable) persister.readKey(
 				rs,
 				descriptor.getSuffixedKeyAliases(),
 				session
 			);
 
 		if ( collectionRowKey != null ) {
 			// we found a collection element in the result set
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Found row of collection: %s",
 						MessageHelper.collectionInfoString( persister, collectionRowKey, getFactory() ) );
 			}
 
 			Object owner = optionalOwner;
 			if ( owner == null ) {
 				owner = persistenceContext.getCollectionOwner( collectionRowKey, persister );
 				if ( owner == null ) {
 					//TODO: This is assertion is disabled because there is a bug that means the
 					//	  original owner of a transient, uninitialized collection is not known
 					//	  if the collection is re-referenced by a different object associated
 					//	  with the current Session
 					//throw new AssertionFailure("bug loading unowned collection");
 				}
 			}
 
 			PersistentCollection rowCollection = persistenceContext.getLoadContexts()
 					.getCollectionLoadContext( rs )
 					.getLoadingCollection( persister, collectionRowKey );
 
 			if ( rowCollection != null ) {
 				rowCollection.readFrom( rs, persister, descriptor, owner );
 			}
 
 		}
 		else if ( optionalKey != null ) {
 			// we did not find a collection element in the result set, so we
 			// ensure that a collection is created with the owner's identifier,
 			// since what we have is an empty collection
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Result set contains (possibly empty) collection: %s",
 						MessageHelper.collectionInfoString( persister, optionalKey, getFactory() ) );
 			}
 
 			persistenceContext.getLoadContexts()
 					.getCollectionLoadContext( rs )
 					.getLoadingCollection( persister, optionalKey ); // handle empty collection
 
 		}
 
 		// else no collection element, but also no owner
 
 	}
 
 	/**
 	 * If this is a collection initializer, we need to tell the session that a collection
 	 * is being initialized, to account for the possibility of the collection having
 	 * no elements (hence no rows in the result set).
 	 */
 	private void handleEmptyCollections(
 	        final Serializable[] keys,
 	        final Object resultSetId,
 	        final SessionImplementor session) {
 
 		if ( keys != null ) {
+			final boolean debugEnabled = LOG.isDebugEnabled();
 			// this is a collection initializer, so we must create a collection
 			// for each of the passed-in keys, to account for the possibility
 			// that the collection is empty and has no rows in the result set
-
 			CollectionPersister[] collectionPersisters = getCollectionPersisters();
 			for ( int j=0; j<collectionPersisters.length; j++ ) {
 				for ( int i = 0; i < keys.length; i++ ) {
 					//handle empty collections
 
-					if ( LOG.isDebugEnabled() ) {
+					if ( debugEnabled ) {
 						LOG.debugf( "Result set contains (possibly empty) collection: %s",
 								MessageHelper.collectionInfoString( collectionPersisters[j], keys[i], getFactory() ) );
 					}
 
 					session.getPersistenceContext()
 							.getLoadContexts()
 							.getCollectionLoadContext( ( ResultSet ) resultSetId )
 							.getLoadingCollection( collectionPersisters[j], keys[i] );
 				}
 			}
 		}
 
 		// else this is not a collection initializer (and empty collections will
 		// be detected by looking for the owner's identifier in the result set)
 	}
 
 	/**
 	 * Read a row of <tt>Key</tt>s from the <tt>ResultSet</tt> into the given array.
 	 * Warning: this method is side-effecty.
 	 * <p/>
 	 * If an <tt>id</tt> is given, don't bother going to the <tt>ResultSet</tt>.
 	 */
 	private EntityKey getKeyFromResultSet(
 	        final int i,
 	        final Loadable persister,
 	        final Serializable id,
 	        final ResultSet rs,
 	        final SessionImplementor session) throws HibernateException, SQLException {
 
 		Serializable resultId;
 
 		// if we know there is exactly 1 row, we can skip.
 		// it would be great if we could _always_ skip this;
 		// it is a problem for <key-many-to-one>
 
 		if ( isSingleRowLoader() && id != null ) {
 			resultId = id;
 		}
 		else {
 
 			Type idType = persister.getIdentifierType();
 			resultId = (Serializable) idType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedKeyAliases(),
 					session,
 					null //problematic for <key-many-to-one>!
 				);
 
 			final boolean idIsResultId = id != null &&
 					resultId != null &&
 					idType.isEqual( id, resultId, factory );
 
 			if ( idIsResultId ) resultId = id; //use the id passed in
 		}
 
 		return resultId == null ? null : session.generateEntityKey( resultId, persister );
 	}
 
 	/**
 	 * Check the version of the object in the <tt>ResultSet</tt> against
 	 * the object version in the session cache, throwing an exception
 	 * if the version numbers are different
 	 */
 	private void checkVersion(
 	        final int i,
 	        final Loadable persister,
 	        final Serializable id,
 	        final Object entity,
 	        final ResultSet rs,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		Object version = session.getPersistenceContext().getEntry( entity ).getVersion();
 
 		if ( version != null ) { //null version means the object is in the process of being loaded somewhere else in the ResultSet
 			VersionType versionType = persister.getVersionType();
 			Object currentVersion = versionType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedVersionAliases(),
 					session,
 					null
 				);
 			if ( !versionType.isEqual(version, currentVersion) ) {
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor()
 							.optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), id );
 			}
 		}
 
 	}
 
 	/**
 	 * Resolve any IDs for currently loaded objects, duplications within the
 	 * <tt>ResultSet</tt>, etc. Instantiate empty objects to be initialized from the
 	 * <tt>ResultSet</tt>. Return an array of objects (a row of results) and an
 	 * array of booleans (by side-effect) that determine whether the corresponding
 	 * object should be initialized.
 	 */
 	private Object[] getRow(
 	        final ResultSet rs,
 	        final Loadable[] persisters,
 	        final EntityKey[] keys,
 	        final Object optionalObject,
 	        final EntityKey optionalObjectKey,
 	        final LockMode[] lockModes,
 	        final List hydratedObjects,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		final int cols = persisters.length;
 		final EntityAliases[] descriptors = getEntityAliases();
 
 		if ( LOG.isDebugEnabled() ) LOG.debugf( "Result row: %s", StringHelper.toString( keys ) );
 
 		final Object[] rowResults = new Object[cols];
 
 		for ( int i = 0; i < cols; i++ ) {
 
 			Object object = null;
 			EntityKey key = keys[i];
 
 			if ( keys[i] == null ) {
 				//do nothing
 			}
 			else {
 
 				//If the object is already loaded, return the loaded one
 				object = session.getEntityUsingInterceptor( key );
 				if ( object != null ) {
 					//its already loaded so don't need to hydrate it
 					instanceAlreadyLoaded(
 							rs,
 							i,
 							persisters[i],
 							key,
 							object,
 							lockModes[i],
 							session
 						);
 				}
 				else {
 					object = instanceNotYetLoaded(
 							rs,
 							i,
 							persisters[i],
 							descriptors[i].getRowIdAlias(),
 							key,
 							lockModes[i],
 							optionalObjectKey,
 							optionalObject,
 							hydratedObjects,
 							session
 						);
 				}
 
 			}
 
 			rowResults[i] = object;
 
 		}
 
 		return rowResults;
 	}
 
 	/**
 	 * The entity instance is already in the session cache
 	 */
 	private void instanceAlreadyLoaded(
 			final ResultSet rs,
 	        final int i,
 	        final Loadable persister,
 	        final EntityKey key,
 	        final Object object,
 	        final LockMode lockMode,
 	        final SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( !persister.isInstance( object ) ) {
 			throw new WrongClassException(
 					"loaded object was of wrong class " + object.getClass(),
 					key.getIdentifier(),
 					persister.getEntityName()
 				);
 		}
 
 		if ( LockMode.NONE != lockMode && upgradeLocks() ) { //no point doing this if NONE was requested
 
 			final boolean isVersionCheckNeeded = persister.isVersioned() &&
 					session.getPersistenceContext().getEntry(object)
 							.getLockMode().lessThan( lockMode );
 			// we don't need to worry about existing version being uninitialized
 			// because this block isn't called by a re-entrant load (re-entrant
 			// loads _always_ have lock mode NONE)
 			if (isVersionCheckNeeded) {
 				//we only check the version when _upgrading_ lock modes
 				checkVersion( i, persister, key.getIdentifier(), object, rs, session );
 				//we need to upgrade the lock mode to the mode requested
 				session.getPersistenceContext().getEntry(object)
 						.setLockMode(lockMode);
 			}
 		}
 	}
 
 	/**
 	 * The entity instance is not in the session cache
 	 */
 	private Object instanceNotYetLoaded(
 	        final ResultSet rs,
 	        final int i,
 	        final Loadable persister,
 	        final String rowIdAlias,
 	        final EntityKey key,
 	        final LockMode lockMode,
 	        final EntityKey optionalObjectKey,
 	        final Object optionalObject,
 	        final List hydratedObjects,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 		final String instanceClass = getInstanceClass(
 				rs,
 				i,
 				persister,
 				key.getIdentifier(),
 				session
 		);
 
 		final Object object;
 		if ( optionalObjectKey != null && key.equals( optionalObjectKey ) ) {
 			//its the given optional object
 			object = optionalObject;
 		}
 		else {
 			// instantiate a new instance
 			object = session.instantiate( instanceClass, key.getIdentifier() );
 		}
 
 		//need to hydrate it.
 
 		// grab its state from the ResultSet and keep it in the Session
 		// (but don't yet initialize the object itself)
 		// note that we acquire LockMode.READ even if it was not requested
 		LockMode acquiredLockMode = lockMode == LockMode.NONE ? LockMode.READ : lockMode;
 		loadFromResultSet(
 				rs,
 				i,
 				object,
 				instanceClass,
 				key,
 				rowIdAlias,
 				acquiredLockMode,
 				persister,
 				session
 			);
 
 		//materialize associations (and initialize the object) later
 		hydratedObjects.add( object );
 
 		return object;
 	}
 
 	private boolean isEagerPropertyFetchEnabled(int i) {
 		boolean[] array = getEntityEagerPropertyFetches();
 		return array!=null && array[i];
 	}
 
 
 	/**
 	 * Hydrate the state an object from the SQL <tt>ResultSet</tt>, into
 	 * an array or "hydrated" values (do not resolve associations yet),
 	 * and pass the hydrates state to the session.
 	 */
 	private void loadFromResultSet(
 	        final ResultSet rs,
 	        final int i,
 	        final Object object,
 	        final String instanceEntityName,
 	        final EntityKey key,
 	        final String rowIdAlias,
 	        final LockMode lockMode,
 	        final Loadable rootPersister,
 	        final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		final Serializable id = key.getIdentifier();
 
 		// Get the persister for the _subclass_
 		final Loadable persister = (Loadable) getFactory().getEntityPersister( instanceEntityName );
 
 		if ( LOG.isTraceEnabled() )
 			LOG.tracev( "Initializing object from ResultSet: {0}", MessageHelper.infoString( persister, id, getFactory() ) );
 
 		boolean eagerPropertyFetch = isEagerPropertyFetchEnabled(i);
 
 		// add temp entry so that the next step is circular-reference
 		// safe - only needed because some types don't take proper
 		// advantage of two-phase-load (esp. components)
 		TwoPhaseLoad.addUninitializedEntity(
 				key,
 				object,
 				persister,
 				lockMode,
 				!eagerPropertyFetch,
 				session
 			);
 
 		//This is not very nice (and quite slow):
 		final String[][] cols = persister == rootPersister ?
 				getEntityAliases()[i].getSuffixedPropertyAliases() :
 				getEntityAliases()[i].getSuffixedPropertyAliases(persister);
 
 		final Object[] values = persister.hydrate(
 				rs,
 				id,
 				object,
 				rootPersister,
 				cols,
 				eagerPropertyFetch,
 				session
 			);
 
 		final Object rowId = persister.hasRowId() ? rs.getObject(rowIdAlias) : null;
 
 		final AssociationType[] ownerAssociationTypes = getOwnerAssociationTypes();
 		if ( ownerAssociationTypes != null && ownerAssociationTypes[i] != null ) {
 			String ukName = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName();
 			if (ukName!=null) {
 				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex(ukName);
 				final Type type = persister.getPropertyTypes()[index];
 
 				// polymorphism not really handled completely correctly,
 				// perhaps...well, actually its ok, assuming that the
 				// entity name used in the lookup is the same as the
 				// the one used here, which it will be
 
 				EntityUniqueKey euk = new EntityUniqueKey(
 						rootPersister.getEntityName(), //polymorphism comment above
 						ukName,
 						type.semiResolve( values[index], session, object ),
 						type,
 						persister.getEntityMode(),
 						session.getFactory()
 				);
 				session.getPersistenceContext().addEntity( euk, object );
 			}
 		}
 
 		TwoPhaseLoad.postHydrate(
 				persister,
 				id,
 				values,
 				rowId,
 				object,
 				lockMode,
 				!eagerPropertyFetch,
 				session
 		);
 
 	}
 
 	/**
 	 * Determine the concrete class of an instance in the <tt>ResultSet</tt>
 	 */
 	private String getInstanceClass(
 	        final ResultSet rs,
 	        final int i,
 	        final Loadable persister,
 	        final Serializable id,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		if ( persister.hasSubclasses() ) {
 
 			// Code to handle subclasses of topClass
 			Object discriminatorValue = persister.getDiscriminatorType().nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedDiscriminatorAlias(),
 					session,
 					null
 				);
 
 			final String result = persister.getSubclassForDiscriminatorValue( discriminatorValue );
 
 			if ( result == null ) {
 				//woops we got an instance of another class hierarchy branch
 				throw new WrongClassException(
 						"Discriminator: " + discriminatorValue,
 						id,
 						persister.getEntityName()
 					);
 			}
 
 			return result;
 
 		}
 		else {
 			return persister.getEntityName();
 		}
 	}
 
 	/**
 	 * Advance the cursor to the first required row of the <tt>ResultSet</tt>
 	 */
 	private void advance(final ResultSet rs, final RowSelection selection)
 			throws SQLException {
 
 		final int firstRow = LimitHelper.getFirstRow( selection );
 		if ( firstRow != 0 ) {
 			if ( getFactory().getSettings().isScrollableResultSetsEnabled() ) {
 				// we can go straight to the first required row
 				rs.absolute( firstRow );
 			}
 			else {
 				// we need to step through the rows one row at a time (slow)
 				for ( int m = 0; m < firstRow; m++ ) rs.next();
 			}
 		}
 	}
 
 	/**
 	 * Build LIMIT clause handler applicable for given selection criteria. Returns {@link NoopLimitHandler} delegate
 	 * if dialect does not support LIMIT expression or processed query does not use pagination.
 	 *
 	 * @param sql Query string.
 	 * @param selection Selection criteria.
 	 * @return LIMIT clause delegate.
 	 */
 	protected LimitHandler getLimitHandler(String sql, RowSelection selection) {
 		final LimitHandler limitHandler = getFactory().getDialect().buildLimitHandler( sql, selection );
 		return LimitHelper.useLimit( limitHandler, selection ) ? limitHandler : new NoopLimitHandler( sql, selection );
 	}
 
 	private ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {
 		final boolean canScroll = getFactory().getSettings().isScrollableResultSetsEnabled();
 		if ( canScroll ) {
 			if ( scroll ) {
 				return queryParameters.getScrollMode();
 			}
 			if ( hasFirstRow && !useLimitOffSet ) {
 				return ScrollMode.SCROLL_INSENSITIVE;
 			}
 		}
 		return null;
 	}
 
 	/**
 	 * Process query string by applying filters, LIMIT clause, locks and comments if necessary.
 	 * Finally execute SQL statement and advance to the first row.
 	 */
 	protected SqlStatementWrapper executeQueryStatement(
 			final QueryParameters queryParameters,
 			final boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			final SessionImplementor session) throws SQLException {
 		return executeQueryStatement( getSQLString(), queryParameters, scroll, afterLoadActions, session );
 	}
 
 	protected SqlStatementWrapper executeQueryStatement(
 			String sqlStatement,
 			QueryParameters queryParameters,
 			boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			SessionImplementor session) throws SQLException {
 
 		// Processing query filters.
 		queryParameters.processFilters( sqlStatement, session );
 
 		// Applying LIMIT clause.
 		final LimitHandler limitHandler = getLimitHandler(
 				queryParameters.getFilteredSQL(),
 				queryParameters.getRowSelection()
 		);
 		String sql = limitHandler.getProcessedSql();
 
 		// Adding locks and comments.
 		sql = preprocessSQL( sql, queryParameters, getFactory().getDialect(), afterLoadActions );
 
 		final PreparedStatement st = prepareQueryStatement( sql, queryParameters, limitHandler, scroll, session );
 		return new SqlStatementWrapper( st, getResultSet( st, queryParameters.getRowSelection(), limitHandler, queryParameters.hasAutoDiscoverScalarTypes(), session ) );
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
 	        final String sql,
 	        final QueryParameters queryParameters,
 	        final LimitHandler limitHandler,
 	        final boolean scroll,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 		final Dialect dialect = getFactory().getDialect();
 		final RowSelection selection = queryParameters.getRowSelection();
 		boolean useLimit = LimitHelper.useLimit( limitHandler, selection );
 		boolean hasFirstRow = LimitHelper.hasFirstRow( selection );
 		boolean useLimitOffset = hasFirstRow && useLimit && limitHandler.supportsLimitOffset();
 		boolean callable = queryParameters.isCallable();
 		final ScrollMode scrollMode = getScrollMode( scroll, hasFirstRow, useLimitOffset, queryParameters );
 
 		PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(
 				sql,
 				callable,
 				scrollMode
 		);
 
 		try {
 
 			int col = 1;
 			//TODO: can we limit stored procedures ?!
 			col += limitHandler.bindLimitParametersAtStartOfQuery( st, col );
 
 			if (callable) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement)st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
 			col += limitHandler.bindLimitParametersAtEndOfQuery( st, col );
 
 			limitHandler.setMaxRows( st );
 
 			if ( selection != null ) {
 				if ( selection.getTimeout() != null ) {
 					st.setQueryTimeout( selection.getTimeout() );
 				}
 				if ( selection.getFetchSize() != null ) {
 					st.setFetchSize( selection.getFetchSize() );
 				}
 			}
 
 			// handle lock timeout...
 			LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 					if ( !dialect.supportsLockTimeouts() ) {
 						if ( LOG.isDebugEnabled() ) {
 							LOG.debugf(
 									"Lock timeout [%s] requested but dialect reported to not support lock timeouts",
 									lockOptions.getTimeOut()
 							);
 						}
 					}
 					else if ( dialect.isLockTimeoutParameterized() ) {
 						st.setInt( col++, lockOptions.getTimeOut() );
 					}
 				}
 			}
 
 			if ( LOG.isTraceEnabled() )
 			   LOG.tracev( "Bound [{0}] parameters total", col );
 		}
 		catch ( SQLException sqle ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw sqle;
 		}
 		catch ( HibernateException he ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw he;
 		}
 
 		return st;
 	}
 
 	/**
 	 * Bind all parameter values into the prepared statement in preparation
 	 * for execution.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 */
 	protected int bindParameterValues(
 			PreparedStatement statement,
 			QueryParameters queryParameters,
 			int startIndex,
 			SessionImplementor session) throws SQLException {
 		int span = 0;
 		span += bindPositionalParameters( statement, queryParameters, startIndex, session );
 		span += bindNamedParameters( statement, queryParameters.getNamedParameters(), startIndex + span, session );
 		return span;
 	}
 
 	/**
 	 * Bind positional parameter values to the JDBC prepared statement.
 	 * <p/>
 	 * Positional parameters are those specified by JDBC-style ? parameters
 	 * in the source query.  It is (currently) expected that these come
 	 * before any named parameters in the source query.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindPositionalParameters(
 	        final PreparedStatement statement,
 	        final QueryParameters queryParameters,
 	        final int startIndex,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 		final Object[] values = queryParameters.getFilteredPositionalParameterValues();
 		final Type[] types = queryParameters.getFilteredPositionalParameterTypes();
 		int span = 0;
 		for ( int i = 0; i < values.length; i++ ) {
 			types[i].nullSafeSet( statement, values[i], startIndex + span, session );
 			span += types[i].getColumnSpan( getFactory() );
 		}
 		return span;
 	}
 
 	/**
 	 * Bind named parameters to the JDBC prepared statement.
 	 * <p/>
 	 * This is a generic implementation, the problem being that in the
 	 * general case we do not know enough information about the named
 	 * parameters to perform this in a complete manner here.  Thus this
 	 * is generally overridden on subclasses allowing named parameters to
 	 * apply the specific behavior.  The most usual limitation here is that
 	 * we need to assume the type span is always one...
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param namedParams A map of parameter names to values
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindNamedParameters(
 			final PreparedStatement statement,
 			final Map namedParams,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		if ( namedParams != null ) {
 			// assumes that types are all of span 1
 			Iterator iter = namedParams.entrySet().iterator();
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			int result = 0;
 			while ( iter.hasNext() ) {
 				Map.Entry e = ( Map.Entry ) iter.next();
 				String name = ( String ) e.getKey();
 				TypedValue typedval = ( TypedValue ) e.getValue();
 				int[] locs = getNamedParameterLocs( name );
 				for ( int i = 0; i < locs.length; i++ ) {
 					if ( debugEnabled ) LOG.debugf( "bindNamedParameters() %s -> %s [%s]", typedval.getValue(), name, locs[i] + startIndex );
 					typedval.getType().nullSafeSet( statement, typedval.getValue(), locs[i] + startIndex, session );
 				}
 				result += locs.length;
 			}
 			return result;
 		}
 		else {
 			return 0;
 		}
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		throw new AssertionFailure("no named parameters");
 	}
 
 	/**
 	 * Execute given <tt>PreparedStatement</tt>, advance to the first result and return SQL <tt>ResultSet</tt>.
 	 */
 	protected final ResultSet getResultSet(
 	        final PreparedStatement st,
 	        final RowSelection selection,
 	        final LimitHandler limitHandler,
 	        final boolean autodiscovertypes,
 	        final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		try {
 			ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 			rs = wrapResultSetIfEnabled( rs , session );
 
 			if ( !limitHandler.supportsLimitOffset() || !LimitHelper.useLimit( limitHandler, selection ) ) {
 				advance( rs, selection );
 			}
 
 			if ( autodiscovertypes ) {
 				autoDiscoverTypes( rs );
 			}
 			return rs;
 		}
 		catch ( SQLException sqle ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw sqle;
 		}
 	}
 
 	protected void autoDiscoverTypes(ResultSet rs) {
 		throw new AssertionFailure("Auto discover types not supported in this loader");
 
 	}
 
 	private synchronized ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
 		// synchronized to avoid multi-thread access issues; defined as method synch to avoid
 		// potential deadlock issues due to nature of code.
 		if ( session.getFactory().getSettings().isWrapResultSetsEnabled() ) {
 			try {
 			   if ( LOG.isDebugEnabled() )
 			      LOG.debugf( "Wrapping result set [%s]", rs );
 				return session.getFactory()
 						.getJdbcServices()
 						.getResultSetWrapper().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch(SQLException e) {
 				LOG.unableToWrapResultSet( e );
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
 			LOG.trace( "Building columnName->columnIndex cache" );
 			columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
 		}
 
 		return columnNameCache;
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 * @param persister only needed for logging
 	 * @param lockOptions
 	 */
 	protected final List loadEntity(
 			final SessionImplementor session,
 			final Object id,
 			final Type identifierType,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalIdentifier,
 			final EntityPersister persister,
 			LockOptions lockOptions) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Loading entity: %s", MessageHelper.infoString( persister, id, identifierType, getFactory() ) );
 		}
 
 		List result;
 		try {
 			QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( new Type[] { identifierType } );
 			qp.setPositionalParameterValues( new Object[] { id } );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( optionalEntityName );
 			qp.setOptionalId( optionalIdentifier );
 			qp.setLockOptions( lockOptions );
 			result = doQueryAndInitializeNonLazyCollections( session, qp, false );
 		}
 		catch ( SQLException sqle ) {
 			final Loadable[] persisters = getEntityPersisters();
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load an entity: " +
 			        MessageHelper.infoString( persisters[persisters.length-1], id, identifierType, getFactory() ),
 			        getSQLString()
 				);
 		}
 
 		LOG.debug( "Done entity load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 * @param persister only needed for logging
 	 */
 	protected final List loadEntity(
 	        final SessionImplementor session,
 	        final Object key,
 	        final Object index,
 	        final Type keyType,
 	        final Type indexType,
 	        final EntityPersister persister) throws HibernateException {
 
 		LOG.debug( "Loading collection element by index" );
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters(
 							new Type[] { keyType, indexType },
 							new Object[] { key, index }
 					),
 					false
 			);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not collection element by index",
 			        getSQLString()
 				);
 		}
 
 		LOG.debug( "Done entity load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by wrappers that batch load entities
 	 * @param persister only needed for logging
 	 * @param lockOptions
 	 */
 	public final List loadEntityBatch(
 			final SessionImplementor session,
 			final Serializable[] ids,
 			final Type idType,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalId,
 			final EntityPersister persister,
 			LockOptions lockOptions) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Batch loading entity: %s", MessageHelper.infoString( persister, ids, getFactory() ) );
 
 		Type[] types = new Type[ids.length];
 		Arrays.fill( types, idType );
 		List result;
 		try {
 			QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( types );
 			qp.setPositionalParameterValues( ids );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( optionalEntityName );
 			qp.setOptionalId( optionalId );
 			qp.setLockOptions( lockOptions );
 			result = doQueryAndInitializeNonLazyCollections( session, qp, false );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load an entity batch: " +
 			        MessageHelper.infoString( getEntityPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 
 		LOG.debug( "Done entity batch load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that initialize collections
 	 */
 	public final void loadCollection(
 	        final SessionImplementor session,
 	        final Serializable id,
 	        final Type type) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ) );
 
 		Serializable[] ids = new Serializable[]{id};
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( new Type[]{type}, ids, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize a collection: " +
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ),
 					getSQLString()
 				);
 		}
 
 		LOG.debug( "Done loading collection" );
 
 	}
 
 	/**
 	 * Called by wrappers that batch initialize collections
 	 */
 	public final void loadCollectionBatch(
 	        final SessionImplementor session,
 	        final Serializable[] ids,
 	        final Type type) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Batch loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ) );
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( idTypes, ids, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not initialize a collection batch: " +
 			        MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 
 		LOG.debug( "Done batch load" );
 
 	}
 
 	/**
 	 * Called by subclasses that batch initialize collections
 	 */
 	protected final void loadCollectionSubselect(
 	        final SessionImplementor session,
 	        final Serializable[] ids,
 	        final Object[] parameterValues,
 	        final Type[] parameterTypes,
 	        final Map namedParameters,
 	        final Type type) throws HibernateException {
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections( session,
 					new QueryParameters( parameterTypes, parameterValues, namedParameters, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load collection by subselect: " +
 			        MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 	}
 
 	/**
 	 * Return the query results, using the query cache, called
 	 * by subclasses that implement cacheable queries
 	 */
 	protected List list(
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final Set querySpaces,
 	        final Type[] resultTypes) throws HibernateException {
 
 		final boolean cacheable = factory.getSettings().isQueryCacheEnabled() &&
 			queryParameters.isCacheable();
 
 		if ( cacheable ) {
 			return listUsingQueryCache( session, queryParameters, querySpaces, resultTypes );
 		}
 		else {
 			return listIgnoreQueryCache( session, queryParameters );
 		}
 	}
 
 	private List listIgnoreQueryCache(SessionImplementor session, QueryParameters queryParameters) {
 		return getResultList( doList( session, queryParameters ), queryParameters.getResultTransformer() );
 	}
 
 	private List listUsingQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Set querySpaces,
 			final Type[] resultTypes) {
 
 		QueryCache queryCache = factory.getQueryCache( queryParameters.getCacheRegion() );
 
 		QueryKey key = generateQueryKey( session, queryParameters );
 
 		if ( querySpaces == null || querySpaces.size() == 0 )
 			LOG.tracev( "Unexpected querySpaces is {0}", ( querySpaces == null ? querySpaces : "empty" ) );
 		else {
 			LOG.tracev( "querySpaces is {0}", querySpaces );
 		}
 
 		List result = getResultFromQueryCache(
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 36e2fd8345..aaee057cb5 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -2297,2008 +2297,2009 @@ public abstract class AbstractEntityPersister
 
 		// aliases for composite-id's
 		if ( getIdentifierType().isComponentType() ) {
 			// Fetch embedded identifiers propertynames from the "virtual" identifier component
 			CompositeType componentId = ( CompositeType ) getIdentifierType();
 			String[] idPropertyNames = componentId.getPropertyNames();
 			String[] idAliases = getIdentifierAliases();
 			String[] idColumnNames = getIdentifierColumnNames();
 
 			for ( int i = 0; i < idPropertyNames.length; i++ ) {
 				if ( entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 					subclassPropertyAliases.put(
 							ENTITY_ID + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							ENTITY_ID + "." + getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 //				if (hasIdentifierProperty() && !ENTITY_ID.equals( getIdentifierPropertyName() ) ) {
 				if ( hasIdentifierProperty() ) {
 					subclassPropertyAliases.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 				else {
 					// embedded composite ids ( alias.idname1, alias.idname2 )
 					subclassPropertyAliases.put( idPropertyNames[i], new String[] { idAliases[i] } );
 					subclassPropertyColumnNames.put( idPropertyNames[i],  new String[] { idColumnNames[i] } );
 				}
 			}
 		}
 
 		if ( entityMetamodel.isPolymorphic() ) {
 			subclassPropertyAliases.put( ENTITY_CLASS, new String[] { getDiscriminatorAlias() } );
 			subclassPropertyColumnNames.put( ENTITY_CLASS, new String[] { getDiscriminatorColumnName() } );
 		}
 
 	}
 
 	private void internalInitSubclassPropertyAliasesMap(String path, Iterator propertyIterator) {
 		while ( propertyIterator.hasNext() ) {
 
 			Property prop = ( Property ) propertyIterator.next();
 			String propname = path == null ? prop.getName() : path + "." + prop.getName();
 			if ( prop.isComposite() ) {
 				Component component = ( Component ) prop.getValue();
 				Iterator compProps = component.getPropertyIterator();
 				internalInitSubclassPropertyAliasesMap( propname, compProps );
 			}
 			else {
 				String[] aliases = new String[prop.getColumnSpan()];
 				String[] cols = new String[prop.getColumnSpan()];
 				Iterator colIter = prop.getColumnIterator();
 				int l = 0;
 				while ( colIter.hasNext() ) {
 					Selectable thing = ( Selectable ) colIter.next();
 					aliases[l] = thing.getAlias( getFactory().getDialect(), prop.getValue().getTable() );
 					cols[l] = thing.getText( getFactory().getDialect() ); // TODO: skip formulas?
 					l++;
 				}
 
 				subclassPropertyAliases.put( propname, aliases );
 				subclassPropertyColumnNames.put( propname, cols );
 			}
 		}
 
 	}
 
 	public Object loadByUniqueKey(
 			String propertyName,
 			Object uniqueKey,
 			SessionImplementor session) throws HibernateException {
 		return getAppropriateUniqueKeyLoader( propertyName, session ).loadByUniqueKey( session, uniqueKey );
 	}
 
 	private EntityLoader getAppropriateUniqueKeyLoader(String propertyName, SessionImplementor session) {
 		final boolean useStaticLoader = !session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& !session.getLoadQueryInfluencers().hasEnabledFetchProfiles()
 				&& propertyName.indexOf('.')<0; //ugly little workaround for fact that createUniqueKeyLoaders() does not handle component properties
 
 		if ( useStaticLoader ) {
 			return ( EntityLoader ) uniqueKeyLoaders.get( propertyName );
 		}
 		else {
 			return createUniqueKeyLoader(
 					propertyMapping.toType( propertyName ),
 					propertyMapping.toColumns( propertyName ),
 					session.getLoadQueryInfluencers()
 			);
 		}
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		return entityMetamodel.getPropertyIndex(propertyName);
 	}
 
 	protected void createUniqueKeyLoaders() throws MappingException {
 		Type[] propertyTypes = getPropertyTypes();
 		String[] propertyNames = getPropertyNames();
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( propertyUniqueness[i] ) {
 				//don't need filters for the static loaders
 				uniqueKeyLoaders.put(
 						propertyNames[i],
 						createUniqueKeyLoader(
 								propertyTypes[i],
 								getPropertyColumnNames( i ),
 								LoadQueryInfluencers.NONE
 						)
 				);
 				//TODO: create uk loaders for component properties
 			}
 		}
 	}
 
 	private EntityLoader createUniqueKeyLoader(
 			Type uniqueKeyType,
 			String[] columns,
 			LoadQueryInfluencers loadQueryInfluencers) {
 		if ( uniqueKeyType.isEntityType() ) {
 			String className = ( ( EntityType ) uniqueKeyType ).getAssociatedEntityName();
 			uniqueKeyType = getFactory().getEntityPersister( className ).getIdentifierType();
 		}
 		return new EntityLoader(
 				this,
 				columns,
 				uniqueKeyType,
 				1,
 				LockMode.NONE,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	protected boolean hasWhere() {
 		return sqlWhereString != null;
 	}
 
 	private void initOrdinaryPropertyPaths(Mapping mapping) throws MappingException {
 		for ( int i = 0; i < getSubclassPropertyNameClosure().length; i++ ) {
 			propertyMapping.initPropertyPaths( getSubclassPropertyNameClosure()[i],
 					getSubclassPropertyTypeClosure()[i],
 					getSubclassPropertyColumnNameClosure()[i],
 					getSubclassPropertyColumnReaderClosure()[i],
 					getSubclassPropertyColumnReaderTemplateClosure()[i],
 					getSubclassPropertyFormulaTemplateClosure()[i],
 					mapping );
 		}
 	}
 
 	private void initIdentifierPropertyPaths(Mapping mapping) throws MappingException {
 		String idProp = getIdentifierPropertyName();
 		if ( idProp != null ) {
 			propertyMapping.initPropertyPaths( idProp, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			propertyMapping.initPropertyPaths( null, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			propertyMapping.initPropertyPaths( ENTITY_ID, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 	}
 
 	private void initDiscriminatorPropertyPath(Mapping mapping) throws MappingException {
 		propertyMapping.initPropertyPaths( ENTITY_CLASS,
 				getDiscriminatorType(),
 				new String[]{getDiscriminatorColumnName()},
 				new String[]{getDiscriminatorColumnReaders()},
 				new String[]{getDiscriminatorColumnReaderTemplate()},
 				new String[]{getDiscriminatorFormulaTemplate()},
 				getFactory() );
 	}
 
 	protected void initPropertyPaths(Mapping mapping) throws MappingException {
 		initOrdinaryPropertyPaths(mapping);
 		initOrdinaryPropertyPaths(mapping); //do two passes, for collection property-ref!
 		initIdentifierPropertyPaths(mapping);
 		if ( entityMetamodel.isPolymorphic() ) {
 			initDiscriminatorPropertyPath( mapping );
 		}
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockMode lockMode,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoaderBuilder.getBuilder( getFactory() )
 				.buildLoader( this, batchSize, lockMode, getFactory(), loadQueryInfluencers );
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockOptions lockOptions,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoaderBuilder.getBuilder( getFactory() )
 				.buildLoader( this, batchSize, lockOptions, getFactory(), loadQueryInfluencers );
 	}
 
 	protected UniqueEntityLoader createEntityLoader(LockMode lockMode) throws MappingException {
 		return createEntityLoader( lockMode, LoadQueryInfluencers.NONE );
 	}
 
 	protected boolean check(int rows, Serializable id, int tableNumber, Expectation expectation, PreparedStatement statement) throws HibernateException {
 		try {
 			expectation.verifyOutcome( rows, statement, -1 );
 		}
 		catch( StaleStateException e ) {
 			if ( !isNullableTable( tableNumber ) ) {
 				if ( getFactory().getStatistics().isStatisticsEnabled() ) {
 					getFactory().getStatisticsImplementor()
 							.optimisticFailure( getEntityName() );
 				}
 				throw new StaleObjectStateException( getEntityName(), id );
 			}
 			return false;
 		}
 		catch( TooManyRowsAffectedException e ) {
 			throw new HibernateException(
 					"Duplicate identifier in table for: " +
 					MessageHelper.infoString( this, id, getFactory() )
 			);
 		}
 		catch ( Throwable t ) {
 			return false;
 		}
 		return true;
 	}
 
 	protected String generateUpdateString(boolean[] includeProperty, int j, boolean useRowId) {
 		return generateUpdateString( includeProperty, j, null, useRowId );
 	}
 
 	/**
 	 * Generate the SQL that updates a row by id (and version)
 	 */
 	protected String generateUpdateString(final boolean[] includeProperty,
 										  final int j,
 										  final Object[] oldFields,
 										  final boolean useRowId) {
 
 		Update update = new Update( getFactory().getDialect() ).setTableName( getTableName( j ) );
 
 		// select the correct row by either pk or rowid
 		if ( useRowId ) {
 			update.addPrimaryKeyColumns( new String[]{rowIdName} ); //TODO: eventually, rowIdName[j]
 		}
 		else {
 			update.addPrimaryKeyColumns( getKeyColumns( j ) );
 		}
 
 		boolean hasColumns = false;
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) 
 					&& !lobProperties.contains( i ) ) {
 				// this is a property of the table, which we are updating
 				update.addColumns( getPropertyColumnNames(i),
 						propertyColumnUpdateable[i], propertyColumnWriters[i] );
 				hasColumns = hasColumns || getPropertyColumnSpan( i ) > 0;
 			}
 		}
 		
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this property belongs on the table and is to be inserted
 				update.addColumns( getPropertyColumnNames(i),
 						propertyColumnUpdateable[i], propertyColumnWriters[i] );
 				hasColumns = true;
 			}
 		}
 
 		if ( j == 0 && isVersioned() && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 			// this is the root (versioned) table, and we are using version-based
 			// optimistic locking;  if we are not updating the version, also don't
 			// check it (unless this is a "generated" version column)!
 			if ( checkVersion( includeProperty ) ) {
 				update.setVersionColumnName( getVersionColumnName() );
 				hasColumns = true;
 			}
 		}
 		else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 			// we are using "all" or "dirty" property-based optimistic locking
 
 			boolean[] includeInWhere = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
 					? getPropertyUpdateability() //optimistic-lock="all", include all updatable properties
 					: includeProperty; 			 //optimistic-lock="dirty", include all properties we are updating this time
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				boolean include = includeInWhere[i] &&
 						isPropertyOfTable( i, j ) &&
 						versionability[i];
 				if ( include ) {
 					// this property belongs to the table, and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					String[] propertyColumnWriters = getPropertyColumnWriters( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( oldFields[i], getFactory() );
 					for ( int k=0; k<propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							update.addWhereColumn( propertyColumnNames[k], "=" + propertyColumnWriters[k] );
 						}
 						else {
 							update.addWhereColumn( propertyColumnNames[k], " is null" );
 						}
 					}
 				}
 			}
 
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "update " + getEntityName() );
 		}
 
 		return hasColumns ? update.toStatementString() : null;
 	}
 
 	private boolean checkVersion(final boolean[] includeProperty) {
         return includeProperty[ getVersionProperty() ] ||
 				entityMetamodel.getPropertyUpdateGenerationInclusions()[ getVersionProperty() ] != ValueInclusion.NONE;
 	}
 
 	protected String generateInsertString(boolean[] includeProperty, int j) {
 		return generateInsertString( false, includeProperty, j );
 	}
 
 	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty) {
 		return generateInsertString( identityInsert, includeProperty, 0 );
 	}
 
 	/**
 	 * Generate the SQL that inserts a row
 	 */
 	protected String generateInsertString(boolean identityInsert,
 			boolean[] includeProperty, int j) {
 
 		// todo : remove the identityInsert param and variations;
 		//   identity-insert strings are now generated from generateIdentityInsertString()
 
 		Insert insert = new Insert( getFactory().getDialect() )
 				.setTableName( getTableName( j ) );
 
 		// add normal properties
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			
 			if ( includeProperty[i] && isPropertyOfTable( i, j )
 					&& !lobProperties.contains( i ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i),
 						propertyColumnInsertable[i],
 						propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		if ( j == 0 ) {
 			addDiscriminatorToInsert( insert );
 		}
 
 		// add the primary key
 		if ( j == 0 && identityInsert ) {
 			insert.addIdentityColumn( getKeyColumns( 0 )[0] );
 		}
 		else {
 			insert.addColumns( getKeyColumns( j ) );
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 		
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i),
 						propertyColumnInsertable[i],
 						propertyColumnWriters[i] );
 			}
 		}
 
 		String result = insert.toStatementString();
 
 		// append the SQL to return the generated identifier
 		if ( j == 0 && identityInsert && useInsertSelectIdentity() ) { //TODO: suck into Insert
 			result = getFactory().getDialect().appendIdentitySelectToInsert( result );
 		}
 
 		return result;
 	}
 
 	/**
 	 * Used to generate an insery statement against the root table in the
 	 * case of identifier generation strategies where the insert statement
 	 * executions actually generates the identifier value.
 	 *
 	 * @param includeProperty indices of the properties to include in the
 	 * insert statement.
 	 * @return The insert SQL statement string
 	 */
 	protected String generateIdentityInsertString(boolean[] includeProperty) {
 		Insert insert = identityDelegate.prepareIdentifierGeneratingInsert();
 		insert.setTableName( getTableName( 0 ) );
 
 		// add normal properties except lobs
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) && !lobProperties.contains( i ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// HHH-4635 & HHH-8103
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) ) {
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		addDiscriminatorToInsert( insert );
 
 		// delegate already handles PK columns
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 
 		return insert.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL that deletes a row by id (and version)
 	 */
 	protected String generateDeleteString(int j) {
 		Delete delete = new Delete()
 				.setTableName( getTableName( j ) )
 				.addPrimaryKeyColumns( getKeyColumns( j ) );
 		if ( j == 0 ) {
 			delete.setVersionColumnName( getVersionColumnName() );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( "delete " + getEntityName() );
 		}
 		return delete.toStatementString();
 	}
 
 	protected int dehydrate(
 			Serializable id,
 			Object[] fields,
 			boolean[] includeProperty,
 			boolean[][] includeColumns,
 			int j,
 			PreparedStatement st,
 			SessionImplementor session,
 			boolean isUpdate) throws HibernateException, SQLException {
 		return dehydrate( id, fields, null, includeProperty, includeColumns, j, st, session, 1, isUpdate );
 	}
 
 	/**
 	 * Marshall the fields of a persistent instance to a prepared statement
 	 */
 	protected int dehydrate(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final boolean[][] includeColumns,
 	        final int j,
 	        final PreparedStatement ps,
 	        final SessionImplementor session,
 	        int index,
 	        boolean isUpdate ) throws SQLException, HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Dehydrating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j )
 					&& !lobProperties.contains( i )) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 		
 		if ( !isUpdate ) {
 			index += dehydrateId( id, rowId, ps, session, index );
 		}
 		
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 		
 		if ( isUpdate ) {
 			index += dehydrateId( id, rowId, ps, session, index );
 		}
 
 		return index;
 
 	}
 	
 	private int dehydrateId( 
 			final Serializable id,
 			final Object rowId,
 			final PreparedStatement ps,
 	        final SessionImplementor session,
 			int index ) throws SQLException {
 		if ( rowId != null ) {
 			ps.setObject( index, rowId );
 			return 1;
 		} else if ( id != null ) {
 			getIdentifierType().nullSafeSet( ps, id, index, session );
 			return getIdentifierColumnSpan();
 		}
 		return 0;
 	}
 
 	/**
 	 * Unmarshall the fields of a persistent instance from a result set,
 	 * without resolving associations or collections. Question: should
 	 * this really be here, or should it be sent back to Loader?
 	 */
 	public Object[] hydrate(
 			final ResultSet rs,
 	        final Serializable id,
 	        final Object object,
 	        final Loadable rootLoadable,
 	        final String[][] suffixedPropertyColumns,
 	        final boolean allProperties,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Hydrating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		final AbstractEntityPersister rootPersister = (AbstractEntityPersister) rootLoadable;
 
 		final boolean hasDeferred = rootPersister.hasSequentialSelect();
 		PreparedStatement sequentialSelect = null;
 		ResultSet sequentialResultSet = null;
 		boolean sequentialSelectEmpty = false;
 		try {
 
 			if ( hasDeferred ) {
 				final String sql = rootPersister.getSequentialSelect( getEntityName() );
 				if ( sql != null ) {
 					//TODO: I am not so sure about the exception handling in this bit!
 					sequentialSelect = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql );
 					rootPersister.getIdentifierType().nullSafeSet( sequentialSelect, id, 1, session );
 					sequentialResultSet = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( sequentialSelect );
 					if ( !sequentialResultSet.next() ) {
 						// TODO: Deal with the "optional" attribute in the <join> mapping;
 						// this code assumes that optional defaults to "true" because it
 						// doesn't actually seem to work in the fetch="join" code
 						//
 						// Note that actual proper handling of optional-ality here is actually
 						// more involved than this patch assumes.  Remember that we might have
 						// multiple <join/> mappings associated with a single entity.  Really
 						// a couple of things need to happen to properly handle optional here:
 						//  1) First and foremost, when handling multiple <join/>s, we really
 						//      should be using the entity root table as the driving table;
 						//      another option here would be to choose some non-optional joined
 						//      table to use as the driving table.  In all likelihood, just using
 						//      the root table is much simplier
 						//  2) Need to add the FK columns corresponding to each joined table
 						//      to the generated select list; these would then be used when
 						//      iterating the result set to determine whether all non-optional
 						//      data is present
 						// My initial thoughts on the best way to deal with this would be
 						// to introduce a new SequentialSelect abstraction that actually gets
 						// generated in the persisters (ok, SingleTable...) and utilized here.
 						// It would encapsulated all this required optional-ality checking...
 						sequentialSelectEmpty = true;
 					}
 				}
 			}
 
 			final String[] propNames = getPropertyNames();
 			final Type[] types = getPropertyTypes();
 			final Object[] values = new Object[types.length];
 			final boolean[] laziness = getPropertyLaziness();
 			final String[] propSubclassNames = getSubclassPropertySubclassNameClosure();
 
 			for ( int i = 0; i < types.length; i++ ) {
 				if ( !propertySelectable[i] ) {
 					values[i] = BackrefPropertyAccessor.UNKNOWN;
 				}
 				else if ( allProperties || !laziness[i] ) {
 					//decide which ResultSet to get the property value from:
 					final boolean propertyIsDeferred = hasDeferred &&
 							rootPersister.isSubclassPropertyDeferred( propNames[i], propSubclassNames[i] );
 					if ( propertyIsDeferred && sequentialSelectEmpty ) {
 						values[i] = null;
 					}
 					else {
 						final ResultSet propertyResultSet = propertyIsDeferred ? sequentialResultSet : rs;
 						final String[] cols = propertyIsDeferred ? propertyColumnAliases[i] : suffixedPropertyColumns[i];
 						values[i] = types[i].hydrate( propertyResultSet, cols, session, object );
 					}
 				}
 				else {
 					values[i] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 				}
 			}
 
 			if ( sequentialResultSet != null ) {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( sequentialResultSet, sequentialSelect );
 			}
 
 			return values;
 
 		}
 		finally {
 			if ( sequentialSelect != null ) {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( sequentialSelect );
 			}
 		}
 	}
 
 	protected boolean useInsertSelectIdentity() {
 		return !useGetGeneratedKeys() && getFactory().getDialect().supportsInsertSelectIdentity();
 	}
 
 	protected boolean useGetGeneratedKeys() {
 		return getFactory().getSettings().isGetGeneratedKeysEnabled();
 	}
 
 	protected String getSequentialSelect(String entityName) {
 		throw new UnsupportedOperationException("no sequential selects");
 	}
 
 	/**
 	 * Perform an SQL INSERT, and then retrieve a generated identifier.
 	 * <p/>
 	 * This form is used for PostInsertIdentifierGenerator-style ids (IDENTITY,
 	 * select, etc).
 	 */
 	protected Serializable insert(
 			final Object[] fields,
 	        final boolean[] notNull,
 	        String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Inserting entity: {0} (native id)", getEntityName() );
 			if ( isVersioned() ) {
 				LOG.tracev( "Version: {0}", Versioning.getVersion( fields, this ) );
 			}
 		}
 
 		Binder binder = new Binder() {
 			public void bindValues(PreparedStatement ps) throws SQLException {
 				dehydrate( null, fields, notNull, propertyColumnInsertable, 0, ps, session, false );
 			}
 			public Object getEntity() {
 				return object;
 			}
 		};
 
 		return identityDelegate.performInsert( sql, session, binder );
 	}
 
 	public String getIdentitySelectString() {
 		//TODO: cache this in an instvar
 		return getFactory().getDialect().getIdentitySelectString(
 				getTableName(0),
 				getKeyColumns(0)[0],
 				getIdentifierType().sqlTypes( getFactory() )[0]
 		);
 	}
 
 	public String getSelectByUniqueKeyString(String propertyName) {
 		return new SimpleSelect( getFactory().getDialect() )
 			.setTableName( getTableName(0) )
 			.addColumns( getKeyColumns(0) )
 			.addCondition( getPropertyColumnNames(propertyName), "=?" )
 			.toStatementString();
 	}
 
 	private BasicBatchKey inserBatchKey;
 
 	/**
 	 * Perform an SQL INSERT.
 	 * <p/>
 	 * This for is used for all non-root tables as well as the root table
 	 * in cases where the identifier value is known before the insert occurs.
 	 */
 	protected void insert(
 			final Serializable id,
 	        final Object[] fields,
 	        final boolean[] notNull,
 	        final int j,
 	        final String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		//note: it is conceptually possible that a UserType could map null to
 		//	  a non-null value, so the following is arguable:
 		if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 			return;
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Inserting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( j == 0 && isVersioned() )
 				LOG.tracev( "Version: {0}", Versioning.getVersion( fields, this ) );
 		}
 
 		// TODO : shouldn't inserts be Expectations.NONE?
 		final Expectation expectation = Expectations.appropriateExpectation( insertResultCheckStyles[j] );
 		// we can't batch joined inserts, *especially* not if it is an identity insert;
 		// nor can we batch statements where the expectation is based on an output param
 		final boolean useBatch = j == 0 && expectation.canBeBatched();
 		if ( useBatch && inserBatchKey == null ) {
 			inserBatchKey = new BasicBatchKey(
 					getEntityName() + "#INSERT",
 					expectation
 			);
 		}
 		final boolean callable = isInsertCallable( j );
 
 		try {
 			// Render the SQL query
 			final PreparedStatement insert;
 			if ( useBatch ) {
 				insert = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( inserBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				insert = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				int index = 1;
 				index += expectation.prepare( insert );
 
 				// Write the values of fields onto the prepared statement - we MUST use the state at the time the
 				// insert was issued (cos of foreign key constraints). Not necessarily the object's current state
 
 				dehydrate( id, fields, null, notNull, propertyColumnInsertable, j, insert, session, index, false );
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( inserBatchKey ).addToBatch();
 				}
 				else {
 					expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( insert ), insert, -1 );
 				}
 
 			}
 			catch ( SQLException e ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw e;
 			}
 			finally {
 				if ( !useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( insert );
 				}
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not insert: " + MessageHelper.infoString( this ),
 					sql
 			);
 		}
 
 	}
 
 	/**
 	 * Perform an SQL UPDATE or SQL INSERT
 	 */
 	protected void updateOrInsert(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( !isInverseTable( j ) ) {
 
 			final boolean isRowToUpdate;
 			if ( isNullableTable( j ) && oldFields != null && isAllNull( oldFields, j ) ) {
 				//don't bother trying to update, we know there is no row there yet
 				isRowToUpdate = false;
 			}
 			else if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 				//if all fields are null, we might need to delete existing row
 				isRowToUpdate = true;
 				delete( id, oldVersion, j, object, getSQLDeleteStrings()[j], session, null );
 			}
 			else {
 				//there is probably a row there, so try to update
 				//if no rows were updated, we will find out
 				isRowToUpdate = update( id, fields, oldFields, rowId, includeProperty, j, oldVersion, object, sql, session );
 			}
 
 			if ( !isRowToUpdate && !isAllNull( fields, j ) ) {
 				// assume that the row was not there since it previously had only null
 				// values, so do an INSERT instead
 				//TODO: does not respect dynamic-insert
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 
 		}
 
 	}
 
 	private BasicBatchKey updateBatchKey;
 
 	protected boolean update(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		final Expectation expectation = Expectations.appropriateExpectation( updateResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && expectation.canBeBatched() && isBatchable(); //note: updates to joined tables can't be batched...
 		if ( useBatch && updateBatchKey == null ) {
 			updateBatchKey = new BasicBatchKey(
 					getEntityName() + "#UPDATE",
 					expectation
 			);
 		}
 		final boolean callable = isUpdateCallable( j );
 		final boolean useVersion = j == 0 && isVersioned();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Updating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion )
 				LOG.tracev( "Existing version: {0} -> New version:{1}", oldVersion, fields[getVersionProperty()] );
 		}
 
 		try {
 			int index = 1; // starting index
 			final PreparedStatement update;
 			if ( useBatch ) {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( updateBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				index+= expectation.prepare( update );
 
 				//Now write the values of fields onto the prepared statement
 				index = dehydrate( id, fields, rowId, includeProperty, propertyColumnUpdateable, j, update, session, index, true );
 
 				// Write any appropriate versioning conditional parameters
 				if ( useVersion && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 					if ( checkVersion( includeProperty ) ) {
 						getVersionType().nullSafeSet( update, oldVersion, index, session );
 					}
 				}
 				else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 					boolean[] versionability = getPropertyVersionability(); //TODO: is this really necessary????
 					boolean[] includeOldField = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
 							? getPropertyUpdateability()
 							: includeProperty;
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						boolean include = includeOldField[i] &&
 								isPropertyOfTable( i, j ) &&
 								versionability[i]; //TODO: is this really necessary????
 						if ( include ) {
 							boolean[] settable = types[i].toColumnNullness( oldFields[i], getFactory() );
 							types[i].nullSafeSet(
 									update,
 									oldFields[i],
 									index,
 									settable,
 									session
 								);
 							index += ArrayHelper.countTrue(settable);
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( updateBatchKey ).addToBatch();
 					return true;
 				}
 				else {
 					return check( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( update ), id, j, expectation, update );
 				}
 
 			}
 			catch ( SQLException e ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw e;
 			}
 			finally {
 				if ( !useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( update );
 				}
 			}
 
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not update: " + MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 		}
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	/**
 	 * Perform an SQL DELETE
 	 */
 	protected void delete(
 			final Serializable id,
 			final Object version,
 			final int j,
 			final Object object,
 			final String sql,
 			final SessionImplementor session,
 			final Object[] loadedState) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		final boolean useVersion = j == 0 && isVersioned();
 		final boolean callable = isDeleteCallable( j );
 		final Expectation expectation = Expectations.appropriateExpectation( deleteResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && isBatchable() && expectation.canBeBatched();
 		if ( useBatch && deleteBatchKey == null ) {
 			deleteBatchKey = new BasicBatchKey(
 					getEntityName() + "#DELETE",
 					expectation
 			);
 		}
 
-		if ( LOG.isTraceEnabled() ) {
+		final boolean traceEnabled = LOG.isTraceEnabled();
+		if ( traceEnabled ) {
 			LOG.tracev( "Deleting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion )
 				LOG.tracev( "Version: {0}", version );
 		}
 
 		if ( isTableCascadeDeleteEnabled( j ) ) {
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev( "Delete handled by foreign key constraint: {0}", getTableName( j ) );
 			}
 			return; //EARLY EXIT!
 		}
 
 		try {
 			//Render the SQL query
 			PreparedStatement delete;
 			int index = 1;
 			if ( useBatch ) {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( deleteBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 
 				index += expectation.prepare( delete );
 
 				// Do the key. The key is immutable so we can use the _current_ object state - not necessarily
 				// the state at the time the delete was issued
 				getIdentifierType().nullSafeSet( delete, id, index, session );
 				index += getIdentifierColumnSpan();
 
 				// We should use the _current_ object state (ie. after any updates that occurred during flush)
 
 				if ( useVersion ) {
 					getVersionType().nullSafeSet( delete, version, index, session );
 				}
 				else if ( isAllOrDirtyOptLocking() && loadedState != null ) {
 					boolean[] versionability = getPropertyVersionability();
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 							// this property belongs to the table and it is not specifically
 							// excluded from optimistic locking by optimistic-lock="false"
 							boolean[] settable = types[i].toColumnNullness( loadedState[i], getFactory() );
 							types[i].nullSafeSet( delete, loadedState[i], index, settable, session );
 							index += ArrayHelper.countTrue( settable );
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( deleteBatchKey ).addToBatch();
 				}
 				else {
 					check( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( delete ), id, j, expectation, delete );
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
 					session.getTransactionCoordinator().getJdbcCoordinator().release( delete );
 				}
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not delete: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 
 		}
 
 	}
 
 	private String[] getUpdateStrings(boolean byRowId, boolean lazy) {
 		if ( byRowId ) {
 			return lazy ? getSQLLazyUpdateByRowIdStrings() : getSQLUpdateByRowIdStrings();
 		}
 		else {
 			return lazy ? getSQLLazyUpdateStrings() : getSQLUpdateStrings();
 		}
 	}
 
 	/**
 	 * Update an object
 	 */
 	public void update(
 			final Serializable id,
 	        final Object[] fields,
 	        final int[] dirtyFields,
 	        final boolean hasDirtyCollection,
 	        final Object[] oldFields,
 	        final Object oldVersion,
 	        final Object object,
 	        final Object rowId,
 	        final SessionImplementor session) throws HibernateException {
 
 		//note: dirtyFields==null means we had no snapshot, and we couldn't get one using select-before-update
 		//	  oldFields==null just means we had no snapshot to begin with (we might have used select-before-update to get the dirtyFields)
 
 		final boolean[] tableUpdateNeeded = getTableUpdateNeeded( dirtyFields, hasDirtyCollection );
 		final int span = getTableSpan();
 
 		final boolean[] propsToUpdate;
 		final String[] updateStrings;
 		EntityEntry entry = session.getPersistenceContext().getEntry( object );
 
 		// Ensure that an immutable or non-modifiable entity is not being updated unless it is
 		// in the process of being deleted.
 		if ( entry == null && ! isMutable() ) {
 			throw new IllegalStateException( "Updating immutable entity that is not in session yet!" );
 		}
 		if ( ( entityMetamodel.isDynamicUpdate() && dirtyFields != null ) ) {
 			// We need to generate the UPDATE SQL when dynamic-update="true"
 			propsToUpdate = getPropertiesToUpdate( dirtyFields, hasDirtyCollection );
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else if ( ! isModifiableEntity( entry ) ) {
 			// We need to generate UPDATE SQL when a non-modifiable entity (e.g., read-only or immutable)
 			// needs:
 			// - to have references to transient entities set to null before being deleted
 			// - to have version incremented do to a "dirty" association
 			// If dirtyFields == null, then that means that there are no dirty properties to
 			// to be updated; an empty array for the dirty fields needs to be passed to
 			// getPropertiesToUpdate() instead of null.
 			propsToUpdate = getPropertiesToUpdate(
 					( dirtyFields == null ? ArrayHelper.EMPTY_INT_ARRAY : dirtyFields ),
 					hasDirtyCollection
 			);
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else {
 			// For the case of dynamic-update="false", or no snapshot, we use the static SQL
 			updateStrings = getUpdateStrings(
 					rowId != null,
 					hasUninitializedLazyProperties( object )
 			);
 			propsToUpdate = getPropertyUpdateability( object );
 		}
 
 		for ( int j = 0; j < span; j++ ) {
 			// Now update only the tables with dirty properties (and the table with the version number)
 			if ( tableUpdateNeeded[j] ) {
 				updateOrInsert(
 						id,
 						fields,
 						oldFields,
 						j == 0 ? rowId : null,
 						propsToUpdate,
 						j,
 						oldVersion,
 						object,
 						updateStrings[j],
 						session
 					);
 			}
 		}
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		final Serializable id;
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			id = insert( fields, notNull, generateInsertString( true, notNull ), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			id = insert( fields, getPropertyInsertability(), getSQLIdentityInsertString(), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 		return id;
 	}
 
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 	}
 
 	/**
 	 * Delete an object
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 			throws HibernateException {
 		final int span = getTableSpan();
 		boolean isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && isAllOrDirtyOptLocking();
 		Object[] loadedState = null;
 		if ( isImpliedOptimisticLocking ) {
 			// need to treat this as if it where optimistic-lock="all" (dirty does *not* make sense);
 			// first we need to locate the "loaded" state
 			//
 			// Note, it potentially could be a proxy, so doAfterTransactionCompletion the location the safe way...
 			final EntityKey key = session.generateEntityKey( id, this );
 			Object entity = session.getPersistenceContext().getEntity( key );
 			if ( entity != null ) {
 				EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 				loadedState = entry.getLoadedState();
 			}
 		}
 
 		final String[] deleteStrings;
 		if ( isImpliedOptimisticLocking && loadedState != null ) {
 			// we need to utilize dynamic delete statements
 			deleteStrings = generateSQLDeletStrings( loadedState );
 		}
 		else {
 			// otherwise, utilize the static delete statements
 			deleteStrings = getSQLDeleteStrings();
 		}
 
 		for ( int j = span - 1; j >= 0; j-- ) {
 			delete( id, version, j, object, deleteStrings[j], session, loadedState );
 		}
 
 	}
 
 	private boolean isAllOrDirtyOptLocking() {
 		return entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.DIRTY
 				|| entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL;
 	}
 
 	private String[] generateSQLDeletStrings(Object[] loadedState) {
 		int span = getTableSpan();
 		String[] deleteStrings = new String[span];
 		for ( int j = span - 1; j >= 0; j-- ) {
 			Delete delete = new Delete()
 					.setTableName( getTableName( j ) )
 					.addPrimaryKeyColumns( getKeyColumns( j ) );
 			if ( getFactory().getSettings().isCommentsEnabled() ) {
 				delete.setComment( "delete " + getEntityName() + " [" + j + "]" );
 			}
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 					// this property belongs to the table and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( loadedState[i], getFactory() );
 					for ( int k = 0; k < propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							delete.addWhereFragment( propertyColumnNames[k] + " = ?" );
 						}
 						else {
 							delete.addWhereFragment( propertyColumnNames[k] + " is null" );
 						}
 					}
 				}
 			}
 			deleteStrings[j] = delete.toStatementString();
 		}
 		return deleteStrings;
 	}
 
 	protected void logStaticSQL() {
         if ( LOG.isDebugEnabled() ) {
             LOG.debugf( "Static SQL for entity: %s", getEntityName() );
             if ( sqlLazySelectString != null ) {
 				LOG.debugf( " Lazy select: %s", sqlLazySelectString );
 			}
             if ( sqlVersionSelectString != null ) {
 				LOG.debugf( " Version select: %s", sqlVersionSelectString );
 			}
             if ( sqlSnapshotSelectString != null ) {
 				LOG.debugf( " Snapshot select: %s", sqlSnapshotSelectString );
 			}
 			for ( int j = 0; j < getTableSpan(); j++ ) {
                 LOG.debugf( " Insert %s: %s", j, getSQLInsertStrings()[j] );
                 LOG.debugf( " Update %s: %s", j, getSQLUpdateStrings()[j] );
                 LOG.debugf( " Delete %s: %s", j, getSQLDeleteStrings()[j] );
 			}
             if ( sqlIdentityInsertString != null ) {
 				LOG.debugf( " Identity insert: %s", sqlIdentityInsertString );
 			}
             if ( sqlUpdateByRowIdString != null ) {
 				LOG.debugf( " Update by row id (all fields): %s", sqlUpdateByRowIdString );
 			}
             if ( sqlLazyUpdateByRowIdString != null ) {
 				LOG.debugf( " Update by row id (non-lazy fields): %s", sqlLazyUpdateByRowIdString );
 			}
             if ( sqlInsertGeneratedValuesSelectString != null ) {
 				LOG.debugf( " Insert-generated property select: %s", sqlInsertGeneratedValuesSelectString );
 			}
             if ( sqlUpdateGeneratedValuesSelectString != null ) {
 				LOG.debugf( " Update-generated property select: %s", sqlUpdateGeneratedValuesSelectString );
 			}
 		}
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		final StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return rootAlias;
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toFromFragmentString();
 	}
 
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toWhereFragmentString();
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return false;
 	}
 
 	protected JoinFragment createJoin(String name, boolean innerJoin, boolean includeSubclasses) {
 		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() ); //all joins join to the pk of the driving table
 		final JoinFragment join = getFactory().getDialect().createOuterJoinFragment();
 		final int tableSpan = getSubclassTableSpan();
 		for ( int j = 1; j < tableSpan; j++ ) { //notice that we skip the first table; it is the driving table!
 			final boolean joinIsIncluded = isClassOrSuperclassTable( j ) ||
 					( includeSubclasses && !isSubclassTableSequentialSelect( j ) && !isSubclassTableLazy( j ) );
 			if ( joinIsIncluded ) {
 				join.addJoin( getSubclassTableName( j ),
 						generateTableAlias( name, j ),
 						idCols,
 						getSubclassTableKeyColumns( j ),
 						innerJoin && isClassOrSuperclassTable( j ) && !isInverseTable( j ) && !isNullableTable( j ) ?
 						JoinType.INNER_JOIN : //we can inner join to superclass tables (the row MUST be there)
 						JoinType.LEFT_OUTER_JOIN //we can never inner join to subclass tables
 					);
 			}
 		}
 		return join;
 	}
 
 	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
 		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
 		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
 		for ( int i = 1; i < tableNumbers.length; i++ ) { //skip the driving table
 			final int j = tableNumbers[i];
 			jf.addJoin( getSubclassTableName( j ),
 					generateTableAlias( getRootAlias(), j ),
 					keyCols,
 					getSubclassTableKeyColumns( j ),
 					isInverseSubclassTable( j ) || isNullableSubclassTable( j ) ?
 					JoinType.LEFT_OUTER_JOIN :
 					JoinType.INNER_JOIN );
 		}
 		return jf;
 	}
 
 	protected SelectFragment createSelect(final int[] subclassColumnNumbers,
 										  final int[] subclassFormulaNumbers) {
 
 		SelectFragment selectFragment = new SelectFragment();
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < subclassColumnNumbers.length; i++ ) {
 			int columnNumber = subclassColumnNumbers[i];
 			if ( subclassColumnSelectableClosure[columnNumber] ) {
 				final String subalias = generateTableAlias( getRootAlias(), columnTableNumbers[columnNumber] );
 				selectFragment.addColumnTemplate( subalias, columnReaderTemplates[columnNumber], columnAliases[columnNumber] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < subclassFormulaNumbers.length; i++ ) {
 			int formulaNumber = subclassFormulaNumbers[i];
 			final String subalias = generateTableAlias( getRootAlias(), formulaTableNumbers[formulaNumber] );
 			selectFragment.addFormula( subalias, formulaTemplates[formulaNumber], formulaAliases[formulaNumber] );
 		}
 
 		return selectFragment;
 	}
 
 	protected String createFrom(int tableNumber, String alias) {
 		return getSubclassTableName( tableNumber ) + ' ' + alias;
 	}
 
 	protected String createWhereByKey(int tableNumber, String alias) {
 		//TODO: move to .sql package, and refactor with similar things!
 		return StringHelper.join( "=? and ",
 				StringHelper.qualify( alias, getSubclassTableKeyColumns( tableNumber ) ) ) + "=?";
 	}
 
 	protected String renderSelect(
 			final int[] tableNumbers,
 	        final int[] columnNumbers,
 	        final int[] formulaNumbers) {
 
 		Arrays.sort( tableNumbers ); //get 'em in the right order (not that it really matters)
 
 		//render the where and from parts
 		int drivingTable = tableNumbers[0];
 		final String drivingAlias = generateTableAlias( getRootAlias(), drivingTable ); //we *could* regerate this inside each called method!
 		final String where = createWhereByKey( drivingTable, drivingAlias );
 		final String from = createFrom( drivingTable, drivingAlias );
 
 		//now render the joins
 		JoinFragment jf = createJoin( tableNumbers, drivingAlias );
 
 		//now render the select clause
 		SelectFragment selectFragment = createSelect( columnNumbers, formulaNumbers );
 
 		//now tie it all together
 		Select select = new Select( getFactory().getDialect() );
 		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 		select.setFromClause( from );
 		select.setWhereClause( where );
 		select.setOuterJoins( jf.toFromFragmentString(), jf.toWhereFragmentString() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "sequential select " + getEntityName() );
 		}
 		return select.toStatementString();
 	}
 
 	private String getRootAlias() {
 		return StringHelper.generateAlias( getEntityName() );
 	}
 
 	protected void postConstruct(Mapping mapping) throws MappingException {
 		initPropertyPaths(mapping);
 
 		//insert/update/delete SQL
 		final int joinSpan = getTableSpan();
 		sqlDeleteStrings = new String[joinSpan];
 		sqlInsertStrings = new String[joinSpan];
 		sqlUpdateStrings = new String[joinSpan];
 		sqlLazyUpdateStrings = new String[joinSpan];
 
 		sqlUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getPropertyUpdateability(), 0, true );
 		sqlLazyUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getNonLazyPropertyUpdateability(), 0, true );
 
 		for ( int j = 0; j < joinSpan; j++ ) {
 			sqlInsertStrings[j] = customSQLInsert[j] == null ?
 					generateInsertString( getPropertyInsertability(), j ) :
 					customSQLInsert[j];
 			sqlUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlLazyUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getNonLazyPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlDeleteStrings[j] = customSQLDelete[j] == null ?
 					generateDeleteString( j ) :
 					customSQLDelete[j];
 		}
 
 		tableHasColumns = new boolean[joinSpan];
 		for ( int j = 0; j < joinSpan; j++ ) {
 			tableHasColumns[j] = sqlUpdateStrings[j] != null;
 		}
 
 		//select SQL
 		sqlSnapshotSelectString = generateSnapshotSelectString();
 		sqlLazySelectString = generateLazySelectString();
 		sqlVersionSelectString = generateSelectVersionString();
 		if ( hasInsertGeneratedProperties() ) {
 			sqlInsertGeneratedValuesSelectString = generateInsertGeneratedValuesSelectString();
 		}
 		if ( hasUpdateGeneratedProperties() ) {
 			sqlUpdateGeneratedValuesSelectString = generateUpdateGeneratedValuesSelectString();
 		}
 		if ( isIdentifierAssignedByInsert() ) {
 			identityDelegate = ( ( PostInsertIdentifierGenerator ) getIdentifierGenerator() )
 					.getInsertGeneratedIdentifierDelegate( this, getFactory().getDialect(), useGetGeneratedKeys() );
 			sqlIdentityInsertString = customSQLInsert[0] == null
 					? generateIdentityInsertString( getPropertyInsertability() )
 					: customSQLInsert[0];
 		}
 		else {
 			sqlIdentityInsertString = null;
 		}
 
 		logStaticSQL();
 
 	}
 
 	public void postInstantiate() throws MappingException {
 		generateEntityDefinition();
 
 		createLoaders();
 		createUniqueKeyLoaders();
 		createQueryLoader();
 	}
 
 	//needed by subclasses to override the createLoader strategy
 	protected Map getLoaders() {
 		return loaders;
 	}
 
 	//Relational based Persisters should be content with this implementation
 	protected void createLoaders() {
 		final Map loaders = getLoaders();
 		loaders.put( LockMode.NONE, createEntityLoader( LockMode.NONE ) );
 
 		UniqueEntityLoader readLoader = createEntityLoader( LockMode.READ );
 		loaders.put( LockMode.READ, readLoader );
 
 		//TODO: inexact, what we really need to know is: are any outer joins used?
 		boolean disableForUpdate = getSubclassTableSpan() > 1 &&
 				hasSubclasses() &&
 				!getFactory().getDialect().supportsOuterJoinForUpdate();
 
 		loaders.put(
 				LockMode.UPGRADE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE )
 			);
 		loaders.put(
 				LockMode.UPGRADE_NOWAIT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_NOWAIT )
 			);
 		loaders.put(
 				LockMode.UPGRADE_SKIPLOCKED,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_SKIPLOCKED )
 			);
 		loaders.put(
 				LockMode.FORCE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.FORCE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_READ,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_READ )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_WRITE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_WRITE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_FORCE_INCREMENT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_FORCE_INCREMENT )
 			);
 		loaders.put( LockMode.OPTIMISTIC, createEntityLoader( LockMode.OPTIMISTIC) );
 		loaders.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, createEntityLoader(LockMode.OPTIMISTIC_FORCE_INCREMENT) );
 
 		loaders.put(
 				"merge",
 				new CascadeEntityLoader( this, CascadingActions.MERGE, getFactory() )
 			);
 		loaders.put(
 				"refresh",
 				new CascadeEntityLoader( this, CascadingActions.REFRESH, getFactory() )
 			);
 	}
 
 	protected void createQueryLoader() {
 		if ( loaderName != null ) {
 			queryLoader = new NamedQueryLoader( loaderName, this );
 		}
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 		return load( id, optionalObject, new LockOptions().setLockMode(lockMode), session );
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 			throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Fetching entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		final UniqueEntityLoader loader = getAppropriateLoader(lockOptions, session );
 		return loader.load( id, optionalObject, session, lockOptions );
 	}
 
 	public void registerAffectingFetchProfile(String fetchProfileName) {
 		affectingFetchProfileNames.add( fetchProfileName );
 	}
 
 	private boolean isAffectedByEnabledFetchProfiles(SessionImplementor session) {
 		Iterator itr = session.getLoadQueryInfluencers().getEnabledFetchProfileNames().iterator();
 		while ( itr.hasNext() ) {
 			if ( affectingFetchProfileNames.contains( itr.next() ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() );
 	}
 
 	private UniqueEntityLoader getAppropriateLoader(LockOptions lockOptions, SessionImplementor session) {
 		if ( queryLoader != null ) {
 			// if the user specified a custom query loader we need to that
 			// regardless of any other consideration
 			return queryLoader;
 		}
 		else if ( isAffectedByEnabledFilters( session ) ) {
 			// because filters affect the rows returned (because they add
 			// restrictions) these need to be next in precedence
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( session.getLoadQueryInfluencers().getInternalFetchProfile() != null && LockMode.UPGRADE.greaterThan( lockOptions.getLockMode() ) ) {
 			// Next, we consider whether an 'internal' fetch profile has been set.
 			// This indicates a special fetch profile Hibernate needs applied
 			// (for its merge loading process e.g.).
 			return ( UniqueEntityLoader ) getLoaders().get( session.getLoadQueryInfluencers().getInternalFetchProfile() );
 		}
 		else if ( isAffectedByEnabledFetchProfiles( session ) ) {
 			// If the session has associated influencers we need to adjust the
 			// SQL query used for loading based on those influencers
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else {
 			return ( UniqueEntityLoader ) getLoaders().get( lockOptions.getLockMode() );
 		}
 	}
 
 	private boolean isAllNull(Object[] array, int tableNumber) {
 		for ( int i = 0; i < array.length; i++ ) {
 			if ( isPropertyOfTable( i, tableNumber ) && array[i] != null ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	public boolean isSubclassPropertyNullable(int i) {
 		return subclassPropertyNullabilityClosure[i];
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is dirty
 	 */
 	protected final boolean[] getPropertiesToUpdate(final int[] dirtyProperties, final boolean hasDirtyCollection) {
 		final boolean[] propsToUpdate = new boolean[ entityMetamodel.getPropertySpan() ];
 		final boolean[] updateability = getPropertyUpdateability(); //no need to check laziness, dirty checking handles that
 		for ( int j = 0; j < dirtyProperties.length; j++ ) {
 			int property = dirtyProperties[j];
 			if ( updateability[property] ) {
 				propsToUpdate[property] = true;
 			}
 		}
 		if ( isVersioned() && updateability[getVersionProperty() ]) {
 			propsToUpdate[ getVersionProperty() ] =
 				Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
 		}
 		return propsToUpdate;
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is insertable and non-null
 	 */
 	protected boolean[] getPropertiesToInsert(Object[] fields) {
 		boolean[] notNull = new boolean[fields.length];
 		boolean[] insertable = getPropertyInsertability();
 		for ( int i = 0; i < fields.length; i++ ) {
 			notNull[i] = insertable[i] && fields[i] != null;
 		}
 		return notNull;
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param currentState The current state of the entity (the state to be checked).
 	 * @param previousState The previous state of the entity (the state to be checked against).
 	 * @param entity The entity for which we are checking state dirtiness.
 	 * @param session The session in which the check is occurring.
 	 * @return <tt>null</tt> or the indices of the dirty properties
 	 * @throws HibernateException
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findDirty(
 				entityMetamodel.getProperties(),
 				currentState,
 				previousState,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param old The old state of the entity.
 	 * @param current The current state of the entity.
 	 * @param entity The entity for which we are checking state modification.
 	 * @param session The session in which the check is occurring.
 	 * @return <tt>null</tt> or the indices of the modified properties
 	 * @throws HibernateException
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findModified(
 				entityMetamodel.getProperties(),
 				current,
 				old,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Which properties appear in the SQL update?
 	 * (Initialized, updateable ones!)
 	 */
 	protected boolean[] getPropertyUpdateability(Object entity) {
 		return hasUninitializedLazyProperties( entity )
 				? getNonLazyPropertyUpdateability()
 				: getPropertyUpdateability();
 	}
 
 	private void logDirtyProperties(int[] props) {
 		if ( LOG.isTraceEnabled() ) {
 			for ( int i = 0; i < props.length; i++ ) {
 				String propertyName = entityMetamodel.getProperties()[ props[i] ].getName();
 				LOG.trace( StringHelper.qualify( getEntityName(), propertyName ) + " is dirty" );
 			}
 		}
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	public EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryHelper.getCacheEntryStructure();
 	}
 
 	@Override
 	public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 		return cacheEntryHelper.buildCacheEntry( entity, state, version, session );
 	}
 
 	public boolean hasNaturalIdCache() {
 		return naturalIdRegionAccessStrategy != null;
 	}
 	
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return naturalIdRegionAccessStrategy;
 	}
 
 	public Comparator getVersionComparator() {
 		return isVersioned() ? getVersionType().getComparator() : null;
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public final String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	public EntityType getEntityType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isPolymorphic() {
 		return entityMetamodel.isPolymorphic();
 	}
 
 	public boolean isInherited() {
 		return entityMetamodel.isInherited();
 	}
 
 	public boolean hasCascades() {
 		return entityMetamodel.hasCascades();
 	}
 
 	public boolean hasIdentifierProperty() {
 		return !entityMetamodel.getIdentifierProperty().isVirtual();
 	}
 
 	public VersionType getVersionType() {
 		return ( VersionType ) locateVersionType();
 	}
 
 	private Type locateVersionType() {
 		return entityMetamodel.getVersionProperty() == null ?
 				null :
 				entityMetamodel.getVersionProperty().getType();
 	}
 
 	public int getVersionProperty() {
 		return entityMetamodel.getVersionPropertyIndex();
 	}
 
 	public boolean isVersioned() {
 		return entityMetamodel.isVersioned();
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return entityMetamodel.getIdentifierProperty().isIdentifierAssignedByInsert();
 	}
 
 	public boolean hasLazyProperties() {
 		return entityMetamodel.hasLazyProperties();
 	}
 
 //	public boolean hasUninitializedLazyProperties(Object entity) {
 //		if ( hasLazyProperties() ) {
 //			InterceptFieldCallback callback = ( ( InterceptFieldEnabled ) entity ).getInterceptFieldCallback();
 //			return callback != null && !( ( FieldInterceptor ) callback ).isInitialized();
 //		}
 //		else {
 //			return false;
 //		}
 //	}
 
 	public void afterReassociate(Object entity, SessionImplementor session) {
 		if ( getEntityMetamodel().getInstrumentationMetadata().isInstrumented() ) {
 			FieldInterceptor interceptor = getEntityMetamodel().getInstrumentationMetadata().extractInterceptor( entity );
 			if ( interceptor != null ) {
 				interceptor.setSession( session );
 			}
 			else {
 				FieldInterceptor fieldInterceptor = getEntityMetamodel().getInstrumentationMetadata().injectInterceptor(
 						entity,
 						getEntityName(),
 						null,
 						session
 				);
 				fieldInterceptor.dirty();
 			}
 		}
 
 		handleNaturalIdReattachment( entity, session );
 	}
 
 	private void handleNaturalIdReattachment(Object entity, SessionImplementor session) {
 		if ( ! hasNaturalIdentifier() ) {
 			return;
 		}
 
 		if ( getEntityMetamodel().hasImmutableNaturalId() ) {
 			// we assume there were no changes to natural id during detachment for now, that is validated later
 			// during flush.
 			return;
 		}
 
 		final NaturalIdHelper naturalIdHelper = session.getPersistenceContext().getNaturalIdHelper();
 		final Serializable id = getIdentifier( entity, session );
 
 		// for reattachment of mutable natural-ids, we absolutely positively have to grab the snapshot from the
 		// database, because we have no other way to know if the state changed while detached.
 		final Object[] naturalIdSnapshot;
 		final Object[] entitySnapshot = session.getPersistenceContext().getDatabaseSnapshot( id, this );
 		if ( entitySnapshot == StatefulPersistenceContext.NO_ROW ) {
 			naturalIdSnapshot = null;
 		}
 		else {
 			naturalIdSnapshot = naturalIdHelper.extractNaturalIdValues( entitySnapshot, this );
 		}
 
 		naturalIdHelper.removeSharedNaturalIdCrossReference( this, id, naturalIdSnapshot );
 		naturalIdHelper.manageLocalNaturalIdCrossReference(
 				this,
 				id,
 				naturalIdHelper.extractNaturalIdValues( entity, this ),
 				naturalIdSnapshot,
 				CachedNaturalIdValueSource.UPDATE
 		);
 	}
 
 	public Boolean isTransient(Object entity, SessionImplementor session) throws HibernateException {
 		final Serializable id;
 		if ( canExtractIdOutOfEntity() ) {
 			id = getIdentifier( entity, session );
 		}
 		else {
 			id = null;
 		}
 		// we *always* assume an instance with a null
 		// identifier or no identifier property is unsaved!
 		if ( id == null ) {
 			return Boolean.TRUE;
 		}
 
 		// check the version unsaved-value, if appropriate
 		final Object version = getVersion( entity );
 		if ( isVersioned() ) {
 			// let this take precedence if defined, since it works for
 			// assigned identifiers
 			Boolean result = entityMetamodel.getVersionProperty()
 					.getUnsavedValue().isUnsaved( version );
 			if ( result != null ) {
 				return result;
 			}
 		}
 
 		// check the id unsaved-value
 		Boolean result = entityMetamodel.getIdentifierProperty()
 				.getUnsavedValue().isUnsaved( id );
diff --git a/hibernate-core/src/main/java/org/hibernate/type/EnumType.java b/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
index 6e25b8c36f..b73a608aa3 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
@@ -1,512 +1,515 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2012, Red Hat Inc. or third-party contributors as
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
 
 import javax.persistence.Enumerated;
 import javax.persistence.MapKeyEnumerated;
 import java.io.Serializable;
 import java.lang.annotation.Annotation;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.usertype.DynamicParameterizedType;
 import org.hibernate.usertype.EnhancedUserType;
 import org.hibernate.usertype.LoggableUserType;
 
 /**
  * Value type mapper for enumerations.
  *
  * Generally speaking, the proper configuration is picked up from the annotations associated with the mapped attribute.
  *
  * There are a few configuration parameters understood by this type mapper:<ul>
  *     <li>
  *         <strong>enumClass</strong> - Names the enumeration class.
  *     </li>
  *     <li>
  *         <strong>useNamed</strong> - Should enum be mapped via name.  Default is to map as ordinal.  Used when
  *         annotations are not used (otherwise {@link javax.persistence.EnumType} is used).
  *     </li>
  *     <li>
  *         <strong>type</strong> - Identifies the JDBC type (via type code) to be used for the column.
  *     </li>
  * </ul>
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  * @author Steve Ebersole
  */
 @SuppressWarnings("unchecked")
 public class EnumType implements EnhancedUserType, DynamicParameterizedType,LoggableUserType, Serializable {
     private static final Logger LOG = Logger.getLogger( EnumType.class.getName() );
 
 	public static final String ENUM = "enumClass";
 	public static final String NAMED = "useNamed";
 	public static final String TYPE = "type";
 
 	private Class<? extends Enum> enumClass;
 	private EnumValueMapper enumValueMapper;
 	private int sqlType = Types.INTEGER;  // before any guessing
 
 	@Override
 	public int[] sqlTypes() {
 		return new int[] { sqlType };
 	}
 
 	@Override
 	public Class<? extends Enum> returnedClass() {
 		return enumClass;
 	}
 
 	@Override
 	public boolean equals(Object x, Object y) throws HibernateException {
 		return x == y;
 	}
 
 	@Override
 	public int hashCode(Object x) throws HibernateException {
 		return x == null ? 0 : x.hashCode();
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws SQLException {
 		if ( enumValueMapper == null ) {
 			resolveEnumValueMapper( rs,  names[0] );
 		}
 		return enumValueMapper.getValue( rs, names );
 	}
 
 	private void resolveEnumValueMapper(ResultSet rs, String name) {
 		if ( enumValueMapper == null ) {
 			try {
 				resolveEnumValueMapper( rs.getMetaData().getColumnType( rs.findColumn( name ) ) );
 			}
 			catch (Exception e) {
 				// because some drivers do not implement this
 				LOG.debugf(
 						"JDBC driver threw exception calling java.sql.ResultSetMetaData.getColumnType; " +
 								"using fallback determination [%s] : %s",
 						enumClass.getName(),
 						e.getMessage()
 				);
 				// peek at the result value to guess type (this is legacy behavior)
 				try {
 					Object value = rs.getObject( name );
 					if ( Number.class.isInstance( value ) ) {
 						treatAsOrdinal();
 					}
 					else {
 						treatAsNamed();
 					}
 				}
 				catch (SQLException ignore) {
 					treatAsOrdinal();
 				}
 			}
 		}
 	}
 
 	private void resolveEnumValueMapper(int columnType) {
 		// fallback for cases where not enough parameter/parameterization information was passed in
 		if ( isOrdinal( columnType ) ) {
 			treatAsOrdinal();
 		}
 		else {
 			treatAsNamed();
 		}
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
 		if ( enumValueMapper == null ) {
 			resolveEnumValueMapper( st, index );
 		}
 		enumValueMapper.setValue( st, (Enum) value, index );
 	}
 
 	private void resolveEnumValueMapper(PreparedStatement st, int index) {
 		if ( enumValueMapper == null ) {
 			try {
 				resolveEnumValueMapper( st.getParameterMetaData().getParameterType( index ) );
 			}
 			catch (Exception e) {
 				// because some drivers do not implement this
 				LOG.debugf(
 						"JDBC driver threw exception calling java.sql.ParameterMetaData#getParameterType; " +
 								"falling back to ordinal-based enum mapping [%s] : %s",
 						enumClass.getName(),
 						e.getMessage()
 				);
 				treatAsOrdinal();
 			}
 		}
 	}
 
 	@Override
 	public Object deepCopy(Object value) throws HibernateException {
 		return value;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	@Override
 	public Serializable disassemble(Object value) throws HibernateException {
 		return ( Serializable ) value;
 	}
 
 	@Override
 	public Object assemble(Serializable cached, Object owner) throws HibernateException {
 		return cached;
 	}
 
 	@Override
 	public Object replace(Object original, Object target, Object owner) throws HibernateException {
 		return original;
 	}
 
 	@Override
 	public void setParameterValues(Properties parameters) {
 		final ParameterType reader = (ParameterType) parameters.get( PARAMETER_TYPE );
 
 		// IMPL NOTE : be protective about not setting enumValueMapper (i.e. calling treatAsNamed/treatAsOrdinal)
 		// in cases where we do not have enough information.  In such cases we do additional checks
 		// as part of nullSafeGet/nullSafeSet to query against the JDBC metadata to make the determination.
 
 		if ( reader != null ) {
 			enumClass = reader.getReturnedClass().asSubclass( Enum.class );
 
 			final boolean isOrdinal;
 			final javax.persistence.EnumType enumType = getEnumType( reader );
 			if ( enumType == null ) {
 				isOrdinal = true;
 			}
 			else if ( javax.persistence.EnumType.ORDINAL.equals( enumType ) ) {
 				isOrdinal = true;
 			}
 			else if ( javax.persistence.EnumType.STRING.equals( enumType ) ) {
 				isOrdinal = false;
 			}
 			else {
 				throw new AssertionFailure( "Unknown EnumType: " + enumType );
 			}
 
 			if ( isOrdinal ) {
 				treatAsOrdinal();
 			}
 			else {
 				treatAsNamed();
 			}
 			sqlType = enumValueMapper.getSqlType();
 		}
 		else {
 			String enumClassName = (String) parameters.get( ENUM );
 			try {
 				enumClass = ReflectHelper.classForName( enumClassName, this.getClass() ).asSubclass( Enum.class );
 			}
 			catch ( ClassNotFoundException exception ) {
 				throw new HibernateException( "Enum class not found", exception );
 			}
 
 			final Object useNamedSetting = parameters.get( NAMED );
 			if ( useNamedSetting != null ) {
 				final boolean useNamed = ConfigurationHelper.getBoolean( NAMED, parameters );
 				if ( useNamed ) {
 					treatAsNamed();
 				}
 				else {
 					treatAsOrdinal();
 				}
 				sqlType = enumValueMapper.getSqlType();
 			}
 		}
 
 		final String type = (String) parameters.get( TYPE );
 		if ( type != null ) {
 			sqlType = Integer.decode( type );
 		}
 	}
 
 	private void treatAsOrdinal() {
 		if ( enumValueMapper == null || ! OrdinalEnumValueMapper.class.isInstance( enumValueMapper ) ) {
 			enumValueMapper = new OrdinalEnumValueMapper();
 			sqlType = enumValueMapper.getSqlType();
 		}
 	}
 
 	private void treatAsNamed() {
 		if ( enumValueMapper == null || ! NamedEnumValueMapper.class.isInstance( enumValueMapper ) ) {
 			enumValueMapper = new NamedEnumValueMapper();
 			sqlType = enumValueMapper.getSqlType();
 		}
 	}
 
 	private javax.persistence.EnumType getEnumType(ParameterType reader) {
 		javax.persistence.EnumType enumType = null;
 		if ( reader.isPrimaryKey() ) {
 			MapKeyEnumerated enumAnn = getAnnotation( reader.getAnnotationsMethod(), MapKeyEnumerated.class );
 			if ( enumAnn != null ) {
 				enumType = enumAnn.value();
 			}
 		}
 		else {
 			Enumerated enumAnn = getAnnotation( reader.getAnnotationsMethod(), Enumerated.class );
 			if ( enumAnn != null ) {
 				enumType = enumAnn.value();
 			}
 		}
 		return enumType;
 	}
 
 	private <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> anClass) {
 		for ( Annotation annotation : annotations ) {
 			if ( anClass.isInstance( annotation ) ) {
 				return (T) annotation;
 			}
 		}
 		return null;
 	}
 
 	@Override
 	public String objectToSQLString(Object value) {
 		return enumValueMapper.objectToSQLString( (Enum) value );
 	}
 
 	@Override
 	public String toXMLString(Object value) {
 		return enumValueMapper.toXMLString( (Enum) value );
 	}
 
 	@Override
 	public Object fromXMLString(String xmlValue) {
 		return enumValueMapper.fromXMLString( xmlValue );
 	}
 
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) {
 		if ( enumValueMapper != null ) {
 			return enumValueMapper.toXMLString( (Enum) value );
 		}
 		return value.toString();
 	}
 
 	private static interface EnumValueMapper extends Serializable {
 		public int getSqlType();
 		public Enum getValue(ResultSet rs, String[] names) throws SQLException;
 		public void setValue(PreparedStatement st, Enum value, int index) throws SQLException;
 
 		public String objectToSQLString(Enum value);
 		public String toXMLString(Enum value);
 		public Enum fromXMLString(String xml);
 	}
 
 	public abstract class EnumValueMapperSupport implements EnumValueMapper {
 		protected abstract Object extractJdbcValue(Enum value);
 
 		@Override
 		public void setValue(PreparedStatement st, Enum value, int index) throws SQLException {
 			final Object jdbcValue = value == null ? null : extractJdbcValue( value );
 
+			final boolean traceEnabled = LOG.isTraceEnabled();
 			if ( jdbcValue == null ) {
-				if ( LOG.isTraceEnabled() ) {
+				if ( traceEnabled ) {
 					LOG.trace(String.format("Binding null to parameter: [%s]", index));
 				}
 				st.setNull( index, getSqlType() );
 				return;
 			}
 
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.trace(String.format("Binding [%s] to parameter: [%s]", jdbcValue, index));
 			}
 			st.setObject( index, jdbcValue, EnumType.this.sqlType );
 		}
 	}
 
 	private class OrdinalEnumValueMapper extends EnumValueMapperSupport implements EnumValueMapper, Serializable {
 		private transient Enum[] enumsByOrdinal;
 
 		@Override
 		public int getSqlType() {
 			return Types.INTEGER;
 		}
 
 		@Override
 		public Enum getValue(ResultSet rs, String[] names) throws SQLException {
 			final int ordinal = rs.getInt( names[0] );
+			final boolean traceEnabled = LOG.isTraceEnabled();
 			if ( rs.wasNull() ) {
-				if ( LOG.isTraceEnabled() ) {
+				if ( traceEnabled ) {
 					LOG.trace(String.format("Returning null as column [%s]", names[0]));
 				}
 				return null;
 			}
 
 			final Enum enumValue = fromOrdinal( ordinal );
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.trace(String.format("Returning [%s] as column [%s]", enumValue, names[0]));
 			}
 			return enumValue;
 		}
 
 		private Enum fromOrdinal(int ordinal) {
 			final Enum[] enumsByOrdinal = enumsByOrdinal();
 			if ( ordinal < 0 || ordinal >= enumsByOrdinal.length ) {
 				throw new IllegalArgumentException(
 						String.format(
 								"Unknown ordinal value [%s] for enum class [%s]",
 								ordinal,
 								enumClass.getName()
 						)
 				);
 			}
 			return enumsByOrdinal[ordinal];
 
 		}
 
 		private Enum[] enumsByOrdinal() {
 			if ( enumsByOrdinal == null ) {
 				enumsByOrdinal = enumClass.getEnumConstants();
 				if ( enumsByOrdinal == null ) {
 					throw new HibernateException( "Failed to init enum values" );
 				}
 			}
 			return enumsByOrdinal;
 		}
 
 		@Override
 		public String objectToSQLString(Enum value) {
 			return toXMLString( value );
 		}
 
 		@Override
 		public String toXMLString(Enum value) {
 			return Integer.toString( value.ordinal() );
 		}
 
 		@Override
 		public Enum fromXMLString(String xml) {
 			return fromOrdinal( Integer.parseInt( xml ) );
 		}
 
 		@Override
 		protected Object extractJdbcValue(Enum value) {
 			return value.ordinal();
 		}
 	}
 
 	private class NamedEnumValueMapper extends EnumValueMapperSupport implements EnumValueMapper, Serializable {
 		@Override
 		public int getSqlType() {
 			return Types.VARCHAR;
 		}
 
 		@Override
 		public Enum getValue(ResultSet rs, String[] names) throws SQLException {
 			final String value = rs.getString( names[0] );
 
+			final boolean traceEnabled = LOG.isTraceEnabled();
 			if ( rs.wasNull() ) {
-				if ( LOG.isTraceEnabled() ) {
+				if ( traceEnabled ) {
 					LOG.trace(String.format("Returning null as column [%s]", names[0]));
 				}
 				return null;
 			}
 
 			final Enum enumValue = fromName( value );
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.trace(String.format("Returning [%s] as column [%s]", enumValue, names[0]));
 			}
 			return enumValue;
 		}
 
 		private Enum fromName(String name) {
 			try {
 				return Enum.valueOf( enumClass, name );
 			}
 			catch ( IllegalArgumentException iae ) {
 				throw new IllegalArgumentException(
 						String.format(
 								"Unknown name value [%s] for enum class [%s]",
 								name,
 								enumClass.getName()
 						)
 				);
 			}
 		}
 
 		@Override
 		public String objectToSQLString(Enum value) {
 			return '\'' + toXMLString( value ) + '\'';
 		}
 
 		@Override
 		public String toXMLString(Enum value) {
 			return value.name();
 		}
 
 		@Override
 		public Enum fromXMLString(String xml) {
 			return fromName( xml );
 		}
 
 		@Override
 		protected Object extractJdbcValue(Enum value) {
 			return value.name();
 		}
 	}
 
 	public boolean isOrdinal() {
 		return isOrdinal( sqlType );
 	}
 
 	private boolean isOrdinal(int paramType) {
 		switch ( paramType ) {
 			case Types.INTEGER:
 			case Types.NUMERIC:
 			case Types.SMALLINT:
 			case Types.TINYINT:
 			case Types.BIGINT:
 			case Types.DECIMAL: //for Oracle Driver
 			case Types.DOUBLE:  //for Oracle Driver
 			case Types.FLOAT:   //for Oracle Driver
 				return true;
 			case Types.CHAR:
 			case Types.LONGVARCHAR:
 			case Types.VARCHAR:
 				return false;
 			default:
 				throw new HibernateException( "Unable to persist an Enum in a column of SQL Type: " + paramType );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java
index ff122f415d..5c24b7f99f 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java
@@ -1,107 +1,108 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.descriptor.JdbcTypeNameMapper;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Convenience base implementation of {@link ValueBinder}
  *
  * @author Steve Ebersole
  */
 public abstract class BasicBinder<J> implements ValueBinder<J> {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BasicBinder.class.getName());
 
     private static final String BIND_MSG_TEMPLATE = "binding parameter [%s] as [%s] - %s";
     private static final String NULL_BIND_MSG_TEMPLATE = "binding parameter [%s] as [%s] - <null>";
 
 	private final JavaTypeDescriptor<J> javaDescriptor;
 	private final SqlTypeDescriptor sqlDescriptor;
 
 	public JavaTypeDescriptor<J> getJavaDescriptor() {
 		return javaDescriptor;
 	}
 
 	public SqlTypeDescriptor getSqlDescriptor() {
 		return sqlDescriptor;
 	}
 
 	public BasicBinder(JavaTypeDescriptor<J> javaDescriptor, SqlTypeDescriptor sqlDescriptor) {
 		this.javaDescriptor = javaDescriptor;
 		this.sqlDescriptor = sqlDescriptor;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public final void bind(PreparedStatement st, J value, int index, WrapperOptions options) throws SQLException {
+        final boolean traceEnabled = LOG.isTraceEnabled();
         if ( value == null ) {
-            if ( LOG.isTraceEnabled() ) {
+            if ( traceEnabled ) {
                 LOG.trace(
                         String.format(
                                 NULL_BIND_MSG_TEMPLATE,
                                 index,
                                 JdbcTypeNameMapper.getTypeName( sqlDescriptor.getSqlType() )
                         )
                 );
             }
             st.setNull( index, sqlDescriptor.getSqlType() );
         }
         else {
-            if ( LOG.isTraceEnabled() ) {
+            if ( traceEnabled ) {
                 LOG.trace(
                         String.format(
                                 BIND_MSG_TEMPLATE,
                                 index,
                                 JdbcTypeNameMapper.getTypeName( sqlDescriptor.getSqlType() ),
                                 getJavaDescriptor().extractLoggableRepresentation( value )
                         )
                 );
             }
             doBind( st, value, index, options );
         }
 	}
 
 	/**
 	 * Perform the binding.  Safe to assume that value is not null.
 	 *
 	 * @param st The prepared statement
 	 * @param value The value to bind (not null).
 	 * @param index The index at which to bind
 	 * @param options The binding options
 	 *
 	 * @throws SQLException Indicates a problem binding to the prepared statement.
 	 */
 	protected abstract void doBind(PreparedStatement st, J value, int index, WrapperOptions options) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
index 7f0f939db9..6db5dd1d19 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
@@ -1,158 +1,167 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Convenience base implementation of {@link org.hibernate.type.descriptor.ValueExtractor}
  *
  * @author Steve Ebersole
  */
 public abstract class BasicExtractor<J> implements ValueExtractor<J> {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, BasicExtractor.class.getName() );
 
 	private final JavaTypeDescriptor<J> javaDescriptor;
 	private final SqlTypeDescriptor sqlDescriptor;
 
 	public BasicExtractor(JavaTypeDescriptor<J> javaDescriptor, SqlTypeDescriptor sqlDescriptor) {
 		this.javaDescriptor = javaDescriptor;
 		this.sqlDescriptor = sqlDescriptor;
 	}
 
 	public JavaTypeDescriptor<J> getJavaDescriptor() {
 		return javaDescriptor;
 	}
 
 	public SqlTypeDescriptor getSqlDescriptor() {
 		return sqlDescriptor;
 	}
 
 	@Override
 	public J extract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 		final J value = doExtract( rs, name, options );
+		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( value == null || rs.wasNull() ) {
-			LOG.tracev( "Found [null] as column [{0}]", name );
+			if ( traceEnabled ) {
+				LOG.tracev( "Found [null] as column [{0}]", name );
+			}
 			return null;
 		}
 		else {
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev( "Found [{0}] as column [{1}]", getJavaDescriptor().extractLoggableRepresentation( value ), name );
 			}
 			return value;
 		}
 	}
 
 	/**
 	 * Perform the extraction.
 	 * <p/>
 	 * Called from {@link #extract}.  Null checking of the value (as well as consulting {@link ResultSet#wasNull}) is
 	 * done there.
 	 *
 	 * @param rs The result set
 	 * @param name The value name in the result set
 	 * @param options The binding options
 	 *
 	 * @return The extracted value.
 	 *
 	 * @throws SQLException Indicates a problem access the result set
 	 */
 	protected abstract J doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException;
 
 	@Override
 	public J extract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 		final J value = doExtract( statement, index, options );
+		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( value == null || statement.wasNull() ) {
-			LOG.tracev( "Found [null] as procedure output  parameter [{0}]", index );
+			if ( traceEnabled ) {
+				LOG.tracev( "Found [null] as procedure output  parameter [{0}]", index );
+			}
 			return null;
 		}
 		else {
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev( "Found [{0}] as procedure output parameter [{1}]", getJavaDescriptor().extractLoggableRepresentation( value ), index );
 			}
 			return value;
 		}
 	}
 
 	/**
 	 * Perform the extraction.
 	 * <p/>
 	 * Called from {@link #extract}.  Null checking of the value (as well as consulting {@link ResultSet#wasNull}) is
 	 * done there.
 	 *
 	 * @param statement The callable statement containing the output parameter
 	 * @param index The index (position) of the output parameter
 	 * @param options The binding options
 	 *
 	 * @return The extracted value.
 	 *
 	 * @throws SQLException Indicates a problem accessing the parameter value
 	 */
 	protected abstract J doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException;
 
 	@Override
 	public J extract(CallableStatement statement, String[] paramNames, WrapperOptions options) throws SQLException {
 		if ( paramNames.length > 1 ) {
 			throw new IllegalArgumentException( "Basic value extraction cannot handle multiple output parameters" );
 		}
 		final String paramName = paramNames[0];
 		final J value = doExtract( statement, paramName, options );
+		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( value == null || statement.wasNull() ) {
-			LOG.tracev( "Found [null] as procedure output  parameter [{0}]", paramName );
+			if ( traceEnabled ) {
+				LOG.tracev( "Found [null] as procedure output  parameter [{0}]", paramName );
+			}
 			return null;
 		}
 		else {
-			if ( LOG.isTraceEnabled() ) {
+			if ( traceEnabled ) {
 				LOG.tracev( "Found [{0}] as procedure output parameter [{1}]", getJavaDescriptor().extractLoggableRepresentation( value ), paramName );
 			}
 			return value;
 		}
 	}
 
 	/**
 	 * Perform the extraction.
 	 * <p/>
 	 * Called from {@link #extract}.  Null checking of the value (as well as consulting {@link ResultSet#wasNull}) is
 	 * done there.
 	 *
 	 * @param statement The callable statement containing the output parameter
 	 * @param name The output parameter name
 	 * @param options The binding options
 	 *
 	 * @return The extracted value.
 	 *
 	 * @throws SQLException Indicates a problem accessing the parameter value
 	 */
 	protected abstract J doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException;
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/ConcurrentWriteTest.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/ConcurrentWriteTest.java
index 9b236fc368..0abcec7353 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/ConcurrentWriteTest.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/ConcurrentWriteTest.java
@@ -1,550 +1,550 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.cache.infinispan.functional;
 
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Random;
 import java.util.Set;
 import java.util.concurrent.Callable;
 import java.util.concurrent.CyclicBarrier;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import java.util.concurrent.Future;
 import java.util.concurrent.TimeUnit;
 import javax.transaction.TransactionManager;
 
 import org.infinispan.util.logging.Log;
 import org.infinispan.util.logging.LogFactory;
 import org.junit.Test;
 
 import org.hibernate.FlushMode;
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 import org.hibernate.test.cache.infinispan.functional.cluster.DualNodeConnectionProviderImpl;
 import org.hibernate.test.cache.infinispan.functional.cluster.DualNodeJtaPlatformImpl;
 import org.hibernate.test.cache.infinispan.functional.cluster.DualNodeJtaTransactionManagerImpl;
 import org.hibernate.test.cache.infinispan.functional.cluster.DualNodeTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 
 /**
  * @author nikita_tovstoles@mba.berkeley.edu
  * @author Galder Zamarreño
  */
 public class ConcurrentWriteTest extends SingleNodeTestCase {
 	private static final Log log = LogFactory.getLog( ConcurrentWriteTest.class );
 	private static final boolean trace = log.isTraceEnabled();
 	/**
 	 * when USER_COUNT==1, tests pass, when >4 tests fail
 	 */
 	private static final int USER_COUNT = 5;
 	private static final int ITERATION_COUNT = 150;
 	private static final int THINK_TIME_MILLIS = 10;
 	private static final long LAUNCH_INTERVAL_MILLIS = 10;
 	private static final Random random = new Random();
 
 	/**
 	 * kill switch used to stop all users when one fails
 	 */
 	private static volatile boolean TERMINATE_ALL_USERS = false;
 
 	/**
 	 * collection of IDs of all customers participating in this test
 	 */
 	private Set<Integer> customerIDs = new HashSet<Integer>();
 
 	private TransactionManager tm;
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( DualNodeTestCase.NODE_ID_PROP, DualNodeTestCase.LOCAL );
 		cfg.setProperty( DualNodeTestCase.NODE_ID_FIELD, DualNodeTestCase.LOCAL );
 	}
 
 	@Override
 	protected boolean getUseQueryCache() {
 		return true;
 	}
 
 	@Override
 	protected TransactionManager getTransactionManager() {
 		return DualNodeJtaTransactionManagerImpl.getInstance( DualNodeTestCase.LOCAL );
 	}
 
 	@Override
 	protected Class<? extends ConnectionProvider> getConnectionProviderClass() {
 		return DualNodeConnectionProviderImpl.class;
 	}
 
 	@Override
 	protected Class<? extends JtaPlatform> getJtaPlatform() {
 		return DualNodeJtaPlatformImpl.class;
 	}
 
 	@Override
 	protected void prepareTest() throws Exception {
 		super.prepareTest();
 		TERMINATE_ALL_USERS = false;
 	}
 
 	@Override
 	protected void cleanupTest() throws Exception {
 		try {
 			super.cleanupTest();
 		}
 		finally {
 			cleanup();
 			// DualNodeJtaTransactionManagerImpl.cleanupTransactions();
 			// DualNodeJtaTransactionManagerImpl.cleanupTransactionManagers();
 		}
 	}
 
 	@Test
 	public void testPingDb() throws Exception {
 		try {
 			beginTx();
 			sessionFactory()
 					.getCurrentSession()
 					.createQuery( "from " + Customer.class.getName() )
 					.list();
 		}
 		catch (Exception e) {
 			setRollbackOnlyTx( e );
 //         setRollbackOnly();
 //         fail("failed to query DB; exception=" + e);
 		}
 		finally {
 			commitOrRollbackTx();
 		}
 	}
 
 	@Test
 	public void testSingleUser() throws Exception {
 		// setup
 sessionFactory().getStatistics().clear();
 		Customer customer = createCustomer( 0 );
 		final Integer customerId = customer.getId();
 		getCustomerIDs().add( customerId );
 
 		assertNull( "contact exists despite not being added", getFirstContact( customerId ) );
 
 		// check that cache was hit
 		SecondLevelCacheStatistics customerSlcs = sessionFactory()
 				.getStatistics()
 				.getSecondLevelCacheStatistics( Customer.class.getName() );
 		assertEquals( customerSlcs.getPutCount(), 1 );
 		assertEquals( customerSlcs.getElementCountInMemory(), 1 );
 		assertEquals( customerSlcs.getEntries().size(), 1 );
 
 		log.infof( "Add contact to customer {0}", customerId );
 		SecondLevelCacheStatistics contactsCollectionSlcs = sessionFactory()
 				.getStatistics()
 				.getSecondLevelCacheStatistics( Customer.class.getName() + ".contacts" );
 		assertEquals( 1, contactsCollectionSlcs.getPutCount() );
 		assertEquals( 1, contactsCollectionSlcs.getElementCountInMemory() );
 		assertEquals( 1, contactsCollectionSlcs.getEntries().size() );
 
 		final Contact contact = addContact( customerId );
 		assertNotNull( "contact returned by addContact is null", contact );
 		assertEquals(
 				"Customer.contacts cache was not invalidated after addContact", 0,
 				contactsCollectionSlcs.getElementCountInMemory()
 		);
 
 		assertNotNull( "Contact missing after successful add call", getFirstContact( customerId ) );
 
 		// read everyone's contacts
 		readEveryonesFirstContact();
 
 		removeContact( customerId );
 		assertNull( "contact still exists after successful remove call", getFirstContact( customerId ) );
 
 	}
 
 	@Test
 	public void testManyUsers() throws Throwable {
 		try {
 			// setup - create users
 			for ( int i = 0; i < USER_COUNT; i++ ) {
 				Customer customer = createCustomer( 0 );
 				getCustomerIDs().add( customer.getId() );
 			}
 			assertEquals( "failed to create enough Customers", USER_COUNT, getCustomerIDs().size() );
 
 			final ExecutorService executor = Executors.newFixedThreadPool( USER_COUNT );
 
 			CyclicBarrier barrier = new CyclicBarrier( USER_COUNT + 1 );
 			List<Future<Void>> futures = new ArrayList<Future<Void>>( USER_COUNT );
 			for ( Integer customerId : getCustomerIDs() ) {
 				Future<Void> future = executor.submit( new UserRunner( customerId, barrier ) );
 				futures.add( future );
 				Thread.sleep( LAUNCH_INTERVAL_MILLIS ); // rampup
 			}
 //         barrier.await(); // wait for all threads to be ready
 			barrier.await( 2, TimeUnit.MINUTES ); // wait for all threads to finish
 			log.info( "All threads finished, let's shutdown the executor and check whether any exceptions were reported" );
 			for ( Future<Void> future : futures ) {
 				future.get();
 			}
 			log.info( "All future gets checked" );
 		}
 		catch (Throwable t) {
 			log.error( "Error running test", t );
 			throw t;
 		}
 	}
 
 	public void cleanup() throws Exception {
 		getCustomerIDs().clear();
 		String deleteContactHQL = "delete from Contact";
 		String deleteCustomerHQL = "delete from Customer";
 		beginTx();
 		try {
 			Session session = sessionFactory().getCurrentSession();
 			session.createQuery( deleteContactHQL ).setFlushMode( FlushMode.AUTO ).executeUpdate();
 			session.createQuery( deleteCustomerHQL ).setFlushMode( FlushMode.AUTO ).executeUpdate();
 		}
 		catch (Exception e) {
 			setRollbackOnlyTx( e );
 		}
 		finally {
 			commitOrRollbackTx();
 		}
 	}
 
 	private Customer createCustomer(int nameSuffix) throws Exception {
 		Customer customer = null;
 		beginTx();
 		try {
 			customer = new Customer();
 			customer.setName( "customer_" + nameSuffix );
 			customer.setContacts( new HashSet<Contact>() );
 			sessionFactory().getCurrentSession().persist( customer );
 		}
 		catch (Exception e) {
 			setRollbackOnlyTx( e );
 		}
 		finally {
 			commitOrRollbackTx();
 		}
 		return customer;
 	}
 
 	/**
 	 * read first contact of every Customer participating in this test. this forces concurrent cache
 	 * writes of Customer.contacts Collection cache node
 	 *
 	 * @return who cares
 	 * @throws java.lang.Exception
 	 */
 	private void readEveryonesFirstContact() throws Exception {
 		beginTx();
 		try {
 			for ( Integer customerId : getCustomerIDs() ) {
 				if ( TERMINATE_ALL_USERS ) {
 					setRollbackOnlyTx();
 					return;
 				}
 				Customer customer = (Customer) sessionFactory()
 						.getCurrentSession()
 						.load( Customer.class, customerId );
 				Set<Contact> contacts = customer.getContacts();
 				if ( !contacts.isEmpty() ) {
 					contacts.iterator().next();
 				}
 			}
 		}
 		catch (Exception e) {
 			setRollbackOnlyTx( e );
 		}
 		finally {
 			commitOrRollbackTx();
 		}
 	}
 
    /**
     * -load existing Customer -get customer's contacts; return 1st one
     * 
     * @param customerId
     * @return first Contact or null if customer has none
     */
    private Contact getFirstContact(Integer customerId) throws Exception {
       assert customerId != null;
       Contact firstContact = null;
       beginTx();
       try {
          final Customer customer = (Customer) sessionFactory()
 				 .getCurrentSession()
 				 .load(Customer.class, customerId);
          Set<Contact> contacts = customer.getContacts();
          firstContact = contacts.isEmpty() ? null : contacts.iterator().next();
          if (TERMINATE_ALL_USERS)
             setRollbackOnlyTx();
       } catch (Exception e) {
          setRollbackOnlyTx(e);
       } finally {
          commitOrRollbackTx();
       }
       return firstContact;
    }
 
    /**
     * -load existing Customer -create a new Contact and add to customer's contacts
     *
     * @param customerId
     * @return added Contact
     */
    private Contact addContact(Integer customerId) throws Exception {
       assert customerId != null;
       Contact contact = null;
       beginTx();
       try {
          final Customer customer = (Customer) sessionFactory()
 				 .getCurrentSession()
 				 .load(Customer.class, customerId);
          contact = new Contact();
          contact.setName("contact name");
          contact.setTlf("wtf is tlf?");
          contact.setCustomer(customer);
          customer.getContacts().add(contact);
          // assuming contact is persisted via cascade from customer
          if (TERMINATE_ALL_USERS)
             setRollbackOnlyTx();
       } catch (Exception e) {
          setRollbackOnlyTx(e);
       } finally {
          commitOrRollbackTx();
       }
       return contact;
    }
 
    /**
     * remove existing 'contact' from customer's list of contacts
     *
     * @param customerId
     * @throws IllegalStateException
     *            if customer does not own a contact
     */
    private void removeContact(Integer customerId) throws Exception {
       assert customerId != null;
 
 		beginTx();
 		try {
 			Customer customer = (Customer) sessionFactory()
 					.getCurrentSession()
 					.load( Customer.class, customerId );
 			Set<Contact> contacts = customer.getContacts();
 			if ( contacts.size() != 1 ) {
 				throw new IllegalStateException(
 						"can't remove contact: customer id=" + customerId
 								+ " expected exactly 1 contact, " + "actual count=" + contacts.size()
 				);
 			}
 
 			Contact contact = contacts.iterator().next();
 			contacts.remove( contact );
 			contact.setCustomer( null );
 
 			// explicitly delete Contact because hbm has no 'DELETE_ORPHAN' cascade?
 			// getEnvironment().getSessionFactory().getCurrentSession().delete(contact); //appears to
 			// not be needed
 
 			// assuming contact is persisted via cascade from customer
 
 			if ( TERMINATE_ALL_USERS ) {
 				setRollbackOnlyTx();
 			}
 		}
 		catch (Exception e) {
 			setRollbackOnlyTx( e );
 		}
 		finally {
 			commitOrRollbackTx();
 		}
 	}
 
 	/**
 	 * @return the customerIDs
 	 */
 	public Set<Integer> getCustomerIDs() {
 		return customerIDs;
 	}
 
 	private String statusOfRunnersToString(Set<UserRunner> runners) {
 		assert runners != null;
 
 		StringBuilder sb = new StringBuilder(
 				"TEST CONFIG [userCount=" + USER_COUNT
 						+ ", iterationsPerUser=" + ITERATION_COUNT + ", thinkTimeMillis="
 						+ THINK_TIME_MILLIS + "] " + " STATE of UserRunners: "
 		);
 
 		for ( UserRunner r : runners ) {
 			sb.append( r.toString() + System.getProperty( "line.separator" ) );
 		}
 		return sb.toString();
 	}
 
 	class UserRunner implements Callable<Void> {
 		private final CyclicBarrier barrier;
 		final private Integer customerId;
 		private int completedIterations = 0;
 		private Throwable causeOfFailure;
 
 		public UserRunner(Integer cId, CyclicBarrier barrier) {
 			assert cId != null;
 			this.customerId = cId;
 			this.barrier = barrier;
 		}
 
 		private boolean contactExists() throws Exception {
 			return getFirstContact( customerId ) != null;
 		}
 
 		public Void call() throws Exception {
 			// name this thread for easier log tracing
 			Thread.currentThread().setName( "UserRunnerThread-" + getCustomerId() );
 			log.info( "Wait for all executions paths to be ready to perform calls" );
 			try {
 //            barrier.await();
 				for ( int i = 0; i < ITERATION_COUNT && !TERMINATE_ALL_USERS; i++ ) {
 					contactExists();
 					if ( trace ) {
 						log.trace( "Add contact for customer " + customerId );
 					}
 					addContact( customerId );
 					if ( trace ) {
 						log.trace( "Added contact" );
 					}
 					thinkRandomTime();
 					contactExists();
 					thinkRandomTime();
 					if ( trace ) {
 						log.trace( "Read all customers' first contact" );
 					}
 					// read everyone's contacts
 					readEveryonesFirstContact();
 					if ( trace ) {
 						log.trace( "Read completed" );
 					}
 					thinkRandomTime();
 					if ( trace ) {
 						log.trace( "Remove contact of customer" + customerId );
 					}
 					removeContact( customerId );
 					if ( trace ) {
 						log.trace( "Removed contact" );
 					}
 					contactExists();
 					thinkRandomTime();
 					++completedIterations;
-					if ( log.isTraceEnabled() ) {
+					if ( trace ) {
 						log.tracef( "Iteration completed {0}", completedIterations );
 					}
 				}
 			}
 			catch (Throwable t) {
 				TERMINATE_ALL_USERS = true;
 				log.error( "Error", t );
 				throw new Exception( t );
 				// rollback current transaction if any
 				// really should not happen since above methods all follow begin-commit-rollback pattern
 				// try {
 				// if
 				// (DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestUtil.LOCAL).getTransaction()
 				// != null) {
 				// DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestUtil.LOCAL).rollback();
 				// }
 				// } catch (SystemException ex) {
 				// throw new RuntimeException("failed to rollback tx", ex);
 				// }
 			}
 			finally {
 				log.info( "Wait for all execution paths to finish" );
 				barrier.await();
 			}
 			return null;
 		}
 
 		public boolean isSuccess() {
 			return ITERATION_COUNT == getCompletedIterations();
 		}
 
 		public int getCompletedIterations() {
 			return completedIterations;
 		}
 
 		public Throwable getCauseOfFailure() {
 			return causeOfFailure;
 		}
 
 		public Integer getCustomerId() {
 			return customerId;
 		}
 
 		@Override
 		public String toString() {
 			return super.toString() + "[customerId=" + getCustomerId() + " iterationsCompleted="
 					+ getCompletedIterations() + " completedAll=" + isSuccess() + " causeOfFailure="
 					+ (this.causeOfFailure != null ? getStackTrace( causeOfFailure ) : "") + "] ";
 		}
 	}
 
 	public static String getStackTrace(Throwable throwable) {
 		StringWriter sw = new StringWriter();
 		PrintWriter pw = new PrintWriter( sw, true );
 		throwable.printStackTrace( pw );
 		return sw.getBuffer().toString();
 	}
 
 	/**
 	 * sleep between 0 and THINK_TIME_MILLIS.
 	 *
 	 * @throws RuntimeException if sleep is interrupted or TERMINATE_ALL_USERS flag was set to true i n the
 	 * meantime
 	 */
 	private void thinkRandomTime() {
 		try {
 			Thread.sleep( random.nextInt( THINK_TIME_MILLIS ) );
 		}
 		catch (InterruptedException ex) {
 			throw new RuntimeException( "sleep interrupted", ex );
 		}
 
 		if ( TERMINATE_ALL_USERS ) {
 			throw new RuntimeException( "told to terminate (because a UserRunner had failed)" );
 		}
 	}
 
 }
