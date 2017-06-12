	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
		final String[] columns = criteriaQuery.findColumns( propertyName, criteria );
		final String[] expressions = StringHelper.suffix( columns, " between ? and ?" );
		return StringHelper.join( " and ", expressions );
	}
