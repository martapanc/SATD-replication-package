/78/report.java
Satd-method: public void run()
********************************************
********************************************
/78/After/Bug 60530  bd3b94bb5_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	public Sampler next() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void addIterationListener(LoopIterationListener lis) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isDone() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void initialize() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/After/Bug 60564  ea7682133_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                        if(log.isDebugEnabled() && onErrorStartNextLoop && !threadContext.isRestartNextLoop()) {
-                                log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
+                        if (log.isDebugEnabled() && onErrorStartNextLoop && !threadContext.isRestartNextLoop()) {
+                            log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
-                        
+
-                    log.info("Thread is done: " + threadName);
+                    log.info("Thread is done: {}", threadName);
-            log.info("Stopping Test: " + e.toString()); 
+            if (log.isInfoEnabled()) {
+                log.info("Stopping Test: {}", e.toString());
+            }
-            log.info("Stopping Test Now: " + e.toString());
+            if (log.isInfoEnabled()) {
+                log.info("Stopping Test Now: {}", e.toString());
+            }
-            log.info("Stop Thread seen for thread " + getThreadName()+", reason:"+ e.toString());
+            if (log.isInfoEnabled()) {
+                log.info("Stop Thread seen for thread {}, reason: {}", getThreadName(), e.toString());
+            }
-                log.info("Thread finished: " + threadName);
+                log.info("Thread finished: {}", threadName);

Lines added: 14. Lines removed: 8. Tot = 22
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private Arguments getVariables() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void error(SAXParseException ex) throws SAXException {
-            log.warn("Type=" + type + " " + ex);
+            if (log.isWarnEnabled()) {
+                log.warn("Type={}. {}", type, ex.toString());
+            }

Lines added: 3. Lines removed: 1. Tot = 4
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void clear() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SamplePackage configureSampler(Sampler sampler) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void done(SamplePackage pack) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void traverse(TestElementTraverser traverser) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String inthreadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static void initialize() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SSLContext getContext() throws GeneralSecurityException {
-                log.debug("Using shared SSL context for: "+Thread.currentThread().getName());
+                log.debug("Using shared SSL context for: {}", Thread.currentThread().getName());
-                log.debug("Creating threadLocal SSL context for: "+Thread.currentThread().getName());
+                log.debug("Creating threadLocal SSL context for: {}", Thread.currentThread().getName());
-            log.debug("Using threadLocal SSL context for: "+Thread.currentThread().getName());
+            log.debug("Using threadLocal SSL context for: {}", Thread.currentThread().getName());

Lines added: 3. Lines removed: 3. Tot = 6
********************************************
********************************************
/78/After/Bug 60797  520166ca4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SampleResult sample(Entry e) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/After/Bug 60812  3fa818235_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            stopTest();
+            shutdownTest();

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
