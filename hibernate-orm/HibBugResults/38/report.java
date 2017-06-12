File path: code/core/src/main/java/org/hibernate/persister/entity/PropertyMapping.java
Comment: TODO: It would be really
Initial commit id: d8d6d82e
Final commit id: af1061a4
   Bugs between [       0]:

   Bugs after [       2]:
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
f679a3c783 HHH-8597 : Rename org.hibernate.loader.plan2 to org.hibernate.loader.plan

Start block index: 13
End block index: 17
	// TODO: It would be really, really nice to use this to also model components!
	/**
	 * Given a component path expression, get the type of the property
	 */
	public Type toType(String propertyName) throws QueryException;
