File path: src/main/org/apache/tools/ant/DirectoryScanner.java
Comment: wo reasons are mentioned in the API docs for File.list
Initial commit id: e2a75898
Final commit id: cafa34ca
   Bugs between [       9]:
2fbb2e62a Not 100% sure this fixes the entire bug, but it eliminates NPEs for me. PR: 34722
0777fa853 Eliminate possible NPE; note that previous change addressed bug 33118 at least partially.
28d39b09a Allow <sync> to keep files in target even if they are not in any source directories, PR 21832
0e2cfd51d <apply> differentiating between empty and up to date broke <classfileset>s. PR: 30567
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
cc9f4f71f Be a little more defensive in a protected method of a non-final public class, PR 26737
9a7f8d24f Optimize scanning in FTP.FTPDirectoryScanner, using similar algorithms to the ones introduced in DirectoryScanner. There is a gain when - the include patterns look like some/very/long/path - the remote file system is case sensitive - the casesensitive and followsymlinks options of the fileset are set to true (the default) PR: 20103
6ff7df960 do not scan needlessly excluded directories PR: 21941
787728897 Optimize DirectoryScanner to take advantage of include patterns which are not beginning with wildcards PR: 20103
   Bugs after [       9]:
bea36bbd2 don't assume File#isDirectory == !File#isFile - i.e. explicitly test and drop objects that are neither files nor directories.  PR 56149
e46fd219c removeNotFollowedSymlinks='true' might be deleting too eagerly.  PR 53959
379895a02 synchronize access to default excludes.  PR 52188.
d85d2da8e restrict impact of fix for PR 50295 on 'good' VMs to an additional syscall for empty directories rather than an additional call for every directory
17d4b3461 Make DirectoryScanner work on buggy JVMs where File.list() returns an empty array rather than null for files.  PR 50295.  Submitted by Daniel Smith
43de42a38 add Git, Mercurial and Bazaar files/dirs to defaultexcludes.  Submitted by Ville Skyttä.  PR 49624
6a87b53fc don't scan directories that are excluded recursively - this used to be done everywhere except for the (most common) case where the directory itself was not explicitly included.  PR 49420
1d3ca73f3 don't run into infinite lopps caused by symbolic links.  PR 45499.
446436982 honor followsymlinks on a fileset's dir as well.  PR 45741.

Start block index: 727
End block index: 776
    private void scandir(File dir, String vpath, boolean fast) {
        String[] newfiles = dir.list();

        if (newfiles == null) {
            /*
             * two reasons are mentioned in the API docs for File.list
             * (1) dir is not a directory. This is impossible as
             *     we wouldn't get here in this case.
             * (2) an IO error occurred (why doesn't it throw an exception 
             *     then???)
             */
            throw new BuildException("IO error scanning directory"
                                     + dir.getAbsolutePath());
        }

        for (int i = 0; i < newfiles.length; i++) {
            String name = vpath+newfiles[i];
            File   file = new File(dir,newfiles[i]);
            if (file.isDirectory()) {
                if (isIncluded(name)) {
                    if (!isExcluded(name)) {
                        dirsIncluded.addElement(name);
                        if (fast) {
                            scandir(file, name+File.separator, fast);
                        }
                    } else {
                        dirsExcluded.addElement(name);
                    }
                } else {
                    dirsNotIncluded.addElement(name);
                    if (fast && couldHoldIncluded(name)) {
                        scandir(file, name+File.separator, fast);
                    }
                }
                if (!fast) {
                    scandir(file, name+File.separator, fast);
                }
            } else if (file.isFile()) {
                if (isIncluded(name)) {
                    if (!isExcluded(name)) {
                        filesIncluded.addElement(name);
                    } else {
                        filesExcluded.addElement(name);
                    }
                } else {
                    filesNotIncluded.addElement(name);
                }
            }
        }
    }
