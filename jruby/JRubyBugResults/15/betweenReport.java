15/report.java
Satd-method: private IRubyObject[] trueIfNoArgument(ThreadContext context, IRubyObject[] args) {
********************************************
********************************************
15/Between/3f6a0c9ec  Fix JRUBY-3870: Object#s diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
trueIfNoArgument(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTrue
* getRuntime
********************************************
********************************************
15/Between/6eb8df5bd  Fix JRUBY-4871: [1.9] At diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+    private IRubyObject[] trueIfNoArgument(ThreadContext context, IRubyObject[] args) {
+        return args.length == 0 ? new IRubyObject[] { context.getRuntime().getTrue() } : args;
+    }

Lines added: 3. Lines removed: 0. Tot = 3
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
trueIfNoArgument(
+        return getMetaClass().public_instance_methods(trueIfNoArgument(context, args));
+        return getMetaClass().public_instance_methods19(trueIfNoArgument(context, args));
+        return getMetaClass().protected_instance_methods(trueIfNoArgument(context, args));
+        return getMetaClass().protected_instance_methods19(trueIfNoArgument(context, args));
+        return getMetaClass().private_instance_methods(trueIfNoArgument(context, args));
+        return getMetaClass().private_instance_methods19(trueIfNoArgument(context, args));
+    private IRubyObject[] trueIfNoArgument(ThreadContext context, IRubyObject[] args) {
-        return getMetaClass().public_instance_methods(trueIfNoArgument(context, args));
-        return getMetaClass().public_instance_methods19(trueIfNoArgument(context, args));
-        return getMetaClass().protected_instance_methods(trueIfNoArgument(context, args));
-        return getMetaClass().protected_instance_methods19(trueIfNoArgument(context, args));
-        return getMetaClass().private_instance_methods(trueIfNoArgument(context, args));
-        return getMetaClass().private_instance_methods19(trueIfNoArgument(context, args));
-    private IRubyObject[] trueIfNoArgument(ThreadContext context, IRubyObject[] args) {

Lines added containing method: 7. Lines removed containing method: 7. Tot = 14
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTrue
* getRuntime
—————————
Method found in diff:	public final Ruby getRuntime() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
15/Between/9a20ef318  fixes JRUBY-4016: [1.9]  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
trueIfNoArgument(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTrue
* getRuntime
—————————
Method found in diff:	public final Ruby getRuntime() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
15/Between/b845a7bec  Fix ruby_test SortedSet  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
trueIfNoArgument(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTrue
* getRuntime
********************************************
********************************************
15/Between/dfe4d4105  Fix JRUBY-4174, make it  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
trueIfNoArgument(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTrue
* getRuntime
—————————
Method found in diff:	public final Ruby getRuntime() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
15/Between/ef99d39db  Fix for JRUBY-5137: [1.9 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
trueIfNoArgument(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTrue
* getRuntime
********************************************
********************************************
