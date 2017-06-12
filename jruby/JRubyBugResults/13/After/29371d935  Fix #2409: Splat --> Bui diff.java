diff --git a/core/src/main/java/org/jruby/ir/IRBuilder.java b/core/src/main/java/org/jruby/ir/IRBuilder.java
index e9172fe67e..792bd34195 100644
--- a/core/src/main/java/org/jruby/ir/IRBuilder.java
+++ b/core/src/main/java/org/jruby/ir/IRBuilder.java
@@ -1,1609 +1,1609 @@
 package org.jruby.ir;
 
 import org.jcodings.specific.ASCIIEncoding;
 import org.jruby.EvalType;
 import org.jruby.Ruby;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.ast.*;
 import org.jruby.ast.types.INameNode;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.internal.runtime.methods.IRMethodArgs;
 import org.jruby.ir.instructions.*;
 import org.jruby.ir.instructions.defined.GetErrorInfoInstr;
 import org.jruby.ir.instructions.defined.RestoreErrorInfoInstr;
 import org.jruby.ir.listeners.IRScopeListener;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.operands.Boolean;
 import org.jruby.ir.operands.Float;
 import org.jruby.ir.transformations.inlining.SimpleCloneInfo;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.util.ByteList;
 import org.jruby.util.KeyValuePair;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.util.*;
 
 import static org.jruby.ir.instructions.RuntimeHelperCall.Methods.*;
 
 import static org.jruby.ir.operands.CurrentScope.*;
 import static org.jruby.ir.operands.ScopeModule.*;
 
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
 // 2. Returning null vs manager.getNil()
 // ----------------------------
 // - We should be returning null from the build methods where it is a normal "error" condition
 // - We should be returning manager.getNil() where the actual return value of a build is the ruby nil operand
 //   Look in buildIf for an example of this
 //
 // 3. Temporary variable reuse
 // ---------------------------
 // I am reusing variables a lot in places in this code.  Should I instead always get a new variable when I need it
 // This introduces artificial data dependencies, but fewer variables.  But, if we are going to implement SSA pass
 // this is not a big deal.  Think this through!
 
 public class IRBuilder {
     static final Operand[] NO_ARGS = new Operand[]{};
     static final UnexecutableNil U_NIL = UnexecutableNil.U_NIL;
 
     public static IRBuilder createIRBuilder(Ruby runtime, IRManager manager) {
         return new IRBuilder(manager);
     }
 
     public static Node buildAST(boolean isCommandLineScript, String arg) {
         Ruby ruby = Ruby.getGlobalRuntime();
 
         // inline script
         if (isCommandLineScript) return ruby.parse(ByteList.create(arg), "-e", null, 0, false);
 
         // from file
         FileInputStream fis = null;
         try {
             File file = new File(arg);
             fis = new FileInputStream(file);
             long size = file.length();
             byte[] bytes = new byte[(int)size];
             fis.read(bytes);
             System.out.println("-- processing " + arg + " --");
             return ruby.parse(new ByteList(bytes), arg, null, 0, false);
         } catch (IOException ioe) {
             throw new RuntimeException(ioe);
         } finally {
             try { if (fis != null) fis.close(); } catch(Exception ignored) { }
         }
     }
 
     private static class IRLoop {
         public final IRScope  container;
         public final IRLoop   parentLoop;
         public final Label    loopStartLabel;
         public final Label    loopEndLabel;
         public final Label    iterStartLabel;
         public final Label    iterEndLabel;
         public final Variable loopResult;
 
         public IRLoop(IRScope s, IRLoop outerLoop) {
             container = s;
             parentLoop = outerLoop;
             loopStartLabel = s.getNewLabel("_LOOP_BEGIN");
             loopEndLabel   = s.getNewLabel("_LOOP_END");
             iterStartLabel = s.getNewLabel("_ITER_BEGIN");
             iterEndLabel   = s.getNewLabel("_ITER_END");
             loopResult     = s.createTemporaryVariable();
             s.setHasLoopsFlag();
         }
     }
 
     private static class RescueBlockInfo {
         RescueNode rescueNode;             // Rescue node for which we are tracking info
         Label      entryLabel;             // Entry of the rescue block
         Variable   savedExceptionVariable; // Variable that contains the saved $! variable
         IRLoop     innermostLoop;          // Innermost loop within which this rescue block is nested, if any
 
         public RescueBlockInfo(RescueNode n, Label l, Variable v, IRLoop loop) {
             rescueNode = n;
             entryLabel = l;
             savedExceptionVariable = v;
             innermostLoop = loop;
         }
 
         public void restoreException(IRBuilder b, IRScope s, IRLoop currLoop) {
             if (currLoop == innermostLoop) b.addInstr(s, new PutGlobalVarInstr("$!", savedExceptionVariable));
         }
     }
 
     /* -----------------------------------------------------------------------------------
      * Every ensure block has a start label and end label
      *
      * This ruby code will translate to the IR shown below
      * -----------------
      *   begin
      *       ... protected body ...
      *   ensure
      *       ... ensure block to run
      *   end
      * -----------------
      *  L_region_start
      *     IR instructions for the protected body
      *     .. copy of ensure block IR ..
      *  L_dummy_rescue:
      *     e = recv_exc
      *  L_start:
      *     .. ensure block IR ..
      *     throw e
      *  L_end:
      * -----------------
      *
      * If N is a node in the protected body that might exit this scope (exception rethrows
      * and returns), N has to first run the ensure block before exiting.
      *
      * Since we can have a nesting of ensure blocks, we are maintaining a stack of these
      * well-nested ensure blocks.  Every node N that will exit this scope will have to
      * run the stack of ensure blocks in the right order.
      * ----------------------------------------------------------------------------------- */
     private static class EnsureBlockInfo {
         Label    regionStart;
         Label    start;
         Label    end;
         Label    dummyRescueBlockLabel;
         Variable savedGlobalException;
 
         // Label of block that will rescue exceptions raised by ensure code
         Label    bodyRescuer;
 
         // Innermost loop within which this ensure block is nested, if any
         IRLoop   innermostLoop;
 
         // AST node for any associated rescue node in the case of begin-rescue-ensure-end block
         // Will be null in the case of begin-ensure-end block
         RescueNode matchingRescueNode;
 
         // This ensure block's instructions
         List<Instr> instrs;
 
         public EnsureBlockInfo(IRScope s, RescueNode n, IRLoop l, Label bodyRescuer) {
             regionStart = s.getNewLabel();
             start       = s.getNewLabel();
             end         = s.getNewLabel();
             dummyRescueBlockLabel = s.getNewLabel();
             instrs = new ArrayList<>();
             savedGlobalException = null;
             innermostLoop = l;
             matchingRescueNode = n;
             this.bodyRescuer = bodyRescuer;
         }
 
         public void addInstr(Instr i) {
             instrs.add(i);
         }
 
         public void addInstrAtBeginning(Instr i) {
             instrs.add(0, i);
         }
 
         public void emitBody(IRBuilder b, IRScope s) {
             b.addInstr(s, new LabelInstr(start));
             for (Instr i: instrs) {
                 b.addInstr(s, i);
             }
         }
 
         public void cloneIntoHostScope(IRBuilder b, IRScope s) {
             SimpleCloneInfo ii = new SimpleCloneInfo(s, true);
 
             // Clone required labels.
             // During normal cloning below, labels not found in the rename map
             // are not cloned.
             ii.renameLabel(start);
             for (Instr i: instrs) {
                 if (i instanceof LabelInstr) {
                     ii.renameLabel(((LabelInstr)i).label);
                 }
             }
 
             // Clone instructions now
             b.addInstr(s, new LabelInstr(ii.getRenamedLabel(start)));
             b.addInstr(s, new ExceptionRegionStartMarkerInstr(bodyRescuer));
             for (Instr i: instrs) {
                 Instr clonedInstr = i.clone(ii);
                 if (clonedInstr instanceof CallBase) {
                     CallBase call = (CallBase)clonedInstr;
                     Operand block = call.getClosureArg(null);
                     if (block instanceof WrappedIRClosure) s.addClosure(((WrappedIRClosure)block).getClosure());
                 }
                 b.addInstr(s, clonedInstr);
             }
             b.addInstr(s, new ExceptionRegionEndMarkerInstr());
         }
     }
 
     // Stack of nested rescue blocks -- this just tracks the start label of the blocks
     private Stack<RescueBlockInfo> activeRescueBlockStack = new Stack<>();
 
     // Stack of ensure blocks that are currently active
     private Stack<EnsureBlockInfo> activeEnsureBlockStack = new Stack<>();
 
     // Stack of ensure blocks whose bodies are being constructed
     private Stack<EnsureBlockInfo> ensureBodyBuildStack   = new Stack<>();
 
     // Combined stack of active rescue/ensure nestings -- required to properly set up
     // rescuers for ensure block bodies cloned into other regions -- those bodies are
     // rescued by the active rescuers at the point of definition rather than the point
     // of cloning.
     private Stack<Label> activeRescuers = new Stack<>();
 
     private int _lastProcessedLineNum = -1;
 
     // Since we are processing ASTs, loop bodies are processed in depth-first manner
     // with outer loops encountered before inner loops, and inner loops finished before outer ones.
     //
     // So, we can keep track of loops in a loop stack which  keeps track of loops as they are encountered.
     // This lets us implement next/redo/break/retry easily for the non-closure cases
     private Stack<IRLoop> loopStack = new Stack<>();
 
     public IRLoop getCurrentLoop() {
         return loopStack.isEmpty() ? null : loopStack.peek();
     }
 
     protected IRManager manager;
 
     public IRBuilder(IRManager manager) {
         this.manager = manager;
         this.activeRescuers.push(Label.UNRESCUED_REGION_LABEL);
     }
 
     public void addInstr(IRScope s, Instr i) {
         // If we are building an ensure body, stash the instruction
         // in the ensure body's list. If not, add it to the scope directly.
         if (ensureBodyBuildStack.empty()) {
             s.addInstr(i);
         } else {
             ensureBodyBuildStack.peek().addInstr(i);
         }
     }
 
     public void addInstrAtBeginning(IRScope s, Instr i) {
         // If we are building an ensure body, stash the instruction
         // in the ensure body's list. If not, add it to the scope directly.
         if (ensureBodyBuildStack.empty()) {
             s.addInstrAtBeginning(i);
         } else {
             ensureBodyBuildStack.peek().addInstrAtBeginning(i);
         }
     }
 
     // FIXME: This all seems wrong to me.  IRClosures can receive explicit closures why are we searching only methods
     // and by-passing closures
     private Operand getImplicitBlockArg(IRScope s) {
         int n = 0;
         while (s instanceof IRClosure || s instanceof IRMetaClassBody) {
             n++;
             s = s.getLexicalParent();
         }
 
         if (s != null) {
             LocalVariable v = s instanceof IRMethod ? s.getLocalVariable(Variable.BLOCK, 0) : null;
 
             if (v != null) return n == 0 ? v : v.cloneForDepth(n);
         }
 
         return manager.getNil();
     }
 
     // Emit cloned ensure bodies by walking up the ensure block stack.
     // If we have been passed a loop value, only emit bodies that are nested within that loop.
     private void emitEnsureBlocks(IRScope s, IRLoop loop) {
         int n = activeEnsureBlockStack.size();
         EnsureBlockInfo[] ebArray = activeEnsureBlockStack.toArray(new EnsureBlockInfo[n]);
         for (int i = n-1; i >= 0; i--) {
             EnsureBlockInfo ebi = ebArray[i];
 
             // For "break" and "next" instructions, we only want to run
             // ensure blocks from the loops they are present in.
             if (loop != null && ebi.innermostLoop != loop) break;
 
             // SSS FIXME: Should $! be restored before or after the ensure block is run?
             if (ebi.savedGlobalException != null) {
                 addInstr(s, new PutGlobalVarInstr("$!", ebi.savedGlobalException));
             }
 
             // Clone into host scope
             ebi.cloneIntoHostScope(this, s);
         }
     }
 
     private Operand buildOperand(Node node, IRScope s) throws NotCompilableException {
         switch (node.getNodeType()) {
             case ALIASNODE: return buildAlias((AliasNode) node, s);
             case ANDNODE: return buildAnd((AndNode) node, s);
             case ARGSCATNODE: return buildArgsCat((ArgsCatNode) node, s);
             case ARGSPUSHNODE: return buildArgsPush((ArgsPushNode) node, s);
             case ARRAYNODE: return buildArray(node, s);
             case ATTRASSIGNNODE: return buildAttrAssign((AttrAssignNode) node, s);
             case BACKREFNODE: return buildBackref((BackRefNode) node, s);
             case BEGINNODE: return buildBegin((BeginNode) node, s);
             case BIGNUMNODE: return buildBignum((BignumNode) node);
             case BLOCKNODE: return buildBlock((BlockNode) node, s);
             case BREAKNODE: return buildBreak((BreakNode) node, s);
             case CALLNODE: return buildCall((CallNode) node, s);
             case CASENODE: return buildCase((CaseNode) node, s);
             case CLASSNODE: return buildClass((ClassNode) node, s);
             case CLASSVARNODE: return buildClassVar((ClassVarNode) node, s);
             case CLASSVARASGNNODE: return buildClassVarAsgn((ClassVarAsgnNode) node, s);
             case CLASSVARDECLNODE: return buildClassVarDecl((ClassVarDeclNode) node, s);
             case COLON2NODE: return buildColon2((Colon2Node) node, s);
             case COLON3NODE: return buildColon3((Colon3Node) node, s);
             case COMPLEXNODE: return buildComplex((ComplexNode) node, s);
             case CONSTDECLNODE: return buildConstDecl((ConstDeclNode) node, s);
             case CONSTNODE: return searchConst(s, ((ConstNode) node).getName());
             case DASGNNODE: return buildDAsgn((DAsgnNode) node, s);
             case DEFINEDNODE: return buildGetDefinition(((DefinedNode) node).getExpressionNode(), s);
             case DEFNNODE: return buildDefn((MethodDefNode) node, s);
             case DEFSNODE: return buildDefs((DefsNode) node, s);
             case DOTNODE: return buildDot((DotNode) node, s);
             case DREGEXPNODE: return buildDRegexp((DRegexpNode) node, s);
             case DSTRNODE: return buildDStr((DStrNode) node, s);
             case DSYMBOLNODE: return buildDSymbol((DSymbolNode) node, s);
             case DVARNODE: return buildDVar((DVarNode) node, s);
             case DXSTRNODE: return buildDXStr((DXStrNode) node, s);
             case ENCODINGNODE: return buildEncoding((EncodingNode)node, s);
             case ENSURENODE: return buildEnsureNode((EnsureNode) node, s);
             case EVSTRNODE: return buildEvStr((EvStrNode) node, s);
             case FALSENODE: return buildFalse();
             case FCALLNODE: return buildFCall((FCallNode) node, s);
             case FIXNUMNODE: return buildFixnum((FixnumNode) node);
             case FLIPNODE: return buildFlip((FlipNode) node, s);
             case FLOATNODE: return buildFloat((FloatNode) node);
             case FORNODE: return buildFor((ForNode) node, s);
             case GLOBALASGNNODE: return buildGlobalAsgn((GlobalAsgnNode) node, s);
             case GLOBALVARNODE: return buildGlobalVar((GlobalVarNode) node, s);
             case HASHNODE: return buildHash((HashNode) node, s);
             case IFNODE: return buildIf((IfNode) node, s);
             case INSTASGNNODE: return buildInstAsgn((InstAsgnNode) node, s);
             case INSTVARNODE: return buildInstVar((InstVarNode) node, s);
             case ITERNODE: return buildIter((IterNode) node, s);
             case LAMBDANODE: return buildLambda((LambdaNode)node, s);
             case LITERALNODE: return buildLiteral((LiteralNode) node, s);
             case LOCALASGNNODE: return buildLocalAsgn((LocalAsgnNode) node, s);
             case LOCALVARNODE: return buildLocalVar((LocalVarNode) node, s);
             case MATCH2NODE: return buildMatch2((Match2Node) node, s);
             case MATCH3NODE: return buildMatch3((Match3Node) node, s);
             case MATCHNODE: return buildMatch((MatchNode) node, s);
             case MODULENODE: return buildModule((ModuleNode) node, s);
             case MULTIPLEASGNNODE: return buildMultipleAsgn((MultipleAsgnNode) node, s); // Only for 1.8
             case MULTIPLEASGN19NODE: return buildMultipleAsgn19((MultipleAsgn19Node) node, s);
             case NEWLINENODE: return buildNewline((NewlineNode) node, s);
             case NEXTNODE: return buildNext((NextNode) node, s);
             case NTHREFNODE: return buildNthRef((NthRefNode) node, s);
             case NILNODE: return buildNil();
             case OPASGNANDNODE: return buildOpAsgnAnd((OpAsgnAndNode) node, s);
             case OPASGNNODE: return buildOpAsgn((OpAsgnNode) node, s);
             case OPASGNORNODE: return buildOpAsgnOr((OpAsgnOrNode) node, s);
             case OPELEMENTASGNNODE: return buildOpElementAsgn((OpElementAsgnNode) node, s);
             case ORNODE: return buildOr((OrNode) node, s);
             case PREEXENODE: return buildPreExe((PreExeNode) node, s);
             case POSTEXENODE: return buildPostExe((PostExeNode) node, s);
             case RATIONALNODE: return buildRational((RationalNode) node);
             case REDONODE: return buildRedo(s);
             case REGEXPNODE: return buildRegexp((RegexpNode) node, s);
             case RESCUEBODYNODE:
                 throw new NotCompilableException("rescue body is handled by rescue compilation at: " + node.getPosition());
             case RESCUENODE: return buildRescue((RescueNode) node, s);
             case RETRYNODE: return buildRetry(s);
             case RETURNNODE: return buildReturn((ReturnNode) node, s);
             case ROOTNODE:
                 throw new NotCompilableException("Use buildRoot(); Root node at: " + node.getPosition());
             case SCLASSNODE: return buildSClass((SClassNode) node, s);
             case SELFNODE: return buildSelf(s);
             case SPLATNODE: return buildSplat((SplatNode) node, s);
             case STRNODE: return buildStr((StrNode) node, s);
             case SUPERNODE: return buildSuper((SuperNode) node, s);
             case SVALUENODE: return buildSValue((SValueNode) node, s);
             case SYMBOLNODE: return buildSymbol((SymbolNode) node);
             case TRUENODE: return buildTrue();
             case UNDEFNODE: return buildUndef(node, s);
             case UNTILNODE: return buildUntil((UntilNode) node, s);
             case VALIASNODE: return buildVAlias((VAliasNode) node, s);
             case VCALLNODE: return buildVCall((VCallNode) node, s);
             case WHILENODE: return buildWhile((WhileNode) node, s);
             case WHENNODE: assert false : "When nodes are handled by case node compilation."; return null;
             case XSTRNODE: return buildXStr((XStrNode) node, s);
             case YIELDNODE: return buildYield((YieldNode) node, s);
             case ZARRAYNODE: return buildZArray(s);
             case ZSUPERNODE: return buildZSuper((ZSuperNode) node, s);
             default: throw new NotCompilableException("Unknown node encountered in builder: " + node.getClass());
         }
     }
 
     private boolean hasListener() {
         return manager.getIRScopeListener() != null;
     }
 
     public static IRBuilder newIRBuilder(IRManager manager) {
         return new IRBuilder(manager);
     }
 
     public Node skipOverNewlines(IRScope s, Node n) {
         if (n.getNodeType() == NodeType.NEWLINENODE) {
             // Do not emit multiple line number instrs for the same line
             int currLineNum = n.getPosition().getLine();
             if (currLineNum != _lastProcessedLineNum) {
                 if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
                     addInstr(s, new TraceInstr(RubyEvent.LINE, methodNameFor(s), s.getFileName(), currLineNum));
                 }
                addInstr(s, new LineNumberInstr(currLineNum));
                _lastProcessedLineNum = currLineNum;
             }
         }
 
         while (n.getNodeType() == NodeType.NEWLINENODE) {
             n = ((NewlineNode) n).getNextNode();
         }
 
         return n;
     }
 
     public Operand build(Node node, IRScope s) {
         if (node == null) return null;
 
         if (s == null) {
             System.out.println("Got a null scope!");
             throw new NotCompilableException("Unknown node encountered in builder: " + node);
         }
         if (hasListener()) {
             IRScopeListener listener = manager.getIRScopeListener();
             listener.startBuildOperand(node, s);
         }
         Operand operand = buildOperand(node, s);
         if (hasListener()) {
             IRScopeListener listener = manager.getIRScopeListener();
             listener.endBuildOperand(node, s, operand);
         }
         return operand;
     }
 
     public Operand buildLambda(LambdaNode node, IRScope s) {
         IRClosure closure = new IRClosure(manager, s, node.getPosition().getLine(), node.getScope(), Arity.procArityOf(node.getArgs()), node.getArgumentType());
 
         // Create a new nested builder to ensure this gets its own IR builder state
         // like the ensure block stack
         IRBuilder closureBuilder = newIRBuilder(manager);
 
         // Receive self
         closureBuilder.addInstr(closure, new ReceiveSelfInstr(closure.getSelf()));
 
         // Set %current_scope = <current-scope>
         // Set %current_module = <current-module>
         closureBuilder.addInstr(closure, new CopyInstr(closure.getCurrentScopeVariable(), CURRENT_SCOPE[0]));
         closureBuilder.addInstr(closure, new CopyInstr(closure.getCurrentModuleVariable(), SCOPE_MODULE[0]));
 
         // args
         closureBuilder.receiveBlockArgs(node, closure);
 
         Operand closureRetVal = node.getBody() == null ? manager.getNil() : closureBuilder.build(node.getBody(), closure);
 
         // can be U_NIL if the node is an if node with returns in both branches.
         if (closureRetVal != U_NIL) closureBuilder.addInstr(closure, new ReturnInstr(closureRetVal));
 
         closureBuilder.handleBreakAndReturnsInLambdas(closure);
 
         Variable lambda = s.createTemporaryVariable();
         WrappedIRClosure lambdaBody = new WrappedIRClosure(closure.getSelf(), closure);
         addInstr(s, new BuildLambdaInstr(lambda, lambdaBody, node.getPosition()));
         return lambda;
     }
 
     public Operand buildEncoding(EncodingNode node, IRScope s) {
         Variable ret = s.createTemporaryVariable();
         addInstr(s, new GetEncodingInstr(ret, node.getEncoding()));
         return ret;
     }
 
     // Non-arg masgn
     public Operand buildMultipleAsgn19(MultipleAsgn19Node multipleAsgnNode, IRScope s) {
         Operand  values = build(multipleAsgnNode.getValueNode(), s);
         Variable ret = getValueInTemporaryVariable(s, values);
         Variable tmp = s.createTemporaryVariable();
         addInstr(s, new ToAryInstr(tmp, ret));
         buildMultipleAsgn19Assignment(multipleAsgnNode, s, null, tmp);
         return ret;
     }
 
     protected Variable copyAndReturnValue(IRScope s, Operand val) {
         return addResultInstr(s, new CopyInstr(s.createTemporaryVariable(), val));
     }
 
     protected Variable getValueInTemporaryVariable(IRScope s, Operand val) {
         if (val != null && val instanceof TemporaryVariable) return (Variable) val;
 
         return copyAndReturnValue(s, val);
     }
 
     // Return the last argument in the list as this represents rhs of the overall attrassign expression
     // e.g. 'a[1] = 2 #=> 2' or 'a[1] = 1,2,3 #=> [1,2,3]'
     protected Operand buildAttrAssignCallArgs(List<Operand> argsList, Node args, IRScope s) {
         switch (args.getNodeType()) {
             case ARRAYNODE: {     // a[1] = 2; a[1,2,3] = 4,5,6
                 Operand last = manager.getNil();
                 for (Node n: args.childNodes()) {
                     last = build(n, s);
                     argsList.add(last);
                 }
                 return last;
             }
             case ARGSPUSHNODE:  { // a[1, *b] = 2
                 ArgsPushNode argsPushNode = (ArgsPushNode)args;
                 Operand lhs = build(argsPushNode.getFirstNode(), s);
                 Operand rhs = build(argsPushNode.getSecondNode(), s);
                 Variable res = s.createTemporaryVariable();
                 addInstr(s, new BuildCompoundArrayInstr(res, lhs, rhs, true));
-                argsList.add(new Splat(res, true));
+                argsList.add(new Splat(res));
                 return rhs;
             }
             case SPLATNODE: {     // a[1] = *b
-                Splat rhs = new Splat(build(((SplatNode)args).getValue(), s), true);
+                Splat rhs = new Splat(buildSplat((SplatNode)args, s));
                 argsList.add(rhs);
                 return rhs;
             }
         }
 
         throw new NotCompilableException("Invalid node for attrassign call args: " + args.getClass().getSimpleName() + ":" + args.getPosition());
     }
 
     protected Operand[] buildCallArgs(Node args, IRScope s) {
         switch (args.getNodeType()) {
             case ARGSCATNODE:
             case ARGSPUSHNODE:
-                return new Operand[] { new Splat(build(args, s), true) };
+                return new Operand[] { new Splat(build(args, s)) };
             case ARRAYNODE: {
                 List<Node> children = args.childNodes();
                 int numberOfArgs = children.size();
                 Operand[] builtArgs = new Operand[numberOfArgs];
 
                 for (int i = 0; i < numberOfArgs; i++) {
                     builtArgs[i] = build(children.get(i), s);
                 }
                 return builtArgs;
             }
             case SPLATNODE:
-                return new Operand[] { new Splat(build(((SplatNode) args).getValue(), s), true) };
+                return new Operand[] { new Splat(buildSplat((SplatNode)args, s)) };
         }
 
         throw new NotCompilableException("Invalid node for call args: " + args.getClass().getSimpleName() + ":" + args.getPosition());
     }
 
     public Operand[] setupCallArgs(Node args, IRScope s) {
         return args == null ? Operand.EMPTY_ARRAY : buildCallArgs(args, s);
     }
 
     public static Operand[] addArg(Operand[] args, Operand extraArg) {
         Operand[] newArgs = new Operand[args.length + 1];
         System.arraycopy(args, 0, newArgs, 0, args.length);
         newArgs[args.length] = extraArg;
         return newArgs;
     }
 
     // Non-arg masgn (actually a nested masgn)
     public void buildVersionSpecificAssignment(Node node, IRScope s, Variable v) {
         switch (node.getNodeType()) {
         case MULTIPLEASGN19NODE: {
             Variable tmp = s.createTemporaryVariable();
             addInstr(s, new ToAryInstr(tmp, v));
             buildMultipleAsgn19Assignment((MultipleAsgn19Node)node, s, null, tmp);
             break;
         }
         default:
             throw new NotCompilableException("Can't build assignment node: " + node);
         }
     }
 
     // This method is called to build assignments for a multiple-assignment instruction
     public void buildAssignment(Node node, IRScope s, Variable rhsVal) {
         switch (node.getNodeType()) {
             case ATTRASSIGNNODE:
                 buildAttrAssignAssignment(node, s, rhsVal);
                 break;
             case CLASSVARASGNNODE:
                 addInstr(s, new PutClassVariableInstr(classVarDefinitionContainer(s), ((ClassVarAsgnNode)node).getName(), rhsVal));
                 break;
             case CLASSVARDECLNODE:
                 addInstr(s, new PutClassVariableInstr(classVarDeclarationContainer(s), ((ClassVarDeclNode)node).getName(), rhsVal));
                 break;
             case CONSTDECLNODE:
                 buildConstDeclAssignment((ConstDeclNode) node, s, rhsVal);
                 break;
             case DASGNNODE: {
                 DAsgnNode variable = (DAsgnNode) node;
                 int depth = variable.getDepth();
                 addInstr(s, new CopyInstr(s.getLocalVariable(variable.getName(), depth), rhsVal));
                 break;
             }
             case GLOBALASGNNODE:
                 addInstr(s, new PutGlobalVarInstr(((GlobalAsgnNode)node).getName(), rhsVal));
                 break;
             case INSTASGNNODE:
                 // NOTE: if 's' happens to the a class, this is effectively an assignment of a class instance variable
                 addInstr(s, new PutFieldInstr(s.getSelf(), ((InstAsgnNode)node).getName(), rhsVal));
                 break;
             case LOCALASGNNODE: {
                 LocalAsgnNode localVariable = (LocalAsgnNode) node;
                 int depth = localVariable.getDepth();
                 addInstr(s, new CopyInstr(s.getLocalVariable(localVariable.getName(), depth), rhsVal));
                 break;
             }
             case ZEROARGNODE:
                 throw new NotCompilableException("Shouldn't get here; zeroarg does not do assignment: " + node);
             default:
                 buildVersionSpecificAssignment(node, s, rhsVal);
         }
     }
 
     protected LocalVariable getBlockArgVariable(IRScope s, String name, int depth) {
         if (!(s instanceof IRFor)) throw new NotCompilableException("Cannot ask for block-arg variable in 1.9 mode");
 
         return s.getLocalVariable(name, depth);
     }
 
     protected void receiveBlockArg(IRScope s, Variable v, Operand argsArray, int argIndex, boolean isSplat) {
         if (argsArray != null) {
             // We are in a nested receive situation -- when we are not at the root of a masgn tree
             // Ex: We are trying to receive (b,c) in this example: "|a, (b,c), d| = ..."
             if (isSplat) addInstr(s, new RestArgMultipleAsgnInstr(v, argsArray, argIndex));
             else addInstr(s, new ReqdArgMultipleAsgnInstr(v, argsArray, argIndex));
         } else {
             // argsArray can be null when the first node in the args-node-ast is a multiple-assignment
             // For example, for-nodes
             addInstr(s, isSplat ? new ReceiveRestArgInstr(v, argIndex, argIndex) : new ReceivePreReqdArgInstr(v, argIndex));
         }
     }
 
     public void buildVersionSpecificBlockArgsAssignment(Node node, IRScope s) {
         if (!(s instanceof IRFor)) throw new NotCompilableException("Should not have come here for block args assignment in 1.9 mode: " + node);
 
         // Argh!  For-loop bodies and regular iterators are different in terms of block-args!
         switch (node.getNodeType()) {
             case MULTIPLEASGN19NODE: {
                 ListNode sourceArray = ((MultipleAsgn19Node) node).getPre();
                 int i = 0;
                 for (Node an: sourceArray.childNodes()) {
                     // Use 1.8 mode version for this
                     buildBlockArgsAssignment(an, s, null, i, false);
                     i++;
                 }
                 break;
             }
             default:
                 throw new NotCompilableException("Can't build assignment node: " + node);
         }
     }
 
     // This method is called to build arguments for a block!
     public void buildBlockArgsAssignment(Node node, IRScope s, Operand argsArray, int argIndex, boolean isSplat) {
         Variable v;
         switch (node.getNodeType()) {
             case ATTRASSIGNNODE:
                 v = s.createTemporaryVariable();
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 buildAttrAssignAssignment(node, s, v);
                 break;
             case DASGNNODE: {
                 DAsgnNode dynamicAsgn = (DAsgnNode) node;
                 v = getBlockArgVariable(s, dynamicAsgn.getName(), dynamicAsgn.getDepth());
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 break;
             }
             case CLASSVARASGNNODE:
                 v = s.createTemporaryVariable();
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 addInstr(s, new PutClassVariableInstr(classVarDefinitionContainer(s), ((ClassVarAsgnNode)node).getName(), v));
                 break;
             case CLASSVARDECLNODE:
                 v = s.createTemporaryVariable();
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 addInstr(s, new PutClassVariableInstr(classVarDeclarationContainer(s), ((ClassVarDeclNode)node).getName(), v));
                 break;
             case CONSTDECLNODE:
                 v = s.createTemporaryVariable();
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 buildConstDeclAssignment((ConstDeclNode) node, s, v);
                 break;
             case GLOBALASGNNODE:
                 v = s.createTemporaryVariable();
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 addInstr(s, new PutGlobalVarInstr(((GlobalAsgnNode)node).getName(), v));
                 break;
             case INSTASGNNODE:
                 v = s.createTemporaryVariable();
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 // NOTE: if 's' happens to the a class, this is effectively an assignment of a class instance variable
                 addInstr(s, new PutFieldInstr(s.getSelf(), ((InstAsgnNode)node).getName(), v));
                 break;
             case LOCALASGNNODE: {
                 LocalAsgnNode localVariable = (LocalAsgnNode) node;
                 int depth = localVariable.getDepth();
                 v = getBlockArgVariable(s, localVariable.getName(), depth);
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 break;
             }
             case ZEROARGNODE:
                 throw new NotCompilableException("Shouldn't get here; zeroarg does not do assignment: " + node);
             default:
                 buildVersionSpecificBlockArgsAssignment(node, s);
         }
     }
 
     public Operand buildAlias(final AliasNode alias, IRScope s) {
         Operand newName = build(alias.getNewName(), s);
         Operand oldName = build(alias.getOldName(), s);
         addInstr(s, new AliasInstr(newName, oldName));
 
         return manager.getNil();
     }
 
     // Translate "ret = (a && b)" --> "ret = (a ? b : false)" -->
     //
     //    v1 = -- build(a) --
     //       OPT: ret can be set to v1, but effectively v1 is false if we take the branch to L.
     //            while this info can be inferred by using attributes, why bother if we can do this?
     //    ret = v1
     //    beq(v1, false, L)
     //    v2 = -- build(b) --
     //    ret = v2
     // L:
     //
     public Operand buildAnd(final AndNode andNode, IRScope s) {
         if (andNode.getFirstNode().getNodeType().alwaysTrue()) {
             // build first node (and ignore its result) and then second node
             build(andNode.getFirstNode(), s);
             return build(andNode.getSecondNode(), s);
         } else if (andNode.getFirstNode().getNodeType().alwaysFalse()) {
             // build first node only and return its value
             return build(andNode.getFirstNode(), s);
         } else {
             Label    l   = s.getNewLabel();
             Operand  v1  = build(andNode.getFirstNode(), s);
             Variable ret = getValueInTemporaryVariable(s, v1);
             addInstr(s, BEQInstr.create(v1, manager.getFalse(), l));
             Operand  v2  = build(andNode.getSecondNode(), s);
             addInstr(s, new CopyInstr(ret, v2));
             addInstr(s, new LabelInstr(l));
             return ret;
         }
     }
 
     public Operand buildArray(Node node, IRScope s) {
         List<Operand> elts = new ArrayList<>();
         for (Node e: node.childNodes())
             elts.add(build(e, s));
 
         return copyAndReturnValue(s, new Array(elts));
     }
 
     public Operand buildArgsCat(final ArgsCatNode argsCatNode, IRScope s) {
         Operand v1 = build(argsCatNode.getFirstNode(), s);
         Operand v2 = build(argsCatNode.getSecondNode(), s);
         Variable res = s.createTemporaryVariable();
         addInstr(s, new BuildCompoundArrayInstr(res, v1, v2, false));
         return res;
     }
 
     public Operand buildArgsPush(final ArgsPushNode node, IRScope s) {
         Operand lhs = build(node.getFirstNode(), s);
         Operand rhs = build(node.getSecondNode(), s);
 
         return addResultInstr(s, new BuildCompoundArrayInstr(s.createTemporaryVariable(), lhs, rhs, true));
     }
 
     private Operand buildAttrAssign(final AttrAssignNode attrAssignNode, IRScope s) {
         Operand obj = build(attrAssignNode.getReceiverNode(), s);
         List<Operand> args = new ArrayList<>();
         Node argsNode = attrAssignNode.getArgsNode();
         Operand lastArg = (argsNode == null) ? manager.getNil() : buildAttrAssignCallArgs(args, argsNode, s);
         addInstr(s, AttrAssignInstr.create(obj, attrAssignNode.getName(), args.toArray(new Operand[args.size()])));
         return lastArg;
     }
 
     public Operand buildAttrAssignAssignment(Node node, IRScope s, Operand value) {
         final AttrAssignNode attrAssignNode = (AttrAssignNode) node;
         Operand obj = build(attrAssignNode.getReceiverNode(), s);
         Operand[] args = setupCallArgs(attrAssignNode.getArgsNode(), s);
         args = addArg(args, value);
         addInstr(s, AttrAssignInstr.create(obj, attrAssignNode.getName(), args));
         return value;
     }
 
     public Operand buildBackref(BackRefNode node, IRScope s) {
         // SSS FIXME: Required? Verify with Tom/Charlie
         return copyAndReturnValue(s, new Backref(node.getType()));
     }
 
     public Operand buildBegin(BeginNode beginNode, IRScope s) {
         return build(beginNode.getBodyNode(), s);
     }
 
     public Operand buildBignum(BignumNode node) {
         // SSS: Since bignum literals are effectively interned objects, no need to copyAndReturnValue(...)
         // Or is this a premature optimization?
         return new Bignum(node.getValue());
     }
 
     public Operand buildBlock(BlockNode node, IRScope s) {
         Operand retVal = null;
         for (Node child : node.childNodes()) {
             retVal = build(child, s);
         }
 
         // Value of the last expression in the block
         return retVal;
     }
 
     public Operand buildBreak(BreakNode breakNode, IRScope s) {
         IRLoop currLoop = getCurrentLoop();
 
         Operand rv = build(breakNode.getValueNode(), s);
         // If we have ensure blocks, have to run those first!
         if (!activeEnsureBlockStack.empty()) emitEnsureBlocks(s, currLoop);
         else if (!activeRescueBlockStack.empty()) activeRescueBlockStack.peek().restoreException(this, s, currLoop);
 
         if (currLoop != null) {
             addInstr(s, new CopyInstr(currLoop.loopResult, rv));
             addInstr(s, new JumpInstr(currLoop.loopEndLabel));
         } else {
             if (s instanceof IRClosure) {
                 // This lexical scope value is only used (and valid) in regular block contexts.
                 // If this instruction is executed in a Proc or Lambda context, the lexical scope value is useless.
                 IRScope returnScope = s.getLexicalParent();
                 // In 1.9 and later modes, no breaks from evals
                 if (s instanceof IREvalScript || returnScope == null) addInstr(s, new ThrowExceptionInstr(IRException.BREAK_LocalJumpError));
                 else addInstr(s, new BreakInstr(rv, returnScope.getName()));
             } else {
                 // We are not in a closure or a loop => bad break instr!
                 addInstr(s, new ThrowExceptionInstr(IRException.BREAK_LocalJumpError));
             }
         }
 
         // Once the break instruction executes, control exits this scope
         return U_NIL;
     }
 
     private void handleNonlocalReturnInMethod(IRScope s) {
         Label rBeginLabel = s.getNewLabel();
         Label rEndLabel   = s.getNewLabel();
         Label gebLabel    = s.getNewLabel();
 
         // Protect the entire body as it exists now with the global ensure block
         //
         // Add label and marker instruction in reverse order to the beginning
         // so that the label ends up being the first instr.
         addInstrAtBeginning(s, new ExceptionRegionStartMarkerInstr(gebLabel));
         addInstrAtBeginning(s, new LabelInstr(rBeginLabel));
         addInstr(s, new ExceptionRegionEndMarkerInstr());
 
         // Receive exceptions (could be anything, but the handler only processes IRReturnJumps)
         addInstr(s, new LabelInstr(gebLabel));
         Variable exc = s.createTemporaryVariable();
         addInstr(s, new ReceiveJRubyExceptionInstr(exc));
 
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             addInstr(s, new TraceInstr(RubyEvent.RETURN, s.getName(), s.getFileName(), -1));
         }
 
         // Handle break using runtime helper
         // --> IRRuntimeHelpers.handleNonlocalReturn(scope, bj, blockType)
         Variable ret = s.createTemporaryVariable();
         addInstr(s, new RuntimeHelperCall(ret, HANDLE_NONLOCAL_RETURN, new Operand[]{exc} ));
         addInstr(s, new ReturnInstr(ret));
 
         // End
         addInstr(s, new LabelInstr(rEndLabel));
     }
 
     private Operand receiveBreakException(IRScope s, Operand block, CodeBlock codeBlock) {
         // Check if we have to handle a break
         if (block == null ||
             !(block instanceof WrappedIRClosure) ||
             !(((WrappedIRClosure)block).getClosure()).flags.contains(IRFlags.HAS_BREAK_INSTRS)) {
             // No protection needed -- add the call and return
             return codeBlock.run();
         }
 
         Label rBeginLabel = s.getNewLabel();
         Label rEndLabel   = s.getNewLabel();
         Label rescueLabel = s.getNewLabel();
 
         // Protected region
         addInstr(s, new LabelInstr(rBeginLabel));
         addInstr(s, new ExceptionRegionStartMarkerInstr(rescueLabel));
         Variable callResult = (Variable)codeBlock.run();
         addInstr(s, new JumpInstr(rEndLabel));
         addInstr(s, new ExceptionRegionEndMarkerInstr());
 
         // Receive exceptions (could be anything, but the handler only processes IRBreakJumps)
         addInstr(s, new LabelInstr(rescueLabel));
         Variable exc = s.createTemporaryVariable();
         addInstr(s, new ReceiveJRubyExceptionInstr(exc));
 
         // Handle break using runtime helper
         // --> IRRuntimeHelpers.handlePropagatedBreak(context, scope, bj, blockType)
         addInstr(s, new RuntimeHelperCall(callResult, HANDLE_PROPAGATE_BREAK, new Operand[]{exc} ));
 
         // End
         addInstr(s, new LabelInstr(rEndLabel));
 
         return callResult;
     }
 
     // Wrap call in a rescue handler that catches the IRBreakJump
     private void receiveBreakException(final IRScope s, Operand block, final CallInstr callInstr) {
         receiveBreakException(s, block, new CodeBlock() { public Operand run() { addInstr(s, callInstr); return callInstr.getResult(); } });
     }
 
     public Operand buildCall(CallNode callNode, IRScope s) {
         Node          callArgsNode = callNode.getArgsNode();
         Node          receiverNode = callNode.getReceiverNode();
 
         // check for "string".freeze
         if (receiverNode instanceof StrNode && callNode.getName().equals("freeze")) {
             // frozen string optimization
             return new FrozenString(((StrNode)receiverNode).getValue());
         }
 
         // Though you might be tempted to move this build into the CallInstr as:
         //    new Callinstr( ... , build(receiverNode, s), ...)
         // that is incorrect IR because the receiver has to be built *before* call arguments are built
         // to preserve expected code execution order
         Operand       receiver     = build(receiverNode, s);
         Operand[] args         = setupCallArgs(callArgsNode, s);
         Operand       block        = setupCallClosure(callNode.getIterNode(), s);
         Variable      callResult   = s.createTemporaryVariable();
         CallInstr     callInstr    = CallInstr.create(callResult, callNode.getName(), receiver, args, block);
 
         // This is to support the ugly Proc.new with no block, which must see caller's frame
         if (
                 callNode.getName().equals("new") &&
                 receiverNode instanceof ConstNode &&
                 ((ConstNode)receiverNode).getName().equals("Proc")) {
             callInstr.setProcNew(true);
         }
 
         receiveBreakException(s, block, callInstr);
         return callResult;
     }
 
     public Operand buildCase(CaseNode caseNode, IRScope s) {
         // get the incoming case value
         Operand value = build(caseNode.getCaseNode(), s);
 
         // This is for handling case statements without a value (see example below)
         //   case
         //     when true <blah>
         //     when false <blah>
         //   end
         if (value == null) value = UndefinedValue.UNDEFINED;
 
         Label     endLabel  = s.getNewLabel();
         boolean   hasElse   = (caseNode.getElseNode() != null);
         Label     elseLabel = s.getNewLabel();
         Variable  result    = s.createTemporaryVariable();
 
         List<Label> labels = new ArrayList<>();
         Map<Label, Node> bodies = new HashMap<>();
 
         // build each "when"
         for (Node aCase : caseNode.getCases().childNodes()) {
             WhenNode whenNode = (WhenNode)aCase;
             Label bodyLabel = s.getNewLabel();
 
             Variable eqqResult = s.createTemporaryVariable();
             labels.add(bodyLabel);
             Operand v1, v2;
             if (whenNode.getExpressionNodes() instanceof DNode) {
                 // DNode produces a proper result, so we don't want the special ListNode handling below
                 // FIXME: This is obviously gross, and we need a better way to filter out non-expression ListNode here
                 // See GH #2423
                 s.addInstr(new EQQInstr(eqqResult, build(whenNode.getExpressionNodes(), s), value));
                 v1 = eqqResult;
                 v2 = manager.getTrue();
             } else if (whenNode.getExpressionNodes() instanceof ListNode) {
                 // Note about refactoring:
                 // - BEQInstr has a quick implementation when the second operand is a boolean literal
                 //   If it can be fixed to do this even on the first operand, we can switch around
                 //   v1 and v2 in the UndefinedValue scenario and DRY out this code.
                 // - Even with this asymmetric implementation of BEQInstr, you might be tempted to
                 //   switch around v1 and v2 in the else case.  But, that is equivalent to this Ruby code change:
                 //      (v1 == value) instead of (value == v1)
                 //   It seems that they should be identical, but the first one is v1.==(value) and the second one is
                 //   value.==(v1).  This is just fine *if* the Ruby programmer has implemented an algebraically
                 //   symmetric "==" method on those objects.  If not, then, the results might be unexpected where the
                 //   code (intentionally or otherwise) relies on this asymmetry of "==".  While it could be argued
                 //   that this a Ruby code bug, we will just try to preserve the order of the == check as it appears
                 //   in the Ruby code.
                 if (value == UndefinedValue.UNDEFINED)  {
                     v1 = build(whenNode.getExpressionNodes(), s);
                     v2 = manager.getTrue();
                 } else {
                     v1 = value;
                     v2 = build(whenNode.getExpressionNodes(), s);
                 }
             } else {
                 addInstr(s, new EQQInstr(eqqResult, build(whenNode.getExpressionNodes(), s), value));
                 v1 = eqqResult;
                 v2 = manager.getTrue();
             }
             addInstr(s, BEQInstr.create(v1, v2, bodyLabel));
 
             // SSS FIXME: This doesn't preserve original order of when clauses.  We could consider
             // preserving the order (or maybe not, since we would have to sort the constants first
             // in any case) for outputting jump tables in certain situations.
             //
             // add body to map for emitting later
             bodies.put(bodyLabel, whenNode.getBodyNode());
         }
 
         // Jump to else in case nothing matches!
         addInstr(s, new JumpInstr(elseLabel));
 
         // Build "else" if it exists
         if (hasElse) {
             labels.add(elseLabel);
             bodies.put(elseLabel, caseNode.getElseNode());
         }
 
         // Now, emit bodies while preserving when clauses order
         for (Label whenLabel: labels) {
             addInstr(s, new LabelInstr(whenLabel));
             Operand bodyValue = build(bodies.get(whenLabel), s);
             // bodyValue can be null if the body ends with a return!
             if (bodyValue != null) {
                 // SSS FIXME: Do local optimization of break results (followed by a copy & jump) to short-circuit the jump right away
                 // rather than wait to do it during an optimization pass when a dead jump needs to be removed.  For this, you have
                 // to look at what the last generated instruction was.
                 addInstr(s, new CopyInstr(result, bodyValue));
                 addInstr(s, new JumpInstr(endLabel));
             }
         }
 
         if (!hasElse) {
             addInstr(s, new LabelInstr(elseLabel));
             addInstr(s, new CopyInstr(result, manager.getNil()));
             addInstr(s, new JumpInstr(endLabel));
         }
 
         // Close it out
         addInstr(s, new LabelInstr(endLabel));
 
         return result;
     }
 
     /**
      * Build a new class and add it to the current scope (s).
      */
     public Operand buildClass(ClassNode classNode, IRScope s) {
         Node superNode = classNode.getSuperNode();
         Colon3Node cpath = classNode.getCPath();
         Operand superClass = (superNode == null) ? null : build(superNode, s);
         String className = cpath.getName();
         Operand container = getContainerFromCPath(cpath, s);
         IRClassBody body = new IRClassBody(manager, s, className, classNode.getPosition().getLine(), classNode.getScope());
         Variable classVar = addResultInstr(s, new DefineClassInstr(s.createTemporaryVariable(), body, container, superClass));
 
         return buildModuleOrClassBody(s, classVar, body, classNode.getBodyNode(), classNode.getPosition().getLine());
     }
 
     // class Foo; class << self; end; end
     // Here, the class << self declaration is in Foo's body.
     // Foo is the class in whose context this is being defined.
     public Operand buildSClass(SClassNode sclassNode, IRScope s) {
         Operand receiver = build(sclassNode.getReceiverNode(), s);
         IRModuleBody body = new IRMetaClassBody(manager, s, manager.getMetaClassName(), sclassNode.getPosition().getLine(), sclassNode.getScope());
         Variable sClassVar = addResultInstr(s, new DefineMetaClassInstr(s.createTemporaryVariable(), receiver, body));
 
         return buildModuleOrClassBody(s, sClassVar, body, sclassNode.getBodyNode(), sclassNode.getPosition().getLine());
     }
 
     // @@c
     public Operand buildClassVar(ClassVarNode node, IRScope s) {
         Variable ret = s.createTemporaryVariable();
         addInstr(s, new GetClassVariableInstr(ret, classVarDefinitionContainer(s), node.getName()));
         return ret;
     }
 
     // Add the specified result instruction to the scope and return its result variable.
     private Variable addResultInstr(IRScope s, ResultInstr instr) {
         addInstr(s, (Instr) instr);
 
         return instr.getResult();
     }
 
     // ClassVarAsgn node is assignment within a method/closure scope
     //
     // def foo
     //   @@c = 1
     // end
     public Operand buildClassVarAsgn(final ClassVarAsgnNode classVarAsgnNode, IRScope s) {
         Operand val = build(classVarAsgnNode.getValueNode(), s);
         addInstr(s, new PutClassVariableInstr(classVarDefinitionContainer(s), classVarAsgnNode.getName(), val));
         return val;
     }
 
     // ClassVarDecl node is assignment outside method/closure scope (top-level, class, module)
     //
     // class C
     //   @@c = 1
     // end
     public Operand buildClassVarDecl(final ClassVarDeclNode classVarDeclNode, IRScope s) {
         Operand val = build(classVarDeclNode.getValueNode(), s);
         addInstr(s, new PutClassVariableInstr(classVarDeclarationContainer(s), classVarDeclNode.getName(), val));
         return val;
     }
 
     public Operand classVarDeclarationContainer(IRScope s) {
         return classVarContainer(s, true);
     }
 
     public Operand classVarDefinitionContainer(IRScope s) {
         return classVarContainer(s, false);
     }
 
     // SSS FIXME: This feels a little ugly.  Is there a better way of representing this?
     public Operand classVarContainer(IRScope s, boolean declContext) {
         /* -------------------------------------------------------------------------------
          * We are looking for the nearest enclosing scope that is a non-singleton class body
          * without running into an eval-scope in between.
          *
          * Stop lexical scope walking at an eval script boundary.  Evals are essentially
          * a way for a programmer to splice an entire tree of lexical scopes at the point
          * where the eval happens.  So, when we hit an eval-script boundary at compile-time,
          * defer scope traversal to when we know where this scope has been spliced in.
          * ------------------------------------------------------------------------------- */
         int n = 0;
         IRScope cvarScope = s;
         while (cvarScope != null && !(cvarScope instanceof IREvalScript) && !cvarScope.isNonSingletonClassBody()) {
             cvarScope = cvarScope.getLexicalParent();
             n++;
         }
 
         if ((cvarScope != null) && cvarScope.isNonSingletonClassBody()) {
             return ScopeModule.ModuleFor(n);
         } else {
             return addResultInstr(s, new GetClassVarContainerModuleInstr(s.createTemporaryVariable(),
                     s.getCurrentScopeVariable(), declContext ? null : s.getSelf()));
         }
     }
 
     public Operand buildConstDecl(ConstDeclNode node, IRScope s) {
         return buildConstDeclAssignment(node, s, build(node.getValueNode(), s));
     }
 
     private Operand findContainerModule(IRScope s) {
         int nearestModuleBodyDepth = s.getNearestModuleReferencingScopeDepth();
         return (nearestModuleBodyDepth == -1) ? s.getCurrentModuleVariable() : ScopeModule.ModuleFor(nearestModuleBodyDepth);
     }
 
     private Operand startingSearchScope(IRScope s) {
         int nearestModuleBodyDepth = s.getNearestModuleReferencingScopeDepth();
         return nearestModuleBodyDepth == -1 ? s.getCurrentScopeVariable() : CurrentScope.ScopeFor(nearestModuleBodyDepth);
     }
 
     public Operand buildConstDeclAssignment(ConstDeclNode constDeclNode, IRScope s, Operand val) {
         Node constNode = constDeclNode.getConstNode();
 
         if (constNode == null) {
             addInstr(s, new PutConstInstr(findContainerModule(s), constDeclNode.getName(), val));
         } else if (constNode.getNodeType() == NodeType.COLON2NODE) {
             Operand module = build(((Colon2Node) constNode).getLeftNode(), s);
             addInstr(s, new PutConstInstr(module, constDeclNode.getName(), val));
         } else { // colon3, assign in Object
             addInstr(s, new PutConstInstr(new ObjectClass(), constDeclNode.getName(), val));
         }
 
         return val;
     }
 
     private void genInheritanceSearchInstrs(IRScope s, Operand startingModule, Variable constVal, Label foundLabel, boolean noPrivateConstants, String name) {
         addInstr(s, new InheritanceSearchConstInstr(constVal, startingModule, name, noPrivateConstants));
         addInstr(s, BNEInstr.create(constVal, UndefinedValue.UNDEFINED, foundLabel));
         addInstr(s, new ConstMissingInstr(constVal, startingModule, name));
         addInstr(s, new LabelInstr(foundLabel));
     }
 
     private Operand searchConstInInheritanceHierarchy(IRScope s, Operand startingModule, String name) {
         Variable constVal = s.createTemporaryVariable();
         genInheritanceSearchInstrs(s, startingModule, constVal, s.getNewLabel(), true, name);
         return constVal;
     }
 
     private Operand searchConst(IRScope s, String name) {
         final boolean noPrivateConstants = false;
         Variable v = s.createTemporaryVariable();
 /**
  * SSS FIXME: Went back to a single instruction for now.
  *
  * Do not split search into lexical-search, inheritance-search, and const-missing instrs.
  *
         Label foundLabel = s.getNewLabel();
         addInstr(s, new LexicalSearchConstInstr(v, startingSearchScope(s), name));
         addInstr(s, BNEInstr.create(v, UndefinedValue.UNDEFINED, foundLabel));
         genInheritanceSearchInstrs(s, findContainerModule(startingScope), v, foundLabel, noPrivateConstants, name);
 **/
         addInstr(s, new SearchConstInstr(v, name, startingSearchScope(s), noPrivateConstants));
         return v;
     }
 
     public Operand buildColon2(final Colon2Node iVisited, IRScope s) {
         Node leftNode = iVisited.getLeftNode();
         final String name = iVisited.getName();
 
         // Colon2ImplicitNode
         if (leftNode == null) return searchConst(s, name);
 
         // Colon2ConstNode
         // 1. Load the module first (lhs of node)
         // 2. Then load the constant from the module
         Operand module = build(leftNode, s);
         return searchConstInInheritanceHierarchy(s, module, name);
     }
 
     public Operand buildColon3(Colon3Node node, IRScope s) {
         return searchConstInInheritanceHierarchy(s, new ObjectClass(), node.getName());
     }
 
     public Operand buildComplex(ComplexNode node, IRScope s) {
         return new Complex((ImmutableLiteral) build(node.getNumber(), s));
     }
 
     interface CodeBlock {
         public Operand run();
     }
 
     private Operand protectCodeWithRescue(IRScope s, CodeBlock protectedCode, CodeBlock rescueBlock) {
         // This effectively mimics a begin-rescue-end code block
         // Except this catches all exceptions raised by the protected code
 
         Variable rv = s.createTemporaryVariable();
         Label rBeginLabel = s.getNewLabel();
         Label rEndLabel   = s.getNewLabel();
         Label rescueLabel = s.getNewLabel();
 
         // Protected region code
         addInstr(s, new LabelInstr(rBeginLabel));
         addInstr(s, new ExceptionRegionStartMarkerInstr(rescueLabel));
         Object v1 = protectedCode.run(); // YIELD: Run the protected code block
         addInstr(s, new CopyInstr(rv, (Operand)v1));
         addInstr(s, new JumpInstr(rEndLabel));
         addInstr(s, new ExceptionRegionEndMarkerInstr());
 
         // SSS FIXME: Create an 'Exception' operand type to eliminate the constant lookup below
         // We could preload a set of constant objects that are preloaded at boot time and use them
         // directly in IR when we know there is no lookup involved.
         //
         // new Operand type: CachedClass(String name)?
         //
         // Some candidates: Exception, StandardError, Fixnum, Object, Boolean, etc.
         // So, when they are referenced, they are fetched directly from the runtime object
         // which probably already has cached references to these constants.
         //
         // But, unsure if this caching is safe ... so, just an idea here for now.
 
         // Rescue code
         Label caughtLabel = s.getNewLabel();
         Variable exc = s.createTemporaryVariable();
         Variable excType = s.createTemporaryVariable();
 
         // Receive 'exc' and verify that 'exc' is of ruby-type 'Exception'
         addInstr(s, new LabelInstr(rescueLabel));
         addInstr(s, new ReceiveRubyExceptionInstr(exc));
         addInstr(s, new InheritanceSearchConstInstr(excType, new ObjectClass(), "Exception", false));
         outputExceptionCheck(s, excType, exc, caughtLabel);
 
         // Fall-through when the exc !== Exception; rethrow 'exc'
         addInstr(s, new ThrowExceptionInstr(exc));
 
         // exc === Exception; Run the rescue block
         addInstr(s, new LabelInstr(caughtLabel));
         Object v2 = rescueBlock.run(); // YIELD: Run the protected code block
         if (v2 != null) addInstr(s, new CopyInstr(rv, manager.getNil()));
 
         // End
         addInstr(s, new LabelInstr(rEndLabel));
 
         return rv;
     }
 
     public Operand buildGetDefinition(Node node, final IRScope scope) {
         node = skipOverNewlines(scope, node);
 
         // FIXME: Do we still have MASGN and MASGN19?
         switch (node.getNodeType()) {
         case CLASSVARASGNNODE: case CLASSVARDECLNODE: case CONSTDECLNODE:
         case DASGNNODE: case GLOBALASGNNODE: case LOCALASGNNODE: case MULTIPLEASGNNODE:
         case MULTIPLEASGN19NODE: case OPASGNNODE: case OPASGNANDNODE: case OPASGNORNODE:
         case OPELEMENTASGNNODE: case INSTASGNNODE:
             return new ConstantStringLiteral("assignment");
         case ORNODE: case ANDNODE:
             return new ConstantStringLiteral("expression");
         case FALSENODE:
             return new ConstantStringLiteral("false");
         case LOCALVARNODE: case DVARNODE:
             return new ConstantStringLiteral("local-variable");
         case MATCH2NODE: case MATCH3NODE:
             return new ConstantStringLiteral("method");
         case NILNODE:
             return new ConstantStringLiteral("nil");
         case SELFNODE:
             return new ConstantStringLiteral("self");
         case TRUENODE:
             return new ConstantStringLiteral("true");
         case DREGEXPNODE: case DSTRNODE: {
             final Node dNode = node;
 
             // protected code
             CodeBlock protectedCode = new CodeBlock() {
                 public Operand run() {
                     build(dNode, scope);
                     // always an expression as long as we get through here without an exception!
                     return new ConstantStringLiteral("expression");
                 }
             };
             // rescue block
             CodeBlock rescueBlock = new CodeBlock() {
                 public Operand run() { return manager.getNil(); } // Nothing to do if we got an exception
             };
 
             // Try verifying definition, and if we get an JumpException exception, process it with the rescue block above
             Operand v = protectCodeWithRescue(scope, protectedCode, rescueBlock);
             Label doneLabel = scope.getNewLabel();
             Variable tmpVar = getValueInTemporaryVariable(scope, v);
             addInstr(scope, BNEInstr.create(tmpVar, manager.getNil(), doneLabel));
             addInstr(scope, new CopyInstr(tmpVar, new ConstantStringLiteral("expression")));
             addInstr(scope, new LabelInstr(doneLabel));
 
             return tmpVar;
         }
         case ARRAYNODE: { // If all elts of array are defined the array is as well
             ArrayNode array = (ArrayNode) node;
             Label undefLabel = scope.getNewLabel();
             Label doneLabel = scope.getNewLabel();
 
             Variable tmpVar = scope.createTemporaryVariable();
             for (Node elt: array.childNodes()) {
                 Operand result = buildGetDefinition(elt, scope);
 
                 addInstr(scope, BEQInstr.create(result, manager.getNil(), undefLabel));
             }
 
             addInstr(scope, new CopyInstr(tmpVar, new ConstantStringLiteral("expression")));
             addInstr(scope, new JumpInstr(doneLabel));
             addInstr(scope, new LabelInstr(undefLabel));
             addInstr(scope, new CopyInstr(tmpVar, manager.getNil()));
             addInstr(scope, new LabelInstr(doneLabel));
 
             return tmpVar;
         }
         case BACKREFNODE:
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_BACKREF,
                     Operand.EMPTY_ARRAY));
         case GLOBALVARNODE:
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_GLOBAL,
                     new Operand[] { new StringLiteral(((GlobalVarNode) node).getName()) }));
         case NTHREFNODE: {
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_NTH_REF,
                     new Operand[] { new Fixnum(((NthRefNode) node).getMatchNumber()) }));
         }
         case INSTVARNODE:
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_INSTANCE_VAR,
                     new Operand[] { scope.getSelf(), new StringLiteral(((InstVarNode) node).getName()) }));
         case CLASSVARNODE:
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_CLASS_VAR,
                     new Operand[]{classVarDefinitionContainer(scope), new StringLiteral(((ClassVarNode) node).getName())}));
         case SUPERNODE: {
             Label undefLabel = scope.getNewLabel();
             Variable tmpVar  = addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_SUPER,
                     new Operand[] { scope.getSelf() }));
             addInstr(scope, BEQInstr.create(tmpVar, manager.getNil(), undefLabel));
             Operand superDefnVal = buildGetArgumentDefinition(((SuperNode) node).getArgsNode(), scope, "super");
             return buildDefnCheckIfThenPaths(scope, undefLabel, superDefnVal);
         }
         case VCALLNODE:
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_METHOD,
                     new Operand[] { scope.getSelf(), new StringLiteral(((VCallNode) node).getName()), Boolean.FALSE}));
         case YIELDNODE:
             return buildDefinitionCheck(scope, new BlockGivenInstr(scope.createTemporaryVariable(), getImplicitBlockArg(scope)), "yield");
         case ZSUPERNODE:
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_SUPER,
                     new Operand[] { scope.getSelf() } ));
         case CONSTNODE: {
             Label defLabel = scope.getNewLabel();
             Label doneLabel = scope.getNewLabel();
             Variable tmpVar  = scope.createTemporaryVariable();
             String constName = ((ConstNode) node).getName();
             addInstr(scope, new LexicalSearchConstInstr(tmpVar, startingSearchScope(scope), constName));
             addInstr(scope, BNEInstr.create(tmpVar, UndefinedValue.UNDEFINED, defLabel));
             addInstr(scope, new InheritanceSearchConstInstr(tmpVar, findContainerModule(scope), constName, false)); // SSS FIXME: should this be the current-module var or something else?
             addInstr(scope, BNEInstr.create(tmpVar, UndefinedValue.UNDEFINED, defLabel));
             addInstr(scope, new CopyInstr(tmpVar, manager.getNil()));
             addInstr(scope, new JumpInstr(doneLabel));
             addInstr(scope, new LabelInstr(defLabel));
             addInstr(scope, new CopyInstr(tmpVar, new ConstantStringLiteral("constant")));
             addInstr(scope, new LabelInstr(doneLabel));
             return tmpVar;
         }
         case COLON3NODE: case COLON2NODE: {
             // SSS FIXME: Is there a reason to do this all with low-level IR?
             // Can't this all be folded into a Java method that would be part
             // of the runtime library, which then can be used by buildDefinitionCheck method above?
             // This runtime library would be used both by the interpreter & the compiled code!
 
             final Colon3Node colon = (Colon3Node) node;
             final String name = colon.getName();
             final Variable errInfo = scope.createTemporaryVariable();
 
             // store previous exception for restoration if we rescue something
             addInstr(scope, new GetErrorInfoInstr(errInfo));
 
             CodeBlock protectedCode = new CodeBlock() {
                 public Operand run() {
                     Operand v = colon instanceof Colon2Node ?
                             build(((Colon2Node)colon).getLeftNode(), scope) : new ObjectClass();
 
                     Variable tmpVar = scope.createTemporaryVariable();
                     addInstr(scope, new RuntimeHelperCall(tmpVar, IS_DEFINED_CONSTANT_OR_METHOD, new Operand[] {v, new ConstantStringLiteral(name)}));
                     return tmpVar;
                 }
             };
 
             // rescue block
             CodeBlock rescueBlock = new CodeBlock() {
                  public Operand run() {
                  // Nothing to do -- ignore the exception, and restore stashed error info!
                  addInstr(scope, new RestoreErrorInfoInstr(errInfo));
                  return manager.getNil();
                  }
             };
 
                 // Try verifying definition, and if we get an JumpException exception, process it with the rescue block above
             return protectCodeWithRescue(scope, protectedCode, rescueBlock);
         }
         case FCALLNODE: {
             /* ------------------------------------------------------------------
              * Generate IR for:
              *    r = self/receiver
              *    mc = r.metaclass
              *    return mc.methodBound(meth) ? buildGetArgumentDefn(..) : false
              * ----------------------------------------------------------------- */
             Label undefLabel = scope.getNewLabel();
             Variable tmpVar = addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_METHOD,
                     new Operand[]{scope.getSelf(), new StringLiteral(((FCallNode) node).getName()), Boolean.FALSE}));
             addInstr(scope, BEQInstr.create(tmpVar, manager.getNil(), undefLabel));
             Operand argsCheckDefn = buildGetArgumentDefinition(((FCallNode) node).getArgsNode(), scope, "method");
             return buildDefnCheckIfThenPaths(scope, undefLabel, argsCheckDefn);
         }
         case CALLNODE: {
             final Label undefLabel = scope.getNewLabel();
             final CallNode callNode = (CallNode) node;
             Operand  receiverDefn = buildGetDefinition(callNode.getReceiverNode(), scope);
             addInstr(scope, BEQInstr.create(receiverDefn, manager.getNil(), undefLabel));
 
             // protected main block
             CodeBlock protectedCode = new CodeBlock() {
                 public Operand run() {
                     Variable tmpVar = scope.createTemporaryVariable();
                     addInstr(scope, new RuntimeHelperCall(tmpVar, IS_DEFINED_CALL,
                             new Operand[]{build(callNode.getReceiverNode(), scope), new StringLiteral(callNode.getName())}));
                     return buildDefnCheckIfThenPaths(scope, undefLabel, tmpVar);
                 }
             };
 
             // rescue block
             CodeBlock rescueBlock = new CodeBlock() {
                 public Operand run() { return manager.getNil(); } // Nothing to do if we got an exception
             };
 
             // Try verifying definition, and if we get an exception, throw it out, and return nil
             return protectCodeWithRescue(scope, protectedCode, rescueBlock);
         }
         case ATTRASSIGNNODE: {
             final Label  undefLabel = scope.getNewLabel();
             final AttrAssignNode attrAssign = (AttrAssignNode) node;
             Operand receiverDefn = buildGetDefinition(attrAssign.getReceiverNode(), scope);
             addInstr(scope, BEQInstr.create(receiverDefn, manager.getNil(), undefLabel));
 
             // protected main block
             CodeBlock protectedCode = new CodeBlock() {
                 public Operand run() {
                     /* --------------------------------------------------------------------------
                      * This basically combines checks from CALLNODE and FCALLNODE
                      *
                      * Generate IR for this sequence
                      *
                      *    1. r  = receiver
                      *    2. mc = r.metaClass
                      *    3. v  = mc.getVisibility(methodName)
                      *    4. f  = !v || v.isPrivate? || (v.isProtected? && receiver/self?.kindof(mc.getRealClass))
                      *    5. return !f && mc.methodBound(attrmethod) ? buildGetArgumentDefn(..) : false
                      *
                      * Hide the complexity of instrs 2-4 into a verifyMethodIsPublicAccessible call
                      * which can executely entirely in Java-land.  No reason to expose the guts in IR.
                      * ------------------------------------------------------------------------------ */
                     Variable tmpVar     = scope.createTemporaryVariable();
                     Operand  receiver   = build(attrAssign.getReceiverNode(), scope);
                     addInstr(scope, new RuntimeHelperCall(tmpVar, IS_DEFINED_METHOD,
                             new Operand[] { receiver, new StringLiteral(attrAssign.getName()), Boolean.TRUE }));
                     addInstr(scope, BEQInstr.create(tmpVar, manager.getNil(), undefLabel));
                     Operand argsCheckDefn = buildGetArgumentDefinition(attrAssign.getArgsNode(), scope, "assignment");
                     return buildDefnCheckIfThenPaths(scope, undefLabel, argsCheckDefn);
                 }
             };
 
             // rescue block
             CodeBlock rescueBlock = new CodeBlock() {
                 public Operand run() { return manager.getNil(); } // Nothing to do if we got an exception
             };
 
             // Try verifying definition, and if we get an JumpException exception, process it with the rescue block above
             return protectCodeWithRescue(scope, protectedCode, rescueBlock);
         }
         default:
             return new ConstantStringLiteral("expression");
         }
     }
 
     protected Variable buildDefnCheckIfThenPaths(IRScope s, Label undefLabel, Operand defVal) {
         Label defLabel = s.getNewLabel();
         Variable tmpVar = getValueInTemporaryVariable(s, defVal);
         addInstr(s, new JumpInstr(defLabel));
         addInstr(s, new LabelInstr(undefLabel));
         addInstr(s, new CopyInstr(tmpVar, manager.getNil()));
         addInstr(s, new LabelInstr(defLabel));
         return tmpVar;
     }
 
     protected Variable buildDefinitionCheck(IRScope s, ResultInstr definedInstr, String definedReturnValue) {
         Label undefLabel = s.getNewLabel();
         addInstr(s, (Instr) definedInstr);
         addInstr(s, BEQInstr.create(definedInstr.getResult(), manager.getFalse(), undefLabel));
         return buildDefnCheckIfThenPaths(s, undefLabel, new ConstantStringLiteral(definedReturnValue));
     }
 
     public Operand buildGetArgumentDefinition(final Node node, IRScope s, String type) {
         if (node == null) return new StringLiteral(type);
@@ -2272,1282 +2272,1282 @@ public class IRBuilder {
         // Create a variable to hold the flip state
         IRScope nearestNonClosure = s.getNearestFlipVariableScope();
         Variable flipState = nearestNonClosure.getNewFlipStateVariable();
         nearestNonClosure.initFlipStateVariable(flipState, s1);
         if (s instanceof IRClosure) {
             // Clone the flip variable to be usable at the proper-depth.
             int n = 0;
             IRScope x = s;
             while (!x.isFlipScope()) {
                 n++;
                 x = x.getLexicalParent();
             }
             if (n > 0) flipState = ((LocalVariable)flipState).cloneForDepth(n);
         }
 
         // Variables and labels needed for the code
         Variable returnVal = s.createTemporaryVariable();
         Label    s2Label   = s.getNewLabel();
         Label    doneLabel = s.getNewLabel();
 
         // Init
         addInstr(s, new CopyInstr(returnVal, manager.getFalse()));
 
         // Are we in state 1?
         addInstr(s, BNEInstr.create(flipState, s1, s2Label));
 
         // ----- Code for when we are in state 1 -----
         Operand s1Val = build(flipNode.getBeginNode(), s);
         addInstr(s, BNEInstr.create(s1Val, manager.getTrue(), s2Label));
 
         // s1 condition is true => set returnVal to true & move to state 2
         addInstr(s, new CopyInstr(returnVal, manager.getTrue()));
         addInstr(s, new CopyInstr(flipState, s2));
 
         // Check for state 2
         addInstr(s, new LabelInstr(s2Label));
 
         // For exclusive ranges/flips, we dont evaluate s2's condition if s1's condition was satisfied
         if (flipNode.isExclusive()) addInstr(s, BEQInstr.create(returnVal, manager.getTrue(), doneLabel));
 
         // Are we in state 2?
         addInstr(s, BNEInstr.create(flipState, s2, doneLabel));
 
         // ----- Code for when we are in state 2 -----
         Operand s2Val = build(flipNode.getEndNode(), s);
         addInstr(s, new CopyInstr(returnVal, manager.getTrue()));
         addInstr(s, BNEInstr.create(s2Val, manager.getTrue(), doneLabel));
 
         // s2 condition is true => move to state 1
         addInstr(s, new CopyInstr(flipState, s1));
 
         // Done testing for s1's and s2's conditions.
         // returnVal will have the result of the flip condition
         addInstr(s, new LabelInstr(doneLabel));
 
         return returnVal;
     }
 
     public Operand buildFloat(FloatNode node) {
         // SSS: Since flaot literals are effectively interned objects, no need to copyAndReturnValue(...)
         // Or is this a premature optimization?
         return new Float(node.getValue());
     }
 
     public Operand buildFor(ForNode forNode, IRScope s) {
         Variable result = s.createTemporaryVariable();
         Operand  receiver = build(forNode.getIterNode(), s);
         Operand  forBlock = buildForIter(forNode, s);
         CallInstr callInstr = new CallInstr(CallType.NORMAL, result, "each", receiver, NO_ARGS, forBlock);
         receiveBreakException(s, forBlock, callInstr);
 
         return result;
     }
 
     public Operand buildForIter(final ForNode forNode, IRScope s) {
             // Create a new closure context
         IRClosure closure = new IRFor(manager, s, forNode.getPosition().getLine(), forNode.getScope(), Arity.procArityOf(forNode.getVarNode()), forNode.getArgumentType());
 
         // Create a new nested builder to ensure this gets its own IR builder state
         // like the ensure block stack
         IRBuilder forBuilder = newIRBuilder(manager);
 
             // Receive self
         forBuilder.addInstr(closure, new ReceiveSelfInstr(closure.getSelf()));
 
             // Build args
         Node varNode = forNode.getVarNode();
         if (varNode != null && varNode.getNodeType() != null) forBuilder.receiveBlockArgs(forNode, closure);
 
         // Set %current_scope = <current-scope>
         // Set %current_module = <current-module>
         forBuilder.addInstr(closure, new CopyInstr(closure.getCurrentScopeVariable(), CURRENT_SCOPE[0]));
         forBuilder.addInstr(closure, new CopyInstr(closure.getCurrentModuleVariable(), SCOPE_MODULE[0]));
 
         // Thread poll on entry of closure
         forBuilder.addInstr(closure, new ThreadPollInstr());
 
             // Start label -- used by redo!
         forBuilder.addInstr(closure, new LabelInstr(closure.startLabel));
 
             // Build closure body and return the result of the closure
         Operand closureRetVal = forNode.getBodyNode() == null ? manager.getNil() : forBuilder.build(forNode.getBodyNode(), closure);
         if (closureRetVal != U_NIL) { // can be null if the node is an if node with returns in both branches.
             forBuilder.addInstr(closure, new ReturnInstr(closureRetVal));
         }
 
         return new WrappedIRClosure(s.getSelf(), closure);
     }
 
     public Operand buildGlobalAsgn(GlobalAsgnNode globalAsgnNode, IRScope s) {
         Operand value = build(globalAsgnNode.getValueNode(), s);
         addInstr(s, new PutGlobalVarInstr(globalAsgnNode.getName(), value));
         return value;
     }
 
     public Operand buildGlobalVar(GlobalVarNode node, IRScope s) {
         return addResultInstr(s, new GetGlobalVariableInstr(s.createTemporaryVariable(), node.getName()));
     }
 
     public Operand buildHash(HashNode hashNode, IRScope s) {
         List<KeyValuePair<Operand, Operand>> args = new ArrayList<>();
         Operand splatKeywordArgument = null;
 
         for (KeyValuePair<Node, Node> pair: hashNode.getPairs()) {
             Node key = pair.getKey();
             Operand keyOperand;
 
             if (key == null) { // splat kwargs [e.g. foo(a: 1, **splat)] key is null and will be in last pair of hash
                 splatKeywordArgument = build(pair.getValue(), s);
                 break;
             } else {
                keyOperand = build(key, s);
             }
 
             args.add(new KeyValuePair<>(keyOperand, build(pair.getValue(), s)));
         }
 
         if (splatKeywordArgument != null) { // splat kwargs merge with any explicit kwargs
             Variable tmp = s.createTemporaryVariable();
             s.addInstr(new RuntimeHelperCall(tmp, MERGE_KWARGS, new Operand[] { splatKeywordArgument, new Hash(args)}));
             return tmp;
         } else {
             return copyAndReturnValue(s, new Hash(args));
         }
     }
 
     // Translate "r = if (cond); .. thenbody ..; else; .. elsebody ..; end" to
     //
     //     v = -- build(cond) --
     //     BEQ(v, FALSE, L1)
     //     r = -- build(thenbody) --
     //     jump L2
     // L1:
     //     r = -- build(elsebody) --
     // L2:
     //     --- r is the result of the if expression --
     //
     public Operand buildIf(final IfNode ifNode, IRScope s) {
         Node actualCondition = skipOverNewlines(s, ifNode.getCondition());
 
         Variable result;
         Label    falseLabel = s.getNewLabel();
         Label    doneLabel  = s.getNewLabel();
         Operand  thenResult;
         addInstr(s, BEQInstr.create(build(actualCondition, s), manager.getFalse(), falseLabel));
 
         boolean thenNull = false;
         boolean elseNull = false;
         boolean thenUnil = false;
         boolean elseUnil = false;
 
         // Build the then part of the if-statement
         if (ifNode.getThenBody() != null) {
             thenResult = build(ifNode.getThenBody(), s);
             if (thenResult != U_NIL) { // thenResult can be U_NIL if then-body ended with a return!
                 // SSS FIXME: Can look at the last instr and short-circuit this jump if it is a break rather
                 // than wait for dead code elimination to do it
                 result = getValueInTemporaryVariable(s, thenResult);
                 addInstr(s, new JumpInstr(doneLabel));
             } else {
                 result = s.createTemporaryVariable();
                 thenUnil = true;
             }
         } else {
             thenNull = true;
             result = addResultInstr(s, new CopyInstr(s.createTemporaryVariable(), manager.getNil()));
             addInstr(s, new JumpInstr(doneLabel));
         }
 
         // Build the else part of the if-statement
         addInstr(s, new LabelInstr(falseLabel));
         if (ifNode.getElseBody() != null) {
             Operand elseResult = build(ifNode.getElseBody(), s);
             // elseResult can be U_NIL if then-body ended with a return!
             if (elseResult != U_NIL) {
                 addInstr(s, new CopyInstr(result, elseResult));
             } else {
                 elseUnil = true;
             }
         } else {
             elseNull = true;
             addInstr(s, new CopyInstr(result, manager.getNil()));
         }
 
         if (thenNull && elseNull) {
             addInstr(s, new LabelInstr(doneLabel));
             return manager.getNil();
         } else if (thenUnil && elseUnil) {
             return U_NIL;
         } else {
             addInstr(s, new LabelInstr(doneLabel));
             return result;
         }
     }
 
     public Operand buildInstAsgn(final InstAsgnNode instAsgnNode, IRScope s) {
         Operand val = build(instAsgnNode.getValueNode(), s);
         // NOTE: if 's' happens to the a class, this is effectively an assignment of a class instance variable
         addInstr(s, new PutFieldInstr(s.getSelf(), instAsgnNode.getName(), val));
         return val;
     }
 
     public Operand buildInstVar(InstVarNode node, IRScope s) {
         return addResultInstr(s, new GetFieldInstr(s.createTemporaryVariable(), s.getSelf(), node.getName()));
     }
 
     public Operand buildIter(final IterNode iterNode, IRScope s) {
         IRClosure closure = new IRClosure(manager, s, iterNode.getPosition().getLine(), iterNode.getScope(), Arity.procArityOf(iterNode.getVarNode()), iterNode.getArgumentType());
 
         // Create a new nested builder to ensure this gets its own IR builder state
         // like the ensure block stack
         IRBuilder closureBuilder = newIRBuilder(manager);
 
         // Receive self
         closureBuilder.addInstr(closure, new ReceiveSelfInstr(closure.getSelf()));
 
         // Build args
         if (iterNode.getVarNode().getNodeType() != null) closureBuilder.receiveBlockArgs(iterNode, closure);
 
         // Set %current_scope = <current-scope>
         // Set %current_module = <current-module>
         closureBuilder.addInstr(closure, new CopyInstr(closure.getCurrentScopeVariable(), CURRENT_SCOPE[0]));
         closureBuilder.addInstr(closure, new CopyInstr(closure.getCurrentModuleVariable(), SCOPE_MODULE[0]));
 
         // Thread poll on entry of closure
         closureBuilder.addInstr(closure, new ThreadPollInstr());
 
         // start label -- used by redo!
         closureBuilder.addInstr(closure, new LabelInstr(closure.startLabel));
 
         // Build closure body and return the result of the closure
         Operand closureRetVal = iterNode.getBodyNode() == null ? manager.getNil() : closureBuilder.build(iterNode.getBodyNode(), closure);
         if (closureRetVal != U_NIL) { // can be U_NIL if the node is an if node with returns in both branches.
             closureBuilder.addInstr(closure, new ReturnInstr(closureRetVal));
         }
 
         // Always add break/return handling even though this
         // is only required for lambdas, but we don't know at this time,
         // if this is a lambda or not.
         //
         // SSS FIXME: At a later time, see if we can optimize this and
         // do this on demand.
         closureBuilder.handleBreakAndReturnsInLambdas(closure);
 
         return new WrappedIRClosure(s.getSelf(), closure);
     }
 
     public Operand buildLiteral(LiteralNode literalNode, IRScope s) {
         return copyAndReturnValue(s, new StringLiteral(literalNode.getName()));
     }
 
     public Operand buildLocalAsgn(LocalAsgnNode localAsgnNode, IRScope s) {
         Variable var  = s.getLocalVariable(localAsgnNode.getName(), localAsgnNode.getDepth());
         Operand value = build(localAsgnNode.getValueNode(), s);
         addInstr(s, new CopyInstr(var, value));
         return value;
 
         // IMPORTANT: The return value of this method is value, not var!
         //
         // Consider this Ruby code: foo((a = 1), (a = 2))
         //
         // If we return 'value' this will get translated to:
         //    a = 1
         //    a = 2
         //    call("foo", [1,2]) <---- CORRECT
         //
         // If we return 'var' this will get translated to:
         //    a = 1
         //    a = 2
         //    call("foo", [a,a]) <---- BUGGY
         //
         // This technique only works if 'value' is an immutable value (ex: fixnum) or a variable
         // So, for Ruby code like this:
         //     def foo(x); x << 5; end;
         //     foo(a=[1,2]);
         //     p a
         // we are guaranteed that the value passed into foo and 'a' point to the same object
         // because of the use of copyAndReturnValue method for literal objects.
     }
 
     public Operand buildLocalVar(LocalVarNode node, IRScope s) {
         return s.getLocalVariable(node.getName(), node.getDepth());
     }
 
     public Operand buildMatch(MatchNode matchNode, IRScope s) {
         Operand regexp = build(matchNode.getRegexpNode(), s);
 
         return addResultInstr(s, new MatchInstr(s.createTemporaryVariable(), regexp));
     }
 
     public Operand buildMatch2(Match2Node matchNode, IRScope s) {
         Operand receiver = build(matchNode.getReceiverNode(), s);
         Operand value    = build(matchNode.getValueNode(), s);
         Variable result  = s.createTemporaryVariable();
         addInstr(s, new Match2Instr(result, receiver, value));
         if (matchNode instanceof Match2CaptureNode) {
             Match2CaptureNode m2c = (Match2CaptureNode)matchNode;
             for (int slot:  m2c.getScopeOffsets()) {
                 // Static scope scope offsets store both depth and offset
                 int depth = slot >> 16;
                 int offset = slot & 0xffff;
 
                 // For now, we'll continue to implicitly reference "$~"
                 String var = getVarNameFromScopeTree(s, depth, offset);
                 addInstr(s, new SetCapturedVarInstr(s.getLocalVariable(var, depth), result, var));
             }
         }
         return result;
     }
 
     private String getVarNameFromScopeTree(IRScope scope, int depth, int offset) {
         if (depth == 0) {
             return scope.getStaticScope().getVariables()[offset];
         }
         return getVarNameFromScopeTree(scope.getLexicalParent(), depth - 1, offset);
     }
 
     public Operand buildMatch3(Match3Node matchNode, IRScope s) {
         Operand receiver = build(matchNode.getReceiverNode(), s);
         Operand value    = build(matchNode.getValueNode(), s);
 
         return addResultInstr(s, new Match3Instr(s.createTemporaryVariable(), receiver, value));
     }
 
     private Operand getContainerFromCPath(Colon3Node cpath, IRScope s) {
         Operand container;
 
         if (cpath instanceof Colon2Node) {
             Node leftNode = ((Colon2Node) cpath).getLeftNode();
 
             if (leftNode != null) { // Foo::Bar
                 container = build(leftNode, s);
             } else { // Only name with no left-side Bar <- Note no :: on left
                 container = findContainerModule(s);
             }
         } else { //::Bar
             container = new ObjectClass();
         }
 
         return container;
     }
 
     public Operand buildModule(ModuleNode moduleNode, IRScope s) {
         Colon3Node cpath = moduleNode.getCPath();
         String moduleName = cpath.getName();
         Operand container = getContainerFromCPath(cpath, s);
         IRModuleBody body = new IRModuleBody(manager, s, moduleName, moduleNode.getPosition().getLine(), moduleNode.getScope());
         Variable moduleVar = addResultInstr(s, new DefineModuleInstr(s.createTemporaryVariable(), body, container));
 
         return buildModuleOrClassBody(s, moduleVar, body, moduleNode.getBodyNode(), moduleNode.getPosition().getLine());
     }
 
     public Operand buildMultipleAsgn(MultipleAsgnNode multipleAsgnNode, IRScope s) {
         Operand  values = build(multipleAsgnNode.getValueNode(), s);
         Variable ret = getValueInTemporaryVariable(s, values);
         buildMultipleAsgnAssignment(multipleAsgnNode, s, null, ret);
         return ret;
     }
 
     // SSS: This method is called both for regular multiple assignment as well as argument passing
     //
     // Ex: a,b,*c=v  is a regular assignment and in this case, the "values" operand will be non-null
     // Ex: { |a,b,*c| ..} is the argument passing case
     public void buildMultipleAsgnAssignment(final MultipleAsgnNode multipleAsgnNode, IRScope s, Operand argsArray, Operand values) {
         final ListNode sourceArray = multipleAsgnNode.getHeadNode();
 
         // First, build assignments for specific named arguments
         int i = 0;
         if (sourceArray != null) {
             for (Node an: sourceArray.childNodes()) {
                 if (values == null) {
                     buildBlockArgsAssignment(an, s, argsArray, i, false);
                 } else {
                     Variable rhsVal = addResultInstr(s, new ReqdArgMultipleAsgnInstr(s.createTemporaryVariable(), values, i));
                     buildAssignment(an, s, rhsVal);
                 }
                 i++;
             }
         }
 
         // First, build an assignment for a splat, if any, with the rest of the args!
         Node argsNode = multipleAsgnNode.getArgsNode();
         if (argsNode == null) {
             if (sourceArray == null) {
                 throw new NotCompilableException("Something's wrong, multiple assignment with no head or args at: " + multipleAsgnNode.getPosition());
             }
         } else if (!(argsNode instanceof StarNode)) {
             if (values != null) {
                 buildAssignment(argsNode, s,    // rest of the argument array!
                         addResultInstr(s, new RestArgMultipleAsgnInstr(s.createTemporaryVariable(), values, i)));
             } else {
                 buildBlockArgsAssignment(argsNode, s, argsArray, i, true); // rest of the argument array!
             }
         }
     }
 
     public Operand buildNewline(NewlineNode node, IRScope s) {
         return build(skipOverNewlines(s, node), s);
     }
 
     public Operand buildNext(final NextNode nextNode, IRScope s) {
         IRLoop currLoop = getCurrentLoop();
 
         Operand rv = (nextNode.getValueNode() == null) ? manager.getNil() : build(nextNode.getValueNode(), s);
 
         // If we have ensure blocks, have to run those first!
         if (!activeEnsureBlockStack.empty()) emitEnsureBlocks(s, currLoop);
         else if (!activeRescueBlockStack.empty()) activeRescueBlockStack.peek().restoreException(this, s, currLoop);
 
         if (currLoop != null) {
             // If a regular loop, the next is simply a jump to the end of the iteration
             addInstr(s, new JumpInstr(currLoop.iterEndLabel));
         } else {
             addInstr(s, new ThreadPollInstr(true));
             // If a closure, the next is simply a return from the closure!
             if (s instanceof IRClosure) addInstr(s, new ReturnInstr(rv));
             else addInstr(s, new ThrowExceptionInstr(IRException.NEXT_LocalJumpError));
         }
 
         // Once the "next instruction" (closure-return) executes, control exits this scope
         return U_NIL;
     }
 
     public Operand buildNthRef(NthRefNode nthRefNode, IRScope s) {
         return copyAndReturnValue(s, new NthRef(nthRefNode.getMatchNumber()));
     }
 
     public Operand buildNil() {
         return manager.getNil();
     }
 
     public Operand buildOpAsgn(OpAsgnNode opAsgnNode, IRScope s) {
         Label l;
         Variable readerValue = s.createTemporaryVariable();
         Variable writerValue = s.createTemporaryVariable();
 
         // get attr
         Operand  v1 = build(opAsgnNode.getReceiverNode(), s);
         addInstr(s, CallInstr.create(readerValue, opAsgnNode.getVariableName(), v1, NO_ARGS, null));
 
         // Ex: e.val ||= n
         //     e.val &&= n
         String opName = opAsgnNode.getOperatorName();
         if (opName.equals("||") || opName.equals("&&")) {
             l = s.getNewLabel();
             addInstr(s, BEQInstr.create(readerValue, opName.equals("||") ? manager.getTrue() : manager.getFalse(), l));
 
             // compute value and set it
             Operand  v2 = build(opAsgnNode.getValueNode(), s);
             addInstr(s, CallInstr.create(writerValue, opAsgnNode.getVariableNameAsgn(), v1, new Operand[] {v2}, null));
             // It is readerValue = v2.
             // readerValue = writerValue is incorrect because the assignment method
             // might return something else other than the value being set!
             addInstr(s, new CopyInstr(readerValue, v2));
             addInstr(s, new LabelInstr(l));
 
             return readerValue;
         }
         // Ex: e.val = e.val.f(n)
         else {
             // call operator
             Operand  v2 = build(opAsgnNode.getValueNode(), s);
             Variable setValue = s.createTemporaryVariable();
             addInstr(s, CallInstr.create(setValue, opAsgnNode.getOperatorName(), readerValue, new Operand[]{v2}, null));
 
             // set attr
             addInstr(s, CallInstr.create(writerValue, opAsgnNode.getVariableNameAsgn(), v1, new Operand[] {setValue}, null));
             // Returning writerValue is incorrect becuase the assignment method
             // might return something else other than the value being set!
             return setValue;
         }
     }
 
     // Translate "x &&= y" --> "x = y if is_true(x)" -->
     //
     //    x = -- build(x) should return a variable! --
     //    f = is_true(x)
     //    beq(f, false, L)
     //    x = -- build(y) --
     // L:
     //
     public Operand buildOpAsgnAnd(OpAsgnAndNode andNode, IRScope s) {
         Label    l  = s.getNewLabel();
         Operand  v1 = build(andNode.getFirstNode(), s);
         Variable result = getValueInTemporaryVariable(s, v1);
         addInstr(s, BEQInstr.create(v1, manager.getFalse(), l));
         Operand v2 = build(andNode.getSecondNode(), s);  // This does the assignment!
         addInstr(s, new CopyInstr(result, v2));
         addInstr(s, new LabelInstr(l));
         return result;
     }
 
     // "x ||= y"
     // --> "x = (is_defined(x) && is_true(x) ? x : y)"
     // --> v = -- build(x) should return a variable! --
     //     f = is_true(v)
     //     beq(f, true, L)
     //     -- build(x = y) --
     //   L:
     //
     public Operand buildOpAsgnOr(final OpAsgnOrNode orNode, IRScope s) {
         Label    l1 = s.getNewLabel();
         Label    l2 = null;
         Variable flag = s.createTemporaryVariable();
         Operand  v1;
         boolean  needsDefnCheck = orNode.getFirstNode().needsDefinitionCheck();
         if (needsDefnCheck) {
             l2 = s.getNewLabel();
             v1 = buildGetDefinition(orNode.getFirstNode(), s);
             addInstr(s, new CopyInstr(flag, v1));
             addInstr(s, BEQInstr.create(flag, manager.getNil(), l2)); // if v1 is undefined, go to v2's computation
         }
         v1 = build(orNode.getFirstNode(), s); // build of 'x'
         addInstr(s, new CopyInstr(flag, v1));
         Variable result = getValueInTemporaryVariable(s, v1);
         if (needsDefnCheck) {
             addInstr(s, new LabelInstr(l2));
         }
         addInstr(s, BEQInstr.create(flag, manager.getTrue(), l1));  // if v1 is defined and true, we are done!
         Operand v2 = build(orNode.getSecondNode(), s); // This is an AST node that sets x = y, so nothing special to do here.
         addInstr(s, new CopyInstr(result, v2));
         addInstr(s, new LabelInstr(l1));
 
         // Return value of x ||= y is always 'x'
         return result;
     }
 
     public Operand buildOpElementAsgn(OpElementAsgnNode node, IRScope s) {
         if (node.isOr()) return buildOpElementAsgnWithOr(node, s);
         if (node.isAnd()) return buildOpElementAsgnWithAnd(node, s);
 
         return buildOpElementAsgnWithMethod(node, s);
     }
 
     // Translate "a[x] ||= n" --> "a[x] = n if !is_true(a[x])"
     //
     //    tmp = build(a) <-- receiver
     //    arg = build(x) <-- args
     //    val = buildCall([], tmp, arg)
     //    f = is_true(val)
     //    beq(f, true, L)
     //    val = build(n) <-- val
     //    buildCall([]= tmp, arg, val)
     // L:
     //
     public Operand buildOpElementAsgnWithOr(OpElementAsgnNode opElementAsgnNode, IRScope s) {
         Operand array = build(opElementAsgnNode.getReceiverNode(), s);
         Label    l     = s.getNewLabel();
         Variable elt   = s.createTemporaryVariable();
         Operand[] argList = setupCallArgs(opElementAsgnNode.getArgsNode(), s);
         addInstr(s, CallInstr.create(elt, "[]", array, argList, null));
         addInstr(s, BEQInstr.create(elt, manager.getTrue(), l));
         Operand value = build(opElementAsgnNode.getValueNode(), s);
         argList = addArg(argList, value);
         addInstr(s, CallInstr.create(elt, "[]=", array, argList, null));
         addInstr(s, new CopyInstr(elt, value));
         addInstr(s, new LabelInstr(l));
         return elt;
     }
 
     // Translate "a[x] &&= n" --> "a[x] = n if is_true(a[x])"
     public Operand buildOpElementAsgnWithAnd(OpElementAsgnNode opElementAsgnNode, IRScope s) {
         Operand array = build(opElementAsgnNode.getReceiverNode(), s);
         Label    l     = s.getNewLabel();
         Variable elt   = s.createTemporaryVariable();
         Operand[] argList = setupCallArgs(opElementAsgnNode.getArgsNode(), s);
         addInstr(s, CallInstr.create(elt, "[]", array, argList, null));
         addInstr(s, BEQInstr.create(elt, manager.getFalse(), l));
         Operand value = build(opElementAsgnNode.getValueNode(), s);
 
         argList = addArg(argList, value);
         addInstr(s, CallInstr.create(elt, "[]=", array, argList, null));
         addInstr(s, new CopyInstr(elt, value));
         addInstr(s, new LabelInstr(l));
         return elt;
     }
 
     // a[i] *= n, etc.  anything that is not "a[i] &&= .. or a[i] ||= .."
     //    arr = build(a) <-- receiver
     //    arg = build(x) <-- args
     //    elt = buildCall([], arr, arg)
     //    val = build(n) <-- val
     //    val = buildCall(METH, elt, val)
     //    val = buildCall([]=, arr, arg, val)
     public Operand buildOpElementAsgnWithMethod(OpElementAsgnNode opElementAsgnNode, IRScope s) {
         Operand array = build(opElementAsgnNode.getReceiverNode(), s);
         Operand[] argList = setupCallArgs(opElementAsgnNode.getArgsNode(), s);
         Variable elt = s.createTemporaryVariable();
         addInstr(s, CallInstr.create(elt, "[]", array, argList, null)); // elt = a[args]
         Operand value = build(opElementAsgnNode.getValueNode(), s);                                       // Load 'value'
         String  operation = opElementAsgnNode.getOperatorName();
         addInstr(s, CallInstr.create(elt, operation, elt, new Operand[] { value }, null)); // elt = elt.OPERATION(value)
         // SSS: do not load the call result into 'elt' to eliminate the RAW dependency on the call
         // We already know what the result is going be .. we are just storing it back into the array
         Variable tmp = s.createTemporaryVariable();
         argList = addArg(argList, elt);
         addInstr(s, CallInstr.create(tmp, "[]=", array, argList, null));   // a[args] = elt
         return elt;
     }
 
     // Translate ret = (a || b) to ret = (a ? true : b) as follows
     //
     //    v1 = -- build(a) --
     //       OPT: ret can be set to v1, but effectively v1 is true if we take the branch to L.
     //            while this info can be inferred by using attributes, why bother if we can do this?
     //    ret = v1
     //    beq(v1, true, L)
     //    v2 = -- build(b) --
     //    ret = v2
     // L:
     //
     public Operand buildOr(final OrNode orNode, IRScope s) {
         if (orNode.getFirstNode().getNodeType().alwaysTrue()) {
             // build first node only and return true
             return build(orNode.getFirstNode(), s);
         } else if (orNode.getFirstNode().getNodeType().alwaysFalse()) {
             // build first node as non-expr and build second node
             build(orNode.getFirstNode(), s);
             return build(orNode.getSecondNode(), s);
         } else {
             Label    l   = s.getNewLabel();
             Operand  v1  = build(orNode.getFirstNode(), s);
             Variable ret = getValueInTemporaryVariable(s, v1);
             addInstr(s, BEQInstr.create(v1, manager.getTrue(), l));
             Operand  v2  = build(orNode.getSecondNode(), s);
             addInstr(s, new CopyInstr(ret, v2));
             addInstr(s, new LabelInstr(l));
             return ret;
         }
     }
 
     public Operand buildPostExe(PostExeNode postExeNode, IRScope s) {
         IRScope topLevel = s.getTopLevelScope();
         IRScope nearestLVarScope = s.getNearestTopLocalVariableScope();
 
         IRClosure endClosure = new IRClosure(manager, s, postExeNode.getPosition().getLine(), nearestLVarScope.getStaticScope(), Arity.procArityOf(postExeNode.getVarNode()), postExeNode.getArgumentType(), "_END_", true);
         // Create a new nested builder to ensure this gets its own IR builder state
         // like the ensure block stack
         IRBuilder closureBuilder = newIRBuilder(manager);
 
         // Set up %current_scope and %current_module
         closureBuilder.addInstr(endClosure, new CopyInstr(endClosure.getCurrentScopeVariable(), CURRENT_SCOPE[0]));
         closureBuilder.addInstr(endClosure, new CopyInstr(endClosure.getCurrentModuleVariable(), SCOPE_MODULE[0]));
         closureBuilder.build(postExeNode.getBodyNode(), endClosure);
 
         // END does not have either explicit or implicit return, so we add one
         closureBuilder.addInstr(endClosure, new ReturnInstr(new Nil()));
 
         // Add an instruction in 's' to record the end block in the 'topLevel' scope.
         // SSS FIXME: IR support for end-blocks that access vars in non-toplevel-scopes
         // might be broken currently. We could either fix it or consider dropping support
         // for END blocks altogether or only support them in the toplevel. Not worth the pain.
         addInstr(s, new RecordEndBlockInstr(topLevel, new WrappedIRClosure(s.getSelf(), endClosure)));
         return manager.getNil();
     }
 
     public Operand buildPreExe(PreExeNode preExeNode, IRScope s) {
         IRClosure beginClosure = new IRFor(manager, s, preExeNode.getPosition().getLine(), s.getTopLevelScope().getStaticScope(), Arity.procArityOf(preExeNode.getVarNode()), preExeNode.getArgumentType(), "_BEGIN_");
         // Create a new nested builder to ensure this gets its own IR builder state
         // like the ensure block stack
         IRBuilder closureBuilder = newIRBuilder(manager);
 
         // Set up %current_scope and %current_module
         closureBuilder.addInstr(beginClosure, new CopyInstr(beginClosure.getCurrentScopeVariable(), CURRENT_SCOPE[0]));
         closureBuilder.addInstr(beginClosure, new CopyInstr(beginClosure.getCurrentModuleVariable(), SCOPE_MODULE[0]));
         closureBuilder.build(preExeNode.getBodyNode(), beginClosure);
 
         // BEGIN does not have either explicit or implicit return, so we add one
         closureBuilder.addInstr(beginClosure, new ReturnInstr(new Nil()));
 
         // Record the begin block at IR build time
         s.getTopLevelScope().recordBeginBlock(beginClosure);
         return manager.getNil();
     }
 
     public Operand buildRational(RationalNode rationalNode) {
         return new Rational(rationalNode.getNumerator(), rationalNode.getDenominator());
     }
 
     public Operand buildRedo(IRScope s) {
         // If in a loop, a redo is a jump to the beginning of the loop.
         // If not, for closures, a redo is a jump to the beginning of the closure.
         // If not in a loop or a closure, it is a local jump error
         IRLoop currLoop = getCurrentLoop();
         if (currLoop != null) {
              addInstr(s, new JumpInstr(currLoop.iterStartLabel));
         } else {
             if (s instanceof IRClosure) {
                 addInstr(s, new ThreadPollInstr(true));
                 addInstr(s, new JumpInstr(((IRClosure)s).startLabel));
             } else {
                 addInstr(s, new ThrowExceptionInstr(IRException.REDO_LocalJumpError));
             }
         }
         return manager.getNil();
     }
 
     public Operand buildRegexp(RegexpNode reNode, IRScope s) {
         // SSS FIXME: Rather than throw syntax error at runtime, we should detect
         // regexp syntax errors at build time and add an exception-throwing instruction instead
         return copyAndReturnValue(s, new Regexp(new StringLiteral(reNode.getValue()), reNode.getOptions()));
     }
 
     public Operand buildRescue(RescueNode node, IRScope s) {
         return buildRescueInternal(node, s, null);
     }
 
     private Operand buildRescueInternal(RescueNode rescueNode, IRScope s, EnsureBlockInfo ensure) {
         // Labels marking start, else, end of the begin-rescue(-ensure)-end block
         Label rBeginLabel = ensure == null ? s.getNewLabel() : ensure.regionStart;
         Label rEndLabel   = ensure == null ? s.getNewLabel() : ensure.end;
         Label rescueLabel = s.getNewLabel(); // Label marking start of the first rescue code.
 
         // Save $! in a temp var so it can be restored when the exception gets handled.
         Variable savedGlobalException = s.createTemporaryVariable();
         addInstr(s, new GetGlobalVariableInstr(savedGlobalException, "$!"));
         if (ensure != null) ensure.savedGlobalException = savedGlobalException;
 
         addInstr(s, new LabelInstr(rBeginLabel));
 
         // Placeholder rescue instruction that tells rest of the compiler passes the boundaries of the rescue block.
         addInstr(s, new ExceptionRegionStartMarkerInstr(rescueLabel));
         activeRescuers.push(rescueLabel);
 
         // Body
         Operand tmp = manager.getNil();  // default return value if for some strange reason, we neither have the body node or the else node!
         Variable rv = s.createTemporaryVariable();
         if (rescueNode.getBodyNode() != null) tmp = build(rescueNode.getBodyNode(), s);
 
         // Push rescue block *after* body has been built.
         // If not, this messes up generation of retry in these scenarios like this:
         //
         //     begin    -- 1
         //       ...
         //     rescue
         //       begin  -- 2
         //         ...
         //         retry
         //       rescue
         //         ...
         //       end
         //     end
         //
         // The retry should jump to 1, not 2.
         // If we push the rescue block before building the body, we will jump to 2.
         RescueBlockInfo rbi = new RescueBlockInfo(rescueNode, rBeginLabel, savedGlobalException, getCurrentLoop());
         activeRescueBlockStack.push(rbi);
 
         // Since rescued regions are well nested within Ruby, this bare marker is sufficient to
         // let us discover the edge of the region during linear traversal of instructions during cfg construction.
         addInstr(s, new ExceptionRegionEndMarkerInstr());
         activeRescuers.pop();
 
         // Else part of the body -- we simply fall through from the main body if there were no exceptions
         Label elseLabel = rescueNode.getElseNode() == null ? null : s.getNewLabel();
         if (elseLabel != null) {
             addInstr(s, new LabelInstr(elseLabel));
             tmp = build(rescueNode.getElseNode(), s);
         }
 
         if (tmp != U_NIL) {
             addInstr(s, new CopyInstr(rv, tmp));
 
             // No explicit return from the protected body
             // - If we dont have any ensure blocks, simply jump to the end of the rescue block
             // - If we do, execute the ensure code.
             if (ensure != null) {
                 ensure.cloneIntoHostScope(this, s);
             }
             addInstr(s, new JumpInstr(rEndLabel));
         }   //else {
             // If the body had an explicit return, the return instruction IR build takes care of setting
             // up execution of all necessary ensure blocks.  So, nothing to do here!
             //
             // Additionally, the value in 'rv' will never be used, so need to set it to any specific value.
             // So, we can leave it undefined.  If on the other hand, there was an exception in that block,
             // 'rv' will get set in the rescue handler -- see the 'rv' being passed into
             // buildRescueBodyInternal below.  So, in either case, we are good!
             //}
 
         // Start of rescue logic
         addInstr(s, new LabelInstr(rescueLabel));
 
         // Save off exception & exception comparison type
         Variable exc = addResultInstr(s, new ReceiveRubyExceptionInstr(s.createTemporaryVariable()));
 
         // Build the actual rescue block(s)
         buildRescueBodyInternal(s, rescueNode.getRescueNode(), rv, exc, rEndLabel);
 
         // End label -- only if there is no ensure block!  With an ensure block, you end at ensureEndLabel.
         if (ensure == null) addInstr(s, new LabelInstr(rEndLabel));
 
         activeRescueBlockStack.pop();
         return rv;
     }
 
     private void outputExceptionCheck(IRScope s, Operand excType, Operand excObj, Label caughtLabel) {
         Variable eqqResult = addResultInstr(s, new RescueEQQInstr(s.createTemporaryVariable(), excType, excObj));
         addInstr(s, BEQInstr.create(eqqResult, manager.getTrue(), caughtLabel));
     }
 
     private void buildRescueBodyInternal(IRScope s, RescueBodyNode rescueBodyNode, Variable rv, Variable exc, Label endLabel) {
         final Node exceptionList = rescueBodyNode.getExceptionNodes();
 
         // Compare and branch as necessary!
         Label uncaughtLabel = s.getNewLabel();
         Label caughtLabel = s.getNewLabel();
         if (exceptionList != null) {
             if (exceptionList instanceof ListNode) {
                 List<Operand> excTypes = new ArrayList<>();
                 for (Node excType : exceptionList.childNodes()) {
                     excTypes.add(build(excType, s));
                 }
                 outputExceptionCheck(s, new Array(excTypes), exc, caughtLabel);
             } else if (exceptionList instanceof SplatNode) { // splatnode, catch
                 outputExceptionCheck(s, build(((SplatNode)exceptionList).getValue(), s), exc, caughtLabel);
             } else { // argscat/argspush
                 outputExceptionCheck(s, build(exceptionList, s), exc, caughtLabel);
             }
         } else {
             // SSS FIXME:
             // rescue => e AND rescue implicitly EQQ the exception object with StandardError
             // We generate explicit IR for this test here.  But, this can lead to inconsistent
             // behavior (when compared to MRI) in certain scenarios.  See example:
             //
             //   self.class.const_set(:StandardError, 1)
             //   begin; raise TypeError.new; rescue; puts "AHA"; end
             //
             // MRI rescues the error, but we will raise an exception because of reassignment
             // of StandardError.  I am ignoring this for now and treating this as undefined behavior.
             //
             // Solution: Create a 'StandardError' operand type to eliminate this.
             Variable v = addResultInstr(s, new InheritanceSearchConstInstr(s.createTemporaryVariable(), s.getCurrentModuleVariable(), "StandardError", false));
             outputExceptionCheck(s, v, exc, caughtLabel);
         }
 
         // Uncaught exception -- build other rescue nodes or rethrow!
         addInstr(s, new LabelInstr(uncaughtLabel));
         if (rescueBodyNode.getOptRescueNode() != null) {
             buildRescueBodyInternal(s, rescueBodyNode.getOptRescueNode(), rv, exc, endLabel);
         } else {
             addInstr(s, new ThrowExceptionInstr(exc));
         }
 
         // Caught exception case -- build rescue body
         addInstr(s, new LabelInstr(caughtLabel));
         Node realBody = skipOverNewlines(s, rescueBodyNode.getBodyNode());
         Operand x = build(realBody, s);
         if (x != U_NIL) { // can be U_NIL if the rescue block has an explicit return
             // Restore "$!"
             RescueBlockInfo rbi = activeRescueBlockStack.peek();
             addInstr(s, new PutGlobalVarInstr("$!", rbi.savedExceptionVariable));
 
             // Set up node return value 'rv'
             addInstr(s, new CopyInstr(rv, x));
 
             // If we have a matching ensure block, clone it so ensure block runs here
             if (!activeEnsureBlockStack.empty() && rbi.rescueNode == activeEnsureBlockStack.peek().matchingRescueNode) {
                 activeEnsureBlockStack.peek().cloneIntoHostScope(this, s);
             }
             addInstr(s, new JumpInstr(endLabel));
         }
     }
 
     public Operand buildRetry(IRScope s) {
         // JRuby only supports retry when present in rescue blocks!
         // 1.9 doesn't support retry anywhere else.
 
         // Jump back to the innermost rescue block
         // We either find it, or we add code to throw a runtime exception
         if (activeRescueBlockStack.empty()) {
             addInstr(s, new ThrowExceptionInstr(IRException.RETRY_LocalJumpError));
         } else {
             addInstr(s, new ThreadPollInstr(true));
             // Restore $! and jump back to the entry of the rescue block
             RescueBlockInfo rbi = activeRescueBlockStack.peek();
             addInstr(s, new PutGlobalVarInstr("$!", rbi.savedExceptionVariable));
             addInstr(s, new JumpInstr(rbi.entryLabel));
             // Retries effectively create a loop
             s.setHasLoopsFlag();
         }
         return manager.getNil();
     }
 
     private Operand processEnsureRescueBlocks(IRScope s, Operand retVal) { 
         // Before we return,
         // - have to go execute all the ensure blocks if there are any.
         //   this code also takes care of resetting "$!"
         // - if we have a rescue block, reset "$!".
         if (!activeEnsureBlockStack.empty()) {
             retVal = addResultInstr(s, new CopyInstr(s.createTemporaryVariable(), retVal));
             emitEnsureBlocks(s, null);
         } else if (!activeRescueBlockStack.empty()) {
             // Restore $!
             RescueBlockInfo rbi = activeRescueBlockStack.peek();
             addInstr(s, new PutGlobalVarInstr("$!", rbi.savedExceptionVariable));
         }
        return retVal;
     }
 
     public Operand buildReturn(ReturnNode returnNode, IRScope s) {
         Operand retVal = (returnNode.getValueNode() == null) ? manager.getNil() : build(returnNode.getValueNode(), s);
 
         if (s instanceof IRClosure) {
             // If 'm' is a block scope, a return returns from the closest enclosing method.
             // If this happens to be a module body, the runtime throws a local jump error if the
             // closure is a proc. If the closure is a lambda, then this becomes a normal return.
             IRMethod m = s.getNearestMethod();
             addInstr(s, new RuntimeHelperCall(null, CHECK_FOR_LJE, new Operand[] { new Boolean(m == null) }));
             retVal = processEnsureRescueBlocks(s, retVal);
             addInstr(s, new NonlocalReturnInstr(retVal, m == null ? "--none--" : m.getName()));
         } else if (s.isModuleBody()) {
             IRMethod sm = s.getNearestMethod();
 
             // Cannot return from top-level module bodies!
             if (sm == null) addInstr(s, new ThrowExceptionInstr(IRException.RETURN_LocalJumpError));
             retVal = processEnsureRescueBlocks(s, retVal);
             if (sm != null) addInstr(s, new NonlocalReturnInstr(retVal, sm.getName()));
         } else {
             retVal = processEnsureRescueBlocks(s, retVal);
             addInstr(s, new ReturnInstr(retVal));
         }
 
         // The value of the return itself in the containing expression can never be used because of control-flow reasons.
         // The expression that uses this result can never be executed beyond the return and hence the value itself is just
         // a placeholder operand.
         return U_NIL;
     }
 
     public IREvalScript buildEvalRoot(StaticScope staticScope, IRScope containingScope, String file, int lineNumber, RootNode rootNode, EvalType evalType) {
         // Top-level script!
         IREvalScript script;
 
         if (evalType == EvalType.BINDING_EVAL) {
             script = new IRBindingEvalScript(manager, containingScope, file, lineNumber, staticScope, evalType);
         } else {
             script = new IREvalScript(manager, containingScope, file, lineNumber, staticScope, evalType);
         }
 
         // We link IRScope to StaticScope because we may add additional variables (like %block).  During execution
         // we end up growing dynamicscope potentially based on any changes made.
         staticScope.setIRScope(script);
 
         // Debug info: record line number
         addInstr(script, new LineNumberInstr(lineNumber));
 
         // Set %current_scope = <current-scope>
         // Set %current_module = <current-module>
         addInstr(script, new CopyInstr(script.getCurrentScopeVariable(), CURRENT_SCOPE[0]));
         addInstr(script, new CopyInstr(script.getCurrentModuleVariable(), SCOPE_MODULE[0]));
         // Build IR for the tree and return the result of the expression tree
         Operand rval = rootNode.getBodyNode() == null ? manager.getNil() : build(rootNode.getBodyNode(), script);
         addInstr(script, new ReturnInstr(rval));
 
         return script;
     }
 
     public IRScriptBody buildRoot(RootNode rootNode) {
         String file = rootNode.getPosition().getFile();
         StaticScope staticScope = rootNode.getStaticScope();
 
         // Top-level script!
         IRScriptBody script = new IRScriptBody(manager, file, staticScope);
         addInstr(script, new ReceiveSelfInstr(script.getSelf()));
         // Set %current_scope = <current-scope>
         // Set %current_module = <current-module>
         addInstr(script, new CopyInstr(script.getCurrentScopeVariable(), CURRENT_SCOPE[0]));
         addInstr(script, new CopyInstr(script.getCurrentModuleVariable(), SCOPE_MODULE[0]));
 
         // Build IR for the tree and return the result of the expression tree
         addInstr(script, new ReturnInstr(build(rootNode.getBodyNode(), script)));
 
         return script;
     }
 
     public Operand buildSelf(IRScope s) {
         return s.getSelf();
     }
 
     public Operand buildSplat(SplatNode splatNode, IRScope s) {
-        // SSS: Since splats can only occur in call argument lists, no need to copyAndReturnValue(...)
-        // Verify with Tom / Charlie
-        return new Splat(build(splatNode.getValue(), s));
+        Variable res = s.createTemporaryVariable();
+        addInstr(s, new BuildSplatInstr(res, build(splatNode.getValue(), s)));
+        return res;
     }
 
     public Operand buildStr(StrNode strNode, IRScope s) {
         return copyAndReturnValue(s, new StringLiteral(strNode.getValue(), strNode.getCodeRange()));
     }
 
     private Operand buildSuperInstr(IRScope s, Operand block, Operand[] args) {
         CallInstr superInstr;
         Variable ret = s.createTemporaryVariable();
         if ((s instanceof IRMethod) && (s.getLexicalParent() instanceof IRClassBody)) {
             IRMethod m = (IRMethod)s;
             if (m.isInstanceMethod) {
                 superInstr = new InstanceSuperInstr(ret, s.getCurrentModuleVariable(), s.getName(), args, block);
             } else {
                 superInstr = new ClassSuperInstr(ret, s.getCurrentModuleVariable(), s.getName(), args, block);
             }
         } else {
             // We dont always know the method name we are going to be invoking if the super occurs in a closure.
             // This is because the super can be part of a block that will be used by 'define_method' to define
             // a new method.  In that case, the method called by super will be determined by the 'name' argument
             // to 'define_method'.
             superInstr = new UnresolvedSuperInstr(ret, s.getSelf(), args, block);
         }
         receiveBreakException(s, block, superInstr);
         return ret;
     }
 
     public Operand buildSuper(SuperNode superNode, IRScope s) {
         if (s.isModuleBody()) return buildSuperInScriptBody(s);
 
         Operand[] args = setupCallArgs(superNode.getArgsNode(), s);
         Operand block = setupCallClosure(superNode.getIterNode(), s);
         if (block == null) block = getImplicitBlockArg(s);
         return buildSuperInstr(s, block, args);
     }
 
     private Operand buildSuperInScriptBody(IRScope s) {
         return addResultInstr(s, new UnresolvedSuperInstr(s.createTemporaryVariable(), s.getSelf(), NO_ARGS, null));
     }
 
     public Operand buildSValue(SValueNode node, IRScope s) {
         // SSS FIXME: Required? Verify with Tom/Charlie
         return copyAndReturnValue(s, new SValue(build(node.getValue(), s)));
     }
 
     public Operand buildSymbol(SymbolNode node) {
         // SSS: Since symbols are interned objects, no need to copyAndReturnValue(...)
         return new Symbol(node.getName(), node.getEncoding());
     }
 
     public Operand buildTrue() {
         return manager.getTrue();
     }
 
     public Operand buildUndef(Node node, IRScope s) {
         Operand methName = build(((UndefNode) node).getName(), s);
         return addResultInstr(s, new UndefMethodInstr(s.createTemporaryVariable(), methName));
     }
 
     private Operand buildConditionalLoop(IRScope s, Node conditionNode,
             Node bodyNode, boolean isWhile, boolean isLoopHeadCondition) {
         if (isLoopHeadCondition &&
                 ((isWhile && conditionNode.getNodeType().alwaysFalse()) ||
                 (!isWhile && conditionNode.getNodeType().alwaysTrue()))) {
             // we won't enter the loop -- just build the condition node
             build(conditionNode, s);
             return manager.getNil();
         } else {
             IRLoop loop = new IRLoop(s, getCurrentLoop());
             Variable loopResult = loop.loopResult;
             Label setupResultLabel = s.getNewLabel();
 
             // Push new loop
             loopStack.push(loop);
 
             // End of iteration jumps here
             addInstr(s, new LabelInstr(loop.loopStartLabel));
             if (isLoopHeadCondition) {
                 Operand cv = build(conditionNode, s);
                 addInstr(s, BEQInstr.create(cv, isWhile ? manager.getFalse() : manager.getTrue(), setupResultLabel));
             }
 
             // Redo jumps here
             addInstr(s, new LabelInstr(loop.iterStartLabel));
 
             // Thread poll at start of iteration -- ensures that redos and nexts run one thread-poll per iteration
             addInstr(s, new ThreadPollInstr(true));
 
             // Build body
             if (bodyNode != null) build(bodyNode, s);
 
             // Next jumps here
             addInstr(s, new LabelInstr(loop.iterEndLabel));
             if (isLoopHeadCondition) {
                 addInstr(s, new JumpInstr(loop.loopStartLabel));
             } else {
                 Operand cv = build(conditionNode, s);
                 addInstr(s, BEQInstr.create(cv, isWhile ? manager.getTrue() : manager.getFalse(), loop.iterStartLabel));
             }
 
             // Loop result -- nil always
             addInstr(s, new LabelInstr(setupResultLabel));
             addInstr(s, new CopyInstr(loopResult, manager.getNil()));
 
             // Loop end -- breaks jump here bypassing the result set up above
             addInstr(s, new LabelInstr(loop.loopEndLabel));
 
             // Done with loop
             loopStack.pop();
 
             return loopResult;
         }
     }
 
     public Operand buildUntil(final UntilNode untilNode, IRScope s) {
         return buildConditionalLoop(s, untilNode.getConditionNode(), untilNode.getBodyNode(), false, untilNode.evaluateAtStart());
     }
 
     public Operand buildVAlias(VAliasNode valiasNode, IRScope s) {
         addInstr(s, new GVarAliasInstr(new StringLiteral(valiasNode.getNewName()), new StringLiteral(valiasNode.getOldName())));
 
         return manager.getNil();
     }
 
     public Operand buildVCall(VCallNode node, IRScope s) {
         return addResultInstr(s, CallInstr.create(CallType.VARIABLE, s.createTemporaryVariable(),
                 node.getName(), s.getSelf(), NO_ARGS, null));
     }
 
     public Operand buildWhile(final WhileNode whileNode, IRScope s) {
         return buildConditionalLoop(s, whileNode.getConditionNode(), whileNode.getBodyNode(), true, whileNode.evaluateAtStart());
     }
 
     public Operand buildXStr(XStrNode node, IRScope s) {
         Variable res = s.createTemporaryVariable();
         addInstr(s, new BacktickInstr(res, new StringLiteral(node.getValue())));
         return res;
     }
 
     public Operand buildYield(YieldNode node, IRScope s) {
         boolean unwrap = true;
         Node argNode = node.getArgsNode();
         // Get rid of one level of array wrapping
         if (argNode != null && (argNode instanceof ArrayNode) && ((ArrayNode)argNode).size() == 1) {
             argNode = ((ArrayNode)argNode).getLast();
             unwrap = false;
         }
 
         Variable ret = s.createTemporaryVariable();
         addInstr(s, new YieldInstr(ret, getImplicitBlockArg(s), build(argNode, s), unwrap));
         return ret;
     }
 
     public Operand buildZArray(IRScope s) {
        return copyAndReturnValue(s, new Array());
     }
 
     private Operand buildZSuperIfNest(final IRScope s, final Operand block) {
         // If we are in a block, we cannot make any assumptions about what args
         // the super instr is going to get -- if there were no 'define_method'
         // for defining methods, we could guarantee that the super is going to
         // receive args from the nearest method the block is embedded in.  But,
         // in the presence of 'define_method' (and eval and aliasing), all bets
         // are off because, any of the intervening block scopes could be a method
         // via a define_method call.
         //
         // Instead, we can actually collect all arguments of all scopes from here
         // till the nearest method scope and select the right set at runtime based
         // on which one happened to be a method scope. This has the additional
         // advantage of making explicit all used arguments.
         CodeBlock zsuperBuilder = new CodeBlock() {
             public Operand run() {
                 Variable scopeDepth = s.createTemporaryVariable();
                 addInstr(s, new ArgScopeDepthInstr(scopeDepth));
 
                 Label allDoneLabel = s.getNewLabel();
 
                 IRScope superScope = s;
                 int depthFromSuper = 0;
                 Label next = null;
 
                 // Loop and generate a block for each possible value of depthFromSuper
                 Variable zsuperResult = s.createTemporaryVariable();
                 while (superScope instanceof IRClosure) {
                     // Generate the next set of instructions
                     if (next != null) addInstr(s, new LabelInstr(next));
                     next = s.getNewLabel();
                     addInstr(s, BNEInstr.create(new Fixnum(depthFromSuper), scopeDepth, next));
                     Operand[] args = adjustVariableDepth(((IRClosure)superScope).getBlockArgs(), depthFromSuper);
                     addInstr(s, new ZSuperInstr(zsuperResult, s.getSelf(), args,  block));
                     addInstr(s, new JumpInstr(allDoneLabel));
 
                     // Move on
                     superScope = superScope.getLexicalParent();
                     depthFromSuper++;
                 }
 
                 addInstr(s, new LabelInstr(next));
 
                 // If we hit a method, this is known to always succeed
                 if (superScope instanceof IRMethod) {
                     Operand[] args = adjustVariableDepth(((IRMethod)superScope).getCallArgs(), depthFromSuper);
                     addInstr(s, new ZSuperInstr(zsuperResult, s.getSelf(), args, block));
                 } //else {
                 // FIXME: Do or don't ... there is no try
                     /* Control should never get here in the runtime */
                     /* Should we add an exception throw here just in case? */
                 //}
 
                 addInstr(s, new LabelInstr(allDoneLabel));
                 return zsuperResult;
             }
         };
 
         return receiveBreakException(s, block, zsuperBuilder);
     }
 
     public Operand buildZSuper(ZSuperNode zsuperNode, IRScope s) {
         if (s.isModuleBody()) return buildSuperInScriptBody(s);
 
         Operand block = setupCallClosure(zsuperNode.getIterNode(), s);
         if (block == null) block = getImplicitBlockArg(s);
 
         // Enebo:ZSuper in for (or nested for) can be statically resolved like method but it needs to fixup depth.
         if (s instanceof IRMethod) {
             return buildSuperInstr(s, block, ((IRMethod)s).getCallArgs());
         } else {
             return buildZSuperIfNest(s, block);
         }
     }
 
     /*
      * Adjust all argument operands by changing their depths to reflect how far they are from
      * super.  This fixup is only currently happening in supers nested in closures.
      */
     private Operand[] adjustVariableDepth(Operand[] args, int depthFromSuper) {
         Operand[] newArgs = new Operand[args.length];
 
         for (int i = 0; i < args.length; i++) {
             // Because of keyword args, we can have a keyword-arg hash in the call args.
             if (args[i] instanceof Hash) {
                 newArgs[i] = ((Hash) args[i]).cloneForLVarDepth(depthFromSuper);
             } else {
                 newArgs[i] = ((DepthCloneable) args[i]).cloneForDepth(depthFromSuper);
             }
         }
 
         return newArgs;
     }
 
     private Operand buildModuleOrClassBody(IRScope parent, Variable moduleVar, IRModuleBody body, Node bodyNode, int linenumber) {
         Variable processBodyResult = addResultInstr(parent, new ProcessModuleBodyInstr(parent.createTemporaryVariable(), moduleVar));
         IRBuilder bodyBuilder = newIRBuilder(manager);
 
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             bodyBuilder.addInstr(body, new TraceInstr(RubyEvent.CLASS, null, body.getFileName(), linenumber));
         }
 
         bodyBuilder.addInstr(body, new ReceiveSelfInstr(body.getSelf()));                            // %self
         bodyBuilder.addInstr(body, new CopyInstr(body.getCurrentScopeVariable(), CURRENT_SCOPE[0])); // %scope
         bodyBuilder.addInstr(body, new CopyInstr(body.getCurrentModuleVariable(), SCOPE_MODULE[0])); // %module
         // Create a new nested builder to ensure this gets its own IR builder state
         Operand bodyReturnValue = bodyBuilder.build(bodyNode, body);
 
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             bodyBuilder.addInstr(body, new TraceInstr(RubyEvent.END, null, body.getFileName(), -1));
         }
 
         bodyBuilder.addInstr(body, new ReturnInstr(bodyReturnValue));
 
         return processBodyResult;
     }
 
     private String methodNameFor(IRScope s) {
         IRScope method = s.getNearestMethod();
 
         return method == null ? null : method.getName();
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/IRClosure.java b/core/src/main/java/org/jruby/ir/IRClosure.java
index a1924493f7..633a547e93 100644
--- a/core/src/main/java/org/jruby/ir/IRClosure.java
+++ b/core/src/main/java/org/jruby/ir/IRClosure.java
@@ -1,379 +1,379 @@
 package org.jruby.ir;
 
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.ir.instructions.*;
 import org.jruby.ir.interpreter.ClosureInterpreterContext;
 import org.jruby.ir.interpreter.InterpreterContext;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.representations.CFG;
 import org.jruby.ir.transformations.inlining.CloneInfo;
 import org.jruby.ir.transformations.inlining.SimpleCloneInfo;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.InterpretedIRBlockBody;
 import org.jruby.util.KeyValuePair;
 import org.objectweb.asm.Handle;
 
 import java.util.ArrayList;
 import java.util.List;
 
 // Closures are contexts/scopes for the purpose of IR building.  They are self-contained and accumulate instructions
 // that don't merge into the flow of the containing scope.  They are manipulated as an unit.
 // Their parents are always execution scopes.
 
 public class IRClosure extends IRScope {
     public final Label startLabel; // Label for the start of the closure (used to implement redo)
     public final Label endLabel;   // Label for the end of the closure (used to implement retry)
     public final int closureId;    // Unique id for this closure within the nearest ancestor method.
 
     private int nestingDepth;      // How many nesting levels within a method is this closure nested in?
 
     private boolean isBeginEndBlock;
 
     // Block parameters
     private List<Operand> blockArgs;
     private List<KeyValuePair<Operand, Operand>> keywordArgs;
 
     /** The parameter names, for Proc#parameters */
     private String[] parameterList;
 
     private Arity arity;
     private int argumentType;
 
     /** Added for interp/JIT purposes */
     private BlockBody body;
 
     /** Added for JIT purposes */
     private Handle handle;
 
     // Used by other constructions and by IREvalScript as well
     protected IRClosure(IRManager manager, IRScope lexicalParent, String fileName, int lineNumber, StaticScope staticScope, String prefix) {
         super(manager, lexicalParent, null, fileName, lineNumber, staticScope);
 
         this.startLabel = getNewLabel(prefix + "START");
         this.endLabel = getNewLabel(prefix + "END");
         this.closureId = lexicalParent.getNextClosureId();
         setName(prefix + closureId);
         this.body = null;
         this.parameterList = new String[] {};
 
         // set nesting depth
         int n = 0;
         IRScope s = this.getLexicalParent();
         while (s instanceof IRClosure) {
             n++;
             s = s.getLexicalParent();
         }
         this.nestingDepth = n;
     }
 
     /** Used by cloning code */
     /* Inlining generates a new name and id and basic cloning will reuse the originals name */
     protected IRClosure(IRClosure c, IRScope lexicalParent, int closureId, String fullName) {
         super(c, lexicalParent);
         this.closureId = closureId;
         super.setName(fullName);
         this.startLabel = getNewLabel(getName() + "_START");
         this.endLabel = getNewLabel(getName() + "_END");
         if (getManager().isDryRun()) {
             this.body = null;
         } else {
             this.body = new InterpretedIRBlockBody(this, c.body.arity());
         }
         this.blockArgs = new ArrayList<>();
         this.keywordArgs = new ArrayList<>();
         this.arity = c.arity;
     }
 
     public IRClosure(IRManager manager, IRScope lexicalParent, int lineNumber, StaticScope staticScope, Arity arity, int argumentType) {
         this(manager, lexicalParent, lineNumber, staticScope, arity, argumentType, "_CLOSURE_");
     }
 
     public IRClosure(IRManager manager, IRScope lexicalParent, int lineNumber, StaticScope staticScope, Arity arity, int argumentType, String prefix) {
         this(manager, lexicalParent, lineNumber, staticScope, arity, argumentType, prefix, false);
     }
 
     public IRClosure(IRManager manager, IRScope lexicalParent, int lineNumber, StaticScope staticScope, Arity arity, int argumentType, String prefix, boolean isBeginEndBlock) {
         this(manager, lexicalParent, lexicalParent.getFileName(), lineNumber, staticScope, prefix);
         this.blockArgs = new ArrayList<>();
         this.keywordArgs = new ArrayList<>();
         this.argumentType = argumentType;
         this.arity = arity;
         lexicalParent.addClosure(this);
 
         if (getManager().isDryRun()) {
             this.body = null;
         } else {
             this.body = new InterpretedIRBlockBody(this, arity);
             if (staticScope != null && !isBeginEndBlock) {
                 staticScope.setIRScope(this);
                 staticScope.setScopeType(this.getScopeType());
             }
         }
 
         this.nestingDepth++;
     }
 
     @Override
     public InterpreterContext allocateInterpreterContext(Instr[] instructionList) {
         return new ClosureInterpreterContext(this, instructionList);
     }
 
     public void setBeginEndBlock() {
         this.isBeginEndBlock = true;
     }
 
     public boolean isBeginEndBlock() {
         return isBeginEndBlock;
     }
 
     public void setParameterList(String[] parameterList) {
         this.parameterList = parameterList;
         if (!getManager().isDryRun()) {
             ((InterpretedIRBlockBody)this.body).setParameterList(parameterList);
         }
     }
 
     public String[] getParameterList() {
         return this.parameterList;
     }
 
     @Override
     public int getNextClosureId() {
         return getLexicalParent().getNextClosureId();
     }
 
     @Override
     public LocalVariable getNewFlipStateVariable() {
         throw new RuntimeException("Cannot get flip variables from closures.");
     }
 
     @Override
     public TemporaryLocalVariable createTemporaryVariable() {
         return getNewTemporaryVariable(TemporaryVariableType.CLOSURE);
     }
 
     @Override
     public TemporaryLocalVariable getNewTemporaryVariable(TemporaryVariableType type) {
         if (type == TemporaryVariableType.CLOSURE) {
             temporaryVariableIndex++;
             return new TemporaryClosureVariable(closureId, temporaryVariableIndex);
         }
 
         return super.getNewTemporaryVariable(type);
     }
 
     @Override
     public Label getNewLabel() {
         return getNewLabel("CL" + closureId + "_LBL");
     }
 
     @Override
     public IRScopeType getScopeType() {
         return IRScopeType.CLOSURE;
     }
 
     @Override
     public boolean isTopLocalVariableScope() {
         return false;
     }
 
     @Override
     public boolean isFlipScope() {
         return false;
     }
 
     @Override
     public void addInstr(Instr i) {
         // Accumulate block arguments
         if (i instanceof ReceiveKeywordRestArgInstr) {
             // Always add the keyword rest arg to the beginning
             keywordArgs.add(0, new KeyValuePair<Operand, Operand>(Symbol.KW_REST_ARG_DUMMY, ((ReceiveArgBase) i).getResult()));
         } else if (i instanceof ReceiveKeywordArgInstr) {
             ReceiveKeywordArgInstr rkai = (ReceiveKeywordArgInstr)i;
             // FIXME: This lost encoding information when name was converted to string earlier in IRBuilder
             keywordArgs.add(new KeyValuePair<Operand, Operand>(new Symbol(rkai.argName, USASCIIEncoding.INSTANCE), rkai.getResult()));
         } else if (i instanceof ReceiveRestArgInstr) {
-            blockArgs.add(new Splat(((ReceiveRestArgInstr)i).getResult(), true));
+            blockArgs.add(new Splat(((ReceiveRestArgInstr)i).getResult()));
         } else if (i instanceof ReceiveArgBase) {
             blockArgs.add(((ReceiveArgBase) i).getResult());
         }
 
         super.addInstr(i);
     }
 
     public Operand[] getBlockArgs() {
         if (receivesKeywordArgs()) {
             int i = 0;
             Operand[] args = new Operand[blockArgs.size() + 1];
             for (Operand arg: blockArgs) {
                 args[i++] = arg;
             }
             args[i] = new Hash(keywordArgs, true);
             return args;
         } else {
             return blockArgs.toArray(new Operand[blockArgs.size()]);
         }
     }
 
     public String toStringBody() {
         StringBuilder buf = new StringBuilder();
         buf.append(getName()).append(" = { \n");
 
         CFG c = getCFG();
         if (c != null) {
             buf.append("\nCFG:\n").append(c.toStringGraph()).append("\nInstructions:\n").append(c.toStringInstrs());
         } else {
             buf.append(toStringInstrs());
         }
         buf.append("\n}\n\n");
         return buf.toString();
     }
 
     public BlockBody getBlockBody() {
         return body;
     }
 
     @Override
     protected LocalVariable findExistingLocalVariable(String name, int scopeDepth) {
         LocalVariable lvar = lookupExistingLVar(name);
         if (lvar != null) return lvar;
 
         int newDepth = scopeDepth - 1;
 
         return newDepth >= 0 ? getLexicalParent().findExistingLocalVariable(name, newDepth) : null;
     }
 
     public LocalVariable getNewLocalVariable(String name, int depth) {
         if (depth == 0 && !(this instanceof IRFor)) {
             LocalVariable lvar = new ClosureLocalVariable(this, name, 0, getStaticScope().addVariableThisScope(name));
             localVars.put(name, lvar);
             return lvar;
         } else {
             IRScope s = this;
             int     d = depth;
             do {
                 // account for for-loops
                 while (s instanceof IRFor) {
                     depth++;
                     s = s.getLexicalParent();
                 }
 
                 // walk up
                 d--;
                 if (d >= 0) s = s.getLexicalParent();
             } while (d >= 0);
 
             return s.getNewLocalVariable(name, 0).cloneForDepth(depth);
         }
     }
 
     @Override
     public LocalVariable getLocalVariable(String name, int depth) {
         // AST doesn't seem to be implementing shadowing properly and sometimes
         // has the wrong depths which screws up variable access. So, we implement
         // shadowing here by searching for an existing local var from depth 0 and upwards.
         //
         // Check scope depths for 'a' in the closure in the following snippet:
         //
         //   "a = 1; foo(1) { |(a)| a }"
         //
         // In "(a)", it is 0 (correct), but in the body, it is 1 (incorrect)
 
         LocalVariable lvar;
         IRScope s = this;
         int d = depth;
         do {
             // account for for-loops
             while (s instanceof IRFor) {
                 depth++;
                 s = s.getLexicalParent();
             }
 
             // lookup
             lvar = s.lookupExistingLVar(name);
 
             // walk up
             d--;
             if (d >= 0) s = s.getLexicalParent();
         } while (lvar == null && d >= 0);
 
         if (lvar == null) {
             // Create a new var at requested/adjusted depth
             lvar = s.getNewLocalVariable(name, 0).cloneForDepth(depth);
         } else {
             // Find # of lexical scopes we walked up to find 'lvar'.
             // We need a copy of 'lvar' usable at that depth
             int lvarDepth = depth - (d + 1);
             if (lvar.getScopeDepth() != lvarDepth) lvar = lvar.cloneForDepth(lvarDepth);
         }
 
         return lvar;
     }
 
     public int getNestingDepth() {
         return nestingDepth;
     }
 
     protected IRClosure cloneForInlining(CloneInfo ii, IRClosure clone) {
         clone.nestingDepth  = this.nestingDepth;
         // SSS FIXME: This is fragile. Untangle this state.
         // Why is this being copied over to InterpretedIRBlockBody?
         clone.setParameterList(this.parameterList);
         clone.isBeginEndBlock = this.isBeginEndBlock;
 
         SimpleCloneInfo clonedII = ii.cloneForCloningClosure(clone);
 
         if (getCFG() != null) {
             clone.setCFG(getCFG().clone(clonedII, clone));
         } else {
             for (Instr i: getInstrs()) {
                 clone.addInstr(i.clone(clonedII));
             }
         }
 
         return clone;
     }
 
     public IRClosure cloneForInlining(CloneInfo ii) {
         IRClosure clonedClosure;
         IRScope lexicalParent = ii.getScope();
 
         if (ii instanceof SimpleCloneInfo && !((SimpleCloneInfo)ii).isEnsureBlockCloneMode()) {
             clonedClosure = new IRClosure(this, lexicalParent, closureId, getName());
         } else {
             int id = lexicalParent.getNextClosureId();
             String fullName = lexicalParent.getName() + "_CLOSURE_CLONE_" + id;
             clonedClosure = new IRClosure(this, lexicalParent, id, fullName);
         }
 
         // WrappedIRClosure should always have a single unique IRClosure in them so we should
         // not end up adding n copies of the same closure as distinct clones...
         lexicalParent.addClosure(clonedClosure);
 
         return cloneForInlining(ii, clonedClosure);
     }
 
     @Override
     public void setName(String name) {
         // We can distinguish closures only with parent scope name
         super.setName(getLexicalParent().getName() + name);
     }
 
     public Arity getArity() {
         return arity;
     }
 
     public int getArgumentType() {
         return argumentType;
     }
 
     public void setHandle(Handle handle) {
         this.handle = handle;
     }
 
     public Handle getHandle() {
         return handle;
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/IRMethod.java b/core/src/main/java/org/jruby/ir/IRMethod.java
index 05f467f204..9ca4c350bb 100644
--- a/core/src/main/java/org/jruby/ir/IRMethod.java
+++ b/core/src/main/java/org/jruby/ir/IRMethod.java
@@ -1,162 +1,162 @@
 package org.jruby.ir;
 
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.ast.MethodDefNode;
 import org.jruby.ast.Node;
 import org.jruby.internal.runtime.methods.IRMethodArgs;
 import org.jruby.ir.instructions.Instr;
 import org.jruby.ir.instructions.ReceiveArgBase;
 import org.jruby.ir.instructions.ReceiveKeywordArgInstr;
 import org.jruby.ir.instructions.ReceiveKeywordRestArgInstr;
 import org.jruby.ir.instructions.ReceiveRestArgInstr;
 import org.jruby.ir.interpreter.InterpreterContext;
 import org.jruby.ir.operands.LocalVariable;
 import org.jruby.ir.operands.Operand;
 import org.jruby.ir.operands.Symbol;
 import org.jruby.ir.operands.Hash;
 import org.jruby.ir.operands.Splat;
 import org.jruby.ir.representations.BasicBlock;
 import org.jruby.util.KeyValuePair;
 import org.jruby.parser.StaticScope;
 
 import java.lang.invoke.MethodType;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 public class IRMethod extends IRScope {
     public final boolean isInstanceMethod;
 
     // Note that if operands from the method are modified,
     // callArgs would have to be updated as well
     //
     // Call parameters
     private List<Operand> callArgs;
     private List<KeyValuePair<Operand, Operand>> keywordArgs;
 
     // Argument description of the form [:req, "a"], [:opt, "b"] ..
     private List<String[]> argDesc;
 
     // Signatures to the jitted versions of this method
     private Map<Integer, MethodType> signatures;
 
     // Method name in the jitted version of this method
     private String jittedName;
 
     private MethodDefNode defn;
 
     public IRMethod(IRManager manager, IRScope lexicalParent, MethodDefNode defn, String name,
             boolean isInstanceMethod, int lineNumber, StaticScope staticScope) {
         super(manager, lexicalParent, name, lexicalParent.getFileName(), lineNumber, staticScope);
 
         this.defn = defn;
         this.isInstanceMethod = isInstanceMethod;
         this.callArgs = new ArrayList<>();
         this.keywordArgs = new ArrayList<>();
         this.argDesc = new ArrayList<>();
         this.signatures = new HashMap<>();
 
         if (!getManager().isDryRun() && staticScope != null) {
             staticScope.setIRScope(this);
             staticScope.setScopeType(this.getScopeType());
         }
     }
 
     /** Run any necessary passes to get the IR ready for interpretation */
     public synchronized InterpreterContext prepareForInterpretation() {
         if (defn != null) {
             IRBuilder.newIRBuilder(getManager()).defineMethodInner(defn, this, getLexicalParent());
 
             defn = null;
         }
 
         return super.prepareForInterpretation();
     }
 
     public synchronized List<BasicBlock> prepareForCompilation() {
         if (defn != null) prepareForInterpretation();
 
         return super.prepareForCompilation();
     }
 
     @Override
     public IRScopeType getScopeType() {
         return isInstanceMethod ? IRScopeType.INSTANCE_METHOD : IRScopeType.CLASS_METHOD;
     }
 
     @Override
     public void addInstr(Instr i) {
         // Accumulate call arguments
         if (i instanceof ReceiveKeywordRestArgInstr) {
             // Always add the keyword rest arg to the beginning
             keywordArgs.add(0, new KeyValuePair<Operand, Operand>(Symbol.KW_REST_ARG_DUMMY, ((ReceiveArgBase) i).getResult()));
         } else if (i instanceof ReceiveKeywordArgInstr) {
             ReceiveKeywordArgInstr rkai = (ReceiveKeywordArgInstr)i;
             // FIXME: This lost encoding information when name was converted to string earlier in IRBuilder
             keywordArgs.add(new KeyValuePair<Operand, Operand>(new Symbol(rkai.argName, USASCIIEncoding.INSTANCE), rkai.getResult()));
         } else if (i instanceof ReceiveRestArgInstr) {
-            callArgs.add(new Splat(((ReceiveRestArgInstr)i).getResult(), true));
+            callArgs.add(new Splat(((ReceiveRestArgInstr)i).getResult()));
         } else if (i instanceof ReceiveArgBase) {
             callArgs.add(((ReceiveArgBase) i).getResult());
         }
 
         super.addInstr(i);
     }
 
     public void addArgDesc(IRMethodArgs.ArgType type, String argName) {
         argDesc.add(new String[]{type.name(), argName});
     }
 
     public List<String[]> getArgDesc() {
         return argDesc;
     }
 
     public Operand[] getCallArgs() {
         if (receivesKeywordArgs()) {
             int i = 0;
             Operand[] args = new Operand[callArgs.size() + 1];
             for (Operand arg: callArgs) {
                 args[i++] = arg;
             }
             args[i] = new Hash(keywordArgs, true);
             return args;
         } else {
             return callArgs.toArray(new Operand[callArgs.size()]);
         }
     }
 
     @Override
     protected LocalVariable findExistingLocalVariable(String name, int scopeDepth) {
         assert scopeDepth == 0: "Local variable depth in IRMethod should always be zero (" + name + " had depth of " + scopeDepth + ")";
         return localVars.get(name);
     }
 
     @Override
     public LocalVariable getLocalVariable(String name, int scopeDepth) {
         LocalVariable lvar = findExistingLocalVariable(name, scopeDepth);
         if (lvar == null) lvar = getNewLocalVariable(name, scopeDepth);
         return lvar;
     }
 
     public void addNativeSignature(int arity, MethodType signature) {
         signatures.put(arity, signature);
     }
 
     public MethodType getNativeSignature(int arity) {
         return signatures.get(arity);
     }
 
     public Map<Integer, MethodType> getNativeSignatures() {
         return Collections.unmodifiableMap(signatures);
     }
 
     public String getJittedName() {
         return jittedName;
     }
 
     public void setJittedName(String jittedName) {
         this.jittedName = jittedName;
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/IRVisitor.java b/core/src/main/java/org/jruby/ir/IRVisitor.java
index 3adc6820a6..bc671abe85 100644
--- a/core/src/main/java/org/jruby/ir/IRVisitor.java
+++ b/core/src/main/java/org/jruby/ir/IRVisitor.java
@@ -1,183 +1,184 @@
 package org.jruby.ir;
 
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.ir.instructions.*;
 import org.jruby.ir.instructions.boxing.*;
 import org.jruby.ir.instructions.defined.GetErrorInfoInstr;
 import org.jruby.ir.instructions.defined.RestoreErrorInfoInstr;
 import org.jruby.ir.instructions.specialized.OneFixnumArgNoBlockCallInstr;
 import org.jruby.ir.instructions.specialized.OneFloatArgNoBlockCallInstr;
 import org.jruby.ir.instructions.specialized.OneOperandArgNoBlockCallInstr;
 import org.jruby.ir.instructions.specialized.ZeroOperandArgNoBlockCallInstr;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.operands.Boolean;
 
 /**
  * Superclass for IR visitors.
  */
 public abstract class IRVisitor {
     public void visit(Instr instr) {
         instr.visit(this);
     }
 
     public void visit(Operand operand) {
         operand.visit(this);
     }
 
     private void error(Object object) {
         throw new NotCompilableException("no visitor logic for " + object.getClass().getName() + " in " + getClass().getName());
     }
 
     // standard instructions
     public void AliasInstr(AliasInstr aliasinstr) { error(aliasinstr); }
     public void ArgScopeDepthInstr(ArgScopeDepthInstr instr) { error(instr); }
     public void AttrAssignInstr(AttrAssignInstr attrassigninstr) { error(attrassigninstr); }
     public void BacktickInstr(BacktickInstr instr) { error(instr); }
     public void BEQInstr(BEQInstr beqinstr) { error(beqinstr); }
     public void BFalseInstr(BFalseInstr bfalseinstr) { error(bfalseinstr); }
     public void BlockGivenInstr(BlockGivenInstr blockgiveninstr) { error(blockgiveninstr); }
     public void BNEInstr(BNEInstr bneinstr) { error(bneinstr); }
     public void BNilInstr(BNilInstr bnilinstr) { error(bnilinstr); }
     public void BreakInstr(BreakInstr breakinstr) { error(breakinstr); }
     public void BTrueInstr(BTrueInstr btrueinstr) { error(btrueinstr); }
     public void BUndefInstr(BUndefInstr bundefinstr) { error(bundefinstr); }
     public void BuildCompoundArrayInstr(BuildCompoundArrayInstr instr) { error(instr); }
     public void BuildCompoundStringInstr(BuildCompoundStringInstr instr) { error(instr); }
     public void BuildDynRegExpInstr(BuildDynRegExpInstr instr) { error(instr); }
     public void BuildRangeInstr(BuildRangeInstr instr) { error(instr); }
+    public void BuildSplatInstr(BuildSplatInstr instr) { error(instr); }
     public void CallInstr(CallInstr callinstr) { error(callinstr); }
     public void CheckArgsArrayArityInstr(CheckArgsArrayArityInstr checkargsarrayarityinstr) { error(checkargsarrayarityinstr); }
     public void CheckArityInstr(CheckArityInstr checkarityinstr) { error(checkarityinstr); }
     public void ClassSuperInstr(ClassSuperInstr classsuperinstr) { error(classsuperinstr); }
     public void ConstMissingInstr(ConstMissingInstr constmissinginstr) { error(constmissinginstr); }
     public void CopyInstr(CopyInstr copyinstr) { error(copyinstr); }
     public void DefineClassInstr(DefineClassInstr defineclassinstr) { error(defineclassinstr); }
     public void DefineClassMethodInstr(DefineClassMethodInstr defineclassmethodinstr) { error(defineclassmethodinstr); }
     public void DefineInstanceMethodInstr(DefineInstanceMethodInstr defineinstancemethodinstr) { error(defineinstancemethodinstr); }
     public void DefineMetaClassInstr(DefineMetaClassInstr definemetaclassinstr) { error(definemetaclassinstr); }
     public void DefineModuleInstr(DefineModuleInstr definemoduleinstr) { error(definemoduleinstr); }
     public void EQQInstr(EQQInstr eqqinstr) { error(eqqinstr); }
     public void ExceptionRegionEndMarkerInstr(ExceptionRegionEndMarkerInstr exceptionregionendmarkerinstr) { error(exceptionregionendmarkerinstr); }
     public void ExceptionRegionStartMarkerInstr(ExceptionRegionStartMarkerInstr exceptionregionstartmarkerinstr) { error(exceptionregionstartmarkerinstr); }
     public void GetClassVarContainerModuleInstr(GetClassVarContainerModuleInstr getclassvarcontainermoduleinstr) { error(getclassvarcontainermoduleinstr); }
     public void GetClassVariableInstr(GetClassVariableInstr getclassvariableinstr) { error(getclassvariableinstr); }
     public void GetFieldInstr(GetFieldInstr getfieldinstr) { error(getfieldinstr); }
     public void GetGlobalVariableInstr(GetGlobalVariableInstr getglobalvariableinstr) { error(getglobalvariableinstr); }
     public void GVarAliasInstr(GVarAliasInstr gvaraliasinstr) { error(gvaraliasinstr); }
     public void InheritanceSearchConstInstr(InheritanceSearchConstInstr inheritancesearchconstinstr) { error(inheritancesearchconstinstr); }
     public void InstanceSuperInstr(InstanceSuperInstr instancesuperinstr) { error(instancesuperinstr); }
     public void Instr(Instr instr) { error(instr); }
     public void JumpInstr(JumpInstr jumpinstr) { error(jumpinstr); }
     public void LabelInstr(LabelInstr labelinstr) { error(labelinstr); }
     public void LexicalSearchConstInstr(LexicalSearchConstInstr lexicalsearchconstinstr) { error(lexicalsearchconstinstr); }
     public void LineNumberInstr(LineNumberInstr linenumberinstr) { error(linenumberinstr); }
     public void LoadLocalVarInstr(LoadLocalVarInstr loadlocalvarinstr) { error(loadlocalvarinstr); }
     public void Match2Instr(Match2Instr match2instr) { error(match2instr); }
     public void Match3Instr(Match3Instr match3instr) { error(match3instr); }
     public void MatchInstr(MatchInstr matchinstr) { error(matchinstr); }
     public void ModuleVersionGuardInstr(ModuleVersionGuardInstr moduleversionguardinstr) { error(moduleversionguardinstr); }
     public void NonlocalReturnInstr(NonlocalReturnInstr nonlocalreturninstr) { error(nonlocalreturninstr); }
     public void NopInstr(NopInstr nopinstr) { error(nopinstr); }
     public void NoResultCallInstr(NoResultCallInstr noresultcallinstr) { error(noresultcallinstr); }
     public void OneFixnumArgNoBlockCallInstr(OneFixnumArgNoBlockCallInstr oneFixnumArgNoBlockCallInstr) { error(oneFixnumArgNoBlockCallInstr); }
     public void OneFloatArgNoBlockCallInstr(OneFloatArgNoBlockCallInstr oneFloatArgNoBlockCallInstr) { error(oneFloatArgNoBlockCallInstr); }
     public void OneOperandArgNoBlockCallInstr(OneOperandArgNoBlockCallInstr oneOperandArgNoBlockCallInstr) { error(oneOperandArgNoBlockCallInstr); }
     public void OptArgMultipleAsgnInstr(OptArgMultipleAsgnInstr optargmultipleasgninstr) { error(optargmultipleasgninstr); }
     public void PopBindingInstr(PopBindingInstr popbindinginstr) { error(popbindinginstr); }
     public void PopFrameInstr(PopFrameInstr popframeinstr) { error(popframeinstr); }
     public void ProcessModuleBodyInstr(ProcessModuleBodyInstr processmodulebodyinstr) { error(processmodulebodyinstr); }
     public void PutClassVariableInstr(PutClassVariableInstr putclassvariableinstr) { error(putclassvariableinstr); }
     public void PutConstInstr(PutConstInstr putconstinstr) { error(putconstinstr); }
     public void PutFieldInstr(PutFieldInstr putfieldinstr) { error(putfieldinstr); }
     public void PutGlobalVarInstr(PutGlobalVarInstr putglobalvarinstr) { error(putglobalvarinstr); }
     public void PushBindingInstr(PushBindingInstr pushbindinginstr) { error(pushbindinginstr); }
     public void PushFrameInstr(PushFrameInstr pushframeinstr) { error(pushframeinstr); }
     public void RaiseArgumentErrorInstr(RaiseArgumentErrorInstr raiseargumenterrorinstr) { error(raiseargumenterrorinstr); }
     public void RaiseRequiredKeywordArgumentErrorInstr(RaiseRequiredKeywordArgumentError instr) { error(instr); }
     public void ReceiveClosureInstr(ReceiveClosureInstr receiveclosureinstr) { error(receiveclosureinstr); }
     public void ReceiveRubyExceptionInstr(ReceiveRubyExceptionInstr receiveexceptioninstr) { error(receiveexceptioninstr); }
     public void ReceiveJRubyExceptionInstr(ReceiveJRubyExceptionInstr receiveexceptioninstr) { error(receiveexceptioninstr); }
     public void ReceiveKeywordArgInstr(ReceiveKeywordArgInstr receiveKeywordArgInstr) { error(receiveKeywordArgInstr); }
     public void ReceiveKeywordRestArgInstr(ReceiveKeywordRestArgInstr receiveKeywordRestArgInstr) { error(receiveKeywordRestArgInstr); }
     public void ReceiveOptArgInstr(ReceiveOptArgInstr receiveoptarginstr) { error(receiveoptarginstr); }
     public void ReceivePreReqdArgInstr(ReceivePreReqdArgInstr receiveprereqdarginstr) { error(receiveprereqdarginstr); }
     public void ReceiveRestArgInstr(ReceiveRestArgInstr receiverestarginstr) { error(receiverestarginstr); }
     public void ReceiveSelfInstr(ReceiveSelfInstr receiveselfinstr) { error(receiveselfinstr); }
     public void RecordEndBlockInstr(RecordEndBlockInstr recordendblockinstr) { error(recordendblockinstr); }
     public void ReqdArgMultipleAsgnInstr(ReqdArgMultipleAsgnInstr reqdargmultipleasgninstr) { error(reqdargmultipleasgninstr); }
     public void RescueEQQInstr(RescueEQQInstr rescueeqqinstr) { error(rescueeqqinstr); }
     public void RestArgMultipleAsgnInstr(RestArgMultipleAsgnInstr restargmultipleasgninstr) { error(restargmultipleasgninstr); }
     public void ReturnInstr(ReturnInstr returninstr) { error(returninstr); }
     public void RuntimeHelperCall(RuntimeHelperCall runtimehelpercall) { error(runtimehelpercall); }
     public void SearchConstInstr(SearchConstInstr searchconstinstr) { error(searchconstinstr); }
     public void SetCapturedVarInstr(SetCapturedVarInstr instr) { error(instr); }
     public void StoreLocalVarInstr(StoreLocalVarInstr storelocalvarinstr) { error(storelocalvarinstr); }
     public void ThreadPollInstr(ThreadPollInstr threadpollinstr) { error(threadpollinstr); }
     public void ThrowExceptionInstr(ThrowExceptionInstr throwexceptioninstr) { error(throwexceptioninstr); }
     public void ToAryInstr(ToAryInstr toaryinstr) { error(toaryinstr); }
     public void UndefMethodInstr(UndefMethodInstr undefmethodinstr) { error(undefmethodinstr); }
     public void UnresolvedSuperInstr(UnresolvedSuperInstr unresolvedsuperinstr) { error(unresolvedsuperinstr); }
     public void YieldInstr(YieldInstr yieldinstr) { error(yieldinstr); }
     public void ZeroOperandArgNoBlockCallInstr(ZeroOperandArgNoBlockCallInstr zeroOperandArgNoBlockCallInstr) { error(zeroOperandArgNoBlockCallInstr); }
     public void ZSuperInstr(ZSuperInstr zsuperinstr) { error(zsuperinstr); }
 
     // "defined" instructions
     public void GetErrorInfoInstr(GetErrorInfoInstr geterrorinfoinstr) { error(geterrorinfoinstr); }
     public void RestoreErrorInfoInstr(RestoreErrorInfoInstr restoreerrorinfoinstr) { error(restoreerrorinfoinstr); }
 
     // ruby 1.9 specific
     public void BuildLambdaInstr(BuildLambdaInstr buildlambdainstr) { error(buildlambdainstr); }
     public void GetEncodingInstr(GetEncodingInstr getencodinginstr) { error(getencodinginstr); }
     public void ReceivePostReqdArgInstr(ReceivePostReqdArgInstr receivepostreqdarginstr) { error(receivepostreqdarginstr); }
 
     // unboxing instrs
     public void BoxFloatInstr(BoxFloatInstr instr) { error(instr); }
     public void BoxFixnumInstr(BoxFixnumInstr instr) { error(instr); }
     public void BoxBooleanInstr(BoxBooleanInstr instr) { error(instr); }
     public void AluInstr(AluInstr instr) { error(instr); }
     public void UnboxFloatInstr(UnboxFloatInstr instr) { error(instr); }
     public void UnboxFixnumInstr(UnboxFixnumInstr instr) { error(instr); }
     public void UnboxBooleanInstr(UnboxBooleanInstr instr) { error(instr); }
 
     // operands
     public void Array(Array array) { error(array); }
     public void AsString(AsString asstring) { error(asstring); }
     public void Backref(Backref backref) { error(backref); }
     public void Bignum(Bignum bignum) { error(bignum); }
     public void Boolean(Boolean bool) { error(bool); }
     public void UnboxedBoolean(UnboxedBoolean bool) { error(bool); }
     public void ClosureLocalVariable(ClosureLocalVariable closurelocalvariable) { error(closurelocalvariable); }
     public void Complex(Complex complex) { error(complex); }
     public void CurrentScope(CurrentScope currentscope) { error(currentscope); }
     public void DynamicSymbol(DynamicSymbol dynamicsymbol) { error(dynamicsymbol); }
     public void Fixnum(Fixnum fixnum) { error(fixnum); }
     public void FrozenString(FrozenString frozen) { error(frozen); }
     public void UnboxedFixnum(UnboxedFixnum fixnum) { error(fixnum); }
     public void Float(org.jruby.ir.operands.Float flote) { error(flote); }
     public void UnboxedFloat(org.jruby.ir.operands.UnboxedFloat flote) { error(flote); }
     public void GlobalVariable(GlobalVariable globalvariable) { error(globalvariable); }
     public void Hash(Hash hash) { error(hash); }
     public void IRException(IRException irexception) { error(irexception); }
     public void Label(Label label) { error(label); }
     public void LocalVariable(LocalVariable localvariable) { error(localvariable); }
     public void Nil(Nil nil) { error(nil); }
     public void NthRef(NthRef nthref) { error(nthref); }
     public void ObjectClass(ObjectClass objectclass) { error(objectclass); }
     public void Rational(Rational rational) { error(rational); }
     public void Regexp(Regexp regexp) { error(regexp); }
     public void ScopeModule(ScopeModule scopemodule) { error(scopemodule); }
     public void Self(Self self) { error(self); }
     public void Splat(Splat splat) { error(splat); }
     public void StandardError(StandardError standarderror) { error(standarderror); }
     public void StringLiteral(StringLiteral stringliteral) { error(stringliteral); }
     public void SValue(SValue svalue) { error(svalue); }
     public void Symbol(Symbol symbol) { error(symbol); }
     public void TemporaryVariable(TemporaryVariable temporaryvariable) { error(temporaryvariable); }
     public void TemporaryLocalVariable(TemporaryLocalVariable temporarylocalvariable) { error(temporarylocalvariable); }
     public void TemporaryFloatVariable(TemporaryFloatVariable temporaryfloatvariable) { error(temporaryfloatvariable); }
     public void TemporaryFixnumVariable(TemporaryFixnumVariable temporaryfixnumvariable) { error(temporaryfixnumvariable); }
     public void TemporaryBooleanVariable(TemporaryBooleanVariable temporarybooleanvariable) { error(temporarybooleanvariable); }
     public void UndefinedValue(UndefinedValue undefinedvalue) { error(undefinedvalue); }
     public void UnexecutableNil(UnexecutableNil unexecutablenil) { error(unexecutablenil); }
     public void WrappedIRClosure(WrappedIRClosure wrappedirclosure) { error(wrappedirclosure); }
 }
diff --git a/core/src/main/java/org/jruby/ir/Operation.java b/core/src/main/java/org/jruby/ir/Operation.java
index 4791b95cfb..82808df26c 100644
--- a/core/src/main/java/org/jruby/ir/Operation.java
+++ b/core/src/main/java/org/jruby/ir/Operation.java
@@ -1,312 +1,313 @@
 package org.jruby.ir;
 
 class OpFlags {
     final static int f_has_side_effect     = 0x00001; // Used by analyses
     final static int f_can_raise_exception = 0x00002; // Used by analyses
     final static int f_is_marker_op        = 0x00004; // UNUSED
     final static int f_is_jump_or_branch   = 0x00008; // Used by analyses
     final static int f_is_return           = 0x00010; // Used by analyses
     final static int f_is_exception        = 0x00020; // Used by analyses
     final static int f_is_debug_op         = 0x00040; // Used by analyses
     final static int f_is_load             = 0x00080; // UNUSED
     final static int f_is_store            = 0x00100; // UNUSED
     final static int f_is_call             = 0x00200; // Only used to opt. interpreter loop
     final static int f_is_arg_receive      = 0x00400; // Only used to opt. interpreter loop
     final static int f_modifies_code       = 0x00800; // Profiler uses this
     final static int f_inline_unfriendly   = 0x01000; // UNUSED: Inliner might use this later
     final static int f_is_book_keeping_op  = 0x02000; // Only used to opt. interpreter loop
     final static int f_is_float_op         = 0x04000; // Only used to opt. interpreter loop
     final static int f_is_int_op           = 0x08000; // Only used to opt. interpreter loop
 }
 
 public enum Operation {
 /* Mark a *non-control-flow* instruction as side-effecting if its compuation is not referentially
  * transparent.  In other words, mark it side-effecting if the following is true:
  *
  *   If "r = op(args)" is the instruction I and v is the value produced by the instruction at runtime,
  *   and replacing I with "r = v" will leave the program behavior unchanged.  If so, and we determine
  *   that the value of 'r' is not used anywhere, then it would be safe to get rid of I altogether.
  *
  * So definitions, calls, returns, stores are all side-effecting by this definition */
 
 // ------ Define the operations below ----
     NOP(0),
 
     /** control-flow **/
     JUMP(OpFlags.f_is_jump_or_branch),
     BEQ(OpFlags.f_is_jump_or_branch),
     BNE(OpFlags.f_is_jump_or_branch),
     B_UNDEF(OpFlags.f_is_jump_or_branch),
     B_NIL(OpFlags.f_is_jump_or_branch),
     B_TRUE(OpFlags.f_is_jump_or_branch),
     B_FALSE(OpFlags.f_is_jump_or_branch),
 
     /** argument receive in methods and blocks **/
     RECV_SELF(0),
     RECV_PRE_REQD_ARG(OpFlags.f_is_arg_receive),
     RECV_POST_REQD_ARG(OpFlags.f_is_arg_receive),
     RECV_KW_ARG(OpFlags.f_is_arg_receive),
     RECV_KW_REST_ARG(OpFlags.f_is_arg_receive),
     RECV_REST_ARG(OpFlags.f_is_arg_receive),
     RECV_OPT_ARG(OpFlags.f_is_arg_receive),
     RECV_CLOSURE(OpFlags.f_is_arg_receive),
     RECV_RUBY_EXC(OpFlags.f_is_arg_receive),
     RECV_JRUBY_EXC(OpFlags.f_is_arg_receive),
 
     /* By default, call instructions cannot be deleted even if their results
      * aren't used by anyone unless we know more about what the call is,
      * what it does, etc.  Hence all these are marked side effecting */
 
     /** calls **/
     CALL(OpFlags.f_has_side_effect | OpFlags.f_is_call | OpFlags.f_can_raise_exception),
     NORESULT_CALL(OpFlags.f_has_side_effect | OpFlags.f_is_call | OpFlags.f_can_raise_exception),
     ATTR_ASSIGN(OpFlags.f_is_call | OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception),
     CLASS_SUPER(OpFlags.f_has_side_effect | OpFlags.f_is_call | OpFlags.f_can_raise_exception),
     INSTANCE_SUPER(OpFlags.f_has_side_effect | OpFlags.f_is_call | OpFlags.f_can_raise_exception),
     UNRESOLVED_SUPER(OpFlags.f_has_side_effect | OpFlags.f_is_call | OpFlags.f_can_raise_exception),
     ZSUPER(OpFlags.f_has_side_effect | OpFlags.f_is_call | OpFlags.f_can_raise_exception),
 
     /* specialized calls */
     CALL_1F(OpFlags.f_has_side_effect | OpFlags.f_is_call | OpFlags.f_can_raise_exception),
     CALL_1D(OpFlags.f_has_side_effect | OpFlags.f_is_call | OpFlags.f_can_raise_exception),
     CALL_1O(OpFlags.f_has_side_effect | OpFlags.f_is_call | OpFlags.f_can_raise_exception),
     CALL_1OB(OpFlags.f_has_side_effect | OpFlags.f_is_call | OpFlags.f_can_raise_exception),
     CALL_0O(OpFlags.f_has_side_effect | OpFlags.f_is_call | OpFlags.f_can_raise_exception),
     NORESULT_CALL_1O(OpFlags.f_has_side_effect | OpFlags.f_is_call | OpFlags.f_can_raise_exception),
 
     /** Ruby operators: should all these be calls? Implementing instrs don't inherit from CallBase.java */
     EQQ(OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception), // a === call used in when
     LAMBDA(OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception),
     MATCH(OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception),
     MATCH2(OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception),
     MATCH3(OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception),
 
     /* Yield: Is this a call? Implementing instr doesn't inherit from CallBase.java */
     YIELD(OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception),
 
     /** returns -- returns unwind stack, etc. */
     RETURN(OpFlags.f_has_side_effect | OpFlags.f_is_return),
     NONLOCAL_RETURN(OpFlags.f_has_side_effect | OpFlags.f_is_return),
     /* BREAK is a return because it can only be used within closures
      * and the net result is to return from the closure. */
     BREAK(OpFlags.f_has_side_effect | OpFlags.f_is_return),
 
     /** defines **/
     ALIAS(OpFlags.f_has_side_effect| OpFlags.f_modifies_code | OpFlags.f_can_raise_exception),
     DEF_MODULE(OpFlags.f_has_side_effect | OpFlags.f_modifies_code | OpFlags.f_inline_unfriendly | OpFlags.f_can_raise_exception),
     DEF_CLASS(OpFlags.f_has_side_effect | OpFlags.f_modifies_code | OpFlags.f_inline_unfriendly | OpFlags.f_can_raise_exception),
     DEF_META_CLASS(OpFlags.f_has_side_effect | OpFlags.f_modifies_code | OpFlags.f_inline_unfriendly | OpFlags.f_can_raise_exception),
     DEF_INST_METH(OpFlags.f_has_side_effect | OpFlags.f_modifies_code | OpFlags.f_inline_unfriendly | OpFlags.f_can_raise_exception),
     DEF_CLASS_METH(OpFlags.f_has_side_effect | OpFlags.f_modifies_code | OpFlags.f_inline_unfriendly | OpFlags.f_can_raise_exception),
     PROCESS_MODULE_BODY(OpFlags.f_has_side_effect | OpFlags.f_modifies_code | OpFlags.f_inline_unfriendly | OpFlags.f_can_raise_exception),
     UNDEF_METHOD(OpFlags.f_has_side_effect | OpFlags.f_modifies_code | OpFlags.f_can_raise_exception),
 
     /** SSS FIXME: This can throw an exception only in tracing mode
      ** Should override canRaiseException in GVarAliasInstr to implement this maybe */
     GVAR_ALIAS(OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception | OpFlags.f_modifies_code),
 
     /** marker instructions used to flag/mark places in the code and dont actually get executed **/
     LABEL(OpFlags.f_is_book_keeping_op | OpFlags.f_is_marker_op),
     EXC_REGION_START(OpFlags.f_is_book_keeping_op | OpFlags.f_is_marker_op),
     EXC_REGION_END(OpFlags.f_is_book_keeping_op | OpFlags.f_is_marker_op),
 
     /** constant operations */
     LEXICAL_SEARCH_CONST(OpFlags.f_can_raise_exception),
     INHERITANCE_SEARCH_CONST(OpFlags.f_can_raise_exception),
     CONST_MISSING(OpFlags.f_can_raise_exception),
     SEARCH_CONST(OpFlags.f_can_raise_exception),
 
     GET_GLOBAL_VAR(OpFlags.f_is_load),
     GET_FIELD(OpFlags.f_is_load),
     /** SSS FIXME: Document what causes this instr to raise an exception */
     GET_CVAR(OpFlags.f_is_load | OpFlags.f_can_raise_exception),
 
     /** value stores **/
     // SSS FIXME: Not all global variable sets can throw exceptions.  Should we split this
     // operation into two different operations?  Those that can throw exceptions and those
     // that cannot.  But, for now, this should be good enough
     PUT_GLOBAL_VAR(OpFlags.f_is_store | OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception),
     // put_const, put_cvar, put_field can raise exception trying to store into a frozen objects
     PUT_CONST(OpFlags.f_is_store | OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception),
     PUT_CVAR(OpFlags.f_is_store | OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception),
     PUT_FIELD(OpFlags.f_is_store | OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception),
 
     /** debugging ops **/
     LINE_NUM(OpFlags.f_is_book_keeping_op | OpFlags.f_is_debug_op),
     TRACE(OpFlags.f_is_book_keeping_op | OpFlags.f_is_debug_op | OpFlags.f_has_side_effect),
 
     /** JRuby-impl instructions **/
     ARG_SCOPE_DEPTH(0),
     BINDING_LOAD(OpFlags.f_is_load),
     BINDING_STORE(OpFlags.f_is_store | OpFlags.f_has_side_effect),
     BUILD_COMPOUND_ARRAY(OpFlags.f_can_raise_exception),
     BUILD_COMPOUND_STRING(OpFlags.f_can_raise_exception),
     BUILD_DREGEXP(OpFlags.f_can_raise_exception),
     BUILD_RANGE(OpFlags.f_can_raise_exception),
+    BUILD_SPLAT(OpFlags.f_can_raise_exception),
     BACKTICK_STRING(OpFlags.f_can_raise_exception),
     CHECK_ARGS_ARRAY_ARITY(OpFlags.f_can_raise_exception),
     CHECK_ARITY(OpFlags.f_is_book_keeping_op | OpFlags.f_can_raise_exception),
     CLASS_VAR_MODULE(0),
     COPY(0),
     GET_ENCODING(0),
     MASGN_OPT(0),
     MASGN_REQD(0),
     MASGN_REST(0),
     RAISE_ARGUMENT_ERROR(OpFlags.f_can_raise_exception),
     RAISE_REQUIRED_KEYWORD_ARGUMENT_ERROR(OpFlags.f_can_raise_exception),
     RECORD_END_BLOCK(OpFlags.f_has_side_effect),
     RESCUE_EQQ(OpFlags.f_can_raise_exception), // a === call used in rescue
     RUNTIME_HELPER(OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception),
     SET_CAPTURED_VAR(OpFlags.f_can_raise_exception),
     THREAD_POLL(OpFlags.f_is_book_keeping_op | OpFlags.f_has_side_effect),
     THROW(OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception | OpFlags.f_is_exception),
     // FIXME: TO_ARY is marked side-effecting since it can allocate new objects
     // Clarify semantics of 'f_has_side_effect' better
     TO_ARY(OpFlags.f_has_side_effect | OpFlags.f_can_raise_exception),
 
     /* Instructions to support defined? */
     BLOCK_GIVEN(0),
     DEFINED_CONSTANT_OR_METHOD(OpFlags.f_can_raise_exception),
     GET_ERROR_INFO(0),
     METHOD_DEFINED(OpFlags.f_can_raise_exception),
     RESTORE_ERROR_INFO(OpFlags.f_has_side_effect),
 
     /* Boxing/Unboxing between Ruby <--> Java types */
     BOX_FIXNUM(0),
     BOX_FLOAT(0),
     BOX_BOOLEAN(0),
     UNBOX_FIXNUM(0),
     UNBOX_FLOAT(0),
     UNBOX_BOOLEAN(0),
 
     /* Unboxed ALU ops */
     IADD(OpFlags.f_is_int_op),
     ISUB(OpFlags.f_is_int_op),
     IMUL(OpFlags.f_is_int_op),
     IDIV(OpFlags.f_is_int_op),
     ILT(OpFlags.f_is_int_op),
     IGT(OpFlags.f_is_int_op),
     IOR(OpFlags.f_is_int_op),
     IAND(OpFlags.f_is_int_op),
     IXOR(OpFlags.f_is_int_op),
     ISHL(OpFlags.f_is_int_op),
     ISHR(OpFlags.f_is_int_op),
     IEQ(OpFlags.f_is_float_op),
     FADD(OpFlags.f_is_float_op),
     FSUB(OpFlags.f_is_float_op),
     FMUL(OpFlags.f_is_float_op),
     FDIV(OpFlags.f_is_float_op),
     FLT(OpFlags.f_is_float_op),
     FGT(OpFlags.f_is_float_op),
     FEQ(OpFlags.f_is_float_op),
 
     /** Other JRuby internal primitives for optimizations */
     MODULE_GUARD(OpFlags.f_is_jump_or_branch), /* a guard acts as a branch */
     PUSH_FRAME(OpFlags.f_is_book_keeping_op | OpFlags.f_has_side_effect),
     PUSH_BINDING(OpFlags.f_is_book_keeping_op | OpFlags.f_has_side_effect),
     POP_FRAME(OpFlags.f_is_book_keeping_op | OpFlags.f_has_side_effect),
     POP_BINDING(OpFlags.f_is_book_keeping_op | OpFlags.f_has_side_effect);
 
     public final OpClass opClass;
     private int flags;
 
     Operation(int flags) {
         this.flags = flags;
 
         if (this.isArgReceive()) {
             this.opClass = OpClass.ARG_OP;
         } else if ((flags & OpFlags.f_is_return) > 0) {
             this.opClass = OpClass.RET_OP;
         } else if (this.isBranch()) {
             this.opClass = OpClass.BRANCH_OP;
         } else if (this.isBookKeepingOp()) {
             this.opClass = OpClass.BOOK_KEEPING_OP;
         } else if (this.isCall()) {
             this.opClass = OpClass.CALL_OP;
         } else if ((flags & OpFlags.f_is_int_op) > 0) {
             this.opClass = OpClass.INT_OP;
         } else if ((flags & OpFlags.f_is_float_op) > 0) {
             this.opClass = OpClass.FLOAT_OP;
         } else {
             this.opClass = OpClass.OTHER_OP;
         }
     }
 
     public boolean transfersControl() {
         return (flags & (OpFlags.f_is_jump_or_branch | OpFlags.f_is_return | OpFlags.f_is_exception)) > 0;
     }
 
     public boolean isLoad() {
         return (flags & OpFlags.f_is_load) > 0;
     }
 
     public boolean isStore() {
         return (flags & OpFlags.f_is_store) > 0;
     }
 
     public boolean isCall() {
         return (flags & OpFlags.f_is_call) > 0;
     }
 
     public boolean isBranch() {
         return (flags & OpFlags.f_is_jump_or_branch) > 0;
     }
 
     public boolean isReturn() {
         return (flags & OpFlags.f_is_return) > 0;
     }
 
     public boolean isException() {
         return (flags & OpFlags.f_is_exception) > 0;
     }
 
     public boolean isArgReceive() {
         return (flags & OpFlags.f_is_arg_receive) > 0;
     }
 
     public boolean startsBasicBlock() {
         return this == LABEL;
     }
 
     /**
      * The last instruction in the BB which will exit the BB.  Note:  This also
      * means any instructions past this point in that BB are unreachable.
      */
     public boolean endsBasicBlock() {
         return transfersControl();
     }
 
     public boolean hasSideEffects() {
         return (flags & OpFlags.f_has_side_effect) > 0;
     }
 
     public boolean isDebugOp() {
         return (flags & OpFlags.f_is_debug_op) > 0;
     }
 
     public boolean isBookKeepingOp() {
         return (flags & OpFlags.f_is_book_keeping_op) > 0;
     }
 
     // Conservative -- say no only if you know it for sure cannot
     public boolean canRaiseException() {
         return (flags & OpFlags.f_can_raise_exception) > 0;
     }
 
     public boolean modifiesCode() {
         return (flags & OpFlags.f_modifies_code) > 0;
     }
 
     public boolean inlineUnfriendly() {
         return (flags & OpFlags.f_inline_unfriendly) > 0;
     }
 
     @Override
     public String toString() {
         return name().toLowerCase();
     }
 
     public static Operation fromOrdinal(int value) {
         return value < 0 || value >= values().length ? null : values()[value];
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/instructions/BuildSplatInstr.java b/core/src/main/java/org/jruby/ir/instructions/BuildSplatInstr.java
new file mode 100644
index 0000000000..1d5ad82daa
--- /dev/null
+++ b/core/src/main/java/org/jruby/ir/instructions/BuildSplatInstr.java
@@ -0,0 +1,71 @@
+package org.jruby.ir.instructions;
+
+import org.jruby.ir.IRVisitor;
+import org.jruby.ir.Operation;
+import org.jruby.ir.operands.Operand;
+import org.jruby.ir.operands.Variable;
+import org.jruby.ir.runtime.IRRuntimeHelpers;
+import org.jruby.ir.transformations.inlining.CloneInfo;
+import org.jruby.parser.StaticScope;
+import org.jruby.runtime.DynamicScope;
+import org.jruby.runtime.ThreadContext;
+import org.jruby.runtime.builtin.IRubyObject;
+
+import java.util.Map;
+
+// Represents a splat value in Ruby code: *array
+public class BuildSplatInstr extends Instr implements ResultInstr {
+    private Variable result;
+    private Operand array;
+
+    public BuildSplatInstr(Variable result, Operand array) {
+        super(Operation.BUILD_SPLAT);
+        this.result = result;
+        this.array = array;
+    }
+
+    @Override
+    public String toString() {
+        return result + " = *" + array;
+    }
+
+    @Override
+    public Variable getResult() {
+        return result;
+    }
+
+    @Override
+    public void updateResult(Variable v) {
+        this.result = v;
+    }
+
+    public Operand getArray() {
+        return array;
+    }
+
+    @Override
+    public Operand[] getOperands() {
+        return new Operand[] { array };
+    }
+
+    @Override
+    public void simplifyOperands(Map<Operand, Operand> valueMap, boolean force) {
+        array = array.getSimplifiedOperand(valueMap, force);
+    }
+
+    @Override
+    public Instr clone(CloneInfo ii) {
+        return new BuildSplatInstr(ii.getRenamedVariable(result), array.cloneForInlining(ii));
+    }
+
+    @Override
+    public Object interpret(ThreadContext context, StaticScope currScope, DynamicScope currDynScope, IRubyObject self, Object[] temp) {
+        IRubyObject arrayVal = (IRubyObject) array.retrieve(context, self, currScope, currDynScope, temp);
+        return IRRuntimeHelpers.irSplat(context, arrayVal);
+    }
+
+    @Override
+    public void visit(IRVisitor visitor) {
+        visitor.BuildSplatInstr(this);
+    }
+}
diff --git a/core/src/main/java/org/jruby/ir/instructions/CallBase.java b/core/src/main/java/org/jruby/ir/instructions/CallBase.java
index a039b8561c..93459745de 100644
--- a/core/src/main/java/org/jruby/ir/instructions/CallBase.java
+++ b/core/src/main/java/org/jruby/ir/instructions/CallBase.java
@@ -1,485 +1,485 @@
 package org.jruby.ir.instructions;
 
 import org.jruby.RubyArray;
 import org.jruby.ir.IRScope;
 import org.jruby.ir.Operation;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.operands.Float;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.ir.transformations.inlining.CloneInfo;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.*;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 import java.util.Map;
 
 import static org.jruby.ir.IRFlags.*;
 
 public abstract class CallBase extends Instr implements ClosureAcceptingInstr {
     private static long callSiteCounter = 1;
 
     public final long callSiteId;
     private final CallType callType;
     protected Operand   receiver;
     protected Operand[] arguments;
     protected Operand   closure;
     protected String name;
     protected CallSite callSite;
 
     private boolean flagsComputed;
     private boolean canBeEval;
     private boolean targetRequiresCallersBinding;    // Does this call make use of the caller's binding?
     private boolean targetRequiresCallersFrame;    // Does this call make use of the caller's frame?
     private boolean dontInline;
     private boolean containsArgSplat;
     private boolean procNew;
 
     protected CallBase(Operation op, CallType callType, String name, Operand receiver, Operand[] args, Operand closure) {
         super(op);
 
         this.callSiteId = callSiteCounter++;
         this.receiver = receiver;
         this.arguments = args;
         this.closure = closure;
         this.name = name;
         this.callType = callType;
         this.callSite = getCallSiteFor(callType, name);
         containsArgSplat = containsArgSplat(args);
         flagsComputed = false;
         canBeEval = true;
         targetRequiresCallersBinding = true;
         targetRequiresCallersFrame = true;
         dontInline = false;
         procNew = false;
     }
 
     @Override
     public Operand[] getOperands() {
         // -0 is not possible so we add 1 to arguments with closure so we get a valid negative value.
         Fixnum arity = new Fixnum(closure != null ? -1*(arguments.length + 1) : arguments.length);
         return buildAllArgs(new Fixnum(callType.ordinal()), receiver, arity, arguments, closure);
     }
 
     public String getName() {
         return name;
     }
 
     /** From interface ClosureAcceptingInstr */
     public Operand getClosureArg() {
         return closure;
     }
 
     public Operand getClosureArg(Operand ifUnspecified) {
         return closure == null ? ifUnspecified : closure;
     }
 
     public Operand getReceiver() {
         return receiver;
     }
 
     public Operand[] getCallArgs() {
         return arguments;
     }
 
     public CallSite getCallSite() {
         return callSite;
     }
 
     public CallType getCallType() {
         return callType;
     }
 
     public boolean containsArgSplat() {
         return containsArgSplat;
     }
 
     public boolean isProcNew() {
         return procNew;
     }
 
     public void setProcNew(boolean procNew) {
         this.procNew = procNew;
     }
 
     public void blockInlining() {
         dontInline = true;
     }
 
     public boolean inliningBlocked() {
         return dontInline;
     }
 
     private static CallSite getCallSiteFor(CallType callType, String name) {
         assert callType != null: "Calltype should never be null";
 
         switch (callType) {
             case NORMAL: return MethodIndex.getCallSite(name);
             case FUNCTIONAL: return MethodIndex.getFunctionalCallSite(name);
             case VARIABLE: return MethodIndex.getVariableCallSite(name);
             case SUPER: return MethodIndex.getSuperCallSite();
             case UNKNOWN:
         }
 
         return null; // fallthrough for unknown
     }
 
     public boolean hasClosure() {
         return closure != null;
     }
 
     public boolean hasLiteralClosure() {
         return closure instanceof WrappedIRClosure;
     }
 
     public boolean isAllConstants() {
         for (Operand argument : arguments) {
             if (!(argument instanceof ImmutableLiteral)) return false;
         }
 
         return true;
     }
 
     public static boolean isAllFixnums(Operand[] args) {
         for (Operand argument : args) {
             if (!(argument instanceof Fixnum)) return false;
         }
 
         return true;
     }
 
     public boolean isAllFixnums() {
         return isAllFixnums(arguments);
     }
 
     public static boolean isAllFloats(Operand[] args) {
         for (Operand argument : args) {
             if (!(argument instanceof Float)) return false;
         }
 
         return true;
     }
 
     public boolean isAllFloats() {
         return isAllFloats(arguments);
     }
 
     @Override
     public boolean computeScopeFlags(IRScope scope) {
         boolean modifiedScope = false;
 
         if (targetRequiresCallersBinding()) {
             modifiedScope = true;
             scope.getFlags().add(BINDING_HAS_ESCAPED);
         }
 
         if (targetRequiresCallersFrame()) {
             modifiedScope = true;
             scope.getFlags().add(REQUIRES_FRAME);
         }
 
         if (canBeEval()) {
             modifiedScope = true;
             scope.getFlags().add(USES_EVAL);
 
             // If this method receives a closure arg, and this call is an eval that has more than 1 argument,
             // it could be using the closure as a binding -- which means it could be using pretty much any
             // variable from the caller's binding!
             if (scope.getFlags().contains(RECEIVES_CLOSURE_ARG) && (getCallArgs().length > 1)) {
                 scope.getFlags().add(CAN_CAPTURE_CALLERS_BINDING);
             }
         }
 
         // Kernel.local_variables inspects variables.
         // and JRuby implementation uses dyn-scope to access the static-scope
         // to output the local variables => we cannot strip dynscope in those cases.
         // FIXME: We need to decouple static-scope and dyn-scope.
         String mname = getName();
         if (mname.equals("local_variables")) {
             scope.getFlags().add(REQUIRES_DYNSCOPE);
         } else if (mname.equals("send") || mname.equals("__send__")) {
             Operand[] args = getCallArgs();
             if (args.length >= 1) {
                 Operand meth = args[0];
                 if (meth instanceof StringLiteral && "local_variables".equals(((StringLiteral)meth).string)) {
                     scope.getFlags().add(REQUIRES_DYNSCOPE);
                 }
             }
         }
 
         return modifiedScope;
     }
 
     @Override
     public void simplifyOperands(Map<Operand, Operand> valueMap, boolean force) {
         // FIXME: receiver should never be null (checkArity seems to be one culprit)
         if (receiver != null) receiver = receiver.getSimplifiedOperand(valueMap, force);
 
         for (int i = 0; i < arguments.length; i++) {
             arguments[i] = arguments[i].getSimplifiedOperand(valueMap, force);
         }
 
         // Recompute containsArgSplat flag
         containsArgSplat = containsArgSplat(arguments);
 
         if (closure != null) closure = closure.getSimplifiedOperand(valueMap, force);
         flagsComputed = false; // Forces recomputation of flags
 
         // recompute whenever instr operands change! (can this really change though?)
         callSite = getCallSiteFor(callType, name);
     }
 
     public Operand[] cloneCallArgs(CloneInfo ii) {
         int i = 0;
         Operand[] clonedArgs = new Operand[arguments.length];
 
         for (Operand a: arguments) {
             clonedArgs[i++] = a.cloneForInlining(ii);
         }
 
         return clonedArgs;
     }
 
     public boolean isRubyInternalsCall() {
         return false;
     }
 
     public boolean isStaticCallTarget() {
         return false;
     }
 
     // SSS FIXME: Are all bases covered?
     // How about aliasing of 'call', 'eval', 'send', 'module_eval', 'class_eval', 'instance_eval'?
     private boolean computeEvalFlag() {
         // ENEBO: This could be made into a recursive two-method thing so then: send(:send, :send, :send, :send, :eval, "Hosed") works
         String mname = getName();
         // checking for "call" is conservative.  It can be eval only if the receiver is a Method
         // CON: Removed "call" check because we didn't do it in 1.7 and it deopts all callers of Method or Proc objects.
         if (/*mname.equals("call") ||*/ mname.equals("eval") || mname.equals("module_eval") || mname.equals("class_eval") || mname.equals("instance_eval")) return true;
 
         // Calls to 'send' where the first arg is either unknown or is eval or send (any others?)
         if (mname.equals("send") || mname.equals("__send__")) {
             Operand[] args = getCallArgs();
             if (args.length >= 2) {
                 Operand meth = args[0];
                 if (!(meth instanceof StringLiteral)) return true; // We don't know
 
                 String name = ((StringLiteral) meth).string;
                 if (   name.equals("call")
                     || name.equals("eval")
                     || mname.equals("module_eval")
                     || mname.equals("class_eval")
                     || mname.equals("instance_eval")
                     || name.equals("send")
                     || name.equals("__send__")) return true;
             }
         }
 
         return false; // All checks passed
     }
 
     private boolean computeRequiresCallersBindingFlag() {
         if (canBeEval()) return true;
 
         // literal closures can be used to capture surrounding binding
         if (hasLiteralClosure()) return true;
 
         String mname = getName();
         if (MethodIndex.SCOPE_AWARE_METHODS.contains(mname)) {
             return true;
         } else if (mname.equals("send") || mname.equals("__send__")) {
             Operand[] args = getCallArgs();
             if (args.length >= 1) {
                 Operand meth = args[0];
                 if (!(meth instanceof StringLiteral)) return true; // We don't know -- could be anything
 
                 String name = ((StringLiteral) meth).string;
                 if (MethodIndex.SCOPE_AWARE_METHODS.contains(name)) {
                     return true;
                 }
             }
         }
 
         /* -------------------------------------------------------------
          * SSS FIXME: What about aliased accesses to these same methods?
          * See problem snippet below. To be clear, the problem with this
          * Module.nesting below is because that method uses DynamicScope
          * to access the static-scope. However, even if we moved the static-scope
          * to Frame, the problem just shifts over to optimizations that eliminate
          * push/pop of Frame objects from certain scopes.
          *
          * [subbu@earth ~/jruby] cat /tmp/pgm.rb
          * class Module
          *   class << self
          *     alias_method :foobar, :nesting
          *   end
          * end
          *
          * module X
          *   puts "X. Nesting is: #{Module.foobar}"
          * end
          *
          * module Y
          *   puts "Y. Nesting is: #{Module.nesting}"
          * end
          *
          * [subbu@earth ~/jruby] jruby -X-CIR -Xir.passes=OptimizeTempVarsPass,LocalOptimizationPass,AddLocalVarLoadStoreInstructions,AddCallProtocolInstructions,LinearizeCFG /tmp/pgm.rb
          * X. Nesting is: []
          * Y. Nesting is: [Y]
          * [subbu@earth ~/jruby] jruby -X-CIR -Xir.passes=LinearizeCFG /tmp/pgm.rb
          * X. Nesting is: [X]
          * Y. Nesting is: [Y]
          * ------------------------------------------------------------- */
 
         // SSS FIXME: Are all bases covered?
         return false;  // All checks done -- dont need one
     }
 
     private boolean computeRequiresCallersFrameFlag() {
         if (canBeEval()) return true;
 
         // literal closures can be used to capture surrounding binding
         if (hasLiteralClosure()) return true;
 
         if (procNew) return true;
 
         String mname = getName();
         if (MethodIndex.FRAME_AWARE_METHODS.contains(mname)) {
             // Known frame-aware methods.
             return true;
 
         } else if (mname.equals("send") || mname.equals("__send__")) {
             // Allow send to access full binding, since someone might send :eval and friends.
             Operand[] args = getCallArgs();
             if (args.length >= 1) {
                 Operand meth = args[0];
                 if (!(meth instanceof StringLiteral)) return true; // We don't know -- could be anything
 
                 String name = ((StringLiteral) meth).string;
                 if (MethodIndex.FRAME_AWARE_METHODS.contains(name)) {
                     return true;
                 }
             }
         }
 
         return false;
     }
 
     private void computeFlags() {
         // Order important!
         flagsComputed = true;
         canBeEval = computeEvalFlag();
         targetRequiresCallersBinding = canBeEval || computeRequiresCallersBindingFlag();
         targetRequiresCallersFrame = canBeEval || computeRequiresCallersFrameFlag();
     }
 
     public boolean canBeEval() {
         if (!flagsComputed) computeFlags();
 
         return canBeEval;
     }
 
     public boolean targetRequiresCallersBinding() {
         if (!flagsComputed) computeFlags();
 
         return targetRequiresCallersBinding;
     }
 
     public boolean targetRequiresCallersFrame() {
         if (!flagsComputed) computeFlags();
 
         return targetRequiresCallersFrame;
     }
 
     @Override
     public String toString() {
         return "" + getOperation()  + "(" + callType + ", " + getName() + ", " + receiver +
                 ", " + Arrays.toString(getCallArgs()) +
                 (closure == null ? "" : ", &" + closure) + ")";
     }
 
     public static boolean containsArgSplat(Operand[] arguments) {
         for (Operand argument : arguments) {
-            if (argument instanceof Splat && ((Splat)argument).unsplatArgs) return true;
+            if (argument instanceof Splat) return true;
         }
 
         return false;
     }
 
     private final static int REQUIRED_OPERANDS = 3;
     private static Operand[] buildAllArgs(Operand callType, Operand receiver,
             Fixnum argsCount, Operand[] callArgs, Operand closure) {
         Operand[] allArgs = new Operand[callArgs.length + REQUIRED_OPERANDS + (closure != null ? 1 : 0)];
 
         assert receiver != null : "RECEIVER is null";
 
 
         allArgs[0] = callType;
         allArgs[1] = receiver;
         // -0 not possible so if closure exists we are negative and we subtract one to get real arg count.
         allArgs[2] = argsCount;
         for (int i = 0; i < callArgs.length; i++) {
             assert callArgs[i] != null : "ARG " + i + " is null";
 
             allArgs[i + REQUIRED_OPERANDS] = callArgs[i];
         }
 
         if (closure != null) allArgs[callArgs.length + REQUIRED_OPERANDS] = closure;
 
         return allArgs;
     }
 
     @Override
     public Object interpret(ThreadContext context, StaticScope currScope, DynamicScope dynamicScope, IRubyObject self, Object[] temp) {
         IRubyObject object = (IRubyObject) receiver.retrieve(context, self, currScope, dynamicScope, temp);
         IRubyObject[] values = prepareArguments(context, self, arguments, currScope, dynamicScope, temp);
         Block preparedBlock = prepareBlock(context, self, currScope, dynamicScope, temp);
 
         return callSite.call(context, self, object, values, preparedBlock);
     }
 
     protected IRubyObject[] prepareArguments(ThreadContext context, IRubyObject self, Operand[] arguments, StaticScope currScope, DynamicScope dynamicScope, Object[] temp) {
         return containsArgSplat ?
                 prepareArgumentsComplex(context, self, arguments, currScope, dynamicScope, temp) :
                 prepareArgumentsSimple(context, self, arguments, currScope, dynamicScope, temp);
     }
 
     protected IRubyObject[] prepareArgumentsSimple(ThreadContext context, IRubyObject self, Operand[] args, StaticScope currScope, DynamicScope currDynScope, Object[] temp) {
         IRubyObject[] newArgs = new IRubyObject[args.length];
 
         for (int i = 0; i < args.length; i++) {
             newArgs[i] = (IRubyObject) args[i].retrieve(context, self, currScope, currDynScope, temp);
         }
 
         return newArgs;
     }
 
     protected IRubyObject[] prepareArgumentsComplex(ThreadContext context, IRubyObject self, Operand[] args, StaticScope currScope, DynamicScope currDynScope, Object[] temp) {
         // SSS: For regular calls, IR builder never introduces splats except as the first argument
         // But when zsuper is converted to SuperInstr with known args, splats can appear anywhere
         // in the list.  So, this looping handles both these scenarios, although if we wanted to
         // optimize for CallInstr which has splats only in the first position, we could do that.
         List<IRubyObject> argList = new ArrayList<IRubyObject>(args.length * 2);
         for (Operand arg : args) {
             IRubyObject rArg = (IRubyObject) arg.retrieve(context, self, currScope, currDynScope, temp);
             if (arg instanceof Splat) {
                 RubyArray array = (RubyArray) rArg;
                 for (int i = 0; i < array.size(); i++) {
                     argList.add(array.eltOk(i));
                 }
             } else {
                 argList.add(rArg);
             }
         }
 
         return argList.toArray(new IRubyObject[argList.size()]);
     }
 
     public Block prepareBlock(ThreadContext context, IRubyObject self, StaticScope currScope, DynamicScope currDynScope, Object[] temp) {
         if (closure == null) return Block.NULL_BLOCK;
 
         return IRRuntimeHelpers.getBlockFromObject(context, closure.retrieve(context, self, currScope, currDynScope, temp));
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/operands/Splat.java b/core/src/main/java/org/jruby/ir/operands/Splat.java
index 97d996f6ee..b73eb7151b 100644
--- a/core/src/main/java/org/jruby/ir/operands/Splat.java
+++ b/core/src/main/java/org/jruby/ir/operands/Splat.java
@@ -1,79 +1,85 @@
 package org.jruby.ir.operands;
 
 import org.jruby.ir.IRVisitor;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.ir.transformations.inlining.CloneInfo;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import java.util.List;
 import java.util.Map;
 
 // Represents a splat value in Ruby code: *array
 //
 // NOTE: This operand is only used in the initial stages of optimization
 // Further down the line, it could get converted to calls that implement splat semantics
 public class Splat extends Operand implements DepthCloneable {
     final private Operand array;
-    final public boolean unsplatArgs;
 
-    public Splat(Operand array, boolean unsplatArgs) {
+    public Splat(Operand array) {
         super(OperandType.SPLAT);
         this.array = array;
-        this.unsplatArgs = unsplatArgs;
-    }
-
-    public Splat(Operand array) {
-        this(array, false);
     }
 
     @Override
     public String toString() {
-        return (unsplatArgs ? "*(unsplat)" : "*") + array;
+        return "*(unsplat)" + array;
     }
 
     @Override
     public boolean hasKnownValue() {
         return false; /*_array.isConstant();*/
     }
 
     public Operand getArray() {
         return array;
     }
 
     @Override
     public Operand getSimplifiedOperand(Map<Operand, Operand> valueMap, boolean force) {
         Operand newArray = array.getSimplifiedOperand(valueMap, force);
-        return (newArray == array) ? this : new Splat(newArray, unsplatArgs);
+        return (newArray == array) ? this : new Splat(newArray);
     }
 
     /** Append the list of variables used in this operand to the input list */
     @Override
     public void addUsedVariables(List<Variable> l) {
         array.addUsedVariables(l);
     }
 
     /** When fixing up splats in nested closure we need to tweak the operand if it is a LocalVariable */
     public Operand cloneForDepth(int n) {
-        return array instanceof LocalVariable ? new Splat(((LocalVariable) array).cloneForDepth(n), unsplatArgs) : this;
+        return array instanceof LocalVariable ? new Splat(((LocalVariable) array).cloneForDepth(n)) : this;
     }
 
     @Override
     public Operand cloneForInlining(CloneInfo ii) {
-        return hasKnownValue() ? this : new Splat(array.cloneForInlining(ii), unsplatArgs);
+        return hasKnownValue() ? this : new Splat(array.cloneForInlining(ii));
     }
 
     @Override
     public Object retrieve(ThreadContext context, IRubyObject self, StaticScope currScope, DynamicScope currDynScope, Object[] temp) {
-        IRubyObject arrayVal = (IRubyObject) array.retrieve(context, self, currScope, currDynScope, temp);
-        // SSS FIXME: Some way to specialize this code?
-        return IRRuntimeHelpers.irSplat(context, arrayVal);
+        // Splat is now only used in call arg lists where it is guaranteed that
+        // the splat-arg is an array.
+        //
+        // It is:
+        // - either a result of a args-cat/args-push (which generate an array),
+        // - or a result of a BuildSplatInstr (which also generates an array),
+        // - or a rest-arg that has been received (which also generates an array)
+        //   and is being passed via zsuper.
+        //
+        // In addition, since this only shows up in call args, the array itself is
+        // never modified. The array elements are extracted out and inserted into
+        // a java array. So, a dup is not required either.
+        //
+        // So, besides retrieving the array, nothing more to be done here!
+        return (IRubyObject) array.retrieve(context, self, currScope, currDynScope, temp);
     }
 
     @Override
     public void visit(IRVisitor visitor) {
         visitor.Splat(this);
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/persistence/OperandDecoderMap.java b/core/src/main/java/org/jruby/ir/persistence/OperandDecoderMap.java
index 678d7680e4..7e28a25a12 100644
--- a/core/src/main/java/org/jruby/ir/persistence/OperandDecoderMap.java
+++ b/core/src/main/java/org/jruby/ir/persistence/OperandDecoderMap.java
@@ -1,133 +1,133 @@
 package org.jruby.ir.persistence;
 
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.ir.IRClosure;
 import org.jruby.ir.IRManager;
 import org.jruby.ir.operands.*;
 import org.jruby.util.KeyValuePair;
 import org.jruby.util.RegexpOptions;
 
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.List;
 
 import static org.jruby.ir.operands.UnexecutableNil.U_NIL;
 
 /**
  *
  */
 class OperandDecoderMap {
     private final IRReaderDecoder d;
     private final IRManager manager;
 
     public OperandDecoderMap(IRManager manager, IRReaderDecoder decoder) {
         this.manager = manager;
         this.d = decoder;
     }
 
     public Operand decode(OperandType type) {
         if (RubyInstanceConfig.IR_READING_DEBUG) System.out.println("Decoding operand " + type);
 
         switch (type) {
             case ARRAY: return new Array(d.decodeOperandList());
             case AS_STRING: return new AsString(d.decodeOperand());
             case BACKREF: return new Backref(d.decodeChar());
             case BIGNUM: return new Bignum(new BigInteger(d.decodeString()));
             case BOOLEAN: return new UnboxedBoolean(d.decodeBoolean());
             case CURRENT_SCOPE: return new CurrentScope(d.decodeInt());
             case DYNAMIC_SYMBOL: return new DynamicSymbol(d.decodeOperand());
             case FIXNUM: return new Fixnum(d.decodeLong());
             case FLOAT: return new org.jruby.ir.operands.Float(d.decodeDouble());
             case GLOBAL_VARIABLE: return new GlobalVariable(d.decodeString());
             case HASH: return decodeHash();
             case IR_EXCEPTION: return IRException.getExceptionFromOrdinal(d.decodeByte());
             case LABEL: return decodeLabel();
             case LOCAL_VARIABLE: return d.getCurrentScope().getLocalVariable(d.decodeString(), d.decodeInt());
             case NIL: return manager.getNil();
             case NTH_REF: return new NthRef(d.decodeInt());
             case OBJECT_CLASS: return new ObjectClass();
             case REGEXP: return decodeRegexp();
             case SCOPE_MODULE: return new ScopeModule(d.decodeInt());
             case SELF: return Self.SELF;
-            case SPLAT: return new Splat(d.decodeOperand(), d.decodeBoolean());
+            case SPLAT: return new Splat(d.decodeOperand());
             case STANDARD_ERROR: return new StandardError();
             case STRING_LITERAL: return new StringLiteral(d.decodeString());
             case SVALUE: return new SValue(d.decodeOperand());
             // FIXME: This is broken since there is no encode/decode for encoding
             case SYMBOL: return new Symbol(d.decodeString(), USASCIIEncoding.INSTANCE);
             case TEMPORARY_VARIABLE: return decodeTemporaryVariable();
             case UNBOXED_BOOLEAN: return new UnboxedBoolean(d.decodeBoolean());
             case UNBOXED_FIXNUM: return new UnboxedFixnum(d.decodeLong());
             case UNBOXED_FLOAT: return new UnboxedFloat(d.decodeDouble());
             case UNDEFINED_VALUE: return UndefinedValue.UNDEFINED;
             case UNEXECUTABLE_NIL: return U_NIL;
             case WRAPPED_IR_CLOSURE: return new WrappedIRClosure(d.decodeVariable(), (IRClosure) d.decodeScope());
         }
 
         return null;
     }
 
     private Operand decodeHash() {
         int size = d.decodeInt();
         List<KeyValuePair<Operand, Operand>> pairs = new ArrayList<KeyValuePair<Operand, Operand>>(size);
 
         for (int i = 0; i < size; i++) {
             pairs.add(new KeyValuePair(d.decodeOperand(), d.decodeOperand()));
         }
 
         return new Hash(pairs);
     }
 
     private Operand decodeLabel() {
         String prefix = d.decodeString();
         int id = d.decodeInt();
 
         // Special case of label
         if ("_GLOBAL_ENSURE_BLOCK".equals(prefix)) return new Label("_GLOBAL_ENSURE_BLOCK", 0);
 
         // Check if this label was already created
         // Important! Program would not be interpreted correctly
         // if new name will be created every time
         String fullLabel = prefix + "_" + id;
         if (d.getVars().containsKey(fullLabel)) {
             return d.getVars().get(fullLabel);
         }
 
         Label newLabel = new Label(prefix, id);
 
         // Add to context for future reuse
         d.getVars().put(fullLabel, newLabel);
 
         return newLabel;
     }
 
     private Regexp decodeRegexp() {
         Operand regex = d.decodeOperand();
         boolean isNone = d.decodeBoolean();
         RegexpOptions options = RegexpOptions.fromEmbeddedOptions(d.decodeInt());
         options.setEncodingNone(isNone);
         return new Regexp(regex, options);
     }
 
     private Operand decodeTemporaryVariable() {
         TemporaryVariableType type = d.decodeTemporaryVariableType();
 
         switch(type) {
             case CLOSURE:
                 return new TemporaryClosureVariable(d.decodeInt(), d.decodeInt());
             case CURRENT_MODULE:
                 return TemporaryCurrentModuleVariable.ModuleVariableFor(d.decodeInt());
             case CURRENT_SCOPE:
                 return TemporaryCurrentScopeVariable.ScopeVariableFor(d.decodeInt());
             case FLOAT:
                 return new TemporaryFloatVariable(d.decodeInt());
             case FIXNUM:
                 return new TemporaryFixnumVariable(d.decodeInt());
             case LOCAL:
                 return new TemporaryLocalVariable(d.decodeInt());
         }
 
         return null;
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/persistence/OperandEncoderMap.java b/core/src/main/java/org/jruby/ir/persistence/OperandEncoderMap.java
index 99f9ddb7f1..1eb56a7361 100644
--- a/core/src/main/java/org/jruby/ir/persistence/OperandEncoderMap.java
+++ b/core/src/main/java/org/jruby/ir/persistence/OperandEncoderMap.java
@@ -1,150 +1,150 @@
 package org.jruby.ir.persistence;
 
 import org.jruby.ir.IRVisitor;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.operands.Boolean;
 import org.jruby.ir.operands.Float;
 import org.jruby.util.KeyValuePair;
 
 /**
  * Can cycles develop or will IR output guarantee non-cyclical nested operands?
  */
 class OperandEncoderMap extends IRVisitor {
     private final IRWriterEncoder encoder;
 
     public OperandEncoderMap(IRWriterEncoder encoder) {
         this.encoder = encoder;
     }
     // FIXME: Potentially some of these values should not need to have their type prefixed.
     public void encode(Operand operand) {
         encoder.encode(operand.getOperandType().getCoded());
         operand.visit(this);
     }
 
     @Override public void Array(Array array) {
         Operand[] elts = array.getElts();
 
         encoder.encode(elts.length);
         for (Operand elt: elts) {
             encode(elt);
         }
     }
 
     @Override public void AsString(AsString asstring) { encoder.encode(asstring.getSource()); }
 
     @Override public void Backref(Backref backref) { encoder.encode(backref.type); }
 
     @Override public void Bignum(Bignum bignum) { encoder.encode(bignum.value.toString()); }
 
     @Override public void Boolean(Boolean booleanliteral) { encoder.encode(booleanliteral.isTrue()); }
 
     @Override public void ClosureLocalVariable(ClosureLocalVariable variable) {
         // We can refigure out closure scope it is in.
         encoder.encode(variable.getName());
         encoder.encode(variable.getScopeDepth());
     }
 
     @Override public void CurrentScope(CurrentScope scope) {
         encoder.encode(scope.getScopeNestingDepth());
     }
 
     //@Override public void DynamicSymbol(DynamicSymbol dsym) { encode(dsym.getSymbolName()); }
     @Override public void DynamicSymbol(DynamicSymbol dsym) {  }
 
     @Override public void Fixnum(Fixnum fixnum) { encoder.encode(fixnum.value); }
 
     @Override public void Float(Float flote) { encoder.encode(flote.value); }
 
     @Override public void GlobalVariable(GlobalVariable variable) { encoder.encode(variable.getName()); }
 
     @Override public void Hash(Hash hash) {
         encoder.encode(hash.pairs.size());
         for (KeyValuePair<Operand, Operand> pair: hash.pairs) {
             encoder.encode(pair.getKey());
             encoder.encode(pair.getValue());
         }
     }
 
     @Override public void IRException(IRException irexception) { encoder.encode((byte) irexception.getType().ordinal()); }
 
     @Override public void Label(Label label) {
         encoder.encode(label.prefix);
         encoder.encode(label.id);
     }
 
     @Override public void LocalVariable(LocalVariable variable) {
         encoder.encode(variable.getName());
         encoder.encode(variable.getScopeDepth());
     }
 
     @Override public void Nil(Nil nil) {} // No data
 
     @Override public void NthRef(NthRef nthref) { encoder.encode(nthref.matchNumber); }
 
     @Override public void ObjectClass(ObjectClass objectclass) {} // No data
 
     @Override public void Regexp(Regexp regexp) {
         encode(regexp.getRegexp());
         encoder.encode(regexp.options.isEncodingNone());
         encoder.encode(regexp.options.toEmbeddedOptions());
     }
 
     @Override public void ScopeModule(ScopeModule scope) { encoder.encode(scope.getScopeModuleDepth()); }
 
     @Override public void Self(Self self) {} // No data
 
-    @Override public void Splat(Splat splat) { encode(splat.getArray()); encoder.encode(splat.unsplatArgs); }
+    @Override public void Splat(Splat splat) { encode(splat.getArray()); }
 
     @Override public void StandardError(StandardError standarderror) {} // No data
 
     @Override public void StringLiteral(StringLiteral stringliteral) { encoder.encode(stringliteral.string); }
 
     @Override public void SValue(SValue svalue) { encode(svalue.getArray()); }
 
     @Override public void Symbol(Symbol symbol) { encoder.encode(symbol.getName()); }
 
     @Override public void TemporaryBooleanVariable(TemporaryBooleanVariable variable) {
         encoder.encode((byte) variable.getType().ordinal());
         encoder.encode(variable.getOffset());
     }
 
     @Override public void TemporaryFixnumVariable(TemporaryFixnumVariable variable) {
         encoder.encode((byte) variable.getType().ordinal());
         encoder.encode(variable.getOffset());
     }
 
     @Override public void TemporaryFloatVariable(TemporaryFloatVariable variable) {
         encoder.encode((byte) variable.getType().ordinal());
         encoder.encode(variable.getOffset());
     }
 
     @Override public void TemporaryLocalVariable(TemporaryLocalVariable variable) {
         encoder.encode((byte) variable.getType().ordinal());
 
         if (variable.getType() == TemporaryVariableType.CLOSURE) {
             encoder.encode(((TemporaryClosureVariable) variable).getClosureId());
         }
         encoder.encode(variable.getOffset());
     }
 
     // Only for CURRENT_SCOPE and CURRENT_MODULE now which is weird
     @Override public void TemporaryVariable(TemporaryVariable variable) {
         encoder.encode((byte) variable.getType().ordinal());
         encoder.encode(((TemporaryLocalVariable) variable).getOffset());
     }
 
     @Override public void UnboxedBoolean(org.jruby.ir.operands.UnboxedBoolean booleanliteral) { encoder.encode(booleanliteral.isTrue()); }
 
     @Override public void UnboxedFixnum(UnboxedFixnum fixnum) { encoder.encode(fixnum.value); }
 
     @Override public void UnboxedFloat(UnboxedFloat flote) { encoder.encode(flote.value); }
 
     @Override public void UndefinedValue(UndefinedValue undefinedvalue) {} // No data
 
     @Override public void UnexecutableNil(UnexecutableNil unexecutablenil) {} // No data
 
     @Override public void WrappedIRClosure(WrappedIRClosure scope) {
         encoder.encode(scope.getSelf());
         encoder.encode(scope.getClosure());
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
index f66e17632c..0a249bb727 100644
--- a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+++ b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
@@ -1,2223 +1,2244 @@
 package org.jruby.ir.targets;
 
 import com.headius.invokebinder.Signature;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.*;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.ir.*;
 import org.jruby.ir.instructions.*;
 import org.jruby.ir.instructions.boxing.*;
 import org.jruby.ir.instructions.defined.GetErrorInfoInstr;
 import org.jruby.ir.instructions.defined.RestoreErrorInfoInstr;
 import org.jruby.ir.instructions.specialized.OneFixnumArgNoBlockCallInstr;
 import org.jruby.ir.instructions.specialized.OneFloatArgNoBlockCallInstr;
 import org.jruby.ir.instructions.specialized.OneOperandArgNoBlockCallInstr;
 import org.jruby.ir.instructions.specialized.ZeroOperandArgNoBlockCallInstr;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.operands.Boolean;
 import org.jruby.ir.operands.Float;
 import org.jruby.ir.operands.GlobalVariable;
 import org.jruby.ir.operands.Label;
 import org.jruby.ir.representations.BasicBlock;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.invokedynamic.InvokeDynamicSupport;
 import org.jruby.util.ByteList;
 import org.jruby.util.ClassDefiningClassLoader;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.KeyValuePair;
 import org.jruby.util.RegexpOptions;
 import org.jruby.util.cli.Options;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 import org.objectweb.asm.Handle;
 import org.objectweb.asm.Opcodes;
 import org.objectweb.asm.Type;
 import org.objectweb.asm.commons.Method;
 
 import java.lang.invoke.MethodType;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import static org.jruby.util.CodegenUtils.*;
 
 /**
  * Implementation of IRCompiler for the JVM.
  */
 public class JVMVisitor extends IRVisitor {
 
     private static final Logger LOG = LoggerFactory.getLogger("JVMVisitor");
     public static final String DYNAMIC_SCOPE = "$dynamicScope";
     private static final boolean DEBUG = false;
 
     public JVMVisitor() {
         this.jvm = Options.COMPILE_INVOKEDYNAMIC.load() ? new JVM7() : new JVM6();
         this.methodIndex = 0;
         this.scopeMap = new HashMap();
     }
 
     public Class compile(IRScope scope, ClassDefiningClassLoader jrubyClassLoader) {
         return defineFromBytecode(scope, compileToBytecode(scope), jrubyClassLoader);
     }
 
     public byte[] compileToBytecode(IRScope scope) {
         codegenScope(scope);
 
 //        try {
 //            FileOutputStream fos = new FileOutputStream("tmp.class");
 //            fos.write(target.code());
 //            fos.close();
 //        } catch (Exception e) {
 //            e.printStackTrace();
 //        }
 
         return code();
     }
 
     public Class defineFromBytecode(IRScope scope, byte[] code, ClassDefiningClassLoader jrubyClassLoader) {
         Class result = jrubyClassLoader.defineClass(c(JVM.scriptToClass(scope.getFileName())), code);
 
         for (Map.Entry<String, IRScope> entry : scopeMap.entrySet()) {
             try {
                 result.getField(entry.getKey()).set(null, entry.getValue());
             } catch (Exception e) {
                 throw new NotCompilableException(e);
             }
         }
 
         return result;
     }
 
     public byte[] code() {
         return jvm.code();
     }
 
     public void codegenScope(IRScope scope) {
         if (scope instanceof IRScriptBody) {
             codegenScriptBody((IRScriptBody)scope);
         } else if (scope instanceof IRMethod) {
             emitMethodJIT((IRMethod)scope);
         } else if (scope instanceof IRModuleBody) {
             emitModuleBodyJIT((IRModuleBody)scope);
         } else {
             throw new NotCompilableException("don't know how to JIT: " + scope);
         }
     }
 
     public void codegenScriptBody(IRScriptBody script) {
         emitScriptBody(script);
     }
 
     private void logScope(IRScope scope) {
         StringBuilder b = new StringBuilder();
 
         b.append("\n\nLinearized instructions for JIT:\n");
 
         int i = 0;
         for (BasicBlock bb : scope.buildLinearization()) {
             for (Instr instr : bb.getInstrs()) {
                 if (i > 0) b.append("\n");
 
                 b.append("  ").append(i).append('\t').append(instr);
 
                 i++;
             }
         }
 
         LOG.info("Starting JVM compilation on scope " + scope);
         LOG.info(b.toString());
     }
 
     public void emitScope(IRScope scope, String name, Signature signature, boolean specificArity) {
         List <BasicBlock> bbs = scope.prepareForCompilation();
 
         Map <BasicBlock, Label> exceptionTable = scope.buildJVMExceptionTable();
 
         if (Options.IR_COMPILER_DEBUG.load()) logScope(scope);
 
         emitClosures(scope);
 
         jvm.pushmethod(name, scope, signature, specificArity);
 
         // store IRScope in map for insertion into class later
         String scopeField = name + "_IRScope";
         if (scopeMap.get(scopeField) == null) {
             scopeMap.put(scopeField, scope);
             jvm.cls().visitField(Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC | Opcodes.ACC_VOLATILE, scopeField, ci(IRScope.class), null, null).visitEnd();
         }
 
         // Some scopes (closures, module/class bodies) do not have explicit call protocol yet.
         // Unconditionally load current dynamic scope for those bodies.
         if (!scope.hasExplicitCallProtocol()) {
             jvmMethod().loadContext();
             jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("org.jruby.runtime.DynamicScope getCurrentScope()"));
             jvmStoreLocal(DYNAMIC_SCOPE);
         }
 
         IRBytecodeAdapter m = jvmMethod();
 
         int numberOfLabels = bbs.size();
         int ipc = 0; // synthetic, used for debug traces that show which instr failed
         for (int i = 0; i < numberOfLabels; i++) {
             BasicBlock bb = bbs.get(i);
             org.objectweb.asm.Label start = jvm.methodData().getLabel(bb.getLabel());
             Label rescueLabel = exceptionTable.get(bb);
             org.objectweb.asm.Label end = null;
 
             m.mark(start);
 
             boolean newEnd = false;
             if (rescueLabel != null) {
                 if (i+1 < numberOfLabels) {
                     end = jvm.methodData().getLabel(bbs.get(i+1).getLabel());
                 } else {
                     newEnd = true;
                     end = new org.objectweb.asm.Label();
                 }
 
                 org.objectweb.asm.Label rescue = jvm.methodData().getLabel(rescueLabel);
                 jvmAdapter().trycatch(start, end, rescue, p(Throwable.class));
             }
 
             // ensure there's at least one instr per block
             m.adapter.nop();
 
             // visit remaining instrs
             for (Instr instr : bb.getInstrs()) {
                 if (DEBUG) instr.setIPC(ipc++); // debug mode uses instr offset for backtrace
                 visit(instr);
             }
 
             if (newEnd) {
                 m.mark(end);
             }
         }
 
         jvm.popmethod();
     }
 
     private static final Signature METHOD_SIGNATURE_BASE = Signature
             .returning(IRubyObject.class)
             .appendArgs(new String[]{"context", "scope", "self", "block", "class"}, ThreadContext.class, StaticScope.class, IRubyObject.class, Block.class, RubyModule.class);
 
     public static final Signature signatureFor(IRScope method, boolean aritySplit) {
         if (aritySplit) {
             StaticScope argScope = method.getStaticScope();
             if (argScope.isArgumentScope() &&
                     argScope.getOptionalArgs() == 0 &&
                     argScope.getRestArg() == -1 &&
                     !method.receivesKeywordArgs()) {
                 // we have only required arguments...emit a signature appropriate to that arity
                 String[] args = new String[argScope.getRequiredArgs()];
                 Class[] types = Helpers.arrayOf(Class.class, args.length, IRubyObject.class);
                 for (int i = 0; i < args.length; i++) {
                     args[i] = "arg" + i;
                 }
                 return METHOD_SIGNATURE_BASE.insertArgs(3, args, types);
             }
             // we can't do an specific-arity signature
             return null;
         }
 
         // normal boxed arg list signature
         return METHOD_SIGNATURE_BASE.insertArgs(3, new String[]{"args"}, IRubyObject[].class);
     }
 
     private static final Signature CLOSURE_SIGNATURE = Signature
             .returning(IRubyObject.class)
             .appendArgs(new String[]{"context", "scope", "self", "args", "block", "superName", "type"}, ThreadContext.class, StaticScope.class, IRubyObject.class, IRubyObject[].class, Block.class, String.class, Block.Type.class);
 
     public void emitScriptBody(IRScriptBody script) {
         String clsName = jvm.scriptToClass(script.getFileName());
         jvm.pushscript(clsName, script.getFileName());
 
         emitScope(script, "__script__", signatureFor(script, false), false);
 
         jvm.cls().visitEnd();
         jvm.popclass();
     }
 
     public void emitMethod(IRMethod method) {
         String name = JavaNameMangler.mangleMethodName(method.getName() + "_" + methodIndex++);
 
         emitWithSignatures(method, name);
     }
 
     public void  emitMethodJIT(IRMethod method) {
         String clsName = jvm.scriptToClass(method.getFileName());
         jvm.pushscript(clsName, method.getFileName());
 
         emitWithSignatures(method, "__script__");
 
         jvm.cls().visitEnd();
         jvm.popclass();
     }
 
     private void emitWithSignatures(IRMethod method, String name) {
         method.setJittedName(name);
 
         Signature signature = signatureFor(method, false);
         emitScope(method, name, signature, false);
         method.addNativeSignature(-1, signature.type());
 
         Signature specificSig = signatureFor(method, true);
         if (specificSig != null) {
             emitScope(method, name, specificSig, true);
             method.addNativeSignature(method.getStaticScope().getRequiredArgs(), specificSig.type());
         }
     }
 
     public Handle emitModuleBodyJIT(IRModuleBody method) {
         String baseName = method.getName() + "_" + methodIndex++;
         String name;
 
         if (baseName.indexOf("DUMMY_MC") != -1) {
             name = "METACLASS_" + methodIndex++;
         } else {
             name = baseName + "_" + methodIndex++;
         }
         String clsName = jvm.scriptToClass(method.getFileName());
         jvm.pushscript(clsName, method.getFileName());
 
         Signature signature = signatureFor(method, false);
         emitScope(method, "__script__", signature, false);
 
         Handle handle = new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(signature.type().returnType(), signature.type().parameterArray()));
 
         jvm.cls().visitEnd();
         jvm.popclass();
 
         return handle;
     }
 
     private void emitClosures(IRScope s) {
         // Emit code for all nested closures
         for (IRClosure c: s.getClosures()) {
             c.setHandle(emitClosure(c));
         }
     }
 
     public Handle emitClosure(IRClosure closure) {
         /* Compile the closure like a method */
         String name = JavaNameMangler.mangleMethodName(closure.getName() + "__" + closure.getLexicalParent().getName() + "_" + methodIndex++);
 
         emitScope(closure, name, CLOSURE_SIGNATURE, false);
 
         return new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(CLOSURE_SIGNATURE.type().returnType(), CLOSURE_SIGNATURE.type().parameterArray()));
     }
 
     public Handle emitModuleBody(IRModuleBody method) {
         String baseName = method.getName() + "_" + methodIndex++;
         String name;
 
         if (baseName.indexOf("DUMMY_MC") != -1) {
             name = "METACLASS_" + methodIndex++;
         } else {
             name = baseName + "_" + methodIndex++;
         }
 
         Signature signature = signatureFor(method, false);
         emitScope(method, name, signature, false);
 
         return new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(signature.type().returnType(), signature.type().parameterArray()));
     }
 
     public void visit(Instr instr) {
         if (DEBUG) { // debug will skip emitting actual file line numbers
             jvmAdapter().line(instr.getIPC());
         }
         instr.visit(this);
     }
 
     public void visit(Operand operand) {
         operand.visit(this);
     }
 
     private int getJVMLocalVarIndex(Variable variable) {
         if (variable instanceof TemporaryLocalVariable) {
             switch (((TemporaryLocalVariable)variable).getType()) {
             case FLOAT: return jvm.methodData().local(variable, JVM.DOUBLE_TYPE);
             case FIXNUM: return jvm.methodData().local(variable, JVM.LONG_TYPE);
             case BOOLEAN: return jvm.methodData().local(variable, JVM.BOOLEAN_TYPE);
             default: return jvm.methodData().local(variable);
             }
         } else {
             return jvm.methodData().local(variable);
         }
     }
 
     private int getJVMLocalVarIndex(String specialVar) {
         return jvm.methodData().local(specialVar);
     }
 
     private org.objectweb.asm.Label getJVMLabel(Label label) {
         return jvm.methodData().getLabel(label);
     }
 
     private void jvmStoreLocal(Variable variable) {
         if (variable instanceof TemporaryLocalVariable) {
             switch (((TemporaryLocalVariable)variable).getType()) {
             case FLOAT: jvmAdapter().dstore(getJVMLocalVarIndex(variable)); break;
             case FIXNUM: jvmAdapter().lstore(getJVMLocalVarIndex(variable)); break;
             case BOOLEAN: jvmAdapter().istore(getJVMLocalVarIndex(variable)); break;
             default: jvmMethod().storeLocal(getJVMLocalVarIndex(variable)); break;
             }
         } else {
             jvmMethod().storeLocal(getJVMLocalVarIndex(variable));
         }
     }
 
     private void jvmStoreLocal(String specialVar) {
         jvmMethod().storeLocal(getJVMLocalVarIndex(specialVar));
     }
 
     private void jvmLoadLocal(Variable variable) {
         if (variable instanceof TemporaryLocalVariable) {
             switch (((TemporaryLocalVariable)variable).getType()) {
             case FLOAT: jvmAdapter().dload(getJVMLocalVarIndex(variable)); break;
             case FIXNUM: jvmAdapter().lload(getJVMLocalVarIndex(variable)); break;
             case BOOLEAN: jvmAdapter().iload(getJVMLocalVarIndex(variable)); break;
             default: jvmMethod().loadLocal(getJVMLocalVarIndex(variable)); break;
             }
         } else {
             jvmMethod().loadLocal(getJVMLocalVarIndex(variable));
         }
     }
 
     private void jvmLoadLocal(String specialVar) {
         jvmMethod().loadLocal(getJVMLocalVarIndex(specialVar));
     }
 
     // JVM maintains a stack of ClassData (for nested classes being compiled)
     // Each class maintains a stack of MethodData (for methods being compiled in the class)
     // MethodData wraps a IRBytecodeAdapter which wraps a SkinnyMethodAdapter which has a ASM MethodVisitor which emits bytecode
     // A long chain of indirection: JVM -> MethodData -> IRBytecodeAdapter -> SkinnyMethodAdapter -> ASM.MethodVisitor
     // In some places, methods reference JVM -> MethodData -> IRBytecodeAdapter (via jvm.method()) and ask it to walk the last 2 links
     // In other places, methods reference JVM -> MethodData -> IRBytecodeAdapter -> SkinnyMethodAdapter (via jvm.method().adapter) and ask it to walk the last link
     // Can this be cleaned up to either (a) get rid of IRBytecodeAdapter OR (b) implement passthru' methods for SkinnyMethodAdapter methods (like the others it implements)?
 
     // SSS FIXME: Needs an update to reflect instr. change
     @Override
     public void AliasInstr(AliasInstr aliasInstr) {
         IRBytecodeAdapter m = jvm.method();
         m.loadContext();
         m.loadSelf();
         jvmLoadLocal(DYNAMIC_SCOPE);
         // CON FIXME: Ideally this would not have to pass through RubyString and toString
         visit(aliasInstr.getNewName());
         jvmAdapter().invokevirtual(p(Object.class), "toString", sig(String.class));
         visit(aliasInstr.getOldName());
         jvmAdapter().invokevirtual(p(Object.class), "toString", sig(String.class));
         m.invokeIRHelper("defineAlias", sig(void.class, ThreadContext.class, IRubyObject.class, DynamicScope.class, String.class, String.class));
     }
 
     @Override
     public void AttrAssignInstr(AttrAssignInstr attrAssignInstr) {
         compileCallCommon(
                 jvmMethod(),
                 attrAssignInstr.getName(),
                 attrAssignInstr.getCallArgs(),
                 attrAssignInstr.getReceiver(),
                 attrAssignInstr.getCallArgs().length,
                 null,
                 false,
                 attrAssignInstr.getReceiver() instanceof Self ? CallType.FUNCTIONAL : CallType.NORMAL,
                 null);
     }
 
     @Override
     public void BEQInstr(BEQInstr beqInstr) {
         jvmMethod().loadContext();
         visit(beqInstr.getArg1());
         visit(beqInstr.getArg2());
         jvmMethod().invokeHelper("BEQ", boolean.class, ThreadContext.class, IRubyObject.class, IRubyObject.class);
         jvmAdapter().iftrue(getJVMLabel(beqInstr.getJumpTarget()));
     }
 
     @Override
     public void BFalseInstr(BFalseInstr bFalseInstr) {
         Operand arg1 = bFalseInstr.getArg1();
         visit(arg1);
         // this is a gross hack because we don't have distinction in boolean instrs between boxed and unboxed
         if (!(arg1 instanceof TemporaryBooleanVariable) && !(arg1 instanceof UnboxedBoolean)) {
             // unbox
             jvmAdapter().invokeinterface(p(IRubyObject.class), "isTrue", sig(boolean.class));
         }
         jvmMethod().bfalse(getJVMLabel(bFalseInstr.getJumpTarget()));
     }
 
     @Override
     public void BlockGivenInstr(BlockGivenInstr blockGivenInstr) {
         jvmMethod().loadContext();
         visit(blockGivenInstr.getBlockArg());
         jvmMethod().invokeIRHelper("isBlockGiven", sig(RubyBoolean.class, ThreadContext.class, Object.class));
         jvmStoreLocal(blockGivenInstr.getResult());
     }
 
     private void loadFloatArg(Operand arg) {
         if (arg instanceof Variable) {
             visit(arg);
         } else {
             double val;
             if (arg instanceof Float) {
                 val = ((Float)arg).value;
             } else if (arg instanceof Fixnum) {
                 val = (double)((Fixnum)arg).value;
             } else {
                 // Should not happen -- so, forcing an exception.
                 throw new NotCompilableException("Non-float/fixnum in loadFloatArg!" + arg);
             }
             jvmAdapter().ldc(val);
         }
     }
 
     private void loadFixnumArg(Operand arg) {
         if (arg instanceof Variable) {
             visit(arg);
         } else {
             long val;
             if (arg instanceof Float) {
                 val = (long)((Float)arg).value;
             } else if (arg instanceof Fixnum) {
                 val = ((Fixnum)arg).value;
             } else {
                 // Should not happen -- so, forcing an exception.
                 throw new NotCompilableException("Non-float/fixnum in loadFixnumArg!" + arg);
             }
             jvmAdapter().ldc(val);
         }
     }
 
     private void loadBooleanArg(Operand arg) {
         if (arg instanceof Variable) {
             visit(arg);
         } else {
             boolean val;
             if (arg instanceof UnboxedBoolean) {
                 val = ((UnboxedBoolean)arg).isTrue();
             } else {
                 // Should not happen -- so, forcing an exception.
                 throw new NotCompilableException("Non-float/fixnum in loadFixnumArg!" + arg);
             }
             jvmAdapter().ldc(val);
         }
     }
 
     @Override
     public void BoxFloatInstr(BoxFloatInstr instr) {
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         // Load runtime
         m.loadContext();
         a.getfield(p(ThreadContext.class), "runtime", ci(Ruby.class));
 
         // Get unboxed float
         loadFloatArg(instr.getValue());
 
         // Box the float
         a.invokevirtual(p(Ruby.class), "newFloat", sig(RubyFloat.class, double.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BoxFixnumInstr(BoxFixnumInstr instr) {
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         // Load runtime
         m.loadContext();
         a.getfield(p(ThreadContext.class), "runtime", ci(Ruby.class));
 
         // Get unboxed fixnum
         loadFixnumArg(instr.getValue());
 
         // Box the fixnum
         a.invokevirtual(p(Ruby.class), "newFixnum", sig(RubyFixnum.class, long.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BoxBooleanInstr(BoxBooleanInstr instr) {
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         // Load runtime
         m.loadContext();
         a.getfield(p(ThreadContext.class), "runtime", ci(Ruby.class));
 
         // Get unboxed boolean
         loadBooleanArg(instr.getValue());
 
         // Box the fixnum
         a.invokevirtual(p(Ruby.class), "newBoolean", sig(RubyBoolean.class, boolean.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void UnboxFloatInstr(UnboxFloatInstr instr) {
         // Load boxed value
         visit(instr.getValue());
 
         // Unbox it
         jvmMethod().invokeIRHelper("unboxFloat", sig(double.class, IRubyObject.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void UnboxFixnumInstr(UnboxFixnumInstr instr) {
         // Load boxed value
         visit(instr.getValue());
 
         // Unbox it
         jvmMethod().invokeIRHelper("unboxFixnum", sig(long.class, IRubyObject.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void UnboxBooleanInstr(UnboxBooleanInstr instr) {
         // Load boxed value
         visit(instr.getValue());
 
         // Unbox it
         jvmMethod().invokeIRHelper("unboxBoolean", sig(boolean.class, IRubyObject.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     public void AluInstr(AluInstr instr) {
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         // Load args
         visit(instr.getArg1());
         visit(instr.getArg2());
 
         // Compute result
         switch (instr.getOperation()) {
             case FADD: a.dadd(); break;
             case FSUB: a.dsub(); break;
             case FMUL: a.dmul(); break;
             case FDIV: a.ddiv(); break;
             case FLT: m.invokeIRHelper("flt", sig(boolean.class, double.class, double.class)); break; // annoying to have to do it in a method
             case FGT: m.invokeIRHelper("fgt", sig(boolean.class, double.class, double.class)); break; // annoying to have to do it in a method
             case FEQ: m.invokeIRHelper("feq", sig(boolean.class, double.class, double.class)); break; // annoying to have to do it in a method
             case IADD: a.ladd(); break;
             case ISUB: a.lsub(); break;
             case IMUL: a.lmul(); break;
             case IDIV: a.ldiv(); break;
             case ILT: m.invokeIRHelper("ilt", sig(boolean.class, long.class, long.class)); break; // annoying to have to do it in a method
             case IGT: m.invokeIRHelper("igt", sig(boolean.class, long.class, long.class)); break; // annoying to have to do it in a method
             case IOR: a.lor(); break;
             case IAND: a.land(); break;
             case IXOR: a.lxor(); break;
             case ISHL: a.lshl(); break;
             case ISHR: a.lshr(); break;
             case IEQ: m.invokeIRHelper("ilt", sig(boolean.class, long.class, long.class)); break; // annoying to have to do it in a method
             default: throw new NotCompilableException("UNHANDLED!");
         }
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BacktickInstr(BacktickInstr instr) {
         // prepare for call to "`" below
         jvmMethod().loadContext();
         jvmMethod().loadSelf(); // TODO: remove caller
         jvmMethod().loadSelf();
 
         ByteList csByteList = new ByteList();
         jvmMethod().pushString(csByteList);
 
         for (Operand p : instr.getPieces()) {
             // visit piece and ensure it's a string
             visit(p);
             jvmAdapter().dup();
             org.objectweb.asm.Label after = new org.objectweb.asm.Label();
             jvmAdapter().instance_of(p(RubyString.class));
             jvmAdapter().iftrue(after);
             jvmAdapter().invokevirtual(p(IRubyObject.class), "anyToString", sig(IRubyObject.class));
 
             jvmAdapter().label(after);
             jvmAdapter().invokevirtual(p(RubyString.class), "append", sig(RubyString.class, IRubyObject.class));
         }
 
         // freeze the string
         jvmAdapter().dup();
         jvmAdapter().ldc(true);
         jvmAdapter().invokeinterface(p(IRubyObject.class), "setFrozen", sig(void.class, boolean.class));
 
         // invoke the "`" method on self
         jvmMethod().invokeSelf("`", 1, false);
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BNEInstr(BNEInstr bneinstr) {
         jvmMethod().loadContext();
         visit(bneinstr.getArg1());
         visit(bneinstr.getArg2());
         jvmMethod().invokeHelper("BNE", boolean.class, ThreadContext.class, IRubyObject.class, IRubyObject.class);
         jvmAdapter().iftrue(getJVMLabel(bneinstr.getJumpTarget()));
     }
 
     @Override
     public void BNilInstr(BNilInstr bnilinstr) {
         visit(bnilinstr.getArg1());
         jvmMethod().isNil();
         jvmMethod().btrue(getJVMLabel(bnilinstr.getJumpTarget()));
     }
 
     @Override
     public void BreakInstr(BreakInstr breakInstr) {
         jvmMethod().loadContext();
         jvmLoadLocal(DYNAMIC_SCOPE);
         visit(breakInstr.getReturnValue());
         jvmMethod().loadBlockType();
         jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "initiateBreak", sig(IRubyObject.class, ThreadContext.class, DynamicScope.class, IRubyObject.class, Block.Type.class));
         jvmMethod().returnValue();
 
     }
 
     @Override
     public void BTrueInstr(BTrueInstr btrueinstr) {
         Operand arg1 = btrueinstr.getArg1();
         visit(arg1);
         // this is a gross hack because we don't have distinction in boolean instrs between boxed and unboxed
         if (!(arg1 instanceof TemporaryBooleanVariable) && !(arg1 instanceof UnboxedBoolean)) {
             jvmMethod().isTrue();
         }
         jvmMethod().btrue(getJVMLabel(btrueinstr.getJumpTarget()));
     }
 
     @Override
     public void BUndefInstr(BUndefInstr bundefinstr) {
         visit(bundefinstr.getArg1());
         jvmMethod().pushUndefined();
         jvmAdapter().if_acmpeq(getJVMLabel(bundefinstr.getJumpTarget()));
     }
 
     @Override
     public void BuildCompoundArrayInstr(BuildCompoundArrayInstr instr) {
         visit(instr.getAppendingArg());
         if (instr.isArgsPush()) jvmAdapter().checkcast("org/jruby/RubyArray");
         visit(instr.getAppendedArg());
         if (instr.isArgsPush()) {
             jvmMethod().invokeHelper("argsPush", RubyArray.class, RubyArray.class, IRubyObject.class);
         } else {
             jvmMethod().invokeHelper("argsCat", RubyArray.class, IRubyObject.class, IRubyObject.class);
         }
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BuildCompoundStringInstr(BuildCompoundStringInstr compoundstring) {
         ByteList csByteList = new ByteList();
         csByteList.setEncoding(compoundstring.getEncoding());
         jvmMethod().pushString(csByteList);
         for (Operand p : compoundstring.getPieces()) {
 //            if ((p instanceof StringLiteral) && (compoundstring.isSameEncodingAndCodeRange((StringLiteral)p))) {
 //                jvmMethod().pushByteList(((StringLiteral)p).bytelist);
 //                jvmAdapter().invokevirtual(p(RubyString.class), "cat", sig(RubyString.class, ByteList.class));
 //            } else {
                 visit(p);
                 jvmAdapter().invokevirtual(p(RubyString.class), "append19", sig(RubyString.class, IRubyObject.class));
 //            }
         }
         jvmStoreLocal(compoundstring.getResult());
     }
 
     @Override
     public void BuildDynRegExpInstr(BuildDynRegExpInstr instr) {
         final IRBytecodeAdapter m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         RegexpOptions options = instr.getOptions();
         final List<Operand> operands = instr.getPieces();
 
         Runnable r = new Runnable() {
             @Override
             public void run() {
                 m.loadContext();
                 for (int i = 0; i < operands.size(); i++) {
                     Operand operand = operands.get(i);
                     visit(operand);
                 }
             }
         };
 
         m.pushDRegexp(r, options, operands.size());
 
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BuildRangeInstr(BuildRangeInstr instr) {
         jvmMethod().loadContext();
         visit(instr.getBegin());
         visit(instr.getEnd());
         jvmAdapter().ldc(instr.isExclusive());
         jvmAdapter().invokestatic(p(RubyRange.class), "newRange", sig(RubyRange.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
+    public void BuildSplatInstr(BuildSplatInstr instr) {
+        jvmMethod().loadContext();
+        visit(instr.getArray());
+        jvmMethod().invokeIRHelper("irSplat", sig(RubyArray.class, ThreadContext.class, IRubyObject.class));
+        jvmStoreLocal(instr.getResult());
+    }
+
+    @Override
     public void CallInstr(CallInstr callInstr) {
         IRBytecodeAdapter m = jvmMethod();
         String name = callInstr.getName();
         Operand[] args = callInstr.getCallArgs();
         Operand receiver = callInstr.getReceiver();
         int numArgs = args.length;
         Operand closure = callInstr.getClosureArg(null);
         boolean hasClosure = closure != null;
         CallType callType = callInstr.getCallType();
         Variable result = callInstr.getResult();
 
         compileCallCommon(m, name, args, receiver, numArgs, closure, hasClosure, callType, result);
     }
 
     private void compileCallCommon(IRBytecodeAdapter m, String name, Operand[] args, Operand receiver, int numArgs, Operand closure, boolean hasClosure, CallType callType, Variable result) {
         m.loadContext();
         m.loadSelf(); // caller
         visit(receiver);
         int arity = numArgs;
 
         if (numArgs == 1 && args[0] instanceof Splat) {
             visit(args[0]);
             m.adapter.invokevirtual(p(RubyArray.class), "toJavaArray", sig(IRubyObject[].class));
             arity = -1;
         } else if (CallBase.containsArgSplat(args)) {
             throw new NotCompilableException("splat in non-initial argument for normal call is unsupported in JIT");
         } else {
             for (Operand operand : args) {
                 visit(operand);
             }
         }
 
         if (hasClosure) {
             m.loadContext();
             visit(closure);
             m.invokeIRHelper("getBlockFromObject", sig(Block.class, ThreadContext.class, Object.class));
         }
 
         switch (callType) {
             case FUNCTIONAL:
             case VARIABLE:
                 m.invokeSelf(name, arity, hasClosure);
                 break;
             case NORMAL:
                 m.invokeOther(name, arity, hasClosure);
                 break;
         }
 
         if (result != null) {
             jvmStoreLocal(result);
         } else {
             // still need to drop, since all dyncalls return something (FIXME)
             m.adapter.pop();
         }
     }
 
     @Override
     public void CheckArgsArrayArityInstr(CheckArgsArrayArityInstr checkargsarrayarityinstr) {
         jvmMethod().loadContext();
         visit(checkargsarrayarityinstr.getArgsArray());
         jvmAdapter().pushInt(checkargsarrayarityinstr.required);
         jvmAdapter().pushInt(checkargsarrayarityinstr.opt);
         jvmAdapter().pushInt(checkargsarrayarityinstr.rest);
         jvmMethod().invokeStatic(Type.getType(Helpers.class), Method.getMethod("void irCheckArgsArrayArity(org.jruby.runtime.ThreadContext, org.jruby.RubyArray, int, int, int)"));
     }
 
     @Override
     public void CheckArityInstr(CheckArityInstr checkarityinstr) {
         if (jvm.methodData().specificArity >= 0) {
             // no arity check in specific arity path
         } else {
             jvmMethod().loadContext();
             jvmMethod().loadArgs();
             jvmAdapter().ldc(checkarityinstr.required);
             jvmAdapter().ldc(checkarityinstr.opt);
             jvmAdapter().ldc(checkarityinstr.rest);
             jvmAdapter().ldc(checkarityinstr.receivesKeywords);
             jvmAdapter().ldc(checkarityinstr.restKey);
             jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "checkArity", sig(void.class, ThreadContext.class, Object[].class, int.class, int.class, int.class, boolean.class, int.class));
         }
     }
 
     @Override
     public void ClassSuperInstr(ClassSuperInstr classsuperinstr) {
         String name = classsuperinstr.getName();
         Operand[] args = classsuperinstr.getCallArgs();
         Operand definingModule = classsuperinstr.getDefiningModule();
         boolean containsArgSplat = classsuperinstr.containsArgSplat();
         Operand closure = classsuperinstr.getClosureArg(null);
 
         superCommon(name, classsuperinstr, args, definingModule, containsArgSplat, closure);
     }
 
     @Override
     public void ConstMissingInstr(ConstMissingInstr constmissinginstr) {
         visit(constmissinginstr.getReceiver());
         jvmAdapter().checkcast("org/jruby/RubyModule");
         jvmMethod().loadContext();
         jvmAdapter().ldc("const_missing");
         // FIXME: This has lost it's encoding info by this point
         jvmMethod().pushSymbol(constmissinginstr.getMissingConst(), USASCIIEncoding.INSTANCE);
         jvmMethod().invokeVirtual(Type.getType(RubyModule.class), Method.getMethod("org.jruby.runtime.builtin.IRubyObject callMethod(org.jruby.runtime.ThreadContext, java.lang.String, org.jruby.runtime.builtin.IRubyObject)"));
         jvmStoreLocal(constmissinginstr.getResult());
     }
 
     @Override
     public void CopyInstr(CopyInstr copyinstr) {
         Operand  src = copyinstr.getSource();
         Variable res = copyinstr.getResult();
         if (res instanceof TemporaryFloatVariable) {
             loadFloatArg(src);
         } else if (res instanceof TemporaryFixnumVariable) {
             loadFixnumArg(src);
         } else {
             visit(src);
         }
         jvmStoreLocal(res);
     }
 
     @Override
     public void DefineClassInstr(DefineClassInstr defineclassinstr) {
         IRClassBody newIRClassBody = defineclassinstr.getNewIRClassBody();
 
         jvmMethod().loadContext();
         Handle handle = emitModuleBody(newIRClassBody);
         jvmMethod().pushHandle(handle);
         jvmAdapter().getstatic(jvm.clsData().clsName, handle.getName() + "_IRScope", ci(IRScope.class));
         visit(defineclassinstr.getContainer());
         visit(defineclassinstr.getSuperClass());
 
         jvmMethod().invokeIRHelper("newCompiledClassBody", sig(DynamicMethod.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, Object.class, Object.class));
 
         jvmStoreLocal(defineclassinstr.getResult());
     }
 
     @Override
     public void DefineClassMethodInstr(DefineClassMethodInstr defineclassmethodinstr) {
         IRMethod method = defineclassmethodinstr.getMethod();
 
         jvmMethod().loadContext();
 
         emitMethod(method);
 
         Map<Integer, MethodType> signatures = method.getNativeSignatures();
 
         MethodType signature = signatures.get(-1);
 
         String defSignature = pushHandlesForDef(
                 method.getJittedName(),
                 signatures,
                 signature,
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, IRubyObject.class),
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, java.lang.invoke.MethodHandle.class, int.class, IRScope.class, IRubyObject.class));
 
         jvmAdapter().getstatic(jvm.clsData().clsName, method.getJittedName() + "_IRScope", ci(IRScope.class));
         visit(defineclassmethodinstr.getContainer());
 
         // add method
         jvmMethod().adapter.invokestatic(p(IRRuntimeHelpers.class), "defCompiledClassMethod", defSignature);
     }
 
     // SSS FIXME: Needs an update to reflect instr. change
     @Override
     public void DefineInstanceMethodInstr(DefineInstanceMethodInstr defineinstancemethodinstr) {
         IRMethod method = defineinstancemethodinstr.getMethod();
 
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         m.loadContext();
 
         emitMethod(method);
         Map<Integer, MethodType> signatures = method.getNativeSignatures();
 
         MethodType variable = signatures.get(-1); // always a variable arity handle
 
         String defSignature = pushHandlesForDef(
                 method.getJittedName(),
                 signatures,
                 variable,
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, DynamicScope.class, IRubyObject.class),
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, java.lang.invoke.MethodHandle.class, int.class, IRScope.class, DynamicScope.class, IRubyObject.class));
 
         a.getstatic(jvm.clsData().clsName, method.getJittedName() + "_IRScope", ci(IRScope.class));
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmMethod().loadSelf();
 
         // add method
         a.invokestatic(p(IRRuntimeHelpers.class), "defCompiledInstanceMethod", defSignature);
     }
 
     public String pushHandlesForDef(String name, Map<Integer, MethodType> signatures, MethodType variable, String variableOnly, String variableAndSpecific) {
         String defSignature;
 
         jvmMethod().pushHandle(new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(variable.returnType(), variable.parameterArray())));
 
         if (signatures.size() == 1) {
             defSignature = variableOnly;
         } else {
             defSignature = variableAndSpecific;
 
             // FIXME: only supports one arity
             for (Map.Entry<Integer, MethodType> entry : signatures.entrySet()) {
                 if (entry.getKey() == -1) continue; // variable arity signature pushed above
                 jvmMethod().pushHandle(new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(entry.getValue().returnType(), entry.getValue().parameterArray())));
                 jvmAdapter().pushInt(entry.getKey());
                 break;
             }
         }
         return defSignature;
     }
 
     @Override
     public void DefineMetaClassInstr(DefineMetaClassInstr definemetaclassinstr) {
         IRModuleBody metaClassBody = definemetaclassinstr.getMetaClassBody();
 
         jvmMethod().loadContext();
         Handle handle = emitModuleBody(metaClassBody);
         jvmMethod().pushHandle(handle);
         jvmAdapter().getstatic(jvm.clsData().clsName, handle.getName() + "_IRScope", ci(IRScope.class));
         visit(definemetaclassinstr.getObject());
 
         jvmMethod().invokeIRHelper("newCompiledMetaClass", sig(DynamicMethod.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, IRubyObject.class));
 
         jvmStoreLocal(definemetaclassinstr.getResult());
     }
 
     @Override
     public void DefineModuleInstr(DefineModuleInstr definemoduleinstr) {
         IRModuleBody newIRModuleBody = definemoduleinstr.getNewIRModuleBody();
 
         jvmMethod().loadContext();
         Handle handle = emitModuleBody(newIRModuleBody);
         jvmMethod().pushHandle(handle);
         jvmAdapter().getstatic(jvm.clsData().clsName, handle.getName() + "_IRScope", ci(IRScope.class));
         visit(definemoduleinstr.getContainer());
 
         jvmMethod().invokeIRHelper("newCompiledModuleBody", sig(DynamicMethod.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, Object.class));
 
         jvmStoreLocal(definemoduleinstr.getResult());
     }
 
     @Override
     public void EQQInstr(EQQInstr eqqinstr) {
         jvmMethod().loadContext();
         visit(eqqinstr.getArg1());
         visit(eqqinstr.getArg2());
         jvmMethod().invokeIRHelper("isEQQ", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class));
         jvmStoreLocal(eqqinstr.getResult());
     }
 
     @Override
     public void ExceptionRegionEndMarkerInstr(ExceptionRegionEndMarkerInstr exceptionregionendmarkerinstr) {
         throw new NotCompilableException("Marker instructions shouldn't reach compiler: " + exceptionregionendmarkerinstr);
     }
 
     @Override
     public void ExceptionRegionStartMarkerInstr(ExceptionRegionStartMarkerInstr exceptionregionstartmarkerinstr) {
         throw new NotCompilableException("Marker instructions shouldn't reach compiler: " + exceptionregionstartmarkerinstr);
     }
 
     @Override
     public void GetClassVarContainerModuleInstr(GetClassVarContainerModuleInstr getclassvarcontainermoduleinstr) {
         jvmMethod().loadContext();
         visit(getclassvarcontainermoduleinstr.getStartingScope());
         if (getclassvarcontainermoduleinstr.getObject() != null) {
             visit(getclassvarcontainermoduleinstr.getObject());
         } else {
             jvmAdapter().aconst_null();
         }
         jvmMethod().invokeIRHelper("getModuleFromScope", sig(RubyModule.class, ThreadContext.class, StaticScope.class, IRubyObject.class));
         jvmStoreLocal(getclassvarcontainermoduleinstr.getResult());
     }
 
     @Override
     public void GetClassVariableInstr(GetClassVariableInstr getclassvariableinstr) {
         visit(getclassvariableinstr.getSource());
         jvmAdapter().checkcast(p(RubyModule.class));
         jvmAdapter().ldc(getclassvariableinstr.getRef());
         jvmAdapter().invokevirtual(p(RubyModule.class), "getClassVar", sig(IRubyObject.class, String.class));
         jvmStoreLocal(getclassvariableinstr.getResult());
     }
 
     @Override
     public void GetFieldInstr(GetFieldInstr getfieldinstr) {
         visit(getfieldinstr.getSource());
         jvmMethod().getField(getfieldinstr.getRef());
         jvmStoreLocal(getfieldinstr.getResult());
     }
 
     @Override
     public void GetGlobalVariableInstr(GetGlobalVariableInstr getglobalvariableinstr) {
         Operand source = getglobalvariableinstr.getSource();
         GlobalVariable gvar = (GlobalVariable)source;
         String name = gvar.getName();
         jvmMethod().loadRuntime();
         jvmMethod().invokeVirtual(Type.getType(Ruby.class), Method.getMethod("org.jruby.internal.runtime.GlobalVariables getGlobalVariables()"));
         jvmAdapter().ldc(name);
         jvmMethod().invokeVirtual(Type.getType(GlobalVariables.class), Method.getMethod("org.jruby.runtime.builtin.IRubyObject get(String)"));
         jvmStoreLocal(getglobalvariableinstr.getResult());
     }
 
     @Override
     public void GVarAliasInstr(GVarAliasInstr gvaraliasinstr) {
         jvmMethod().loadRuntime();
         jvmAdapter().invokevirtual(p(Ruby.class), "getGlobalVariables", sig(GlobalVariables.class));
         visit(gvaraliasinstr.getNewName());
         jvmAdapter().invokevirtual(p(Object.class), "toString", sig(String.class));
         visit(gvaraliasinstr.getOldName());
         jvmAdapter().invokevirtual(p(Object.class), "toString", sig(String.class));
         jvmAdapter().invokevirtual(p(GlobalVariables.class), "alias", sig(void.class, String.class, String.class));
     }
 
     @Override
     public void InheritanceSearchConstInstr(InheritanceSearchConstInstr inheritancesearchconstinstr) {
         jvmMethod().loadContext();
         visit(inheritancesearchconstinstr.getCurrentModule());
 
         jvmMethod().inheritanceSearchConst(inheritancesearchconstinstr.getConstName(), inheritancesearchconstinstr.isNoPrivateConsts());
         jvmStoreLocal(inheritancesearchconstinstr.getResult());
     }
 
     @Override
     public void InstanceSuperInstr(InstanceSuperInstr instancesuperinstr) {
         String name = instancesuperinstr.getName();
         Operand[] args = instancesuperinstr.getCallArgs();
         Operand definingModule = instancesuperinstr.getDefiningModule();
         boolean containsArgSplat = instancesuperinstr.containsArgSplat();
         Operand closure = instancesuperinstr.getClosureArg(null);
 
         superCommon(name, instancesuperinstr, args, definingModule, containsArgSplat, closure);
     }
 
     private void superCommon(String name, CallInstr instr, Operand[] args, Operand definingModule, boolean containsArgSplat, Operand closure) {
         IRBytecodeAdapter m = jvmMethod();
         Operation operation = instr.getOperation();
 
         m.loadContext();
         m.loadSelf(); // TODO: get rid of caller
         m.loadSelf();
         if (definingModule == UndefinedValue.UNDEFINED) {
             jvmAdapter().aconst_null();
         } else {
             visit(definingModule);
         }
 
         // TODO: CON: is this safe?
         jvmAdapter().checkcast(p(RubyClass.class));
 
         // process args
         for (int i = 0; i < args.length; i++) {
             Operand operand = args[i];
             visit(operand);
         }
 
         // if there's splats, provide a map and let the call site sort it out
         boolean[] splatMap = IRRuntimeHelpers.buildSplatMap(args, containsArgSplat);
 
         boolean hasClosure = closure != null;
         if (hasClosure) {
             m.loadContext();
             visit(closure);
             m.invokeIRHelper("getBlockFromObject", sig(Block.class, ThreadContext.class, Object.class));
         }
 
         switch (operation) {
             case INSTANCE_SUPER:
                 m.invokeInstanceSuper(name, args.length, hasClosure, splatMap);
                 break;
             case CLASS_SUPER:
                 m.invokeClassSuper(name, args.length, hasClosure, splatMap);
                 break;
             case UNRESOLVED_SUPER:
                 m.invokeUnresolvedSuper(name, args.length, hasClosure, splatMap);
                 break;
             case ZSUPER:
                 m.invokeZSuper(name, args.length, hasClosure, splatMap);
                 break;
             default:
                 throw new NotCompilableException("unknown super type " + operation + " in " + instr);
         }
 
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void JumpInstr(JumpInstr jumpinstr) {
         jvmMethod().goTo(getJVMLabel(jumpinstr.getJumpTarget()));
     }
 
     @Override
     public void LabelInstr(LabelInstr labelinstr) {
     }
 
     @Override
     public void LexicalSearchConstInstr(LexicalSearchConstInstr lexicalsearchconstinstr) {
         jvmMethod().loadContext();
         visit(lexicalsearchconstinstr.getDefiningScope());
 
         jvmMethod().lexicalSearchConst(lexicalsearchconstinstr.getConstName());
 
         jvmStoreLocal(lexicalsearchconstinstr.getResult());
     }
 
     @Override
     public void LineNumberInstr(LineNumberInstr linenumberinstr) {
         if (DEBUG) return; // debug mode uses IPC for line numbers
 
         jvmAdapter().line(linenumberinstr.getLineNumber() + 1);
     }
 
     @Override
     public void LoadLocalVarInstr(LoadLocalVarInstr loadlocalvarinstr) {
         IRBytecodeAdapter m = jvmMethod();
         jvmLoadLocal(DYNAMIC_SCOPE);
         int depth = loadlocalvarinstr.getLocalVar().getScopeDepth();
         int location = loadlocalvarinstr.getLocalVar().getLocation();
         // TODO if we can avoid loading nil unnecessarily, it could be a big win
         OUTER: switch (depth) {
             case 0:
                 switch (location) {
                     case 0:
                         m.pushNil();
                         m.adapter.invokevirtual(p(DynamicScope.class), "getValueZeroDepthZeroOrNil", sig(IRubyObject.class, IRubyObject.class));
                         break OUTER;
                     case 1:
                         m.pushNil();
                         m.adapter.invokevirtual(p(DynamicScope.class), "getValueOneDepthZeroOrNil", sig(IRubyObject.class, IRubyObject.class));
                         break OUTER;
                     case 2:
                         m.pushNil();
                         m.adapter.invokevirtual(p(DynamicScope.class), "getValueTwoDepthZeroOrNil", sig(IRubyObject.class, IRubyObject.class));
                         break OUTER;
                     case 3:
                         m.pushNil();
                         m.adapter.invokevirtual(p(DynamicScope.class), "getValueThreeDepthZeroOrNil", sig(IRubyObject.class, IRubyObject.class));
                         break OUTER;
                     default:
                         m.adapter.pushInt(location);
                         m.pushNil();
                         m.adapter.invokevirtual(p(DynamicScope.class), "getValueDepthZeroOrNil", sig(IRubyObject.class, int.class, IRubyObject.class));
                         break OUTER;
                 }
             default:
                 m.adapter.pushInt(location);
                 m.adapter.pushInt(depth);
                 m.pushNil();
                 m.adapter.invokevirtual(p(DynamicScope.class), "getValueOrNil", sig(IRubyObject.class, int.class, int.class, IRubyObject.class));
         }
         jvmStoreLocal(loadlocalvarinstr.getResult());
     }
 
     @Override
     public void Match2Instr(Match2Instr match2instr) {
         visit(match2instr.getReceiver());
         jvmMethod().loadContext();
         visit(match2instr.getArg());
         jvmAdapter().invokevirtual(p(RubyRegexp.class), "op_match19", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class));
         jvmStoreLocal(match2instr.getResult());
     }
 
     @Override
     public void Match3Instr(Match3Instr match3instr) {
         jvmMethod().loadContext();
         visit(match3instr.getReceiver());
         visit(match3instr.getArg());
         jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "match3", sig(IRubyObject.class, ThreadContext.class, RubyRegexp.class, IRubyObject.class));
         jvmStoreLocal(match3instr.getResult());
     }
 
     @Override
     public void MatchInstr(MatchInstr matchinstr) {
         visit(matchinstr.getReceiver());
         jvmMethod().loadContext();
         jvmAdapter().invokevirtual(p(RubyRegexp.class), "op_match2_19", sig(IRubyObject.class, ThreadContext.class));
         jvmStoreLocal(matchinstr.getResult());
     }
 
     @Override
     public void ModuleVersionGuardInstr(ModuleVersionGuardInstr moduleversionguardinstr) {
         // SSS FIXME: Unused at this time
         throw new NotCompilableException("Unsupported instruction: " + moduleversionguardinstr);
     }
 
     @Override
     public void NopInstr(NopInstr nopinstr) {
         // do nothing
     }
 
     @Override
     public void NoResultCallInstr(NoResultCallInstr noResultCallInstr) {
         IRBytecodeAdapter m = jvmMethod();
         String name = noResultCallInstr.getName();
         Operand[] args = noResultCallInstr.getCallArgs();
         Operand receiver = noResultCallInstr.getReceiver();
         int numArgs = args.length;
         Operand closure = noResultCallInstr.getClosureArg(null);
         boolean hasClosure = closure != null;
         CallType callType = noResultCallInstr.getCallType();
 
         compileCallCommon(m, name, args, receiver, numArgs, closure, hasClosure, callType, null);
     }
 
     @Override
     public void OneFixnumArgNoBlockCallInstr(OneFixnumArgNoBlockCallInstr oneFixnumArgNoBlockCallInstr) {
         if (MethodIndex.getFastFixnumOpsMethod(oneFixnumArgNoBlockCallInstr.getName()) == null) {
             CallInstr(oneFixnumArgNoBlockCallInstr);
             return;
         }
         IRBytecodeAdapter m = jvmMethod();
         String name = oneFixnumArgNoBlockCallInstr.getName();
         long fixnum = oneFixnumArgNoBlockCallInstr.getFixnumArg();
         Operand receiver = oneFixnumArgNoBlockCallInstr.getReceiver();
         Variable result = oneFixnumArgNoBlockCallInstr.getResult();
 
         m.loadContext();
 
         // for visibility checking without requiring frame self
         // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
         m.loadSelf(); // caller
 
         visit(receiver);
 
         m.invokeOtherOneFixnum(name, fixnum);
 
         if (result != null) {
             jvmStoreLocal(result);
         } else {
             // still need to drop, since all dyncalls return something (FIXME)
             m.adapter.pop();
         }
     }
 
     @Override
     public void OneFloatArgNoBlockCallInstr(OneFloatArgNoBlockCallInstr oneFloatArgNoBlockCallInstr) {
         if (MethodIndex.getFastFloatOpsMethod(oneFloatArgNoBlockCallInstr.getName()) == null) {
             CallInstr(oneFloatArgNoBlockCallInstr);
             return;
         }
         IRBytecodeAdapter m = jvmMethod();
         String name = oneFloatArgNoBlockCallInstr.getName();
         double flote = oneFloatArgNoBlockCallInstr.getFloatArg();
         Operand receiver = oneFloatArgNoBlockCallInstr.getReceiver();
         Variable result = oneFloatArgNoBlockCallInstr.getResult();
 
         m.loadContext();
 
         // for visibility checking without requiring frame self
         // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
         m.loadSelf(); // caller
 
         visit(receiver);
 
         m.invokeOtherOneFloat(name, flote);
 
         if (result != null) {
             jvmStoreLocal(result);
         } else {
             // still need to drop, since all dyncalls return something (FIXME)
             m.adapter.pop();
         }
     }
 
     @Override
     public void OneOperandArgNoBlockCallInstr(OneOperandArgNoBlockCallInstr oneOperandArgNoBlockCallInstr) {
         CallInstr(oneOperandArgNoBlockCallInstr);
     }
 
     @Override
     public void OptArgMultipleAsgnInstr(OptArgMultipleAsgnInstr optargmultipleasgninstr) {
         visit(optargmultipleasgninstr.getArrayArg());
         jvmAdapter().checkcast(p(RubyArray.class));
         jvmAdapter().ldc(optargmultipleasgninstr.getMinArgsLength());
         jvmAdapter().ldc(optargmultipleasgninstr.getIndex());
         jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "extractOptionalArgument", sig(IRubyObject.class, RubyArray.class, int.class, int.class));
         jvmStoreLocal(optargmultipleasgninstr.getResult());
     }
 
     @Override
     public void PopBindingInstr(PopBindingInstr popbindinginstr) {
         jvmMethod().loadContext();
         jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("void popScope()"));
     }
 
     @Override
     public void PopFrameInstr(PopFrameInstr popframeinstr) {
         jvmMethod().loadContext();
         jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("void postMethodFrameOnly()"));
     }
 
     @Override
     public void ProcessModuleBodyInstr(ProcessModuleBodyInstr processmodulebodyinstr) {
         jvmMethod().loadContext();
         visit(processmodulebodyinstr.getModuleBody());
         jvmMethod().invokeIRHelper("invokeModuleBody", sig(IRubyObject.class, ThreadContext.class, DynamicMethod.class));
         jvmStoreLocal(processmodulebodyinstr.getResult());
     }
 
     @Override
     public void PushBindingInstr(PushBindingInstr pushbindinginstr) {
         jvmMethod().loadContext();
         jvmMethod().loadStaticScope();
         jvmAdapter().invokestatic(p(DynamicScope.class), "newDynamicScope", sig(DynamicScope.class, StaticScope.class));
         jvmAdapter().dup();
         jvmStoreLocal(DYNAMIC_SCOPE);
         jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("void pushScope(org.jruby.runtime.DynamicScope)"));
     }
 
     @Override
     public void RaiseRequiredKeywordArgumentErrorInstr(RaiseRequiredKeywordArgumentError instr) {
         jvmMethod().loadContext();
         jvmAdapter().ldc(instr.getName());
         jvmMethod().invokeIRHelper("newRequiredKeywordArgumentError", sig(RaiseException.class, ThreadContext.class, String.class));
         jvmAdapter().athrow();
     }
 
     @Override
     public void PushFrameInstr(PushFrameInstr pushframeinstr) {
         jvmMethod().loadContext();
         jvmMethod().loadFrameClass();
         jvmAdapter().ldc(pushframeinstr.getFrameName());
         jvmMethod().loadSelf();
         jvmMethod().loadBlock();
         jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("void preMethodFrameOnly(org.jruby.RubyModule, String, org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.Block)"));
 
         // FIXME: this should be part of explicit call protocol only when needed, optimizable, and correct for the scope
         // See also CompiledIRMethod.call
         jvmMethod().loadContext();
         jvmAdapter().invokestatic(p(Visibility.class), "values", sig(Visibility[].class));
         jvmAdapter().ldc(Visibility.PUBLIC.ordinal());
         jvmAdapter().aaload();
         jvmAdapter().invokevirtual(p(ThreadContext.class), "setCurrentVisibility", sig(void.class, Visibility.class));
     }
 
     @Override
     public void PutClassVariableInstr(PutClassVariableInstr putclassvariableinstr) {
         visit(putclassvariableinstr.getValue());
         visit(putclassvariableinstr.getTarget());
 
         // don't understand this logic; duplicated from interpreter
         if (putclassvariableinstr.getValue() instanceof CurrentScope) {
             jvmAdapter().pop2();
             return;
         }
 
         // hmm.
         jvmAdapter().checkcast(p(RubyModule.class));
         jvmAdapter().swap();
         jvmAdapter().ldc(putclassvariableinstr.getRef());
         jvmAdapter().swap();
         jvmAdapter().invokevirtual(p(RubyModule.class), "setClassVar", sig(IRubyObject.class, String.class, IRubyObject.class));
         jvmAdapter().pop();
     }
 
     @Override
     public void PutConstInstr(PutConstInstr putconstinstr) {
         IRBytecodeAdapter m = jvmMethod();
         visit(putconstinstr.getTarget());
         m.adapter.checkcast(p(RubyModule.class));
         m.adapter.ldc(putconstinstr.getRef());
         visit(putconstinstr.getValue());
         m.adapter.invokevirtual(p(RubyModule.class), "setConstant", sig(IRubyObject.class, String.class, IRubyObject.class));
         m.adapter.pop();
     }
 
     @Override
     public void PutFieldInstr(PutFieldInstr putfieldinstr) {
         visit(putfieldinstr.getTarget());
         visit(putfieldinstr.getValue());
         jvmMethod().putField(putfieldinstr.getRef());
     }
 
     @Override
     public void PutGlobalVarInstr(PutGlobalVarInstr putglobalvarinstr) {
         GlobalVariable target = (GlobalVariable)putglobalvarinstr.getTarget();
         String name = target.getName();
         jvmMethod().loadRuntime();
         jvmMethod().invokeVirtual(Type.getType(Ruby.class), Method.getMethod("org.jruby.internal.runtime.GlobalVariables getGlobalVariables()"));
         jvmAdapter().ldc(name);
         visit(putglobalvarinstr.getValue());
         jvmMethod().invokeVirtual(Type.getType(GlobalVariables.class), Method.getMethod("org.jruby.runtime.builtin.IRubyObject set(String, org.jruby.runtime.builtin.IRubyObject)"));
         // leaves copy of value on stack
         jvmAdapter().pop();
     }
 
     @Override
     public void ReceiveClosureInstr(ReceiveClosureInstr receiveclosureinstr) {
         jvmMethod().loadRuntime();
         jvmLoadLocal("$block");
         jvmMethod().invokeIRHelper("newProc", sig(IRubyObject.class, Ruby.class, Block.class));
         jvmStoreLocal(receiveclosureinstr.getResult());
     }
 
     @Override
     public void ReceiveRubyExceptionInstr(ReceiveRubyExceptionInstr receiveexceptioninstr) {
         // exception should be on stack from try/catch, so unwrap and store it
         jvmStoreLocal(receiveexceptioninstr.getResult());
     }
 
     @Override
     public void ReceiveJRubyExceptionInstr(ReceiveJRubyExceptionInstr receiveexceptioninstr) {
         // exception should be on stack from try/catch, so just store it
         jvmStoreLocal(receiveexceptioninstr.getResult());
     }
 
     @Override
     public void ReceiveKeywordArgInstr(ReceiveKeywordArgInstr instr) {
         jvmMethod().loadContext();
         jvmMethod().loadArgs();
         jvmAdapter().pushInt(instr.required);
         jvmAdapter().ldc(instr.argName);
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
         jvmMethod().invokeIRHelper("receiveKeywordArg", sig(IRubyObject.class, ThreadContext.class, IRubyObject[].class, int.class, String.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void ReceiveKeywordRestArgInstr(ReceiveKeywordRestArgInstr instr) {
         jvmMethod().loadContext();
         jvmMethod().loadArgs();
         jvmAdapter().pushInt(instr.required);
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
         jvmMethod().invokeIRHelper("receiveKeywordRestArg", sig(IRubyObject.class, ThreadContext.class, IRubyObject[].class, int.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void ReceiveOptArgInstr(ReceiveOptArgInstr instr) {
         jvmMethod().loadArgs();
         jvmAdapter().pushInt(instr.requiredArgs);
         jvmAdapter().pushInt(instr.preArgs);
         jvmAdapter().pushInt(instr.getArgIndex());
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
         jvmMethod().invokeIRHelper("receiveOptArg", sig(IRubyObject.class, IRubyObject[].class, int.class, int.class, int.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void ReceivePreReqdArgInstr(ReceivePreReqdArgInstr instr) {
         if (jvm.methodData().specificArity >= 0 &&
                 instr.getArgIndex() < jvm.methodData().specificArity) {
             jvmAdapter().aload(jvm.methodData().signature.argOffset("arg" + instr.getArgIndex()));
         } else {
             jvmMethod().loadContext();
             jvmMethod().loadArgs();
             jvmAdapter().pushInt(instr.getArgIndex());
             jvmMethod().invokeIRHelper("getPreArgSafe", sig(IRubyObject.class, ThreadContext.class, IRubyObject[].class, int.class));
         }
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void ReceivePostReqdArgInstr(ReceivePostReqdArgInstr instr) {
         jvmMethod().loadArgs();
         jvmAdapter().pushInt(instr.preReqdArgsCount);
         jvmAdapter().pushInt(instr.postReqdArgsCount);
         jvmAdapter().pushInt(instr.getArgIndex());
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
         jvmMethod().invokeIRHelper("receivePostReqdArg", sig(IRubyObject.class, IRubyObject[].class, int.class, int.class, int.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void ReceiveRestArgInstr(ReceiveRestArgInstr instr) {
         jvmMethod().loadContext();
         jvmMethod().loadArgs();
         jvmAdapter().pushInt(instr.required);
         jvmAdapter().pushInt(instr.getArgIndex());
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
         jvmMethod().invokeIRHelper("receiveRestArg", sig(IRubyObject.class, ThreadContext.class, Object[].class, int.class, int.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void ReceiveSelfInstr(ReceiveSelfInstr receiveselfinstr) {
         jvmMethod().loadSelf();
         jvmStoreLocal(receiveselfinstr.getResult());
     }
 
     @Override
     public void RecordEndBlockInstr(RecordEndBlockInstr recordEndBlockInstr) {
         jvmMethod().loadContext();
 
         jvmMethod().loadContext();
         visit(recordEndBlockInstr.getEndBlockClosure());
         jvmMethod().invokeIRHelper("getBlockFromObject", sig(Block.class, ThreadContext.class, Object.class));
 
         jvmMethod().invokeIRHelper("pushExitBlock", sig(void.class, ThreadContext.class, Block.class));
     }
 
     @Override
     public void ReqdArgMultipleAsgnInstr(ReqdArgMultipleAsgnInstr reqdargmultipleasgninstr) {
         jvmMethod().loadContext();
         visit(reqdargmultipleasgninstr.getArrayArg());
         jvmAdapter().checkcast(p(RubyArray.class));
         jvmAdapter().pushInt(reqdargmultipleasgninstr.getPreArgsCount());
         jvmAdapter().pushInt(reqdargmultipleasgninstr.getIndex());
         jvmAdapter().pushInt(reqdargmultipleasgninstr.getPostArgsCount());
         jvmMethod().invokeIRHelper("irReqdArgMultipleAsgn", sig(IRubyObject.class, ThreadContext.class, RubyArray.class, int.class, int.class, int.class));
         jvmStoreLocal(reqdargmultipleasgninstr.getResult());
     }
 
     @Override
     public void RescueEQQInstr(RescueEQQInstr rescueeqqinstr) {
         jvmMethod().loadContext();
         visit(rescueeqqinstr.getArg1());
         visit(rescueeqqinstr.getArg2());
         jvmMethod().invokeIRHelper("isExceptionHandled", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, Object.class));
         jvmStoreLocal(rescueeqqinstr.getResult());
     }
 
     @Override
     public void RestArgMultipleAsgnInstr(RestArgMultipleAsgnInstr restargmultipleasgninstr) {
         jvmMethod().loadContext();
         visit(restargmultipleasgninstr.getArrayArg());
         jvmAdapter().checkcast(p(RubyArray.class));
         jvmAdapter().pushInt(restargmultipleasgninstr.getPreArgsCount());
         jvmAdapter().pushInt(restargmultipleasgninstr.getPostArgsCount());
         jvmAdapter().invokestatic(p(Helpers.class), "viewArgsArray", sig(RubyArray.class, ThreadContext.class, RubyArray.class, int.class, int.class));
         jvmStoreLocal(restargmultipleasgninstr.getResult());
     }
 
     @Override
     public void RuntimeHelperCall(RuntimeHelperCall runtimehelpercall) {
         switch (runtimehelpercall.getHelperMethod()) {
             case HANDLE_PROPAGATE_BREAK:
                 jvmMethod().loadContext();
                 jvmLoadLocal(DYNAMIC_SCOPE);
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmMethod().loadBlockType();
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "handlePropagatedBreak", sig(IRubyObject.class, ThreadContext.class, DynamicScope.class, Object.class, Block.Type.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case HANDLE_NONLOCAL_RETURN:
                 jvmMethod().loadStaticScope();
                 jvmLoadLocal(DYNAMIC_SCOPE);
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmMethod().loadBlockType();
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "handleNonlocalReturn", sig(IRubyObject.class, StaticScope.class, DynamicScope.class, Object.class, Block.Type.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case HANDLE_BREAK_AND_RETURNS_IN_LAMBDA:
                 jvmMethod().loadContext();
                 jvmMethod().loadStaticScope();
                 jvmLoadLocal(DYNAMIC_SCOPE);
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmMethod().loadBlockType();
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "handleBreakAndReturnsInLambdas", sig(IRubyObject.class, ThreadContext.class, StaticScope.class, DynamicScope.class, Object.class, Block.Type.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_BACKREF:
                 jvmMethod().loadContext();
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedBackref", sig(IRubyObject.class, ThreadContext.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_CALL:
                 jvmMethod().loadContext();
                 jvmMethod().loadSelf();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((StringLiteral) runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedCall", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_CONSTANT_OR_METHOD:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((StringLiteral)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedConstantOrMethod", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_NTH_REF:
                 jvmMethod().loadContext();
                 jvmAdapter().ldc((int)((Fixnum)runtimehelpercall.getArgs()[0]).getValue());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedNthRef", sig(IRubyObject.class, ThreadContext.class, int.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_GLOBAL:
                 jvmMethod().loadContext();
                 jvmAdapter().ldc(((StringLiteral)runtimehelpercall.getArgs()[0]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedGlobal", sig(IRubyObject.class, ThreadContext.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_INSTANCE_VAR:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((StringLiteral)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedInstanceVar", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_CLASS_VAR:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().checkcast(p(RubyModule.class));
                 jvmAdapter().ldc(((StringLiteral)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedClassVar", sig(IRubyObject.class, ThreadContext.class, RubyModule.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_SUPER:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((StringLiteral)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedSuper", sig(IRubyObject.class, ThreadContext.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_METHOD:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((StringLiteral) runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().ldc(((Boolean)runtimehelpercall.getArgs()[2]).isTrue());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedMethod", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class, boolean.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case MERGE_KWARGS:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 visit(runtimehelpercall.getArgs()[1]);
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "mergeKeywordArguments", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case CHECK_FOR_LJE:
                 jvmMethod().loadContext();
                 jvmLoadLocal(DYNAMIC_SCOPE);
                 jvmAdapter().ldc(((Boolean)runtimehelpercall.getArgs()[0]).isTrue());
                 jvmMethod().loadBlockType();
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "checkForLJE", sig(void.class, ThreadContext.class, DynamicScope.class, boolean.class, Block.Type.class));
                 break;
             default:
                 throw new NotCompilableException("Unknown IR runtime helper method: " + runtimehelpercall.getHelperMethod() + "; INSTR: " + this);
         }
     }
 
     @Override
     public void NonlocalReturnInstr(NonlocalReturnInstr returninstr) {
         jvmMethod().loadContext();
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmMethod().loadBlockType();
         visit(returninstr.getReturnValue());
 
         jvmMethod().invokeIRHelper("initiateNonLocalReturn", sig(IRubyObject.class, ThreadContext.class, DynamicScope.class, Block.Type.class, IRubyObject.class));
         jvmMethod().returnValue();
     }
 
     @Override
     public void ReturnInstr(ReturnInstr returninstr) {
         visit(returninstr.getReturnValue());
         jvmMethod().returnValue();
     }
 
     @Override
     public void SearchConstInstr(SearchConstInstr searchconstinstr) {
         jvmMethod().loadContext();
         visit(searchconstinstr.getStartingScope());
         jvmMethod().searchConst(searchconstinstr.getConstName(), searchconstinstr.isNoPrivateConsts());
         jvmStoreLocal(searchconstinstr.getResult());
     }
 
     @Override
     public void SetCapturedVarInstr(SetCapturedVarInstr instr) {
         jvmMethod().loadContext();
         visit(instr.getMatch2Result());
         jvmAdapter().ldc(instr.getVarName());
         jvmMethod().invokeIRHelper("setCapturedVar", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void StoreLocalVarInstr(StoreLocalVarInstr storelocalvarinstr) {
         IRBytecodeAdapter m = jvmMethod();
         jvmLoadLocal(DYNAMIC_SCOPE);
         int depth = storelocalvarinstr.getLocalVar().getScopeDepth();
         int location = storelocalvarinstr.getLocalVar().getLocation();
         Operand storeValue = storelocalvarinstr.getValue();
         switch (depth) {
             case 0:
                 switch (location) {
                     case 0:
                         storeValue.visit(this);
                         m.adapter.invokevirtual(p(DynamicScope.class), "setValueZeroDepthZero", sig(IRubyObject.class, IRubyObject.class));
                         m.adapter.pop();
                         return;
                     case 1:
                         storeValue.visit(this);
                         m.adapter.invokevirtual(p(DynamicScope.class), "setValueOneDepthZero", sig(IRubyObject.class, IRubyObject.class));
                         m.adapter.pop();
                         return;
                     case 2:
                         storeValue.visit(this);
                         m.adapter.invokevirtual(p(DynamicScope.class), "setValueTwoDepthZero", sig(IRubyObject.class, IRubyObject.class));
                         m.adapter.pop();
                         return;
                     case 3:
                         storeValue.visit(this);
                         m.adapter.invokevirtual(p(DynamicScope.class), "setValueThreeDepthZero", sig(IRubyObject.class, IRubyObject.class));
                         m.adapter.pop();
                         return;
                     default:
                         storeValue.visit(this);
                         m.adapter.pushInt(location);
                         m.adapter.invokevirtual(p(DynamicScope.class), "setValueDepthZero", sig(IRubyObject.class, IRubyObject.class, int.class));
                         m.adapter.pop();
                         return;
                 }
             default:
                 m.adapter.pushInt(location);
                 storeValue.visit(this);
                 m.adapter.pushInt(depth);
                 m.adapter.invokevirtual(p(DynamicScope.class), "setValue", sig(IRubyObject.class, int.class, IRubyObject.class, int.class));
                 m.adapter.pop();
         }
     }
 
     @Override
     public void ThreadPollInstr(ThreadPollInstr threadpollinstr) {
         jvmMethod().checkpoint();
     }
 
     @Override
     public void ThrowExceptionInstr(ThrowExceptionInstr throwexceptioninstr) {
         visit(throwexceptioninstr.getExceptionArg());
         jvmAdapter().athrow();
     }
 
     @Override
     public void ToAryInstr(ToAryInstr toaryinstr) {
         jvmMethod().loadContext();
         visit(toaryinstr.getArrayArg());
         jvmMethod().invokeIRHelper("irToAry", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class));
         jvmStoreLocal(toaryinstr.getResult());
     }
 
     @Override
     public void UndefMethodInstr(UndefMethodInstr undefmethodinstr) {
         jvmMethod().loadContext();
         visit(undefmethodinstr.getMethodName());
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmMethod().loadSelf();
         jvmMethod().invokeIRHelper("undefMethod", sig(IRubyObject.class, ThreadContext.class, Object.class, DynamicScope.class, IRubyObject.class));
         jvmStoreLocal(undefmethodinstr.getResult());
     }
 
     @Override
     public void UnresolvedSuperInstr(UnresolvedSuperInstr unresolvedsuperinstr) {
         String name = unresolvedsuperinstr.getName();
         Operand[] args = unresolvedsuperinstr.getCallArgs();
         // this would be getDefiningModule but that is not used for unresolved super
         Operand definingModule = UndefinedValue.UNDEFINED;
         boolean containsArgSplat = unresolvedsuperinstr.containsArgSplat();
         Operand closure = unresolvedsuperinstr.getClosureArg(null);
 
         superCommon(name, unresolvedsuperinstr, args, definingModule, containsArgSplat, closure);
     }
 
     @Override
     public void YieldInstr(YieldInstr yieldinstr) {
         jvmMethod().loadContext();
         visit(yieldinstr.getBlockArg());
 
         if (yieldinstr.getYieldArg() == UndefinedValue.UNDEFINED) {
             jvmMethod().invokeIRHelper("yieldSpecific", sig(IRubyObject.class, ThreadContext.class, Object.class));
         } else {
             visit(yieldinstr.getYieldArg());
             jvmAdapter().ldc(yieldinstr.isUnwrapArray());
             jvmMethod().invokeIRHelper("yield", sig(IRubyObject.class, ThreadContext.class, Object.class, Object.class, boolean.class));
         }
 
         jvmStoreLocal(yieldinstr.getResult());
     }
 
     @Override
     public void ZeroOperandArgNoBlockCallInstr(ZeroOperandArgNoBlockCallInstr zeroOperandArgNoBlockCallInstr) {
         CallInstr(zeroOperandArgNoBlockCallInstr);
     }
 
     @Override
     public void ZSuperInstr(ZSuperInstr zsuperinstr) {
         String name = zsuperinstr.getName();
         Operand[] args = zsuperinstr.getCallArgs();
         // this would be getDefiningModule but that is not used for unresolved super
         Operand definingModule = UndefinedValue.UNDEFINED;
         boolean containsArgSplat = zsuperinstr.containsArgSplat();
         Operand closure = zsuperinstr.getClosureArg(null);
 
         superCommon(name, zsuperinstr, args, definingModule, containsArgSplat, closure);
     }
 
     @Override
     public void GetErrorInfoInstr(GetErrorInfoInstr geterrorinfoinstr) {
         jvmMethod().loadContext();
         jvmAdapter().invokevirtual(p(ThreadContext.class), "getErrorInfo", sig(IRubyObject.class));
         jvmStoreLocal(geterrorinfoinstr.getResult());
     }
 
     @Override
     public void RestoreErrorInfoInstr(RestoreErrorInfoInstr restoreerrorinfoinstr) {
         jvmMethod().loadContext();
         visit(restoreerrorinfoinstr.getArg());
         jvmAdapter().invokevirtual(p(ThreadContext.class), "setErrorInfo", sig(IRubyObject.class, IRubyObject.class));
         jvmAdapter().pop();
     }
 
     // ruby 1.9 specific
     @Override
     public void BuildLambdaInstr(BuildLambdaInstr buildlambdainstr) {
         jvmMethod().loadRuntime();
 
         IRClosure body = ((WrappedIRClosure)buildlambdainstr.getLambdaBody()).getClosure();
         if (body == null) {
             jvmMethod().pushNil();
         } else {
             visit(buildlambdainstr.getLambdaBody());
         }
 
         jvmAdapter().getstatic(p(Block.Type.class), "LAMBDA", ci(Block.Type.class));
         jvmAdapter().ldc(buildlambdainstr.getPosition().getFile());
         jvmAdapter().pushInt(buildlambdainstr.getPosition().getLine());
 
         jvmAdapter().invokestatic(p(RubyProc.class), "newProc", sig(RubyProc.class, Ruby.class, Block.class, Block.Type.class, String.class, int.class));
 
         jvmStoreLocal(buildlambdainstr.getResult());
     }
 
     @Override
     public void GetEncodingInstr(GetEncodingInstr getencodinginstr) {
         jvmMethod().loadContext();
         jvmMethod().pushEncoding(getencodinginstr.getEncoding());
         jvmStoreLocal(getencodinginstr.getResult());
     }
 
     // operands
     @Override
     public void Array(Array array) {
         jvmMethod().loadContext();
 
         for (Operand operand : array.getElts()) {
             visit(operand);
         }
 
         jvmMethod().array(array.getElts().length);
     }
 
     @Override
     public void AsString(AsString asstring) {
         visit(asstring.getSource());
         jvmAdapter().invokeinterface(p(IRubyObject.class), "asString", sig(RubyString.class));
     }
 
     @Override
     public void Backref(Backref backref) {
         jvmMethod().loadContext();
         jvmAdapter().invokevirtual(p(ThreadContext.class), "getBackRef", sig(IRubyObject.class));
 
         switch (backref.type) {
             case '&':
                 jvmAdapter().invokestatic(p(RubyRegexp.class), "last_match", sig(IRubyObject.class, IRubyObject.class));
                 break;
             case '`':
                 jvmAdapter().invokestatic(p(RubyRegexp.class), "match_pre", sig(IRubyObject.class, IRubyObject.class));
                 break;
             case '\'':
                 jvmAdapter().invokestatic(p(RubyRegexp.class), "match_post", sig(IRubyObject.class, IRubyObject.class));
                 break;
             case '+':
                 jvmAdapter().invokestatic(p(RubyRegexp.class), "match_last", sig(IRubyObject.class, IRubyObject.class));
                 break;
             default:
                 assert false: "backref with invalid type";
         }
     }
 
     @Override
     public void Bignum(Bignum bignum) {
         jvmMethod().pushBignum(bignum.value);
     }
 
     @Override
     public void Boolean(org.jruby.ir.operands.Boolean booleanliteral) {
         jvmMethod().pushBoolean(booleanliteral.isTrue());
     }
 
     @Override
     public void UnboxedBoolean(org.jruby.ir.operands.UnboxedBoolean bool) {
         jvmAdapter().ldc(bool.isTrue());
     }
 
     @Override
     public void ClosureLocalVariable(ClosureLocalVariable closurelocalvariable) {
         LocalVariable(closurelocalvariable);
     }
 
     @Override
     public void Complex(Complex complex) {
         jvmMethod().loadRuntime();
         jvmMethod().pushFixnum(0);
         visit(complex.getNumber());
         jvmAdapter().invokestatic(p(RubyComplex.class), "newComplexRaw", sig(RubyComplex.class, Ruby.class, IRubyObject.class, IRubyObject.class));
     }
 
     @Override
     public void CurrentScope(CurrentScope currentscope) {
         jvmMethod().loadStaticScope();
     }
 
     @Override
     public void DynamicSymbol(DynamicSymbol dynamicsymbol) {
         jvmMethod().loadRuntime();
         visit(dynamicsymbol.getSymbolName());
         jvmAdapter().invokeinterface(p(IRubyObject.class), "asJavaString", sig(String.class));
         jvmAdapter().invokevirtual(p(Ruby.class), "newSymbol", sig(RubySymbol.class, String.class));
     }
 
     @Override
     public void Fixnum(Fixnum fixnum) {
         jvmMethod().pushFixnum(fixnum.getValue());
     }
 
     @Override
     public void FrozenString(FrozenString frozen) {
         jvmMethod().pushFrozenString(frozen.getByteList());
     }
 
     @Override
     public void UnboxedFixnum(UnboxedFixnum fixnum) {
         jvmAdapter().ldc(fixnum.getValue());
     }
 
     @Override
     public void Float(org.jruby.ir.operands.Float flote) {
         jvmMethod().pushFloat(flote.getValue());
     }
 
     @Override
     public void UnboxedFloat(org.jruby.ir.operands.UnboxedFloat flote) {
         jvmAdapter().ldc(flote.getValue());
     }
 
     @Override
     public void Hash(Hash hash) {
         List<KeyValuePair<Operand, Operand>> pairs = hash.getPairs();
         Iterator<KeyValuePair<Operand, Operand>> iter = pairs.iterator();
         boolean kwargs = hash.isKWArgsHash && pairs.get(0).getKey() == Symbol.KW_REST_ARG_DUMMY;
 
         jvmMethod().loadContext();
         if (kwargs) {
             visit(pairs.get(0).getValue());
             jvmAdapter().checkcast(p(RubyHash.class));
 
             iter.next();
         }
 
         for (; iter.hasNext() ;) {
             KeyValuePair<Operand, Operand> pair = iter.next();
             visit(pair.getKey());
             visit(pair.getValue());
         }
 
         if (kwargs) {
             jvmMethod().kwargsHash(pairs.size() - 1);
         } else {
             jvmMethod().hash(pairs.size());
         }
     }
 
     @Override
     public void LocalVariable(LocalVariable localvariable) {
         // CON FIXME: This isn't as efficient as it could be, but we should not see these in optimized JIT scopes
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmAdapter().ldc(localvariable.getOffset());
         jvmAdapter().ldc(localvariable.getScopeDepth());
         jvmMethod().pushNil();
         jvmAdapter().invokevirtual(p(DynamicScope.class), "getValueOrNil", sig(IRubyObject.class, int.class, int.class, IRubyObject.class));
     }
 
     @Override
     public void Nil(Nil nil) {
         jvmMethod().pushNil();
     }
 
     @Override
     public void NthRef(NthRef nthref) {
         jvmMethod().loadContext();
         jvmAdapter().pushInt(nthref.matchNumber);
         jvmMethod().invokeIRHelper("nthMatch", sig(IRubyObject.class, ThreadContext.class, int.class));
     }
 
     @Override
     public void ObjectClass(ObjectClass objectclass) {
         jvmMethod().pushObjectClass();
     }
 
     @Override
     public void Rational(Rational rational) {
         jvmMethod().loadRuntime();
         jvmAdapter().ldc(rational.getNumerator());
         jvmAdapter().ldc(rational.getDenominator());
         jvmAdapter().invokevirtual(p(Ruby.class), "newRational", sig(RubyRational.class, long.class, long.class));
     }
 
     @Override
     public void Regexp(Regexp regexp) {
         if (!regexp.hasKnownValue() && !regexp.options.isOnce()) {
             jvmMethod().loadRuntime();
             visit(regexp.getRegexp());
             jvmAdapter().invokevirtual(p(RubyString.class), "getByteList", sig(ByteList.class));
             jvmAdapter().ldc(regexp.options.toEmbeddedOptions());
             jvmAdapter().invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, Ruby.class, RubyString.class, int.class));
             jvmAdapter().dup();
             jvmAdapter().invokevirtual(p(RubyRegexp.class), "setLiteral", sig(void.class));
         } else {
             // FIXME: need to check this on cached path
             // context.runtime.getKCode() != rubyRegexp.getKCode()) {
             jvmMethod().loadContext();
             visit(regexp.getRegexp());
             jvmMethod().pushRegexp(regexp.options.toEmbeddedOptions());
         }
     }
 
     @Override
     public void ScopeModule(ScopeModule scopemodule) {
         jvmMethod().loadStaticScope();
         jvmAdapter().pushInt(scopemodule.getScopeModuleDepth());
         jvmAdapter().invokestatic(p(Helpers.class), "getNthScopeModule", sig(RubyModule.class, StaticScope.class, int.class));
     }
 
     @Override
     public void Self(Self self) {
         jvmMethod().loadSelf();
     }
 
     @Override
     public void Splat(Splat splat) {
         jvmMethod().loadContext();
         visit(splat.getArray());
-        jvmMethod().invokeIRHelper("irSplat", sig(RubyArray.class, ThreadContext.class, IRubyObject.class));
+        // Splat is now only used in call arg lists where it is guaranteed that
+        // the splat-arg is an array.
+        //
+        // It is:
+        // - either a result of a args-cat/args-push (which generate an array),
+        // - or a result of a BuildSplatInstr (which also generates an array),
+        // - or a rest-arg that has been received (which also generates an array)
+        //   and is being passed via zsuper.
+        //
+        // In addition, since this only shows up in call args, the array itself is
+        // never modified. The array elements are extracted out and inserted into
+        // a java array. So, a dup is not required either.
+        //
+        // So, besides retrieving the array, nothing more to be done here!
     }
 
     @Override
     public void StandardError(StandardError standarderror) {
         jvmMethod().loadRuntime();
         jvmAdapter().invokevirtual(p(Ruby.class), "getStandardError", sig(RubyClass.class));
     }
 
     @Override
     public void StringLiteral(StringLiteral stringliteral) {
         jvmMethod().pushString(stringliteral.getByteList());
     }
 
     @Override
     public void SValue(SValue svalue) {
         visit(svalue.getArray());
         jvmAdapter().dup();
         jvmAdapter().instance_of(p(RubyArray.class));
         org.objectweb.asm.Label after = new org.objectweb.asm.Label();
         jvmAdapter().iftrue(after);
         jvmAdapter().pop();
         jvmMethod().pushNil();
         jvmAdapter().label(after);
     }
 
     @Override
     public void Symbol(Symbol symbol) {
         jvmMethod().pushSymbol(symbol.getName(), symbol.getEncoding());
     }
 
     @Override
     public void TemporaryVariable(TemporaryVariable temporaryvariable) {
         jvmLoadLocal(temporaryvariable);
     }
 
     @Override
     public void TemporaryLocalVariable(TemporaryLocalVariable temporarylocalvariable) {
         jvmLoadLocal(temporarylocalvariable);
     }
 
     @Override
     public void TemporaryFloatVariable(TemporaryFloatVariable temporaryfloatvariable) {
         jvmLoadLocal(temporaryfloatvariable);
     }
 
     @Override
     public void TemporaryFixnumVariable(TemporaryFixnumVariable temporaryfixnumvariable) {
         jvmLoadLocal(temporaryfixnumvariable);
     }
 
     @Override
     public void TemporaryBooleanVariable(TemporaryBooleanVariable temporarybooleanvariable) {
         jvmLoadLocal(temporarybooleanvariable);
     }
 
     @Override
     public void UndefinedValue(UndefinedValue undefinedvalue) {
         jvmMethod().pushUndefined();
     }
 
     @Override
     public void UnexecutableNil(UnexecutableNil unexecutablenil) {
         throw new NotCompilableException(this.getClass().getSimpleName() + " should never be directly executed!");
     }
 
     @Override
     public void WrappedIRClosure(WrappedIRClosure wrappedirclosure) {
         IRClosure closure = wrappedirclosure.getClosure();
 
         jvmAdapter().newobj(p(Block.class));
         jvmAdapter().dup();
 
         { // FIXME: block body should be cached
             jvmAdapter().newobj(p(CompiledIRBlockBody.class));
             jvmAdapter().dup();
 
             jvmAdapter().ldc(closure.getHandle());
             jvmAdapter().getstatic(jvm.clsData().clsName, closure.getHandle().getName() + "_IRScope", ci(IRScope.class));
             jvmAdapter().ldc(closure.getArity().getValue());
 
             jvmAdapter().invokespecial(p(CompiledIRBlockBody.class), "<init>", sig(void.class, java.lang.invoke.MethodHandle.class, IRScope.class, int.class));
         }
 
         { // prepare binding
             jvmMethod().loadContext();
             visit(closure.getSelf());
             jvmLoadLocal(DYNAMIC_SCOPE);
             jvmAdapter().invokevirtual(p(ThreadContext.class), "currentBinding", sig(Binding.class, IRubyObject.class, DynamicScope.class));
         }
 
         jvmAdapter().invokespecial(p(Block.class), "<init>", sig(void.class, BlockBody.class, Binding.class));
     }
 
     private SkinnyMethodAdapter jvmAdapter() {
         return jvmMethod().adapter;
     }
 
     private IRBytecodeAdapter jvmMethod() {
         return jvm.method();
     }
 
     private JVM jvm;
     private int methodIndex;
     private Map<String, IRScope> scopeMap;
 }
