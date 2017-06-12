File path: code/core/src/main/java/org/hibernate/type/EntityType.java
Comment: ODO: implement caching?! proxies?!
Initial commit id: d8d6d82e
Final commit id: 1c349144
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 585
End block index: 610
	public Object loadByUniqueKey(
			String entityName,
			String uniqueKeyPropertyName,
			Object key,
			SessionImplementor session) throws HibernateException {
		final SessionFactoryImplementor factory = session.getFactory();
		UniqueKeyLoadable persister = ( UniqueKeyLoadable ) factory.getEntityPersister( entityName );

		//TODO: implement caching?! proxies?!

		EntityUniqueKey euk = new EntityUniqueKey(
				entityName,
				uniqueKeyPropertyName,
				key,
				getIdentifierOrUniqueKeyType( factory ),
				session.getEntityMode(),
				session.getFactory()
		);

		final PersistenceContext persistenceContext = session.getPersistenceContext();
		Object result = persistenceContext.getEntity( euk );
		if ( result == null ) {
			result = persister.loadByUniqueKey( uniqueKeyPropertyName, key, session );
		}
		return result == null ? null : persistenceContext.proxyFor( result );
	}
