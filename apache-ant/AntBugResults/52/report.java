File path: src/testcases/org/apache/tools/ant/types/CommandlineTest.java
Comment: uld become a single empty argument
Initial commit id: 130a5405
Final commit id: b5057787
   Bugs between [       1]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       0]:


Start block index: 74
End block index: 161
    public void testTokenizer() {
        String[] s = Commandline.translateCommandline("1 2 3");
        assertEquals("Simple case", 3, s.length);
        for (int i=0; i<3; i++) {
            assertEquals(""+(i+1), s[i]);
        }
        
        s = Commandline.translateCommandline("");
        assertEquals("empty string", 0, s.length);

        s = Commandline.translateCommandline(null);
        assertEquals("null", 0, s.length);

        s = Commandline.translateCommandline("1 \'2\' 3");
        assertEquals("Simple case with single quotes", 3, s.length);
        assertEquals("Single quotes have been stripped", "2", s[1]);

        s = Commandline.translateCommandline("1 \"2\" 3");
        assertEquals("Simple case with double quotes", 3, s.length);
        assertEquals("Double quotes have been stripped", "2", s[1]);

        s = Commandline.translateCommandline("1 \"2 3\" 4");
        assertEquals("Case with double quotes and whitespace", 3, s.length);
        assertEquals("Double quotes stripped, space included", "2 3", s[1]);
        
        s = Commandline.translateCommandline("1 \"2\'3\" 4");
        assertEquals("Case with double quotes around single quote", 3, s.length);
        assertEquals("Double quotes stripped, single quote included", "2\'3",
                     s[1]);

        s = Commandline.translateCommandline("1 \'2 3\' 4");
        assertEquals("Case with single quotes and whitespace", 3, s.length);
        assertEquals("Single quotes stripped, space included", "2 3", s[1]);
        
        s = Commandline.translateCommandline("1 \'2\"3\' 4");
        assertEquals("Case with single quotes around double quote", 3, s.length);
        assertEquals("Single quotes stripped, double quote included", "2\"3",
                     s[1]);

        // \ doesn't have a special meaning anymore - this is different from
        // what the Unix sh does but causes a lot of problems on DOS
        // based platforms otherwise
        s = Commandline.translateCommandline("1 2\\ 3 4");
        assertEquals("case with quoted whitespace", 4, s.length);
        assertEquals("backslash included", "2\\", s[1]);

        // "" should become a single empty argument, same for ''
        // PR 5906
        s = Commandline.translateCommandline("\"\" a");
        assertEquals("Doublequoted null arg prepend", 2, s.length);
        assertEquals("Doublequoted null arg prepend", "", s[0]);
        assertEquals("Doublequoted null arg prepend", "a", s[1]);
        s = Commandline.translateCommandline("a \"\"");
        assertEquals("Doublequoted null arg append", 2, s.length);
        assertEquals("Doublequoted null arg append", "a", s[0]);
        assertEquals("Doublequoted null arg append", "", s[1]);
        s = Commandline.translateCommandline("\"\"");
        assertEquals("Doublequoted null arg", 1, s.length);
        assertEquals("Doublequoted null arg", "", s[0]);

        s = Commandline.translateCommandline("\'\' a");
        assertEquals("Singlequoted null arg prepend", 2, s.length);
        assertEquals("Singlequoted null arg prepend", "", s[0]);
        assertEquals("Singlequoted null arg prepend", "a", s[1]);
        s = Commandline.translateCommandline("a \'\'");
        assertEquals("Singlequoted null arg append", 2, s.length);
        assertEquals("Singlequoted null arg append", "a", s[0]);
        assertEquals("Singlequoted null arg append", "", s[1]);
        s = Commandline.translateCommandline("\'\'");
        assertEquals("Singlequoted null arg", 1, s.length);
        assertEquals("Singlequoted null arg", "", s[0]);

        // now to the expected failures
        
        try {
            s = Commandline.translateCommandline("a \'b c");
            fail("unbalanced single quotes undetected");
        } catch (BuildException be) {
            assertEquals("unbalanced quotes in a \'b c", be.getMessage());
        }

        try {
            s = Commandline.translateCommandline("a \"b c");
            fail("unbalanced double quotes undetected");
        } catch (BuildException be) {
            assertEquals("unbalanced quotes in a \"b c", be.getMessage());
        }
    }
