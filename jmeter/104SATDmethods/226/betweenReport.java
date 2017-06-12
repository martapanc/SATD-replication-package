/226/report.java
Satd-method: static {
********************************************
********************************************
/226/Between/Bug 50203  55c15877_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        if (!CharUtils.isAsciiPrintable(ch)){
+        if (ch != '\t' && !CharUtils.isAsciiPrintable(ch)){

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
/226/Between/Bug 53765  472da151_diff.java
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
/226/Between/Bug 54412  6445156a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        String howToSave = props.getProperty(OUTPUT_FORMAT_PROP, XML);
+        String howToSave = props.getProperty(OUTPUT_FORMAT_PROP, CSV);

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
static 
-    //NOTUSED private static final String CSV = "csv"; // $NON_NLS-1$
+    private static final String CSV = "csv"; // $NON_NLS-1$

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
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
/226/Between/Bug 57025  b31d061c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        _threadCounts=TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_COUNTS, FALSE));
+        _threadCounts=TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_COUNTS, TRUE));

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
/226/Between/Bug 57182  f6b96cee_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        _idleTime=TRUE.equalsIgnoreCase(props.getProperty(SAVE_IDLE_TIME, FALSE));
+        _idleTime=TRUE.equalsIgnoreCase(props.getProperty(SAVE_IDLE_TIME, TRUE));

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
/226/Between/Bug 57193: 5d6aec5d_diff.java
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
/226/Between/Bug 58653  27745b72_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
static 
-    private static final String ASSERTION_RESULTS_FAILURE_MESSAGE_PROP =
+    public static final String ASSERTION_RESULTS_FAILURE_MESSAGE_PROP =

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
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
/226/Between/Bug 58978  5486169c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                ASSERTION_RESULTS_FAILURE_MESSAGE_PROP, FALSE));
+                ASSERTION_RESULTS_FAILURE_MESSAGE_PROP, TRUE));

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
/226/Between/Bug 58991  e9482584_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        _fieldNames = TRUE.equalsIgnoreCase(props.getProperty(PRINT_FIELD_NAMES_PROP, FALSE));
+        _fieldNames = TRUE.equalsIgnoreCase(props.getProperty(PRINT_FIELD_NAMES_PROP, TRUE));

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
/226/Between/Bug 59064  e85059bc_diff.java
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
/226/Between/Bug 59523  14d593ab_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+        // FIXME Can _timeStampFormat be null ? it does not appear to me .

Lines added: 1. Lines removed: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
static 
-    private static final String MILLISECONDS = "ms"; // $NON_NLS-1$
+    public static final String MILLISECONDS = "ms"; // $NON_NLS-1$
-    private static final String NONE = "none"; // $NON_NLS-1$
+    public static final String NONE = "none"; // $NON_NLS-1$

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
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
/226/Between/Bug 60125  a7efa9ef_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        String dlm = props.getProperty(DEFAULT_DELIMITER_PROP, DEFAULT_DELIMITER);
-        if (dlm.equals("\\t")) {// Make it easier to enter a tab (can use \<tab> but that is awkward)
-            dlm="\t";
-        }
-
-        if (dlm.length() != 1){
-            throw new JMeterError("Delimiter '"+dlm+"' must be of length 1.");
-        }
+        String dlm = JMeterUtils.getDelimiter(props.getProperty(DEFAULT_DELIMITER_PROP, DEFAULT_DELIMITER));

Lines added: 1. Lines removed: 8. Tot = 9
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
static 
-    private static final String DEFAULT_DELIMITER_PROP = "jmeter.save.saveservice.default_delimiter"; // $NON_NLS-1$
+    public static final String DEFAULT_DELIMITER_PROP = "jmeter.save.saveservice.default_delimiter"; // $NON_NLS-1$
-    private static final String DEFAULT_DELIMITER = ","; // $NON_NLS-1$
+    public static final String DEFAULT_DELIMITER = ","; // $NON_NLS-1$

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
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
