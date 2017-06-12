diff --git a/src/org/jruby/ast/AttrAssignNode.java b/src/org/jruby/ast/AttrAssignNode.java
index 5397d6c43c..e7401a2fb7 100644
--- a/src/org/jruby/ast/AttrAssignNode.java
+++ b/src/org/jruby/ast/AttrAssignNode.java
@@ -1,101 +1,107 @@
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
  * Copyright (C) 2006-2007 Thomas E Enebo <enebo@acm.org>
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
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Node that represents an assignment of either an array element or attribute.
  */
 public class AttrAssignNode extends Node implements INameNode, IArgumentNode {
     private final Node receiverNode;
     private String name;
     private Node argsNode;
 
     public AttrAssignNode(ISourcePosition position, Node receiverNode, String name, Node argsNode) {
         super(position, NodeType.ATTRASSIGNNODE);
         
         this.receiverNode = receiverNode;
         this.name = name;
         this.argsNode = argsNode;
+        if (argsNode instanceof ArrayNode) {
+            ((ArrayNode)argsNode).setLightweight(true);
+        }
     }
 
     /**
      * Accept for the visitor pattern.
      * @param visitor the visitor
      **/
     public Instruction accept(NodeVisitor visitor) {
         return visitor.visitAttrAssignNode(this);
     }
 
     /**
      * Gets the name.
      * name is the name of the method called
      * @return name
      */
     public String getName() {
         return name;
     }
     
     /**
      * Gets the receiverNode.
      * receiverNode is the object on which the method is being called
      * @return receiverNode
      */
     public Node getReceiverNode() {
         return receiverNode;
     }
     
     /**
      * Gets the argsNode.
      * argsNode representing the method's arguments' value for this call.
      * @return argsNode
      */
     public Node getArgsNode() {
         return argsNode;
     }
     
     /**
      * Set the argsNode
      * 
      * @param argsNode set the arguments for this node.
      */
     public void setArgsNode(Node argsNode) {
         this.argsNode = argsNode;
+        if (argsNode instanceof ArrayNode) {
+            ((ArrayNode)argsNode).setLightweight(true);
+        }
     }
 
     public List<Node> childNodes() {
         return Node.createList(receiverNode, argsNode);
     }
 }
