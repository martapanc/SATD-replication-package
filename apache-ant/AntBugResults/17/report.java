File path: src/main/org/apache/tools/ant/taskdefs/optional/TraXLiaison.java
Comment: not sure what could be the need of this...
Initial commit id: f66cab0e
Final commit id: 0deb1503
   Bugs between [       0]:

   Bugs after [       8]:
43844a7e6 PR 56748 Spelling fixes, submitted by Ville Skyttä
f0565366f Allow params of XSLT to be optionally typed.  PR 21525.  Submitted by František Kučera
5f20b9914 microoptimizations.  PR 50716
53db3a260 Add support for Xalan2 Traces.  PR 36670.
a0c1be104 add an option to suppress processor warnings.  PR 18897.
58069d347 Try to load TraX factory via configured classpath.  PR 46172
0df2b1de3 Minor updates based on the input of Dave Brosius pr: 39320
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof

Start block index: 114
End block index: 147
    public void transform(File infile, File outfile) throws Exception {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(infile);
            fos = new FileOutputStream(outfile);
            StreamSource src = new StreamSource(fis);
            src.setSystemId(getSystemId(infile));
            StreamResult res = new StreamResult(fos);
            // not sure what could be the need of this...
            res.setSystemId(getSystemId(outfile));

            transformer.transform(src, res);
        } finally {
            // make sure to close all handles, otherwise the garbage
            // collector will close them...whenever possible and
            // Windows may complain about not being able to delete files.
            try {
                if (xslStream != null){
                    xslStream.close();
                }
            } catch (IOException ignored){}
            try {
                if (fis != null){
                    fis.close();
                }
            } catch (IOException ignored){}
            try {
                if (fos != null){
                    fos.close();
                }
            } catch (IOException ignored){}
        }
    }
