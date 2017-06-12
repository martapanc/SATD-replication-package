	protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
		final List<String> list = new ArrayList<String>();
		Type elemType = getElementType( factory );
		Iterator itr = getElementsIterator( value );
		while ( itr.hasNext() ) {
			list.add( elemType.toLoggableString( itr.next(), factory ) );
		}
		return list.toString();
	}
