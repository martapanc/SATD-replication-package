private void scandir(File dir, String vpath, boolean fast, String[] newfiles) {
    // avoid double scanning of directories, can only happen in fast mode
    if (fast && hasBeenScanned(vpath)) {
        return;
    }
    if (!followSymlinks) {
        Vector noLinks = new Vector();
        for (int i = 0; i < newfiles.length; i++) {
            try {
                if (FILE_UTILS.isSymbolicLink(dir, newfiles[i])) {
                    String name = vpath + newfiles[i];
                    File file = new File(dir, newfiles[i]);
                    (file.isDirectory()
                        ? dirsExcluded : filesExcluded).addElement(name);
                } else {
                    noLinks.addElement(newfiles[i]);
                }
            } catch (IOException ioe) {
                String msg = "IOException caught while checking "
                    + "for links, couldn't get canonical path!";
                // will be caught and redirected to Ant's logging system
                System.err.println(msg);
                noLinks.addElement(newfiles[i]);
            }
        }
        newfiles = (String[]) (noLinks.toArray(new String[noLinks.size()]));
    }
    for (int i = 0; i < newfiles.length; i++) {
        String name = vpath + newfiles[i];
        File file = new File(dir, newfiles[i]);
        String[] children = file.list();
        if (children == null) { // probably file
            if (isIncluded(name)) {
                accountForIncludedFile(name, file);
            } else {
                everythingIncluded = false;
                filesNotIncluded.addElement(name);
            }
        } else { // dir
            if (isIncluded(name)) {
                accountForIncludedDir(name, file, fast, children);
            } else {
                everythingIncluded = false;
                dirsNotIncluded.addElement(name);
                if (fast && couldHoldIncluded(name)) {
                    scandir(file, name + File.separator, fast, children);
                }
            }
            if (!fast) {
                scandir(file, name + File.separator, fast, children);
            }
        }
    }
}
