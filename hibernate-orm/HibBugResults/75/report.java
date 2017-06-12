File path: code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
Comment:  it depends upon ordering of mapping doc
Initial commit id: d8d6d82e
Final commit id: 9caca0ce
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 2927
End block index: 2947
	private static void parseFilter(Element filterElement, Filterable filterable, Mappings model) {
		final String name = filterElement.attributeValue( "name" );
		String condition = filterElement.getTextTrim();
		if ( StringHelper.isEmpty(condition) ) {
			condition = filterElement.attributeValue( "condition" );
		}
		//TODO: bad implementation, cos it depends upon ordering of mapping doc
		//      fixing this requires that Collection/PersistentClass gain access
		//      to the Mappings reference from Configuration (or the filterDefinitions
		//      map directly) sometime during Configuration.buildSessionFactory
		//      (after all the types/filter-defs are known and before building
		//      persisters).
		if ( StringHelper.isEmpty(condition) ) {
			condition = model.getFilterDefinition(name).getDefaultFilterCondition();
		}
		if ( condition==null) {
			throw new MappingException("no filter condition found for filter: " + name);
		}
		log.debug( "Applying filter [" + name + "] as [" + condition + "]" );
		filterable.addFilter( name, condition );
	}
