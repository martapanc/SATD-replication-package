diff --git a/src/org/jruby/compiler/ir/IR_Builder.java b/src/org/jruby/compiler/ir/IR_Builder.java
index e6a9b176d5..ef0663919a 100644
--- a/src/org/jruby/compiler/ir/IR_Builder.java
+++ b/src/org/jruby/compiler/ir/IR_Builder.java
@@ -1,1716 +1,1602 @@
 package org.jruby.compiler.ir;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.ast.AliasNode;
 import org.jruby.ast.AndNode;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.AttrAssignNode;
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BignumNode;
 import org.jruby.ast.BinaryOperatorNode;
 import org.jruby.ast.BlockNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.ClassVarAsgnNode;
 import org.jruby.ast.ClassVarNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.Colon3Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.ConstNode;
 import org.jruby.ast.DAsgnNode;
 import org.jruby.ast.DStrNode;
 import org.jruby.ast.DVarNode;
 import org.jruby.ast.DefinedNode;
 import org.jruby.ast.DefnNode;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EnsureNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FCallNode;
 import org.jruby.ast.FixnumNode;
 import org.jruby.ast.FloatNode;
 import org.jruby.ast.GlobalAsgnNode;
 import org.jruby.ast.GlobalVarNode;
 import org.jruby.ast.HashNode;
 import org.jruby.ast.IfNode;
 import org.jruby.ast.InstAsgnNode;
 import org.jruby.ast.InstVarNode;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.LocalAsgnNode;
 import org.jruby.ast.LocalVarNode;
 import org.jruby.ast.Match2Node;
 import org.jruby.ast.Match3Node;
 import org.jruby.ast.MatchNode;
 import org.jruby.ast.NewlineNode;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.NthRefNode;
 import org.jruby.ast.OpAsgnOrNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OrNode;
 import org.jruby.ast.RegexpNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.SValueNode;
 import org.jruby.ast.SplatNode;
 import org.jruby.ast.StrNode;
 import org.jruby.ast.SuperNode;
 import org.jruby.ast.SymbolNode;
 import org.jruby.ast.VCallNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.CallType;
 import org.jruby.exceptions.JumpException;
 import org.jruby.RubyMatchData;
 import org.jruby.ast.ArgsCatNode;
 import org.jruby.ast.ArgsPushNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.CaseNode;
 import org.jruby.ast.ClassNode;
 import org.jruby.ast.ClassVarDeclNode;
 import org.jruby.ast.Colon2ConstNode;
 import org.jruby.ast.Colon2MethodNode;
 import org.jruby.ast.DRegexpNode;
 import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.DXStrNode;
 import org.jruby.ast.DefsNode;
 import org.jruby.ast.FileNode;
 import org.jruby.ast.FlipNode;
 import org.jruby.ast.ForNode;
 import org.jruby.ast.ModuleNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.OpElementAsgnNode;
 import org.jruby.ast.PostExeNode;
 import org.jruby.ast.PreExeNode;
 import org.jruby.ast.SClassNode;
 import org.jruby.ast.StarNode;
 import org.jruby.ast.ToAryNode;
 import org.jruby.ast.UndefNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.VAliasNode;
 import org.jruby.ast.WhenNode;
 import org.jruby.ast.WhenOneArgNode;
 import org.jruby.ast.XStrNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.BlockBody;
 
 // This class converts an AST into a bunch of IR instructions
 
 // IR Building Notes
 // -----------------
 //
 // 1. More copy instructions added than necessary
 // ----------------------------------------------
 // Note that in general, there will be lots of a = b kind of copies
 // introduced in the IR because the translation is entirely single-node focused.
 // An example will make this clear
 //
 // RUBY: 
 //     v = @f 
 // will translate to 
 //
 // AST: 
 //     LocalAsgnNode v 
 //       InstrVarNode f 
 // will translate to
 //
 // IR: 
 //     tmp = self.f [ GET_FIELD(tmp,self,f) ]
 //     v = tmp      [ COPY(v, tmp) ]
 //
 // instead of
 //     v = self.f   [ GET_FIELD(v, self, f) ]
 //
 // We could get smarter and pass in the variable into which this expression is going to get evaluated
 // and use that to store the value of the expression (or not build the expression if the variable is null).
 //
 // But, that makes the code more complicated, and in any case, all this will get fixed in a single pass of
 // copy propagation and dead-code elimination.
 //
 // Something to pay attention to and if this extra pass becomes a concern (not convinced that it is yet),
 // this smart can be built in here.  Right now, the goal is to do something simple and straightforward that is going to be correct.
 //
 // 2. Returning null vs Nil.NIL
 // ----------------------------
 // - We should be returning null from the build methods where it is a normal "error" condition
 // - We should be returning Nil.NIL where the actual return value of a build is the ruby nil operand
 //   Look in buildIfNode for an example of this
 
 public class IR_Builder
 {
     public static Node skipOverNewlines(Node n)
     {
         //Equivalent check ..
         //while (n instanceof NewlineNode)
         while (n.getNodeType() == NodeType.NEWLINENODE)
             n = ((NewlineNode)n).getNextNode();
 
         return n;
     }
 
     public Operand build(Node node, IR_Scope m) {
         if (node == null) {
             return null;
         }
         switch (node.getNodeType()) {
             case ALIASNODE: return buildAlias(node, m); // done
             case ANDNODE: return buildAnd(node, m); // done
             case ARGSCATNODE: return buildArgsCat(node, m); // SSS FIXME: What code generates this AST?
             case ARGSPUSHNODE: return buildArgsPush(node, m); // Nothing to do for 1.8
             case ARRAYNODE: return buildArray(node, m); // done
             case ATTRASSIGNNODE: return buildAttrAssign(node, m); // Incomplete
             case BACKREFNODE: return buildBackref(node, m); // done
             case BEGINNODE: return buildBegin(node, m); // done
             case BIGNUMNODE: return buildBignum(node, m); // done
             case BLOCKNODE: return buildBlock(node, m); // done
             case BREAKNODE: return buildBreak(node, m); // done?
             case CALLNODE: return buildCall(node, m); // done
             case CASENODE: return buildCase(node, m);
             case CLASSNODE: return buildClass(node, m); // done
             case CLASSVARNODE: return buildClassVar(node, m); // done
             case CLASSVARASGNNODE: return buildClassVarAsgn(node, m); // done
             case CLASSVARDECLNODE: return buildClassVarDecl(node, m);
             case COLON2NODE: return buildColon2(node, m); // done
             case COLON3NODE: return buildColon3(node, m); // done
             case CONSTDECLNODE: return buildConstDecl(node, m); // done
             case CONSTNODE: return buildConst(node, m); // done
             case DASGNNODE: return buildDAsgn(node, m); // done
             case DEFINEDNODE: return buildDefined(node, m);
             case DEFNNODE: return buildDefn(node, m); // done
             case DEFSNODE: return buildDefs(node, m); // done
             case DOTNODE: return buildDot(node, m); // done
             case DREGEXPNODE: return buildDRegexp(node, m); // done
             case DSTRNODE: return buildDStr(node, m); // done
             case DSYMBOLNODE: return buildDSymbol(node, m); // done
             case DVARNODE: return buildDVar(node, m); // done
             case DXSTRNODE: return buildDXStr(node, m); // done
             case ENSURENODE: return buildEnsureNode(node, m);
             case EVSTRNODE: return buildEvStr(node, m); // done
             case FALSENODE: return buildFalse(node, m); // done
             case FCALLNODE: return buildFCall(node, m); // done
             case FIXNUMNODE: return buildFixnum(node, m); // done
             case FLIPNODE: return buildFlip(node, m); // SSS FIXME: What code generates this AST?
             case FLOATNODE: return buildFloat(node, m); // done
             case FORNODE: return buildFor(node, m); // done
             case GLOBALASGNNODE: return buildGlobalAsgn(node, m); // done
             case GLOBALVARNODE: return buildGlobalVar(node, m); // done
             case HASHNODE: return buildHash(node, m); // done
             case IFNODE: return buildIf(node, m); // done
             case INSTASGNNODE: return buildInstAsgn(node, m); // done
             case INSTVARNODE: return buildInstVar(node, m); // done
             case ITERNODE: return buildIter(node, m); // done
             case LOCALASGNNODE: return buildLocalAsgn(node, m); // done
             case LOCALVARNODE: return buildLocalVar(node, m); // done
             case MATCH2NODE: return buildMatch2(node, m); // done
             case MATCH3NODE: return buildMatch3(node, m); // done
             case MATCHNODE: return buildMatch(node, m); // done
             case MODULENODE: return buildModule(node, m);
             case MULTIPLEASGNNODE: return buildMultipleAsgn(node, m);
             case NEWLINENODE: return buildNewline(node, m); // done
             case NEXTNODE: return buildNext(node, m); // done?
             case NTHREFNODE: return buildNthRef(node, m);
             case NILNODE: return buildNil(node, m); // done
             case NOTNODE: return buildNot(node, m); // done
             case OPASGNANDNODE: return buildOpAsgnAnd(node, m); // done
             case OPASGNNODE: return buildOpAsgn(node, m); // SSS FIXME: What code generates this AST?
             case OPASGNORNODE: return buildOpAsgnOr(node, m); // SSS FIXME: What code generates this AST?
             case OPELEMENTASGNNODE: return buildOpElementAsgn(node, m); // SSS FIXME: What code generates this AST?
             case ORNODE: return buildOr(node, m); // done
             case POSTEXENODE: return buildPostExe(node, m);
             case PREEXENODE: return buildPreExe(node, m);
             case REDONODE: return buildRedo(node, m); // done??
             case REGEXPNODE: return buildRegexp(node, m); // done
             case RESCUEBODYNODE:
                 throw new NotCompilableException("rescue body is handled by rescue compilation at: " + node.getPosition());
             case RESCUENODE: return buildRescue(node, m);
             case RETRYNODE: return buildRetry(node, m); // done??
             case RETURNNODE: return buildReturn(node, m); // done
             case ROOTNODE:
                 throw new NotCompilableException("Use buildRoot(); Root node at: " + node.getPosition());
             case SCLASSNODE: return buildSClass(node, m);
             case SELFNODE: return buildSelf(node, m); // done
             case SPLATNODE: return buildSplat(node, m); // done
             case STRNODE: return buildStr(node, m); // done
             case SUPERNODE: return buildSuper(node, m);
             case SVALUENODE: return buildSValue(node, m);
             case SYMBOLNODE: return buildSymbol(node, m); // done
             case TOARYNODE: return buildToAry(node, m); // done
             case TRUENODE: return buildTrue(node, m); // done
             case UNDEFNODE: return buildUndef(node, m);
             case UNTILNODE: return buildUntil(node, m); // done
             case VALIASNODE: return buildVAlias(node, m);
             case VCALLNODE: return buildVCall(node, m); // done
             case WHILENODE: return buildWhile(node, m); // done
             case WHENNODE: assert false : "When nodes are handled by case node compilation."; break;
             case XSTRNODE: return buildXStr(node, m); // done
             case YIELDNODE: return buildYield(node, m); // done
             case ZARRAYNODE: return buildZArray(node, m);
             case ZSUPERNODE: return buildZSuper(node, m);
             default: throw new NotCompilableException("Unknown node encountered in buildr: " + node);
         }
     }
 
     public void buildArguments(List<Operand> args, Node node, IR_Scope s) {
         switch (node.getNodeType()) {
             case ARGSCATNODE: buildArgsCatArguments(args, node, s, true);
             case ARGSPUSHNODE: buildArgsPushArguments(args, node, s, true);
             case ARRAYNODE: buildArrayArguments(args, node, s, true);
             case SPLATNODE: buildSplatArguments(args, node, s, true);
             default: 
                 Operand retVal = build(node, s, true);
                 s.convertToJavaArray(); 
                 return (retVal == null) ? null : new ArrayList<Operand>(retVal);
         }
     }
     
     public void buildVariableArityArguments(List<Operand> args, Node node, IR_Scope s) {
        buildArguments(args, node, s);
     }
 
     public void buildSpecificArityArguments (List<Operand> args, Node node, IR_Scope s) {
         if (node.getNodeType() == NodeType.ARRAYNODE) {
             ArrayNode arrayNode = (ArrayNode)node;
             if (arrayNode.isLightweight()) {
                 // explode array, it's an internal "args" array
                 for (Node n : arrayNode.childNodes())
                     args.add(build(n, s, true));
             } else {
                 // use array as-is, it's a literal array
                 args.add(build(arrayNode, s, true));
             }
         } else {
             args.add(build(node, s, true));
         }
     }
 
     public List<Operand> setupArgs(Node receiver, Node args, IR_Scope s) {
         if (args == null)
             return null;
 
         // unwrap newline nodes to get their actual type
         args = skipOverNewlines(args);
 
         List<Operand> argsList = new ArrayList<Operand>();
         argList.add(build(receiver, s)); // SSS FIXME: I added this in.  Is this correct?
         buildArgs(argsList, args, s);
 
         return argsList;
     }
 
     public List<Operand> setupArgs(Node args, IR_Scope s) {
         if (args == null)
             return null;
 
         // unwrap newline nodes to get their actual type
         args = skipOverNewlines(args);
 
         List<Operand> argList = new ArrayList<Operand>();
         argList.add(m.getSelf());
         buildArgs(argList, args, s);
 
         return argList;
     }
 
     public void buildArgs(List<Operand> argsList, Node args, IR_Scope s) {
         switch (args.getNodeType()) {
             case ARGSCATNODE:
             case ARGSPUSHNODE:
             case SPLATNODE:
                 buildVariableArityArguments(argsList, args, s);
                 break;
             case ARRAYNODE:
                 ArrayNode arrayNode = (ArrayNode)args;
                 if (arrayNode.size() > 3)
                     buildVariableArityArguments(argsList, arrayNode, s);
                 else if (arrayNode.size() > 0)
                     buildSpecificArityArguments(argsList, arrayNode, s);
                 break;
             default:
                 buildSpecificArityArguments(argsList, arrayNode, s);
                 break;
         }
     }
 
     public Operand buildAssignment(Node node, IR_Scope m) {
         switch (node.getNodeType()) {
             case ATTRASSIGNNODE: 
                 return buildAttrAssignAssignment(node, m);
 // SSS FIXME:
 //
 // There are also differences in variable scoping between 1.8 and 1.9 
 // Ruby 1.8 is the buggy semantics if I understand correctly.
 //
 // The semantics of how this shadows other variables outside the block needs
 // to be figured out during live var analysis.
             case DASGNNODE:
                 DAsgnNode dasgnNode = (DAsgnNode)node;
                 Operand arg = new Variable(node.getName());
                 s.addInstr(new RECV_BLOCK_ARG_Instr(arg, new Constant(dasgnNode.getIndex())));
                 return arg;
             case CLASSVARASGNNODE:
                 return buildClassVarAsgnAssignment(node, m);
             case CLASSVARDECLNODE:
                 return buildClassVarDeclAssignment(node, m);
             case CONSTDECLNODE:
                 return buildConstDeclAssignment(node, m);
             case GLOBALASGNNODE:
                 return buildGlobalAsgnAssignment(node, m);
             case INSTASGNNODE:
                 return buildInstAsgnAssignment(node, m);
             case LOCALASGNNODE:
                 LocalAsgnNode localAsgnNode = (LocalAsgnNode)node;
                 m.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth());
                 break;
             case MULTIPLEASGNNODE:
                 return buildMultipleAsgnAssignment(node, m);
             case ZEROARGNODE:
                 throw new NotCompilableException("Shouldn't get here; zeroarg does not do assignment: " + node);
             default:
                 throw new NotCompilableException("Can't build assignment node: " + node);
         }
     }
 
     public Operand buildAlias(Node node, IR_Scope s) {
         final AliasNode alias = (AliasNode) node;
         Operand[] args = new Operand[] { new MetaObject(s), new MethAddr(alias.getNewName()), new MethAddr(alias.getOldName()) };
         m.addInstr(new RUBY_INTERNALS_CALL_Instr(null, MethAddr.DEFINE_ALIAS, args));
 
             // SSS FIXME: Can this return anything other than nil?
         return Nil.NIL;
     }
 
     // Translate "ret = (a && b)" --> "ret = (a ? b : false)" -->
     // 
     //    v1 = -- build(a) --
     //       OPT: ret can be set to v1, but effectively v1 is false if we take the branch to L.
     //            while this info can be inferred by using attributes, why bother if we can do this?
     //    ret = false   
     //    beq(v1, false, L)
     //    v2 = -- build(b) --
     //    ret = v2
     // L:
     //
     private Operand buildAnd_2(Node node, IR_Scope m)
     {
         Variable ret = m.getNewTmpVariable();
         Label    l   = m.getNewLabel();
         Operand  v1  = build(andNode.getFirstNode(), m);
         m.addInstr(new COPY_Instr(ret, BooleanLiteral.FALSE));
         m.addInstr(new BEQ_Instr(v1, BooleanLiteral.FALSE, l));
         Operand  v2  = build(andNode.getSecondNode(), m);
         m.addInstr(new COPY_Instr(ret, v2);
         m.addInstr(new LABEL_Instr(l));
         return ret;
     }
 
     public Operand buildAnd(Node node, IR_Scope m) {
         final AndNode andNode = (AndNode) node;
 
         if (andNode.getFirstNode().getNodeType().alwaysTrue()) {
             // build first node (and ignore its result) and then second node
             build(andNode.getFirstNode(), m);
             return build(andNode.getSecondNode(), m);
         } else if (andNode.getFirstNode().getNodeType().alwaysFalse()) {
             // build first node only and return false
             build(andNode.getFirstNode(), m);
             return BooleanLiteral.FALSE;
         } else {
             return buildAnd_2(andNode, m);
         }
     }
 
     public Operand buildArray(Node node, IR_Scope m) {
         List<Operand> elts = new ArrayList<Operand>();
         for (Node e: node.childNodes())
             elts.add(build(e, m));
 
         return new Array(elts);
     }
 
     public Operand buildArgsCat(Node node, IR_Scope m) {
         ArgsCatNode argsCatNode = (ArgsCatNode) node;
 
             // SSS FIXME: Is this going to be a custom instr/operand (like range, hash, etc.)?
             // Need to understand what generates this AST
         Operand v1 = build(argsCatNode.getFirstNode(), m);
         m.ensureRubyArray();
         Operand v2 = build(argsCatNode.getSecondNode(), m);
         m.splatCurrentValue();
         m.concatArrays();
     }
 
     public Operand buildArgsPush(Node node, IR_Scope m) {
         throw new NotCompilableException("ArgsPush should never be encountered bare in 1.8");
     }
 
     private void buildAttrAssign(Node node, IR_Scope m) {
         final AttrAssignNode attrAssignNode = (AttrAssignNode) node;
         List<Operand> args = setupArgs(attrAssignNode.getArgsNode());
         Operand receiver   = build(attrAssignNode.getReceiverNode(), m);
             // SSS FIXME: What is this?
         m.getInvocationCompiler().invokeAttrAssign(attrAssignNode.getName(), receiverCallback, argsCallback);
     }
 
     public Operand buildAttrAssignAssignment(Node node, IR_Scope m) {
         final AttrAssignNode attrAssignNode = (AttrAssignNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(IR_Scope m) {
                 build(attrAssignNode.getReceiverNode(), m,true);
             }
         };
         ArgumentsCallback argsCallback = setupArgs(attrAssignNode.getArgsNode());
 
         m.getInvocationCompiler().invokeAttrAssignMasgn(attrAssignNode.getName(), receiverCallback, argsCallback);
         // TODO: don't require pop
         if (!expr) m.consumeCurrentValue();
     }
 
     public Operand buildBackref(Node node, IR_Scope m) {
         BackRefNode iVisited = (BackRefNode) node;
 
         m.performBackref(iVisited.getType());
         // TODO: don't require pop
         if (!expr) m.consumeCurrentValue();
     }
 
     public Operand buildBegin(Node node, IR_Scope m) {
         return build(((BeginNode)node).getBodyNode(), m);
     }
 
     public Operand buildBignum(Node node, IR_Scope s) {
         return new Fixnum(((BignumNode)node).getValue());
     }
 
     public Operand buildBlock(Node node, IR_Scope s) {
         Operand retVal = null;
         for (Iterator<Node> iter = ((BlockNode) node).childNodes().iterator(); iter.hasNext();)
             retVal = build(iter.next(), s);
 
            // Value of the last expression in the block 
         return retVal;
     }
 
     public Operand buildBreak(Node node, IR_Scope m) {
         final BreakNode breakNode = (BreakNode) node;
         Operand rv = build(breakNode.getValueNode(), m);
             // If this is not a closure, the break is equivalent to jumping to the loop end label
         m.addInstr((s instanceof IR_Closure) ? new BREAK_Instr(rv) : new JUMP_Instr(s.getCurrentLoop()._loopEndLabel));
 
             // SSS FIXME: Should I be returning the operand constructed here?
         return Nil.NIL;
     }
 
     public Operand buildCall(Node node, IR_Scope s) {
         CallNode callNode = (CallNode) node;
 
         Node          callArgsNode = callNode.getArgsNode();
         Node          receiverNode = callNode.getReceiverNode();
         List<Operand> args         = setupArgs(receiverNode, callArgsNode, s);
         Operand       block        = setupCallClosure(callNode.getIterNode(), s);
         Variable      callResult   = s.getNewTmpVariable();
         IR_Instr      callInstr    = new CALL_Instr(callResult, new MethAddr(callNode.getName()), args.toArray(new Operand[args.size()]), block);
         s.addInstr(callInstr);
         return callResult;
     }
 
     public Operand buildCase(Node node, IR_Scope m) {
         CaseNode caseNode = (CaseNode) node;
 
-        boolean hasCase = caseNode.getCaseNode() != null;
+        // get the incoming case value
+        Operand value = build(caseNode.getCaseNode(), m);
 
-        // aggregate when nodes into a list, unfortunately, this is no
-        List<Node> cases = caseNode.getCases().childNodes();
+        // the CASE instruction
+        Label endLabel = m.getNewLabel();
+        CASE_Instr caseInstr = new CASE_Instr(null, value, endLabel);
+        m.addInstr(caseInstr);
 
-        // last node, either !instanceof WhenNode or null, is the else
-        Node elseNode = caseNode.getElseNode();
+        // lists to aggregate variables and bodies for whens
+        List<Variable> variables = new ArrayList<Variable>();
+        List<Label> labels = new ArrayList<Label>();
 
-        buildWhen(caseNode.getCaseNode(), cases, elseNode, m, hasCase);
-    }
-
-    private FastSwitchType getHomogeneousSwitchType(List<Node> whenNodes) {
-        FastSwitchType foundType = null;
-        Outer: for (Node node : whenNodes) {
-            WhenNode whenNode = (WhenNode)node;
-            if (whenNode.getExpressionNodes() instanceof ArrayNode) {
-                ArrayNode arrayNode = (ArrayNode)whenNode.getExpressionNodes();
+        Map<Label, Node> bodies = new HashMap<Label, Node>();
 
-                for (Node maybeFixnum : arrayNode.childNodes()) {
-                    if (maybeFixnum instanceof FixnumNode) {
-                        FixnumNode fixnumNode = (FixnumNode)maybeFixnum;
-                        long value = fixnumNode.getValue();
-                        if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
-                            if (foundType != null && foundType != FastSwitchType.FIXNUM) return null;
-                            if (foundType == null) foundType = FastSwitchType.FIXNUM;
-                            continue;
-                        } else {
-                            return null;
-                        }
-                    } else {
-                        return null;
-                    }
-                }
-            } else if (whenNode.getExpressionNodes() instanceof FixnumNode) {
-                FixnumNode fixnumNode = (FixnumNode)whenNode.getExpressionNodes();
-                long value = fixnumNode.getValue();
-                if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
-                    if (foundType != null && foundType != FastSwitchType.FIXNUM) return null;
-                    if (foundType == null) foundType = FastSwitchType.FIXNUM;
-                    continue;
-                } else {
-                    return null;
-                }
-            } else if (whenNode.getExpressionNodes() instanceof StrNode) {
-                StrNode strNode = (StrNode)whenNode.getExpressionNodes();
-                if (strNode.getValue().length() == 1) {
-                    if (foundType != null && foundType != FastSwitchType.SINGLE_CHAR_STRING) return null;
-                    if (foundType == null) foundType = FastSwitchType.SINGLE_CHAR_STRING;
+        // build each "when"
+        for (Node aCase : caseNode.getCases().childNodes()) {
+            WhenNode whenNode = (WhenNode)aCase;
+            Label bodyLabel = m.getNewLabel();
 
-                    continue;
-                } else {
-                    if (foundType != null && foundType != FastSwitchType.STRING) return null;
-                    if (foundType == null) foundType = FastSwitchType.STRING;
+            if (whenNode.getExpressionNodes() instanceof ListNode) {
+                // multiple conditions for when
+                for (Node expression : ((ListNode)whenNode.getExpressionNodes()).childNodes()) {
+                    Variable eqqResult = m.getNewVariable();
 
-                    continue;
+                    variables.add(eqqResult);
+                    labels.add(bodyLabel);
+                    
+                    m.addInstr(new EQQ_Instr(eqqResult, build(expression, m), value));
+                    m.addInstr(new BTRUE_Instr(eqqResult, bodyLabel));
                 }
-            } else if (whenNode.getExpressionNodes() instanceof SymbolNode) {
-                SymbolNode symbolNode = (SymbolNode)whenNode.getExpressionNodes();
-                if (symbolNode.getName().length() == 1) {
-                    if (foundType != null && foundType != FastSwitchType.SINGLE_CHAR_SYMBOL) return null;
-                    if (foundType == null) foundType = FastSwitchType.SINGLE_CHAR_SYMBOL;
+            } else {
+                Variable eqqResult = m.getNewVariable();
 
-                    continue;
-                } else {
-                    if (foundType != null && foundType != FastSwitchType.SYMBOL) return null;
-                    if (foundType == null) foundType = FastSwitchType.SYMBOL;
+                variables.add(eqqResult);
+                labels.add(bodyLabel);
 
-                    continue;
-                }
-            } else {
-                return null;
+                m.addInstr(new EQQ_Instr(eqqResult, build(whenNode.getExpressionNodes(), m), value));
+                m.addInstr(new BTRUE_Instr(eqqResult, bodyLabel));
             }
+
+            // add body to map for emitting later
+            bodies.put(bodyLabel, whenNode.getBodyNode());
         }
-        return foundType;
-    }
 
-    public Operand buildWhen(final Node value, List<Node> whenNodes, final Node elseNode, IR_Scope m, final boolean expr, final boolean hasCase) {
-        CompilerCallback caseValue = null;
-        if (value != null) caseValue = new CompilerCallback() {
-            public void call(IR_Scope m) {
-                build(value, m, true);
-                m.addInstr(new THREAD_POLL_Instr());
-            }
-        };
+        // build "else" if it exists
+        if (caseNode.getElseNode() != null) {
+            Label elseLbl = m.getNewLabel();
+            caseInstr.setElse(elseLbl);
 
-        List<ArgumentsCallback> conditionals = new ArrayList<ArgumentsCallback>();
-        List<CompilerCallback> bodies = new ArrayList<CompilerCallback>();
-        Map<CompilerCallback, int[]> switchCases = null;
-        FastSwitchType switchType = getHomogeneousSwitchType(whenNodes);
-        if (switchType != null) {
-            // NOTE: Currently this optimization is limited to the following situations:
-            // * All expressions must be int-ranged literal fixnums
-            // It also still emits the code for the "safe" when logic, which is rather
-            // wasteful (since it essentially doubles each code body). As such it is
-            // normally disabled, but it serves as an example of how this optimization
-            // could be done. Ideally, it should be combined with the when processing
-            // to improve code reuse before it's generally available.
-            switchCases = new HashMap<CompilerCallback, int[]>();
-        }
-        for (Node node : whenNodes) {
-            final WhenNode whenNode = (WhenNode)node;
-            CompilerCallback body = new CompilerCallback() {
-                public void call(IR_Scope m) {
-                    build(whenNode.getBodyNode(), m);
-                }
-            };
-            addConditionalForWhen(whenNode, conditionals, bodies, body);
-            if (switchCases != null) switchCases.put(body, getOptimizedCases(whenNode));
+            bodies.put(elseLbl, caseNode.getElseNode());
         }
-        
-        CompilerCallback fallback = new CompilerCallback() {
-            public void call(IR_Scope m) {
-                build(elseNode, m);
-            }
-        };
-        
-        m.buildSequencedConditional(caseValue, switchType, switchCases, conditionals, bodies, fallback);
-    }
 
-    private int[] getOptimizedCases(WhenNode whenNode) {
-        if (whenNode.getExpressionNodes() instanceof ArrayNode) {
-            ArrayNode expression = (ArrayNode)whenNode.getExpressionNodes();
-            if (expression.get(expression.size() - 1) instanceof WhenNode) {
-                // splatted when, can't do it yet
-                return null;
-            }
-
-            int[] cases = new int[expression.size()];
-            for (int i = 0; i < cases.length; i++) {
-                switch (expression.get(i).getNodeType()) {
-                case FIXNUMNODE:
-                    cases[i] = (int)((FixnumNode)expression.get(i)).getValue();
-                    break;
-                default:
-                    // can't do it
-                    return null;
-                }
-            }
-            return cases;
-        } else if (whenNode.getExpressionNodes() instanceof FixnumNode) {
-            FixnumNode fixnumNode = (FixnumNode)whenNode.getExpressionNodes();
-            return new int[] {(int)fixnumNode.getValue()};
-        } else if (whenNode.getExpressionNodes() instanceof StrNode) {
-            StrNode strNode = (StrNode)whenNode.getExpressionNodes();
-            if (strNode.getValue().length() == 1) {
-                return new int[] {strNode.getValue().get(0)};
-            } else {
-                return new int[] {strNode.getValue().hashCode()};
-            }
-        } else if (whenNode.getExpressionNodes() instanceof SymbolNode) {
-            SymbolNode symbolNode = (SymbolNode)whenNode.getExpressionNodes();
-            if (symbolNode.getName().length() == 1) {
-                return new int[] {symbolNode.getName().charAt(0)};
-            } else {
-                return new int[] {symbolNode.getName().hashCode()};
-            }
+        // now emit bodies
+        for (Map.Entry<Label, Node> entry : bodies.entrySet()) {
+            m.addInstr(new LABEL_Instr(entry.getKey()));
+            build(entry.getValue(), m);
+            m.addInstr(new JUMP_Instr(endLabel));
         }
-        return null;
-    }
 
-    private void addConditionalForWhen(final WhenNode whenNode, List<ArgumentsCallback> conditionals, List<CompilerCallback> bodies, CompilerCallback body) {
-        bodies.add(body);
+        // close it out
+        m.addInstr(new LABEL_Instr(endLabel));
+        caseInstr.setLabels(labels);
+        caseInstr.setVariables(variables);
 
-        // If it's a single-arg when but contains an array, we know it's a real literal array
-        // FIXME: This is a gross way to figure it out; parser help similar to yield argument passing (expandArguments) would be better
-        if (whenNode.getExpressionNodes() instanceof ArrayNode) {
-            if (whenNode instanceof WhenOneArgNode) {
-                // one arg but it's an array, treat it as a proper array
-                conditionals.add(new ArgumentsCallback() {
-                    public int getArity() {
-                        return 1;
-                    }
+        // CON FIXME: I don't know how to make case be an expression...does that
+        // logic need to go here?
 
-                    public void call(IR_Scope m) {
-                        build(whenNode.getExpressionNodes(), m, true);
-                    }
-                });
-                return;
-            }
-        }
-        // otherwise, use normal args buildr
-        conditionals.add(setupArgs(whenNode.getExpressionNodes()));
+        return caseInstr;
     }
 
     public Operand buildClass(Node node, IR_Scope s) {
         final ClassNode classNode = (ClassNode) node;
         final Node superNode = classNode.getSuperNode();
         final Node cpathNode = classNode.getCPath();
 
         Operand superClass;
         if (superNode != null)
             superClass = build(superNode, s);
 
             // By default, the container for this class is 's'
         Operand container = null;
 
             // Do we have a dynamic container?
         if (cpathNode instanceof Colon2Node) {
             Node leftNode = ((Colon2Node) cpathNode).getLeftNode();
             if (leftNode != null)
                 container = build(leftNode, m);
         } else if (cpathNode instanceof Colon3Node) {
             container = ___  // m.loadObject(); We need a constant reference to the Object class meta-object!
         }
 
             // Build a new class and add it to the current scope (could be a script / module / class)
         String   className = cpathNode.getName();
         IR_Class c;
         Operand  cMetaObj;
         if (container == null) {
             c = new IR_Class(s, superClass, className);
             cMetaObj = new MetaObject(c);
             s.addClass(c);
         }
         else {
             c = new IR_Class(container, superClass, className);
             cMetaObj = new MetaObject(c);
             s.addInstr(new PUT_CONST_Instr(container, className, cMetaObj));
         }
 
             // Build the class body!
         if (classNode.getBodyNode() != null)
             build(classNode.getBodyNode(), c);
 
             // Return a meta object corresponding to the class
         return cMetaObj;
     }
 
     public Operand buildSClass(Node node, IR_Scope m) {
         final SClassNode sclassNode = (SClassNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
 
                     public void call(IR_Scope m) {
                         build(sclassNode.getReceiverNode(), m, true);
                     }
                 };
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(IR_Scope m) {
                         if (sclassNode.getBodyNode() != null) {
                             build(sclassNode.getBodyNode(), m, true);
                         } else {
                             m.loadNil();
                         }
                     }
                 };
 
 
         m.defineClass("SCLASS", sclassNode.getScope(), null, null, bodyCallback, receiverCallback, inspector);
         // TODO: don't require pop
         if (!expr) m.consumeCurrentValue();
     }
 
     public Operand buildClassVar(Node node, IR_Scope s) {
         Variable ret = s.getNewTmpVariable();
             // SSS FIXME: Is this right?  What if 's' is not a class??  Can that happen?
         s.addInstr(new GET_FIELD_Instr(ret, new MetaObject(s), ((ClassVarNode)node).getName()));
         return ret;
     }
 
     public Operand buildClassVarAsgn(Node node, IR_Scope s) {
         final ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
         Operand val = build(classVarAsgnNode.getValueNode(), s);
         s.addInstr(new PUT_FIELD_Instr(new MetaObject(s), ((ClassVarNode)node).getName(), val));
         return val;
     }
 
     public Operand buildClassVarAsgnAssignment(Node node, IR_Scope m) {
         ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
 
         m.assignClassVariable(classVarAsgnNode.getName());
         // TODO: don't require pop
         if (!expr) m.consumeCurrentValue();
     }
 
     public Operand buildClassVarDecl(Node node, IR_Scope m) {
         final ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(IR_Scope m) {
                 build(classVarDeclNode.getValueNode(), m, true);
             }
         };
         
         m.declareClassVariable(classVarDeclNode.getName(), value);
         // TODO: don't require pop
         if (!expr) m.consumeCurrentValue();
     }
 
     public Operand buildClassVarDeclAssignment(Node node, IR_Scope m) {
         ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
 
         m.declareClassVariable(classVarDeclNode.getName());
         // TODO: don't require pop
         if (!expr) m.consumeCurrentValue();
     }
 
     public Operand buildConstDecl(Node node, IR_Scope s) {
         Operand       val;
         ConstDeclNode constDeclNode = (ConstDeclNode) node;
         Node          constNode     = constDeclNode.getConstNode();
 
         if (constNode == null) {
             val = build(constDeclNode.getValueNode(), s);
             s.setConstantValue(constDeclNode.getName(), val);
         } else if (constNode.getNodeType() == NodeType.COLON2NODE) {
             Operand module = build(((Colon2Node) constNode).getLeftNode(), s);
             val = build(constDeclNode.getValueNode(), s);
             s.addInstr(new PUT_CONST_Instr(module, constDeclNode.getName(), val);
         } else { // colon3, assign in Object
             val = build(constDeclNode.getValueNode(), s);
             s.addInstr(new PUT_CONST_Instr(s.getSelf(), constDeclNode.getName(), val));
         }
 
         return val;
     }
 
     public Operand buildConstDeclAssignment(Node node, IR_Scope m) {
         // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
         ConstDeclNode constDeclNode = (ConstDeclNode) node;
         Node constNode = constDeclNode.getConstNode();
 
         if (constNode == null) {
             m.assignConstantInCurrent(constDeclNode.getName());
         } else if (constNode.getNodeType() == NodeType.COLON2NODE) {
             build(((Colon2Node) constNode).getLeftNode(), m,true);
             m.swapValues();
             m.assignConstantInModule(constDeclNode.getName());
         } else {// colon3, assign in Object
             m.assignConstantInObject(constDeclNode.getName());
         }
         // TODO: don't require pop
         if (!expr) m.consumeCurrentValue();
     }
 
     public Operand buildConst(Node node, IR_Scope s) {
         return s.getConstantValue(((ConstNode)node).getName()); 
     }
 
     public Operand buildColon2(Node node, IR_Scope s) {
         final Colon2Node iVisited = (Colon2Node) node;
         Node leftNode = iVisited.getLeftNode();
         final String name = iVisited.getName();
 
         if (leftNode == null) {
             return s.getConstantValue(name);
         } 
         else if (node instanceof Colon2ConstNode) {
             // 1. Load the module first (lhs of node)
             // 2. Then load the constant from the module
             Operand module = build(iVisited.getLeftNode(), s);
             if (module instanceof MetaObject) {
                 return ((MetaObject)module)._scope.getConstantValue(name);
             }
             else {
                 constVal = s.getNewTmpVariable();
                 s.addInstr(new GET_CONST_Instr(constVal, module, name));
                 return constVal;
             }
         } 
         else if (node instanceof Colon2MethodNode) {
             // SSS FIXME: What is this??
             final CompilerCallback receiverCallback = new CompilerCallback() {
                 public void call(IR_Scope m) {
                     build(iVisited.getLeftNode(), m,true);
                 }
             };
             
             m.getInvocationCompiler().invokeDynamic(name, receiverCallback, null, CallType.FUNCTIONAL, null, false);
         }
     }
 
     public Operand buildColon3(Node node, IR_Scope s) {
         Operand cv = getNewTmpVariable();
         addInstr(new GET_CONST_Instr(cv, s.getSelf(), node.getName()));
         return cv;
     }
 
     public Operand buildGetDefinitionBase(final Node node, IR_Scope m) {
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
             // these are all simple cases that don't require the heavier defined logic
             buildGetDefinition(node, m);
             break;
         default:
             BranchCallback reg = new BranchCallback() {
 
                         public void branch(IR_Scope m) {
                             m.inDefined();
                             buildGetDefinition(node, m);
                         }
                     };
             BranchCallback out = new BranchCallback() {
 
                         public void branch(IR_Scope m) {
                             m.outDefined();
                         }
                     };
             m.protect(reg, out, String.class);
         }
     }
 
     public Operand buildDefined(final Node node, IR_Scope m) {
         buildGetDefinitionBase(((DefinedNode) node).getExpressionNode(), m);
         m.stringOrNil();
     }
 
     public Operand buildGetArgumentDefinition(final Node node, IR_Scope m, String type) {
         if (node == null) {
             return new StringLiteral(type);
         } else if (node instanceof ArrayNode) {
             Object endToken = m.getNewEnding();
             for (int i = 0; i < ((ArrayNode) node).size(); i++) {
                 Node iterNode = ((ArrayNode) node).get(i);
                 buildGetDefinition(iterNode, m);
                 m.ifNull(endToken);
             }
             Operand sl = new StringLiteral(type);
             Object realToken = m.getNewEnding();
             m.go(realToken);
             m.setEnding(endToken);
             m.pushNull();
             m.setEnding(realToken);
         } else {
             buildGetDefinition(node, m);
             Object endToken = m.getNewEnding();
             m.ifNull(endToken);
             Operand sl = new StringLiteral(type);
             Object realToken = m.getNewEnding();
             m.go(realToken);
             m.setEnding(endToken);
             m.pushNull();
             m.setEnding(realToken);
         }
     }
 
     public Operand buildGetDefinition(final Node node, IR_Scope m) {
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
                 return new StringLiteral("assignment");
             case BACKREFNODE:
                     // SSS FIXME!
                 Operand x = m.backref();
                 return x instanceof RubyMatchData.class ? new StringLiteral("$" + ((BackRefNode) node).getType()) : Nil.NIL;
             case DVARNODE:  
                 return new StringLiteral("local-variable(in-block)");
             case FALSENODE:
                 return new StringLiteral("false");
             case TRUENODE:
                 return new StringLiteral("true");
             case LOCALVARNODE: 
                 return new StringLiteral("local-variable");
             case MATCH2NODE: 
             case MATCH3NODE: 
                 return new StringLiteral("method");
             case NILNODE: 
                 return new StringLiteral("nil");
             case NTHREFNODE:
                 m.isCaptured(((NthRefNode) node).getMatchNumber(),
                         new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 return new StringLiteral("$" + ((NthRefNode) node).getMatchNumber());
                             }
                         },
                         new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 return Nil.NIL;
                             }
                         });
                 break;
             case SELFNODE:
                 return new StringLiteral("self");
             case VCALLNODE:
                 m.loadSelf();
                 m.isMethodBound(((VCallNode) node).getName(),
                         new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 return new StringLiteral("method");
                             }
                         },
                         new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 return Nil.NIL;
                             }
                         });
                 break;
             case YIELDNODE:
                 m.hasBlock(new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 return new StringLiteral("yield");
                             }
                         },
                         new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 return Nil.NIL;
                             }
                         });
                 break;
             case GLOBALVARNODE:
                 m.isGlobalDefined(((GlobalVarNode) node).getName(),
                         new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 return new StringLiteral("global-variable");
                             }
                         },
                         new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 return Nil.NIL;
                             }
                         });
                 break;
             case INSTVARNODE:
                 m.isInstanceVariableDefined(((InstVarNode) node).getName(),
                         new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 return new StringLiteral("instance-variable");
                             }
                         },
                         new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 return Nil.NIL;
                             }
                         });
                 break;
             case CONSTNODE:
                 m.isConstantDefined(((ConstNode) node).getName(),
                         new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 return new StringLiteral("constant");
                             }
                         },
                         new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 return Nil.NIL;
                             }
                         });
                 break;
             case FCALLNODE:
                 m.loadSelf();
                 m.isMethodBound(((FCallNode) node).getName(),
                         new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 buildGetArgumentDefinition(((FCallNode) node).getArgsNode(), m, "method");
                             }
                         },
                         new BranchCallback() {
                             public void branch(IR_Scope m) {
                                 return Nil.NIL;
                             }
                         });
                 break;
             case COLON3NODE:
             case COLON2NODE:
                 {
                     final Colon3Node iVisited = (Colon3Node) node;
 
                     final String name = iVisited.getName();
 
                     BranchCallback setup = new BranchCallback() {
 
                                 public void branch(IR_Scope m) {
                                     if (iVisited instanceof Colon2Node) {
                                         final Node leftNode = ((Colon2Node) iVisited).getLeftNode();
                                         build(leftNode, m,true);
                                     } else {
                                         m.loadObject();
                                     }
                                 }
                             };
                     BranchCallback isConstant = new BranchCallback() {
 
                                 public void branch(IR_Scope m) {
                                     return new StringLiteral("constant");
                                 }
                             };
                     BranchCallback isMethod = new BranchCallback() {
 
                                 public void branch(IR_Scope m) {
                                     return new StringLiteral("method");
                                 }
                             };
                     BranchCallback none = new BranchCallback() {
                                 public void branch(IR_Scope m) {
                                     return Nil.NIL;
                                 }
                             };
                     m.isConstantBranch(setup, isConstant, isMethod, none, name);
                     break;
                 }
             case CALLNODE:
                 {
                     final CallNode iVisited = (CallNode) node;
                     Object isnull = m.getNewEnding();
                     Object ending = m.getNewEnding();
                     buildGetDefinition(iVisited.getReceiverNode(), m);
                     m.ifNull(isnull);
 
                     m.rescue(new BranchCallback() {
 
                                 public void branch(IR_Scope m) {
                                     build(iVisited.getReceiverNode(), m,true); //[IRubyObject]
                                     m.duplicateCurrentValue(); //[IRubyObject, IRubyObject]
                                     m.metaclass(); //[IRubyObject, RubyClass]
                                     m.duplicateCurrentValue(); //[IRubyObject, RubyClass, RubyClass]
                                     m.getVisibilityFor(iVisited.getName()); //[IRubyObject, RubyClass, Visibility]
                                     m.duplicateCurrentValue(); //[IRubyObject, RubyClass, Visibility, Visibility]
                                     final Object isfalse = m.getNewEnding();
                                     Object isreal = m.getNewEnding();
                                     Object ending = m.getNewEnding();
                                     m.isPrivate(isfalse, 3); //[IRubyObject, RubyClass, Visibility]
                                     m.isNotProtected(isreal, 1); //[IRubyObject, RubyClass]
                                     m.selfIsKindOf(isreal); //[IRubyObject]
                                     m.consumeCurrentValue();
                                     m.go(isfalse);
                                     m.setEnding(isreal); //[]
 
                                     m.isMethodBound(iVisited.getName(), new BranchCallback() {
 
                                                 public void branch(IR_Scope m) {
                                                     buildGetArgumentDefinition(iVisited.getArgsNode(), m, "method");
                                                 }
                                             },
                                             new BranchCallback() {
 
                                                 public void branch(IR_Scope m) {
                                                     m.go(isfalse);
                                                 }
                                             });
                                     m.go(ending);
                                     m.setEnding(isfalse);
                                     m.pushNull();
                                     m.setEnding(ending);
                                 }
                             }, JumpException.class,
                             new BranchCallback() {
 
                                 public void branch(IR_Scope m) {
                                     m.pushNull();
                                 }
                             }, String.class);
 
                     //          m.swapValues();
             //m.consumeCurrentValue();
                     m.go(ending);
                     m.setEnding(isnull);
                     m.pushNull();
                     m.setEnding(ending);
                     break;
                 }
             case CLASSVARNODE:
                 {
                     ClassVarNode iVisited = (ClassVarNode) node;
                     final Object ending = m.getNewEnding();
                     final Object failure = m.getNewEnding();
                     final Object singleton = m.getNewEnding();
                     Object second = m.getNewEnding();
                     Object third = m.getNewEnding();
 
                     m.loadCurrentModule(); //[RubyClass]
                     m.duplicateCurrentValue(); //[RubyClass, RubyClass]
                     m.ifNotNull(second); //[RubyClass]
                     m.consumeCurrentValue(); //[]
                     m.loadSelf(); //[self]
                     m.metaclass(); //[RubyClass]
                     m.duplicateCurrentValue(); //[RubyClass, RubyClass]
                     m.isClassVarDefined(iVisited.getName(),
                             new BranchCallback() {
 
                                 public void branch(IR_Scope m) {
                                     m.consumeCurrentValue();
                                     Operand sl = new StringLiteral("class variable");
                                     m.go(ending);
                                 }
                             },
                             new BranchCallback() {
 
                                 public void branch(IR_Scope m) {
                                 }
                             });
                     m.setEnding(second);  //[RubyClass]
                     m.duplicateCurrentValue();
                     m.isClassVarDefined(iVisited.getName(),
                             new BranchCallback() {
 
                                 public void branch(IR_Scope m) {
                                     m.consumeCurrentValue();
                                     Operand sl = new StringLiteral("class variable");
                                     m.go(ending);
                                 }
                             },
                             new BranchCallback() {
 
                                 public void branch(IR_Scope m) {
                                 }
                             });
                     m.setEnding(third); //[RubyClass]
                     m.duplicateCurrentValue(); //[RubyClass, RubyClass]
                     m.ifSingleton(singleton); //[RubyClass]
                     m.consumeCurrentValue();//[]
                     m.go(failure);
                     m.setEnding(singleton);
                     m.attached();//[RubyClass]
                     m.notIsModuleAndClassVarDefined(iVisited.getName(), failure); //[]
                     Operand sl = new StringLiteral("class variable");
                     m.go(ending);
                     m.setEnding(failure);
                     m.pushNull();
                     m.setEnding(ending);
                 }
                 break;
             case ZSUPERNODE:
                 {
                     Object fail = m.getNewEnding();
                     Object fail2 = m.getNewEnding();
                     Object fail_easy = m.getNewEnding();
                     Object ending = m.getNewEnding();
 
                     m.getFrameName(); //[String]
                     m.duplicateCurrentValue(); //[String, String]
                     m.ifNull(fail); //[String]
                     m.getFrameKlazz(); //[String, RubyClass]
                     m.duplicateCurrentValue(); //[String, RubyClass, RubyClass]
                     m.ifNull(fail2); //[String, RubyClass]
                     m.superClass();
                     m.ifNotSuperMethodBound(fail_easy);
 
                     Operand sl = new StringLiteral("super");
                     m.go(ending);
 
                     m.setEnding(fail2);
                     m.consumeCurrentValue();
                     m.setEnding(fail);
                     m.consumeCurrentValue();
                     m.setEnding(fail_easy);
                     m.pushNull();
                     m.setEnding(ending);
                 }
                 break;
             case SUPERNODE:
                 {
                     Object fail = m.getNewEnding();
                     Object fail2 = m.getNewEnding();
                     Object fail_easy = m.getNewEnding();
                     Object ending = m.getNewEnding();
 
                     m.getFrameName(); //[String]
                     m.duplicateCurrentValue(); //[String, String]
                     m.ifNull(fail); //[String]
                     m.getFrameKlazz(); //[String, RubyClass]
                     m.duplicateCurrentValue(); //[String, RubyClass, RubyClass]
                     m.ifNull(fail2); //[String, RubyClass]
                     m.superClass();
                     m.ifNotSuperMethodBound(fail_easy);
 
                     buildGetArgumentDefinition(((SuperNode) node).getArgsNode(), m, "super");
                     m.go(ending);
 
                     m.setEnding(fail2);
                     m.consumeCurrentValue();
                     m.setEnding(fail);
                     m.consumeCurrentValue();
                     m.setEnding(fail_easy);
                     m.pushNull();
                     m.setEnding(ending);
                     break;
                 }
             case ATTRASSIGNNODE:
                 {
                     final AttrAssignNode iVisited = (AttrAssignNode) node;
                     Object isnull = m.getNewEnding();
                     Object ending = m.getNewEnding();
                     buildGetDefinition(iVisited.getReceiverNode(), m);
                     m.ifNull(isnull);
 
                     m.rescue(new BranchCallback() {
 
                                 public void branch(IR_Scope m) {
                                     build(iVisited.getReceiverNode(), m,true); //[IRubyObject]
                                     m.duplicateCurrentValue(); //[IRubyObject, IRubyObject]
                                     m.metaclass(); //[IRubyObject, RubyClass]
                                     m.duplicateCurrentValue(); //[IRubyObject, RubyClass, RubyClass]
                                     m.getVisibilityFor(iVisited.getName()); //[IRubyObject, RubyClass, Visibility]
                                     m.duplicateCurrentValue(); //[IRubyObject, RubyClass, Visibility, Visibility]
                                     final Object isfalse = m.getNewEnding();
                                     Object isreal = m.getNewEnding();
                                     Object ending = m.getNewEnding();
                                     m.isPrivate(isfalse, 3); //[IRubyObject, RubyClass, Visibility]
                                     m.isNotProtected(isreal, 1); //[IRubyObject, RubyClass]
                                     m.selfIsKindOf(isreal); //[IRubyObject]
                                     m.consumeCurrentValue();
                                     m.go(isfalse);
                                     m.setEnding(isreal); //[]
 
                                     m.isMethodBound(iVisited.getName(), new BranchCallback() {
 
                                                 public void branch(IR_Scope m) {
                                                     buildGetArgumentDefinition(iVisited.getArgsNode(), m, "assignment");
                                                 }
                                             },
                                             new BranchCallback() {
 
                                                 public void branch(IR_Scope m) {
                                                     m.go(isfalse);
                                                 }
                                             });
                                     m.go(ending);
                                     m.setEnding(isfalse);
                                     m.pushNull();
                                     m.setEnding(ending);
                                 }
                             }, JumpException.class,
                             new BranchCallback() {
 
                                 public void branch(IR_Scope m) {
                                     m.pushNull();
                                 }
                             }, String.class);
 
                     m.go(ending);
                     m.setEnding(isnull);
                     m.pushNull();
                     m.setEnding(ending);
                     break;
                 }
             default:
                 m.rescue(new BranchCallback() {
 
                             public void branch(IR_Scope m) {
                                 build(node, m,true);
                                 m.consumeCurrentValue();
                                 m.pushNull();
                             }
                         }, JumpException.class,
                         new BranchCallback() {
 
                             public void branch(IR_Scope m) {
                                 m.pushNull();
                             }
                         }, String.class);
                 m.consumeCurrentValue();
                 //MPS_FIXME: new StringLiteral("expression");
         }
     }
 
     public Operand buildDAsgn(Node node, IR_Scope s) {
         final DAsgnNode dasgnNode = (DAsgnNode) node;
 
         // SSS: Looks like we receive the arg in buildAssignment via the IterNode
         // We won't get here for argument receives!  So, buildDasgn is called for
         // assignments to block variables within a block.  As far as the IR is concerned,
         // this is just a simple copy
         Operand arg = new Variable(node.getName());
         s.addIntsr(new COPY_Instr(arg, build(dasgnNode.getValueNode(), s)));
         return arg;
     }
 
 /**
  * SSS FIXME: Used anywhere?  I don't see calls to this anywhere
     public Operand buildDAsgnAssignment(Node node, IR_Scope s) {
         DAsgnNode dasgnNode = (DAsgnNode) node;
         s.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth());
     }
 **/
 
     private Operand defineNewMethod(Node n, IR_Scope s, boolean isInstanceMethod)
     {
         final DefnNode defnNode = (DefnNode) node;
         IR_Method m = new IR_Method(s, defnNode.getName(), isInstanceMethod);
 
             // Build IR for args
         buildArgs(defnNode.getArgsNode(), m);
 
             // Build IR for body
         if (defnNode.getBodyNode() != null) {
                 // if root of method is rescue, build as a light rescue
             if (defnNode.getBodyNode() instanceof RescueNode)
                 buildRescueInternal(defnNode.getBodyNode(), m, true);
             else
                 build(defnNode.getBodyNode(), m);
         } else {
            m.addInstr(new RETURN_Instr(Nil.NIL));
         }
 
             // No value returned for a method definition 
             // SSS FIXME: Verify from the ruby spec that this is true
         return null;
     }
 
     public Operand buildDefn(Node node, IR_Scope s) {
             // Instance method
         return defineNewMethod(n, s, true);
     }
 
     public Operand buildDefs(Node node, IR_Scope m) {
             // Class method
         return defineNewMethod(n, s, false);
 
             // SSS FIXME: Receiver -- this is the class meta object basically?
         // Operand receiver = build(defsNode.getReceiverNode(), s);
     }
 
     public Operand buildArgs(Node node, IR_Scope s) {
         final IR_Method m = (IR_Method)s;
         final ArgsNode argsNode = (ArgsNode)node;
         final int required = argsNode.getRequiredArgsCount();
         final int opt = argsNode.getOptionalArgsCount();
         final int rest = argsNode.getRestArg();
 
           // TODO: Add IR instructions for checking method arity!
         // m.getVariableCompiler().checkMethodArity(required, opt, rest);
 
             // self = args[0]
             // SSS FIXME: Verify that this is correct
         m.addInstr(new RECV_ARG_Instr(m.getSelf(), new Constant(0)));
 
             // Other args begin at index 1
         int argIndex = 1;
 
             // Both for fixed arity and variable arity methods
         ListNode preArgs  = argsNode.getPre();
         for (int i = 0; i < m.numRequiredArgs(); i++, argIndex++) {
             ArgumentNode a = (ArgumentNode)preArgs.get(i);
             m.addInstr(new RECV_ARG_Instr(new Variable(a.getName()), new Constant(argIndex)));
         }
 
         if (opt > 0 || rest > -1) {
             ListNode optArgs = argsNode.getOptArgs();
             for (j = 0; j < opt; j++, argIndex++) {
                     // Jump to 'l' if this arg is not null.  If null, fall through and build the default value!
                 Label l = m.getNewLabel();
                 LoclAsgnNode n = optArgs.get(j);
                 m.addInstr(new RECV_OPT_ARG_Instr(new Variable(n.getName()), new Constant(argIndex), l));
                 build(n, m, true);
                 m.addInstr(new LABEL_Instr(l));
             }
 
             if (rest > -1) {
                 m.addInstr(new RECV_ARG_Instr(new Variable(argsNode.getRestArgNode().getName()), new Constant(argIndex)));
                 argIndex++;
             }
         }
 
         // FIXME: Ruby 1.9 post args code needs to come here
 
         if (argsNode.getBlock() != null)
             m.addInstr(new RECV_ARG_Instr(argsNode.getBlockNode().getName(), new Constant(argIndex)));
 
         // TODO: don't require pop
         if (!expr) m.consumeCurrentValue();
 
             // This is not an expression that computes anything
         return null;
     }
 
     public Operand buildDot(Node node, IR_Scope s) {
         final DotNode dotNode = (DotNode) node;
         return new Range(build(dotNode.getBeginNode(), s), build(dotNode.getEndNode(), s));
     }
 
     public Operand buildDRegexp(Node node, IR_Scope s) {
         final DRegexpNode dregexpNode = (DRegexpNode) node;
         List<Operand> strPieces = new ArrayList<Operand>();
         for (Node n : dregexpNode.childNodes()) {
             strPieces.add(build(n, s));
 
         return new Regexp(new CompoundString(strPieces), dregexpNode.getOptions());
     }
 
     public Operand buildDStr(Node node, IR_Scope s) {
         final DStrNode dstrNode = (DStrNode) node;
         List<Operand> strPieces = new ArrayList<Operand>();
         for (Node n : dstrNode.childNodes()) {
             strPieces.add(build(n, s));
 
         return new CompoundString(strPieces);
     }
 
     public Operand buildDSymbol(Node node, IR_Scope s) {
         List<Operand> strPieces = new ArrayList<Operand>();
         for (Node n : node.childNodes()) {
             strPieces.add(build(n, s));
 
         return new DynamicSymbol(new CompoundString(strPieces));
     }
 
     public Operand buildDVar(Node node, IR_Scope m) {
         return new Variable(((DarNode) node).getName());
     }
 
     public Operand buildDXStr(Node node, IR_Scope m) {
         final DXStrNode dstrNode = (DXStrNode) node;
         List<Operand> strPieces = new ArrayList<Operand>();
         for (Node nextNode : dstrNode.childNodes()) {
             strPieces.add(build(nextNode, m));
 
         return new BacktickString(strPieces);
     }
 
     public Operand buildEnsureNode(Node node, IR_Scope m) {
         final EnsureNode ensureNode = (EnsureNode) node;
 
         if (ensureNode.getEnsureNode() != null) {
             m.performEnsure(new BranchCallback() {
 
                         public void branch(IR_Scope m) {
                             if (ensureNode.getBodyNode() != null) {
                                 build(ensureNode.getBodyNode(), m, true);
                             } else {
                                 m.loadNil();
                             }
                         }
                     },
                     new BranchCallback() {
 
                         public void branch(IR_Scope m) {
                             build(ensureNode.getEnsureNode(), m, false);
                         }
                     });
         } else {
             if (ensureNode.getBodyNode() != null) {
                 build(ensureNode.getBodyNode(), m,true);
             } else {
                 m.loadNil();
             }
         }
         // TODO: don't require pop
         if (!expr) m.consumeCurrentValue();
     }
 
     public Operand buildEvStr(Node node, IR_Scope m) {
             // SSS: FIXME: Somewhere here, we need to record information the type of this operand as String
         return build(((EvStrNode) node).getBody(), s)
     }
 
     public Operand buildFalse(Node node, IR_Scope m) {
         m.addInstr(new THREAD_POLL_Instr());
         return BooleanLiteral.FALSE; 
     }
 
     public Operand buildFCall(Node node, IR_Scope s) {
         FCallNode     fcallNode    = (FCallNode)node;
         Node          callArgsNode = fcallNode.getArgsNode();
         List<Operand> args         = setupArgs(callArgsNode, s);
         Operand       block        = setupCallClosure(fcallNode.getIterNode(), s);
         Variable      callResult   = s.getNewTmpVariable();
         IR_Instr      callInstr    = new CALL_Instr(callResult, new MethAddr(fcallNode.getName()), args.toArray(new Operand[args.size()]), block);
         s.addInstr(callInstr);
         return callResult;
     }
 
     private Operand setupCallClosure(Node node, IR_Scope s) {
         if (node == null)
             return null;
 
         switch (node.getNodeType()) {
             case ITERNODE:
                 build((IterNode)node, s, true);
                 return new Operand(); //FIXME
             case BLOCKPASSNODE:
                 build(((BlockPassNode)node).getBodyNode(), s, true);
                 // FIXME: Translate this call below!
                 s.unwrapPassedBlock();
                 return new Operand(); //FIXME
             default:
                 throw new NotCompilableException("ERROR: Encountered a method with a non-block, non-blockpass iter node at: " + node);
         }
     }
 
     public Operand buildFixnum(Node node, IR_Scope m) {
         return new Fixnum(((FixnumNode)node).getValue());
     }
 
     public Operand buildFlip(Node node, IR_Scope m) {
         final FlipNode flipNode = (FlipNode) node;
 
         m.getVariableCompiler().retrieveLocalVariable(flipNode.getIndex(), flipNode.getDepth());
 
         if (flipNode.isExclusive()) {
             m.performBooleanBranch(new BranchCallback() {
 
                 public void branch(IR_Scope m) {
                     build(flipNode.getEndNode(), m,true);
                     m.performBooleanBranch(new BranchCallback() {
 
                         public void branch(IR_Scope m) {
                             m.loadFalse();
                             m.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), false);
                         }
                     }, new BranchCallback() {
 
                         public void branch(IR_Scope m) {
                         }
                     });
                     m.loadTrue();
                 }
             }, new BranchCallback() {
 
                 public void branch(IR_Scope m) {
                     build(flipNode.getBeginNode(), m,true);
                     becomeTrueOrFalse(m);
                     m.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), true);
                 }
             });
         } else {
             m.performBooleanBranch(new BranchCallback() {
 
                 public void branch(IR_Scope m) {
                     build(flipNode.getEndNode(), m,true);
                     m.performBooleanBranch(new BranchCallback() {
 
                         public void branch(IR_Scope m) {
                             m.loadFalse();
                             m.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), false);
                         }
                     }, new BranchCallback() {
 
                         public void branch(IR_Scope m) {
                         }
                     });
                     m.loadTrue();
                 }
             }, new BranchCallback() {
 
                 public void branch(IR_Scope m) {
                     build(flipNode.getBeginNode(), m,true);
                     m.performBooleanBranch(new BranchCallback() {
 
                         public void branch(IR_Scope m) {
                             build(flipNode.getEndNode(), m,true);
                             flipTrueOrFalse(m);
                             m.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), false);
                             m.loadTrue();
                         }
                     }, new BranchCallback() {
 
                         public void branch(IR_Scope m) {
                             m.loadFalse();
                         }
                     });
                 }
             });
         }
         // TODO: don't require pop
         if (!expr) m.consumeCurrentValue();
     }
 
     private void becomeTrueOrFalse(IR_Scope m) {
         m.performBooleanBranch(new BranchCallback() {
 
                     public void branch(IR_Scope m) {
                         m.loadTrue();
