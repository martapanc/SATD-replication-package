99/report.java
Satd-method: public JavaObject static_value() {
********************************************
********************************************
99/Between/b19d79dff  Damian's fixes for apple diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        try {
-	    // TODO: Only setAccessible to account for pattern found by
-	    // accessing constants included from a non-public interface.
-	    // (aka java.util.zip.ZipConstants being implemented by many
-	    // classes)
-	    field.setAccessible(true);
-            return JavaObject.wrap(getRuntime(), field.get(null));
-        } catch (IllegalAccessException iae) {
-	    throw getRuntime().newTypeError("illegal static value access: " + iae.getMessage());
+        
+        if (Ruby.isSecurityRestricted())
+            return null;
+        else {
+            try {
+                // TODO: Only setAccessible to account for pattern found by
+                // accessing constants included from a non-public interface.
+                // (aka java.util.zip.ZipConstants being implemented by many
+                // classes)
+                field.setAccessible(true);
+                return JavaObject.wrap(getRuntime(), field.get(null));
+            } catch (IllegalAccessException iae) {
+                throw getRuntime().newTypeError("illegal static value access: " + iae.getMessage());
+            }

Lines added: 14. Lines removed: 9. Tot = 23
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
1624d16b2f 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setAccessible
* getMessage
* wrap
********************************************
********************************************
99/Between/b390103c2  Damn the torpedos...full diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
1624d16b2f 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setAccessible
* getMessage
* wrap
—————————
Method found in diff:	public static IRubyObject wrap(IRubyObject recv, RubyGzipFile io, IRubyObject proc) throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
