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

import org.hibernate.EntityMode;
import org.hibernate.LockMode;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.persister.entity.EntityPersister;

import java.io.IOException;
import java.io.ObjectInputStream;
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
public final class MutableEntityEntry extends AbstractEntityEntry {

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

		super( status, loadedState, rowId, id, version, lockMode, existsInDatabase, persister,
				disableVersionIncrement, lazyPropertiesAreUnfetched, persistenceContext );
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

		super( factory, entityName, id, status, previousStatus, loadedState, deletedState,
				version, lockMode, existsInDatabase, isBeingReplicated, loadedWithLazyPropertiesUnfetched,
				persistenceContext );
	}


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
}
