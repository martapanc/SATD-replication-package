diff --git a/src/core/org/apache/jmeter/gui/AbstractJMeterGuiComponent.java b/src/core/org/apache/jmeter/gui/AbstractJMeterGuiComponent.java
index eefa21441..b29944916 100644
--- a/src/core/org/apache/jmeter/gui/AbstractJMeterGuiComponent.java
+++ b/src/core/org/apache/jmeter/gui/AbstractJMeterGuiComponent.java
@@ -1,341 +1,341 @@
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
 
 import java.awt.Component;
 import java.awt.Container;
 import java.awt.Font;
 import java.util.Locale;
 
 import javax.swing.BorderFactory;
 import javax.swing.JComponent;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.border.Border;
 
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.Printable;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * This abstract class takes care of the most basic functions necessary to
  * create a viable JMeter GUI component. It extends JPanel and implements
  * JMeterGUIComponent. This abstract class is, in turn, extended by several
  * other abstract classes that create different classes of GUI components for
  * JMeter (Visualizers, Timers, Samplers, Modifiers, Controllers, etc).
  *
  * @see org.apache.jmeter.gui.JMeterGUIComponent
  * @see org.apache.jmeter.config.gui.AbstractConfigGui
  * @see org.apache.jmeter.assertions.gui.AbstractAssertionGui
  * @see org.apache.jmeter.control.gui.AbstractControllerGui
  * @see org.apache.jmeter.timers.gui.AbstractTimerGui
  * @see org.apache.jmeter.visualizers.gui.AbstractVisualizer
  * @see org.apache.jmeter.samplers.gui.AbstractSamplerGui
  *
  */
 public abstract class AbstractJMeterGuiComponent extends JPanel implements JMeterGUIComponent, Printable {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     /** Logging */
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AbstractJMeterGuiComponent.class);
 
     /** Flag indicating whether or not this component is enabled. */
     private boolean enabled = true;
 
     /** A GUI panel containing the name of this component. */
     protected NamePanel namePanel;
     // used by AbstractReportGui
 
     private final CommentPanel commentPanel;
 
     /**
      * When constructing a new component, this takes care of basic tasks like
      * setting up the Name Panel and assigning the class's static label as the
      * name to start.
      */
     public AbstractJMeterGuiComponent() {
         namePanel = new NamePanel();
         commentPanel=new CommentPanel();
         initGui();
     }
 
     /**
      * Provides a default implementation for setting the name property. It's unlikely
      * developers will need to override.
      */
     @Override
     public void setName(String name) {
         namePanel.setName(name);
     }
 
     /**
      * Provides a default implementation for setting the comment property. It's
      * unlikely developers will need to override.
      * 
      * @param comment
      *            The comment for the property
      */
     public void setComment(String comment) {
         commentPanel.setText(comment);
     }
 
     /**
      * Provides a default implementation for the enabled property. It's unlikely
      * developers will need to override.
      */
     @Override
     public boolean isEnabled() {
         return enabled;
     }
 
     /**
      * Provides a default implementation for the enabled property. It's unlikely
      * developers will need to override.
      */
     @Override
