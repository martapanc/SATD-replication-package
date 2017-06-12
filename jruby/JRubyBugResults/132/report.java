File path: truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
Comment: IXME: lose syntactical elements here (and others like this)
Initial commit id: fa938c4c
Final commit id: 8b6eec17
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 3297
End block index: 3308
  @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                    Node node = null;

                    /* FIXME: lose syntactical elements here (and others like this)*/
                    if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
                        (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
                        yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
                    } else {
                        yyVal = support.arg_concat(support.getPosition(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
    return yyVal;
  }
