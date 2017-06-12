/227/report.java
Satd-method: public static SampleResult makeResultFromDelimitedString(String inputLine, SampleSaveConfiguration saveConfig) {
********************************************
********************************************
/227/Between/Bug 29481  5c126dc9_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
makeResultFromDelimitedString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* saveThreadName
* saveFileName
* saveBytes
* printMilliseconds
* saveLabel
* saveUrl
* saveSuccess
* saveTime
* setResponseCode
* saveAssertionResultsFailureMessage
* getDelimiter
* formatter
* warn
* setResultFileName
* setDataType
* nextToken
* setResponseMessage
* setSuccessful
* booleanValue
* getTime
* saveMessage
* setBytes
* setSampleLabel
* saveCode
* valueOf
* parse
* saveDataType
* saveThreadCounts
* parseInt
* parseLong
* setThreadName
* toString
* startsWith
********************************************
********************************************
/227/Between/Bug 40772  1f186f59_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		StringTokenizer splitter = new StringTokenizer(inputLine, _saveConfig.getDelimiter());
+		/*
+		 * Bug 40772: replaced StringTokenizer with String.split(), as the
+		 * former does not return empty tokens.
+		 */
+		// The \Q prefix is needed to ensure that meta-characters (e.g. ".") work.
+		String parts[]=inputLine.split("\\Q"+_saveConfig.getDelimiter());// $NON-NLS-1$
+		int i=0;
-				text = splitter.nextToken();
+				text = parts[i++];
-				text = splitter.nextToken();
+				text = parts[i++];
-				text = splitter.nextToken();
+				text = parts[i++];
-				text = splitter.nextToken();
+				text = parts[i++];
-				text = splitter.nextToken();
+				text = parts[i++];
-				text = splitter.nextToken();
+				text = parts[i++];
-				text = splitter.nextToken();
+				text = parts[i++];
-				text = splitter.nextToken();
+				text = parts[i++];
-				text = splitter.nextToken();
+				text = parts[i++];
-				text = splitter.nextToken();
+				text = parts[i++];
-                text = splitter.nextToken();
+                text = parts[i++];
-                text = splitter.nextToken();
+                text = parts[i++];
-                text = splitter.nextToken();
+                text = parts[i++];
-                text = splitter.nextToken();
+                text = parts[i++];

Lines added: 21. Lines removed: 15. Tot = 36
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
makeResultFromDelimitedString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* saveThreadName
* saveFileName
* saveBytes
* printMilliseconds
* saveLabel
* saveUrl
* saveSuccess
* saveTime
* setResponseCode
* saveAssertionResultsFailureMessage
* getDelimiter
* formatter
* warn
* setResultFileName
* setDataType
* nextToken
* setResponseMessage
* setSuccessful
* booleanValue
* getTime
* saveMessage
* setBytes
* setSampleLabel
* saveCode
* valueOf
* parse
* saveDataType
* saveThreadCounts
* parseInt
* parseLong
* setThreadName
* toString
* startsWith
********************************************
********************************************
/227/Between/Bug 41277  fc0611c0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+            if (saveConfig.saveLatency()) {
+                text = parts[i++];
+                result.setLatency(Long.parseLong(text));
+            }
+
+            if (saveConfig.saveEncoding()) {
+                text = parts[i++];
+                result.setEncodingAndType(text);
+            }
+

Lines added: 10. Lines removed: 0. Tot = 10
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
makeResultFromDelimitedString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* saveThreadName
* saveFileName
* saveBytes
* printMilliseconds
* saveLabel
* saveUrl
* saveSuccess
* saveTime
* setResponseCode
* saveAssertionResultsFailureMessage
* getDelimiter
* formatter
* warn
* setResultFileName
* setDataType
* nextToken
* setResponseMessage
* setSuccessful
* booleanValue
* getTime
* saveMessage
* setBytes
* setSampleLabel
* saveCode
* valueOf
* parse
* saveDataType
* saveThreadCounts
* parseInt
* parseLong
* setThreadName
* toString
* startsWith
********************************************
********************************************
/227/Between/Bug 42919  1b221e05_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
makeResultFromDelimitedString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* saveThreadName
* saveFileName
* saveBytes
* printMilliseconds
* saveLabel
* saveUrl
* saveSuccess
* saveTime
* setResponseCode
* saveAssertionResultsFailureMessage
* getDelimiter
* formatter
* warn
* setResultFileName
* setDataType
* nextToken
* setResponseMessage
* setSuccessful
* booleanValue
* getTime
* saveMessage
* setBytes
* setSampleLabel
* saveCode
* valueOf
* parse
* saveDataType
* saveThreadCounts
* parseInt
* parseLong
* setThreadName
* toString
* startsWith
********************************************
********************************************
/227/Between/Bug 43430  9a3d4075_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
makeResultFromDelimitedString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* saveThreadName
* saveFileName
* saveBytes
* printMilliseconds
* saveLabel
* saveUrl
* saveSuccess
* saveTime
* setResponseCode
* saveAssertionResultsFailureMessage
* getDelimiter
* formatter
* warn
* setResultFileName
* setDataType
* nextToken
* setResponseMessage
* setSuccessful
* booleanValue
* getTime
* saveMessage
* setBytes
* setSampleLabel
* saveCode
* valueOf
* parse
* saveDataType
* saveThreadCounts
* parseInt
* parseLong
* setThreadName
* toString
* startsWith
********************************************
********************************************
/227/Between/Bug 43450  6f9771e8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
makeResultFromDelimitedString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* saveThreadName
* saveFileName
* saveBytes
* printMilliseconds
* saveLabel
* saveUrl
* saveSuccess
* saveTime
* setResponseCode
* saveAssertionResultsFailureMessage
* getDelimiter
* formatter
* warn
* setResultFileName
* setDataType
* nextToken
* setResponseMessage
* setSuccessful
* booleanValue
* getTime
* saveMessage
* setBytes
* setSampleLabel
* saveCode
* valueOf
* parse
* saveDataType
* saveThreadCounts
* parseInt
* parseLong
* setThreadName
* toString
* startsWith
********************************************
********************************************
/227/Between/Bug 43450  be023bb6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
makeResultFromDelimitedString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* saveThreadName
* saveFileName
* saveBytes
* printMilliseconds
* saveLabel
* saveUrl
* saveSuccess
* saveTime
* setResponseCode
* saveAssertionResultsFailureMessage
* getDelimiter
* formatter
* warn
* setResultFileName
* setDataType
* nextToken
* setResponseMessage
* setSuccessful
* booleanValue
* getTime
* saveMessage
* setBytes
* setSampleLabel
* saveCode
* valueOf
* parse
* saveDataType
* saveThreadCounts
* parseInt
* parseLong
* setThreadName
* toString
* startsWith
********************************************
********************************************
