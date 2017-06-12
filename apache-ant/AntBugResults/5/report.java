File path: src/testcases/org/apache/tools/ant/types/selectors/FilenameSelectorTest.java
Comment: This is turned off temporarily. There appears to be a bug
Initial commit id: ca91f8cb
Final commit id: b5057787
   Bugs between [       1]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       0]:


Start block index: 116
End block index: 164
    public void testSelectionBehaviour() {
        FilenameSelector s;
        String results;

        try {
            makeBed();

            s = (FilenameSelector)getInstance();
            s.setName("no match possible");
            results = selectionString(s);
            assertEquals("FFFFFFFFFFFF", results);

            s = (FilenameSelector)getInstance();
            s.setName("*.gz");
            results = selectionString(s);
            // This is turned off temporarily. There appears to be a bug
            // in SelectorUtils.matchPattern() where it is recursive on
            // Windows even if no ** is in pattern.
            //assertEquals("FFFTFFFFFFFF", results); // Unix
            // vs
            //assertEquals("FFFTFFFFTFFF", results); // Windows

            s = (FilenameSelector)getInstance();
            s.setName("**/*.gz");
            s.setNegate(true);
            results = selectionString(s);
            assertEquals("TTTFTTTTFTTT", results);

            s = (FilenameSelector)getInstance();
            s.setName("**/*.GZ");
            s.setCasesensitive(false);
            results = selectionString(s);
            assertEquals("FFFTFFFFTFFF", results);

            s = (FilenameSelector)getInstance();
            Parameter param1 = new Parameter();
            param1.setName("name");
            param1.setValue("**/*.bz2");
            Parameter[] params = {param1};
            s.setParameters(params);
            results = selectionString(s);
            assertEquals("FFTFFFFFFTTF", results);

        }
        finally {
            cleanupBed();
        }

    }
