/257/report.java
Satd-method: private String sendPostData(PostMethod post) throws IOException {
********************************************
********************************************
/257/Between/Bug 50516  98a9ad03_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 50684  592bf6b7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+            final boolean browserCompatible = getDoBrowserCompatibleMultipart();
-               HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
-               String parameterName = arg.getName();
-               if (arg.isSkippable(parameterName)){
-                   continue;
-               }
-               partlist.add(new StringPart(arg.getName(), arg.getValue(), contentEncoding));
+                HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
+                String parameterName = arg.getName();
+                if (arg.isSkippable(parameterName)){
+                    continue;
+                }
+                StringPart part = new StringPart(arg.getName(), arg.getValue(), contentEncoding);
+                if (browserCompatible) {
+                    part.setTransferEncoding(null);
+                    part.setContentType(null);
+                }
+                partlist.add(part);

Lines added: 12. Lines removed: 6. Tot = 18
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 51380  3ccce769_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 51775  9d9fc5b6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 51775  b3732e9f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 51882  c8d0b33a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 53039  caaf9e66_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 54482  8075cd90_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 54482  d91a728e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 54482  fd31714f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 54778  05cccf1b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 54778  ee7db54f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 55023  c199d56a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 55255  78f927f9_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	+                    public String getName() { // HC3.1 does not have the method
+                    public String getName() { // HC3.1 does not have the method
+                        return HTTPConstants.DELETE;
+                    }

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 55717  61c1eed7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 55717  9c53b7a1_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 57956  13de0f65_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 57956  6318068e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 57995  795c1a3d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                File inputFile = new File(file.getPath());
+                File inputFile = FileServer.getFileServer().getResolvedFile(file.getPath());

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 59038  fd8938f0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 59079  52848488_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 60423  0bf26f41_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 60564  81c34baf_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/257/Between/Bug 60727  2651c6ff_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-    private String sendPostData(PostMethod post) throws IOException {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(
-                String postBody = sendPostData((PostMethod)httpMethod);
-    private String sendPostData(PostMethod post) throws IOException {

Lines added containing method: 0. Lines removed containing method: 2. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	-                    public String getName() { // HC3.1 does not have the method
-                    public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-        public void setHideFileData(boolean hideFileData) {
-        public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
/257/Between/Change str a75d1b6f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getPath
* toArray
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* size
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* writeRequest
* isSkippable
* close
* setContentCharset
* equals
* toString
* getName
* setRequestHeader
* decode
* getRequestCharSet
* add
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* flush
* getParamName
* getMimeType
* length
* getValue
* setHideFileData
* toByteArray
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
