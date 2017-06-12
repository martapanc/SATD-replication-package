File path: src/main/org/apache/tools/ant/taskdefs/Zip.java
Comment: ne of our subclasses knows that we need to update the
Initial commit id: 8d651ad9
Final commit id: b6422c58
   Bugs between [      24]:
ee4aa0108 zip's whenempty doesn't look at non-filesets at all.  PR 50115
ba12ac85f better use the full potential of a Hashtable.  PR 48755.  Submitted by Marc Bevand.
dd92def3a try to make ZipExtraField change in zip backwards compatible for subclasses that override zipFile.  PR 48541
f2d99c202 create parent directory of archive in <zip> and <tar> if needed.  PR 45377.  Based on patch by Remie Bolte
ee0fc90c1 make sure log messages to get emmitted twice in double-pass mode.  PR 39426.
8aaa00583 close streams passed in to zipFile(InputStream, ...).  PR 42632.
549fc1f68 don't trust permissions read from another ZIP file blindly.  PR 42122.
0df2b1de3 Minor updates based on the input of Dave Brosius pr: 39320
32f2e37a9 JDK 1.2 is EOL and documentation is no more available. Point to JDK 5 API PR: 37203
de95ac9a0 Make Solaris detect Ant jar files as jar files, PR: 32649
ab3f80589 NPE when adding comment to zip file that is not there PR: 33779
a18bd3310 On second thought, this seems to be the real fix for PR: 33412
782e45115 <zip> update ignored defaultexcludes, PR 33412
1225a5a84 Added level attribute to zip & subclasses. PR: 25513
8d72468ce Add support of zip comment. PR: 22793 Obtained from: Larry Shatzer
6e7294106 Make it possible to create manifest only jars with the option duplicate="preserve" PR: 32802
54d0543e1 prevent empty archive in zip if whenempty is set to skip, PR: 22865
1efa389be Push fix for PR#17934 to 1.6.2
b56144731 Don't delete existing file when trying to update a read-only archive, PR: 28419
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
5dc8a1418 add filename to errormessage if unable to rename file in zip task PR: 24945 Obtained from: Bart Vanhaute
6b62a56cc More robust cleanup of temporary files, PR 17512
9434671ef removing enum variable PR: 22345 Obtained from: Submitted by: Reviewed by:
02305cfec Improve fix for PR: 19449.
   Bugs after [       4]:
234c1d9ff weed out race-condition in mkdirs calls inspired by PR 55290 and Matthias Bhend's suggestion
ef4c94df7 Provide more control over Zip64 features, in particilar turn them off in <jar> by default - this might fix bugzilla issue 54762
0282d8fe2 PR 54026 Zip task on <mappedresources> that excludes certain files by way of the mapper results in a NullPointerException
5f20b9914 microoptimizations.  PR 50716

Start block index: 700
End block index: 829
    protected ArchiveState getResourcesToAdd(FileSet[] filesets,
                                             File zipFile,
                                             boolean needsUpdate)
        throws BuildException {

        Resource[][] initialResources = grabResources(filesets);
        if (isEmpty(initialResources)) {
            if (needsUpdate && doUpdate) {
                /*
                 * This is a rather hairy case.
                 *
                 * One of our subclasses knows that we need to update the
                 * archive, but at the same time, there are no resources
                 * known to us that would need to be added.  Only the
                 * subclass seems to know what's going on.
                 *
                 * This happens if <jar> detects that the manifest has changed,
                 * for example.  The manifest is not part of any resources
                 * because of our support for inline <manifest>s.
                 *
                 * If we invoke createEmptyZip like Ant 1.5.2 did,
                 * we'll loose all stuff that has been in the original
                 * archive (bugzilla report 17780).
                 */
                return new ArchiveState(true, initialResources);
            }

            if (emptyBehavior.equals("skip")) {
                if (doUpdate) {
                    log(archiveType + " archive " + zipFile 
                        + " not updated because no new files were included.", 
                        Project.MSG_VERBOSE);
                } else {
                    log("Warning: skipping " + archiveType + " archive " 
                        + zipFile + " because no files were included.", 
                        Project.MSG_WARN);
                }
            } else if (emptyBehavior.equals("fail")) {
                throw new BuildException("Cannot create " + archiveType
                                         + " archive " + zipFile +
                                         ": no files were included.", 
                                         getLocation());
            } else {
                // Create.
                createEmptyZip(zipFile);
            }
            return new ArchiveState(needsUpdate, initialResources);
        }

        // initialResources is not empty

        if (!zipFile.exists()) {
            return new ArchiveState(true, initialResources);
        }

        if (needsUpdate && !doUpdate) {
            // we are recreating the archive, need all resources
            return new ArchiveState(true, initialResources);
        }

        Resource[][] newerResources = new Resource[filesets.length][];

        for (int i = 0; i < filesets.length; i++) {
            if (!(fileset instanceof ZipFileSet) 
                || ((ZipFileSet) fileset).getSrc() == null) {
                File base = filesets[i].getDir(getProject());
            
                for (int j = 0; j < initialResources[i].length; j++) {
                    File resourceAsFile = 
                        fileUtils.resolveFile(base, 
                                              initialResources[i][j].getName());
                    if (resourceAsFile.equals(zipFile)) {
                        throw new BuildException("A zip file cannot include "
                                                 + "itself", getLocation());
                    }
                }
            }
        }

        for (int i = 0; i < filesets.length; i++) {
            if (initialResources[i].length == 0) {
                newerResources[i] = new Resource[] {};
                continue;
            }
            
            FileNameMapper myMapper = new IdentityMapper();
            if (filesets[i] instanceof ZipFileSet) {
                ZipFileSet zfs = (ZipFileSet) filesets[i];
                if (zfs.getFullpath() != null
                    && !zfs.getFullpath().equals("") ) {
                    // in this case all files from origin map to
                    // the fullPath attribute of the zipfileset at
                    // destination
                    MergingMapper fm = new MergingMapper();
                    fm.setTo(zfs.getFullpath());
                    myMapper = fm;

                } else if (zfs.getPrefix() != null 
                           && !zfs.getPrefix().equals("")) {
                    GlobPatternMapper gm=new GlobPatternMapper();
                    gm.setFrom("*");
                    String prefix = zfs.getPrefix();
                    if (!prefix.endsWith("/") && !prefix.endsWith("\\")) {
                        prefix += "/";
                    }
                    gm.setTo(prefix + "*");
                    myMapper = gm;
                }
            }
            newerResources[i] = 
                ResourceUtils.selectOutOfDateSources(this,
                                                     initialResources[i],
                                                     myMapper,
                                                     getZipScanner());
            needsUpdate = needsUpdate || (newerResources[i].length > 0);

            if (needsUpdate && !doUpdate) {
                // we will return initialResources anyway, no reason
                // to scan further.
                break;
            }
        }

        if (needsUpdate && !doUpdate) {
            // we are recreating the archive, need all resources
            return new ArchiveState(true, initialResources);
        }
        
        return new ArchiveState(needsUpdate, newerResources);
    }
