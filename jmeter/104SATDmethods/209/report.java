File path: src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
Comment: ODO implement other non-text types
Initial commit id: 298ffdce
Final commit id: e5c10847
   Bugs between [       5]:
e5c108478 Bug 47474 - View Results Tree support for plugin renderers
dd20f3a41 Bug 36726 - add search function to Tree View Listener
447cb7f46 Bug 47137 - Labels in View Results Tree aren't I18N
6f9771e84 Bug 43450 - add save/restore of error count; fix Calculator to use error count
e09e4965b Bug 42582 - JSON pretty printing in Tree View Listener
   Bugs after [      14]:
61304dee3 Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution Contributed by Woonsan Ko #comment #266 Bugzilla Id: 60564
eb234b7aa Bug 60583 - VRT listener with JavaFX html view don't work with openjdk 8 Bugzilla Id: 60583
ac1f2c212 Bug 60542 - View Results Tree : Allow Upper Panel to be collapsed Contributed by UbikLoadPack Bugzilla Id: 60542
8cc1b70be Bug 59102 - View Results Tree: Better default value for "view.results.tree.max_size" Bugzilla Id: 59102
08efaaadc Bug 55597 View Results Tree: Add a search feature to search in recorded samplers
4321ec752 Bug 56228 - View Results Tree : Improve ergonomy by changing placement of Renderers and allowing custom ordering Bugzilla Id: 56228
b85f6c38c Bug 54226 - View Results Tree : Show response even when server does not return ContentType header Bugzilla Id: 54226
c0f98a933 Bug 52266 - Code:Inconsistent synchronization Bugzilla Id: 52266
ea4d5caba Bug 52694 - Deadlock in GUI related to non AWT Threads updating GUI
9845e49b4 Bug 52217 - ViewResultsFullVisualizer : Synchronization issues on root and treeModel
11668430e Bug 52022 - In View Results Tree rather than showing just a message if the results are to big, show as much of the result as are configured
3a87c8dc3 Bug 52003 - View Results Tree "Scroll automatically" does not scroll properly in case nodes are expanded
90d8067c4 Bug 42246 - dont override colour for autoscroll checkbox
c592cc4c2 Bug 42246 - Need for a 'auto-scroll' option in "View Results Tree" and "Assertion Results"

