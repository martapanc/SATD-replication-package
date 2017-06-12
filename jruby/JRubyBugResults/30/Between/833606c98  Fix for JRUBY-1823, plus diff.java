diff --git a/src/org/jruby/ast/IterNode.java b/src/org/jruby/ast/IterNode.java
index f0ed12fe05..01cd7b0798 100644
--- a/src/org/jruby/ast/IterNode.java
+++ b/src/org/jruby/ast/IterNode.java
@@ -1,102 +1,104 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.InterpretedBlock;
 
 /**
  * Represents a block.  
  */
 public class IterNode extends Node {
     private final Node varNode;
     private final Node bodyNode;
     
     // What static scoping relationship exists when it comes into being.
     private StaticScope scope;
     private InterpretedBlock blockBody;
 
     public IterNode(ISourcePosition position, Node varNode, StaticScope scope, Node bodyNode) {
         this(position, varNode, scope, bodyNode, NodeType.ITERNODE);
     }
     
     public IterNode(ISourcePosition position, Node varNode, StaticScope scope, Node bodyNode, 
             NodeType id) {
         super(position, id);
         this.varNode = varNode;
         this.scope = scope;
         this.bodyNode = bodyNode;
-        this.blockBody = new InterpretedBlock(this, Arity.procArityOf(varNode));
+        NodeType argsNodeId = BlockBody.getArgumentTypeWackyHack(this);
+        this.blockBody = new InterpretedBlock(this, Arity.procArityOf(varNode), BlockBody.asArgumentType(argsNodeId));
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitIterNode(this);
     }
     
     public StaticScope getScope() {
         return scope;
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a Node
      */
     public Node getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Gets the varNode.
      * @return Returns a Node
      */
     public Node getVarNode() {
         return varNode;
     }
     
     public InterpretedBlock getBlockBody() {
         return blockBody;
     }
     
     public List<Node> childNodes() {
         return Node.createList(varNode, bodyNode);
     }
 }
diff --git a/src/org/jruby/compiler/ASTCompiler.java b/src/org/jruby/compiler/ASTCompiler.java
index 5459ad3d65..73b9cc2ef3 100644
--- a/src/org/jruby/compiler/ASTCompiler.java
+++ b/src/org/jruby/compiler/ASTCompiler.java
@@ -1,1127 +1,1128 @@
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
  * Copyright (C) 2006 Charles O Nutter <headius@headius.com>
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
 
 package org.jruby.compiler;
 
 import java.util.Iterator;
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
 import org.jruby.exceptions.RaiseException;
 import org.jruby.RubyMatchData;
 import org.jruby.ast.ArgsCatNode;
 import org.jruby.ast.ArgsPushNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.CaseNode;
 import org.jruby.ast.ClassNode;
 import org.jruby.ast.ClassVarDeclNode;
 import org.jruby.ast.DRegexpNode;
 import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.DXStrNode;
 import org.jruby.ast.DefsNode;
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
 import org.jruby.ast.XStrNode;
 import org.jruby.ast.ZSuperNode;
+import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *
  * @author headius
  */
 public class ASTCompiler {
     private boolean isAtRoot = true;
     
     public void compile(Node node, MethodCompiler context) {
         if (node == null) {
             context.loadNil();
             return;
         }
         switch (node.nodeId) {
             case ALIASNODE:
                 compileAlias(node, context);
                 break;
             case ANDNODE:
                 compileAnd(node, context);
                 break;
             case ARGSCATNODE:
                 compileArgsCat(node, context);
                 break;
             case ARGSPUSHNODE:
                 compileArgsPush(node, context);
                 break;
             case ARRAYNODE:
                 compileArray(node, context);
                 break;
             case ATTRASSIGNNODE:
                 compileAttrAssign(node, context);
                 break;
             case BACKREFNODE:
                 compileBackref(node, context);
                 break;
             case BEGINNODE:
                 compileBegin(node, context);
                 break;
             case BIGNUMNODE:
                 compileBignum(node, context);
                 break;
             case BLOCKNODE:
                 compileBlock(node, context);
                 break;
             case BREAKNODE:
                 compileBreak(node, context);
                 break;
             case CALLNODE:
                 compileCall(node, context);
                 break;
             case CASENODE:
                 compileCase(node, context);
                 break;
             case CLASSNODE:
                 compileClass(node, context);
                 break;
             case CLASSVARNODE:
                 compileClassVar(node, context);
                 break;
             case CLASSVARASGNNODE:
                 compileClassVarAsgn(node, context);
                 break;
             case CLASSVARDECLNODE:
                 compileClassVarDecl(node, context);
                 break;
             case COLON2NODE:
                 compileColon2(node, context);
                 break;
             case COLON3NODE:
                 compileColon3(node, context);
                 break;
             case CONSTDECLNODE:
                 compileConstDecl(node, context);
                 break;
             case CONSTNODE:
                 compileConst(node, context);
                 break;
             case DASGNNODE:
                 compileDAsgn(node, context);
                 break;
             case DEFINEDNODE:
                 compileDefined(node, context);
                 break;
             case DEFNNODE:
                 compileDefn(node, context);
                 break;
             case DEFSNODE:
                 compileDefs(node, context);
                 break;
             case DOTNODE:
                 compileDot(node, context);
                 break;
             case DREGEXPNODE:
                 compileDRegexp(node, context);
                 break;
             case DSTRNODE:
                 compileDStr(node, context);
                 break;
             case DSYMBOLNODE:
                 compileDSymbol(node, context);
                 break;
             case DVARNODE:
                 compileDVar(node, context);
                 break;
             case DXSTRNODE:
                 compileDXStr(node, context);
                 break;
             case ENSURENODE:
                 compileEnsureNode(node, context);
                 break;
             case EVSTRNODE:
                 compileEvStr(node, context);
                 break;
             case FALSENODE:
                 compileFalse(node, context);
                 break;
             case FCALLNODE:
                 compileFCall(node, context);
                 break;
             case FIXNUMNODE:
                 compileFixnum(node, context);
                 break;
             case FLIPNODE:
                 compileFlip(node, context);
                 break;
             case FLOATNODE:
                 compileFloat(node, context);
                 break;
             case FORNODE:
                 compileFor(node, context);
                 break;
             case GLOBALASGNNODE:
                 compileGlobalAsgn(node, context);
                 break;
             case GLOBALVARNODE:
                 compileGlobalVar(node, context);
                 break;
             case HASHNODE:
                 compileHash(node, context);
                 break;
             case IFNODE:
                 compileIf(node, context);
                 break;
             case INSTASGNNODE:
                 compileInstAsgn(node, context);
                 break;
             case INSTVARNODE:
                 compileInstVar(node, context);
                 break;
             case ITERNODE:
                 compileIter(node, context);
                 break;
             case LOCALASGNNODE:
                 compileLocalAsgn(node, context);
                 break;
             case LOCALVARNODE:
                 compileLocalVar(node, context);
                 break;
             case MATCH2NODE:
                 compileMatch2(node, context);
                 break;
             case MATCH3NODE:
                 compileMatch3(node, context);
                 break;
             case MATCHNODE:
                 compileMatch(node, context);
                 break;
             case MODULENODE:
                 compileModule(node, context);
                 break;
             case MULTIPLEASGNNODE:
                 compileMultipleAsgn(node, context);
                 break;
             case NEWLINENODE:
                 compileNewline(node, context);
                 break;
             case NEXTNODE:
                 compileNext(node, context);
                 break;
             case NTHREFNODE:
                 compileNthRef(node, context);
                 break;
             case NILNODE:
                 compileNil(node, context);
                 break;
             case NOTNODE:
                 compileNot(node, context);
                 break;
             case OPASGNANDNODE:
                 compileOpAsgnAnd(node, context);
                 break;
             case OPASGNNODE:
                 compileOpAsgn(node, context);
                 break;
             case OPASGNORNODE:
                 compileOpAsgnOr(node, context);
                 break;
             case OPELEMENTASGNNODE:
                 compileOpElementAsgn(node, context);
                 break;
             case ORNODE:
                 compileOr(node, context);
                 break;
             case POSTEXENODE:
                 compilePostExe(node, context);
                 break;
             case PREEXENODE:
                 compilePreExe(node, context);
                 break;
             case REDONODE:
                 compileRedo(node, context);
                 break;
             case REGEXPNODE:
                 compileRegexp(node, context);
                 break;
             case RESCUEBODYNODE:
                 throw new NotCompilableException("rescue body is handled by rescue compilation at: " + node.getPosition());
             case RESCUENODE:
                 compileRescue(node, context);
                 break;
             case RETRYNODE:
                 compileRetry(node, context);
                 break;
             case RETURNNODE:
                 compileReturn(node, context);
                 break;
             case ROOTNODE:
                 throw new NotCompilableException("Use compileRoot(); Root node at: " + node.getPosition());
             case SCLASSNODE:
                 compileSClass(node, context);
                 break;
             case SELFNODE:
                 compileSelf(node, context);
                 break;
             case SPLATNODE:
                 compileSplat(node, context);
                 break;
             case STRNODE:
                 compileStr(node, context);
                 break;
             case SUPERNODE:
                 compileSuper(node, context);
                 break;
             case SVALUENODE:
                 compileSValue(node, context);
                 break;
             case SYMBOLNODE:
                 compileSymbol(node, context);
                 break;
             case TOARYNODE:
                 compileToAry(node, context);
                 break;
             case TRUENODE:
                 compileTrue(node, context);
                 break;
             case UNDEFNODE:
                 compileUndef(node, context);
                 break;
             case UNTILNODE:
                 compileUntil(node, context);
                 break;
             case VALIASNODE:
                 compileVAlias(node, context);
                 break;
             case VCALLNODE:
                 compileVCall(node, context);
                 break;
             case WHILENODE:
                 compileWhile(node, context);
                 break;
             case WHENNODE:
                 assert false : "When nodes are handled by case node compilation.";
                 break;
             case XSTRNODE:
                 compileXStr(node, context);
                 break;
             case YIELDNODE:
                 compileYield(node, context);
                 break;
             case ZARRAYNODE:
                 compileZArray(node, context);
                 break;
             case ZSUPERNODE:
                 compileZSuper(node, context);
                 break;
             default:
                 assert false : "Unknown node encountered in compiler: " + node;
         }
     }
 
     public void compileArguments(Node node, MethodCompiler context) {
         switch (node.nodeId) {
             case ARGSCATNODE:
                 compileArgsCatArguments(node, context);
                 break;
             case ARGSPUSHNODE:
                 compileArgsPushArguments(node, context);
                 break;
             case ARRAYNODE:
                 compileArrayArguments(node, context);
                 break;
             case SPLATNODE:
                 compileSplatArguments(node, context);
                 break;
             default:
                 compile(node, context);
                 context.convertToJavaArray();
         }
     }
 
     public void compileAssignment(Node node, MethodCompiler context) {
         switch (node.nodeId) {
             case ATTRASSIGNNODE:
                 compileAttrAssignAssignment(node, context);
                 break;
             case DASGNNODE:
                 compileDAsgnAssignment(node, context);
                 break;
             case CLASSVARASGNNODE:
                 compileClassVarAsgnAssignment(node, context);
                 break;
             case CLASSVARDECLNODE:
                 compileClassVarDeclAssignment(node, context);
                 break;
             case CONSTDECLNODE:
                 compileConstDeclAssignment(node, context);
                 break;
             case GLOBALASGNNODE:
                 compileGlobalAsgnAssignment(node, context);
                 break;
             case INSTASGNNODE:
                 compileInstAsgnAssignment(node, context);
                 break;
             case LOCALASGNNODE:
                 compileLocalAsgnAssignment(node, context);
                 break;
             case MULTIPLEASGNNODE:
                 compileMultipleAsgnAssignment(node, context);
                 break;
             case ZEROARGNODE:
                 throw new NotCompilableException("Shouldn't get here; zeroarg does not do assignment: " + node);
             default:
                 throw new NotCompilableException("Can't compile assignment node: " + node);
         }
     }
 
     public static YARVNodesCompiler getYARVCompiler() {
         return new YARVNodesCompiler();
     }
 
     public void compileAlias(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final AliasNode alias = (AliasNode) node;
 
         context.defineAlias(alias.getNewName(), alias.getOldName());
     }
 
     public void compileAnd(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final AndNode andNode = (AndNode) node;
 
         compile(andNode.getFirstNode(), context);
 
         BranchCallback longCallback = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         compile(andNode.getSecondNode(), context);
                     }
                 };
 
         context.performLogicalAnd(longCallback);
     }
 
     public void compileArray(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ArrayNode arrayNode = (ArrayNode) node;
 
         ArrayCallback callback = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray, int index) {
                         Node node = (Node) ((Object[]) sourceArray)[index];
                         compile(node, context);
                     }
                 };
 
         context.createObjectArray(arrayNode.childNodes().toArray(), callback);
         context.createNewArray(arrayNode.isLightweight());
     }
 
     public void compileArgsCat(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ArgsCatNode argsCatNode = (ArgsCatNode) node;
 
         compile(argsCatNode.getFirstNode(), context);
         context.ensureRubyArray();
         compile(argsCatNode.getSecondNode(), context);
         context.splatCurrentValue();
         context.concatArrays();
     }
 
     public void compileArgsPush(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ArgsPushNode argsPush = (ArgsPushNode) node;
 
         compile(argsPush.getFirstNode(), context);
         compile(argsPush.getSecondNode(), context);
         context.concatArrays();
     }
 
     public void compileAttrAssign(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         AttrAssignNode attrAssignNode = (AttrAssignNode) node;
 
         compile(attrAssignNode.getReceiverNode(), context);
         compileArguments(attrAssignNode.getArgsNode(), context);
 
         context.getInvocationCompiler().invokeAttrAssign(attrAssignNode.getName());
     }
 
     public void compileAttrAssignAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         AttrAssignNode attrAssignNode = (AttrAssignNode) node;
 
         compile(attrAssignNode.getReceiverNode(), context);
         context.swapValues();
         if (attrAssignNode.getArgsNode() != null) {
             compileArguments(attrAssignNode.getArgsNode(), context);
             context.swapValues();
             context.appendToObjectArray();
         } else {
             context.createObjectArray(1);
         }
 
         context.getInvocationCompiler().invokeAttrAssign(attrAssignNode.getName());
     }
 
     public void compileBackref(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         BackRefNode iVisited = (BackRefNode) node;
 
         context.performBackref(iVisited.getType());
     }
 
     public void compileBegin(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         BeginNode beginNode = (BeginNode) node;
 
         compile(beginNode.getBodyNode(), context);
     }
 
     public void compileBignum(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         context.createNewBignum(((BignumNode) node).getValue());
     }
 
     public void compileBlock(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         BlockNode blockNode = (BlockNode) node;
 
         for (Iterator<Node> iter = blockNode.childNodes().iterator(); iter.hasNext();) {
             Node n = iter.next();
 
             compile(n, context);
 
             if (iter.hasNext()) {
                 // clear result from previous line
                 context.consumeCurrentValue();
             }
         }
     }
 
     public void compileBreak(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final BreakNode breakNode = (BreakNode) node;
 
         CompilerCallback valueCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         if (breakNode.getValueNode() != null) {
                             compile(breakNode.getValueNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         context.issueBreakEvent(valueCallback);
     }
 
     public void compileCall(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final CallNode callNode = (CallNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         compile(callNode.getReceiverNode(), context);
                     }
                 };
 
         CompilerCallback argsCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         compileArguments(callNode.getArgsNode(), context);
                     }
                 };
 
         if (callNode.getIterNode() == null) {
             // no block, go for simple version
             if (callNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeDynamic(callNode.getName(), receiverCallback, argsCallback, CallType.NORMAL, null, false);
             } else {
                 context.getInvocationCompiler().invokeDynamic(callNode.getName(), receiverCallback, null, CallType.NORMAL, null, false);
             }
         } else {
             CompilerCallback closureArg = getBlock(callNode.getIterNode());
 
             if (callNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeDynamic(callNode.getName(), receiverCallback, argsCallback, CallType.NORMAL, closureArg, false);
             } else {
                 context.getInvocationCompiler().invokeDynamic(callNode.getName(), receiverCallback, null, CallType.NORMAL, closureArg, false);
             }
         }
     }
 
     public void compileCase(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         CaseNode caseNode = (CaseNode) node;
 
         boolean hasCase = false;
         if (caseNode.getCaseNode() != null) {
             compile(caseNode.getCaseNode(), context);
             hasCase = true;
         }
 
         context.pollThreadEvents();
 
         Node firstWhenNode = caseNode.getFirstWhenNode();
         compileWhen(firstWhenNode, context, hasCase);
     }
 
     public void compileWhen(Node node, MethodCompiler context, final boolean hasCase) {
         if (node == null) {
             // reached the end of the when chain, pop the case (if provided) and we're done
             if (hasCase) {
                 context.consumeCurrentValue();
             }
             context.loadNil();
             return;
         }
 
         if (!(node instanceof WhenNode)) {
             if (hasCase) {
                 // case value provided and we're going into "else"; consume it.
                 context.consumeCurrentValue();
             }
             compile(node, context);
             return;
         }
 
         WhenNode whenNode = (WhenNode) node;
 
         if (whenNode.getExpressionNodes() instanceof ArrayNode) {
             ArrayNode arrayNode = (ArrayNode) whenNode.getExpressionNodes();
 
             compileMultiArgWhen(whenNode, arrayNode, 0, context, hasCase);
         } else {
             if (hasCase) {
                 context.duplicateCurrentValue();
             }
 
             // evaluate the when argument
             compile(whenNode.getExpressionNodes(), context);
 
             final WhenNode currentWhen = whenNode;
 
             if (hasCase) {
                 // we have a case value, call === on the condition value passing the case value
                 context.swapValues();
                 context.createObjectArray(1);
                 context.getInvocationCompiler().invokeEqq();
             }
 
             // check if the condition result is true, branch appropriately
             BranchCallback trueBranch = new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             // consume extra case value, we won't need it anymore
                             if (hasCase) {
                                 context.consumeCurrentValue();
                             }
 
                             if (currentWhen.getBodyNode() != null) {
                                 compile(currentWhen.getBodyNode(), context);
                             } else {
                                 context.loadNil();
                             }
                         }
                     };
 
             BranchCallback falseBranch = new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             // proceed to the next when
                             compileWhen(currentWhen.getNextCase(), context, hasCase);
                         }
                     };
 
             context.performBooleanBranch(trueBranch, falseBranch);
         }
     }
 
     public void compileMultiArgWhen(final WhenNode whenNode, final ArrayNode expressionsNode, final int conditionIndex, MethodCompiler context, final boolean hasCase) {
 
         if (conditionIndex >= expressionsNode.size()) {
             // done with conditions, continue to next when in the chain
             compileWhen(whenNode.getNextCase(), context, hasCase);
             return;
         }
 
         Node tag = expressionsNode.get(conditionIndex);
 
         context.setPosition(tag.getPosition());
 
         // reduce the when cases to a true or false ruby value for the branch below
         if (tag instanceof WhenNode) {
             // prepare to handle the when logic
             if (hasCase) {
                 context.duplicateCurrentValue();
             } else {
                 context.loadNull();
             }
             compile(((WhenNode) tag).getExpressionNodes(), context);
             context.checkWhenWithSplat();
         } else {
             if (hasCase) {
                 context.duplicateCurrentValue();
             }
 
             // evaluate the when argument
             compile(tag, context);
 
             if (hasCase) {
                 // we have a case value, call === on the condition value passing the case value
                 context.swapValues();
                 context.createObjectArray(1);
                 context.getInvocationCompiler().invokeEqq();
             }
         }
 
         // check if the condition result is true, branch appropriately
         BranchCallback trueBranch = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         // consume extra case value, we won't need it anymore
                         if (hasCase) {
                             context.consumeCurrentValue();
                         }
 
                         if (whenNode.getBodyNode() != null) {
                             compile(whenNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         BranchCallback falseBranch = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         // proceed to the next when
                         compileMultiArgWhen(whenNode, expressionsNode, conditionIndex + 1, context, hasCase);
                     }
                 };
 
         context.performBooleanBranch(trueBranch, falseBranch);
     }
 
     public void compileClass(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final ClassNode classNode = (ClassNode) node;
 
         final Node superNode = classNode.getSuperNode();
 
         final Node cpathNode = classNode.getCPath();
 
         CompilerCallback superCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         compile(superNode, context);
                     }
                 };
         if (superNode == null) {
             superCallback = null;
         }
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         boolean oldIsAtRoot = isAtRoot;
                         isAtRoot = false;
                         if (classNode.getBodyNode() != null) {
                             compile(classNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                         isAtRoot = oldIsAtRoot;
                     }
                 };
 
         CompilerCallback pathCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         if (cpathNode instanceof Colon2Node) {
                             Node leftNode = ((Colon2Node) cpathNode).getLeftNode();
                             if (leftNode != null) {
                                 compile(leftNode, context);
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
 
         context.defineClass(classNode.getCPath().getName(), classNode.getScope(), superCallback, pathCallback, bodyCallback, null);
     }
 
     public void compileSClass(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final SClassNode sclassNode = (SClassNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         compile(sclassNode.getReceiverNode(), context);
                     }
                 };
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         boolean oldIsAtRoot = isAtRoot;
                         isAtRoot = false;
                         if (sclassNode.getBodyNode() != null) {
                             compile(sclassNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                         isAtRoot = oldIsAtRoot;
                     }
                 };
 
         context.defineClass("SCLASS", sclassNode.getScope(), null, null, bodyCallback, receiverCallback);
     }
 
     public void compileClassVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ClassVarNode classVarNode = (ClassVarNode) node;
 
         context.retrieveClassVariable(classVarNode.getName());
     }
 
     public void compileClassVarAsgn(Node node, MethodCompiler context) {
         ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
 
         // FIXME: probably more efficient with a callback
         compile(classVarAsgnNode.getValueNode(), context);
 
         compileClassVarAsgnAssignment(node, context);
     }
 
     public void compileClassVarAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
 
         context.assignClassVariable(classVarAsgnNode.getName());
     }
 
     public void compileClassVarDecl(Node node, MethodCompiler context) {
         ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
 
         // FIXME: probably more efficient with a callback
         compile(classVarDeclNode.getValueNode(), context);
 
         compileClassVarDeclAssignment(node, context);
     }
 
     public void compileClassVarDeclAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
 
         context.declareClassVariable(classVarDeclNode.getName());
     }
 
     public void compileConstDecl(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ConstDeclNode constDeclNode = (ConstDeclNode) node;
         Node constNode = constDeclNode.getConstNode();
 
         if (constNode == null) {
             compile(constDeclNode.getValueNode(), context);
 
             context.assignConstantInCurrent(constDeclNode.getName());
         } else if (constNode.nodeId == NodeType.COLON2NODE) {
             compile(((Colon2Node) constNode).getLeftNode(), context);
             compile(constDeclNode.getValueNode(), context);
 
             context.assignConstantInModule(constDeclNode.getName());
         } else {// colon3, assign in Object
             compile(constDeclNode.getValueNode(), context);
 
             context.assignConstantInObject(constDeclNode.getName());
         }
     }
 
     public void compileConstDeclAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ConstDeclNode constDeclNode = (ConstDeclNode) node;
         Node constNode = constDeclNode.getConstNode();
 
         if (constNode == null) {
             context.assignConstantInCurrent(constDeclNode.getName());
         } else if (constNode.nodeId == NodeType.COLON2NODE) {
             compile(((Colon2Node) constNode).getLeftNode(), context);
             context.swapValues();
             context.assignConstantInModule(constDeclNode.getName());
         } else {// colon3, assign in Object
             context.assignConstantInObject(constDeclNode.getName());
         }
     }
 
     public void compileConst(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ConstNode constNode = (ConstNode) node;
 
         context.retrieveConstant(constNode.getName());
     }
 
     public void compileColon2(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         final Colon2Node iVisited = (Colon2Node) node;
         Node leftNode = iVisited.getLeftNode();
         final String name = iVisited.getName();
 
         if (leftNode == null) {
             context.loadObject();
             context.retrieveConstantFromModule(name);
         } else {
             final CompilerCallback receiverCallback = new CompilerCallback() {
 
                         public void call(MethodCompiler context) {
                             compile(iVisited.getLeftNode(), context);
                         }
                     };
 
             BranchCallback moduleCallback = new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             receiverCallback.call(context);
                             context.retrieveConstantFromModule(name);
                         }
                     };
 
             BranchCallback notModuleCallback = new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             context.getInvocationCompiler().invokeDynamic(name, receiverCallback, null, CallType.FUNCTIONAL, null, false);
                         }
                     };
 
             context.branchIfModule(receiverCallback, moduleCallback, notModuleCallback);
         }
     }
 
     public void compileColon3(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         Colon3Node iVisited = (Colon3Node) node;
         String name = iVisited.getName();
 
         context.loadObject();
         context.retrieveConstantFromModule(name);
     }
 
     public void compileGetDefinitionBase(final Node node, MethodCompiler context) {
         BranchCallback reg = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         context.inDefined();
                         compileGetDefinition(node, context);
                     }
                 };
         BranchCallback out = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         context.outDefined();
                     }
                 };
         context.protect(reg, out, String.class);
     }
 
     public void compileDefined(final Node node, MethodCompiler context) {
         compileGetDefinitionBase(((DefinedNode) node).getExpressionNode(), context);
         context.stringOrNil();
     }
 
     public void compileGetArgumentDefinition(final Node node, MethodCompiler context, String type) {
         if (node == null) {
             context.pushString(type);
         } else if (node instanceof ArrayNode) {
             Object endToken = context.getNewEnding();
             for (int i = 0; i < ((ArrayNode) node).size(); i++) {
                 Node iterNode = ((ArrayNode) node).get(i);
                 compileGetDefinition(iterNode, context);
                 context.ifNull(endToken);
             }
             context.pushString(type);
             Object realToken = context.getNewEnding();
             context.go(realToken);
             context.setEnding(endToken);
             context.pushNull();
             context.setEnding(realToken);
         } else {
             compileGetDefinition(node, context);
             Object endToken = context.getNewEnding();
             context.ifNull(endToken);
             context.pushString(type);
             Object realToken = context.getNewEnding();
             context.go(realToken);
             context.setEnding(endToken);
             context.pushNull();
             context.setEnding(realToken);
         }
     }
 
     public void compileGetDefinition(final Node node, MethodCompiler context) {
         switch (node.nodeId) {
             case CLASSVARASGNNODE:
             case CLASSVARDECLNODE:
             case CONSTDECLNODE:
             case DASGNNODE:
             case GLOBALASGNNODE:
             case LOCALASGNNODE:
             case MULTIPLEASGNNODE:
             case OPASGNNODE:
             case OPELEMENTASGNNODE:
                 context.pushString("assignment");
                 break;
             case BACKREFNODE:
                 context.backref();
                 context.isInstanceOf(RubyMatchData.class,
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 context.pushString("$" + ((BackRefNode) node).getType());
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case DVARNODE:
                 context.pushString("local-variable(in-block)");
                 break;
             case FALSENODE:
                 context.pushString("false");
                 break;
             case TRUENODE:
                 context.pushString("true");
                 break;
             case LOCALVARNODE:
@@ -1272,1910 +1273,1899 @@ public class ASTCompiler {
                                 public void branch(MethodCompiler context) {
                                     context.pushString("method");
                                 }
                             };
                     BranchCallback none = new BranchCallback() {
 
                                 public void branch(MethodCompiler context) {
                                     context.pushNull();
                                 }
                             };
                     context.isConstantBranch(setup, isConstant, isMethod, none, name);
                     break;
                 }
             case CALLNODE:
                 {
                     final CallNode iVisited = (CallNode) node;
                     Object isnull = context.getNewEnding();
                     Object ending = context.getNewEnding();
                     compileGetDefinition(iVisited.getReceiverNode(), context);
                     context.ifNull(isnull);
 
                     context.rescue(new BranchCallback() {
 
                                 public void branch(MethodCompiler context) {
                                     compile(iVisited.getReceiverNode(), context); //[IRubyObject]
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
 
                                                 public void branch(MethodCompiler context) {
                                                     compileGetArgumentDefinition(iVisited.getArgsNode(), context, "method");
                                                 }
                                             },
                                             new BranchCallback() {
 
                                                 public void branch(MethodCompiler context) {
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
 
                                 public void branch(MethodCompiler context) {
                                     context.pushNull();
                                 }
                             }, String.class);
 
                     //          context.swapValues();
             //context.consumeCurrentValue();
                     context.go(ending);
                     context.setEnding(isnull);
                     context.pushNull();
                     context.setEnding(ending);
                     break;
                 }
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
 
                                 public void branch(MethodCompiler context) {
                                     context.consumeCurrentValue();
                                     context.pushString("class variable");
                                     context.go(ending);
                                 }
                             },
                             new BranchCallback() {
 
                                 public void branch(MethodCompiler context) {
                                 }
                             });
                     context.setEnding(second);  //[RubyClass]
                     context.duplicateCurrentValue();
                     context.isClassVarDefined(iVisited.getName(),
                             new BranchCallback() {
 
                                 public void branch(MethodCompiler context) {
                                     context.consumeCurrentValue();
                                     context.pushString("class variable");
                                     context.go(ending);
                                 }
                             },
                             new BranchCallback() {
 
                                 public void branch(MethodCompiler context) {
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
                     context.pushString("class variable");
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
 
                     context.pushString("super");
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
 
                                 public void branch(MethodCompiler context) {
                                     compile(iVisited.getReceiverNode(), context); //[IRubyObject]
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
 
                                                 public void branch(MethodCompiler context) {
                                                     compileGetArgumentDefinition(iVisited.getArgsNode(), context, "assignment");
                                                 }
                                             },
                                             new BranchCallback() {
 
                                                 public void branch(MethodCompiler context) {
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
 
                                 public void branch(MethodCompiler context) {
                                     context.pushNull();
                                 }
                             }, String.class);
 
                     context.go(ending);
                     context.setEnding(isnull);
                     context.pushNull();
                     context.setEnding(ending);
                     break;
                 }
             default:
                 context.rescue(new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 compile(node, context);
                                 context.consumeCurrentValue();
                                 context.pushNull();
                             }
                         }, JumpException.class,
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 context.pushNull();
                             }
                         }, String.class);
                 context.consumeCurrentValue();
                 context.pushString("expression");
         }
     }
 
     public void compileDAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         DAsgnNode dasgnNode = (DAsgnNode) node;
 
         compile(dasgnNode.getValueNode(), context);
 
         compileDAsgnAssignment(dasgnNode, context);
     }
 
     public void compileDAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         DAsgnNode dasgnNode = (DAsgnNode) node;
 
         context.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth());
     }
 
     public void compileDefn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final DefnNode defnNode = (DefnNode) node;
         final ArgsNode argsNode = defnNode.getArgsNode();
 
         CompilerCallback body = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         if (defnNode.getBodyNode() != null) {
                             compile(defnNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         CompilerCallback args = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         compileArgs(argsNode, context);
                     }
                 };
 
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(defnNode.getArgsNode());
         inspector.inspect(defnNode.getBodyNode());
 
         context.defineNewMethod(defnNode.getName(), defnNode.getArgsNode().getArity().getValue(), defnNode.getScope(), body, args, null, inspector, isAtRoot);
     }
 
     public void compileDefs(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final DefsNode defsNode = (DefsNode) node;
         final ArgsNode argsNode = defsNode.getArgsNode();
 
         CompilerCallback receiver = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         compile(defsNode.getReceiverNode(), context);
                     }
                 };
 
         CompilerCallback body = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         if (defsNode.getBodyNode() != null) {
                             compile(defsNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         CompilerCallback args = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         compileArgs(argsNode, context);
                     }
                 };
 
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(defsNode.getArgsNode());
         inspector.inspect(defsNode.getBodyNode());
 
         context.defineNewMethod(defsNode.getName(), defsNode.getArgsNode().getArity().getValue(), defsNode.getScope(), body, args, receiver, inspector, false);
     }
 
     public void compileArgs(Node node, MethodCompiler context) {
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
 
                         public void nextValue(MethodCompiler context, Object object, int index) {
                             // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
                             context.getVariableCompiler().assignLocalVariable(index);
                         }
                     };
         }
 
         if (opt > 0) {
             optionalGiven = new ArrayCallback() {
 
                         public void nextValue(MethodCompiler context, Object object, int index) {
                             Node optArg = ((ListNode) object).get(index);
 
                             compileAssignment(optArg, context);
                         }
                     };
             optionalNotGiven = new ArrayCallback() {
 
                         public void nextValue(MethodCompiler context, Object object, int index) {
                             Node optArg = ((ListNode) object).get(index);
 
                             compile(optArg, context);
                         }
                     };
         }
 
         if (rest > -1) {
             restAssignment = new CompilerCallback() {
 
                         public void call(MethodCompiler context) {
                             context.getVariableCompiler().assignLocalVariable(argsNode.getRestArg());
                         }
                     };
         }
 
         if (argsNode.getBlockArgNode() != null) {
             blockAssignment = new CompilerCallback() {
 
                         public void call(MethodCompiler context) {
                             context.getVariableCompiler().assignLocalVariable(argsNode.getBlockArgNode().getCount());
                         }
                     };
         }
 
         context.lineNumber(argsNode.getPosition());
 
         context.getVariableCompiler().checkMethodArity(required, opt, rest);
         context.getVariableCompiler().assignMethodArguments(argsNode.getArgs(),
                 argsNode.getRequiredArgsCount(),
                 argsNode.getOptArgs(),
                 argsNode.getOptionalArgsCount(),
                 requiredAssignment,
                 optionalGiven,
                 optionalNotGiven,
                 restAssignment,
                 blockAssignment);
     }
 
     public void compileDot(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         DotNode dotNode = (DotNode) node;
 
         compile(dotNode.getBeginNode(), context);
         compile(dotNode.getEndNode(), context);
 
         context.createNewRange(dotNode.isExclusive());
     }
 
     public void compileDRegexp(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final DRegexpNode dregexpNode = (DRegexpNode) node;
 
         CompilerCallback createStringCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         ArrayCallback dstrCallback = new ArrayCallback() {
 
                                     public void nextValue(MethodCompiler context, Object sourceArray,
                                             int index) {
                                         compile(dregexpNode.get(index), context);
                                     }
                                 };
                         context.createNewString(dstrCallback, dregexpNode.size());
                         context.toJavaString();
                     }
                 };
 
         context.createNewRegexp(createStringCallback, dregexpNode.getOptions());
     }
 
     public void compileDStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final DStrNode dstrNode = (DStrNode) node;
 
         ArrayCallback dstrCallback = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray,
                             int index) {
                         compile(dstrNode.get(index), context);
                     }
                 };
         context.createNewString(dstrCallback, dstrNode.size());
     }
 
     public void compileDSymbol(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final DSymbolNode dsymbolNode = (DSymbolNode) node;
 
         ArrayCallback dstrCallback = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray,
                             int index) {
                         compile(dsymbolNode.get(index), context);
                     }
                 };
         context.createNewSymbol(dstrCallback, dsymbolNode.size());
     }
 
     public void compileDVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         DVarNode dvarNode = (DVarNode) node;
 
         context.getVariableCompiler().retrieveLocalVariable(dvarNode.getIndex(), dvarNode.getDepth());
     }
 
     public void compileDXStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final DXStrNode dxstrNode = (DXStrNode) node;
 
         final ArrayCallback dstrCallback = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray,
                             int index) {
                         compile(dxstrNode.get(index), context);
                     }
                 };
 
         CompilerCallback argsCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         context.createNewString(dstrCallback, dxstrNode.size());
                         context.createObjectArray(1);
                     }
                 };
 
         context.getInvocationCompiler().invokeDynamic("`", null, argsCallback, CallType.FUNCTIONAL, null, false);
     }
 
     public void compileEnsureNode(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final EnsureNode ensureNode = (EnsureNode) node;
 
         if (ensureNode.getEnsureNode() != null) {
             context.protect(new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             if (ensureNode.getBodyNode() != null) {
                                 compile(ensureNode.getBodyNode(), context);
                             } else {
                                 context.loadNil();
                             }
                         }
                     },
                     new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             compile(ensureNode.getEnsureNode(), context);
                             context.consumeCurrentValue();
                         }
                     }, IRubyObject.class);
         } else {
             if (ensureNode.getBodyNode() != null) {
                 compile(ensureNode.getBodyNode(), context);
             } else {
                 context.loadNil();
             }
         }
     }
 
     public void compileEvStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final EvStrNode evStrNode = (EvStrNode) node;
 
         compile(evStrNode.getBody(), context);
         context.asString();
     }
 
     public void compileFalse(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         context.loadFalse();
 
         context.pollThreadEvents();
     }
 
     public void compileFCall(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final FCallNode fcallNode = (FCallNode) node;
 
         CompilerCallback argsCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         compileArguments(fcallNode.getArgsNode(), context);
                     }
                 };
 
         if (fcallNode.getIterNode() == null) {
             // no block, go for simple version
             if (fcallNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, argsCallback, CallType.FUNCTIONAL, null, false);
             } else {
                 context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, null, CallType.FUNCTIONAL, null, false);
             }
         } else {
             CompilerCallback closureArg = getBlock(fcallNode.getIterNode());
 
             if (fcallNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, argsCallback, CallType.FUNCTIONAL, closureArg, false);
             } else {
                 context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, null, CallType.FUNCTIONAL, closureArg, false);
             }
         }
     }
 
     private CompilerCallback getBlock(Node node) {
         if (node == null) {
             return null;
         }
 
         switch (node.nodeId) {
             case ITERNODE:
                 final IterNode iterNode = (IterNode) node;
 
                 return new CompilerCallback() {
 
                             public void call(MethodCompiler context) {
                                 compile(iterNode, context);
                             }
                         };
             case BLOCKPASSNODE:
                 final BlockPassNode blockPassNode = (BlockPassNode) node;
 
                 return new CompilerCallback() {
 
                             public void call(MethodCompiler context) {
                                 compile(blockPassNode.getBodyNode(), context);
                                 context.unwrapPassedBlock();
                             }
                         };
             default:
                 throw new NotCompilableException("ERROR: Encountered a method with a non-block, non-blockpass iter node at: " + node);
         }
     }
 
     public void compileFixnum(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         FixnumNode fixnumNode = (FixnumNode) node;
 
         context.createNewFixnum(fixnumNode.getValue());
     }
 
     public void compileFlip(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final FlipNode flipNode = (FlipNode) node;
 
         context.getVariableCompiler().retrieveLocalVariable(flipNode.getIndex(), flipNode.getDepth());
 
         if (flipNode.isExclusive()) {
             context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getEndNode(), context);
                             context.performBooleanBranch(new BranchCallback() {
 
                                         public void branch(MethodCompiler context) {
                                             context.loadFalse();
                                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                                             context.consumeCurrentValue();
                                         }
                                     }, new BranchCallback() {
 
                                         public void branch(MethodCompiler context) {
                                         }
                                     });
                             context.loadTrue();
                         }
                     }, new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getBeginNode(), context);
                             becomeTrueOrFalse(context);
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                         }
                     });
         } else {
             context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getEndNode(), context);
                             context.performBooleanBranch(new BranchCallback() {
 
                                         public void branch(MethodCompiler context) {
                                             context.loadFalse();
                                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                                             context.consumeCurrentValue();
                                         }
                                     }, new BranchCallback() {
 
                                         public void branch(MethodCompiler context) {
                                         }
                                     });
                             context.loadTrue();
                         }
                     }, new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getBeginNode(), context);
                             context.performBooleanBranch(new BranchCallback() {
 
                                         public void branch(MethodCompiler context) {
                                             compile(flipNode.getEndNode(), context);
                                             flipTrueOrFalse(context);
                                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                                             context.consumeCurrentValue();
                                             context.loadTrue();
                                         }
                                     }, new BranchCallback() {
 
                                         public void branch(MethodCompiler context) {
                                             context.loadFalse();
                                         }
                                     });
                         }
                     });
         }
     }
 
     private void becomeTrueOrFalse(MethodCompiler context) {
         context.performBooleanBranch(new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         context.loadTrue();
                     }
                 }, new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         context.loadFalse();
                     }
                 });
     }
 
     private void flipTrueOrFalse(MethodCompiler context) {
         context.performBooleanBranch(new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         context.loadFalse();
                     }
                 }, new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         context.loadTrue();
                     }
                 });
     }
 
     public void compileFloat(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         FloatNode floatNode = (FloatNode) node;
 
         context.createNewFloat(floatNode.getValue());
     }
 
     public void compileFor(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final ForNode forNode = (ForNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         compile(forNode.getIterNode(), context);
                     }
                 };
 
         final CompilerCallback closureArg = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         compileForIter(forNode, context);
                     }
                 };
 
         context.getInvocationCompiler().invokeDynamic("each", receiverCallback, null, CallType.NORMAL, closureArg, false);
     }
 
     public void compileForIter(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final ForNode forNode = (ForNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         if (forNode.getBodyNode() != null) {
                             compile(forNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         // create the closure class and instantiate it
         final CompilerCallback closureArgs = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         if (forNode.getVarNode() != null) {
                             compileAssignment(forNode.getVarNode(), context);
                         }
                     }
                 };
 
         boolean hasMultipleArgsHead = false;
         if (forNode.getVarNode() instanceof MultipleAsgnNode) {
             hasMultipleArgsHead = ((MultipleAsgnNode) forNode.getVarNode()).getHeadNode() != null;
         }
 
         NodeType argsNodeId = null;
         if (forNode.getVarNode() != null) {
             argsNodeId = forNode.getVarNode().nodeId;
         }
 
         if (argsNodeId == null) {
             // no args, do not pass args processor
             context.createNewForLoop(Arity.procArityOf(forNode.getVarNode()).getValue(),
                     closureBody, null, hasMultipleArgsHead, argsNodeId);
         } else {
             context.createNewForLoop(Arity.procArityOf(forNode.getVarNode()).getValue(),
                     closureBody, closureArgs, hasMultipleArgsHead, argsNodeId);
         }
     }
 
     public void compileGlobalAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode) node;
 
         compile(globalAsgnNode.getValueNode(), context);
 
         if (globalAsgnNode.getName().length() == 2) {
             switch (globalAsgnNode.getName().charAt(1)) {
                 case '_':
                     context.getVariableCompiler().assignLastLine();
                     return;
                 case '~':
                     assert false : "Parser shouldn't allow assigning to $~";
                     return;
                 default:
                 // fall off the end, handle it as a normal global
             }
         }
 
         context.assignGlobalVariable(globalAsgnNode.getName());
     }
 
     public void compileGlobalAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode) node;
 
         if (globalAsgnNode.getName().length() == 2) {
             switch (globalAsgnNode.getName().charAt(1)) {
                 case '_':
                     context.getVariableCompiler().assignLastLine();
                     return;
                 case '~':
                     assert false : "Parser shouldn't allow assigning to $~";
                     return;
                 default:
                 // fall off the end, handle it as a normal global
             }
         }
 
         context.assignGlobalVariable(globalAsgnNode.getName());
     }
 
     public void compileGlobalVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         GlobalVarNode globalVarNode = (GlobalVarNode) node;
 
         if (globalVarNode.getName().length() == 2) {
             switch (globalVarNode.getName().charAt(1)) {
                 case '_':
                     context.getVariableCompiler().retrieveLastLine();
                     return;
                 case '~':
                     context.getVariableCompiler().retrieveBackRef();
                     return;
             }
         }
 
         context.retrieveGlobalVariable(globalVarNode.getName());
     }
 
     public void compileHash(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         HashNode hashNode = (HashNode) node;
 
         if (hashNode.getListNode() == null || hashNode.getListNode().size() == 0) {
             context.createEmptyHash();
             return;
         }
 
         ArrayCallback hashCallback = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray,
                             int index) {
                         ListNode listNode = (ListNode) sourceArray;
                         int keyIndex = index * 2;
                         compile(listNode.get(keyIndex), context);
                         compile(listNode.get(keyIndex + 1), context);
                     }
                 };
 
         context.createNewHash(hashNode.getListNode(), hashCallback, hashNode.getListNode().size() / 2);
     }
 
     public void compileIf(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final IfNode ifNode = (IfNode) node;
 
         compile(ifNode.getCondition(), context);
 
         BranchCallback trueCallback = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (ifNode.getThenBody() != null) {
                             compile(ifNode.getThenBody(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         BranchCallback falseCallback = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (ifNode.getElseBody() != null) {
                             compile(ifNode.getElseBody(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         context.performBooleanBranch(trueCallback, falseCallback);
     }
 
     public void compileInstAsgn(Node node, MethodCompiler context) {
         InstAsgnNode instAsgnNode = (InstAsgnNode) node;
 
         compile(instAsgnNode.getValueNode(), context);
 
         compileInstAsgnAssignment(node, context);
     }
 
     public void compileInstAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         InstAsgnNode instAsgnNode = (InstAsgnNode) node;
         context.assignInstanceVariable(instAsgnNode.getName());
     }
 
     public void compileInstVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         InstVarNode instVarNode = (InstVarNode) node;
 
         context.retrieveInstanceVariable(instVarNode.getName());
     }
 
     public void compileIter(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final IterNode iterNode = (IterNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         if (iterNode.getBodyNode() != null) {
                             compile(iterNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         // create the closure class and instantiate it
         final CompilerCallback closureArgs = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         if (iterNode.getVarNode() != null) {
                             compileAssignment(iterNode.getVarNode(), context);
                         }
                     }
                 };
 
         boolean hasMultipleArgsHead = false;
         if (iterNode.getVarNode() instanceof MultipleAsgnNode) {
             hasMultipleArgsHead = ((MultipleAsgnNode) iterNode.getVarNode()).getHeadNode() != null;
         }
 
-        NodeType argsNodeId = null;
-        if (iterNode.getVarNode() != null && iterNode.getVarNode().nodeId != NodeType.ZEROARGNODE) {
-            // if we have multiple asgn with just *args, need a special type for that
-            argsNodeId = iterNode.getVarNode().nodeId;
-            if (argsNodeId == NodeType.MULTIPLEASGNNODE) {
-                MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode)iterNode.getVarNode();
-                if (multipleAsgnNode.getHeadNode() == null && multipleAsgnNode.getArgsNode() != null) {
-                    // FIXME: This is gross. Don't do this.
-                    argsNodeId = NodeType.SVALUENODE;
-                }
-            }
-        }
+        NodeType argsNodeId = BlockBody.getArgumentTypeWackyHack(iterNode);
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(iterNode.getBodyNode());
         inspector.inspect(iterNode.getVarNode());
         if (argsNodeId == null) {
             // no args, do not pass args processor
             context.createNewClosure(iterNode.getScope(), Arity.procArityOf(iterNode.getVarNode()).getValue(),
                     closureBody, null, hasMultipleArgsHead, argsNodeId, inspector);
         } else {
             context.createNewClosure(iterNode.getScope(), Arity.procArityOf(iterNode.getVarNode()).getValue(),
                     closureBody, closureArgs, hasMultipleArgsHead, argsNodeId, inspector);
         }
     }
 
     public void compileLocalAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         LocalAsgnNode localAsgnNode = (LocalAsgnNode) node;
 
         compile(localAsgnNode.getValueNode(), context);
 
         context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth());
     }
 
     public void compileLocalAsgnAssignment(Node node, MethodCompiler context) {
         // "assignment" means the value is already on the stack
         context.lineNumber(node.getPosition());
 
         LocalAsgnNode localAsgnNode = (LocalAsgnNode) node;
 
         context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth());
     }
 
     public void compileLocalVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         LocalVarNode localVarNode = (LocalVarNode) node;
 
         context.getVariableCompiler().retrieveLocalVariable(localVarNode.getIndex(), localVarNode.getDepth());
     }
 
     public void compileMatch(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         MatchNode matchNode = (MatchNode) node;
 
         compile(matchNode.getRegexpNode(), context);
 
         context.match();
     }
 
     public void compileMatch2(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         Match2Node matchNode = (Match2Node) node;
 
         compile(matchNode.getReceiverNode(), context);
         compile(matchNode.getValueNode(), context);
 
         context.match2();
     }
 
     public void compileMatch3(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         Match3Node matchNode = (Match3Node) node;
 
         compile(matchNode.getReceiverNode(), context);
         compile(matchNode.getValueNode(), context);
 
         context.match3();
     }
 
     public void compileModule(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final ModuleNode moduleNode = (ModuleNode) node;
 
         final Node cpathNode = moduleNode.getCPath();
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         if (moduleNode.getBodyNode() != null) {
                             compile(moduleNode.getBodyNode(), context);
                         }
                         context.loadNil();
                     }
                 };
 
         CompilerCallback pathCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         if (cpathNode instanceof Colon2Node) {
                             Node leftNode = ((Colon2Node) cpathNode).getLeftNode();
                             if (leftNode != null) {
                                 compile(leftNode, context);
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
 
         context.defineModule(moduleNode.getCPath().getName(), moduleNode.getScope(), pathCallback, bodyCallback);
     }
 
     public void compileMultipleAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode) node;
 
         // FIXME: This is a little less efficient than it could be, since in the interpreter we avoid objectspace for these arrays
         compile(multipleAsgnNode.getValueNode(), context);
 
         compileMultipleAsgnAssignment(node, context);
     }
 
     public void compileMultipleAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode) node;
 
         context.ensureMultipleAssignableRubyArray(multipleAsgnNode.getHeadNode() != null);
 
         // normal items at the "head" of the masgn
         ArrayCallback headAssignCallback = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray,
                             int index) {
                         ListNode headNode = (ListNode) sourceArray;
                         Node assignNode = headNode.get(index);
 
                         // perform assignment for the next node
                         compileAssignment(assignNode, context);
                     }
                 };
 
         // head items for which we've run out of assignable elements
         ArrayCallback headNilCallback = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray,
                             int index) {
                         ListNode headNode = (ListNode) sourceArray;
                         Node assignNode = headNode.get(index);
 
                         // perform assignment for the next node
                         context.loadNil();
                         compileAssignment(assignNode, context);
                     }
                 };
 
         CompilerCallback argsCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         Node argsNode = multipleAsgnNode.getArgsNode();
                         if (argsNode instanceof StarNode) {
                         // done processing args
                         } else {
                             // assign to appropriate variable
                             compileAssignment(argsNode, context);
                         }
                     }
                 };
 
         if (multipleAsgnNode.getHeadNode() == null) {
             if (multipleAsgnNode.getArgsNode() == null) {
                 throw new NotCompilableException("Something's wrong, multiple assignment with no head or args at: " + multipleAsgnNode.getPosition());
             } else {
                 context.forEachInValueArray(0, 0, null, null, null, argsCallback);
             }
         } else {
             if (multipleAsgnNode.getArgsNode() == null) {
                 context.forEachInValueArray(0, multipleAsgnNode.getHeadNode().size(), multipleAsgnNode.getHeadNode(), headAssignCallback, headNilCallback, null);
             } else {
                 context.forEachInValueArray(0, multipleAsgnNode.getHeadNode().size(), multipleAsgnNode.getHeadNode(), headAssignCallback, headNilCallback, argsCallback);
             }
         }
     }
 
     public void compileNewline(Node node, MethodCompiler context) {
         // TODO: add trace call?
         context.lineNumber(node.getPosition());
 
         context.setPosition(node.getPosition());
 
         NewlineNode newlineNode = (NewlineNode) node;
 
         compile(newlineNode.getNextNode(), context);
     }
 
     public void compileNext(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final NextNode nextNode = (NextNode) node;
 
         CompilerCallback valueCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         if (nextNode.getValueNode() != null) {
                             compile(nextNode.getValueNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         context.pollThreadEvents();
         context.issueNextEvent(valueCallback);
     }
 
     public void compileNthRef(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         NthRefNode nthRefNode = (NthRefNode) node;
 
         context.nthRef(nthRefNode.getMatchNumber());
     }
 
     public void compileNil(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         context.loadNil();
 
         context.pollThreadEvents();
     }
 
     public void compileNot(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         NotNode notNode = (NotNode) node;
 
         compile(notNode.getConditionNode(), context);
 
         context.negateCurrentValue();
     }
 
     public void compileOpAsgnAnd(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final BinaryOperatorNode andNode = (BinaryOperatorNode) node;
 
         compile(andNode.getFirstNode(), context);
 
         BranchCallback longCallback = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         compile(andNode.getSecondNode(), context);
                     }
                 };
 
         context.performLogicalAnd(longCallback);
         context.pollThreadEvents();
     }
 
     public void compileOpAsgnOr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final OpAsgnOrNode orNode = (OpAsgnOrNode) node;
 
         compileGetDefinitionBase(orNode.getFirstNode(), context);
 
         context.isNull(new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         compile(orNode.getSecondNode(), context);
                     }
                 }, new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         compile(orNode.getFirstNode(), context);
                         context.duplicateCurrentValue();
                         context.performBooleanBranch(new BranchCallback() {
 
                                     public void branch(MethodCompiler context) {
                                     //Do nothing
                                     }
                                 },
                                 new BranchCallback() {
 
                                     public void branch(MethodCompiler context) {
                                         context.consumeCurrentValue();
                                         compile(orNode.getSecondNode(), context);
                                     }
                                 });
                     }
                 });
 
         context.pollThreadEvents();
     }
 
     public void compileOpAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         // FIXME: This is a little more complicated than it needs to be; do we see now why closures would be nice in Java?
 
         final OpAsgnNode opAsgnNode = (OpAsgnNode) node;
 
         final CompilerCallback receiverCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         compile(opAsgnNode.getReceiverNode(), context); // [recv]
                         context.duplicateCurrentValue(); // [recv, recv]
                     }
                 };
 
         BranchCallback doneBranch = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         // get rid of extra receiver, leave the variable result present
                         context.swapValues();
                         context.consumeCurrentValue();
                     }
                 };
 
         // Just evaluate the value and stuff it in an argument array
         final ArrayCallback justEvalValue = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray,
                             int index) {
                         compile(((Node[]) sourceArray)[index], context);
                     }
                 };
 
         BranchCallback assignBranch = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         // eliminate extra value, eval new one and assign
                         context.consumeCurrentValue();
                         context.createObjectArray(new Node[]{opAsgnNode.getValueNode()}, justEvalValue);
                         context.getInvocationCompiler().invokeAttrAssign(opAsgnNode.getVariableNameAsgn());
                     }
                 };
 
         CompilerCallback receiver2Callback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         context.getInvocationCompiler().invokeDynamic(opAsgnNode.getVariableName(), receiverCallback, null, CallType.FUNCTIONAL, null, false);
                     }
                 };
 
         if (opAsgnNode.getOperatorName() == "||") {
             // if lhs is true, don't eval rhs and assign
             receiver2Callback.call(context);
             context.duplicateCurrentValue();
             context.performBooleanBranch(doneBranch, assignBranch);
         } else if (opAsgnNode.getOperatorName() == "&&") {
             // if lhs is true, eval rhs and assign
             receiver2Callback.call(context);
             context.duplicateCurrentValue();
             context.performBooleanBranch(assignBranch, doneBranch);
         } else {
             // eval new value, call operator on old value, and assign
             CompilerCallback argsCallback = new CompilerCallback() {
 
                         public void call(MethodCompiler context) {
                             context.createObjectArray(new Node[]{opAsgnNode.getValueNode()}, justEvalValue);
                         }
                     };
             context.getInvocationCompiler().invokeDynamic(opAsgnNode.getOperatorName(), receiver2Callback, argsCallback, CallType.FUNCTIONAL, null, false);
             context.createObjectArray(1);
             context.getInvocationCompiler().invokeAttrAssign(opAsgnNode.getVariableNameAsgn());
         }
 
         context.pollThreadEvents();
     }
 
     public void compileOpElementAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode) node;
 
         compile(opElementAsgnNode.getReceiverNode(), context);
         compileArguments(opElementAsgnNode.getArgsNode(), context);
 
         CompilerCallback valueArgsCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         compile(opElementAsgnNode.getValueNode(), context);
                     }
                 };
 
         context.getInvocationCompiler().opElementAsgn(valueArgsCallback, opElementAsgnNode.getOperatorName());
     }
 
     public void compileOr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final OrNode orNode = (OrNode) node;
 
         compile(orNode.getFirstNode(), context);
 
         BranchCallback longCallback = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         compile(orNode.getSecondNode(), context);
                     }
                 };
 
         context.performLogicalOr(longCallback);
     }
 
     public void compilePostExe(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final PostExeNode postExeNode = (PostExeNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         if (postExeNode.getBodyNode() != null) {
                             compile(postExeNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
         context.createNewEndBlock(closureBody);
     }
 
     public void compilePreExe(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final PreExeNode preExeNode = (PreExeNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         if (preExeNode.getBodyNode() != null) {
                             compile(preExeNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
         context.runBeginBlock(preExeNode.getScope(), closureBody);
     }
 
     public void compileRedo(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         //RedoNode redoNode = (RedoNode)node;
 
         context.issueRedoEvent();
     }
 
     public void compileRegexp(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         RegexpNode reNode = (RegexpNode) node;
 
         context.createNewRegexp(reNode.getValue(), reNode.getOptions());
     }
 
     public void compileRescue(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final RescueNode rescueNode = (RescueNode) node;
 
         BranchCallback body = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (rescueNode.getBodyNode() != null) {
                             compile(rescueNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
 
                         if (rescueNode.getElseNode() != null) {
                             context.consumeCurrentValue();
                             compile(rescueNode.getElseNode(), context);
                         }
                     }
                 };
 
         BranchCallback handler = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         context.loadException();
                         context.unwrapRaiseException();
                         context.duplicateCurrentValue();
                         context.assignGlobalVariable("$!");
                         context.consumeCurrentValue();
                         context.rethrowIfSystemExit();
                         compileRescueBody(rescueNode.getRescueNode(), context);
                     }
                 };
 
         context.rescue(body, RaiseException.class, handler, IRubyObject.class);
     }
 
     public void compileRescueBody(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final RescueBodyNode rescueBodyNode = (RescueBodyNode) node;
 
         context.loadException();
         context.unwrapRaiseException();
 
         Node exceptionList = rescueBodyNode.getExceptionNodes();
         if (exceptionList == null) {
             context.loadClass("StandardError");
             context.createObjectArray(1);
         } else {
             compileArguments(exceptionList, context);
         }
 
         context.checkIsExceptionHandled();
 
         BranchCallback trueBranch = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (rescueBodyNode.getBodyNode() != null) {
                             compile(rescueBodyNode.getBodyNode(), context);
                             context.loadNil();
                             // FIXME: this should reset to what it was before
                             context.assignGlobalVariable("$!");
                             context.consumeCurrentValue();
                         } else {
                             context.loadNil();
                             // FIXME: this should reset to what it was before
                             context.assignGlobalVariable("$!");
                         }
                     }
                 };
 
         BranchCallback falseBranch = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (rescueBodyNode.getOptRescueNode() != null) {
                             compileRescueBody(rescueBodyNode.getOptRescueNode(), context);
                         } else {
                             context.rethrowException();
                         }
                     }
                 };
 
         context.performBooleanBranch(trueBranch, falseBranch);
     }
 
     public void compileRetry(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         context.pollThreadEvents();
 
         context.issueRetryEvent();
     }
 
     public void compileReturn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ReturnNode returnNode = (ReturnNode) node;
 
         if (returnNode.getValueNode() != null) {
             compile(returnNode.getValueNode(), context);
         } else {
             context.loadNil();
         }
 
         context.performReturn();
     }
 
     public void compileRoot(Node node, ScriptCompiler context, ASTInspector inspector) {
         RootNode rootNode = (RootNode) node;
 
         context.startScript(rootNode.getStaticScope());
 
         // create method for toplevel of script
         MethodCompiler methodCompiler = context.startMethod("__file__", null, rootNode.getStaticScope(), inspector);
 
         Node nextNode = rootNode.getBodyNode();
         if (nextNode != null) {
             if (nextNode.nodeId == NodeType.BLOCKNODE) {
                 // it's a multiple-statement body, iterate over all elements in turn and chain if it get too long
                 BlockNode blockNode = (BlockNode) nextNode;
 
                 for (int i = 0; i < blockNode.size(); i++) {
                     if ((i + 1) % 500 == 0) {
                         methodCompiler = methodCompiler.chainToMethod("__file__from_line_" + (i + 1), inspector);
                     }
                     compile(blockNode.get(i), methodCompiler);
 
                     if (i + 1 < blockNode.size()) {
                         // clear result from previous line
                         methodCompiler.consumeCurrentValue();
                     }
                 }
             } else {
                 // single-statement body, just compile it
                 compile(nextNode, methodCompiler);
             }
         } else {
             methodCompiler.loadNil();
         }
 
         methodCompiler.endMethod();
 
         context.endScript();
     }
 
     public void compileSelf(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         context.retrieveSelf();
     }
 
     public void compileSplat(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         SplatNode splatNode = (SplatNode) node;
 
         compile(splatNode.getValue(), context);
 
         context.splatCurrentValue();
     }
 
     public void compileStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         StrNode strNode = (StrNode) node;
 
         context.createNewString(strNode.getValue());
     }
 
     public void compileSuper(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final SuperNode superNode = (SuperNode) node;
 
         CompilerCallback argsCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
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
     }
 
     public void compileSValue(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         SValueNode svalueNode = (SValueNode) node;
 
         compile(svalueNode.getValue(), context);
 
         context.singlifySplattedValue();
     }
 
     public void compileSymbol(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         context.createNewSymbol(((SymbolNode) node).getName());
     }    
     
     public void compileToAry(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ToAryNode toAryNode = (ToAryNode) node;
 
         compile(toAryNode.getValue(), context);
 
         context.aryToAry();
     }
 
     public void compileTrue(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         context.loadTrue();
 
         context.pollThreadEvents();
     }
 
     public void compileUndef(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         context.undefMethod(((UndefNode) node).getName());
     }
 
     public void compileUntil(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final UntilNode untilNode = (UntilNode) node;
 
         BranchCallback condition = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         compile(untilNode.getConditionNode(), context);
                         context.negateCurrentValue();
                     }
                 };
 
         BranchCallback body = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (untilNode.getBodyNode() == null) {
                             context.loadNil();
                             return;
                         }
                         compile(untilNode.getBodyNode(), context);
                     }
                 };
 
         context.performBooleanLoop(condition, body, untilNode.evaluateAtStart());
 
         context.pollThreadEvents();
     }
 
     public void compileVAlias(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         VAliasNode valiasNode = (VAliasNode) node;
 
         context.aliasGlobal(valiasNode.getNewName(), valiasNode.getOldName());
     }
 
     public void compileVCall(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         VCallNode vcallNode = (VCallNode) node;
 
         context.getInvocationCompiler().invokeDynamic(vcallNode.getName(), null, null, CallType.VARIABLE, null, false);
     }
 
     public void compileWhile(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final WhileNode whileNode = (WhileNode) node;
 
         BranchCallback condition = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         compile(whileNode.getConditionNode(), context);
                     }
                 };
 
         BranchCallback body = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (whileNode.getBodyNode() == null) {
                             context.loadNil();
                         } else {
                             compile(whileNode.getBodyNode(), context);
                         }
                     }
                 };
 
         context.performBooleanLoop(condition, body, whileNode.evaluateAtStart());
 
         context.pollThreadEvents();
     }
 
     public void compileXStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final XStrNode xstrNode = (XStrNode) node;
 
         CompilerCallback argsCallback = new CompilerCallback() {
 
                     public void call(MethodCompiler context) {
                         context.createNewString(xstrNode.getValue());
                         context.createObjectArray(1);
                     }
                 };
         context.getInvocationCompiler().invokeDynamic("`", null, argsCallback, CallType.FUNCTIONAL, null, false);
     }
 
     public void compileYield(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         YieldNode yieldNode = (YieldNode) node;
 
         if (yieldNode.getArgsNode() != null) {
             compile(yieldNode.getArgsNode(), context);
         }
 
         context.getInvocationCompiler().yield(yieldNode.getArgsNode() != null, yieldNode.getCheckState());
     }
 
     public void compileZArray(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         context.createEmptyArray();
     }
 
     public void compileZSuper(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ZSuperNode zsuperNode = (ZSuperNode) node;
 
         CompilerCallback closure = getBlock(zsuperNode.getIterNode());
 
         context.callZSuper(closure);
     }
 
     public void compileArgsCatArguments(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ArgsCatNode argsCatNode = (ArgsCatNode) node;
 
         compileArguments(argsCatNode.getFirstNode(), context);
         // arguments compilers always create IRubyObject[], but we want to use RubyArray.concat here;
         // FIXME: as a result, this is NOT efficient, since it creates and then later unwraps an array
         context.createNewArray(true);
         compile(argsCatNode.getSecondNode(), context);
         context.splatCurrentValue();
         context.concatArrays();
         context.convertToJavaArray();
     }
 
     public void compileArgsPushArguments(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ArgsPushNode argsPushNode = (ArgsPushNode) node;
         compile(argsPushNode.getFirstNode(), context);
         compile(argsPushNode.getSecondNode(), context);
         context.appendToArray();
         context.convertToJavaArray();
     }
 
     public void compileArrayArguments(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ArrayNode arrayNode = (ArrayNode) node;
 
         ArrayCallback callback = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray, int index) {
                         Node node = (Node) ((Object[]) sourceArray)[index];
                         compile(node, context);
                     }
                 };
 
         context.setPosition(arrayNode.getPosition());
         context.createObjectArray(arrayNode.childNodes().toArray(), callback);
     // leave as a normal array
     }
 
     public void compileSplatArguments(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         SplatNode splatNode = (SplatNode) node;
 
         compile(splatNode.getValue(), context);
         context.splatCurrentValue();
         context.convertToJavaArray();
     }
 
     /**
      * Check whether the target node can safely be compiled.
      * 
      * @param node 
      */
     public static void confirmNodeIsSafe(Node node) {
         switch (node.nodeId) {
             case ARGSNODE:
                 ArgsNode argsNode = (ArgsNode) node;
                 // FIXME: We can't compile cases like def(a=(b=1)) because the variables
             // in the arg list get ordered differently than you might expect (b comes first)
             // So the code below searches through all opt args, ensuring none of them skip
             // indicies. A skipped index means there's a hidden local var/arg like b above
             // and so we shouldn't try to compile.
                 if (argsNode.getOptArgs() != null && argsNode.getOptArgs().size() > 0) {
                     int index = argsNode.getRequiredArgsCount() - 1;
 
                     for (int i = 0; i < argsNode.getOptArgs().size(); i++) {
                         int newIndex = ((LocalAsgnNode) argsNode.getOptArgs().get(i)).getIndex();
 
                         if (newIndex - index != 1) {
                             throw new NotCompilableException("Can't compile def with optional args that assign other variables at: " + node.getPosition());
                         }
                         index = newIndex;
                     }
                 }
                 break;
         }
     }
 }
