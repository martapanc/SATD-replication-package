124/report.java
Satd-method: public IRubyObject bind(ThreadContext context, IRubyObject arg) {
********************************************
********************************************
124/After/97409f488  Fix JRUBY-6761 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
bind(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* printStackTrace
* fix2int
* getMessage
* convertToString
* newErrnoENOPROTOOPTError
* getCause
* pop
* zero
* socket
* getRuntime
********************************************
********************************************
124/After/ce2282e2f  Fix JRUBY-6527 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
bind(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* printStackTrace
* fix2int
* getMessage
* convertToString
* newErrnoENOPROTOOPTError
* getCause
* pop
* zero
* socket
* getRuntime
—————————
Method found in diff:	public RaiseException newErrnoENOPROTOOPTError() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
124/After/d5a87bf2d  Fix JRUBY-6526 and conti diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
bind(
+            throw sockerr(runtime, "bind(2): name or service not known");
+                socket.bind(iaddr);

Lines added containing method: 2. Lines removed containing method: 0. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* printStackTrace
* fix2int
* getMessage
* convertToString
* newErrnoENOPROTOOPTError
* getCause
* pop
* zero
* socket
* getRuntime
********************************************
********************************************
