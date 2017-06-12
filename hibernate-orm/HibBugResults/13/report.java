File path: code/core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
Comment: hack/workaround as sqlquery impl depend on having a key.
Initial commit id: d8d6d82e
Final commit id: 9caca0ce
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 80
End block index: 101
	private static NativeSQLQueryRootReturn bindReturn(Element returnElem, Mappings mappings, int elementCount) {
		String alias = returnElem.attributeValue( "alias" );
		if( StringHelper.isEmpty(alias)) {
			alias = "alias_" + elementCount; // hack/workaround as sqlquery impl depend on having a key.
		}

		String entityName = HbmBinder.getEntityName(returnElem, mappings);
		if(entityName==null) {
			throw new MappingException( "<return alias='" + alias + "'> must specify either a class or entity-name");
		}
		LockMode lockMode = getLockMode( returnElem.attributeValue( "lock-mode" ) );

		PersistentClass pc = mappings.getClass( entityName );
		java.util.Map propertyResults = bindPropertyResults(alias, returnElem, pc, mappings );

		return new NativeSQLQueryRootReturn(
				alias,
				entityName,
				propertyResults,
				lockMode
			);
	}
