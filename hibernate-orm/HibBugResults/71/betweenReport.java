71/report.java
Satd-method: private static final Log log = LogFactory.getLog(StatisticsImpl.class);
********************************************
********************************************
71/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
log 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getLog
********************************************
********************************************
71/Between/ HHH-5986  0816d00e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
log 
-				log.debug( "could not log warnings", sqle );
+		 * Delegate to log common details of a {@link SQLWarning warning}
+			log.debug( "could not log warnings", sqle );
+			log.debug( "could not log warnings", sqlException );
+								StringHelper.toLowerCase( catalog ),
-	public static final Logger log = LoggerFactory.getLogger(JDBCExceptionReporter.class);
-			log.debug( "could not log warnings", sqle );
-			log.debug( "could not log warnings", sqle );
-		 * Delegate to log common details of a {@link SQLWarning warning}

Lines added containing method: 4. Lines removed containing method: 5. Tot = 9
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getLog
********************************************
********************************************
71/Between/ HHH-6033  f93d1412_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
log 
-	 * log in info level the main statistics

Lines added containing method: 0. Lines removed containing method: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getLog
********************************************
********************************************
