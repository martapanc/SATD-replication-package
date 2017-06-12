File path: src/main/org/apache/tools/ant/taskdefs/optional/ejb/EjbJar.java
Comment: hould probably think of a more elegant way to handle this
Initial commit id: e02f0ab1
Final commit id: cf07b113
   Bugs between [       0]:

   Bugs after [       3]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
85b203207 changed email address of Rob van Oostrum to rob at springwellfarms dot ca PR: 14709
202be1f5e Adds functionality that makes jboss element look for jbosscmp-jdbc.xml descriptor if ejbjar has cmpversion="2.0" set PR: 14709 Submitted by: Rob van Oostrum (rvanoo at xs4all dot nl)

Start block index: 370
End block index: 413
    public void writeJar(File jarfile, Hashtable files) throws BuildException{
        JarOutputStream jarStream = null;
        Iterator entryIterator = null;
        String entryName = null;
        File entryFile = null;

        try {
            /* If the jarfile already exists then whack it and recreate it.
             * Should probably think of a more elegant way to handle this
             * so that in case of errors we don't leave people worse off
             * than when we started =)
             */
            if (jarfile.exists()) jarfile.delete();
            jarfile.getParentFile().mkdirs();
            jarfile.createNewFile();
            
            // Create the streams necessary to write the jarfile
            jarStream = new JarOutputStream(new FileOutputStream(jarfile));
            jarStream.setMethod(JarOutputStream.DEFLATED);
            
            // Loop through all the class files found and add them to the jar
            entryIterator = files.keySet().iterator();
            while (entryIterator.hasNext()) {
                entryName = (String) entryIterator.next();
                entryFile = (File) files.get(entryName);
                
                this.log("adding file '" + entryName + "'",
                         Project.MSG_VERBOSE);

                addFileToJar(jarStream,
                             new FileInputStream(entryFile),
                             entryName);
            }
            // All done.  Close the jar stream.
            jarStream.close();
        }
        catch(IOException ioe) {
            String msg = "IOException while processing ejb-jar file '"
                + jarfile.toString()
                + "'. Details: "
                + ioe.getMessage();
            throw new BuildException(msg, ioe);
        }
    } // end of writeJar
