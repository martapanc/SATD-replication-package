File path: src/main/org/apache/tools/ant/taskdefs/optional/starteam/StarTeamCheckout.java
Comment: Just a note: StarTeam has a status for NEW which implies
Initial commit id: 63b2c1c4
Final commit id: 54bac0e8
   Bugs between [       3]:
ef415543d PR35852: Committed changes provided by Benjamin Burgess
509ae3e1f Fix a NPE that occurred apparently because Folder.list() can return null Thanks to Bob Evans PR: 31965
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       0]:


Start block index: 114
End block index: 180
    protected void visit(Folder starteamFolder, java.io.File targetFolder ) 
	throws BuildException 
    {
	try {
	    Hashtable localFiles = getLocalFiles(targetFolder);

	    // If we have been told to create the working folders
	    if (createDirs) {
		// Create if it doesn't exist
		if (!targetFolder.exists()) {
		    targetFolder.mkdir();
		}
	    }
	    // For all Files in this folder, we need to check 
	    // if there have been modifications.

	    Item[] files = starteamFolder.getItems("File");
	    for (int i = 0; i < files.length; i++) {
		File eachFile = (File) files[i];
		String filename = eachFile.getName();
		java.io.File localFile = new java.io.File(targetFolder, filename);
		localFiles.remove(localFile.toString());
		
		int fileStatus = (eachFile.getStatus());

		// We try to update the status once to give StarTeam another chance.
		if (fileStatus == Status.MERGE || fileStatus == Status.UNKNOWN) {
		    eachFile.updateStatus(true, true);
		}

		// If the file is current then skip it.
		// If the file doesn't pass the include/exclude tests, skip it. 
		if (fileStatus == Status.CURRENT || !shouldProcess(filename)) {
		    continue;
		}

		// Check out anything else.
		// Just a note: StarTeam has a status for NEW which implies that there 
		// is an item  on your local machine that is not in the repository. 
		// These are the items that show up as NOT IN VIEW in the Starteam GUI.
		// One would think that we would want to perhaps checkin the NEW items
		// (not in all cases! - Steve Cohen 15 Dec 2001)
		// Unfortunately, the sdk doesn't really work, and we can't actually see
		// anything with a status of NEW. That is why we can just check out 
		// everything here without worrying about losing anything.

		log("Checking Out: " + (localFile.toString()), Project.MSG_INFO);
		eachFile.checkoutTo(localFile, Item.LockType.
				    UNCHANGED, true, true, true);
	    }

	    // Now we recursively call this method on all sub folders in this folder.
	    Folder[] subFolders = starteamFolder.getSubFolders();
	    for (int i = 0; i < subFolders.length; i++) {
		localFiles.remove(subFolders[i].getPath());
		visit(subFolders[i], 
		      new java.io.File(targetFolder, subFolders[i].getName()));
	    }

	    // Delete all folders or files that are not in Starteam.
	    if (this.deleteUncontrolled && !localFiles.isEmpty()) {
		delete(localFiles);
	    }
	} catch (IOException e) {
	    throw new BuildException(e);
	}
    }
