34/report.java
Satd-method: private static int parseSignalString(Ruby runtime, String value) {
********************************************
********************************************
34/Between/2ebc903a2  Fix for JRUBY-2353: Proc diff.java
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
34/Between/2f1445af0  Use jnr-constants for si diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        if (negative) startIndex++;
-        
-        boolean signalString = value.startsWith("SIG", startIndex);
+        if (value.startsWith("-")) startIndex++;
+        String signalName = value.startsWith("SIG", startIndex)
+                ? value 
+                : "SIG" + value.substring(startIndex);
-        if (signalString) startIndex += 3;
-       
-        String signalName = value.substring(startIndex);
-        
-        // FIXME: This table will get moved into POSIX library so we can get all actual supported
-        // signals.  This is a quick fix to support basic signals until that happens.
-        for (int i = 0; i < signals.length; i++) {
-            if (signals[i].equals(signalName)) return negative ? -i : i;
+        try {
+            int signalValue = Signal.valueOf(signalName).value();
+            return negative ? -signalValue : signalValue;
+
+        } catch (IllegalArgumentException ex) {
+            throw runtime.newArgumentError("unsupported name `SIG" + signalName + "'");
-        throw runtime.newArgumentError("unsupported name `SIG" + signalName + "'");

Lines added: 10. Lines removed: 12. Tot = 22
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
34/Between/35a1935d2  Fix JRUBY-5531: Process. diff.java
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
34/Between/3f5f461e6  Improve on the previous  diff.java
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
34/Between/42b0fa9bc  Fix JRUBY-4468: Process. diff.java
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
—————————
Method found in diff:	public RaiseException newArgumentError(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
34/Between/5d094dbf5  Fix JRUBY-5688: Process: diff.java
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
34/Between/78835628f  Fix JRUBY-5687: Process: diff.java
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
34/Between/7c7ecc5cd  Fix JRUBY-5463: Process. diff.java
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
—————————
Method found in diff:	public RaiseException newArgumentError(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
34/Between/b643acda3  Fix for JRUBY-2795: Proc diff.java
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
34/Between/bc381827a  Fix JRUBY-2796 Missing c diff.java
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
34/Between/d76f6369e  Fix JRUBY-4469: Process. diff.java
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
