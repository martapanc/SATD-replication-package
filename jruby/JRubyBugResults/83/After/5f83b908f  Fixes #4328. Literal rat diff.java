diff --git a/core/src/main/java/org/jruby/ast/BignumNode.java b/core/src/main/java/org/jruby/ast/BignumNode.java
index eaa36a7e33..3e915297ea 100644
--- a/core/src/main/java/org/jruby/ast/BignumNode.java
+++ b/core/src/main/java/org/jruby/ast/BignumNode.java
@@ -1,75 +1,80 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 package org.jruby.ast;
 
 import java.math.BigInteger;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** 
  * Represents a big integer literal.
  */
 public class BignumNode extends NumericNode implements SideEffectFree {
     private BigInteger value;
 
     public BignumNode(ISourcePosition position, BigInteger value) {
         super(position);
         this.value = value;
     }
 
     public NodeType getNodeType() {
         return NodeType.BIGNUMNODE;
     }
 
     public <T> T accept(NodeVisitor<T> iVisitor) {
         return iVisitor.visitBignumNode(this);
     }
 
+    @Override
+    public NumericNode negate() {
+        return new BignumNode(getPosition(), value.negate());
+    }
+
     /**
      * Gets the value.
      * @return Returns a BigInteger
      */
     public BigInteger getValue() {
         return value;
     }
     
     public List<Node> childNodes() {
         return EMPTY_LIST;
     }
 
     public void setValue(BigInteger value) {
         this.value = value;
     }
 }
diff --git a/core/src/main/java/org/jruby/ast/FixnumNode.java b/core/src/main/java/org/jruby/ast/FixnumNode.java
index 508e6834d7..3a2385ca6c 100644
--- a/core/src/main/java/org/jruby/ast/FixnumNode.java
+++ b/core/src/main/java/org/jruby/ast/FixnumNode.java
@@ -1,74 +1,79 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** 
  * Represents an integer literal.
  */
 public class FixnumNode extends NumericNode implements ILiteralNode, SideEffectFree {
     private long value;
 
     public FixnumNode(ISourcePosition position, long value) {
         super(position);
         this.value = value;
     }
 
     public <T> T accept(NodeVisitor<T> iVisitor) {
         return iVisitor.visitFixnumNode(this);
     }
 
     public NodeType getNodeType() {
         return NodeType.FIXNUMNODE;
     }
 
+    @Override
+    public NumericNode negate() {
+        return new FixnumNode(getPosition(), -value);
+    }
+
     /**
      * Gets the value.
      * @return Returns a long
      */
     public long getValue() {
         return value;
     }
 
     public void setValue(long value) {
         this.value = value;
     }
 
     public List<Node> childNodes() {
         return EMPTY_LIST;
     }
 }
diff --git a/core/src/main/java/org/jruby/ast/FloatNode.java b/core/src/main/java/org/jruby/ast/FloatNode.java
index 9b94076d7d..4e30440c0d 100644
--- a/core/src/main/java/org/jruby/ast/FloatNode.java
+++ b/core/src/main/java/org/jruby/ast/FloatNode.java
@@ -1,79 +1,84 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** 
  * Represents a float literal.
  */
 public class FloatNode extends NumericNode implements ILiteralNode, SideEffectFree {
     private double value;
 
     public FloatNode(ISourcePosition position, double value) {
         super(position);
         this.value = value;
     }
 
     public NodeType getNodeType() {
         return NodeType.FLOATNODE;
     }
 
     public <T> T accept(NodeVisitor<T> iVisitor) {
         return iVisitor.visitFloatNode(this);
     }
 
+    @Override
+    public NumericNode negate() {
+        return new FloatNode(getPosition(), -value);
+    }
+
     /**
      * Gets the value.
      * @return Returns a double
      */
     public double getValue() {
         return value;
     }
     
     /**
      * Sets the value
      * @param value to set
      */
     public void setValue(double value) {
         this.value = value;
     }
 
     
     public List<Node> childNodes() {
         return EMPTY_LIST;
     }
 }
diff --git a/core/src/main/java/org/jruby/ast/NumericNode.java b/core/src/main/java/org/jruby/ast/NumericNode.java
index dbfc0925c3..74633db36d 100644
--- a/core/src/main/java/org/jruby/ast/NumericNode.java
+++ b/core/src/main/java/org/jruby/ast/NumericNode.java
@@ -1,13 +1,18 @@
 package org.jruby.ast;
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.lexer.yacc.ISourcePosition;
+import org.jruby.lexer.yacc.SyntaxException;
 
 /**
  * Any node representing a numeric value.
  */
 public abstract class NumericNode extends Node implements ILiteralNode {
     public NumericNode(ISourcePosition position) {
         super(position, false);
     }
+
+    public NumericNode negate() {
+        throw new IllegalArgumentException("Unexpected negation of a numeric type");
+    }
 }
