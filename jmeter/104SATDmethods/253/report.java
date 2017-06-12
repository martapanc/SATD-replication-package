File path: src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
Comment: TODO: is the serializable test necessary now that JMeterTreeNode is
Initial commit id: ac4f4d2f
Final commit id: 76159a5b
   Bugs between [       0]:

   Bugs after [       9]:
2ee6bd97e Bug 60564 - Migrating LogKit to SLF4J - core/gui package (1/2) Contributed by Woonsan Ko This comments #272 Bugzilla Id: 60564
8cf39ed85 Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
80e99eb89 Bug 57193: * Generics wont work in javadoc, so describe the type  textually. * add missing return and throws javadoc statements Bugzilla Id: 57193
9d8dc6783 Bug 54648 - JMeter GUI on OS X crashes when using CMD+C (keyboard shortcut or UI menu entry) on an element from the tree Remove final as in this case transient cannot be final. See: http://mail-archives.apache.org/mod_mbox/jmeter-dev/201409.mbox/%3CCAOGo0VaKGcTm=3+jF-mrjaVFBi21HPWE7mW1u09ve-QsP6sLOA@mail.gmail.com%3E Bugzilla Id: 54648
1372fcf46 Bug 54648 - JMeter GUI on OS X crashes when using CMD+C (keyboard shortcut or UI menu entry) on an element from the tree Put back final. Bugzilla Id: 54648
4f697f781 Bug 54648 - JMeter GUI on OS X crashes when using CMD+C (keyboard shortcut or UI menu entry) on an element from the tree
6572ccd24 Bug 51876 - Functionnality to search in Samplers TreeView
c84b8bca0 Bug 51876 - Functionnality to search in Samplers TreeView
3dd627dcf Bug 51876 - Functionnality to search in Samplers TreeView

Start block index: 51
End block index: 54
    public JMeterTreeNode(){// Allow serializable test to work
    		// TODO: is the serializable test necessary now that JMeterTreeNode is no longer a GUI component?
    	this(null,null);
    }
