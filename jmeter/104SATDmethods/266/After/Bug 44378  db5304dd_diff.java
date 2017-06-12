diff --git a/src/components/org/apache/jmeter/config/CSVDataSetResources_tr.properties b/src/components/org/apache/jmeter/config/CSVDataSetResources_tr.properties
new file mode 100644
index 000000000..a110bdf35
--- /dev/null
+++ b/src/components/org/apache/jmeter/config/CSVDataSetResources_tr.properties
@@ -0,0 +1,15 @@
+#Stored by I18NEdit, may be edited!
+csv_data.displayName=CSV Veri Kayna\u011F\u0131n\u0131 Ayarla
+delimiter.displayName=S\u0131n\u0131rlay\u0131c\u0131 (tab i\u00E7in '\\t' kullan)
+delimiter.shortDescription=S\u0131n\u0131rlay\u0131c\u0131 gir (tab i\u00E7in '\\t')
+displayName=CSV Veri K\u00FCmesi Ayar\u0131
+fileEncoding.displayName=Dosya Kodlamas\u0131
+fileEncoding.shortDescription=Dosyada kullan\u0131lan karakter kodlamas\u0131
+filename.displayName=Dosya ad\u0131
+filename.shortDescription=cvs verisini tutan dosyan\u0131n ad\u0131 (g\u00F6reli veya tam dosya yolu)
+recycle.displayName=Dosya sonunda geri d\u00F6n\u00FC\u015F\u00FCm ?
+recycle.shortDescription=Dosya sonundan itibaren tekrar okunsun mu?
+stopThread.displayName=Dosya sonunda i\u015F par\u00E7ac\u0131\u011F\u0131n\u0131 durdur ?
+stopThread.shortDescription=\u0130\u015F par\u00E7ac\u0131c\u0131\u011F\u0131 dosya sonuna var\u0131ld\u0131\u011F\u0131nda durdurulsun mu (e\u011Fer geri d\u00F6n\u00FC\u015F\u00FCm etkin de\u011Filse) ?
+variableNames.displayName=De\u011Fi\u015Fken isimleri (virg\u00FClle ayr\u0131lm\u0131\u015F)
+variableNames.shortDescription=De\u011Fi\u015Fken isimlerini csv verisindeki kolon s\u0131ras\u0131yla \u00F6rt\u00FC\u015Fecek \u015Fekilde listele. Virg\u00FClle ay\u0131rarak.
diff --git a/src/components/org/apache/jmeter/extractor/BeanShellPostProcessorResources_tr.properties b/src/components/org/apache/jmeter/extractor/BeanShellPostProcessorResources_tr.properties
new file mode 100644
index 000000000..7903ed43d
--- /dev/null
+++ b/src/components/org/apache/jmeter/extractor/BeanShellPostProcessorResources_tr.properties
@@ -0,0 +1,10 @@
+#Stored by I18NEdit, may be edited!
+displayName=BeanShell Son \u0130\u015Flemcisi
+filename.displayName=Dosya Ad\u0131
+filename.shortDescription=BeanShell beti\u011Fi dosyas\u0131 (bu beti\u011Fin \u00FCzerine yazar)
+filenameGroup.displayName=Betik dosyas\u0131 (buradaki beti\u011Fin \u00FCzerine yazar)
+parameterGroup.displayName=BeanShell'e ge\u00E7ilecek parametreler (\=> Dizgi(String) Parametreler ve String []bsh.args)
+parameters.displayName=Parametreler
+parameters.shortDescription=BeanShell'e ge\u00E7ilecek parametreler (dosya ya da betik)
+script.shortDescription=BeanShell beti\u011Fi
+scripting.displayName=Betik (de\u011Fi\u015Fkenler\: ctx vars prev data log)
diff --git a/src/components/org/apache/jmeter/extractor/DebugPostProcessorResources_tr.properties b/src/components/org/apache/jmeter/extractor/DebugPostProcessorResources_tr.properties
new file mode 100644
index 000000000..d0bae5445
--- /dev/null
+++ b/src/components/org/apache/jmeter/extractor/DebugPostProcessorResources_tr.properties
@@ -0,0 +1,2 @@
+#Stored by I18NEdit, may be edited!
+displayName=Ay\u0131klama Son \u0130\u015Flemcisi
diff --git a/src/components/org/apache/jmeter/modifiers/BeanShellPreProcessorResources_tr.properties b/src/components/org/apache/jmeter/modifiers/BeanShellPreProcessorResources_tr.properties
new file mode 100644
index 000000000..cc3ca3ef9
--- /dev/null
+++ b/src/components/org/apache/jmeter/modifiers/BeanShellPreProcessorResources_tr.properties
@@ -0,0 +1,10 @@
+#Stored by I18NEdit, may be edited!
+displayName=BeanShell \u00D6n \u0130\u015Flemcisi
+filename.displayName=Dosya Ad\u0131
+filename.shortDescription=BeanShell betik dosyas\u0131 (buradaki beti\u011Fin \u00FCzerine yazar)
+filenameGroup.displayName=Betik dosyas\u0131 (buradaki beti\u011Fin \u00FCzerine yazar)
+parameterGroup.displayName=BeanShell'e ge\u00E7ilecek parametreler (\=> Dizgi (string) parametreler ve String [] bsh.args)
+parameters.displayName=Parametreler
+parameters.shortDescription=BeanShell'e ge\u00E7ilecek parametreler (dosya ya da betik)
+script.shortDescription=Beanshell beti\u011Fi
+scripting.displayName=Betik (de\u011Fi\u015Fkenler\: ctx vars prev sampler log)
diff --git a/src/components/org/apache/jmeter/sampler/DebugSamplerResources_tr.properties b/src/components/org/apache/jmeter/sampler/DebugSamplerResources_tr.properties
new file mode 100644
index 000000000..b8107ef2d
--- /dev/null
+++ b/src/components/org/apache/jmeter/sampler/DebugSamplerResources_tr.properties
@@ -0,0 +1,8 @@
+#Stored by I18NEdit, may be edited!
+displayJMeterProperties.displayName=JMeter ayarlar\u0131
+displayJMeterProperties.shortDescription=JMeter ayarlar\u0131n\u0131 g\u00F6ster ?
+displayJMeterVariables.displayName=JMeter de\u011Fi\u015Fkenleri
+displayJMeterVariables.shortDescription=JMeter de\u011Fi\u015Fkenlerini g\u00F6ster ?
+displayName=Ay\u0131klama \u00D6rnekleyicisi
+displaySystemProperties.displayName=Sistem ayarlar\u0131
+displaySystemProperties.shortDescription=Sistem ayarlar\u0131n\u0131 g\u00F6ster ?
diff --git a/src/components/org/apache/jmeter/timers/BeanShellTimerResources_tr.properties b/src/components/org/apache/jmeter/timers/BeanShellTimerResources_tr.properties
new file mode 100644
index 000000000..30e25d169
--- /dev/null
+++ b/src/components/org/apache/jmeter/timers/BeanShellTimerResources_tr.properties
@@ -0,0 +1,10 @@
+#Stored by I18NEdit, may be edited!
+displayName=BeanShell Zamanlay\u0131c\u0131
+filename.displayName=Dosya Ad\u0131
+filename.shortDescription=BeanShell beti\u011Fi dosyas\u0131 (buradaki beti\u011Fin \u00FCzerine yazar)
+filenameGroup.displayName=Betik dosyas\u0131 (buradaki beti\u011Fin \u00FCzerine yazar)
+parameterGroup.displayName=BeanShell'e ge\u00E7ilecek parametreler (\=> Dizgi(string) Parametreler ve String []bsh.args)
+parameters.displayName=Parametreler
+parameters.shortDescription=BeanShell'e ge\u00E7ilecek parametreler (dosya ya da betik)
+script.shortDescription=Gecikmeyi yaratacak BeanShell beti\u011Fi
+scripting.displayName=Betik (de\u011Fi\u015Fkenler\: ctx vars prev sampler log)
diff --git a/src/components/org/apache/jmeter/timers/ConstantThroughputTimerResources_tr.properties b/src/components/org/apache/jmeter/timers/ConstantThroughputTimerResources_tr.properties
new file mode 100644
index 000000000..f1a7aaf06
--- /dev/null
+++ b/src/components/org/apache/jmeter/timers/ConstantThroughputTimerResources_tr.properties
@@ -0,0 +1,12 @@
+#Stored by I18NEdit, may be edited!
+calcMode.1=sadece bu i\u015F par\u00E7ac\u0131\u011F\u0131
+calcMode.2=b\u00FCt\u00FCn aktif i\u015F par\u00E7ac\u0131klar\u0131
+calcMode.3=bu i\u015F par\u00E7ac\u0131\u011F\u0131 grubundaki t\u00FCm aktif i\u015F par\u00E7ac\u0131klar\u0131
+calcMode.4=t\u00FCm i\u015F par\u00E7ac\u0131klar\u0131 (payla\u015F\u0131ml\u0131)
+calcMode.5=bu i\u015F pa\u00E7ac\u0131\u011F\u0131 grubundaki t\u00FCm aktif i\u015F par\u00E7ac\u0131klar\u0131 (payla\u015F\u0131ml\u0131)
+calcMode.displayName=transfer oran\u0131 hesab\u0131n\u0131n yap\u0131laca\u011F\u0131 temel
+calcMode.shortDescription=Sabit Transfer Oran\u0131 Zamanlay\u0131c\u0131 eskiden her bir i\u015F par\u00E7ac\u0131\u011F\u0131 i\u00E7in, testteki tek i\u015F par\u00E7ac\u0131\u011F\u0131ym\u0131\u015Fcas\u0131na gecikirken; \u015Fimdi gecikme hesab\u0131 testteki veya i\u015F par\u00E7ac\u0131\u011F\u0131 grubundaki aktif i\u015F par\u00E7ac\u0131\u011F\u0131 say\u0131s\u0131na g\u00F6re yap\u0131lmakta.
+delay.displayName=Etkilenen her \u00F6rnekleyiciden \u00F6nce gecikme
+displayName=Sabit Transfer Oran\u0131 Zamanlay\u0131c\u0131
+throughput.displayName=Hedeflenen transfer oran\u0131 (\u00F6rnek/dak.)
+throughput.shortDescription=Dakika, i\u015F par\u00E7ac\u0131\u011F\u0131 ve etkilenen \u00F6rnekleyici ba\u015F\u0131na istedi\u011Finiz en fazla \u00F6rnek say\u0131s\u0131.
diff --git a/src/components/org/apache/jmeter/timers/SyncTimerResources_tr.properties b/src/components/org/apache/jmeter/timers/SyncTimerResources_tr.properties
new file mode 100644
index 000000000..0f4c23919
--- /dev/null
+++ b/src/components/org/apache/jmeter/timers/SyncTimerResources_tr.properties
@@ -0,0 +1,5 @@
+#Stored by I18NEdit, may be edited!
+displayName=E\u015Fzamanl\u0131 Zamanlay\u0131c\u0131
+groupSize.displayName=Grup ba\u015F\u0131na d\u00FC\u015Fen sahte kullan\u0131c\u0131 say\u0131s\u0131
+groupSize.shortDescription=E\u015F zamanl\u0131 blo\u011Fun sal\u0131verilmesini tetikleyen sahte kullan\u0131c\u0131 say\u0131s\u0131n\u0131 belirtin (\u00F6n tan\u0131ml\u0131 olarak '0' t\u00FCm kullan\u0131c\u0131lar anlam\u0131na gelmektedir)
+grouping.displayName=Gruplama
diff --git a/src/components/org/apache/jmeter/visualizers/BeanShellListenerResources_tr.properties b/src/components/org/apache/jmeter/visualizers/BeanShellListenerResources_tr.properties
new file mode 100644
index 000000000..3112d1386
--- /dev/null
+++ b/src/components/org/apache/jmeter/visualizers/BeanShellListenerResources_tr.properties
@@ -0,0 +1,10 @@
+#Stored by I18NEdit, may be edited!
+displayName=BeanShell Alg\u0131lay\u0131c\u0131
+filename.displayName=Dosya Ad\u0131
+filename.shortDescription=BeanShell betik dosyas\u0131 (buradaki beti\u011Fin \u00FCzerine yazar)
+filenameGroup.displayName=Betik dosyas\u0131 (buradaki beti\u011Fin \u00FCzerine yazar)
+parameterGroup.displayName=BeanShell'e ge\u00E7ilecek parametreler (\=> Dizgi (String) Parametreler ve String []bsh.args)
+parameters.displayName=Parametreler
+parameters.shortDescription=BeanShell'e ge\u00E7ilecek parametreler (dosya ya da betik)
+script.shortDescription=Beanshell beti\u011Fi
+scripting.displayName=Betik (de\u011Fi\u015Fkenler\: ctx vars sampleEvent sampleResult log)
diff --git a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
index 5ec11ddca..6e7d1f40d 100644
--- a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
+++ b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
@@ -1,630 +1,636 @@
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
 
 package org.apache.jmeter.gui.util;
 
 import java.awt.Component;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 
 import javax.swing.JComponent;
 import javax.swing.JMenu;
 import javax.swing.JMenuBar;
 import javax.swing.JMenuItem;
 import javax.swing.JPopupMenu;
 import javax.swing.MenuElement;
 import javax.swing.UIManager;
 
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jmeter.gui.action.LoadRecentProject;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.LocaleChangeEvent;
 import org.apache.jmeter.util.LocaleChangeListener;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class JMeterMenuBar extends JMenuBar implements LocaleChangeListener {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
     private JMenu fileMenu;
 
     private JMenuItem file_save_as;
 
     private JMenuItem file_selection_as;
 
     private JMenuItem file_revert;
 
     private JMenuItem file_load;
 
     private List file_load_recent_files;
 
     private JMenuItem file_merge;
 
     private JMenuItem file_exit;
 
     private JMenuItem file_close;
 
     private JMenu editMenu;
 
     private JMenu edit_add;
 
 	// JMenu edit_add_submenu;
     private JMenuItem edit_remove; // TODO - should this be created?
 
     private JMenu runMenu;
 
     private JMenuItem run_start;
 
     private JMenu remote_start;
 
     private JMenuItem remote_start_all;
 
     private Collection remote_engine_start;
 
     private JMenuItem run_stop;
 
 	private JMenuItem run_shut;
     
     private JMenu remote_stop;
 
     private JMenuItem remote_stop_all;
 
     private Collection remote_engine_stop;
 
     private JMenuItem run_clear;
 
     private JMenuItem run_clearAll;
 
 	// JMenu reportMenu;
 	// JMenuItem analyze;
     private JMenu optionsMenu;
 
     private JMenu lafMenu;
 
     private JMenuItem sslManager;
 
     private JMenu helpMenu;
 
     private JMenuItem help_about;
 
     private String[] remoteHosts;
 
 	private JMenu remote_exit;
 
 	private JMenuItem remote_exit_all;
 
 	private Collection remote_engine_exit;
 
 	public JMeterMenuBar() {
 		// List for recent files menu items
         file_load_recent_files = new LinkedList();
 		// Lists for remote engines menu items
 		remote_engine_start = new LinkedList();
 		remote_engine_stop = new LinkedList();
 		remote_engine_exit = new LinkedList();
 		remoteHosts = JOrphanUtils.split(JMeterUtils.getPropDefault("remote_hosts", ""), ","); //$NON-NLS-1$
 		if (remoteHosts.length == 1 && remoteHosts[0].equals("")) {
 			remoteHosts = new String[0];
 		}
 		this.getRemoteItems();
 		createMenuBar();
 	}
 
 	public void setFileSaveEnabled(boolean enabled) {
         if(file_save_as != null) {
             file_save_as.setEnabled(enabled);
         }
 	}
 
 	public void setFileLoadEnabled(boolean enabled) {
 		if (file_load != null) {
 			file_load.setEnabled(enabled);
 		}
 		if (file_merge != null) {
 			file_merge.setEnabled(enabled);
 		}
 	}
 
     public void setFileRevertEnabled(boolean enabled) {
         if(file_revert != null) {
             file_revert.setEnabled(enabled);
         }
     }
     
     public void setProjectFileLoaded(String file) {
         if(file_load_recent_files != null && file != null) {
             LoadRecentProject.updateRecentFileMenuItems(file_load_recent_files, file);
         }
     }
 
 	public void setEditEnabled(boolean enabled) {
 		if (editMenu != null) {
 			editMenu.setEnabled(enabled);
 		}
 	}
 
 	public void setEditAddMenu(JMenu menu) {
 		// If the Add menu already exists, remove it.
 		if (edit_add != null) {
 			editMenu.remove(edit_add);
 		}
 		// Insert the Add menu as the first menu item in the Edit menu.
 		edit_add = menu;
 		editMenu.insert(edit_add, 0);
 	}
 
 	public void setEditMenu(JPopupMenu menu) {
 		if (menu != null) {
 			editMenu.removeAll();
 			Component[] comps = menu.getComponents();
 			for (int i = 0; i < comps.length; i++) {
 				editMenu.add(comps[i]);
 			}
 			editMenu.setEnabled(true);
 		} else {
 			editMenu.setEnabled(false);
 		}
 	}
 
 	public void setEditAddEnabled(boolean enabled) {
 		// There was a NPE being thrown without the null check here.. JKB
 		if (edit_add != null) {
 			edit_add.setEnabled(enabled);
 		}
 		// If we are enabling the Edit-->Add menu item, then we also need to
 		// enable the Edit menu. The Edit menu may already be enabled, but
 		// there's no harm it trying to enable it again.
 		if (enabled) {
 			setEditEnabled(true);
 		} else {
 			// If we are disabling the Edit-->Add menu item and the
 			// Edit-->Remove menu item is disabled, then we also need to
 			// disable the Edit menu.
 			// The Java Look and Feel Guidelines say to disable a menu if all
 			// menu items are disabled.
 			if (!edit_remove.isEnabled()) {
 				editMenu.setEnabled(false);
 			}
 		}
 	}
 
 	public void setEditRemoveEnabled(boolean enabled) {
 		edit_remove.setEnabled(enabled);
 		// If we are enabling the Edit-->Remove menu item, then we also need to
 		// enable the Edit menu. The Edit menu may already be enabled, but
 		// there's no harm it trying to enable it again.
 		if (enabled) {
 			setEditEnabled(true);
 		} else {
 			// If we are disabling the Edit-->Remove menu item and the
 			// Edit-->Add menu item is disabled, then we also need to disable
 			// the Edit menu.
 			// The Java Look and Feel Guidelines say to disable a menu if all
 			// menu items are disabled.
 			if (!edit_add.isEnabled()) {
 				editMenu.setEnabled(false);
 			}
 		}
 	}
 
 	/**
 	 * Creates the MenuBar for this application. I believe in my heart that this
 	 * should be defined in a file somewhere, but that is for later.
 	 */
 	public void createMenuBar() {
 		makeFileMenu();
 		makeEditMenu();
 		makeRunMenu();
 		makeOptionsMenu();
 		makeHelpMenu();
 		this.add(fileMenu);
 		this.add(editMenu);
 		this.add(runMenu);
 		this.add(optionsMenu);
 		this.add(helpMenu);
 	}
 
 	private void makeHelpMenu() {
 		// HELP MENU
 		helpMenu = new JMenu(JMeterUtils.getResString("help")); //$NON-NLS-1$
 		helpMenu.setMnemonic('H');
 		JMenuItem contextHelp = new JMenuItem(JMeterUtils.getResString("help"), 'H'); //$NON-NLS-1$
 		contextHelp.setActionCommand(ActionNames.HELP);
 		contextHelp.setAccelerator(KeyStrokes.HELP);
 		contextHelp.addActionListener(ActionRouter.getInstance());
 
         JMenuItem whatClass = new JMenuItem(JMeterUtils.getResString("help_node"), 'W');//$NON-NLS-1$
         whatClass.setActionCommand(ActionNames.WHAT_CLASS);
         whatClass.setAccelerator(KeyStrokes.WHAT_CLASS);
         whatClass.addActionListener(ActionRouter.getInstance());
 
         JMenuItem setDebug = new JMenuItem(JMeterUtils.getResString("debug_on"));//$NON-NLS-1$
         setDebug.setActionCommand(ActionNames.DEBUG_ON);
         setDebug.setAccelerator(KeyStrokes.DEBUG_ON);
         setDebug.addActionListener(ActionRouter.getInstance());
 
         JMenuItem resetDebug = new JMenuItem(JMeterUtils.getResString("debug_off"));//$NON-NLS-1$
         resetDebug.setActionCommand(ActionNames.DEBUG_OFF);
         resetDebug.setAccelerator(KeyStrokes.DEBUG_OFF);
         resetDebug.addActionListener(ActionRouter.getInstance());
 
         help_about = new JMenuItem(JMeterUtils.getResString("about"), 'A'); //$NON-NLS-1$
 		help_about.setActionCommand(ActionNames.ABOUT);
 		help_about.addActionListener(ActionRouter.getInstance());
 		helpMenu.add(contextHelp);
         helpMenu.addSeparator();
         helpMenu.add(whatClass);
         helpMenu.add(setDebug);
         helpMenu.add(resetDebug);
         helpMenu.addSeparator();
 		helpMenu.add(help_about);
 	}
 
 	private void makeOptionsMenu() {
 		// OPTIONS MENU
 		optionsMenu = new JMenu(JMeterUtils.getResString("option")); //$NON-NLS-1$
 		JMenuItem functionHelper = new JMenuItem(JMeterUtils.getResString("function_dialog_menu_item"), 'F'); //$NON-NLS-1$
 		functionHelper.addActionListener(ActionRouter.getInstance());
 		functionHelper.setActionCommand(ActionNames.FUNCTIONS);
 		functionHelper.setAccelerator(KeyStrokes.FUNCTIONS);
 		lafMenu = new JMenu(JMeterUtils.getResString("appearance")); //$NON-NLS-1$
 		UIManager.LookAndFeelInfo lafs[] = UIManager.getInstalledLookAndFeels();
 		for (int i = 0; i < lafs.length; ++i) {
 			JMenuItem laf = new JMenuItem(lafs[i].getName());
 			laf.addActionListener(ActionRouter.getInstance());
 			laf.setActionCommand(ActionNames.LAF_PREFIX + lafs[i].getClassName());
 			lafMenu.setMnemonic('L');
 			lafMenu.add(laf);
 		}
 		optionsMenu.setMnemonic('O');
 		optionsMenu.add(functionHelper);
 		optionsMenu.add(lafMenu);
 		if (SSLManager.isSSLSupported()) {
 			sslManager = new JMenuItem(JMeterUtils.getResString("sslmanager")); //$NON-NLS-1$
 			sslManager.addActionListener(ActionRouter.getInstance());
 			sslManager.setActionCommand(ActionNames.SSL_MANAGER);
 			sslManager.setMnemonic('S');
 			sslManager.setAccelerator(KeyStrokes.SSL_MANAGER);
 			optionsMenu.add(sslManager);
 		}
 		optionsMenu.add(makeLanguageMenu());
 		
 		JMenuItem collapse = new JMenuItem(JMeterUtils.getResString("menu_collapse_all")); //$NON-NLS-1$
 		collapse.addActionListener(ActionRouter.getInstance());
 		collapse.setActionCommand(ActionNames.COLLAPSE_ALL);
 		collapse.setAccelerator(KeyStrokes.COLLAPSE_ALL);
 		optionsMenu.add(collapse);
 		
 		JMenuItem expand = new JMenuItem(JMeterUtils.getResString("menu_expand_all")); //$NON-NLS-1$
 		expand.addActionListener(ActionRouter.getInstance());
 		expand.setActionCommand(ActionNames.EXPAND_ALL);
 		expand.setAccelerator(KeyStrokes.EXPAND_ALL);
 		optionsMenu.add(expand);
 	}
 
 	// TODO fetch list of languages from a file?
 	// N.B. Changes to language list need to be reflected in
 	// resources/PackageTest.java
 	private JMenu makeLanguageMenu() {
 		/*
 		 * Note: the item name is used by ChangeLanguage to create a Locale for
 		 * that language, so need to ensure that the language strings are valid
 		 * If they exist, use the Locale language constants
 		 */
 		// TODO: do accelerator keys make sense? The key may not be present in
 		// translations
 		JMenu languageMenu = new JMenu(JMeterUtils.getResString("choose_language")); //$NON-NLS-1$
 		languageMenu.setMnemonic('C');
 		// add english
 		JMenuItem english = new JMenuItem(JMeterUtils.getResString("en"), 'E'); //$NON-NLS-1$
 		english.addActionListener(ActionRouter.getInstance());
 		english.setActionCommand(ActionNames.CHANGE_LANGUAGE);
 		english.setName(Locale.ENGLISH.getLanguage());
 		languageMenu.add(english);
 		// add Japanese
 		JMenuItem japanese = new JMenuItem(JMeterUtils.getResString("jp"), 'J'); //$NON-NLS-1$
 		japanese.addActionListener(ActionRouter.getInstance());
 		japanese.setActionCommand(ActionNames.CHANGE_LANGUAGE);
 		japanese.setName(Locale.JAPANESE.getLanguage());
 		languageMenu.add(japanese);
 		// add Norwegian
 		JMenuItem norway = new JMenuItem(JMeterUtils.getResString("no"), 'N'); //$NON-NLS-1$
 		norway.addActionListener(ActionRouter.getInstance());
 		norway.setActionCommand(ActionNames.CHANGE_LANGUAGE);
 		norway.setName("no"); // No default for Norwegian
 		languageMenu.add(norway);
 		// add German
 		JMenuItem german = new JMenuItem(JMeterUtils.getResString("de"), 'G'); //$NON-NLS-1$
 		german.addActionListener(ActionRouter.getInstance());
 		german.setActionCommand(ActionNames.CHANGE_LANGUAGE);
 		german.setName(Locale.GERMAN.getLanguage());
 		languageMenu.add(german);
 		// add French
 		JMenuItem french = new JMenuItem(JMeterUtils.getResString("fr"), 'F'); //$NON-NLS-1$
 		french.addActionListener(ActionRouter.getInstance());
 		french.setActionCommand(ActionNames.CHANGE_LANGUAGE);
 		french.setName(Locale.FRENCH.getLanguage());
 		languageMenu.add(french);
 		// add chinese (simple)
 		JMenuItem chineseSimple = new JMenuItem(JMeterUtils.getResString("zh_cn")); //$NON-NLS-1$
 		chineseSimple.addActionListener(ActionRouter.getInstance());
 		chineseSimple.setActionCommand(ActionNames.CHANGE_LANGUAGE);
 		chineseSimple.setName(Locale.SIMPLIFIED_CHINESE.toString());
 		languageMenu.add(chineseSimple);
 		// add chinese (traditional)
 		JMenuItem chineseTrad = new JMenuItem(JMeterUtils.getResString("zh_tw")); //$NON-NLS-1$
 		chineseTrad.addActionListener(ActionRouter.getInstance());
 		chineseTrad.setActionCommand(ActionNames.CHANGE_LANGUAGE);
 		chineseTrad.setName(Locale.TRADITIONAL_CHINESE.toString());
 		languageMenu.add(chineseTrad);
 		// add spanish
 		JMenuItem spanish = new JMenuItem(JMeterUtils.getResString("es")); //$NON-NLS-1$
 		spanish.addActionListener(ActionRouter.getInstance());
 		spanish.setActionCommand(ActionNames.CHANGE_LANGUAGE);
 		spanish.setName("es"); //$NON-NLS-1$
 		languageMenu.add(spanish);
+		// add turkish
+		JMenuItem turkish = new JMenuItem(JMeterUtils.getResString("tr")); //$NON-NLS-1$
+		turkish.addActionListener(ActionRouter.getInstance());
+		turkish.setActionCommand(ActionNames.CHANGE_LANGUAGE);
+		turkish.setName("tr"); //$NON-NLS-1$
+		languageMenu.add(turkish);
 		return languageMenu;
 	}
 
 	private void makeRunMenu() {
 		// RUN MENU
 		runMenu = new JMenu(JMeterUtils.getResString("run")); //$NON-NLS-1$
 		runMenu.setMnemonic('R');
 		run_start = new JMenuItem(JMeterUtils.getResString("start"), 'S'); //$NON-NLS-1$
 		run_start.setAccelerator(KeyStrokes.ACTION_START);
 		run_start.addActionListener(ActionRouter.getInstance());
 		run_start.setActionCommand(ActionNames.ACTION_START);
 		run_stop = new JMenuItem(JMeterUtils.getResString("stop"), 'T'); //$NON-NLS-1$
 		run_stop.setAccelerator(KeyStrokes.ACTION_STOP);
 		run_stop.setEnabled(false);
 		run_stop.addActionListener(ActionRouter.getInstance());
 		run_stop.setActionCommand(ActionNames.ACTION_STOP);
 
 		run_shut = new JMenuItem(JMeterUtils.getResString("shutdown"), 'Y'); //$NON-NLS-1$
 		run_shut.setAccelerator(KeyStrokes.ACTION_SHUTDOWN);
 		run_shut.setEnabled(false);
 		run_shut.addActionListener(ActionRouter.getInstance());
 		run_shut.setActionCommand(ActionNames.ACTION_SHUTDOWN);
 
 		run_clear = new JMenuItem(JMeterUtils.getResString("clear"), 'C'); //$NON-NLS-1$
 		run_clear.addActionListener(ActionRouter.getInstance());
 		run_clear.setActionCommand(ActionNames.CLEAR);
 		run_clear.setAccelerator(KeyStrokes.CLEAR);
 		
 		run_clearAll = new JMenuItem(JMeterUtils.getResString("clear_all"), 'a'); //$NON-NLS-1$
 		run_clearAll.addActionListener(ActionRouter.getInstance());
 		run_clearAll.setActionCommand(ActionNames.CLEAR_ALL);
 		run_clearAll.setAccelerator(KeyStrokes.CLEAR_ALL);
 		
 		runMenu.add(run_start);
 		if (remote_start != null) {
 			runMenu.add(remote_start);
 		}
 		remote_start_all = new JMenuItem(JMeterUtils.getResString("remote_start_all")); //$NON-NLS-1$
 		remote_start_all.setName("remote_start_all"); //$NON-NLS-1$
 		remote_start_all.setAccelerator(KeyStrokes.REMOTE_START_ALL);
 		remote_start_all.addActionListener(ActionRouter.getInstance());
 		remote_start_all.setActionCommand(ActionNames.REMOTE_START_ALL);
 		runMenu.add(remote_start_all);
 		runMenu.add(run_stop);
 		runMenu.add(run_shut);
 		if (remote_stop != null) {
 			runMenu.add(remote_stop);
 		}
 		remote_stop_all = new JMenuItem(JMeterUtils.getResString("remote_stop_all"), 'X'); //$NON-NLS-1$
 		remote_stop_all.setAccelerator(KeyStrokes.REMOTE_STOP_ALL);
 		remote_stop_all.addActionListener(ActionRouter.getInstance());
 		remote_stop_all.setActionCommand(ActionNames.REMOTE_STOP_ALL);
 		runMenu.add(remote_stop_all);
 
 		if (remote_exit != null) {
 			runMenu.add(remote_exit);
 		}
 		remote_exit_all = new JMenuItem(JMeterUtils.getResString("remote_exit_all")); //$NON-NLS-1$
 		remote_exit_all.addActionListener(ActionRouter.getInstance());
 		remote_exit_all.setActionCommand(ActionNames.REMOTE_EXIT_ALL);
 		runMenu.add(remote_exit_all);
 
 		runMenu.addSeparator();
 		runMenu.add(run_clear);
 		runMenu.add(run_clearAll);
 	}
 
 	private void makeEditMenu() {
 		// EDIT MENU
 		editMenu = new JMenu(JMeterUtils.getResString("edit")); //$NON-NLS-1$
         editMenu.setMnemonic('E');
 		// From the Java Look and Feel Guidelines: If all items in a menu
 		// are disabled, then disable the menu. Makes sense.
 		editMenu.setEnabled(false);
 	}
 
 	private void makeFileMenu() {
 		// FILE MENU
 		fileMenu = new JMenu(JMeterUtils.getResString("file")); //$NON-NLS-1$
 		fileMenu.setMnemonic('F');
 		JMenuItem file_save = new JMenuItem(JMeterUtils.getResString("save"), 'S'); //$NON-NLS-1$
 		file_save.setAccelerator(KeyStrokes.SAVE);
 		file_save.setActionCommand(ActionNames.SAVE);
 		file_save.addActionListener(ActionRouter.getInstance());
 		file_save.setEnabled(true);
 
 		file_save_as = new JMenuItem(JMeterUtils.getResString("save_all_as"), 'A'); //$NON-NLS-1$
 		file_save_as.setAccelerator(KeyStrokes.SAVE_ALL_AS);
 		file_save_as.setActionCommand(ActionNames.SAVE_ALL_AS);
 		file_save_as.addActionListener(ActionRouter.getInstance());
 		file_save_as.setEnabled(true);
 
 		file_selection_as = new JMenuItem(JMeterUtils.getResString("save_as")); //$NON-NLS-1$
 		file_selection_as.setActionCommand(ActionNames.SAVE_AS);
 		file_selection_as.addActionListener(ActionRouter.getInstance());
 		file_selection_as.setEnabled(true);
 
 		file_revert = new JMenuItem(JMeterUtils.getResString("revert_project"), 'R'); //$NON-NLS-1$
 		file_revert.setActionCommand(ActionNames.REVERT_PROJECT);
 		file_revert.addActionListener(ActionRouter.getInstance());
 		file_revert.setEnabled(false);
 
 		file_load = new JMenuItem(JMeterUtils.getResString("menu_open"), 'O'); //$NON-NLS-1$
 		file_load.setAccelerator(KeyStrokes.OPEN);
 		file_load.addActionListener(ActionRouter.getInstance());
 		// Set default SAVE menu item to disabled since the default node that
 		// is selected is ROOT, which does not allow items to be inserted.
 		file_load.setEnabled(false);
 		file_load.setActionCommand(ActionNames.OPEN);
 
 		file_close = new JMenuItem(JMeterUtils.getResString("menu_close"), 'C'); //$NON-NLS-1$
 		file_close.setAccelerator(KeyStrokes.CLOSE);
 		file_close.setActionCommand(ActionNames.CLOSE);
 		file_close.addActionListener(ActionRouter.getInstance());
 
 		file_exit = new JMenuItem(JMeterUtils.getResString("exit"), 'X'); //$NON-NLS-1$
 		file_exit.setAccelerator(KeyStrokes.EXIT);
 		file_exit.setActionCommand(ActionNames.EXIT);
 		file_exit.addActionListener(ActionRouter.getInstance());
 
 		file_merge = new JMenuItem(JMeterUtils.getResString("menu_merge"), 'M'); //$NON-NLS-1$
 		// file_merge.setAccelerator(
 		// KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
 		file_merge.addActionListener(ActionRouter.getInstance());
 		// Set default SAVE menu item to disabled since the default node that
 		// is selected is ROOT, which does not allow items to be inserted.
 		file_merge.setEnabled(false);
 		file_merge.setActionCommand(ActionNames.MERGE);
 
 		fileMenu.add(file_close);
 		fileMenu.add(file_load);
 		fileMenu.add(file_merge);
         fileMenu.addSeparator();
 		fileMenu.add(file_save);
 		fileMenu.add(file_save_as);
 		fileMenu.add(file_selection_as);
         fileMenu.add(file_revert);
         fileMenu.addSeparator();
         // Add the recent files, which will also add a separator that is
         // visible when needed
         file_load_recent_files = LoadRecentProject.getRecentFileMenuItems();
         for(Iterator i = file_load_recent_files.iterator(); i.hasNext();) {
             fileMenu.add((JComponent)i.next());
         }
         fileMenu.add(file_exit);
 	}
 
 	public void setRunning(boolean running, String host) {
 		log.info("setRunning(" + running + "," + host + ")");
 
 		Iterator iter = remote_engine_start.iterator();
 		Iterator iter2 = remote_engine_stop.iterator();
 		Iterator iter3 = remote_engine_exit.iterator();
 		while (iter.hasNext() && iter2.hasNext() && iter3.hasNext()) {
 			JMenuItem start = (JMenuItem) iter.next();
 			JMenuItem stop = (JMenuItem) iter2.next();
 			JMenuItem exit = (JMenuItem) iter3.next();
 			if (start.getText().equals(host)) {
 				log.debug("Found start host: " + start.getText());
 				start.setEnabled(!running);
 			}
 			if (stop.getText().equals(host)) {
 				log.debug("Found stop  host: " + stop.getText());
 				stop.setEnabled(running);
 			}
 			if (exit.getText().equals(host)) {
 				log.debug("Found exit  host: " + exit.getText());
 				exit.setEnabled(true);
 			}
 		}
 	}
 
 	public void setEnabled(boolean enable) {
 		run_start.setEnabled(!enable);
 		run_stop.setEnabled(enable);
 		run_shut.setEnabled(enable);
 	}
 
 	private void getRemoteItems() {
 		if (remoteHosts.length > 0) {
 			remote_start = new JMenu(JMeterUtils.getResString("remote_start")); //$NON-NLS-1$
 			remote_stop = new JMenu(JMeterUtils.getResString("remote_stop")); //$NON-NLS-1$
 			remote_exit = new JMenu(JMeterUtils.getResString("remote_exit")); //$NON-NLS-1$
 
 			for (int i = 0; i < remoteHosts.length; i++) {
 				remoteHosts[i] = remoteHosts[i].trim();
 				JMenuItem item = new JMenuItem(remoteHosts[i]);
 				item.setActionCommand(ActionNames.REMOTE_START);
 				item.setName(remoteHosts[i]);
 				item.addActionListener(ActionRouter.getInstance());
 				remote_engine_start.add(item);
 				remote_start.add(item);
 				item = new JMenuItem(remoteHosts[i]);
 				item.setActionCommand(ActionNames.REMOTE_STOP);
 				item.setName(remoteHosts[i]);
 				item.addActionListener(ActionRouter.getInstance());
 				item.setEnabled(false);
 				remote_engine_stop.add(item);
 				remote_stop.add(item);
 				item = new JMenuItem(remoteHosts[i]);
 				item.setActionCommand(ActionNames.REMOTE_EXIT);
 				item.setName(remoteHosts[i]);
 				item.addActionListener(ActionRouter.getInstance());
 				item.setEnabled(false);
 				remote_engine_exit.add(item);
 				remote_exit.add(item);
 			}
 		}
 	}
 
 	/**
 	 * Processes a locale change notification. Changes the texts in all menus to
 	 * the new language.
 	 */
 	public void localeChanged(LocaleChangeEvent event) {
 		updateMenuElement(fileMenu);
 		updateMenuElement(editMenu);
 		updateMenuElement(runMenu);
 		updateMenuElement(optionsMenu);
 		updateMenuElement(helpMenu);
 	}
 
 	/**
 	 * Refreshes all texts in the menu and all submenus to a new locale.
 	 */
 	private void updateMenuElement(MenuElement menu) {
 		Component component = menu.getComponent();
 
 		if (component.getName() != null) {
 			if (component instanceof JMenu) {
 				((JMenu) component).setText(JMeterUtils.getResString(component.getName()));
 			} else {
 				((JMenuItem) component).setText(JMeterUtils.getResString(component.getName()));
 			}
 		}
 
 		MenuElement[] subelements = menu.getSubElements();
 
 		for (int i = 0; i < subelements.length; i++) {
 			updateMenuElement(subelements[i]);
 		}
 	}
 }
