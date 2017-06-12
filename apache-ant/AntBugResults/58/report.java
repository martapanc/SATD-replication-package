File path: src/testcases/org/apache/tools/ant/taskdefs/CopydirTest.java
Comment: we have something to delete in later tests :-)
Initial commit id: 7c231e50
Final commit id: b5057787
   Bugs between [       1]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       0]:


Start block index: 85
End block index: 92
    public void test5() { 
        executeTarget("test5");
        java.io.File f = new java.io.File("src/etc/testcases/taskdefs.tmp");
        if (!f.exists() || !f.isDirectory()) { 
            fail("Copy failed");
        }
        // We keep this, so we have something to delete in later tests :-)
    }
