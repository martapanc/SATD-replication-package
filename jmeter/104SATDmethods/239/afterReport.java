/239/report.java
Satd-method: private static final boolean _time,  _timestamp, _success,
********************************************
********************************************
/239/After/Bug 25441  986530c4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* equalsIgnoreCase
* getJMeterProperties
* getProperty
* equals
—————————
Method found in diff:	+    public boolean equals(Object obj) {
+    public boolean equals(Object obj) {
+        if(this == obj) {
+            return true;
+        }
+        if((obj == null) || (obj.getClass() != this.getClass())) {
+            return false;
+        }
+        // We know we are comparing to another SampleSaveConfiguration
+        SampleSaveConfiguration s = (SampleSaveConfiguration)obj;
+        boolean primitiveValues = s.time == time &&
+            s.latency == latency && 
+            s.timestamp == timestamp &&
+            s.success == success &&
+            s.label == label &&
+            s.code == code &&
+            s.message == message &&
+            s.threadName == threadName &&
+            s.dataType == dataType &&
+            s.encoding == encoding &&
+            s.assertions == assertions &&
+            s.subresults == subresults &&
+            s.responseData == responseData &&
+            s.samplerData == samplerData &&
+            s.xml == xml &&
+            s.fieldNames == fieldNames &&
+            s.responseHeaders == responseHeaders &&
+            s.requestHeaders == requestHeaders &&
+            s.assertionsResultsToSave == assertionsResultsToSave &&
+            s.saveAssertionResultsFailureMessage == saveAssertionResultsFailureMessage &&
+            s.printMilliseconds == printMilliseconds &&
+            s.responseDataOnError == responseDataOnError &&
+            s.url == url &&
+            s.bytes == bytes &&
+            s.fileName == fileName &&
+            s.threadCounts == threadCounts;
+        
+        boolean stringValues = false;
+        if(primitiveValues) {
+            stringValues = s.delimiter == delimiter || (delimiter != null && delimiter.equals(s.delimiter));
+        }
+        boolean complexValues = false;
+        if(primitiveValues && stringValues) {
+            complexValues = s.formatter == formatter || (formatter != null && formatter.equals(s.formatter));
+        }
+        
+        return primitiveValues && stringValues && complexValues;
+    }

Lines added: 47. Lines removed: 0. Tot = 47
********************************************
********************************************
/239/After/Bug 43450  be023bb6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
+            s.sampleCount == sampleCount &&

Lines added: 1. Lines removed: 0. Tot = 1
********************************************
********************************************
/239/After/Bug 50203  55c15877_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 53765  472da151_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 54412  6445156a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 57025  b31d061c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 57182  f6b96cee_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 57193: 5d6aec5d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 58653  27745b72_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 58978  5486169c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 58991  e9482584_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 59064  e85059bc_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 59523  14d593ab_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 60106  58c262ee_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 60125  a7efa9ef_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* equalsIgnoreCase
* getJMeterProperties
* getProperty
* equals
—————————
Method found in diff:	public static Properties getJMeterProperties() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static String getProperty(String propName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean equals(Object obj) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/239/After/Bug 60229  bac01a62_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
+            s.sentBytes == sentBytes &&

Lines added: 1. Lines removed: 0. Tot = 1
********************************************
********************************************
/239/After/Bug 60564  88a09242_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 60830  1ee63ff4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
/239/After/Bug 60830  46234ac0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
_time, 

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
-            complexValues = Objects.equals(formatter, s.formatter);
+            complexValues = Objects.equals(dateFormat, s.dateFormat);

Lines added: 1. Lines removed: 1. Tot = 2
********************************************
********************************************
