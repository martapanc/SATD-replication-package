87/report.java
Satd-method: public boolean remove(Object element) {
********************************************
********************************************
87/Between/04ce842cc  Fixes for JRUBY-2883: Ma diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/0505fb1fc  [1.9] Fix JRUBY-4508, ad diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/105217d34  RubyArray now obeys List diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        IRubyObject deleted = delete(getRuntime().getCurrentContext(), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element), Block.NULL_BLOCK);
-        return deleted.isNil() ? false : true; // TODO: is this correct ?
+        Ruby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+        IRubyObject item = JavaUtil.convertJavaToUsableRubyObject(runtime, element);
+        Boolean listchanged = false;
+
+        for (int i1 = 0; i1 < realLength; i1++) {
+            IRubyObject e = values[begin + i1];
+            if (equalInternal(context, e, item)) {
+                delete_at(i1);
+                listchanged = true;
+                break;
+            }
+        }
+
+        return listchanged;

Lines added: 15. Lines removed: 2. Tot = 17
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(
-            if (remove(iter.next())) {

Lines added containing method: 0. Lines removed containing method: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/219e0308d  Fix for JRUBY-3387: Arra diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/238d16953  Fix for JRUBY-1209: Arra diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(
-            if (set.remove(v)) store(j++, v);
-            if (set.remove(v)) ary3.append(v);
-            if (set.remove(v)) ary3.append(v);
-            if (set.remove(v)) ary3.append(v);

Lines added containing method: 0. Lines removed containing method: 4. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/2b6aedfc5  Fix for JRUBY-3878: Stri diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/397ae2d50  Fix JRUBY-3148 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/3cdb25eaa  Fixes a bug, where frame diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/4d034fafe  Fix for JRUBY-4157: fann diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/56eeae1a8  Fix for JRUBY-4515: Ruby diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/6266374bc  Fix for JRUBY-4053: Acti diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/738035503  Fix for JRUBY-587. MRI Y diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
—————————
Method found in diff:	public boolean isNil() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
87/Between/7b201461f  Fixes for JRUBY-3816: Ob diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        IRubyObject deleted = delete(getRuntime().getCurrentContext(), JavaUtil.convertJavaToRuby(getRuntime(), element), Block.NULL_BLOCK);
+        IRubyObject deleted = delete(getRuntime().getCurrentContext(), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element), Block.NULL_BLOCK);

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/875bdbab0  Fix for JRUBY-1210: Arra diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(
-        val.remove(obj);
+        if (val != null ) val.remove(obj);

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
—————————
Method found in diff:	public final boolean isNil() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
87/Between/993f8c99e  A bunch of findbugs fixe diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/a2854314c  Fix for JRUBY-2065: Arra diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/ac5467733  fixes JRUBY-4175: RubySp diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/aeef3e6d1  fixes JRUBY-4181: [1.8.7 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/b3332e8a4  Fix for JRUBY-3251: Conc diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/b395be2f7  Rewritten Enumerator cla diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/bad1f6788  Fixes for JRUBY-1409, on diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/c6aebe391  Fix JRUBY-3612 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/d77f8c920  Fixes for JRUBY-361: sup diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
********************************************
********************************************
87/Between/e421186fd  Fixes and tests for JRUB diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
remove(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* convertJavaToRuby
* isNil
—————————
Method found in diff:	public boolean isNil() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
