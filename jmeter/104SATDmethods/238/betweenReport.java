/238/report.java
Satd-method: protected Object processFileOrScript(ScriptEngine scriptEngine, Bindings bindings) throws IOException, ScriptException {
********************************************
********************************************
/238/Between/Bug 56554  4f208f5c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        } else if (!StringUtils.isEmpty(getScript())){
+        } else if (!StringUtils.isEmpty(getScript())) {
-                CompiledScript compiledScript = 
-                        compiledScriptsCache.get(cacheKey);
-                if (compiledScript==null) {
+                computeScriptMD5();
+                CompiledScript compiledScript = compiledScriptsCache.get(this.scriptMd5);
+                if (compiledScript == null) {
-                        compiledScript = 
-                                compiledScriptsCache.get(cacheKey);
-                        if (compiledScript==null) {
-                            compiledScript = 
-                                    ((Compilable) scriptEngine).compile(getScript());
-                            compiledScriptsCache.put(cacheKey, compiledScript);
+                        compiledScript = compiledScriptsCache.get(this.scriptMd5);
+                        if (compiledScript == null) {
+                            compiledScript = ((Compilable) scriptEngine).compile(getScript());
+                            compiledScriptsCache.put(this.scriptMd5, compiledScript);
+                

Lines added: 9. Lines removed: 10. Tot = 19
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
processFileOrScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getName
* canRead
* put
* compile
* get
* getAbsolutePath
* isEmpty
* length
* createBindings
* eval
* equals
* exists
* lastModified
* closeQuietly
********************************************
********************************************
/238/Between/Bug 57193: f023972d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
processFileOrScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getName
* canRead
* put
* compile
* get
* getAbsolutePath
* isEmpty
* length
* createBindings
* eval
* equals
* exists
* lastModified
* closeQuietly
********************************************
********************************************
/238/Between/Bug 59945  2e36b4f4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
processFileOrScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getName
* canRead
* put
* compile
* get
* getAbsolutePath
* isEmpty
* length
* createBindings
* eval
* equals
* exists
* lastModified
* closeQuietly
********************************************
********************************************
/238/Between/Bug 60564  ea768213_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
processFileOrScript(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getName
* canRead
* put
* compile
* get
* getAbsolutePath
* isEmpty
* length
* createBindings
* eval
* equals
* exists
* lastModified
* closeQuietly
********************************************
********************************************
/238/Between/Bug 60813  3fb51fc0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-    protected Object processFileOrScript(ScriptEngine scriptEngine, Bindings bindings) throws IOException, ScriptException {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
processFileOrScript(
-    protected Object processFileOrScript(ScriptEngine scriptEngine, Bindings bindings) throws IOException, ScriptException {
+    protected Object processFileOrScript(ScriptEngine scriptEngine, Bindings bindings)

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getName
* canRead
* put
* compile
* get
* getAbsolutePath
* isEmpty
* length
* createBindings
* eval
* equals
* exists
* lastModified
* closeQuietly
********************************************
********************************************