diff --git a/src/org/jruby/compiler/impl/StandardASMCompiler.java b/src/org/jruby/compiler/impl/StandardASMCompiler.java
index 3ccc52db20..f6371a7fe8 100644
--- a/src/org/jruby/compiler/impl/StandardASMCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardASMCompiler.java
@@ -1,2062 +1,2063 @@
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
  * Copyright (C) 2006 Charles O Nutter <headius@headius.com>
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
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.math.BigInteger;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyRange;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.executable.Script;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.ArrayCallback;
 import org.jruby.compiler.BranchCallback;
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.compiler.InvocationCompiler;
 import org.jruby.compiler.MethodCompiler;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.ScriptCompiler;
 import org.jruby.compiler.VariableCompiler;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.ReOptions;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
+import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.InstanceVariables;
 import org.jruby.util.ByteList;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.JavaNameMangler;
 import org.objectweb.asm.ClassReader;
 import org.objectweb.asm.ClassVisitor;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.Opcodes;
 import org.objectweb.asm.util.CheckClassAdapter;
 
 /**
  *
  * @author headius
  */
 public class StandardASMCompiler implements ScriptCompiler, Opcodes {
     private static final CodegenUtils cg = CodegenUtils.cg;
     
     private static final String THREADCONTEXT = cg.p(ThreadContext.class);
     private static final String RUBY = cg.p(Ruby.class);
     private static final String IRUBYOBJECT = cg.p(IRubyObject.class);
 
     private static final String METHOD_SIGNATURE = cg.sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class});
     private static final String CLOSURE_SIGNATURE = cg.sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class});
 
     private static final int THIS = 0;
     private static final int THREADCONTEXT_INDEX = 1;
     private static final int SELF_INDEX = 2;
     private static final int ARGS_INDEX = 3;
     private static final int CLOSURE_INDEX = 4;
     private static final int DYNAMIC_SCOPE_INDEX = 5;
     private static final int RUNTIME_INDEX = 6;
     private static final int VARS_ARRAY_INDEX = 7;
     private static final int NIL_INDEX = 8;
     private static final int EXCEPTION_INDEX = 9;
     private static final int PREVIOUS_EXCEPTION_INDEX = 10;
     
     private String classname;
     private String sourcename;
 
     private ClassWriter classWriter;
     private SkinnyMethodAdapter initMethod;
     private SkinnyMethodAdapter clinitMethod;
     int methodIndex = -1;
     int innerIndex = -1;
     int fieldIndex = 0;
     int rescueNumber = 1;
     int ensureNumber = 1;
     StaticScope topLevelScope;
     
     Map<String, String> sourcePositions = new HashMap<String, String>();
     Map<String, String> byteLists = new HashMap<String, String>();
     Map<String, String> symbols = new HashMap<String, String>();
     
     /** Creates a new instance of StandardCompilerContext */
     public StandardASMCompiler(String classname, String sourcename) {
         this.classname = classname;
         this.sourcename = sourcename;
     }
 
     public byte[] getClassByteArray() {
         return classWriter.toByteArray();
     }
 
     public Class<?> loadClass(JRubyClassLoader classLoader) throws ClassNotFoundException {
         classLoader.defineClass(cg.c(classname), classWriter.toByteArray());
         return classLoader.loadClass(cg.c(classname));
     }
 
     public void writeClass(File destination) throws IOException {
         writeClass(classname, destination, classWriter);
     }
 
     private void writeClass(String classname, File destination, ClassWriter writer) throws IOException {
         String fullname = classname + ".class";
         String filename = null;
         String path = null;
         
         // verify the class
         byte[] bytecode = writer.toByteArray();
         CheckClassAdapter.verify(new ClassReader(bytecode), false, new PrintWriter(System.err));
         
         if (fullname.lastIndexOf("/") == -1) {
             filename = fullname;
             path = "";
         } else {
             filename = fullname.substring(fullname.lastIndexOf("/") + 1);
             path = fullname.substring(0, fullname.lastIndexOf("/"));
         }
         // create dir if necessary
         File pathfile = new File(destination, path);
         pathfile.mkdirs();
 
         FileOutputStream out = new FileOutputStream(new File(pathfile, filename));
 
         out.write(bytecode);
     }
 
     public String getClassname() {
         return classname;
     }
 
     public String getSourcename() {
         return sourcename;
     }
 
     public ClassVisitor getClassVisitor() {
         return classWriter;
     }
 
     public void startScript(StaticScope scope) {
         classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
 
         // Create the class with the appropriate class name and source file
         classWriter.visit(V1_4, ACC_PUBLIC + ACC_SUPER, classname, null, cg.p(Object.class), new String[]{cg.p(Script.class)});
         classWriter.visitSource(sourcename, null);
         
         topLevelScope = scope;
 
         beginInit();
         beginClassInit();
     }
 
     public void endScript() {
         // add Script#run impl, used for running this script with a specified threadcontext and self
         // root method of a script is always in __file__ method
         String methodName = "__file__";
         SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC, "run", METHOD_SIGNATURE, null, null));
         method.start();
 
         // invoke __file__ with threadcontext, self, args (null), and block (null)
         method.aload(THIS);
         method.aload(THREADCONTEXT_INDEX);
         method.aload(SELF_INDEX);
         method.aload(ARGS_INDEX);
         method.aload(CLOSURE_INDEX);
 
         method.invokevirtual(classname, methodName, METHOD_SIGNATURE);
         method.areturn();
         
         method.end();
         
         // the load method is used for loading as a top-level script, and prepares appropriate scoping around the code
         method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC, "load", METHOD_SIGNATURE, null, null));
         method.start();
 
         // invoke __file__ with threadcontext, self, args (null), and block (null)
         Label tryBegin = new Label();
         Label tryFinally = new Label();
         
         method.label(tryBegin);
         method.aload(THREADCONTEXT_INDEX);
         buildStaticScopeNames(method, topLevelScope);
         method.invokestatic(cg.p(RuntimeHelpers.class), "preLoad", cg.sig(void.class, ThreadContext.class, String[].class));
         
         method.aload(THIS);
         method.aload(THREADCONTEXT_INDEX);
         method.aload(SELF_INDEX);
         method.aload(ARGS_INDEX);
         method.aload(CLOSURE_INDEX);
 
         method.invokevirtual(classname, methodName, METHOD_SIGNATURE);
         method.aload(THREADCONTEXT_INDEX);
         method.invokestatic(cg.p(RuntimeHelpers.class), "postLoad", cg.sig(void.class, ThreadContext.class));
         method.areturn();
         
         method.label(tryFinally);
         method.aload(THREADCONTEXT_INDEX);
         method.invokestatic(cg.p(RuntimeHelpers.class), "postLoad", cg.sig(void.class, ThreadContext.class));
         method.athrow();
         
         method.trycatch(tryBegin, tryFinally, tryFinally, null);
         
         method.end();
 
         // add main impl, used for detached or command-line execution of this script with a new runtime
         // root method of a script is always in stub0, method0
         method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_STATIC, "main", cg.sig(Void.TYPE, cg.params(String[].class)), null, null));
         method.start();
 
         // new instance to invoke run against
         method.newobj(classname);
         method.dup();
         method.invokespecial(classname, "<init>", cg.sig(Void.TYPE));
         
         // instance config for the script run
         method.newobj(cg.p(RubyInstanceConfig.class));
         method.dup();
         method.invokespecial(cg.p(RubyInstanceConfig.class), "<init>", "()V");
         
         // set argv from main's args
         method.dup();
         method.aload(0);
         method.invokevirtual(cg.p(RubyInstanceConfig.class), "setArgv", cg.sig(void.class, String[].class));
 
         // invoke run with threadcontext and topself
         method.invokestatic(cg.p(Ruby.class), "newInstance", cg.sig(Ruby.class, RubyInstanceConfig.class));
         method.dup();
 
         method.invokevirtual(RUBY, "getCurrentContext", cg.sig(ThreadContext.class));
         method.swap();
         method.invokevirtual(RUBY, "getTopSelf", cg.sig(IRubyObject.class));
         method.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
         method.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
 
         method.invokevirtual(classname, "load", METHOD_SIGNATURE);
         method.voidreturn();
         method.end();
         
         endInit();
         endClassInit();
     }
 
     public void buildStaticScopeNames(SkinnyMethodAdapter method, StaticScope scope) {
         // construct static scope list of names
         method.ldc(new Integer(scope.getNumberOfVariables()));
         method.anewarray(cg.p(String.class));
         for (int i = 0; i < scope.getNumberOfVariables(); i++) {
             method.dup();
             method.ldc(new Integer(i));
             method.ldc(scope.getVariables()[i]);
             method.arraystore();
         }
     }
 
     private void beginInit() {
         ClassVisitor cv = getClassVisitor();
 
         initMethod = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC, "<init>", cg.sig(Void.TYPE), null, null));
         initMethod.start();
         initMethod.aload(THIS);
         initMethod.invokespecial(cg.p(Object.class), "<init>", cg.sig(Void.TYPE));
         
         cv.visitField(ACC_PRIVATE | ACC_FINAL, "$class", cg.ci(Class.class), null, null);
         
         // FIXME: this really ought to be in clinit, but it doesn't matter much
         initMethod.aload(THIS);
         initMethod.ldc(cg.c(classname));
         initMethod.invokestatic(cg.p(Class.class), "forName", cg.sig(Class.class, cg.params(String.class)));
         initMethod.putfield(classname, "$class", cg.ci(Class.class));
     }
 
     private void endInit() {
         initMethod.voidreturn();
         initMethod.end();
     }
 
     private void beginClassInit() {
         ClassVisitor cv = getClassVisitor();
 
         clinitMethod = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC | ACC_STATIC, "<clinit>", cg.sig(Void.TYPE), null, null));
         clinitMethod.start();
     }
 
     private void endClassInit() {
         clinitMethod.voidreturn();
         clinitMethod.end();
     }
     
     public MethodCompiler startMethod(String friendlyName, CompilerCallback args, StaticScope scope, ASTInspector inspector) {
         ASMMethodCompiler methodCompiler = new ASMMethodCompiler(friendlyName, inspector);
         
         methodCompiler.beginMethod(args, scope);
         
         return methodCompiler;
     }
 
     public abstract class AbstractMethodCompiler implements MethodCompiler {
         protected SkinnyMethodAdapter method;
         protected VariableCompiler variableCompiler;
         protected InvocationCompiler invocationCompiler;
         
         protected Label[] currentLoopLabels;
         protected Label scopeStart;
         protected Label scopeEnd;
         protected Label redoJump;
         protected boolean withinProtection = false;
         
         // The current local variable count, to use for temporary locals during processing
         protected int localVariable = PREVIOUS_EXCEPTION_INDEX + 1;
 
         public abstract void beginMethod(CompilerCallback args, StaticScope scope);
 
         public abstract void endMethod();
         
         public MethodCompiler chainToMethod(String methodName, ASTInspector inspector) {
             // chain to the next segment of this giant method
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.aload(ARGS_INDEX);
             if(this instanceof ASMClosureCompiler) {
                 pushNull();
             } else {
                 loadBlock();
             }
             method.invokevirtual(classname, methodName, cg.sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}));
             endMethod();
 
             ASMMethodCompiler methodCompiler = new ASMMethodCompiler(methodName, inspector);
 
             methodCompiler.beginChainedMethod();
 
             return methodCompiler;
         }
         
         public StandardASMCompiler getScriptCompiler() {
             return StandardASMCompiler.this;
         }
 
         public void lineNumber(ISourcePosition position) {
             Label line = new Label();
             method.label(line);
             method.visitLineNumber(position.getStartLine() + 1, line);
         }
 
         public void loadThreadContext() {
             method.aload(THREADCONTEXT_INDEX);
         }
 
         public void loadSelf() {
             method.aload(SELF_INDEX);
         }
 
         public void loadRuntime() {
             method.aload(RUNTIME_INDEX);
         }
 
         public void loadBlock() {
             method.aload(CLOSURE_INDEX);
         }
 
         public void loadNil() {
             method.aload(NIL_INDEX);
         }
         
         public void loadNull() {
             method.aconst_null();
         }
 
         public void loadSymbol(String symbol) {
             loadRuntime();
 
             method.ldc(symbol);
 
             invokeIRuby("newSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
         }
 
         public void loadObject() {
             loadRuntime();
 
             invokeIRuby("getObject", cg.sig(RubyClass.class, cg.params()));
         }
 
         /**
          * This is for utility methods used by the compiler, to reduce the amount of code generation
          * necessary.  All of these live in CompilerHelpers.
          */
         public void invokeUtilityMethod(String methodName, String signature) {
             method.invokestatic(cg.p(RuntimeHelpers.class), methodName, signature);
         }
 
         public void invokeThreadContext(String methodName, String signature) {
             method.invokevirtual(THREADCONTEXT, methodName, signature);
         }
 
         public void invokeIRuby(String methodName, String signature) {
             method.invokevirtual(RUBY, methodName, signature);
         }
 
         public void invokeIRubyObject(String methodName, String signature) {
             method.invokeinterface(IRUBYOBJECT, methodName, signature);
         }
 
         public void consumeCurrentValue() {
             method.pop();
         }
 
         public void duplicateCurrentValue() {
             method.dup();
         }
 
         public void swapValues() {
             method.swap();
         }
 
         public void retrieveSelf() {
             loadSelf();
         }
 
         public void retrieveSelfClass() {
             loadSelf();
             metaclass();
         }
         
         public VariableCompiler getVariableCompiler() {
             return variableCompiler;
         }
         
         public InvocationCompiler getInvocationCompiler() {
             return invocationCompiler;
         }
 
         public void assignConstantInCurrent(String name) {
             loadThreadContext();
             method.ldc(name);
             method.dup2_x1();
             method.pop2();
             invokeThreadContext("setConstantInCurrent", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
         }
 
         public void assignConstantInModule(String name) {
             method.ldc(name);
             loadThreadContext();
             invokeUtilityMethod("setConstantInModule", cg.sig(IRubyObject.class, IRubyObject.class, IRubyObject.class, String.class, ThreadContext.class));
         }
 
         public void assignConstantInObject(String name) {
             // load Object under value
             loadRuntime();
             invokeIRuby("getObject", cg.sig(RubyClass.class, cg.params()));
             method.swap();
 
             assignConstantInModule(name);
         }
 
         public void retrieveConstant(String name) {
             loadThreadContext();
             method.ldc(name);
             invokeThreadContext("getConstant", cg.sig(IRubyObject.class, cg.params(String.class)));
         }
 
         public void retrieveConstantFromModule(String name) {
             method.visitTypeInsn(CHECKCAST, cg.p(RubyModule.class));
             method.ldc(name);
             method.invokevirtual(cg.p(RubyModule.class), "fastGetConstantFrom", cg.sig(IRubyObject.class, cg.params(String.class)));
         }
 
         public void retrieveClassVariable(String name) {
             loadThreadContext();
             loadRuntime();
             loadSelf();
             method.ldc(name);
 
             invokeUtilityMethod("fastFetchClassVariable", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class)));
         }
 
         public void assignClassVariable(String name) {
             loadThreadContext();
             method.swap();
             loadRuntime();
             method.swap();
             loadSelf();
             method.swap();
             method.ldc(name);
             method.swap();
 
             invokeUtilityMethod("fastSetClassVariable", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
         }
 
         public void declareClassVariable(String name) {
             loadThreadContext();
             method.swap();
             loadRuntime();
             method.swap();
             loadSelf();
             method.swap();
             method.ldc(name);
             method.swap();
 
             invokeUtilityMethod("fastDeclareClassVariable", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
         }
 
         public void createNewFloat(double value) {
             loadRuntime();
             method.ldc(new Double(value));
 
             invokeIRuby("newFloat", cg.sig(RubyFloat.class, cg.params(Double.TYPE)));
         }
 
         public void createNewFixnum(long value) {
             loadRuntime();
             method.ldc(new Long(value));
 
             invokeIRuby("newFixnum", cg.sig(RubyFixnum.class, cg.params(Long.TYPE)));
         }
 
         public void createNewBignum(BigInteger value) {
             loadRuntime();
             method.ldc(value.toString());
 
             method.invokestatic(cg.p(RubyBignum.class), "newBignum", cg.sig(RubyBignum.class, cg.params(Ruby.class, String.class)));
         }
 
         public void createNewString(ArrayCallback callback, int count) {
             loadRuntime();
             invokeIRuby("newString", cg.sig(RubyString.class, cg.params()));
             for (int i = 0; i < count; i++) {
                 callback.nextValue(this, null, i);
                 method.invokevirtual(cg.p(RubyString.class), "append", cg.sig(RubyString.class, cg.params(IRubyObject.class)));
             }
         }
 
         public void createNewSymbol(ArrayCallback callback, int count) {
             loadRuntime();
             invokeIRuby("newString", cg.sig(RubyString.class, cg.params()));
             for (int i = 0; i < count; i++) {
                 callback.nextValue(this, null, i);
                 method.invokevirtual(cg.p(RubyString.class), "append", cg.sig(RubyString.class, cg.params(IRubyObject.class)));
             }
             toJavaString();
             loadRuntime();
             method.swap();
             invokeIRuby("newSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
         }
 
         public void createNewString(ByteList value) {
             // FIXME: this is sub-optimal, storing string value in a java.lang.String again
             String fieldName = cacheByteList(value.toString());
             loadRuntime();
             method.getstatic(classname, fieldName, cg.ci(ByteList.class));
 
             invokeIRuby("newStringShared", cg.sig(RubyString.class, cg.params(ByteList.class)));
         }
 
         public void createNewSymbol(String name) {
             method.aload(0);
             loadRuntime();
             String methodName = cacheSymbol(name);
             method.invokevirtual(classname, methodName, 
                     cg.sig(RubySymbol.class, cg.params(Ruby.class)));
         }
 
         public void createNewArray(boolean lightweight) {
             loadRuntime();
             // put under object array already present
             method.swap();
 
             if (lightweight) {
                 invokeIRuby("newArrayNoCopyLight", cg.sig(RubyArray.class, cg.params(IRubyObject[].class)));
             } else {
                 invokeIRuby("newArrayNoCopy", cg.sig(RubyArray.class, cg.params(IRubyObject[].class)));
             }
         }
 
         public void createEmptyArray() {
             loadRuntime();
 
             invokeIRuby("newArray", cg.sig(RubyArray.class, cg.params()));
         }
 
         public void createObjectArray(Object[] sourceArray, ArrayCallback callback) {
             buildObjectArray(IRUBYOBJECT, sourceArray, callback);
         }
 
         public void createObjectArray(int elementCount) {
             // if element count is less than 6, use helper methods
             if (elementCount < 6) {
                 Class[] params = new Class[elementCount];
                 Arrays.fill(params, IRubyObject.class);
                 invokeUtilityMethod("constructObjectArray", cg.sig(IRubyObject[].class, params));
             } else {
                 // This is pretty inefficient for building an array, so just raise an error if someone's using it for a lot of elements
                 throw new NotCompilableException("Don't use createObjectArray(int) for more than 5 elements");
             }
         }
 
         private void buildObjectArray(String type, Object[] sourceArray, ArrayCallback callback) {
             if (sourceArray.length == 0) {
                 method.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
             } else if (sourceArray.length <= RuntimeHelpers.MAX_SPECIFIC_ARITY_OBJECT_ARRAY) {
                 // if we have a specific-arity helper to construct an array for us, use that
                 for (int i = 0; i < sourceArray.length; i++) {
                     callback.nextValue(this, sourceArray, i);
                 }
                 invokeUtilityMethod("constructObjectArray", cg.sig(IRubyObject[].class, cg.params(IRubyObject.class, sourceArray.length)));
             } else {
                 // brute force construction inline
                 method.ldc(new Integer(sourceArray.length));
                 method.anewarray(type);
 
                 for (int i = 0; i < sourceArray.length; i++) {
                     method.dup();
                     method.ldc(new Integer(i));
 
                     callback.nextValue(this, sourceArray, i);
 
                     method.arraystore();
                 }
             }
         }
 
         public void createEmptyHash() {
             loadRuntime();
 
             method.invokestatic(cg.p(RubyHash.class), "newHash", cg.sig(RubyHash.class, cg.params(Ruby.class)));
         }
 
         public void createNewHash(Object elements, ArrayCallback callback, int keyCount) {
             loadRuntime();
             
             if (keyCount <= RuntimeHelpers.MAX_SPECIFIC_ARITY_HASH) {
                 // we have a specific-arity method we can use to construct, so use that
                 for (int i = 0; i < keyCount; i++) {
                     callback.nextValue(this, elements, i);
                 }
                 
                 invokeUtilityMethod("constructHash", cg.sig(RubyHash.class, cg.params(Ruby.class, IRubyObject.class, keyCount * 2)));
             } else {
                 method.invokestatic(cg.p(RubyHash.class), "newHash", cg.sig(RubyHash.class, cg.params(Ruby.class)));
 
                 for (int i = 0; i < keyCount; i++) {
                     method.dup();
                     callback.nextValue(this, elements, i);
                     method.invokevirtual(cg.p(RubyHash.class), "fastASet", cg.sig(void.class, cg.params(IRubyObject.class, IRubyObject.class)));
                 }
             }
         }
 
         public void createNewRange(boolean isExclusive) {
             loadRuntime();
 
             // could be more efficient with a callback
             method.dup_x2();
             method.pop();
 
             method.ldc(new Boolean(isExclusive));
 
             method.invokestatic(cg.p(RubyRange.class), "newRange", cg.sig(RubyRange.class, cg.params(Ruby.class, IRubyObject.class, IRubyObject.class, Boolean.TYPE)));
         }
 
         /**
          * Invoke IRubyObject.isTrue
          */
         private void isTrue() {
             invokeIRubyObject("isTrue", cg.sig(Boolean.TYPE));
         }
 
         public void performBooleanBranch(BranchCallback trueBranch, BranchCallback falseBranch) {
             Label afterJmp = new Label();
             Label falseJmp = new Label();
 
             // call isTrue on the result
             isTrue();
 
             method.ifeq(falseJmp); // EQ == 0 (i.e. false)
             trueBranch.branch(this);
             method.go_to(afterJmp);
 
             // FIXME: optimize for cases where we have no false branch
             method.label(falseJmp);
             falseBranch.branch(this);
 
             method.label(afterJmp);
         }
 
         public void performLogicalAnd(BranchCallback longBranch) {
             Label falseJmp = new Label();
 
             // dup it since we need to return appropriately if it's false
             method.dup();
 
             // call isTrue on the result
             isTrue();
 
             method.ifeq(falseJmp); // EQ == 0 (i.e. false)
             // pop the extra result and replace with the send part of the AND
             method.pop();
             longBranch.branch(this);
             method.label(falseJmp);
         }
 
         public void performLogicalOr(BranchCallback longBranch) {
             // FIXME: after jump is not in here.  Will if ever be?
             //Label afterJmp = new Label();
             Label falseJmp = new Label();
 
             // dup it since we need to return appropriately if it's false
             method.dup();
 
             // call isTrue on the result
             isTrue();
 
             method.ifne(falseJmp); // EQ == 0 (i.e. false)
             // pop the extra result and replace with the send part of the AND
             method.pop();
             longBranch.branch(this);
             method.label(falseJmp);
         }
 
         public void performBooleanLoop(BranchCallback condition, BranchCallback body, boolean checkFirst) {
             // FIXME: handle next/continue, break, etc
             Label tryBegin = new Label();
             Label tryEnd = new Label();
             Label catchRedo = new Label();
             Label catchNext = new Label();
             Label catchBreak = new Label();
             Label catchRaised = new Label();
             Label endOfBody = new Label();
             Label conditionCheck = new Label();
             Label topOfBody = new Label();
             Label done = new Label();
             Label normalLoopEnd = new Label();
             method.trycatch(tryBegin, tryEnd, catchRedo, cg.p(JumpException.RedoJump.class));
             method.trycatch(tryBegin, tryEnd, catchNext, cg.p(JumpException.NextJump.class));
             method.trycatch(tryBegin, tryEnd, catchBreak, cg.p(JumpException.BreakJump.class));
             if (checkFirst) {
                 // only while loops seem to have this RaiseException magic
                 method.trycatch(tryBegin, tryEnd, catchRaised, cg.p(RaiseException.class));
             }
             
             method.label(tryBegin);
             {
                 
                 Label[] oldLoopLabels = currentLoopLabels;
                 
                 currentLoopLabels = new Label[] {endOfBody, topOfBody, done};
                 
                 // FIXME: if we terminate immediately, this appears to break while in method arguments
                 // we need to push a nil for the cases where we will never enter the body
                 if (checkFirst) {
                     method.go_to(conditionCheck);
                 }
 
                 method.label(topOfBody);
 
                 body.branch(this);
                 
                 method.label(endOfBody);
 
                 // clear body or next result after each successful loop
                 method.pop();
                 
                 method.label(conditionCheck);
                 
                 // check the condition
                 condition.branch(this);
                 isTrue();
                 method.ifne(topOfBody); // NE == nonzero (i.e. true)
                 
                 currentLoopLabels = oldLoopLabels;
             }
 
             method.label(tryEnd);
             // skip catch block
             method.go_to(normalLoopEnd);
 
             // catch logic for flow-control exceptions
             {
                 // redo jump
                 {
                     method.label(catchRedo);
                     method.pop();
                     method.go_to(topOfBody);
                 }
 
                 // next jump
                 {
                     method.label(catchNext);
                     method.pop();
                     // exceptionNext target is for a next that doesn't push a new value, like this one
                     method.go_to(conditionCheck);
                 }
 
                 // break jump
                 {
                     method.label(catchBreak);
                     loadBlock();
                     invokeUtilityMethod("breakJumpInWhile", cg.sig(IRubyObject.class, JumpException.BreakJump.class, Block.class));
                     method.go_to(done);
                 }
 
                 // FIXME: This generates a crapload of extra code that is frequently *never* needed
                 // raised exception
                 if (checkFirst) {
                     // only while loops seem to have this RaiseException magic
                     method.label(catchRaised);
                     Label raiseNext = new Label();
                     Label raiseRedo = new Label();
                     Label raiseRethrow = new Label();
                     method.dup();
                     invokeUtilityMethod("getLocalJumpTypeOrRethrow", cg.sig(String.class, cg.params(RaiseException.class)));
                     // if we get here we have a RaiseException we know is a local jump error and an error type
 
                     // is it break?
                     method.dup(); // dup string
                     method.ldc("break");
                     method.invokevirtual(cg.p(String.class), "equals", cg.sig(boolean.class, cg.params(Object.class)));
                     method.ifeq(raiseNext);
                     // pop the extra string, get the break value, and end the loop
                     method.pop();
                     invokeUtilityMethod("unwrapLocalJumpErrorValue", cg.sig(IRubyObject.class, cg.params(RaiseException.class)));
                     method.go_to(done);
 
                     // is it next?
                     method.label(raiseNext);
                     method.dup();
                     method.ldc("next");
                     method.invokevirtual(cg.p(String.class), "equals", cg.sig(boolean.class, cg.params(Object.class)));
                     method.ifeq(raiseRedo);
                     // pop the extra string and the exception, jump to the condition
                     method.pop2();
                     method.go_to(conditionCheck);
 
                     // is it redo?
                     method.label(raiseRedo);
                     method.dup();
                     method.ldc("redo");
                     method.invokevirtual(cg.p(String.class), "equals", cg.sig(boolean.class, cg.params(Object.class)));
                     method.ifeq(raiseRethrow);
                     // pop the extra string and the exception, jump to the condition
                     method.pop2();
                     method.go_to(topOfBody);
 
                     // just rethrow it
                     method.label(raiseRethrow);
                     method.pop(); // pop extra string
                     method.athrow();
                 }
             }
             
             method.label(normalLoopEnd);
             loadNil();
             method.label(done);
         }
 
         public void createNewClosure(
                 StaticScope scope,
                 int arity,
                 CompilerCallback body,
                 CompilerCallback args,
                 boolean hasMultipleArgsHead,
                 NodeType argsNodeId,
                 ASTInspector inspector) {
             String closureMethodName = "closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, inspector);
             
             closureCompiler.beginMethod(args, scope);
             
             body.call(closureCompiler);
             
             closureCompiler.endMethod();
 
             // Done with closure compilation
             /////////////////////////////////////////////////////////////////////////////
             // Now, store a compiled block object somewhere we can access it in the future
             // in current method, load the field to see if we've created a BlockCallback yet
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(cg.p(CallbackFactory.class), "getBlockCallback", cg.sig(CompiledBlockCallback.class, cg.params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
             method.ldc(new Integer(arity));
 
             buildStaticScopeNames(method, scope);
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             method.ldc(Boolean.valueOf(hasMultipleArgsHead));
-            method.ldc(Block.asArgumentType(argsNodeId));
+            method.ldc(BlockBody.asArgumentType(argsNodeId));
             // if there's a sub-closure or there's scope-aware methods, it can't be "light"
             method.ldc(!(inspector.hasClosure() || inspector.hasScopeAwareMethods()));
 
             invokeUtilityMethod("createBlock", cg.sig(Block.class,
                     cg.params(ThreadContext.class, IRubyObject.class, Integer.TYPE, String[].class, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE, boolean.class)));
         }
 
         public void runBeginBlock(StaticScope scope, CompilerCallback body) {
             String closureMethodName = "closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, null);
             
             closureCompiler.beginMethod(null, scope);
             
             body.call(closureCompiler);
             
             closureCompiler.endMethod();
 
             // Done with closure compilation
             /////////////////////////////////////////////////////////////////////////////
             // Now, store a compiled block object somewhere we can access it in the future
             // in current method, load the field to see if we've created a BlockCallback yet
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(cg.p(CallbackFactory.class), "getBlockCallback", cg.sig(CompiledBlockCallback.class, cg.params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
 
             buildStaticScopeNames(method, scope);
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
 
             invokeUtilityMethod("runBeginBlock", cg.sig(IRubyObject.class,
                     cg.params(ThreadContext.class, IRubyObject.class, String[].class, CompiledBlockCallback.class)));
         }
 
         public void createNewForLoop(int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId) {
             String closureMethodName = "closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, null);
             
             closureCompiler.beginMethod(args, null);
             
             body.call(closureCompiler);
             
             closureCompiler.endMethod();
 
             // Done with closure compilation
             /////////////////////////////////////////////////////////////////////////////
             // Now, store a compiled block object somewhere we can access it in the future
             // in current method, load the field to see if we've created a BlockCallback yet
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(cg.p(CallbackFactory.class), "getBlockCallback", cg.sig(CompiledBlockCallback.class, cg.params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
             method.ldc(new Integer(arity));
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             method.ldc(Boolean.valueOf(hasMultipleArgsHead));
-            method.ldc(Block.asArgumentType(argsNodeId));
+            method.ldc(BlockBody.asArgumentType(argsNodeId));
 
             invokeUtilityMethod("createSharedScopeBlock", cg.sig(Block.class,
                     cg.params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
         }
 
         public void createNewEndBlock(CompilerCallback body) {
             String closureMethodName = "END_closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, null);
             
             closureCompiler.beginMethod(null, null);
             
             body.call(closureCompiler);
             
             closureCompiler.endMethod();
 
             // Done with closure compilation
             /////////////////////////////////////////////////////////////////////////////
             // Now, store a compiled block object somewhere we can access it in the future
             // in current method, load the field to see if we've created a BlockCallback yet
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(cg.p(CallbackFactory.class), "getBlockCallback", cg.sig(CompiledBlockCallback.class, cg.params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
             method.ldc(new Integer(0));
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             method.ldc(false);
             method.ldc(Block.ZERO_ARGS);
 
             invokeUtilityMethod("createSharedScopeBlock", cg.sig(Block.class,
                     cg.params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
             
             loadRuntime();
             invokeUtilityMethod("registerEndBlock", cg.sig(void.class, Block.class, Ruby.class));
             loadNil();
         }
 
         private void getCallbackFactory() {
             // FIXME: Perhaps a bit extra code, but only for defn/s; examine
             loadRuntime();
             getCompiledClass();
             method.dup();
             method.invokevirtual(cg.p(Class.class), "getClassLoader", cg.sig(ClassLoader.class));
             method.invokestatic(cg.p(CallbackFactory.class), "createFactory", cg.sig(CallbackFactory.class, cg.params(Ruby.class, Class.class, ClassLoader.class)));
         }
 
         public void getCompiledClass() {
             method.aload(THIS);
             method.getfield(classname, "$class", cg.ci(Class.class));
         }
 
         private void getRubyClass() {
             loadThreadContext();
             invokeThreadContext("getRubyClass", cg.sig(RubyModule.class));
         }
 
         public void println() {
             method.dup();
             method.getstatic(cg.p(System.class), "out", cg.ci(PrintStream.class));
             method.swap();
 
             method.invokevirtual(cg.p(PrintStream.class), "println", cg.sig(Void.TYPE, cg.params(Object.class)));
         }
 
         public void defineAlias(String newName, String oldName) {
             loadThreadContext();
             method.ldc(newName);
             method.ldc(oldName);
             invokeUtilityMethod("defineAlias", cg.sig(IRubyObject.class, ThreadContext.class, String.class, String.class));
         }
 
         public void loadFalse() {
             // TODO: cache?
             loadRuntime();
             invokeIRuby("getFalse", cg.sig(RubyBoolean.class));
         }
 
         public void loadTrue() {
             // TODO: cache?
             loadRuntime();
             invokeIRuby("getTrue", cg.sig(RubyBoolean.class));
         }
 
         public void loadCurrentModule() {
             loadThreadContext();
             invokeThreadContext("getCurrentScope", cg.sig(DynamicScope.class));
             method.invokevirtual(cg.p(DynamicScope.class), "getStaticScope", cg.sig(StaticScope.class));
             method.invokevirtual(cg.p(StaticScope.class), "getModule", cg.sig(RubyModule.class));
         }
 
         public void retrieveInstanceVariable(String name) {
             loadRuntime();
             loadSelf();
             method.ldc(name);
             invokeUtilityMethod("fastGetInstanceVariable", cg.sig(IRubyObject.class, Ruby.class, IRubyObject.class, String.class));
         }
 
         public void assignInstanceVariable(String name) {
             // FIXME: more efficient with a callback
             loadSelf();
             invokeIRubyObject("getInstanceVariables", cg.sig(InstanceVariables.class));
             method.swap();
 
             method.ldc(name);
             method.swap();
 
             method.invokeinterface(cg.p(InstanceVariables.class), "fastSetInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
         }
 
         public void retrieveGlobalVariable(String name) {
             loadRuntime();
 
             invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
             method.ldc(name);
             method.invokevirtual(cg.p(GlobalVariables.class), "get", cg.sig(IRubyObject.class, cg.params(String.class)));
         }
 
         public void assignGlobalVariable(String name) {
             // FIXME: more efficient with a callback
             loadRuntime();
 
             invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
             method.swap();
             method.ldc(name);
             method.swap();
             method.invokevirtual(cg.p(GlobalVariables.class), "set", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
         }
 
         public void negateCurrentValue() {
             loadRuntime();
             invokeUtilityMethod("negate", cg.sig(IRubyObject.class, IRubyObject.class, Ruby.class));
         }
 
         public void splatCurrentValue() {
             loadRuntime();
             method.invokestatic(cg.p(ASTInterpreter.class), "splatValue", cg.sig(RubyArray.class, cg.params(IRubyObject.class, Ruby.class)));
         }
 
         public void singlifySplattedValue() {
             loadRuntime();
             method.invokestatic(cg.p(ASTInterpreter.class), "aValueSplat", cg.sig(IRubyObject.class, cg.params(IRubyObject.class, Ruby.class)));
         }
 
         public void aryToAry() {
             loadRuntime();
             method.invokestatic(cg.p(ASTInterpreter.class), "aryToAry", cg.sig(IRubyObject.class, cg.params(IRubyObject.class, Ruby.class)));
         }
 
         public void ensureRubyArray() {
             invokeUtilityMethod("ensureRubyArray", cg.sig(RubyArray.class, cg.params(IRubyObject.class)));
         }
 
         public void ensureMultipleAssignableRubyArray(boolean masgnHasHead) {
             loadRuntime();
             method.swap();
             method.ldc(new Boolean(masgnHasHead));
             invokeUtilityMethod("ensureMultipleAssignableRubyArray", cg.sig(RubyArray.class, cg.params(Ruby.class, IRubyObject.class, boolean.class)));
         }
 
         public void forEachInValueArray(int start, int count, Object source, ArrayCallback callback, ArrayCallback nilCallback, CompilerCallback argsCallback) {
             // FIXME: This could probably be made more efficient
             for (; start < count; start++) {
                 Label noMoreArrayElements = new Label();
                 Label doneWithElement = new Label();
                 
                 // confirm we're not past the end of the array
                 method.dup(); // dup the original array object
                 method.invokevirtual(cg.p(RubyArray.class), "getLength", cg.sig(Integer.TYPE));
                 method.ldc(new Integer(start));
                 method.if_icmple(noMoreArrayElements); // if length <= start, end loop
                 
                 // extract item from array
                 method.dup(); // dup the original array object
                 method.ldc(new Integer(start)); // index for the item
                 method.invokevirtual(cg.p(RubyArray.class), "entry", cg.sig(IRubyObject.class, cg.params(Integer.TYPE))); // extract item
                 callback.nextValue(this, source, start);
                 method.go_to(doneWithElement);
                 
                 // otherwise no items left available, use the code from nilCallback
                 method.label(noMoreArrayElements);
                 nilCallback.nextValue(this, source, start);
                 
                 // end of this element
                 method.label(doneWithElement);
                 // normal assignment leaves the value; pop it.
                 method.pop();
             }
             
             if (argsCallback != null) {
                 Label emptyArray = new Label();
                 Label readyForArgs = new Label();
                 // confirm we're not past the end of the array
                 method.dup(); // dup the original array object
                 method.invokevirtual(cg.p(RubyArray.class), "getLength", cg.sig(Integer.TYPE));
                 method.ldc(new Integer(start));
                 method.if_icmple(emptyArray); // if length <= start, end loop
                 
                 // assign remaining elements as an array for rest args
                 method.dup(); // dup the original array object
                 method.ldc(start);
                 invokeUtilityMethod("createSubarray", cg.sig(RubyArray.class, RubyArray.class, int.class));
                 method.go_to(readyForArgs);
                 
                 // create empty array
                 method.label(emptyArray);
                 createEmptyArray();
                 
                 // assign rest args
                 method.label(readyForArgs);
                 argsCallback.call(this);
                 //consume leftover assigned value
                 method.pop();
             }
         }
 
         public void asString() {
             method.invokeinterface(cg.p(IRubyObject.class), "asString", cg.sig(RubyString.class, cg.params()));
         }
         
         public void toJavaString() {
             method.invokevirtual(cg.p(Object.class), "toString", cg.sig(String.class));
         }
 
         public void nthRef(int match) {
             method.ldc(new Integer(match));
             backref();
             method.invokestatic(cg.p(RubyRegexp.class), "nth_match", cg.sig(IRubyObject.class, cg.params(Integer.TYPE, IRubyObject.class)));
         }
 
         public void match() {
             method.invokevirtual(cg.p(RubyRegexp.class), "op_match2", cg.sig(IRubyObject.class, cg.params()));
         }
 
         public void match2() {
             method.invokevirtual(cg.p(RubyRegexp.class), "op_match", cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));
         }
 
         public void match3() {
             loadThreadContext();
             invokeUtilityMethod("match3", cg.sig(IRubyObject.class, RubyRegexp.class, IRubyObject.class, ThreadContext.class));
         }
 
         public void createNewRegexp(final ByteList value, final int options) {
             String regexpField = getNewConstant(cg.ci(RubyRegexp.class), "lit_reg_");
 
             // in current method, load the field to see if we've created a Pattern yet
             method.aload(THIS);
             method.getfield(classname, regexpField, cg.ci(RubyRegexp.class));
 
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated); //[]
 
             // load string, for Regexp#source and Regexp#inspect
             String regexpString = value.toString();
 
             loadRuntime(); //[R]
             method.ldc(regexpString); //[R, rS]
             method.ldc(new Integer(options)); //[R, rS, opts]
 
             method.invokestatic(cg.p(RubyRegexp.class), "newRegexp", cg.sig(RubyRegexp.class, cg.params(Ruby.class, String.class, Integer.TYPE))); //[reg]
 
             method.aload(THIS); //[reg, T]
             method.swap(); //[T, reg]
             method.putfield(classname, regexpField, cg.ci(RubyRegexp.class)); //[]
             method.label(alreadyCreated);
             method.aload(THIS); //[T]
             method.getfield(classname, regexpField, cg.ci(RubyRegexp.class)); 
         }
 
         public void createNewRegexp(CompilerCallback createStringCallback, final int options) {
             boolean onceOnly = (options & ReOptions.RE_OPTION_ONCE) != 0;   // for regular expressions with the /o flag
             Label alreadyCreated = null;
             String regexpField = null;
 
             // only alter the code if the /o flag was present
             if (onceOnly) {
                 regexpField = getNewConstant(cg.ci(RubyRegexp.class), "lit_reg_");
     
                 // in current method, load the field to see if we've created a Pattern yet
                 method.aload(THIS);
                 method.getfield(classname, regexpField, cg.ci(RubyRegexp.class));
     
                 alreadyCreated = new Label();
                 method.ifnonnull(alreadyCreated);
             }
 
             loadRuntime();
 
             createStringCallback.call(this);
             method.ldc(new Integer(options));
 
             method.invokestatic(cg.p(RubyRegexp.class), "newRegexp", cg.sig(RubyRegexp.class, cg.params(Ruby.class, String.class, Integer.TYPE))); //[reg]
 
             // only alter the code if the /o flag was present
             if (onceOnly) {
                 method.aload(THIS);
                 method.swap();
                 method.putfield(classname, regexpField, cg.ci(RubyRegexp.class));
                 method.label(alreadyCreated);
                 method.aload(THIS);
                 method.getfield(classname, regexpField, cg.ci(RubyRegexp.class));
             }
         }
 
         public void pollThreadEvents() {
             loadThreadContext();
             invokeThreadContext("pollThreadEvents", cg.sig(Void.TYPE));
         }
 
         public void nullToNil() {
             Label notNull = new Label();
             method.dup();
             method.ifnonnull(notNull);
             method.pop();
             method.aload(NIL_INDEX);
             method.label(notNull);
         }
 
         public void isInstanceOf(Class clazz, BranchCallback trueBranch, BranchCallback falseBranch) {
             method.instance_of(cg.p(clazz));
 
             Label falseJmp = new Label();
             Label afterJmp = new Label();
 
             method.ifeq(falseJmp); // EQ == 0 (i.e. false)
             trueBranch.branch(this);
 
             method.go_to(afterJmp);
             method.label(falseJmp);
 
             falseBranch.branch(this);
 
             method.label(afterJmp);
         }
 
         public void isCaptured(final int number, final BranchCallback trueBranch, final BranchCallback falseBranch) {
             backref();
             method.dup();
             isInstanceOf(RubyMatchData.class, new BranchCallback() {
 
                 public void branch(MethodCompiler context) {
                     method.visitTypeInsn(CHECKCAST, cg.p(RubyMatchData.class));
                     method.dup();
                     method.invokevirtual(cg.p(RubyMatchData.class), "use", cg.sig(void.class));
                     method.ldc(new Long(number));
                     method.invokevirtual(cg.p(RubyMatchData.class), "group", cg.sig(IRubyObject.class, cg.params(long.class)));
                     method.invokeinterface(cg.p(IRubyObject.class), "isNil", cg.sig(boolean.class));
                     Label isNil = new Label();
                     Label after = new Label();
 
                     method.ifne(isNil);
                     trueBranch.branch(context);
                     method.go_to(after);
 
                     method.label(isNil);
                     falseBranch.branch(context);
                     method.label(after);
                 }
             }, new BranchCallback() {
 
                 public void branch(MethodCompiler context) {
                     method.pop();
                     falseBranch.branch(context);
                 }
             });
         }
 
         public void branchIfModule(CompilerCallback receiverCallback, BranchCallback moduleCallback, BranchCallback notModuleCallback) {
             receiverCallback.call(this);
             isInstanceOf(RubyModule.class, moduleCallback, notModuleCallback);
         }
 
         public void backref() {
             loadThreadContext();
             invokeThreadContext("getCurrentFrame", cg.sig(Frame.class));
             method.invokevirtual(cg.p(Frame.class), "getBackRef", cg.sig(IRubyObject.class));
         }
 
         public void backrefMethod(String methodName) {
             backref();
             method.invokestatic(cg.p(RubyRegexp.class), methodName, cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));
         }
         
         public void issueLoopBreak() {
             // inside a loop, break out of it
             // go to end of loop, leaving break value on stack
             method.go_to(currentLoopLabels[2]);
         }
         
         public void issueLoopNext() {
             // inside a loop, jump to conditional
             method.go_to(currentLoopLabels[0]);
         }
         
         public void issueLoopRedo() {
             // inside a loop, jump to body
             method.go_to(currentLoopLabels[1]);
         }
 
         protected String getNewEnsureName() {
             return "__ensure_" + (ensureNumber++);
         }
 
         public void protect(BranchCallback regularCode, BranchCallback protectedCode, Class ret) {
 
             String mname = getNewEnsureName();
             SkinnyMethodAdapter mv = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, cg.sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}), null, null));
             SkinnyMethodAdapter old_method = null;
             SkinnyMethodAdapter var_old_method = null;
             SkinnyMethodAdapter inv_old_method = null;
             boolean oldWithinProtection = withinProtection;
             withinProtection = true;
             try {
                 old_method = this.method;
                 var_old_method = getVariableCompiler().getMethodAdapter();
                 inv_old_method = getInvocationCompiler().getMethodAdapter();
                 this.method = mv;
                 getVariableCompiler().setMethodAdapter(mv);
                 getInvocationCompiler().setMethodAdapter(mv);
 
                 mv.visitCode();
                 // set up a local IRuby variable
 
                 mv.aload(THREADCONTEXT_INDEX);
                 mv.dup();
                 mv.invokevirtual(cg.p(ThreadContext.class), "getRuntime", cg.sig(Ruby.class));
                 mv.dup();
                 mv.astore(RUNTIME_INDEX);
             
                 // grab nil for local variables
                 mv.invokevirtual(cg.p(Ruby.class), "getNil", cg.sig(IRubyObject.class));
                 mv.astore(NIL_INDEX);
             
                 mv.invokevirtual(cg.p(ThreadContext.class), "getCurrentScope", cg.sig(DynamicScope.class));
                 mv.dup();
                 mv.astore(DYNAMIC_SCOPE_INDEX);
                 mv.invokevirtual(cg.p(DynamicScope.class), "getValues", cg.sig(IRubyObject[].class));
                 mv.astore(VARS_ARRAY_INDEX);
 
                 Label codeBegin = new Label();
                 Label codeEnd = new Label();
                 Label ensureBegin = new Label();
                 Label ensureEnd = new Label();
                 method.label(codeBegin);
 
                 regularCode.branch(this);
 
                 method.label(codeEnd);
 
                 protectedCode.branch(this);
                 mv.areturn();
 
                 method.label(ensureBegin);
                 method.astore(EXCEPTION_INDEX);
                 method.label(ensureEnd);
 
                 protectedCode.branch(this);
 
                 method.aload(EXCEPTION_INDEX);
                 method.athrow();
                 
                 method.trycatch(codeBegin, codeEnd, ensureBegin, null);
                 method.trycatch(ensureBegin, ensureEnd, ensureBegin, null);
 
                 mv.visitMaxs(1, 1);
                 mv.visitEnd();
             } finally {
                 this.method = old_method;
                 getVariableCompiler().setMethodAdapter(var_old_method);
                 getInvocationCompiler().setMethodAdapter(inv_old_method);
                 withinProtection = oldWithinProtection;
             }
 
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.aload(ARGS_INDEX);
             if(this instanceof ASMClosureCompiler) {
                 pushNull();
             } else {
                 loadBlock();
             }
             method.invokevirtual(classname, mname, cg.sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}));
         }
 
         protected String getNewRescueName() {
             return "__rescue_" + (rescueNumber++);
         }
 
         public void rescue(BranchCallback regularCode, Class exception, BranchCallback catchCode, Class ret) {
             String mname = getNewRescueName();
             SkinnyMethodAdapter mv = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, cg.sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}), null, null));
             SkinnyMethodAdapter old_method = null;
             SkinnyMethodAdapter var_old_method = null;
             SkinnyMethodAdapter inv_old_method = null;
             Label afterMethodBody = new Label();
             Label catchRetry = new Label();
             Label catchRaised = new Label();
             Label catchJumps = new Label();
             Label exitRescue = new Label();
             boolean oldWithinProtection = withinProtection;
             withinProtection = true;
             try {
                 old_method = this.method;
                 var_old_method = getVariableCompiler().getMethodAdapter();
                 inv_old_method = getInvocationCompiler().getMethodAdapter();
                 this.method = mv;
                 getVariableCompiler().setMethodAdapter(mv);
                 getInvocationCompiler().setMethodAdapter(mv);
 
                 mv.visitCode();
 
                 // set up a local IRuby variable
                 mv.aload(THREADCONTEXT_INDEX);
                 mv.dup();
                 mv.invokevirtual(cg.p(ThreadContext.class), "getRuntime", cg.sig(Ruby.class));
                 mv.dup();
                 mv.astore(RUNTIME_INDEX);
                 
                 // store previous exception for restoration if we rescue something
                 loadRuntime();
                 invokeUtilityMethod("getErrorInfo", cg.sig(IRubyObject.class, Ruby.class));
                 mv.astore(PREVIOUS_EXCEPTION_INDEX);
             
                 // grab nil for local variables
                 mv.invokevirtual(cg.p(Ruby.class), "getNil", cg.sig(IRubyObject.class));
                 mv.astore(NIL_INDEX);
             
                 mv.invokevirtual(cg.p(ThreadContext.class), "getCurrentScope", cg.sig(DynamicScope.class));
                 mv.dup();
                 mv.astore(DYNAMIC_SCOPE_INDEX);
                 mv.invokevirtual(cg.p(DynamicScope.class), "getValues", cg.sig(IRubyObject[].class));
                 mv.astore(VARS_ARRAY_INDEX);
 
                 Label beforeBody = new Label();
                 Label afterBody = new Label();
                 Label catchBlock = new Label();
                 mv.visitTryCatchBlock(beforeBody, afterBody, catchBlock, cg.p(exception));
                 mv.visitLabel(beforeBody);
 
                 regularCode.branch(this);
 
                 mv.label(afterBody);
                 mv.go_to(exitRescue);
                 mv.label(catchBlock);
                 mv.astore(EXCEPTION_INDEX);
 
                 catchCode.branch(this);
                 
                 mv.label(afterMethodBody);
                 mv.go_to(exitRescue);
                 
                 mv.trycatch(beforeBody, afterMethodBody, catchRetry, cg.p(JumpException.RetryJump.class));
                 mv.label(catchRetry);
                 mv.pop();
                 mv.go_to(beforeBody);
                 
                 // any exceptions raised must continue to be raised, skipping $! restoration
                 mv.trycatch(beforeBody, afterMethodBody, catchRaised, cg.p(RaiseException.class));
                 mv.label(catchRaised);
                 mv.athrow();
                 
                 // and remaining jump exceptions should restore $!
                 mv.trycatch(beforeBody, afterMethodBody, catchJumps, cg.p(JumpException.class));
                 mv.label(catchJumps);
                 loadRuntime();
                 mv.aload(PREVIOUS_EXCEPTION_INDEX);
                 invokeUtilityMethod("setErrorInfo", cg.sig(void.class, Ruby.class, IRubyObject.class));
                 mv.athrow();
                 
                 mv.label(exitRescue);
                 
                 // restore the original exception
                 loadRuntime();
                 mv.aload(PREVIOUS_EXCEPTION_INDEX);
                 invokeUtilityMethod("setErrorInfo", cg.sig(void.class, Ruby.class, IRubyObject.class));
                 
                 mv.areturn();
                 mv.visitMaxs(1, 1);
                 mv.visitEnd();
             } finally {
                 withinProtection = oldWithinProtection;
                 this.method = old_method;
                 getVariableCompiler().setMethodAdapter(var_old_method);
                 getInvocationCompiler().setMethodAdapter(inv_old_method);
             }
             
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.aload(ARGS_INDEX);
             if(this instanceof ASMClosureCompiler) {
                 pushNull();
             } else {
                 loadBlock();
             }
             method.invokevirtual(classname, mname, cg.sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}));
         }
 
         public void inDefined() {
             method.aload(THREADCONTEXT_INDEX);
             method.iconst_1();
             invokeThreadContext("setWithinDefined", cg.sig(void.class, cg.params(boolean.class)));
         }
 
         public void outDefined() {
             method.aload(THREADCONTEXT_INDEX);
             method.iconst_0();
             invokeThreadContext("setWithinDefined", cg.sig(void.class, cg.params(boolean.class)));
         }
 
         public void stringOrNil() {
             loadRuntime();
             loadNil();
             invokeUtilityMethod("stringOrNil", cg.sig(IRubyObject.class, String.class, Ruby.class, IRubyObject.class));
         }
 
         public void pushNull() {
             method.aconst_null();
         }
 
         public void pushString(String str) {
             method.ldc(str);
         }
 
         public void isMethodBound(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
             metaclass();
             method.ldc(name);
             method.iconst_0(); // push false
             method.invokevirtual(cg.p(RubyClass.class), "isMethodBound", cg.sig(boolean.class, cg.params(String.class, boolean.class)));
             Label falseLabel = new Label();
             Label exitLabel = new Label();
             method.ifeq(falseLabel); // EQ == 0 (i.e. false)
             trueBranch.branch(this);
             method.go_to(exitLabel);
             method.label(falseLabel);
             falseBranch.branch(this);
             method.label(exitLabel);
         }
 
         public void hasBlock(BranchCallback trueBranch, BranchCallback falseBranch) {
             loadBlock();
             method.invokevirtual(cg.p(Block.class), "isGiven", cg.sig(boolean.class));
             Label falseLabel = new Label();
             Label exitLabel = new Label();
             method.ifeq(falseLabel); // EQ == 0 (i.e. false)
             trueBranch.branch(this);
             method.go_to(exitLabel);
             method.label(falseLabel);
             falseBranch.branch(this);
             method.label(exitLabel);
         }
         
         public void isGlobalDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
             loadRuntime();
             invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
             method.ldc(name);
             method.invokevirtual(cg.p(GlobalVariables.class), "isDefined", cg.sig(boolean.class, cg.params(String.class)));
             Label falseLabel = new Label();
             Label exitLabel = new Label();
             method.ifeq(falseLabel); // EQ == 0 (i.e. false)
             trueBranch.branch(this);
             method.go_to(exitLabel);
             method.label(falseLabel);
             falseBranch.branch(this);
             method.label(exitLabel);
         }
         
         public void isConstantDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
             loadThreadContext();
             method.ldc(name);
             invokeThreadContext("getConstantDefined", cg.sig(boolean.class, cg.params(String.class)));
             Label falseLabel = new Label();
             Label exitLabel = new Label();
             method.ifeq(falseLabel); // EQ == 0 (i.e. false)
             trueBranch.branch(this);
             method.go_to(exitLabel);
             method.label(falseLabel);
             falseBranch.branch(this);
             method.label(exitLabel);
         }
         
         public void isInstanceVariableDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
             loadSelf();
             invokeIRubyObject("getInstanceVariables", cg.sig(InstanceVariables.class));
             method.ldc(name);
             //method.invokeinterface(cg.p(IRubyObject.class), "getInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class)));
             method.invokeinterface(cg.p(InstanceVariables.class), "fastHasInstanceVariable", cg.sig(boolean.class, cg.params(String.class)));
             Label trueLabel = new Label();
             Label exitLabel = new Label();
             //method.ifnonnull(trueLabel);
             method.ifne(trueLabel);
             falseBranch.branch(this);
             method.go_to(exitLabel);
             method.label(trueLabel);
             trueBranch.branch(this);
             method.label(exitLabel);
         }
         
         public void isClassVarDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch){
             method.ldc(name);
             method.invokevirtual(cg.p(RubyModule.class), "fastIsClassVarDefined", cg.sig(boolean.class, cg.params(String.class)));
             Label trueLabel = new Label();
             Label exitLabel = new Label();
             method.ifne(trueLabel);
             falseBranch.branch(this);
             method.go_to(exitLabel);
             method.label(trueLabel);
             trueBranch.branch(this);
             method.label(exitLabel);
         }
         
         public Object getNewEnding() {
             return new Label();
         }
         
         public void isNil(BranchCallback trueBranch, BranchCallback falseBranch) {
             method.invokeinterface(cg.p(IRubyObject.class), "isNil", cg.sig(boolean.class));
             Label falseLabel = new Label();
             Label exitLabel = new Label();
             method.ifeq(falseLabel); // EQ == 0 (i.e. false)
             trueBranch.branch(this);
             method.go_to(exitLabel);
             method.label(falseLabel);
             falseBranch.branch(this);
             method.label(exitLabel);
         }
         
         public void isNull(BranchCallback trueBranch, BranchCallback falseBranch) {
             Label falseLabel = new Label();
             Label exitLabel = new Label();
             method.ifnonnull(falseLabel);
             trueBranch.branch(this);
             method.go_to(exitLabel);
             method.label(falseLabel);
             falseBranch.branch(this);
             method.label(exitLabel);
         }
         
         public void ifNull(Object gotoToken) {
             method.ifnull((Label)gotoToken);
         }
         
         public void ifNotNull(Object gotoToken) {
             method.ifnonnull((Label)gotoToken);
         }
         
         public void setEnding(Object endingToken){
             method.label((Label)endingToken);
         }
         
         public void go(Object gotoToken) {
             method.go_to((Label)gotoToken);
         }
         
         public void isConstantBranch(final BranchCallback setup, final BranchCallback isConstant, final BranchCallback isMethod, final BranchCallback none, final String name) {
             rescue(new BranchCallback() {
                     public void branch(MethodCompiler context) {
                         setup.branch(AbstractMethodCompiler.this);
                         method.dup(); //[C,C]
                         method.instance_of(cg.p(RubyModule.class)); //[C, boolean]
 
                         Label falseJmp = new Label();
                         Label afterJmp = new Label();
                         Label nextJmp = new Label();
                         Label nextJmpPop = new Label();
 
                         method.ifeq(nextJmp); // EQ == 0 (i.e. false)   //[C]
                         method.visitTypeInsn(CHECKCAST, cg.p(RubyModule.class));
                         method.dup(); //[C, C]
                         method.ldc(name); //[C, C, String]
                         method.invokevirtual(cg.p(RubyModule.class), "fastGetConstantAt", cg.sig(IRubyObject.class, cg.params(String.class))); //[C, null|C]
                         method.dup();
                         method.ifnull(nextJmpPop);
                         method.pop(); method.pop();
 
                         isConstant.branch(AbstractMethodCompiler.this);
 
                         method.go_to(afterJmp);
                         
                         method.label(nextJmpPop);
                         method.pop();
 
                         method.label(nextJmp); //[C]
 
                         metaclass();
                         method.ldc(name);
                         method.iconst_1(); // push true
                         method.invokevirtual(cg.p(RubyClass.class), "isMethodBound", cg.sig(boolean.class, cg.params(String.class, boolean.class)));
                         method.ifeq(falseJmp); // EQ == 0 (i.e. false)
                         
                         isMethod.branch(AbstractMethodCompiler.this);
                         method.go_to(afterJmp);
 
                         method.label(falseJmp);
                         none.branch(AbstractMethodCompiler.this);
             
                         method.label(afterJmp);
                     }}, JumpException.class, none, String.class);
         }
         
         public void metaclass() {
             invokeIRubyObject("getMetaClass", cg.sig(RubyClass.class));
         }
         
         public void getVisibilityFor(String name) {
             method.ldc(name);
             method.invokevirtual(cg.p(RubyClass.class), "searchMethod", cg.sig(DynamicMethod.class, cg.params(String.class)));
             method.invokevirtual(cg.p(DynamicMethod.class), "getVisibility", cg.sig(Visibility.class));
         }
         
         public void isPrivate(Object gotoToken, int toConsume) {
             method.invokevirtual(cg.p(Visibility.class), "isPrivate", cg.sig(boolean.class));
             Label temp = new Label();
             method.ifeq(temp); // EQ == 0 (i.e. false)
             while((toConsume--) > 0) {
                   method.pop();
             }
             method.go_to((Label)gotoToken);
             method.label(temp);
         }
         
         public void isNotProtected(Object gotoToken, int toConsume) {
             method.invokevirtual(cg.p(Visibility.class), "isProtected", cg.sig(boolean.class));
             Label temp = new Label();
             method.ifne(temp);
             while((toConsume--) > 0) {
                   method.pop();
             }
             method.go_to((Label)gotoToken);
             method.label(temp);
         }
         
         public void selfIsKindOf(Object gotoToken) {
             method.invokevirtual(cg.p(RubyClass.class), "getRealClass", cg.sig(RubyClass.class));
             loadSelf();
             method.invokevirtual(cg.p(RubyModule.class), "isInstance", cg.sig(boolean.class, cg.params(IRubyObject.class)));
             method.ifne((Label)gotoToken); // EQ != 0 (i.e. true)
         }
         
         public void notIsModuleAndClassVarDefined(String name, Object gotoToken) {
             method.dup(); //[?, ?]
             method.instance_of(cg.p(RubyModule.class)); //[?, boolean]
             Label falsePopJmp = new Label();
             Label successJmp = new Label();
             method.ifeq(falsePopJmp);
 
             method.visitTypeInsn(CHECKCAST, cg.p(RubyModule.class)); //[RubyModule]
             method.ldc(name); //[RubyModule, String]
             
             method.invokevirtual(cg.p(RubyModule.class), "fastIsClassVarDefined", cg.sig(boolean.class, cg.params(String.class))); //[boolean]
             method.ifeq((Label)gotoToken);
             method.go_to(successJmp);
             method.label(falsePopJmp);
             method.pop();
             method.go_to((Label)gotoToken);
             method.label(successJmp);
         }
         
         public void ifSingleton(Object gotoToken) {
             method.invokevirtual(cg.p(RubyModule.class), "isSingleton", cg.sig(boolean.class));
             method.ifne((Label)gotoToken); // EQ == 0 (i.e. false)
         }
         
         public void getInstanceVariable(String name) {
             method.ldc(name);
             invokeIRubyObject("getInstanceVariables", cg.sig(InstanceVariables.class));
             method.invokeinterface(cg.p(InstanceVariables.class), "fastGetInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class)));
         }
         
         public void getFrameName() {
             loadThreadContext();
             invokeThreadContext("getFrameName", cg.sig(String.class));
         }
         
         public void getFrameKlazz() {
             loadThreadContext();
             invokeThreadContext("getFrameKlazz", cg.sig(RubyModule.class));
         }
         
         public void superClass() {
             method.invokevirtual(cg.p(RubyModule.class), "getSuperClass", cg.sig(RubyClass.class));
         }
         public void attached() {
             method.visitTypeInsn(CHECKCAST, cg.p(MetaClass.class));
             method.invokevirtual(cg.p(MetaClass.class), "getAttached", cg.sig(IRubyObject.class));
         }
         public void ifNotSuperMethodBound(Object token) {
             method.swap();
             method.iconst_0();
             method.invokevirtual(cg.p(RubyModule.class), "isMethodBound", cg.sig(boolean.class, cg.params(String.class, boolean.class)));
             method.ifeq((Label)token);
         }
         
         public void concatArrays() {
             method.invokevirtual(cg.p(RubyArray.class), "concat", cg.sig(RubyArray.class, cg.params(IRubyObject.class)));
         }
         
         public void concatObjectArrays() {
             invokeUtilityMethod("concatObjectArrays", cg.sig(IRubyObject[].class, cg.params(IRubyObject[].class, IRubyObject[].class)));
         }
         
         public void appendToArray() {
             method.invokevirtual(cg.p(RubyArray.class), "append", cg.sig(RubyArray.class, cg.params(IRubyObject.class)));
         }
         
         public void appendToObjectArray() {
             invokeUtilityMethod("appendToObjectArray", cg.sig(IRubyObject[].class, cg.params(IRubyObject[].class, IRubyObject.class)));
         }
         
         public void convertToJavaArray() {
             method.invokestatic(cg.p(ArgsUtil.class), "convertToJavaArray", cg.sig(IRubyObject[].class, cg.params(IRubyObject.class)));
         }
 
         public void aliasGlobal(String newName, String oldName) {
             loadRuntime();
             invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
             method.ldc(newName);
             method.ldc(oldName);
             method.invokevirtual(cg.p(GlobalVariables.class), "alias", cg.sig(Void.TYPE, cg.params(String.class, String.class)));
             loadNil();
         }
         
         public void undefMethod(String name) {
             loadThreadContext();
             invokeThreadContext("getRubyClass", cg.sig(RubyModule.class));
             
             Label notNull = new Label();
             method.dup();
             method.ifnonnull(notNull);
             method.pop();
             loadRuntime();
             method.ldc("No class to undef method '" + name + "'.");
             invokeIRuby("newTypeError", cg.sig(RaiseException.class, cg.params(String.class)));
             method.athrow();
             
             method.label(notNull);
             method.ldc(name);
             method.invokevirtual(cg.p(RubyModule.class), "undef", cg.sig(Void.TYPE, cg.params(String.class)));
             
             loadNil();
         }
 
         public void defineClass(
                 final String name, 
                 final StaticScope staticScope, 
                 final CompilerCallback superCallback, 
                 final CompilerCallback pathCallback, 
                 final CompilerCallback bodyCallback, 
                 final CompilerCallback receiverCallback) {
             String methodName = "rubyclass__" + JavaNameMangler.mangleStringForCleanJavaIdentifier(name) + "__" + ++methodIndex;
 
             final ASMMethodCompiler methodCompiler = new ASMMethodCompiler(methodName, null);
             
             CompilerCallback bodyPrep = new CompilerCallback() {
                 public void call(MethodCompiler context) {
                     if (receiverCallback == null) {
                         if (superCallback != null) {
                             methodCompiler.loadRuntime();
                             superCallback.call(methodCompiler);
 
                             methodCompiler.invokeUtilityMethod("prepareSuperClass", cg.sig(RubyClass.class, cg.params(Ruby.class, IRubyObject.class)));
                         } else {
                             methodCompiler.method.aconst_null();
                         }
 
                         methodCompiler.loadThreadContext();
 
                         pathCallback.call(methodCompiler);
 
                         methodCompiler.invokeUtilityMethod("prepareClassNamespace", cg.sig(RubyModule.class, cg.params(ThreadContext.class, IRubyObject.class)));
 
                         methodCompiler.method.swap();
 
                         methodCompiler.method.ldc(name);
 
                         methodCompiler.method.swap();
 
                         methodCompiler.method.invokevirtual(cg.p(RubyModule.class), "defineOrGetClassUnder", cg.sig(RubyClass.class, cg.params(String.class, RubyClass.class)));
                     } else {
                         methodCompiler.loadRuntime();
 
diff --git a/src/org/jruby/runtime/Block.java b/src/org/jruby/runtime/Block.java
index 394e19420c..02c75ce2a6 100644
--- a/src/org/jruby/runtime/Block.java
+++ b/src/org/jruby/runtime/Block.java
@@ -1,204 +1,186 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.ast.NodeType;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *  Internal live representation of a block ({...} or do ... end).
  */
 public class Block {
     // FIXME: Maybe not best place, but move it to a good home
     public static final int ZERO_ARGS = 0;
     public static final int MULTIPLE_ASSIGNMENT = 1;
     public static final int ARRAY = 2;
     public static final int SINGLE_RESTARG = 3;
     
     public enum Type { NORMAL, PROC, LAMBDA }
     
     /**
      * The Proc that this block is associated with.  When we reference blocks via variable
      * reference they are converted to Proc objects.  We store a reference of the associated
      * Proc object for easy conversion.  
      */
     private RubyProc proc = null;
     
     public Type type = Type.NORMAL;
     
     private final Binding binding;
     
     private final BlockBody body;
     
     /**
      * All Block variables should either refer to a real block or this NULL_BLOCK.
      */
     public static final Block NULL_BLOCK = new Block() {
         public boolean isGiven() {
             return false;
         }
         
         public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
                 RubyModule klass, boolean aValue) {
             throw context.getRuntime().newLocalJumpError("noreason", (IRubyObject)value, "yield called out of block");
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject[] args) {
             throw context.getRuntime().newLocalJumpError("noreason", context.getRuntime().newArrayNoCopy(args), "yield called out of block");
         }
 
         public IRubyObject yield(ThreadContext context, IRubyObject value) {
             throw context.getRuntime().newLocalJumpError("noreason", (IRubyObject)value, "yield called out of block");
         }
         
         public Block cloneBlock() {
             return this;
         }
         
         public BlockBody getBody() {
             return BlockBody.NULL_BODY;
         }
     };
     
     protected Block() {
         this(null, null);
     }
     
     public Block(BlockBody body, Binding binding) {
         this.body = body;
         this.binding = binding;
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject[] args) {
         return body.call(context, args, binding, type);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value) {
         return body.yield(context, value, binding, type);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue) {
         return body.yield(context, value, self, klass, aValue, binding, type);
     }
     
     protected int arrayLength(IRubyObject node) {
         return node instanceof RubyArray ? ((RubyArray)node).getLength() : 0;
     }
     
     public Block cloneBlock() {
         // We clone dynamic scope because this will be a new instance of a block.  Any previously
         // captured instances of this block may still be around and we do not want to start
         // overwriting those values when we create a new one.
         // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
         Block newBlock = body.cloneBlock(binding);
         
         newBlock.type = type;
 
         return newBlock;
     }
 
     /**
      * What is the arity of this block?
      * 
      * @return the arity
      */
     public Arity arity() {
         return body.arity();
     }
 
     /**
      * Retrieve the proc object associated with this block
      * 
      * @return the proc or null if this has no proc associated with it
      */
     public RubyProc getProcObject() {
     	return proc;
     }
     
     /**
      * Set the proc object associated with this block
      * 
      * @param procObject
      */
     public void setProcObject(RubyProc procObject) {
     	this.proc = procObject;
     }
     
     /**
      * Is the current block a real yield'able block instead a null one
      * 
      * @return true if this is a valid block or false otherwise
      */
     public boolean isGiven() {
         return true;
     }
     
     public Binding getBinding() {
         return binding;
     }
     
     public BlockBody getBody() {
         return body;
     }
 
     /**
      * Gets the frame.
      * 
      * @return Returns a RubyFrame
      */
     public Frame getFrame() {
         return binding.getFrame();
     }
-
-    
-    /**
-     * Compiled codes way of examining arguments
-     * 
-     * @param nodeId to be considered
-     * @return something not linked to AST and a constant to make compiler happy
-     */
-    public static int asArgumentType(NodeType nodeId) {
-        if (nodeId == null) return ZERO_ARGS;
-        
-        switch (nodeId) {
-        case ZEROARGNODE: return ZERO_ARGS;
-        case MULTIPLEASGNNODE: return MULTIPLE_ASSIGNMENT;
-        case SVALUENODE: return SINGLE_RESTARG;
-        }
-        return ARRAY;
-    }
 }
diff --git a/src/org/jruby/runtime/BlockBody.java b/src/org/jruby/runtime/BlockBody.java
index b9aad079dd..d51b94d57e 100644
--- a/src/org/jruby/runtime/BlockBody.java
+++ b/src/org/jruby/runtime/BlockBody.java
@@ -1,135 +1,192 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
+import org.jruby.ast.IterNode;
+import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NodeType;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block.Type;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *  Internal live representation of a block ({...} or do ... end).
  */
 public abstract class BlockBody implements JumpTarget {
     // FIXME: Maybe not best place, but move it to a good home
     public static final int ZERO_ARGS = 0;
     public static final int MULTIPLE_ASSIGNMENT = 1;
     public static final int ARRAY = 2;
     public static final int SINGLE_RESTARG = 3;
+    protected final int argumentType;
     
-    public static final BlockBody NULL_BODY = new BlockBody() {
+    public static final BlockBody NULL_BODY = new BlockBody(ZERO_ARGS) {
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Type type) {
             return null;
         }
 
         @Override
         public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Type type) {
             return null;
         }
 
         @Override
         public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, RubyModule klass, boolean aValue, Binding binding, Type type) {
             return null;
         }
         
         @Override
         public StaticScope getStaticScope() {
             return null;
         }
 
         @Override
         public Block cloneBlock(Binding binding) {
             return null;
         }
 
         @Override
         public Arity arity() {
             return null;
         }
     };
     
-    public BlockBody() {
+    public BlockBody(int argumentType) {
+        this.argumentType = argumentType;
     }
 
     public abstract IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type);
     
     public abstract IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type);
     
     public abstract IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue, Binding binding, Block.Type type);
     
     protected int arrayLength(IRubyObject node) {
         return node instanceof RubyArray ? ((RubyArray)node).getLength() : 0;
     }
     
     public abstract StaticScope getStaticScope();
 
     public abstract Block cloneBlock(Binding binding);
 
     /**
      * What is the arity of this block?
      * 
      * @return the arity
      */
     public abstract Arity arity();
     
     /**
      * Is the current block a real yield'able block instead a null one
      * 
      * @return true if this is a valid block or false otherwise
      */
     public boolean isGiven() {
         return true;
     }
     
     /**
      * Compiled codes way of examining arguments
      * 
      * @param nodeId to be considered
      * @return something not linked to AST and a constant to make compiler happy
      */
     public static int asArgumentType(NodeType nodeId) {
         if (nodeId == null) return ZERO_ARGS;
         
         switch (nodeId) {
         case ZEROARGNODE: return ZERO_ARGS;
         case MULTIPLEASGNNODE: return MULTIPLE_ASSIGNMENT;
         case SVALUENODE: return SINGLE_RESTARG;
         }
         return ARRAY;
     }
+    
+    public IRubyObject[] prepareArgumentsForCall(ThreadContext context, IRubyObject[] args, Block.Type type) {
+        switch (type) {
+        case NORMAL: {
+            assert false : "can this happen?";
+            if (args.length == 1 && args[0] instanceof RubyArray) {
+                if (argumentType == MULTIPLE_ASSIGNMENT || argumentType == SINGLE_RESTARG) {
+                    args = ((RubyArray) args[0]).toJavaArray();
+                }
+                break;
+            }
+        }
+        case PROC: {
+            if (args.length == 1 && args[0] instanceof RubyArray) {
+                if (argumentType == MULTIPLE_ASSIGNMENT && argumentType != SINGLE_RESTARG) {
+                    args = ((RubyArray) args[0]).toJavaArray();
+                }
+            }
+            break;
+        }
+        case LAMBDA:
+            if (argumentType == ARRAY && args.length != 1) {
+                context.getRuntime().getWarnings().warn("multiple values for a block parameter (" + args.length + " for " + arity().getValue() + ")");
+                if (args.length == 0) {
+                    args = new IRubyObject[] {context.getRuntime().getNil()};
+                } else {
+                    args = new IRubyObject[] {context.getRuntime().newArrayNoCopy(args)};
+                }
+            } else {
+                arity().checkArity(context.getRuntime(), args);
+            }
+            break;
+        }
+        
+        return args;
+    }
+    
+    public static NodeType getArgumentTypeWackyHack(IterNode iterNode) {
+        NodeType argsNodeId = null;
+        if (iterNode.getVarNode() != null && iterNode.getVarNode().nodeId != NodeType.ZEROARGNODE) {
+            // if we have multiple asgn with just *args, need a special type for that
+            argsNodeId = iterNode.getVarNode().nodeId;
+            if (argsNodeId == NodeType.MULTIPLEASGNNODE) {
+                MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode)iterNode.getVarNode();
+                if (multipleAsgnNode.getHeadNode() == null && multipleAsgnNode.getArgsNode() != null) {
+                    // FIXME: This is gross. Don't do this.
+                    argsNodeId = NodeType.SVALUENODE;
+                }
+            }
+        }
+        
+        return argsNodeId;
+    }
 }
