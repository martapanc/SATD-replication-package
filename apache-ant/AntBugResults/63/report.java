File path: src/main/org/apache/tools/ant/taskdefs/optional/scm/AntStarTeamCheckOut.java
Comment: This is ugly; checking for the root folder.
Initial commit id: dc7444ae
Final commit id: 54bac0e8
   Bugs between [       1]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       0]:


Start block index: 440
End block index: 497
	protected void runType(Server s, com.starbase.starteam.Project p, View v, Type t)
	{

		// This is ugly; checking for the root folder.
		Folder f = v.getRootFolder();
		if (!(getFolderName()==null))
		{
			if (getFolderName().equals("\\") || getFolderName().equals("/"))
			{
				setFolderName(null);
			}
			else
			{
				f = StarTeamFinder.findFolder(v.getRootFolder(), getFolderName());
			}
		}

		if (getVerboseAsBoolean() && !(getFolderName()==null))
		{
			project.log("Found " + getProjectName() + delim + getViewName() + delim + getFolderName() + delim + "\n");
		}

		// For performance reasons, it is important to pre-fetch all the
		// properties we'll need for all the items we'll be searching.

		// We always display the ItemID (OBJECT_ID) and primary descriptor.
		int nProperties = 2;

		// We'll need this item type's primary descriptor.
		Property p1 = getPrimaryDescriptor(t);

		// Does this item type have a secondary descriptor?
		// If so, we'll need it.
		Property p2 = getSecondaryDescriptor(t);
		if (p2 != null)
		{
			nProperties++;
		}

		// Now, build an array of the property names.
		String[] strNames = new String[nProperties];
		int iProperty = 0;
		strNames[iProperty++] = s.getPropertyNames().OBJECT_ID;
		strNames[iProperty++] = p1.getName();
		if (p2 != null)
		{
			strNames[iProperty++] = p2.getName();
		}

		// Pre-fetch the item properties and cache them.
		f.populateNow(t.getName(), strNames, -1);

		// Now, search for items in the selected folder.
		runFolder(s, p, v, t, f);

		// Free up the memory used by the cached items.
		f.discardItems(t.getName(), -1);
	}