diff --git a/src/core/org/apache/jmeter/resources/messages.properties b/src/core/org/apache/jmeter/resources/messages.properties
index 00db5ed64..9675e0f8a 100644
--- a/src/core/org/apache/jmeter/resources/messages.properties
+++ b/src/core/org/apache/jmeter/resources/messages.properties
@@ -1,879 +1,880 @@
 # Warning: JMeterUtils.getResString() replaces space with '_'
 # and converts keys to lowercase before lookup
 # => All keys in this file must also be lower case or they won't match
 #
 about=About Apache JMeter
 add=Add
 add_as_child=Add as Child
 add_parameter=Add Variable
 add_pattern=Add Pattern\:
 add_test=Add Test
 add_user=Add User
 add_value=Add Value
 addtest=Add test
 aggregate_graph=Statistical Graphs
 aggregate_graph_column=Column
 aggregate_graph_display=Display Graph
 aggregate_graph_height=Height
 aggregate_graph_max_length_xaxis_label=Max length of x-axis label
 aggregate_graph_ms=Milliseconds
 aggregate_graph_response_time=Response Time
 aggregate_graph_save=Save Graph
 aggregate_graph_save_table=Save Table Data
 aggregate_graph_save_table_header=Save Table Header
 aggregate_graph_title=Aggregate Graph
 aggregate_graph_user_title=Title for Graph
 aggregate_graph_width=Width
 aggregate_report=Aggregate Report
 aggregate_report_90=90%
 aggregate_report_90%_line=90% Line
 aggregate_report_bandwidth=KB/sec
 aggregate_report_count=# Samples
 aggregate_report_error=Error
 aggregate_report_error%=Error %
 aggregate_report_max=Max
 aggregate_report_median=Median
 aggregate_report_min=Min
 aggregate_report_rate=Throughput
 aggregate_report_stddev=Std. Dev.
 aggregate_report_total_label=TOTAL
 als_message=Note\: The Access Log Parser is generic in design and allows you to plugin
 als_message2=your own parser. To do so, implement the LogParser, add the jar to the
 als_message3=/lib directory and enter the class in the sampler.
 analyze=Analyze Data File...
 anchor_modifier_title=HTML Link Parser
 appearance=Look and Feel
 argument_must_not_be_negative=The Argument must not be negative\!
 assertion_assume_success=Ignore Status
 assertion_code_resp=Response Code
 assertion_contains=Contains
 assertion_equals=Equals
 assertion_headers=Response Headers
 assertion_matches=Matches
 assertion_message_resp=Response Message
 assertion_not=Not
 assertion_pattern_match_rules=Pattern Matching Rules
 assertion_patterns_to_test=Patterns to Test
 assertion_resp_field=Response Field to Test
 assertion_substring=Substring
 assertion_text_resp=Text Response
 assertion_textarea_label=Assertions\:
 assertion_title=Response Assertion
 assertion_url_samp=URL Sampled
 assertion_visualizer_title=Assertion Results
 attribute=Attribute
 attrs=Attributes
 auth_base_url=Base URL
 auth_manager_title=HTTP Authorization Manager
 auths_stored=Authorizations Stored in the Authorization Manager
 average=Average
 average_bytes=Avg. Bytes
 bind=Thread Bind
 browse=Browse...
 bsf_sampler_title=BSF Sampler
 bsf_script=Script to run (variables: log, Label, FileName, Parameters, args[], SampleResult, ctx, vars)
 bsf_script_file=Script file to run
 bsf_script_language=Scripting language\:
 bsf_script_parameters=Parameters to pass to script/file\:
 bsh_assertion_script=Script (see below for variables that are defined)
 bsh_assertion_script_variables=The following variables are defined for the script:\nRead/Write: Failure, FailureMessage, SampleResult, vars, log.\nReadOnly: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
 bsh_assertion_title=BeanShell Assertion
 bsh_function_expression=Expression to evaluate
 bsh_sampler_title=BeanShell Sampler
 bsh_script=Script (see below for variables that are defined)
 bsh_script_variables=The following variables are defined for the script:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, log
 bsh_script_file=Script file
 bsh_script_parameters=Parameters (-> String Parameters and String []bsh.args)
 busy_testing=I'm busy testing, please stop the test before changing settings
 cache_session_id=Cache Session Id?
 cancel=Cancel
 cancel_revert_project=There are test items that have not been saved.  Do you wish to revert to the previously saved test plan?
 cancel_exit_to_save=There are test items that have not been saved.  Do you wish to save before exiting?
 cancel_new_to_save=There are test items that have not been saved.  Do you wish to save before clearing the test plan?
 choose_function=Choose a function
 choose_language=Choose Language
 clear=Clear
 clear_all=Clear All
 clear_cookies_per_iter=Clear cookies each iteration?
 column_delete_disallowed=Deleting this column is not permitted
 column_number=Column number of CSV file | next | *alias
 compare=Compare
 comparefilt=Compare filter
 config_element=Config Element
 config_save_settings=Configure
 configure_wsdl=Configure
 constant_throughput_timer_memo=Add a delay between sampling to attain constant throughput
 constant_timer_delay=Thread Delay (in milliseconds)\:
 constant_timer_memo=Add a constant delay between sampling
 constant_timer_title=Constant Timer
 content_encoding=Content encoding\:
 controller=Controller
 cookie_manager_policy=Cookie Policy
 cookie_manager_title=HTTP Cookie Manager
 cookies_stored=Cookies Stored in the Cookie Manager
 copy=Copy
 corba_config_title=CORBA Sampler Config
 corba_input_data_file=Input Data File\:
 corba_methods=Choose method to invoke\:
 corba_name_server=Name Server\:
 corba_port=Port Number\:
 corba_request_data=Input Data
 corba_sample_title=CORBA Sampler
 counter_config_title=Counter
 counter_per_user=Track counter independently for each user
 countlim=Size limit
 csvread_file_file_name=CSV file to get values from | *alias
 cut=Cut
 cut_paste_function=Copy and paste function string
 database_conn_pool_max_usage=Max Usage For Each Connection\:
 database_conn_pool_props=Database Connection Pool
 database_conn_pool_size=Number of Connections in Pool\:
 database_conn_pool_title=JDBC Database Connection Pool Defaults
 database_driver_class=Driver Class\:
 database_login_title=JDBC Database Login Defaults
 database_sql_query_string=SQL Query String\:
 database_sql_query_title=JDBC SQL Query Defaults
 database_testing_title=JDBC Request
 database_url=JDBC URL\:
 database_url_jdbc_props=Database URL and JDBC Driver
 ddn=DN
 de=German
 debug_off=Disable debug
 debug_on=Enable debug
 default_parameters=Default Parameters
 default_value_field=Default Value\:
 delay=Startup delay (seconds)
 delete=Delete
 delete_parameter=Delete Variable
 delete_test=Delete Test
 delete_user=Delete User
 deltest=Deletion test
 deref=Dereference aliases
 disable=Disable
 distribution_graph_title=Distribution Graph (alpha)
 distribution_note1=The graph will update every 10 samples
 dn=DN
 domain=Domain
 done=Done
 duration=Duration (seconds)
 duration_assertion_duration_test=Duration to Assert
 duration_assertion_failure=The operation lasted too long\: It took {0} milliseconds, but should not have lasted longer than {1} milliseconds.
 duration_assertion_input_error=Please enter a valid positive integer.
 duration_assertion_label=Duration in milliseconds\:
 duration_assertion_title=Duration Assertion
 edit=Edit
 email_results_title=Email Results
 en=English
 enable=Enable
 encode?=Encode?
 encoded_value=URL Encoded Value
 endtime=End Time  
 entry_dn=Entry DN
 entrydn=Entry DN
 error_loading_help=Error loading help page
 error_occurred=Error Occurred
 error_title=Error
 es=Spanish
 eval_name_param=Text containing variable and function references
 evalvar_name_param=Name of variable
 example_data=Sample Data
 example_title=Example Sampler
 exit=Exit
 expiration=Expiration
 field_name=Field name
 file=File
 file_already_in_use=That file is already in use
 file_visualizer_append=Append to Existing Data File
 file_visualizer_auto_flush=Automatically Flush After Each Data Sample
 file_visualizer_browse=Browse...
 file_visualizer_close=Close
 file_visualizer_file_options=File Options
 file_visualizer_filename=Filename
 file_visualizer_flush=Flush
 file_visualizer_missing_filename=No output filename specified.
 file_visualizer_open=Open
 file_visualizer_output_file=Write results to file / Read from file
 file_visualizer_submit_data=Include Submitted Data
 file_visualizer_title=File Reporter
 file_visualizer_verbose=Verbose Output
 filename=File Name
 follow_redirects=Follow Redirects
 follow_redirects_auto=Redirect Automatically
 foreach_controller_title=ForEach Controller
 foreach_input=Input variable prefix
 foreach_output=Output variable name
 foreach_use_separator=Add "_" before number ?
 format=Number format
 fr=French
 ftp_binary_mode=Use Binary mode ?
 ftp_get=get(RETR)
 ftp_local_file=Local File:
 ftp_remote_file=Remote File:
 ftp_sample_title=FTP Request Defaults
 ftp_save_response_data=Save File in Response ?
 ftp_testing_title=FTP Request
 ftp_put=put(STOR)
 function_dialog_menu_item=Function Helper Dialog
 function_helper_title=Function Helper
 function_name_param=Name of variable in which to store the result (required)
 function_name_paropt=Name of variable in which to store the result (optional)
 function_params=Function Parameters
 functional_mode=Functional Test Mode (i.e. save Response Data and Sampler Data)
 functional_mode_explanation=Selecting Functional Test Mode may adversely affect performance.
 gaussian_timer_delay=Constant Delay Offset (in milliseconds)\:
 gaussian_timer_memo=Adds a random delay with a gaussian distribution
 gaussian_timer_range=Deviation (in milliseconds)\:
 gaussian_timer_title=Gaussian Random Timer
 generate=Generate
 generator=Name of Generator class
 generator_cnf_msg=Could not find the generator class. Please make sure you place your jar file in the /lib directory.
 generator_illegal_msg=Could not access the generator class due to IllegalAcessException.
 generator_instantiate_msg=Could not create an instance of the generator parser. Please make sure the generator implements Generator interface.
 get_xml_from_file=File with SOAP XML Data (overrides above text)
 get_xml_from_random=Message Folder
 get_xml_message=Note\: Parsing XML is CPU intensive. Therefore, do not set the thread count
 get_xml_message2=too high. In general, 10 threads will consume 100% of the CPU on a 900mhz
 get_xml_message3=Pentium 3. On a pentium 4 2.4ghz cpu, 50 threads is the upper limit. Your
 get_xml_message4=options for increasing the number of clients is to increase the number of
 get_xml_message5=machines or use multi-cpu systems.
 graph_choose_graphs=Graphs to Display
 graph_full_results_title=Graph Full Results
 graph_results_average=Average
 graph_results_data=Data
 graph_results_deviation=Deviation
 graph_results_latest_sample=Latest Sample
 graph_results_median=Median
 graph_results_ms=ms
 graph_results_no_samples=No of Samples
 graph_results_throughput=Throughput
 graph_results_title=Graph Results
 grouping_add_separators=Add separators between groups
 grouping_in_controllers=Put each group in a new controller
 grouping_mode=Grouping\:
 grouping_no_groups=Do not group samplers
 grouping_store_first_only=Store 1st sampler of each group only
 header_manager_title=HTTP Header Manager
 headers_stored=Headers Stored in the Header Manager
 help=Help
 help_node=What's this node?
 html_assertion_file=Write JTidy report to file
 html_assertion_label=HTML Assertion
 html_assertion_title=HTML Assertion
 html_parameter_mask=HTML Parameter Mask
 http_implementation=HTTP Implementation:
 http_response_code=HTTP response code
 http_url_rewriting_modifier_title=HTTP URL Re-writing Modifier
 http_user_parameter_modifier=HTTP User Parameter Modifier
 httpmirror_title=HTTP Mirror Server
 id_prefix=ID Prefix
 id_suffix=ID Suffix
 if_controller_evaluate_all=Evaluate for all children?
 if_controller_label=Condition (Javascript)
 if_controller_title=If Controller
 ignore_subcontrollers=Ignore sub-controller blocks
 include_controller=Include Controller
 include_equals=Include Equals?
 include_path=Include Test Plan
 increment=Increment
 infinite=Forever
 initial_context_factory=Initial Context Factory
 insert_after=Insert After
 insert_before=Insert Before
 insert_parent=Insert Parent
 interleave_control_title=Interleave Controller
 intsum_param_1=First int to add.
 intsum_param_2=Second int to add - further ints can be summed by adding further arguments.
 invalid_data=Invalid data
 invalid_mail=Error occurred sending the e-mail
 invalid_mail_address=One or more invalid e-mail addresses detected
 invalid_mail_server=Problem contacting the e-mail server (see JMeter log file)
 invalid_variables=Invalid variables
 iteration_counter_arg_1=TRUE, for each user to have own counter, FALSE for a global counter
 iterator_num=Loop Count\:
 jar_file=Jar Files
 java_request=Java Request
 java_request_defaults=Java Request Defaults
 javascript_expression=JavaScript expression to evaluate
 jexl_expression=JEXL expression to evaluate
 jms_auth_not_required=Not Required
 jms_auth_required=Required
 jms_authentication=Authentication
 jms_client_caption=Receive client uses TopicSubscriber.receive() to listen for message.
 jms_client_caption2=MessageListener uses onMessage(Message) interface to listen for new messages.
 jms_client_type=Client
 jms_communication_style=Communication style
 jms_concrete_connection_factory=Concrete Connection Factory
 jms_config=Configuration
 jms_config_title=JMS Configuration
 jms_connection_factory=Connection Factory
 jms_error_msg=Object message should read from an external file. Text input is currently selected, please remember to change it.
 jms_file=File
 jms_initial_context_factory=Initial Context Factory
 jms_itertions=Number of samples to aggregate
 jms_jndi_defaults_title=JNDI Default Configuration
 jms_jndi_props=JNDI Properties
 jms_message_title=Message properties
 jms_message_type=Message Type
 jms_msg_content=Content
 jms_object_message=Object Message
 jms_point_to_point=JMS Point-to-Point
 jms_props=JMS Properties
 jms_provider_url=Provider URL
 jms_publisher=JMS Publisher
 jms_pwd=Password
 jms_queue=Queue
 jms_queue_connection_factory=QueueConnection Factory
 jms_queueing=JMS Resources
 jms_random_file=Random File
 jms_read_response=Read Response
 jms_receive_queue=JNDI name Receive queue
 jms_request=Request Only
 jms_requestreply=Request Response
 jms_sample_title= JMS Default Request
 jms_send_queue=JNDI name Request queue
 jms_subscriber_on_message=Use MessageListener.onMessage()
 jms_subscriber_receive=Use TopicSubscriber.receive()
 jms_subscriber_title=JMS Subscriber
 jms_testing_title= Messaging Request
 jms_text_message=Text Message
 jms_timeout=Timeout (milliseconds)
 jms_topic=Topic
 jms_use_file=From file
 jms_use_non_persistent_delivery=Use non-persistent delivery mode?
 jms_use_properties_file=Use jndi.properties file
 jms_use_random_file=Random File
 jms_use_text=Textarea
 jms_user=User
 jndi_config_title=JNDI Configuration
 jndi_lookup_name=Remote Interface
 jndi_lookup_title=JNDI Lookup Configuration
 jndi_method_button_invoke=Invoke
 jndi_method_button_reflect=Reflect
 jndi_method_home_name=Home Method Name
 jndi_method_home_parms=Home Method Parameters
 jndi_method_name=Method Configuration
 jndi_method_remote_interface_list=Remote Interfaces
 jndi_method_remote_name=Remote Method Name
 jndi_method_remote_parms=Remote Method Parameters
 jndi_method_title=Remote Method Configuration
 jndi_testing_title=JNDI Request
 jndi_url_jndi_props=JNDI Properties
 jp=Japanese
 junit_append_error=Append assertion errors
 junit_append_exception=Append runtime exceptions
 junit_constructor_error=Unable to create an instance of the class
 junit_constructor_string=Constructor String Label
 junit_do_setup_teardown=Do not call setUp and tearDown
 junit_error_code=Error Code
 junit_error_default_code=9999
 junit_error_default_msg=An unexpected error occured
 junit_error_msg=Error Message
 junit_failure_code=Failure Code
 junit_failure_default_code=0001
 junit_failure_default_msg=Test failed
 junit_failure_msg=Failure Message
 junit_pkg_filter=Package Filter
 junit_request=JUnit Request
 junit_request_defaults=JUnit Request Defaults
 junit_success_code=Success Code
 junit_success_default_code=1000
 junit_success_default_msg=Test successful
 junit_success_msg=Success Message
 junit_test_config=JUnit Test Parameters
 junit_test_method=Test Method
 ldap_argument_list=LDAPArgument List
 ldap_connto=Connection timeout (in milliseconds)
 ldap_parse_results=Parse the search results ?
 ldap_sample_title=LDAP Request Defaults
 ldap_search_baseobject=Perform baseobject search
 ldap_search_onelevel=Perform onelevel search
 ldap_search_subtree=Perform subtree search
 ldap_secure=Use Secure LDAP Protocol ?
 ldap_testing_title=LDAP Request
 ldapext_sample_title=LDAP Extended Request Defaults
 ldapext_testing_title= LDAP Extended Request
 load=Load
 load_wsdl=Load WSDL
 log_errors_only=Errors
 log_file=Location of log File
 log_function_comment=Additional comment (optional)
 log_function_level=Log level (default INFO) or OUT or ERR
 log_function_string=String to be logged
 log_function_string_ret=String to be logged (and returned)
 log_function_throwable=Throwable text (optional)
 log_only=Log/Display Only:
 log_parser=Name of Log Parser class
 log_parser_cnf_msg=Could not find the class. Please make sure you place your jar file in the /lib directory.
 log_parser_illegal_msg=Could not access the class due to IllegalAcessException.
 log_parser_instantiate_msg=Could not create an instance of the log parser. Please make sure the parser implements LogParser interface.
 log_sampler=Tomcat Access Log Sampler
 log_success_only=Successes
 logic_controller_title=Simple Controller
 login_config=Login Configuration
 login_config_element=Login Config Element
 longsum_param_1=First long to add
 longsum_param_2=Second long to add - further longs can be summed by adding further arguments.
 loop_controller_title=Loop Controller
 looping_control=Looping Control
 lower_bound=Lower Bound
 mail_reader_account=Username:
 mail_reader_all_messages=All
 mail_reader_delete=Delete messages from the server
 mail_reader_folder=Folder:
 mail_reader_imap=IMAP
 mail_reader_num_messages=Number of messages to retrieve:
 mail_reader_password=Password:
 mail_reader_pop3=POP3
 mail_reader_server=Server:
 mail_reader_server_type=Server Type:
 mail_reader_title=Mail Reader Sampler
 mail_sent=Mail sent successfully
 mailer_attributes_panel=Mailing attributes
 mailer_error=Couldn't send mail. Please correct any misentries.
 mailer_visualizer_title=Mailer Visualizer
 match_num_field=Match No. (0 for Random)\:
 max=Maximum
 maximum_param=The maximum value allowed for a range of values
 md5hex_assertion_failure=Error asserting MD5 sum : got {0} but should have been {1}
 md5hex_assertion_label=MD5Hex
 md5hex_assertion_md5hex_test=MD5Hex to Assert
 md5hex_assertion_title=MD5Hex Assertion
 memory_cache=Memory Cache
 menu_assertions=Assertions
 menu_close=Close
 menu_collapse_all=Collapse All
 menu_config_element=Config Element
 menu_edit=Edit
 menu_expand_all=Expand All
 menu_generative_controller=Sampler
 menu_listener=Listener
 menu_logic_controller=Logic Controller
 menu_merge=Merge
 menu_modifiers=Modifiers
 menu_non_test_elements=Non-Test Elements
 menu_open=Open
 menu_post_processors=Post Processors
 menu_pre_processors=Pre Processors
 menu_response_based_modifiers=Response Based Modifiers
 menu_timer=Timer
 metadata=MetaData
 method=Method\:
 mimetype=Mimetype
 minimum_param=The minimum value allowed for a range of values
 minute=minute
 modddn=Old entry name
 modification_controller_title=Modification Controller
 modification_manager_title=Modification Manager
 modify_test=Modify Test
 modtest=Modification test
 module_controller_title=Module Controller
 module_controller_warning=Could not find module: 
 module_controller_module_to_run=Module To Run 
 monitor_equation_active=Active:  (busy/max) > 25%
 monitor_equation_dead=Dead:  no response
 monitor_equation_healthy=Healthy:  (busy/max) < 25%
 monitor_equation_load=Load:  ( (busy / max) * 50) + ( (used memory / max memory) * 50)
 monitor_equation_warning=Warning:  (busy/max) > 67%
 monitor_health_tab_title=Health
 monitor_health_title=Monitor Results
 monitor_is_title=Use as Monitor
 monitor_label_left_bottom=0 %
 monitor_label_left_middle=50 %
 monitor_label_left_top=100 %
 monitor_label_right_active=Active
 monitor_label_right_dead=Dead
 monitor_label_right_healthy=Healthy
 monitor_label_right_warning=Warning
 monitor_legend_health=Health
 monitor_legend_load=Load
 monitor_legend_memory_per=Memory % (used/total)
 monitor_legend_thread_per=Thread % (busy/max)
 monitor_load_factor_mem=50
 monitor_load_factor_thread=50
 monitor_performance_servers=Servers
 monitor_performance_tab_title=Performance
 monitor_performance_title=Performance Graph
 name=Name\:
 new=New
 newdn=New distinguished name
 no=Norwegian
 number_of_threads=Number of Threads (users)\:
 obsolete_test_element=This test element is obsolete
 once_only_controller_title=Once Only Controller
 opcode=opCode
 open=Open...
 option=Options
 optional_tasks=Optional Tasks
 paramtable=Send Parameters With the Request\:
 password=Password
 paste=Paste
 paste_insert=Paste As Insert
 path=Path\:
 path_extension_choice=Path Extension (use ";" as separator)
 path_extension_dont_use_equals=Do not use equals in path extension (Intershop Enfinity compatibility)
 path_extension_dont_use_questionmark=Do not use questionmark in path extension (Intershop Enfinity compatibility)
 patterns_to_exclude=URL Patterns to Exclude
 patterns_to_include=URL Patterns to Include
 pkcs12_desc=PKCS 12 Key (*.p12)
 port=Port\:
 property_as_field_label={0}\:
 property_default_param=Default value
 property_edit=Edit
 property_editor.value_is_invalid_message=The text you just entered is not a valid value for this property.\nThe property will be reverted to its previous value.
 property_editor.value_is_invalid_title=Invalid input
 property_name_param=Name of property
 property_returnvalue_param=Return Original Value of property (default false) ?
 property_tool_tip={0}\: {1}
 property_undefined=Undefined
 property_value_param=Value of property
 property_visualiser_title=Property Display
 protocol=Protocol (default http)\:
 protocol_java_border=Java class
 protocol_java_classname=Classname\:
 protocol_java_config_tile=Configure Java Sample
 protocol_java_test_title=Java Testing
 provider_url=Provider URL
 proxy_assertions=Add Assertions
 proxy_cl_error=If specifying a proxy server, host and port must be given
 proxy_content_type_exclude=Exclude\:
 proxy_content_type_filter=Content-type filter
 proxy_content_type_include=Include\:
 proxy_headers=Capture HTTP Headers
 proxy_httpsspoofing=Attempt HTTPS Spoofing
 proxy_httpsspoofing_match=Optional URL match string:
 proxy_regex=Regex matching
 proxy_sampler_settings=HTTP Sampler settings
 proxy_sampler_type=Type\:
 proxy_separators=Add Separators
 proxy_target=Target Controller\:
 proxy_test_plan_content=Test plan content
 proxy_title=HTTP Proxy Server
 ramp_up=Ramp-Up Period (in seconds)\:
 random_control_title=Random Controller
 random_order_control_title=Random Order Controller
 read_response_message=Read response is not checked. To see the response, please check the box in the sampler.
 read_response_note=If read response is unchecked, the sampler will not read the response
 read_response_note2=or set the SampleResult. This improves performance, but it means
 read_response_note3=the response content won't be logged.
 read_soap_response=Read SOAP Response
 realm=Realm
 record_controller_title=Recording Controller
 ref_name_field=Reference Name\:
 regex_extractor_title=Regular Expression Extractor
 regex_field=Regular Expression\:
 regex_source=Response Field to check
 regex_src_body=Body
 regex_src_hdrs=Headers
 regex_src_url=URL
 regexfunc_param_1=Regular expression used to search results from previous request
 regexfunc_param_2=Template for the replacement string, using groups from the regular expression.  Format is $[group]$.  Example $1$.
 regexfunc_param_3=Which match to use.  An integer 1 or greater, RAND to indicate JMeter should randomly choose, A float, or ALL indicating all matches should be used ([1])
 regexfunc_param_4=Between text.  If ALL is selected, the between text will be used to generate the results ([""])
 regexfunc_param_5=Default text.  Used instead of the template if the regular expression finds no matches ([""])
 remote_error_init=Error initialising remote server
 remote_error_starting=Error starting remote server
 remote_exit=Remote Exit
 remote_exit_all=Remote Exit All
 remote_start=Remote Start
 remote_start_all=Remote Start All
 remote_stop=Remote Stop
 remote_stop_all=Remote Stop All
 remove=Remove
 rename=Rename entry
 report=Report
 report_bar_chart=Bar Chart
 report_bar_graph_url=URL
 report_base_directory=Base Directory
 report_chart_caption=Chart Caption
 report_chart_x_axis=X Axis
 report_chart_x_axis_label=Label for X Axis
 report_chart_y_axis=Y Axis
 report_chart_y_axis_label=Label for Y Axis
 report_line_graph=Line Graph
 report_line_graph_urls=Include URLs
 report_output_directory=Output Directory for Report
 report_page=Report Page
 report_page_element=Page Element
 report_page_footer=Page Footer
 report_page_header=Page Header
 report_page_index=Create Page Index
 report_page_intro=Page Introduction
 report_page_style_url=Stylesheet url
 report_page_title=Page Title
 report_pie_chart=Pie Chart
 report_plan=Report Plan
 report_select=Select
 report_summary=Report Summary
 report_table=Report Table
 report_writer=Report Writer
 report_writer_html=HTML Report Writer
 request_data=Request Data
 reset_gui=Reset Gui
 restart=Restart
 resultaction_title=Result Status Action Handler
 resultsaver_errors=Save Failed Responses only
 resultsaver_prefix=Filename prefix\:
 resultsaver_title=Save Responses to a file
 retobj=Return object
 reuseconnection=Re-use connection
 revert_project=Revert
 revert_project?=Revert project?
 root=Root
 root_title=Root
 run=Run
 running_test=Running test
 runtime_controller_title=Runtime Controller
 runtime_seconds=Runtime (seconds)
 sample_result_save_configuration=Sample Result Save Configuration
 sampler_label=Label
 sampler_on_error_action=Action to be taken after a Sampler error
 sampler_on_error_continue=Continue
 sampler_on_error_stop_test=Stop Test
 sampler_on_error_stop_thread=Stop Thread
 save=Save
 save?=Save?
 save_all_as=Save Test Plan as
 save_as=Save Selection As...
 save_as_error=More than one item selected!
 save_as_image=Save Node As Image
 save_as_image_all=Save Screen As Image
 save_assertionresultsfailuremessage=Save Assertion Failure Message
 save_assertions=Save Assertion Results (XML)
 save_asxml=Save As XML
 save_bytes=Save byte count
 save_code=Save Response Code
 save_datatype=Save Data Type
 save_encoding=Save Encoding
 save_fieldnames=Save Field Names (CSV)
 save_filename=Save Response Filename
 save_graphics=Save Graph
 save_hostname=Save Hostname
 save_label=Save Label
 save_latency=Save Latency
 save_message=Save Response Message
 save_overwrite_existing_file=The selected file already exists, do you want to overwrite it?
 save_requestheaders=Save Request Headers (XML)
 save_responsedata=Save Response Data (XML)
 save_responseheaders=Save Response Headers (XML)
 save_samplerdata=Save Sampler Data (XML)
 save_subresults=Save Sub Results (XML)
 save_success=Save Success
 save_samplecount=Save Sample and Error Counts
 save_threadcounts=Save Active Thread Counts
 save_threadname=Save Thread Name
 save_time=Save Elapsed Time
 save_timestamp=Save Time Stamp
 save_url=Save URL
 sbind=Single bind/unbind
 scheduler=Scheduler
 scheduler_configuration=Scheduler Configuration
 scope=Scope
 search_base=Search base
 search_filter=Search Filter
 search_test=Search Test
 searchbase=Search base
 searchfilter=Search Filter
 searchtest=Search test
 second=second
 secure=Secure
 send_file=Send a File With the Request\:
 send_file_browse=Browse...
 send_file_filename_label=Filename\:
 send_file_mime_label=MIME Type\:
 send_file_param_name_label=Value for "name" attribute\:
 server=Server Name or IP\:
 servername=Servername \:
 session_argument_name=Session Argument Name
 should_save=You should save your test plan before running it.  \nIf you are using supporting data files (ie, for CSV Data Set or _StringFromFile), \nthen it is particularly important to first save your test script. \nDo you want to save your test plan first?
 shutdown=Shutdown
 simple_config_element=Simple Config Element
 simple_data_writer_title=Simple Data Writer
 size_assertion_comparator_error_equal=been equal to
 size_assertion_comparator_error_greater=been greater than
 size_assertion_comparator_error_greaterequal=been greater or equal to
 size_assertion_comparator_error_less=been less than
 size_assertion_comparator_error_lessequal=been less than or equal to
 size_assertion_comparator_error_notequal=not been equal to
 size_assertion_comparator_label=Type of Comparison
 size_assertion_failure=The result was the wrong size\: It was {0} bytes, but should have {1} {2} bytes.
 size_assertion_input_error=Please enter a valid positive integer.
 size_assertion_label=Size in bytes\:
 size_assertion_size_test=Size to Assert
 size_assertion_title=Size Assertion
 soap_action=Soap Action
 soap_data_title=Soap/XML-RPC Data
 soap_sampler_title=SOAP/XML-RPC Request
 soap_send_action=Send SOAPAction: 
 spline_visualizer_average=Average
 spline_visualizer_incoming=Incoming
 spline_visualizer_maximum=Maximum
 spline_visualizer_minimum=Minimum
 spline_visualizer_title=Spline Visualizer
 spline_visualizer_waitingmessage=Waiting for samples
 ssl_alias_prompt=Please type your preferred alias
 ssl_alias_select=Select your alias for the test
 ssl_alias_title=Client Alias
 ssl_error_title=Key Store Problem
 ssl_pass_prompt=Please type your password
 ssl_pass_title=KeyStore Password
 ssl_port=SSL Port
 sslmanager=SSL Manager
 start=Start
 starttime=Start Time
 stop=Stop
 stopping_test=Shutting down all test threads.  Please be patient.
 stopping_test_title=Stopping Test
 string_from_file_file_name=Enter full path to file
 string_from_file_seq_final=Final file sequence number (opt)
 string_from_file_seq_start=Start file sequence number (opt)
 summariser_title=Generate Summary Results
 summary_report=Summary Report
 switch_controller_label=Switch Value
 switch_controller_title=Switch Controller
 table_visualizer_bytes=Bytes
 table_visualizer_sample_num=Sample #
 table_visualizer_sample_time=Sample Time(ms)
 table_visualizer_start_time=Start Time
 table_visualizer_status=Status
 table_visualizer_success=Success
 table_visualizer_thread_name=Thread Name
 table_visualizer_warning=Warning
 tcp_config_title=TCP Sampler Config
 tcp_nodelay=Set NoDelay
 tcp_port=Port Number\:
 tcp_request_data=Text to send
 tcp_sample_title=TCP Sampler
 tcp_timeout=Timeout (milliseconds)\:
 template_field=Template\:
 test=Test
 test_action_action=Action
 test_action_duration=Duration (milliseconds)
 test_action_pause=Pause
 test_action_stop=Stop
 test_action_target=Target
 test_action_target_test=All Threads
 test_action_target_thread=Current Thread
 test_action_title=Test Action
 test_configuration=Test Configuration
 test_plan=Test Plan
 test_plan_classpath_browse=Add directory or jar to classpath
 testconfiguration=Test Configuration
 testplan.serialized=Run Thread Groups consecutively (i.e. run groups one at a time)
 testplan_comments=Comments\:
 testt=Test
 thread_delay_properties=Thread Delay Properties
 thread_group_title=Thread Group
 thread_properties=Thread Properties
 threadgroup=Thread Group
 throughput_control_bynumber_label=Total Executions
 throughput_control_bypercent_label=Percent Executions
 throughput_control_perthread_label=Per User
 throughput_control_title=Throughput Controller
 throughput_control_tplabel=Throughput
 time_format=Format string for SimpleDateFormat (optional)
 timelim=Time limit