diff --git a/src/org/jruby/runtime/CallBlock.java b/src/org/jruby/runtime/CallBlock.java
index 10173e746d..b42b901e43 100644
--- a/src/org/jruby/runtime/CallBlock.java
+++ b/src/org/jruby/runtime/CallBlock.java
@@ -1,140 +1,141 @@
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
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyModule;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Block implemented using a Java-based BlockCallback implementation
  * rather than with an ICallable. For lightweight block logic within
  * Java code.
  */
 public class CallBlock extends BlockBody {
     private Arity arity;
     private BlockCallback callback;
     private RubyModule imClass;
     private ThreadContext context;
     
     public static Block newCallClosure(IRubyObject self, RubyModule imClass, Arity arity, BlockCallback callback, ThreadContext context) {
         Binding binding = new Binding(self,
                 context.getCurrentFrame(),
                 Visibility.PUBLIC,
                 context.getRubyClass(),
                 context.getCurrentScope());
         BlockBody body = new CallBlock(imClass, arity, callback, context);
         
         return new Block(body, binding);
     }
 
     private CallBlock(RubyModule imClass, Arity arity, BlockCallback callback, ThreadContext context) {
+        super(BlockBody.SINGLE_RESTARG);
         this.arity = arity;
         this.callback = callback;
         this.imClass = imClass;
         this.context = context;
     }
     
     protected void pre(ThreadContext context, RubyModule klass, Binding binding) {
         // FIXME: This could be a "light" block
         context.preYieldNoScope(binding, klass);
     }
     
     protected void post(ThreadContext context, Binding binding) {
         context.postYieldNoScope();
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
         return callback.call(context, args, Block.NULL_BLOCK);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         return yield(context, value, null, null, false, binding, type);
     }
 
     /**
      * Yield to this block, usually passed to the current call.
      * 
      * @param context represents the current thread-specific data
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param aValue Should value be arrayified or not?
      * @return
      */
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         if (klass == null) {
             self = binding.getSelf();
             // FIXME: We never set this back!
             binding.getFrame().setSelf(self);
         }
         
         pre(context, klass, binding);
 
         try {
             IRubyObject[] args = new IRubyObject[] {value};
             // This while loop is for restarting the block call in case a 'redo' fires.
             while (true) {
                 try {
                     return callback.call(context, args, Block.NULL_BLOCK);
                 } catch (JumpException.RedoJump rj) {
                     context.pollThreadEvents();
                     // do nothing, allow loop to redo
                 } catch (JumpException.BreakJump bj) {
                     if (bj.getTarget() == null) {
                         bj.setTarget(this);                            
                     }                        
                     throw bj;
                 }
             }
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return (IRubyObject)nj.getValue();
         } finally {
             post(context, binding);
         }
     }
     
     public StaticScope getStaticScope() {
         throw new RuntimeException("CallBlock does not have a static scope; this should not be called");
     }
 
     public Block cloneBlock(Binding binding) {
         binding = new Binding(binding.getSelf(), context.getCurrentFrame().duplicate(),
                 Visibility.PUBLIC,
                 context.getRubyClass(),
                 context.getCurrentScope());
         return new Block(this, binding);
     }
 
     public Arity arity() {
         return arity;
     }
 }
