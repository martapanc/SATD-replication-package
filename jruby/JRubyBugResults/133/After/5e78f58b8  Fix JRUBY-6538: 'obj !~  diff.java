diff --git a/src/org/jruby/parser/Ruby19Parser.java b/src/org/jruby/parser/Ruby19Parser.java
index f80dd7e021..7a32bcd6ae 100644
--- a/src/org/jruby/parser/Ruby19Parser.java
+++ b/src/org/jruby/parser/Ruby19Parser.java
@@ -2670,1618 +2670,1618 @@ states[80] = new ParserState() {
     return yyVal;
   }
 };
 states[281] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new Hash19Node(((Token)yyVals[-2+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[516] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-1+yyTop]) == null) {
                         support.yyerror("can't define single method for ().");
                     } else if (((Node)yyVals[-1+yyTop]) instanceof ILiteralNode) {
                         support.yyerror("can't define single method for literals.");
                     }
                     support.checkExpression(((Node)yyVals[-1+yyTop]));
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[449] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = ((FloatNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[382] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[47] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
     return yyVal;
   }
 };
 states[248] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.arg_blk_pass(((Node)yyVals[-1+yyTop]), ((BlockPassNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[114] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new LiteralNode(((Token)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[315] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.setState(LexState.EXPR_FNAME);
     return yyVal;
   }
 };
 states[483] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[416] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
 
                     ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
                     int extraLength = ((String) ((Token)yyVals[-2+yyTop]).getValue()).length() - 1;
 
                     /* We may need to subtract addition offset off of first */
                     /* string fragment (we optimistically take one off in*/
                     /* ParserSupport.literal_concat).  Check token length*/
                     /* and subtract as neeeded.*/
                     if ((((Node)yyVals[-1+yyTop]) instanceof DStrNode) && extraLength > 0) {
                       Node strNode = ((DStrNode)((Node)yyVals[-1+yyTop])).get(0);
                     }
     return yyVal;
   }
 };
 states[349] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[14] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
 states[215] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "|", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[81] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                       yyVal = new MultipleAsgn19Node(((Token)yyVals[-2+yyTop]).getPosition(), null, new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[282] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ReturnNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[517] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[450] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = support.negateInteger(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[383] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[249] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newArrayNode(((ListNode)yyVals[-1+yyTop]).getPosition(), new Hash19Node(lexer.getPosition(), ((ListNode)yyVals[-1+yyTop])));
                     yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[115] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new LiteralNode(((Token)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[316] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.setInSingle(support.getInSingle() + 1);
                     support.pushLocalScope();
                     lexer.setState(LexState.EXPR_ENDFN); /* force for args */
     return yyVal;
   }
 };
 states[484] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[417] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ISourcePosition position = ((Token)yyVals[-2+yyTop]).getPosition();
 
                     if (((Node)yyVals[-1+yyTop]) == null) {
                         yyVal = new XStrNode(position, null);
                     } else if (((Node)yyVals[-1+yyTop]) instanceof StrNode) {
                         yyVal = new XStrNode(position, (ByteList) ((StrNode)yyVals[-1+yyTop]).getValue().clone());
                     } else if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
                         yyVal = new DXStrNode(position, ((DStrNode)yyVals[-1+yyTop]));
 
                         ((Node)yyVal).setPosition(position);
                     } else {
                         yyVal = new DXStrNode(position).add(((Node)yyVals[-1+yyTop]));
                     }
     return yyVal;
   }
 };
 states[350] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[15] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.appendToBlock(((Node)yyVals[-2+yyTop]), support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop]))));
     return yyVal;
   }
 };
 states[216] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "^", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[283] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_yield(((Token)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[518] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[451] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = support.negateFloat(((FloatNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[384] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_fcall(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[49] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.checkExpression(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[250] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), new Hash19Node(lexer.getPosition(), ((ListNode)yyVals[-1+yyTop])));
                     yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[116] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((LiteralNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[317] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     /* TODO: We should use implicit nil for body, but problem (punt til later)*/
                     Node body = ((Node)yyVals[-1+yyTop]); /*$8 == null ? NilImplicitNode.NIL : $8;*/
 
                     yyVal = new DefsNode(((Token)yyVals[-8+yyTop]).getPosition(), ((Node)yyVals[-7+yyTop]), new ArgumentNode(((Token)yyVals[-4+yyTop]).getPosition(), (String) ((Token)yyVals[-4+yyTop]).getValue()), ((ArgsNode)yyVals[-2+yyTop]), support.getCurrentScope(), body);
                     support.popCurrentScope();
                     support.setInSingle(support.getInSingle() - 1);
     return yyVal;
   }
 };
 states[485] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((RestArgNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[418] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newRegexpNode(((Token)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), (RegexpNode) ((RegexpNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[351] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[16] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[217] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "&", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[83] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[284] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ZYieldNode(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[385] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[251] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
     return yyVal;
   }
 };
 states[117] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[318] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new BreakNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[486] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((RestArgNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[419] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ZArrayNode(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[352] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     RestArgNode rest = new UnnamedRestArgNode(((ListNode)yyVals[-1+yyTop]).getPosition(), null, support.getCurrentScope().addVariable("*"));
                     yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, rest, null, null);
     return yyVal;
   }
 };
 states[17] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.setState(LexState.EXPR_FNAME);
     return yyVal;
   }
 };
 states[218] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=>", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[84] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newArrayNode(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[285] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ZYieldNode(((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[520] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).addAll(((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[386] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[252] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = Long.valueOf(lexer.getCmdArgumentState().begin());
     return yyVal;
   }
 };
 states[118] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newUndef(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[319] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new NextNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[487] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[420] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[353] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[219] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[85] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[286] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new DefinedNode(((Token)yyVals[-4+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[18] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newAlias(((Token)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[521] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ISourcePosition pos;
                     if (((Node)yyVals[-2+yyTop]) == null && ((Node)yyVals[0+yyTop]) == null) {
                         pos = ((Token)yyVals[-1+yyTop]).getPosition();
                     } else {
                         pos = ((Node)yyVals[-2+yyTop]).getPosition();
                     }
 
                     yyVal = support.newArrayNode(pos, ((Node)yyVals[-2+yyTop])).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[387] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]), null, null);
     return yyVal;
   }
 };
 states[52] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ReturnNode(((Token)yyVals[-1+yyTop]).getPosition(), support.ret_args(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]).getPosition()));
     return yyVal;
   }
 };
 states[253] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.getCmdArgumentState().reset(((Long)yyVals[-1+yyTop]).longValue());
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[119] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.setState(LexState.EXPR_FNAME);
     return yyVal;
   }
 };
 states[320] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new RedoNode(((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[488] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(lexer.getPosition(), null, null, null, null, null);
     return yyVal;
   }
 };
 states[421] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[354] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[220] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[86] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[287] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[-1+yyTop])), "!");
     return yyVal;
   }
 };
 states[19] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new VAliasNode(((Token)yyVals[-2+yyTop]).getPosition(), (String) ((Token)yyVals[-1+yyTop]).getValue(), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
 states[522] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ISourcePosition pos = ((Token)yyVals[-1+yyTop]).getPosition();
                     yyVal = support.newArrayNode(pos, new SymbolNode(pos, (String) ((Token)yyVals[-1+yyTop]).getValue())).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[388] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-2+yyTop]), new Token("call", ((Node)yyVals[-2+yyTop]).getPosition()), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[53] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new BreakNode(((Token)yyVals[-1+yyTop]).getPosition(), support.ret_args(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]).getPosition()));
     return yyVal;
   }
 };
 states[254] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new BlockPassNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[120] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.appendToBlock(((Node)yyVals[-3+yyTop]), support.newUndef(((Node)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
 states[321] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new RetryNode(((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[489] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("formal argument cannot be a constant");
     return yyVal;
   }
 };
 states[422] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]) instanceof EvStrNode ? new DStrNode(((ListNode)yyVals[-2+yyTop]).getPosition(), lexer.getEncoding()).add(((Node)yyVals[-1+yyTop])) : ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[355] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-3+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[221] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[87] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[288] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(NilImplicitNode.NIL, "!");
     return yyVal;
   }
 };
 states[20] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new VAliasNode(((Token)yyVals[-2+yyTop]).getPosition(), (String) ((Token)yyVals[-1+yyTop]).getValue(), "$" + ((BackRefNode)yyVals[0+yyTop]).getType());
     return yyVal;
   }
 };
 states[389] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-2+yyTop]), new Token("call", ((Node)yyVals[-2+yyTop]).getPosition()), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[54] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new NextNode(((Token)yyVals[-1+yyTop]).getPosition(), support.ret_args(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]).getPosition()));
     return yyVal;
   }
 };
 states[255] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((BlockPassNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[322] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.checkExpression(((Node)yyVals[0+yyTop]));
                     yyVal = ((Node)yyVals[0+yyTop]);
                     if (yyVal == null) yyVal = NilImplicitNode.NIL;
     return yyVal;
   }
 };
 states[490] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("formal argument cannot be an instance variable");
     return yyVal;
   }
 };
 states[356] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-5+yyTop])), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[88] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[289] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new FCallNoArgBlockNode(((Token)yyVals[-1+yyTop]).getPosition(), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((IterNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[21] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("can't make alias for the number variables");
     return yyVal;
   }
 };
 states[222] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[457] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new Token("nil", Tokens.kNIL, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[390] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_super(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[256] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[491] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("formal argument cannot be a global variable");
     return yyVal;
   }
 };
 states[424] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[357] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-1+yyTop])), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[89] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[22] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[223] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "==", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[458] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new Token("self", Tokens.kSELF, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[391] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ZSuperNode(((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[56] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[492] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("formal argument cannot be a class variable");
     return yyVal;
   }
 };
 states[425] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = new ZArrayNode(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[358] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[90] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
 states[291] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-1+yyTop]) != null && 
                           ((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
                         throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                     }
                     yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
                     ((Node)yyVal).setPosition(((Node)yyVals[-1+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[23] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IfNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), null);
     return yyVal;
   }
 };
 states[224] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "===", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[459] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new Token("true", Tokens.kTRUE, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[392] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-3+yyTop]) instanceof SelfNode) {
                         yyVal = support.new_fcall(new Token("[]", support.getPosition(((Node)yyVals[-3+yyTop]))), ((Node)yyVals[-1+yyTop]), null);
                     } else {
                         yyVal = support.new_call(((Node)yyVals[-3+yyTop]), new Token("[]", support.getPosition(((Node)yyVals[-3+yyTop]))), ((Node)yyVals[-1+yyTop]), null);
                     }
     return yyVal;
   }
 };
 states[258] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ISourcePosition pos = ((Node)yyVals[0+yyTop]) == null ? lexer.getPosition() : ((Node)yyVals[0+yyTop]).getPosition();
                     yyVal = support.newArrayNode(pos, ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[57] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[426] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-1+yyTop]);
                     ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[359] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((RestArgNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[91] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
 states[292] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((LambdaNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[24] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IfNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), null, ((Node)yyVals[-2+yyTop]));
     return yyVal;
   }
 };
 states[225] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "!=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[460] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new Token("false", Tokens.kFALSE, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[393] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.pushBlockScope();
     return yyVal;
   }
 };
 states[192] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                     /* FIXME: Consider fixing node_assign itself rather than single case*/
                     ((Node)yyVal).setPosition(support.getPosition(((Node)yyVals[-2+yyTop])));
     return yyVal;
   }
 };
 states[58] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.pushBlockScope();
     return yyVal;
   }
 };
 states[259] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newSplatNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[494] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.formal_argument(((Token)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[427] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[360] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((RestArgNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[293] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IfNode(((Token)yyVals[-5+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[25] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
                         yyVal = new WhileNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
                     } else {
                         yyVal = new WhileNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
                     }
     return yyVal;
   }
 };
 states[226] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getMatchNode(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                   /* ENEBO
                         $$ = match_op($1, $3);
                         if (nd_type($1) == NODE_LIT && TYPE($1->nd_lit) == T_REGEXP) {
                             $$ = reg_named_capture_assign($1->nd_lit, $$);
                         }
                   */
     return yyVal;
   }
 };
 states[92] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
 states[461] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new Token("__FILE__", Tokens.k__FILE__, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[394] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IterNode(((Token)yyVals[-4+yyTop]).getPosition(), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                     support.popCurrentScope();
     return yyVal;
   }
 };
 states[193] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ISourcePosition position = ((Token)yyVals[-1+yyTop]).getPosition();
                     Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                     yyVal = support.node_assign(((Node)yyVals[-4+yyTop]), new RescueNode(position, ((Node)yyVals[-2+yyTop]), new RescueBodyNode(position, null, body, null), null));
     return yyVal;
   }
 };
 states[59] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IterNode(((Token)yyVals[-4+yyTop]).getPosition(), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                     support.popCurrentScope();
     return yyVal;
   }
 };
 states[260] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node node = support.splat_array(((Node)yyVals[-2+yyTop]));
 
                     if (node != null) {
                         yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
                     } else {
                         yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                     }
     return yyVal;
   }
 };
 states[495] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.arg_var(((Token)yyVals[0+yyTop]));
   /*
                     $$ = new ArgAuxiliaryNode($1.getPosition(), (String) $1.getValue(), 1);
   */
     return yyVal;
   }
 };
 states[428] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[361] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[294] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IfNode(((Token)yyVals[-5+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-2+yyTop]));
     return yyVal;
   }
 };
 states[26] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
                         yyVal = new UntilNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
                     } else {
                         yyVal = new UntilNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
                     }
     return yyVal;
   }
 };
 states[227] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new NotNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getMatchNode(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "!~", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[93] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("dynamic constant assignment");
                     }
 
                     ISourcePosition position = support.getPosition(((Node)yyVals[-2+yyTop]));
 
                     yyVal = new ConstDeclNode(position, null, support.new_colon2(position, ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[462] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new Token("__LINE__", Tokens.k__LINE__, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[395] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.pushBlockScope();
     return yyVal;
   }
 };
 states[194] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.checkExpression(((Node)yyVals[0+yyTop]));
 
                     ISourcePosition pos = ((AssignableNode)yyVals[-2+yyTop]).getPosition();
                     String asgnOp = (String) ((Token)yyVals[-1+yyTop]).getValue();
                     if (asgnOp.equals("||")) {
                         ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                         yyVal = new OpAsgnOrNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                     } else if (asgnOp.equals("&&")) {
                         ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                         yyVal = new OpAsgnAndNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                     } else {
                         ((AssignableNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-2+yyTop])), asgnOp, ((Node)yyVals[0+yyTop])));
                         ((AssignableNode)yyVals[-2+yyTop]).setPosition(pos);
                         yyVal = ((AssignableNode)yyVals[-2+yyTop]);
                     }
     return yyVal;
   }
 };
 states[60] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_fcall(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[261] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
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
 };
 states[496] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
                     /*		    {
 			ID tid = internal_id();
 			arg_var(tid);
 			if (dyna_in_block()) {
 			    $2->nd_value = NEW_DVAR(tid);
 			}
 			else {
 			    $2->nd_value = NEW_LVAR(tid);
 			}
 			$$ = NEW_ARGS_AUX(tid, 1);
 			$$->nd_next = $2;*/
     return yyVal;
   }
 };
 states[429] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ByteList aChar = ByteList.create("");
                     aChar.setEncoding(lexer.getEncoding());
                     yyVal = lexer.createStrNode(((Token)yyVals[0+yyTop]).getPosition(), aChar, 0);
     return yyVal;
   }
 };
 states[362] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
     /* was $$ = null;*/
                    yyVal = support.new_args(lexer.getPosition(), null, null, null, null, null);
     return yyVal;
   }
 };
 states[295] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.getConditionState().begin();
     return yyVal;
   }
 };
 states[27] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                     yyVal = new RescueNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), new RescueBodyNode(support.getPosition(((Node)yyVals[-2+yyTop])), null, body, null), null);
     return yyVal;
   }
 };
 states[228] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
     return yyVal;
   }
 };
 states[94] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("dynamic constant assignment");
                     }
 
                     ISourcePosition position = ((Token)yyVals[-1+yyTop]).getPosition();
 
                     yyVal = new ConstDeclNode(position, null, support.new_colon3(position, (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[463] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new Token("__ENCODING__", Tokens.k__ENCODING__, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[396] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IterNode(((Token)yyVals[-4+yyTop]).getPosition(), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                     /* FIXME: What the hell is this?*/
                     ((ISourcePositionHolder)yyVals[-5+yyTop]).setPosition(support.getPosition(((ISourcePositionHolder)yyVals[-5+yyTop])));
                     support.popCurrentScope();
     return yyVal;
   }
 };
 states[329] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IfNode(((Token)yyVals[-4+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[195] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.checkExpression(((Node)yyVals[-2+yyTop]));
                     ISourcePosition pos = ((Token)yyVals[-1+yyTop]).getPosition();
                     Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                     Node rest;
 
                     pos = ((AssignableNode)yyVals[-4+yyTop]).getPosition();
                     String asgnOp = (String) ((Token)yyVals[-3+yyTop]).getValue();
                     if (asgnOp.equals("||")) {
                         ((AssignableNode)yyVals[-4+yyTop]).setValueNode(((Node)yyVals[-2+yyTop]));
                         rest = new OpAsgnOrNode(pos, support.gettable2(((AssignableNode)yyVals[-4+yyTop])), ((AssignableNode)yyVals[-4+yyTop]));
                     } else if (asgnOp.equals("&&")) {
                         ((AssignableNode)yyVals[-4+yyTop]).setValueNode(((Node)yyVals[-2+yyTop]));
                         rest = new OpAsgnAndNode(pos, support.gettable2(((AssignableNode)yyVals[-4+yyTop])), ((AssignableNode)yyVals[-4+yyTop]));
                     } else {
                         ((AssignableNode)yyVals[-4+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-4+yyTop])), asgnOp, ((Node)yyVals[-2+yyTop])));
                         ((AssignableNode)yyVals[-4+yyTop]).setPosition(pos);
                         rest = ((AssignableNode)yyVals[-4+yyTop]);
                     }
 
                     yyVal = new RescueNode(((Token)yyVals[-1+yyTop]).getPosition(), rest, new RescueBodyNode(((Token)yyVals[-1+yyTop]).getPosition(), null, body, null), null);
     return yyVal;
   }
 };
 states[61] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_fcall(((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[262] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node node = support.splat_array(((Node)yyVals[-2+yyTop]));
 
                     if (node != null) {
                         yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
                     } else {
                         yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                     }
     return yyVal;
   }
 };
 states[497] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(lexer.getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[430] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.literal_concat(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[363] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.commandStart = true;
                     yyVal = ((ArgsNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[28] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (support.isInDef() || support.isInSingle()) {
                         support.warn(ID.END_IN_METHOD, ((Token)yyVals[-3+yyTop]).getPosition(), "END in method; use at_exit");
                     }
                     yyVal = new PostExeNode(((Token)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[229] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "~");
     return yyVal;
   }
 };
 states[95] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.backrefAssignError(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[296] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.getConditionState().end();
     return yyVal;
   }
 };
 states[464] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.gettable(((Token)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[397] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newWhenNode(((Token)yyVals[-4+yyTop]).getPosition(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[196] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
   /* FIXME: arg_concat missing for opt_call_args*/
                     yyVal = support.new_opElementAsgnNode(support.getPosition(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-5+yyTop]), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[62] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[263] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node node = null;
 
                     if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
                         (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
                         yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
                     } else {
                         yyVal = support.arg_concat(((Node)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                     }
     return yyVal;
   }
 };
 states[498] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                     yyVal = ((ListNode)yyVals[-2+yyTop]);
     return yyVal;
   }
 };
 states[431] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[364] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((Token)yyVals[-2+yyTop]).getPosition(), null, null, null, null, null);
     return yyVal;
   }
 };
 states[230] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<<", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[96] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                       /* if (!($$ = assignable($1, 0))) $$ = NEW_BEGIN(0);*/
                     yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[297] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
                     yyVal = new WhileNode(((Token)yyVals[-6+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
     return yyVal;
   }
 };
 states[465] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[331] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[197] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
     return yyVal;
   }
 };
 states[63] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop])); 
     return yyVal;
   }
 };
 states[264] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = support.newSplatNode(support.getPosition(((Token)yyVals[-1+yyTop])), ((Node)yyVals[0+yyTop]));  
     return yyVal;
   }
 };
 states[499] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.arg_var(support.formal_argument(((Token)yyVals[-2+yyTop])));
                     yyVal = new OptArgNode(((Token)yyVals[-2+yyTop]).getPosition(), support.assignable(((Token)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
 states[432] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[365] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((Token)yyVals[0+yyTop]).getPosition(), null, null, null, null, null);
     return yyVal;
   }
 };
 states[30] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.checkExpression(((Node)yyVals[0+yyTop]));
                     ((MultipleAsgn19Node)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                     yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
     return yyVal;
   }
 };
 states[231] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">>", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[97] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[298] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                   lexer.getConditionState().begin();
     return yyVal;
   }
 };
 states[466] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[198] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
     return yyVal;
   }
 };
 states[64] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[500] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.arg_var(support.formal_argument(((Token)yyVals[-2+yyTop])));
                     yyVal = new OptArgNode(((Token)yyVals[-2+yyTop]).getPosition(), support.assignable(((Token)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
 states[433] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[366] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ArgsNode)yyVals[-2+yyTop]);
     return yyVal;
   }
 };
 states[31] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.checkExpression(((Node)yyVals[0+yyTop]));
 
                     ISourcePosition pos = ((AssignableNode)yyVals[-2+yyTop]).getPosition();
                     String asgnOp = (String) ((Token)yyVals[-1+yyTop]).getValue();
                     if (asgnOp.equals("||")) {
                         ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                         yyVal = new OpAsgnOrNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                     } else if (asgnOp.equals("&&")) {
                         ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                         yyVal = new OpAsgnAndNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                     } else {
                         ((AssignableNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-2+yyTop])), asgnOp, ((Node)yyVals[0+yyTop])));
                         ((AssignableNode)yyVals[-2+yyTop]).setPosition(pos);
                         yyVal = ((AssignableNode)yyVals[-2+yyTop]);
                     }
     return yyVal;
   }
 };
 states[232] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newAndNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[98] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
 states[299] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                   lexer.getConditionState().end();
     return yyVal;
   }
 };
 states[467] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[400] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node node;
                     if (((Node)yyVals[-3+yyTop]) != null) {
                         node = support.appendToBlock(support.node_assign(((Node)yyVals[-3+yyTop]), new GlobalVarNode(((Token)yyVals[-5+yyTop]).getPosition(), "$!")), ((Node)yyVals[-1+yyTop]));
                         if (((Node)yyVals[-1+yyTop]) != null) {
                             node.setPosition(support.unwrapNewlineNode(((Node)yyVals[-1+yyTop])).getPosition());
                         }
                     } else {
                         node = ((Node)yyVals[-1+yyTop]);
                     }
                     Node body = node == null ? NilImplicitNode.NIL : node;
                     yyVal = new RescueBodyNode(((Token)yyVals[-5+yyTop]).getPosition(), ((Node)yyVals[-4+yyTop]), body, ((RescueBodyNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[333] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
     return yyVal;
   }
 };
 states[199] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
     return yyVal;
   }
 };
 states[65] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[501] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new BlockNode(((Node)yyVals[0+yyTop]).getPosition()).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[434] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = lexer.getStrTerm();
                     lexer.setStrTerm(null);
                     lexer.setState(LexState.EXPR_BEG);
     return yyVal;
   }
 };
 states[367] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[32] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
   /* FIXME: arg_concat logic missing for opt_call_args*/
                     yyVal = support.new_opElementAsgnNode(support.getPosition(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-5+yyTop]), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[233] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newOrNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[99] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
 states[300] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
                     yyVal = new UntilNode(((Token)yyVals[-6+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
     return yyVal;
   }
 };
 states[468] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[401] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null; 
     return yyVal;
   }
 };
 states[334] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[200] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("constant re-assignment");
     return yyVal;
   }
 };
 states[66] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_super(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop])); /* .setPosFrom($2);*/
     return yyVal;
   }
 };
 }
 					// line 2030 "Ruby19Parser.y"
 
     /** The parse method use an lexer stream and parse it to an AST node 
      * structure
      */
     public RubyParserResult parse(ParserConfiguration configuration, LexerSource source) throws IOException {
         support.reset();
         support.setConfiguration(configuration);
         support.setResult(new RubyParserResult());
         
         lexer.reset();
         lexer.setSource(source);
         lexer.setEncoding(configuration.getDefaultEncoding());
 
         Object debugger = null;
         if (configuration.isDebug()) {
             try {
                 Class yyDebugAdapterClass = Class.forName("jay.yydebug.yyDebugAdapter");
                 debugger = yyDebugAdapterClass.newInstance();
             } catch (IllegalAccessException iae) {
                 // ignore, no debugger present
             } catch (InstantiationException ie) {
                 // ignore, no debugger present
             } catch (ClassNotFoundException cnfe) {
                 // ignore, no debugger present
             }
         }
         //yyparse(lexer, new jay.yydebug.yyAnim("JRuby", 9));
         yyparse(lexer, debugger);
         
         return support.getResult();
     }
 }
 					// line 8163 "-"
