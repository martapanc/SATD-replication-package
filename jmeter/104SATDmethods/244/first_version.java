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

package org.apache.jmeter.protocol.http.config.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.http.gui.HTTPFileArgsPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.util.HTTPFileArg;
import org.apache.jmeter.protocol.http.util.HTTPFileArgs;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jmeter.util.JMeterUtils;

public class MultipartUrlConfigGui extends UrlConfigGui {

	/**
	 * Files panel that holds file informations to be uploaded by
	 * http request.
	 */
	private HTTPFileArgsPanel filesPanel;

	// used by HttpTestSampleGui
	public MultipartUrlConfigGui() {
		super();
		init();
	}

	// not currently used
    public MultipartUrlConfigGui(boolean value) {
        super(value);
        init();
    }

    public void modifyTestElement(TestElement sampler) {
        super.modifyTestElement(sampler);
        filesPanel.modifyTestElement(sampler);
    }

	public void configure(TestElement el) {
		super.configure(el);
        filesPanel.configure(el);
	}

	private void init() {// called from ctor, so must not be overridable
		this.setLayout(new BorderLayout());

		// WEB SERVER PANEL
		VerticalPanel webServerPanel = new VerticalPanel();
		webServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), 
				JMeterUtils.getResString("web_server"))); // $NON-NLS-1$
		final JPanel domainPanel = getDomainPanel();
		final JPanel portPanel = getPortPanel();
		domainPanel.add(portPanel,BorderLayout.EAST);
		webServerPanel.add(domainPanel);
		//webServerPanel.add(getPortPanel());

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		northPanel.add(getProtocolAndMethodPanel());
		northPanel.add(getPathPanel());

		// WEB REQUEST PANEL
		JPanel webRequestPanel = new JPanel();
		webRequestPanel.setLayout(new BorderLayout());
		webRequestPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), 
				JMeterUtils.getResString("web_request"))); // $NON-NLS-1$

		webRequestPanel.add(northPanel, BorderLayout.NORTH);
		webRequestPanel.add(getParameterPanel(), BorderLayout.CENTER);
		webRequestPanel.add(getHTTPFileArgsPanel(), BorderLayout.SOUTH);

		this.add(webServerPanel, BorderLayout.NORTH);
		this.add(webRequestPanel, BorderLayout.CENTER);
	}

	private JPanel getHTTPFileArgsPanel() {
		filesPanel = new HTTPFileArgsPanel(JMeterUtils.getResString("send_file")); // $NON-NLS-1$
		return filesPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.jmeter.protocol.http.config.gui.UrlConfigGui#clear()
	 */
	public void clear() {
		super.clear();
		filesPanel.clear();
	}
}
