121/report.java
Satd-method: public Block cloneBlock(Binding binding) {
********************************************
********************************************
121/Between/99f983249  Rejigger how binding wor diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        binding = new Binding(binding.getSelf(), binding.getFrame(),
-                Visibility.PUBLIC,
-                binding.getKlass(),
-                binding.getDynamicScope());
+        binding = binding.clone(Visibility.PUBLIC);

Lines added: 1. Lines removed: 4. Tot = 5
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
cloneBlock(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDynamicScope
* getSelf
* getFrame
* duplicate
* getKlass
* getVisibility
—————————
Method found in diff:	public DynamicScope getDynamicScope() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getSelf() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Frame getFrame() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Frame duplicate() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyModule getKlass() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Visibility getVisibility() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
121/Between/ff96ef927  Fix for JRUBY-1872: next diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
cloneBlock(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDynamicScope
* getSelf
* getFrame
* duplicate
* getKlass
* getVisibility
********************************************
********************************************
