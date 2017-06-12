File path: src/main/org/apache/tools/ant/taskdefs/optional/scm/AntStarTeamCheckOut.java
Comment: Check it out; also ugly.
Initial commit id: dc7444ae
Final commit id: 54bac0e8
   Bugs between [       1]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       0]:


Start block index: 541
End block index: 691
	protected void runItem(Server s, com.starbase.starteam.Project p, View v, Type t, Folder f, Item item)
	{

		// Get descriptors for this item type.
		Property p1 = getPrimaryDescriptor(t);
		Property p2 = getSecondaryDescriptor(t);

		// Time to filter...
		String pName = (String)item.get(p1.getName());
		boolean includeIt = false;
		boolean excludeIt = false;

		// See if it fits any includes.
		if (getIncludes()!=null)
		{
			StringTokenizer inStr = new StringTokenizer(getIncludes(), " ");
			while (inStr.hasMoreTokens())
			{
				if (match(inStr.nextToken(), pName))
				{
					includeIt = true;
				}
			}
		}

		// See if it fits any excludes.
		if (getExcludes()!=null)
		{
			StringTokenizer exStr = new StringTokenizer(getExcludes(), " ");
			while (exStr.hasMoreTokens())
			{
				if (match(exStr.nextToken(), pName))
				{
					excludeIt = true;
				}
			}
		}

		// Don't check it out if
		// (a) It fits no include filters
		// (b) It fits an exclude filter
		if (!includeIt | excludeIt)
		{
			return;
		}

		// VERBOSE MODE ONLY
		if (getVerboseAsBoolean())
		{
			// Show folder only if changed.
			boolean bShowHeader = true;
			if (f != prevFolder)
			{
				// We want to display the folder the same way you would
				// enter it on the command line ... so we remove the 
				// View name (which is also the name of the root folder,
				// and therefore shows up at the start of the path).
				String strFolder = f.getFolderHierarchy();
				int i = strFolder.indexOf(delim);
				if (i >= 0)
				{
					strFolder = strFolder.substring(i+1);
				}
				System.out.println("            Folder: \"" + strFolder + "\"");
				prevFolder = f;
			}
			else
				bShowHeader	= false;

			// If we displayed the project, view, item type, or folder,
			// then show the list of relevant item properties.
			if (bShowHeader)
			{
				System.out.print("                Item");
				System.out.print(",\t" + p1.getDisplayName());
				if (p2 != null)
				{
					System.out.print(",\t" + p2.getDisplayName());
				}
				System.out.println("");
			}

			// Finally, show the Item properties ...

			// Always show the ItemID.
			System.out.print("                " + item.getItemID());

			// Show the primary descriptor.
			// There should always be one.
			System.out.print(",\t" + formatForDisplay(p1, item.get(p1.getName())));

			// Show the secondary descriptor, if there is one.
			// Some item types have one, some don't.
			if (p2 != null)
			{
				System.out.print(",\t" + formatForDisplay(p2, item.get(p2.getName())));
			}

			// Show if the file is locked.
			int locker = item.getLocker();
			if (locker>-1)
			{
				System.out.println(",\tLocked by " + locker);
			}
			else
			{
				System.out.println(",\tNot locked");
			}
		}
		// END VERBOSE ONLY

		// Check it out; also ugly.

		// Change the item to be checked out to a StarTeam File.
		com.starbase.starteam.File remote = (com.starbase.starteam.File)item;

		// Create a variable dirName that contains the name of the StarTeam folder that is the root folder in this view.
		// Get the default path to the current view.
		String dirName = v.getDefaultPath();
		// Settle on "/" as the default path separator for this purpose only.
		dirName = dirName.replace('\\', '/');
		// Take the StarTeam folder name furthest down in the hierarchy.
		dirName = dirName.substring(dirName.lastIndexOf("/", dirName.length() - 2) + 1, dirName.length() - 1);

		// Replace the projectName in the file's absolute path to the viewName.
		// This eventually makes the target of a checkout operation equal to:
		// targetFolder + dirName + [subfolders] + itemName
		StringTokenizer pathTokenizer = new StringTokenizer(item.getParentFolder().getFolderHierarchy(), delim);
		String localName = delim;
		String currentToken = null;
		while (pathTokenizer.hasMoreTokens())
		{
			currentToken = pathTokenizer.nextToken();
			if (currentToken.equals(getProjectName()))
			{
				currentToken = dirName;
			}
			localName += currentToken + delim;
		}
		// Create a reference to the local target file using the format listed above.
		java.io.File local = new java.io.File(getTargetFolder() + localName + item.get(p1.getName()));
		try
		{
			remote.checkoutTo(local, Item.LockType.UNCHANGED, false, true, true);
		}
		catch (Throwable e)
		{
			project.log("    " + e.getMessage());
		}
		checkedOut++;
	}
