File path: src/jorphan/org/apache/jorphan/gui/JLabeledChoice.java
Comment: Maybe move to vector if MT problems occur
Initial commit id: e010fabc
Final commit id: 247ab1c9
   Bugs between [       2]:
1f919dc4e Bug 54199 - Move to Java 6 add @Override Bugzilla Id: 54199
a9101ed64  Bug 41209 -  JLabeled* and ToolTips
   Bugs after [       0]:


Start block index: 72
End block index: 72
	 private ArrayList mChangeListeners = new ArrayList(3);  // Maybe move to vector if MT problems occur
