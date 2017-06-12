File path: src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
Comment: om tree panel for to display response as tree view author
Initial commit id: 76159a5b
Final commit id: e5c10847
   Bugs between [      10]:
e5c108478 Bug 47474 - View Results Tree support for plugin renderers
dd20f3a41 Bug 36726 - add search function to Tree View Listener
447cb7f46 Bug 47137 - Labels in View Results Tree aren't I18N
6f9771e84 Bug 43450 - add save/restore of error count; fix Calculator to use error count
e09e4965b Bug 42582 - JSON pretty printing in Tree View Listener
d1bab6aed Bug 42184 - Number of bytes for subsamples not added to sample when sub samples are added
a585fdb25 Bug 41913 (19861) - fix ViewTree Assertion display; accumulate sub-sample byte-count
0a717bbad Bug 41873 - Add name to AssertionResult and display AssertionResult in ViewResultsFullVisualizer
607a2d815 Bug 39717 - use icons in the results tree instead of colors
944144d7b Bug 26337 - show date of sample
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

Start block index: 687
End block index: 796
	 * A Dom tree panel for to display response as tree view author <a
	 * href="mailto:d.maung@mdl.com">Dave Maung</a> TODO implement to find any
	 * nodes in the tree using TreePath.
	 *
	 */
	private class DOMTreePanel extends JPanel {

		private JTree domJTree;

		public DOMTreePanel(org.w3c.dom.Document document) {
			super(new GridLayout(1, 0));
			try {
				Node firstElement = getFirstElement(document);
				DefaultMutableTreeNode top = new XMLDefaultMutableTreeNode(firstElement);
				domJTree = new JTree(top);

				domJTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				domJTree.setShowsRootHandles(true);
				JScrollPane domJScrollPane = new JScrollPane(domJTree);
				domJTree.setAutoscrolls(true);
				this.add(domJScrollPane);
				ToolTipManager.sharedInstance().registerComponent(domJTree);
				domJTree.setCellRenderer(new DomTreeRenderer());
				this.setPreferredSize(new Dimension(800, 600));
			} catch (SAXException e) {
				log.warn("", e);
			}

		}

		/**
		 * Skip all DTD nodes, all prolog nodes. They dont support in tree view
		 * We let user to insert them however in DOMTreeView, we dont display it
		 *
		 * @param root
		 * @return
		 */
		private Node getFirstElement(Node parent) {
			NodeList childNodes = parent.getChildNodes();
			Node toReturn = null;
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				toReturn = childNode;
				if (childNode.getNodeType() == Node.ELEMENT_NODE)
					break;

			}
			return toReturn;
		}

		/**
		 * This class is to view as tooltext. This is very useful, when the
		 * contents has long string and does not fit in the view. it will also
		 * automatically wrap line for each 100 characters since tool tip
		 * support html. author <a href="mailto:d.maung@mdl.com">Dave Maung</a>
		 */
		private class DomTreeRenderer extends DefaultTreeCellRenderer {
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean phasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, phasFocus);

				DefaultMutableTreeNode valueTreeNode = (DefaultMutableTreeNode) value;
				setToolTipText(getHTML(valueTreeNode.toString(), "<br>", 100));
				return this;
			}

			/**
			 * get the html
			 *
			 * @param str
			 * @param separator
			 * @param maxChar
			 * @return
			 */
			private String getHTML(String str, String separator, int maxChar) {
				StringBuffer strBuf = new StringBuffer("<html><body bgcolor=\"yellow\"><b>");
				char[] chars = str.toCharArray();
				for (int i = 0; i < chars.length; i++) {

					if (i % maxChar == 0 && i != 0)
						strBuf.append(separator);
					strBuf.append(encode(chars[i]));

				}
				strBuf.append("</b></body></html>");
				return strBuf.toString();

			}

			private String encode(char c) {
				String toReturn = String.valueOf(c);
				switch (c) {
				case '<':
					toReturn = "&lt;";
					break;
				case '>':
					toReturn = "&gt;";
					break;
				case '\'':
					toReturn = "&apos;";
					break;
				case '\"':
					toReturn = "&quot;";
					break;

				}
				return toReturn;
			}
		}
	}
