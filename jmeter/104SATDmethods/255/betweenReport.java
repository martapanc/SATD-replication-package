/255/report.java
Satd-method: private void setConnectionHeaders(HttpMethod method, URL u, HeaderManager headerManager, CacheManager cacheManager) {
********************************************
********************************************
/255/Between/Bug 50516  98a9ad03_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                        method.addRequestHeader(n, v);
+                        if (HEADER_HOST.equalsIgnoreCase(n)) {
+                            method.getParams().setVirtualHost(v);
+                        } else {
+                            method.addRequestHeader(n, v);
+                        }

Lines added: 5. Lines removed: 1. Tot = 6
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
********************************************
********************************************
/255/Between/Bug 50684  592bf6b7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
********************************************
********************************************
/255/Between/Bug 51380  3ccce769_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
********************************************
********************************************
/255/Between/Bug 51775  9d9fc5b6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                            v = removePortInHostHeader(v, u.getPort());
+                            v = v.replaceFirst(":\\d+$",""); // remove any port specification // $NON-NLS-1$ $NON-NLS-2$

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
********************************************
********************************************
/255/Between/Bug 51775  b3732e9f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+                            v = removePortInHostHeader(v, u.getPort());

Lines added: 1. Lines removed: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
********************************************
********************************************
/255/Between/Bug 51882  c8d0b33a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
********************************************
********************************************
/255/Between/Bug 53039  caaf9e66_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 54482  8075cd90_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 54482  d91a728e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 54482  fd31714f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 54778  05cccf1b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 54778  ee7db54f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 55023  c199d56a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 55255  78f927f9_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	+                    public String getName() { // HC3.1 does not have the method
+                    public String getName() { // HC3.1 does not have the method
+                        return HTTPConstants.DELETE;
+                    }

Lines added: 3. Lines removed: 0. Tot = 3
********************************************
********************************************
/255/Between/Bug 55717  61c1eed7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 55717  9c53b7a1_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 57956  13de0f65_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 57956  6318068e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 57995  795c1a3d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 59038  fd8938f0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 59079  52848488_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 60423  0bf26f41_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 60564  81c34baf_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/255/Between/Bug 60727  2651c6ff_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-    private void setConnectionHeaders(HttpMethod method, URL u, HeaderManager headerManager, CacheManager cacheManager) {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(
-        setConnectionHeaders(httpMethod, u, getHeaderManager(), getCacheManager());
-    private void setConnectionHeaders(HttpMethod method, URL u, HeaderManager headerManager, CacheManager cacheManager) {

Lines added containing method: 0. Lines removed containing method: 2. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
—————————
Method found in diff:	-                    public String getName() { // HC3.1 does not have the method
-                    public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
/255/Between/Change str a75d1b6f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
setConnectionHeaders(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getValue
* setHeaders
* iterator
* getObjectValue
* getName
* getHeaders
* addRequestHeader
* hasNext
* equalsIgnoreCase
********************************************
********************************************
