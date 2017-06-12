diff --git a/hibernate-core/src/main/java/org/hibernate/UnsupportedLockAttemptException.java b/hibernate-core/src/main/java/org/hibernate/UnsupportedLockAttemptException.java
new file mode 100644
index 0000000000..4f669d3e81
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/UnsupportedLockAttemptException.java
@@ -0,0 +1,44 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate;
+
+/**
+ * This exception is thrown when an invalid LockMode is selected for an entity. This occurs if the
+ * user tries to set an inappropriate LockMode for an entity.
+ *
+ * @author John O'Hara
+ */
+public class UnsupportedLockAttemptException extends HibernateException {
+	public UnsupportedLockAttemptException(String message) {
+		super( message );
+	}
+
+	public UnsupportedLockAttemptException(Throwable cause) {
+		super( cause );
+	}
+
+	public UnsupportedLockAttemptException(String message, Throwable cause) {
+		super( message, cause );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/AbstractEntityEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/AbstractEntityEntry.java
new file mode 100644
index 0000000000..b56c7e9cfa
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/AbstractEntityEntry.java
@@ -0,0 +1,668 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010-2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.engine.internal;
+
+import org.hibernate.AssertionFailure;
+import org.hibernate.CustomEntityDirtinessStrategy;
+import org.hibernate.EntityMode;
+import org.hibernate.HibernateException;
+import org.hibernate.LockMode;
+import org.hibernate.Session;
+import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
+import org.hibernate.engine.spi.CachedNaturalIdValueSource;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.EntityEntryExtraState;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SelfDirtinessTracker;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.UniqueKeyLoadable;
+import org.hibernate.pretty.MessageHelper;
+
+import java.io.IOException;
+import java.io.ObjectOutputStream;
+import java.io.Serializable;
+
+/**
+ * We need an entry to tell us all about the current state of an mutable object with respect to its persistent state
+ *
+ * Implementation Warning: Hibernate needs to instantiate a high amount of instances of this class,
+ * therefore we need to take care of its impact on memory consumption.
+ *
+ * @author Gavin King
+ * @author Emmanuel Bernard <emmanuel@hibernate.org>
+ * @author Gunnar Morling
+ * @author Sanne Grinovero  <sanne@hibernate.org>
+ */
+public abstract class AbstractEntityEntry implements Serializable, EntityEntry {
+	protected final Serializable id;
+	protected Object[] loadedState;
+	protected Object version;
+	protected final EntityPersister persister; // permanent but we only need the entityName state in a non transient way
+	protected transient EntityKey cachedEntityKey; // cached EntityKey (lazy-initialized)
+	protected final transient Object rowId;
+	protected final transient PersistenceContext persistenceContext;
+	protected EntityEntryExtraState next;
+
+	/**
+	 * Holds several boolean and enum typed attributes in a very compact manner. Enum values are stored in 4 bits
+	 * (where 0 represents {@code null}, and each enum value is represented by its ordinal value + 1), thus allowing
+	 * for up to 15 values per enum. Boolean values are stored in one bit.
+	 * <p>
+	 * The value is structured as follows:
+	 *
+	 * <pre>
+	 * 1 - Lock mode
+	 * 2 - Status
+	 * 3 - Previous Status
+	 * 4 - existsInDatabase
+	 * 5 - isBeingReplicated
+	 * 6 - loadedWithLazyPropertiesUnfetched; NOTE: this is not updated when properties are fetched lazily!
+	 *
+	 * 0000 0000 | 0000 0000 | 0654 3333 | 2222 1111
+	 * </pre>
+	 * Use {@link #setCompressedValue(org.hibernate.engine.internal.AbstractEntityEntry.EnumState, Enum)},
+	 * {@link #getCompressedValue(org.hibernate.engine.internal.AbstractEntityEntry.EnumState, Class)} etc
+	 * to access the enums and booleans stored in this value.
+	 * <p>
+	 * Representing enum values by their ordinal value is acceptable for our case as this value itself is never
+	 * serialized or deserialized and thus is not affected should ordinal values change.
+	 */
+	private transient int compressedState;
+
+	/**
+	 * @deprecated the tenantId and entityMode parameters where removed: this constructor accepts but ignores them.
+	 * Use the other constructor!
+	 */
+	@Deprecated
+	public AbstractEntityEntry(
+			final Status status,
+			final Object[] loadedState,
+			final Object rowId,
+			final Serializable id,
+			final Object version,
+			final LockMode lockMode,
+			final boolean existsInDatabase,
+			final EntityPersister persister,
+			final EntityMode entityMode,
+			final String tenantId,
+			final boolean disableVersionIncrement,
+			final boolean lazyPropertiesAreUnfetched,
+			final PersistenceContext persistenceContext) {
+		this( status, loadedState, rowId, id, version, lockMode, existsInDatabase,
+				persister,disableVersionIncrement, lazyPropertiesAreUnfetched, persistenceContext );
+	}
+
+	public AbstractEntityEntry(
+			final Status status,
+			final Object[] loadedState,
+			final Object rowId,
+			final Serializable id,
+			final Object version,
+			final LockMode lockMode,
+			final boolean existsInDatabase,
+			final EntityPersister persister,
+			final boolean disableVersionIncrement,
+			final boolean lazyPropertiesAreUnfetched,
+			final PersistenceContext persistenceContext) {
+		setCompressedValue( EnumState.STATUS, status );
+		// not useful strictly speaking but more explicit
+		setCompressedValue( EnumState.PREVIOUS_STATUS, null );
+		// only retain loaded state if the status is not Status.READ_ONLY
+		if ( status != Status.READ_ONLY ) {
+			this.loadedState = loadedState;
+		}
+		this.id=id;
+		this.rowId=rowId;
+		setCompressedValue( BooleanState.EXISTS_IN_DATABASE, existsInDatabase );
+		this.version=version;
+		setCompressedValue( EnumState.LOCK_MODE, lockMode );
+		setCompressedValue( BooleanState.IS_BEING_REPLICATED, disableVersionIncrement );
+		setCompressedValue( BooleanState.LOADED_WITH_LAZY_PROPERTIES_UNFETCHED, lazyPropertiesAreUnfetched );
+		this.persister=persister;
+		this.persistenceContext = persistenceContext;
+	}
+
+	/**
+	 * This for is used during custom deserialization handling
+	 */
+	@SuppressWarnings( {"JavaDoc"})
+	protected AbstractEntityEntry(
+			final SessionFactoryImplementor factory,
+			final String entityName,
+			final Serializable id,
+			final Status status,
+			final Status previousStatus,
+			final Object[] loadedState,
+			final Object[] deletedState,
+			final Object version,
+			final LockMode lockMode,
+			final boolean existsInDatabase,
+			final boolean isBeingReplicated,
+			final boolean loadedWithLazyPropertiesUnfetched,
+			final PersistenceContext persistenceContext) {
+		this.persister = ( factory == null ? null : factory.getEntityPersister( entityName ) );
+		this.id = id;
+		setCompressedValue( EnumState.STATUS, status );
+		setCompressedValue( EnumState.PREVIOUS_STATUS, previousStatus );
+		this.loadedState = loadedState;
+		setDeletedState( deletedState );
+		this.version = version;
+		setCompressedValue( EnumState.LOCK_MODE, lockMode );
+		setCompressedValue( BooleanState.EXISTS_IN_DATABASE, existsInDatabase );
+		setCompressedValue( BooleanState.IS_BEING_REPLICATED, isBeingReplicated );
+		setCompressedValue( BooleanState.LOADED_WITH_LAZY_PROPERTIES_UNFETCHED, loadedWithLazyPropertiesUnfetched );
+		this.rowId = null; // this is equivalent to the old behavior...
+		this.persistenceContext = persistenceContext;
+	}
+
+	@Override
+	public LockMode getLockMode() {
+		return getCompressedValue( EnumState.LOCK_MODE );
+	}
+
+	@Override
+	public void setLockMode(LockMode lockMode) {
+		setCompressedValue( EnumState.LOCK_MODE, lockMode );
+	}
+
+
+	@Override
+	public Status getStatus() {
+		return getCompressedValue( EnumState.STATUS );
+	}
+
+	private Status getPreviousStatus() {
+		return getCompressedValue( EnumState.PREVIOUS_STATUS );
+	}
+
+	@Override
+	public void setStatus(Status status) {
+		if ( status == Status.READ_ONLY ) {
+			//memory optimization
+			loadedState = null;
+		}
+
+		final Status currentStatus = this.getStatus();
+
+		if ( currentStatus != status ) {
+			setCompressedValue( EnumState.PREVIOUS_STATUS, currentStatus );
+			setCompressedValue( EnumState.STATUS, status );
+		}
+	}
+
+	@Override
+	public Serializable getId() {
+		return id;
+	}
+
+	@Override
+	public Object[] getLoadedState() {
+		return loadedState;
+	}
+
+	private static final Object[] DEFAULT_DELETED_STATE = null;
+
+	@Override
+	public Object[] getDeletedState() {
+		final EntityEntryExtraStateHolder extra = getExtraState( EntityEntryExtraStateHolder.class );
+		return extra != null ? extra.getDeletedState() : DEFAULT_DELETED_STATE;
+	}
+
+	@Override
+	public void setDeletedState(Object[] deletedState) {
+		EntityEntryExtraStateHolder extra = getExtraState( EntityEntryExtraStateHolder.class );
+		if ( extra == null && deletedState == DEFAULT_DELETED_STATE ) {
+			//this is the default value and we do not store the extra state
+			return;
+		}
+		if ( extra == null ) {
+			extra = new EntityEntryExtraStateHolder();
+			addExtraState( extra );
+		}
+		extra.setDeletedState( deletedState );
+	}
+
+	@Override
+	public boolean isExistsInDatabase() {
+		return getCompressedValue( BooleanState.EXISTS_IN_DATABASE );
+	}
+
+	@Override
+	public Object getVersion() {
+		return version;
+	}
+
+	@Override
+	public EntityPersister getPersister() {
+		return persister;
+	}
+
+	@Override
+	public EntityKey getEntityKey() {
+		if ( cachedEntityKey == null ) {
+			if ( getId() == null ) {
+				throw new IllegalStateException( "cannot generate an EntityKey when id is null.");
+			}
+			cachedEntityKey = new EntityKey( getId(), getPersister() );
+		}
+		return cachedEntityKey;
+	}
+
+	@Override
+	public String getEntityName() {
+		return persister == null ? null : persister.getEntityName();
+
+	}
+
+	@Override
+	public boolean isBeingReplicated() {
+		return getCompressedValue( BooleanState.IS_BEING_REPLICATED );
+	}
+
+	@Override
+	public Object getRowId() {
+		return rowId;
+	}
+
+	@Override
+	public void postUpdate(Object entity, Object[] updatedState, Object nextVersion) {
+		this.loadedState = updatedState;
+		setLockMode( LockMode.WRITE );
+
+		if ( getPersister().isVersioned() ) {
+			this.version = nextVersion;
+			getPersister().setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
+		}
+
+		if ( getPersister().getInstrumentationMetadata().isInstrumented() ) {
+			final FieldInterceptor interceptor = getPersister().getInstrumentationMetadata().extractInterceptor( entity );
+			if ( interceptor != null ) {
+				interceptor.clearDirty();
+			}
+		}
+
+		if( entity instanceof SelfDirtinessTracker ) {
+			((SelfDirtinessTracker) entity).$$_hibernate_clearDirtyAttributes();
+		}
+
+		persistenceContext.getSession()
+				.getFactory()
+				.getCustomEntityDirtinessStrategy()
+				.resetDirty( entity, getPersister(), (Session) persistenceContext.getSession() );
+	}
+
+	@Override
+	public void postDelete() {
+		setCompressedValue( EnumState.PREVIOUS_STATUS, getStatus() );
+		setCompressedValue( EnumState.STATUS, Status.GONE );
+		setCompressedValue( BooleanState.EXISTS_IN_DATABASE, false );
+	}
+
+	@Override
+	public void postInsert(Object[] insertedState) {
+		setCompressedValue( BooleanState.EXISTS_IN_DATABASE, true );
+	}
+
+	@Override
+	public boolean isNullifiable(boolean earlyInsert, SessionImplementor session) {
+		if ( getStatus() == Status.SAVING ) {
+			return true;
+		}
+		else if ( earlyInsert ) {
+			return !isExistsInDatabase();
+		}
+		else {
+			return session.getPersistenceContext().getNullifiableEntityKeys().contains( getEntityKey() );
+		}
+	}
+
+	@Override
+	public Object getLoadedValue(String propertyName) {
+		if ( loadedState == null || propertyName == null ) {
+			return null;
+		}
+		else {
+			final int propertyIndex = ( (UniqueKeyLoadable) persister ).getPropertyIndex( propertyName );
+			return loadedState[propertyIndex];
+		}
+	}
+
+	@Override
+	public boolean requiresDirtyCheck(Object entity) {
+		return isModifiableEntity()
+				&& ( !isUnequivocallyNonDirty( entity ) );
+	}
+
+	@SuppressWarnings( {"SimplifiableIfStatement"})
+	private boolean isUnequivocallyNonDirty(Object entity) {
+
+		if(entity instanceof SelfDirtinessTracker) {
+			return ((SelfDirtinessTracker) entity).$$_hibernate_hasDirtyAttributes();
+		}
+
+		final CustomEntityDirtinessStrategy customEntityDirtinessStrategy =
+				persistenceContext.getSession().getFactory().getCustomEntityDirtinessStrategy();
+		if ( customEntityDirtinessStrategy.canDirtyCheck( entity, getPersister(), (Session) persistenceContext.getSession() ) ) {
+			return ! customEntityDirtinessStrategy.isDirty( entity, getPersister(), (Session) persistenceContext.getSession() );
+		}
+
+		if ( getPersister().hasMutableProperties() ) {
+			return false;
+		}
+
+		if ( getPersister().getInstrumentationMetadata().isInstrumented() ) {
+			// the entity must be instrumented (otherwise we cant check dirty flag) and the dirty flag is false
+			return ! getPersister().getInstrumentationMetadata().extractInterceptor( entity ).isDirty();
+		}
+
+		return false;
+	}
+
+	@Override
+	public boolean isModifiableEntity() {
+		final Status status = getStatus();
+		final Status previousStatus = getPreviousStatus();
+		return getPersister().isMutable()
+				&& status != Status.READ_ONLY
+				&& ! ( status == Status.DELETED && previousStatus == Status.READ_ONLY );
+	}
+
+	@Override
+	public void forceLocked(Object entity, Object nextVersion) {
+		version = nextVersion;
+		loadedState[ persister.getVersionProperty() ] = version;
+		// TODO:  use LockMode.PESSIMISTIC_FORCE_INCREMENT
+		//noinspection deprecation
+		setLockMode( LockMode.FORCE );
+		persister.setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
+	}
+
+	@Override
+	public boolean isReadOnly() {
+		final Status status = getStatus();
+		if (status != Status.MANAGED && status != Status.READ_ONLY) {
+			throw new HibernateException("instance was not in a valid state");
+		}
+		return status == Status.READ_ONLY;
+	}
+
+	@Override
+	public void setReadOnly(boolean readOnly, Object entity) {
+		if ( readOnly == isReadOnly() ) {
+			// simply return since the status is not being changed
+			return;
+		}
+		if ( readOnly ) {
+			setStatus( Status.READ_ONLY );
+			loadedState = null;
+		}
+		else {
+			if ( ! persister.isMutable() ) {
+				throw new IllegalStateException( "Cannot make an immutable entity modifiable." );
+			}
+			setStatus( Status.MANAGED );
+			loadedState = getPersister().getPropertyValues( entity );
+			persistenceContext.getNaturalIdHelper().manageLocalNaturalIdCrossReference(
+					persister,
+					id,
+					loadedState,
+					null,
+					CachedNaturalIdValueSource.LOAD
+			);
+		}
+	}
+
+	@Override
+	public String toString() {
+		return "EntityEntry" +
+				MessageHelper.infoString( getPersister().getEntityName(), id ) +
+				'(' + getStatus() + ')';
+	}
+
+	@Override
+	public boolean isLoadedWithLazyPropertiesUnfetched() {
+		return getCompressedValue( BooleanState.LOADED_WITH_LAZY_PROPERTIES_UNFETCHED );
+	}
+
+	@Override
+	public void serialize(ObjectOutputStream oos) throws IOException {
+		final Status previousStatus = getPreviousStatus();
+		oos.writeObject( getEntityName() );
+		oos.writeObject( id );
+		oos.writeObject( getStatus().name() );
+		oos.writeObject( (previousStatus == null ? "" : previousStatus.name()) );
+		// todo : potentially look at optimizing these two arrays
+		oos.writeObject( loadedState );
+		oos.writeObject( getDeletedState() );
+		oos.writeObject( version );
+		oos.writeObject( getLockMode().toString() );
+		oos.writeBoolean( isExistsInDatabase() );
+		oos.writeBoolean( isBeingReplicated() );
+		oos.writeBoolean( isLoadedWithLazyPropertiesUnfetched() );
+	}
+
+
+	@Override
+	public void addExtraState(EntityEntryExtraState extraState) {
+		if ( next == null ) {
+			next = extraState;
+		}
+		else {
+			next.addExtraState( extraState );
+		}
+	}
+
+	@Override
+	public <T extends EntityEntryExtraState> T getExtraState(Class<T> extraStateType) {
+		if ( next == null ) {
+			return null;
+		}
+		if ( extraStateType.isAssignableFrom( next.getClass() ) ) {
+			return (T) next;
+		}
+		else {
+			return next.getExtraState( extraStateType );
+		}
+	}
+
+	public PersistenceContext getPersistenceContext(){
+		return persistenceContext;
+	}
+
+	/**
+	 * Saves the value for the given enum property.
+	 *
+	 * @param state
+	 *            identifies the value to store
+	 * @param value
+	 *            the value to store; The caller must make sure that it matches
+	 *            the given identifier
+	 */
+	protected <E extends Enum<E>> void setCompressedValue(EnumState<E> state, E value) {
+		// reset the bits for the given property to 0
+		compressedState &= state.getUnsetMask();
+		// store the numeric representation of the enum value at the right offset
+		compressedState |= ( state.getValue( value ) << state.getOffset() );
+	}
+
+	/**
+	 * Gets the current value of the given enum property.
+	 *
+	 * @param state
+	 *            identifies the value to store
+	 * @return the current value of the specified property
+	 */
+	protected <E extends Enum<E>> E getCompressedValue(EnumState<E> state) {
+		// restore the numeric value from the bits at the right offset and return the corresponding enum constant
+		final int index = ( ( compressedState & state.getMask() ) >> state.getOffset() ) - 1;
+		return index == - 1 ? null : state.getEnumConstants()[index];
+	}
+
+	/**
+	 * Saves the value for the given boolean flag.
+	 *
+	 * @param state
+	 *            identifies the value to store
+	 * @param value
+	 *            the value to store
+	 */
+	protected void setCompressedValue(BooleanState state, boolean value) {
+		compressedState &= state.getUnsetMask();
+		compressedState |= ( state.getValue( value ) << state.getOffset() );
+	}
+
+	/**
+	 * Gets the current value of the given boolean flag.
+	 *
+	 * @param state
+	 *            identifies the value to store
+	 * @return the current value of the specified flag
+	 */
+	protected boolean getCompressedValue(BooleanState state) {
+		return ( ( compressedState & state.getMask() ) >> state.getOffset() ) == 1;
+	}
+
+	/**
+	 * Represents an enum value stored within a number value, using four bits starting at a specified offset.
+	 *
+	 * @author Gunnar Morling
+	 */
+	protected static class EnumState<E extends Enum<E>> {
+
+		protected static final EnumState<LockMode> LOCK_MODE = new EnumState<LockMode>( 0, LockMode.class );
+		protected static final EnumState<Status> STATUS = new EnumState<Status>( 4, Status.class );
+		protected static final EnumState<Status> PREVIOUS_STATUS = new EnumState<Status>( 8, Status.class );
+
+		protected final int offset;
+		protected final E[] enumConstants;
+		protected final int mask;
+		protected final int unsetMask;
+
+		private EnumState(int offset, Class<E> enumType) {
+			final E[] enumConstants = enumType.getEnumConstants();
+
+			// In case any of the enums cannot be stored in 4 bits anymore, we'd have to re-structure the compressed
+			// state int
+			if ( enumConstants.length > 15 ) {
+				throw new AssertionFailure( "Cannot store enum type " + enumType.getName() + " in compressed state as"
+						+ " it has too many values." );
+			}
+
+			this.offset = offset;
+			this.enumConstants = enumConstants;
+
+			// a mask for reading the four bits, starting at the right offset
+			this.mask = 0xF << offset;
+
+			// a mask for setting the four bits at the right offset to 0
+			this.unsetMask = 0xFFFF & ~mask;
+		}
+
+		/**
+		 * Returns the numeric value to be stored for the given enum value.
+		 */
+		private int getValue(E value) {
+			return value != null ? value.ordinal() + 1 : 0;
+		}
+
+		/**
+		 * Returns the offset within the number value at which this enum value is stored.
+		 */
+		private int getOffset() {
+			return offset;
+		}
+
+		/**
+		 * Returns the bit mask for reading this enum value from the number value storing it.
+		 */
+		private int getMask() {
+			return mask;
+		}
+
+		/**
+		 * Returns the bit mask for resetting this enum value from the number value storing it.
+		 */
+		private int getUnsetMask() {
+			return unsetMask;
+		}
+
+		/**
+		 * Returns the constants of the represented enum which is cached for performance reasons.
+		 */
+		private E[] getEnumConstants() {
+			return enumConstants;
+		}
+	}
+
+	/**
+	 * Represents a boolean flag stored within a number value, using one bit at a specified offset.
+	 *
+	 * @author Gunnar Morling
+	 */
+	protected enum BooleanState {
+
+		EXISTS_IN_DATABASE(13),
+		IS_BEING_REPLICATED(14),
+		LOADED_WITH_LAZY_PROPERTIES_UNFETCHED(15);
+
+		private final int offset;
+		private final int mask;
+		private final int unsetMask;
+
+		private BooleanState(int offset) {
+			this.offset = offset;
+			this.mask = 0x1 << offset;
+			this.unsetMask = 0xFFFF & ~mask;
+		}
+
+		private int getValue(boolean value) {
+			return value ? 1 : 0;
+		}
+
+		/**
+		 * Returns the offset within the number value at which this boolean flag is stored.
+		 */
+		private int getOffset() {
+			return offset;
+		}
+
+		/**
+		 * Returns the bit mask for reading this flag from the number value storing it.
+		 */
+		private int getMask() {
+			return mask;
+		}
+
+		/**
+		 * Returns the bit mask for resetting this flag from the number value storing it.
+		 */
+		private int getUnsetMask() {
+			return unsetMask;
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/EntityEntryContext.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/EntityEntryContext.java
index 6d53713e73..93e880fd90 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/EntityEntryContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/EntityEntryContext.java
@@ -1,510 +1,574 @@
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
 package org.hibernate.engine.internal;
 
+import org.hibernate.LockMode;
+import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.ManagedEntity;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.jboss.logging.Logger;
+
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
+import java.lang.reflect.InvocationTargetException;
+import java.lang.reflect.Method;
 import java.util.IdentityHashMap;
 import java.util.Map;
 
-import org.hibernate.LockMode;
-import org.hibernate.engine.spi.EntityEntry;
-import org.hibernate.engine.spi.ManagedEntity;
-
-import org.jboss.logging.Logger;
-
 /**
  * Defines a context for maintaining the relation between an entity associated with the Session ultimately owning this
  * EntityEntryContext instance and that entity's corresponding EntityEntry.  2 approaches are supported:<ul>
  *     <li>
  *         the entity->EntityEntry association is maintained in a Map within this class
  *     </li>
  *     <li>
  *         the EntityEntry is injected into the entity via it implementing the {@link org.hibernate.engine.spi.ManagedEntity} contract,
  *         either directly or through bytecode enhancement.
  *     </li>
  * </ul>
  * <p/>
  *
  * @author Steve Ebersole
  */
 public class EntityEntryContext {
 	private static final Logger log = Logger.getLogger( EntityEntryContext.class );
 
 	private transient ManagedEntity head;
 	private transient ManagedEntity tail;
 	private transient int count;
 
 	private transient IdentityHashMap<Object,ManagedEntity> nonEnhancedEntityXref;
 
 	@SuppressWarnings( {"unchecked"})
 	private transient Map.Entry<Object,EntityEntry>[] reentrantSafeEntries = new Map.Entry[0];
 	private transient boolean dirty;
 
 	/**
 	 * Constructs a EntityEntryContext
 	 */
 	public EntityEntryContext() {
 	}
 
 	/**
 	 * Adds the entity and entry to this context, associating them together
 	 *
 	 * @param entity The entity
 	 * @param entityEntry The entry
 	 */
 	public void addEntityEntry(Object entity, EntityEntry entityEntry) {
 		// IMPORTANT!!!!!
 		//		add is called more than once of some entities.  In such cases the first
 		//		call is simply setting up a "marker" to avoid infinite looping from reentrancy
 
 		// any addition (even the double one described above) should invalidate the cross-ref array
 		dirty = true;
 
 		// determine the appropriate ManagedEntity instance to use based on whether the entity is enhanced or not.
 		// also track whether the entity was already associated with the context
 		final ManagedEntity managedEntity;
 		final boolean alreadyAssociated;
 		if ( ManagedEntity.class.isInstance( entity ) ) {
 			managedEntity = (ManagedEntity) entity;
 			alreadyAssociated = managedEntity.$$_hibernate_getEntityEntry() != null;
 		}
 		else {
 			ManagedEntity wrapper = null;
 			if ( nonEnhancedEntityXref == null ) {
 				nonEnhancedEntityXref = new IdentityHashMap<Object, ManagedEntity>();
 			}
 			else {
 				wrapper = nonEnhancedEntityXref.get( entity );
 			}
 
 			if ( wrapper == null ) {
 				wrapper = new ManagedEntityImpl( entity );
 				nonEnhancedEntityXref.put( entity, wrapper );
 				alreadyAssociated = false;
 			}
 			else {
 				alreadyAssociated = true;
 			}
 
 			managedEntity = wrapper;
 		}
 
 		// associate the EntityEntry with the entity
 		managedEntity.$$_hibernate_setEntityEntry( entityEntry );
 
 		if ( alreadyAssociated ) {
 			// if the entity was already associated with the context, skip the linking step.
 			return;
 		}
 
 		// finally, set up linking and count
 		if ( tail == null ) {
 			assert head == null;
 			head = managedEntity;
 			tail = head;
 			count = 1;
 		}
 		else {
 			tail.$$_hibernate_setNextManagedEntity( managedEntity );
 			managedEntity.$$_hibernate_setPreviousManagedEntity( tail );
 			tail = managedEntity;
 			count++;
 		}
 	}
 
 	/**
 	 * Does this entity exist in this context, associated with an EntityEntry?
 	 *
 	 * @param entity The entity to check
 	 *
 	 * @return {@code true} if it is associated with this context
 	 */
 	public boolean hasEntityEntry(Object entity) {
 		return getEntityEntry( entity ) != null;
 	}
 
 	/**
 	 * Retrieve the associated EntityEntry for the entity
 	 *
 	 * @param entity The entity to retrieve the EntityEntry for
 	 *
 	 * @return The associated EntityEntry
 	 */
 	public EntityEntry getEntityEntry(Object entity) {
 		// essentially resolve the entity to a ManagedEntity...
 		final ManagedEntity managedEntity;
 		if ( ManagedEntity.class.isInstance( entity ) ) {
 			managedEntity = (ManagedEntity) entity;
 		}
 		else if ( nonEnhancedEntityXref == null ) {
 			managedEntity = null;
 		}
 		else {
 			managedEntity = nonEnhancedEntityXref.get( entity );
 		}
 
 		// and get/return the EntityEntry from the ManagedEntry
 		return managedEntity == null
 				? null
 				: managedEntity.$$_hibernate_getEntityEntry();
 	}
 
 	/**
 	 * Remove an entity from the context, returning the EntityEntry which was associated with it
 	 *
 	 * @param entity The entity to remove
 	 *
 	 * @return Tjee EntityEntry
 	 */
 	public EntityEntry removeEntityEntry(Object entity) {
 		dirty = true;
 
 		// again, resolve the entity to a ManagedEntity (which may not be possible for non-enhanced)...
 		final ManagedEntity managedEntity;
 		if ( ManagedEntity.class.isInstance( entity ) ) {
 			managedEntity = (ManagedEntity) entity;
 		}
 		else if ( nonEnhancedEntityXref == null ) {
 			managedEntity = null;
 		}
 		else {
 			managedEntity = nonEnhancedEntityXref.remove( entity );
 		}
 
 		// if we could not resolve it, just return (it was not associated with this context)
 		if ( managedEntity == null ) {
 			return null;
 		}
 
 		// prepare for re-linking...
 		final ManagedEntity previous = managedEntity.$$_hibernate_getPreviousManagedEntity();
 		final ManagedEntity next = managedEntity.$$_hibernate_getNextManagedEntity();
 		managedEntity.$$_hibernate_setPreviousManagedEntity( null );
 		managedEntity.$$_hibernate_setNextManagedEntity( null );
 
 		// re-link
 		count--;
 
 		if ( count == 0 ) {
 			// handle as a special case...
 			head = null;
 			tail = null;
 
 			assert previous == null;
 			assert next == null;
 		}
 		else {
 			// otherwise, previous or next (or both) should be non-null
 			if ( previous == null ) {
 				// we are removing head
 				assert managedEntity == head;
 				head = next;
 			}
 			else {
 				previous.$$_hibernate_setNextManagedEntity( next );
 			}
 
 			if ( next == null ) {
 				// we are removing tail
 				assert managedEntity == tail;
 				tail = previous;
 			}
 			else {
 				next.$$_hibernate_setPreviousManagedEntity( previous );
 			}
 		}
 
 		// finally clean out the ManagedEntity and return the associated EntityEntry
 		final EntityEntry theEntityEntry = managedEntity.$$_hibernate_getEntityEntry();
-		managedEntity.$$_hibernate_setEntityEntry( null );
+		// need to think about implications for memory leaks here if we don't removed reference to EntityEntry
+		if( canClearEntityEntryReference(managedEntity) ){
+			managedEntity.$$_hibernate_setEntityEntry( null );
+		}
 		return theEntityEntry;
 	}
 
 	/**
 	 * The main bugaboo with IdentityMap that warranted this class in the first place.
 	 *
 	 * Return an array of all the entity/EntityEntry pairs in this context.  The array is to make sure
 	 * that the iterators built off of it are safe from concurrency/reentrancy
 	 *
 	 * @return The safe array
 	 */
 	public Map.Entry<Object, EntityEntry>[] reentrantSafeEntityEntries() {
 		if ( dirty ) {
 			reentrantSafeEntries = new EntityEntryCrossRefImpl[count];
 			int i = 0;
 			ManagedEntity managedEntity = head;
 			while ( managedEntity != null ) {
 				reentrantSafeEntries[i++] = new EntityEntryCrossRefImpl(
 						managedEntity.$$_hibernate_getEntityInstance(),
 						managedEntity.$$_hibernate_getEntityEntry()
 				);
 				managedEntity = managedEntity.$$_hibernate_getNextManagedEntity();
 			}
 			dirty = false;
 		}
 		return reentrantSafeEntries;
 	}
 
 	/**
 	 * Clear this context of all managed entities
 	 */
 	public void clear() {
 		dirty = true;
 
 		ManagedEntity node = head;
 		while ( node != null ) {
 			final ManagedEntity nextNode = node.$$_hibernate_getNextManagedEntity();
 
-			node.$$_hibernate_setEntityEntry( null );
+			if( canClearEntityEntryReference(node) ){
+				node.$$_hibernate_setEntityEntry( null );
+			}
+
 			node.$$_hibernate_setPreviousManagedEntity( null );
 			node.$$_hibernate_setNextManagedEntity( null );
 
 			node = nextNode;
 		}
 
 		if ( nonEnhancedEntityXref != null ) {
 			nonEnhancedEntityXref.clear();
 		}
 
 		head = null;
 		tail = null;
 		count = 0;
 
 		reentrantSafeEntries = null;
 	}
 
 	/**
 	 * Down-grade locks to NONE for all entities in this context
 	 */
 	public void downgradeLocks() {
 		if ( head == null ) {
 			return;
 		}
 
 		ManagedEntity node = head;
 		while ( node != null ) {
 			node.$$_hibernate_getEntityEntry().setLockMode( LockMode.NONE );
 
 			node = node.$$_hibernate_getNextManagedEntity();
 		}
 	}
 
 	/**
 	 * JDK serialization hook for serializing
 	 *
 	 * @param oos The stream to write ourselves to
 	 *
 	 * @throws IOException Indicates an IO exception accessing the given stream
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		log.tracef( "Starting serialization of [%s] EntityEntry entries", count );
 		oos.writeInt( count );
 		if ( count == 0 ) {
 			return;
 		}
 
 		ManagedEntity managedEntity = head;
 		while ( managedEntity != null ) {
 			// so we know whether or not to build a ManagedEntityImpl on deserialize
 			oos.writeBoolean( managedEntity == managedEntity.$$_hibernate_getEntityInstance() );
 			oos.writeObject( managedEntity.$$_hibernate_getEntityInstance() );
+			// we need to know which implementation of EntityEntry is being serialized
+			oos.writeInt( managedEntity.$$_hibernate_getEntityEntry().getClass().getName().length() );
+			oos.writeChars( managedEntity.$$_hibernate_getEntityEntry().getClass().getName() );
 			managedEntity.$$_hibernate_getEntityEntry().serialize( oos );
 
 			managedEntity = managedEntity.$$_hibernate_getNextManagedEntity();
 		}
 	}
 
 	/**
 	 * JDK serialization hook for deserializing
 	 *
 	 * @param ois The stream to read ourselves from
 	 * @param rtn The persistence context we belong to
 	 *
 	 * @return The deserialized EntityEntryContext
 	 *
 	 * @throws IOException Indicates an IO exception accessing the given stream
 	 * @throws ClassNotFoundException Problem reading stream data
 	 */
 	public static EntityEntryContext deserialize(ObjectInputStream ois, StatefulPersistenceContext rtn)
 			throws IOException, ClassNotFoundException {
 		final int count = ois.readInt();
 		log.tracef( "Starting deserialization of [%s] EntityEntry entries", count );
 
 		final EntityEntryContext context = new EntityEntryContext();
 		context.count = count;
 		context.dirty = true;
 
 		if ( count == 0 ) {
 			return context;
 		}
 
 		ManagedEntity previous = null;
 
 		for ( int i = 0; i < count; i++ ) {
 			final boolean isEnhanced = ois.readBoolean();
 			final Object entity = ois.readObject();
-			final EntityEntry entry = MutableEntityEntry.deserialize(ois, rtn);
+
+			//Call deserialize method dynamically via reflection
+			final int numChars = ois.readInt();
+			final char[] entityEntryClassNameArr = new char[numChars];
+			for ( int j = 0; j < numChars; j++ ) {
+				entityEntryClassNameArr[j] = ois.readChar();
+			}
+
+			final EntityEntry entry = deserializeEntityEntry( entityEntryClassNameArr, ois, rtn );
+
 			final ManagedEntity managedEntity;
 			if ( isEnhanced ) {
 				managedEntity = (ManagedEntity) entity;
 			}
 			else {
 				managedEntity = new ManagedEntityImpl( entity );
 				if ( context.nonEnhancedEntityXref == null ) {
 					context.nonEnhancedEntityXref = new IdentityHashMap<Object, ManagedEntity>();
 				}
 				context.nonEnhancedEntityXref.put( entity, managedEntity );
 			}
 			managedEntity.$$_hibernate_setEntityEntry( entry );
 
 			if ( previous == null ) {
 				context.head = managedEntity;
 			}
 			else {
 				previous.$$_hibernate_setNextManagedEntity( managedEntity );
 				managedEntity.$$_hibernate_setPreviousManagedEntity( previous );
 			}
 
 			previous = managedEntity;
 		}
 
 		context.tail = previous;
 
 		return context;
 	}
 
+	private static EntityEntry deserializeEntityEntry(char[] entityEntryClassNameArr, ObjectInputStream ois, StatefulPersistenceContext rtn){
+		EntityEntry entry = null;
+
+		final String entityEntryClassName = new String( entityEntryClassNameArr );
+		final Class entityEntryClass =   rtn.getSession().getFactory().getServiceRegistry().getService( ClassLoaderService.class ).classForName( entityEntryClassName );
+
+		try {
+			final Method deserializeMethod = entityEntryClass.getDeclaredMethod( "deserialize", ObjectInputStream.class,	PersistenceContext.class );
+			entry = (EntityEntry) deserializeMethod.invoke( null, ois, rtn );
+		}
+		catch (NoSuchMethodException e) {
+			log.errorf( "Enable to deserialize [%s]", entityEntryClassName );
+		}
+		catch (InvocationTargetException e) {
+			log.errorf( "Enable to deserialize [%s]", entityEntryClassName );
+		}
+		catch (IllegalAccessException e) {
+			log.errorf( "Enable to deserialize [%s]", entityEntryClassName );
+		}
+
+		return entry;
+
+	}
+
 	public int getNumberOfManagedEntities() {
 		return count;
 	}
 
+	/*
+	Check instance type of EntityEntry and if type is ImmutableEntityEntry, check to see if entity is referenced cached in the second level cache
+	 */
+	private boolean canClearEntityEntryReference(ManagedEntity managedEntity){
+
+		if( managedEntity.$$_hibernate_getEntityEntry() == null ) {
+			return true;
+		}
+
+		if( !(managedEntity.$$_hibernate_getEntityEntry() instanceof ImmutableEntityEntry) ) {
+			return true;
+		}
+		else if( managedEntity.$$_hibernate_getEntityEntry().getPersister().canUseReferenceCacheEntries() ) {
+			return false;
+		}
+
+		return true;
+
+	}
 	/**
 	 * The wrapper for entity classes which do not implement ManagedEntity
 	 */
 	private static class ManagedEntityImpl implements ManagedEntity {
 		private final Object entityInstance;
 		private EntityEntry entityEntry;
 		private ManagedEntity previous;
 		private ManagedEntity next;
 
 		public ManagedEntityImpl(Object entityInstance) {
 			this.entityInstance = entityInstance;
 		}
 
 		@Override
 		public Object $$_hibernate_getEntityInstance() {
 			return entityInstance;
 		}
 
 		@Override
 		public EntityEntry $$_hibernate_getEntityEntry() {
 			return entityEntry;
 		}
 
 		@Override
 		public void $$_hibernate_setEntityEntry(EntityEntry entityEntry) {
 			this.entityEntry = entityEntry;
 		}
 
 		@Override
 		public ManagedEntity $$_hibernate_getNextManagedEntity() {
 			return next;
 		}
 
 		@Override
 		public void $$_hibernate_setNextManagedEntity(ManagedEntity next) {
 			this.next = next;
 		}
 
 		@Override
 		public ManagedEntity $$_hibernate_getPreviousManagedEntity() {
 			return previous;
 		}
 
 		@Override
 		public void $$_hibernate_setPreviousManagedEntity(ManagedEntity previous) {
 			this.previous = previous;
 		}
 	}
 
 	/**
 	 * Used in building the {@link #reentrantSafeEntityEntries()} entries
 	 */
 	public static interface EntityEntryCrossRef extends Map.Entry<Object,EntityEntry> {
 		/**
 		 * The entity
 		 *
 		 * @return The entity
 		 */
 		public Object getEntity();
 
 		/**
 		 * The associated EntityEntry
 		 *
 		 * @return The EntityEntry associated with the entity in this context
 		 */
 		public EntityEntry getEntityEntry();
 	}
 
 	/**
 	 * Implementation of the EntityEntryCrossRef interface
 	 */
 	private static class EntityEntryCrossRefImpl implements EntityEntryCrossRef {
 		private final Object entity;
 		private EntityEntry entityEntry;
 
 		private EntityEntryCrossRefImpl(Object entity, EntityEntry entityEntry) {
 			this.entity = entity;
 			this.entityEntry = entityEntry;
 		}
 
 		@Override
 		public Object getEntity() {
 			return entity;
 		}
 
 		@Override
 		public EntityEntry getEntityEntry() {
 			return entityEntry;
 		}
 
 		@Override
 		public Object getKey() {
 			return getEntity();
 		}
 
 		@Override
 		public EntityEntry getValue() {
 			return getEntityEntry();
 		}
 
 		@Override
 		public EntityEntry setValue(EntityEntry entityEntry) {
 			final EntityEntry old = this.entityEntry;
 			this.entityEntry = entityEntry;
 			return old;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/ImmutableEntityEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/ImmutableEntityEntry.java
new file mode 100644
index 0000000000..43c2180007
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/ImmutableEntityEntry.java
@@ -0,0 +1,194 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010-2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.engine.internal;
+
+import org.hibernate.EntityMode;
+import org.hibernate.LockMode;
+import org.hibernate.UnsupportedLockAttemptException;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Status;
+import org.hibernate.persister.entity.EntityPersister;
+
+import java.io.IOException;
+import java.io.ObjectInputStream;
+import java.io.Serializable;
+
+/**
+ * We need an entry to tell us all about the current state of an mutable object with respect to its persistent state
+ *
+ * Implementation Warning: Hibernate needs to instantiate a high amount of instances of this class,
+ * therefore we need to take care of its impact on memory consumption.
+ *
+ * @author Gavin King
+ * @author Emmanuel Bernard <emmanuel@hibernate.org>
+ * @author Gunnar Morling
+ * @author Sanne Grinovero  <sanne@hibernate.org>
+ */
+public final class ImmutableEntityEntry extends AbstractEntityEntry {
+
+	/**
+	 * Holds several boolean and enum typed attributes in a very compact manner. Enum values are stored in 4 bits
+	 * (where 0 represents {@code null}, and each enum value is represented by its ordinal value + 1), thus allowing
+	 * for up to 15 values per enum. Boolean values are stored in one bit.
+	 * <p>
+	 * The value is structured as follows:
+	 *
+	 * <pre>
+	 * 1 - Lock mode
+	 * 2 - Status
+	 * 3 - Previous Status
+	 * 4 - existsInDatabase
+	 * 5 - isBeingReplicated
+	 * 6 - loadedWithLazyPropertiesUnfetched; NOTE: this is not updated when properties are fetched lazily!
+	 *
+	 * 0000 0000 | 0000 0000 | 0654 3333 | 2222 1111
+	 * </pre>
+	 * Use {@link #setCompressedValue(org.hibernate.engine.internal.ImmutableEntityEntry.EnumState, Enum)},
+	 * {@link #getCompressedValue(org.hibernate.engine.internal.ImmutableEntityEntry.EnumState, Class)} etc
+	 * to access the enums and booleans stored in this value.
+	 * <p>
+	 * Representing enum values by their ordinal value is acceptable for our case as this value itself is never
+	 * serialized or deserialized and thus is not affected should ordinal values change.
+	 */
+	private transient int compressedState;
+
+	/**
+	 * @deprecated the tenantId and entityMode parameters where removed: this constructor accepts but ignores them.
+	 * Use the other constructor!
+	 */
+	@Deprecated
+	public ImmutableEntityEntry(
+			final Status status,
+			final Object[] loadedState,
+			final Object rowId,
+			final Serializable id,
+			final Object version,
+			final LockMode lockMode,
+			final boolean existsInDatabase,
+			final EntityPersister persister,
+			final EntityMode entityMode,
+			final String tenantId,
+			final boolean disableVersionIncrement,
+			final boolean lazyPropertiesAreUnfetched,
+			final PersistenceContext persistenceContext) {
+		this( status, loadedState, rowId, id, version, lockMode, existsInDatabase,
+				persister,disableVersionIncrement, lazyPropertiesAreUnfetched, persistenceContext );
+	}
+
+	public ImmutableEntityEntry(
+			final Status status,
+			final Object[] loadedState,
+			final Object rowId,
+			final Serializable id,
+			final Object version,
+			final LockMode lockMode,
+			final boolean existsInDatabase,
+			final EntityPersister persister,
+			final boolean disableVersionIncrement,
+			final boolean lazyPropertiesAreUnfetched,
+			final PersistenceContext persistenceContext) {
+
+		super( status, loadedState, rowId, id, version, lockMode, existsInDatabase, persister,
+				disableVersionIncrement, lazyPropertiesAreUnfetched, persistenceContext );
+	}
+
+	/**
+	 * This for is used during custom deserialization handling
+	 */
+	@SuppressWarnings( {"JavaDoc"})
+	private ImmutableEntityEntry(
+			final SessionFactoryImplementor factory,
+			final String entityName,
+			final Serializable id,
+			final Status status,
+			final Status previousStatus,
+			final Object[] loadedState,
+			final Object[] deletedState,
+			final Object version,
+			final LockMode lockMode,
+			final boolean existsInDatabase,
+			final boolean isBeingReplicated,
+			final boolean loadedWithLazyPropertiesUnfetched,
+			final PersistenceContext persistenceContext) {
+
+		super( factory, entityName, id, status, previousStatus, loadedState, deletedState,
+				version, lockMode, existsInDatabase, isBeingReplicated, loadedWithLazyPropertiesUnfetched,
+				persistenceContext );
+	}
+
+	@Override
+	public void setLockMode(LockMode lockMode) {
+
+		switch(lockMode) {
+			case NONE : case READ:
+				setCompressedValue( EnumState.LOCK_MODE, lockMode );
+				break;
+			default:
+				throw new UnsupportedLockAttemptException("Lock mode not supported");
+		}
+	}
+
+	/**
+	 * Custom deserialization routine used during deserialization of a
+	 * Session/PersistenceContext for increased performance.
+	 *
+	 * @param ois The stream from which to read the entry.
+	 * @param persistenceContext The context being deserialized.
+	 *
+	 * @return The deserialized EntityEntry
+	 *
+	 * @throws java.io.IOException If a stream error occurs
+	 * @throws ClassNotFoundException If any of the classes declared in the stream
+	 * cannot be found
+	 */
+	public static EntityEntry deserialize(
+			ObjectInputStream ois,
+			PersistenceContext persistenceContext) throws IOException, ClassNotFoundException {
+		String previousStatusString;
+		return new ImmutableEntityEntry(
+				persistenceContext.getSession().getFactory(),
+				(String) ois.readObject(),
+				(Serializable) ois.readObject(),
+				Status.valueOf( (String) ois.readObject() ),
+				( previousStatusString = (String) ois.readObject() ).length() == 0
+						? null
+						: Status.valueOf( previousStatusString ),
+				(Object[]) ois.readObject(),
+				(Object[]) ois.readObject(),
+				ois.readObject(),
+				LockMode.valueOf( (String) ois.readObject() ),
+				ois.readBoolean(),
+				ois.readBoolean(),
+				ois.readBoolean(),
+				persistenceContext
+		);
+	}
+
+	public PersistenceContext getPersistenceContext(){
+		return persistenceContext;
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/ImmutableEntityEntryFactory.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/ImmutableEntityEntryFactory.java
new file mode 100644
index 0000000000..f3d72c7b94
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/ImmutableEntityEntryFactory.java
@@ -0,0 +1,79 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+
+package org.hibernate.engine.internal;
+
+import org.hibernate.LockMode;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.EntityEntryFactory;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.Status;
+import org.hibernate.persister.entity.EntityPersister;
+
+import java.io.Serializable;
+
+/**
+ * Factory for the safe approach implementation of {@link org.hibernate.engine.spi.EntityEntry}.
+ * <p/>
+ * Smarter implementations could store less state.
+ *
+ * @author Emmanuel Bernard
+ */
+public class ImmutableEntityEntryFactory implements EntityEntryFactory {
+	/**
+	 * Singleton access
+	 */
+	public static final ImmutableEntityEntryFactory INSTANCE = new ImmutableEntityEntryFactory();
+
+	private ImmutableEntityEntryFactory() {
+	}
+
+	@Override
+	public EntityEntry createEntityEntry(
+			Status status,
+			Object[] loadedState,
+			Object rowId,
+			Serializable id,
+			Object version,
+			LockMode lockMode,
+			boolean existsInDatabase,
+			EntityPersister persister,
+			boolean disableVersionIncrement,
+			boolean lazyPropertiesAreUnfetched,
+			PersistenceContext persistenceContext) {
+		return new ImmutableEntityEntry(
+				status,
+				loadedState,
+				rowId,
+				id,
+				version,
+				lockMode,
+				existsInDatabase,
+				persister,
+				disableVersionIncrement,
+				lazyPropertiesAreUnfetched,
+				persistenceContext
+		);
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/MutableEntityEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/MutableEntityEntry.java
index 28a6626752..b67d201f76 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/MutableEntityEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/MutableEntityEntry.java
@@ -1,697 +1,151 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010-2015, Red Hat Inc. or third-party contributors as
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
 
-import org.hibernate.AssertionFailure;
-import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityMode;
-import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
-import org.hibernate.Session;
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
-import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.EntityEntry;
-import org.hibernate.engine.spi.EntityEntryExtraState;
-import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
-import org.hibernate.engine.spi.SelfDirtinessTracker;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.persister.entity.UniqueKeyLoadable;
-import org.hibernate.pretty.MessageHelper;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
-import java.io.ObjectOutputStream;
 import java.io.Serializable;
 
 /**
  * We need an entry to tell us all about the current state of an mutable object with respect to its persistent state
  *
  * Implementation Warning: Hibernate needs to instantiate a high amount of instances of this class,
  * therefore we need to take care of its impact on memory consumption.
  *
  * @author Gavin King
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  * @author Gunnar Morling
  * @author Sanne Grinovero  <sanne@hibernate.org>
  */
-public final class MutableEntityEntry implements Serializable, EntityEntry {
-	private final Serializable id;
-	private Object[] loadedState;
-	private Object version;
-	private final EntityPersister persister; // permanent but we only need the entityName state in a non transient way
-	private transient EntityKey cachedEntityKey; // cached EntityKey (lazy-initialized)
-	private final transient Object rowId;
-	private final transient PersistenceContext persistenceContext;
-	private EntityEntryExtraState next;
-
-	/**
-	 * Holds several boolean and enum typed attributes in a very compact manner. Enum values are stored in 4 bits
-	 * (where 0 represents {@code null}, and each enum value is represented by its ordinal value + 1), thus allowing
-	 * for up to 15 values per enum. Boolean values are stored in one bit.
-	 * <p>
-	 * The value is structured as follows:
-	 *
-	 * <pre>
-	 * 1 - Lock mode
-	 * 2 - Status
-	 * 3 - Previous Status
-	 * 4 - existsInDatabase
-	 * 5 - isBeingReplicated
-	 * 6 - loadedWithLazyPropertiesUnfetched; NOTE: this is not updated when properties are fetched lazily!
-	 *
-	 * 0000 0000 | 0000 0000 | 0654 3333 | 2222 1111
-	 * </pre>
-	 * Use {@link #setCompressedValue(MutableEntityEntry.EnumState, Enum)},
-	 * {@link #getCompressedValue(MutableEntityEntry.EnumState, Class)} etc
-	 * to access the enums and booleans stored in this value.
-	 * <p>
-	 * Representing enum values by their ordinal value is acceptable for our case as this value itself is never
-	 * serialized or deserialized and thus is not affected should ordinal values change.
-	 */
-	private transient int compressedState;
+public final class MutableEntityEntry extends AbstractEntityEntry {
 
 	/**
 	 * @deprecated the tenantId and entityMode parameters where removed: this constructor accepts but ignores them.
 	 * Use the other constructor!
 	 */
 	@Deprecated
 	public MutableEntityEntry(
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final EntityMode entityMode,
 			final String tenantId,
 			final boolean disableVersionIncrement,
 			final boolean lazyPropertiesAreUnfetched,
 			final PersistenceContext persistenceContext) {
 		this( status, loadedState, rowId, id, version, lockMode, existsInDatabase,
 				persister,disableVersionIncrement, lazyPropertiesAreUnfetched, persistenceContext );
 	}
 
 	public MutableEntityEntry(
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			final boolean lazyPropertiesAreUnfetched,
 			final PersistenceContext persistenceContext) {
-		setCompressedValue( EnumState.STATUS, status );
-		// not useful strictly speaking but more explicit
-		setCompressedValue( EnumState.PREVIOUS_STATUS, null );
-		// only retain loaded state if the status is not Status.READ_ONLY
-		if ( status != Status.READ_ONLY ) {
-			this.loadedState = loadedState;
-		}
-		this.id=id;
-		this.rowId=rowId;
-		setCompressedValue( BooleanState.EXISTS_IN_DATABASE, existsInDatabase );
-		this.version=version;
-		setCompressedValue( EnumState.LOCK_MODE, lockMode );
-		setCompressedValue( BooleanState.IS_BEING_REPLICATED, disableVersionIncrement );
-		setCompressedValue( BooleanState.LOADED_WITH_LAZY_PROPERTIES_UNFETCHED, lazyPropertiesAreUnfetched );
-		this.persister=persister;
-		this.persistenceContext = persistenceContext;
+
+		super( status, loadedState, rowId, id, version, lockMode, existsInDatabase, persister,
+				disableVersionIncrement, lazyPropertiesAreUnfetched, persistenceContext );
 	}
 
 	/**
 	 * This for is used during custom deserialization handling
 	 */
 	@SuppressWarnings( {"JavaDoc"})
 	private MutableEntityEntry(
 			final SessionFactoryImplementor factory,
 			final String entityName,
 			final Serializable id,
 			final Status status,
 			final Status previousStatus,
 			final Object[] loadedState,
 			final Object[] deletedState,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final boolean isBeingReplicated,
 			final boolean loadedWithLazyPropertiesUnfetched,
 			final PersistenceContext persistenceContext) {
-		this.persister = ( factory == null ? null : factory.getEntityPersister( entityName ) );
-		this.id = id;
-		setCompressedValue( EnumState.STATUS, status );
-		setCompressedValue( EnumState.PREVIOUS_STATUS, previousStatus );
-		this.loadedState = loadedState;
-		setDeletedState( deletedState );
-		this.version = version;
-		setCompressedValue( EnumState.LOCK_MODE, lockMode );
-		setCompressedValue( BooleanState.EXISTS_IN_DATABASE, existsInDatabase );
-		setCompressedValue( BooleanState.IS_BEING_REPLICATED, isBeingReplicated );
-		setCompressedValue( BooleanState.LOADED_WITH_LAZY_PROPERTIES_UNFETCHED, loadedWithLazyPropertiesUnfetched );
-		this.rowId = null; // this is equivalent to the old behavior...
-		this.persistenceContext = persistenceContext;
-	}
-
-	@Override
-	public LockMode getLockMode() {
-		return getCompressedValue( EnumState.LOCK_MODE );
-	}
-
-	@Override
-	public void setLockMode(LockMode lockMode) {
-		setCompressedValue( EnumState.LOCK_MODE, lockMode );
-	}
-
-	@Override
-	public Status getStatus() {
-		return getCompressedValue( EnumState.STATUS );
-	}
-
-	private Status getPreviousStatus() {
-		return getCompressedValue( EnumState.PREVIOUS_STATUS );
-	}
-
-	@Override
-	public void setStatus(Status status) {
-		if ( status == Status.READ_ONLY ) {
-			//memory optimization
-			loadedState = null;
-		}
-
-		Status currentStatus = this.getStatus();
 
-		if ( currentStatus != status ) {
-			setCompressedValue( EnumState.PREVIOUS_STATUS, currentStatus );
-			setCompressedValue( EnumState.STATUS, status );
-		}
+		super( factory, entityName, id, status, previousStatus, loadedState, deletedState,
+				version, lockMode, existsInDatabase, isBeingReplicated, loadedWithLazyPropertiesUnfetched,
+				persistenceContext );
 	}
 
-	@Override
-	public Serializable getId() {
-		return id;
-	}
-
-	@Override
-	public Object[] getLoadedState() {
-		return loadedState;
-	}
-
-	private static final Object[] DEFAULT_DELETED_STATE = null;
-
-	@Override
-	public Object[] getDeletedState() {
-		EntityEntryExtraStateHolder extra = getExtraState( EntityEntryExtraStateHolder.class );
-		return extra != null ? extra.getDeletedState() : DEFAULT_DELETED_STATE;
-	}
-
-	@Override
-	public void setDeletedState(Object[] deletedState) {
-		EntityEntryExtraStateHolder extra = getExtraState( EntityEntryExtraStateHolder.class );
-		if ( extra == null && deletedState == DEFAULT_DELETED_STATE ) {
-			//this is the default value and we do not store the extra state
-			return;
-		}
-		if ( extra == null ) {
-			extra = new EntityEntryExtraStateHolder();
-			addExtraState( extra );
-		}
-		extra.setDeletedState( deletedState );
-	}
-
-	@Override
-	public boolean isExistsInDatabase() {
-		return getCompressedValue( BooleanState.EXISTS_IN_DATABASE );
-	}
-
-	@Override
-	public Object getVersion() {
-		return version;
-	}
-
-	@Override
-	public EntityPersister getPersister() {
-		return persister;
-	}
-
-	@Override
-	public EntityKey getEntityKey() {
-		if ( cachedEntityKey == null ) {
-			if ( getId() == null ) {
-				throw new IllegalStateException( "cannot generate an EntityKey when id is null.");
-			}
-			cachedEntityKey = new EntityKey( getId(), getPersister() );
-		}
-		return cachedEntityKey;
-	}
-
-	@Override
-	public String getEntityName() {
-		return persister == null ? null : persister.getEntityName();
-
-	}
-
-	@Override
-	public boolean isBeingReplicated() {
-		return getCompressedValue( BooleanState.IS_BEING_REPLICATED );
-	}
-
-	@Override
-	public Object getRowId() {
-		return rowId;
-	}
-
-	@Override
-	public void postUpdate(Object entity, Object[] updatedState, Object nextVersion) {
-		this.loadedState = updatedState;
-		setLockMode( LockMode.WRITE );
-
-		if ( getPersister().isVersioned() ) {
-			this.version = nextVersion;
-			getPersister().setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
-		}
-
-		if ( getPersister().getInstrumentationMetadata().isInstrumented() ) {
-			final FieldInterceptor interceptor = getPersister().getInstrumentationMetadata().extractInterceptor( entity );
-			if ( interceptor != null ) {
-				interceptor.clearDirty();
-			}
-		}
-
-		if( entity instanceof SelfDirtinessTracker ) {
-			((SelfDirtinessTracker) entity).$$_hibernate_clearDirtyAttributes();
-		}
-
-		persistenceContext.getSession()
-				.getFactory()
-				.getCustomEntityDirtinessStrategy()
-				.resetDirty( entity, getPersister(), (Session) persistenceContext.getSession() );
-	}
-
-	@Override
-	public void postDelete() {
-		setCompressedValue( EnumState.PREVIOUS_STATUS, getStatus() );
-		setCompressedValue( EnumState.STATUS, Status.GONE );
-		setCompressedValue( BooleanState.EXISTS_IN_DATABASE, false );
-	}
-
-	@Override
-	public void postInsert(Object[] insertedState) {
-		setCompressedValue( BooleanState.EXISTS_IN_DATABASE, true );
-	}
-
-	@Override
-	public boolean isNullifiable(boolean earlyInsert, SessionImplementor session) {
-		if ( getStatus() == Status.SAVING ) {
-			return true;
-		}
-		else if ( earlyInsert ) {
-			return !isExistsInDatabase();
-		}
-		else {
-			return session.getPersistenceContext().getNullifiableEntityKeys().contains( getEntityKey() );
-		}
-	}
-
-	@Override
-	public Object getLoadedValue(String propertyName) {
-		if ( loadedState == null || propertyName == null ) {
-			return null;
-		}
-		else {
-			int propertyIndex = ( (UniqueKeyLoadable) persister ).getPropertyIndex( propertyName );
-			return loadedState[propertyIndex];
-		}
-	}
-
-	@Override
-	public boolean requiresDirtyCheck(Object entity) {
-		return isModifiableEntity()
-				&& ( !isUnequivocallyNonDirty( entity ) );
-	}
-
-	@SuppressWarnings( {"SimplifiableIfStatement"})
-	private boolean isUnequivocallyNonDirty(Object entity) {
-
-		if(entity instanceof SelfDirtinessTracker)
-			return ((SelfDirtinessTracker) entity).$$_hibernate_hasDirtyAttributes();
-
-		final CustomEntityDirtinessStrategy customEntityDirtinessStrategy =
-				persistenceContext.getSession().getFactory().getCustomEntityDirtinessStrategy();
-		if ( customEntityDirtinessStrategy.canDirtyCheck( entity, getPersister(), (Session) persistenceContext.getSession() ) ) {
-			return ! customEntityDirtinessStrategy.isDirty( entity, getPersister(), (Session) persistenceContext.getSession() );
-		}
-
-		if ( getPersister().hasMutableProperties() ) {
-			return false;
-		}
-
-		if ( getPersister().getInstrumentationMetadata().isInstrumented() ) {
-			// the entity must be instrumented (otherwise we cant check dirty flag) and the dirty flag is false
-			return ! getPersister().getInstrumentationMetadata().extractInterceptor( entity ).isDirty();
-		}
-
-		return false;
-	}
-
-	@Override
-	public boolean isModifiableEntity() {
-		Status status = getStatus();
-		Status previousStatus = getPreviousStatus();
-		return getPersister().isMutable()
-				&& status != Status.READ_ONLY
-				&& ! ( status == Status.DELETED && previousStatus == Status.READ_ONLY );
-	}
-
-	@Override
-	public void forceLocked(Object entity, Object nextVersion) {
-		version = nextVersion;
-		loadedState[ persister.getVersionProperty() ] = version;
-		// TODO:  use LockMode.PESSIMISTIC_FORCE_INCREMENT
-		//noinspection deprecation
-		setLockMode( LockMode.FORCE );
-		persister.setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
-	}
-
-	@Override
-	public boolean isReadOnly() {
-		Status status = getStatus();
-		if (status != Status.MANAGED && status != Status.READ_ONLY) {
-			throw new HibernateException("instance was not in a valid state");
-		}
-		return status == Status.READ_ONLY;
-	}
-
-	@Override
-	public void setReadOnly(boolean readOnly, Object entity) {
-		if ( readOnly == isReadOnly() ) {
-			// simply return since the status is not being changed
-			return;
-		}
-		if ( readOnly ) {
-			setStatus( Status.READ_ONLY );
-			loadedState = null;
-		}
-		else {
-			if ( ! persister.isMutable() ) {
-				throw new IllegalStateException( "Cannot make an immutable entity modifiable." );
-			}
-			setStatus( Status.MANAGED );
-			loadedState = getPersister().getPropertyValues( entity );
-			persistenceContext.getNaturalIdHelper().manageLocalNaturalIdCrossReference(
-					persister,
-					id,
-					loadedState,
-					null,
-					CachedNaturalIdValueSource.LOAD
-			);
-		}
-	}
-
-	@Override
-	public String toString() {
-		return "EntityEntry" +
-				MessageHelper.infoString( getPersister().getEntityName(), id ) +
-				'(' + getStatus() + ')';
-	}
-
-	@Override
-	public boolean isLoadedWithLazyPropertiesUnfetched() {
-		return getCompressedValue( BooleanState.LOADED_WITH_LAZY_PROPERTIES_UNFETCHED );
-	}
-
-	@Override
-	public void serialize(ObjectOutputStream oos) throws IOException {
-		Status previousStatus = getPreviousStatus();
-		oos.writeObject( getEntityName() );
-		oos.writeObject( id );
-		oos.writeObject( getStatus().name() );
-		oos.writeObject( (previousStatus == null ? "" : previousStatus.name()) );
-		// todo : potentially look at optimizing these two arrays
-		oos.writeObject( loadedState );
-		oos.writeObject( getDeletedState() );
-		oos.writeObject( version );
-		oos.writeObject( getLockMode().toString() );
-		oos.writeBoolean( isExistsInDatabase() );
-		oos.writeBoolean( isBeingReplicated() );
-		oos.writeBoolean( isLoadedWithLazyPropertiesUnfetched() );
-	}
 
 	/**
 	 * Custom deserialization routine used during deserialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param ois The stream from which to read the entry.
 	 * @param persistenceContext The context being deserialized.
 	 *
 	 * @return The deserialized EntityEntry
 	 *
 	 * @throws java.io.IOException If a stream error occurs
 	 * @throws ClassNotFoundException If any of the classes declared in the stream
 	 * cannot be found
 	 */
 	public static EntityEntry deserialize(
 			ObjectInputStream ois,
 			PersistenceContext persistenceContext) throws IOException, ClassNotFoundException {
 		String previousStatusString;
 		return new MutableEntityEntry(
 				persistenceContext.getSession().getFactory(),
 				(String) ois.readObject(),
 				(Serializable) ois.readObject(),
 				Status.valueOf( (String) ois.readObject() ),
 				( previousStatusString = (String) ois.readObject() ).length() == 0
 						? null
 						: Status.valueOf( previousStatusString ),
 				(Object[]) ois.readObject(),
 				(Object[]) ois.readObject(),
 				ois.readObject(),
 				LockMode.valueOf( (String) ois.readObject() ),
 				ois.readBoolean(),
 				ois.readBoolean(),
 				ois.readBoolean(),
 				persistenceContext
 		);
 	}
-
-	@Override
-	public void addExtraState(EntityEntryExtraState extraState) {
-		if ( next == null ) {
-			next = extraState;
-		}
-		else {
-			next.addExtraState( extraState );
-		}
-	}
-
-	@Override
-	public <T extends EntityEntryExtraState> T getExtraState(Class<T> extraStateType) {
-		if ( next == null ) {
-			return null;
-		}
-		if ( extraStateType.isAssignableFrom( next.getClass() ) ) {
-			return (T) next;
-		}
-		else {
-			return next.getExtraState( extraStateType );
-		}
-	}
-	/**
-	 * Saves the value for the given enum property.
-	 *
-	 * @param state
-	 *            identifies the value to store
-	 * @param value
-	 *            the value to store; The caller must make sure that it matches
-	 *            the given identifier
-	 */
-	private <E extends Enum<E>> void setCompressedValue(EnumState<E> state, E value) {
-		// reset the bits for the given property to 0
-		compressedState &= state.getUnsetMask();
-		// store the numeric representation of the enum value at the right offset
-		compressedState |= ( state.getValue( value ) << state.getOffset() );
-	}
-
-	/**
-	 * Gets the current value of the given enum property.
-	 *
-	 * @param state
-	 *            identifies the value to store
-	 * @return the current value of the specified property
-	 */
-	private <E extends Enum<E>> E getCompressedValue(EnumState<E> state) {
-		// restore the numeric value from the bits at the right offset and return the corresponding enum constant
-		int index = ( ( compressedState & state.getMask() ) >> state.getOffset() ) - 1;
-		return index == - 1 ? null : state.getEnumConstants()[index];
-	}
-
-	/**
-	 * Saves the value for the given boolean flag.
-	 *
-	 * @param state
-	 *            identifies the value to store
-	 * @param value
-	 *            the value to store
-	 */
-	private void setCompressedValue(BooleanState state, boolean value) {
-		compressedState &= state.getUnsetMask();
-		compressedState |= ( state.getValue( value ) << state.getOffset() );
-	}
-
-	/**
-	 * Gets the current value of the given boolean flag.
-	 *
-	 * @param state
-	 *            identifies the value to store
-	 * @return the current value of the specified flag
-	 */
-	private boolean getCompressedValue(BooleanState state) {
-		return ( ( compressedState & state.getMask() ) >> state.getOffset() ) == 1;
-	}
-
-	/**
-	 * Represents an enum value stored within a number value, using four bits starting at a specified offset.
-	 *
-	 * @author Gunnar Morling
-	 */
-	private static class EnumState<E extends Enum<E>> {
-
-		private static final EnumState<LockMode> LOCK_MODE = new EnumState<LockMode>( 0, LockMode.class );
-		private static final EnumState<Status> STATUS = new EnumState<Status>( 4, Status.class );
-		private static final EnumState<Status> PREVIOUS_STATUS = new EnumState<Status>( 8, Status.class );
-
-		private final int offset;
-		private final E[] enumConstants;
-		private final int mask;
-		private final int unsetMask;
-
-		private EnumState(int offset, Class<E> enumType) {
-			E[] enumConstants = enumType.getEnumConstants();
-
-			// In case any of the enums cannot be stored in 4 bits anymore, we'd have to re-structure the compressed
-			// state int
-			if ( enumConstants.length > 15 ) {
-				throw new AssertionFailure( "Cannot store enum type " + enumType.getName() + " in compressed state as"
-						+ " it has too many values." );
-			}
-
-			this.offset = offset;
-			this.enumConstants = enumConstants;
-
-			// a mask for reading the four bits, starting at the right offset
-			this.mask = 0xF << offset;
-
-			// a mask for setting the four bits at the right offset to 0
-			this.unsetMask = 0xFFFF & ~mask;
-		}
-
-		/**
-		 * Returns the numeric value to be stored for the given enum value.
-		 */
-		private int getValue(E value) {
-			return value != null ? value.ordinal() + 1 : 0;
-		}
-
-		/**
-		 * Returns the offset within the number value at which this enum value is stored.
-		 */
-		private int getOffset() {
-			return offset;
-		}
-
-		/**
-		 * Returns the bit mask for reading this enum value from the number value storing it.
-		 */
-		private int getMask() {
-			return mask;
-		}
-
-		/**
-		 * Returns the bit mask for resetting this enum value from the number value storing it.
-		 */
-		private int getUnsetMask() {
-			return unsetMask;
-		}
-
-		/**
-		 * Returns the constants of the represented enum which is cached for performance reasons.
-		 */
-		private E[] getEnumConstants() {
-			return enumConstants;
-		}
-	}
-
-	/**
-	 * Represents a boolean flag stored within a number value, using one bit at a specified offset.
-	 *
-	 * @author Gunnar Morling
-	 */
-	private enum BooleanState {
-
-		EXISTS_IN_DATABASE(13),
-		IS_BEING_REPLICATED(14),
-		LOADED_WITH_LAZY_PROPERTIES_UNFETCHED(15);
-
-		private final int offset;
-		private final int mask;
-		private final int unsetMask;
-
-		private BooleanState(int offset) {
-			this.offset = offset;
-			this.mask = 0x1 << offset;
-			this.unsetMask = 0xFFFF & ~mask;
-		}
-
-		private int getValue(boolean value) {
-			return value ? 1 : 0;
-		}
-
-		/**
-		 * Returns the offset within the number value at which this boolean flag is stored.
-		 */
-		private int getOffset() {
-			return offset;
-		}
-
-		/**
-		 * Returns the bit mask for reading this flag from the number value storing it.
-		 */
-		private int getMask() {
-			return mask;
-		}
-
-		/**
-		 * Returns the bit mask for resetting this flag from the number value storing it.
-		 */
-		private int getUnsetMask() {
-			return unsetMask;
-		}
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/DefaultEntityEntryFactory.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/MutableEntityEntryFactory.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/engine/internal/DefaultEntityEntryFactory.java
rename to hibernate-core/src/main/java/org/hibernate/engine/internal/MutableEntityEntryFactory.java
index c7481dcd0d..c2c7a7d201 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/DefaultEntityEntryFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/MutableEntityEntryFactory.java
@@ -1,79 +1,79 @@
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
 
 package org.hibernate.engine.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Factory for the safe approach implementation of {@link org.hibernate.engine.spi.EntityEntry}.
  * <p/>
  * Smarter implementations could store less state.
  *
  * @author Emmanuel Bernard
  */
-public class DefaultEntityEntryFactory implements EntityEntryFactory {
+public class MutableEntityEntryFactory implements EntityEntryFactory {
 	/**
 	 * Singleton access
 	 */
-	public static final DefaultEntityEntryFactory INSTANCE = new DefaultEntityEntryFactory();
+	public static final MutableEntityEntryFactory INSTANCE = new MutableEntityEntryFactory();
 
-	private DefaultEntityEntryFactory() {
+	private MutableEntityEntryFactory() {
 	}
 
 	@Override
 	public EntityEntry createEntityEntry(
 			Status status,
 			Object[] loadedState,
 			Object rowId,
 			Serializable id,
 			Object version,
 			LockMode lockMode,
 			boolean existsInDatabase,
 			EntityPersister persister,
 			boolean disableVersionIncrement,
 			boolean lazyPropertiesAreUnfetched,
 			PersistenceContext persistenceContext) {
 		return new MutableEntityEntry(
 				status,
 				loadedState,
 				rowId,
 				id,
 				version,
 				lockMode,
 				existsInDatabase,
 				persister,
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched,
 				persistenceContext
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index ae4604aab3..58ab70d7ea 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1827 +1,1842 @@
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
 package org.hibernate.persister.entity;
 
-import java.io.Serializable;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.util.ArrayList;
-import java.util.Arrays;
-import java.util.Collections;
-import java.util.Comparator;
-import java.util.HashMap;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.LinkedHashMap;
-import java.util.List;
-import java.util.Map;
-import java.util.Set;
-
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StructuredCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.CacheHelper;
-import org.hibernate.engine.internal.DefaultEntityEntryFactory;
+import org.hibernate.engine.internal.MutableEntityEntryFactory;
+import org.hibernate.engine.internal.ImmutableEntityEntryFactory;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext.NaturalIdHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.jdbc.TooManyRowsAffectedException;
 import org.hibernate.loader.entity.BatchingEntityLoaderBuilder;
 import org.hibernate.loader.entity.CascadeEntityLoader;
 import org.hibernate.loader.entity.EntityLoader;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.internal.EntityIdentifierDefinitionHelper;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.Update;
 import org.hibernate.tuple.GenerationTiming;
 import org.hibernate.tuple.InDatabaseValueGenerationStrategy;
 import org.hibernate.tuple.InMemoryValueGenerationStrategy;
 import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.tuple.ValueGeneration;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.hibernate.type.VersionType;
-
 import org.jboss.logging.Logger;
 
+import java.io.Serializable;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.util.ArrayList;
+import java.util.Arrays;
+import java.util.Collections;
+import java.util.Comparator;
+import java.util.HashMap;
+import java.util.HashSet;
+import java.util.Iterator;
+import java.util.LinkedHashMap;
+import java.util.List;
+import java.util.Map;
+import java.util.Set;
+
 /**
  * Basic functionality for persisting an entity via JDBC
  * through either generated or custom SQL
  *
  * @author Gavin King
  */
 public abstract class AbstractEntityPersister
 		implements OuterJoinLoadable, Queryable, ClassMetadata, UniqueKeyLoadable,
 		SQLLoadable, LazyPropertyInitializer, PostInsertIdentityPersister, Lockable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, AbstractEntityPersister.class.getName() );
 
 	public static final String ENTITY_CLASS = "class";
 
 	// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final SessionFactoryImplementor factory;
 	private final EntityRegionAccessStrategy cacheAccessStrategy;
 	private final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy;
 	private final boolean isLazyPropertiesCacheable;
 	private final CacheEntryHelper cacheEntryHelper;
 	private final EntityMetamodel entityMetamodel;
 	private final EntityTuplizer entityTuplizer;
+	private final EntityEntryFactory entityEntryFactory;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private final String[] rootTableKeyColumnNames;
 	private final String[] rootTableKeyColumnReaders;
 	private final String[] rootTableKeyColumnReaderTemplates;
 	private final String[] identifierAliases;
 	private final int identifierColumnSpan;
 	private final String versionColumnName;
 	private final boolean hasFormulaProperties;
 	private final int batchSize;
 	private final boolean hasSubselectLoadableCollections;
 	protected final String rowIdName;
 
 	private final Set lazyProperties;
 
 	// The optional SQL string defined in the where attribute
 	private final String sqlWhereString;
 	private final String sqlWhereStringTemplate;
 
 	//information about properties of this class,
 	//including inherited properties
 	//(only really needed for updatable/insertable properties)
 	private final int[] propertyColumnSpans;
 	private final String[] propertySubclassNames;
 	private final String[][] propertyColumnAliases;
 	private final String[][] propertyColumnNames;
 	private final String[][] propertyColumnFormulaTemplates;
 	private final String[][] propertyColumnReaderTemplates;
 	private final String[][] propertyColumnWriters;
 	private final boolean[][] propertyColumnUpdateable;
 	private final boolean[][] propertyColumnInsertable;
 	private final boolean[] propertyUniqueness;
 	private final boolean[] propertySelectable;
 	
 	private final List<Integer> lobProperties = new ArrayList<Integer>();
 
 	//information about lazy properties of this class
 	private final String[] lazyPropertyNames;
 	private final int[] lazyPropertyNumbers;
 	private final Type[] lazyPropertyTypes;
 	private final String[][] lazyPropertyColumnAliases;
 
 	//information about all properties in class hierarchy
 	private final String[] subclassPropertyNameClosure;
 	private final String[] subclassPropertySubclassNameClosure;
 	private final Type[] subclassPropertyTypeClosure;
 	private final String[][] subclassPropertyFormulaTemplateClosure;
 	private final String[][] subclassPropertyColumnNameClosure;
 	private final String[][] subclassPropertyColumnReaderClosure;
 	private final String[][] subclassPropertyColumnReaderTemplateClosure;
 	private final FetchMode[] subclassPropertyFetchModeClosure;
 	private final boolean[] subclassPropertyNullabilityClosure;
 	private final boolean[] propertyDefinedOnSubclass;
 	private final int[][] subclassPropertyColumnNumberClosure;
 	private final int[][] subclassPropertyFormulaNumberClosure;
 	private final CascadeStyle[] subclassPropertyCascadeStyleClosure;
 
 	//information about all columns/formulas in class hierarchy
 	private final String[] subclassColumnClosure;
 	private final boolean[] subclassColumnLazyClosure;
 	private final String[] subclassColumnAliasClosure;
 	private final boolean[] subclassColumnSelectableClosure;
 	private final String[] subclassColumnReaderTemplateClosure;
 	private final String[] subclassFormulaClosure;
 	private final String[] subclassFormulaTemplateClosure;
 	private final String[] subclassFormulaAliasClosure;
 	private final boolean[] subclassFormulaLazyClosure;
 
 	// dynamic filters attached to the class-level
 	private final FilterHelper filterHelper;
 
 	private final Set<String> affectingFetchProfileNames = new HashSet<String>();
 
 	private final Map uniqueKeyLoaders = new HashMap();
 	private final Map lockers = new HashMap();
 	private final Map loaders = new HashMap();
 
 	// SQL strings
 	private String sqlVersionSelectString;
 	private String sqlSnapshotSelectString;
 	private String sqlLazySelectString;
 
 	private String sqlIdentityInsertString;
 	private String sqlUpdateByRowIdString;
 	private String sqlLazyUpdateByRowIdString;
 
 	private String[] sqlDeleteStrings;
 	private String[] sqlInsertStrings;
 	private String[] sqlUpdateStrings;
 	private String[] sqlLazyUpdateStrings;
 
 	private String sqlInsertGeneratedValuesSelectString;
 	private String sqlUpdateGeneratedValuesSelectString;
 
 	//Custom SQL (would be better if these were private)
 	protected boolean[] insertCallable;
 	protected boolean[] updateCallable;
 	protected boolean[] deleteCallable;
 	protected String[] customSQLInsert;
 	protected String[] customSQLUpdate;
 	protected String[] customSQLDelete;
 	protected ExecuteUpdateResultCheckStyle[] insertResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] updateResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] deleteResultCheckStyles;
 
 	private InsertGeneratedIdentifierDelegate identityDelegate;
 
 	private boolean[] tableHasColumns;
 
 	private final String loaderName;
 
 	private UniqueEntityLoader queryLoader;
 
 	private final String temporaryIdTableName;
 	private final String temporaryIdTableDDL;
 
 	private final Map subclassPropertyAliases = new HashMap();
 	private final Map subclassPropertyColumnNames = new HashMap();
 
 	protected final BasicEntityPropertyMapping propertyMapping;
 
+	private final boolean useReferenceCacheEntries;
+
 	protected void addDiscriminatorToInsert(Insert insert) {}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {}
 
 	protected abstract int[] getSubclassColumnTableNumberClosure();
 
 	protected abstract int[] getSubclassFormulaTableNumberClosure();
 
 	public abstract String getSubclassTableName(int j);
 
 	protected abstract String[] getSubclassTableKeyColumns(int j);
 
 	protected abstract boolean isClassOrSuperclassTable(int j);
 
 	protected abstract int getSubclassTableSpan();
 
 	protected abstract int getTableSpan();
 
 	protected abstract boolean isTableCascadeDeleteEnabled(int j);
 
 	protected abstract String getTableName(int j);
 
 	protected abstract String[] getKeyColumns(int j);
 
 	protected abstract boolean isPropertyOfTable(int property, int j);
 
 	protected abstract int[] getPropertyTableNumbersInSelect();
 
 	protected abstract int[] getPropertyTableNumbers();
 
 	protected abstract int getSubclassPropertyTableNumber(int i);
 
 	protected abstract String filterFragment(String alias) throws MappingException;
 
 	protected abstract String filterFragment(String alias, Set<String> treatAsDeclarations);
 
 	private static final String DISCRIMINATOR_ALIAS = "clazz_";
 
 	public String getDiscriminatorColumnName() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	public String getDiscriminatorColumnReaders() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	public String getDiscriminatorColumnReaderTemplate() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	protected String getDiscriminatorAlias() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	protected String getDiscriminatorFormulaTemplate() {
 		return null;
 	}
 
 	protected boolean isInverseTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableSubclassTable(int j) {
 		return false;
 	}
 
 	protected boolean isInverseSubclassTable(int j) {
 		return false;
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return entityMetamodel.getSubclassEntityNames().contains(entityName);
 	}
 
 	private boolean[] getTableHasColumns() {
 		return tableHasColumns;
 	}
 
 	public String[] getRootTableKeyColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	protected String[] getSQLUpdateByRowIdStrings() {
 		if ( sqlUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan() + 1];
 		result[0] = sqlUpdateByRowIdString;
 		System.arraycopy( sqlUpdateStrings, 0, result, 1, getTableSpan() );
 		return result;
 	}
 
 	protected String[] getSQLLazyUpdateByRowIdStrings() {
 		if ( sqlLazyUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan()];
 		result[0] = sqlLazyUpdateByRowIdString;
 		for ( int i = 1; i < getTableSpan(); i++ ) {
 			result[i] = sqlLazyUpdateStrings[i];
 		}
 		return result;
 	}
 
 	protected String getSQLSnapshotSelectString() {
 		return sqlSnapshotSelectString;
 	}
 
 	protected String getSQLLazySelectString() {
 		return sqlLazySelectString;
 	}
 
 	protected String[] getSQLDeleteStrings() {
 		return sqlDeleteStrings;
 	}
 
 	protected String[] getSQLInsertStrings() {
 		return sqlInsertStrings;
 	}
 
 	protected String[] getSQLUpdateStrings() {
 		return sqlUpdateStrings;
 	}
 
 	protected String[] getSQLLazyUpdateStrings() {
 		return sqlLazyUpdateStrings;
 	}
 
 	/**
 	 * The query that inserts a row, letting the database generate an id
 	 *
 	 * @return The IDENTITY-based insertion query.
 	 */
 	protected String getSQLIdentityInsertString() {
 		return sqlIdentityInsertString;
 	}
 
 	protected String getVersionSelectString() {
 		return sqlVersionSelectString;
 	}
 
 	protected boolean isInsertCallable(int j) {
 		return insertCallable[j];
 	}
 
 	protected boolean isUpdateCallable(int j) {
 		return updateCallable[j];
 	}
 
 	protected boolean isDeleteCallable(int j) {
 		return deleteCallable[j];
 	}
 
 	protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
 		return false;
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return false;
 	}
 
 	public boolean hasSequentialSelect() {
 		return false;
 	}
 
 	/**
 	 * Decide which tables need to be updated.
 	 * <p/>
 	 * The return here is an array of boolean values with each index corresponding
 	 * to a given table in the scope of this persister.
 	 *
 	 * @param dirtyProperties The indices of all the entity properties considered dirty.
 	 * @param hasDirtyCollection Whether any collections owned by the entity which were considered dirty.
 	 *
 	 * @return Array of booleans indicating which table require updating.
 	 */
 	protected boolean[] getTableUpdateNeeded(final int[] dirtyProperties, boolean hasDirtyCollection) {
 
 		if ( dirtyProperties == null ) {
 			return getTableHasColumns(); // for objects that came in via update()
 		}
 		else {
 			boolean[] updateability = getPropertyUpdateability();
 			int[] propertyTableNumbers = getPropertyTableNumbers();
 			boolean[] tableUpdateNeeded = new boolean[ getTableSpan() ];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				int property = dirtyProperties[i];
 				int table = propertyTableNumbers[property];
 				tableUpdateNeeded[table] = tableUpdateNeeded[table] ||
 						( getPropertyColumnSpan(property) > 0 && updateability[property] );
 			}
 			if ( isVersioned() ) {
 				tableUpdateNeeded[0] = tableUpdateNeeded[0] ||
 					Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
 			}
 			return tableUpdateNeeded;
 		}
 	}
 
 	public boolean hasRowId() {
 		return rowIdName != null;
 	}
 
 	protected boolean[][] getPropertyColumnUpdateable() {
 		return propertyColumnUpdateable;
 	}
 
 	protected boolean[][] getPropertyColumnInsertable() {
 		return propertyColumnInsertable;
 	}
 
 	protected boolean[] getPropertySelectable() {
 		return propertySelectable;
 	}
 
 	public AbstractEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final PersisterCreationContext creationContext) throws HibernateException {
 
 		// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		this.factory = creationContext.getSessionFactory();
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		this.naturalIdRegionAccessStrategy = naturalIdRegionAccessStrategy;
 		isLazyPropertiesCacheable = persistentClass.isLazyPropertiesCacheable();
 
 		this.entityMetamodel = new EntityMetamodel( persistentClass, this, factory );
 		this.entityTuplizer = this.entityMetamodel.getTuplizer();
+
+		if( entityMetamodel.isMutable() ) {
+			this.entityEntryFactory = MutableEntityEntryFactory.INSTANCE;
+		}
+		else {
+			this.entityEntryFactory = ImmutableEntityEntryFactory.INSTANCE;
+		}
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		int batch = persistentClass.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 		hasSubselectLoadableCollections = persistentClass.hasSubselectLoadableCollections();
 
 		propertyMapping = new BasicEntityPropertyMapping( this );
 
 		// IDENTIFIER
 
 		identifierColumnSpan = persistentClass.getIdentifier().getColumnSpan();
 		rootTableKeyColumnNames = new String[identifierColumnSpan];
 		rootTableKeyColumnReaders = new String[identifierColumnSpan];
 		rootTableKeyColumnReaderTemplates = new String[identifierColumnSpan];
 		identifierAliases = new String[identifierColumnSpan];
 
 		rowIdName = persistentClass.getRootTable().getRowId();
 
 		loaderName = persistentClass.getLoaderName();
 
 		Iterator iter = persistentClass.getIdentifier().getColumnIterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			Column col = ( Column ) iter.next();
 			rootTableKeyColumnNames[i] = col.getQuotedName( factory.getDialect() );
 			rootTableKeyColumnReaders[i] = col.getReadExpr( factory.getDialect() );
 			rootTableKeyColumnReaderTemplates[i] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			identifierAliases[i] = col.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( persistentClass.isVersioned() ) {
 			versionColumnName = ( ( Column ) persistentClass.getVersion().getColumnIterator().next() ).getQuotedName( factory.getDialect() );
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( persistentClass.getWhere() ) ? "( " + persistentClass.getWhere() + ") " : null;
 		sqlWhereStringTemplate = sqlWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( sqlWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		// PROPERTIES
 
 		final boolean lazyAvailable = isInstrumented();
 
 		int hydrateSpan = entityMetamodel.getPropertySpan();
 		propertyColumnSpans = new int[hydrateSpan];
 		propertySubclassNames = new String[hydrateSpan];
 		propertyColumnAliases = new String[hydrateSpan][];
 		propertyColumnNames = new String[hydrateSpan][];
 		propertyColumnFormulaTemplates = new String[hydrateSpan][];
 		propertyColumnReaderTemplates = new String[hydrateSpan][];
 		propertyColumnWriters = new String[hydrateSpan][];
 		propertyUniqueness = new boolean[hydrateSpan];
 		propertySelectable = new boolean[hydrateSpan];
 		propertyColumnUpdateable = new boolean[hydrateSpan][];
 		propertyColumnInsertable = new boolean[hydrateSpan][];
 		HashSet thisClassProperties = new HashSet();
 
 		lazyProperties = new HashSet();
 		ArrayList lazyNames = new ArrayList();
 		ArrayList lazyNumbers = new ArrayList();
 		ArrayList lazyTypes = new ArrayList();
 		ArrayList lazyColAliases = new ArrayList();
 
 		iter = persistentClass.getPropertyClosureIterator();
 		i = 0;
 		boolean foundFormula = false;
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 			thisClassProperties.add( prop );
 
 			int span = prop.getColumnSpan();
 			propertyColumnSpans[i] = span;
 			propertySubclassNames[i] = prop.getPersistentClass().getEntityName();
 			String[] colNames = new String[span];
 			String[] colAliases = new String[span];
 			String[] colReaderTemplates = new String[span];
 			String[] colWriters = new String[span];
 			String[] formulaTemplates = new String[span];
 			Iterator colIter = prop.getColumnIterator();
 			int k = 0;
 			while ( colIter.hasNext() ) {
 				Selectable thing = ( Selectable ) colIter.next();
 				colAliases[k] = thing.getAlias( factory.getDialect() , prop.getValue().getTable() );
 				if ( thing.isFormula() ) {
 					foundFormula = true;
 					formulaTemplates[k] = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				}
 				else {
 					Column col = (Column)thing;
 					colNames[k] = col.getQuotedName( factory.getDialect() );
 					colReaderTemplates[k] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					colWriters[k] = col.getWriteExpr();
 				}
 				k++;
 			}
 			propertyColumnNames[i] = colNames;
 			propertyColumnFormulaTemplates[i] = formulaTemplates;
 			propertyColumnReaderTemplates[i] = colReaderTemplates;
 			propertyColumnWriters[i] = colWriters;
 			propertyColumnAliases[i] = colAliases;
 
 			if ( lazyAvailable && prop.isLazy() ) {
 				lazyProperties.add( prop.getName() );
 				lazyNames.add( prop.getName() );
 				lazyNumbers.add( i );
 				lazyTypes.add( prop.getValue().getType() );
 				lazyColAliases.add( colAliases );
 			}
 
 			propertyColumnUpdateable[i] = prop.getValue().getColumnUpdateability();
 			propertyColumnInsertable[i] = prop.getValue().getColumnInsertability();
 
 			propertySelectable[i] = prop.isSelectable();
 
 			propertyUniqueness[i] = prop.getValue().isAlternateUniqueKey();
 			
 			if (prop.isLob() && getFactory().getDialect().forceLobAsLastValue() ) {
 				lobProperties.add( i );
 			}
 
 			i++;
 
 		}
 		hasFormulaProperties = foundFormula;
 		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
 		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
 		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
 		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );
 
 		// SUBCLASS PROPERTY CLOSURE
 
 		ArrayList columns = new ArrayList();
 		ArrayList columnsLazy = new ArrayList();
 		ArrayList columnReaderTemplates = new ArrayList();
 		ArrayList aliases = new ArrayList();
 		ArrayList formulas = new ArrayList();
 		ArrayList formulaAliases = new ArrayList();
 		ArrayList formulaTemplates = new ArrayList();
 		ArrayList formulasLazy = new ArrayList();
 		ArrayList types = new ArrayList();
 		ArrayList names = new ArrayList();
 		ArrayList classes = new ArrayList();
 		ArrayList templates = new ArrayList();
 		ArrayList propColumns = new ArrayList();
 		ArrayList propColumnReaders = new ArrayList();
 		ArrayList propColumnReaderTemplates = new ArrayList();
 		ArrayList joinedFetchesList = new ArrayList();
 		ArrayList cascades = new ArrayList();
 		ArrayList definedBySubclass = new ArrayList();
 		ArrayList propColumnNumbers = new ArrayList();
 		ArrayList propFormulaNumbers = new ArrayList();
 		ArrayList columnSelectables = new ArrayList();
 		ArrayList propNullables = new ArrayList();
 
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 			names.add( prop.getName() );
 			classes.add( prop.getPersistentClass().getEntityName() );
 			boolean isDefinedBySubclass = !thisClassProperties.contains( prop );
 			definedBySubclass.add( Boolean.valueOf( isDefinedBySubclass ) );
 			propNullables.add( Boolean.valueOf( prop.isOptional() || isDefinedBySubclass ) ); //TODO: is this completely correct?
 			types.add( prop.getType() );
 
 			Iterator colIter = prop.getColumnIterator();
 			String[] cols = new String[prop.getColumnSpan()];
 			String[] readers = new String[prop.getColumnSpan()];
 			String[] readerTemplates = new String[prop.getColumnSpan()];
 			String[] forms = new String[prop.getColumnSpan()];
 			int[] colnos = new int[prop.getColumnSpan()];
 			int[] formnos = new int[prop.getColumnSpan()];
 			int l = 0;
 			Boolean lazy = Boolean.valueOf( prop.isLazy() && lazyAvailable );
 			while ( colIter.hasNext() ) {
 				Selectable thing = ( Selectable ) colIter.next();
 				if ( thing.isFormula() ) {
 					String template = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					formnos[l] = formulaTemplates.size();
 					colnos[l] = -1;
 					formulaTemplates.add( template );
 					forms[l] = template;
 					formulas.add( thing.getText( factory.getDialect() ) );
 					formulaAliases.add( thing.getAlias( factory.getDialect() ) );
 					formulasLazy.add( lazy );
 				}
 				else {
 					Column col = (Column)thing;
 					String colName = col.getQuotedName( factory.getDialect() );
 					colnos[l] = columns.size(); //before add :-)
 					formnos[l] = -1;
 					columns.add( colName );
 					cols[l] = colName;
 					aliases.add( thing.getAlias( factory.getDialect(), prop.getValue().getTable() ) );
 					columnsLazy.add( lazy );
 					columnSelectables.add( Boolean.valueOf( prop.isSelectable() ) );
 
 					readers[l] = col.getReadExpr( factory.getDialect() );
 					String readerTemplate = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					readerTemplates[l] = readerTemplate;
 					columnReaderTemplates.add( readerTemplate );
 				}
 				l++;
 			}
 			propColumns.add( cols );
 			propColumnReaders.add( readers );
 			propColumnReaderTemplates.add( readerTemplates );
 			templates.add( forms );
 			propColumnNumbers.add( colnos );
 			propFormulaNumbers.add( formnos );
 
 			joinedFetchesList.add( prop.getValue().getFetchMode() );
 			cascades.add( prop.getCascadeStyle() );
 		}
 		subclassColumnClosure = ArrayHelper.toStringArray( columns );
 		subclassColumnAliasClosure = ArrayHelper.toStringArray( aliases );
 		subclassColumnLazyClosure = ArrayHelper.toBooleanArray( columnsLazy );
 		subclassColumnSelectableClosure = ArrayHelper.toBooleanArray( columnSelectables );
 		subclassColumnReaderTemplateClosure = ArrayHelper.toStringArray( columnReaderTemplates );
 
 		subclassFormulaClosure = ArrayHelper.toStringArray( formulas );
 		subclassFormulaTemplateClosure = ArrayHelper.toStringArray( formulaTemplates );
 		subclassFormulaAliasClosure = ArrayHelper.toStringArray( formulaAliases );
 		subclassFormulaLazyClosure = ArrayHelper.toBooleanArray( formulasLazy );
 
 		subclassPropertyNameClosure = ArrayHelper.toStringArray( names );
 		subclassPropertySubclassNameClosure = ArrayHelper.toStringArray( classes );
 		subclassPropertyTypeClosure = ArrayHelper.toTypeArray( types );
 		subclassPropertyNullabilityClosure = ArrayHelper.toBooleanArray( propNullables );
 		subclassPropertyFormulaTemplateClosure = ArrayHelper.to2DStringArray( templates );
 		subclassPropertyColumnNameClosure = ArrayHelper.to2DStringArray( propColumns );
 		subclassPropertyColumnReaderClosure = ArrayHelper.to2DStringArray( propColumnReaders );
 		subclassPropertyColumnReaderTemplateClosure = ArrayHelper.to2DStringArray( propColumnReaderTemplates );
 		subclassPropertyColumnNumberClosure = ArrayHelper.to2DIntArray( propColumnNumbers );
 		subclassPropertyFormulaNumberClosure = ArrayHelper.to2DIntArray( propFormulaNumbers );
 
 		subclassPropertyCascadeStyleClosure = new CascadeStyle[cascades.size()];
 		iter = cascades.iterator();
 		int j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyCascadeStyleClosure[j++] = ( CascadeStyle ) iter.next();
 		}
 		subclassPropertyFetchModeClosure = new FetchMode[joinedFetchesList.size()];
 		iter = joinedFetchesList.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyFetchModeClosure[j++] = ( FetchMode ) iter.next();
 		}
 
 		propertyDefinedOnSubclass = new boolean[definedBySubclass.size()];
 		iter = definedBySubclass.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			propertyDefinedOnSubclass[j++] = (Boolean) iter.next();
 		}
 
 		// Handle any filters applied to the class level
 		filterHelper = new FilterHelper( persistentClass.getFilters(), factory );
 
 		temporaryIdTableName = persistentClass.getTemporaryIdTableName();
 		temporaryIdTableDDL = persistentClass.getTemporaryIdTableDDL();
 
+
+		// Check if we can use Reference Cached entities in 2lc
+		// todo : should really validate that the cache access type is read-only
+		boolean refCacheEntries = true;
+		if ( ! factory.getSettings().isDirectReferenceCacheEntriesEnabled() ) {
+			refCacheEntries = false;
+		}
+
+		// for now, limit this to just entities that:
+		// 		1) are immutable
+		if ( entityMetamodel.isMutable() ) {
+			refCacheEntries =  false;
+		}
+
+		//		2)  have no associations.  Eventually we want to be a little more lenient with associations.
+		for ( Type type : getSubclassPropertyTypeClosure() ) {
+			if ( type.isAssociationType() ) {
+				refCacheEntries =  false;
+			}
+		}
+
+		useReferenceCacheEntries = refCacheEntries;
+
 		this.cacheEntryHelper = buildCacheEntryHelper();
+
 	}
 
 	protected CacheEntryHelper buildCacheEntryHelper() {
 		if ( cacheAccessStrategy == null ) {
 			// the entity defined no caching...
 			return NoopCacheEntryHelper.INSTANCE;
 		}
 
 		if ( canUseReferenceCacheEntries() ) {
 			entityMetamodel.setLazy( false );
 			// todo : do we also need to unset proxy factory?
 			return new ReferenceCacheEntryHelper( this );
 		}
 
 		return factory.getSettings().isStructuredCacheEntriesEnabled()
 				? new StructuredCacheEntryHelper( this )
 				: new StandardCacheEntryHelper( this );
 	}
 
 	public boolean canUseReferenceCacheEntries() {
-		// todo : should really validate that the cache access type is read-only
-
-		if ( ! factory.getSettings().isDirectReferenceCacheEntriesEnabled() ) {
-			return false;
-		}
-
-		// for now, limit this to just entities that:
-		// 		1) are immutable
-		if ( entityMetamodel.isMutable() ) {
-			return false;
-		}
-
-		//		2)  have no associations.  Eventually we want to be a little more lenient with associations.
-		for ( Type type : getSubclassPropertyTypeClosure() ) {
-			if ( type.isAssociationType() ) {
-				return false;
-			}
-		}
-
-		return true;
+		return useReferenceCacheEntries;
 	}
 
 	protected static String getTemplateFromString(String string, SessionFactoryImplementor factory) {
 		return string == null ?
 				null :
 				Template.renderWhereStringTemplate( string, factory.getDialect(), factory.getSqlFunctionRegistry() );
 	}
 
 	protected String generateLazySelectString() {
 
 		if ( !entityMetamodel.hasLazyProperties() ) {
 			return null;
 		}
 
 		HashSet tableNumbers = new HashSet();
 		ArrayList columnNumbers = new ArrayList();
 		ArrayList formulaNumbers = new ArrayList();
 		for ( int i = 0; i < lazyPropertyNames.length; i++ ) {
 			// all this only really needs to consider properties
 			// of this class, not its subclasses, but since we
 			// are reusing code used for sequential selects, we
 			// use the subclass closure
 			int propertyNumber = getSubclassPropertyIndex( lazyPropertyNames[i] );
 
 			int tableNumber = getSubclassPropertyTableNumber( propertyNumber );
 			tableNumbers.add(  tableNumber );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int j = 0; j < colNumbers.length; j++ ) {
 				if ( colNumbers[j]!=-1 ) {
 					columnNumbers.add( colNumbers[j] );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int j = 0; j < formNumbers.length; j++ ) {
 				if ( formNumbers[j]!=-1 ) {
 					formulaNumbers.add( formNumbers[j] );
 				}
 			}
 		}
 
 		if ( columnNumbers.size()==0 && formulaNumbers.size()==0 ) {
 			// only one-to-one is lazy fetched
 			return null;
 		}
 
 		return renderSelect( ArrayHelper.toIntArray( tableNumbers ),
 				ArrayHelper.toIntArray( columnNumbers ),
 				ArrayHelper.toIntArray( formulaNumbers ) );
 
 	}
 
 	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session)
 			throws HibernateException {
 
 		final Serializable id = session.getContextEntityIdentifier( entity );
 
 		final EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 		if ( entry == null ) {
 			throw new HibernateException( "entity is not associated with the session: " + id );
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Initializing lazy properties of: {0}, field access: {1}", MessageHelper.infoString( this, id, getFactory() ), fieldName );
 		}
 
 		if ( session.getCacheMode().isGetEnabled() && hasCache() ) {
 			final CacheKey cacheKey = session.generateCacheKey( id, getIdentifierType(), getEntityName() );
 			final Object ce = CacheHelper.fromSharedCache( session, cacheKey, getCacheAccessStrategy() );
 			if ( ce != null ) {
 				final CacheEntry cacheEntry = (CacheEntry) getCacheEntryStructure().destructure(ce, factory);
 				if ( !cacheEntry.areLazyPropertiesUnfetched() ) {
 					//note early exit here:
 					return initializeLazyPropertiesFromCache( fieldName, entity, session, entry, cacheEntry );
 				}
 			}
 		}
 
 		return initializeLazyPropertiesFromDatastore( fieldName, entity, session, id, entry );
 
 	}
 
 	private Object initializeLazyPropertiesFromDatastore(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Serializable id,
 			final EntityEntry entry) {
 
 		if ( !hasLazyProperties() ) throw new AssertionFailure( "no lazy properties" );
 
 		LOG.trace( "Initializing lazy properties from datastore" );
 
 		try {
 
 			Object result = null;
 			PreparedStatement ps = null;
 			try {
 				final String lazySelect = getSQLLazySelectString();
 				ResultSet rs = null;
 				try {
 					if ( lazySelect != null ) {
 						// null sql means that the only lazy properties
 						// are shared PK one-to-one associations which are
 						// handled differently in the Type#nullSafeGet code...
 						ps = session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( lazySelect );
 						getIdentifierType().nullSafeSet( ps, id, 1, session );
 						rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 						rs.next();
 					}
 					final Object[] snapshot = entry.getLoadedState();
 					for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 						Object propValue = lazyPropertyTypes[j].nullSafeGet( rs, lazyPropertyColumnAliases[j], session, entity );
 						if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 							result = propValue;
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 				}
 			}
 
 			LOG.trace( "Done initializing lazy properties" );
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize lazy properties: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getSQLLazySelectString()
 				);
 		}
 	}
 
 	private Object initializeLazyPropertiesFromCache(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final EntityEntry entry,
 			final CacheEntry cacheEntry
 	) {
 
 		LOG.trace( "Initializing lazy properties from second-level cache" );
 
 		Object result = null;
 		Serializable[] disassembledValues = cacheEntry.getDisassembledState();
 		final Object[] snapshot = entry.getLoadedState();
 		for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 			final Object propValue = lazyPropertyTypes[j].assemble(
 					disassembledValues[ lazyPropertyNumbers[j] ],
 					session,
 					entity
 				);
 			if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 				result = propValue;
 			}
 		}
 
 		LOG.trace( "Done initializing lazy properties" );
 
 		return result;
 	}
 
 	private boolean initializeLazyProperty(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Object[] snapshot,
 			final int j,
 			final Object propValue) {
 		setPropertyValue( entity, lazyPropertyNumbers[j], propValue );
 		if ( snapshot != null ) {
 			// object have been loaded with setReadOnly(true); HHH-2236
 			snapshot[ lazyPropertyNumbers[j] ] = lazyPropertyTypes[j].deepCopy( propValue, factory );
 		}
 		return fieldName.equals( lazyPropertyNames[j] );
 	}
 
 	public boolean isBatchable() {
 		return optimisticLockStyle() == OptimisticLockStyle.NONE
 				|| ( !isVersioned() && optimisticLockStyle() == OptimisticLockStyle.VERSION )
 				|| getFactory().getSettings().isJdbcBatchVersionedData();
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return getPropertySpaces();
 	}
 
 	protected Set getLazyProperties() {
 		return lazyProperties;
 	}
 
 	public boolean isBatchLoadable() {
 		return batchSize > 1;
 	}
 
 	public String[] getIdentifierColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	public String[] getIdentifierColumnReaders() {
 		return rootTableKeyColumnReaders;
 	}
 
 	public String[] getIdentifierColumnReaderTemplates() {
 		return rootTableKeyColumnReaderTemplates;
 	}
 
 	protected int getIdentifierColumnSpan() {
 		return identifierColumnSpan;
 	}
 
 	protected String[] getIdentifierAliases() {
 		return identifierAliases;
 	}
 
 	public String getVersionColumnName() {
 		return versionColumnName;
 	}
 
 	protected String getVersionedTableName() {
 		return getTableName( 0 );
 	}
 
 	protected boolean[] getSubclassColumnLazyiness() {
 		return subclassColumnLazyClosure;
 	}
 
 	protected boolean[] getSubclassFormulaLazyiness() {
 		return subclassFormulaLazyClosure;
 	}
 
 	/**
 	 * We can't immediately add to the cache if we have formulas
 	 * which must be evaluated, or if we have the possibility of
 	 * two concurrent updates to the same item being merged on
 	 * the database. This can happen if (a) the item is not
 	 * versioned and either (b) we have dynamic update enabled
 	 * or (c) we have multiple tables holding the state of the
 	 * item.
 	 */
 	public boolean isCacheInvalidationRequired() {
 		return hasFormulaProperties() ||
 				( !isVersioned() && ( entityMetamodel.isDynamicUpdate() || getTableSpan() > 1 ) );
 	}
 
 	public boolean isLazyPropertiesCacheable() {
 		return isLazyPropertiesCacheable;
 	}
 
 	public String selectFragment(String alias, String suffix) {
 		return identifierSelectFragment( alias, suffix ) +
 				propertySelectFragment( alias, suffix, false );
 	}
 
 	public String[] getIdentifierAliases(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getIdentiferColumnNames() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return new Alias( suffix ).toAliasStrings( getIdentifierAliases() );
 	}
 
 	public String[] getPropertyAliases(String suffix, int i) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		return new Alias( suffix ).toUnquotedAliasStrings( propertyColumnAliases[i] );
 	}
 
 	public String getDiscriminatorAlias(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getdiscriminatorColumnName() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return entityMetamodel.hasSubclasses() ?
 				new Alias( suffix ).toAliasString( getDiscriminatorAlias() ) :
 				null;
 	}
 
 	public String identifierSelectFragment(String name, String suffix) {
 		return new SelectFragment()
 				.setSuffix( suffix )
 				.addColumns( name, getIdentifierColumnNames(), getIdentifierAliases() )
 				.toFragmentString()
 				.substring( 2 ); //strip leading ", "
 	}
 
 
 	public String propertySelectFragment(String tableAlias, String suffix, boolean allProperties) {
 		return propertySelectFragmentFragment( tableAlias, suffix, allProperties ).toFragmentString();
 	}
 
 	public SelectFragment propertySelectFragmentFragment(
 			String tableAlias,
 			String suffix,
 			boolean allProperties) {
 		SelectFragment select = new SelectFragment()
 				.setSuffix( suffix )
 				.setUsedAliases( getIdentifierAliases() );
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < getSubclassColumnClosure().length; i++ ) {
 			boolean selectable = ( allProperties || !subclassColumnLazyClosure[i] ) &&
 				!isSubclassTableSequentialSelect( columnTableNumbers[i] ) &&
 				subclassColumnSelectableClosure[i];
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, columnTableNumbers[i] );
 				select.addColumnTemplate( subalias, columnReaderTemplates[i], columnAliases[i] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < getSubclassFormulaTemplateClosure().length; i++ ) {
 			boolean selectable = ( allProperties || !subclassFormulaLazyClosure[i] )
 				&& !isSubclassTableSequentialSelect( formulaTableNumbers[i] );
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, formulaTableNumbers[i] );
 				select.addFormula( subalias, formulaTemplates[i], formulaAliases[i] );
 			}
 		}
 
 		if ( entityMetamodel.hasSubclasses() ) {
 			addDiscriminatorToSelect( select, tableAlias, suffix );
 		}
 
 		if ( hasRowId() ) {
 			select.addColumn( tableAlias, rowIdName, ROWID_ALIAS );
 		}
 
 		return select;
 	}
 
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting current persistent state for: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getSQLSnapshotSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				//if ( isVersioned() ) getVersionType().nullSafeSet( ps, version, getIdentifierColumnSpan()+1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					//otherwise return the "hydrated" state (ie. associations are not resolved)
 					Type[] types = getPropertyTypes();
 					Object[] values = new Object[types.length];
 					boolean[] includeProperty = getPropertyUpdateability();
 					for ( int i = 0; i < types.length; i++ ) {
 						if ( includeProperty[i] ) {
 							values[i] = types[i].hydrate( rs, getPropertyAliases( "", i ), session, null ); //null owner ok??
 						}
 					}
 					return values;
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 			        getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	@Override
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) throws HibernateException {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"resolving unique key [%s] to identifier for entity [%s]",
 					key,
 					getEntityName()
 			);
 		}
 
 		int propertyIndex = getSubclassPropertyIndex( uniquePropertyName );
 		if ( propertyIndex < 0 ) {
 			throw new HibernateException(
 					"Could not determine Type for property [" + uniquePropertyName + "] on entity [" + getEntityName() + "]"
 			);
 		}
 		Type propertyType = getSubclassPropertyType( propertyIndex );
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( generateIdByUniqueKeySelectString( uniquePropertyName ) );
 			try {
 				propertyType.nullSafeSet( ps, key, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					return (Serializable) getIdentifierType().nullSafeGet( rs, getIdentifierAliases(), session, null );
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					String.format(
 							"could not resolve unique property [%s] to identifier for entity [%s]",
 							uniquePropertyName,
 							getEntityName()
 					),
 					getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	protected String generateIdByUniqueKeySelectString(String uniquePropertyName) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "resolve id by unique property [" + getEntityName() + "." + uniquePropertyName + "]" );
 		}
 
 		final String rooAlias = getRootAlias();
 
 		select.setFromClause( fromTableFragment( rooAlias ) + fromJoinFragment( rooAlias, true, false ) );
 
 		SelectFragment selectFragment = new SelectFragment();
 		selectFragment.addColumns( rooAlias, getIdentifierColumnNames(), getIdentifierAliases() );
 		select.setSelectClause( selectFragment );
 
 		StringBuilder whereClauseBuffer = new StringBuilder();
 		final int uniquePropertyIndex = getSubclassPropertyIndex( uniquePropertyName );
 		final String uniquePropertyTableAlias = generateTableAlias(
 				rooAlias,
 				getSubclassPropertyTableNumber( uniquePropertyIndex )
 		);
 		String sep = "";
 		for ( String columnTemplate : getSubclassPropertyColumnReaderTemplateClosure()[uniquePropertyIndex] ) {
 			if ( columnTemplate == null ) {
 				continue;
 			}
 			final String columnReference = StringHelper.replace( columnTemplate, Template.TEMPLATE, uniquePropertyTableAlias );
 			whereClauseBuffer.append( sep ).append( columnReference ).append( "=?" );
 			sep = " and ";
 		}
 		for ( String formulaTemplate : getSubclassPropertyFormulaTemplateClosure()[uniquePropertyIndex] ) {
 			if ( formulaTemplate == null ) {
 				continue;
 			}
 			final String formulaReference = StringHelper.replace( formulaTemplate, Template.TEMPLATE, uniquePropertyTableAlias );
 			whereClauseBuffer.append( sep ).append( formulaReference ).append( "=?" );
 			sep = " and ";
 		}
 		whereClauseBuffer.append( whereJoinFragment( rooAlias, true, false ) );
 
 		select.setWhereClause( whereClauseBuffer.toString() );
 
 		return select.setOuterJoins( "", "" ).toStatementString();
 	}
 
 
 	/**
 	 * Generate the SQL that selects the version number by id
 	 */
 	protected String generateSelectVersionString() {
 		SimpleSelect select = new SimpleSelect( getFactory().getDialect() )
 				.setTableName( getVersionedTableName() );
 		if ( isVersioned() ) {
 			select.addColumn( versionColumnName );
 		}
 		else {
 			select.addColumns( rootTableKeyColumnNames );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get version " + getEntityName() );
 		}
 		return select.addCondition( rootTableKeyColumnNames, "=?" ).toStatementString();
 	}
 
 	public boolean[] getPropertyUniqueness() {
 		return propertyUniqueness;
 	}
 
 	protected String generateInsertGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( GenerationTiming.INSERT );
 	}
 
 	protected String generateUpdateGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( GenerationTiming.ALWAYS );
 	}
 
 	private String generateGeneratedValuesSelectString(final GenerationTiming generationTimingToMatch) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get generated state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 
 		// Here we render the select column list based on the properties defined as being generated.
 		// For partial component generation, we currently just re-select the whole component
 		// rather than trying to handle the individual generated portions.
 		String selectClause = concretePropertySelectFragment(
 				getRootAlias(),
 				new InclusionChecker() {
 					@Override
 					public boolean includeProperty(int propertyNumber) {
 						final InDatabaseValueGenerationStrategy generationStrategy
 								= entityMetamodel.getInDatabaseValueGenerationStrategies()[propertyNumber];
 						return generationStrategy != null
 								&& timingsMatch( generationStrategy.getGenerationTiming(), generationTimingToMatch );
 					}
 				}
 		);
 		selectClause = selectClause.substring( 2 );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuilder()
 				.append( StringHelper.join( "=? and ", aliasedIdColumns ) )
 				.append( "=?" )
 				.append( whereJoinFragment( getRootAlias(), true, false ) )
 				.toString();
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	protected static interface InclusionChecker {
 		public boolean includeProperty(int propertyNumber);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final boolean[] includeProperty) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					public boolean includeProperty(int propertyNumber) {
 						return includeProperty[propertyNumber];
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, InclusionChecker inclusionChecker) {
 		int propertyCount = getPropertyNames().length;
 		int[] propertyTableNumbers = getPropertyTableNumbersInSelect();
 		SelectFragment frag = new SelectFragment();
 		for ( int i = 0; i < propertyCount; i++ ) {
 			if ( inclusionChecker.includeProperty( i ) ) {
 				frag.addColumnTemplates(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnReaderTemplates[i],
 						propertyColumnAliases[i]
 				);
 				frag.addFormulas(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnFormulaTemplates[i],
 						propertyColumnAliases[i]
 				);
 			}
 		}
 		return frag.toFragmentString();
 	}
 
 	protected String generateSnapshotSelectString() {
 
 		//TODO: should we use SELECT .. FOR UPDATE?
 
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String selectClause = StringHelper.join( ", ", aliasedIdColumns ) +
 				concretePropertySelectFragment( getRootAlias(), getPropertyUpdateability() );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuilder()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		/*if ( isVersioned() ) {
 			where.append(" and ")
 				.append( getVersionColumnName() )
 				.append("=?");
 		}*/
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 		if ( !isVersioned() ) {
 			throw new AssertionFailure( "cannot force version increment on non-versioned entity" );
 		}
 
 		if ( isVersionPropertyGenerated() ) {
 			// the difficulty here is exactly what do we update in order to
 			// force the version to be incremented in the db...
 			throw new HibernateException( "LockMode.FORCE is currently not supported for generated version properties" );
 		}
 
 		Object nextVersion = getVersionType().next( currentVersion, session );
         if (LOG.isTraceEnabled()) LOG.trace("Forcing version increment [" + MessageHelper.infoString(this, id, getFactory()) + "; "
                                             + getVersionType().toLoggableString(currentVersion, getFactory()) + " -> "
                                             + getVersionType().toLoggableString(nextVersion, getFactory()) + "]");
 
 		// todo : cache this sql...
 		String versionIncrementString = generateVersionIncrementUpdateString();
 		PreparedStatement st = null;
 		try {
 			st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( versionIncrementString, false );
 			try {
 				getVersionType().nullSafeSet( st, nextVersion, 1, session );
 				getIdentifierType().nullSafeSet( st, id, 2, session );
 				getVersionType().nullSafeSet( st, currentVersion, 2 + getIdentifierColumnSpan(), session );
 				int rows = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st );
 				if ( rows != 1 ) {
 					throw new StaleObjectStateException( getEntityName(), id );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve version: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 				);
 		}
 
 		return nextVersion;
 	}
 
 	private String generateVersionIncrementUpdateString() {
 		Update update = new Update( getFactory().getDialect() );
 		update.setTableName( getTableName( 0 ) );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "forced version increment" );
 		}
 		update.addColumn( getVersionColumnName() );
 		update.addPrimaryKeyColumns( getIdentifierColumnNames() );
 		update.setVersionColumnName( getVersionColumnName() );
 		return update.toStatementString();
 	}
 
 	/**
 	 * Retrieve the version number
 	 */
 	public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting version: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getVersionSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( st, id, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					if ( !rs.next() ) {
 						return null;
 					}
 					if ( !isVersioned() ) {
 						return this;
 					}
 					return getVersionType().nullSafeGet( rs, getVersionColumnName(), session, null );
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve version: " + MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 			);
 		}
 	}
 
 	protected void initLockers() {
 		lockers.put( LockMode.READ, generateLocker( LockMode.READ ) );
 		lockers.put( LockMode.UPGRADE, generateLocker( LockMode.UPGRADE ) );
 		lockers.put( LockMode.UPGRADE_NOWAIT, generateLocker( LockMode.UPGRADE_NOWAIT ) );
 		lockers.put( LockMode.UPGRADE_SKIPLOCKED, generateLocker( LockMode.UPGRADE_SKIPLOCKED ) );
 		lockers.put( LockMode.FORCE, generateLocker( LockMode.FORCE ) );
 		lockers.put( LockMode.PESSIMISTIC_READ, generateLocker( LockMode.PESSIMISTIC_READ ) );
 		lockers.put( LockMode.PESSIMISTIC_WRITE, generateLocker( LockMode.PESSIMISTIC_WRITE ) );
 		lockers.put( LockMode.PESSIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.PESSIMISTIC_FORCE_INCREMENT ) );
 		lockers.put( LockMode.OPTIMISTIC, generateLocker( LockMode.OPTIMISTIC ) );
 		lockers.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.OPTIMISTIC_FORCE_INCREMENT ) );
 	}
 
 	protected LockingStrategy generateLocker(LockMode lockMode) {
 		return factory.getDialect().getLockingStrategy( this, lockMode );
 	}
 
 	private LockingStrategy getLocker(LockMode lockMode) {
 		return ( LockingStrategy ) lockers.get( lockMode );
 	}
 
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockMode lockMode,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockMode ).lock( id, version, object, LockOptions.WAIT_FOREVER, session );
 	}
 
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockOptions lockOptions,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockOptions.getLockMode() ).lock( id, version, object, lockOptions.getTimeOut(), session );
 	}
 
 	public String getRootTableName() {
 		return getSubclassTableName( 0 );
 	}
 
 	public String getRootTableAlias(String drivingAlias) {
 		return drivingAlias;
 	}
 
 	public String[] getRootTableIdentifierColumnNames() {
 		return getRootTableKeyColumnNames();
 	}
 
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		return propertyMapping.toColumns( alias, propertyName );
 	}
 
 	public String[] toColumns(String propertyName) throws QueryException {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public String[] getPropertyColumnNames(String propertyName) {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	/**
 	 * Warning:
 	 * When there are duplicated property names in the subclasses
 	 * of the class, this method may return the wrong table
 	 * number for the duplicated subclass property (note that
 	 * SingleTableEntityPersister defines an overloaded form
 	 * which takes the entity name.
 	 */
 	public int getSubclassPropertyTableNumber(String propertyPath) {
 		String rootPropertyName = StringHelper.root(propertyPath);
 		Type type = propertyMapping.toType(rootPropertyName);
 		if ( type.isAssociationType() ) {
 			AssociationType assocType = ( AssociationType ) type;
 			if ( assocType.useLHSPrimaryKey() ) {
 				// performance op to avoid the array search
 				return 0;
 			}
 			else if ( type.isCollectionType() ) {
 				// properly handle property-ref-based associations
 				rootPropertyName = assocType.getLHSPropertyName();
 			}
 		}
 		//Enable for HHH-440, which we don't like:
 		/*if ( type.isComponentType() && !propertyName.equals(rootPropertyName) ) {
 			String unrooted = StringHelper.unroot(propertyName);
 			int idx = ArrayHelper.indexOf( getSubclassColumnClosure(), unrooted );
 			if ( idx != -1 ) {
 				return getSubclassColumnTableNumberClosure()[idx];
 			}
 		}*/
 		int index = ArrayHelper.indexOf( getSubclassPropertyNameClosure(), rootPropertyName); //TODO: optimize this better!
 		return index==-1 ? 0 : getSubclassPropertyTableNumber(index);
 	}
 
 	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
 		int tableIndex = getSubclassPropertyTableNumber( propertyPath );
 		if ( tableIndex == 0 ) {
 			return Declarer.CLASS;
 		}
 		else if ( isClassOrSuperclassTable( tableIndex ) ) {
 			return Declarer.SUPERCLASS;
 		}
 		else {
 			return Declarer.SUBCLASS;
 		}
 	}
 
 	private DiscriminatorMetadata discriminatorMetadata;
 
 	public DiscriminatorMetadata getTypeDiscriminatorMetadata() {
 		if ( discriminatorMetadata == null ) {
 			discriminatorMetadata = buildTypeDiscriminatorMetadata();
 		}
 		return discriminatorMetadata;
 	}
 
 	private DiscriminatorMetadata buildTypeDiscriminatorMetadata() {
 		return new DiscriminatorMetadata() {
 			public String getSqlFragment(String sqlQualificationAlias) {
 				return toColumns( sqlQualificationAlias, ENTITY_CLASS )[0];
 			}
 
 			public Type getResolutionType() {
 				return new DiscriminatorType( getDiscriminatorType(), AbstractEntityPersister.this );
 			}
 		};
 	}
 
 	public static String generateTableAlias(String rootAlias, int tableNumber) {
 		if ( tableNumber == 0 ) {
 			return rootAlias;
 		}
 		StringBuilder buf = new StringBuilder().append( rootAlias );
 		if ( !rootAlias.endsWith( "_" ) ) {
 			buf.append( '_' );
 		}
 		return buf.append( tableNumber ).append( '_' ).toString();
 	}
 
 	public String[] toColumns(String name, final int i) {
 		final String alias = generateTableAlias( name, getSubclassPropertyTableNumber( i ) );
 		String[] cols = getSubclassPropertyColumnNames( i );
 		String[] templates = getSubclassPropertyFormulaTemplateClosure()[i];
 		String[] result = new String[cols.length];
 		for ( int j = 0; j < cols.length; j++ ) {
 			if ( cols[j] == null ) {
 				result[j] = StringHelper.replace( templates[j], Template.TEMPLATE, alias );
 			}
 			else {
 				result[j] = StringHelper.qualify( alias, cols[j] );
 			}
 		}
 		return result;
 	}
 
 	private int getSubclassPropertyIndex(String propertyName) {
 		return ArrayHelper.indexOf(subclassPropertyNameClosure, propertyName);
 	}
 
 	protected String[] getPropertySubclassNames() {
 		return propertySubclassNames;
 	}
 
 	public String[] getPropertyColumnNames(int i) {
 		return propertyColumnNames[i];
 	}
 
 	public String[] getPropertyColumnWriters(int i) {
 		return propertyColumnWriters[i];
 	}
 
 	protected int getPropertyColumnSpan(int i) {
 		return propertyColumnSpans[i];
 	}
 
 	protected boolean hasFormulaProperties() {
 		return hasFormulaProperties;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return subclassPropertyFetchModeClosure[i];
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return subclassPropertyCascadeStyleClosure[i];
 	}
 
 	public Type getSubclassPropertyType(int i) {
 		return subclassPropertyTypeClosure[i];
 	}
 
 	public String getSubclassPropertyName(int i) {
 		return subclassPropertyNameClosure[i];
 	}
 
 	public int countSubclassProperties() {
 		return subclassPropertyTypeClosure.length;
 	}
 
 	public String[] getSubclassPropertyColumnNames(int i) {
 		return subclassPropertyColumnNameClosure[i];
 	}
 
 	public boolean isDefinedOnSubclass(int i) {
 		return propertyDefinedOnSubclass[i];
 	}
 
 	@Override
 	public String[][] getSubclassPropertyFormulaTemplateClosure() {
 		return subclassPropertyFormulaTemplateClosure;
 	}
 
 	protected Type[] getSubclassPropertyTypeClosure() {
 		return subclassPropertyTypeClosure;
 	}
 
 	protected String[][] getSubclassPropertyColumnNameClosure() {
 		return subclassPropertyColumnNameClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderClosure() {
 		return subclassPropertyColumnReaderClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderTemplateClosure() {
 		return subclassPropertyColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassPropertyNameClosure() {
 		return subclassPropertyNameClosure;
 	}
 
 	@Override
 	public int[] resolveAttributeIndexes(Set<String> properties) {
 		Iterator<String> iter = properties.iterator();
 		int[] fields = new int[properties.size()];
 		int counter = 0;
 		while(iter.hasNext()) {
 			Integer index = entityMetamodel.getPropertyIndexOrNull( iter.next() );
 			if ( index != null )
 				fields[counter++] = index;
 		}
 		return fields;
 	}
 
 	protected String[] getSubclassPropertySubclassNameClosure() {
@@ -3818,1286 +3833,1285 @@ public abstract class AbstractEntityPersister
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
 		if ( result != null ) {
 			return result;
 		}
 
 		// check to see if it is in the second-level cache
 		if ( session.getCacheMode().isGetEnabled() && hasCache() ) {
 			final CacheKey ck = session.generateCacheKey( id, getIdentifierType(), getRootEntityName() );
 			final Object ce = CacheHelper.fromSharedCache( session, ck, getCacheAccessStrategy() );
 			if ( ce != null ) {
 				return Boolean.FALSE;
 			}
 		}
 
 		return null;
 	}
 
 	public boolean hasCollections() {
 		return entityMetamodel.hasCollections();
 	}
 
 	public boolean hasMutableProperties() {
 		return entityMetamodel.hasMutableProperties();
 	}
 
 	public boolean isMutable() {
 		return entityMetamodel.isMutable();
 	}
 
 	private boolean isModifiableEntity(EntityEntry entry) {
 
 		return ( entry == null ? isMutable() : entry.isModifiableEntity() );
 	}
 
 	public boolean isAbstract() {
 		return entityMetamodel.isAbstract();
 	}
 
 	public boolean hasSubclasses() {
 		return entityMetamodel.hasSubclasses();
 	}
 
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() throws HibernateException {
 		return entityMetamodel.getIdentifierProperty().getIdentifierGenerator();
 	}
 
 	public String getRootEntityName() {
 		return entityMetamodel.getRootName();
 	}
 
 	public ClassMetadata getClassMetadata() {
 		return this;
 	}
 
 	public String getMappedSuperclass() {
 		return entityMetamodel.getSuperclass();
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return entityMetamodel.isExplicitPolymorphism();
 	}
 
 	protected boolean useDynamicUpdate() {
 		return entityMetamodel.isDynamicUpdate();
 	}
 
 	protected boolean useDynamicInsert() {
 		return entityMetamodel.isDynamicInsert();
 	}
 
 	protected boolean hasEmbeddedCompositeIdentifier() {
 		return entityMetamodel.getIdentifierProperty().isEmbedded();
 	}
 
 	public boolean canExtractIdOutOfEntity() {
 		return hasIdentifierProperty() || hasEmbeddedCompositeIdentifier() || hasIdentifierMapper();
 	}
 
 	private boolean hasIdentifierMapper() {
 		return entityMetamodel.getIdentifierProperty().hasIdentifierMapper();
 	}
 
 	public String[] getKeyColumnNames() {
 		return getIdentifierColumnNames();
 	}
 
 	public String getName() {
 		return getEntityName();
 	}
 
 	public boolean isCollection() {
 		return false;
 	}
 
 	public boolean consumesEntityAlias() {
 		return true;
 	}
 
 	public boolean consumesCollectionAlias() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) throws MappingException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public Type getType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return entityMetamodel.isSelectBeforeUpdate();
 	}
 
 	protected final OptimisticLockStyle optimisticLockStyle() {
 		return entityMetamodel.getOptimisticLockStyle();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 		return entityMetamodel.getTuplizer().createProxy( id, session );
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) +
 				'(' + entityMetamodel.getName() + ')';
 	}
 
 	public final String selectFragment(
 			Joinable rhs,
 			String rhsAlias,
 			String lhsAlias,
 			String entitySuffix,
 			String collectionSuffix,
 			boolean includeCollectionColumns) {
 		return selectFragment( lhsAlias, entitySuffix );
 	}
 
 	public boolean isInstrumented() {
 		return entityMetamodel.isInstrumented();
 	}
 
 	public boolean hasInsertGeneratedProperties() {
 		return entityMetamodel.hasInsertGeneratedValues();
 	}
 
 	public boolean hasUpdateGeneratedProperties() {
 		return entityMetamodel.hasUpdateGeneratedValues();
 	}
 
 	public boolean isVersionPropertyGenerated() {
 		return isVersioned() && getEntityMetamodel().isVersionGenerated();
 	}
 
 	public boolean isVersionPropertyInsertable() {
 		return isVersioned() && getPropertyInsertability() [ getVersionProperty() ];
 	}
 
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		getEntityTuplizer().afterInitialize( entity, lazyPropertiesAreUnfetched, session );
 	}
 
 	public String[] getPropertyNames() {
 		return entityMetamodel.getPropertyNames();
 	}
 
 	public Type[] getPropertyTypes() {
 		return entityMetamodel.getPropertyTypes();
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return entityMetamodel.getPropertyLaziness();
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return entityMetamodel.getPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return entityMetamodel.getPropertyCheckability();
 	}
 
 	public boolean[] getNonLazyPropertyUpdateability() {
 		return entityMetamodel.getNonlazyPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return entityMetamodel.getPropertyInsertability();
 	}
 
 	@Deprecated
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return null;
 	}
 
 	@Deprecated
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return null;
 	}
 
 	public boolean[] getPropertyNullability() {
 		return entityMetamodel.getPropertyNullability();
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return entityMetamodel.getPropertyVersionability();
 	}
 
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return entityMetamodel.getCascadeStyles();
 	}
 
 	public final Class getMappedClass() {
 		return getEntityTuplizer().getMappedClass();
 	}
 
 	public boolean implementsLifecycle() {
 		return getEntityTuplizer().isLifecycleImplementor();
 	}
 
 	public Class getConcreteProxyClass() {
 		return getEntityTuplizer().getConcreteProxyClass();
 	}
 
 	public void setPropertyValues(Object object, Object[] values) {
 		getEntityTuplizer().setPropertyValues( object, values );
 	}
 
 	public void setPropertyValue(Object object, int i, Object value) {
 		getEntityTuplizer().setPropertyValue( object, i, value );
 	}
 
 	public Object[] getPropertyValues(Object object) {
 		return getEntityTuplizer().getPropertyValues( object );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) {
 		return getEntityTuplizer().getPropertyValue( object, i );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) {
 		return getEntityTuplizer().getPropertyValue( object, propertyName );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) {
 		return getEntityTuplizer().getIdentifier( object, null );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return getEntityTuplizer().getIdentifier( entity, session );
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		getEntityTuplizer().setIdentifier( entity, id, session );
 	}
 
 	@Override
 	public Object getVersion(Object object) {
 		return getEntityTuplizer().getVersion( object );
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		return getEntityTuplizer().instantiate( id, session );
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return getEntityTuplizer().isInstance( object );
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return getEntityTuplizer().hasUninitializedLazyProperties( object );
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		getEntityTuplizer().resetIdentifier( entity, currentId, currentVersion, session );
 	}
 
 	@Override
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		if ( !hasSubclasses() ) {
 			return this;
 		}
 		else {
 			final String concreteEntityName = getEntityTuplizer().determineConcreteSubclassEntityName(
 					instance,
 					factory
 			);
 			if ( concreteEntityName == null || getEntityName().equals( concreteEntityName ) ) {
 				// the contract of EntityTuplizer.determineConcreteSubclassEntityName says that returning null
 				// is an indication that the specified entity-name (this.getEntityName) should be used.
 				return this;
 			}
 			else {
 				return factory.getEntityPersister( concreteEntityName );
 			}
 		}
 	}
 
 	public boolean isMultiTable() {
 		return false;
 	}
 
 	public String getTemporaryIdTableName() {
 		return temporaryIdTableName;
 	}
 
 	public String getTemporaryIdTableDDL() {
 		return temporaryIdTableDDL;
 	}
 
 	protected int getPropertySpan() {
 		return entityMetamodel.getPropertySpan();
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException {
 		return getEntityTuplizer().getPropertyValuesToInsert( object, mergeMap, session );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasInsertGeneratedProperties() ) {
 			throw new AssertionFailure("no insert-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlInsertGeneratedValuesSelectString, GenerationTiming.INSERT );
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasUpdateGeneratedProperties() ) {
 			throw new AssertionFailure("no update-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlUpdateGeneratedValuesSelectString, GenerationTiming.ALWAYS );
 	}
 
 	private void processGeneratedProperties(
 			Serializable id,
 	        Object entity,
 	        Object[] state,
 	        SessionImplementor session,
 	        String selectionSQL,
 			GenerationTiming matchTiming) {
 		// force immediate execution of the insert batch (if one)
 		session.getTransactionCoordinator().getJdbcCoordinator().executeBatch();
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( selectionSQL );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					if ( !rs.next() ) {
 						throw new HibernateException(
 								"Unable to locate row for retrieval of generated properties: " +
 								MessageHelper.infoString( this, id, getFactory() )
 							);
 					}
 					int propertyIndex = -1;
 					for ( NonIdentifierAttribute attribute : entityMetamodel.getProperties() ) {
 						propertyIndex++;
 						final ValueGeneration valueGeneration = attribute.getValueGenerationStrategy();
 						if ( isReadRequired( valueGeneration, matchTiming ) ) {
 							final Object hydratedState = attribute.getType().hydrate(
 									rs, getPropertyAliases(
 									"",
 									propertyIndex
 							), session, entity
 							);
 							state[propertyIndex] = attribute.getType().resolve( hydratedState, session, entity );
 							setPropertyValue( entity, propertyIndex, state[propertyIndex] );
 						}
 					}
 //					for ( int i = 0; i < getPropertySpan(); i++ ) {
 //						if ( includeds[i] != ValueInclusion.NONE ) {
 //							Object hydratedState = getPropertyTypes()[i].hydrate( rs, getPropertyAliases( "", i ), session, entity );
 //							state[i] = getPropertyTypes()[i].resolve( hydratedState, session, entity );
 //							setPropertyValue( entity, i, state[i] );
 //						}
 //					}
 				}
 				finally {
 					if ( rs != null ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"unable to select generated column values",
 					selectionSQL
 			);
 		}
 
 	}
 
 	/**
 	 * Whether the given value generation strategy requires to read the value from the database or not.
 	 */
 	private boolean isReadRequired(ValueGeneration valueGeneration, GenerationTiming matchTiming) {
 		return valueGeneration != null &&
 				valueGeneration.getValueGenerator() == null &&
 				timingsMatch( valueGeneration.getGenerationTiming(), matchTiming );
 	}
 
 	private boolean timingsMatch(GenerationTiming timing, GenerationTiming matchTiming) {
 		return
 				(matchTiming == GenerationTiming.INSERT && timing.includesInsert()) ||
 						(matchTiming == GenerationTiming.ALWAYS && timing.includesUpdate());
 	}
 
 	public String getIdentifierPropertyName() {
 		return entityMetamodel.getIdentifierProperty().getName();
 	}
 
 	public Type getIdentifierType() {
 		return entityMetamodel.getIdentifierProperty().getType();
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return entityMetamodel.getNaturalIdentifierProperties();
 	}
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !hasNaturalIdentifier() ) {
 			throw new MappingException( "persistent class did not define a natural-id : " + MessageHelper.infoString( this ) );
 		}
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting current natural-id snapshot state for: {0}",
 					MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		int[] naturalIdPropertyIndexes = getNaturalIdentifierProperties();
 		int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
 		boolean[] naturalIdMarkers = new boolean[ getPropertySpan() ];
 		Type[] extractionTypes = new Type[ naturalIdPropertyCount ];
 		for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 			extractionTypes[i] = getPropertyTypes()[ naturalIdPropertyIndexes[i] ];
 			naturalIdMarkers[ naturalIdPropertyIndexes[i] ] = true;
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// TODO : look at perhaps caching this...
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id state " + getEntityName() );
 		}
 		select.setSelectClause( concretePropertySelectFragmentSansLeadingComma( getRootAlias(), naturalIdMarkers ) );
 		select.setFromClause( fromTableFragment( getRootAlias() ) + fromJoinFragment( getRootAlias(), true, false ) );
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String whereClause = new StringBuilder()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		String sql = select.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 		///////////////////////////////////////////////////////////////////////
 
 		Object[] snapshot = new Object[ naturalIdPropertyCount ];
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					final EntityKey key = session.generateEntityKey( id, this );
 					Object owner = session.getPersistenceContext().getEntity( key );
 					for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 						snapshot[i] = extractionTypes[i].hydrate( rs, getPropertyAliases( "", naturalIdPropertyIndexes[i] ), session, null );
 						if (extractionTypes[i].isEntityType()) {
 							snapshot[i] = extractionTypes[i].resolve(snapshot[i], session, owner);
 						}
 					}
 					return snapshot;
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 			        sql
 			);
 		}
 	}
 
 	@Override
 	public Serializable loadEntityIdByNaturalId(
 			Object[] naturalIdValues,
 			LockOptions lockOptions,
 			SessionImplementor session) {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"Resolving natural-id [%s] to id : %s ",
 					naturalIdValues,
 					MessageHelper.infoString( this )
 			);
 		}
 
 		final boolean[] valueNullness = determineValueNullness( naturalIdValues );
 		final String sqlEntityIdByNaturalIdString = determinePkByNaturalIdQuery( valueNullness );
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlEntityIdByNaturalIdString );
 			try {
 				int positions = 1;
 				int loop = 0;
 				for ( int idPosition : getNaturalIdentifierProperties() ) {
 					final Object naturalIdValue = naturalIdValues[loop++];
 					if ( naturalIdValue != null ) {
 						final Type type = getPropertyTypes()[idPosition];
 						type.nullSafeSet( ps, naturalIdValue, positions, session );
 						positions += type.getColumnSpan( session.getFactory() );
 					}
 				}
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					// if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 
 					final Object hydratedId = getIdentifierType().hydrate( rs, getIdentifierAliases(), session, null );
 					return (Serializable) getIdentifierType().resolve( hydratedId, session, null );
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					String.format(
 							"could not resolve natural-id [%s] to id : %s",
 							naturalIdValues,
 							MessageHelper.infoString( this )
 					),
 					sqlEntityIdByNaturalIdString
 			);
 		}
 	}
 
 	private boolean[] determineValueNullness(Object[] naturalIdValues) {
 		boolean[] nullness = new boolean[ naturalIdValues.length ];
 		for ( int i = 0; i < naturalIdValues.length; i++ ) {
 			nullness[i] = naturalIdValues[i] == null;
 		}
 		return nullness;
 	}
 
 	private Boolean naturalIdIsNonNullable;
 	private String cachedPkByNonNullableNaturalIdQuery;
 
 	private String determinePkByNaturalIdQuery(boolean[] valueNullness) {
 		if ( ! hasNaturalIdentifier() ) {
 			throw new HibernateException( "Attempt to build natural-id -> PK resolution query for entity that does not define natural id" );
 		}
 
 		// performance shortcut for cases where the natural-id is defined as completely non-nullable
 		if ( isNaturalIdNonNullable() ) {
 			if ( valueNullness != null && ! ArrayHelper.isAllFalse( valueNullness ) ) {
 				throw new HibernateException( "Null value(s) passed to lookup by non-nullable natural-id" );
 			}
 			if ( cachedPkByNonNullableNaturalIdQuery == null ) {
 				cachedPkByNonNullableNaturalIdQuery = generateEntityIdByNaturalIdSql( null );
 			}
 			return cachedPkByNonNullableNaturalIdQuery;
 		}
 
 		// Otherwise, regenerate it each time
 		return generateEntityIdByNaturalIdSql( valueNullness );
 	}
 
 	protected boolean isNaturalIdNonNullable() {
 		if ( naturalIdIsNonNullable == null ) {
 			naturalIdIsNonNullable = determineNaturalIdNullability();
 		}
 		return naturalIdIsNonNullable;
 	}
 
 	private boolean determineNaturalIdNullability() {
 		boolean[] nullability = getPropertyNullability();
 		for ( int position : getNaturalIdentifierProperties() ) {
 			// if any individual property is nullable, return false
 			if ( nullability[position] ) {
 				return false;
 			}
 		}
 		// return true if we found no individually nullable properties
 		return true;
 	}
 
 	private String generateEntityIdByNaturalIdSql(boolean[] valueNullness) {
 		EntityPersister rootPersister = getFactory().getEntityPersister( getRootEntityName() );
 		if ( rootPersister != this ) {
 			if ( rootPersister instanceof AbstractEntityPersister ) {
 				return ( (AbstractEntityPersister) rootPersister ).generateEntityIdByNaturalIdSql( valueNullness );
 			}
 		}
 
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id->entity-id state " + getEntityName() );
 		}
 
 		final String rootAlias = getRootAlias();
 
 		select.setSelectClause( identifierSelectFragment( rootAlias, "" ) );
 		select.setFromClause( fromTableFragment( rootAlias ) + fromJoinFragment( rootAlias, true, false ) );
 
 		final StringBuilder whereClause = new StringBuilder();
 		final int[] propertyTableNumbers = getPropertyTableNumbers();
 		final int[] naturalIdPropertyIndexes = this.getNaturalIdentifierProperties();
 		int valuesIndex = -1;
 		for ( int propIdx = 0; propIdx < naturalIdPropertyIndexes.length; propIdx++ ) {
 			valuesIndex++;
 			if ( propIdx > 0 ) {
 				whereClause.append( " and " );
 			}
 
 			final int naturalIdIdx = naturalIdPropertyIndexes[propIdx];
 			final String tableAlias = generateTableAlias( rootAlias, propertyTableNumbers[naturalIdIdx] );
 			final String[] propertyColumnNames = getPropertyColumnNames( naturalIdIdx );
 			final String[] aliasedPropertyColumns = StringHelper.qualify( tableAlias, propertyColumnNames );
 
 			if ( valueNullness != null && valueNullness[valuesIndex] ) {
 				whereClause.append( StringHelper.join( " is null and ", aliasedPropertyColumns ) ).append( " is null" );
 			}
 			else {
 				whereClause.append( StringHelper.join( "=? and ", aliasedPropertyColumns ) ).append( "=?" );
 			}
 		}
 
 		whereClause.append( whereJoinFragment( getRootAlias(), true, false ) );
 
 		return select.setOuterJoins( "", "" ).setWhereClause( whereClause.toString() ).toStatementString();
 	}
 
 	protected String concretePropertySelectFragmentSansLeadingComma(String alias, boolean[] include) {
 		String concretePropertySelectFragment = concretePropertySelectFragment( alias, include );
 		int firstComma = concretePropertySelectFragment.indexOf( ", " );
 		if ( firstComma == 0 ) {
 			concretePropertySelectFragment = concretePropertySelectFragment.substring( 2 );
 		}
 		return concretePropertySelectFragment;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return entityMetamodel.hasNaturalIdentifier();
 	}
 
 	public void setPropertyValue(Object object, String propertyName, Object value) {
 		getEntityTuplizer().setPropertyValue( object, propertyName, value );
 	}
 	
 	public static int getTableId(String tableName, String[] tables) {
 		for ( int j = 0; j < tables.length; j++ ) {
 			if ( tableName.equalsIgnoreCase( tables[j] ) ) {
 				return j;
 			}
 		}
 		throw new AssertionFailure( "Table " + tableName + " not found" );
 	}
 	
 	@Override
 	public EntityMode getEntityMode() {
 		return entityMetamodel.getEntityMode();
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return entityTuplizer;
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return entityMetamodel.getInstrumentationMetadata();
 	}
 
 	@Override
 	public String getTableAliasForColumn(String columnName, String rootAlias) {
 		return generateTableAlias( rootAlias, determineTableNumberForColumn( columnName ) );
 	}
 
 	public int determineTableNumberForColumn(String columnName) {
 		return 0;
 	}
 
 	@Override
 	public EntityEntryFactory getEntityEntryFactory() {
-		// todo : in ORM terms this should check #isMutable() and return an appropriate one.
-		return DefaultEntityEntryFactory.INSTANCE;
+		return this.entityEntryFactory;
 	}
 
 	/**
 	 * Consolidated these onto a single helper because the 2 pieces work in tandem.
 	 */
 	public static interface CacheEntryHelper {
 		public CacheEntryStructure getCacheEntryStructure();
 
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session);
 	}
 
 	private static class StandardCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private StandardCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class ReferenceCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private ReferenceCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new ReferenceCacheEntryImpl( entity, persister );
 		}
 	}
 
 	private static class StructuredCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 		private final StructuredCacheEntry structure;
 
 		private StructuredCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 			this.structure = new StructuredCacheEntry( persister );
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return structure;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class NoopCacheEntryHelper implements CacheEntryHelper {
 		public static final NoopCacheEntryHelper INSTANCE = new NoopCacheEntryHelper();
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			throw new HibernateException( "Illegal attempt to build cache entry for non-cached entity" );
 		}
 	}
 
 
 	// EntityDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private EntityIdentifierDefinition entityIdentifierDefinition;
 	private Iterable<AttributeDefinition> embeddedCompositeIdentifierAttributes;
 	private Iterable<AttributeDefinition> attributeDefinitions;
 
 	@Override
 	public void generateEntityDefinition() {
 		prepareEntityIdentifierDefinition();
 		collectAttributeDefinitions();
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return this;
 	}
 
 	@Override
 	public EntityIdentifierDefinition getEntityKeyDefinition() {
 		return entityIdentifierDefinition;
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		return attributeDefinitions;
 	}
 
 
 	private void prepareEntityIdentifierDefinition() {
 		if ( entityIdentifierDefinition != null ) {
 			return;
 		}
 		final Type idType = getIdentifierType();
 
 		if ( !idType.isComponentType() ) {
 			entityIdentifierDefinition =
 					EntityIdentifierDefinitionHelper.buildSimpleEncapsulatedIdentifierDefinition( this );
 			return;
 		}
 
 		final CompositeType cidType = (CompositeType) idType;
 		if ( !cidType.isEmbedded() ) {
 			entityIdentifierDefinition =
 					EntityIdentifierDefinitionHelper.buildEncapsulatedCompositeIdentifierDefinition( this );
 			return;
 		}
 
 		entityIdentifierDefinition =
 				EntityIdentifierDefinitionHelper.buildNonEncapsulatedCompositeIdentifierDefinition( this );
 	}
 
 	private void collectAttributeDefinitions(
 			Map<String,AttributeDefinition> attributeDefinitionsByName,
 			EntityMetamodel metamodel) {
 		for ( int i = 0; i < metamodel.getPropertySpan(); i++ ) {
 			final AttributeDefinition attributeDefinition = metamodel.getProperties()[i];
 			// Don't replace an attribute definition if it is already in attributeDefinitionsByName
 			// because the new value will be from a subclass.
 			final AttributeDefinition oldAttributeDefinition = attributeDefinitionsByName.get(
 					attributeDefinition.getName()
 			);
 			if ( oldAttributeDefinition != null ) {
 				if ( LOG.isTraceEnabled() ) {
 						LOG.tracef(
 								"Ignoring subclass attribute definition [%s.%s] because it is defined in a superclass ",
 								entityMetamodel.getName(),
 								attributeDefinition.getName()
 						);
 				}
 			}
 			else {
 				attributeDefinitionsByName.put( attributeDefinition.getName(), attributeDefinition );
 			}
 		}
 
 		// see if there are any subclass persisters...
 		final Set<String> subClassEntityNames = metamodel.getSubclassEntityNames();
 		if ( subClassEntityNames == null ) {
 			return;
 		}
 
 		// see if we can find the persisters...
 		for ( String subClassEntityName : subClassEntityNames ) {
 			if ( metamodel.getName().equals( subClassEntityName ) ) {
 				// skip it
 				continue;
 			}
 			try {
 				final EntityPersister subClassEntityPersister = factory.getEntityPersister( subClassEntityName );
 				collectAttributeDefinitions( attributeDefinitionsByName, subClassEntityPersister.getEntityMetamodel() );
 			}
 			catch (MappingException e) {
 				throw new IllegalStateException(
 						String.format(
 								"Could not locate subclass EntityPersister [%s] while processing EntityPersister [%s]",
 								subClassEntityName,
 								metamodel.getName()
 						),
 						e
 				);
 			}
 		}
 	}
 
 	private void collectAttributeDefinitions() {
 		// todo : I think this works purely based on luck atm
 		// 		specifically in terms of the sub/super class entity persister(s) being available.  Bit of chicken-egg
 		// 		problem there:
 		//			* If I do this during postConstruct (as it is now), it works as long as the
 		//			super entity persister is already registered, but I don't think that is necessarily true.
 		//			* If I do this during postInstantiate then lots of stuff in postConstruct breaks if we want
 		//			to try and drive SQL generation on these (which we do ultimately).  A possible solution there
 		//			would be to delay all SQL generation until postInstantiate
 
 		Map<String,AttributeDefinition> attributeDefinitionsByName = new LinkedHashMap<String,AttributeDefinition>();
 		collectAttributeDefinitions( attributeDefinitionsByName, getEntityMetamodel() );
 
 
 //		EntityMetamodel currentEntityMetamodel = this.getEntityMetamodel();
 //		while ( currentEntityMetamodel != null ) {
 //			for ( int i = 0; i < currentEntityMetamodel.getPropertySpan(); i++ ) {
 //				attributeDefinitions.add( currentEntityMetamodel.getProperties()[i] );
 //			}
 //			// see if there is a super class EntityMetamodel
 //			final String superEntityName = currentEntityMetamodel.getSuperclass();
 //			if ( superEntityName != null ) {
 //				currentEntityMetamodel = factory.getEntityPersister( superEntityName ).getEntityMetamodel();
 //			}
 //			else {
 //				currentEntityMetamodel = null;
 //			}
 //		}
 
 		this.attributeDefinitions = Collections.unmodifiableList(
 				new ArrayList<AttributeDefinition>( attributeDefinitionsByName.values() )
 		);
 //		// todo : leverage the attribute definitions housed on EntityMetamodel
 //		// 		for that to work, we'd have to be able to walk our super entity persister(s)
 //		this.attributeDefinitions = new Iterable<AttributeDefinition>() {
 //			@Override
 //			public Iterator<AttributeDefinition> iterator() {
 //				return new Iterator<AttributeDefinition>() {
 ////					private final int numberOfAttributes = countSubclassProperties();
 ////					private final int numberOfAttributes = entityMetamodel.getPropertySpan();
 //
 //					EntityMetamodel currentEntityMetamodel = entityMetamodel;
 //					int numberOfAttributesInCurrentEntityMetamodel = currentEntityMetamodel.getPropertySpan();
 //
 //					private int currentAttributeNumber;
 //
 //					@Override
 //					public boolean hasNext() {
 //						return currentEntityMetamodel != null
 //								&& currentAttributeNumber < numberOfAttributesInCurrentEntityMetamodel;
 //					}
 //
 //					@Override
 //					public AttributeDefinition next() {
 //						final int attributeNumber = currentAttributeNumber;
 //						currentAttributeNumber++;
 //						final AttributeDefinition next = currentEntityMetamodel.getProperties()[ attributeNumber ];
 //
 //						if ( currentAttributeNumber >= numberOfAttributesInCurrentEntityMetamodel ) {
 //							// see if there is a super class EntityMetamodel
 //							final String superEntityName = currentEntityMetamodel.getSuperclass();
 //							if ( superEntityName != null ) {
 //								currentEntityMetamodel = factory.getEntityPersister( superEntityName ).getEntityMetamodel();
 //								if ( currentEntityMetamodel != null ) {
 //									numberOfAttributesInCurrentEntityMetamodel = currentEntityMetamodel.getPropertySpan();
 //									currentAttributeNumber = 0;
 //								}
 //							}
 //						}
 //
 //						return next;
 //					}
 //
 //					@Override
 //					public void remove() {
 //						throw new UnsupportedOperationException( "Remove operation not supported here" );
 //					}
 //				};
 //			}
 //		};
 	}
 
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTestUtils.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTestUtils.java
index 2af44a90ad..a729ed3b83 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTestUtils.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTestUtils.java
@@ -1,328 +1,328 @@
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
 package org.hibernate.test.bytecode.enhancement;
 
 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.io.IOException;
 import java.lang.reflect.InvocationTargetException;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Set;
 import javax.tools.JavaCompiler;
 import javax.tools.JavaFileObject;
 import javax.tools.StandardJavaFileManager;
 import javax.tools.StandardLocation;
 import javax.tools.ToolProvider;
 
 import javassist.ClassPool;
 import javassist.CtClass;
 import javassist.LoaderClassPath;
 
 import org.hibernate.LockMode;
 import org.hibernate.bytecode.enhance.spi.DefaultEnhancementContext;
 import org.hibernate.bytecode.enhance.spi.EnhancementContext;
 import org.hibernate.bytecode.enhance.spi.Enhancer;
 import org.hibernate.bytecode.enhance.spi.EnhancerConstants;