diff --git a/src/org/jruby/runtime/CompiledBlock.java b/src/org/jruby/runtime/CompiledBlock.java
index b499f4f543..ef680e98d5 100644
--- a/src/org/jruby/runtime/CompiledBlock.java
+++ b/src/org/jruby/runtime/CompiledBlock.java
@@ -1,211 +1,189 @@
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
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Block implemented using a Java-based BlockCallback implementation
  * rather than with an ICallable. For lightweight block logic within
  * Java code.
  */
 public class CompiledBlock extends BlockBody {
     protected final CompiledBlockCallback callback;
     protected final boolean hasMultipleArgsHead;
-    protected final int argumentType;
     protected final Arity arity;
     protected final StaticScope scope;
     
     public static Block newCompiledClosure(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
         DynamicScope dynamicScope, Arity arity, StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         Binding binding = new Binding(self, frame, visibility, klass, dynamicScope);
         BlockBody body = new CompiledBlock(arity, scope, callback, hasMultipleArgsHead, argumentType);
         
         return new Block(body, binding);
     }
     
     public static Block newCompiledClosure(ThreadContext context, IRubyObject self, Arity arity,
             StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         Frame f = context.getCurrentFrame();
         f.setPosition(context.getPosition());
         return newCompiledClosure(
                 self,
                 f,
                 Visibility.PUBLIC,
                 context.getRubyClass(),
                 context.getCurrentScope(),
                 arity,
                 scope,
                 callback,
                 hasMultipleArgsHead,
                 argumentType);
     }
 
     protected CompiledBlock(Arity arity, StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
+        super(argumentType);
         this.arity = arity;
         this.scope = scope;
         this.callback = callback;
         this.hasMultipleArgsHead = hasMultipleArgsHead;
-        this.argumentType = argumentType;
     }
     
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
-        switch (type) {
-        case NORMAL: {
-            assert false : "can this happen?";
-            if (args.length == 1 && args[0] instanceof RubyArray) {
-                if (argumentType == MULTIPLE_ASSIGNMENT || argumentType == SINGLE_RESTARG) {
-                    args = ((RubyArray) args[0]).toJavaArray();
-                }
-                break;
-            }
-        }
-        case PROC: {
-            if (args.length == 1 && args[0] instanceof RubyArray) {
-                if (argumentType == MULTIPLE_ASSIGNMENT && argumentType != SINGLE_RESTARG) {
-                    args = ((RubyArray) args[0]).toJavaArray();
-                }
-            }
-            break;
-        }
-        case LAMBDA:
-            arity().checkArity(context.getRuntime(), args);
-            break;
-        }
+        args = prepareArgumentsForCall(context, args, type);
 
         return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true, binding, type);
     }
 
     @Override
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         return yield(context, value, null, null, false, binding, type);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject args, IRubyObject self, RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         if (klass == null) {
             self = binding.getSelf();
             binding.getFrame().setSelf(self);
         }
 
         // handle as though it's just an array coming in...i.e. it should be multiassigned or just 
         // assigned as is to var 0.
         // FIXME for now, since masgn isn't supported, this just wraps args in an IRubyObject[], 
         // since single vars will want that anyway
         Visibility oldVis = binding.getFrame().getVisibility();
         IRubyObject[] realArgs = aValue ? 
                 setupBlockArgs(context, args, self) : setupBlockArg(context, args, self); 
         pre(context, klass, binding);
         
         try {
             return callback.call(context, self, realArgs);
         } catch (JumpException.BreakJump bj) {
             if (bj.getTarget() == null) {
                 bj.setTarget(this);
             }
             throw bj;
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return type == Block.Type.LAMBDA ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
         } finally {
             binding.getFrame().setVisibility(oldVis);
             post(context, binding);
         }
     }
     
     protected void pre(ThreadContext context, RubyModule klass, Binding binding) {
         context.preYieldSpecificBlockNEW(binding, scope, klass);
     }
     
     protected void post(ThreadContext context, Binding binding) {
         context.postYield(binding);
     }
 
     private IRubyObject[] setupBlockArgs(ThreadContext context, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return IRubyObject.NULL_ARRAY;
         case MULTIPLE_ASSIGNMENT:
         case SINGLE_RESTARG:
             return new IRubyObject[] {value};
         default:
             int length = arrayLength(value);
             switch (length) {
             case 0:
                 value = context.getRuntime().getNil();
                 break;
             case 1:
                 value = ((RubyArray)value).eltInternal(0);
                 break;
             default:
                 context.getRuntime().getWarnings().warn("multiple values for a block parameter (" +
                         length + " for 1)");
             }
             return new IRubyObject[] {value};
         }
     }
 
     private IRubyObject[] setupBlockArg(ThreadContext context, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return IRubyObject.NULL_ARRAY;
         case MULTIPLE_ASSIGNMENT:
         case SINGLE_RESTARG:
             return new IRubyObject[] {ArgsUtil.convertToRubyArray(context.getRuntime(), value, hasMultipleArgsHead)};
         default:
             if (value == null) {
                 context.getRuntime().getWarnings().warn("multiple values for a block parameter (0 for 1)");
                 return new IRubyObject[] {context.getRuntime().getNil()};
             }
             return new IRubyObject[] {value};
         }
     }
     
     public StaticScope getStaticScope() {
         return scope;
     }
 
     public Block cloneBlock(Binding binding) {
         binding = new Binding(binding.getSelf(),
                 binding.getFrame().duplicate(),
                 binding.getVisibility(),
                 binding.getKlass(),
                 binding.getDynamicScope());
         
         return new Block(this, binding);
     }
 
     @Override
     public Arity arity() {
         return arity;
     }
 }