diff --git a/src/org/jruby/compiler/ASTCompiler.java b/src/org/jruby/compiler/ASTCompiler.java
index 38a3e5865c..3ea0fb532c 100644
--- a/src/org/jruby/compiler/ASTCompiler.java
+++ b/src/org/jruby/compiler/ASTCompiler.java
@@ -1,1665 +1,1637 @@
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
 import org.jruby.runtime.BlockBody;
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
     
     public class VariableArityArguments implements ArgumentsCallback {
         private Node node;
         
         public VariableArityArguments(Node node) {
             this.node = node;
         }
         
         public int getArity() {
             return -1;
         }
         
         public void call(MethodCompiler context) {
             compileArguments(node, context);
         }
     }
     
     public class SpecificArityArguments implements ArgumentsCallback {
         private int arity;
         private Node node;
         
         public SpecificArityArguments(Node node) {
             if (node.nodeId == NodeType.ARRAYNODE && ((ArrayNode)node).isLightweight()) {
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
         
         public void call(MethodCompiler context) {
             if (node.nodeId == NodeType.ARRAYNODE) {
                 ArrayNode arrayNode = (ArrayNode)node;
                 if (arrayNode.isLightweight()) {
                     // explode array, it's an internal "args" array
                     for (Node n : arrayNode.childNodes()) {
                         compile(n, context);
                     }
                 } else {
                     // use array as-is, it's a literal array
                     compile(arrayNode, context);
                 }
             } else {
                 compile(node, context);
             }
         }
     }
 
     public ArgumentsCallback getArgsCallback(Node node) {
         if (node == null) {
             return null;
         }
         // unwrap newline nodes to get their actual type
         while (node.nodeId == NodeType.NEWLINENODE) {
             node = ((NewlineNode)node).getNextNode();
         }
         switch (node.nodeId) {
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
 
-    public void compileAttrAssign(Node node, MethodCompiler context) {
-        context.lineNumber(node.getPosition());
-
-        AttrAssignNode attrAssignNode = (AttrAssignNode) node;
-        
-        // if one arg, it's a simple attr assign
-        if (attrAssignNode.getArgsNode().childNodes().size() == 1) {
-            compileAttrAssignSimple(attrAssignNode, context);
-        } else {
-            // more complex attr assign, as in a[1,2] = 3
-            compile(attrAssignNode.getReceiverNode(), context);
-            compileArguments(attrAssignNode.getArgsNode(), context);
-
-            context.getInvocationCompiler().invokeAttrAssign(attrAssignNode.getName());
-        }
-    }
-
-    private void compileAttrAssignSimple(Node node, MethodCompiler context) {
+    private void compileAttrAssign(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final AttrAssignNode attrAssignNode = (AttrAssignNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(MethodCompiler context) {
                 compile(attrAssignNode.getReceiverNode(), context);
             }
         };
         
-        assert attrAssignNode.getArgsNode() instanceof ArrayNode : "attr assign with non-array args node";
-        ArrayNode arrayNode = (ArrayNode)attrAssignNode.getArgsNode();
-        
-        ArgumentsCallback argsCallback = null;
-        if (arrayNode.get(0) == null) {
-            argsCallback = new ArgumentsCallback() {
-                public int getArity() { return 1; }
-                public void call(MethodCompiler context) { context.loadNil(); }
-            };
-        } else {
-            argsCallback = getArgsCallback(arrayNode.get(0));
-        }
+        ArgumentsCallback argsCallback = getArgsCallback(attrAssignNode.getArgsNode());
 
-        context.getInvocationCompiler().invokeAttrAssignSimple(attrAssignNode.getName(), receiverCallback, argsCallback);
+        context.getInvocationCompiler().invokeAttrAssign(attrAssignNode.getName(), receiverCallback, argsCallback);
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
 
         ArgumentsCallback argsCallback = getArgsCallback(callNode.getArgsNode());
         CompilerCallback closureArg = getBlock(callNode.getIterNode());
 
         context.getInvocationCompiler().invokeDynamic(callNode.getName(), receiverCallback, argsCallback, CallType.NORMAL, closureArg, false);
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
 
                             public void branch(MethodCompiler context) {
                                 context.pushString("$" + ((NthRefNode) node).getMatchNumber());
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
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
 
                             public void branch(MethodCompiler context) {
                                 context.pushString("method");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case YIELDNODE:
                 context.hasBlock(new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 context.pushString("yield");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case GLOBALVARNODE:
                 context.isGlobalDefined(((GlobalVarNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 context.pushString("global-variable");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case INSTVARNODE:
                 context.isInstanceVariableDefined(((InstVarNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 context.pushString("instance-variable");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case CONSTNODE:
                 context.isConstantDefined(((ConstNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 context.pushString("constant");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case FCALLNODE:
                 context.loadSelf();
                 context.isMethodBound(((FCallNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
                                 compileGetArgumentDefinition(((FCallNode) node).getArgsNode(), context, "method");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(MethodCompiler context) {
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
 
                                 public void branch(MethodCompiler context) {
                                     if (iVisited instanceof Colon2Node) {
                                         final Node leftNode = ((Colon2Node) iVisited).getLeftNode();
                                         compile(leftNode, context);
                                     } else {
                                         context.loadObject();
                                     }
                                 }
                             };
                     BranchCallback isConstant = new BranchCallback() {
 
                                 public void branch(MethodCompiler context) {
                                     context.pushString("constant");
                                 }
                             };
                     BranchCallback isMethod = new BranchCallback() {
 
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
diff --git a/src/org/jruby/compiler/InvocationCompiler.java b/src/org/jruby/compiler/InvocationCompiler.java
index 1fdad50746..0f6930c905 100644
--- a/src/org/jruby/compiler/InvocationCompiler.java
+++ b/src/org/jruby/compiler/InvocationCompiler.java
@@ -1,70 +1,70 @@
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
 
 import org.jruby.runtime.CallType;
 
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 
 /**
  *
  * @author headius
  */
 public interface InvocationCompiler {
     public SkinnyMethodAdapter getMethodAdapter();
     public void setMethodAdapter(SkinnyMethodAdapter sma);
     /**
      * Invoke the named method as a "function", i.e. as a method on the current "self"
      * object, using the specified argument count. It is expected that previous calls
      * to the compiler has prepared the exact number of argument values necessary for this
      * call. Those values will be consumed, and the result of the call will be generated.
      */
     public void invokeDynamic(String name, CompilerCallback receiverCallback, ArgumentsCallback argsCallback, CallType callType, CompilerCallback closureArg, boolean attrAssign);
     
     public void invokeSuper(CompilerCallback argsCallback, CompilerCallback closureCallback);
     
     /**
      * Attr assign calls have slightly different semantics that normal calls, so this method handles those additional semantics.
      */
     public void invokeAttrAssign(String name);
-    public void invokeAttrAssignSimple(String name, CompilerCallback receiverCallback, ArgumentsCallback argsCallback);
+    public void invokeAttrAssign(String name, CompilerCallback receiverCallback, ArgumentsCallback argsCallback);
     
     public void opElementAsgn(CompilerCallback valueCallback, String operator);
     
     /**
      * Invoke the block passed into this method, or throw an error if no block is present.
      * If arguments have been prepared for the block, specify true. Otherwise the default
      * empty args will be used.
      */
     public void yield(boolean hasArgs, boolean unwrap);
     
     /**
      * Used for when nodes with a case; assumes stack is ..., case_value, when_cond_array
      */
     public void invokeEqq();
 }
diff --git a/src/org/jruby/compiler/VariableCompiler.java b/src/org/jruby/compiler/VariableCompiler.java
index 249a7f9bd1..84d7847748 100644
--- a/src/org/jruby/compiler/VariableCompiler.java
+++ b/src/org/jruby/compiler/VariableCompiler.java
@@ -1,64 +1,68 @@
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
 
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 
 /**
  *
  * @author headius
  */
 public interface VariableCompiler {
     public SkinnyMethodAdapter getMethodAdapter();
     public void setMethodAdapter(SkinnyMethodAdapter sma);
     public void beginMethod(CompilerCallback argsCallback, StaticScope scope);
     public void beginClass(CompilerCallback bodyPrep, StaticScope scope);
     public void beginClosure(CompilerCallback argsCallback, StaticScope scope);
     public void assignLocalVariable(int index);
     public void retrieveLocalVariable(int index);
     public void assignLastLine();
     public void retrieveLastLine();
     public void retrieveBackRef();
     public void assignLocalVariable(int index, int depth);
     public void retrieveLocalVariable(int index, int depth);
     public void checkMethodArity(int requiredArgs, int optArgs, int restArg);
     public void assignMethodArguments(
             Object requiredArgs,
             int requiredArgsCount,
             Object optArgs,
             int optArgsCount,
             ArrayCallback requiredAssignment,
             ArrayCallback optGivenAssignment,
             ArrayCallback optNotGivenAssignment,
             CompilerCallback restAssignment,
             CompilerCallback blockAssignment);
+    public int grabTempLocal();
+    public void setTempLocal(int index);
+    public void getTempLocal(int index);
+    public void releaseTempLocal();
 }
diff --git a/src/org/jruby/compiler/impl/AbstractVariableCompiler.java b/src/org/jruby/compiler/impl/AbstractVariableCompiler.java
index c00cf20d91..c327657e5f 100644
--- a/src/org/jruby/compiler/impl/AbstractVariableCompiler.java
+++ b/src/org/jruby/compiler/impl/AbstractVariableCompiler.java
@@ -1,239 +1,258 @@
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.compiler.ArrayCallback;
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.compiler.VariableCompiler;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.builtin.IRubyObject;
 import static org.jruby.util.CodegenUtils.*;
 import org.objectweb.asm.Label;
 
 /**
  *
  * @author headius
  */
 public abstract class AbstractVariableCompiler implements VariableCompiler {
     protected SkinnyMethodAdapter method;
     protected StandardASMCompiler.AbstractMethodCompiler methodCompiler;
     protected int argsIndex;
     protected int closureIndex;
+    protected int tempVariableIndex;
     protected Arity arity;
 
     public AbstractVariableCompiler(
             StandardASMCompiler.AbstractMethodCompiler methodCompiler,
             SkinnyMethodAdapter method,
             int argsIndex,
-            int closureIndex) {
+            int closureIndex,
+            int firstTempIndex) {
         this.methodCompiler = methodCompiler;
         this.method = method;
         this.argsIndex = argsIndex;
         this.closureIndex = closureIndex;
+        this.tempVariableIndex = firstTempIndex;
     }
     
     public SkinnyMethodAdapter getMethodAdapter() {
         return this.method;
     }
 
     public void setMethodAdapter(SkinnyMethodAdapter sma) {
         this.method = sma;
     }
 
     public void assignLastLine() {
         method.dup();
 
         methodCompiler.loadThreadContext();
         methodCompiler.invokeThreadContext("getCurrentFrame", sig(Frame.class));
         method.swap();
         method.invokevirtual(p(Frame.class), "setLastLine", sig(Void.TYPE, params(IRubyObject.class)));
     }
 
     public void retrieveLastLine() {
         methodCompiler.loadThreadContext();
         methodCompiler.invokeThreadContext("getCurrentFrame", sig(Frame.class));
         method.invokevirtual(p(Frame.class), "getLastLine", sig(IRubyObject.class));
     }
 
     public void retrieveBackRef() {
         methodCompiler.loadThreadContext();
         methodCompiler.invokeThreadContext("getCurrentFrame", sig(Frame.class));
         method.invokevirtual(p(Frame.class), "getBackRef", sig(IRubyObject.class));
     }
 
     public void checkMethodArity(int requiredArgs, int optArgs, int restArg) {
         // check arity
         Label arityError = new Label();
         Label noArityError = new Label();
 
         if (restArg != -1) {
             if (requiredArgs > 0) {
                 // just confirm minimum args provided
                 method.aload(argsIndex);
                 method.arraylength();
                 method.ldc(requiredArgs);
                 method.if_icmplt(arityError);
             }
         } else if (optArgs > 0) {
             if (requiredArgs > 0) {
                 // confirm minimum args provided
                 method.aload(argsIndex);
                 method.arraylength();
                 method.ldc(requiredArgs);
                 method.if_icmplt(arityError);
             }
 
             // confirm maximum not greater than optional
             method.aload(argsIndex);
             method.arraylength();
             method.ldc(requiredArgs + optArgs);
             method.if_icmpgt(arityError);
         } else {
             // just confirm args length == required
             method.aload(argsIndex);
             method.arraylength();
             method.ldc(requiredArgs);
             method.if_icmpne(arityError);
         }
 
         method.go_to(noArityError);
 
         method.label(arityError);
         methodCompiler.loadRuntime();
         method.aload(argsIndex);
         method.ldc(requiredArgs);
         method.ldc(requiredArgs + optArgs);
         method.invokestatic(p(Arity.class), "checkArgumentCount", sig(int.class, Ruby.class, IRubyObject[].class, int.class, int.class));
         method.pop();
 
         method.label(noArityError);
     }
 
     public void assignMethodArguments(
             Object requiredArgs,
             int requiredArgsCount,
             Object optArgs,
             int optArgsCount,
             ArrayCallback requiredAssignment,
             ArrayCallback optGivenAssignment,
             ArrayCallback optNotGivenAssignment,
             CompilerCallback restAssignment,
             CompilerCallback blockAssignment) {
         // load arguments array
         method.aload(argsIndex);
         
         // NOTE: This assumes arity has already been confirmed, and does not re-check
         
         // first, iterate over all required args
         int currentArgElement = 0;
         for (; currentArgElement < requiredArgsCount; currentArgElement++) {
             // extract item from array
             method.dup(); // dup the original array
             method.ldc(new Integer(currentArgElement)); // index for the item
             method.arrayload();
             requiredAssignment.nextValue(methodCompiler, requiredArgs, currentArgElement);
             
             // normal assignment leaves the value; pop it.
             method.pop();
         }
         
         // next, iterate over all optional args, until no more arguments
         for (int optArgElement = 0; optArgElement < optArgsCount; currentArgElement++, optArgElement++) {
             Label noMoreArrayElements = new Label();
             Label doneWithElement = new Label();
 
             // confirm we're not past the end of the array
             method.dup(); // dup the original array
             method.arraylength();
             method.ldc(new Integer(currentArgElement));
             method.if_icmple(noMoreArrayElements); // if length <= start, end loop
 
             // extract item from array
             method.dup(); // dup the original array
             method.ldc(new Integer(currentArgElement)); // index for the item
             method.arrayload();
             optGivenAssignment.nextValue(methodCompiler, optArgs, optArgElement);
             method.go_to(doneWithElement);
 
             // otherwise no items left available, use the code from nilCallback
             method.label(noMoreArrayElements);
             optNotGivenAssignment.nextValue(methodCompiler, optArgs, optArgElement);
 
             // end of this element
             method.label(doneWithElement);
             // normal assignment leaves the value; pop it.
             method.pop();
         }
 
         // if there's args left and we want them, assign to rest arg
         if (restAssignment != null) {
             Label emptyArray = new Label();
             Label readyForArgs = new Label();
 
             // confirm we're not past the end of the array
             method.dup(); // dup the original array
             method.arraylength();
             method.ldc(new Integer(currentArgElement));
             method.if_icmple(emptyArray); // if length <= start, end loop
 
             // assign remaining elements as an array for rest args
             method.dup(); // dup the original array object
             methodCompiler.loadRuntime();
             method.ldc(currentArgElement);
             methodCompiler.invokeUtilityMethod("createSubarray", sig(RubyArray.class, IRubyObject[].class, Ruby.class, int.class));
             method.go_to(readyForArgs);
 
             // create empty array
             method.label(emptyArray);
             methodCompiler.createEmptyArray();
 
             // assign rest args
             method.label(readyForArgs);
             restAssignment.call(methodCompiler);
             //consume leftover assigned value
             method.pop();
         }
         
         // done with arguments array
         method.pop();
         
         // block argument assignment, if there's a block arg
         if (blockAssignment != null) {
             methodCompiler.loadRuntime();
             method.aload(closureIndex);
 
             methodCompiler.invokeUtilityMethod("processBlockArgument", sig(IRubyObject.class, params(Ruby.class, Block.class)));
             blockAssignment.call(methodCompiler);
             method.pop();
         }
     }
+        
+    public int grabTempLocal() {
+        return tempVariableIndex++;
+    }
+
+    public void setTempLocal(int index) {
+        method.astore(index);
+    }
+
+    public void getTempLocal(int index) {
+        method.aload(index);
+    }
+
+    public void releaseTempLocal() {
+        tempVariableIndex--;
+    }
 }
diff --git a/src/org/jruby/compiler/impl/BoxedVariableCompiler.java b/src/org/jruby/compiler/impl/BoxedVariableCompiler.java
index c01f376f97..d5c9eb0070 100644
--- a/src/org/jruby/compiler/impl/BoxedVariableCompiler.java
+++ b/src/org/jruby/compiler/impl/BoxedVariableCompiler.java
@@ -1,109 +1,113 @@
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
 
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.parser.StaticScope;
 
 /**
  *
  * @author headius
  */
 public class BoxedVariableCompiler extends HeapBasedVariableCompiler {
     private StaticScope scope; // the static scope for the currently compiling method
 
     public BoxedVariableCompiler(
             StandardASMCompiler.AbstractMethodCompiler methodCompiler,
             SkinnyMethodAdapter method,
             int scopeIndex,
             int varsIndex,
             int argsIndex,
-            int closureIndex) {
-        super(methodCompiler, method,scopeIndex, varsIndex, argsIndex, closureIndex);
+            int closureIndex,
+            int firstTempIndex) {
+        super(methodCompiler, method,scopeIndex, varsIndex, argsIndex, closureIndex, firstTempIndex);
         
         this.scopeIndex = scopeIndex;
         this.varsIndex = varsIndex;
     }
 
     public void beginMethod(CompilerCallback argsCallback, StaticScope scope) {
         this.scope = scope; 
         
         // fill non-captured Java local vars with nil as well
         if (scope != null) {
             methodCompiler.loadNil();
             for (int i = 0; i < scope.getNumberOfVariables(); i++) {
                 if (scope.isCaptured(i)) continue;
                 
                 assignLocalVariable(i);
+                
+                // move the first temp index up for each stack-based var
+                tempVariableIndex++;
             }
             method.pop();
         }
         
         super.beginMethod(argsCallback, scope);
     }
 
     public void beginClass(CompilerCallback bodyPrep, StaticScope scope) {
         assert false : "Do not use boxed var compiler for class bodies";
     }
 
     public void beginClosure(CompilerCallback argsCallback, StaticScope scope) {
         this.scope = scope;
 
         // fill non-captured Java local vars with nil as well
         if (scope != null) {
             methodCompiler.loadNil();
             for (int i = 0; i < scope.getNumberOfVariables(); i++) {
                 if (scope.isCaptured(i)) continue;
                 
                 assignLocalVariable(i);
             }
             method.pop();
         }
         
         super.beginClosure(argsCallback, scope);
     }
 
     public void assignLocalVariable(int index) {
         if (scope.isCaptured(index)) {
             super.assignLocalVariable(index);
         } else {
             // non-captured var, just use locals
             method.dup();
             method.astore(10 + index);
         }
     }
 
     public void retrieveLocalVariable(int index) {
         if (scope.isCaptured(index)) {
             super.retrieveLocalVariable(index);
         } else {
             // non-captured, use java local vars
             method.aload(10 + index);
         }
     }
 }
diff --git a/src/org/jruby/compiler/impl/HeapBasedVariableCompiler.java b/src/org/jruby/compiler/impl/HeapBasedVariableCompiler.java
index c73c7e496d..9ed34f311e 100644
--- a/src/org/jruby/compiler/impl/HeapBasedVariableCompiler.java
+++ b/src/org/jruby/compiler/impl/HeapBasedVariableCompiler.java
@@ -1,231 +1,234 @@
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
 
 import java.util.Arrays;
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.builtin.IRubyObject;
 import static org.jruby.util.CodegenUtils.*;
 
 /**
  *
  * @author headius
  */
 public class HeapBasedVariableCompiler extends AbstractVariableCompiler {
     protected int scopeIndex; // the index of the DynamicScope in the local Java scope to use for depth > 0 variable accesses
     protected int varsIndex; // the index of the IRubyObject[] in the local Java scope to use for depth 0 variable accesses
 
     public HeapBasedVariableCompiler(
             StandardASMCompiler.AbstractMethodCompiler methodCompiler,
             SkinnyMethodAdapter method,
             int scopeIndex,
             int varsIndex,
             int argsIndex,
-            int closureIndex) {
-        super(methodCompiler, method, argsIndex, closureIndex);
+            int closureIndex,
+            int firstTempIndex) {
+        super(methodCompiler, method, argsIndex, closureIndex, firstTempIndex);
         
         this.scopeIndex = scopeIndex;
         this.varsIndex = varsIndex;
     }
 
     public void beginMethod(CompilerCallback argsCallback, StaticScope scope) {
         // store the local vars in a local variable
         methodCompiler.loadThreadContext();
         methodCompiler.invokeThreadContext("getCurrentScope", sig(DynamicScope.class));
         method.dup();
         method.astore(scopeIndex);
         method.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
         method.astore(varsIndex);
 
         // fill local vars with nil, to avoid checking every access.
         method.aload(varsIndex);
         methodCompiler.loadNil();
         method.invokestatic(p(Arrays.class), "fill", sig(Void.TYPE, params(Object[].class, Object.class)));
         
         if (argsCallback != null) {
             argsCallback.call(methodCompiler);
         }
+        
+        // default for starting tempVariableIndex is ok
     }
 
     public void beginClass(CompilerCallback bodyPrep, StaticScope scope) {
         // store the local vars in a local variable for preparing the class (using previous scope)
         methodCompiler.loadThreadContext();
         methodCompiler.invokeThreadContext("getCurrentScope", sig(DynamicScope.class));
         method.dup();
         method.astore(scopeIndex);
         method.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
         method.astore(varsIndex);
         
         // class bodies prepare their own dynamic scope, so let it do that
         bodyPrep.call(methodCompiler);
         
         // store the new local vars in a local variable
         methodCompiler.loadThreadContext();
         methodCompiler.invokeThreadContext("getCurrentScope", sig(DynamicScope.class));
         method.dup();
         method.astore(scopeIndex);
         method.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
         method.astore(varsIndex);
 
         // fill local vars with nil, to avoid checking every access.
         method.aload(varsIndex);
         methodCompiler.loadNil();
         method.invokestatic(p(Arrays.class), "fill", sig(Void.TYPE, params(Object[].class, Object.class)));
     }
 
     public void beginClosure(CompilerCallback argsCallback, StaticScope scope) {
         // store the local vars in a local variable
         methodCompiler.loadThreadContext();
         methodCompiler.invokeThreadContext("getCurrentScope", sig(DynamicScope.class));
         method.dup();
         method.astore(scopeIndex);
         method.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
         method.astore(varsIndex);
 
         if (scope != null) {
             methodCompiler.loadNil();
             for (int i = 0; i < scope.getNumberOfVariables(); i++) {
                 assignLocalVariable(i);
             }
             method.pop();
         }
         
         if (argsCallback != null) {
             // load args[0] which will be the IRubyObject representing block args
             method.aload(argsIndex);
             method.ldc(new Integer(0));
             method.arrayload();
             argsCallback.call(methodCompiler);
             method.pop(); // clear remaining value on the stack
         }
     }
 
     public void assignLocalVariable(int index) {
         method.dup();
 
         switch (index) {
         case 0:
             method.aload(scopeIndex);
             method.swap();
             method.invokevirtual(p(DynamicScope.class), "setValueZeroDepthZero", sig(Void.TYPE, params(IRubyObject.class)));
             break;
         case 1:
             method.aload(scopeIndex);
             method.swap();
             method.invokevirtual(p(DynamicScope.class), "setValueOneDepthZero", sig(Void.TYPE, params(IRubyObject.class)));
             break;
         default:
             method.aload(varsIndex);
             method.swap();
             method.ldc(new Integer(index));
             method.swap();
             method.arraystore();
         }
     }
 
     public void assignLocalVariable(int index, int depth) {
         if (depth == 0) {
             assignLocalVariable(index);
             return;
         }
 
         method.dup();
 
         // unwrap scopes to appropriate depth
         method.aload(scopeIndex);
         while (depth > 0) {
             method.invokevirtual(p(DynamicScope.class), "getNextCapturedScope", sig(DynamicScope.class));
             depth--;
         }
         
         switch (index) {
         case 0:
             method.swap();
             method.invokevirtual(p(DynamicScope.class), "setValueZeroDepthZero", sig(Void.TYPE, params(IRubyObject.class)));
             break;
         case 1:
             method.swap();
             method.invokevirtual(p(DynamicScope.class), "setValueOneDepthZero", sig(Void.TYPE, params(IRubyObject.class)));
             break;
         default:
             method.swap();
             method.ldc(new Integer(index));
             method.invokevirtual(p(DynamicScope.class), "setValueDepthZero", sig(Void.TYPE, params(IRubyObject.class, Integer.TYPE)));
         }
     }
 
     public void retrieveLocalVariable(int index) {
         switch (index) {
         case 0:
             method.aload(scopeIndex);
             methodCompiler.loadNil();
             method.invokevirtual(p(DynamicScope.class), "getValueZeroDepthZeroOrNil", sig(IRubyObject.class, IRubyObject.class));
             break;
         case 1:
             method.aload(scopeIndex);
             methodCompiler.loadNil();
             method.invokevirtual(p(DynamicScope.class), "getValueOneDepthZeroOrNil", sig(IRubyObject.class, IRubyObject.class));
             break;
         default:
             method.aload(varsIndex);
             method.ldc(new Integer(index));
             method.arrayload();
         }
     }
 
     public void retrieveLocalVariable(int index, int depth) {
         if (depth == 0) {
             retrieveLocalVariable(index);
             return;
         }
 
         // unwrap scopes to appropriate depth
         method.aload(scopeIndex);
         while (depth > 0) {
             method.invokevirtual(p(DynamicScope.class), "getNextCapturedScope", sig(DynamicScope.class));
             depth--;
         }
         
         switch (index) {
         case 0:
             methodCompiler.loadNil();
             method.invokevirtual(p(DynamicScope.class), "getValueZeroDepthZeroOrNil", sig(IRubyObject.class, IRubyObject.class));
             break;
         case 1:
             methodCompiler.loadNil();
             method.invokevirtual(p(DynamicScope.class), "getValueOneDepthZeroOrNil", sig(IRubyObject.class, IRubyObject.class));
             break;
         default:
             method.ldc(new Integer(index));
             methodCompiler.loadNil();
             method.invokevirtual(p(DynamicScope.class), "getValueDepthZeroOrNil", sig(IRubyObject.class, params(Integer.TYPE, IRubyObject.class)));
         }
     }
 }
diff --git a/src/org/jruby/compiler/impl/StackBasedVariableCompiler.java b/src/org/jruby/compiler/impl/StackBasedVariableCompiler.java
index 97ee242817..4808860d39 100644
--- a/src/org/jruby/compiler/impl/StackBasedVariableCompiler.java
+++ b/src/org/jruby/compiler/impl/StackBasedVariableCompiler.java
@@ -1,127 +1,136 @@
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
 
 package org.jruby.compiler.impl;
 
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.builtin.IRubyObject;
 import static org.jruby.util.CodegenUtils.*;
 
 /**
  *
  * @author headius
  */
 public class StackBasedVariableCompiler extends AbstractVariableCompiler {
     private int scopeIndex; // the index of the dynamic scope for higher scopes
 
-    public StackBasedVariableCompiler(StandardASMCompiler.AbstractMethodCompiler methodCompiler, SkinnyMethodAdapter method, int scopeIndex, int argsIndex, int closureIndex) {
-        super(methodCompiler, method, argsIndex, closureIndex);
+    public StackBasedVariableCompiler(
+            StandardASMCompiler.AbstractMethodCompiler methodCompiler,
+            SkinnyMethodAdapter method,
+            int scopeIndex,
+            int argsIndex,
+            int closureIndex,
+            int firstTempIndex) {
+        super(methodCompiler, method, argsIndex, closureIndex, firstTempIndex);
         this.scopeIndex = scopeIndex;
     }
 
     public void beginMethod(CompilerCallback argsCallback, StaticScope scope) {
         // fill in all vars with nil so compiler is happy about future accesses
         methodCompiler.loadNil();
         for (int i = 0; i < scope.getNumberOfVariables(); i++) {
             assignLocalVariable(i);
         }
         method.pop();
         
+        // temp locals must start after last real local
+        tempVariableIndex += scope.getNumberOfVariables();
+        
         if (argsCallback != null) {
             argsCallback.call(methodCompiler);
         }
     }
 
     public void beginClass(CompilerCallback bodyPrep, StaticScope scope) {
         throw new NotCompilableException("ERROR: stack-based variables should not be compiling class bodies");
     }
 
     public void beginClosure(CompilerCallback argsCallback, StaticScope scope) {
         // store the local vars in a local variable
         methodCompiler.loadThreadContext();
         methodCompiler.invokeThreadContext("getCurrentScope", sig(DynamicScope.class));
         method.astore(scopeIndex);
         
         if (scope != null) {
             methodCompiler.loadNil();
             for (int i = 0; i < scope.getNumberOfVariables(); i++) {
                 assignLocalVariable(i);
             }
             method.pop();
         }
         
         if (argsCallback != null) {
             // load args[0] which will be the IRubyObject representing block args
             method.aload(argsIndex);
             method.ldc(new Integer(0));
             method.arrayload();
             argsCallback.call(methodCompiler);
             method.pop(); // clear remaining value on the stack
         }
     }
 
     public void assignLocalVariable(int index) {
         method.dup();
 
         method.astore(10 + index);
     }
 
     public void assignLocalVariable(int index, int depth) {
         if (depth == 0) {
             assignLocalVariable(index);
         } else {
             method.dup();
 
             method.aload(scopeIndex);
             method.swap();
             method.ldc(new Integer(index));
             method.swap();
             method.ldc(new Integer(depth));
             method.invokevirtual(p(DynamicScope.class), "setValue", sig(Void.TYPE, params(Integer.TYPE, IRubyObject.class, Integer.TYPE)));
         }
     }
 
     public void retrieveLocalVariable(int index) {
         method.aload(10 + index);
     }
 
     public void retrieveLocalVariable(int index, int depth) {
         if (depth == 0) {
             retrieveLocalVariable(index);
         } else {
             method.aload(scopeIndex);
             method.ldc(new Integer(index));
             method.ldc(new Integer(depth));
             methodCompiler.loadNil();
             method.invokevirtual(p(DynamicScope.class), "getValueOrNil", sig(IRubyObject.class, params(Integer.TYPE, Integer.TYPE, IRubyObject.class)));
         }
     }
 }
diff --git a/src/org/jruby/compiler/impl/StandardASMCompiler.java b/src/org/jruby/compiler/impl/StandardASMCompiler.java
index e9795788b7..4f9090ea24 100644
--- a/src/org/jruby/compiler/impl/StandardASMCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardASMCompiler.java
@@ -1,1393 +1,1391 @@
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
 import org.jruby.ast.executable.AbstractScript;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.ArrayCallback;
 import org.jruby.compiler.BranchCallback;
 import org.jruby.compiler.CacheCompiler;
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
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.InstanceVariables;
 import org.jruby.util.ByteList;
 import static org.jruby.util.CodegenUtils.*;
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
     private static final String THREADCONTEXT = p(ThreadContext.class);
     private static final String RUBY = p(Ruby.class);
     private static final String IRUBYOBJECT = p(IRubyObject.class);
 
     private static final String METHOD_SIGNATURE = sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class});
     private static final String CLOSURE_SIGNATURE = sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class});
 
     public static final int THIS = 0;
     public static final int THREADCONTEXT_INDEX = 1;
     public static final int SELF_INDEX = 2;
     public static final int ARGS_INDEX = 3;
     public static final int CLOSURE_INDEX = 4;
     public static final int DYNAMIC_SCOPE_INDEX = 5;
     public static final int RUNTIME_INDEX = 6;
     public static final int VARS_ARRAY_INDEX = 7;
     public static final int NIL_INDEX = 8;
     public static final int EXCEPTION_INDEX = 9;
     public static final int PREVIOUS_EXCEPTION_INDEX = 10;
+    public static final int FIRST_TEMP_INDEX = 11;
     
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
     
     CacheCompiler cacheCompiler;
     
     /** Creates a new instance of StandardCompilerContext */
     public StandardASMCompiler(String classname, String sourcename) {
         this.classname = classname;
         this.sourcename = sourcename;
     }
 
     public byte[] getClassByteArray() {
         return classWriter.toByteArray();
     }
 
     public Class<?> loadClass(JRubyClassLoader classLoader) throws ClassNotFoundException {
         classLoader.defineClass(c(classname), classWriter.toByteArray());
         return classLoader.loadClass(c(classname));
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
     
     static boolean USE_INHERITED_CACHE_FIELDS = true;
 
     public void startScript(StaticScope scope) {
         classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
 
         // Create the class with the appropriate class name and source file
         classWriter.visit(V1_4, ACC_PUBLIC + ACC_SUPER, classname, null, p(AbstractScript.class), null);
         classWriter.visitSource(sourcename, null);
         
         topLevelScope = scope;
 
         beginInit();
         beginClassInit();
         
         cacheCompiler = new InheritedCacheCompiler(this);
     }
 
     public void endScript(boolean generateRun, boolean generateLoad, boolean generateMain) {
         // add Script#run impl, used for running this script with a specified threadcontext and self
         // root method of a script is always in __file__ method
         String methodName = "__file__";
         
         if (generateRun) {
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
         }
         
         if (generateLoad || generateMain) {
             // the load method is used for loading as a top-level script, and prepares appropriate scoping around the code
             SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC, "load", METHOD_SIGNATURE, null, null));
             method.start();
 
             // invoke __file__ with threadcontext, self, args (null), and block (null)
             Label tryBegin = new Label();
             Label tryFinally = new Label();
 
             method.label(tryBegin);
             method.aload(THREADCONTEXT_INDEX);
             buildStaticScopeNames(method, topLevelScope);
             method.invokestatic(p(RuntimeHelpers.class), "preLoad", sig(void.class, ThreadContext.class, String[].class));
 
             method.aload(THIS);
             method.aload(THREADCONTEXT_INDEX);
             method.aload(SELF_INDEX);
             method.aload(ARGS_INDEX);
             method.aload(CLOSURE_INDEX);
 
             method.invokevirtual(classname, methodName, METHOD_SIGNATURE);
             method.aload(THREADCONTEXT_INDEX);
             method.invokestatic(p(RuntimeHelpers.class), "postLoad", sig(void.class, ThreadContext.class));
             method.areturn();
 
             method.label(tryFinally);
             method.aload(THREADCONTEXT_INDEX);
             method.invokestatic(p(RuntimeHelpers.class), "postLoad", sig(void.class, ThreadContext.class));
             method.athrow();
 
             method.trycatch(tryBegin, tryFinally, tryFinally, null);
 
             method.end();
         }
         
         if (generateMain) {
             // add main impl, used for detached or command-line execution of this script with a new runtime
             // root method of a script is always in stub0, method0
             SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_STATIC, "main", sig(Void.TYPE, params(String[].class)), null, null));
             method.start();
 
             // new instance to invoke run against
             method.newobj(classname);
             method.dup();
             method.invokespecial(classname, "<init>", sig(Void.TYPE));
 
             // instance config for the script run
             method.newobj(p(RubyInstanceConfig.class));
             method.dup();
             method.invokespecial(p(RubyInstanceConfig.class), "<init>", "()V");
 
             // set argv from main's args
             method.dup();
             method.aload(0);
             method.invokevirtual(p(RubyInstanceConfig.class), "setArgv", sig(void.class, String[].class));
 
             // invoke run with threadcontext and topself
             method.invokestatic(p(Ruby.class), "newInstance", sig(Ruby.class, RubyInstanceConfig.class));
             method.dup();
 
             method.invokevirtual(RUBY, "getCurrentContext", sig(ThreadContext.class));
             method.swap();
             method.invokevirtual(RUBY, "getTopSelf", sig(IRubyObject.class));
             method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
             method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
 
             method.invokevirtual(classname, "load", METHOD_SIGNATURE);
             method.voidreturn();
             method.end();
         }
         
         endInit();
         endClassInit();
     }
 
     public void buildStaticScopeNames(SkinnyMethodAdapter method, StaticScope scope) {
         // construct static scope list of names
         method.ldc(new Integer(scope.getNumberOfVariables()));
         method.anewarray(p(String.class));
         for (int i = 0; i < scope.getNumberOfVariables(); i++) {
             method.dup();
             method.ldc(new Integer(i));
             method.ldc(scope.getVariables()[i]);
             method.arraystore();
         }
     }
 
     private void beginInit() {
         ClassVisitor cv = getClassVisitor();
 
         initMethod = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC, "<init>", sig(Void.TYPE), null, null));
         initMethod.start();
         initMethod.aload(THIS);
         if (USE_INHERITED_CACHE_FIELDS) {
             initMethod.invokespecial(p(AbstractScript.class), "<init>", sig(Void.TYPE));
         } else {
             initMethod.invokespecial(p(Object.class), "<init>", sig(Void.TYPE));
         }
         
         cv.visitField(ACC_PRIVATE | ACC_FINAL, "$class", ci(Class.class), null, null);
         
         // FIXME: this really ought to be in clinit, but it doesn't matter much
         initMethod.aload(THIS);
         initMethod.ldc(c(classname));
         initMethod.invokestatic(p(Class.class), "forName", sig(Class.class, params(String.class)));
         initMethod.putfield(classname, "$class", ci(Class.class));
     }
 
     private void endInit() {
         initMethod.voidreturn();
         initMethod.end();
     }
 
     private void beginClassInit() {
         ClassVisitor cv = getClassVisitor();
 
         clinitMethod = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC | ACC_STATIC, "<clinit>", sig(Void.TYPE), null, null));
         clinitMethod.start();
     }
 
     private void endClassInit() {
         clinitMethod.voidreturn();
         clinitMethod.end();
     }
     
     public SkinnyMethodAdapter getInitMethod() {
         return initMethod;
     }
     
     public SkinnyMethodAdapter getClassInitMethod() {
         return clinitMethod;
     }
     
     public CacheCompiler getCacheCompiler() {
         return cacheCompiler;
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
-        
-        // The current local variable count, to use for temporary locals during processing
-        protected int localVariable = PREVIOUS_EXCEPTION_INDEX + 1;
 
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
             method.invokevirtual(classname, methodName, sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}));
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
 
             invokeIRuby("newSymbol", sig(RubySymbol.class, params(String.class)));
         }
 
         public void loadObject() {
             loadRuntime();
 
             invokeIRuby("getObject", sig(RubyClass.class, params()));
         }
 
         /**
          * This is for utility methods used by the compiler, to reduce the amount of code generation
          * necessary.  All of these live in CompilerHelpers.
          */
         public void invokeUtilityMethod(String methodName, String signature) {
             method.invokestatic(p(RuntimeHelpers.class), methodName, signature);
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
             invokeThreadContext("setConstantInCurrent", sig(IRubyObject.class, params(String.class, IRubyObject.class)));
         }
 
         public void assignConstantInModule(String name) {
             method.ldc(name);
             loadThreadContext();
             invokeUtilityMethod("setConstantInModule", sig(IRubyObject.class, IRubyObject.class, IRubyObject.class, String.class, ThreadContext.class));
         }
 
         public void assignConstantInObject(String name) {
             // load Object under value
             loadRuntime();
             invokeIRuby("getObject", sig(RubyClass.class, params()));
             method.swap();
 
             assignConstantInModule(name);
         }
 
         public void retrieveConstant(String name) {
             loadThreadContext();
             method.ldc(name);
             invokeThreadContext("getConstant", sig(IRubyObject.class, params(String.class)));
         }
 
         public void retrieveConstantFromModule(String name) {
             method.visitTypeInsn(CHECKCAST, p(RubyModule.class));
             method.ldc(name);
             method.invokevirtual(p(RubyModule.class), "fastGetConstantFrom", sig(IRubyObject.class, params(String.class)));
         }
 
         public void retrieveClassVariable(String name) {
             loadThreadContext();
             loadRuntime();
             loadSelf();
             method.ldc(name);
 
             invokeUtilityMethod("fastFetchClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class)));
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
 
             invokeUtilityMethod("fastSetClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
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
 
             invokeUtilityMethod("fastDeclareClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
         }
 
         public void createNewFloat(double value) {
             loadRuntime();
             method.ldc(new Double(value));
 
             invokeIRuby("newFloat", sig(RubyFloat.class, params(Double.TYPE)));
         }
 
         public void createNewFixnum(long value) {
             loadRuntime();
             method.ldc(new Long(value));
 
             invokeIRuby("newFixnum", sig(RubyFixnum.class, params(Long.TYPE)));
         }
 
         public void createNewBignum(BigInteger value) {
             loadRuntime();
             method.ldc(value.toString());
 
             method.invokestatic(p(RubyBignum.class), "newBignum", sig(RubyBignum.class, params(Ruby.class, String.class)));
         }
 
         public void createNewString(ArrayCallback callback, int count) {
             loadRuntime();
             invokeIRuby("newString", sig(RubyString.class, params()));
             for (int i = 0; i < count; i++) {
                 callback.nextValue(this, null, i);
                 method.invokevirtual(p(RubyString.class), "append", sig(RubyString.class, params(IRubyObject.class)));
             }
         }
 
         public void createNewSymbol(ArrayCallback callback, int count) {
             loadRuntime();
             invokeIRuby("newString", sig(RubyString.class, params()));
             for (int i = 0; i < count; i++) {
                 callback.nextValue(this, null, i);
                 method.invokevirtual(p(RubyString.class), "append", sig(RubyString.class, params(IRubyObject.class)));
             }
             toJavaString();
             loadRuntime();
             method.swap();
             invokeIRuby("newSymbol", sig(RubySymbol.class, params(String.class)));
         }
 
         public void createNewString(ByteList value) {
             // FIXME: this is sub-optimal, storing string value in a java.lang.String again
             loadRuntime();
             getCacheCompiler().cacheByteList(method, value.toString());
 
             invokeIRuby("newStringShared", sig(RubyString.class, params(ByteList.class)));
         }
 
         public void createNewSymbol(String name) {
             getCacheCompiler().cacheSymbol(method, name);
         }
 
         public void createNewArray(boolean lightweight) {
             loadRuntime();
             // put under object array already present
             method.swap();
 
             if (lightweight) {
                 invokeIRuby("newArrayNoCopyLight", sig(RubyArray.class, params(IRubyObject[].class)));
             } else {
                 invokeIRuby("newArrayNoCopy", sig(RubyArray.class, params(IRubyObject[].class)));
             }
         }
 
         public void createEmptyArray() {
             loadRuntime();
 
             invokeIRuby("newArray", sig(RubyArray.class, params()));
         }
 
         public void createObjectArray(Object[] sourceArray, ArrayCallback callback) {
             buildObjectArray(IRUBYOBJECT, sourceArray, callback);
         }
 
         public void createObjectArray(int elementCount) {
             // if element count is less than 6, use helper methods
             if (elementCount < 6) {
                 Class[] params = new Class[elementCount];
                 Arrays.fill(params, IRubyObject.class);
                 invokeUtilityMethod("constructObjectArray", sig(IRubyObject[].class, params));
             } else {
                 // This is pretty inefficient for building an array, so just raise an error if someone's using it for a lot of elements
                 throw new NotCompilableException("Don't use createObjectArray(int) for more than 5 elements");
             }
         }
 
         private void buildObjectArray(String type, Object[] sourceArray, ArrayCallback callback) {
             if (sourceArray.length == 0) {
                 method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
             } else if (sourceArray.length <= RuntimeHelpers.MAX_SPECIFIC_ARITY_OBJECT_ARRAY) {
                 // if we have a specific-arity helper to construct an array for us, use that
                 for (int i = 0; i < sourceArray.length; i++) {
                     callback.nextValue(this, sourceArray, i);
                 }
                 invokeUtilityMethod("constructObjectArray", sig(IRubyObject[].class, params(IRubyObject.class, sourceArray.length)));
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
 
             method.invokestatic(p(RubyHash.class), "newHash", sig(RubyHash.class, params(Ruby.class)));
         }
 
         public void createNewHash(Object elements, ArrayCallback callback, int keyCount) {
             loadRuntime();
             
             if (keyCount <= RuntimeHelpers.MAX_SPECIFIC_ARITY_HASH) {
                 // we have a specific-arity method we can use to construct, so use that
                 for (int i = 0; i < keyCount; i++) {
                     callback.nextValue(this, elements, i);
                 }
                 
                 invokeUtilityMethod("constructHash", sig(RubyHash.class, params(Ruby.class, IRubyObject.class, keyCount * 2)));
             } else {
                 method.invokestatic(p(RubyHash.class), "newHash", sig(RubyHash.class, params(Ruby.class)));
 
                 for (int i = 0; i < keyCount; i++) {
                     method.dup();
                     callback.nextValue(this, elements, i);
                     method.invokevirtual(p(RubyHash.class), "fastASet", sig(void.class, params(IRubyObject.class, IRubyObject.class)));
                 }
             }
         }
 
         public void createNewRange(boolean isExclusive) {
             loadRuntime();
 
             // could be more efficient with a callback
             method.dup_x2();
             method.pop();
 
             method.ldc(new Boolean(isExclusive));
 
             method.invokestatic(p(RubyRange.class), "newRange", sig(RubyRange.class, params(Ruby.class, IRubyObject.class, IRubyObject.class, Boolean.TYPE)));
         }
 
         /**
          * Invoke IRubyObject.isTrue
          */
         private void isTrue() {
             invokeIRubyObject("isTrue", sig(Boolean.TYPE));
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
             method.trycatch(tryBegin, tryEnd, catchRedo, p(JumpException.RedoJump.class));
             method.trycatch(tryBegin, tryEnd, catchNext, p(JumpException.NextJump.class));
             method.trycatch(tryBegin, tryEnd, catchBreak, p(JumpException.BreakJump.class));
             if (checkFirst) {
                 // only while loops seem to have this RaiseException magic
                 method.trycatch(tryBegin, tryEnd, catchRaised, p(RaiseException.class));
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
                     invokeUtilityMethod("breakJumpInWhile", sig(IRubyObject.class, JumpException.BreakJump.class, Block.class));
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
                     invokeUtilityMethod("getLocalJumpTypeOrRethrow", sig(String.class, params(RaiseException.class)));
                     // if we get here we have a RaiseException we know is a local jump error and an error type
 
                     // is it break?
                     method.dup(); // dup string
                     method.ldc("break");
                     method.invokevirtual(p(String.class), "equals", sig(boolean.class, params(Object.class)));
                     method.ifeq(raiseNext);
                     // pop the extra string, get the break value, and end the loop
                     method.pop();
                     invokeUtilityMethod("unwrapLocalJumpErrorValue", sig(IRubyObject.class, params(RaiseException.class)));
                     method.go_to(done);
 
                     // is it next?
                     method.label(raiseNext);
                     method.dup();
                     method.ldc("next");
                     method.invokevirtual(p(String.class), "equals", sig(boolean.class, params(Object.class)));
                     method.ifeq(raiseRedo);
                     // pop the extra string and the exception, jump to the condition
                     method.pop2();
                     method.go_to(conditionCheck);
 
                     // is it redo?
                     method.label(raiseRedo);
                     method.dup();
                     method.ldc("redo");
                     method.invokevirtual(p(String.class), "equals", sig(boolean.class, params(Object.class)));
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
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(p(CallbackFactory.class), "getBlockCallback", sig(CompiledBlockCallback.class, params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
             method.ldc(new Integer(arity));
 
             buildStaticScopeNames(method, scope);
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             method.ldc(Boolean.valueOf(hasMultipleArgsHead));
             method.ldc(BlockBody.asArgumentType(argsNodeId));
             // if there's a sub-closure or there's scope-aware methods, it can't be "light"
             method.ldc(!(inspector.hasClosure() || inspector.hasScopeAwareMethods()));
 
             invokeUtilityMethod("createBlock", sig(Block.class,
                     params(ThreadContext.class, IRubyObject.class, Integer.TYPE, String[].class, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE, boolean.class)));
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
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(p(CallbackFactory.class), "getBlockCallback", sig(CompiledBlockCallback.class, params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
 
             buildStaticScopeNames(method, scope);
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
 
             invokeUtilityMethod("runBeginBlock", sig(IRubyObject.class,
                     params(ThreadContext.class, IRubyObject.class, String[].class, CompiledBlockCallback.class)));
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
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(p(CallbackFactory.class), "getBlockCallback", sig(CompiledBlockCallback.class, params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
             method.ldc(new Integer(arity));
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             method.ldc(Boolean.valueOf(hasMultipleArgsHead));
             method.ldc(BlockBody.asArgumentType(argsNodeId));
 
             invokeUtilityMethod("createSharedScopeBlock", sig(Block.class,
                     params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
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
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(p(CallbackFactory.class), "getBlockCallback", sig(CompiledBlockCallback.class, params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
             method.ldc(new Integer(0));
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             method.ldc(false);
             method.ldc(Block.ZERO_ARGS);
 
             invokeUtilityMethod("createSharedScopeBlock", sig(Block.class,
                     params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
             
             loadRuntime();
             invokeUtilityMethod("registerEndBlock", sig(void.class, Block.class, Ruby.class));
             loadNil();
         }
 
         private void getCallbackFactory() {
             // FIXME: Perhaps a bit extra code, but only for defn/s; examine
             loadRuntime();
             getCompiledClass();
             method.dup();
             method.invokevirtual(p(Class.class), "getClassLoader", sig(ClassLoader.class));
             method.invokestatic(p(CallbackFactory.class), "createFactory", sig(CallbackFactory.class, params(Ruby.class, Class.class, ClassLoader.class)));
         }
 
         public void getCompiledClass() {
             method.aload(THIS);
             method.getfield(classname, "$class", ci(Class.class));
         }
 
         private void getRubyClass() {
             loadThreadContext();
             invokeThreadContext("getRubyClass", sig(RubyModule.class));
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
             invokeIRuby("getFalse", sig(RubyBoolean.class));
         }
 
         public void loadTrue() {
             // TODO: cache?
             loadRuntime();
             invokeIRuby("getTrue", sig(RubyBoolean.class));
         }
 
         public void loadCurrentModule() {
             loadThreadContext();
             invokeThreadContext("getCurrentScope", sig(DynamicScope.class));
             method.invokevirtual(p(DynamicScope.class), "getStaticScope", sig(StaticScope.class));
             method.invokevirtual(p(StaticScope.class), "getModule", sig(RubyModule.class));
         }
 
         public void retrieveInstanceVariable(String name) {
             loadRuntime();
             loadSelf();
             method.ldc(name);
             invokeUtilityMethod("fastGetInstanceVariable", sig(IRubyObject.class, Ruby.class, IRubyObject.class, String.class));
         }
 
         public void assignInstanceVariable(String name) {
             // FIXME: more efficient with a callback
             loadSelf();
             invokeIRubyObject("getInstanceVariables", sig(InstanceVariables.class));
             method.swap();
 
             method.ldc(name);
             method.swap();
 
             method.invokeinterface(p(InstanceVariables.class), "fastSetInstanceVariable", sig(IRubyObject.class, params(String.class, IRubyObject.class)));
         }
 
         public void retrieveGlobalVariable(String name) {
             loadRuntime();
 
             invokeIRuby("getGlobalVariables", sig(GlobalVariables.class));
             method.ldc(name);
             method.invokevirtual(p(GlobalVariables.class), "get", sig(IRubyObject.class, params(String.class)));
         }
 
         public void assignGlobalVariable(String name) {
             // FIXME: more efficient with a callback
             loadRuntime();
 
             invokeIRuby("getGlobalVariables", sig(GlobalVariables.class));
             method.swap();
             method.ldc(name);
             method.swap();
             method.invokevirtual(p(GlobalVariables.class), "set", sig(IRubyObject.class, params(String.class, IRubyObject.class)));
         }
 
         public void negateCurrentValue() {
             loadRuntime();
             invokeUtilityMethod("negate", sig(IRubyObject.class, IRubyObject.class, Ruby.class));
         }
 
         public void splatCurrentValue() {
             loadRuntime();
             method.invokestatic(p(ASTInterpreter.class), "splatValue", sig(RubyArray.class, params(IRubyObject.class, Ruby.class)));
         }
 
         public void singlifySplattedValue() {
             loadRuntime();
             method.invokestatic(p(ASTInterpreter.class), "aValueSplat", sig(IRubyObject.class, params(IRubyObject.class, Ruby.class)));
         }
 
         public void aryToAry() {
             loadRuntime();
             method.invokestatic(p(ASTInterpreter.class), "aryToAry", sig(IRubyObject.class, params(IRubyObject.class, Ruby.class)));
         }
 
         public void ensureRubyArray() {
             invokeUtilityMethod("ensureRubyArray", sig(RubyArray.class, params(IRubyObject.class)));
         }
 
         public void ensureMultipleAssignableRubyArray(boolean masgnHasHead) {
             loadRuntime();
             method.swap();
             method.ldc(new Boolean(masgnHasHead));
             invokeUtilityMethod("ensureMultipleAssignableRubyArray", sig(RubyArray.class, params(Ruby.class, IRubyObject.class, boolean.class)));
         }
 
         public void forEachInValueArray(int start, int count, Object source, ArrayCallback callback, ArrayCallback nilCallback, CompilerCallback argsCallback) {
             // FIXME: This could probably be made more efficient
             for (; start < count; start++) {
                 Label noMoreArrayElements = new Label();
                 Label doneWithElement = new Label();
                 
                 // confirm we're not past the end of the array
                 method.dup(); // dup the original array object
                 method.invokevirtual(p(RubyArray.class), "getLength", sig(Integer.TYPE));
                 method.ldc(new Integer(start));
                 method.if_icmple(noMoreArrayElements); // if length <= start, end loop
                 
                 // extract item from array
                 method.dup(); // dup the original array object
                 method.ldc(new Integer(start)); // index for the item
                 method.invokevirtual(p(RubyArray.class), "entry", sig(IRubyObject.class, params(Integer.TYPE))); // extract item
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
                 method.invokevirtual(p(RubyArray.class), "getLength", sig(Integer.TYPE));
                 method.ldc(new Integer(start));
                 method.if_icmple(emptyArray); // if length <= start, end loop
                 
                 // assign remaining elements as an array for rest args
                 method.dup(); // dup the original array object
                 method.ldc(start);
                 invokeUtilityMethod("createSubarray", sig(RubyArray.class, RubyArray.class, int.class));
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
             method.invokeinterface(p(IRubyObject.class), "asString", sig(RubyString.class, params()));
         }
         
         public void toJavaString() {
             method.invokevirtual(p(Object.class), "toString", sig(String.class));
         }
 
         public void nthRef(int match) {
             method.ldc(new Integer(match));
             backref();
             method.invokestatic(p(RubyRegexp.class), "nth_match", sig(IRubyObject.class, params(Integer.TYPE, IRubyObject.class)));
         }
 
         public void match() {
             method.invokevirtual(p(RubyRegexp.class), "op_match2", sig(IRubyObject.class, params()));
         }
 
         public void match2() {
             method.invokevirtual(p(RubyRegexp.class), "op_match", sig(IRubyObject.class, params(IRubyObject.class)));
         }
 
         public void match3() {
             loadThreadContext();
             invokeUtilityMethod("match3", sig(IRubyObject.class, RubyRegexp.class, IRubyObject.class, ThreadContext.class));
         }
 
         public void createNewRegexp(final ByteList value, final int options) {
             String regexpField = getNewConstant(ci(RubyRegexp.class), "lit_reg_");
 
             // in current method, load the field to see if we've created a Pattern yet
             method.aload(THIS);
             method.getfield(classname, regexpField, ci(RubyRegexp.class));
 
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated); //[]
 
             // load string, for Regexp#source and Regexp#inspect
             String regexpString = value.toString();
 
             loadRuntime(); //[R]
             method.ldc(regexpString); //[R, rS]
             method.ldc(new Integer(options)); //[R, rS, opts]
 
             method.invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, params(Ruby.class, String.class, Integer.TYPE))); //[reg]
 
             method.aload(THIS); //[reg, T]
             method.swap(); //[T, reg]
             method.putfield(classname, regexpField, ci(RubyRegexp.class)); //[]
             method.label(alreadyCreated);
             method.aload(THIS); //[T]
             method.getfield(classname, regexpField, ci(RubyRegexp.class)); 
         }
 
         public void createNewRegexp(CompilerCallback createStringCallback, final int options) {
             boolean onceOnly = (options & ReOptions.RE_OPTION_ONCE) != 0;   // for regular expressions with the /o flag
             Label alreadyCreated = null;
             String regexpField = null;
 
             // only alter the code if the /o flag was present
             if (onceOnly) {
                 regexpField = getNewConstant(ci(RubyRegexp.class), "lit_reg_");
     
                 // in current method, load the field to see if we've created a Pattern yet
                 method.aload(THIS);
                 method.getfield(classname, regexpField, ci(RubyRegexp.class));
     
                 alreadyCreated = new Label();
                 method.ifnonnull(alreadyCreated);
             }
 
             loadRuntime();
 
             createStringCallback.call(this);
             method.invokevirtual(p(RubyString.class), "getByteList", sig(ByteList.class));
             method.ldc(new Integer(options));
 
             method.invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, params(Ruby.class, ByteList.class, Integer.TYPE))); //[reg]
 
             // only alter the code if the /o flag was present
             if (onceOnly) {
                 method.aload(THIS);
                 method.swap();
@@ -1398,1336 +1396,1336 @@ public class StandardASMCompiler implements ScriptCompiler, Opcodes {
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
             method.aload(NIL_INDEX);
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
 
                 public void branch(MethodCompiler context) {
                     method.visitTypeInsn(CHECKCAST, p(RubyMatchData.class));
                     method.dup();
                     method.invokevirtual(p(RubyMatchData.class), "use", sig(void.class));
                     method.ldc(new Long(number));
                     method.invokevirtual(p(RubyMatchData.class), "group", sig(IRubyObject.class, params(long.class)));
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
             invokeThreadContext("getCurrentFrame", sig(Frame.class));
             method.invokevirtual(p(Frame.class), "getBackRef", sig(IRubyObject.class));
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
             return "__ensure_" + (ensureNumber++);
         }
 
         public void protect(BranchCallback regularCode, BranchCallback protectedCode, Class ret) {
 
             String mname = getNewEnsureName();
             SkinnyMethodAdapter mv = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}), null, null));
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
                 mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                 mv.dup();
                 mv.astore(RUNTIME_INDEX);
             
                 // grab nil for local variables
                 mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
                 mv.astore(NIL_INDEX);
             
                 mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
                 mv.dup();
                 mv.astore(DYNAMIC_SCOPE_INDEX);
                 mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
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
             method.invokevirtual(classname, mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}));
         }
 
         protected String getNewRescueName() {
             return "__rescue_" + (rescueNumber++);
         }
 
         public void rescue(BranchCallback regularCode, Class exception, BranchCallback catchCode, Class ret) {
             String mname = getNewRescueName();
             SkinnyMethodAdapter mv = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}), null, null));
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
                 mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                 mv.dup();
                 mv.astore(RUNTIME_INDEX);
                 
                 // store previous exception for restoration if we rescue something
                 loadRuntime();
                 invokeUtilityMethod("getErrorInfo", sig(IRubyObject.class, Ruby.class));
                 mv.astore(PREVIOUS_EXCEPTION_INDEX);
             
                 // grab nil for local variables
                 mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
                 mv.astore(NIL_INDEX);
             
                 mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
                 mv.dup();
                 mv.astore(DYNAMIC_SCOPE_INDEX);
                 mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
                 mv.astore(VARS_ARRAY_INDEX);
 
                 Label beforeBody = new Label();
                 Label afterBody = new Label();
                 Label catchBlock = new Label();
                 mv.visitTryCatchBlock(beforeBody, afterBody, catchBlock, p(exception));
                 mv.visitLabel(beforeBody);
 
                 regularCode.branch(this);
 
                 mv.label(afterBody);
                 mv.go_to(exitRescue);
                 mv.label(catchBlock);
                 mv.astore(EXCEPTION_INDEX);
 
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
                 loadRuntime();
                 mv.aload(PREVIOUS_EXCEPTION_INDEX);
                 invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
                 mv.athrow();
                 
                 mv.label(exitRescue);
                 
                 // restore the original exception
                 loadRuntime();
                 mv.aload(PREVIOUS_EXCEPTION_INDEX);
                 invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
                 
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
             method.invokevirtual(classname, mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}));
         }
 
         public void inDefined() {
             method.aload(THREADCONTEXT_INDEX);
             method.iconst_1();
             invokeThreadContext("setWithinDefined", sig(void.class, params(boolean.class)));
         }
 
         public void outDefined() {
             method.aload(THREADCONTEXT_INDEX);
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
             invokeIRuby("getGlobalVariables", sig(GlobalVariables.class));
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
         
         public void isClassVarDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch){
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
                         method.pop(); method.pop();
 
                         isConstant.branch(AbstractMethodCompiler.this);
 
                         method.go_to(afterJmp);
                         
                         method.label(nextJmpPop);
                         method.pop();
 
                         method.label(nextJmp); //[C]
 
                         metaclass();
                         method.ldc(name);
                         method.iconst_1(); // push true
                         method.invokevirtual(p(RubyClass.class), "isMethodBound", sig(boolean.class, params(String.class, boolean.class)));
                         method.ifeq(falseJmp); // EQ == 0 (i.e. false)
                         
                         isMethod.branch(AbstractMethodCompiler.this);
                         method.go_to(afterJmp);
 
                         method.label(falseJmp);
                         none.branch(AbstractMethodCompiler.this);
             
                         method.label(afterJmp);
                     }}, JumpException.class, none, String.class);
         }
         
         public void metaclass() {
             invokeIRubyObject("getMetaClass", sig(RubyClass.class));
         }
         
         public void getVisibilityFor(String name) {
             method.ldc(name);
             method.invokevirtual(p(RubyClass.class), "searchMethod", sig(DynamicMethod.class, params(String.class)));
             method.invokevirtual(p(DynamicMethod.class), "getVisibility", sig(Visibility.class));
         }
         
         public void isPrivate(Object gotoToken, int toConsume) {
             method.invokevirtual(p(Visibility.class), "isPrivate", sig(boolean.class));
             Label temp = new Label();
             method.ifeq(temp); // EQ == 0 (i.e. false)
             while((toConsume--) > 0) {
                   method.pop();
             }
             method.go_to((Label)gotoToken);
             method.label(temp);
         }
         
         public void isNotProtected(Object gotoToken, int toConsume) {
             method.invokevirtual(p(Visibility.class), "isProtected", sig(boolean.class));
             Label temp = new Label();
             method.ifne(temp);
             while((toConsume--) > 0) {
                   method.pop();
             }
             method.go_to((Label)gotoToken);
             method.label(temp);
         }
         
         public void selfIsKindOf(Object gotoToken) {
             method.invokevirtual(p(RubyClass.class), "getRealClass", sig(RubyClass.class));
             loadSelf();
             method.invokevirtual(p(RubyModule.class), "isInstance", sig(boolean.class, params(IRubyObject.class)));
             method.ifne((Label)gotoToken); // EQ != 0 (i.e. true)
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
             method.ifeq((Label)gotoToken);
             method.go_to(successJmp);
             method.label(falsePopJmp);
             method.pop();
             method.go_to((Label)gotoToken);
             method.label(successJmp);
         }
         
         public void ifSingleton(Object gotoToken) {
             method.invokevirtual(p(RubyModule.class), "isSingleton", sig(boolean.class));
             method.ifne((Label)gotoToken); // EQ == 0 (i.e. false)
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
             method.ifeq((Label)token);
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
             invokeIRuby("getGlobalVariables", sig(GlobalVariables.class));
             method.ldc(newName);
             method.ldc(oldName);
             method.invokevirtual(p(GlobalVariables.class), "alias", sig(Void.TYPE, params(String.class, String.class)));
             loadNil();
         }
         
         public void undefMethod(String name) {
             loadThreadContext();
             invokeThreadContext("getRubyClass", sig(RubyModule.class));
             
             Label notNull = new Label();
             method.dup();
             method.ifnonnull(notNull);
             method.pop();
             loadRuntime();
             method.ldc("No class to undef method '" + name + "'.");
             invokeIRuby("newTypeError", sig(RaiseException.class, params(String.class)));
             method.athrow();
             
             method.label(notNull);
             method.ldc(name);
             method.invokevirtual(p(RubyModule.class), "undef", sig(Void.TYPE, params(String.class)));
             
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
 
                             methodCompiler.invokeUtilityMethod("prepareSuperClass", sig(RubyClass.class, params(Ruby.class, IRubyObject.class)));
                         } else {
                             methodCompiler.method.aconst_null();
                         }
 
                         methodCompiler.loadThreadContext();
 
                         pathCallback.call(methodCompiler);
 
                         methodCompiler.invokeUtilityMethod("prepareClassNamespace", sig(RubyModule.class, params(ThreadContext.class, IRubyObject.class)));
 
                         methodCompiler.method.swap();
 
                         methodCompiler.method.ldc(name);
 
                         methodCompiler.method.swap();
 
                         methodCompiler.method.invokevirtual(p(RubyModule.class), "defineOrGetClassUnder", sig(RubyClass.class, params(String.class, RubyClass.class)));
                     } else {
                         methodCompiler.loadRuntime();
 
                         methodCompiler.method.aload(ARGS_INDEX);
                         methodCompiler.method.iconst_0();
                         methodCompiler.method.arrayload();
 
                         methodCompiler.invokeUtilityMethod("getSingletonClass", sig(RubyClass.class, params(Ruby.class, IRubyObject.class)));
                     }
 
                     // set self to the class
                     methodCompiler.method.dup();
                     methodCompiler.method.astore(SELF_INDEX);
 
                     // CLASS BODY
                     methodCompiler.loadThreadContext();
                     methodCompiler.method.swap();
 
                     // static scope
                     buildStaticScopeNames(methodCompiler.method, staticScope);
                     methodCompiler.invokeThreadContext("preCompiledClass", sig(Void.TYPE, params(RubyModule.class, String[].class)));
                 }
             };
 
             // Here starts the logic for the class definition
             Label start = new Label();
             Label end = new Label();
             Label after = new Label();
             Label noException = new Label();
             methodCompiler.method.trycatch(start, end, after, null);
 
             methodCompiler.beginClass(bodyPrep, staticScope);
 
             methodCompiler.method.label(start);
 
             bodyCallback.call(methodCompiler);
             methodCompiler.method.label(end);
             // finally with no exception
             methodCompiler.loadThreadContext();
             methodCompiler.invokeThreadContext("postCompiledClass", sig(Void.TYPE, params()));
             
             methodCompiler.method.go_to(noException);
             
             methodCompiler.method.label(after);
             // finally with exception
             methodCompiler.loadThreadContext();
             methodCompiler.invokeThreadContext("postCompiledClass", sig(Void.TYPE, params()));
             methodCompiler.method.athrow();
             
             methodCompiler.method.label(noException);
 
             methodCompiler.endMethod();
 
             // prepare to call class definition method
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             if (receiverCallback == null) {
                 method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
             } else {
                 // store the receiver in args array, to maintain a live reference until method returns
                 receiverCallback.call(this);
                 createObjectArray(1);
             }
             method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
 
             method.invokevirtual(classname, methodName, METHOD_SIGNATURE);
         }
 
         public void defineModule(final String name, final StaticScope staticScope, final CompilerCallback pathCallback, final CompilerCallback bodyCallback) {
             String methodName = "rubyclass__" + JavaNameMangler.mangleStringForCleanJavaIdentifier(name) + "__" + ++methodIndex;
 
             final ASMMethodCompiler methodCompiler = new ASMMethodCompiler(methodName, null);
 
             CompilerCallback bodyPrep = new CompilerCallback() {
                 public void call(MethodCompiler context) {
                     methodCompiler.loadThreadContext();
 
                     pathCallback.call(methodCompiler);
 
                     methodCompiler.invokeUtilityMethod("prepareClassNamespace", sig(RubyModule.class, params(ThreadContext.class, IRubyObject.class)));
 
                     methodCompiler.method.ldc(name);
 
                     methodCompiler.method.invokevirtual(p(RubyModule.class), "defineOrGetModuleUnder", sig(RubyModule.class, params(String.class)));
 
                     // set self to the class
                     methodCompiler.method.dup();
                     methodCompiler.method.astore(SELF_INDEX);
 
                     // CLASS BODY
                     methodCompiler.loadThreadContext();
                     methodCompiler.method.swap();
 
                     // static scope
                     buildStaticScopeNames(methodCompiler.method, staticScope);
 
                     methodCompiler.invokeThreadContext("preCompiledClass", sig(Void.TYPE, params(RubyModule.class, String[].class)));
                 }
             };
 
             // Here starts the logic for the class definition
             Label start = new Label();
             Label end = new Label();
             Label after = new Label();
             Label noException = new Label();
             methodCompiler.method.trycatch(start, end, after, null);
             
             methodCompiler.beginClass(bodyPrep, staticScope);
 
             methodCompiler.method.label(start);
 
             bodyCallback.call(methodCompiler);
             methodCompiler.method.label(end);
             
             methodCompiler.method.go_to(noException);
             
             methodCompiler.method.label(after);
             methodCompiler.loadThreadContext();
             methodCompiler.invokeThreadContext("postCompiledClass", sig(Void.TYPE, params()));
             methodCompiler.method.athrow();
             
             methodCompiler.method.label(noException);
             methodCompiler.loadThreadContext();
             methodCompiler.invokeThreadContext("postCompiledClass", sig(Void.TYPE, params()));
 
             methodCompiler.endMethod();
 
             // prepare to call class definition method
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
             method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
 
             method.invokevirtual(classname, methodName, METHOD_SIGNATURE);
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
             loadRuntime();
             loadThreadContext();
             if (closure != null) {
                 closure.call(this);
             } else {
                 method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
             }
             loadSelf();
             
             invokeUtilityMethod("callZSuper", sig(IRubyObject.class, params(Ruby.class, ThreadContext.class, Block.class, IRubyObject.class)));
         }
         
         public void checkIsExceptionHandled() {
             // ruby exception and list of exception types is on the stack
             loadRuntime();
             loadThreadContext();
             loadSelf();
             invokeUtilityMethod("isExceptionHandled", sig(IRubyObject.class, RubyException.class, IRubyObject[].class, Ruby.class, ThreadContext.class, IRubyObject.class));
         }
         
         public void rethrowException() {
             loadException();
             method.athrow();
         }
         
         public void loadClass(String name) {
             loadRuntime();
             method.ldc(name);
             invokeIRuby("getClass", sig(RubyClass.class, String.class));
         }
         
         public void unwrapRaiseException() {
             // RaiseException is on stack, get RubyException out
             method.invokevirtual(p(RaiseException.class), "getException", sig(RubyException.class));
         }
         
         public void loadException() {
             method.aload(EXCEPTION_INDEX);
         }
         
         public void setPosition(ISourcePosition position) {
             if (!RubyInstanceConfig.POSITIONLESS_COMPILE_ENABLED) {
                 // FIXME I'm still not happy with this additional overhead per line,
                 // nor about the extra script construction cost, but it will have to do for now.
                 loadThreadContext();
                 getCacheCompiler().cachePosition(method, position.getFile(), position.getEndLine());
                 invokeThreadContext("setPosition", sig(void.class, ISourcePosition.class));
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
             ++methodIndex;
             String methodName;
             if (root && Boolean.getBoolean("jruby.compile.toplevel")) {
                 methodName = name;
             } else {
                 methodName = JavaNameMangler.mangleStringForCleanJavaIdentifier(name) + "__" + methodIndex;
             }
 
             MethodCompiler methodCompiler = startMethod(methodName, args, scope, inspector);
 
             // callbacks to fill in method body
             body.call(methodCompiler);
 
             methodCompiler.endMethod();
 
             // prepare to call "def" utility method to handle def logic
             loadThreadContext();
 
             loadSelf();
             
             if (receiver != null) receiver.call(this);
             
             // script object
             method.aload(THIS);
 
             method.ldc(name);
 
             method.ldc(methodName);
 
             buildStaticScopeNames(method, scope);
 
             method.ldc(methodArity);
             
             // arities
             method.ldc(scope.getRequiredArgs());
             method.ldc(scope.getOptionalArgs());
             method.ldc(scope.getRestArg());
             
             // if method has frame aware methods or frameless compilation is NOT enabled
             if (inspector.hasFrameAwareMethods() || !RubyInstanceConfig.FRAMELESS_COMPILE_ENABLED) {
                 if (inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                     method.getstatic(p(CallConfiguration.class), CallConfiguration.FRAME_AND_SCOPE.name(), ci(CallConfiguration.class));
                 } else {
                     method.getstatic(p(CallConfiguration.class), CallConfiguration.FRAME_ONLY.name(), ci(CallConfiguration.class));
                 }
             } else {
                 if (inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                     // TODO: call config with scope but no frame
                     method.getstatic(p(CallConfiguration.class), CallConfiguration.BACKTRACE_AND_SCOPE.name(), ci(CallConfiguration.class));
                 } else {
                     method.getstatic(p(CallConfiguration.class), CallConfiguration.BACKTRACE_ONLY.name(), ci(CallConfiguration.class));
                 }
             }
             
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
     }
 
     public class ASMClosureCompiler extends AbstractMethodCompiler {
         private String closureMethodName;
         
         public ASMClosureCompiler(String closureMethodName, String closureFieldName, ASTInspector inspector) {
             this.closureMethodName = closureMethodName;
 
             // declare the field
             getClassVisitor().visitField(ACC_PRIVATE, closureFieldName, ci(CompiledBlockCallback.class), null, null);
             
             method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, closureMethodName, CLOSURE_SIGNATURE, null, null));
             if (inspector == null) {
-                variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX);
+                variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
             } else if (inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                 // enable "boxed" variable compilation when only a closure present
                 // this breaks using a proc as a binding
                 if (Boolean.getBoolean("jruby.compile.boxed") && !inspector.hasScopeAwareMethods()) {
-                    variableCompiler = new BoxedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX);
+                    variableCompiler = new BoxedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
                 } else {
-                    variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX);
+                    variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
                 }
             } else {
-                variableCompiler = new StackBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, ARGS_INDEX, CLOSURE_INDEX);
+                variableCompiler = new StackBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
             }
             invocationCompiler = new StandardInvocationCompiler(this, method);
         }
 
         public void beginMethod(CompilerCallback args, StaticScope scope) {
             method.start();
 
             // set up a local IRuby variable
             method.aload(THREADCONTEXT_INDEX);
             invokeThreadContext("getRuntime", sig(Ruby.class));
             method.dup();
             method.astore(RUNTIME_INDEX);
             
             // grab nil for local variables
             invokeIRuby("getNil", sig(IRubyObject.class));
             method.astore(NIL_INDEX);
             
             variableCompiler.beginClosure(args, scope);
 
             // start of scoping for closure's vars
             scopeStart = new Label();
             scopeEnd = new Label();
             redoJump = new Label();
             method.label(scopeStart);
         }
 
         public void beginClass(CompilerCallback bodyPrep, StaticScope scope) {
             throw new NotCompilableException("ERROR: closure compiler should not be used for class bodies");
         }
 
         public void endMethod() {
             // end of scoping for closure's vars
             scopeEnd = new Label();
             method.areturn();
             method.label(scopeEnd);
             
             // handle redos by restarting the block
             method.pop();
             method.go_to(scopeStart);
             
             method.trycatch(scopeStart, scopeEnd, scopeEnd, p(JumpException.RedoJump.class));
             method.end();
         }
 
         @Override
         public void loadBlock() {
             loadThreadContext();
             invokeThreadContext("getFrameBlock", sig(Block.class));
         }
 
         @Override
         protected String getNewRescueName() {
             return closureMethodName + "_" + super.getNewRescueName();
         }
 
         @Override
         protected String getNewEnsureName() {
             return closureMethodName + "_" + super.getNewEnsureName();
         }
 
         public void performReturn() {
             loadThreadContext();
             invokeUtilityMethod("returnJump", sig(IRubyObject.class, IRubyObject.class, ThreadContext.class));
         }
 
         public void processRequiredArgs(Arity arity, int requiredArgs, int optArgs, int restArg) {
             throw new NotCompilableException("Shouldn't be calling this...");
         }
 
         public void assignOptionalArgs(Object object, int expectedArgsCount, int size, ArrayCallback optEval) {
             throw new NotCompilableException("Shouldn't be calling this...");
         }
 
         public void processRestArg(int startIndex, int restArg) {
             throw new NotCompilableException("Shouldn't be calling this...");
         }
 
         public void processBlockArgument(int index) {
             loadRuntime();
             loadThreadContext();
             loadBlock();
             method.ldc(new Integer(index));
             invokeUtilityMethod("processBlockArgument", sig(void.class, params(Ruby.class, ThreadContext.class, Block.class, int.class)));
         }
         
         public void issueBreakEvent(CompilerCallback value) {
             if (withinProtection || currentLoopLabels == null) {
                 value.call(this);
                 invokeUtilityMethod("breakJump", sig(IRubyObject.class, IRubyObject.class));
             } else {
                 value.call(this);
                 issueLoopBreak();
             }
         }
 
         public void issueNextEvent(CompilerCallback value) {
             if (withinProtection || currentLoopLabels == null) {
                 value.call(this);
                 invokeUtilityMethod("nextJump", sig(IRubyObject.class, IRubyObject.class));
             } else {
                 value.call(this);
                 issueLoopNext();
             }
         }
 
         public void issueRedoEvent() {
             // FIXME: This isn't right for within ensured/rescued code
             if (withinProtection) {
                 invokeUtilityMethod("redoJump", sig(IRubyObject.class));
             } else if (currentLoopLabels != null) {
                 issueLoopRedo();
             } else {
                 // jump back to the top of the main body of this closure
                 method.go_to(scopeStart);
             }
         }
     }
 
     public class ASMMethodCompiler extends AbstractMethodCompiler {
         private String friendlyName;
 
         public ASMMethodCompiler(String friendlyName, ASTInspector inspector) {
             this.friendlyName = friendlyName;
 
             method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC, friendlyName, METHOD_SIGNATURE, null, null));
             if (inspector == null) {
-                variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX);
+                variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
             } else if (inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                 // enable "boxed" variable compilation when only a closure present
                 // this breaks using a proc as a binding
                 if (Boolean.getBoolean("jruby.compile.boxed") && !inspector.hasScopeAwareMethods()) {
-                    variableCompiler = new BoxedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX);
+                    variableCompiler = new BoxedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
                 } else {
-                    variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX);
+                    variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
                 }
             } else {
-                variableCompiler = new StackBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, ARGS_INDEX, CLOSURE_INDEX);
+                variableCompiler = new StackBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
             }
             invocationCompiler = new StandardInvocationCompiler(this, method);
         }
         
         public void beginChainedMethod() {
             method.aload(THREADCONTEXT_INDEX);
             method.dup();
             method.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
             method.dup();
             method.astore(RUNTIME_INDEX);
 
             // grab nil for local variables
             method.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             method.astore(NIL_INDEX);
 
             method.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
             method.dup();
             method.astore(DYNAMIC_SCOPE_INDEX);
             method.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
             method.astore(VARS_ARRAY_INDEX);
         }
 
         public void beginMethod(CompilerCallback args, StaticScope scope) {
             method.start();
 
             // set up a local IRuby variable
             method.aload(THREADCONTEXT_INDEX);
             invokeThreadContext("getRuntime", sig(Ruby.class));
             method.dup();
             method.astore(RUNTIME_INDEX);
             
             
             // grab nil for local variables
             invokeIRuby("getNil", sig(IRubyObject.class));
             method.astore(NIL_INDEX);
             
             variableCompiler.beginMethod(args, scope);
 
             // visit a label to start scoping for local vars in this method
             Label start = new Label();
             method.label(start);
 
             scopeStart = start;
         }
 
         public void beginClass(CompilerCallback bodyPrep, StaticScope scope) {
             method.start();
 
             // set up a local IRuby variable
             method.aload(THREADCONTEXT_INDEX);
             invokeThreadContext("getRuntime", sig(Ruby.class));
             method.dup();
             method.astore(RUNTIME_INDEX);
             
             // grab nil for local variables
             invokeIRuby("getNil", sig(IRubyObject.class));
             method.astore(NIL_INDEX);
             
             variableCompiler.beginClass(bodyPrep, scope);
 
             // visit a label to start scoping for local vars in this method
             Label start = new Label();
             method.label(start);
 
             scopeStart = start;
         }
 
         public void endMethod() {
             // return last value from execution
             method.areturn();
 
             // end of variable scope
             Label end = new Label();
             method.label(end);
 
             method.end();
         }
         
         public void performReturn() {
             // normal return for method body. return jump for within a begin/rescue/ensure
             if (withinProtection) {
                 loadThreadContext();
                 invokeUtilityMethod("returnJump", sig(IRubyObject.class, IRubyObject.class, ThreadContext.class));
             } else {
                 method.areturn();
             }
         }
 
         public void issueBreakEvent(CompilerCallback value) {
             if (withinProtection) {
                 value.call(this);
                 invokeUtilityMethod("breakJump", sig(IRubyObject.class, IRubyObject.class));
             } else if (currentLoopLabels != null) {
                 value.call(this);
                 issueLoopBreak();
             } else {
                 // in method body with no containing loop, issue jump error
                 // load runtime and value, issue jump error
                 loadRuntime();
                 value.call(this);
                 invokeUtilityMethod("breakLocalJumpError", sig(IRubyObject.class, Ruby.class, IRubyObject.class));
             }
         }
 
         public void issueNextEvent(CompilerCallback value) {
             if (withinProtection) {
                 value.call(this);
                 invokeUtilityMethod("nextJump", sig(IRubyObject.class, IRubyObject.class));
             } else if (currentLoopLabels != null) {
                 value.call(this);
                 issueLoopNext();
             } else {
                 // in method body with no containing loop, issue jump error
                 // load runtime and value, issue jump error
                 loadRuntime();
                 value.call(this);
                 invokeUtilityMethod("nextLocalJumpError", sig(IRubyObject.class, Ruby.class, IRubyObject.class));
             }
         }
 
         public void issueRedoEvent() {
             if (withinProtection) {
                 invokeUtilityMethod("redoJump", sig(IRubyObject.class));
             } else if (currentLoopLabels != null) {
                 issueLoopRedo();
             } else {
                 // in method body with no containing loop, issue jump error
                 // load runtime and value, issue jump error
                 loadRuntime();
                 invokeUtilityMethod("redoLocalJumpError", sig(IRubyObject.class, Ruby.class));
             }
         }
     }
 
     private int constants = 0;
 
     public String getNewConstant(String type, String name_prefix) {
         return getNewConstant(type, name_prefix, null);
     }
 
     public String getNewConstant(String type, String name_prefix, Object init) {
         ClassVisitor cv = getClassVisitor();
 
         String realName;
         synchronized (this) {
             realName = "_" + constants++;
         }
 
         // declare the field
         cv.visitField(ACC_PRIVATE, realName, type, null, null).visitEnd();
 
         if(init != null) {
             initMethod.aload(THIS);
             initMethod.ldc(init);
             initMethod.putfield(classname, realName, type);
         }
 
         return realName;
     }
 
     public String getNewField(String type, String name, Object init) {
         ClassVisitor cv = getClassVisitor();
 
         // declare the field
         cv.visitField(ACC_PRIVATE, name, type, null, null).visitEnd();
 
         if(init != null) {
             initMethod.aload(THIS);
             initMethod.ldc(init);
             initMethod.putfield(classname, name, type);
         }
 
         return name;
     }
 
     public String getNewStaticConstant(String type, String name_prefix) {
         ClassVisitor cv = getClassVisitor();
 
         String realName;
         synchronized (this) {
             realName = "__" + constants++;
         }
 
         // declare the field
         cv.visitField(ACC_PRIVATE | ACC_STATIC, realName, type, null, null).visitEnd();
         return realName;
     }
 }
