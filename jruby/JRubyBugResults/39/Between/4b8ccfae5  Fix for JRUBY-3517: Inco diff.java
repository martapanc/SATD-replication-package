diff --git a/src/org/jruby/compiler/ASTCompiler.java b/src/org/jruby/compiler/ASTCompiler.java
index 46bfe634fa..36958e05df 100644
--- a/src/org/jruby/compiler/ASTCompiler.java
+++ b/src/org/jruby/compiler/ASTCompiler.java
@@ -1720,2001 +1720,2001 @@ public class ASTCompiler {
                 context.pushString("expression");
         }
     }
 
     public void compileDAsgn(Node node, BodyCompiler context, boolean expr) {
         final DAsgnNode dasgnNode = (DAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(dasgnNode.getValueNode(), context, true);
             }
         };
         
         context.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth(), value, expr);
     }
 
     public void compileDAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         DAsgnNode dasgnNode = (DAsgnNode) node;
 
         context.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth(), expr);
     }
 
     public void compileDefn(Node node, BodyCompiler context, boolean expr) {
         final DefnNode defnNode = (DefnNode) node;
         final ArgsNode argsNode = defnNode.getArgsNode();
 
         CompilerCallback body = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (defnNode.getBodyNode() != null) {
                             if (defnNode.getBodyNode() instanceof RescueNode) {
                                 // if root of method is rescue, compile as a light rescue
                                 compileRescueInternal(defnNode.getBodyNode(), context, true);
                             } else {
                                 compile(defnNode.getBodyNode(), context, true);
                             }
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         CompilerCallback args = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compileArgs(argsNode, context, true);
                     }
                 };
 
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         // check args first, since body inspection can depend on args
         inspector.inspect(defnNode.getArgsNode());
 
         // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
         // TODO: this is gross.
         if (defnNode.getBodyNode() instanceof RescueNode) {
             RescueNode rescueNode = (RescueNode)defnNode.getBodyNode();
             inspector.inspect(rescueNode.getBodyNode());
             inspector.inspect(rescueNode.getElseNode());
             inspector.inspect(rescueNode.getRescueNode());
         } else {
             inspector.inspect(defnNode.getBodyNode());
         }
 
         context.defineNewMethod(defnNode.getName(), defnNode.getArgsNode().getArity().getValue(), defnNode.getScope(), body, args, null, inspector, isAtRoot);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileDefs(Node node, BodyCompiler context, boolean expr) {
         final DefsNode defsNode = (DefsNode) node;
         final ArgsNode argsNode = defsNode.getArgsNode();
 
         CompilerCallback receiver = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(defsNode.getReceiverNode(), context, true);
                     }
                 };
 
         CompilerCallback body = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (defsNode.getBodyNode() != null) {
                             if (defsNode.getBodyNode() instanceof RescueNode) {
                                 // if root of method is rescue, compile as light rescue
                                 compileRescueInternal(defsNode.getBodyNode(), context, true);
                             } else {
                                 compile(defsNode.getBodyNode(), context, true);
                             }
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         CompilerCallback args = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compileArgs(argsNode, context, true);
                     }
                 };
 
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(defsNode.getArgsNode());
 
         // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
         // TODO: this is gross.
         if (defsNode.getBodyNode() instanceof RescueNode) {
             RescueNode rescueNode = (RescueNode)defsNode.getBodyNode();
             inspector.inspect(rescueNode.getBodyNode());
             inspector.inspect(rescueNode.getElseNode());
             inspector.inspect(rescueNode.getRescueNode());
         } else {
             inspector.inspect(defsNode.getBodyNode());
         }
 
         context.defineNewMethod(defsNode.getName(), defsNode.getArgsNode().getArity().getValue(), defsNode.getScope(), body, args, receiver, inspector, false);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileArgs(Node node, BodyCompiler context, boolean expr) {
         final ArgsNode argsNode = (ArgsNode) node;
 
         final int required = argsNode.getRequiredArgsCount();
         final int opt = argsNode.getOptionalArgsCount();
         final int rest = argsNode.getRestArg();
 
         ArrayCallback requiredAssignment = null;
         ArrayCallback optionalGiven = null;
         ArrayCallback optionalNotGiven = null;
         CompilerCallback restAssignment = null;
         CompilerCallback blockAssignment = null;
 
         if (required > 0) {
             requiredAssignment = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object object, int index) {
                             // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
                             context.getVariableCompiler().assignLocalVariable(index, false);
                         }
                     };
         }
 
         if (opt > 0) {
             optionalGiven = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object object, int index) {
                             Node optArg = ((ListNode) object).get(index);
 
                             compileAssignment(optArg, context,true);
                             context.consumeCurrentValue();
                         }
                     };
             optionalNotGiven = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object object, int index) {
                             Node optArg = ((ListNode) object).get(index);
 
                             compile(optArg, context,true);
                             context.consumeCurrentValue();
                         }
                     };
         }
 
         if (rest > -1) {
             restAssignment = new CompilerCallback() {
 
                         public void call(BodyCompiler context) {
                             context.getVariableCompiler().assignLocalVariable(argsNode.getRestArg(), false);
                         }
                     };
         }
 
         if (argsNode.getBlock() != null) {
             blockAssignment = new CompilerCallback() {
 
                         public void call(BodyCompiler context) {
                             context.getVariableCompiler().assignLocalVariable(argsNode.getBlock().getCount(), false);
                         }
                     };
         }
 
         context.getVariableCompiler().checkMethodArity(required, opt, rest);
         context.getVariableCompiler().assignMethodArguments(argsNode.getPre(),
                 argsNode.getRequiredArgsCount(),
                 argsNode.getOptArgs(),
                 argsNode.getOptionalArgsCount(),
                 requiredAssignment,
                 optionalGiven,
                 optionalNotGiven,
                 restAssignment,
                 blockAssignment);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileDot(Node node, BodyCompiler context, boolean expr) {
         final DotNode dotNode = (DotNode) node;
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             CompilerCallback beginEndCallback = new CompilerCallback() {
                 public void call(BodyCompiler context) {
                     compile(dotNode.getBeginNode(), context, true);
                     compile(dotNode.getEndNode(), context, true);
                 }
             };
 
             context.createNewRange(beginEndCallback, dotNode.isExclusive());
         }
         if (popit) context.consumeCurrentValue();
     }
 
     public void compileDRegexp(Node node, BodyCompiler context, boolean expr) {
         final DRegexpNode dregexpNode = (DRegexpNode) node;
 
         CompilerCallback createStringCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         ArrayCallback dstrCallback = new ArrayCallback() {
 
                                     public void nextValue(BodyCompiler context, Object sourceArray,
                                             int index) {
                                         compile(dregexpNode.get(index), context, true);
                                     }
                                 };
                         context.createNewString(dstrCallback, dregexpNode.size());
                     }
                 };
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             context.createNewRegexp(createStringCallback, dregexpNode.getOptions());
             if (popit) context.consumeCurrentValue();
         } else {
             // not an expression, only compile the elements
             for (Node nextNode : dregexpNode.childNodes()) {
                 compile(nextNode, context, false);
             }
         }
     }
 
     public void compileDStr(Node node, BodyCompiler context, boolean expr) {
         final DStrNode dstrNode = (DStrNode) node;
 
         ArrayCallback dstrCallback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray,
                             int index) {
                         compile(dstrNode.get(index), context, true);
                     }
                 };
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             context.createNewString(dstrCallback, dstrNode.size());
             if (popit) context.consumeCurrentValue();
         } else {
             // not an expression, only compile the elements
             for (Node nextNode : dstrNode.childNodes()) {
                 compile(nextNode, context, false);
             }
         }
     }
 
     public void compileDSymbol(Node node, BodyCompiler context, boolean expr) {
         final DSymbolNode dsymbolNode = (DSymbolNode) node;
 
         ArrayCallback dstrCallback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray,
                             int index) {
                         compile(dsymbolNode.get(index), context, true);
                     }
                 };
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             context.createNewSymbol(dstrCallback, dsymbolNode.size());
             if (popit) context.consumeCurrentValue();
         } else {
             // not an expression, only compile the elements
             for (Node nextNode : dsymbolNode.childNodes()) {
                 compile(nextNode, context, false);
             }
         }
     }
 
     public void compileDVar(Node node, BodyCompiler context, boolean expr) {
         DVarNode dvarNode = (DVarNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.getVariableCompiler().retrieveLocalVariable(dvarNode.getIndex(), dvarNode.getDepth());
         } else {
             context.getVariableCompiler().retrieveLocalVariable(dvarNode.getIndex(), dvarNode.getDepth());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileDXStr(Node node, BodyCompiler context, boolean expr) {
         final DXStrNode dxstrNode = (DXStrNode) node;
 
         final ArrayCallback dstrCallback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray,
                             int index) {
                         compile(dxstrNode.get(index), context,true);
                     }
                 };
 
         ArgumentsCallback argsCallback = new ArgumentsCallback() {
                     public int getArity() {
                         return 1;
                     }
                     
                     public void call(BodyCompiler context) {
                         context.createNewString(dstrCallback, dxstrNode.size());
                     }
                 };
 
         context.getInvocationCompiler().invokeDynamic("`", null, argsCallback, CallType.FUNCTIONAL, null, false);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileEnsureNode(Node node, BodyCompiler context, boolean expr) {
         final EnsureNode ensureNode = (EnsureNode) node;
 
         if (ensureNode.getEnsureNode() != null) {
             context.performEnsure(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             if (ensureNode.getBodyNode() != null) {
                                 compile(ensureNode.getBodyNode(), context, true);
                             } else {
                                 context.loadNil();
                             }
                         }
                     },
                     new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(ensureNode.getEnsureNode(), context, false);
                         }
                     });
         } else {
             if (ensureNode.getBodyNode() != null) {
                 compile(ensureNode.getBodyNode(), context,true);
             } else {
                 context.loadNil();
             }
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileEvStr(Node node, BodyCompiler context, boolean expr) {
         final EvStrNode evStrNode = (EvStrNode) node;
 
         compile(evStrNode.getBody(), context,true);
         context.asString();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileFalse(Node node, BodyCompiler context, boolean expr) {
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) {
                 context.loadFalse();
                 context.pollThreadEvents();
             }
         } else {
             context.loadFalse();
             context.pollThreadEvents();
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileFCall(Node node, BodyCompiler context, boolean expr) {
         final FCallNode fcallNode = (FCallNode) node;
 
         ArgumentsCallback argsCallback = getArgsCallback(fcallNode.getArgsNode());
         
         CompilerCallback closureArg = getBlock(fcallNode.getIterNode());
 
         context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, argsCallback, CallType.FUNCTIONAL, closureArg, fcallNode.getIterNode() instanceof IterNode);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     private CompilerCallback getBlock(Node node) {
         if (node == null) {
             return null;
         }
 
         switch (node.getNodeType()) {
             case ITERNODE:
                 final IterNode iterNode = (IterNode) node;
 
                 return new CompilerCallback() {
 
                             public void call(BodyCompiler context) {
                                 compile(iterNode, context,true);
                             }
                         };
             case BLOCKPASSNODE:
                 final BlockPassNode blockPassNode = (BlockPassNode) node;
 
                 return new CompilerCallback() {
 
                             public void call(BodyCompiler context) {
                                 compile(blockPassNode.getBodyNode(), context,true);
                                 context.unwrapPassedBlock();
                             }
                         };
             default:
                 throw new NotCompilableException("ERROR: Encountered a method with a non-block, non-blockpass iter node at: " + node);
         }
     }
 
     public void compileFixnum(Node node, BodyCompiler context, boolean expr) {
         FixnumNode fixnumNode = (FixnumNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.createNewFixnum(fixnumNode.getValue());
         } else {
             context.createNewFixnum(fixnumNode.getValue());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileFlip(Node node, BodyCompiler context, boolean expr) {
         final FlipNode flipNode = (FlipNode) node;
 
         context.getVariableCompiler().retrieveLocalVariable(flipNode.getIndex(), flipNode.getDepth());
 
         if (flipNode.isExclusive()) {
             context.performBooleanBranch(new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getEndNode(), context,true);
                     context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.loadFalse();
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), false);
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                         }
                     });
                     context.loadTrue();
                 }
             }, new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getBeginNode(), context,true);
                     becomeTrueOrFalse(context);
                     context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), true);
                 }
             });
         } else {
             context.performBooleanBranch(new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getEndNode(), context,true);
                     context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.loadFalse();
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), false);
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                         }
                     });
                     context.loadTrue();
                 }
             }, new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getBeginNode(), context,true);
                     context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(flipNode.getEndNode(), context,true);
                             flipTrueOrFalse(context);
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), false);
                             context.loadTrue();
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.loadFalse();
                         }
                     });
                 }
             });
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     private void becomeTrueOrFalse(BodyCompiler context) {
         context.performBooleanBranch(new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.loadTrue();
                     }
                 }, new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.loadFalse();
                     }
                 });
     }
 
     private void flipTrueOrFalse(BodyCompiler context) {
         context.performBooleanBranch(new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.loadFalse();
                     }
                 }, new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.loadTrue();
                     }
                 });
     }
 
     public void compileFloat(Node node, BodyCompiler context, boolean expr) {
         FloatNode floatNode = (FloatNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.createNewFloat(floatNode.getValue());
         } else {
             context.createNewFloat(floatNode.getValue());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileFor(Node node, BodyCompiler context, boolean expr) {
         final ForNode forNode = (ForNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(forNode.getIterNode(), context, true);
                     }
                 };
 
         final CompilerCallback closureArg = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compileForIter(forNode, context);
                     }
                 };
 
         context.getInvocationCompiler().invokeDynamic("each", receiverCallback, null, CallType.NORMAL, closureArg, true);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileForIter(Node node, BodyCompiler context) {
         final ForNode forNode = (ForNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (forNode.getBodyNode() != null) {
                             compile(forNode.getBodyNode(), context,true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         // create the closure class and instantiate it
         final CompilerCallback closureArgs = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (forNode.getVarNode() != null) {
                             compileAssignment(forNode.getVarNode(), context, false);
                         }
                     }
                 };
 
         boolean hasMultipleArgsHead = false;
         if (forNode.getVarNode() instanceof MultipleAsgnNode) {
             hasMultipleArgsHead = ((MultipleAsgnNode) forNode.getVarNode()).getHeadNode() != null;
         }
 
         NodeType argsNodeId = null;
         if (forNode.getVarNode() != null) {
             argsNodeId = forNode.getVarNode().getNodeType();
         }
         
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(forNode.getBodyNode());
         inspector.inspect(forNode.getVarNode());
 
         // force heap-scope behavior, since it uses parent's scope
         inspector.setFlag(ASTInspector.CLOSURE);
 
         if (argsNodeId == null) {
             // no args, do not pass args processor
             context.createNewForLoop(Arity.procArityOf(forNode.getVarNode()).getValue(),
                     closureBody, null, hasMultipleArgsHead, argsNodeId, inspector);
         } else {
             context.createNewForLoop(Arity.procArityOf(forNode.getVarNode()).getValue(),
                     closureBody, closureArgs, hasMultipleArgsHead, argsNodeId, inspector);
         }
     }
 
     public void compileGlobalAsgn(Node node, BodyCompiler context, boolean expr) {
         final GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(globalAsgnNode.getValueNode(), context, true);
             }
         };
 
         if (globalAsgnNode.getName().length() == 2) {
             switch (globalAsgnNode.getName().charAt(1)) {
             case '_':
                 context.getVariableCompiler().assignLastLine(value);
                 break;
             case '~':
                 context.getVariableCompiler().assignBackRef(value);
                 break;
             default:
                 context.assignGlobalVariable(globalAsgnNode.getName(), value);
             }
         } else {
             context.assignGlobalVariable(globalAsgnNode.getName(), value);
         }
 
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileGlobalAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode) node;
 
         if (globalAsgnNode.getName().length() == 2) {
             switch (globalAsgnNode.getName().charAt(1)) {
             case '_':
                 context.getVariableCompiler().assignLastLine();
                 break;
             case '~':
                 context.getVariableCompiler().assignBackRef();
                 break;
             default:
                 context.assignGlobalVariable(globalAsgnNode.getName());
             }
         } else {
             context.assignGlobalVariable(globalAsgnNode.getName());
         }
         
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileGlobalVar(Node node, BodyCompiler context, boolean expr) {
         GlobalVarNode globalVarNode = (GlobalVarNode) node;
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
         
         if (doit) {
             if (globalVarNode.getName().length() == 2) {
                 switch (globalVarNode.getName().charAt(1)) {
                 case '_':
                     context.getVariableCompiler().retrieveLastLine();
                     break;
                 case '~':
                     context.getVariableCompiler().retrieveBackRef();
                     break;
                 default:
                     context.retrieveGlobalVariable(globalVarNode.getName());
                 }
             } else {
                 context.retrieveGlobalVariable(globalVarNode.getName());
             }
         }
         
         if (popit) context.consumeCurrentValue();
     }
 
     public void compileHash(Node node, BodyCompiler context, boolean expr) {
         HashNode hashNode = (HashNode) node;
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             if (hashNode.getListNode() == null || hashNode.getListNode().size() == 0) {
                 context.createEmptyHash();
                 return;
             }
 
             ArrayCallback hashCallback = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object sourceArray,
                                 int index) {
                             ListNode listNode = (ListNode) sourceArray;
                             int keyIndex = index * 2;
                             compile(listNode.get(keyIndex), context, true);
                             compile(listNode.get(keyIndex + 1), context, true);
                         }
                     };
 
             context.createNewHash(hashNode.getListNode(), hashCallback, hashNode.getListNode().size() / 2);
             if (popit) context.consumeCurrentValue();
         } else {
             for (Node nextNode : hashNode.getListNode().childNodes()) {
                 compile(nextNode, context, false);
             }
         }
     }
 
     public void compileIf(Node node, BodyCompiler context, final boolean expr) {
         final IfNode ifNode = (IfNode) node;
 
         // optimizations if we know ahead of time it will always be true or false
         Node actualCondition = ifNode.getCondition();
         while (actualCondition instanceof NewlineNode) {
             actualCondition = ((NewlineNode)actualCondition).getNextNode();
         }
 
         if (actualCondition.getNodeType().alwaysTrue()) {
             // compile condition as non-expr and just compile "then" body
             compile(actualCondition, context, false);
             compile(ifNode.getThenBody(), context, expr);
         } else if (actualCondition.getNodeType().alwaysFalse()) {
             // always false or nil
             compile(ifNode.getElseBody(), context, expr);
         } else {
             BranchCallback trueCallback = new BranchCallback() {
                 public void branch(BodyCompiler context) {
                     if (ifNode.getThenBody() != null) {
                         compile(ifNode.getThenBody(), context, expr);
                     } else {
                         if (expr) context.loadNil();
                     }
                 }
             };
 
             BranchCallback falseCallback = new BranchCallback() {
                 public void branch(BodyCompiler context) {
                     if (ifNode.getElseBody() != null) {
                         compile(ifNode.getElseBody(), context, expr);
                     } else {
                         if (expr) context.loadNil();
                     }
                 }
             };
             
             // normal
             compile(actualCondition, context, true);
             context.performBooleanBranch(trueCallback, falseCallback);
         }
     }
 
     public void compileInstAsgn(Node node, BodyCompiler context, boolean expr) {
         final InstAsgnNode instAsgnNode = (InstAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(instAsgnNode.getValueNode(), context, true);
             }
         };
 
         context.assignInstanceVariable(instAsgnNode.getName(), value);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileInstAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         InstAsgnNode instAsgnNode = (InstAsgnNode) node;
         context.assignInstanceVariable(instAsgnNode.getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileInstVar(Node node, BodyCompiler context, boolean expr) {
         InstVarNode instVarNode = (InstVarNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.retrieveInstanceVariable(instVarNode.getName());
         } else {
             context.retrieveInstanceVariable(instVarNode.getName());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileIter(Node node, BodyCompiler context) {
         final IterNode iterNode = (IterNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (iterNode.getBodyNode() != null) {
                             compile(iterNode.getBodyNode(), context, true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         // create the closure class and instantiate it
         final CompilerCallback closureArgs = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (iterNode.getVarNode() != null) {
                             compileAssignment(iterNode.getVarNode(), context, false);
                         }
                     }
                 };
 
         boolean hasMultipleArgsHead = false;
         if (iterNode.getVarNode() instanceof MultipleAsgnNode) {
             hasMultipleArgsHead = ((MultipleAsgnNode) iterNode.getVarNode()).getHeadNode() != null;
         }
 
         NodeType argsNodeId = BlockBody.getArgumentTypeWackyHack(iterNode);
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(iterNode.getBodyNode());
         inspector.inspect(iterNode.getVarNode());
         
         if (argsNodeId == null) {
             // no args, do not pass args processor
             context.createNewClosure(iterNode.getPosition().getStartLine(), iterNode.getScope(), Arity.procArityOf(iterNode.getVarNode()).getValue(),
                     closureBody, null, hasMultipleArgsHead, argsNodeId, inspector);
         } else {
             context.createNewClosure(iterNode.getPosition().getStartLine(), iterNode.getScope(), Arity.procArityOf(iterNode.getVarNode()).getValue(),
                     closureBody, closureArgs, hasMultipleArgsHead, argsNodeId, inspector);
         }
     }
 
     public void compileLocalAsgn(Node node, BodyCompiler context, boolean expr) {
         final LocalAsgnNode localAsgnNode = (LocalAsgnNode) node;
 
         // just push nil for pragmas
         if (ASTInspector.PRAGMAS.contains(localAsgnNode.getName())) {
             if (expr) context.loadNil();
         } else {
             CompilerCallback value = new CompilerCallback() {
                 public void call(BodyCompiler context) {
                     compile(localAsgnNode.getValueNode(), context,true);
                 }
             };
 
             context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth(), value, expr);
         }
     }
 
     public void compileLocalAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         // "assignment" means the value is already on the stack
         LocalAsgnNode localAsgnNode = (LocalAsgnNode) node;
 
         context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth(), expr);
     }
 
     public void compileLocalVar(Node node, BodyCompiler context, boolean expr) {
         LocalVarNode localVarNode = (LocalVarNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.getVariableCompiler().retrieveLocalVariable(localVarNode.getIndex(), localVarNode.getDepth());
         } else {
             context.getVariableCompiler().retrieveLocalVariable(localVarNode.getIndex(), localVarNode.getDepth());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileMatch(Node node, BodyCompiler context, boolean expr) {
         MatchNode matchNode = (MatchNode) node;
 
         compile(matchNode.getRegexpNode(), context,true);
 
         context.match();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileMatch2(Node node, BodyCompiler context, boolean expr) {
         final Match2Node matchNode = (Match2Node) node;
 
         compile(matchNode.getReceiverNode(), context,true);
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(matchNode.getValueNode(), context,true);
             }
         };
 
         context.match2(value);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileMatch3(Node node, BodyCompiler context, boolean expr) {
         Match3Node matchNode = (Match3Node) node;
 
         compile(matchNode.getReceiverNode(), context,true);
         compile(matchNode.getValueNode(), context,true);
 
         context.match3();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileModule(Node node, BodyCompiler context, boolean expr) {
         final ModuleNode moduleNode = (ModuleNode) node;
 
         final Node cpathNode = moduleNode.getCPath();
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (moduleNode.getBodyNode() != null) {
                             compile(moduleNode.getBodyNode(), context,true);
                         }
                         context.loadNil();
                     }
                 };
 
         CompilerCallback pathCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (cpathNode instanceof Colon2Node) {
                             Node leftNode = ((Colon2Node) cpathNode).getLeftNode();
                             if (leftNode != null) {
                                 compile(leftNode, context,true);
                             } else {
                                 context.loadNil();
                             }
                         } else if (cpathNode instanceof Colon3Node) {
                             context.loadObject();
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(moduleNode.getBodyNode());
 
         context.defineModule(moduleNode.getCPath().getName(), moduleNode.getScope(), pathCallback, bodyCallback, inspector);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileMultipleAsgn(Node node, BodyCompiler context, boolean expr) {
         MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode) node;
 
         if (expr) {
             // need the array, use unoptz version
             compileUnoptimizedMultipleAsgn(multipleAsgnNode, context, expr);
         } else {
             // try optz version
             compileOptimizedMultipleAsgn(multipleAsgnNode, context, expr);
         }
     }
 
     private void compileOptimizedMultipleAsgn(MultipleAsgnNode multipleAsgnNode, BodyCompiler context, boolean expr) {
         // expect value to be an array of nodes
         if (multipleAsgnNode.getValueNode() instanceof ArrayNode) {
             // head must not be null and there must be no "args" (like *arg)
             if (multipleAsgnNode.getHeadNode() != null && multipleAsgnNode.getArgsNode() == null) {
                 // sizes must match
                 if (multipleAsgnNode.getHeadNode().size() == ((ArrayNode)multipleAsgnNode.getValueNode()).size()) {
                     // "head" must have no non-trivial assigns (array groupings, basically)
                     boolean normalAssigns = true;
                     for (Node asgn : multipleAsgnNode.getHeadNode().childNodes()) {
                         if (asgn instanceof ListNode) {
                             normalAssigns = false;
                             break;
                         }
                     }
                     
                     if (normalAssigns) {
-                        // only supports simple parallel assignment of up to 4 values to the same number of assignees
+                        // only supports simple parallel assignment of up to 10 values to the same number of assignees
                         int size = multipleAsgnNode.getHeadNode().size();
                         if (size >= 2 && size <= 10) {
                             ArrayNode values = (ArrayNode)multipleAsgnNode.getValueNode();
                             for (Node value : values.childNodes()) {
                                 compile(value, context, true);
                             }
                             context.reverseValues(size);
                             for (Node asgn : multipleAsgnNode.getHeadNode().childNodes()) {
                                 compileAssignment(asgn, context, false);
                             }
                             return;
                         }
                     }
                 }
             }
         }
 
         // if we get here, no optz cases work; fall back on unoptz.
         compileUnoptimizedMultipleAsgn(multipleAsgnNode, context, expr);
     }
 
     private void compileUnoptimizedMultipleAsgn(MultipleAsgnNode multipleAsgnNode, BodyCompiler context, boolean expr) {
         compile(multipleAsgnNode.getValueNode(), context, true);
 
         compileMultipleAsgnAssignment(multipleAsgnNode, context, expr);
     }
 
     public void compileMultipleAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         final MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode) node;
 
         // normal items at the "head" of the masgn
         ArrayCallback headAssignCallback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray,
                             int index) {
                         ListNode headNode = (ListNode) sourceArray;
                         Node assignNode = headNode.get(index);
 
                         // perform assignment for the next node
                         compileAssignment(assignNode, context, false);
                     }
                 };
 
         CompilerCallback argsCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         Node argsNode = multipleAsgnNode.getArgsNode();
                         if (argsNode instanceof StarNode) {
                             // done processing args
                             context.consumeCurrentValue();
                         } else {
                             // assign to appropriate variable
                             compileAssignment(argsNode, context, false);
                         }
                     }
                 };
 
         if (multipleAsgnNode.getHeadNode() == null) {
             if (multipleAsgnNode.getArgsNode() == null) {
                 throw new NotCompilableException("Something's wrong, multiple assignment with no head or args at: " + multipleAsgnNode.getPosition());
             } else {
                 if (multipleAsgnNode.getArgsNode() instanceof StarNode) {
                     // do nothing
                 } else {
                     context.ensureMultipleAssignableRubyArray(multipleAsgnNode.getHeadNode() != null);
 
                     context.forEachInValueArray(0, 0, null, null, argsCallback);
                 }
             }
         } else {
             context.ensureMultipleAssignableRubyArray(multipleAsgnNode.getHeadNode() != null);
             
             if (multipleAsgnNode.getArgsNode() == null) {
                 context.forEachInValueArray(0, multipleAsgnNode.getHeadNode().size(), multipleAsgnNode.getHeadNode(), headAssignCallback, null);
             } else {
                 context.forEachInValueArray(0, multipleAsgnNode.getHeadNode().size(), multipleAsgnNode.getHeadNode(), headAssignCallback, argsCallback);
             }
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileNewline(Node node, BodyCompiler context, boolean expr) {
         // TODO: add trace call?
         context.lineNumber(node.getPosition());
 
         context.setLinePosition(node.getPosition());
 
         NewlineNode newlineNode = (NewlineNode) node;
 
         compile(newlineNode.getNextNode(), context, expr);
     }
 
     public void compileNext(Node node, BodyCompiler context, boolean expr) {
         final NextNode nextNode = (NextNode) node;
 
         CompilerCallback valueCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (nextNode.getValueNode() != null) {
                             compile(nextNode.getValueNode(), context,true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         context.pollThreadEvents();
         context.issueNextEvent(valueCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileNthRef(Node node, BodyCompiler context, boolean expr) {
         NthRefNode nthRefNode = (NthRefNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.nthRef(nthRefNode.getMatchNumber());
         } else {
             context.nthRef(nthRefNode.getMatchNumber());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileNil(Node node, BodyCompiler context, boolean expr) {
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) {
                 context.loadNil();
                 context.pollThreadEvents();
             }
         } else {
             context.loadNil();
             context.pollThreadEvents();
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileNot(Node node, BodyCompiler context, boolean expr) {
         NotNode notNode = (NotNode) node;
 
         compile(notNode.getConditionNode(), context, true);
 
         context.negateCurrentValue();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpAsgnAnd(Node node, BodyCompiler context, boolean expr) {
         final BinaryOperatorNode andNode = (BinaryOperatorNode) node;
 
         compile(andNode.getFirstNode(), context,true);
 
         BranchCallback longCallback = new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         compile(andNode.getSecondNode(), context,true);
                     }
                 };
 
         context.performLogicalAnd(longCallback);
         context.pollThreadEvents();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpAsgnOr(Node node, BodyCompiler context, boolean expr) {
         final OpAsgnOrNode orNode = (OpAsgnOrNode) node;
 
         if (needsDefinitionCheck(orNode.getFirstNode())) {
             compileGetDefinitionBase(orNode.getFirstNode(), context);
 
             context.isNull(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(orNode.getSecondNode(), context,true);
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(orNode.getFirstNode(), context,true);
                             context.duplicateCurrentValue();
                             context.performBooleanBranch(new BranchCallback() {
 
                                         public void branch(BodyCompiler context) {
                                         //Do nothing
                                         }
                                     },
                                     new BranchCallback() {
 
                                         public void branch(BodyCompiler context) {
                                             context.consumeCurrentValue();
                                             compile(orNode.getSecondNode(), context,true);
                                         }
                                     });
                         }
                     });
         } else {
             compile(orNode.getFirstNode(), context,true);
             context.duplicateCurrentValue();
             context.performBooleanBranch(new BranchCallback() {
                 public void branch(BodyCompiler context) {
                 //Do nothing
                 }
             },
             new BranchCallback() {
                 public void branch(BodyCompiler context) {
                     context.consumeCurrentValue();
                     compile(orNode.getSecondNode(), context,true);
                 }
             });
 
         }
 
         context.pollThreadEvents();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     /**
      * Check whether the given node is considered always "defined" or whether it
      * has some form of definition check.
      *
      * @param node Then node to check
      * @return Whether the type of node represents a possibly undefined construct
      */
     private boolean needsDefinitionCheck(Node node) {
         switch (node.getNodeType()) {
         case CLASSVARASGNNODE:
         case CLASSVARDECLNODE:
         case CONSTDECLNODE:
         case DASGNNODE:
         case GLOBALASGNNODE:
         case LOCALASGNNODE:
         case MULTIPLEASGNNODE:
         case OPASGNNODE:
         case OPELEMENTASGNNODE:
         case DVARNODE:
         case FALSENODE:
         case TRUENODE:
         case LOCALVARNODE:
         case MATCH2NODE:
         case MATCH3NODE:
         case NILNODE:
         case SELFNODE:
             // all these types are immediately considered "defined"
             return false;
         default:
             return true;
         }
     }
 
     public void compileOpAsgn(Node node, BodyCompiler context, boolean expr) {
         final OpAsgnNode opAsgnNode = (OpAsgnNode) node;
 
         if (opAsgnNode.getOperatorName().equals("||")) {
             compileOpAsgnWithOr(opAsgnNode, context, true);
         } else if (opAsgnNode.getOperatorName().equals("&&")) {
             compileOpAsgnWithAnd(opAsgnNode, context, true);
         } else {
             compileOpAsgnWithMethod(opAsgnNode, context, true);
         }
 
         context.pollThreadEvents();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpAsgnWithOr(Node node, BodyCompiler context, boolean expr) {
         final OpAsgnNode opAsgnNode = (OpAsgnNode) node;
 
         final CompilerCallback receiverCallback = new CompilerCallback() {
 
             public void call(BodyCompiler context) {
                 compile(opAsgnNode.getReceiverNode(), context, true); // [recv]
             }
         };
         
         ArgumentsCallback argsCallback = getArgsCallback(opAsgnNode.getValueNode());
         
         context.getInvocationCompiler().invokeOpAsgnWithOr(opAsgnNode.getVariableName(), opAsgnNode.getVariableNameAsgn(), receiverCallback, argsCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpAsgnWithAnd(Node node, BodyCompiler context, boolean expr) {
         final OpAsgnNode opAsgnNode = (OpAsgnNode) node;
 
         final CompilerCallback receiverCallback = new CompilerCallback() {
 
             public void call(BodyCompiler context) {
                 compile(opAsgnNode.getReceiverNode(), context, true); // [recv]
             }
         };
         
         ArgumentsCallback argsCallback = getArgsCallback(opAsgnNode.getValueNode());
         
         context.getInvocationCompiler().invokeOpAsgnWithAnd(opAsgnNode.getVariableName(), opAsgnNode.getVariableNameAsgn(), receiverCallback, argsCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpAsgnWithMethod(Node node, BodyCompiler context, boolean expr) {
         final OpAsgnNode opAsgnNode = (OpAsgnNode) node;
 
         final CompilerCallback receiverCallback = new CompilerCallback() {
                     public void call(BodyCompiler context) {
                         compile(opAsgnNode.getReceiverNode(), context, true); // [recv]
                     }
                 };
 
         // eval new value, call operator on old value, and assign
         ArgumentsCallback argsCallback = new ArgumentsCallback() {
             public int getArity() {
                 return 1;
             }
 
             public void call(BodyCompiler context) {
                 compile(opAsgnNode.getValueNode(), context, true);
             }
         };
         
         context.getInvocationCompiler().invokeOpAsgnWithMethod(opAsgnNode.getOperatorName(), opAsgnNode.getVariableName(), opAsgnNode.getVariableNameAsgn(), receiverCallback, argsCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpElementAsgn(Node node, BodyCompiler context, boolean expr) {
         final OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode) node;
         
         if (opElementAsgnNode.getOperatorName() == "||") {
             compileOpElementAsgnWithOr(node, context, expr);
         } else if (opElementAsgnNode.getOperatorName() == "&&") {
             compileOpElementAsgnWithAnd(node, context, expr);
         } else {
             compileOpElementAsgnWithMethod(node, context, expr);
         }
     }
     
     private class OpElementAsgnArgumentsCallback implements ArgumentsCallback  {
         private Node node;
 
         public OpElementAsgnArgumentsCallback(Node node) {
             this.node = node;
         }
         
         public int getArity() {
             switch (node.getNodeType()) {
             case ARGSCATNODE:
             case ARGSPUSHNODE:
             case SPLATNODE:
                 return -1;
             case ARRAYNODE:
                 ArrayNode arrayNode = (ArrayNode)node;
                 if (arrayNode.size() == 0) {
                     return 0;
                 } else if (arrayNode.size() > 3) {
                     return -1;
                 } else {
                     return ((ArrayNode)node).size();
                 }
             default:
                 return 1;
             }
         }
 
         public void call(BodyCompiler context) {
             if (getArity() == 1) {
                 // if arity 1, just compile the one element to save us the array cost
                 compile(((ArrayNode)node).get(0), context,true);
             } else {
                 // compile into array
                 compileArguments(node, context);
             }
         }
     };
 
     public void compileOpElementAsgnWithOr(Node node, BodyCompiler context, boolean expr) {
         final OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(opElementAsgnNode.getReceiverNode(), context, true);
             }
         };
 
         ArgumentsCallback argsCallback = new OpElementAsgnArgumentsCallback(opElementAsgnNode.getArgsNode());
 
         CompilerCallback valueCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(opElementAsgnNode.getValueNode(), context, true);
             }
         };
 
         context.getInvocationCompiler().opElementAsgnWithOr(receiverCallback, argsCallback, valueCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpElementAsgnWithAnd(Node node, BodyCompiler context, boolean expr) {
         final OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(opElementAsgnNode.getReceiverNode(), context, true);
             }
         };
 
         ArgumentsCallback argsCallback = new OpElementAsgnArgumentsCallback(opElementAsgnNode.getArgsNode()); 
 
         CompilerCallback valueCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(opElementAsgnNode.getValueNode(), context, true);
             }
         };
 
         context.getInvocationCompiler().opElementAsgnWithAnd(receiverCallback, argsCallback, valueCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpElementAsgnWithMethod(Node node, BodyCompiler context, boolean expr) {
         final OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(opElementAsgnNode.getReceiverNode(), context,true);
             }
         };
 
         ArgumentsCallback argsCallback = getArgsCallback(opElementAsgnNode.getArgsNode());
 
         CompilerCallback valueCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(opElementAsgnNode.getValueNode(), context,true);
             }
         };
 
         context.getInvocationCompiler().opElementAsgnWithMethod(receiverCallback, argsCallback, valueCallback, opElementAsgnNode.getOperatorName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOr(Node node, BodyCompiler context, boolean expr) {
         final OrNode orNode = (OrNode) node;
 
         if (orNode.getFirstNode().getNodeType().alwaysTrue()) {
             // compile first node only
             compile(orNode.getFirstNode(), context, expr);
         } else if (orNode.getFirstNode().getNodeType().alwaysFalse()) {
             // compile first node as non-expr and compile second node
             compile(orNode.getFirstNode(), context, false);
             compile(orNode.getSecondNode(), context, expr);
         } else {
             compile(orNode.getFirstNode(), context, true);
 
             BranchCallback longCallback = new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(orNode.getSecondNode(), context, true);
                         }
                     };
 
             context.performLogicalOr(longCallback);
             // TODO: don't require pop
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compilePostExe(Node node, BodyCompiler context, boolean expr) {
         final PostExeNode postExeNode = (PostExeNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (postExeNode.getBodyNode() != null) {
                             compile(postExeNode.getBodyNode(), context, true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
         context.createNewEndBlock(closureBody);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compilePreExe(Node node, BodyCompiler context, boolean expr) {
         final PreExeNode preExeNode = (PreExeNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (preExeNode.getBodyNode() != null) {
                             compile(preExeNode.getBodyNode(), context,true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
         context.runBeginBlock(preExeNode.getScope(), closureBody);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileRedo(Node node, BodyCompiler context, boolean expr) {
         //RedoNode redoNode = (RedoNode)node;
 
         context.issueRedoEvent();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileRegexp(Node node, BodyCompiler context, boolean expr) {
         RegexpNode reNode = (RegexpNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.createNewRegexp(reNode.getValue(), reNode.getOptions());
         } else {
             context.createNewRegexp(reNode.getValue(), reNode.getOptions());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileRescue(Node node, BodyCompiler context, boolean expr) {
         compileRescueInternal(node, context, false);
         
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     private void compileRescueInternal(Node node, BodyCompiler context, final boolean light) {
         final RescueNode rescueNode = (RescueNode) node;
 
         BranchCallback body = new BranchCallback() {
             public void branch(BodyCompiler context) {
                 if (rescueNode.getBodyNode() != null) {
                     compile(rescueNode.getBodyNode(), context, true);
                 } else {
                     context.loadNil();
                 }
 
                 if (rescueNode.getElseNode() != null) {
                     context.consumeCurrentValue();
                     compile(rescueNode.getElseNode(), context, true);
                 }
             }
         };
 
         BranchCallback rubyHandler = new BranchCallback() {
             public void branch(BodyCompiler context) {
                 compileRescueBodyInternal(rescueNode.getRescueNode(), context, light);
             }
         };
 
         ASTInspector rescueInspector = new ASTInspector();
         rescueInspector.inspect(rescueNode.getRescueNode());
         if (light) {
             context.performRescueLight(body, rubyHandler, rescueInspector.getFlag(ASTInspector.RETRY));
         } else {
             context.performRescue(body, rubyHandler, rescueInspector.getFlag(ASTInspector.RETRY));
         }
     }
 
     private void compileRescueBodyInternal(Node node, BodyCompiler context, final boolean light) {
         final RescueBodyNode rescueBodyNode = (RescueBodyNode) node;
 
         context.loadException();
 
         final Node exceptionList = rescueBodyNode.getExceptionNodes();
         ArgumentsCallback rescueArgs = getArgsCallback(exceptionList);
         if (rescueArgs == null) rescueArgs = new ArgumentsCallback() {
             public int getArity() {
                 return 1;
             }
 
             public void call(BodyCompiler context) {
                 context.loadStandardError();
             }
         };
 
         context.checkIsExceptionHandled(rescueArgs);
 
         BranchCallback trueBranch = new BranchCallback() {
             public void branch(BodyCompiler context) {
                 // check if it's an immediate, and don't outline
                 Node realBody = rescueBodyNode.getBodyNode();
                 if (realBody instanceof NewlineNode) {
                     context.setLinePosition(realBody.getPosition());
                     while (realBody instanceof NewlineNode) {
                         realBody = ((NewlineNode)realBody).getNextNode();
                     }
                 }
 
                 if (realBody.getNodeType().isImmediate()) {
                     compile(realBody, context, true);
                     context.clearErrorInfo();
                 } else {
                     context.storeExceptionInErrorInfo();
                     if (light) {
                         compile(rescueBodyNode.getBodyNode(), context, true);
                     } else {
                         BodyCompiler nestedBody = context.outline("rescue_line_" + rescueBodyNode.getPosition().getStartLine());
                         compile(rescueBodyNode.getBodyNode(), nestedBody, true);
                         nestedBody.endBody();
                     }
 
                     // FIXME: this should reset to what it was before
                     context.clearErrorInfo();
                 }
             }
         };
 
         BranchCallback falseBranch = new BranchCallback() {
             public void branch(BodyCompiler context) {
                 if (rescueBodyNode.getOptRescueNode() != null) {
                     compileRescueBodyInternal(rescueBodyNode.getOptRescueNode(), context, light);
                 } else {
                     context.rethrowException();
                 }
             }
         };
 
         context.performBooleanBranch(trueBranch, falseBranch);
     }
 
     public void compileRetry(Node node, BodyCompiler context, boolean expr) {
         context.pollThreadEvents();
 
         context.issueRetryEvent();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileReturn(Node node, BodyCompiler context, boolean expr) {
         ReturnNode returnNode = (ReturnNode) node;
 
         if (returnNode.getValueNode() != null) {
             compile(returnNode.getValueNode(), context,true);
         } else {
             context.loadNil();
         }
 
         context.performReturn();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileRoot(Node node, ScriptCompiler context, ASTInspector inspector) {
         compileRoot(node, context, inspector, true, true);
     }
 
     public void compileRoot(Node node, ScriptCompiler context, ASTInspector inspector, boolean load, boolean main) {
         RootNode rootNode = (RootNode) node;
         StaticScope staticScope = rootNode.getStaticScope();
 
         context.startScript(staticScope);
 
         // force static scope to claim restarg at 0, so it only implements the [] version of __file__
         staticScope.setRestArg(-2);
 
         // create method for toplevel of script
         BodyCompiler methodCompiler = context.startRoot("__file__", "__file__", staticScope, inspector);
 
         Node nextNode = rootNode.getBodyNode();
         if (nextNode != null) {
             if (nextNode.getNodeType() == NodeType.BLOCKNODE) {
                 // it's a multiple-statement body, iterate over all elements in turn and chain if it get too long
                 BlockNode blockNode = (BlockNode) nextNode;
 
                 for (int i = 0; i < blockNode.size(); i++) {
                     if ((i + 1) % RubyInstanceConfig.CHAINED_COMPILE_LINE_COUNT == 0) {
                         methodCompiler = methodCompiler.chainToMethod("__file__from_line_" + (i + 1));
                     }
                     compile(blockNode.get(i), methodCompiler, i + 1 >= blockNode.size());
                 }
             } else {
                 // single-statement body, just compile it
                 compile(nextNode, methodCompiler,true);
             }
         } else {
             methodCompiler.loadNil();
         }
 
         methodCompiler.endBody();
 
         context.endScript(load, main);
     }
 
     public void compileSelf(Node node, BodyCompiler context, boolean expr) {
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.retrieveSelf();
         } else {
             context.retrieveSelf();
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileSplat(Node node, BodyCompiler context, boolean expr) {
         SplatNode splatNode = (SplatNode) node;
 
         compile(splatNode.getValue(), context, true);
 
         context.splatCurrentValue();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileStr(Node node, BodyCompiler context, boolean expr) {
         StrNode strNode = (StrNode) node;
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             if (strNode instanceof FileNode) {
                 context.loadFilename();
             } else {
                 context.createNewString(strNode.getValue());
             }
         }
         if (popit) context.consumeCurrentValue();
     }
 
     public void compileSuper(Node node, BodyCompiler context, boolean expr) {
         final SuperNode superNode = (SuperNode) node;
 
         CompilerCallback argsCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compileArguments(superNode.getArgsNode(), context);
                     }
                 };
 
 
         if (superNode.getIterNode() == null) {
             // no block, go for simple version
             if (superNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeSuper(argsCallback, null);
             } else {
                 context.getInvocationCompiler().invokeSuper(null, null);
             }
         } else {
             CompilerCallback closureArg = getBlock(superNode.getIterNode());
 
             if (superNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeSuper(argsCallback, closureArg);
             } else {
                 context.getInvocationCompiler().invokeSuper(null, closureArg);
             }
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileSValue(Node node, BodyCompiler context, boolean expr) {
         SValueNode svalueNode = (SValueNode) node;
 
         compile(svalueNode.getValue(), context,true);
 
         context.singlifySplattedValue();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileSymbol(Node node, BodyCompiler context, boolean expr) {
         context.createNewSymbol(((SymbolNode) node).getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }    
     
     public void compileToAry(Node node, BodyCompiler context, boolean expr) {
         ToAryNode toAryNode = (ToAryNode) node;
 
         compile(toAryNode.getValue(), context,true);
 
         context.aryToAry();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileTrue(Node node, BodyCompiler context, boolean expr) {
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) {
                 context.loadTrue();
                 context.pollThreadEvents();
             }
         } else {
             context.loadTrue();
             context.pollThreadEvents();
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileUndef(Node node, BodyCompiler context, boolean expr) {
         context.undefMethod(((UndefNode) node).getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileUntil(Node node, BodyCompiler context, boolean expr) {
         final UntilNode untilNode = (UntilNode) node;
 
         if (untilNode.getConditionNode().getNodeType().alwaysTrue() &&
                 untilNode.evaluateAtStart()) {
             // condition is always true, just compile it and not body
             compile(untilNode.getConditionNode(), context, false);
             if (expr) context.loadNil();
         } else {
             BranchCallback condition = new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(untilNode.getConditionNode(), context, true);
                     context.negateCurrentValue();
                 }
             };
 
             BranchCallback body = new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     if (untilNode.getBodyNode() != null) {
                         compile(untilNode.getBodyNode(), context, true);
                     }
                 }
             };
 
             if (untilNode.containsNonlocalFlow) {
                 context.performBooleanLoopSafe(condition, body, untilNode.evaluateAtStart());
             } else {
                 context.performBooleanLoopLight(condition, body, untilNode.evaluateAtStart());
             }
 
             context.pollThreadEvents();
             // TODO: don't require pop
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileVAlias(Node node, BodyCompiler context, boolean expr) {
         VAliasNode valiasNode = (VAliasNode) node;
 
         context.aliasGlobal(valiasNode.getNewName(), valiasNode.getOldName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileVCall(Node node, BodyCompiler context, boolean expr) {
         VCallNode vcallNode = (VCallNode) node;
         
         context.getInvocationCompiler().invokeDynamic(vcallNode.getName(), null, null, CallType.VARIABLE, null, false);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileWhile(Node node, BodyCompiler context, boolean expr) {
         final WhileNode whileNode = (WhileNode) node;
 
         if (whileNode.getConditionNode().getNodeType().alwaysFalse() &&
                 whileNode.evaluateAtStart()) {
             // do nothing
             if (expr) context.loadNil();
         } else {
             BranchCallback condition = new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(whileNode.getConditionNode(), context, true);
                 }
             };
 
             BranchCallback body = new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     if (whileNode.getBodyNode() != null) {
                         compile(whileNode.getBodyNode(), context, true);
                     }
                 }
             };
 
             if (whileNode.containsNonlocalFlow) {
                 context.performBooleanLoopSafe(condition, body, whileNode.evaluateAtStart());
             } else {
                 context.performBooleanLoopLight(condition, body, whileNode.evaluateAtStart());
             }
 
             context.pollThreadEvents();
             // TODO: don't require pop
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileXStr(Node node, BodyCompiler context, boolean expr) {
         final XStrNode xstrNode = (XStrNode) node;
 
         ArgumentsCallback argsCallback = new ArgumentsCallback() {
             public int getArity() {
                 return 1;
             }
 
             public void call(BodyCompiler context) {
                 context.createNewString(xstrNode.getValue());
             }
         };
         context.getInvocationCompiler().invokeDynamic("`", null, argsCallback, CallType.FUNCTIONAL, null, false);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileYield(Node node, BodyCompiler context, boolean expr) {
         final YieldNode yieldNode = (YieldNode) node;
 
         ArgumentsCallback argsCallback = getArgsCallback(yieldNode.getArgsNode());
 
         // TODO: This filtering is kind of gross...it would be nice to get some parser help here
         if (argsCallback == null || argsCallback.getArity() == 0) {
             context.getInvocationCompiler().yieldSpecific(argsCallback);
         } else if ((argsCallback.getArity() == 1 || argsCallback.getArity() == 2 || argsCallback.getArity() == 3) && yieldNode.getExpandArguments()) {
             // send it along as arity-specific, we don't need the array
             context.getInvocationCompiler().yieldSpecific(argsCallback);
         } else {
             CompilerCallback argsCallback2 = null;
             if (yieldNode.getArgsNode() != null) {
                 argsCallback2 = new CompilerCallback() {
                     public void call(BodyCompiler context) {
                         compile(yieldNode.getArgsNode(), context,true);
                     }
                 };
             }
 
             context.getInvocationCompiler().yield(argsCallback2, yieldNode.getExpandArguments());
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileZArray(Node node, BodyCompiler context, boolean expr) {
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             context.createEmptyArray();
         }
 
         if (popit) context.consumeCurrentValue();
     }
 
     public void compileZSuper(Node node, BodyCompiler context, boolean expr) {
         ZSuperNode zsuperNode = (ZSuperNode) node;
 
         CompilerCallback closure = getBlock(zsuperNode.getIterNode());
 
         context.callZSuper(closure);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileArgsCatArguments(Node node, BodyCompiler context, boolean expr) {
         ArgsCatNode argsCatNode = (ArgsCatNode) node;
 
         compileArguments(argsCatNode.getFirstNode(), context);
         // arguments compilers always create IRubyObject[], but we want to use RubyArray.concat here;
         // FIXME: as a result, this is NOT efficient, since it creates and then later unwraps an array
         context.createNewArray(true);
         compile(argsCatNode.getSecondNode(), context,true);
         context.splatCurrentValue();
         context.concatArrays();
         context.convertToJavaArray();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileArgsPushArguments(Node node, BodyCompiler context, boolean expr) {
         ArgsPushNode argsPushNode = (ArgsPushNode) node;
         compile(argsPushNode.getFirstNode(), context,true);
         compile(argsPushNode.getSecondNode(), context,true);
         context.appendToArray();
         context.convertToJavaArray();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileArrayArguments(Node node, BodyCompiler context, boolean expr) {
         ArrayNode arrayNode = (ArrayNode) node;
 
         ArrayCallback callback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray, int index) {
                         Node node = (Node) ((Object[]) sourceArray)[index];
                         compile(node, context,true);
                     }
                 };
 
         context.setLinePosition(arrayNode.getPosition());
         context.createObjectArray(arrayNode.childNodes().toArray(), callback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     // leave as a normal array
     }
 
     public void compileSplatArguments(Node node, BodyCompiler context, boolean expr) {
diff --git a/src/org/jruby/compiler/impl/StandardInvocationCompiler.java b/src/org/jruby/compiler/impl/StandardInvocationCompiler.java
index 0611a12915..8a4d44b329 100644
--- a/src/org/jruby/compiler/impl/StandardInvocationCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardInvocationCompiler.java
@@ -1,693 +1,696 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
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
 
 package org.jruby.compiler.impl;
 
 import org.jruby.RubyArray;
 import org.jruby.compiler.ArgumentsCallback;
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.compiler.InvocationCompiler;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import static org.jruby.util.CodegenUtils.*;
 import org.objectweb.asm.Label;
 
 /**
  *
  * @author headius
  */
 public class StandardInvocationCompiler implements InvocationCompiler {
     protected BaseBodyCompiler methodCompiler;
     protected SkinnyMethodAdapter method;
 
     public StandardInvocationCompiler(BaseBodyCompiler methodCompiler, SkinnyMethodAdapter method) {
         this.methodCompiler = methodCompiler;
         this.method = method;
     }
 
     public SkinnyMethodAdapter getMethodAdapter() {
         return this.method;
     }
 
     public void setMethodAdapter(SkinnyMethodAdapter sma) {
         this.method = sma;
     }
 
     public void invokeAttrAssignMasgn(String name, CompilerCallback receiverCallback, ArgumentsCallback argsCallback) {
-        // value is already on stack, evaluate receiver and args and call helper
+        // value is already on stack, save it for later
+        int temp = methodCompiler.getVariableCompiler().grabTempLocal();
+        methodCompiler.getVariableCompiler().setTempLocal(temp);
+        
+        // receiver first, so we know which call site to use
         receiverCallback.call(methodCompiler);
 
-        String signature;
+        // select appropriate call site
+        method.dup(); // dup receiver
+        methodCompiler.loadSelf(); // load self
+        methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, name, CallType.NORMAL);
+        methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, name, CallType.VARIABLE);
+        methodCompiler.invokeUtilityMethod("selectAttrAsgnCallSite", sig(CallSite.class, IRubyObject.class, IRubyObject.class, CallSite.class, CallSite.class));
+
+        String signature = null;
         if (argsCallback == null) {
             signature = sig(IRubyObject.class,
-                    IRubyObject.class /*value*/,
                     IRubyObject.class /*receiver*/,
+                    CallSite.class,
+                    IRubyObject.class /*value*/,
                     ThreadContext.class,
-                    IRubyObject.class, /*self*/
-                    CallSite.class);
+                    IRubyObject.class /*self*/);
         } else {
             switch (argsCallback.getArity()) {
-                case 0:
-                    signature = sig(IRubyObject.class,
-                            IRubyObject.class /*value*/,
-                            IRubyObject.class /*receiver*/,
-                            ThreadContext.class,
-                            IRubyObject.class, /*self*/
-                            CallSite.class);
-                    break;
-                case 1:
-                    argsCallback.call(methodCompiler);
-                    signature = sig(IRubyObject.class,
-                            IRubyObject.class /*value*/,
-                            IRubyObject.class /*receiver*/,
-                            IRubyObject.class /*arg0*/,
-                            ThreadContext.class,
-                            IRubyObject.class, /*self*/
-                            CallSite.class);
-                    break;
-                case 2:
-                    argsCallback.call(methodCompiler);
-                    signature = sig(IRubyObject.class,
-                            IRubyObject.class /*value*/,
-                            IRubyObject.class /*receiver*/,
-                            IRubyObject.class /*arg0*/,
-                            IRubyObject.class /*arg1*/,
-                            ThreadContext.class,
-                            IRubyObject.class, /*self*/
-                            CallSite.class);
-                    break;
-                case 3:
-                    argsCallback.call(methodCompiler);
-                    signature = sig(IRubyObject.class,
-                            IRubyObject.class /*value*/,
-                            IRubyObject.class /*receiver*/,
-                            IRubyObject.class /*arg0*/,
-                            IRubyObject.class /*arg1*/,
-                            IRubyObject.class /*arg2*/,
-                            ThreadContext.class,
-                            IRubyObject.class, /*self*/
-                            CallSite.class);
-                    break;
-                default:
-                    argsCallback.call(methodCompiler);
-                    signature = sig(IRubyObject.class,
-                            IRubyObject.class /*value*/,
-                            IRubyObject.class /*receiver*/,
-                            IRubyObject[].class /*args*/,
-                            ThreadContext.class,
-                            IRubyObject.class, /*self*/
-                            CallSite.class);
-                    break;
+            case 1:
+                argsCallback.call(methodCompiler);
+                signature = sig(IRubyObject.class,
+                        IRubyObject.class, /*receiver*/
+                        CallSite.class,
+                        IRubyObject.class, /*arg0*/
+                        IRubyObject.class, /*value*/
+                        ThreadContext.class,
+                        IRubyObject.class /*self*/);
+                break;
+            case 2:
+                argsCallback.call(methodCompiler);
+                signature = sig(IRubyObject.class,
+                        IRubyObject.class, /*receiver*/
+                        CallSite.class,
+                        IRubyObject.class, /*arg0*/
+                        IRubyObject.class, /*arg1*/
+                        IRubyObject.class, /*value*/
+                        ThreadContext.class,
+                        IRubyObject.class /*self*/);
+                break;
+            case 3:
+                argsCallback.call(methodCompiler);
+                signature = sig(IRubyObject.class,
+                        IRubyObject.class, /*receiver*/
+                        CallSite.class,
+                        IRubyObject.class, /*arg0*/
+                        IRubyObject.class, /*arg1*/
+                        IRubyObject.class, /*arg2*/
+                        IRubyObject.class, /*value*/
+                        ThreadContext.class,
+                        IRubyObject.class /*self*/);
+                break;
+            default:
+                argsCallback.call(methodCompiler);
+                signature = sig(IRubyObject.class,
+                        IRubyObject.class, /*receiver*/
+                        CallSite.class,
+                        IRubyObject[].class, /*args*/
+                        IRubyObject.class, /*value*/
+                        ThreadContext.class,
+                        IRubyObject.class /*self*/);
             }
         }
-        
+
+        methodCompiler.getVariableCompiler().getTempLocal(temp);
+        methodCompiler.getVariableCompiler().releaseTempLocal();
         methodCompiler.loadThreadContext();
         methodCompiler.loadSelf();
-        methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, name, CallType.NORMAL);
 
         methodCompiler.invokeUtilityMethod("doAttrAsgn", signature);
     }
 
     public void invokeAttrAssign(String name, CompilerCallback receiverCallback, ArgumentsCallback argsCallback) {
         // receiver first, so we know which call site to use
         receiverCallback.call(methodCompiler);
         
         // select appropriate call site
         method.dup(); // dup receiver
         methodCompiler.loadSelf(); // load self
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, name, CallType.NORMAL);
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, name, CallType.VARIABLE);
         methodCompiler.invokeUtilityMethod("selectAttrAsgnCallSite", sig(CallSite.class, IRubyObject.class, IRubyObject.class, CallSite.class, CallSite.class));
         
         String signature = null;
         switch (argsCallback.getArity()) {
         case 1:
             signature = sig(IRubyObject.class,
                     IRubyObject.class, /*receiver*/
                     CallSite.class,
                     IRubyObject.class, /*value*/
                     ThreadContext.class,
                     IRubyObject.class /*self*/);
             break;
         case 2:
             signature = sig(IRubyObject.class,
                     IRubyObject.class, /*receiver*/
                     CallSite.class,
                     IRubyObject.class, /*arg0*/
                     IRubyObject.class, /*value*/
                     ThreadContext.class,
                     IRubyObject.class /*self*/);
             break;
         case 3:
             signature = sig(IRubyObject.class,
                     IRubyObject.class, /*receiver*/
                     CallSite.class,
                     IRubyObject.class, /*arg0*/
                     IRubyObject.class, /*arg1*/
                     IRubyObject.class, /*value*/
                     ThreadContext.class,
                     IRubyObject.class /*self*/);
             break;
         default:
             signature = sig(IRubyObject.class,
                     IRubyObject.class, /*receiver*/
                     CallSite.class,
                     IRubyObject[].class, /*args*/
                     ThreadContext.class,
                     IRubyObject.class /*self*/);
         }
         
         argsCallback.call(methodCompiler);
         methodCompiler.loadThreadContext();
         methodCompiler.loadSelf();
         
         methodCompiler.invokeUtilityMethod("doAttrAsgn", signature);
     }
     
     public void opElementAsgnWithOr(CompilerCallback receiver, ArgumentsCallback args, CompilerCallback valueCallback) {
         // get call site and thread context
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, "[]", CallType.FUNCTIONAL);
         methodCompiler.loadThreadContext();
         methodCompiler.loadSelf();
         
         // evaluate and save receiver and args
         receiver.call(methodCompiler);
         args.call(methodCompiler);
         method.dup2();
         int argsLocal = methodCompiler.getVariableCompiler().grabTempLocal();
         methodCompiler.getVariableCompiler().setTempLocal(argsLocal);
         int receiverLocal = methodCompiler.getVariableCompiler().grabTempLocal();
         methodCompiler.getVariableCompiler().setTempLocal(receiverLocal);
         
         // invoke
         switch (args.getArity()) {
         case 1:
             method.invokevirtual(p(CallSite.class), "call", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class));
             break;
         default:
             method.invokevirtual(p(CallSite.class), "call", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject[].class));
         }
         
         // check if it's true, ending if so
         method.dup();
         methodCompiler.invokeIRubyObject("isTrue", sig(boolean.class));
         Label done = new Label();
         method.ifne(done);
         
         // not true, eval value and assign
         method.pop();
         // thread context, receiver and original args
         methodCompiler.loadThreadContext();
         methodCompiler.loadSelf();
         methodCompiler.getVariableCompiler().getTempLocal(receiverLocal);
         methodCompiler.getVariableCompiler().getTempLocal(argsLocal);
         
         // eval value for assignment
         valueCallback.call(methodCompiler);
         
         // call site
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, "[]=", CallType.FUNCTIONAL);
         
         // depending on size of original args, call appropriate utility method
         switch (args.getArity()) {
         case 0:
             throw new NotCompilableException("Op Element Asgn with zero-arity args");
         case 1:
             methodCompiler.invokeUtilityMethod("opElementAsgnWithOrPartTwoOneArg", 
                     sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, CallSite.class));
             break;
         case 2:
             methodCompiler.invokeUtilityMethod("opElementAsgnWithOrPartTwoTwoArgs", 
                     sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject[].class, IRubyObject.class, CallSite.class));
             break;
         case 3:
             methodCompiler.invokeUtilityMethod("opElementAsgnWithOrPartTwoThreeArgs", 
                     sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject[].class, IRubyObject.class, CallSite.class));
             break;
         default:
             methodCompiler.invokeUtilityMethod("opElementAsgnWithOrPartTwoNArgs", 
                     sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject[].class, IRubyObject.class, CallSite.class));
             break;
         }
         
         method.label(done);
         
         methodCompiler.getVariableCompiler().releaseTempLocal();
         methodCompiler.getVariableCompiler().releaseTempLocal();
     }
     
     public void opElementAsgnWithAnd(CompilerCallback receiver, ArgumentsCallback args, CompilerCallback valueCallback) {
         // get call site and thread context
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, "[]", CallType.FUNCTIONAL);
         methodCompiler.loadThreadContext();
         methodCompiler.loadSelf();
         
         // evaluate and save receiver and args
         receiver.call(methodCompiler);
         args.call(methodCompiler);
         method.dup2();
         int argsLocal = methodCompiler.getVariableCompiler().grabTempLocal();
         methodCompiler.getVariableCompiler().setTempLocal(argsLocal);
         int receiverLocal = methodCompiler.getVariableCompiler().grabTempLocal();
         methodCompiler.getVariableCompiler().setTempLocal(receiverLocal);
         
         // invoke
         switch (args.getArity()) {
         case 1:
             method.invokevirtual(p(CallSite.class), "call", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class));
             break;
         default:
             method.invokevirtual(p(CallSite.class), "call", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject[].class));
         }
         
         // check if it's true, ending if not
         method.dup();
         methodCompiler.invokeIRubyObject("isTrue", sig(boolean.class));
         Label done = new Label();
         method.ifeq(done);
         
         // not true, eval value and assign
         method.pop();
         // thread context, receiver and original args
         methodCompiler.loadThreadContext();
         methodCompiler.loadSelf();
         methodCompiler.getVariableCompiler().getTempLocal(receiverLocal);
         methodCompiler.getVariableCompiler().getTempLocal(argsLocal);
         
         // eval value and save it
         valueCallback.call(methodCompiler);
         
         // call site
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, "[]=", CallType.FUNCTIONAL);
         
         // depending on size of original args, call appropriate utility method
         switch (args.getArity()) {
         case 0:
             throw new NotCompilableException("Op Element Asgn with zero-arity args");
         case 1:
             methodCompiler.invokeUtilityMethod("opElementAsgnWithOrPartTwoOneArg", 
                     sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, CallSite.class));
             break;
         case 2:
             methodCompiler.invokeUtilityMethod("opElementAsgnWithOrPartTwoTwoArgs", 
                     sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject[].class, IRubyObject.class, CallSite.class));
             break;
         case 3:
             methodCompiler.invokeUtilityMethod("opElementAsgnWithOrPartTwoThreeArgs", 
                     sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject[].class, IRubyObject.class, CallSite.class));
             break;
         default:
             methodCompiler.invokeUtilityMethod("opElementAsgnWithOrPartTwoNArgs", 
                     sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject[].class, IRubyObject.class, CallSite.class));
             break;
         }
         
         method.label(done);
         
         methodCompiler.getVariableCompiler().releaseTempLocal();
         methodCompiler.getVariableCompiler().releaseTempLocal();
     }
     
     public void opElementAsgnWithMethod(CompilerCallback receiver, ArgumentsCallback args, CompilerCallback valueCallback, String operator) {
         methodCompiler.loadThreadContext();
         methodCompiler.loadSelf();
         receiver.call(methodCompiler);
         args.call(methodCompiler);
         valueCallback.call(methodCompiler); // receiver, args, result, value
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, "[]", CallType.FUNCTIONAL);
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, operator, CallType.NORMAL);
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, "[]=", CallType.FUNCTIONAL);
         
         switch (args.getArity()) {
         case 0:
             methodCompiler.invokeUtilityMethod("opElementAsgnWithMethod",
                     sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, CallSite.class, CallSite.class, CallSite.class));
             break;
         case 1:
             methodCompiler.invokeUtilityMethod("opElementAsgnWithMethod",
                     sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, CallSite.class, CallSite.class, CallSite.class));
             break;
         case 2:
             methodCompiler.invokeUtilityMethod("opElementAsgnWithMethod",
                     sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, CallSite.class, CallSite.class, CallSite.class));
             break;
         case 3:
             methodCompiler.invokeUtilityMethod("opElementAsgnWithMethod",
                     sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, CallSite.class, CallSite.class, CallSite.class));
             break;
         default:
             methodCompiler.invokeUtilityMethod("opElementAsgnWithMethod",
                     sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject[].class, IRubyObject.class, CallSite.class, CallSite.class, CallSite.class));
             break;
         }
     }
 
     public void invokeSuper(CompilerCallback argsCallback, CompilerCallback closureArg) {
         methodCompiler.loadThreadContext();
         methodCompiler.invokeUtilityMethod("checkSuperDisabledOrOutOfMethod", sig(void.class, ThreadContext.class));
         
         methodCompiler.loadSelf();
 
         methodCompiler.loadThreadContext(); // [self, tc]
         
         // args
         if (argsCallback == null) {
             method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
             // block
             if (closureArg == null) {
                 // no args, no block
                 methodCompiler.loadBlock();
             } else {
                 // no args, with block
                 closureArg.call(methodCompiler);
             }
         } else {
             argsCallback.call(methodCompiler);
             // block
             if (closureArg == null) {
                 // with args, no block
                 methodCompiler.loadBlock();
             } else {
                 // with args, with block
                 closureArg.call(methodCompiler);
             }
         }
         
         method.invokeinterface(p(IRubyObject.class), "callSuper", sig(IRubyObject.class, ThreadContext.class, IRubyObject[].class, Block.class));
     }
 
     public void invokeDynamic(String name, CompilerCallback receiverCallback, ArgumentsCallback argsCallback, CallType callType, CompilerCallback closureArg, boolean iterator) {
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, name, callType);
 
         methodCompiler.loadThreadContext(); // [adapter, tc]
 
         // for visibility checking without requiring frame self
         // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
         methodCompiler.loadSelf();
         
         if (receiverCallback != null) {
             receiverCallback.call(methodCompiler);
         } else {
             methodCompiler.loadSelf();
         }
         
         String signature;
         String callSiteMethod = "call";
         // args
         if (argsCallback == null) {
             // block
             if (closureArg == null) {
                 // no args, no block
                 signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class));
             } else {
                 // no args, with block
                 if (iterator) callSiteMethod = "callIter";
                 closureArg.call(methodCompiler);
                 signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, Block.class));
             }
         } else {
             argsCallback.call(methodCompiler);
             // block
             if (closureArg == null) {
                 // with args, no block
                 switch (argsCallback.getArity()) {
                 case 1:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class));
                     break;
                 case 2:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class));
                     break;
                 case 3:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class));
                     break;
                 default:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject[].class));
                 }
             } else {
                 // with args, with block
                 if (iterator) callSiteMethod = "callIter";
                 closureArg.call(methodCompiler);
                 
                 switch (argsCallback.getArity()) {
                 case 1:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class));
                     break;
                 case 2:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class));
                     break;
                 case 3:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class));
                     break;
                 default:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject[].class, Block.class));
                 }
             }
         }
         
         // adapter, tc, recv, args{0,1}, block{0,1}]
 
         method.invokevirtual(p(CallSite.class), callSiteMethod, signature);
     }
 
     public void invokeOpAsgnWithOr(String attrName, String attrAsgnName, CompilerCallback receiverCallback, ArgumentsCallback argsCallback) {
         receiverCallback.call(methodCompiler);
         method.dup();
         methodCompiler.loadThreadContext();
         methodCompiler.loadSelf();
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, attrName, CallType.FUNCTIONAL);
         
         methodCompiler.invokeUtilityMethod("preOpAsgnWithOrAnd", sig(IRubyObject.class, IRubyObject.class, ThreadContext.class, IRubyObject.class, CallSite.class));
         
         Label done = new Label();
         Label isTrue = new Label();
         
         method.dup();
         methodCompiler.invokeIRubyObject("isTrue", sig(boolean.class));
         method.ifne(isTrue);
         
         method.pop(); // pop extra attr value
         argsCallback.call(methodCompiler);
         methodCompiler.loadThreadContext();
         methodCompiler.loadSelf();
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, attrAsgnName, CallType.NORMAL);
         
         methodCompiler.invokeUtilityMethod("postOpAsgnWithOrAnd",
                 sig(IRubyObject.class, IRubyObject.class, IRubyObject.class, ThreadContext.class, IRubyObject.class, CallSite.class));
         method.go_to(done);
         
         method.label(isTrue);
         method.swap();
         method.pop();
         
         method.label(done);
     }
 
     public void invokeOpAsgnWithAnd(String attrName, String attrAsgnName, CompilerCallback receiverCallback, ArgumentsCallback argsCallback) {
         receiverCallback.call(methodCompiler);
         method.dup();
         methodCompiler.loadThreadContext();
         methodCompiler.loadSelf();
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, attrName, CallType.FUNCTIONAL);
         
         methodCompiler.invokeUtilityMethod("preOpAsgnWithOrAnd", sig(IRubyObject.class, IRubyObject.class, ThreadContext.class, IRubyObject.class, CallSite.class));
         
         Label done = new Label();
         Label isFalse = new Label();
         
         method.dup();
         methodCompiler.invokeIRubyObject("isTrue", sig(boolean.class));
         method.ifeq(isFalse);
         
         method.pop(); // pop extra attr value
         argsCallback.call(methodCompiler);
         methodCompiler.loadThreadContext();
         methodCompiler.loadSelf();
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, attrAsgnName, CallType.NORMAL);
         
         methodCompiler.invokeUtilityMethod("postOpAsgnWithOrAnd",
                 sig(IRubyObject.class, IRubyObject.class, IRubyObject.class, ThreadContext.class, IRubyObject.class, CallSite.class));
         method.go_to(done);
         
         method.label(isFalse);
         method.swap();
         method.pop();
         
         method.label(done);
     }
 
     public void invokeOpAsgnWithMethod(String operatorName, String attrName, String attrAsgnName, CompilerCallback receiverCallback, ArgumentsCallback argsCallback) {
         methodCompiler.loadThreadContext();
         methodCompiler.loadSelf();
         receiverCallback.call(methodCompiler);
         argsCallback.call(methodCompiler);
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, attrName, CallType.FUNCTIONAL);
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, operatorName, CallType.FUNCTIONAL);
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, attrAsgnName, CallType.NORMAL);
         
         methodCompiler.invokeUtilityMethod("opAsgnWithMethod",
                 sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, CallSite.class, CallSite.class, CallSite.class));
     }
 
     public void invokeOpElementAsgnWithMethod(String operatorName, CompilerCallback receiverCallback, ArgumentsCallback argsCallback) {
         methodCompiler.loadThreadContext(); // [adapter, tc]
         methodCompiler.loadSelf();
         receiverCallback.call(methodCompiler);
         argsCallback.call(methodCompiler);
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, "[]", CallType.FUNCTIONAL);
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, operatorName, CallType.FUNCTIONAL);
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, "[]=", CallType.NORMAL);
         
         methodCompiler.invokeUtilityMethod("opElementAsgnWithMethod",
                 sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, CallSite.class, CallSite.class, CallSite.class));
     }
 
     public void yield(CompilerCallback argsCallback, boolean unwrap) {
         methodCompiler.loadBlock();
         methodCompiler.loadThreadContext();
 
         String signature;
         if (argsCallback != null) {
             argsCallback.call(methodCompiler);
             signature = sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, boolean.class);
         } else {
             signature = sig(IRubyObject.class, ThreadContext.class, boolean.class);
         }
         method.ldc(unwrap);
 
         method.invokevirtual(p(Block.class), "yield", signature);
     }
 
     public void yieldSpecific(ArgumentsCallback argsCallback) {
         methodCompiler.loadBlock();
         methodCompiler.loadThreadContext();
 
         String signature;
         if (argsCallback == null) {
             signature = sig(IRubyObject.class, ThreadContext.class);
         } else {
             argsCallback.call(methodCompiler);
             switch (argsCallback.getArity()) {
             case 1:
                 signature = sig(IRubyObject.class, ThreadContext.class, IRubyObject.class);
                 break;
             case 2:
                 signature = sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class);
                 break;
             case 3:
                 signature = sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class);
                 break;
             default:
                 throw new NotCompilableException("Can't do specific-arity call for > 3 args yet");
             }
         }
 
         method.invokevirtual(p(Block.class), "yieldSpecific", signature);
     }
 
     public void invokeEqq(ArgumentsCallback receivers, CompilerCallback argument) {
         if (argument == null) {
             receivers.call(methodCompiler);
 
             switch (receivers.getArity()) {
             case 1:
                 methodCompiler.invokeUtilityMethod("invokeEqqForCaselessWhen", sig(boolean.class,
                         IRubyObject.class /*receiver*/
                         ));
                 break;
             case 2:
                 methodCompiler.invokeUtilityMethod("invokeEqqForCaselessWhen", sig(boolean.class,
                         IRubyObject.class, /*receiver*/
                         IRubyObject.class
                         ));
                 break;
             case 3:
                 methodCompiler.invokeUtilityMethod("invokeEqqForCaselessWhen", sig(boolean.class,
                         IRubyObject.class, /*receiver*/
                         IRubyObject.class,
                         IRubyObject.class
                         ));
                 break;
             default:
                 methodCompiler.invokeUtilityMethod("invokeEqqForCaselessWhen", sig(boolean.class,
                         IRubyObject[].class /*receiver*/
                         ));
             }
         } else {
             // arg and receiver already present on the stack
             methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(methodCompiler, "===", CallType.NORMAL);
             methodCompiler.loadThreadContext();
             methodCompiler.loadSelf();
             argument.call(methodCompiler);
             receivers.call(methodCompiler);
 
             switch (receivers.getArity()) {
             case 1:
                 methodCompiler.invokeUtilityMethod("invokeEqqForCaseWhen", sig(boolean.class,
                         CallSite.class,
                         ThreadContext.class,
                         IRubyObject.class /*self*/,
                         IRubyObject.class, /*arg*/
                         IRubyObject.class /*receiver*/
                         ));
                 break;
             case 2:
                 methodCompiler.invokeUtilityMethod("invokeEqqForCaseWhen", sig(boolean.class,
                         CallSite.class,
                         ThreadContext.class,
                         IRubyObject.class /*self*/,
                         IRubyObject.class, /*arg*/
                         IRubyObject.class, /*receiver*/
                         IRubyObject.class
                         ));
                 break;
             case 3:
                 methodCompiler.invokeUtilityMethod("invokeEqqForCaseWhen", sig(boolean.class,
                         CallSite.class,
                         ThreadContext.class,
                         IRubyObject.class /*self*/,
                         IRubyObject.class, /*arg*/
                         IRubyObject.class, /*receiver*/
                         IRubyObject.class,
                         IRubyObject.class
                         ));
                 break;
             default:
                 methodCompiler.invokeUtilityMethod("invokeEqqForCaseWhen", sig(boolean.class,
                         CallSite.class,
                         ThreadContext.class,
                         IRubyObject.class /*self*/,
                         IRubyObject.class, /*arg*/
                         IRubyObject[].class /*receiver*/
                         ));
             }
         }
     }
 }
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index 31ecb12bc9..12fb0df416 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -1,1104 +1,1095 @@
 package org.jruby.javasupport.util;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyHash;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyKernel;
 import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.javasupport.JavaClass;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CompiledBlock;
 import org.jruby.runtime.CompiledBlock19;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledBlockCallback19;
 import org.jruby.runtime.CompiledBlockLight;
 import org.jruby.runtime.CompiledBlockLight19;
 import org.jruby.runtime.CompiledSharedScopeBlock;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.Interpreted19Block;
 import org.jruby.runtime.InterpretedBlock;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.TypeConverter;
 
 /**
  * Helper methods which are called by the compiler.  Note: These will show no consumers, but
  * generated code does call these so don't remove them thinking they are dead code. 
  *
  */
 public class RuntimeHelpers {
     public static CallSite selectAttrAsgnCallSite(IRubyObject receiver, IRubyObject self, CallSite normalSite, CallSite variableSite) {
         if (receiver == self) return variableSite;
         return normalSite;
     }
-    public static IRubyObject doAttrAsgn(IRubyObject value, IRubyObject receiver, ThreadContext context, IRubyObject caller, CallSite callSite) {
-        callSite.call(context, caller, receiver, value);
-        return value;
-    }
-    public static IRubyObject doAttrAsgn(IRubyObject value, IRubyObject receiver, IRubyObject arg0, ThreadContext context, IRubyObject caller, CallSite callSite) {
-        callSite.call(context, caller, receiver, arg0, value);
-        return value;
-    }
-    public static IRubyObject doAttrAsgn(IRubyObject value, IRubyObject receiver, IRubyObject arg0, IRubyObject arg1, ThreadContext context, IRubyObject caller, CallSite callSite) {
-        callSite.call(context, caller, receiver, arg0, arg1, value);
-        return value;
-    }
-    public static IRubyObject doAttrAsgn(IRubyObject value, IRubyObject receiver, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, ThreadContext context, IRubyObject caller, CallSite callSite) {
-        callSite.call(context, caller, receiver, arg0, arg1, arg2, value);
-        return value;
-    }
-    public static IRubyObject doAttrAsgn(IRubyObject value, IRubyObject receiver, IRubyObject[] args, ThreadContext context, IRubyObject caller, CallSite callSite) {
-        callSite.call(context, caller, receiver, appendToObjectArray(args, value));
-        return value;
-    }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject arg0, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, arg0, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject arg0, IRubyObject arg1, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, arg0, arg1, value);
         return value;
     }
