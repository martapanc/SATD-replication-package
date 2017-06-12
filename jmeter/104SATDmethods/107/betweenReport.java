/107/report.java
Satd-method: public void start(String[] args) {
********************************************
********************************************
/107/Between/Bug 50659  dc1a76af6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                startServer(JMeterUtils.getPropDefault("server_port", 0));// $NON-NLS-1$
+                try {
+                    RemoteJMeterEngineImpl.startServer(JMeterUtils.getPropDefault("server_port", 0)); // $NON-NLS-1$
+                } catch (Exception ex) {
+                    System.err.println("Server failed to start: "+ex);
+                    log.error("Giving up, as server failed with:", ex);
+                    throw ex;
+                }

Lines added: 7. Lines removed: 1. Tot = 8
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 51091  5f7112827_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 51831  04763b7d0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 52029  979329621_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 52346  ba3cdcaf4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(
+            daemon.start();

Lines added containing method: 1. Lines removed containing method: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 52934  1152bb1b5_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 54152  849643223_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 54152  8642a7617_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 54414  4a2b1d231_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 55334  90d52dfec_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 55512  be7f7415c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 57193: 65bd9c284_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 57365  321e520fe_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 57365  b74853f78_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 57500  022af006b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(
+                distributedRunner.start();

Lines added containing method: 1. Lines removed containing method: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 57605  40b3221e7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 57821  480c3656b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 58653  27745b727_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                    CLOption rem=parser.getArgumentById(REMOTE_OPT_PARAM);
-                    if (rem==null) { rem=parser.getArgumentById(REMOTE_OPT); }
-                    CLOption jtl = parser.getArgumentById(LOGFILE_OPT);
-                    String jtlFile = null;
-                    if (jtl != null){
-                        jtlFile=processLAST(jtl.getArgument(), ".jtl"); // $NON-NLS-1$
+                    CLOption testReportOpt = parser
+                            .getArgumentById(REPORT_GENERATING_OPT);
+
+                    if (testReportOpt != null) {
+                        String reportFile = testReportOpt.getArgument();
+                        ReportGenerator generator = new ReportGenerator(
+                                reportFile, null);
+                        generator.generate();
+                    } else {
+                        CLOption rem = parser.getArgumentById(REMOTE_OPT_PARAM);
+                        if (rem == null) {
+                            rem = parser.getArgumentById(REMOTE_OPT);
+                        }
+                        CLOption jtl = parser.getArgumentById(LOGFILE_OPT);
+                        String jtlFile = null;
+                        if (jtl != null) {
+                            jtlFile = processLAST(jtl.getArgument(), ".jtl"); // $NON-NLS-1$
+                        }
+                        CLOption reportAtEndOpt = parser.getArgumentById(REPORT_AT_END_OPT);
+                        if(reportAtEndOpt != null) {
+                            if(jtlFile == null) {
+                                throw new IllegalUserActionException("Option -"+REPORT_AT_END_OPT+" requires -"+LOGFILE_OPT + " option");
+                            }
+                        }
+                        startNonGui(testFile, jtlFile, rem, reportAtEndOpt != null);
+                        startOptionalServers();
-                    startNonGui(testFile, jtlFile, rem);
-                    startOptionalServers();

Lines added: 26. Lines removed: 8. Tot = 34
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 58781  04ba97a97_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+            } else if (parser.getArgumentById(OPTIONS_OPT) != null) {
+                System.out.println(CLUtil.describeOptions(options).toString());

Lines added: 2. Lines removed: 0. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 58986  35cd20998_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                        // We check folder does not exist or it is empty
-                        if(!reportOutputFolderAsFile.exists() || 
-                                // folder exists but is empty
-                                (reportOutputFolderAsFile.isDirectory() && reportOutputFolderAsFile.listFiles().length == 0)) {
-                            if(!reportOutputFolderAsFile.exists()) {
-                                // Report folder does not exist, we check we can create it 
-                                if(!reportOutputFolderAsFile.mkdirs()) {
-                                    throw new IllegalArgumentException("Cannot create output report to:'"
-                                            +reportOutputFolderAsFile.getAbsolutePath()+"' as I was not able to create it");
-                                }
-                            }
-                            log.info("Setting property '"+JMETER_REPORT_OUTPUT_DIR_PROPERTY+"' to:'"+reportOutputFolderAsFile.getAbsolutePath()+"'");
-                            JMeterUtils.setProperty(JMETER_REPORT_OUTPUT_DIR_PROPERTY, 
-                                    reportOutputFolderAsFile.getAbsolutePath());                        
-                        } else {
-                            throw new IllegalArgumentException("Cannot output report to:'"
-                                    +reportOutputFolderAsFile.getAbsolutePath()+"' as it would overwrite existing non empty folder");
-                        }
+
+                        JOrphanUtils.canSafelyWriteToFolder(reportOutputFolderAsFile);
+                        log.info("Setting property '"+JMETER_REPORT_OUTPUT_DIR_PROPERTY+"' to:'"+reportOutputFolderAsFile.getAbsolutePath()+"'");
+                        JMeterUtils.setProperty(JMETER_REPORT_OUTPUT_DIR_PROPERTY, 
+                                reportOutputFolderAsFile.getAbsolutePath());                        

Lines added: 5. Lines removed: 18. Tot = 23
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 58986  a75c821ad_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+                    CLOption reportOutputFolderOpt = parser
+                            .getArgumentById(REPORT_OUTPUT_FOLDER_OPT);
+                    if(reportOutputFolderOpt != null) {
+                        String reportOutputFolder = parser.getArgumentById(REPORT_OUTPUT_FOLDER_OPT).getArgument();
+                        File reportOutputFolderAsFile = new File(reportOutputFolder);
+                        // We check folder does not exist or it is empty
+                        if(!reportOutputFolderAsFile.exists() || 
+                                // folder exists but is empty
+                                (reportOutputFolderAsFile.isDirectory() && reportOutputFolderAsFile.listFiles().length == 0)) {
+                            if(!reportOutputFolderAsFile.exists()) {
+                                // Report folder does not exist, we check we can create it 
+                                if(!reportOutputFolderAsFile.mkdirs()) {
+                                    throw new IllegalArgumentException("Cannot create output report to:'"
+                                            +reportOutputFolderAsFile.getAbsolutePath()+"' as I was not able to create it");
+                                }
+                            }
+                            log.info("Setting property '"+JMETER_REPORT_OUTPUT_DIR_PROPERTY+"' to:'"+reportOutputFolderAsFile.getAbsolutePath()+"'");
+                            JMeterUtils.setProperty(JMETER_REPORT_OUTPUT_DIR_PROPERTY, 
+                                    reportOutputFolderAsFile.getAbsolutePath());                        
+                        } else {
+                            throw new IllegalArgumentException("Cannot output report to:'"
+                                    +reportOutputFolderAsFile.getAbsolutePath()+"' as it would overwrite existing non empty folder");
+                        }
+                    }
+                    

Lines added: 25. Lines removed: 0. Tot = 25
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 58987  9ee466a0e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            log.fatalError("An error occurred: ",e);
+            log.fatalError("An error occurred: "+e.getMessage(),e);

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 59391  135483ac0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/Between/Bug 60053  f464c9baf_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
start(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* split
* getLocalHostName
* getJMeterHome
* info
* getErrorString
* printStackTrace
* getLocalHostIP
* fatalError
* format
* getArgument
* exit
* getDisplayName
* setProperty
* getResourceFileAsText
* isDebugEnabled
* getPropDefault
* getLocalHostFullName
* println
* describeOptions
* getJMeterCopyright
* getCanonicalPath
* getProperty
* debug
* getJMeterVersion
* getMessage
* getDefault
* currentTimeMillis
* equals
* toString
* getRecentFile
* getLocale
* getArgumentById
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
