File path: src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
Comment: It might be useful also to make this available in the 'Request' tab
Initial commit id: e09e4965
Final commit id: e5c10847
   Bugs between [       4]:
e5c108478 Bug 47474 - View Results Tree support for plugin renderers
dd20f3a41 Bug 36726 - add search function to Tree View Listener
447cb7f46 Bug 47137 - Labels in View Results Tree aren't I18N
6f9771e84 Bug 43450 - add save/restore of error count; fix Calculator to use error count
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

Start block index: 478
End block index: 531
	// It might be useful also to make this available in the 'Request' tab, for
	// when posting JSON.
	private static String prettyJSON(String json) {
		StringBuffer pretty = new StringBuffer(json.length() * 2); // Educated guess

		final String tab = ":   "; // $NON-NLS-1$
		StringBuffer index = new StringBuffer();
		String nl = ""; // $NON-NLS-1$

		Matcher valueOrPair = VALUE_OR_PAIR_PATTERN.matcher(json);

		boolean misparse = false;

		for (int i = 0; i < json.length(); ) {
			final char currentChar = json.charAt(i);
			if ((currentChar == '{') || (currentChar == '[')) {
				pretty.append(nl).append(index).append(currentChar);
				i++;
				index.append(tab);
				misparse = false;
			}
			else if ((currentChar == '}') || (currentChar == ']')) {
				if (index.length() > 0) {
					index.delete(0, tab.length());
				}
				pretty.append(nl).append(index).append(currentChar);
				i++;
				int j = i;
				while ((j < json.length()) && Character.isWhitespace(json.charAt(j))) {
					j++;
				}
				if ((j < json.length()) && (json.charAt(j) == ',')) {
					pretty.append(","); // $NON-NLS-1$
					i=j+1;
				}
				misparse = false;
			}
			else if (valueOrPair.find(i) && valueOrPair.group().length() > 0) {
				pretty.append(nl).append(index).append(valueOrPair.group());
				i=valueOrPair.end();
				misparse = false;
			}
			else {
				if (!misparse) {
					pretty.append(nl).append("- Parse failed from:");
				}
				pretty.append(currentChar);
				i++;
				misparse = true;
			}
			nl = "\n"; // $NON-NLS-1$
		}
		return pretty.toString();
	}

*********************** Method when SATD was removed **************************

  private Component createLeftPanel() {
      SampleResult rootSampleResult = new SampleResult();
      rootSampleResult.setSampleLabel("Root");
      rootSampleResult.setSuccessful(true);
      root = new DefaultMutableTreeNode(rootSampleResult);

      treeModel = new DefaultTreeModel(root);
      jTree = new JTree(treeModel);
      jTree.setCellRenderer(new ResultsNodeRenderer());
      jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      jTree.addTreeSelectionListener(this);
      jTree.setRootVisible(false);
      jTree.setShowsRootHandles(true);

      JScrollPane treePane = new JScrollPane(jTree);
      treePane.setPreferredSize(new Dimension(200, 300));

      VerticalPanel leftPane = new VerticalPanel();
      leftPane.add(treePane, BorderLayout.CENTER);
      leftPane.add(createComboRender(), BorderLayout.SOUTH);

      return leftPane;
  }
