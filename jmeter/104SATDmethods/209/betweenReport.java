/209/report.java
Satd-method: public void valueChanged(TreeSelectionEvent e) {
********************************************
********************************************
/209/Between/Bug 36726  dd20f3a4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+                    // Reset search
+                    searchTextExtension.resetTextToFind();
+                    
+                        if (!searchTextExtension.isEnabled()) {
+                            searchTextExtension.setEnabled(true);
+                        }
+                            if (searchTextExtension.isEnabled()) {
+                                searchTextExtension.setEnabled(false);
+                            }

Lines added: 9. Lines removed: 0. Tot = 9
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/209/Between/Bug 42582  e09e4965_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+							} else if (command.equals(JSON_COMMAND)) {
+								showRenderJSONResponse(response);

Lines added: 2. Lines removed: 0. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
—————————
Method found in diff:	public void error(SAXParseException exception) throws SAXParseException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/209/Between/Bug 43450  6f9771e8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+						statsBuff.append("Error Count: ").append(res.getErrorCount()).append(NL);

Lines added: 1. Lines removed: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
—————————
Method found in diff:	public void error(SAXParseException exception) throws SAXParseException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/209/Between/Bug 47137  447cb7f4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                            sb.append("\nRequest Headers:\n");
+                            sb.append("\n"); //$NON-NLS-1$
+                            sb.append(JMeterUtils.getResString("view_results_request_headers")); //$NON-NLS-1$
+                            sb.append("\n"); //$NON-NLS-1$
-                    statsBuff.append("Thread Name: ").append(res.getThreadName()).append(NL);
+                    statsBuff.append(JMeterUtils.getResString("view_results_thread_name")).append(res.getThreadName()).append(NL); //$NON-NLS-1$
-                    statsBuff.append("Sample Start: ").append(startTime).append(NL);
-                    statsBuff.append("Load time: ").append(res.getTime()).append(NL);
-                    statsBuff.append("Latency: ").append(res.getLatency()).append(NL);
-                    statsBuff.append("Size in bytes: ").append(res.getBytes()).append(NL);
-                    statsBuff.append("Sample Count: ").append(res.getSampleCount()).append(NL);
-                    statsBuff.append("Error Count: ").append(res.getErrorCount()).append(NL);
+                    statsBuff.append(JMeterUtils.getResString("view_results_sample_start")).append(startTime).append(NL); //$NON-NLS-1$
+                    statsBuff.append(JMeterUtils.getResString("view_results_load_time")).append(res.getTime()).append(NL); //$NON-NLS-1$
+                    statsBuff.append(JMeterUtils.getResString("view_results_latency")).append(res.getLatency()).append(NL); //$NON-NLS-1$
+                    statsBuff.append(JMeterUtils.getResString("view_results_size_in_bytes")).append(res.getBytes()).append(NL); //$NON-NLS-1$
+                    statsBuff.append(JMeterUtils.getResString("view_results_sample_count")).append(res.getSampleCount()).append(NL); //$NON-NLS-1$
+                    statsBuff.append(JMeterUtils.getResString("view_results_error_count")).append(res.getErrorCount()).append(NL); //$NON-NLS-1$
-                    statsBuff.append("Response code: ").append(responseCode).append(NL);
+                    statsBuff.append(JMeterUtils.getResString("view_results_response_code")).append(responseCode).append(NL); //$NON-NLS-1$
-                    statsBuff.append("Response message: ").append(responseMsgStr).append(NL);
+                    statsBuff.append(JMeterUtils.getResString("view_results_response_message")).append(responseMsgStr).append(NL); //$NON-NLS-1$
-                    statsBuff.append("Response headers:").append(NL);
+                    statsBuff.append(JMeterUtils.getResString("view_results_response_headers")).append(NL); //$NON-NLS-1$
-                    statsBuff.append(samplerClass.substring(1+samplerClass.lastIndexOf('.'))).append(" fields:").append(NL);
+                    statsBuff.append(samplerClass.substring(1+samplerClass.lastIndexOf('.'))).append(" " + JMeterUtils.getResString("view_results_fields")).append(NL); //$NON-NLS-1$
-                    statsBuff.append("Assertion error: ").append(res.isError()).append(NL);
-                    statsBuff.append("Assertion failure: ").append(res.isFailure()).append(NL);
-                    statsBuff.append("Assertion failure message : ").append(res.getFailureMessage()).append(NL);
+                    statsBuff.append(JMeterUtils.getResString("view_results_assertion_error")).append(res.isError()).append(NL); //$NON-NLS-1$
+                    statsBuff.append(JMeterUtils.getResString("view_results_assertion_failure")).append(res.isFailure()).append(NL); //$NON-NLS-1$
+                    statsBuff.append(JMeterUtils.getResString("view_results_assertion_failure_message")).append(res.getFailureMessage()).append(NL); //$NON-NLS-1$

Lines added: 17. Lines removed: 15. Tot = 32
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
—————————
Method found in diff:	public String toString() {
-        String desc = "Shows the text results of sampling in tree form";
+        String desc = JMeterUtils.getResString("view_results_desc"); //$NON-NLS-1$

Lines added: 1. Lines removed: 1. Tot = 2
********************************************
********************************************
/209/Between/Bug 47474  e5c10847_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        log.debug("Start : valueChanged1");
+        lastSelectionEvent = e;
-        if (log.isDebugEnabled()) {
-            log.debug("valueChanged : selected node - " + node);
-        }
-
-        StyledDocument statsDoc = stats.getStyledDocument();
-        try {
-            statsDoc.remove(0, statsDoc.getLength());
-            sampleDataField.setText(""); // $NON-NLS-1$
-            results.setText(""); // $NON-NLS-1$
-            if (node != null) {
-                Object userObject = node.getUserObject();
-                if(userObject instanceof SampleResult) {
-                    SampleResult res = (SampleResult) userObject;
-
-                    // We are displaying a SampleResult
-                    setupTabPaneForSampleResult();
-
-                    if (log.isDebugEnabled()) {
-                        log.debug("valueChanged1 : sample result - " + res);
-                        log.debug("valueChanged1 : load time - " + res.getTime());
-                    }
-
-                    String sd = res.getSamplerData();
-                    if (sd != null) {
-                        String rh = res.getRequestHeaders();
-                        if (rh != null) {
-                            StringBuilder sb = new StringBuilder(sd.length() + rh.length()+20);
-                            sb.append(sd);
-                            sb.append("\n"); //$NON-NLS-1$
-                            sb.append(JMeterUtils.getResString("view_results_request_headers")); //$NON-NLS-1$
-                            sb.append("\n"); //$NON-NLS-1$
-                            sb.append(rh);
-                            sd = sb.toString();
-                        }
-                        sampleDataField.setText(sd);
-                    }
-
-                    StringBuilder statsBuff = new StringBuilder(200);
-                    statsBuff.append(JMeterUtils.getResString("view_results_thread_name")).append(res.getThreadName()).append(NL); //$NON-NLS-1$
-                    String startTime = dateFormat.format(new Date(res.getStartTime()));
-                    statsBuff.append(JMeterUtils.getResString("view_results_sample_start")).append(startTime).append(NL); //$NON-NLS-1$
-                    statsBuff.append(JMeterUtils.getResString("view_results_load_time")).append(res.getTime()).append(NL); //$NON-NLS-1$
-                    statsBuff.append(JMeterUtils.getResString("view_results_latency")).append(res.getLatency()).append(NL); //$NON-NLS-1$
-                    statsBuff.append(JMeterUtils.getResString("view_results_size_in_bytes")).append(res.getBytes()).append(NL); //$NON-NLS-1$
-                    statsBuff.append(JMeterUtils.getResString("view_results_sample_count")).append(res.getSampleCount()).append(NL); //$NON-NLS-1$
-                    statsBuff.append(JMeterUtils.getResString("view_results_error_count")).append(res.getErrorCount()).append(NL); //$NON-NLS-1$
-                    statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
-                    statsBuff = new StringBuilder(); //reset for reuse
-
-                    String responseCode = res.getResponseCode();
-                    if (log.isDebugEnabled()) {
-                        log.debug("valueChanged1 : response code - " + responseCode);
-                    }
-                    int responseLevel = 0;
-                    if (responseCode != null) {
-                        try {
-                            responseLevel = Integer.parseInt(responseCode) / 100;
-                        } catch (NumberFormatException numberFormatException) {
-                            // no need to change the foreground color
-                        }
-                    }
-
-                    Style style = null;
-                    switch (responseLevel) {
-                    case 3:
-                        style = statsDoc.getStyle(STYLE_REDIRECT);
-                        break;
-                    case 4:
-                        style = statsDoc.getStyle(STYLE_CLIENT_ERROR);
-                        break;
-                    case 5:
-                        style = statsDoc.getStyle(STYLE_SERVER_ERROR);
-                        break;
-                    }
-
-                    statsBuff.append(JMeterUtils.getResString("view_results_response_code")).append(responseCode).append(NL); //$NON-NLS-1$
-                    statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), style);
-                    statsBuff = new StringBuilder(100); //reset for reuse
-
-                    // response message label
-                    String responseMsgStr = res.getResponseMessage();
-
-                    if (log.isDebugEnabled()) {
-                        log.debug("valueChanged1 : response message - " + responseMsgStr);
-                    }
-                    statsBuff.append(JMeterUtils.getResString("view_results_response_message")).append(responseMsgStr).append(NL); //$NON-NLS-1$
-
-                    statsBuff.append(NL);
-                    statsBuff.append(JMeterUtils.getResString("view_results_response_headers")).append(NL); //$NON-NLS-1$
-                    statsBuff.append(res.getResponseHeaders()).append(NL);
-                    statsBuff.append(NL);
-                    final String samplerClass = res.getClass().getName();
-                    statsBuff.append(samplerClass.substring(1+samplerClass.lastIndexOf('.'))).append(" " + JMeterUtils.getResString("view_results_fields")).append(NL); //$NON-NLS-1$
-                    statsBuff.append("ContentType: ").append(res.getContentType()).append(NL);
-                    statsBuff.append("DataEncoding: ").append(res.getDataEncodingNoDefault()).append(NL);
-                    statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
-                    statsBuff = null; // Done
-
-                    // Reset search
-                    searchTextExtension.resetTextToFind();
-                    
-                    // get the text response and image icon
-                    // to determine which is NOT null
-                    if ((SampleResult.TEXT).equals(res.getDataType())) // equals(null) is OK
-                    {
-                        String response = getResponseAsString(res);
-                        if (command.equals(TEXT_COMMAND)) {
-                            showTextResponse(response);
-                        } else if (command.equals(HTML_COMMAND)) {
-                            showRenderedResponse(response, res);
-                        } else if (command.equals(JSON_COMMAND)) {
-                            showRenderJSONResponse(response);
-                        } else if (command.equals(XML_COMMAND)) {
-                            showRenderXMLResponse(res);
-                        }
-                        if (!searchTextExtension.isEnabled()) {
-                            searchTextExtension.setEnabled(true);
-                        }
-                    } else {
-                        byte[] responseBytes = res.getResponseData();
-                        if (responseBytes != null) {
-                            showImage(new ImageIcon(responseBytes)); //TODO implement other non-text types
-                            if (searchTextExtension.isEnabled()) {
-                                searchTextExtension.setEnabled(false);
-                            }
-                        }
+        if (node != null) {
+            // to restore last tab used
+            if (rightSide.getTabCount() > selectedTab) {
+                resultsRender.setLastSelectedTab(rightSide.getSelectedIndex());
+            }
+            Object userObject = node.getUserObject();
+            resultsRender.setSamplerResult(userObject);
+            resultsRender.setupTabPane();
+            // display a SampleResult
+            if (userObject instanceof SampleResult) {
+                SampleResult sampleResult = (SampleResult) userObject;
+                if ((SampleResult.TEXT).equals(sampleResult.getDataType())){
+                    resultsRender.renderResult(sampleResult);
+                } else {
+                    byte[] responseBytes = sampleResult.getResponseData();
+                    if (responseBytes != null) {
+                        resultsRender.renderImage(sampleResult);
-                else if(userObject instanceof AssertionResult) {
-                    AssertionResult res = (AssertionResult) userObject;
-
-                    // We are displaying an AssertionResult
-                    setupTabPaneForAssertionResult();
-
-                    if (log.isDebugEnabled()) {
-                        log.debug("valueChanged1 : sample result - " + res);
-                    }
-
-                    StringBuilder statsBuff = new StringBuilder(100);
-                    statsBuff.append(JMeterUtils.getResString("view_results_assertion_error")).append(res.isError()).append(NL); //$NON-NLS-1$
-                    statsBuff.append(JMeterUtils.getResString("view_results_assertion_failure")).append(res.isFailure()).append(NL); //$NON-NLS-1$
-                    statsBuff.append(JMeterUtils.getResString("view_results_assertion_failure_message")).append(res.getFailureMessage()).append(NL); //$NON-NLS-1$
-                    statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
-                    statsBuff = null;
-                }
-        } catch (BadLocationException exc) {
-            log.error("Error setting statistics text", exc);
-            stats.setText("");
-        log.debug("End : valueChanged1");
-    }
-
-    private void showImage(Icon image) {
-        imageLabel.setIcon(image);
-        resultsScrollPane.setViewportView(imageLabel);
-        setEnabledButtons(false);

Lines added: 18. Lines removed: 154. Tot = 172
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
valueChanged(
+                    this.valueChanged(lastSelectionEvent);

Lines added containing method: 1. Lines removed containing method: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getDataType
* getBytes
* getStartTime
* getLastSelectedPathComponent
* format
* isFailure
* getUserObject
* getRequestHeaders
* getResponseData
* getThreadName
* isDebugEnabled
* getTime
* getResponseMessage
* error
* remove
* getStyle
* isError
* getResponseHeaders
* insertString
* debug
* length
* getFailureMessage
* parseInt
* getSamplerData
* getStyledDocument
* getResponseCode
* equals
* getLength
* toString
* append
* setText
—————————
Method found in diff:	-    public String toString() {
-    public String toString() {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
