diff --git a/core/src/main/java/org/jruby/RubyProc.java b/core/src/main/java/org/jruby/RubyProc.java
index b457f24882..5f91596737 100644
--- a/core/src/main/java/org/jruby/RubyProc.java
+++ b/core/src/main/java/org/jruby/RubyProc.java
@@ -1,381 +1,377 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.IRBlockBody;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.Signature;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.DataType;
 import org.jruby.util.ArraySupport;
 
 import java.util.Arrays;
 
 /**
  * @author  jpetersen
  */
 @JRubyClass(name="Proc")
 public class RubyProc extends RubyObject implements DataType {
     private Block block = Block.NULL_BLOCK;
     private Block.Type type;
     private String file = null;
     private int line = -1;
 
     protected RubyProc(Ruby runtime, RubyClass rubyClass, Block.Type type) {
         super(runtime, rubyClass);
 
         this.type = type;
     }
 
     @Deprecated
     protected RubyProc(Ruby runtime, RubyClass rubyClass, Block.Type type, ISourcePosition sourcePosition) {
         this(runtime, rubyClass, type, sourcePosition.getFile(), sourcePosition.getLine());
     }
 
     protected RubyProc(Ruby runtime, RubyClass rubyClass, Block.Type type, String file, int line) {
         this(runtime, rubyClass, type);
 
         this.file = file;
         this.line = line;
     }
 
 
     public RubyProc(Ruby runtime, RubyClass rubyClass, Block block, String file, int line) {
         this(runtime, rubyClass, block.type);
         this.block = block;
         this.file = file;
         this.line = line;
     }
 
     public static RubyClass createProcClass(Ruby runtime) {
         RubyClass procClass = runtime.defineClass("Proc", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setProc(procClass);
 
         procClass.setClassIndex(ClassIndex.PROC);
         procClass.setReifiedClass(RubyProc.class);
 
         procClass.defineAnnotatedMethods(RubyProc.class);
 
         return procClass;
     }
 
     public Block getBlock() {
         return block;
     }
 
     // Proc class
 
     @Deprecated
     public static RubyProc newProc(Ruby runtime, Block.Type type) {
         throw runtime.newRuntimeError("deprecated RubyProc.newProc with no block; do not use");
     }
 
     public static RubyProc newProc(Ruby runtime, Block block, Block.Type type) {
         RubyProc proc = new RubyProc(runtime, runtime.getProc(), type);
         proc.setup(block);
 
         return proc;
     }
 
     @Deprecated
     public static RubyProc newProc(Ruby runtime, Block block, Block.Type type, ISourcePosition sourcePosition) {
         RubyProc proc = new RubyProc(runtime, runtime.getProc(), type, sourcePosition);
         proc.setup(block);
 
         return proc;
     }
 
     public static RubyProc newProc(Ruby runtime, Block block, Block.Type type, String file, int line) {
         RubyProc proc = new RubyProc(runtime, runtime.getProc(), type, file, line);
         proc.setup(block);
 
         return proc;
     }
 
     /**
      * Create a new instance of a Proc object.  We override this method (from RubyClass)
      * since we need to deal with special case of Proc.new with no arguments or block arg.  In
      * this case, we need to check previous frame for a block to consume.
      */
     @JRubyMethod(name = "new", rest = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         // No passed in block, lets check next outer frame for one ('Proc.new')
         if (!block.isGiven()) block = context.getCurrentFrame().getBlock();
 
         // This metaclass == recv check seems gross, but MRI seems to do the same:
         // if (!proc && ruby_block->block_obj && CLASS_OF(ruby_block->block_obj) == klass) {
         if (block.isGiven() && block.getProcObject() != null && block.getProcObject().getMetaClass() == recv) {
             return block.getProcObject();
         }
 
         RubyProc obj = new RubyProc(context.runtime, (RubyClass)recv, Block.Type.PROC);
         obj.setup(block);
 
         obj.callMethod(context, "initialize", args, block);
         return obj;
     }
 
     private void setup(Block procBlock) {
         if (!procBlock.isGiven()) {
             throw getRuntime().newArgumentError("tried to create Proc object without a block");
         }
 
         if (isLambda()) {
             // TODO: warn "tried to create Proc object without a block"
         }
 
         if (isThread()) {
             // binding for incoming proc must not share frame
             Binding oldBinding = procBlock.getBinding();
             Binding newBinding = new Binding(
                     oldBinding.getSelf(),
                     oldBinding.getFrame().duplicate(),
                     oldBinding.getVisibility(),
                     oldBinding.getDynamicScope(),
                     oldBinding.getMethod(),
                     oldBinding.getFile(),
                     oldBinding.getLine());
             block = new Block(procBlock.getBody(), newBinding);
 
             // modify the block with a new backref/lastline-grabbing scope
             StaticScope oldScope = block.getBody().getStaticScope();
             StaticScope newScope = oldScope.duplicate();
             block.getBody().setStaticScope(newScope);
         } else {
             // just use as is
             block = procBlock;
         }
 
         // force file/line info into the new block's binding
         block.getBinding().setFile(block.getBody().getFile());
         block.getBinding().setLine(block.getBody().getLine());
 
         block.type = type;
         block.setProcObject(this);
 
         // pre-request dummy scope to avoid clone overhead in lightweight blocks
         block.getBinding().getDummyScope(block.getBody().getStaticScope());
     }
 
     @JRubyMethod(name = "clone")
     @Override
     public IRubyObject rbClone() {
     	RubyProc newProc = newProc(getRuntime(), block, type, file, line);
     	// TODO: CLONE_SETUP here
     	return newProc;
     }
 
     @JRubyMethod(name = "dup")
     @Override
     public IRubyObject dup() {
         return newProc(getRuntime(), block, type, file, line);
     }
 
     @Override
     public IRubyObject to_s() {
         return to_s19();
     }
 
     @JRubyMethod(name = "to_s", alias = "inspect")
     public IRubyObject to_s19() {
         StringBuilder sb = new StringBuilder(32);
         sb.append("#<Proc:0x").append(Integer.toString(System.identityHashCode(block), 16));
 
         String file = block.getBody().getFile();
         if (file != null) sb.append('@').append(file).append(':').append(block.getBody().getLine() + 1);
 
         if (isLambda()) sb.append(" (lambda)");
         sb.append('>');
 
         IRubyObject string = RubyString.newString(getRuntime(), sb.toString());
 
         if (isTaint()) string.setTaint(true);
 
         return string;
     }
 
     @JRubyMethod(name = "binding")
     public IRubyObject binding() {
         return getRuntime().newBinding(block.getBinding());
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Block block) {
         return call19(context, args, block);
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject[] args) {
         return call(context, args, null, Block.NULL_BLOCK);
     }
 
     /**
      * For Type.LAMBDA, ensures that the args have the correct arity.
      *
      * For others, transforms the given arguments appropriately for the given arity (i.e. trimming to one arg for fixed
      * arity of one, etc.)
      */
     public static IRubyObject[] prepareArgs(ThreadContext context, Block.Type type, BlockBody blockBody, IRubyObject[] args) {
         Signature signature = blockBody.getSignature();
 
         if (args == null) return IRubyObject.NULL_ARRAY;
 
         if (type == Block.Type.LAMBDA) {
             signature.checkArity(context.runtime, args);
             return args;
         }
 
         boolean isFixed = signature.isFixed();
         int required = signature.required();
         int actual = args.length;
-        boolean restKwargs = blockBody instanceof IRBlockBody && ((IRBlockBody) blockBody).getSignature().hasKwargs();
+        boolean kwargs = blockBody instanceof IRBlockBody && signature.hasKwargs();
 
         // FIXME: This is a hot mess.  restkwargs factors into destructing a single element array as well.  I just weaved it into this logic.
         // for procs and blocks, single array passed to multi-arg must be spread
-        if ((signature != Signature.ONE_ARGUMENT &&  required != 0 && (isFixed || signature != Signature.OPTIONAL) || restKwargs) &&
+        if ((signature != Signature.ONE_ARGUMENT &&  required != 0 && (isFixed || signature != Signature.OPTIONAL) || kwargs) &&
                 actual == 1 && args[0].respondsTo("to_ary")) {
             IRubyObject newAry = Helpers.aryToAry(args[0]);
 
             // This is very common to yield in *IRBlockBody.  When we tackle call protocol for blocks this will combine.
             if (newAry.isNil()) {
                 args = new IRubyObject[] { args[0] };
             } else if (newAry instanceof RubyArray){
                 args = ((RubyArray) newAry).toJavaArrayMaybeUnsafe();
             } else {
                 throw context.runtime.newTypeError(args[0].getType().getName() + "#to_ary should return Array");
             }
             actual = args.length;
         }
 
+        // FIXME: This pruning only seems to be needed for any?/all? specs.
         // fixed arity > 0 with mismatch needs a new args array
-        if (isFixed && required > 0 && required != actual) {
-            IRubyObject[] newArgs = ArraySupport.newCopy(args, required);
-
-            if (required > actual) { // Not enough required args pad.
-                Helpers.fillNil(newArgs, actual, required, context.runtime);
-            }
-
-            args = newArgs;
+        int kwargsHackRequired = required + (kwargs ? 1 : 0);
+        if (isFixed && required > 0 && kwargsHackRequired != actual) {
+            args = ArraySupport.newCopy(args, kwargsHackRequired);
         }
 
         return args;
     }
 
     @JRubyMethod(name = {"call", "[]", "yield", "==="}, rest = true, omit = true)
     public final IRubyObject call19(ThreadContext context, IRubyObject[] args, Block blockCallArg) {
         IRubyObject[] preppedArgs = prepareArgs(context, type, block.getBody(), args);
 
         return call(context, preppedArgs, null, blockCallArg);
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject[] args, IRubyObject self, Block passedBlock) {
         assert args != null;
 
         Block newBlock;
 
         // bind to new self, if given
         if (self == null) {
             newBlock = block;
         } else {
             newBlock = block.cloneBlockAndFrame();
             newBlock.getBinding().setSelf(self);
         }
 
         return newBlock.call(context, args, passedBlock);
     }
 
     @JRubyMethod(name = "arity")
     public RubyFixnum arity() {
         Signature signature = block.getSignature();
 
         if (block.type == Block.Type.LAMBDA) return getRuntime().newFixnum(signature.arityValue());
 
         // FIXME: Consider min/max like MRI here instead of required + kwarg count.
         return getRuntime().newFixnum(signature.hasRest() ? signature.arityValue() : signature.required() + signature.getRequiredKeywordForArityCount());
     }
 
     @JRubyMethod(name = "to_proc")
     public RubyProc to_proc() {
     	return this;
     }
 
     @JRubyMethod
     public IRubyObject source_location(ThreadContext context) {
         Ruby runtime = context.runtime;
         if (file != null) return runtime.newArray(runtime.newString(file), runtime.newFixnum(line + 1 /*zero-based*/));
 
         if (block != null) {
             Binding binding = block.getBinding();
             return runtime.newArray(runtime.newString(binding.getFile()),
                     runtime.newFixnum(binding.getLine() + 1 /*zero-based*/));
         }
 
         return runtime.getNil();
     }
 
     @JRubyMethod
     public IRubyObject parameters(ThreadContext context) {
         BlockBody body = this.getBlock().getBody();
 
         return Helpers.argumentDescriptorsToParameters(context.runtime,
                 body.getArgumentDescriptors(), isLambda());
     }
 
     @JRubyMethod(name = "lambda?")
     public IRubyObject lambda_p(ThreadContext context) {
         return context.runtime.newBoolean(isLambda());
     }
 
     private boolean isLambda() {
         return type.equals(Block.Type.LAMBDA);
     }
 
     //private boolean isProc() {
     //    return type.equals(Block.Type.PROC);
     //}
 
     private boolean isThread() {
         return type.equals(Block.Type.THREAD);
     }
 
 }
diff --git a/core/src/main/java/org/jruby/ir/IRBuilder.java b/core/src/main/java/org/jruby/ir/IRBuilder.java
index ed8c0d30aa..f5c0c289e0 100644
--- a/core/src/main/java/org/jruby/ir/IRBuilder.java
+++ b/core/src/main/java/org/jruby/ir/IRBuilder.java
@@ -982,2128 +982,2130 @@ public class IRBuilder {
         }
 
         Label rBeginLabel = getNewLabel();
         Label rEndLabel   = getNewLabel();
         Label rescueLabel = getNewLabel();
 
         // Protected region
         addInstr(new LabelInstr(rBeginLabel));
         addInstr(new ExceptionRegionStartMarkerInstr(rescueLabel));
         Variable callResult = (Variable)codeBlock.run();
         addInstr(new JumpInstr(rEndLabel, true));
         addInstr(new ExceptionRegionEndMarkerInstr());
 
         // Receive exceptions (could be anything, but the handler only processes IRBreakJumps)
         addInstr(new LabelInstr(rescueLabel));
         Variable exc = createTemporaryVariable();
         addInstr(new ReceiveJRubyExceptionInstr(exc));
 
         // Handle break using runtime helper
         // --> IRRuntimeHelpers.handlePropagatedBreak(context, scope, bj, blockType)
         addInstr(new RuntimeHelperCall(callResult, HANDLE_PROPAGATED_BREAK, new Operand[]{exc} ));
 
         // End
         addInstr(new LabelInstr(rEndLabel));
 
         return callResult;
     }
 
     // Wrap call in a rescue handler that catches the IRBreakJump
     private void receiveBreakException(Operand block, final CallInstr callInstr) {
         receiveBreakException(block, new CodeBlock() { public Operand run() { addInstr(callInstr); return callInstr.getResult(); } });
     }
 
     public Operand buildCall(CallNode callNode) {
         Node callArgsNode = callNode.getArgsNode();
         Node receiverNode = callNode.getReceiverNode();
 
         // Frozen string optimization: check for "string".freeze
         if (receiverNode instanceof StrNode && callNode.getName().equals("freeze")) {
             StrNode asString = (StrNode) receiverNode;
             return new FrozenString(asString.getValue(), asString.getCodeRange(), asString.getPosition().getFile(), asString.getPosition().getLine());
         }
 
         // The receiver has to be built *before* call arguments are built
         // to preserve expected code execution order
         Operand receiver = buildWithOrder(receiverNode, callNode.containsVariableAssignment());
         Variable callResult = createTemporaryVariable();
 
         ArrayNode argsAry;
         if (
                 !callNode.isLazy() &&
                 callNode.getName().equals("[]") &&
                 callNode.getArgsNode() instanceof ArrayNode &&
                 (argsAry = (ArrayNode) callNode.getArgsNode()).size() == 1 &&
                 argsAry.get(0) instanceof StrNode &&
                 !scope.maybeUsingRefinements() &&
                 callNode.getIterNode() == null) {
             StrNode keyNode = (StrNode) argsAry.get(0);
             addInstr(ArrayDerefInstr.create(callResult, receiver, new FrozenString(keyNode.getValue(), keyNode.getCodeRange(), keyNode.getPosition().getFile(), keyNode.getLine())));
             return callResult;
         }
 
         Label lazyLabel = getNewLabel();
         Label endLabel = getNewLabel();
         if (callNode.isLazy()) {
             addInstr(new BNilInstr(lazyLabel, receiver));
         }
 
         Operand[] args = setupCallArgs(callArgsNode);
         Operand block = setupCallClosure(callNode.getIterNode());
 
         CallInstr callInstr = CallInstr.create(scope, callResult, callNode.getName(), receiver, args, block);
 
         // This is to support the ugly Proc.new with no block, which must see caller's frame
         if ( callNode.getName().equals("new") &&
              receiverNode instanceof ConstNode &&
              ((ConstNode)receiverNode).getName().equals("Proc")) {
             callInstr.setProcNew(true);
         }
 
         receiveBreakException(block, callInstr);
 
         if (callNode.isLazy()) {
             addInstr(new JumpInstr(endLabel));
             addInstr(new LabelInstr(lazyLabel));
             addInstr(new CopyInstr(callResult, manager.getNil()));
             addInstr(new LabelInstr(endLabel));
         }
 
         return callResult;
     }
 
     public Operand buildCase(CaseNode caseNode) {
         // scan all cases to see if we have a homogeneous literal case/when
         NodeType seenType = null;
         for (Node aCase : caseNode.getCases().children()) {
             WhenNode whenNode = (WhenNode)aCase;
             NodeType exprNodeType = whenNode.getExpressionNodes().getNodeType();
 
             if (seenType == null) {
                 seenType = exprNodeType;
             } else if (seenType != exprNodeType) {
                 seenType = null;
                 break;
             }
         }
 
         if (seenType != null) {
             switch (seenType) {
                 case FIXNUMNODE:
                     return buildFixnumCase(caseNode);
             }
         }
 
         // get the incoming case value
         Operand value = build(caseNode.getCaseNode());
 
         // This is for handling case statements without a value (see example below)
         //   case
         //     when true <blah>
         //     when false <blah>
         //   end
         if (value == null) value = UndefinedValue.UNDEFINED;
 
         Label     endLabel  = getNewLabel();
         boolean   hasElse   = (caseNode.getElseNode() != null);
         Label     elseLabel = getNewLabel();
         Variable  result    = createTemporaryVariable();
 
         List<Label> labels = new ArrayList<>();
         Map<Label, Node> bodies = new HashMap<>();
 
         // build each "when"
         for (Node aCase : caseNode.getCases().children()) {
             WhenNode whenNode = (WhenNode)aCase;
             Label bodyLabel = getNewLabel();
 
             Variable eqqResult = createTemporaryVariable();
             labels.add(bodyLabel);
             Operand v1, v2;
             if (whenNode.getExpressionNodes() instanceof ListNode
                     // DNode produces a proper result, so we don't want the special ListNode handling below
                     // FIXME: This is obviously gross, and we need a better way to filter out non-expression ListNode here
                     // See GH #2423
                     && !(whenNode.getExpressionNodes() instanceof DNode)) {
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
                     v1 = build(whenNode.getExpressionNodes());
                     v2 = manager.getTrue();
                 } else {
                     v1 = value;
                     v2 = build(whenNode.getExpressionNodes());
                 }
             } else {
                 Operand expression = buildWithOrder(whenNode.getExpressionNodes(), whenNode.containsVariableAssignment());
                 Node exprNodes = whenNode.getExpressionNodes();
                 boolean needsSplat = exprNodes instanceof ArgsPushNode || exprNodes instanceof SplatNode || exprNodes instanceof ArgsCatNode;
 
                 addInstr(new EQQInstr(eqqResult, expression, value, needsSplat));
                 v1 = eqqResult;
                 v2 = manager.getTrue();
             }
             addInstr(BEQInstr.create(v1, v2, bodyLabel));
 
             // SSS FIXME: This doesn't preserve original order of when clauses.  We could consider
             // preserving the order (or maybe not, since we would have to sort the constants first
             // in any case) for outputting jump tables in certain situations.
             //
             // add body to map for emitting later
             bodies.put(bodyLabel, whenNode.getBodyNode());
         }
 
         // Jump to else in case nothing matches!
         addInstr(new JumpInstr(elseLabel));
 
         // Build "else" if it exists
         if (hasElse) {
             labels.add(elseLabel);
             bodies.put(elseLabel, caseNode.getElseNode());
         }
 
         // Now, emit bodies while preserving when clauses order
         for (Label whenLabel: labels) {
             addInstr(new LabelInstr(whenLabel));
             Operand bodyValue = build(bodies.get(whenLabel));
             // bodyValue can be null if the body ends with a return!
             if (bodyValue != null) {
                 // SSS FIXME: Do local optimization of break results (followed by a copy & jump) to short-circuit the jump right away
                 // rather than wait to do it during an optimization pass when a dead jump needs to be removed.  For this, you have
                 // to look at what the last generated instruction was.
                 addInstr(new CopyInstr(result, bodyValue));
                 addInstr(new JumpInstr(endLabel));
             }
         }
 
         if (!hasElse) {
             addInstr(new LabelInstr(elseLabel));
             addInstr(new CopyInstr(result, manager.getNil()));
             addInstr(new JumpInstr(endLabel));
         }
 
         // Close it out
         addInstr(new LabelInstr(endLabel));
 
         return result;
     }
 
     private Operand buildFixnumCase(CaseNode caseNode) {
         Map<Integer, Label> jumpTable = new HashMap<>();
         Map<Node, Label> nodeBodies = new HashMap<>();
 
         // gather fixnum-when bodies or bail
         for (Node aCase : caseNode.getCases().children()) {
             WhenNode whenNode = (WhenNode) aCase;
             Label bodyLabel = getNewLabel();
 
             FixnumNode expr = (FixnumNode) whenNode.getExpressionNodes();
             long exprLong = expr.getValue();
             if (exprLong > Integer.MAX_VALUE) throw new NotCompilableException("optimized fixnum case has long-ranged when at " + caseNode.getPosition());
 
             if (jumpTable.get((int) exprLong) == null) {
                 jumpTable.put((int) exprLong, bodyLabel);
             }
 
             nodeBodies.put(whenNode, bodyLabel);
         }
 
         // sort the jump table
         Map.Entry<Integer, Label>[] jumpEntries = jumpTable.entrySet().toArray(new Map.Entry[jumpTable.size()]);
         Arrays.sort(jumpEntries, new Comparator<Map.Entry<Integer, Label>>() {
             @Override
             public int compare(Map.Entry<Integer, Label> o1, Map.Entry<Integer, Label> o2) {
                 return Integer.compare(o1.getKey(), o2.getKey());
             }
         });
 
         // build a switch
         int[] jumps = new int[jumpTable.size()];
         Label[] targets = new Label[jumps.length];
         int i = 0;
         for (Map.Entry<Integer, Label> jumpEntry : jumpEntries) {
             jumps[i] = jumpEntry.getKey();
             targets[i] = jumpEntry.getValue();
             i++;
         }
 
         // get the incoming case value
         Operand value = build(caseNode.getCaseNode());
 
         Label     eqqPath   = getNewLabel();
         Label     endLabel  = getNewLabel();
         boolean   hasElse   = (caseNode.getElseNode() != null);
         Label     elseLabel = getNewLabel();
         Variable  result    = createTemporaryVariable();
 
         // insert fast switch with fallback to eqq
         addInstr(new BSwitchInstr(jumps, value, eqqPath, targets, elseLabel));
         addInstr(new LabelInstr(eqqPath));
 
         List<Label> labels = new ArrayList<>();
         Map<Label, Node> bodies = new HashMap<>();
 
         // build each "when"
         for (Node aCase : caseNode.getCases().children()) {
             WhenNode whenNode = (WhenNode)aCase;
             Label bodyLabel = nodeBodies.get(whenNode);
             if (bodyLabel == null) bodyLabel = getNewLabel();
 
             Variable eqqResult = createTemporaryVariable();
             labels.add(bodyLabel);
             Operand v1, v2;
             if (whenNode.getExpressionNodes() instanceof ListNode
                     // DNode produces a proper result, so we don't want the special ListNode handling below
                     // FIXME: This is obviously gross, and we need a better way to filter out non-expression ListNode here
                     // See GH #2423
                     && !(whenNode.getExpressionNodes() instanceof DNode)) {
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
                     v1 = build(whenNode.getExpressionNodes());
                     v2 = manager.getTrue();
                 } else {
                     v1 = value;
                     v2 = build(whenNode.getExpressionNodes());
                 }
             } else {
                 Operand expression = build(whenNode.getExpressionNodes());
 
                 // use frozen string for direct literal strings in `when`
                 if (expression instanceof StringLiteral) {
                     expression = ((StringLiteral) expression).frozenString;
                 }
 
                 addInstr(new EQQInstr(eqqResult, expression, value, true));
                 v1 = eqqResult;
                 v2 = manager.getTrue();
             }
             addInstr(BEQInstr.create(v1, v2, bodyLabel));
 
             // SSS FIXME: This doesn't preserve original order of when clauses.  We could consider
             // preserving the order (or maybe not, since we would have to sort the constants first
             // in any case) for outputting jump tables in certain situations.
             //
             // add body to map for emitting later
             bodies.put(bodyLabel, whenNode.getBodyNode());
         }
 
         // Jump to else in case nothing matches!
         addInstr(new JumpInstr(elseLabel));
 
         // Build "else" if it exists
         if (hasElse) {
             labels.add(elseLabel);
             bodies.put(elseLabel, caseNode.getElseNode());
         }
 
         // Now, emit bodies while preserving when clauses order
         for (Label whenLabel: labels) {
             addInstr(new LabelInstr(whenLabel));
             Operand bodyValue = build(bodies.get(whenLabel));
             // bodyValue can be null if the body ends with a return!
             if (bodyValue != null) {
                 // SSS FIXME: Do local optimization of break results (followed by a copy & jump) to short-circuit the jump right away
                 // rather than wait to do it during an optimization pass when a dead jump needs to be removed.  For this, you have
                 // to look at what the last generated instruction was.
                 addInstr(new CopyInstr(result, bodyValue));
                 addInstr(new JumpInstr(endLabel));
             }
         }
 
         if (!hasElse) {
             addInstr(new LabelInstr(elseLabel));
             addInstr(new CopyInstr(result, manager.getNil()));
             addInstr(new JumpInstr(endLabel));
         }
 
         // Close it out
         addInstr(new LabelInstr(endLabel));
 
         return result;
     }
 
     /**
      * Build a new class and add it to the current scope (s).
      */
     public Operand buildClass(ClassNode classNode) {
         Node superNode = classNode.getSuperNode();
         Colon3Node cpath = classNode.getCPath();
         Operand superClass = (superNode == null) ? null : build(superNode);
         String className = cpath.getName();
         Operand container = getContainerFromCPath(cpath);
         IRClassBody body = new IRClassBody(manager, scope, className, classNode.getLine(), classNode.getScope());
         Variable classVar = addResultInstr(new DefineClassInstr(createTemporaryVariable(), body, container, superClass));
 
         Variable processBodyResult = addResultInstr(new ProcessModuleBodyInstr(createTemporaryVariable(), classVar, NullBlock.INSTANCE));
         newIRBuilder(manager, body).buildModuleOrClassBody(classNode.getBodyNode(), classNode.getLine());
         return processBodyResult;
     }
 
     // class Foo; class << self; end; end
     // Here, the class << self declaration is in Foo's body.
     // Foo is the class in whose context this is being defined.
     public Operand buildSClass(SClassNode sclassNode) {
         Operand receiver = build(sclassNode.getReceiverNode());
         IRModuleBody body = new IRMetaClassBody(manager, scope, manager.getMetaClassName(), sclassNode.getLine(), sclassNode.getScope());
         Variable sClassVar = addResultInstr(new DefineMetaClassInstr(createTemporaryVariable(), receiver, body));
 
         // sclass bodies inherit the block of their containing method
         Variable processBodyResult = addResultInstr(new ProcessModuleBodyInstr(createTemporaryVariable(), sClassVar, scope.getYieldClosureVariable()));
         newIRBuilder(manager, body).buildModuleOrClassBody(sclassNode.getBodyNode(), sclassNode.getLine());
         return processBodyResult;
     }
 
     // @@c
     public Operand buildClassVar(ClassVarNode node) {
         Variable ret = createTemporaryVariable();
         addInstr(new GetClassVariableInstr(ret, classVarDefinitionContainer(), node.getName()));
         return ret;
     }
 
     // Add the specified result instruction to the scope and return its result variable.
     private Variable addResultInstr(ResultInstr instr) {
         addInstr((Instr) instr);
 
         return instr.getResult();
     }
 
     // ClassVarAsgn node is assignment within a method/closure scope
     //
     // def foo
     //   @@c = 1
     // end
     public Operand buildClassVarAsgn(final ClassVarAsgnNode classVarAsgnNode) {
         Operand val = build(classVarAsgnNode.getValueNode());
         addInstr(new PutClassVariableInstr(classVarDefinitionContainer(), classVarAsgnNode.getName(), val));
         return val;
     }
 
     // ClassVarDecl node is assignment outside method/closure scope (top-level, class, module)
     //
     // class C
     //   @@c = 1
     // end
     public Operand buildClassVarDecl(final ClassVarDeclNode classVarDeclNode) {
         Operand val = build(classVarDeclNode.getValueNode());
         addInstr(new PutClassVariableInstr(classVarDeclarationContainer(), classVarDeclNode.getName(), val));
         return val;
     }
 
     public Operand classVarDeclarationContainer() {
         return classVarContainer(true);
     }
 
     public Operand classVarDefinitionContainer() {
         return classVarContainer(false);
     }
 
     // SSS FIXME: This feels a little ugly.  Is there a better way of representing this?
     public Operand classVarContainer(boolean declContext) {
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
         IRScope cvarScope = scope;
         while (cvarScope != null && !(cvarScope instanceof IREvalScript) && !cvarScope.isNonSingletonClassBody()) {
             // For loops don't get their own static scope
             if (!(cvarScope instanceof IRFor)) {
                 n++;
             }
             cvarScope = cvarScope.getLexicalParent();
         }
 
         if (cvarScope != null && cvarScope.isNonSingletonClassBody()) {
             return ScopeModule.ModuleFor(n);
         } else {
             return addResultInstr(new GetClassVarContainerModuleInstr(createTemporaryVariable(),
                     scope.getCurrentScopeVariable(), declContext ? null : buildSelf()));
         }
     }
 
     public Operand buildConstDecl(ConstDeclNode node) {
         return buildConstDeclAssignment(node, build(node.getValueNode()));
     }
 
     private Operand findContainerModule() {
         int nearestModuleBodyDepth = scope.getNearestModuleReferencingScopeDepth();
         return (nearestModuleBodyDepth == -1) ? scope.getCurrentModuleVariable() : ScopeModule.ModuleFor(nearestModuleBodyDepth);
     }
 
     private Operand startingSearchScope() {
         int nearestModuleBodyDepth = scope.getNearestModuleReferencingScopeDepth();
         return nearestModuleBodyDepth == -1 ? scope.getCurrentScopeVariable() : CurrentScope.ScopeFor(nearestModuleBodyDepth);
     }
 
     public Operand buildConstDeclAssignment(ConstDeclNode constDeclNode, Operand value) {
         Node constNode = constDeclNode.getConstNode();
 
         if (constNode == null) {
             return putConstant(constDeclNode.getName(), value);
         } else if (constNode.getNodeType() == NodeType.COLON2NODE) {
             return putConstant((Colon2Node) constNode, value);
         } else { // colon3, assign in Object
             return putConstant((Colon3Node) constNode, value);
         }
     }
 
     private Operand putConstant(String name, Operand value) {
         addInstr(new PutConstInstr(findContainerModule(), name, value));
 
         return value;
     }
 
     private Operand putConstant(Colon3Node node, Operand value) {
         addInstr(new PutConstInstr(new ObjectClass(), node.getName(), value));
 
         return value;
     }
 
     private Operand putConstant(Colon2Node node, Operand value) {
         addInstr(new PutConstInstr(build(node.getLeftNode()), node.getName(), value));
 
         return value;
     }
 
     private Operand putConstantAssignment(OpAsgnConstDeclNode node, Operand value) {
         Node constNode = node.getFirstNode();
 
         if (constNode instanceof Colon2Node) return putConstant((Colon2Node) constNode, value);
 
         return putConstant((Colon3Node) constNode, value);
     }
 
     private Operand searchModuleForConst(Operand startingModule, String name) {
         return addResultInstr(new SearchModuleForConstInstr(createTemporaryVariable(), startingModule, name, true));
     }
 
     private Operand searchConst(String name) {
         return addResultInstr(new SearchConstInstr(createTemporaryVariable(), name, startingSearchScope(), false));
     }
 
     public Operand buildColon2(final Colon2Node colon2) {
         Node lhs = colon2.getLeftNode();
 
         // Colon2ImplicitNode - (module|class) Foo.  Weird, but it is a wrinkle of AST inheritance.
         if (lhs == null) return searchConst(colon2.getName());
 
         // Colon2ConstNode (Left::name)
         return searchModuleForConst(build(lhs), colon2.getName());
     }
 
     public Operand buildColon3(Colon3Node node) {
         return searchModuleForConst(new ObjectClass(), node.getName());
     }
 
     public Operand buildComplex(ComplexNode node) {
         return new Complex((ImmutableLiteral) build(node.getNumber()));
     }
 
     interface CodeBlock {
         public Operand run();
     }
 
     private Operand protectCodeWithRescue(CodeBlock protectedCode, CodeBlock rescueBlock) {
         // This effectively mimics a begin-rescue-end code block
         // Except this catches all exceptions raised by the protected code
 
         Variable rv = createTemporaryVariable();
         Label rBeginLabel = getNewLabel();
         Label rEndLabel   = getNewLabel();
         Label rescueLabel = getNewLabel();
 
         // Protected region code
         addInstr(new LabelInstr(rBeginLabel));
         addInstr(new ExceptionRegionStartMarkerInstr(rescueLabel));
         Object v1 = protectedCode.run(); // YIELD: Run the protected code block
         addInstr(new CopyInstr(rv, (Operand)v1));
         addInstr(new JumpInstr(rEndLabel, true));
         addInstr(new ExceptionRegionEndMarkerInstr());
 
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
         Label caughtLabel = getNewLabel();
         Variable exc = createTemporaryVariable();
         Variable excType = createTemporaryVariable();
 
         // Receive 'exc' and verify that 'exc' is of ruby-type 'Exception'
         addInstr(new LabelInstr(rescueLabel));
         addInstr(new ReceiveRubyExceptionInstr(exc));
         addInstr(new InheritanceSearchConstInstr(excType, new ObjectClass(), "Exception"));
         outputExceptionCheck(excType, exc, caughtLabel);
 
         // Fall-through when the exc !== Exception; rethrow 'exc'
         addInstr(new ThrowExceptionInstr(exc));
 
         // exc === Exception; Run the rescue block
         addInstr(new LabelInstr(caughtLabel));
         Object v2 = rescueBlock.run(); // YIELD: Run the protected code block
         if (v2 != null) addInstr(new CopyInstr(rv, manager.getNil()));
 
         // End
         addInstr(new LabelInstr(rEndLabel));
 
         return rv;
     }
 
     public Operand buildGetDefinition(Node node) {
         switch (node.getNodeType()) {
         case CLASSVARASGNNODE: case CLASSVARDECLNODE: case CONSTDECLNODE:
         case DASGNNODE: case GLOBALASGNNODE: case LOCALASGNNODE:
         case MULTIPLEASGNNODE: case OPASGNNODE: case OPASGNANDNODE: case OPASGNORNODE:
         case OPELEMENTASGNNODE: case INSTASGNNODE:
             return new FrozenString("assignment");
         case ORNODE: case ANDNODE:
             return new FrozenString("expression");
         case FALSENODE:
             return new FrozenString("false");
         case LOCALVARNODE: case DVARNODE:
             return new FrozenString("local-variable");
         case MATCH2NODE: case MATCH3NODE:
             return new FrozenString("method");
         case NILNODE:
             return new FrozenString("nil");
         case SELFNODE:
             return new FrozenString("self");
         case TRUENODE:
             return new FrozenString("true");
         case DREGEXPNODE: case DSTRNODE: {
             return new FrozenString("expression");
         }
         case ARRAYNODE: { // If all elts of array are defined the array is as well
             ArrayNode array = (ArrayNode) node;
             Label undefLabel = getNewLabel();
             Label doneLabel = getNewLabel();
 
             Variable tmpVar = createTemporaryVariable();
             for (Node elt: array.children()) {
                 Operand result = buildGetDefinition(elt);
 
                 addInstr(BEQInstr.create(result, manager.getNil(), undefLabel));
             }
 
             addInstr(new CopyInstr(tmpVar, new FrozenString("expression")));
             addInstr(new JumpInstr(doneLabel));
             addInstr(new LabelInstr(undefLabel));
             addInstr(new CopyInstr(tmpVar, manager.getNil()));
             addInstr(new LabelInstr(doneLabel));
 
             return tmpVar;
         }
         case BACKREFNODE:
             return addResultInstr(new RuntimeHelperCall(createTemporaryVariable(), IS_DEFINED_BACKREF, Operand.EMPTY_ARRAY));
         case GLOBALVARNODE:
             return addResultInstr(new RuntimeHelperCall(createTemporaryVariable(), IS_DEFINED_GLOBAL,
                     new Operand[] { new FrozenString(((GlobalVarNode) node).getName()) }));
         case NTHREFNODE: {
             return addResultInstr(new RuntimeHelperCall(createTemporaryVariable(), IS_DEFINED_NTH_REF,
                     new Operand[] { new Fixnum(((NthRefNode) node).getMatchNumber()) }));
         }
         case INSTVARNODE:
             return addResultInstr(new RuntimeHelperCall(createTemporaryVariable(), IS_DEFINED_INSTANCE_VAR,
                     new Operand[] { buildSelf(), new FrozenString(((InstVarNode) node).getName()) }));
         case CLASSVARNODE:
             return addResultInstr(new RuntimeHelperCall(createTemporaryVariable(), IS_DEFINED_CLASS_VAR,
                     new Operand[]{classVarDefinitionContainer(), new FrozenString(((ClassVarNode) node).getName())}));
         case SUPERNODE: {
             Label undefLabel = getNewLabel();
             Variable tmpVar  = addResultInstr(new RuntimeHelperCall(createTemporaryVariable(), IS_DEFINED_SUPER,
                     new Operand[] { buildSelf() }));
             addInstr(BEQInstr.create(tmpVar, manager.getNil(), undefLabel));
             Operand superDefnVal = buildGetArgumentDefinition(((SuperNode) node).getArgsNode(), "super");
             return buildDefnCheckIfThenPaths(undefLabel, superDefnVal);
         }
         case VCALLNODE:
             return addResultInstr(new RuntimeHelperCall(createTemporaryVariable(), IS_DEFINED_METHOD,
                     new Operand[] { buildSelf(), new FrozenString(((VCallNode) node).getName()), manager.getFalse()}));
         case YIELDNODE:
             return buildDefinitionCheck(new BlockGivenInstr(createTemporaryVariable(), scope.getYieldClosureVariable()), "yield");
         case ZSUPERNODE:
             return addResultInstr(new RuntimeHelperCall(createTemporaryVariable(), IS_DEFINED_SUPER,
                     new Operand[] { buildSelf() } ));
         case CONSTNODE: {
             Label defLabel = getNewLabel();
             Label doneLabel = getNewLabel();
             Variable tmpVar  = createTemporaryVariable();
             String constName = ((ConstNode) node).getName();
             addInstr(new LexicalSearchConstInstr(tmpVar, startingSearchScope(), constName));
             addInstr(BNEInstr.create(defLabel, tmpVar, UndefinedValue.UNDEFINED));
             addInstr(new InheritanceSearchConstInstr(tmpVar, findContainerModule(), constName)); // SSS FIXME: should this be the current-module var or something else?
             addInstr(BNEInstr.create(defLabel, tmpVar, UndefinedValue.UNDEFINED));
             addInstr(new CopyInstr(tmpVar, manager.getNil()));
             addInstr(new JumpInstr(doneLabel));
             addInstr(new LabelInstr(defLabel));
             addInstr(new CopyInstr(tmpVar, new FrozenString("constant")));
             addInstr(new LabelInstr(doneLabel));
             return tmpVar;
         }
         case COLON3NODE: case COLON2NODE: {
             // SSS FIXME: Is there a reason to do this all with low-level IR?
             // Can't this all be folded into a Java method that would be part
             // of the runtime library, which then can be used by buildDefinitionCheck method above?
             // This runtime library would be used both by the interpreter & the compiled code!
 
             final Colon3Node colon = (Colon3Node) node;
             final String name = colon.getName();
             final Variable errInfo = createTemporaryVariable();
 
             // store previous exception for restoration if we rescue something
             addInstr(new GetErrorInfoInstr(errInfo));
 
             CodeBlock protectedCode = new CodeBlock() {
                 public Operand run() {
                     if (!(colon instanceof Colon2Node)) { // colon3 (weird inheritance)
                         return addResultInstr(new RuntimeHelperCall(createTemporaryVariable(),
                                 IS_DEFINED_CONSTANT_OR_METHOD, new Operand[] {new ObjectClass(), new FrozenString(name)}));
                     }
 
                     Label bad = getNewLabel();
                     Label done = getNewLabel();
                     Variable result = createTemporaryVariable();
                     Operand test = buildGetDefinition(((Colon2Node) colon).getLeftNode());
                     addInstr(BEQInstr.create(test, manager.getNil(), bad));
                     Operand lhs = build(((Colon2Node) colon).getLeftNode());
                     addInstr(new RuntimeHelperCall(result, IS_DEFINED_CONSTANT_OR_METHOD, new Operand[] {lhs, new FrozenString(name)}));
                     addInstr(new JumpInstr(done));
                     addInstr(new LabelInstr(bad));
                     addInstr(new CopyInstr(result, manager.getNil()));
                     addInstr(new LabelInstr(done));
 
                     return result;
                 }
             };
 
             // rescue block
             CodeBlock rescueBlock = new CodeBlock() {
                  public Operand run() {
                  // Nothing to do -- ignore the exception, and restore stashed error info!
                  addInstr(new RestoreErrorInfoInstr(errInfo));
                  return manager.getNil();
                  }
             };
 
             // Try verifying definition, and if we get an JumpException exception, process it with the rescue block above
             return protectCodeWithRescue(protectedCode, rescueBlock);
         }
         case FCALLNODE: {
             /* ------------------------------------------------------------------
              * Generate IR for:
              *    r = self/receiver
              *    mc = r.metaclass
              *    return mc.methodBound(meth) ? buildGetArgumentDefn(..) : false
              * ----------------------------------------------------------------- */
             Label undefLabel = getNewLabel();
             Variable tmpVar = addResultInstr(new RuntimeHelperCall(createTemporaryVariable(), IS_DEFINED_METHOD,
                     new Operand[]{buildSelf(), new FrozenString(((FCallNode) node).getName()), manager.getFalse()}));
             addInstr(BEQInstr.create(tmpVar, manager.getNil(), undefLabel));
             Operand argsCheckDefn = buildGetArgumentDefinition(((FCallNode) node).getArgsNode(), "method");
             return buildDefnCheckIfThenPaths(undefLabel, argsCheckDefn);
         }
         case CALLNODE: {
             final CallNode callNode = (CallNode) node;
 
             // protected main block
             CodeBlock protectedCode = new CodeBlock() {
                 public Operand run() {
                     final Label undefLabel = getNewLabel();
                     Operand receiverDefn = buildGetDefinition(callNode.getReceiverNode());
                     addInstr(BEQInstr.create(receiverDefn, manager.getNil(), undefLabel));
                     Variable tmpVar = createTemporaryVariable();
                     addInstr(new RuntimeHelperCall(tmpVar, IS_DEFINED_CALL,
                             new Operand[]{build(callNode.getReceiverNode()), new StringLiteral(callNode.getName())}));
                     return buildDefnCheckIfThenPaths(undefLabel, tmpVar);
                 }
             };
 
             // rescue block
             CodeBlock rescueBlock = new CodeBlock() {
                 public Operand run() { return manager.getNil(); } // Nothing to do if we got an exception
             };
 
             // Try verifying definition, and if we get an exception, throw it out, and return nil
             return protectCodeWithRescue(protectedCode, rescueBlock);
         }
         case ATTRASSIGNNODE: {
             final AttrAssignNode attrAssign = (AttrAssignNode) node;
 
             // protected main block
             CodeBlock protectedCode = new CodeBlock() {
                 public Operand run() {
                     final Label  undefLabel = getNewLabel();
                     Operand receiverDefn = buildGetDefinition(attrAssign.getReceiverNode());
                     addInstr(BEQInstr.create(receiverDefn, manager.getNil(), undefLabel));
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
                     Variable tmpVar     = createTemporaryVariable();
                     Operand  receiver   = build(attrAssign.getReceiverNode());
                     addInstr(new RuntimeHelperCall(tmpVar, IS_DEFINED_METHOD,
                             new Operand[] { receiver, new StringLiteral(attrAssign.getName()), manager.getTrue() }));
                     addInstr(BEQInstr.create(tmpVar, manager.getNil(), undefLabel));
                     Operand argsCheckDefn = buildGetArgumentDefinition(attrAssign.getArgsNode(), "assignment");
                     return buildDefnCheckIfThenPaths(undefLabel, argsCheckDefn);
                 }
             };
 
             // rescue block
             CodeBlock rescueBlock = new CodeBlock() {
                 public Operand run() { return manager.getNil(); } // Nothing to do if we got an exception
             };
 
             // Try verifying definition, and if we get an JumpException exception, process it with the rescue block above
             return protectCodeWithRescue(protectedCode, rescueBlock);
         }
         default:
             return new FrozenString("expression");
         }
     }
 
     protected Variable buildDefnCheckIfThenPaths(Label undefLabel, Operand defVal) {
         Label defLabel = getNewLabel();
         Variable tmpVar = getValueInTemporaryVariable(defVal);
         addInstr(new JumpInstr(defLabel));
         addInstr(new LabelInstr(undefLabel));
         addInstr(new CopyInstr(tmpVar, manager.getNil()));
         addInstr(new LabelInstr(defLabel));
         return tmpVar;
     }
 
     protected Variable buildDefinitionCheck(ResultInstr definedInstr, String definedReturnValue) {
         Label undefLabel = getNewLabel();
         addInstr((Instr) definedInstr);
         addInstr(BEQInstr.create(definedInstr.getResult(), manager.getFalse(), undefLabel));
         return buildDefnCheckIfThenPaths(undefLabel, new FrozenString(definedReturnValue));
     }
 
     public Operand buildGetArgumentDefinition(final Node node, String type) {
         if (node == null) return new StringLiteral(type);
 
         Operand rv = new FrozenString(type);
         boolean failPathReqd = false;
         Label failLabel = getNewLabel();
         if (node instanceof ArrayNode) {
             for (int i = 0; i < ((ArrayNode) node).size(); i++) {
                 Node iterNode = ((ArrayNode) node).get(i);
                 Operand def = buildGetDefinition(iterNode);
                 if (def == manager.getNil()) { // Optimization!
                     rv = manager.getNil();
                     break;
                 } else if (!def.hasKnownValue()) { // Optimization!
                     failPathReqd = true;
                     addInstr(BEQInstr.create(def, manager.getNil(), failLabel));
                 }
             }
         } else {
             Operand def = buildGetDefinition(node);
             if (def == manager.getNil()) { // Optimization!
                 rv = manager.getNil();
             } else if (!def.hasKnownValue()) { // Optimization!
                 failPathReqd = true;
                 addInstr(BEQInstr.create(def, manager.getNil(), failLabel));
             }
         }
 
         // Optimization!
         return failPathReqd ? buildDefnCheckIfThenPaths(failLabel, rv) : rv;
 
     }
 
     public Operand buildDAsgn(final DAsgnNode dasgnNode) {
         // SSS: Looks like we receive the arg in buildBlockArgsAssignment via the IterNode
         // We won't get here for argument receives!  So, buildDasgn is called for
         // assignments to block variables within a block.  As far as the IR is concerned,
         // this is just a simple copy
         int depth = dasgnNode.getDepth();
         Variable arg = getLocalVariable(dasgnNode.getName(), depth);
         Operand  value = build(dasgnNode.getValueNode());
 
         // no use copying a variable to itself
         if (arg == value) return value;
 
         addInstr(new CopyInstr(arg, value));
 
         return value;
 
         // IMPORTANT: The return value of this method is value, not arg!
         //
         // Consider this Ruby code: foo((a = 1), (a = 2))
         //
         // If we return 'value' this will get translated to:
         //    a = 1
         //    a = 2
         //    call("foo", [1,2]) <---- CORRECT
         //
         // If we return 'arg' this will get translated to:
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
 
     // Called by defineMethod but called on a new builder so things like ensure block info recording
     // do not get confused.
     protected InterpreterContext defineMethodInner(DefNode defNode, IRScope parent, boolean needsCodeCoverage) {
         this.needsCodeCoverage = needsCodeCoverage;
 
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // Explicit line number here because we need a line number for trace before we process any nodes
             addInstr(manager.newLineNumber(scope.getLineNumber()));
             addInstr(new TraceInstr(RubyEvent.CALL, getName(), getFileName(), scope.getLineNumber()));
         }
 
         prepareImplicitState();                                    // recv_self, add frame block, etc)
 
         // These instructions need to be toward the top of the method because they may both be needed for processing
         // optional arguments as in def foo(a = Object).
         // Set %current_scope = <current-scope>
         // Set %current_module = isInstanceMethod ? %self.metaclass : %self
         int nearestScopeDepth = parent.getNearestModuleReferencingScopeDepth();
         addInstr(new CopyInstr(scope.getCurrentScopeVariable(), CurrentScope.ScopeFor(nearestScopeDepth == -1 ? 1 : nearestScopeDepth)));
         addInstr(new CopyInstr(scope.getCurrentModuleVariable(), ScopeModule.ModuleFor(nearestScopeDepth == -1 ? 1 : nearestScopeDepth)));
 
         // Build IR for arguments (including the block arg)
         receiveMethodArgs(defNode.getArgsNode());
 
         // Build IR for body
         Operand rv = build(defNode.getBodyNode());
 
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             addInstr(new LineNumberInstr(defNode.getEndLine()));
             addInstr(new TraceInstr(RubyEvent.RETURN, getName(), getFileName(), defNode.getEndLine()));
         }
 
         if (rv != null) addInstr(new ReturnInstr(rv));
 
         scope.computeScopeFlagsEarly(instructions);
         // If the method can receive non-local returns
         if (scope.canReceiveNonlocalReturns()) handleNonlocalReturnInMethod();
 
         ArgumentDescriptor[] argDesc;
         if (argumentDescriptions == null) {
             argDesc = NO_ARG_DESCS;
         } else {
             argDesc = new ArgumentDescriptor[argumentDescriptions.size() / 2];
             for (int i = 0; i < argumentDescriptions.size();) {
                 argDesc[i / 2] = new ArgumentDescriptor(
                         ArgumentType.valueOf(argumentDescriptions.get(i++)),
                         argumentDescriptions.get(i++));
             }
         }
 
         ((IRMethod) scope).setArgumentDescriptors(argDesc);
 
         return scope.allocateInterpreterContext(instructions);
     }
 
     static final ArgumentDescriptor[] NO_ARG_DESCS = new ArgumentDescriptor[0];
 
     private IRMethod defineNewMethod(MethodDefNode defNode, boolean isInstanceMethod) {
         return new IRMethod(manager, scope, defNode, defNode.getName(), isInstanceMethod, defNode.getLine(), defNode.getScope(), needsCodeCoverage());
 
         //return newIRBuilder(manager).defineMethodInner(defNode, method, parent);
     }
 
     public Operand buildDefn(MethodDefNode node) { // Instance method
         IRMethod method = defineNewMethod(node, true);
         addInstr(new DefineInstanceMethodInstr(method));
         // FIXME: Method name should save encoding
         return new Symbol(method.getName(), ASCIIEncoding.INSTANCE);
     }
 
     public Operand buildDefs(DefsNode node) { // Class method
         Operand container =  build(node.getReceiverNode());
         IRMethod method = defineNewMethod(node, false);
         addInstr(new DefineClassMethodInstr(container, method));
         // FIXME: Method name should save encoding
         return new Symbol(method.getName(), ASCIIEncoding.INSTANCE);
     }
 
     protected LocalVariable getArgVariable(String name, int depth) {
         // For non-loops, this name will override any name that exists in outer scopes
         return scope instanceof IRFor ? getLocalVariable(name, depth) : getNewLocalVariable(name, 0);
     }
 
