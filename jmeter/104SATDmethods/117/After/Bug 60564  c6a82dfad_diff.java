diff --git a/src/core/org/apache/jmeter/gui/action/AbstractAction.java b/src/core/org/apache/jmeter/gui/action/AbstractAction.java
index 64989e423..cdd1342a9 100644
--- a/src/core/org/apache/jmeter/gui/action/AbstractAction.java
+++ b/src/core/org/apache/jmeter/gui/action/AbstractAction.java
@@ -1,63 +1,63 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public abstract class AbstractAction implements Command {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AbstractAction.class);
 
     /**
      * @see Command#doAction(ActionEvent)
      */
     @Override
     public void doAction(ActionEvent e) throws IllegalUserActionException {
     }
 
     /**
      * @see Command#getActionNames()
      */
     @Override
     abstract public Set<String> getActionNames();
 
     /**
      * @param e the event that led to the call of this method
      */
     protected void popupShouldSave(ActionEvent e) {
         log.debug("popupShouldSave");
         if (GuiPackage.getInstance().getTestPlanFile() == null) {
             if (JOptionPane.showConfirmDialog(GuiPackage.getInstance().getMainFrame(),
                     JMeterUtils.getResString("should_save"),  //$NON-NLS-1$
                     JMeterUtils.getResString("warning"),  //$NON-NLS-1$
                     JOptionPane.YES_NO_OPTION,
                     JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                 ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(),ActionNames.SAVE));
             }
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/ActionRouter.java b/src/core/org/apache/jmeter/gui/action/ActionRouter.java
index 926b88bd1..ff66159a3 100644
--- a/src/core/org/apache/jmeter/gui/action/ActionRouter.java
+++ b/src/core/org/apache/jmeter/gui/action/ActionRouter.java
@@ -1,350 +1,352 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.HeadlessException;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import javax.swing.SwingUtilities;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.jorphan.util.JMeterError;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public final class ActionRouter implements ActionListener {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ActionRouter.class);
 
     // This is cheap, so no need to resort to IODH or lazy init
     private static final ActionRouter INSTANCE = new ActionRouter();
 
     private final Map<String, Set<Command>> commands = new HashMap<>();
 
     private final Map<String, Set<ActionListener>> preActionListeners =
             new HashMap<>();
 
     private final Map<String, Set<ActionListener>> postActionListeners =
             new HashMap<>();
 
     private ActionRouter() {
     }
 
     @Override
     public void actionPerformed(final ActionEvent e) {
         SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                 performAction(e);
             }
 
         });
     }
 
     private void performAction(final ActionEvent e) {
         String actionCommand = e.getActionCommand();
         try {
             try {
                 GuiPackage.getInstance().updateCurrentGui();
             } catch (Exception err){
-                log.error("performAction(" + actionCommand + ") updateCurrentGui() on" + e.toString() + " caused", err);
+                log.error("performAction({}) updateCurrentGui() on{} caused", actionCommand, e, err);
                 JMeterUtils.reportErrorToUser("Problem updating GUI - see log file for details");
             }
             for (Command c : commands.get(actionCommand)) {
                 try {
                     preActionPerformed(c.getClass(), e);
                     c.doAction(e);
                     postActionPerformed(c.getClass(), e);
                 } catch (IllegalUserActionException err) {
                     String msg = err.getMessage();
                     if (msg == null) {
                         msg = err.toString();
                     }
                     Throwable t = err.getCause();
                     if (t != null) {
                         String cause = t.getMessage();
                         if (cause == null) {
                             cause = t.toString();
                         }
                         msg = msg + "\n" + cause;
                     }
                     JMeterUtils.reportErrorToUser(msg);
                 } catch (Exception err) {
-                    log.error("Error processing "+c.toString(), err);
+                    log.error("Error processing {}", c, err);
                 }
             }
         } catch (NullPointerException er) {
-            log.error("performAction(" + actionCommand + ") " + e.toString() + " caused", er);
+            log.error("performAction({}) {} caused", actionCommand, e, er);
             JMeterUtils.reportErrorToUser("Sorry, this feature (" + actionCommand + ") not yet implemented");
         }
     }
 
     /**
      * To execute an action immediately in the current thread.
      *
      * @param e
      *            the action to execute
      */
     public void doActionNow(ActionEvent e) {
         performAction(e);
     }
 
     /**
      * Get the set of {@link Command}s registered under the name
      * <code>actionName</code>
      * 
      * @param actionName
      *            The name the {@link Command}s were registered
      * @return a set with all registered {@link Command}s for
      *         <code>actionName</code>
      */
     public Set<Command> getAction(String actionName) {
         Set<Command> set = new HashSet<>();
         for (Command c : commands.get(actionName)) {
             try {
                 set.add(c);
             } catch (Exception err) {
                 log.error("Could not add Command", err);
             }
         }
         return set;
     }
 
     /**
      * Get the {@link Command} registered under the name <code>actionName</code>,
      * that is of {@link Class} <code>actionClass</code>
      * 
      * @param actionName
      *            The name the {@link Command}s were registered
      * @param actionClass
      *            The class the {@link Command}s should be equal to
      * @return The registered {@link Command} for <code>actionName</code>, or
      *         <code>null</code> if none could be found
      */
     public Command getAction(String actionName, Class<?> actionClass) {
         for (Command com : commands.get(actionName)) {
             if (com.getClass().equals(actionClass)) {
                 return com;
             }
         }
         return null;
     }
 
     /**
      * Get the {@link Command} registered under the name <code>actionName</code>
      * , which class names are equal to <code>className</code>
      * 
      * @param actionName
      *            The name the {@link Command}s were registered
      * @param className
      *            The name of the class the {@link Command}s should be equal to
      * @return The {@link Command} for <code>actionName</code> or
      *         <code>null</code> if none could be found
      */
     public Command getAction(String actionName, String className) {
         for (Command com : commands.get(actionName)) {
             if (com.getClass().getName().equals(className)) {
                 return com;
             }
         }
         return null;
     }
 
     /**
      * Allows an ActionListener to receive notification of a command being
      * executed prior to the actual execution of the command.
      *
      * @param action
      *            the Class of the command for which the listener will
      *            notifications for. Class must extend
      *            org.apache.jmeter.gui.action.Command.
      * @param listener
      *            the ActionListener to receive the notifications
      */
     public void addPreActionListener(Class<?> action, ActionListener listener) {
         addActionListener(action, listener, preActionListeners);
     }
 
     /**
      * Allows an ActionListener to be removed from receiving notifications of a
      * command being executed prior to the actual execution of the command.
      *
      * @param action
      *            the Class of the command for which the listener will
      *            notifications for. Class must extend
      *            org.apache.jmeter.gui.action.Command.
      * @param listener
      *            the ActionListener to receive the notifications
      */
     public void removePreActionListener(Class<?> action, ActionListener listener) {
         removeActionListener(action, listener, preActionListeners);
     }
 
     /**
      * @param action {@link Class}
      * @param e {@link ActionListener}
      * @param actionListeners {@link Set}
      */
     private void removeActionListener(Class<?> action, ActionListener listener, Map<String, Set<ActionListener>> actionListeners) {
         if (action != null) {
             Set<ActionListener> set = actionListeners.get(action.getName());
             if (set != null) {
                 set.remove(listener);
                 actionListeners.put(action.getName(), set);
             }
         }
     }
 
     /**
      * Allows an ActionListener to receive notification of a command being
      * executed after the command has executed.
      *
      * @param action
      *            the Class of the command for which the listener will
      *            notifications for. Class must extend
      *            org.apache.jmeter.gui.action.Command.
      * @param listener
      *            The {@link ActionListener} to be registered
      */
     public void addPostActionListener(Class<?> action, ActionListener listener) {
         addActionListener(action, listener, postActionListeners);
     }
 
     /**
      * @param action {@link Class}
      * @param list {@link ActionListener}
      * @param actionListeners {@link Set}
      */
     private void addActionListener(Class<?> action, ActionListener listener, Map<String, Set<ActionListener>> actionListeners) {
         if (action != null) {
             Set<ActionListener> set = actionListeners.get(action.getName());
             if (set == null) {
                 set = new HashSet<>();
             }
             set.add(listener);
             actionListeners.put(action.getName(), set);
         }
     }
 
     /**
      * Allows an ActionListener to be removed from receiving notifications of a
      * command being executed after the command has executed.
      *
      * @param action
      *            the Class of the command for which the listener will
      *            notifications for. Class must extend
      *            org.apache.jmeter.gui.action.Command.
      * @param listener The {@link ActionListener} that should be deregistered
      */
     public void removePostActionListener(Class<?> action, ActionListener listener) {
         removeActionListener(action, listener, postActionListeners);
     }
 
     /**
      * @param action {@link Class}
      * @param e {@link ActionEvent}
      */
     protected void preActionPerformed(Class<? extends Command> action, ActionEvent e) {
         actionPerformed(action, e, preActionListeners);
     }
 
     /**
      * @param action {@link Class}
      * @param e {@link ActionEvent}
      */
     protected void postActionPerformed(Class<? extends Command> action, ActionEvent e) {
         actionPerformed(action, e, postActionListeners);
     }
 
     /**
      * @param action {@link Class}
      * @param e {@link ActionEvent}
      * @param actionListeners {@link Set}
      */
     private void actionPerformed(Class<? extends Command> action, ActionEvent e, Map<String, Set<ActionListener>> actionListeners) {
         if (action != null) {
             Set<ActionListener> listenerSet = actionListeners.get(action.getName());
             if (listenerSet != null && listenerSet.size() > 0) {
                 ActionListener[] listeners = listenerSet.toArray(new ActionListener[listenerSet.size()]);
                 for (ActionListener listener : listeners) {
                     listener.actionPerformed(e);
                 }
             }
         }
     }
 
     /**
      * Only for use by the JMeter.startGui.
      * This method must not be called by getInstance() as was done previously.
      * See <a href="https://bz.apache.org/bugzilla/show_bug.cgi?id=58790">Bug 58790</a> 
      */
     public void populateCommandMap() {
         if (!commands.isEmpty()) {
             return; // already done
         }
         try {
             List<String> listClasses = ClassFinder.findClassesThatExtend(
                     JMeterUtils.getSearchPaths(), // strPathsOrJars - pathnames or jarfiles to search for classes
                     // classNames - required parent class(es) or annotations
                     new Class[] {Class.forName("org.apache.jmeter.gui.action.Command") }, // $NON-NLS-1$
                     false, // innerClasses - should we include inner classes?
                     null, // contains - classname should contain this string
                     // Ignore the classes which are specific to the reporting tool
                     "org.apache.jmeter.report.gui", // $NON-NLS-1$ // notContains - classname should not contain this string
                     false); // annotations - true if classnames are annotations
             if (listClasses.isEmpty()) {
-                log.fatalError("!!!!!Uh-oh, didn't find any action handlers!!!!!");
+                log.error("!!!!!Uh-oh, didn't find any action handlers!!!!!");
                 throw new JMeterError("No action handlers found - check JMeterHome and libraries");
             }
             for (String strClassName : listClasses) {
                 Class<?> commandClass = Class.forName(strClassName);
                 Command command = (Command) commandClass.newInstance();
                 for (String commandName : command.getActionNames()) {
                     Set<Command> commandObjects = commands.get(commandName);
                     if (commandObjects == null) {
                         commandObjects = new HashSet<>();
                         commands.put(commandName, commandObjects);
                     }
                     commandObjects.add(command);
                 }
             }
-        } catch (HeadlessException e){
-            log.warn(e.toString());
+        } catch (HeadlessException e) {
+            if (log.isWarnEnabled()) {
+                log.warn("AWT headless exception occurred. {}", e.toString());
+            }
         } catch (Exception e) {
             log.error("exception finding action handlers", e);
         }
     }
 
     /**
      * Gets the Instance attribute of the ActionRouter class
      *
      * @return The Instance value
      */
     public static ActionRouter getInstance() {
         return INSTANCE;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/AddParent.java b/src/core/org/apache/jmeter/gui/action/AddParent.java
index 23b0a5970..960695352 100644
--- a/src/core/org/apache/jmeter/gui/action/AddParent.java
+++ b/src/core/org/apache/jmeter/gui/action/AddParent.java
@@ -1,83 +1,83 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.Component;
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.testelement.TestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Implements the Add Parent menu command
  */
 public class AddParent extends AbstractAction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AddParent.class);
 
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.ADD_PARENT);
     }
 
     public AddParent() {
     }
 
     @Override
     public void doAction(ActionEvent e) {
         String name = ((Component) e.getSource()).getName();
         GuiPackage guiPackage = GuiPackage.getInstance();
         try {
             guiPackage.updateCurrentNode();
             TestElement controller = guiPackage.createTestElement(name);
             addParentToTree(controller);
         } catch (Exception err) {
-            log.error("", err);
+            log.error("Exception while adding a TestElement.", err);
         }
 
     }
 
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     protected void addParentToTree(TestElement newParent) {
         GuiPackage guiPackage = GuiPackage.getInstance();
         JMeterTreeNode newNode = new JMeterTreeNode(newParent, guiPackage.getTreeModel());
         JMeterTreeNode currentNode = guiPackage.getTreeListener().getCurrentNode();
         JMeterTreeNode parentNode = (JMeterTreeNode) currentNode.getParent();
         int index = parentNode.getIndex(currentNode);
         guiPackage.getTreeModel().insertNodeInto(newNode, parentNode, index);
         JMeterTreeNode[] nodes = guiPackage.getTreeListener().getSelectedNodes();
         for (JMeterTreeNode node : nodes) {
             moveNode(guiPackage, node, newNode);
         }
     }
 
     private void moveNode(GuiPackage guiPackage, JMeterTreeNode node, JMeterTreeNode newParentNode) {
         guiPackage.getTreeModel().removeNodeFromParent(node);
         guiPackage.getTreeModel().insertNodeInto(node, newParentNode, newParentNode.getChildCount());
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/AddThinkTimeBetweenEachStep.java b/src/core/org/apache/jmeter/gui/action/AddThinkTimeBetweenEachStep.java
index a8f3847b9..5fe77f9d4 100644
--- a/src/core/org/apache/jmeter/gui/action/AddThinkTimeBetweenEachStep.java
+++ b/src/core/org/apache/jmeter/gui/action/AddThinkTimeBetweenEachStep.java
@@ -1,152 +1,152 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.Toolkit;
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.action.thinktime.ThinkTimeCreator;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.ThreadGroup;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Add ThinkTime (TestAction + UniformRandomTimer)
  * @since 3.2
  */
 public class AddThinkTimeBetweenEachStep extends AbstractAction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AddThinkTimeBetweenEachStep.class);
 
     private static final Set<String> commands = new HashSet<>();
     
     private static final String DEFAULT_IMPLEMENTATION = 
             JMeterUtils.getPropDefault("think_time_creator.impl", 
                     "org.apache.jmeter.thinktime.DefaultThinkTimeCreator");
     static {
         commands.add(ActionNames.ADD_THINK_TIME_BETWEEN_EACH_STEP);
     }
 
     /**
      * 
      */
     public AddThinkTimeBetweenEachStep() {
         super();
     }
 
     @Override
     public void doAction(ActionEvent e) {
         GuiPackage guiPackage = GuiPackage.getInstance();
         JMeterTreeNode currentNode = guiPackage.getTreeListener().getCurrentNode();
         if (!
                 (currentNode.getUserObject() instanceof Controller ||
                         currentNode.getUserObject() instanceof ThreadGroup)
                 ) {
             Toolkit.getDefaultToolkit().beep();
             return;
         }
         try {
             addThinkTimeToChildren(guiPackage, currentNode);            
         } catch (Exception err) {
             Toolkit.getDefaultToolkit().beep();
             log.error("Failed to add think times", err);
             JMeterUtils.reportErrorToUser("Failed to add think times", err);
         }
     }
 
     /**
      * Add Think Time to children of parentNode
      * @param guiPackage {@link GuiPackage}
      * @param parentNode Parent node of elements on which we add think times
      * @throws IllegalUserActionException 
      */
     private void addThinkTimeToChildren(GuiPackage guiPackage, 
             JMeterTreeNode parentNode) throws IllegalUserActionException {
         guiPackage.updateCurrentNode();
         boolean insertThinkTime = false;
         try {
             int index = 0;
             while(true) {
                 if(index == parentNode.getChildCount()) {
                     index++;
                     break;
                 }
                 JMeterTreeNode childNode = (JMeterTreeNode) parentNode.getChildAt(index);
                 Object userObject = childNode.getUserObject();
                 if(userObject instanceof Sampler ||
                         userObject instanceof Controller) {
                     insertThinkTime = true;                
                 }
                 if(insertThinkTime) {
                     JMeterTreeNode[] nodes = createThinkTime(guiPackage, parentNode);
                     if(nodes.length != 2) {
                         throw new IllegalArgumentException("Invalid Think Time, expected 2 nodes, got:"+nodes.length);
                     }
                     index++;
                     addNodesToTreeHierachically(guiPackage, parentNode, nodes, index);
                     insertThinkTime = false;
                 }
                 index++;
             }
         } catch(Exception ex) {
             throw new IllegalUserActionException("Cannot add think times", ex);
         }
     }
 
     /**
      * add nodes to JMeter Tree
      * @param guiPackage {@link GuiPackage}
      * @param parentNode {@link JMeterTreeNode}
      * @param childNodes Child nodes
      * @param index insertion index
      */
     private void addNodesToTreeHierachically(GuiPackage guiPackage, 
             JMeterTreeNode parentNode, 
             JMeterTreeNode[] childNodes, 
             int index) {
         guiPackage.getTreeModel().insertNodeInto(childNodes[0], parentNode, index);    
         guiPackage.getTreeModel().insertNodeInto(childNodes[1], childNodes[0], 0);
     }
 
     /**
      * 
      * @param guiPackage {@link GuiPackage}
      * @param parentNode {@link JMeterTreeNode}
      * @return array of {@link JMeterTreeNode}
      * @throws IllegalUserActionException
      */
     private JMeterTreeNode[] createThinkTime(GuiPackage guiPackage, JMeterTreeNode parentNode) 
         throws Exception {
         Class<?> clazz = Class.forName(DEFAULT_IMPLEMENTATION);
         ThinkTimeCreator thinkTimeCreator = (ThinkTimeCreator) clazz.newInstance();
         return thinkTimeCreator.createThinkTime(guiPackage, parentNode);
     }
     
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/AddToTree.java b/src/core/org/apache/jmeter/gui/action/AddToTree.java
index 516130ecd..46a747811 100644
--- a/src/core/org/apache/jmeter/gui/action/AddToTree.java
+++ b/src/core/org/apache/jmeter/gui/action/AddToTree.java
@@ -1,82 +1,82 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JComponent;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class AddToTree extends AbstractAction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AddToTree.class);
 
     private static final Set<String> commandSet;
 
     static {
         Set<String> commands = new HashSet<>();
         commands.add(ActionNames.ADD);
         commandSet = Collections.unmodifiableSet(commands);
     }
 
     public AddToTree() {
     }
 
     /**
      * Gets the Set of actions this Command class responds to.
      *
      * @return the ActionNames value
      */
     @Override
     public Set<String> getActionNames() {
         return commandSet;
     }
 
     /**
      * Adds the specified class to the current node of the tree.
      */
     @Override
     public void doAction(ActionEvent e) {
         GuiPackage guiPackage = GuiPackage.getInstance();
         try {
             guiPackage.updateCurrentNode();
             TestElement testElement = guiPackage.createTestElement(((JComponent) e.getSource()).getName());
             JMeterTreeNode parentNode = guiPackage.getCurrentNode();
             JMeterTreeNode node = guiPackage.getTreeModel().addComponent(testElement, parentNode);
             guiPackage.getNamingPolicy().nameOnCreation(node);
             guiPackage.getMainFrame().getTree().setSelectionPath(new TreePath(node.getPath()));
         } catch (Exception err) {
-            log.error("", err); // $NON-NLS-1$
+            log.error("Exception while adding a component to tree.", err); // $NON-NLS-1$
             String msg = err.getMessage();
             if (msg == null) {
                 msg = err.toString();
             }
             JMeterUtils.reportErrorToUser(msg);
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/ApplyNamingConvention.java b/src/core/org/apache/jmeter/gui/action/ApplyNamingConvention.java
index 29ab95009..771e9f2bc 100644
--- a/src/core/org/apache/jmeter/gui/action/ApplyNamingConvention.java
+++ b/src/core/org/apache/jmeter/gui/action/ApplyNamingConvention.java
@@ -1,92 +1,92 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.Toolkit;
 import java.awt.event.ActionEvent;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Allows to apply naming convention on nodes
  * @since 3.2
  */
 public class ApplyNamingConvention extends AbstractAction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ApplyNamingConvention.class);
 
     private static final Set<String> commands = new HashSet<>();
     
 
     static {
         commands.add(ActionNames.APPLY_NAMING_CONVENTION);
     }
 
     public ApplyNamingConvention() {
     }
 
     @Override
     public void doAction(ActionEvent e) {
         GuiPackage guiPackage = GuiPackage.getInstance();
         JMeterTreeNode currentNode = guiPackage.getTreeListener().getCurrentNode();
         if (!(currentNode.getUserObject() instanceof Controller)) {
             Toolkit.getDefaultToolkit().beep();
             return;
         }
         try {
             applyNamingPolicyToCurrentNode(guiPackage, currentNode);            
         } catch (Exception err) {
             Toolkit.getDefaultToolkit().beep();
             log.error("Failed to apply naming policy", err);
             JMeterUtils.reportErrorToUser("Failed to apply naming policy", err);
         }
 
     }
 
     /**
      * Apply the naming policy of currentNode children
      * @param guiPackage {@link GuiPackage}
      * @param currentNode Parent node of elements on which we apply naming policy
      */
     private void applyNamingPolicyToCurrentNode(GuiPackage guiPackage, 
             JMeterTreeNode currentNode) {
         TreeNodeNamingPolicy namingPolicy = guiPackage.getNamingPolicy();
         guiPackage.updateCurrentNode();
         Enumeration<JMeterTreeNode> enumeration = currentNode.children();
         int index = 0;
         namingPolicy.resetState(currentNode);
         while(enumeration.hasMoreElements()) {
             JMeterTreeNode childNode = enumeration.nextElement();
             namingPolicy.rename(currentNode, childNode, index);
             index++;
         }        
     }
 
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/ChangeLanguage.java b/src/core/org/apache/jmeter/gui/action/ChangeLanguage.java
index f3124e071..3185fa541 100644
--- a/src/core/org/apache/jmeter/gui/action/ChangeLanguage.java
+++ b/src/core/org/apache/jmeter/gui/action/ChangeLanguage.java
@@ -1,73 +1,73 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.Component;
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Locale;
 import java.util.Set;
 
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Change language
  */
 public class ChangeLanguage extends AbstractActionWithNoRunningTest {
     private static final Set<String> commands = new HashSet<>();
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ChangeLanguage.class);
 
     static {
         commands.add(ActionNames.CHANGE_LANGUAGE);
     }
 
     /**
      * @see org.apache.jmeter.gui.action.AbstractActionWithNoRunningTest#doActionAfterCheck(ActionEvent)
      */
     @Override
     public void doActionAfterCheck(ActionEvent e) {
         String locale = ((Component) e.getSource()).getName();
         Locale loc;
 
         int sep = locale.indexOf('_');
         if (sep > 0) {
             loc = new Locale(locale.substring(0, sep), locale.substring(sep + 1));
         } else {
             loc = new Locale(locale, "");
         }
-        log.debug("Changing locale to " + loc.toString());
+        log.debug("Changing locale to {}", loc);
         try {
             JMeterUtils.setLocale(loc);
         } catch (JMeterError err) {
             JMeterUtils.reportErrorToUser(err.toString());
         }
     }
 
     /**
      * @see org.apache.jmeter.gui.action.Command#getActionNames()
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/ChangeParent.java b/src/core/org/apache/jmeter/gui/action/ChangeParent.java
index 1b62c13b9..a2da1dad0 100644
--- a/src/core/org/apache/jmeter/gui/action/ChangeParent.java
+++ b/src/core/org/apache/jmeter/gui/action/ChangeParent.java
@@ -1,113 +1,113 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.Component;
 import java.awt.Toolkit;
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JTree;
 import javax.swing.tree.TreeNode;
 import javax.swing.tree.TreePath;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.JMeterGUIComponent;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Allows to change Controller implementation
  */
 public class ChangeParent extends AbstractAction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ChangeParent.class);
 
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.CHANGE_PARENT);
     }
 
     public ChangeParent() {
     }
 
     @Override
     public void doAction(ActionEvent e) {
         String name = ((Component) e.getSource()).getName();
         GuiPackage guiPackage = GuiPackage.getInstance();
         JMeterTreeNode currentNode = guiPackage.getTreeListener().getCurrentNode();
         if (!(currentNode.getUserObject() instanceof Controller)) {
             Toolkit.getDefaultToolkit().beep();
             return;
         }
         try {
             guiPackage.updateCurrentNode();
             TestElement controller = guiPackage.createTestElement(name);
             changeParent(controller, guiPackage, currentNode);
         } catch (Exception err) {
             Toolkit.getDefaultToolkit().beep();
             log.error("Failed to change parent", err);
         }
 
     }
 
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     private void changeParent(TestElement newParent, GuiPackage guiPackage, JMeterTreeNode currentNode) {
         
         // keep the old name if it was not the default one
         Controller currentController = (Controller) currentNode.getUserObject();
         JMeterGUIComponent currentGui = guiPackage.getCurrentGui();
         String defaultName = JMeterUtils.getResString(currentGui.getLabelResource());
         if(StringUtils.isNotBlank(currentController.getName()) 
                 && !currentController.getName().equals(defaultName)){
             newParent.setName(currentController.getName());            
         }
 
         JMeterTreeModel treeModel = guiPackage.getTreeModel();
         JMeterTreeNode newNode = new JMeterTreeNode(newParent, treeModel);
         JMeterTreeNode parentNode = (JMeterTreeNode) currentNode.getParent();
         int index = parentNode.getIndex(currentNode);
         treeModel.insertNodeInto(newNode, parentNode, index);
         treeModel.removeNodeFromParent(currentNode);
         int childCount = currentNode.getChildCount();
         for (int i = 0; i < childCount; i++) {
             // Using index 0 is voluntary as child is removed in next step and added to new parent
             JMeterTreeNode node = (JMeterTreeNode) currentNode.getChildAt(0);
             treeModel.removeNodeFromParent(node);
             treeModel.insertNodeInto(node, newNode, newNode.getChildCount());
         }
 
         // select the node
         TreeNode[] nodes = treeModel.getPathToRoot(newNode);
         JTree tree = guiPackage.getTreeListener().getJTree();
         tree.setSelectionPath(new TreePath(nodes));
     }
 
 }