-    public void setEnabled(boolean e) {
-        log.debug("Setting enabled: " + e);
-        enabled = e;
+    public void setEnabled(boolean enabled) {
+        log.debug("Setting enabled: {}", enabled);
+        this.enabled = enabled;
     }
 
     /**
      * Provides a default implementation for the name property. It's unlikely
      * developers will need to override.
      */
     @Override
     public String getName() {
         if (getNamePanel() != null) {
             return getNamePanel().getName();
         }
         return ""; // $NON-NLS-1$
     }
 
     /**
      * Provides a default implementation for the comment property. It's unlikely
      * developers will need to override.
      * 
      * @return The comment for the property
      */
     public String getComment() {
         if (getCommentPanel() != null) {
             return getCommentPanel().getText();
         }
         return ""; // $NON-NLS-1$
     }
 
     /**
      * Provides the Name Panel for extending classes. Extending classes are free
      * to place it as desired within the component, or not at all. Most
      * components place the NamePanel automatically by calling
      * {@link #makeTitlePanel()} instead of directly calling this method.
      *
      * @return a NamePanel containing the name of this component
      */
     protected NamePanel getNamePanel() {
         return namePanel;
     }
 
     private CommentPanel getCommentPanel(){
         return commentPanel;
     }
     /**
      * Provides a label containing the title for the component. Subclasses
      * typically place this label at the top of their GUI. The title is set to
      * the name returned from the component's
      * {@link JMeterGUIComponent#getStaticLabel() getStaticLabel()} method. Most
      * components place this label automatically by calling
      * {@link #makeTitlePanel()} instead of directly calling this method.
      *
      * @return a JLabel which subclasses can add to their GUI
      */
     protected Component createTitleLabel() {
         JLabel titleLabel = new JLabel(getStaticLabel());
         Font curFont = titleLabel.getFont();
         titleLabel.setFont(curFont.deriveFont((float) curFont.getSize() + 4));
         return titleLabel;
     }
 
     /**
      * A newly created gui component can be initialized with the contents of a
      * Test Element object by calling this method. The component is responsible
      * for querying the Test Element object for the relevant information to
      * display in its GUI.
      * <p>
      * AbstractJMeterGuiComponent provides a partial implementation of this
      * method, setting the name of the component and its enabled status.
      * Subclasses should override this method, performing their own
      * configuration as needed, but also calling this super-implementation.
      *
      * @param element
      *            the TestElement to configure
      */
     @Override
     public void configure(TestElement element) {
         setName(element.getName());
         enabled = element.isEnabled();
         getCommentPanel().setText(element.getComment());
     }
 
     /**
      * Provides a default implementation that resets the name field to the value of
      * getStaticLabel(), reset comment and sets enabled to true. Your GUI may need more things
      * cleared, in which case you should override, clear the extra fields, and
      * still call super.clearGui().
      */
     @Override
     public void clearGui() {
         initGui();
         enabled = true;
     }
 
     // helper method - also used by constructor
     private void initGui() {
         setName(getStaticLabel());
         commentPanel.clearGui();
     }
 
     /**
      * This provides a convenience for extenders when they implement the
      * {@link JMeterGUIComponent#modifyTestElement(TestElement)} method. This
      * method will set the name, gui class, and test class for the created Test
      * Element. It should be called by every extending class when
      * creating/modifying Test Elements, as that will best assure consistent
      * behavior.
      *
      * @param mc
      *            the TestElement being created.
      */
     protected void configureTestElement(TestElement mc) {
         mc.setName(getName());
 
         mc.setProperty(new StringProperty(TestElement.GUI_CLASS, this.getClass().getName()));
 
         mc.setProperty(new StringProperty(TestElement.TEST_CLASS, mc.getClass().getName()));
 
         // This stores the state of the TestElement
-        log.debug("setting element to enabled: " + enabled);
+        log.debug("setting element to enabled: {}", enabled);
         mc.setEnabled(enabled);
         mc.setComment(getComment());
     }
 
     /**
      * Create a standard title section for JMeter components. This includes the
      * title for the component and the Name Panel allowing the user to change
      * the name for the component. This method is typically added to the top of
      * the component at the beginning of the component's init method.
      *
      * @return a panel containing the component title and name panel
      */
     protected Container makeTitlePanel() {
         VerticalPanel titlePanel = new VerticalPanel();
         titlePanel.add(createTitleLabel());
         VerticalPanel contentPanel = new VerticalPanel();
         contentPanel.setBorder(BorderFactory.createEtchedBorder());
         contentPanel.add(getNamePanel());
         contentPanel.add(getCommentPanel());
         titlePanel.add(contentPanel);
         return titlePanel;
     }
 
     /**
      * Create a top-level Border which can be added to JMeter components.
      * Components typically set this as their border in their init method. It
      * simply provides a nice spacing between the GUI components used and the
      * edges of the window in which they appear.
      *
      * @return a Border for JMeter components
      */
     protected Border makeBorder() {
         return BorderFactory.createEmptyBorder(10, 10, 5, 10);
     }
 
     /**
      * Create a scroll panel that sets it's preferred size to it's minimum size.
      * Explicitly for scroll panes that live inside other scroll panes, or
      * within containers that stretch components to fill the area they exist in.
      * Use this for any component you would put in a scroll pane (such as
      * TextAreas, tables, JLists, etc). It is here for convenience and to avoid
      * duplicate code. JMeter displays best if you follow this custom.
      *
      * @param comp
      *            the component which should be placed inside the scroll pane
      * @return a JScrollPane containing the specified component
      */
     protected JScrollPane makeScrollPane(Component comp) {
         JScrollPane pane = new JScrollPane(comp);
         pane.setPreferredSize(pane.getMinimumSize());
         return pane;
     }
 
     /**
      * Create a scroll panel that sets it's preferred size to it's minimum size.
      * Explicitly for scroll panes that live inside other scroll panes, or
      * within containers that stretch components to fill the area they exist in.
      * Use this for any component you would put in a scroll pane (such as
      * TextAreas, tables, JLists, etc). It is here for convenience and to avoid
      * duplicate code. JMeter displays best if you follow this custom.
      *
      * @see javax.swing.ScrollPaneConstants
      *
      * @param comp
      *            the component which should be placed inside the scroll pane
      * @param verticalPolicy
      *            the vertical scroll policy
      * @param horizontalPolicy
      *            the horizontal scroll policy
      * @return a JScrollPane containing the specified component
      */
     protected JScrollPane makeScrollPane(Component comp, int verticalPolicy, int horizontalPolicy) {
         JScrollPane pane = new JScrollPane(comp, verticalPolicy, horizontalPolicy);
         pane.setPreferredSize(pane.getMinimumSize());
         return pane;
     }
 
     @Override
     public String getStaticLabel() {
         return JMeterUtils.getResString(getLabelResource());
     }
 
     /**
      * Compute Anchor value to find reference in documentation for a particular component
      * @return String anchor
      */
     @Override
     public String getDocAnchor() {
         // Ensure we use default bundle
         String label =  JMeterUtils.getResString(getLabelResource(), new Locale("",""));
         return label.replace(' ', '_');
     }
 
     /**
      * Subclasses need to over-ride this method, if they wish to return
      * something other than the Visualizer itself.
      *
      * @return this object
      */
     @Override
     public JComponent getPrintableComponent() {
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/MainFrame.java b/src/core/org/apache/jmeter/gui/MainFrame.java
index 3dda8f848..adc20ae26 100644
--- a/src/core/org/apache/jmeter/gui/MainFrame.java
+++ b/src/core/org/apache/jmeter/gui/MainFrame.java
@@ -1,904 +1,908 @@
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
 import java.awt.Color;
 import java.awt.Component;
 import java.awt.Cursor;
 import java.awt.Dimension;
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
 
 import javax.swing.AbstractAction;
 import javax.swing.Action;
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.DropMode;
 import javax.swing.ImageIcon;
 import javax.swing.InputMap;
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
 import javax.swing.KeyStroke;
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
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jmeter.gui.action.LoadDraggedFile;
 import org.apache.jmeter.gui.logging.GuiLogEventListener;
 import org.apache.jmeter.gui.logging.LogEventObject;
 import org.apache.jmeter.gui.tree.JMeterCellRenderer;
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.tree.JMeterTreeTransferHandler;
 import org.apache.jmeter.gui.util.EscapeDialog;
 import org.apache.jmeter.gui.util.JMeterMenuBar;
 import org.apache.jmeter.gui.util.JMeterToolBar;
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.Remoteable;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ComponentUtil;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * The main JMeter frame, containing the menu bar, test tree, and an area for
  * JMeter component GUIs.
  *
  */
 public class MainFrame extends JFrame implements TestStateListener, Remoteable, DropTargetListener, Clearable, ActionListener {
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     // This is used to keep track of local (non-remote) tests
     // The name is chosen to be an unlikely host-name
     public static final String LOCAL = "*local*"; // $NON-NLS-1$
 
     // The application name
     private static final String DEFAULT_APP_NAME = "Apache JMeter"; // $NON-NLS-1$
 
     // The default title for the Menu bar
     private static final String DEFAULT_TITLE = DEFAULT_APP_NAME +
             " (" + JMeterUtils.getJMeterVersion() + ")"; // $NON-NLS-1$ $NON-NLS-2$
 
     // Allow display/hide LoggerPanel
     private static final boolean DISPLAY_LOGGER_PANEL =
             JMeterUtils.getPropDefault("jmeter.loggerpanel.display", false); // $NON-NLS-1$
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
 
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
 
     private final String iconSize = JMeterUtils.getPropDefault(JMeterToolBar.TOOLBAR_ICON_SIZE, JMeterToolBar.DEFAULT_TOOLBAR_ICON_SIZE); 
 
     /** An image which is displayed when a test is running. */
     private final ImageIcon runningIcon = JMeterUtils.getImage("status/" + iconSize +"/user-online-2.png");// $NON-NLS-1$
 
     /** An image which is displayed when a test is not currently running. */
     private final ImageIcon stoppedIcon = JMeterUtils.getImage("status/" + iconSize +"/user-offline-2.png");// $NON-NLS-1$
     
     /** An image which is displayed to indicate FATAL, ERROR or WARNING. */
     private final ImageIcon warningIcon = JMeterUtils.getImage("status/" + iconSize +"/pictogram-din-w000-general.png");// $NON-NLS-1$
 
     /** The button used to display the running/stopped image. */
     private JButton runningIndicator;
 
     /** The set of currently running hosts. */
     private final Set<String> hosts = new HashSet<>();
 
     /** A message dialog shown while JMeter threads are stopping. */
     private JDialog stoppingMessage;
 
     private JLabel totalThreads;
     private JLabel activeThreads;
 
     private JMeterToolBar toolbar;
 
     /**
      * Label at top right showing test duration
      */
     private JLabel testTimeDuration;
 
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
     
     private javax.swing.Timer computeTestDurationTimer = new javax.swing.Timer(1000, 
             this::computeTestDuration);
 
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
         runningIndicator.setFocusable(false);
         runningIndicator.setBorderPainted(false);
         runningIndicator.setContentAreaFilled(false);
         runningIndicator.setMargin(new Insets(0, 0, 0, 0));
 
         testTimeDuration = new JLabel("00:00:00"); //$NON-NLS-1$
         testTimeDuration.setToolTipText(JMeterUtils.getResString("duration_tooltip")); //$NON-NLS-1$
         testTimeDuration.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
 
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
         warnIndicator.setCursor(new Cursor(Cursor.HAND_CURSOR));
         warnIndicator.setToolTipText(JMeterUtils.getResString("error_indicator_tooltip")); // $NON-NLS-1$
         warnIndicator.addActionListener(this);
 
         errorsOrFatalsLabel = new JLabel("0"); // $NON-NLS-1$
         errorsOrFatalsLabel.setToolTipText(JMeterUtils.getResString("error_indicator_tooltip")); // $NON-NLS-1$
 
         tree = makeTree(treeModel, treeListener);
 
         GuiPackage.getInstance().setMainFrame(this);
         init();
         initTopLevelDndHandler();
         setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
         
         addMouseWheelListener(e -> {
             if (e.isControlDown()) {
                 final float scale = 1.1f;
                 int rotation = e.getWheelRotation();
                 if (rotation > 0) { // DOWN
                     JMeterUtils.applyScaleOnFonts(1.0f/scale);
                 } else if (rotation < 0) { // UP
                     JMeterUtils.applyScaleOnFonts(scale);
                 }
                 e.consume();
             }
         });
     }
 
     protected void computeTestDuration(ActionEvent evt) {
         long startTime = JMeterContextService.getTestStartTime();
         if (startTime > 0) {
             long elapsedSec = (System.currentTimeMillis()-startTime + 500) / 1000; // rounded seconds
             testTimeDuration.setText(JOrphanUtils.formatDuration(elapsedSec));
         }
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
                 for (MenuElement element : menuElement) {
                     JMenu menu = (JMenu) element;
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
         if (!StringUtils.isEmpty(host)) {
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
                 if (stoppingMessage != null) { // TODO - how can this be null?
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
         computeTestDurationTimer.start();
         runningIndicator.setIcon(runningIcon);
         activeThreads.setText("0"); // $NON-NLS-1$
         totalThreads.setText("0"); // $NON-NLS-1$
         menuBar.setRunning(true, host);
         if (LOCAL.equals(host)) {
             toolbar.setLocalTestStarted(true);
         } else {
             toolbar.setRemoteTestStarted(true);
         }
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
             computeTestDurationTimer.stop();
         }
         menuBar.setRunning(false, host);
         if (LOCAL.equals(host)) {
             toolbar.setLocalTestStarted(false);
         } else {
             toolbar.setRemoteTestStarted(false);
         }
         if (stoppingMessage != null) {
             stoppingMessage.dispose();
             stoppingMessage = null;
         }
     }
 
     /**
      * Create the GUI components and layout.
      */
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
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
         errorsAndFatalsCounterLogTarget = new ErrorsAndFatalsCounterLogTarget();
         GuiPackage.getInstance().getLogEventBus().registerEventListener(logPanel);
         GuiPackage.getInstance().getLogEventBus().registerEventListener(errorsAndFatalsCounterLogTarget);
 
         topAndDown.setTopComponent(mainPanel);
         topAndDown.setBottomComponent(logPanel);
 
         treeAndMain.setRightComponent(topAndDown);
 
         treeAndMain.setResizeWeight(.2);
         treeAndMain.setContinuousLayout(true);
         all.add(treeAndMain, BorderLayout.CENTER);
 
         getContentPane().add(all);
 
         tree.setSelectionRow(1);
         addWindowListener(new WindowHappenings());
         // Building is complete, register as listener
         GuiPackage.getInstance().registerAsListener();
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
         this.toolbar = JMeterToolBar.createToolbar(true);
         GuiPackage guiInstance = GuiPackage.getInstance();
         guiInstance.setMainToolbar(toolbar);
         toolPanel.add(toolbar);
 
         toolPanel.add(Box.createRigidArea(new Dimension(10, 15)));
         toolPanel.add(Box.createGlue());
 
         toolPanel.add(testTimeDuration);
         toolPanel.add(Box.createRigidArea(new Dimension(20, 15)));
 
         toolPanel.add(errorsOrFatalsLabel);
         toolPanel.add(warnIndicator);
         toolPanel.add(Box.createRigidArea(new Dimension(20, 15)));
 
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
 
         addQuickComponentHotkeys(treevar);
 
         return treevar;
     }
 
     private void addQuickComponentHotkeys(JTree treevar) {
         Action quickComponent = new AbstractAction("Quick Component") {
             private static final long serialVersionUID = 1L;
 
             @Override
             public void actionPerformed(ActionEvent actionEvent) {
                 String propname = "gui.quick_" + actionEvent.getActionCommand();
                 String comp = JMeterUtils.getProperty(propname);
-                log.debug("Event " + propname + ": " + comp);
+                log.debug("Event {}: {}", propname, comp);
 
                 if (comp == null) {
-                    log.warn("No component set through property: " + propname);
+                    log.warn("No component set through property: {}", propname);
                     return;
                 }
 
                 GuiPackage guiPackage = GuiPackage.getInstance();
                 try {
                     guiPackage.updateCurrentNode();
                     TestElement testElement = guiPackage.createTestElement(SaveService.aliasToClass(comp));
                     JMeterTreeNode parentNode = guiPackage.getCurrentNode();
                     while (!MenuFactory.canAddTo(parentNode, testElement)) {
                         parentNode = (JMeterTreeNode) parentNode.getParent();
                     }
                     if (parentNode.getParent() == null) {
                         log.debug("Cannot add element on very top level");
                     } else {
                         JMeterTreeNode node = guiPackage.getTreeModel().addComponent(testElement, parentNode);
                         guiPackage.getMainFrame().getTree().setSelectionPath(new TreePath(node.getPath()));
                     }
                 } catch (Exception err) {
-                    log.warn("Failed to perform quick component add: " + comp, err); // $NON-NLS-1$
+                    log.warn("Failed to perform quick component add: {}", comp, err); // $NON-NLS-1$
                 }
             }
         };
 
         InputMap inputMap = treevar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
         KeyStroke[] keyStrokes = new KeyStroke[]{KeyStrokes.CTRL_0,
                 KeyStrokes.CTRL_1, KeyStrokes.CTRL_2, KeyStrokes.CTRL_3,
                 KeyStrokes.CTRL_4, KeyStrokes.CTRL_5, KeyStrokes.CTRL_6,
                 KeyStrokes.CTRL_7, KeyStrokes.CTRL_8, KeyStrokes.CTRL_9,};
         for (int n = 0; n < keyStrokes.length; n++) {
             treevar.getActionMap().put(ActionNames.QUICK_COMPONENT + String.valueOf(n), quickComponent);
             inputMap.put(keyStrokes[n], ActionNames.QUICK_COMPONENT + String.valueOf(n));
         }
     }
 
     /**
      * Create the tree cell renderer used to draw the nodes in the test tree.
      *
      * @return a renderer to draw the test tree nodes
      */
     private TreeCellRenderer getCellRenderer() {
         DefaultTreeCellRenderer rend = new JMeterCellRenderer();
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
             for (DataFlavor flavor : flavors) {
                 // Check for file lists specifically
                 if (flavor.isFlavorJavaFileListType()) {
                     dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                     try {
                         openJmxFilesFromDragAndDrop(tr);
                     } finally {
                         dtde.dropComplete(true);
                     }
                     return;
                 }
             }
         } catch (UnsupportedFlavorException | IOException e) {
-            log.warn("Dnd failed" , e);
+            log.warn("Dnd failed", e);
         }
 
     }
 
     public boolean openJmxFilesFromDragAndDrop(Transferable tr) throws UnsupportedFlavorException, IOException {
         @SuppressWarnings("unchecked")
         List<File> files = (List<File>)
                 tr.getTransferData(DataFlavor.javaFileListFlavor);
         if (files.isEmpty()) {
             return false;
         }
         File file = files.get(0);
         if (!file.getName().endsWith(".jmx")) {
-            log.warn("Importing file:" + file.getName()+ "from DnD failed because file extension does not end with .jmx");
+            if (log.isWarnEnabled()) {
+                log.warn("Importing file, {}, from DnD failed because file extension does not end with .jmx", file.getName());
+            }
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
      * ErrorsAndFatalsCounterLogTarget.
      */
     public final class ErrorsAndFatalsCounterLogTarget implements GuiLogEventListener, Clearable {
         public AtomicInteger errorOrFatal = new AtomicInteger(0);
 
         @Override
         public void processLogEvent(LogEventObject logEventObject) {
             if (logEventObject.isMoreSpecificThanError()) {
                 final int newValue = errorOrFatal.incrementAndGet();
                 SwingUtilities.invokeLater(new Runnable() {
                     @Override
                     public void run() {
                         errorsOrFatalsLabel.setForeground(Color.RED);
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
                     errorsOrFatalsLabel.setForeground(Color.BLACK);
                     errorsOrFatalsLabel.setText(Integer.toString(errorOrFatal.get()));
                 }
             });
         }
 
     }
 
 
     @Override
     public void clearData() {
         logPanel.clear();
         errorsAndFatalsCounterLogTarget.clearData();
     }
 
     /**
      * Handles click on warnIndicator
      */
     @Override
     public void actionPerformed(ActionEvent event) {
         if (event.getSource() == warnIndicator) {
             ActionRouter.getInstance().doActionNow(new ActionEvent(event.getSource(), event.getID(), ActionNames.LOGGER_PANEL_ENABLE_DISABLE));
         }
     }
 
     /**
      * Define AWT window title (WM_CLASS string) (useful on Gnome 3 / Linux)
      */
     private void setWindowTitle() {
         Class<?> xtoolkit = Toolkit.getDefaultToolkit().getClass();
         if (xtoolkit.getName().equals("sun.awt.X11.XToolkit")) { // NOSONAR (we don't want to depend on native LAF) $NON-NLS-1$
             try {
                 final Field awtAppClassName = xtoolkit.getDeclaredField("awtAppClassName"); // $NON-NLS-1$
                 awtAppClassName.setAccessible(true);
                 awtAppClassName.set(null, DEFAULT_APP_NAME);
             } catch (NoSuchFieldException | IllegalAccessException nsfe) {
-                log.warn("Error awt title: " + nsfe); // $NON-NLS-1$
+                if (log.isWarnEnabled()) {
+                    log.warn("Error awt title: {}", nsfe.toString()); // $NON-NLS-1$
+                }
             }
        }
     }
 
     /**
      * Update Undo/Redo icons state
      * 
      * @param canUndo
      *            Flag whether the undo button should be enabled
      * @param canRedo
      *            Flag whether the redo button should be enabled
      */
     public void updateUndoRedoIcons(boolean canUndo, boolean canRedo) {
         toolbar.updateUndoRedoIcons(canUndo, canRedo);
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/SavePropertyDialog.java b/src/core/org/apache/jmeter/gui/SavePropertyDialog.java
index 0e99bc1d8..cbfefdc48 100644
--- a/src/core/org/apache/jmeter/gui/SavePropertyDialog.java
+++ b/src/core/org/apache/jmeter/gui/SavePropertyDialog.java
@@ -1,170 +1,170 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 /*
  * Created on Sep 15, 2004
  */
 package org.apache.jmeter.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Frame;
 import java.awt.GridLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.HashMap;
 import java.util.Map;
 
 import javax.swing.AbstractAction;
 import javax.swing.Action;
 import javax.swing.InputMap;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JComponent;
 import javax.swing.JDialog;
 import javax.swing.JPanel;
 import javax.swing.JRootPane;
 
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Generates Configure pop-up dialogue for Listeners from all methods in SampleSaveConfiguration
  * with the signature "boolean saveXXX()".
  * There must be a corresponding "void setXXX(boolean)" method, and a property save_XXX which is
  * used to name the field on the dialogue.
  *
  */
 public class SavePropertyDialog extends JDialog implements ActionListener {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SavePropertyDialog.class);
 
-    private static final long serialVersionUID = 232L;
+    private static final long serialVersionUID = 233L;
 
     private static final Map<String, Functor> functors = new HashMap<>();
 
     private static final String RESOURCE_PREFIX = "save_"; // $NON-NLS-1$ e.g. save_XXX property
 
     private SampleSaveConfiguration saveConfig;
 
     /**
      * @deprecated Constructor only intended for use in testing
      */
     @Deprecated // Constructor only intended for use in testing
     public SavePropertyDialog() {
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
     }
     /**
      * @param owner The {@link Frame} from which the dialog is displayed
      * @param title The string to be used as a title of this dialog
      * @param modal specifies whether the dialog should be modal
      * @param s The details, which sample attributes are to be saved
      * @throws java.awt.HeadlessException - when run headless
      */
     public SavePropertyDialog(Frame owner, String title, boolean modal, SampleSaveConfiguration s)
     {
         super(owner, title, modal);
         saveConfig = s;
-        log.debug("SampleSaveConfiguration = " + saveConfig);// $NON-NLS-1$
+        log.debug("SampleSaveConfiguration = {}", saveConfig);// $NON-NLS-1$
         initDialog();
     }
 
     private void initDialog() {
         this.getContentPane().setLayout(new BorderLayout());
         final int configCount = (SampleSaveConfiguration.SAVE_CONFIG_NAMES.size() / 3) + 1;
-        log.debug("grid panel is " + 3 + " by " + configCount);
+        log.debug("grid panel is {} by {}", 3, configCount);
         JPanel checkPanel = new JPanel(new GridLayout(configCount, 3));
         for (final String name : SampleSaveConfiguration.SAVE_CONFIG_NAMES) {
             try {
                 JCheckBox check = new JCheckBox(
                         JMeterUtils.getResString(RESOURCE_PREFIX + name),
                         getSaveState(SampleSaveConfiguration.getterName(name)));
                 check.addActionListener(this);
                 final String actionCommand = SampleSaveConfiguration.setterName(name); // $NON-NLS-1$
                 check.setActionCommand(actionCommand);
                 if (!functors.containsKey(actionCommand)) {
                     functors.put(actionCommand, new Functor(actionCommand));
                 }
                 checkPanel.add(check, BorderLayout.NORTH);
             } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                 log.warn("Problem creating save config dialog", e);
             }
         }
         getContentPane().add(checkPanel, BorderLayout.NORTH);
         JButton exit = new JButton(JMeterUtils.getResString("done")); // $NON-NLS-1$
         this.getContentPane().add(exit, BorderLayout.SOUTH);
         exit.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 dispose();
             }
         });
     }
     
     @Override
     protected JRootPane createRootPane() {
         JRootPane rootPane = new JRootPane();
         Action escapeAction = new AbstractAction("ESCAPE") {
             /**
              * 
              */
             private static final long serialVersionUID = 2208129319916921772L;
 
             @Override
             public void actionPerformed(ActionEvent e) {
                 setVisible(false);
             }
         };
         InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
         inputMap.put(KeyStrokes.ESC, escapeAction.getValue(Action.NAME));
         rootPane.getActionMap().put(escapeAction.getValue(Action.NAME), escapeAction);
         return rootPane;
     }
 
     @Override
     public void actionPerformed(ActionEvent e) {
         String action = e.getActionCommand();
         Functor f = functors.get(action);
         f.invoke(saveConfig, new Object[] {Boolean.valueOf(((JCheckBox) e.getSource()).isSelected()) });
     }
 
     private boolean getSaveState(String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
         Method method = SampleSaveConfiguration.class.getMethod(methodName);
         return ((Boolean) method.invoke(saveConfig)).booleanValue();
     }
 
     /**
      * @return Returns the saveConfig.
      */
     public SampleSaveConfiguration getSaveConfig() {
         return saveConfig;
     }
 
     /**
      * @param saveConfig
      *            The saveConfig to set.
      */
     public void setSaveConfig(SampleSaveConfiguration saveConfig) {
         this.saveConfig = saveConfig;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/UndoHistory.java b/src/core/org/apache/jmeter/gui/UndoHistory.java
index ff380eddc..c722356f0 100644
--- a/src/core/org/apache/jmeter/gui/UndoHistory.java
+++ b/src/core/org/apache/jmeter/gui/UndoHistory.java
@@ -1,370 +1,374 @@
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
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
 import javax.swing.JTree;
 import javax.swing.event.TreeModelEvent;
 import javax.swing.event.TreeModelListener;
 
 import org.apache.jmeter.gui.action.UndoCommand;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * This class serves storing Test Tree state and navigating through it
  * to give the undo/redo ability for test plan changes
  * 
  * @since 2.12
  */
 public class UndoHistory implements TreeModelListener, Serializable {
     /**
      * 
      */
-    private static final long serialVersionUID = -974269825492906010L;
+    private static final long serialVersionUID = 1L;
     
     /**
      * Interface to be implemented by components interested in UndoHistory
      */
     public interface HistoryListener {
         void notifyChangeInHistory(UndoHistory history);
     }
 
     /**
      * Avoid storing too many elements
      *
      * @param <T> Class that should be held in this container
      */
     private static class LimitedArrayList<T> extends ArrayList<T> {
         /**
          *
          */
         private static final long serialVersionUID = -6574380490156356507L;
         private int limit;
 
         public LimitedArrayList(int limit) {
             this.limit = limit;
         }
 
         @Override
         public boolean add(T item) {
             if (this.size() + 1 > limit) {
                 this.remove(0);
             }
             return super.add(item);
         }
     }
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(UndoHistory.class);
 
     /**
      * temporary storage for GUI tree expansion state
      */
     private ArrayList<Integer> savedExpanded = new ArrayList<>();
 
     /**
      * temporary storage for GUI tree selected row
      */
     private int savedSelected = 0;
 
     private static final int INITIAL_POS = -1;
     private int position = INITIAL_POS;
 
     private static final int HISTORY_SIZE = JMeterUtils.getPropDefault("undo.history.size", 0);
 
     private List<UndoHistoryItem> history = new LimitedArrayList<>(HISTORY_SIZE);
 
     /**
      * flag to prevent recursive actions
      */
     private boolean working = false;
 
     /**
      * History listeners
      */
     private List<HistoryListener> listeners = new ArrayList<>();
 
     public UndoHistory() {
     }
 
     /**
      * Clears the undo history
      */
     public void clear() {
         if (working) {
             return;
         }
         log.debug("Clearing undo history");
         history.clear();
         position = INITIAL_POS;
         notifyListeners();
     }
 
     /**
      * Add tree model copy to the history
      * <p>
      * This method relies on the rule that the record in history made AFTER
      * change has been made to test plan
      *
      * @param treeModel JMeterTreeModel
      * @param comment   String
      */
     public void add(JMeterTreeModel treeModel, String comment) {
         if(!isEnabled()) {
             log.debug("undo.history.size is set to 0, undo/redo feature is disabled");
             return;
         }
 
         // don't add element if we are in the middle of undo/redo or a big loading
         if (working) {
             log.debug("Not adding history because of noop");
             return;
         }
 
         JMeterTreeNode root = (JMeterTreeNode) treeModel.getRoot();
         if (root.getChildCount() < 1) {
             log.debug("Not adding history because of no children");
             return;
         }
 
         String name = root.getName();
 
-        log.debug("Adding history element " + name + ": " + comment);
+        log.debug("Adding history element {}: {}", name, comment);
 
         working = true;
         // get test plan tree
         HashTree tree = treeModel.getCurrentSubTree((JMeterTreeNode) treeModel.getRoot());
         // first clone to not convert original tree
         tree = (HashTree) tree.getTree(tree.getArray()[0]).clone();
 
         position++;
         while (history.size() > position) {
-            log.debug("Removing further record, position: " + position + ", size: " + history.size());
+            if (log.isDebugEnabled()) {
+                log.debug("Removing further record, position: {}, size: {}", position, history.size());
+            }
             history.remove(history.size() - 1);
         }
 
         // cloning is required because we need to immute stored data
         HashTree copy = UndoCommand.convertAndCloneSubTree(tree);
 
         history.add(new UndoHistoryItem(copy, comment));
 
-        log.debug("Added history element, position: " + position + ", size: " + history.size());
+        log.debug("Added history element, position: {}, size: {}", position, history.size());
         working = false;
         notifyListeners();
     }
 
     /**
      * Goes through undo history, changing GUI
      *
      * @param offset        the direction to go to, usually -1 for undo or 1 for redo
      * @param acceptorModel TreeModel to accept the changes
      */
     public void moveInHistory(int offset, JMeterTreeModel acceptorModel) {
-        log.debug("Moving history from position " + position + " with step " + offset + ", size is " + history.size());
+        log.debug("Moving history from position {} with step {}, size is {}", position, offset, history.size());
         if (offset < 0 && !canUndo()) {
             log.warn("Can't undo, we're already on the last record");
             return;
         }
 
         if (offset > 0 && !canRedo()) {
             log.warn("Can't redo, we're already on the first record");
             return;
         }
 
         if (history.isEmpty()) {
             log.warn("Can't proceed, the history is empty");
             return;
         }
 
         position += offset;
 
         final GuiPackage guiInstance = GuiPackage.getInstance();
 
         // save tree expansion and selection state before changing the tree
         saveTreeState(guiInstance);
 
         // load the tree
         loadHistoricalTree(acceptorModel, guiInstance);
 
         // load tree UI state
         restoreTreeState(guiInstance);
 
-        log.debug("Current position " + position + ", size is " + history.size());
+        if (log.isDebugEnabled()) {
+            log.debug("Current position {}, size is {}", position, history.size());
+        }
 
         // refresh the all ui
         guiInstance.updateCurrentGui();
         guiInstance.getMainFrame().repaint();
         notifyListeners();
     }
 
     /**
      * Load the undo item into acceptorModel tree
      *
      * @param acceptorModel tree to accept the data
      * @param guiInstance {@link GuiPackage} to be used
      */
     private void loadHistoricalTree(JMeterTreeModel acceptorModel, GuiPackage guiInstance) {
         HashTree newModel = history.get(position).getTree();
         acceptorModel.removeTreeModelListener(this);
         working = true;
         try {
             guiInstance.getTreeModel().clearTestPlan();
             guiInstance.addSubTree(newModel);
         } catch (Exception ex) {
             log.error("Failed to load from history", ex);
         }
         acceptorModel.addTreeModelListener(this);
         working = false;
     }
 
     /**
      * @return true if remaing items
      */
     public boolean canRedo() {
         return position < history.size() - 1;
     }
 
     /**
      * @return true if not at first element
      */
     public boolean canUndo() {
         return position > INITIAL_POS + 1;
     }
 
     /**
      * Record the changes in the node as the undo step
      *
      * @param tme {@link TreeModelEvent} with event details
      */
     @Override
     public void treeNodesChanged(TreeModelEvent tme) {
         String name = ((JMeterTreeNode) tme.getTreePath().getLastPathComponent()).getName();
-        log.debug("Nodes changed " + name);
+        log.debug("Nodes changed {}", name);
         final JMeterTreeModel sender = (JMeterTreeModel) tme.getSource();
         add(sender, "Node changed " + name);
     }
 
     /**
      * Record adding nodes as the undo step
      *
      * @param tme {@link TreeModelEvent} with event details
      */
     @Override
     public void treeNodesInserted(TreeModelEvent tme) {
         String name = ((JMeterTreeNode) tme.getTreePath().getLastPathComponent()).getName();
-        log.debug("Nodes inserted " + name);
+        log.debug("Nodes inserted {}", name);
         final JMeterTreeModel sender = (JMeterTreeModel) tme.getSource();
         add(sender, "Add " + name);
     }
 
     /**
      * Record deleting nodes as the undo step
      *
      * @param tme {@link TreeModelEvent} with event details
      */
     @Override
     public void treeNodesRemoved(TreeModelEvent tme) {
         String name = ((JMeterTreeNode) tme.getTreePath().getLastPathComponent()).getName();
-        log.debug("Nodes removed: " + name);
+        log.debug("Nodes removed: {}", name);
         add((JMeterTreeModel) tme.getSource(), "Remove " + name);
     }
 
     /**
      * Record some other change
      *
      * @param tme {@link TreeModelEvent} with event details
      */
     @Override
     public void treeStructureChanged(TreeModelEvent tme) {
         log.debug("Nodes struct changed");
         add((JMeterTreeModel) tme.getSource(), "Complex Change");
     }
 
     /**
      * Save tree expanded and selected state
      *
      * @param guiPackage {@link GuiPackage} to be used
      */
     private void saveTreeState(GuiPackage guiPackage) {
         savedExpanded.clear();
 
         MainFrame mainframe = guiPackage.getMainFrame();
         if (mainframe != null) {
             final JTree tree = mainframe.getTree();
             savedSelected = tree.getMinSelectionRow();
 
             for (int rowN = 0; rowN < tree.getRowCount(); rowN++) {
                 if (tree.isExpanded(rowN)) {
                     savedExpanded.add(Integer.valueOf(rowN));
                 }
             }
         }
     }
 
     /**
      * Restore tree expanded and selected state
      *
      * @param guiInstance GuiPackage to be used
      */
     private void restoreTreeState(GuiPackage guiInstance) {
         final JTree tree = guiInstance.getMainFrame().getTree();
 
         if (savedExpanded.size() > 0) {
             for (int rowN : savedExpanded) {
                 tree.expandRow(rowN);
             }
         } else {
             tree.expandRow(0);
         }
         tree.setSelectionRow(savedSelected);
     }
     
     /**
      * 
      * @return true if history is enabled
      */
     boolean isEnabled() {
         return HISTORY_SIZE > 0;
     }
     
     /**
      * Register HistoryListener 
      * @param listener to add to our listeners
      */
     public void registerHistoryListener(HistoryListener listener) {
         listeners.add(listener);
     }
     
     /**
      * Notify listener
      */
     private void notifyListeners() {
         for (HistoryListener listener : listeners) {
             listener.notifyChangeInHistory(this);
         }
     }
 
 }
diff --git a/src/core/org/apache/jmeter/gui/action/template/TemplateManager.java b/src/core/org/apache/jmeter/gui/action/template/TemplateManager.java
index 590b5549e..482758b12 100644
--- a/src/core/org/apache/jmeter/gui/action/template/TemplateManager.java
+++ b/src/core/org/apache/jmeter/gui/action/template/TemplateManager.java
@@ -1,164 +1,172 @@
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
 
 package org.apache.jmeter.gui.action.template;
 
 import java.io.File;
 import java.util.LinkedHashMap;
 import java.util.Map;
 
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 import com.thoughtworks.xstream.XStream;
 import com.thoughtworks.xstream.io.StreamException;
 import com.thoughtworks.xstream.io.xml.DomDriver;
 
 /**
  * Manages Test Plan templates
  * @since 2.10
  */
 public class TemplateManager {
     // Created by XStream reading templates.xml
     private static class Templates {
         /*
          * N.B. Must use LinkedHashMap for field type
          * XStream creates a plain HashMap if one uses Map as the field type.
          */
         private final LinkedHashMap<String, Template> templates = new LinkedHashMap<>();
     }
     private static final String TEMPLATE_FILES = JMeterUtils.getPropDefault("template.files", // $NON-NLS-1$
             "/bin/templates/templates.xml");
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(TemplateManager.class);
     
     private static final TemplateManager SINGLETON = new TemplateManager();
     
     private final Map<String, Template> allTemplates;
 
     private final XStream xstream = initXStream();
 
     public static TemplateManager getInstance() {
         return SINGLETON;
     }
     
     private TemplateManager()  {
         allTemplates = readTemplates();            
     }
     
     private XStream initXStream() {
         XStream xstream = new XStream(new DomDriver(){
             /**
              * Create the DocumentBuilderFactory instance.
              * See https://blog.compass-security.com/2012/08/secure-xml-parser-configuration/
              * See https://github.com/x-stream/xstream/issues/25
              * @return the new instance
              */
             @Override
             protected DocumentBuilderFactory createDocumentBuilderFactory() {
                 final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                 try {
                     factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                     factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                 } catch (ParserConfigurationException e) {
                     throw new StreamException(e);
                 }
                 factory.setExpandEntityReferences(false);
                 return factory;
             }
         });
         xstream.alias("template", Template.class);
         xstream.alias("templates", Templates.class);
         xstream.useAttributeFor(Template.class, "isTestPlan");
         
         // templates i
         xstream.addImplicitMap(Templates.class, 
                 // field TemplateManager#templates 
                 "templates", // $NON-NLS-1$
                 Template.class,     
                 // field Template#name 
                 "name" // $NON-NLS-1$
                 );
                 
         return xstream;
     }
 
     public void addTemplate(Template template) {
         allTemplates.put(template.getName(), template);
     }
 
     /**
      * Resets the template Map by re-reading the template files.
      *
      * @return this
      */
     public TemplateManager reset() {
         allTemplates.clear();
         allTemplates.putAll(readTemplates());
         return this;
     }
 
     /**
      * @return the templates names
      */
     public String[] getTemplateNames() {
         return allTemplates.keySet().toArray(new String[allTemplates.size()]);
     }
 
     private Map<String, Template> readTemplates() {
         final Map<String, Template> temps = new LinkedHashMap<>();
        
         final String[] templateFiles = TEMPLATE_FILES.split(",");
         for (String templateFile : templateFiles) {
             if(!StringUtils.isEmpty(templateFile)) {
                 final File f = new File(JMeterUtils.getJMeterHome(), templateFile); 
                 try {
                     if(f.exists() && f.canRead()) {
-                        log.info("Reading templates from:"+f.getAbsolutePath());
+                        if (log.isInfoEnabled()) {
+                            log.info("Reading templates from: {}", f.getAbsolutePath());
+                        }
                         final File parent = f.getParentFile();
                         final LinkedHashMap<String, Template> templates = ((Templates) xstream.fromXML(f)).templates;
                         for(Template t : templates.values()) {
                             if (!t.getFileName().startsWith("/")) {
                                 t.setParent(parent);
                             }
                         }
                         temps.putAll(templates);
                     } else {
-                        log.warn("Ignoring template file:'"+f.getAbsolutePath()+"' as it does not exist or is not readable");
+                        if (log.isWarnEnabled()) {
+                            log.warn("Ignoring template file:'{}' as it does not exist or is not readable",
+                                    f.getAbsolutePath());
+                        }
+                    }
+                } catch(Exception ex) {
+                    if (log.isWarnEnabled()) {
+                        log.warn("Ignoring template file:'{}', an error occured parsing the file", f.getAbsolutePath(),
+                                ex);
                     }
-                } catch(Exception ex) {                    
-                    log.warn("Ignoring template file:'"+f.getAbsolutePath()+"', an error occured parsing the file", ex);
                 } 
             }
         }
         return temps;
     }
 
     /**
      * @param selectedTemplate Template name
      * @return {@link Template}
      */
     public Template getTemplateByName(String selectedTemplate) {
         return allTemplates.get(selectedTemplate);
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/validation/TreeClonerForValidation.java b/src/core/org/apache/jmeter/gui/action/validation/TreeClonerForValidation.java
index 1d8944f6a..051635eb0 100644
--- a/src/core/org/apache/jmeter/gui/action/validation/TreeClonerForValidation.java
+++ b/src/core/org/apache/jmeter/gui/action/validation/TreeClonerForValidation.java
@@ -1,97 +1,94 @@
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
 
 package org.apache.jmeter.gui.action.validation;
 
 import org.apache.jmeter.control.LoopController;
 import org.apache.jmeter.engine.TreeCloner;
 import org.apache.jmeter.threads.AbstractThreadGroup;
 import org.apache.jmeter.threads.ThreadGroup;
 import org.apache.jmeter.timers.Timer;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.backend.Backend;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Clones the test tree,  skipping test elements that implement {@link Timer} by default.
  * @since 3.0
  */
 public class TreeClonerForValidation extends TreeCloner {
     
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(TreeClonerForValidation.class);
 
     /**
      * Number of Threads to configure when running a Thread Group during a validation
      */
     protected static final int VALIDATION_NUMBER_OF_THREADS = JMeterUtils.getPropDefault("testplan_validation.nb_threads_per_thread_group", 1); //$NON-NLS-1$
 
     /**
      * Ignore or not timers during a Thread Group validation
      */
     protected static final boolean VALIDATION_IGNORE_TIMERS = JMeterUtils.getPropDefault("testplan_validation.ignore_timers", true); //$NON-NLS-1$
 
     /**
      * Ignore or not Backend during a Thread Group validation
      */
     protected static final boolean VALIDATION_IGNORE_BACKENDS = JMeterUtils.getPropDefault("testplan_validation.ignore_backends", true); //$NON-NLS-1$
 
     /**
      * Number of iterations to run during a Thread Group validation
      */
     protected static final int VALIDATION_ITERATIONS = JMeterUtils.getPropDefault("testplan_validation.number_iterations", 1); //$NON-NLS-1$
     
     static {
-        if(LOG.isInfoEnabled()) {
-            LOG.info("Running validation with number of threads:"+VALIDATION_NUMBER_OF_THREADS
-                    + ", ignoreTimers:"+VALIDATION_IGNORE_TIMERS
-                    + ", number of iterations:"+VALIDATION_ITERATIONS);
-        }
+        log.info("Running validation with number of threads:{}, ignoreTimers:{}, number of iterations:{}",
+                VALIDATION_NUMBER_OF_THREADS, VALIDATION_IGNORE_TIMERS, VALIDATION_ITERATIONS);
     }
 
     public TreeClonerForValidation() {
         this(false);
     }
 
     public TreeClonerForValidation(boolean honourNoThreadClone) {
         super(honourNoThreadClone);
     }
 
     /**
      * @see org.apache.jmeter.engine.TreeCloner#addNodeToTree(java.lang.Object)
      */
     @Override
     protected Object addNodeToTree(Object node) {
         if((VALIDATION_IGNORE_TIMERS && node instanceof Timer) || 
                 (VALIDATION_IGNORE_BACKENDS && node instanceof Backend)) {
             return node; // don't add timer or backend
         } else {
             Object clonedNode = super.addNodeToTree(node);
             if(clonedNode instanceof org.apache.jmeter.threads.ThreadGroup) {
                 ThreadGroup tg = (ThreadGroup)clonedNode;
                 tg.setNumThreads(VALIDATION_NUMBER_OF_THREADS);
                 tg.setScheduler(false);
                 tg.setProperty(ThreadGroup.DELAY, 0);
                 if(((AbstractThreadGroup)clonedNode).getSamplerController() instanceof LoopController) {
                     ((LoopController)((AbstractThreadGroup)clonedNode).getSamplerController()).setLoops(VALIDATION_ITERATIONS);
                 }
             }
             return clonedNode;
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/tree/JMeterTreeListener.java b/src/core/org/apache/jmeter/gui/tree/JMeterTreeListener.java
index c038ff7ad..da042bd8a 100644
--- a/src/core/org/apache/jmeter/gui/tree/JMeterTreeListener.java
+++ b/src/core/org/apache/jmeter/gui/tree/JMeterTreeListener.java
@@ -1,240 +1,240 @@
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
 
 package org.apache.jmeter.gui.tree;
 
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.InputEvent;
 import java.awt.event.KeyEvent;
 import java.awt.event.KeyListener;
 import java.awt.event.MouseEvent;
 import java.awt.event.MouseListener;
 
 import javax.swing.JPopupMenu;
 import javax.swing.JTree;
 import javax.swing.event.TreeSelectionEvent;
 import javax.swing.event.TreeSelectionListener;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.MainFrame;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.KeyStrokes;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class JMeterTreeListener implements TreeSelectionListener, MouseListener, KeyListener {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JMeterTreeListener.class);
 
     private TreePath currentPath;
 
     private ActionListener actionHandler;
 
     private JMeterTreeModel model;
 
     private JTree tree;
 
     /**
      * Constructor for the JMeterTreeListener object.
      * 
      * @param model
      *            The {@link JMeterTreeModel} for this listener
      */
     public JMeterTreeListener(JMeterTreeModel model) {
         this.model = model;
     }
 
     /**
      * Constructor for the {@link JMeterTreeListener} object
      */
     public JMeterTreeListener() {
     }
 
     /**
      * Set the {@link JMeterTreeModel} for this listener
      * @param m The {@link JMeterTreeModel} to be used
      */
     public void setModel(JMeterTreeModel m) {
         model = m;
     }
 
     /**
      * Sets the ActionHandler attribute of the JMeterTreeListener object.
      *
      * @param ah
      *            the new ActionHandler value
      */
     public void setActionHandler(ActionListener ah) {
         actionHandler = ah;
     }
 
     /**
      * Sets the JTree attribute of the JMeterTreeListener object.
      *
      * @param tree
      *            the new JTree value
      */
     public void setJTree(JTree tree) {
         this.tree = tree;
     }
 
     /**
      * Gets the JTree attribute of the JMeterTreeListener object.
      *
      * @return tree the current JTree value.
      */
     public JTree getJTree() {
         return tree;
     }
 
     /**
      * Gets the CurrentNode attribute of the JMeterTreeListener object.
      *
      * @return the CurrentNode value
      */
     public JMeterTreeNode getCurrentNode() {
         if (currentPath != null) {
             if (currentPath.getLastPathComponent() != null) {
                 return (JMeterTreeNode) currentPath.getLastPathComponent();
             }
             return (JMeterTreeNode) currentPath.getParentPath().getLastPathComponent();
         }
         return (JMeterTreeNode) model.getRoot();
     }
 
     public JMeterTreeNode[] getSelectedNodes() {
         TreePath[] paths = tree.getSelectionPaths();
         if (paths == null) {
             return new JMeterTreeNode[] { getCurrentNode() };
         }
         JMeterTreeNode[] nodes = new JMeterTreeNode[paths.length];
         for (int i = 0; i < paths.length; i++) {
             nodes[i] = (JMeterTreeNode) paths[i].getLastPathComponent();
         }
 
         return nodes;
     }
 
     public TreePath removedSelectedNode() {
         currentPath = currentPath.getParentPath();
         return currentPath;
     }
 
     @Override
     public void valueChanged(TreeSelectionEvent e) {
         log.debug("value changed, updating currentPath");
         currentPath = e.getNewLeadSelectionPath();
         // Call requestFocusInWindow to ensure current component loses focus and
         // all values are correctly saved
         // see https://bz.apache.org/bugzilla/show_bug.cgi?id=55103
         // see https://bz.apache.org/bugzilla/show_bug.cgi?id=55459
         tree.requestFocusInWindow();
         actionHandler.actionPerformed(new ActionEvent(this, 3333, ActionNames.EDIT)); // $NON-NLS-1$
     }
 
     @Override
     public void mouseClicked(MouseEvent ev) {
     }
 
     @Override
     public void mouseReleased(MouseEvent e) {
         GuiPackage.getInstance().getMainFrame().repaint();
     }
 
     @Override
     public void mouseEntered(MouseEvent e) {
     }
 
     @Override
     public void mousePressed(MouseEvent e) {
         // Get the Main Frame.
         MainFrame mainFrame = GuiPackage.getInstance().getMainFrame();
         // Close any Main Menu that is open
         mainFrame.closeMenu();
         int selRow = tree.getRowForLocation(e.getX(), e.getY());
         if (tree.getPathForLocation(e.getX(), e.getY()) != null) {
             log.debug("mouse pressed, updating currentPath");
             currentPath = tree.getPathForLocation(e.getX(), e.getY());
         }
         if (selRow != -1) {
             if (isRightClick(e)) {
                 if (tree.getSelectionCount() < 2) {
                     tree.setSelectionPath(currentPath);
                 }
                 log.debug("About to display pop-up");
                 displayPopUp(e);
             }
         }
     }
 
     @Override
     public void mouseExited(MouseEvent ev) {
     }
 
     @Override
     public void keyPressed(KeyEvent e) {
         String actionName = null;
 
         if (KeyStrokes.matches(e, KeyStrokes.COPY)) {
             actionName = ActionNames.COPY;
         } else if (KeyStrokes.matches(e, KeyStrokes.PASTE)) {
             actionName = ActionNames.PASTE;
         } else if (KeyStrokes.matches(e, KeyStrokes.CUT)) {
             actionName = ActionNames.CUT;
         } else if (KeyStrokes.matches(e, KeyStrokes.DUPLICATE)) {
             actionName = ActionNames.DUPLICATE;
         } else if (KeyStrokes.matches(e, KeyStrokes.ALT_UP_ARROW)) {
             actionName = ActionNames.MOVE_UP;
         } else if (KeyStrokes.matches(e, KeyStrokes.ALT_DOWN_ARROW)) {
             actionName = ActionNames.MOVE_DOWN;
         } else if (KeyStrokes.matches(e, KeyStrokes.ALT_LEFT_ARROW)) {
             actionName = ActionNames.MOVE_LEFT;
         } else if (KeyStrokes.matches(e, KeyStrokes.ALT_RIGHT_ARROW)) {
             actionName = ActionNames.MOVE_RIGHT;
         } else if (KeyStrokes.matches(e, KeyStrokes.SHIFT_LEFT_ARROW)) {
             actionName = ActionNames.COLLAPSE;
         } else if (KeyStrokes.matches(e, KeyStrokes.SHIFT_RIGHT_ARROW)) {
             actionName = ActionNames.EXPAND;
         } 
         
         if (actionName != null) {
             final ActionRouter actionRouter = ActionRouter.getInstance();
             actionRouter.doActionNow(new ActionEvent(e.getSource(), e.getID(), actionName));
             e.consume();
         }
     }
 
     @Override
     public void keyReleased(KeyEvent e) {
     }
 
     @Override
     public void keyTyped(KeyEvent e) {
     }
 
     private boolean isRightClick(MouseEvent e) {
         return e.isPopupTrigger() || (InputEvent.BUTTON2_MASK & e.getModifiers()) > 0 || (InputEvent.BUTTON3_MASK == e.getModifiers());
     }
 
     private void displayPopUp(MouseEvent e) {
         JPopupMenu pop = getCurrentNode().createPopupMenu();
         GuiPackage.getInstance().displayPopUp(e, pop);
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java b/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
index 6d8b79a35..3b0fd1f1d 100644
--- a/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
+++ b/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
@@ -1,201 +1,201 @@
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
 
 package org.apache.jmeter.gui.tree;
 
 import java.awt.Image;
 import java.beans.BeanInfo;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Enumeration;
 import java.util.List;
 
 import javax.swing.ImageIcon;
 import javax.swing.JPopupMenu;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.TreeNode;
 
 import org.apache.jmeter.gui.GUIFactory;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testelement.TestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class JMeterTreeNode extends DefaultMutableTreeNode implements NamedTreeNode {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JMeterTreeNode.class);
 
     private static final int TEST_PLAN_LEVEL = 1;
 
     // See Bug 54648
     private transient JMeterTreeModel treeModel;
 
     private boolean markedBySearch;
 
     public JMeterTreeNode() {// Allow serializable test to work
         // TODO: is the serializable test necessary now that JMeterTreeNode is
         // no longer a GUI component?
         this(null, null);
     }
 
     public JMeterTreeNode(TestElement userObj, JMeterTreeModel treeModel) {
         super(userObj);
         this.treeModel = treeModel;
     }
 
     public boolean isEnabled() {
         return getTestElement().isEnabled();
     }
 
     public void setEnabled(boolean enabled) {
         getTestElement().setEnabled(enabled);
         treeModel.nodeChanged(this);
     }
     
     /**
      * Return nodes to level 2
      * @return {@link List} of {@link JMeterTreeNode}s
      */
     public List<JMeterTreeNode> getPathToThreadGroup() {
         List<JMeterTreeNode> nodes = new ArrayList<>();
         if (treeModel != null) {
             TreeNode[] nodesToRoot = treeModel.getPathToRoot(this);
             for (TreeNode node : nodesToRoot) {
                 JMeterTreeNode jMeterTreeNode = (JMeterTreeNode) node;
                 int level = jMeterTreeNode.getLevel();
                 if (level >= TEST_PLAN_LEVEL) {
                     nodes.add(jMeterTreeNode);
                 }
             }
         }
         return nodes;
     }
     
     /**
      * Tag Node as result of a search
      * @param tagged The flag to be used for tagging
      */
     public void setMarkedBySearch(boolean tagged) {
         this.markedBySearch = tagged;
         treeModel.nodeChanged(this);
     }
     
     /**
      * Node is markedBySearch by a search
      * @return true if marked by search
      */
     public boolean isMarkedBySearch() {
         return this.markedBySearch;
     }
 
     public ImageIcon getIcon() {
         return getIcon(true);
     }
 
     public ImageIcon getIcon(boolean enabled) {
         TestElement testElement = getTestElement();
         try {
             if (testElement instanceof TestBean) {
                 Class<?> testClass = testElement.getClass();
                 try {
                     Image img = Introspector.getBeanInfo(testClass).getIcon(BeanInfo.ICON_COLOR_16x16);
                     // If icon has not been defined, then use GUI_CLASS property
                     if (img == null) {
                         Object clazz = Introspector.getBeanInfo(testClass).getBeanDescriptor()
                                 .getValue(TestElement.GUI_CLASS);
                         if (clazz == null) {
-                            log.warn("getIcon(): Can't obtain GUI class from " + testClass.getName());
+                            log.warn("getIcon(): Can't obtain GUI class from {}", testClass);
                             return null;
                         }
                         return GUIFactory.getIcon(Class.forName((String) clazz), enabled);
                     }
                     return new ImageIcon(img);
                 } catch (IntrospectionException e1) {
-                    log.error("Can't obtain icon for class "+testElement, e1);
+                    log.error("Can't obtain icon for class {}", testElement, e1);
                     throw new org.apache.jorphan.util.JMeterError(e1);
                 }
             }
             return GUIFactory.getIcon(Class.forName(testElement.getPropertyAsString(TestElement.GUI_CLASS)),
                         enabled);
         } catch (ClassNotFoundException e) {
-            log.warn("Can't get icon for class " + testElement, e);
+            log.warn("Can't get icon for class {}", testElement, e);
             return null;
         }
     }
 
     public Collection<String> getMenuCategories() {
         try {
             return GuiPackage.getInstance().getGui(getTestElement()).getMenuCategories();
         } catch (Exception e) {
             log.error("Can't get popup menu for gui", e);
             return null;
         }
     }
 
     public JPopupMenu createPopupMenu() {
         try {
             return GuiPackage.getInstance().getGui(getTestElement()).createPopupMenu();
         } catch (Exception e) {
             log.error("Can't get popup menu for gui", e);
             return null;
         }
     }
 
     public TestElement getTestElement() {
         return (TestElement) getUserObject();
     }
 
     public String getStaticLabel() {
         return GuiPackage.getInstance().getGui((TestElement) getUserObject()).getStaticLabel();
     }
 
     public String getDocAnchor() {
         return GuiPackage.getInstance().getGui((TestElement) getUserObject()).getDocAnchor();
     }
 
     /** {@inheritDoc} */
     @Override
     public void setName(String name) {
         ((TestElement) getUserObject()).setName(name);
     }
 
     /** {@inheritDoc} */
     @Override
     public String getName() {
         return ((TestElement) getUserObject()).getName();
     }
 
     /** {@inheritDoc} */
     @Override
     public void nameChanged() {
         if (treeModel != null) { // may be null during startup
             treeModel.nodeChanged(this);
         }
     }
 
     // Override in order to provide type safety
     @Override
     @SuppressWarnings("unchecked")
     public Enumeration<JMeterTreeNode> children() {
         return super.children();
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/tree/JMeterTreeTransferHandler.java b/src/core/org/apache/jmeter/gui/tree/JMeterTreeTransferHandler.java
index 0a97b093f..7d07a9dec 100644
--- a/src/core/org/apache/jmeter/gui/tree/JMeterTreeTransferHandler.java
+++ b/src/core/org/apache/jmeter/gui/tree/JMeterTreeTransferHandler.java
@@ -1,316 +1,316 @@
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
 
 package org.apache.jmeter.gui.tree;
 
 import java.awt.datatransfer.DataFlavor;
 import java.awt.datatransfer.Transferable;
 import java.awt.datatransfer.UnsupportedFlavorException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.Enumeration;
 import java.util.List;
 
 import javax.swing.JComponent;
 import javax.swing.JTree;
 import javax.swing.TransferHandler;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.util.MenuFactory;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class JMeterTreeTransferHandler extends TransferHandler {
     
-    private static final long serialVersionUID = 8560957372186260765L;
+    private static final long serialVersionUID = 1L;
 
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JMeterTreeTransferHandler.class);
   
     private DataFlavor nodeFlavor;
     private DataFlavor[] jMeterTreeNodeDataFlavors = new DataFlavor[1];
     
     // hold the nodes that should be removed on drop
     private List<JMeterTreeNode> nodesForRemoval = null;
 
     public JMeterTreeTransferHandler() {
         try {
             // only allow a drag&drop inside the current jvm
             String jvmLocalFlavor = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + JMeterTreeNode[].class.getName() + "\"";
             nodeFlavor = new DataFlavor(jvmLocalFlavor);
             jMeterTreeNodeDataFlavors[0] = nodeFlavor;
         }
         catch (ClassNotFoundException e) {
-            LOG.error("Class Not Found", e);
+            log.error("Class Not Found", e);
         }
     }
 
     
     @Override
     public int getSourceActions(JComponent c) {
         return MOVE;
     }
     
 
     @Override
     protected Transferable createTransferable(JComponent c) {
         this.nodesForRemoval = null;
         JTree tree = (JTree) c;
         TreePath[] paths = tree.getSelectionPaths();
         if (paths != null) {
             
             // sort the selected tree path by row
             sortTreePathByRow(paths, tree);
             
             // if child and a parent are selected : only keep the parent
             boolean[] toRemove = new boolean[paths.length];
             int size = paths.length;
             for (int i = 0; i < paths.length; i++) {
                 for (int j = 0; j < paths.length; j++) {
                     if(i!=j && ((JMeterTreeNode)paths[i].getLastPathComponent()).isNodeAncestor((JMeterTreeNode)paths[j].getLastPathComponent())) {
                         toRemove[i] = true;
                         size--;
                         break;
                     }
                 }
             }
 
             // remove unneeded nodes
             JMeterTreeNode[] nodes = new JMeterTreeNode[size];
             size = 0;
             for (int i = 0; i < paths.length; i++) {
                 if(!toRemove[i]) {
                     JMeterTreeNode node = (JMeterTreeNode) paths[i].getLastPathComponent();
                     nodes[size++] = node;
                 }
             }
             
             return new NodesTransferable(nodes);
         }
         
         return null;
     }
     
 
     private static void sortTreePathByRow(TreePath[] paths, final JTree tree) {
         Comparator<TreePath> cp = new Comparator<TreePath>() {
 
             @Override
             public int compare(TreePath o1, TreePath o2) {
                 int row1 = tree.getRowForPath(o1);
                 int row2 = tree.getRowForPath(o2);
                 
                 return row1<row2 ? -1 : (row1==row2 ? 0 : 1);
             }
         };
         
         Arrays.sort(paths, cp);
     }
 
 
     @Override
     protected void exportDone(JComponent source, Transferable data, int action) {
         
         if(this.nodesForRemoval != null
                 && ((action & MOVE) == MOVE))  {
             GuiPackage guiInstance = GuiPackage.getInstance();
             for (JMeterTreeNode jMeterTreeNode : nodesForRemoval) {
                 guiInstance.getTreeModel().removeNodeFromParent(jMeterTreeNode);
             }
             
             nodesForRemoval = null;
         }
     }
 
     @Override
     public boolean canImport(TransferHandler.TransferSupport support) {
         if (!support.isDrop()) {
             return false;
         }
         
         // the tree accepts a jmx file 
         DataFlavor[] flavors = support.getDataFlavors();
         for (DataFlavor flavor : flavors) {
             // Check for file lists specifically
             if (flavor.isFlavorJavaFileListType()) {
                 return true;
             }
         }
 
         // or a treenode from the same tree
         if (!support.isDataFlavorSupported(nodeFlavor)) {
             return false;
         }
         
         // the copy is disabled
         int action = support.getDropAction();
         if(action != MOVE) {
             return false;
         }
         
         support.setShowDropLocation(true);
 
         // Do not allow a drop on the drag source selections.
         JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
         
         TreePath dest = dl.getPath();
         JMeterTreeNode target = (JMeterTreeNode) dest.getLastPathComponent();
         
         // TestPlan and WorkBench are the only children of the root
         if(target.isRoot()) {
             return false;
         }
         
         JMeterTreeNode[] nodes = getDraggedNodes(support.getTransferable());
         if(nodes == null || nodes.length == 0) {
             return false;
         }
         
         for (JMeterTreeNode node : nodes) {
             if(target == node) {
                 return false;
             }
             
             // Do not allow a non-leaf node to be moved into one of its children
             if (node.getChildCount() > 0
                     && target.isNodeAncestor(node)) {
                 return false;
             }
         }
         
         // re-use node association logic
         return MenuFactory.canAddTo(target, nodes);
     }
 
 
     @Override
     public boolean importData(TransferHandler.TransferSupport support) {
         if (!canImport(support)) {
             return false;
         }
         
         // deal with the jmx files
         GuiPackage guiInstance = GuiPackage.getInstance();
         DataFlavor[] flavors = support.getDataFlavors();
         Transferable t = support.getTransferable();
         for (DataFlavor flavor : flavors) {
             // Check for file lists specifically
             if (flavor.isFlavorJavaFileListType()) {
                 try {
                     return guiInstance.getMainFrame().openJmxFilesFromDragAndDrop(t);
                 }
                 catch (Exception e) {
-                    LOG.error("Drop file failed", e);
+                    log.error("Drop file failed", e);
                 }
                 return false;
             }
         }
         
         // Extract transfer data.
         JMeterTreeNode[] nodes = getDraggedNodes(t);
 
         if(nodes == null || nodes.length == 0) {
             return false;
         }
         
         // Get drop location and mode
         JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
         TreePath dest = dl.getPath();
         JMeterTreeNode target = (JMeterTreeNode) dest.getLastPathComponent();
        
         nodesForRemoval = new ArrayList<>();
         int index = dl.getChildIndex();
         TreePath[] pathsToSelect = new TreePath[nodes.length];
         int pathPosition = 0;
         JMeterTreeModel treeModel = guiInstance.getTreeModel();
         for (JMeterTreeNode node : nodes) {
             
             if (index == -1) { // drop mode == DropMode.ON
                 index = target.getChildCount();
             }
 
             // Insert a clone of the node, the original one will be removed by the exportDone method
             // the children are not cloned but moved to the cloned node
             // working on the original node would be harder as 
             //    you'll have to deal with the insertion index offset if you re-order a node inside a parent
             JMeterTreeNode copy = (JMeterTreeNode) node.clone();
             
             // first copy the children as the call to copy.add will modify the collection we're iterating on
             Enumeration<?> enumFrom = node.children();
             List<JMeterTreeNode> tmp = new ArrayList<>();
             while (enumFrom.hasMoreElements()) {
                 JMeterTreeNode child = (JMeterTreeNode) enumFrom.nextElement();
                 tmp.add(child);
             }
             
             for (JMeterTreeNode jMeterTreeNode : tmp) {
                 copy.add(jMeterTreeNode);
             }
             treeModel.insertNodeInto(copy, target, index++);
             nodesForRemoval.add(node);
             pathsToSelect[pathPosition++] = new TreePath(treeModel.getPathToRoot(copy));
         }
         
         TreePath treePath = new TreePath(target.getPath());
         // expand the destination node
         JTree tree = (JTree) support.getComponent();
         tree.expandPath(treePath);
         tree.setSelectionPaths(pathsToSelect);
         return true;
     }
 
 
     private JMeterTreeNode[] getDraggedNodes(Transferable t) {
         JMeterTreeNode[] nodes = null;
         try {
             nodes = (JMeterTreeNode[]) t.getTransferData(nodeFlavor);
         }
         catch (Exception e) {
-            LOG.error("Unsupported Flavor in Transferable", e);
+            log.error("Unsupported Flavor in Transferable", e);
         }
         return nodes;
     }
 
     
     private class NodesTransferable implements Transferable {
         JMeterTreeNode[] nodes;
 
         public NodesTransferable(JMeterTreeNode[] nodes) {
             this.nodes = nodes;
         }
 
         @Override
         public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
             if (!isDataFlavorSupported(flavor)) {
                 throw new UnsupportedFlavorException(flavor);
             }
             return nodes;
         }
 
         @Override
         public DataFlavor[] getTransferDataFlavors() {
             return jMeterTreeNodeDataFlavors;
         }
 
         @Override
         public boolean isDataFlavorSupported(DataFlavor flavor) {
             return nodeFlavor.equals(flavor);
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/IconToolbarBean.java b/src/core/org/apache/jmeter/gui/util/IconToolbarBean.java
index 6ceb1d672..f71eb309b 100644
--- a/src/core/org/apache/jmeter/gui/util/IconToolbarBean.java
+++ b/src/core/org/apache/jmeter/gui/util/IconToolbarBean.java
@@ -1,103 +1,103 @@
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
 
 import org.apache.jmeter.gui.action.ActionNames;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public final class IconToolbarBean {
     
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(IconToolbarBean.class);
 
     private static final String ICON_FIELD_SEP = ",";  //$NON-NLS-1$
 
     private final String i18nKey;
     
     private final String actionName;
     
     private final String iconPath;
     
     private final String iconPathPressed;
 
     /**
      * Constructor to transform a line value (from icon set file) to a icon bean for toolbar.
      * @param strToSplit - the line value (i18n key, ActionNames ID, icon path, optional icon pressed path)
      * @throws IllegalArgumentException if error in parsing.
      */
     IconToolbarBean(final String strToSplit, final String iconSize) throws IllegalArgumentException {
         if (strToSplit == null) {
             throw new IllegalArgumentException("Icon definition must not be null"); //$NON-NLS-1$
         }
         final String[] tmp = strToSplit.split(ICON_FIELD_SEP);
         if (tmp.length > 2) {
             this.i18nKey = tmp[0];
             this.actionName = tmp[1];
             this.iconPath = tmp[2].replace("<SIZE>", iconSize); //$NON-NLS-1$
             this.iconPathPressed = (tmp.length > 3) ? tmp[3].replace("<SIZE>", iconSize) : this.iconPath; //$NON-NLS-1$
         } else {
             throw new IllegalArgumentException("Incorrect argument format - expected at least 2 fields separated by " + ICON_FIELD_SEP);
         }
     }
 
     /**
      * Resolve action name ID declared in icon set file to ActionNames value
      * @return the resolve actionName
      */
     public String getActionNameResolve() {
         final String aName;
         try {
             aName = (String) (ActionNames.class.getField(this.actionName).get(null));
         } catch (Exception e) {
-            log.warn("Toolbar icon Action names error: " + this.actionName + ", use unknown action."); //$NON-NLS-1$
+            log.warn("Toolbar icon Action names error: {}, use unknown action.", this.actionName); //$NON-NLS-1$
             return this.actionName; // return unknown action names for display error msg
         }
         return aName;
     }
     
     /**
      * @return the i18nKey
      */
     public String getI18nKey() {
         return i18nKey;
     }
 
     /**
      * @return the actionName
      */
     public String getActionName() {
         return actionName;
     }
 
     /**
      * @return the iconPath
      */
     public String getIconPath() {
         return iconPath;
     }
 
     /**
      * @return the iconPathPressed
      */
     public String getIconPathPressed() {
         return iconPathPressed;
     }
 
 }
diff --git a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
index 033815eb1..f6e2906ed 100644
--- a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
+++ b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
@@ -1,841 +1,845 @@
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
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class JMeterMenuBar extends JMenuBar implements LocaleChangeListener {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JMeterMenuBar.class);
 
     private JMenu fileMenu;
 
     private JMenuItem fileSaveAs;
 
     private JMenuItem fileSelectionAs;
 
     private JMenuItem fileSelectionAsTestFragment;
 
     private JMenuItem fileRevert;
 
     private JMenuItem fileLoad;
 
     private JMenuItem templates;
 
     private List<JComponent> fileLoadRecentFiles;
 
     private JMenuItem fileMerge;
 
     private JMenuItem fileExit;
 
     private JMenuItem fileNew;
 
     private JMenu editMenu;
 
     private JMenu editAdd;
 
     private JMenu runMenu;
 
     private JMenuItem runStart;
 
     private JMenuItem runStartNoTimers;
 
     private JMenu remoteStart;
 
     private JMenuItem remoteStartAll;
 
     private Collection<JMenuItem> remoteEngineStart;
 
     private JMenuItem runStop;
 
     private JMenuItem runShut;
 
     private JMenu remoteStop;
 
     private JMenu remoteShut;
 
     private JMenuItem remoteStopAll;
 
     private JMenuItem remoteShutAll;
 
     private Collection<JMenuItem> remoteEngineStop;
 
     private Collection<JMenuItem> remoteEngineShut;
 
     private JMenuItem runClear;
 
     private JMenuItem runClearAll;
 
     private JMenu optionsMenu;
 
     private JMenu lafMenu;
 
     private JMenuItem sslManager;
 
     private JMenu helpMenu;
 
     private JMenuItem helpAbout;
 
     private String[] remoteHosts;
 
     private JMenu remoteExit;
 
     private JMenuItem remoteExitAll;
 
     private Collection<JMenuItem> remoteEngineExit;
 
     private JMenu searchMenu;
 
     private ArrayList<MenuCreator> menuCreators;
 
     public static final String SYSTEM_LAF = "System"; // $NON-NLS-1$
 
     public static final String CROSS_PLATFORM_LAF = "CrossPlatform"; // $NON-NLS-1$
 
     public JMeterMenuBar() {
         // List for recent files menu items
         fileLoadRecentFiles = new LinkedList<>();
         // Lists for remote engines menu items
         remoteEngineStart = new LinkedList<>();
         remoteEngineStop = new LinkedList<>();
         remoteEngineShut = new LinkedList<>();
         remoteEngineExit = new LinkedList<>();
         remoteHosts = JOrphanUtils.split(JMeterUtils.getPropDefault("remote_hosts", ""), ","); //$NON-NLS-1$
         if (remoteHosts.length == 1 && remoteHosts[0].isEmpty()) {
             remoteHosts = new String[0];
         }
         this.getRemoteItems();
         createMenuBar();
         JMeterUtils.addLocaleChangeListener(this);
     }
 
     public void setFileSaveEnabled(boolean enabled) {
         if(fileSaveAs != null) {
             fileSaveAs.setEnabled(enabled);
         }
     }
 
     public void setFileLoadEnabled(boolean enabled) {
         if (fileLoad != null) {
             fileLoad.setEnabled(enabled);
         }
         if (fileMerge != null) {
             fileMerge.setEnabled(enabled);
         }
     }
 
     public void setFileRevertEnabled(boolean enabled) {
         if(fileRevert != null) {
             fileRevert.setEnabled(enabled);
         }
     }
 
     public void setProjectFileLoaded(String file) {
         if(fileLoadRecentFiles != null && file != null) {
             LoadRecentProject.updateRecentFileMenuItems(fileLoadRecentFiles, file);
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
         if (editAdd != null) {
             editMenu.remove(editAdd);
         }
         // Insert the Add menu as the first menu item in the Edit menu.
         editAdd = menu;
         editMenu.insert(editAdd, 0);
     }
 
     // Called by MainFrame#setEditMenu() which is called by EditCommand#doAction and GuiPackage#localeChanged
     public void setEditMenu(JPopupMenu menu) {
         if (menu != null) {
             editMenu.removeAll();
             Component[] comps = menu.getComponents();
             for (Component comp : comps) {
                 editMenu.add(comp);
             }
             editMenu.setEnabled(true);
         } else {
             editMenu.setEnabled(false);
         }
     }
 
     public void setEditAddEnabled(boolean enabled) {
         // There was a NPE being thrown without the null check here.. JKB
         if (editAdd != null) {
             editAdd.setEnabled(enabled);
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
         this.menuCreators = new ArrayList<>();
         try {
             List<String> listClasses = ClassFinder.findClassesThatExtend(
                     JMeterUtils.getSearchPaths(),
                     new Class[] {MenuCreator.class });
             for (String strClassName : listClasses) {
                 try {
-                    if(log.isDebugEnabled()) {
-                        log.debug("Loading menu creator class: "+ strClassName);
-                    }
+                    log.debug("Loading menu creator class: {}", strClassName);
                     Class<?> commandClass = Class.forName(strClassName);
                     if (!Modifier.isAbstract(commandClass.getModifiers())) {
-                        if(log.isDebugEnabled()) {
-                            log.debug("Instantiating: "+ commandClass.getName());
-                        }
+                        log.debug("Instantiating: {}", commandClass);
                         MenuCreator creator = (MenuCreator) commandClass.newInstance();
                         menuCreators.add(creator);
                     }
                 } catch (Exception e) {
-                    log.error("Exception registering "+MenuCreator.class.getName() + " with implementation:"+strClassName, e);
+                    log.error("Exception registering {} with implementation: {}", MenuCreator.class, strClassName, e);
                 }
             }
         } catch (IOException e) {
-            log.error("Exception finding implementations of "+MenuCreator.class, e);
+            log.error("Exception finding implementations of {}", MenuCreator.class, e);
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
         for (MenuCreator menuCreator : menuCreators) {
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
 
         helpAbout = makeMenuItemRes("about", 'A', ActionNames.ABOUT); //$NON-NLS-1$
 
         helpMenu.add(contextHelp);
         helpMenu.addSeparator();
         helpMenu.add(whatClass);
         helpMenu.add(setDebug);
         helpMenu.add(resetDebug);
         helpMenu.add(heapDump);
 
         addPluginsMenuItems(helpMenu, menuCreators, MENU_LOCATION.HELP);
 
         helpMenu.addSeparator();
         helpMenu.add(helpAbout);
     }
 
     private void makeOptionsMenu() {
         // OPTIONS MENU
         optionsMenu = makeMenuRes("option",'O'); //$NON-NLS-1$
         JMenuItem functionHelper = makeMenuItemRes("function_dialog_menu_item", 'F', ActionNames.FUNCTIONS, KeyStrokes.FUNCTIONS); //$NON-NLS-1$
 
         lafMenu = makeMenuRes("appearance",'L'); //$NON-NLS-1$
         for (LookAndFeelInfo laf : getAllLAFs()) {
             JMenuItem menuItem = new JMenuItem(laf.getName());
             menuItem.addActionListener(ActionRouter.getInstance());
             menuItem.setActionCommand(ActionNames.LAF_PREFIX + laf.getClassName());
             menuItem.setToolTipText(laf.getClassName()); // show the classname to the user
             lafMenu.add(menuItem);
         }
         optionsMenu.add(functionHelper);
         optionsMenu.add(lafMenu);
 
         JCheckBoxMenuItem menuLoggerPanel = makeCheckBoxMenuItemRes("menu_logger_panel", ActionNames.LOGGER_PANEL_ENABLE_DISABLE); //$NON-NLS-1$
         GuiPackage guiInstance = GuiPackage.getInstance();
         if (guiInstance != null) { //avoid error in ant task tests (good way?)
             guiInstance.setMenuItemLoggerPanel(menuLoggerPanel);
         }
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
 
         JMenuItem zoomIn = makeMenuItemRes("menu_zoom_in", ActionNames.ZOOM_IN); //$NON-NLS-1$
         optionsMenu.add(zoomIn);
         JMenuItem zoomOut = makeMenuItemRes("menu_zoom_out", ActionNames.ZOOM_OUT); //$NON-NLS-1$
         optionsMenu.add(zoomOut);
 
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
         List<String> lang = new ArrayList<>(20);
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
-                log.info("Adding locale "+newLang);
+                log.info("Adding locale {}", newLang);
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
 
         runStart = makeMenuItemRes("start", 'S', ActionNames.ACTION_START, KeyStrokes.ACTION_START); //$NON-NLS-1$
 
         runStartNoTimers = makeMenuItemRes("start_no_timers", ActionNames.ACTION_START_NO_TIMERS); //$NON-NLS-1$
         
         runStop = makeMenuItemRes("stop", 'T', ActionNames.ACTION_STOP, KeyStrokes.ACTION_STOP); //$NON-NLS-1$
         runStop.setEnabled(false);
 
         runShut = makeMenuItemRes("shutdown", 'Y', ActionNames.ACTION_SHUTDOWN, KeyStrokes.ACTION_SHUTDOWN); //$NON-NLS-1$
         runShut.setEnabled(false);
 
         runClear = makeMenuItemRes("clear", 'C', ActionNames.CLEAR, KeyStrokes.CLEAR); //$NON-NLS-1$
 
         runClearAll = makeMenuItemRes("clear_all", 'a', ActionNames.CLEAR_ALL, KeyStrokes.CLEAR_ALL); //$NON-NLS-1$
 
         runMenu.add(runStart);
         runMenu.add(runStartNoTimers);
         if (remoteStart != null) {
             runMenu.add(remoteStart);
         }
         remoteStartAll = makeMenuItemRes("remote_start_all", ActionNames.REMOTE_START_ALL, KeyStrokes.REMOTE_START_ALL); //$NON-NLS-1$
 
         runMenu.add(remoteStartAll);
         runMenu.add(runStop);
         runMenu.add(runShut);
         if (remoteStop != null) {
             runMenu.add(remoteStop);
         }
         remoteStopAll = makeMenuItemRes("remote_stop_all", 'X', ActionNames.REMOTE_STOP_ALL, KeyStrokes.REMOTE_STOP_ALL); //$NON-NLS-1$
         runMenu.add(remoteStopAll);
 
         if (remoteShut != null) {
             runMenu.add(remoteShut);
         }
         remoteShutAll = makeMenuItemRes("remote_shut_all", 'X', ActionNames.REMOTE_SHUT_ALL, KeyStrokes.REMOTE_SHUT_ALL); //$NON-NLS-1$
         runMenu.add(remoteShutAll);
 
         if (remoteExit != null) {
             runMenu.add(remoteExit);
         }
         remoteExitAll = makeMenuItemRes("remote_exit_all", ActionNames.REMOTE_EXIT_ALL); //$NON-NLS-1$
         runMenu.add(remoteExitAll);
 
         runMenu.addSeparator();
         runMenu.add(runClear);
         runMenu.add(runClearAll);
 
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
 
         JMenuItem fileSave = makeMenuItemRes("save", 'S', ActionNames.SAVE, KeyStrokes.SAVE); //$NON-NLS-1$
         fileSave.setEnabled(true);
 
         fileSaveAs = makeMenuItemRes("save_all_as", 'A', ActionNames.SAVE_ALL_AS, KeyStrokes.SAVE_ALL_AS); //$NON-NLS-1$
         fileSaveAs.setEnabled(true);
 
         fileSelectionAs = makeMenuItemRes("save_as", ActionNames.SAVE_AS); //$NON-NLS-1$
         fileSelectionAs.setEnabled(true);
 
         fileSelectionAsTestFragment = makeMenuItemRes("save_as_test_fragment", ActionNames.SAVE_AS_TEST_FRAGMENT); //$NON-NLS-1$
         fileSelectionAsTestFragment.setEnabled(true);
 
         fileRevert = makeMenuItemRes("revert_project", 'R', ActionNames.REVERT_PROJECT); //$NON-NLS-1$
         fileRevert.setEnabled(false);
 
         fileLoad = makeMenuItemRes("menu_open", 'O', ActionNames.OPEN, KeyStrokes.OPEN); //$NON-NLS-1$
         // Set default SAVE menu item to disabled since the default node that
         // is selected is ROOT, which does not allow items to be inserted.
         fileLoad.setEnabled(false);
 
         templates = makeMenuItemRes("template_menu", 'T', ActionNames.TEMPLATES); //$NON-NLS-1$
         templates.setEnabled(true);
 
         fileNew = makeMenuItemRes("new", 'N', ActionNames.CLOSE, KeyStrokes.CLOSE); //$NON-NLS-1$
 
         fileExit = makeMenuItemRes("exit", 'X', ActionNames.EXIT, KeyStrokes.EXIT); //$NON-NLS-1$
 
         fileMerge = makeMenuItemRes("menu_merge", 'M', ActionNames.MERGE); //$NON-NLS-1$
         fileMerge.setEnabled(false);
 
         fileMenu.add(fileNew);
         fileMenu.add(templates);
         fileMenu.add(fileLoad);
         fileMenu.add(fileMerge);
         fileMenu.addSeparator();
         fileMenu.add(fileSave);
         fileMenu.add(fileSaveAs);
         fileMenu.add(fileSelectionAs);
         fileMenu.add(fileSelectionAsTestFragment);
         fileMenu.add(fileRevert);
         fileMenu.addSeparator();
         // Add the recent files, which will also add a separator that is
         // visible when needed
         fileLoadRecentFiles = LoadRecentProject.getRecentFileMenuItems();
         for(JComponent jc : fileLoadRecentFiles){
             fileMenu.add(jc);
         }
 
         addPluginsMenuItems(fileMenu, menuCreators, MENU_LOCATION.FILE);
 
         fileMenu.add(fileExit);
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
-        log.info("setRunning(" + running + "," + host + ")");
+        log.info("setRunning({}, {})", running, host);
         if(org.apache.jmeter.gui.MainFrame.LOCAL.equals(host)) {
             return;
         }
         Iterator<JMenuItem> iter = remoteEngineStart.iterator();
         Iterator<JMenuItem> iter2 = remoteEngineStop.iterator();
         Iterator<JMenuItem> iter3 = remoteEngineExit.iterator();
         Iterator<JMenuItem> iter4 = remoteEngineShut.iterator();
         while (iter.hasNext() && iter2.hasNext() && iter3.hasNext() &&iter4.hasNext()) {
             JMenuItem start = iter.next();
             JMenuItem stop = iter2.next();
             JMenuItem exit = iter3.next();
             JMenuItem shut = iter4.next();
             if (start.getText().equals(host)) {
-                log.debug("Found start host: " + start.getText());
+                if (log.isDebugEnabled()) {
+                    log.debug("Found start host: {}", start.getText());
+                }
                 start.setEnabled(!running);
             }
             if (stop.getText().equals(host)) {
-                log.debug("Found stop  host: " + stop.getText());
+                if (log.isDebugEnabled()) {
+                    log.debug("Found stop  host: {}", stop.getText());
+                }
                 stop.setEnabled(running);
             }
             if (exit.getText().equals(host)) {
-                log.debug("Found exit  host: " + exit.getText());
+                if (log.isDebugEnabled()) {
+                    log.debug("Found exit  host: {}", exit.getText());
+                }
                 exit.setEnabled(true);
             }
             if (shut.getText().equals(host)) {
-                log.debug("Found exit  host: " + exit.getText());
+                if (log.isDebugEnabled()) {
+                    log.debug("Found shut  host: {}", exit.getText());
+                }
                 shut.setEnabled(running);
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void setEnabled(boolean enable) {
         runStart.setEnabled(!enable);
         runStartNoTimers.setEnabled(!enable);
         runStop.setEnabled(enable);
         runShut.setEnabled(enable);
     }
 
     private void getRemoteItems() {
         if (remoteHosts.length > 0) {
             remoteStart = makeMenuRes("remote_start"); //$NON-NLS-1$
             remoteStop = makeMenuRes("remote_stop"); //$NON-NLS-1$
             remoteShut = makeMenuRes("remote_shut"); //$NON-NLS-1$
             remoteExit = makeMenuRes("remote_exit"); //$NON-NLS-1$
 
             for (int i = 0; i < remoteHosts.length; i++) {
                 remoteHosts[i] = remoteHosts[i].trim();
 
                 JMenuItem item = makeMenuItemNoRes(remoteHosts[i], ActionNames.REMOTE_START);
                 remoteEngineStart.add(item);
                 remoteStart.add(item);
 
                 item = makeMenuItemNoRes(remoteHosts[i], ActionNames.REMOTE_STOP);
                 item.setEnabled(false);
                 remoteEngineStop.add(item);
                 remoteStop.add(item);
 
                 item = makeMenuItemNoRes(remoteHosts[i], ActionNames.REMOTE_SHUT);
                 item.setEnabled(false);
                 remoteEngineShut.add(item);
                 remoteShut.add(item);
 
                 item = makeMenuItemNoRes(remoteHosts[i],ActionNames.REMOTE_EXIT);
                 item.setEnabled(false);
                 remoteEngineExit.add(item);
                 remoteExit.add(item);
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
      * 
      * @return The list of available {@link LookAndFeelInfo}s
      */
     // This is also used by LookAndFeelCommand
     public static LookAndFeelInfo[] getAllLAFs() {
         UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
         int i = lafs.length;
         UIManager.LookAndFeelInfo[] lafsAll = new UIManager.LookAndFeelInfo[i+2];
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
 
         for (MenuElement subElement : menu.getSubElements()) {
             updateMenuElement(subElement);
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
index 65e908057..85342de2e 100644
--- a/src/core/org/apache/jmeter/gui/util/JMeterToolBar.java
+++ b/src/core/org/apache/jmeter/gui/util/JMeterToolBar.java
@@ -1,297 +1,299 @@
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
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * The JMeter main toolbar class
  *
  */
 public class JMeterToolBar extends JToolBar implements LocaleChangeListener {
     
     /**
      * 
      */
-    private static final long serialVersionUID = -4591210341986068907L;
+    private static final long serialVersionUID = 1L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JMeterToolBar.class);
 
     private static final String TOOLBAR_ENTRY_SEP = ",";  //$NON-NLS-1$
 
     private static final String TOOLBAR_PROP_NAME = "toolbar"; //$NON-NLS-1$
 
     // protected fields: JMeterToolBar class can be use to create another toolbar (plugin, etc.)    
     protected static final String DEFAULT_TOOLBAR_PROPERTY_FILE = "org/apache/jmeter/images/toolbar/icons-toolbar.properties"; //$NON-NLS-1$
 
     protected static final String USER_DEFINED_TOOLBAR_PROPERTY_FILE = "jmeter.toolbar.icons"; //$NON-NLS-1$
 
     public static final String TOOLBAR_ICON_SIZE = "jmeter.toolbar.icons.size"; //$NON-NLS-1$
 
     public static final String DEFAULT_TOOLBAR_ICON_SIZE = "22x22"; //$NON-NLS-1$
     
     protected static final String TOOLBAR_LIST = "jmeter.toolbar";
     
     /**
      * Create the default JMeter toolbar
      * 
      * @param visible
      *            Flag whether toolbar should be visible
      * @return the newly created {@link JMeterToolBar}
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
                     try {
                         toolBar.add(makeButtonItemRes(iconToolbarBean));
                     } catch (Exception e) {
-                        log.warn(e.getMessage());
+                        if (log.isWarnEnabled()) {
+                            log.warn("Exception while adding button item to toolbar. {}", e.getMessage());
+                        }
                     }
                 }
             }
             toolBar.initButtonsState();
         }
     }
     
     /**
      * Generate a button component from icon bean
      * @param iconBean contains I18N key, ActionNames, icon path, optional icon path pressed
      * @return a button for toolbar
      */
     private static JButton makeButtonItemRes(IconToolbarBean iconBean) throws Exception {
         final URL imageURL = JMeterUtils.class.getClassLoader().getResource(iconBean.getIconPath());
         if (imageURL == null) {
             throw new Exception("No icon for: " + iconBean.getActionName());
         }
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
             p = defaultProps;
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
         
         String iconSize = JMeterUtils.getPropDefault(TOOLBAR_ICON_SIZE, DEFAULT_TOOLBAR_ICON_SIZE); 
 
         List<IconToolbarBean> listIcons = new ArrayList<>();
         for (String key : oList) {
-            log.debug("Toolbar icon key: " + key); //$NON-NLS-1$
+            log.debug("Toolbar icon key: {}", key); //$NON-NLS-1$
             String trimmed = key.trim();
             if (trimmed.equals("|")) { //$NON-NLS-1$
                 listIcons.add(null);
             } else {
                 String property = p.getProperty(trimmed);
                 if (property == null) {
-                    log.warn("No definition for toolbar entry: " + key);
+                    log.warn("No definition for toolbar entry: {}", key);
                 } else {
                     try {
                         IconToolbarBean itb = new IconToolbarBean(property, iconSize);
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
         Map<String, Boolean> currentButtonStates = getCurrentButtonsStates();
         this.removeAll();
         setupToolbarContent(this);
         updateButtons(currentButtonStates);
     }
 
     /**
      * 
      * @return Current state (enabled/disabled) of Toolbar button
      */
     private Map<String, Boolean> getCurrentButtonsStates() {
         Component[] components = getComponents();
         Map<String, Boolean> buttonStates = new HashMap<>(components.length);
         for (Component component : components) {
             if (component instanceof JButton) {
                 JButton button = (JButton) component;
                 buttonStates.put(button.getActionCommand(), Boolean.valueOf(button.isEnabled()));
             }
         }
         return buttonStates;
     }
 
     /**
      * Init the state of buttons
      */
     public void initButtonsState() {
         Map<String, Boolean> buttonStates = new HashMap<>();
         buttonStates.put(ActionNames.ACTION_START, Boolean.TRUE);
         buttonStates.put(ActionNames.ACTION_START_NO_TIMERS, Boolean.TRUE);
         buttonStates.put(ActionNames.ACTION_STOP, Boolean.FALSE);
         buttonStates.put(ActionNames.ACTION_SHUTDOWN, Boolean.FALSE);
         buttonStates.put(ActionNames.UNDO, Boolean.FALSE);
         buttonStates.put(ActionNames.REDO, Boolean.FALSE);
         buttonStates.put(ActionNames.REMOTE_START_ALL, Boolean.TRUE);
         buttonStates.put(ActionNames.REMOTE_STOP_ALL, Boolean.FALSE);
         buttonStates.put(ActionNames.REMOTE_SHUT_ALL, Boolean.FALSE);
         updateButtons(buttonStates);
     }
     
     /**
      * Change state of buttons on local test
      * 
      * @param started
      *            Flag whether local test is started
      */
     public void setLocalTestStarted(boolean started) {
         Map<String, Boolean> buttonStates = new HashMap<>(3);
         buttonStates.put(ActionNames.ACTION_START, Boolean.valueOf(!started));
         buttonStates.put(ActionNames.ACTION_START_NO_TIMERS, Boolean.valueOf(!started));
         buttonStates.put(ActionNames.ACTION_STOP, Boolean.valueOf(started));
         buttonStates.put(ActionNames.ACTION_SHUTDOWN, Boolean.valueOf(started));
         updateButtons(buttonStates);
     }
     
     /**
      * Change state of buttons on remote test
      * 
      * @param started
      *            Flag whether the test is started
      */
     public void setRemoteTestStarted(boolean started) {
         Map<String, Boolean> buttonStates = new HashMap<>(3);
         buttonStates.put(ActionNames.REMOTE_START_ALL, Boolean.valueOf(!started));
         buttonStates.put(ActionNames.REMOTE_STOP_ALL, Boolean.valueOf(started));
         buttonStates.put(ActionNames.REMOTE_SHUT_ALL, Boolean.valueOf(started));
         updateButtons(buttonStates);
     }
 
     /**
      * Change state of buttons after undo or redo
      * 
      * @param canUndo
      *            Flag whether the button corresponding to
      *            {@link ActionNames#UNDO} should be enabled
      * @param canRedo
      *            Flag whether the button corresponding to
      *            {@link ActionNames#REDO} should be enabled
      */
     public void updateUndoRedoIcons(boolean canUndo, boolean canRedo) {
         Map<String, Boolean> buttonStates = new HashMap<>(2);
         buttonStates.put(ActionNames.UNDO, Boolean.valueOf(canUndo));
         buttonStates.put(ActionNames.REDO, Boolean.valueOf(canRedo));
         updateButtons(buttonStates);
     }
 
     /**
      * Set buttons to a given state
      * 
      * @param buttonStates
      *            {@link Map} of button names and their states
      */
     private void updateButtons(Map<String, Boolean> buttonStates) {
         for (Component component : getComponents()) {
             if (component instanceof JButton) {
                 JButton button = (JButton) component;
                 Boolean enabled = buttonStates.get(button.getActionCommand());
                 if (enabled != null) {
                     button.setEnabled(enabled.booleanValue());
                 }
             }
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/JSyntaxTextArea.java b/src/core/org/apache/jmeter/gui/util/JSyntaxTextArea.java
index 0c024089e..4ffeedf56 100644
--- a/src/core/org/apache/jmeter/gui/util/JSyntaxTextArea.java
+++ b/src/core/org/apache/jmeter/gui/util/JSyntaxTextArea.java
@@ -1,244 +1,244 @@
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
 
 import java.awt.Font;
 import java.awt.HeadlessException;
 import java.util.Properties;
 
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
 import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
 import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
 import org.fife.ui.rtextarea.RUndoManager;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Utility class to handle RSyntaxTextArea code
  * It's not currently possible to instantiate the RSyntaxTextArea class when running headless.
  * So we use getInstance methods to create the class and allow for headless testing.
  */
 public class JSyntaxTextArea extends RSyntaxTextArea {
 
-    private static final long serialVersionUID = 210L;
+    private static final long serialVersionUID = 211L;
 
     private final Properties languageProperties = JMeterUtils.loadProperties("org/apache/jmeter/gui/util/textarea.properties"); //$NON-NLS-1$
 
     private final boolean disableUndo;
     private static final boolean WRAP_STYLE_WORD = JMeterUtils.getPropDefault("jsyntaxtextarea.wrapstyleword", true);
     private static final boolean LINE_WRAP       = JMeterUtils.getPropDefault("jsyntaxtextarea.linewrap", true);
     private static final boolean CODE_FOLDING    = JMeterUtils.getPropDefault("jsyntaxtextarea.codefolding", true);
     private static final int MAX_UNDOS           = JMeterUtils.getPropDefault("jsyntaxtextarea.maxundos", 50);
     private static final String USER_FONT_FAMILY = JMeterUtils.getPropDefault("jsyntaxtextarea.font.family", null);
     private static final int USER_FONT_SIZE      = JMeterUtils.getPropDefault("jsyntaxtextarea.font.size", -1);
-    private static final Logger log              = LoggingManager.getLoggerForClass();
+    private static final Logger log              = LoggerFactory.getLogger(JSyntaxTextArea.class);
 
     /**
      * Creates the default syntax highlighting text area. The following are set:
      * <ul>
      * <li>setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA)</li>
      * <li>setCodeFoldingEnabled(true)</li>
      * <li>setAntiAliasingEnabled(true)</li>
      * <li>setLineWrap(true)</li>
      * <li>setWrapStyleWord(true)</li>
      * </ul>
      * 
      * @param rows
      *            The number of rows for the text area
      * @param cols
      *            The number of columns for the text area
      * @param disableUndo
      *            true to disable undo manager
      * @return {@link JSyntaxTextArea}
      */
     public static JSyntaxTextArea getInstance(int rows, int cols, boolean disableUndo) {
         try {
             return new JSyntaxTextArea(rows, cols, disableUndo);
         } catch (HeadlessException e) {
             // Allow override for unit testing only
             if ("true".equals(System.getProperty("java.awt.headless"))) { // $NON-NLS-1$ $NON-NLS-2$
                 return new JSyntaxTextArea(disableUndo) {
                     private static final long serialVersionUID = 1L;
                     @Override
                     protected void init() {
                         try {
                             super.init();
                         } catch (HeadlessException|NullPointerException e) {
                             // ignored
                         }
                     }
                     // Override methods that would fail
                     @Override
                     public void setCodeFoldingEnabled(boolean b) {  }
                     @Override
                     public void setCaretPosition(int b) { }
                     @Override
                     public void discardAllEdits() { }
                     @Override
                     public void setText(String t) { }
                     @Override
                     public boolean isCodeFoldingEnabled(){ return true; }
                 };
             } else {
                 throw e;
             }
         }
     }
 
     /**
      * Creates the default syntax highlighting text area. The following are set:
      * <ul>
      * <li>setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA)</li>
      * <li>setCodeFoldingEnabled(true)</li>
      * <li>setAntiAliasingEnabled(true)</li>
      * <li>setLineWrap(true)</li>
      * <li>setWrapStyleWord(true)</li>
      * </ul>
      * 
      * @param rows
      *            The number of rows for the text area
      * @param cols
      *            The number of columns for the text area
      * @return {@link JSyntaxTextArea}
      */
     public static JSyntaxTextArea getInstance(int rows, int cols) {
         return getInstance(rows, cols, false);
     }
 
     @Deprecated
     public JSyntaxTextArea() {
         // For use by test code only
         this(30, 50, false);
     }
 
     // for use by headless tests only
     private JSyntaxTextArea(boolean dummy) {
         disableUndo = dummy;
     }
 
     /**
      * Creates the default syntax highlighting text area. The following are set:
      * <ul>
      * <li>setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA)</li>
      * <li>setCodeFoldingEnabled(true)</li>
      * <li>setAntiAliasingEnabled(true)</li>
      * <li>setLineWrap(true)</li>
      * <li>setWrapStyleWord(true)</li>
      * </ul>
      * 
      * @param rows
      *            The number of rows for the text area
      * @param cols
      *            The number of columns for the text area
      * @deprecated use {@link #getInstance(int, int)} instead
      */
     @Deprecated
     public JSyntaxTextArea(int rows, int cols) {
         this(rows, cols, false);
     }
 
     /**
      * Creates the default syntax highlighting text area. The following are set:
      * <ul>
      * <li>setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA)</li>
      * <li>setCodeFoldingEnabled(true)</li>
      * <li>setAntiAliasingEnabled(true)</li>
      * <li>setLineWrap(true)</li>
      * <li>setWrapStyleWord(true)</li>
      * </ul>
      * 
      * @param rows
      *            The number of rows for the text area
      * @param cols
      *            The number of columns for the text area
      * @param disableUndo
      *            true to disable undo manager, defaults to false
      * @deprecated use {@link #getInstance(int, int, boolean)} instead
      */
     @Deprecated
     public JSyntaxTextArea(int rows, int cols, boolean disableUndo) {
         super(rows, cols);
         super.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
         super.setCodeFoldingEnabled(CODE_FOLDING);
         super.setAntiAliasingEnabled(true);
         super.setLineWrap(LINE_WRAP);
         super.setWrapStyleWord(WRAP_STYLE_WORD);
         this.disableUndo = disableUndo;
         if (USER_FONT_FAMILY != null) {
             int fontSize = USER_FONT_SIZE > 0 ? USER_FONT_SIZE : getFont().getSize();
             setFont(new Font(USER_FONT_FAMILY, Font.PLAIN, fontSize));
             if (log.isDebugEnabled()) {
-                log.debug("Font is set to: " + getFont());
+                log.debug("Font is set to: {}", getFont());
             }
         }
         if(disableUndo) {
             // We need to do this to force recreation of undoManager which
             // will use the disableUndo otherwise it would always be false
             // See BUG 57440
             discardAllEdits();
         }
     }
 
     /**
      * Sets the language of the text area.
      * 
      * @param language
      *            The language to be set
      */
     public void setLanguage(String language) {
         if(language == null) {
           // TODO: Log a message?
           // But how to find the name of the offending GUI element in the case of a TestBean?
           super.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
         } else {
           final String style = languageProperties.getProperty(language);
           if (style == null) {
               super.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
           } else {
               super.setSyntaxEditingStyle(style);
           }
         }
     }
 
     /**
      * Override UndoManager to allow disabling if feature causes issues
      * See <a href="https://github.com/bobbylight/RSyntaxTextArea/issues/19">Issue 19 on RSyntaxTextArea</a>
      */
     @Override
     protected RUndoManager createUndoManager() {
         RUndoManager undoManager = super.createUndoManager();
         if(disableUndo) {
             undoManager.setLimit(0);
         } else {
             undoManager.setLimit(MAX_UNDOS);
         }
         return undoManager;
     }
 
     /**
      * Sets initial text resetting undo history
      * 
      * @param string
      *            The initial text to be set
      */
     public void setInitialText(String string) {
         setText(string);
         discardAllEdits();
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/MenuFactory.java b/src/core/org/apache/jmeter/gui/util/MenuFactory.java
index fff174bee..d2d3e3110 100644
--- a/src/core/org/apache/jmeter/gui/util/MenuFactory.java
+++ b/src/core/org/apache/jmeter/gui/util/MenuFactory.java
@@ -1,753 +1,754 @@
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
 import java.awt.HeadlessException;
 import java.io.IOException;
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.Set;
 
 import javax.swing.JMenu;
 import javax.swing.JMenuItem;
 import javax.swing.JPopupMenu;
 import javax.swing.KeyStroke;
 import javax.swing.MenuElement;
 
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.JMeterGUIComponent;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testbeans.gui.TestBeanGUI;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.Printable;
 import org.apache.jorphan.gui.GuiUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public final class MenuFactory {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(MenuFactory.class);
 
     /*
      *  Predefined strings for makeMenu().
      *  These are used as menu categories in the menuMap Hashmap,
      *  and also for resource lookup in messages.properties
     */
     public static final String THREADS = "menu_threads"; //$NON-NLS-1$
     
     public static final String FRAGMENTS = "menu_fragments"; //$NON-NLS-1$
 
     public static final String TIMERS = "menu_timer"; //$NON-NLS-1$
 
     public static final String CONTROLLERS = "menu_logic_controller"; //$NON-NLS-1$
 
     public static final String SAMPLERS = "menu_generative_controller"; //$NON-NLS-1$
 
     public static final String CONFIG_ELEMENTS = "menu_config_element"; //$NON-NLS-1$
 
     public static final String POST_PROCESSORS = "menu_post_processors"; //$NON-NLS-1$
 
     public static final String PRE_PROCESSORS = "menu_pre_processors"; //$NON-NLS-1$
 
     public static final String ASSERTIONS = "menu_assertions"; //$NON-NLS-1$
 
     public static final String NON_TEST_ELEMENTS = "menu_non_test_elements"; //$NON-NLS-1$
 
     public static final String LISTENERS = "menu_listener"; //$NON-NLS-1$
 
     private static final Map<String, List<MenuInfo>> menuMap = new HashMap<>();
 
     private static final Set<String> elementsToSkip = new HashSet<>();
 
     // MENU_ADD_xxx - controls which items are in the ADD menu
     // MENU_PARENT_xxx - controls which items are in the Insert Parent menu
     private static final String[] MENU_ADD_CONTROLLER = new String[] {
         MenuFactory.CONTROLLERS,
         MenuFactory.CONFIG_ELEMENTS,
         MenuFactory.TIMERS,
         MenuFactory.PRE_PROCESSORS,
         MenuFactory.SAMPLERS,
         MenuFactory.POST_PROCESSORS,
         MenuFactory.ASSERTIONS,
         MenuFactory.LISTENERS,
         };
 
     private static final String[] MENU_PARENT_CONTROLLER = new String[] {
         MenuFactory.CONTROLLERS };
 
     private static final String[] MENU_ADD_SAMPLER = new String[] {
         MenuFactory.CONFIG_ELEMENTS,
         MenuFactory.TIMERS,
         MenuFactory.PRE_PROCESSORS,
         MenuFactory.POST_PROCESSORS,
         MenuFactory.ASSERTIONS,
         MenuFactory.LISTENERS,
         };
 
     private static final String[] MENU_PARENT_SAMPLER = new String[] {
         MenuFactory.CONTROLLERS };
 
     private static final List<MenuInfo> timers;
     private static final List<MenuInfo> controllers;
     private static final List<MenuInfo> samplers;
     private static final List<MenuInfo> threads;
     private static final List<MenuInfo> fragments;
     private static final List<MenuInfo> configElements;
     private static final List<MenuInfo> assertions;
     private static final List<MenuInfo> listeners;
     private static final List<MenuInfo> nonTestElements;
     private static final List<MenuInfo> postProcessors;
     private static final List<MenuInfo> preProcessors;
 
     static {
         threads = new LinkedList<>();
         fragments = new LinkedList<>();
         timers = new LinkedList<>();
         controllers = new LinkedList<>();
         samplers = new LinkedList<>();
         configElements = new LinkedList<>();
         assertions = new LinkedList<>();
         listeners = new LinkedList<>();
         postProcessors = new LinkedList<>();
         preProcessors = new LinkedList<>();
         nonTestElements = new LinkedList<>();
         menuMap.put(THREADS, threads);
         menuMap.put(FRAGMENTS, fragments);
         menuMap.put(TIMERS, timers);
         menuMap.put(ASSERTIONS, assertions);
         menuMap.put(CONFIG_ELEMENTS, configElements);
         menuMap.put(CONTROLLERS, controllers);
         menuMap.put(LISTENERS, listeners);
         menuMap.put(NON_TEST_ELEMENTS, nonTestElements);
         menuMap.put(SAMPLERS, samplers);
         menuMap.put(POST_PROCESSORS, postProcessors);
         menuMap.put(PRE_PROCESSORS, preProcessors);
         try {
             String[] classesToSkip =
                 JOrphanUtils.split(JMeterUtils.getPropDefault("not_in_menu", ""), ","); //$NON-NLS-1$
             for (String aClassesToSkip : classesToSkip) {
                 elementsToSkip.add(aClassesToSkip.trim());
             }
 
             initializeMenus();
             sortPluginMenus();
         } catch (Error | RuntimeException ex) { // NOSONAR We want to log Errors in jmeter.log 
             log.error("Error initializing menus in static bloc, check configuration if using 3rd party libraries", ex);
             throw ex;
         } catch (Exception ex) {
             log.error("Error initializing menus in static bloc, check configuration if using 3rd party libraries", ex);
         }
     }
 
     /**
      * Private constructor to prevent instantiation.
      */
     private MenuFactory() {
     }
 
     public static void addEditMenu(JPopupMenu menu, boolean removable) {
         addSeparator(menu);
         if (removable) {
             menu.add(makeMenuItemRes("cut", ActionNames.CUT, KeyStrokes.CUT)); //$NON-NLS-1$
         }
         menu.add(makeMenuItemRes("copy", ActionNames.COPY, KeyStrokes.COPY));  //$NON-NLS-1$
         menu.add(makeMenuItemRes("paste", ActionNames.PASTE, KeyStrokes.PASTE)); //$NON-NLS-1$
         menu.add(makeMenuItemRes("duplicate", ActionNames.DUPLICATE, KeyStrokes.DUPLICATE));  //$NON-NLS-1$
         menu.add(makeMenuItemRes("reset_gui", ActionNames.RESET_GUI )); //$NON-NLS-1$
         if (removable) {
             menu.add(makeMenuItemRes("remove", ActionNames.REMOVE, KeyStrokes.REMOVE)); //$NON-NLS-1$
         }
     }
 
     public static void addPasteResetMenu(JPopupMenu menu) {
         addSeparator(menu);
         menu.add(makeMenuItemRes("paste", ActionNames.PASTE, KeyStrokes.PASTE)); //$NON-NLS-1$
         menu.add(makeMenuItemRes("reset_gui", ActionNames.RESET_GUI )); //$NON-NLS-1$
     }
 
     public static void addFileMenu(JPopupMenu pop) {
         addFileMenu(pop, true);
     }
 
     /**
      * @param menu JPopupMenu
      * @param addSaveTestFragmentMenu Add Save as Test Fragment menu if true 
      */
     public static void addFileMenu(JPopupMenu menu, boolean addSaveTestFragmentMenu) {
         // the undo/redo as a standard goes first in Edit menus
         // maybe there's better place for them in JMeter?
         addUndoItems(menu);
 
         addSeparator(menu);
         menu.add(makeMenuItemRes("open", ActionNames.OPEN));// $NON-NLS-1$
         menu.add(makeMenuItemRes("menu_merge", ActionNames.MERGE));// $NON-NLS-1$
         menu.add(makeMenuItemRes("save_as", ActionNames.SAVE_AS));// $NON-NLS-1$
         if(addSaveTestFragmentMenu) {
             menu.add(makeMenuItemRes("save_as_test_fragment", ActionNames.SAVE_AS_TEST_FRAGMENT));// $NON-NLS-1$
         }
         addSeparator(menu);
         JMenuItem savePicture = makeMenuItemRes("save_as_image",// $NON-NLS-1$
                 ActionNames.SAVE_GRAPHICS,
                 KeyStrokes.SAVE_GRAPHICS);
         menu.add(savePicture);
         if (!(GuiPackage.getInstance().getCurrentGui() instanceof Printable)) {
             savePicture.setEnabled(false);
         }
 
         JMenuItem savePictureAll = makeMenuItemRes("save_as_image_all",// $NON-NLS-1$
                 ActionNames.SAVE_GRAPHICS_ALL,
                 KeyStrokes.SAVE_GRAPHICS_ALL);
         menu.add(savePictureAll);
 
         addSeparator(menu);
 
         JMenuItem disabled = makeMenuItemRes("disable", ActionNames.DISABLE);// $NON-NLS-1$
         JMenuItem enabled = makeMenuItemRes("enable", ActionNames.ENABLE);// $NON-NLS-1$
         boolean isEnabled = GuiPackage.getInstance().getTreeListener().getCurrentNode().isEnabled();
         if (isEnabled) {
             disabled.setEnabled(true);
             enabled.setEnabled(false);
         } else {
             disabled.setEnabled(false);
             enabled.setEnabled(true);
         }
         menu.add(enabled);
         menu.add(disabled);
         JMenuItem toggle = makeMenuItemRes("toggle", ActionNames.TOGGLE, KeyStrokes.TOGGLE);// $NON-NLS-1$
         menu.add(toggle);
         addSeparator(menu);
         menu.add(makeMenuItemRes("help", ActionNames.HELP));// $NON-NLS-1$
     }
 
     /**
      * Add undo / redo
      * @param menu JPopupMenu
      */
     private static void addUndoItems(JPopupMenu menu) {
         addSeparator(menu);
 
         JMenuItem undo = makeMenuItemRes("undo", ActionNames.UNDO); //$NON-NLS-1$
         undo.setEnabled(GuiPackage.getInstance().canUndo());
         menu.add(undo);
 
         JMenuItem redo = makeMenuItemRes("redo", ActionNames.REDO); //$NON-NLS-1$
         // TODO: we could even show some hints on action being undone here if this will be required (by passing those hints into history  records)
         redo.setEnabled(GuiPackage.getInstance().canRedo());
         menu.add(redo);
     }
 
 
     public static JMenu makeMenus(String[] categories, String label, String actionCommand) {
         JMenu addMenu = new JMenu(label);
         for (String category : categories) {
             addMenu.add(makeMenu(category, actionCommand));
         }
         GuiUtils.makeScrollableMenu(addMenu);
         return addMenu;
     }
 
     public static JPopupMenu getDefaultControllerMenu() {
         JPopupMenu pop = new JPopupMenu();
         pop.add(MenuFactory.makeMenus(MENU_ADD_CONTROLLER,
                 JMeterUtils.getResString("add"),// $NON-NLS-1$
                 ActionNames.ADD));
         pop.add(MenuFactory.makeMenuItemRes("add_think_times",// $NON-NLS-1$
                 ActionNames.ADD_THINK_TIME_BETWEEN_EACH_STEP));
 
         pop.add(MenuFactory.makeMenuItemRes("apply_naming",// $NON-NLS-1$
                 ActionNames.APPLY_NAMING_CONVENTION));
         
         pop.add(makeMenus(MENU_PARENT_CONTROLLER,
                 JMeterUtils.getResString("change_parent"),// $NON-NLS-1$
                 ActionNames.CHANGE_PARENT));
 
         pop.add(makeMenus(MENU_PARENT_CONTROLLER,
                 JMeterUtils.getResString("insert_parent"),// $NON-NLS-1$
                 ActionNames.ADD_PARENT));
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultSamplerMenu() {
         JPopupMenu pop = new JPopupMenu();
         pop.add(MenuFactory.makeMenus(MENU_ADD_SAMPLER,
                 JMeterUtils.getResString("add"),// $NON-NLS-1$
                 ActionNames.ADD));
         pop.add(makeMenus(MENU_PARENT_SAMPLER,
                 JMeterUtils.getResString("insert_parent"),// $NON-NLS-1$
                 ActionNames.ADD_PARENT));
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultConfigElementMenu() {
         JPopupMenu pop = new JPopupMenu();
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultVisualizerMenu() {
         JPopupMenu pop = new JPopupMenu();
         pop.add(
                 MenuFactory.makeMenuItemRes("clear", ActionNames.CLEAR)); //$NON-NLS-1$
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultTimerMenu() {
         JPopupMenu pop = new JPopupMenu();
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultAssertionMenu() {
         JPopupMenu pop = new JPopupMenu();
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultExtractorMenu() {
         JPopupMenu pop = new JPopupMenu();
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultMenu() { // if type is unknown
         JPopupMenu pop = new JPopupMenu();
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     /**
      * Create a menu from a menu category.
      *
      * @param category - predefined string (used as key for menuMap HashMap and messages.properties lookup)
      * @param actionCommand - predefined string, e.g. ActionNames.ADD
      *     @see org.apache.jmeter.gui.action.ActionNames
      * @return the menu
      */
     public static JMenu makeMenu(String category, String actionCommand) {
         return makeMenu(menuMap.get(category), actionCommand, JMeterUtils.getResString(category));
     }
 
     /**
      * Create a menu from a collection of items.
      *
      * @param menuInfo - collection of MenuInfo items
      * @param actionCommand - predefined string, e.g. ActionNames.ADD
      *     @see org.apache.jmeter.gui.action.ActionNames
      * @param menuName The name of the newly created menu
      * @return the menu
      */
     public static JMenu makeMenu(Collection<MenuInfo> menuInfo, String actionCommand, String menuName) {
         JMenu menu = new JMenu(menuName);
         for (MenuInfo info : menuInfo) {
             menu.add(makeMenuItem(info, actionCommand));
         }
         GuiUtils.makeScrollableMenu(menu);
         return menu;
     }
 
     public static void setEnabled(JMenu menu) {
         if (menu.getSubElements().length == 0) {
             menu.setEnabled(false);
         }
     }
 
     /**
      * Create a single menu item
      *
      * @param label for the MenuItem
      * @param name for the MenuItem
      * @param actionCommand - predefined string, e.g. ActionNames.ADD
      *     @see org.apache.jmeter.gui.action.ActionNames
      * @return the menu item
      */
     public static JMenuItem makeMenuItem(String label, String name, String actionCommand) {
         JMenuItem newMenuChoice = new JMenuItem(label);
         newMenuChoice.setName(name);
         newMenuChoice.addActionListener(ActionRouter.getInstance());
         if (actionCommand != null) {
             newMenuChoice.setActionCommand(actionCommand);
         }
 
         return newMenuChoice;
     }
 
     /**
      * Create a single menu item from the resource name.
      *
      * @param resource for the MenuItem
      * @param actionCommand - predefined string, e.g. ActionNames.ADD
      *     @see org.apache.jmeter.gui.action.ActionNames
      * @return the menu item
      */
     public static JMenuItem makeMenuItemRes(String resource, String actionCommand) {
         JMenuItem newMenuChoice = new JMenuItem(JMeterUtils.getResString(resource));
         newMenuChoice.setName(resource);
         newMenuChoice.addActionListener(ActionRouter.getInstance());
         if (actionCommand != null) {
             newMenuChoice.setActionCommand(actionCommand);
         }
 
         return newMenuChoice;
     }
 
     /**
      * Create a single menu item from a MenuInfo object
      *
      * @param info the MenuInfo object
      * @param actionCommand - predefined string, e.g. ActionNames.ADD
      *     @see org.apache.jmeter.gui.action.ActionNames
      * @return the menu item
      */
     public static Component makeMenuItem(MenuInfo info, String actionCommand) {
         JMenuItem newMenuChoice = new JMenuItem(info.getLabel());
         newMenuChoice.setName(info.getClassName());
         newMenuChoice.addActionListener(ActionRouter.getInstance());
         if (actionCommand != null) {
             newMenuChoice.setActionCommand(actionCommand);
         }
 
         return newMenuChoice;
     }
 
     public static JMenuItem makeMenuItemRes(String resource, String actionCommand, KeyStroke accel) {
         JMenuItem item = makeMenuItemRes(resource, actionCommand);
         item.setAccelerator(accel);
         return item;
     }
 
     public static JMenuItem makeMenuItem(String label, String name, String actionCommand, KeyStroke accel) {
         JMenuItem item = makeMenuItem(label, name, actionCommand);
         item.setAccelerator(accel);
         return item;
     }
 
     private static void initializeMenus() {
         try {
             List<String> guiClasses = ClassFinder.findClassesThatExtend(JMeterUtils.getSearchPaths(), new Class[] {
                     JMeterGUIComponent.class, TestBean.class });
             Collections.sort(guiClasses);
             for (String name : guiClasses) {
 
                 /*
                  * JMeterTreeNode and TestBeanGUI are special GUI classes, and
                  * aren't intended to be added to menus
                  *
                  * TODO: find a better way of checking this
                  */
                 if (name.endsWith("JMeterTreeNode") // $NON-NLS-1$
                         || name.endsWith("TestBeanGUI")) {// $NON-NLS-1$
                     continue;// Don't try to instantiate these
                 }
 
                 if (elementsToSkip.contains(name)) { // No point instantiating class
-                    log.info("Skipping " + name);
+                    log.info("Skipping {}", name);
                     continue;
                 }
 
                 boolean hideBean = false; // Should the TestBean be hidden?
 
                 JMeterGUIComponent item = null;
                 try {
                     Class<?> c = Class.forName(name);
                     if (TestBean.class.isAssignableFrom(c)) {
                         TestBeanGUI tbgui = new TestBeanGUI(c);
                         hideBean = tbgui.isHidden() || (tbgui.isExpert() && !JMeterUtils.isExpertMode());
                         item = tbgui;
                     } else {
                         item = (JMeterGUIComponent) c.newInstance();
                     }
                 } catch (NoClassDefFoundError e) {
-                    log.warn("Configuration error, probably corrupt or missing third party library(jar) ? Could not create class:" + name + ". " + e, 
-                            e);
+                    log.warn(
+                            "Configuration error, probably corrupt or missing third party library(jar) ? Could not create class: {}. {}",
+                            name, e, e);
                     continue;
                 } catch(HeadlessException e) {
-                    log.warn("Could not instantiate class:" + name, e); // NOSONAR
+                    log.warn("Could not instantiate class: {}", name, e); // NOSONAR
                     continue;
                 } catch(RuntimeException e) {
                     throw (RuntimeException) e;
                 } catch (Exception e) {
-                    log.warn("Could not instantiate class:" + name, e); // NOSONAR
+                    log.warn("Could not instantiate class: {}", name, e); // NOSONAR
                     continue;
                 }
                 if (hideBean || elementsToSkip.contains(item.getStaticLabel())) {
-                    log.info("Skipping " + name);
+                    log.info("Skipping {}", name);
                     continue;
                 } else {
                     elementsToSkip.add(name); // Don't add it again
                 }
                 Collection<String> categories = item.getMenuCategories();
                 if (categories == null) {
-                    log.debug(name + " participates in no menus.");
+                    log.debug("{} participates in no menus.", name);
                     continue;
                 }
                 if (categories.contains(THREADS)) {
                     threads.add(new MenuInfo(item, name));
                 }
                 if (categories.contains(FRAGMENTS)) {
                     fragments.add(new MenuInfo(item, name));
                 }
                 if (categories.contains(TIMERS)) {
                     timers.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(POST_PROCESSORS)) {
                     postProcessors.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(PRE_PROCESSORS)) {
                     preProcessors.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(CONTROLLERS)) {
                     controllers.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(SAMPLERS)) {
                     samplers.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(NON_TEST_ELEMENTS)) {
                     nonTestElements.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(LISTENERS)) {
                     listeners.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(CONFIG_ELEMENTS)) {
                     configElements.add(new MenuInfo(item, name));
                 }
                 if (categories.contains(ASSERTIONS)) {
                     assertions.add(new MenuInfo(item, name));
                 }
 
             }
         } catch (IOException e) {
-            log.error("", e);
+            log.error("IO Exception while initializing menus.", e);
         }
     }
 
     private static void addSeparator(JPopupMenu menu) {
         MenuElement[] elements = menu.getSubElements();
         if ((elements.length > 0) && !(elements[elements.length - 1] instanceof JPopupMenu.Separator)) {
             menu.addSeparator();
         }
     }
 
     /**
      * Determine whether or not nodes can be added to this parent.
      *
      * Used by Merge
      *
      * @param parentNode
      *            The {@link JMeterTreeNode} to test, if a new element can be
      *            added to it
      * @param element
      *            - top-level test element to be added
      * @return whether it is OK to add the element to this parent
      */
     public static boolean canAddTo(JMeterTreeNode parentNode, TestElement element) {
         JMeterTreeNode node = new JMeterTreeNode(element, null);
         return canAddTo(parentNode, new JMeterTreeNode[]{node});
     }
 
     /**
      * Determine whether or not nodes can be added to this parent.
      *
      * Used by DragNDrop and Paste.
      *
      * @param parentNode
      *            The {@link JMeterTreeNode} to test, if <code>nodes[]</code>
      *            can be added to it
      * @param nodes
      *            - array of nodes that are to be added
      * @return whether it is OK to add the dragged nodes to this parent
      */
     public static boolean canAddTo(JMeterTreeNode parentNode, JMeterTreeNode[] nodes) {
         if (null == parentNode) {
             return false;
         }
         if (foundClass(nodes, new Class[]{WorkBench.class})){// Can't add a Workbench anywhere
             return false;
         }
         if (foundClass(nodes, new Class[]{TestPlan.class})){// Can't add a TestPlan anywhere
             return false;
         }
         TestElement parent = parentNode.getTestElement();
 
         // Force TestFragment to only be pastable under a Test Plan
         if (foundClass(nodes, new Class[]{org.apache.jmeter.control.TestFragmentController.class})){
             if (parent instanceof TestPlan) {
                 return true;
             }
             return false;
         }
 
         if (parent instanceof WorkBench) {// allow everything else
             return true;
         }
         if (parent instanceof TestPlan) {
             if (foundClass(nodes,
                      new Class[]{Sampler.class, Controller.class}, // Samplers and Controllers need not apply ...
                      org.apache.jmeter.threads.AbstractThreadGroup.class)  // but AbstractThreadGroup (Controller) is OK
                 ){
                 return false;
             }
             return true;
         }
         // AbstractThreadGroup is only allowed under a TestPlan
         if (foundClass(nodes, new Class[]{org.apache.jmeter.threads.AbstractThreadGroup.class})){
             return false;
         }
         if (parent instanceof Controller) {// Includes thread group; anything goes
             return true;
         }
         if (parent instanceof Sampler) {// Samplers and Controllers need not apply ...
             if (foundClass(nodes, new Class[]{Sampler.class, Controller.class})){
                 return false;
             }
             return true;
         }
         // All other
         return false;
     }
 
     // Is any node an instance of one of the classes?
     private static boolean foundClass(JMeterTreeNode[] nodes, Class<?>[] classes) {
         for (JMeterTreeNode node : nodes) {
             for (Class<?> aClass : classes) {
                 if (aClass.isInstance(node.getUserObject())) {
                     return true;
                 }
             }
         }
         return false;
     }
 
     // Is any node an instance of one of the classes, but not an exception?
     private static boolean foundClass(JMeterTreeNode[] nodes, Class<?>[] classes, Class<?> except) {
         for (JMeterTreeNode node : nodes) {
             Object userObject = node.getUserObject();
             if (!except.isInstance(userObject)) {
                 for (Class<?> aClass : classes) {
                     if (aClass.isInstance(userObject)) {
                         return true;
                     }
                 }
             }
         }
         return false;
     }
 
     // Methods used for Test cases
     static int menuMap_size() {
         return menuMap.size();
     }
     static int assertions_size() {
         return assertions.size();
     }
     static int configElements_size() {
         return configElements.size();
     }
     static int controllers_size() {
         return controllers.size();
     }
     static int listeners_size() {
         return listeners.size();
     }
     static int nonTestElements_size() {
         return nonTestElements.size();
     }
     static int postProcessors_size() {
         return postProcessors.size();
     }
     static int preProcessors_size() {
         return preProcessors.size();
     }
     static int samplers_size() {
         return samplers.size();
     }
     static int timers_size() {
         return timers.size();
     }
     static int elementsToSkip_size() {
         return elementsToSkip.size();
     }
 
     /**
      * Menu sort helper class
      */
     private static class MenuInfoComparator implements Comparator<MenuInfo>, Serializable {
         private static final long serialVersionUID = 1L;
         private final boolean caseBlind;
         MenuInfoComparator(boolean caseBlind){
             this.caseBlind = caseBlind;
         }
         @Override
         public int compare(MenuInfo o1, MenuInfo o2) {
             String lab1 = o1.getLabel();
             String lab2 = o2.getLabel();
             if (caseBlind) {
                 return lab1.toLowerCase(Locale.ENGLISH).compareTo(lab2.toLowerCase(Locale.ENGLISH));
             }
             return lab1.compareTo(lab2);
         }
     }
 
     /**
      * Sort loaded menus; all but THREADS are sorted case-blind.
      * [This is so Thread Group appears before setUp and tearDown]
      */
     private static void sortPluginMenus() {
         for(Entry<String, List<MenuInfo>> me : menuMap.entrySet()){
             Collections.sort(me.getValue(), new MenuInfoComparator(!me.getKey().equals(THREADS)));
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/PowerTableModel.java b/src/core/org/apache/jmeter/gui/util/PowerTableModel.java
index 859f98acb..e075d5c35 100644
--- a/src/core/org/apache/jmeter/gui/util/PowerTableModel.java
+++ b/src/core/org/apache/jmeter/gui/util/PowerTableModel.java
@@ -1,277 +1,277 @@
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
 
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.util.ArrayList;
 import java.util.List;
 
 import javax.swing.event.TableModelEvent;
 import javax.swing.table.DefaultTableModel;
 
 import org.apache.jorphan.collections.Data;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class PowerTableModel extends DefaultTableModel {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(PowerTableModel.class);
 
-    private static final long serialVersionUID = 233L;
+    private static final long serialVersionUID = 234L;
 
     private Data model = new Data();
 
     private Class<?>[] columnClasses;
 
     public PowerTableModel(String[] headers, Class<?>[] classes) {
         if (headers.length != classes.length){
             throw new IllegalArgumentException("Header and column array sizes differ");
         }
         model.setHeaders(headers);
         columnClasses = classes;
     }
 
     public PowerTableModel() {
     }
 
     public void setRowValues(int row, Object[] values) {
         if (values.length != model.getHeaderCount()){
             throw new IllegalArgumentException("Incorrect number of data items");
         }
         model.setCurrentPos(row);
         for (int i = 0; i < values.length; i++) {
             model.addColumnValue(model.getHeaders()[i], values[i]);
         }
     }
 
     public Data getData() {
         return model;
     }
 
     public void addNewColumn(String colName, Class<?> colClass) {
         model.addHeader(colName);
         Class<?>[] newClasses = new Class[columnClasses.length + 1];
         System.arraycopy(columnClasses, 0, newClasses, 0, columnClasses.length);
         newClasses[newClasses.length - 1] = colClass;
         columnClasses = newClasses;
         Object defaultValue = createDefaultValue(columnClasses.length - 1);
         model.setColumnData(colName, defaultValue);
         this.fireTableStructureChanged();
     }
 
     @Override
     public void removeRow(int row) {
-        log.debug("remove row: " + row);
+        log.debug("remove row: {}", row);
         if (model.size() > row) {
             log.debug("Calling remove row on Data");
             model.removeRow(row);
         }
     }
 
     public void removeColumn(int col) {
         model.removeColumn(col);
         this.fireTableStructureChanged();
     }
 
     public void setColumnData(int col, List<?> data) {
         model.setColumnData(col, data);
     }
 
     public List<?> getColumnData(String colName) {
         return model.getColumnAsObjectArray(colName);
     }
 
     public void clearData() {
         String[] headers = model.getHeaders();
         model = new Data();
         model.setHeaders(headers);
         this.fireTableDataChanged();
     }
 
     @Override
     public void addRow(Object[] data) {
         if (data.length != model.getHeaderCount()){
             throw new IllegalArgumentException("Incorrect number of data items");
         }
         model.setCurrentPos(model.size());
         for (int i = 0; i < data.length; i++) {
             model.addColumnValue(model.getHeaders()[i], data[i]);
         }
     }
 
     @Override
     public void moveRow(int start, int end, int to) {
         ArrayList<Object[]> rows = new ArrayList<>();
         for(int i=0; i < getRowCount(); i++){
             rows.add(getRowData(i));
         }
 
         List<Object[]> subList = new ArrayList<>(rows.subList(start, end));
         for (int x = end - 1; x >= start; x--) {
             rows.remove(x);
         }
 
         rows.addAll(to, subList);
         for(int i = 0; i < rows.size(); i++){
             setRowValues(i, rows.get(i));
         }
 
         super.fireTableChanged(new TableModelEvent(this));
     }
 
     public void addNewRow() {
         addRow(createDefaultRow());
     }
 
     private Object[] createDefaultRow() {
         Object[] rowData = new Object[getColumnCount()];
         for (int i = 0; i < rowData.length; i++) {
             rowData[i] = createDefaultValue(i);
         }
         return rowData;
     }
 
     public Object[] getRowData(int row) {
         Object[] rowData = new Object[getColumnCount()];
         for (int i = 0; i < rowData.length; i++) {
             rowData[i] = model.getColumnValue(i, row);
         }
         return rowData;
     }
 
     private Object createDefaultValue(int i) {
         Class<?> colClass = getColumnClass(i);
         try {
             return colClass.newInstance();
         } catch (Exception e) {
             try {
                 Constructor<?> constr = colClass.getConstructor(new Class[] { String.class });
                 return constr.newInstance(new Object[] { "" });
             } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
             }
             try {
                 Constructor<?> constr = colClass.getConstructor(new Class[] { Integer.TYPE });
                 return constr.newInstance(new Object[] { Integer.valueOf(0) });
             } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
             }
             try {
                 Constructor<?> constr = colClass.getConstructor(new Class[] { Long.TYPE });
                 return constr.newInstance(new Object[] { Long.valueOf(0L) });
             } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
             }
             try {
                 Constructor<?> constr = colClass.getConstructor(new Class[] { Boolean.TYPE });
                 return constr.newInstance(new Object[] { Boolean.FALSE });
             } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
             }
             try {
                 Constructor<?> constr = colClass.getConstructor(new Class[] { Float.TYPE });
                 return constr.newInstance(new Object[] { Float.valueOf(0F) });
             } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
             }
             try {
                 Constructor<?> constr = colClass.getConstructor(new Class[] { Double.TYPE });
                 return constr.newInstance(new Object[] { Double.valueOf(0D) });
             } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
             }
             try {
                 Constructor<?> constr = colClass.getConstructor(new Class[] { Character.TYPE });
                 return constr.newInstance(new Object[] { Character.valueOf(' ') });
             } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
             }
             try {
                 Constructor<?> constr = colClass.getConstructor(new Class[] { Byte.TYPE });
                 return constr.newInstance(new Object[] { Byte.valueOf(Byte.MIN_VALUE) });
             } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
             }
             try {
                 Constructor<?> constr = colClass.getConstructor(new Class[] { Short.TYPE });
                 return constr.newInstance(new Object[] { Short.valueOf(Short.MIN_VALUE) });
             } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
             }
         }
         return "";
     }
 
     /**
      * Required by table model interface.
      *
      * @return the RowCount value
      */
     @Override
     public int getRowCount() {
         if (model == null) {
             return 0;
         }
         return model.size();
     }
 
     /**
      * Required by table model interface.
      *
      * @return the ColumnCount value
      */
     @Override
     public int getColumnCount() {
         return model.getHeaders().length;
     }
 
     /**
      * Required by table model interface.
      *
      * @return the ColumnName value
      */
     @Override
     public String getColumnName(int column) {
         return model.getHeaders()[column];
     }
 
     @Override
     public boolean isCellEditable(int row, int column) {
         // all table cells are editable
         return true;
     }
 
     @Override
     public Class<?> getColumnClass(int column) {
         return columnClasses[column];
     }
 
     /**
      * Required by table model interface. return the ValueAt value
      */
     @Override
     public Object getValueAt(int row, int column) {
         return model.getColumnValue(column, row);
     }
 
     /**
      * Sets the ValueAt attribute of the Arguments object.
      *
      * @param value
      *            the new ValueAt value
      */
     @Override
     public void setValueAt(Object value, int row, int column) {
         if (row < model.size()) {
             model.setCurrentPos(row);
             model.addColumnValue(model.getHeaders()[column], value);
         }
     }
 }
