diff --git a/src/org/jruby/compiler/ASTInspector.java b/src/org/jruby/compiler/ASTInspector.java
index 4587a66044..596d86c299 100644
--- a/src/org/jruby/compiler/ASTInspector.java
+++ b/src/org/jruby/compiler/ASTInspector.java
@@ -1,526 +1,535 @@
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
 
 package org.jruby.compiler;
 
 import java.util.HashSet;
 import java.util.Set;
 import org.jruby.ast.AndNode;
 import org.jruby.ast.ArgsCatNode;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArgsPushNode;
 import org.jruby.ast.AssignableNode;
 import org.jruby.ast.AttrAssignNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BinaryOperatorNode;
 import org.jruby.ast.BlockAcceptingNode;
+import org.jruby.ast.BlockNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.CaseNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FlipNode;
 import org.jruby.ast.ForNode;
 import org.jruby.ast.GlobalAsgnNode;
 import org.jruby.ast.HashNode;
 import org.jruby.ast.IArgumentNode;
 import org.jruby.ast.IScopingNode;
 import org.jruby.ast.IfNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.Match2Node;
 import org.jruby.ast.Match3Node;
 import org.jruby.ast.MatchNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NewlineNode;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.OpAsgnAndNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OpElementAsgnNode;
 import org.jruby.ast.OrNode;
 import org.jruby.ast.PostExeNode;
 import org.jruby.ast.PreExeNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.SValueNode;
 import org.jruby.ast.SplatNode;
 import org.jruby.ast.SuperNode;
 import org.jruby.ast.ToAryNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.WhenNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.ast.types.INameNode;
 import org.jruby.util.SafePropertyAccessor;
 
 /**
  *
  * @author headius
  */
 public class ASTInspector {
     private boolean hasClosure;
     private boolean hasClass;
     private boolean hasDef;
     private boolean hasScopeAwareMethods;
     private boolean hasFrameAwareMethods;
     private boolean hasBlockArg;
     private boolean hasOptArgs;
     private boolean hasRestArg;
     
     public static Set<String> FRAME_AWARE_METHODS = new HashSet<String>();
     private static Set<String> SCOPE_AWARE_METHODS = new HashSet<String>();
     
     static {
         FRAME_AWARE_METHODS.add("eval");
         FRAME_AWARE_METHODS.add("module_eval");
         FRAME_AWARE_METHODS.add("class_eval");
         FRAME_AWARE_METHODS.add("instance_eval");
         FRAME_AWARE_METHODS.add("binding");
         FRAME_AWARE_METHODS.add("public");
         FRAME_AWARE_METHODS.add("private");
         FRAME_AWARE_METHODS.add("protected");
         FRAME_AWARE_METHODS.add("module_function");
         FRAME_AWARE_METHODS.add("block_given?");
         FRAME_AWARE_METHODS.add("iterator?");
         
         SCOPE_AWARE_METHODS.add("eval");
         SCOPE_AWARE_METHODS.add("module_eval");
         SCOPE_AWARE_METHODS.add("class_eval");
         SCOPE_AWARE_METHODS.add("instance_eval");
         SCOPE_AWARE_METHODS.add("binding");
         SCOPE_AWARE_METHODS.add("local_variables");
     }
     
     public void disable() {
         hasClosure = true;
         hasClass = true;
         hasDef = true;
         hasScopeAwareMethods = true;
         hasFrameAwareMethods = true;
         hasBlockArg = true;
         hasOptArgs = true;
         hasRestArg = true;
     }
     
     public static final boolean ENABLED = SafePropertyAccessor.getProperty("jruby.astInspector.enabled", "true").equals("true");
     
     public void inspect(Node node) {
         // TODO: This code effectively disables all inspection-based optimizations; none of them are 100% safe yet
         if (!ENABLED) disable();
 
         if (node == null) return;
         
         switch (node.nodeId) {
         case ALIASNODE:
             break;
         case ANDNODE:
             AndNode andNode = (AndNode)node;
             inspect(andNode.getFirstNode());
             inspect(andNode.getSecondNode());
             break;
         case ARGSCATNODE:
             ArgsCatNode argsCatNode = (ArgsCatNode)node;
             inspect(argsCatNode.getFirstNode());
             inspect(argsCatNode.getSecondNode());
             break;
         case ARGSPUSHNODE:
             ArgsPushNode argsPushNode = (ArgsPushNode)node;
             inspect(argsPushNode.getFirstNode());
             inspect(argsPushNode.getSecondNode());
             break;
         case ARGUMENTNODE:
             break;
         case ARRAYNODE:
         case BLOCKNODE:
         case DREGEXPNODE:
         case DSTRNODE:
         case DSYMBOLNODE:
         case DXSTRNODE:
         case LISTNODE:
             ListNode listNode = (ListNode)node;
             for (int i = 0; i < listNode.size(); i++) {
                 inspect(listNode.get(i));
             }
             break;
         case ARGSNODE:
             ArgsNode argsNode = (ArgsNode)node;
             if (argsNode.getBlockArgNode() != null) hasBlockArg = true;
             if (argsNode.getOptArgs() != null) {
                 hasOptArgs = true;
                 inspect(argsNode.getOptArgs());
             }
             if (argsNode.getRestArg() == -2 || argsNode.getRestArg() >= 0) hasRestArg = true;
             break;
         case ASSIGNABLENODE:
             AssignableNode assignableNode = (AssignableNode)node;
             inspect(assignableNode.getValueNode());
             break;
         case ATTRASSIGNNODE:
             AttrAssignNode attrAssignNode = (AttrAssignNode)node;
             inspect(attrAssignNode.getArgsNode());
             inspect(attrAssignNode.getReceiverNode());
             break;
         case BACKREFNODE:
             hasFrameAwareMethods = true;
             break;
         case BEGINNODE:
             inspect(((BeginNode)node).getBodyNode());
             break;
         case BIGNUMNODE:
             break;
         case BINARYOPERATORNODE:
             BinaryOperatorNode binaryOperatorNode = (BinaryOperatorNode)node;
             inspect(binaryOperatorNode.getFirstNode());
             inspect(binaryOperatorNode.getSecondNode());
             break;
         case BLOCKARGNODE:
             break;
         case BLOCKPASSNODE:
             BlockPassNode blockPassNode = (BlockPassNode)node;
             inspect(blockPassNode.getArgsNode());
             inspect(blockPassNode.getBodyNode());
             break;
         case BREAKNODE:
             inspect(((BreakNode)node).getValueNode());
             break;
         case CALLNODE:
             CallNode callNode = (CallNode)node;
             inspect(callNode.getReceiverNode());
         case FCALLNODE:
             inspect(((IArgumentNode)node).getArgsNode());
             inspect(((BlockAcceptingNode)node).getIterNode());
         case VCALLNODE:
             INameNode nameNode = (INameNode)node;
             if (FRAME_AWARE_METHODS.contains(nameNode.getName())) {
                 hasFrameAwareMethods = true;
             }
             if (SCOPE_AWARE_METHODS.contains(nameNode.getName())) {
                 hasScopeAwareMethods = true;
             }
             break;
         case CASENODE:
             CaseNode caseNode = (CaseNode)node;
             inspect(caseNode.getCaseNode());
             inspect(caseNode.getFirstWhenNode());
             break;
         case CLASSNODE:
             hasScopeAwareMethods = true;
             hasClass = true;
             break;
         case CLASSVARNODE:
             hasScopeAwareMethods = true;
             break;
         case GLOBALASGNNODE:
             GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode)node;
             if (globalAsgnNode.getName().equals("$_") || globalAsgnNode.getName().equals("$~")) {
                 hasScopeAwareMethods = true;
             }
             break;
         case CONSTDECLNODE:
         case CLASSVARASGNNODE:
         case CLASSVARDECLNODE:
             hasScopeAwareMethods = true;
         case DASGNNODE:
         case INSTASGNNODE:
         case LOCALASGNNODE:
             inspect(((AssignableNode)node).getValueNode());
             break;
         case COLON2NODE:
             inspect(((Colon2Node)node).getLeftNode());
             break;
         case COLON3NODE:
             break;
         case CONSTNODE:
             hasScopeAwareMethods = true;
             break;
         case DEFNNODE:
         case DEFSNODE:
             hasDef = true;
             hasScopeAwareMethods = true;
             break;
         case DEFINEDNODE:
             disable();
             break;
         case DOTNODE:
             DotNode dotNode = (DotNode)node;
             inspect(dotNode.getBeginNode());
             inspect(dotNode.getEndNode());
             break;
         case DVARNODE:
             break;
         case ENSURENODE:
             disable();
             break;
         case EVSTRNODE:
             inspect(((EvStrNode)node).getBody());
             break;
         case FALSENODE:
             break;
         case FIXNUMNODE:
             break;
         case FLIPNODE:
             inspect(((FlipNode)node).getBeginNode());
             inspect(((FlipNode)node).getEndNode());
             break;
         case FLOATNODE:
             break;
         case FORNODE:
             hasClosure = true;
             hasScopeAwareMethods = true;
             hasFrameAwareMethods = true;
             inspect(((ForNode)node).getIterNode());
             inspect(((ForNode)node).getBodyNode());
             inspect(((ForNode)node).getVarNode());
             break;
         case GLOBALVARNODE:
             break;
         case HASHNODE:
             HashNode hashNode = (HashNode)node;
             inspect(hashNode.getListNode());
             break;
         case IFNODE:
             IfNode ifNode = (IfNode)node;
             inspect(ifNode.getCondition());
             inspect(ifNode.getThenBody());
             inspect(ifNode.getElseBody());
             break;
         case INSTVARNODE:
             break;
         case ISCOPINGNODE:
             IScopingNode iscopingNode = (IScopingNode)node;
             inspect(iscopingNode.getCPath());
             break;
         case ITERNODE:
             hasClosure = true;
             hasFrameAwareMethods = true;
             break;
         case LOCALVARNODE:
             break;
         case MATCHNODE:
             inspect(((MatchNode)node).getRegexpNode());
             break;
         case MATCH2NODE:
             Match2Node match2Node = (Match2Node)node;
             inspect(match2Node.getReceiverNode());
             inspect(match2Node.getValueNode());
             break;
         case MATCH3NODE:
             Match3Node match3Node = (Match3Node)node;
             inspect(match3Node.getReceiverNode());
             inspect(match3Node.getValueNode());
             break;
         case MODULENODE:
             hasClass = true;
             hasScopeAwareMethods = true;
             break;
         case MULTIPLEASGNNODE:
             MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode)node;
             inspect(multipleAsgnNode.getArgsNode());
             inspect(multipleAsgnNode.getHeadNode());
             inspect(multipleAsgnNode.getValueNode());
             break;
         case NEWLINENODE:
             inspect(((NewlineNode)node).getNextNode());
             break;
         case NEXTNODE:
             inspect(((NextNode)node).getValueNode());
             break;
         case NILNODE:
             break;
         case NOTNODE:
             inspect(((NotNode)node).getConditionNode());
             break;
         case NTHREFNODE:
             break;
         case OPASGNANDNODE:
             OpAsgnAndNode opAsgnAndNode = (OpAsgnAndNode)node;
             inspect(opAsgnAndNode.getFirstNode());
             inspect(opAsgnAndNode.getSecondNode());
             break;
         case OPASGNNODE:
             OpAsgnNode opAsgnNode = (OpAsgnNode)node;
             inspect(opAsgnNode.getReceiverNode());
             inspect(opAsgnNode.getValueNode());
             break;
         case OPASGNORNODE:
             disable(); // Depends on defined
             break;
         case OPELEMENTASGNNODE:
             OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode)node;
             inspect(opElementAsgnNode.getArgsNode());
             inspect(opElementAsgnNode.getReceiverNode());
             inspect(opElementAsgnNode.getValueNode());
             break;
         case ORNODE:
             OrNode orNode = (OrNode)node;
             inspect(orNode.getFirstNode());
             inspect(orNode.getSecondNode());
             break;
         case POSTEXENODE:
             PostExeNode postExeNode = (PostExeNode)node;
             hasClosure = true;
             hasFrameAwareMethods = true;
             hasScopeAwareMethods = true;
             inspect(postExeNode.getBodyNode());
             inspect(postExeNode.getVarNode());
             break;
         case PREEXENODE:
             PreExeNode preExeNode = (PreExeNode)node;
             hasClosure = true;
             hasFrameAwareMethods = true;
             hasScopeAwareMethods = true;
             inspect(preExeNode.getBodyNode());
             inspect(preExeNode.getVarNode());
             break;
         case REDONODE:
             break;
         case REGEXPNODE:
             break;
         case ROOTNODE:
             inspect(((RootNode)node).getBodyNode());