diff --git a/core/src/main/java/org/jruby/ast/RationalNode.java b/core/src/main/java/org/jruby/ast/RationalNode.java
index a9087d2505..e1f8c50382 100644
--- a/core/src/main/java/org/jruby/ast/RationalNode.java
+++ b/core/src/main/java/org/jruby/ast/RationalNode.java
@@ -1,50 +1,55 @@
 /*
  * To change this license header, choose License Headers in Project Properties.
  * To change this template file, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.ast;
 
 import java.util.List;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author enebo
  */
 public class RationalNode extends NumericNode implements SideEffectFree {
-    private final long numerator;
-    private final long denominator;
+    private final NumericNode numerator;
+    private final NumericNode denominator;
 
-    public RationalNode(ISourcePosition position, long numerator, long denominator) {
+    public RationalNode(ISourcePosition position, NumericNode numerator, NumericNode denominator) {
         super(position);
         
         this.numerator = numerator;
         this.denominator = denominator;
     }
 
     @Override
     public Object accept(NodeVisitor visitor) {
         return visitor.visitRationalNode(this);
     }
 
     @Override
+    public NumericNode negate() {
+        return new RationalNode(getPosition(), numerator.negate(), denominator);
+    }
+
+    @Override
     public List<Node> childNodes() {
         return EMPTY_LIST;
     }
 
     @Override
     public NodeType getNodeType() {
         return NodeType.RATIONALNODE;
     }
 
-    public long getNumerator() {
+    public NumericNode getNumerator() {
         return numerator;
     }
 
-    public long getDenominator() {
+    public NumericNode getDenominator() {
         return denominator;
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/IRBuilder.java b/core/src/main/java/org/jruby/ir/IRBuilder.java
index 341fdfab8c..282f055517 100644
--- a/core/src/main/java/org/jruby/ir/IRBuilder.java
+++ b/core/src/main/java/org/jruby/ir/IRBuilder.java
@@ -2456,1692 +2456,1694 @@ public class IRBuilder {
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
 
     public Operand buildDRegexp(Variable result, DRegexpNode node) {
         Node[] nodePieces = node.children();
         Operand[] pieces = new Operand[nodePieces.length];
         for (int i = 0; i < pieces.length; i++) {
             pieces[i] = dynamicPiece(nodePieces[i]);
         }
 
         if (result == null) result = createTemporaryVariable();
         addInstr(new BuildDynRegExpInstr(result, pieces, node.getOptions()));
         return result;
     }
 
     public Operand buildDStr(Variable result, DStrNode node) {
         Node[] nodePieces = node.children();
         Operand[] pieces = new Operand[nodePieces.length];
         for (int i = 0; i < pieces.length; i++) {
             pieces[i] = dynamicPiece(nodePieces[i]);
         }
 
         if (result == null) result = createTemporaryVariable();
         addInstr(new BuildCompoundStringInstr(result, pieces, node.getEncoding(), node.isFrozen(), getFileName(), node.getLine()));
         return result;
     }
 
     public Operand buildDSymbol(Variable result, DSymbolNode node) {
         Node[] nodePieces = node.children();
         Operand[] pieces = new Operand[nodePieces.length];
         for (int i = 0; i < pieces.length; i++) {
             pieces[i] = dynamicPiece(nodePieces[i]);
         }
 
         if (result == null) result = createTemporaryVariable();
         addInstr(new BuildCompoundStringInstr(result, pieces, node.getEncoding(), false, getFileName(), node.getLine()));
         return copyAndReturnValue(new DynamicSymbol(result));
     }
 
     public Operand buildDVar(DVarNode node) {
         return getLocalVariable(node.getName(), node.getDepth());
     }
 
     public Operand buildDXStr(Variable result, DXStrNode dstrNode) {
         Node[] nodePieces = dstrNode.children();
         Operand[] pieces = new Operand[nodePieces.length];
         for (int i = 0; i < pieces.length; i++) {
             pieces[i] = dynamicPiece(nodePieces[i]);
         }
 
         if (result == null) result = createTemporaryVariable();
         return addResultInstr(new BacktickInstr(result, pieces));
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
 
     public Operand buildFCall(Variable result, FCallNode fcallNode) {
         Node      callArgsNode = fcallNode.getArgsNode();
         Operand[] args         = setupCallArgs(callArgsNode);
         Operand   block        = setupCallClosure(fcallNode.getIterNode());
 
         if (result == null) result = createTemporaryVariable();
 
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
 
         CallInstr callInstr = CallInstr.create(scope, CallType.FUNCTIONAL, result, fcallNode.getName(), buildSelf(), args, block);
         receiveBreakException(block, callInstr);
         return result;
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
 
     public Operand buildGlobalVar(Variable result, GlobalVarNode node) {
         if (result == null) result = createTemporaryVariable();
 
         return addResultInstr(new GetGlobalVariableInstr(result, node.getName()));
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
     public Operand buildIf(Variable result, final IfNode ifNode) {
         Node actualCondition = ifNode.getCondition();
 
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
             thenResult = build(result, ifNode.getThenBody());
             if (thenResult != U_NIL) { // thenResult can be U_NIL if then-body ended with a return!
                 // SSS FIXME: Can look at the last instr and short-circuit this jump if it is a break rather
                 // than wait for dead code elimination to do it
                 result = getValueInTemporaryVariable(thenResult);
                 addInstr(new JumpInstr(doneLabel));
             } else {
                 if (result == null) result = createTemporaryVariable();
                 thenUnil = true;
             }
         } else {
             thenNull = true;
             if (result == null) result = createTemporaryVariable();
             addInstr(new CopyInstr(result, manager.getNil()));
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
         Variable variable  = getLocalVariable(localAsgnNode.getName(), localAsgnNode.getDepth());
         Operand value = build(variable, localAsgnNode.getValueNode());
 
         // no use copying a variable to itself
         if (variable == value) return value;
 
         addInstr(new CopyInstr(variable, value));
 
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
 
     public Operand buildMatch(Variable result, MatchNode matchNode) {
         Operand regexp = build(matchNode.getRegexpNode());
 
         Variable tempLastLine = createTemporaryVariable();
         addResultInstr(new GetGlobalVariableInstr(tempLastLine, "$_"));
 
         if (result == null) result = createTemporaryVariable();
         return addResultInstr(new MatchInstr(result, regexp, tempLastLine));
     }
 
     public Operand buildMatch2(Variable result, Match2Node matchNode) {
         Operand receiver = build(matchNode.getReceiverNode());
         Operand value    = build(matchNode.getValueNode());
 
         if (result == null) result = createTemporaryVariable();
 
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
 
     public Operand buildMatch3(Variable result, Match3Node matchNode) {
         Operand receiver = build(matchNode.getReceiverNode());
         Operand value = build(matchNode.getValueNode());
 
         if (result == null) result = createTemporaryVariable();
 
         return addResultInstr(new MatchInstr(result, receiver, value));
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
 
         addInstr(new JumpInstr(endLabel));
         addInstr(new LabelInstr(lazyLabel));
         addInstr(new CopyInstr(result, manager.getNil()));
         addInstr(new LabelInstr(endLabel));
 
         return result;
     }
 
     // FIXME: All three paths feel a bit inefficient.  They all interact with the constant first to know
     // if it exists or whether it should raise if it doesn't...then it will access it against for the put
     // logic.  I could avoid that work but then there would be quite a bit more logic in Java.   This is a
     // pretty esoteric corner of Ruby so I am not inclined to put anything more than a comment that it can be
     // improved.
     public Operand buildOpAsgnConstDeclNode(OpAsgnConstDeclNode node) {
         String op = node.getOperator();
 
         if ("||".equals(op)) {
             Variable result = createTemporaryVariable();
             Label isDefined = getNewLabel();
             Label done = getNewLabel();
             Variable defined = createTemporaryVariable();
             addInstr(new CopyInstr(defined, buildGetDefinition(node.getFirstNode())));
             addInstr(BNEInstr.create(isDefined, defined, manager.getNil()));
             addInstr(new CopyInstr(result, putConstantAssignment(node, build(node.getSecondNode()))));
             addInstr(new JumpInstr(done));
             addInstr(new LabelInstr(isDefined));
             addInstr(new CopyInstr(result, build(node.getFirstNode())));
             addInstr(new LabelInstr(done));
             return result;
         } else if ("&&".equals(op)) {
             build(node.getFirstNode()); // Get once to make sure it is there or will throw if not
             return addResultInstr(new CopyInstr(createTemporaryVariable(), putConstantAssignment(node, build(node.getSecondNode()))));
         }
 
         Variable result = createTemporaryVariable();
         Operand lhs = build(node.getFirstNode());
         Operand rhs = build(node.getSecondNode());
         addInstr(CallInstr.create(scope, result, node.getOperator(), lhs, new Operand[] { rhs }, null));
         return addResultInstr(new CopyInstr(createTemporaryVariable(), putConstantAssignment(node, result)));
     }
 
     // Translate "x &&= y" --> "x = y if is_true(x)" -->
     //
     //    x = -- build(x) should return a variable! --
     //    f = is_true(x)
     //    beq(f, false, L)
     //    x = -- build(y) --
     // L:
     //
     public Operand buildOpAsgnAnd(OpAsgnAndNode andNode) {
         Label    l  = getNewLabel();
         Operand  v1 = build(andNode.getFirstNode());
         Variable result = getValueInTemporaryVariable(v1);
         addInstr(BEQInstr.create(v1, manager.getFalse(), l));
         Operand v2 = build(andNode.getSecondNode());  // This does the assignment!
         addInstr(new CopyInstr(result, v2));
         addInstr(new LabelInstr(l));
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
     public Operand buildOpAsgnOr(final OpAsgnOrNode orNode) {
         Label    l1 = getNewLabel();
         Label    l2 = null;
         Variable flag = createTemporaryVariable();
         Operand  v1;
         boolean  needsDefnCheck = orNode.getFirstNode().needsDefinitionCheck();
         if (needsDefnCheck) {
             l2 = getNewLabel();
             v1 = buildGetDefinition(orNode.getFirstNode());
             addInstr(new CopyInstr(flag, v1));
             addInstr(BEQInstr.create(flag, manager.getNil(), l2)); // if v1 is undefined, go to v2's computation
         }
         v1 = build(orNode.getFirstNode()); // build of 'x'
         addInstr(new CopyInstr(flag, v1));
         Variable result = getValueInTemporaryVariable(v1);
         if (needsDefnCheck) {
             addInstr(new LabelInstr(l2));
         }
         addInstr(BEQInstr.create(flag, manager.getTrue(), l1));  // if v1 is defined and true, we are done!
         Operand v2 = build(orNode.getSecondNode()); // This is an AST node that sets x = y, so nothing special to do here.
         addInstr(new CopyInstr(result, v2));
         addInstr(new LabelInstr(l1));
 
         // Return value of x ||= y is always 'x'
         return result;
     }
 
     public Operand buildOpElementAsgn(OpElementAsgnNode node) {
         // Translate "a[x] ||= n" --> "a[x] = n if !is_true(a[x])"
         if (node.isOr()) return buildOpElementAsgnWith(node, manager.getTrue());
 
         // Translate "a[x] &&= n" --> "a[x] = n if is_true(a[x])"
         if (node.isAnd()) return buildOpElementAsgnWith(node, manager.getFalse());
 
         // a[i] *= n, etc.  anything that is not "a[i] &&= .. or a[i] ||= .."
         return buildOpElementAsgnWithMethod(node);
     }
 
     private Operand buildOpElementAsgnWith(OpElementAsgnNode opElementAsgnNode, Boolean truthy) {
         Node receiver = opElementAsgnNode.getReceiverNode();
         CallType callType = receiver instanceof SelfNode ? CallType.FUNCTIONAL : CallType.NORMAL;
         Operand array = buildWithOrder(receiver, opElementAsgnNode.containsVariableAssignment());
         Label endLabel = getNewLabel();
         Variable elt = createTemporaryVariable();
         Operand[] argList = setupCallArgs(opElementAsgnNode.getArgsNode());
         addInstr(CallInstr.create(scope, callType, elt, "[]", array, argList, null));
         addInstr(BEQInstr.create(elt, truthy, endLabel));
         Operand value = build(opElementAsgnNode.getValueNode());
 
         argList = addArg(argList, value);
         addInstr(CallInstr.create(scope, callType, elt, "[]=", array, argList, null));
         addInstr(new CopyInstr(elt, value));
 
         addInstr(new LabelInstr(endLabel));
         return elt;
     }
 
     // a[i] *= n, etc.  anything that is not "a[i] &&= .. or a[i] ||= .."
     public Operand buildOpElementAsgnWithMethod(OpElementAsgnNode opElementAsgnNode) {
         Node receiver = opElementAsgnNode.getReceiverNode();
         CallType callType = receiver instanceof SelfNode ? CallType.FUNCTIONAL : CallType.NORMAL;
         Operand array = buildWithOrder(receiver, opElementAsgnNode.containsVariableAssignment());
         Operand[] argList = setupCallArgs(opElementAsgnNode.getArgsNode());
         Variable elt = createTemporaryVariable();
         addInstr(CallInstr.create(scope, callType, elt, "[]", array, argList, null)); // elt = a[args]
         Operand value = build(opElementAsgnNode.getValueNode());                                       // Load 'value'
         String  operation = opElementAsgnNode.getOperatorName();
         addInstr(CallInstr.create(scope, callType, elt, operation, elt, new Operand[] { value }, null)); // elt = elt.OPERATION(value)
         // SSS: do not load the call result into 'elt' to eliminate the RAW dependency on the call
         // We already know what the result is going be .. we are just storing it back into the array
         Variable tmp = createTemporaryVariable();
         argList = addArg(argList, elt);
         addInstr(CallInstr.create(scope, callType, tmp, "[]=", array, argList, null));   // a[args] = elt
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
     public Operand buildOr(final OrNode orNode) {
         // lazy evaluation opt.  Don't bother building rhs of expr is lhs is unconditionally true.
         if (orNode.getFirstNode().getNodeType().alwaysTrue()) return build(orNode.getFirstNode());
 
         // lazy evaluation opt. Eliminate conditional logic if we know lhs is always false.
         if (orNode.getFirstNode().getNodeType().alwaysFalse()) {
             build(orNode.getFirstNode());          // needs to be executed for potential side-effects
             return build(orNode.getSecondNode());  // but we return rhs for result.
         }
 
         Label endOfExprLabel = getNewLabel();
         Operand left = build(orNode.getFirstNode());
         Variable result = getValueInTemporaryVariable(left);
         addInstr(BEQInstr.create(left, manager.getTrue(), endOfExprLabel));
         Operand right  = build(orNode.getSecondNode());
         addInstr(new CopyInstr(result, right));
         addInstr(new LabelInstr(endOfExprLabel));
 
         return result;
     }
 
     private InterpreterContext buildPrePostExeInner(Node body) {
         // Set up %current_scope and %current_module
         addInstr(new CopyInstr(scope.getCurrentScopeVariable(), CURRENT_SCOPE[0]));
         addInstr(new CopyInstr(scope.getCurrentModuleVariable(), SCOPE_MODULE[0]));
         build(body);
 
         // END does not have either explicit or implicit return, so we add one
         addInstr(new ReturnInstr(new Nil()));
 
         return scope.allocateInterpreterContext(instructions);
     }
 
     public Operand buildPostExe(PostExeNode postExeNode) {
         IRScope topLevel = scope.getTopLevelScope();
         IRScope nearestLVarScope = scope.getNearestTopLocalVariableScope();
 
         IRClosure endClosure = new IRClosure(manager, scope, postExeNode.getLine(), nearestLVarScope.getStaticScope(), Signature.from(postExeNode), "_END_", true);
         // Create a new nested builder to ensure this gets its own IR builder state like the ensure block stack
         newIRBuilder(manager, endClosure).buildPrePostExeInner(postExeNode.getBodyNode());
 
         // Add an instruction in 's' to record the end block in the 'topLevel' scope.
         // SSS FIXME: IR support for end-blocks that access vars in non-toplevel-scopes
         // might be broken currently. We could either fix it or consider dropping support
         // for END blocks altogether or only support them in the toplevel. Not worth the pain.
         addInstr(new RecordEndBlockInstr(topLevel, new WrappedIRClosure(buildSelf(), endClosure)));
         return manager.getNil();
     }
 
     public Operand buildPreExe(PreExeNode preExeNode) {
         IRScope topLevel = scope.getTopLevelScope();
         IRClosure beginClosure = new IRFor(manager, scope, preExeNode.getLine(), topLevel.getStaticScope(),
                 Signature.from(preExeNode), "_BEGIN_");
         // Create a new nested builder to ensure this gets its own IR builder state like the ensure block stack
         newIRBuilder(manager, beginClosure).buildPrePostExeInner(preExeNode.getBodyNode());
 
         topLevel.recordBeginBlock(beginClosure);  // Record the begin block at IR build time
         return manager.getNil();
     }
 
     public Operand buildRational(RationalNode rationalNode) {
-        return new Rational(rationalNode.getNumerator(), rationalNode.getDenominator());
+
+        return new Rational((ImmutableLiteral) build(rationalNode.getNumerator()),
+                (ImmutableLiteral) build(rationalNode.getDenominator()));
     }
 
     public Operand buildRedo() {
         // If we have ensure blocks, have to run those first!
         if (!activeEnsureBlockStack.empty()) {
             emitEnsureBlocks(getCurrentLoop());
         }
 
         // If in a loop, a redo is a jump to the beginning of the loop.
         // If not, for closures, a redo is a jump to the beginning of the closure.
         // If not in a loop or a closure, it is a local jump error
         IRLoop currLoop = getCurrentLoop();
         if (currLoop != null) {
              addInstr(new JumpInstr(currLoop.iterStartLabel));
         } else {
             if (scope instanceof IRClosure) {
                 addInstr(new ThreadPollInstr(true));
                 addInstr(new JumpInstr(((IRClosure) scope).startLabel));
             } else {
                 addInstr(new ThrowExceptionInstr(IRException.REDO_LocalJumpError));
             }
         }
         return manager.getNil();
     }
 
     public Operand buildRegexp(RegexpNode reNode) {
         // SSS FIXME: Rather than throw syntax error at runtime, we should detect
         // regexp syntax errors at build time and add an exception-throwing instruction instead
         return copyAndReturnValue(new Regexp(reNode.getValue(), reNode.getOptions()));
     }
 
     public Operand buildRescue(RescueNode node) {
         return buildEnsureInternal(node, null);
     }
 
     private boolean canBacktraceBeRemoved(RescueNode rescueNode) {
         if (RubyInstanceConfig.FULL_TRACE_ENABLED || !(rescueNode instanceof RescueModNode) &&
                 rescueNode.getElseNode() != null) return false;
 
         RescueBodyNode rescueClause = rescueNode.getRescueNode();
 
         if (rescueClause.getOptRescueNode() != null) return false;  // We will not handle multiple rescues
         if (rescueClause.getExceptionNodes() != null) return false; // We cannot know if these are builtin or not statically.
 
         Node body = rescueClause.getBodyNode();
 
         // This optimization omits backtrace info for the exception getting rescued so we cannot
         // optimize the exception variable.
         if (body instanceof GlobalVarNode && ((GlobalVarNode) body).getName().equals("$!")) return false;
 
         // FIXME: This MIGHT be able to expand to more complicated expressions like Hash or Array if they
         // contain only SideEffectFree nodes.  Constructing a literal out of these should be safe from
         // effecting or being able to access $!.
         return body instanceof SideEffectFree;
     }
 
     private Operand buildRescueInternal(RescueNode rescueNode, EnsureBlockInfo ensure) {
         boolean needsBacktrace = !canBacktraceBeRemoved(rescueNode);
 
         // Labels marking start, else, end of the begin-rescue(-ensure)-end block
         Label rBeginLabel = getNewLabel();
         Label rEndLabel   = ensure.end;
         Label rescueLabel = getNewLabel(); // Label marking start of the first rescue code.
         ensure.needsBacktrace = needsBacktrace;
 
         addInstr(new LabelInstr(rBeginLabel));
 
         // Placeholder rescue instruction that tells rest of the compiler passes the boundaries of the rescue block.
         addInstr(new ExceptionRegionStartMarkerInstr(rescueLabel));
         activeRescuers.push(rescueLabel);
         addInstr(manager.needsBacktrace(needsBacktrace));
 
         // Body
         Operand tmp = manager.getNil();  // default return value if for some strange reason, we neither have the body node or the else node!
         Variable rv = createTemporaryVariable();
         if (rescueNode.getBodyNode() != null) tmp = build(rescueNode.getBodyNode());
 
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
         RescueBlockInfo rbi = new RescueBlockInfo(rBeginLabel, ensure.savedGlobalException);
         activeRescueBlockStack.push(rbi);
 
         // Since rescued regions are well nested within Ruby, this bare marker is sufficient to
         // let us discover the edge of the region during linear traversal of instructions during cfg construction.
         addInstr(new ExceptionRegionEndMarkerInstr());
         activeRescuers.pop();
 
         // Else part of the body -- we simply fall through from the main body if there were no exceptions
         if (rescueNode.getElseNode() != null) {
             addInstr(new LabelInstr(getNewLabel()));
             tmp = build(rescueNode.getElseNode());
         }
 
         if (tmp != U_NIL) {
             addInstr(new CopyInstr(rv, tmp));
 
             // No explicit return from the protected body
             // - If we dont have any ensure blocks, simply jump to the end of the rescue block
             // - If we do, execute the ensure code.
             ensure.cloneIntoHostScope(this);
             addInstr(new JumpInstr(rEndLabel, true));
         }   //else {
             // If the body had an explicit return, the return instruction IR build takes care of setting
             // up execution of all necessary ensure blocks. So, nothing to do here!
             //
             // Additionally, the value in 'rv' will never be used, so no need to set it to any specific value.
             // So, we can leave it undefined. If on the other hand, there was an exception in that block,
             // 'rv' will get set in the rescue handler -- see the 'rv' being passed into
             // buildRescueBodyInternal below. So, in either case, we are good!
             //}
 
         // Start of rescue logic
         addInstr(new LabelInstr(rescueLabel));
 
         // This is optimized no backtrace path so we need to reenable backtraces since we are
         // exiting that region.
         if (!needsBacktrace) addInstr(manager.needsBacktrace(true));
 
         // Save off exception & exception comparison type
         Variable exc = addResultInstr(new ReceiveRubyExceptionInstr(createTemporaryVariable()));
 
         // Build the actual rescue block(s)
         buildRescueBodyInternal(rescueNode.getRescueNode(), rv, exc, rEndLabel);
 
         activeRescueBlockStack.pop();
         return rv;
     }
 
     private void outputExceptionCheck(Operand excType, Operand excObj, Label caughtLabel) {
         Variable eqqResult = addResultInstr(new RescueEQQInstr(createTemporaryVariable(), excType, excObj));
         addInstr(BEQInstr.create(eqqResult, manager.getTrue(), caughtLabel));
     }
 
     private void buildRescueBodyInternal(RescueBodyNode rescueBodyNode, Variable rv, Variable exc, Label endLabel) {
         final Node exceptionList = rescueBodyNode.getExceptionNodes();
 
         // Compare and branch as necessary!
         Label uncaughtLabel = getNewLabel();
         Label caughtLabel = getNewLabel();
         if (exceptionList != null) {
             if (exceptionList instanceof ListNode) {
                 Node [] exceptionNodes = ((ListNode) exceptionList).children();
                 Operand[] exceptionTypes = new Operand[exceptionNodes.length];
                 for (int i = 0; i < exceptionNodes.length; i++) {
                     exceptionTypes[i] = build(exceptionNodes[i]);
                 }
                 outputExceptionCheck(new Array(exceptionTypes), exc, caughtLabel);
             } else if (exceptionList instanceof SplatNode) { // splatnode, catch
                 outputExceptionCheck(build(((SplatNode)exceptionList).getValue()), exc, caughtLabel);
             } else { // argscat/argspush
                 outputExceptionCheck(build(exceptionList), exc, caughtLabel);
             }
         } else {
             outputExceptionCheck(manager.getStandardError(), exc, caughtLabel);
         }
 
         // Uncaught exception -- build other rescue nodes or rethrow!
         addInstr(new LabelInstr(uncaughtLabel));
         if (rescueBodyNode.getOptRescueNode() != null) {
             buildRescueBodyInternal(rescueBodyNode.getOptRescueNode(), rv, exc, endLabel);
         } else {
             addInstr(new ThrowExceptionInstr(exc));
         }
 
         // Caught exception case -- build rescue body
         addInstr(new LabelInstr(caughtLabel));
         Node realBody = rescueBodyNode.getBodyNode();
         Operand x = build(realBody);
         if (x != U_NIL) { // can be U_NIL if the rescue block has an explicit return
             // Set up node return value 'rv'
             addInstr(new CopyInstr(rv, x));
 
             // Clone the topmost ensure block (which will be a wrapper
             // around the current rescue block)
             activeEnsureBlockStack.peek().cloneIntoHostScope(this);
 
             addInstr(new JumpInstr(endLabel, true));
         }
     }
 
     public Operand buildRetry() {
         // JRuby only supports retry when present in rescue blocks!
         // 1.9 doesn't support retry anywhere else.
 
         // SSS FIXME: We should be able to use activeEnsureBlockStack for this
         // But, see the code in buildRescueInternal that pushes/pops these and
         // the documentation for retries.  There is a small ordering issue
         // which is preventing me from getting rid of activeRescueBlockStack
         // altogether!
         //
         // Jump back to the innermost rescue block
         // We either find it, or we add code to throw a runtime exception
         if (activeRescueBlockStack.empty()) {
             addInstr(new ThrowExceptionInstr(IRException.RETRY_LocalJumpError));
         } else {
             addInstr(new ThreadPollInstr(true));
             // Restore $! and jump back to the entry of the rescue block
             RescueBlockInfo rbi = activeRescueBlockStack.peek();
             addInstr(new PutGlobalVarInstr("$!", rbi.savedExceptionVariable));
             addInstr(new JumpInstr(rbi.entryLabel));
             // Retries effectively create a loop
             scope.setHasLoopsFlag();
         }
         return manager.getNil();
     }
 
     private Operand processEnsureRescueBlocks(Operand retVal) {
         // Before we return,
         // - have to go execute all the ensure blocks if there are any.
         //   this code also takes care of resetting "$!"
         if (!activeEnsureBlockStack.empty()) {
             retVal = addResultInstr(new CopyInstr(createTemporaryVariable(), retVal));
             emitEnsureBlocks(null);
         }
        return retVal;
     }
 
     public Operand buildReturn(ReturnNode returnNode) {
         Operand retVal = build(returnNode.getValueNode());
 
         if (scope instanceof IRClosure) {
             // If 'm' is a block scope, a return returns from the closest enclosing method.
             // If this happens to be a module body, the runtime throws a local jump error if the
             // closure is a proc. If the closure is a lambda, then this becomes a normal return.
             boolean maybeLambda = scope.getNearestMethod() == null;
             addInstr(new CheckForLJEInstr(maybeLambda));
             addInstr(new NonlocalReturnInstr(retVal, maybeLambda ? "--none--" : scope.getNearestMethod().getName()));
         } else if (scope.isModuleBody()) {
             IRMethod sm = scope.getNearestMethod();
 
             // Cannot return from top-level module bodies!
             if (sm == null) addInstr(new ThrowExceptionInstr(IRException.RETURN_LocalJumpError));
             if (sm != null) addInstr(new NonlocalReturnInstr(retVal, sm.getName()));
         } else {
             retVal = processEnsureRescueBlocks(retVal);
 
             if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
                 addInstr(new TraceInstr(RubyEvent.RETURN, getName(), getFileName(), returnNode.getLine()));
             }
 
             addInstr(new ReturnInstr(retVal));
         }
 
         // The value of the return itself in the containing expression can never be used because of control-flow reasons.
         // The expression that uses this result can never be executed beyond the return and hence the value itself is just
         // a placeholder operand.
         return U_NIL;
     }
 
     public InterpreterContext buildEvalRoot(RootNode rootNode) {
         needsCodeCoverage = false;  // Assuming there is no path into build eval root without actually being an eval.
         addInstr(manager.newLineNumber(scope.getLineNumber()));
 
         prepareImplicitState();                                    // recv_self, add frame block, etc)
         addCurrentScopeAndModule();                                // %current_scope/%current_module
 
         Operand returnValue = rootNode.getBodyNode() == null ? manager.getNil() : build(rootNode.getBodyNode());
         addInstr(new ReturnInstr(returnValue));
 
         return scope.allocateInterpreterContext(instructions);
     }
 
     public static InterpreterContext buildRoot(IRManager manager, RootNode rootNode) {
         IRScriptBody script = new IRScriptBody(manager, rootNode.getFile(), rootNode.getStaticScope());
 
         return topIRBuilder(manager, script).buildRootInner(rootNode);
     }
 
     private void addCurrentScopeAndModule() {
         addInstr(new CopyInstr(scope.getCurrentScopeVariable(), CURRENT_SCOPE[0])); // %current_scope
         addInstr(new CopyInstr(scope.getCurrentModuleVariable(), SCOPE_MODULE[0])); // %current_module
     }
 
     private InterpreterContext buildRootInner(RootNode rootNode) {
         needsCodeCoverage = rootNode.needsCoverage();
         prepareImplicitState();                                    // recv_self, add frame block, etc)
         addCurrentScopeAndModule();                                // %current_scope/%current_module
 
         // Build IR for the tree and return the result of the expression tree
         addInstr(new ReturnInstr(build(rootNode.getBodyNode())));
 
         return scope.allocateInterpreterContext(instructions);
     }
 
     public Variable buildSelf() {
         return scope.getSelf();
     }
 
     public Operand buildSplat(SplatNode splatNode) {
         return addResultInstr(new BuildSplatInstr(createTemporaryVariable(), build(splatNode.getValue()), true));
     }
 
     public Operand buildStr(StrNode strNode) {
         Operand literal = buildStrRaw(strNode);
 
         return literal instanceof FrozenString ? literal : copyAndReturnValue(literal);
     }
 
     public Operand buildStrRaw(StrNode strNode) {
         if (strNode instanceof FileNode) return new Filename();
 
         ISourcePosition pos = strNode.getPosition();
 
         if (strNode.isFrozen()) return new FrozenString(strNode.getValue(), strNode.getCodeRange(), pos.getFile(), pos.getLine());
 
         return new StringLiteral(strNode.getValue(), strNode.getCodeRange(), pos.getFile(), pos.getLine());
     }
 
     private Operand buildSuperInstr(Operand block, Operand[] args) {
         CallInstr superInstr;
         Variable ret = createTemporaryVariable();
         if (scope instanceof IRMethod && scope.getLexicalParent() instanceof IRClassBody) {
             if (((IRMethod) scope).isInstanceMethod) {
                 superInstr = new InstanceSuperInstr(ret, scope.getCurrentModuleVariable(), getName(), args, block, scope.maybeUsingRefinements());
             } else {
                 superInstr = new ClassSuperInstr(ret, scope.getCurrentModuleVariable(), getName(), args, block, scope.maybeUsingRefinements());
             }
         } else {
             // We dont always know the method name we are going to be invoking if the super occurs in a closure.
             // This is because the super can be part of a block that will be used by 'define_method' to define
             // a new method.  In that case, the method called by super will be determined by the 'name' argument
             // to 'define_method'.
             superInstr = new UnresolvedSuperInstr(ret, buildSelf(), args, block, scope.maybeUsingRefinements());
         }
         receiveBreakException(block, superInstr);
         return ret;
     }
 
     public Operand buildSuper(SuperNode superNode) {
         if (scope.isModuleBody()) return buildSuperInScriptBody();
 
         Operand[] args = setupCallArgs(superNode.getArgsNode());
         Operand block = setupCallClosure(superNode.getIterNode());
         if (block == null) block = scope.getYieldClosureVariable();
         return buildSuperInstr(block, args);
     }
 
     private Operand buildSuperInScriptBody() {
         return addResultInstr(new UnresolvedSuperInstr(createTemporaryVariable(), buildSelf(), NO_ARGS, null, scope.maybeUsingRefinements()));
     }
 
     public Operand buildSValue(SValueNode node) {
         // SSS FIXME: Required? Verify with Tom/Charlie
         return copyAndReturnValue(new SValue(build(node.getValue())));
     }
 
     public Operand buildSymbol(SymbolNode node) {
         // Since symbols are interned objects, no need to copyAndReturnValue(...)
         // SSS FIXME: Premature opt?
         return new Symbol(node.getName(), node.getEncoding());
     }
 
     public Operand buildTrue() {
         return manager.getTrue();
     }
 
     public Operand buildUndef(Node node) {
         Operand methName = build(((UndefNode) node).getName());
         return addResultInstr(new UndefMethodInstr(createTemporaryVariable(), methName));
     }
 
     private Operand buildConditionalLoop(Node conditionNode,
                                          Node bodyNode, boolean isWhile, boolean isLoopHeadCondition) {
         if (isLoopHeadCondition &&
                 ((isWhile && conditionNode.getNodeType().alwaysFalse()) ||
                 (!isWhile && conditionNode.getNodeType().alwaysTrue()))) {
             // we won't enter the loop -- just build the condition node
             build(conditionNode);
             return manager.getNil();
         } else {
             IRLoop loop = new IRLoop(scope, getCurrentLoop());
             Variable loopResult = loop.loopResult;
             Label setupResultLabel = getNewLabel();
 
             // Push new loop
             loopStack.push(loop);
 
             // End of iteration jumps here
             addInstr(new LabelInstr(loop.loopStartLabel));
             if (isLoopHeadCondition) {
                 Operand cv = build(conditionNode);
                 addInstr(BEQInstr.create(cv, isWhile ? manager.getFalse() : manager.getTrue(), setupResultLabel));
             }
 
             // Redo jumps here
             addInstr(new LabelInstr(loop.iterStartLabel));
 
             // Thread poll at start of iteration -- ensures that redos and nexts run one thread-poll per iteration
             addInstr(new ThreadPollInstr(true));
 
             // Build body
             if (bodyNode != null) build(bodyNode);
 
             // Next jumps here
             addInstr(new LabelInstr(loop.iterEndLabel));
             if (isLoopHeadCondition) {
                 addInstr(new JumpInstr(loop.loopStartLabel));
             } else {
                 Operand cv = build(conditionNode);
                 addInstr(BEQInstr.create(cv, isWhile ? manager.getTrue() : manager.getFalse(), loop.iterStartLabel));
             }
 
             // Loop result -- nil always
             addInstr(new LabelInstr(setupResultLabel));
             addInstr(new CopyInstr(loopResult, manager.getNil()));
 
             // Loop end -- breaks jump here bypassing the result set up above
             addInstr(new LabelInstr(loop.loopEndLabel));
 
             // Done with loop
             loopStack.pop();
 
             return loopResult;
         }
     }
 
     public Operand buildUntil(final UntilNode untilNode) {
         return buildConditionalLoop(untilNode.getConditionNode(), untilNode.getBodyNode(), false, untilNode.evaluateAtStart());
     }
 
     public Operand buildVAlias(VAliasNode valiasNode) {
         addInstr(new GVarAliasInstr(new StringLiteral(valiasNode.getNewName()), new StringLiteral(valiasNode.getOldName())));
 
         return manager.getNil();
     }
 
     public Operand buildVCall(Variable result, VCallNode node) {
         if (result == null) result = createTemporaryVariable();
 
         return addResultInstr(CallInstr.create(scope, CallType.VARIABLE, result, node.getName(), buildSelf(), NO_ARGS, null));
     }
 
     public Operand buildWhile(final WhileNode whileNode) {
         return buildConditionalLoop(whileNode.getConditionNode(), whileNode.getBodyNode(), true, whileNode.evaluateAtStart());
     }
 
     public Operand buildXStr(XStrNode node) {
         return addResultInstr(new BacktickInstr(createTemporaryVariable(), new Operand[] { new FrozenString(node.getValue(), node.getCodeRange(), node.getPosition().getFile(), node.getPosition().getLine())}));
     }
 
     public Operand buildYield(YieldNode node, Variable result) {
         boolean unwrap = true;
         Node argNode = node.getArgsNode();
         // Get rid of one level of array wrapping
         if (argNode != null && (argNode instanceof ArrayNode) && ((ArrayNode)argNode).size() == 1) {
             argNode = ((ArrayNode)argNode).getLast();
             unwrap = false;
         }
 
         Variable ret = result == null ? createTemporaryVariable() : result;
         if (argNode instanceof ArrayNode && unwrap) {
             addInstr(new YieldInstr(ret, scope.getYieldClosureVariable(), buildArray((ArrayNode)argNode, true), unwrap));
         } else {
             addInstr(new YieldInstr(ret, scope.getYieldClosureVariable(), build(argNode), unwrap));
         }
         return ret;
     }
 
     public Operand copy(Operand value) {
         return copy(null, value);
     }
 
     public Operand copy(Variable result, Operand value) {
         return addResultInstr(new CopyInstr(result == null ? createTemporaryVariable() : result, value));
     }
 
     public Operand buildZArray(Variable result) {
        return copy(result, new Array());
     }
 
     private Operand buildZSuperIfNest(final Operand block) {
         final IRBuilder builder = this;
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
                 Variable scopeDepth = createTemporaryVariable();
                 addInstr(new ArgScopeDepthInstr(scopeDepth));
 
                 Label allDoneLabel = getNewLabel();
 
                 int depthFromSuper = 0;
                 Label next = null;
                 IRBuilder superBuilder = builder;
                 IRScope superScope = scope;
 
                 // Loop and generate a block for each possible value of depthFromSuper
                 Variable zsuperResult = createTemporaryVariable();
                 while (superScope instanceof IRClosure) {
                     // Generate the next set of instructions
                     if (next != null) addInstr(new LabelInstr(next));
                     next = getNewLabel();
                     addInstr(BNEInstr.create(next, new Fixnum(depthFromSuper), scopeDepth));
                     Operand[] args = adjustVariableDepth(getCallArgs(superScope, superBuilder), depthFromSuper);
                     addInstr(new ZSuperInstr(zsuperResult, buildSelf(), args,  block, scope.maybeUsingRefinements()));
                     addInstr(new JumpInstr(allDoneLabel));
 
                     // We may run out of live builds and walk int already built scopes if zsuper in an eval
                     superBuilder = superBuilder != null && superBuilder.parent != null ? superBuilder.parent : null;
                     superScope = superScope.getLexicalParent();
                     depthFromSuper++;
                 }
 
                 addInstr(new LabelInstr(next));
 
                 // If we hit a method, this is known to always succeed
                 if (superScope instanceof IRMethod) {
                     Operand[] args = adjustVariableDepth(getCallArgs(superScope, superBuilder), depthFromSuper);
                     addInstr(new ZSuperInstr(zsuperResult, buildSelf(), args, block, scope.maybeUsingRefinements()));
                 } //else {
                 // FIXME: Do or don't ... there is no try
                     /* Control should never get here in the runtime */
                     /* Should we add an exception throw here just in case? */
                 //}
 
                 addInstr(new LabelInstr(allDoneLabel));
                 return zsuperResult;
             }
         };
 
         return receiveBreakException(block, zsuperBuilder);
     }
 
     public Operand buildZSuper(ZSuperNode zsuperNode) {
         if (scope.isModuleBody()) return buildSuperInScriptBody();
 
         Operand block = setupCallClosure(zsuperNode.getIterNode());
         if (block == null) block = scope.getYieldClosureVariable();
 
         // Enebo:ZSuper in for (or nested for) can be statically resolved like method but it needs to fixup depth.
         if (scope instanceof IRMethod) {
             return buildSuperInstr(block, getCallArgs(scope, this));
         } else {
             return buildZSuperIfNest(block);
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
 
     private InterpreterContext buildModuleOrClassBody(Node bodyNode, int linenumber) {
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             addInstr(new TraceInstr(RubyEvent.CLASS, null, getFileName(), linenumber));
         }
 
         prepareImplicitState();                                    // recv_self, add frame block, etc)
         addCurrentScopeAndModule();                                // %current_scope/%current_module
 
         Operand bodyReturnValue = build(bodyNode);
 
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // FIXME: We also should add a line number instruction so that backtraces
             // inside the trace function get the correct line. Unfortunately, we don't
             // have one here and we can't do it dynamically like TraceInstr does.
             // See https://github.com/jruby/jruby/issues/4051
             addInstr(new TraceInstr(RubyEvent.END, null, getFileName(), -1));
         }
 
         addInstr(new ReturnInstr(bodyReturnValue));
 
         return scope.allocateInterpreterContext(instructions);
     }
 
     private String methodNameFor() {
         IRScope method = scope.getNearestMethod();
 
         return method == null ? null : method.getName();
     }
 
     private TemporaryVariable createTemporaryVariable() {
         return scope.createTemporaryVariable();
     }
 
     public LocalVariable getLocalVariable(String name, int scopeDepth) {
         return scope.getLocalVariable(name, scopeDepth);
     }
 
     public LocalVariable getNewLocalVariable(String name, int scopeDepth) {
         return scope.getNewLocalVariable(name, scopeDepth);
     }
 
     public String getName() {
         return scope.getName();
     }
 
     private Label getNewLabel() {
         return scope.getNewLabel();
     }
 
     private String getFileName() {
         return scope.getFileName();
     }
 
     public void initFlipStateVariable(Variable v, Operand initState) {
         addInstrAtBeginning(new CopyInstr(v, initState));
     }
 
     /**
      * Extract all call arguments from the specified scope (only useful for Closures and Methods) so that
      * we can convert zsupers to supers with explicit arguments.
      *
      * Note: This is fairly expensive because we walk entire scope when we could potentially stop earlier
      * if we knew when recv_* were done.
      */
     public static Operand[] getCallArgs(IRScope scope, IRBuilder builder) {
         List<Operand> callArgs = new ArrayList<>(5);
         List<KeyValuePair<Operand, Operand>> keywordArgs = new ArrayList<>(3);
 
         if (builder != null) {  // Still in currently building scopes
             for (Instr instr : builder.instructions) {
                 extractCallOperands(callArgs, keywordArgs, instr);
             }
         } else {               // walked out past the eval to already build scopes
             for (Instr instr : scope.interpreterContext.getInstructions()) {
                 extractCallOperands(callArgs, keywordArgs, instr);
             }
         }
 
         return getCallOperands(scope, callArgs, keywordArgs);
     }
 
 
     private static void extractCallOperands(List<Operand> callArgs, List<KeyValuePair<Operand, Operand>> keywordArgs, Instr instr) {
         if (instr instanceof ReceiveKeywordRestArgInstr) {
             // Always add the keyword rest arg to the beginning
             keywordArgs.add(0, new KeyValuePair<Operand, Operand>(Symbol.KW_REST_ARG_DUMMY, ((ReceiveArgBase) instr).getResult()));
         } else if (instr instanceof ReceiveKeywordArgInstr) {
             ReceiveKeywordArgInstr rkai = (ReceiveKeywordArgInstr) instr;
             // FIXME: This lost encoding information when name was converted to string earlier in IRBuilder
             keywordArgs.add(new KeyValuePair<Operand, Operand>(new Symbol(rkai.argName, USASCIIEncoding.INSTANCE), rkai.getResult()));
         } else if (instr instanceof ReceiveRestArgInstr) {
             callArgs.add(new Splat(((ReceiveRestArgInstr) instr).getResult()));
         } else if (instr instanceof ReceiveArgBase) {
             callArgs.add(((ReceiveArgBase) instr).getResult());
         }
     }
 
     private static Operand[] getCallOperands(IRScope scope, List<Operand> callArgs, List<KeyValuePair<Operand, Operand>> keywordArgs) {
         if (scope.receivesKeywordArgs()) {
             int i = 0;
             Operand[] args = new Operand[callArgs.size() + 1];
             for (Operand arg: callArgs) {
                 args[i++] = arg;
             }
             args[i] = new Hash(keywordArgs, true);
             return args;
         }
 
         return callArgs.toArray(new Operand[callArgs.size()]);
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/operands/Rational.java b/core/src/main/java/org/jruby/ir/operands/Rational.java
index 2063fc2c5b..6e19fb212b 100644
--- a/core/src/main/java/org/jruby/ir/operands/Rational.java
+++ b/core/src/main/java/org/jruby/ir/operands/Rational.java
@@ -1,47 +1,50 @@
 package org.jruby.ir.operands;
 
+import org.jruby.RubyRational;
 import org.jruby.ir.IRVisitor;
 import org.jruby.runtime.ThreadContext;
+import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * Literal Rational number.
  */
 public class Rational extends ImmutableLiteral {
-    private final long numerator;
-    private final long denominator;
+    private final ImmutableLiteral numerator;
+    private final ImmutableLiteral denominator;
 
-    public Rational(long numerator, long denominator) {
+    public Rational(ImmutableLiteral numerator, ImmutableLiteral denominator) {
         super();
 
         this.numerator = numerator;
         this.denominator = denominator;
     }
 
     @Override
     public OperandType getOperandType() {
         return OperandType.RATIONAL;
     }
 
     @Override
     public Object createCacheObject(ThreadContext context) {
-        return context.runtime.newRational(numerator, denominator);
+        return RubyRational.newRationalRaw(context.runtime,
+                (IRubyObject) numerator.cachedObject(context), (IRubyObject) denominator.cachedObject(context));
     }
 
     @Override
     public String toString() {
         return "Rational:" + numerator + "/1";
     }
 
     @Override
     public void visit(IRVisitor visitor) {
         visitor.Rational(this);
     }
 
-    public long getNumerator() {
+    public ImmutableLiteral getNumerator() {
         return numerator;
     }
 
-    public long getDenominator() {
+    public ImmutableLiteral getDenominator() {
         return denominator;
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
index 438c52fbca..b4bbb917cd 100644
--- a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+++ b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
@@ -1461,1134 +1461,1134 @@ public class JVMVisitor extends IRVisitor {
 
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
         boolean reuseParentDynScope = scope.getExecutionContext().getFlags().contains(IRFlags.REUSE_PARENT_DYNSCOPE);
         boolean pushNewDynScope = !scope.getExecutionContext().getFlags().contains(IRFlags.DYNSCOPE_ELIMINATED) && !reuseParentDynScope;
 
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
         jvmMethod().setGlobalVariable(putglobalvarinstr.getTarget().getName(), file, lastLine);
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
         jvmAdapter().pushInt(instr.optArgsCount);
         jvmAdapter().pushBoolean(instr.restArg);
         jvmAdapter().pushInt(instr.postReqdArgsCount);
         jvmAdapter().pushInt(instr.getArgIndex());
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
         jvmMethod().invokeIRHelper("receivePostReqdArg", sig(IRubyObject.class, ThreadContext.class, IRubyObject[].class, int.class, int.class, boolean.class, int.class, int.class, boolean.class));
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
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedBackref", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_CALL:
                 jvmMethod().loadContext();
                 jvmMethod().loadSelf();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((StringLiteral) runtimehelpercall.getArgs()[1]).getString());
                 visit(runtimehelpercall.getArgs()[2]);
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedCall", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, String.class, IRubyObject.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_CONSTANT_OR_METHOD:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[1]).getString());
                 visit(runtimehelpercall.getArgs()[2]);
                 visit(runtimehelpercall.getArgs()[3]);
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedConstantOrMethod", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class, IRubyObject.class, IRubyObject.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_NTH_REF:
                 jvmMethod().loadContext();
                 jvmAdapter().ldc((int)((Fixnum)runtimehelpercall.getArgs()[0]).getValue());
                 visit(runtimehelpercall.getArgs()[1]);
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedNthRef", sig(IRubyObject.class, ThreadContext.class, int.class, IRubyObject.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_GLOBAL:
                 jvmMethod().loadContext();
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[0]).getString());
                 visit(runtimehelpercall.getArgs()[1]);
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedGlobal", sig(IRubyObject.class, ThreadContext.class, String.class, IRubyObject.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_INSTANCE_VAR:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[1]).getString());
                 visit(runtimehelpercall.getArgs()[2]);
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedInstanceVar", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class, IRubyObject.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_CLASS_VAR:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().checkcast(p(RubyModule.class));
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[1]).getString());
                 visit(runtimehelpercall.getArgs()[2]);
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedClassVar", sig(IRubyObject.class, ThreadContext.class, RubyModule.class, String.class, IRubyObject.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_SUPER:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmMethod().loadFrameName();
                 jvmMethod().loadFrameClass();
                 visit(runtimehelpercall.getArgs()[1]);
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedSuper", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class, RubyModule.class, IRubyObject.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_METHOD:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((Stringable) runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().ldc(((Boolean)runtimehelpercall.getArgs()[2]).isTrue());
                 visit(runtimehelpercall.getArgs()[3]);
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedMethod", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class, boolean.class, IRubyObject.class));
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
-        jvmAdapter().ldc(rational.getNumerator());
-        jvmAdapter().ldc(rational.getDenominator());
-        jvmAdapter().invokevirtual(p(Ruby.class), "newRational", sig(RubyRational.class, long.class, long.class));
+        visit(rational.getNumerator());
+        visit(rational.getDenominator());
+        jvmAdapter().invokevirtual(p(RubyRational.class), "newRationalRaw", sig(RubyRational.class, Ruby.class, IRubyObject.class, IRubyObject.class));
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
diff --git a/core/src/main/java/org/jruby/lexer/yacc/RubyLexer.java b/core/src/main/java/org/jruby/lexer/yacc/RubyLexer.java
index cf77463377..474e53349c 100644
--- a/core/src/main/java/org/jruby/lexer/yacc/RubyLexer.java
+++ b/core/src/main/java/org/jruby/lexer/yacc/RubyLexer.java
@@ -1,1432 +1,1441 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004-2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Zach Dennis <zdennis@mktec.com>
  * Copyright (C) 2006 Thomas Corbat <tcorbat@hsr.ch>
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
 package org.jruby.lexer.yacc;
 
 import java.io.IOException;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.HashMap;
 import org.jcodings.Encoding;
 import org.jruby.Ruby;
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.BignumNode;
 import org.jruby.ast.ComplexNode;
 import org.jruby.ast.FixnumNode;
 import org.jruby.ast.FloatNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NthRefNode;
 import org.jruby.ast.NumericNode;
 import org.jruby.ast.RationalNode;
 import org.jruby.ast.StrNode;
 import org.jruby.common.IRubyWarnings;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.lexer.LexerSource;
 import org.jruby.lexer.LexingCommon;
 import org.jruby.lexer.yacc.SyntaxException.PID;
 import org.jruby.parser.ParserSupport;
 import org.jruby.parser.RubyParser;
 import org.jruby.parser.Tokens;
 import org.jruby.util.ByteList;
 import org.jruby.util.SafeDoubleParser;
 import org.jruby.util.StringSupport;
 import org.jruby.util.cli.Options;
 
 /*
  * This is a port of the MRI lexer to Java.
  */
 public class RubyLexer extends LexingCommon {
     private static final HashMap<String, Keyword> map;
 
     static {
         map = new HashMap<String, Keyword>();
 
         map.put("end", Keyword.END);
         map.put("else", Keyword.ELSE);
         map.put("case", Keyword.CASE);
         map.put("ensure", Keyword.ENSURE);
         map.put("module", Keyword.MODULE);
         map.put("elsif", Keyword.ELSIF);
         map.put("def", Keyword.DEF);
         map.put("rescue", Keyword.RESCUE);
         map.put("not", Keyword.NOT);
         map.put("then", Keyword.THEN);
         map.put("yield", Keyword.YIELD);
         map.put("for", Keyword.FOR);
         map.put("self", Keyword.SELF);
         map.put("false", Keyword.FALSE);
         map.put("retry", Keyword.RETRY);
         map.put("return", Keyword.RETURN);
         map.put("true", Keyword.TRUE);
         map.put("if", Keyword.IF);
         map.put("defined?", Keyword.DEFINED_P);
         map.put("super", Keyword.SUPER);
         map.put("undef", Keyword.UNDEF);
         map.put("break", Keyword.BREAK);
         map.put("in", Keyword.IN);
         map.put("do", Keyword.DO);
         map.put("nil", Keyword.NIL);
         map.put("until", Keyword.UNTIL);
         map.put("unless", Keyword.UNLESS);
         map.put("or", Keyword.OR);
         map.put("next", Keyword.NEXT);
         map.put("when", Keyword.WHEN);
         map.put("redo", Keyword.REDO);
         map.put("and", Keyword.AND);
         map.put("begin", Keyword.BEGIN);
         map.put("__LINE__", Keyword.__LINE__);
         map.put("class", Keyword.CLASS);
         map.put("__FILE__", Keyword.__FILE__);
         map.put("END", Keyword.LEND);
         map.put("BEGIN", Keyword.LBEGIN);
         map.put("while", Keyword.WHILE);
         map.put("alias", Keyword.ALIAS);
         map.put("__ENCODING__", Keyword.__ENCODING__);
     }
 
     private BignumNode newBignumNode(String value, int radix) {
         return new BignumNode(getPosition(), new BigInteger(value, radix));
     }
 
     private FixnumNode newFixnumNode(String value, int radix) throws NumberFormatException {
         return new FixnumNode(getPosition(), Long.parseLong(value, radix));
     }
     
     private RationalNode newRationalNode(String value, int radix) throws NumberFormatException {
-        return new RationalNode(getPosition(), Long.parseLong(value, radix), 1);
+        NumericNode numerator;
+
+        try {
+            numerator = new FixnumNode(getPosition(), Long.parseLong(value, radix));
+        } catch (NumberFormatException e) {
+            numerator = new BignumNode(getPosition(), new BigInteger(value, radix));
+        }
+
+        return new RationalNode(getPosition(), numerator, new FixnumNode(getPosition(), 1));
     }
     
     private ComplexNode newComplexNode(NumericNode number) {
         return new ComplexNode(getPosition(), number);
     }
     
     protected void ambiguousOperator(String op, String syn) {
         warnings.warn(ID.AMBIGUOUS_ARGUMENT, getPosition(), "`" + op + "' after local variable or literal is interpreted as binary operator");
         warnings.warn(ID.AMBIGUOUS_ARGUMENT, getPosition(), "even though it seems like " + syn);
     }
    
     public enum Keyword {
         END ("end", Tokens.kEND, Tokens.kEND, EXPR_END),
         ELSE ("else", Tokens.kELSE, Tokens.kELSE, EXPR_BEG),
         CASE ("case", Tokens.kCASE, Tokens.kCASE, EXPR_BEG),
         ENSURE ("ensure", Tokens.kENSURE, Tokens.kENSURE, EXPR_BEG),
         MODULE ("module", Tokens.kMODULE, Tokens.kMODULE, EXPR_BEG),
         ELSIF ("elsif", Tokens.kELSIF, Tokens.kELSIF, EXPR_BEG),
         DEF ("def", Tokens.kDEF, Tokens.kDEF, EXPR_FNAME),
         RESCUE ("rescue", Tokens.kRESCUE, Tokens.kRESCUE_MOD, EXPR_MID),
         NOT ("not", Tokens.kNOT, Tokens.kNOT, EXPR_ARG),
         THEN ("then", Tokens.kTHEN, Tokens.kTHEN, EXPR_BEG),
         YIELD ("yield", Tokens.kYIELD, Tokens.kYIELD, EXPR_ARG),
         FOR ("for", Tokens.kFOR, Tokens.kFOR, EXPR_BEG),
         SELF ("self", Tokens.kSELF, Tokens.kSELF, EXPR_END),
         FALSE ("false", Tokens.kFALSE, Tokens.kFALSE, EXPR_END),
         RETRY ("retry", Tokens.kRETRY, Tokens.kRETRY, EXPR_END),
         RETURN ("return", Tokens.kRETURN, Tokens.kRETURN, EXPR_MID),
         TRUE ("true", Tokens.kTRUE, Tokens.kTRUE, EXPR_END),
         IF ("if", Tokens.kIF, Tokens.kIF_MOD, EXPR_BEG),
         DEFINED_P ("defined?", Tokens.kDEFINED, Tokens.kDEFINED, EXPR_ARG),
         SUPER ("super", Tokens.kSUPER, Tokens.kSUPER, EXPR_ARG),
         UNDEF ("undef", Tokens.kUNDEF, Tokens.kUNDEF, EXPR_FNAME|EXPR_FITEM),
         BREAK ("break", Tokens.kBREAK, Tokens.kBREAK, EXPR_MID),
         IN ("in", Tokens.kIN, Tokens.kIN, EXPR_BEG),
         DO ("do", Tokens.kDO, Tokens.kDO, EXPR_BEG),
         NIL ("nil", Tokens.kNIL, Tokens.kNIL, EXPR_END),
         UNTIL ("until", Tokens.kUNTIL, Tokens.kUNTIL_MOD, EXPR_BEG),
         UNLESS ("unless", Tokens.kUNLESS, Tokens.kUNLESS_MOD, EXPR_BEG),
         OR ("or", Tokens.kOR, Tokens.kOR, EXPR_BEG),
         NEXT ("next", Tokens.kNEXT, Tokens.kNEXT, EXPR_MID),
         WHEN ("when", Tokens.kWHEN, Tokens.kWHEN, EXPR_BEG),
         REDO ("redo", Tokens.kREDO, Tokens.kREDO, EXPR_END),
         AND ("and", Tokens.kAND, Tokens.kAND, EXPR_BEG),
         BEGIN ("begin", Tokens.kBEGIN, Tokens.kBEGIN, EXPR_BEG),
         __LINE__ ("__LINE__", Tokens.k__LINE__, Tokens.k__LINE__, EXPR_END),
         CLASS ("class", Tokens.kCLASS, Tokens.kCLASS, EXPR_CLASS),
         __FILE__("__FILE__", Tokens.k__FILE__, Tokens.k__FILE__, EXPR_END),
         LEND ("END", Tokens.klEND, Tokens.klEND, EXPR_END),
         LBEGIN ("BEGIN", Tokens.klBEGIN, Tokens.klBEGIN, EXPR_END),
         WHILE ("while", Tokens.kWHILE, Tokens.kWHILE_MOD, EXPR_BEG),
         ALIAS ("alias", Tokens.kALIAS, Tokens.kALIAS, EXPR_FNAME|EXPR_FITEM),
         __ENCODING__("__ENCODING__", Tokens.k__ENCODING__, Tokens.k__ENCODING__, EXPR_END);
         
         public final String name;
         public final int id0;
         public final int id1;
         public final int state;
         
         Keyword(String name, int id0, int id1, int state) {
             this.name = name;
             this.id0 = id0;
             this.id1 = id1;
             this.state = state;
         }
     }
     
     public static Keyword getKeyword(String str) {
         return (Keyword) map.get(str);
     }
     
     // Used for tiny smidgen of grammar in lexer (see setParserSupport())
     private ParserSupport parserSupport = null;
 
     // What handles warnings
     private IRubyWarnings warnings;
 
     public int tokenize_ident(int result) {
         // FIXME: Get token from newtok index to lex_p?
         String value = createTokenString();
 
         if (isLexState(last_state, EXPR_DOT|EXPR_FNAME) && parserSupport.getCurrentScope().isDefined(value) >= 0) {
             setState(EXPR_END);
         }
 
         yaccValue = value.intern();
         return result;
     }
 
     private StrTerm lex_strterm;
 
     public RubyLexer(ParserSupport support, LexerSource source, IRubyWarnings warnings) {
         super(source);
         this.parserSupport = support;
         this.warnings = warnings;
         reset();
     }
 
     @Deprecated
     public RubyLexer(ParserSupport support, LexerSource source) {
         super(source);
         this.parserSupport = support;
         reset();
     }
     
     public void reset() {
         super.reset();
         lex_strterm = null;
         // FIXME: ripper offsets correctly but we need to subtract one?
         ruby_sourceline = src.getLineOffset() - 1;
 
         parser_prepare();
     }
 
     public int nextc() {
         if (lex_p == lex_pend) {
             line_offset += lex_pend;
 
             ByteList v = lex_nextline;
             lex_nextline = null;
 
             if (v == null) {
                 if (eofp) return EOF;
 
                 if (src == null || (v = src.gets()) == null) {
                     eofp = true;
                     lex_goto_eol();
                     return EOF;
                 }
             }
 
             if (heredoc_end > 0) {
                 ruby_sourceline = heredoc_end;
                 heredoc_end = 0;
             }
             ruby_sourceline++;
             line_count++;
             lex_pbeg = lex_p = 0;
             lex_pend = lex_p + v.length();
             lexb = v;
             flush();
             lex_lastline = v;
         }
 
         int c = p(lex_p);
         lex_p++;
         if (c == '\r') {
             if (peek('\n')) {
                 lex_p++;
                 c = '\n';
             } else if (ruby_sourceline > last_cr_line) {
                 last_cr_line = ruby_sourceline;
                 warnings.warn(ID.VOID_VALUE_EXPRESSION, getFile(), ruby_sourceline, "encountered \\r in middle of line, treated as a mere space");
                 c = ' ';
             }
         }
 
         return c;
     }
 
     public void heredoc_dedent(Node root) {
         int indent = heredoc_indent;
 
         if (indent <= 0 || root == null) return;
 
         if (root instanceof StrNode) {
             StrNode str = (StrNode) root;
             dedent_string(str.getValue(), indent);
         } else if (root instanceof ListNode) {
             ListNode list = (ListNode) root;
             int length = list.size();
             int currentLine = -1;
             for (int i = 0; i < length; i++) {
                 Node child = list.get(i);
                 if (currentLine == child.getLine()) continue;  // Only process first element on a line?
 
                 currentLine = child.getLine();                 // New line
 
                 if (child instanceof StrNode) {
                     dedent_string(((StrNode) child).getValue(), indent);
                 }
             }
         }
     }
 
     public void compile_error(String message) {
         throw new SyntaxException(PID.BAD_HEX_NUMBER, getFile(), ruby_sourceline, lexb.toString(), message);
     }
 
     // FIXME: How does lexb.toString() vs getCurrentLine() differ.
     public void compile_error(PID pid, String message) {
         String src = createAsEncodedString(lex_lastline.unsafeBytes(), lex_lastline.begin(), lex_lastline.length(), getEncoding());
         throw new SyntaxException(pid, getFile(), ruby_sourceline, src, message);
     }
 
     public void heredoc_restore(HeredocTerm here) {
         ByteList line = here.lastLine;
         lex_lastline = line;
         lex_pbeg = 0;
         lex_pend = lex_pbeg + line.length();
         lex_p = lex_pbeg + here.nth;
         lexb = line;
         heredoc_end = ruby_sourceline;
         ruby_sourceline = here.line;
         flush();
     }
 
     public int nextToken() throws IOException {
         token = yylex();
         return token == EOF ? 0 : token;
     }
 
     public ISourcePosition getPosition(ISourcePosition startPosition) {
         if (startPosition != null) return startPosition;
 
         if (tokline != null && ruby_sourceline == tokline.getLine()) return tokline;
 
         return new SimpleSourcePosition(getFile(), ruby_sourceline);
     }
 
     /**
      * Parse must pass its support object for some check at bottom of
      * yylex().  Ruby does it this way as well (i.e. a little parsing
      * logic in the lexer).
      * 
      * @param parserSupport
      */
     public void setParserSupport(ParserSupport parserSupport) {
         this.parserSupport = parserSupport;
     }
 
     @Override
     protected void setCompileOptionFlag(String name, ByteList value) {
         if (tokenSeen) {
             warnings.warn(ID.ACCESSOR_MODULE_FUNCTION, "`" + name + "' is ignored after any tokens");
             return;
         }
 
         int b = asTruth(name, value);
         if (b < 0) return;
 
         // Enebo: This is a hash in MRI for multiple potential compile options but we currently only support one.
         // I am just going to set it and when a second is done we will reevaluate how they are populated.
         parserSupport.getConfiguration().setFrozenStringLiteral(b == 1);
     }
 
     private final ByteList TRUE = new ByteList(new byte[] {'t', 'r', 'u', 'e'});
     private final ByteList FALSE = new ByteList(new byte[] {'f', 'a', 'l', 's', 'e'});
     protected int asTruth(String name, ByteList value) {
         int result = value.caseInsensitiveCmp(TRUE);
         if (result == 0) return 1;
 
         result = value.caseInsensitiveCmp(FALSE);
         if (result == 0) return 0;
 
         warnings.warn(ID.ACCESSOR_MODULE_FUNCTION, "invalid value for " + name + ": " + value);
         return -1;
     }
 
     @Override
     protected void setTokenInfo(String name, ByteList value) {
 
     }
 
     protected void setEncoding(ByteList name) {
         Ruby runtime = parserSupport.getConfiguration().getRuntime();
         Encoding newEncoding = runtime.getEncodingService().loadEncoding(name);
 
         if (newEncoding == null) throw runtime.newArgumentError("unknown encoding name: " + name.toString());
         if (!newEncoding.isAsciiCompatible()) throw runtime.newArgumentError(name.toString() + " is not ASCII compatible");
 
         setEncoding(newEncoding);
     }
 
     public StrTerm getStrTerm() {
         return lex_strterm;
     }
     
     public void setStrTerm(StrTerm strterm) {
         this.lex_strterm = strterm;
     }
 
     public void setWarnings(IRubyWarnings warnings) {
         this.warnings = warnings;
     }
 
     private int considerComplex(int token, int suffix) {
         if ((suffix & SUFFIX_I) == 0) {
             return token;
         } else {
             yaccValue = newComplexNode((NumericNode) yaccValue);
             return RubyParser.tIMAGINARY;
         }
     }
 
     private int getFloatToken(String number, int suffix) {
         if ((suffix & SUFFIX_R) != 0) {
             BigDecimal bd = new BigDecimal(number);
             BigDecimal denominator = BigDecimal.ONE.scaleByPowerOfTen(bd.scale());
             BigDecimal numerator = bd.multiply(denominator);
 
             try {
-                yaccValue = new RationalNode(getPosition(), numerator.longValueExact(), denominator.longValueExact());
+                yaccValue = new RationalNode(getPosition(), new FixnumNode(getPosition(), numerator.longValueExact()),
+                        new FixnumNode(getPosition(), denominator.longValueExact()));
             } catch (ArithmeticException ae) {
                 // FIXME: Rational supports Bignum numerator and denominator
                 compile_error(PID.RATIONAL_OUT_OF_RANGE, "Rational (" + numerator + "/" + denominator + ") out of range.");
             }
             return considerComplex(Tokens.tRATIONAL, suffix);
         }
 
         double d;
         try {
             d = SafeDoubleParser.parseDouble(number);
         } catch (NumberFormatException e) {
             warnings.warn(ID.FLOAT_OUT_OF_RANGE, getPosition(), "Float " + number + " out of range.");
 
             d = number.startsWith("-") ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
         }
         yaccValue = new FloatNode(getPosition(), d);
         return considerComplex(Tokens.tFLOAT, suffix);
     }
 
     private int getIntegerToken(String value, int radix, int suffix) {
         Node literalValue;
 
         if ((suffix & SUFFIX_R) != 0) {
             literalValue = newRationalNode(value, radix);
         } else {
             try {
                 literalValue = newFixnumNode(value, radix);
             } catch (NumberFormatException e) {
                 literalValue = newBignumNode(value, radix);
             }
         }
 
         yaccValue = literalValue;
         return considerComplex(Tokens.tINTEGER, suffix);
     }
 
 
     // STR_NEW3/parser_str_new
     public StrNode createStr(ByteList buffer, int flags) {
         Encoding bufferEncoding = buffer.getEncoding();
         int codeRange = StringSupport.codeRangeScan(bufferEncoding, buffer);
 
         if ((flags & STR_FUNC_REGEXP) == 0 && bufferEncoding.isAsciiCompatible()) {
             // If we have characters outside 7-bit range and we are still ascii then change to ascii-8bit
             if (codeRange == StringSupport.CR_7BIT) {
                 // Do nothing like MRI
             } else if (getEncoding() == USASCII_ENCODING &&
                     bufferEncoding != UTF8_ENCODING) {
                 codeRange = ParserSupport.associateEncoding(buffer, ASCII8BIT_ENCODING, codeRange);
             }
         }
 
         StrNode newStr = new StrNode(getPosition(), buffer, codeRange);
 
         if (parserSupport.getConfiguration().isFrozenStringLiteral()) newStr.setFrozen(true);
 
         return newStr;
     }
     
     /**
      * What type/kind of quote are we dealing with?
      * 
      * @param c first character the the quote construct
      * @return a token that specifies the quote type
      */
     private int parseQuote(int c) throws IOException {
         int begin, end;
         boolean shortHand;
         
         // Short-hand (e.g. %{,%.,%!,... versus %Q{).
         if (!Character.isLetterOrDigit(c)) {
             begin = c;
             c = 'Q';
             shortHand = true;
         // Long-hand (e.g. %Q{}).
         } else {
             shortHand = false;
             begin = nextc();
             if (Character.isLetterOrDigit(begin) /* no mb || ismbchar(term)*/) compile_error(PID.STRING_UNKNOWN_TYPE, "unknown type of %string");
         }
         if (c == EOF || begin == EOF) compile_error(PID.STRING_HITS_EOF, "unterminated quoted string meets end of file");
 
         // Figure end-char.  '\0' is special to indicate begin=end and that no nesting?
         switch(begin) {
         case '(': end = ')'; break;
         case '[': end = ']'; break;
         case '{': end = '}'; break;
         case '<': end = '>'; break;
         default: 
             end = begin; 
             begin = '\0';
         }
 
         switch (c) {
         case 'Q':
             lex_strterm = new StringTerm(str_dquote, begin ,end);
             yaccValue = "%"+ (shortHand ? (""+end) : ("" + c + begin));
             return Tokens.tSTRING_BEG;
 
         case 'q':
             lex_strterm = new StringTerm(str_squote, begin, end);
             yaccValue = "%"+c+begin;
             return Tokens.tSTRING_BEG;
 
         case 'W':
             lex_strterm = new StringTerm(str_dquote | STR_FUNC_QWORDS, begin, end);
             do {c = nextc();} while (Character.isWhitespace(c));
             pushback(c);
             yaccValue = "%"+c+begin;
             return Tokens.tWORDS_BEG;
 
         case 'w':
             lex_strterm = new StringTerm(/* str_squote | */ STR_FUNC_QWORDS, begin, end);
             do {c = nextc();} while (Character.isWhitespace(c));
             pushback(c);
             yaccValue = "%"+c+begin;
             return Tokens.tQWORDS_BEG;
 
         case 'x':
             lex_strterm = new StringTerm(str_xquote, begin, end);
             yaccValue = "%"+c+begin;
             return Tokens.tXSTRING_BEG;
 
         case 'r':
             lex_strterm = new StringTerm(str_regexp, begin, end);
             yaccValue = "%"+c+begin;
             return Tokens.tREGEXP_BEG;
 
         case 's':
             lex_strterm = new StringTerm(str_ssym, begin, end);
             setState(EXPR_FNAME|EXPR_FITEM);
             yaccValue = "%"+c+begin;
             return Tokens.tSYMBEG;
         
         case 'I':
             lex_strterm = new StringTerm(str_dquote | STR_FUNC_QWORDS, begin, end);
             do {c = nextc();} while (Character.isWhitespace(c));
             pushback(c);
             yaccValue = "%" + c + begin;
             return Tokens.tSYMBOLS_BEG;
         case 'i':
             lex_strterm = new StringTerm(/* str_squote | */STR_FUNC_QWORDS, begin, end);
             do {c = nextc();} while (Character.isWhitespace(c));
             pushback(c);
             yaccValue = "%" + c + begin;
             return Tokens.tQSYMBOLS_BEG;
         default:
             compile_error(PID.STRING_UNKNOWN_TYPE, "unknown type of %string");
         }
         return -1; // not-reached
     }
     
     private int hereDocumentIdentifier() throws IOException {
         int c = nextc(); 
         int term;
 
         int func = 0;
         if (c == '-') {
             c = nextc();
             func = STR_FUNC_INDENT;
         } else if (c == '~') {
             c = nextc();
             func = STR_FUNC_INDENT;
             heredoc_indent = Integer.MAX_VALUE;
             heredoc_line_indent = 0;
         }
         
         ByteList markerValue;
         if (c == '\'' || c == '"' || c == '`') {
             if (c == '\'') {
                 func |= str_squote;
             } else if (c == '"') {
                 func |= str_dquote;
             } else {
                 func |= str_xquote; 
             }
 
             newtok(false); // skip past quote type
 
             term = c;
             while ((c = nextc()) != EOF && c != term) {
                 if (!tokadd_mbchar(c)) return EOF;
             }
 
             if (c == EOF) compile_error("unterminated here document identifier");
 
             // c == term.  This differs from MRI in that we unwind term symbol so we can make
             // our marker with just tokp and lex_p info (e.g. we don't make second numberBuffer).
             pushback(term);
             markerValue = createTokenByteList();
             nextc();
         } else {
             if (!isIdentifierChar(c)) {
                 pushback(c);
                 if ((func & STR_FUNC_INDENT) != 0) {
                     pushback(heredoc_indent > 0 ? '~' : '-');
                 }
                 return 0;
             }
             newtok(true);
             term = '"';
             func |= str_dquote;
             do {
                 if (!tokadd_mbchar(c)) return EOF;
             } while ((c = nextc()) != EOF && isIdentifierChar(c));
             pushback(c);
             markerValue = createTokenByteList();
         }
 
         int len = lex_p - lex_pbeg;
         lex_goto_eol();
         lex_strterm = new HeredocTerm(markerValue, func, len, ruby_sourceline, lex_lastline);
 
         if (term == '`') {
             yaccValue = "`";
             flush();
             return Tokens.tXSTRING_BEG;
         }
         
         yaccValue = "\"";
         flush();
         return Tokens.tSTRING_BEG;
     }
     
     private boolean arg_ambiguous() {
         if (warnings.isVerbose() && Options.PARSER_WARN_AMBIGUOUS_ARGUMENTS.load()) {
             warnings.warning(ID.AMBIGUOUS_ARGUMENT, getPosition(), "Ambiguous first argument; make sure.");
         }
         return true;
     }
 
     /*
      * Not normally used, but is left in here since it can be useful in debugging
      * grammar and lexing problems.
      *
      */
     private void printToken(int token) {
         //System.out.print("LOC: " + support.getPosition() + " ~ ");
         
         switch (token) {
             case Tokens.yyErrorCode: System.err.print("yyErrorCode,"); break;
             case Tokens.kCLASS: System.err.print("kClass,"); break;
             case Tokens.kMODULE: System.err.print("kModule,"); break;
             case Tokens.kDEF: System.err.print("kDEF,"); break;
             case Tokens.kUNDEF: System.err.print("kUNDEF,"); break;
             case Tokens.kBEGIN: System.err.print("kBEGIN,"); break;
             case Tokens.kRESCUE: System.err.print("kRESCUE,"); break;
             case Tokens.kENSURE: System.err.print("kENSURE,"); break;
             case Tokens.kEND: System.err.print("kEND,"); break;
             case Tokens.kIF: System.err.print("kIF,"); break;
             case Tokens.kUNLESS: System.err.print("kUNLESS,"); break;
             case Tokens.kTHEN: System.err.print("kTHEN,"); break;
             case Tokens.kELSIF: System.err.print("kELSIF,"); break;
             case Tokens.kELSE: System.err.print("kELSE,"); break;
             case Tokens.kCASE: System.err.print("kCASE,"); break;
             case Tokens.kWHEN: System.err.print("kWHEN,"); break;
             case Tokens.kWHILE: System.err.print("kWHILE,"); break;
             case Tokens.kUNTIL: System.err.print("kUNTIL,"); break;
             case Tokens.kFOR: System.err.print("kFOR,"); break;
             case Tokens.kBREAK: System.err.print("kBREAK,"); break;
             case Tokens.kNEXT: System.err.print("kNEXT,"); break;
             case Tokens.kREDO: System.err.print("kREDO,"); break;
             case Tokens.kRETRY: System.err.print("kRETRY,"); break;
             case Tokens.kIN: System.err.print("kIN,"); break;
             case Tokens.kDO: System.err.print("kDO,"); break;
             case Tokens.kDO_COND: System.err.print("kDO_COND,"); break;
             case Tokens.kDO_BLOCK: System.err.print("kDO_BLOCK,"); break;
             case Tokens.kRETURN: System.err.print("kRETURN,"); break;
             case Tokens.kYIELD: System.err.print("kYIELD,"); break;
             case Tokens.kSUPER: System.err.print("kSUPER,"); break;
             case Tokens.kSELF: System.err.print("kSELF,"); break;
             case Tokens.kNIL: System.err.print("kNIL,"); break;
             case Tokens.kTRUE: System.err.print("kTRUE,"); break;
             case Tokens.kFALSE: System.err.print("kFALSE,"); break;
             case Tokens.kAND: System.err.print("kAND,"); break;
             case Tokens.kOR: System.err.print("kOR,"); break;
             case Tokens.kNOT: System.err.print("kNOT,"); break;
             case Tokens.kIF_MOD: System.err.print("kIF_MOD,"); break;
             case Tokens.kUNLESS_MOD: System.err.print("kUNLESS_MOD,"); break;
             case Tokens.kWHILE_MOD: System.err.print("kWHILE_MOD,"); break;
             case Tokens.kUNTIL_MOD: System.err.print("kUNTIL_MOD,"); break;
             case Tokens.kRESCUE_MOD: System.err.print("kRESCUE_MOD,"); break;
             case Tokens.kALIAS: System.err.print("kALIAS,"); break;
             case Tokens.kDEFINED: System.err.print("kDEFINED,"); break;
             case Tokens.klBEGIN: System.err.print("klBEGIN,"); break;
             case Tokens.klEND: System.err.print("klEND,"); break;
             case Tokens.k__LINE__: System.err.print("k__LINE__,"); break;
             case Tokens.k__FILE__: System.err.print("k__FILE__,"); break;
             case Tokens.k__ENCODING__: System.err.print("k__ENCODING__,"); break;
             case Tokens.kDO_LAMBDA: System.err.print("kDO_LAMBDA,"); break;
             case Tokens.tIDENTIFIER: System.err.print("tIDENTIFIER["+ value() + "],"); break;
             case Tokens.tFID: System.err.print("tFID[" + value() + "],"); break;
             case Tokens.tGVAR: System.err.print("tGVAR[" + value() + "],"); break;
             case Tokens.tIVAR: System.err.print("tIVAR[" + value() +"],"); break;
             case Tokens.tCONSTANT: System.err.print("tCONSTANT["+ value() +"],"); break;
             case Tokens.tCVAR: System.err.print("tCVAR,"); break;
             case Tokens.tINTEGER: System.err.print("tINTEGER,"); break;
             case Tokens.tFLOAT: System.err.print("tFLOAT,"); break;
             case Tokens.tSTRING_CONTENT: System.err.print("tSTRING_CONTENT[" + ((StrNode) value()).getValue() + "],"); break;
             case Tokens.tSTRING_BEG: System.err.print("tSTRING_BEG,"); break;
             case Tokens.tSTRING_END: System.err.print("tSTRING_END,"); break;
             case Tokens.tSTRING_DBEG: System.err.print("tSTRING_DBEG,"); break;
             case Tokens.tSTRING_DVAR: System.err.print("tSTRING_DVAR,"); break;
             case Tokens.tXSTRING_BEG: System.err.print("tXSTRING_BEG,"); break;
             case Tokens.tREGEXP_BEG: System.err.print("tREGEXP_BEG,"); break;
             case Tokens.tREGEXP_END: System.err.print("tREGEXP_END,"); break;
             case Tokens.tWORDS_BEG: System.err.print("tWORDS_BEG,"); break;
             case Tokens.tQWORDS_BEG: System.err.print("tQWORDS_BEG,"); break;
             case Tokens.tBACK_REF: System.err.print("tBACK_REF,"); break;
             case Tokens.tBACK_REF2: System.err.print("tBACK_REF2,"); break;
             case Tokens.tNTH_REF: System.err.print("tNTH_REF,"); break;
             case Tokens.tUPLUS: System.err.print("tUPLUS"); break;
             case Tokens.tUMINUS: System.err.print("tUMINUS,"); break;
             case Tokens.tPOW: System.err.print("tPOW,"); break;
             case Tokens.tCMP: System.err.print("tCMP,"); break;
             case Tokens.tEQ: System.err.print("tEQ,"); break;
             case Tokens.tEQQ: System.err.print("tEQQ,"); break;
             case Tokens.tNEQ: System.err.print("tNEQ,"); break;
             case Tokens.tGEQ: System.err.print("tGEQ,"); break;
             case Tokens.tLEQ: System.err.print("tLEQ,"); break;
             case Tokens.tANDOP: System.err.print("tANDOP,"); break;
             case Tokens.tOROP: System.err.print("tOROP,"); break;
             case Tokens.tMATCH: System.err.print("tMATCH,"); break;
             case Tokens.tNMATCH: System.err.print("tNMATCH,"); break;
             case Tokens.tDOT: System.err.print("tDOT,"); break;
             case Tokens.tDOT2: System.err.print("tDOT2,"); break;
             case Tokens.tDOT3: System.err.print("tDOT3,"); break;
             case Tokens.tAREF: System.err.print("tAREF,"); break;
             case Tokens.tASET: System.err.print("tASET,"); break;
             case Tokens.tLSHFT: System.err.print("tLSHFT,"); break;
             case Tokens.tRSHFT: System.err.print("tRSHFT,"); break;
             case Tokens.tCOLON2: System.err.print("tCOLON2,"); break;
             case Tokens.tCOLON3: System.err.print("tCOLON3,"); break;
             case Tokens.tOP_ASGN: System.err.print("tOP_ASGN,"); break;
             case Tokens.tASSOC: System.err.print("tASSOC,"); break;
             case Tokens.tLPAREN: System.err.print("tLPAREN,"); break;
             case Tokens.tLPAREN2: System.err.print("tLPAREN2,"); break;
             case Tokens.tLPAREN_ARG: System.err.print("tLPAREN_ARG,"); break;
             case Tokens.tLBRACK: System.err.print("tLBRACK,"); break;
             case Tokens.tRBRACK: System.err.print("tRBRACK,"); break;
             case Tokens.tLBRACE: System.err.print("tLBRACE,"); break;
             case Tokens.tLBRACE_ARG: System.err.print("tLBRACE_ARG,"); break;
             case Tokens.tSTAR: System.err.print("tSTAR,"); break;
             case Tokens.tSTAR2: System.err.print("tSTAR2,"); break;
             case Tokens.tAMPER: System.err.print("tAMPER,"); break;
             case Tokens.tAMPER2: System.err.print("tAMPER2,"); break;
             case Tokens.tSYMBEG: System.err.print("tSYMBEG,"); break;
             case Tokens.tTILDE: System.err.print("tTILDE,"); break;
             case Tokens.tPERCENT: System.err.print("tPERCENT,"); break;
             case Tokens.tDIVIDE: System.err.print("tDIVIDE,"); break;
             case Tokens.tPLUS: System.err.print("tPLUS,"); break;
             case Tokens.tMINUS: System.err.print("tMINUS,"); break;
             case Tokens.tLT: System.err.print("tLT,"); break;
             case Tokens.tGT: System.err.print("tGT,"); break;
             case Tokens.tCARET: System.err.print("tCARET,"); break;
             case Tokens.tBANG: System.err.print("tBANG,"); break;
             case Tokens.tLCURLY: System.err.print("tTLCURLY,"); break;
             case Tokens.tRCURLY: System.err.print("tRCURLY,"); break;
             case Tokens.tPIPE: System.err.print("tTPIPE,"); break;
             case Tokens.tLAMBDA: System.err.print("tLAMBDA,"); break;
             case Tokens.tLAMBEG: System.err.print("tLAMBEG,"); break;
             case Tokens.tRPAREN: System.err.print("tRPAREN,"); break;
             case Tokens.tLABEL: System.err.print("tLABEL("+ value() +":),"); break;
             case Tokens.tLABEL_END: System.err.print("tLABEL_END"); break;
             case '\n': System.err.println("NL"); break;
             case EOF: System.out.println("EOF"); break;
             case Tokens.tDSTAR: System.err.print("tDSTAR"); break;
             default: System.err.print("'" + (char)token + "',"); break;
         }
     }
 
     // DEBUGGING HELP 
     private int yylex2() throws IOException {
         int currentToken = yylex2();
         
         printToken(currentToken);
         
         return currentToken;
     }
     
     /**
      *  Returns the next token. Also sets yyVal is needed.
      *
      *@return    Description of the Returned Value
      */
     private int yylex() throws IOException {
         int c;
         boolean spaceSeen = false;
         boolean commandState;
         boolean tokenSeen = this.tokenSeen;
         
         if (lex_strterm != null) {
             int tok = lex_strterm.parseString(this);
 
             if (tok == Tokens.tSTRING_END && (lex_strterm.getFlags() & STR_FUNC_LABEL) != 0) {
                 if ((isLexState(lex_state, EXPR_BEG|EXPR_ENDFN) && !conditionState.isInState() ||
                         isARG()) && isLabelSuffix()) {
                     nextc();
                     tok = Tokens.tLABEL_END;
                     setState(EXPR_BEG|EXPR_LABEL);
                     lex_strterm = null;
                 }
             }
 
             if (tok == Tokens.tSTRING_END || tok == Tokens.tREGEXP_END) {
                 lex_strterm = null;
                 setState(EXPR_END);
             }
 
             return tok;
         }
 
         commandState = commandStart;
         commandStart = false;
         this.tokenSeen = true;
 
         loop: for(;;) {
             last_state = lex_state;
             c = nextc();
             switch(c) {
             case '\000': /* NUL */
             case '\004': /* ^D */
             case '\032': /* ^Z */
             case EOF:	 /* end of script. */
                 return EOF;
            
                 /* white spaces */
             case ' ': case '\t': case '\f': case '\r':
             case '\13': /* '\v' */
                 getPosition();
                 spaceSeen = true;
                 continue;
             case '#': {	/* it's a comment */
                 this.tokenSeen = tokenSeen;
                 if (!parseMagicComment(parserSupport.getConfiguration().getRuntime(), lexb.makeShared(lex_p, lex_pend - lex_p))) {
                     if (comment_at_top()) set_file_encoding(lex_p, lex_pend);
                 }
                 lex_p = lex_pend;
             }
             /* fall through */
             case '\n': {
                 this.tokenSeen = tokenSeen;
                 boolean normalArg = isLexState(lex_state, EXPR_BEG | EXPR_CLASS | EXPR_FNAME | EXPR_DOT) &&
                         !isLexState(lex_state, EXPR_LABELED);
                 if (normalArg || isLexStateAll(lex_state, EXPR_ARG | EXPR_LABELED)) {
                     if (!normalArg && inKwarg) {
                         commandStart = true;
                         setState(EXPR_BEG);
                         return '\n';
                     }
                     continue loop;
                 }
 
                 boolean done = false;
                 while (!done) {
                     c = nextc();
 
                     switch (c) {
                     case ' ': case '\t': case '\f': case '\r': case '\13': /* '\v' */
                         spaceSeen = true;
                         continue;
                     case '&':
                     case '.': {
                         if (peek('.') == (c == '&')) {
                             pushback(c);
 
                             continue loop;
                         }
                     }
                     default:
                     case -1:		// EOF (ENEBO: After default?
                         done = true;
                     }
                 }
 
                 if (c == -1) return EOF;
 
                 pushback(c);
                 getPosition();
 
                 commandStart = true;
                 setState(EXPR_BEG);
                 return '\n';
             }
             case '*':
                 return star(spaceSeen);
             case '!':
                 return bang();
             case '=':
                 // documentation nodes
                 if (was_bol()) {
                     if (strncmp(lexb.makeShared(lex_p, lex_pend - lex_p), BEGIN_DOC_MARKER, BEGIN_DOC_MARKER.length()) &&
                             Character.isWhitespace(p(lex_p + 5))) {
                         for (;;) {
                             lex_goto_eol();
 
                             c = nextc();
 
                             if (c == EOF) {
                                 compile_error("embedded document meets end of file");
                                 return EOF;
                             }
 
                             if (c != '=') continue;
 
                             if (strncmp(lexb.makeShared(lex_p, lex_pend - lex_p), END_DOC_MARKER, END_DOC_MARKER.length()) &&
                                     (lex_p + 3 == lex_pend || Character.isWhitespace(p(lex_p + 3)))) {
                                 break;
                             }
                         }
                         lex_goto_eol();
 
                         continue loop;
                     }
                 }
 
                 setState(isAfterOperator() ? EXPR_ARG : EXPR_BEG);
 
                 c = nextc();
                 if (c == '=') {
                     c = nextc();
                     if (c == '=') {
                         yaccValue = "===";
                         return Tokens.tEQQ;
                     }
                     pushback(c);
                     yaccValue = "==";
                     return Tokens.tEQ;
                 }
                 if (c == '~') {
                     yaccValue = "=~";
                     return Tokens.tMATCH;
                 } else if (c == '>') {
                     yaccValue = "=>";
                     return Tokens.tASSOC;
                 }
                 pushback(c);
                 yaccValue = "=";
                 return '=';
                 
             case '<':
                 return lessThan(spaceSeen);
             case '>':
                 return greaterThan();
             case '"':
                 return doubleQuote(commandState);
             case '`':
                 return backtick(commandState);
             case '\'':
                 return singleQuote(commandState);
             case '?':
                 return questionMark();
             case '&':
                 return ampersand(spaceSeen);
             case '|':
                 return pipe();
             case '+':
                 return plus(spaceSeen);
             case '-':
                 return minus(spaceSeen);
             case '.':
                 return dot();
             case '0' : case '1' : case '2' : case '3' : case '4' :
             case '5' : case '6' : case '7' : case '8' : case '9' :
                 return parseNumber(c);
             case ')':
                 return rightParen();
             case ']':
                 return rightBracket();
             case '}':
                 return rightCurly();
             case ':':
                 return colon(spaceSeen);
             case '/':
                 return slash(spaceSeen);
             case '^':
                 return caret();
             case ';':
                 commandStart = true;
                 setState(EXPR_BEG);
                 yaccValue = ";";
                 return ';';
             case ',':
                 return comma(c);
             case '~':
                 return tilde();
             case '(':
                 return leftParen(spaceSeen);
             case '[':
                 return leftBracket(spaceSeen);
             case '{':
             	return leftCurly();
             case '\\':
                 c = nextc();
                 if (c == '\n') {
                     spaceSeen = true;
                     continue;
                 }
                 pushback(c);
                 yaccValue = "\\";
                 return '\\';
             case '%':
                 return percent(spaceSeen);
             case '$':
                 return dollar();
             case '@':
                 return at();
             case '_':
                 if (was_bol() && whole_match_p(END_MARKER, false)) {
                     line_offset += lex_pend;
                     __end__seen = true;
                     eofp = true;
 
                     lex_goto_eol();
                     return EOF;
                 }
                 return identifier(c, commandState);
             default:
                 return identifier(c, commandState);
             }
         }
     }
 
     private int identifierToken(int result, String value) {
         if (result == Tokens.tIDENTIFIER && !isLexState(last_state, EXPR_DOT|EXPR_FNAME) &&
                 parserSupport.getCurrentScope().isDefined(value) >= 0) {
             setState(EXPR_END|EXPR_LABEL);
         }
 
         yaccValue = value;
         return result;
     }
     
     private int ampersand(boolean spaceSeen) throws IOException {
         int c = nextc();
         
         switch (c) {
         case '&':
             setState(EXPR_BEG);
             if ((c = nextc()) == '=') {
                 yaccValue = "&&";
                 setState(EXPR_BEG);
                 return Tokens.tOP_ASGN;
             }
             pushback(c);
             yaccValue = "&&";
             return Tokens.tANDOP;
         case '=':
             yaccValue = "&";
             setState(EXPR_BEG);
             return Tokens.tOP_ASGN;
         case '.':
             setState(EXPR_DOT);
             yaccValue = "&.";
             return Tokens.tANDDOT;
         }
         pushback(c);
         
         //tmpPosition is required because getPosition()'s side effects.
         //if the warning is generated, the getPosition() on line 954 (this line + 18) will create
         //a wrong position if the "inclusive" flag is not set.
         ISourcePosition tmpPosition = getPosition();
         if (isSpaceArg(c, spaceSeen)) {
             if (warnings.isVerbose() && Options.PARSER_WARN_ARGUMENT_PREFIX.load())
                 warnings.warning(ID.ARGUMENT_AS_PREFIX, tmpPosition, "`&' interpreted as argument prefix");
             c = Tokens.tAMPER;
         } else if (isBEG()) {
             c = Tokens.tAMPER;
         } else {
             warn_balanced(c, spaceSeen, "&", "argument prefix");
             c = Tokens.tAMPER2;
         }
 
         setState(isAfterOperator() ? EXPR_ARG : EXPR_BEG);
         
         yaccValue = "&";
         return c;
     }
 
     private int at() throws IOException {
         newtok(true);
         int c = nextc();
         int result;
         if (c == '@') {
             c = nextc();
             result = Tokens.tCVAR;
         } else {
             result = Tokens.tIVAR;
         }
 
         if (c == EOF || Character.isSpaceChar(c)) {
             if (result == Tokens.tIVAR) {
                 compile_error("`@' without identifiers is not allowed as an instance variable name");
             }
 
             compile_error("`@@' without identifiers is not allowed as a class variable name");
         } else if (Character.isDigit(c) || !isIdentifierChar(c)) {
             pushback(c);
             if (result == Tokens.tIVAR) {
                 compile_error(PID.IVAR_BAD_NAME, "`@" + ((char) c) + "' is not allowed as an instance variable name");
             }
             compile_error(PID.CVAR_BAD_NAME, "`@@" + ((char) c) + "' is not allowed as a class variable name");
         }
 
         if (!tokadd_ident(c)) return EOF;
 
         last_state = lex_state;
         setState(EXPR_END);
 
         return tokenize_ident(result);
     }
 
     private int backtick(boolean commandState) throws IOException {
         yaccValue = "`";
 
         if (isLexState(lex_state, EXPR_FNAME)) {
             setState(EXPR_ENDFN);
             return Tokens.tBACK_REF2;
         }
         if (isLexState(lex_state, EXPR_DOT)) {
             setState(commandState ? EXPR_CMDARG : EXPR_ARG);
 
             return Tokens.tBACK_REF2;
         }
 
         lex_strterm = new StringTerm(str_xquote, '\0', '`');
         return Tokens.tXSTRING_BEG;
     }
     
     private int bang() throws IOException {
         int c = nextc();
 
         if (isAfterOperator()) {
             setState(EXPR_ARG);
             if (c == '@') {
                 yaccValue = "!";
                 return Tokens.tBANG;
             }
         } else {
             setState(EXPR_BEG);
         }
         
         switch (c) {
         case '=':
             yaccValue = "!=";
             
             return Tokens.tNEQ;
         case '~':
             yaccValue = "!~";
             
             return Tokens.tNMATCH;
         default: // Just a plain bang
             pushback(c);
             yaccValue = "!";
             
             return Tokens.tBANG;
         }
     }
     
     private int caret() throws IOException {
         int c = nextc();
         if (c == '=') {
             setState(EXPR_BEG);
             yaccValue = "^";
             return Tokens.tOP_ASGN;
         }
 
         setState(isAfterOperator() ? EXPR_ARG : EXPR_BEG);
         
         pushback(c);
         yaccValue = "^";
         return Tokens.tCARET;
     }
 
     private int colon(boolean spaceSeen) throws IOException {
         int c = nextc();
         
         if (c == ':') {
             if (isBEG() || isLexState(lex_state, EXPR_CLASS) || (isARG() && spaceSeen)) {
                 setState(EXPR_BEG);
                 yaccValue = "::";
                 return Tokens.tCOLON3;
             }
             setState(EXPR_DOT);
             yaccValue = ":";
             return Tokens.tCOLON2;
         }
 
         if (isEND() || Character.isWhitespace(c) || c == '#') {
             pushback(c);
             setState(EXPR_BEG);
             yaccValue = ":";
             warn_balanced(c, spaceSeen, ":", "symbol literal");
             return ':';
         }
         
         switch (c) {
         case '\'':
             lex_strterm = new StringTerm(str_ssym, '\0', c);
             break;
         case '"':
             lex_strterm = new StringTerm(str_dsym, '\0', c);
             break;
         default:
             pushback(c);
             break;
         }
         
         setState(EXPR_FNAME);
         yaccValue = ":";
         return Tokens.tSYMBEG;
     }
 
     private int comma(int c) throws IOException {
         setState(EXPR_BEG|EXPR_LABEL);
         yaccValue = ",";
         
         return c;
     }
 
     private int doKeyword(int state) {
         int leftParenBegin = getLeftParenBegin();
         if (leftParenBegin > 0 && leftParenBegin == parenNest) {
             setLeftParenBegin(0);
             parenNest--;
             return Tokens.kDO_LAMBDA;
         }
 
         if (conditionState.isInState()) return Tokens.kDO_COND;
 
         if (cmdArgumentState.isInState() && !isLexState(state, EXPR_CMDARG)) {
             return Tokens.kDO_BLOCK;
         }
         if (isLexState(state,  EXPR_BEG|EXPR_ENDARG)) {
             return Tokens.kDO_BLOCK;
         }
         return Tokens.kDO;
     }
     
     private int dollar() throws IOException {
         setState(EXPR_END);
         newtok(true);
         int c = nextc();
         
         switch (c) {
         case '_':       /* $_: last read line string */
             c = nextc();
             if (isIdentifierChar(c)) {
                 if (!tokadd_ident(c)) return EOF;
 
                 last_state = lex_state;
                 setState(EXPR_END);
                 yaccValue = createTokenString().intern();
                 return Tokens.tGVAR;
             }
             pushback(c);
             c = '_';
             // fall through
         case '~':       /* $~: match-data */
         case '*':       /* $*: argv */
         case '$':       /* $$: pid */
         case '?':       /* $?: last status */
         case '!':       /* $!: error string */
         case '@':       /* $@: error position */
         case '/':       /* $/: input record separator */
         case '\\':      /* $\: output record separator */
         case ';':       /* $;: field separator */
         case ',':       /* $,: output field separator */
         case '.':       /* $.: last read line number */
         case '=':       /* $=: ignorecase */
         case ':':       /* $:: load path */
         case '<':       /* $<: reading filename */
         case '>':       /* $>: default output handle */
         case '\"':      /* $": already loaded files */
             yaccValue = "$" + (char) c;
             return Tokens.tGVAR;
 
         case '-':
             c = nextc();
             if (isIdentifierChar(c)) {
                 if (!tokadd_mbchar(c)) return EOF;
             } else {
                 pushback(c);
                 pushback('-');
                 return '$';
             }
             yaccValue = createTokenString().intern();
             /* xxx shouldn't check if valid option variable */
             return Tokens.tGVAR;
 
         case '&':       /* $&: last match */
         case '`':       /* $`: string before last match */
         case '\'':      /* $': string after last match */
         case '+':       /* $+: string matches last paren. */
             // Explicit reference to these vars as symbols...
             if (isLexState(last_state, EXPR_FNAME)) {
                 yaccValue = "$" + (char) c;
                 return Tokens.tGVAR;
             }
             
             yaccValue = new BackRefNode(getPosition(), c);
             return Tokens.tBACK_REF;
 
         case '1': case '2': case '3': case '4': case '5': case '6':
         case '7': case '8': case '9':
             do {
                 c = nextc();
             } while (Character.isDigit(c));
             pushback(c);
             if (isLexState(last_state, EXPR_FNAME)) {
                 yaccValue = createTokenString().intern();
                 return Tokens.tGVAR;
             }
 
             int ref;
             String refAsString = createTokenString();
 
             try {
                 ref = Integer.parseInt(refAsString.substring(1).intern());
             } catch (NumberFormatException e) {
                 warnings.warn(ID.AMBIGUOUS_ARGUMENT, "`" + refAsString + "' is too big for a number variable, always nil");
                 ref = 0;
             }
 
             yaccValue = new NthRefNode(getPosition(), ref);
             return Tokens.tNTH_REF;
         case '0':
             setState(EXPR_END);
 
             return identifierToken(Tokens.tGVAR, ("$" + (char) c).intern());
         default:
             if (!isIdentifierChar(c)) {
                 if (c == EOF || Character.isSpaceChar(c)) {
                     compile_error(PID.CVAR_BAD_NAME, "`$' without identifiers is not allowed as a global variable name");
                 } else {
                     pushback(c);
                     compile_error(PID.CVAR_BAD_NAME, "`$" + ((char) c) + "' is not allowed as a global variable name");
                 }
             }
 
             last_state = lex_state;
             setState(EXPR_END);
 
             tokadd_ident(c);
 
             return identifierToken(Tokens.tGVAR, createTokenString().intern());  // $blah
         }
     }
 
     private int dot() throws IOException {
         int c;
         
         setState(EXPR_BEG);
         if ((c = nextc()) == '.') {
             if ((c = nextc()) == '.') {
                 yaccValue = "...";
                 return Tokens.tDOT3;
             }
             pushback(c);
             yaccValue = "..";
             return Tokens.tDOT2;
         }
         
         pushback(c);
         if (Character.isDigit(c)) compile_error(PID.FLOAT_MISSING_ZERO, "no .<digit> floating literal anymore; put 0 before dot");
 
         setState(EXPR_DOT);
         yaccValue = ".";
         return Tokens.tDOT;
     }
     
     private int doubleQuote(boolean commandState) throws IOException {
         int label = isLabelPossible(commandState) ? str_label : 0;
         lex_strterm = new StringTerm(str_dquote|label, '\0', '"');
         yaccValue = "\"";
 
         return Tokens.tSTRING_BEG;
     }
     
     private int greaterThan() throws IOException {
         setState(isAfterOperator() ? EXPR_ARG : EXPR_BEG);
 
         int c = nextc();
 
         switch (c) {
         case '=':
             yaccValue = ">=";
             
             return Tokens.tGEQ;
         case '>':
             if ((c = nextc()) == '=') {
                 setState(EXPR_BEG);
                 yaccValue = ">>";
                 return Tokens.tOP_ASGN;
             }
             pushback(c);
             
             yaccValue = ">>";
             return Tokens.tRSHFT;
         default:
diff --git a/core/src/main/java/org/jruby/parser/ParserSupport.java b/core/src/main/java/org/jruby/parser/ParserSupport.java
index 34ec7bcff4..4f0e1d6a3d 100644
--- a/core/src/main/java/org/jruby/parser/ParserSupport.java
+++ b/core/src/main/java/org/jruby/parser/ParserSupport.java
@@ -89,1393 +89,1391 @@ public class ParserSupport {
 
     public StaticScope getCurrentScope() {
         return currentScope;
     }
     
     public ParserConfiguration getConfiguration() {
         return configuration;
     }
     
     public void popCurrentScope() {
         if (!currentScope.isBlockScope()) {
             lexer.getCmdArgumentState().reset(currentScope.getCommandArgumentStack());
         }
         currentScope = currentScope.getEnclosingScope();
 
     }
     
     public void pushBlockScope() {
         currentScope = configuration.getRuntime().getStaticScopeFactory().newBlockScope(currentScope, lexer.getFile());
     }
     
     public void pushLocalScope() {
         currentScope = configuration.getRuntime().getStaticScopeFactory().newLocalScope(currentScope, lexer.getFile());
         currentScope.setCommandArgumentStack(lexer.getCmdArgumentState().getStack());
         lexer.getCmdArgumentState().reset(0);
     }
     
     public Node arg_concat(ISourcePosition position, Node node1, Node node2) {
         return node2 == null ? node1 : new ArgsCatNode(position, node1, node2);
     }
 
     public Node arg_blk_pass(Node firstNode, BlockPassNode secondNode) {
         if (secondNode != null) {
             secondNode.setArgsNode(firstNode);
             return secondNode;
         }
         return firstNode;
     }
 
     /**
      * We know for callers of this that it cannot be any of the specials checked in gettable.
      * 
      * @param node to check its variable type
      * @return an AST node representing this new variable
      */
     public Node gettable2(Node node) {
         switch (node.getNodeType()) {
         case DASGNNODE: // LOCALVAR
         case LOCALASGNNODE:
             String name = ((INameNode) node).getName();
             if (name.equals(lexer.getCurrentArg())) {
                 warn(ID.AMBIGUOUS_ARGUMENT, node.getPosition(), "circular argument reference - " + name);
             }
             return currentScope.declare(node.getPosition(), name);
         case CONSTDECLNODE: // CONSTANT
             return new ConstNode(node.getPosition(), ((INameNode) node).getName());
         case INSTASGNNODE: // INSTANCE VARIABLE
             return new InstVarNode(node.getPosition(), ((INameNode) node).getName());
         case CLASSVARDECLNODE:
         case CLASSVARASGNNODE:
             return new ClassVarNode(node.getPosition(), ((INameNode) node).getName());
         case GLOBALASGNNODE:
             return new GlobalVarNode(node.getPosition(), ((INameNode) node).getName());
         }
 
         getterIdentifierError(node.getPosition(), ((INameNode) node).getName());
         return null;
     }
 
     public Node declareIdentifier(String name) {
         if (name.equals(lexer.getCurrentArg())) {
             warn(ID.AMBIGUOUS_ARGUMENT, lexer.getPosition(), "circular argument reference - " + name);
         }
         return currentScope.declare(lexer.tokline, name);
     }
 
     // We know it has to be tLABEL or tIDENTIFIER so none of the other assignable logic is needed
     public AssignableNode assignableLabelOrIdentifier(String name, Node value) {
         return currentScope.assign(lexer.getPosition(), name.intern(), makeNullNil(value));
     }
 
     // We know it has to be tLABEL or tIDENTIFIER so none of the other assignable logic is needed
     public AssignableNode assignableKeyword(String name, Node value) {
         return currentScope.assignKeyword(lexer.getPosition(), name.intern(), makeNullNil(value));
     }
 
     // Only calls via f_kw so we know it has to be tLABEL
     public AssignableNode assignableLabel(String name, Node value) {
         return currentScope.assignKeyword(lexer.getPosition(), name, makeNullNil(value));
     }
     
     protected void getterIdentifierError(ISourcePosition position, String identifier) {
         lexer.compile_error(PID.BAD_IDENTIFIER, "identifier " + identifier + " is not valid to get");
     }
 
     /**
      *  Wraps node with NEWLINE node.
      *
      *@param node
      */
     public Node newline_node(Node node, ISourcePosition position) {
         if (node == null) return null;
 
         configuration.coverLine(position.getLine());
         node.setNewline();
 
         return node;
     }
 
     // This is the last node made in the AST unintuitively so so post-processing can occur here.
     public Node addRootNode(Node topOfAST) {
         final int endPosition;
 
         if (lexer.isEndSeen()) {
             endPosition = lexer.getLineOffset();
         } else {
             endPosition = -1;
         }
 
         ISourcePosition position;
         CoverageData coverageData = configuration.finishCoverage(lexer.getFile(), lexer.lineno());
         if (result.getBeginNodes().isEmpty()) {
             if (topOfAST == null) {
                 topOfAST = NilImplicitNode.NIL;
                 position = lexer.getPosition();
             } else {
                 position = topOfAST.getPosition();
             }
         } else {
             position = topOfAST != null ? topOfAST.getPosition() : result.getBeginNodes().get(0).getPosition();
             BlockNode newTopOfAST = new BlockNode(position);
             for (Node beginNode : result.getBeginNodes()) {
                 appendToBlock(newTopOfAST, beginNode);
             }
 
             // Add real top to new top (unless this top is empty [only begin/end nodes or truly empty])
             if (topOfAST != null) newTopOfAST.add(topOfAST);
             topOfAST = newTopOfAST;
         }
         
         return new RootNode(position, result.getScope(), topOfAST, lexer.getFile(), endPosition, coverageData != null);
     }
     
     /* MRI: block_append */
     public Node appendToBlock(Node head, Node tail) {
         if (tail == null) return head;
         if (head == null) return tail;
 
         if (!(head instanceof BlockNode)) {
             head = new BlockNode(head.getPosition()).add(head);
         }
 
         if (warnings.isVerbose() && isBreakStatement(((ListNode) head).getLast()) && Options.PARSER_WARN_NOT_REACHED.load()) {
             warnings.warning(ID.STATEMENT_NOT_REACHED, tail.getPosition(), "statement not reached");
         }
 
         // Assumption: tail is never a list node
         ((ListNode) head).addAll(tail);
         return head;
     }
 
     // We know it has to be tLABEL or tIDENTIFIER so none of the other assignable logic is needed
     public AssignableNode assignableInCurr(String name, Node value) {
         currentScope.addVariableThisScope(name);
         return currentScope.assign(lexer.getPosition(), name, makeNullNil(value));
     }
 
     public Node getOperatorCallNode(Node firstNode, String operator) {
         checkExpression(firstNode);
 
         return new CallNode(firstNode.getPosition(), firstNode, operator, null, null);
     }
     
     public Node getOperatorCallNode(Node firstNode, String operator, Node secondNode) {
         return getOperatorCallNode(firstNode, operator, secondNode, null);
     }
 
     public Node getOperatorCallNode(Node firstNode, String operator, Node secondNode, ISourcePosition defaultPosition) {
         if (defaultPosition != null) {
             firstNode = checkForNilNode(firstNode, defaultPosition);
             secondNode = checkForNilNode(secondNode, defaultPosition);
         }
         
         checkExpression(firstNode);
         checkExpression(secondNode);
 
         return new CallNode(firstNode.getPosition(), firstNode, operator, new ArrayNode(secondNode.getPosition(), secondNode), null);
     }
 
     public Node getMatchNode(Node firstNode, Node secondNode) {
         if (firstNode instanceof DRegexpNode) {
             return new Match2Node(firstNode.getPosition(), firstNode, secondNode);
         } else if (firstNode instanceof RegexpNode) {
             List<Integer> locals = allocateNamedLocals((RegexpNode) firstNode);
 
             if (locals.size() > 0) {
                 int[] primitiveLocals = new int[locals.size()];
                 for (int i = 0; i < primitiveLocals.length; i++) {
                     primitiveLocals[i] = locals.get(i);
                 }
                 return new Match2CaptureNode(firstNode.getPosition(), firstNode, secondNode, primitiveLocals);
             } else {
                 return new Match2Node(firstNode.getPosition(), firstNode, secondNode);
             }
         } else if (secondNode instanceof DRegexpNode || secondNode instanceof RegexpNode) {
             return new Match3Node(firstNode.getPosition(), firstNode, secondNode);
         }
 
         return getOperatorCallNode(firstNode, "=~", secondNode);
     }
 
     /**
      * Define an array set condition so we can return lhs
      * 
      * @param receiver array being set
      * @param index node which should evalute to index of array set
      * @return an AttrAssignNode
      */
     public Node aryset(Node receiver, Node index) {
         checkExpression(receiver);
 
         return new_attrassign(receiver.getPosition(), receiver, "[]=", index, false);
     }
 
     /**
      * Define an attribute set condition so we can return lhs
      * 
      * @param receiver object which contains attribute
      * @param name of the attribute being set
      * @return an AttrAssignNode
      */
     public Node attrset(Node receiver, String name) {
         return attrset(receiver, ".", name);
     }
 
     public Node attrset(Node receiver, String callType, String name) {
         checkExpression(receiver);
 
         return new_attrassign(receiver.getPosition(), receiver, name + "=", null, isLazy(callType));
     }
 
     public void backrefAssignError(Node node) {
         if (node instanceof NthRefNode) {
             String varName = "$" + ((NthRefNode) node).getMatchNumber();
             lexer.compile_error(PID.INVALID_ASSIGNMENT, "Can't set variable " + varName + '.');
         } else if (node instanceof BackRefNode) {
             String varName = "$" + ((BackRefNode) node).getType();
             lexer.compile_error(PID.INVALID_ASSIGNMENT, "Can't set variable " + varName + '.');
         }
     }
 
     public Node arg_add(ISourcePosition position, Node node1, Node node2) {
         if (node1 == null) {
             if (node2 == null) {
                 return new ArrayNode(position, NilImplicitNode.NIL);
             } else {
                 return new ArrayNode(node2.getPosition(), node2);
             }
         }
         if (node1 instanceof ArrayNode) return ((ArrayNode) node1).add(node2);
         
         return new ArgsPushNode(position, node1, node2);
     }
     
 	/**
 	 * @fixme position
 	 **/
     public Node node_assign(Node lhs, Node rhs) {
         if (lhs == null) return null;
 
         Node newNode = lhs;
 
         checkExpression(rhs);
         if (lhs instanceof AssignableNode) {
     	    ((AssignableNode) lhs).setValueNode(rhs);
         } else if (lhs instanceof IArgumentNode) {
             IArgumentNode invokableNode = (IArgumentNode) lhs;
             
             return invokableNode.setArgsNode(arg_add(lhs.getPosition(), invokableNode.getArgsNode(), rhs));
         }
         
         return newNode;
     }
     
     public Node ret_args(Node node, ISourcePosition position) {
         if (node != null) {
             if (node instanceof BlockPassNode) {
                 lexer.compile_error(PID.BLOCK_ARG_UNEXPECTED, "block argument should not be given");
             } else if (node instanceof ArrayNode && ((ArrayNode)node).size() == 1) {
                 node = ((ArrayNode)node).get(0);
             } else if (node instanceof SplatNode) {
                 node = newSValueNode(position, node);
             }
         }
 
         if (node == null) node = NilImplicitNode.NIL;
         
         return node;
     }
 
     /**
      * Is the supplied node a break/control statement?
      * 
      * @param node to be checked
      * @return true if a control node, false otherwise
      */
     public boolean isBreakStatement(Node node) {
         breakLoop: do {
             if (node == null) return false;
 
             switch (node.getNodeType()) {
             case BREAKNODE: case NEXTNODE: case REDONODE:
             case RETRYNODE: case RETURNNODE:
                 return true;
             default:
                 return false;
             }
         } while (true);                    
     }
     
     public void warnUnlessEOption(ID id, Node node, String message) {
         if (!configuration.isInlineSource()) {
             warnings.warn(id, node.getPosition(), message);
         }
     }
 
     public void warningUnlessEOption(ID id, Node node, String message) {
         if (warnings.isVerbose() && !configuration.isInlineSource()) {
             warnings.warning(id, node.getPosition(), message);
         }
     }
 
     // logical equivalent to value_expr in MRI
     public boolean checkExpression(Node node) {
         boolean conditional = false;
 
         while (node != null) {
             switch (node.getNodeType()) {
             case RETURNNODE: case BREAKNODE: case NEXTNODE: case REDONODE:
             case RETRYNODE:
                 if (!conditional) lexer.compile_error(PID.VOID_VALUE_EXPRESSION, "void value expression");
 
                 return false;
             case BLOCKNODE:
                 node = ((BlockNode) node).getLast();
                 break;
             case BEGINNODE:
                 node = ((BeginNode) node).getBodyNode();
                 break;
             case IFNODE:
                 if (!checkExpression(((IfNode) node).getThenBody())) return false;
                 node = ((IfNode) node).getElseBody();
                 break;
             case ANDNODE: case ORNODE:
                 conditional = true;
                 node = ((BinaryOperatorNode) node).getSecondNode();
                 break;
             default: // Node
                 return true;
             }
         }
 
         return true;
     }
     
     /**
      * Is this a literal in the sense that MRI has a NODE_LIT for.  This is different than
      * ILiteralNode.  We should pick a different name since ILiteralNode is something we created
      * which is similiar but used for a slightly different condition (can I do singleton things).
      * 
      * @param node to be tested
      * @return true if it is a literal
      */
     public boolean isLiteral(Node node) {
         return node != null && (node instanceof FixnumNode || node instanceof BignumNode || 
                 node instanceof FloatNode || node instanceof SymbolNode || 
                 (node instanceof RegexpNode && ((RegexpNode) node).getOptions().toJoniOptions() == 0));
     }
 
     private void handleUselessWarn(Node node, String useless) {
         if (Options.PARSER_WARN_USELESSS_USE_OF.load()) {
             warnings.warn(ID.USELESS_EXPRESSION, node.getPosition(), "Useless use of " + useless + " in void context.");
         }
     }
 
     /**
      * Check to see if current node is an useless statement.  If useless a warning if printed.
      * 
      * @param node to be checked.
      */
     public void checkUselessStatement(Node node) {
         if (!warnings.isVerbose() || (!configuration.isInlineSource() && configuration.isEvalParse())) return;
         
         uselessLoop: do {
             if (node == null) return;
             
             switch (node.getNodeType()) {
             case CALLNODE: {
                 String name = ((CallNode) node).getName();
                 
                 if (name == "+" || name == "-" || name == "*" || name == "/" || name == "%" || 
                     name == "**" || name == "+@" || name == "-@" || name == "|" || name == "^" || 
                     name == "&" || name == "<=>" || name == ">" || name == ">=" || name == "<" || 
                     name == "<=" || name == "==" || name == "!=") {
                     handleUselessWarn(node, name);
                 }
                 return;
             }
             case BACKREFNODE: case DVARNODE: case GLOBALVARNODE:
             case LOCALVARNODE: case NTHREFNODE: case CLASSVARNODE:
             case INSTVARNODE:
                 handleUselessWarn(node, "a variable"); return;
             // FIXME: Temporarily disabling because this fires way too much running Rails tests. JRUBY-518
             /*case CONSTNODE:
                 handleUselessWarn(node, "a constant"); return;*/
             case BIGNUMNODE: case DREGEXPNODE: case DSTRNODE: case DSYMBOLNODE:
             case FIXNUMNODE: case FLOATNODE: case REGEXPNODE:
             case STRNODE: case SYMBOLNODE:
                 handleUselessWarn(node, "a literal"); return;
             // FIXME: Temporarily disabling because this fires way too much running Rails tests. JRUBY-518
             /*case CLASSNODE: case COLON2NODE:
                 handleUselessWarn(node, "::"); return;*/
             case DOTNODE:
                 handleUselessWarn(node, ((DotNode) node).isExclusive() ? "..." : ".."); return;
             case DEFINEDNODE:
                 handleUselessWarn(node, "defined?"); return;
             case FALSENODE:
                 handleUselessWarn(node, "false"); return;
             case NILNODE: 
                 handleUselessWarn(node, "nil"); return;
             // FIXME: Temporarily disabling because this fires way too much running Rails tests. JRUBY-518
             /*case SELFNODE:
                 handleUselessWarn(node, "self"); return;*/
             case TRUENODE:
                 handleUselessWarn(node, "true"); return;
             default: return;
             }
         } while (true);
     }
 
     /**
      * Check all nodes but the last one in a BlockNode for useless (void context) statements.
      * 
      * @param blockNode to be checked.
      */
     public void checkUselessStatements(BlockNode blockNode) {
         if (warnings.isVerbose()) {
             Node lastNode = blockNode.getLast();
 
             for (int i = 0; i < blockNode.size(); i++) {
                 Node currentNode = blockNode.get(i);
         		
                 if (lastNode != currentNode ) {
                     checkUselessStatement(currentNode);
                 }
             }
         }
     }
 
 	/**
      * assign_in_cond
 	 **/
     private boolean checkAssignmentInCondition(Node node) {
         if (node instanceof MultipleAsgnNode) {
             lexer.compile_error(PID.MULTIPLE_ASSIGNMENT_IN_CONDITIONAL, "multiple assignment in conditional");
         } else if (node instanceof LocalAsgnNode || node instanceof DAsgnNode || node instanceof GlobalAsgnNode || node instanceof InstAsgnNode) {
             Node valueNode = ((AssignableNode) node).getValueNode();
             if (isStaticContent(valueNode)) {
                 warnings.warn(ID.ASSIGNMENT_IN_CONDITIONAL, node.getPosition(), "found = in conditional, should be ==");
             }
             return true;
         } 
 
         return false;
     }
 
     // Only literals or does it contain something more dynamic like variables?
     private boolean isStaticContent(Node node) {
         if (node instanceof HashNode) {
             HashNode hash = (HashNode) node;
             for (KeyValuePair<Node, Node> pair : hash.getPairs()) {
                 if (!isStaticContent(pair.getKey()) || !isStaticContent(pair.getValue())) return false;
             }
             return true;
         } else if (node instanceof ArrayNode) {
             ArrayNode array = (ArrayNode) node;
             int size = array.size();
 
             for (int i = 0; i < size; i++) {
                 if (!isStaticContent(array.get(i))) return false;
             }
             return true;
         } else if (node instanceof ILiteralNode || node instanceof NilNode || node instanceof TrueNode || node instanceof FalseNode) {
             return true;
         }
 
         return false;
     }
     
     protected Node makeNullNil(Node node) {
         return node == null ? NilImplicitNode.NIL : node;
     }
 
     private Node cond0(Node node) {
         checkAssignmentInCondition(node);
 
         if (node == null) return new NilNode(lexer.getPosition());
         
         Node leftNode;
         Node rightNode;
 
         // FIXME: DSTR,EVSTR,STR: warning "string literal in condition"
         switch(node.getNodeType()) {
         case DREGEXPNODE: {
             ISourcePosition position = node.getPosition();
 
             return new Match2Node(position, node, new GlobalVarNode(position, "$_"));
         }
         case ANDNODE:
             leftNode = cond0(((AndNode) node).getFirstNode());
             rightNode = cond0(((AndNode) node).getSecondNode());
             
             return new AndNode(node.getPosition(), makeNullNil(leftNode), makeNullNil(rightNode));
         case ORNODE:
             leftNode = cond0(((OrNode) node).getFirstNode());
             rightNode = cond0(((OrNode) node).getSecondNode());
             
             return new OrNode(node.getPosition(), makeNullNil(leftNode), makeNullNil(rightNode));
         case DOTNODE: {
             DotNode dotNode = (DotNode) node;
             if (dotNode.isLiteral()) return node; 
             
             String label = String.valueOf("FLIP" + node.hashCode());
             currentScope.getLocalScope().addVariable(label);
             int slot = currentScope.isDefined(label);
             
             return new FlipNode(node.getPosition(),
                     getFlipConditionNode(((DotNode) node).getBeginNode()),
                     getFlipConditionNode(((DotNode) node).getEndNode()),
                     dotNode.isExclusive(), slot);
         }
         case REGEXPNODE:
             if (Options.PARSER_WARN_REGEX_CONDITION.load()) {
                 warningUnlessEOption(ID.REGEXP_LITERAL_IN_CONDITION, node, "regex literal in condition");
             }
             
             return new MatchNode(node.getPosition(), node);
         }
 
         return node;
     }
 
     public Node getConditionNode(Node node) {
         Node cond = cond0(node);
 
         cond.setNewline();
 
         return cond;
     }
 
     /* MRI: range_op */
     private Node getFlipConditionNode(Node node) {
         if (!configuration.isInlineSource()) return node;
         
         node = getConditionNode(node);
 
         if (node instanceof FixnumNode) {
             warnUnlessEOption(ID.LITERAL_IN_CONDITIONAL_RANGE, node, "integer literal in conditional range");
             return getOperatorCallNode(node, "==", new GlobalVarNode(node.getPosition(), "$."));
         } 
 
         return node;
     }
 
     public SValueNode newSValueNode(ISourcePosition position, Node node) {
         return new SValueNode(position, node);
     }
     
     public SplatNode newSplatNode(ISourcePosition position, Node node) {
         return new SplatNode(position, makeNullNil(node));
     }
     
     public ArrayNode newArrayNode(ISourcePosition position, Node firstNode) {
         return new ArrayNode(position, makeNullNil(firstNode));
     }
 
     public ISourcePosition position(ISourcePositionHolder one, ISourcePositionHolder two) {
         return one == null ? two.getPosition() : one.getPosition();
     }
 
     public AndNode newAndNode(ISourcePosition position, Node left, Node right) {
         checkExpression(left);
         
         if (left == null && right == null) return new AndNode(position, makeNullNil(left), makeNullNil(right));
         
         return new AndNode(position(left, right), makeNullNil(left), makeNullNil(right));
     }
 
     public OrNode newOrNode(ISourcePosition position, Node left, Node right) {
         checkExpression(left);
 
         if (left == null && right == null) return new OrNode(position, makeNullNil(left), makeNullNil(right));
         
         return new OrNode(position(left, right), makeNullNil(left), makeNullNil(right));
     }
 
     /**
      * Ok I admit that this is somewhat ugly.  We post-process a chain of when nodes and analyze
      * them to re-insert them back into our new CaseNode the way we want.  The grammar is being
      * difficult and until I go back into the depths of that this is where things are.
      *
      * @param expression of the case node (e.g. case foo)
      * @param firstWhenNode first when (which could also be the else)
      * @return a new case node
      */
     public CaseNode newCaseNode(ISourcePosition position, Node expression, Node firstWhenNode) {
         ArrayNode cases = new ArrayNode(firstWhenNode != null ? firstWhenNode.getPosition() : position);
         CaseNode caseNode = new CaseNode(position, expression, cases);
 
         for (Node current = firstWhenNode; current != null; current = ((WhenNode) current).getNextCase()) {
             if (current instanceof WhenOneArgNode) {
                 cases.add(current);
             } else if (current instanceof WhenNode) {
                 simplifyMultipleArgumentWhenNodes((WhenNode) current, cases);
             } else {
                 caseNode.setElseNode(current);
                 break;
             }
         }
 
         return caseNode;
     }
 
     /*
      * This method exists for us to break up multiple expression when nodes (e.g. when 1,2,3:)
      * into individual whenNodes.  The primary reason for this is to ensure lazy evaluation of
      * the arguments (when foo,bar,gar:) to prevent side-effects.  In the old code this was done
      * using nested when statements, which was awful for interpreter and compilation.
      *
      * Notes: This has semantic equivalence but will not be lexically equivalent.  Compiler
      * needs to detect same bodies to simplify bytecode generated.
      */
     private void simplifyMultipleArgumentWhenNodes(WhenNode sourceWhen, ArrayNode cases) {
         Node expressionNodes = sourceWhen.getExpressionNodes();
 
         if (expressionNodes instanceof SplatNode || expressionNodes instanceof ArgsCatNode) {
             cases.add(sourceWhen);
             return;
         }
 
         if (expressionNodes instanceof ListNode) {
             ListNode list = (ListNode) expressionNodes;
             ISourcePosition position = sourceWhen.getPosition();
             Node bodyNode = sourceWhen.getBodyNode();
 
             for (int i = 0; i < list.size(); i++) {
                 Node expression = list.get(i);
 
                 if (expression instanceof SplatNode || expression instanceof ArgsCatNode) {
                     cases.add(new WhenNode(position, expression, bodyNode, null));
                 } else {
                     cases.add(new WhenOneArgNode(position, expression, bodyNode, null));
                 }
             }
         } else {
             cases.add(sourceWhen);
         }
     }
     
     public WhenNode newWhenNode(ISourcePosition position, Node expressionNodes, Node bodyNode, Node nextCase) {
         if (bodyNode == null) bodyNode = NilImplicitNode.NIL;
 
         if (expressionNodes instanceof SplatNode || expressionNodes instanceof ArgsCatNode || expressionNodes instanceof ArgsPushNode) {
             return new WhenNode(position, expressionNodes, bodyNode, nextCase);
         }
 
         ListNode list = (ListNode) expressionNodes;
 
         if (list.size() == 1) {
             Node element = list.get(0);
             
             if (!(element instanceof SplatNode)) {
                 return new WhenOneArgNode(position, element, bodyNode, nextCase);
             }
         }
 
         return new WhenNode(position, expressionNodes, bodyNode, nextCase);
     }
 
     // FIXME: Currently this is passing in position of receiver
     public Node new_opElementAsgnNode(Node receiverNode, String operatorName, Node argsNode, Node valueNode) {
         ISourcePosition position = lexer.tokline;  // FIXME: ruby_sourceline in new lexer.
 
         Node newNode = new OpElementAsgnNode(position, receiverNode, operatorName, argsNode, valueNode);
 
         fixpos(newNode, receiverNode);
 
         return newNode;
     }
 
     public Node newOpAsgn(ISourcePosition position, Node receiverNode, String callType, Node valueNode, String variableName, String operatorName) {
         return new OpAsgnNode(position, receiverNode, valueNode, variableName, operatorName, isLazy(callType));
     }
 
     public Node newOpConstAsgn(ISourcePosition position, Node lhs, String operatorName, Node rhs) {
         // FIXME: Maybe need to fixup position?
         if (lhs != null) {
             return new OpAsgnConstDeclNode(position, lhs, operatorName, rhs);
         } else {
             return new BeginNode(position, NilImplicitNode.NIL);
         }
     }
 
     public boolean isLazy(String callType) {
         return "&.".equals(callType);
     }
     
     public Node new_attrassign(ISourcePosition position, Node receiver, String name, Node args, boolean isLazy) {
         return new AttrAssignNode(position, receiver, name, args, isLazy);
     }
     
     private boolean isNumericOperator(String name) {
         if (name.length() == 1) {
             switch (name.charAt(0)) {
                 case '+': case '-': case '*': case '/': case '<': case '>':
                     return true;
             }
         } else if (name.length() == 2) {
             switch (name.charAt(0)) {
             case '<': case '>': case '=':
                 switch (name.charAt(1)) {
                 case '=': case '<':
                     return true;
                 }
             }
         }
         
         return false;
     }
 
     public Node new_call(Node receiver, String callType, String name, Node argsNode, Node iter) {
         if (argsNode instanceof BlockPassNode) {
             if (iter != null) lexer.compile_error(PID.BLOCK_ARG_AND_BLOCK_GIVEN, "Both block arg and actual block given.");
 
             BlockPassNode blockPass = (BlockPassNode) argsNode;
             return new CallNode(position(receiver, argsNode), receiver, name, blockPass.getArgsNode(), blockPass, isLazy(callType));
         }
 
         return new CallNode(position(receiver, argsNode), receiver, name, argsNode, iter, isLazy(callType));
 
     }
 
     public Node new_call(Node receiver, String name, Node argsNode, Node iter) {
         return new_call(receiver, ".", name, argsNode, iter);
     }
 
     public Colon2Node new_colon2(ISourcePosition position, Node leftNode, String name) {
         if (leftNode == null) return new Colon2ImplicitNode(position, name);
 
         return new Colon2ConstNode(position, leftNode, name);
     }
 
     public Colon3Node new_colon3(ISourcePosition position, String name) {
         return new Colon3Node(position, name);
     }
 
     public void frobnicate_fcall_args(FCallNode fcall, Node args, Node iter) {
         if (args instanceof BlockPassNode) {
             if (iter != null) lexer.compile_error(PID.BLOCK_ARG_AND_BLOCK_GIVEN, "Both block arg and actual block given.");
 
             BlockPassNode blockPass = (BlockPassNode) args;
             args = blockPass.getArgsNode();
             iter = blockPass;
         }
 
         fcall.setArgsNode(args);
         fcall.setIterNode(iter);
     }
 
     public void fixpos(Node node, Node orig) {
         if (node == null || orig == null) return;
 
         node.setPosition(orig.getPosition());
     }
 
     public Node new_fcall(String operation) {
         return new FCallNode(lexer.tokline, operation);
     }
 
     public Node new_super(ISourcePosition position, Node args) {
         if (args != null && args instanceof BlockPassNode) {
             return new SuperNode(position, ((BlockPassNode) args).getArgsNode(), args);
         }
         return new SuperNode(position, args);
     }
 
     /**
     *  Description of the RubyMethod
     */
     public void initTopLocalVariables() {
         DynamicScope scope = configuration.getScope(lexer.getFile());
         currentScope = scope.getStaticScope(); 
         
         result.setScope(scope);
     }
 
     /** Getter for property inSingle.
      * @return Value of property inSingle.
      */
     public boolean isInSingle() {
         return inSingleton != 0;
     }
 
     /** Setter for property inSingle.
      * @param inSingle New value of property inSingle.
      */
     public void setInSingle(int inSingle) {
         this.inSingleton = inSingle;
     }
 
     public boolean isInDef() {
         return inDefinition;
     }
 
     public void setInDef(boolean inDef) {
         this.inDefinition = inDef;
     }
 
     /** Getter for property inSingle.
      * @return Value of property inSingle.
      */
     public int getInSingle() {
         return inSingleton;
     }
 
     /**
      * Gets the result.
      * @return Returns a RubyParserResult
      */
     public RubyParserResult getResult() {
         return result;
     }
 
     /**
      * Sets the result.
      * @param result The result to set
      */
     public void setResult(RubyParserResult result) {
         this.result = result;
     }
 
     /**
      * Sets the configuration.
      * @param configuration The configuration to set
      */
     public void setConfiguration(ParserConfiguration configuration) {
         this.configuration = configuration;
     }
 
     public void setWarnings(IRubyWarnings warnings) {
         this.warnings = warnings;
     }
 
     public void setLexer(RubyLexer lexer) {
         this.lexer = lexer;
     }
 
     public DStrNode createDStrNode(ISourcePosition position) {
         DStrNode dstr = new DStrNode(position, lexer.getEncoding());
         if (getConfiguration().isFrozenStringLiteral()) dstr.setFrozen(true);
         return dstr;
     }
 
     public KeyValuePair<Node, Node> createKeyValue(Node key, Node value) {
         if (key != null && key instanceof StrNode) ((StrNode) key).setFrozen(true);
 
         return new KeyValuePair<>(key, value);
     }
 
     public Node asSymbol(ISourcePosition position, String value) {
         return new SymbolNode(position, value, lexer.getEncoding(), lexer.getTokenCR());
     }
         
     public Node asSymbol(ISourcePosition position, Node value) {
         return value instanceof StrNode ? new SymbolNode(position, ((StrNode) value).getValue()) :
                 new DSymbolNode(position, (DStrNode) value);
     }
     
     public Node literal_concat(ISourcePosition position, Node head, Node tail) { 
         if (head == null) return tail;
         if (tail == null) return head;
         
         if (head instanceof EvStrNode) {
             head = createDStrNode(head.getPosition()).add(head);
         }
 
         if (lexer.getHeredocIndent() > 0) {
             if (head instanceof StrNode) {
                 head = createDStrNode(head.getPosition()).add(head);
                 return list_append(head, tail);
             } else if (head instanceof DStrNode) {
                 return list_append(head, tail);
             }
         }
 
         if (tail instanceof StrNode) {
             if (head instanceof StrNode) {
                 StrNode front = (StrNode) head;
                 // string_contents always makes an empty strnode...which is sometimes valid but
                 // never if it ever is in literal_concat.
                 if (front.getValue().getRealSize() > 0) {
                     return new StrNode(head.getPosition(), front, (StrNode) tail);
                 } else {
                     return tail;
                 }
             } 
             head.setPosition(head.getPosition());
             return ((ListNode) head).add(tail);
         	
         } else if (tail instanceof DStrNode) {
             if (head instanceof StrNode) { // Str + oDStr -> Dstr(Str, oDStr.contents)
                 DStrNode newDStr = new DStrNode(head.getPosition(), ((DStrNode) tail).getEncoding());
                 newDStr.add(head);
                 newDStr.addAll(tail);
                 if (getConfiguration().isFrozenStringLiteral()) newDStr.setFrozen(true);
                 return newDStr;
             } 
 
             return ((ListNode) head).addAll(tail);
         } 
 
         // tail must be EvStrNode at this point 
         if (head instanceof StrNode) {
         	
             //Do not add an empty string node
             if(((StrNode) head).getValue().length() == 0) {
                 head = createDStrNode(head.getPosition());
             } else {
                 head = createDStrNode(head.getPosition()).add(head);
             }
         }
         return ((DStrNode) head).add(tail);
     }
 
     public Node newRescueModNode(Node body, Node rescueBody) {
         if (rescueBody == null) rescueBody = NilImplicitNode.NIL; // foo rescue () can make null.
         ISourcePosition pos = getPosition(body);
 
         return new RescueModNode(pos, body, new RescueBodyNode(pos, null, rescueBody, null));
     }
     
     public Node newEvStrNode(ISourcePosition position, Node node) {
         if (node instanceof StrNode || node instanceof DStrNode || node instanceof EvStrNode) return node;
 
         return new EvStrNode(position, node);
     }
     
     public Node new_yield(ISourcePosition position, Node node) {
         if (node != null && node instanceof BlockPassNode) {
             lexer.compile_error(PID.BLOCK_ARG_UNEXPECTED, "Block argument should not be given.");
         }
 
         return new YieldNode(position, node);
     }
     
     public NumericNode negateInteger(NumericNode integerNode) {
         if (integerNode instanceof FixnumNode) {
             FixnumNode fixnumNode = (FixnumNode) integerNode;
             
             fixnumNode.setValue(-fixnumNode.getValue());
             return fixnumNode;
         } else if (integerNode instanceof BignumNode) {
             BignumNode bignumNode = (BignumNode) integerNode;
 
             BigInteger value = bignumNode.getValue().negate();
 
             // Negating a bignum will make the last negative value of our bignum
             if (value.compareTo(RubyBignum.LONG_MIN) >= 0) {
                 return new FixnumNode(bignumNode.getPosition(), value.longValue());
             }
             
             bignumNode.setValue(value);
         }
         
         return integerNode;
     }
     
     public FloatNode negateFloat(FloatNode floatNode) {
         floatNode.setValue(-floatNode.getValue());
         
         return floatNode;
     }
 
     public ComplexNode negateComplexNode(ComplexNode complexNode) {
         complexNode.setNumber(negateNumeric(complexNode.getNumber()));
 
         return complexNode;
     }
 
     public RationalNode negateRational(RationalNode rationalNode) {
-        return new RationalNode(rationalNode.getPosition(),
-                                -rationalNode.getNumerator(),
-                                rationalNode.getDenominator());
+        return (RationalNode) rationalNode.negate();
     }
     
     private Node checkForNilNode(Node node, ISourcePosition defaultPosition) {
         return (node == null) ? new NilNode(defaultPosition) : node; 
     }
 
     public Node new_args(ISourcePosition position, ListNode pre, ListNode optional, RestArgNode rest,
             ListNode post, ArgsTailHolder tail) {
         ArgsNode argsNode;
         if (tail == null) {
             argsNode = new ArgsNode(position, pre, optional, rest, post, null);
         } else {
             argsNode = new ArgsNode(position, pre, optional, rest, post,
                     tail.getKeywordArgs(), tail.getKeywordRestArgNode(), tail.getBlockArg());
         }
 
         getCurrentScope().setSignature(Signature.from(argsNode));
 
         return argsNode;
     }
     
     public ArgsTailHolder new_args_tail(ISourcePosition position, ListNode keywordArg, 
             String keywordRestArgName, BlockArgNode blockArg) {
         if (keywordRestArgName == null) return new ArgsTailHolder(position, keywordArg, null, blockArg);
         
         String restKwargsName = keywordRestArgName;
 
         int slot = currentScope.exists(restKwargsName);
         if (slot == -1) slot = currentScope.addVariable(restKwargsName);
 
         KeywordRestArgNode keywordRestArg = new KeywordRestArgNode(position, restKwargsName, slot);
         
         return new ArgsTailHolder(position, keywordArg, keywordRestArg, blockArg);
     }
 
     public Node remove_duplicate_keys(HashNode hash) {
         List<Node> encounteredKeys = new ArrayList();
 
         for (KeyValuePair<Node,Node> pair: hash.getPairs()) {
             Node key = pair.getKey();
             if (key == null) continue;
             int index = encounteredKeys.indexOf(key);
             if (index >= 0) {
                 warn(ID.AMBIGUOUS_ARGUMENT, hash.getPosition(), "key " + key +
                         " is duplicated and overwritten on line " + (encounteredKeys.get(index).getLine() + 1));
             } else {
                 encounteredKeys.add(key);
             }
         }
 
         return hash;
     }
 
     public Node newAlias(ISourcePosition position, Node newNode, Node oldNode) {
         return new AliasNode(position, newNode, oldNode);
     }
 
     public Node newUndef(ISourcePosition position, Node nameNode) {
         return new UndefNode(position, nameNode);
     }
 
     /**
      * generate parsing error
      */
     public void yyerror(String message) {
         lexer.compile_error(PID.GRAMMAR_ERROR, message);
     }
 
     /**
      * generate parsing error
      * @param message text to be displayed.
      * @param expected list of acceptable tokens, if available.
      */
     public void yyerror(String message, String[] expected, String found) {
         lexer.compile_error(PID.GRAMMAR_ERROR, message + ", unexpected " + found + "\n");
     }
 
     public ISourcePosition getPosition(ISourcePositionHolder start) {
         return start != null ? lexer.getPosition(start.getPosition()) : lexer.getPosition();
     }
 
     public void warn(ID id, ISourcePosition position, String message, Object... data) {
         warnings.warn(id, position, message);
     }
 
     public void warning(ID id, ISourcePosition position, String message, Object... data) {
         if (warnings.isVerbose()) warnings.warning(id, position, message);
     }
 
     // ENEBO: Totally weird naming (in MRI is not allocated and is a local var name) [1.9]
     public boolean is_local_id(String name) {
         return lexer.isIdentifierChar(name.charAt(0));
     }
 
     // 1.9
     public ListNode list_append(Node list, Node item) {
         if (list == null) return new ArrayNode(item.getPosition(), item);
         if (!(list instanceof ListNode)) return new ArrayNode(list.getPosition(), list).add(item);
 
         return ((ListNode) list).add(item);
     }
 
     // 1.9
     public Node new_bv(String identifier) {
         if (!is_local_id(identifier)) {
             getterIdentifierError(lexer.getPosition(), identifier);
         }
         shadowing_lvar(identifier);
         
         return arg_var(identifier);
     }
 
     public ArgumentNode arg_var(String name) {
         return new ArgumentNode(lexer.getPosition(), name, getCurrentScope().addVariableThisScope(name));
     }
 
     public String formal_argument(String identifier) {
         lexer.validateFormalIdentifier(identifier);
 
         return shadowing_lvar(identifier);
     }
 
     // 1.9
     public String shadowing_lvar(String name) {
         if (name == "_") return name;
 
         StaticScope current = getCurrentScope();
         if (current.exists(name) >= 0) yyerror("duplicated argument name");
 
         if (current.isBlockScope() && warnings.isVerbose() && current.isDefined(name) >= 0 &&
                 Options.PARSER_WARN_LOCAL_SHADOWING.load()) {
             warnings.warning(ID.STATEMENT_NOT_REACHED, lexer.getPosition(), "shadowing outer local variable - " + name);
         }
 
         return name;
     }
 
     // 1.9
     public ListNode list_concat(Node first, Node second) {
         if (first instanceof ListNode) {
             if (second instanceof ListNode) {
                 return ((ListNode) first).addAll((ListNode) second);
             } else {
                 return ((ListNode) first).addAll(second);
             }
         }
 
         return new ArrayNode(first.getPosition(), first).add(second);
     }
 
     // 1.9
     /**
      * If node is a splat and it is splatting a literal array then return the literal array.
      * Otherwise return null.  This allows grammar to not splat into a Ruby Array if splatting
      * a literal array.
      */
     public Node splat_array(Node node) {
         if (node instanceof SplatNode) node = ((SplatNode) node).getValue();
         if (node instanceof ArrayNode) return node;
         return null;
     }
 
     // 1.9
     public Node arg_append(Node node1, Node node2) {
         if (node1 == null) return new ArrayNode(node2.getPosition(), node2);
         if (node1 instanceof ListNode) return ((ListNode) node1).add(node2);
         if (node1 instanceof BlockPassNode) return arg_append(((BlockPassNode) node1).getBodyNode(), node2);
         if (node1 instanceof ArgsPushNode) {
             ArgsPushNode pushNode = (ArgsPushNode) node1;
             Node body = pushNode.getSecondNode();
 
             return new ArgsCatNode(pushNode.getPosition(), pushNode.getFirstNode(),
                     new ArrayNode(body.getPosition(), body).add(node2));
         }
 
         return new ArgsPushNode(position(node1, node2), node1, node2);
     }
 
     // MRI: reg_fragment_check
     public void regexpFragmentCheck(RegexpNode end, ByteList value) {
         setRegexpEncoding(end, value);
         try {
             RubyRegexp.preprocessCheck(configuration.getRuntime(), value);
         } catch (RaiseException re) {
             compile_error(re.getMessage());
         }
     }        // 1.9 mode overrides to do extra checking...
 
     private List<Integer> allocateNamedLocals(RegexpNode regexpNode) {
         RubyRegexp pattern = RubyRegexp.newRegexp(configuration.getRuntime(), regexpNode.getValue(), regexpNode.getOptions());
         pattern.setLiteral();
         String[] names = pattern.getNames();
         int length = names.length;
         List<Integer> locals = new ArrayList<Integer>();
         StaticScope scope = getCurrentScope();
 
         for (int i = 0; i < length; i++) {
             // TODO: Pass by non-local-varnamed things but make sure consistent with list we get from regexp
             if (RubyLexer.getKeyword(names[i]) == null && !Character.isUpperCase(names[i].charAt(0))) {
                 int slot = scope.isDefined(names[i]);
                 if (slot >= 0) {
                     // If verbose and the variable is not just another named capture, warn
                     if (warnings.isVerbose() && !scope.isNamedCapture(slot)) {
                         warn(ID.AMBIGUOUS_ARGUMENT, getPosition(regexpNode), "named capture conflicts a local variable - " + names[i]);
                     }
                     locals.add(slot);
                 } else {
                     locals.add(getCurrentScope().addNamedCaptureVariable(names[i]));
                 }
             }
         }
 
         return locals;
     }
 
     private boolean is7BitASCII(ByteList value) {
         return StringSupport.codeRangeScan(value.getEncoding(), value) == StringSupport.CR_7BIT;
     }
 
     // TODO: Put somewhere more consolidated (similiar
     private char optionsEncodingChar(Encoding optionEncoding) {
         if (optionEncoding == USASCII_ENCODING) return 'n';
         if (optionEncoding == org.jcodings.specific.EUCJPEncoding.INSTANCE) return 'e';
         if (optionEncoding == org.jcodings.specific.SJISEncoding.INSTANCE) return 's';
         if (optionEncoding == UTF8_ENCODING) return 'u';
 
         return ' ';
     }
 
     public void compile_error(String message) { // mri: rb_compile_error_with_enc
         String line = lexer.getCurrentLine();
         ISourcePosition position = lexer.getPosition();
         String errorMessage = lexer.getFile() + ":" + (position.getLine() + 1) + ": ";
 
         if (line != null && line.length() > 5) {
             boolean addNewline = message != null && ! message.endsWith("\n");
 
             message += (addNewline ? "\n" : "") + line;
         }
 
         throw getConfiguration().getRuntime().newSyntaxError(errorMessage + message);
     }
 
     protected void compileError(Encoding optionEncoding, Encoding encoding) {
         lexer.compile_error(PID.REGEXP_ENCODING_MISMATCH, "regexp encoding option '" + optionsEncodingChar(optionEncoding) +
                 "' differs from source encoding '" + encoding + "'");
     }
     
     // MRI: reg_fragment_setenc_gen
     public void setRegexpEncoding(RegexpNode end, ByteList value) {
         RegexpOptions options = end.getOptions();
         Encoding optionsEncoding = options.setup(configuration.getRuntime()) ;
 
         // Change encoding to one specified by regexp options as long as the string is compatible.
         if (optionsEncoding != null) {
             if (optionsEncoding != value.getEncoding() && !is7BitASCII(value)) {
                 compileError(optionsEncoding, value.getEncoding());
             }
 
             value.setEncoding(optionsEncoding);
         } else if (options.isEncodingNone()) {
             if (value.getEncoding() == ASCII8BIT_ENCODING && !is7BitASCII(value)) {
                 compileError(optionsEncoding, value.getEncoding());
             }
             value.setEncoding(ASCII8BIT_ENCODING);
         } else if (lexer.getEncoding() == USASCII_ENCODING) {
             if (!is7BitASCII(value)) {
                 value.setEncoding(USASCII_ENCODING); // This will raise later
             } else {
                 value.setEncoding(ASCII8BIT_ENCODING);
             }
         }
     }    
 
     protected void checkRegexpSyntax(ByteList value, RegexpOptions options) {
         final String stringValue = value.toString();
         // Joni doesn't support these modifiers - but we can fix up in some cases - let the error delay until we try that
         if (stringValue.startsWith("(?u)") || stringValue.startsWith("(?a)") || stringValue.startsWith("(?d)"))
             return;
 
         try {
             // This is only for syntax checking but this will as a side-effect create an entry in the regexp cache.
             RubyRegexp.newRegexpParser(getConfiguration().getRuntime(), value, (RegexpOptions)options.clone());
         } catch (RaiseException re) {
             compile_error(re.getMessage());
         }
     }
 
     public Node newRegexpNode(ISourcePosition position, Node contents, RegexpNode end) {
         RegexpOptions options = end.getOptions();
         Encoding encoding = lexer.getEncoding();
 
         if (contents == null) {
             ByteList newValue = ByteList.create("");
             if (encoding != null) {
                 newValue.setEncoding(encoding);
             }
 
             regexpFragmentCheck(end, newValue);
             return new RegexpNode(position, newValue, options.withoutOnce());
         } else if (contents instanceof StrNode) {
             ByteList meat = (ByteList) ((StrNode) contents).getValue().clone();
             regexpFragmentCheck(end, meat);
             checkRegexpSyntax(meat, options.withoutOnce());
             return new RegexpNode(contents.getPosition(), meat, options.withoutOnce());
         } else if (contents instanceof DStrNode) {
             DStrNode dStrNode = (DStrNode) contents;
             
             for (int i = 0; i < dStrNode.size(); i++) {
                 Node fragment = dStrNode.get(i);
                 if (fragment instanceof StrNode) {
                     ByteList frag = ((StrNode) fragment).getValue();
                     regexpFragmentCheck(end, frag);
 //                    if (!lexer.isOneEight()) encoding = frag.getEncoding();
                 }
             }
             
             DRegexpNode dRegexpNode = new DRegexpNode(position, options, encoding);
             dRegexpNode.add(new StrNode(contents.getPosition(), createMaster(options)));
             dRegexpNode.addAll(dStrNode);
             return dRegexpNode;
         }
 
         // EvStrNode: #{val}: no fragment check, but at least set encoding
         ByteList master = createMaster(options);
         regexpFragmentCheck(end, master);
         encoding = master.getEncoding();
         DRegexpNode node = new DRegexpNode(position, options, encoding);
         node.add(new StrNode(contents.getPosition(), master));
         node.add(contents);
         return node;
     }
     
     // Create the magical empty 'master' string which will be encoded with
     // regexp options encoding so dregexps can end up starting with the
     // right encoding.
     private ByteList createMaster(RegexpOptions options) {
         Encoding encoding = options.setup(configuration.getRuntime());
 
         return new ByteList(ByteList.NULL_ARRAY, encoding);
     }
     
     // FIXME:  This logic is used by many methods in MRI, but we are only using it in lexer
     // currently.  Consolidate this when we tackle a big encoding refactoring
     public static int associateEncoding(ByteList buffer, Encoding newEncoding, int codeRange) {
         Encoding bufferEncoding = buffer.getEncoding();
                 
         if (newEncoding == bufferEncoding) return codeRange;
         
         // TODO: Special const error
         
         buffer.setEncoding(newEncoding);
         
         if (codeRange != StringSupport.CR_7BIT || !newEncoding.isAsciiCompatible()) {
             return StringSupport.CR_UNKNOWN;
         }
         
         return codeRange;
     }
     
     public KeywordArgNode keyword_arg(ISourcePosition position, AssignableNode assignable) {
         return new KeywordArgNode(position, assignable);
     }
     
     public NumericNode negateNumeric(NumericNode node) {
         switch (node.getNodeType()) {
             case FIXNUMNODE:
             case BIGNUMNODE:
                 return negateInteger(node);
             case COMPLEXNODE:
                 return negateComplexNode((ComplexNode) node);
             case FLOATNODE:
                 return negateFloat((FloatNode) node);
             case RATIONALNODE:
                 return negateRational((RationalNode) node);
         }
         
         yyerror("Invalid or unimplemented numeric to negate: " + node.toString());
         return null;
     }
     
     public Node new_defined(ISourcePosition position, Node something) {
         return new DefinedNode(position, something);
     }
 
     public String internalId() {
         return "";
     }
 
 }
