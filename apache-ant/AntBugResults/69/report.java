File path: src/testcases/org/apache/tools/ant/types/selectors/ModifiedSelectorTest.java
Comment: sorry - otherwise we will get a ClassCastException because the MockCache
Initial commit id: 04d73d15
Final commit id: b5057787
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 234
End block index: 278
    public void doDelayUpdateTest(int kind) {
        // no check for 1<=kind<=3 - only internal use therefore check it
        // while development

        // readable form of parameter kind
        String[] kinds = {"task", "target", "build"};

        // setup the "Ant project"
        MockProject project = new MockProject();
        File base  = new File("base");
        File file1 = new File("file1");
        File file2 = new File("file2");

        // setup the selector
        ModifiedSelector sel = new ModifiedSelector();
        sel.setProject(project);
        sel.setUpdate(true);
        sel.setDelayUpdate(true);
        // sorry - otherwise we will get a ClassCastException because the MockCache
        // is loaded by two different classloader ...
        sel.setClassLoader(this.getClass().getClassLoader());
        sel.addClasspath(testclasses);

        sel.setAlgorithmClass("org.apache.tools.ant.types.selectors.MockAlgorithm");
        sel.setCacheClass("org.apache.tools.ant.types.selectors.MockCache");
        sel.configure();

        // get the cache, so we can check our things
        MockCache cache = (MockCache)sel.getCache();

        // the test
        assertFalse("Cache must not be saved before 1st selection.", cache.saved);
        sel.isSelected(base, "file1", file1);
        assertFalse("Cache must not be saved after 1st selection.", cache.saved);
        sel.isSelected(base, "file2", file2);
        assertFalse("Cache must not be saved after 2nd selection.", cache.saved);
        switch (kind) {
            case 1 : project.fireTaskFinished();   break;
            case 2 : project.fireTargetFinished(); break;
            case 3 : project.fireBuildFinished();  break;
        }
        assertTrue("Cache must be saved after " + kinds[kind-1] + "Finished-Event.", cache.saved);

        // MockCache doesnt create a file - therefore no cleanup needed
    }
