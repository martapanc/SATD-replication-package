/226/report.java
Satd-method: static {
********************************************
********************************************
/226/After/Bug 60106  58c262ee_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        _connectTime     = TRUE.equalsIgnoreCase(props.getProperty(CONNECT_TIME_PROP, FALSE));
+        _connectTime     = TRUE.equalsIgnoreCase(props.getProperty(CONNECT_TIME_PROP, TRUE));

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
static 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* equalsIgnoreCase
* getJMeterProperties
* getProperty
* equals
—————————
Method found in diff:	public boolean equals(Object obj) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/226/After/Bug 60229  bac01a62_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
static 
+    private static final String SAVE_SENT_BYTES_PROP = "jmeter.save.saveservice.sent_bytes"; // $NON_NLS-1$
+    private static final boolean _sentBytes;
+    public static final String CSV_SENT_BYTES = "sentBytes"; // $NON-NLS-1$
+    private static final String ATT_SENT_BYTES        = "sby"; //$NON-NLS-1$
+    private static final String NODE_SENT_BYTES = "sentBytes"; // $NON-NLS-1$

Lines added containing method: 5. Lines removed containing method: 0. Tot = 5
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* equalsIgnoreCase
* getJMeterProperties
* getProperty
* equals
—————————
Method found in diff:	public boolean equals(Object obj) {
+            s.sentBytes == sentBytes &&

Lines added: 1. Lines removed: 0. Tot = 1
********************************************
********************************************
/226/After/Bug 60564  88a09242_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
static 
-    private static final long serialVersionUID = 251L;
+    private static final long serialVersionUID = 252L;
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AsynchSampleSender.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BatchSampleSender.class);
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
-    private static final long serialVersionUID = -5556040298982085715L;
+    private static final long serialVersionUID = 1L;
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(DataStrippingSampleSender.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(DiskStoreSampleSender.class);
-    private static final long serialVersionUID = 252L;
+    private static final long serialVersionUID = 253L;
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HoldSampleSender.class);
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RemoteListenerWrapper.class);
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RemoteSampleListenerWrapper.class);
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RemoteTestListenerWrapper.class);
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SampleEvent.class);
-    private static final long serialVersionUID = 232L;
+    private static final long serialVersionUID = 233L;
-    private static final long serialVersionUID = 7L;
+    private static final long serialVersionUID = 8L;
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SampleSaveConfiguration.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SampleSenderFactory.class);
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(StandardSampleSender.class);
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(StatisticalSampleSender.class);

Lines added containing method: 25. Lines removed containing method: 25. Tot = 50
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* equalsIgnoreCase
* getJMeterProperties
* getProperty
* equals
—————————
Method found in diff:	public boolean equals(Object obj) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/226/After/Bug 60830  1ee63ff4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
static 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* equalsIgnoreCase
* getJMeterProperties
* getProperty
* equals
—————————
Method found in diff:	public boolean equals(Object obj) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/226/After/Bug 60830  46234ac0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        TIMESTAMP_FORMAT = props.getProperty(TIME_STAMP_FORMAT_PROP, MILLISECONDS);
+        String temporaryTimestampFormat = props.getProperty(TIME_STAMP_FORMAT_PROP, MILLISECONDS);
-        PRINT_MILLISECONDS = MILLISECONDS.equalsIgnoreCase(TIMESTAMP_FORMAT);
+        PRINT_MILLISECONDS = MILLISECONDS.equalsIgnoreCase(temporaryTimestampFormat);
-        // Prepare for a pretty date
-        // FIXME Can TIMESTAMP_FORMAT be null ? it does not appear to me .
-        if (!PRINT_MILLISECONDS && !NONE.equalsIgnoreCase(TIMESTAMP_FORMAT) && (TIMESTAMP_FORMAT != null)) {
-            DATE_FORMATTER = new SimpleDateFormat(TIMESTAMP_FORMAT);
+        if (!PRINT_MILLISECONDS && !NONE.equalsIgnoreCase(temporaryTimestampFormat)) {
+            DATE_FORMAT = validateFormat(temporaryTimestampFormat);
-            DATE_FORMATTER = null;
+            DATE_FORMAT = null;
-        TIMESTAMP = !NONE.equalsIgnoreCase(TIMESTAMP_FORMAT);// reversed compare allows for null
+        TIMESTAMP = !NONE.equalsIgnoreCase(temporaryTimestampFormat);// reversed compare allows for null

Lines added: 6. Lines removed: 8. Tot = 14
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
static 
-    private static final String TIMESTAMP_FORMAT;
-    private static final DateFormat DATE_FORMATTER;
+    private static final String DATE_FORMAT;
+    static final String CONFIG_GETTER_PREFIX = "save";  // $NON-NLS-1$
+    static final String CONFIG_SETTER_PREFIX = "set";  // $NON-NLS-1$
+    public static final List<String> SAVE_CONFIG_NAMES = Collections.unmodifiableList(Arrays.asList(new String[]{
-    static final String CONFIG_GETTER_PREFIX = "save";  // $NON-NLS-1$
-    static final String CONFIG_SETTER_PREFIX = "set";  // $NON-NLS-1$
-    public static final List<String> SAVE_CONFIG_NAMES = Collections.unmodifiableList(Arrays.asList(new String[]{
+    private static String validateFormat(String temporaryTimestampFormat) {
-                                // so it's OK to use a static DateFormat

Lines added containing method: 5. Lines removed containing method: 6. Tot = 11
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* equalsIgnoreCase
* getJMeterProperties
* getProperty
* equals
—————————
Method found in diff:	public boolean equals(Object obj) {
-            complexValues = Objects.equals(formatter, s.formatter);
+            complexValues = Objects.equals(dateFormat, s.dateFormat);

Lines added: 1. Lines removed: 1. Tot = 2
********************************************
********************************************
