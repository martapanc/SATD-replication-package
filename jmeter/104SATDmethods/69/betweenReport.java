69/report.java
Satd-method: public void clear()
********************************************
********************************************
69/Between/Bug 36755  e861ae37d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
clear(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* currentThread
* getName
********************************************
********************************************
69/Between/Bug 41944  289264650_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
clear(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* currentThread
* getName
********************************************
********************************************
69/Between/Bug 43119  d81ad7e22_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
clear(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* currentThread
* getName
********************************************
********************************************
69/Between/Bug 44575  59671c56f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
clear(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* currentThread
* getName
********************************************
********************************************
69/Between/Bug 49365  9cca78bc0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
clear(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* currentThread
* getName
********************************************
********************************************
69/Between/Bug 52214  3e16150b7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        super.clear();
-        synchronized(this){
+        synchronized(LOCK){
+            if (getAddTimeStamp()) {
+                DateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT);
+                timeStamp = format.format(new Date());
+            } else {
+                timeStamp = "";
+            }
+            numberPadLength=getNumberPadLen();
+        super.clear();

Lines added: 9. Lines removed: 2. Tot = 11
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
clear(
-        super.clear();
+        super.clear();

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* currentThread
* getName
********************************************
********************************************
