File path: src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
Comment: TODO: the above works in the current situation
Initial commit id: bbe252af
Final commit id: f45aad65
   Bugs between [       0]:

   Bugs after [      10]:
ea090c4eb Bug 60564 - Migrating LogKit to SLF4J - core/samplers,save,services,swing,testbeans (2/2) Contributed by Woonsan Ko This closes #271 Bugzilla Id: 60564
66ee22a99 Bug 52741 - TestBeanGUI default values do not work at second time or later
0d647fff5 Bug 52552 - Help reference only works in English
cde354555 Bug 52279 - Switching to another language loses icons in Tree and logs error Can't obtain GUI class from ... Better fix after discussion on dev mailing list, thanks sebb for your review
9a3c9b818 Bug 52280 - The menu item Options / Choose Language does not change all the displayed text to the new language
a4e473598 Bug 52280 - The menu item Options / Choose Language does not change all the displayed text to the new language Fix the menu categories labels
6237f65f7 Bug 52279 - Switching to another language loses icons in Tree and logs error Can't obtain GUI class from ...
b43c12b5e Bug 52160 - Don't display TestBeanGui items which are flagged as hidden
87951a11f clearGui() now calls clearGuiFields() - see Bug 43332
7c3ae3640 Bug 42947 - TestBeanGUI fields not being updated for short-cut keys

Start block index: 643
End block index: 740
    private void init()
    {
		// TODO: add support for Bean Customizers

        setLayout(new BorderLayout(0, 5));

        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints cl= new GridBagConstraints(); // for labels
		cl.gridx= 0;
		cl.anchor= GridBagConstraints.LINE_END;
		cl.insets= new Insets(0, 1, 0, 1);

		GridBagConstraints ce= new GridBagConstraints(); // for editors
		ce.fill= GridBagConstraints.BOTH;
		ce.gridx= 1;
		ce.weightx= 1.0;
		ce.insets= new Insets(0, 1, 0, 1);
		
		GridBagConstraints cp= new GridBagConstraints(); // for panels
		cp.fill= GridBagConstraints.BOTH;
		cp.gridx= 1;
		cp.gridy= GridBagConstraints.RELATIVE;
		cp.gridwidth= 2;
		cp.weightx= 1.0;

		JPanel currentPanel= mainPanel;
		String currentGroup= "";
		int y=0;
		
        for (int i=0; i<editors.length; i++)
        {
            if (editors[i] == null) continue;

			if (log.isDebugEnabled())
			{
				log.debug("Laying property "+descriptors[i].getName());
			}
			
			String g= group(descriptors[i]);
			if (! currentGroup.equals(g))
			{
				if (currentPanel != mainPanel)
				{
					mainPanel.add(currentPanel, cp);
				}
				currentGroup= g;
				currentPanel= new JPanel(new GridBagLayout()); 
				currentPanel.setBorder(
					BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(),
						groupDisplayName(g)));
				cp.weighty= 0.0;
				y= 0;
			}

			Component customEditor= editors[i].getCustomEditor();

			boolean multiLineEditor= false;
			if (customEditor.getPreferredSize().height > 50)
			{
				// TODO: the above works in the current situation, but it's
				// just a hack. How to get each editor to report whether it
				// wants to grow bigger? Whether the property label should
				// be at the left or at the top of the editor? ...?
				multiLineEditor= true;
			}
			
			JLabel label= createLabel(descriptors[i]);
			label.setLabelFor(customEditor);

			cl.gridy= y;
			cl.gridwidth= multiLineEditor ? 2 : 1;
			cl.anchor= multiLineEditor 
				? GridBagConstraints.CENTER
				: GridBagConstraints.LINE_END;
            currentPanel.add(label, cl);

			ce.gridx= multiLineEditor ? 0 : 1;
			ce.gridy= multiLineEditor ? ++y : y;
			ce.gridwidth= multiLineEditor ? 2 : 1;
			ce.weighty= multiLineEditor ? 1.0 : 0.0;

			cp.weighty+= ce.weighty;

            currentPanel.add(customEditor, ce);

            y++;
        }
		if (currentPanel != mainPanel)
		{
			mainPanel.add(currentPanel, cp);
		}
        add(mainPanel, BorderLayout.CENTER);
    }
