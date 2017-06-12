diff --git a/src/org/jruby/compiler/ASTCompiler.java b/src/org/jruby/compiler/ASTCompiler.java
index eec3486d68..e667268a7a 100644
--- a/src/org/jruby/compiler/ASTCompiler.java
+++ b/src/org/jruby/compiler/ASTCompiler.java
@@ -385,2002 +385,2002 @@ public class ASTCompiler {
                 compileRescue(node, context, expr);
                 break;
             case RETRYNODE:
                 compileRetry(node, context, expr);
                 break;
             case RETURNNODE:
                 compileReturn(node, context, expr);
                 break;
             case ROOTNODE:
                 throw new NotCompilableException("Use compileRoot(); Root node at: " + node.getPosition());
             case SCLASSNODE:
                 compileSClass(node, context, expr);
                 break;
             case SELFNODE:
                 compileSelf(node, context, expr);
                 break;
             case SPLATNODE:
                 compileSplat(node, context, expr);
                 break;
             case STRNODE:
                 compileStr(node, context, expr);
                 break;
             case SUPERNODE:
                 compileSuper(node, context, expr);
                 break;
             case SVALUENODE:
                 compileSValue(node, context, expr);
                 break;
             case SYMBOLNODE:
                 compileSymbol(node, context, expr);
                 break;
             case TOARYNODE:
                 compileToAry(node, context, expr);
                 break;
             case TRUENODE:
                 compileTrue(node, context, expr);
                 break;
             case UNDEFNODE:
                 compileUndef((UndefNode) node, context, expr);
                 break;
             case UNTILNODE:
                 compileUntil(node, context, expr);
                 break;
             case VALIASNODE:
                 compileVAlias(node, context, expr);
                 break;
             case VCALLNODE:
                 compileVCall(node, context, expr);
                 break;
             case WHILENODE:
                 compileWhile(node, context, expr);
                 break;
             case WHENNODE:
                 assert false : "When nodes are handled by case node compilation.";
                 break;
             case XSTRNODE:
                 compileXStr(node, context, expr);
                 break;
             case YIELDNODE:
                 compileYield(node, context, expr);
                 break;
             case ZARRAYNODE:
                 compileZArray(node, context, expr);
                 break;
             case ZSUPERNODE:
                 compileZSuper(node, context, expr);
                 break;
             default:
                 throw new NotCompilableException("Unknown node encountered in compiler: " + node);
         }
     }
 
     public void compileArguments(Node node, BodyCompiler context) {
         switch (node.getNodeType()) {
             case ARGSCATNODE:
                 compileArgsCatArguments(node, context, true);
                 break;
             case ARGSPUSHNODE:
                 compileArgsPushArguments(node, context, true);
                 break;
             case ARRAYNODE:
                 compileArrayArguments(node, context, true);
                 break;
             case SPLATNODE:
                 compileSplatArguments(node, context, true);
                 break;
             default:
                 compile(node, context, true);
                 context.convertToJavaArray();
         }
     }
     
     public class VariableArityArguments implements ArgumentsCallback {
         private Node node;
         
         public VariableArityArguments(Node node) {
             this.node = node;
         }
         
         public int getArity() {
             return -1;
         }
         
         public void call(BodyCompiler context) {
             compileArguments(node, context);
         }
     }
     
     public class SpecificArityArguments implements ArgumentsCallback {
         private int arity;
         private Node node;
         
         public SpecificArityArguments(Node node) {
             if (node.getNodeType() == NodeType.ARRAYNODE && ((ArrayNode)node).isLightweight()) {
                 // only arrays that are "lightweight" are being used as args arrays
                 this.arity = ((ArrayNode)node).size();
             } else {
                 // otherwise, it's a literal array
                 this.arity = 1;
             }
             this.node = node;
         }
         
         public int getArity() {
             return arity;
         }
         
         public void call(BodyCompiler context) {
             if (node.getNodeType() == NodeType.ARRAYNODE) {
                 ArrayNode arrayNode = (ArrayNode)node;
                 if (arrayNode.isLightweight()) {
                     // explode array, it's an internal "args" array
                     for (Node n : arrayNode.childNodes()) {
                         compile(n, context,true);
                     }
                 } else {
                     // use array as-is, it's a literal array
                     compile(arrayNode, context,true);
                 }
             } else {
                 compile(node, context,true);
             }
         }
     }
 
     public ArgumentsCallback getArgsCallback(Node node) {
         if (node == null) {
             return null;
         }
         // unwrap newline nodes to get their actual type
         while (node.getNodeType() == NodeType.NEWLINENODE) {
             node = ((NewlineNode)node).getNextNode();
         }
         switch (node.getNodeType()) {
             case ARGSCATNODE:
             case ARGSPUSHNODE:
             case SPLATNODE:
                 return new VariableArityArguments(node);
             case ARRAYNODE:
                 ArrayNode arrayNode = (ArrayNode)node;
                 if (arrayNode.size() == 0) {
                     return null;
                 } else if (arrayNode.size() > 3) {
                     return new VariableArityArguments(node);
                 } else {
                     return new SpecificArityArguments(node);
                 }
             default:
                 return new SpecificArityArguments(node);
         }
     }
 
     public void compileAssignment(Node node, BodyCompiler context, boolean expr) {
         switch (node.getNodeType()) {
             case ATTRASSIGNNODE:
                 compileAttrAssignAssignment(node, context, expr);
                 break;
             case DASGNNODE:
                 DAsgnNode dasgnNode = (DAsgnNode)node;
                 context.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth(), expr);
                 break;
             case CLASSVARASGNNODE:
                 compileClassVarAsgnAssignment(node, context, expr);
                 break;
             case CLASSVARDECLNODE:
                 compileClassVarDeclAssignment(node, context, expr);
                 break;
             case CONSTDECLNODE:
                 compileConstDeclAssignment(node, context, expr);
                 break;
             case GLOBALASGNNODE:
                 compileGlobalAsgnAssignment(node, context, expr);
                 break;
             case INSTASGNNODE:
                 compileInstAsgnAssignment(node, context, expr);
                 break;
             case LOCALASGNNODE:
                 LocalAsgnNode localAsgnNode = (LocalAsgnNode)node;
                 context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth(), expr);
                 break;
             case MULTIPLEASGNNODE:
                 compileMultipleAsgnAssignment(node, context, expr);
                 break;
             case ZEROARGNODE:
                 context.consumeCurrentValue();;
                 break;
             default:
                 throw new NotCompilableException("Can't compile assignment node: " + node);
         }
     }
 
     public void compileAlias(final AliasNode alias, BodyCompiler context, boolean expr) {
         CompilerCallback args = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(alias.getNewName(), context, true);
                 compile(alias.getOldName(), context, true);
             }
         };
 
         context.defineAlias(args);
         
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileAnd(Node node, BodyCompiler context, final boolean expr) {
         final AndNode andNode = (AndNode) node;
 
         if (andNode.getFirstNode().getNodeType().alwaysTrue()) {
             // compile first node as non-expr and then second node
             compile(andNode.getFirstNode(), context, false);
             compile(andNode.getSecondNode(), context, expr);
         } else if (andNode.getFirstNode().getNodeType().alwaysFalse()) {
             // compile first node only
             compile(andNode.getFirstNode(), context, expr);
         } else {
             compile(andNode.getFirstNode(), context, true);
             BranchCallback longCallback = new BranchCallback() {
                         public void branch(BodyCompiler context) {
                             compile(andNode.getSecondNode(), context, true);
                         }
                     };
 
             context.performLogicalAnd(longCallback);
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileArray(Node node, BodyCompiler context, boolean expr) {
         ArrayNode arrayNode = (ArrayNode) node;
         
         if (expr) {
             ArrayCallback callback = new ArrayCallback() {
                 public void nextValue(BodyCompiler context, Object sourceArray, int index) {
                     Node node = (Node) ((Object[]) sourceArray)[index];
                     compile(node, context, true);
                 }
             };
 
             List<Node> childNodes = arrayNode.childNodes();
 
             if (isListAllLiterals(arrayNode)) {
                 context.createNewLiteralArray(childNodes.toArray(), callback, arrayNode.isLightweight());
             } else {
                 context.createNewArray(childNodes.toArray(), callback, arrayNode.isLightweight());
             }
         } else {
             if (isListAllLiterals(arrayNode)) {
                 // do nothing, no observable effect
             } else {
                 for (Iterator<Node> iter = arrayNode.childNodes().iterator(); iter.hasNext();) {
                     Node nextNode = iter.next();
                     compile(nextNode, context, false);
                 }
             }
         }
     }
 
     private boolean isListAllLiterals(ListNode listNode) {
         for (int i = 0; i < listNode.size(); i++) {
             switch (listNode.get(i).getNodeType()) {
             case STRNODE:
             case FLOATNODE:
             case FIXNUMNODE:
             case BIGNUMNODE:
             case REGEXPNODE:
             case ZARRAYNODE:
             case SYMBOLNODE:
             case NILNODE:
                 // simple literals, continue
                 continue;
             case ARRAYNODE:
                 // scan contained array
                 if (isListAllLiterals((ArrayNode)listNode.get(i))) {
                     continue;
                 } else {
                     return false;
                 }
             case HASHNODE:
                 // scan contained hash
                 if (isListAllLiterals(((HashNode)listNode.get(i)).getListNode())) {
                     continue;
                 } else {
                     return false;
                 }
             default:
                 return false;
             }
         }
 
         // all good!
         return true;
     }
 
     public void compileArgsCat(Node node, BodyCompiler context, boolean expr) {
         ArgsCatNode argsCatNode = (ArgsCatNode) node;
 
         compile(argsCatNode.getFirstNode(), context,true);
         compile(argsCatNode.getSecondNode(), context,true);
         context.argsCat();
 
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileArgsPush(Node node, BodyCompiler context, boolean expr) {
         throw new NotCompilableException("ArgsPush should never be encountered bare in 1.8");
     }
 
     private void compileAttrAssign(Node node, BodyCompiler context, boolean expr) {
         final AttrAssignNode attrAssignNode = (AttrAssignNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(attrAssignNode.getReceiverNode(), context,true);
             }
         };
         
         ArgumentsCallback argsCallback = getArgsCallback(attrAssignNode.getArgsNode());
 
         // Ruby 1.8 and 1.9 only bypass visibility check when receiver is statically
         // determined to be self.
         boolean isSelf = attrAssignNode.getReceiverNode() instanceof SelfNode;
         context.getInvocationCompiler().invokeAttrAssign(attrAssignNode.getName(), receiverCallback, argsCallback, isSelf, expr);
     }
 
     public void compileAttrAssignAssignment(Node node, BodyCompiler context, boolean expr) {
         final AttrAssignNode attrAssignNode = (AttrAssignNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(attrAssignNode.getReceiverNode(), context,true);
             }
         };
         ArgumentsCallback argsCallback = getArgsCallback(attrAssignNode.getArgsNode());
 
         // Ruby 1.8 and 1.9 only bypass visibility check when receiver is statically
         // determined to be self.
         boolean isSelf = attrAssignNode.getReceiverNode() instanceof SelfNode;
         context.getInvocationCompiler().invokeAttrAssignMasgn(attrAssignNode.getName(), receiverCallback, argsCallback, isSelf);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileBackref(Node node, BodyCompiler context, boolean expr) {
         BackRefNode iVisited = (BackRefNode) node;
 
         context.performBackref(iVisited.getType());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileBegin(Node node, BodyCompiler context, boolean expr) {
         BeginNode beginNode = (BeginNode) node;
 
         compile(beginNode.getBodyNode(), context, expr);
     }
 
     public void compileBignum(Node node, BodyCompiler context, boolean expr) {
         if (expr) context.createNewBignum(((BignumNode) node).getValue());
     }
 
     public void compileBlock(Node node, BodyCompiler context, boolean expr) {
         BlockNode blockNode = (BlockNode) node;
 
         for (Iterator<Node> iter = blockNode.childNodes().iterator(); iter.hasNext();) {
             Node n = iter.next();
 
             compile(n, context, iter.hasNext() ? false : expr);
         }
     }
 
     public void compileBreak(Node node, BodyCompiler context, boolean expr) {
         final BreakNode breakNode = (BreakNode) node;
 
         CompilerCallback valueCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (breakNode.getValueNode() != null) {
                             compile(breakNode.getValueNode(), context, true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         context.issueBreakEvent(valueCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileCall(Node node, BodyCompiler context, boolean expr) {
         final CallNode callNode = (CallNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(callNode.getReceiverNode(), context, true);
             }
         };
 
         ArgumentsCallback argsCallback = getArgsCallback(callNode.getArgsNode());
         CompilerCallback closureArg = getBlock(callNode.getIterNode());
 
         String name = callNode.getName();
         CallType callType = CallType.NORMAL;
 
         DYNOPT: if (RubyInstanceConfig.DYNOPT_COMPILE_ENABLED) {
             // dynopt does not handle non-local block flow control yet, so we bail out
             // if there's a closure.
             if (callNode.getIterNode() != null) break DYNOPT;
             if (callNode.callAdapter instanceof CachingCallSite) {
                 CachingCallSite cacheSite = (CachingCallSite)callNode.callAdapter;
                 if (cacheSite.isOptimizable()) {
                     CacheEntry entry = cacheSite.getCache();
                     if (entry.method.getNativeCall() != null) {
                         NativeCall nativeCall = entry.method.getNativeCall();
 
                         // only do direct calls for specific arity
                         if (argsCallback == null || argsCallback.getArity() >= 0 && argsCallback.getArity() <= 3) {
                             if (compileIntrinsic(context, callNode, cacheSite.methodName, entry.token, entry.method, receiverCallback, argsCallback, closureArg)) {
                                 // intrinsic compilation worked, hooray!
                                 return;
                             } else {
                                 // otherwise, normal straight-through native call
                                 context.getInvocationCompiler().invokeNative(
                                         name, nativeCall, entry.token, receiverCallback,
                                         argsCallback, closureArg, CallType.NORMAL,
                                         callNode.getIterNode() instanceof IterNode);
                                 return;
                             }
                         }
                     }
 
                     // check for a recursive call
                     if (callNode.getReceiverNode() instanceof SelfNode) {
                         // recursive calls
                         if (compileRecursiveCall(callNode.getName(), entry.token, CallType.NORMAL, callNode.getIterNode() instanceof IterNode, entry.method, context, argsCallback, closureArg, expr)) return;
                     }
                 }
             }
         }
 
         if (argsCallback != null && argsCallback.getArity() == 1) {
             Node argument = callNode.getArgsNode().childNodes().get(0);
             if (MethodIndex.hasFastOps(name)) {
                 if (argument instanceof FixnumNode) {
                     context.getInvocationCompiler().invokeBinaryFixnumRHS(name, receiverCallback, ((FixnumNode)argument).getValue());
                     if (!expr) context.consumeCurrentValue();
                     return;
                 } else if (argument instanceof FloatNode) {
                     context.getInvocationCompiler().invokeBinaryFloatRHS(name, receiverCallback, ((FloatNode)argument).getValue());
                     if (!expr) context.consumeCurrentValue();
                     return;
                 }
             }
         }
 
         // if __send__ with a literal symbol, compile it as a direct fcall
         if (RubyInstanceConfig.FASTSEND_COMPILE_ENABLED) {
             String literalSend = getLiteralSend(callNode);
             if (literalSend != null) {
                 name = literalSend;
                 callType = CallType.FUNCTIONAL;
             }
         }
         
         if (callNode instanceof SpecialArgs) {
             context.getInvocationCompiler().invokeDynamicVarargs(
                     name, receiverCallback, argsCallback,
                     callType, closureArg, callNode.getIterNode() instanceof IterNode);
         } else {
             context.getInvocationCompiler().invokeDynamic(
                     name, receiverCallback, argsCallback,
                     callType, closureArg, callNode.getIterNode() instanceof IterNode);
         }
         
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     private static final Map<Class, Map<Class, Map<String, String>>> Intrinsics;
     static {
         Intrinsics = new HashMap();
 
         Map<Class, Map<String, String>> fixnumIntrinsics = new HashMap();
         Intrinsics.put(RubyFixnum.class, fixnumIntrinsics);
 
         Map<String, String> fixnumLongIntrinsics = new HashMap();
         fixnumIntrinsics.put(FixnumNode.class, fixnumLongIntrinsics);
         
         fixnumLongIntrinsics.put("+", "op_plus");
         fixnumLongIntrinsics.put("-", "op_minus");
         fixnumLongIntrinsics.put("/", "op_div");
         fixnumLongIntrinsics.put("*", "op_plus");
         fixnumLongIntrinsics.put("**", "op_pow");
         fixnumLongIntrinsics.put("<", "op_lt");
         fixnumLongIntrinsics.put("<=", "op_le");
         fixnumLongIntrinsics.put(">", "op_gt");
         fixnumLongIntrinsics.put(">=", "op_ge");
         fixnumLongIntrinsics.put("==", "op_equal");
         fixnumLongIntrinsics.put("<=>", "op_cmp");
 
         Map<Class, Map<String, String>> floatIntrinsics = new HashMap();
         Intrinsics.put(RubyFloat.class, floatIntrinsics);
 
         Map<String, String>floatDoubleIntrinsics = new HashMap();
         floatIntrinsics.put(FloatNode.class, floatDoubleIntrinsics);
         
         floatDoubleIntrinsics.put("+", "op_plus");
         floatDoubleIntrinsics.put("-", "op_minus");
         floatDoubleIntrinsics.put("/", "op_fdiv");
         floatDoubleIntrinsics.put("*", "op_plus");
         floatDoubleIntrinsics.put("**", "op_pow");
         floatDoubleIntrinsics.put("<", "op_lt");
         floatDoubleIntrinsics.put("<=", "op_le");
         floatDoubleIntrinsics.put(">", "op_gt");
         floatDoubleIntrinsics.put(">=", "op_ge");
         floatDoubleIntrinsics.put("==", "op_equal");
         floatDoubleIntrinsics.put("<=>", "op_cmp");
     }
 
     private boolean compileRecursiveCall(String name, int generation, CallType callType, boolean iterator, DynamicMethod method, BodyCompiler context, ArgumentsCallback argsCallback, CompilerCallback closure, boolean expr) {
         if (currentBodyNode != null && context.isSimpleRoot()) {
             if (method instanceof InterpretedMethod) {
                 InterpretedMethod target = (InterpretedMethod)method;
                 if (target.getBodyNode() == currentBodyNode) {
                     context.getInvocationCompiler().invokeRecursive(name, generation, argsCallback, closure, callType, iterator);
                     if (!expr) context.consumeCurrentValue();
                     return true;
                 }
             }
             if (method instanceof DefaultMethod) {
                 DefaultMethod target = (DefaultMethod)method;
                 if (target.getBodyNode() == currentBodyNode) {
                     context.getInvocationCompiler().invokeRecursive(name, generation, argsCallback, closure, callType, iterator);
                     if (!expr) context.consumeCurrentValue();
                     return true;
                 }
             }
 
             if (method instanceof JittedMethod) {
                 DefaultMethod target = (DefaultMethod)((JittedMethod)method).getRealMethod();
                 if (target.getBodyNode() == currentBodyNode) {
                     context.getInvocationCompiler().invokeRecursive(name, generation, argsCallback, closure, callType, iterator);
                     if (!expr) context.consumeCurrentValue();
                     return true;
                 }
             }
         }
         return false;
     }
 
     private boolean compileTrivialCall(String name, DynamicMethod method, int generation, BodyCompiler context, boolean expr) {
         Node simpleBody = null;
         if (method instanceof InterpretedMethod) {
             InterpretedMethod target = (InterpretedMethod)method;
             simpleBody = target.getBodyNode();
             while (simpleBody instanceof NewlineNode) simpleBody = ((NewlineNode)simpleBody).getNextNode();
         }
 
         if (method instanceof DefaultMethod) {
             DefaultMethod target = (DefaultMethod)method;
             simpleBody = target.getBodyNode();
             while (simpleBody instanceof NewlineNode) simpleBody = ((NewlineNode)simpleBody).getNextNode();
         }
 
         if (method instanceof JittedMethod) {
             DefaultMethod target = (DefaultMethod)((JittedMethod)method).getRealMethod();
             simpleBody = target.getBodyNode();
             while (simpleBody instanceof NewlineNode) simpleBody = ((NewlineNode)simpleBody).getNextNode();
         }
 
         if (simpleBody != null) {
             switch (simpleBody.getNodeType()) {
             case SELFNODE:
             case INSTVARNODE:
             case NILNODE:
             case FIXNUMNODE:
             case FLOATNODE:
             case STRNODE:
             case BIGNUMNODE:
             case FALSENODE:
             case TRUENODE:
             case SYMBOLNODE:
             case XSTRNODE:
                 final Node simpleBodyFinal = simpleBody;
                 context.getInvocationCompiler().invokeTrivial(name, generation, new CompilerCallback() {
                     public void call(BodyCompiler context) {
                         compile(simpleBodyFinal, context, true);
                     }
                 });
                 if (!expr) context.consumeCurrentValue();
                 return true;
             }
         }
         return false;
     }
 
     private boolean compileIntrinsic(BodyCompiler context, CallNode callNode, String name, int generation, DynamicMethod method, CompilerCallback receiverCallback, ArgumentsCallback argsCallback, CompilerCallback closureCallback) {
         if (!(method.getImplementationClass() instanceof RubyClass)) return false;
 
         RubyClass implClass = (RubyClass)method.getImplementationClass();
         Map<Class, Map<String, String>> typeIntrinsics = Intrinsics.get(implClass.getReifiedClass());
         if (typeIntrinsics != null) {
             if (argsCallback != null && argsCallback.getArity() == 1) {
                 Node argument = callNode.getArgsNode().childNodes().get(0);
                 if (argument instanceof FixnumNode) {
                     Map<String, String> typeLongIntrinsics = typeIntrinsics.get(FixnumNode.class);
                     if (typeLongIntrinsics != null && typeLongIntrinsics.containsKey(name)) {
                         context.getInvocationCompiler().invokeFixnumLong(name, generation, receiverCallback, typeLongIntrinsics.get(name), ((FixnumNode)argument).getValue());
                         return true;
                     }
                 }
                 if (argument instanceof FloatNode) {
                     Map<String, String> typeDoubleIntrinsics = typeIntrinsics.get(FloatNode.class);
                     if (typeDoubleIntrinsics != null && typeDoubleIntrinsics.containsKey(name)) {
                         context.getInvocationCompiler().invokeFloatDouble(name, generation, receiverCallback, typeDoubleIntrinsics.get(name), ((FloatNode)argument).getValue());
                         return true;
                     }
                 }
             }
         }
         return false;
     }
 
     private String getLiteralSend(CallNode callNode) {
         if (callNode.getName().equals("__send__")) {
             if (callNode.getArgsNode() instanceof ArrayNode) {
                 ArrayNode arrayNode = (ArrayNode)callNode.getArgsNode();
                 if (arrayNode.get(0) instanceof SymbolNode) {
                     return ((SymbolNode)arrayNode.get(0)).getName();
                 } else if (arrayNode.get(0) instanceof StrNode) {
                     return ((StrNode)arrayNode.get(0)).getValue().toString();
                 }
             }
         }
         return null;
     }
 
     public void compileCase(Node node, BodyCompiler context, boolean expr) {
         CaseNode caseNode = (CaseNode) node;
 
         boolean hasCase = caseNode.getCaseNode() != null;
 
         // aggregate when nodes into a list, unfortunately, this is no
         List<Node> cases = caseNode.getCases().childNodes();
 
         // last node, either !instanceof WhenNode or null, is the else
         Node elseNode = caseNode.getElseNode();
 
         compileWhen(caseNode.getCaseNode(), cases, elseNode, context, expr, hasCase);
     }
 
     private FastSwitchType getHomogeneousSwitchType(List<Node> whenNodes) {
         FastSwitchType foundType = null;
         Outer: for (Node node : whenNodes) {
             WhenNode whenNode = (WhenNode)node;
             if (whenNode.getExpressionNodes() instanceof ArrayNode) {
                 ArrayNode arrayNode = (ArrayNode)whenNode.getExpressionNodes();
 
                 for (Node maybeFixnum : arrayNode.childNodes()) {
                     if (maybeFixnum instanceof FixnumNode) {
                         FixnumNode fixnumNode = (FixnumNode)maybeFixnum;
                         long value = fixnumNode.getValue();
                         if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
                             if (foundType != null && foundType != FastSwitchType.FIXNUM) return null;
                             if (foundType == null) foundType = FastSwitchType.FIXNUM;
                             continue;
                         } else {
                             return null;
                         }
                     } else {
                         return null;
                     }
                 }
             } else if (whenNode.getExpressionNodes() instanceof FixnumNode) {
                 FixnumNode fixnumNode = (FixnumNode)whenNode.getExpressionNodes();
                 long value = fixnumNode.getValue();
                 if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
                     if (foundType != null && foundType != FastSwitchType.FIXNUM) return null;
                     if (foundType == null) foundType = FastSwitchType.FIXNUM;
                     continue;
                 } else {
                     return null;
                 }
             } else if (whenNode.getExpressionNodes() instanceof StrNode) {
                 StrNode strNode = (StrNode)whenNode.getExpressionNodes();
                 if (strNode.getValue().length() == 1) {
                     if (foundType != null && foundType != FastSwitchType.SINGLE_CHAR_STRING) return null;
                     if (foundType == null) foundType = FastSwitchType.SINGLE_CHAR_STRING;
 
                     continue;
                 } else {
                     if (foundType != null && foundType != FastSwitchType.STRING) return null;
                     if (foundType == null) foundType = FastSwitchType.STRING;
 
                     continue;
                 }
             } else if (whenNode.getExpressionNodes() instanceof SymbolNode) {
                 SymbolNode symbolNode = (SymbolNode)whenNode.getExpressionNodes();
                 if (symbolNode.getName().length() == 1) {
                     if (foundType != null && foundType != FastSwitchType.SINGLE_CHAR_SYMBOL) return null;
                     if (foundType == null) foundType = FastSwitchType.SINGLE_CHAR_SYMBOL;
 
                     continue;
                 } else {
                     if (foundType != null && foundType != FastSwitchType.SYMBOL) return null;
                     if (foundType == null) foundType = FastSwitchType.SYMBOL;
 
                     continue;
                 }
             } else {
                 return null;
             }
         }
         return foundType;
     }
 
     public void compileWhen(final Node value, List<Node> whenNodes, final Node elseNode, BodyCompiler context, final boolean expr, final boolean hasCase) {
         CompilerCallback caseValue = null;
         if (value != null) caseValue = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(value, context, true);
                 context.pollThreadEvents();
             }
         };
 
         List<ArgumentsCallback> conditionals = new ArrayList<ArgumentsCallback>();
         List<CompilerCallback> bodies = new ArrayList<CompilerCallback>();
         Map<CompilerCallback, int[]> switchCases = null;
         FastSwitchType switchType = getHomogeneousSwitchType(whenNodes);
         if (switchType != null && !RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // NOTE: Currently this optimization is limited to the following situations:
             // * All expressions are int-ranged literal fixnums
             // * All expressions are literal symbols
             // * All expressions are literal strings
             // If the case value is not of the same type as the when values, the
             // default === logic applies.
             switchCases = new HashMap<CompilerCallback, int[]>();
         }
         for (Node node : whenNodes) {
             final WhenNode whenNode = (WhenNode)node;
             CompilerCallback body = new CompilerCallback() {
                 public void call(BodyCompiler context) {
                     if (RubyInstanceConfig.FULL_TRACE_ENABLED) context.traceLine();
                     compile(whenNode.getBodyNode(), context, expr);
                 }
             };
             addConditionalForWhen(whenNode, conditionals, bodies, body);
             if (switchCases != null) switchCases.put(body, getOptimizedCases(whenNode));
         }
         
         CompilerCallback fallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(elseNode, context, expr);
             }
         };
         
         context.compileSequencedConditional(caseValue, switchType, switchCases, conditionals, bodies, fallback);
     }
 
     private int[] getOptimizedCases(WhenNode whenNode) {
         if (whenNode.getExpressionNodes() instanceof ArrayNode) {
             ArrayNode expression = (ArrayNode)whenNode.getExpressionNodes();
             if (expression.get(expression.size() - 1) instanceof WhenNode) {
                 // splatted when, can't do it yet
                 return null;
             }
 
             int[] cases = new int[expression.size()];
             for (int i = 0; i < cases.length; i++) {
                 switch (expression.get(i).getNodeType()) {
                 case FIXNUMNODE:
                     cases[i] = (int)((FixnumNode)expression.get(i)).getValue();
                     break;
                 default:
                     // can't do it
                     return null;
                 }
             }
             return cases;
         } else if (whenNode.getExpressionNodes() instanceof FixnumNode) {
             FixnumNode fixnumNode = (FixnumNode)whenNode.getExpressionNodes();
             return new int[] {(int)fixnumNode.getValue()};
         } else if (whenNode.getExpressionNodes() instanceof StrNode) {
             StrNode strNode = (StrNode)whenNode.getExpressionNodes();
             if (strNode.getValue().length() == 1) {
                 return new int[] {strNode.getValue().get(0)};
             } else {
                 return new int[] {strNode.getValue().hashCode()};
             }
         } else if (whenNode.getExpressionNodes() instanceof SymbolNode) {
             SymbolNode symbolNode = (SymbolNode)whenNode.getExpressionNodes();
             if (symbolNode.getName().length() == 1) {
                 return new int[] {symbolNode.getName().charAt(0)};
             } else {
                 return new int[] {symbolNode.getName().hashCode()};
             }
         }
         return null;
     }
 
     private void addConditionalForWhen(final WhenNode whenNode, List<ArgumentsCallback> conditionals, List<CompilerCallback> bodies, CompilerCallback body) {
         bodies.add(body);
 
         // If it's a single-arg when but contains an array, we know it's a real literal array
         // FIXME: This is a gross way to figure it out; parser help similar to yield argument passing (expandArguments) would be better
         if (whenNode.getExpressionNodes() instanceof ArrayNode) {
             if (whenNode instanceof WhenOneArgNode) {
                 // one arg but it's an array, treat it as a proper array
                 conditionals.add(new ArgumentsCallback() {
                     public int getArity() {
                         return 1;
                     }
 
                     public void call(BodyCompiler context) {
                         compile(whenNode.getExpressionNodes(), context, true);
                     }
                 });
                 return;
             }
         }
         // otherwise, use normal args compiler
         conditionals.add(getArgsCallback(whenNode.getExpressionNodes()));
     }
 
     public void compileClass(Node node, BodyCompiler context, boolean expr) {
         final ClassNode classNode = (ClassNode) node;
 
         final Node superNode = classNode.getSuperNode();
 
         final Node cpathNode = classNode.getCPath();
 
         CompilerCallback superCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(superNode, context, true);
                     }
                 };
         if (superNode == null) {
             superCallback = null;
         }
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         boolean oldIsAtRoot = isAtRoot;
                         isAtRoot = false;
                         if (classNode.getBodyNode() != null) {
                             compile(classNode.getBodyNode(), context, true);
                         } else {
                             context.loadNil();
                         }
                         isAtRoot = oldIsAtRoot;
                     }
                 };
 
         CompilerCallback pathCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (cpathNode instanceof Colon2Node) {
                             Node leftNode = ((Colon2Node) cpathNode).getLeftNode();
                             if (leftNode != null) {
                                 if (leftNode instanceof NilNode) {
                                     context.raiseTypeError("No outer class");
                                 } else {
                                     compile(leftNode, context, true);
                                 }
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
         inspector.inspect(classNode.getBodyNode());
 
         context.defineClass(classNode.getCPath().getName(), classNode.getScope(), superCallback, pathCallback, bodyCallback, null, inspector);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileSClass(Node node, BodyCompiler context, boolean expr) {
         final SClassNode sclassNode = (SClassNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(sclassNode.getReceiverNode(), context, true);
                     }
                 };
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         boolean oldIsAtRoot = isAtRoot;
                         isAtRoot = false;
                         if (sclassNode.getBodyNode() != null) {
                             compile(sclassNode.getBodyNode(), context, true);
                         } else {
                             context.loadNil();
                         }
                         isAtRoot = oldIsAtRoot;
                     }
                 };
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(sclassNode.getBodyNode());
 
         context.defineClass("SCLASS", sclassNode.getScope(), null, null, bodyCallback, receiverCallback, inspector);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVar(Node node, BodyCompiler context, boolean expr) {
         ClassVarNode classVarNode = (ClassVarNode) node;
 
         context.retrieveClassVariable(classVarNode.getName());
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVarAsgn(Node node, BodyCompiler context, boolean expr) {
         final ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(classVarAsgnNode.getValueNode(), context, true);
             }
         };
 
         context.assignClassVariable(classVarAsgnNode.getName(), value);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVarAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
 
         context.assignClassVariable(classVarAsgnNode.getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVarDecl(Node node, BodyCompiler context, boolean expr) {
         final ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(classVarDeclNode.getValueNode(), context, true);
             }
         };
         
         context.declareClassVariable(classVarDeclNode.getName(), value);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVarDeclAssignment(Node node, BodyCompiler context, boolean expr) {
         ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
 
         context.declareClassVariable(classVarDeclNode.getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileConstDecl(Node node, BodyCompiler context, boolean expr) {
         // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
         ConstDeclNode constDeclNode = (ConstDeclNode) node;
         Node constNode = constDeclNode.getConstNode();
 
         if (constNode == null) {
             compile(constDeclNode.getValueNode(), context,true);
 
             context.assignConstantInCurrent(constDeclNode.getName());
         } else if (constNode.getNodeType() == NodeType.COLON2NODE) {
-            compile(((Colon2Node) constNode).getLeftNode(), context,true);
             compile(constDeclNode.getValueNode(), context,true);
+            compile(((Colon2Node) constNode).getLeftNode(), context,true);
 
             context.assignConstantInModule(constDeclNode.getName());
         } else {// colon3, assign in Object
             compile(constDeclNode.getValueNode(), context,true);
 
             context.assignConstantInObject(constDeclNode.getName());
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileConstDeclAssignment(Node node, BodyCompiler context, boolean expr) {
         // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
         ConstDeclNode constDeclNode = (ConstDeclNode) node;
         Node constNode = constDeclNode.getConstNode();
 
         if (constNode == null) {
             context.assignConstantInCurrent(constDeclNode.getName());
         } else if (constNode.getNodeType() == NodeType.COLON2NODE) {
             compile(((Colon2Node) constNode).getLeftNode(), context,true);
             context.swapValues();
             context.assignConstantInModule(constDeclNode.getName());
         } else {// colon3, assign in Object
             context.assignConstantInObject(constDeclNode.getName());
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileConst(Node node, BodyCompiler context, boolean expr) {
         ConstNode constNode = (ConstNode) node;
 
         context.retrieveConstant(constNode.getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
         // XXX: const lookup can trigger const_missing; is that enough to warrant it always being executed?
     }
 
     public void compileColon2(Node node, BodyCompiler context, boolean expr) {
         final Colon2Node iVisited = (Colon2Node) node;
         Node leftNode = iVisited.getLeftNode();
         final String name = iVisited.getName();
 
         if (leftNode == null) {
             context.loadObject();
             context.retrieveConstantFromModule(name);
         } else {
             if (node instanceof Colon2ConstNode) {
                 compile(iVisited.getLeftNode(), context, true);
                 context.retrieveConstantFromModule(name);
             } else if (node instanceof Colon2MethodNode) {
                 final CompilerCallback receiverCallback = new CompilerCallback() {
                     public void call(BodyCompiler context) {
                         compile(iVisited.getLeftNode(), context,true);
                     }
                 };
                 
                 context.getInvocationCompiler().invokeDynamic(name, receiverCallback, null, CallType.FUNCTIONAL, null, false);
             }
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileColon3(Node node, BodyCompiler context, boolean expr) {
         Colon3Node iVisited = (Colon3Node) node;
         String name = iVisited.getName();
 
         context.retrieveConstantFromObject(name);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileGetDefinitionBase(final Node node, BodyCompiler context) {
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
         case INSTVARNODE:
         case BACKREFNODE:
         case SELFNODE:
         case VCALLNODE:
         case YIELDNODE:
         case GLOBALVARNODE:
         case CONSTNODE:
         case FCALLNODE:
         case CLASSVARNODE:
         case COLON2NODE:
         case COLON3NODE:
         case CALLNODE:
             // these are all simple cases that don't require the heavier defined logic
             compileGetDefinition(node, context);
             break;
         case NEWLINENODE:
             compileGetDefinitionBase(((NewlineNode)node).getNextNode(), context);
             break;
         default:
             BranchCallback reg = new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.inDefined();
                             compileGetDefinition(node, context);
                         }
                     };
             BranchCallback out = new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.outDefined();
                         }
                     };
             context.protect(reg, out, ByteList.class);
         }
     }
 
     public void compileDefined(final Node node, BodyCompiler context, boolean expr) {
         if (expr) {
             compileGetDefinitionBase(((DefinedNode) node).getExpressionNode(), context);
             context.stringOrNil();
         }
     }
 
     public void compileGetArgumentDefinition(final Node node, BodyCompiler context, String type) {
         if (node == null) {
             context.pushByteList(ByteList.create(type));
         } else if (node instanceof ArrayNode) {
             Object endToken = context.getNewEnding();
             for (int i = 0; i < ((ArrayNode) node).size(); i++) {
                 Node iterNode = ((ArrayNode) node).get(i);
                 compileGetDefinition(iterNode, context);
                 context.ifNull(endToken);
             }
             context.pushByteList(ByteList.create(type));
             Object realToken = context.getNewEnding();
             context.go(realToken);
             context.setEnding(endToken);
             context.pushNull();
             context.setEnding(realToken);
         } else {
             compileGetDefinition(node, context);
             Object endToken = context.getNewEnding();
             context.ifNull(endToken);
             context.pushByteList(ByteList.create(type));
             Object realToken = context.getNewEnding();
             context.go(realToken);
             context.setEnding(endToken);
             context.pushNull();
             context.setEnding(realToken);
         }
     }
 
     public void compileGetDefinition(final Node node, BodyCompiler context) {
         switch (node.getNodeType()) {
             case CLASSVARASGNNODE:
             case CLASSVARDECLNODE:
             case CONSTDECLNODE:
             case DASGNNODE:
             case GLOBALASGNNODE:
             case LOCALASGNNODE:
             case MULTIPLEASGNNODE:
             case MULTIPLEASGN19NODE:
             case OPASGNNODE:
             case OPASGNANDNODE:
             case OPASGNORNODE:
             case OPELEMENTASGNNODE:
             case INSTASGNNODE: // simple assignment cases
                 context.pushByteList(Node.ASSIGNMENT_BYTELIST);
                 break;
             case ANDNODE: // all these just evaluate and then do expression if there's no error
             case ORNODE:
             case DSTRNODE:
             case DREGEXPNODE:
                 compileDefinedAndOrDStrDRegexp(node, context);
                 break;
             case NOTNODE: // evaluates, and under 1.9 flips to "method" if result is nonnull
                 {
                     context.rescue(new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     compile(node, context, false);
                                     context.pushByteList(Node.EXPRESSION_BYTELIST);
                                 }
                             }, JumpException.class,
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.pushNull();
                                 }
                             }, ByteList.class);
                     context.definedNot();
                     break;
                 }
             case BACKREFNODE:
                 compileDefinedBackref(node, context);
                 break;
             case DVARNODE:
                 compileDefinedDVar(node, context);
                 break;
             case FALSENODE:
                 context.pushByteList(Node.FALSE_BYTELIST);
                 break;
             case TRUENODE:
                 context.pushByteList(Node.TRUE_BYTELIST);
                 break;
             case LOCALVARNODE:
                 context.pushByteList(Node.LOCAL_VARIABLE_BYTELIST);
                 break;
             case MATCH2NODE:
             case MATCH3NODE:
                 context.pushByteList(Node.METHOD_BYTELIST);
                 break;
             case NILNODE:
                 context.pushByteList(Node.NIL_BYTELIST);
                 break;
             case NTHREFNODE:
                 compileDefinedNthref(node, context);
                 break;
             case SELFNODE:
                 context.pushByteList(Node.SELF_BYTELIST);
                 break;
             case VCALLNODE:
                 context.loadSelf();
                 context.isMethodBound(((VCallNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushByteList(Node.METHOD_BYTELIST);
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case YIELDNODE:
                 context.hasBlock(new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushByteList(Node.YIELD_BYTELIST);
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case GLOBALVARNODE:
                 context.isGlobalDefined(((GlobalVarNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushByteList(Node.GLOBAL_VARIABLE_BYTELIST);
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case INSTVARNODE:
                 context.isInstanceVariableDefined(((InstVarNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushByteList(Node.INSTANCE_VARIABLE_BYTELIST);
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case CONSTNODE:
                 context.isConstantDefined(((ConstNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushByteList(Node.CONSTANT_BYTELIST);
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case FCALLNODE:
                 context.loadSelf();
                 context.isMethodBound(((FCallNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 compileGetArgumentDefinition(((FCallNode) node).getArgsNode(), context, "method");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case COLON3NODE:
             case COLON2NODE:
                 {
                     final Colon3Node iVisited = (Colon3Node) node;
 
                     final String name = iVisited.getName();
 
                     BranchCallback setup = new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     if (iVisited instanceof Colon2Node) {
                                         final Node leftNode = ((Colon2Node) iVisited).getLeftNode();
                                         compile(leftNode, context,true);
                                     } else {
                                         context.loadObject();
                                     }
                                 }
                             };
                     context.isConstantBranch(setup, name);
                     break;
                 }
             case CALLNODE:
                 compileDefinedCall(node, context);
                 break;
             case CLASSVARNODE:
                 {
                     ClassVarNode iVisited = (ClassVarNode) node;
                     final Object ending = context.getNewEnding();
                     final Object failure = context.getNewEnding();
                     final Object singleton = context.getNewEnding();
                     Object second = context.getNewEnding();
                     Object third = context.getNewEnding();
 
                     context.loadCurrentModule(); //[RubyClass]
                     context.duplicateCurrentValue(); //[RubyClass, RubyClass]
                     context.ifNotNull(second); //[RubyClass]
                     context.consumeCurrentValue(); //[]
                     context.loadSelf(); //[self]
                     context.metaclass(); //[RubyClass]
                     context.duplicateCurrentValue(); //[RubyClass, RubyClass]
                     context.isClassVarDefined(iVisited.getName(),
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.consumeCurrentValue();
                                     context.pushByteList(Node.CLASS_VARIABLE_BYTELIST);
                                     context.go(ending);
                                 }
                             },
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                 }
                             });
                     context.setEnding(second);  //[RubyClass]
                     context.duplicateCurrentValue();
                     context.isClassVarDefined(iVisited.getName(),
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.consumeCurrentValue();
                                     context.pushByteList(Node.CLASS_VARIABLE_BYTELIST);
                                     context.go(ending);
                                 }
                             },
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                 }
                             });
                     context.setEnding(third); //[RubyClass]
                     context.duplicateCurrentValue(); //[RubyClass, RubyClass]
                     context.ifSingleton(singleton); //[RubyClass]
                     context.consumeCurrentValue();//[]
                     context.go(failure);
                     context.setEnding(singleton);
                     context.attached();//[RubyClass]
                     context.notIsModuleAndClassVarDefined(iVisited.getName(), failure); //[]
                     context.pushByteList(Node.CLASS_VARIABLE_BYTELIST);
                     context.go(ending);
                     context.setEnding(failure);
                     context.pushNull();
                     context.setEnding(ending);
                 }
                 break;
             case ZSUPERNODE:
                 {
                     Object fail = context.getNewEnding();
                     Object fail2 = context.getNewEnding();
                     Object fail_easy = context.getNewEnding();
                     Object ending = context.getNewEnding();
 
                     context.getFrameName(); //[String]
                     context.duplicateCurrentValue(); //[String, String]
                     context.ifNull(fail); //[String]
                     context.getFrameKlazz(); //[String, RubyClass]
                     context.duplicateCurrentValue(); //[String, RubyClass, RubyClass]
                     context.ifNull(fail2); //[String, RubyClass]
                     context.superClass();
                     context.ifNotSuperMethodBound(fail_easy);
 
                     context.pushByteList(Node.SUPER_BYTELIST);
                     context.go(ending);
 
                     context.setEnding(fail2);
                     context.consumeCurrentValue();
                     context.setEnding(fail);
                     context.consumeCurrentValue();
                     context.setEnding(fail_easy);
                     context.pushNull();
                     context.setEnding(ending);
                 }
                 break;
             case SUPERNODE:
                 {
                     Object fail = context.getNewEnding();
                     Object fail2 = context.getNewEnding();
                     Object fail_easy = context.getNewEnding();
                     Object ending = context.getNewEnding();
 
                     context.getFrameName(); //[String]
                     context.duplicateCurrentValue(); //[String, String]
                     context.ifNull(fail); //[String]
                     context.getFrameKlazz(); //[String, RubyClass]
                     context.duplicateCurrentValue(); //[String, RubyClass, RubyClass]
                     context.ifNull(fail2); //[String, RubyClass]
                     context.superClass();
                     context.ifNotSuperMethodBound(fail_easy);
 
                     compileGetArgumentDefinition(((SuperNode) node).getArgsNode(), context, "super");
                     context.go(ending);
 
                     context.setEnding(fail2);
                     context.consumeCurrentValue();
                     context.setEnding(fail);
                     context.consumeCurrentValue();
                     context.setEnding(fail_easy);
                     context.pushNull();
                     context.setEnding(ending);
                     break;
                 }
             case ATTRASSIGNNODE:
                 {
                     final AttrAssignNode iVisited = (AttrAssignNode) node;
                     Object isnull = context.getNewEnding();
                     Object ending = context.getNewEnding();
                     compileGetDefinition(iVisited.getReceiverNode(), context);
                     context.ifNull(isnull);
 
                     context.rescue(new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     compile(iVisited.getReceiverNode(), context,true); //[IRubyObject]
                                     context.duplicateCurrentValue(); //[IRubyObject, IRubyObject]
                                     context.metaclass(); //[IRubyObject, RubyClass]
                                     context.duplicateCurrentValue(); //[IRubyObject, RubyClass, RubyClass]
                                     context.getVisibilityFor(iVisited.getName()); //[IRubyObject, RubyClass, Visibility]
                                     context.duplicateCurrentValue(); //[IRubyObject, RubyClass, Visibility, Visibility]
                                     final Object isfalse = context.getNewEnding();
                                     Object isreal = context.getNewEnding();
                                     Object ending = context.getNewEnding();
                                     context.isPrivate(isfalse, 3); //[IRubyObject, RubyClass, Visibility]
                                     context.isNotProtected(isreal, 1); //[IRubyObject, RubyClass]
                                     context.selfIsKindOf(isreal); //[IRubyObject]
                                     context.consumeCurrentValue();
                                     context.go(isfalse);
                                     context.setEnding(isreal); //[]
 
                                     context.isMethodBound(iVisited.getName(), new BranchCallback() {
 
                                                 public void branch(BodyCompiler context) {
                                                     compileGetArgumentDefinition(iVisited.getArgsNode(), context, "assignment");
                                                 }
                                             },
                                             new BranchCallback() {
 
                                                 public void branch(BodyCompiler context) {
                                                     context.go(isfalse);
                                                 }
                                             });
                                     context.go(ending);
                                     context.setEnding(isfalse);
                                     context.pushNull();
                                     context.setEnding(ending);
                                 }
                             }, JumpException.class,
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.pushNull();
                                 }
                             }, ByteList.class);
 
                     context.go(ending);
                     context.setEnding(isnull);
                     context.pushNull();
                     context.setEnding(ending);
                     break;
                 }
             default:
                 context.rescue(new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 compile(node, context,true);
                                 context.consumeCurrentValue();
                                 context.pushNull();
                             }
                         }, JumpException.class,
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         }, ByteList.class);
                 context.consumeCurrentValue();
                 context.pushByteList(Node.EXPRESSION_BYTELIST);
         }
     }
 
     protected void compileDefinedAndOrDStrDRegexp(final Node node, BodyCompiler context) {
         context.rescue(new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         compile(node, context, false);
                         context.pushByteList(Node.EXPRESSION_BYTELIST);
                     }
                 }, JumpException.class,
                 new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.pushNull();
                     }
                 }, ByteList.class);
     }
 
     protected void compileDefinedCall(final Node node, BodyCompiler context) {
             final CallNode iVisited = (CallNode) node;
             Object isnull = context.getNewEnding();
             Object ending = context.getNewEnding();
             compileGetDefinition(iVisited.getReceiverNode(), context);
             context.ifNull(isnull);
 
             context.rescue(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(iVisited.getReceiverNode(), context, true); //[IRubyObject]
                             context.definedCall(iVisited.getName());
                         }
                     }, JumpException.class,
                     new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.pushNull();
                         }
                     }, ByteList.class);
 
             //          context.swapValues();
     //context.consumeCurrentValue();
             context.go(ending);
             context.setEnding(isnull);
             context.pushNull();
             context.setEnding(ending);
     }
 
     protected void compileDefinedDVar(final Node node, BodyCompiler context) {
         context.pushByteList(Node.LOCAL_VARIABLE_IN_BLOCK_BYTELIST);
     }
 
     protected void compileDefinedBackref(final Node node, BodyCompiler context) {
         context.backref();
         context.isInstanceOf(RubyMatchData.class,
                 new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.pushByteList(ByteList.create("$" + ((BackRefNode) node).getType()));
                     }
                 },
                 new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.pushNull();
                     }
                 });
     }
 
     protected void compileDefinedNthref(final Node node, BodyCompiler context) {
         context.isCaptured(((NthRefNode) node).getMatchNumber(),
                 new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.pushByteList(ByteList.create("$" + ((NthRefNode) node).getMatchNumber()));
                     }
                 },
                 new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.pushNull();
                     }
                 });
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
 
     private Node currentBodyNode;
 
     public void compileDefn(Node node, BodyCompiler context, boolean expr) {
         final DefnNode defnNode = (DefnNode) node;
         final ArgsNode argsNode = defnNode.getArgsNode();
 
         CompilerCallback body = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (defnNode.getBodyNode() != null) {
                             Node oldBodyNode = currentBodyNode;
                             currentBodyNode = defnNode.getBodyNode();
                             if (defnNode.getBodyNode() instanceof RescueNode) {
                                 // if root of method is rescue, compile as a light rescue
                                 compileRescueInternal(defnNode.getBodyNode(), context, true);
                             } else {
                                 compile(defnNode.getBodyNode(), context, true);
                             }
                             currentBodyNode = oldBodyNode;
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
 
         context.defineNewMethod(
                 defnNode.getName(), defnNode.getArgsNode().getArity().getValue(),
                 defnNode.getScope(), body, args, null, inspector, isAtRoot,
                 defnNode.getPosition().getFile(), defnNode.getPosition().getStartLine(),
                 RuntimeHelpers.encodeParameterList(argsNode));
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
 
         context.defineNewMethod(
                 defsNode.getName(), defsNode.getArgsNode().getArity().getValue(),
                 defsNode.getScope(), body, args, receiver, inspector, false,
                 defsNode.getPosition().getFile(), defsNode.getPosition().getStartLine(),
                 RuntimeHelpers.encodeParameterList(argsNode));
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
 
                             compileAssignment(optArg, context, false);
                         }
                     };
             optionalNotGiven = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object object, int index) {
                             Node optArg = ((ListNode) object).get(index);
 
                             compile(optArg, context, false);
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
 
         if (expr) {
             CompilerCallback beginEndCallback = new CompilerCallback() {
                 public void call(BodyCompiler context) {
                     compile(dotNode.getBeginNode(), context, true);
                     compile(dotNode.getEndNode(), context, true);
                 }
             };
 
             context.createNewRange(beginEndCallback, dotNode.isExclusive());
         }
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
 
         if (expr) {
             context.createNewRegexp(createStringCallback, dregexpNode.getOptions().toEmbeddedOptions());
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
 
         if (expr) {
             context.createNewString(dstrCallback, dstrNode.size());
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
 
         if (expr) {
             context.createNewSymbol(dstrCallback, dsymbolNode.size());
         } else {
             // not an expression, only compile the elements
             for (Node nextNode : dsymbolNode.childNodes()) {
                 compile(nextNode, context, false);
             }
         }
     }
 
     public void compileDVar(Node node, BodyCompiler context, boolean expr) {
         DVarNode dvarNode = (DVarNode) node;
 
         if (expr) context.getVariableCompiler().retrieveLocalVariable(dvarNode.getIndex(), dvarNode.getDepth());
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
         if (expr) {
             context.loadFalse();
             context.pollThreadEvents();
         }
     }
 
     public void compileFCall(Node node, BodyCompiler context, boolean expr) {
         final FCallNode fcallNode = (FCallNode) node;
 
         ArgumentsCallback argsCallback = getArgsCallback(fcallNode.getArgsNode());
         
         CompilerCallback closureArg = getBlock(fcallNode.getIterNode());
 
         DYNOPT: if (RubyInstanceConfig.DYNOPT_COMPILE_ENABLED) {
             // dynopt does not handle non-local block flow control yet, so we bail out
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index 8b7ab417c7..5a0eed472e 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -237,2001 +237,2001 @@ public class RuntimeHelpers {
     public static String[][] parseBlockDescriptor(String descriptor) {
         String[] firstSplit = descriptor.split(",");
         String[] secondSplit;
         if (firstSplit[2].length() == 0) {
             secondSplit = new String[0];
         } else {
             secondSplit = firstSplit[2].split(";");
             // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
             for (int i = 0; i < secondSplit.length; i++) {
                 secondSplit[i] = secondSplit[i].intern();
             }
         }
         return new String[][] {firstSplit, secondSplit};
     }
 
     public static BlockBody createCompiledBlockBody(ThreadContext context, Object scriptObject, String descriptor) {
         String[][] splitDesc = parseBlockDescriptor(descriptor);
         String[] firstSplit = splitDesc[0];
         String[] secondSplit = splitDesc[1];
         return createCompiledBlockBody(context, scriptObject, firstSplit[0], Integer.parseInt(firstSplit[1]), secondSplit, Boolean.valueOf(firstSplit[3]), Integer.parseInt(firstSplit[4]), firstSplit[5], Integer.parseInt(firstSplit[6]), Boolean.valueOf(firstSplit[7]));
     }
     
     public static BlockBody createCompiledBlockBody(ThreadContext context, Object scriptObject, String closureMethod, int arity, 
             String[] staticScopeNames, boolean hasMultipleArgsHead, int argsNodeType, String file, int line, boolean light) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         if (light) {
             return CompiledBlockLight.newCompiledBlockLight(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback(scriptObject, closureMethod, file, line),
                     hasMultipleArgsHead, argsNodeType);
         } else {
             return CompiledBlock.newCompiledBlock(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback(scriptObject, closureMethod, file, line),
                     hasMultipleArgsHead, argsNodeType);
         }
     }
 
     public static BlockBody createCompiledBlockBody19(ThreadContext context, Object scriptObject, String descriptor) {
         String[][] splitDesc = parseBlockDescriptor(descriptor);
         String[] firstSplit = splitDesc[0];
         String[] secondSplit = splitDesc[1];
         return createCompiledBlockBody19(context, scriptObject, firstSplit[0], Integer.parseInt(firstSplit[1]), secondSplit, Boolean.valueOf(firstSplit[3]), Integer.parseInt(firstSplit[4]), firstSplit[5], Integer.parseInt(firstSplit[6]), Boolean.valueOf(firstSplit[7]), firstSplit[8]);
     }
 
     public static BlockBody createCompiledBlockBody19(ThreadContext context, Object scriptObject, String closureMethod, int arity,
             String[] staticScopeNames, boolean hasMultipleArgsHead, int argsNodeType, String file, int line, boolean light, String parameterList) {
         StaticScope staticScope =
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
 
         if (light) {
             return CompiledBlockLight19.newCompiledBlockLight(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback19(scriptObject, closureMethod, file, line),
                     hasMultipleArgsHead, argsNodeType, parameterList.split(";"));
         } else {
             return CompiledBlock19.newCompiledBlock(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback19(scriptObject, closureMethod, file, line),
                     hasMultipleArgsHead, argsNodeType, parameterList.split(";"));
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
     
     public static IRubyObject runBeginBlock(ThreadContext context, IRubyObject self, String scopeString, CompiledBlockCallback callback) {
         StaticScope staticScope = decodeBlockScope(context, scopeString);
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
     
     public static IRubyObject def(ThreadContext context, IRubyObject self, Object scriptObject, String name, String javaName, String scopeString,
             int arity, String filename, int line, CallConfiguration callConfig, String parameterDesc) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
         
         RubyModule containingClass = context.getRubyClass();
         Visibility visibility = context.getCurrentVisibility();
         
         performNormalMethodChecks(containingClass, runtime, name);
 
         StaticScope scope = createScopeForClass(context, scopeString);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method = constructNormalMethod(
                 factory, javaName,
                 name, containingClass, new SimpleSourcePosition(filename, line), arity, scope, visibility, scriptObject,
                 callConfig,
                 parameterDesc);
         
         addInstanceMethod(containingClass, name, method, visibility,context, runtime);
         
         return runtime.getNil();
     }
     
     public static IRubyObject defs(ThreadContext context, IRubyObject self, IRubyObject receiver, Object scriptObject, String name, String javaName, String scopeString,
             int arity, String filename, int line, CallConfiguration callConfig, String parameterDesc) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
 
         RubyClass rubyClass = performSingletonMethodChecks(runtime, receiver, name);
         
         StaticScope scope = createScopeForClass(context, scopeString);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method = constructSingletonMethod(
                 factory, javaName, rubyClass,
                 new SimpleSourcePosition(filename, line), arity, scope,
                 scriptObject, callConfig, parameterDesc);
         
         rubyClass.addMethod(name, method);
         
         callSingletonMethodHook(receiver,context, runtime.fastNewSymbol(name));
         
         return runtime.getNil();
     }
 
     public static byte[] defOffline(String name, String classPath, String invokerName, Arity arity, StaticScope scope, CallConfiguration callConfig, String filename, int line) {
         MethodFactory factory = MethodFactory.createFactory(RuntimeHelpers.class.getClassLoader());
         byte[] methodBytes = factory.getCompiledMethodOffline(name, classPath, invokerName, arity, scope, callConfig, filename, line);
 
         return methodBytes;
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
 
         if (name.equals("method_missing")) {
             return RubyKernel.method_missing(context, receiver, args, Block.NULL_BLOCK);
         }
 
         IRubyObject[] newArgs = prepareMethodMissingArgs(args, context, name);
 
         return invoke(context, receiver, "method_missing", newArgs, Block.NULL_BLOCK);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject[] args, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, args, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject arg0, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, arg0, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject arg0, IRubyObject arg1, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, arg0, arg1, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, arg0, arg1, arg2, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, block);
     }
 
     public static DynamicMethod selectMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType) {
         Ruby runtime = context.getRuntime();
 
         if (name.equals("method_missing")) {
             return selectInternalMM(runtime, visibility, callType);
         }
 
         DynamicMethod methodMissing = receiver.getMetaClass().searchMethod("method_missing");
         if (methodMissing.isUndefined() || methodMissing == runtime.getDefaultMethodMissing()) {
             return selectInternalMM(runtime, visibility, callType);
         }
         return new MethodMissingMethod(methodMissing, callType);
     }
 
     public static DynamicMethod selectMethodMissing(ThreadContext context, RubyClass selfClass, Visibility visibility, String name, CallType callType) {
         Ruby runtime = context.getRuntime();
 
         if (name.equals("method_missing")) {
             return selectInternalMM(runtime, visibility, callType);
         }
 
         DynamicMethod methodMissing = selfClass.searchMethod("method_missing");
         if (methodMissing.isUndefined() || methodMissing == runtime.getDefaultMethodMissing()) {
             return selectInternalMM(runtime, visibility, callType);
         }
         return new MethodMissingMethod(methodMissing, callType);
     }
 
     public static DynamicMethod selectMethodMissing(RubyClass selfClass, Visibility visibility, String name, CallType callType) {
         Ruby runtime = selfClass.getClassRuntime();
 
         if (name.equals("method_missing")) {
             return selectInternalMM(runtime, visibility, callType);
         }
 
         DynamicMethod methodMissing = selfClass.searchMethod("method_missing");
         if (methodMissing.isUndefined() || methodMissing == runtime.getDefaultMethodMissing()) {
             return selectInternalMM(runtime, visibility, callType);
         }
         return new MethodMissingMethod(methodMissing, callType);
     }
 
     private static class MethodMissingMethod extends DynamicMethod {
         private final DynamicMethod delegate;
         private final CallType lastCallStatus;
 
         public MethodMissingMethod(DynamicMethod delegate, CallType lastCallStatus) {
             this.delegate = delegate;
             this.lastCallStatus = lastCallStatus;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             context.setLastCallStatus(lastCallStatus);
             return this.delegate.call(context, self, clazz, "method_missing", prepareMethodMissingArgs(args, context, name), block);
         }
 
         @Override
         public DynamicMethod dup() {
             return this;
         }
     }
 
     private static DynamicMethod selectInternalMM(Ruby runtime, Visibility visibility, CallType callType) {
         if (visibility == Visibility.PRIVATE) {
             return runtime.getPrivateMethodMissing();
         } else if (visibility == Visibility.PROTECTED) {
             return runtime.getProtectedMethodMissing();
         } else if (callType == CallType.VARIABLE) {
             return runtime.getVariableMethodMissing();
         } else if (callType == CallType.SUPER) {
             return runtime.getSuperMethodMissing();
         } else {
             return runtime.getNormalMethodMissing();
         }
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
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject... args) {
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
 
     public static IRubyObject invokeChecked(ThreadContext context, IRubyObject self, String name) {
         return self.getMetaClass().finvokeChecked(context, self, name);
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
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, args, block);
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
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, block);
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
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, arg0, block);
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
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, arg0, arg1, block);
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
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, arg0, arg1, arg2, block);
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
         if (rubyModule == null || rubyModule.isNil()) {
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
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + required + ")");
                 }
             } else {
                 // only rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + required + ")");
                 }
             }
         } else {
             if (rest < 0) {
                 // opt but no rest, must be at least required and no more than required + opt
                 if (given < required) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + required + ")");
                 } else if (given > (required + opt)) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + (required + opt) + ")");
                 }
             } else {
                 // opt and rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + required + ")");
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
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         if(backref instanceof RubyMatchData) {
             ((RubyMatchData)backref).use();
         }
         return backref;
     }
     
     public static IRubyObject backrefLastMatch(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         return RubyRegexp.last_match(backref);
     }
     
     public static IRubyObject backrefMatchPre(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         return RubyRegexp.match_pre(backref);
     }
     
     public static IRubyObject backrefMatchPost(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         return RubyRegexp.match_post(backref);
     }
     
     public static IRubyObject backrefMatchLast(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         return RubyRegexp.match_last(backref);
     }
 
     public static IRubyObject[] getArgValues(ThreadContext context) {
         return context.getCurrentScope().getArgValues();
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
     
     public static IRubyObject throwReturnJump(IRubyObject result, ThreadContext context) {
         throw context.returnJump(result);
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
         return isExceptionHandled((IRubyObject)currentException, exception, context);
     }
 
     public static IRubyObject isExceptionHandled(IRubyObject currentException, IRubyObject exception, ThreadContext context) {
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
 
     private static boolean checkJavaException(Throwable throwable, IRubyObject catchable, ThreadContext context) {
         if (context.getRuntime().getException().op_ge(catchable).isTrue() ||
                 context.getRuntime().getObject() == catchable) {
             if (throwable instanceof RaiseException) {
                 return isExceptionHandled(((RaiseException)throwable).getException(), catchable, context).isTrue();
             }
             // let Ruby exceptions decide if they handle it
             return isExceptionHandled(JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), throwable), catchable, context).isTrue();
         }
         if (catchable instanceof RubyClass && catchable.getInstanceVariables().hasInstanceVariable("@java_class")) {
             RubyClass rubyClass = (RubyClass)catchable;
             JavaClass javaClass = (JavaClass)rubyClass.fastGetInstanceVariable("@java_class");
             if (javaClass != null) {
                 Class cls = javaClass.javaClass();
                 if (cls.isInstance(throwable)) {
                     return true;
                 }
             }
         }
         return false;
     }
     
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject[] throwables, ThreadContext context) {
         if (currentThrowable instanceof Unrescuable) {
             UnsafeFactory.getUnsafe().throwException(currentThrowable);
         }
 
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwables, context);
         } else {
             for (int i = 0; i < throwables.length; i++) {
                 if (checkJavaException(currentThrowable, throwables[0], context)) {
                     return context.getRuntime().getTrue();
                 }
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable, ThreadContext context) {
         if (currentThrowable instanceof Unrescuable) {
             UnsafeFactory.getUnsafe().throwException(currentThrowable);
         }
 
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable, context);
         } else {
             if (checkJavaException(currentThrowable, throwable, context)) {
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable0, IRubyObject throwable1, ThreadContext context) {
         if (currentThrowable instanceof Unrescuable) {
             UnsafeFactory.getUnsafe().throwException(currentThrowable);
         }
 
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable0, throwable1, context);
         } else {
             if (checkJavaException(currentThrowable, throwable0, context)) {
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentThrowable, throwable1, context)) {
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable0, IRubyObject throwable1, IRubyObject throwable2, ThreadContext context) {
         if (currentThrowable instanceof Unrescuable) {
             UnsafeFactory.getUnsafe().throwException(currentThrowable);
         }
         
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable0, throwable1, throwable2, context);
         } else {
             if (checkJavaException(currentThrowable, throwable0, context)) {
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentThrowable, throwable1, context)) {
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentThrowable, throwable2, context)) {
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static void storeExceptionInErrorInfo(Throwable currentThrowable, ThreadContext context) {
         IRubyObject exception = null;
         if (currentThrowable instanceof RaiseException) {
             exception = ((RaiseException)currentThrowable).getException();
         } else {
             exception = JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), currentThrowable);
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
 
     public static RubyArray createSubarray(RubyArray input, int start, int post) {
         return (RubyArray)input.subseqLight(start, input.size() - post - start);
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
 
     public static IRubyObject optElementOrNull(IRubyObject[] input, int element, int postCount) {
         if (element + postCount >= input.length) {
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
 
     public static IRubyObject postElementOrNil(IRubyObject[] input, int postCount, int postIndex, IRubyObject nil) {
         int aryIndex = input.length - postCount + postIndex;
         if (aryIndex >= input.length || aryIndex < 0) {
             return nil;
         } else {
             return input[aryIndex];
         }
     }
     
     public static RubyBoolean isWhenTriggered(IRubyObject expression, IRubyObject expressionsObject, ThreadContext context) {
         RubyArray expressions = RuntimeHelpers.splatValue(expressionsObject);
         for (int j = 0,k = expressions.getLength(); j < k; j++) {
             IRubyObject condition = expressions.eltInternal(j);
 
             if ((expression != null && condition.callMethod(context, "===", expression).isTrue()) ||
                     (expression == null && condition.isTrue())) {
                 return context.getRuntime().getTrue();
             }
         }
         
         return context.getRuntime().getFalse();
     }
     
-    public static IRubyObject setConstantInModule(IRubyObject module, IRubyObject value, String name, ThreadContext context) {
+    public static IRubyObject setConstantInModule(IRubyObject value, IRubyObject module, String name, ThreadContext context) {
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
     
     public static final int MAX_SPECIFIC_ARITY_OBJECT_ARRAY = 10;
     
     public static IRubyObject[] anewarrayIRubyObjects(int size) {
         return new IRubyObject[size];
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, int start) {
         ary[start] = one;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, int start) {
         ary[start] = one;
         ary[start+1] = two;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         ary[start+6] = seven;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         ary[start+6] = seven;
         ary[start+7] = eight;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         ary[start+6] = seven;
         ary[start+7] = eight;
         ary[start+8] = nine;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine, IRubyObject ten, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         ary[start+6] = seven;
         ary[start+7] = eight;
         ary[start+8] = nine;
         ary[start+9] = ten;
         return ary;
     }
     
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
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six) {
         return new IRubyObject[] {one, two, three, four, five, six};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven) {
         return new IRubyObject[] {one, two, three, four, five, six, seven};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight) {
         return new IRubyObject[] {one, two, three, four, five, six, seven, eight};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine) {
         return new IRubyObject[] {one, two, three, four, five, six, seven, eight, nine};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine, IRubyObject ten) {
         return new IRubyObject[] {one, two, three, four, five, six, seven, eight, nine, ten};
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
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six, seven);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six, seven, eight);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six, seven, eight, nine);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine, IRubyObject ten) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six, seven, eight, nine, ten);
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
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five) {
         return new String[] {one, two, three, four, five};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six) {
         return new String[] {one, two, three, four, five, six};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven) {
         return new String[] {one, two, three, four, five, six, seven};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight) {
         return new String[] {one, two, three, four, five, six, seven, eight};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight, String nine) {
         return new String[] {one, two, three, four, five, six, seven, eight, nine};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight, String nine, String ten) {
         return new String[] {one, two, three, four, five, six, seven, eight, nine, ten};
     }
     
     public static final int MAX_SPECIFIC_ARITY_HASH = 3;
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         hash.fastASetCheckString(runtime, key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         hash.fastASetCheckString(runtime, key2, value2);
         hash.fastASetCheckString(runtime, key3, value3);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         hash.fastASetCheckString19(runtime, key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         hash.fastASetCheckString19(runtime, key2, value2);
         hash.fastASetCheckString19(runtime, key3, value3);
         return hash;
     }
 
     public static IRubyObject undefMethod(ThreadContext context, Object nameArg) {
         RubyModule module = context.getRubyClass();
 
         String name = (nameArg instanceof String) ?
             (String) nameArg : nameArg.toString();
 
         if (module == null) {
             throw context.getRuntime().newTypeError("No class to undef method '" + name + "'.");
         }
 
         module.undef(context, name);
 
         return context.getRuntime().getNil();
     }
     
     public static IRubyObject defineAlias(ThreadContext context, IRubyObject self, Object newNameArg, Object oldNameArg) {
         Ruby runtime = context.getRuntime();
         RubyModule module = context.getRubyClass();
    
         if (module == null || self instanceof RubyFixnum || self instanceof RubySymbol){
             throw runtime.newTypeError("no class to make alias");
         }
 
         String newName = (newNameArg instanceof String) ?
             (String) newNameArg : newNameArg.toString();
         String oldName = (oldNameArg instanceof String) ? 
             (String) oldNameArg : oldNameArg.toString();
 
         module.defineAlias(newName, oldName);
         module.callMethod(context, "method_added", runtime.newSymbol(newName));
    
         return runtime.getNil();
     }
     
     public static IRubyObject negate(IRubyObject value, Ruby runtime) {
         if (value.isTrue()) return runtime.getFalse();
         return runtime.getTrue();
     }
     
     public static IRubyObject stringOrNil(ByteList value, ThreadContext context) {
         if (value == null) return context.nil;
         return RubyString.newStringShared(context.runtime, value);
     }
     
     public static void preLoad(ThreadContext context, String[] varNames) {
         StaticScope staticScope = new LocalStaticScope(null, varNames);
         preLoadCommon(context, staticScope, false);
     }
 
     public static void preLoad(ThreadContext context, String scopeString, boolean wrap) {
         StaticScope staticScope = decodeRootScope(context, scopeString);
         preLoadCommon(context, staticScope, wrap);
     }
 
     private static void preLoadCommon(ThreadContext context, StaticScope staticScope, boolean wrap) {
         RubyClass objectClass = context.getRuntime().getObject();
         IRubyObject topLevel = context.getRuntime().getTopSelf();
         if (wrap) {
             staticScope.setModule(RubyModule.newModule(context.runtime));
         } else {
             staticScope.setModule(objectClass);
         }
         DynamicScope scope = DynamicScope.newDynamicScope(staticScope);
 
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
         context.preNodeEval(objectClass, topLevel);
     }
     
     public static void postLoad(ThreadContext context) {
         context.postNodeEval();
         context.postScopedBody();
     }
     
     public static void registerEndBlock(Block block, Ruby runtime) {
         runtime.pushExitBlock(runtime.newProc(Block.Type.LAMBDA, block));
     }
     
     public static IRubyObject match3(RubyRegexp regexp, IRubyObject value, ThreadContext context) {
         if (value instanceof RubyString) {
             return regexp.op_match(context, value);
         } else {
             return value.callMethod(context, "=~", regexp);
         }
     }
     
     public static IRubyObject getErrorInfo(Ruby runtime) {
         return runtime.getGlobalVariables().get("$!");
     }
     
     public static void setErrorInfo(Ruby runtime, IRubyObject error) {
         runtime.getGlobalVariables().set("$!", error);
     }
 
     public static IRubyObject setLastLine(Ruby runtime, ThreadContext context, IRubyObject value) {
         return context.getCurrentScope().setLastLine(value);
     }
 
     public static IRubyObject getLastLine(Ruby runtime, ThreadContext context) {
         return context.getCurrentScope().getLastLine(runtime);
     }
 
     public static IRubyObject setBackref(Ruby runtime, ThreadContext context, IRubyObject value) {
         if (!value.isNil() && !(value instanceof RubyMatchData)) throw runtime.newTypeError(value, runtime.getMatchData());
         return context.getCurrentScope().setBackRef(value);
     }
 
     public static IRubyObject getBackref(Ruby runtime, ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(runtime);
         if (backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
         return backref;
     }
     
     public static IRubyObject preOpAsgnWithOrAnd(IRubyObject receiver, ThreadContext context, IRubyObject self, CallSite varSite) {
         return varSite.call(context, self, receiver);
     }
     
     public static IRubyObject postOpAsgnWithOrAnd(IRubyObject receiver, IRubyObject value, ThreadContext context, IRubyObject self, CallSite varAsgnSite) {
         varAsgnSite.call(context, self, receiver, value);
         return value;
     }
     
     public static IRubyObject opAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, CallSite varSite, CallSite opSite, CallSite opAsgnSite) {
         IRubyObject var = varSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, arg);
         opAsgnSite.call(context, self, receiver, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, arg, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg1, IRubyObject arg2, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg1, arg2);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, arg1, arg2, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg1, arg2, arg3);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, new IRubyObject[] {arg1, arg2, arg3, result});
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, appendToObjectArray(args, result));
 
         return result;
     }
 
     
     public static IRubyObject opElementAsgnWithOrPartTwoOneArg(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, arg, value);
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoTwoArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, args[0], args[1], value);
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoThreeArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, new IRubyObject[] {args[0], args[1], args[2], value});
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoNArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 0, args.length);
         newArgs[args.length] = value;
         asetSite.call(context, self, receiver, newArgs);
         return value;
     }
 
     public static RubyArray arrayValue(IRubyObject value) {
         Ruby runtime = value.getRuntime();
         return arrayValue(runtime.getCurrentContext(), runtime, value);
     }
 
     public static RubyArray arrayValue(ThreadContext context, Ruby runtime, IRubyObject value) {
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
             // remove this hack too.
 
             if (value.respondsTo("to_a") && value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime.getKernel()) {
                 IRubyObject avalue = value.callMethod(context, "to_a");
                 if (!(avalue instanceof RubyArray)) {
                     if (runtime.is1_9() && avalue.isNil()) {
                         return runtime.newArray(value);
                     } else {
                         throw runtime.newTypeError("`to_a' did not return Array");
                     }
                 }
                 return (RubyArray)avalue;
             } else {
                 return runtime.newArray(value);
             }
         }
         return (RubyArray)tmp;
     }
 
     public static IRubyObject aryToAry(IRubyObject value) {
         if (value instanceof RubyArray) return value;
 
         if (value.respondsTo("to_ary")) {
             return TypeConverter.convertToType(value, value.getRuntime().getArray(), "to_ary", false);
         }
 
         return value.getRuntime().newArray(value);
     }
 
     public static IRubyObject aValueSplat(IRubyObject value) {
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return value.getRuntime().getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first() : array;
     }
 
     public static IRubyObject aValueSplat19(IRubyObject value) {
         if (!(value instanceof RubyArray)) {
             return value.getRuntime().getNil();
         }
 
         return (RubyArray) value;
     }
 
     public static RubyArray splatValue(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newArray(value);
         }
 
         return arrayValue(value);
     }
 
     public static RubyArray splatValue19(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newEmptyArray();
         }
 
         return arrayValue(value);
     }
     
     public static IRubyObject[] splatToArguments(IRubyObject value) {
         Ruby runtime = value.getRuntime();
         
         if (value.isNil()) {
             return runtime.getSingleNilArray();
         }
         
         return splatToArgumentsCommon(runtime, value);
     }
     
     public static IRubyObject[] splatToArguments19(IRubyObject value) {
         Ruby runtime = value.getRuntime();
         
         if (value.isNil()) {
             return IRubyObject.NULL_ARRAY;
         }
         
         return splatToArgumentsCommon(runtime, value);
     }
     
     private static IRubyObject[] splatToArgumentsCommon(Ruby runtime, IRubyObject value) {
         
         if (value.isNil()) {
             return runtime.getSingleNilArray();
         }
         
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             return convertSplatToJavaArray(runtime, value);
         }
         return ((RubyArray)tmp).toJavaArrayMaybeUnsafe();
     }
     
     private static IRubyObject[] convertSplatToJavaArray(Ruby runtime, IRubyObject value) {
         // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
         // remove this hack too.
 
         RubyClass metaClass = value.getMetaClass();
         DynamicMethod method = metaClass.searchMethod("to_a");
         if (method.isUndefined() || method.getImplementationClass() == runtime.getKernel()) {
             return new IRubyObject[] {value};
         }
 
         IRubyObject avalue = method.call(runtime.getCurrentContext(), value, metaClass, "to_a");
         if (!(avalue instanceof RubyArray)) {
             if (runtime.is1_9() && avalue.isNil()) {
                 return new IRubyObject[] {value};
             } else {
                 throw runtime.newTypeError("`to_a' did not return Array");
             }
         }
         return ((RubyArray)avalue).toJavaArray();
     }
     
     public static IRubyObject[] argsCatToArguments(IRubyObject[] args, IRubyObject cat) {
         IRubyObject[] ary = splatToArguments(cat);
         return argsCatToArgumentsCommon(args, ary, cat);
     }
     
     public static IRubyObject[] argsCatToArguments19(IRubyObject[] args, IRubyObject cat) {
         IRubyObject[] ary = splatToArguments19(cat);
         return argsCatToArgumentsCommon(args, ary, cat);
     }
     
     private static IRubyObject[] argsCatToArgumentsCommon(IRubyObject[] args, IRubyObject[] ary, IRubyObject cat) {
         if (ary.length > 0) {
             IRubyObject[] newArgs = new IRubyObject[args.length + ary.length];
             System.arraycopy(args, 0, newArgs, 0, args.length);
             System.arraycopy(ary, 0, newArgs, args.length, ary.length);
             args = newArgs;
         }
         
         return args;
     }
 
     public static void addInstanceMethod(RubyModule containingClass, String name, DynamicMethod method, Visibility visibility, ThreadContext context, Ruby runtime) {
         containingClass.addMethod(name, method);
 
         RubySymbol sym = runtime.fastNewSymbol(name);
         if (visibility == Visibility.MODULE_FUNCTION) {
             addModuleMethod(containingClass, name, method, context, sym);
         }
 
         callNormalMethodHook(containingClass, context, sym);
     }
 
     private static void addModuleMethod(RubyModule containingClass, String name, DynamicMethod method, ThreadContext context, RubySymbol sym) {
         containingClass.getSingletonClass().addMethod(name, new WrapperMethod(containingClass.getSingletonClass(), method, Visibility.PUBLIC));
         containingClass.callMethod(context, "singleton_method_added", sym);
     }
 
     private static void callNormalMethodHook(RubyModule containingClass, ThreadContext context, RubySymbol name) {
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             callSingletonMethodHook(((MetaClass) containingClass).getAttached(), context, name);
         } else {
             containingClass.callMethod(context, "method_added", name);
         }
     }
 
     private static void callSingletonMethodHook(IRubyObject receiver, ThreadContext context, RubySymbol name) {
         receiver.callMethod(context, "singleton_method_added", name);
     }
 
     private static DynamicMethod constructNormalMethod(
             MethodFactory factory,
             String javaName,
             String name,
             RubyModule containingClass,
             ISourcePosition position,
             int arity,
             StaticScope scope,
             Visibility visibility,
             Object scriptObject,
             CallConfiguration callConfig,
             String parameterDesc) {
         
         DynamicMethod method;
 
         if (name.equals("initialize") || name.equals("initialize_copy") || visibility == Visibility.MODULE_FUNCTION) {
             visibility = Visibility.PRIVATE;
         }
         
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
             method = factory.getCompiledMethodLazily(
                     containingClass,
                     javaName,
                     Arity.createArity(arity),
                     visibility,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         } else {
             method = factory.getCompiledMethod(
                     containingClass,
                     javaName,
                     Arity.createArity(arity),
                     visibility,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         }
 
         return method;
     }
 
     private static DynamicMethod constructSingletonMethod(
             MethodFactory factory,
             String javaName,
             RubyClass rubyClass,
             ISourcePosition position,
             int arity,
             StaticScope scope,
             Object scriptObject,
             CallConfiguration callConfig,
             String parameterDesc) {
         
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
             return factory.getCompiledMethodLazily(
                     rubyClass,
                     javaName,
                     Arity.createArity(arity),
                     Visibility.PUBLIC,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         } else {
             return factory.getCompiledMethod(
                     rubyClass,
                     javaName,
                     Arity.createArity(arity),
                     Visibility.PUBLIC,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         }
     }
 
     public static String encodeScope(StaticScope scope) {
         StringBuilder namesBuilder = new StringBuilder();
 
         boolean first = true;
         for (String name : scope.getVariables()) {
             if (!first) namesBuilder.append(';');
             first = false;
             namesBuilder.append(name);
         }
         namesBuilder
                 .append(',')
                 .append(scope.getRequiredArgs())
                 .append(',')
                 .append(scope.getOptionalArgs())
                 .append(',')
                 .append(scope.getRestArg());
 
         return namesBuilder.toString();
     }
 
     public static LocalStaticScope decodeRootScope(ThreadContext context, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         LocalStaticScope scope = new LocalStaticScope(null, decodedScope[1]);
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     public static LocalStaticScope decodeLocalScope(ThreadContext context, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         LocalStaticScope scope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), decodedScope[1]);
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     public static BlockStaticScope decodeBlockScope(ThreadContext context, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         BlockStaticScope scope = new BlockStaticScope(context.getCurrentScope().getStaticScope(), decodedScope[1]);
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     private static String[][] decodeScopeDescriptor(String scopeString) {
         String[] scopeElements = scopeString.split(",");
         String[] scopeNames = scopeElements[0].length() == 0 ? new String[0] : getScopeNames(scopeElements[0]);
         return new String[][] {scopeElements, scopeNames};
     }
 
     private static void setAritiesFromDecodedScope(StaticScope scope, String[] scopeElements) {
         scope.setArities(Integer.parseInt(scopeElements[1]), Integer.parseInt(scopeElements[2]), Integer.parseInt(scopeElements[3]));
     }
 
     private static StaticScope createScopeForClass(ThreadContext context, String scopeString) {
         StaticScope scope = decodeLocalScope(context, scopeString);
         scope.determineModule();
 
         return scope;
     }
 
     private static void performNormalMethodChecks(RubyModule containingClass, Ruby runtime, String name) throws RaiseException {
 
         if (containingClass == runtime.getDummy()) {
             throw runtime.newTypeError("no class/module to add method");
         }
 
         if (containingClass == runtime.getObject() && name.equals("initialize")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining Object#initialize may cause infinite loop");
         }
 
         if (name.equals("__id__") || name.equals("__send__")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining `" + name + "' may cause serious problem");
         }
     }
 
     private static RubyClass performSingletonMethodChecks(Ruby runtime, IRubyObject receiver, String name) throws RaiseException {
 
         if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
             throw runtime.newSecurityError("Insecure; can't define singleton method.");
         }
 
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError("can't define singleton method \"" + name + "\" for " + receiver.getMetaClass().getBaseName());
         }
 
         if (receiver.isFrozen()) {
             throw runtime.newFrozenError("object");
         }
         
         RubyClass rubyClass = receiver.getSingletonClass();
 
         if (runtime.getSafeLevel() >= 4 && rubyClass.getMethods().get(name) != null) {
             throw runtime.newSecurityError("redefining method prohibited.");
         }
         
         return rubyClass;
     }
     
     public static IRubyObject arrayEntryOrNil(RubyArray array, int index) {
         if (index < array.getLength()) {
             return array.eltInternal(index);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilZero(RubyArray array) {
         if (0 < array.getLength()) {
             return array.eltInternal(0);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilOne(RubyArray array) {
         if (1 < array.getLength()) {
             return array.eltInternal(1);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilTwo(RubyArray array) {
         if (2 < array.getLength()) {
             return array.eltInternal(2);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNil(RubyArray array, int pre, int post, int index) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + index);
         } else if (pre + index < array.getLength()) {
             return array.eltInternal(pre + index);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNilZero(RubyArray array, int pre, int post) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + 0);
         } else if (pre + 0 < array.getLength()) {
             return array.eltInternal(pre + 0);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNilOne(RubyArray array, int pre, int post) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + 1);
         } else if (pre + 1 < array.getLength()) {
             return array.eltInternal(pre + 1);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNilTwo(RubyArray array, int pre, int post) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + 2);
         } else if (pre + 2 < array.getLength()) {
             return array.eltInternal(pre + 2);
         } else {
             return array.getRuntime().getNil();
         }
     }
     
     public static RubyArray subarrayOrEmpty(RubyArray array, Ruby runtime, int index) {
         if (index < array.getLength()) {
             return createSubarray(array, index);
         } else {
             return RubyArray.newEmptyArray(runtime);
         }
     }
 
     public static RubyArray subarrayOrEmpty(RubyArray array, Ruby runtime, int index, int post) {
         if (index + post < array.getLength()) {
             return createSubarray(array, index, post);
         } else {
             return RubyArray.newEmptyArray(runtime);
         }
     }
     
     public static RubyModule checkIsModule(IRubyObject maybeModule) {
         if (maybeModule instanceof RubyModule) return (RubyModule)maybeModule;
         
         throw maybeModule.getRuntime().newTypeError(maybeModule + " is not a class/module");
     }
     
     public static IRubyObject getGlobalVariable(Ruby runtime, String name) {
         return runtime.getGlobalVariables().get(name);
     }
     
     public static IRubyObject setGlobalVariable(IRubyObject value, Ruby runtime, String name) {
         return runtime.getGlobalVariables().set(name, value);
     }
 
     public static IRubyObject getInstanceVariable(IRubyObject self, Ruby runtime, String internedName) {
         IRubyObject result = self.getInstanceVariables().fastGetInstanceVariable(internedName);
         if (result != null) return result;
         if (runtime.isVerbose()) warnAboutUninitializedIvar(runtime, internedName);
         return runtime.getNil();
     }
 
     private static void warnAboutUninitializedIvar(Ruby runtime, String internedName) {
         runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + internedName + " not initialized");
     }
 
     public static IRubyObject setInstanceVariable(IRubyObject value, IRubyObject self, String name) {
         return self.getInstanceVariables().fastSetInstanceVariable(name, value);
     }
 
     public static RubyProc newLiteralLambda(ThreadContext context, Block block, IRubyObject self) {
         return RubyProc.newProc(context.getRuntime(), block, Block.Type.LAMBDA);
     }
 
     public static void fillNil(IRubyObject[]arr, int from, int to, Ruby runtime) {
         IRubyObject nils[] = runtime.getNilPrefilledArray();
         int i;
 
         for (i = from; i + Ruby.NIL_PREFILLED_ARRAY_SIZE < to; i += Ruby.NIL_PREFILLED_ARRAY_SIZE) {
             System.arraycopy(nils, 0, arr, i, Ruby.NIL_PREFILLED_ARRAY_SIZE);
         }
         System.arraycopy(nils, 0, arr, i, to - i);
     }
 
     public static void fillNil(IRubyObject[]arr, Ruby runtime) {
         fillNil(arr, 0, arr.length, runtime);
     }
 
     public static boolean isFastSwitchableString(IRubyObject str) {
         return str instanceof RubyString;
     }
 
     public static boolean isFastSwitchableSingleCharString(IRubyObject str) {
         return str instanceof RubyString && ((RubyString)str).getByteList().length() == 1;
     }
 
     public static int getFastSwitchString(IRubyObject str) {
         ByteList byteList = ((RubyString)str).getByteList();
         return byteList.hashCode();
     }
 
     public static int getFastSwitchSingleCharString(IRubyObject str) {
         ByteList byteList = ((RubyString)str).getByteList();
         return byteList.get(0);
     }
 
     public static boolean isFastSwitchableSymbol(IRubyObject sym) {
         return sym instanceof RubySymbol;
     }
 
     public static boolean isFastSwitchableSingleCharSymbol(IRubyObject sym) {
