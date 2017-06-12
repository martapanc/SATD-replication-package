File path: src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
Comment: all the others could be private too?
Initial commit id: ccb0fe6d
Final commit id: 9aff4f1d
   Bugs between [       2]:
17953cd07 Bug 38031 - add mnemonic for Edit
7d7e1252a Bug 35525 - Added Spanish localisation
   Bugs after [      23]:
055154b4c Bug 60814 - Menu : Add "Open Recent" menu item to make recent files loading more obvious Bugzilla Id: 60814
4e78b5e89 Bug 60792 - Create a new Help menu item to create a thread dump Bugzilla Id: 60792
0020353a2 Bug 60664 - Add a UI menu to set log level Contributed by Woonsan Ko This closes #275 Bugzilla Id: 60664
2ee6bd97e Bug 60564 - Migrating LogKit to SLF4J - core/gui package (1/2) Contributed by Woonsan Ko This comments #272 Bugzilla Id: 60564
03a2728d2 Bug 59995 - Allow user to change font size with 2 new menu items and use "jmeter.hidpi.scale.factor" for scaling fonts Contributed by UbikLoadPack Bugzilla Id: 59995
e1c1410eb Bug 55258 - Drop "Close" icon from toolbar and add "New" to menu This closes #227 Bugzilla Id: 55258
1e04e7e54 Bug 59236 - JMeter Properties : Make some cleanup Remove ability to disable toolbar in menu Bugzilla Id: 59236
e0ec0a9c7 Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
493384439 Bug 55693 - Add a "Save as Test Fragment" option Bugzilla Id: 55693
4eb162852 Bug 55657 - Remote and Local Stop/Shutdown buttons state does not take into account local / remote status Bugzilla Id: 55657
4832bb912 Bug 55172 - Provide plugins a way to add Top Menu and menu items Made method private as per sebb comment Bugzilla Id: 55172
a034d3ec1 Bug 55172 - Provide plugins a way to add Top Menu and menu items Bugzilla Id: 55172
f184d4a74 Bug 55085 - UX Improvement : Ability to create New Test Plan from Templates Bugzilla Id: 55085
326d4cea6 Bug 52601 - CTRL + F for the new Find feature CTRL + F1 help for functions CTRL + F for search dialog Bugzilla Id: 52601
d491016ab Bug 41788 - Log viewer (console window) needed as an option
b8298512c Bug 51876 - Functionnality to search in Samplers TreeView
62b9f6ba6 Bug 52040 - Add a toolbar in JMeter main window
0f1cc3440 Bug 52027 - Allow System or CrossPlatform LAF to be set from options menu
f2ee7acde Bug 52019 - Add menu option to Start a test ignoring Pause Timers
0f34fcd0f Bug 51876 - Functionnality to search in Samplers TreeView
3dd627dcf Bug 51876 - Functionnality to search in Samplers TreeView
2235b2038  Bug 46900 -  i18N: polish property file
db5304dd6 Bug 44378 - Turkish localisation

Start block index: 101
End block index: 101
	private JMenuItem run_shut; // all the others could be private too?
