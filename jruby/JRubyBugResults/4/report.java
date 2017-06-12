File path: truffle/src/main/java/org/jruby/truffle/parser/parser/ParserSupport.java
Comment: / ENEBO: Totally weird naming (in MRI is not allocated and is a local var name)
Initial commit id: fa938c4c
Final commit id: 8b6eec17
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 1258
End block index: 1261
    // ENEBO: Totally weird naming (in MRI is not allocated and is a local var name) [1.9]
    public boolean is_local_id(String name) {
        return lexer.isIdentifierChar(name.charAt(0));
    }
