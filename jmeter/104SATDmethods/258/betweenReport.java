/258/report.java
Satd-method: private String sendPostData(PostMethod post) throws IOException {
********************************************
********************************************
/258/Between/Bug 19128  0431342f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        
+        HTTPFileArg files[] = getHTTPFiles();
-            if(hasUploadableFiles())
-            {
-                noParts++;
-            }
+            noParts += files.length;
-            if(hasUploadableFiles()) {
-                File inputFile = new File(getFilename());
+            for (int i=0; i < files.length; i++) {
+                HTTPFileArg file = files[i];
+                File inputFile = new File(file.getPath());
-                ViewableFilePart filePart = new ViewableFilePart(getFileField(), inputFile, getMimetype(), null);
+                ViewableFilePart filePart = new ViewableFilePart(file.getParamName(), inputFile, file.getMimeType(), null);
-
+            // TODO: needs a multiple file upload scenerio
+                // If getSendFileAsPostBody returned true, it's sure that file is not null
+                HTTPFileArg file = files[0];
-                    if(getMimetype() != null && getMimetype().length() > 0) {
-                        post.setRequestHeader(HEADER_CONTENT_TYPE, getMimetype());
+                    if(file.getMimeType() != null && file.getMimeType().length() > 0) {
+                        post.setRequestHeader(HEADER_CONTENT_TYPE, file.getMimeType());
-                
-                FileRequestEntity fileRequestEntity = new FileRequestEntity(new File(getFilename()),null); 
+
+                FileRequestEntity fileRequestEntity = new FileRequestEntity(new File(file.getPath()),null);
+                    // TODO: needs a multiple file upload scenerio
-                        if(getMimetype() != null && getMimetype().length() > 0) {
-                            post.setRequestHeader(HEADER_CONTENT_TYPE, getMimetype());
+                        HTTPFileArg file = files.length > 0? files[0] : null;
+                        if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
+                            post.setRequestHeader(HEADER_CONTENT_TYPE, file.getMimeType());

Lines added: 17. Lines removed: 15. Tot = 32
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* setRequestHeader
* decode
* getRequestCharSet
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* flush
* writeRequest
* close
* setContentCharset
* length
* getArgumentCount
* getValue
* setHideFileData
* toByteArray
* equals
* toString
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/258/Between/Bug 28502  2526e684_diff.java
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
* getName
* setRequestHeader
* decode
* getRequestCharSet
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* flush
* writeRequest
* close
* setContentCharset
* length
* getArgumentCount
* getValue
* setHideFileData
* toByteArray
* equals
* toString
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/258/Between/Bug 44521  006b977a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            // Check how many parts we need, one for each parameter and file
-            int noParts = getArguments().getArgumentCount();
-            noParts += files.length;
-
+            // We don't know how many entries will be skipped
+            ArrayList partlist = new ArrayList();
-            Part[] parts = new Part[noParts];
-            int partNo = 0;
-               if (parameterName.length()==0){
-                   continue; // Skip parameters with a blank name (allows use of optional variables in parameter lists)
+               if (arg.isSkippable(parameterName)){
+                   continue;
-               parts[partNo++] = new StringPart(arg.getName(), arg.getValue(), contentEncoding);
+               partlist.add(new StringPart(arg.getName(), arg.getValue(), contentEncoding));
-                parts[partNo++] = filePart;
+                partlist.add(filePart);
+            int partNo = partlist.size();
+            Part[] parts = (Part[])partlist.toArray(new Part[partNo]);
-                    // Just append all the non-empty parameter values, and use that as the post body
+                    // Just append all the parameter values, and use that as the post body
-                        if (parameterName.length()==0){
-                            continue; // Skip parameters with a blank name (allows use of optional variables in parameter lists)
+                        if (arg.isSkippable(parameterName)){
+                            continue;

Lines added: 11. Lines removed: 13. Tot = 24
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* setRequestHeader
* decode
* getRequestCharSet
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* flush
* writeRequest
* close
* setContentCharset
* length
* getArgumentCount
* getValue
* setHideFileData
* toByteArray
* equals
* toString
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/258/Between/Bug 44852  4f047a40_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        // Buffer to hold the post body, expect file content
+        // Buffer to hold the post body, except file content

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sendPostData(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* setRequestHeader
* decode
* getRequestCharSet
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* flush
* writeRequest
* close
* setContentCharset
* length
* getArgumentCount
* getValue
* setHideFileData
* toByteArray
* equals
* toString
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/258/Between/Bug 47321  22ef64ab_diff.java
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
* getName
* setRequestHeader
* decode
* getRequestCharSet
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* flush
* writeRequest
* close
* setContentCharset
* length
* getArgumentCount
* getValue
* setHideFileData
* toByteArray
* equals
* toString
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/258/Between/Bug 47461  1987e3fd_diff.java
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
* getName
* setRequestHeader
* decode
* getRequestCharSet
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* flush
* writeRequest
* close
* setContentCharset
* length
* getArgumentCount
* getValue
* setHideFileData
* toByteArray
* equals
* toString
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/258/Between/Bug 48300  db972810_diff.java
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
* getName
* setRequestHeader
* decode
* getRequestCharSet
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* flush
* writeRequest
* close
* setContentCharset
* length
* getArgumentCount
* getValue
* setHideFileData
* toByteArray
* equals
* toString
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/258/Between/Implement  6ccc5cf0_diff.java
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
* getName
* setRequestHeader
* decode
* getRequestCharSet
* iterator
* getObjectValue
* trim
* isAlwaysEncoded
* getRequestHeader
* setCharSet
* hasNext
* getContentLength
* isRepeatable
* setRequestEntity
* getContentType
* getParams
* next
* getEncodedValue
* sendMultipartWithoutFileContent
* addParameter
* getRequestEntity
* flush
* writeRequest
* close
* setContentCharset
* length
* getArgumentCount
* getValue
* setHideFileData
* toByteArray
* equals
* toString
* append
—————————
Method found in diff:	public void setHideFileData(boolean hideFileData) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
