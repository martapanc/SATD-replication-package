/107/report.java
Satd-method: public void start(String[] args) {
********************************************
********************************************
/107/After/Bug 59995  03a2728d2_diff.java
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
Method found in diff:	public static synchronized String getLocalHostName(){

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static String getJMeterHome() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static synchronized String getLocalHostIP(){

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static Object setProperty(String propName, String propValue) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static String getResourceFileAsText(String name) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static int getPropDefault(String propName, int defaultVal) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static synchronized String getLocalHostFullName(){

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static String getJMeterCopyright() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static String getProperty(String propName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static String getJMeterVersion() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static Locale getLocale() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/After/Bug 60564  5f0651b4a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            log.info(JMeterUtils.getJMeterCopyright());
-            log.info("Version " + JMeterUtils.getJMeterVersion());
-            logProperty("java.version"); //$NON-NLS-1$
-            logProperty("java.vm.name"); //$NON-NLS-1$
-            logProperty("os.name"); //$NON-NLS-1$
-            logProperty("os.arch"); //$NON-NLS-1$
-            logProperty("os.version"); //$NON-NLS-1$
-            logProperty("file.encoding"); // $NON-NLS-1$
-            log.info("Max memory     ="+ Runtime.getRuntime().maxMemory());
-            log.info("Available Processors ="+ Runtime.getRuntime().availableProcessors());
-            log.info("Default Locale=" + Locale.getDefault().getDisplayName());
-            log.info("JMeter  Locale=" + JMeterUtils.getLocale().getDisplayName());
-            log.info("JMeterHome="     + JMeterUtils.getJMeterHome());
-            logProperty("user.dir","  ="); //$NON-NLS-1$
-            log.info("PWD       ="+new File(".").getCanonicalPath());//$NON-NLS-1$
-            log.info("IP: "+JMeterUtils.getLocalHostIP()
-                    +" Name: "+JMeterUtils.getLocalHostName()
-                    +" FullName: "+JMeterUtils.getLocalHostFullName());
+            if (log.isInfoEnabled()) {
+                log.info(JMeterUtils.getJMeterCopyright());
+                log.info("Version {}", JMeterUtils.getJMeterVersion());
+                log.info("java.version={}", System.getProperty("java.version"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("java.vm.name={}", System.getProperty("java.vm.name"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("os.name={}", System.getProperty("os.name"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("os.arch={}", System.getProperty("os.arch"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("os.version={}", System.getProperty("os.version"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("file.encoding={}", System.getProperty("file.encoding"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("Max memory     ={}", Runtime.getRuntime().maxMemory());
+                log.info("Available Processors ={}", Runtime.getRuntime().availableProcessors());
+                log.info("Default Locale={}", Locale.getDefault().getDisplayName());
+                log.info("JMeter  Locale={}", JMeterUtils.getLocale().getDisplayName());
+                log.info("JMeterHome={}", JMeterUtils.getJMeterHome());
+                log.info("user.dir  ={}", System.getProperty("user.dir"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("PWD       ={}", new File(".").getCanonicalPath());//$NON-NLS-1$
+                log.info("IP: {} Name: {} FullName: {}", JMeterUtils.getLocalHostIP(), JMeterUtils.getLocalHostName(),
+                        JMeterUtils.getLocalHostFullName());
+            }
-            log.fatalError("An error occurred: ",e);
+            log.error("An error occurred: ", e);

Lines added: 20. Lines removed: 19. Tot = 39
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
Method found in diff:	public void exit() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static void println(String str) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/107/After/Bug 60589  22288a776_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in 