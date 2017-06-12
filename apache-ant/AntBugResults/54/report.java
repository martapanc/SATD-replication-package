File path: src/testcases/org/apache/tools/ant/taskdefs/optional/net/FTPTest.java
Comment: provide public visibility
Initial commit id: 225b9fd6
Final commit id: b5057787
   Bugs between [       5]:
f2d32ac66 Add initialSiteCommand and siteCommand attributes. PR:34257, 34853
d3f03ad75 Merge from ANT_16_BRANCH Fix problem with non absolute remote dirs PR: 23833
9a7f8d24f Optimize scanning in FTP.FTPDirectoryScanner, using similar algorithms to the ones introduced in DirectoryScanner. There is a gain when - the include patterns look like some/very/long/path - the remote file system is case sensitive - the casesensitive and followsymlinks options of the fileset are set to true (the default) PR: 20103
24f9da26b Issue a warning message indicating that selectors within filesets are not supported in the ftp task when the fileset is remote (all actions except put) PR: 18280
1c3832c2f Added a testcase showing resolution of PR 14063 Ant ftp did not download file it is a symlink PR: 14063
   Bugs after [       0]:


Start block index: 526
End block index: 529
        // provide public visibility
        public String resolveFile(String file) {
            return super.resolveFile(file);
        }
