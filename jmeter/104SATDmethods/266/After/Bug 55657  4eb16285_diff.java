diff --git a/src/core/org/apache/jmeter/gui/MainFrame.java b/src/core/org/apache/jmeter/gui/MainFrame.java
index 6968eadf4..2f2c66bb6 100644
--- a/src/core/org/apache/jmeter/gui/MainFrame.java
+++ b/src/core/org/apache/jmeter/gui/MainFrame.java
@@ -1,805 +1,813 @@
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
 
 package org.apache.jmeter.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.Dimension;
 import java.awt.Font;
 import java.awt.Insets;
 import java.awt.Toolkit;
 import java.awt.datatransfer.DataFlavor;
 import java.awt.datatransfer.Transferable;
 import java.awt.datatransfer.UnsupportedFlavorException;
 import java.awt.dnd.DnDConstants;
 import java.awt.dnd.DropTarget;
 import java.awt.dnd.DropTargetDragEvent;
 import java.awt.dnd.DropTargetDropEvent;
 import java.awt.dnd.DropTargetEvent;
 import java.awt.dnd.DropTargetListener;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.MouseEvent;
 import java.awt.event.WindowAdapter;
 import java.awt.event.WindowEvent;
 import java.io.File;
 import java.io.IOException;
 import java.lang.reflect.Field;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.concurrent.atomic.AtomicInteger;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.DropMode;
 import javax.swing.ImageIcon;
 import javax.swing.JButton;
 import javax.swing.JComponent;
 import javax.swing.JDialog;
 import javax.swing.JFrame;
 import javax.swing.JLabel;
 import javax.swing.JMenu;
 import javax.swing.JPanel;
 import javax.swing.JPopupMenu;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTree;
 import javax.swing.MenuElement;
 import javax.swing.SwingUtilities;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.DefaultTreeCellRenderer;
 import javax.swing.tree.TreeCellRenderer;
 import javax.swing.tree.TreeModel;
 import javax.swing.tree.TreePath;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.LoadDraggedFile;
 import org.apache.jmeter.gui.tree.JMeterCellRenderer;
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeTransferHandler;
 import org.apache.jmeter.gui.util.EscapeDialog;
 import org.apache.jmeter.gui.util.JMeterMenuBar;
 import org.apache.jmeter.gui.util.JMeterToolBar;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.Remoteable;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.LogEvent;
 import org.apache.log.LogTarget;
 import org.apache.log.Logger;
 import org.apache.log.Priority;
 
 /**
  * The main JMeter frame, containing the menu bar, test tree, and an area for
  * JMeter component GUIs.
  *
  */
 public class MainFrame extends JFrame implements TestStateListener, Remoteable, DropTargetListener, Clearable, ActionListener {
 
     private static final long serialVersionUID = 240L;
 
     // This is used to keep track of local (non-remote) tests
     // The name is chosen to be an unlikely host-name
-    private static final String LOCAL = "*local*"; // $NON-NLS-1$
+    public static final String LOCAL = "*local*"; // $NON-NLS-1$
 
     // The application name
     private static final String DEFAULT_APP_NAME = "Apache JMeter"; // $NON-NLS-1$
 
     // The default title for the Menu bar
     private static final String DEFAULT_TITLE = DEFAULT_APP_NAME +
             " (" + JMeterUtils.getJMeterVersion() + ")"; // $NON-NLS-1$ $NON-NLS-2$
 
     // Allow display/hide toolbar
     private static final boolean DISPLAY_TOOLBAR =
             JMeterUtils.getPropDefault("jmeter.toolbar.display", true); // $NON-NLS-1$
 
     // Allow display/hide LoggerPanel
     private static final boolean DISPLAY_LOGGER_PANEL =
             JMeterUtils.getPropDefault("jmeter.loggerpanel.display", false); // $NON-NLS-1$
 
     // Allow display/hide Log Error/Fatal counter
     private static final boolean DISPLAY_ERROR_FATAL_COUNTER =
             JMeterUtils.getPropDefault("jmeter.errorscounter.display", true); // $NON-NLS-1$
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** The menu bar. */
     private JMeterMenuBar menuBar;
 
     /** The main panel where components display their GUIs. */
     private JScrollPane mainPanel;
 
     /** The panel where the test tree is shown. */
     private JScrollPane treePanel;
 
     /** The LOG panel. */
     private LoggerPanel logPanel;
 
     /** The test tree. */
     private JTree tree;
 
     /** An image which is displayed when a test is running. */
     private final ImageIcon runningIcon = JMeterUtils.getImage("thread.enabled.gif");// $NON-NLS-1$
 
     /** An image which is displayed when a test is not currently running. */
     private final ImageIcon stoppedIcon = JMeterUtils.getImage("thread.disabled.gif");// $NON-NLS-1$
 
     /** An image which is displayed to indicate FATAL, ERROR or WARNING. */
     private final ImageIcon warningIcon = JMeterUtils.getImage("warning.png");// $NON-NLS-1$
 
     /** The button used to display the running/stopped image. */
     private JButton runningIndicator;
 
     /** The set of currently running hosts. */
     private final Set<String> hosts = new HashSet<String>();
 
     /** A message dialog shown while JMeter threads are stopping. */
     private JDialog stoppingMessage;
 
     private JLabel totalThreads;
     private JLabel activeThreads;
 
     private JMeterToolBar toolbar;
 
     /**
      * Indicator for Log errors and Fatals
      */
     private JButton warnIndicator;
     /**
      * Counter
      */
     private JLabel errorsOrFatalsLabel;
     /**
      * LogTarget that receives ERROR or FATAL
      */
     private transient ErrorsAndFatalsCounterLogTarget errorsAndFatalsCounterLogTarget;
 
     /**
      * Create a new JMeter frame.
      *
      * @param treeModel
      *            the model for the test tree
      * @param treeListener
      *            the listener for the test tree
      */
     public MainFrame(TreeModel treeModel, JMeterTreeListener treeListener) {
 
         // TODO: Make the running indicator its own class instead of a JButton
         runningIndicator = new JButton(stoppedIcon);
         runningIndicator.setMargin(new Insets(0, 0, 0, 0));
         runningIndicator.setBorder(BorderFactory.createEmptyBorder());
 
         totalThreads = new JLabel("0"); // $NON-NLS-1$
         totalThreads.setToolTipText(JMeterUtils.getResString("total_threads_tooltip")); // $NON-NLS-1$
         activeThreads = new JLabel("0"); // $NON-NLS-1$
         activeThreads.setToolTipText(JMeterUtils.getResString("active_threads_tooltip")); // $NON-NLS-1$
 
         warnIndicator = new JButton(warningIcon);
         warnIndicator.setMargin(new Insets(0, 0, 0, 0));
         // Transparent JButton with no border
         warnIndicator.setOpaque(false);
         warnIndicator.setContentAreaFilled(false);
         warnIndicator.setBorderPainted(false);
 
         warnIndicator.setToolTipText(JMeterUtils.getResString("error_indicator_tooltip")); // $NON-NLS-1$
         warnIndicator.addActionListener(this);
         errorsOrFatalsLabel = new JLabel("0"); // $NON-NLS-1$
         errorsOrFatalsLabel.setToolTipText(JMeterUtils.getResString("error_indicator_tooltip")); // $NON-NLS-1$
 
         tree = makeTree(treeModel, treeListener);
 
         GuiPackage.getInstance().setMainFrame(this);
         init();
         initTopLevelDndHandler();
         setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
     }
 
     /**
      * Default constructor for the JMeter frame. This constructor will not
      * properly initialize the tree, so don't use it.
      *
      * @deprecated Do not use - only needed for JUnit tests
      */
     @Deprecated
     public MainFrame() {
     }
 
     // MenuBar related methods
     // TODO: Do we really need to have all these menubar methods duplicated
     // here? Perhaps we can make the menu bar accessible through GuiPackage?
 
     /**
      * Specify whether or not the File|Load menu item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setFileLoadEnabled(boolean enabled) {
         menuBar.setFileLoadEnabled(enabled);
     }
 
     /**
      * Specify whether or not the File|Save menu item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setFileSaveEnabled(boolean enabled) {
         menuBar.setFileSaveEnabled(enabled);
     }
 
     /**
      * Specify whether or not the File|Revert item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setFileRevertEnabled(boolean enabled) {
         menuBar.setFileRevertEnabled(enabled);
     }
 
     /**
      * Specify the project file that was just loaded
      *
      * @param file - the full path to the file that was loaded
      */
     public void setProjectFileLoaded(String file) {
         menuBar.setProjectFileLoaded(file);
     }
 
     /**
      * Set the menu that should be used for the Edit menu.
      *
      * @param menu
      *            the new Edit menu
      */
     public void setEditMenu(JPopupMenu menu) {
         menuBar.setEditMenu(menu);
     }
 
     /**
      * Specify whether or not the Edit menu item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setEditEnabled(boolean enabled) {
         menuBar.setEditEnabled(enabled);
     }
 
     /**
      * Set the menu that should be used for the Edit|Add menu.
      *
      * @param menu
      *            the new Edit|Add menu
      */
     public void setEditAddMenu(JMenu menu) {
         menuBar.setEditAddMenu(menu);
     }
 
     /**
      * Specify whether or not the Edit|Add menu item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setEditAddEnabled(boolean enabled) {
         menuBar.setEditAddEnabled(enabled);
     }
 
     /**
      * Close the currently selected menu.
      */
     public void closeMenu() {
         if (menuBar.isSelected()) {
             MenuElement[] menuElement = menuBar.getSubElements();
             if (menuElement != null) {
                 for (int i = 0; i < menuElement.length; i++) {
                     JMenu menu = (JMenu) menuElement[i];
                     if (menu.isSelected()) {
                         menu.setPopupMenuVisible(false);
                         menu.setSelected(false);
                         break;
                     }
                 }
             }
         }
     }
 
     /**
      * Show a dialog indicating that JMeter threads are stopping on a particular
      * host.
      *
      * @param host
      *            the host where JMeter threads are stopping
      */
     public void showStoppingMessage(String host) {
         if (stoppingMessage != null){
             stoppingMessage.dispose();
         }
         stoppingMessage = new EscapeDialog(this, JMeterUtils.getResString("stopping_test_title"), true); //$NON-NLS-1$
         String label = JMeterUtils.getResString("stopping_test"); //$NON-NLS-1
         if(!StringUtils.isEmpty(host)) {
             label = label + JMeterUtils.getResString("stopping_test_host")+ ": " + host;
         }
         JLabel stopLabel = new JLabel(label); //$NON-NLS-1$$NON-NLS-2$
         stopLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
         stoppingMessage.getContentPane().add(stopLabel);
         stoppingMessage.pack();
         ComponentUtil.centerComponentInComponent(this, stoppingMessage);
         SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                 if (stoppingMessage != null) {// TODO - how can this be null?
                     stoppingMessage.setVisible(true);
                 }
             }
         });
     }
 
     public void updateCounts() {
         SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                 activeThreads.setText(Integer.toString(JMeterContextService.getNumberOfThreads()));
                 totalThreads.setText(Integer.toString(JMeterContextService.getTotalThreads()));
             }
         });
     }
 
     public void setMainPanel(JComponent comp) {
         mainPanel.setViewportView(comp);
     }
 
     public JTree getTree() {
         return tree;
     }
 
     // TestStateListener implementation
 
     /**
      * Called when a test is started on the local system. This implementation
      * sets the running indicator and ensures that the menubar is enabled and in
      * the running state.
      */
     @Override
     public void testStarted() {
         testStarted(LOCAL);
         menuBar.setEnabled(true);
     }
 
     /**
      * Called when a test is started on a specific host. This implementation
      * sets the running indicator and ensures that the menubar is in the running
      * state.
      *
      * @param host
      *            the host where the test is starting
      */
     @Override
     public void testStarted(String host) {
         hosts.add(host);
         runningIndicator.setIcon(runningIcon);
         activeThreads.setText("0"); // $NON-NLS-1$
         totalThreads.setText("0"); // $NON-NLS-1$
         menuBar.setRunning(true, host);
-        toolbar.setTestStarted(true);
+        if(LOCAL.equals(host)) {
+            toolbar.setLocalTestStarted(true);
+        } else {
+            toolbar.setRemoteTestStarted(true);
+        }
     }
 
     /**
      * Called when a test is ended on the local system. This implementation
      * disables the menubar, stops the running indicator, and closes the
      * stopping message dialog.
      */
     @Override
     public void testEnded() {
         testEnded(LOCAL);
         menuBar.setEnabled(false);
     }
 
     /**
      * Called when a test is ended on the remote system. This implementation
      * stops the running indicator and closes the stopping message dialog.
      *
      * @param host
      *            the host where the test is ending
      */
     @Override
     public void testEnded(String host) {
         hosts.remove(host);
         if (hosts.size() == 0) {
             runningIndicator.setIcon(stoppedIcon);
             JMeterContextService.endTest();
         }
         menuBar.setRunning(false, host);
-        toolbar.setTestStarted(false);
+        if(LOCAL.equals(host)) {
+            toolbar.setLocalTestStarted(false);
+        } else {
+            toolbar.setRemoteTestStarted(false);
+        }
         if (stoppingMessage != null) {
             stoppingMessage.dispose();
             stoppingMessage = null;
         }
     }
 
     /**
      * Create the GUI components and layout.
      */
     private void init() {
         menuBar = new JMeterMenuBar();
         setJMenuBar(menuBar);
         JPanel all = new JPanel(new BorderLayout());
         all.add(createToolBar(), BorderLayout.NORTH);
 
         JSplitPane treeAndMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
 
         treePanel = createTreePanel();
         treeAndMain.setLeftComponent(treePanel);
 
         JSplitPane topAndDown = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
         topAndDown.setOneTouchExpandable(true);
         topAndDown.setDividerLocation(0.8);
         topAndDown.setResizeWeight(.8);
         topAndDown.setContinuousLayout(true);
         topAndDown.setBorder(null); // see bug jdk 4131528
         if (!DISPLAY_LOGGER_PANEL) {
             topAndDown.setDividerSize(0);
         }
         mainPanel = createMainPanel();
 
         logPanel = createLoggerPanel();
         if (DISPLAY_ERROR_FATAL_COUNTER) {
             errorsAndFatalsCounterLogTarget = new ErrorsAndFatalsCounterLogTarget();
             LoggingManager.addLogTargetToRootLogger(new LogTarget[]{
                 logPanel,
                 errorsAndFatalsCounterLogTarget
                  });
         } else {
             LoggingManager.addLogTargetToRootLogger(new LogTarget[]{
                     logPanel
                      });
         }
 
         topAndDown.setTopComponent(mainPanel);
         topAndDown.setBottomComponent(logPanel);
 
         treeAndMain.setRightComponent(topAndDown);
 
         treeAndMain.setResizeWeight(.2);
         treeAndMain.setContinuousLayout(true);
         all.add(treeAndMain, BorderLayout.CENTER);
 
         getContentPane().add(all);
 
         tree.setSelectionRow(1);
         addWindowListener(new WindowHappenings());
 
         setTitle(DEFAULT_TITLE);
         setIconImage(JMeterUtils.getImage("icon-apache.png").getImage());// $NON-NLS-1$
         setWindowTitle(); // define AWT WM_CLASS string
     }
 
 
     /**
      * Support for Test Plan Dnd
      * see BUG 52281 (when JDK6 will be minimum JDK target)
      */
     public void initTopLevelDndHandler() {
         new DropTarget(this, this);
     }
 
     public void setExtendedFrameTitle(String fname) {
         // file New operation may set to null, so just return app name
         if (fname == null) {
             setTitle(DEFAULT_TITLE);
             return;
         }
 
         // allow for windows / chars in filename
         String temp = fname.replace('\\', '/'); // $NON-NLS-1$ // $NON-NLS-2$
         String simpleName = temp.substring(temp.lastIndexOf('/') + 1);// $NON-NLS-1$
         setTitle(simpleName + " (" + fname + ") - " + DEFAULT_TITLE); // $NON-NLS-1$ // $NON-NLS-2$
     }
 
     /**
      * Create the JMeter tool bar pane containing the running indicator.
      *
      * @return a panel containing the running indicator
      */
     private Component createToolBar() {
         Box toolPanel = new Box(BoxLayout.X_AXIS);
         // add the toolbar
         this.toolbar = JMeterToolBar.createToolbar(DISPLAY_TOOLBAR);
         GuiPackage guiInstance = GuiPackage.getInstance();
         guiInstance.setMainToolbar(toolbar);
         guiInstance.getMenuItemToolbar().getModel().setSelected(DISPLAY_TOOLBAR);
         toolPanel.add(toolbar);
 
         toolPanel.add(Box.createRigidArea(new Dimension(10, 15)));
         toolPanel.add(Box.createGlue());
 
         if (DISPLAY_ERROR_FATAL_COUNTER) {
             toolPanel.add(errorsOrFatalsLabel);
             toolPanel.add(warnIndicator);
             toolPanel.add(Box.createRigidArea(new Dimension(20, 15)));
         }
         toolPanel.add(activeThreads);
         toolPanel.add(new JLabel(" / "));
         toolPanel.add(totalThreads);
         toolPanel.add(Box.createRigidArea(new Dimension(10, 15)));
         toolPanel.add(runningIndicator);
         return toolPanel;
     }
 
     /**
      * Create the panel where the GUI representation of the test tree is
      * displayed. The tree should already be created before calling this method.
      *
      * @return a scroll pane containing the test tree GUI
      */
     private JScrollPane createTreePanel() {
         JScrollPane treeP = new JScrollPane(tree);
         treeP.setMinimumSize(new Dimension(100, 0));
         return treeP;
     }
 
     /**
      * Create the main panel where components can display their GUIs.
      *
      * @return the main scroll pane
      */
     private JScrollPane createMainPanel() {
         return new JScrollPane();
     }
 
     /**
      * Create at the down of the left a Console for Log events
      * @return {@link LoggerPanel}
      */
     private LoggerPanel createLoggerPanel() {
         LoggerPanel loggerPanel = new LoggerPanel();
         loggerPanel.setMinimumSize(new Dimension(0, 100));
         loggerPanel.setPreferredSize(new Dimension(0, 150));
         GuiPackage guiInstance = GuiPackage.getInstance();
         guiInstance.setLoggerPanel(loggerPanel);
         guiInstance.getMenuItemLoggerPanel().getModel().setSelected(DISPLAY_LOGGER_PANEL);
         loggerPanel.setVisible(DISPLAY_LOGGER_PANEL);
         return loggerPanel;
     }
 
     /**
      * Create and initialize the GUI representation of the test tree.
      *
      * @param treeModel
      *            the test tree model
      * @param treeListener
      *            the test tree listener
      *
      * @return the initialized test tree GUI
      */
     private JTree makeTree(TreeModel treeModel, JMeterTreeListener treeListener) {
         JTree treevar = new JTree(treeModel) {
             private static final long serialVersionUID = 240L;
 
             @Override
             public String getToolTipText(MouseEvent event) {
                 TreePath path = this.getPathForLocation(event.getX(), event.getY());
                 if (path != null) {
                     Object treeNode = path.getLastPathComponent();
                     if (treeNode instanceof DefaultMutableTreeNode) {
                         Object testElement = ((DefaultMutableTreeNode) treeNode).getUserObject();
                         if (testElement instanceof TestElement) {
                             String comment = ((TestElement) testElement).getComment();
                             if (comment != null && comment.length() > 0) {
                                 return comment;
                                 }
                             }
                         }
                     }
                 return null;
                 }
             };
         treevar.setToolTipText("");
         treevar.setCellRenderer(getCellRenderer());
         treevar.setRootVisible(false);
         treevar.setShowsRootHandles(true);
 
         treeListener.setJTree(treevar);
         treevar.addTreeSelectionListener(treeListener);
         treevar.addMouseListener(treeListener);
         treevar.addKeyListener(treeListener);
         
         // enable drag&drop, install a custom transfer handler
         treevar.setDragEnabled(true);
         treevar.setDropMode(DropMode.ON_OR_INSERT);
         treevar.setTransferHandler(new JMeterTreeTransferHandler());
 
         return treevar;
     }
 
     /**
      * Create the tree cell renderer used to draw the nodes in the test tree.
      *
      * @return a renderer to draw the test tree nodes
      */
     private TreeCellRenderer getCellRenderer() {
         DefaultTreeCellRenderer rend = new JMeterCellRenderer();
         rend.setFont(new Font("Dialog", Font.PLAIN, 11));
         return rend;
     }
 
     /**
      * A window adapter used to detect when the main JMeter frame is being
      * closed.
      */
     private static class WindowHappenings extends WindowAdapter {
         /**
          * Called when the main JMeter frame is being closed. Sends a
          * notification so that JMeter can react appropriately.
          *
          * @param event
          *            the WindowEvent to handle
          */
         @Override
         public void windowClosing(WindowEvent event) {
             ActionRouter.getInstance().actionPerformed(new ActionEvent(this, event.getID(), ActionNames.EXIT));
         }
     }
 
     @Override
     public void dragEnter(DropTargetDragEvent dtde) {
         // NOOP
     }
 
     @Override
     public void dragExit(DropTargetEvent dte) {
         // NOOP
     }
 
     @Override
     public void dragOver(DropTargetDragEvent dtde) {
         // NOOP
     }
 
     /**
      * Handler of Top level Dnd
      */
     @Override
     public void drop(DropTargetDropEvent dtde) {
         try {
             Transferable tr = dtde.getTransferable();
             DataFlavor[] flavors = tr.getTransferDataFlavors();
             for (int i = 0; i < flavors.length; i++) {
                 // Check for file lists specifically
                 if (flavors[i].isFlavorJavaFileListType()) {
                     dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                     try {
                         openJmxFilesFromDragAndDrop(tr);
                     } finally {
                         dtde.dropComplete(true);
                     }
                     return;
                 }
             }
         } catch (UnsupportedFlavorException e) {
             log.warn("Dnd failed" , e);
         } catch (IOException e) {
             log.warn("Dnd failed" , e);
         }
 
     }
 
     public boolean openJmxFilesFromDragAndDrop(Transferable tr) throws UnsupportedFlavorException, IOException {
         @SuppressWarnings("unchecked")
         List<File> files = (List<File>)
                 tr.getTransferData(DataFlavor.javaFileListFlavor);
         if(files.isEmpty()) {
             return false;
         }
         File file = files.get(0);
         if(!file.getName().endsWith(".jmx")) {
             log.warn("Importing file:" + file.getName()+ "from DnD failed because file extension does not end with .jmx");
             return false;
         }
 
         ActionEvent fakeEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ActionNames.OPEN);
         LoadDraggedFile.loadProject(fakeEvent, file);
         
         return true;
     }
 
     @Override
     public void dropActionChanged(DropTargetDragEvent dtde) {
         // NOOP
     }
 
     /**
      *
      */
     public final class ErrorsAndFatalsCounterLogTarget implements LogTarget, Clearable {
         public AtomicInteger errorOrFatal = new AtomicInteger(0);
 
         @Override
         public void processEvent(LogEvent event) {
             if(event.getPriority().equals(Priority.ERROR) ||
                     event.getPriority().equals(Priority.FATAL_ERROR)) {
                 final int newValue = errorOrFatal.incrementAndGet();
                 SwingUtilities.invokeLater(new Runnable() {
                     @Override
                     public void run() {
                         errorsOrFatalsLabel.setText(Integer.toString(newValue));
                     }
                 });
             }
         }
 
         @Override
         public void clearData() {
             errorOrFatal.set(0);
             SwingUtilities.invokeLater(new Runnable() {
                 @Override
                 public void run() {
                     errorsOrFatalsLabel.setText(Integer.toString(errorOrFatal.get()));
                 }
             });
         }
     }
 
 
     @Override
     public void clearData() {
         logPanel.clear();
         if(DISPLAY_ERROR_FATAL_COUNTER) {
             errorsAndFatalsCounterLogTarget.clearData();
         }
     }
 
     /**
      * Handles click on warnIndicator
      */
     @Override
     public void actionPerformed(ActionEvent event) {
         if(event.getSource()==warnIndicator) {
             ActionRouter.getInstance().doActionNow(new ActionEvent(event.getSource(), event.getID(), ActionNames.LOGGER_PANEL_ENABLE_DISABLE));
         }
     }
 
     /**
      * Define AWT window title (WM_CLASS string) (useful on Gnome 3 / Linux)
      */
     private void setWindowTitle() {
         Class<?> xtoolkit = Toolkit.getDefaultToolkit().getClass();
         if (xtoolkit.getName().equals("sun.awt.X11.XToolkit")) { // $NON-NLS-1$
             try {
                 final Field awtAppClassName = xtoolkit.getDeclaredField("awtAppClassName"); // $NON-NLS-1$
                 awtAppClassName.setAccessible(true);
                 awtAppClassName.set(null, DEFAULT_APP_NAME);
             } catch (NoSuchFieldException nsfe) {
                 log.warn("Error awt title: " + nsfe); // $NON-NLS-1$
             } catch (IllegalAccessException iae) {
                 log.warn("Error awt title: " + iae); // $NON-NLS-1$
             }
        }
     }
 }
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
index 9b37bfb25..2f8953e59 100644
--- a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
+++ b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
@@ -1,839 +1,841 @@
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
 
 package org.apache.jmeter.gui.util;
 
 import java.awt.Component;
 import java.awt.event.KeyEvent;
 import java.io.IOException;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 
 import javax.swing.JCheckBoxMenuItem;
 import javax.swing.JComponent;
 import javax.swing.JMenu;
 import javax.swing.JMenuBar;
 import javax.swing.JMenuItem;
 import javax.swing.JPopupMenu;
 import javax.swing.KeyStroke;
 import javax.swing.MenuElement;
 import javax.swing.UIManager;
 import javax.swing.UIManager.LookAndFeelInfo;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jmeter.gui.action.LoadRecentProject;
 import org.apache.jmeter.gui.plugin.MenuCreator;
 import org.apache.jmeter.gui.plugin.MenuCreator.MENU_LOCATION;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.LocaleChangeEvent;
 import org.apache.jmeter.util.LocaleChangeListener;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 public class JMeterMenuBar extends JMenuBar implements LocaleChangeListener {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private JMenu fileMenu;
 
     private JMenuItem file_save_as;
 
     private JMenuItem file_selection_as;
 
     private JMenuItem file_revert;
 
     private JMenuItem file_load;
 
     private JMenuItem templates;
 
     private List<JComponent> file_load_recent_files;
 
     private JMenuItem file_merge;
 
     private JMenuItem file_exit;
 
     private JMenuItem file_close;
 
     private JMenu editMenu;
 
     private JMenu edit_add;
 
     private JMenu runMenu;
 
     private JMenuItem run_start;
 
     private JMenuItem run_start_no_timers;
 
     private JMenu remote_start;
 
     private JMenuItem remote_start_all;
 
     private Collection<JMenuItem> remote_engine_start;
 
     private JMenuItem run_stop;
 
     private JMenuItem run_shut;
 
     private JMenu remote_stop;
 
     private JMenu remote_shut;
 
     private JMenuItem remote_stop_all;
 
     private JMenuItem remote_shut_all;
 
     private Collection<JMenuItem> remote_engine_stop;
 
     private Collection<JMenuItem> remote_engine_shut;
 
     private JMenuItem run_clear;
 
     private JMenuItem run_clearAll;
 
     // JMenu reportMenu;
     // JMenuItem analyze;
     private JMenu optionsMenu;
 
     private JMenu lafMenu;
 
     private JMenuItem sslManager;
 
     private JMenu helpMenu;
 
     private JMenuItem help_about;
 
     private String[] remoteHosts;
 
     private JMenu remote_exit;
 
     private JMenuItem remote_exit_all;
 
     private Collection<JMenuItem> remote_engine_exit;
 
     private JMenu searchMenu;
 
     private ArrayList<MenuCreator> menuCreators;
 
     public static final String SYSTEM_LAF = "System"; // $NON-NLS-1$
 
     public static final String CROSS_PLATFORM_LAF = "CrossPlatform"; // $NON-NLS-1$
 
     public JMeterMenuBar() {
         // List for recent files menu items
         file_load_recent_files = new LinkedList<JComponent>();
         // Lists for remote engines menu items
         remote_engine_start = new LinkedList<JMenuItem>();
         remote_engine_stop = new LinkedList<JMenuItem>();
         remote_engine_shut = new LinkedList<JMenuItem>();
         remote_engine_exit = new LinkedList<JMenuItem>();
         remoteHosts = JOrphanUtils.split(JMeterUtils.getPropDefault("remote_hosts", ""), ","); //$NON-NLS-1$
         if (remoteHosts.length == 1 && remoteHosts[0].equals("")) {
             remoteHosts = new String[0];
         }
         this.getRemoteItems();
         createMenuBar();
         JMeterUtils.addLocaleChangeListener(this);
     }
 
     public void setFileSaveEnabled(boolean enabled) {
         if(file_save_as != null) {
             file_save_as.setEnabled(enabled);
         }
     }
 
     public void setFileLoadEnabled(boolean enabled) {
         if (file_load != null) {
             file_load.setEnabled(enabled);
         }
         if (file_merge != null) {
             file_merge.setEnabled(enabled);
         }
     }
 
     public void setFileRevertEnabled(boolean enabled) {
         if(file_revert != null) {
             file_revert.setEnabled(enabled);
         }
     }
 
     public void setProjectFileLoaded(String file) {
         if(file_load_recent_files != null && file != null) {
             LoadRecentProject.updateRecentFileMenuItems(file_load_recent_files, file);
         }
     }
 
     public void setEditEnabled(boolean enabled) {
         if (editMenu != null) {
             editMenu.setEnabled(enabled);
         }
     }
 
     // Does not appear to be used; called by MainFrame#setEditAddMenu() but that is not called
     public void setEditAddMenu(JMenu menu) {
         // If the Add menu already exists, remove it.
         if (edit_add != null) {
             editMenu.remove(edit_add);
         }
         // Insert the Add menu as the first menu item in the Edit menu.
         edit_add = menu;
         editMenu.insert(edit_add, 0);
     }
 
     // Called by MainFrame#setEditMenu() which is called by EditCommand#doAction and GuiPackage#localeChanged
     public void setEditMenu(JPopupMenu menu) {
         if (menu != null) {
             editMenu.removeAll();
             Component[] comps = menu.getComponents();
             for (int i = 0; i < comps.length; i++) {
                 editMenu.add(comps[i]);
             }
             editMenu.setEnabled(true);
         } else {
             editMenu.setEnabled(false);
         }
     }
 
     public void setEditAddEnabled(boolean enabled) {
         // There was a NPE being thrown without the null check here.. JKB
         if (edit_add != null) {
             edit_add.setEnabled(enabled);
         }
         // If we are enabling the Edit-->Add menu item, then we also need to
         // enable the Edit menu. The Edit menu may already be enabled, but
         // there's no harm it trying to enable it again.
         setEditEnabled(enabled);
     }
 
     /**
      * Creates the MenuBar for this application. I believe in my heart that this
      * should be defined in a file somewhere, but that is for later.
      */
     public void createMenuBar() {
         this.menuCreators = new ArrayList<MenuCreator>();
         try {
             List<String> listClasses = ClassFinder.findClassesThatExtend(
                     JMeterUtils.getSearchPaths(), 
                     new Class[] {MenuCreator.class }); 
             for (String strClassName : listClasses) {
                 try {
                     if(log.isDebugEnabled()) {
                         log.debug("Loading menu creator class: "+ strClassName);
                     }
                     Class<?> commandClass = Class.forName(strClassName);
                     if (!Modifier.isAbstract(commandClass.getModifiers())) {
                         if(log.isDebugEnabled()) {
                             log.debug("Instantiating: "+ commandClass.getName());
                         }
                         MenuCreator creator = (MenuCreator) commandClass.newInstance();
                         menuCreators.add(creator);                  
                     }
                 } catch (Exception e) {
                     log.error("Exception registering "+MenuCreator.class.getName() + " with implementation:"+strClassName, e);
                 }
             }
         } catch (IOException e) {
             log.error("Exception finding implementations of "+MenuCreator.class, e);
         }
 
         makeFileMenu();
         makeEditMenu();
         makeRunMenu();
         makeOptionsMenu();
         makeHelpMenu();
         makeSearchMenu();
         this.add(fileMenu);
         this.add(editMenu);
         this.add(searchMenu);
         this.add(runMenu);
         this.add(optionsMenu);
         for (Iterator<MenuCreator> iterator = menuCreators.iterator(); iterator.hasNext();) {
             MenuCreator menuCreator = iterator.next();
             JMenu[] topLevelMenus = menuCreator.getTopLevelMenus();
             for (JMenu topLevelMenu : topLevelMenus) {
                 this.add(topLevelMenu);                
             }
         }
         this.add(helpMenu);
     }
 
     private void makeHelpMenu() {
         // HELP MENU
         helpMenu = makeMenuRes("help",'H'); //$NON-NLS-1$
 
         JMenuItem contextHelp = makeMenuItemRes("help", 'H', ActionNames.HELP, KeyStrokes.HELP); //$NON-NLS-1$
 
         JMenuItem whatClass = makeMenuItemRes("help_node", 'W', ActionNames.WHAT_CLASS, KeyStrokes.WHAT_CLASS);//$NON-NLS-1$
 
         JMenuItem setDebug = makeMenuItemRes("debug_on", ActionNames.DEBUG_ON, KeyStrokes.DEBUG_ON);//$NON-NLS-1$
 
         JMenuItem resetDebug = makeMenuItemRes("debug_off", ActionNames.DEBUG_OFF, KeyStrokes.DEBUG_OFF);//$NON-NLS-1$
 
         JMenuItem heapDump = makeMenuItemRes("heap_dump", ActionNames.HEAP_DUMP);//$NON-NLS-1$
 
         help_about = makeMenuItemRes("about", 'A', ActionNames.ABOUT); //$NON-NLS-1$
 
         helpMenu.add(contextHelp);
         helpMenu.addSeparator();
         helpMenu.add(whatClass);
         helpMenu.add(setDebug);
         helpMenu.add(resetDebug);
         helpMenu.add(heapDump);
 
         addPluginsMenuItems(helpMenu, menuCreators, MENU_LOCATION.HELP);
 
         helpMenu.addSeparator();
         helpMenu.add(help_about);
     }
 
     private void makeOptionsMenu() {
         // OPTIONS MENU
         optionsMenu = makeMenuRes("option",'O'); //$NON-NLS-1$
         JMenuItem functionHelper = makeMenuItemRes("function_dialog_menu_item", 'F', ActionNames.FUNCTIONS, KeyStrokes.FUNCTIONS); //$NON-NLS-1$
 
         lafMenu = makeMenuRes("appearance",'L'); //$NON-NLS-1$
         UIManager.LookAndFeelInfo lafs[] = getAllLAFs();
         for (int i = 0; i < lafs.length; ++i) {
             JMenuItem laf = new JMenuItem(lafs[i].getName());
             laf.addActionListener(ActionRouter.getInstance());
             laf.setActionCommand(ActionNames.LAF_PREFIX + lafs[i].getClassName());
             laf.setToolTipText(lafs[i].getClassName()); // show the classname to the user
             lafMenu.add(laf);
         }
         optionsMenu.add(functionHelper);
         optionsMenu.add(lafMenu);
 
         JCheckBoxMenuItem menuToolBar = makeCheckBoxMenuItemRes("menu_toolbar", ActionNames.TOOLBAR); //$NON-NLS-1$
         JCheckBoxMenuItem menuLoggerPanel = makeCheckBoxMenuItemRes("menu_logger_panel", ActionNames.LOGGER_PANEL_ENABLE_DISABLE); //$NON-NLS-1$
         GuiPackage guiInstance = GuiPackage.getInstance();
         if (guiInstance != null) { //avoid error in ant task tests (good way?)
             guiInstance.setMenuItemToolbar(menuToolBar);
             guiInstance.setMenuItemLoggerPanel(menuLoggerPanel);
         }
         optionsMenu.add(menuToolBar);
         optionsMenu.add(menuLoggerPanel);
         
         if (SSLManager.isSSLSupported()) {
             sslManager = makeMenuItemRes("sslmanager", 'S', ActionNames.SSL_MANAGER, KeyStrokes.SSL_MANAGER); //$NON-NLS-1$
             optionsMenu.add(sslManager);
         }
         optionsMenu.add(makeLanguageMenu());
 
         JMenuItem collapse = makeMenuItemRes("menu_collapse_all", ActionNames.COLLAPSE_ALL, KeyStrokes.COLLAPSE_ALL); //$NON-NLS-1$
         optionsMenu.add(collapse);
 
         JMenuItem expand = makeMenuItemRes("menu_expand_all", ActionNames.EXPAND_ALL, KeyStrokes.EXPAND_ALL); //$NON-NLS-1$
         optionsMenu.add(expand);
 
         addPluginsMenuItems(optionsMenu, menuCreators, MENU_LOCATION.OPTIONS);
     }
 
     private static class LangMenuHelper{
         final ActionRouter actionRouter = ActionRouter.getInstance();
         final JMenu languageMenu;
 
         LangMenuHelper(JMenu _languageMenu){
             languageMenu = _languageMenu;
         }
 
         /**
          * Create a language entry from the locale name.
          *
          * @param locale - must also be a valid resource name
          */
         void addLang(String locale){
             String localeString = JMeterUtils.getLocaleString(locale);
             JMenuItem language = new JMenuItem(localeString);
             language.addActionListener(actionRouter);
             language.setActionCommand(ActionNames.CHANGE_LANGUAGE);
             language.setName(locale); // This is used by the ChangeLanguage class to define the Locale
             languageMenu.add(language);
         }
 
    }
 
     /**
      * Generate the list of supported languages.
      *
      * @return list of languages
      */
     // Also used by org.apache.jmeter.resources.PackageTest
     public static String[] getLanguages(){
         List<String> lang = new ArrayList<String>(20);
         lang.add(Locale.ENGLISH.toString()); // en
         lang.add(Locale.FRENCH.toString()); // fr
         lang.add(Locale.GERMAN.toString()); // de
         lang.add("no"); // $NON-NLS-1$
         lang.add("pl"); // $NON-NLS-1$
         lang.add("pt_BR"); // $NON-NLS-1$
         lang.add("es"); // $NON-NLS-1$
         lang.add("tr"); // $NON-NLS-1$
         lang.add(Locale.JAPANESE.toString()); // ja
         lang.add(Locale.SIMPLIFIED_CHINESE.toString()); // zh_CN
         lang.add(Locale.TRADITIONAL_CHINESE.toString()); // zh_TW
         final String addedLocales = JMeterUtils.getProperty("locales.add");
         if (addedLocales != null){
             String [] addLanguages =addedLocales.split(","); // $NON-NLS-1$
             for(String newLang : addLanguages){
                 log.info("Adding locale "+newLang);
                 lang.add(newLang);
             }
         }
         return lang.toArray(new String[lang.size()]);
     }
 
     static JMenu makeLanguageMenu() {
         final JMenu languageMenu = makeMenuRes("choose_language",'C'); //$NON-NLS-1$
 
         LangMenuHelper langMenu = new LangMenuHelper(languageMenu);
 
         /*
          * Note: the item name is used by ChangeLanguage to create a Locale for
          * that language, so need to ensure that the language strings are valid
          * If they exist, use the Locale language constants.
          * Also, need to ensure that the names are valid resource entries too.
          */
 
         for(String lang : getLanguages()){
             langMenu.addLang(lang);
         }
         return languageMenu;
     }
 
     private void makeRunMenu() {
         // RUN MENU
         runMenu = makeMenuRes("run",'R'); //$NON-NLS-1$
 
         run_start = makeMenuItemRes("start", 'S', ActionNames.ACTION_START, KeyStrokes.ACTION_START); //$NON-NLS-1$
 
         run_start_no_timers = makeMenuItemRes("start_no_timers", ActionNames.ACTION_START_NO_TIMERS); //$NON-NLS-1$
         
         run_stop = makeMenuItemRes("stop", 'T', ActionNames.ACTION_STOP, KeyStrokes.ACTION_STOP); //$NON-NLS-1$
         run_stop.setEnabled(false);
 
         run_shut = makeMenuItemRes("shutdown", 'Y', ActionNames.ACTION_SHUTDOWN, KeyStrokes.ACTION_SHUTDOWN); //$NON-NLS-1$
         run_shut.setEnabled(false);
 
         run_clear = makeMenuItemRes("clear", 'C', ActionNames.CLEAR, KeyStrokes.CLEAR); //$NON-NLS-1$
 
         run_clearAll = makeMenuItemRes("clear_all", 'a', ActionNames.CLEAR_ALL, KeyStrokes.CLEAR_ALL); //$NON-NLS-1$
 
         runMenu.add(run_start);
         runMenu.add(run_start_no_timers);
         if (remote_start != null) {
             runMenu.add(remote_start);
         }
         remote_start_all = makeMenuItemRes("remote_start_all", ActionNames.REMOTE_START_ALL, KeyStrokes.REMOTE_START_ALL); //$NON-NLS-1$
 
         runMenu.add(remote_start_all);
         runMenu.add(run_stop);
         runMenu.add(run_shut);
         if (remote_stop != null) {
             runMenu.add(remote_stop);
         }
         remote_stop_all = makeMenuItemRes("remote_stop_all", 'X', ActionNames.REMOTE_STOP_ALL, KeyStrokes.REMOTE_STOP_ALL); //$NON-NLS-1$
         runMenu.add(remote_stop_all);
 
         if (remote_shut != null) {
             runMenu.add(remote_shut);
         }
         remote_shut_all = makeMenuItemRes("remote_shut_all", 'X', ActionNames.REMOTE_SHUT_ALL, KeyStrokes.REMOTE_SHUT_ALL); //$NON-NLS-1$
         runMenu.add(remote_shut_all);
 
         if (remote_exit != null) {
             runMenu.add(remote_exit);
         }
         remote_exit_all = makeMenuItemRes("remote_exit_all", ActionNames.REMOTE_EXIT_ALL); //$NON-NLS-1$
         runMenu.add(remote_exit_all);
 
         runMenu.addSeparator();
         runMenu.add(run_clear);
         runMenu.add(run_clearAll);
 
         addPluginsMenuItems(runMenu, menuCreators, MENU_LOCATION.RUN);
     }
 
     private void makeEditMenu() {
         // EDIT MENU
         editMenu = makeMenuRes("edit",'E'); //$NON-NLS-1$
 
         // From the Java Look and Feel Guidelines: If all items in a menu
         // are disabled, then disable the menu. Makes sense.
         editMenu.setEnabled(false);
 
         addPluginsMenuItems(editMenu, menuCreators, MENU_LOCATION.EDIT);
     }
 
     private void makeFileMenu() {
         // FILE MENU
         fileMenu = makeMenuRes("file",'F'); //$NON-NLS-1$
 
         JMenuItem file_save = makeMenuItemRes("save", 'S', ActionNames.SAVE, KeyStrokes.SAVE); //$NON-NLS-1$
         file_save.setEnabled(true);
 
         file_save_as = makeMenuItemRes("save_all_as", 'A', ActionNames.SAVE_ALL_AS, KeyStrokes.SAVE_ALL_AS); //$NON-NLS-1$
         file_save_as.setEnabled(true);
 
         file_selection_as = makeMenuItemRes("save_as", ActionNames.SAVE_AS); //$NON-NLS-1$
         file_selection_as.setEnabled(true);
 
         file_revert = makeMenuItemRes("revert_project", 'R', ActionNames.REVERT_PROJECT); //$NON-NLS-1$
         file_revert.setEnabled(false);
 
         file_load = makeMenuItemRes("menu_open", 'O', ActionNames.OPEN, KeyStrokes.OPEN); //$NON-NLS-1$
         // Set default SAVE menu item to disabled since the default node that
         // is selected is ROOT, which does not allow items to be inserted.
         file_load.setEnabled(false);
 
         templates = makeMenuItemRes("template_menu", 'T', ActionNames.TEMPLATES); //$NON-NLS-1$
         templates.setEnabled(true);
 
         file_close = makeMenuItemRes("menu_close", 'C', ActionNames.CLOSE, KeyStrokes.CLOSE); //$NON-NLS-1$
 
         file_exit = makeMenuItemRes("exit", 'X', ActionNames.EXIT, KeyStrokes.EXIT); //$NON-NLS-1$
 
         file_merge = makeMenuItemRes("menu_merge", 'M', ActionNames.MERGE); //$NON-NLS-1$
         // file_merge.setAccelerator(
         // KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
         // Set default SAVE menu item to disabled since the default node that
         // is selected is ROOT, which does not allow items to be inserted.
         file_merge.setEnabled(false);
 
         fileMenu.add(file_close);
         fileMenu.add(file_load);
         fileMenu.add(templates);
         fileMenu.add(file_merge);
         fileMenu.addSeparator();
         fileMenu.add(file_save);
         fileMenu.add(file_save_as);
         fileMenu.add(file_selection_as);
         fileMenu.add(file_revert);
         fileMenu.addSeparator();
         // Add the recent files, which will also add a separator that is
         // visible when needed
         file_load_recent_files = LoadRecentProject.getRecentFileMenuItems();
         for(JComponent jc : file_load_recent_files){
             fileMenu.add(jc);
         }
 
         addPluginsMenuItems(fileMenu, menuCreators, MENU_LOCATION.FILE);
 
         fileMenu.add(file_exit);
     }
 
     private void makeSearchMenu() {
         // Search MENU
         searchMenu = makeMenuRes("menu_search"); //$NON-NLS-1$
 
         JMenuItem search = makeMenuItemRes("menu_search", 'F', ActionNames.SEARCH_TREE, KeyStrokes.SEARCH_TREE); //$NON-NLS-1$
         searchMenu.add(search);
         search.setEnabled(true);
 
         JMenuItem searchReset = makeMenuItemRes("menu_search_reset", ActionNames.SEARCH_RESET); //$NON-NLS-1$
         searchMenu.add(searchReset);
         searchReset.setEnabled(true);
 
         addPluginsMenuItems(searchMenu, menuCreators, MENU_LOCATION.SEARCH);
     }
 
     /**
      * @param menu 
      * @param menuCreators
      * @param location
      */
     private void addPluginsMenuItems(JMenu menu, List<MenuCreator> menuCreators, MENU_LOCATION location) {
         boolean addedSeparator = false;
         for (MenuCreator menuCreator : menuCreators) {
             JMenuItem[] menuItems = menuCreator.getMenuItemsAtLocation(location);
             for (JMenuItem jMenuItem : menuItems) {
                 if(!addedSeparator) {
                     menu.addSeparator();
                     addedSeparator = true;
                 }
                 menu.add(jMenuItem);
             }
         }
     }
     
     public void setRunning(boolean running, String host) {
         log.info("setRunning(" + running + "," + host + ")");
-
+        if(org.apache.jmeter.gui.MainFrame.LOCAL.equals(host)) {
+            return;
+        }
         Iterator<JMenuItem> iter = remote_engine_start.iterator();
         Iterator<JMenuItem> iter2 = remote_engine_stop.iterator();
         Iterator<JMenuItem> iter3 = remote_engine_exit.iterator();
         Iterator<JMenuItem> iter4 = remote_engine_shut.iterator();
         while (iter.hasNext() && iter2.hasNext() && iter3.hasNext() &&iter4.hasNext()) {
             JMenuItem start = iter.next();
             JMenuItem stop = iter2.next();
             JMenuItem exit = iter3.next();
             JMenuItem shut = iter4.next();
             if (start.getText().equals(host)) {
                 log.debug("Found start host: " + start.getText());
                 start.setEnabled(!running);
             }
             if (stop.getText().equals(host)) {
                 log.debug("Found stop  host: " + stop.getText());
                 stop.setEnabled(running);
             }
             if (exit.getText().equals(host)) {
                 log.debug("Found exit  host: " + exit.getText());
                 exit.setEnabled(true);
             }
             if (shut.getText().equals(host)) {
                 log.debug("Found exit  host: " + exit.getText());
                 shut.setEnabled(running);
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void setEnabled(boolean enable) {
         run_start.setEnabled(!enable);
         run_start_no_timers.setEnabled(!enable);
         run_stop.setEnabled(enable);
         run_shut.setEnabled(enable);
     }
 
     private void getRemoteItems() {
         if (remoteHosts.length > 0) {
             remote_start = makeMenuRes("remote_start"); //$NON-NLS-1$
             remote_stop = makeMenuRes("remote_stop"); //$NON-NLS-1$
             remote_shut = makeMenuRes("remote_shut"); //$NON-NLS-1$
             remote_exit = makeMenuRes("remote_exit"); //$NON-NLS-1$
 
             for (int i = 0; i < remoteHosts.length; i++) {
                 remoteHosts[i] = remoteHosts[i].trim();
 
                 JMenuItem item = makeMenuItemNoRes(remoteHosts[i], ActionNames.REMOTE_START);
                 remote_engine_start.add(item);
                 remote_start.add(item);
 
                 item = makeMenuItemNoRes(remoteHosts[i], ActionNames.REMOTE_STOP);
                 item.setEnabled(false);
                 remote_engine_stop.add(item);
                 remote_stop.add(item);
 
                 item = makeMenuItemNoRes(remoteHosts[i], ActionNames.REMOTE_SHUT);
                 item.setEnabled(false);
                 remote_engine_shut.add(item);
                 remote_shut.add(item);
 
                 item = makeMenuItemNoRes(remoteHosts[i],ActionNames.REMOTE_EXIT);
                 item.setEnabled(false);
                 remote_engine_exit.add(item);
                 remote_exit.add(item);
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void localeChanged(LocaleChangeEvent event) {
         updateMenuElement(fileMenu);
         updateMenuElement(editMenu);
         updateMenuElement(searchMenu);
         updateMenuElement(runMenu);
         updateMenuElement(optionsMenu);
         updateMenuElement(helpMenu);
         for (MenuCreator creator : menuCreators) {
             creator.localeChanged();
         }
     }
 
     /**
      * Get a list of all installed LAFs plus CrossPlatform and System.
      */
     // This is also used by LookAndFeelCommand
     public static LookAndFeelInfo[] getAllLAFs() {
         UIManager.LookAndFeelInfo lafs[] = UIManager.getInstalledLookAndFeels();
         int i = lafs.length;
         UIManager.LookAndFeelInfo lafsAll[] = new UIManager.LookAndFeelInfo[i+2];
         System.arraycopy(lafs, 0, lafsAll, 0, i);
         lafsAll[i++]=new UIManager.LookAndFeelInfo(CROSS_PLATFORM_LAF,UIManager.getCrossPlatformLookAndFeelClassName());
         lafsAll[i++]=new UIManager.LookAndFeelInfo(SYSTEM_LAF,UIManager.getSystemLookAndFeelClassName());
         return lafsAll;
     }
     /**
      * <p>Refreshes all texts in the menu and all submenus to a new locale.</p>
      *
      * <p>Assumes that the item name is set to the resource key, so the resource can be retrieved.
      * Certain action types do not follow this rule, @see JMeterMenuBar#isNotResource(String)</p>
      *
      * The Language Change event assumes that the name is the same as the locale name,
      * so this additionally means that all supported locales must be defined as resources.
      *
      */
     private void updateMenuElement(MenuElement menu) {
         Component component = menu.getComponent();
         final String compName = component.getName();
         if (compName != null) {
             for (MenuCreator menuCreator : menuCreators) {
                 if(menuCreator.localeChanged(menu)) {
                     return;
                 }
             }
             if (component instanceof JMenu) {
                 final JMenu jMenu = (JMenu) component;
                 if (isResource(jMenu.getActionCommand())){
                     jMenu.setText(JMeterUtils.getResString(compName));
                 }
             } else {
                 final JMenuItem jMenuItem = (JMenuItem) component;
                 if (isResource(jMenuItem.getActionCommand())){
                     jMenuItem.setText(JMeterUtils.getResString(compName));
                 } else if  (ActionNames.CHANGE_LANGUAGE.equals(jMenuItem.getActionCommand())){
                     jMenuItem.setText(JMeterUtils.getLocaleString(compName));
                 }
             }
         }
 
         MenuElement[] subelements = menu.getSubElements();
 
         for (int i = 0; i < subelements.length; i++) {
             updateMenuElement(subelements[i]);
         }
     }
 
     /**
      * Return true if component name is a resource.<br/>
      * i.e it is not a hostname:<br/>
      *
      * <tt>ActionNames.REMOTE_START</tt><br/>
      * <tt>ActionNames.REMOTE_STOP</tt><br/>
      * <tt>ActionNames.REMOTE_EXIT</tt><br/>
      *
      * nor a filename:<br/>
      * <tt>ActionNames.OPEN_RECENT</tt>
      *
      * nor a look and feel prefix:<br/>
      * <tt>ActionNames.LAF_PREFIX</tt>
      */
     private static boolean isResource(String actionCommand) {
         if (ActionNames.CHANGE_LANGUAGE.equals(actionCommand)){//
             return false;
         }
         if (ActionNames.ADD.equals(actionCommand)){//
             return false;
         }
         if (ActionNames.REMOTE_START.equals(actionCommand)){//
             return false;
         }
         if (ActionNames.REMOTE_STOP.equals(actionCommand)){//
             return false;
         }
         if (ActionNames.REMOTE_SHUT.equals(actionCommand)){//
             return false;
         }
         if (ActionNames.REMOTE_EXIT.equals(actionCommand)){//
             return false;
         }
         if (ActionNames.OPEN_RECENT.equals(actionCommand)){//
             return false;
         }
         if (actionCommand != null && actionCommand.startsWith(ActionNames.LAF_PREFIX)){
             return false;
         }
         return true;
     }
 
     /**
      * Make a menu from a resource string.
      * @param resource used to name menu and set text.
      * @return the menu
      */
     private static JMenu makeMenuRes(String resource) {
         JMenu menu = new JMenu(JMeterUtils.getResString(resource));
         menu.setName(resource);
         return menu;
     }
 
     /**
      * Make a menu from a resource string and set its mnemonic.
      *
      * @param resource
      * @param mnemonic
      * @return the menu
      */
     private static JMenu makeMenuRes(String resource, int mnemonic){
         JMenu menu = makeMenuRes(resource);
         menu.setMnemonic(mnemonic);
         return menu;
     }
 
     /**
      * Make a menuItem using a fixed label which is also used as the item name.
      * This is used for items such as recent files and hostnames which are not resources
      * @param label (this is not used as a resource key)
      * @param actionCommand
      * @return the menu item
      */
     private static JMenuItem makeMenuItemNoRes(String label, String actionCommand) {
         JMenuItem menuItem = new JMenuItem(label);
         menuItem.setName(label);
         menuItem.setActionCommand(actionCommand);
         menuItem.addActionListener(ActionRouter.getInstance());
         return menuItem;
     }
 
     private static JMenuItem makeMenuItemRes(String resource, String actionCommand) {
         return makeMenuItemRes(resource, KeyEvent.VK_UNDEFINED, actionCommand, null);
     }
 
     private static JMenuItem makeMenuItemRes(String resource, String actionCommand, KeyStroke keyStroke) {
         return makeMenuItemRes(resource, KeyEvent.VK_UNDEFINED, actionCommand, keyStroke);
     }
 
     private static JMenuItem makeMenuItemRes(String resource, int mnemonic, String actionCommand) {
         return makeMenuItemRes(resource, mnemonic, actionCommand, null);
     }
 
     private static JMenuItem makeMenuItemRes(String resource, int mnemonic, String actionCommand, KeyStroke keyStroke){
         JMenuItem menuItem = new JMenuItem(JMeterUtils.getResString(resource), mnemonic);
         menuItem.setName(resource);
         menuItem.setActionCommand(actionCommand);
         menuItem.setAccelerator(keyStroke);
         menuItem.addActionListener(ActionRouter.getInstance());
         return menuItem;
     }
     
     private static JCheckBoxMenuItem makeCheckBoxMenuItemRes(String resource, String actionCommand) {
         return makeCheckBoxMenuItemRes(resource, actionCommand, null);
     }
 
     private static JCheckBoxMenuItem makeCheckBoxMenuItemRes(String resource, 
             String actionCommand, KeyStroke keyStroke){
         JCheckBoxMenuItem cbkMenuItem = new JCheckBoxMenuItem(JMeterUtils.getResString(resource));
         cbkMenuItem.setName(resource);
         cbkMenuItem.setActionCommand(actionCommand);
         cbkMenuItem.setAccelerator(keyStroke);
         cbkMenuItem.addActionListener(ActionRouter.getInstance());
         return cbkMenuItem;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/JMeterToolBar.java b/src/core/org/apache/jmeter/gui/util/JMeterToolBar.java
index 9e132af92..ba789729c 100644
--- a/src/core/org/apache/jmeter/gui/util/JMeterToolBar.java
+++ b/src/core/org/apache/jmeter/gui/util/JMeterToolBar.java
@@ -1,207 +1,252 @@
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
  * distributed  under the  License is distributed on an "AS IS" BASIS,
  * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
  * implied.
  * 
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.jmeter.gui.util;
 
 import java.awt.Component;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
 import javax.swing.ImageIcon;
 import javax.swing.JButton;
 import javax.swing.JOptionPane;
 import javax.swing.JToolBar;
 
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.LocaleChangeEvent;
 import org.apache.jmeter.util.LocaleChangeListener;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * The JMeter main toolbar class
  *
  */
 public class JMeterToolBar extends JToolBar implements LocaleChangeListener {
     
     /**
      * 
      */
     private static final long serialVersionUID = -4591210341986068907L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String TOOLBAR_ENTRY_SEP = ",";  //$NON-NLS-1$
 
     private static final String TOOLBAR_PROP_NAME = "toolbar"; //$NON-NLS-1$
 
     // protected fields: JMeterToolBar class can be use to create another toolbar (plugin, etc.)    
     protected static final String DEFAULT_TOOLBAR_PROPERTY_FILE = "org/apache/jmeter/images/toolbar/icons-toolbar.properties"; //$NON-NLS-1$
 
     protected static final String USER_DEFINED_TOOLBAR_PROPERTY_FILE = "jmeter.toolbar.icons"; //$NON-NLS-1$
     
     private static final String TOOLBAR_LIST = "jmeter.toolbar";
     
     /**
      * Create the default JMeter toolbar
      * @return the JMeter toolbar
      */
     public static JMeterToolBar createToolbar(boolean visible) {
         JMeterToolBar toolBar = new JMeterToolBar();
         toolBar.setFloatable(false);
         toolBar.setVisible(visible);
 
         setupToolbarContent(toolBar);
         JMeterUtils.addLocaleChangeListener(toolBar);
         // implicit return empty toolbar if icons == null
         return toolBar;
     }
 
     /**
      * Setup toolbar content
      * @param toolBar {@link JMeterToolBar}
      */
     private static void setupToolbarContent(JMeterToolBar toolBar) {
         List<IconToolbarBean> icons = getIconMappings();
         if (icons != null) {
             for (IconToolbarBean iconToolbarBean : icons) {
                 if (iconToolbarBean == null) {
                     toolBar.addSeparator();
                 } else {
                     toolBar.add(makeButtonItemRes(iconToolbarBean));
                 }
             }
-            toolBar.setTestStarted(false);
+            toolBar.initButtonsState();
         }
     }
     
     /**
      * Generate a button component from icon bean
      * @param iconBean contains I18N key, ActionNames, icon path, optional icon path pressed
      * @return a button for toolbar
      */
     private static JButton makeButtonItemRes(IconToolbarBean iconBean) {
         final URL imageURL = JMeterUtils.class.getClassLoader().getResource(iconBean.getIconPath());
         JButton button = new JButton(new ImageIcon(imageURL));
         button.setToolTipText(JMeterUtils.getResString(iconBean.getI18nKey()));
         final URL imageURLPressed = JMeterUtils.class.getClassLoader().getResource(iconBean.getIconPathPressed());
         button.setPressedIcon(new ImageIcon(imageURLPressed));
         button.addActionListener(ActionRouter.getInstance());
         button.setActionCommand(iconBean.getActionNameResolve());
         return button;
     }
     
     /**
      * Parse icon set file.
      * @return List of icons/action definition
      */
     private static List<IconToolbarBean> getIconMappings() {
         // Get the standard toolbar properties
         Properties defaultProps = JMeterUtils.loadProperties(DEFAULT_TOOLBAR_PROPERTY_FILE);
         if (defaultProps == null) {
             JOptionPane.showMessageDialog(null, 
                     JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                     JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                     JOptionPane.WARNING_MESSAGE);
             return null;
         }
         Properties p;
         String userProp = JMeterUtils.getProperty(USER_DEFINED_TOOLBAR_PROPERTY_FILE); 
         if (userProp != null){
             p = JMeterUtils.loadProperties(userProp, defaultProps);
         } else {
             p=defaultProps;
         }
 
         String order = JMeterUtils.getPropDefault(TOOLBAR_LIST, p.getProperty(TOOLBAR_PROP_NAME));
 
         if (order == null) {
             log.warn("Could not find toolbar definition list");
             JOptionPane.showMessageDialog(null, 
                     JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                     JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                     JOptionPane.WARNING_MESSAGE);
             return null;
         }
 
         String[] oList = order.split(TOOLBAR_ENTRY_SEP);
 
         List<IconToolbarBean> listIcons = new ArrayList<IconToolbarBean>();
         for (String key : oList) {
             log.debug("Toolbar icon key: " + key); //$NON-NLS-1$
             String trimmed = key.trim();
             if (trimmed.equals("|")) { //$NON-NLS-1$
                 listIcons.add(null);
             } else {
                 String property = p.getProperty(trimmed);
                 if (property == null) {
                     log.warn("No definition for toolbar entry: " + key);
                 } else {
                     try {
                         IconToolbarBean itb = new IconToolbarBean(property);
                         listIcons.add(itb);
                     } catch (IllegalArgumentException e) {
                         // already reported by IconToolbarBean
                     }
                 }
             }
         }
         return listIcons;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void localeChanged(LocaleChangeEvent event) {
         this.removeAll();
         setupToolbarContent(this);
     }
 
     /**
      * Change state of buttons
      * @param started
      */
-    public void setTestStarted(boolean started) {
+    public void initButtonsState() {
+        final boolean started = false;
         Map<String, Boolean> buttonStates = new HashMap<String, Boolean>();
         buttonStates.put(ActionNames.ACTION_START,Boolean.valueOf(!started));
         buttonStates.put(ActionNames.ACTION_START_NO_TIMERS,Boolean.valueOf(!started));
         buttonStates.put(ActionNames.ACTION_STOP,Boolean.valueOf(started));
         buttonStates.put(ActionNames.ACTION_SHUTDOWN,Boolean.valueOf(started));
         buttonStates.put(ActionNames.REMOTE_START_ALL,Boolean.valueOf(!started));
         buttonStates.put(ActionNames.REMOTE_STOP_ALL,Boolean.valueOf(started));
         buttonStates.put(ActionNames.REMOTE_SHUT_ALL,Boolean.valueOf(started));
         Component[] components = getComponents();
         for (int i = 0; i < components.length; i++) {
             if(components[i]instanceof JButton) {
                 JButton button = (JButton) components[i];
                 Boolean enabled = buttonStates.get(button.getActionCommand());
                 if(enabled != null) {
                     button.setEnabled(enabled.booleanValue());
                 }
             }
         }
     }
+    
+    
+    /**
+     * Change state of buttons on local test
+     * @param started
+     */
+    public void setLocalTestStarted(boolean started) {
+        Map<String, Boolean> buttonStates = new HashMap<String, Boolean>(3);
+        buttonStates.put(ActionNames.ACTION_START,Boolean.valueOf(!started));
+        buttonStates.put(ActionNames.ACTION_START_NO_TIMERS,Boolean.valueOf(!started));
+        buttonStates.put(ActionNames.ACTION_STOP,Boolean.valueOf(started));
+        buttonStates.put(ActionNames.ACTION_SHUTDOWN,Boolean.valueOf(started));
+        Component[] components = getComponents();
+        for (int i = 0; i < components.length; i++) {
+            if(components[i]instanceof JButton) {
+                JButton button = (JButton) components[i];
+                Boolean enabled = buttonStates.get(button.getActionCommand());
+                if(enabled != null) {
+                    button.setEnabled(enabled.booleanValue());
+                }
+            }
+        }
+    }
+    
+    /**
+     * Change state of buttons on remote test
+     * @param started
+     */
+    public void setRemoteTestStarted(boolean started) {
+        Map<String, Boolean> buttonStates = new HashMap<String, Boolean>(3);
+        buttonStates.put(ActionNames.REMOTE_START_ALL,Boolean.valueOf(!started));
+        buttonStates.put(ActionNames.REMOTE_STOP_ALL,Boolean.valueOf(started));
+        buttonStates.put(ActionNames.REMOTE_SHUT_ALL,Boolean.valueOf(started));
+        Component[] components = getComponents();
+        for (int i = 0; i < components.length; i++) {
+            if(components[i]instanceof JButton) {
+                JButton button = (JButton) components[i];
+                Boolean enabled = buttonStates.get(button.getActionCommand());
+                if(enabled != null) {
+                    button.setEnabled(enabled.booleanValue());
+                }
+            }
+        }
+    }
 }
\ No newline at end of file
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 0c5f06031..b4927c07f 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,562 +1,563 @@
 <?xml version="1.0"?> 
 <!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at
  
        http://www.apache.org/licenses/LICENSE-2.0
  
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 -->
 <document>   
 <properties>     
     <author email="dev AT jmeter.apache.org">JMeter developers</author>     
     <title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 <style type="text/css"><!--
 h2 { color: #960000; }
 h3 { color: #960000; }
 --></style>
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 2.10 =================== -->
 
 <h1>Version 2.10</h1>
 
 Summary
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Known bugs">Known bugs</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <ch_category>Core Improvements</ch_category>
 
 <ch_title>New Performance improvements</ch_title>
 <p>
 <ul>
 <li>A Huge performance improvement has been made on High Throughput Tests (no pause), see <bugzilla>54777</bugzilla></li>
 <li>An issue with unnecessary SSL Context reset has been fixed which improves performances of pure HTTP tests, see <bugzilla>55023</bugzilla></li>
 <li>Important performance improvement in parsing of Embedded resource in HTML pages thanks to a switch to JODD/Lagarto HTML Parser, see <bugzilla>55632</bugzilla></li>
 </ul>
 </p>
 
 <ch_title>New CSS/JQuery Tester in View Tree Results</ch_title>
 <p>A new CSS/JQuery Tester in View Tree Results that makes CSS/JQuery Extractor a first class
 citizen in JMeter, you can now test your expressions very easily</p>
 <p>
 <figure width="1144" height="638" image="changes/2.10/01_css_jquery_tester.png"></figure>
 </p>
 
 <ch_title>Many improvements in HTTP(S) Recording have been made</ch_title>
 <p>
 <figure width="691" height="215" image="changes/2.10/18_https_test_script_recorder.png"></figure>
 <note>
 The "HTTP Proxy Server" test element has been renamed as "HTTP(S) Test Script Recorder".
 </note>
 <ul>
 <li>Better recording of HTTPS sites, embedded resources using subdomains will more easily be recorded when using JDK 7. See <bugzilla>55507</bugzilla>.
 See updated documentation: <complink name="HTTP(S) Test Script Recorder"/>
 </li>
 <li>Redirection are now more smartly detected by HTTP Proxy Server, see <bugzilla>55531</bugzilla></li>
 <li>Many fixes on edge cases with HTTPS have been made, see <bugzilla>55502</bugzilla>, <bugzilla>55504</bugzilla>, <bugzilla>55506</bugzilla></li>
 <li>Many encoding fixes have been made, see <bugzilla>54482</bugzilla>, <bugzilla>54142</bugzilla>, <bugzilla>54293</bugzilla></li>
 </ul>
 </p>
 
 <ch_title>You can now load test MongoDB through new MongoDB Source Config</ch_title>
 <p>
 <figure width="912" height="486" image="changes/2.10/02_mongodb_source_config.png"></figure>
 </p>
 <p>
 <figure width="850" height="687" image="changes/2.10/14_mongodb_jsr223.png"></figure>
 </p>
 
 <ch_title>Kerberos authentication has been added to Auth Manager</ch_title>
 <p>
 <figure width="1005" height="364" image="changes/2.10/15_kerberos.png"></figure>
 </p>
 
 <ch_title>Device can now be used in addition to source IP address</ch_title>
 
 <p>
 <figure width="1087" height="699" image="changes/2.10/16_device.png"></figure>
 </p>
 
 <ch_title>You can now do functional testing of MongoDB scripts through new MongoDB Script</ch_title>
 <p>
 <figure width="906" height="313" image="changes/2.10/03_mongodb_script_alpha.png"></figure>
 </p>
 
 <ch_title>Timeout has been added to OS Process Sampler</ch_title>
 <p>
 <figure width="684" height="586" image="changes/2.10/17_os_process_timeout.png"></figure>
 </p>
 
 <ch_title>Query timeout has been added to JDBC Request</ch_title>
 <p>
 <figure width="540" height="600" image="changes/2.10/04_jdbc_request_timeout.png"></figure>
 </p>
 
 <ch_title>New functions (__urlencode and __urldecode) are now available to encode/decode URL encoded chars</ch_title>
 <p>
 <figure width="512" height="240" image="changes/2.10/05_urlencode_function.png"></figure>
 </p>
 
 <ch_title>Continuous Integration is now eased by addition of a new flag that forces NON-GUI JVM to exit after test end</ch_title>
 <p>See jmeter property:</p>
 <code>jmeterengine.force.system.exit</code>
 <p></p>
 
 <ch_title>HttpSampler now allows DELETE Http Method to have a body (works for HC4 and HC31 implementations). This allows for example to test Elastic Search APIs</ch_title>
 <p>
 <figure width="573" height="444" image="changes/2.10/06_http_request_delete_method.png"></figure>
 </p>
 
 <ch_title>2 implementations of HtmlParser have been added to improve Embedded resources parsing</ch_title>
 <p>
 You can choose the implementation to use for parsing Embedded resources in HTML pages:
 See jmeter.properties and look at property "htmlParser.className".
 <ul>
 <li>org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser for optimal performances</li>
 <li>org.apache.jmeter.protocol.http.parser.JSoupBasedHtmlParser for most accurate parsing and functional testing</li>
 </ul>
 </p>
 
 <ch_title>Distributed testing has been improved</ch_title>
 <p>
 <ul>
 <li>
 Number of threads on each node are now reported to controller.
 <p>
 <figure width="988" height="355" image="changes/2.10/17_threads_summariser.png"></figure>
 </p>
 <p>
 <figure width="125" height="33" image="changes/2.10/17_threads_gui.png"></figure>
 </p>
 
 </li>
 <li>Performance improvement on BatchSampleSender(<bugzilla>55423</bugzilla>)</li>
 <li>Addition of 2 SampleSender modes (StrippedAsynch and StrippedDiskStore), see jmeter.properties</li>
 </ul>
 </p>
 
 <ch_title>ModuleController has been improved to better handle changes to referenced controllers</ch_title>
 
 <ch_title>Improved class loader configuration, see <bugzilla>55503</bugzilla></ch_title>
 <p>
 <ul>
 <li>New property "plugin_dependency_paths" for plugin dependencies</li>
 <li>Properties "search_paths", "user.classpath" and "plugin_dependency_paths"
     now automatically add all jars from configured directories</li>
 </ul>
 </p>
 
 <ch_title>Best-practices section has been improved, ensure you read it to get the most out of JMeter</ch_title>
 <p>See <a href="usermanual/best-practices.html">Best Practices</a>
 </p>
 <ch_category>GUI and ergonomy Improvements</ch_category>
 
 
 <ch_title>New Templates feature that allows you to create test plan from existing template or merge
 template into your Test Plan</ch_title>
 <p>
 <figure width="428" height="130" image="changes/2.10/07_jmeter_templates_icon.png"></figure>
 </p>
 <p>
 <figure width="781" height="472" image="changes/2.10/08_jmeter_templates_box.png"></figure>
 </p>
 
 <ch_title>Workbench can now be saved</ch_title>
 <p>
 <figure width="489" height="198" image="changes/2.10/09_save_workbench.png"></figure>
 </p>
 
 <ch_title>Syntax color has been added to scripts elements (BeanShell, BSF, and JSR223), MongoDB and JDBC elements making code much more readable and allowing UNDO/REDO through CTRL+Z/CTRL+Y</ch_title>
 <p>BSF Sampler with syntax color
 <figure width="915" height="620" image="changes/2.10/10_color_syntax_bsf_sampler.png"></figure>
 </p>
 <p>JSR223 Pre Processor with syntax color
 <figure width="911" height="614" image="changes/2.10/11_color_syntax_jsr223_preprocessor.png"></figure>
 </p>
 
 <ch_title>Better editors are now available for Test Elements with large text content, like HTTP Sampler, and JMS related Test Element providing line numbering and allowing UNDO/REDO through CTRL+Z/CTRL+Y</ch_title>
 
 <ch_title>JMeter GUI can now be fully Internationalized, all remaining issues have been fixed</ch_title>
 <h5>Currently French has all its labels translated. Other languages are partly translated, feel free to 
 contribute translations by reading <a href="localising/index.html">Localisation (Translator's Guide)</a></h5>
 
 <ch_title>Moving elements in Test plan has been improved in many ways</ch_title>
 <h5>Drag and drop of elements in Test Plan tree is now much easier and possible on multiple nodes</h5>
 <p>
 <figure width="894" height="236" image="changes/2.10/12_drap_n-drop_multiple.png"></figure>
 </p>
 <p>
 <b>Note that due to this <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6560955">bug in Java</a>,
 you cannot drop a node after last node. The workaround is to drop it before this last node and then Drag and Drop the last node 
 before the one you just dropped.</b>
 </p>
 <h5>New shortcuts have been added to move elements in the tree. </h5>
 <p>(alt + Arrow Up) and (alt + Arrow Down) move the element within the parent node<br/>
 (alt + Arrow Left) and (alt + Arrow Right) move the element up and down in the tree depth</p>
 
 <ch_title>Response Time Graph Y axis can now be scaled</ch_title>
 <p>
 <figure width="947" height="596" image="changes/2.10/13_response_time_graph_y_scale.png"></figure>
 </p>
 
 <ch_title>JUnit Sampler gives now more details on configuration errors</ch_title>
 
 <!--  =================== Known bugs =================== -->
 
 
 <ch_section>Known bugs</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see <bugzilla>52496</bugzilla>).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </li>
 
 <li>Webservice sampler does not consider the HTTP response status to compute the status of a response, thus a response 500 containing a non empty body will be considered as successful, see <bugzilla>54006</bugzilla>.
 To workaround this issue, ensure you always read the response and add a Response Assertion checking text inside the response.
 </li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 these only apply to a locally run test; they do not include any threads started on remote systems when using client-server mode, (see <bugzilla>54152</bugzilla>).
 </li>
 
 <li>
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <pre>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </pre>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
 </li>
 
 <li>
 With Java 1.6 and Gnome 3 on Linux systems, the JMeter menu may not work correctly (shift between mouse's click and the menu). 
 This is a known Java bug (see  <bugzilla>54477 </bugzilla>). 
 A workaround is to use a Java 7 runtime (OpenJDK or Oracle JDK).
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 </ul>
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <p>SMTP Sampler now uses eml file subject if subject field is empty</p>
 
 <p>With this version autoFlush has been turned off on PrintWriter in charge of writing test results. 
 This results in improved throughput for intensive tests but can result in more test data loss in case
 of JMeter crash (extremely rare). To revert to previous behaviour set jmeter.save.saveservice.autoflush property to true. </p>
 
 <p>
 Shortcut for Function Helper Dialog is now CTRL+SHIFT+F1 (CMD + SHIFT + F1 for Mac OS).
 The original key sequence (Ctrl+F1) did not work in some locations (it is consumed by the Java Swing ToolTipManager).
 It was therefore necessary to change the shortcut.
 </p>
 
 <p>
 Webservice (SOAP) Request has been removed by default from GUI as Element is deprecated (use HTTP Sampler with Body Data, see also Template "Building a SOAP Webservice Test Plan"), if you need to show it, see property not_in_menu in jmeter.properties
 </p>
 
 <p>
 Transaction Controller now sets Response Code of Generated Parent Sampler (if Generate Parent Sampler is checked) to response code of first failing child in case of failure of one of the children, in previous versions Response Code was empty.
 </p>
 
 <p>
 In previous versions, IncludeController could run Test Elements located inside a Thread Group, this behaviour (which was not documented) could result in weird behaviour, it has been removed in this version (see <bugzilla>55464</bugzilla>). 
 The correct way to include Test Elements is to use Test Fragment as stated in documentation of Include Controller.
 </p>
 
 <p>
 The retry count for the HttpClient 3.1 and HttpClient 4.x samplers has been changed to 0.
 Previously the default was 1, which could cause unexpected additional traffic.
 </p>
 
 <p>Starting with this version, the HTTP(S) Test Script Recorder tries to detect when a sample is the result of a previous
 redirect. If the current response is a redirect, JMeter will save the redirect URL. When the next request is received, 
 it is compared with the saved redirect URL and if there is a match, JMeter will disable the generated sample.
 To revert to previous behaviour, set the property <code>proxy.redirect.disabling=false</code>
  </p>
 
 <p>Starting with this version, in HTTP(S) Test Script Recorder if Grouping is set to "Put each group in a new Transaction Controller", 
 the Recorder will create Transaction Controller instances with "Include duration of timer and pre-post processors in generated sample" set 
 to false. This default value reflect more accurately response time.
  </p>
 
 <p>__escapeOroRegexpChars function (which escapes ORO reserved characters) no longer trims the value (see <bugzilla>55328</bugzilla>)</p>
 
 <p>The commons-lang-2.6.jar has been removed from embedded libraries in jmeter/lib folder as it is not needed by JMeter at run-time (it is only used by Apache Velocity for generating documentation).
 If you use any plugin or third-party code that depends on it, you need to add it in jmeter/lib folder</p>
 
 <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li><bugzilla>54627</bugzilla> - JMeter Proxy GUI: Type of sampler setting takes the whole screen when there are samplers with long names.</li>
 <li><bugzilla>54629</bugzilla> - HTMLParser does not extract &amp;lt;object&amp;gt; tag urls.</li>
 <li><bugzilla>55023</bugzilla> - SSL Context reuse feature (51380) adversely affects non-ssl request performance/throughput. based on analysis by Brent Cromarty (brent.cromarty at yahoo.ca)</li>
 <li><bugzilla>55092</bugzilla> - Log message "WARN - jmeter.protocol.http.sampler.HTTPSamplerBase: Null URL detected (should not happen)" displayed when embedded resource URL is malformed.</li>
 <li><bugzilla>55161</bugzilla> - Useless processing in SoapSampler.setPostHeaders. Contributed by Adrian Nistor (nistor1 at illinois.edu)</li>
 <li><bugzilla>54482</bugzilla> - HC fails to follow redirects with non-encoded chars.</li>
 <li><bugzilla>54142</bugzilla> - HTTP Proxy Server throws an exception when path contains "|" character.</li>
 <li><bugzilla>55388</bugzilla> - HC3 does not allow IP Source field to override httpclient.localaddress.</li>
 <li><bugzilla>55450</bugzilla> - HEAD redirects should remain as HEAD</li>
 <li><bugzilla>55455</bugzilla> - HTTPS with HTTPClient4 ignores cps setting</li>
 <li><bugzilla>55502</bugzilla> - Proxy generates empty http:/ entries when recording</li>
 <li><bugzilla>55504</bugzilla> - Proxy incorrectly issues CONNECT requests when browser prompts for certificate override</li>
 <li><bugzilla>55506</bugzilla> - Proxy should deliver failed requests to any configured Listeners</li>
 <li><bugzilla>55545</bugzilla> - HTTP Proxy Server GUI should not allow both Follow and Auto redirect to be selected</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>54913</bugzilla> - JMSPublisherGui incorrectly restores its state. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>55027</bugzilla> - Test Action regression, duration value is not recorded (nightly build).</li>
 <li><bugzilla>55163</bugzilla> - BeanShellTestElement fails to quote string when calling testStarted(String)/testEnded(String).</li>
 <li><bugzilla>55349</bugzilla> - NativeCommand hangs if no input file is specified and the application requests input.</li>
 <li><bugzilla>55462</bugzilla> - System Sampler should not change the sampler label if a sample fails</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>54467</bugzilla> - Loop Controller: compute loop value only once per parent iteration.</li>
 <li><bugzilla>54985</bugzilla> - Make Transaction Controller set Response Code of Generated Parent Sampler to response code of first failing child in case of failure of one of its children. Contributed by Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li><bugzilla>54950</bugzilla> - ModuleController : Changes to referenced Module are not taken into account if changes occur after first run and referenced node is disabled.</li>
 <li><bugzilla>55201</bugzilla> - ForEach controller excludes start index and includes end index (clarified documentation).</li>
 <li><bugzilla>55334</bugzilla> - Adding Include Controller to test plan (made of Include Controllers) without saving TestPlan leads to included code not being taken into account until save.</li>
 <li><bugzilla>55375</bugzilla> -  StackOverflowError with ModuleController in Non-GUI mode if its name is the same as the target node.</li>
 <li><bugzilla>55464</bugzilla> - Include Controller running included thread group</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>54589</bugzilla> - View Results Tree have a lot of Garbage characters if html page uses double-byte charset.</li>
 <li><bugzilla>54753</bugzilla> - StringIndexOutOfBoundsException at SampleResult.getSampleLabel() if key_on_threadname=false when using Statistical mode.</li>
 <li><bugzilla>54685</bugzilla> - ArrayIndexOutOfBoundsException if "sample_variable" is set in client but not server.</li>
 <li><bugzilla>55111</bugzilla> - ViewResultsTree: text not refitted if vertical scrollbar is required. Contributed by Milamber</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>54540</bugzilla> - "HTML Parameter Mask" are not marked deprecated in the IHM.</li>
 <li><bugzilla>54575</bugzilla> - CSS/JQuery Extractor : Choosing JODD Implementation always uses JSOUP.</li>
 <li><bugzilla>54901</bugzilla> - Response Assertion GUI behaves weirdly.</li>
 <li><bugzilla>54924</bugzilla> - XMLAssertion uses JMeter JVM file.encoding instead of response encoding and does not clean threadlocal variable.</li>
 <li><bugzilla>53679</bugzilla> -  Constant Throughput Timer bug with localization. Reported by Ludovic Garcia</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bugzilla>55328</bugzilla> - __escapeOroRegexpChars trims spaces.</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li><bugzilla>55437</bugzilla> - ComboStringEditor does not translate EDIT and UNDEFINED strings on language change</li>
 <li><bugzilla>55501</bugzilla> - Incorrect encoding for French description of __char function. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>54504</bugzilla> - Resource string not found: [clipboard_node_read_error].</li>
 <li><bugzilla>54538</bugzilla> - GUI: context menu is too big.</li>
 <li><bugzilla>54847</bugzilla> - Cut &amp; Paste is broken with tree multi-selection. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54870</bugzilla> - Tree drag and drop may lose leaf nodes (affected nightly build). Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>55056</bugzilla> - wasted work in Data.append(). Contributed by Adrian Nistor (nistor1 at illinois.edu)</li>
 <li><bugzilla>55129</bugzilla> -  Change Javadoc generation per CVE-2013-1571, VU#225657.</li>
 <li><bugzilla>55187</bugzilla> - Integer overflow when computing ONE_YEAR_MS in HTTP CacheManager.</li>
 <li><bugzilla>55208</bugzilla> - JSR223 language entries are duplicated; fold to lower case.</li>
 <li><bugzilla>55203</bugzilla> - TestBeanGUI - wrong language settings found.</li>
 <li><bugzilla>55065</bugzilla> - Useless processing in Spline3.converge(). Contributed by Adrian Nistor (nistor1 at illinois.edu)</li>
 <li><bugzilla>55064</bugzilla> - Useless processing in ReportTreeListener.isValidDragAction(). Contributed by Adrian Nistor (nistor1 at illinois.edu)</li>
 <li><bugzilla>55242</bugzilla> - BeanShell Client jar throws exceptions after upgrading to 2.8.</li>
 <li><bugzilla>55288</bugzilla> - JMeter should default to 0 retries for HTTP requests.</li>
 <li><bugzilla>55405</bugzilla> - ant download_jars task fails if lib/api or lib/doc are missing. Contributed by Antonio Gomes Rodrigues.</li>
 <li><bugzilla>55427</bugzilla> - TestBeanHelper should ignore properties not supported by GenericTestBeanCustomizer</li>
 <li><bugzilla>55459</bugzilla> - Elements using ComboStringEditor lose the input value if user selects another Test Element</li>
 <li><bugzilla>54152</bugzilla> - In distributed testing : activeThreads always show 0 in GUI and Summariser</li>
 <li><bugzilla>55509</bugzilla> - Allow Plugins to be notified of remote thread number progression</li>
 <li><bugzilla>55572</bugzilla> - Detail popup of parameter does not show a Scrollbar when content exceeds display</li>
 <li><bugzilla>55580</bugzilla> -  Help pane does not scroll to start for &lt;a href="#"&gt; links</li>
 <li><bugzilla>55600</bugzilla> - JSyntaxTextArea : Strange behaviour on first undo</li>
 <li><bugzilla>55655</bugzilla> - NullPointerException when Remote stopping /shutdown all if one engine did not start correctly. Contributed by UBIK Load Pack (support at ubikloadpack.com)</li>
+<li><bugzilla>55657</bugzilla> - Remote and Local Stop/Shutdown buttons state does not take into account local / remote status </li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>HTTP Request: Small user interaction improvements in Row parameter Detail Box. Contributed by Milamber</li>
 <li><bugzilla>55255</bugzilla> - Allow Body in HTTP DELETE method to support API that use it (like ElasticSearch).</li>
 <li><bugzilla>53480</bugzilla> - Add Kerberos support to Http Sampler (HttpClient4). Based on patch by Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li><bugzilla>54874</bugzilla> - Support device in addition to source IP address. Based on patch by Dan Fruehauf (malkodan at gmail.com)</li>
 <li><bugzilla>55488</bugzilla> - Add .ico and .woff file extension to default suggested exclusions in proxy recorder. Contributed by Antonio Gomes Rodrigues</li>
 <li><bugzilla>55525</bugzilla> - Proxy should support alias for keyserver entry</li>
 <li><bugzilla>55531</bugzilla> - Proxy recording and redirects. Added code to disable redirected samples.</li>
 <li><bugzilla>55507</bugzilla> - Proxy SSL recording does not handle external embedded resources well</li>
 <li><bugzilla>55632</bugzilla> - Have a new implementation of htmlParser for embedded resources parsing with better performances</li>
 <li><bugzilla>55653</bugzilla> - HTTP(S) Test Script Recorder should set TransactionController property "Include duration of timer and pre-post processors in generated sample" to false</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li><bugzilla>54788</bugzilla> - JMS Point-to-Point Sampler - GUI enhancements to increase readability and ease of use. Contributed by Bruno Antunes (b.m.antunes at gmail.com)</li>
 <li><bugzilla>54798</bugzilla> - Using subject from EML-file for SMTP Sampler. Contributed by Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li><bugzilla>54759</bugzilla> - SSLPeerUnverifiedException using HTTPS , property documented.</li>
 <li><bugzilla>54896</bugzilla> - JUnit sampler gives only "failed to create an instance of the class" message with constructor problems.</li>
 <li><bugzilla>55084</bugzilla> - Add timeout support for JDBC Request. Contributed by Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li><bugzilla>55403</bugzilla> - Enhancement to OS sampler: Support for timeout</li>
 <li><bugzilla>55518</bugzilla> - Add ability to limit number of cached PreparedStatements per connection when "Prepared Select Statement", "Prepared Update Statement" or "Callable Statement" query type is selected</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>54271</bugzilla> - Module Controller breaks if test plan is renamed.</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>54532</bugzilla> - Improve Response Time Graph Y axis scale with huge values or small values (&amp;lt; 1000ms). Add a new field to define increment scale. Contributed by Milamber based on patch by Luca Maragnani (luca.maragnani at gmail.com)</li>
 <li><bugzilla>54576</bugzilla> - View Results Tree : Add a CSS/JQuery Tester.</li>
 <li><bugzilla>54777</bugzilla> - Improve Performance of default ResultCollector. Based on patch by Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li><bugzilla>55389</bugzilla> - Show IP source address in request data</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>54789</bugzilla> - XPath Assertion - GUI enhancements to increase readability and ease of use.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bugzilla>54991</bugzilla> - Add functions to encode/decode URL encoded chars (__urlencode and __urldecode). Contributed by Milamber.</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li><bugzilla>55241</bugzilla> - Need GUI Editor to process fields which are based on Enums with localised display strings</li>
 <li><bugzilla>55440</bugzilla> - ComboStringEditor should allow tags to be language dependent</li>
 <li><bugzilla>55432</bugzilla> - CSV Dataset Config loses sharing mode when switching languages</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>54584</bugzilla> - MongoDB plugin. Based on patch by Jan Paul Ettles (janpaulettles at gmail.com)</li>
 <li><bugzilla>54669</bugzilla> - Add flag forcing non-GUI JVM to exit after test. Contributed by Scott Emmons</li>
 <li><bugzilla>42428</bugzilla> - Workbench not saved with Test Plan. Contributed by Dzmitry Kashlach (dzmitrykashlach at gmail.com)</li>
 <li><bugzilla>54825</bugzilla> - Add shortcuts to move elements in the tree. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54834</bugzilla> - Improve Drag &amp; Drop in the jmeter tree. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54839</bugzilla> - Set the application name on Mac. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54841</bugzilla> - Correctly handle the quit shortcut on Mac Os (CMD-Q). Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54844</bugzilla> - Set the application icon on Mac Os. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54864</bugzilla> - Enable multi selection drag &amp; drop in the tree without having to start dragging before releasing Shift or Control. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54945</bugzilla> - Add Shutdown Hook to enable trapping kill or CTRL+C signals.</li>
 <li><bugzilla>54990</bugzilla> - Download large files avoiding outOfMemory.</li>
 <li><bugzilla>55085</bugzilla> - UX Improvement : Ability to create New Test Plan from Templates. Contributed by UBIK Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>55172</bugzilla> - Provide plugins a way to add Top Menu and menu items.</li>
 <li><bugzilla>55202</bugzilla> - Add syntax color for scripts elements (BeanShell, BSF, and JSR223) and JDBC elements with RSyntaxTextArea. Contributed by Milamber based on patch by Marko Vlahovic (vlahovic74 at gmail.com)</li>
 <li><bugzilla>55175</bugzilla> - HTTPHC4Impl refactoring to allow better inheritance.</li>
 <li><bugzilla>55236</bugzilla> - Templates - provide button to reload template details.</li>
 <li><bugzilla>55237</bugzilla> - Template system should support relative fileName entries.</li>
 <li><bugzilla>55423</bugzilla> - BatchSampleSender: Reduce locking granularity by moving listener.processBatch outside of synchronized block</li>
 <li><bugzilla>55424</bugzilla> - Add Stripping to existing SampleSenders</li>
 <li><bugzilla>55451</bugzilla> - Test Element GUI with JSyntaxTextArea scroll down when text content is long enough to add a Scrollbar</li>
 <li><bugzilla>55513</bugzilla> - StreamCopier cannot be used with System.err or System.out as it closes the output stream</li>
 <li><bugzilla>55514</bugzilla> - SystemCommand should support arbitrary input and output streams</li>
 <li><bugzilla>55515</bugzilla> - SystemCommand should support chaining of commands</li>
 <li><bugzilla>55606</bugzilla> - Use JSyntaxtTextArea for Http Request, JMS Test Elements</li>
 <li><bugzilla>55651</bugzilla> - Change JMeter application icon to Apache plume icon</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to jsoup-1.7.2</li>
 <li><bugzilla>54776</bugzilla> - Update the dependency on Bouncy Castle to 1.48. Contributed by Emmanuel Bourg (ebourg at apache.org)</li>
 <li>Updated to HttpComponents Client 4.2.6 (from 4.2.3)</li>
 <li>Updated to HttpComponents Core 4.2.5 (from 4.2.3)</li>
 <li>Updated to commons-codec 1.8 (from 1.6)</li>
 <li>Updated to commons-io 2.4 (from 2.2)</li>
 <li>Updated to commons-logging 1.1.3 (from 1.1.1)</li>
 <li>Updated to commons-net 3.3 (from 3.1)</li>
 <li>Updated to jdom-1.1.3 (from 1.1.2)</li>
 <li>Updated to jodd-lagarto and jodd-core 3.4.8 (from 3.4.1)</li>
 <li>Updated to junit 4.11 (from 4.10)</li>
 <li>Updated to slf4j-api 1.7.5 (from 1.7.2)</li>
 <li>Updated to tika 1.4 (from 1.3)</li>
 <li>Updated to xmlgraphics-commons 1.5 (from 1.3.1)</li>
 <li>Updated to xstream 1.4.4 (from 1.4.2)</li>
 <li>Updated to BouncyCastle 1.49 (from 1.48)</li>
 <li><bugzilla>54912</bugzilla> - JMeterTreeListener should use constants. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54903</bugzilla> - Remove the dependency on the Activation Framework. Contributed by Emmanuel Bourg (ebourg at apache.org)</li>
 <li>Moved commons-lang (2.6) to lib/doc as it's only needed by Velocity.</li>
 <li>Re-organised and simplified NOTICE and LICENSE files.</li>
 <li><bugzilla>55411</bugzilla> -  NativeCommand could be useful elsewhere. Copied code to o.a.jorphan.exec.</li>
 <li><bugzilla>55435</bugzilla> - ComboStringEditor could be simplified to make most settings final</li>
 <li><bugzilla>55436</bugzilla> - ComboStringEditor should implement ClearGui</li>
 <li><bugzilla>55463</bugzilla> - Component.requestFocus() is discouraged; use requestFocusInWindow() instead</li>
 <li><bugzilla>55486</bugzilla> - New JMeter Logo. Contributed by UBIK Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>55548</bugzilla> - Tidy up use of TestElement.ENABLED; use TestElement.isEnabled()/setEnabled() throughout</li>
 <li><bugzilla>55617</bugzilla> - Improvements to jorphan collection. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>55623</bugzilla> - Invalid/unexpected configuration values should not be silently ignored</li>
 <li><bugzilla>55626</bugzilla> - Rename HTTP Proxy Server as HTTP(S) Test Script Recorder</li>
 </ul>
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above.<br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 <li>Immanuel Hayden (immanuel.hayden at gmail.com)</li>
 <li>Danny Lade (dlade at web.de)</li>
 <li>Brent Cromarty (brent.cromarty at yahoo.ca)</li>
 <li>Wolfgang Heider (wolfgang.heider at racon.at)</li>
 <li>Shmuel Krakower (shmulikk at gmail.com)</li>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
 </section> 
 </body> 
 </document>
