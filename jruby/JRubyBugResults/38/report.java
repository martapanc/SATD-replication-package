File path: truffle/src/main/java/org/jruby/truffle/parser/parser/ParserConfiguration.java
Comment: / FIXME: We should really not be creating the dynamic scope for the root
Initial commit id: fa938c4c
Final commit id: 8b6eec17
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 157
End block index: 167
    public DynamicScope getScope(String file) {
        if (asBlock) return existingScope;

        // FIXME: We should really not be creating the dynamic scope for the root
        // of the AST before parsing.  This makes us end up needing to readjust
        // this dynamic scope coming out of parse (and for local static scopes it
        // will always happen because of $~ and $_).
        // FIXME: Because we end up adjusting this after-the-fact, we can't use
        // any of the specific-size scopes.
        return new ManyVarsDynamicScope(staticScopeFactory.newLocalScope(null, file), existingScope);
    }
