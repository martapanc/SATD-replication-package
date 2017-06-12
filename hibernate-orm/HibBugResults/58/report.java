File path: hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationKey.java
Comment: ODO: inefficient
Initial commit id: a102bf2c
Final commit id: 18079f34
   Bugs between [       1]:
18079f346d HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC) - Initial reworking to remove SQL references (for reuse in Search, OGM, etc) and to split out conceptual "from clause" and "select clause" into different structures (see QuerySpaces)
   Bugs after [       2]:
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
af1061a42d HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)

Start block index: 49
End block index: 51
	public int hashCode() {
		return table.hashCode(); //TODO: inefficient
	}
