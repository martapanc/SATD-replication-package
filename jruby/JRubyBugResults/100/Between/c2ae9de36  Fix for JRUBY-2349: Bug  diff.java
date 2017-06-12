diff --git a/src/org/jruby/ast/Colon2Node.java b/src/org/jruby/ast/Colon2Node.java
index f9887abfca..499535bef9 100644
--- a/src/org/jruby/ast/Colon2Node.java
+++ b/src/org/jruby/ast/Colon2Node.java
@@ -1,150 +1,148 @@
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
 import org.jruby.evaluator.Instruction;
 import org.jruby.exceptions.JumpException;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.IdUtil;
 
 /** 
  * Represents a '::' constant access or method call (Java::JavaClass).
  */
 public final class Colon2Node extends Colon3Node implements INameNode {
     private final Node leftNode;
     private final boolean isConstant;
 
     public Colon2Node(ISourcePosition position, Node leftNode, String name) {
         super(position, NodeType.COLON2NODE, name);
         this.leftNode = leftNode;
         this.isConstant = IdUtil.isConstant(name);
     }
     
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     @Override
     public Instruction accept(NodeVisitor iVisitor) {
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
     
     @Override
     public String toString() {
         String result = "Colon2Node [";
         if (leftNode != null) result += leftNode;
         result += getName();
         return result + "]";
     }
  
     /** Get parent module/class that this module represents */
     @Override
     public RubyModule getEnclosingModule(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
         if (leftNode != null) {
             IRubyObject result = leftNode.interpret(runtime, context, self, aBlock);
             return RuntimeHelpers.prepareClassNamespace(context, result);
         } else {
             return context.getCurrentScope().getStaticScope().getModule();
         }
     }
     
     @Override
     public IRubyObject interpret(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
         // TODO: Made this more colon3 friendly because of cpath production
         // rule in grammar (it is convenient to think of them as the same thing
         // at a grammar level even though evaluation is).
 
         // TODO: Can we eliminate leftnode by making implicit Colon3node?
         if (leftNode == null) return runtime.getObject().fastGetConstantFrom(name);
 
         IRubyObject result = leftNode.interpret(runtime, context, self, aBlock);
         
         if (isConstant) {
             return getConstantFrom(runtime, result);
         }
 
         return RuntimeHelpers.invoke(context, result, name, aBlock);
     }
     
     private IRubyObject getConstantFrom(Ruby runtime, IRubyObject result) {
-        if (result instanceof RubyModule) return ((RubyModule) result).fastGetConstantFrom(name);
-
-        throw runtime.newTypeError(result + " is not a class/module");
+        return RuntimeHelpers.checkIsModule(result).fastGetConstantFrom(name);
     }
     
     @Override
     public String definition(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
        try {
             IRubyObject left = leftNode.interpret(runtime, context, self, aBlock);
 
             if (isModuleAndHasConstant(left)) {
                 return "constant";
             } else if (hasMethod(left)) {
                 return "method";
             }
         } catch (JumpException e) {
         }
             
         return null;
     }
     
     private boolean isModuleAndHasConstant(IRubyObject left) {
         return left instanceof RubyModule && ((RubyModule) left).fastGetConstantAt(name) != null;
     }
     
     private boolean hasMethod(IRubyObject left) {
         return left.getMetaClass().isMethodBound(name, true);
     }
  }
diff --git a/src/org/jruby/compiler/ASTCompiler.java b/src/org/jruby/compiler/ASTCompiler.java
index 51c4aa14d2..9aeffd9374 100644
--- a/src/org/jruby/compiler/ASTCompiler.java
+++ b/src/org/jruby/compiler/ASTCompiler.java
@@ -1,2170 +1,2171 @@
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
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.Iterator;
 import java.util.List;
 import org.jruby.RubyFixnum;
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
+import org.jruby.util.IdUtil;
 
 /**
  *
  * @author headius
  */
 public class ASTCompiler {
     private boolean isAtRoot = true;
     
     public void compile(Node node, BodyCompiler context) {
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
 
     public void compileArguments(Node node, BodyCompiler context) {
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
         
         public void call(BodyCompiler context) {
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
         
         public void call(BodyCompiler context) {
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
 
     public void compileAssignment(Node node, BodyCompiler context) {
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
 
     public void compileAlias(Node node, BodyCompiler context) {
         final AliasNode alias = (AliasNode) node;
 
         context.defineAlias(alias.getNewName(), alias.getOldName());
     }
 
     public void compileAnd(Node node, BodyCompiler context) {
         final AndNode andNode = (AndNode) node;
 
         compile(andNode.getFirstNode(), context);
 
         BranchCallback longCallback = new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         compile(andNode.getSecondNode(), context);
                     }
                 };
 
         context.performLogicalAnd(longCallback);
     }
 
     public void compileArray(Node node, BodyCompiler context) {
         ArrayNode arrayNode = (ArrayNode) node;
 
         ArrayCallback callback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray, int index) {
                         Node node = (Node) ((Object[]) sourceArray)[index];
                         compile(node, context);
                     }
                 };
 
         context.createNewArray(arrayNode.childNodes().toArray(), callback, arrayNode.isLightweight());
     }
 
     public void compileArgsCat(Node node, BodyCompiler context) {
         ArgsCatNode argsCatNode = (ArgsCatNode) node;
 
         compile(argsCatNode.getFirstNode(), context);
         context.ensureRubyArray();
         compile(argsCatNode.getSecondNode(), context);
         context.splatCurrentValue();
         context.concatArrays();
     }
 
     public void compileArgsPush(Node node, BodyCompiler context) {
         ArgsPushNode argsPush = (ArgsPushNode) node;
 
         compile(argsPush.getFirstNode(), context);
         compile(argsPush.getSecondNode(), context);
         context.concatArrays();
     }
 
     private void compileAttrAssign(Node node, BodyCompiler context) {
         final AttrAssignNode attrAssignNode = (AttrAssignNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(attrAssignNode.getReceiverNode(), context);
             }
         };
         
         ArgumentsCallback argsCallback = getArgsCallback(attrAssignNode.getArgsNode());
 
         context.getInvocationCompiler().invokeAttrAssign(attrAssignNode.getName(), receiverCallback, argsCallback);
     }
 
     public void compileAttrAssignAssignment(Node node, BodyCompiler context) {
         final AttrAssignNode attrAssignNode = (AttrAssignNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(attrAssignNode.getReceiverNode(), context);
             }
         };
         ArgumentsCallback argsCallback = getArgsCallback(attrAssignNode.getArgsNode());
 
         context.getInvocationCompiler().invokeAttrAssignMasgn(attrAssignNode.getName(), receiverCallback, argsCallback);
     }
 
     public void compileBackref(Node node, BodyCompiler context) {
         BackRefNode iVisited = (BackRefNode) node;
 
         context.performBackref(iVisited.getType());
     }
 
     public void compileBegin(Node node, BodyCompiler context) {
         BeginNode beginNode = (BeginNode) node;
 
         compile(beginNode.getBodyNode(), context);
     }
 
     public void compileBignum(Node node, BodyCompiler context) {
         context.createNewBignum(((BignumNode) node).getValue());
     }
 
     public void compileBlock(Node node, BodyCompiler context) {
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
 
     public void compileBreak(Node node, BodyCompiler context) {
         final BreakNode breakNode = (BreakNode) node;
 
         CompilerCallback valueCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (breakNode.getValueNode() != null) {
                             compile(breakNode.getValueNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         context.issueBreakEvent(valueCallback);
     }
 
     public void compileCall(Node node, BodyCompiler context) {
         final CallNode callNode = (CallNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(callNode.getReceiverNode(), context);
             }
         };
 
         ArgumentsCallback argsCallback = getArgsCallback(callNode.getArgsNode());
         CompilerCallback closureArg = getBlock(callNode.getIterNode());
 
         context.getInvocationCompiler().invokeDynamic(callNode.getName(), receiverCallback, argsCallback, CallType.NORMAL, closureArg, callNode.getIterNode() instanceof IterNode);
     }
 
     public void compileCase(Node node, BodyCompiler context) {
         CaseNode caseNode = (CaseNode) node;
 
         boolean hasCase = false;
         if (caseNode.getCaseNode() != null) {
             compile(caseNode.getCaseNode(), context);
             hasCase = true;
         }
 
         context.pollThreadEvents();
         
         Node firstWhenNode = caseNode.getFirstWhenNode();
 
         // NOTE: Currently this optimization is limited to the following situations:
         // * No multi-valued whens
         // * All whens must be an int-ranged literal fixnum
         // It also still emits the code for the "safe" when logic, which is rather
         // wasteful (since it essentially doubles each code body). As such it is
         // normally disabled, but it serves as an example of how this optimization
         // could be done. Ideally, it should be combined with the when processing
         // to improve code reuse before it's generally available.
         if (hasCase && caseIsAllIntRangedFixnums(caseNode) && RubyInstanceConfig.FASTCASE_COMPILE_ENABLED) {
             compileFixnumCase(firstWhenNode, context);
         } else {
             compileWhen(firstWhenNode, context, hasCase);
         }
     }
 
     private boolean caseIsAllIntRangedFixnums(CaseNode caseNode) {
         boolean caseIsAllLiterals = true;
         for (Node node = caseNode.getFirstWhenNode(); node != null && node instanceof WhenNode; node = ((WhenNode)node).getNextCase()) {
             WhenNode whenNode = (WhenNode)node;
             if (whenNode.getExpressionNodes() instanceof ArrayNode) {
                 ArrayNode arrayNode = (ArrayNode)whenNode.getExpressionNodes();
                 if (arrayNode.size() == 1 && arrayNode.get(0) instanceof FixnumNode) {
                     FixnumNode fixnumNode = (FixnumNode)arrayNode.get(0);
                     long value = fixnumNode.getValue();
                     if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
                         // OK! we can safely use it in a case
                         continue;
                     }
                 }
             }
             // if we get here, our rigid requirements have not been met; fail
             caseIsAllLiterals = false;
             break;
         }
         return caseIsAllLiterals;
     }
 
     @SuppressWarnings("unchecked")
     public void compileFixnumCase(final Node node, BodyCompiler context) {
         List<WhenNode> cases = new ArrayList<WhenNode>();
         Node elseBody = null;
 
         // first get all when nodes
         for (Node tmpNode = node; tmpNode != null; tmpNode = ((WhenNode)tmpNode).getNextCase()) {
             WhenNode whenNode = (WhenNode)tmpNode;
 
             cases.add(whenNode);
 
             if (whenNode.getNextCase() != null) {
                 // if there's another node coming, make sure it will be a when...
                 if (whenNode.getNextCase() instanceof WhenNode) {
                     // normal when, continue
                     continue;
                 } else {
                     // ...or handle it as an else and we're done
                     elseBody = whenNode.getNextCase();
                     break;
                 }
             }
         }
         // sort them based on literal value
         Collections.sort(cases, new Comparator() {
             public int compare(Object o1, Object o2) {
                 WhenNode w1 = (WhenNode)o1;
                 WhenNode w2 = (WhenNode)o2;
                 Integer value1 = (int)((FixnumNode)((ArrayNode)w1.getExpressionNodes()).get(0)).getValue();
                 Integer value2 = (int)((FixnumNode)((ArrayNode)w2.getExpressionNodes()).get(0)).getValue();
                 return value1.compareTo(value2);
             }
         });
         final int[] intCases = new int[cases.size()];
         final Node[] bodies = new Node[intCases.length];
 
         // prepare arrays of int cases and code bodies
         for (int i = 0 ; i < intCases.length; i++) {
             intCases[i] = (int)((FixnumNode)((ArrayNode)cases.get(i).getExpressionNodes()).get(0)).getValue();
             bodies[i] = cases.get(i).getBodyNode();
         }
         
         final ArrayCallback callback = new ArrayCallback() {
             public void nextValue(BodyCompiler context, Object array, int index) {
                 Node[] bodies = (Node[])array;
                 if (bodies[index] != null) {
                     compile(bodies[index], context);
                 } else {
                     context.loadNil();
                 }
             }
         };
 
         final Node finalElse = elseBody;
         final CompilerCallback elseCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 if (finalElse == null) {
                     context.loadNil();
                 } else {
                     compile(finalElse, context);
                 }
             }
         };
 
         // compile the literal switch; embed the fast version, but also the slow
         // in case we need to fall back on it
         // TODO: It would be nice to detect only types that fixnum is === to here
         // and just fail fast for the others...maybe examine Ruby 1.9's optz?
         context.typeCheckBranch(RubyFixnum.class,
                 new BranchCallback() {
                     public void branch(BodyCompiler context) {
                         context.literalSwitch(intCases, bodies, callback, elseCallback);
                     }
                 },
                 new BranchCallback() {
                     public void branch(BodyCompiler context) {
                         compileWhen(node, context, true);
                     }
                 }
         );
     }
 
     public void compileWhen(Node node, BodyCompiler context, final boolean hasCase) {
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
                 context.getInvocationCompiler().invokeEqq();
             }
 
             // check if the condition result is true, branch appropriately
             BranchCallback trueBranch = new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
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
 
                         public void branch(BodyCompiler context) {
                             // proceed to the next when
                             compileWhen(currentWhen.getNextCase(), context, hasCase);
                         }
                     };
 
             context.performBooleanBranch(trueBranch, falseBranch);
         }
     }
 
     public void compileMultiArgWhen(final WhenNode whenNode, final ArrayNode expressionsNode, final int conditionIndex, BodyCompiler context, final boolean hasCase) {
 
         if (conditionIndex >= expressionsNode.size()) {
             // done with conditions, continue to next when in the chain
             compileWhen(whenNode.getNextCase(), context, hasCase);
             return;
         }
 
         Node tag = expressionsNode.get(conditionIndex);
 
         context.setLinePosition(tag.getPosition());
 
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
                 context.getInvocationCompiler().invokeEqq();
             }
         }
 
         // check if the condition result is true, branch appropriately
         BranchCallback trueBranch = new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
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
 
                     public void branch(BodyCompiler context) {
                         // proceed to the next when
                         compileMultiArgWhen(whenNode, expressionsNode, conditionIndex + 1, context, hasCase);
                     }
                 };
 
         context.performBooleanBranch(trueBranch, falseBranch);
     }
 
     public void compileClass(Node node, BodyCompiler context) {
         final ClassNode classNode = (ClassNode) node;
 
         final Node superNode = classNode.getSuperNode();
 
         final Node cpathNode = classNode.getCPath();
 
         CompilerCallback superCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(superNode, context);
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
                             compile(classNode.getBodyNode(), context);
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
 
     public void compileSClass(Node node, BodyCompiler context) {
         final SClassNode sclassNode = (SClassNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(sclassNode.getReceiverNode(), context);
                     }
                 };
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
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
 
     public void compileClassVar(Node node, BodyCompiler context) {
         ClassVarNode classVarNode = (ClassVarNode) node;
 
         context.retrieveClassVariable(classVarNode.getName());
     }
 
     public void compileClassVarAsgn(Node node, BodyCompiler context) {
         final ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(classVarAsgnNode.getValueNode(), context);
             }
         };
 
         context.assignClassVariable(classVarAsgnNode.getName(), value);
     }
 
     public void compileClassVarAsgnAssignment(Node node, BodyCompiler context) {
         ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
 
         context.assignClassVariable(classVarAsgnNode.getName());
     }
 
     public void compileClassVarDecl(Node node, BodyCompiler context) {
         final ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(classVarDeclNode.getValueNode(), context);
             }
         };
         
         context.declareClassVariable(classVarDeclNode.getName(), value);
     }
 
     public void compileClassVarDeclAssignment(Node node, BodyCompiler context) {
         ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
 
         context.declareClassVariable(classVarDeclNode.getName());
     }
 
     public void compileConstDecl(Node node, BodyCompiler context) {
         // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
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
 
     public void compileConstDeclAssignment(Node node, BodyCompiler context) {
         // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
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
 
     public void compileConst(Node node, BodyCompiler context) {
         ConstNode constNode = (ConstNode) node;
 
         context.retrieveConstant(constNode.getName());
     }
 
     public void compileColon2(Node node, BodyCompiler context) {
         final Colon2Node iVisited = (Colon2Node) node;
         Node leftNode = iVisited.getLeftNode();
         final String name = iVisited.getName();
 
         if (leftNode == null) {
             context.loadObject();
             context.retrieveConstantFromModule(name);
         } else {
             final CompilerCallback receiverCallback = new CompilerCallback() {
 
                         public void call(BodyCompiler context) {
                             compile(iVisited.getLeftNode(), context);
                         }
                     };
 
             BranchCallback moduleCallback = new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             receiverCallback.call(context);
                             context.retrieveConstantFromModule(name);
                         }
                     };
 
             BranchCallback notModuleCallback = new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.getInvocationCompiler().invokeDynamic(name, receiverCallback, null, CallType.FUNCTIONAL, null, false);
                         }
                     };
 
