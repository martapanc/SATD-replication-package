/207/report.java
Satd-method: public SampleResult sample(Entry e) {
********************************************
********************************************
/207/Between/Bug 49552  cf33f272_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+            instance.setHeaderFields((CollectionProperty)getProperty(SmtpSampler.HEADER_FIELDS));

Lines added: 1. Lines removed: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setPassword
* setEmlMessage
* setUseStartTLS
* setReceiverTo
* trim
* getCount
* printStackTrace
* matches
* execute
* getInputStream
* writeTo
* setSuccessful
* setTrustAllCerts
* setReceiverCC
* setSynchronousMode
* getMessage
* setBody
* setUseSSL
* setMbProvider
* equals
* setUseEmlMessage
* setSmtpPort
* toString
* setSender
* setTrustStoreToUse
* setSubject
* sampleEnd
* addAttachment
* getCause
* getBytes
* split
* setSmtpServer
* setResponseCodeOK
* setEnforceStartTLS
* add
* sampleStart
* read
* setResponseCode
* warn
* setDataType
* setResponseMessage
* getTime
* prepareMessage
* setUseLocalTrustStore
* setBytes
* setSampleLabel
* setUsername
* length
* setUseAuthentication
* getServerResponse
* setResponseData
* setReceiverBCC
* append
********************************************
********************************************
/207/Between/Bug 49603  91e79e3e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        instance.setUseSSL(getPropertyAsBoolean(USE_SSL));
-        instance.setUseStartTLS(getPropertyAsBoolean(USE_STARTTLS));
-        instance.setTrustAllCerts(getPropertyAsBoolean(SSL_TRUST_ALL_CERTS));
-        instance.setEnforceStartTLS(getPropertyAsBoolean(ENFORCE_STARTTLS));
+        instance.setUseSSL(getPropertyAsBoolean(SecuritySettingsPanel.USE_SSL));
+        instance.setUseStartTLS(getPropertyAsBoolean(SecuritySettingsPanel.USE_STARTTLS));
+        instance.setTrustAllCerts(getPropertyAsBoolean(SecuritySettingsPanel.SSL_TRUST_ALL_CERTS));
+        instance.setEnforceStartTLS(getPropertyAsBoolean(SecuritySettingsPanel.ENFORCE_STARTTLS));
-        instance.setUseLocalTrustStore(getPropertyAsBoolean(USE_LOCAL_TRUSTSTORE));
-        instance.setTrustStoreToUse(getPropertyAsString(TRUSTSTORE_TO_USE));
+        instance.setUseLocalTrustStore(getPropertyAsBoolean(SecuritySettingsPanel.USE_LOCAL_TRUSTSTORE));
+        instance.setTrustStoreToUse(getPropertyAsString(SecuritySettingsPanel.TRUSTSTORE_TO_USE));

Lines added: 6. Lines removed: 6. Tot = 12
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setPassword
* setEmlMessage
* setUseStartTLS
* setReceiverTo
* trim
* getCount
* printStackTrace
* matches
* execute
* getInputStream
* writeTo
* setSuccessful
* setTrustAllCerts
* setReceiverCC
* setSynchronousMode
* getMessage
* setBody
* setUseSSL
* setMbProvider
* equals
* setUseEmlMessage
* setSmtpPort
* toString
* setSender
* setTrustStoreToUse
* setSubject
* sampleEnd
* addAttachment
* getCause
* getBytes
* split
* setSmtpServer
* setResponseCodeOK
* setEnforceStartTLS
* add
* sampleStart
* read
* setResponseCode
* warn
* setDataType
* setResponseMessage
* getTime
* prepareMessage
* setUseLocalTrustStore
* setBytes
* setSampleLabel
* setUsername
* length
* setUseAuthentication
* getServerResponse
* setResponseData
* setReceiverBCC
* append
********************************************
********************************************
/207/Between/Bug 49622  270ae748_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-
-            instance.setSubject(getPropertyAsString(SUBJECT)
-                    + (getPropertyAsBoolean(INCLUDE_TIMESTAMP) ?
-                            " <<< current timestamp: " + new Date().getTime() + " >>>"
-                            : ""
-                       ));
+            
+            if(getPropertyAsBoolean(SUPPRESS_SUBJECT)){
+            	instance.setSubject(null);
+            }else{
+            	String subject = getPropertyAsString(SUBJECT);
+            	if (getPropertyAsBoolean(INCLUDE_TIMESTAMP)){
+            		StringBuffer sb = new StringBuffer(subject);
+            		sb.append(" <<< current timestamp: ");
+            		sb.append(new Date().getTime());
+            		sb.append(" >>>");
+            		subject = sb.toString();
+            	}
+            	instance.setSubject(subject);
+            }

Lines added: 14. Lines removed: 6. Tot = 20
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setPassword
* setEmlMessage
* setUseStartTLS
* setReceiverTo
* trim
* getCount
* printStackTrace
* matches
* execute
* getInputStream
* writeTo
* setSuccessful
* setTrustAllCerts
* setReceiverCC
* setSynchronousMode
* getMessage
* setBody
* setUseSSL
* setMbProvider
* equals
* setUseEmlMessage
* setSmtpPort
* toString
* setSender
* setTrustStoreToUse
* setSubject
* sampleEnd
* addAttachment
* getCause
* getBytes
* split
* setSmtpServer
* setResponseCodeOK
* setEnforceStartTLS
* add
* sampleStart
* read
* setResponseCode
* warn
* setDataType
* setResponseMessage
* getTime
* prepareMessage
* setUseLocalTrustStore
* setBytes
* setSampleLabel
* setUsername
* length
* setUseAuthentication
* getServerResponse
* setResponseData
* setReceiverBCC
* append
********************************************
********************************************
/207/Between/Bug 49775  a3d623b6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+                instance.setPlainBody(getPropertyAsBoolean(PLAIN_BODY));
-                        instance.addAttachment(new File(attachment));
+                    	File file = new File(attachment);
+                    	if(!file.isAbsolute() && !file.exists()){
+                            log.debug("loading file with relative path: " +attachment);
+                            file = new File(FileServer.getFileServer().getBaseDir(), attachment);
+                            log.debug("file path set to: "+attachment);
+                        }
+                        instance.addAttachment(file);

Lines added: 8. Lines removed: 1. Tot = 9
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setPassword
* setEmlMessage
* setUseStartTLS
* setReceiverTo
* trim
* getCount
* printStackTrace
* matches
* execute
* getInputStream
* writeTo
* setSuccessful
* setTrustAllCerts
* setReceiverCC
* setSynchronousMode
* getMessage
* setBody
* setUseSSL
* setMbProvider
* equals
* setUseEmlMessage
* setSmtpPort
* toString
* setSender
* setTrustStoreToUse
* setSubject
* sampleEnd
* addAttachment
* getCause
* getBytes
* split
* setSmtpServer
* setResponseCodeOK
* setEnforceStartTLS
* add
* sampleStart
* read
* setResponseCode
* warn
* setDataType
* setResponseMessage
* getTime
* prepareMessage
* setUseLocalTrustStore
* setBytes
* setSampleLabel
* setUsername
* length
* setUseAuthentication
* getServerResponse
* setResponseData
* setReceiverBCC
* append
********************************************
********************************************
/207/Between/Bug 49862  67fc58bb_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                    StringBuffer sb = new StringBuffer(subject);
+                    StringBuilder sb = new StringBuilder(subject);
-                    	File file = new File(attachment);
-                    	if(!file.isAbsolute() && !file.exists()){
+                        File file = new File(attachment);
+                        if(!file.isAbsolute() && !file.exists()){
+        // Set up the sample result details
+        res.setDataType(SampleResult.TEXT);
+        try {
+            res.setRequestHeaders(getRequestHeaders(message));
+            res.setSamplerData(getSamplerData(message));
+        } catch (MessagingException e1) {
+            res.setSamplerData("Error occurred trying to save request info: "+e1);
+            log.warn("Error occurred trying to save request info",e1);
+        } catch (IOException e1) {
+            res.setSamplerData("Error occurred trying to save request info: "+e1);
+            log.warn("Error occurred trying to save request info",e1);
+        }
+
-            // Set up the sample result details
-            res.setSamplerData(
-                    "To: " + receiverTo
-                    + "\nCC: " + receiverCC
-                    + "\nBCC: " + receiverBcc);
-            res.setDataType(SampleResult.TEXT);
-            StringBuffer sb = new StringBuffer();
+            StringBuilder sb = new StringBuilder();

Lines added: 17. Lines removed: 10. Tot = 27
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setPassword
* setEmlMessage
* setUseStartTLS
* setReceiverTo
* trim
* getCount
* printStackTrace
* matches
* execute
* getInputStream
* writeTo
* setSuccessful
* setTrustAllCerts
* setReceiverCC
* setSynchronousMode
* getMessage
* setBody
* setUseSSL
* setMbProvider
* equals
* setUseEmlMessage
* setSmtpPort
* toString
* setSender
* setTrustStoreToUse
* setSubject
* sampleEnd
* addAttachment
* getCause
* getBytes
* split
* setSmtpServer
* setResponseCodeOK
* setEnforceStartTLS
* add
* sampleStart
* read
* setResponseCode
* warn
* setDataType
* setResponseMessage
* getTime
* prepareMessage
* setUseLocalTrustStore
* setBytes
* setSampleLabel
* setUsername
* length
* setUseAuthentication
* getServerResponse
* setResponseData
* setReceiverBCC
* append
********************************************
********************************************
/207/Between/Bug 53027  fed4b33a_diff.java
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
* setPassword
* setEmlMessage
* setUseStartTLS
* setReceiverTo
* trim
* getCount
* printStackTrace
* matches
* execute
* getInputStream
* writeTo
* setSuccessful
* setTrustAllCerts
* setReceiverCC
* setSynchronousMode
* getMessage
* setBody
* setUseSSL
* setMbProvider
* equals
* setUseEmlMessage
* setSmtpPort
* toString
* setSender
* setTrustStoreToUse
* setSubject
* sampleEnd
* addAttachment
* getCause
* getBytes
* split
* setSmtpServer
* setResponseCodeOK
* setEnforceStartTLS
* add
* sampleStart
* read
* setResponseCode
* warn
* setDataType
* setResponseMessage
* getTime
* prepareMessage
* setUseLocalTrustStore
* setBytes
* setSampleLabel
* setUsername
* length
* setUseAuthentication
* getServerResponse
* setResponseData
* setReceiverBCC
* append
********************************************
********************************************
/207/Between/Bug 53039  a44c6efe_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                res.setBytes(-1);
+                res.setBytes(-1L);

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setPassword
* setEmlMessage
* setUseStartTLS
* setReceiverTo
* trim
* getCount
* printStackTrace
* matches
* execute
* getInputStream
* writeTo
* setSuccessful
* setTrustAllCerts
* setReceiverCC
* setSynchronousMode
* getMessage
* setBody
* setUseSSL
* setMbProvider
* equals
* setUseEmlMessage
* setSmtpPort
* toString
* setSender
* setTrustStoreToUse
* setSubject
* sampleEnd
* addAttachment
* getCause
* getBytes
* split
* setSmtpServer
* setResponseCodeOK
* setEnforceStartTLS
* add
* sampleStart
* read
* setResponseCode
* warn
* setDataType
* setResponseMessage
* getTime
* prepareMessage
* setUseLocalTrustStore
* setBytes
* setSampleLabel
* setUsername
* length
* setUseAuthentication
* getServerResponse
* setResponseData
* setReceiverBCC
* append
********************************************
********************************************
/207/Between/Bug 53042  74885f03_diff.java
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
* setPassword
* setEmlMessage
* setUseStartTLS
* setReceiverTo
* trim
* getCount
* printStackTrace
* matches
* execute
* getInputStream
* writeTo
* setSuccessful
* setTrustAllCerts
* setReceiverCC
* setSynchronousMode
* getMessage
* setBody
* setUseSSL
* setMbProvider
* equals
* setUseEmlMessage
* setSmtpPort
* toString
* setSender
* setTrustStoreToUse
* setSubject
* sampleEnd
* addAttachment
* getCause
* getBytes
* split
* setSmtpServer
* setResponseCodeOK
* setEnforceStartTLS
* add
* sampleStart
* read
* setResponseCode
* warn
* setDataType
* setResponseMessage
* getTime
* prepareMessage
* setUseLocalTrustStore
* setBytes
* setSampleLabel
* setUsername
* length
* setUseAuthentication
* getServerResponse
* setResponseData
* setReceiverBCC
* append
********************************************
********************************************
