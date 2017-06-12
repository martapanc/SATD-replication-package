diff --git a/src/core/org/apache/jmeter/testbeans/gui/GenericTestBeanCustomizer.java b/src/core/org/apache/jmeter/testbeans/gui/GenericTestBeanCustomizer.java
index e7d874b93..6fa7b632c 100644
--- a/src/core/org/apache/jmeter/testbeans/gui/GenericTestBeanCustomizer.java
+++ b/src/core/org/apache/jmeter/testbeans/gui/GenericTestBeanCustomizer.java
@@ -1,591 +1,595 @@
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
  */
 package org.apache.jmeter.testbeans.gui;
 
 import java.awt.Component;
 import java.awt.GridBagConstraints;
 import java.awt.GridBagLayout;
 import java.awt.Insets;
 import java.beans.BeanInfo;
 import java.beans.PropertyChangeEvent;
 import java.beans.PropertyChangeListener;
 import java.beans.PropertyDescriptor;
 import java.beans.PropertyEditor;
 import java.beans.PropertyEditorManager;
 import java.io.Serializable;
 import java.text.MessageFormat;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.Map;
 import java.util.MissingResourceException;
 import java.util.ResourceBundle;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * The GenericTestBeanCustomizer is designed to provide developers with a
  * mechanism to quickly implement GUIs for new components.
  * <p>
  * It allows editing each of the public exposed properties of the edited type 'a
  * la JavaBeans': as far as the types of those properties have an associated
  * editor, there's no GUI development required.
  * <p>
  * This class understands the following PropertyDescriptor attributes:
  * <dl>
  * <dt>group: String</dt>
  * <dd>Group under which the property should be shown in the GUI. The string is
  * also used as a group title (but see comment on resourceBundle below). The
  * default group is "".</dd>
  * <dt>order: Integer</dt>
  * <dd>Order in which the property will be shown in its group. A smaller
  * integer means higher up in the GUI. The default order is 0. Properties of
  * equal order are sorted alphabetically.</dd>
  * <dt>tags: String[]</dt>
  * <dd>List of values to be offered for the property in addition to those
  * offered by its property editor.</dd>
  * <dt>notUndefined: Boolean</dt>
  * <dd>If true, the property should not be left undefined. A <b>default</b>
  * attribute must be provided if this is set.</dd>
  * <dd>notExpression: Boolean</dd>
  * <dd>If true, the property content should always be constant: JMeter
  * 'expressions' (strings using ${var}, etc...) can't be used.</dt>
  * <dd>notOther: Boolean</dd>
  * <dd>If true, the property content must always be one of the tags values or
  * null.</dt>
  * <dt>default: Object</dt>
  * <dd>Initial value for the property's GUI. Must be provided and be non-null
  * if <b>notUndefined</b> is set. Must be one of the provided tags (or null) if
  * <b>notOther</b> is set.
  * </dl>
  * <p>
  * The following BeanDescriptor attributes are also understood:
  * <dl>
  * <dt>group.<i>group</i>.order: Integer</dt>
  * <dd>where <b><i>group</i></b> is a group name used in a <b>group</b>
  * attribute in one or more PropertyDescriptors. Defines the order in which the
  * group will be shown in the GUI. A smaller integer means higher up in the GUI.
  * The default order is 0. Groups of equal order are sorted alphabetically.</dd>
  * <dt>resourceBundle: ResourceBundle</dt>
  * <dd>A resource bundle to be used for GUI localization: group display names
  * will be obtained from property "<b><i>group</i>.displayName</b>" if
  * available (where <b><i>group</i></b> is the group name).
  * </dl>
  * 
  * @author <a href="mailto:jsalvata@apache.org">Jordi Salvat i Alabart</a>
  */
 public class GenericTestBeanCustomizer extends JPanel implements SharedCustomizer, PropertyChangeListener {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	public static final String GROUP = "group"; //$NON-NLS-1$
 
 	public static final String ORDER = "order"; //$NON-NLS-1$
 
 	public static final String TAGS = "tags"; //$NON-NLS-1$
 
 	public static final String NOT_UNDEFINED = "notUndefined"; //$NON-NLS-1$
 
 	public static final String NOT_EXPRESSION = "notExpression"; //$NON-NLS-1$
 
 	public static final String NOT_OTHER = "notOther"; //$NON-NLS-1$
 
 	public static final String DEFAULT = "default"; //$NON-NLS-1$
 
 	public static final String RESOURCE_BUNDLE = "resourceBundle"; //$NON-NLS-1$
 
 	public static final String ORDER(String group) {
 		return "group." + group + ".order";
 	}
 
 	public static final String DEFAULT_GROUP = "";
 
 	private int scrollerCount = 0;
 
 	/**
 	 * BeanInfo object for the class of the objects being edited.
 	 */
 	private transient BeanInfo beanInfo;
 
 	/**
 	 * Property descriptors from the beanInfo.
 	 */
 	private transient PropertyDescriptor[] descriptors;
 
 	/**
 	 * Property editors -- or null if the property can't be edited. Unused if
 	 * customizerClass==null.
 	 */
 	private transient PropertyEditor[] editors;
 
 	/**
 	 * Message format for property field labels:
 	 */
 	private MessageFormat propertyFieldLabelMessage;
 
 	/**
 	 * Message format for property tooltips:
 	 */
 	private MessageFormat propertyToolTipMessage;
 
 	/**
 	 * The Map we're currently customizing. Set by setObject().
 	 */
 	private Map propertyMap;
 
     public GenericTestBeanCustomizer(){
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
     }
 	/**
 	 * Create a customizer for a given test bean type.
 	 * 
 	 * @param testBeanClass
 	 *            a subclass of TestBean
 	 * @see org.apache.jmeter.testbeans.TestBean
 	 */
 	GenericTestBeanCustomizer(BeanInfo beanInfo) {
 		super();
 
 		this.beanInfo = beanInfo;
 
 		// Get and sort the property descriptors:
 		descriptors = beanInfo.getPropertyDescriptors();
 		Arrays.sort(descriptors, new PropertyComparator());
 
 		// Obtain the propertyEditors:
 		editors = new PropertyEditor[descriptors.length];
 		for (int i = 0; i < descriptors.length; i++) {
 			String name = descriptors[i].getName();
 
 			// Don't get editors for hidden or non-read-write properties:
 			if (descriptors[i].isHidden() || (descriptors[i].isExpert() && !JMeterUtils.isExpertMode())
 					|| descriptors[i].getReadMethod() == null || descriptors[i].getWriteMethod() == null) {
 				log.debug("No editor for property " + name);
 				editors[i] = null;
 				continue;
 			}
 
 			PropertyEditor propertyEditor;
 			Class editorClass = descriptors[i].getPropertyEditorClass();
 
 			if (log.isDebugEnabled()) {
 				log.debug("Property " + name + " has editor class " + editorClass);
 			}
 
 			if (editorClass != null) {
 				try {
 					propertyEditor = (PropertyEditor) editorClass.newInstance();
 				} catch (InstantiationException e) {
 					log.error("Can't create property editor.", e);
 					throw new Error(e.toString());
 				} catch (IllegalAccessException e) {
 					log.error("Can't create property editor.", e);
 					throw new Error(e.toString());
 				}
 			} else {
 				Class c = descriptors[i].getPropertyType();
 				propertyEditor = PropertyEditorManager.findEditor(c);
 			}
 
 			if (log.isDebugEnabled()) {
 				log.debug("Property " + name + " has property editor " + propertyEditor);
 			}
 
 			if (propertyEditor == null) {
 				log.debug("No editor for property " + name);
 				editors[i] = null;
 				continue;
 			}
 
 			if (!propertyEditor.supportsCustomEditor()) {
 				propertyEditor = createWrapperEditor(propertyEditor, descriptors[i]);
 
 				if (log.isDebugEnabled()) {
 					log.debug("Editor for property " + name + " is wrapped in " + propertyEditor);
 				}
 			}
 			if (propertyEditor.getCustomEditor() instanceof JScrollPane) {
 				scrollerCount++;
 			}
 
 			editors[i] = propertyEditor;
 
 			// Initialize the editor with the provided default value or null:
 			setEditorValue(i, descriptors[i].getValue(DEFAULT));
 
 			// Now subscribe as a listener (we didn't want to receive the event
 			// for the setEditorValue above!)
 			propertyEditor.addPropertyChangeListener(this);
 		}
 
 		// Obtain message formats:
 		propertyFieldLabelMessage = new MessageFormat(JMeterUtils.getResString("property_as_field_label")); //$NON-NLS-1$
 		propertyToolTipMessage = new MessageFormat(JMeterUtils.getResString("property_tool_tip")); //$NON-NLS-1$
 
 		// Initialize the GUI:
 		init();
 	}
 
 	/**
 	 * Find the default typeEditor and a suitable guiEditor for the given
 	 * property descriptor, and combine them in a WrapperEditor.
 	 * 
 	 * @param typeEditor
 	 * @param descriptor
 	 * @return
 	 */
 	private WrapperEditor createWrapperEditor(PropertyEditor typeEditor, PropertyDescriptor descriptor) {
 		String[] editorTags = typeEditor.getTags();
 		String[] additionalTags = (String[]) descriptor.getValue(TAGS);
 		String[] tags = null;
 		if (editorTags == null)
 			tags = additionalTags;
 		else if (additionalTags == null)
 			tags = editorTags;
 		else {
 			tags = new String[editorTags.length + additionalTags.length];
 			int j = 0;
 			for (int i = 0; i < editorTags.length; i++)
 				tags[j++] = editorTags[i];
 			for (int i = 0; i < additionalTags.length; i++)
 				tags[j++] = additionalTags[i];
 		}
 
 		boolean notNull = Boolean.TRUE.equals(descriptor.getValue(NOT_UNDEFINED));
 		boolean notExpression = Boolean.TRUE.equals(descriptor.getValue(NOT_EXPRESSION));
 		boolean notOther = Boolean.TRUE.equals(descriptor.getValue(NOT_OTHER));
 
 		PropertyEditor guiEditor;
 		if (notNull && tags == null) {
 			guiEditor = new FieldStringEditor();
 		} else {
 			ComboStringEditor e = new ComboStringEditor();
 			e.setNoUndefined(notNull);
 			e.setNoEdit(notExpression && notOther);
 			e.setTags(tags);
 
 			guiEditor = e;
 		}
 
 		WrapperEditor wrapper = new WrapperEditor(typeEditor, guiEditor, !notNull, // acceptsNull
 				!notExpression, // acceptsExpressions
 				!notOther, // acceptsOther
 				descriptor.getValue(DEFAULT));
 
 		return wrapper;
 	}
 
 	/**
 	 * Set the value of the i-th property, properly reporting a possible
 	 * failure.
 	 * 
 	 * @param i
 	 *            the index of the property in the descriptors and editors
 	 *            arrays
 	 * @param value
 	 *            the value to be stored in the editor
 	 * 
 	 * @throws IllegalArgumentException
 	 *             if the editor refuses the value
 	 */
 	private void setEditorValue(int i, Object value) throws IllegalArgumentException {
 		editors[i].setValue(value);
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.gui.JMeterGUIComponent#configure(org.apache.jmeter.testelement.TestElement)
 	 */
 	public void setObject(Object map) {
 		propertyMap = (Map) map;
 
 		if (propertyMap.size() == 0) {
 			// Uninitialized -- set it to the defaults:
 			for (int i = 0; i < editors.length; i++) {
 				Object value = descriptors[i].getValue(DEFAULT);
 				String name = descriptors[i].getName();
 				if (value != null) {
 					propertyMap.put(name, value);
 					log.debug("Set " + name + "= " + value);
 				}
 				firePropertyChange(name, null, value);
 			}
 		}
 
 		// Now set the editors to the element's values:
 		for (int i = 0; i < editors.length; i++) {
 			if (editors[i] == null)
 				continue;
 			try {
 				setEditorValue(i, propertyMap.get(descriptors[i].getName()));
 			} catch (IllegalArgumentException e) {
 				// I guess this can happen as a result of a bad
 				// file read? In this case, it would be better to replace the
 				// incorrect value with anything valid, e.g. the default value
 				// for the property.
 				// But for the time being, I just prefer to be aware of any
 				// problems occuring here, most likely programming errors,
 				// so I'll bail out.
 				// (MS Note) Can't bail out - newly create elements have blank
 				// values and must get the defaults.
 				// Also, when loading previous versions of jmeter test scripts,
 				// some values
 				// may not be right, and should get default values - MS
 				// TODO: review this and possibly change to:
 				setEditorValue(i, descriptors[i].getValue(DEFAULT));
 			}
 		}
 	}
 
 	/**
 	 * Find the index of the property of the given name.
 	 * 
 	 * @param name
 	 *            the name of the property
 	 * @return the index of that property in the descriptors array, or -1 if
 	 *         there's no property of this name.
 	 */
 //	private int descriptorIndex(String name) // NOTUSED
 //	{
 //		for (int i = 0; i < descriptors.length; i++) {
 //			if (descriptors[i].getName().equals(name)) {
 //				return i;
 //			}
 //		}
 //		return -1;
 //	}
 
 	/**
 	 * Initialize the GUI.
 	 */
 	private void init() {
 		setLayout(new GridBagLayout());
 
 		GridBagConstraints cl = new GridBagConstraints(); // for labels
 		cl.gridx = 0;
 		cl.anchor = GridBagConstraints.EAST;
 		cl.insets = new Insets(0, 1, 0, 1);
 
 		GridBagConstraints ce = new GridBagConstraints(); // for editors
 		ce.fill = GridBagConstraints.BOTH;
 		ce.gridx = 1;
 		ce.weightx = 1.0;
 		ce.insets = new Insets(0, 1, 0, 1);
 
 		GridBagConstraints cp = new GridBagConstraints(); // for panels
 		cp.fill = GridBagConstraints.BOTH;
 		cp.gridx = 1;
 		cp.gridy = GridBagConstraints.RELATIVE;
 		cp.gridwidth = 2;
 		cp.weightx = 1.0;
 
 		JPanel currentPanel = this;
 		String currentGroup = DEFAULT_GROUP;
 		int y = 0;
 
 		for (int i = 0; i < editors.length; i++) {
 			if (editors[i] == null)
 				continue;
 
 			if (log.isDebugEnabled()) {
 				log.debug("Laying property " + descriptors[i].getName());
 			}
 
 			String g = group(descriptors[i]);
 			if (!currentGroup.equals(g)) {
 				if (currentPanel != this) {
 					add(currentPanel, cp);
 				}
 				currentGroup = g;
 				currentPanel = new JPanel(new GridBagLayout());
 				currentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
 						groupDisplayName(g)));
 				cp.weighty = 0.0;
 				y = 0;
 			}
 
 			Component customEditor = editors[i].getCustomEditor();
 
 			boolean multiLineEditor = false;
 			if (customEditor.getPreferredSize().height > 50 || customEditor instanceof JScrollPane) {
 				// TODO: the above works in the current situation, but it's
 				// just a hack. How to get each editor to report whether it
 				// wants to grow bigger? Whether the property label should
 				// be at the left or at the top of the editor? ...?
 				multiLineEditor = true;
 			}
 
 			JLabel label = createLabel(descriptors[i]);
 			label.setLabelFor(customEditor);
 
 			cl.gridy = y;
 			cl.gridwidth = multiLineEditor ? 2 : 1;
 			cl.anchor = multiLineEditor ? GridBagConstraints.CENTER : GridBagConstraints.EAST;
 			currentPanel.add(label, cl);
 
 			ce.gridx = multiLineEditor ? 0 : 1;
 			ce.gridy = multiLineEditor ? ++y : y;
 			ce.gridwidth = multiLineEditor ? 2 : 1;
 			ce.weighty = multiLineEditor ? 1.0 : 0.0;
 
 			cp.weighty += ce.weighty;
 
 			currentPanel.add(customEditor, ce);
 
 			y++;
 		}
 		if (currentPanel != this) {
 			add(currentPanel, cp);
 		}
 
 		// Add a 0-sized invisible component that will take all the vertical
 		// space that nobody wants:
 		cp.weighty = 0.0001;
 		add(Box.createHorizontalStrut(0), cp);
 	}
 
 	private JLabel createLabel(PropertyDescriptor desc) {
 		String text = desc.getDisplayName();
 		if (!"".equals(text)) {
 			text = propertyFieldLabelMessage.format(new Object[] { desc.getDisplayName() });
 		}
 		// if the displayName is the empty string, leave it like that.
 		JLabel label = new JLabel(text);
 		label.setHorizontalAlignment(JLabel.TRAILING);
 		text = propertyToolTipMessage.format(new Object[] { desc.getName(), desc.getShortDescription() });
 		label.setToolTipText(text);
 
 		return label;
 	}
 
 	/**
 	 * Obtain a property descriptor's group.
 	 * 
 	 * @param descriptor
 	 * @return the group String.
 	 */
 	private String group(PropertyDescriptor d) {
 		String group = (String) d.getValue(GROUP);
 		if (group == null)
 			group = DEFAULT_GROUP;
 		return group;
 	}
 
 	/**
 	 * Obtain a group's display name
 	 */
 	private String groupDisplayName(String group) {
 		try {
 			ResourceBundle b = (ResourceBundle) beanInfo.getBeanDescriptor().getValue(RESOURCE_BUNDLE);
 			if (b == null)
 				return group;
 			else
 				return b.getString(group + ".displayName");
 		} catch (MissingResourceException e) {
 			return group;
 		}
 	}
 
 	/**
 	 * Comparator used to sort properties for presentation in the GUI.
 	 */
 	private class PropertyComparator implements Comparator, Serializable {
 		public int compare(Object o1, Object o2) {
 			return compare((PropertyDescriptor) o1, (PropertyDescriptor) o2);
 		}
 
 		private int compare(PropertyDescriptor d1, PropertyDescriptor d2) {
 			int result;
 
 			String g1 = group(d1), g2 = group(d2);
 			Integer go1 = groupOrder(g1), go2 = groupOrder(g2);
 
 			result = go1.compareTo(go2);
 			if (result != 0)
 				return result;
 
 			result = g1.compareTo(g2);
 			if (result != 0)
 				return result;
 
 			Integer po1 = propertyOrder(d1), po2 = propertyOrder(d2);
 			result = po1.compareTo(po2);
 			if (result != 0)
 				return result;
 
 			return d1.getName().compareTo(d2.getName());
 		}
 
 		/**
 		 * Obtain a group's order.
 		 * 
 		 * @param group
 		 *            group name
 		 * @return the group's order (zero by default)
 		 */
 		private Integer groupOrder(String group) {
 			Integer order = (Integer) beanInfo.getBeanDescriptor().getValue(ORDER(group));
 			if (order == null)
 				order = new Integer(0);
 			return order;
 		}
 
 		/**
 		 * Obtain a property's order.
 		 * 
 		 * @param d
 		 * @return the property's order attribute (zero by default)
 		 */
 		private Integer propertyOrder(PropertyDescriptor d) {
 			Integer order = (Integer) d.getValue(ORDER);
 			if (order == null)
 				order = new Integer(0);
 			return order;
 		}
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
 	 */
 	public void propertyChange(PropertyChangeEvent evt) {
+		// evt will be null only when called from TestBeanGUI.modifyTestElement()
+		// TODO - is the propertyChange event needed, now that modifyTestElement calls this?
 		for (int i = 0; i < editors.length; i++) {
-			if (editors[i] == evt.getSource()) {
+			if (evt == null || editors[i] == evt.getSource()) {
 				Object value = editors[i].getValue();
 				String name = descriptors[i].getName();
 				if (value == null) {
 					propertyMap.remove(name);
 					log.debug("Unset " + name);
 				} else {
 					propertyMap.put(name, value);
 					log.debug("Set " + name + "= " + value);
 				}
-				firePropertyChange(name, evt.getOldValue(), value);
+				if (evt != null ) {
+					firePropertyChange(name, evt.getOldValue(), value);
+				}
 				return;
 			}
 		}
 		throw new Error("Unexpected propertyChange event received: " + evt);
 	}
 }
diff --git a/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java b/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
index 5c393a69b..78af7e239 100644
--- a/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
+++ b/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
@@ -1,434 +1,441 @@
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
  */
 package org.apache.jmeter.testbeans.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.beans.BeanDescriptor;
 import java.beans.BeanInfo;
 import java.beans.Customizer;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.beans.PropertyDescriptor;
 import java.beans.PropertyEditorManager;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 
 import javax.swing.JPopupMenu;
 
 import org.apache.commons.collections.map.LRUMap;
 import org.apache.jmeter.assertions.Assertion;
 import org.apache.jmeter.assertions.gui.AbstractAssertionGui;
 import org.apache.jmeter.config.ConfigElement;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.control.gui.AbstractControllerGui;
 import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
 import org.apache.jmeter.gui.JMeterGUIComponent;
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.processor.gui.AbstractPostProcessorGui;
 import org.apache.jmeter.processor.gui.AbstractPreProcessorGui;
 import org.apache.jmeter.reporters.AbstractListenerElement;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jmeter.testbeans.BeanInfoSupport;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.AbstractProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.timers.Timer;
 import org.apache.jmeter.timers.gui.AbstractTimerGui;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.Visualizer;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * JMeter GUI element editing for TestBean elements.
  * <p>
  * The actual GUI is always a bean customizer: if the bean descriptor provides
  * one, it will be used; otherwise, a GenericTestBeanCustomizer will be created
  * for this purpose.
  * <p>
  * Those customizers deviate from the standards only in that, instead of a bean,
  * they will receive a Map in the setObject call. This will be a property name
  * to value Map. The customizer is also in charge of initializing empty Maps
  * with sensible initial values.
  * <p>
  * If the provided Customizer class implements the SharedCustomizer interface,
  * the same instance of the customizer will be reused for all beans of the type:
  * setObject(map) can then be called multiple times. Otherwise, one separate
  * instance will be used for each element. For efficiency reasons, most
  * customizers should implement SharedCustomizer.
  * 
  */
 public class TestBeanGUI extends AbstractJMeterGuiComponent implements JMeterGUIComponent {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	private Class testBeanClass;
 
 	private transient BeanInfo beanInfo;
 
 	private Class customizerClass;
 
 	/**
 	 * The single customizer if the customizer class implements
 	 * SharedCustomizer, null otherwise.
 	 */
 	private Customizer customizer = null;
 
 	/**
 	 * TestElement to Customizer map if customizer is null. This is necessary to
 	 * avoid the cost of creating a new customizer on each edit. The cache size
 	 * needs to be limited, though, to avoid memory issues when editing very
 	 * large test plans.
 	 */
 	private Map customizers = new LRUMap(20);
 
 	/**
 	 * Index of the customizer in the JPanel's child component list:
 	 */
 	private int customizerIndexInPanel;
 
 	/**
 	 * The property name to value map that the active customizer edits:
 	 */
 	private Map propertyMap = new HashMap();
 
 	/**
 	 * Whether the GUI components have been created.
 	 */
 	private boolean initialized = false;
 
 	static {
 		List paths = new LinkedList();
 		paths.add("org.apache.jmeter.testbeans.gui");// $NON-NLS-1$
 		paths.addAll(Arrays.asList(PropertyEditorManager.getEditorSearchPath()));
 		String s = JMeterUtils.getPropDefault("propertyEditorSearchPath", null);// $NON-NLS-1$
 		if (s != null) {
 			paths.addAll(Arrays.asList(JOrphanUtils.split(s, ",", "")));// $NON-NLS-1$ // $NON-NLS-2$
 		}
 		PropertyEditorManager.setEditorSearchPath((String[]) paths.toArray(new String[0]));
 	}
 
 	// Dummy for JUnit test
 	public TestBeanGUI() {
 		log.warn("Constructor only for use in testing");// $NON-NLS-1$
 	}
 
 	public TestBeanGUI(Class testBeanClass) {
 		super();
 		log.debug("testing class: " + testBeanClass.getName());
 		// A quick verification, just in case:
 		if (!TestBean.class.isAssignableFrom(testBeanClass)) {
 			Error e = new Error();
 			log.error("This should never happen!", e);
 			throw e; // Programming error: bail out.
 		}
 
 		this.testBeanClass = testBeanClass;
 
 		// Get the beanInfo:
 		try {
 			beanInfo = Introspector.getBeanInfo(testBeanClass);
 		} catch (IntrospectionException e) {
 			log.error("Can't get beanInfo for " + testBeanClass.getName(), e);
 			throw new Error(e.toString()); // Programming error. Don't
 											// continue.
 		}
 
 		customizerClass = beanInfo.getBeanDescriptor().getCustomizerClass();
 
 		// Creation of the customizer and GUI initialization is delayed until
 		// the
 		// first
 		// configure call. We don't need all that just to find out the static
 		// label, menu
 		// categories, etc!
 		initialized = false;
 	}
 
 	private Customizer createCustomizer() {
 		try {
 			return (Customizer) customizerClass.newInstance();
 		} catch (InstantiationException e) {
 			log.error("Could not instantiate customizer of class " + customizerClass, e);
 			throw new Error(e.toString());
 		} catch (IllegalAccessException e) {
 			log.error("Could not instantiate customizer of class " + customizerClass, e);
 			throw new Error(e.toString());
 		}
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.gui.JMeterGUIComponent#getStaticLabel()
 	 */
 	public String getStaticLabel() {
 		if (beanInfo == null)
 			return "null";// $NON-NLS-1$
 		return beanInfo.getBeanDescriptor().getDisplayName();
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
 	 */
 	public TestElement createTestElement() {
 		try {
 			TestElement element = (TestElement) testBeanClass.newInstance();
 			// configure(element);
 			// super.clear(); // set name, enabled.
 			modifyTestElement(element); // put the default values back into the
 			// new element
 			return element;
 		} catch (InstantiationException e) {
 			log.error("Can't create test element", e);
 			throw new Error(e.toString()); // Programming error. Don't
 											// continue.
 		} catch (IllegalAccessException e) {
 			log.error("Can't create test element", e);
 			throw new Error(e.toString()); // Programming error. Don't
 											// continue.
 		}
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(org.apache.jmeter.testelement.TestElement)
 	 */
 	public void modifyTestElement(TestElement element) {
+		// Fetch data from screen fields
+		if (customizer instanceof GenericTestBeanCustomizer) {
+			GenericTestBeanCustomizer gtbc = (GenericTestBeanCustomizer) customizer;
+			gtbc.propertyChange(null); 
+			// TODO - is this the best way to do this?
+			// Is the original property change Listener still needed?
+		}
 		configureTestElement(element);
 
 		// Copy all property values from the map into the element:
 		PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
 		for (int i = 0; i < props.length; i++) {
 			String name = props[i].getName();
 			Object value = propertyMap.get(name);
 			log.debug("Modify " + name + " to " + value);
 			if (value == null) {
 				Object valueNotUnDefined = props[i].getValue(BeanInfoSupport.NOT_UNDEFINED);
 				if (valueNotUnDefined != null && ((Boolean) valueNotUnDefined).booleanValue()) {
 					setPropertyInElement(element, name, props[i].getValue(BeanInfoSupport.DEFAULT));
 				} else {
 					element.removeProperty(name);
 				}
 			} else {
 				setPropertyInElement(element, name, propertyMap.get(name));
 			}
 		}
 	}
 
 	/**
 	 * @param element
 	 * @param name
 	 */
 	private void setPropertyInElement(TestElement element, String name, Object value) {
 		JMeterProperty jprop = AbstractProperty.createProperty(value);
 		jprop.setName(name);
 		element.setProperty(jprop);
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.gui.JMeterGUIComponent#createPopupMenu()
 	 */
 	public JPopupMenu createPopupMenu() {
 		// TODO: this menu is too wide (allows, e.g. to add controllers, no
 		// matter what the type of the element).
         // Change to match the actual bean's capabilities.
 		if (Timer.class.isAssignableFrom(testBeanClass))// HACK: Fix one such problem
 		{
 			return MenuFactory.getDefaultTimerMenu();
 		}
         else if(Sampler.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultSamplerMenu();
         }
         else if(ConfigTestElement.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultConfigElementMenu();
         }
         else if(Assertion.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultAssertionMenu();
         }
         else if(PostProcessor.class.isAssignableFrom(testBeanClass) || 
                 PreProcessor.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultExtractorMenu();
         }
         else if(AbstractListenerElement.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultVisualizerMenu();
         }
         else return MenuFactory.getDefaultControllerMenu();
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.gui.JMeterGUIComponent#configure(org.apache.jmeter.testelement.TestElement)
 	 */
 	public void configure(TestElement element) {
 		if (!initialized)
 			init();
 		clearGui();
 
 		super.configure(element);
 
 		// Copy all property values into the map:
 		for (PropertyIterator jprops = element.propertyIterator(); jprops.hasNext();) {
 			JMeterProperty jprop = jprops.next();
 			propertyMap.put(jprop.getName(), jprop.getObjectValue());
 		}
 
 		if (customizer != null) {
 			customizer.setObject(propertyMap);
 		} else {
 			if (initialized)
 				remove(customizerIndexInPanel);
 			Customizer c = (Customizer) customizers.get(element);
 			if (c == null) {
 				c = createCustomizer();
 				c.setObject(propertyMap);
 				customizers.put(element, c);
 			}
 			add((Component) c, BorderLayout.CENTER);
 		}
 
 		initialized = true;
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.gui.JMeterGUIComponent#getMenuCategories()
 	 */
 	public Collection getMenuCategories() {
 		List menuCategories = new LinkedList();
 		BeanDescriptor bd = beanInfo.getBeanDescriptor();
 
 		// We don't want to show expert beans in the menus unless we're
 		// in expert mode:
 		if (bd.isExpert() && !JMeterUtils.isExpertMode()) {
 			return null;
 		}
 
 		int matches = 0; // How many classes can we assign from?
 		// TODO: there must be a nicer way...
 		if (Assertion.class.isAssignableFrom(testBeanClass)) {
 			menuCategories.add(MenuFactory.ASSERTIONS);
 			bd.setValue(TestElement.GUI_CLASS, AbstractAssertionGui.class.getName());
 			matches++;
 		}
 		if (ConfigElement.class.isAssignableFrom(testBeanClass)) {
 			menuCategories.add(MenuFactory.CONFIG_ELEMENTS);
 			bd.setValue(TestElement.GUI_CLASS, AbstractConfigGui.class.getName());
 			matches++;
 		}
 		if (Controller.class.isAssignableFrom(testBeanClass)) {
 			menuCategories.add(MenuFactory.CONTROLLERS);
 			bd.setValue(TestElement.GUI_CLASS, AbstractControllerGui.class.getName());
 			matches++;
 		}
 		if (Visualizer.class.isAssignableFrom(testBeanClass)) {
 			menuCategories.add(MenuFactory.LISTENERS);
 			bd.setValue(TestElement.GUI_CLASS, AbstractVisualizer.class.getName());
 			matches++;
 		}
 		if (PostProcessor.class.isAssignableFrom(testBeanClass)) {
 			menuCategories.add(MenuFactory.POST_PROCESSORS);
 			bd.setValue(TestElement.GUI_CLASS, AbstractPostProcessorGui.class.getName());
 			matches++;
 		}
 		if (PreProcessor.class.isAssignableFrom(testBeanClass)) {
 			matches++;
 			menuCategories.add(MenuFactory.PRE_PROCESSORS);
 			bd.setValue(TestElement.GUI_CLASS, AbstractPreProcessorGui.class.getName());
 		}
 		if (Sampler.class.isAssignableFrom(testBeanClass)) {
 			matches++;
 			menuCategories.add(MenuFactory.SAMPLERS);
 			bd.setValue(TestElement.GUI_CLASS, AbstractSamplerGui.class.getName());
 		}
 		if (Timer.class.isAssignableFrom(testBeanClass)) {
 			matches++;
 			menuCategories.add(MenuFactory.TIMERS);
 			bd.setValue(TestElement.GUI_CLASS, AbstractTimerGui.class.getName());
 		}
 		if (matches == 0) {
 			log.error("Could not assign GUI class to " + testBeanClass.getName());
 		} else if (matches > 1) {// may be impossible, but no harm in
 									// checking ...
 			log.error("More than 1 GUI class found for " + testBeanClass.getName());
 		}
 		return menuCategories;
 	}
 
 	private void init() {
 		setLayout(new BorderLayout(0, 5));
 
 		setBorder(makeBorder());
 		add(makeTitlePanel(), BorderLayout.NORTH);
 
 		customizerIndexInPanel = getComponentCount();
 
 		if (customizerClass == null) {
 			customizer = new GenericTestBeanCustomizer(beanInfo);
 		} else if (SharedCustomizer.class.isAssignableFrom(customizerClass)) {
 			customizer = createCustomizer();
 		}
 
 		if (customizer != null)
 			add((Component) customizer, BorderLayout.CENTER);
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.gui.JMeterGUIComponent#getLabelResource()
 	 */
 	public String getLabelResource() {
 		// @see getStaticLabel
 		return null;
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.gui.JMeterGUIComponent#clearGui()
 	 */
 	public void clearGui() {
 		super.clearGui();
 		propertyMap.clear();
 	}
 }