diff --git a/src/org/jruby/runtime/CompiledBlockLight.java b/src/org/jruby/runtime/CompiledBlockLight.java
index 5f27b57cc8..9b123c2051 100644
--- a/src/org/jruby/runtime/CompiledBlockLight.java
+++ b/src/org/jruby/runtime/CompiledBlockLight.java
@@ -1,211 +1,189 @@
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
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Block implemented using a Java-based BlockCallback implementation
  * rather than with an ICallable. For lightweight block logic within
  * Java code.
  */
 public class CompiledBlockLight extends BlockBody {
     protected final CompiledBlockCallback callback;
     protected final boolean hasMultipleArgsHead;
-    protected final int argumentType;
     protected final Arity arity;
     protected final DynamicScope dummyScope;
     
     public static Block newCompiledClosureLight(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
         DynamicScope dynamicScope, Arity arity, StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         Binding binding = new Binding(self, frame, visibility, klass, dynamicScope);
         BlockBody body = new CompiledBlockLight(arity, DynamicScope.newDynamicScope(scope, dynamicScope), callback, hasMultipleArgsHead, argumentType);
         
         return new Block(body, binding);
     }
     
     public static Block newCompiledClosureLight(ThreadContext context, IRubyObject self, Arity arity,
             StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         Frame f = context.getCurrentFrame();
         f.setPosition(context.getPosition());
         return newCompiledClosureLight(
                 self,
                 f,
                 Visibility.PUBLIC,
                 context.getRubyClass(),
                 context.getCurrentScope(),
                 arity,
                 scope, 
                 callback,
                 hasMultipleArgsHead,
                 argumentType);
     }
 
     protected CompiledBlockLight(Arity arity, DynamicScope dummyScope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
+        super(argumentType);
         this.arity = arity;
         this.callback = callback;
         this.hasMultipleArgsHead = hasMultipleArgsHead;
-        this.argumentType = argumentType;
         this.dummyScope = dummyScope;
     }
     
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
-        switch (type) {
-        case NORMAL: {
-            assert false : "can this happen?";
-            if (args.length == 1 && args[0] instanceof RubyArray) {
-                if (argumentType == MULTIPLE_ASSIGNMENT || argumentType == SINGLE_RESTARG) {
-                    args = ((RubyArray) args[0]).toJavaArray();
-                }
-                break;
-            }
-        }
-        case PROC: {
-            if (args.length == 1 && args[0] instanceof RubyArray) {
-                if (argumentType == MULTIPLE_ASSIGNMENT && argumentType != SINGLE_RESTARG) {
-                    args = ((RubyArray) args[0]).toJavaArray();
-                }
-            }
-            break;
-        }
-        case LAMBDA:
-            arity().checkArity(context.getRuntime(), args);
-            break;
-        }
+        args = prepareArgumentsForCall(context, args, type);
 
         return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true, binding, type);
     }
 
     @Override
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         return yield(context, value, null, null, false, binding, type);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject args, IRubyObject self, RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         if (klass == null) {
             self = binding.getSelf();
             binding.getFrame().setSelf(self);
         }
 
         // handle as though it's just an array coming in...i.e. it should be multiassigned or just 
         // assigned as is to var 0.
         // FIXME for now, since masgn isn't supported, this just wraps args in an IRubyObject[], 
         // since single vars will want that anyway
         Visibility oldVis = binding.getFrame().getVisibility();
         IRubyObject[] realArgs = aValue ? 
                 setupBlockArgs(context, args, self) : setupBlockArg(context, args, self); 
         pre(context, klass, binding);
         
         try {
             return callback.call(context, self, realArgs);
         } catch (JumpException.BreakJump bj) {
             if (bj.getTarget() == null) {
                 bj.setTarget(this);
             }
             throw bj;
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return type == Block.Type.LAMBDA ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
         } finally {
             binding.getFrame().setVisibility(oldVis);
             post(context, binding);
         }
     }
     
     protected void pre(ThreadContext context, RubyModule klass, Binding binding) {
         context.preYieldLightBlockNEW(binding, dummyScope, klass);
     }
     
     protected void post(ThreadContext context, Binding binding) {
         context.postYieldLight(binding);
     }
 
     private IRubyObject[] setupBlockArgs(ThreadContext context, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return IRubyObject.NULL_ARRAY;
         case MULTIPLE_ASSIGNMENT:
         case SINGLE_RESTARG:
             return new IRubyObject[] {value};
         default:
             int length = arrayLength(value);
             switch (length) {
             case 0:
                 value = context.getRuntime().getNil();
                 break;
             case 1:
                 value = ((RubyArray)value).eltInternal(0);
                 break;
             default:
                 context.getRuntime().getWarnings().warn("multiple values for a block parameter (" +
                         length + " for 1)");
             }
             return new IRubyObject[] {value};
         }
     }
 
     private IRubyObject[] setupBlockArg(ThreadContext context, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return IRubyObject.NULL_ARRAY;
         case MULTIPLE_ASSIGNMENT:
         case SINGLE_RESTARG:
             return new IRubyObject[] {ArgsUtil.convertToRubyArray(context.getRuntime(), value, hasMultipleArgsHead)};
         default:
             if (value == null) {
                 context.getRuntime().getWarnings().warn("multiple values for a block parameter (0 for 1)");
                 return new IRubyObject[] {context.getRuntime().getNil()};
             }
             return new IRubyObject[] {value};
         }
     }
     
     public StaticScope getStaticScope() {
         return dummyScope.getStaticScope();
     }
 
     public Block cloneBlock(Binding binding) {
         binding = new Binding(binding.getSelf(),
                 binding.getFrame().duplicate(),
                 binding.getVisibility(),
                 binding.getKlass(),
                 binding.getDynamicScope());
         
         return new Block(this, binding);
     }
 
     @Override
     public Arity arity() {
         return arity;
     }
 }
