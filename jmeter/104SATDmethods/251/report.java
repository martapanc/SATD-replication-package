File path: src/core/org/apache/jmeter/JMeter.java
Comment: NOTUSED: GuiPackage guiPack =
Initial commit id: 76159a5b
Final commit id: 2d640689
   Bugs between [      33]:
135483ac0 Bug 59391 - In Distributed mode, the client exits abnormally at the end of test Bugzilla Id: 59391
35cd20998 Bug 58986 - Report/Dashboard reuses the same output directory Factor in JOrphanUtils the check for output folder and make it more readable and complete Add the check in both HtmlTemplateExporter and JMeter as per sebb request Bugzilla Id: 58986
a75c821ad Bug 58986 - Report/Dashboard reuses the same output directory Bugzilla Id: 58986
9ee466a0e Bug 58987 - Report/Dashboard: Improve error reporting Avoid Log+Rethrow, only rethrow Add column index in message Bugzilla Id: 58987
04ba97a97 Bug 58781 - Command line option "-?" shows Unknown option Bugzilla Id: 58781
27745b727 Bug 58653 - New JMeter Dashboard/Report with Dynamic Graphs, Tables to help analyzing load test results Bugzilla Id: 58653
480c3656b Bug 57821 - Command-line option "-X --remoteexit" doesnt work since 2.13 (regression related to Bug 57500) Bugzilla Id: 57821
40b3221e7 Bug 57605 - When there is an error loading Test Plan, SaveService.loadTree returns null leading to NPE in callers Bugzilla Id: 57605
022af006b Bug 57500 - Introduce retry behavior for remote testing
321e520fe Bug 57365 - Selected LAF is not correctly setup due to call of UIManager.setLookAndFeel too late Fix test failure Bugzilla Id: 57365
b74853f78 Bug 57365 - Selected LAF is not correctly setup due to call of UIManager.setLookAndFeel too late Bugzilla Id: 57365
65bd9c284 Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
be7f7415c Bug 55512 - Summariser should be enabled by default in Non GUI mode Bugzilla Id: 55512
849643223 Bug 54152 - In distributed testing : activeThreads and totalThreads always show 0 Add comment Bugzilla Id: 54152
8642a7617 Bug 54152 - In distributed testing : activeThreads and totalThreads always show 0 Bugzilla Id: 54152
90d52dfec Bug 55334 - Adding Include Controller to test plan (made of Include Controllers) without saving TestPlan leads to included code not being taken into account until save Bugzilla Id: 55334
4a2b1d231 Bug 54414 - Remote Test should not start if one of the engines fails to start correctly Bugzilla Id: 54414
1152bb1b5 Bug 52934 - GUI : Open Test plan with the tree expanded to the testplan level and no further and select the root of the tree
ba3cdcaf4 Bug 52346 - Shutdown detects if there are any non-daemon threads left which prevent JVM exit
5f7112827 Bug 51091 - New function returning the name of the current "Test Plan"
979329621 Bug 52029 - Command-line shutdown only gets sent to last engine that was started
04763b7d0 Bug 51831 - Cannot disable UDP server or change the maximum UDP port Also failed to check max port initially
dc1a76af6 Bug 50659 - JMeter server does not support concurrent tests - prevent client from starting another (part deux) Remove thread from ClientJMeterEngine - if it is required for GUI, then let the GUI create the thread This allows proper return of error conditions Also replace System.exit() with interrupt() of RMI thread
a29c6ac4a Bug 47398 - SampleEvents are sent twice over RMI in distributed testing and non gui mode
f664eb41d Bug 47165 - Using the same module name in command line mode causes NPE
7902bed60 Bug 46636 - rmi ports Remote server now uses port defined by the property server.rmi.localport if present Simplified Client / Server classes Show remote object connections on console
b2dff2302 Allow spaces in JMeter path names (apply work-round for Java bug 4496398)
6fd5f0f52 Bug 38687 - patch caused problems with non-GUI mode on systems without displays Added hack to create non-GUI version of JMeterTreeModel
d4a83fd68 Bug 38687 - Module controller does not work in non-GUI mode
797cf35b8 Bug 24684 - remote startup problems if spaces in the path of the jmeter
e861ae37d Bug 36755 (patch 20073) - consistent closing of file streams
c05a84bd5 Bug 41029 - JMeter -t fails to close input JMX file
122efff9f Bug 38681 - Include controller now works in non-GUI mode
   Bugs after [       6]:
22288a776 Bug 60589 Migrate LogKit to SLF4J - Drop avalon, logkit and excalibur with backward compatibility for 3rd party modules. Contributed by Woonsan Ko Documenting log4j2 related changes since 3.2 This closes #278 Bugzilla Id: 60589
5f0651b4a Bug 60564 - Migrating LogKit to SLF4J - core, core/control, core/engine/ClientJMeterEngine Contributed by Woonsan Ko This closes #269 Bugzilla Id: 60564
9418f1a3d Bug 60589 - Migrate LogKit to SLF4J - Drop avalon, logkit and excalibur with backward compatibility for 3rd party modules Part 1 of PR #254 Contributed by Woonsan Ko
03a2728d2 Bug 59995 - Allow user to change font size with 2 new menu items and use "jmeter.hidpi.scale.factor" for scaling fonts Contributed by UbikLoadPack Bugzilla Id: 59995
5153cdb45 Bug 60595: Add a SplashScreen at the start of JMeter GUI Contributed by maxime.chassagneux at gmail.com This closes #251 Bugzilla Id: 60595
f464c9baf Bug 60053 - In Non GUI mode, a Stacktrace is shown at end of test while report is being generated Bugzilla Id: 60053

Start block index: 179
End block index: 209
	public void startGui(CLOption testFile) {

		PluginManager.install(this, true);
		JMeterTreeModel treeModel = new JMeterTreeModel();
		JMeterTreeListener treeLis = new JMeterTreeListener(treeModel);
		treeLis.setActionHandler(ActionRouter.getInstance());
		// NOTUSED: GuiPackage guiPack =
		GuiPackage.getInstance(treeLis, treeModel);
		org.apache.jmeter.gui.MainFrame main = new org.apache.jmeter.gui.MainFrame(ActionRouter.getInstance(),
				treeModel, treeLis);
		main.setTitle("Apache JMeter");
		main.setIconImage(JMeterUtils.getImage("jmeter.jpg").getImage());
		ComponentUtil.centerComponentInWindow(main, 80);
		main.show();
		ActionRouter.getInstance().actionPerformed(new ActionEvent(main, 1, CheckDirty.ADD_ALL));
		if (testFile != null) {
			try {
				File f = new File(testFile.getArgument());
				log.info("Loading file: " + f);
				FileInputStream reader = new FileInputStream(f);
				HashTree tree = SaveService.loadTree(reader);

				GuiPackage.getInstance().setTestPlanFile(f.getAbsolutePath());

				new Load().insertLoadedTree(1, tree);
			} catch (Exception e) {
				log.error("Failure loading test file", e);
				JMeterUtils.reportErrorToUser(e.toString());
			}
		}
	}

*********************** Method when SATD was removed **************************

  private void startGui(String testFile) {
      String jMeterLaf = LookAndFeelCommand.getJMeterLaf();
      try {
          UIManager.setLookAndFeel(jMeterLaf);
      } catch (Exception ex) {
          log.warn("Could not set LAF to:"+jMeterLaf, ex);
      }

      PluginManager.install(this, true);

      JMeterTreeModel treeModel = new JMeterTreeModel();
      JMeterTreeListener treeLis = new JMeterTreeListener(treeModel);
      final ActionRouter instance = ActionRouter.getInstance();
      instance.populateCommandMap();
      treeLis.setActionHandler(instance);
      GuiPackage.initInstance(treeLis, treeModel);
      MainFrame main = new MainFrame(treeModel, treeLis);
      ComponentUtil.centerComponentInWindow(main, 80);
      main.setVisible(true);
      instance.actionPerformed(new ActionEvent(main, 1, ActionNames.ADD_ALL));
      if (testFile != null) {
          try {
              File f = new File(testFile);
              log.info("Loading file: " + f);
              FileServer.getFileServer().setBaseForScript(f);

              HashTree tree = SaveService.loadTree(f);

              GuiPackage.getInstance().setTestPlanFile(f.getAbsolutePath());

              Load.insertLoadedTree(1, tree);
          } catch (ConversionException e) {
              log.error("Failure loading test file", e);
              JMeterUtils.reportErrorToUser(SaveService.CEtoString(e));
          } catch (Exception e) {
              log.error("Failure loading test file", e);
              JMeterUtils.reportErrorToUser(e.toString());
          }
      } else {
          JTree jTree = GuiPackage.getInstance().getMainFrame().getTree();
          TreePath path = jTree.getPathForRow(0);
          jTree.setSelectionPath(path);
          FocusRequester.requestFocus(jTree);
      }
  }
