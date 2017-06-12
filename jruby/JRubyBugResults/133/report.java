File path: src/org/jruby/parser/Ruby19Parser.java
Comment: IXME: Resolve what the hell is going on
Initial commit id: bf8db0bc
Final commit id: 84ac6052
   Bugs between [       1]:
97f8fddfe6 fixes JRUBY-4836: [1.9] Symbol with empty string is allowed
   Bugs after [       4]:
5e78f58b81 Fix JRUBY-6538: 'obj !~ thing' is not the same as obj.send(:!~, thing)
58d4d4c90e Fix JRUBY-6534: Broken block-local vars in 1.9 mode
3e6d201fe4 Fix JRUBY-6504: Block parameter syntax issue where the closing | comes at the beginning of the line
1dd08acd07 Fix JRUBY-6237: Allow the syntax `a = b = f 1`

Start block index: 886
End block index: 3747
  public Object yyparse (RubyYaccLexer yyLex) throws java.io.IOException, yyException {
    if (yyMax <= 0) yyMax = 256;			// initial size
    int yyState = 0, yyStates[] = new int[yyMax];	// state stack
    Object yyVal = null, yyVals[] = new Object[yyMax];	// value stack
    int yyToken = -1;					// current input
    int yyErrorFlag = 0;				// #tokens to shift

    yyLoop: for (int yyTop = 0;; ++ yyTop) {
      if (yyTop >= yyStates.length) {			// dynamically increase
        int[] i = new int[yyStates.length+yyMax];
        System.arraycopy(yyStates, 0, i, 0, yyStates.length);
        yyStates = i;
        Object[] o = new Object[yyVals.length+yyMax];
        System.arraycopy(yyVals, 0, o, 0, yyVals.length);
        yyVals = o;
      }
      yyStates[yyTop] = yyState;
      yyVals[yyTop] = yyVal;

      yyDiscarded: for (;;) {	// discarding a token does not change stack
        int yyN;
        if ((yyN = yyDefRed[yyState]) == 0) {	// else [default] reduce (yyN)
          if (yyToken < 0) {
            yyToken = yyLex.advance() ? yyLex.token() : 0;
          }
          if ((yyN = yySindex[yyState]) != 0 && (yyN += yyToken) >= 0
              && yyN < yyTable.length && yyCheck[yyN] == yyToken) {
            yyState = yyTable[yyN];		// shift to yyN
            yyVal = yyLex.value();
            yyToken = -1;
            if (yyErrorFlag > 0) -- yyErrorFlag;
            continue yyLoop;
          }
          if ((yyN = yyRindex[yyState]) != 0 && (yyN += yyToken) >= 0
              && yyN < yyTable.length && yyCheck[yyN] == yyToken)
            yyN = yyTable[yyN];			// reduce (yyN)
          else
            switch (yyErrorFlag) {
  
            case 0:
              yyerror("syntax error", yyExpecting(yyState), yyNames[yyToken]);
  
            case 1: case 2:
              yyErrorFlag = 3;
              do {
                if ((yyN = yySindex[yyStates[yyTop]]) != 0
                    && (yyN += yyErrorCode) >= 0 && yyN < yyTable.length
                    && yyCheck[yyN] == yyErrorCode) {
                  yyState = yyTable[yyN];
                  yyVal = yyLex.value();
                  continue yyLoop;
                }
              } while (-- yyTop >= 0);
              throw new yyException("irrecoverable syntax error");
  
            case 3:
              if (yyToken == 0) {
                throw new yyException("irrecoverable syntax error at end-of-file");
              }
              yyToken = -1;
              continue yyDiscarded;		// leave stack alone
            }
        }
        int yyV = yyTop + 1-yyLen[yyN];
        yyVal = yyDefault(yyV > yyTop ? null : yyVals[yyV]);
        switch (yyN) {
case 1:
					// line 275 "Ruby19Parser.y"
  {
                  lexer.setState(LexState.EXPR_BEG);
                  support.initTopLocalVariables();
              }
  break;
case 2:
					// line 278 "Ruby19Parser.y"
  {
  /* ENEBO: Removed !compile_for_eval which probably is to reduce warnings*/
                  if (((Node)yyVals[0+yyTop]) != null) {
                      /* last expression should not be void */
                      if (((Node)yyVals[0+yyTop]) instanceof BlockNode) {
                          support.checkUselessStatement(((BlockNode)yyVals[0+yyTop]).getLast());
                      } else {
                          support.checkUselessStatement(((Node)yyVals[0+yyTop]));
                      }
                  }
                  support.getResult().setAST(support.addRootNode(((Node)yyVals[0+yyTop]), getPosition(((Node)yyVals[0+yyTop]))));
              }
  break;
case 3:
					// line 291 "Ruby19Parser.y"
  {
                  Node node = ((Node)yyVals[-3+yyTop]);

                  if (((RescueBodyNode)yyVals[-2+yyTop]) != null) {
                      node = new RescueNode(getPosition(((Node)yyVals[-3+yyTop]), true), ((Node)yyVals[-3+yyTop]), ((RescueBodyNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
                  } else if (((Node)yyVals[-1+yyTop]) != null) {
                      warnings.warn(ID.ELSE_WITHOUT_RESCUE, getPosition(((Node)yyVals[-3+yyTop])), "else without rescue is useless");
                      node = support.appendToBlock(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                  }
                  if (((Node)yyVals[0+yyTop]) != null) {
                      if (node == null) node = NilImplicitNode.NIL;
                      node = new EnsureNode(getPosition(((Node)yyVals[-3+yyTop])), node, ((Node)yyVals[0+yyTop]));
                  }

                  yyVal = node;
                }
  break;
case 4:
					// line 308 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-1+yyTop]) instanceof BlockNode) {
                        support.checkUselessStatements(((BlockNode)yyVals[-1+yyTop]));
                    }
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 6:
					// line 316 "Ruby19Parser.y"
  {
                    yyVal = support.newline_node(((Node)yyVals[0+yyTop]), getPosition(((Node)yyVals[0+yyTop]), true));
                }
  break;
case 7:
					// line 319 "Ruby19Parser.y"
  {
                    yyVal = support.appendToBlock(((Node)yyVals[-2+yyTop]), support.newline_node(((Node)yyVals[0+yyTop]), getPosition(((Node)yyVals[0+yyTop]), true)));
                }
  break;
case 8:
					// line 322 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 9:
					// line 326 "Ruby19Parser.y"
  {
                    lexer.setState(LexState.EXPR_FNAME);
                }
  break;
case 10:
					// line 328 "Ruby19Parser.y"
  {
                    yyVal = new AliasNode(support.union(((Token)yyVals[-3+yyTop]), ((Token)yyVals[0+yyTop])), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 11:
					// line 331 "Ruby19Parser.y"
  {
                    yyVal = new VAliasNode(getPosition(((Token)yyVals[-2+yyTop])), (String) ((Token)yyVals[-1+yyTop]).getValue(), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 12:
					// line 334 "Ruby19Parser.y"
  {
                    yyVal = new VAliasNode(getPosition(((Token)yyVals[-2+yyTop])), (String) ((Token)yyVals[-1+yyTop]).getValue(), "$" + ((BackRefNode)yyVals[0+yyTop]).getType());
                }
  break;
case 13:
					// line 337 "Ruby19Parser.y"
  {
                    yyerror("can't make alias for the number variables");
                }
  break;
case 14:
					// line 340 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 15:
					// line 343 "Ruby19Parser.y"
  {
                    yyVal = new IfNode(support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), null);
                }
  break;
case 16:
					// line 346 "Ruby19Parser.y"
  {
                    yyVal = new IfNode(support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), null, ((Node)yyVals[-2+yyTop]));
                }
  break;
case 17:
					// line 349 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
                        yyVal = new WhileNode(getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
                    } else {
                        yyVal = new WhileNode(getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
                    }
                }
  break;
case 18:
					// line 356 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
                        yyVal = new UntilNode(getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
                    } else {
                        yyVal = new UntilNode(getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
                    }
                }
  break;
case 19:
					// line 363 "Ruby19Parser.y"
  {
                    Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                    yyVal = new RescueNode(getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), new RescueBodyNode(getPosition(((Node)yyVals[-2+yyTop])), null, body, null), null);
                }
  break;
case 20:
					// line 367 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        yyerror("BEGIN in method");
                    }
                    support.pushLocalScope();
                }
  break;
case 21:
					// line 372 "Ruby19Parser.y"
  {
                    support.getResult().addBeginNode(new PreExeNode(getPosition(((Node)yyVals[-1+yyTop])), support.getCurrentScope(), ((Node)yyVals[-1+yyTop])));
                    support.popCurrentScope();
                    yyVal = null;
                }
  break;
case 22:
					// line 377 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        warnings.warn(ID.END_IN_METHOD, getPosition(((Token)yyVals[-3+yyTop])), "END in method; use at_exit");
                    }
                    yyVal = new PostExeNode(getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 23:
					// line 383 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));
                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 24:
					// line 387 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));
                    ((MultipleAsgn19Node)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                    yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
                }
  break;
case 25:
					// line 392 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));

                    String asgnOp = (String) ((Token)yyVals[-1+yyTop]).getValue();
                    if (asgnOp.equals("||")) {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                        yyVal = new OpAsgnOrNode(support.union(((AssignableNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                    } else if (asgnOp.equals("&&")) {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                        yyVal = new OpAsgnAndNode(support.union(((AssignableNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                    } else {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-2+yyTop])), asgnOp, ((Node)yyVals[0+yyTop])));
                        ((AssignableNode)yyVals[-2+yyTop]).setPosition(support.union(((AssignableNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                        yyVal = ((AssignableNode)yyVals[-2+yyTop]);
                    }
                }
  break;
case 26:
					// line 408 "Ruby19Parser.y"
  {
  /* FIXME: arg_concat logic missing for opt_call_args*/
                    yyVal = support.new_opElementAsgnNode(getPosition(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-5+yyTop]), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 27:
					// line 412 "Ruby19Parser.y"
  {
                    yyVal = new OpAsgnNode(getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
                }
  break;
case 28:
					// line 415 "Ruby19Parser.y"
  {
                    yyVal = new OpAsgnNode(getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
                }
  break;
case 29:
					// line 418 "Ruby19Parser.y"
  {
                    yyVal = new OpAsgnNode(getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
                }
  break;
case 30:
					// line 421 "Ruby19Parser.y"
  {
                    support.backrefAssignError(((Node)yyVals[-2+yyTop]));
                }
  break;
case 31:
					// line 424 "Ruby19Parser.y"
  {
                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 32:
					// line 427 "Ruby19Parser.y"
  {
                    ((MultipleAsgn19Node)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                    yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
                }
  break;
case 33:
					// line 431 "Ruby19Parser.y"
  {
                    ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                    yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
                    ((MultipleAsgn19Node)yyVals[-2+yyTop]).setPosition(support.union(((MultipleAsgn19Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                }
  break;
case 36:
					// line 440 "Ruby19Parser.y"
  {
                    yyVal = support.newAndNode(getPosition(((Token)yyVals[-1+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 37:
					// line 443 "Ruby19Parser.y"
  {
                    yyVal = support.newOrNode(getPosition(((Token)yyVals[-1+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 38:
					// line 446 "Ruby19Parser.y"
  {
                    yyVal = new NotNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])));
                }
  break;
case 39:
					// line 449 "Ruby19Parser.y"
  {
                    yyVal = new NotNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])));
                }
  break;
case 41:
					// line 454 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));
                }
  break;
case 44:
					// line 461 "Ruby19Parser.y"
  {
                    yyVal = new ReturnNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), support.ret_args(((Node)yyVals[0+yyTop]), getPosition(((Token)yyVals[-1+yyTop]))));
                }
  break;
case 45:
					// line 464 "Ruby19Parser.y"
  {
                    yyVal = new BreakNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), support.ret_args(((Node)yyVals[0+yyTop]), getPosition(((Token)yyVals[-1+yyTop]))));
                }
  break;
case 46:
					// line 467 "Ruby19Parser.y"
  {
                    yyVal = new NextNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), support.ret_args(((Node)yyVals[0+yyTop]), getPosition(((Token)yyVals[-1+yyTop]))));
                }
  break;
case 48:
					// line 473 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 49:
					// line 476 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 50:
					// line 481 "Ruby19Parser.y"
  {
                    support.pushBlockScope();
                }
  break;
case 51:
					// line 483 "Ruby19Parser.y"
  {
                    yyVal = new IterNode(getPosition(((Token)yyVals[-4+yyTop])), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                    support.popCurrentScope();
                }
  break;
case 52:
					// line 489 "Ruby19Parser.y"
  {
                    yyVal = support.new_fcall(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 53:
					// line 492 "Ruby19Parser.y"
  {
                    yyVal = support.new_fcall(((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                }
  break;
case 54:
					// line 495 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 55:
					// line 498 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop])); 
                }
  break;
case 56:
					// line 501 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 57:
					// line 504 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                }
  break;
case 58:
					// line 507 "Ruby19Parser.y"
  {
                    yyVal = support.new_super(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop])); /* .setPosFrom($2);*/
                }
  break;
case 59:
					// line 510 "Ruby19Parser.y"
  {
                    yyVal = support.new_yield(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 61:
					// line 516 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 62:
					// line 521 "Ruby19Parser.y"
  {
                    yyVal = ((MultipleAsgn19Node)yyVals[0+yyTop]);
                }
  break;
case 63:
					// line 524 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-2+yyTop])), support.newArrayNode(getPosition(((Token)yyVals[-2+yyTop])), ((Node)yyVals[-1+yyTop])), null, null);
                }
  break;
case 64:
					// line 529 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[0+yyTop])), ((ListNode)yyVals[0+yyTop]), null, null);
                }
  break;
case 65:
					// line 532 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(support.union(((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), ((ListNode)yyVals[-1+yyTop]).add(((Node)yyVals[0+yyTop])), null, null);
                }
  break;
case 66:
					// line 535 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-2+yyTop])), ((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), (ListNode) null);
                }
  break;
case 67:
					// line 538 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-4+yyTop])), ((ListNode)yyVals[-4+yyTop]), ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 68:
					// line 541 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-1+yyTop])), ((ListNode)yyVals[-1+yyTop]), new StarNode(getPosition(null)), null);
                }
  break;
case 69:
					// line 544 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-3+yyTop])), ((ListNode)yyVals[-3+yyTop]), new StarNode(getPosition(null)), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 70:
					// line 547 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-1+yyTop])), null, ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 71:
					// line 550 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-3+yyTop])), null, ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 72:
					// line 553 "Ruby19Parser.y"
  {
                      yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[0+yyTop])), null, new StarNode(getPosition(null)), null);
                }
  break;
case 73:
					// line 556 "Ruby19Parser.y"
  {
                      yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-2+yyTop])), null, new StarNode(getPosition(null)), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 75:
					// line 561 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 76:
					// line 566 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 77:
					// line 569 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
                }
  break;
case 78:
					// line 574 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                }
  break;
case 79:
					// line 577 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                }
  break;
case 80:
					// line 581 "Ruby19Parser.y"
  {
                    yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
                }
  break;
case 81:
					// line 584 "Ruby19Parser.y"
  {
                    yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 82:
					// line 587 "Ruby19Parser.y"
  {
                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 83:
					// line 590 "Ruby19Parser.y"
  {
                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 84:
					// line 593 "Ruby19Parser.y"
  {
                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 85:
					// line 596 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        yyerror("dynamic constant assignment");
                    }

                    ISourcePosition position = support.union(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]));

                    yyVal = new ConstDeclNode(position, null, new Colon2Node(position, ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
                }
  break;
case 86:
					// line 605 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        yyerror("dynamic constant assignment");
                    }

                    ISourcePosition position = support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop]));

                    yyVal = new ConstDeclNode(position, null, new Colon3Node(position, (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
                }
  break;
case 87:
					// line 614 "Ruby19Parser.y"
  {
                    support.backrefAssignError(((Node)yyVals[0+yyTop]));
                }
  break;
case 88:
					// line 618 "Ruby19Parser.y"
  {
                      /* if (!($$ = assignable($1, 0))) $$ = NEW_BEGIN(0);*/
                    yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
                }
  break;
case 89:
					// line 622 "Ruby19Parser.y"
  {
                    yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 90:
					// line 625 "Ruby19Parser.y"
  {
                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 91:
					// line 628 "Ruby19Parser.y"
  {
                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 92:
					// line 631 "Ruby19Parser.y"
  {
                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 93:
					// line 634 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        yyerror("dynamic constant assignment");
                    }

                    ISourcePosition position = support.union(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]));

                    yyVal = new ConstDeclNode(position, null, new Colon2Node(position, ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
                }
  break;
case 94:
					// line 643 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        yyerror("dynamic constant assignment");
                    }

                    ISourcePosition position = support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop]));

                    yyVal = new ConstDeclNode(position, null, new Colon3Node(position, (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
                }
  break;
case 95:
					// line 652 "Ruby19Parser.y"
  {
                    support.backrefAssignError(((Node)yyVals[0+yyTop]));
                }
  break;
case 96:
					// line 656 "Ruby19Parser.y"
  {
                    yyerror("class/module name must be CONSTANT");
                }
  break;
case 98:
					// line 661 "Ruby19Parser.y"
  {
                    yyVal = new Colon3Node(support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop])), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 99:
					// line 664 "Ruby19Parser.y"
  {
                    yyVal = new Colon2Node(((Token)yyVals[0+yyTop]).getPosition(), null, (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 100:
					// line 667 "Ruby19Parser.y"
  {
                    yyVal = new Colon2Node(support.union(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 104:
					// line 673 "Ruby19Parser.y"
  {
                   lexer.setState(LexState.EXPR_END);
                   yyVal = ((Token)yyVals[0+yyTop]);
               }
  break;
case 105:
					// line 677 "Ruby19Parser.y"
  {
                   lexer.setState(LexState.EXPR_END);
                   yyVal = ((Token)yyVals[0+yyTop]);
               }
  break;
case 106:
					// line 683 "Ruby19Parser.y"
  {
                    yyVal = ((Token)yyVals[0+yyTop]);
                }
  break;
case 107:
					// line 686 "Ruby19Parser.y"
  {
                    yyVal = ((Token)yyVals[0+yyTop]);
                }
  break;
case 108:
					// line 691 "Ruby19Parser.y"
  {
                    yyVal = ((Token)yyVals[0+yyTop]);
                }
  break;
case 109:
					// line 694 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 110:
					// line 698 "Ruby19Parser.y"
  {
                    yyVal = new UndefNode(getPosition(((Token)yyVals[0+yyTop])), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 111:
					// line 701 "Ruby19Parser.y"
  {
                    lexer.setState(LexState.EXPR_FNAME);
                }
  break;
case 112:
					// line 703 "Ruby19Parser.y"
  {
                    yyVal = support.appendToBlock(((Node)yyVals[-3+yyTop]), new UndefNode(getPosition(((Node)yyVals[-3+yyTop])), (String) ((Token)yyVals[0+yyTop]).getValue()));
                }
  break;
case 184:
					// line 722 "Ruby19Parser.y"
  {
                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    /* FIXME: Consider fixing node_assign itself rather than single case*/
                    ((Node)yyVal).setPosition(support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                }
  break;
case 185:
					// line 727 "Ruby19Parser.y"
  {
                    ISourcePosition position = support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                    yyVal = support.node_assign(((Node)yyVals[-4+yyTop]), new RescueNode(position, ((Node)yyVals[-2+yyTop]), new RescueBodyNode(position, null, body, null), null));
                }
  break;
case 186:
					// line 732 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));
                    String asgnOp = (String) ((Token)yyVals[-1+yyTop]).getValue();

                    if (asgnOp.equals("||")) {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                        yyVal = new OpAsgnOrNode(support.union(((AssignableNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                    } else if (asgnOp.equals("&&")) {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                        yyVal = new OpAsgnAndNode(support.union(((AssignableNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                    } else {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-2+yyTop])), asgnOp, ((Node)yyVals[0+yyTop])));
                        ((AssignableNode)yyVals[-2+yyTop]).setPosition(support.union(((AssignableNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                        yyVal = ((AssignableNode)yyVals[-2+yyTop]);
                    }
                }
  break;
case 187:
					// line 748 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[-2+yyTop]));
                    ISourcePosition position = support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                    Node rescueNode = new RescueNode(position, ((Node)yyVals[-2+yyTop]), new RescueBodyNode(position, null, body, null), null);

                    String asgnOp = (String) ((Token)yyVals[-3+yyTop]).getValue();
                    if (asgnOp.equals("||")) {
                        ((AssignableNode)yyVals[-4+yyTop]).setValueNode(((Node)yyVals[-2+yyTop]));
                        yyVal = new OpAsgnOrNode(support.union(((AssignableNode)yyVals[-4+yyTop]), ((Node)yyVals[-2+yyTop])), support.gettable2(((AssignableNode)yyVals[-4+yyTop])), ((AssignableNode)yyVals[-4+yyTop]));
                    } else if (asgnOp.equals("&&")) {
                        ((AssignableNode)yyVals[-4+yyTop]).setValueNode(((Node)yyVals[-2+yyTop]));
                        yyVal = new OpAsgnAndNode(support.union(((AssignableNode)yyVals[-4+yyTop]), ((Node)yyVals[-2+yyTop])), support.gettable2(((AssignableNode)yyVals[-4+yyTop])), ((AssignableNode)yyVals[-4+yyTop]));
                    } else {
                        ((AssignableNode)yyVals[-4+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-4+yyTop])), asgnOp, ((Node)yyVals[-2+yyTop])));
                        ((AssignableNode)yyVals[-4+yyTop]).setPosition(support.union(((AssignableNode)yyVals[-4+yyTop]), ((Node)yyVals[-2+yyTop])));
                        yyVal = ((AssignableNode)yyVals[-4+yyTop]);
                    }
                }
  break;
case 188:
					// line 767 "Ruby19Parser.y"
  {
  /* FIXME: arg_concat missing for opt_call_args*/
                    yyVal = support.new_opElementAsgnNode(getPosition(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-5+yyTop]), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 189:
					// line 771 "Ruby19Parser.y"
  {
                    yyVal = new OpAsgnNode(getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
                }
  break;
case 190:
					// line 774 "Ruby19Parser.y"
  {
                    yyVal = new OpAsgnNode(getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
                }
  break;
case 191:
					// line 777 "Ruby19Parser.y"
  {
                    yyVal = new OpAsgnNode(getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
                }
  break;
case 192:
					// line 780 "Ruby19Parser.y"
  {
                    yyerror("constant re-assignment");
                }
  break;
case 193:
					// line 783 "Ruby19Parser.y"
  {
                    yyerror("constant re-assignment");
                }
  break;
case 194:
					// line 786 "Ruby19Parser.y"
  {
                    support.backrefAssignError(((Node)yyVals[-2+yyTop]));
                }
  break;
case 195:
					// line 789 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[-2+yyTop]));
                    support.checkExpression(((Node)yyVals[0+yyTop]));
    
                    boolean isLiteral = ((Node)yyVals[-2+yyTop]) instanceof FixnumNode && ((Node)yyVals[0+yyTop]) instanceof FixnumNode;
                    yyVal = new DotNode(support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), false, isLiteral);
                }
  break;
case 196:
					// line 796 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[-2+yyTop]));
                    support.checkExpression(((Node)yyVals[0+yyTop]));

                    boolean isLiteral = ((Node)yyVals[-2+yyTop]) instanceof FixnumNode && ((Node)yyVals[0+yyTop]) instanceof FixnumNode;
                    yyVal = new DotNode(support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), true, isLiteral);
                }
  break;
case 197:
					// line 803 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "+", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 198:
					// line 806 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "-", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 199:
					// line 809 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "*", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 200:
					// line 812 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "/", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 201:
					// line 815 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "%", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 202:
					// line 818 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 203:
					// line 821 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), getPosition(null)), "-@");
                }
  break;
case 204:
					// line 824 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(support.getOperatorCallNode(((FloatNode)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), getPosition(null)), "-@");
                }
  break;
case 205:
					// line 827 "Ruby19Parser.y"
  {
                    if (support.isLiteral(((Node)yyVals[0+yyTop]))) {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    } else {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "+@");
                    }
                }
  break;
case 206:
					// line 834 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "-@");
                }
  break;
case 207:
					// line 837 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "|", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 208:
					// line 840 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "^", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 209:
					// line 843 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "&", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 210:
					// line 846 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=>", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 211:
					// line 849 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 212:
					// line 852 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">=", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 213:
					// line 855 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 214:
					// line 858 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 215:
					// line 861 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "==", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 216:
					// line 864 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "===", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 217:
					// line 867 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "!=", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 218:
					// line 870 "Ruby19Parser.y"
  {
                    yyVal = support.getMatchNode(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                  /* ENEBO
                        $$ = match_op($1, $3);
                        if (nd_type($1) == NODE_LIT && TYPE($1->nd_lit) == T_REGEXP) {
                            $$ = reg_named_capture_assign($1->nd_lit, $$);
                        }
                  */
                }
  break;
case 219:
					// line 879 "Ruby19Parser.y"
  {
                    yyVal = new NotNode(support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.getMatchNode(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                }
  break;
case 220:
					// line 882 "Ruby19Parser.y"
  {
                    yyVal = new NotNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])));
                }
  break;
case 221:
					// line 885 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "~");
                }
  break;
case 222:
					// line 888 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<<", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 223:
					// line 891 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">>", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 224:
					// line 894 "Ruby19Parser.y"
  {
                    yyVal = support.newAndNode(getPosition(((Token)yyVals[-1+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 225:
					// line 897 "Ruby19Parser.y"
  {
                    yyVal = support.newOrNode(getPosition(((Token)yyVals[-1+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 226:
					// line 900 "Ruby19Parser.y"
  {
                    /* ENEBO: arg surrounded by in_defined set/unset*/
                    yyVal = new DefinedNode(getPosition(((Token)yyVals[-2+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 227:
					// line 904 "Ruby19Parser.y"
  {
                    yyVal = new IfNode(getPosition(((Node)yyVals[-5+yyTop])), support.getConditionNode(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 228:
					// line 907 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 229:
					// line 911 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));
                    yyVal = ((Node)yyVals[0+yyTop]) != null ? ((Node)yyVals[0+yyTop]) : NilImplicitNode.NIL;
                }
  break;
case 231:
					// line 917 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 232:
					// line 920 "Ruby19Parser.y"
  {
                    yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), new HashNode(getPosition(null), ((ListNode)yyVals[-1+yyTop])));
                }
  break;
case 233:
					// line 923 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(getPosition(((ListNode)yyVals[-1+yyTop])), new HashNode(getPosition(null), ((ListNode)yyVals[-1+yyTop])));
                }
  break;
case 234:
					// line 927 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                    if (yyVal != null) ((Node)yyVal).setPosition(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                }
  break;
case 239:
					// line 936 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 240:
					// line 939 "Ruby19Parser.y"
  {
                    yyVal = support.arg_blk_pass(((Node)yyVals[-1+yyTop]), ((BlockPassNode)yyVals[0+yyTop]));
                }
  break;
case 241:
					// line 942 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(getPosition(((ListNode)yyVals[-1+yyTop])), new HashNode(getPosition(null), ((ListNode)yyVals[-1+yyTop])));
                    yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
                }
  break;
case 242:
					// line 946 "Ruby19Parser.y"
  {
                    yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), new HashNode(getPosition(null), ((ListNode)yyVals[-1+yyTop])));
                    yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
                }
  break;
case 243:
					// line 950 "Ruby19Parser.y"
  {}
  break;
case 244:
					// line 952 "Ruby19Parser.y"
  {
                    yyVal = new Long(lexer.getCmdArgumentState().begin());
                }
  break;
case 245:
					// line 954 "Ruby19Parser.y"
  {
                    lexer.getCmdArgumentState().reset(((Long)yyVals[-1+yyTop]).longValue());
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 246:
					// line 959 "Ruby19Parser.y"
  {
                    yyVal = new BlockPassNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 247:
					// line 963 "Ruby19Parser.y"
  {
                    yyVal = ((BlockPassNode)yyVals[0+yyTop]);
                }
  break;
case 249:
					// line 968 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(getPosition2(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 250:
					// line 971 "Ruby19Parser.y"
  {
                    yyVal = support.newSplatNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 251:
					// line 974 "Ruby19Parser.y"
  {
                    Node node = support.splat_array(((Node)yyVals[-2+yyTop]));

                    if (node != null) {
                        yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
                    } else {
                        yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                }
  break;
case 252:
					// line 983 "Ruby19Parser.y"
  {
                    Node node = null;

                    /* FIXME: lose syntactical elements here (and others like this)*/
                    if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
                        (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
                        yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
                    } else {
                        yyVal = support.arg_concat(support.union(((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                }
  break;
case 253:
					// line 995 "Ruby19Parser.y"
  {
                    Node node = support.splat_array(((Node)yyVals[-2+yyTop]));

                    if (node != null) {
                        yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
                    } else {
                        yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                }
  break;
case 254:
					// line 1004 "Ruby19Parser.y"
  {
                    Node node = null;

                    if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
                        (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
                        yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
                    } else {
                        yyVal = support.arg_concat(support.union(((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                }
  break;
case 255:
					// line 1014 "Ruby19Parser.y"
  {
                     yyVal = support.newSplatNode(getPosition(((Token)yyVals[-1+yyTop])), ((Node)yyVals[0+yyTop]));  
                }
  break;
case 264:
					// line 1026 "Ruby19Parser.y"
  {
                    yyVal = new FCallNoArgNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 265:
					// line 1029 "Ruby19Parser.y"
  {
                    yyVal = new BeginNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]));
                }
  break;
case 266:
					// line 1032 "Ruby19Parser.y"
  {
                    lexer.setState(LexState.EXPR_ENDARG); 
                }
  break;
case 267:
					// line 1034 "Ruby19Parser.y"
  {
                    warnings.warning(ID.GROUPED_EXPRESSION, getPosition(((Token)yyVals[-3+yyTop])), "(...) interpreted as grouped expression");
                    yyVal = ((Node)yyVals[-2+yyTop]);
                }
  break;
case 268:
					// line 1038 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-1+yyTop]) != null) {
                        /* compstmt position includes both parens around it*/
                        ((ISourcePositionHolder) ((Node)yyVals[-1+yyTop])).setPosition(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                    }
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 269:
					// line 1045 "Ruby19Parser.y"
  {
                    yyVal = new Colon2Node(support.union(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 270:
					// line 1048 "Ruby19Parser.y"
  {
                    yyVal = new Colon3Node(support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop])), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 271:
					// line 1051 "Ruby19Parser.y"
  {
                    ISourcePosition position = support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]));
                    if (((Node)yyVals[-1+yyTop]) == null) {
                        yyVal = new ZArrayNode(position); /* zero length array */
                    } else {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                        ((ISourcePositionHolder)yyVal).setPosition(position);
                    }
                }
  break;
case 272:
					// line 1060 "Ruby19Parser.y"
  {
                    yyVal = new HashNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])), ((ListNode)yyVals[-1+yyTop]));
                }
  break;
case 273:
					// line 1063 "Ruby19Parser.y"
  {
                    yyVal = new ReturnNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
                }
  break;
case 274:
					// line 1066 "Ruby19Parser.y"
  {
                    yyVal = support.new_yield(support.union(((Token)yyVals[-3+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 275:
					// line 1069 "Ruby19Parser.y"
  {
                    yyVal = new YieldNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])), null, false);
                }
  break;
case 276:
					// line 1072 "Ruby19Parser.y"
  {
                    yyVal = new YieldNode(((Token)yyVals[0+yyTop]).getPosition(), null, false);
                }
  break;
case 277:
					// line 1075 "Ruby19Parser.y"
  {
                    yyVal = new DefinedNode(getPosition(((Token)yyVals[-4+yyTop])), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 278:
					// line 1078 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[-1+yyTop])), "!");
                }
  break;
case 279:
					// line 1081 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(NilImplicitNode.NIL, "!");
                }
  break;
case 280:
					// line 1084 "Ruby19Parser.y"
  {
                    yyVal = new FCallNoArgBlockNode(support.union(((Token)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop])), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((IterNode)yyVals[0+yyTop]));
                }
  break;
case 282:
					// line 1088 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-1+yyTop]) != null && 
                          ((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
                        throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, getPosition(((Node)yyVals[-1+yyTop])), "Both block arg and actual block given.");
                    }
                    yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
                    ((Node)yyVal).setPosition(support.union(((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop])));
                }
  break;
case 283:
					// line 1096 "Ruby19Parser.y"
  {
                    yyVal = ((LambdaNode)yyVals[0+yyTop]);
                }
  break;
case 284:
					// line 1099 "Ruby19Parser.y"
  {
                    yyVal = new IfNode(support.union(((Token)yyVals[-5+yyTop]), ((Token)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 285:
					// line 1102 "Ruby19Parser.y"
  {
                    yyVal = new IfNode(support.union(((Token)yyVals[-5+yyTop]), ((Token)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-2+yyTop]));
                }
  break;
case 286:
					// line 1105 "Ruby19Parser.y"
  {
                    lexer.getConditionState().begin();
                }
  break;
case 287:
					// line 1107 "Ruby19Parser.y"
  {
                    lexer.getConditionState().end();
                }
  break;
case 288:
					// line 1109 "Ruby19Parser.y"
  {
                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
                    yyVal = new WhileNode(support.union(((Token)yyVals[-6+yyTop]), ((Token)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
                }
  break;
case 289:
					// line 1113 "Ruby19Parser.y"
  {
                  lexer.getConditionState().begin();
                }
  break;
case 290:
					// line 1115 "Ruby19Parser.y"
  {
                  lexer.getConditionState().end();
                }
  break;
case 291:
					// line 1117 "Ruby19Parser.y"
  {
                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
                    yyVal = new UntilNode(getPosition(((Token)yyVals[-6+yyTop])), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
                }
  break;
case 292:
					// line 1121 "Ruby19Parser.y"
  {
                    yyVal = new CaseNode(support.union(((Token)yyVals[-4+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 293:
					// line 1124 "Ruby19Parser.y"
  {
                    yyVal = new CaseNode(support.union(((Token)yyVals[-3+yyTop]), ((Token)yyVals[0+yyTop])), null, ((Node)yyVals[-1+yyTop]));
                }
  break;
case 294:
					// line 1127 "Ruby19Parser.y"
  {
                    lexer.getConditionState().begin();
                }
  break;
case 295:
					// line 1129 "Ruby19Parser.y"
  {
                    lexer.getConditionState().end();
                }
  break;
case 296:
					// line 1131 "Ruby19Parser.y"
  {
                      /* ENEBO: Lots of optz in 1.9 parser here*/
                    yyVal = new ForNode(support.union(((Token)yyVals[-8+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-7+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-4+yyTop]));
                }
  break;
case 297:
					// line 1135 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        yyerror("class definition in method body");
                    }
                    support.pushLocalScope();
                }
  break;
case 298:
					// line 1140 "Ruby19Parser.y"
  {
                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);

                    yyVal = new ClassNode(support.union(((Token)yyVals[-5+yyTop]), ((Token)yyVals[0+yyTop])), ((Colon3Node)yyVals[-4+yyTop]), support.getCurrentScope(), body, ((Node)yyVals[-3+yyTop]));
                    support.popCurrentScope();
                }
  break;
case 299:
					// line 1146 "Ruby19Parser.y"
  {
                    yyVal = new Boolean(support.isInDef());
                    support.setInDef(false);
                }
  break;
case 300:
					// line 1149 "Ruby19Parser.y"
  {
                    yyVal = new Integer(support.getInSingle());
                    support.setInSingle(0);
                    support.pushLocalScope();
                }
  break;
case 301:
					// line 1153 "Ruby19Parser.y"
  {
                    yyVal = new SClassNode(support.union(((Token)yyVals[-7+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-5+yyTop]), support.getCurrentScope(), ((Node)yyVals[-1+yyTop]));
                    support.popCurrentScope();
                    support.setInDef(((Boolean)yyVals[-4+yyTop]).booleanValue());
                    support.setInSingle(((Integer)yyVals[-2+yyTop]).intValue());
                }
  break;
case 302:
					// line 1159 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) { 
                        yyerror("module definition in method body");
                    }
                    support.pushLocalScope();
                }
  break;
case 303:
					// line 1164 "Ruby19Parser.y"
  {
                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);

                    yyVal = new ModuleNode(support.union(((Token)yyVals[-4+yyTop]), ((Token)yyVals[0+yyTop])), ((Colon3Node)yyVals[-3+yyTop]), support.getCurrentScope(), body);
                    support.popCurrentScope();
                }
  break;
case 304:
					// line 1170 "Ruby19Parser.y"
  {
                    support.setInDef(true);
                    support.pushLocalScope();
                }
  break;
case 305:
					// line 1173 "Ruby19Parser.y"
  {
                    /* TODO: We should use implicit nil for body, but problem (punt til later)*/
                    Node body = ((Node)yyVals[-1+yyTop]); /*$5 == null ? NilImplicitNode.NIL : $5;*/

                    yyVal = new DefnNode(support.union(((Token)yyVals[-5+yyTop]), ((Token)yyVals[0+yyTop])), new ArgumentNode(((Token)yyVals[-4+yyTop]).getPosition(), (String) ((Token)yyVals[-4+yyTop]).getValue()), ((ArgsNode)yyVals[-2+yyTop]), support.getCurrentScope(), body);
                    support.popCurrentScope();
                    support.setInDef(false);
                }
  break;
case 306:
					// line 1181 "Ruby19Parser.y"
  {
                    lexer.setState(LexState.EXPR_FNAME);
                }
  break;
case 307:
					// line 1183 "Ruby19Parser.y"
  {
                    support.setInSingle(support.getInSingle() + 1);
                    support.pushLocalScope();
                    lexer.setState(LexState.EXPR_END); /* force for args */
                }
  break;
case 308:
					// line 1187 "Ruby19Parser.y"
  {
                    /* TODO: We should use implicit nil for body, but problem (punt til later)*/
                    Node body = ((Node)yyVals[-1+yyTop]); /*$8 == null ? NilImplicitNode.NIL : $8;*/

                    yyVal = new DefsNode(support.union(((Token)yyVals[-8+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-7+yyTop]), new ArgumentNode(((Token)yyVals[-4+yyTop]).getPosition(), (String) ((Token)yyVals[-4+yyTop]).getValue()), ((ArgsNode)yyVals[-2+yyTop]), support.getCurrentScope(), body);
                    support.popCurrentScope();
                    support.setInSingle(support.getInSingle() - 1);
                }
  break;
case 309:
					// line 1195 "Ruby19Parser.y"
  {
                    yyVal = new BreakNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
                }
  break;
case 310:
					// line 1198 "Ruby19Parser.y"
  {
                    yyVal = new NextNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
                }
  break;
case 311:
					// line 1201 "Ruby19Parser.y"
  {
                    yyVal = new RedoNode(((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 312:
					// line 1204 "Ruby19Parser.y"
  {
                    yyVal = new RetryNode(((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 313:
					// line 1208 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));
                    yyVal = ((Node)yyVals[0+yyTop]);
                    if (yyVal == null) yyVal = NilImplicitNode.NIL;
                }
  break;
case 320:
					// line 1222 "Ruby19Parser.y"
  {
                    yyVal = new IfNode(getPosition(((Token)yyVals[-4+yyTop])), support.getConditionNode(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 322:
					// line 1227 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 324:
					// line 1232 "Ruby19Parser.y"
  {}
  break;
case 325:
					// line 1234 "Ruby19Parser.y"
  {
                     yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
                }
  break;
case 326:
					// line 1237 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 327:
					// line 1241 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                }
  break;
case 328:
					// line 1244 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                }
  break;
case 329:
					// line 1248 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[0+yyTop])), ((ListNode)yyVals[0+yyTop]), null, null);
                }
  break;
case 330:
					// line 1251 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-3+yyTop])), ((ListNode)yyVals[-3+yyTop]), support.assignable(((Token)yyVals[0+yyTop]), null), null);
                }
  break;
case 331:
					// line 1254 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-5+yyTop])), ((ListNode)yyVals[-5+yyTop]), support.assignable(((Token)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 332:
					// line 1257 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-2+yyTop])), ((ListNode)yyVals[-2+yyTop]), new StarNode(getPosition(null)), null);
                }
  break;
case 333:
					// line 1260 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-4+yyTop])), ((ListNode)yyVals[-4+yyTop]), new StarNode(getPosition(null)), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 334:
					// line 1263 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-1+yyTop])), null, support.assignable(((Token)yyVals[0+yyTop]), null), null);
                }
  break;
case 335:
					// line 1266 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-3+yyTop])), null, support.assignable(((Token)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 336:
					// line 1269 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[0+yyTop])), null, new StarNode(getPosition(null)), null);
                }
  break;
case 337:
					// line 1272 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-2+yyTop])), null, null, ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 338:
					// line 1276 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 339:
					// line 1279 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-7+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 340:
					// line 1282 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 341:
					// line 1285 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 342:
					// line 1288 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 343:
					// line 1291 "Ruby19Parser.y"
  {
    /* FIXME, weird unnamed rest*/
                    yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, null);
                }
  break;
case 344:
					// line 1295 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 345:
					// line 1298 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 346:
					// line 1301 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 347:
					// line 1304 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 348:
					// line 1307 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 349:
					// line 1310 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 350:
					// line 1313 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((RestArgNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 351:
					// line 1316 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((RestArgNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 352:
					// line 1319 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(getPosition(((BlockArgNode)yyVals[0+yyTop])), null, null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 353:
					// line 1323 "Ruby19Parser.y"
  {
    /* was $$ = null;*/
                   yyVal = support.new_args(getPosition(null), null, null, null, null, null);
                }
  break;
case 354:
					// line 1327 "Ruby19Parser.y"
  {
                    lexer.commandStart = true;
                    yyVal = ((ArgsNode)yyVals[0+yyTop]);
                }
  break;
case 355:
					// line 1332 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(getPosition(null), null, null, null, null, null);
                }
  break;
case 356:
					// line 1335 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(getPosition(null), null, null, null, null, null);
                }
  break;
case 357:
					// line 1338 "Ruby19Parser.y"
  {
                    yyVal = ((ArgsNode)yyVals[-2+yyTop]);
                }
  break;
case 359:
					// line 1344 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 360:
					// line 1349 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 361:
					// line 1352 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 362:
					// line 1356 "Ruby19Parser.y"
  {
                    support.new_bv(((Token)yyVals[0+yyTop]));
                }
  break;
case 363:
					// line 1359 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 364:
					// line 1363 "Ruby19Parser.y"
  {
                    support.pushBlockScope();
                    yyVal = lexer.getLeftParenBegin();
                    lexer.setLeftParenBegin(lexer.incrementParenNest());
                }
  break;
case 365:
					// line 1367 "Ruby19Parser.y"
  {
                    yyVal = new LambdaNode(support.union(((ArgsNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), ((ArgsNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), support.getCurrentScope());
                    support.popCurrentScope();
                    lexer.setLeftParenBegin(((Integer)yyVals[-2+yyTop]));
                }
  break;
case 366:
					// line 1373 "Ruby19Parser.y"
  {
                    yyVal = ((ArgsNode)yyVals[-2+yyTop]);
                    ((ISourcePositionHolder)yyVal).setPosition(support.union(((Token)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop])));
                }
  break;
case 367:
					// line 1377 "Ruby19Parser.y"
  {
                    yyVal = ((ArgsNode)yyVals[-1+yyTop]);
                    ((ISourcePositionHolder)yyVal).setPosition(support.union(((ArgsNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])));
                }
  break;
case 368:
					// line 1382 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 369:
					// line 1385 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 370:
					// line 1389 "Ruby19Parser.y"
  {
                    support.pushBlockScope();
                }
  break;
case 371:
					// line 1391 "Ruby19Parser.y"
  {
                    yyVal = new IterNode(getPosition(((Token)yyVals[-4+yyTop])), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                    support.popCurrentScope();
                }
  break;
case 372:
					// line 1396 "Ruby19Parser.y"
  {
                    if (((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
                        throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, getPosition(((Node)yyVals[-1+yyTop])), "Both block arg and actual block given.");
                    }
                    yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
                    ((Node)yyVal).setPosition(support.union(((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop])));
                }
  break;
case 373:
					// line 1403 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 374:
					// line 1406 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 375:
					// line 1410 "Ruby19Parser.y"
  {
                    yyVal = support.new_fcall(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 376:
					// line 1413 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 377:
					// line 1416 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 378:
					// line 1419 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]), null, null);
                }
  break;
case 379:
					// line 1422 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-2+yyTop]), new Token("call", ((Node)yyVals[-2+yyTop]).getPosition()), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 380:
					// line 1425 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-2+yyTop]), new Token("call", ((Node)yyVals[-2+yyTop]).getPosition()), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 381:
					// line 1428 "Ruby19Parser.y"
  {
                    yyVal = support.new_super(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]));
                }
  break;
case 382:
					// line 1431 "Ruby19Parser.y"
  {
                    yyVal = new ZSuperNode(((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 383:
					// line 1434 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-3+yyTop]) instanceof SelfNode) {
                        yyVal = support.new_fcall(new Token("[]", support.union(((Node)yyVals[-3+yyTop]), ((Token)yyVals[0+yyTop]))), ((Node)yyVals[-1+yyTop]), null);
                    } else {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), new Token("[]", support.union(((Node)yyVals[-3+yyTop]), ((Token)yyVals[0+yyTop]))), ((Node)yyVals[-1+yyTop]), null);
                    }
                }
  break;
case 384:
					// line 1442 "Ruby19Parser.y"
  {
                    support.pushBlockScope();
                }
  break;
case 385:
					// line 1444 "Ruby19Parser.y"
  {
                    yyVal = new IterNode(getPosition(((Token)yyVals[-4+yyTop])), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                    support.popCurrentScope();
                }
  break;
case 386:
					// line 1448 "Ruby19Parser.y"
  {
                    support.pushBlockScope();
                }
  break;
case 387:
					// line 1450 "Ruby19Parser.y"
  {
                    yyVal = new IterNode(support.union(((Token)yyVals[-4+yyTop]), ((Token)yyVals[0+yyTop])), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                    ((ISourcePositionHolder)yyVals[-5+yyTop]).setPosition(support.union(((ISourcePositionHolder)yyVals[-5+yyTop]), ((ISourcePositionHolder)yyVal)));
                    support.popCurrentScope();
                }
  break;
case 388:
					// line 1456 "Ruby19Parser.y"
  {
                    yyVal = support.newWhenNode(support.union(((Token)yyVals[-4+yyTop]), support.unwrapNewlineNode(((Node)yyVals[-1+yyTop]))), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 391:
					// line 1462 "Ruby19Parser.y"
  {
                    Node node;
                    if (((Node)yyVals[-3+yyTop]) != null) {
                        node = support.appendToBlock(support.node_assign(((Node)yyVals[-3+yyTop]), new GlobalVarNode(getPosition(((Token)yyVals[-5+yyTop])), "$!")), ((Node)yyVals[-1+yyTop]));
                        if (((Node)yyVals[-1+yyTop]) != null) {
                            node.setPosition(support.unwrapNewlineNode(((Node)yyVals[-1+yyTop])).getPosition());
                        }
                    } else {
                        node = ((Node)yyVals[-1+yyTop]);
                    }
                    Node body = node == null ? NilImplicitNode.NIL : node;
                    yyVal = new RescueBodyNode(getPosition(((Token)yyVals[-5+yyTop]), true), ((Node)yyVals[-4+yyTop]), body, ((RescueBodyNode)yyVals[0+yyTop]));
                }
  break;
case 392:
					// line 1475 "Ruby19Parser.y"
  { yyVal = null; }
  break;
case 393:
					// line 1477 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                }
  break;
case 394:
					// line 1480 "Ruby19Parser.y"
  {
                    yyVal = support.splat_array(((Node)yyVals[0+yyTop]));
                    if (yyVal == null) yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 396:
					// line 1486 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 398:
					// line 1491 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 401:
					// line 1497 "Ruby19Parser.y"
  {
                    /* FIXME: We may be intern'ing more than once.*/
                    yyVal = new SymbolNode(((Token)yyVals[0+yyTop]).getPosition(), ((String) ((Token)yyVals[0+yyTop]).getValue()).intern());
                }
  break;
case 403:
					// line 1503 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]) instanceof EvStrNode ? new DStrNode(getPosition(((Node)yyVals[0+yyTop]))).add(((Node)yyVals[0+yyTop])) : ((Node)yyVals[0+yyTop]);
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
  break;
case 404:
					// line 1516 "Ruby19Parser.y"
  {
                    yyVal = new StrNode(((Token)yyVals[-1+yyTop]).getPosition(), ByteList.create((String) ((Token)yyVals[0+yyTop]).getValue()));
                }
  break;
case 405:
					// line 1519 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 406:
					// line 1522 "Ruby19Parser.y"
  {
                    yyVal = support.literal_concat(getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 407:
					// line 1526 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);

                    ((ISourcePositionHolder)yyVal).setPosition(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                    int extraLength = ((String) ((Token)yyVals[-2+yyTop]).getValue()).length() - 1;

                    /* We may need to subtract addition offset off of first */
                    /* string fragment (we optimistically take one off in*/
                    /* ParserSupport.literal_concat).  Check token length*/
                    /* and subtract as neeeded.*/
                    if ((((Node)yyVals[-1+yyTop]) instanceof DStrNode) && extraLength > 0) {
                      Node strNode = ((DStrNode)((Node)yyVals[-1+yyTop])).get(0);
                      assert strNode != null;
                      strNode.getPosition().adjustStartOffset(-extraLength);
                    }
                }
  break;
case 408:
					// line 1543 "Ruby19Parser.y"
  {
                    ISourcePosition position = support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]));

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
                }
  break;
case 409:
					// line 1559 "Ruby19Parser.y"
  {
                    int options = ((RegexpNode)yyVals[0+yyTop]).getOptions();
                    Node node = ((Node)yyVals[-1+yyTop]);

                    if (node == null) {
                        yyVal = new RegexpNode(getPosition(((Token)yyVals[-2+yyTop])), ByteList.create(""), options & ~ReOptions.RE_OPTION_ONCE);
                    } else if (node instanceof StrNode) {
                        yyVal = new RegexpNode(((Node)yyVals[-1+yyTop]).getPosition(), (ByteList) ((StrNode) node).getValue().clone(), options & ~ReOptions.RE_OPTION_ONCE);
                    } else if (node instanceof DStrNode) {
                        yyVal = new DRegexpNode(getPosition(((Token)yyVals[-2+yyTop])), (DStrNode) node, options, (options & ReOptions.RE_OPTION_ONCE) != 0);
                    } else {
                        yyVal = new DRegexpNode(getPosition(((Token)yyVals[-2+yyTop])), options, (options & ReOptions.RE_OPTION_ONCE) != 0).add(node);
                    }
                }
  break;
case 410:
					// line 1574 "Ruby19Parser.y"
  {
                    yyVal = new ZArrayNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                }
  break;
case 411:
					// line 1577 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-1+yyTop]);
                }
  break;
case 412:
					// line 1581 "Ruby19Parser.y"
  {
                    yyVal = new ArrayNode(getPosition(null));
                }
  break;
case 413:
					// line 1584 "Ruby19Parser.y"
  {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]) instanceof EvStrNode ? new DStrNode(getPosition(((ListNode)yyVals[-2+yyTop]))).add(((Node)yyVals[-1+yyTop])) : ((Node)yyVals[-1+yyTop]));
                }
  break;
case 415:
					// line 1589 "Ruby19Parser.y"
  {
                     yyVal = support.literal_concat(getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 416:
					// line 1593 "Ruby19Parser.y"
  {
                     yyVal = new ZArrayNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                }
  break;
case 417:
					// line 1596 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-1+yyTop]);
                    ((ISourcePositionHolder)yyVal).setPosition(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                }
  break;
case 418:
					// line 1601 "Ruby19Parser.y"
  {
                    yyVal = new ArrayNode(getPosition(null));
                }
  break;
case 419:
					// line 1604 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
                }
  break;
case 420:
					// line 1608 "Ruby19Parser.y"
  {
                    yyVal = new StrNode(((Token)yyVals[0+yyTop]).getPosition(), ByteList.create(""));
                }
  break;
case 421:
					// line 1611 "Ruby19Parser.y"
  {
                    yyVal = support.literal_concat(getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 422:
					// line 1615 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 423:
					// line 1618 "Ruby19Parser.y"
  {
                    yyVal = support.literal_concat(getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 424:
					// line 1622 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 425:
					// line 1625 "Ruby19Parser.y"
  {
                    yyVal = lexer.getStrTerm();
                    lexer.setStrTerm(null);
                    lexer.setState(LexState.EXPR_BEG);
                }
  break;
case 426:
					// line 1629 "Ruby19Parser.y"
  {
                    lexer.setStrTerm(((StrTerm)yyVals[-1+yyTop]));
                    yyVal = new EvStrNode(support.union(((Token)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 427:
					// line 1633 "Ruby19Parser.y"
  {
                   yyVal = lexer.getStrTerm();
                   lexer.setStrTerm(null);
                   lexer.setState(LexState.EXPR_BEG);
                }
  break;
case 428:
					// line 1637 "Ruby19Parser.y"
  {
                   lexer.setStrTerm(((StrTerm)yyVals[-2+yyTop]));

                   yyVal = support.newEvStrNode(support.union(((Token)yyVals[-3+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 429:
					// line 1643 "Ruby19Parser.y"
  {
                     yyVal = new GlobalVarNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 430:
					// line 1646 "Ruby19Parser.y"
  {
                     yyVal = new InstVarNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 431:
					// line 1649 "Ruby19Parser.y"
  {
                     yyVal = new ClassVarNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 433:
					// line 1655 "Ruby19Parser.y"
  {
                     lexer.setState(LexState.EXPR_END);
                     yyVal = ((Token)yyVals[0+yyTop]);
                     ((ISourcePositionHolder)yyVal).setPosition(support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop])));
                }
  break;
case 438:
					// line 1664 "Ruby19Parser.y"
  {
                     lexer.setState(LexState.EXPR_END);

                     /* DStrNode: :"some text #{some expression}"*/
                     /* StrNode: :"some text"*/
                     /* EvStrNode :"#{some expression}"*/
                     if (((Node)yyVals[-1+yyTop]) == null) {
                       yyerror("empty symbol literal");
                     }
                     /* FIXME: No node here seems to be an empty string
                        instead of an error
                        if (!($$ = $2)) {
                        $$ = NEW_LIT(ID2SYM(rb_intern("")));
                        }
                     */

                     if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
                         yyVal = new DSymbolNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])), ((DStrNode)yyVals[-1+yyTop]));
                     } else {
                       ISourcePosition position = support.union(((Node)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop]));

                       /* We substract one since tsymbeg is longer than one*/
                       /* and we cannot union it directly so we assume quote*/
                       /* is one character long and subtract for it.*/
                       position.adjustStartOffset(-1);
                       ((Node)yyVals[-1+yyTop]).setPosition(position);

                       yyVal = new DSymbolNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                       ((DSymbolNode)yyVal).add(((Node)yyVals[-1+yyTop]));
                     }
                }
  break;
case 439:
					// line 1696 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 440:
					// line 1699 "Ruby19Parser.y"
  {
                     yyVal = ((FloatNode)yyVals[0+yyTop]);
                }
  break;
case 441:
					// line 1702 "Ruby19Parser.y"
  {
                     yyVal = support.negateInteger(((Node)yyVals[0+yyTop]));
                }
  break;
case 442:
					// line 1705 "Ruby19Parser.y"
  {
                     yyVal = support.negateFloat(((FloatNode)yyVals[0+yyTop]));
                }
  break;
case 448:
					// line 1710 "Ruby19Parser.y"
  { 
                    yyVal = new Token("nil", Tokens.kNIL, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 449:
					// line 1713 "Ruby19Parser.y"
  {
                    yyVal = new Token("self", Tokens.kSELF, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 450:
					// line 1716 "Ruby19Parser.y"
  { 
                    yyVal = new Token("true", Tokens.kTRUE, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 451:
					// line 1719 "Ruby19Parser.y"
  {
                    yyVal = new Token("false", Tokens.kFALSE, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 452:
					// line 1722 "Ruby19Parser.y"
  {
                    yyVal = new Token("__FILE__", Tokens.k__FILE__, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 453:
					// line 1725 "Ruby19Parser.y"
  {
                    yyVal = new Token("__LINE__", Tokens.k__LINE__, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 454:
					// line 1728 "Ruby19Parser.y"
  {
                    yyVal = new Token("__ENCODING__", Tokens.k__LINE__, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 455:
					// line 1732 "Ruby19Parser.y"
  {
                    yyVal = support.gettable(((Token)yyVals[0+yyTop]));
                }
  break;
case 456:
					// line 1736 "Ruby19Parser.y"
  {
                    yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
                }
  break;
case 457:
					// line 1740 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 458:
					// line 1743 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 459:
					// line 1747 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 460:
					// line 1750 "Ruby19Parser.y"
  {
                   lexer.setState(LexState.EXPR_BEG);
                }
  break;
case 461:
					// line 1752 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 462:
					// line 1755 "Ruby19Parser.y"
  {
                   yyerrok();
                   yyVal = null;
                }
  break;
case 463:
					// line 1761 "Ruby19Parser.y"
  {
                    yyVal = ((ArgsNode)yyVals[-1+yyTop]);
                    ((ISourcePositionHolder)yyVal).setPosition(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                    lexer.setState(LexState.EXPR_BEG);
                }
  break;
case 464:
					// line 1766 "Ruby19Parser.y"
  {
                    yyVal = ((ArgsNode)yyVals[-1+yyTop]);
                }
  break;
case 465:
					// line 1770 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 466:
					// line 1773 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-7+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 467:
					// line 1776 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 468:
					// line 1779 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 469:
					// line 1782 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 470:
					// line 1785 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 471:
					// line 1788 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 472:
					// line 1791 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 473:
					// line 1794 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 474:
					// line 1797 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 475:
					// line 1800 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 476:
					// line 1803 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((RestArgNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 477:
					// line 1806 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((RestArgNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 478:
					// line 1809 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 479:
					// line 1812 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(getPosition(null), null, null, null, null, null);
                }
  break;
case 480:
					// line 1816 "Ruby19Parser.y"
  {
                    yyerror("formal argument cannot be a constant");
                }
  break;
case 481:
					// line 1819 "Ruby19Parser.y"
  {
                    yyerror("formal argument cannot be an instance variable");
                }
  break;
case 482:
					// line 1822 "Ruby19Parser.y"
  {
                    yyerror("formal argument cannot be a global variable");
                }
  break;
case 483:
					// line 1825 "Ruby19Parser.y"
  {
                    yyerror("formal argument cannot be a class variable");
                }
  break;
case 485:
					// line 1831 "Ruby19Parser.y"
  {
    /* FIXME: Resolve what the hell is going on*/
    /*                    if (support.is_local_id($1)) {
                        yyerror("formal argument must be local variable");
                        }*/
                     
                    support.shadowing_lvar(((Token)yyVals[0+yyTop]));
                    yyVal = ((Token)yyVals[0+yyTop]);
                }
  break;
case 486:
					// line 1841 "Ruby19Parser.y"
  {
                    support.arg_var(((Token)yyVals[0+yyTop]));
                    yyVal = new ArgumentNode(((ISourcePositionHolder)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
  /*
                    $$ = new ArgAuxiliaryNode($1.getPosition(), (String) $1.getValue(), 1);
  */
                }
  break;
case 487:
					// line 1848 "Ruby19Parser.y"
  {
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
                }
  break;
case 488:
					// line 1863 "Ruby19Parser.y"
  {
                    yyVal = new ArrayNode(getPosition(null), ((Node)yyVals[0+yyTop]));
                }
  break;
case 489:
					// line 1866 "Ruby19Parser.y"
  {
                    ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                    yyVal = ((ListNode)yyVals[-2+yyTop]);
                }
  break;
case 490:
					// line 1871 "Ruby19Parser.y"
  {
                    if (!support.is_local_id(((Token)yyVals[-2+yyTop]))) {
                        yyerror("formal argument must be local variable");
                    }
                    support.shadowing_lvar(((Token)yyVals[-2+yyTop]));
                    support.arg_var(((Token)yyVals[-2+yyTop]));
                    yyVal = new OptArgNode(getPosition(((Token)yyVals[-2+yyTop])), support.assignable(((Token)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                }
  break;
case 491:
					// line 1880 "Ruby19Parser.y"
  {
                    if (!support.is_local_id(((Token)yyVals[-2+yyTop]))) {
                        yyerror("formal argument must be local variable");
                    }
                    support.shadowing_lvar(((Token)yyVals[-2+yyTop]));
                    support.arg_var(((Token)yyVals[-2+yyTop]));
                    yyVal = new OptArgNode(getPosition(((Token)yyVals[-2+yyTop])), support.assignable(((Token)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                }
  break;
case 492:
					// line 1889 "Ruby19Parser.y"
  {
                    yyVal = new BlockNode(getPosition(((Node)yyVals[0+yyTop]))).add(((Node)yyVals[0+yyTop]));
                }
  break;
case 493:
					// line 1892 "Ruby19Parser.y"
  {
                    yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 494:
					// line 1896 "Ruby19Parser.y"
  {
                    yyVal = new BlockNode(getPosition(((Node)yyVals[0+yyTop]))).add(((Node)yyVals[0+yyTop]));
                }
  break;
case 495:
					// line 1899 "Ruby19Parser.y"
  {
                    yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 498:
					// line 1905 "Ruby19Parser.y"
  {
                    if (!support.is_local_id(((Token)yyVals[0+yyTop]))) {
                        yyerror("duplicate rest argument name");
                    }
                    support.shadowing_lvar(((Token)yyVals[0+yyTop]));
                    yyVal = new RestArgNode(support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop])), (String) ((Token)yyVals[0+yyTop]).getValue(), support.arg_var(((Token)yyVals[0+yyTop])));
                }
  break;
case 499:
					// line 1912 "Ruby19Parser.y"
  {
                    yyVal = new UnnamedRestArgNode(((Token)yyVals[0+yyTop]).getPosition(), support.getCurrentScope().getLocalScope().addVariable("*"));
                }
  break;
case 502:
					// line 1919 "Ruby19Parser.y"
  {
                    String identifier = (String) ((Token)yyVals[0+yyTop]).getValue();

                    if (!support.is_local_id(((Token)yyVals[0+yyTop]))) {
                        yyerror("block argument must be local variable");
                    }
                    support.shadowing_lvar(((Token)yyVals[0+yyTop]));
                    yyVal = new BlockArgNode(support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop])), support.arg_var(((Token)yyVals[0+yyTop])), identifier);
                }
  break;
case 503:
					// line 1929 "Ruby19Parser.y"
  {
                    yyVal = ((BlockArgNode)yyVals[0+yyTop]);
                }
  break;
case 504:
					// line 1932 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 505:
					// line 1936 "Ruby19Parser.y"
  {
                    if (!(((Node)yyVals[0+yyTop]) instanceof SelfNode)) {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                    }
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 506:
					// line 1942 "Ruby19Parser.y"
  {
                    lexer.setState(LexState.EXPR_BEG);
                }
  break;
case 507:
					// line 1944 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-1+yyTop]) == null) {
                        yyerror("can't define single method for ().");
                    } else if (((Node)yyVals[-1+yyTop]) instanceof ILiteralNode) {
                        yyerror("can't define single method for literals.");
                    }
                    support.checkExpression(((Node)yyVals[-1+yyTop]));
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 508:
					// line 1954 "Ruby19Parser.y"
  {
                    yyVal = new ArrayNode(getPosition(null));
                }
  break;
case 509:
					// line 1957 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-1+yyTop]);
                }
  break;
case 511:
					// line 1962 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-2+yyTop]).addAll(((ListNode)yyVals[0+yyTop]));
                }
  break;
case 512:
					// line 1966 "Ruby19Parser.y"
  {
                    ISourcePosition position;
                    if (((Node)yyVals[-2+yyTop]) == null && ((Node)yyVals[0+yyTop]) == null) {
                        position = getPosition(((Token)yyVals[-1+yyTop]));
                    } else {
                        position = support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }

                    yyVal = support.newArrayNode(position, ((Node)yyVals[-2+yyTop])).add(((Node)yyVals[0+yyTop]));
                }
  break;
case 513:
					// line 1976 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), new SymbolNode(getPosition(((Token)yyVals[-1+yyTop])), (String) ((Token)yyVals[-1+yyTop]).getValue())).add(((Node)yyVals[0+yyTop]));
                }
  break;
case 530:
					// line 1986 "Ruby19Parser.y"
  {
                    yyVal = ((Token)yyVals[0+yyTop]);
                }
  break;
case 531:
					// line 1989 "Ruby19Parser.y"
  {
                    yyVal = ((Token)yyVals[0+yyTop]);
                }
  break;
case 535:
					// line 1994 "Ruby19Parser.y"
  {
                      yyerrok();
                }
  break;
case 538:
					// line 2000 "Ruby19Parser.y"
  {
                      yyerrok();
                }
  break;
case 539:
					// line 2004 "Ruby19Parser.y"
  {
                      yyVal = null;
                }
  break;
case 540:
					// line 2008 "Ruby19Parser.y"
  {  
                  yyVal = null;
                }
  break;
					// line 8029 "-"
        }
        yyTop -= yyLen[yyN];
        yyState = yyStates[yyTop];
        int yyM = yyLhs[yyN];
        if (yyState == 0 && yyM == 0) {
          yyState = yyFinal;
          if (yyToken < 0) {
            yyToken = yyLex.advance() ? yyLex.token() : 0;
          }
          if (yyToken == 0) {
            return yyVal;
          }
          continue yyLoop;
        }
        if ((yyN = yyGindex[yyM]) != 0 && (yyN += yyState) >= 0
            && yyN < yyTable.length && yyCheck[yyN] == yyState)
          yyState = yyTable[yyN];
        else
          yyState = yyDgoto[yyM];
        continue yyLoop;
      }
    }
  }

					// line 2013 "Ruby19Parser.y"

    /** The parse method use an lexer stream and parse it to an AST node 
     * structure
     */
    public RubyParserResult parse(ParserConfiguration configuration, LexerSource source) {
        support.reset();
        support.setConfiguration(configuration);
        support.setResult(new RubyParserResult());
        
        lexer.reset();
        lexer.setSource(source);
        try {
   //yyparse(lexer, new jay.yydebug.yyAnim("JRuby", 9));
    //yyparse(lexer, new jay.yydebug.yyDebugAdapter());
            yyparse(lexer, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (yyException e) {
            e.printStackTrace();
        }
        
        return support.getResult();
    }

    // +++
    // Helper Methods
    
    void yyerrok() {}

    /**
     * Since we can recieve positions at times we know can be null we
     * need an extra safety net here.
     */
    private ISourcePosition getPosition2(ISourcePositionHolder pos) {
        return pos == null ? lexer.getPosition(null, false) : pos.getPosition();
    }

    private ISourcePosition getPosition(ISourcePositionHolder start) {
        return getPosition(start, false);
    }

    private ISourcePosition getPosition(ISourcePositionHolder start, boolean inclusive) {
        if (start != null) {
            return lexer.getPosition(start.getPosition(), inclusive);
        } 

        return lexer.getPosition(null, inclusive);
    }
}