Start block index: 294
End block index: 430
	public void valueChanged(TreeSelectionEvent e) {
		log.debug("Start : valueChanged1");
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();

		if (log.isDebugEnabled()) {
			log.debug("valueChanged : selected node - " + node);
		}

		StyledDocument statsDoc = stats.getStyledDocument();
		try {
			statsDoc.remove(0, statsDoc.getLength());
			sampleDataField.setText(""); // $NON-NLS-1$
			results.setText(""); // $NON-NLS-1$
			if (node != null) {
				Object userObject = node.getUserObject();
				if(userObject instanceof SampleResult) {
					SampleResult res = (SampleResult) userObject;

					// We are displaying a SampleResult
					setupTabPaneForSampleResult();

					if (log.isDebugEnabled()) {
						log.debug("valueChanged1 : sample result - " + res);
					}

					if (res != null) {
						// load time label

						log.debug("valueChanged1 : load time - " + res.getTime());
						String sd = res.getSamplerData();
						if (sd != null) {
							String rh = res.getRequestHeaders();
							if (rh != null) {
								StringBuffer sb = new StringBuffer(sd.length() + rh.length()+20);
								sb.append(sd);
								sb.append("\nRequest Headers:\n");
								sb.append(rh);
								sd = sb.toString();
							}
							sampleDataField.setText(sd);
						}

						StringBuffer statsBuff = new StringBuffer(200);
						statsBuff.append("Thread Name: ").append(res.getThreadName()).append(NL);
						String startTime = dateFormat.format(new Date(res.getStartTime()));
						statsBuff.append("Sample Start: ").append(startTime).append(NL);
						statsBuff.append("Load time: ").append(res.getTime()).append(NL);
						statsBuff.append("Size in bytes: ").append(res.getBytes()).append(NL);
						statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
						statsBuff = new StringBuffer(); //reset for reuse

						String responseCode = res.getResponseCode();
						log.debug("valueChanged1 : response code - " + responseCode);

						int responseLevel = 0;
						if (responseCode != null) {
							try {
								responseLevel = Integer.parseInt(responseCode) / 100;
							} catch (NumberFormatException numberFormatException) {
								// no need to change the foreground color
							}
						}

						Style style = null;
						switch (responseLevel) {
						case 3:
							style = statsDoc.getStyle(STYLE_REDIRECT);
							break;
						case 4:
							style = statsDoc.getStyle(STYLE_CLIENT_ERROR);
							break;
						case 5:
							style = statsDoc.getStyle(STYLE_SERVER_ERROR);
							break;
						}

						statsBuff.append("Response code: ").append(responseCode).append(NL);
						statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), style);
						statsBuff = new StringBuffer(100); //reset for reuse

						// response message label
						String responseMsgStr = res.getResponseMessage();

						log.debug("valueChanged1 : response message - " + responseMsgStr);
						statsBuff.append("Response message: ").append(responseMsgStr).append(NL);

						statsBuff.append(NL).append("Response headers:").append(NL);
						statsBuff.append(res.getResponseHeaders()).append(NL);
						statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
						statsBuff = null; // Done

						// get the text response and image icon
						// to determine which is NOT null
						if ((SampleResult.TEXT).equals(res.getDataType())) // equals(null) is OK
						{
							String response = getResponseAsString(res);
							if (command.equals(TEXT_COMMAND)) {
								showTextResponse(response);
							} else if (command.equals(HTML_COMMAND)) {
								showRenderedResponse(response, res);
							} else if (command.equals(XML_COMMAND)) {
								showRenderXMLResponse(response);
							}
						} else {
							byte[] responseBytes = res.getResponseData();
							if (responseBytes != null) {
								showImage(new ImageIcon(responseBytes)); //TODO implement other non-text types
							}
						}
					}
				}
				else if(userObject instanceof AssertionResult) {
					AssertionResult res = (AssertionResult) userObject;

					// We are displaying an AssertionResult
					setupTabPaneForAssertionResult();

					if (log.isDebugEnabled()) {
						log.debug("valueChanged1 : sample result - " + res);
					}

					if (res != null) {
						StringBuffer statsBuff = new StringBuffer(100);
						statsBuff.append("Assertion error: ").append(res.isError()).append(NL);
						statsBuff.append("Assertion failure: ").append(res.isFailure()).append(NL);
						statsBuff.append("Assertion failure message : ").append(res.getFailureMessage()).append(NL);
						statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
						statsBuff = null;
					}
				}
			}
		} catch (BadLocationException exc) {
			log.error("Error setting statistics text", exc);
			stats.setText("");
		}
		log.debug("End : valueChanged1");
	}

*********************** Method when SATD was removed **************************

public void valueChanged(TreeSelectionEvent e) {
    lastSelectionEvent = e;
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();

    if (node != null) {
        // to restore last tab used
        if (rightSide.getTabCount() > selectedTab) {
            resultsRender.setLastSelectedTab(rightSide.getSelectedIndex());
        }
        Object userObject = node.getUserObject();
        resultsRender.setSamplerResult(userObject);
        resultsRender.setupTabPane();
        // display a SampleResult
        if (userObject instanceof SampleResult) {
            SampleResult sampleResult = (SampleResult) userObject;
            if ((SampleResult.TEXT).equals(sampleResult.getDataType())){
                resultsRender.renderResult(sampleResult);
            } else {
                byte[] responseBytes = sampleResult.getResponseData();
                if (responseBytes != null) {
                    resultsRender.renderImage(sampleResult);
                }
            }
        }
    }
}