diff --git a/src/org/jruby/runtime/InterpretedBlock.java b/src/org/jruby/runtime/InterpretedBlock.java
index 4d5093d8d8..49f3445350 100644
--- a/src/org/jruby/runtime/InterpretedBlock.java
+++ b/src/org/jruby/runtime/InterpretedBlock.java
@@ -1,295 +1,252 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 package org.jruby.runtime;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.evaluator.AssignmentVisitor;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *  Internal live representation of a block ({...} or do ... end).
  */
 public class InterpretedBlock extends BlockBody {
     /**
      * AST Node representing the parameter (VARiable) list to the block.
      */
     private final IterNode iterNode;
     
     protected final Arity arity;
 
     public static Block newInterpretedClosure(ThreadContext context, IterNode iterNode, IRubyObject self) {
         Frame f = context.getCurrentFrame();
         f.setPosition(context.getPosition());
         return newInterpretedClosure(iterNode,
                          self,
-                         iterNode == null ? null : Arity.procArityOf(iterNode.getVarNode()),
+                         Arity.procArityOf(iterNode.getVarNode()),
                          f,
                          f.getVisibility(),
                          context.getRubyClass(),
                          context.getCurrentScope());
     }
 
     public static Block newInterpretedClosure(ThreadContext context, InterpretedBlock body, IRubyObject self) {
         Frame f = context.getCurrentFrame();
         f.setPosition(context.getPosition());
         Binding binding = new Binding(self,
                          f,
                          f.getVisibility(),
                          context.getRubyClass(),
                          context.getCurrentScope());
         return new Block(body, binding);
     }
     
     public static Block newInterpretedClosure(IterNode iterNode, IRubyObject self, Arity arity, Frame frame,
             Visibility visibility, RubyModule klass, DynamicScope dynamicScope) {
-        return new Block(new InterpretedBlock(iterNode, arity), new Binding(self, frame, visibility, klass, dynamicScope));
+        NodeType argsNodeId = getArgumentTypeWackyHack(iterNode);
+        return new Block(new InterpretedBlock(iterNode, arity, asArgumentType(argsNodeId)), new Binding(self, frame, visibility, klass, dynamicScope));
     }
 
-    public InterpretedBlock(IterNode iterNode) {
-        this(iterNode, iterNode == null ? null : Arity.procArityOf(iterNode.getVarNode()));
+    public InterpretedBlock(IterNode iterNode, int argumentType) {
+        this(iterNode, Arity.procArityOf(iterNode == null ? null : iterNode.getVarNode()), argumentType);
     }
     
-    public InterpretedBlock(IterNode iterNode, Arity arity) {
+    public InterpretedBlock(IterNode iterNode, Arity arity, int argumentType) {
+        super(argumentType);
         this.iterNode = iterNode;
         this.arity = arity;
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
-        switch (type) {
-        case NORMAL: {
-            assert false : "can this happen?";
-            if (args.length == 1 && args[0] instanceof RubyArray && iterNode != null) {
-                Node vNode = iterNode.getVarNode();
-
-                if (vNode.nodeId == NodeType.MULTIPLEASGNNODE) {
-                    args = ((RubyArray) args[0]).toJavaArray();
-                }
-            }
-            break;
-        }
-        case PROC: {
-            if (args.length == 1 && args[0] instanceof RubyArray && iterNode != null) {
-                Node vNode = iterNode.getVarNode();
-
-                if (vNode.nodeId == NodeType.MULTIPLEASGNNODE) {
-                    // if we only have *arg, we leave it wrapped in the array
-                    if (((MultipleAsgnNode)vNode).getArgsNode() == null) {
-                        args = ((RubyArray) args[0]).toJavaArray();
-                    }
-                }
-            }
-            break;
-        }
-        case LAMBDA:
-            arity().checkArity(context.getRuntime(), args);
-            break;
-        }
+        args = prepareArgumentsForCall(context, args, type);
 
         return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true, binding, type);
     }
     
     protected void pre(ThreadContext context, RubyModule klass, Binding binding) {
         context.preYieldSpecificBlockNEW(binding, iterNode.getScope(), klass);
     }
     
     protected void post(ThreadContext context, Binding binding) {
         context.postYield(binding);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         return yield(context, value, null, null, false, binding, type);
     }
 
     /**
      * Yield to this block, usually passed to the current call.
      * 
      * @param context represents the current thread-specific data
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param aValue Should value be arrayified or not?
      * @return
      */
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         if (klass == null) {
             self = binding.getSelf();
             binding.getFrame().setSelf(self);
         }
         
         Visibility oldVis = binding.getFrame().getVisibility();
         pre(context, klass, binding);
 
         try {
             if (iterNode.getVarNode() != null) {
                 if (aValue) {
                     setupBlockArgs(context, iterNode.getVarNode(), value, self);
                 } else {
                     setupBlockArg(context, iterNode.getVarNode(), value, self);
                 }
             }
             
             // This while loop is for restarting the block call in case a 'redo' fires.
             while (true) {
                 try {
                     return ASTInterpreter.eval(context.getRuntime(), context, iterNode.getBodyNode(), self, Block.NULL_BLOCK);
                 } catch (JumpException.RedoJump rj) {
                     context.pollThreadEvents();
                     // do nothing, allow loop to redo
                 } catch (JumpException.BreakJump bj) {
                     if (bj.getTarget() == null) {
                         bj.setTarget(this);                            
                     }                        
                     throw bj;
                 }
             }
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return type == Block.Type.LAMBDA ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
         } finally {
             binding.getFrame().setVisibility(oldVis);
             post(context, binding);
         }
     }
 
     private void setupBlockArgs(ThreadContext context, Node varNode, IRubyObject value, IRubyObject self) {
         Ruby runtime = self.getRuntime();
         
         switch (varNode.nodeId) {
         case ZEROARGNODE:
             break;
         case MULTIPLEASGNNODE:
             value = AssignmentVisitor.multiAssign(runtime, context, self, (MultipleAsgnNode)varNode, (RubyArray)value, false);
             break;
         default:
             int length = arrayLength(value);
             switch (length) {
             case 0:
                 value = runtime.getNil();
                 break;
             case 1:
                 value = ((RubyArray)value).eltInternal(0);
                 break;
             default:
                 runtime.getWarnings().warn("multiple values for a block parameter (" + length + " for 1)");
             }
             AssignmentVisitor.assign(runtime, context, self, varNode, value, Block.NULL_BLOCK, false);
         }
     }
 
     private void setupBlockArg(ThreadContext context, Node varNode, IRubyObject value, IRubyObject self) {
         Ruby runtime = self.getRuntime();
         
         switch (varNode.nodeId) {
         case ZEROARGNODE:
             return;
         case MULTIPLEASGNNODE:
             value = AssignmentVisitor.multiAssign(runtime, context, self, (MultipleAsgnNode)varNode,
                     ArgsUtil.convertToRubyArray(runtime, value, ((MultipleAsgnNode)varNode).getHeadNode() != null), false);
             break;
         default:
             if (value == null) {
                 runtime.getWarnings().warn("multiple values for a block parameter (0 for 1)");
             }
             AssignmentVisitor.assign(runtime, context, self, varNode, value, Block.NULL_BLOCK, false);
         }
     }
     
     protected int arrayLength(IRubyObject node) {
         return node instanceof RubyArray ? ((RubyArray)node).getLength() : 0;
     }
     
     public StaticScope getStaticScope() {
         return iterNode.getScope();
     }
 
     public Block cloneBlock(Binding binding) {
         // We clone dynamic scope because this will be a new instance of a block.  Any previously
         // captured instances of this block may still be around and we do not want to start
         // overwriting those values when we create a new one.
         // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
         binding = new Binding(binding.getSelf(), binding.getFrame().duplicate(), binding.getVisibility(), binding.getKlass(), 
                 binding.getDynamicScope());
         return new Block(this, binding);
     }
 
     public IterNode getIterNode() {
         return iterNode;
     }
 
     /**
      * What is the arity of this block?
      * 
      * @return the arity
      */
     public Arity arity() {
         return arity;
     }
     
     /**
      * Is the current block a real yield'able block instead a null one
      * 
      * @return true if this is a valid block or false otherwise
      */
     public boolean isGiven() {
         return true;
     }
-    
-    /**
-     * Compiled codes way of examining arguments
-     * 
-     * @param nodeId to be considered
-     * @return something not linked to AST and a constant to make compiler happy
-     */
-    public static int asArgumentType(NodeType nodeId) {
-        if (nodeId == null) return ZERO_ARGS;
-        
-        switch (nodeId) {
-        case ZEROARGNODE: return ZERO_ARGS;
-        case MULTIPLEASGNNODE: return MULTIPLE_ASSIGNMENT;
-        case SVALUENODE: return SINGLE_RESTARG;
-        }
-        return ARRAY;
-    }
 }
