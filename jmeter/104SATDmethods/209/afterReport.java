/209/report.java
Satd-method: public void valueChanged(TreeSelectionEvent e) {
********************************************
********************************************
/209/After/Bug 42246  90d8067c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
********************************************
********************************************
/209/After/Bug 42246  c592cc4c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
********************************************
********************************************
/209/After/Bug 52003  3a87c8dc_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
********************************************
********************************************
/209/After/Bug 52022  11668430_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
********************************************
********************************************
/209/After/Bug 52217  9845e49b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
********************************************
********************************************
/209/After/Bug 52266  c0f98a93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
+        DefaultMutableTreeNode node = null;
+        synchronized (this) {
+            node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
+        }

Lines added: 4. Lines removed: 1. Tot = 5
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
********************************************
********************************************
/209/After/Bug 52694  ea4d5cab_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/209/After/Bug 54226  b85f6c38_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                if ((SampleResult.TEXT).equals(sampleResult.getDataType())){
+                if (isTextDataType(sampleResult)){

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
********************************************
********************************************
/209/After/Bug 55597  08efaaad_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
—————————
Method found in diff:	public String getDataType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getBytes() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public long getStartTime() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isFailure() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getRequestHeaders() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getThreadName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public long getTime() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getResponseMessage() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isError() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getResponseHeaders() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFailureMessage() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getSamplerData() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getResponseCode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/209/After/Bug 56228  4321ec75_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
********************************************
********************************************
/209/After/Bug 59102  8cc1b70b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
********************************************
********************************************
/209/After/Bug 60542  ac1f2c21_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
********************************************
********************************************
/209/After/Bug 60564  61304dee_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
********************************************
********************************************
/209/After/Bug 60583  eb234b7a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
********************************************
********************************************
