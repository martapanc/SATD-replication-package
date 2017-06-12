diff --git a/spec/tags/ruby/language/class_tags.txt b/spec/tags/ruby/language/class_tags.txt
deleted file mode 100644
index 9a3022613f..0000000000
--- a/spec/tags/ruby/language/class_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2349):A class definition raises TypeError if the constant qualifying the class is nil
diff --git a/src/org/jruby/ast/Colon2Node.java b/src/org/jruby/ast/Colon2Node.java
index f67cac5ca2..a701e5bd7a 100644
--- a/src/org/jruby/ast/Colon2Node.java
+++ b/src/org/jruby/ast/Colon2Node.java
@@ -1,88 +1,90 @@
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /** 
  * Represents a '::' constant access or method call (Java::JavaClass).
  */
 public abstract class Colon2Node extends Colon3Node implements INameNode {
     protected final Node leftNode;
 
     public Colon2Node(ISourcePosition position, Node leftNode, String name) {
         super(position, name);
         this.leftNode = leftNode;
     }
 
     public NodeType getNodeType() {
         return NodeType.COLON2NODE;
     }
     
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     @Override
     public Object accept(NodeVisitor iVisitor) {
         return iVisitor.visitColon2Node(this);
     }
 
     /**
      * Gets the leftNode.
      * @return Returns a Node
      */
     public Node getLeftNode() {
         return leftNode;
     }
 
     @Override
     public List<Node> childNodes() {
         return Node.createList(leftNode);
     }
 
     /** Get parent module/class that this module represents */
     @Override
     public RubyModule getEnclosingModule(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
+    	if (leftNode != null && leftNode instanceof NilNode)
+        	throw context.getRuntime().newTypeError("no outer class/module");
         return RuntimeHelpers.prepareClassNamespace(context, leftNode.interpret(runtime, context, self, aBlock));
     }
  }
diff --git a/src/org/jruby/compiler/ASTCompiler.java b/src/org/jruby/compiler/ASTCompiler.java
index 6681447c58..2e2786d354 100644
--- a/src/org/jruby/compiler/ASTCompiler.java
+++ b/src/org/jruby/compiler/ASTCompiler.java
@@ -1,2006 +1,2012 @@
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
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
+
 import org.jruby.RubyInstanceConfig;
+import org.jruby.RubyMatchData;
 import org.jruby.ast.AliasNode;
 import org.jruby.ast.AndNode;
+import org.jruby.ast.ArgsCatNode;
 import org.jruby.ast.ArgsNode;
+import org.jruby.ast.ArgsPushNode;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.AttrAssignNode;
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BignumNode;
 import org.jruby.ast.BinaryOperatorNode;
 import org.jruby.ast.BlockNode;
+import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.CallNode;
+import org.jruby.ast.CaseNode;
+import org.jruby.ast.ClassNode;
 import org.jruby.ast.ClassVarAsgnNode;
+import org.jruby.ast.ClassVarDeclNode;
 import org.jruby.ast.ClassVarNode;
+import org.jruby.ast.Colon2ConstNode;
+import org.jruby.ast.Colon2MethodNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.Colon3Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.ConstNode;
 import org.jruby.ast.DAsgnNode;
+import org.jruby.ast.DRegexpNode;
 import org.jruby.ast.DStrNode;
+import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.DVarNode;
+import org.jruby.ast.DXStrNode;
 import org.jruby.ast.DefinedNode;
 import org.jruby.ast.DefnNode;
+import org.jruby.ast.DefsNode;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EnsureNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FCallNode;
+import org.jruby.ast.FileNode;
 import org.jruby.ast.FixnumNode;
+import org.jruby.ast.FlipNode;
 import org.jruby.ast.FloatNode;
+import org.jruby.ast.ForNode;
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
+import org.jruby.ast.ModuleNode;
+import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NewlineNode;
 import org.jruby.ast.NextNode;
+import org.jruby.ast.NilNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.NthRefNode;
-import org.jruby.ast.OpAsgnOrNode;
 import org.jruby.ast.OpAsgnNode;
+import org.jruby.ast.OpAsgnOrNode;
+import org.jruby.ast.OpElementAsgnNode;
 import org.jruby.ast.OrNode;
+import org.jruby.ast.PostExeNode;
+import org.jruby.ast.PreExeNode;
 import org.jruby.ast.RegexpNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.RootNode;
+import org.jruby.ast.SClassNode;
 import org.jruby.ast.SValueNode;
 import org.jruby.ast.SplatNode;
+import org.jruby.ast.StarNode;
 import org.jruby.ast.StrNode;
 import org.jruby.ast.SuperNode;
 import org.jruby.ast.SymbolNode;
-import org.jruby.ast.VCallNode;
-import org.jruby.ast.WhileNode;
-import org.jruby.ast.YieldNode;
-import org.jruby.runtime.Arity;
-import org.jruby.runtime.CallType;
-import org.jruby.exceptions.JumpException;
-import org.jruby.RubyMatchData;
-import org.jruby.ast.ArgsCatNode;
-import org.jruby.ast.ArgsPushNode;
-import org.jruby.ast.BlockPassNode;
-import org.jruby.ast.CaseNode;
-import org.jruby.ast.ClassNode;
-import org.jruby.ast.ClassVarDeclNode;
-import org.jruby.ast.Colon2ConstNode;
-import org.jruby.ast.Colon2MethodNode;
-import org.jruby.ast.DRegexpNode;
-import org.jruby.ast.DSymbolNode;
-import org.jruby.ast.DXStrNode;
-import org.jruby.ast.DefsNode;
-import org.jruby.ast.FileNode;
-import org.jruby.ast.FlipNode;
-import org.jruby.ast.ForNode;
-import org.jruby.ast.ModuleNode;
-import org.jruby.ast.MultipleAsgnNode;
-import org.jruby.ast.OpElementAsgnNode;
-import org.jruby.ast.PostExeNode;
-import org.jruby.ast.PreExeNode;
-import org.jruby.ast.SClassNode;
-import org.jruby.ast.StarNode;
 import org.jruby.ast.ToAryNode;
 import org.jruby.ast.UndefNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.VAliasNode;
+import org.jruby.ast.VCallNode;
 import org.jruby.ast.WhenNode;
 import org.jruby.ast.WhenOneArgNode;
+import org.jruby.ast.WhileNode;
 import org.jruby.ast.XStrNode;
+import org.jruby.ast.YieldNode;
 import org.jruby.ast.ZSuperNode;
+import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
+import org.jruby.runtime.Arity;
 import org.jruby.runtime.BlockBody;
