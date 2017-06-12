File path: src/org/jruby/compiler/ASTCompiler19.java
Comment: / FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
Initial commit id: 20225f17
Final commit id: 37355c58
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 75
End block index: 78
                        public void nextValue(BodyCompiler context, Object object, int index) {
                            // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
                            context.getVariableCompiler().assignLocalVariable(index, false);
                        }
