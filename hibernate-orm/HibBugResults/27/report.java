File path: hibernate-core/src/main/java/org/hibernate/engine/internal/MutableEntityEntry.java
Comment: todo : potentially look at optimizing these two arrays
Initial commit id: 5c4dacb8
Final commit id: 3e5a8b66
   Bugs between [       1]:
3e5a8b6603 HHH-9701 - Develop "immutable EntityEntry" impl
   Bugs after [       3]:
1e44e7420b HHH-10267 - Support defining lazy attribute fetch groups
750d6fb090 HHH-9857 - Reuse of EntityEntry for bytecode enhanced read-only reference cached entities
bd256e4783 HHH-9803 - Checkstyle fix ups - headers

Start block index: 451
End block index: 465
	public void serialize(ObjectOutputStream oos) throws IOException {
		Status previousStatus = getPreviousStatus();
		oos.writeObject( getEntityName() );
		oos.writeObject( id );
		oos.writeObject( getStatus().name() );
		oos.writeObject( (previousStatus == null ? "" : previousStatus.name()) );
		// todo : potentially look at optimizing these two arrays
		oos.writeObject( loadedState );
		oos.writeObject( getDeletedState() );
		oos.writeObject( version );
		oos.writeObject( getLockMode().toString() );
		oos.writeBoolean( isExistsInDatabase() );
		oos.writeBoolean( isBeingReplicated() );
		oos.writeBoolean( isLoadedWithLazyPropertiesUnfetched() );
	}