-    private void addArgReceiveInstr(Variable v, int argIndex, boolean post, int numPreReqd, int numPostRead) {
+    private void addArgReceiveInstr(Variable v, int argIndex, Signature signature) {
+        boolean post = signature != null;
+
         if (post) {
-            addInstr(new ReceivePostReqdArgInstr(v, argIndex, numPreReqd, numPostRead));
+            addInstr(new ReceivePostReqdArgInstr(v, argIndex, signature.pre(), signature.opt(), signature.hasRest(), signature.post()));
         } else {
             addInstr(new ReceivePreReqdArgInstr(v, argIndex));
         }
     }
 
     /* '_' can be seen as a variable only by its first assignment as a local variable.  For any additional
      * '_' we create temporary variables in the case the scope has a zsuper in it.  If so, then the zsuper
      * call will slurp those temps up as it's parameters so it can properly set up the call.
      */
     private Variable argumentResult(String name) {
         boolean isUnderscore = name.equals("_");
 
         if (isUnderscore && underscoreVariableSeen) {
             return createTemporaryVariable();
         } else {
             if (isUnderscore) underscoreVariableSeen = true;
             return getNewLocalVariable(name, 0);
         }
     }
 
-    public void receiveRequiredArg(Node node, int argIndex, boolean post, int numPreReqd, int numPostRead) {
+    public void receiveRequiredArg(Node node, int argIndex, Signature signature) {
         switch (node.getNodeType()) {
             case ARGUMENTNODE: {
                 String argName = ((ArgumentNode)node).getName();
 
                 if (scope instanceof IRMethod) addArgumentDescription(ArgumentType.req, argName);
 
-                addArgReceiveInstr(argumentResult(argName), argIndex, post, numPreReqd, numPostRead);
+                addArgReceiveInstr(argumentResult(argName), argIndex, signature);
                 break;
             }
             case MULTIPLEASGNNODE: {
                 MultipleAsgnNode childNode = (MultipleAsgnNode) node;
                 Variable v = createTemporaryVariable();
-                addArgReceiveInstr(v, argIndex, post, numPreReqd, numPostRead);
+                addArgReceiveInstr(v, argIndex, signature);
                 if (scope instanceof IRMethod) addArgumentDescription(ArgumentType.anonreq, null);
                 Variable tmp = createTemporaryVariable();
                 addInstr(new ToAryInstr(tmp, v));
                 buildMultipleAsgn19Assignment(childNode, tmp, null);
                 break;
             }
             default: throw new NotCompilableException("Can't build assignment node: " + node);
         }
     }
 
     protected void receiveNonBlockArgs(final ArgsNode argsNode) {
         Signature signature = scope.getStaticScope().getSignature();
         KeywordRestArgNode keyRest = argsNode.getKeyRest();
 
         // For closures, we don't need the check arity call
         if (scope instanceof IRMethod) {
             // Expensive to do this explicitly?  But, two advantages:
             // (a) on inlining, we'll be able to get rid of these checks in almost every case.
             // (b) compiler to bytecode will anyway generate this and this is explicit.
             // For now, we are going explicit instruction route.
             // But later, perhaps can make this implicit in the method setup preamble?
 
             addInstr(new CheckArityInstr(signature.required(), signature.opt(), signature.hasRest(), argsNode.hasKwargs(),
                     keyRest == null ? -1 : keyRest.getIndex()));
         } else if (scope instanceof IRClosure && argsNode.hasKwargs()) {
             // FIXME: This is added to check for kwargs correctness but bypass regular correctness.
             // Any other arity checking currently happens within Java code somewhere (RubyProc.call?)
             addInstr(new CheckArityInstr(signature.required(), signature.opt(), signature.hasRest(), argsNode.hasKwargs(),
                     keyRest == null ? -1 : keyRest.getIndex()));
         }
 
         // Other args begin at index 0
         int argIndex = 0;
 
         // Pre(-opt and rest) required args
         Node[] args = argsNode.getArgs();
         int preCount = signature.pre();
         for (int i = 0; i < preCount; i++, argIndex++) {
-            receiveRequiredArg(args[i], argIndex, false, -1, -1);
+            receiveRequiredArg(args[i], argIndex, null);
         }
 
         // Fixup opt/rest
         int opt = signature.opt() > 0 ? signature.opt() : 0;
 
         // Now for opt args
         if (opt > 0) {
             int optIndex = argsNode.getOptArgIndex();
             for (int j = 0; j < opt; j++, argIndex++) {
                 // We fall through or jump to variableAssigned once we know we have a valid value in place.
                 Label variableAssigned = getNewLabel();
                 OptArgNode optArg = (OptArgNode)args[optIndex + j];
                 String argName = optArg.getName();
                 Variable argVar = argumentResult(argName);
                 if (scope instanceof IRMethod) addArgumentDescription(ArgumentType.opt, argName);
                 // You need at least required+j+1 incoming args for this opt arg to get an arg at all
                 addInstr(new ReceiveOptArgInstr(argVar, signature.required(), signature.pre(), j));
                 addInstr(BNEInstr.create(variableAssigned, argVar, UndefinedValue.UNDEFINED));
                 // We add this extra nil copy because we do not know if we have a circular defininition of
                 // argVar: proc { |a=a| } or proc { |a = foo(bar(a))| }.
                 addInstr(new CopyInstr(argVar, manager.getNil()));
                 // This bare build looks weird but OptArgNode is just a marker and value is either a LAsgnNode
                 // or a DAsgnNode.  So building the value will end up having a copy(var, assignment).
                 build(optArg.getValue());
                 addInstr(new LabelInstr(variableAssigned));
             }
         }
 
         // Rest arg
         if (signature.hasRest()) {
             // Consider: def foo(*); .. ; end
             // For this code, there is no argument name available from the ruby code.
             // So, we generate an implicit arg name
             String argName = argsNode.getRestArgNode().getName();
             if (scope instanceof IRMethod) {
                 addArgumentDescription(
                         argName == null || argName.length() == 0 ? ArgumentType.anonrest : ArgumentType.rest,
                         argName);
             }
             argName = (argName == null || argName.equals("")) ? "*" : argName;
 
             // You need at least required+opt+1 incoming args for the rest arg to get any args at all
             // If it is going to get something, then it should ignore required+opt args from the beginning
             // because they have been accounted for already.
             addInstr(new ReceiveRestArgInstr(argumentResult(argName), signature.required() + opt, argIndex));
         }
 
         // Post(-opt and rest) required args
         int postCount = argsNode.getPostCount();
         int postIndex = argsNode.getPostIndex();
         for (int i = 0; i < postCount; i++) {
-            receiveRequiredArg(args[postIndex + i], i, true, signature.pre(), postCount);
+            receiveRequiredArg(args[postIndex + i], i, signature);
         }
     }
 
     /**
      * Reify the implicit incoming block into a full Proc, for use as "block arg", but only if
      * a block arg is specified in this scope's arguments.
      *  @param argsNode the arguments containing the block arg, if any
      *
      */
     protected void receiveBlockArg(final ArgsNode argsNode) {
         // reify to Proc if we have a block arg
         BlockArgNode blockArg = argsNode.getBlock();
         if (blockArg != null) {
             String argName = blockArg.getName();
             Variable blockVar = argumentResult(argName);
             if (scope instanceof IRMethod) addArgumentDescription(ArgumentType.block, argName);
             Variable tmp = createTemporaryVariable();
             addInstr(new LoadImplicitClosureInstr(tmp));
             addInstr(new ReifyClosureInstr(blockVar, tmp));
         }
     }
 
     /**
      * Prepare implicit runtime state needed for typical methods to execute. This includes such things
      * as the implicit self variable and any yieldable block available to this scope.
      */
     private void prepareImplicitState() {
         // Receive self
         addInstr(manager.getReceiveSelfInstr());
 
         // used for yields; metaclass body (sclass) inherits yield var from surrounding, and accesses it as implicit
         if (scope instanceof IRMethod || scope instanceof IRMetaClassBody) {
             addInstr(new LoadImplicitClosureInstr(scope.getYieldClosureVariable()));
         } else {
             addInstr(new LoadFrameClosureInstr(scope.getYieldClosureVariable()));
         }
     }
 
     /**
      * Process all arguments specified for this scope. This includes pre args (required args
      * at the beginning of the argument list), opt args (arguments with a default value),
      * a rest arg (catch-all for argument list overflow), post args (required arguments after
      * a rest arg) and a block arg (to reify an incoming block into a Proc object.
      *  @param argsNode the args node containing the specification for the arguments
      *
      */
     public void receiveArgs(final ArgsNode argsNode) {
         // 1.9 pre, opt, rest, post args
         receiveNonBlockArgs(argsNode);
 
         // 2.0 keyword args
         Node[] args = argsNode.getArgs();
         int required = argsNode.getRequiredArgsCount();
         if (argsNode.hasKwargs()) {
             int keywordIndex = argsNode.getKeywordsIndex();
             int keywordsCount = argsNode.getKeywordCount();
             for (int i = 0; i < keywordsCount; i++) {
                 KeywordArgNode kwarg = (KeywordArgNode) args[keywordIndex + i];
                 AssignableNode kasgn = kwarg.getAssignable();
                 String argName = ((INameNode) kasgn).getName();
                 Variable av = getNewLocalVariable(argName, 0);
                 Label l = getNewLabel();
                 if (scope instanceof IRMethod) addKeyArgDesc(kasgn, argName);
                 addInstr(new ReceiveKeywordArgInstr(av, argName, required));
                 addInstr(BNEInstr.create(l, av, UndefinedValue.UNDEFINED)); // if 'av' is not undefined, we are done
 
                 // Required kwargs have no value and check_arity will throw if they are not provided.
                 if (!isRequiredKeywordArgumentValue(kasgn)) {
                     addInstr(new CopyInstr(av, buildNil())); // wipe out undefined value with nil
                     build(kasgn);
                 } else {
                     addInstr(new RaiseRequiredKeywordArgumentError(argName));
                 }
                 addInstr(new LabelInstr(l));
             }
         }
 
         // 2.0 keyword rest arg
         KeywordRestArgNode keyRest = argsNode.getKeyRest();
         if (keyRest != null) {
             String argName = keyRest.getName();
             ArgumentType type = ArgumentType.keyrest;
 
             // anonymous keyrest
             if (argName == null || argName.length() == 0) type = ArgumentType.anonkeyrest;
 
             Variable av = getNewLocalVariable(argName, 0);
             if (scope instanceof IRMethod) addArgumentDescription(type, argName);
             addInstr(new ReceiveKeywordRestArgInstr(av, required));
         }
 
         // Block arg
         receiveBlockArg(argsNode);
     }
 
     private void addKeyArgDesc(AssignableNode kasgn, String argName) {
         if (isRequiredKeywordArgumentValue(kasgn)) {
             addArgumentDescription(ArgumentType.keyreq, argName);
         } else {
             addArgumentDescription(ArgumentType.key, argName);
         }
     }
 
     private boolean isRequiredKeywordArgumentValue(AssignableNode kasgn) {
         return (kasgn.getValueNode().getNodeType()) ==  NodeType.REQUIRED_KEYWORD_ARGUMENT_VALUE;
     }
 
     // This method is called to build arguments
     public void buildArgsMasgn(Node node, Operand argsArray, boolean isMasgnRoot, int preArgsCount, int postArgsCount, int index, boolean isSplat) {
         Variable v;
         switch (node.getNodeType()) {
             case DASGNNODE: {
                 DAsgnNode dynamicAsgn = (DAsgnNode) node;
                 v = getArgVariable(dynamicAsgn.getName(), dynamicAsgn.getDepth());
                 if (isSplat) addInstr(new RestArgMultipleAsgnInstr(v, argsArray, preArgsCount, postArgsCount, index));
                 else addInstr(new ReqdArgMultipleAsgnInstr(v, argsArray, preArgsCount, postArgsCount, index));
                 break;
             }
             case LOCALASGNNODE: {
                 LocalAsgnNode localVariable = (LocalAsgnNode) node;
                 v = getArgVariable(localVariable.getName(), localVariable.getDepth());
                 if (isSplat) addInstr(new RestArgMultipleAsgnInstr(v, argsArray, preArgsCount, postArgsCount, index));
                 else addInstr(new ReqdArgMultipleAsgnInstr(v, argsArray, preArgsCount, postArgsCount, index));
                 break;
             }
             case MULTIPLEASGNNODE: {
                 MultipleAsgnNode childNode = (MultipleAsgnNode) node;
                 if (!isMasgnRoot) {
                     v = createTemporaryVariable();
                     if (isSplat) addInstr(new RestArgMultipleAsgnInstr(v, argsArray, preArgsCount, postArgsCount, index));
                     else addInstr(new ReqdArgMultipleAsgnInstr(v, argsArray, preArgsCount, postArgsCount, index));
                     Variable tmp = createTemporaryVariable();
                     addInstr(new ToAryInstr(tmp, v));
                     argsArray = tmp;
                 }
                 // Build
                 buildMultipleAsgn19Assignment(childNode, argsArray, null);
                 break;
             }
             default:
                 throw new NotCompilableException("Shouldn't get here: " + node);
         }
     }
 
     // This method is called both for regular multiple assignment as well as argument passing
     //
     // Ex: a,b,*c=v  is a regular assignment and in this case, the "values" operand will be non-null
     // Ex: { |a,b,*c| ..} is the argument passing case
     public void buildMultipleAsgn19Assignment(final MultipleAsgnNode multipleAsgnNode, Operand argsArray, Operand values) {
         final ListNode masgnPre = multipleAsgnNode.getPre();
         final List<Tuple<Node, Variable>> assigns = new ArrayList<>();
 
         // Build assignments for specific named arguments
         int i = 0;
         if (masgnPre != null) {
             for (Node an: masgnPre.children()) {
                 if (values == null) {
                     buildArgsMasgn(an, argsArray, false, -1, -1, i, false);
                 } else {
                     Variable rhsVal = createTemporaryVariable();
                     addInstr(new ReqdArgMultipleAsgnInstr(rhsVal, values, i));
                     assigns.add(new Tuple<>(an, rhsVal));
                 }
                 i++;
             }
         }
 
         // Build an assignment for a splat, if any, with the rest of the operands!
         Node restNode = multipleAsgnNode.getRest();
         int postArgsCount = multipleAsgnNode.getPostCount();
         if (restNode != null && !(restNode instanceof StarNode)) {
             if (values == null) {
                 buildArgsMasgn(restNode, argsArray, false, i, postArgsCount, 0, true); // rest of the argument array!
             } else {
                 Variable rhsVal = createTemporaryVariable();
                 addInstr(new RestArgMultipleAsgnInstr(rhsVal, values, i, postArgsCount, 0));
                 assigns.add(new Tuple<>(restNode, rhsVal)); // rest of the argument array!
             }
         }
 
         // Build assignments for rest of the operands
         final ListNode masgnPost = multipleAsgnNode.getPost();
         if (masgnPost != null) {
             int j = 0;
             for (Node an: masgnPost.children()) {
                 if (values == null) {
                     buildArgsMasgn(an, argsArray, false, i, postArgsCount, j, false);
                 } else {
                     Variable rhsVal = createTemporaryVariable();
                     addInstr(new ReqdArgMultipleAsgnInstr(rhsVal, values, i, postArgsCount, j));  // Fetch from the end
                     assigns.add(new Tuple<>(an, rhsVal));
                 }
                 j++;
             }
         }
 
         for (Tuple<Node, Variable> assign: assigns) {
             buildAssignment(assign.a, assign.b);
         }
     }
 
     private void handleBreakAndReturnsInLambdas() {
         Label rEndLabel   = getNewLabel();
         Label rescueLabel = Label.getGlobalEnsureBlockLabel();
 
         // Protect the entire body as it exists now with the global ensure block
         addInstrAtBeginning(new ExceptionRegionStartMarkerInstr(rescueLabel));
         addInstr(new ExceptionRegionEndMarkerInstr());
 
         // Receive exceptions (could be anything, but the handler only processes IRBreakJumps)
         addInstr(new LabelInstr(rescueLabel));
         Variable exc = createTemporaryVariable();
         addInstr(new ReceiveJRubyExceptionInstr(exc));
 
         // Handle break using runtime helper
         // --> IRRuntimeHelpers.handleBreakAndReturnsInLambdas(context, scope, bj, blockType)
         Variable ret = createTemporaryVariable();
         addInstr(new RuntimeHelperCall(ret, RuntimeHelperCall.Methods.HANDLE_BREAK_AND_RETURNS_IN_LAMBDA, new Operand[]{exc} ));
         addInstr(new ReturnOrRethrowSavedExcInstr(ret));
 
         // End
         addInstr(new LabelInstr(rEndLabel));
     }
 
     public void receiveMethodArgs(final ArgsNode argsNode) {
         receiveArgs(argsNode);
     }
 
     public void receiveBlockArgs(final IterNode node) {
         Node args = node.getVarNode();
         if (args instanceof ArgsNode) { // regular blocks
             ((IRClosure) scope).setArgumentDescriptors(Helpers.argsNodeToArgumentDescriptors(((ArgsNode) args)));
             receiveArgs((ArgsNode)args);
         } else  {
             // for loops -- reuse code in IRBuilder:buildBlockArgsAssignment
             buildBlockArgsAssignment(args, null, 0, false);
         }
     }
 
     public Operand buildDot(final DotNode dotNode) {
         return addResultInstr(new BuildRangeInstr(createTemporaryVariable(), build(dotNode.getBeginNode()),
                 build(dotNode.getEndNode()), dotNode.isExclusive()));
     }
 
     private Operand dynamicPiece(Node pieceNode) {
         Operand piece = pieceNode instanceof StrNode ? buildStrRaw((StrNode) pieceNode) : build(pieceNode);
 
         if (piece instanceof StringLiteral) {
             piece = ((StringLiteral)piece).frozenString;
         }
 
         return piece == null ? manager.getNil() : piece;
     }
 
     public Operand buildDRegexp(DRegexpNode node) {
         Node[] nodePieces = node.children();
         Operand[] pieces = new Operand[nodePieces.length];
         for (int i = 0; i < pieces.length; i++) {
             pieces[i] = dynamicPiece(nodePieces[i]);
         }
 
         Variable res = createTemporaryVariable();
         addInstr(new BuildDynRegExpInstr(res, pieces, node.getOptions()));
         return res;
     }
 
     public Operand buildDStr(DStrNode node) {
         Node[] nodePieces = node.children();
         Operand[] pieces = new Operand[nodePieces.length];
         for (int i = 0; i < pieces.length; i++) {
             pieces[i] = dynamicPiece(nodePieces[i]);
         }
 
         Variable res = createTemporaryVariable();
         addInstr(new BuildCompoundStringInstr(res, pieces, node.getEncoding(), node.isFrozen(), getFileName(), node.getLine()));
         return res;
     }
 
     public Operand buildDSymbol(DSymbolNode node) {
         Node[] nodePieces = node.children();
         Operand[] pieces = new Operand[nodePieces.length];
         for (int i = 0; i < pieces.length; i++) {
             pieces[i] = dynamicPiece(nodePieces[i]);
         }
 
         Variable res = createTemporaryVariable();
         addInstr(new BuildCompoundStringInstr(res, pieces, node.getEncoding(), false, getFileName(), node.getLine()));
         return copyAndReturnValue(new DynamicSymbol(res));
     }
 
     public Operand buildDVar(DVarNode node) {
         return getLocalVariable(node.getName(), node.getDepth());
     }
 
     public Operand buildDXStr(final DXStrNode dstrNode) {
         Node[] nodePieces = dstrNode.children();
         Operand[] pieces = new Operand[nodePieces.length];
         for (int i = 0; i < pieces.length; i++) {
             pieces[i] = dynamicPiece(nodePieces[i]);
         }
 
         return addResultInstr(new BacktickInstr(createTemporaryVariable(), pieces));
     }
 
     /* ****************************************************************
      * Consider the ensure-protected ruby code below:
 
            begin
              .. protected body ..
            ensure
              .. eb code
            end
 
        This ruby code is effectively rewritten into the following ruby code
 
           begin
             .. protected body ..
             .. copy of ensure body code ..
           rescue <any-exception-or-error> => e
             .. ensure body code ..
             raise e
           end
 
       which in IR looks like this:
 
           L1:
             Exception region start marker_1 (protected by L10)
             ... IR for protected body ...
             Exception region end marker_1
           L2:
             Exception region start marker_2 (protected by whichever block handles exceptions for ensure body)
             .. copy of IR for ensure block ..
             Exception region end marker_2
             jump L3
           L10:          <----- dummy rescue block
             e = recv_exception
             .. IR for ensure block ..
             throw e
           L3:
 
      * ****************************************************************/
     public Operand buildEnsureNode(final EnsureNode ensureNode) {
         return buildEnsureInternal(ensureNode.getBodyNode(), ensureNode.getEnsureNode());
     }
 
     public Operand buildEnsureInternal(Node ensureBodyNode, Node ensurerNode) {
         // Save $!
         final Variable savedGlobalException = createTemporaryVariable();
         addInstr(new GetGlobalVariableInstr(savedGlobalException, "$!"));
 
         // ------------ Build the body of the ensure block ------------
         //
         // The ensure code is built first so that when the protected body is being built,
         // the ensure code can be cloned at break/next/return sites in the protected body.
 
         // Push a new ensure block node onto the stack of ensure bodies being built
         // The body's instructions are stashed and emitted later.
         EnsureBlockInfo ebi = new EnsureBlockInfo(scope,
             (ensureBodyNode instanceof RescueNode) ? (RescueNode)ensureBodyNode : null,
             getCurrentLoop(),
             activeRescuers.peek());
 
         // Record $! save var if we had a non-empty rescue node.
         // $! will be restored from it where required.
         if (ensureBodyNode != null && ensureBodyNode instanceof RescueNode) {
             ebi.savedGlobalException = savedGlobalException;
         }
 
         ensureBodyBuildStack.push(ebi);
         Operand ensureRetVal = ensurerNode == null ? manager.getNil() : build(ensurerNode);
         ensureBodyBuildStack.pop();
 
         // ------------ Build the protected region ------------
         activeEnsureBlockStack.push(ebi);
 
         // Start of protected region
         addInstr(new LabelInstr(ebi.regionStart));
         addInstr(new ExceptionRegionStartMarkerInstr(ebi.dummyRescueBlockLabel));
         activeRescuers.push(ebi.dummyRescueBlockLabel);
 
         // Generate IR for code being protected
         Variable ensureExprValue = createTemporaryVariable();
         Operand rv = ensureBodyNode instanceof RescueNode ? buildRescueInternal((RescueNode) ensureBodyNode, ebi) : build(ensureBodyNode);
 
         // End of protected region
         addInstr(new ExceptionRegionEndMarkerInstr());
         activeRescuers.pop();
 
         // Is this a begin..(rescue..)?ensure..end node that actually computes a value?
         // (vs. returning from protected body)
         boolean isEnsureExpr = ensurerNode != null && rv != U_NIL && !(ensureBodyNode instanceof RescueNode);
 
         // Clone the ensure body and jump to the end
         if (isEnsureExpr) {
             addInstr(new CopyInstr(ensureExprValue, rv));
             ebi.cloneIntoHostScope(this);
             addInstr(new JumpInstr(ebi.end));
         }
 
         // Pop the current ensure block info node
         activeEnsureBlockStack.pop();
 
         // ------------ Emit the ensure body alongwith dummy rescue block ------------
         // Now build the dummy rescue block that
         // catches all exceptions thrown by the body
         Variable exc = createTemporaryVariable();
         addInstr(new LabelInstr(ebi.dummyRescueBlockLabel));
         addInstr(new ReceiveJRubyExceptionInstr(exc));
 
         // Emit code to conditionally restore $!
         Variable ret = createTemporaryVariable();
         addInstr(new RuntimeHelperCall(ret, RESTORE_EXCEPTION_VAR, new Operand[]{exc, savedGlobalException} ));
 
         // Now emit the ensure body's stashed instructions
         if (ensurerNode != null) {
             ebi.emitBody(this);
         }
 
         // 1. Ensure block has no explicit return => the result of the entire ensure expression is the result of the protected body.
         // 2. Ensure block has an explicit return => the result of the protected body is ignored.
         // U_NIL => there was a return from within the ensure block!
         if (ensureRetVal == U_NIL) rv = U_NIL;
 
         // Return (rethrow exception/end)
         // rethrows the caught exception from the dummy ensure block
         addInstr(new ThrowExceptionInstr(exc));
 
         // End label for the exception region
         addInstr(new LabelInstr(ebi.end));
 
         return isEnsureExpr ? ensureExprValue : rv;
     }
 
     public Operand buildEvStr(EvStrNode node) {
         return new AsString(build(node.getBody()));
     }
 
     public Operand buildFalse() {
         return manager.getFalse();
     }
 
     public Operand buildFCall(FCallNode fcallNode) {
         Node      callArgsNode = fcallNode.getArgsNode();
         Operand[] args         = setupCallArgs(callArgsNode);
         Operand   block        = setupCallClosure(fcallNode.getIterNode());
         Variable  callResult   = createTemporaryVariable();
 
         determineIfMaybeUsingMethod(fcallNode.getName(), args);
 
         // We will stuff away the iters AST source into the closure in the hope we can convert
         // this closure to a method.
         if (fcallNode.getName().equals("define_method") && block instanceof WrappedIRClosure) {
             IRClosure closure = ((WrappedIRClosure) block).getClosure();
 
             // To convert to a method we need its variable scoping to appear like a normal method.
             if (!closure.getFlags().contains(IRFlags.ACCESS_PARENTS_LOCAL_VARIABLES) &&
                     fcallNode.getIterNode() instanceof IterNode) {
                 closure.setSource((IterNode) fcallNode.getIterNode());
             }
         }
 
         CallInstr callInstr    = CallInstr.create(scope, CallType.FUNCTIONAL, callResult, fcallNode.getName(), buildSelf(), args, block);
         receiveBreakException(block, callInstr);
         return callResult;
     }
 
     private Operand setupCallClosure(Node node) {
         if (node == null) return null;
 
         switch (node.getNodeType()) {
             case ITERNODE:
                 return build(node);
             case BLOCKPASSNODE:
                 Node bodyNode = ((BlockPassNode)node).getBodyNode();
                 if (bodyNode instanceof SymbolNode) {
                     return new SymbolProc(((SymbolNode)bodyNode).getName(), ((SymbolNode)bodyNode).getEncoding());
                 }
                 return build(bodyNode);
             default:
                 throw new NotCompilableException("ERROR: Encountered a method with a non-block, non-blockpass iter node at: " + node);
         }
     }
 
     // FIXME: This needs to be called on super/zsuper too
     private void determineIfMaybeUsingMethod(String methodName, Operand[] args) {
         IRScope outerScope = scope.getNearestTopLocalVariableScope();
 
         // 'using single_mod_arg' possible nearly everywhere but method scopes.
         if (USING_METHOD.equals(methodName) && !(outerScope instanceof IRMethod) && args.length == 1) {
             scope.setIsMaybeUsingRefinements();
         }
     }
 
     public Operand buildFixnum(FixnumNode node) {
         return new Fixnum(node.getValue());
     }
 
     public Operand buildFlip(FlipNode flipNode) {
         /* ----------------------------------------------------------------------
          * Consider a simple 2-state (s1, s2) FSM with the following transitions:
          *
          *     new_state(s1, F) = s1
          *     new_state(s1, T) = s2
          *     new_state(s2, F) = s2
          *     new_state(s2, T) = s1
          *
          * Here is the pseudo-code for evaluating the flip-node.
          * Let 'v' holds the value of the current state.
          *
          *    1. if (v == 's1') f1 = eval_condition(s1-condition); v = new_state(v, f1); ret = f1
          *    2. if (v == 's2') f2 = eval_condition(s2-condition); v = new_state(v, f2); ret = true
          *    3. return ret
          *
          * For exclusive flip conditions, line 2 changes to:
          *    2. if (!f1 && (v == 's2')) f2 = eval_condition(s2-condition); v = new_state(v, f2)
          *
          * In IR code below, we are representing the two states as 1 and 2.  Any
          * two values are good enough (even true and false), but 1 and 2 is simple
          * enough and also makes the IR output readable
          * ---------------------------------------------------------------------- */
 
         Fixnum s1 = new Fixnum((long)1);
         Fixnum s2 = new Fixnum((long)2);
 
         // Create a variable to hold the flip state
         IRBuilder nearestNonClosureBuilder = getNearestFlipVariableScopeBuilder();
 
         // Flip is completely broken atm and it was semi-broken in its last incarnation.
         // Method and closures (or evals) are not built at the same time and if -X-C or JIT or AOT
         // and jit.threshold=0 then the non-closure where we want to store the hidden flip variable
         // is unable to get more instrs added to it (not quite true for -X-C but definitely true
         // for JIT/AOT.  Also it means needing to grow the size of any heap scope for variables.
         if (nearestNonClosureBuilder == null) {
             Variable excType = createTemporaryVariable();
             addInstr(new InheritanceSearchConstInstr(excType, new ObjectClass(), "NotImplementedError"));
             Variable exc = addResultInstr(CallInstr.create(scope, createTemporaryVariable(), "new", excType, new Operand[] {new FrozenString("Flip support currently broken")}, null));
             addInstr(new ThrowExceptionInstr(exc));
             return buildNil();
         }
         Variable flipState = nearestNonClosureBuilder.scope.getNewFlipStateVariable();
         nearestNonClosureBuilder.initFlipStateVariable(flipState, s1);
         if (scope instanceof IRClosure) {
             // Clone the flip variable to be usable at the proper-depth.
             int n = 0;
             IRScope x = scope;
             while (!x.isFlipScope()) {
                 n++;
                 x = x.getLexicalParent();
             }
             if (n > 0) flipState = ((LocalVariable)flipState).cloneForDepth(n);
         }
 
         // Variables and labels needed for the code
         Variable returnVal = createTemporaryVariable();
         Label    s2Label   = getNewLabel();
         Label    doneLabel = getNewLabel();
 
         // Init
         addInstr(new CopyInstr(returnVal, manager.getFalse()));
 
         // Are we in state 1?
         addInstr(BNEInstr.create(s2Label, flipState, s1));
 
         // ----- Code for when we are in state 1 -----
         Operand s1Val = build(flipNode.getBeginNode());
         addInstr(BNEInstr.create(s2Label, s1Val, manager.getTrue()));
 
         // s1 condition is true => set returnVal to true & move to state 2
         addInstr(new CopyInstr(returnVal, manager.getTrue()));
         addInstr(new CopyInstr(flipState, s2));
 
         // Check for state 2
         addInstr(new LabelInstr(s2Label));
 
         // For exclusive ranges/flips, we dont evaluate s2's condition if s1's condition was satisfied
         if (flipNode.isExclusive()) addInstr(BEQInstr.create(returnVal, manager.getTrue(), doneLabel));
 
         // Are we in state 2?
         addInstr(BNEInstr.create(doneLabel, flipState, s2));
 
         // ----- Code for when we are in state 2 -----
         Operand s2Val = build(flipNode.getEndNode());
         addInstr(new CopyInstr(returnVal, manager.getTrue()));
         addInstr(BNEInstr.create(doneLabel, s2Val, manager.getTrue()));
 
         // s2 condition is true => move to state 1
         addInstr(new CopyInstr(flipState, s1));
 
         // Done testing for s1's and s2's conditions.
         // returnVal will have the result of the flip condition
         addInstr(new LabelInstr(doneLabel));
 
         return returnVal;
     }
 
     public Operand buildFloat(FloatNode node) {
         // Since flaot literals are effectively interned objects, no need to copyAndReturnValue(...)
         // SSS FIXME: Or is this a premature optimization?
         return new Float(node.getValue());
     }
 
     public Operand buildFor(ForNode forNode) {
         Variable result = createTemporaryVariable();
         Operand  receiver = build(forNode.getIterNode());
         Operand  forBlock = buildForIter(forNode);
         CallInstr callInstr = new CallInstr(CallType.NORMAL, result, "each", receiver, NO_ARGS, forBlock, scope.maybeUsingRefinements());
         receiveBreakException(forBlock, callInstr);
 
         return result;
     }
 
     private InterpreterContext buildForIterInner(ForNode forNode) {
         prepareImplicitState();                                    // recv_self, add frame block, etc)
 
         Node varNode = forNode.getVarNode();
         if (varNode != null && varNode.getNodeType() != null) receiveBlockArgs(forNode);
 
         addCurrentScopeAndModule();                                // %current_scope/%current_module
         addInstr(new LabelInstr(((IRClosure) scope).startLabel));  // Start label -- used by redo!
 
         // Build closure body and return the result of the closure
         Operand closureRetVal = forNode.getBodyNode() == null ? manager.getNil() : build(forNode.getBodyNode());
         if (closureRetVal != U_NIL) { // can be null if the node is an if node with returns in both branches.
             addInstr(new ReturnInstr(closureRetVal));
         }
 
         return scope.allocateInterpreterContext(instructions);
     }
 
     public Operand buildForIter(final ForNode forNode) {
         // Create a new closure context
         IRClosure closure = new IRFor(manager, scope, forNode.getLine(), forNode.getScope(), Signature.from(forNode));
 
         // Create a new nested builder to ensure this gets its own IR builder state like the ensure block stack
         newIRBuilder(manager, closure).buildForIterInner(forNode);
 
         return new WrappedIRClosure(buildSelf(), closure);
     }
 
     public Operand buildGlobalAsgn(GlobalAsgnNode globalAsgnNode) {
         Operand value = build(globalAsgnNode.getValueNode());
         addInstr(new PutGlobalVarInstr(globalAsgnNode.getName(), value));
         return value;
     }
 
     public Operand buildGlobalVar(GlobalVarNode node) {
         return addResultInstr(new GetGlobalVariableInstr(createTemporaryVariable(), node.getName()));
     }
 
     public Operand buildHash(HashNode hashNode) {
         List<KeyValuePair<Operand, Operand>> args = new ArrayList<>();
         boolean hasAssignments = hashNode.containsVariableAssignment();
         Variable hash = null;
 
         for (KeyValuePair<Node, Node> pair: hashNode.getPairs()) {
             Node key = pair.getKey();
             Operand keyOperand;
 
             if (key == null) {                          // Splat kwarg [e.g. {**splat1, a: 1, **splat2)]
                 if (hash == null) {                     // No hash yet. Define so order is preserved.
                     hash = copyAndReturnValue(new Hash(args));
                     args = new ArrayList<>();           // Used args but we may find more after the splat so we reset
                 } else if (!args.isEmpty()) {
                     addInstr(new RuntimeHelperCall(hash, MERGE_KWARGS, new Operand[] { hash, new Hash(args) }));
                     args = new ArrayList<>();
                 }
                 Operand splat = buildWithOrder(pair.getValue(), hasAssignments);
                 addInstr(new RuntimeHelperCall(hash, MERGE_KWARGS, new Operand[] { hash, splat}));
                 continue;
             } else {
                 keyOperand = buildWithOrder(key, hasAssignments);
             }
 
             args.add(new KeyValuePair<>(keyOperand, buildWithOrder(pair.getValue(), hasAssignments)));
         }
 
         if (hash == null) {           // non-**arg ordinary hash
             hash = copyAndReturnValue(new Hash(args));
         } else if (!args.isEmpty()) { // ordinary hash values encountered after a **arg
             addInstr(new RuntimeHelperCall(hash, MERGE_KWARGS, new Operand[] { hash, new Hash(args) }));
         }
 
         return hash;
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
     public Operand buildIf(final IfNode ifNode) {
         Node actualCondition = ifNode.getCondition();
 
         Variable result;
         Label    falseLabel = getNewLabel();
         Label    doneLabel  = getNewLabel();
         Operand  thenResult;
         addInstr(BEQInstr.create(build(actualCondition), manager.getFalse(), falseLabel));
 
         boolean thenNull = false;
         boolean elseNull = false;
         boolean thenUnil = false;
         boolean elseUnil = false;
 
         // Build the then part of the if-statement
         if (ifNode.getThenBody() != null) {
             thenResult = build(ifNode.getThenBody());
             if (thenResult != U_NIL) { // thenResult can be U_NIL if then-body ended with a return!
                 // SSS FIXME: Can look at the last instr and short-circuit this jump if it is a break rather
                 // than wait for dead code elimination to do it
                 result = getValueInTemporaryVariable(thenResult);
                 addInstr(new JumpInstr(doneLabel));
             } else {
                 result = createTemporaryVariable();
                 thenUnil = true;
             }
         } else {
             thenNull = true;
             result = addResultInstr(new CopyInstr(createTemporaryVariable(), manager.getNil()));
             addInstr(new JumpInstr(doneLabel));
         }
 
         // Build the else part of the if-statement
         addInstr(new LabelInstr(falseLabel));
         if (ifNode.getElseBody() != null) {
             Operand elseResult = build(ifNode.getElseBody());
             // elseResult can be U_NIL if then-body ended with a return!
             if (elseResult != U_NIL) {
                 addInstr(new CopyInstr(result, elseResult));
             } else {
                 elseUnil = true;
             }
         } else {
             elseNull = true;
             addInstr(new CopyInstr(result, manager.getNil()));
         }
 
         if (thenNull && elseNull) {
             addInstr(new LabelInstr(doneLabel));
             return manager.getNil();
         } else if (thenUnil && elseUnil) {
             return U_NIL;
         } else {
             addInstr(new LabelInstr(doneLabel));
             return result;
         }
     }
 
     public Operand buildInstAsgn(final InstAsgnNode instAsgnNode) {
         Operand val = build(instAsgnNode.getValueNode());
         // NOTE: if 's' happens to the a class, this is effectively an assignment of a class instance variable
         addInstr(new PutFieldInstr(buildSelf(), instAsgnNode.getName(), val));
         return val;
     }
 
     public Operand buildInstVar(InstVarNode node) {
         return addResultInstr(new GetFieldInstr(createTemporaryVariable(), buildSelf(), node.getName()));
     }
 
     private InterpreterContext buildIterInner(IterNode iterNode) {
         prepareImplicitState();                                    // recv_self, add frame block, etc)
         addCurrentScopeAndModule();                                // %current_scope/%current_module
 
         if (iterNode.getVarNode().getNodeType() != null) receiveBlockArgs(iterNode);
 
         addInstr(new LabelInstr(((IRClosure) scope).startLabel));  // start label -- used by redo!
 
         // Build closure body and return the result of the closure
         Operand closureRetVal = iterNode.getBodyNode() == null ? manager.getNil() : build(iterNode.getBodyNode());
         if (closureRetVal != U_NIL) { // can be U_NIL if the node is an if node with returns in both branches.
             addInstr(new ReturnInstr(closureRetVal));
         }
 
         // Always add break/return handling even though this
         // is only required for lambdas, but we don't know at this time,
         // if this is a lambda or not.
         //
         // SSS FIXME: At a later time, see if we can optimize this and
         // do this on demand.
         handleBreakAndReturnsInLambdas();
 
         return scope.allocateInterpreterContext(instructions);
     }
     public Operand buildIter(final IterNode iterNode) {
         IRClosure closure = new IRClosure(manager, scope, iterNode.getLine(), iterNode.getScope(), Signature.from(iterNode), needsCodeCoverage);
 
         // Create a new nested builder to ensure this gets its own IR builder state like the ensure block stack
         newIRBuilder(manager, closure).buildIterInner(iterNode);
 
         return new WrappedIRClosure(buildSelf(), closure);
     }
 
     public Operand buildLiteral(LiteralNode literalNode) {
         return copyAndReturnValue(new StringLiteral(literalNode.getName()));
     }
 
     public Operand buildLocalAsgn(LocalAsgnNode localAsgnNode) {
         Variable var  = getLocalVariable(localAsgnNode.getName(), localAsgnNode.getDepth());
         Operand value = build(localAsgnNode.getValueNode());
 
         // no use copying a variable to itself
         if (var == value) return value;
 
         addInstr(new CopyInstr(var, value));
 
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
 
     public Operand buildLocalVar(LocalVarNode node) {
         return getLocalVariable(node.getName(), node.getDepth());
     }
 
     public Operand buildMatch(MatchNode matchNode) {
         Operand regexp = build(matchNode.getRegexpNode());
 
         Variable tempLastLine = createTemporaryVariable();
         addResultInstr(new GetGlobalVariableInstr(tempLastLine, "$_"));
         return addResultInstr(new MatchInstr(createTemporaryVariable(), regexp, tempLastLine));
     }
 
     public Operand buildMatch2(Match2Node matchNode) {
         Operand receiver = build(matchNode.getReceiverNode());
         Operand value    = build(matchNode.getValueNode());
         Variable result  = createTemporaryVariable();
         addInstr(new MatchInstr(result, receiver, value));
 
         if (matchNode instanceof Match2CaptureNode) {
             Match2CaptureNode m2c = (Match2CaptureNode)matchNode;
             for (int slot:  m2c.getScopeOffsets()) {
                 // Static scope scope offsets store both depth and offset
                 int depth = slot >> 16;
                 int offset = slot & 0xffff;
 
                 // For now, we'll continue to implicitly reference "$~"
                 String var = getVarNameFromScopeTree(scope, depth, offset);
                 addInstr(new SetCapturedVarInstr(getLocalVariable(var, depth), result, var));
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
 
     public Operand buildMatch3(Match3Node matchNode) {
         Operand receiver = build(matchNode.getReceiverNode());
         Operand value = build(matchNode.getValueNode());
 
         return addResultInstr(new MatchInstr(createTemporaryVariable(), receiver, value));
     }
 
     private Operand getContainerFromCPath(Colon3Node cpath) {
         Operand container;
 
         if (cpath instanceof Colon2Node) {
             Node leftNode = ((Colon2Node) cpath).getLeftNode();
 
             if (leftNode != null) { // Foo::Bar
                 container = build(leftNode);
             } else { // Only name with no left-side Bar <- Note no :: on left
                 container = findContainerModule();
             }
         } else { //::Bar
             container = new ObjectClass();
         }
 
         return container;
     }
 
     public Operand buildModule(ModuleNode moduleNode) {
         Colon3Node cpath = moduleNode.getCPath();
         String moduleName = cpath.getName();
         Operand container = getContainerFromCPath(cpath);
         IRModuleBody body = new IRModuleBody(manager, scope, moduleName, moduleNode.getLine(), moduleNode.getScope());
         Variable moduleVar = addResultInstr(new DefineModuleInstr(createTemporaryVariable(), body, container));
 
         Variable processBodyResult = addResultInstr(new ProcessModuleBodyInstr(createTemporaryVariable(), moduleVar, NullBlock.INSTANCE));
         newIRBuilder(manager, body).buildModuleOrClassBody(moduleNode.getBodyNode(), moduleNode.getLine());
         return processBodyResult;
     }
 
     public Operand buildNext(final NextNode nextNode) {
         IRLoop currLoop = getCurrentLoop();
 
         Operand rv = build(nextNode.getValueNode());
 
         // If we have ensure blocks, have to run those first!
         if (!activeEnsureBlockStack.empty()) emitEnsureBlocks(currLoop);
 
         if (currLoop != null) {
             // If a regular loop, the next is simply a jump to the end of the iteration
             addInstr(new JumpInstr(currLoop.iterEndLabel));
         } else {
             addInstr(new ThreadPollInstr(true));
             // If a closure, the next is simply a return from the closure!
             if (scope instanceof IRClosure) {
                 addInstr(new ReturnInstr(rv));
             } else {
                 addInstr(new ThrowExceptionInstr(IRException.NEXT_LocalJumpError));
             }
         }
 
         // Once the "next instruction" (closure-return) executes, control exits this scope
         return U_NIL;
     }
 
     public Operand buildNthRef(NthRefNode nthRefNode) {
         return copyAndReturnValue(new NthRef(nthRefNode.getMatchNumber()));
     }
 
     public Operand buildNil() {
         return manager.getNil();
     }
 
     // FIXME: The logic for lazy and non-lazy building is pretty icky...clean up
     public Operand buildOpAsgn(OpAsgnNode opAsgnNode) {
         Label l;
         Variable readerValue = createTemporaryVariable();
         Variable writerValue = createTemporaryVariable();
         Node receiver = opAsgnNode.getReceiverNode();
         CallType callType = receiver instanceof SelfNode ? CallType.FUNCTIONAL : CallType.NORMAL;
 
         // get attr
         Operand  v1 = build(opAsgnNode.getReceiverNode());
 
         Label lazyLabel = getNewLabel();
         Label endLabel = getNewLabel();
         Variable result = createTemporaryVariable();
         if (opAsgnNode.isLazy()) {
             addInstr(new BNilInstr(lazyLabel, v1));
         }
 
         addInstr(CallInstr.create(scope, callType, readerValue, opAsgnNode.getVariableName(), v1, NO_ARGS, null));
 
         // Ex: e.val ||= n
         //     e.val &&= n
         String opName = opAsgnNode.getOperatorName();
         if (opName.equals("||") || opName.equals("&&")) {
             l = getNewLabel();
             addInstr(BEQInstr.create(readerValue, opName.equals("||") ? manager.getTrue() : manager.getFalse(), l));
 
             // compute value and set it
             Operand  v2 = build(opAsgnNode.getValueNode());
             addInstr(CallInstr.create(scope, callType, writerValue, opAsgnNode.getVariableNameAsgn(), v1, new Operand[] {v2}, null));
             // It is readerValue = v2.
             // readerValue = writerValue is incorrect because the assignment method
             // might return something else other than the value being set!
             addInstr(new CopyInstr(readerValue, v2));
             addInstr(new LabelInstr(l));
 
             if (!opAsgnNode.isLazy()) return readerValue;
 
             addInstr(new CopyInstr(result, readerValue));
         } else {  // Ex: e.val = e.val.f(n)
             // call operator
             Operand  v2 = build(opAsgnNode.getValueNode());
             Variable setValue = createTemporaryVariable();
             addInstr(CallInstr.create(scope, setValue, opAsgnNode.getOperatorName(), readerValue, new Operand[]{v2}, null));
 
             // set attr
             addInstr(CallInstr.create(scope, callType, writerValue, opAsgnNode.getVariableNameAsgn(), v1, new Operand[] {setValue}, null));
             // Returning writerValue is incorrect becuase the assignment method
             // might return something else other than the value being set!
             if (!opAsgnNode.isLazy()) return setValue;
 
             addInstr(new CopyInstr(result, setValue));
         }
 
diff --git a/core/src/main/java/org/jruby/ir/instructions/ReceivePostReqdArgInstr.java b/core/src/main/java/org/jruby/ir/instructions/ReceivePostReqdArgInstr.java
index cf1fd4b43d..abbe9fe73c 100644
--- a/core/src/main/java/org/jruby/ir/instructions/ReceivePostReqdArgInstr.java
+++ b/core/src/main/java/org/jruby/ir/instructions/ReceivePostReqdArgInstr.java
@@ -1,82 +1,95 @@
 package org.jruby.ir.instructions;
 
 import org.jruby.ir.IRVisitor;
 import org.jruby.ir.Operation;
 import org.jruby.ir.operands.Operand;
 import org.jruby.ir.operands.Variable;
 import org.jruby.ir.persistence.IRReaderDecoder;
 import org.jruby.ir.persistence.IRWriterEncoder;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.ir.transformations.inlining.CloneInfo;
 import org.jruby.ir.transformations.inlining.InlineCloneInfo;
 import org.jruby.ir.transformations.inlining.SimpleCloneInfo;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * This represents a required arg that shows up after optional/rest args
  * in a method/block parameter list. This instruction gets to pick an argument
  * based on how many arguments have already been accounted for by parameters
  * present earlier in the list.
  */
 public class ReceivePostReqdArgInstr extends ReceiveArgBase implements FixedArityInstr {
     /** The method/block parameter list has these many required parameters before opt+rest args*/
     public final int preReqdArgsCount;
 
+    /** The method/block parameter list has a maximum of this many optional arguments*/
+    public final int optArgsCount;
+
+    /** Does this method/block accept a rest argument */
+    public final boolean restArg;
+
     /** The method/block parameter list has these many required parameters after opt+rest args*/
     public final int postReqdArgsCount;
 
-    public ReceivePostReqdArgInstr(Variable result, int argIndex, int preReqdArgsCount, int postReqdArgsCount) {
+    public ReceivePostReqdArgInstr(Variable result, int argIndex, int preReqdArgsCount, int optArgCount, boolean restArg, int postReqdArgsCount) {
         super(Operation.RECV_POST_REQD_ARG, result, argIndex);
         this.preReqdArgsCount = preReqdArgsCount;
+        this.optArgsCount = optArgCount;
+        this.restArg = restArg;
         this.postReqdArgsCount = postReqdArgsCount;
     }
 
     @Override
     public String[] toStringNonOperandArgs() {
         return new String[] { "index: " + getArgIndex(), "pre: " + preReqdArgsCount, "post: " + postReqdArgsCount};
     }
 
     @Override
     public Instr clone(CloneInfo info) {
-        if (info instanceof SimpleCloneInfo) return new ReceivePostReqdArgInstr(info.getRenamedVariable(result), argIndex, preReqdArgsCount, postReqdArgsCount);
+        if (info instanceof SimpleCloneInfo) {
+            return new ReceivePostReqdArgInstr(info.getRenamedVariable(result), argIndex, preReqdArgsCount, optArgsCount, restArg, postReqdArgsCount);
+        }
 
         InlineCloneInfo ii = (InlineCloneInfo) info;
 
         if (ii.canMapArgsStatically()) {
             int n = ii.getArgsCount();
             int remaining = n - preReqdArgsCount;
             Operand argVal;
             if (remaining <= argIndex) {
                 // SSS: FIXME: Argh!
                 argVal = ii.getHostScope().getManager().getNil();
             } else {
                 argVal = (remaining > postReqdArgsCount) ? ii.getArg(n - postReqdArgsCount + argIndex) : ii.getArg(preReqdArgsCount + argIndex);
             }
             return new CopyInstr(ii.getRenamedVariable(result), argVal);
         }
 
         return new ReqdArgMultipleAsgnInstr(ii.getRenamedVariable(result), ii.getArgs(), preReqdArgsCount, postReqdArgsCount, argIndex);
     }
 
     @Override
     public void encode(IRWriterEncoder e) {
         super.encode(e);
         e.encode(getArgIndex());
         e.encode(preReqdArgsCount);
+        e.encode(optArgsCount);
+        e.encode(restArg);
         e.encode(postReqdArgsCount);
     }
 
     public static ReceivePostReqdArgInstr decode(IRReaderDecoder d) {
-        return new ReceivePostReqdArgInstr(d.decodeVariable(), d.decodeInt(), d.decodeInt(), d.decodeInt());
+        return new ReceivePostReqdArgInstr(d.decodeVariable(), d.decodeInt(), d.decodeInt(), d.decodeInt(), d.decodeBoolean(), d.decodeInt());
     }
 
     public IRubyObject receivePostReqdArg(ThreadContext context, IRubyObject[] args, boolean acceptsKeywordArgument) {
-        return IRRuntimeHelpers.receivePostReqdArg(context, args, preReqdArgsCount, postReqdArgsCount, argIndex, acceptsKeywordArgument);
+        return IRRuntimeHelpers.receivePostReqdArg(context, args, preReqdArgsCount, optArgsCount, restArg,
+                postReqdArgsCount, argIndex, acceptsKeywordArgument);
     }
 
     @Override
     public void visit(IRVisitor visitor) {
         visitor.ReceivePostReqdArgInstr(this);
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/runtime/IRRuntimeHelpers.java b/core/src/main/java/org/jruby/ir/runtime/IRRuntimeHelpers.java
index 2c00f0b894..b1a9c6b3e9 100644
--- a/core/src/main/java/org/jruby/ir/runtime/IRRuntimeHelpers.java
+++ b/core/src/main/java/org/jruby/ir/runtime/IRRuntimeHelpers.java
@@ -1,1907 +1,1926 @@
 package org.jruby.ir.runtime;
 
 import com.headius.invokebinder.Signature;
 import org.jcodings.Encoding;
 import org.jruby.*;
 import org.jruby.common.IRubyWarnings;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.Unrescuable;
 import org.jruby.internal.runtime.methods.CompiledIRMetaClassBody;
 import org.jruby.internal.runtime.methods.CompiledIRMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.InterpretedIRBodyMethod;
 import org.jruby.internal.runtime.methods.InterpretedIRMetaClassBody;
 import org.jruby.internal.runtime.methods.InterpretedIRMethod;
 import org.jruby.internal.runtime.methods.MixedModeIRMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.ir.IRMetaClassBody;
 import org.jruby.ir.IRScope;
 import org.jruby.ir.IRScopeType;
 import org.jruby.ir.Interp;
 import org.jruby.ir.JIT;
 import org.jruby.ir.Tuple;
 import org.jruby.ir.operands.IRException;
 import org.jruby.ir.operands.Operand;
 import org.jruby.ir.operands.Splat;
 import org.jruby.ir.operands.UndefinedValue;
 import org.jruby.ir.persistence.IRReader;
 import org.jruby.ir.persistence.IRReaderStream;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.*;
 import org.jruby.runtime.JavaSites.IRRuntimeHelpersSites;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callsite.CachingCallSite;
 import org.jruby.runtime.callsite.FunctionalCachingCallSite;
 import org.jruby.runtime.callsite.NormalCachingCallSite;
 import org.jruby.runtime.callsite.RefinedCachingCallSite;
 import org.jruby.runtime.callsite.VariableCachingCallSite;
 import org.jruby.runtime.ivars.VariableAccessor;
 import org.jruby.util.ArraySupport;
 import org.jruby.util.ByteList;
 import org.jruby.util.DefinedMessage;
 import org.jruby.util.RecursiveComparator;
 import org.jruby.util.RegexpOptions;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 import org.objectweb.asm.Type;
 
 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.lang.invoke.MethodHandle;
 import java.util.Map;
 
 public class IRRuntimeHelpers {
     private static final Logger LOG = LoggerFactory.getLogger(IRRuntimeHelpers.class);
 
     public static boolean inProfileMode() {
         return RubyInstanceConfig.IR_PROFILE;
     }
 
     public static boolean isDebug() {
         return RubyInstanceConfig.IR_DEBUG;
     }
 
     public static boolean inNonMethodBodyLambda(StaticScope scope, Block.Type blockType) {
         // SSS FIXME: Hack! AST interpreter and JIT compiler marks a proc's static scope as
         // an argument scope if it is used to define a method's body via :define_method.
         // Since that is exactly what we want to figure out here, am just using that flag here.
         // But, this is ugly (as is the original hack in the current runtime).  What is really
         // needed is a new block type -- a block that is used to define a method body.
         return blockType == Block.Type.LAMBDA && !scope.isArgumentScope();
     }
 
     public static boolean inLambda(Block.Type blockType) {
         return blockType == Block.Type.LAMBDA;
     }
 
     public static boolean inProc(Block.Type blockType) {
         return blockType == Block.Type.PROC;
     }
 
     public static void checkForLJE(ThreadContext context, DynamicScope dynScope, boolean maybeLambda, Block.Type blockType) {
         if (IRRuntimeHelpers.inLambda(blockType)) return;
 
         StaticScope scope = dynScope.getStaticScope();
         IRScopeType scopeType = scope.getScopeType();
         boolean inDefineMethod = false;
         while (dynScope != null) {
             StaticScope ss = dynScope.getStaticScope();
             // SSS FIXME: Why is scopeType empty? Looks like this static-scope
             // was not associated with the AST scope that got converted to IR.
             //
             // Ruby code: lambda { Thread.new { return }.join }.call
             //
             // To be investigated.
             IRScopeType ssType = ss.getScopeType();
             if (ssType != null) {
                 if (ssType.isMethodType()) {
                     break;
                 } else if (ss.isArgumentScope() && ssType.isClosureType() && ssType != IRScopeType.EVAL_SCRIPT) {
                     inDefineMethod = true;
                     break;
                 }
             }
             dynScope = dynScope.getParentScope();
         }
 
         // SSS FIXME: Why is scopeType empty? Looks like this static-scope
         // was not associated with the AST scope that got converted to IR.
         //
         // Ruby code: lambda { Thread.new { return }.join }.call
         //
         // To be investigated.
         if (   (scopeType == null || (!inDefineMethod && scopeType.isClosureType() && scopeType != IRScopeType.EVAL_SCRIPT))
             && (maybeLambda || !context.scopeExistsOnCallStack(dynScope)))
         {
             // Cannot return from the call that we have long since exited.
             throw IRException.RETURN_LocalJumpError.getException(context.runtime);
         }
     }
 
     /*
      * Handle non-local returns (ex: when nested in closures, root scopes of module/class/sclass bodies)
      */
     public static IRubyObject initiateNonLocalReturn(ThreadContext context, DynamicScope dynScope, Block.Type blockType, IRubyObject returnValue) {
         if (IRRuntimeHelpers.inLambda(blockType)) throw new IRWrappedLambdaReturnValue(returnValue);
 
         // If not in a lambda, check if this was a non-local return
         while (dynScope != null) {
             StaticScope ss = dynScope.getStaticScope();
             // SSS FIXME: Why is scopeType empty? Looks like this static-scope
             // was not associated with the AST scope that got converted to IR.
             //
             // Ruby code: lambda { Thread.new { return }.join }.call
             //
             // To be investigated.
             IRScopeType ssType = ss.getScopeType();
             if (ssType != null) {
                 if (ssType.isMethodType() ||
                         (ss.isArgumentScope() && ssType.isClosureType() && ssType != IRScopeType.EVAL_SCRIPT) ||
                         (ssType.isClosureType() && dynScope.isLambda())) {
                     break;
                 }
             }
             dynScope = dynScope.getParentScope();
         }
 
         // methodtoReturnFrom will not be -1 for explicit returns from class/module/sclass bodies
         throw IRReturnJump.create(dynScope, returnValue);
     }
 
     @JIT
     public static IRubyObject handleNonlocalReturn(StaticScope scope, DynamicScope dynScope, Object rjExc, Block.Type blockType) throws RuntimeException {
         if (!(rjExc instanceof IRReturnJump)) {
             Helpers.throwException((Throwable)rjExc);
             return null; // Unreachable
         } else {
             IRReturnJump rj = (IRReturnJump)rjExc;
 
             // If we are in the method scope we are supposed to return from, stop propagating.
             if (rj.methodToReturnFrom == dynScope) {
                 if (isDebug()) System.out.println("---> Non-local Return reached target in scope: " + dynScope + " matching dynscope? " + (rj.methodToReturnFrom == dynScope));
                 return (IRubyObject) rj.returnValue;
             }
 
             // If not, Just pass it along!
             throw rj;
         }
     }
 
     public static IRubyObject initiateBreak(ThreadContext context, DynamicScope dynScope, IRubyObject breakValue, Block.Type blockType) throws RuntimeException {
         if (inLambda(blockType)) {
             // Wrap the return value in an exception object
             // and push it through the break exception paths so
             // that ensures are run, frames/scopes are popped
             // from runtime stacks, etc.
             throw new IRWrappedLambdaReturnValue(breakValue);
         } else {
             StaticScope scope = dynScope.getStaticScope();
             IRScopeType scopeType = scope.getScopeType();
             if (!scopeType.isClosureType()) {
                 // Error -- breaks can only be initiated in closures
                 throw IRException.BREAK_LocalJumpError.getException(context.runtime);
             }
 
             IRBreakJump bj = IRBreakJump.create(dynScope.getParentScope(), breakValue);
             if (scopeType == IRScopeType.EVAL_SCRIPT) {
                 // If we are in an eval, record it so we can account for it
                 bj.breakInEval = true;
             }
 
             // Start the process of breaking through the intermediate scopes
             throw bj;
         }
     }
 
     @JIT
     public static IRubyObject handleBreakAndReturnsInLambdas(ThreadContext context, StaticScope scope, DynamicScope dynScope, Object exc, Block.Type blockType) throws RuntimeException {
         if (exc instanceof IRWrappedLambdaReturnValue) {
             // Wrap the return value in an exception object
             // and push it through the nonlocal return exception paths so
             // that ensures are run, frames/scopes are popped
             // from runtime stacks, etc.
             return ((IRWrappedLambdaReturnValue)exc).returnValue;
         } else if ((exc instanceof IRBreakJump) && inNonMethodBodyLambda(scope, blockType)) {
             // We just unwound all the way up because of a non-local break
             context.setSavedExceptionInLambda(IRException.BREAK_LocalJumpError.getException(context.runtime));
             return null;
         } else if (exc instanceof IRReturnJump && (blockType == null || inLambda(blockType))) {
             try {
                 // Ignore non-local return processing in non-lambda blocks.
                 // Methods have a null blocktype
                 return handleNonlocalReturn(scope, dynScope, exc, blockType);
             } catch (Throwable e) {
                 context.setSavedExceptionInLambda(e);
                 return null;
             }
         } else {
             // Propagate the exception
             context.setSavedExceptionInLambda((Throwable)exc);
             return null;
         }
     }
 
     @JIT
     public static IRubyObject returnOrRethrowSavedException(ThreadContext context, IRubyObject value) {
         // This rethrows the exception saved in handleBreakAndReturnsInLambda
         // after additional code to pop frames, bindings, etc. are done.
         Throwable exc = context.getSavedExceptionInLambda();
         if (exc != null) {
             // IMPORTANT: always clear!
             context.setSavedExceptionInLambda(null);
             Helpers.throwException(exc);
         }
 
         // otherwise, return value
         return value;
     }
 
     @JIT
     public static IRubyObject handlePropagatedBreak(ThreadContext context, DynamicScope dynScope, Object bjExc, Block.Type blockType) {
         if (!(bjExc instanceof IRBreakJump)) {
             Helpers.throwException((Throwable)bjExc);
             return null; // Unreachable
         }
 
         IRBreakJump bj = (IRBreakJump)bjExc;
         if (bj.breakInEval) {
             // If the break was in an eval, we pretend as if it was in the containing scope
             StaticScope scope = dynScope.getStaticScope();
             IRScopeType scopeType = scope.getScopeType();
             if (!scopeType.isClosureType()) {
                 // Error -- breaks can only be initiated in closures
                 throw IRException.BREAK_LocalJumpError.getException(context.runtime);
             } else {
                 bj.breakInEval = false;
                 throw bj;
             }
         } else if (bj.scopeToReturnTo == dynScope) {
             // Done!! Hurray!
             if (isDebug()) System.out.println("---> Break reached target in scope: " + dynScope);
             return bj.breakValue;
 /* ---------------------------------------------------------------
  * SSS FIXME: Puzzled .. Why is this not needed?
         } else if (!context.scopeExistsOnCallStack(bj.scopeToReturnTo.getStaticScope())) {
             throw IRException.BREAK_LocalJumpError.getException(context.runtime);
  * --------------------------------------------------------------- */
         } else {
             // Propagate
             throw bj;
         }
     }
 
     // Used by JIT
     public static IRubyObject undefMethod(ThreadContext context, Object nameArg, DynamicScope currDynScope, IRubyObject self) {
         RubyModule module = IRRuntimeHelpers.findInstanceMethodContainer(context, currDynScope, self);
         String name = (nameArg instanceof String) ?
                 (String) nameArg : nameArg.toString();
 
         if (module == null) {
             throw context.runtime.newTypeError("No class to undef method '" + name + "'.");
         }
 
         module.undef(context, name);
 
         return context.nil;
     }
 
     public static double unboxFloat(IRubyObject val) {
         if (val instanceof RubyFloat) {
             return ((RubyFloat)val).getValue();
         } else {
             return ((RubyFixnum)val).getDoubleValue();
         }
     }
 
     public static long unboxFixnum(IRubyObject val) {
         if (val instanceof RubyFloat) {
             return (long)((RubyFloat)val).getValue();
         } else {
             return ((RubyFixnum)val).getLongValue();
         }
     }
 
     public static boolean flt(double v1, double v2) {
         return v1 < v2;
     }
 
     public static boolean fgt(double v1, double v2) {
         return v1 > v2;
     }
 
     public static boolean feq(double v1, double v2) {
         return v1 == v2;
     }
 
     public static boolean ilt(long v1, long v2) {
         return v1 < v2;
     }
 
     public static boolean igt(long v1, long v2) {
         return v1 > v2;
     }
 
     public static boolean ieq(long v1, long v2) {
         return v1 == v2;
     }
 
     public static Object unwrapRubyException(Object excObj) {
         // Unrescuable:
         //   IRBreakJump, IRReturnJump, ThreadKill, RubyContinuation, MainExitException, etc.
         //   These cannot be rescued -- only run ensure blocks
         if (excObj instanceof Unrescuable) {
             Helpers.throwException((Throwable)excObj);
         }
         // Ruby exceptions, errors, and other java exceptions.
         // These can be rescued -- run rescue blocks
         return (excObj instanceof RaiseException) ? ((RaiseException) excObj).getException() : excObj;
     }
 
     private static boolean isJavaExceptionHandled(ThreadContext context, IRubyObject excType, Object excObj, boolean arrayCheck) {
         if (!(excObj instanceof Throwable)) {
             return false;
         }
 
         final Ruby runtime = context.runtime;
         final Throwable ex = (Throwable) excObj;
 
         if (excType instanceof RubyArray) {
             RubyArray testTypes = (RubyArray)excType;
             for (int i = 0, n = testTypes.getLength(); i < n; i++) {
                 IRubyObject testType = testTypes.eltInternal(i);
                 if (IRRuntimeHelpers.isJavaExceptionHandled(context, testType, ex, true)) {
                     IRubyObject exception;
                     if (n == 1) {
                         exception = wrapJavaException(context, testType, ex);
                     } else { // wrap as normal JI object
                         exception = Helpers.wrapJavaException(runtime, ex);
                     }
 
                     runtime.getGlobalVariables().set("$!", exception);
                     return true;
                 }
             }
         }
         else {
             IRubyObject exception = wrapJavaException(context, excType, ex);
             if (Helpers.checkJavaException(exception, ex, excType, context)) {
                 runtime.getGlobalVariables().set("$!", exception);
                 return true;
             }
         }
 
         return false;
     }
 
     private static IRubyObject wrapJavaException(final ThreadContext context, final IRubyObject excType, final Throwable throwable) {
         final Ruby runtime = context.runtime;
         if (excType == runtime.getNativeException()) { // wrap Throwable in a NativeException object
             NativeException exception = new NativeException(runtime, runtime.getNativeException(), throwable);
             exception.prepareIntegratedBacktrace(context, throwable.getStackTrace());
             return exception;
         }
         return Helpers.wrapJavaException(runtime, throwable); // wrap as normal JI object
     }
 
     private static boolean isRubyExceptionHandled(ThreadContext context, IRubyObject excType, Object excObj) {
         if (excType instanceof RubyArray) {
             RubyArray testTypes = (RubyArray)excType;
             for (int i = 0, n = testTypes.getLength(); i < n; i++) {
                 IRubyObject testType = testTypes.eltInternal(i);
                 if (IRRuntimeHelpers.isRubyExceptionHandled(context, testType, excObj)) {
                     context.runtime.getGlobalVariables().set("$!", (IRubyObject)excObj);
                     return true;
                 }
             }
         } else if (excObj instanceof IRubyObject) {
             // SSS FIXME: Should this check be "runtime.getModule().isInstance(excType)"??
             if (!(excType instanceof RubyModule)) {
                 throw context.runtime.newTypeError("class or module required for rescue clause. Found: " + excType);
             }
 
             if (excType.callMethod(context, "===", (IRubyObject)excObj).isTrue()) {
                 context.runtime.getGlobalVariables().set("$!", (IRubyObject)excObj);
                 return true;
             }
         }
         return false;
     }
 
     public static IRubyObject isExceptionHandled(ThreadContext context, IRubyObject excType, Object excObj) {
         // SSS FIXME: JIT should do an explicit unwrap in code just like in interpreter mode.
         // This is called once for each RescueEQQ instr and unwrapping each time is unnecessary.
         // This is not a performance issue, but more a question of where this belongs.
         // It seems more logical to (a) recv-exc (b) unwrap-exc (c) do all the rescue-eqq checks.
         //
         // Unwrap Ruby exceptions
         excObj = unwrapRubyException(excObj);
 
         boolean ret = IRRuntimeHelpers.isRubyExceptionHandled(context, excType, excObj)
             || IRRuntimeHelpers.isJavaExceptionHandled(context, excType, excObj, false);
 
         return context.runtime.newBoolean(ret);
     }
 
     public static IRubyObject isEQQ(ThreadContext context, IRubyObject receiver, IRubyObject value, CallSite callSite, boolean splattedValue) {
         boolean isUndefValue = value == UndefinedValue.UNDEFINED;
         if (splattedValue && receiver instanceof RubyArray) {
             RubyArray testVals = (RubyArray) receiver;
             for (int i = 0, n = testVals.getLength(); i < n; i++) {
                 IRubyObject v = testVals.eltInternal(i);
                 IRubyObject eqqVal = isUndefValue ? v : callSite.call(context, v, v, value);
                 if (eqqVal.isTrue()) return eqqVal;
             }
             return context.runtime.getFalse();
         }
         return isUndefValue ? receiver : callSite.call(context, receiver, receiver, value);
     }
 
     @Deprecated
     public static IRubyObject isEQQ(ThreadContext context, IRubyObject receiver, IRubyObject value, CallSite callSite) {
         return isEQQ(context, receiver, value, callSite, true);
     }
 
     public static IRubyObject newProc(Ruby runtime, Block block) {
         return (block == Block.NULL_BLOCK) ? runtime.getNil() : runtime.newProc(Block.Type.PROC, block);
     }
 
     public static IRubyObject yield(ThreadContext context, Block b, IRubyObject yieldVal, boolean unwrapArray) {
         return (unwrapArray && (yieldVal instanceof RubyArray)) ? b.yieldArray(context, yieldVal, null) : b.yield(context, yieldVal);
     }
 
     public static IRubyObject yieldSpecific(ThreadContext context, Block b) {
         return b.yieldSpecific(context);
     }
 
     public static IRubyObject[] convertValueIntoArgArray(ThreadContext context, IRubyObject value,
                                                          org.jruby.runtime.Signature signature, boolean argIsArray) {
         // SSS FIXME: This should not really happen -- so, some places in the runtime library are breaking this contract.
         if (argIsArray && !(value instanceof RubyArray)) argIsArray = false;
 
         switch (signature.arityValue()) {
             case -1 :
                 return argIsArray || (signature.opt() > 1 && value instanceof RubyArray) ? ((RubyArray)value).toJavaArray() : new IRubyObject[] { value };
             case  0 : return new IRubyObject[] { value };
             case  1 : {
                if (argIsArray) {
                    RubyArray valArray = ((RubyArray)value);
                    if (valArray.size() == 0) {
                        value = RubyArray.newEmptyArray(context.runtime);
                    }
                }
                return new IRubyObject[] { value };
             }
             default :
                 if (argIsArray) {
                     RubyArray valArray = (RubyArray)value;
                     if (valArray.size() == 1) value = valArray.eltInternal(0);
                     value = Helpers.aryToAry(value);
                     return (value instanceof RubyArray) ? ((RubyArray)value).toJavaArray() : new IRubyObject[] { value };
                 } else {
                     IRubyObject val0 = Helpers.aryToAry(value);
                     // FIXME: This logic exists in RubyProc and IRRubyBlockBody. consolidate when we do block call protocol work
                     if (val0.isNil()) {
                         return new IRubyObject[] { value };
                     } else if (!(val0 instanceof RubyArray)) {
                         throw context.runtime.newTypeError(value.getType().getName() + "#to_ary should return Array");
                     } else {
                         return ((RubyArray) val0).toJavaArray();
                     }
                 }
         }
     }
 
     @JIT
     public static Block getBlockFromObject(ThreadContext context, Object value) {
         Block block;
         if (value instanceof Block) {
             block = (Block) value;
         } else if (value instanceof RubyProc) {
             block = ((RubyProc) value).getBlock();
         } else if (value instanceof RubyMethod) {
             block = ((RubyProc)((RubyMethod)value).to_proc(context)).getBlock();
         } else if ((value instanceof IRubyObject) && ((IRubyObject)value).isNil()) {
             block = Block.NULL_BLOCK;
         } else if (value instanceof IRubyObject) {
             block = ((RubyProc) TypeConverter.convertToType((IRubyObject) value, context.runtime.getProc(), "to_proc", true)).getBlock();
         } else {
             throw new RuntimeException("Unhandled case in CallInstr:prepareBlock.  Got block arg: " + value);
         }
         return block;
     }
 
     public static void checkArity(ThreadContext context, StaticScope scope, Object[] args, int required, int opt, boolean rest,
                                   boolean receivesKwargs, int restKey, Block.Type blockType) {
         int argsLength = args.length;
         RubyHash keywordArgs = extractKwargsHash(args, required, receivesKwargs);
 
         if (restKey == -1 && keywordArgs != null) checkForExtraUnwantedKeywordArgs(context, scope, keywordArgs);
 
         // keyword arguments value is not used for arity checking.
         if (keywordArgs != null) argsLength -= 1;
 
         if ((blockType == null || blockType.checkArity) && (argsLength < required || (!rest && argsLength > (required + opt)))) {
             Arity.raiseArgumentError(context.runtime, argsLength, required, required + opt);
         }
     }
 
     public static IRubyObject[] frobnicateKwargsArgument(ThreadContext context, IRubyObject[] args, int requiredArgsCount) {
         if (args.length <= requiredArgsCount) return args; // No kwarg because required args slurp them up.
 
         final IRubyObject kwargs = toHash(args[args.length - 1], context);
 
         if (kwargs != null) {
             if (kwargs.isNil()) { // nil on to_hash is supposed to keep itself as real value so we need to make kwargs hash
                 return ArraySupport.newCopy(args, RubyHash.newSmallHash(context.runtime));
             }
 
             Tuple<RubyHash, RubyHash> hashes = new Tuple<>(RubyHash.newSmallHash(context.runtime), RubyHash.newSmallHash(context.runtime));
 
             // We know toHash makes null, nil, or Hash
             ((RubyHash) kwargs).visitAll(context, DivvyKeywordsVisitor, hashes);
 
             if (!hashes.b.isEmpty()) { // rest args exists too expand args
                 IRubyObject[] newArgs = new IRubyObject[args.length + 1];
                 System.arraycopy(args, 0, newArgs, 0, args.length);
                 args = newArgs;
                 args[args.length - 2] = hashes.b; // opt args
             }
             args[args.length - 1] = hashes.a; // kwargs hash
         }
 
         return args;
     }
 
     private static final RubyHash.VisitorWithState<Tuple<RubyHash, RubyHash>> DivvyKeywordsVisitor = new RubyHash.VisitorWithState<Tuple<RubyHash, RubyHash>>() {
         @Override
         public void visit(ThreadContext context, RubyHash self, IRubyObject key, IRubyObject value, int index, Tuple<RubyHash, RubyHash> hashes) {
             if (key instanceof RubySymbol) {
                 hashes.a.op_aset(context, key, value);
             } else {
                 hashes.b.op_aset(context, key, value);
             }
         }
     };
 
     private static IRubyObject toHash(IRubyObject lastArg, ThreadContext context) {
         if (lastArg instanceof RubyHash) return (RubyHash) lastArg;
         if (lastArg.respondsTo("to_hash")) {
             if ( context == null ) context = lastArg.getRuntime().getCurrentContext();
             lastArg = lastArg.callMethod(context, "to_hash");
             if (lastArg.isNil()) return lastArg;
             TypeConverter.checkType(context, lastArg, context.runtime.getHash());
             return (RubyHash) lastArg;
         }
         return null;
     }
 
     public static RubyHash extractKwargsHash(Object[] args, int requiredArgsCount, boolean receivesKwargs) {
         if (!receivesKwargs) return null;
         if (args.length <= requiredArgsCount) return null; // No kwarg because required args slurp them up.
 
         Object lastArg = args[args.length - 1];
 
         if (lastArg instanceof IRubyObject) {
             IRubyObject returnValue = toHash((IRubyObject) lastArg, null);
             if (returnValue instanceof RubyHash) return (RubyHash) returnValue;
         }
 
         return null;
     }
 
     public static void checkForExtraUnwantedKeywordArgs(ThreadContext context, final StaticScope scope, RubyHash keywordArgs) {
         keywordArgs.visitAll(context, CheckUnwantedKeywordsVisitor, scope);
     }
 
     private static final RubyHash.VisitorWithState<StaticScope> CheckUnwantedKeywordsVisitor = new RubyHash.VisitorWithState<StaticScope>() {
         @Override
         public void visit(ThreadContext context, RubyHash self, IRubyObject key, IRubyObject value, int index, StaticScope scope) {
             if (!scope.keywordExists(key.asJavaString())) {
                 throw context.runtime.newArgumentError("unknown keyword: " + key.asJavaString());
             }
         }
     };
 
     public static IRubyObject match3(ThreadContext context, RubyRegexp regexp, IRubyObject argValue) {
         if (argValue instanceof RubyString) {
             return regexp.op_match19(context, argValue);
         } else {
             return argValue.callMethod(context, "=~", regexp);
         }
     }
 
     public static IRubyObject extractOptionalArgument(RubyArray rubyArray, int minArgsLength, int index) {
         int n = rubyArray.getLength();
         return minArgsLength < n ? rubyArray.entry(index) : UndefinedValue.UNDEFINED;
     }
 
     @JIT
     public static IRubyObject isDefinedBackref(ThreadContext context) {
         return RubyMatchData.class.isInstance(context.getBackRef()) ?
                 context.runtime.getDefinedMessage(DefinedMessage.GLOBAL_VARIABLE) : context.nil;
     }
 
     @JIT
     public static IRubyObject isDefinedGlobal(ThreadContext context, String name) {
         return context.runtime.getGlobalVariables().isDefined(name) ?
                 context.runtime.getDefinedMessage(DefinedMessage.GLOBAL_VARIABLE) : context.nil;
     }
 
     // FIXME: This checks for match data differently than isDefinedBackref.  Seems like they should use same mechanism?
     @JIT
     public static IRubyObject isDefinedNthRef(ThreadContext context, int matchNumber) {
         IRubyObject backref = context.getBackRef();
 
         if (backref instanceof RubyMatchData) {
             if (!((RubyMatchData) backref).group(matchNumber).isNil()) {
                 return context.runtime.getDefinedMessage(DefinedMessage.GLOBAL_VARIABLE);
             }
         }
 
         return context.nil;
     }
 
     @JIT
     public static IRubyObject isDefinedClassVar(ThreadContext context, RubyModule receiver, String name) {
         boolean defined = receiver.isClassVarDefined(name);
 
         if (!defined && receiver.isSingleton()) { // Look for class var in singleton if it is one.
             IRubyObject attached = ((MetaClass) receiver).getAttached();
 
             if (attached instanceof RubyModule) defined = ((RubyModule) attached).isClassVarDefined(name);
         }
 
         return defined ? context.runtime.getDefinedMessage(DefinedMessage.CLASS_VARIABLE) : context.nil;
     }
 
     @JIT
     public static IRubyObject isDefinedInstanceVar(ThreadContext context, IRubyObject receiver, String name) {
         return receiver.getInstanceVariables().hasInstanceVariable(name) ?
                 context.runtime.getDefinedMessage(DefinedMessage.INSTANCE_VARIABLE) : context.nil;
     }
 
     @JIT
     public static IRubyObject isDefinedCall(ThreadContext context, IRubyObject self, IRubyObject receiver, String name) {
         RubyString boundValue = Helpers.getDefinedCall(context, self, receiver, name);
 
         return boundValue == null ? context.nil : boundValue;
     }
 
     @JIT
     public static IRubyObject isDefinedConstantOrMethod(ThreadContext context, IRubyObject receiver, String name) {
         RubyString definedType = Helpers.getDefinedConstantOrBoundMethod(receiver, name);
 
         return definedType == null ? context.nil : definedType;
     }
 
     @JIT
     public static IRubyObject isDefinedMethod(ThreadContext context, IRubyObject receiver, String name, boolean checkIfPublic) {
         DynamicMethod method = receiver.getMetaClass().searchMethod(name);
 
         boolean defined = !method.isUndefined();
 
         if (defined) {
             // If we find the method we optionally check if it is public before returning "method".
             defined = !checkIfPublic || method.getVisibility() == Visibility.PUBLIC;
         } else {
             // If we did not find the method, check respond_to_missing?
             defined = receiver.respondsToMissing(name, checkIfPublic);
         }
 
         if (defined) return context.runtime.getDefinedMessage(DefinedMessage.METHOD);
 
         return context.nil;
     }
 
     @JIT
     public static IRubyObject isDefinedSuper(ThreadContext context, IRubyObject receiver) {
         boolean flag = false;
         String frameName = context.getFrameName();
 
         if (frameName != null) {
             RubyModule frameClass = context.getFrameKlazz();
             if (frameClass != null) {
                 flag = Helpers.findImplementerIfNecessary(receiver.getMetaClass(), frameClass).getSuperClass().isMethodBound(frameName, false);
             }
         }
         return flag ? context.runtime.getDefinedMessage(DefinedMessage.SUPER) : context.nil;
     }
 
     public static IRubyObject nthMatch(ThreadContext context, int matchNumber) {
         return RubyRegexp.nth_match(matchNumber, context.getBackRef());
     }
 
     public static void defineAlias(ThreadContext context, IRubyObject self, DynamicScope currDynScope, String newNameString, String oldNameString) {
         if (self == null || self instanceof RubyFixnum || self instanceof RubySymbol) {
             throw context.runtime.newTypeError("no class to make alias");
         }
 
         RubyModule module = findInstanceMethodContainer(context, currDynScope, self);
         module.defineAlias(newNameString, oldNameString);
         module.callMethod(context, "method_added", context.runtime.newSymbol(newNameString));
     }
 
     public static RubyModule getModuleFromScope(ThreadContext context, StaticScope scope, IRubyObject arg) {
         Ruby runtime = context.runtime;
         RubyModule rubyClass = scope.getModule();
 
         // SSS FIXME: Copied from ASTInterpreter.getClassVariableBase and adapted
         while (scope != null && (rubyClass.isSingleton() || rubyClass == runtime.getDummy())) {
             scope = scope.getPreviousCRefScope();
             rubyClass = scope.getModule();
             if (scope.getPreviousCRefScope() == null) {
                 runtime.getWarnings().warn(IRubyWarnings.ID.CVAR_FROM_TOPLEVEL_SINGLETON_METHOD, "class variable access from toplevel singleton method");
             }
         }
 
         if ((scope == null) && (arg != null)) {
             // We ran out of scopes to check -- look in arg's metaclass
             rubyClass = arg.getMetaClass();
         }
 
         if (rubyClass == null) {
             throw context.runtime.newTypeError("no class/module to define class variable");
         }
 
         return rubyClass;
     }
 
     @JIT @Interp
     public static IRubyObject mergeKeywordArguments(ThreadContext context, IRubyObject restKwarg, IRubyObject explicitKwarg) {
         RubyHash hash = (RubyHash) TypeConverter.checkHashType(context.runtime, restKwarg).dup();
 
         hash.modify();
         final RubyHash otherHash = explicitKwarg.convertToHash();
 
         if (otherHash.empty_p().isTrue()) return hash;
 
         otherHash.visitAll(context, new KwargMergeVisitor(hash), Block.NULL_BLOCK);
 
         return hash;
     }
 
     private static class KwargMergeVisitor extends RubyHash.VisitorWithState<Block> {
         final RubyHash target;
 
         KwargMergeVisitor(RubyHash target) {
             this.target = target;
         }
 
         @Override
         public void visit(ThreadContext context, RubyHash self, IRubyObject key, IRubyObject value, int index, Block block) {
             // All kwargs keys must be symbols.
             TypeConverter.checkType(context, key, context.runtime.getSymbol());
 
             target.op_aset(context, key, value);
         }
     }
 
     @JIT
     public static IRubyObject restoreExceptionVar(ThreadContext context, IRubyObject exc, IRubyObject savedExc) {
         if (exc instanceof IRReturnJump || exc instanceof IRBreakJump) {
             context.runtime.getGlobalVariables().set("$!", savedExc);
         }
         return null;
     }
 
     public static RubyModule findInstanceMethodContainer(ThreadContext context, DynamicScope currDynScope, IRubyObject self) {
         boolean inBindingEval = currDynScope.inBindingEval();
 
         // Top-level-scripts are special but, not if binding-evals are in force!
         if (!inBindingEval && self == context.runtime.getTopSelf()) return self.getType();
 
         for (DynamicScope ds = currDynScope; ds != null; ) {
             IRScopeType scopeType = ds.getStaticScope().getScopeType();
             switch (ds.getEvalType()) {
                 // The most common use case in the MODULE_EVAL case is:
                 //   a method is defined inside a closure
                 //   that is nested inside a module_eval.
                 //   here self = the module
                 // In the rare case where it is not (looks like it can
                 // happen in some testing frameworks), we have to add
                 // the method to self itself => its metaclass.
                 //
                 // SSS FIXME: Looks like this rare case happens when
                 // the closure is used in a "define_method &block" scenario
                 // => in reality the scope is not a closure but an
                 // instance_method. So, when we fix define_method implementation
                 // to actually convert blocks to real instance_method scopes,
                 // we will not have this edge case since the code will then
                 // be covered by the (scopeType == IRScopeType.INSTANCE_METHOD)
                 // scenario below. Whenever we get to fixing define_method
                 // implementation, we should rip out this code here.
                 //
                 // Verify that this test runs:
                 // -------------
                 //   require "minitest/autorun"
                 //
                 //   describe "A" do
                 //     it "should do something" do
                 //       def foo
                 //       end
                 //     end
                 //   end
                 // -------------
                 case MODULE_EVAL  : return self instanceof RubyModule ? (RubyModule) self : self.getMetaClass();
                 case INSTANCE_EVAL: return self.getSingletonClass();
                 case BINDING_EVAL : ds = ds.getParentScope(); break;
                 case NONE:
                     if (scopeType == null || scopeType.isClosureType()) {
                         // Walk up the dynscope hierarchy
                         ds = ds.getParentScope();
                     } else if (inBindingEval) {
                         // Binding evals are special!
                         return ds.getStaticScope().getModule();
                     } else {
                         switch (scopeType) {
                             case CLASS_METHOD:
                             case MODULE_BODY:
                             case CLASS_BODY:
                             case METACLASS_BODY:
                                 // This is a similar scenario as the FIXME above that was added
                                 // in b65a5842ecf56ca32edc2a17800968f021b6a064. At that time,
                                 // I was wondering if it would affect this site here and looks
                                 // like it does.
                                 return self instanceof RubyModule ? (RubyModule) self : self.getMetaClass();
 
                             case INSTANCE_METHOD:
                             case SCRIPT_BODY:
                                 return self.getMetaClass();
 
                             default:
                                 throw new RuntimeException("Should not get here! scopeType is " + scopeType);
                         }
                     }
                     break;
             }
         }
 
         throw new RuntimeException("Should not get here!");
     }
 
     public static RubyBoolean isBlockGiven(ThreadContext context, Object blk) {
         if (blk instanceof RubyProc) blk = ((RubyProc) blk).getBlock();
         if (blk instanceof RubyNil) blk = Block.NULL_BLOCK;
         return context.runtime.newBoolean( ((Block) blk).isGiven() );
     }
 
     public static IRubyObject receiveRestArg(ThreadContext context, Object[] args, int required, int argIndex, boolean acceptsKeywordArguments) {
         RubyHash keywordArguments = extractKwargsHash(args, required, acceptsKeywordArguments);
         return constructRestArg(context, args, keywordArguments, required, argIndex);
     }
 
     public static IRubyObject receiveRestArg(ThreadContext context, IRubyObject[] args, int required, int argIndex, boolean acceptsKeywordArguments) {
         RubyHash keywordArguments = extractKwargsHash(args, required, acceptsKeywordArguments);
         return constructRestArg(context, args, keywordArguments, required, argIndex);
     }
 
     public static IRubyObject constructRestArg(ThreadContext context, Object[] args, RubyHash keywordArguments, int required, int argIndex) {
         int argsLength = keywordArguments != null ? args.length - 1 : args.length;
         int remainingArguments = argsLength - required;
 
         if (remainingArguments <= 0) return context.runtime.newEmptyArray();
 
         return RubyArray.newArrayMayCopy(context.runtime, (IRubyObject[]) args, argIndex, remainingArguments);
     }
 
     private static IRubyObject constructRestArg(ThreadContext context, IRubyObject[] args, RubyHash keywordArguments, int required, int argIndex) {
         int argsLength = keywordArguments != null ? args.length - 1 : args.length;
         if ( required == 0 && argsLength == args.length ) {
             return RubyArray.newArray(context.runtime, args);
         }
         int remainingArguments = argsLength - required;
 
         if (remainingArguments <= 0) return context.runtime.newEmptyArray();
 
         return RubyArray.newArrayMayCopy(context.runtime, args, argIndex, remainingArguments);
     }
 
-    @JIT
-    public static IRubyObject receivePostReqdArg(ThreadContext context, IRubyObject[] args, int preReqdArgsCount, int postReqdArgsCount, int argIndex, boolean acceptsKeywordArgument) {
-        boolean kwargs = extractKwargsHash(args, preReqdArgsCount + postReqdArgsCount, acceptsKeywordArgument) != null;
+    @JIT @Interp
+    public static IRubyObject receivePostReqdArg(ThreadContext context, IRubyObject[] args, int pre,
+                                                 int opt, boolean rest, int post,
+                                                 int argIndex, boolean acceptsKeywordArgument) {
+        int required = pre + post;
+        // FIXME: Once we extract kwargs from rest of args processing we can delete this extract and n calc.
+        boolean kwargs = extractKwargsHash(args, required, acceptsKeywordArgument) != null;
         int n = kwargs ? args.length - 1 : args.length;
-        int remaining = n - preReqdArgsCount;
-        if (remaining <= argIndex) return context.nil;
+        int remaining = n - pre;       // we know we have received all pre args by post receives.
+
+        if (remaining < post) {        // less args available than post args need
+            if (pre + argIndex >= n) { // argument is past end of arg list
+                return context.nil;
+            } else {
+                return args[pre + argIndex];
+            }
+        }
 
-        return (remaining > postReqdArgsCount) ? args[n - postReqdArgsCount + argIndex] : args[preReqdArgsCount + argIndex];
+        // At this point we know we have enough arguments left for post without worrying about AIOOBE.
+
+        if (rest) {                      // we can read from back since we will take all args we can get.
+            return args[n - post + argIndex];
+        } else if (n > required + opt) { // we filled all opt so we can read from front (and avoid excess args case from proc).
+            return args[pre + opt + argIndex];
+        } else {                         // partial opts filled in too few args so we can read from end.
+            return args[n - post + argIndex];
+        }
     }
 
     public static IRubyObject receiveOptArg(IRubyObject[] args, int requiredArgs, int preArgs, int argIndex, boolean acceptsKeywordArgument) {
         int optArgIndex = argIndex;  // which opt arg we are processing? (first one has index 0, second 1, ...).
         RubyHash keywordArguments = extractKwargsHash(args, requiredArgs, acceptsKeywordArgument);
         int argsLength = keywordArguments != null ? args.length - 1 : args.length;
 
         if (requiredArgs + optArgIndex >= argsLength) return UndefinedValue.UNDEFINED; // No more args left
 
         return args[preArgs + optArgIndex];
     }
 
     public static IRubyObject getPreArgSafe(ThreadContext context, IRubyObject[] args, int argIndex) {
         IRubyObject result;
         result = argIndex < args.length ? args[argIndex] : context.nil; // SSS FIXME: This check is only required for closures, not methods
         return result;
     }
 
     public static IRubyObject receiveKeywordArg(ThreadContext context, IRubyObject[] args, int required, String argName, boolean acceptsKeywordArgument) {
         RubyHash keywordArguments = extractKwargsHash(args, required, acceptsKeywordArgument);
 
         if (keywordArguments == null) return UndefinedValue.UNDEFINED;
 
         RubySymbol keywordName = context.runtime.newSymbol(argName);
 
         if (keywordArguments.fastARef(keywordName) == null) return UndefinedValue.UNDEFINED;
 
         // SSS FIXME: Can we use an internal delete here?
         // Enebo FIXME: Delete seems wrong if we are doing this for duplication purposes.
         return keywordArguments.delete(context, keywordName, Block.NULL_BLOCK);
     }
 
     public static IRubyObject receiveKeywordRestArg(ThreadContext context, IRubyObject[] args, int required, boolean keywordArgumentSupplied) {
         RubyHash keywordArguments = extractKwargsHash(args, required, keywordArgumentSupplied);
 
         return keywordArguments == null ? RubyHash.newSmallHash(context.runtime) : keywordArguments;
     }
 
     public static IRubyObject setCapturedVar(ThreadContext context, IRubyObject matchRes, String varName) {
         IRubyObject val;
         if (matchRes.isNil()) {
             val = context.nil;
         } else {
             IRubyObject backref = context.getBackRef();
             int n = ((RubyMatchData)backref).getNameToBackrefNumber(varName);
             val = RubyRegexp.nth_match(n, backref);
         }
 
         return val;
     }
 
     @JIT // for JVM6
     public static IRubyObject instanceSuperSplatArgs(ThreadContext context, IRubyObject self, String methodName, RubyModule definingModule, IRubyObject[] args, Block block, boolean[] splatMap) {
         return instanceSuper(context, self, methodName, definingModule, splatArguments(args, splatMap), block);
     }
 
     @Interp
     public static IRubyObject instanceSuper(ThreadContext context, IRubyObject self, String methodName, RubyModule definingModule, IRubyObject[] args, Block block) {
         RubyClass superClass = definingModule.getMethodLocation().getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(methodName) : UndefinedMethod.INSTANCE;
         IRubyObject rVal = method.isUndefined() ?
                 Helpers.callMethodMissing(context, self, method.getVisibility(), methodName, CallType.SUPER, args, block)
                 : method.call(context, self, superClass, methodName, args, block);
         return rVal;
     }
 
     @JIT // for JVM6
     public static IRubyObject classSuperSplatArgs(ThreadContext context, IRubyObject self, String methodName, RubyModule definingModule, IRubyObject[] args, Block block, boolean[] splatMap) {
         return classSuper(context, self, methodName, definingModule, splatArguments(args, splatMap), block);
     }
 
     @Interp
     public static IRubyObject classSuper(ThreadContext context, IRubyObject self, String methodName, RubyModule definingModule, IRubyObject[] args, Block block) {
         RubyClass superClass = definingModule.getMetaClass().getMethodLocation().getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(methodName) : UndefinedMethod.INSTANCE;
         IRubyObject rVal = method.isUndefined() ?
             Helpers.callMethodMissing(context, self, method.getVisibility(), methodName, CallType.SUPER, args, block)
                 : method.call(context, self, superClass, methodName, args, block);
         return rVal;
     }
 
     public static IRubyObject unresolvedSuperSplatArgs(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block, boolean[] splatMap) {
         return unresolvedSuper(context, self, splatArguments(args, splatMap), block);
     }
 
     public static IRubyObject unresolvedSuper(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
 
         // We have to rely on the frame stack to find the implementation class
         RubyModule klazz = context.getFrameKlazz();
         String methodName = context.getCurrentFrame().getName();
 
         Helpers.checkSuperDisabledOrOutOfMethod(context, klazz, methodName);
         RubyModule implMod = Helpers.findImplementerIfNecessary(self.getMetaClass(), klazz);
         RubyClass superClass = implMod.getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(methodName) : UndefinedMethod.INSTANCE;
 
         IRubyObject rVal;
         if (method.isUndefined()) {
             rVal = Helpers.callMethodMissing(context, self, method.getVisibility(), methodName, CallType.SUPER, args, block);
         } else {
             rVal = method.call(context, self, superClass, methodName, args, block);
         }
 
         return rVal;
     }
 
     public static IRubyObject zSuperSplatArgs(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block, boolean[] splatMap) {
         if (block == null || !block.isGiven()) block = context.getFrameBlock();
         return unresolvedSuper(context, self, splatArguments(args, splatMap), block);
     }
 
     public static IRubyObject zSuper(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         if (block == null || !block.isGiven()) block = context.getFrameBlock();
         return unresolvedSuper(context, self, args, block);
     }
 
     public static IRubyObject[] splatArguments(IRubyObject[] args, boolean[] splatMap) {
         if (splatMap != null && splatMap.length > 0) {
             int count = 0;
             for (int i = 0; i < splatMap.length; i++) {
                 count += splatMap[i] ? ((RubyArray)args[i]).size() : 1;
             }
 
             IRubyObject[] newArgs = new IRubyObject[count];
             int actualOffset = 0;
             for (int i = 0; i < splatMap.length; i++) {
                 if (splatMap[i]) {
                     RubyArray ary = (RubyArray) args[i];
                     for (int j = 0; j < ary.size(); j++) {
                         newArgs[actualOffset++] = ary.eltOk(j);
                     }
                 } else {
                     newArgs[actualOffset++] = args[i];
                 }
             }
 
             args = newArgs;
         }
         return args;
     }
 
     public static String encodeSplatmap(boolean[] splatmap) {
         if (splatmap == null) return "";
         StringBuilder builder = new StringBuilder();
         for (boolean b : splatmap) {
             builder.append(b ? '1' : '0');
         }
         return builder.toString();
     }
 
     public static boolean[] decodeSplatmap(String splatmapString) {
         boolean[] splatMap;
         if (splatmapString.length() > 0) {
             splatMap = new boolean[splatmapString.length()];
 
             for (int i = 0; i < splatmapString.length(); i++) {
                 if (splatmapString.charAt(i) == '1') {
                     splatMap[i] = true;
                 }
             }
         } else {
             splatMap = null;
         }
         return splatMap;
     }
 
     public static boolean[] buildSplatMap(Operand[] args) {
         boolean[] splatMap = null;
 
         for (int i = 0; i < args.length; i++) {
             Operand operand = args[i];
             if (operand instanceof Splat) {
                 if (splatMap == null) splatMap = new boolean[args.length];
                 splatMap[i] = true;
             }
         }
 
         return splatMap;
     }
 
     public static boolean anyTrue(boolean[] booleans) {
         for (boolean b : booleans) if (b) return true;
         return false;
     }
 
     public static boolean needsSplatting(boolean[] splatmap) {
         return splatmap != null && splatmap.length > 0 && anyTrue(splatmap);
     }
 
     public static final Type[] typesFromSignature(Signature signature) {
         Type[] types = new Type[signature.argCount()];
         for (int i = 0; i < signature.argCount(); i++) {
             types[i] = Type.getType(signature.argType(i));
         }
         return types;
     }
 
     @JIT
     public static RubyString newFrozenStringFromRaw(ThreadContext context, String str, String encoding, int cr, String file, int line) {
         return newFrozenString(context, newByteListFromRaw(context.runtime, str, encoding), cr, file, line);
     }
 
     @JIT
     public static final ByteList newByteListFromRaw(Ruby runtime, String str, String encoding) {
         return new ByteList(str.getBytes(RubyEncoding.ISO), runtime.getEncodingService().getEncodingFromString(encoding), false);
     }
 
     @JIT
     public static RubyEncoding retrieveEncoding(ThreadContext context, String name) {
         return context.runtime.getEncodingService().getEncoding(retrieveJCodingsEncoding(context, name));
     }
 
     @JIT
     public static Encoding retrieveJCodingsEncoding(ThreadContext context, String name) {
         return context.runtime.getEncodingService().findEncodingOrAliasEntry(name.getBytes()).getEncoding();
     }
 
     @JIT
     public static RubyHash constructHashFromArray(Ruby runtime, IRubyObject[] pairs) {
         int length = pairs.length / 2;
         boolean useSmallHash = length <= 10;
 
         RubyHash hash = useSmallHash ? RubyHash.newHash(runtime) : RubyHash.newSmallHash(runtime);
         for (int i = 0; i < pairs.length;) {
             if (useSmallHash) {
                 hash.fastASetSmall(runtime, pairs[i++], pairs[i++], true);
             } else {
                 hash.fastASet(runtime, pairs[i++], pairs[i++], true);
             }
 
         }
         return hash;
     }
 
     @JIT
     public static RubyHash dupKwargsHashAndPopulateFromArray(ThreadContext context, RubyHash dupHash, IRubyObject[] pairs) {
         Ruby runtime = context.runtime;
         RubyHash hash = dupHash.dupFast(context);
         for (int i = 0; i < pairs.length;) {
             hash.fastASet(runtime, pairs[i++], pairs[i++], true);
         }
         return hash;
     }
 
     @JIT
     public static IRubyObject searchConst(ThreadContext context, StaticScope staticScope, String constName, boolean noPrivateConsts) {
         RubyModule object = context.runtime.getObject();
         IRubyObject constant = (staticScope == null) ? object.getConstant(constName) : staticScope.getConstantInner(constName);
 
         // Inheritance lookup
         RubyModule module = null;
         if (constant == null) {
             // SSS FIXME: Is this null check case correct?
             module = staticScope == null ? object : staticScope.getModule();
             constant = noPrivateConsts ? module.getConstantFromNoConstMissing(constName, false) : module.getConstantNoConstMissing(constName);
         }
 
         // Call const_missing or cache
         if (constant == null) {
             return module.callMethod(context, "const_missing", context.runtime.fastNewSymbol(constName));
         }
 
         return constant;
     }
 
     @JIT
     public static IRubyObject inheritedSearchConst(ThreadContext context, IRubyObject cmVal, String constName, boolean noPrivateConsts) {
         RubyModule module;
         if (cmVal instanceof RubyModule) {
             module = (RubyModule) cmVal;
         } else {
             throw context.runtime.newTypeError(cmVal + " is not a type/class");
         }
 
         IRubyObject constant = noPrivateConsts ? module.getConstantFromNoConstMissing(constName, false) : module.getConstantNoConstMissing(constName);
 
         if (constant == null) {
             constant = UndefinedValue.UNDEFINED;
         }
 
         return constant;
     }
 
     @JIT
     public static IRubyObject lexicalSearchConst(ThreadContext context, StaticScope staticScope, String constName) {
         IRubyObject constant = staticScope.getConstantInner(constName);
 
         if (constant == null) {
             constant = UndefinedValue.UNDEFINED;
         }
 
         return constant;
     }
 
     public static IRubyObject setInstanceVariable(IRubyObject self, IRubyObject value, String name) {
         return self.getInstanceVariables().setInstanceVariable(name, value);
     }
 
     /**
      * Construct a new DynamicMethod to wrap the given IRModuleBody and singletonizable object. Used by interpreter.
      */
     @Interp
     public static DynamicMethod newInterpretedMetaClass(Ruby runtime, IRScope metaClassBody, IRubyObject obj) {
         RubyClass singletonClass = newMetaClassFromIR(runtime, metaClassBody, obj);
 
         return new InterpretedIRMetaClassBody(metaClassBody, singletonClass);
     }
 
     /**
      * Construct a new DynamicMethod to wrap the given IRModuleBody and singletonizable object. Used by JIT.
      */
     @JIT
     public static DynamicMethod newCompiledMetaClass(ThreadContext context, MethodHandle handle, IRScope metaClassBody, IRubyObject obj) {
         RubyClass singletonClass = newMetaClassFromIR(context.runtime, metaClassBody, obj);
 
         return new CompiledIRMetaClassBody(handle, metaClassBody, singletonClass);
     }
 
     private static RubyClass newMetaClassFromIR(Ruby runtime, IRScope metaClassBody, IRubyObject obj) {
         RubyClass singletonClass = Helpers.getSingletonClass(runtime, obj);
 
         StaticScope metaClassScope = metaClassBody.getStaticScope();
 
         metaClassScope.setModule(singletonClass);
         return singletonClass;
     }
 
     /**
      * Construct a new DynamicMethod to wrap the given IRModuleBody and singletonizable object. Used by interpreter.
      */
     @Interp
     public static DynamicMethod newInterpretedModuleBody(ThreadContext context, IRScope irModule, Object rubyContainer) {
         RubyModule newRubyModule = newRubyModuleFromIR(context, irModule, rubyContainer);
         return new InterpretedIRBodyMethod(irModule, newRubyModule);
     }
 
     @JIT
     public static DynamicMethod newCompiledModuleBody(ThreadContext context, MethodHandle handle, IRScope irModule, Object rubyContainer) {
         RubyModule newRubyModule = newRubyModuleFromIR(context, irModule, rubyContainer);
         return new CompiledIRMethod(handle, irModule, Visibility.PUBLIC, newRubyModule, false);
     }
 
     private static RubyModule newRubyModuleFromIR(ThreadContext context, IRScope irModule, Object rubyContainer) {
         if (!(rubyContainer instanceof RubyModule)) {
             throw context.runtime.newTypeError("no outer class/module");
         }
 
         RubyModule newRubyModule = ((RubyModule) rubyContainer).defineOrGetModuleUnder(irModule.getName());
         irModule.getStaticScope().setModule(newRubyModule);
         return newRubyModule;
     }
 
     @Interp
     public static DynamicMethod newInterpretedClassBody(ThreadContext context, IRScope irClassBody, Object container, Object superClass) {
         RubyModule newRubyClass = newRubyClassFromIR(context.runtime, irClassBody, superClass, container);
 
         return new InterpretedIRBodyMethod(irClassBody, newRubyClass);
     }
 
     @JIT
     public static DynamicMethod newCompiledClassBody(ThreadContext context, MethodHandle handle, IRScope irClassBody, Object container, Object superClass) {
         RubyModule newRubyClass = newRubyClassFromIR(context.runtime, irClassBody, superClass, container);
 
         return new CompiledIRMethod(handle, irClassBody, Visibility.PUBLIC, newRubyClass, false);
     }
 
     public static RubyModule newRubyClassFromIR(Ruby runtime, IRScope irClassBody, Object superClass, Object container) {
         if (!(container instanceof RubyModule)) {
             throw runtime.newTypeError("no outer class/module");
         }
 
         RubyModule newRubyClass;
 
         if (irClassBody instanceof IRMetaClassBody) {
             newRubyClass = ((RubyModule)container).getMetaClass();
         } else {
             RubyClass sc;
             if (superClass == UndefinedValue.UNDEFINED) {
                 sc = null;
             } else {
                 RubyClass.checkInheritable((IRubyObject) superClass);
 
                 sc = (RubyClass) superClass;
             }
 
             newRubyClass = ((RubyModule)container).defineOrGetClassUnder(irClassBody.getName(), sc);
         }
 
         irClassBody.getStaticScope().setModule(newRubyClass);
         return newRubyClass;
     }
 
     @Interp
     public static void defInterpretedClassMethod(ThreadContext context, IRScope method, IRubyObject obj) {
         RubyClass rubyClass = checkClassForDef(context, method, obj);
 
         DynamicMethod newMethod;
         if (context.runtime.getInstanceConfig().getCompileMode() == RubyInstanceConfig.CompileMode.OFF) {
             newMethod = new InterpretedIRMethod(method, Visibility.PUBLIC, rubyClass);
         } else {
             newMethod = new MixedModeIRMethod(method, Visibility.PUBLIC, rubyClass);
         }
         // FIXME: needs checkID and proper encoding to force hard symbol
         rubyClass.addMethod(method.getName(), newMethod);
         if (!rubyClass.isRefinement()) {
             obj.callMethod(context, "singleton_method_added", context.runtime.fastNewSymbol(method.getName()));
         }
     }
 
     @JIT
     public static void defCompiledClassMethod(ThreadContext context, MethodHandle handle, IRScope method, IRubyObject obj) {
         RubyClass rubyClass = checkClassForDef(context, method, obj);
 
         // FIXME: needs checkID and proper encoding to force hard symbol
         rubyClass.addMethod(method.getName(), new CompiledIRMethod(handle, method, Visibility.PUBLIC, rubyClass, method.receivesKeywordArgs()));
         if (!rubyClass.isRefinement()) {
             // FIXME: needs checkID and proper encoding to force hard symbol
             obj.callMethod(context, "singleton_method_added", context.runtime.fastNewSymbol(method.getName()));
         }
     }
 
     @JIT
     public static void defCompiledClassMethod(ThreadContext context, MethodHandle variable, MethodHandle specific, int specificArity, IRScope method, IRubyObject obj) {
         RubyClass rubyClass = checkClassForDef(context, method, obj);
 
         // FIXME: needs checkID and proper encoding to force hard symbol
         rubyClass.addMethod(method.getName(), new CompiledIRMethod(variable, specific, specificArity, method, Visibility.PUBLIC, rubyClass, method.receivesKeywordArgs()));
         if (!rubyClass.isRefinement()) {
             // FIXME: needs checkID and proper encoding to force hard symbol
             obj.callMethod(context, "singleton_method_added", context.runtime.fastNewSymbol(method.getName()));
         }
     }
 
     private static RubyClass checkClassForDef(ThreadContext context, IRScope method, IRubyObject obj) {
         if (obj instanceof RubyFixnum || obj instanceof RubySymbol || obj instanceof RubyFloat) {
             throw context.runtime.newTypeError("can't define singleton method \"" + method.getName() + "\" for " + obj.getMetaClass().getBaseName());
         }
 
         // if (obj.isFrozen()) throw context.runtime.newFrozenError("object");
 
         return obj.getSingletonClass();
     }
 
     @Interp
     public static void defInterpretedInstanceMethod(ThreadContext context, IRScope method, DynamicScope currDynScope, IRubyObject self) {
         Ruby runtime = context.runtime;
         RubyModule rubyClass = findInstanceMethodContainer(context, currDynScope, self);
 
         Visibility currVisibility = context.getCurrentVisibility();
         Visibility newVisibility = Helpers.performNormalMethodChecksAndDetermineVisibility(runtime, rubyClass, method.getName(), currVisibility);
 
         DynamicMethod newMethod;
         if (context.runtime.getInstanceConfig().getCompileMode() == RubyInstanceConfig.CompileMode.OFF) {
             newMethod = new InterpretedIRMethod(method, newVisibility, rubyClass);
         } else {
             newMethod = new MixedModeIRMethod(method, newVisibility, rubyClass);
         }
 
         // FIXME: needs checkID and proper encoding to force hard symbol
         Helpers.addInstanceMethod(rubyClass, method.getName(), newMethod, currVisibility, context, runtime);
     }
 
     @JIT
     public static void defCompiledInstanceMethod(ThreadContext context, MethodHandle handle, IRScope method, DynamicScope currDynScope, IRubyObject self) {
         Ruby runtime = context.runtime;
         RubyModule clazz = findInstanceMethodContainer(context, currDynScope, self);
 
         Visibility currVisibility = context.getCurrentVisibility();
         Visibility newVisibility = Helpers.performNormalMethodChecksAndDetermineVisibility(runtime, clazz, method.getName(), currVisibility);
 
         DynamicMethod newMethod = new CompiledIRMethod(handle, method, newVisibility, clazz, method.receivesKeywordArgs());
 
         // FIXME: needs checkID and proper encoding to force hard symbol
         Helpers.addInstanceMethod(clazz, method.getName(), newMethod, currVisibility, context, runtime);
     }
 
     @JIT
     public static void defCompiledInstanceMethod(ThreadContext context, MethodHandle variable, MethodHandle specific, int specificArity, IRScope method, DynamicScope currDynScope, IRubyObject self) {
         Ruby runtime = context.runtime;
         RubyModule clazz = findInstanceMethodContainer(context, currDynScope, self);
 
         Visibility currVisibility = context.getCurrentVisibility();
         Visibility newVisibility = Helpers.performNormalMethodChecksAndDetermineVisibility(runtime, clazz, method.getName(), currVisibility);
 
         DynamicMethod newMethod = new CompiledIRMethod(variable, specific, specificArity, method, newVisibility, clazz, method.receivesKeywordArgs());
 
         // FIXME: needs checkID and proper encoding to force hard symbol
         Helpers.addInstanceMethod(clazz, method.getName(), newMethod, currVisibility, context, runtime);
     }
 
     @JIT
     public static IRubyObject invokeModuleBody(ThreadContext context, DynamicMethod method, Block block) {
         RubyModule implClass = method.getImplementationClass();
 
         return method.call(context, implClass, implClass, "", block);
     }
 
     @JIT
     public static RubyRegexp newDynamicRegexp(ThreadContext context, IRubyObject[] pieces, int embeddedOptions) {
         RegexpOptions options = RegexpOptions.fromEmbeddedOptions(embeddedOptions);
         RubyString pattern = RubyRegexp.preprocessDRegexp(context.runtime, pieces, options);
         RubyRegexp re = RubyRegexp.newDRegexp(context.runtime, pattern, options);
         re.setLiteral();
 
         return re;
     }
 
     @JIT
     public static RubyRegexp newDynamicRegexp(ThreadContext context, IRubyObject arg0, int embeddedOptions) {
         RegexpOptions options = RegexpOptions.fromEmbeddedOptions(embeddedOptions);
         RubyString pattern = RubyRegexp.preprocessDRegexp(context.runtime, arg0, options);
         RubyRegexp re = RubyRegexp.newDRegexp(context.runtime, pattern, options);
         re.setLiteral();
 
         return re;
     }
 
     @JIT
     public static RubyRegexp newDynamicRegexp(ThreadContext context, IRubyObject arg0, IRubyObject arg1, int embeddedOptions) {
         RegexpOptions options = RegexpOptions.fromEmbeddedOptions(embeddedOptions);
         RubyString pattern = RubyRegexp.preprocessDRegexp(context.runtime, arg0, arg1, options);
         RubyRegexp re = RubyRegexp.newDRegexp(context.runtime, pattern, options);
         re.setLiteral();
 
         return re;
     }
 
     @JIT
     public static RubyRegexp newDynamicRegexp(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, int embeddedOptions) {
         RegexpOptions options = RegexpOptions.fromEmbeddedOptions(embeddedOptions);
         RubyString pattern = RubyRegexp.preprocessDRegexp(context.runtime, arg0, arg1, arg2, options);
         RubyRegexp re = RubyRegexp.newDRegexp(context.runtime, pattern, options);
         re.setLiteral();
 
         return re;
     }
 
     @JIT
     public static RubyRegexp newDynamicRegexp(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, int embeddedOptions) {
         RegexpOptions options = RegexpOptions.fromEmbeddedOptions(embeddedOptions);
         RubyString pattern = RubyRegexp.preprocessDRegexp(context.runtime, arg0, arg1, arg2, arg3, options);
         RubyRegexp re = RubyRegexp.newDRegexp(context.runtime, pattern, options);
         re.setLiteral();
 
         return re;
     }
 
     @JIT
     public static RubyRegexp newDynamicRegexp(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, int embeddedOptions) {
         RegexpOptions options = RegexpOptions.fromEmbeddedOptions(embeddedOptions);
         RubyString pattern = RubyRegexp.preprocessDRegexp(context.runtime, arg0, arg1, arg2, arg3, arg4, options);
         RubyRegexp re = RubyRegexp.newDRegexp(context.runtime, pattern, options);
         re.setLiteral();
 
         return re;
     }
 
     public static RubyRegexp newLiteralRegexp(ThreadContext context, ByteList source, RegexpOptions options) {
         RubyRegexp re = RubyRegexp.newRegexp(context.runtime, source, options);
         re.setLiteral();
         return re;
     }
 
     @JIT
     public static RubyRegexp newLiteralRegexp(ThreadContext context, ByteList source, int embeddedOptions) {
         return newLiteralRegexp(context, source, RegexpOptions.fromEmbeddedOptions(embeddedOptions));
     }
 
     @JIT
     public static RubyArray irSplat(ThreadContext context, IRubyObject ary) {
         Ruby runtime = context.runtime;
         IRubyObject tmp = TypeConverter.convertToTypeWithCheck19(context, ary, runtime.getArray(), sites(context).to_a_checked);
         if (tmp.isNil()) {
             tmp = runtime.newArray(ary);
         }
         else if (true /**RTEST(flag)**/) { // this logic is only used for bare splat, and MRI dups
             tmp = ((RubyArray)tmp).aryDup();
         }
         return (RubyArray)tmp;
     }
 
     /**
      * Call to_ary to get Array or die typing.  The optionally dup it if specified.  Some conditional
      * cases in compiler we know we are safe in not-duping.  This method is the same impl as MRIs
      * splatarray instr in the YARV instruction set.
      */
     @JIT @Interp
     public static RubyArray splatArray(ThreadContext context, IRubyObject ary, boolean dupArray) {
         Ruby runtime = context.runtime;
         IRubyObject tmp = TypeConverter.convertToTypeWithCheck19(context, ary, runtime.getArray(), sites(context).to_a_checked);
 
         if (tmp.isNil()) {
             tmp = runtime.newArray(ary);
         } else if (dupArray) {
             tmp = ((RubyArray) tmp).aryDup();
         }
 
         return (RubyArray) tmp;
     }
 
     public static IRubyObject irToAry(ThreadContext context, IRubyObject value) {
         if (!(value instanceof RubyArray)) {
             value = RubyArray.aryToAry(value);
         }
 
         return value;
     }
 
     public static int irReqdArgMultipleAsgnIndex(int n,  int preArgsCount, int index, int postArgsCount) {
         if (preArgsCount == -1) {
             return index < n ? index : -1;
         } else {
             int remaining = n - preArgsCount;
             if (remaining <= index) {
                 return -1;
             } else {
                 return (remaining > postArgsCount) ? n - postArgsCount + index : preArgsCount + index;
             }
         }
     }
 
     public static IRubyObject irReqdArgMultipleAsgn(ThreadContext context, RubyArray rubyArray, int preArgsCount, int index, int postArgsCount) {
         int i = irReqdArgMultipleAsgnIndex(rubyArray.getLength(), preArgsCount, index, postArgsCount);
         return i == -1 ? context.nil : rubyArray.entry(i);
     }
 
     public static IRubyObject irNot(ThreadContext context, IRubyObject obj) {
         return context.runtime.newBoolean(!(obj.isTrue()));
     }
 
     @JIT
     public static RaiseException newRequiredKeywordArgumentError(ThreadContext context, String name) {
         return context.runtime.newArgumentError("missing keyword: " + name);
     }
 
     @JIT
     public static void pushExitBlock(ThreadContext context, Block blk) {
         context.runtime.pushExitBlock(context.runtime.newProc(Block.Type.LAMBDA, blk));
     }
 
     @JIT
     public static FunctionalCachingCallSite newFunctionalCachingCallSite(String name) {
         return new FunctionalCachingCallSite(name);
     }
 
     @JIT
     public static NormalCachingCallSite newNormalCachingCallSite(String name) {
         return new NormalCachingCallSite(name);
     }
 
     @JIT
     public static VariableCachingCallSite newVariableCachingCallSite(String name) {
         return new VariableCachingCallSite(name);
     }
 
     @JIT
     public static RefinedCachingCallSite newRefinedCachingCallSite(String name, String callType) {
         return new RefinedCachingCallSite(name, CallType.valueOf(callType));
     }
 
     @JIT
     public static IRScope decodeScopeFromBytes(Ruby runtime, byte[] scopeBytes, String filename) {
         try {
             return IRReader.load(runtime.getIRManager(), new IRReaderStream(runtime.getIRManager(), new ByteArrayInputStream(scopeBytes), new ByteList(filename.getBytes())));
         } catch (IOException ioe) {
             // should not happen for bytes
             return null;
         }
     }
 
     @JIT
     public static VariableAccessor getVariableAccessorForRead(IRubyObject object, String name) {
         return ((RubyBasicObject)object).getMetaClass().getRealClass().getVariableAccessorForRead(name);
     }
 
     @JIT
     public static VariableAccessor getVariableAccessorForWrite(IRubyObject object, String name) {
         return ((RubyBasicObject)object).getMetaClass().getRealClass().getVariableAccessorForWrite(name);
     }
 
     @JIT
     public static IRubyObject getVariableWithAccessor(IRubyObject self, VariableAccessor accessor, ThreadContext context, String name) {
         IRubyObject result = (IRubyObject)accessor.get(self);
         if (result == null) {
             if (context.runtime.isVerbose()) {
                 context.runtime.getWarnings().warning(IRubyWarnings.ID.IVAR_NOT_INITIALIZED, "instance variable " + name + " not initialized");
             }
             result = context.nil;
         }
         return result;
     }
 
     @JIT
     public static void setVariableWithAccessor(IRubyObject self, IRubyObject value, VariableAccessor accessor) {
         accessor.set(self, value);
     }
 
     @JIT
     public static RubyFixnum getArgScopeDepth(ThreadContext context, StaticScope currScope) {
         int i = 0;
         while (!currScope.isArgumentScope()) {
             currScope = currScope.getEnclosingScope();
             i++;
         }
         return context.runtime.newFixnum(i);
     }
 
     private static IRubyObject[] toAry(ThreadContext context, IRubyObject[] args) {
         if (args.length == 1 && args[0].respondsTo("to_ary")) {
             IRubyObject newAry = Helpers.aryToAry(args[0]);
             if (newAry.isNil()) {
                 args = new IRubyObject[] { args[0] };
             } else if (newAry instanceof RubyArray) {
                 args = ((RubyArray) newAry).toJavaArray();
             } else {
                 throw context.runtime.newTypeError(args[0].getType().getName() + "#to_ary should return Array");
             }
         }
         return args;
     }
 
     private static IRubyObject[] prepareProcArgs(ThreadContext context, Block b, IRubyObject[] args) {
         if (args.length != 1) return args;
 
         // Potentially expand single value if it is an array depending on what we are calling.
         return IRRuntimeHelpers.convertValueIntoArgArray(context, args[0], b.getBody().getSignature(), b.type == Block.Type.NORMAL && args[0] instanceof RubyArray);
     }
 
     private static IRubyObject[] prepareBlockArgsInternal(ThreadContext context, Block block, IRubyObject[] args) {
         if (args == null) {
             args = IRubyObject.NULL_ARRAY;
         }
 
         boolean isProcCall = context.getCurrentBlockType() == Block.Type.PROC;
         org.jruby.runtime.Signature sig = block.getBody().getSignature();
         if (block.type == Block.Type.LAMBDA) {
             if (!isProcCall && sig.arityValue() != -1 && sig.required() != 1) {
                 args = toAry(context, args);
             }
             sig.checkArity(context.runtime, args);
             return args;
         }
 
         if (isProcCall) {
             return prepareProcArgs(context, block, args);
         }
 
         int arityValue = sig.arityValue();
         if (!sig.hasKwargs() && arityValue >= -1 && arityValue <= 1) {
             return args;
         }
 
         // We get here only when we need both required and optional/rest args
         // (keyword or non-keyword in either case).
         // So, convert a single value to an array if possible.
         args = toAry(context, args);
 
         // Deal with keyword args that needs special handling
         int needsKwargs = sig.hasKwargs() ? 1 - sig.getRequiredKeywordForArityCount() : 0;
         int required = sig.required();
         int actual = args.length;
         if (needsKwargs == 0 || required > actual) {
             // Nothing to do if we have fewer args in args than what is required
             // The required arg instructions will return nil in those cases.
             return args;
         }
 
         if (sig.isFixed() && required > 0 && required + needsKwargs != actual) {
             final int len = required + needsKwargs; // Make sure we have a ruby-hash
             IRubyObject[] newArgs = ArraySupport.newCopy(args, len);
             if (actual < len) {
                 // Not enough args and we need an empty {} for kwargs processing.
                 newArgs[len - 1] = RubyHash.newHash(context.runtime);
             } else {
                 // We have more args than we need and kwargs is always the last arg.
                 newArgs[len - 1] = args[args.length - 1];
             }
             args = newArgs;
         }
 
         return args;
     }
 
     /**
      * Check whether incoming args are zero length for a lambda, and no-op for non-lambda.
      *
      * This could probably be simplified to just an arity check with no return value, but returns the
      * incoming args currently for consistency with the other prepares.
      *
      * @param context
      * @param block
      * @param args
      * @return
      */
     @Interp @JIT
     public static IRubyObject[] prepareNoBlockArgs(ThreadContext context, Block block, IRubyObject[] args) {
         if (args == null) {
             args = IRubyObject.NULL_ARRAY;
         }
 
         if (block.type == Block.Type.LAMBDA) {
             block.getSignature().checkArity(context.runtime, args);
         }
 
         return args;
     }
 
     @Interp @JIT
     public static IRubyObject[] prepareSingleBlockArgs(ThreadContext context, Block block, IRubyObject[] args) {
         if (args == null) {
             args = IRubyObject.NULL_ARRAY;
         }
 
         if (block.type == Block.Type.LAMBDA) {
             block.getBody().getSignature().checkArity(context.runtime, args);
             return args;
         }
 
         boolean isProcCall = context.getCurrentBlockType() == Block.Type.PROC;
         if (isProcCall) {
             if (args.length == 0) {
                 args = context.runtime.getSingleNilArray();
             } else if (args.length == 1) {
                 args = prepareProcArgs(context, block, args);
             } else {
                 args = new IRubyObject[] { args[0] };
             }
         }
 
         // If there are insufficient args, ReceivePreReqdInstr will return nil
         return args;
     }
 
     @Interp @JIT
     public static IRubyObject[] prepareFixedBlockArgs(ThreadContext context, Block block, IRubyObject[] args) {
         if (args == null) {
             args = IRubyObject.NULL_ARRAY;
         }
 
         boolean isProcCall = context.getCurrentBlockType() == Block.Type.PROC;
         if (block.type == Block.Type.LAMBDA) {
             org.jruby.runtime.Signature sig = block.getBody().getSignature();
             // We don't need to check for the 1 required arg case here
             // since that goes down the prepareSingleBlockArgs route
             if (!isProcCall && sig.arityValue() != 1) {
                 args = toAry(context, args);
             }
             sig.checkArity(context.runtime, args);
             return args;
         }
 
         if (isProcCall) {
             return prepareProcArgs(context, block, args);
         }
 
         // If we need more than 1 reqd arg, convert a single value to an array if possible.
         // If there are insufficient args, ReceivePreReqdInstr will return nil
         return toAry(context, args);
     }
 
     // This is the placeholder for scenarios not handled by specialized instructions.
     @Interp @JIT
     public static IRubyObject[] prepareBlockArgs(ThreadContext context, Block block, IRubyObject[] args, boolean usesKwArgs) {
         args = prepareBlockArgsInternal(context, block, args);
         if (usesKwArgs) {
             args = frobnicateKwargsArgument(context, args, block.getBody().getSignature().required());
         }
         return args;
     }
 
     private static DynamicScope getNewBlockScope(Block block, boolean pushNewDynScope, boolean reuseParentDynScope) {
         DynamicScope newScope = block.getBinding().getDynamicScope();
         if (pushNewDynScope) return block.allocScope(newScope);
 
         // Reuse! We can avoid the push only if surrounding vars aren't referenced!
         if (reuseParentDynScope) return newScope;
 
         // No change
         return null;
     }
 
     @Interp @JIT
     public static DynamicScope pushBlockDynamicScopeIfNeeded(ThreadContext context, Block block, boolean pushNewDynScope, boolean reuseParentDynScope) {
         DynamicScope newScope = getNewBlockScope(block, pushNewDynScope, reuseParentDynScope);
         if (newScope != null) {
             context.pushScope(newScope);
         }
         return newScope;
     }
 
     @Interp @JIT
     public static IRubyObject updateBlockState(Block block, IRubyObject self) {
         // SSS FIXME: Why is self null in non-binding-eval contexts?
         if (self == null || block.getEvalType() == EvalType.BINDING_EVAL) {
             // Update self to the binding's self
             self = useBindingSelf(block.getBinding());
         }
 
         // Clear block's eval type
         block.setEvalType(EvalType.NONE);
 
         // Return self in case it has been updated
         return self;
     }
 
     public static IRubyObject useBindingSelf(Binding binding) {
         IRubyObject self = binding.getSelf();
         binding.getFrame().setSelf(self);
 
         return self;
     }
 
     /**
      * Create a new Symbol.to_proc for the given symbol name and encoding.
      *
      * @param context
      * @param symbol
      * @return
      */
     @Interp
     public static RubyProc newSymbolProc(ThreadContext context, String symbol, Encoding encoding) {
         return (RubyProc)context.runtime.newSymbol(symbol, encoding).to_proc(context);
     }
 
     /**
      * Create a new Symbol.to_proc for the given symbol name and encoding.
      *
      * @param context
      * @param symbol
      * @return
      */
     @JIT
     public static RubyProc newSymbolProc(ThreadContext context, String symbol, String encoding) {
         return newSymbolProc(context, symbol, retrieveJCodingsEncoding(context, encoding));
     }
 
     @JIT
     public static IRubyObject[] singleBlockArgToArray(IRubyObject value) {
         IRubyObject[] args;
         if (value instanceof RubyArray) {
             args = value.convertToArray().toJavaArray();
         } else {
             args = new IRubyObject[] { value };
         }
         return args;
     }
 
     @JIT
     public static Block prepareBlock(ThreadContext context, IRubyObject self, DynamicScope scope, BlockBody body) {
         Block block = new Block(body, context.currentBinding(self, scope));
 
         return block;
     }
 
     public static RubyString newFrozenString(ThreadContext context, ByteList bytelist, int coderange, String file, int line) {
         Ruby runtime = context.runtime;
 
         RubyString string = RubyString.newString(runtime, bytelist, coderange);
 
         if (runtime.getInstanceConfig().isDebuggingFrozenStringLiteral()) {
             // stuff location info into the string and then freeze it
             RubyArray info = (RubyArray) runtime.newArray(runtime.newString(file).freeze(context), runtime.newFixnum(line)).freeze(context);
             string.setInstanceVariable(RubyString.DEBUG_INFO_FIELD, info);
             string.setFrozen(true);
         } else {
             string = runtime.freezeAndDedupString(string);
         }
 
         return string;
     }
 
     public static RubyString freezeLiteralString(ThreadContext context, RubyString string, String file, int line) {
         Ruby runtime = context.runtime;
 
         if (runtime.getInstanceConfig().isDebuggingFrozenStringLiteral()) {
             // stuff location info into the string and then freeze it
             RubyArray info = (RubyArray) runtime.newArray(runtime.newString(file).freeze(context), runtime.newFixnum(line)).freeze(context);
             string.setInstanceVariable(RubyString.DEBUG_INFO_FIELD, info);
         }
 
         string.setFrozen(true);
 
         return string;
     }
 
     @JIT
     public static IRubyObject callOptimizedAref(ThreadContext context, IRubyObject caller, IRubyObject target, RubyString keyStr, CallSite site) {
         if (target instanceof RubyHash && ((CachingCallSite) site).isBuiltin(target.getMetaClass())) {
             // call directly with cached frozen string
             return ((RubyHash) target).op_aref(context, keyStr);
         }
 
         return site.call(context, caller, target, keyStr.strDup(context.runtime));
     }
 
     public static DynamicMethod getRefinedMethodForClass(StaticScope refinedScope, RubyModule target, String methodName) {
         Map<RubyClass, RubyModule> refinements;
         RubyModule refinement;
         DynamicMethod method = null;
         RubyModule overlay;
diff --git a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
index a59a82789c..23496bc130 100644
--- a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+++ b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
@@ -764,1762 +764,1764 @@ public class JVMVisitor extends IRVisitor {
             jvmAdapter().instance_of(p(RubyString.class));
             jvmAdapter().iftrue(after);
             jvmAdapter().invokevirtual(p(IRubyObject.class), "anyToString", sig(IRubyObject.class));
 
             jvmAdapter().label(after);
             jvmAdapter().invokevirtual(p(RubyString.class), "append19", sig(RubyString.class, IRubyObject.class));
         }
 
         // freeze the string
         jvmAdapter().dup();
         jvmAdapter().ldc(true);
         jvmAdapter().invokeinterface(p(IRubyObject.class), "setFrozen", sig(void.class, boolean.class));
 
         // invoke the "`" method on self
         jvmMethod().invokeSelf(file, lastLine, "`", 1, false, CallType.FUNCTIONAL, false);
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
 
     public void BSwitchInstr(BSwitchInstr bswitchinstr) {
         visit(bswitchinstr.getCaseOperand());
         jvmAdapter().dup();
         jvmAdapter().instance_of(p(RubyFixnum.class));
         org.objectweb.asm.Label rubyCaseLabel = getJVMLabel(bswitchinstr.getRubyCaseLabel());
         org.objectweb.asm.Label notFixnum = new org.objectweb.asm.Label();
         jvmAdapter().iffalse(notFixnum);
         jvmAdapter().checkcast(p(RubyFixnum.class));
         jvmAdapter().invokevirtual(p(RubyFixnum.class), "getIntValue", sig(int.class));
         Label[] targets = bswitchinstr.getTargets();
         org.objectweb.asm.Label[] jvmTargets = new org.objectweb.asm.Label[targets.length];
         for (int i = 0; i < targets.length; i++) jvmTargets[i] = getJVMLabel(targets[i]);
 
         // if jump table is all contiguous values, use a tableswitch
         int[] jumps = bswitchinstr.getJumps();
         int low = jumps[0];
         int high = jumps[jumps.length - 1];
         int span = high - low;
         if (span == jumps.length) {
             jvmAdapter().tableswitch(low, high, getJVMLabel(bswitchinstr.getElseTarget()), jvmTargets);
         } else {
             jvmAdapter().lookupswitch(getJVMLabel(bswitchinstr.getElseTarget()), bswitchinstr.getJumps(), jvmTargets);
         }
         jvmAdapter().label(notFixnum);
         jvmAdapter().pop();
         jvmAdapter().label(rubyCaseLabel);
     }
 
     @Override
     public void BTrueInstr(BTrueInstr btrueinstr) {
         Operand arg1 = btrueinstr.getArg1();
         // this is a gross hack because we don't have distinction in boolean instrs between boxed and unboxed
         if (arg1 instanceof TemporaryBooleanVariable || arg1 instanceof UnboxedBoolean) {
             // no need to unbox, just branch
             visit(arg1);
             jvmMethod().btrue(getJVMLabel(btrueinstr.getJumpTarget()));
         } else if (arg1 instanceof UnboxedFixnum || arg1 instanceof UnboxedFloat) {
             // always true, always branch
             jvmMethod().goTo(getJVMLabel(btrueinstr.getJumpTarget()));
         } else {
             // unbox and branch
             visit(arg1);
             jvmAdapter().invokeinterface(p(IRubyObject.class), "isTrue", sig(boolean.class));
             jvmMethod().btrue(getJVMLabel(btrueinstr.getJumpTarget()));
         }
     }
 
     @Override
     public void BUndefInstr(BUndefInstr bundefinstr) {
         visit(bundefinstr.getArg1());
         jvmMethod().pushUndefined();
         jvmAdapter().if_acmpeq(getJVMLabel(bundefinstr.getJumpTarget()));
     }
 
     @Override
     public void BuildBackrefInstr(BuildBackrefInstr instr) {
         jvmMethod().loadContext();
         jvmAdapter().invokevirtual(p(ThreadContext.class), "getBackRef", sig(IRubyObject.class));
 
         switch (instr.type) {
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
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BuildCompoundArrayInstr(BuildCompoundArrayInstr instr) {
         jvmMethod().loadContext();
         visit(instr.getAppendingArg());
         if (instr.isArgsPush()) jvmAdapter().checkcast("org/jruby/RubyArray");
         visit(instr.getAppendedArg());
         if (instr.isArgsPush()) {
             jvmMethod().invokeHelper("argsPush", RubyArray.class, ThreadContext.class, RubyArray.class, IRubyObject.class);
         } else {
             jvmMethod().invokeHelper("argsCat", RubyArray.class, ThreadContext.class, IRubyObject.class, IRubyObject.class);
         }
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BuildCompoundStringInstr(BuildCompoundStringInstr compoundstring) {
         ByteList csByteList = new ByteList();
         csByteList.setEncoding(compoundstring.getEncoding());
         jvmMethod().pushString(csByteList, StringSupport.CR_UNKNOWN);
         for (Operand p : compoundstring.getPieces()) {
 //            if ((p instanceof StringLiteral) && (compoundstring.isSameEncodingAndCodeRange((StringLiteral)p))) {
 //                jvmMethod().pushByteList(((StringLiteral)p).bytelist);
 //                jvmAdapter().invokevirtual(p(RubyString.class), "cat", sig(RubyString.class, ByteList.class));
 //            } else {
                 visit(p);
                 jvmAdapter().invokevirtual(p(RubyString.class), "append19", sig(RubyString.class, IRubyObject.class));
 //            }
         }
         if (compoundstring.isFrozen()) {
             jvmMethod().loadContext();
             jvmAdapter().swap();
             jvmAdapter().ldc(compoundstring.getFile());
             jvmAdapter().ldc(compoundstring.getLine());
             jvmMethod().invokeIRHelper("freezeLiteralString", sig(RubyString.class, ThreadContext.class, RubyString.class, String.class, int.class));
         }
         jvmStoreLocal(compoundstring.getResult());
     }
 
     @Override
     public void BuildDynRegExpInstr(BuildDynRegExpInstr instr) {
         final IRBytecodeAdapter m = jvmMethod();
 
         if (instr.getOptions().isOnce() && instr.getRegexp() != null) {
             visit(new Regexp(instr.getRegexp().source().convertToString().getByteList(), instr.getOptions()));
             jvmStoreLocal(instr.getResult());
             return;
         }
 
         RegexpOptions options = instr.getOptions();
         final Operand[] operands = instr.getPieces();
 
         Runnable r = new Runnable() {
             @Override
             public void run() {
                 m.loadContext();
                 for (int i = 0; i < operands.length; i++) {
                     Operand operand = operands[i];
                     visit(operand);
                 }
             }
         };
 
         m.pushDRegexp(r, options, operands.length);
 
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
     public void BuildSplatInstr(BuildSplatInstr instr) {
         jvmMethod().loadContext();
         visit(instr.getArray());
         jvmAdapter().ldc(instr.getDup());
         jvmMethod().invokeIRHelper("splatArray", sig(RubyArray.class, ThreadContext.class, IRubyObject.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void CallInstr(CallInstr callInstr) {
         if (callInstr instanceof OneFixnumArgNoBlockCallInstr) {
             oneFixnumArgNoBlockCallInstr((OneFixnumArgNoBlockCallInstr) callInstr);
             return;
         } else if (callInstr instanceof OneFloatArgNoBlockCallInstr) {
             oneFloatArgNoBlockCallInstr((OneFloatArgNoBlockCallInstr) callInstr);
             return;
         }
 
         // JIT does not support refinements yet
         if (callInstr.getCallSite() instanceof RefinedCachingCallSite) {
             throw new NotCompilableException("refinements are unsupported in JIT");
         }
 
         IRBytecodeAdapter m = jvmMethod();
         String name = callInstr.getName();
         Operand[] args = callInstr.getCallArgs();
         Operand receiver = callInstr.getReceiver();
         int numArgs = args.length;
         Operand closure = callInstr.getClosureArg(null);
         boolean hasClosure = closure != null;
         CallType callType = callInstr.getCallType();
         Variable result = callInstr.getResult();
 
         compileCallCommon(m, name, args, receiver, numArgs, closure, hasClosure, callType, result, callInstr.isPotentiallyRefined());
     }
 
     private void compileCallCommon(IRBytecodeAdapter m, String name, Operand[] args, Operand receiver, int numArgs, Operand closure, boolean hasClosure, CallType callType, Variable result, boolean isPotentiallyRefined) {
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
                 m.invokeSelf(file, lastLine, name, arity, hasClosure, CallType.FUNCTIONAL, isPotentiallyRefined);
                 break;
             case VARIABLE:
                 m.invokeSelf(file, lastLine, name, arity, hasClosure, CallType.VARIABLE, isPotentiallyRefined);
                 break;
             case NORMAL:
                 m.invokeOther(file, lastLine, name, arity, hasClosure, isPotentiallyRefined);
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
         jvmAdapter().pushBoolean(checkargsarrayarityinstr.rest);
         jvmMethod().invokeStatic(Type.getType(Helpers.class), Method.getMethod("void irCheckArgsArrayArity(org.jruby.runtime.ThreadContext, org.jruby.RubyArray, int, int, boolean)"));
     }
 
     @Override
     public void CheckArityInstr(CheckArityInstr checkarityinstr) {
         if (jvm.methodData().specificArity >= 0) {
             // no arity check in specific arity path
         } else {
             jvmMethod().loadContext();
             jvmMethod().loadStaticScope();
             jvmMethod().loadArgs();
             // TODO: pack these, e.g. in a constant pool String
             jvmAdapter().ldc(checkarityinstr.required);
             jvmAdapter().ldc(checkarityinstr.opt);
             jvmAdapter().ldc(checkarityinstr.rest);
             jvmAdapter().ldc(checkarityinstr.receivesKeywords);
             jvmAdapter().ldc(checkarityinstr.restKey);
             jvmMethod().loadBlockType();
             jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "checkArity", sig(void.class, ThreadContext.class, StaticScope.class, Object[].class, int.class, int.class, boolean.class, boolean.class, int.class, Block.Type.class));
         }
     }
 
     @Override
     public void CheckForLJEInstr(CheckForLJEInstr checkForljeinstr) {
         jvmMethod().loadContext();
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmAdapter().ldc(checkForljeinstr.maybeLambda());
         jvmMethod().loadBlockType();
         jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "checkForLJE", sig(void.class, ThreadContext.class, DynamicScope.class, boolean.class, Block.Type.class));
     }
 
         @Override
     public void ClassSuperInstr(ClassSuperInstr classsuperinstr) {
         String name = classsuperinstr.getName();
         Operand[] args = classsuperinstr.getCallArgs();
         Operand definingModule = classsuperinstr.getDefiningModule();
         boolean[] splatMap = classsuperinstr.splatMap();
         Operand closure = classsuperinstr.getClosureArg(null);
 
         superCommon(name, classsuperinstr, args, definingModule, splatMap, closure);
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
 
         storeHeapOrStack(src, res);
     }
 
     private void storeHeapOrStack(final Operand value, final Variable res) {
         jvmStoreLocal(new Runnable() {
             @Override
             public void run() {
                 if (res instanceof TemporaryFloatVariable) {
                     loadFloatArg(value);
                 } else if (res instanceof TemporaryFixnumVariable) {
                     loadFixnumArg(value);
                 } else {
                     visit(value);
                 }
             }
         }, res);
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
 
         JVMVisitorMethodContext context = new JVMVisitorMethodContext();
         emitMethod(method, context);
 
         MethodType variable = context.getNativeSignature(-1); // always a variable arity handle
         assert(variable != null);
 
         String defSignature = pushHandlesForDef(
                 context.getJittedName(),
                 context.getNativeSignaturesExceptVariable(),
                 variable,
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, IRubyObject.class),
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, java.lang.invoke.MethodHandle.class, int.class, IRScope.class, IRubyObject.class));
 
         jvmAdapter().getstatic(jvm.clsData().clsName, context.getJittedName() + "_IRScope", ci(IRScope.class));
         visit(defineclassmethodinstr.getContainer());
 
         // add method
         jvmMethod().adapter.invokestatic(p(IRRuntimeHelpers.class), "defCompiledClassMethod", defSignature);
     }
 
     // SSS FIXME: Needs an update to reflect instr. change
     @Override
     public void DefineInstanceMethodInstr(DefineInstanceMethodInstr defineinstancemethodinstr) {
         IRMethod method = defineinstancemethodinstr.getMethod();
         JVMVisitorMethodContext context = new JVMVisitorMethodContext();
 
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         m.loadContext();
 
         emitMethod(method, context);
 
         MethodType variable = context.getNativeSignature(-1); // always a variable arity handle
         assert(variable != null);
 
         String defSignature = pushHandlesForDef(
                 context.getJittedName(),
                 context.getNativeSignaturesExceptVariable(),
                 variable,
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, DynamicScope.class, IRubyObject.class),
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, java.lang.invoke.MethodHandle.class, int.class, IRScope.class, DynamicScope.class, IRubyObject.class));
 
         a.getstatic(jvm.clsData().clsName, context.getJittedName() + "_IRScope", ci(IRScope.class));
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmMethod().loadSelf();
 
         // add method
         a.invokestatic(p(IRRuntimeHelpers.class), "defCompiledInstanceMethod", defSignature);
     }
 
     private String pushHandlesForDef(String name, IntHashMap<MethodType> signaturesExceptVariable, MethodType variable, String variableOnly, String variableAndSpecific) {
         String defSignature;
 
         jvmMethod().pushHandle(new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(variable.returnType(), variable.parameterArray())));
 
         if (signaturesExceptVariable.size() == 0) {
             defSignature = variableOnly;
         } else {
             defSignature = variableAndSpecific;
 
             for (IntHashMap.Entry<MethodType> entry : signaturesExceptVariable.entrySet()) {
                 jvmMethod().pushHandle(new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(entry.getValue().returnType(), entry.getValue().parameterArray())));
                 jvmAdapter().pushInt(entry.getKey());
                 break; // FIXME: only supports one arity
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
         String siteName = jvmMethod().getUniqueSiteName("===");
         IRBytecodeAdapter.cacheCallSite(jvmAdapter(), jvmMethod().getClassData().clsName, siteName, "===", CallType.FUNCTIONAL, false);
         jvmAdapter().ldc(eqqinstr.isSplattedValue());
         jvmMethod().invokeIRHelper("isEQQ", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, CallSite.class, boolean.class));
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
         jvmMethod().getGlobalVariable(getglobalvariableinstr.getTarget().getName());
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
 
         jvmMethod().inheritanceSearchConst(inheritancesearchconstinstr.getConstName(), false);
         jvmStoreLocal(inheritancesearchconstinstr.getResult());
     }
 
     @Override
     public void InstanceSuperInstr(InstanceSuperInstr instancesuperinstr) {
         String name = instancesuperinstr.getName();
         Operand[] args = instancesuperinstr.getCallArgs();
         Operand definingModule = instancesuperinstr.getDefiningModule();
         boolean[] splatMap = instancesuperinstr.splatMap();
         Operand closure = instancesuperinstr.getClosureArg(null);
 
         superCommon(name, instancesuperinstr, args, definingModule, splatMap, closure);
     }
 
     private void superCommon(String name, CallInstr instr, Operand[] args, Operand definingModule, boolean[] splatMap, Operand closure) {
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
 
         boolean hasClosure = closure != null;
         if (hasClosure) {
             m.loadContext();
             visit(closure);
             m.invokeIRHelper("getBlockFromObject", sig(Block.class, ThreadContext.class, Object.class));
         }
 
         switch (operation) {
             case INSTANCE_SUPER:
                 m.invokeInstanceSuper(file, lastLine, name, args.length, hasClosure, splatMap);
                 break;
             case CLASS_SUPER:
                 m.invokeClassSuper(file, lastLine, name, args.length, hasClosure, splatMap);
                 break;
             case UNRESOLVED_SUPER:
                 m.invokeUnresolvedSuper(file, lastLine, name, args.length, hasClosure, splatMap);
                 break;
             case ZSUPER:
                 m.invokeZSuper(file, lastLine, name, args.length, hasClosure, splatMap);
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
 
         lastLine = linenumberinstr.getLineNumber() + 1;
         jvmAdapter().line(lastLine);
     }
 
     @Override
     public void LoadLocalVarInstr(LoadLocalVarInstr loadlocalvarinstr) {
         LocalVariable(loadlocalvarinstr.getLocalVar());
         jvmStoreLocal(loadlocalvarinstr.getResult());
     }
 
     @Override
     public void LoadImplicitClosure(LoadImplicitClosureInstr loadimplicitclosureinstr) {
         jvmMethod().loadBlock();
         jvmStoreLocal(loadimplicitclosureinstr.getResult());
     }
 
     @Override
     public void LoadFrameClosure(LoadFrameClosureInstr loadframeclosureinstr) {
         jvmMethod().loadContext();
         jvmAdapter().invokevirtual(p(ThreadContext.class), "getFrameBlock", sig(Block.class));
         jvmStoreLocal(loadframeclosureinstr.getResult());
     }
 
     @Override
     public void MatchInstr(MatchInstr matchInstr) {
         compileCallCommon(jvmMethod(), "=~", matchInstr.getCallArgs(), matchInstr.getReceiver(), 1, null, false, CallType.NORMAL, matchInstr.getResult(), false);
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
 
         compileCallCommon(m, name, args, receiver, numArgs, closure, hasClosure, callType, null, noResultCallInstr.isPotentiallyRefined());
     }
 
     public void oneFixnumArgNoBlockCallInstr(OneFixnumArgNoBlockCallInstr oneFixnumArgNoBlockCallInstr) {
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
 
         m.invokeOtherOneFixnum(file, lastLine, name, fixnum, oneFixnumArgNoBlockCallInstr.getCallType());
 
         if (result != null) {
             jvmStoreLocal(result);
         } else {
             // still need to drop, since all dyncalls return something (FIXME)
             m.adapter.pop();
         }
     }
 
     public void oneFloatArgNoBlockCallInstr(OneFloatArgNoBlockCallInstr oneFloatArgNoBlockCallInstr) {
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
 
         m.invokeOtherOneFloat(file, lastLine, name, flote, oneFloatArgNoBlockCallInstr.getCallType());
 
         if (result != null) {
             jvmStoreLocal(result);
         } else {
             // still need to drop, since all dyncalls return something (FIXME)
             m.adapter.pop();
         }
     }
 
     @Override
     public void OptArgMultipleAsgnInstr(OptArgMultipleAsgnInstr optargmultipleasgninstr) {
         visit(optargmultipleasgninstr.getArray());
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
     public void PopBlockFrameInstr(PopBlockFrameInstr instr) {
         jvmMethod().loadContext();
         visit(instr.getFrame());
         jvmAdapter().invokevirtual(p(ThreadContext.class), "postYieldNoScope", sig(void.class, Frame.class));
     }
 
     @Override
     public void PopMethodFrameInstr(PopMethodFrameInstr popframeinstr) {
         jvmMethod().loadContext();
         jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("void postMethodFrameOnly()"));
     }
 
     @Override
     public void PrepareBlockArgsInstr(PrepareBlockArgsInstr instr) {
         jvmMethod().loadContext();
         jvmMethod().loadSelfBlock();
         jvmMethod().loadArgs();
         jvmAdapter().ldc(((IRClosure)jvm.methodData().scope).receivesKeywordArgs());
         jvmMethod().invokeIRHelper("prepareBlockArgs", sig(IRubyObject[].class, ThreadContext.class, Block.class, IRubyObject[].class, boolean.class));
         jvmMethod().storeArgs();
     }
 
     @Override
     public void PrepareFixedBlockArgsInstr(PrepareFixedBlockArgsInstr instr) {
         jvmMethod().loadContext();
         jvmMethod().loadSelfBlock();
         jvmMethod().loadArgs();
         jvmMethod().invokeIRHelper("prepareFixedBlockArgs", sig(IRubyObject[].class, ThreadContext.class, Block.class, IRubyObject[].class));
         jvmMethod().storeArgs();
     }
 
     @Override
     public void PrepareSingleBlockArgInstr(PrepareSingleBlockArgInstr instr) {
         jvmMethod().loadContext();
         jvmMethod().loadSelfBlock();
         jvmMethod().loadArgs();
         jvmMethod().invokeIRHelper("prepareSingleBlockArgs", sig(IRubyObject[].class, ThreadContext.class, Block.class, IRubyObject[].class));
         jvmMethod().storeArgs();
     }
 
     @Override
     public void PrepareNoBlockArgsInstr(PrepareNoBlockArgsInstr instr) {
         jvmMethod().loadContext();
         jvmMethod().loadSelfBlock();
         jvmMethod().loadArgs();
         jvmMethod().invokeIRHelper("prepareNoBlockArgs", sig(IRubyObject[].class, ThreadContext.class, Block.class, IRubyObject[].class));
         jvmMethod().storeArgs();
     }
 
     @Override
     public void ProcessModuleBodyInstr(ProcessModuleBodyInstr processmodulebodyinstr) {
         jvmMethod().loadContext();
         visit(processmodulebodyinstr.getModuleBody());
         visit(processmodulebodyinstr.getBlock());
         jvmMethod().invokeIRHelper("invokeModuleBody", sig(IRubyObject.class, ThreadContext.class, DynamicMethod.class, Block.class));
         jvmStoreLocal(processmodulebodyinstr.getResult());
     }
 
     @Override
     public void PushBlockBindingInstr(PushBlockBindingInstr instr) {
         IRScope scope = jvm.methodData().scope;
         // FIXME: Centralize this out of InterpreterContext
         boolean reuseParentDynScope = scope.getFlags().contains(IRFlags.REUSE_PARENT_DYNSCOPE);
         boolean pushNewDynScope = !scope.getFlags().contains(IRFlags.DYNSCOPE_ELIMINATED) && !reuseParentDynScope;
 
         jvmMethod().loadContext();
         jvmMethod().loadSelfBlock();
         jvmAdapter().ldc(pushNewDynScope);
         jvmAdapter().ldc(reuseParentDynScope);
         jvmMethod().invokeIRHelper("pushBlockDynamicScopeIfNeeded", sig(DynamicScope.class, ThreadContext.class, Block.class, boolean.class, boolean.class));
         jvmStoreLocal(DYNAMIC_SCOPE);
     }
 
     @Override
     public void PushBlockFrameInstr(PushBlockFrameInstr instr) {
         jvmMethod().loadContext();
         jvmMethod().loadSelfBlock();
         jvmAdapter().invokevirtual(p(Block.class), "getBinding", sig(Binding.class));
         jvmAdapter().invokevirtual(p(ThreadContext.class), "preYieldNoScope", sig(Frame.class, Binding.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void PushMethodBindingInstr(PushMethodBindingInstr pushbindinginstr) {
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
     public void PushMethodFrameInstr(PushMethodFrameInstr pushframeinstr) {
         jvmMethod().loadContext();
         jvmMethod().loadFrameClass();
         jvmMethod().loadFrameName();
         jvmMethod().loadSelf();
         jvmMethod().loadBlock();
         jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("void preMethodFrameOnly(org.jruby.RubyModule, String, org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.Block)"));
 
         // FIXME: this should be part of explicit call protocol only when needed, optimizable, and correct for the scope
         // See also CompiledIRMethod.call
         jvmMethod().loadContext();
         jvmAdapter().getstatic(p(Visibility.class), "PUBLIC", ci(Visibility.class));
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
         visit(putglobalvarinstr.getValue());
         jvmMethod().setGlobalVariable(putglobalvarinstr.getTarget().getName());
         // leaves copy of value on stack
         jvmAdapter().pop();
     }
 
     @Override
     public void ReifyClosureInstr(ReifyClosureInstr reifyclosureinstr) {
         jvmMethod().loadRuntime();
         jvmLoadLocal("$blockArg");
         jvmMethod().invokeIRHelper("newProc", sig(IRubyObject.class, Ruby.class, Block.class));
         jvmStoreLocal(reifyclosureinstr.getResult());
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
         jvmMethod().loadContext();
         jvmMethod().loadArgs();
         jvmAdapter().pushInt(instr.preReqdArgsCount);
+        jvmAdapter().pushInt(instr.optArgsCount);
+        jvmAdapter().pushBoolean(instr.restArg);
         jvmAdapter().pushInt(instr.postReqdArgsCount);
         jvmAdapter().pushInt(instr.getArgIndex());
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
-        jvmMethod().invokeIRHelper("receivePostReqdArg", sig(IRubyObject.class, ThreadContext.class, IRubyObject[].class, int.class, int.class, int.class, boolean.class));
+        jvmMethod().invokeIRHelper("receivePostReqdArg", sig(IRubyObject.class, ThreadContext.class, IRubyObject[].class, int.class, int.class, boolean.class, int.class, int.class, boolean.class));
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
         // noop...self is passed in
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
         visit(reqdargmultipleasgninstr.getArray());
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
         visit(restargmultipleasgninstr.getArray());
         jvmAdapter().checkcast(p(RubyArray.class));
         jvmAdapter().pushInt(restargmultipleasgninstr.getPreArgsCount());
         jvmAdapter().pushInt(restargmultipleasgninstr.getPostArgsCount());
         jvmAdapter().invokestatic(p(Helpers.class), "viewArgsArray", sig(RubyArray.class, ThreadContext.class, RubyArray.class, int.class, int.class));
         jvmStoreLocal(restargmultipleasgninstr.getResult());
     }
 
     @Override
     public void RestoreBindingVisibilityInstr(RestoreBindingVisibilityInstr instr) {
         jvmMethod().loadSelfBlock();
         jvmAdapter().invokevirtual(p(Block.class), "getBinding", sig(Binding.class));
         jvmAdapter().invokevirtual(p(Binding.class), "getFrame", sig(Frame.class));
         visit(instr.getVisibility());
         jvmAdapter().invokevirtual(p(Frame.class), "setVisibility", sig(void.class, Visibility.class));
     }
 
     @Override
     public void ReturnOrRethrowSavedExcInstr(ReturnOrRethrowSavedExcInstr instr) {
         jvmMethod().loadContext();
         visit(instr.getReturnValue());
         jvmMethod().invokeIRHelper("returnOrRethrowSavedException", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class));
         jvmMethod().returnValue();
     }
 
     @Override
     public void RuntimeHelperCall(RuntimeHelperCall runtimehelpercall) {
         switch (runtimehelpercall.getHelperMethod()) {
             case HANDLE_PROPAGATED_BREAK:
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
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[1]).getString());
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
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[0]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedGlobal", sig(IRubyObject.class, ThreadContext.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_INSTANCE_VAR:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedInstanceVar", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_CLASS_VAR:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().checkcast(p(RubyModule.class));
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedClassVar", sig(IRubyObject.class, ThreadContext.class, RubyModule.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_SUPER:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedSuper", sig(IRubyObject.class, ThreadContext.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_METHOD:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((Stringable) runtimehelpercall.getArgs()[1]).getString());
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
             case RESTORE_EXCEPTION_VAR:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 visit(runtimehelpercall.getArgs()[1]);
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "restoreExceptionVar", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             default:
                 throw new NotCompilableException("Unknown IR runtime helper method: " + runtimehelpercall.getHelperMethod() + "; INSTR: " + this);
         }
     }
 
     @Override
     public void SaveBindingVisibilityInstr(SaveBindingVisibilityInstr instr) {
         jvmMethod().loadSelfBlock();
         jvmAdapter().invokevirtual(p(Block.class), "getBinding", sig(Binding.class));
         jvmAdapter().invokevirtual(p(Binding.class), "getFrame", sig(Frame.class));
         jvmAdapter().invokevirtual(p(Frame.class), "getVisibility", sig(Visibility.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void ToggleBacktraceInstr(ToggleBacktraceInstr instr) {
         jvmMethod().loadContext();
         jvmAdapter().pushBoolean(instr.requiresBacktrace());
         jvmAdapter().invokevirtual(p(ThreadContext.class), "setExceptionRequiresBacktrace", sig(void.class, boolean.class));
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
     public void SearchModuleForConstInstr(SearchModuleForConstInstr instr) {
         jvmMethod().loadContext();
         visit(instr.getCurrentModule());
 
         jvmMethod().searchModuleForConst(instr.getConstName(), instr.isNoPrivateConsts());
         jvmStoreLocal(instr.getResult());
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
         visit(throwexceptioninstr.getException());
         jvmAdapter().athrow();
     }
 
     @Override
     public void ToAryInstr(ToAryInstr toaryinstr) {
         jvmMethod().loadContext();
         visit(toaryinstr.getArray());
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
         boolean[] splatMap = unresolvedsuperinstr.splatMap();
         Operand closure = unresolvedsuperinstr.getClosureArg(null);
 
         superCommon(name, unresolvedsuperinstr, args, definingModule, splatMap, closure);
     }
 
     @Override
     public void UpdateBlockExecutionStateInstr (UpdateBlockExecutionStateInstr instr) {
         jvmMethod().loadSelfBlock();
         jvmMethod().loadSelf();
         jvmMethod().invokeIRHelper("updateBlockState", sig(IRubyObject.class, Block.class, IRubyObject.class));
         jvmMethod().storeSelf();
     }
 
     @Override
     public void YieldInstr(YieldInstr yieldinstr) {
         jvmMethod().loadContext();
         visit(yieldinstr.getBlockArg());
 
         if (yieldinstr.getYieldArg() == UndefinedValue.UNDEFINED) {
             jvmMethod().yieldSpecific();
         } else {
             Operand yieldOp = yieldinstr.getYieldArg();
             if (yieldinstr.isUnwrapArray() && yieldOp instanceof Array && ((Array) yieldOp).size() > 1) {
                 Array yieldValues = (Array) yieldOp;
                 for (Operand yieldValue : yieldValues) {
                     visit(yieldValue);
                 }
                 jvmMethod().yieldValues(yieldValues.size());
             } else {
                 visit(yieldinstr.getYieldArg());
                 jvmMethod().yield(yieldinstr.isUnwrapArray());
             }
         }
 
         jvmStoreLocal(yieldinstr.getResult());
     }
 
     @Override
     public void ZSuperInstr(ZSuperInstr zsuperinstr) {
         String name = zsuperinstr.getName();
         Operand[] args = zsuperinstr.getCallArgs();
         // this would be getDefiningModule but that is not used for unresolved super
         Operand definingModule = UndefinedValue.UNDEFINED;
         boolean[] splatMap = zsuperinstr.splatMap();
         Operand closure = zsuperinstr.getClosureArg(null);
 
         superCommon(name, zsuperinstr, args, definingModule, splatMap, closure);
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
         jvmAdapter().ldc(buildlambdainstr.getFile());
         jvmAdapter().pushInt(buildlambdainstr.getLine());
 
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
         jvmAdapter().dup();
 
         // get symbol name
         jvmAdapter().invokeinterface(p(IRubyObject.class), "asJavaString", sig(String.class));
         jvmAdapter().swap();
 
         // get encoding of symbol name
         jvmAdapter().invokeinterface(p(IRubyObject.class), "asString", sig(RubyString.class));
         jvmAdapter().invokevirtual(p(RubyString.class), "getByteList", sig(ByteList.class));
         jvmAdapter().invokevirtual(p(ByteList.class), "getEncoding", sig(Encoding.class));
 
         // keeps encoding of symbol name
         jvmAdapter().invokevirtual(p(Ruby.class), "newSymbol", sig(RubySymbol.class, String.class, Encoding.class));
     }
 
     @Override
     public void Filename(Filename filename) {
         // Fixme: Not very efficient to do all this every time
         jvmMethod().loadRuntime();
         jvmMethod().loadStaticScope();
         jvmAdapter().invokevirtual(p(StaticScope.class), "getIRScope", sig(IRScope.class));
         jvmAdapter().invokevirtual(p(IRScope.class), "getFileName", sig(String.class));
         jvmAdapter().invokevirtual(p(Ruby.class), "newString", sig(RubyString.class, String.class));
     }
 
 
     @Override
     public void Fixnum(Fixnum fixnum) {
         jvmMethod().pushFixnum(fixnum.getValue());
     }
 
     @Override
     public void FrozenString(FrozenString frozen) {
         jvmMethod().pushFrozenString(frozen.getByteList(), frozen.getCodeRange(), frozen.getFile(), frozen.getLine());
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
         IRBytecodeAdapter m = jvmMethod();
 
         int depth = localvariable.getScopeDepth();
         int location = localvariable.getLocation();
 
         // We can only use the fast path with no null checking in methods, since closures may JIT independently
         // atop methods that do not guarantee all scoped vars are initialized. See jruby/jruby#4235.
         if (jvm.methodData().scope instanceof IRMethod) {
             jvmLoadLocal(DYNAMIC_SCOPE);
 
             if (depth == 0) {
                 if (location < DynamicScopeGenerator.SPECIALIZED_GETS.size()) {
                     m.adapter.invokevirtual(p(DynamicScope.class), DynamicScopeGenerator.SPECIALIZED_GETS.get(location), sig(IRubyObject.class));
                 } else {
                     m.adapter.pushInt(location);
                     m.adapter.invokevirtual(p(DynamicScope.class), "getValueDepthZero", sig(IRubyObject.class, int.class));
                 }
             } else {
                 m.adapter.pushInt(location);
                 m.adapter.pushInt(depth);
                 m.adapter.invokevirtual(p(DynamicScope.class), "getValue", sig(IRubyObject.class, int.class, int.class));
             }
         } else {
             jvmLoadLocal(DYNAMIC_SCOPE);
 
             if (depth == 0) {
                 if (location < DynamicScopeGenerator.SPECIALIZED_GETS_OR_NIL.size()) {
                     m.pushNil();
                     m.adapter.invokevirtual(p(DynamicScope.class), DynamicScopeGenerator.SPECIALIZED_GETS_OR_NIL.get(location), sig(IRubyObject.class, IRubyObject.class));
                 } else {
                     m.adapter.pushInt(location);
                     m.pushNil();
                     m.adapter.invokevirtual(p(DynamicScope.class), "getValueDepthZeroOrNil", sig(IRubyObject.class, int.class, IRubyObject.class));
                 }
             } else {
                 m.adapter.pushInt(location);
                 m.adapter.pushInt(depth);
                 m.pushNil();
                 m.adapter.invokevirtual(p(DynamicScope.class), "getValueOrNil", sig(IRubyObject.class, int.class, int.class, IRubyObject.class));
             }
         }
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
     public void NullBlock(NullBlock nullblock) {
         jvmAdapter().getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
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
         jvmMethod().pushRegexp(regexp.getSource(), regexp.options.toEmbeddedOptions());
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
         visit(splat.getArray());
         // Splat is now only used in call arg lists where it is guaranteed that
         // the splat-arg is an array.
         //
         // It is:
         // - either a result of a args-cat/args-push (which generate an array),
         // - or a result of a BuildSplatInstr (which also generates an array),
         // - or a rest-arg that has been received (which also generates an array)
         //   and is being passed via zsuper.
         //
         // In addition, since this only shows up in call args, the array itself is
         // never modified. The array elements are extracted out and inserted into
         // a java array. So, a dup is not required either.
         //
         // So, besides retrieving the array, nothing more to be done here!
     }
 
     @Override
     public void StandardError(StandardError standarderror) {
         jvmMethod().loadRuntime();
         jvmAdapter().invokevirtual(p(Ruby.class), "getStandardError", sig(RubyClass.class));
     }
 
     @Override
     public void StringLiteral(StringLiteral stringliteral) {
         jvmMethod().pushString(stringliteral.getByteList(), stringliteral.getCodeRange());
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
     public void SymbolProc(SymbolProc symbolproc) {
         jvmMethod().pushSymbolProc(symbolproc.getName(), symbolproc.getEncoding());
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
 
         jvmMethod().loadContext();
         visit(closure.getSelf());
         jvmLoadLocal(DYNAMIC_SCOPE);
 
         jvmMethod().prepareBlock(closure.getHandle(), closure.getSignature(), jvm.clsData().clsName);
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
     private String file;
     private int lastLine = -1;
 }
diff --git a/spec/tags/ruby/language/block_tags.txt b/spec/tags/ruby/language/block_tags.txt
index 067e564f54..d1c37e7e2b 100644
--- a/spec/tags/ruby/language/block_tags.txt
+++ b/spec/tags/ruby/language/block_tags.txt
@@ -1,2 +1,2 @@
 fails:A block yielded a single Array assigns elements to optional arguments
-fails:A block yielded a single Array calls #to_hash on the last element when there are more arguments than parameters
+
