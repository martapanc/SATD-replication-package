diff --git a/src/core/org/apache/jmeter/reporters/gui/ResultSaverGui.java b/src/core/org/apache/jmeter/reporters/gui/ResultSaverGui.java
index d9d7c38c1..5ef123e1c 100644
--- a/src/core/org/apache/jmeter/reporters/gui/ResultSaverGui.java
+++ b/src/core/org/apache/jmeter/reporters/gui/ResultSaverGui.java
@@ -1,115 +1,125 @@
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
 
 package org.apache.jmeter.reporters.gui;
 
 import java.awt.BorderLayout;
 
 import javax.swing.Box;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JTextField;
 
 import org.apache.jmeter.reporters.ResultSaver;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.processor.gui.AbstractPostProcessorGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Create a ResultSaver test element, which saves the sample information in set
  * of files
  * 
  * @version $Revision$ Last updated: $Date$
  */
 public class ResultSaverGui extends AbstractPostProcessorGui implements Clearable {
 
 	private JTextField filename;
 
 	private JCheckBox errorsOnly;
 
 	public ResultSaverGui() {
 		super();
 		init();
 	}
 
 	/**
 	 * @see org.apache.jmeter.gui.JMeterGUIComponent#getStaticLabel()
 	 */
 	public String getLabelResource() {
 		return "resultsaver_title"; // $NON-NLS-1$
 	}
 
 	/**
 	 * @see org.apache.jmeter.gui.JMeterGUIComponent#configure(TestElement)
 	 */
 	public void configure(TestElement el) {
 		super.configure(el);
 		filename.setText(el.getPropertyAsString(ResultSaver.FILENAME));
 		errorsOnly.setSelected(el.getPropertyAsBoolean(ResultSaver.ERRORS_ONLY));
 	}
 
 	/**
 	 * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
 	 */
 	public TestElement createTestElement() {
 		ResultSaver ResultSaver = new ResultSaver();
 		modifyTestElement(ResultSaver);
 		return ResultSaver;
 	}
 
 	/**
 	 * Modifies a given TestElement to mirror the data in the gui components.
 	 * 
 	 * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
 	 */
 	public void modifyTestElement(TestElement te) {
 		super.configureTestElement(te);
 		te.setProperty(ResultSaver.FILENAME, filename.getText());
 		te.setProperty(ResultSaver.ERRORS_ONLY, errorsOnly.isSelected());
 	}
 
+    /**
+     * Implements JMeterGUIComponent.clear
+     */
+    public void clear() {
+        super.clear();
+        
+        filename.setText(""); //$NON-NLS-1$
+        errorsOnly.setSelected(false);
+    }
+
 	private void init() {
 		setLayout(new BorderLayout());
 		setBorder(makeBorder());
 		Box box = Box.createVerticalBox();
 		box.add(makeTitlePanel());
 		box.add(createFilenamePanel());
 		errorsOnly = new JCheckBox(JMeterUtils.getResString("resultsaver_errors")); // $NON-NLS-1$
 		box.add(errorsOnly);
 		add(box, BorderLayout.NORTH);
 
 		// add(makeTitlePanel(),BorderLayout.NORTH);
 	}
 
 	private JPanel createFilenamePanel()// TODO ought to be a FileChooser ...
 	{
 		JLabel label = new JLabel(JMeterUtils.getResString("resultsaver_prefix")); // $NON-NLS-1$
 
 		filename = new JTextField(10);
 		filename.setName(ResultSaver.FILENAME);
 		label.setLabelFor(filename);
 
 		JPanel filenamePanel = new JPanel(new BorderLayout(5, 0));
 		filenamePanel.add(label, BorderLayout.WEST);
 		filenamePanel.add(filename, BorderLayout.CENTER);
 		return filenamePanel;
 	}
 
 }
