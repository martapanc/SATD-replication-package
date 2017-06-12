File path: code/core/src/main/java/org/hibernate/criterion/BetweenExpression.java
Comment: ODO: get SQL rendering out of this package!
Initial commit id: d8d6d82e
Final commit id: 8c28ba84
   Bugs between [       0]:

   Bugs after [       2]:
c8cbb8f0c6 HHH-11584 - Made parameter names of Restrictions#between more readable
bd256e4783 HHH-9803 - Checkstyle fix ups - headers

Start block index: 25
End block index: 33
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
	throws HibernateException {
		return StringHelper.join(
			" and ",
			StringHelper.suffix( criteriaQuery.getColumnsUsingProjection(criteria, propertyName), " between ? and ?" )
		);

		//TODO: get SQL rendering out of this package!
	}
