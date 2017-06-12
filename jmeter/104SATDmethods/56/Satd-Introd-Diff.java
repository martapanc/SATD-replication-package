diff --git a/src/core/org/apache/jmeter/gui/action/EditCommand.java b/src/core/org/apache/jmeter/gui/action/EditCommand.java
index 7fdb9c439..7026b991d 100644
--- a/src/core/org/apache/jmeter/gui/action/EditCommand.java
+++ b/src/core/org/apache/jmeter/gui/action/EditCommand.java
@@ -1,52 +1,54 @@
 package org.apache.jmeter.gui.action;
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.JMeterGUIComponent;
 import org.apache.jmeter.gui.NamePanel;
 
 /**
  * @author    Michael Stover
  * @version   $Revision$
  */
 public class EditCommand implements Command
 {
     private static Set commands = new HashSet();
     static
     {
         commands.add("edit");
     }
 
     public EditCommand()
     {
     }
 
     public void doAction(ActionEvent e)
     {
         GuiPackage guiPackage = GuiPackage.getInstance();
         guiPackage.getMainFrame().setMainPanel(
             (javax.swing.JComponent) guiPackage.getCurrentGui());
         guiPackage.getMainFrame().setEditMenu(
             ((JMeterGUIComponent) guiPackage
                 .getTreeListener()
                 .getCurrentNode())
                 .createPopupMenu());
+        // TODO: I believe the following code (to the end of the method) is obsolete,
+        // since NamePanel no longer seems to be the GUI for any component:
         if (!(guiPackage.getCurrentGui() instanceof NamePanel))
         {
             guiPackage.getMainFrame().setFileLoadEnabled(true);
             guiPackage.getMainFrame().setFileSaveEnabled(true);
         }
         else
         {
             guiPackage.getMainFrame().setFileLoadEnabled(false);
             guiPackage.getMainFrame().setFileSaveEnabled(false);
         }
     }
 
     public Set getActionNames()
     {
         return commands;
     }
 }
