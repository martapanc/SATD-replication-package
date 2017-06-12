86/report.java
Satd-method: protected void initializeClass() {
********************************************
********************************************
86/Between/1c02ca0e6  Fixes for JRUBY-734, alo diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            extendObject(runtime.getModule("FileTest"));
+            runtime.getModule("FileTest").extend_object(FileMetaClass.this);

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initializeClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found:
* getEnumerableModule
* getMethod
* getOptSingletonMethod
* getClasses
* getOptMethod
* newFixnum
* optional
* callbackFactory
—————————
Method found in diff:	public RubyFixnum newFixnum(long value) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public CallbackFactory callbackFactory(Class type) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
86/Between/3cd5f1307  Fix for JRUBY-791, and a diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initializeClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found:
* getEnumerableModule
* getMethod
* getOptSingletonMethod
* getClasses
* getOptMethod
* newFixnum
* optional
* callbackFactory
********************************************
********************************************
86/Between/49cf13936  Fix for JRUBY-455: It wa diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initializeClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found:
* getEnumerableModule
* getMethod
* getOptSingletonMethod
* getClasses
* getOptMethod
* newFixnum
* optional
* callbackFactory
********************************************
********************************************
86/Between/57504d74a  Initial fixes for JRUBY- diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initializeClass(
-		public void initializeClass() {

Lines added containing method: 0. Lines removed containing method: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found:
* getEnumerableModule
* getMethod
* getOptSingletonMethod
* getClasses
* getOptMethod
* newFixnum
* optional
* callbackFactory
—————————
Method found in diff:	public RubyFixnum newFixnum(long value) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public CallbackFactory callbackFactory(Class type) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
86/Between/999482001  Fix for JRUBY-321: remov diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initializeClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found:
* getEnumerableModule
* getMethod
* getOptSingletonMethod
* getClasses
* getOptMethod
* newFixnum
* optional
* callbackFactory
********************************************
********************************************
86/Between/ebb36c83a  Fix for JRUBY-249, by Da diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+			defineSingletonMethod("pipe", Arity.noArguments());

Lines added: 1. Lines removed: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
initializeClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found:
* getEnumerableModule
* getMethod
* getOptSingletonMethod
* getClasses
* getOptMethod
* newFixnum
* optional
* callbackFactory
********************************************
********************************************