diff --git a/src/core/org/apache/jmeter/gui/action/CheckDirty.java b/src/core/org/apache/jmeter/gui/action/CheckDirty.java
index 69ae49be0..3cd98c15c 100644
--- a/src/core/org/apache/jmeter/gui/action/CheckDirty.java
+++ b/src/core/org/apache/jmeter/gui/action/CheckDirty.java
@@ -1,191 +1,193 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.HashTreeTraverser;
 import org.apache.jorphan.collections.ListedHashTree;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Check if the TestPlan has been changed since it was last saved
  *
  */
 public class CheckDirty extends AbstractAction implements HashTreeTraverser, ActionListener {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(CheckDirty.class);
 
     private final Map<JMeterTreeNode, TestElement> previousGuiItems;
 
     private boolean checkMode = false;
 
     private boolean removeMode = false;
 
     private boolean dirty = false;
 
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.CHECK_DIRTY);
         commands.add(ActionNames.SUB_TREE_SAVED);
         commands.add(ActionNames.SUB_TREE_MERGED);
         commands.add(ActionNames.SUB_TREE_LOADED);
         commands.add(ActionNames.ADD_ALL);
         commands.add(ActionNames.CHECK_REMOVE);
     }
 
     public CheckDirty() {
         previousGuiItems = new HashMap<>();
         ActionRouter.getInstance().addPreActionListener(ExitCommand.class, this);
     }
 
     @Override
     public void actionPerformed(ActionEvent e) {
         if (e.getActionCommand().equals(ActionNames.EXIT)) {
             doAction(e);
         }
     }
 
     /**
      * @see Command#doAction(ActionEvent)
      */
     @Override
     public void doAction(ActionEvent e) {
         String action = e.getActionCommand();
         if (action.equals(ActionNames.SUB_TREE_SAVED)) {
             HashTree subTree = (HashTree) e.getSource();
             subTree.traverse(this);
         } else if (action.equals(ActionNames.SUB_TREE_LOADED)) {
             ListedHashTree addTree = (ListedHashTree) e.getSource();
             addTree.traverse(this);
         } else if (action.equals(ActionNames.ADD_ALL)) {
             previousGuiItems.clear();
             GuiPackage.getInstance().getTreeModel().getTestPlan().traverse(this);
             if (isWorkbenchSaveable()) {
                 GuiPackage.getInstance().getTreeModel().getWorkBench().traverse(this);
             }
         } else if (action.equals(ActionNames.CHECK_REMOVE)) {
             GuiPackage guiPackage = GuiPackage.getInstance();
             JMeterTreeNode[] nodes = guiPackage.getTreeListener().getSelectedNodes();
             removeMode = true;
             try {
                 for (int i = nodes.length - 1; i >= 0; i--) {
                     guiPackage.getTreeModel().getCurrentSubTree(nodes[i]).traverse(this);
                 }
             } finally {
                 removeMode = false;
             }
         }
         
         // If we are merging in another test plan, we know the test plan is dirty now
         if(action.equals(ActionNames.SUB_TREE_MERGED)) {
             dirty = true;
         }
         else {
             dirty = false;
             checkMode = true;
             try {
                 HashTree wholeTree = GuiPackage.getInstance().getTreeModel().getTestPlan();
                 wholeTree.traverse(this);
                 
                 // check the workbench for modification
                 if(!dirty) { // NOSONAR
                     if (isWorkbenchSaveable()) {
                         HashTree workbench = GuiPackage.getInstance().getTreeModel().getWorkBench();
                         workbench.traverse(this);
                     }
                 }
             } finally {
                 checkMode = false;
             }
         }
         GuiPackage.getInstance().setDirty(dirty);
     }
 
     /**
      * check if the workbench should be saved
      */
     private boolean isWorkbenchSaveable() {
         JMeterTreeNode workbenchNode = (JMeterTreeNode) ((JMeterTreeNode) GuiPackage.getInstance().getTreeModel().getRoot()).getChildAt(1);
         return ((WorkBench) workbenchNode.getUserObject()).getSaveWorkBench();
     }
 
     /**
      * The tree traverses itself depth-first, calling addNode for each
      * object it encounters as it goes.
      */
     @Override
     public void addNode(Object node, HashTree subTree) {
-        log.debug("Node is class:" + node.getClass());
+        if (log.isDebugEnabled()) {
+            log.debug("Node is class: {}", node.getClass());
+        }
         JMeterTreeNode treeNode = (JMeterTreeNode) node;
         if (checkMode) {
             // Only check if we have not found any differences so far
             if(!dirty) {
                 if (previousGuiItems.containsKey(treeNode)) {
                     if (!previousGuiItems.get(treeNode).equals(treeNode.getTestElement())) {
                         dirty = true;
                     }
                 } else {
                     dirty = true;
                 }
             }
         } else if (removeMode) {
             previousGuiItems.remove(treeNode);
         } else {
             previousGuiItems.put(treeNode, (TestElement) treeNode.getTestElement().clone());
         }
     }
 
     /**
      * Indicates traversal has moved up a step, and the visitor should remove
      * the top node from it's stack structure.
      */
     @Override
     public void subtractNode() {
     }
 
     /**
      * Process path is called when a leaf is reached. If a visitor wishes to
      * generate Lists of path elements to each leaf, it should keep a Stack data
      * structure of nodes passed to it with addNode, and removing top items for
      * every subtractNode() call.
      */
     @Override
     public void processPath() {
     }
 
     /**
      * @see Command#getActionNames()
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Clear.java b/src/core/org/apache/jmeter/gui/action/Clear.java
index 34202a62b..babdae6c8 100644
--- a/src/core/org/apache/jmeter/gui/action/Clear.java
+++ b/src/core/org/apache/jmeter/gui/action/Clear.java
@@ -1,84 +1,84 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.JMeterGUIComponent;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.samplers.Clearable;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Handles the following actions:
  * - Clear (Data)
  * - Clear All (Data)
  * - Reset (Clear GUI)
  */
 public class Clear extends AbstractAction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Clear.class);
 
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.CLEAR);
         commands.add(ActionNames.CLEAR_ALL);
         commands.add(ActionNames.RESET_GUI);
     }
 
     public Clear() {
     }
 
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     @Override
     public void doAction(ActionEvent e) {
         GuiPackage guiPackage = GuiPackage.getInstance();
         final String actionCommand = e.getActionCommand();
         if (actionCommand.equals(ActionNames.CLEAR)) {
             JMeterGUIComponent guiComp = guiPackage.getCurrentGui();
             if (guiComp instanceof Clearable){
                 ((Clearable) guiComp).clearData();
             }
         } else if (actionCommand.equals(ActionNames.RESET_GUI)) {
             JMeterGUIComponent guiComp = guiPackage.getCurrentGui();
             guiComp.clearGui();
         } else {
             guiPackage.getMainFrame().clearData();
             for (JMeterTreeNode node : guiPackage.getTreeModel().getNodesOfType(Clearable.class)) {
                 JMeterGUIComponent guiComp = guiPackage.getGui(node.getTestElement());
                 if (guiComp instanceof Clearable){
                     Clearable item = (Clearable) guiComp;
                     try {
                         item.clearData();
                     } catch (Exception ex) {
-                        log.error("Can't clear: "+node+" "+guiComp, ex);
+                        log.error("Can't clear: {} {}", node, guiComp, ex);
                     }
                 }
             }
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Copy.java b/src/core/org/apache/jmeter/gui/action/Copy.java
index ff6620a45..654879855 100644
--- a/src/core/org/apache/jmeter/gui/action/Copy.java
+++ b/src/core/org/apache/jmeter/gui/action/Copy.java
@@ -1,147 +1,147 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 package org.apache.jmeter.gui.action;
 
 import java.awt.Toolkit;
 import java.awt.datatransfer.Clipboard;
 import java.awt.event.ActionEvent;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterTreeNodeTransferable;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Implements the Copy menu command
  */
 public class Copy extends AbstractAction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Copy.class);
 
     private static final HashSet<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.COPY);
     }
 
     /*
      * @see org.apache.jmeter.gui.action.Command#getActionNames()
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     @Override
     public void doAction(ActionEvent e) {
         JMeterTreeListener treeListener = GuiPackage.getInstance().getTreeListener();
         JMeterTreeNode[] nodes = treeListener.getSelectedNodes();
         nodes = keepOnlyAncestors(nodes);
         nodes = cloneTreeNodes(nodes);
         setCopiedNodes(nodes);
     }
 
     public static JMeterTreeNode[] getCopiedNodes() {
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         if (clipboard.isDataFlavorAvailable(JMeterTreeNodeTransferable.JMETER_TREE_NODE_ARRAY_DATA_FLAVOR)) {
             try {
                 return (JMeterTreeNode[]) clipboard.getData(JMeterTreeNodeTransferable.JMETER_TREE_NODE_ARRAY_DATA_FLAVOR);
             } catch (Exception ex) {
-                log.error("Clipboard node read error:" + ex.getMessage(), ex);
+                log.error("Clipboard node read error: {}", ex.getMessage(), ex);
                 JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(), 
                         JMeterUtils.getResString("clipboard_node_read_error")+":\n" + ex.getLocalizedMessage(),  //$NON-NLS-1$  //$NON-NLS-2$
                         JMeterUtils.getResString("error_title"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
             }
         }
         return null;
     }
 
     public static JMeterTreeNode cloneTreeNode(JMeterTreeNode node) {
         JMeterTreeNode treeNode = (JMeterTreeNode) node.clone();
         treeNode.setUserObject(((TestElement) node.getUserObject()).clone());
         cloneChildren(treeNode, node);
         return treeNode;
     }
     
     /**
      * If a child and one of its ancestors are selected : only keep the ancestor
      * @param currentNodes JMeterTreeNode[]
      * @return JMeterTreeNode[]
      */
     static JMeterTreeNode[] keepOnlyAncestors(JMeterTreeNode[] currentNodes) {
         List<JMeterTreeNode> nodes = new ArrayList<>();
         for (int i = 0; i < currentNodes.length; i++) {
             boolean exclude = false;
             for (int j = 0; j < currentNodes.length; j++) {
                 if(i!=j && currentNodes[i].isNodeAncestor(currentNodes[j])) {
                     exclude = true;
                     break;
                 }
             }
             
             if(!exclude) {
                 nodes.add(currentNodes[i]);
             }
         }
 
         return nodes.toArray(new JMeterTreeNode[nodes.size()]);
     }
 
     public static void setCopiedNodes(JMeterTreeNode[] nodes) {
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         try {
             JMeterTreeNodeTransferable transferable = new JMeterTreeNodeTransferable();
             transferable.setTransferData(nodes);
             clipboard.setContents(transferable, null);
         } catch (Exception ex) {
-            log.error("Clipboard node read error:" + ex.getMessage(), ex);
+            log.error("Clipboard node read error: {}", ex.getMessage(), ex);
             JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(), 
                     JMeterUtils.getResString("clipboard_node_read_error")+":\n" + ex.getLocalizedMessage(), //$NON-NLS-1$ //$NON-NLS-2$ 
                     JMeterUtils.getResString("error_title"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
         }
     }
 
     public static JMeterTreeNode[] cloneTreeNodes(JMeterTreeNode[] nodes) {
         JMeterTreeNode[] treeNodes = new JMeterTreeNode[nodes.length];
         for (int i = 0; i < nodes.length; i++) {
             treeNodes[i] = cloneTreeNode(nodes[i]);
         }
         return treeNodes;
     }
 
     private static void cloneChildren(JMeterTreeNode to, JMeterTreeNode from) {
         Enumeration<?> enumFrom = from.children();
         while (enumFrom.hasMoreElements()) {
             JMeterTreeNode child = (JMeterTreeNode) enumFrom.nextElement();
             JMeterTreeNode childClone = (JMeterTreeNode) child.clone();
             childClone.setUserObject(((TestElement) child.getUserObject()).clone());
             to.add(childClone);
             cloneChildren((JMeterTreeNode) to.getLastChild(), child);
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/EnableComponent.java b/src/core/org/apache/jmeter/gui/action/EnableComponent.java
index 957caf7ac..a413fc3fd 100644
--- a/src/core/org/apache/jmeter/gui/action/EnableComponent.java
+++ b/src/core/org/apache/jmeter/gui/action/EnableComponent.java
@@ -1,87 +1,87 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Implements the Enable menu item.
  */
 public class EnableComponent extends AbstractAction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(EnableComponent.class);
 
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.ENABLE);
         commands.add(ActionNames.DISABLE);
         commands.add(ActionNames.TOGGLE);
     }
 
     /**
      * @see org.apache.jmeter.gui.action.Command#doAction(ActionEvent)
      */
     @Override
     public void doAction(ActionEvent e) {
         JMeterTreeNode[] nodes = GuiPackage.getInstance().getTreeListener().getSelectedNodes();
 
         if (e.getActionCommand().equals(ActionNames.ENABLE)) {
             log.debug("enabling currently selected gui objects");
             enableComponents(nodes, true);
         } else if (e.getActionCommand().equals(ActionNames.DISABLE)) {
             log.debug("disabling currently selected gui objects");
             enableComponents(nodes, false);
         } else if (e.getActionCommand().equals(ActionNames.TOGGLE)) {
             log.debug("toggling currently selected gui objects");
             toggleComponents(nodes);
         }
     }
 
     private void enableComponents(JMeterTreeNode[] nodes, boolean enable) {
         GuiPackage pack = GuiPackage.getInstance();
         for (JMeterTreeNode node : nodes) {
             node.setEnabled(enable);
             pack.getGui(node.getTestElement()).setEnabled(enable);
         }
     }
 
     private void toggleComponents(JMeterTreeNode[] nodes) {
         GuiPackage pack = GuiPackage.getInstance();
         for (JMeterTreeNode node : nodes) {
             boolean enable = !node.isEnabled();
             node.setEnabled(enable);
             pack.getGui(node.getTestElement()).setEnabled(enable);
         }
     }
 
     /**
      * @see org.apache.jmeter.gui.action.Command#getActionNames()
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Help.java b/src/core/org/apache/jmeter/gui/action/Help.java
index 0a6997d33..2e732a15b 100644
--- a/src/core/org/apache/jmeter/gui/action/Help.java
+++ b/src/core/org/apache/jmeter/gui/action/Help.java
@@ -1,132 +1,132 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.Frame;
 import java.awt.GridLayout;
 import java.awt.event.ActionEvent;
 import java.io.IOException;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JDialog;
 import javax.swing.JScrollPane;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.util.EscapeDialog;
 import org.apache.jmeter.swing.HtmlPane;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ComponentUtil;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Implements the Help menu item.
  */
 public class Help extends AbstractAction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Help.class);
 
     private static final Set<String> commands = new HashSet<>();
 
     private static final String HELP_DOCS = "file:///"  // $NON-NLS-1$
         + JMeterUtils.getJMeterHome()
         + "/printable_docs/usermanual/"; // $NON-NLS-1$
 
     private static final String HELP_PAGE = HELP_DOCS + "component_reference.html"; // $NON-NLS-1$
 
     public static final String HELP_FUNCTIONS = HELP_DOCS + "functions.html"; // $NON-NLS-1$
 
     private static JDialog helpWindow;
 
     private static final HtmlPane helpDoc;
 
     private static final JScrollPane scroller;
 
     static {
         commands.add(ActionNames.HELP);
         helpDoc = new HtmlPane();
         scroller = new JScrollPane(helpDoc);
         helpDoc.setEditable(false);
     }
 
     /**
      * @see org.apache.jmeter.gui.action.Command#doAction(ActionEvent)
      */
     @Override
     public void doAction(ActionEvent e) {
         JDialog dialog = initHelpWindow();
         dialog.setVisible(true); // set the window visible immediately
 
         /*
          * This means that a new page will be shown before rendering is complete,
          * however the correct location will be displayed.
          * Attempts to use a "page" PropertyChangeListener to detect when the page
          * has been loaded failed to work any better. 
          */
         StringBuilder url=new StringBuilder();
         if (e.getSource() instanceof String[]) {
             String[] source = (String[]) e.getSource();
             url.append(source[0]).append('#').append(source[1]);
         } else {
             url.append(HELP_PAGE).append('#').append(GuiPackage.getInstance().getTreeListener().getCurrentNode().getDocAnchor());
         }
         try {
             helpDoc.setPage(url.toString()); // N.B. this only reloads if necessary (ignores the reference)
         } catch (IOException ioe) {
-            log.error("Error setting page for url"+url, ioe);
+            log.error("Error setting page for url, {}", url, ioe);
             helpDoc.setText("<html><head><title>Problem loading help page</title>"
                     + "<style><!--"
                     + ".note { background-color: #ffeeee; border: 1px solid brown; }"
                     + "div { padding: 10; margin: 10; }"
                     + "--></style></head>"
                     + "<body><div class='note'>"
                     + "<h1>Problem loading help page</h1>"
                     + "<div>Can't load url: &quot;<em>"
                     + url.toString() + "</em>&quot;</div>"
                     + "<div>See log for more info</div>"
                     + "</body>");
         }
     }
 
     /**
      * @return {@link JDialog} Help window and initializes it if necessary
      */
     private static JDialog initHelpWindow() {
         if (helpWindow == null) {
             helpWindow = new EscapeDialog(new Frame(),// independent frame to
                                                     // allow it to be overlaid
                                                     // by the main frame
                     JMeterUtils.getResString("help"),//$NON-NLS-1$
                     false);
             helpWindow.getContentPane().setLayout(new GridLayout(1, 1));
             helpWindow.getContentPane().removeAll();
             helpWindow.getContentPane().add(scroller);
             ComponentUtil.centerComponentInWindow(helpWindow, 60);
         }
         return helpWindow;
     }
 
     /**
      * @see org.apache.jmeter.gui.action.Command#getActionNames()
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Load.java b/src/core/org/apache/jmeter/gui/action/Load.java
index c9548ffb9..a789c9456 100644
--- a/src/core/org/apache/jmeter/gui/action/Load.java
+++ b/src/core/org/apache/jmeter/gui/action/Load.java
@@ -1,245 +1,249 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.io.File;
 import java.io.IOException;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JFileChooser;
 import javax.swing.JTree;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.FocusRequester;
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 import com.thoughtworks.xstream.converters.ConversionException;
 
 /**
  * Handles the Open (load a new file) and Merge commands.
  *
  */
 public class Load extends AbstractActionWithNoRunningTest {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Load.class);
 
     private static final boolean EXPAND_TREE = JMeterUtils.getPropDefault("onload.expandtree", false); //$NON-NLS-1$
 
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.OPEN);
         commands.add(ActionNames.MERGE);
     }
 
     public Load() {
         super();
     }
 
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     @Override
     public void doActionAfterCheck(final ActionEvent e) {
         final JFileChooser chooser = FileDialoger.promptToOpenFile(new String[] { ".jmx" }); //$NON-NLS-1$
         if (chooser == null) {
             return;
         }
         final File selectedFile = chooser.getSelectedFile();
         if (selectedFile != null) {
             final boolean merging = e.getActionCommand().equals(ActionNames.MERGE);
             // We must ask the user if it is ok to close current project
             if (!merging) { // i.e. it is OPEN
                 if (!Close.performAction(e)) {
                     return;
                 }
             }
             loadProjectFile(e, selectedFile, merging);
         }
     }
 
     /**
      * Loads or merges a file into the current GUI, reporting any errors to the user.
      * If the file is a complete test plan, sets the GUI test plan file name
      *
      * @param e the event that triggered the action
      * @param f the file to load
      * @param merging if true, then try to merge the file into the current GUI.
      */
     static void loadProjectFile(final ActionEvent e, final File f, final boolean merging) {
         loadProjectFile(e, f, merging, true);
     }
 
     /**
      * Loads or merges a file into the current GUI, reporting any errors to the user.
      * If the file is a complete test plan, sets the GUI test plan file name
      *
      * @param e the event that triggered the action
      * @param f the file to load
      * @param merging if true, then try to merge the file into the current GUI.
      * @param setDetails if true, then set the file details (if not merging)
      */
     static void loadProjectFile(final ActionEvent e, final File f, final boolean merging, final boolean setDetails) {
         ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.STOP_THREAD));
 
         final GuiPackage guiPackage = GuiPackage.getInstance();
         if (f != null) {
             try {
                 if (merging) {
-                    log.info("Merging file: " + f);
+                    log.info("Merging file: {}", f);
                 } else {
-                    log.info("Loading file: " + f);
+                    log.info("Loading file: {}", f);
                     // TODO should this be done even if not a full test plan?
                     // and what if load fails?
                     if (setDetails) {
                         FileServer.getFileServer().setBaseForScript(f);
                     }
                 }
                 final HashTree tree = SaveService.loadTree(f);
                 final boolean isTestPlan = insertLoadedTree(e.getID(), tree, merging);
 
                 // don't change name if merging
                 if (!merging && isTestPlan && setDetails) {
                     // TODO should setBaseForScript be called here rather than
                     // above?
                     guiPackage.setTestPlanFile(f.getAbsolutePath());
                 }
             } catch (NoClassDefFoundError ex) {// Allow for missing optional jars
-                reportError("Missing jar file", ex, true);
+                reportError("Missing jar file. {}", ex, true);
             } catch (ConversionException ex) {
-                log.warn("Could not convert file "+ex);
+                if (log.isWarnEnabled()) {
+                    log.warn("Could not convert file. {}", ex.toString());
+                }
                 JMeterUtils.reportErrorToUser(SaveService.CEtoString(ex));
             } catch (IOException ex) {
-                reportError("Error reading file: ", ex, false);
+                reportError("Error reading file. {}", ex, false);
             } catch (Exception ex) {
-                reportError("Unexpected error", ex, true);
+                reportError("Unexpected error. {}", ex, true);
             }
             FileDialoger.setLastJFCDirectory(f.getParentFile().getAbsolutePath());
             guiPackage.updateCurrentGui();
             guiPackage.getMainFrame().repaint();
         }
     }
 
     /**
      * Inserts (or merges) the tree into the GUI.
      * Does not check if the previous tree has been saved.
      * Clears the existing GUI test plan if we are inserting a complete plan.
      * @param id the id for the ActionEvent that is created
      * @param tree the tree to load
      * @param merging true if the tree is to be merged; false if it is to replace the existing tree
      * @return true if the loaded tree was a full test plan
      * @throws IllegalUserActionException if the tree cannot be merged at the selected position or the tree is empty
      */
     // Does not appear to be used externally; called by #loadProjectFile()
     public static boolean insertLoadedTree(final int id, final HashTree tree, final boolean merging) throws IllegalUserActionException {
         if (tree == null) {
             throw new IllegalUserActionException("Empty TestPlan or error reading test plan - see log file");
         }
         final boolean isTestPlan = tree.getArray()[0] instanceof TestPlan;
 
         // If we are loading a new test plan, initialize the tree with the testplan node we are loading
         final GuiPackage guiInstance = GuiPackage.getInstance();
         if(isTestPlan && !merging) {
             // Why does this not call guiInstance.clearTestPlan() ?
             // Is there a reason for not clearing everything?
             guiInstance.clearTestPlan((TestElement)tree.getArray()[0]);
         }
 
         if (merging){ // Check if target of merge is reasonable
             final TestElement te = (TestElement)tree.getArray()[0];
             if (!(te instanceof WorkBench || te instanceof TestPlan)){// These are handled specially by addToTree
                 final boolean ok = MenuFactory.canAddTo(guiInstance.getCurrentNode(), te);
                 if (!ok){
                     String name = te.getName();
                     String className = te.getClass().getName();
                     className = className.substring(className.lastIndexOf('.')+1);
                     throw new IllegalUserActionException("Can't merge "+name+" ("+className+") here");
                 }
             }
         }
         final HashTree newTree = guiInstance.addSubTree(tree);
         guiInstance.updateCurrentGui();
         guiInstance.getMainFrame().getTree().setSelectionPath(
                 new TreePath(((JMeterTreeNode) newTree.getArray()[0]).getPath()));
         final HashTree subTree = guiInstance.getCurrentSubTree();
         // Send different event wether we are merging a test plan into another test plan,
         // or loading a testplan from scratch
         ActionEvent actionEvent =
             new ActionEvent(subTree.get(subTree.getArray()[subTree.size() - 1]), id,
                     merging ? ActionNames.SUB_TREE_MERGED : ActionNames.SUB_TREE_LOADED);
 
         ActionRouter.getInstance().actionPerformed(actionEvent);
         final JTree jTree = guiInstance.getMainFrame().getTree();
         if (EXPAND_TREE && !merging) { // don't automatically expand when merging
             for(int i = 0; i < jTree.getRowCount(); i++) {
                 jTree.expandRow(i);
             }
         } else {
             jTree.expandRow(0);
         }
         jTree.setSelectionPath(jTree.getPathForRow(1));
         FocusRequester.requestFocus(jTree);
         return isTestPlan;
     }
 
     /**
      * Inserts the tree into the GUI.
      * Does not check if the previous tree has been saved.
      * Clears the existing GUI test plan if we are inserting a complete plan.
      * @param id the id for the ActionEvent that is created
      * @param tree the tree to load
      * @return true if the loaded tree was a full test plan
      * @throws IllegalUserActionException if the tree cannot be merged at the selected position or the tree is empty
      */
     // Called by JMeter#startGui()
     public static boolean insertLoadedTree(final int id, final HashTree tree) throws IllegalUserActionException {
         return insertLoadedTree(id, tree, false);
     }
 
     // Helper method to simplify code
