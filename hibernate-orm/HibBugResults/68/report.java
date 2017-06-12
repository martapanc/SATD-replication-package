File path: code/core/src/main/java/org/hibernate/type/EntityType.java
Comment: ODO: this is a bit arbitrary
Initial commit id: d8d6d82e
Final commit id: 9938937f
   Bugs between [       0]:

   Bugs after [      12]:
1c34914455 HHH-11703 - Entity with Natural ID not being cached in the persistenceContext, causing extra queries
0a2a5c622e HHH-11097 - Performance problem if cached entity has attribute state with an expensive toString() method (LOB, etc)
816c97613d HHH-9512: Avoid creation of invalid managed -> managed entity mapping in MergeContext when traversing cascade loop
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
47b8ed5121 HHH-10073 Removing methods scheduled for removal in 5.0
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
611f8a0e1c HHH-9803 - Checkstyle fix ups
5bdef580bd HHH-8991 Cache lookup of identifier Type and associated EntityPersister for each EntityType
76aede601f HHH-8845 formatting
6329be56ff HHH-8845 - More informative error message
8fe5460ec0 HHH-8741 - More checkstyle cleanups
cd590470c0 HHH-8741 - More checkstyle cleanups

Start block index: 351
End block index: 359
	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
	throws MappingException {
		if ( isReferenceToPrimaryKey() ) { //TODO: this is a bit arbitrary, expose a switch to the user?
			return "";
		}
		else {
			return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
		}
	}
