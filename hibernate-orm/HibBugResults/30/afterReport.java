30/report.java
Satd-method: public abstract class TransactionHelper {
********************************************
********************************************
30/After/ HHH-5985  21cc90fb_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-public abstract class TransactionHelper {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
class 
-public abstract class TransactionHelper {
-		class WorkToDo implements Work {
-public class MultipleHiLoPerTableGenerator 
+public class MultipleHiLoPerTableGenerator implements PersistentIdentifierGenerator, Configurable {
+	private static final Logger log = LoggerFactory.getLogger( MultipleHiLoPerTableGenerator.class );
+						.getService( JdbcServices.class )
-public class TableGenerator extends TransactionHelper
+public class TableGenerator implements PersistentIdentifierGenerator, Configurable {
+				.getService( JdbcServices.class )
-public class TableGenerator extends TransactionHelper implements PersistentIdentifierGenerator, Configurable {
+public class TableGenerator implements PersistentIdentifierGenerator, Configurable {
+				.getService( JdbcServices.class )
-public class TableStructure extends TransactionHelper implements DatabaseStructure {
+public class TableStructure implements DatabaseStructure {
+										.getService( JdbcServices.class )

Lines added containing method: 9. Lines removed containing method: 6. Tot = 15
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* doIsolatedWork
* getSQLExceptionConverter
* getFactory
********************************************
********************************************
