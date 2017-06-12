File path: src/core/org/apache/jmeter/reporters/gui/ResultSaverGui.java
Comment: TODO ought to be a FileChooser ...
Initial commit id: 76159a5b
Final commit id: ac5961b3
   Bugs between [       2]:
59671c56f Bug 44575 - Result Saver can now save only successful results [Oops - should have been included in r639127]
0f7f880de Bug 40077 - Creating new Elements copies values from Existing elements Applied patch 20129
   Bugs after [       2]:
3e16150b7 Bug 52214 - Save Responses to a file - improve naming algorithm - add fixed width numbers - add optional timestamp - fix synchronisation
d81ad7e22 Bug 43119 - Save Responses to file: optionally omit the file number

Start block index: 100
End block index: 112
	private JPanel createFilenamePanel()// TODO ought to be a FileChooser ...
	{
		JLabel label = new JLabel(JMeterUtils.getResString("resultsaver_prefix")); // $NON-NLS-1$

		filename = new JTextField(10);
		filename.setName(ResultSaver.FILENAME);
		label.setLabelFor(filename);

		JPanel filenamePanel = new JPanel(new BorderLayout(5, 0));
		filenamePanel.add(label, BorderLayout.WEST);
		filenamePanel.add(filename, BorderLayout.CENTER);
		return filenamePanel;
	}
