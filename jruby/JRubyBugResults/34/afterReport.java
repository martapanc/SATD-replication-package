34/report.java
Satd-method: private static int parseSignalString(Ruby runtime, String value) {
********************************************
********************************************
34/After/4bba125ed  Fix JRUBY-6906: error me diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            throw runtime.newArgumentError("unsupported name `SIG" + signalName + "'");
+            throw runtime.newArgumentError("unsupported name `" + signalName + "'");

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseSignalString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* newArgumentError
* substring
* startsWith
********************************************
********************************************
34/After/f63673104  Fix JRUBY-5729: Process. diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseSignalString(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* newArgumentError
* substring
* startsWith
********************************************
********************************************