diff --git a/src/org/jruby/runtime/MethodBlock.java b/src/org/jruby/runtime/MethodBlock.java
index dd03921cfb..b69010a8f0 100644
--- a/src/org/jruby/runtime/MethodBlock.java
+++ b/src/org/jruby/runtime/MethodBlock.java
@@ -1,149 +1,150 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyMethod;
 import org.jruby.RubyModule;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 
 /**
  *  Internal live representation of a block ({...} or do ... end).
  */
 public class MethodBlock extends BlockBody {
     
     private final RubyMethod method;
     private final Callback callback;
     
     private final Arity arity;
 
     public static Block createMethodBlock(ThreadContext context, DynamicScope dynamicScope, Callback callback, RubyMethod method, IRubyObject self) {
         Binding binding = new Binding(self,
                                context.getCurrentFrame().duplicate(),
                          context.getCurrentFrame().getVisibility(),
                          context.getRubyClass(),
                          dynamicScope);
         BlockBody body = new MethodBlock(callback, method);
         
         return new Block(body, binding);
     }
 
     public MethodBlock(Callback callback, RubyMethod method) {
+        super(BlockBody.SINGLE_RESTARG);
         this.callback = callback;
         this.method = method;
         this.arity = Arity.createArity((int) method.arity().getLongValue());
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true, binding, type);
     }
     
     protected void pre(ThreadContext context, RubyModule klass, Binding binding) {
         context.preYieldNoScope(binding, klass);
     }
     
     protected void post(ThreadContext context, Binding binding) {
         context.postYieldNoScope();
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         return yield(context, value, null, null, false, binding, type);
     }
 
     /**
      * Yield to this block, usually passed to the current call.
      * 
      * @param context represents the current thread-specific data
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param aValue Should value be arrayified or not?
      * @return
      */
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         if (klass == null) {
             self = binding.getSelf();
             binding.getFrame().setSelf(self);
         }
         
         pre(context, klass, binding);
 
         try {
             // This while loop is for restarting the block call in case a 'redo' fires.
             while (true) {
                 try {
                     return callback.execute(value, new IRubyObject[] { method, self }, Block.NULL_BLOCK);
                 } catch (JumpException.RedoJump rj) {
                     context.pollThreadEvents();
                     // do nothing, allow loop to redo
                 } catch (JumpException.BreakJump bj) {
                     if (bj.getTarget() == null) {
                         bj.setTarget(this);                            
                     }                        
                     throw bj;
                 }
             }
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return (IRubyObject)nj.getValue();
         } finally {
             post(context, binding);
         }
     }
     
     public StaticScope getStaticScope() {
         throw new RuntimeException("MethodBlock does not have a static scope; this should not be called");
     }
 
     public Block cloneBlock(Binding binding) {
         // We clone dynamic scope because this will be a new instance of a block.  Any previously
         // captured instances of this block may still be around and we do not want to start
         // overwriting those values when we create a new one.
         // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
         binding = new Binding(binding.getSelf(), binding.getFrame().duplicate(), binding.getVisibility(), binding.getKlass(), 
                 binding.getDynamicScope().cloneScope());
 
         return new Block(this, binding);
     }
 
     /**
      * What is the arity of this block?
      * 
      * @return the arity
      */
     public Arity arity() {
         return arity;
     }
 }