diff --git a/src/org/jruby/compiler/impl/StandardInvocationCompiler.java b/src/org/jruby/compiler/impl/StandardInvocationCompiler.java
index b3f838a88f..be65c4cff0 100644
--- a/src/org/jruby/compiler/impl/StandardInvocationCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardInvocationCompiler.java
@@ -1,417 +1,453 @@
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
 
 import org.jruby.RubyModule;
 import org.jruby.compiler.ArgumentsCallback;
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.compiler.InvocationCompiler;
 import org.jruby.exceptions.JumpException;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import static org.jruby.util.CodegenUtils.*;
 import org.objectweb.asm.Label;
 
 /**
  *
  * @author headius
  */
 public class StandardInvocationCompiler implements InvocationCompiler {
     private StandardASMCompiler.AbstractMethodCompiler methodCompiler;
     private SkinnyMethodAdapter method;
     
     private static final int THIS = 0;
 
     public StandardInvocationCompiler(StandardASMCompiler.AbstractMethodCompiler methodCompiler, SkinnyMethodAdapter method) {
         this.methodCompiler = methodCompiler;
         this.method = method;
     }
 
     public SkinnyMethodAdapter getMethodAdapter() {
         return this.method;
     }
 
     public void setMethodAdapter(SkinnyMethodAdapter sma) {
         this.method = sma;
     }
 
     public void invokeAttrAssign(String name) {
         // start with [recv, args]
         // get args[length - 1] and stuff it under the receiver
         // dup args * 2
         method.dup(); // [recv, args, args]
         method.dup(); // [recv, args, args, args]
         method.arraylength(); // [recv, args, args, len]
         method.iconst_1(); // [recv, args, args, len, 1]
         method.isub(); // [recv, args, args, len-1]
         // load from array
         method.arrayload(); // [recv, args, val]
         method.dup_x2(); // [val, recv, args, val]
         method.pop(); // [val, recv, args]
         invokeDynamic(name, true, true, CallType.NORMAL, null, true); // [val, result]
         // pop result, use args[length - 1] captured above
         method.pop(); // [val]
     }
 
-    public void invokeAttrAssignSimple(String name, CompilerCallback receiverCallback, ArgumentsCallback argsCallback) {
+    public void invokeAttrAssign(String name, CompilerCallback receiverCallback, ArgumentsCallback argsCallback) {
         Label variableCallType = new Label();
         Label readyForCall = new Label();
         
         // receiver first, so we know which call site to use
         receiverCallback.call(methodCompiler);
         
         // select appropriate call site
         method.dup(); // dup receiver
         methodCompiler.loadSelf(); // load self
         method.if_acmpeq(variableCallType); // compare
         
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(method, name, CallType.NORMAL);
         method.go_to(readyForCall);
         method.label(variableCallType);
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(method, name, CallType.VARIABLE);
         method.label(readyForCall);
         
         // call site under receiver
         method.swap();
         
-        // load single argument and dup it under  call site
-        argsCallback.call(methodCompiler);
-        method.dup_x2();
-        
         // load thread context under receiver
         methodCompiler.loadThreadContext();
-        method.dup_x2();
-        method.pop();
+        method.swap();
+        
+        String signature = null;
+        switch (argsCallback.getArity()) {
+        case 1:
+            signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class));
+            break;
+        case 2:
+            signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class));
+            break;
+        case 3:
+            signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class));
+            break;
+        default:
+            signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject[].class));
+        }
+        
+        argsCallback.call(methodCompiler);
+        
+        
+        // store in temp variable
+        int tempLocal = methodCompiler.variableCompiler.grabTempLocal();
+        switch (argsCallback.getArity()) {
+        case 1:
+        case 2:
+        case 3:
+            // specific-arity args, just save off top of stack
+            method.dup();
+            break;
+        default:
+            // variable-arity args, peel off and save the last argument in the array
+            method.dup(); // [args, args]
+            method.dup(); // [args, args, args]
+            method.arraylength(); // [args, args, len]
+            method.iconst_1(); // [args, args, len, 1]
+            method.isub(); // [args, args, len-1]
+            // load from array
+            method.arrayload(); // [args, val]
+        }
+        methodCompiler.variableCompiler.setTempLocal(tempLocal);
         
         // invoke call site