+import org.jruby.runtime.CallType;
 
 /**
  *
  * @author headius
  */
 public class ASTCompiler {
     private boolean isAtRoot = true;
     
     public void compile(Node node, BodyCompiler context, boolean expr) {
         if (node == null) {
             if (expr) context.loadNil();
             return;
         }
         switch (node.getNodeType()) {
             case ALIASNODE:
                 compileAlias(node, context, expr);
                 break;
             case ANDNODE:
                 compileAnd(node, context, expr);
                 break;
             case ARGSCATNODE:
                 compileArgsCat(node, context, expr);
                 break;
             case ARGSPUSHNODE:
                 compileArgsPush(node, context, expr);
                 break;
             case ARRAYNODE:
                 compileArray(node, context, expr);
                 break;
             case ATTRASSIGNNODE:
                 compileAttrAssign(node, context, expr);
                 break;
             case BACKREFNODE:
                 compileBackref(node, context, expr);
                 break;
             case BEGINNODE:
                 compileBegin(node, context, expr);
                 break;
             case BIGNUMNODE:
                 compileBignum(node, context, expr);
                 break;
             case BLOCKNODE:
                 compileBlock(node, context, expr);
                 break;
             case BREAKNODE:
                 compileBreak(node, context, expr);
                 break;
             case CALLNODE:
                 compileCall(node, context, expr);
                 break;
             case CASENODE:
                 compileCase(node, context, expr);
                 break;
             case CLASSNODE:
                 compileClass(node, context, expr);
                 break;
             case CLASSVARNODE:
                 compileClassVar(node, context, expr);
                 break;
             case CLASSVARASGNNODE:
                 compileClassVarAsgn(node, context, expr);
                 break;
             case CLASSVARDECLNODE:
                 compileClassVarDecl(node, context, expr);
                 break;
             case COLON2NODE:
                 compileColon2(node, context, expr);
                 break;
             case COLON3NODE:
                 compileColon3(node, context, expr);
                 break;
             case CONSTDECLNODE:
                 compileConstDecl(node, context, expr);
                 break;
             case CONSTNODE:
                 compileConst(node, context, expr);
                 break;
             case DASGNNODE:
                 compileDAsgn(node, context, expr);
                 break;
             case DEFINEDNODE:
                 compileDefined(node, context, expr);
                 break;
             case DEFNNODE:
                 compileDefn(node, context, expr);
                 break;
             case DEFSNODE:
                 compileDefs(node, context, expr);
                 break;
             case DOTNODE:
                 compileDot(node, context, expr);
                 break;
             case DREGEXPNODE:
                 compileDRegexp(node, context, expr);
                 break;
             case DSTRNODE:
                 compileDStr(node, context, expr);
                 break;
             case DSYMBOLNODE:
                 compileDSymbol(node, context, expr);
                 break;
             case DVARNODE:
                 compileDVar(node, context, expr);
                 break;
             case DXSTRNODE:
                 compileDXStr(node, context, expr);
                 break;
             case ENSURENODE:
                 compileEnsureNode(node, context, expr);
                 break;
             case EVSTRNODE:
                 compileEvStr(node, context, expr);
                 break;
             case FALSENODE:
                 compileFalse(node, context, expr);
                 break;
             case FCALLNODE:
                 compileFCall(node, context, expr);
                 break;
             case FIXNUMNODE:
                 compileFixnum(node, context, expr);
                 break;
             case FLIPNODE:
                 compileFlip(node, context, expr);
                 break;
             case FLOATNODE:
                 compileFloat(node, context, expr);
                 break;
             case FORNODE:
                 compileFor(node, context, expr);
                 break;
             case GLOBALASGNNODE:
                 compileGlobalAsgn(node, context, expr);
                 break;
             case GLOBALVARNODE:
                 compileGlobalVar(node, context, expr);
                 break;
             case HASHNODE:
                 compileHash(node, context, expr);
                 break;
             case IFNODE:
                 compileIf(node, context, expr);
                 break;
             case INSTASGNNODE:
                 compileInstAsgn(node, context, expr);
                 break;
             case INSTVARNODE:
                 compileInstVar(node, context, expr);
                 break;
             case ITERNODE:
                 compileIter(node, context);
                 break;
             case LOCALASGNNODE:
                 compileLocalAsgn(node, context, expr);
                 break;
             case LOCALVARNODE:
                 compileLocalVar(node, context, expr);
                 break;
             case MATCH2NODE:
                 compileMatch2(node, context, expr);
                 break;
             case MATCH3NODE:
                 compileMatch3(node, context, expr);
                 break;
             case MATCHNODE:
                 compileMatch(node, context, expr);
                 break;
             case MODULENODE:
                 compileModule(node, context, expr);
                 break;
             case MULTIPLEASGNNODE:
                 compileMultipleAsgn(node, context, expr);
                 break;
             case NEWLINENODE:
                 compileNewline(node, context, expr);
                 break;
             case NEXTNODE:
                 compileNext(node, context, expr);
                 break;
             case NTHREFNODE:
                 compileNthRef(node, context, expr);
                 break;
             case NILNODE:
                 compileNil(node, context, expr);
                 break;
             case NOTNODE:
                 compileNot(node, context, expr);
                 break;
             case OPASGNANDNODE:
                 compileOpAsgnAnd(node, context, expr);
                 break;
             case OPASGNNODE:
                 compileOpAsgn(node, context, expr);
                 break;
             case OPASGNORNODE:
                 compileOpAsgnOr(node, context, expr);
                 break;
             case OPELEMENTASGNNODE:
                 compileOpElementAsgn(node, context, expr);
                 break;
             case ORNODE:
                 compileOr(node, context, expr);
                 break;
             case POSTEXENODE:
                 compilePostExe(node, context, expr);
                 break;
             case PREEXENODE:
                 compilePreExe(node, context, expr);
                 break;
             case REDONODE:
                 compileRedo(node, context, expr);
                 break;
             case REGEXPNODE:
                 compileRegexp(node, context, expr);
                 break;
             case RESCUEBODYNODE:
                 throw new NotCompilableException("rescue body is handled by rescue compilation at: " + node.getPosition());
             case RESCUENODE:
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
                 compileUndef(node, context, expr);
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
                 throw new NotCompilableException("Shouldn't get here; zeroarg does not do assignment: " + node);
             default:
                 throw new NotCompilableException("Can't compile assignment node: " + node);
         }
     }
 
     public void compileAlias(Node node, BodyCompiler context, boolean expr) {
         final AliasNode alias = (AliasNode) node;
 
         context.defineAlias(alias.getNewName(), alias.getOldName());
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
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
         
         if (doit) {
             ArrayCallback callback = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object sourceArray, int index) {
                             Node node = (Node) ((Object[]) sourceArray)[index];
                             compile(node, context, true);
                         }
                     };
 
             context.createNewArray(arrayNode.childNodes().toArray(), callback, arrayNode.isLightweight());
 
             if (popit) context.consumeCurrentValue();
         } else {
             for (Iterator<Node> iter = arrayNode.childNodes().iterator(); iter.hasNext();) {
                 Node nextNode = iter.next();
                 compile(nextNode, context, false);
             }
         }
     }
 
     public void compileArgsCat(Node node, BodyCompiler context, boolean expr) {
         ArgsCatNode argsCatNode = (ArgsCatNode) node;
 
         compile(argsCatNode.getFirstNode(), context,true);
         context.ensureRubyArray();
         compile(argsCatNode.getSecondNode(), context,true);
         context.splatCurrentValue();
         context.concatArrays();
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
 
         context.getInvocationCompiler().invokeAttrAssign(attrAssignNode.getName(), receiverCallback, argsCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileAttrAssignAssignment(Node node, BodyCompiler context, boolean expr) {
         final AttrAssignNode attrAssignNode = (AttrAssignNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(attrAssignNode.getReceiverNode(), context,true);
             }
         };
         ArgumentsCallback argsCallback = getArgsCallback(attrAssignNode.getArgsNode());
 
         context.getInvocationCompiler().invokeAttrAssignMasgn(attrAssignNode.getName(), receiverCallback, argsCallback);
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
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.createNewBignum(((BignumNode) node).getValue());
         } else {
             context.createNewBignum(((BignumNode) node).getValue());
             if (!expr) context.consumeCurrentValue();
         }
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
 
         if (argsCallback != null && argsCallback.getArity() == 1) {
             Node argument = callNode.getArgsNode().childNodes().get(0);
             if (name.length() == 1) {
                 switch (name.charAt(0)) {
                 case '+': case '-': case '<':
                     if (argument instanceof FixnumNode) {
                         context.getInvocationCompiler().invokeBinaryFixnumRHS(name, receiverCallback, ((FixnumNode)argument).getValue());
                         if (!expr) context.consumeCurrentValue();
                         return;
                     }
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
         
         context.getInvocationCompiler().invokeDynamic(
                 name, receiverCallback, argsCallback,
                 callType, closureArg, callNode.getIterNode() instanceof IterNode);
         
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
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
         if (switchType != null) {
             // NOTE: Currently this optimization is limited to the following situations:
             // * All expressions must be int-ranged literal fixnums
             // It also still emits the code for the "safe" when logic, which is rather
             // wasteful (since it essentially doubles each code body). As such it is
             // normally disabled, but it serves as an example of how this optimization
             // could be done. Ideally, it should be combined with the when processing
             // to improve code reuse before it's generally available.
             switchCases = new HashMap<CompilerCallback, int[]>();
         }
         for (Node node : whenNodes) {
             final WhenNode whenNode = (WhenNode)node;
             CompilerCallback body = new CompilerCallback() {
                 public void call(BodyCompiler context) {
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
-                                compile(leftNode, context, true);
+                                if (leftNode instanceof NilNode) {
+                                    context.raiseTypeError("No outer class");
+                                } else {
+                                    compile(leftNode, context, true);
+                                }
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
             compile(((Colon2Node) constNode).getLeftNode(), context,true);
             compile(constDeclNode.getValueNode(), context,true);
 
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
             // these are all simple cases that don't require the heavier defined logic
             compileGetDefinition(node, context);
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
             context.protect(reg, out, String.class);
         }
     }
 
     public void compileDefined(final Node node, BodyCompiler context, boolean expr) {
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) {
                 compileGetDefinitionBase(((DefinedNode) node).getExpressionNode(), context);
                 context.stringOrNil();
             }
         } else {
             compileGetDefinitionBase(((DefinedNode) node).getExpressionNode(), context);
             context.stringOrNil();
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileGetArgumentDefinition(final Node node, BodyCompiler context, String type) {
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
 
     public void compileGetDefinition(final Node node, BodyCompiler context) {
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
                 context.pushString("assignment");
                 break;
             case BACKREFNODE:
                 context.backref();
                 context.isInstanceOf(RubyMatchData.class,
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushString("$" + ((BackRefNode) node).getType());
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
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
                 context.pushString("local-variable");
                 break;
             case MATCH2NODE:
             case MATCH3NODE:
                 context.pushString("method");
                 break;
             case NILNODE:
                 context.pushString("nil");
                 break;
             case NTHREFNODE:
                 context.isCaptured(((NthRefNode) node).getMatchNumber(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushString("$" + ((NthRefNode) node).getMatchNumber());
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case SELFNODE:
                 context.pushString("self");
                 break;
             case VCALLNODE:
                 context.loadSelf();
                 context.isMethodBound(((VCallNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushString("method");
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
                                 context.pushString("yield");
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
                                 context.pushString("global-variable");
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
                                 context.pushString("instance-variable");
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
                                 context.pushString("constant");
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
                     BranchCallback isConstant = new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.pushString("constant");
                                 }
                             };
                     BranchCallback isMethod = new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.pushString("method");
                                 }
                             };
                     BranchCallback none = new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
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
                                                     compileGetArgumentDefinition(iVisited.getArgsNode(), context, "method");
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
 
                                 public void branch(BodyCompiler context) {
                                     context.consumeCurrentValue();
                                     context.pushString("class variable");
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
                                     context.pushString("class variable");
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
                             }, String.class);
 
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
                         }, String.class);
                 context.consumeCurrentValue();
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
diff --git a/src/org/jruby/compiler/BodyCompiler.java b/src/org/jruby/compiler/BodyCompiler.java
index 94f501de94..44d77de1ff 100644
--- a/src/org/jruby/compiler/BodyCompiler.java
+++ b/src/org/jruby/compiler/BodyCompiler.java
@@ -1,590 +1,592 @@
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
 
 package org.jruby.compiler;
 
 import java.util.List;
 import java.util.Map;
 import org.jruby.ast.NodeType;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.util.ByteList;
 
 /**
  *
  * @author headius
  */
 public interface BodyCompiler {
     /**
      * End compilation for the method associated with the specified token. This should
      * close out all structures created for compilation of the method.
      * 
      * @param token A token identifying the method to be terminated.
      */
     public void endBody();
     
     /**
      * As code executes, values are assumed to be "generated", often by being pushed
      * on to some execution stack. Generally, these values are consumed by other
      * methods on the context, but occasionally a value must be "thrown out". This method
      * provides a way to discard the previous value generated by some other call(s).
      */
     public void consumeCurrentValue();
     
     /**
      * Push a copy the topmost value on the stack.
      */
     public void duplicateCurrentValue();
     
     /**
      * For logging, println the object reference currently atop the stack
      */
     public void aprintln();
     
     /**
      * Swap the top and second values on the stack.
      */
     public void swapValues();
 
     /**
      * Reverse the top n values on the stack.
      *
      * @param n The number of values to reverse.
      */
     public void reverseValues(int n);
     
     /**
      * This method provides a way to specify a line number for the current piece of code
      * being compiled. The compiler may use this information to create debugging
      * information in a bytecode-format-dependent way.
      * 
      * @param position The ISourcePosition information to use.
      */
     public void lineNumber(ISourcePosition position);
     
     public VariableCompiler getVariableCompiler();
     
     public InvocationCompiler getInvocationCompiler();
     
     /**
      * Retrieve the current "self" and put a reference on top of the stack.
      */
     public void retrieveSelf();
     
     /**
      * Retrieve the current "self" object's metaclass and put a reference on top of the stack
      */
     public void retrieveSelfClass();
     
     public void retrieveClassVariable(String name);
     
     public void assignClassVariable(String name);
     
     public void assignClassVariable(String name, CompilerCallback value);
     
     public void declareClassVariable(String name);
     
     public void declareClassVariable(String name, CompilerCallback value);
     
     /**
      * Generate a new "Fixnum" value.
      */
     public void createNewFixnum(long value);
 
     /**
      * Generate a new "Float" value.
      */
     public void createNewFloat(double value);
 
     /**
      * Generate a new "Bignum" value.
      */
     public void createNewBignum(java.math.BigInteger value);
     
     /**
      * Generate a new "String" value.
      */
     public void createNewString(ByteList value);
 
     /**
      * Generate a new dynamic "String" value.
      */
     public void createNewString(ArrayCallback callback, int count);
     public void createNewSymbol(ArrayCallback callback, int count);
 
     /**
      * Generate a new "Symbol" value (or fetch the existing one).
      */
     public void createNewSymbol(String name);
     
     public void createObjectArray(Object[] elementArray, ArrayCallback callback);
 
     /**
      * Combine the top <pre>elementCount</pre> elements into a single element, generally
      * an array or similar construct. The specified number of elements are consumed and
      * an aggregate element remains.
      * 
      * @param elementCount The number of elements to consume
      */
     public void createObjectArray(int elementCount);
 
     /**
      * Given an aggregated set of objects (likely created through a call to createObjectArray)
      * create a Ruby array object.
      */
     public void createNewArray(boolean lightweight);
 
     /**
      * Given an aggregated set of objects (likely created through a call to createObjectArray)
      * create a Ruby array object. This version accepts an array of objects
      * to feed to an ArrayCallback to construct the elements of the array.
      */
     public void createNewArray(Object[] sourceArray, ArrayCallback callback, boolean lightweight);
 
     /**
      * Create an empty Ruby array
      */
     public void createEmptyArray();
     
     /**
      * Create an empty Ruby Hash object and put a reference on top of the stack.
      */
     public void createEmptyHash();
     
     /**
      * Create a new hash by calling back to the specified ArrayCallback. It is expected that the keyCount
      * will be the actual count of key/value pairs, and the caller will handle passing an appropriate elements
      * collection in and dealing with the sequential indices passed to the callback.
      * 
      * @param elements An object holding the elements from which to create the Hash.
      * @param callback An ArrayCallback implementation to which the elements array and iteration counts
      * are passed in sequence.
      * @param keyCount the total count of key-value pairs to be constructed from the elements collection.
      */
     public void createNewHash(Object elements, ArrayCallback callback, int keyCount);
     
     /**
      * Create a new range. It is expected that the stack will contain the end and begin values for the range as
      * its topmost and second topmost elements.
      * 
      * @param isExclusive Whether the range is exclusive or not (inclusive)
      */
     public void createNewRange(CompilerCallback beginEndCalback, boolean isExclusive);
 
     /**
      * Create a new literal lambda. The stack should contain a reference to the closure object.
      */
     public void createNewLambda(CompilerCallback closure);
     
     /**
      * Perform a boolean branch operation based on the Ruby "true" value of the top value
      * on the stack. If Ruby "true", invoke the true branch callback. Otherwise, invoke the false branch callback.
      * 
      * @param trueBranch The callback for generating code for the "true" condition
      * @param falseBranch The callback for generating code for the "false" condition
      */
     public void performBooleanBranch(BranchCallback trueBranch, BranchCallback falseBranch);
     
     /**
      * Perform a logical short-circuited Ruby "and" operation, using Ruby notions of true and false.
      * If the value on top of the stack is false, it remains and the branch is not executed. If it is true,
      * the top of the stack is replaced with the result of the branch.
      * 
      * @param longBranch The branch to execute if the "and" operation does not short-circuit.
      */
     public void performLogicalAnd(BranchCallback longBranch);
     
     
     /**
      * Perform a logical short-circuited Ruby "or" operation, using Ruby notions of true and false.
      * If the value on top of the stack is true, it remains and the branch is not executed. If it is false,
      * the top of the stack is replaced with the result of the branch.
      * 
      * @param longBranch The branch to execute if the "or" operation does not short-circuit.
      */
     public void performLogicalOr(BranchCallback longBranch);
     
     /**
      * Perform a boolean loop using the given condition-calculating branch and body branch. For
      * while loops, pass true for checkFirst. For statement-modifier while loops, pass false. For
      * unless loops, reverse the result of the condition after calculating it.
      * 
      * This version ensures the stack is maintained so while results can be used in any context.
      * 
      * @param condition The code to execute for calculating the loop condition. A Ruby true result will
      * cause the body to be executed again.
      * @param body The body to executed for the loop.
      * @param checkFirst whether to check the condition the first time through or not.
      */
     public void performBooleanLoopSafe(BranchCallback condition, BranchCallback body, boolean checkFirst);
     
     /**
      * Perform a boolean loop using the given condition-calculating branch and body branch. For
      * while loops, pass true for checkFirst. For statement-modifier while loops, pass false. For
      * unless loops, reverse the result of the condition after calculating it.
      * 
      * @param condition The code to execute for calculating the loop condition. A Ruby true result will
      * cause the body to be executed again.
      * @param body The body to executed for the loop.
      * @param checkFirst whether to check the condition the first time through or not.
      */
     public void performBooleanLoop(BranchCallback condition, BranchCallback body, boolean checkFirst);
     
     /**
      * Perform a boolean loop using the given condition-calculating branch and body branch. For
      * while loops, pass true for checkFirst. For statement-modifier while loops, pass false. For
      * unless loops, reverse the result of the condition after calculating it.
      * 
      * This version does not handle non-local flow control which can bubble out of
      * eval or closures, and only expects normal flow control to be used within
      * its body.
      * 
      * @param condition The code to execute for calculating the loop condition. A Ruby true result will
      * cause the body to be executed again.
      * @param body The body to executed for the loop.
      * @param checkFirst whether to check the condition the first time through or not.
      */
     public void performBooleanLoopLight(BranchCallback condition, BranchCallback body, boolean checkFirst);
     
     /**
      * Return the current value on the top of the stack, taking into consideration surrounding blocks.
      */
     public void performReturn();
 
     /**
      * Create a new closure (block) using the given lexical scope information, call arity, and
      * body generated by the body callback. The closure will capture containing scopes and related information.
      *
      * @param scope The static scoping information
      * @param arity The arity of the block's argument list
      * @param body The callback which will generate the closure's body
      */
     public void createNewClosure(int line, StaticScope scope, int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId, ASTInspector inspector);
 
     /**
      * Create a new closure (block) using the given lexical scope information, call arity, and
      * body generated by the body callback. The closure will capture containing scopes and related information.
      *
      * @param scope The static scoping information
      * @param arity The arity of the block's argument list
      * @param body The callback which will generate the closure's body
      */
     public void createNewClosure19(int line, StaticScope scope, int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId, ASTInspector inspector);
     
     /**
      * Create a new closure (block) for a for loop with the given call arity and
      * body generated by the body callback.
      * 
      * @param scope The static scoping information
      * @param arity The arity of the block's argument list
      * @param body The callback which will generate the closure's body
      */
     public void createNewForLoop(int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId, ASTInspector inspector);
     
     /**
      * Define a new method with the given name, arity, local variable count, and body callback.
      * This will create a new compiled method and bind it to the given name at this point in
      * the program's execution.
      * 
      * @param name The name to which to bind the resulting method.
      * @param arity The arity of the method's argument list
      * @param localVarCount The number of local variables within the method
      * @param body The callback which will generate the method's body.
      */
     public void defineNewMethod(String name, int methodArity, StaticScope scope,
             CompilerCallback body, CompilerCallback args,
             CompilerCallback receiver, ASTInspector inspector, boolean root);
     
     /**
      * Define an alias for a new name to an existing oldName'd method.
      * 
      * @param newName The new alias to create
      * @param oldName The name of the existing method or alias
      */
     public void defineAlias(String newName, String oldName);
     
     public void assignConstantInCurrent(String name);
     
     public void assignConstantInModule(String name);
     
     public void assignConstantInObject(String name);
     
     /**
      * Retrieve the constant with the specified name available at the current point in the
      * program's execution.
      * 
      * @param name The name of the constant
      */
     public void retrieveConstant(String name);
 
     /**
      * Retreive a named constant from the RubyModule/RubyClass that's just been pushed.
      * 
      * @param name The name of the constant
      */
     public void retrieveConstantFromModule(String name);
 
     /**
      * Retreive a named constant from the RubyModule/RubyClass that's just been pushed.
      *
      * @param name The name of the constant
      */
     public void retrieveConstantFromObject(String name);
     
     /**
      * Load a Ruby "false" value on top of the stack.
      */
     public void loadFalse();
     
     /**
      * Load a Ruby "true" value on top of the stack.
      */
     public void loadTrue();
     
     /**
      * Load a Ruby "nil" value on top of the stack.
      */
     public void loadNil();
     
     public void loadNull();
     
     /**
      * Load the Object class
      */
     public void loadObject();
     
     /**
      * Retrieve the instance variable with the given name, based on the current "self".
      * 
      * @param name The name of the instance variable to retrieve.
      */
     public void retrieveInstanceVariable(String name);
     
     /**
      * Assign the value on top of the stack to the instance variable with the specified name
      * on the current "self". The value is consumed.
      * 
      * @param name The name of the value to assign.
      */
     public void assignInstanceVariable(String name);
     
     /**
      * Assign the value on top of the stack to the instance variable with the specified name
      * on the current "self". The value is consumed.
      * 
      * @param name The name of the value to assign.
      * @param value A callback for compiling the value to assign
      */
     public void assignInstanceVariable(String name, CompilerCallback value);
     
     /**
      * Assign the top of the stack to the global variable with the specified name.
      * 
      * @param name The name of the global variable.
      */
     public void assignGlobalVariable(String name);
     
     /**
      * Assign the top of the stack to the global variable with the specified name.
      * 
      * @param name The name of the global variable.
      * @param value The callback to compile the value to assign
      */
     public void assignGlobalVariable(String name, CompilerCallback value);
     
     /**
      * Retrieve the global variable with the specified name to the top of the stack.
      * 
      * @param name The name of the global variable.
      */
     public void retrieveGlobalVariable(String name);
     
     /**
      * Perform a logical Ruby "not" operation on the value on top of the stack, leaving the
      * negated result.
      */
     public void negateCurrentValue();
     
     /**
      * Convert the current value into a "splatted value" suitable for passing as
      * method arguments or disassembling into multiple variables.
      */
     public void splatCurrentValue();
     
     /**
      * Given a splatted value, extract a single value. If no splat or length is
      * zero, use nil
      */
     public void singlifySplattedValue();
     
     /**
      * Given an IRubyObject[] on the stack (or otherwise available as the present object)
      * call back to the provided ArrayCallback 'callback' for 'count' elements, starting with 'start'.
      * Each call to callback will have a value from the input array on the stack; once the items are exhausted,
      * the code in nilCallback will be invoked *with no value on the stack*.
      */
     public void forEachInValueArray(int count, int start, Object source, ArrayCallback callback, CompilerCallback argsCallback);
 
     /**
      * Given an IRubyObject[] on the stack (or otherwise available as the present object)
      * call back to the provided ArrayCallback 'callback' for 'count' elements, starting with 'start'.
      * Each call to callback will have a value from the input array on the stack; once the items are exhausted,
      * the code in nilCallback will be invoked *with no value on the stack*.
      */
     public void forEachInValueArray(int count, int preSize, Object preSource, int postSize, Object postSource, ArrayCallback callback, CompilerCallback argsCallback);
     
     /**
      * Ensures that the present value is an IRubyObject[] by wrapping it with one if it is not.
      */
     public void ensureRubyArray();
     
     /**
      * Ensures that the present value is an IRubyObject[] by wrapping it with one or coercing it if it is not.
      */
     public void ensureMultipleAssignableRubyArray(boolean masgnHasHead);
     
     public void issueBreakEvent(CompilerCallback value);
     
     public void issueNextEvent(CompilerCallback value);
     
     public void issueRedoEvent();
     
     public void issueRetryEvent();
 
     public void asString();
 
     public void nthRef(int match);
 
     public void match();
 
     public void match2(CompilerCallback value);
 
     public void match3();
 
     public void createNewRegexp(ByteList value, int options);
     public void createNewRegexp(CompilerCallback createStringCallback, int options);
     
     public void pollThreadEvents();
 
     /**
      * Push the current back reference
      */
     public void backref();
     /**
      * Call a static helper method on RubyRegexp with the current backref 
      */
     public void backrefMethod(String methodName);
     
     public void nullToNil();
 
     /**
      * Makes sure that the code in protectedCode will always run after regularCode.
      */
     public void protect(BranchCallback regularCode, BranchCallback protectedCode, Class ret);
     public void rescue(BranchCallback regularCode, Class exception, BranchCallback protectedCode, Class ret);
     public void performRescue(BranchCallback regularCode, BranchCallback rubyCatchCode, boolean needsRetry);
     public void performRescueLight(BranchCallback regularCode, BranchCallback rubyCatchCode, boolean needsRetry);
     public void performEnsure(BranchCallback regularCode, BranchCallback ensuredCode);
     public void inDefined();
     public void outDefined();
     public void stringOrNil();
     public void pushNull();
     public void pushString(String strVal);
     public void isMethodBound(String name, BranchCallback trueBranch, BranchCallback falseBranch);
     public void hasBlock(BranchCallback trueBranch, BranchCallback falseBranch);
     public void isGlobalDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch);
     public void isConstantDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch);
     public void isInstanceVariableDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch);
     public void isClassVarDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch);
     public Object getNewEnding();
     public void ifNull(Object gotoToken);
     public void isNil(BranchCallback trueBranch, BranchCallback falseBranch);
     public void isNull(BranchCallback trueBranch, BranchCallback falseBranch);
     public void ifNotNull(Object gotoToken);
     public void setEnding(Object endingToken);
     public void go(Object gotoToken);
     public void isConstantBranch(BranchCallback setup, BranchCallback isConstant, BranchCallback isMethod, BranchCallback none, String name);
     public void metaclass();
     public void getVisibilityFor(String name);
     public void isPrivate(Object gotoToken, int toConsume);
     public void isNotProtected(Object gotoToken, int toConsume);
     public void selfIsKindOf(Object gotoToken);
     public void loadCurrentModule();
     public void notIsModuleAndClassVarDefined(String name, Object gotoToken);
     public void loadSelf();
     public void ifSingleton(Object gotoToken);
     public void getInstanceVariable(String name);
     public void getFrameName();
     public void getFrameKlazz(); 
     public void superClass();
     public void attached();    
     public void ifNotSuperMethodBound(Object token);
     public void isInstanceOf(Class clazz, BranchCallback trueBranch, BranchCallback falseBranch);
     public void isCaptured(int number, BranchCallback trueBranch, BranchCallback falseBranch);
     public void concatArrays();
     public void appendToArray();
     public void convertToJavaArray();
     public void aryToAry();
     public void toJavaString();
     public void aliasGlobal(String newName, String oldName);
     public void undefMethod(String name);
     public void defineClass(String name, StaticScope staticScope, CompilerCallback superCallback, CompilerCallback pathCallback, CompilerCallback bodyCallback, CompilerCallback receiverCallback, ASTInspector inspector);
     public void defineModule(String name, StaticScope staticScope, CompilerCallback pathCallback, CompilerCallback bodyCallback, ASTInspector inspector);
     public void unwrapPassedBlock();
     public void performBackref(char type);
     public void callZSuper(CompilerCallback closure);
     public void appendToObjectArray();
     public void checkIsExceptionHandled(ArgumentsCallback rescueArgs);
     public void rethrowException();
     public void loadClass(String name);
     public void loadStandardError();
     public void unwrapRaiseException();
     public void loadException();
     public void setFilePosition(ISourcePosition position);
     public void setLinePosition(ISourcePosition position);
     public void checkWhenWithSplat();
     public void createNewEndBlock(CompilerCallback body);
     public void runBeginBlock(StaticScope scope, CompilerCallback body);
     public void rethrowIfSystemExit();
 
     public BodyCompiler chainToMethod(String name);
     public BodyCompiler outline(String methodName);
     public void wrapJavaException();
     public void literalSwitch(int[] caseInts, Object[] caseBodies, ArrayCallback casesCallback, CompilerCallback defaultCallback);
     public void typeCheckBranch(Class type, BranchCallback trueCallback, BranchCallback falseCallback);
     public void loadFilename();
     public void storeExceptionInErrorInfo();
     public void clearErrorInfo();
 
     public void compileSequencedConditional(
             CompilerCallback inputValue,
             FastSwitchType fastSwitchType,
             Map<CompilerCallback, int[]> switchCases,
             List<ArgumentsCallback> conditionals,
             List<CompilerCallback> bodies,
             CompilerCallback fallback);
+
+    public void raiseTypeError(String string);
 }
diff --git a/src/org/jruby/compiler/impl/BaseBodyCompiler.java b/src/org/jruby/compiler/impl/BaseBodyCompiler.java
index a4829ab7ba..bfa67c38df 100644
--- a/src/org/jruby/compiler/impl/BaseBodyCompiler.java
+++ b/src/org/jruby/compiler/impl/BaseBodyCompiler.java
@@ -948,1594 +948,1601 @@ public abstract class BaseBodyCompiler implements BodyCompiler {
     public void getCompiledClass() {
         method.aload(StandardASMCompiler.THIS);
         method.getfield(script.getClassname(), "$class", ci(Class.class));
     }
 
     public void println() {
         method.dup();
         method.getstatic(p(System.class), "out", ci(PrintStream.class));
         method.swap();
 
         method.invokevirtual(p(PrintStream.class), "println", sig(Void.TYPE, params(Object.class)));
     }
 
     public void defineAlias(String newName, String oldName) {
         loadThreadContext();
         method.ldc(newName);
         method.ldc(oldName);
         invokeUtilityMethod("defineAlias", sig(IRubyObject.class, ThreadContext.class, String.class, String.class));
     }
 
     public void loadFalse() {
         // TODO: cache?
         loadRuntime();
         invokeRuby("getFalse", sig(RubyBoolean.class));
     }
 
     public void loadTrue() {
         // TODO: cache?
         loadRuntime();
         invokeRuby("getTrue", sig(RubyBoolean.class));
     }
 
     public void loadCurrentModule() {
         loadThreadContext();
         invokeThreadContext("getCurrentScope", sig(DynamicScope.class));
         method.invokevirtual(p(DynamicScope.class), "getStaticScope", sig(StaticScope.class));
         method.invokevirtual(p(StaticScope.class), "getModule", sig(RubyModule.class));
     }
 
     public void retrieveInstanceVariable(String name) {
         script.getCacheCompiler().cachedGetVariable(this, name);
     }
 
     public void assignInstanceVariable(String name) {
         final int tmp = getVariableCompiler().grabTempLocal();
         getVariableCompiler().setTempLocal(tmp);
         CompilerCallback callback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 context.getVariableCompiler().getTempLocal(tmp);
             }
         };
         script.getCacheCompiler().cachedSetVariable(this, name, callback);
     }
 
     public void assignInstanceVariable(String name, CompilerCallback value) {
         script.getCacheCompiler().cachedSetVariable(this, name, value);
     }
 
     public void retrieveGlobalVariable(String name) {
         loadRuntime();
         method.ldc(name);
         invokeUtilityMethod("getGlobalVariable", sig(IRubyObject.class, Ruby.class, String.class));
     }
 
     public void assignGlobalVariable(String name) {
         loadRuntime();
         method.ldc(name);
         invokeUtilityMethod("setGlobalVariable", sig(IRubyObject.class, IRubyObject.class, Ruby.class, String.class));
     }
 
     public void assignGlobalVariable(String name, CompilerCallback value) {
         value.call(this);
         loadRuntime();
         method.ldc(name);
         invokeUtilityMethod("setGlobalVariable", sig(IRubyObject.class, IRubyObject.class, Ruby.class, String.class));
     }
 
     public void negateCurrentValue() {
         loadRuntime();
         invokeUtilityMethod("negate", sig(IRubyObject.class, IRubyObject.class, Ruby.class));
     }
 
     public void splatCurrentValue() {
         method.invokestatic(p(RuntimeHelpers.class), "splatValue", sig(RubyArray.class, params(IRubyObject.class)));
     }
 
     public void singlifySplattedValue() {
         method.invokestatic(p(RuntimeHelpers.class), "aValueSplat", sig(IRubyObject.class, params(IRubyObject.class)));
     }
 
     public void aryToAry() {
         method.invokestatic(p(RuntimeHelpers.class), "aryToAry", sig(IRubyObject.class, params(IRubyObject.class)));
     }
 
     public void ensureRubyArray() {
         invokeUtilityMethod("ensureRubyArray", sig(RubyArray.class, params(IRubyObject.class)));
     }
 
     public void ensureMultipleAssignableRubyArray(boolean masgnHasHead) {
         loadRuntime();
         method.pushBoolean(masgnHasHead);
         invokeUtilityMethod("ensureMultipleAssignableRubyArray", sig(RubyArray.class, params(IRubyObject.class, Ruby.class, boolean.class)));
     }
 
     public void forEachInValueArray(int start, int count, Object source, ArrayCallback callback, CompilerCallback argsCallback) {
         if (start < count || argsCallback != null) {
             int tempLocal = getVariableCompiler().grabTempLocal();
             getVariableCompiler().setTempLocal(tempLocal);
             
             for (; start < count; start++) {
                 getVariableCompiler().getTempLocal(tempLocal);
                 switch (start) {
                 case 0:
                     invokeUtilityMethod("arrayEntryOrNilZero", sig(IRubyObject.class, RubyArray.class));
                     break;
                 case 1:
                     invokeUtilityMethod("arrayEntryOrNilOne", sig(IRubyObject.class, RubyArray.class));
                     break;
                 case 2:
                     invokeUtilityMethod("arrayEntryOrNilTwo", sig(IRubyObject.class, RubyArray.class));
                     break;
                 default:
                     method.pushInt(start);
                     invokeUtilityMethod("arrayEntryOrNil", sig(IRubyObject.class, RubyArray.class, int.class));
                     break;
                 }
                 callback.nextValue(this, source, start);
             }
 
             if (argsCallback != null) {
                 getVariableCompiler().getTempLocal(tempLocal);
                 loadRuntime();
                 method.pushInt(start);
                 invokeUtilityMethod("subarrayOrEmpty", sig(RubyArray.class, RubyArray.class, Ruby.class, int.class));
                 argsCallback.call(this);
             }
 
             getVariableCompiler().getTempLocal(tempLocal);
             getVariableCompiler().releaseTempLocal();
         }
     }
 
     public void forEachInValueArray(int start, int preCount, Object preSource, int postCount, Object postSource, ArrayCallback callback, CompilerCallback argsCallback) {
         if (start < preCount || argsCallback != null) {
             int tempLocal = getVariableCompiler().grabTempLocal();
             getVariableCompiler().setTempLocal(tempLocal);
 
             for (; start < preCount; start++) {
                 getVariableCompiler().getTempLocal(tempLocal);
                 switch (start) {
                 case 0:
                     invokeUtilityMethod("arrayEntryOrNilZero", sig(IRubyObject.class, RubyArray.class));
                     break;
                 case 1:
                     invokeUtilityMethod("arrayEntryOrNilOne", sig(IRubyObject.class, RubyArray.class));
                     break;
                 case 2:
                     invokeUtilityMethod("arrayEntryOrNilTwo", sig(IRubyObject.class, RubyArray.class));
                     break;
                 default:
                     method.pushInt(start);
                     invokeUtilityMethod("arrayEntryOrNil", sig(IRubyObject.class, RubyArray.class, int.class));
                     break;
                 }
                 callback.nextValue(this, preSource, start);
             }
 
             if (argsCallback != null) {
                 getVariableCompiler().getTempLocal(tempLocal);
                 loadRuntime();
                 method.pushInt(start);
                 invokeUtilityMethod("subarrayOrEmpty", sig(RubyArray.class, RubyArray.class, Ruby.class, int.class));
                 argsCallback.call(this);
             }
 
             if (postCount > 0) {
                 throw new NotCompilableException("1.9 mode can't handle post variables in masgn yet");
             }
 
             getVariableCompiler().getTempLocal(tempLocal);
             getVariableCompiler().releaseTempLocal();
         }
     }
 
     public void asString() {
         method.invokeinterface(p(IRubyObject.class), "asString", sig(RubyString.class));
     }
 
     public void toJavaString() {
         method.invokevirtual(p(Object.class), "toString", sig(String.class));
     }
 
     public void nthRef(int match) {
         method.pushInt(match);
         backref();
         method.invokestatic(p(RubyRegexp.class), "nth_match", sig(IRubyObject.class, params(Integer.TYPE, IRubyObject.class)));
     }
 
     public void match() {
         loadThreadContext();
         method.invokevirtual(p(RubyRegexp.class), "op_match2", sig(IRubyObject.class, params(ThreadContext.class)));
     }
 
     public void match2(CompilerCallback value) {
         loadThreadContext();
         value.call(this);
         method.invokevirtual(p(RubyRegexp.class), "op_match", sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class)));
     }
 
     public void match3() {
         loadThreadContext();
         invokeUtilityMethod("match3", sig(IRubyObject.class, RubyRegexp.class, IRubyObject.class, ThreadContext.class));
     }
 
     public void createNewRegexp(final ByteList value, final int options) {
         script.getCacheCompiler().cacheRegexp(this, value.toString(), options);
     }
 
     public void createNewRegexp(CompilerCallback createStringCallback, final int options) {
         boolean onceOnly = (options & ReOptions.RE_OPTION_ONCE) != 0;   // for regular expressions with the /o flag
 
         if (onceOnly) {
             script.getCacheCompiler().cacheDRegexp(this, createStringCallback, options);
         } else {
             loadRuntime();
             createStringCallback.call(this);
             method.pushInt(options);
             method.invokestatic(p(RubyRegexp.class), "newDRegexp", sig(RubyRegexp.class, params(Ruby.class, RubyString.class, int.class))); //[reg]
         }
     }
 
     public void pollThreadEvents() {
         if (!RubyInstanceConfig.THREADLESS_COMPILE_ENABLED) {
             loadThreadContext();
             invokeThreadContext("pollThreadEvents", sig(Void.TYPE));
         }
     }
 
     public void nullToNil() {
         Label notNull = new Label();
         method.dup();
         method.ifnonnull(notNull);
         method.pop();
         loadNil();
         method.label(notNull);
     }
 
     public void isInstanceOf(Class clazz, BranchCallback trueBranch, BranchCallback falseBranch) {
         method.instance_of(p(clazz));
 
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
 
             public void branch(BodyCompiler context) {
                 method.visitTypeInsn(CHECKCAST, p(RubyMatchData.class));
                 method.pushInt(number);
                 method.invokevirtual(p(RubyMatchData.class), "group", sig(IRubyObject.class, params(int.class)));
                 method.invokeinterface(p(IRubyObject.class), "isNil", sig(boolean.class));
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
 
             public void branch(BodyCompiler context) {
                 method.pop();
                 falseBranch.branch(context);
             }
         });
     }
 
     public void backref() {
         loadRuntime();
         loadThreadContext();
         invokeUtilityMethod("getBackref", sig(IRubyObject.class, Ruby.class, ThreadContext.class));
     }
 
     public void backrefMethod(String methodName) {
         backref();
         method.invokestatic(p(RubyRegexp.class), methodName, sig(IRubyObject.class, params(IRubyObject.class)));
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
         return "ensure_" + (script.getAndIncrementEnsureNumber()) + "$RUBY$__ensure__";
     }
 
     public void protect(BranchCallback regularCode, BranchCallback protectedCode, Class ret) {
         String mname = getNewEnsureName();
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(script.getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, Block.class}), null, null));
         SkinnyMethodAdapter old_method = null;
         SkinnyMethodAdapter var_old_method = null;
         SkinnyMethodAdapter inv_old_method = null;
         boolean oldInNestedMethod = inNestedMethod;
         inNestedMethod = true;
         Label[] oldLoopLabels = currentLoopLabels;
         currentLoopLabels = null;
         int oldArgCount = argParamCount;
         argParamCount = 0; // synthetic methods always have zero arg parameters
         try {
             old_method = this.method;
             var_old_method = getVariableCompiler().getMethodAdapter();
             inv_old_method = getInvocationCompiler().getMethodAdapter();
             this.method = mv;
             getVariableCompiler().setMethodAdapter(mv);
             getInvocationCompiler().setMethodAdapter(mv);
 
             mv.visitCode();
             // set up a local IRuby variable
 
             mv.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
             mv.dup();
             mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
             mv.dup();
             mv.astore(getRuntimeIndex());
 
             // grab nil for local variables
             mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             mv.astore(getNilIndex());
             mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
             mv.dup();
             mv.astore(getDynamicScopeIndex());
             mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
             mv.astore(getVarsArrayIndex());
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
             method.astore(getExceptionIndex());
             method.label(ensureEnd);
             protectedCode.branch(this);
 
             method.aload(getExceptionIndex());
             method.athrow();
 
             method.trycatch(codeBegin, codeEnd, ensureBegin, null);
             method.trycatch(ensureBegin, ensureEnd, ensureBegin, null);
             mv.visitMaxs(1, 1);
             mv.visitEnd();
         } finally {
             this.method = old_method;
             getVariableCompiler().setMethodAdapter(var_old_method);
             getInvocationCompiler().setMethodAdapter(inv_old_method);
             inNestedMethod = oldInNestedMethod;
             currentLoopLabels = oldLoopLabels;
             argParamCount = oldArgCount;
         }
 
         method.aload(StandardASMCompiler.THIS);
         loadThreadContext();
         loadSelf();
         if (this instanceof ChildScopedBodyCompiler) {
             pushNull();
         } else {
             loadBlock();
         }
         method.invokevirtual(script.getClassname(), mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, Block.class}));
     }
 
     public void performEnsure(BranchCallback regularCode, BranchCallback protectedCode) {
         String mname = getNewEnsureName();
         BaseBodyCompiler ensure = outline(mname);
         ensure.performEnsureInner(regularCode, protectedCode);
     }
 
     private void performEnsureInner(BranchCallback regularCode, BranchCallback protectedCode) {
         Label codeBegin = new Label();
         Label codeEnd = new Label();
         Label ensureBegin = new Label();
         Label ensureEnd = new Label();
         method.label(codeBegin);
 
         regularCode.branch(this);
 
         method.label(codeEnd);
 
         protectedCode.branch(this);
         method.areturn();
 
         method.label(ensureBegin);
         method.astore(getExceptionIndex());
         method.label(ensureEnd);
 
         protectedCode.branch(this);
 
         method.aload(getExceptionIndex());
         method.athrow();
 
         method.trycatch(codeBegin, codeEnd, ensureBegin, null);
         method.trycatch(ensureBegin, ensureEnd, ensureBegin, null);
 
         loadNil();
         endBody();
     }
 
     protected String getNewRescueName() {
         return "rescue_" + (script.getAndIncrementRescueNumber()) + "$RUBY$__rescue__";
     }
 
     public void storeExceptionInErrorInfo() {
         loadException();
         loadThreadContext();
         invokeUtilityMethod("storeExceptionInErrorInfo", sig(void.class, Throwable.class, ThreadContext.class));
     }
 
     public void clearErrorInfo() {
         loadThreadContext();
         invokeUtilityMethod("clearErrorInfo", sig(void.class, ThreadContext.class));
     }
 
     public void rescue(BranchCallback regularCode, Class exception, BranchCallback catchCode, Class ret) {
         String mname = getNewRescueName();
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(script.getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, Block.class}), null, null));
         SkinnyMethodAdapter old_method = null;
         SkinnyMethodAdapter var_old_method = null;
         SkinnyMethodAdapter inv_old_method = null;
         Label afterMethodBody = new Label();
         Label catchRetry = new Label();
         Label catchRaised = new Label();
         Label catchJumps = new Label();
         Label exitRescue = new Label();
         boolean oldWithinProtection = inNestedMethod;
         inNestedMethod = true;
         Label[] oldLoopLabels = currentLoopLabels;
         currentLoopLabels = null;
         int oldArgCount = argParamCount;
         argParamCount = 0; // synthetic methods always have zero arg parameters
         try {
             old_method = this.method;
             var_old_method = getVariableCompiler().getMethodAdapter();
             inv_old_method = getInvocationCompiler().getMethodAdapter();
             this.method = mv;
             getVariableCompiler().setMethodAdapter(mv);
             getInvocationCompiler().setMethodAdapter(mv);
 
             mv.start();
 
             // set up a local IRuby variable
             mv.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
             mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
             mv.astore(getRuntimeIndex());
 
             // store previous exception for restoration if we rescue something
             loadThreadContext();
             invokeThreadContext("getErrorInfo", sig(IRubyObject.class));
             mv.astore(getPreviousExceptionIndex());
 
             // grab nil for local variables
             loadRuntime();
             mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             mv.astore(getNilIndex());
 
             mv.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
             mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
             mv.astore(getDynamicScopeIndex());
 
             // if more than 4 vars, get values array too
             if (scope.getNumberOfVariables() > 4) {
                 mv.aload(getDynamicScopeIndex());
                 mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
                 mv.astore(getVarsArrayIndex());
             }
 
             Label beforeBody = new Label();
             Label afterBody = new Label();
             Label catchBlock = new Label();
             mv.trycatch(beforeBody, afterBody, catchBlock, p(exception));
             mv.label(beforeBody);
 
             regularCode.branch(this);
 
             mv.label(afterBody);
             mv.go_to(exitRescue);
             mv.label(catchBlock);
             mv.astore(getExceptionIndex());
 
             catchCode.branch(this);
 
             mv.label(afterMethodBody);
             mv.go_to(exitRescue);
 
             // retry handling in the rescue block
             mv.trycatch(catchBlock, afterMethodBody, catchRetry, p(JumpException.RetryJump.class));
             mv.label(catchRetry);
             mv.pop();
             mv.go_to(beforeBody);
 
             // any exceptions raised must continue to be raised, skipping $! restoration
             mv.trycatch(beforeBody, afterMethodBody, catchRaised, p(RaiseException.class));
             mv.label(catchRaised);
             mv.athrow();
 
             // and remaining jump exceptions should restore $!
             mv.trycatch(beforeBody, afterMethodBody, catchJumps, p(JumpException.class));
             mv.label(catchJumps);
             loadThreadContext();
             method.aload(getPreviousExceptionIndex());
             invokeThreadContext("setErrorInfo", sig(IRubyObject.class, IRubyObject.class));
             method.pop();
             mv.athrow();
 
             mv.label(exitRescue);
 
             // restore the original exception
             loadThreadContext();
             method.aload(getPreviousExceptionIndex());
             invokeThreadContext("setErrorInfo", sig(IRubyObject.class, IRubyObject.class));
             method.pop();
 
             mv.areturn();
             mv.end();
         } finally {
             inNestedMethod = oldWithinProtection;
             this.method = old_method;
             getVariableCompiler().setMethodAdapter(var_old_method);
             getInvocationCompiler().setMethodAdapter(inv_old_method);
             currentLoopLabels = oldLoopLabels;
             argParamCount = oldArgCount;
         }
 
         method.aload(StandardASMCompiler.THIS);
         loadThreadContext();
         loadSelf();
         if (this instanceof ChildScopedBodyCompiler) {
             pushNull();
         } else {
             loadBlock();
         }
         method.invokevirtual(script.getClassname(), mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, Block.class}));
     }
 
     public void performRescue(BranchCallback regularCode, BranchCallback rubyCatchCode, boolean needsRetry) {
         String mname = getNewRescueName();
         BaseBodyCompiler rescueMethod = outline(mname);
         rescueMethod.performRescueLight(regularCode, rubyCatchCode, needsRetry);
         rescueMethod.endBody();
     }
 
     public void performRescueLight(BranchCallback regularCode, BranchCallback rubyCatchCode, boolean needsRetry) {
         Label afterRubyCatchBody = new Label();
         Label catchRetry = new Label();
         Label catchJumps = new Label();
         Label exitRescue = new Label();
 
         // store previous exception for restoration if we rescue something
         loadThreadContext();
         invokeThreadContext("getErrorInfo", sig(IRubyObject.class));
         method.astore(getPreviousExceptionIndex());
 
         Label beforeBody = new Label();
         Label afterBody = new Label();
         Label rubyCatchBlock = new Label();
         Label flowCatchBlock = new Label();
         method.visitTryCatchBlock(beforeBody, afterBody, flowCatchBlock, p(JumpException.FlowControlException.class));
         method.visitTryCatchBlock(beforeBody, afterBody, rubyCatchBlock, p(Throwable.class));
 
         method.visitLabel(beforeBody);
         {
             regularCode.branch(this);
         }
         method.label(afterBody);
         method.go_to(exitRescue);
 
         // Handle Flow exceptions, just propagating them
         method.label(flowCatchBlock);
         {
             // rethrow to outer flow catcher
             method.athrow();
         }
 
         // Handle Ruby exceptions (RaiseException)
         method.label(rubyCatchBlock);
         {
             method.astore(getExceptionIndex());
 
             rubyCatchCode.branch(this);
             method.label(afterRubyCatchBody);
             method.go_to(exitRescue);
         }
 
         // retry handling in the rescue blocks
         if (needsRetry) {
             method.trycatch(rubyCatchBlock, afterRubyCatchBody, catchRetry, p(JumpException.RetryJump.class));
             method.label(catchRetry);
             {
                 method.pop();
             }
             method.go_to(beforeBody);
         }
 
         // and remaining jump exceptions should restore $!
         method.trycatch(beforeBody, afterRubyCatchBody, catchJumps, p(JumpException.FlowControlException.class));
         method.label(catchJumps);
         {
             loadThreadContext();
             method.aload(getPreviousExceptionIndex());
             invokeThreadContext("setErrorInfo", sig(IRubyObject.class, IRubyObject.class));
             method.pop();
             method.athrow();
         }
 
         method.label(exitRescue);
 
         // restore the original exception
         loadThreadContext();
         method.aload(getPreviousExceptionIndex());
         invokeThreadContext("setErrorInfo", sig(IRubyObject.class, IRubyObject.class));
         method.pop();
     }
 
     public void wrapJavaException() {
         loadRuntime();
         loadException();
         wrapJavaObject();
     }
 
     public void wrapJavaObject() {
         method.invokestatic(p(JavaUtil.class), "convertJavaToUsableRubyObject", sig(IRubyObject.class, Ruby.class, Object.class));
     }
 
     public void inDefined() {
         method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
         method.iconst_1();
         invokeThreadContext("setWithinDefined", sig(void.class, params(boolean.class)));
     }
 
     public void outDefined() {
         method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
         method.iconst_0();
         invokeThreadContext("setWithinDefined", sig(void.class, params(boolean.class)));
     }
 
     public void stringOrNil() {
         loadRuntime();
         loadNil();
         invokeUtilityMethod("stringOrNil", sig(IRubyObject.class, String.class, Ruby.class, IRubyObject.class));
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
         method.invokevirtual(p(RubyClass.class), "isMethodBound", sig(boolean.class, params(String.class, boolean.class)));
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
         method.invokevirtual(p(Block.class), "isGiven", sig(boolean.class));
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
         invokeRuby("getGlobalVariables", sig(GlobalVariables.class));
         method.ldc(name);
         method.invokevirtual(p(GlobalVariables.class), "isDefined", sig(boolean.class, params(String.class)));
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
         invokeThreadContext("getConstantDefined", sig(boolean.class, params(String.class)));
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
         invokeIRubyObject("getInstanceVariables", sig(InstanceVariables.class));
         method.ldc(name);
         //method.invokeinterface(p(IRubyObject.class), "getInstanceVariable", sig(IRubyObject.class, params(String.class)));
         method.invokeinterface(p(InstanceVariables.class), "fastHasInstanceVariable", sig(boolean.class, params(String.class)));
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
 
     public void isClassVarDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
         method.ldc(name);
         method.invokevirtual(p(RubyModule.class), "fastIsClassVarDefined", sig(boolean.class, params(String.class)));
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
         method.invokeinterface(p(IRubyObject.class), "isNil", sig(boolean.class));
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
         method.ifnull((Label) gotoToken);
     }
 
     public void ifNotNull(Object gotoToken) {
         method.ifnonnull((Label) gotoToken);
     }
 
     public void setEnding(Object endingToken) {
         method.label((Label) endingToken);
     }
 
     public void go(Object gotoToken) {
         method.go_to((Label) gotoToken);
     }
 
     public void isConstantBranch(final BranchCallback setup, final BranchCallback isConstant, final BranchCallback isMethod, final BranchCallback none, final String name) {
         rescue(new BranchCallback() {
 
             public void branch(BodyCompiler context) {
                 setup.branch(BaseBodyCompiler.this);
                 method.dup(); //[C,C]
                 method.instance_of(p(RubyModule.class)); //[C, boolean]
 
                 Label falseJmp = new Label();
                 Label afterJmp = new Label();
                 Label nextJmp = new Label();
                 Label nextJmpPop = new Label();
 
                 method.ifeq(nextJmp); // EQ == 0 (i.e. false)   //[C]
                 method.visitTypeInsn(CHECKCAST, p(RubyModule.class));
                 method.dup(); //[C, C]
                 method.ldc(name); //[C, C, String]
                 method.invokevirtual(p(RubyModule.class), "fastGetConstantAt", sig(IRubyObject.class, params(String.class))); //[C, null|C]
                 method.dup();
                 method.ifnull(nextJmpPop);
                 method.pop();
                 method.pop();
 
                 isConstant.branch(BaseBodyCompiler.this);
 
                 method.go_to(afterJmp);
 
                 method.label(nextJmpPop);
                 method.pop();
 
                 method.label(nextJmp); //[C]
 
                 metaclass();
                 method.ldc(name);
                 method.iconst_1(); // push true
                 method.invokevirtual(p(RubyClass.class), "isMethodBound", sig(boolean.class, params(String.class, boolean.class)));
                 method.ifeq(falseJmp); // EQ == 0 (i.e. false)
 
                 isMethod.branch(BaseBodyCompiler.this);
                 method.go_to(afterJmp);
 
                 method.label(falseJmp);
                 none.branch(BaseBodyCompiler.this);
 
                 method.label(afterJmp);
             }
         }, JumpException.class, none, String.class);
     }
 
     public void metaclass() {
         invokeIRubyObject("getMetaClass", sig(RubyClass.class));
     }
 
     public void aprintln() {
         method.aprintln();
     }
 
     public void getVisibilityFor(String name) {
         method.ldc(name);
         method.invokevirtual(p(RubyClass.class), "searchMethod", sig(DynamicMethod.class, params(String.class)));
         method.invokevirtual(p(DynamicMethod.class), "getVisibility", sig(Visibility.class));
     }
 
     public void isPrivate(Object gotoToken, int toConsume) {
         method.getstatic(p(Visibility.class), "PRIVATE", ci(Visibility.class));
         Label temp = new Label();
         method.if_acmpne(temp);
         while ((toConsume--) > 0) {
             method.pop();
         }
         method.go_to((Label) gotoToken);
         method.label(temp);
     }
 
     public void isNotProtected(Object gotoToken, int toConsume) {
         method.getstatic(p(Visibility.class), "PROTECTED", ci(Visibility.class));
         Label temp = new Label();
         method.if_acmpeq(temp);
         while ((toConsume--) > 0) {
             method.pop();
         }
         method.go_to((Label) gotoToken);
         method.label(temp);
     }
 
     public void selfIsKindOf(Object gotoToken) {
         method.invokevirtual(p(RubyClass.class), "getRealClass", sig(RubyClass.class));
         loadSelf();
         method.invokevirtual(p(RubyModule.class), "isInstance", sig(boolean.class, params(IRubyObject.class)));
         method.ifne((Label) gotoToken); // EQ != 0 (i.e. true)
     }
 
     public void notIsModuleAndClassVarDefined(String name, Object gotoToken) {
         method.dup(); //[?, ?]
         method.instance_of(p(RubyModule.class)); //[?, boolean]
         Label falsePopJmp = new Label();
         Label successJmp = new Label();
         method.ifeq(falsePopJmp);
 
         method.visitTypeInsn(CHECKCAST, p(RubyModule.class)); //[RubyModule]
         method.ldc(name); //[RubyModule, String]
 
         method.invokevirtual(p(RubyModule.class), "fastIsClassVarDefined", sig(boolean.class, params(String.class))); //[boolean]
         method.ifeq((Label) gotoToken);
         method.go_to(successJmp);
         method.label(falsePopJmp);
         method.pop();
         method.go_to((Label) gotoToken);
         method.label(successJmp);
     }
 
     public void ifSingleton(Object gotoToken) {
         method.invokevirtual(p(RubyModule.class), "isSingleton", sig(boolean.class));
         method.ifne((Label) gotoToken); // EQ == 0 (i.e. false)
     }
 
     public void getInstanceVariable(String name) {
         method.ldc(name);
         invokeIRubyObject("getInstanceVariables", sig(InstanceVariables.class));
         method.invokeinterface(p(InstanceVariables.class), "fastGetInstanceVariable", sig(IRubyObject.class, params(String.class)));
     }
 
     public void getFrameName() {
         loadThreadContext();
         invokeThreadContext("getFrameName", sig(String.class));
     }
 
     public void getFrameKlazz() {
         loadThreadContext();
         invokeThreadContext("getFrameKlazz", sig(RubyModule.class));
     }
 
     public void superClass() {
         method.invokevirtual(p(RubyModule.class), "getSuperClass", sig(RubyClass.class));
     }
 
     public void attached() {
         method.visitTypeInsn(CHECKCAST, p(MetaClass.class));
         method.invokevirtual(p(MetaClass.class), "getAttached", sig(IRubyObject.class));
     }
 
     public void ifNotSuperMethodBound(Object token) {
         method.swap();
         method.iconst_0();
         method.invokevirtual(p(RubyModule.class), "isMethodBound", sig(boolean.class, params(String.class, boolean.class)));
         method.ifeq((Label) token);
     }
 
     public void concatArrays() {
         method.invokevirtual(p(RubyArray.class), "concat", sig(RubyArray.class, params(IRubyObject.class)));
     }
 
     public void concatObjectArrays() {
         invokeUtilityMethod("concatObjectArrays", sig(IRubyObject[].class, params(IRubyObject[].class, IRubyObject[].class)));
     }
 
     public void appendToArray() {
         method.invokevirtual(p(RubyArray.class), "append", sig(RubyArray.class, params(IRubyObject.class)));
     }
 
     public void appendToObjectArray() {
         invokeUtilityMethod("appendToObjectArray", sig(IRubyObject[].class, params(IRubyObject[].class, IRubyObject.class)));
     }
 
     public void convertToJavaArray() {
         method.invokestatic(p(ArgsUtil.class), "convertToJavaArray", sig(IRubyObject[].class, params(IRubyObject.class)));
     }
 
     public void aliasGlobal(String newName, String oldName) {
         loadRuntime();
         invokeRuby("getGlobalVariables", sig(GlobalVariables.class));
         method.ldc(newName);
         method.ldc(oldName);
         method.invokevirtual(p(GlobalVariables.class), "alias", sig(Void.TYPE, params(String.class, String.class)));
         loadNil();
     }