-import org.hibernate.engine.internal.DefaultEntityEntryFactory;
+import org.hibernate.engine.internal.MutableEntityEntryFactory;
 import org.hibernate.engine.spi.CompositeOwner;
 import org.hibernate.engine.spi.CompositeTracker;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.ManagedEntity;
 import org.hibernate.engine.spi.PersistentAttributeInterceptor;
 import org.hibernate.engine.spi.SelfDirtinessTracker;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import com.sun.tools.classfile.ConstantPoolException;
 import com.sun.tools.javap.JavapTask;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * utility class to use in bytecode enhancement tests
  *
  * @author Steve Ebersole
  */
 public abstract class EnhancerTestUtils extends BaseUnitTestCase {
 
 	private static EnhancementContext enhancementContext = new DefaultEnhancementContext();
 
     private static String workingDir = System.getProperty("java.io.tmpdir");
 
     private static final CoreMessageLogger log = CoreLogging.messageLogger(EnhancerTestUtils.class);
 
     /**
      * method that performs the enhancement of a class
      * also checks the signature of enhanced entities methods using 'javap' decompiler
      */
     static Class<?> enhanceAndDecompile(Class<?> classToEnhance, ClassLoader cl) throws Exception {
         CtClass entityCtClass = generateCtClassForAnEntity(classToEnhance);
 
         byte[] original = entityCtClass.toBytecode();
         byte[] enhanced = new Enhancer(enhancementContext).enhance(entityCtClass.getName(), original);
         assertFalse("entity was not enhanced", Arrays.equals(original, enhanced));
         log.infof("enhanced entity [%s]", entityCtClass.getName());
 
         ClassPool cp = new ClassPool(false);
         cp.appendClassPath(new LoaderClassPath(cl));
         CtClass enhancedCtClass = cp.makeClass(new ByteArrayInputStream(enhanced));
 
         enhancedCtClass.debugWriteFile(workingDir);
         decompileDumpedClass(classToEnhance.getName());
 
         Class<?> enhancedClass = enhancedCtClass.toClass(cl, EnhancerTestUtils.class.getProtectionDomain());
         assertNotNull(enhancedClass);
         return enhancedClass;
     }
 
     private static void decompileDumpedClass(String className) {
         try {
             JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
             StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
             fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(new File(workingDir)));
 
             JavapTask javapTask = new JavapTask();
             for (JavaFileObject jfo : fileManager.getJavaFileObjects(workingDir + File.separator + getFilenameForClassName(className))) {
                 try {
                     Set<String> interfaceNames = new HashSet<String>();
                     Set<String> fieldNames = new HashSet<String>();
                     Set<String> methodNames = new HashSet<String>();
 
                     JavapTask.ClassFileInfo info = javapTask.read(jfo);
 
                     log.infof("decompiled class [%s]", info.cf.getName());
 
                     for (int i : info.cf.interfaces) {
                         interfaceNames.add(info.cf.constant_pool.getClassInfo(i).getName());
                         log.debugf("declared iFace  = ", info.cf.constant_pool.getClassInfo(i).getName());
                     }
                     for (com.sun.tools.classfile.Field f : info.cf.fields) {
                         fieldNames.add(f.getName(info.cf.constant_pool));
                         log.debugf("declared field  = ", f.getName(info.cf.constant_pool));
                     }
                     for (com.sun.tools.classfile.Method m : info.cf.methods) {
                         methodNames.add(m.getName(info.cf.constant_pool));
                         log.debugf("declared method = ", m.getName(info.cf.constant_pool));
                     }
 
                     // checks signature against known interfaces
                     if (interfaceNames.contains(PersistentAttributeInterceptor.class.getName())) {
                         assertTrue(fieldNames.contains(EnhancerConstants.INTERCEPTOR_FIELD_NAME));
                         assertTrue(methodNames.contains(EnhancerConstants.INTERCEPTOR_GETTER_NAME));
                         assertTrue(methodNames.contains(EnhancerConstants.INTERCEPTOR_SETTER_NAME));
                     }
                     if (interfaceNames.contains(ManagedEntity.class.getName())) {
                         assertTrue(methodNames.contains(EnhancerConstants.ENTITY_INSTANCE_GETTER_NAME));
 
                         assertTrue(fieldNames.contains(EnhancerConstants.ENTITY_ENTRY_FIELD_NAME));
                         assertTrue(methodNames.contains(EnhancerConstants.ENTITY_ENTRY_GETTER_NAME));
                         assertTrue(methodNames.contains(EnhancerConstants.ENTITY_ENTRY_SETTER_NAME));
 
                         assertTrue(fieldNames.contains(EnhancerConstants.PREVIOUS_FIELD_NAME));
                         assertTrue(methodNames.contains(EnhancerConstants.PREVIOUS_GETTER_NAME));
                         assertTrue(methodNames.contains(EnhancerConstants.PREVIOUS_SETTER_NAME));
 
                         assertTrue(fieldNames.contains(EnhancerConstants.NEXT_FIELD_NAME));
                         assertTrue(methodNames.contains(EnhancerConstants.NEXT_GETTER_NAME));
                         assertTrue(methodNames.contains(EnhancerConstants.NEXT_SETTER_NAME));
                     }
                     if (interfaceNames.contains(SelfDirtinessTracker.class.getName())) {
                         assertTrue(fieldNames.contains(EnhancerConstants.TRACKER_FIELD_NAME));
                         assertTrue(methodNames.contains(EnhancerConstants.TRACKER_GET_NAME));
                         assertTrue(methodNames.contains(EnhancerConstants.TRACKER_CLEAR_NAME));
                         assertTrue(methodNames.contains(EnhancerConstants.TRACKER_HAS_CHANGED_NAME));
                     }
                     if (interfaceNames.contains(CompositeTracker.class.getName())) {
                         assertTrue(fieldNames.contains(EnhancerConstants.TRACKER_COMPOSITE_FIELD_NAME));
                         assertTrue(methodNames.contains(EnhancerConstants.TRACKER_COMPOSITE_SET_OWNER));
                         assertTrue(methodNames.contains(EnhancerConstants.TRACKER_COMPOSITE_SET_OWNER));
                     }
                     if (interfaceNames.contains(CompositeOwner.class.getName())) {
                         assertTrue(fieldNames.contains(EnhancerConstants.TRACKER_CHANGER_NAME));
                         assertTrue(methodNames.contains(EnhancerConstants.TRACKER_CHANGER_NAME));
                     }
                 } catch (ConstantPoolException e) {
                     e.printStackTrace();
                 }
             }
         } catch (IOException ioe) {
             assertNull("Failed to open class file", ioe);
         } catch (RuntimeException re) {
             log.warnf(re, "WARNING: UNABLE DECOMPILE DUE TO %s", re.getMessage());
         }
     }
 
     private static CtClass generateCtClassForAnEntity(Class<?> entityClassToEnhance) throws Exception {
         ClassPool cp = new ClassPool(false);
         return cp.makeClass(EnhancerTestUtils.class.getClassLoader().getResourceAsStream(getFilenameForClassName(entityClassToEnhance.getName())));
     }
 
     private static String getFilenameForClassName(String className) {
         return className.replace('.', File.separatorChar) + JavaFileObject.Kind.CLASS.extension;
     }
 
     /**
      * clears the dirty set for an entity
      */
     public static void clearDirtyTracking (Object entityInstance) {
         try {
             entityInstance.getClass().getMethod(EnhancerConstants.TRACKER_CLEAR_NAME).invoke(entityInstance);
             checkDirtyTracking(entityInstance);
         } catch (InvocationTargetException e) {
             assertNull("Exception in clear dirty tracking", e);
         } catch (NoSuchMethodException e) {
             assertNull("Exception in clear dirty tracking", e);
         } catch (IllegalAccessException e) {
             assertNull("Exception in clear  dirty tracking", e);
         }
     }
 
     /**
      * compares the dirty fields of an entity with a set of expected values
      */
     public static void checkDirtyTracking (Object entityInstance, String ... dirtyFields) {
         try {
             assertTrue((dirtyFields.length == 0) != (Boolean) entityInstance.getClass().getMethod(EnhancerConstants.TRACKER_HAS_CHANGED_NAME).invoke(entityInstance));
             Set<?> tracked = (Set<?>) entityInstance.getClass().getMethod(EnhancerConstants.TRACKER_GET_NAME).invoke(entityInstance);
             assertEquals(dirtyFields.length, tracked.size());
             assertTrue(tracked.containsAll(Arrays.asList(dirtyFields)));
         } catch (InvocationTargetException e) {
             assertNull("Exception while checking dirty tracking", e);
         } catch (NoSuchMethodException e) {
             assertNull("Exception while checking dirty tracking", e);
         } catch (IllegalAccessException e) {
             assertNull("Exception while checking dirty tracking", e);
         }
     }
 
     static EntityEntry makeEntityEntry() {
-        return DefaultEntityEntryFactory.INSTANCE.createEntityEntry(
+        return MutableEntityEntryFactory.INSTANCE.createEntityEntry(
                 Status.MANAGED,
                 null,
                 null,
                 1,
                 null,
                 LockMode.NONE,
                 false,
                 null,
                 false,
                 false,
                 null
         );
     }
 
     public static class LocalPersistentAttributeInterceptor implements PersistentAttributeInterceptor {
 
         @Override public boolean readBoolean(Object obj, String name, boolean oldValue) {
             log.infof( "Reading boolean [%s]" , name );
             return oldValue;
         }
         @Override public boolean writeBoolean(Object obj, String name, boolean oldValue, boolean newValue) {
             log.infof( "Writing boolean []", name );
             return newValue;
         }
 
         @Override public byte readByte(Object obj, String name, byte oldValue) {
             log.infof( "Reading byte [%s]", name );
             return oldValue;
         }
         @Override public byte writeByte(Object obj, String name, byte oldValue, byte newValue) {
             log.infof( "Writing byte [%s]", name );
             return newValue;
         }
 
         @Override public char readChar(Object obj, String name, char oldValue) {
             log.infof( "Reading char [%s]", name );
             return oldValue;
         }
         @Override public char writeChar(Object obj, String name, char oldValue, char newValue) {
             log.infof( "Writing char [%s]", name );
             return newValue;
         }
 
         @Override public short readShort(Object obj, String name, short oldValue) {
             log.infof( "Reading short [%s]", name );
             return oldValue;
         }
         @Override public short writeShort(Object obj, String name, short oldValue, short newValue) {
             log.infof( "Writing short [%s]", name );
             return newValue;
         }
 
         @Override public int readInt(Object obj, String name, int oldValue) {
             log.infof( "Reading int [%s]", name );
             return oldValue;
         }
         @Override public int writeInt(Object obj, String name, int oldValue, int newValue) {
             log.infof( "Writing int [%s]", name );
             return newValue;
         }
 
         @Override public float readFloat(Object obj, String name, float oldValue) {
             log.infof( "Reading float [%s]", name );
             return oldValue;
         }
         @Override public float writeFloat(Object obj, String name, float oldValue, float newValue) {
             log.infof( "Writing float [%s]", name );
             return newValue;
         }
 
         @Override public double readDouble(Object obj, String name, double oldValue) {
             log.infof( "Reading double [%s]", name );
             return oldValue;
         }
         @Override public double writeDouble(Object obj, String name, double oldValue, double newValue) {
             log.infof( "Writing double [%s]", name );
             return newValue;
         }
 
         @Override public long readLong(Object obj, String name, long oldValue) {
             log.infof( "Reading long [%s]", name );
             return oldValue;
         }
         @Override public long writeLong(Object obj, String name, long oldValue, long newValue) {
             log.infof( "Writing long [%s]", name );
             return newValue;
         }
 
         @Override public Object readObject(Object obj, String name, Object oldValue) {
             log.infof( "Reading Object [%s]", name );
             return oldValue;
         }
         @Override public Object writeObject(Object obj, String name, Object oldValue, Object newValue) {
             log.infof( "Writing Object [%s]", name );
             return newValue;
         }
     }
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cache/ByteCodeEnhancedImmutableReferenceCacheTest.java b/hibernate-core/src/test/java/org/hibernate/test/cache/ByteCodeEnhancedImmutableReferenceCacheTest.java
new file mode 100644
index 0000000000..ff5bb8c697
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/cache/ByteCodeEnhancedImmutableReferenceCacheTest.java
@@ -0,0 +1,199 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.cache;
+
+import org.hibernate.Session;
+import org.hibernate.annotations.Cache;
+import org.hibernate.annotations.CacheConcurrencyStrategy;
+import org.hibernate.annotations.Immutable;
+import org.hibernate.annotations.Proxy;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.ManagedEntity;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.junit.Test;
+
+import javax.persistence.Cacheable;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.Transient;
+
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertNotNull;
+import static org.junit.Assert.assertTrue;
+
+/**
+ * @author John O'Hara
+ */
+public class ByteCodeEnhancedImmutableReferenceCacheTest extends BaseCoreFunctionalTestCase {
+	@Override
+	protected void configure(Configuration configuration) {
+		super.configure( configuration );
+		configuration.setProperty( AvailableSettings.USE_DIRECT_REFERENCE_CACHE_ENTRIES, "true" );
+		configuration.setProperty( AvailableSettings.USE_QUERY_CACHE, "true" );
+	}
+
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[]{MyEnhancedReferenceData.class};
+	}
+
+	@Test
+	public void testUseOfDirectReferencesInCache() throws Exception {
+		EntityPersister persister = (EntityPersister) sessionFactory().getClassMetadata( MyEnhancedReferenceData.class );
+		assertFalse( persister.isMutable() );
+		assertTrue( persister.buildCacheEntry( null, null, null, null ).isReferenceEntry() );
+		assertFalse( persister.hasProxy() );
+
+		final MyEnhancedReferenceData myReferenceData = new MyEnhancedReferenceData( 1, "first item", "abc" );
+
+		// save a reference in one session
+		Session s = openSession();
+		s.beginTransaction();
+		s.save( myReferenceData );
+		s.getTransaction().commit();
+		s.close();
+
+		assertNotNull( myReferenceData.$$_hibernate_getEntityEntry() );
+
+		// now load it in another
+		s = openSession();
+		s.beginTransaction();
+		//		MyEnhancedReferenceData loaded = (MyEnhancedReferenceData) s.get( MyEnhancedReferenceData.class, 1 );
+		MyEnhancedReferenceData loaded = (MyEnhancedReferenceData) s.load( MyEnhancedReferenceData.class, 1 );
+		s.getTransaction().commit();
+		s.close();
+
+		// the 2 instances should be the same (==)
+		assertTrue( "The two instances were different references", myReferenceData == loaded );
+
+		// now try query caching
+		s = openSession();
+		s.beginTransaction();
+		MyEnhancedReferenceData queried = (MyEnhancedReferenceData) s.createQuery( "from MyEnhancedReferenceData" ).setCacheable( true ).list().get( 0 );
+		s.getTransaction().commit();
+		s.close();
+
+		// the 2 instances should be the same (==)
+		assertTrue( "The two instances were different references", myReferenceData == queried );
+
+		// cleanup
+		s = openSession();
+		s.beginTransaction();
+		s.delete( myReferenceData );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Entity(name = "MyEnhancedReferenceData")
+	@Immutable
+	@Cacheable
+	@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
+	@Proxy(lazy = false)
+	@SuppressWarnings("UnusedDeclaration")
+	public static class MyEnhancedReferenceData implements ManagedEntity {
+		@Id
+		private Integer id;
+		private String name;
+		private String theValue;
+
+		@Transient
+		private transient EntityEntry entityEntry;
+		@Transient
+		private transient ManagedEntity previous;
+		@Transient
+		private transient ManagedEntity next;
+
+		public MyEnhancedReferenceData(Integer id, String name, String theValue) {
+			this.id = id;
+			this.name = name;
+			this.theValue = theValue;
+		}
+
+		protected MyEnhancedReferenceData() {
+		}
+
+		public Integer getId() {
+			return id;
+		}
+
+		public void setId(Integer id) {
+			this.id = id;
+		}
+
+		public String getName() {
+			return name;
+		}
+
+		public void setName(String name) {
+			this.name = name;
+		}
+
+		public String getTheValue() {
+			return theValue;
+		}
+
+		public void setTheValue(String theValue) {
+			this.theValue = theValue;
+		}
+
+		@Override
+		public Object $$_hibernate_getEntityInstance() {
+			return this;
+		}
+
+		@Override
+		public EntityEntry $$_hibernate_getEntityEntry() {
+			return entityEntry;
+		}
+
+		@Override
+		public void $$_hibernate_setEntityEntry(EntityEntry entityEntry) {
+			this.entityEntry = entityEntry;
+		}
+
+		@Override
+		public ManagedEntity $$_hibernate_getNextManagedEntity() {
+			return next;
+		}
+
+		@Override
+		public void $$_hibernate_setNextManagedEntity(ManagedEntity next) {
+			this.next = next;
+		}
+
+		@Override
+		public ManagedEntity $$_hibernate_getPreviousManagedEntity() {
+			return previous;
+		}
+
+		@Override
+		public void $$_hibernate_setPreviousManagedEntity(ManagedEntity previous) {
+			this.previous = previous;
+		}
+
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index 32ffa34fe8..3d53c7e86d 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
@@ -1,873 +1,873 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * JBoss, Home of Professional Open Source
  * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.test.cfg.persister;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Comparator;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.collection.spi.PersistentCollection;
-import org.hibernate.engine.internal.DefaultEntityEntryFactory;
+import org.hibernate.engine.internal.MutableEntityEntryFactory;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class GoofyPersisterClassProvider implements PersisterClassResolver {
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	public static class NoopEntityPersister implements EntityPersister {
 
 		public NoopEntityPersister(
 				final PersistentClass persistentClass,
 				final EntityRegionAccessStrategy cacheAccessStrategy,
 				final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 				final PersisterCreationContext creationContext) {
 			throw new GoofyException(NoopEntityPersister.class);
 		}
 
 		@Override
 		public EntityMode getEntityMode() {
 			return null;
 		}
 
 		@Override
 		public EntityTuplizer getEntityTuplizer() {
 			return null;
 		}
 
 		@Override
 		public EntityInstrumentationMetadata getInstrumentationMetadata() {
 			return new NonPojoInstrumentationMetadata( null );
 		}
 
 		@Override
 		public void generateEntityDefinition() {
 		}
 
 		@Override
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		@Override
 		public SessionFactoryImplementor getFactory() {
 			return null;
 		}
 
 		@Override
 		public EntityEntryFactory getEntityEntryFactory() {
-			return DefaultEntityEntryFactory.INSTANCE;
+			return MutableEntityEntryFactory.INSTANCE;
 		}
 
 		@Override
 		public String getRootEntityName() {
 			return null;
 		}
 
 		@Override
 		public String getEntityName() {
 			return null;
 		}
 
 		@Override
 		public EntityMetamodel getEntityMetamodel() {
 			return null;
 		}
 
 		@Override
 		public boolean isSubclassEntityName(String entityName) {
 			return false;
 		}
 
 		@Override
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public boolean hasProxy() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasMutableProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasSubselectLoadableCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCascades() {
 			return false;
 		}
 
 		@Override
 		public boolean isMutable() {
 			return false;
 		}
 
 		@Override
 		public boolean isInherited() {
 			return false;
 		}
 
 		@Override
 		public boolean isIdentifierAssignedByInsert() {
 			return false;
 		}
 
 		@Override
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;
 		}
 
 		@Override
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean hasIdentifierProperty() {
 			return false;
 		}
 
 		@Override
 		public boolean canExtractIdOutOfEntity() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersioned() {
 			return false;
 		}
 
 		@Override
 		public Comparator getVersionComparator() {
 			return null;
 		}
 
 		@Override
 		public VersionType getVersionType() {
 			return null;
 		}
 
 		@Override
 		public int getVersionProperty() {
 			return 0;
 		}
 
 		@Override
 		public boolean hasNaturalIdentifier() {
 			return false;
 		}
 
 		@Override
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];
 		}
 
 		@Override
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 				SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean hasLazyProperties() {
 			return false;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
 		}
 
 		@Override
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
 		}
 
 		@Override
 		public Type[] getPropertyTypes() {
 			return new Type[0];
 		}
 
 		@Override
 		public String[] getPropertyNames() {
 			return new String[0];
 		}
 
 		@Override
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];
 		}
 
 		@Override
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];
 		}
 
 		@Override
 		public Type getIdentifierType() {
 			return null;
 		}
 
 		@Override
 		public String getIdentifierPropertyName() {
 			return null;
 		}
 
 		@Override
 		public boolean isCacheInvalidationRequired() {
 			return false;
 		}
 
 		@Override
 		public boolean isLazyPropertiesCacheable() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCache() {
 			return false;
 		}
 
 		@Override
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;
 		}
 		
 		@Override
 		public boolean hasNaturalIdCache() {
 			return false;
 		}
 
 		@Override
 		public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 			return null;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(
 				Object entity, Object[] state, Object version, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public ClassMetadata getClassMetadata() {
 			return null;
 		}
 
 		@Override
 		public boolean isBatchLoadable() {
 			return false;
 		}
 
 		@Override
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;
 		}
 
 		@Override
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 			throw new UnsupportedOperationException( "not supported" );
 		}
 
 		@Override
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return false;
 		}
 
 		@Override
 		public boolean hasInsertGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasUpdateGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersionPropertyGenerated() {
 			return false;
 		}
 
 		@Override
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		}
 
 		@Override
 		public void afterReassociate(Object entity, SessionImplementor session) {
 		}
 
 		@Override
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public Class getMappedClass() {
 			return null;
 		}
 
 		@Override
 		public boolean implementsLifecycle() {
 			return false;
 		}
 
 		@Override
 		public Class getConcreteProxyClass() {
 			return null;
 		}
 
 		@Override
 		public void setPropertyValues(Object object, Object[] values) {
 		}
 
 		@Override
 		public void setPropertyValue(Object object, int i, Object value) {
 		}
 
 		@Override
 		public Object[] getPropertyValues(Object object) {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, int i) {
 			return null;
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, String propertyName) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object object) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		}
 
 		@Override
 		public Object getVersion(Object object) {
 			return null;
 		}
 
 		@Override
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstance(Object object) {
 			return false;
 		}
 
 		@Override
 		public boolean hasUninitializedLazyProperties(Object object) {
 			return false;
 		}
 
 		@Override
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		}
 
 		@Override
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 			return null;
 		}
 
 		@Override
 		public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 			// TODO Auto-generated method stub
 			return null;
 		}
 
 		@Override
 		public EntityPersister getEntityPersister() {
 			return this;
 		}
 
 		@Override
 		public EntityIdentifierDefinition getEntityKeyDefinition() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public Iterable<AttributeDefinition> getAttributes() {
 			throw new NotYetImplementedException();
 		}
 
         @Override
         public int[] resolveAttributeIndexes(Set<String> attributes) {
             return null;
         }
 
 		@Override
 		public boolean canUseReferenceCacheEntries() {
 			return false;
 		}
 	}
 
 	public static class NoopCollectionPersister implements CollectionPersister {
 
 		public NoopCollectionPersister(
 				Collection collectionBinding,
 				CollectionRegionAccessStrategy cacheAccessStrategy,
 				PersisterCreationContext creationContext) {
 			throw new GoofyException(NoopCollectionPersister.class);
 		}
 
 		public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public CollectionPersister getCollectionPersister() {
 			return this;
 		}
 
 		public CollectionType getCollectionType() {
 			throw new NotYetImplementedException();
 		}
 
 		@Override
 		public CollectionIndexDefinition getIndexDefinition() {
 			throw new NotYetImplementedException();
 		}
 
 		@Override
 		public CollectionElementDefinition getElementDefinition() {
 			throw new NotYetImplementedException();
 		}
 
 		public Type getKeyType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIndexType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getElementType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getElementClass() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readElement(ResultSet rs, Object owner, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIdentifier(ResultSet rs, String columnAlias, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isPrimitiveArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isOneToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isManyToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIndex() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInverse() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void recreate(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void deleteRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void updateRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insertRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRole() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getOwnerEntityPersister() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrphanDelete() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasManyToManyOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getCollectionSpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionMetadata getCollectionMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCascadeDeleteEnabled() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getElementNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIndexNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void postInstantiate() throws MappingException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getKeyColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getIndexColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getElementColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierColumnAlias(String suffix) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isExtraLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getSize(Serializable key, SessionImplementor session) {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public int getBatchSize() {
 			return 0;
 		}
 
 		@Override
 		public String getMappedByProperty() {
 			return null;
 		}
 
 		@Override
 		public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
index e226f29636..2071b638e7 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
@@ -1,714 +1,714 @@
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Hashtable;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.cfg.NotYetImplementedException;
-import org.hibernate.engine.internal.DefaultEntityEntryFactory;
+import org.hibernate.engine.internal.MutableEntityEntryFactory;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDHexGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.StaticFilterAliasGenerator;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 public class CustomPersister implements EntityPersister {
 
 	private static final Hashtable INSTANCES = new Hashtable();
 	private static final IdentifierGenerator GENERATOR = new UUIDHexGenerator();
 
 	private SessionFactoryImplementor factory;
 
 	@SuppressWarnings("UnusedParameters")
 	public CustomPersister(
 			PersistentClass model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			PersisterCreationContext creationContext) {
 		this.factory = creationContext.getSessionFactory();
 	}
 
 	public boolean hasLazyProperties() {
 		return false;
 	}
 
 	public boolean isInherited() {
 		return false;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public EntityEntryFactory getEntityEntryFactory() {
-		return DefaultEntityEntryFactory.INSTANCE;
+		return MutableEntityEntryFactory.INSTANCE;
 	}
 
 	@Override
 	public Class getMappedClass() {
 		return Custom.class;
 	}
 
 	@Override
 	public void generateEntityDefinition() {
 	}
 
 	public void postInstantiate() throws MappingException {}
 
 	public String getEntityName() {
 		return Custom.class.getName();
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return Custom.class.getName().equals(entityName);
 	}
 
 	public boolean hasProxy() {
 		return false;
 	}
 
 	public boolean hasCollections() {
 		return false;
 	}
 
 	public boolean hasCascades() {
 		return false;
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return false;
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return false;
 	}
 
 	public Boolean isTransient(Object object, SessionImplementor session) {
 		return ( (Custom) object ).id==null;
 	}
 
 	@Override
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 		return getPropertyValues( object );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void retrieveGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean implementsLifecycle() {
 		return false;
 	}
 
 	@Override
 	public Class getConcreteProxyClass() {
 		return Custom.class;
 	}
 
 	@Override
 	public void setPropertyValues(Object object, Object[] values) {
 		setPropertyValue( object, 0, values[0] );
 	}
 
 	@Override
 	public void setPropertyValue(Object object, int i, Object value) {
 		( (Custom) object ).setName( (String) value );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object object) throws HibernateException {
 		Custom c = (Custom) object;
 		return new Object[] { c.getName() };
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		return ( (Custom) object ).id;
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return ( (Custom) entity ).id;
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		( (Custom) entity ).id = (String) id;
 	}
 
 	@Override
 	public Object getVersion(Object object) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		Custom c = new Custom();
 		c.id = (String) id;
 		return c;
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return object instanceof Custom;
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return false;
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		( ( Custom ) entity ).id = ( String ) currentId;
 	}
 
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	public int[] findDirty(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	public int[] findModified(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * @see EntityPersister#hasIdentifierProperty()
 	 */
 	public boolean hasIdentifierProperty() {
 		return true;
 	}
 
 	/**
 	 * @see EntityPersister#isVersioned()
 	 */
 	public boolean isVersioned() {
 		return false;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionType()
 	 */
 	public VersionType getVersionType() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionProperty()
 	 */
 	public int getVersionProperty() {
 		return 0;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierGenerator()
 	 */
 	public IdentifierGenerator getIdentifierGenerator()
 	throws HibernateException {
 		return GENERATOR;
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, org.hibernate.LockOptions , SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 		return load(id, optionalObject, lockOptions.getLockMode(), session);
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, LockMode, SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		// fails when optional object is supplied
 
 		Custom clone = null;
 		Custom obj = (Custom) INSTANCES.get(id);
 		if (obj!=null) {
 			clone = (Custom) obj.clone();
 			TwoPhaseLoad.addUninitializedEntity(
 					session.generateEntityKey( id, this ),
 					clone,
 					this,
 					LockMode.NONE,
 					false,
 					session
 			);
 			TwoPhaseLoad.postHydrate(
 					this, id,
 					new String[] { obj.getName() },
 					null,
 					clone,
 					LockMode.NONE,
 					false,
 					session
 			);
 			TwoPhaseLoad.initializeEntity(
 					clone,
 					false,
 					session,
 					new PreLoadEvent( (EventSource) session )
 			);
 			TwoPhaseLoad.postLoad( clone, session, new PostLoadEvent( (EventSource) session ) );
 		}
 		return clone;
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void insert(
 		Serializable id,
 		Object[] fields,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put(id, ( (Custom) object ).clone() );
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void delete(
 		Serializable id,
 		Object version,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.remove(id);
 	}
 
 	/**
 	 * @see EntityPersister
 	 */
 	public void update(
 		Serializable id,
 		Object[] fields,
 		int[] dirtyFields,
 		boolean hasDirtyCollection,
 		Object[] oldFields,
 		Object oldVersion,
 		Object object,
 		Object rowId,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put( id, ( (Custom) object ).clone() );
 
 	}
 
 	private static final Type[] TYPES = new Type[] { StandardBasicTypes.STRING };
 	private static final String[] NAMES = new String[] { "name" };
 	private static final boolean[] MUTABILITY = new boolean[] { true };
 	private static final boolean[] GENERATION = new boolean[] { false };
 
 	/**
 	 * @see EntityPersister#getPropertyTypes()
 	 */
 	public Type[] getPropertyTypes() {
 		return TYPES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyNames()
 	 */
 	public String[] getPropertyNames() {
 		return NAMES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyCascadeStyles()
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierType()
 	 */
 	public Type getIdentifierType() {
 		return StandardBasicTypes.STRING;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierPropertyName()
 	 */
 	public String getIdentifierPropertyName() {
 		return "id";
 	}
 
 	public boolean hasCache() {
 		return false;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return null;
 	}
 	
 	public boolean hasNaturalIdCache() {
 		return false;
 	}
 
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return null;
 	}
 
 	public String getRootEntityName() {
 		return "CUSTOMS";
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	/**
 	 * @see EntityPersister#getClassMetadata()
 	 */
 	public ClassMetadata getClassMetadata() {
 		return null;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return MUTABILITY;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return MUTABILITY;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyInsertability()
 	 */
 	public boolean[] getPropertyInsertability() {
 		return MUTABILITY;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 
 	public boolean canExtractIdOutOfEntity() {
 		return true;
 	}
 
 	public boolean isBatchLoadable() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session)
 		throws HibernateException {
 		throw new UnsupportedOperationException("no proxy for this class");
 	}
 
 	public Object getCurrentVersion(
 		Serializable id,
 		SessionImplementor session)
 		throws HibernateException {
 
 		return INSTANCES.get(id);
 	}
 
 	@Override
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 			throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public boolean isCacheInvalidationRequired() {
 		return false;
 	}
 
 	@Override
 	public void afterInitialize(Object entity, boolean fetched, SessionImplementor session) {
 	}
 
 	@Override
 	public void afterReassociate(Object entity, SessionImplementor session) {
 	}
 
 	@Override
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 		throw new UnsupportedOperationException( "not supported" );
 	}
 
 	@Override
 	public boolean[] getPropertyVersionability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return UnstructuredCacheEntry.INSTANCE;
 	}
 
 	@Override
 	public CacheEntry buildCacheEntry(
 			Object entity, Object[] state, Object version, SessionImplementor session) {
 		return new StandardCacheEntryImpl(
 				state,
 				this,
 				this.hasUninitializedLazyProperties( entity ),
 				version,
 				session,
 				entity
 		);
 	}
 
 	@Override
 	public boolean hasSubselectLoadableCollections() {
 		return false;
 	}
 
 	@Override
 	public int[] getNaturalIdentifierProperties() {
 		return null;
 	}
 
 	@Override
 	public boolean hasNaturalIdentifier() {
 		return false;
 	}
 
 	@Override
 	public boolean hasMutableProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean isInstrumented() {
 		return false;
 	}
 
 	@Override
 	public boolean hasInsertGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean hasUpdateGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean[] getPropertyLaziness() {
 		return null;
 	}
 
 	@Override
 	public boolean isLazyPropertiesCacheable() {
 		return true;
 	}
 
 	@Override
 	public boolean isVersionPropertyGenerated() {
 		return false;
 	}
 
 	@Override
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 			SessionImplementor session) {
 		return null;
 	}
 
 	@Override
 	public Comparator getVersionComparator() {
 		return null;
 	}
 
 	@Override
 	public EntityMetamodel getEntityMetamodel() {
 		return null;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return null;
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return new NonPojoInstrumentationMetadata( getEntityName() );
 	}
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new StaticFilterAliasGenerator(rootAlias);
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return this;
 	}
 
 	@Override
 	public EntityIdentifierDefinition getEntityKeyDefinition() {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		throw new NotYetImplementedException();
 	}
 
     @Override
     public int[] resolveAttributeIndexes(Set<String> attributes) {
         return null;
     }
 
 	@Override
 	public boolean canUseReferenceCacheEntries() {
 		return false;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/PersisterClassProviderTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/PersisterClassProviderTest.java
index 360db60842..f92e934ab0 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/PersisterClassProviderTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/PersisterClassProviderTest.java
@@ -1,642 +1,642 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * JBoss, Home of Professional Open Source
  * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.jpa.test.ejb3configuration;
 
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.Map;
 import java.util.Set;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.PersistenceException;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
-import org.hibernate.engine.internal.DefaultEntityEntryFactory;
+import org.hibernate.engine.internal.MutableEntityEntryFactory;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.boot.spi.Bootstrap;
 import org.hibernate.jpa.test.PersistenceUnitDescriptorAdapter;
 import org.hibernate.jpa.test.SettingsGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 import org.junit.Assert;
 import org.junit.Test;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class PersisterClassProviderTest {
 	@Test
 	@SuppressWarnings("unchecked")
 	public void testPersisterClassProvider() {
 		Map settings = SettingsGenerator.generateSettings(
 				PersisterClassResolverInitiator.IMPL_NAME, GoofyPersisterClassProvider.class,
 				AvailableSettings.LOADED_CLASSES, Arrays.asList( Bell.class )
 		);
 		try {
 			EntityManagerFactory entityManagerFactory = Bootstrap.getEntityManagerFactoryBuilder(
 					new PersistenceUnitDescriptorAdapter(),
 					settings
 			).build();
 			entityManagerFactory.close();
 		}
 		catch ( PersistenceException e ) {
             Assert.assertNotNull( e.getCause() );
 			Assert.assertNotNull( e.getCause().getCause() );
 			Assert.assertEquals( GoofyException.class, e.getCause().getCause().getClass() );
 
 		}
 	}
 
 	public static class GoofyPersisterClassProvider implements PersisterClassResolver {
 		@Override
 		public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 			return GoofyProvider.class;
 		}
 
 		@Override
 		public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 			return null;
 		}
 	}
 
 	public static class GoofyProvider implements EntityPersister {
 
 		@SuppressWarnings( {"UnusedParameters"})
 		public GoofyProvider(
 				org.hibernate.mapping.PersistentClass persistentClass,
 				org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
 				NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 				PersisterCreationContext creationContext) {
 			throw new GoofyException();
 		}
 
 		@Override
 		public EntityMode getEntityMode() {
 			return null;
 		}
 
 		@Override
 		public EntityTuplizer getEntityTuplizer() {
 			return null;
 		}
 
 		@Override
 		public EntityInstrumentationMetadata getInstrumentationMetadata() {
 			return new NonPojoInstrumentationMetadata( getEntityName() );
 		}
 
 		@Override
 		public void generateEntityDefinition() {
 		}
 
 		@Override
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		@Override
 		public SessionFactoryImplementor getFactory() {
 			return null;
 		}
 
 		@Override
 		public EntityEntryFactory getEntityEntryFactory() {
-			return DefaultEntityEntryFactory.INSTANCE;
+			return MutableEntityEntryFactory.INSTANCE;
 		}
 
 		@Override
 		public String getRootEntityName() {
 			return null;
 		}
 
 		@Override
 		public String getEntityName() {
 			return null;
 		}
 
 		@Override
 		public EntityMetamodel getEntityMetamodel() {
 			return null;
 		}
 
 		@Override
 		public boolean isSubclassEntityName(String entityName) {
 			return false;
 		}
 
 		@Override
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public boolean hasProxy() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasMutableProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasSubselectLoadableCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCascades() {
 			return false;
 		}
 
 		@Override
 		public boolean isMutable() {
 			return false;
 		}
 
 		@Override
 		public boolean isInherited() {
 			return false;
 		}
 
 		@Override
 		public boolean isIdentifierAssignedByInsert() {
 			return false;
 		}
 
 		@Override
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;
 		}
 
 		@Override
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean hasIdentifierProperty() {
 			return false;
 		}
 
 		@Override
 		public boolean canExtractIdOutOfEntity() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersioned() {
 			return false;
 		}
 
 		@Override
 		public Comparator getVersionComparator() {
 			return null;
 		}
 
 		@Override
 		public VersionType getVersionType() {
 			return null;
 		}
 
 		@Override
 		public int getVersionProperty() {
 			return 0;
 		}
 
 		@Override
 		public boolean hasNaturalIdentifier() {
 			return false;
 		}
 		
         @Override
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];
 		}
 
 		@Override
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 				SessionImplementor session) {
 			return null;
 		}
 		
 		@Override
         public boolean hasNaturalIdCache() {
             return false;
         }
 
         @Override
         public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
             return null;
         }
 
         @Override
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean hasLazyProperties() {
 			return false;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
 		}
 
 		@Override
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
 		}
 
 		@Override
 		public Type[] getPropertyTypes() {
 			return new Type[0];
 		}
 
 		@Override
 		public String[] getPropertyNames() {
 			return new String[0];
 		}
 
 		@Override
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];
 		}
 
 		@Override
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];
 		}
 
 		@Override
 		public Type getIdentifierType() {
 			return null;
 		}
 
 		@Override
 		public String getIdentifierPropertyName() {
 			return null;
 		}
 
 		@Override
 		public boolean isCacheInvalidationRequired() {
 			return false;
 		}
 
 		@Override
 		public boolean isLazyPropertiesCacheable() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCache() {
 			return false;
 		}
 
 		@Override
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;
 		}
 
 		@Override
 		public ClassMetadata getClassMetadata() {
 			return null;
 		}
 
 		@Override
 		public boolean isBatchLoadable() {
 			return false;
 		}
 
 		@Override
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;
 		}
 
 		@Override
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 			throw new UnsupportedOperationException( "Not supported" );
 		}
 
 		@Override
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return false;
 		}
 
 		@Override
 		public boolean hasInsertGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasUpdateGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersionPropertyGenerated() {
 			return false;
 		}
 
 		@Override
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		}
 
 		@Override
 		public void afterReassociate(Object entity, SessionImplementor session) {
 		}
 
 		@Override
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public Class getMappedClass() {
 			return null;
 		}
 
 		@Override
 		public boolean implementsLifecycle() {
 			return false;
 		}
 
 		@Override
 		public Class getConcreteProxyClass() {
 			return null;
 		}
 
 		@Override
 		public void setPropertyValues(Object object, Object[] values) {
 		}
 
 		@Override
 		public void setPropertyValue(Object object, int i, Object value) {
 		}
 
 		@Override
 		public Object[] getPropertyValues(Object object) {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, int i) {
 			return null;
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, String propertyName) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object object) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		}
 
 		@Override
 		public Object getVersion(Object object) {
 			return null;
 		}
 
 		@Override
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstance(Object object) {
 			return false;
 		}
 
 		@Override
 		public boolean hasUninitializedLazyProperties(Object object) {
 			return false;
 		}
 
 		@Override
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		}
 
 		@Override
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 			return null;
 		}
 
 		@Override
 		public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 			return null;
 		}
 
 		@Override
 		public int[] resolveAttributeIndexes(Set<String> properties) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean canUseReferenceCacheEntries() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public EntityPersister getEntityPersister() {
 			return this;
 		}
 
 		@Override
 		public EntityIdentifierDefinition getEntityKeyDefinition() {
 			return null;
 		}
 
 		@Override
 		public Iterable<AttributeDefinition> getAttributes() {
 			return null;
 		}
 	}
 
 	public static class GoofyException extends RuntimeException {
 
 	}
 }
