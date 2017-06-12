File path: code/core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
Comment: todo : better to handle dynamic filters through implicit DynamicFilterParameterSpecification
Initial commit id: d8d6d82e
Final commit id: 6cabc326
   Bugs between [       0]:

   Bugs after [       9]:
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
eb308a953a HHH-9340 - Streams API for query result processing.
7cae5ba95b HHH-9340 - Streams API for query result processing.
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
bbfacee64d HHH-9803 - Checkstyle fix ups
193c8cef20 HHH-9344 Convert QueryLoader to use nanoTime instead of currentTimeMillis
b3e79f3f40 HHH-3051 corrected CustomLoader returnTypes (different approach)
2a90123991 HHH-3051 corrected Loader returnTypes

Start block index: 495
End block index: 518
	private int bindFilterParameterValues(
			PreparedStatement st,
			QueryParameters queryParameters,
			int position,
			SessionImplementor session) throws SQLException {
		// todo : better to handle dynamic filters through implicit DynamicFilterParameterSpecification
		// see the discussion there in DynamicFilterParameterSpecification's javadocs as to why
		// it is currently not done that way.
		int filteredParamCount = queryParameters.getFilteredPositionalParameterTypes() == null
				? 0
				: queryParameters.getFilteredPositionalParameterTypes().length;
		int nonfilteredParamCount = queryParameters.getPositionalParameterTypes() == null
				? 0
				: queryParameters.getPositionalParameterTypes().length;
		int filterParamCount = filteredParamCount - nonfilteredParamCount;
		for ( int i = 0; i < filterParamCount; i++ ) {
			Type type = queryParameters.getFilteredPositionalParameterTypes()[i];
			Object value = queryParameters.getFilteredPositionalParameterValues()[i];
			type.nullSafeSet( st, value, position, session );
			position += type.getColumnSpan( getFactory() );
		}

		return position;
	}
