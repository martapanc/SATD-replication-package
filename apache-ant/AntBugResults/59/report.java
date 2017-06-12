File path: src/testcases/org/apache/tools/ant/taskdefs/StyleTest.java
Comment: ied from ConcatTest
Initial commit id: 5d847c58
Final commit id: b5057787
   Bugs between [       5]:
f39da1b5b unit test for PR 25911
a1634b420 Unit test for PR 24866
6dbabcb77 Add nested mappers to xslt, PR: 11249
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
1a5276fbb finish off the bug report of Martijn. Note that the diffs of StyleTest.java is big, the file contained control M characters at the end of each line PR: 22549 Submitted by: J.M. (Martijn) Kruithof ant at kruithof dot xs4all dot nl
   Bugs after [       0]:


Start block index: 115
End block index: 133
    // *************  copied from ConcatTest  *************

    // ------------------------------------------------------
    //   Helper methods - should be in BuildFileTest
    // -----------------------------------------------------

    private String getFileString(String filename)
        throws IOException
    {
        Reader r = null;
        try {
            r = new FileReader(getProject().resolveFile(filename));
            return  FileUtils.newFileUtils().readFully(r);
        }
        finally {
            try {r.close();} catch (Throwable ignore) {}
        }

    }
