File path: code/core/src/main/java/org/hibernate/engine/TransactionHelper.java
Comment: todo : remove this and just have subclasses use Isolater/IsolatedWork directly...
Initial commit id: d8d6d82e
Final commit id: 08d9fe21
   Bugs between [       0]:

   Bugs after [       1]:
21cc90fbf4 HHH-5985 - Remove TransactionHelper in preference of IsolationDelegate

Start block index: 18
End block index: 53
public abstract class TransactionHelper {

	// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...

	/**
	 * The work to be done
	 */
	protected abstract Serializable doWorkInCurrentTransaction(Connection conn, String sql) throws SQLException;

	/**
	 * Suspend the current transaction and perform work in a new transaction
	 */
	public Serializable doWorkInNewTransaction(final SessionImplementor session)
	throws HibernateException {
		class Work implements IsolatedWork {
			Serializable generatedValue;
			public void doWork(Connection connection) throws HibernateException {
				String sql = null;
				try {
					generatedValue = doWorkInCurrentTransaction( connection, sql );
				}
				catch( SQLException sqle ) {
					throw JDBCExceptionHelper.convert(
							session.getFactory().getSQLExceptionConverter(),
							sqle,
							"could not get or update next value",
							sql
						);
				}
			}
		}
		Work work = new Work();
		Isolater.doIsolatedWork( work, session );
		return work.generatedValue;
	}
}