+            if (((RootNode)node).getBodyNode() instanceof BlockNode) {
+                BlockNode blockNode = (BlockNode)((RootNode)node).getBodyNode();
+                if (blockNode.size() > 500) {
+                    // method has more than 500 lines; we'll need to split it
+                    // and therefore need to use a heap-based scope
+                    hasScopeAwareMethods = true;
+                }
+            }
             break;
         case RESCUEBODYNODE:
             disable();
             break;
         case RESCUENODE:
             disable();
             break;
         case RETRYNODE:
             disable();
             break;
         case RETURNNODE:
             inspect(((ReturnNode)node).getValueNode());
             break;
         case SCLASSNODE:
             hasClass = true;
             hasScopeAwareMethods = true;
             break;
         case SCOPENODE:
             break;
         case SELFNODE:
             break;
         case SPLATNODE:
             inspect(((SplatNode)node).getValue());
             break;
         case STARNODE:
             break;
         case STRNODE:
             break;
         case SUPERNODE:
             SuperNode superNode = (SuperNode)node;
             inspect(superNode.getArgsNode());
             inspect(superNode.getIterNode());
             break;
         case SVALUENODE:
             inspect(((SValueNode)node).getValue());
             break;
         case SYMBOLNODE:
             break;
         case TOARYNODE:
             inspect(((ToAryNode)node).getValue());
             break;
         case TRUENODE:
             break;
         case UNDEFNODE:
             hasScopeAwareMethods = true;
             break;
         case UNTILNODE:
             UntilNode untilNode = (UntilNode)node;
             inspect(untilNode.getConditionNode());
             inspect(untilNode.getBodyNode());
             break;
         case VALIASNODE:
             break;
         case WHENNODE:
             inspect(((WhenNode)node).getBodyNode());
             inspect(((WhenNode)node).getExpressionNodes());
             inspect(((WhenNode)node).getNextCase());
             break;
         case WHILENODE:
             WhileNode whileNode = (WhileNode)node;
             inspect(whileNode.getConditionNode());
             inspect(whileNode.getBodyNode());
             break;
         case XSTRNODE:
             break;
         case YIELDNODE:
             inspect(((YieldNode)node).getArgsNode());
             break;
         case ZARRAYNODE:
             break;
         case ZEROARGNODE:
             break;
         case ZSUPERNODE:
             hasScopeAwareMethods = true;
             hasFrameAwareMethods = true;
             inspect(((ZSuperNode)node).getIterNode());
             break;
         default:
             // encountered a node we don't recognize, set everything to true to disable optz
             assert false : "All nodes should be accounted for in AST inspector: " + node;
             disable();
         }
     }
 
     public boolean hasClass() {
         return hasClass;
     }
 
     public boolean hasClosure() {
         return hasClosure;
     }
 
     public boolean hasDef() {
         return hasDef;
     }
 
     public boolean hasFrameAwareMethods() {
         return hasFrameAwareMethods;
     }
 
     public boolean hasScopeAwareMethods() {
         return hasScopeAwareMethods;
     }
 
     public boolean hasBlockArg() {
         return hasBlockArg;
     }
 
     public boolean hasOptArgs() {
         return hasOptArgs;
     }
 
     public boolean hasRestArg() {
         return hasRestArg;
     }
 }
