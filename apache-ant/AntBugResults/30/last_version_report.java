    protected ArchiveState getResourcesToAdd(FileSet[] filesets,
                                             File zipFile,
                                             boolean needsUpdate)
        throws BuildException {

        Resource[][] initialResources = grabResources(filesets);
        if (isEmpty(initialResources)) {
            if (Boolean.FALSE.equals(haveNonFileSetResourcesToAdd.get())) {
                if (needsUpdate && doUpdate) {
                    /*
                     * This is a rather hairy case.
                     *
                     * One of our subclasses knows that we need to
                     * update the archive, but at the same time, there
                     * are no resources known to us that would need to
                     * be added.  Only the subclass seems to know
                     * what's going on.
                     *
                     * This happens if <jar> detects that the manifest
                     * has changed, for example.  The manifest is not
                     * part of any resources because of our support
                     * for inline <manifest>s.
                     *
                     * If we invoke createEmptyZip like Ant 1.5.2 did,
                     * we'll loose all stuff that has been in the
                     * original archive (bugzilla report 17780).
                     */
                    return new ArchiveState(true, initialResources);
                }

                if (emptyBehavior.equals("skip")) {
                    if (doUpdate) {
                        logWhenWriting(archiveType + " archive " + zipFile
                                       + " not updated because no new files were"
                                       + " included.", Project.MSG_VERBOSE);
                    } else {
                        logWhenWriting("Warning: skipping " + archiveType
                                       + " archive " + zipFile
                                       + " because no files were included.",
                                       Project.MSG_WARN);
                    }
                } else if (emptyBehavior.equals("fail")) {
                    throw new BuildException("Cannot create " + archiveType
                                             + " archive " + zipFile
                                             + ": no files were included.",
                                             getLocation());
                } else {
                    // Create.
                    if (!zipFile.exists())  {
                        needsUpdate = true;
                    }
                }
            }

            // either we there are non-fileset resources or we
            // (re-)create the archive anyway
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
                || ((ZipFileSet) fileset).getSrc(getProject()) == null) {
                File base = filesets[i].getDir(getProject());

                for (int j = 0; j < initialResources[i].length; j++) {
                    File resourceAsFile =
                        FILE_UTILS.resolveFile(base,
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
                if (zfs.getFullpath(getProject()) != null
                    && !zfs.getFullpath(getProject()).equals("")) {
                    // in this case all files from origin map to
                    // the fullPath attribute of the zipfileset at
                    // destination
                    MergingMapper fm = new MergingMapper();
                    fm.setTo(zfs.getFullpath(getProject()));
                    myMapper = fm;

                } else if (zfs.getPrefix(getProject()) != null
                           && !zfs.getPrefix(getProject()).equals("")) {
                    GlobPatternMapper gm = new GlobPatternMapper();
                    gm.setFrom("*");
                    String prefix = zfs.getPrefix(getProject());
                    if (!prefix.endsWith("/") && !prefix.endsWith("\\")) {
                        prefix += "/";
                    }
                    gm.setTo(prefix + "*");
                    myMapper = gm;
                }
            }

            newerResources[i] = selectOutOfDateResources(initialResources[i],
                                                         myMapper);
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
