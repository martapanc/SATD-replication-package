diff --git a/src/org/jruby/compiler/NodeCompilerFactory.java b/src/org/jruby/compiler/NodeCompilerFactory.java
index 05e36f1f10..cd16f4b484 100644
--- a/src/org/jruby/compiler/NodeCompilerFactory.java
+++ b/src/org/jruby/compiler/NodeCompilerFactory.java
@@ -1,1944 +1,1946 @@
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
 import org.jruby.ast.SClassNode;
 import org.jruby.ast.StarNode;
 import org.jruby.ast.ToAryNode;
 import org.jruby.ast.UndefNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.VAliasNode;
 import org.jruby.ast.WhenNode;
 import org.jruby.ast.XStrNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *
  * @author headius
  */
 public class NodeCompilerFactory {
     public static void compile(Node node, MethodCompiler context) {
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
         case OPTNNODE:
             throw new NotCompilableException("Opt N at: " + node.getPosition());
         case ORNODE:
             compileOr(node, context);
             break;
         case POSTEXENODE:
             throw new NotCompilableException("END block at: " + node.getPosition());
         case REDONODE:
             compileRedo(node, context);
             break;
         case REGEXPNODE:
             compileRegexp(node, context);
             break;
         case RESCUEBODYNODE:
             throw new NotCompilableException("rescue body at: " + node.getPosition());
         case RESCUENODE:
             compileRescue(node, context);
             break;
         case RETRYNODE:
             throw new NotCompilableException("retry at: " + node.getPosition());
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
             //throw new NotCompilableException("super call at: " + node.getPosition());
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
             assert false: "When nodes are handled by case node compilation.";
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
             assert false: "Unknown node encountered in compiler: " + node;
         }
     }
     
     public static void compileArguments(Node node, MethodCompiler context) {
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
     
     public static void compileAssignment(Node node, MethodCompiler context) {
         switch (node.nodeId) {
         case ATTRASSIGNNODE:
             compileAttrAssignAssignment(node, context);
             break;
         case DASGNNODE:
             compileDAsgnAssignment(node, context);
             break;
         case CLASSVARASGNNODE:
             compileClassVarAsgn(node, context);
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
         default:    
             throw new NotCompilableException("Can't compile assignment node: " + node);
         }
     }
     
     public static YARVNodesCompiler getYARVCompiler() {
         return new YARVNodesCompiler();
     }
 
     public static void compileAlias(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         final AliasNode alias = (AliasNode)node;
         
         context.defineAlias(alias.getNewName(),alias.getOldName());
         
         ClosureCallback receiverCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 context.retrieveSelfClass();
             }
         };
         
         ClosureCallback argsCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 context.createObjectArray(new Object[] {alias.getNewName()}, new ArrayCallback() {
                     public void nextValue(MethodCompiler context, Object sourceArray,
                                           int index) {
                         context.loadSymbol(alias.getNewName());
                     }
                 });
             }
         };
         
         context.getInvocationCompiler().invokeDynamic("method_added", receiverCallback, argsCallback, CallType.FUNCTIONAL, null, false);
     }
     
     public static void compileAnd(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         final AndNode andNode = (AndNode)node;
         
         compile(andNode.getFirstNode(), context);
         
         BranchCallback longCallback = new BranchCallback() {
             public void branch(MethodCompiler context) {
                 compile(andNode.getSecondNode(), context);
             }
         };
         
         context.performLogicalAnd(longCallback);
     }
     
     public static void compileArray(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         ArrayNode arrayNode = (ArrayNode)node;
         
         ArrayCallback callback = new ArrayCallback() {
             public void nextValue(MethodCompiler context, Object sourceArray, int index) {
                 Node node = (Node)((Object[])sourceArray)[index];
                 compile(node, context);
             }
         };
         
         context.createObjectArray(arrayNode.childNodes().toArray(), callback);
         context.createNewArray(arrayNode.isLightweight());
     }
     
     public static void compileArgsCat(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         ArgsCatNode argsCatNode = (ArgsCatNode)node;
         
         compile(argsCatNode.getFirstNode(), context);
         context.ensureRubyArray();
         compile(argsCatNode.getSecondNode(), context);
         context.splatCurrentValue();
         context.concatArrays();
     }
     
     public static void compileArgsPush(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         ArgsPushNode argsPush = (ArgsPushNode)node;
         
         compile(argsPush.getFirstNode(), context);
         compile(argsPush.getSecondNode(), context);
         context.concatArrays();
     }
     
     public static void compileAttrAssign(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         AttrAssignNode attrAssignNode = (AttrAssignNode)node;
         
         compile(attrAssignNode.getReceiverNode(), context);
         compileArguments(attrAssignNode.getArgsNode(), context);
         
         context.getInvocationCompiler().invokeAttrAssign(attrAssignNode.getName());
     }
     
     public static void compileAttrAssignAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         AttrAssignNode attrAssignNode = (AttrAssignNode)node;
         
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
     
     public static void compileBackref(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         BackRefNode iVisited = (BackRefNode) node;
         
         context.performBackref(iVisited.getType());
     }
     
     public static void compileBegin(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         BeginNode beginNode = (BeginNode)node;
         
         compile(beginNode.getBodyNode(), context);
     }
 
     public static void compileBignum(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         context.createNewBignum(((BignumNode)node).getValue());
     }
 
     public static void compileBlock(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         BlockNode blockNode = (BlockNode)node;
         
         for (Iterator<Node> iter = blockNode.childNodes().iterator(); iter.hasNext();) {
             Node n = iter.next();
             
             compile(n, context);
             
             if (iter.hasNext()) {
                 // clear result from previous line
                 context.consumeCurrentValue();
             }
         }
     }
     
     public static void compileBreak(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         final BreakNode breakNode = (BreakNode)node;
         
         ClosureCallback valueCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 if (breakNode.getValueNode() != null) {
                     NodeCompilerFactory.compile(breakNode.getValueNode(), context);
                 } else {
                     context.loadNil();
                 }
             }
         };
         try {
             context.issueBreakEvent(valueCallback);
         } catch (RuntimeException re) {
             re.printStackTrace();
             System.out.println(breakNode.getPosition());
         }
     }
     
     public static void compileCall(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         final CallNode callNode = (CallNode)node;
         
         ClosureCallback receiverCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 NodeCompilerFactory.compile(callNode.getReceiverNode(), context);
             }
         };
         
         ClosureCallback argsCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
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
             ClosureCallback closureArg = getBlock(callNode.getIterNode());
             
             if (callNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeDynamic(callNode.getName(), receiverCallback, argsCallback, CallType.NORMAL, closureArg, false);
             } else {
                 context.getInvocationCompiler().invokeDynamic(callNode.getName(), receiverCallback, null, CallType.NORMAL, closureArg, false);
             }
         }
     }
     
     public static void compileCase(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         CaseNode caseNode = (CaseNode)node;
         
         boolean hasCase = false;
         if (caseNode.getCaseNode() != null) {
             compile(caseNode.getCaseNode(), context);
             hasCase = true;
         }
 
         context.pollThreadEvents();
 
         Node firstWhenNode = caseNode.getFirstWhenNode();
         compileWhen(firstWhenNode, context, hasCase);
     }
     
     public static void compileWhen(Node node, MethodCompiler context, final boolean hasCase) {
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
         
         WhenNode whenNode = (WhenNode)node;
         
         if (whenNode.getExpressionNodes() instanceof ArrayNode) {
             ArrayNode arrayNode = (ArrayNode)whenNode.getExpressionNodes();
             
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
                         NodeCompilerFactory.compile(currentWhen.getBodyNode(), context);
                     } else {
                         context.loadNil();
                     }
                 }
             };
 
             BranchCallback falseBranch = new BranchCallback() {
                 public void branch(MethodCompiler context) {
                     // proceed to the next when
                     NodeCompilerFactory.compileWhen(currentWhen.getNextCase(), context, hasCase);
                 }
             };
 
             context.performBooleanBranch(trueBranch, falseBranch);
         }
     }
     
     public static void compileMultiArgWhen(
             final WhenNode whenNode, final ArrayNode expressionsNode, final int conditionIndex, MethodCompiler context, final boolean hasCase) {
         
         if (conditionIndex >= expressionsNode.size()) {
             // done with conditions, continue to next when in the chain
             compileWhen(whenNode.getNextCase(), context, hasCase);
             return;
         }
         
         Node tag = expressionsNode.get(conditionIndex);
 
         // need to add in position stuff some day :)
         context.setPosition(tag.getPosition());
 
         // reduce the when cases to a true or false ruby value for the branch below
         if (tag instanceof WhenNode) {
             // prepare to handle the when logic
             if (hasCase) {
                 context.duplicateCurrentValue();
             } else {
                 context.loadNull();
             }
             compile(((WhenNode)tag).getExpressionNodes(), context);
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
                     NodeCompilerFactory.compile(whenNode.getBodyNode(), context);
                 } else {
                     context.loadNil();
                 }
             }
         };
 
         BranchCallback falseBranch = new BranchCallback() {
             public void branch(MethodCompiler context) {
                 // proceed to the next when
                 NodeCompilerFactory.compileMultiArgWhen(whenNode, expressionsNode, conditionIndex + 1, context, hasCase);
             }
         };
 
         context.performBooleanBranch(trueBranch, falseBranch);
     }
     
     public static void compileClass(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         final ClassNode classNode = (ClassNode)node;
         
         final Node superNode = classNode.getSuperNode();
         
         final Node cpathNode = classNode.getCPath();
         
         ClosureCallback superCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 NodeCompilerFactory.compile(superNode, context);
             }
         };
         if (superNode == null) {
             superCallback = null;
         }
         
         ClosureCallback bodyCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 if (classNode.getBodyNode() != null) {
                     NodeCompilerFactory.compile(classNode.getBodyNode(), context);
                 } else {
                     context.loadNil();
                 }
             }
         };
         
         ClosureCallback pathCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 if (cpathNode instanceof Colon2Node) {
                     Node leftNode = ((Colon2Node)cpathNode).getLeftNode();
                     if (leftNode != null) {
                         NodeCompilerFactory.compile(leftNode, context);
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
     
     public static void compileSClass(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         final SClassNode sclassNode = (SClassNode)node;
         
         ClosureCallback receiverCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 NodeCompilerFactory.compile(sclassNode.getReceiverNode(), context);
             }
         };
         
         ClosureCallback bodyCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 if (sclassNode.getBodyNode() != null) {
                     NodeCompilerFactory.compile(sclassNode.getBodyNode(), context);
                 } else {
                     context.loadNil();
                 }
             }
         };
         
         context.defineClass("SCLASS", sclassNode.getScope(), null, null, bodyCallback, receiverCallback);
     }
 
     public static void compileClassVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         ClassVarNode classVarNode = (ClassVarNode)node;
         
         context.retrieveClassVariable(classVarNode.getName());
     }
 
     public static void compileClassVarAsgn(Node node, MethodCompiler context) {
         ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode)node;
         
         // FIXME: probably more efficient with a callback
         compile(classVarAsgnNode.getValueNode(), context);
         
         compileClassVarAsgnAssignment(node, context);
     }
 
     public static void compileClassVarAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode)node;
         
         context.assignClassVariable(classVarAsgnNode.getName());
     }
 
     public static void compileClassVarDecl(Node node, MethodCompiler context) {
         ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode)node;
         
         // FIXME: probably more efficient with a callback
         compile(classVarDeclNode.getValueNode(), context);
         
         compileClassVarDeclAssignment(node, context);
     }
 
     public static void compileClassVarDeclAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode)node;
         
         context.declareClassVariable(classVarDeclNode.getName());
     }
     
     public static void compileConstDecl(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         ConstDeclNode constDeclNode = (ConstDeclNode)node;
+        Node constNode = constDeclNode.getConstNode();
         
         if (constDeclNode.getConstNode() == null) {
             compile(constDeclNode.getValueNode(), context);
         
             context.assignConstantInCurrent(constDeclNode.getName());
-        } else if (constDeclNode.nodeId == NodeType.COLON2NODE) {
+        } else if (constNode.nodeId == NodeType.COLON2NODE) {
+            compile(((Colon2Node)constNode).getLeftNode(), context);
             compile(constDeclNode.getValueNode(), context);
             
             context.assignConstantInModule(constDeclNode.getName());
         } else {// colon3, assign in Object
             compile(constDeclNode.getValueNode(), context);
             
             context.assignConstantInObject(constDeclNode.getName());
         }
     }
     
     public static void compileConstDeclAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         ConstDeclNode constDeclNode = (ConstDeclNode)node;
         
         if (constDeclNode.getConstNode() == null) {
             context.assignConstantInCurrent(constDeclNode.getName());
         } else if (constDeclNode.nodeId == NodeType.COLON2NODE) {
             context.assignConstantInModule(constDeclNode.getName());
         } else {// colon3, assign in Object
             context.assignConstantInObject(constDeclNode.getName());
         }
     }
 
     public static void compileConst(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         ConstNode constNode = (ConstNode)node;
         
         context.retrieveConstant(constNode.getName());
     }
     
     public static void compileColon2(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         final Colon2Node iVisited = (Colon2Node) node;
         Node leftNode = iVisited.getLeftNode();
         final String name = iVisited.getName();
 
         if(leftNode == null) {
             context.loadObject();
             context.retrieveConstantFromModule(name);
         } else {
             final ClosureCallback receiverCallback = new ClosureCallback() {
                 public void compile(MethodCompiler context) {
                     NodeCompilerFactory.compile(iVisited.getLeftNode(), context);
                 }
             };
             
             BranchCallback moduleCallback = new BranchCallback() {
                     public void branch(MethodCompiler context) {
                         receiverCallback.compile(context);
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
     
     public static void compileColon3(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         Colon3Node iVisited = (Colon3Node) node;
         String name = iVisited.getName();
 
         context.loadObject();
         context.retrieveConstantFromModule(name);
     }
     
     public static void compileGetDefinitionBase(final Node node, MethodCompiler context) {
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
         context.protect(reg,out,String.class);
     }
 
     public static void compileDefined(final Node node, MethodCompiler context) {
         compileGetDefinitionBase(((DefinedNode)node).getExpressionNode(), context);
         context.stringOrNil();
     }
 
     public static void compileGetArgumentDefinition(final Node node, MethodCompiler context, String type) {
         if (node == null) {
             context.pushString(type);
         } else if(node instanceof ArrayNode) {
             Object endToken = context.getNewEnding();
             for (int i = 0; i < ((ArrayNode)node).size(); i++) {
                 Node iterNode = ((ArrayNode)node).get(i);
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
 
     public static void compileGetDefinition(final Node node, MethodCompiler context) {
         switch(node.nodeId) {
         case CLASSVARASGNNODE: case CLASSVARDECLNODE: case CONSTDECLNODE:
         case DASGNNODE: case GLOBALASGNNODE: case LOCALASGNNODE:
         case MULTIPLEASGNNODE: case OPASGNNODE: case OPELEMENTASGNNODE:
             context.pushString("assignment");
             break;
         case BACKREFNODE:
             context.backref();
             context.isInstanceOf(RubyMatchData.class, 
                                  new BranchCallback(){
                 public void branch(MethodCompiler context) {
                     context.pushString("$" + ((BackRefNode) node).getType());
                 }},
                                  new BranchCallback(){
                                      public void branch(MethodCompiler context) {
                                          context.pushNull();
                                      }});
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
         case MATCH2NODE: case MATCH3NODE:
             context.pushString("method");
             break;
         case NILNODE:
             context.pushString("nil");
             break;
         case NTHREFNODE:
             context.isCaptured(((NthRefNode) node).getMatchNumber(),
                                new BranchCallback(){
                                    public void branch(MethodCompiler context) {
                                        context.pushString("$" + ((NthRefNode) node).getMatchNumber());
                                    }},
                                new BranchCallback(){
                                    public void branch(MethodCompiler context) {
                                        context.pushNull();
                                    }});
             break;
         case SELFNODE:
             context.pushString("self");
             break;
         case VCALLNODE:
             context.loadSelf();
             context.isMethodBound(((VCallNode)node).getName(),
                                   new BranchCallback(){
                                       public void branch(MethodCompiler context){
                                           context.pushString("method");
                                       }
                                   },
                                   new BranchCallback(){
                                       public void branch(MethodCompiler context){
                                           context.pushNull();
                                       }
                                   });
             break;
         case YIELDNODE:
             context.hasBlock(new BranchCallback(){
                     public void branch(MethodCompiler context){
                         context.pushString("yield");
                     }
                 },
                 new BranchCallback(){
                     public void branch(MethodCompiler context){
                         context.pushNull();
                     }
                 });
             break;
         case GLOBALVARNODE:
             context.isGlobalDefined(((GlobalVarNode) node).getName(),
                                     new BranchCallback(){
                                         public void branch(MethodCompiler context){
                                             context.pushString("global-variable");
                                         }
                                     },
                                     new BranchCallback(){
                                         public void branch(MethodCompiler context){
                                             context.pushNull();
                                         }
                                     });
             break;
         case INSTVARNODE:
             context.isInstanceVariableDefined(((InstVarNode) node).getName(),
                                               new BranchCallback(){
                                                   public void branch(MethodCompiler context){
                                                       context.pushString("instance-variable");
                                                   }
                                               },
                                               new BranchCallback(){
                                                   public void branch(MethodCompiler context){
                                                       context.pushNull();
                                                   }
                                               });
             break;
         case CONSTNODE:
             context.isConstantDefined(((ConstNode) node).getName(),
                                       new BranchCallback(){
                                           public void branch(MethodCompiler context){
                                               context.pushString("constant");
                                           }
                                       },
                                       new BranchCallback(){
                                           public void branch(MethodCompiler context){
                                               context.pushNull();
                                           }
                                       });
             break;
         case FCALLNODE:
             context.loadSelf();
             context.isMethodBound(((FCallNode)node).getName(),
                                   new BranchCallback(){
                                       public void branch(MethodCompiler context){
                                           compileGetArgumentDefinition(((FCallNode)node).getArgsNode(), context, "method");
                                       }
                                   },
                                   new BranchCallback(){
                                       public void branch(MethodCompiler context){
                                           context.pushNull();
                                       }
                                   });
             break;
         case COLON3NODE:
         case COLON2NODE: {
             final Colon3Node iVisited = (Colon3Node) node;
 
             final String name = iVisited.getName();
 
             BranchCallback setup = new BranchCallback() {
                     public void branch(MethodCompiler context){
                         if(iVisited instanceof Colon2Node) {
                             final Node leftNode = ((Colon2Node)iVisited).getLeftNode();
                             NodeCompilerFactory.compile(leftNode, context);
                         } else {
                             context.loadObject();
                         }
                     }
                 };
             BranchCallback isConstant = new BranchCallback() {
                     public void branch(MethodCompiler context){
                         context.pushString("constant");
                     }
                 };
             BranchCallback isMethod = new BranchCallback() {
                     public void branch(MethodCompiler context){
                         context.pushString("method");
                     }
                 };
             BranchCallback none = new BranchCallback() {
                     public void branch(MethodCompiler context){
                         context.pushNull();
                     }
                 };
             context.isConstantBranch(setup, isConstant, isMethod, none, name);
         }
             break;
         case CALLNODE: {
             final CallNode iVisited = (CallNode) node;
             Object isnull = context.getNewEnding();
             Object ending = context.getNewEnding();
             NodeCompilerFactory.compileGetDefinition(iVisited.getReceiverNode(), context);
             context.ifNull(isnull);
 
             context.rescue(new BranchCallback() {
                     public void branch(MethodCompiler context) {
                         NodeCompilerFactory.compile(iVisited.getReceiverNode(), context); //[IRubyObject]
                         context.duplicateCurrentValue(); //[IRubyObject, IRubyObject]
                         context.metaclass(); //[IRubyObject, RubyClass]
                         context.duplicateCurrentValue(); //[IRubyObject, RubyClass, RubyClass]
                         context.getVisibilityFor(iVisited.getName()); //[IRubyObject, RubyClass, Visibility]
                         context.duplicateCurrentValue(); //[IRubyObject, RubyClass, Visibility, Visibility]
                         final Object isfalse = context.getNewEnding();
                         Object isreal = context.getNewEnding();
                         Object ending = context.getNewEnding();
                         context.isPrivate(isfalse,3); //[IRubyObject, RubyClass, Visibility]
                         context.isNotProtected(isreal,1); //[IRubyObject, RubyClass]
                         context.selfIsKindOf(isreal); //[IRubyObject]
                         context.consumeCurrentValue();
                         context.go(isfalse);
                         context.setEnding(isreal); //[]
                         
                         context.isMethodBound(iVisited.getName(), new BranchCallback(){
                                 public void branch(MethodCompiler context) {
                                     compileGetArgumentDefinition(iVisited.getArgsNode(), context, "method");
                                 }
                             }, 
                             new BranchCallback(){
                                 public void branch(MethodCompiler context) { 
                                     context.go(isfalse); 
                                 }
                             });
                         context.go(ending);
                         context.setEnding(isfalse);
                         context.pushNull();
                         context.setEnding(ending);
                     }}, JumpException.class,
                 new BranchCallback() {
                         public void branch(MethodCompiler context) {
                             context.pushNull();
                         }}, String.class);
 
             //          context.swapValues();
             //context.consumeCurrentValue();
             context.go(ending);
             context.setEnding(isnull);            
             context.pushNull();
             context.setEnding(ending); 
         }
             break;
         case CLASSVARNODE: {
             ClassVarNode iVisited = (ClassVarNode) node;
             final Object ending = context.getNewEnding();
             Object failure = context.getNewEnding();
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
                                           }},
                                       new BranchCallback() {
                                           public void branch(MethodCompiler context) {}});
             context.setEnding(second);  //[RubyClass]
             context.duplicateCurrentValue();
             context.isClassVarDefined(iVisited.getName(),
                                       new BranchCallback() {
                                           public void branch(MethodCompiler context) {
                                               context.consumeCurrentValue();
                                               context.pushString("class variable");
                                               context.go(ending);
                                           }},
                                       new BranchCallback() {
                                           public void branch(MethodCompiler context) {
                                           }});
             context.setEnding(third); //[RubyClass]
             context.getInstanceVariable("__attached__");  //[RubyClass]
             context.notIsModuleAndClassVarDefined(iVisited.getName(), failure); //[]
             context.pushString("class variable");
             context.go(ending);
             context.setEnding(failure);
             context.pushNull();
             context.setEnding(ending);
         }
             break;
         case ZSUPERNODE: {
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
         case SUPERNODE: {
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
 
             compileGetArgumentDefinition(((SuperNode)node).getArgsNode(), context, "super");
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
         case ATTRASSIGNNODE: {
             final AttrAssignNode iVisited = (AttrAssignNode) node;
             Object isnull = context.getNewEnding();
             Object ending = context.getNewEnding();
             NodeCompilerFactory.compileGetDefinition(iVisited.getReceiverNode(), context);
             context.ifNull(isnull);
 
             context.rescue(new BranchCallback() {
                     public void branch(MethodCompiler context) {
                         NodeCompilerFactory.compile(iVisited.getReceiverNode(), context); //[IRubyObject]
                         context.duplicateCurrentValue(); //[IRubyObject, IRubyObject]
                         context.metaclass(); //[IRubyObject, RubyClass]
                         context.duplicateCurrentValue(); //[IRubyObject, RubyClass, RubyClass]
                         context.getVisibilityFor(iVisited.getName()); //[IRubyObject, RubyClass, Visibility]
                         context.duplicateCurrentValue(); //[IRubyObject, RubyClass, Visibility, Visibility]
                         final Object isfalse = context.getNewEnding();
                         Object isreal = context.getNewEnding();
                         Object ending = context.getNewEnding();
                         context.isPrivate(isfalse,3); //[IRubyObject, RubyClass, Visibility]
                         context.isNotProtected(isreal,1); //[IRubyObject, RubyClass]
                         context.selfIsKindOf(isreal); //[IRubyObject]
                         context.consumeCurrentValue();
                         context.go(isfalse);
                         context.setEnding(isreal); //[]
 
                         context.isMethodBound(iVisited.getName(), new BranchCallback(){
                                 public void branch(MethodCompiler context) {
                                     compileGetArgumentDefinition(iVisited.getArgsNode(), context, "assignment");
                                 }
                             }, 
                             new BranchCallback(){
                                 public void branch(MethodCompiler context) { 
                                     context.go(isfalse); 
                                 }
                             });
                         context.go(ending);
                         context.setEnding(isfalse);
                         context.pushNull();
                         context.setEnding(ending);
                     }}, JumpException.class,
                 new BranchCallback() {
                         public void branch(MethodCompiler context) {
                             context.pushNull();
                         }}, String.class);
 
             context.go(ending);
             context.setEnding(isnull);            
             context.pushNull();
             context.setEnding(ending); 
         }
             break;
         default:
             context.rescue(new BranchCallback(){
                     public void branch(MethodCompiler context){
                         NodeCompilerFactory.compile(node, context);
                         context.consumeCurrentValue();
                         context.pushNull();
                     }
                 },JumpException.class, 
                 new BranchCallback(){public void branch(MethodCompiler context){context.pushNull();}}, String.class);
             context.consumeCurrentValue();
             context.pushString("expression");
         }        
     }
 
     public static void compileDAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         DAsgnNode dasgnNode = (DAsgnNode)node;
         
         compile(dasgnNode.getValueNode(), context);
         
         compileDAsgnAssignment(dasgnNode, context);
     }
 
     public static void compileDAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         DAsgnNode dasgnNode = (DAsgnNode)node;
         
         context.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth());
     }
     
     public static void compileDefn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         final DefnNode defnNode = (DefnNode)node;
         final ArgsNode argsNode = defnNode.getArgsNode();
         
         ClosureCallback body = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 if (defnNode.getBodyNode() != null) {
                     NodeCompilerFactory.compile(defnNode.getBodyNode(), context);
                 } else {
                     context.loadNil();
                 }
             }
         };
         
         ClosureCallback args = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 compileArgs(argsNode, context);
             }
         };
         
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(defnNode.getArgsNode());
         inspector.inspect(defnNode.getBodyNode());
         
         context.defineNewMethod(defnNode.getName(), defnNode.getScope(), body, args, null, inspector);
     }
     
     public static void compileDefs(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         final DefsNode defsNode = (DefsNode)node;
         final ArgsNode argsNode = defsNode.getArgsNode();
         
         ClosureCallback receiver = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 NodeCompilerFactory.compile(defsNode.getReceiverNode(), context);
             }
         };
         
         ClosureCallback body = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 if (defsNode.getBodyNode() != null) {
                     NodeCompilerFactory.compile(defsNode.getBodyNode(), context);
                 } else {
                     context.loadNil();
                 }
             }
         };
         
         ClosureCallback args = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 compileArgs(argsNode, context);
             }
         };
         
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(defsNode.getArgsNode());
         inspector.inspect(defsNode.getBodyNode());
         
         context.defineNewMethod(defsNode.getName(), defsNode.getScope(), body, args, receiver, inspector);
     }
     
     public static void compileArgs(Node node, MethodCompiler context) {
         ArgsNode argsNode = (ArgsNode)node;
         
         int required = argsNode.getRequiredArgsCount();
         int restArg = argsNode.getRestArg();
         boolean hasOptArgs = argsNode.getOptArgs() != null;
         Arity arity = argsNode.getArity();
         
         NodeCompilerFactory.confirmNodeIsSafe(argsNode);
 
         context.lineNumber(argsNode.getPosition());
         
         final ArrayCallback evalOptionalValue = new ArrayCallback() {
             public void nextValue(MethodCompiler context, Object object, int index) {
                 ListNode optArgs = (ListNode)object;
                 
                 Node node = optArgs.get(index);
 
                 compile(node, context);
             }
         };
 
         if (argsNode.getBlockArgNode() != null) {
             context.getVariableCompiler().processBlockArgument(argsNode.getBlockArgNode().getCount());
         }
 
         if (hasOptArgs) {
             if (restArg > -1) {
                 int opt = argsNode.getOptArgs().size();
                 context.getVariableCompiler().processRequiredArgs(arity, required, opt, restArg);
 
                 ListNode optArgs = argsNode.getOptArgs();
                 context.getVariableCompiler().assignOptionalArgs(optArgs, required, opt, evalOptionalValue);
 
                 context.getVariableCompiler().processRestArg(required + opt, restArg);
             } else {
                 int opt = argsNode.getOptArgs().size();
                 context.getVariableCompiler().processRequiredArgs(arity, required, opt, restArg);
 
                 ListNode optArgs = argsNode.getOptArgs();
                 context.getVariableCompiler().assignOptionalArgs(optArgs, required, opt, evalOptionalValue);
             }
         } else {
             if (restArg > -1) {
                 context.getVariableCompiler().processRequiredArgs(arity, required, 0, restArg);
 
                 context.getVariableCompiler().processRestArg(required, restArg);
             } else {
                 context.getVariableCompiler().processRequiredArgs(arity, required, 0, restArg);
             }
         }
         
     }
     
     public static void compileDot(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         DotNode dotNode = (DotNode)node;
 
         compile(dotNode.getBeginNode(), context);
         compile(dotNode.getEndNode(), context);
         
         context.createNewRange(dotNode.isExclusive());
     }
     
     public static void compileDRegexp(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final DRegexpNode dregexpNode = (DRegexpNode)node;
         
         ClosureCallback createStringCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 ArrayCallback dstrCallback = new ArrayCallback() {
                 public void nextValue(MethodCompiler context, Object sourceArray,
                                       int index) {
                         NodeCompilerFactory.compile(dregexpNode.get(index), context);
                     }
                 };
                 context.createNewString(dstrCallback,dregexpNode.size());
                 context.toJavaString();
             }
         };
    
         int opts = dregexpNode.getOptions();
         String lang = ((opts & 16) != 0) ? "n" : null;
         if((opts & 48) == 48) { // param s
             lang = "s";
         } else if((opts & 32) == 32) { // param e
             lang = "e";
         } else if((opts & 64) != 0) { // param s
             lang = "u";
         }
         
         context.createNewRegexp(createStringCallback, opts, lang);
     }
     
     public static void compileDStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final DStrNode dstrNode = (DStrNode)node;
         
         ArrayCallback dstrCallback = new ArrayCallback() {
                 public void nextValue(MethodCompiler context, Object sourceArray,
                                       int index) {
                     compile(dstrNode.get(index), context);
                 }
             };
         context.createNewString(dstrCallback,dstrNode.size());
     }
     
     public static void compileDSymbol(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final DSymbolNode dsymbolNode = (DSymbolNode)node;
         
         ArrayCallback dstrCallback = new ArrayCallback() {
                 public void nextValue(MethodCompiler context, Object sourceArray,
                                       int index) {
                     compile(dsymbolNode.get(index), context);
                 }
             };
         context.createNewSymbol(dstrCallback,dsymbolNode.size());
     }
     
     public static void compileDVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         DVarNode dvarNode = (DVarNode)node;
         
         context.getVariableCompiler().retrieveLocalVariable(dvarNode.getIndex(), dvarNode.getDepth());
     }
     
     public static void compileDXStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final DXStrNode dxstrNode = (DXStrNode)node;
         
         final ArrayCallback dstrCallback = new ArrayCallback() {
             public void nextValue(MethodCompiler context, Object sourceArray,
                                   int index) {
                 compile(dxstrNode.get(index), context);
             }
         };
         
         ClosureCallback argsCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 context.createNewString(dstrCallback,dxstrNode.size());
                 context.createObjectArray(1);
             }
         };
         
         context.getInvocationCompiler().invokeDynamic("`", null, argsCallback, CallType.FUNCTIONAL, null, false);
     }
     
     public static void compileEnsureNode(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         final EnsureNode ensureNode = (EnsureNode)node;
         
         if(ensureNode.getEnsureNode() != null) {
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
 
     public static void compileEvStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         final EvStrNode evStrNode = (EvStrNode)node;
         
         compile(evStrNode.getBody(), context);
         context.asString();
     }
     
     public static void compileFalse(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         context.loadFalse();
 
         context.pollThreadEvents();
     }
     
     public static void compileFCall(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         final FCallNode fcallNode = (FCallNode)node;
         
         ClosureCallback argsCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
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
             ClosureCallback closureArg = getBlock(fcallNode.getIterNode());
 
             if (fcallNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, argsCallback, CallType.FUNCTIONAL, closureArg, false);
             } else {
                 context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, null, CallType.FUNCTIONAL, closureArg, false);
             }
         }
     }
     
     private static ClosureCallback getBlock(Node node) {
         if (node == null) return null;
         
         switch (node.nodeId) {
         case ITERNODE:
             final IterNode iterNode = (IterNode) node;
 
             return new ClosureCallback() {
                 public void compile(MethodCompiler context) {
                     NodeCompilerFactory.compile(iterNode, context);
                 }
             };
         case BLOCKPASSNODE:
             final BlockPassNode blockPassNode = (BlockPassNode) node;
 
             return new ClosureCallback() {
                 public void compile(MethodCompiler context) {
                     NodeCompilerFactory.compile(blockPassNode.getBodyNode(), context);
                     context.unwrapPassedBlock();
                 }
             };
         default:
             throw new NotCompilableException("ERROR: Encountered a method with a non-block, non-blockpass iter node at: " + node);
         }
     }
 
     public static void compileFixnum(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         FixnumNode fixnumNode = (FixnumNode)node;
         
         context.createNewFixnum(fixnumNode.getValue());
     }
 
     public static void compileFlip(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         final FlipNode flipNode = (FlipNode)node;
         
         context.getVariableCompiler().retrieveLocalVariable(flipNode.getIndex(), flipNode.getDepth());
    
         if (flipNode.isExclusive()) {
             context.performBooleanBranch(
                     new BranchCallback() {
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getEndNode(), context);
                             context.performBooleanBranch(
                                     new BranchCallback() {
                                         public void branch(MethodCompiler context) {
                                             context.loadFalse();
                                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                                             context.consumeCurrentValue();
                                         }
                                     }, new BranchCallback() {public void branch(MethodCompiler context) {}});
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
             context.performBooleanBranch(
                     new BranchCallback() {
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getEndNode(), context);
                             context.performBooleanBranch(
                                     new BranchCallback() {
                                         public void branch(MethodCompiler context) {
                                             context.loadFalse();
                                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                                             context.consumeCurrentValue();
                                         }
                                     }, new BranchCallback() {public void branch(MethodCompiler context) {}});
                             context.loadTrue();
                         }
                     }, new BranchCallback() {
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getBeginNode(), context);
                             context.performBooleanBranch(
                                     new BranchCallback() {
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
     
     private static void becomeTrueOrFalse(MethodCompiler context) {
         context.performBooleanBranch(
                 new BranchCallback() {
                     public void branch(MethodCompiler context) {
                         context.loadTrue();
                     }
                 }, new BranchCallback() {
                     public void branch(MethodCompiler context) {
                         context.loadFalse();
                     }
                 });
     }
     
     private static void flipTrueOrFalse(MethodCompiler context) {
         context.performBooleanBranch(
                 new BranchCallback() {
                     public void branch(MethodCompiler context) {
                         context.loadFalse();
                     }
                 }, new BranchCallback() {
                     public void branch(MethodCompiler context) {
                         context.loadTrue();
                     }
                 });
     }
     
     public static void compileFloat(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         FloatNode floatNode = (FloatNode)node;
         
         context.createNewFloat(floatNode.getValue());
     }
     
     public static void compileFor(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         
         final ForNode forNode = (ForNode)node;
         
         ClosureCallback receiverCallback = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 NodeCompilerFactory.compile(forNode.getIterNode(), context);
             }
         };
            
         final ClosureCallback closureArg = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 compileForIter(forNode, context);
             }
         };
 
         context.getInvocationCompiler().invokeDynamic("each", receiverCallback, null, CallType.NORMAL, closureArg, false);
     }
     
     public static void compileForIter(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final ForNode forNode = (ForNode)node;
 
         // create the closure class and instantiate it
         final ClosureCallback closureBody = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 if (forNode.getBodyNode() != null) {
                     NodeCompilerFactory.compile(forNode.getBodyNode(), context);
                 } else {
                     context.loadNil();
                 }
             }
         };
 
         // create the closure class and instantiate it
         final ClosureCallback closureArgs = new ClosureCallback() {
             public void compile(MethodCompiler context) {
                 if (forNode.getVarNode() != null) {
                     compileAssignment(forNode.getVarNode(), context);
                 }
             }
         };
         
         boolean hasMultipleArgsHead = false;
         if (forNode.getVarNode() instanceof MultipleAsgnNode) {
diff --git a/src/org/jruby/compiler/impl/StandardASMCompiler.java b/src/org/jruby/compiler/impl/StandardASMCompiler.java
index d79249bd29..8cb8be5fc6 100644
--- a/src/org/jruby/compiler/impl/StandardASMCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardASMCompiler.java
@@ -1,1448 +1,1447 @@
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
 import java.math.BigInteger;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyLocalJumpError;
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
 import org.jruby.compiler.ClosureCallback;
 import org.jruby.compiler.InvocationCompiler;
 import org.jruby.compiler.MethodCompiler;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.ScriptCompiler;
 import org.jruby.compiler.VariableCompiler;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.util.CompilerHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.SimpleSourcePosition;
 import org.jruby.parser.ReOptions;
 import org.jruby.parser.StaticScope;
 import org.jruby.regexp.RegexpPattern;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallAdapter;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CompiledBlock;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledSharedScopeBlock;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.JRubyClassLoader;
 import org.objectweb.asm.ClassVisitor;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.Opcodes;
 
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
     
     private String classname;
     private String sourcename;
 
     private ClassWriter classWriter;
     private SkinnyMethodAdapter initMethod;
     private SkinnyMethodAdapter clinitMethod;
     int methodIndex = -1;
     int innerIndex = -1;
     
     Map<String, String> sourcePositions = new HashMap<String, String>();
     Map<String, String> byteLists = new HashMap<String, String>();
     
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
 
         out.write(writer.toByteArray());
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
 
     public void startScript() {
         classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
 
         // Create the class with the appropriate class name and source file
         classWriter.visit(V1_4, ACC_PUBLIC + ACC_SUPER, classname, null, cg.p(Object.class), new String[]{cg.p(Script.class)});
         classWriter.visitSource(sourcename, null);
 
         beginInit();
         beginClassInit();
     }
 
     public void endScript() {
         // add Script#run impl, used for running this script with a specified threadcontext and self
         // root method of a script is always in __load__ method
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
 
         // add main impl, used for detached or command-line execution of this script with a new runtime
         // root method of a script is always in stub0, method0
         method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_STATIC, "main", cg.sig(Void.TYPE, cg.params(String[].class)), null, null));
         method.start();
 
         // new instance to invoke run against
         method.newobj(classname);
         method.dup();
         method.invokespecial(classname, "<init>", cg.sig(Void.TYPE));
 
         // invoke run with threadcontext and topself
         method.invokestatic(cg.p(Ruby.class), "getDefaultInstance", cg.sig(Ruby.class));
         method.dup();
 
         method.invokevirtual(RUBY, "getCurrentContext", cg.sig(ThreadContext.class));
         method.swap();
         method.invokevirtual(RUBY, "getTopSelf", cg.sig(IRubyObject.class));
         method.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
         method.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
 
         method.invokevirtual(classname, "run", METHOD_SIGNATURE);
         method.voidreturn();
         method.end();
         
         endInit();
         endClassInit();
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
     
     public MethodCompiler startMethod(String friendlyName, ClosureCallback args, StaticScope scope, ASTInspector inspector) {
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
         protected int localVariable = EXCEPTION_INDEX + 1;
 
         public abstract void beginMethod(ClosureCallback args, StaticScope scope);
 
         public abstract void endMethod();
         
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
 
         public void loadClosure() {
             loadThreadContext();
             invokeThreadContext("getFrameBlock", cg.sig(Block.class));
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
             method.invokestatic(cg.p(CompilerHelpers.class), methodName, signature);
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
             invokeIRubyObject("getMetaClass", cg.sig(RubyClass.class));
         }
         
         public VariableCompiler getVariableCompiler() {
             return variableCompiler;
         }
         
         public InvocationCompiler getInvocationCompiler() {
             return invocationCompiler;
         }
 
         public void assignLocalVariableBlockArg(int argIndex, int varIndex) {
             // this is copying values, but it would be more efficient to just use the args in-place
             method.aload(DYNAMIC_SCOPE_INDEX);
             method.ldc(new Integer(varIndex));
             method.aload(ARGS_INDEX);
             method.ldc(new Integer(argIndex));
             method.arrayload();
             method.iconst_0();
             method.invokevirtual(cg.p(DynamicScope.class), "setValue", cg.sig(Void.TYPE, cg.params(Integer.TYPE, IRubyObject.class, Integer.TYPE)));
         }
 
         public void assignLocalVariableBlockArg(int argIndex, int varIndex, int depth) {
             if (depth == 0) {
                 assignLocalVariableBlockArg(argIndex, varIndex);
                 return;
             }
 
             method.aload(DYNAMIC_SCOPE_INDEX);
             method.ldc(new Integer(varIndex));
             method.aload(ARGS_INDEX);
             method.ldc(new Integer(argIndex));
             method.arrayload();
             method.ldc(new Integer(depth));
             method.invokevirtual(cg.p(DynamicScope.class), "setValue", cg.sig(Void.TYPE, cg.params(Integer.TYPE, IRubyObject.class, Integer.TYPE)));
         }
 
         public void assignConstantInCurrent(String name) {
             loadThreadContext();
             method.ldc(name);
             method.dup2_x1();
             method.pop2();
             invokeThreadContext("setConstantInCurrent", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
         }
 
         public void assignConstantInModule(String name) {
-            loadThreadContext();
             method.ldc(name);
-            method.swap2();
-            invokeThreadContext("setConstantInCurrent", cg.sig(IRubyObject.class, cg.params(String.class, RubyModule.class, IRubyObject.class)));
+            loadThreadContext();
+            invokeUtilityMethod("setConstantInModule", cg.sig(IRubyObject.class, IRubyObject.class, IRubyObject.class, String.class, ThreadContext.class));
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
             method.invokevirtual(cg.p(RubyModule.class), "getConstantFrom", cg.sig(IRubyObject.class, cg.params(String.class)));
         }
 
         public void retrieveClassVariable(String name) {
             loadThreadContext();
             loadRuntime();
             loadSelf();
             method.ldc(name);
 
             invokeUtilityMethod("fetchClassVariable", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class)));
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
 
             invokeUtilityMethod("setClassVariable", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
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
 
             invokeUtilityMethod("declareClassVariable", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
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
             loadRuntime();
             method.ldc(name);
             invokeIRuby("newSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
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
                 invokeUtilityMethod("createObjectArray", cg.sig(IRubyObject[].class, params));
             } else {
                 // This is pretty inefficient for building an array, so just raise an error if someone's using it for a lot of elements
                 throw new NotCompilableException("Don't use createObjectArray(int) for more than 5 elements");
             }
         }
 
         private void buildObjectArray(String type, Object[] sourceArray, ArrayCallback callback) {
             method.ldc(new Integer(sourceArray.length));
             method.anewarray(type);
 
             for (int i = 0; i < sourceArray.length; i++) {
                 method.dup();
                 method.ldc(new Integer(i));
 
                 callback.nextValue(this, sourceArray, i);
 
                 method.arraystore();
             }
         }
 
         public void createEmptyHash() {
             loadRuntime();
 
             method.invokestatic(cg.p(RubyHash.class), "newHash", cg.sig(RubyHash.class, cg.params(Ruby.class)));
         }
 
         public void createNewHash(Object elements, ArrayCallback callback, int keyCount) {
             loadRuntime();
 
             // create a new hashmap
             method.newobj(cg.p(HashMap.class));
             method.dup();
             method.invokespecial(cg.p(HashMap.class), "<init>", cg.sig(Void.TYPE));
 
             for (int i = 0; i < keyCount; i++) {
                 method.dup();
                 callback.nextValue(this, elements, i);
                 method.invokevirtual(cg.p(HashMap.class), "put", cg.sig(Object.class, cg.params(Object.class, Object.class)));
                 method.pop();
             }
 
             loadNil();
             method.invokestatic(cg.p(RubyHash.class), "newHash", cg.sig(RubyHash.class, cg.params(Ruby.class, Map.class, IRubyObject.class)));
         }
 
         public void createNewRange(boolean isExclusive) {
             loadRuntime();
 
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
             Label afterJmp = new Label();
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
                     loadClosure();
                     invokeUtilityMethod("breakJumpInWhile", cg.sig(IRubyObject.class, JumpException.BreakJump.class, Block.class));
                     method.go_to(done);
                 }
 
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
                     method.invokevirtual(cg.p(RaiseException.class), "getException", cg.sig(RubyException.class));
                     method.checkcast(cg.p(RubyLocalJumpError.class));
                     method.invokevirtual(cg.p(RubyLocalJumpError.class), "exitValue", cg.sig(IRubyObject.class));
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
 
         public void createNewClosure(StaticScope scope, int arity, ClosureCallback body, ClosureCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId) {
             String closureMethodName = "closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName);
             
             closureCompiler.beginMethod(args, scope);
             
             body.compile(closureCompiler);
             
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
             method.ldc(Block.asArgumentType(argsNodeId));
 
             invokeUtilityMethod("createBlock", cg.sig(CompiledBlock.class,
                     cg.params(ThreadContext.class, IRubyObject.class, Integer.TYPE, String[].class, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
         }
 
         public void createNewForLoop(int arity, ClosureCallback body, ClosureCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId) {
             String closureMethodName = "closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName);
             
             closureCompiler.beginMethod(args, null);
             
             body.compile(closureCompiler);
             
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
             method.ldc(Block.asArgumentType(argsNodeId));
 
             invokeUtilityMethod("createSharedScopeBlock", cg.sig(CompiledSharedScopeBlock.class,
                     cg.params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
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
 
         private void getCallbackFactory() {
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
 
         public void debug(String str) {
             method.ldc(str);
             method.getstatic(cg.p(System.class), "out", cg.ci(PrintStream.class));
             method.swap();
 
             method.invokevirtual(cg.p(PrintStream.class), "println", cg.sig(Void.TYPE, cg.params(Object.class)));
         }
 
         public void defineAlias(String newName, String oldName) {
             getRubyClass();
             method.ldc(newName);
             method.ldc(oldName);
             method.invokevirtual(cg.p(RubyModule.class), "defineAlias", cg.sig(Void.TYPE, cg.params(String.class, String.class)));
             loadNil();
             // TODO: should call method_added, and possibly push nil.
         }
 
         public void loadFalse() {
             loadRuntime();
             invokeIRuby("getFalse", cg.sig(RubyBoolean.class));
         }
 
         public void loadTrue() {
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
             loadSelf();
 
             method.ldc(name);
             invokeIRubyObject("getInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class)));
 
             // check if it's null; if so, load nil
             method.dup();
             Label notNull = new Label();
             method.ifnonnull(notNull);
 
             // pop the dup'ed null
             method.pop();
             // replace it with nil
             loadNil();
 
             method.label(notNull);
         }
 
         public void assignInstanceVariable(String name) {
             loadSelf();
             method.swap();
 
             method.ldc(name);
             method.swap();
 
             invokeIRubyObject("setInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
         }
 
         public void assignInstanceVariableBlockArg(int argIndex, String name) {
             loadSelf();
             method.ldc(name);
 
             method.aload(ARGS_INDEX);
             method.ldc(new Integer(argIndex));
             method.arrayload();
 
             invokeIRubyObject("setInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
         }
 
         public void retrieveGlobalVariable(String name) {
             loadRuntime();
 
             invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
             method.ldc(name);
             method.invokevirtual(cg.p(GlobalVariables.class), "get", cg.sig(IRubyObject.class, cg.params(String.class)));
         }
 
         public void assignGlobalVariable(String name) {
             loadRuntime();
 
             invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
             method.swap();
             method.ldc(name);
             method.swap();
             method.invokevirtual(cg.p(GlobalVariables.class), "set", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
         }
 
         public void assignGlobalVariableBlockArg(int argIndex, String name) {
             loadRuntime();
 
             invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
             method.ldc(name);
 
             method.aload(ARGS_INDEX);
             method.ldc(new Integer(argIndex));
             method.arrayload();
 
             method.invokevirtual(cg.p(GlobalVariables.class), "set", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
         }
 
         public void negateCurrentValue() {
             isTrue();
             Label isTrue = new Label();
             Label end = new Label();
             method.ifne(isTrue);
             loadTrue();
             method.go_to(end);
             method.label(isTrue);
             loadFalse();
             method.label(end);
         }
 
         public void splatCurrentValue() {
             loadRuntime();
             method.swap();
             method.invokestatic(cg.p(EvaluationState.class), "splatValue", cg.sig(RubyArray.class, cg.params(Ruby.class, IRubyObject.class)));
         }
 
         public void singlifySplattedValue() {
             loadRuntime();
             method.swap();
             method.invokestatic(cg.p(EvaluationState.class), "aValueSplat", cg.sig(IRubyObject.class, cg.params(Ruby.class, IRubyObject.class)));
         }
 
         public void aryToAry() {
             loadRuntime();
             method.swap();
             method.invokestatic(cg.p(EvaluationState.class), "aryToAry", cg.sig(IRubyObject.class, cg.params(Ruby.class, IRubyObject.class)));
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
 
         public void forEachInValueArray(int start, int count, Object source, ArrayCallback callback, ArrayCallback nilCallback, ClosureCallback argsCallback) {
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
                 argsCallback.compile(this);
                 //consume leftover assigned value
                 method.pop();
             }
         }
 
         public void loadInteger(int value) {
             throw new UnsupportedOperationException("Not supported yet.");
         }
 
         public void performGEBranch(BranchCallback trueBranch, BranchCallback falseBranch) {
             throw new UnsupportedOperationException("Not supported yet.");
         }
 
         public void performGTBranch(BranchCallback trueBranch, BranchCallback falseBranch) {
             throw new UnsupportedOperationException("Not supported yet.");
         }
 
         public void performLEBranch(BranchCallback trueBranch, BranchCallback falseBranch) {
             throw new UnsupportedOperationException("Not supported yet.");
         }
 
         public void performLTBranch(BranchCallback trueBranch, BranchCallback falseBranch) {
             throw new UnsupportedOperationException("Not supported yet.");
         }
 
         public void loadRubyArraySize() {
             throw new UnsupportedOperationException("Not supported yet.");
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
             method.invokevirtual(cg.p(RubyRegexp.class), "match2", cg.sig(IRubyObject.class, cg.params()));
         }
 
         public void match2() {
             method.invokevirtual(cg.p(RubyRegexp.class), "match", cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));
         }
 
         public void match3() {
             method.dup();
             method.instance_of(cg.p(RubyString.class));
 
             Label l0 = new Label();
             method.ifeq(l0);
 
             method.invokevirtual(cg.p(RubyRegexp.class), "match", cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));
 
             Label l1 = new Label();
             method.go_to(l1);
             method.label(l0);
 
             method.swap();
             loadThreadContext();
             method.swap();
             method.ldc("=~");
             method.swap();
 
             method.invokeinterface(cg.p(IRubyObject.class), "callMethod", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, String.class, IRubyObject.class)));
             method.label(l1);
         }
 
         public void createNewRegexp(final ByteList value, final int options, final String lang) {
             String regname = getNewConstant(cg.ci(RubyRegexp.class), "literal_reg_");
             String name = getNewConstant(cg.ci(RegexpPattern.class), "literal_re_");
             String name_flags = getNewConstant(cg.ci(Integer.TYPE), "literal_re_flags_");
 
             // in current method, load the field to see if we've created a Pattern yet
             method.aload(THIS);
             method.getfield(classname, name, cg.ci(RegexpPattern.class));
 
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             loadRuntime();
 
             // load string, for Regexp#source and Regexp#inspect
             String regexpString = null;
             if ((options & ReOptions.RE_UNICODE) > 0) {
                 regexpString = value.toUtf8String();
             } else {
                 regexpString = value.toString();
             }
 
             loadRuntime();
             method.ldc(regexpString);
             method.ldc(new Integer(options));
             invokeUtilityMethod("regexpLiteral", cg.sig(RegexpPattern.class, cg.params(Ruby.class, String.class, Integer.TYPE)));
             method.dup();
 
             method.aload(THIS);
             method.swap();
             method.putfield(classname, name, cg.ci(RegexpPattern.class));
 
             if (null == lang) {
                 method.aconst_null();
             } else {
                 method.ldc(lang);
             }
 
             method.invokestatic(cg.p(RubyRegexp.class), "newRegexp", cg.sig(RubyRegexp.class, cg.params(Ruby.class, RegexpPattern.class, String.class)));
 
             method.aload(THIS);
             method.swap();
             method.putfield(classname, regname, cg.ci(RubyRegexp.class));
             method.label(alreadyCreated);
             method.aload(THIS);
             method.getfield(classname, regname, cg.ci(RubyRegexp.class));
         }
 
         public void createNewRegexp(ClosureCallback createStringCallback, final int options, final String lang) {
             loadRuntime();
 
             loadRuntime();
             createStringCallback.compile(this);
             method.ldc(new Integer(options));
             invokeUtilityMethod("regexpLiteral", cg.sig(RegexpPattern.class, cg.params(Ruby.class, String.class, Integer.TYPE)));
 
             if (null == lang) {
                 method.aconst_null();
             } else {
                 method.ldc(lang);
             }
 
             method.invokestatic(cg.p(RubyRegexp.class), "newRegexp", cg.sig(RubyRegexp.class, cg.params(Ruby.class, RegexpPattern.class, String.class)));
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
             isInstanceOf(RubyMatchData.class,
                          new BranchCallback() {
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
             },
                          new BranchCallback() {
                              public void branch(MethodCompiler context) {
                                  method.pop();
                                  falseBranch.branch(context);
                              }
                          });
         }
 
         public void branchIfModule(ClosureCallback receiverCallback, BranchCallback moduleCallback, BranchCallback notModuleCallback) {
             receiverCallback.compile(this);
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
 
         private int ensureNumber = 1;
 
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
diff --git a/src/org/jruby/javasupport/util/CompilerHelpers.java b/src/org/jruby/javasupport/util/CompilerHelpers.java
index 00cbbac5b5..cfcdf022d0 100644
--- a/src/org/jruby/javasupport/util/CompilerHelpers.java
+++ b/src/org/jruby/javasupport/util/CompilerHelpers.java
@@ -1,580 +1,584 @@
 package org.jruby.javasupport.util;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyProc;
 import org.jruby.RubyRegexp;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.ReOptions;
 import org.jruby.parser.StaticScope;
 import org.jruby.regexp.PatternSyntaxException;
 import org.jruby.regexp.RegexpFactory;
 import org.jruby.regexp.RegexpPattern;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CompiledBlock;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledSharedScopeBlock;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 /**
  * Helper methods which are called by the compiler.  Note: These will show no consumers, but
  * generated code does call these so don't remove them thinking they are dead code. 
  *
  */
 public class CompilerHelpers {
     public static CompiledBlock createBlock(ThreadContext context, IRubyObject self, int arity, 
             String[] staticScopeNames, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argsNodeType) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         return new CompiledBlock(context, self, Arity.createArity(arity), 
                 new DynamicScope(staticScope, context.getCurrentScope()), callback, hasMultipleArgsHead, argsNodeType);
     }
     
     public static CompiledSharedScopeBlock createSharedScopeBlock(ThreadContext context, IRubyObject self, int arity, 
             CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argsNodeType) {
         
         return new CompiledSharedScopeBlock(context, self, Arity.createArity(arity), 
                 context.getCurrentScope(), callback, hasMultipleArgsHead, argsNodeType);
     }
     
     public static IRubyObject def(ThreadContext context, IRubyObject self, Object scriptObject, String name, String javaName, String[] scopeNames,
             int arity, CallConfiguration callConfig) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
         
         RubyModule containingClass = context.getRubyClass();
         Visibility visibility = context.getCurrentVisibility();
         
         if (containingClass == null) {
             throw runtime.newTypeError("No class to add method.");
         }
         
         if (containingClass == runtime.getObject() && name == "initialize") {
             runtime.getWarnings().warn("redefining Object#initialize may cause infinite loop");
         }
         
         StaticScope scope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), scopeNames);
         scope.determineModule();
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method;
         
         if (name == "initialize" || visibility.isModuleFunction()) {
             method = factory.getCompiledMethod(containingClass, javaName, 
                     Arity.createArity(arity), Visibility.PRIVATE, scope, scriptObject);
         } else {
             method = factory.getCompiledMethod(containingClass, javaName, 
                     Arity.createArity(arity), visibility, scope, scriptObject);
         }
         
         method.setCallConfig(callConfig);
         
         containingClass.addMethod(name, method);
         
         if (visibility.isModuleFunction()) {
             containingClass.getSingletonClass().addMethod(name,
                     new WrapperMethod(containingClass.getSingletonClass(), method,
                     Visibility.PUBLIC));
             containingClass.callMethod(context, "singleton_method_added", runtime.newSymbol(name));
         }
         
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             ((MetaClass) containingClass).getAttachedObject().callMethod(
                     context, "singleton_method_added", runtime.newSymbol(name));
         } else {
             containingClass.callMethod(context, "method_added", runtime.newSymbol(name));
         }
         
         return runtime.getNil();
     }
     
     public static IRubyObject defs(ThreadContext context, IRubyObject self, IRubyObject receiver, Object scriptObject, String name, String javaName, String[] scopeNames,
             int arity, CallConfiguration callConfig) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
         
         RubyClass rubyClass;
         if (receiver.isNil()) {
             rubyClass = runtime.getNilClass();
         } else if (receiver == runtime.getTrue()) {
             rubyClass = runtime.getClass("TrueClass");
         } else if (receiver == runtime.getFalse()) {
             rubyClass = runtime.getClass("FalseClass");
         } else {
             if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                 throw runtime.newSecurityError("Insecure; can't define singleton method.");
             }
             if (receiver.isFrozen()) {
                 throw runtime.newFrozenError("object");
             }
             if (receiver.getMetaClass() == runtime.getFixnum() || receiver.getMetaClass() == runtime.getClass("Symbol")) {
                 throw runtime.newTypeError("can't define singleton method \"" + name
                                            + "\" for " + receiver.getType());
             }
    
             rubyClass = receiver.getSingletonClass();
         }
    
         if (runtime.getSafeLevel() >= 4) {
             Object method = rubyClass.getMethods().get(name);
             if (method != null) {
                 throw runtime.newSecurityError("Redefining method prohibited.");
             }
         }
         
         StaticScope scope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), scopeNames);
         scope.determineModule();
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method;
         
         method = factory.getCompiledMethod(rubyClass, javaName, 
                 Arity.createArity(arity), Visibility.PUBLIC, scope, scriptObject);
         
         method.setCallConfig(callConfig);
         
         rubyClass.addMethod(name, method);
         receiver.callMethod(context, "singleton_method_added", runtime.newSymbol(name));
         
         return runtime.getNil();
     }
     
     public static RubyClass getSingletonClass(Ruby runtime, IRubyObject receiver) {
         RubyClass singletonClass;
 
         if (receiver.isNil()) {
             singletonClass = runtime.getNilClass();
         } else if (receiver == runtime.getTrue()) {
             singletonClass = runtime.getClass("TrueClass");
         } else if (receiver == runtime.getFalse()) {
             singletonClass = runtime.getClass("FalseClass");
         } else if (receiver.getMetaClass() == runtime.getFixnum() || receiver.getMetaClass() == runtime.getClass("Symbol")) {
             throw runtime.newTypeError("no virtual class for " + receiver.getMetaClass().getBaseName());
         } else {
             if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                 throw runtime.newSecurityError("Insecure: can't extend object.");
             }
 
             singletonClass = receiver.getSingletonClass();
         }
         
         return singletonClass;
     }
     
     public static IRubyObject doAttrAssign(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, String name, IRubyObject caller, CallType callType, Block block) {
         if (receiver == caller) callType = CallType.VARIABLE;
         
         try {
             return receiver.compilerCallMethod(context, name, args, caller, callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
     
     public static IRubyObject doAttrAssignIndexed(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, byte methodIndex, String name, IRubyObject caller, 
             CallType callType, Block block) {
         if (receiver == caller) callType = CallType.VARIABLE;
         
         try {
             return receiver.compilerCallMethodWithIndex(context, methodIndex, name, args, caller, 
                     callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
     
     public static IRubyObject doInvokeDynamic(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, String name, IRubyObject caller, CallType callType, Block block) {
         try {
             return receiver.compilerCallMethod(context, name, args, caller, callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
     
     public static IRubyObject doInvokeDynamicIndexed(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, byte methodIndex, String name, IRubyObject caller, 
             CallType callType, Block block) {
         try {
             return receiver.compilerCallMethodWithIndex(context, methodIndex, name, args, caller, 
                     callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
 
     public static RubyArray ensureRubyArray(IRubyObject value) {
         if (!(value instanceof RubyArray)) {
             value = RubyArray.newArray(value.getRuntime(), value);
         }
         return (RubyArray) value;
     }
 
     public static RubyArray ensureMultipleAssignableRubyArray(Ruby runtime, IRubyObject value, boolean masgnHasHead) {
         if (!(value instanceof RubyArray)) {
             value = ArgsUtil.convertToRubyArray(runtime, value, masgnHasHead);
         }
         return (RubyArray) value;
     }
     
     public static IRubyObject fetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name) {
         RubyModule rubyClass = EvaluationState.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.getClassVar(name);
     }
    
     // needs to be rewritten to support new jump exceptions
 //    public static IRubyObject handleJumpException(JumpException je, Block block) {
 //        // JRUBY-530, Kernel#loop case:
 //        if (je.isBreakInKernelLoop()) {
 //            // consume and rethrow or just keep rethrowing?
 //            if (block == je.getTarget()) je.setBreakInKernelLoop(false);
 //
 //            throw je;
 //        }
 //
 //        return (IRubyObject) je.getValue();
 //    }
     
     public static IRubyObject nullToNil(IRubyObject value, Ruby runtime) {
         return value != null ? value : runtime.getNil();
     }
     
     public static RubyClass prepareSuperClass(Ruby runtime, IRubyObject rubyClass) {
         if (!(rubyClass instanceof RubyClass)) {
             throw runtime.newTypeError("superclass must be a Class (" + 
                     RubyObject.trueFalseNil(rubyClass) + ") given");
         }
         return (RubyClass)rubyClass;
     }
     
     public static RubyModule prepareClassNamespace(ThreadContext context, IRubyObject rubyModule) {
         if (rubyModule == null || rubyModule.isNil()) {
             rubyModule = context.getCurrentScope().getStaticScope().getModule();
             
             if (rubyModule == null) {
                 throw context.getRuntime().newTypeError("no outer class/module");
             }
         }
         
         return (RubyModule)rubyModule;
     }
     
     public static RegexpPattern regexpLiteral(Ruby runtime, String ptr, int options) {
         IRubyObject noCaseGlobal = runtime.getGlobalVariables().get("$=");
 
         int extraOptions = noCaseGlobal.isTrue() ? ReOptions.RE_OPTION_IGNORECASE : 0;
 
         try {
             if((options & 256) == 256 ) {
                 return RegexpFactory.getFactory("java").createPattern(ByteList.create(ptr), (options & ~256) | extraOptions, 0);
             } else {
                 return runtime.getRegexpFactory().createPattern(ByteList.create(ptr), options | extraOptions, 0);
             }
         } catch(PatternSyntaxException e) {
             throw runtime.newRegexpError(e.getMessage());
         }
     }
 
     public static IRubyObject setClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name, IRubyObject value) {
         RubyModule rubyClass = EvaluationState.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     public static IRubyObject declareClassVariable(ThreadContext context, Ruby runtime, IRubyObject self, String name, IRubyObject value) {
         // FIXME: This isn't quite right; it shouldn't evaluate the value if it's going to throw the error
         RubyModule rubyClass = EvaluationState.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) throw runtime.newTypeError("no class/module to define class variable");
         
         rubyClass.setClassVar(name, value);
    
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
         if (exception.isKindOf(runtime.getClass("LocalJumpError"))) {
             RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
 
             IRubyObject reason = jumpError.reason();
 
             return reason.asSymbol();
         }
 
         throw re;
     }
     
     public static void processBlockArgument(Ruby runtime, ThreadContext context, Block block, int index) {
         if (!block.isGiven()) {
             context.getCurrentScope().setValue(index, runtime.getNil(), 0);
             return;
         }
         
         RubyProc blockArg;
         
         if (block.getProcObject() != null) {
             blockArg = block.getProcObject();
         } else {
             blockArg = runtime.newProc(false, block);
             blockArg.getBlock().isLambda = block.isLambda;
         }
         // We pass depth zero since we know this only applies to newly created local scope
         context.getCurrentScope().setValue(index, blockArg, 0);
     }
         
     public static void processRestArg(Ruby runtime, IRubyObject[] scope, int restArg, IRubyObject[] args, int start) {
         if (args.length <= start) {
             scope[restArg] = RubyArray.newArray(runtime, 0);
         } else {
             scope[restArg] = RubyArray.newArrayNoCopy(runtime, args, start);
         }
     }
     
     public static IRubyObject[] createObjectArray(IRubyObject arg1) {
         return new IRubyObject[] {arg1};
     }
     
     public static IRubyObject[] createObjectArray(IRubyObject arg1, IRubyObject arg2) {
         return new IRubyObject[] {arg1, arg2};
     }
     
     public static IRubyObject[] createObjectArray(IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return new IRubyObject[] {arg1, arg2, arg3};
     }
     
     public static IRubyObject[] createObjectArray(IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4) {
         return new IRubyObject[] {arg1, arg2, arg3, arg4};
     }
     
     public static IRubyObject[] createObjectArray(IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5) {
         return new IRubyObject[] {arg1, arg2, arg3, arg4, arg5};
     }
     
     public static Block getBlockFromBlockPassBody(IRubyObject proc, Block currentBlock) {
         Ruby runtime = proc.getRuntime();
 
         // No block from a nil proc
         if (proc.isNil()) return Block.NULL_BLOCK;
 
         // If not already a proc then we should try and make it one.
         if (!(proc instanceof RubyProc)) {
             proc = proc.convertToType(runtime.getClass("Proc"), 0, "to_proc", false);
 
             if (!(proc instanceof RubyProc)) {
                 throw runtime.newTypeError("wrong argument type "
                         + proc.getMetaClass().getName() + " (expected Proc)");
             }
         }
 
         // TODO: Add safety check for taintedness
         if (currentBlock != null && currentBlock.isGiven()) {
             RubyProc procObject = currentBlock.getProcObject();
             // The current block is already associated with proc.  No need to create a new one
             if (procObject != null && procObject == proc) return currentBlock;
         }
 
         return ((RubyProc) proc).getBlock();
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
         if (context.getFrameKlazz() == null) {
             String name = context.getFrameName();
             throw runtime.newNameError("superclass method '" + name
                     + "' disabled", name);
         }
         
         // Has the method that is calling super received a block argument
         if (!block.isGiven()) block = context.getCurrentFrame().getBlock(); 
         
         return self.callSuper(context, context.getCurrentScope().getArgValues(), block);
     }
     
     public static IRubyObject[] appendToObjectArray(IRubyObject[] array, IRubyObject add) {
         IRubyObject[] newArray = new IRubyObject[array.length + 1];
         System.arraycopy(array, 0, newArray, 0, array.length);
         newArray[array.length] = add;
         return newArray;
     }
     
     public static IRubyObject returnJump(IRubyObject result, ThreadContext context) {
         throw new JumpException.ReturnJump(context.getFrameJumpTarget(), result);
     }
     
     public static IRubyObject breakJumpInWhile(JumpException.BreakJump bj, Block aBlock) {
         // JRUBY-530, while case
         if (bj.getTarget() == aBlock) {
             bj.setTarget(null);
             
             throw bj;
         }
 
         return (IRubyObject) bj.getValue();
     }
     
     public static IRubyObject[] concatObjectArrays(IRubyObject[] array, IRubyObject[] add) {
         IRubyObject[] newArray = new IRubyObject[array.length + add.length];
         System.arraycopy(array, 0, newArray, 0, array.length);
         System.arraycopy(add, 0, newArray, array.length, add.length);
         return newArray;
     }
     
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject[] exceptions, Ruby runtime, ThreadContext context, IRubyObject self) {
         for (int i = 0; i < exceptions.length; i++) {
             if (!exceptions[i].isKindOf(runtime.getClass("Module"))) {
                 throw runtime.newTypeError("class or module required for rescue clause");
             }
             IRubyObject result = exceptions[i].callMethod(context, "===", currentException);
             if (result.isTrue()) return result;
         }
         return runtime.getFalse();
     }
     
     public static void checkSuperDisabled(ThreadContext context) {
         RubyModule klazz = context.getFrameKlazz();
         
         if (klazz == null) {
             String name = context.getFrameName();
             throw context.getRuntime().newNameError("Superclass method '" + name
                     + "' disabled.", name);
         }
     }
     
     public static Block ensureSuperBlock(Block given, Block parent) {
         if (!given.isGiven()) {
             return parent;
         }
         return given;
     }
     
     public static RubyModule findImplementerIfNecessary(boolean needsImplementer, RubyModule clazz, RubyModule implementationClass) {
         if (needsImplementer) {
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
     
     public static RubyBoolean isWhenTriggered(IRubyObject expression, IRubyObject expressionsObject, ThreadContext context) {
         RubyArray expressions = EvaluationState.splatValue(context.getRuntime(), expressionsObject);
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
+    
+    public static IRubyObject setConstantInModule(IRubyObject module, IRubyObject value, String name, ThreadContext context) {
+        return context.setConstantInModule(name, (RubyModule)module, value);
+    }
 }
diff --git a/test/testCompiler.rb b/test/testCompiler.rb
index 19a1f74698..49f4f62a46 100644
--- a/test/testCompiler.rb
+++ b/test/testCompiler.rb
@@ -1,438 +1,443 @@
 require 'jruby'
 require 'java'
 require 'test/minirunit'
 
 StandardASMCompiler = org.jruby.compiler.impl.StandardASMCompiler
 NodeCompilerFactory = org.jruby.compiler.NodeCompilerFactory
 ASTInspector = org.jruby.compiler.ASTInspector
 Block = org.jruby.runtime.Block
 IRubyObject = org.jruby.runtime.builtin.IRubyObject
 
 def compile_to_class(src)
   node = JRuby.parse(src, "testCompiler#{src.object_id}", false)
   filename = node.position.file
   classname = filename.sub("/", ".").sub("\\", ".").sub(".rb", "")
   inspector = ASTInspector.new
   inspector.inspect(node)
   context = StandardASMCompiler.new(classname, filename)
   NodeCompilerFactory.compileRoot(node, context, inspector)
 
   context.loadClass(JRuby.runtime.getJRubyClassLoader)
 end
 
 def compile_and_run(src)
   cls = compile_to_class(src)
 
   cls.new_instance.run(JRuby.runtime.current_context, JRuby.runtime.top_self, IRubyObject[0].new, Block::NULL_BLOCK)
 end
 
 asgnFixnumCode = "a = 5; a"
 asgnFloatCode = "a = 5.5; a"
 asgnStringCode = "a = 'hello'; a"
 asgnDStringCode = 'a = "hello#{42}"; a'
 asgnEvStringCode = 'a = "hello#{1+42}"; a'
 arrayCode = "['hello', 5, ['foo', 6]]"
 fcallCode = "foo('bar')"
 callCode = "'bar'.capitalize"
 ifCode = "if 1 == 1; 2; else; 3; end"
 unlessCode = "unless 1 == 1; 2; else; 3; end"
 whileCode = "a = 0; while a < 5; a = a + 2; end; a"
 whileNoBody = "$foo = false; def flip; $foo = !$foo; $foo; end; while flip; end"
 andCode = "1 && 2"
 andShortCode = "nil && 3"
 beginCode = "begin; a = 4; end; a"
 
 regexpLiteral = "/foo/"
 
 match1 = "/foo/ =~ 'foo'"
 match2 = "'foo' =~ /foo/"
 match3 = ":aaa =~ /foo/"
 
 iterBasic = "foo2('baz') { 4 }"
 
 defBasic = "def foo3(arg); arg + '2'; end"
 
 test_no_exception {
   compile_to_class(asgnFixnumCode);
 }
 
 # clone this since we're generating classnames based on object_id above
 test_equal(5, compile_and_run(asgnFixnumCode.clone))
 test_equal(5.5, compile_and_run(asgnFloatCode))
 test_equal('hello', compile_and_run(asgnStringCode))
 test_equal('hello42', compile_and_run(asgnDStringCode))
 test_equal('hello43', compile_and_run(asgnEvStringCode))
 test_equal(/foo/, compile_and_run(regexpLiteral))
 test_equal(nil, compile_and_run('$2'))
 test_equal(0, compile_and_run(match1))
 test_equal(0, compile_and_run(match2))
 test_equal(false, compile_and_run(match3))
 
 def foo(arg)
   arg + '2'
 end
 
 def foo2(arg)
   arg
 end
 
 test_equal(['hello', 5, ['foo', 6]], compile_and_run(arrayCode))
 test_equal('bar2', compile_and_run(fcallCode))
 test_equal('Bar', compile_and_run(callCode))
 test_equal(2, compile_and_run(ifCode))
 test_equal(2, compile_and_run("2 if true"))
 test_equal(3, compile_and_run(unlessCode))
 test_equal(3, compile_and_run("3 unless false"))
 test_equal(6, compile_and_run(whileCode))
 test_equal('baz', compile_and_run(iterBasic))
 compile_and_run(defBasic)
 test_equal('hello2', foo3('hello'))
 
 test_equal(2, compile_and_run(andCode))
 test_equal(nil, compile_and_run(andShortCode));
 test_equal(4, compile_and_run(beginCode));
 
 class << Object
   alias :old_method_added :method_added
   def method_added(sym)
     $method_added = sym
     old_method_added(sym)
   end
 end
 test_no_exception {
   compile_and_run("alias :to_string :to_s")
   to_string
   test_equal(:to_string, $method_added)
 }
 
 # Some complicated block var stuff
 blocksCode = <<-EOS
 def a
   yield 3
 end
 
 arr = []
 x = 1
 1.times { 
   y = 2
   arr << x
   x = 3
   a { 
     arr << y
     y = 4
     arr << x
     x = 5
   }
   arr << y
   arr << x
   x = 6
 }
 arr << x
 EOS
 
 test_equal([1,2,3,4,5,6], compile_and_run(blocksCode))
 
 yieldInBlock = <<EOS
 def foo
   bar { yield }
 end
 def bar
   yield
 end
 foo { 1 }
 EOS
 
 test_equal(1, compile_and_run(yieldInBlock))
 
 yieldInProc = <<EOS
 def foo
   proc { yield }
 end
 p = foo { 1 }
 p.call
 EOS
 
 test_equal(1, compile_and_run(yieldInProc))
 
 test_equal({}, compile_and_run("{}"))
 test_equal({:foo => :bar}, compile_and_run("{:foo => :bar}"))
 
 test_equal(1..2, compile_and_run("1..2"))
 
 # FIXME: These tests aren't quite right..only the first one should allow self.a to be accessed
 # The other two should fail because the a accessor is private at top level. Only attr assigns
 # should be made into "variable" call types and allowed through. I need a better way to
 # test these, and the too-permissive visibility should be fixed.
 test_equal(1, compile_and_run("def a=(x); 2; end; self.a = 1"))
 
 test_equal(1, compile_and_run("def a; 1; end; def a=(arg); fail; end; self.a ||= 2"))
 #test_equal([1, 1], compile_and_run("def a; @a; end; def a=(arg); @a = arg; 4; end; x = self.a ||= 1; [x, self.a]"))
 test_equal(nil, compile_and_run("def a; nil; end; def a=(arg); fail; end; self.a &&= 2"))
 #test_equal([1, 1], compile_and_run("def a; @a; end; def a=(arg); @a = arg; end; @a = 3; x = self.a &&= 1; [x, self.a]"))
 
 test_equal(1, compile_and_run("def foo; $_ = 1; bar; $_; end; def bar; $_ = 2; end; foo"))
 
 # test empty bodies
 test_no_exception {
   test_equal(nil, compile_and_run(whileNoBody))
 }
 
 test_no_exception {
   # fcall with empty block
   test_equal(nil, compile_and_run("proc { }.call"))
   # call with empty block
   # FIXME: can't call proc this way, it's private
   #test_equal(nil, compile_and_run("self.proc {}.call"))
 }
 
 # blocks with some basic single arguments
 test_no_exception {
   test_equal(1, compile_and_run("a = 0; [1].each {|a|}; a"))
   test_equal(1, compile_and_run("a = 0; [1].each {|x| a = x}; a"))
   test_equal(1, compile_and_run("[1].each {|@a|}; @a"))
   # make sure incoming array isn't treated as args array
   test_equal([1], compile_and_run("[[1]].each {|@a|}; @a"))
 }
 
 # blocks with tail (rest) arguments
 test_no_exception {
   test_equal([2,3], compile_and_run("[[1,2,3]].each {|x,*y| break y}"))
   test_equal([], compile_and_run("1.times {|x,*y| break y}"))
   test_no_exception { compile_and_run("1.times {|x,*|}")}
 }
 
 # blocks with unsupported arg layouts should still raise error
 test_exception {
   compile_and_run("1.times {|@@a|}")
 }
 test_exception {
   compile_and_run("1.times {|a[0]|}")
 }
 
 class_string = <<EOS
 class CompiledClass1
   def foo
     "cc1"
   end
 end
 CompiledClass1.new.foo
 EOS
 
 test_equal("cc1", compile_and_run(class_string))
 
 module_string = <<EOS
 module CompiledModule1
   def bar
     "cm1"
   end
 end
 
 class CompiledClass2
   include CompiledModule1
 end
 
 CompiledClass2.new.bar
 EOS
 
 test_equal("cm1", compile_and_run(module_string))
 
 # opasgn with anything other than || or && was broken
 class Holder
   attr_accessor :value
 end
 $h = Holder.new
 test_equal(1, compile_and_run("$h.value ||= 1"))
 test_equal(2, compile_and_run("$h.value &&= 2"))
 test_equal(3, compile_and_run("$h.value += 1"))
 
 # opt args
 optargs_method = <<EOS
 def foo(a, b = 1)
   [a, b]
 end
 EOS
 test_no_exception {
   compile_and_run(optargs_method)
 }
 test_equal([1, 1], compile_and_run("foo(1)"))
 test_equal([1, 2], compile_and_run("foo(1, 2)"))
 test_exception { compile_and_run("foo(1, 2, 3)") }
 
 # we do not compile opt args that cause other vars to be assigned, as in def (a=(b=1))
 test_exception { compile_and_run("def foo(a=(b=1)); end")}
 test_exception { compile_and_run("def foo(a, b=(c=1)); end")}
 
 class CoercibleToArray
   def to_ary
     [2, 3]
   end
 end
 
 # argscat
 def foo(a, b, c)
   return a, b, c
 end
 
 test_equal([1, 2, 3], compile_and_run("foo(1, *[2, 3])"))
 test_equal([1, 2, 3], compile_and_run("foo(1, *CoercibleToArray.new)"))
 
 # multiple assignment
 test_equal([1, 2, 3], compile_and_run("a = nil; 1.times { a, b, @c = 1, 2, 3; a = [a, b, @c] }; a"))
 
 # There's a bug in this test script that prevents these succeeding; commenting out for now
 #test_equal([1, nil, nil], compile_and_run("a, (b, c) = 1; [a, b, c]"))
 #test_equal([1, 2, nil], compile_and_run("a, (b, c) = 1, 2; [a, b, c]"))
 #test_equal([1, 2, 3], compile_and_run("a, (b, c) = 1, [2, 3]; [a, b, c]"))
 #test_equal([1, 2, 3], compile_and_run("a, (b, c) = 1, CoercibleToArray.new; [a, b, c]"))
 
 # until loops
 test_equal(3, compile_and_run("a = 1; until a == 3; a += 1; end; a"))
 test_equal(3, compile_and_run("a = 3; until a == 3; end; a"))
 
 # dynamic regexp
 test_equal([/foobar/, /foobaz/], compile_and_run('a = "bar"; b = []; while true; b << %r[foo#{a}]; break if a == "baz"; a = "baz"; end; b'))
 
 # return
 test_no_exception {
     test_equal(1, compile_and_run("def foo; 1; end; foo"))
     test_equal(nil, compile_and_run("def foo; return; end; foo"))
     test_equal(1, compile_and_run("def foo; return 1; end; foo"))
 }
 
 # reopening a class
 test_no_exception {
     test_equal(3, compile_and_run("class Fixnum; def foo; 3; end; end; 1.foo"))
 }
 
 # singleton defs
 test_equal("foo", compile_and_run("a = 'bar'; def a.foo; 'foo'; end; a.foo"))
 test_equal("foo", compile_and_run("class Fixnum; def self.foo; 'foo'; end; end; Fixnum.foo"))
 test_equal("foo", compile_and_run("def String.foo; 'foo'; end; String.foo"))
 
 # singleton classes
 test_equal("bar", compile_and_run("a = 'bar'; class << a; def bar; 'bar'; end; end; a.bar"))
 test_equal("bar", compile_and_run("class Fixnum; class << self; def bar; 'bar'; end; end; end; Fixnum.bar"))
 test_equal(class << Fixnum; self; end, compile_and_run("class Fixnum; def self.metaclass; class << self; self; end; end; end; Fixnum.metaclass"))
 
 # some loop flow control tests
 test_equal(nil, compile_and_run("a = true; b = while a; a = false; break; end; b"))
 test_equal(1, compile_and_run("a = true; b = while a; a = false; break 1; end; b"))
 test_equal(2, compile_and_run("a = 0; while true; a += 1; next if a < 2; break; end; a"))
 test_equal(2, compile_and_run("a = 0; while true; a += 1; next 1 if a < 2; break; end; a"))
 test_equal(2, compile_and_run("a = 0; while true; a += 1; redo if a < 2; break; end; a"))
 test_equal(nil, compile_and_run("a = false; b = until a; a = true; break; end; b"))
 test_equal(1, compile_and_run("a = false; b = until a; a = true; break 1; end; b"))
 test_equal(2, compile_and_run("a = 0; until false; a += 1; next if a < 2; break; end; a"))
 test_equal(2, compile_and_run("a = 0; until false; a += 1; next 1 if a < 2; break; end; a"))
 test_equal(2, compile_and_run("a = 0; until false; a += 1; redo if a < 2; break; end; a"))
 # same with evals
 test_equal(nil, compile_and_run("a = true; b = while a; a = false; eval 'break'; end; b"))
 test_equal(1, compile_and_run("a = true; b = while a; a = false; eval 'break 1'; end; b"))
 test_equal(2, compile_and_run("a = 0; while true; a += 1; eval 'next' if a < 2; eval 'break'; end; a"))
 test_equal(2, compile_and_run("a = 0; while true; a += 1; eval 'next 1' if a < 2; eval 'break'; end; a"))
 test_equal(2, compile_and_run("a = 0; while true; a += 1; eval 'redo' if a < 2; eval 'break'; end; a"))
 test_equal(nil, compile_and_run("a = false; b = until a; a = true; eval 'break'; end; b"))
 test_equal(1, compile_and_run("a = false; b = until a; a = true; eval 'break 1'; end; b"))
 test_equal(2, compile_and_run("a = 0; until false; a += 1; eval 'next' if a < 2; eval 'break'; end; a"))
 test_equal(2, compile_and_run("a = 0; until false; a += 1; eval 'next 1' if a < 2; eval 'break'; end; a"))
 test_equal(2, compile_and_run("a = 0; until false; a += 1; eval 'redo' if a < 2; eval 'break'; end; a"))
 
 # non-local flow control with while loops
 test_equal(2, compile_and_run("a = 0; 1.times { a += 1; redo if a < 2 }; a"))
 test_equal(3, compile_and_run("def foo(&b); while true; b.call; end; end; foo { break 3 }"))
 # this one doesn't work normally, so I wouldn't expect it to work here yet
 #test_equal(2, compile_and_run("a = 0; 1.times { a += 1; eval 'redo' if a < 2 }; a"))
 test_equal(3, compile_and_run("def foo(&b); while true; b.call; end; end; foo { eval 'break 3' }"))
 
 # block pass node compilation
 test_equal([false, true], compile_and_run("def foo; block_given?; end; p = proc {}; [foo(&nil), foo(&p)]"))
 test_equal([false, true], compile_and_run("public; def foo; block_given?; end; p = proc {}; [self.foo(&nil), self.foo(&p)]"))
 
 # backref nodes
 test_equal(["foo", "foo", "bazbar", "barfoo", "foo"], compile_and_run("'bazbarfoobarfoo' =~ /(foo)/; [$~[0], $&, $`, $', $+]"))
 test_equal(["", "foo ", "foo bar ", "foo bar foo "], compile_and_run("a = []; 'foo bar foo bar'.scan(/\\w+/) {a << $`}; a"))
 
 # argspush
 test_equal("fasdfo", compile_and_run("a = 'foo'; y = ['o']; a[*y] = 'asdf'; a"))
 
 # constnode, colon2node, and colon3node
 const_code = <<EOS
 A = 'a'; module X; B = 'b'; end; module Y; def self.go; [A, X::B, ::A]; end; end; Y.go
 EOS
 test_equal(["a", "b", "a"], compile_and_run(const_code))
 
 # flip (taken from http://redhanded.hobix.com/inspect/hopscotchingArraysWithFlipFlops.html)
 test_equal([1, 3, 5, 7, 9], compile_and_run("s = true; (1..10).reject { true if (s = !s) .. (s) }"))
 test_equal([1, 4, 7, 10], compile_and_run("s = true; (1..10).reject { true if (s = !s) .. (s = !s) }"))
 big_flip = <<EOS
 s = true; (1..10).inject([]) do |ary, v|; ary << [] unless (s = !s) .. (s = !s); ary.last << v; ary; end
 EOS
 test_equal([[1, 2, 3], [4, 5, 6], [7, 8, 9], [10]], compile_and_run(big_flip))
 big_triple_flip = <<EOS
 s = true
 (1..64).inject([]) do |ary, v|
     unless (s ^= v[2].zero?)...(s ^= !v[1].zero?)
         ary << []
     end
     ary.last << v
     ary
 end
 EOS
 expected = [[1, 2, 3, 4, 5, 6, 7, 8],
       [9, 10, 11, 12, 13, 14, 15, 16],
       [17, 18, 19, 20, 21, 22, 23, 24],
       [25, 26, 27, 28, 29, 30, 31, 32],
       [33, 34, 35, 36, 37, 38, 39, 40],
       [41, 42, 43, 44, 45, 46, 47, 48],
       [49, 50, 51, 52, 53, 54, 55, 56],
       [57, 58, 59, 60, 61, 62, 63, 64]]
 test_equal(expected, compile_and_run(big_triple_flip))
 
 # bug 1305, no values yielded to single-arg block assigns a null into the arg
 test_equal(NilClass, compile_and_run("def foo; yield; end; foo {|x| x.class}"))
 
 # ensure that invalid classes and modules raise errors
 AFixnum = 1;
 test_exception(TypeError) { compile_and_run("class AFixnum; end")}
 test_exception(TypeError) { compile_and_run("class B < AFixnum; end")}
 test_exception(TypeError) { compile_and_run("module AFixnum; end")}
 
 # attr assignment in multiple assign
 test_equal("bar", compile_and_run("a = Object.new; class << a; attr_accessor :b; end; a.b, a.b = 'baz', 'bar'; a.b"))
 test_equal(["foo", "bar"], compile_and_run("a = []; a[0], a[1] = 'foo', 'bar'; a"))
 
 # for loops
 test_equal([2, 4, 6], compile_and_run("a = []; for b in [1, 2, 3]; a << b * 2; end; a"))
 # FIXME: scoping is all wrong for running these tests, so c doesn't scope right here
 #test_equal([1, 2, 3], compile_and_run("a = []; for b, c in {:a => 1, :b => 2, :c => 3}; a << c; end; a.sort"))
 
 # ensure blocks
 test_equal(1, compile_and_run("a = 2; begin; a = 3; ensure; a = 1; end; a"))
 test_equal(1, compile_and_run("$a = 2; def foo; return; ensure; $a = 1; end; foo; $a"))
 
 # op element assign
 test_equal([4, 4], compile_and_run("a = []; [a[0] ||= 4, a[0]]"))
 test_equal([4, 4], compile_and_run("a = [4]; [a[0] ||= 5, a[0]]"))
 test_equal([4, 4], compile_and_run("a = [1]; [a[0] += 3, a[0]]"))
 test_equal([1], compile_and_run("a = {}; a[0] ||= [1]; a[0]"))
 test_equal(2, compile_and_run("a = [1]; a[0] &&= 2; a[0]"))
 
 # non-local return
 test_equal(3, compile_and_run("def foo; loop {return 3}; return 4; end; foo"))
 
 # class var declaration
 test_equal(3, compile_and_run("class Foo; @@foo = 3; end"))
 test_equal(3, compile_and_run("class Bar; @@bar = 3; def self.bar; @@bar; end; end; Bar.bar"))
 
 # rescue
 test_no_exception {
   test_equal(2, compile_and_run("x = begin; 1; raise; rescue; 2; end"))
   test_equal(3, compile_and_run("x = begin; 1; raise; rescue TypeError; 2; rescue; 3; end"))
   test_equal(4, compile_and_run("x = begin; 1; rescue; 2; else; 4; end"))
   test_equal(4, compile_and_run("def foo; begin; return 4; rescue; end; return 3; end; foo"))
 }
 
 # break in a while in an ensure
 test_no_exception {
   test_equal(5, compile_and_run("begin; x = while true; break 5; end; ensure; end"))
+}
+
+# JRUBY-1388, Foo::Bar broke in the compiler
+test_no_exception {
+  test_equal(5, compile_and_run("module Foo2; end; Foo2::Foo3 = 5; Foo2::Foo3"))
 }
\ No newline at end of file