diff --git a/src/org/jruby/parser/Ruby19Parser.y b/src/org/jruby/parser/Ruby19Parser.y
index 0830745f9c..0562e8c5ce 100644
--- a/src/org/jruby/parser/Ruby19Parser.y
+++ b/src/org/jruby/parser/Ruby19Parser.y
@@ -1,1902 +1,1902 @@
 %{
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2008-2009 Thomas E Enebo <enebo@acm.org>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.parser;
 
 import java.io.IOException;
 
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArgumentNode;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.AssignableNode;
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BlockAcceptingNode;
 import org.jruby.ast.BlockArgNode;
 import org.jruby.ast.BlockNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.ClassNode;
 import org.jruby.ast.ClassVarNode;
 import org.jruby.ast.Colon3Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.DStrNode;
 import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.DXStrNode;
 import org.jruby.ast.DefinedNode;
 import org.jruby.ast.DefnNode;
 import org.jruby.ast.DefsNode;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EnsureNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FCallNoArgBlockNode;
 import org.jruby.ast.FCallNoArgNode;
 import org.jruby.ast.FixnumNode;
 import org.jruby.ast.FloatNode;
 import org.jruby.ast.ForNode;
 import org.jruby.ast.GlobalVarNode;
 import org.jruby.ast.Hash19Node;
 import org.jruby.ast.IfNode;
 import org.jruby.ast.InstVarNode;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.LambdaNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.LiteralNode;
 import org.jruby.ast.ModuleNode;
 import org.jruby.ast.MultipleAsgn19Node;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.NilImplicitNode;
 import org.jruby.ast.NilNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.OpAsgnAndNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OpAsgnOrNode;
 import org.jruby.ast.OptArgNode;
 import org.jruby.ast.PostExeNode;
 import org.jruby.ast.PreExe19Node;
 import org.jruby.ast.RedoNode;
 import org.jruby.ast.RegexpNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
 import org.jruby.ast.RestArgNode;
 import org.jruby.ast.RetryNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.SClassNode;
 import org.jruby.ast.SelfNode;
 import org.jruby.ast.StarNode;
 import org.jruby.ast.StrNode;
 import org.jruby.ast.SymbolNode;
 import org.jruby.ast.UnnamedRestArgNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.VAliasNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.ast.XStrNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.ast.ZArrayNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.ast.ZYieldNode;
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.common.IRubyWarnings;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.ISourcePositionHolder;
 import org.jruby.lexer.yacc.LexerSource;
 import org.jruby.lexer.yacc.RubyYaccLexer;
 import org.jruby.lexer.yacc.RubyYaccLexer.LexState;
 import org.jruby.lexer.yacc.StrTerm;
 import org.jruby.lexer.yacc.SyntaxException;
 import org.jruby.lexer.yacc.SyntaxException.PID;
 import org.jruby.lexer.yacc.Token;
 import org.jruby.util.ByteList;
 
 public class Ruby19Parser implements RubyParser {
     protected ParserSupport19 support;
     protected RubyYaccLexer lexer;
 
     public Ruby19Parser() {
         this(new ParserSupport19());
     }
 
     public Ruby19Parser(ParserSupport19 support) {
         this.support = support;
         lexer = new RubyYaccLexer(false);
         lexer.setParserSupport(support);
         support.setLexer(lexer);
     }
 
     public void setWarnings(IRubyWarnings warnings) {
         support.setWarnings(warnings);
         lexer.setWarnings(warnings);
     }
 %}
 
 // We need to make sure we have same tokens in the same order and up
 // front so 1.8 and 1.9 parser can use the same Tokens.java file.
 %token <Token> kCLASS kMODULE kDEF kUNDEF kBEGIN kRESCUE kENSURE kEND kIF
   kUNLESS kTHEN kELSIF kELSE kCASE kWHEN kWHILE kUNTIL kFOR kBREAK kNEXT
   kREDO kRETRY kIN kDO kDO_COND kDO_BLOCK kRETURN kYIELD kSUPER kSELF kNIL
   kTRUE kFALSE kAND kOR kNOT kIF_MOD kUNLESS_MOD kWHILE_MOD kUNTIL_MOD
   kRESCUE_MOD kALIAS kDEFINED klBEGIN klEND k__LINE__ k__FILE__
   k__ENCODING__ kDO_LAMBDA 
 
 %token <Token> tIDENTIFIER tFID tGVAR tIVAR tCONSTANT tCVAR tLABEL tCHAR
 %type <Token> variable
 %type <Token> sym symbol operation operation2 operation3 cname fname op 
 %type <Token> f_norm_arg dot_or_colon restarg_mark blkarg_mark
 %token <Token> tUPLUS         /* unary+ */
 %token <Token> tUMINUS        /* unary- */
 %token <Token> tUMINUS_NUM    /* unary- */
 %token <Token> tPOW           /* ** */
 %token <Token> tCMP           /* <=> */
 %token <Token> tEQ            /* == */
 %token <Token> tEQQ           /* === */
 %token <Token> tNEQ           /* != */
 %token <Token> tGEQ           /* >= */
 %token <Token> tLEQ           /* <= */
 %token <Token> tANDOP tOROP   /* && and || */
 %token <Token> tMATCH tNMATCH /* =~ and !~ */
 %token <Token>  tDOT           /* Is just '.' in ruby and not a token */
 %token <Token> tDOT2 tDOT3    /* .. and ... */
 %token <Token> tAREF tASET    /* [] and []= */
 %token <Token> tLSHFT tRSHFT  /* << and >> */
 %token <Token> tCOLON2        /* :: */
 %token <Token> tCOLON3        /* :: at EXPR_BEG */
 %token <Token> tOP_ASGN       /* +=, -=  etc. */
 %token <Token> tASSOC         /* => */
 %token <Token> tLPAREN        /* ( */
 %token <Token> tLPAREN2        /* ( Is just '(' in ruby and not a token */
 %token <Token> tRPAREN        /* ) */
 %token <Token> tLPAREN_ARG    /* ( */
 %token <Token> tLBRACK        /* [ */
 %token <Token> tRBRACK        /* ] */
 %token <Token> tLBRACE        /* { */
 %token <Token> tLBRACE_ARG    /* { */
 %token <Token> tSTAR          /* * */
 %token <Token> tSTAR2         /* *  Is just '*' in ruby and not a token */
 %token <Token> tAMPER         /* & */
 %token <Token> tAMPER2        /* &  Is just '&' in ruby and not a token */
 %token <Token> tTILDE         /* ` is just '`' in ruby and not a token */
 %token <Token> tPERCENT       /* % is just '%' in ruby and not a token */
 %token <Token> tDIVIDE        /* / is just '/' in ruby and not a token */
 %token <Token> tPLUS          /* + is just '+' in ruby and not a token */
 %token <Token> tMINUS         /* - is just '-' in ruby and not a token */
 %token <Token> tLT            /* < is just '<' in ruby and not a token */
 %token <Token> tGT            /* > is just '>' in ruby and not a token */
 %token <Token> tPIPE          /* | is just '|' in ruby and not a token */
 %token <Token> tBANG          /* ! is just '!' in ruby and not a token */
 %token <Token> tCARET         /* ^ is just '^' in ruby and not a token */
 %token <Token> tLCURLY        /* { is just '{' in ruby and not a token */
 %token <Token> tRCURLY        /* } is just '}' in ruby and not a token */
 %token <Token> tBACK_REF2     /* { is just '`' in ruby and not a token */
 %token <Token> tSYMBEG tSTRING_BEG tXSTRING_BEG tREGEXP_BEG tWORDS_BEG tQWORDS_BEG
 %token <Token> tSTRING_DBEG tSTRING_DVAR tSTRING_END
 %token <Token> tLAMBDA tLAMBEG
 %token <Node> tNTH_REF tBACK_REF tSTRING_CONTENT tINTEGER
 %token <FloatNode> tFLOAT  
 %token <RegexpNode>  tREGEXP_END
 %type <RestArgNode> f_rest_arg 
 %type <Node> singleton strings string string1 xstring regexp
 %type <Node> string_contents xstring_contents string_content method_call
 %type <Node> words qwords word literal numeric dsym cpath command_asgn command_call
 %type <Node> compstmt bodystmt stmts stmt expr arg primary command 
 %type <Node> expr_value primary_value opt_else cases if_tail exc_var
    // ENEBO: missing call_args2, open_args
 %type <Node> call_args opt_ensure paren_args superclass
 %type <Node> command_args var_ref opt_paren_args block_call block_command
 %type <Node> f_opt undef_list string_dvar backref
 %type <ArgsNode> f_args f_arglist f_larglist block_param block_param_def opt_block_param 
 %type <Node> mrhs mlhs_item mlhs_node arg_value case_body exc_list aref_args
    // ENEBO: missing block_var == for_var, opt_block_var
 %type <Node> lhs none args
 %type <ListNode> qword_list word_list f_arg f_optarg f_marg_list
    // ENEBO: missing when_args
 %type <ListNode> mlhs_head assocs assoc assoc_list mlhs_post f_block_optarg
 %type <BlockPassNode> opt_block_arg block_arg none_block_pass
 %type <BlockArgNode> opt_f_block_arg f_block_arg
 %type <IterNode> brace_block do_block cmd_brace_block
    // ENEBO: missing mhls_entry
 %type <MultipleAsgn19Node> mlhs mlhs_basic 
 %type <RescueBodyNode> opt_rescue
 %type <AssignableNode> var_lhs
 %type <LiteralNode> fsym
 %type <Node> fitem
    // ENEBO: begin all new types
 %type <Node> f_arg_item
 %type <Node> bv_decls opt_bv_decl lambda_body 
 %type <LambdaNode> lambda
 %type <Node> mlhs_inner f_block_opt for_var
 %type <Node> opt_call_args f_marg f_margs
 %type <Token> bvar
    // ENEBO: end all new types
 
 %type <Token> rparen rbracket reswords f_bad_arg
 %type <Node> top_compstmt top_stmts top_stmt
 
 /*
  *    precedence table
  */
 
 %nonassoc tLOWEST
 %nonassoc tLBRACE_ARG
 
 %nonassoc  kIF_MOD kUNLESS_MOD kWHILE_MOD kUNTIL_MOD
 %left  kOR kAND
 %right kNOT
 %nonassoc kDEFINED
 %right '=' tOP_ASGN
 %left kRESCUE_MOD
 %right '?' ':'
 %nonassoc tDOT2 tDOT3
 %left  tOROP
 %left  tANDOP
 %nonassoc  tCMP tEQ tEQQ tNEQ tMATCH tNMATCH
 %left  tGT tGEQ tLT tLEQ
 %left  tPIPE tCARET
 %left  tAMPER2
 %left  tLSHFT tRSHFT
 %left  tPLUS tMINUS
 %left  tSTAR2 tDIVIDE tPERCENT
 %right tUMINUS_NUM tUMINUS
 %right tPOW
 %right tBANG tTILDE tUPLUS
 
    //%token <Integer> tLAST_TOKEN
 
 %%
 program       : {
                   lexer.setState(LexState.EXPR_BEG);
                   support.initTopLocalVariables();
               } top_compstmt {
   // ENEBO: Removed !compile_for_eval which probably is to reduce warnings
                   if ($2 != null) {
                       /* last expression should not be void */
                       if ($2 instanceof BlockNode) {
                           support.checkUselessStatement($<BlockNode>2.getLast());
                       } else {
                           support.checkUselessStatement($2);
                       }
                   }
                   support.getResult().setAST(support.addRootNode($2, support.getPosition($2)));
               }
 
 top_compstmt  : top_stmts opt_terms {
                   if ($1 instanceof BlockNode) {
                       support.checkUselessStatements($<BlockNode>1);
                   }
                   $$ = $1;
               }
 
 top_stmts     : none
               | top_stmt {
                     $$ = support.newline_node($1, support.getPosition($1));
               }
               | top_stmts terms top_stmt {
                     $$ = support.appendToBlock($1, support.newline_node($3, support.getPosition($3)));
               }
               | error top_stmt {
                     $$ = $2;
               }
 
 top_stmt      : stmt
               | klBEGIN {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("BEGIN in method");
                     }
               } tLCURLY top_compstmt tRCURLY {
                     support.getResult().addBeginNode(new PreExe19Node($1.getPosition(), support.getCurrentScope(), $4));
                     $$ = null;
               }
 
 bodystmt      : compstmt opt_rescue opt_else opt_ensure {
                   Node node = $1;
 
                   if ($2 != null) {
                       node = new RescueNode(support.getPosition($1), $1, $2, $3);
                   } else if ($3 != null) {
                       support.warn(ID.ELSE_WITHOUT_RESCUE, support.getPosition($1), "else without rescue is useless");
                       node = support.appendToBlock($1, $3);
                   }
                   if ($4 != null) {
                       if (node == null) node = NilImplicitNode.NIL;
                       node = new EnsureNode(support.getPosition($1), node, $4);
                   }
 
                   $$ = node;
                 }
 
 compstmt        : stmts opt_terms {
                     if ($1 instanceof BlockNode) {
                         support.checkUselessStatements($<BlockNode>1);
                     }
                     $$ = $1;
                 }
 
 stmts           : none
                 | stmt {
                     $$ = support.newline_node($1, support.getPosition($1));
                 }
                 | stmts terms stmt {
                     $$ = support.appendToBlock($1, support.newline_node($3, support.getPosition($3)));
                 }
                 | error stmt {
                     $$ = $2;
                 }
 
 stmt            : kALIAS fitem {
                     lexer.setState(LexState.EXPR_FNAME);
                 } fitem {
                     $$ = support.newAlias($1.getPosition(), $2, $4);
                 }
                 | kALIAS tGVAR tGVAR {
                     $$ = new VAliasNode($1.getPosition(), (String) $2.getValue(), (String) $3.getValue());
                 }
                 | kALIAS tGVAR tBACK_REF {
                     $$ = new VAliasNode($1.getPosition(), (String) $2.getValue(), "$" + $<BackRefNode>3.getType());
                 }
                 | kALIAS tGVAR tNTH_REF {
                     support.yyerror("can't make alias for the number variables");
                 }
                 | kUNDEF undef_list {
                     $$ = $2;
                 }
                 | stmt kIF_MOD expr_value {
                     $$ = new IfNode(support.getPosition($1), support.getConditionNode($3), $1, null);
                 }
                 | stmt kUNLESS_MOD expr_value {
                     $$ = new IfNode(support.getPosition($1), support.getConditionNode($3), null, $1);
                 }
                 | stmt kWHILE_MOD expr_value {
                     if ($1 != null && $1 instanceof BeginNode) {
                         $$ = new WhileNode(support.getPosition($1), support.getConditionNode($3), $<BeginNode>1.getBodyNode(), false);
                     } else {
                         $$ = new WhileNode(support.getPosition($1), support.getConditionNode($3), $1, true);
                     }
                 }
                 | stmt kUNTIL_MOD expr_value {
                     if ($1 != null && $1 instanceof BeginNode) {
                         $$ = new UntilNode(support.getPosition($1), support.getConditionNode($3), $<BeginNode>1.getBodyNode(), false);
                     } else {
                         $$ = new UntilNode(support.getPosition($1), support.getConditionNode($3), $1, true);
                     }
                 }
                 | stmt kRESCUE_MOD stmt {
                     Node body = $3 == null ? NilImplicitNode.NIL : $3;
                     $$ = new RescueNode(support.getPosition($1), $1, new RescueBodyNode(support.getPosition($1), null, body, null), null);
                 }
                 | klEND tLCURLY compstmt tRCURLY {
                     if (support.isInDef() || support.isInSingle()) {
                         support.warn(ID.END_IN_METHOD, $1.getPosition(), "END in method; use at_exit");
                     }
                     $$ = new PostExeNode($1.getPosition(), $3);
                 }
                 | command_asgn
                 | mlhs '=' command_call {
                     support.checkExpression($3);
                     $1.setValueNode($3);
                     $$ = $1;
                 }
                 | var_lhs tOP_ASGN command_call {
                     support.checkExpression($3);
 
                     ISourcePosition pos = $1.getPosition();
                     String asgnOp = (String) $2.getValue();
                     if (asgnOp.equals("||")) {
                         $1.setValueNode($3);
                         $$ = new OpAsgnOrNode(pos, support.gettable2($1), $1);
                     } else if (asgnOp.equals("&&")) {
                         $1.setValueNode($3);
                         $$ = new OpAsgnAndNode(pos, support.gettable2($1), $1);
                     } else {
                         $1.setValueNode(support.getOperatorCallNode(support.gettable2($1), asgnOp, $3));
                         $1.setPosition(pos);
                         $$ = $1;
                     }
                 }
                 | primary_value '[' opt_call_args rbracket tOP_ASGN command_call {
   // FIXME: arg_concat logic missing for opt_call_args
                     $$ = support.new_opElementAsgnNode(support.getPosition($1), $1, (String) $5.getValue(), $3, $6);
                 }
                 | primary_value tDOT tIDENTIFIER tOP_ASGN command_call {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
                 }
                 | primary_value tDOT tCONSTANT tOP_ASGN command_call {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
                 }
                 | primary_value tCOLON2 tIDENTIFIER tOP_ASGN command_call {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
                 }
                 | backref tOP_ASGN command_call {
                     support.backrefAssignError($1);
                 }
                 | lhs '=' mrhs {
                     $$ = support.node_assign($1, $3);
                 }
                 | mlhs '=' arg_value {
                     $1.setValueNode($3);
                     $$ = $1;
                 }
                 | mlhs '=' mrhs {
                     $<AssignableNode>1.setValueNode($3);
                     $$ = $1;
                     $1.setPosition(support.getPosition($1));
                 }
                 | expr
 
 command_asgn    : lhs '=' command_call {
                     support.checkExpression($3);
                     $$ = support.node_assign($1, $3);
                 }
                 | lhs '=' command_asgn {
                     support.checkExpression($3);
                     $$ = support.node_assign($1, $3);
                 }
 
 // Node:expr *CURRENT* all but arg so far
 expr            : command_call
                 | expr kAND expr {
                     $$ = support.newAndNode($2.getPosition(), $1, $3);
                 }
                 | expr kOR expr {
                     $$ = support.newOrNode($2.getPosition(), $1, $3);
                 }
                 | kNOT opt_nl expr {
                     $$ = support.getOperatorCallNode(support.getConditionNode($3), "!");
                 }
                 | tBANG command_call {
                     $$ = support.getOperatorCallNode(support.getConditionNode($2), "!");
                 }
                 | arg
 
 expr_value      : expr {
                     support.checkExpression($1);
                 }
 
 // Node:command - call with or with block on end [!null]
 command_call    : command
                 | block_command
                 | kRETURN call_args {
                     $$ = new ReturnNode($1.getPosition(), support.ret_args($2, $1.getPosition()));
                 }
                 | kBREAK call_args {
                     $$ = new BreakNode($1.getPosition(), support.ret_args($2, $1.getPosition()));
                 }
                 | kNEXT call_args {
                     $$ = new NextNode($1.getPosition(), support.ret_args($2, $1.getPosition()));
                 }
 
 // Node:block_command - A call with a block (foo.bar {...}, foo::bar {...}, bar {...}) [!null]
 block_command   : block_call
                 | block_call tDOT operation2 command_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | block_call tCOLON2 operation2 command_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
 
 // :brace_block - [!null]
 cmd_brace_block : tLBRACE_ARG {
                     support.pushBlockScope();
                 } opt_block_param compstmt tRCURLY {
                     $$ = new IterNode($1.getPosition(), $3, $4, support.getCurrentScope());
                     support.popCurrentScope();
                 }
 
 // Node:command - fcall/call/yield/super [!null]
 command        : operation command_args %prec tLOWEST {
                     $$ = support.new_fcall($1, $2, null);
                 }
                 | operation command_args cmd_brace_block {
                     $$ = support.new_fcall($1, $2, $3);
                 }
                 | primary_value tDOT operation2 command_args %prec tLOWEST {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | primary_value tDOT operation2 command_args cmd_brace_block {
                     $$ = support.new_call($1, $3, $4, $5); 
                 }
                 | primary_value tCOLON2 operation2 command_args %prec tLOWEST {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | primary_value tCOLON2 operation2 command_args cmd_brace_block {
                     $$ = support.new_call($1, $3, $4, $5);
                 }
                 | kSUPER command_args {
                     $$ = support.new_super($2, $1); // .setPosFrom($2);
                 }
                 | kYIELD command_args {
                     $$ = support.new_yield($1.getPosition(), $2);
                 }
 
 // MultipleAssig19Node:mlhs - [!null]
 mlhs            : mlhs_basic
                 | tLPAREN mlhs_inner rparen {
                     $$ = $2;
                 }
 
 // MultipleAssign19Node:mlhs_entry - mlhs w or w/o parens [!null]
 mlhs_inner      : mlhs_basic {
                     $$ = $1;
                 }
                 | tLPAREN mlhs_inner rparen {
                     $$ = new MultipleAsgn19Node($1.getPosition(), support.newArrayNode($1.getPosition(), $2), null, null);
                 }
 
 // MultipleAssign19Node:mlhs_basic - multiple left hand side (basic because used in multiple context) [!null]
 mlhs_basic      : mlhs_head {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, null, null);
                 }
                 | mlhs_head mlhs_item {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1.add($2), null, null);
                 }
                 | mlhs_head tSTAR mlhs_node {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, $3, (ListNode) null);
                 }
                 | mlhs_head tSTAR mlhs_node ',' mlhs_post {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, $3, $5);
                 }
                 | mlhs_head tSTAR {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, new StarNode(lexer.getPosition()), null);
                 }
                 | mlhs_head tSTAR ',' mlhs_post {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, new StarNode(lexer.getPosition()), $4);
                 }
                 | tSTAR mlhs_node {
                     $$ = new MultipleAsgn19Node($1.getPosition(), null, $2, null);
                 }
                 | tSTAR mlhs_node ',' mlhs_post {
                     $$ = new MultipleAsgn19Node($1.getPosition(), null, $2, $4);
                 }
                 | tSTAR {
                       $$ = new MultipleAsgn19Node($1.getPosition(), null, new StarNode(lexer.getPosition()), null);
                 }
                 | tSTAR ',' mlhs_post {
                       $$ = new MultipleAsgn19Node($1.getPosition(), null, new StarNode(lexer.getPosition()), $3);
                 }
 
 mlhs_item       : mlhs_node
                 | tLPAREN mlhs_inner rparen {
                     $$ = $2;
                 }
 
 // Set of mlhs terms at front of mlhs (a, *b, d, e = arr  # a is head)
 mlhs_head       : mlhs_item ',' {
                     $$ = support.newArrayNode($1.getPosition(), $1);
                 }
                 | mlhs_head mlhs_item ',' {
                     $$ = $1.add($2);
                 }
 
 // Set of mlhs terms at end of mlhs (a, *b, d, e = arr  # d,e is post)
 mlhs_post       : mlhs_item {
                     $$ = support.newArrayNode($1.getPosition(), $1);
                 }
                 | mlhs_post ',' mlhs_item {
                     $$ = $1.add($3);
                 }
 
 mlhs_node       : variable {
                     $$ = support.assignable($1, NilImplicitNode.NIL);
                 }
                 | primary_value '[' opt_call_args rbracket {
                     $$ = support.aryset($1, $3);
                 }
                 | primary_value tDOT tIDENTIFIER {
                     $$ = support.attrset($1, (String) $3.getValue());
                 }
                 | primary_value tCOLON2 tIDENTIFIER {
                     $$ = support.attrset($1, (String) $3.getValue());
                 }
                 | primary_value tDOT tCONSTANT {
                     $$ = support.attrset($1, (String) $3.getValue());
                 }
                 | primary_value tCOLON2 tCONSTANT {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("dynamic constant assignment");
                     }
 
                     ISourcePosition position = support.getPosition($1);
 
                     $$ = new ConstDeclNode(position, null, support.new_colon2(position, $1, (String) $3.getValue()), NilImplicitNode.NIL);
                 }
                 | tCOLON3 tCONSTANT {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("dynamic constant assignment");
                     }
 
                     ISourcePosition position = $1.getPosition();
 
                     $$ = new ConstDeclNode(position, null, support.new_colon3(position, (String) $2.getValue()), NilImplicitNode.NIL);
                 }
                 | backref {
                     support.backrefAssignError($1);
                 }
 
 lhs             : variable {
                       // if (!($$ = assignable($1, 0))) $$ = NEW_BEGIN(0);
                     $$ = support.assignable($1, NilImplicitNode.NIL);
                 }
                 | primary_value '[' opt_call_args rbracket {
                     $$ = support.aryset($1, $3);
                 }
                 | primary_value tDOT tIDENTIFIER {
                     $$ = support.attrset($1, (String) $3.getValue());
                 }
                 | primary_value tCOLON2 tIDENTIFIER {
                     $$ = support.attrset($1, (String) $3.getValue());
                 }
                 | primary_value tDOT tCONSTANT {
                     $$ = support.attrset($1, (String) $3.getValue());
                 }
                 | primary_value tCOLON2 tCONSTANT {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("dynamic constant assignment");
                     }
 
                     ISourcePosition position = support.getPosition($1);
 
                     $$ = new ConstDeclNode(position, null, support.new_colon2(position, $1, (String) $3.getValue()), NilImplicitNode.NIL);
                 }
                 | tCOLON3 tCONSTANT {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("dynamic constant assignment");
                     }
 
                     ISourcePosition position = $1.getPosition();
 
                     $$ = new ConstDeclNode(position, null, support.new_colon3(position, (String) $2.getValue()), NilImplicitNode.NIL);
                 }
                 | backref {
                     support.backrefAssignError($1);
                 }
 
 cname           : tIDENTIFIER {
                     support.yyerror("class/module name must be CONSTANT");
                 }
                 | tCONSTANT
 
 cpath           : tCOLON3 cname {
                     $$ = support.new_colon3($1.getPosition(), (String) $2.getValue());
                 }
                 | cname {
                     $$ = support.new_colon2($1.getPosition(), null, (String) $1.getValue());
                 }
                 | primary_value tCOLON2 cname {
                     $$ = support.new_colon2(support.getPosition($1), $1, (String) $3.getValue());
                 }
 
 // Token:fname - A function name [!null]
 fname          : tIDENTIFIER | tCONSTANT | tFID 
                | op {
                    lexer.setState(LexState.EXPR_ENDFN);
                    $$ = $1;
                }
                | reswords {
                    lexer.setState(LexState.EXPR_ENDFN);
                    $$ = $1;
                }
 
 // LiteralNode:fsym
 fsym           : fname {
                     $$ = new LiteralNode($1);
                 }
                 | symbol {
                     $$ = new LiteralNode($1);
                 }
 
 // Node:fitem
 fitem           : fsym {
                     $$ = $1;
                 }
                 | dsym {
                     $$ = $1;
                 }
 
 undef_list      : fitem {
                     $$ = support.newUndef($1.getPosition(), $1);
                 }
                 | undef_list ',' {
                     lexer.setState(LexState.EXPR_FNAME);
                 } fitem {
                     $$ = support.appendToBlock($1, support.newUndef($1.getPosition(), $4));
                 }
 
 // Token:op
 op              : tPIPE | tCARET | tAMPER2 | tCMP | tEQ | tEQQ | tMATCH
                 | tNMATCH | tGT | tGEQ | tLT | tLEQ | tNEQ | tLSHFT | tRSHFT
                 | tPLUS | tMINUS | tSTAR2 | tSTAR | tDIVIDE | tPERCENT | tPOW
                 | tBANG | tTILDE | tUPLUS | tUMINUS | tAREF | tASET | tBACK_REF2
 
 // Token:op
 reswords        : k__LINE__ | k__FILE__ | k__ENCODING__ | klBEGIN | klEND
                 | kALIAS | kAND | kBEGIN | kBREAK | kCASE | kCLASS | kDEF
                 | kDEFINED | kDO | kELSE | kELSIF | kEND | kENSURE | kFALSE
                 | kFOR | kIN | kMODULE | kNEXT | kNIL | kNOT
                 | kOR | kREDO | kRESCUE | kRETRY | kRETURN | kSELF | kSUPER
                 | kTHEN | kTRUE | kUNDEF | kWHEN | kYIELD
                 | kIF_MOD | kUNLESS_MOD | kWHILE_MOD | kUNTIL_MOD | kRESCUE_MOD
 
 arg             : lhs '=' arg {
                     $$ = support.node_assign($1, $3);
                     // FIXME: Consider fixing node_assign itself rather than single case
                     $<Node>$.setPosition(support.getPosition($1));
                 }
                 | lhs '=' arg kRESCUE_MOD arg {
                     ISourcePosition position = $4.getPosition();
                     Node body = $5 == null ? NilImplicitNode.NIL : $5;
                     $$ = support.node_assign($1, new RescueNode(position, $3, new RescueBodyNode(position, null, body, null), null));
                 }
                 | var_lhs tOP_ASGN arg {
                     support.checkExpression($3);
 
                     ISourcePosition pos = $1.getPosition();
                     String asgnOp = (String) $2.getValue();
                     if (asgnOp.equals("||")) {
                         $1.setValueNode($3);
                         $$ = new OpAsgnOrNode(pos, support.gettable2($1), $1);
                     } else if (asgnOp.equals("&&")) {
                         $1.setValueNode($3);
                         $$ = new OpAsgnAndNode(pos, support.gettable2($1), $1);
                     } else {
                         $1.setValueNode(support.getOperatorCallNode(support.gettable2($1), asgnOp, $3));
                         $1.setPosition(pos);
                         $$ = $1;
                     }
                 }
                 | var_lhs tOP_ASGN arg kRESCUE_MOD arg {
                     support.checkExpression($3);
                     ISourcePosition pos = $4.getPosition();
                     Node body = $5 == null ? NilImplicitNode.NIL : $5;
                     Node rest;
 
                     pos = $1.getPosition();
                     String asgnOp = (String) $2.getValue();
                     if (asgnOp.equals("||")) {
                         $1.setValueNode($3);
                         rest = new OpAsgnOrNode(pos, support.gettable2($1), $1);
                     } else if (asgnOp.equals("&&")) {
                         $1.setValueNode($3);
                         rest = new OpAsgnAndNode(pos, support.gettable2($1), $1);
                     } else {
                         $1.setValueNode(support.getOperatorCallNode(support.gettable2($1), asgnOp, $3));
                         $1.setPosition(pos);
                         rest = $1;
                     }
 
                     $$ = new RescueNode($4.getPosition(), rest, new RescueBodyNode($4.getPosition(), null, body, null), null);
                 }
                 | primary_value '[' opt_call_args rbracket tOP_ASGN arg {
   // FIXME: arg_concat missing for opt_call_args
                     $$ = support.new_opElementAsgnNode(support.getPosition($1), $1, (String) $5.getValue(), $3, $6);
                 }
                 | primary_value tDOT tIDENTIFIER tOP_ASGN arg {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
                 }
                 | primary_value tDOT tCONSTANT tOP_ASGN arg {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
                 }
                 | primary_value tCOLON2 tIDENTIFIER tOP_ASGN arg {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
                 }
                 | primary_value tCOLON2 tCONSTANT tOP_ASGN arg {
                     support.yyerror("constant re-assignment");
                 }
                 | tCOLON3 tCONSTANT tOP_ASGN arg {
                     support.yyerror("constant re-assignment");
                 }
                 | backref tOP_ASGN arg {
                     support.backrefAssignError($1);
                 }
                 | arg tDOT2 arg {
                     support.checkExpression($1);
                     support.checkExpression($3);
     
                     boolean isLiteral = $1 instanceof FixnumNode && $3 instanceof FixnumNode;
                     $$ = new DotNode(support.getPosition($1), $1, $3, false, isLiteral);
                 }
                 | arg tDOT3 arg {
                     support.checkExpression($1);
                     support.checkExpression($3);
 
                     boolean isLiteral = $1 instanceof FixnumNode && $3 instanceof FixnumNode;
                     $$ = new DotNode(support.getPosition($1), $1, $3, true, isLiteral);
                 }
                 | arg tPLUS arg {
                     $$ = support.getOperatorCallNode($1, "+", $3, lexer.getPosition());
                 }
                 | arg tMINUS arg {
                     $$ = support.getOperatorCallNode($1, "-", $3, lexer.getPosition());
                 }
                 | arg tSTAR2 arg {
                     $$ = support.getOperatorCallNode($1, "*", $3, lexer.getPosition());
                 }
                 | arg tDIVIDE arg {
                     $$ = support.getOperatorCallNode($1, "/", $3, lexer.getPosition());
                 }
                 | arg tPERCENT arg {
                     $$ = support.getOperatorCallNode($1, "%", $3, lexer.getPosition());
                 }
                 | arg tPOW arg {
                     $$ = support.getOperatorCallNode($1, "**", $3, lexer.getPosition());
                 }
                 | tUMINUS_NUM tINTEGER tPOW arg {
                     $$ = support.getOperatorCallNode(support.getOperatorCallNode($2, "**", $4, lexer.getPosition()), "-@");
                 }
                 | tUMINUS_NUM tFLOAT tPOW arg {
                     $$ = support.getOperatorCallNode(support.getOperatorCallNode($2, "**", $4, lexer.getPosition()), "-@");
                 }
                 | tUPLUS arg {
                     $$ = support.getOperatorCallNode($2, "+@");
                 }
                 | tUMINUS arg {
                     $$ = support.getOperatorCallNode($2, "-@");
                 }
                 | arg tPIPE arg {
                     $$ = support.getOperatorCallNode($1, "|", $3, lexer.getPosition());
                 }
                 | arg tCARET arg {
                     $$ = support.getOperatorCallNode($1, "^", $3, lexer.getPosition());
                 }
                 | arg tAMPER2 arg {
                     $$ = support.getOperatorCallNode($1, "&", $3, lexer.getPosition());
                 }
                 | arg tCMP arg {
                     $$ = support.getOperatorCallNode($1, "<=>", $3, lexer.getPosition());
                 }
                 | arg tGT arg {
                     $$ = support.getOperatorCallNode($1, ">", $3, lexer.getPosition());
                 }
                 | arg tGEQ arg {
                     $$ = support.getOperatorCallNode($1, ">=", $3, lexer.getPosition());
                 }
                 | arg tLT arg {
                     $$ = support.getOperatorCallNode($1, "<", $3, lexer.getPosition());
                 }
                 | arg tLEQ arg {
                     $$ = support.getOperatorCallNode($1, "<=", $3, lexer.getPosition());
                 }
                 | arg tEQ arg {
                     $$ = support.getOperatorCallNode($1, "==", $3, lexer.getPosition());
                 }
                 | arg tEQQ arg {
                     $$ = support.getOperatorCallNode($1, "===", $3, lexer.getPosition());
                 }
                 | arg tNEQ arg {
                     $$ = support.getOperatorCallNode($1, "!=", $3, lexer.getPosition());
                 }
                 | arg tMATCH arg {
                     $$ = support.getMatchNode($1, $3);
                   /* ENEBO
                         $$ = match_op($1, $3);
                         if (nd_type($1) == NODE_LIT && TYPE($1->nd_lit) == T_REGEXP) {
                             $$ = reg_named_capture_assign($1->nd_lit, $$);
                         }
                   */
                 }
                 | arg tNMATCH arg {
-                    $$ = new NotNode(support.getPosition($1), support.getMatchNode($1, $3));
+                    $$ = support.getOperatorCallNode($1, "!~", $3, lexer.getPosition());
                 }
                 | tBANG arg {
                     $$ = support.getOperatorCallNode(support.getConditionNode($2), "!");
                 }
                 | tTILDE arg {
                     $$ = support.getOperatorCallNode($2, "~");
                 }
                 | arg tLSHFT arg {
                     $$ = support.getOperatorCallNode($1, "<<", $3, lexer.getPosition());
                 }
                 | arg tRSHFT arg {
                     $$ = support.getOperatorCallNode($1, ">>", $3, lexer.getPosition());
                 }
                 | arg tANDOP arg {
                     $$ = support.newAndNode($2.getPosition(), $1, $3);
                 }
                 | arg tOROP arg {
                     $$ = support.newOrNode($2.getPosition(), $1, $3);
                 }
                 | kDEFINED opt_nl arg {
                     // ENEBO: arg surrounded by in_defined set/unset
                     $$ = new DefinedNode($1.getPosition(), $3);
                 }
                 | arg '?' arg opt_nl ':' arg {
                     $$ = new IfNode(support.getPosition($1), support.getConditionNode($1), $3, $6);
                 }
                 | primary {
                     $$ = $1;
                 }
 
 arg_value       : arg {
                     support.checkExpression($1);
                     $$ = $1 != null ? $1 : NilImplicitNode.NIL;
                 }
 
 aref_args       : none
                 | args trailer {
                     $$ = $1;
                 }
                 | args ',' assocs trailer {
                     $$ = support.arg_append($1, new Hash19Node(lexer.getPosition(), $3));
                 }
                 | assocs trailer {
                     $$ = support.newArrayNode($1.getPosition(), new Hash19Node(lexer.getPosition(), $1));
                 }
 
 paren_args      : tLPAREN2 opt_call_args rparen {
                     $$ = $2;
                     if ($$ != null) $<Node>$.setPosition($1.getPosition());
                 }
 
 opt_paren_args  : none | paren_args
 
 opt_call_args   : none | call_args
 
 // [!null]
 call_args       : command {
                     $$ = support.newArrayNode(support.getPosition($1), $1);
                 }
                 | args opt_block_arg {
                     $$ = support.arg_blk_pass($1, $2);
                 }
                 | assocs opt_block_arg {
                     $$ = support.newArrayNode($1.getPosition(), new Hash19Node(lexer.getPosition(), $1));
                     $$ = support.arg_blk_pass((Node)$$, $2);
                 }
                 | args ',' assocs opt_block_arg {
                     $$ = support.arg_append($1, new Hash19Node(lexer.getPosition(), $3));
                     $$ = support.arg_blk_pass((Node)$$, $4);
                 }
                 | block_arg {
                 }
 
 command_args    : /* none */ {
                     $$ = Long.valueOf(lexer.getCmdArgumentState().begin());
                 } call_args {
                     lexer.getCmdArgumentState().reset($<Long>1.longValue());
                     $$ = $2;
                 }
 
 block_arg       : tAMPER arg_value {
                     $$ = new BlockPassNode($1.getPosition(), $2);
                 }
 
 opt_block_arg   : ',' block_arg {
                     $$ = $2;
                 }
                 | ',' {
                     $$ = null;
                 }
                 | none_block_pass
 
 // [!null]
 args            : arg_value {
                     ISourcePosition pos = $1 == null ? lexer.getPosition() : $1.getPosition();
                     $$ = support.newArrayNode(pos, $1);
                 }
                 | tSTAR arg_value {
                     $$ = support.newSplatNode($1.getPosition(), $2);
                 }
                 | args ',' arg_value {
                     Node node = support.splat_array($1);
 
                     if (node != null) {
                         $$ = support.list_append(node, $3);
                     } else {
                         $$ = support.arg_append($1, $3);
                     }
                 }
                 | args ',' tSTAR arg_value {
                     Node node = null;
 
                     // FIXME: lose syntactical elements here (and others like this)
                     if ($4 instanceof ArrayNode &&
                         (node = support.splat_array($1)) != null) {
                         $$ = support.list_concat(node, $4);
                     } else {
                         $$ = support.arg_concat(support.getPosition($1), $1, $4);
                     }
                 }
 
 mrhs            : args ',' arg_value {
                     Node node = support.splat_array($1);
 
                     if (node != null) {
                         $$ = support.list_append(node, $3);
                     } else {
                         $$ = support.arg_append($1, $3);
                     }
                 }
                 | args ',' tSTAR arg_value {
                     Node node = null;
 
                     if ($4 instanceof ArrayNode &&
                         (node = support.splat_array($1)) != null) {
                         $$ = support.list_concat(node, $4);
                     } else {
                         $$ = support.arg_concat($1.getPosition(), $1, $4);
                     }
                 }
                 | tSTAR arg_value {
                      $$ = support.newSplatNode(support.getPosition($1), $2);  
                 }
 
 primary         : literal
                 | strings
                 | xstring
                 | regexp
                 | words
                 | qwords
                 | var_ref
                 | backref
                 | tFID {
                     $$ = new FCallNoArgNode($1.getPosition(), (String) $1.getValue());
                 }
                 | kBEGIN bodystmt kEND {
                     $$ = new BeginNode(support.getPosition($1), $2 == null ? NilImplicitNode.NIL : $2);
                 }
                 | tLPAREN_ARG expr {
                     lexer.setState(LexState.EXPR_ENDARG); 
                 } rparen {
                     support.warning(ID.GROUPED_EXPRESSION, $1.getPosition(), "(...) interpreted as grouped expression");
                     $$ = $2;
                 }
                 | tLPAREN compstmt tRPAREN {
                     if ($2 != null) {
                         // compstmt position includes both parens around it
                         ((ISourcePositionHolder) $2).setPosition($1.getPosition());
                         $$ = $2;
                     } else {
                         $$ = new NilNode($1.getPosition());
                     }
                 }
                 | primary_value tCOLON2 tCONSTANT {
                     $$ = support.new_colon2(support.getPosition($1), $1, (String) $3.getValue());
                 }
                 | tCOLON3 tCONSTANT {
                     $$ = support.new_colon3($1.getPosition(), (String) $2.getValue());
                 }
                 | tLBRACK aref_args tRBRACK {
                     ISourcePosition position = $1.getPosition();
                     if ($2 == null) {
                         $$ = new ZArrayNode(position); /* zero length array */
                     } else {
                         $$ = $2;
                         $<ISourcePositionHolder>$.setPosition(position);
                     }
                 }
                 | tLBRACE assoc_list tRCURLY {
                     $$ = new Hash19Node($1.getPosition(), $2);
                 }
                 | kRETURN {
                     $$ = new ReturnNode($1.getPosition(), NilImplicitNode.NIL);
                 }
                 | kYIELD tLPAREN2 call_args rparen {
                     $$ = support.new_yield($1.getPosition(), $3);
                 }
                 | kYIELD tLPAREN2 rparen {
                     $$ = new ZYieldNode($1.getPosition());
                 }
                 | kYIELD {
                     $$ = new ZYieldNode($1.getPosition());
                 }
                 | kDEFINED opt_nl tLPAREN2 expr rparen {
                     $$ = new DefinedNode($1.getPosition(), $4);
                 }
                 | kNOT tLPAREN2 expr rparen {
                     $$ = support.getOperatorCallNode(support.getConditionNode($3), "!");
                 }
                 | kNOT tLPAREN2 rparen {
                     $$ = support.getOperatorCallNode(NilImplicitNode.NIL, "!");
                 }
                 | operation brace_block {
                     $$ = new FCallNoArgBlockNode($1.getPosition(), (String) $1.getValue(), $2);
                 }
                 | method_call
                 | method_call brace_block {
                     if ($1 != null && 
                           $<BlockAcceptingNode>1.getIterNode() instanceof BlockPassNode) {
                         throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, $1.getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                     }
                     $$ = $<BlockAcceptingNode>1.setIterNode($2);
                     $<Node>$.setPosition($1.getPosition());
                 }
                 | tLAMBDA lambda {
                     $$ = $2;
                 }
                 | kIF expr_value then compstmt if_tail kEND {
                     $$ = new IfNode($1.getPosition(), support.getConditionNode($2), $4, $5);
                 }
                 | kUNLESS expr_value then compstmt opt_else kEND {
                     $$ = new IfNode($1.getPosition(), support.getConditionNode($2), $5, $4);
                 }
                 | kWHILE {
                     lexer.getConditionState().begin();
                 } expr_value do {
                     lexer.getConditionState().end();
                 } compstmt kEND {
                     Node body = $6 == null ? NilImplicitNode.NIL : $6;
                     $$ = new WhileNode($1.getPosition(), support.getConditionNode($3), body);
                 }
                 | kUNTIL {
                   lexer.getConditionState().begin();
                 } expr_value do {
                   lexer.getConditionState().end();
                 } compstmt kEND {
                     Node body = $6 == null ? NilImplicitNode.NIL : $6;
                     $$ = new UntilNode($1.getPosition(), support.getConditionNode($3), body);
                 }
                 | kCASE expr_value opt_terms case_body kEND {
                     $$ = support.newCaseNode($1.getPosition(), $2, $4);
                 }
                 | kCASE opt_terms case_body kEND {
                     $$ = support.newCaseNode($1.getPosition(), null, $3);
                 }
                 | kFOR for_var kIN {
                     lexer.getConditionState().begin();
                 } expr_value do {
                     lexer.getConditionState().end();
                 } compstmt kEND {
                       // ENEBO: Lots of optz in 1.9 parser here
                     $$ = new ForNode($1.getPosition(), $2, $8, $5, support.getCurrentScope());
                 }
                 | kCLASS cpath superclass {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("class definition in method body");
                     }
                     support.pushLocalScope();
                 } bodystmt kEND {
                     Node body = $5 == null ? NilImplicitNode.NIL : $5;
 
                     $$ = new ClassNode($1.getPosition(), $<Colon3Node>2, support.getCurrentScope(), body, $3);
                     support.popCurrentScope();
                 }
                 | kCLASS tLSHFT expr {
                     $$ = Boolean.valueOf(support.isInDef());
                     support.setInDef(false);
                 } term {
                     $$ = Integer.valueOf(support.getInSingle());
                     support.setInSingle(0);
                     support.pushLocalScope();
                 } bodystmt kEND {
                     $$ = new SClassNode($1.getPosition(), $3, support.getCurrentScope(), $7);
                     support.popCurrentScope();
                     support.setInDef($<Boolean>4.booleanValue());
                     support.setInSingle($<Integer>6.intValue());
                 }
                 | kMODULE cpath {
                     if (support.isInDef() || support.isInSingle()) { 
                         support.yyerror("module definition in method body");
                     }
                     support.pushLocalScope();
                 } bodystmt kEND {
                     Node body = $4 == null ? NilImplicitNode.NIL : $4;
 
                     $$ = new ModuleNode($1.getPosition(), $<Colon3Node>2, support.getCurrentScope(), body);
                     support.popCurrentScope();
                 }
                 | kDEF fname {
                     support.setInDef(true);
                     support.pushLocalScope();
                 } f_arglist bodystmt kEND {
                     // TODO: We should use implicit nil for body, but problem (punt til later)
                     Node body = $5; //$5 == null ? NilImplicitNode.NIL : $5;
 
                     $$ = new DefnNode($1.getPosition(), new ArgumentNode($2.getPosition(), (String) $2.getValue()), $4, support.getCurrentScope(), body);
                     support.popCurrentScope();
                     support.setInDef(false);
                 }
                 | kDEF singleton dot_or_colon {
                     lexer.setState(LexState.EXPR_FNAME);
                 } fname {
                     support.setInSingle(support.getInSingle() + 1);
                     support.pushLocalScope();
                     lexer.setState(LexState.EXPR_ENDFN); /* force for args */
                 } f_arglist bodystmt kEND {
                     // TODO: We should use implicit nil for body, but problem (punt til later)
                     Node body = $8; //$8 == null ? NilImplicitNode.NIL : $8;
 
                     $$ = new DefsNode($1.getPosition(), $2, new ArgumentNode($5.getPosition(), (String) $5.getValue()), $7, support.getCurrentScope(), body);
                     support.popCurrentScope();
                     support.setInSingle(support.getInSingle() - 1);
                 }
                 | kBREAK {
                     $$ = new BreakNode($1.getPosition(), NilImplicitNode.NIL);
                 }
                 | kNEXT {
                     $$ = new NextNode($1.getPosition(), NilImplicitNode.NIL);
                 }
                 | kREDO {
                     $$ = new RedoNode($1.getPosition());
                 }
                 | kRETRY {
                     $$ = new RetryNode($1.getPosition());
                 }
 
 primary_value   : primary {
                     support.checkExpression($1);
                     $$ = $1;
                     if ($$ == null) $$ = NilImplicitNode.NIL;
                 }
 
 then            : term
                 | kTHEN
                 | term kTHEN
 
 do              : term
                 | kDO_COND
 
 if_tail         : opt_else
                 | kELSIF expr_value then compstmt if_tail {
                     $$ = new IfNode($1.getPosition(), support.getConditionNode($2), $4, $5);
                 }
 
 opt_else        : none
                 | kELSE compstmt {
                     $$ = $2;
                 }
 
 for_var         : lhs
                 | mlhs {
                 }
 
 f_marg          : f_norm_arg {
                      $$ = support.assignable($1, NilImplicitNode.NIL);
                 }
                 | tLPAREN f_margs rparen {
                     $$ = $2;
                 }
 
 // [!null]
 f_marg_list     : f_marg {
                     $$ = support.newArrayNode($1.getPosition(), $1);
                 }
                 | f_marg_list ',' f_marg {
                     $$ = $1.add($3);
                 }
 
 f_margs         : f_marg_list {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, null, null);
                 }
                 | f_marg_list ',' tSTAR f_norm_arg {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, support.assignable($4, null), null);
                 }
                 | f_marg_list ',' tSTAR f_norm_arg ',' f_marg_list {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, support.assignable($4, null), $6);
                 }
                 | f_marg_list ',' tSTAR {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, new StarNode(lexer.getPosition()), null);
                 }
                 | f_marg_list ',' tSTAR ',' f_marg_list {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, new StarNode(lexer.getPosition()), $5);
                 }
                 | tSTAR f_norm_arg {
                     $$ = new MultipleAsgn19Node($1.getPosition(), null, support.assignable($2, null), null);
                 }
                 | tSTAR f_norm_arg ',' f_marg_list {
                     $$ = new MultipleAsgn19Node($1.getPosition(), null, support.assignable($2, null), $4);
                 }
                 | tSTAR {
                     $$ = new MultipleAsgn19Node($1.getPosition(), null, new StarNode(lexer.getPosition()), null);
                 }
                 | tSTAR ',' f_marg_list {
                     $$ = new MultipleAsgn19Node($1.getPosition(), null, null, $3);
                 }
 
 // [!null]
 block_param     : f_arg ',' f_block_optarg ',' f_rest_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, $5, null, $6);
                 }
                 | f_arg ',' f_block_optarg ',' f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, $5, $7, $8);
                 }
                 | f_arg ',' f_block_optarg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, null, null, $4);
                 }
                 | f_arg ',' f_block_optarg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, null, $5, $6);
                 }
                 | f_arg ',' f_rest_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, null, $3, null, $4);
                 }
                 | f_arg ',' {
                     RestArgNode rest = new UnnamedRestArgNode($1.getPosition(), null, support.getCurrentScope().addVariable("*"));
                     $$ = support.new_args($1.getPosition(), $1, null, rest, null, null);
                 }
                 | f_arg ',' f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, null, $3, $5, $6);
                 }
                 | f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, null, null, null, $2);
                 }
                 | f_block_optarg ',' f_rest_arg opt_f_block_arg {
                     $$ = support.new_args(support.getPosition($1), null, $1, $3, null, $4);
                 }
                 | f_block_optarg ',' f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args(support.getPosition($1), null, $1, $3, $5, $6);
                 }
                 | f_block_optarg opt_f_block_arg {
                     $$ = support.new_args(support.getPosition($1), null, $1, null, null, $2);
                 }
                 | f_block_optarg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, $1, null, $3, $4);
                 }
                 | f_rest_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, null, $1, null, $2);
                 }
                 | f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, null, $1, $3, $4);
                 }
                 | f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, null, null, null, $1);
                 }
 
 opt_block_param : none {
     // was $$ = null;
                    $$ = support.new_args(lexer.getPosition(), null, null, null, null, null);
                 }
                 | block_param_def {
                     lexer.commandStart = true;
                     $$ = $1;
                 }
 
 block_param_def : tPIPE opt_bv_decl tPIPE {
                     $$ = support.new_args($1.getPosition(), null, null, null, null, null);
                 }
                 | tOROP {
                     $$ = support.new_args($1.getPosition(), null, null, null, null, null);
                 }
                 | tPIPE block_param opt_bv_decl tPIPE {
                     $$ = $2;
                 }
 
 // shadowed block variables....
 opt_bv_decl     : opt_nl {
                     $$ = null;
                 }
                 | opt_nl ';' bv_decls opt_nl {
                     $$ = null;
                 }
 
 // ENEBO: This is confusing...
 bv_decls        : bvar {
                     $$ = null;
                 }
                 | bv_decls ',' bvar {
                     $$ = null;
                 }
 
 bvar            : tIDENTIFIER {
                     support.new_bv($1);
                 }
                 | f_bad_arg {
                     $$ = null;
                 }
 
 lambda          : /* none */  {
                     support.pushBlockScope();
                     $$ = lexer.getLeftParenBegin();
                     lexer.setLeftParenBegin(lexer.incrementParenNest());
                 } f_larglist lambda_body {
                     $$ = new LambdaNode($2.getPosition(), $2, $3, support.getCurrentScope());
                     support.popCurrentScope();
                     lexer.setLeftParenBegin($<Integer>1);
                 }
 
 f_larglist      : tLPAREN2 f_args opt_bv_decl tRPAREN {
                     $$ = $2;
                     $<ISourcePositionHolder>$.setPosition($1.getPosition());
                 }
                 | f_args opt_bv_decl {
                     $$ = $1;
                 }
 
 lambda_body     : tLAMBEG compstmt tRCURLY {
                     $$ = $2;
                 }
                 | kDO_LAMBDA compstmt kEND {
                     $$ = $2;
                 }
 
 do_block        : kDO_BLOCK {
                     support.pushBlockScope();
                 } opt_block_param compstmt kEND {
                     $$ = new IterNode(support.getPosition($1), $3, $4, support.getCurrentScope());
                     support.popCurrentScope();
                 }
 
 block_call      : command do_block {
                     // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
                     if ($1 instanceof YieldNode) {
                         throw new SyntaxException(PID.BLOCK_GIVEN_TO_YIELD, $1.getPosition(), lexer.getCurrentLine(), "block given to yield");
                     }
                     if ($<BlockAcceptingNode>1.getIterNode() instanceof BlockPassNode) {
                         throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, $1.getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                     }
                     $$ = $<BlockAcceptingNode>1.setIterNode($2);
                     $<Node>$.setPosition($1.getPosition());
                 }
                 | block_call tDOT operation2 opt_paren_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | block_call tCOLON2 operation2 opt_paren_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
 
 // [!null]
 method_call     : operation paren_args {
                     $$ = support.new_fcall($1, $2, null);
                 }
                 | primary_value tDOT operation2 opt_paren_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | primary_value tCOLON2 operation2 paren_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | primary_value tCOLON2 operation3 {
                     $$ = support.new_call($1, $3, null, null);
                 }
                 | primary_value tDOT paren_args {
                     $$ = support.new_call($1, new Token("call", $1.getPosition()), $3, null);
                 }
                 | primary_value tCOLON2 paren_args {
                     $$ = support.new_call($1, new Token("call", $1.getPosition()), $3, null);
                 }
                 | kSUPER paren_args {
                     $$ = support.new_super($2, $1);
                 }
                 | kSUPER {
                     $$ = new ZSuperNode($1.getPosition());
                 }
                 | primary_value '[' opt_call_args rbracket {
                     if ($1 instanceof SelfNode) {
                         $$ = support.new_fcall(new Token("[]", support.getPosition($1)), $3, null);
                     } else {
                         $$ = support.new_call($1, new Token("[]", support.getPosition($1)), $3, null);
                     }
                 }
 
 brace_block     : tLCURLY {
                     support.pushBlockScope();
                 } opt_block_param compstmt tRCURLY {
                     $$ = new IterNode($1.getPosition(), $3, $4, support.getCurrentScope());
                     support.popCurrentScope();
                 }
                 | kDO {
                     support.pushBlockScope();
                 } opt_block_param compstmt kEND {
                     $$ = new IterNode($1.getPosition(), $3, $4, support.getCurrentScope());
                     // FIXME: What the hell is this?
                     $<ISourcePositionHolder>0.setPosition(support.getPosition($<ISourcePositionHolder>0));
                     support.popCurrentScope();
                 }
 
 case_body       : kWHEN args then compstmt cases {
                     $$ = support.newWhenNode($1.getPosition(), $2, $4, $5);
                 }
 
 cases           : opt_else | case_body
 
 opt_rescue      : kRESCUE exc_list exc_var then compstmt opt_rescue {
                     Node node;
                     if ($3 != null) {
                         node = support.appendToBlock(support.node_assign($3, new GlobalVarNode($1.getPosition(), "$!")), $5);
                         if ($5 != null) {
                             node.setPosition(support.unwrapNewlineNode($5).getPosition());
                         }
                     } else {
                         node = $5;
                     }
                     Node body = node == null ? NilImplicitNode.NIL : node;
                     $$ = new RescueBodyNode($1.getPosition(), $2, body, $6);
                 }
                 | { 
                     $$ = null; 
                 }
 
 exc_list        : arg_value {
                     $$ = support.newArrayNode($1.getPosition(), $1);
                 }
                 | mrhs {
                     $$ = support.splat_array($1);
                     if ($$ == null) $$ = $1;
                 }
                 | none
 
 exc_var         : tASSOC lhs {
                     $$ = $2;
                 }
                 | none
 
 opt_ensure      : kENSURE compstmt {
                     $$ = $2;
                 }
                 | none
 
 literal         : numeric
                 | symbol {
                     // FIXME: We may be intern'ing more than once.
                     $$ = new SymbolNode($1.getPosition(), ((String) $1.getValue()).intern());
                 }
                 | dsym
 
 strings         : string {
                     $$ = $1 instanceof EvStrNode ? new DStrNode($1.getPosition(), lexer.getEncoding()).add($1) : $1;
                     /*
                     NODE *node = $1;
                     if (!node) {
                         node = NEW_STR(STR_NEW0());
                     } else {
                         node = evstr2dstr(node);
                     }
                     $$ = node;
                     */
                 }
 
 // [!null]
 string          : tCHAR {
                     ByteList aChar = ByteList.create((String) $1.getValue());
                     aChar.setEncoding(lexer.getEncoding());
                     $$ = lexer.createStrNode($<Token>0.getPosition(), aChar, 0);
                 }
                 | string1 {
                     $$ = $1;
                 }
                 | string string1 {
                     $$ = support.literal_concat($1.getPosition(), $1, $2);
                 }
 
 string1         : tSTRING_BEG string_contents tSTRING_END {
                     $$ = $2;
 
                     $<ISourcePositionHolder>$.setPosition($1.getPosition());
                     int extraLength = ((String) $1.getValue()).length() - 1;
 
                     // We may need to subtract addition offset off of first 
                     // string fragment (we optimistically take one off in
                     // ParserSupport.literal_concat).  Check token length
                     // and subtract as neeeded.
                     if (($2 instanceof DStrNode) && extraLength > 0) {
                       Node strNode = ((DStrNode)$2).get(0);
                     }
                 }
 
 xstring         : tXSTRING_BEG xstring_contents tSTRING_END {
                     ISourcePosition position = $1.getPosition();
 
                     if ($2 == null) {
                         $$ = new XStrNode(position, null);
                     } else if ($2 instanceof StrNode) {
                         $$ = new XStrNode(position, (ByteList) $<StrNode>2.getValue().clone());
                     } else if ($2 instanceof DStrNode) {
                         $$ = new DXStrNode(position, $<DStrNode>2);
 
                         $<Node>$.setPosition(position);
                     } else {
                         $$ = new DXStrNode(position).add($2);
                     }
                 }
 
 regexp          : tREGEXP_BEG xstring_contents tREGEXP_END {
                     $$ = support.newRegexpNode($1.getPosition(), $2, (RegexpNode) $3);
                 }
 
 words           : tWORDS_BEG ' ' tSTRING_END {
                     $$ = new ZArrayNode($1.getPosition());
                 }
                 | tWORDS_BEG word_list tSTRING_END {
                     $$ = $2;
                 }
 
 word_list       : /* none */ {
                     $$ = new ArrayNode(lexer.getPosition());
                 }
                 | word_list word ' ' {
                      $$ = $1.add($2 instanceof EvStrNode ? new DStrNode($1.getPosition(), lexer.getEncoding()).add($2) : $2);
                 }
 
 word            : string_content
                 | word string_content {
                      $$ = support.literal_concat(support.getPosition($1), $1, $2);
                 }
 
 qwords          : tQWORDS_BEG ' ' tSTRING_END {
                      $$ = new ZArrayNode($1.getPosition());
                 }
                 | tQWORDS_BEG qword_list tSTRING_END {
                     $$ = $2;
                     $<ISourcePositionHolder>$.setPosition($1.getPosition());
                 }
 
 qword_list      : /* none */ {
                     $$ = new ArrayNode(lexer.getPosition());
                 }
                 | qword_list tSTRING_CONTENT ' ' {
                     $$ = $1.add($2);
                 }
 
 string_contents : /* none */ {
                     ByteList aChar = ByteList.create("");
                     aChar.setEncoding(lexer.getEncoding());
                     $$ = lexer.createStrNode($<Token>0.getPosition(), aChar, 0);
                 }
                 | string_contents string_content {
                     $$ = support.literal_concat($1.getPosition(), $1, $2);
                 }
 
 xstring_contents: /* none */ {
                     $$ = null;
                 }
                 | xstring_contents string_content {
                     $$ = support.literal_concat(support.getPosition($1), $1, $2);
                 }
 
 string_content  : tSTRING_CONTENT {
                     $$ = $1;
                 }
                 | tSTRING_DVAR {
                     $$ = lexer.getStrTerm();
                     lexer.setStrTerm(null);
                     lexer.setState(LexState.EXPR_BEG);
                 } string_dvar {
                     lexer.setStrTerm($<StrTerm>2);
                     $$ = new EvStrNode($1.getPosition(), $3);
                 }
                 | tSTRING_DBEG {
                    $$ = lexer.getStrTerm();
                    lexer.getConditionState().stop();
                    lexer.getCmdArgumentState().stop();
                    lexer.setStrTerm(null);
                    lexer.setState(LexState.EXPR_BEG);
                 } compstmt tRCURLY {
                    lexer.getConditionState().restart();
                    lexer.getCmdArgumentState().restart();
                    lexer.setStrTerm($<StrTerm>2);
 
                    $$ = support.newEvStrNode($1.getPosition(), $3);
                 }
 
 string_dvar     : tGVAR {
                      $$ = new GlobalVarNode($1.getPosition(), (String) $1.getValue());
                 }
                 | tIVAR {
                      $$ = new InstVarNode($1.getPosition(), (String) $1.getValue());
                 }
                 | tCVAR {
                      $$ = new ClassVarNode($1.getPosition(), (String) $1.getValue());
                 }
                 | backref
 
 // Token:symbol
 symbol          : tSYMBEG sym {
                      lexer.setState(LexState.EXPR_END);
                      $$ = $2;
                      $<ISourcePositionHolder>$.setPosition($1.getPosition());
                 }
 
 // Token:symbol
 sym             : fname | tIVAR | tGVAR | tCVAR
 
 dsym            : tSYMBEG xstring_contents tSTRING_END {
                      lexer.setState(LexState.EXPR_END);
 
                      // DStrNode: :"some text #{some expression}"
                      // StrNode: :"some text"
                      // EvStrNode :"#{some expression}"
                      // Ruby 1.9 allows empty strings as symbols
                      if ($2 == null) {
                          $$ = new SymbolNode($1.getPosition(), "");
                      } else if ($2 instanceof DStrNode) {
                          $$ = new DSymbolNode($1.getPosition(), $<DStrNode>2);
                      } else if ($2 instanceof StrNode) {
                          $$ = new SymbolNode($1.getPosition(), $<StrNode>2.getValue().toString().intern());
                      } else {
                          $$ = new DSymbolNode($1.getPosition());
                          $<DSymbolNode>$.add($2);
                      }
                 }
 
 numeric         : tINTEGER {
                     $$ = $1;
                 }
                 | tFLOAT {
                      $$ = $1;
                 }
                 | tUMINUS_NUM tINTEGER %prec tLOWEST {
                      $$ = support.negateInteger($2);
                 }
                 | tUMINUS_NUM tFLOAT %prec tLOWEST {
                      $$ = support.negateFloat($2);
                 }
 
 // [!null]
 variable        : tIDENTIFIER | tIVAR | tGVAR | tCONSTANT | tCVAR
                 | kNIL { 
                     $$ = new Token("nil", Tokens.kNIL, $1.getPosition());
                 }
                 | kSELF {
                     $$ = new Token("self", Tokens.kSELF, $1.getPosition());
                 }
                 | kTRUE { 
                     $$ = new Token("true", Tokens.kTRUE, $1.getPosition());
                 }
                 | kFALSE {
                     $$ = new Token("false", Tokens.kFALSE, $1.getPosition());
                 }
                 | k__FILE__ {
                     $$ = new Token("__FILE__", Tokens.k__FILE__, $1.getPosition());
                 }
                 | k__LINE__ {
                     $$ = new Token("__LINE__", Tokens.k__LINE__, $1.getPosition());
                 }
                 | k__ENCODING__ {
                     $$ = new Token("__ENCODING__", Tokens.k__ENCODING__, $1.getPosition());
                 }
 
 // [!null]
 var_ref         : variable {
                     $$ = support.gettable($1);
                 }
 
 // [!null]
 var_lhs         : variable {
                     $$ = support.assignable($1, NilImplicitNode.NIL);
                 }
 
 // [!null]
 backref         : tNTH_REF {
                     $$ = $1;
                 }
                 | tBACK_REF {
                     $$ = $1;
                 }
 
 superclass      : term {
                     $$ = null;
                 }
                 | tLT {
                    lexer.setState(LexState.EXPR_BEG);
                 } expr_value term {
                     $$ = $3;
                 }
                 | error term {
                    $$ = null;
                 }
 
 // [!null]
 // ENEBO: Look at command_start stuff I am ripping out
 f_arglist       : tLPAREN2 f_args rparen {
                     $$ = $2;
                     $<ISourcePositionHolder>$.setPosition($1.getPosition());
                     lexer.setState(LexState.EXPR_BEG);
                 }
                 | f_args term {
                     $$ = $1;
                 }
 
 // [!null]
 f_args          : f_arg ',' f_optarg ',' f_rest_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, $5, null, $6);
                 }
                 | f_arg ',' f_optarg ',' f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, $5, $7, $8);
                 }
                 | f_arg ',' f_optarg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, null, null, $4);
                 }
                 | f_arg ',' f_optarg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, null, $5, $6);
                 }
                 | f_arg ',' f_rest_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, null, $3, null, $4);
                 }
                 | f_arg ',' f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, null, $3, $5, $6);
                 }
                 | f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, null, null, null, $2);
                 }
                 | f_optarg ',' f_rest_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, $1, $3, null, $4);
                 }
                 | f_optarg ',' f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, $1, $3, $5, $6);
                 }
                 | f_optarg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, $1, null, null, $2);
                 }
                 | f_optarg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, $1, null, $3, $4);
                 }
                 | f_rest_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, null, $1, null, $2);
                 }
                 | f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, null, $1, $3, $4);
                 }
                 | f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, null, null, null, $1);
                 }
                 | /* none */ {
                     $$ = support.new_args(lexer.getPosition(), null, null, null, null, null);
                 }
 
 f_bad_arg       : tCONSTANT {
                     support.yyerror("formal argument cannot be a constant");
                 }
                 | tIVAR {
                     support.yyerror("formal argument cannot be an instance variable");
                 }
                 | tGVAR {
                     support.yyerror("formal argument cannot be a global variable");
                 }
                 | tCVAR {
                     support.yyerror("formal argument cannot be a class variable");
                 }
 
 // Token:f_norm_arg [!null]
 f_norm_arg      : f_bad_arg
                 | tIDENTIFIER {
                     $$ = support.formal_argument($1);
                 }
 
 f_arg_item      : f_norm_arg {
                     $$ = support.arg_var($1);
   /*
                     $$ = new ArgAuxiliaryNode($1.getPosition(), (String) $1.getValue(), 1);
   */
                 }
                 | tLPAREN f_margs rparen {
                     $$ = $2;
                     /*		    {
 			ID tid = internal_id();
 			arg_var(tid);
 			if (dyna_in_block()) {
 			    $2->nd_value = NEW_DVAR(tid);
 			}
 			else {
 			    $2->nd_value = NEW_LVAR(tid);
 			}
 			$$ = NEW_ARGS_AUX(tid, 1);
 			$$->nd_next = $2;*/
                 }
 
 // [!null]
 f_arg           : f_arg_item {
                     $$ = new ArrayNode(lexer.getPosition(), $1);
                 }
                 | f_arg ',' f_arg_item {
                     $1.add($3);
                     $$ = $1;
                 }
 
 f_opt           : tIDENTIFIER '=' arg_value {
                     support.arg_var(support.formal_argument($1));
                     $$ = new OptArgNode($1.getPosition(), support.assignable($1, $3));
                 }
 
 f_block_opt     : tIDENTIFIER '=' primary_value {
                     support.arg_var(support.formal_argument($1));
