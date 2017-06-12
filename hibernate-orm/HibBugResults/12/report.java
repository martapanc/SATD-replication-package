File path: code/core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
Comment: get the right object from the list ... would it be easier to just call getEntity() ??
Initial commit id: d8d6d82e
Final commit id: 06b0faaf
   Bugs between [       0]:

   Bugs after [       4]:
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
7308e14fed HHH-9803 - Checkstyle fix ups
cd590470c0 HHH-8741 - More checkstyle cleanups

Start block index: 40
End block index: 54
	private Object getObjectFromList(List results, Serializable id, SessionImplementor session) {
		// get the right object from the list ... would it be easier to just call getEntity() ??
		Iterator iter = results.iterator();
		while ( iter.hasNext() ) {
			Object obj = iter.next();
			final boolean equal = idType.isEqual( 
					id, 
					session.getContextEntityIdentifier(obj), 
					session.getEntityMode(), 
					session.getFactory() 
			);
			if ( equal ) return obj;
		}
		return null;
	}
