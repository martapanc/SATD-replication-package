135/report.java
Satd-method: public RubyString join(IRubyObject[] args) {
********************************************
********************************************
135/Between/1c02ca0e6  Fixes for JRUBY-734, alo diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
join(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isTaint
* convertToString
* setTaint
* toJavaArray
* append
* startsWith
* newString
—————————
Method found in diff:	public boolean isTaint() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+    public RubyString convertToString() {
+    public RubyString convertToString() {
+        return (RubyString) convertToType("String", "to_str", true);
+    }

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	public void setTaint(boolean taint) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString append(IRubyObject other) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString newString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
135/Between/4825bbbd4  Fix for JRUBY-396: Adds  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
join(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isTaint
* convertToString
* setTaint
* toJavaArray
* append
* startsWith
* newString
********************************************
********************************************
135/Between/542415f6b  Fix for JRUBY-301, make  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
join(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isTaint
* convertToString
* setTaint
* toJavaArray
* append
* startsWith
* newString
********************************************
********************************************
135/Between/57504d74a  Initial fixes for JRUBY- diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
join(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isTaint
* convertToString
* setTaint
* toJavaArray
* append
* startsWith
* newString
—————————
Method found in diff:	public boolean isTaint() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString convertToString() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setTaint(boolean taint) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray append(IRubyObject value) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString newString(String string) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
135/Between/59c5d204b  Fix for JRUBY-333, Rails diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
join(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isTaint
* convertToString
* setTaint
* toJavaArray
* append
* startsWith
* newString
********************************************
********************************************
135/Between/8d7f2f4f7  Fix for JRUBY-412: Make  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
join(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isTaint
* convertToString
* setTaint
* toJavaArray
* append
* startsWith
* newString
—————————
Method found in diff:	public RubyString newString(String string);

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
135/Between/9fa4acdd4  Fix for JRUBY-461: trim  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
join(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isTaint
* convertToString
* setTaint
* toJavaArray
* append
* startsWith
* newString
********************************************
********************************************
135/Between/aa567ca8c  Fix for JRUBY-224, File. diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
join(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isTaint
* convertToString
* setTaint
* toJavaArray
* append
* startsWith
* newString
********************************************
********************************************
135/Between/dbdef3809  Fix for JRUBY-429, and a diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
join(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isTaint
* convertToString
* setTaint
* toJavaArray
* append
* startsWith
* newString
—————————
Method found in diff:	public RubyString newString(String string) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
