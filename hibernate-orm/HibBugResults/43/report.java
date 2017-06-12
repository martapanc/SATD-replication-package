File path: hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
Comment: why does this matter?
Initial commit id: b3791bc3
Final commit id: 8e2f2a9d
   Bugs between [       6]:
8e2f2a9da6 HHH-8597 : Delete org.hibernate.loader.plan2 and related code
dc7cdf9d88 HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
1825a4762c HHH-8211 Checkstyle and FindBugs fix-ups
b6fd7bf223 HHH-7841 - Redesign Loader
f3298620ee HHH-7841 - Redesign Loader
560a397a01 HHH-7841 - Redesign Loader
   Bugs after [       0]:


Start block index: 129
End block index: 160
	protected static final String orderBy(List<JoinableAssociationImpl> associations)
	throws MappingException {
		StringBuilder buf = new StringBuilder();
		JoinableAssociationImpl last = null;
		for ( JoinableAssociationImpl oj : associations ) {
			if ( oj.getJoinType() == JoinType.LEFT_OUTER_JOIN ) { // why does this matter?
				if ( oj.getJoinable().isCollection() ) {
					final QueryableCollection queryableCollection = (QueryableCollection) oj.getJoinable();
					if ( queryableCollection.hasOrdering() ) {
						final String orderByString = queryableCollection.getSQLOrderByString( oj.getRHSAlias() );
						buf.append( orderByString ).append(", ");
					}
				}
				else {
					// it might still need to apply a collection ordering based on a
					// many-to-many defined order-by...
					if ( last != null && last.getJoinable().isCollection() ) {
						final QueryableCollection queryableCollection = (QueryableCollection) last.getJoinable();
						if ( queryableCollection.isManyToMany() && last.isManyToManyWith( oj ) ) {
							if ( queryableCollection.hasManyToManyOrdering() ) {
								final String orderByString = queryableCollection.getManyToManyOrderByString( oj.getRHSAlias() );
								buf.append( orderByString ).append(", ");
							}
						}
					}
				}
			}
			last = oj;
		}
		if ( buf.length()>0 ) buf.setLength( buf.length()-2 );
		return buf.toString();
	}
