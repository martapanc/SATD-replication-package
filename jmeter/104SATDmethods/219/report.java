File path: src/protocol/java/org/apache/jmeter/protocol/java/control/gui/BSFSamplerGui.java
Comment: TODO ought to be a FileChooser ...
Initial commit id: 76159a5b
Final commit id: 7576a848
   Bugs between [       4]:
7576a8487 Bug 52048 - BSFSampler, BSFPreProcessor and BSFPostProcessor should share the same GUI
86a21b46f Bug 52048 - BSFSampler, BSFPreProcessor and BSFPostProcessor should share the same GUI
6e7cecae4 Bug 52048 - BSFSampler, BSFPreProcessor and BSFPostProcessor should share the same GUI
0f7f880de Bug 40077 - Creating new Elements copies values from Existing elements Applied patch 20129
   Bugs after [       0]:


Start block index: 113
End block index: 125
	private JPanel createFilenamePanel()// TODO ought to be a FileChooser ...
	{
		JLabel label = new JLabel(JMeterUtils.getResString("bsf_script_file"));

		filename = new JTextField(10);
		filename.setName(BSFSampler.FILENAME);
		label.setLabelFor(filename);

		JPanel filenamePanel = new JPanel(new BorderLayout(5, 0));
		filenamePanel.add(label, BorderLayout.WEST);
		filenamePanel.add(filename, BorderLayout.CENTER);
		return filenamePanel;
	}
