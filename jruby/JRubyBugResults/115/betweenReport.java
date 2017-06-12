115/report.java
Satd-method: private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, Frame frame, Frame previousFrame) {
********************************************
********************************************
115/Between/1d1e53347  Kinda hacky fixes for ex diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
26f5d63fd5 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getLine
* getFile
* newString
* is1_9
* append
—————————
Method found in diff:	public int getLine() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
115/Between/25720ab18  Fix for JRUBY-1531: Trac diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
26f5d63fd5 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getLine
* getFile
* newString
* is1_9
* append
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getLine() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
115/Between/26f5d63fd  Fix for JRUBY-4531: java diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
26f5d63fd5 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getLine
* getFile
* newString
* is1_9
* append
—————————
Method found in diff:	public int getLine() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
115/Between/5f3de4246  Fix for JRUBY-4484: jrub diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
26f5d63fd5 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getLine
* getFile
* newString
* is1_9
* append
—————————
Method found in diff:	public int getLine() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyClass getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
115/Between/93b1257da  Revert "Fix for JRUBY-44 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
26f5d63fd5 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getLine
* getFile
* newString
* is1_9
* append
—————————
Method found in diff:	public int getLine() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyClass getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
115/Between/979340494  Fix for JRUBY-5086: jrub diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
26f5d63fd5 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getLine
* getFile
* newString
* is1_9
* append
—————————
Method found in diff:	public int getLine() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
115/Between/99f983249  Rejigger how binding wor diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
26f5d63fd5 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getLine
* getFile
* newString
* is1_9
* append
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+    public int getLine() {
+    public int getLine() {
+        return line;
+    }

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	public RubyClass getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString newString() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean is1_9() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
115/Between/a4d33150b  Final fixes and test for diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
26f5d63fd5 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getLine
* getFile
* newString
* is1_9
* append
—————————
Method found in diff:	public int getLine() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
115/Between/b585f5861  Fix for JRUBY-3397: defi diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
26f5d63fd5 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getLine
* getFile
* newString
* is1_9
* append
—————————
Method found in diff:	public int getLine() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
115/Between/cbe7b2b79  Fix for JRUBY-4262: Weir diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
26f5d63fd5 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getLine
* getFile
* newString
* is1_9
* append
—————————
Method found in diff:	public int getLine() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
115/Between/d9835f820  Interpreter fix for JRUB diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
26f5d63fd5 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getLine
* getFile
* newString
* is1_9
* append
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getLine() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
