File path: code/core/src/main/java/org/hibernate/exception/Configurable.java
Comment: todo: this might really even be moved into the cfg package and used as the basis for all things which are configurable.
Initial commit id: d8d6d82e
Final commit id: a806626a
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 14
End block index: 22
	// todo: this might really even be moved into the cfg package and used as the basis for all things which are configurable.

	/**
	 * Configure the component, using the given settings and properties.
	 *
	 * @param properties All defined startup properties.
	 * @throws HibernateException Indicates a configuration exception.
	 */
	public void configure(Properties properties) throws HibernateException;
