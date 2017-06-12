/30/report.java
Satd-method: public void doAction(ActionEvent e)
********************************************
********************************************
/30/Between/Bug 51822  d6b7c4fd0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        guiPackage.getMainFrame().setMainPanel((javax.swing.JComponent) guiPackage.getCurrentGui());
+        JMeterGUIComponent currentGui = guiPackage.getCurrentGui();
+        guiPackage.getMainFrame().setMainPanel((javax.swing.JComponent) currentGui);
-        if (!(guiPackage.getCurrentGui() instanceof NamePanel)) {
+        if (!(currentGui instanceof NamePanel)) {

Lines added: 3. Lines removed: 2. Tot = 5
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
doAction(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* createPopupMenu
* getMainFrame
* getCurrentNode
* getCurrentGui
* getTreeListener
* getInstance
* setFileSaveEnabled
* setFileLoadEnabled
********************************************
********************************************
