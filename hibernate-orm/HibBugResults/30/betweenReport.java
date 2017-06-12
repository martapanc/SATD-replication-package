30/report.java
Satd-method: public abstract class TransactionHelper {
********************************************
********************************************
30/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
class 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* doIsolatedWork
* getSQLExceptionConverter
* getFactory
********************************************
********************************************
30/Between/ HHH-5765  3ca8216c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-					throw JDBCExceptionHelper.convert(
-							session.getFactory().getSQLExceptionConverter(),
+					throw session.getFactory().getSQLExceptionHelper().convert(

Lines added: 1. Lines removed: 2. Tot = 3
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
class 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* doIsolatedWork
* getSQLExceptionConverter
* getFactory
—————————
Method found in diff:	public static void doIsolatedWork(IsolatedWork work, SessionImplementor session) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public SQLExceptionConverter getSQLExceptionConverter() {
-	public SQLExceptionConverter getSQLExceptionConverter() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
30/Between/ HHH-5949  08d9fe21_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...
+	// todo : remove this and just have subclasses use IsolationDelegate directly...
-	public Serializable doWorkInNewTransaction(final SessionImplementor session)
-	throws HibernateException {
-		class Work implements IsolatedWork {
+	public Serializable doWorkInNewTransaction(final SessionImplementor session) throws HibernateException {
+		class WorkToDo implements Work {
-			public void doWork(Connection connection) throws HibernateException {
+
+			@Override
+			public void execute(Connection connection) throws SQLException {
-				catch( SQLException sqle ) {
+				catch( SQLException e ) {
-							sqle,
+							e,
-						);
+					);
-		Work work = new Work();
-		Isolater.doIsolatedWork( work, session );
+		WorkToDo work = new WorkToDo();
+		session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork( work, true );

Lines added: 11. Lines removed: 10. Tot = 21
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
class 
-public class JRun4TransactionManagerLookup extends JNDITransactionManagerLookup {
+public class ResourceClosedException extends HibernateException {
-		Settings settings = buildSettings( copy, serviceRegistry.getService( JdbcServices.class ) );
+		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
+		settings.setJtaPlatform( serviceRegistry.getService( JtaPlatform.class ) );
+			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
+		final JtaPlatform jtaPlatform = factory.getServiceRegistry().getService( JtaPlatform.class );
-		class Work implements IsolatedWork {
+		class WorkToDo implements Work {
+public class BasicBatchKey implements BatchKey {
-public class BatchBuilder {
-	private static final Logger log = LoggerFactory.getLogger( BatchBuilder.class );
+public class BatchBuilderImpl implements BatchBuilder, Configurable {
+	private static final Logger log = LoggerFactory.getLogger( BatchBuilderImpl.class );
+public class BatchBuilderInitiator implements ServiceInitiator<BatchBuilder> {
+			return (BatchBuilder) registry.getService( ClassLoaderService.class ).classForName( builderClassName ).newInstance();
-public class ConnectionManagerImpl implements ConnectionManager {
-	private static final Logger log = LoggerFactory.getLogger( ConnectionManagerImpl.class );
-	 * @throws ClassNotFoundException Indicates resource class resolution.
-public class JDBCContextImpl implements ConnectionManagerImpl.Callback, JDBCContext {
-	private static final Logger log = LoggerFactory.getLogger( JDBCContextImpl.class );
+public class JdbcCoordinatorImpl implements JdbcCoordinator {
+	private static final Logger log = LoggerFactory.getLogger( JdbcCoordinatorImpl.class );
+		return sessionFactory().getServiceRegistry().getService( BatchBuilder.class );
-public class StatementPreparer {
-	private static final Logger log = LoggerFactory.getLogger( StatementPreparer.class );
-	private abstract class StatementPreparation {
-	private abstract class QueryStatementPreparation extends StatementPreparation {
+class StatementPreparerImpl implements StatementPreparer {
+	private abstract class StatementPreparationTemplate {
+	private abstract class QueryStatementPreparationTemplate extends StatementPreparationTemplate {
-public class ConnectionProxyHandler extends AbstractProxyHandler implements InvocationHandler, ConnectionObserver {
+public class ConnectionProxyHandler
+public class ConnectionObserverAdapter implements ConnectionObserver {
-public final class JBossTransactionManagerLookup extends JNDITransactionManagerLookup {
-public class SQLExceptionHelper {
+public class SQLExceptionHelper implements Serializable {
-public class Isolater {
-	private static final Logger log = LoggerFactory.getLogger( Isolater.class );
-	public static class JtaDelegate implements Delegate {
-	public static class JdbcDelegate implements Delegate {
-public class SynchronizationRegistry {
-	private static final Logger log = LoggerFactory.getLogger( SynchronizationRegistry.class );
+public class SynchronizationRegistryImpl implements SynchronizationRegistry {
+	private static final Logger log = LoggerFactory.getLogger( SynchronizationRegistryImpl.class );
+public class TransactionCoordinatorImpl implements TransactionCoordinator {
+	private static final Logger log = LoggerFactory.getLogger( TransactionCoordinatorImpl.class );
+public class TransactionFactoryInitiator implements ServiceInitiator<TransactionFactory> {
+	private static final Logger log = LoggerFactory.getLogger( TransactionFactoryInitiator.class );
+		ClassLoaderService classLoaderService = registry.getService( ClassLoaderService.class );
+			throw new HibernateException( "Unable to instantiate specified TransactionFactory class [" + strategyClassName + "]", e );
+public class JdbcIsolationDelegate implements IsolationDelegate {
+	private static final Logger log = LoggerFactory.getLogger( JdbcIsolationDelegate.class );
+public class JdbcTransaction extends AbstractTransactionImpl {
+	private static final Logger log = LoggerFactory.getLogger( JdbcTransaction.class );
+public final class JdbcTransactionFactory implements TransactionFactory<JdbcTransaction> {
+public class CMTTransaction extends AbstractTransactionImpl {
-public class CMTTransactionFactory implements TransactionFactory {
+public class CMTTransactionFactory  implements TransactionFactory<CMTTransaction> {
+public class JtaIsolationDelegate implements IsolationDelegate {
+	private static final Logger log = LoggerFactory.getLogger( JtaIsolationDelegate.class );
+public class JtaTransaction extends AbstractTransactionImpl {
+	private static final Logger log = LoggerFactory.getLogger( JtaTransaction.class );
+				log.warn( "You should set hibernate.transaction.manager_lookup_class if cache is enabled" );
+public class JtaTransactionFactory implements TransactionFactory<JtaTransaction> {
+public abstract class AbstractTransactionImpl implements TransactionImplementor {
+	private static final Logger log = LoggerFactory.getLogger( AbstractTransactionImpl.class );
-public class HibernateSynchronizationImpl implements Synchronization {
-	private static final Logger log = LoggerFactory.getLogger( HibernateSynchronizationImpl.class );
+public class RegisteredSynchronization implements Synchronization {
+	private static final Logger log = LoggerFactory.getLogger( RegisteredSynchronization.class );
-public class CallbackCoordinator {
-	private static final Logger log = LoggerFactory.getLogger( CallbackCoordinator.class );
+public class SynchronizationCallbackCoordinatorImpl implements SynchronizationCallbackCoordinator {
+	private static final Logger log = LoggerFactory.getLogger( SynchronizationCallbackCoordinatorImpl.class );
+	private static class TemporaryTableCreationWork implements Work {
+	private static class TemporaryTableDropWork implements Work {
-public abstract class AbstractSessionImpl implements SessionImplementor {
+public abstract class AbstractSessionImpl implements SessionImplementor, TransactionContext {
+public class ConnectionObserverStatsBridge implements ConnectionObserver, Serializable {
-public final class SessionFactoryImpl implements SessionFactory, SessionFactoryImplementor {
+public final class SessionFactoryImpl
+		return serviceRegistry.getService( org.hibernate.engine.transaction.spi.TransactionFactory.class );
+			return serviceRegistry.getService( JtaPlatform.class ).retrieveTransactionManager() != null;
- * of Hibernate's internal implementation. As such, this class exposes two interfaces;
- * of Hibernate. This class is not threadsafe.
+ * This class is not thread-safe.
-public final class SessionImpl extends AbstractSessionImpl 
+public final class SessionImpl
+	// a separate class responsible for generating/dispatching events just duplicates most of the Session methods...
-public class StatelessSessionImpl extends AbstractSessionImpl
+public class StatelessSessionImpl extends AbstractSessionImpl implements StatelessSession {
+public class TransactionEnvironmentImpl implements TransactionEnvironment {
+		return serviceRegistry().getService( JdbcServices.class );
+		return serviceRegistry().getService( JtaPlatform.class );
+		return serviceRegistry().getService( TransactionFactory.class );
-public class BorrowedConnectionProxy implements InvocationHandler {
-	private static final Class[] PROXY_INTERFACES = new Class[] { Connection.class, ConnectionWrapper.class };
-	 * Determines the appropriate class loader to which the generated proxy
-	 * @return The class loader appropriate for proxy construction.
+	private static final Logger log = LoggerFactory.getLogger( HibernateServiceMBean.class );
-	 * The fully qualified class name of the Hibernate <tt>TransactionFactory</tt> implementation
+	 * The fully qualified class name of the Hibernate {@link org.hibernate.engine.transaction.spi.TransactionFactory}
-	 * Set the fully qualified class name of the Hibernate <tt>TransactionFactory</tt> implementation
+	 * Set the fully qualified class name of the Hibernate {@link org.hibernate.engine.transaction.spi.TransactionFactory}
-	 * @return the class name
-	 * @param lkpStrategy the class name
+	 * @param name The implementation class name.
-			InjectService injectService = method.getAnnotation( InjectService.class );
+				InjectService injectService = method.getAnnotation( InjectService.class );
+public abstract class AbstractJtaPlatform
+		return serviceRegistry().getService( JndiService.class );
-public class BTMTransactionManagerLookup implements TransactionManagerLookup {
+public class BitronixJtaPlatform extends AbstractJtaPlatform {
+			Class transactionManagerServicesClass = serviceRegistry().getService( ClassLoaderService.class ).classForName( TM_CLASS_NAME );
+public class BorlandEnterpriseServerJtaPlatform extends AbstractJtaPlatform {
-public class JBossAppServerPlatform implements JtaPlatform, Configurable {
+public class JBossAppServerPlatform extends AbstractJtaPlatform implements SynchronizationRegistryAccess {
+public class JBossStandAloneJtaPlatform extends AbstractJtaPlatform {
+					.getService( ClassLoaderService.class )
+public class JOTMJtaPlatform extends AbstractJtaPlatform {
+			final Class tmClass = serviceRegistry().getService( ClassLoaderService.class ).classForName( TM_CLASS_NAME );
-public class JOnASTransactionManagerLookup implements TransactionManagerLookup {
+public class JOnASJtaPlatform extends AbstractJtaPlatform {
+public class JRun4JtaPlatform extends AbstractJtaPlatform {
+	private static final Logger log = LoggerFactory.getLogger( JtaPlatformInitiator.class );
+			TransactionManagerLookup lookup = (TransactionManagerLookup) registry.getService( ClassLoaderService.class )
-public class JOTMTransactionManagerLookup implements TransactionManagerLookup {
+public class NoJtaPlatform implements JtaPlatform {
+public class OC4JJtaPlatform extends AbstractJtaPlatform {
+public class OrionJtaPlatform extends AbstractJtaPlatform {
+public class ResinJtaPlatform extends AbstractJtaPlatform {
+public class SunOneJtaPlatform extends AbstractJtaPlatform {
+public class SynchronizationRegistryBasedSynchronizationStrategy implements JtaSynchronizationStrategy {
+public class TransactionManagerBasedSynchronizationStrategy implements JtaSynchronizationStrategy {
+public class TransactionManagerLookupBridge extends AbstractJtaPlatform {
+		return (UserTransaction) serviceRegistry().getService( JndiService.class ).locate( lookup.getUserTransactionName() );
+ * This class is reported to work on WAS version 6 in any of the standard J2EE/JEE component containers.
-public class WebSphereExtendedJTATransactionLookup implements TransactionManagerLookup {
+public class WebSphereExtendedJtaPlatform extends AbstractJtaPlatform {
-	public static class TransactionManagerAdapter implements TransactionManager {
+	public class TransactionManagerAdapter implements TransactionManager {
+public class WebSphereJtaPlatform extends AbstractJtaPlatform {
+	private static final Logger log = LoggerFactory.getLogger( WebSphereJtaPlatform.class );
+public class WeblogicJtaPlatform extends AbstractJtaPlatform {
+public class JtaPlatformException extends HibernateException {
-public final class BESTransactionManagerLookup extends JNDITransactionManagerLookup {
-public class CMTTransaction implements Transaction {
-public final class CacheSynchronization implements Synchronization {
-public class JBossTSStandaloneTransactionManagerLookup implements TransactionManagerLookup {
-public class JDBCTransaction implements Transaction {
-public final class JDBCTransactionFactory implements TransactionFactory {
-public abstract class JNDITransactionManagerLookup implements TransactionManagerLookup {
-public class JTATransaction implements Transaction {
-	private static final Logger log = LoggerFactory.getLogger( JTATransaction.class );
-			log.warn( "You should set hibernate.transaction.manager_lookup_class if cache is enabled" );
-				log.warn( "You should set hibernate.transaction.manager_lookup_class if cache is enabled" );
-public class JTATransactionFactory implements TransactionFactory {
-	private static final Logger log = LoggerFactory.getLogger( JTATransactionFactory.class );
-public class OC4JTransactionManagerLookup extends JNDITransactionManagerLookup {
-public class OrionTransactionManagerLookup extends JNDITransactionManagerLookup {
-public class ResinTransactionManagerLookup extends JNDITransactionManagerLookup {
-public class SunONETransactionManagerLookup extends JNDITransactionManagerLookup {
-public final class TransactionFactoryFactory {
-	private static final Logger log = LoggerFactory.getLogger( TransactionFactoryFactory.class );
-			log.error( "TransactionFactory class not found", e );
-			throw new HibernateException( "TransactionFactory class not found: " + strategyClassName );
-public final class TransactionManagerLookupFactory {
-public class WebSphereTransactionManagerLookup implements TransactionManagerLookup {
+		TransactionManager tm = factory.getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager();
+public class JournalingBatchObserver implements BatchObserver {
+public class JournalingConnectionObserver implements ConnectionObserver {
-public final class WeblogicTransactionManagerLookup extends JNDITransactionManagerLookup {
+public class JournalingTransactionObserver implements TransactionObserver {
+public class TransactionContextImpl implements TransactionContext {
+public class TransactionEnvironmentImpl implements TransactionEnvironment {
+		return serviceRegistry.getService( JdbcServices.class );
+		return serviceRegistry.getService( JtaPlatform.class );
+		return serviceRegistry.getService( TransactionFactory.class );
+public class AtomikosDataSourceConnectionProvider implements ConnectionProvider, ServiceRegistryAwareService {
+		AtomikosJtaPlatform jtaPlatform = (AtomikosJtaPlatform) ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();
+public class AtomikosJtaPlatform extends AbstractJtaPlatform implements Startable, Stoppable {
+	private static final Logger log = LoggerFactory.getLogger( AtomikosJtaPlatform.class );
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		Connection originalConnection = sfi().getServiceRegistry().getService( ConnectionProvider.class ).getConnection();
+		sfi().getServiceRegistry().getService( ConnectionProvider.class ).closeConnection( originalConnection );
-			assertEquals( "Bad conversion [" + sqle.getMessage() + "]", ConstraintViolationException.class , jdbcException.getClass() );
-	public static class StatsBatchBuilder extends BatchBuilder {
+	public static class StatsBatchBuilder extends BatchBuilderImpl {
-	private static class ConnectionCounter implements ConnectionObserver {
+public class BatchingTest extends UnitTestCase implements BatchKey {
-	public static class TestingBatchBuilder extends BatchBuilder {
+	public static class TestingBatchBuilder extends BatchBuilderImpl {
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		Transaction tx1 = sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().suspend();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().resume( tx1 );
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		tx1 = sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().suspend();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().resume( tx1 );
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		Transaction tx4 = sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().suspend();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		Transaction tx1 = sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().suspend();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().resume( tx1 );
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().resume( tx4 );
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		Transaction tx4 = sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().suspend();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		Transaction tx1 = sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().suspend();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().resume( tx1 );
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().resume( tx4 );
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().rollback();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+		sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+			sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+			sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+			sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().begin();
+			sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager().commit();
+public class TestExpectedUsage extends UnitTestCase {
+public class BasicDrivingTest extends UnitTestCase {
+				JtaPlatform instance = ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();
+				JtaPlatform instance = ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();
+public class ManagedDrivingTest extends UnitTestCase {
+		JtaPlatform instance = ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();

Lines added containing method: 207. Lines removed containing method: 77. Tot = 284
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* doIsolatedWork
* getSQLExceptionConverter
* getFactory
—————————
Method found in diff:	-	public static void doIsolatedWork(IsolatedWork work, SessionImplementor session) throws HibernateException {
-	public static void doIsolatedWork(IsolatedWork work, SessionImplementor session) throws HibernateException {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public SQLExceptionConverter getSQLExceptionConverter();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected SessionFactoryImplementor getFactory() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
