File path: src/main/org/apache/tools/ant/types/resources/StringResource.java
Comment:  can't get my head around this; is encoding treatment needed here?
Initial commit id: fbb98866
Final commit id: de1642bd
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 138
End block index: 148
    public synchronized InputStream getInputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getInputStream();
        }
        //I can't get my head around this; is encoding treatment needed here?
        return
            //new oata.util.ReaderInputStream(new InputStreamReader(
            new ByteArrayInputStream(getContent().getBytes())
            //, encoding), encoding)
            ;
    }
