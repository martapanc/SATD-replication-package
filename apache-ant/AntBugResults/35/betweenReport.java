35/report.java
Satd-method: private static String[] getProcEnvCommand() {
********************************************
********************************************
35/Between/Add Windows 200 1b72f9a64_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            String[] cmd = {"cmd", "/c", "set" };
-            return cmd;
+            return new String[] {"cmd", "/c", "set" };
-            if (!Os.isFamily("win9x")) {
-                // Windows XP/2000/NT
-                String[] cmd = {"cmd", "/c", "set" };
-                return cmd;
-            } else {
+            if (Os.isFamily("win9x")) {
-                String[] cmd = {"command.com", "/c", "set" };
-                return cmd;
+                return new String[] {"command.com", "/c", "set" };
+            } else {
+                // Windows XP/2000/NT/2003
+                return new String[] {"cmd", "/c", "set" };
-            String[] cmd = {"env"};
-            return cmd;
+            return new String[] {"env"};
-            String[] cmd = {"show", "logical"};
-            return cmd;
+            return new String[] {"show", "logical"};
-            String[] cmd = null;
-            return cmd;
+            return null;

Lines added: 9. Lines removed: 15. Tot = 24
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
c25de7702 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* canRead
* isFamily
********************************************
********************************************
35/Between/Close process s 08fc13867_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
c25de7702 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* canRead
* isFamily
********************************************
********************************************
35/Between/Move Process st c25de7702_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
c25de7702 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* canRead
* isFamily
********************************************
********************************************
35/Between/PR 52706: allow abec7e48e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            return new String[] {"cmd", "/c", "set" };
+            return new String[] {"cmd", "/c", "set"};
-                return new String[] {"command.com", "/c", "set" };
+                return new String[] {"command.com", "/c", "set"};
-                return new String[] {"cmd", "/c", "set" };
+                return new String[] {"cmd", "/c", "set"};
-            //TODO: I have no idea how to get it, someone must fix it
+            // TODO: I have no idea how to get it, someone must fix it

Lines added: 4. Lines removed: 4. Tot = 8
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
c25de7702 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* canRead
* isFamily
********************************************
********************************************
35/Between/provide a Map b ff41336fc_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
c25de7702 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* canRead
* isFamily
********************************************
********************************************
35/Between/remove authors  c885f5683_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
c25de7702 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* canRead
* isFamily
********************************************
********************************************
35/Between/Remove direct c 3396e7c32_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
c25de7702 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* canRead
* isFamily
********************************************
********************************************
35/Between/The user.dir tr 8e7167b58_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
c25de7702 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* canRead
* isFamily
********************************************
********************************************
