File path: src/jorphan/org/apache/jorphan/gui/JLabeledTextArea.java
Comment: Maybe move to vector if MT problems occur
Initial commit id: e010fabc
Final commit id: 247ab1c9
   Bugs between [       3]:
1f919dc4e Bug 54199 - Move to Java 6 add @Override Bugzilla Id: 54199
38206b73f Add getTextLines() method; needed for Bug 51861
a9101ed64  Bug 41209 -  JLabeled* and ToolTips
   Bugs after [       0]:


Start block index: 79
End block index: 79
	 private ArrayList mChangeListeners = new ArrayList(3);  // Maybe move to vector if MT problems occur