+tr=Turkish
 transaction_controller_parent=Generate parent sample
 transaction_controller_title=Transaction Controller
 unbind=Thread Unbind
 uniform_timer_delay=Constant Delay Offset (in milliseconds)\:
 uniform_timer_memo=Adds a random delay with a uniform distribution
 uniform_timer_range=Random Delay Maximum (in milliseconds)\:
 uniform_timer_title=Uniform Random Timer
 update_per_iter=Update Once Per Iteration
 upload=File Upload
 upper_bound=Upper Bound
 url=URL
 url_config_get=GET
 url_config_http=HTTP
 url_config_https=HTTPS
 url_config_post=POST
 url_config_protocol=Protocol\:
 url_config_title=HTTP Request Defaults
 url_full_config_title=UrlFull Sample
 url_multipart_config_title=HTTP Multipart Request Defaults
 use_keepalive=Use KeepAlive
 use_multipart_for_http_post=Use multipart/form-data for HTTP POST
 use_recording_controller=Use Recording Controller
 user=User
 user_defined_test=User Defined Test
 user_defined_variables=User Defined Variables
 user_param_mod_help_note=(Do not change this.  Instead, modify the file of that name in JMeter's /bin directory)
 user_parameters_table=Parameters
 user_parameters_title=User Parameters
 userdn=Username
 username=Username
 userpw=Password
 value=Value
 var_name=Reference Name
 variable_name_param=Name of variable (may include variable and function references)
 view_graph_tree_title=View Graph Tree
 view_results_in_table=View Results in Table
 view_results_render_embedded=Download embedded resources
 view_results_render_html=Render HTML
 view_results_render_json=Render JSON
 view_results_render_text=Show Text
 view_results_render_xml=Render XML
 view_results_tab_request=Request
 view_results_tab_response=Response data
 view_results_tab_sampler=Sampler result
 view_results_tab_assertion=Assertion result
 view_results_title=View Results
 view_results_tree_title=View Results Tree
 warning=Warning!
 web_request=HTTP Request
 web_server=Web Server
 web_server_client=Client implementation:
 web_server_domain=Server Name or IP\:
 web_server_port=Port Number\:
 web_testing_embedded_url_pattern=Embedded URLs must match\:
 web_testing_retrieve_images=Retrieve All Embedded Resources from HTML Files
 web_testing_title=HTTP Request
 web_testing2_title=HTTP Request HTTPClient
 webservice_proxy_host=Proxy Host
 webservice_proxy_note=If Use HTTP Proxy is checked, but no host or port are provided, the sampler
 webservice_proxy_note2=will look at command line options. If no proxy host or port are provided by
 webservice_proxy_note3=either, it will fail silently.
 webservice_proxy_port=Proxy Port
 webservice_sampler_title=WebService(SOAP) Request
 webservice_soap_action=SOAPAction
 webservice_timeout=Timeout:
 webservice_use_proxy=Use HTTP Proxy
 while_controller_label=Condition (function or variable)
 while_controller_title=While Controller
 workbench_title=WorkBench
 wsdl_helper_error=The WSDL was not valid, please double check the url.
 wsdl_url=WSDL URL
 wsdl_url_error=The WSDL was emtpy.
 xml_assertion_title=XML Assertion
 xml_namespace_button=Use Namespaces
 xml_tolerant_button=Tolerant XML/HTML Parser
 xml_validate_button=Validate XML
 xml_whitespace_button=Ignore Whitespace
 xmlschema_assertion_label=File Name:
 xmlschema_assertion_title=XML Schema Assertion
 xpath_assertion_button=Validate
 xpath_assertion_check=Check XPath Expression
 xpath_assertion_error=Error with XPath
 xpath_assertion_failed=Invalid XPath Expression
 xpath_assertion_label=XPath
 xpath_assertion_negate=True if nothing matches
 xpath_assertion_option=XML Parsing Options
 xpath_assertion_test=XPath Assertion 
 xpath_assertion_tidy=Try and tidy up the input
 xpath_assertion_title=XPath Assertion
 xpath_assertion_valid=Valid XPath Expression
 xpath_assertion_validation=Validate the XML against the DTD
 xpath_assertion_whitespace=Ignore whitespace
 xpath_expression=XPath expression to match against
 xpath_extractor_namespace=Use Namespaces?
 xpath_extractor_query=XPath query:
 xpath_extractor_title=XPath Extractor
 xpath_extractor_tolerant=Use Tidy ?
 xpath_file_file_name=XML file to get values from 
 xpath_tidy_quiet=Quiet
 xpath_tidy_report_errors=Report errors
 xpath_tidy_show_warnings=Show warnings
 you_must_enter_a_valid_number=You must enter a valid number
 zh_cn=Chinese (Simplified)
 zh_tw=Chinese (Traditional)
 # Please add new entries in alphabetical order
diff --git a/src/core/org/apache/jmeter/resources/messages_tr.properties b/src/core/org/apache/jmeter/resources/messages_tr.properties
new file mode 100644
index 000000000..2d98ecf5f
--- /dev/null
+++ b/src/core/org/apache/jmeter/resources/messages_tr.properties
@@ -0,0 +1,875 @@
+#Stored by I18NEdit, may be edited!
+about=Apache JMeter Hakk\u0131nda
+add=Ekle
+add_as_child=\u00C7ocuk Olarak Ekle
+add_parameter=De\u011Fi\u015Fken Olarak Ekle
+add_pattern=Desen Ekle\:
+add_test=Test Ekle
+add_user=Kullan\u0131c\u0131 Ekle
+add_value=De\u011Fer Ekle
+addtest=Test ekle
+aggregate_graph=\u0130statiksel Grafikler
+aggregate_graph_column=Kolon
+aggregate_graph_display=Grafik G\u00F6ster
+aggregate_graph_height=Y\u00FCkseklik
+aggregate_graph_max_length_xaxis_label=X-ekseni etiketinin maximum uzunlu\u011Fu
+aggregate_graph_ms=Milisaniyeler
+aggregate_graph_response_time=Cevap Zaman\u0131
+aggregate_graph_save=Grafi\u011Fi kaydet
+aggregate_graph_save_table=Tablo Verisini Kaydet
+aggregate_graph_save_table_header=Tablo Ba\u015Fl\u0131\u011F\u0131n\u0131 Kaydet
+aggregate_graph_title=Toplu Grafik
+aggregate_graph_user_title=Grafik Ba\u015Fl\u0131\u011F\u0131
+aggregate_graph_width=Geni\u015Flik
+aggregate_report=Toplu Rapor
+aggregate_report_90=90%
+aggregate_report_90%_line=90% Sat\u0131r
+aggregate_report_bandwidth=KB/sn
+aggregate_report_count=\# \u00D6rnek
+aggregate_report_error=Hata
+aggregate_report_error%=Hata %
+aggregate_report_max=En \u00C7ok
+aggregate_report_median=Ortalama
+aggregate_report_min=En Az
+aggregate_report_rate=Transfer Oran\u0131
+aggregate_report_stddev=Std. Dev.
+aggregate_report_total_label=TOPLAM
+als_message=Not\: Eri\u015Fim Logu Ayr\u0131\u015Ft\u0131r\u0131c\u0131s\u0131 eklentiye izin veren genel-ge\u00E7er bir tasar\u0131ma sahiptir
+als_message2=\u00F6zg\u00FCn ayr\u0131\u015Ft\u0131r\u0131c\u0131. Bu \u015Fekilde yapmak i\u00E7in, LogParser'i ger\u00E7ekle, jar'\u0131 \u015Furaya ekle\: 
+als_message3=/lib dizini ve \u00F6rnekleyicideki s\u0131n\u0131f\u0131 gir.
+analyze=Veri Dosyas\u0131n\u0131 Analiz Et...
+anchor_modifier_title=HTML Ba\u011Flant\u0131s\u0131 Ayr\u0131\u015Ft\u0131r\u0131c\u0131s\u0131
+appearance=Temalar
+argument_must_not_be_negative=Ba\u011F\u0131ms\u0131z de\u011Fi\u015Fken negatif olmamal\u0131\!
+assertion_assume_success=Durumu Yoksay
+assertion_code_resp=Cevap Kodu
+assertion_contains=\u0130\u00E7erir
+assertion_equals=E\u015Fittir
+assertion_headers=Cevap Ba\u015Fl\u0131klar\u0131
+assertion_matches=\u00D6rt\u00FC\u015F\u00FCr
+assertion_message_resp=Cevap Mesaj\u0131
+assertion_not=Not
+assertion_pattern_match_rules=Desen \u00D6rt\u00FC\u015Fme Kurallar\u0131
+assertion_patterns_to_test=Test Edilecek Desenler
+assertion_resp_field=Test Edilecek Cevap Alan\u0131
+assertion_text_resp=Metin Cevap
+assertion_textarea_label=Do\u011Frulamalar\:
+assertion_title=Cevap Do\u011Frulamas\u0131
+assertion_url_samp=URL \u00D6rneklendi
+assertion_visualizer_title=Do\u011Frulama Sonu\u00E7lar\u0131
+attribute=\u00D6znitelik
+attrs=\u00D6znitelikler
+auth_base_url=Temel URL
+auth_manager_title=HTTP Yetkilendirme Y\u00F6neticisi
+auths_stored=Yetkilendirme Y\u00F6neticisinde Tutulan Yetkilendirmeler
+average=Ortalama
+average_bytes=Ort. Byte
+bind=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Ba\u011Flamas\u0131
+browse=G\u00F6zat...
+bsf_sampler_title=BSF \u00D6rnekleyicisi
+bsf_script=\u00C7al\u0131\u015Ft\u0131r\u0131lacak betik (de\u011Fi\u015Fkenler\: log, Label, FileName, Parameters, args[], SampleResult, ctx, vars)
+bsf_script_file=\u00C7al\u0131\u015Ft\u0131r\u0131lacak betik dosyas\u0131
+bsf_script_language=Betik dili\:
+bsf_script_parameters=Beti\u011Fe veya betik dosyas\u0131na ge\u00E7ilecek parametreler
+bsh_assertion_script=Betik (tan\u0131ml\u0131 de\u011Fi\u015Fkenler i\u00E7in a\u015Fa\u011F\u0131ya bak\u0131n)
+bsh_assertion_script_variables=Betik i\u00E7in \u015Fu de\u011Fi\u015Fkenler tan\u0131mlanm\u0131\u015Ft\u0131r\:\nOkuma/Yazma\: Failure, FailureMessage, SampleResult, vars, log.\nSalt Okunur\: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
+bsh_assertion_title=BeanShell Do\u011Frulamas\u0131
+bsh_function_expression=De\u011Ferlendirilecek ifade
+bsh_sampler_title=BeanShell \u00D6rnekleyici
+bsh_script=Betik (tan\u0131ml\u0131 de\u011Fi\u015Fkenler i\u00E7in a\u015Fa\u011F\u0131ya bak\u0131n)
+bsh_script_file=Betik Dosyas\u0131
+bsh_script_parameters=Parametreler (-> Dizgi (String) Parametreler ve String []bsh.args)
+bsh_script_variables=Betik i\u00E7in \u015Fu de\u011Fi\u015Fkenler tan\u0131ml\u0131d\u0131r\:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, log
+busy_testing=Testle me\u015Fgul\u00FCm, l\u00FCtfen ayarlar\u0131 de\u011Fi\u015Ftirmeden \u00F6nce testi durdurun
+cache_session_id=\u00D6nbellek oturum Id'si?
+cancel=\u0130ptal
+cancel_exit_to_save=Kaydedilmemi\u015F test maddeleri var. \u00C7\u0131kmadan \u00F6nce kaydetmek ister misiniz?
+cancel_new_to_save=Kaydedilmemi\u015F test maddeleri var. Testi temizlemeden \u00F6nce kaydetmek ister misin?
+cancel_revert_project=Kaydedilmemi\u015F test maddeleri var. Daha \u00F6nce kaydedilmi\u015F olan test plan\u0131na d\u00F6nmek ister misiniz?
+choose_function=Fonksiyon se\u00E7in
+choose_language=Dil se\u00E7in
+clear=Temizle
+clear_all=Hepsini Temizle
+clear_cookies_per_iter=Her tekrar i\u00E7in \u00E7erezleri temizle?
+column_delete_disallowed=Bu kolonu silmek i\u00E7in izin yok
+column_number=CSV dosyas\u0131 i\u00E7in kolon numaras\u0131 | ileri | *takma ad
+compare=Kar\u015F\u0131la\u015Ft\u0131r
+comparefilt=Filtreyi kar\u015F\u0131la\u015Ft\u0131r
+config_element=Ayar Eleman\u0131
+config_save_settings=Ayarla
+configure_wsdl=Ayarla
+constant_throughput_timer_memo=Sabit bir transfer oran\u0131 elde etmek i\u00E7in \u00F6rneklemeler aras\u0131na gecikme ekle
+constant_timer_delay=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Gecikmesi (milisaniyeler)
+constant_timer_memo=\u00D6rneklemeler aras\u0131na sabit bir gecikme ekle
+constant_timer_title=Sabit Zamanlay\u0131c\u0131
+content_encoding=\u0130\u00E7erik kodlamas\u0131\:
+controller=Denet\u00E7i
+cookie_manager_policy=\u00C7erez Politikas\u0131
+cookie_manager_title=HTTP \u00C7erez Y\u00F6neticisi
+cookies_stored=\u00C7erez Y\u00F6neticisinde Tutulan \u00C7erezler
+copy=Kopyala
+corba_config_title=CORBA \u00D6rnekleme Ayar\u0131
+corba_input_data_file=Giri\u015F Verisi Dosyas\u0131\:\n
+corba_methods=\u00C7a\u011Fr\u0131lacak metodu se\u00E7in\:
+corba_name_server=\u0130sim Sunucusu\:
+corba_port=Port Numaras\u0131\:
+corba_request_data=Giri\u015F Verisi
+corba_sample_title=CORBA \u00D6rnekleyici
+counter_config_title=Saya\u00E7
+counter_per_user=Sayac\u0131 her kullan\u0131c\u0131 i\u00E7in ba\u011F\u0131ms\u0131z \u00E7al\u0131\u015Ft\u0131r
+countlim=Boyut s\u0131n\u0131r\u0131
+csvread_file_file_name=De\u011Ferlerin okunaca\u011F\u0131 CSV dosyas\u0131 | *k\u0131saltma
+cut=Kes
+cut_paste_function=Fonksiyon metnini kopyala ve yap\u0131\u015Ft\u0131r
+database_conn_pool_max_usage=Her Ba\u011Flant\u0131 i\u00E7in En Fazla Kullan\u0131m\:
+database_conn_pool_props=Veritaban\u0131 Ba\u011Flant\u0131s\u0131 Havuzu
+database_conn_pool_size=Havuzdaki Ba\u011Flant\u0131 Say\u0131s\u0131
+database_conn_pool_title=JDBC Veritaban Ba\u011Flant\u0131s\u0131 Havuzu \u00D6ntan\u0131ml\u0131 De\u011Ferleri
+database_driver_class=S\u00FCr\u00FCc\u00FC S\u0131n\u0131f\u0131\:
+database_login_title=JDBC Veritaban\u0131 Giri\u015Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
+database_sql_query_string=SQL Sorgusu Metni\:
+database_sql_query_title=JDBC SQL Sorgusu \u00D6ntan\u0131ml\u0131 De\u011Ferleri
+database_testing_title=JDBC \u0130ste\u011Fi
+database_url=JDBC Adresi\:
+database_url_jdbc_props=Veritaban\u0131 Adresi ve JDBC S\u00FCr\u00FCc\u00FCs\u00FC
+ddn=DN
+de=Alman
+debug_off=Hata ay\u0131klamas\u0131n\u0131 saf d\u0131\u015F\u0131 b\u0131rak
+debug_on=Hata ay\u0131klamas\u0131n\u0131 etkinle\u015Ftir
+default_parameters=\u00D6ntan\u0131ml\u0131 Parametreler
+default_value_field=\u00D6ntan\u0131ml\u0131 De\u011Fer\:
+delay=Ba\u015Flang\u0131\u00E7 gecikmesi (saniye)
+delete=Sil
+delete_parameter=De\u011Fi\u015Fkeni Sil
+delete_test=Testi Sil
+delete_user=Kullan\u0131c\u0131y\u0131 Sil
+deltest=Testi sil
+deref=K\u0131saltmalar\u0131 g\u00F6ster
+disable=Safd\u0131\u015F\u0131 b\u0131rak
+distribution_graph_title=Da\u011F\u0131t\u0131m Grafi\u011Fi (alfa)
+distribution_note1=Grafik 10 \u00F6rnekte bir g\u00FCncellenecek
+dn=DN
+domain=Etki Alan\u0131
+done=Bitti
+duration=S\u00FCre (saniye)
+duration_assertion_duration_test=Do\u011Frulama S\u00FCresi
+duration_assertion_failure=\u0130\u015Flem \u00E7ok uzun s\u00FCrd\u00FC\: {0} milisaniye s\u00FCrmesi gerekirken, {1}  milisaniyeden fazla s\u00FCrd\u00FC.
+duration_assertion_input_error=Pozitif bir tamsay\u0131 giriniz.
+duration_assertion_label=Milisaniye olarak s\u00FCre\:
+duration_assertion_title=S\u00FCre Do\u011Frulamas\u0131
+edit=D\u00FCzenle
+email_results_title=E-posta Sonu\u00E7lar\u0131
+en=\u0130ngilizce
+enable=Etkinle\u015Ftir
+encode?=Kodlama?
+encoded_value=URL Kodlanm\u0131\u015F De\u011Fer
+endtime=Biti\u015F Zaman\u0131
+entry_dn=Giri\u015F DN'i
+entrydn=Giri\u015F DN'i
+error_loading_help=Yard\u0131m sayfas\u0131n\u0131 y\u00FCklerken hata
+error_occurred=Hata Olu\u015Ftu
+error_title=Hata
+es=\u0130spanyolca
+eval_name_param=De\u011Fi\u015Fken ve fonksiyon referanslar\u0131 i\u00E7eren metin
+evalvar_name_param=De\u011Fi\u015Fken ismi
+example_data=\u00D6rnek Veri
+example_title=\u00D6rnekleyici \u00F6rne\u011Fi
+exit=\u00C7\u0131k\u0131\u015F
+expiration=S\u00FCre dolumu
+field_name=Alan ismi
+file=Dosya
+file_already_in_use=Bu dosya zaten kullan\u0131l\u0131yor
+file_visualizer_append=Varolan Veri Dosyas\u0131na Ekle
+file_visualizer_auto_flush=Her \u00D6rnekten Sonra Otomatik Olarak Temizle
+file_visualizer_browse=G\u00F6zat...
+file_visualizer_close=Kapat
+file_visualizer_file_options=Dosya Se\u00E7enekleri
+file_visualizer_filename=Dosya ismi
+file_visualizer_flush=Temizle
+file_visualizer_missing_filename=\u00C7\u0131kt\u0131 dosyas\u0131 belirtilmedi.
+file_visualizer_open=A\u00E7
+file_visualizer_output_file=Sonu\u00E7lar\u0131 dosyaya yaz / Dosyadan oku
+file_visualizer_submit_data=Girilen Veriyi Ekle
+file_visualizer_title=Dosya Raprolay\u0131c\u0131
+file_visualizer_verbose=\u00C7\u0131kt\u0131y\u0131 Detayland\u0131r
+filename=Dosya \u0130smi
+follow_redirects=Y\u00F6nlendirmeleri \u0130zle
+follow_redirects_auto=Otomatik Olarak Y\u00F6nlendir
+foreach_controller_title=ForEach Denet\u00E7isi
+foreach_input=Giri\u015F de\u011Fi\u015Fkeni \u00F6neki
+foreach_output=\u00C7\u0131k\u0131\u015F de\u011Fi\u015Fkeni ismi
+foreach_use_separator=Numara \u00F6n\u00FCne "_" ekle ?
+format=Numara bi\u00E7imi
+fr=Frans\u0131zca
+ftp_binary_mode=\u0130kili kipi kullan ?
+ftp_get=get(RETR)
+ftp_local_file=Yerel Dosya\:
+ftp_put=put(STOR)
+ftp_remote_file=Uzak Dosya\:
+ftp_sample_title=FTP \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
+ftp_save_response_data=Cevab\u0131 Dosyaya Kaydet ?
+ftp_testing_title=FTP \u0130ste\u011Fi
+function_dialog_menu_item=Fonksiyon Yard\u0131m\u0131 Diyalo\u011Fu
+function_helper_title=Fonksiyon Yard\u0131m\u0131
+function_name_param=Sonucu tutacak de\u011F\u015Fkenin ismi (gerekli)
+function_name_paropt=Sonucu tutacak de\u011Fi\u015Fkenin ismi (iste\u011Fe ba\u011Fl\u0131)
+function_params=Fonksiyon Parametresi
+functional_mode=Fonksiyonel Test Kipi (\u00F6r\: Cevap Verisini ve \u00D6rnekleyici Verisini kaydet)
+functional_mode_explanation=Ba\u015Far\u0131m\u0131 olumsuz etkileyecek olmas\u0131na ra\u011Fmen Fonksiyonel Test Kipi se\u00E7iliyor.
+gaussian_timer_delay=Sabit Gecikme S\u0131n\u0131r\u0131 (milisaniye)
+gaussian_timer_memo=Gauss da\u011F\u0131l\u0131m\u0131na g\u00F6re rastgele bir gecikme ekler
+gaussian_timer_range=Sapma (milisaniye)
+gaussian_timer_title=Gauss Rastgele Zamanlay\u0131c\u0131
+generate=\u00DCret
+generator=\u00DCretici S\u0131n\u0131f\u0131n \u0130smi
+generator_cnf_msg=\u00DCretici s\u0131n\u0131f\u0131 bulamad\u0131. L\u00FCtfen jar dosyas\u0131n\u0131 /lib dizini alt\u0131na yerle\u015Ftirdi\u011Finizden emin olun.
+generator_illegal_msg=IllegalAccessException nedeniyle \u00FCretici s\u0131n\u0131fa eri\u015Femedi.
+generator_instantiate_msg=\u00DCretici ayr\u0131\u015Ft\u0131r\u0131c\u0131 i\u00E7in \u00F6rnek yaratamad\u0131. L\u00FCtfen \u00FCreticinin "Generator" arabirimini ger\u00E7ekledi\u011Finden emin olun.
+get_xml_from_file=SOAP XML Verisini i\u00E7eren Dosya (A\u015Fa\u011F\u0131daki metnin \u00FCzerine yazar)
+get_xml_from_random=Mesaj Klas\u00F6r\u00FC
+get_xml_message=Not\: XML ayr\u0131\u015Ft\u0131rmak i\u015Flemci t\u00FCketir. Bu y\u00FCzden i\u015F par\u00E7ac\u0131\u011F\u0131 say\u0131s\u0131n\u0131 de\u011Fi\u015Ftirmeyin
+get_xml_message2=\u00E7ok fazla. Normalde, 10 i\u015F par\u00E7ac\u0131\u011F\u0131 900mhz bir i\u015Flemciyi 100% me\u015Fgul eder
+get_xml_message3=Pentium 3. Pentium 4 2.4ghz bit i\u015Flemci i\u00E7in 50 i\u015F par\u00E7ac\u0131\u011F\u0131 \u00FCst s\u0131n\u0131rd\u0131r. Sizin
+get_xml_message4=istemci say\u0131s\u0131n\u0131 artt\u0131racak se\u00E7eneklerin artt\u0131raca\u011F\u0131 say\u0131lar
+get_xml_message5=makineler ya da \u00E7oklu i\u015Flemcili sistemler.
+graph_choose_graphs=G\u00F6sterilecek Grafikler
+graph_full_results_title=Grafik Tam Sonu\u00E7lar\u0131
+graph_results_average=Ortalama
+graph_results_data=Veri
+graph_results_deviation=Sapma
+graph_results_latest_sample=Son \u00D6rnek
+graph_results_median=Orta
+graph_results_ms=ms
+graph_results_no_samples=\u00D6rnek Say\u0131s\u0131
+graph_results_throughput=Transfer Oran\u0131
+graph_results_title=Grafik Sonu\u00E7lar\u0131
+grouping_add_separators=Gruplar aras\u0131na ayra\u00E7 ekle
+grouping_in_controllers=Her grubu yeni bir denet\u00E7iye koy
+grouping_mode=Gruplama\:
+grouping_no_groups=\u00D6rnekleyicileri gruplama
+grouping_store_first_only=Her grubun sadece 1. \u00F6rnekleyicilerini tut
+header_manager_title=HTTP Ba\u015Fl\u0131k Y\u00F6neticisi
+headers_stored=Ba\u015Fl\u0131k Y\u00F6neticisinde Tutulan Ba\u015Fl\u0131klar
+help=Yard\u0131m
+help_node=Bu d\u00FC\u011F\u00FCm nedir?
+html_assertion_file=JTidy raporunu dosyaya yaz
+html_assertion_label=HTML Do\u011Frulama
+html_assertion_title=HTML Do\u011Frulama
+html_parameter_mask=HTML Parametre Maskesi
+http_implementation=HTTP Uygulamas\u0131\:
+http_response_code=HTTP cevap kodu
+http_url_rewriting_modifier_title=HTTP URL Yeniden Yazma Niteleyicisi
+http_user_parameter_modifier=HTTP Kullan\u0131c\u0131 Parametresi Niteleyicisi
+httpmirror_title=HTTP Ayna Sunucusu
+id_prefix=ID \u00D6neki
+id_suffix=ID Soneki
+if_controller_evaluate_all=T\u00FCm \u00E7ocuklar i\u00E7in hesapla?
+if_controller_label=Durum (Javascript)
+if_controller_title=If Denet\u00E7isi
+ignore_subcontrollers=Alt denet\u00E7i bloklar\u0131n\u0131 yoksay
+include_controller=\u0130\u00E7erme Denet\u00E7isi
+include_equals=E\u015Fleniyorsa i\u00E7er?
+include_path=Test Plan\u0131n\u0131 \u0130\u00E7er
+increment=Artt\u0131r
+infinite=Her zaman
+initial_context_factory=\u0130lk Ba\u011Flam Fabrikas\u0131
+insert_after=Arkas\u0131na Ekle
+insert_before=\u00D6n\u00FCne Ekle
+insert_parent=Ebeveynine Ekle
+interleave_control_title=Aral\u0131k Denet\u00E7isi
+intsum_param_1=Eklenecek ilk tamsay\u0131.
+intsum_param_2=Eklenecek ikinci tamsay\u0131 - sonraki tamsay\u0131lar di\u011Fer argumanlar\u0131 ekleyerek toplanabilir.
+invalid_data=Ge\u00E7ersiz veri
+invalid_mail=E-posta g\u00F6nderirken hata olu\u015Ftu
+invalid_mail_address=Bir veya daha fazla ge\u00E7ersiz e-posta adresi tespit edildi
+invalid_mail_server=E-posta sunucusuna ba\u011Flan\u0131rken problem (JMeter log dosyas\u0131na bak\u0131n\u0131z)
+invalid_variables=Ge\u00E7ersiz de\u011Fi\u015Fkenler
+iteration_counter_arg_1=TRUE, her kullan\u0131c\u0131n\u0131n kendi sayac\u0131na sahip olmas\u0131 i\u00E7in, FALSE genel-ge\u00E7er saya\u00E7 i\u00E7in
+iterator_num=D\u00F6ng\u00FC Say\u0131s\u0131\:
+jar_file=Jar Dosyalar\u0131
+java_request=Java \u0130ste\u011Fi
+java_request_defaults=Java \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
+javascript_expression=De\u011Ferlendirilecek javascript ifadesi
+jexl_expression=De\u011Ferlendirilecek JEXL ifadesi
+jms_auth_not_required=Gerekli De\u011Fil
+jms_auth_required=Gerekli
+jms_authentication=Kimlik Do\u011Frulamas\u0131
+jms_client_caption=Mesaj dinlemek i\u00E7in TopicSubscriber.receive() kullanan istemciyi al.
+jms_client_caption2=MessageListener yeni mesajlar\u0131 dinlemek i\u00E7in onMessage(Message)'\u0131 kullan\u0131r.
+jms_client_type=\u0130stemci
+jms_communication_style=\u0130leti\u015Fim \u015Eekli
+jms_concrete_connection_factory=Somut Ba\u011Flant\u0131 Fabrikas\u0131
+jms_config=Ayarlar
+jms_config_title=JMS Ayar\u0131
+jms_connection_factory=Ba\u011Flant\u0131 Fabrikas\u0131
+jms_error_msg=Nesne mesaj\u0131 harici bir dosyadan okunmal\u0131. Metin girdisi se\u00E7ili durumda, l\u00FCtfen de\u011Fi\u015Ftirmeyi unutmay\u0131n.
+jms_file=Dosya
+jms_initial_context_factory=Ba\u015Flang\u0131\u00E7 Ba\u011Flam Fabrikas\u0131
+jms_itertions=Toplanacak istek say\u0131s\u0131
+jms_jndi_defaults_title=JNDI \u00D6ntan\u0131ml\u0131 Ayarlar\u0131
+jms_jndi_props=JDNI \u00D6zellikleri
+jms_message_title=Mesaj \u00F6zellikleri
+jms_message_type=Mesaj Tipi
+jms_msg_content=\u0130\u00E7erik
+jms_object_message=Nesne Mesaj\u0131
+jms_point_to_point=JMS U\u00E7tan Uca
+jms_props=JMS \u00D6zellikleri
+jms_provider_url=Sa\u011Flay\u0131c\u0131 Adresi (URL)
+jms_publisher=JMS Yay\u0131nc\u0131s\u0131
+jms_pwd=\u015Eifre
+jms_queue=S\u0131ra
+jms_queue_connection_factory=QueueConnection Fabrikas\u0131
+jms_queueing=JMS Kaynaklar\u0131
+jms_random_file=Rastgele Dosyas\u0131
+jms_read_response=Cevab\u0131 Oku
+jms_receive_queue=S\u0131ray\u0131 alan JNDI ismi 
+jms_request=Sadece \u0130stek
+jms_requestreply=\u0130stek Cevap
+jms_sample_title=JMS \u00D6ntan\u0131ml\u0131 \u0130ste\u011Fi 
+jms_send_queue=S\u0131ray\u0131 alan JNDI ismi
+jms_subscriber_on_message=MessageListener.onMessage()'\u0131 kullan
+jms_subscriber_receive=TopicSubscriber.receive()'\u0131 kullan
+jms_subscriber_title=JMS Abonesi
+jms_testing_title=Mesajla\u015Fma \u0130ste\u011Fi
+jms_text_message=Metin Mesaj\u0131
+jms_timeout=Zaman A\u015F\u0131m\u0131 (milisaniye)
+jms_topic=Konu
+jms_use_file=Dosyadan
+jms_use_non_persistent_delivery=S\u00FCrekli olmayan da\u011F\u0131t\u0131m kipini kullan?
+jms_use_properties_file=jndi.properties dosyas\u0131n\u0131 kullan
+jms_use_random_file=Rastgele Dosyas\u0131
+jms_use_text=Metin alan\u0131
+jms_user=Kullan\u0131c\u0131
+jndi_config_title=JNDI Ayar\u0131
+jndi_lookup_name=Uzak Arabirim
+jndi_lookup_title=JNDI Arama Ayar\u0131
+jndi_method_button_invoke=\u00C7a\u011F\u0131r
+jndi_method_button_reflect=Yans\u0131t
+jndi_method_home_name=Yerel Metod \u0130smi
+jndi_method_home_parms=Yerel Metod Parametreleri
+jndi_method_name=Metod Ayar\u0131
+jndi_method_remote_interface_list=Uzak Arabirimler
+jndi_method_remote_name=Uzak Metod \u0130smi
+jndi_method_remote_parms=Uzak Metod Parametreleri
+jndi_method_title=Uzak Metod Ayar\u0131
+jndi_testing_title=JNDI \u0130ste\u011Fi
+jndi_url_jndi_props=JNDI \u00D6zellikleri
+jp=Japonca
+junit_append_error=Do\u011Frulama hatalar\u0131n\u0131 ekle
+junit_append_exception=\u00C7al\u0131\u015Fma zaman\u0131 istisnalar\u0131n\u0131 ekle
+junit_constructor_error=S\u0131n\u0131f \u00F6rne\u011Fi yarat\u0131lamad\u0131
+junit_constructor_string=Yap\u0131c\u0131 Metin Etiketi
+junit_do_setup_teardown=setUp ve tearDown'u \u00E7a\u011F\u0131rma
+junit_error_code=Hata Kodu
+junit_error_default_code=9999
+junit_error_default_msg=Beklenmedik hata olu\u015Ftu
+junit_error_msg=Hata Mesaj\u0131
+junit_failure_code=Ba\u015Far\u0131s\u0131zl\u0131k Kodu 
+junit_failure_default_code=0001
+junit_failure_default_msg=Test ba\u015Far\u0131s\u0131z oldu.
+junit_failure_msg=Ba\u015Far\u0131s\u0131zl\u0131k Mesaj\u0131
+junit_pkg_filter=Paket Filtresi
+junit_request=JUnit \u0130ste\u011Fi
+junit_request_defaults=JUnit \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
+junit_success_code=Ba\u015Far\u0131 Kodu
+junit_success_default_code=1000
+junit_success_default_msg=Test ba\u015Far\u0131s\u0131z
+junit_success_msg=Ba\u015Far\u0131 Mesaj\u0131
+junit_test_config=JUnit Test Parametreleri
+junit_test_method=Test Metodu
+ldap_argument_list=LDAP Arg\u00FCman Listesi
+ldap_connto=Ba\u011Flant\u0131 zaman a\u015F\u0131m\u0131 (milisaniye)
+ldap_parse_results=Arama sonu\u00E7lar\u0131n\u0131 ayr\u0131\u015Ft\u0131r ?
+ldap_sample_title=LDAP \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 Ayarlar\u0131
+ldap_search_baseobject=Temel-nesne aramas\u0131 ger\u00E7ekle\u015Ftir
+ldap_search_onelevel=Tek-seviye aramas\u0131 ger\u00E7ekle\u015Ftir
+ldap_search_subtree=Alt-a\u011Fa\u00E7 aramas\u0131 ger\u00E7ekle\u015Ftir
+ldap_secure=G\u00FCvenli LDAP Protokulu kullan ?
+ldap_testing_title=LDAP \u0130ste\u011Fi
+ldapext_sample_title=LDAP Geli\u015Fmi\u015F \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
+ldapext_testing_title=LDAP Geli\u015Fmi\u015F \u0130ste\u011Fi
+load=Y\u00FCkle
+load_wsdl=WSDL Y\u00FCkle
+log_errors_only=Hatalar
+log_file=Log Dosyas\u0131 Yolu
+log_function_comment=Ek yorum (iste\u011Fe ba\u011Fl\u0131)
+log_function_level=Log seviyesi (\u00F6ntan\u0131ml\u0131 INFO) ya OUT ya da ERR
+log_function_string=Loglanacak metin
+log_function_string_ret=Loglanacak (ve d\u00F6n\u00FClecek) metin
+log_function_throwable=At\u0131lacak metin (iste\u011Fe ba\u011Fl\u0131)
+log_only=Sadece Log/G\u00F6r\u00FCnt\u00FCleme\:
+log_parser=Log Ayr\u0131\u015Ft\u0131r\u0131c\u0131s\u0131 S\u0131n\u0131f\u0131n\u0131n \u0130smi
+log_parser_cnf_msg=S\u0131n\u0131f\u0131 bulamad\u0131. L\u00FCtfen jar dosyas\u0131n\u0131 /lib dizini alt\u0131na yerle\u015Ftirdi\u011Finizden emin olun.
+log_parser_illegal_msg=IllegalAcessException nedeniyle s\u0131n\u0131fa eri\u015Femedi.
+log_parser_instantiate_msg=Log ayr\u0131\u015Ft\u0131r\u0131c\u0131 \u00F6rne\u011Fi yaratamad\u0131. L\u00FCtfen ayr\u0131\u015Ft\u0131r\u0131c\u0131n\u0131n LogParser arabirimini ger\u00E7ekledi\u011Finden emin olun.
+log_sampler=Tomcat Eri\u015Fimi Log \u00D6rnekleyicisi
+log_success_only=Ba\u015Far\u0131lar
+logic_controller_title=Basit Denet\u00E7i
+login_config=Kullan\u0131c\u0131 Giri\u015Fi Ayar\u0131
+login_config_element=Kullan\u0131c\u0131 Giri\u015Fi Eleman\u0131
+longsum_param_1=Eklenek ilk b\u00FCy\u00FCk say\u0131 (long)
+longsum_param_2=Eklenecek ikinci b\u00FCy\u00FCk say\u0131 (long) - di\u011Fer b\u00FCy\u00FCk say\u0131lar di\u011Fer arg\u00FCmanlar\u0131n toplanmas\u0131yla elde edilebilir.
+loop_controller_title=D\u00F6ng\u00FC Denet\u00E7isi
+looping_control=D\u00F6ng\u00FC Kontrol\u00FC
+lower_bound=A\u015Fa\u011F\u0131 S\u0131n\u0131r
+mail_reader_account=Kullan\u0131c\u0131 ismi\:
+mail_reader_all_messages=Hepsi
+mail_reader_delete=Sunucudan mesajlar\u0131 sil
+mail_reader_folder=Klas\u00F6r\:
+mail_reader_imap=IMAP
+mail_reader_num_messages=\u00C7ekilecek mesajlar\u0131n say\u0131s\u0131\:
+mail_reader_password=\u015Eifre\:
+mail_reader_pop3=POP3
+mail_reader_server=Sunucu\:
+mail_reader_server_type=Sunucu Tipi\:
+mail_reader_title=Eposta Okuyucu \u00D6rnekleyicisi
+mail_sent=Eposta ba\u015Far\u0131yla g\u00F6nderildi
+mailer_attributes_panel=Eposta \u00F6znitelikleri
+mailer_error=Eposta g\u00F6nderilemedi. L\u00FCtfen sorunlu girdileri d\u00FCzeltin.
+mailer_visualizer_title=Eposta G\u00F6r\u00FCnt\u00FCleyicisi
+match_num_field=E\u015Fle\u015Fme Numaras\u0131. (Rastgele i\u00E7in 0)
+max=En Fazla
+maximum_param=\u0130zin verilen de\u011Fer aral\u0131\u011F\u0131 i\u00E7in en b\u00FCy\u00FCk de\u011Fer
+md5hex_assertion_failure=MD5 toplam\u0131 do\u011Frulamas\u0131 hatas\u0131\: {1} beklenirken {0} al\u0131nd\u0131
+md5hex_assertion_label=MDBHex
+md5hex_assertion_md5hex_test=Do\u011Frulanacak MD5Hex
+md5hex_assertion_title=MD5Hex Do\u011Frulamas\u0131
+memory_cache=\u00D6nbellek
+menu_assertions=Do\u011Frulamalar
+menu_close=Kapat
+menu_collapse_all=Hepsini Kapat
+menu_config_element=Ayar Eleman\u0131
+menu_edit=D\u00FCzenle
+menu_expand_all=Hepsini A\u00E7
+menu_generative_controller=\u00D6rnekleyici
+menu_listener=Dinleyici
+menu_logic_controller=Mant\u0131k Denet\u00E7isi
+menu_merge=Birle\u015Ftir
+menu_modifiers=Niteleyiciler
+menu_non_test_elements=Test-d\u0131\u015F\u0131 Elemanlar
+menu_open=A\u00E7
+menu_post_processors=Test Sonras\u0131 \u0130\u015Flemciler
+menu_pre_processors=Test \u00D6ncesi \u0130\u015Flemciler
+menu_response_based_modifiers=Cevap Temelli Niteleyiciler
+menu_timer=Zamanlay\u0131c\u0131
+metadata=Veri hakk\u0131nda veri (metadata)
+method=Metod\:
+mimetype=Mime tipi
+minimum_param=\u0130zin verilen de\u011Fer aral\u0131\u011F\u0131 i\u00E7in en k\u00FC\u00E7\u00FCk de\u011Fer
+minute=dakika
+modddn=Eski girdi ismi
+modification_controller_title=De\u011Fi\u015Fiklik Denet\u00E7isi
+modification_manager_title=De\u011Fi\u015Fiklik Y\u00F6neticisi
+modify_test=Testi De\u011Fi\u015Ftir
+modtest=De\u011Fi\u015Fiklik testi
+module_controller_module_to_run=\u00C7al\u0131\u015Ft\u0131r\u0131lacak Birim
+module_controller_title=Birim Denet\u00E7isi
+module_controller_warning=Birim bulunamad\u0131\:
+monitor_equation_active=Aktif\: (me\u015Fgul/maksimum) > 25%
+monitor_equation_dead=\u00D6l\u00FC\: cevap yok
+monitor_equation_healthy=Sa\u011Fl\u0131kl\u0131\: (me\u015Fgul/maksimum) < 25%
+monitor_equation_load=Y\u00FCk\: ((me\u015Fgul / maksimum * 50) + ((kullan\u0131lan bellek / maksimum bellek)) > 25%
+monitor_equation_warning=Uyar\u0131\: (me\u015Fgul/maksimum) > 67%
+monitor_health_tab_title=Sa\u011Fl\u0131k
+monitor_health_title=\u0130zleme Sonu\u00E7lar\u0131
+monitor_is_title=\u0130zleyici olarak kullan
+monitor_label_left_bottom=0 %
+monitor_label_left_middle=50 %
+monitor_label_left_top=100 %
+monitor_label_right_active=Aktif
+monitor_label_right_dead=\u00D6l\u00FC
+monitor_label_right_healthy=Sa\u011Fl\u0131kl\u0131
+monitor_label_right_warning=Uyar\u0131
+monitor_legend_health=Sa\u011Fl\u0131k
+monitor_legend_load=Y\u00FCk
+monitor_legend_memory_per=Bellek % (kullan\u0131lan/toplam)
+monitor_legend_thread_per=\u0130\u015F par\u00E7ac\u0131\u011F\u0131 % (me\u015Fgul/maksimum)
+monitor_load_factor_mem=50
+monitor_load_factor_thread=50
+monitor_performance_servers=Sunucular
+monitor_performance_tab_title=Ba\u015Far\u0131m
+monitor_performance_title=Ba\u015Far\u0131m Grafi\u011Fi
+name=\u0130sim\:
+new=Yeni
+newdn=Yeni ay\u0131rt edici isim
+no=Norve\u00E7ce
+number_of_threads=\u0130\u015F par\u00E7ac\u0131\u011F\u0131 say\u0131s\u0131
+obsolete_test_element=Test eleman\u0131 belirsiz
+once_only_controller_title=Bir Kerelik Denet\u00E7i
+opcode=opCode
+open=A\u00E7...
+option=Se\u00E7enekler
+optional_tasks=\u0130ste\u011Fe Ba\u011Fl\u0131 G\u00F6revler
+paramtable=\u0130stekle parametreleri g\u00F6nder\:
+password=\u015Eifre
+paste=Yap\u0131\u015Ft\u0131r
+paste_insert=Ekleme Olarak Yap\u0131\u015Ft\u0131r
+path=Yol\:
+path_extension_choice=Yol Uzatmas\u0131 (ayra\u00E7 olarak ";" kullan)
+path_extension_dont_use_equals=Yol uzatmas\u0131nda e\u015Fitlik kullanmay\u0131n (Intershop Enfinity uyumlulu\u011Fu)
+path_extension_dont_use_questionmark=Yol uzatmas\u0131nda soru i\u015Fareti kullanmay\u0131n (Intershop Enfinity uyumlulu\u011Fu)
+patterns_to_exclude=Hari\u00E7 Tutulacak URL Desenleri
+patterns_to_include=Dahil Edilecek URL Desenleri
+pkcs12_desc=PKCS 12 Anahtar (*.p12)
+port=Port\:
+property_as_field_label={0}\:
+property_default_param=\u00D6ntan\u0131ml\u0131 de\u011Fer
+property_edit=D\u00FCzenle
+property_editor.value_is_invalid_message=Girdi\u011Finiz metin bu \u00F6zellik i\u00E7in ge\u00E7erli de\u011Fil.\n\u00D6zellik \u00F6nceki de\u011Ferine geri d\u00F6nd\u00FCr\u00FClecek.
+property_editor.value_is_invalid_title=Ge\u00E7ersiz girdi
+property_name_param=\u00D6zellik ismi
+property_returnvalue_param=\u00D6zelli\u011Fin orjinal de\u011Ferini d\u00F6n (\u00F6ntan\u0131ml\u0131 false)?
+property_tool_tip={0}\: {1}
+property_undefined=Tan\u0131ms\u0131z
+property_value_param=\u00D6zelli\u011Fin de\u011Feri
+property_visualiser_title=\u00D6zellik G\u00F6r\u00FCnt\u00FCleme
+protocol=Protokol (\u00F6ntan\u0131ml\u0131 http)\:
+protocol_java_border=Java s\u0131n\u0131f\u0131
+protocol_java_classname=S\u0131n\u0131f ismi\:
+protocol_java_config_tile=Java \u00D6rne\u011Fi Ayarla
+protocol_java_test_title=Java Testi
+provider_url=Sa\u011Flay\u0131c\u0131 Adresi (URL)
+proxy_assertions=Do\u011Frulamalar\u0131 Ekle
+proxy_cl_error=Vekil sunucu belirtiliyorsa, sunucu ve port verilmeli
+proxy_content_type_exclude=Hari\u00E7 tut\:
+proxy_content_type_filter=\u0130\u00E7erik-tipi filtresi
+proxy_content_type_include=\u0130\u00E7eren\:
+proxy_headers=HTTP Ba\u015Fl\u0131klar\u0131n\u0131 Yakala
+proxy_httpsspoofing=HTTPS Taklidi Dene
+proxy_httpsspoofing_match=\u0130ste\u011Fe ba\u011Fl\u0131 URL e\u015Fle\u015Fme metni\:
+proxy_regex=D\u00FCzenli ifade e\u015Fle\u015Fmesi
+proxy_sampler_settings=HTTP \u00D6rnekleyici Ayarlar\u0131
+proxy_sampler_type=Tip\:
+proxy_separators=Ayra\u00E7lar\u0131 Ekle
+proxy_target=Hedef Denet\u00E7isi\:
+proxy_test_plan_content=Test plan\u0131 i\u00E7eri\u011Fi
+proxy_title=HTTP Vekil Sunucusu
+ramp_up=Rampa S\u00FCresi (saniyeler)\:
+random_control_title=Rastgele Denet\u00E7isi
+random_order_control_title=Rastgele S\u0131ra Denet\u00E7isi
+read_response_message="Cevap Oku" se\u00E7ene\u011Fi i\u015Faretli de\u011Fil. Cevab\u0131 g\u00F6rmek i\u00E7in, l\u00FCtfen \u00F6rnekleyicideki ilgili kutuyu i\u015Faretleyin.
+read_response_note=E\u011Fer "cevap oku" se\u00E7ene\u011Fi se\u00E7ili de\u011Filse, \u00F6rnekleyici cevab\u0131 okumaz
+read_response_note2=ya da  SampleResult'\u0131 kur. Bu ba\u015Far\u0131m\u0131 artt\u0131r\u0131r, ama \u015Fu anlama gelir
+read_response_note3=cevap i\u00E7eri\u011Fi loglanmayacak.
+read_soap_response=SOAP Cevab\u0131n\u0131 Oku
+realm=Alan (Realm)
+record_controller_title=Kaydetme Denet\u00E7isi
+ref_name_field=Referans \u0130smi\:
+regex_extractor_title=D\u00FCzenli \u0130fade \u00C7\u0131kar\u0131c\u0131
+regex_field=D\u00FCzenli \u0130fade\:
+regex_source=Se\u00E7ilecek Cevap Alanlar\u0131
+regex_src_body=G\u00F6vde
+regex_src_hdrs=Ba\u015Fl\u0131klar
+regex_src_url=Adres (URL)
+regexfunc_param_1=\u00D6nceki istekte sonu\u00E7lar\u0131 aramak i\u00E7in kullan\u0131lan d\u00FCzenli ifade
+regexfunc_param_2=De\u011Fi\u015Ftirme metni i\u00E7in, d\u00FCzenli ifadeden gruplar\u0131 kullanan \u015Fablon.\nBi\u00E7im $[group]$.  \u00D6rnek $1$.
+regexfunc_param_3=Hangi e\u015Fle\u015Fme kullan\u0131lacak. 1 ya da 1'den b\u00FCy\u00FCk bir tamsay\u0131, rastgele se\u00E7im i\u00E7in RAND, b\u00FCy\u00FCk say\u0131 (float) i\u00E7in A, t\u00FCm e\u015Fle\u015Fmelerin kullan\u0131lmas\u0131 i\u00E7in  ALL ([1])
+regexfunc_param_4=Metinler aras\u0131nda. E\u011Fer ALL se\u00E7iliyse, aradaki metin sonu\u00E7lar\u0131 yaratmak i\u00E7in kullan\u0131lacak ([""])
+regexfunc_param_5=\u00D6ntan\u0131ml\u0131 metin. E\u011Fer d\u00FCzenli ifade bir e\u015Fle\u015Fme yakalayamazsa, yerine kullan\u0131lacak \u015Fablon ([""])
+remote_error_init=Uzak sunucuyu s\u0131f\u0131rlarken hata
+remote_error_starting=Uzak sunucuyu ba\u015Flat\u0131rken hata
+remote_exit=Uzakta \u00C7\u0131k
+remote_exit_all=Uzakta Hepsinden \u00C7\u0131k 
+remote_start=Uzakta Ba\u015Flat
+remote_start_all=Uzakta Hepsini Ba\u015Flat
+remote_stop=Uzakta Durdur
+remote_stop_all=Uzakta Hepsini Durdur
+remove=Kald\u0131r
+rename=Girdiyi yeniden adland\u0131r
+report=Rapor
+report_bar_chart=Bar Grafi\u011Fi
+report_bar_graph_url=Adres (URL)
+report_base_directory=Temel Dizin
+report_chart_caption=Grafik Ba\u015Fl\u0131\u011F\u0131
+report_chart_x_axis=X Ekseni
+report_chart_x_axis_label=X Ekseni Etiketi
+report_chart_y_axis=Y Ekseni
+report_chart_y_axis_label=Y Ekseni Etiketi
+report_line_graph=\u00C7izgi Grafik
+report_line_graph_urls=\u0130\u00E7erilen Adresler (URLler)
+report_output_directory=Rapor i\u00E7in \u00C7\u0131kt\u0131 Dizini
+report_page=Rapor Sayfas\u0131
+report_page_element=Sayfa Eleman\u0131
+report_page_footer=Sayfa Altl\u0131\u011F\u0131
+report_page_header=Sayfa Ba\u015Fl\u0131\u011F\u0131
+report_page_index=Sayfa \u0130ndeksi Yarat
+report_page_intro=Sayfa Giri\u015Fi
+report_page_style_url=CSS Adresi (URL)
+report_page_title=Sayfa Ba\u015Fl\u0131\u011F\u0131
+report_pie_chart=Elma Grafi\u011Fi
+report_plan=Rapor Plan\u0131
+report_select=Se\u00E7
+report_summary=Rapor \u00D6zeti
+report_table=Rapor Tablosu
+report_writer=Rapor Yaz\u0131c\u0131
+report_writer_html=HTML Raporu Yaz\u0131c\u0131
+request_data=\u0130stek Verisi
+reset_gui=Aray\u00FCz\u00FC S\u0131f\u0131rla
+restart=Ba\u015Ftan ba\u015Flat
+resultaction_title=Sonu\u00E7 Durumu Eylem \u0130\u015Fleyici
+resultsaver_errors=Sadece Ba\u015Far\u0131s\u0131z Cevaplar\u0131 Kaydet
+resultsaver_prefix=Dosya ismi \u00F6neki\:
+resultsaver_title=Cevaplar\u0131 dosyaya kaydet
+retobj=Nesne d\u00F6n
+reuseconnection=Ba\u011Flant\u0131y\u0131 tekrar kullan
+revert_project=Geri d\u00F6nd\u00FCr
+revert_project?=Projeyi geri d\u00F6nd\u00FCr?
+root=K\u00F6k
+root_title=K\u00F6k
+run=\u00C7al\u0131\u015Ft\u0131r
+running_test=Testi \u00E7al\u0131\u015Ft\u0131r
+runtime_controller_title=\u00C7al\u0131\u015Fma Zaman\u0131 Denet\u00E7isi
+runtime_seconds=\u00C7al\u0131\u015Fma Zaman\u0131 (saniyeler)
+sample_result_save_configuration=\u00D6rnek Sonu\u00E7 Kaydetme Ayar\u0131
+sampler_label=Etiket
+sampler_on_error_action=\u00D6rnekleyici hatas\u0131ndan sonra yap\u0131lacak hareket
+sampler_on_error_continue=Devam et
+sampler_on_error_stop_test=Testi Durdur
+sampler_on_error_stop_thread=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Durdur
+save=Kaydet
+save?=Kaydet?
+save_all_as=Test Plan\u0131 olarak Kaydet
+save_as=Se\u00E7imi Farkl\u0131 Kaydet...
+save_as_error=Birden fazla madde se\u00E7ili\!
+save_as_image=D\u00FC\u011F\u00FCm\u00FC Resim olarak Kaydet
+save_as_image_all=Ekran\u0131 Resim olarak Kaydet
+save_assertionresultsfailuremessage=Do\u011Frulama Ba\u015Far\u0131s\u0131zl\u0131\u011F\u0131 Mesaj\u0131n\u0131 Kaydet
+save_assertions=Do\u011Frulama Sonu\u00E7lar\u0131n\u0131 Kaydet (XML)
+save_asxml=XML olarak Kaydet
+save_bytes=Bayt say\u0131s\u0131n\u0131 kaydet
+save_code=Cevap Kodunu Kaydet
+save_datatype=Data Tipini Kaydet
+save_encoding=Kodlamay\u0131 Kaydet
+save_fieldnames=Alan \u0130simlerini Kaydet (CSV)
+save_filename=Cevap Dosya \u0130smini Kaydet
+save_graphics=Grafi\u011Fi Kaydet
+save_hostname=Sunucu \u0130smini Kaydet
+save_label=Etiketi Kaydet
+save_latency=Gecikme S\u00FCresi Kaydet
+save_message=Cevap Mesaj\u0131n\u0131 Kaydet
+save_overwrite_existing_file=Se\u00E7ili dosya zaten mevcut, \u00FCst\u00FCne yazmak ister misiniz?
+save_requestheaders=\u0130stek Ba\u015Fl\u0131klar\u0131n\u0131 Kaydet (XML)
+save_responsedata=Cevap Verisini Kaydet (XML)
+save_responseheaders=Cevap Ba\u015Fl\u0131klar\u0131n\u0131 Kaydet (XML)
+save_samplecount=\u00D6rnek ve Hata Say\u0131s\u0131n\u0131 Kaydet
+save_samplerdata=\u00D6rnekleyici Verisini Kaydet (XML)
+save_subresults=Alt Sonu\u00E7lar\u0131 Kaydet (XML)\n
+save_success=Ba\u015Far\u0131y\u0131 Kaydet
+save_threadcounts=Aktif \u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Say\u0131s\u0131n\u0131 Kaydet
+save_threadname=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 \u0130smini Kaydet
+save_time=Ge\u00E7en Zaman\u0131 Kaydet
+save_timestamp=Tarih Bilgisini Kaydet
+save_url=URL Adresini Kaydet
+sbind=Yaln\u0131z ba\u011Fla/ba\u011Flama
+scheduler=Planla
+scheduler_configuration=Planlama Ayar\u0131
+scope=Kapsam
+search_base=Ara\u015Ft\u0131rma temeli
+search_filter=Ara\u015Ft\u0131rma Filtresi
+search_test=Ara\u015Ft\u0131rma Testi
+searchbase=Ara\u015Ft\u0131rma temeli
+searchfilter=Ara\u015Ft\u0131rma Filtresi
+searchtest=Ara\u015Ft\u0131rma testi
+second=saniye
+secure=G\u00FCvenli
+send_file=\u0130stekle Beraber Dosya G\u00F6nder\:
+send_file_browse=G\u00F6zat...
+send_file_filename_label=Dosya ismi\:
+send_file_mime_label=MIME Tipi
+send_file_param_name_label="isim" \u00F6zniteli\u011Fi de\u011Feri\:
+server=Sunucu \u0130smi veya IP\:
+servername=Sunucu \u0130smi \:
+session_argument_name=Oturum Arg\u00FCman\u0131 \u0130smi
+should_save=Testi \u00E7al\u0131\u015Ft\u0131rmadan \u00F6nce test plan\u0131n\u0131 kaydetmeniz tavsiye edilir.\nE\u011Fer destek veri dosyalar\u0131 kullan\u0131yorsan\u0131z (\u00F6r\: CSV Veri K\u00FCmesi ya da _StringFromFile), \u00F6ncelikle test beti\u011Fini kaydetmeniz \u00F6nemlidir.\n\u00D6ncelikle test plan\u0131n\u0131 kaydetmek istiyor musunuz?
+shutdown=Kapat
+simple_config_element=Basit Ayar Eleman\u0131
+simple_data_writer_title=Basit Veri Yaz\u0131c\u0131
+size_assertion_comparator_error_equal=e\u015Fittir
+size_assertion_comparator_error_greater=b\u00FCy\u00FCkt\u00FCr
+size_assertion_comparator_error_greaterequal=b\u00FCy\u00FCkt\u00FCr ya da e\u015Fittir
+size_assertion_comparator_error_less=k\u00FC\u00E7\u00FCkt\u00FCr
+size_assertion_comparator_error_lessequal=k\u00FC\u00E7\u00FCkt\u00FCr ya da e\u015Fittir
+size_assertion_comparator_error_notequal=e\u015Fit de\u011Fildir
+size_assertion_comparator_label=Kar\u015F\u0131la\u015Ft\u0131rma Tipi
+size_assertion_failure=Sonu\u00E7 boyutunda yanl\u0131\u015Fl\u0131k\: {0} bayt, halbuki {1} {2} bayt olmas\u0131 bekleniyordu.
+size_assertion_input_error=L\u00FCtfen ge\u00E7erli pozitif bir tamsay\u0131 girin.
+size_assertion_label=Boyut (bayt)\:
+size_assertion_size_test=Do\u011Frulanacak Boyut
+size_assertion_title=Boyut Do\u011Frulamas\u0131
+soap_action=Soap Action
+soap_data_title=Soap/XML-RPC Verisi
+soap_sampler_title=SOAP/XML-RPC \u0130ste\u011Fi
+soap_send_action=SOAPAction g\u00F6nder\: 
+spline_visualizer_average=Ortalama
+spline_visualizer_incoming=Gelen
+spline_visualizer_maximum=Maksimum
+spline_visualizer_minimum=Minimum
+spline_visualizer_title=Cetvel G\u00F6r\u00FCnt\u00FCleyici
+spline_visualizer_waitingmessage=\u00D6rnekler i\u00E7in bekliyor
+ssl_alias_prompt=L\u00FCtfen tercih etti\u011Finiz k\u0131saltmay\u0131 girin
+ssl_alias_select=Test k\u0131saltman\u0131z\u0131 se\u00E7iniz
+ssl_alias_title=\u0130stemci K\u0131saltmas\u0131
+ssl_error_title=Anahtar Kayd\u0131 Problemi
+ssl_pass_prompt=L\u00FCtfen \u015Fifrenizi girin
+ssl_pass_title=Anahtar Kayd\u0131 Problemi
+ssl_port=SSL Portu
+sslmanager=SSL Y\u00F6neticisi
+start=Ba\u015Flat
+starttime=Ba\u015Flama Zaman\u0131
+stop=Durdur
+stopping_test=T\u00FCm i\u015F par\u00E7ac\u0131klar\u0131n\u0131 kapat\u0131yor. L\u00FCtfen sab\u0131rl\u0131 olun.
+stopping_test_title=Testleri Durduruyor
+string_from_file_file_name=Dosya tam yolunu girin
+string_from_file_seq_final=Dosya s\u0131ra numaras\u0131n\u0131 sonland\u0131r (iste\u011Fe ba\u011Fl\u0131)
+string_from_file_seq_start=Dosya s\u0131ra numaras\u0131n\u0131 ba\u015Flat (iste\u011Fe ba\u011Fl\u0131)
+summariser_title=\u00D6zet Sonu\u00E7lar Olu\u015Ftur
+summary_report=\u00D6zet Rapor
+switch_controller_label=Dallanma (Switch) De\u011Feri
+switch_controller_title=Dallanma (Switch) Denet\u00E7isi
+table_visualizer_bytes=Bayt
+table_visualizer_sample_num=\u00D6rnek \#
+table_visualizer_sample_time=\u00D6rnek Zaman\u0131(ms)
+table_visualizer_start_time=Ba\u015Flama Zaman\u0131
+table_visualizer_status=Durum
+table_visualizer_success=Ba\u015Far\u0131
+table_visualizer_thread_name=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 \u0130smi
+table_visualizer_warning=Uyar\u0131
+tcp_config_title=TCP \u00D6rnekleyici Ayar\u0131
+tcp_nodelay=Hi\u00E7 Gecikmeye Ayarla
+tcp_port=Port Numaras\u0131\:
+tcp_request_data=G\u00F6nderilecek metin
+tcp_sample_title=TCP \u00D6rnekleyici
+tcp_timeout=Zaman A\u015F\u0131m\u0131 (milisaniye)
+template_field=\u015Eablon\:
+test=Test
+test_action_action=Hareket
+test_action_duration=S\u00FCre (milisaniye)
+test_action_pause=Duraklat
+test_action_stop=Durdur
+test_action_target=Hedef
+test_action_target_test=T\u00FCm \u0130\u015F Par\u00E7ac\u0131klar\u0131
+test_action_target_thread=\u015Eu Anki \u0130\u015F Par\u00E7ac\u0131\u011F\u0131
+test_action_title=Test Hareketi
+test_configuration=Test Ayar\u0131
+test_plan=Test Plan\u0131
+test_plan_classpath_browse=S\u0131n\u0131f yoluna (classpath) dizin veya jar ekle
+testconfiguration=Test Ayar\u0131
+testplan.serialized=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Gruplar\u0131n\u0131 Ard\u0131\u015F\u0131k \u00C7al\u0131\u015Ft\u0131r (\u00F6r\: her defas\u0131nda bir grup \u00E7al\u0131\u015Ft\u0131r)
+testplan_comments=Yorumlar\:
+testt=Test
+thread_delay_properties=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Gecikme \u00D6zellikleri
+thread_group_title=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Grubu
+thread_properties=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 \u00D6zellikleri
+threadgroup=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Grubu
+throughput_control_bynumber_label=Toplam Y\u00FCr\u00FCtme
+throughput_control_bypercent_label=Y\u00FCr\u00FCtme Y\u00FCzdesi
+throughput_control_perthread_label=Kullan\u0131c\u0131 Ba\u015F\u0131na
+throughput_control_title=Transfer Oran\u0131 Denet\u00E7isi
+throughput_control_tplabel=Transfer Oran\u0131
+time_format=Metni SimpleDateFormat i\u00E7in bi\u00E7imlendir (iste\u011Fe ba\u011Fl\u0131)
+timelim=Zaman s\u0131n\u0131r\u0131
+tr=T\u00FCrk\u00E7e
+transaction_controller_parent=Ebeveyn \u00F6rnek olu\u015Ftur
+transaction_controller_title=\u0130\u015Flem (transaction) Denet\u00E7isi
+unbind=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131n\u0131 B\u0131rak
+uniform_timer_delay=Sabit Gecikme S\u0131n\u0131r\u0131 (milisaniye)\:
+uniform_timer_memo=Tek bi\u00E7imli da\u011F\u0131l\u0131mla rastgele gecikme ekler
+uniform_timer_range=Maksimum Rastgele Gecikme (milisaniye)\:
+uniform_timer_title=Tek Bi\u00E7imli Rastgele Zamanlay\u0131c\u0131
+update_per_iter=Her Tekrar i\u00E7in Bir Defa Yenile
+upload=Dosya Y\u00FCkleme
+upper_bound=\u00DCst S\u0131n\u0131r
+url=Adres (URL)
+url_config_get=GET
+url_config_http=HTTP
+url_config_https=HTTPS
+url_config_post=POST
+url_config_protocol=Protokol\:
+url_config_title=HTTP \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
+url_full_config_title=UrlFull \u00D6rne\u011Fi
+url_multipart_config_title=HTTP Multipart \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
+use_keepalive=Canl\u0131Tut (KeepAlive) kullan
+use_multipart_for_http_post=HTTP POST i\u00E7in multipart/form-data kullan
+use_recording_controller=Kaydetme Denet\u00E7isi Kullan
+user=Kullan\u0131c\u0131
+user_defined_test=Kullan\u0131c\u0131 Tan\u0131ml\u0131 Test
+user_defined_variables=Kullan\u0131c\u0131 Tan\u0131ml\u0131 De\u011Fi\u015Fkenler
+user_param_mod_help_note=(Buray\u0131 de\u011Fi\u015Ftirme. Onun yerine, JMeter'in /bin dizinindeki dosya ismini d\u00FCzenle)
+user_parameters_table=Parametreler
+user_parameters_title=Kullan\u0131c\u0131 Parametreleri
+userdn=Kullan\u0131c\u0131 ismi
+username=Kullan\u0131c\u0131 ismi
+userpw=\u015Eifre
+value=De\u011Fer
+var_name=Referans \u0130smi
+variable_name_param=De\u011Fi\u015Fken ismi (de\u011Fi\u015Fken ve fonksiyon referanslar\u0131 i\u00E7erebilir)
+view_graph_tree_title=Grafik A\u011Fac\u0131n\u0131 G\u00F6ster
+view_results_in_table=Sonu\u00E7 Tablosunu G\u00F6ster
+view_results_render_embedded=G\u00F6m\u00FCl\u00FC kaynaklar\u0131 indir
+view_results_render_html=HTML i\u015Fle
+view_results_render_json=JSON i\u015Fle
+view_results_render_text=Metin G\u00F6ster
+view_results_render_xml=XML \u0130\u015Fle
+view_results_tab_assertion=Do\u011Frulama sonucu
+view_results_tab_request=\u0130stek
+view_results_tab_response=Cevap verisi
+view_results_tab_sampler=\u00D6rnekleyici sonucu
+view_results_title=Sonu\u00E7lar\u0131 G\u00F6ster
+view_results_tree_title=Sonu\u00E7lar\u0131 G\u00F6sterme A\u011Fac\u0131
+warning=Uyar\u0131\!
+web_request=HTTP \u0130ste\u011Fi
+web_server=A\u011F Sunucusu
+web_server_client=\u0130stemci uygulamas\u0131\:
+web_server_domain=Sunucu \u0130smi veya IP\:
+web_server_port=Port Numaras\u0131\:
+web_testing2_title=HTTP \u0130ste\u011Fi HTTPClient
+web_testing_embedded_url_pattern=G\u00F6m\u00FCl\u00FC Adresler (URL) \u00F6rt\u00FC\u015Fmeli\:
+web_testing_retrieve_images=HTML Dosyalardan T\u00FCm G\u00F6m\u00FCl\u00FC Kaynaklar\u0131 Al
+web_testing_title=HTTP \u0130ste\u011Fi
+webservice_proxy_host=Vekil Makine
+webservice_proxy_note=E\u011Fer "HTTP Vekil Sunucu kullan" se\u00E7iliyse, ama sunucu veya port belirtilmemi\u015Fse, \u00F6rnekleyici
+webservice_proxy_note2=komut sat\u0131r\u0131 se\u00E7eneklerine bakacakt\u0131r. E\u011Fer vekil sunucu veya port belirtilmediyse
+webservice_proxy_note3=ya da, sessizce ba\u015Far\u0131s\u0131z olacakt\u0131r.
+webservice_proxy_port=Vekil Port
+webservice_sampler_title=WebService(SOAP) \u0130ste\u011Fi
+webservice_soap_action=SOAPAction
+webservice_timeout=Zaman A\u015F\u0131m\u0131\:
+webservice_use_proxy=HTTP Vekil Sunucusunu kullan
+while_controller_label=Ko\u015Ful (fonksiyon veya de\u011Fi\u015Fken)
+while_controller_title=While Denet\u00E7isi
+workbench_title=Tezgah
+wsdl_helper_error=WSDL ge\u00E7erli de\u011Fil, l\u00FCtfen url adresini iki defa kontrol edin.
+wsdl_url=WSDL Adresi
+wsdl_url_error=WSDL bo\u015F.
+xml_assertion_title=XML Do\u011Frulamas\u0131
+xml_namespace_button=Namespace kullan
+xml_tolerant_button=Ho\u015Fg\u00F6r\u00FClen XML/HTML Ayr\u0131\u015Ft\u0131r\u0131c\u0131
+xml_validate_button=XML'i do\u011Frula
+xml_whitespace_button=G\u00F6r\u00FCnmeyen Karakterleri Yoksay
+xmlschema_assertion_label=Dosya \u0130smi\:
+xmlschema_assertion_title=XML \u015Eemas\u0131 Do\u011Frulamas\u0131
+xpath_assertion_button=Do\u011Frula
+xpath_assertion_check=XPath \u0130fadesini Kontrol Et
+xpath_assertion_error=XPath'te Hata
+xpath_assertion_failed=Ge\u00E7ersiz XPath \u0130fadesi
+xpath_assertion_label=XPath
+xpath_assertion_negate=E\u011Fer e\u015Fle\u015Fen yoksa True
+xpath_assertion_option=XML Ayr\u0131\u015Ft\u0131r\u0131c\u0131 Se\u00E7enekleri
+xpath_assertion_test=XPath Do\u011Frulamas\u0131
+xpath_assertion_tidy=Dene ve girdiyi d\u00FCzenle
+xpath_assertion_title=XPath Do\u011Frulamas\u0131
+xpath_assertion_valid=Ge\u00E7erli XPath \u0130fadesi
+xpath_assertion_validation=XML'i DTD'ye g\u00F6re kontrol et
+xpath_assertion_whitespace=G\u00F6r\u00FCnmeyen Karakterleri Yoksay
+xpath_expression=Kar\u015F\u0131la\u015Ft\u0131r\u0131lacak XPath ifadesi
+xpath_extractor_namespace=Namespace kullan?
+xpath_extractor_query=XPath sorgusu\:
+xpath_extractor_title=XPath \u00C7\u0131kar\u0131c\u0131
+xpath_extractor_tolerant=D\u00FCzenli kullan ?
+xpath_file_file_name=De\u011Ferlerin okunaca\u011F\u0131 XML dosyas\u0131
+xpath_tidy_quiet=Sessiz
+xpath_tidy_report_errors=Hatalar\u0131 raporla
+xpath_tidy_show_warnings=Uyar\u0131lar\u0131 g\u00F6ster
+you_must_enter_a_valid_number=Ge\u00E7erli bir rakam girmelisiniz
+zh_cn=\u00C7ince (Basitle\u015Ftirilmi\u015F)
+zh_tw=\u00C7ince (Geleneksel)
diff --git a/src/examples/org/apache/jmeter/examples/testbeans/example2/Example2Resources_tr.properties b/src/examples/org/apache/jmeter/examples/testbeans/example2/Example2Resources_tr.properties
new file mode 100644
index 000000000..a151967fb
--- /dev/null
+++ b/src/examples/org/apache/jmeter/examples/testbeans/example2/Example2Resources_tr.properties
@@ -0,0 +1,3 @@
+#Stored by I18NEdit, may be edited!
+displayName=\u00D6rnek2
+myStringProperty.displayName=Bir Metin
diff --git a/src/i18nedit.properties b/src/i18nedit.properties
index c2fec70fd..aa2de4ab3 100644
--- a/src/i18nedit.properties
+++ b/src/i18nedit.properties
@@ -1,10 +1,13 @@
 #I18NEdit settings for project
 locale.default=en
-locales=de ja no tw
+locales=de ja no tw tr
 main.name=jakarta-jmeter
 personal.Administrator.sourcelocale=en
 personal.Administrator.targetlocale=tw
 personal.Administrator.workmode=directed
+personal.ekesken.sourcelocale=en
+personal.ekesken.targetlocale=tr
+personal.ekesken.workmode=directed
 personal.jordi.sourcelocale=en
 personal.jordi.targetlocale=ja
 personal.jordi.workmode=free
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSamplerResources_tr.properties b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSamplerResources_tr.properties
new file mode 100644
index 000000000..eadfd8f66
--- /dev/null
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSamplerResources_tr.properties
@@ -0,0 +1,17 @@
+#Stored by I18NEdit, may be edited!
+accesslogfile.displayName=Log Dosyas\u0131 Yeri
+defaults.displayName=\u00D6ntan\u0131ml\u0131 Test De\u011Ferleri
+displayName=Log \u00D6rnekleyicisine Eri\u015Fim
+domain.displayName=Sunucu
+domain.shortDescription=Test edilecek sunucunun makine ismi
+filterClassName.displayName=Filtre (\u0130ste\u011Fe ba\u011Fl\u0131)
+filterClassName.shortDescription=Log dosyas\u0131 girdilerini filtrelemek i\u00E7in filtre uygulamas\u0131 se\u00E7 (iste\u011Fe ba\u011Fl\u0131)
+imageParsing.displayName=Resimleri Ayr\u0131\u015Ft\u0131r
+imageParsing.shortDescription=E\u011Fer a\u00E7\u0131ksa, JMeter web sayfas\u0131ndaki resimleri ve kaynaklar\u0131 indirecektir
+logFile.displayName=Log Dosyas\u0131
+logFile.shortDescription=\u0130stekler i\u00E7in ayr\u0131\u015Ft\u0131r\u0131lacak log dosyas\u0131n\u0131n yeri
+parserClassName.displayName=Ayr\u0131\u015Ft\u0131r\u0131c\u0131
+parserClassName.shortDescription=Log dosyas\u0131n\u0131 ayr\u0131\u015Ft\u0131rmak i\u00E7in bir ayr\u0131\u015Ft\u0131r\u0131c\u0131 uygulamas\u0131 kullan. 
+plugins.displayName=Eklenti S\u0131n\u0131flar\u0131
+portString.displayName=Port
+portString.shortDescription=Test edilecek Port Numaras\u0131
diff --git a/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/config/DataSourceElementResources_tr.properties b/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/config/DataSourceElementResources_tr.properties
new file mode 100644
index 000000000..2d2bb87c3
--- /dev/null
+++ b/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/config/DataSourceElementResources_tr.properties
@@ -0,0 +1,30 @@
+#Stored by I18NEdit, may be edited!
+autocommit.displayName=An\u0131nda \u0130\u015Flem
+autocommit.shortDescription=Sorgular\u0131n an\u0131nda olarak i\u015Flenip i\u015Flenmeyece\u011Fi.
+checkQuery.displayName=Do\u011Frulama Sorgusu
+checkQuery.shortDescription=Ba\u011Flant\u0131n\u0131n hala \u00E7al\u0131\u015F\u0131p \u00E7al\u0131\u015Fmad\u0131\u011F\u0131n\u0131 kontrol eden sorgu. Sadece "Canl\u0131Tut" se\u00E7ili ise ge\u00E7erlidir.
+connectionAge.displayName=Maksimum Ba\u011Flant\u0131 Ya\u015F\u0131 (ms)
+connectionAge.shortDescription=Bo\u015Ftaki ba\u011Flant\u0131n\u0131n kopar\u0131lmadan korunaca\u011F\u0131 maksimum milisaniye say\u0131s\u0131
+dataSource.displayName=De\u011Fi\u015Fken \u0130smi
+dataSource.shortDescription=Havuzun ba\u011Flanaca\u011F\u0131 JMeter de\u011Fi\u015Fkeninin ismi.
+database.displayName=Veritaban\u0131 Ba\u011Flant\u0131s\u0131 Ayar\u0131
+dbUrl.displayName=Veritaban\u0131 Adresi (URL)
+dbUrl.shortDescription=Veritaban\u0131 i\u00E7in tam adres (URL), jdbc protokolu k\u0131s\u0131mlar\u0131 dahil
+displayName=JDBC Ba\u011Flant\u0131 Ayarlar\u0131
+driver.displayName=JDBC S\u00FCr\u00FCc\u00FC s\u0131n\u0131f\u0131
+driver.shortDescription=Kullan\u0131lacak JDBC s\u00FCr\u00FCc\u00FCs\u00FCn\u00FCn tam paket ve s\u0131n\u0131f ismi\n(JMeter s\u0131n\u0131f yolunda(classpath) yer almal\u0131)
+keep-alive.displayName=Havuzla ba\u011Flant\u0131 Do\u011Frulamas\u0131
+keepAlive.displayName=Canl\u0131-Tut
+keepAlive.shortDescription=Havuzun ba\u011Flant\u0131 do\u011Frulamas\u0131 yap\u0131p yapmayaca\u011F\u0131. "no" ise, Ba\u011Flant\u0131 Ya\u015F\u0131 ve Ba\u011Flant\u0131 Do\u011Frulamas\u0131 yoksay\u0131lacak.
+password.displayName=\u015Eifre
+password.shortDescription=Veritaban\u0131na ba\u011Flan\u0131l\u0131rken kullan\u0131lacak \u015Fifre
+pool.displayName=Ba\u011Flant\u0131 Havuzu Ayar\u0131
+poolMax.displayName=Maksimum Ba\u011Flant\u0131 Say\u0131s\u0131
+poolMax.shortDescription=Havuzun ayn\u0131 zamanda a\u00E7aca\u011F\u0131 maksimum ba\u011Flant\u0131 say\u0131s\u0131
+timeout.displayName=Havuz Zaman A\u015F\u0131m\u0131
+timeout.shortDescription=Havuz bir ba\u011Flant\u0131 uygun hale gelinceye kadar t\u00FCm istekleri engeller. Bu hata d\u00F6nmeden \u00F6nceki maksimum engelleme zaman\u0131d\u0131r.
+trimInterval.displayName=Bo\u015Ftaki Toparlama Ara\u015F\u0131\u011F\u0131 (ms)
+trimInterval.shortDescription=Havuz d\u00FCzenli aral\u0131klarla bo\u015Ftaki fazladan ba\u011Flant\u0131lar\u0131 kald\u0131r\u0131r.
+username.displayName=Kullan\u0131c\u0131 ismi
+username.shortDescription=Veritaban\u0131na ba\u011Flan\u0131l\u0131rken kullan\u0131lacak kullan\u0131c\u0131 ismi
+varName.displayName=Havuzla \u0130li\u015Fkilendirilecek De\u011Fi\u015Fkenin \u0130smi
diff --git a/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/sampler/JDBCSamplerResources_tr.properties b/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/sampler/JDBCSamplerResources_tr.properties
new file mode 100644
index 000000000..04d0bb93d
--- /dev/null
+++ b/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/sampler/JDBCSamplerResources_tr.properties
@@ -0,0 +1,14 @@
+#Stored by I18NEdit, may be edited!
+dataSource.displayName=De\u011Fi\u015Fken \u0130smi
+dataSource.shortDescription=Ba\u011Flant\u0131 havuzunun ili\u015Fkilendirilece\u011Fi JMeter de\u011Fi\u015Fkeninin ismi.
+displayName=JDBC \u0130ste\u011Fi
+query.displayName=Sorgu
+query.shortDescription=Veritaban\u0131na g\u00F6nderilecek SQL sorgusu
+queryArguments.displayName=Parametre de\u011Ferleri
+queryArguments.shortDescription=SQL parametresi de\u011Ferleri
+queryArgumentsTypes.displayName=Parametre tipleri
+queryArgumentsTypes.shortDescription=java.sql.Types'tan JDBC Tip isimleri. VARCHAR, INTEGER, gibi.
+queryType.displayName=Sorgu Tipi
+queryType.shortDescription=SQL ifadesinin select veya update ifadesi olarak \u00E7al\u0131\u015Ft\u0131r\u0131laca\u011F\u0131n\u0131 belirler.
+sql.displayName=SQL Sorgusu
+varName.displayName=Havuzla ili\u015Fkilendirilecek De\u011Fi\u015Fkenin \u0130smi
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index b059d29bd..532205479 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,1096 +1,1097 @@
 <?xml version="1.0"?> 
 <!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at
  
        http://www.apache.org/licenses/LICENSE-2.0
  
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 -->
 <document>   
 <properties>     
 	<author email="jmeter-dev AT jakarta.apache.org">JMeter developers</author>     
 	<title>History of Changes</title>   
 </properties> 
 <body> 
 <section name="History of Changes"> 
 <p><b>Changes sections are chronologically ordered from top (most recent) to bottom 
 (least recent)</b></p>  
 
 <!--  ===================  -->
 
 <h3>Version 2.3.2</h3>
 
 <h4>Summary of main changes</h4>
 
 <p>Fixes Clear Cookie each iteration bug (unfortunately introduced in 2.3.1)</p>
 
 <p>Switch Controller now works properly with functions and variables, 
 and the condition can now be a name instead of a number.
 Simple Controller now works properly under a While Controller</p>
 
 <p>CSV fields can now contain delimiters. 
 CSV and XML files can now contain additional variables (define the JMeter property sample_variables).</p>
 
 <p>Response Assertion can now match on substrings</p>
 
 <p>Number of classes loaded in non-GUI mode is much reduced.</p>
 
 <h4>Known bugs</h4>
 
 <p>Once Only controller behaves OK under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <h4>Incompatible changes</h4>
 <ul>
 <li>
 To reduce the number of classes loaded in non-GUI mode,
 Functions will only be found if their classname contains the string
 '.functions.' and does not contain the string '.gui.'.
 All existing JMeter functions conform to this restriction.
 To revert to earlier behaviour, comment or change the properties classfinder.functions.* in jmeter.properties.
 </li>
 </ul>
 
 <h4>Bug fixes</h4>
 <ul>
 <li>Check that the CSV delimiter is reasonable.</li>
 <li>Fix Switch Controller to work properly with functions and variables</li>
 <li>Bug 44011 - application/soap+xml not treated as a text type</li>
 <li>Bug 43427 - Simple Controller is only partly executed in While loop</li>
 <li>Bug 33954 - Stack Overflow in If/While controllers (may have been fixed previously)</li>
 <li>Bug 44022 - Memory Leak when closing test plan</li>
 <li>Bug 44042 - Regression in Cookie Manager (Bug introduced in 2.3.1)</li>
 <li>Bug 41028 - JMeter server doesn't alert the user when the host is defined as a loopback address</li>
 <li>Bug 44142 - Function __machineName causes NPE if parameters are omitted.</li>
 <li>Bug 44144 - JMS point-to-point: request response test does not work</li>
 <li>Bug 44314 - Not possible to add more than one SyncTimer</li>
 </ul>
 
 <h4>Improvements</h4>
 <ul>
 <li>CSV files can now handle fields with embedded delimiters.</li>
 <li>longSum() function added</li>
 <li>Bug 43382 - configure Tidy output (warnings, errors) for XPath Assertion and Post-Processor</li>
 <li>Bug 43984 - trim spaces from port field</li>
 <li>Add optional comment to __log() function</li>
 <li>Make Random function variable name optional</li>
 <li>Reduce class loading in non-GUI mode by only looking for Functions in class names
 that contain '.functions.' and don't contain '.gui.'</li>
 <li>Bug 43379 - Switch Controller now supports selection by name as well as number</li>
 <li>Can specify list of variable names to be written to JTL files (CSV and XML format)</li>
 <li>Now checks that the remoteStart options -r and -R are only used with non_GUI -n option</li>
 <li>Bug 44184 - Allow header to be saved with Aggregate Graph data</li>
 <li>Added "Save Table Data" buttons to Aggregate and Summary Reports - save table as CSV format with header</li>
 <li>Allow most functions to be used on the Test Plan. 
 Note __split() and __regex() cannot be used on the Test Plan.</li>
 <li>Allow Global properties to be loaded from a file, e.g. -Gglobal.properties</li>
 <li>Add "Substring" option to Response Assertion</li>
+<li>Bug 44378 - Turkish localisation</li>
 </ul>
 
 <h4>Non-functional changes</h4>
 <ul>
 <li>Better handling of MirrorServer startup problems and improved unit test.</li>
 <li>Build process now detects missing 3rd party libraries and reports need for both binary and source archives</li>
 <li>Skip BeanShell tests if jar is not present</li>
 <li>Update to Xalan 2.7.1, Commons-Logging 1.1.1, XStream 1.2.2</li>
 <li>Use properties for log/logn function descriptions</li>
 <li>Check that all jmx files in the demos directory can be loaded OK</li>
 <li>Update copyright to 2008; use copy tag instead of numeric character in HTML output</li>
 </ul>
 
 <!--  ===================  -->
 
 <h3>Version 2.3.1</h3>
 <h4>Summary of changes</h4>
 
 <h5>JMeter Proxy</h5>
 
 <p>
 The Proxy spoof function was broken in 2.3; it has been fixed. 
 Spoof now supports an optional parameter to limit spoofing to particular URLs. 
 This is useful for HTTPS pages that have insecure content - e.g. images/stylesheets may be accessed using HTTP.
 Spoofed responses now drop the default port (443) from https links to make them work better.
 </p>
 <p>
 Ignored proxy samples are now visible in Listeners - the label is enclosed in [ and ] as an indication. 
 Proxy documentation has been improved.
 </p>
 
 <h5>GUI changes</h5>
 
 <p>The Add menus show element types in the order in which they are processed 
 - see <a href="usermanual/test_plan.html#executionorder">Test Plan Execution Order</a>.
 It is no longer possible to add test elements to inappropriate parts of the tree
 - e.g. samplers cannot be added directly under a test plan. 
 This also applies to Paste and drag and drop.
 </p>
 
 <p>
 The File menu now supports a "Revert" option, which reloads the current file.
 Also the last few file names used are remembered for easy reloading.
 </p>
 
 <p>
 The Options Menu now supports Collapse All and Expand All items to collapse and expand the test tree.
 </p>
 
 <h5>Remote testing</h5>
 
 <p>
 The JMeter server now starts the RMI server directly (by default).
 This simplifies testing, and means that the RMI server will be stopped when the server stops.
 </p>
 
 <p>
 Functions can now be used in Listener filenames (variables do not work).
 </p>
 
 <p>
 Command-line option -G can now be used to define properties for remote servers.
 Option -X can be used to stop a remote server after a non-GUI run.
 Server can be set to automatically exit after a single test (set property server.exitaftertest=true).
 </p>
 
 <h5>Other enhancements</h5>
 
 <p>
 JMeter startup no longer loads as many classes; this should reduce memory requirements.
 </p>
 
 <p>
 Parameter and file support added to all BeanShell elements. 
 Javascript function now supports access to JMeter objects; 
 Jexl function always did have access, but the documentation has now been included.
 New functions __eval() and __evalVar() for evaluating variables.
 </p>
 
 <p>
 CSV files with the correct header column names are now automatically recognised when loaded. 
 There is no need to configure the properties.
 </p>
 
 <p>
 The hostname can now be saved in CSV and XML output files. 
 New "Successes only" option added when saving result files.
 Errors / Successes only option is now supported when loading XML and CSV files. 
 </p>
 
 <p>
 General documentation improvements.
 </p>
 
 <h5>HTTP</h5>
 
 <p>PUT and DELETE should now work properly. 
 Cookie Manager no longer clears manually entered cookies.
 </p>
 <p>Now handles the META tag http-equiv charset</p>
 
 <h5>JDBC</h5>
 
 <p>JDBC Sampler now allows INOUT and OUT parameters for Called procedures. 
 JDBC Sampler now allows per-thread connections - set Max Connections = 0 in JDBC Config.
 </p>
 
 <hr></hr>
 <!-- ========================================================= -->
 
 <h4>Incompatible changes</h4>
 <ul>
 <li>JMeter server now creates the RMI registry by default.
 If the RMI registry has already been started externally, this will generate a warning message, but the server will continue.
 This should not affect JMeter testing. 
 However, if you are also using the RMI registry for other applications there may be problems.
 For example, when the JMeter server shuts down it will stop the RMI registry.
 Also user-written command files may need to be adjusted (the ones supplied with JMeter have been updated).
 To revert to the earlier behaviour, define the JMeter property: <b>server.rmi.create=false</b>.
 </li>
 <li>The Proxy server removes If-Modified-Since and If-None-Match headers from generated Header Managers.
 To revert to the previous behaviour, define the property proxy.headers.remove with no value</li>
 </ul>
 
 <h4>Bug fixes</h4>
 <ul>
 <li>Bug 43430 - Count of active threads is incorrect for remote samples</li>
 <li>Throughput Controller was not working for "all thread" counts</li>
 <li>If a POST body is built from parameter values only, these are now encoded if the checkbox is set.</li>
 <li>Bug 43584 - Assertion Failure Message contains a comma that is also used as the delimiter for CSV files</li>
 <li>HTTP Mirror Server now always returns the exact same content, it used to return incorrect data if UTF-8 encoding was used for HTTP POST body, for example</li>
 <li>Bug 43612 - HTTP PUT does not honor request parameters</li>
 <li>Bug 43694 - ForEach Controller (empty collection processing error)</li>
 <li>Bug 42012 - Variable Listener filenames do not get processed in remote tests.
 Filenames can now include function references; variable references do not work.</li>
 <li>Ensure Listener nodes get own save configuration when copy-pasted</li>
 <li>Correct Proxy Server include and exclude matching description - port and query are included, contrary to previously documented.</li>
 <li>Aggregate Graph and Aggregate Report Column Header is KB/Sec; fixed the values to be KB rather than bytes</li>
 <li>
 Fix SamplingStatCalculator so it no longer adds elapsed time to endTime, as this is handled by SampleResult.
 This corrects discrepancies between Summary Report and Aggregate Report throughput calculation.
 </li>
 <li>Default HTTPSampleResult to ISO-8859-1 encoding</li>
 <li>Fix default encoding for blank encoding</li>
 <li>Fix Https spoofing (port problem) which was broken in 2.3</li>
 <li>Fix HTTP (Java) sampler so http.java.sampler.retries means retries, i.e. does not include initial try</li>
 <li>Fix SampleResult dataType checking to better detect TEXT documents</li>
 </ul>
 
 <h4>Improvements</h4>
 <ul>
 <li>Add run_gui Ant target, to package and then start the JMeter GUI from Ant</li>
 <li>Add File->Revert to easily drop the current changes and reload the project file currently loaded</li>
 <li>Bug 31366 - Remember recently opened file(s)</li>
 <li>Bug 43351 - Add support for Parameters and script file to all BeanShell test elements</li>
 <li>SaveService no longer needs to instantiate classes</li>
 <li>New functions: __eval() and __evalVar()</li>
 <li>Menu items now appear in execution order</li>
 <li>Test Plan items can now only be dropped/pasted/merged into parts of the tree where they are allowed</li>
 <li>Property Display to show the value of System and JMeter properties and allow them to be changed</li>
 <li>Bug 43451 - Allow Regex Extractor to operate on Response Code/Message</li>
 <li>JDBC Sampler now allows INOUT and OUT parameters for Called procedures</li>
 <li>JDBC Sampler now allows per-thread connections</li>
 <li>Cookie Manager not longer clears cookies defined in the GUI</li>
 <li>HTTP Parameters without names are ignored (except for POST requests with no file)</li>
 <li>"Save Selection As" added to main menu; now checks only item is selected</li>
 <li>Test Plan now has Paste menu item (paste was already supported via ^V)</li>
 <li>If the default delimiter does not work when loading a CSV file, guess the delimiter by analysing the header line.</li>
 <li>Add optional "loopback" protocol for HttpClient sampler</li>
 <li>HTTP Mirror Server now supports blocking waiting for more data to appear, if content-length header is present in request</li>
 <li>HTTP Mirror Server GUI now has the Start and Stop buttons in a more visible place</li>
 <li>Server mode now creates the RMI registry; to disable set the JMeter property server.rmi.create=false</li>
 <li>HTTP Sampler now supports using MIME Type field to specify content-type request header when body is constructed from parameter values</li>
 <li>Enable exit after a single server test - define JMeter property server.exitaftertest=true</li>
 <li>Added -G option to set properties in remote servers</li>
 <li>Added -X option to stop remote servers after non-GUI run</li>
 <li>Bug 43485 - Ability to specify keep-alive on SOAP/XML-RPC request</li>
 <li>Bug 43678 - Handle META tag http-equiv charset</li>
 <li>Bug 42555 - [I18N] Proposed corrections for the french translation</li>
 <li>Bug 43727 - Test Action does not support variables or functions</li>
 <li>The Proxy server removes If-Modified-Since and If-None-Match headers from generated Header Managers by default.
 To change the list of removed headers, define the property proxy.headers.remove as a comma-separated list of headers to remove</li>
 <li>The javaScript function now has access to JMeter variables and context etc. See <a href="usermanual/functions.html#__javaScript">JavaScript function</a></li>
 <li>Use drop-down list for BSF Sampler language field</li>
 <li>Add hostname to items that can be saved in CSV and XML output files.</li>
 <li>Errors only flag is now supported when loading XML and CSV files</li>
 <li>Ensure ResultCollector uses SaveService encoding</li>
 <li>Proxy now rejects attempts to use it with https</li>
 <li>Proxy spoofing can now use RE matching to determine which urls to spoof (useful if images are not https)</li>
 <li>Proxy spoofing now drops the default HTTPS port (443) when converting https: links to http:</li>
 <li>Add Successes Only logging and display</li>
 <li>The JMeter log file name is formatted as a SimpleDateFormat (applied to the current date) if it contains paired single-quotes,  .e.g. 'jmeter_'yyyyMMddHHmmss'.log'</li>
 <li>Added Collapse All and Expand All Option menu items</li>
 <li>Allow optional definition of extra content-types that are viewable as text</li>
 </ul>
 
 <h4>Non-functional Improvements</h4>
 <ul>
 <li>Functor code tightened up; Functor can now be used with interfaces, as well as pre-defined targets and parameters.</li>
 <li>Save graphics function now prompts before overwriting an existing file</li>
 <li>Debug Sampler and Debug PostProcessor added.</li>
 <li>Fixed up method names in Calculator and SamplingStatCalculator</li>
 <li>Tidied up Listener documentation.</li>
 </ul>
 
 <!--  ===================  -->
 
 <h3>Version 2.3</h3>
 
 <h3>Fixes since 2.3RC4</h3>
 
 <h4>Bug fixes</h4>
 <ul>
 <li>Fix NPE in SampleResultConverter - XStream PrettyPrintWriter cannot handle nulls</li>
 <li>If Java HTTP sampler sees null ResponseMessage, replace with HTTP header</li>
 <li>Bug 43332 - 2.3RC4 does not clear Guis based on TestBean</li>
 <li>Bug 42948 - Problems with Proxy gui table fields in Java 1.6</li>
 <li>Fixup broken jmeter-server script</li>
 <li>Bug 43364 - option to revert If Controller to pre 2.3RC3 behaviour</li>
 <li>Bug 43449 - Statistical Remote mode does not handle Latency</li>
 <li>Bug 43450 (partial fix) - Allow SampleCount and ErrorCount to be saved to/restored from files</li>
 </ul>
 
 <h4>Improvements</h4>
 <ul>
 <li>Add nameSpace option to XPath extractor</li>
 <li>Add NULL parameter option to JDBC sampler</li>
 <li>Add documentation links for Rhino and BeanShell to functions; clarify variables and properties</li>
 <li>Ensure uncaught exceptions are logged</li>
 <li>Look for user.properties and system.properties in JMeter bin directory if not found locally</li>
 </ul>
 
 <h4>Fixes since 2.3RC3</h4>
 <ul>
 <li>Fixed NPE in Summariser (bug introduced in 2.3RC3)</li>
 <li>Fixed setup of proxy port (bug introduced in 2.3RC3)</li>
 <li>Fixed errors when running non-GUI on a headless host (bug introduced in 2.3RC3)</li>
 <li>Bug 43054 - SSLManager causes stress tests to saturate and crash (bug introduced in 2.3RC3)</li>
 <li>Clarified HTTP Request Defaults usage of the port field</li>
 <li>Bug 43006 - NPE if icon.properties file not found</li>
 <li>Bug 42918 - Size Assertion now treats an empty response as having zero length</li>
 <li>Bug 43007 - Test ends before all threadgroups started</li>
 <li>Fix possible NPE in HTTPSampler2 if 302 does not have Location header.</li>
 <li>Bug 42919 - Failure Message blank in CSV output [now records first non-blank message]</li>
 <li>Add link to Extending JMeter PDF</li>
 <li>Allow for quoted charset in Content-Type parsing</li>
 <li>Bug 39792 - ClientJMeter synchronisation needed</li>
 <li>Bug 43122 - GUI changes not always picked up when short-cut keys used (bug introduced in 2.3RC3)</li>
 <li>Bug 42947 - TestBeanGUI changes not picked up when short-cut keys used</li>
 <li>Added serializer.jar (needed for update to xalan 2.7.0)</li>
 <li>Bug 38687 - Module controller does not work in non-GUI mode</li>
 </ul>
 
 <h4>Improvements since 2.3RC3</h4>
 <ul>
 <li>Add stop thread option to CSV Dataset</li>
 <li>Updated commons-httpclient to 3.1</li>
 <li>Bug 28715 - allow variable cookie values (set CookieManager.allow_variable_cookies=false to disable)</li>
 <li>Bug 40873 - add JMS point-to-point non-persistent delivery option</li>
 <li>Bug 43283 - Save action adds .jmx if not present; checks for existing file on Save As</li>
 <li>Control+A key does not work for Save All As; changed to Control+Shift+S</li>
 <li>Bug 40991 - Allow Assertions to check Headers</li>
 </ul>
 
 <h3>Version 2.3RC3</h3>
 
 <h4>Known problems/restrictions:</h4>
 <p>
 The JMeter remote server does not support multiple concurrent tests - each remote test should be run in a separate server.
 Otherwise tests may fail with random Exceptions, e.g. ConcurrentModification Exception in StandardJMeterEngine.
 See bug 43168.
 </p>
 <p>
 The default HTTP Request (not HTTPClient) sampler may not work for HTTPS connections via a proxy.
 This appears to be due to a Java bug, see <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id=39337">Bug 39337</a>.
 To avoid the problem, try a more recent version of Java, or switch to the HTTPClient version of the HTTP Request sampler.
 </p>
 <p>Transaction Controller parent mode does not support nested Transaction Controllers.
 Doing so may cause a Null Pointer Exception in TestCompiler.
 </p>
 <p>Thread active counts are always zero in CSV and XML files when running remote tests.
 </p>
 <p>The property file_format.testlog=2.1 is treated the same as 2.2.
 However JMeter does honour the 3 testplan versions.</p>
 <p>
 Bug 22510 - JMeter always uses the first entry in the keystore.
 </p>
 <p>
 Remote mode does not work if JMeter is installed in a directory where the path name contains spaces.
 </p>
 <p>
 BeanShell test elements leak memory.
 This can be reduced by using a file instead of including the script in the test element.
 </p>
 <p>
 Variables and functions do not work in Listeners in client-server (remote) mode so they cannot be used
 to name log files in client-server mode.
 </p>
 <p>
 CSV Dataset variables are defined after configuration processing is completed,
 so they cannot be used for other configuration items such as JDBC Config.
 (see <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id=40934">Bug 40394 </a>)
 </p>
 
 <h4>Summary of changes (for more details, see below)</h4>
 <p>
 Some of the main enhancements are:
 </p>
 <ul>
 <li>Htmlparser 2.0 now used for parsing</li>
 <li>HTTP Authorisation now supports domain and realm</li>
 <li>HttpClient options can be specified via httpclient.parameters file</li>
 <li>HttpClient now behaves the same as Java Http for SSL certificates</li>
 <li>HTTP Mirror Server to allow local testing of HTTP samplers</li>
 <li>HTTP Proxy supports XML-RPC recording, and other proxy improvements</li>
 <li>__V() function allows support of nested variable references</li>
 <li>LDAP Ext sampler optionally parses result sets and supports secure mode</li>
 <li>FTP Sampler supports Ascii/Binary mode and upload</li>
 <li>Transaction Controller now optionally generates a Sample with subresults</li>
 <li>HTTPS session contexts are now per-thread, rather than shared. This gives better emulation of multiple users</li>
 <li>BeanShell elements now support ThreadListener and TestListener interfaces</li>
 <li>Coloured icons in Tree View Listener and elsewhere to better differentiate failed samples.</li>
 </ul>
 <p>
 The main bug fixes are:
 </p>
 <ul>
 <li>HTTPS (SSL) handling now much improved</li>
 <li>Various Remote mode bugs fixed</li>
 <li>Control+C and Control+V now work in the test tree</li>
 <li>Latency and Encoding now available in CSV log output</li>
 <li>Test elements no longer default to previous contents; test elements no longer cleared when changing language.</li>
 </ul>
 
 <h4>Incompatible changes (usage):</h4>
 <p>
 <b>N.B. The javax.net.ssl properties have been moved from jmeter.properties to system.properties,
 and will no longer work if defined in jmeter.properties.</b>
 <br></br>
 The new arrangement is more flexible, as it allows arbitrary system properties to be defined.
 </p>
 <p>
 SSL session contexts are now created per-thread, rather than being shared.
 This generates a more realistic load for HTTPS tests.
 The change is likely to slow down tests with many SSL threads.
 The original behaviour can be enabled by setting the JMeter property:
 <pre>
 https.sessioncontext.shared=true
 </pre>
 </p>
 <p>
 The LDAP Extended Sampler now uses the same panel for both Thread Bind and Single-Bind tests.
 This means that any tests using the Single-bind test will need to be updated to set the username and password.
 </p>
 <p>
 Bug 41140: JMeterThread behaviour was changed so that PostProcessors are run in forward order
 (as they appear in the test plan) rather than reverse order as previously.
 The original behaviour can be restored by setting the following JMeter property:
 <br/>
 jmeterthread.reversePostProcessors=true
 </p>
 <p>
 The HTTP Authorisation Manager now has extra columns for domain and realm, 
 so the temporary work-round of using '\' and '@' in the username to delimit the domain and realm
 has been removed.
 </p>
 <p>
 Control-Z no longer used for Remote Start All - this now uses Control+Shift+R
 </p>
 <p>
 HttpClient now uses pre-emptive authentication. 
 This can be changed by setting the following:
 <pre>
 jmeter.properties:
 httpclient.parameters.file=httpclient.parameters
 
 httpclient.parameters:
 http.authentication.preemptive$Boolean=false
 </pre>
 </p>
 
 <p>
 The port field in HTTP Request Defaults is no longer ignored for https samplers if it is set to 80.
 </p>
 
 <h4>Incompatible changes (development):</h4>
 <p>
 <b>N.B.</b>The clear() method was defined in the following interfaces: Clearable, JMeterGUIComponent and TestElement.
 The methods serve different purposes, so two of them were renamed: 
 the Clearable method is now clearData() and the JMeterGUIComponent method is now clearGui().
 3rd party add-ons may need to be rebuilt.
 </p>
 <p>
 Calulator and SamplingStatCalculator classes no longer provide any formatting of their data.
 Formatting should now be done using the jorphan.gui Renderer classes.
 </p>
 <p>
 Removed deprecated method JMeterUtils.split() - use JOrphanUtils version instead.
 </p>
 <p>
 Removed method saveUsingJPEGEncoder() from SaveGraphicsService.
 It was unused so far, and used the only Sun-specific class in JMeter.
 </p>
 
 
 <h4>New functionality/improvements:</h4>
 <ul>
 <li>Add Domain and Realm support to HTTP Authorisation Manager</li>
 <li>HttpClient now behaves the same as the JDK http sampler for invalid certificates etc</li>
 <li>Added httpclient.parameters.file to allow HttpClient parameters to be defined</li>
 <li>Bug 33964 - Http Requests can send a file as the entire post body if name/type are omitted</li>
 <li>Bug 41705 - add content-encoding option to HTTP samplers for POST requests</li>
 <li>Bug 40933, 40945 - optional RE matching when retrieving embedded resource URLs</li>
 <li>Bug 27780 - (patch 19936) create multipart/form-data HTTP request without uploading file</li>
 <li>Bug 42098 - Use specified encoding for parameter values in HTTP GET</li>
 <li>Bug 42506 - JMeter threads now use independent SSL sessions</li>
 <li>Bug 41707 - HTTP Proxy XML-RPC support</li>
 <li>Bug 41880 - Add content-type filtering to HTTP Proxy Server</li>
 <li>Bug 41876 - Add more options to control what the HTTP Proxy generates</li>
 <li>Bug 42158 - Improve support for multipart/form-data requests in HTTP Proxy server</li>
 <li>Bug 42173 - Let HTTP Proxy handle encoding of request, and undecode parameter values</li>
 <li>Bug 42674 - default to pre-emptive HTTP authorisation if not specified</li>
 <li>Support "file" protocol in HTTP Samplers</li>
 <li>Http Autoredirects are now enabled by default when creating new samplers</li>
 
 <li>Bug 40103 - various LDAP enhancements</li>
 <li>Bug 40369 - LDAP: Stable search results in sampler</li>
 <li>Bug 40381 - LDAP: more descriptive strings</li>
 
 <li>BeanShell Post-Processor no longer ignores samples with zero-length result data</li>
 <li>Added beanshell.init.file property to run a BeanShell script at startup</li>
 <li>Bug 39864 - BeanShell init files now found from currrent or bin directory</li>
 <li>BeanShell elements now support ThreadListener and TestListener interfaces</li>
 <li>BSF Sampler passes additional variables to the script</li>
 
 <li>Added timeout for WebService (SOAP) Sampler</li>
 
 <li>Bug 40825 - Add JDBC prepared statement support</li>
 <li>Extend JDBC Sampler: Commit, Rollback, AutoCommit</li>
 
 <li>Bug 41457 - Add TCP Sampler option to not re-use connections</li>
 
 <li>Bug 41522 - Use JUnit sampler name in sample results</li>
 
 <li>Bug 42223 - FTP Sampler can now upload files</li>
 
 <li>Bug 40804 - Change Counter default to max = Long.MAX_VALUE</li>
 
 <li>Use property jmeter.home (if present) to override user.dir when starting JMeter</li>
 <li>New -j option to easily change jmeter log file</li>
 
 <li>HTTP Mirror Server Workbench element</li>
 
 <li>Bug 41253 - extend XPathExtractor to work with non-NodeList XPath expressions</li>
 <li>Bug 42088 - Add XPath Assertion for booleans</li>
 
 <li>Added __V variable function to resolve nested variable names</li>
 
 <li>Bug 40369 - Equals Response Assertion</li>
 <li>Bug 41704 - Allow charset encoding to be specified for CSV DataSet</li>
 <li>Bug 41259 - Comment field added to all test elements</li>
 <li>Add standard deviation to Summary Report</li>
 <li>Bug 41873 - Add name to AssertionResult and display AssertionResult in ViewResultsFullVisualizer</li>
 <li>Bug 36755 - Save XML test files with UTF-8 encoding</li>
 <li>Use ISO date-time format for Tree View Listener (previously the year was not shown)</li>
 <li>Improve loading of CSV files: if possible, use header to determine format; guess timestamp format if not milliseconds</li>
 <li>Bug 41913 - TransactionController now creates samples as sub-samples of the transaction</li>
 <li>Bug 42582 - JSON pretty printing in Tree View Listener</li>
 <li>Bug 40099 - Enable use of object variable in ForEachController</li>
 
 <li>Bug 39693 - View Result Table uses icon instead of check box</li>
 <li>Bug 39717 - use icons in the results tree</li>
 <li>Bug 42247 - improve HCI</li>
 <li>Allow user to cancel out of Close dialogue</li>
 </ul>
 
 <h4>Non-functional improvements:</h4>
 <ul>
 <li>Functor calls can now be unit tested</li>
 <li>Replace com.sun.net classes with javax.net</li>
 <li>Extract external jar definitions into build.properties file</li>
 <li>Use specific jar names in build classpaths so errors are detected sooner</li>
 <li>Tidied up ORO calls; now only one cache, size given by oro.patterncache.size, default 1000</li>
 <li>Bug 42326 - Order of elements in .jmx files changes</li>
 </ul>
 
 <h4>External jar updates:</h4>
 <ul>
 <li>Htmlparser 2.0-20060923</li>
 <li>xstream 1.2.1/xpp3_min-1.1.3.4.O</li>
 <li>Batik 1.6</li>
 <li>BSF 2.4.0</li>
 <li>commons-collections 3.2</li>
 <li>commons-httpclient-3.1-rc1</li>
 <li>commons-jexl 1.1</li>
 <li>commons-lang-2.3 (added)</li>
 <li>JUnit 3.8.2</li>
 <li>velocity 1.5</li>
 <li>commons-io 1.3.1 (added)</li>
 </ul>
 
 <h4>Bug fixes:</h4>
 <ul>
 <li>Bug 39773 - NTLM now needs local host name - fix other call</li>
 <li>Bug 40438 - setting "httpclient.localaddress" has no effect</li>
 <li>Bug 40419 - Chinese messages translation fix</li>
 <li>Bug 39861 - fix typo</li>
 <li>Bug 40562 - redirects no longer invoke RE post processors</li>
 <li>Bug 40451 - set label if not set by sampler</li>
 <li>Fix NPE in CounterConfig.java in Remote mode</li>
 <li>Bug 40791 - Calculator used by Summary Report</li>
 <li>Bug 40772 - correctly parse missing fields in CSV log files</li>
 <li>Bug 40773 - XML log file timestamp not parsed correctly</li>
 <li>Bug 41029 - JMeter -t fails to close input JMX file</li>
 <li>Bug 40954 - Statistical mode in distributed testing shows wrong results</li>
 <li>Fix ClassCast Exception when using sampler that returns null, e..g TestAction</li>
 <li>Bug 41140 - Post-processors are run in reverse order</li>
 <li>Bug 41277 - add Latency and Encoding to CSV output</li>
 <li>Bug 41414 - Mac OS X may add extra item to -jar classpath</li>
 <li>Fix NPE when saving thread counts in remote testing</li>
 <li>Bug 34261 - NPE in HtmlParser (allow for missing attributes)</li>
 <li>Bug 40100 - check FileServer type before calling close</li>
 <li>Bug 39887 - jmeter.util.SSLManager: Couldn't load keystore error message</li>
 <li>Bug 41543 - exception when webserver returns "500 Internal Server Error" and content-length is 0</li>
 <li>Bug 41416 - don't use chunked input for text-box input in SOAP-RPC sampler</li>
 <li>Bug 39827 - SOAP Sampler content length for files</li>
 <li>Fix Class cast exception in Clear.java</li>
 <li>Bug 40383 - don't set content-type if already set</li>
 <li>Mailer Visualiser test button now works if test plan has not yet been saved</li>
 <li>Bug 36959 - Shortcuts "ctrl c" and "ctrl v" don't work on the tree elements</li>
 <li>Bug 40696 - retrieve embedded resources from STYLE URL() attributes</li>
 <li>Bug 41568 - Problem when running tests remotely when using a 'Counter'</li>
 <li>Fixed various classes that assumed timestamps were always end time stamps:
 <ul>
 <li>SamplingStatCalculator</li>
 <li>JTLData</li>
 <li>RunningSample</li>
 </ul>
 </li>
 <li>Bug 40325 - allow specification of proxyuser and proxypassword for WebServiceSampler</li>
 <li>Change HttpClient proxy definition to use NTCredentials; added http.proxyDomain property for this</li>
 <li>Bug 40371 - response assertion "pattern to test" scrollbar problem</li>
 <li>Bug 40589 - Unescape XML entities in embedded URLs</li>
 <li>Bug 41902 - NPE in HTTPSampler when responseCode = -1</li>
 <li>Bug 41903 - ViewResultsFullVisualizer : status column looks bad when you do copy and paste</li>
 <li>Bug 41837 - Parameter value corruption in proxy</li>
 <li>Bug 41905 - Can't cut/paste/select Header Manager fields in Java 1.6</li>
 <li>Bug 41928 - Make all request headers sent by HTTP Request sampler appear in sample result</li>
 <li>Bug 41944 - Subresults not handled recursively by ResultSaver</li>
 <li>Bug 42022 - HTTPSampler does not allow multiple headers of same name</li>
 <li>Bug 42019 - Content type not stored in redirected HTTP request with subresults</li>
 <li>Bug 42057 - connection can be null if method is null</li>
 <li>Bug 41518 - JMeter changes the HTTP header Content Type for POST request</li>
 <li>Bug 42156 - HTTPRequest HTTPClient incorrectly urlencodes parameter value in POST</li>
 <li>Bug 42184 - Number of bytes for subsamples not added to sample when sub samples are added</li>
 <li>Bug 42185 - If a HTTP Sampler follows a redirect, and is set up to download images, then images are downloaded multiple times</li>
 <li>Bug 39808 - Invalid redirect causes incorrect sample time</li>
 <li>Bug 42267 - Concurrent GUI update failure in Proxy Recording</li>
 <li>Bug 30120 - Name of simple controller is resetted if a new simple controller is added as child</li>
 <li>Bug 41078 - merge results in name change of test plan</li>
 <li>Bug 40077 - Creating new Elements copies values from Existing elements</li>
 <li>Bug 42325 - Implement the "clear" method for the LogicControllers</li>
 <li>Bug 25441 - TestPlan changes sometimes detected incorrectly (isDirty)</li>
 <li>Bug 39734 - Listeners shared after copy/paste operation</li>
 <li>Bug 40851 - Loop controller with 0 iterations, stops evaluating the iterations field</li>
 <li>Bug 24684 - remote startup problems if spaces in the path of the jmeter</li>
 <li>Use Listener configuration when loading CSV data files</li>
 <li>Function methods setParameters() need to be synchronized</li>
 <li>Fix CLI long optional argument to require "=" (as for short options)</li>
 <li>Fix SlowSocket to work properly with Httpclient (both http and https)</li>
 <li>Bug 41612 - Loop nested in If Controller behaves erratically</li>
 <li>Bug 42232 - changing language clears UDV contents</li>
 <li>Jexl function did not allow variables</li>
 </ul>
 
 <h3>Version 2.2</h3>
 
 <h4>Incompatible changes:</h4>
 <p>
 The time stamp is now set to the sampler start time (it was the end).
 To revert to the previous behaviour, change the property <b>sampleresult.timestamp.start</b> to false (or comment it)
 </p>
 <p>The JMX output format has been simplified and files are not backwards compatible</p>
 <p>
 The JMeter.BAT file no longer changes directory to JMeter home, but runs from the current working directory.
 The jmeter-n.bat and jmeter-t.bat files change to the directory containing the input file.
 </p>
 <p>
 Listeners are now started slightly later in order to allow variable names to be used.
 This may cause some problems; if so define the following in jmeter.properties:
 <br/>
 jmeterengine.startlistenerslater=false
 </p>
 
 <h4>Known problems:</h4>
 <ul>
 <li>Post-processors run in reverse order (see bug 41140)</li>
 <li>Module Controller does not work in non-GUI mode</li>
 <li>Aggregate Report and some other listeners use increasing amounts of memory as a test progresses</li>
 <li>Does not always handle non-default encoding properly</li>
 <li>Spaces in the installation path cause problems for client-server mode</li>
 <li>Change of Language does not propagate to all test elements</li>
 <li>SamplingStatCalculator keeps a List of all samples for calculation purposes; 
 this can cause memory exhaustion in long-running tests</li>
 <li>Does not properly handle server certificates if they are expired or not installed locally</li>
 </ul>
 
 <h4>New functionality:</h4>
 <ul>
 <li>Report function</li>
 <li>XPath Extractor Post-Processor. Handles single and multiple matches.</li>
 <li>Simpler JMX file format (2.2)</li>
 <li>BeanshellSampler code can update ResponseData directly</li>
 <li>Bug 37490 - Allow UDV as delay in Duration Assertion</li>
 <li>Slow connection emulation for HttpClient</li>
 <li>Enhanced JUnitSampler so that by default assert errors and exceptions are not appended to the error message. 
 Users must explicitly check append in the sampler</li>
 <li>Enhanced the documentation for webservice sampler to explain how it works with CSVDataSet</li>
 <li>Enhanced the documentation for javascript function to explain escaping comma</li>
 <li>Allow CSV Data Set file names to be absolute</li>
 <li>Report Tree compiler errors better</li>
 <li>Don't reset Regex Extractor variable if default is empty</li>
 <li>includecontroller.prefix property added</li>
 <li>Regular Expression Extractor sets group count</li>
 <li>Can now save entire screen as an image, not just the right-hand pane</li>
 <li>Bug 38901 - Add optional SOAPAction header to SOAP Sampler</li>
 <li>New BeanShell test elements: Timer, PreProcessor, PostProcessor, Listener</li>
 <li>__split() function now clears next variable, so it can be used with ForEach Controller</li>
 <li>Bug 38682 - add CallableStatement functionality to JDBC Sampler</li>
 <li>Make it easier to change the RMI/Server port</li>
 <li>Add property jmeter.save.saveservice.xml_pi to provide optional xml processing instruction in JTL files</li>
 <li>Add bytes and URL to items that can be saved in sample log files (XML and CSV)</li>
 <li>The Post-Processor "Save Responses to a File" now saves the generated file name with the
 sample, and the file name can be included in the sample log file.
 </li>
 <li>Change jmeter.bat DOS script so it works from any directory</li>
 <li>New -N option to define nonProxyHosts from command-line</li>
 <li>New -S option to define system properties from input file</li>
 <li>Bug 26136 - allow configuration of local address</li>
 <li>Expand tree by default when loading a test plan - can be disabled by setting property onload.expandtree=false</li>
 <li>Bug 11843 - URL Rewriter can now cache the session id</li>
 <li>Counter Pre-Processor now supports formatted numbers</li>
 <li>Add support for HEAD PUT OPTIONS TRACE and DELETE methods</li>
 <li>Allow default HTTP implementation to be changed</li>
 <li>Optionally save active thread counts (group and all) to result files</li>
 <li>Variables/functions can now be used in Listener file names</li>
 <li>New __time() function; define START.MS/START.YMD/START.HMS properties and variables</li>
 <li>Add Thread Name to Tree and Table Views</li>
 <li>Add debug functions: What class, debug on, debug off</li>
 <li>Non-caching Calculator - used by Table Visualiser to reduce memory footprint</li>
 <li>Summary Report - similar to Aggregate Report, but uses less memory</li>
 <li>Bug 39580 - recycle option for CSV Dataset</li>
 <li>Bug 37652 - support for Ajp Tomcat protocol</li>
 <li>Bug 39626 - Loading SOAP/XML-RPC requests from file</li>
 <li>Bug 39652 - Allow truncation of labels on AxisGraph</li>
 <li>Allow use of htmlparser 1.6</li>
 <li>Bug 39656 - always use SOAP action if it is provided</li>
 <li>Automatically include properties from user.properties file</li>
 <li>Add __jexl() function - evaluates Commons JEXL expressions</li>
 <li>Optionally load JMeter properties from user.properties and system properties from system.properties.</li>
 <li>Bug 39707 - allow Regex match against URL</li>
 <li>Add start time to Table Visualiser</li>
 <li>HTTP Samplers can now extract embedded resources for any required media types</li>
 </ul>
 
 <h4>Bug fixes:</h4>
 <ul>
 <li>Fix NPE when no module selected in Module Controller</li>
 <li>Fix NPE in XStream when no ResponseData present</li>
 <li>Remove ?xml prefix when running with Java 1.5 and no x-jars</li>
 <li>Bug 37117 - setProperty() function should return ""; added optional return of original setting</li>
 <li>Fix CSV output time format</li>
 <li>Bug 37140 - handle encoding better in RegexFunction</li>
 <li>Load all cookies, not just the first; fix class cast exception</li>
 <li>Fix default Cookie path name (remove page name)</li>
 <li>Fixed resultcode attribute name</li>
 <li>Bug 36898 - apply encoding to RegexExtractor</li>
 <li>Add properties for saving subresults, assertions, latency, samplerData, responseHeaders, requestHeaders &amp; encoding</li>
 <li>Bug 37705 - Synch Timer now works OK after run is stopped</li>
 <li>Bug 37716 - Proxy request now handles file Post correctly</li>
 <li>HttpClient Sampler now saves latency</li>
 <li>Fix NPE when using JavaScript function on Test Plan</li>
 <li>Fix Base Href parsing in htmlparser</li>
 <li>Bug 38256 - handle cookie with no path</li>
 <li>Bug 38391 - use long when accumulating timer delays</li>
 <li>Bug 38554 - Random function now uses long numbers</li>
 <li>Bug 35224 - allow duplicate attributes for LDAP sampler</li>
 <li>Bug 38693 - Webservice sampler can now use https protocol</li>
 <li>Bug 38646 - Regex Extractor now clears old variables on match failure</li>
 <li>Bug 38640 - fix WebService Sampler pooling</li>
 <li>Bug 38474 - HTML Link Parser doesn't follow frame links</li>
 <li>Bug 36430 - Counter now uses long rather than int to increase the range</li>
 <li>Bug 38302 - fix XPath function</li>
 <li>Bug 38748 - JDBC DataSourceElement fails with remote testing</li>
 <li>Bug 38902 - sometimes -1 seems to be returned unnecessarily for response code</li>
 <li>Bug 38840 - make XML Assertion thread-safe</li>
 <li>Bug 38681 - Include controller now works in non-GUI mode</li>
 <li>Add write(OS,IS) implementation to TCPClientImpl</li>
 <li>Sample Result converter saves response code as "rc". Previously it saved as "rs" but read with "rc"; it will now also read with "rc".
 The XSL stylesheets also now accept either "rc" or "rs"</li>
 <li>Fix counter function so each counter instance is independent (previously the per-user counters were shared between instances of the function)</li>
 <li>Fix TestBean Examples so that they work</li>
 <li>Fix JTidy parser so it does not skip body tags with background images</li>
 <li>Fix HtmlParser parser so it catches all background images</li>
 <li>Bug 39252 set SoapSampler sample result from XML data</li>
 <li>Bug 38694 - WebServiceSampler not setting data encoding correctly</li>
 <li>Result Collector now closes input files read by listeners</li>
 <li>Bug 25505 - First HTTP sampling fails with "HTTPS hostname wrong: should be 'localhost'"</li>
 <li>Bug 25236 - remove double scrollbar from Assertion Result Listener</li>
 <li>Bug 38234 - Graph Listener divide by zero problem</li>
 <li>Bug 38824 - clarify behaviour of Ignore Status</li>
 <li>Bug 38250 - jmeter.properties "language" now supports country suffix, for zh_CN and zh_TW etc</li>
 <li>jmeter.properties file is now closed after it has been read</li>
 <li>Bug 39533 - StatCalculator added wrong items</li>
 <li>Bug 39599 - ConcurrentModificationException</li>
 <li>HTTPSampler2 now handles Auto and Follow redirects correctly</li>
 <li>Bug 29481 - fix reloading sample results so subresults not counted twice</li>
 <li>Bug 30267 - handle AutoRedirects properly</li>
 <li>Bug 39677 - allow for space in JMETER_BIN variable</li>
 <li>Use Commons HttpClient cookie parsing and management. Fix various problems with cookie handling.</li>
 <li>Bug 39773 - NTCredentials needs host name</li>
 </ul>	
 	
 <h4>Other changes</h4>
 <ul>
 <li>Updated to HTTPClient 3.0 (from 2.0)</li>
 <li>Updated to Commons Collections 3.1</li>
 <li>Improved formatting of Request Data in Tree View</li>
 <li>Expanded user documentation</li>
 <li>Added MANIFEST, NOTICE and LICENSE to all jars</li>
 <li>Extract htmlparser interface into separate jarfile to make it possible to replace the parser</li>
 <li>Removed SQL Config GUI as no longer needed (or working!)</li>
 <li>HTTPSampler no longer logs a warning for Page not found (404)</li>
 <li>StringFromFile now callable as __StringFromFile (as well as _StringFromFile)</li>
 <li>Updated to Commons Logging 1.1</li>
 </ul>
 
 <!--  ===================  -->
 
 
 <hr/>
 <h3>Version 2.1.1</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>New Include Controller allows a test plan to reference an external jmx file</li>
 <li>New JUnitSampler added for using JUnit Test classes</li>
 <li>New Aggregate Graph listener is capable of graphing aggregate statistics</li>
 <li>Can provide additional classpath entries using the property user.classpath and on the Test Plan element</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>AccessLog Sampler and JDBC test elements populated correctly from 2.0 test plans</li>
 <li>BSF Sampler now populates filename and parameters from saved test plan</li>
 <li>Bug 36500 - handle missing data more gracefully in WebServiceSampler</li>
 <li>Bug 35546 - add merge to right-click menu</li>
 <li>Bug 36642 - Summariser stopped working in 2.1</li>
 <li>Bug 36618 - CSV header line did not match saved data</li>
 <li>JMeter should now run under JVM 1.3 (but does not build with 1.3)</li>
 </ul>	
 	
 
 <!--  ===================  -->
 
 <h3>Version 2.1</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>New Test Script file format - smaller, more compact, more readable</li>
 <li>New Sample Result file format - smaller, more compact</li>
 <li>XSchema Assertion</li>
 <li>XML Tree display</li>
 <li>CSV DataSet Config item</li>
 <li>New JDBC Connection Pool Config Element</li>
 <li>Synchronisation Timer</li>
 <li>setProperty function</li>
 <li>Save response data on error</li>
 <li>Ant JMeter XSLT now optionally shows failed responses and has internal links</li>
 <li>Allow JavaScript variable name to be omitted</li>
 <li>Changed following Samplers to set sample label from sampler name</li>
 <li>All Test elements can be saved as a graphics image to a file</li>
 <li>Bug 35026 - add RE pattern matching to Proxy</li>
 <li>Bug 34739 - Enhance constant Throughput timer</li>
 <li>Bug 25052 - use response encoding to create comparison string in Response Assertion</li>
 <li>New optional icons</li>
 <li>Allow icons to be defined via property files</li>
 <li>New stylesheets for 2.1 format XML test output</li>
 <li>Save samplers, config element and listeners as PNG</li>
 <li>Enhanced support for WSDL processing</li>
 <li>New JMS sampler for topic and queue messages</li>
 <li>How-to for JMS samplers</li>
 <li>Bug 35525 - Added Spanish localisation</li>
 <li>Bug 30379 - allow server.rmi.port to be overridden</li>
 <li>enhanced the monitor listener to save the calculated stats</li>
 <li>Functions and variables now work at top level of test plan</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>Bug 34586 - XPath always remained as /</li>
 <li>BeanShellInterpreter did not handle null objects properly</li>
 <li>Fix Chinese resource bundle names</li>
 <li>Save field names if required to CSV files</li>
 <li>Ensure XML file is closed</li>
 <li>Correct icons now displayed for TestBean components</li>
 <li>Allow for missing optional jar(s) in creating menus</li>
 <li>Changed Samplers to set sample label from sampler name as was the case for HTTP</li>
 <li>Fix various samplers to avoid NPEs when incomplete data is provided</li>
 <li>Fix Cookie Manager to use seconds; add debug</li>
 <li>Bug 35067 - set up filename when using -t option</li>
 <li>Don't substitute TestElement.* properties by UDVs in Proxy</li>
 <li>Bug 35065 - don't save old extensions in File Saver</li>
 <li>Bug 25413 - don't enable Restart button unnecessarily</li>
 <li>Bug 35059 - Runtime Controller stopped working</li>
 <li>Clear up any left-over connections created by LDAP Extended Sampler</li>
 <li>Bug 23248 - module controller didn't remember stuff between save and reload</li>
 <li>Fix Chinese locales</li>
 <li>Bug 29920 - change default locale if necessary to ensure default properties are picked up when English is selected.</li>
 <li>Bug fixes for Tomcat monitor captions</li> 
 <li>Fixed webservice sampler so it works with user defined variables</li>
 <li>Fixed screen borders for LDAP config GUI elements</li>
 <li>Bug 31184 - make sure encoding is specified in JDBC sampler</li>
 <li>TCP sampler - only share sockets with same host:port details; correct the manual</li>
 <li>Extract src attribute for embed tags in JTidy and Html Parsers</li>
 </ul>	
 
 <!--  ===================  -->
 
 <h3>Version 2.0.3</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>XPath Assertion and XPath Function</li>
 <li>Switch Controller</li>
 <li>ForEach Controller can now loop through sets of groups</li>
 <li>Allow CSVRead delimiter to be changed (see jmeter.properties)</li>
 <li>Bug 33920 - allow additional property files</li>
 <li>Bug 33845 - allow direct override of Home dir</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>Regex Extractor nested constant not put in correct place (32395)</li>
 <li>Start time reset to now if necessary so that delay works OK.</li>
 <li>Missing start/end times in scheduler are assumed to be now, not 1970</li>
 <li>Bug 28661 - 304 responses not appearing in listeners</li>
 <li>DOS scripts now handle different disks better</li>
 <li>Bug 32345 - HTTP Rewriter does not work with HTTP Request default</li>
 <li>Catch Runtime Exceptions so an error in one Listener does not affect others</li>
 <li>Bug 33467 - __threadNum() extracted number wrongly </li>
 <li>Bug 29186,33299 - fix CLI parsing of "-" in second argument</li>
 <li>Fix CLI parse bug: -D arg1=arg2. Log more startup parameters.</li>
 <li>Fix JTidy and HTMLParser parsers to handle form src= and link rel=stylesheet</li>
 <li>JMeterThread now logs Errors to jmeter.log which were appearing on console</li>
 <li>Ensure WhileController condition is dynamically checked</li>
 <li>Bug 32790 ensure If Controller condition is re-evaluated each time</li>
 <li>Bug 30266 - document how to display proxy recording responses</li>
 <li>Bug 33921 - merge should not change file name</li>
 <li>Close file now gives chance to save changes</li>
 <li>Bug 33559 - fixes to Runtime Controller</li>
 </ul>
 <h4>Other changes:</h4>
 <ul>
 <li>To help with variable evaluation, JMeterThread sets "sampling started" a bit earlier (see jmeter.properties)</li>
 <li>Bug 33796 - delete cookies with null/empty values</li>
 <li>Better checking of parameter count in JavaScript function</li>
 <li>Thread Group now defaults to 1 loop instead of forever</li>
 <li>All Beanshell access is now via a single class; only need BSH jar at run-time</li>
 <li>Bug 32464 - document Direct Draw settings in jmeter.bat</li>
 <li>Bug 33919 - increase Counter field sizes</li>
 <li>Bug 32252 - ForEach was not initialising counters</li>
 </ul>
 
 <!--  ===================  -->
 
 <h3>Version 2.0.2</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>While Controller</li>
 <li>BeanShell intilisation scripts</li>
 <li>Result Saver can optionally save failed results only</li>
 <li>Display as HTML has option not to download frames and images etc</li>
 <li>Multiple Tree elements can now be enabled/disabled/copied/pasted at once</li>
 <li>__split() function added</li>
 <li>(28699) allow Assertion to regard unsuccessful responses - e.g. 404 - as successful</li>
 <li>(29075) Regex Extractor can now extract data out of http response header as well as the body</li>
 <li>__log() functions can now write to stdout and stderr</li>
 <li>URL Modifier can now optionally ignore query parameters</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>If controller now works after the first false condition (31390)</li>
 <li>Regex GUI was losing track of Header/Body checkbox (29853)</li>
 <li>Display as HTML now handles frames and relative images</li>
 <li>Right-click open replaced by merge</li>
 <li>Fix some drag and drop problems</li>
 <li>Fixed foreach demo example so it works</li>
 <li>(30741) SSL password prompt now works again </li>
 <li>StringFromFile now closes files at end of test; start and end now optional as intended</li>
 <li>(31342) Fixed text of SOAP Sampler headers</li>
 <li>Proxy must now be stopped before it can be removed (25145)</li>
 <li>Link Parser now supports BASE href (25490)</li>
 <li>(30917) Classfinder ignores duplicate names</li>
 <li>(22820) Allow Counter value to be cleared</li>
 <li>(28230) Fix NPE in HTTP Sampler retrieving embedded resources</li>
 <li>Improve handling of StopTest; catch and log some more errors</li>
 <li>ForEach Controller no longer runs any samples if first variable is not defined</li>
 <li>(28663) NPE in remote JDBC execution</li>
 <li>(30110) Deadlock in stopTest processing</li>
 <li>(31696) Duration not working correctly when using Scheduler</li>
 <li>JMeterContext now uses ThreadLocal - should fix some potential NPE errors</li>
 </ul>
 <h3>Version 2.0.1</h3>
 <p>Bug fix release. TBA.</p>
 <h3>Version 2.0</h3>
 <ul>
 	<li>HTML parsing improved; now has choice of 3 parsers, and most embedded elements can now be detected and downloaded.</li>
 <li>Redirects can now be delegated to URLConnection by defining the JMeter property HTTPSamper.delegateRedirects=true (default is false) </li>
 <li>Stop Thread and Stop Test methods added for Samplers and Assertions etc. Samplers can call setStopThread(true) or setStopTest(true) if they detect an error that needs to stop the thread of the test after the sample has been processed </li>
 <li>Thread Group Gui now has an extra pane to specify what happens after a Sampler error: Continue (as now), Stop Thread or Stop Test. 
     This needs to be extended to a lower level at some stage. </li>
 <li>Added Shutdown to Run Menu. This is the same as Stop except that it lets the Threads finish normally (i.e. after the next sample has been completed) </li>
 <li>Remote samples can be cached until the end of a test by defining the property hold_samples=true when running the server.
 More work is needed to be able to control this from the GUI </li>
 <li>Proxy server has option to skip recording browser headers </li>
 <li>Proxy restart works better (stop waits for daemon to finish) </li>
 <li>Scheduler ignores start if it has already passed </li>
 <li>Scheduler now has delay function </li>
 <li>added Summariser test element (mainly for non-GUI) testing. This prints summary statistics to System.out and/or the log file every so oftem (3 minutes by default). Multiple summarisers can be used; samples are accumulated by summariser name. </li>
 <li>Extra Proxy Server options: 
 Create all samplers with keep-alive disabled 
 Add Separator markers between sets of samples 
 Add Response Assertion to first sampler in each set </li>
 <li>Test Plan has a comment field</li>
 	
 	<li>Help Page can now be pushed to background</li>
 	<li>Separate Function help page</li>
 	<li>New / amended functions</li>
 	<ul>
 	  <li>__property() and __P() functions</li>
 	  <li>__log() and __logn() - for writing to the log file</li>
       <li>_StringFromFile can now process a sequence of files, e.g. dir/file01.txt, dir/file02.txt etc </li>
       <li>_StringFromFile() funtion can now use a variable or function for the file name </li>
 	</ul>
 	<li>New / amended Assertions</li>
 	<ul>
         <li>Response Assertion now works for URLs, and it handles null data better </li>
         <li>Response Assertion can now match on Response Code and Response message as well </li>
 		<li>HTML Assertion using JTidy to check for well-formed HTML</li>
 	</ul>
 	<li>If Controller (not fully functional yet)</li>
 	<li>Transaction Controller (aggregates the times of its children)</li>
 	<li>New Samplers</li>
 		<ul>
 			<li>Basic BSF Sampler (optional)</li>
 			<li>BeanShell Sampler (optional, needs to be downloaded from www.beanshell.org</li>
 			<li>Basic TCP Sampler</li>
 		</ul>
      <li>Optionally start BeanShell server (allows remote access to JMeter variables and methods) </li>
 </ul>
 <h3>Version 1.9.1</h3>
 <p>TBA</p>
 <h3>Version 1.9</h3>
 <ul>
 <li>Sample result log files can now be in CSV or XML format</li>
 <li>New Event model for notification of iteration events during test plan run</li>
 <li>New Javascript function for executing arbitrary javascript statements</li>
 <li>Many GUI improvements</li>
 <li>New Pre-processors and Post-processors replace Modifiers and Response-Based Modifiers. </li>
 <li>Compatible with jdk1.3</li>
 <li>JMeter functions are now fully recursive and universal (can use functions as parameters to functions)</li>
 <li>Integrated help window now supports hypertext links</li>
 <li>New Random Function</li>
 <li>New XML Assertion</li>
 <li>New LDAP Sampler (alpha code)</li>
 <li>New Ant Task to run JMeter (in extras folder)</li>
 <li>New Java Sampler test implementation (to assist developers)</li>
 <li>More efficient use of memory, faster loading of .jmx files</li>
 <li>New SOAP Sampler (alpha code)</li>
 <li>New Median calculation in Graph Results visualizer</li>
 <li>Default config element added for developer benefit</li>
 <li>Various performance enhancements during test run</li>
 <li>New Simple File recorder for minimal GUI overhead during test run</li>
 <li>New Function: StringFromFile - grabs values from a file</li>
 <li>New Function: CSVRead - grabs multiple values from a file</li>
 <li>Functions now longer need to be encoded - special values should be escaped 
 with "\" if they are literal values</li>
 <li>New cut/copy/paste functionality</li>
 <li>SSL testing should work with less user-fudging, and in non-gui mode</li>
 <li>Mailer Model works in non-gui mode</li>
 <li>New Througput Controller</li>
 <li>New Module Controller</li>
 <li>Tests can now be scheduled to run from a certain time till a certain time</li>
 <li>Remote JMeter servers can be started from a non-gui client.  Also, in gui mode, all remote servers can be started with a single click</li>
 <li>ThreadGroups can now be run either serially or in parallel (default)</li>
 <li>New command line options to override properties</li>
 <li>New Size Assertion</li>
 
 </ul>
 
 <h3>Version 1.8.1</h3>
 <ul>
 <li>Bug Fix Release.  Many bugs were fixed.</li>
 <li>Removed redundant "Root" node from test tree.</li>
 <li>Re-introduced Icons in test tree.</li>