-        String signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class));
         method.invokevirtual(p(CallSite.class), "call", signature);
         
-        // pop the return value leaving the dup'ed args on the stack
+        // pop the return value and restore the dup'ed arg on the stack
         method.pop();
+        
+        methodCompiler.variableCompiler.getTempLocal(tempLocal);
+        methodCompiler.variableCompiler.releaseTempLocal();
     }
     
     public void opElementAsgn(CompilerCallback valueCallback, String operator) {
         // FIXME: op element asgn is not yet using CallAdapter. Boo hoo.
         
         // receiver and args are already on the stack
         methodCompiler.method.dup2();
         
         // invoke the [] operator and dup the result
         invokeDynamic("[]", true, true, CallType.FUNCTIONAL, null, false);
         methodCompiler.method.dup();
         
         // stack is now: .. receiver, args, result, result
         Label end = new Label();
         if (operator == "||") {
             Label falseResult = new Label();
             methodCompiler.invokeIRubyObject("isTrue", sig(boolean.class)); // receiver, args, result, istrue
             methodCompiler.method.ifeq(falseResult); // receiver, args, result
             
             // it's true, clear everything but the result
             methodCompiler.method.dup_x2(); // result, receiver, args, result
             methodCompiler.method.pop(); // result, receiver, args
             methodCompiler.method.pop2(); // result
             methodCompiler.method.go_to(end);
             
             // it's false, stuff the element in
             methodCompiler.method.label(falseResult);
             // START: .. receiver, args, result
             methodCompiler.method.pop(); // receiver, args
             valueCallback.call(methodCompiler); // receiver, args, value
             // save the value rather than using the result of the []= call
             methodCompiler.method.dup_x2(); // value, receiver, args, value
             methodCompiler.appendToObjectArray(); // value, receiver, combinedArgs
             invokeDynamic("[]=", true, true, CallType.FUNCTIONAL, null, false); // value, assignmentResult
             // pop result
             methodCompiler.method.pop();
             
             methodCompiler.method.label(end);
         } else if (operator == "&&") {
             // TODO: This is the reverse of the above logic, and could probably be abstracted better
             Label falseResult = new Label();
             methodCompiler.invokeIRubyObject("isTrue", sig(boolean.class));
             methodCompiler.method.ifeq(falseResult);
             
             // it's true, stuff the element in
             // START: .. receiver, args, result
             methodCompiler.method.pop(); // receiver, args
             valueCallback.call(methodCompiler); // receiver, args, value
             methodCompiler.appendToObjectArray(); // receiver, combinedArgs
             invokeDynamic("[]=", true, true, CallType.FUNCTIONAL, null, false); // assignmentResult
             methodCompiler.method.go_to(end);
             
             // it's false, clear everything but the result
             methodCompiler.method.label(falseResult);
             methodCompiler.method.dup_x2();
             methodCompiler.method.pop();
             methodCompiler.method.pop2();
             
             methodCompiler.method.label(end);
         } else {
             // remove extra result, operate on it, and reassign with original args
             methodCompiler.method.pop();
             // START: .. receiver, args, result
             valueCallback.call(methodCompiler); // receiver, args, result, value
             methodCompiler.createObjectArray(1);
             invokeDynamic(operator, true, true, CallType.FUNCTIONAL, null, false); // receiver, args, newresult
             methodCompiler.appendToObjectArray(); // receiver, newargs
             invokeDynamic("[]=", true, true, CallType.FUNCTIONAL, null, false); // assignmentResult
         }
     }
 
     public void invokeSuper(CompilerCallback argsCallback, CompilerCallback closureArg) {
         methodCompiler.loadThreadContext();
         methodCompiler.invokeUtilityMethod("checkSuperDisabled", sig(void.class, ThreadContext.class));
         
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
 
     public void invokeDynamic(String name, CompilerCallback receiverCallback, ArgumentsCallback argsCallback, CallType callType, CompilerCallback closureArg, boolean attrAssign) {
         if (receiverCallback != null) {
             receiverCallback.call(methodCompiler);
         } else {
             methodCompiler.loadSelf();
         }
         
         // load call adapter
         // FIXME: These swaps suck, but OpAsgn breaks if it can't dup receiver in the middle of making this call :(
         methodCompiler.getScriptCompiler().getCacheCompiler().cacheCallSite(method, name, callType);
         method.swap();
 
         methodCompiler.loadThreadContext(); // [adapter, tc]
         method.swap();
         
         String signature;
         // args
         if (argsCallback == null) {
             // block
             if (closureArg == null) {
                 // no args, no block
                 signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class));
             } else {
                 // no args, with block
                 closureArg.call(methodCompiler);
                 signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, Block.class));
             }
         } else {
             argsCallback.call(methodCompiler);
             // block
             if (closureArg == null) {
                 // with args, no block
                 switch (argsCallback.getArity()) {
                 case 1:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class));
                     break;
                 case 2:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class));
                     break;
                 case 3:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class));
                     break;
                 default:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject[].class));
                 }
             } else {
                 // with args, with block
                 closureArg.call(methodCompiler);
                 
                 switch (argsCallback.getArity()) {
                 case 1:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, Block.class));
                     break;
                 case 2:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class));
                     break;
                 case 3:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class));
                     break;
                 default:
                     signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class));
                 }
             }
         }
         
         // adapter, tc, recv, args{0,1}, block{0,1}]
 
         method.invokevirtual(p(CallSite.class), "call", signature);
     }
 
     private void invokeDynamic(String name, boolean hasReceiver, boolean hasArgs, CallType callType, CompilerCallback closureArg, boolean attrAssign) {
         String callSig = sig(IRubyObject.class, params(IRubyObject.class, IRubyObject[].class, ThreadContext.class, String.class, IRubyObject.class, CallType.class, Block.class));
         String callSigIndexed = sig(IRubyObject.class, params(IRubyObject.class, IRubyObject[].class, ThreadContext.class, Byte.TYPE, String.class, IRubyObject.class, CallType.class, Block.class));
 
         int index = MethodIndex.getIndex(name);
 
         if (hasArgs) {
             if (hasReceiver) {
                 // Call with args
                 // receiver already present
             } else {
                 // FCall
                 // no receiver present, use self
                 methodCompiler.loadSelf();
                 // put self under args
                 method.swap();
             }
         } else {
             if (hasReceiver) {
                 // receiver already present
                 // empty args list
                 method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
             } else {
                 // VCall
                 // no receiver present, use self
                 methodCompiler.loadSelf();
 
                 // empty args list
                 method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
             }
         }
 
         methodCompiler.loadThreadContext();
 
         if (index != 0) {
             // load method index
             method.ldc(new Integer(index));
         }
 
         method.ldc(name);
 
         // load self for visibility checks
         methodCompiler.loadSelf();
 
         method.getstatic(p(CallType.class), callType.toString(), ci(CallType.class));
 
         if (closureArg == null) {
             method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
         } else {
             closureArg.call(methodCompiler);
         }
 
         Label tryBegin = new Label();
         Label tryEnd = new Label();
         Label tryCatch = new Label();
         if (closureArg != null) {
             // wrap with try/catch for block flow-control exceptions
             // FIXME: for flow-control from containing blocks, but it's not working right;
             // stack is not as expected for invoke calls below...
             //method.trycatch(tryBegin, tryEnd, tryCatch, p(JumpException.class));
             method.label(tryBegin);
         }
 
         if (attrAssign) {
             if (index != 0) {
                 methodCompiler.invokeUtilityMethod("doAttrAssignIndexed", callSigIndexed);
             } else {
                 methodCompiler.invokeUtilityMethod("doAttrAssign", callSig);
             }
         } else {
             if (index != 0) {
                 methodCompiler.invokeUtilityMethod("doInvokeDynamicIndexed", callSigIndexed);
             } else {
                 methodCompiler.invokeUtilityMethod("doInvokeDynamic", callSig);
             }
         }
 
         if (closureArg != null) {
             method.label(tryEnd);
 
             // no physical break, terminate loop and skip catch block
             Label normalEnd = new Label();
             method.go_to(normalEnd);
 
             method.label(tryCatch);
             {
                 methodCompiler.loadBlock();
                 methodCompiler.invokeUtilityMethod("handleJumpException", sig(IRubyObject.class, params(JumpException.class, Block.class)));
             }
 
             method.label(normalEnd);
         }
     }
 
     public void yield(boolean hasArgs, boolean unwrap) {
         methodCompiler.loadBlock();
 
         if (hasArgs) {
             method.swap();
 
             methodCompiler.loadThreadContext();
             method.swap();
 
             // args now here
         } else {
             methodCompiler.loadThreadContext();
 
             // empty args
             method.aconst_null();
         }
 
         method.aconst_null();
         method.aconst_null();
         method.ldc(new Boolean(unwrap));
 
         method.invokevirtual(p(Block.class), "yield", sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class, RubyModule.class, Boolean.TYPE)));
     }
 
     public void invokeEqq() {
         invokeDynamic("===", true, true, CallType.NORMAL, null, false); // [val, result]
     }
 }
diff --git a/test/bench/bench_attr_assign.rb b/test/bench/bench_attr_assign.rb
index fabe117983..2057d35ee8 100644
--- a/test/bench/bench_attr_assign.rb
+++ b/test/bench/bench_attr_assign.rb
@@ -1,84 +1,84 @@
 require 'benchmark'
 
 class Foo < Array
   attr_accessor :bar
 
   def control
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
     self; 1; self; 1; self; 1; self; 1; self; 1
   end
   
   def hundred_assigns
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
     self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1; self.bar = 1
   end
   
   def hundred_array_assigns
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
     self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1; self[1] = 1
   end
 end
 
 foo = Foo.new
 
 (ARGV[0] || 10).to_i.times {
   Benchmark.bm(20) {|bm|
-    bm.report("control") { 10000.times { foo.control } }
-    bm.report("100 asgns") { 10000.times { foo.hundred_assigns } }
-    bm.report("100 array asgns") { 10000.times { foo.hundred_array_assigns } }
+    bm.report("control") { 100000.times { foo.control } }
+    bm.report("100 asgns") { 100000.times { foo.hundred_assigns } }
+    bm.report("100 array asgns") { 100000.times { foo.hundred_array_assigns } }
   }
 }
\ No newline at end of file
