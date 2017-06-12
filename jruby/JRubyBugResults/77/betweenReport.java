77/report.java
Satd-method: public static IRubyObject s_readline(ThreadContext context, IRubyObject recv, IRubyObject prompt, IRubyObject add_to_hist) throws IOException {
********************************************
********************************************
77/Between/3487bba2f  Fixed JRUBY-4116 and JRU diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
s_readline(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* asUTF8
* getTerminal
* restartSystemCall
* readLine
* initializeTerminal
* disableEcho
* isTrue
* getNil
* getHistory
* enableEcho
* newIOErrorFromException
* newUnicodeString
* getRuntime
* addToHistory
—————————
Method found in diff:	public static boolean restartSystemCall(Exception e) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getNil() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static History getHistory(ConsoleHolder holder) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
77/Between/703b764c7  Fix for JRUBY-3420: Adde diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
s_readline(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* asUTF8
* getTerminal
* restartSystemCall
* readLine
* initializeTerminal
* disableEcho
* isTrue
* getNil
* getHistory
* enableEcho
* newIOErrorFromException
* newUnicodeString
* getRuntime
* addToHistory
—————————
Method found in diff:	public static History getHistory(ConsoleHolder holder) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
77/Between/a020c0488  Fix up a problem with my diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+                    // This is for JRUBY-2988, since after a suspend the terminal seems
+                    // to need to be reinitialized. Since we can't easily detect suspension,
+                    // initialize after every readline. Probably not fast, but this is for
+                    // interactive terminals anyway...so who cares?
+                    try {holder.readline.getTerminal().initializeTerminal();} catch (Exception e) {}
-                // This is for JRUBY-2988, since after a suspend the terminal seems
-                // to need to be reinitialized. Since we can't easily detect suspension,
-                // initialize after every readline. Probably not fast, but this is for
-                // interactive terminals anyway...so who cares?
-                try {holder.readline.getTerminal().initializeTerminal();} catch (Exception e) {}

Lines added: 5. Lines removed: 5. Tot = 10
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
s_readline(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* asUTF8
* getTerminal
* restartSystemCall
* readLine
* initializeTerminal
* disableEcho
* isTrue
* getNil
* getHistory
* enableEcho
* newIOErrorFromException
* newUnicodeString
* getRuntime
* addToHistory
—————————
Method found in diff:	public static History getHistory(ConsoleHolder holder) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
