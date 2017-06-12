File path: src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
Comment: I guess this can happen as a result of a bad
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

Start block index: 439
End block index: 465
    public void configure(TestElement element)
    {
        super.configure(element);

		for (int i=0; i<editors.length; i++)
		{
			if (editors[i] == null) continue;
			JMeterProperty jprop= element.getProperty(descriptors[i].getName());
			try
			{
				setEditorValue(i, jprop.getObjectValue());
			}
			catch (IllegalArgumentException e)
			{
				// I guess this can happen as a result of a bad
				// file read? In this case, it would be better to replace the
				// incorrect value with anything valid, e.g. the default value
				// for the property.
				// But for the time being, I just prefer to be aware of any
				// problems occuring here, most likely programming errors,
				// so I'll bail out.
				throw new Error("Bad property value.", e);
				// TODO: review this and possibly change to:
				// setEditorValue(i, descriptors[i].getValue("default");
			}
		}
    }

*********************** Method when SATD was removed **************************

    public void configure(TestElement element)
    {
        if (! initialized) init();

        super.configure(element);

        // Copy all property values into the map:
        propertyMap.clear();
        for (PropertyIterator jprops= element.propertyIterator(); jprops.hasNext(); )
        {
            JMeterProperty jprop= jprops.next();
            propertyMap.put(jprop.getName(), jprop.getObjectValue());
        }

        if (customizer != null)
        {
            customizer.setObject(propertyMap);
        }
        else
        {
            if (initialized) remove(customizerIndexInPanel);
            Customizer c= (Customizer)customizers.get(element);
            if (c == null)
            {
                c= createCustomizer();
                c.setObject(propertyMap);
                customizers.put(element, c);
            }
            add((Component)c, BorderLayout.CENTER);
        }

        initialized= true;
    }