-            context.branchIfModule(receiverCallback, moduleCallback, notModuleCallback);
+            context.branchIfModule(receiverCallback, moduleCallback, notModuleCallback, IdUtil.isConstant(name));
         }
     }
 
     public void compileColon3(Node node, BodyCompiler context) {
         Colon3Node iVisited = (Colon3Node) node;
         String name = iVisited.getName();
 
         context.loadObject();
         context.retrieveConstantFromModule(name);
     }
 
     public void compileGetDefinitionBase(final Node node, BodyCompiler context) {
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
 
     public void compileDefined(final Node node, BodyCompiler context) {
         compileGetDefinitionBase(((DefinedNode) node).getExpressionNode(), context);
         context.stringOrNil();
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
                                         compile(leftNode, context);
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
                                 compile(node, context);
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
 
     public void compileDAsgn(Node node, BodyCompiler context) {
         final DAsgnNode dasgnNode = (DAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(dasgnNode.getValueNode(), context);
             }
         };
         
         context.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth(), value);
     }
 
     public void compileDAsgnAssignment(Node node, BodyCompiler context) {
         DAsgnNode dasgnNode = (DAsgnNode) node;
 
         context.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth());
     }
 
     public void compileDefn(Node node, BodyCompiler context) {
         final DefnNode defnNode = (DefnNode) node;
         final ArgsNode argsNode = defnNode.getArgsNode();
 
         CompilerCallback body = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (defnNode.getBodyNode() != null) {
                             compile(defnNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         CompilerCallback args = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compileArgs(argsNode, context);
                     }
                 };
 
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         // check args first, since body inspection can depend on args
         inspector.inspect(defnNode.getArgsNode());
         inspector.inspect(defnNode.getBodyNode());
 
         context.defineNewMethod(defnNode.getName(), defnNode.getArgsNode().getArity().getValue(), defnNode.getScope(), body, args, null, inspector, isAtRoot);
     }
 
     public void compileDefs(Node node, BodyCompiler context) {
         final DefsNode defsNode = (DefsNode) node;
         final ArgsNode argsNode = defsNode.getArgsNode();
 
         CompilerCallback receiver = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(defsNode.getReceiverNode(), context);
                     }
                 };
 
         CompilerCallback body = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (defsNode.getBodyNode() != null) {
                             compile(defsNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         CompilerCallback args = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compileArgs(argsNode, context);
                     }
                 };
 
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(defsNode.getArgsNode());
         inspector.inspect(defsNode.getBodyNode());
 
         context.defineNewMethod(defsNode.getName(), defsNode.getArgsNode().getArity().getValue(), defsNode.getScope(), body, args, receiver, inspector, false);
     }
 
     public void compileArgs(Node node, BodyCompiler context) {
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
                             context.getVariableCompiler().assignLocalVariable(index);
                         }
                     };
         }
 
         if (opt > 0) {
             optionalGiven = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object object, int index) {
                             Node optArg = ((ListNode) object).get(index);
 
                             compileAssignment(optArg, context);
                         }
                     };
             optionalNotGiven = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object object, int index) {
                             Node optArg = ((ListNode) object).get(index);
 
                             compile(optArg, context);
                         }
                     };
         }
 
         if (rest > -1) {
             restAssignment = new CompilerCallback() {
 
                         public void call(BodyCompiler context) {
                             context.getVariableCompiler().assignLocalVariable(argsNode.getRestArg());
                         }
                     };
         }
 
         if (argsNode.getBlockArgNode() != null) {
             blockAssignment = new CompilerCallback() {
 
                         public void call(BodyCompiler context) {
                             context.getVariableCompiler().assignLocalVariable(argsNode.getBlockArgNode().getCount());
                         }
                     };
         }
 
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
 
     public void compileDot(Node node, BodyCompiler context) {
         DotNode dotNode = (DotNode) node;
 
         compile(dotNode.getBeginNode(), context);
         compile(dotNode.getEndNode(), context);
 
         context.createNewRange(dotNode.isExclusive());
     }
 
     public void compileDRegexp(Node node, BodyCompiler context) {
         final DRegexpNode dregexpNode = (DRegexpNode) node;
 
         CompilerCallback createStringCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         ArrayCallback dstrCallback = new ArrayCallback() {
 
                                     public void nextValue(BodyCompiler context, Object sourceArray,
                                             int index) {
                                         compile(dregexpNode.get(index), context);
                                     }
                                 };
                         context.createNewString(dstrCallback, dregexpNode.size());
                     }
                 };
 
         context.createNewRegexp(createStringCallback, dregexpNode.getOptions());
     }
 
     public void compileDStr(Node node, BodyCompiler context) {
         final DStrNode dstrNode = (DStrNode) node;
 
         ArrayCallback dstrCallback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray,
                             int index) {
                         compile(dstrNode.get(index), context);
                     }
                 };
         context.createNewString(dstrCallback, dstrNode.size());
     }
 
     public void compileDSymbol(Node node, BodyCompiler context) {
         final DSymbolNode dsymbolNode = (DSymbolNode) node;
 
         ArrayCallback dstrCallback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray,
                             int index) {
                         compile(dsymbolNode.get(index), context);
                     }
                 };
         context.createNewSymbol(dstrCallback, dsymbolNode.size());
     }
 
     public void compileDVar(Node node, BodyCompiler context) {
         DVarNode dvarNode = (DVarNode) node;
 
         context.getVariableCompiler().retrieveLocalVariable(dvarNode.getIndex(), dvarNode.getDepth());
     }
 
     public void compileDXStr(Node node, BodyCompiler context) {
         final DXStrNode dxstrNode = (DXStrNode) node;
 
         final ArrayCallback dstrCallback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray,
                             int index) {
                         compile(dxstrNode.get(index), context);
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
     }
 
     public void compileEnsureNode(Node node, BodyCompiler context) {
         final EnsureNode ensureNode = (EnsureNode) node;
 
         if (ensureNode.getEnsureNode() != null) {
             context.protect(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             if (ensureNode.getBodyNode() != null) {
                                 compile(ensureNode.getBodyNode(), context);
                             } else {
                                 context.loadNil();
                             }
                         }
                     },
                     new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
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
 
     public void compileEvStr(Node node, BodyCompiler context) {
         final EvStrNode evStrNode = (EvStrNode) node;
 
         compile(evStrNode.getBody(), context);
         context.asString();
     }
 
     public void compileFalse(Node node, BodyCompiler context) {
         context.loadFalse();
 
         context.pollThreadEvents();
     }
 
     public void compileFCall(Node node, BodyCompiler context) {
         final FCallNode fcallNode = (FCallNode) node;
 
         ArgumentsCallback argsCallback = getArgsCallback(fcallNode.getArgsNode());
         
         CompilerCallback closureArg = getBlock(fcallNode.getIterNode());
 
         context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, argsCallback, CallType.FUNCTIONAL, closureArg, fcallNode.getIterNode() instanceof IterNode);
     }
 
     private CompilerCallback getBlock(Node node) {
         if (node == null) {
             return null;
         }
 
         switch (node.nodeId) {
             case ITERNODE:
                 final IterNode iterNode = (IterNode) node;
 
                 return new CompilerCallback() {
 
                             public void call(BodyCompiler context) {
                                 compile(iterNode, context);
                             }
                         };
             case BLOCKPASSNODE:
                 final BlockPassNode blockPassNode = (BlockPassNode) node;
 
                 return new CompilerCallback() {
 
                             public void call(BodyCompiler context) {
                                 compile(blockPassNode.getBodyNode(), context);
                                 context.unwrapPassedBlock();
                             }
                         };
             default:
                 throw new NotCompilableException("ERROR: Encountered a method with a non-block, non-blockpass iter node at: " + node);
         }
     }
 
     public void compileFixnum(Node node, BodyCompiler context) {
         FixnumNode fixnumNode = (FixnumNode) node;
 
         context.createNewFixnum(fixnumNode.getValue());
     }
 
     public void compileFlip(Node node, BodyCompiler context) {
         final FlipNode flipNode = (FlipNode) node;
 
         context.getVariableCompiler().retrieveLocalVariable(flipNode.getIndex(), flipNode.getDepth());
 
         if (flipNode.isExclusive()) {
             context.performBooleanBranch(new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getEndNode(), context);
                     context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.loadFalse();
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                             context.consumeCurrentValue();
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                         }
                     });
                     context.loadTrue();
                 }
             }, new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getBeginNode(), context);
                     becomeTrueOrFalse(context);
                     context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                 }
             });
         } else {
             context.performBooleanBranch(new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getEndNode(), context);
                     context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.loadFalse();
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                             context.consumeCurrentValue();
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                         }
                     });
                     context.loadTrue();
                 }
             }, new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getBeginNode(), context);
                     context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(flipNode.getEndNode(), context);
                             flipTrueOrFalse(context);
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                             context.consumeCurrentValue();
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
 
     public void compileFloat(Node node, BodyCompiler context) {
         FloatNode floatNode = (FloatNode) node;
 
         context.createNewFloat(floatNode.getValue());
     }
 
     public void compileFor(Node node, BodyCompiler context) {
         final ForNode forNode = (ForNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(forNode.getIterNode(), context);
                     }
                 };
 
         final CompilerCallback closureArg = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compileForIter(forNode, context);
                     }
                 };
 
         context.getInvocationCompiler().invokeDynamic("each", receiverCallback, null, CallType.NORMAL, closureArg, true);
     }
 
     public void compileForIter(Node node, BodyCompiler context) {
         final ForNode forNode = (ForNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (forNode.getBodyNode() != null) {
                             compile(forNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         // create the closure class and instantiate it
         final CompilerCallback closureArgs = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (forNode.getVarNode() != null) {
                             compileAssignment(forNode.getVarNode(), context);
                         }
                     }
                 };
 
         boolean hasMultipleArgsHead = false;
diff --git a/src/org/jruby/compiler/BodyCompiler.java b/src/org/jruby/compiler/BodyCompiler.java
index ee3fdccfa8..dd2e0c57f9 100644
--- a/src/org/jruby/compiler/BodyCompiler.java
+++ b/src/org/jruby/compiler/BodyCompiler.java
@@ -1,546 +1,546 @@
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
     public void createNewRange(boolean isExclusive);
     
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
      * Create a new closure (block) for a for loop with the given call arity and
      * body generated by the body callback.
      * 
      * @param scope The static scoping information
      * @param arity The arity of the block's argument list
      * @param body The callback which will generate the closure's body
      */
     public void createNewForLoop(int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId);
     
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
      * Load the given string as a symbol on to the top of the stack.
      * 
      * @param symbol The symbol to load.
      */
     public void loadSymbol(String symbol);
     
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
     public void forEachInValueArray(int count, int start, Object source, ArrayCallback callback, ArrayCallback nilCallback, CompilerCallback argsCallback);
     
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
 
     public void match2();
 
     public void match3();
 
     public void createNewRegexp(ByteList value, int options);
     public void createNewRegexp(CompilerCallback createStringCallback, int options);
     
     public void pollThreadEvents();
 
-    public void branchIfModule(CompilerCallback receiverCallback, BranchCallback moduleCallback, BranchCallback notModuleCallback);
+    public void branchIfModule(CompilerCallback receiverCallback, BranchCallback moduleCallback, BranchCallback notModuleCallback, boolean mustBeModule);
 
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
     public void performRescue(BranchCallback regularCode, BranchCallback rubyCatchCode, BranchCallback javaCatchCode);
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
     public void defineClass(String name, StaticScope staticScope, CompilerCallback superCallback, CompilerCallback pathCallback, CompilerCallback bodyCallback, CompilerCallback receiverCallback);
     public void defineModule(String name, StaticScope staticScope, CompilerCallback pathCallback, CompilerCallback bodyCallback);
     public void unwrapPassedBlock();
     public void performBackref(char type);
     public void callZSuper(CompilerCallback closure);
     public void appendToObjectArray();
     public void checkIsExceptionHandled();
     public void checkIsJavaExceptionHandled();
     public void rethrowException();
     public void loadClass(String name);
     public void unwrapRaiseException();
     public void loadException();
     public void setFilePosition(ISourcePosition position);
     public void setLinePosition(ISourcePosition position);
     public void checkWhenWithSplat();
     public void createNewEndBlock(CompilerCallback body);
     public void runBeginBlock(StaticScope scope, CompilerCallback body);
     public void rethrowIfSystemExit();
 
     public BodyCompiler chainToMethod(String name, ASTInspector inspector);
     public void wrapJavaException();
     public void literalSwitch(int[] caseInts, Object caseBodies, ArrayCallback casesCallback, CompilerCallback defaultCallback);
     public void typeCheckBranch(Class type, BranchCallback trueCallback, BranchCallback falseCallback);
 }
diff --git a/src/org/jruby/compiler/impl/BaseBodyCompiler.java b/src/org/jruby/compiler/impl/BaseBodyCompiler.java
index b846e501dd..574fe2708d 100644
--- a/src/org/jruby/compiler/impl/BaseBodyCompiler.java
+++ b/src/org/jruby/compiler/impl/BaseBodyCompiler.java
@@ -249,2002 +249,2003 @@ public abstract class BaseBodyCompiler implements BodyCompiler {
 
     public void invokeIRuby(String methodName, String signature) {
         method.invokevirtual(StandardASMCompiler.RUBY, methodName, signature);
     }
 
     public void invokeIRubyObject(String methodName, String signature) {
         method.invokeinterface(StandardASMCompiler.IRUBYOBJECT, methodName, signature);
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
 
     public void assignClassVariable(String name, CompilerCallback value) {
         loadThreadContext();
         loadRuntime();
         loadSelf();
         method.ldc(name);
         value.call(this);
 
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
 
     public void declareClassVariable(String name, CompilerCallback value) {
         loadThreadContext();
         loadRuntime();
         loadSelf();
         method.ldc(name);
         value.call(this);
 
         invokeUtilityMethod("fastDeclareClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
     }
 
     public void createNewFloat(double value) {
         loadRuntime();
         method.ldc(new Double(value));
 
         invokeIRuby("newFloat", sig(RubyFloat.class, params(Double.TYPE)));
     }
 
     public void createNewFixnum(long value) {
         script.getCacheCompiler().cacheFixnum(this, value);
     }
 
     public void createNewBignum(BigInteger value) {
         loadRuntime();
         script.getCacheCompiler().cacheBigInteger(this, value);
         method.invokestatic(p(RubyBignum.class), "newBignum", sig(RubyBignum.class, params(Ruby.class, BigInteger.class)));
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
         createNewString(callback, count);
         toJavaString();
         invokeIRuby("newSymbol", sig(RubySymbol.class, params(String.class)));
     }
 
     public void createNewString(ByteList value) {
         // FIXME: this is sub-optimal, storing string value in a java.lang.String again
         loadRuntime();
         script.getCacheCompiler().cacheByteList(this, value.toString());
 
         invokeIRuby("newStringShared", sig(RubyString.class, params(ByteList.class)));
     }
 
     public void createNewSymbol(String name) {
         script.getCacheCompiler().cacheSymbol(this, name);
     }
 
     public void createNewArray(boolean lightweight) {
         loadRuntime();
         // put under object array already present
         method.swap();
 
         if (lightweight) {
             method.invokestatic(p(RubyArray.class), "newArrayNoCopyLight", sig(RubyArray.class, params(Ruby.class, IRubyObject[].class)));
         } else {
             method.invokestatic(p(RubyArray.class), "newArrayNoCopy", sig(RubyArray.class, params(Ruby.class, IRubyObject[].class)));
         }
     }
 
     public void createNewArray(Object[] sourceArray, ArrayCallback callback, boolean lightweight) {
         loadRuntime();
 
         createObjectArray(sourceArray, callback);
 
         if (lightweight) {
             method.invokestatic(p(RubyArray.class), "newArrayNoCopyLight", sig(RubyArray.class, params(Ruby.class, IRubyObject[].class)));
         } else {
             method.invokestatic(p(RubyArray.class), "newArrayNoCopy", sig(RubyArray.class, params(Ruby.class, IRubyObject[].class)));
         }
     }
 
     public void createEmptyArray() {
         loadRuntime();
 
         invokeIRuby("newArray", sig(RubyArray.class, params()));
     }
 
     public void createObjectArray(Object[] sourceArray, ArrayCallback callback) {
         buildObjectArray(StandardASMCompiler.IRUBYOBJECT, sourceArray, callback);
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
             method.pushInt(sourceArray.length);
             method.anewarray(type);
 
             for (int i = 0; i < sourceArray.length; i++) {
                 method.dup();
                 method.pushInt(i);
 
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
         loadThreadContext();
 
         // could be more efficient with a callback
         method.dup2_x2();
         method.pop2();
 
         if (isExclusive) {
             method.invokestatic(p(RubyRange.class), "newExclusiveRange", sig(RubyRange.class, params(Ruby.class, ThreadContext.class, IRubyObject.class, IRubyObject.class)));
         } else {
             method.invokestatic(p(RubyRange.class), "newInclusiveRange", sig(RubyRange.class, params(Ruby.class, ThreadContext.class, IRubyObject.class, IRubyObject.class)));
         }
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
 
     public void performBooleanLoopSafe(BranchCallback condition, BranchCallback body, boolean checkFirst) {
         String mname = getNewRescueName();
         String signature = sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, Block.class});
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(script.getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, signature, null, null));
         SkinnyMethodAdapter old_method = null;
         SkinnyMethodAdapter var_old_method = null;
         SkinnyMethodAdapter inv_old_method = null;
         boolean oldWithinProtection = withinProtection;
         withinProtection = true;
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
 
             // store previous exception for restoration if we rescue something
             loadRuntime();
             invokeUtilityMethod("getErrorInfo", sig(IRubyObject.class, Ruby.class));
             mv.astore(getPreviousExceptionIndex());
 
             // grab nil for local variables
             mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             mv.astore(getNilIndex());
 
             mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
             mv.dup();
             mv.astore(getDynamicScopeIndex());
             mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
             mv.astore(getVarsArrayIndex());
 
             performBooleanLoop(condition, body, checkFirst);
 
             mv.areturn();
             mv.visitMaxs(1, 1);
             mv.visitEnd();
         } finally {
             withinProtection = oldWithinProtection;
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
         method.invokevirtual(script.getClassname(), mname, signature);
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
 
             currentLoopLabels = new Label[]{endOfBody, topOfBody, done};
 
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
                 loadThreadContext();
                 invokeUtilityMethod("breakJumpInWhile", sig(IRubyObject.class, JumpException.BreakJump.class, Block.class, ThreadContext.class));
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
 
     public void performBooleanLoopLight(BranchCallback condition, BranchCallback body, boolean checkFirst) {
         Label endOfBody = new Label();
         Label conditionCheck = new Label();
         Label topOfBody = new Label();
         Label done = new Label();
 
         Label[] oldLoopLabels = currentLoopLabels;
 
         currentLoopLabels = new Label[]{endOfBody, topOfBody, done};
 
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
 
         loadNil();
         method.label(done);
     }
 
     public void createNewClosure(
             int line,
             StaticScope scope,
             int arity,
             CompilerCallback body,
             CompilerCallback args,
             boolean hasMultipleArgsHead,
             NodeType argsNodeId,
             ASTInspector inspector) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$" + "__block__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, inspector, scope);
 
         closureCompiler.beginMethod(args, scope);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
 
         loadThreadContext();
         loadSelf();
         script.getCacheCompiler().cacheClosure(this, closureMethodName, arity, scope, hasMultipleArgsHead, argsNodeId, inspector);
 
         invokeUtilityMethod("createBlock", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, BlockBody.class)));
     }
 
     public void runBeginBlock(StaticScope scope, CompilerCallback body) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$__begin__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, null, scope);
 
         closureCompiler.beginMethod(null, scope);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
         loadThreadContext();
         loadSelf();
 
         StandardASMCompiler.buildStaticScopeNames(method, scope);
 
         script.getCacheCompiler().cacheClosureOld(this, closureMethodName);
 
         invokeUtilityMethod("runBeginBlock", sig(IRubyObject.class,
                 params(ThreadContext.class, IRubyObject.class, String[].class, CompiledBlockCallback.class)));
     }
 
     public void createNewForLoop(int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$__for__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, null, scope);
 
         closureCompiler.beginMethod(args, null);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
         loadThreadContext();
         loadSelf();
         method.pushInt(arity);
 
         script.getCacheCompiler().cacheClosureOld(this, closureMethodName);
 
         method.ldc(Boolean.valueOf(hasMultipleArgsHead));
         method.ldc(BlockBody.asArgumentType(argsNodeId));
 
         invokeUtilityMethod("createSharedScopeBlock", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
     }
 
     public void createNewEndBlock(CompilerCallback body) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$__end__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, null, scope);
 
         closureCompiler.beginMethod(null, null);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
         loadThreadContext();
         loadSelf();
         method.iconst_0();
 
         script.getCacheCompiler().cacheClosureOld(this, closureMethodName);
 
         method.iconst_0(); // false
         method.iconst_0(); // zero
 
         invokeUtilityMethod("createSharedScopeBlock", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
 
         loadRuntime();
         invokeUtilityMethod("registerEndBlock", sig(void.class, Block.class, Ruby.class));
         loadNil();
     }
 
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
 
     public void assignInstanceVariable(String name, CompilerCallback value) {
         // FIXME: more efficient with a callback
         loadSelf();
         invokeIRubyObject("getInstanceVariables", sig(InstanceVariables.class));
 
         method.ldc(name);
         value.call(this);
 
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
 
     public void assignGlobalVariable(String name, CompilerCallback value) {
         // FIXME: more efficient with a callback
         loadRuntime();
 
         invokeIRuby("getGlobalVariables", sig(GlobalVariables.class));
         method.ldc(name);
         value.call(this);
         method.invokevirtual(p(GlobalVariables.class), "set", sig(IRubyObject.class, params(String.class, IRubyObject.class)));
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
 
     public void forEachInValueArray(int start, int count, Object source, ArrayCallback callback, ArrayCallback nilCallback, CompilerCallback argsCallback) {
         // FIXME: This could probably be made more efficient
         for (; start < count; start++) {
             method.dup(); // dup the original array object
             loadNil();
             method.pushInt(start);
             invokeUtilityMethod("arrayEntryOrNil", sig(IRubyObject.class, RubyArray.class, IRubyObject.class, int.class));
             callback.nextValue(this, source, start);
             method.pop();
         }
 
         if (argsCallback != null) {
             method.dup(); // dup the original array object
             loadRuntime();
             method.pushInt(start);
             invokeUtilityMethod("subarrayOrEmpty", sig(RubyArray.class, RubyArray.class, Ruby.class, int.class));
             argsCallback.call(this);
             method.pop();
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
 
     public void match2() {
         loadThreadContext();
         method.swap();
         method.invokevirtual(p(RubyRegexp.class), "op_match", sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class)));
     }
 
     public void match3() {
         loadThreadContext();
         invokeUtilityMethod("match3", sig(IRubyObject.class, RubyRegexp.class, IRubyObject.class, ThreadContext.class));
     }
 
     public void createNewRegexp(final ByteList value, final int options) {
         String regexpField = script.getNewConstant(ci(RubyRegexp.class), "lit_reg_");
 
         // in current method, load the field to see if we've created a Pattern yet
         method.aload(StandardASMCompiler.THIS);
         method.getfield(script.getClassname(), regexpField, ci(RubyRegexp.class));
 
         Label alreadyCreated = new Label();
         method.ifnonnull(alreadyCreated); //[]
 
         // load string, for Regexp#source and Regexp#inspect
         String regexpString = value.toString();
 
         loadRuntime(); //[R]
         method.ldc(regexpString); //[R, rS]
         method.pushInt(options); //[R, rS, opts]
 
         method.invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, params(Ruby.class, String.class, Integer.TYPE))); //[reg]
 
         method.aload(StandardASMCompiler.THIS); //[reg, T]
         method.swap(); //[T, reg]
         method.putfield(script.getClassname(), regexpField, ci(RubyRegexp.class)); //[]
         method.label(alreadyCreated);
         method.aload(StandardASMCompiler.THIS); //[T]
         method.getfield(script.getClassname(), regexpField, ci(RubyRegexp.class));
     }
 
     public void createNewRegexp(CompilerCallback createStringCallback, final int options) {
         boolean onceOnly = (options & ReOptions.RE_OPTION_ONCE) != 0;   // for regular expressions with the /o flag
         Label alreadyCreated = null;
         String regexpField = null;
 
         // only alter the code if the /o flag was present
         if (onceOnly) {
             regexpField = script.getNewConstant(ci(RubyRegexp.class), "lit_reg_");
 
             // in current method, load the field to see if we've created a Pattern yet
             method.aload(StandardASMCompiler.THIS);
             method.getfield(script.getClassname(), regexpField, ci(RubyRegexp.class));
 
             alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
         }
 
         loadRuntime();
 
         createStringCallback.call(this);
         method.invokevirtual(p(RubyString.class), "getByteList", sig(ByteList.class));
         method.pushInt(options);
 
         method.invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, params(Ruby.class, ByteList.class, Integer.TYPE))); //[reg]
 
         // only alter the code if the /o flag was present
         if (onceOnly) {
             method.aload(StandardASMCompiler.THIS);
             method.swap();
             method.putfield(script.getClassname(), regexpField, ci(RubyRegexp.class));
             method.label(alreadyCreated);
             method.aload(StandardASMCompiler.THIS);
             method.getfield(script.getClassname(), regexpField, ci(RubyRegexp.class));
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
                 method.dup();
                 method.invokevirtual(p(RubyMatchData.class), "use", sig(void.class));
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
 
-    public void branchIfModule(CompilerCallback receiverCallback, BranchCallback moduleCallback, BranchCallback notModuleCallback) {
+    public void branchIfModule(CompilerCallback receiverCallback, BranchCallback moduleCallback, BranchCallback notModuleCallback, boolean mustBeModule) {
         receiverCallback.call(this);
+        invokeUtilityMethod("checkIsModule", sig(RubyModule.class, IRubyObject.class));
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
         return "ensure_" + (script.getAndIncrementEnsureNumber()) + "$RUBY$__ensure__";
     }
 
     public void protect(BranchCallback regularCode, BranchCallback protectedCode, Class ret) {
 
         String mname = getNewEnsureName();
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(script.getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, Block.class}), null, null));
         SkinnyMethodAdapter old_method = null;
         SkinnyMethodAdapter var_old_method = null;
         SkinnyMethodAdapter inv_old_method = null;
         boolean oldWithinProtection = withinProtection;
         withinProtection = true;
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
             withinProtection = oldWithinProtection;
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
 
     protected String getNewRescueName() {
         return "rescue_" + (script.getAndIncrementRescueNumber()) + "$RUBY$__rescue__";
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
         boolean oldWithinProtection = withinProtection;
         withinProtection = true;
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
 
             // store previous exception for restoration if we rescue something
             loadRuntime();
             invokeUtilityMethod("getErrorInfo", sig(IRubyObject.class, Ruby.class));
             mv.astore(getPreviousExceptionIndex());
 
             // grab nil for local variables
             mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             mv.astore(getNilIndex());
 
             mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
             mv.dup();
             mv.astore(getDynamicScopeIndex());
             mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
             mv.astore(getVarsArrayIndex());
 
             Label beforeBody = new Label();
             Label afterBody = new Label();
             Label catchBlock = new Label();
             mv.visitTryCatchBlock(beforeBody, afterBody, catchBlock, p(exception));
             mv.visitLabel(beforeBody);
 
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
             loadRuntime();
             mv.aload(getPreviousExceptionIndex());
             invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
             mv.athrow();
 
             mv.label(exitRescue);
 
             // restore the original exception
             loadRuntime();
             mv.aload(getPreviousExceptionIndex());
             invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
 
             mv.areturn();
             mv.visitMaxs(1, 1);
             mv.visitEnd();
         } finally {
             withinProtection = oldWithinProtection;
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
 
     public void performRescue(BranchCallback regularCode, BranchCallback rubyCatchCode, BranchCallback javaCatchCode) {
         String mname = getNewRescueName();
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(script.getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, Block.class}), null, null));
         SkinnyMethodAdapter old_method = null;
         SkinnyMethodAdapter var_old_method = null;
         SkinnyMethodAdapter inv_old_method = null;
         Label afterRubyCatchBody = new Label();
         Label afterJavaCatchBody = new Label();
         Label rubyCatchRetry = new Label();
         Label rubyCatchRaised = new Label();
         Label rubyCatchJumps = new Label();
         Label javaCatchRetry = new Label();
         Label javaCatchRaised = new Label();
         Label javaCatchJumps = new Label();
         Label exitRescue = new Label();
         boolean oldWithinProtection = withinProtection;
         withinProtection = true;
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
 
             // store previous exception for restoration if we rescue something
             loadRuntime();
             invokeUtilityMethod("getErrorInfo", sig(IRubyObject.class, Ruby.class));
             mv.astore(getPreviousExceptionIndex());
 
             // grab nil for local variables
             mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             mv.astore(getNilIndex());
 
             mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
             mv.dup();
             mv.astore(getDynamicScopeIndex());
             mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
             mv.astore(getVarsArrayIndex());
 
             Label beforeBody = new Label();
             Label afterBody = new Label();
             Label rubyCatchBlock = new Label();
             Label flowCatchBlock = new Label();
             Label javaCatchBlock = new Label();
             mv.visitTryCatchBlock(beforeBody, afterBody, rubyCatchBlock, p(RaiseException.class));
             mv.visitTryCatchBlock(beforeBody, afterBody, flowCatchBlock, p(JumpException.FlowControlException.class));
             mv.visitTryCatchBlock(beforeBody, afterBody, javaCatchBlock, p(Exception.class));
 
             mv.visitLabel(beforeBody);
             {
                 regularCode.branch(this);
             }
             mv.label(afterBody);
             mv.go_to(exitRescue);
 
             // first handle Ruby exceptions (RaiseException)
             mv.label(rubyCatchBlock);
             {
                 mv.astore(getExceptionIndex());
 
                 rubyCatchCode.branch(this);
                 mv.label(afterRubyCatchBody);
                 mv.go_to(exitRescue);
 
                 // retry handling in the rescue block
                 mv.trycatch(rubyCatchBlock, afterRubyCatchBody, rubyCatchRetry, p(JumpException.RetryJump.class));
                 mv.label(rubyCatchRetry);
                 {
                     mv.pop();
                 }
                 mv.go_to(beforeBody);
 
                 // any exceptions raised must continue to be raised, skipping $! restoration
                 mv.trycatch(beforeBody, afterRubyCatchBody, rubyCatchRaised, p(RaiseException.class));
                 mv.label(rubyCatchRaised);
                 {
                     mv.athrow();
                 }
 
                 // and remaining jump exceptions should restore $!
                 mv.trycatch(beforeBody, afterRubyCatchBody, rubyCatchJumps, p(JumpException.class));
                 mv.label(rubyCatchJumps);
                 {
                     loadRuntime();
                     mv.aload(getPreviousExceptionIndex());
                     invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
                     mv.athrow();
                 }
             }
 
             // Next handle Flow exceptions, just propagating them
             mv.label(flowCatchBlock);
             {
                 // restore the original exception
                 loadRuntime();
                 mv.aload(getPreviousExceptionIndex());
                 invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
 
                 // rethrow
                 mv.athrow();
             }
 
             // now handle Java exceptions
             mv.label(javaCatchBlock);
             {
                 mv.astore(getExceptionIndex());
 
                 javaCatchCode.branch(this);
                 mv.label(afterJavaCatchBody);
                 mv.go_to(exitRescue);
 
                 // retry handling in the rescue block
                 mv.trycatch(javaCatchBlock, afterJavaCatchBody, javaCatchRetry, p(JumpException.RetryJump.class));
                 mv.label(javaCatchRetry);
                 {
                     mv.pop();
                 }
                 mv.go_to(beforeBody);
 
                 // any exceptions raised must continue to be raised, skipping $! restoration
                 mv.trycatch(javaCatchBlock, afterJavaCatchBody, javaCatchRaised, p(RaiseException.class));
                 mv.label(javaCatchRaised);
                 {
                     mv.athrow();
                 }
 
                 // and remaining jump exceptions should restore $!
                 mv.trycatch(javaCatchBlock, afterJavaCatchBody, javaCatchJumps, p(JumpException.class));
                 mv.label(javaCatchJumps);
                 {
                     loadRuntime();
                     mv.aload(getPreviousExceptionIndex());
                     invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
                     mv.athrow();
                 }
             }
 
             mv.label(exitRescue);
 
             // restore the original exception
             loadRuntime();
             mv.aload(getPreviousExceptionIndex());
             invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
 
             mv.areturn();
             mv.visitMaxs(1, 1);
             mv.visitEnd();
         } finally {
             withinProtection = oldWithinProtection;
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
         method.invokevirtual(script.getClassname(), mname, sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, Block.class}));
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
             final CompilerCallback receiverCallback) {
         String classMethodName = null;
         if (receiverCallback == null) {
             String mangledName = JavaNameMangler.mangleStringForCleanJavaIdentifier(name);
             classMethodName = "class_" + script.getAndIncrementMethodIndex() + "$RUBY$" + mangledName;
         } else {
             classMethodName = "sclass_" + script.getAndIncrementMethodIndex() + "$RUBY$__singleton__";
         }
 
         final RootScopedBodyCompiler classBody = new ASMClassBodyCompiler(script, classMethodName, null, staticScope);
 
         CompilerCallback bodyPrep = new CompilerCallback() {
 
             public void call(BodyCompiler context) {
                 if (receiverCallback == null) {
                     if (superCallback != null) {
                         classBody.loadRuntime();
                         superCallback.call(classBody);
 
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
                 StandardASMCompiler.buildStaticScopeNames(classBody.method, staticScope);
                 classBody.invokeThreadContext("preCompiledClass", sig(Void.TYPE, params(RubyModule.class, String[].class)));
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
             // if there's no receiver, there could potentially be a superclass like class Foo << self
             // so we pass in self here
             method.aload(StandardASMCompiler.SELF_INDEX);
         } else {
             // otherwise, there's a receiver, so we pass that in directly for the sclass logic
             receiverCallback.call(this);
         }
         method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
 
         method.invokevirtual(script.getClassname(), classMethodName, StandardASMCompiler.METHOD_SIGNATURES[0]);
     }
 
     public void defineModule(final String name, final StaticScope staticScope, final CompilerCallback pathCallback, final CompilerCallback bodyCallback) {
         String mangledName = JavaNameMangler.mangleStringForCleanJavaIdentifier(name);
         String moduleMethodName = "module__" + script.getAndIncrementMethodIndex() + "$RUBY$" + mangledName;
 
         final RootScopedBodyCompiler classBody = new ASMClassBodyCompiler(script, moduleMethodName, null, staticScope);
 
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
                 StandardASMCompiler.buildStaticScopeNames(classBody.method, staticScope);
 
                 classBody.invokeThreadContext("preCompiledClass", sig(Void.TYPE, params(RubyModule.class, String[].class)));
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
 
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index 9557a08b2a..9554d2755b 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -220,1001 +220,1007 @@ public class RuntimeHelpers {
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
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         
         if (superClass == null) {
             String name = context.getFrameName(); 
             return callMethodMissing(context, self, klazz.searchMethod(name), name, args, CallType.SUPER, block);
         }
         return invokeAs(context, superClass, self, context.getFrameName(), args, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         
         if (superClass == null) {
             String name = context.getFrameName(); 
             return callMethodMissing(context, self, klazz.searchMethod(name), name, CallType.SUPER, block);
         }
         return invokeAs(context, superClass, self, context.getFrameName(), block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         
         if (superClass == null) {
             String name = context.getFrameName(); 
             return callMethodMissing(context, self, klazz.searchMethod(name), name, arg0, CallType.SUPER, block);
         }
         return invokeAs(context, superClass, self, context.getFrameName(), arg0, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         
         if (superClass == null) {
             String name = context.getFrameName(); 
             return callMethodMissing(context, self, klazz.searchMethod(name), name, arg0, arg1, CallType.SUPER, block);
         }
         return invokeAs(context, superClass, self, context.getFrameName(), arg0, arg1, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         
         if (superClass == null) {
             String name = context.getFrameName(); 
             return callMethodMissing(context, self, klazz.searchMethod(name), name, arg0, arg1, arg2, CallType.SUPER, block);
         }
         return invokeAs(context, superClass, self, context.getFrameName(), arg0, arg1, arg2, block);
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
         proc = TypeConverter.convertToType(proc, runtime.getProc(), 0, "to_proc", false);
 
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
         checkSuperDisabledOrOutOfMethod(context);
 
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
     
     public static IRubyObject breakJumpInWhile(JumpException.BreakJump bj, Block aBlock, ThreadContext context) {
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
         throw runtime.newLocalJumpError("break", value, "unexpected break");
     }
     
     public static IRubyObject[] concatObjectArrays(IRubyObject[] array, IRubyObject[] add) {
         IRubyObject[] newArray = new IRubyObject[array.length + add.length];
         System.arraycopy(array, 0, newArray, 0, array.length);
         System.arraycopy(add, 0, newArray, array.length, add.length);
         return newArray;
     }
     
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject[] exceptions, Ruby runtime, ThreadContext context, IRubyObject self) {
         for (int i = 0; i < exceptions.length; i++) {
             if (!runtime.getModule().isInstance(exceptions[i])) {
                 throw runtime.newTypeError("class or module required for rescue clause");
             }
             IRubyObject result = exceptions[i].callMethod(context, "===", currentException);
             if (result.isTrue()) return result;
         }
         return runtime.getFalse();
     }
     
     public static IRubyObject isJavaExceptionHandled(Exception currentException, IRubyObject[] exceptions, Ruby runtime, ThreadContext context, IRubyObject self) {
         for (int i = 0; i < exceptions.length; i++) {
             if (exceptions[i] instanceof RubyClass) {
                 RubyClass rubyClass = (RubyClass)exceptions[i];
                 JavaClass javaClass = (JavaClass)rubyClass.fastGetInstanceVariable("@java_class");
                 if (javaClass != null) {
                     Class cls = javaClass.javaClass();
                     if (cls.isInstance(currentException)) {
                         return runtime.getTrue();
                     }
                 }
             }
         }
         
         return runtime.getFalse();
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
         return RubyArray.newArrayNoCopy(runtime, input, start);
     }
     
     public static RubyBoolean isWhenTriggered(IRubyObject expression, IRubyObject expressionsObject, ThreadContext context) {
         RubyArray expressions = RuntimeHelpers.splatValue(expressionsObject);
         for (int j = 0,k = expressions.getLength(); j < k; j++) {
             IRubyObject condition = expressions.eltInternal(j);
 
             if ((expression != null && condition.callMethod(context, MethodIndex.OP_EQQ, "===", expression)
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
     
     public static IRubyObject retryJump() {
         throw JumpException.RETRY_JUMP;
     }
     
     public static IRubyObject redoJump() {
         throw JumpException.REDO_JUMP;
     }
     
     public static IRubyObject redoLocalJumpError(Ruby runtime) {
         throw runtime.newLocalJumpError("redo", runtime.getNil(), "unexpected redo");
     }
     
     public static IRubyObject nextJump(IRubyObject value) {
         throw new JumpException.NextJump(value);
     }
     
     public static IRubyObject nextLocalJumpError(Ruby runtime, IRubyObject value) {
         throw runtime.newLocalJumpError("next", value, "unexpected next");
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
     
     public static IRubyObject getInstanceVariable(Ruby runtime, IRubyObject self, String name) {
         IRubyObject result = self.getInstanceVariables().getInstanceVariable(name);
         
         if (result != null) return result;
         
         runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + name + " not initialized");
         
         return runtime.getNil();
     }
     
     public static IRubyObject fastGetInstanceVariable(Ruby runtime, IRubyObject self, String internedName) {
         IRubyObject result;
         if ((result = self.getInstanceVariables().fastGetInstanceVariable(internedName)) != null) return result;
         
         runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + internedName + " not initialized");
         
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
         return context.getCurrentFrame().setLastLine(value);
     }
 
     public static IRubyObject getLastLine(Ruby runtime, ThreadContext context) {
         return context.getCurrentFrame().getLastLine();
     }
 
     public static IRubyObject setBackref(Ruby runtime, ThreadContext context, IRubyObject value) {
         if (!value.isNil() && !(value instanceof RubyMatchData)) throw runtime.newTypeError(value, runtime.getMatchData());
         return context.getCurrentFrame().setBackRef(value);
     }
 
     public static IRubyObject getBackref(Ruby runtime, ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
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
             return TypeConverter.convertToType(value, value.getRuntime().getArray(), MethodIndex.TO_A, "to_ary", false);
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
         return factory.getCompiledMethodLazily(rubyClass, javaName, Arity.createArity(arity), Visibility.PUBLIC, scope, scriptObject, callConfig);
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
     
     public static IRubyObject arrayEntryOrNil(RubyArray array, IRubyObject nil, int index) {
         if (index < array.getLength()) {
             return array.entry(index);
         } else {
             return nil;
         }
     }
     
     public static RubyArray subarrayOrEmpty(RubyArray array, Ruby runtime, int index) {
         if (index < array.getLength()) {
             return createSubarray(array, index);
         } else {
             return RubyArray.newEmptyArray(runtime);
         }
     }
+    
+    public static RubyModule checkIsModule(IRubyObject maybeModule) {
+        if (maybeModule instanceof RubyModule) return (RubyModule)maybeModule;
+        
+        throw maybeModule.getRuntime().newTypeError(maybeModule + " is not a class/module");
+    }
 }