+    public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject value, ThreadContext context, IRubyObject caller) {
+        callSite.call(context, caller, receiver, arg0, arg1, arg2, value);
+        return value;
+    }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject[] args, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, args);
         return args[args.length - 1];
     }
+    public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject[] args, IRubyObject value, ThreadContext context, IRubyObject caller) {
+        IRubyObject[] newArgs = new IRubyObject[args.length + 1];
+        System.arraycopy(args, 0, newArgs, 0, args.length);
+        newArgs[args.length] = value;
+        callSite.call(context, caller, receiver, newArgs);
+        return value;
+    }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject[] receivers) {
         for (int i = 0; i < receivers.length; i++) {
             IRubyObject receiver = receivers[i];
             if (invokeEqqForCaseWhen(callSite, context, caller, arg, receiver)) return true;
         }
         return false;
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject receiver) {
         IRubyObject result = callSite.call(context, caller, receiver, arg);
         if (result.isTrue()) return true;
         return false;
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject receiver0, IRubyObject receiver1) {
         IRubyObject result = callSite.call(context, caller, receiver0, arg);
         if (result.isTrue()) return true;
         return invokeEqqForCaseWhen(callSite, context, caller, arg, receiver1);
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject receiver0, IRubyObject receiver1, IRubyObject receiver2) {
         IRubyObject result = callSite.call(context, caller, receiver0, arg);
         if (result.isTrue()) return true;
         return invokeEqqForCaseWhen(callSite, context, caller, arg, receiver1, receiver2);
     }
 
     public static boolean areAnyTrueForCaselessWhen(IRubyObject[] receivers) {
         for (int i = 0; i < receivers.length; i++) {
             if (receivers[i].isTrue()) return true;
         }
         return false;
     }
 
     public static boolean invokeEqqForCaselessWhen(IRubyObject receiver) {
         return receiver.isTrue();
     }
 
     public static boolean invokeEqqForCaselessWhen(IRubyObject receiver0, IRubyObject receiver1) {
         return receiver0.isTrue() || receiver1.isTrue();
     }
 
     public static boolean invokeEqqForCaselessWhen(IRubyObject receiver0, IRubyObject receiver1, IRubyObject receiver2) {
         return receiver0.isTrue() || receiver1.isTrue() || receiver2.isTrue();
     }
     
     public static CompiledBlockCallback createBlockCallback(Ruby runtime, Object scriptObject, String closureMethod) {
         Class scriptClass = scriptObject.getClass();
         ClassLoader scriptClassLoader = scriptClass.getClassLoader();
         CallbackFactory factory = CallbackFactory.createFactory(runtime, scriptClass, scriptClassLoader);
         
         return factory.getBlockCallback(closureMethod, scriptObject);
     }
 
     public static CompiledBlockCallback19 createBlockCallback19(Ruby runtime, Object scriptObject, String closureMethod) {
         Class scriptClass = scriptObject.getClass();
         ClassLoader scriptClassLoader = scriptClass.getClassLoader();
         CallbackFactory factory = CallbackFactory.createFactory(runtime, scriptClass, scriptClassLoader);
 
         return factory.getBlockCallback19(closureMethod, scriptObject);
     }
     
     public static BlockBody createCompiledBlockBody(ThreadContext context, Object scriptObject, String closureMethod, int arity, 
             String[] staticScopeNames, boolean hasMultipleArgsHead, int argsNodeType, boolean light) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         if (light) {
             return CompiledBlockLight.newCompiledBlockLight(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback(context.getRuntime(), scriptObject, closureMethod),
                     hasMultipleArgsHead, argsNodeType);
         } else {
             return CompiledBlock.newCompiledBlock(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback(context.getRuntime(), scriptObject, closureMethod),
                     hasMultipleArgsHead, argsNodeType);
         }
     }
 
     public static BlockBody createCompiledBlockBody19(ThreadContext context, Object scriptObject, String closureMethod, int arity,
             String[] staticScopeNames, boolean hasMultipleArgsHead, int argsNodeType, boolean light) {
         StaticScope staticScope =
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
 
         if (light) {
             return CompiledBlockLight19.newCompiledBlockLight(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback19(context.getRuntime(), scriptObject, closureMethod),
                     hasMultipleArgsHead, argsNodeType);
         } else {
             return CompiledBlock19.newCompiledBlock(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback19(context.getRuntime(), scriptObject, closureMethod),
                     hasMultipleArgsHead, argsNodeType);
         }
     }
     
     public static Block createBlock(ThreadContext context, IRubyObject self, BlockBody body) {
         return CompiledBlock.newCompiledClosure(
                 context,
                 self,
                 body);
     }
 
     public static Block createBlock19(ThreadContext context, IRubyObject self, BlockBody body) {
         return CompiledBlock19.newCompiledClosure(
                 context,
                 self,
                 body);
     }
     
     public static IRubyObject runBeginBlock(ThreadContext context, IRubyObject self, String[] staticScopeNames, CompiledBlockCallback callback) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         context.preScopedBody(DynamicScope.newDynamicScope(staticScope, context.getCurrentScope()));
         
         Block block = CompiledBlock.newCompiledClosure(context, self, Arity.createArity(0), staticScope, callback, false, BlockBody.ZERO_ARGS);
         
         try {
             block.yield(context, null);
         } finally {
             context.postScopedBody();
         }
         
         return context.getRuntime().getNil();
     }
     
     public static Block createSharedScopeBlock(ThreadContext context, IRubyObject self, int arity, 
             CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argsNodeType) {
         
         return CompiledSharedScopeBlock.newCompiledSharedScopeClosure(context, self, Arity.createArity(arity), 
                 context.getCurrentScope(), callback, hasMultipleArgsHead, argsNodeType);
     }
     
     public static IRubyObject def(ThreadContext context, IRubyObject self, Object scriptObject, String name, String javaName, String[] scopeNames,
             int arity, int required, int optional, int rest, CallConfiguration callConfig) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
         
         RubyModule containingClass = context.getRubyClass();
         Visibility visibility = context.getCurrentVisibility();
         
         performNormalMethodChecks(containingClass, runtime, name);
         
         StaticScope scope = creatScopeForClass(context, scopeNames, required, optional, rest);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method = constructNormalMethod(name, visibility, factory, containingClass, javaName, arity, scope, scriptObject, callConfig);
         
         addInstanceMethod(containingClass, name, method, visibility,context, runtime);
         
         return runtime.getNil();
     }
     
     public static IRubyObject defs(ThreadContext context, IRubyObject self, IRubyObject receiver, Object scriptObject, String name, String javaName, String[] scopeNames,
             int arity, int required, int optional, int rest, CallConfiguration callConfig) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
 
         RubyClass rubyClass = performSingletonMethodChecks(runtime, receiver, name);
         
         StaticScope scope = creatScopeForClass(context, scopeNames, required, optional, rest);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method = constructSingletonMethod(factory, rubyClass, javaName, arity, scope,scriptObject, callConfig);
         
         rubyClass.addMethod(name, method);
         
         callSingletonMethodHook(receiver,context, runtime.fastNewSymbol(name));
         
         return runtime.getNil();
     }
     
     public static RubyClass getSingletonClass(Ruby runtime, IRubyObject receiver) {
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError("no virtual class for " + receiver.getMetaClass().getBaseName());
         } else {
             if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                 throw runtime.newSecurityError("Insecure: can't extend object.");
             }
 
             return receiver.getSingletonClass();
         }
     }
 
     // TODO: Only used by interface implementation; eliminate it
     public static IRubyObject invokeMethodMissing(IRubyObject receiver, String name, IRubyObject[] args) {
         ThreadContext context = receiver.getRuntime().getCurrentContext();
 
         // store call information so method_missing impl can use it
         context.setLastCallStatusAndVisibility(CallType.FUNCTIONAL, Visibility.PUBLIC);
 
         if (name == "method_missing") {
             return RubyKernel.method_missing(context, receiver, args, Block.NULL_BLOCK);
         }
 
         IRubyObject[] newArgs = prepareMethodMissingArgs(args, context, name);
 
         return invoke(context, receiver, "method_missing", newArgs, Block.NULL_BLOCK);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 IRubyObject[] args, CallType callType, Block block) {
         context.setLastCallStatusAndVisibility(callType, method.getVisibility());
         return callMethodMissingInternal(context, receiver, name, args, block);
     }
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 IRubyObject arg, CallType callType, Block block) {
         context.setLastCallStatusAndVisibility(callType, method.getVisibility());
         return callMethodMissingInternal(context, receiver, name, arg, block);
     }
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 IRubyObject arg0, IRubyObject arg1, CallType callType, Block block) {
         context.setLastCallStatusAndVisibility(callType, method.getVisibility());
         return callMethodMissingInternal(context, receiver, name, arg0, arg1, block);
     }
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, CallType callType, Block block) {
         context.setLastCallStatusAndVisibility(callType, method.getVisibility());
         return callMethodMissingInternal(context, receiver, name, arg0, arg1, arg2, block);
     }
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 CallType callType, Block block) {
         context.setLastCallStatusAndVisibility(callType, method.getVisibility());
         return callMethodMissingInternal(context, receiver, name, block);
     }
     
     private static IRubyObject callMethodMissingInternal(ThreadContext context, IRubyObject receiver, String name, 
                                                 IRubyObject[] args, Block block) {
         if (name.equals("method_missing")) return RubyKernel.method_missing(context, receiver, args, block);
         IRubyObject[] newArgs = prepareMethodMissingArgs(args, context, name);
         return invoke(context, receiver, "method_missing", newArgs, block);
     }
     private static IRubyObject callMethodMissingInternal(ThreadContext context, IRubyObject receiver, String name, 
                                                 Block block) {
         if (name.equals("method_missing")) return RubyKernel.method_missing(context, receiver, IRubyObject.NULL_ARRAY, block);
         return invoke(context, receiver, "method_missing", context.getRuntime().newSymbol(name), block);
     }
     private static IRubyObject callMethodMissingInternal(ThreadContext context, IRubyObject receiver, String name, 
                                                 IRubyObject arg0, Block block) {
         if (name.equals("method_missing")) return RubyKernel.method_missing(context, receiver, constructObjectArray(arg0), block);
         return invoke(context, receiver, "method_missing", context.getRuntime().newSymbol(name), arg0, block);
     }
     private static IRubyObject callMethodMissingInternal(ThreadContext context, IRubyObject receiver, String name, 
                                                 IRubyObject arg0, IRubyObject arg1, Block block) {
         if (name.equals("method_missing")) return RubyKernel.method_missing(context, receiver, constructObjectArray(arg0,arg1), block);
         return invoke(context, receiver, "method_missing", context.getRuntime().newSymbol(name), arg0, arg1, block);
     }
     private static IRubyObject callMethodMissingInternal(ThreadContext context, IRubyObject receiver, String name, 
                                                 IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         if (name.equals("method_missing")) return RubyKernel.method_missing(context, receiver, constructObjectArray(arg0,arg1,arg2), block);
         return invoke(context, receiver, "method_missing", constructObjectArray(context.getRuntime().newSymbol(name), arg0, arg1, arg2), block);
     }
 
     private static IRubyObject[] prepareMethodMissingArgs(IRubyObject[] args, ThreadContext context, String name) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 1, args.length);
         newArgs[0] = context.getRuntime().newSymbol(name);
 
         return newArgs;
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, Block block) {
         return self.getMetaClass().finvoke(context, self, name, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, Block block) {
         return self.getMetaClass().finvoke(context, self, name, arg0, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1, arg2, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args, Block block) {
         return self.getMetaClass().finvoke(context, self, name, args, block);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name) {
         return self.getMetaClass().finvoke(context, self, name);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0) {
         return self.getMetaClass().finvoke(context, self, name, arg0);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1, arg2);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args) {
         return self.getMetaClass().finvoke(context, self, name, args);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, CallType callType) {
         return RuntimeHelpers.invoke(context, self, name, IRubyObject.NULL_ARRAY, callType, Block.NULL_BLOCK);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args, CallType callType, Block block) {
         return self.getMetaClass().invoke(context, self, name, args, callType, block);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg, CallType callType, Block block) {
         return self.getMetaClass().invoke(context, self, name, arg, callType, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject[] args, Block block) {
         return asClass.finvoke(context, self, name, args, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, Block block) {
         return asClass.finvoke(context, self, name, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject arg0, Block block) {
         return asClass.finvoke(context, self, name, arg0, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         return asClass.finvoke(context, self, name, arg0, arg1, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return asClass.finvoke(context, self, name, arg0, arg1, arg2, block);
     }
 
     /**
      * The protocol for super method invocation is a bit complicated
      * in Ruby. In real terms it involves first finding the real
      * implementation class (the super class), getting the name of the
      * method to call from the frame, and then invoke that on the
      * super class with the current self as the actual object
      * invoking.
      */
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
         
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method, name, args, CallType.SUPER, block);
         }
         return method.call(context, self, superClass, name, args, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method, name, CallType.SUPER, block);
         }
         return method.call(context, self, superClass, name, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method, name, arg0, CallType.SUPER, block);
         }
         return method.call(context, self, superClass, name, arg0, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method, name, arg0, arg1, CallType.SUPER, block);
         }
         return method.call(context, self, superClass, name, arg0, arg1, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method, name, arg0, arg1, arg2, CallType.SUPER, block);
         }
         return method.call(context, self, superClass, name, arg0, arg1, arg2, block);
     }
 
     public static RubyArray ensureRubyArray(IRubyObject value) {
         return ensureRubyArray(value.getRuntime(), value);
     }
 
     public static RubyArray ensureRubyArray(Ruby runtime, IRubyObject value) {
         return value instanceof RubyArray ? (RubyArray)value : RubyArray.newArray(runtime, value);
     }
 
     public static RubyArray ensureMultipleAssignableRubyArray(IRubyObject value, Ruby runtime, boolean masgnHasHead) {
         if (!(value instanceof RubyArray)) {
             value = ArgsUtil.convertToRubyArray(runtime, value, masgnHasHead);
         }
         return (RubyArray) value;
     }
     
     public static IRubyObject fetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.getClassVar(name);
     }
     
     public static IRubyObject fastFetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String internedName) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.fastGetClassVar(internedName);
     }
     
     public static IRubyObject getConstant(ThreadContext context, String internedName) {
         Ruby runtime = context.getRuntime();
 
         return context.getCurrentScope().getStaticScope().getConstantWithConstMissing(runtime, internedName, runtime.getObject());
     }
     
     public static IRubyObject nullToNil(IRubyObject value, Ruby runtime) {
         return value != null ? value : runtime.getNil();
     }
     
     public static RubyClass prepareSuperClass(Ruby runtime, IRubyObject rubyClass) {
         RubyClass.checkInheritable(rubyClass); // use the same logic as in EvaluationState
         return (RubyClass)rubyClass;
     }
     
     public static RubyModule prepareClassNamespace(ThreadContext context, IRubyObject rubyModule) {
         if (rubyModule == null || rubyModule.isNil()) { // the isNil check should go away since class nil::Foo;end is not supposed be correct
             rubyModule = context.getCurrentScope().getStaticScope().getModule();
 
             if (rubyModule == null) {
                 throw context.getRuntime().newTypeError("no outer class/module");
             }
         }
 
         if (rubyModule instanceof RubyModule) {
             return (RubyModule)rubyModule;
         } else {
             throw context.getRuntime().newTypeError(rubyModule + " is not a class/module");
         }
     }
     
     public static IRubyObject setClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name, IRubyObject value) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     public static IRubyObject fastSetClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String internedName, IRubyObject value) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.fastSetClassVar(internedName, value);
    
         return value;
     }
     
     public static IRubyObject declareClassVariable(ThreadContext context, Ruby runtime, IRubyObject self, String name, IRubyObject value) {
         // FIXME: This isn't quite right; it shouldn't evaluate the value if it's going to throw the error
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) throw runtime.newTypeError("no class/module to define class variable");
         
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     public static IRubyObject fastDeclareClassVariable(ThreadContext context, Ruby runtime, IRubyObject self, String internedName, IRubyObject value) {
         // FIXME: This isn't quite right; it shouldn't evaluate the value if it's going to throw the error
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) throw runtime.newTypeError("no class/module to define class variable");
         
         rubyClass.fastSetClassVar(internedName, value);
    
         return value;
     }
     
     public static void handleArgumentSizes(ThreadContext context, Ruby runtime, int given, int required, int opt, int rest) {
         if (opt == 0) {
             if (rest < 0) {
                 // no opt, no rest, exact match
                 if (given != required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 }
             } else {
                 // only rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 }
             }
         } else {
             if (rest < 0) {
                 // opt but no rest, must be at least required and no more than required + opt
                 if (given < required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 } else if (given > (required + opt)) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + (required + opt) + ")");
                 }
             } else {
                 // opt and rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 }
             }
         }
     }
     
     /**
      * If it's Redo, Next, or Break, rethrow it as a normal exception for while to handle
      * @param re
      * @param runtime
      */
     public static Throwable unwrapRedoNextBreakOrJustLocalJump(RaiseException re, ThreadContext context) {
         RubyException exception = re.getException();
         if (context.getRuntime().getLocalJumpError().isInstance(exception)) {
             RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
 
             switch (jumpError.getReason()) {
             case REDO:
                 return JumpException.REDO_JUMP;
             case NEXT:
                 return new JumpException.NextJump(jumpError.exit_value());
             case BREAK:
                 return new JumpException.BreakJump(context.getFrameJumpTarget(), jumpError.exit_value());
             }
         }
         return re;
     }
     
     public static String getLocalJumpTypeOrRethrow(RaiseException re) {
         RubyException exception = re.getException();
         Ruby runtime = exception.getRuntime();
         if (runtime.getLocalJumpError().isInstance(exception)) {
             RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
 
             IRubyObject reason = jumpError.reason();
 
             return reason.asJavaString();
         }
 
         throw re;
     }
     
     public static IRubyObject unwrapLocalJumpErrorValue(RaiseException re) {
         return ((RubyLocalJumpError)re.getException()).exit_value();
     }
     
     public static IRubyObject processBlockArgument(Ruby runtime, Block block) {
         if (!block.isGiven()) {
             return runtime.getNil();
         }
         
         return processGivenBlock(block, runtime);
     }
 
     private static IRubyObject processGivenBlock(Block block, Ruby runtime) {
         RubyProc blockArg = block.getProcObject();
 
         if (blockArg == null) {
             blockArg = runtime.newBlockPassProc(Block.Type.PROC, block);
             blockArg.getBlock().type = Block.Type.PROC;
         }
 
         return blockArg;
     }
     
     public static Block getBlockFromBlockPassBody(Ruby runtime, IRubyObject proc, Block currentBlock) {
         // No block from a nil proc
         if (proc.isNil()) return Block.NULL_BLOCK;
 
         // If not already a proc then we should try and make it one.
         if (!(proc instanceof RubyProc)) {
             proc = coerceProc(proc, runtime);
         }
 
         return getBlockFromProc(currentBlock, proc);
     }
 
     private static IRubyObject coerceProc(IRubyObject proc, Ruby runtime) throws RaiseException {
         proc = TypeConverter.convertToType(proc, runtime.getProc(), "to_proc", false);
 
         if (!(proc instanceof RubyProc)) {
             throw runtime.newTypeError("wrong argument type " + proc.getMetaClass().getName() + " (expected Proc)");
         }
         return proc;
     }
 
     private static Block getBlockFromProc(Block currentBlock, IRubyObject proc) {
         // TODO: Add safety check for taintedness
         if (currentBlock != null && currentBlock.isGiven()) {
             RubyProc procObject = currentBlock.getProcObject();
             // The current block is already associated with proc.  No need to create a new one
             if (procObject != null && procObject == proc) {
                 return currentBlock;
             }
         }
 
         return ((RubyProc) proc).getBlock();       
     }
     
     public static Block getBlockFromBlockPassBody(IRubyObject proc, Block currentBlock) {
         return getBlockFromBlockPassBody(proc.getRuntime(), proc, currentBlock);
 
     }
     
     public static IRubyObject backref(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         if(backref instanceof RubyMatchData) {
             ((RubyMatchData)backref).use();
         }
         return backref;
     }
     
     public static IRubyObject backrefLastMatch(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.last_match(backref);
     }
     
     public static IRubyObject backrefMatchPre(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.match_pre(backref);
     }
     
     public static IRubyObject backrefMatchPost(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.match_post(backref);
     }
     
     public static IRubyObject backrefMatchLast(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.match_last(backref);
     }
     
     public static IRubyObject callZSuper(Ruby runtime, ThreadContext context, Block block, IRubyObject self) {
         // Has the method that is calling super received a block argument
         if (!block.isGiven()) block = context.getCurrentFrame().getBlock(); 
         
         return RuntimeHelpers.invokeSuper(context, self, context.getCurrentScope().getArgValues(), block);
     }
     
     public static IRubyObject[] appendToObjectArray(IRubyObject[] array, IRubyObject add) {
         IRubyObject[] newArray = new IRubyObject[array.length + 1];
         System.arraycopy(array, 0, newArray, 0, array.length);
         newArray[array.length] = add;
         return newArray;
     }
     
     public static JumpException.ReturnJump returnJump(IRubyObject result, ThreadContext context) {
         return context.returnJump(result);
     }
     
     public static IRubyObject breakJumpInWhile(JumpException.BreakJump bj, ThreadContext context) {
         // JRUBY-530, while case
         if (bj.getTarget() == context.getFrameJumpTarget()) {
             return (IRubyObject) bj.getValue();
         }
 
         throw bj;
     }
     
     public static IRubyObject breakJump(ThreadContext context, IRubyObject value) {
         throw new JumpException.BreakJump(context.getFrameJumpTarget(), value);
     }
     
     public static IRubyObject breakLocalJumpError(Ruby runtime, IRubyObject value) {
         throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, value, "unexpected break");
     }
     
     public static IRubyObject[] concatObjectArrays(IRubyObject[] array, IRubyObject[] add) {
         IRubyObject[] newArray = new IRubyObject[array.length + add.length];
         System.arraycopy(array, 0, newArray, 0, array.length);
         System.arraycopy(add, 0, newArray, array.length, add.length);
         return newArray;
     }
 
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject[] exceptions, ThreadContext context) {
         for (int i = 0; i < exceptions.length; i++) {
             IRubyObject result = isExceptionHandled(currentException, exceptions[i], context);
             if (result.isTrue()) return result;
         }
         return context.getRuntime().getFalse();
     }
 
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject exception, ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (!runtime.getModule().isInstance(exception)) {
             throw runtime.newTypeError("class or module required for rescue clause");
         }
         IRubyObject result = invoke(context, exception, "===", currentException);
         if (result.isTrue()) return result;
         return runtime.getFalse();
     }
     
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject exception0, IRubyObject exception1, ThreadContext context) {
         IRubyObject result = isExceptionHandled(currentException, exception0, context);
         if (result.isTrue()) return result;
         return isExceptionHandled(currentException, exception1, context);
     }
 
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject exception0, IRubyObject exception1, IRubyObject exception2, ThreadContext context) {
         IRubyObject result = isExceptionHandled(currentException, exception0, context);
         if (result.isTrue()) return result;
         return isExceptionHandled(currentException, exception1, exception2, context);
     }
 
     private static boolean checkJavaException(Exception currentException, IRubyObject exception) {
         if (exception instanceof RubyClass) {
             RubyClass rubyClass = (RubyClass)exception;
             JavaClass javaClass = (JavaClass)rubyClass.fastGetInstanceVariable("@java_class");
             if (javaClass != null) {
                 Class cls = javaClass.javaClass();
                 if (cls.isInstance(currentException)) {
                     return true;
                 }
             }
         }
         return false;
     }
     
     public static IRubyObject isJavaExceptionHandled(Exception currentException, IRubyObject[] exceptions, ThreadContext context) {
         if (currentException instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentException).getException(), exceptions, context);
         } else {
             for (int i = 0; i < exceptions.length; i++) {
                 if (checkJavaException(currentException, exceptions[0])) {
                     return context.getRuntime().getTrue();
                 }
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Exception currentException, IRubyObject exception, ThreadContext context) {
         if (currentException instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentException).getException(), exception, context);
         } else {
             if (checkJavaException(currentException, exception)) {
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Exception currentException, IRubyObject exception0, IRubyObject exception1, ThreadContext context) {
         if (currentException instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentException).getException(), exception0, exception1, context);
         } else {
             if (checkJavaException(currentException, exception0)) {
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentException, exception1)) {
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Exception currentException, IRubyObject exception0, IRubyObject exception1, IRubyObject exception2, ThreadContext context) {
         if (currentException instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentException).getException(), exception0, exception1, exception2, context);
         } else {
             if (checkJavaException(currentException, exception0)) {
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentException, exception1)) {
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentException, exception2)) {
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static void storeExceptionInErrorInfo(Exception currentException, ThreadContext context) {
         IRubyObject exception = null;
         if (currentException instanceof RaiseException) {
             exception = ((RaiseException)currentException).getException();
         } else {
             exception = JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), currentException);
         }
         context.setErrorInfo(exception);
     }
 
     public static void clearErrorInfo(ThreadContext context) {
         context.setErrorInfo(context.getRuntime().getNil());
     }
     
     public static void checkSuperDisabledOrOutOfMethod(ThreadContext context) {
         if (context.getFrameKlazz() == null) {
             String name = context.getFrameName();
             if (name != null) {
                 throw context.getRuntime().newNameError("superclass method '" + name + "' disabled", name);
             } else {
                 throw context.getRuntime().newNoMethodError("super called outside of method", null, context.getRuntime().getNil());
             }
         }
     }
     
     public static Block ensureSuperBlock(Block given, Block parent) {
         if (!given.isGiven()) {
             return parent;
         }
         return given;
     }
     
     public static RubyModule findImplementerIfNecessary(RubyModule clazz, RubyModule implementationClass) {
         if (implementationClass != null && implementationClass.needsImplementer()) {
             // modules are included with a shim class; we must find that shim to handle super() appropriately
             return clazz.findImplementer(implementationClass);
         } else {
             // classes are directly in the hierarchy, so no special logic is necessary for implementer
             return implementationClass;
         }
     }
     
     public static RubyArray createSubarray(RubyArray input, int start) {
         return (RubyArray)input.subseqLight(start, input.size() - start);
     }
     
     public static RubyArray createSubarray(IRubyObject[] input, Ruby runtime, int start) {
         if (start >= input.length) {
             return RubyArray.newEmptyArray(runtime);
         } else {
             return RubyArray.newArrayNoCopy(runtime, input, start);
         }
     }
 
     public static RubyArray createSubarray(IRubyObject[] input, Ruby runtime, int start, int exclude) {
         int length = input.length - exclude - start;
         if (length <= 0) {
             return RubyArray.newEmptyArray(runtime);
         } else {
             return RubyArray.newArrayNoCopy(runtime, input, start, length);
         }
     }
 
     public static IRubyObject elementOrNull(IRubyObject[] input, int element) {
         if (element >= input.length) {
             return null;
         } else {
             return input[element];
         }
     }
 
     public static IRubyObject elementOrNil(IRubyObject[] input, int element, IRubyObject nil) {
         if (element >= input.length) {
             return nil;
         } else {
             return input[element];
         }
     }
     
     public static RubyBoolean isWhenTriggered(IRubyObject expression, IRubyObject expressionsObject, ThreadContext context) {
         RubyArray expressions = RuntimeHelpers.splatValue(expressionsObject);
         for (int j = 0,k = expressions.getLength(); j < k; j++) {
             IRubyObject condition = expressions.eltInternal(j);
 
             if ((expression != null && condition.callMethod(context, "===", expression)
                     .isTrue())
                     || (expression == null && condition.isTrue())) {
                 return context.getRuntime().getTrue();
             }
         }
         
         return context.getRuntime().getFalse();
     }
     
     public static IRubyObject setConstantInModule(IRubyObject module, IRubyObject value, String name, ThreadContext context) {
         return context.setConstantInModule(name, module, value);
     }
 
     public static IRubyObject setConstantInCurrent(IRubyObject value, ThreadContext context, String name) {
         return context.setConstantInCurrent(name, value);
     }
     
     public static IRubyObject retryJump() {
         throw JumpException.RETRY_JUMP;
     }
     
     public static IRubyObject redoJump() {
         throw JumpException.REDO_JUMP;
     }
     
     public static IRubyObject redoLocalJumpError(Ruby runtime) {
         throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.REDO, runtime.getNil(), "unexpected redo");
     }
     
     public static IRubyObject nextJump(IRubyObject value) {
         throw new JumpException.NextJump(value);
     }
     
     public static IRubyObject nextLocalJumpError(Ruby runtime, IRubyObject value) {
         throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.NEXT, value, "unexpected next");
     }
     
     public static final int MAX_SPECIFIC_ARITY_OBJECT_ARRAY = 5;
     
     public static IRubyObject[] constructObjectArray(IRubyObject one) {
         return new IRubyObject[] {one};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two) {
         return new IRubyObject[] {one, two};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three) {
         return new IRubyObject[] {one, two, three};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four) {
         return new IRubyObject[] {one, two, three, four};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five) {
         return new IRubyObject[] {one, two, three, four, five};
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one) {
         return RubyArray.newArrayLight(runtime, one);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two) {
         return RubyArray.newArrayLight(runtime, one, two);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three) {
         return RubyArray.newArrayLight(runtime, one, two, three);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four) {
         return RubyArray.newArrayLight(runtime, one, two, three, four);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five);
     }
     
     public static String[] constructStringArray(String one) {
         return new String[] {one};
     }
     
     public static String[] constructStringArray(String one, String two) {
         return new String[] {one, two};
     }
     
     public static String[] constructStringArray(String one, String two, String three) {
         return new String[] {one, two, three};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four) {
         return new String[] {one, two, three, four};
