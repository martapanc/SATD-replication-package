/232/report.java
Satd-method: public SampleResult sample(Entry entry) {
********************************************
********************************************
/232/Between/Bug 55403  8d0f4b0f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        SystemCommand nativeCommand = new SystemCommand(directory, env, getStdin(), getStdout(), getStderr());
+        SystemCommand nativeCommand = new SystemCommand(directory, getTimeout(), env, getStdin(), getStdout(), getStderr());

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* sampleEnd
* run
* put
* getBytes
* getExecutionEnvironment
* add
* sampleStart
* getArgument
* setResponseCode
* setDataType
* setSuccessful
* setResponseMessage
* isDebugEnabled
* getDefaultBase
* setSamplerData
* get
* getOutResult
* setSampleLabel
* debug
* getAbsolutePath
* isEmpty
* getArgumentCount
* setResponseData
* getPropertyAsString
* toString
* append
********************************************
********************************************
/232/Between/Bug 55403  98e59758_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        SystemCommand nativeCommand = new SystemCommand(directory, getTimeout(), env, getStdin(), getStdout(), getStderr());
+        SystemCommand nativeCommand = new SystemCommand(directory, getTimeout(), POLL_INTERVAL, env, getStdin(), getStdout(), getStderr());

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* sampleEnd
* run
* put
* getBytes
* getExecutionEnvironment
* add
* sampleStart
* getArgument
* setResponseCode
* setDataType
* setSuccessful
* setResponseMessage
* isDebugEnabled
* getDefaultBase
* setSamplerData
* get
* getOutResult
* setSampleLabel
* debug
* getAbsolutePath
* isEmpty
* getArgumentCount
* setResponseData
* getPropertyAsString
* toString
* append
********************************************
********************************************
/232/Between/Bug 57193: dd30d617_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* sampleEnd
* run
* put
* getBytes
* getExecutionEnvironment
* add
* sampleStart
* getArgument
* setResponseCode
* setDataType
* setSuccessful
* setResponseMessage
* isDebugEnabled
* getDefaultBase
* setSamplerData
* get
* getOutResult
* setSampleLabel
* debug
* getAbsolutePath
* isEmpty
* getArgumentCount
* setResponseData
* getPropertyAsString
* toString
* append
********************************************
********************************************