diff --git a/src/org/jruby/runtime/SharedScopeBlock.java b/src/org/jruby/runtime/SharedScopeBlock.java
index 2e8ec5e5d3..e389d0d016 100644
--- a/src/org/jruby/runtime/SharedScopeBlock.java
+++ b/src/org/jruby/runtime/SharedScopeBlock.java
@@ -1,68 +1,68 @@
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
  * Copyright (C) 2007 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyModule;
 import org.jruby.ast.IterNode;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * Represents the live state of a for or END construct in Ruby.  This is different from an
  * ordinary block in that it does not have its own scoped variables.  It leeches those from
  * the next outer scope.  Because of this we do not set up, clone, nor tear down scope-related
  * stuff.  Also because of this we do not need to clone the block since it state does not change.
  * 
  */
 public class SharedScopeBlock extends InterpretedBlock {
     protected SharedScopeBlock(IterNode iterNode) {
-        super(iterNode);
+        super(iterNode, asArgumentType(getArgumentTypeWackyHack(iterNode)));
     }
     
     public static Block newInterpretedSharedScopeClosure(ThreadContext context, IterNode iterNode, DynamicScope dynamicScope, IRubyObject self) {
         Binding binding = new Binding(self,
                 context.getCurrentFrame().duplicate(),
                 context.getCurrentFrame().getVisibility(),
                 context.getRubyClass(),
                 dynamicScope);
         BlockBody body = new SharedScopeBlock(iterNode);
 
         return new Block(body, binding);
     }
     
     protected void pre(ThreadContext context, RubyModule klass, Binding binding) {
         context.preForBlock(binding, klass);
     }
     
     public IRubyObject call(ThreadContext context, IRubyObject[] args, IRubyObject replacementSelf, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true, binding, type);
     }
     
     public Block cloneBlock(Binding binding) {
         return new Block(this, binding);
     }
 }
