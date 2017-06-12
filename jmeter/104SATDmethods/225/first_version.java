// $Header$
/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.jmeter.util.JMeterUtils;

/**
 * @author Michael Stover
 * @version $Revision$
 */
public class ButtonPanel extends JPanel {
	public final static int ADD_BUTTON = 1;

	public final static int EDIT_BUTTON = 2;

	public final static int DELETE_BUTTON = 3;

	public final static int LOAD_BUTTON = 4;

	public final static int SAVE_BUTTON = 5;

	private JButton add, delete, edit, load, save;

	public ButtonPanel() {
		init();
	}

	public void addButtonListener(int button, ActionListener listener) {
		switch (button) {
		case ADD_BUTTON:
			add.addActionListener(listener);
			break;
		case EDIT_BUTTON:
			edit.addActionListener(listener);
			break;
		case DELETE_BUTTON:
			delete.addActionListener(listener);
			break;
		case LOAD_BUTTON:
			load.addActionListener(listener);
			break;
		case SAVE_BUTTON:
			save.addActionListener(listener);
			break;
		}
	}

	/*
	 * NOTUSED private void initButtonMap() { }
	 */
	private void init() {
		add = new JButton(JMeterUtils.getResString("add"));
		add.setActionCommand("Add");
		edit = new JButton(JMeterUtils.getResString("edit"));
		edit.setActionCommand("Edit");
		delete = new JButton(JMeterUtils.getResString("delete"));
		delete.setActionCommand("Delete");
		load = new JButton(JMeterUtils.getResString("load"));
		load.setActionCommand("Load");
		save = new JButton(JMeterUtils.getResString("save"));
		save.setActionCommand("Save");
		Dimension d = delete.getPreferredSize();
		add.setPreferredSize(d);
		edit.setPreferredSize(d);
		// close.setPreferredSize(d);
		load.setPreferredSize(d);
		save.setPreferredSize(d);
		GridBagLayout g = new GridBagLayout();
		this.setLayout(g);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 1;
		g.setConstraints(add, c);
		this.add(add);
		c.gridx = 2;
		c.gridy = 1;
		g.setConstraints(edit, c);
		this.add(edit);
		c.gridx = 3;
		c.gridy = 1;
		g.setConstraints(delete, c);
		this.add(delete);
		/*
		 * c.gridx = 1; c.gridy = 2; g.setConstraints(close, c);
		 * panel.add(close);
		 */
		c.gridx = 2;
		c.gridy = 2;
		g.setConstraints(load, c);
		this.add(load);
		c.gridx = 3;
		c.gridy = 2;
		g.setConstraints(save, c);
		this.add(save);
	}
}