+    
+    public void raiseTypeError(String msg) {
+        loadRuntime();        
+        method.ldc(msg);
+        invokeRuby("newTypeError", sig(RaiseException.class, params(String.class)));
+        method.athrow();                                    
+    }    
 
     public void undefMethod(String name) {
         loadThreadContext();
         invokeThreadContext("getRubyClass", sig(RubyModule.class));
 
         Label notNull = new Label();
         method.dup();
         method.ifnonnull(notNull);
         method.pop();
         loadRuntime();
         method.ldc("No class to undef method '" + name + "'.");
         invokeRuby("newTypeError", sig(RaiseException.class, params(String.class)));
         method.athrow();
 
         method.label(notNull);
         loadThreadContext();
         method.ldc(name);
         method.invokevirtual(p(RubyModule.class), "undef", sig(Void.TYPE, params(ThreadContext.class, String.class)));
 
         loadNil();
     }
 
     public void defineClass(
             final String name,
             final StaticScope staticScope,
             final CompilerCallback superCallback,
             final CompilerCallback pathCallback,
             final CompilerCallback bodyCallback,
             final CompilerCallback receiverCallback,
             final ASTInspector inspector) {
         String classMethodName = null;
         if (receiverCallback == null) {
             String mangledName = JavaNameMangler.mangleStringForCleanJavaIdentifier(name);
             classMethodName = "class_" + script.getAndIncrementMethodIndex() + "$RUBY$" + mangledName;
         } else {
             classMethodName = "sclass_" + script.getAndIncrementMethodIndex() + "$RUBY$__singleton__";
         }
 
         final RootScopedBodyCompiler classBody = new ClassBodyCompiler(script, classMethodName, inspector, staticScope);
 
         CompilerCallback bodyPrep = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 if (receiverCallback == null) {
                     // no receiver for singleton class
                     if (superCallback != null) {
                         // but there's a superclass passed in, use it
                         classBody.loadRuntime();
                         classBody.method.aload(StandardASMCompiler.SELF_INDEX);
 
                         classBody.invokeUtilityMethod("prepareSuperClass", sig(RubyClass.class, params(Ruby.class, IRubyObject.class)));
                     } else {
                         classBody.method.aconst_null();
                     }
 
                     classBody.loadThreadContext();
 
                     pathCallback.call(classBody);
 
                     classBody.invokeUtilityMethod("prepareClassNamespace", sig(RubyModule.class, params(ThreadContext.class, IRubyObject.class)));
 
                     classBody.method.swap();
 
                     classBody.method.ldc(name);
 
                     classBody.method.swap();
 
                     classBody.method.invokevirtual(p(RubyModule.class), "defineOrGetClassUnder", sig(RubyClass.class, params(String.class, RubyClass.class)));
                 } else {
                     classBody.loadRuntime();
 
                     // we re-set self to the class, but store the old self in a temporary local variable
                     // this is to prevent it GCing in case the singleton is short-lived
                     classBody.method.aload(StandardASMCompiler.SELF_INDEX);
                     int selfTemp = classBody.getVariableCompiler().grabTempLocal();
                     classBody.getVariableCompiler().setTempLocal(selfTemp);
                     classBody.method.aload(StandardASMCompiler.SELF_INDEX);
 
                     classBody.invokeUtilityMethod("getSingletonClass", sig(RubyClass.class, params(Ruby.class, IRubyObject.class)));
                 }
 
                 // set self to the class
                 classBody.method.dup();
                 classBody.method.astore(StandardASMCompiler.SELF_INDEX);
 
                 // CLASS BODY
                 classBody.loadThreadContext();
                 classBody.method.swap();
 
                 // static scope
                 script.getCacheCompiler().cacheStaticScope(classBody, staticScope);
                 if (inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                     classBody.invokeThreadContext("preCompiledClass", sig(Void.TYPE, params(RubyModule.class, StaticScope.class)));
                 } else {
                     classBody.invokeThreadContext("preCompiledClassDummyScope", sig(Void.TYPE, params(RubyModule.class, StaticScope.class)));
                 }
             }
         };
 
         // Here starts the logic for the class definition
         Label start = new Label();
         Label end = new Label();
         Label after = new Label();
         Label noException = new Label();
         classBody.method.trycatch(start, end, after, null);
 
         classBody.beginMethod(bodyPrep, staticScope);
 
         classBody.method.label(start);
 
         bodyCallback.call(classBody);
         classBody.method.label(end);
         // finally with no exception
         classBody.loadThreadContext();
         classBody.invokeThreadContext("postCompiledClass", sig(Void.TYPE, params()));
 
         classBody.method.go_to(noException);
 
         classBody.method.label(after);
         // finally with exception
         classBody.loadThreadContext();
         classBody.invokeThreadContext("postCompiledClass", sig(Void.TYPE, params()));
         classBody.method.athrow();
 
         classBody.method.label(noException);
 
         classBody.endBody();
 
         // prepare to call class definition method
         method.aload(StandardASMCompiler.THIS);
         loadThreadContext();
         if (receiverCallback == null) {
             // if there's no receiver, evaluate and pass in the superclass, or
             // pass self if it no superclass
             if (superCallback != null) {
                 superCallback.call(this);
             } else {
                 method.aload(StandardASMCompiler.SELF_INDEX);
             }
         } else {
             // otherwise, there's a receiver, so we pass that in directly for the sclass logic
             receiverCallback.call(this);
         }
         method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
 
         method.invokevirtual(script.getClassname(), classMethodName, StandardASMCompiler.METHOD_SIGNATURES[0]);
     }
 
     public void defineModule(final String name, final StaticScope staticScope, final CompilerCallback pathCallback, final CompilerCallback bodyCallback, final ASTInspector inspector) {
         String mangledName = JavaNameMangler.mangleStringForCleanJavaIdentifier(name);
         String moduleMethodName = "module__" + script.getAndIncrementMethodIndex() + "$RUBY$" + mangledName;
 
         final RootScopedBodyCompiler classBody = new ClassBodyCompiler(script, moduleMethodName, inspector, staticScope);
 
         CompilerCallback bodyPrep = new CompilerCallback() {
 
             public void call(BodyCompiler context) {
                 classBody.loadThreadContext();
 
                 pathCallback.call(classBody);
 
                 classBody.invokeUtilityMethod("prepareClassNamespace", sig(RubyModule.class, params(ThreadContext.class, IRubyObject.class)));
 
                 classBody.method.ldc(name);
 
                 classBody.method.invokevirtual(p(RubyModule.class), "defineOrGetModuleUnder", sig(RubyModule.class, params(String.class)));
 
                 // set self to the class
                 classBody.method.dup();
                 classBody.method.astore(StandardASMCompiler.SELF_INDEX);
 
                 // CLASS BODY
                 classBody.loadThreadContext();
                 classBody.method.swap();
 
                 // static scope
                 script.getCacheCompiler().cacheStaticScope(classBody, staticScope);
                 if (inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                     classBody.invokeThreadContext("preCompiledClass", sig(Void.TYPE, params(RubyModule.class, StaticScope.class)));
                 } else {
                     classBody.invokeThreadContext("preCompiledClassDummyScope", sig(Void.TYPE, params(RubyModule.class, StaticScope.class)));
                 }
             }
         };
 
         // Here starts the logic for the class definition
         Label start = new Label();
         Label end = new Label();
         Label after = new Label();
         Label noException = new Label();
         classBody.method.trycatch(start, end, after, null);
 
         classBody.beginMethod(bodyPrep, staticScope);
 
         classBody.method.label(start);
 
         bodyCallback.call(classBody);
         classBody.method.label(end);
 
         classBody.method.go_to(noException);
 
         classBody.method.label(after);
         classBody.loadThreadContext();
         classBody.invokeThreadContext("postCompiledClass", sig(Void.TYPE, params()));
         classBody.method.athrow();
 
         classBody.method.label(noException);
         classBody.loadThreadContext();
         classBody.invokeThreadContext("postCompiledClass", sig(Void.TYPE, params()));
 
         classBody.endBody();
 
         // prepare to call class definition method
         method.aload(StandardASMCompiler.THIS);
         loadThreadContext();
         loadSelf();
         method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
         method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
 
         method.invokevirtual(script.getClassname(), moduleMethodName, StandardASMCompiler.METHOD_SIGNATURES[4]);
     }
 
     public void unwrapPassedBlock() {
         loadBlock();
         invokeUtilityMethod("getBlockFromBlockPassBody", sig(Block.class, params(IRubyObject.class, Block.class)));
     }
 
     public void performBackref(char type) {
         loadThreadContext();
         switch (type) {
         case '~':
             invokeUtilityMethod("backref", sig(IRubyObject.class, params(ThreadContext.class)));
             break;
         case '&':
             invokeUtilityMethod("backrefLastMatch", sig(IRubyObject.class, params(ThreadContext.class)));
             break;
         case '`':
             invokeUtilityMethod("backrefMatchPre", sig(IRubyObject.class, params(ThreadContext.class)));
             break;
         case '\'':
             invokeUtilityMethod("backrefMatchPost", sig(IRubyObject.class, params(ThreadContext.class)));
             break;
         case '+':
             invokeUtilityMethod("backrefMatchLast", sig(IRubyObject.class, params(ThreadContext.class)));
             break;
         default:
             throw new NotCompilableException("ERROR: backref with invalid type");
         }
     }
 
     public void callZSuper(CompilerCallback closure) {
         ArgumentsCallback argsCallback = new ArgumentsCallback() {
             public int getArity() {
                 return -1;
             }
 
             public void call(BodyCompiler context) {
                 loadThreadContext();
                 invokeUtilityMethod("getArgValues", sig(IRubyObject[].class, ThreadContext.class));
             }
         };
         getInvocationCompiler().invokeDynamic(null, null, argsCallback, CallType.SUPER, closure, false);
     }
 
     public void checkIsExceptionHandled(ArgumentsCallback rescueArgs) {
         // original exception is on stack
         rescueArgs.call(this);
         loadThreadContext();
 
         switch (rescueArgs.getArity()) {
         case 1:
             invokeUtilityMethod("isJavaExceptionHandled", sig(IRubyObject.class, Throwable.class, IRubyObject.class, ThreadContext.class));
             break;
         case 2:
             invokeUtilityMethod("isJavaExceptionHandled", sig(IRubyObject.class, Throwable.class, IRubyObject.class, IRubyObject.class, ThreadContext.class));
             break;
         case 3:
             invokeUtilityMethod("isJavaExceptionHandled", sig(IRubyObject.class, Throwable.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, ThreadContext.class));
             break;
         default:
             invokeUtilityMethod("isJavaExceptionHandled", sig(IRubyObject.class, Throwable.class, IRubyObject[].class, ThreadContext.class));
         }
     }
 
     public void rethrowException() {
         loadException();
         method.athrow();
     }
 
     public void loadClass(String name) {
         loadRuntime();
         method.ldc(name);
         invokeRuby("getClass", sig(RubyClass.class, String.class));
     }
 
     public void loadStandardError() {
         loadRuntime();
         invokeRuby("getStandardError", sig(RubyClass.class));
     }
 
     public void unwrapRaiseException() {
         // RaiseException is on stack, get RubyException out
         method.invokevirtual(p(RaiseException.class), "getException", sig(RubyException.class));
     }
 
     public void loadException() {
         method.aload(getExceptionIndex());
     }
 
     public void setFilePosition(ISourcePosition position) {
         if (!RubyInstanceConfig.POSITIONLESS_COMPILE_ENABLED) {
             loadThreadContext();
             method.ldc(position.getFile());
             invokeThreadContext("setFile", sig(void.class, params(String.class)));
         }
     }
 
     public void setLinePosition(ISourcePosition position) {
         if (!RubyInstanceConfig.POSITIONLESS_COMPILE_ENABLED) {
             if (lastPositionLine == position.getStartLine()) {
                 // updating position for same line; skip
                 return;
             } else {
                 lastPositionLine = position.getStartLine();
                 loadThreadContext();
                 method.pushInt(position.getStartLine());
                 method.invokestatic(script.getClassname(), "setPosition", sig(void.class, params(ThreadContext.class, int.class)));
             }
         }
     }
 
     public void checkWhenWithSplat() {
         loadThreadContext();
         invokeUtilityMethod("isWhenTriggered", sig(RubyBoolean.class, IRubyObject.class, IRubyObject.class, ThreadContext.class));
     }
 
     public void issueRetryEvent() {
         invokeUtilityMethod("retryJump", sig(IRubyObject.class));
     }
 
     public void defineNewMethod(String name, int methodArity, StaticScope scope,
             CompilerCallback body, CompilerCallback args,
             CompilerCallback receiver, ASTInspector inspector, boolean root) {
         // TODO: build arg list based on number of args, optionals, etc
         String newMethodName;
         if (root && Boolean.getBoolean("jruby.compile.toplevel")) {
             newMethodName = name;
         } else {
             String mangledName = JavaNameMangler.mangleStringForCleanJavaIdentifier(name);
             newMethodName = "method__" + script.getAndIncrementMethodIndex() + "$RUBY$" + mangledName;
         }
 
         BodyCompiler methodCompiler = script.startMethod(name, newMethodName, args, scope, inspector);
 
         // callbacks to fill in method body
         body.call(methodCompiler);
 
         methodCompiler.endBody();
 
         // prepare to call "def" utility method to handle def logic
         loadThreadContext();
 
         loadSelf();
 
         if (receiver != null) {
             receiver.call(this);        // script object
         }
         method.aload(StandardASMCompiler.THIS);
 
         method.ldc(name);
 
         method.ldc(newMethodName);
 
         StandardASMCompiler.buildStaticScopeNames(method, scope);
 
         method.pushInt(methodArity);
 
         // arities
         method.pushInt(scope.getRequiredArgs());
         method.pushInt(scope.getOptionalArgs());
         method.pushInt(scope.getRestArg());
         method.getstatic(p(CallConfiguration.class), inspector.getCallConfig().name(), ci(CallConfiguration.class));
 
         if (receiver != null) {
             invokeUtilityMethod("defs", sig(IRubyObject.class,
                     params(ThreadContext.class, IRubyObject.class, IRubyObject.class, Object.class, String.class, String.class, String[].class, int.class, int.class, int.class, int.class, CallConfiguration.class)));
         } else {
             invokeUtilityMethod("def", sig(IRubyObject.class,
                     params(ThreadContext.class, IRubyObject.class, Object.class, String.class, String.class, String[].class, int.class, int.class, int.class, int.class, CallConfiguration.class)));
         }
     }
 
     public void rethrowIfSystemExit() {
         loadRuntime();
         method.ldc("SystemExit");
         method.invokevirtual(p(Ruby.class), "fastGetClass", sig(RubyClass.class, String.class));
         method.swap();
         method.invokevirtual(p(RubyModule.class), "isInstance", sig(boolean.class, params(IRubyObject.class)));
         method.iconst_0();
         Label ifEnd = new Label();
         method.if_icmpeq(ifEnd);
         loadException();
         method.athrow();
         method.label(ifEnd);
     }
 
     public void literalSwitch(int[] cases, Object[] bodies, ArrayCallback arrayCallback, CompilerCallback defaultCallback) {
         // TODO assuming case is a fixnum
         method.checkcast(p(RubyFixnum.class));
         method.invokevirtual(p(RubyFixnum.class), "getLongValue", sig(long.class));
         method.l2i();
 
         Map<Object, Label> labelMap = new HashMap<Object, Label>();
         Label[] labels = new Label[cases.length];
         for (int i = 0; i < labels.length; i++) {
             Object body = bodies[i];
             Label label = labelMap.get(body);
             if (label == null) {
                 label = new Label();
                 labelMap.put(body, label);
             }
             labels[i] = label;
         }
         Label defaultLabel = new Label();
         Label endLabel = new Label();
 
         method.lookupswitch(defaultLabel, cases, labels);
         Set<Label> labelDone = new HashSet<Label>();
         for (int i = 0; i < cases.length; i++) {
             if (labelDone.contains(labels[i])) continue;
             labelDone.add(labels[i]);
             method.label(labels[i]);
             arrayCallback.nextValue(this, bodies, i);
             method.go_to(endLabel);
         }
 
         method.label(defaultLabel);
         defaultCallback.call(this);
         method.label(endLabel);
     }
 
     public void typeCheckBranch(Class type, BranchCallback trueCallback, BranchCallback falseCallback) {
         Label elseLabel = new Label();
         Label done = new Label();
 
         method.dup();
         method.instance_of(p(type));
         method.ifeq(elseLabel);
 
         trueCallback.branch(this);
         method.go_to(done);
 
         method.label(elseLabel);
         falseCallback.branch(this);
 
         method.label(done);
     }
     
     public void loadFilename() {
         loadRuntime();
         loadThis();
         method.getfield(getScriptCompiler().getClassname(), "filename", ci(String.class));
         method.invokestatic(p(RubyString.class), "newString", sig(RubyString.class, Ruby.class, CharSequence.class));
     }
 
     public void compileSequencedConditional(
             CompilerCallback inputValue,
             FastSwitchType fastSwitchType,
             Map<CompilerCallback, int[]> switchCases,
             List<ArgumentsCallback> conditionals,
             List<CompilerCallback> bodies,
             CompilerCallback fallback) {
         Map<CompilerCallback, Label> bodyLabels = new HashMap<CompilerCallback, Label>();
         Label defaultCase = new Label();
         Label slowPath = new Label();
         CompilerCallback getCaseValue = null;
         final int tmp = getVariableCompiler().grabTempLocal();
         
         if (inputValue != null) {
             // we have an input case, prepare branching logic
             inputValue.call(this);
             getVariableCompiler().setTempLocal(tmp);
             getCaseValue = new CompilerCallback() {
                 public void call(BodyCompiler context) {
                     getVariableCompiler().getTempLocal(tmp);
                 }
             };
 
             if (switchCases != null) {
                 // we have optimized switch cases, build a lookupswitch
 
                 SortedMap<Integer, Label> optimizedLabels = new TreeMap<Integer, Label>();
                 for (Map.Entry<CompilerCallback, int[]> entry : switchCases.entrySet()) {
                     Label lbl = new Label();
 
                     bodyLabels.put(entry.getKey(), lbl);
                     
                     for (int i : entry.getValue()) {
                         optimizedLabels.put(i, lbl);
                     }
                 }
 
                 int[] caseValues = new int[optimizedLabels.size()];
                 Label[] caseLabels = new Label[optimizedLabels.size()];
                 Set<Map.Entry<Integer, Label>> entrySet = optimizedLabels.entrySet();
                 Iterator<Map.Entry<Integer, Label>> iterator = entrySet.iterator();
                 for (int i = 0; i < entrySet.size(); i++) {
                     Map.Entry<Integer, Label> entry = iterator.next();
                     caseValues[i] = entry.getKey();
                     caseLabels[i] = entry.getValue();
                 }
 
                 // checkcast the value; if match, fast path; otherwise proceed to slow logic
                 getCaseValue.call(this);
                 method.instance_of(p(fastSwitchType.getAssociatedClass()));
                 method.ifeq(slowPath);
 
                 switch (fastSwitchType) {
                 case FIXNUM:
                     getCaseValue.call(this);
                     method.checkcast(p(RubyFixnum.class));
                     method.invokevirtual(p(RubyFixnum.class), "getLongValue", sig(long.class));
                     method.l2i();
                     break;
                 case SINGLE_CHAR_STRING:
                     getCaseValue.call(this);
                     invokeUtilityMethod("isFastSwitchableSingleCharString", sig(boolean.class, IRubyObject.class));
                     method.ifeq(slowPath);
                     getCaseValue.call(this);
                     invokeUtilityMethod("getFastSwitchSingleCharString", sig(int.class, IRubyObject.class));
                     break;
                 case STRING:
                     getCaseValue.call(this);
                     invokeUtilityMethod("isFastSwitchableString", sig(boolean.class, IRubyObject.class));
                     method.ifeq(slowPath);
                     getCaseValue.call(this);
                     invokeUtilityMethod("getFastSwitchString", sig(int.class, IRubyObject.class));
                     break;
                 case SINGLE_CHAR_SYMBOL:
                     getCaseValue.call(this);
                     invokeUtilityMethod("isFastSwitchableSingleCharSymbol", sig(boolean.class, IRubyObject.class));
                     method.ifeq(slowPath);
                     getCaseValue.call(this);
                     invokeUtilityMethod("getFastSwitchSingleCharSymbol", sig(int.class, IRubyObject.class));
                     break;
                 case SYMBOL:
                     getCaseValue.call(this);
                     invokeUtilityMethod("isFastSwitchableSymbol", sig(boolean.class, IRubyObject.class));
                     method.ifeq(slowPath);
                     getCaseValue.call(this);
                     invokeUtilityMethod("getFastSwitchSymbol", sig(int.class, IRubyObject.class));
                     break;
                 }
 
                 method.lookupswitch(defaultCase, caseValues, caseLabels);
             }
         }
 
         Label done = new Label();
 
         // expression-based tests + bodies
         Label currentLabel = slowPath;
         for (int i = 0; i < conditionals.size(); i++) {
             ArgumentsCallback conditional = conditionals.get(i);
             CompilerCallback body = bodies.get(i);
 
             method.label(currentLabel);
 
             getInvocationCompiler().invokeEqq(conditional, getCaseValue);
             if (i + 1 < conditionals.size()) {
                 // normal case, create a new label
                 currentLabel = new Label();
             } else {
                 // last conditional case, use defaultCase
                 currentLabel = defaultCase;
             }
             method.ifeq(currentLabel);
 
             Label bodyLabel = bodyLabels.get(body);
             if (bodyLabel != null) method.label(bodyLabel);
 
             body.call(this);
 
             method.go_to(done);
         }
 
         // "else" body
         method.label(currentLabel);
         fallback.call(this);
 
         method.label(done);
 
         getVariableCompiler().releaseTempLocal();
     }
 }