diff --git a/src/org/jruby/compiler/impl/StandardASMCompiler.java b/src/org/jruby/compiler/impl/StandardASMCompiler.java
index d73b41ed91..1f2d2e7ed9 100644
--- a/src/org/jruby/compiler/impl/StandardASMCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardASMCompiler.java
@@ -1581,1183 +1581,1185 @@ public class StandardASMCompiler implements ScriptCompiler, Opcodes {
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
         
         public void setFilePosition(ISourcePosition position) {
             if (!RubyInstanceConfig.POSITIONLESS_COMPILE_ENABLED) {
                 loadThreadContext();
                 method.ldc(position.getFile());
                 invokeThreadContext("setFile", sig(void.class, params(String.class)));
             }
         }
 
         public void setLinePosition(ISourcePosition position) {
             if (!RubyInstanceConfig.POSITIONLESS_COMPILE_ENABLED) {
                 // TODO: Put these in appropriate places to reduce the number of file sets
                 setFilePosition(position);
                 loadThreadContext();
                 method.ldc(position.getStartLine());
                 invokeThreadContext("setLine", sig(void.class, params(int.class)));
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
                     if (RubyInstanceConfig.FASTEST_COMPILE_ENABLED) {
                         method.getstatic(p(CallConfiguration.class), CallConfiguration.SCOPE_ONLY.name(), ci(CallConfiguration.class));
                     } else {
                         method.getstatic(p(CallConfiguration.class), CallConfiguration.BACKTRACE_AND_SCOPE.name(), ci(CallConfiguration.class));
                     }
                 } else {
                     if (RubyInstanceConfig.FASTEST_COMPILE_ENABLED) {
                         method.getstatic(p(CallConfiguration.class), CallConfiguration.NO_FRAME_NO_SCOPE.name(), ci(CallConfiguration.class));
                     } else {
                         method.getstatic(p(CallConfiguration.class), CallConfiguration.BACKTRACE_ONLY.name(), ci(CallConfiguration.class));
                     }
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
                 variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
             } else if (inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                 // enable "boxed" variable compilation when only a closure present
                 // this breaks using a proc as a binding
                 if (RubyInstanceConfig.BOXED_COMPILE_ENABLED && !inspector.hasScopeAwareMethods()) {
                     variableCompiler = new BoxedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
                 } else {
                     variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
                 }
             } else {
                 variableCompiler = new StackBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
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
                 variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
             } else if (inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                 // enable "boxed" variable compilation when only a closure present
                 // this breaks using a proc as a binding
                 if (RubyInstanceConfig.BOXED_COMPILE_ENABLED && !inspector.hasScopeAwareMethods()) {
                     variableCompiler = new BoxedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
                 } else {
                     variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
                 }
             } else {
                 variableCompiler = new StackBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, ARGS_INDEX, CLOSURE_INDEX, FIRST_TEMP_INDEX);
             }
             invocationCompiler = new StandardInvocationCompiler(this, method);
         }
         
         public void beginChainedMethod() {
+            method.start();
+            
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
diff --git a/test/testCompiler.rb b/test/testCompiler.rb
index d285a9ca58..5e875d7aaa 100644
--- a/test/testCompiler.rb
+++ b/test/testCompiler.rb
@@ -1,473 +1,478 @@
 require 'jruby'
 require 'java'
 require 'test/minirunit'
 
 StandardASMCompiler = org.jruby.compiler.impl.StandardASMCompiler
 ASTCompiler = org.jruby.compiler.ASTCompiler
 ASTInspector = org.jruby.compiler.ASTInspector
 Block = org.jruby.runtime.Block
 IRubyObject = org.jruby.runtime.builtin.IRubyObject
 
 def silence_warnings
   verb = $VERBOSE
   $VERBOSE = nil
   yield
 ensure
   $VERBOSE = verb
 end
 
 def compile_to_class(src)
   node = JRuby.parse(src, "testCompiler#{src.object_id}", false)
   filename = node.position.file
   classname = filename.sub("/", ".").sub("\\", ".").sub(".rb", "")
   inspector = ASTInspector.new
   inspector.inspect(node)
   context = StandardASMCompiler.new(classname, filename)
   compiler = ASTCompiler.new
   compiler.compileRoot(node, context, inspector)
 
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
 
 compile_and_run("1.times {|@@a|}")
 compile_and_run("a = []; 1.times {|a[0]|}")
 
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
 
 # opt args that cause other vars to be assigned, as in def (a=(b=1))
 compile_and_run("def foo(a=(b=1)); end")
 compile_and_run("def foo(a, b=(c=1)); end")
 
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
 
 silence_warnings {
   # bug 1305, no values yielded to single-arg block assigns a null into the arg
   test_equal(NilClass, compile_and_run("def foo; yield; end; foo {|x| x.class}"))
 }
 
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
   
   # test that $! is getting reset/cleared appropriately
   $! = nil
   test_equal(nil, compile_and_run("begin; raise; rescue; end; $!"))
   test_equal(nil, compile_and_run("1.times { begin; raise; rescue; next; end }; $!"))
   test_ok(nil != compile_and_run("begin; raise; rescue; begin; raise; rescue; end; $!; end"))
   test_ok(nil != compile_and_run("begin; raise; rescue; 1.times { begin; raise; rescue; next; end }; $!; end"))
 }
 
 # break in a while in an ensure
 test_no_exception {
   test_equal(5, compile_and_run("begin; x = while true; break 5; end; ensure; end"))
 }
 
 # JRUBY-1388, Foo::Bar broke in the compiler
 test_no_exception {
   test_equal(5, compile_and_run("module Foo2; end; Foo2::Foo3 = 5; Foo2::Foo3"))
 }
 
 test_equal(5, compile_and_run("def foo; yield; end; x = false; foo { break 5 if x; begin; ensure; x = true; redo; end; break 6}"))
 
 # END block
 test_no_exception { compile_and_run("END {}") }
 
 # BEGIN block
 test_equal(5, compile_and_run("BEGIN { $begin = 5 }; $begin"))
 
 # nothing at all!
 test_no_exception {
   test_equal(nil, compile_and_run(""))
 }
 
 # JRUBY-2043
 test_equal(5, compile_and_run("def foo; 1.times { a, b = [], 5; a[1] = []; return b; }; end; foo"))
-test_equal({"1" => 2}, compile_and_run("def foo; x = {1 => 2}; x.inject({}) do |hash, (key, value)|; hash[key.to_s] = value; hash; end; end; foo"))
\ No newline at end of file
+test_equal({"1" => 2}, compile_and_run("def foo; x = {1 => 2}; x.inject({}) do |hash, (key, value)|; hash[key.to_s] = value; hash; end; end; foo"))
+
+# JRUBY-2246
+long_src = "a = 1\n"
+5000.times { long_src << "a += 1\n" }
+test_equal(5001, compile_and_run(long_src))
