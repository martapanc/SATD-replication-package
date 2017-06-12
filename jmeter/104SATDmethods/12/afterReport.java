/12/report.java
Satd-method: public AssertionResult getResult(SampleResult inResponse) {
********************************************
********************************************
/12/After/Bug 60564  0af7ce0e4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            if (log.isDebugEnabled()){
-                log.debug("HTMLAssertions.getResult(): Setup tidy ...");
-                log.debug("doctype: " + getDoctype());
-                log.debug("errors only: " + isErrorsOnly());
-                log.debug("error threshold: " + getErrorThreshold());
-                log.debug("warning threshold: " + getWarningThreshold());
-                log.debug("html mode: " + isHTML());
-                log.debug("xhtml mode: " + isXHTML());
-                log.debug("xml mode: " + isXML());
+            if (log.isDebugEnabled()) {
+                log.debug(
+                        "Setting up tidy... doctype: {}, errors only: {}, error threshold: {}, warning threshold: {}, html mode: {}, xhtml mode: {}, xml mode: {}.",
+                        getDoctype(), isErrorsOnly(), getErrorThreshold(), getWarningThreshold(), isHTML(), isXHTML(),
+                        isXML());
-                log.debug("err file: " + getFilename());
-                log.debug("getParser : tidy parser created - " + tidy);
-                log.debug("HTMLAssertions.getResult(): Tidy instance created!");
+                log.debug("Tidy instance created... err file: {}, tidy parser: {}", getFilename(), tidy);
-            log.debug("Start : parse");
+            log.debug("Parsing with tidy starting...");
-            if (log.isDebugEnabled()) {
-                log.debug("node : " + node);
-                log.debug("End   : parse");
-                log.debug("HTMLAssertions.getResult(): parsing with tidy done!");
-                log.debug("Output: " + os.toString());
-            }
+            log.debug("Parsing with tidy done! node: {}, output: {}", node, os);
-                if (log.isDebugEnabled()) {
-                    log.debug("HTMLAssertions.getResult(): errors/warnings detected:");
-                    log.debug(errbuf.toString());
-                }
+                log.debug("Errors/warnings detected while parsing with tidy: {}", errbuf);

Lines added: 9. Lines removed: 23. Tot = 32
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getResult(
-                log.debug("HTMLAssertions.getResult(): Setup tidy ...");
-                log.debug("HTMLAssertions.getResult(): Tidy instance created!");
-                log.debug("HTMLAssertions.getResult(): parsing with tidy done!");
-                    log.debug("HTMLAssertions.getResult(): errors/warnings detected:");

Lines added containing method: 0. Lines removed containing method: 4. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setOnlyErrors
* setXHTML
* setErrfile
* getParseWarnings
* getParseErrors
* format
* warn
* setFailure
* getResponseData
* setCharEncoding
* isDebugEnabled
* setXmlTags
* error
* setShowWarnings
* setDocType
* debug
* setQuiet
* getMessage
* setErrout
* parse
* setFailureMessage
* setResultForNull
* toString
—————————
Method found in diff:	public void setXHTML() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void error(SAXParseException exception) throws SAXParseException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setShowWarnings(boolean val) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setQuiet(boolean val) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