-    private static void reportError(final String reason, final Throwable ex, final boolean stackTrace) {
-        if (stackTrace) {
-            log.warn(reason, ex);
-        } else {
-            log.warn(reason + ex);
+    private static void reportError(final String messageFormat, final Throwable ex, final boolean stackTrace) {
+        if (log.isWarnEnabled()) {
+            if (stackTrace) {
+                log.warn(messageFormat, ex.toString(), ex);
+            } else {
+                log.warn(messageFormat, ex.toString());
+            }
         }
         String msg = ex.getMessage();
         if (msg == null) {
             msg = "Unexpected error - see log for details";
         }
         JMeterUtils.reportErrorToUser(msg);
     }
 
 }
diff --git a/src/core/org/apache/jmeter/gui/action/LookAndFeelCommand.java b/src/core/org/apache/jmeter/gui/action/LookAndFeelCommand.java
index e6a0330e1..dca9a01cc 100644
--- a/src/core/org/apache/jmeter/gui/action/LookAndFeelCommand.java
+++ b/src/core/org/apache/jmeter/gui/action/LookAndFeelCommand.java
@@ -1,140 +1,140 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Locale;
 import java.util.Set;
 import java.util.prefs.Preferences;
 
 import javax.swing.UIManager;
 
 import org.apache.jmeter.gui.util.JMeterMenuBar;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Implements the Look and Feel menu item.
  */
 public class LookAndFeelCommand extends AbstractAction {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(LookAndFeelCommand.class);
 
     private static final String JMETER_LAF = "jmeter.laf"; // $NON-NLS-1$
 
     private static final Set<String> commands = new HashSet<>();
 
     private static final Preferences PREFS = Preferences.userNodeForPackage(LookAndFeelCommand.class);
     // Note: Windows user preferences are stored relative to: HKEY_CURRENT_USER\Software\JavaSoft\Prefs
 
     /** Prefix for the user preference key */
     private static final String USER_PREFS_KEY = "laf"; //$NON-NLS-1$
 
     static {
         UIManager.LookAndFeelInfo[] lfs = JMeterMenuBar.getAllLAFs();
         for (UIManager.LookAndFeelInfo lf : lfs) {
             commands.add(ActionNames.LAF_PREFIX + lf.getClassName());
         }
-        String jMeterLaf = getJMeterLaf();
         if (log.isInfoEnabled()) {
+            final String jMeterLaf = getJMeterLaf();
             List<String> names = new ArrayList<>();
             for(UIManager.LookAndFeelInfo laf : lfs) {
                 if (laf.getClassName().equals(jMeterLaf)) {
                     names.add(laf.getName());
                 }
             }
-            if (names.size() > 0) {
-                log.info("Using look and feel: "+jMeterLaf+ " " +names.toString());
+            if (!names.isEmpty()) {
+                log.info("Using look and feel: {} {}", jMeterLaf, names);
             } else {
-                log.info("Using look and feel: "+jMeterLaf);
+                log.info("Using look and feel: {}", jMeterLaf);
             }
         }
     }
 
     /**
      * Get LookAndFeel classname from the following properties:
      * <ul>
      * <li>User preferences key: "laf"</li>
      * <li>jmeter.laf.&lt;os.name&gt; - lowercased; spaces replaced by '_'</li>
      * <li>jmeter.laf.&lt;os.family&gt; - lowercased.</li>
      * <li>jmeter.laf</li>
      * <li>UIManager.getCrossPlatformLookAndFeelClassName()</li>
      * </ul>
      * @return LAF classname
      */
     public static String getJMeterLaf(){
         String laf = PREFS.get(USER_PREFS_KEY, null);
         if (laf != null) {
             return checkLafName(laf);            
         }
 
         String osName = System.getProperty("os.name") // $NON-NLS-1$
                         .toLowerCase(Locale.ENGLISH);
         // Spaces are not allowed in property names read from files
         laf = JMeterUtils.getProperty(JMETER_LAF+"."+osName.replace(' ', '_'));
         if (laf != null) {
             return checkLafName(laf);
         }
         String[] osFamily = osName.split("\\s"); // e.g. windows xp => windows
         laf = JMeterUtils.getProperty(JMETER_LAF+"."+osFamily[0]);
         if (laf != null) {
             return checkLafName(laf);
         }
         laf = JMeterUtils.getProperty(JMETER_LAF);
         if (laf != null) {
             return checkLafName(laf);
         }
         return UIManager.getCrossPlatformLookAndFeelClassName();
     }
 
     // Check if LAF is a built-in one
     private static String checkLafName(String laf){
         if (JMeterMenuBar.SYSTEM_LAF.equalsIgnoreCase(laf)){
             return UIManager.getSystemLookAndFeelClassName();
         }
         if (JMeterMenuBar.CROSS_PLATFORM_LAF.equalsIgnoreCase(laf)){
             return UIManager.getCrossPlatformLookAndFeelClassName();
         }
         return laf;
     }
 
     public LookAndFeelCommand() {
     }
 
     @Override
     public void doAction(ActionEvent ev) {
         try {
             String className = ev.getActionCommand().substring(ActionNames.LAF_PREFIX.length()).replace('/', '.');
             UIManager.setLookAndFeel(className);
             JMeterUtils.refreshUI();
             PREFS.put(USER_PREFS_KEY, className);
         } catch (javax.swing.UnsupportedLookAndFeelException | InstantiationException | ClassNotFoundException | IllegalAccessException e) {
             JMeterUtils.reportErrorToUser("Look and Feel unavailable:" + e.toString());
         }
     }
 
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Paste.java b/src/core/org/apache/jmeter/gui/action/Paste.java
index a5b23c543..b00550d01 100644
--- a/src/core/org/apache/jmeter/gui/action/Paste.java
+++ b/src/core/org/apache/jmeter/gui/action/Paste.java
@@ -1,95 +1,95 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.Toolkit;
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Places a copied JMeterTreeNode under the selected node.
  *
  */
 public class Paste extends AbstractAction {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Paste.class);
 
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.PASTE);
     }
 
     /**
      * @see Command#getActionNames()
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     /**
      * @see Command#doAction(ActionEvent)
      */
     @Override
     public void doAction(ActionEvent e) {
         JMeterTreeNode[] draggedNodes = Copy.getCopiedNodes();
         if (draggedNodes == null) {
             Toolkit.getDefaultToolkit().beep();
             return;
         }
         JMeterTreeListener treeListener = GuiPackage.getInstance().getTreeListener();
         JMeterTreeNode currentNode = treeListener.getCurrentNode();
         if (MenuFactory.canAddTo(currentNode, draggedNodes)) {
             for (JMeterTreeNode draggedNode : draggedNodes) {
                 if (draggedNode != null) {
                     addNode(currentNode, draggedNode);
                 }
             }
         } else {
             Toolkit.getDefaultToolkit().beep();
         }
         GuiPackage.getInstance().getMainFrame().repaint();
     }
 
     private void addNode(JMeterTreeNode parent, JMeterTreeNode node) {
         try {
             // Add this node
             JMeterTreeNode newNode = GuiPackage.getInstance().getTreeModel().addComponent(node.getTestElement(), parent);
             // Add all the child nodes of the node we are adding
             for(int i = 0; i < node.getChildCount(); i++) {
                 addNode(newNode, (JMeterTreeNode)node.getChildAt(i));
             }
         }
         catch (IllegalUserActionException iuae) {
-            log.error("", iuae); // $NON-NLS-1$
+            log.error("Illegal user action while adding a tree node.", iuae); // $NON-NLS-1$
             JMeterUtils.reportErrorToUser(iuae.getMessage());
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Save.java b/src/core/org/apache/jmeter/gui/action/Save.java
index 9f8a4f11f..42f2e110e 100644
--- a/src/core/org/apache/jmeter/gui/action/Save.java
+++ b/src/core/org/apache/jmeter/gui/action/Save.java
@@ -1,450 +1,454 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.text.DecimalFormat;
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Set;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import javax.swing.JFileChooser;
 import javax.swing.JOptionPane;
 
 import org.apache.commons.io.FileUtils;
 import org.apache.commons.io.FilenameUtils;
 import org.apache.commons.io.filefilter.FileFilterUtils;
 import org.apache.commons.io.filefilter.IOFileFilter;
 import org.apache.jmeter.control.gui.TestFragmentControllerGui;
 import org.apache.jmeter.engine.TreeCloner;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jmeter.threads.ThreadGroup;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.ListedHashTree;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Save the current test plan; implements:
  * Save
  * Save TestPlan As
  * Save (Selection) As
  */
 public class Save extends AbstractAction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Save.class);
 
     private static final List<File> EMPTY_FILE_LIST = Collections.emptyList();
     
     private static final String JMX_BACKUP_ON_SAVE = "jmeter.gui.action.save.backup_on_save"; // $NON-NLS-1$
 
     private static final String JMX_BACKUP_DIRECTORY = "jmeter.gui.action.save.backup_directory"; // $NON-NLS-1$
     
     private static final String JMX_BACKUP_MAX_HOURS = "jmeter.gui.action.save.keep_backup_max_hours"; // $NON-NLS-1$
     
     private static final String JMX_BACKUP_MAX_COUNT = "jmeter.gui.action.save.keep_backup_max_count"; // $NON-NLS-1$
     
     public static final String JMX_FILE_EXTENSION = ".jmx"; // $NON-NLS-1$
 
     private static final String DEFAULT_BACKUP_DIRECTORY = JMeterUtils.getJMeterHome() + "/backups"; //$NON-NLS-1$
     
     // Whether we should keep backups for save JMX files. Default is to enable backup
     private static final boolean BACKUP_ENABLED = JMeterUtils.getPropDefault(JMX_BACKUP_ON_SAVE, true);
     
     // Path to the backup directory
     private static final String BACKUP_DIRECTORY = JMeterUtils.getPropDefault(JMX_BACKUP_DIRECTORY, DEFAULT_BACKUP_DIRECTORY);
     
     // Backup files expiration in hours. Default is to never expire (zero value).
     private static final int BACKUP_MAX_HOURS = JMeterUtils.getPropDefault(JMX_BACKUP_MAX_HOURS, 0);
     
     // Max number of backup files. Default is to limit to 10 backups max.
     private static final int BACKUP_MAX_COUNT = JMeterUtils.getPropDefault(JMX_BACKUP_MAX_COUNT, 10);
 
     // NumberFormat to format version number in backup file names
     private static final DecimalFormat BACKUP_VERSION_FORMATER = new DecimalFormat("000000"); //$NON-NLS-1$
     
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.SAVE_AS); // Save (Selection) As
         commands.add(ActionNames.SAVE_AS_TEST_FRAGMENT); // Save as Test Fragment
         commands.add(ActionNames.SAVE_ALL_AS); // Save TestPlan As
         commands.add(ActionNames.SAVE); // Save
     }
 
     /**
      * Constructor for the Save object.
      */
     public Save() {}
 
     /**
      * Gets the ActionNames attribute of the Save object.
      *
      * @return the ActionNames value
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     @Override
     public void doAction(ActionEvent e) throws IllegalUserActionException {
         HashTree subTree;
         boolean fullSave = false; // are we saving the whole tree?
         if (!commands.contains(e.getActionCommand())) {
             throw new IllegalUserActionException("Invalid user command:" + e.getActionCommand());
         }
         if (e.getActionCommand().equals(ActionNames.SAVE_AS)) {
             JMeterTreeNode[] nodes = GuiPackage.getInstance().getTreeListener().getSelectedNodes();
             if (nodes.length > 1){
                 JMeterUtils.reportErrorToUser(
                         JMeterUtils.getResString("save_as_error"), // $NON-NLS-1$
                         JMeterUtils.getResString("save_as")); // $NON-NLS-1$
                 return;
             }
             subTree = GuiPackage.getInstance().getCurrentSubTree();
         } 
         else if (e.getActionCommand().equals(ActionNames.SAVE_AS_TEST_FRAGMENT)) {
             JMeterTreeNode[] nodes = GuiPackage.getInstance().getTreeListener().getSelectedNodes();
             if(checkAcceptableForTestFragment(nodes)) {                
                 subTree = GuiPackage.getInstance().getCurrentSubTree();
                 // Create Test Fragment node
                 TestElement element = GuiPackage.getInstance().createTestElement(TestFragmentControllerGui.class.getName());
                 HashTree hashTree = new ListedHashTree();
                 HashTree tfTree = hashTree.add(new JMeterTreeNode(element, null));
                 for (JMeterTreeNode node : nodes) {
                     // Clone deeply current node
                     TreeCloner cloner = new TreeCloner(false);
                     GuiPackage.getInstance().getTreeModel().getCurrentSubTree(node).traverse(cloner);
                     // Add clone to tfTree
                     tfTree.add(cloner.getClonedTree());
                 }
                                 
                 subTree = hashTree;
                 
             } else {
                 JMeterUtils.reportErrorToUser(
                         JMeterUtils.getResString("save_as_test_fragment_error"), // $NON-NLS-1$
                         JMeterUtils.getResString("save_as_test_fragment")); // $NON-NLS-1$
                 return;
             }
         } else {
             fullSave = true;
             HashTree testPlan = GuiPackage.getInstance().getTreeModel().getTestPlan();
             // If saveWorkBench 
             if (isWorkbenchSaveable()) {
                 HashTree workbench = GuiPackage.getInstance().getTreeModel().getWorkBench();
                 testPlan.add(workbench);
             }
             subTree = testPlan;
         }
 
         String updateFile = GuiPackage.getInstance().getTestPlanFile();
         if (!ActionNames.SAVE.equals(e.getActionCommand()) || updateFile == null) {
             JFileChooser chooser = FileDialoger.promptToSaveFile(updateFile == null ? GuiPackage.getInstance().getTreeListener()
                     .getCurrentNode().getName()
                     + JMX_FILE_EXTENSION : updateFile);
             if (chooser == null) {
                 return;
             }
             updateFile = chooser.getSelectedFile().getAbsolutePath();
             // Make sure the file ends with proper extension
             if(FilenameUtils.getExtension(updateFile).isEmpty()) {
                 updateFile = updateFile + JMX_FILE_EXTENSION;
             }
             // Check if the user is trying to save to an existing file
             File f = new File(updateFile);
             if(f.exists()) {
                 int response = JOptionPane.showConfirmDialog(GuiPackage.getInstance().getMainFrame(),
                         JMeterUtils.getResString("save_overwrite_existing_file"), // $NON-NLS-1$
                         JMeterUtils.getResString("save?"),  // $NON-NLS-1$
                         JOptionPane.YES_NO_OPTION,
                         JOptionPane.QUESTION_MESSAGE);
                 if (response == JOptionPane.CLOSED_OPTION || response == JOptionPane.NO_OPTION) {
                     return ; // Do not save, user does not want to overwrite
                 }
             }
 
             if (!e.getActionCommand().equals(ActionNames.SAVE_AS)) {
                 GuiPackage.getInstance().setTestPlanFile(updateFile);
             }
         }
         
         // backup existing file according to jmeter/user.properties settings
         List<File> expiredBackupFiles = EMPTY_FILE_LIST;
         File fileToBackup = new File(updateFile);
         try {
             expiredBackupFiles = createBackupFile(fileToBackup);
         } catch (Exception ex) {
-            log.error("Failed to create a backup for " + fileToBackup.getName(), ex); //$NON-NLS-1$
+            log.error("Failed to create a backup for {}", fileToBackup, ex); //$NON-NLS-1$
         }
         
         try {
             convertSubTree(subTree);
         } catch (Exception err) {
-            log.warn("Error converting subtree "+err);
+            if (log.isWarnEnabled()) {
+                log.warn("Error converting subtree. {}", err.toString());
+            }
         }
 
         try (FileOutputStream ostream = new FileOutputStream(updateFile)){
             SaveService.saveTree(subTree, ostream);
             if (fullSave) { // Only update the stored copy of the tree for a full save
                 subTree = GuiPackage.getInstance().getTreeModel().getTestPlan(); // refetch, because convertSubTree affects it
                 if (isWorkbenchSaveable()) {
                     HashTree workbench = GuiPackage.getInstance().getTreeModel().getWorkBench();
                     subTree.add(workbench);
                 }
                 ActionRouter.getInstance().doActionNow(new ActionEvent(subTree, e.getID(), ActionNames.SUB_TREE_SAVED));
             }
             
             // delete expired backups : here everything went right so we can
             // proceed to deletion
             for (File expiredBackupFile : expiredBackupFiles) {
                 try {
                     FileUtils.deleteQuietly(expiredBackupFile);
                 } catch (Exception ex) {
-                    log.warn("Failed to delete backup file " + expiredBackupFile.getName()); //$NON-NLS-1$
+                    log.warn("Failed to delete backup file, {}", expiredBackupFile); //$NON-NLS-1$
                 }
             }
         } catch(RuntimeException ex) {
             throw ex;
         }
         catch (Exception ex) {
-            log.error("Error saving tree:", ex);
+            log.error("Error saving tree.", ex);
             throw new IllegalUserActionException("Couldn't save test plan to file: " + updateFile, ex);
         } 
 
         GuiPackage.getInstance().updateCurrentGui();
     }
     
     /**
      * <p>
      * Create a backup copy of the specified file whose name will be
      * <code>{baseName}-{version}.jmx</code><br>
      * Where :<br>
      * <code>{baseName}</code> is the name of the file to backup without its
      * <code>.jmx</code> extension. For a file named <code>testplan.jmx</code>
      * it would then be <code>testplan</code><br>
      * <code>{version}</code> is the version number automatically incremented
      * after the higher version number of pre-existing backup files. <br>
      * <br>
      * Example: <code>testplan-000028.jmx</code> <br>
      * <br>
      * If <code>jmeter.gui.action.save.backup_directory</code> is <b>not</b>
      * set, then backup files will be created in
      * <code>${JMETER_HOME}/backups</code>
      * </p>
      * <p>
      * Backup process is controlled by the following jmeter/user properties :<br>
      * <table border=1>
      * <tr>
      * <th align=left>Property</th>
      * <th align=left>Type/Value</th>
      * <th align=left>Description</th>
      * </tr>
      * <tr>
      * <td><code>jmeter.gui.action.save.backup_on_save</code></td>
      * <td><code>true|false</code></td>
      * <td>Enables / Disables backup</td>
      * </tr>
      * <tr>
      * <td><code>jmeter.gui.action.save.backup_directory</code></td>
      * <td><code>/path/to/backup/directory</code></td>
      * <td>Set the directory path where backups will be stored upon save. If not
      * set then backups will be created in <code>${JMETER_HOME}/backups</code><br>
      * If that directory does not exist, it will be created</td>
      * </tr>
      * <tr>
      * <td><code>jmeter.gui.action.save.keep_backup_max_hours</code></td>
      * <td><code>integer</code></td>
      * <td>Maximum number of hours to preserve backup files. Backup files whose
      * age exceeds that limit should be deleted and will be added to this method
      * returned list</td>
      * </tr>
      * <tr>
      * <td><code>jmeter.gui.action.save.keep_backup_max_count</code></td>
      * <td><code>integer</code></td>
      * <td>Max number of backup files to be preserved. Exceeding backup files
      * should be deleted and will be added to this method returned list. Only
      * the most recent files will be preserved.</td>
      * </tr>
      * </table>
      * </p>
      * 
      * @param fileToBackup
      *            The file to create a backup from
      * @return A list of expired backup files selected according to the above
      *         properties and that should be deleted after the save operation
      *         has performed successfully
      */
     private List<File> createBackupFile(File fileToBackup) {
         if (!BACKUP_ENABLED || !fileToBackup.exists()) {
             return EMPTY_FILE_LIST;
         }
         char versionSeparator = '-'; //$NON-NLS-1$
         String baseName = fileToBackup.getName();
         // remove .jmx extension if any
         baseName = baseName.endsWith(JMX_FILE_EXTENSION) ? baseName.substring(0, baseName.length() - JMX_FILE_EXTENSION.length()) : baseName;
         // get a file to the backup directory
         File backupDir = new File(BACKUP_DIRECTORY);
         backupDir.mkdirs();
         if (!backupDir.isDirectory()) {
-            log.error("Could not backup file ! Backup directory does not exist, is not a directory or could not be created ! <" + backupDir.getAbsolutePath() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
+            log.error(
+                    "Could not backup file ! Backup directory does not exist, is not a directory or could not be created ! <{}>", //$NON-NLS-1$
+                    backupDir.getAbsolutePath()); //$NON-NLS-2$
         }
 
         /**
          *  select files matching
          * {baseName}{versionSeparator}{version}{jmxExtension}
          * where {version} is a 6 digits number
          */
         String backupPatternRegex = Pattern.quote(baseName + versionSeparator) + "([\\d]{6})" + Pattern.quote(JMX_FILE_EXTENSION); //$NON-NLS-1$
         Pattern backupPattern = Pattern.compile(backupPatternRegex);
         // create a file filter that select files matching a given regex pattern
         IOFileFilter patternFileFilter = new PrivatePatternFileFilter(backupPattern);
         // get all backup files in the backup directory
         List<File> backupFiles = new ArrayList<>(FileUtils.listFiles(backupDir, patternFileFilter, null));
         // find the highest version number among existing backup files (this
         // should be the more recent backup)
         int lastVersionNumber = 0;
         for (File backupFile : backupFiles) {
             Matcher matcher = backupPattern.matcher(backupFile.getName());
             if (matcher.find() && matcher.groupCount() > 0) {
                 // parse version number from the backup file name
                 // should never fail as it matches the regex
                 int version = Integer.parseInt(matcher.group(1));
                 lastVersionNumber = Math.max(lastVersionNumber, version);
             }
         }
         // find expired backup files
         List<File> expiredFiles = new ArrayList<>();
         if (BACKUP_MAX_HOURS > 0) {
             Calendar cal = Calendar.getInstance();
             cal.add(Calendar.HOUR_OF_DAY, -BACKUP_MAX_HOURS);
             long expiryDate = cal.getTime().getTime();
             // select expired files that should be deleted
             IOFileFilter expiredFileFilter = FileFilterUtils.ageFileFilter(expiryDate, true);
             expiredFiles.addAll(FileFilterUtils.filterList(expiredFileFilter, backupFiles));
         }
         // sort backups from by their last modified time
         Collections.sort(backupFiles, new Comparator<File>() {
             @Override
             public int compare(File o1, File o2) {
                 long diff = o1.lastModified() - o2.lastModified();
                 // convert the long to an int in order to comply with the method
                 // contract
                 return diff < 0 ? -1 : diff > 0 ? 1 : 0;
             }
         });
         /**
          *  backup name is of the form 
          * {baseName}{versionSeparator}{version}{jmxExtension}
          */
         String backupName = baseName + versionSeparator + BACKUP_VERSION_FORMATER.format(lastVersionNumber + 1L) + JMX_FILE_EXTENSION;
         File backupFile = new File(backupDir, backupName);
         // create file backup
         try {
             FileUtils.copyFile(fileToBackup, backupFile);
         } catch (IOException e) {
-            log.error("Failed to backup file :" + fileToBackup.getAbsolutePath(), e); //$NON-NLS-1$
+            log.error("Failed to backup file : {}", fileToBackup.getAbsolutePath(), e); //$NON-NLS-1$
             return EMPTY_FILE_LIST;
         }
         // add the fresh new backup file (list is still sorted here)
         backupFiles.add(backupFile);
         // unless max backups is not set, ensure that we don't keep more backups
         // than required
         if (BACKUP_MAX_COUNT > 0 && backupFiles.size() > BACKUP_MAX_COUNT) {
             // keep the most recent files in the limit of the specified max
             // count
             expiredFiles.addAll(backupFiles.subList(0, backupFiles.size() - BACKUP_MAX_COUNT));
         }
         return expiredFiles;
     }
     
     /**
      * check if the workbench should be saved
      */
     private boolean isWorkbenchSaveable() {
         JMeterTreeNode workbenchNode = (JMeterTreeNode) ((JMeterTreeNode) GuiPackage.getInstance().getTreeModel().getRoot()).getChildAt(1);
         return ((WorkBench) workbenchNode.getUserObject()).getSaveWorkBench();
     }
 
     /**
      * Check nodes does not contain a node of type TestPlan or ThreadGroup
      * @param nodes
      */
     private static boolean checkAcceptableForTestFragment(JMeterTreeNode[] nodes) {
         for (JMeterTreeNode node : nodes) {
             Object userObject = node.getUserObject();
             if (userObject instanceof ThreadGroup ||
                     userObject instanceof TestPlan) {
                 return false;
             }
         }
         return true;
     }
 
     // package protected to allow access from test code
     void convertSubTree(HashTree tree) {
         for (Object o : new LinkedList<>(tree.list())) {
             JMeterTreeNode item = (JMeterTreeNode) o;
             convertSubTree(tree.getTree(item));
             TestElement testElement = item.getTestElement(); // requires JMeterTreeNode
             tree.replaceKey(item, testElement);
         }
     }
     
     private static class PrivatePatternFileFilter implements IOFileFilter {
         
         private Pattern pattern;
         
         public PrivatePatternFileFilter(Pattern pattern) {
             if(pattern == null) {
                 throw new IllegalArgumentException("pattern cannot be null !"); //$NON-NLS-1$
             }
             this.pattern = pattern;
         }
         
         @Override
         public boolean accept(File dir, String fileName) {
             return pattern.matcher(fileName).matches();
         }
         
         @Override
         public boolean accept(File file) {
             return accept(file.getParentFile(), file.getName());
         }
     }
     
 }
