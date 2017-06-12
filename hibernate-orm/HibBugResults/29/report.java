File path: code/core/src/main/java/org/hibernate/jdbc/Batcher.java
Comment: TODO : remove these last two as batcher is no longer managing connections
Initial commit id: d8d6d82e
Final commit id: 97fef96b
   Bugs between [       0]:

   Bugs after [       2]:
7262276fa9 HHH-5778 : Wire in new batch code
b006a6c3c5 HHH-5765 : Refactor JDBCContext/ConnectionManager spi/impl and to use new proxies

Start block index: 140
End block index: 145
	// TODO : remove these last two as batcher is no longer managing connections

	/**
	 * Obtain a JDBC connection
	 */
	public Connection openConnection() throws HibernateException;
