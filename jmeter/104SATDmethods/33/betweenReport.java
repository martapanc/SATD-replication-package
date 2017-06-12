/33/report.java
Satd-method: public SampleResult sample(Entry e) {
********************************************
********************************************
/33/Between/Bug 49603  91e79e3ed_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+            
+            if (isUseStartTLS()) {
+                props.setProperty("mail.pop3s.starttls.enable", "true");
+                if (isEnforceStartTLS()){
+                    // Requires JavaMail 1.4.2+
+                    props.setProperty("mail.pop3s.starttls.require", "true");
+                }
+            }
+
+            if (isTrustAllCerts()) {
+                if (isUseSSL()) {
+                    props.setProperty("mail.pop3s.ssl.socketFactory.class", TRUST_ALL_SOCKET_FACTORY);
+                    props.setProperty("mail.pop3s.ssl.socketFactory.fallback", "false");
+                } else if (isUseStartTLS()) {
+                    props.setProperty("mail.pop3s.ssl.socketFactory.class", TRUST_ALL_SOCKET_FACTORY);
+                    props.setProperty("mail.pop3s.ssl.socketFactory.fallback", "false");
+                }
+            } else if (isUseLocalTrustStore()){
+                File truststore = new File(getTrustStoreToUse());
+                log.info("load local truststore - try to load truststore from: "+truststore.getAbsolutePath());
+                if(!truststore.exists()){
+                	log.info("load local truststore -Failed to load truststore from: "+truststore.getAbsolutePath());
+                    truststore = new File(FileServer.getFileServer().getBaseDir(), getTrustStoreToUse());
+                    log.info("load local truststore -Attempting to read truststore from:  "+truststore.getAbsolutePath());
+                    if(!truststore.exists()){
+                    	log.info("load local truststore -Failed to load truststore from: "+truststore.getAbsolutePath() + ". Local truststore not available, aborting execution.");
+                        throw new IOException("Local truststore file not found. Also not available under : " + truststore.getAbsolutePath());
+                    }
+                }
+                if (isUseSSL()) {
+                    // Requires JavaMail 1.4.2+
+                    props.put("mail.pop3s.ssl.socketFactory", new LocalTrustStoreSSLSocketFactory(truststore));
+                    props.put("mail.pop3s.ssl.socketFactory.fallback", "false");
+                } else if (isUseStartTLS()) {
+                    // Requires JavaMail 1.4.2+
+                    props.put("mail.pop3s.ssl.socketFactory", new LocalTrustStoreSSLSocketFactory(truststore));
+                    props.put("mail.pop3s.ssl.socketFactory.fallback", "false");
+                }
+            }            
-            Session session = Session.getDefaultInstance(props, null);
+            Session session = Session.getInstance(props, null);

Lines added: 40. Lines removed: 1. Tot = 41
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getStore
* getName
* sampleEnd
* getAllHeaders
* getMessages
* setEncodingAndType
* getFolder
* setSampleCount
* getEndTime
* hasMoreElements
* setResponseCodeOK
* addSubResult
* setContentType
* sampleStart
* setFlag
* setResponseHeaders
* setResponseMessageOK
* setResponseCode
* writeTo
* setLength
* setDataType
* setResponseMessage
* setSuccessful
* getContentType
* setSamplerData
* decodeText
* setDataEncoding
* getDefaultInstance
* close
* connect
* setSampleLabel
* debug
* getMessageNumber
* setResponseData
* getValue
* toByteArray
* setResponseOK
* toString
* open
* append
* nextElement
—————————
Method found in diff:	public String getFolder() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString(){

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/33/Between/Bug 51011  3dbd39d86_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            parent.setResponseMessage(ex.toString());
+            parent.setResponseMessage(ex.toString() + "\n" + samplerString); // $NON-NLS-1$

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getStore
* getName
* sampleEnd
* getAllHeaders
* getMessages
* setEncodingAndType
* getFolder
* setSampleCount
* getEndTime
* hasMoreElements
* setResponseCodeOK
* addSubResult
* setContentType
* sampleStart
* setFlag
* setResponseHeaders
* setResponseMessageOK
* setResponseCode
* writeTo
* setLength
* setDataType
* setResponseMessage
* setSuccessful
* getContentType
* setSamplerData
* decodeText
* setDataEncoding
* getDefaultInstance
* close
* connect
* setSampleLabel
* debug
* getMessageNumber
* setResponseData
* getValue
* toByteArray
* setResponseOK
* toString
* open
* append
* nextElement
—————————
Method found in diff:	public String getFolder() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString(){

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/33/Between/Bug 53042  d7fdf5ad2_diff.java
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
* getStore
* getName
* sampleEnd
* getAllHeaders
* getMessages
* setEncodingAndType
* getFolder
* setSampleCount
* getEndTime
* hasMoreElements
* setResponseCodeOK
* addSubResult
* setContentType
* sampleStart
* setFlag
* setResponseHeaders
* setResponseMessageOK
* setResponseCode
* writeTo
* setLength
* setDataType
* setResponseMessage
* setSuccessful
* getContentType
* setSamplerData
* decodeText
* setDataEncoding
* getDefaultInstance
* close
* connect
* setSampleLabel
* debug
* getMessageNumber
* setResponseData
* getValue
* toByteArray
* setResponseOK
* toString
* open
* append
* nextElement
—————————
Method found in diff:	public String getFolder() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString(){

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/33/Between/Bug 56539  87b67eb18_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            parent.setSampleCount(messages.length); // TODO is this sensible?
-

Lines added: 0. Lines removed: 2. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getStore
* getName
* sampleEnd
* getAllHeaders
* getMessages
* setEncodingAndType
* getFolder
* setSampleCount
* getEndTime
* hasMoreElements
* setResponseCodeOK
* addSubResult
* setContentType
* sampleStart
* setFlag
* setResponseHeaders
* setResponseMessageOK
* setResponseCode
* writeTo
* setLength
* setDataType
* setResponseMessage
* setSuccessful
* getContentType
* setSamplerData
* decodeText
* setDataEncoding
* getDefaultInstance
* close
* connect
* setSampleLabel
* debug
* getMessageNumber
* setResponseData
* getValue
* toByteArray
* setResponseOK
* toString
* open
* append
* nextElement
—————————
Method found in diff:	public String getFolder() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString(){

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