\ No newline at end of file
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index 3fedd85330..700f21d0af 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -1,1580 +1,1580 @@
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
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, arg0, arg1, arg2, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject[] args, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, args);
         return args[args.length - 1];
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject[] args, IRubyObject value, ThreadContext context, IRubyObject caller) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 0, args.length);
         newArgs[args.length] = value;
         callSite.call(context, caller, receiver, newArgs);
         return value;
     }
 
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
         return new MethodMissingMethod(methodMissing);
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
         return new MethodMissingMethod(methodMissing);
     }
 
     private static class MethodMissingMethod extends DynamicMethod {
         private DynamicMethod delegate;
 
         public MethodMissingMethod(DynamicMethod delegate) {
             this.delegate = delegate;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
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
-        if (rubyModule == null || rubyModule.isNil()) { // the isNil check should go away since class nil::Foo;end is not supposed be correct
+        if (rubyModule == null || rubyModule.isNil()) {
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
 
     private static boolean checkJavaException(Throwable currentThrowable, IRubyObject throwable) {
         if (throwable instanceof RubyClass) {
             RubyClass rubyClass = (RubyClass)throwable;
             JavaClass javaClass = (JavaClass)rubyClass.fastGetInstanceVariable("@java_class");
             if (javaClass != null) {
                 Class cls = javaClass.javaClass();
                 if (cls.isInstance(currentThrowable)) {
                     return true;
                 }
             }
         }
         return false;
     }
     
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject[] throwables, ThreadContext context) {
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwables, context);
         } else {
             for (int i = 0; i < throwables.length; i++) {
                 if (checkJavaException(currentThrowable, throwables[0])) {
                     return context.getRuntime().getTrue();
                 }
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable, ThreadContext context) {
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable, context);
         } else {
             if (checkJavaException(currentThrowable, throwable)) {
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable0, IRubyObject throwable1, ThreadContext context) {
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable0, throwable1, context);
         } else {
             if (checkJavaException(currentThrowable, throwable0)) {
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentThrowable, throwable1)) {
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable0, IRubyObject throwable1, IRubyObject throwable2, ThreadContext context) {
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable0, throwable1, throwable2, context);
         } else {
             if (checkJavaException(currentThrowable, throwable0)) {
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentThrowable, throwable1)) {
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentThrowable, throwable2)) {
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
         hash.fastASet(key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASet(key1, value1);
         hash.fastASet(key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASet(key1, value1);
         hash.fastASet(key2, value2);
         hash.fastASet(key3, value3);
         return hash;
     }
     
     public static IRubyObject defineAlias(ThreadContext context, String newName, String oldName) {
         Ruby runtime = context.getRuntime();
         RubyModule module = context.getRubyClass();
    
         if (module == null) throw runtime.newTypeError("no class to make alias");
    
         module.defineAlias(newName, oldName);
         module.callMethod(context, "method_added", runtime.newSymbol(newName));
    
         return runtime.getNil();
     }
     
     public static IRubyObject negate(IRubyObject value, Ruby runtime) {
         if (value.isTrue()) return runtime.getFalse();
         return runtime.getTrue();
     }
     
     public static IRubyObject stringOrNil(String value, Ruby runtime, IRubyObject nil) {
         if (value == null) return nil;
         return RubyString.newString(runtime, value);
     }
     
     public static void preLoad(ThreadContext context, String[] varNames) {
         StaticScope staticScope = new LocalStaticScope(null, varNames);
         staticScope.setModule(context.getRuntime().getObject());
         DynamicScope scope = DynamicScope.newDynamicScope(staticScope);
         
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
     }
     
     public static void postLoad(ThreadContext context) {
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
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
             // remove this hack too.
             Ruby runtime = value.getRuntime();
             
             if (value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime.getKernel()) {
                 value = value.callMethod(runtime.getCurrentContext(), "to_a");
                 if (!(value instanceof RubyArray)) throw runtime.newTypeError("`to_a' did not return Array");
                 return (RubyArray)value;
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
 
     public static RubyArray splatValue(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newArray(value);
         }
 
         return arrayValue(value);
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
 
     private static DynamicMethod constructNormalMethod(String name, Visibility visibility, MethodFactory factory, RubyModule containingClass, String javaName, int arity, StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
         DynamicMethod method;
 
         if (name.equals("initialize") || name.equals("initialize_copy") || visibility == Visibility.MODULE_FUNCTION) {
             visibility = Visibility.PRIVATE;
         }
         
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
             method = factory.getCompiledMethodLazily(containingClass, javaName, Arity.createArity(arity), visibility, scope, scriptObject, callConfig);
         } else {
             method = factory.getCompiledMethod(containingClass, javaName, Arity.createArity(arity), visibility, scope, scriptObject, callConfig);
         }
 
         return method;
     }
 
     private static DynamicMethod constructSingletonMethod(MethodFactory factory, RubyClass rubyClass, String javaName, int arity, StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
             return factory.getCompiledMethodLazily(rubyClass, javaName, Arity.createArity(arity), Visibility.PUBLIC, scope, scriptObject, callConfig);
         } else {
             return factory.getCompiledMethod(rubyClass, javaName, Arity.createArity(arity), Visibility.PUBLIC, scope, scriptObject, callConfig);
         }
     }
 
     private static StaticScope creatScopeForClass(ThreadContext context, String[] scopeNames, int required, int optional, int rest) {
 
         StaticScope scope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), scopeNames);
         scope.determineModule();
         scope.setArities(required, optional, rest);
 
         return scope;
     }
 
     private static void performNormalMethodChecks(RubyModule containingClass, Ruby runtime, String name) throws RaiseException {
 
         if (containingClass == runtime.getDummy()) {
             throw runtime.newTypeError("no class/module to add method");
         }
 
         if (containingClass == runtime.getObject() && name.equals("initialize")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining Object#initialize may cause infinite loop", "Object#initialize");
         }
 
         if (name.equals("__id__") || name.equals("__send__")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining `" + name + "' may cause serious problem", name);
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
     
     public static RubyArray subarrayOrEmpty(RubyArray array, Ruby runtime, int index) {
         if (index < array.getLength()) {
             return createSubarray(array, index);
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
