File path: code/core/src/main/java/org/hibernate/criterion/Example.java
Comment: uck!
Initial commit id: d8d6d82e
Final commit id: 8c28ba84
   Bugs between [       0]:

   Bugs after [       2]:
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
1361925bc7 HHH-9722

Start block index: 177
End block index: 216
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
		throws HibernateException {

		StringBuffer buf = new StringBuffer().append('(');
		EntityPersister meta = criteriaQuery.getFactory().getEntityPersister( criteriaQuery.getEntityName(criteria) );
		String[] propertyNames = meta.getPropertyNames();
		Type[] propertyTypes = meta.getPropertyTypes();
		//TODO: get all properties, not just the fetched ones!
		Object[] propertyValues = meta.getPropertyValues( entity, getEntityMode(criteria, criteriaQuery) );
		for (int i=0; i<propertyNames.length; i++) {
			Object propertyValue = propertyValues[i];
			String propertyName = propertyNames[i];

			boolean isPropertyIncluded = i!=meta.getVersionProperty() &&
				isPropertyIncluded( propertyValue, propertyName, propertyTypes[i] );
			if (isPropertyIncluded) {
				if ( propertyTypes[i].isComponentType() ) {
					appendComponentCondition(
						propertyName,
						propertyValue,
						(AbstractComponentType) propertyTypes[i],
						criteria,
						criteriaQuery,
						buf
					);
				}
				else {
					appendPropertyCondition(
						propertyName,
						propertyValue,
						criteria,
						criteriaQuery,
						buf
					);
				}
			}
		}
		if ( buf.length()==1 ) buf.append("1=1"); //yuck!
		return buf.append(')').toString();
	}
