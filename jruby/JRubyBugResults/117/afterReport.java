117/report.java
Satd-method: public static RubyClass createFileClass(Ruby runtime) {
********************************************
********************************************
117/After/15dd7233e  Potentially fix JRUBY-70 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
createFileClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* defineConstant
* freeze
* getIO
* defineModuleUnder
* getNil
* extend_object
* KindOf
* defineClass
* defineAnnotatedMethods
* includeModule
* newFixnum
* getFileTest
* newString
* setFile
* fastSetConstant
********************************************
********************************************
117/After/34c409148  Fix JRUBY-6702 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
createFileClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* defineConstant
* freeze
* getIO
* defineModuleUnder
* getNil
* extend_object
* KindOf
* defineClass
* defineAnnotatedMethods
* includeModule
* newFixnum
* getFileTest
* newString
* setFile
* fastSetConstant
********************************************
********************************************
117/After/35604c284  Fix JRUBY-6735: FileUtil diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
createFileClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* defineConstant
* freeze
* getIO
* defineModuleUnder
* getNil
* extend_object
* KindOf
* defineClass
* defineAnnotatedMethods
* includeModule
* newFixnum
* getFileTest
* newString
* setFile
* fastSetConstant
********************************************
********************************************
117/After/37e33c389  My stab at fixing JRUBY- diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
createFileClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* defineConstant
* freeze
* getIO
* defineModuleUnder
* getNil
* extend_object
* KindOf
* defineClass
* defineAnnotatedMethods
* includeModule
* newFixnum
* getFileTest
* newString
* setFile
* fastSetConstant
********************************************
********************************************
117/After/533b30428  Fix JRUBY-6780 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
createFileClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* defineConstant
* freeze
* getIO
* defineModuleUnder
* getNil
* extend_object
* KindOf
* defineClass
* defineAnnotatedMethods
* includeModule
* newFixnum
* getFileTest
* newString
* setFile
* fastSetConstant
********************************************
********************************************
117/After/58111ccd4  Fix JRUBY-6998 and defin diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+        
+        // NULL device
+        if (runtime.is1_9() || runtime.is2_0()) {
+            constants.setConstant("NULL", runtime.newString(getNullDevice()));
+        }

Lines added: 5. Lines removed: 0. Tot = 5
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
createFileClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* defineConstant
* freeze
* getIO
* defineModuleUnder
* getNil
* extend_object
* KindOf
* defineClass
* defineAnnotatedMethods
* includeModule
* newFixnum
* getFileTest
* newString
* setFile
* fastSetConstant
********************************************
********************************************
117/After/6de4987a9  Rework the previous logi diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
createFileClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* defineConstant
* freeze
* getIO
* defineModuleUnder
* getNil
* extend_object
* KindOf
* defineClass
* defineAnnotatedMethods
* includeModule
* newFixnum
* getFileTest
* newString
* setFile
* fastSetConstant
********************************************
********************************************
117/After/78491e1c9  Fix JRUBY-7145 by checki diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
createFileClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* defineConstant
* freeze
* getIO
* defineModuleUnder
* getNil
* extend_object
* KindOf
* defineClass
* defineAnnotatedMethods
* includeModule
* newFixnum
* getFileTest
* newString
* setFile
* fastSetConstant
********************************************
********************************************
117/After/89ed6b777  Fix JRUBY-6578: File.rea diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
createFileClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* defineConstant
* freeze
* getIO
* defineModuleUnder
* getNil
* extend_object
* KindOf
* defineClass
* defineAnnotatedMethods
* includeModule
* newFixnum
* getFileTest
* newString
* setFile
* fastSetConstant
********************************************
********************************************
117/After/92fe8de00  Fix JRUBY-6774 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
createFileClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* defineConstant
* freeze
* getIO
* defineModuleUnder
* getNil
* extend_object
* KindOf
* defineClass
* defineAnnotatedMethods
* includeModule
* newFixnum
* getFileTest
* newString
* setFile
* fastSetConstant
********************************************
********************************************
117/After/ad609577a  Merge pull request #178  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
createFileClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* defineConstant
* freeze
* getIO
* defineModuleUnder
* getNil
* extend_object
* KindOf
* defineClass
* defineAnnotatedMethods
* includeModule
* newFixnum
* getFileTest
* newString
* setFile
* fastSetConstant
********************************************
********************************************
117/After/bcc26747f  Use RubyModule.JavaClass diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        fileClass.kindOf = new RubyModule.KindOf() {
-            @Override
-            public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                return obj instanceof RubyFile;
-            }
-        };
+        fileClass.kindOf = new RubyModule.JavaClassKindOf(RubyFile.class);

Lines added: 1. Lines removed: 6. Tot = 7
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
createFileClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* defineConstant
* freeze
* getIO
* defineModuleUnder
* getNil
* extend_object
* KindOf
* defineClass
* defineAnnotatedMethods
* includeModule
* newFixnum
* getFileTest
* newString
* setFile
* fastSetConstant
—————————
Method found in diff:	public void defineAnnotatedMethods(Class clazz) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized void includeModule(IRubyObject arg) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static RubyFixnum newFixnum(Ruby runtime, long value) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString newString(CharSequence s) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
117/After/e89ffda1a  Throw correct Errno from diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
createFileClass(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* defineConstant
* freeze
* getIO
* defineModuleUnder
* getNil
* extend_object
* KindOf
* defineClass
* defineAnnotatedMethods
* includeModule
* newFixnum
* getFileTest
* newString
* setFile
* fastSetConstant
********************************************
********************************************
