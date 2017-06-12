/208/report.java
Satd-method: private static final Logger log = LoggingManager.getLoggerForClass();
********************************************
********************************************
/208/After/Bug 60564  24c8763e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
log 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getLoggerForClass
********************************************
********************************************
/208/After/Bug 60564  2c275677_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-    private static final Logger log = LoggingManager.getLoggerForClass();

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
log 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AxisGraph.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BSFListener.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BeanShellListener.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Graph.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JSR223Listener.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(LineGraph.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(MailerVisualizer.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RenderAsDocument.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RenderAsHTML.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RenderAsXML.class);
+    private static final Logger log = LoggerFactory.getLogger(RenderAsXPath.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RequestPanel.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RespTimeGraphChart.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RespTimeGraphVisualizer.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SearchTextExtension.class);
+    private static final Logger log = LoggerFactory.getLogger(SearchTreePanel.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(StatGraphVisualizer.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(XMLDefaultMutableTreeNode.class);
+    private static final Logger log = LoggerFactory.getLogger(AbstractBackendListenerClient.class);
+    private static final Logger log = LoggerFactory.getLogger(BackendListener.class);
+    private static final Logger log = LoggerFactory.getLogger(BackendListenerContext.class);
+    private static final Logger log = LoggerFactory.getLogger(BackendListenerGui.class);
+    private static final Logger log = LoggerFactory.getLogger(GraphiteBackendListenerClient.class);
+    private static final Logger log = LoggerFactory.getLogger(PickleGraphiteMetricsSender.class);
+    private static final Logger log = LoggerFactory.getLogger(TextGraphiteMetricsSender.class);
+    private static final Logger log = LoggerFactory.getLogger(HttpMetricsSender.class);
+    private static final Logger log = LoggerFactory.getLogger(InfluxdbBackendListenerClient.class);
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Colors.class);

Lines added containing method: 28. Lines removed containing method: 17. Tot = 45
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getLoggerForClass
********************************************
********************************************
