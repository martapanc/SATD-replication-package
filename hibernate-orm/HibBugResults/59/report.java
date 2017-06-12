File path: code/core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
Comment: ODO: is there a more elegant way than downcasting?
Initial commit id: d8d6d82e
Final commit id: 87e3f0fd
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 29
End block index: 57
	public void initialize(Serializable key, SessionImplementor session) 
	throws HibernateException {
		
		if ( log.isDebugEnabled() ) {
			log.debug(
					"initializing collection: " + 
					persister.getRole() + 
					" using named query: " + 
					queryName 
				);
		}
		
		//TODO: is there a more elegant way than downcasting?
		AbstractQueryImpl query = (AbstractQueryImpl) session.getNamedSQLQuery(queryName); 
		if ( query.getNamedParameters().length>0 ) {
			query.setParameter( 
					query.getNamedParameters()[0], 
					key, 
					persister.getKeyType() 
				);
		}
		else {
			query.setParameter( 0, key, persister.getKeyType() );
		}
		query.setCollectionKey( key )
				.setFlushMode( FlushMode.MANUAL )
				.list();

	}