diff --git a/src/core/org/apache/jmeter/gui/action/SelectTemplatesDialog.java b/src/core/org/apache/jmeter/gui/action/SelectTemplatesDialog.java
index 77693765e..6e2aa5fc8 100644
--- a/src/core/org/apache/jmeter/gui/action/SelectTemplatesDialog.java
+++ b/src/core/org/apache/jmeter/gui/action/SelectTemplatesDialog.java
@@ -1,246 +1,246 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 import java.awt.FlowLayout;
 import java.awt.Font;
 import java.awt.HeadlessException;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.File;
 
 import javax.swing.AbstractAction;
 import javax.swing.Action;
 import javax.swing.ActionMap;
 import javax.swing.InputMap;
 import javax.swing.JButton;
 import javax.swing.JComponent;
 import javax.swing.JDialog;
 import javax.swing.JFrame;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JRootPane;
 import javax.swing.JScrollPane;
 import javax.swing.UIManager;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 import javax.swing.event.HyperlinkEvent;
 import javax.swing.event.HyperlinkListener;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.action.template.Template;
 import org.apache.jmeter.gui.action.template.TemplateManager;
 import org.apache.jmeter.swing.HtmlPane;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.gui.JLabeledChoice;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Dialog used for Templates selection
  * @since 2.10
  */
 public class SelectTemplatesDialog extends JDialog implements ChangeListener, ActionListener, HyperlinkListener {
 
-    private static final long serialVersionUID = -4436834972710248247L;
+    private static final long serialVersionUID = 1;
     
     // Minimal dimensions for dialog box
     private static final int MINIMAL_BOX_WIDTH = 500;
     private static final int MINIMAL_BOX_HEIGHT = 300;
     
     private static final Font FONT_DEFAULT = UIManager.getDefaults().getFont("TextField.font"); //$NON-NLS-1$
 
     private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, (int) Math.round(FONT_DEFAULT.getSize() * 0.8)); //$NON-NLS-1$
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SelectTemplatesDialog.class);
 
     private final JLabeledChoice templateList = new JLabeledChoice(JMeterUtils.getResString("template_choose"), false); //$NON-NLS-1$
 
     private final HtmlPane helpDoc = new HtmlPane();
 
     private final JButton reloadTemplateButton = new JButton(JMeterUtils.getResString("template_reload")); //$NON-NLS-1$
 
     private final JButton applyTemplateButton = new JButton();
 
     private final JButton cancelButton = new JButton(JMeterUtils.getResString("cancel")); //$NON-NLS-1$
     
     private final JScrollPane scroller = new JScrollPane(helpDoc);
 
     public SelectTemplatesDialog() {
         super((JFrame) null, JMeterUtils.getResString("template_title"), true); //$NON-NLS-1$
         init();
     }
 
     @Override
     protected JRootPane createRootPane() {
         JRootPane rootPane = new JRootPane();
         // Hide Window on ESC
         Action escapeAction = new AbstractAction("ESCAPE") { //$NON-NLS-1$
             /**
              *
              */
             private static final long serialVersionUID = -6543764044868772971L;
 
             @Override
             public void actionPerformed(ActionEvent actionEvent) {
                 setVisible(false);
             }
         };
         // Do search on Enter
         Action enterAction = new AbstractAction("ENTER") { //$NON-NLS-1$
 
             private static final long serialVersionUID = -3661361497864527363L;
 
             @Override
             public void actionPerformed(final ActionEvent actionEvent) {
                 checkDirtyAndLoad(actionEvent);
             }
         };
         ActionMap actionMap = rootPane.getActionMap();
         actionMap.put(escapeAction.getValue(Action.NAME), escapeAction);
         actionMap.put(enterAction.getValue(Action.NAME), enterAction);
         InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
         inputMap.put(KeyStrokes.ESC, escapeAction.getValue(Action.NAME));
         inputMap.put(KeyStrokes.ENTER, enterAction.getValue(Action.NAME));
 
         return rootPane;
     }
     
     /**
      * Check if existing Test Plan has been modified and ask user 
      * what he wants to do if test plan is dirty
      * @param actionEvent {@link ActionEvent}
      */
     private void checkDirtyAndLoad(final ActionEvent actionEvent)
             throws HeadlessException {
         final String selectedTemplate = templateList.getText();
         final Template template = TemplateManager.getInstance().getTemplateByName(selectedTemplate);
         if (template == null) {
             return;
         }
         final boolean isTestPlan = template.isTestPlan();
         // Check if the user wants to drop any changes
         if (isTestPlan) {
             ActionRouter.getInstance().doActionNow(new ActionEvent(actionEvent.getSource(), actionEvent.getID(), ActionNames.CHECK_DIRTY));
             GuiPackage guiPackage = GuiPackage.getInstance();
             if (guiPackage.isDirty()) {
                 // Check if the user wants to create from template
                 int response = JOptionPane.showConfirmDialog(GuiPackage.getInstance().getMainFrame(),
                         JMeterUtils.getResString("cancel_new_from_template"), // $NON-NLS-1$
                         JMeterUtils.getResString("template_load?"),  // $NON-NLS-1$
                         JOptionPane.YES_NO_CANCEL_OPTION,
                         JOptionPane.QUESTION_MESSAGE);
                 if(response == JOptionPane.YES_OPTION) {
                     ActionRouter.getInstance().doActionNow(new ActionEvent(actionEvent.getSource(), actionEvent.getID(), ActionNames.SAVE));
                 }
                 if (response == JOptionPane.CLOSED_OPTION || response == JOptionPane.CANCEL_OPTION) {
                     return; // Don't clear the plan
                 }
             }
         }
         ActionRouter.getInstance().doActionNow(new ActionEvent(actionEvent.getSource(), actionEvent.getID(), ActionNames.STOP_THREAD));
         final File parent = template.getParent();
         final File fileToCopy = parent != null 
               ? new File(parent, template.getFileName())
               : new File(JMeterUtils.getJMeterHome(), template.getFileName());       
         Load.loadProjectFile(actionEvent, fileToCopy, !isTestPlan, false);
         this.setVisible(false);
     }
 
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         templateList.setValues(TemplateManager.getInstance().getTemplateNames());            
         templateList.addChangeListener(this);
         reloadTemplateButton.addActionListener(this);
         reloadTemplateButton.setFont(FONT_SMALL);
         this.getContentPane().setLayout(new BorderLayout(10, 0));
         
         JPanel templateBar = new JPanel(new BorderLayout());
         templateBar.add(templateList, BorderLayout.CENTER);
         JPanel reloadBtnBar = new JPanel();
         reloadBtnBar.add(reloadTemplateButton);
         templateBar.add(reloadBtnBar, BorderLayout.EAST);
         this.getContentPane().add(templateBar, BorderLayout.NORTH);
         helpDoc.setContentType("text/html"); //$NON-NLS-1$
         helpDoc.setEditable(false);
         helpDoc.addHyperlinkListener(this);
         this.getContentPane().add(scroller, BorderLayout.CENTER);
 
         applyTemplateButton.addActionListener(this);
         cancelButton.addActionListener(this);
 
         // Bottom buttons bar
         JPanel actionBtnBar = new JPanel(new FlowLayout());
         actionBtnBar.add(applyTemplateButton);
         actionBtnBar.add(cancelButton);
         this.getContentPane().add(actionBtnBar, BorderLayout.SOUTH);
 
         this.pack();
         this.setMinimumSize(new Dimension(MINIMAL_BOX_WIDTH, MINIMAL_BOX_HEIGHT));
         ComponentUtil.centerComponentInWindow(this, 50); // center position and 50% of screen size
         populateTemplatePage();
     }
 
     /**
      * Do search
      * @param e {@link ActionEvent}
      */
     @Override
     public void actionPerformed(ActionEvent e) {
         final Object source = e.getSource();
         if (source == cancelButton) {
             this.setVisible(false);
             return;
         } else if (source == applyTemplateButton) {
             checkDirtyAndLoad(e);            
         } else if (source == reloadTemplateButton) {
             templateList.setValues(TemplateManager.getInstance().reset().getTemplateNames());
         }
     }
     
     @Override
     public void stateChanged(ChangeEvent event) {
         populateTemplatePage();
     }
 
     private void populateTemplatePage() {
         String selectedTemplate = templateList.getText();
         Template template = TemplateManager.getInstance().getTemplateByName(selectedTemplate);
         helpDoc.setText(template.getDescription());
         applyTemplateButton.setText(template.isTestPlan() 
                 ? JMeterUtils.getResString("template_create_from")
                 : JMeterUtils.getResString("template_merge_from") );
     }
 
     @Override
     public void hyperlinkUpdate(HyperlinkEvent e) {
         if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
             if (java.awt.Desktop.isDesktopSupported()) {
                 try {
                     java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
                 } catch (Exception ex) {
-                    log.error("Error opening URL in browser:"+e.getURL());
+                    log.error("Error opening URL in browser: {}", e.getURL());
                 } 
             }
         }
     }
 
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Start.java b/src/core/org/apache/jmeter/gui/action/Start.java
index 5cf17c179..d5c8820fd 100644
--- a/src/core/org/apache/jmeter/gui/action/Start.java
+++ b/src/core/org/apache/jmeter/gui/action/Start.java
@@ -1,297 +1,300 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.JMeter;
 import org.apache.jmeter.engine.JMeterEngineException;
 import org.apache.jmeter.engine.StandardJMeterEngine;
 import org.apache.jmeter.engine.TreeCloner;
 import org.apache.jmeter.engine.TreeClonerNoTimer;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.action.validation.TreeClonerForValidation;
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.threads.AbstractThreadGroup;
 import org.apache.jmeter.timers.Timer;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.ListedHashTree;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Set of Actions to:
  * <ul>
  *      <li>Start a Test Plan</li>
  *      <li>Start a Test Plan without sleeping on the timers</li>
  *      <li>Stop a Test Plan</li>
  *      <li>Shutdown a Test plan</li>
  *      <li>Run a set of Thread Groups</li>
  *      <li>Run a set of Thread Groups without sleeping on the timers</li>
  *      <li>Validate a set of Thread Groups with/without sleeping on the timers depending on jmeter properties</li>
  * </ul>
  */
 public class Start extends AbstractAction {
     
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Start.class);
 
     private static final Set<String> commands = new HashSet<>();
 
     private static final String VALIDATION_CLONER_CLASS_PROPERTY_NAME = 
             "testplan_validation.tree_cloner_class"; //$NON-NLS-1$
     /**
      * Implementation of {@link TreeCloner} used to clone the tree before running validation
      */
     private static final String CLONER_FOR_VALIDATION_CLASS_NAME = 
             JMeterUtils.getPropDefault(VALIDATION_CLONER_CLASS_PROPERTY_NAME, //$NON-NLS-1$ 
                     "org.apache.jmeter.validation.ComponentTreeClonerForValidation");
 
     static {
         commands.add(ActionNames.ACTION_START);
         commands.add(ActionNames.ACTION_START_NO_TIMERS);
         commands.add(ActionNames.ACTION_STOP);
         commands.add(ActionNames.ACTION_SHUTDOWN);
         commands.add(ActionNames.RUN_TG);
         commands.add(ActionNames.RUN_TG_NO_TIMERS);
         commands.add(ActionNames.VALIDATE_TG);
     }
 
     private StandardJMeterEngine engine;
 
     /**
      * Constructor for the Start object.
      */
     public Start() {
     }
 
     /**
      * Gets the ActionNames attribute of the Start object.
      *
      * @return the ActionNames value
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     @Override
     public void doAction(ActionEvent e) {
         if (e.getActionCommand().equals(ActionNames.ACTION_START)) {
             popupShouldSave(e);
             startEngine(false);
         } else if (e.getActionCommand().equals(ActionNames.ACTION_START_NO_TIMERS)) {
             popupShouldSave(e);
             startEngine(true);
         } else if (e.getActionCommand().equals(ActionNames.ACTION_STOP)) {
             if (engine != null) {
-                LOG.info("Stopping test");
+                log.info("Stopping test");
                 GuiPackage.getInstance().getMainFrame().showStoppingMessage("");
                 engine.stopTest();
             }
         } else if (e.getActionCommand().equals(ActionNames.ACTION_SHUTDOWN)) {
             if (engine != null) {
-                LOG.info("Shutting test down");
+                log.info("Shutting test down");
                 GuiPackage.getInstance().getMainFrame().showStoppingMessage("");
                 engine.askThreadsToStop();
             }
         } else if (e.getActionCommand().equals(ActionNames.RUN_TG) 
                 || e.getActionCommand().equals(ActionNames.RUN_TG_NO_TIMERS)
                 || e.getActionCommand().equals(ActionNames.VALIDATE_TG)) {
             popupShouldSave(e);
             boolean noTimers = e.getActionCommand().equals(ActionNames.RUN_TG_NO_TIMERS);
             boolean isValidation = e.getActionCommand().equals(ActionNames.VALIDATE_TG);
             
             JMeterTreeListener treeListener = GuiPackage.getInstance().getTreeListener();
             JMeterTreeNode[] nodes = treeListener.getSelectedNodes();
             nodes = Copy.keepOnlyAncestors(nodes);
             AbstractThreadGroup[] tg = keepOnlyThreadGroups(nodes);
             if(nodes.length > 0) {
                 startEngine(noTimers, isValidation, tg);
             }
             else {
-                LOG.warn("No thread group selected the test will not be started");
+                log.warn("No thread group selected the test will not be started");
             }
         } 
     }
 
     /**
      * filter the nodes to keep only the thread group
      * @param currentNodes jmeter tree nodes
      * @return the thread groups
      */
     private AbstractThreadGroup[] keepOnlyThreadGroups(JMeterTreeNode[] currentNodes) {
         List<AbstractThreadGroup> nodes = new ArrayList<>();
         for (JMeterTreeNode jMeterTreeNode : currentNodes) {
             if(jMeterTreeNode.getTestElement() instanceof AbstractThreadGroup) {
                 nodes.add((AbstractThreadGroup) jMeterTreeNode.getTestElement());
             }
         }
         return nodes.toArray(new AbstractThreadGroup[nodes.size()]);
     }
 
     /**
      * Start JMeter engine
      * @param ignoreTimer flag to ignore timers
      */
     private void startEngine(boolean ignoreTimer) {
         startEngine(ignoreTimer, null);
     }
     
     /**
      * Start JMeter engine
      * @param ignoreTimer flag to ignore timers
      * @param threadGroupsToRun Array of AbstractThreadGroup to run
      */
     private void startEngine(boolean ignoreTimer, 
             AbstractThreadGroup[] threadGroupsToRun) {
         startEngine(ignoreTimer, false, threadGroupsToRun);
     }
     
     /**
      * Start JMeter engine
      * @param ignoreTimer flag to ignore timers
      * @param isValidationShot 
      * @param threadGroupsToRun Array of AbstractThreadGroup to run
      */
     private void startEngine(boolean ignoreTimer, 
             boolean isValidationShot,
             AbstractThreadGroup[] threadGroupsToRun) {
         GuiPackage gui = GuiPackage.getInstance();
         HashTree testTree = gui.getTreeModel().getTestPlan();
         
         JMeter.convertSubTree(testTree);
         if(threadGroupsToRun != null && threadGroupsToRun.length>0) {
             removeThreadGroupsFromHashTree(testTree, threadGroupsToRun);
         }
         testTree.add(testTree.getArray()[0], gui.getMainFrame());
-        LOG.debug("test plan before cloning is running version: "
-                + ((TestPlan) testTree.getArray()[0]).isRunningVersion());
+        if (log.isDebugEnabled()) {
+            log.debug("test plan before cloning is running version: {}",
+                    ((TestPlan) testTree.getArray()[0]).isRunningVersion());
+        }
 
         ListedHashTree clonedTree = null;
         if(isValidationShot) {
             TreeCloner cloner = createTreeClonerForValidation();
             testTree.traverse(cloner);
             clonedTree = cloner.getClonedTree();
         } else {
             TreeCloner cloner = cloneTree(testTree, ignoreTimer);      
             clonedTree = cloner.getClonedTree();
         }
         engine = new StandardJMeterEngine();
         engine.configure(clonedTree);
         try {
             engine.runTest();
         } catch (JMeterEngineException e) {
             JOptionPane.showMessageDialog(gui.getMainFrame(), e.getMessage(), 
                     JMeterUtils.getResString("error_occurred"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
         }
-        LOG.debug("test plan after cloning and running test is running version: "
-                + ((TestPlan) testTree.getArray()[0]).isRunningVersion());
+        if (log.isDebugEnabled()) {
+            log.debug("test plan after cloning and running test is running version: {}",
+                    ((TestPlan) testTree.getArray()[0]).isRunningVersion());
+        }
     }
 
     /**
      * 
      * @return {@link TreeCloner}
      */
     private static TreeCloner createTreeClonerForValidation() {
         Class<?> clazz;
         try {
             clazz = Class.forName(CLONER_FOR_VALIDATION_CLASS_NAME, true, Thread.currentThread().getContextClassLoader());
             return (TreeCloner) clazz.newInstance();
         } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
-            LOG.error("Error instanciating class:'"
-                    +CLONER_FOR_VALIDATION_CLASS_NAME+"' defined in property :'"
-                    +VALIDATION_CLONER_CLASS_PROPERTY_NAME+"'", ex);
+            log.error("Error instanciating class:'{}' defined in property:'{}'", CLONER_FOR_VALIDATION_CLASS_NAME,
+                    VALIDATION_CLONER_CLASS_PROPERTY_NAME, ex);
             return new TreeClonerForValidation();
         }
     }
 
     /**
      * Remove thread groups from testTree that are not in threadGroupsToKeep
      * @param testTree {@link HashTree}
      * @param threadGroupsToKeep Array of {@link AbstractThreadGroup} to keep
      */
     private void removeThreadGroupsFromHashTree(HashTree testTree, AbstractThreadGroup[] threadGroupsToKeep) {
         LinkedList<Object> copyList = new LinkedList<>(testTree.list());
         for (Object o  : copyList) {
             TestElement item = (TestElement) o;
             if (o instanceof AbstractThreadGroup) {
                 if (!isInThreadGroups(item, threadGroupsToKeep)) {
                     // hack hack hack
                     // due to the bug of equals / hashcode on AbstractTestElement
                     // where 2 AbstractTestElement can be equals but have different hashcode
                     try {
                         item.setEnabled(false);
                         testTree.remove(item);
                     } finally {
                         item.setEnabled(true);                        
                     }
                 }
                 else {
                     removeThreadGroupsFromHashTree(testTree.getTree(item), threadGroupsToKeep);
                 }
             }
             else {
                 removeThreadGroupsFromHashTree(testTree.getTree(item), threadGroupsToKeep);
             }
         }
     }
     
     /**
      * @param item {@link TestElement}
      * @param threadGroups Array of {@link AbstractThreadGroup} 
      * @return true if item is in threadGroups array
      */
     private boolean isInThreadGroups(TestElement item, AbstractThreadGroup[] threadGroups) {
         for (AbstractThreadGroup abstractThreadGroup : threadGroups) {
             if(item == abstractThreadGroup) {
                 return true;
             }
         }
         return false;
     }
     
     
     /**
      * Create a Cloner that ignores {@link Timer} if removeTimers is true
      * @param testTree {@link HashTree}
      * @param removeTimers boolean remove timers 
      * @return {@link TreeCloner}
      */
     private TreeCloner cloneTree(HashTree testTree, boolean removeTimers) {
         TreeCloner cloner = null;
         if(removeTimers) {
             cloner = new TreeClonerNoTimer(false);
         } else {
             cloner = new TreeCloner(false);     
         }
         testTree.traverse(cloner);
         return cloner;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/What.java b/src/core/org/apache/jmeter/gui/action/What.java
index 3e931b322..4f01ba8fb 100644
--- a/src/core/org/apache/jmeter/gui/action/What.java
+++ b/src/core/org/apache/jmeter/gui/action/What.java
@@ -1,93 +1,97 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.testelement.TestElement;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.HeapDumper;
-import org.apache.log.Logger;
 import org.apache.logging.log4j.Level;
 import org.apache.logging.log4j.core.config.Configurator;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  *
  * Debug class to show details of the currently selected object
  * Currently shows TestElement and GUI class names
  *
  * Also enables/disables debug for the test element.
  *
  */
 public class What extends AbstractAction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(What.class);
 
     private static final Set<String> commandSet;
 
     static {
         Set<String> commands = new HashSet<>();
         commands.add(ActionNames.WHAT_CLASS);
         commands.add(ActionNames.DEBUG_ON);
         commands.add(ActionNames.DEBUG_OFF);
         commands.add(ActionNames.HEAP_DUMP);
         commandSet = Collections.unmodifiableSet(commands);
     }
 
 
     @Override
     public void doAction(ActionEvent e) throws IllegalUserActionException {
         JMeterTreeNode node= GuiPackage.getInstance().getTreeListener().getCurrentNode();
         TestElement te = (TestElement)node.getUserObject();
         if (ActionNames.WHAT_CLASS.equals(e.getActionCommand())){
             String guiClassName = te.getPropertyAsString(TestElement.GUI_CLASS);
             System.out.println(te.getClass().getName());
             System.out.println(guiClassName);
-            log.info("TestElement:"+te.getClass().getName()+", guiClassName:"+guiClassName);
-        } else if (ActionNames.DEBUG_ON.equals(e.getActionCommand())){
-            Configurator.setAllLevels(te.getClass().getName(), Level.DEBUG);
-            log.info("Log level set to DEBUG for " + te.getClass().getName());
+            if (log.isInfoEnabled()) {
+                log.info("TestElement: {}, guiClassName: {}", te.getClass(), guiClassName);
+            }
+        } else if (ActionNames.DEBUG_ON.equals(e.getActionCommand())) {
+            final String loggerName = te.getClass().getName();
+            Configurator.setAllLevels(loggerName, Level.DEBUG);
+            log.info("Log level set to DEBUG for {}", loggerName);
         } else if (ActionNames.DEBUG_OFF.equals(e.getActionCommand())){
-            Configurator.setAllLevels(te.getClass().getName(), Level.INFO);
-            log.info("Log level set to INFO for " + te.getClass().getName());
+            final String loggerName = te.getClass().getName();
+            Configurator.setAllLevels(loggerName, Level.INFO);
+            log.info("Log level set to INFO for {}", loggerName);
         } else if (ActionNames.HEAP_DUMP.equals(e.getActionCommand())){
             try {
                 String s = HeapDumper.dumpHeap();
                 JOptionPane.showMessageDialog(null, "Created "+s, "HeapDump", JOptionPane.INFORMATION_MESSAGE);
             } catch (Exception ex) {
                 JOptionPane.showMessageDialog(null, ex.toString(), "HeapDump", JOptionPane.ERROR_MESSAGE);
             }
         }
     }
 
     /**
      * Provide the list of Action names that are available in this command.
      */
     @Override
     public Set<String> getActionNames() {
         return commandSet;
     }
 }
