File path: src/testcases/org/apache/tools/ant/taskdefs/XmlPropertyTest.java
Comment: What is the property supposed to be?
Initial commit id: 524a7831
Final commit id: b5057787
   Bugs between [       3]:
1ddab2c14 add xmlcatalog nested element to the xmlproperty task PR: 27053 Obtained from: David Crossley and Dave Brondsema
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
cdd1db5a9 PR:	17195 Submitted by:	Markku Saarela
   Bugs after [       0]:


Start block index: 204
End block index: 260
    private static void ensureProperties (String msg, File inputFile, 
                                          File workingDir, Project project, 
                                          Properties properties) {
        Hashtable xmlproperties = project.getProperties();
        // Every key identified by the Properties must have been loaded.
        Enumeration propertyKeyEnum = properties.propertyNames();
        while(propertyKeyEnum.hasMoreElements()){
            String currentKey = propertyKeyEnum.nextElement().toString();
            String assertMsg = msg + "-" + inputFile.getName() 
                + " Key=" + currentKey;

            String propertyValue = properties.getProperty(currentKey);

            String xmlValue = (String)xmlproperties.get(currentKey);

            if ( propertyValue.indexOf("ID.") == 0 ) {
                // The property is an id's thing -- either a property
                // or a path.  We need to make sure
                // that the object was created with the given id.
                // We don't have an adequate way of testing the actual
                // *value* of the Path object, though...
                String id = currentKey;
                Object obj = project.getReferences().get(id);

                if ( obj == null ) {
                    fail(assertMsg + " Object ID does not exist.");
                }

                // What is the property supposed to be?
                propertyValue = 
                    propertyValue.substring(3, propertyValue.length());
                if (propertyValue.equals("path")) {
                    if (!(obj instanceof Path)) {
                        fail(assertMsg + " Path ID is a " 
                             + obj.getClass().getName());
                    }
                } else {
                    assertEquals(assertMsg, propertyValue, obj.toString());
                }

            } else {

                if (propertyValue.indexOf("FILE.") == 0) {
                    // The property is the name of a file.  We are testing
                    // a location attribute, so we need to resolve the given
                    // file name in the provided folder.
                    String fileName = 
                        propertyValue.substring(5, propertyValue.length());
                    File f = new File(workingDir, fileName);
                    propertyValue = f.getAbsolutePath();
                }

                assertEquals(assertMsg, propertyValue, xmlValue);
            }

        }
    }
