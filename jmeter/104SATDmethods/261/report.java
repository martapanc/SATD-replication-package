File path: src/core/org/apache/jmeter/gui/ReportMainFrame.java
Comment: TODO: Do we really need to have all these menubar methods duplicated
Initial commit id: a1408a8d
Final commit id: 6d2b7079
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 111
End block index: 122
	// TODO: Do we really need to have all these menubar methods duplicated
	// here? Perhaps we can make the menu bar accessible through GuiPackage?

	/**
	 * Specify whether or not the File|Load menu item should be enabled.
	 * 
	 * @param enabled
	 *            true if the menu item should be enabled, false otherwise
	 */
	public void setFileLoadEnabled(boolean enabled) {
        super.setFileLoadEnabled(enabled);
	}
