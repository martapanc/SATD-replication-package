File path: src/testcases/org/apache/tools/ant/taskdefs/FixCrLfTest.java
Comment:  public so theoretically must remain for BC?
Initial commit id: 0fb6ce69
Final commit id: b5057787
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 177
End block index: 204
    // not used, but public so theoretically must remain for BC?
    public void assertEqualContent(File expect, File result)
        throws AssertionFailedError, IOException {
        if (!result.exists()) {
            fail("Expected file "+result+" doesn\'t exist");
        }

        InputStream inExpect = null;
        InputStream inResult = null;
        try {
            inExpect = new BufferedInputStream(new FileInputStream(expect));
            inResult = new BufferedInputStream(new FileInputStream(result));

            int expectedByte = inExpect.read();
            while (expectedByte != -1) {
                assertEquals(expectedByte, inResult.read());
                expectedByte = inExpect.read();
            }
            assertEquals("End of file", -1, inResult.read());
        } finally {
            if (inResult != null) {
                inResult.close();
            }
            if (inExpect != null) {
                inExpect.close();
            }
        }
    }
