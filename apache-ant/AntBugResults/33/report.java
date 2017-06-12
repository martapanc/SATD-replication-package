File path: proposal/myrmidon/src/main/org/apache/tools/ant/taskdefs/JikesOutputParser.java
Comment: we add advanced parsing capabilities.
Initial commit id: d1064dea
Final commit id: 58c82aeb
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 129
End block index: 134
    private void parseEmacsOutput( BufferedReader reader )
        throws IOException
    {
        // This may change, if we add advanced parsing capabilities.
        parseStandardOutput( reader );
    }
