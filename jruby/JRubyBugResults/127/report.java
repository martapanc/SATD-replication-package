File path: truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
Comment: NEBO: Lots of optz in 1.9 parser here
Initial commit id: fa938c4c
Final commit id: 8b6eec17
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 3599
End block index: 3603
  @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      /* ENEBO: Lots of optz in 1.9 parser here*/
                    yyVal = new ForNode(((ISourcePosition)yyVals[-8+yyTop]), ((Node)yyVals[-7+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-4+yyTop]), support.getCurrentScope());
    return yyVal;
  }
