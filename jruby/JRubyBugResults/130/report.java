File path: truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
Comment: IXME: Consider fixing node_assign itself rather than single case
Initial commit id: fa938c4c
Final commit id: 8b6eec17
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 2853
End block index: 2858
  @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    /* FIXME: Consider fixing node_assign itself rather than single case*/
                    ((Node)yyVal).setPosition(support.getPosition(((Node)yyVals[-2+yyTop])));
    return yyVal;
  }
