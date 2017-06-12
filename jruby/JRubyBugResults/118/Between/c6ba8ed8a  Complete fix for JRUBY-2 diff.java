diff --git a/src/org/jruby/evaluator/ASTInterpreter.java b/src/org/jruby/evaluator/ASTInterpreter.java
index d27c2354d4..4510e841c7 100644
--- a/src/org/jruby/evaluator/ASTInterpreter.java
+++ b/src/org/jruby/evaluator/ASTInterpreter.java
@@ -1,1848 +1,1845 @@
 /*
  ******************************************************************************
  * BEGIN LICENSE BLOCK *** Version: CPL 1.0/GPL 2.0/LGPL 2.1
  * 
  * The contents of this file are subject to the Common Public License Version
  * 1.0 (the "License"); you may not use this file except in compliance with the
  * License. You may obtain a copy of the License at
  * http://www.eclipse.org/legal/cpl-v10.html
  * 
  * Software distributed under the License is distributed on an "AS IS" basis,
  * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
  * the specific language governing rights and limitations under the License.
  * 
  * Copyright (C) 2006 Charles Oliver Nutter <headius@headius.com>
  * Copytight (C) 2006-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 Ola Bini <ola@ologix.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"), or
  * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in
  * which case the provisions of the GPL or the LGPL are applicable instead of
  * those above. If you wish to allow use of your version of this file only under
  * the terms of either the GPL or the LGPL, and not to allow others to use your
  * version of this file under the terms of the CPL, indicate your decision by
  * deleting the provisions above and replace them with the notice and other
  * provisions required by the GPL or the LGPL. If you do not delete the
  * provisions above, a recipient may use your version of this file under the
  * terms of any one of the CPL, the GPL or the LGPL. END LICENSE BLOCK ****
  ******************************************************************************/
 
 package org.jruby.evaluator;
 
 import org.jruby.Ruby;
 import org.jruby.MetaClass;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBinding;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyRange;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.AliasNode;
 import org.jruby.ast.ArgsCatNode;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArgsPushNode;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.AttrAssignNode;
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BignumNode;
 import org.jruby.ast.BinaryOperatorNode;
 import org.jruby.ast.BlockNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.CaseNode;
 import org.jruby.ast.ClassNode;
 import org.jruby.ast.ClassVarAsgnNode;
 import org.jruby.ast.ClassVarDeclNode;
 import org.jruby.ast.ClassVarNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.Colon3Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.ConstNode;
 import org.jruby.ast.DAsgnNode;
 import org.jruby.ast.DRegexpNode;
 import org.jruby.ast.DStrNode;
 import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.DVarNode;
 import org.jruby.ast.DXStrNode;
 import org.jruby.ast.DefinedNode;
 import org.jruby.ast.DefnNode;
 import org.jruby.ast.DefsNode;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EnsureNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FCallNode;
 import org.jruby.ast.FixnumNode;
 import org.jruby.ast.FlipNode;
 import org.jruby.ast.FloatNode;
 import org.jruby.ast.ForNode;
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
 import org.jruby.ast.ModuleNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NewlineNode;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.NthRefNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OpAsgnOrNode;
 import org.jruby.ast.OpElementAsgnNode;
 import org.jruby.ast.OrNode;
 import org.jruby.ast.PostExeNode;
 import org.jruby.ast.PreExeNode;
 import org.jruby.ast.RegexpNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.SClassNode;
 import org.jruby.ast.SValueNode;
 import org.jruby.ast.SplatNode;
 import org.jruby.ast.StrNode;
 import org.jruby.ast.SuperNode;
 import org.jruby.ast.SymbolNode;
 import org.jruby.ast.ToAryNode;
 import org.jruby.ast.UndefNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.VAliasNode;
 import org.jruby.ast.VCallNode;
 import org.jruby.ast.WhenNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.ast.XStrNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DefaultMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.EventHook;
 import org.jruby.runtime.SharedScopeBlock;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.InterpretedBlock;
 import org.jruby.util.TypeConverter;
 
 public class ASTInterpreter {
     public static IRubyObject eval(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block block) {
         assert self != null : "self during eval must never be null";
         try {
             return evalInternal(runtime, context, node, self, block);
         } catch (StackOverflowError sfe) {
             throw runtime.newSystemStackError("stack level too deep");
         }
     }
 
     
     /**
      * Evaluate the given string under the specified binding object. If the binding is not a Proc or Binding object
      * (RubyProc or RubyBinding) throw an appropriate type error.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param binding The binding object under which to perform the evaluation
      * @param file The filename to use when reporting errors during the evaluation
      * @param lineNumber is the line number to pretend we are starting from
      * @return An IRubyObject result from the evaluation
      */
     public static IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, IRubyObject scope, 
             String file, int lineNumber) {
         // both of these are ensured by the (very few) callers
         assert !scope.isNil();
         assert file != null;
 
         Ruby runtime = src.getRuntime();
         String savedFile = context.getFile();
         int savedLine = context.getLine();
 
         if (!(scope instanceof RubyBinding)) {
             if (scope instanceof RubyProc) {
                 scope = ((RubyProc) scope).binding();
             } else {
                 // bomb out, it's not a binding or a proc
                 throw runtime.newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
             }
         }
 
         Binding binding = ((RubyBinding)scope).getBinding();
         DynamicScope evalScope = binding.getDynamicScope().getEvalScope();
         
         // FIXME:  This determine module is in a strange location and should somehow be in block
         evalScope.getStaticScope().determineModule();
 
         try {
             // Binding provided for scope, use it
             context.preEvalWithBinding(binding);
             IRubyObject newSelf = binding.getSelf();
             RubyString source = src.convertToString();
             Node node = 
                 runtime.parseEval(source.getByteList(), file, evalScope, lineNumber);
 
             return eval(runtime, context, node, newSelf, binding.getFrame().getBlock());
         } catch (JumpException.BreakJump bj) {
             throw runtime.newLocalJumpError("break", (IRubyObject)bj.getValue(), "unexpected break");
         } catch (JumpException.RedoJump rj) {
             throw runtime.newLocalJumpError("redo", (IRubyObject)rj.getValue(), "unexpected redo");
         } finally {
             context.postEvalWithBinding(binding);
 
             // restore position
             context.setFile(savedFile);
             context.setLine(savedLine);
         }
     }
 
     /**
      * Evaluate the given string.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param file The filename to use when reporting errors during the evaluation
      * @param lineNumber that the eval supposedly starts from
      * @return An IRubyObject result from the evaluation
      */
     public static IRubyObject evalSimple(ThreadContext context, IRubyObject self, IRubyObject src, String file, int lineNumber) {
         // this is ensured by the callers
         assert file != null;
 
         Ruby runtime = src.getRuntime();
         String savedFile = context.getFile();
         int savedLine = context.getLine();
 
         // no binding, just eval in "current" frame (caller's frame)
         RubyString source = src.convertToString();
         
         DynamicScope evalScope = context.getCurrentScope().getEvalScope();
         evalScope.getStaticScope().determineModule();
         
         try {
             Node node = runtime.parseEval(source.getByteList(), file, evalScope, lineNumber);
             
             return ASTInterpreter.eval(runtime, context, node, self, Block.NULL_BLOCK);
         } catch (JumpException.BreakJump bj) {
             throw runtime.newLocalJumpError("break", (IRubyObject)bj.getValue(), "unexpected break");
         } finally {
             // restore position
             context.setFile(savedFile);
             context.setLine(savedLine);
         }
     }
 
     private static IRubyObject evalInternal(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         do {
             if (node == null) return nilNode(runtime, context);
 
             switch (node.nodeId) {
             case ALIASNODE:
                 return aliasNode(runtime, context, node);
             case ANDNODE: {
                 BinaryOperatorNode iVisited = (BinaryOperatorNode) node;
    
                 IRubyObject result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
                 if (!result.isTrue()) return result;
                 node = iVisited.getSecondNode();
                 continue;
             }
             case ARGSCATNODE:
                 return argsCatNode(runtime, context, node, self, aBlock);
             case ARGSPUSHNODE:
                 return argsPushNode(runtime, context, node, self, aBlock);
             case ARRAYNODE:
                 return arrayNode(runtime, context, node, self, aBlock);
             case ATTRASSIGNNODE:
                 return attrAssignNode(runtime, context, node, self, aBlock); 
             case BACKREFNODE:
                 return backRefNode(context, node);
             case BEGINNODE: 
                 node = ((BeginNode)node).getBodyNode();
                 continue;
             case BIGNUMNODE:
                 return bignumNode(runtime, node);
             case BLOCKNODE:
                 return blockNode(runtime, context, node, self, aBlock);
             case BLOCKPASSNODE:
             assert false: "Call nodes and friends deal with this";
             case BREAKNODE:
                 return breakNode(runtime, context, node, self, aBlock);
             case CALLNODE:
                 return callNode(runtime, context, node, self, aBlock);
             case CASENODE:
                 return caseNode(runtime, context, node, self, aBlock);
             case CLASSNODE:
                 return classNode(runtime, context, node, self, aBlock);
             case CLASSVARASGNNODE:
                 return classVarAsgnNode(runtime, context, node, self, aBlock);
             case CLASSVARDECLNODE:
                 return classVarDeclNode(runtime, context, node, self, aBlock);
             case CLASSVARNODE:
                 return classVarNode(runtime, context, node, self);
             case COLON2NODE:
                 return colon2Node(runtime, context, node, self, aBlock);
             case COLON3NODE:
                 return colon3Node(runtime, node);
             case CONSTDECLNODE:
                 return constDeclNode(runtime, context, node, self, aBlock);
             case CONSTNODE:
                 return constNode(context, node);
             case DASGNNODE:
                 return dAsgnNode(runtime, context, node, self, aBlock);
             case DEFINEDNODE:
                 return definedNode(runtime, context, node, self, aBlock);
             case DEFNNODE:
                 return defnNode(runtime, context, node);
             case DEFSNODE:
                 return defsNode(runtime, context, node, self, aBlock);
             case DOTNODE:
                 return dotNode(runtime, context, node, self, aBlock);
             case DREGEXPNODE:
                 return dregexpNode(runtime, context, node, self, aBlock);
             case DSTRNODE:
                 return dStrNode(runtime, context, node, self, aBlock);
             case DSYMBOLNODE:
                 return dSymbolNode(runtime, context, node, self, aBlock);
             case DVARNODE:
                 return dVarNode(runtime, context, node);
             case DXSTRNODE:
                 return dXStrNode(runtime, context, node, self, aBlock);
             case ENSURENODE:
                 return ensureNode(runtime, context, node, self, aBlock);
             case EVSTRNODE:
                 return evStrNode(runtime, context, node, self, aBlock);
             case FALSENODE:
                 return falseNode(runtime, context);
             case FCALLNODE:
                 return fCallNode(runtime, context, node, self, aBlock);
             case FIXNUMNODE:
                 return fixnumNode(runtime, node);
             case FLIPNODE:
                 return flipNode(runtime, context, node, self, aBlock);
             case FLOATNODE:
                 return floatNode(runtime, node);
             case FORNODE:
                 return forNode(runtime, context, node, self, aBlock);
             case GLOBALASGNNODE:
                 return globalAsgnNode(runtime, context, node, self, aBlock);
             case GLOBALVARNODE:
                 return globalVarNode(runtime, context, node);
             case HASHNODE:
                 return hashNode(runtime, context, node, self, aBlock);
             case IFNODE: {
                 IfNode iVisited = (IfNode) node;
                 IRubyObject result = evalInternal(runtime,context, iVisited.getCondition(), self, aBlock);
 
                 if (result.isTrue()) {
                     node = iVisited.getThenBody();
                 } else {
                     node = iVisited.getElseBody();
                 }
                 continue;
             }
             case INSTASGNNODE:
                 return instAsgnNode(runtime, context, node, self, aBlock);
             case INSTVARNODE:
                 return instVarNode(runtime, node, self);
             case ITERNODE: 
             assert false: "Call nodes deal with these directly";
             case LOCALASGNNODE:
                 return localAsgnNode(runtime, context, node, self, aBlock);
             case LOCALVARNODE:
                 return localVarNode(runtime, context, node);
             case MATCH2NODE:
                 return match2Node(runtime, context, node, self, aBlock);
             case MATCH3NODE:
                 return match3Node(runtime, context, node, self, aBlock);
             case MATCHNODE:
                 return matchNode(runtime, context, node, self, aBlock);
             case MODULENODE:
                 return moduleNode(runtime, context, node, self, aBlock);
             case MULTIPLEASGNNODE:
                 return multipleAsgnNode(runtime, context, node, self, aBlock);
             case NEWLINENODE: {
                 NewlineNode iVisited = (NewlineNode) node;
         
                 // something in here is used to build up ruby stack trace...
                 context.setFile(iVisited.getPosition().getFile());
                 context.setLine(iVisited.getPosition().getStartLine());
 
                 if (isTrace(runtime)) {
                     callTraceFunction(runtime, context, EventHook.RUBY_EVENT_LINE);
                 }
 
                 // TODO: do above but not below for additional newline nodes
                 node = iVisited.getNextNode();
                 continue;
             }
             case NEXTNODE:
                 return nextNode(runtime, context, node, self, aBlock);
             case NILNODE:
                 return nilNode(runtime, context);
             case NOTNODE:
                 return notNode(runtime, context, node, self, aBlock);
             case NTHREFNODE:
                 return nthRefNode(context, node);
             case OPASGNANDNODE: {
                 BinaryOperatorNode iVisited = (BinaryOperatorNode) node;
         
                 // add in reverse order
                 IRubyObject result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
                 if (!result.isTrue()) return pollAndReturn(context, result);
                 node = iVisited.getSecondNode();
                 continue;
             }
             case OPASGNNODE:
                 return opAsgnNode(runtime, context, node, self, aBlock);
             case OPASGNORNODE:
                 return opAsgnOrNode(runtime, context, node, self, aBlock);
             case OPELEMENTASGNNODE:
                 return opElementAsgnNode(runtime, context, node, self, aBlock);
             case ORNODE:
                 return orNode(runtime, context, node, self, aBlock);
             case PREEXENODE:
                 return preExeNode(runtime, context, node, self, aBlock);
             case POSTEXENODE:
                 return postExeNode(runtime, context, node, self, aBlock);
             case REDONODE: 
                 return redoNode(context, node);
             case REGEXPNODE:
                 return regexpNode(runtime, node);
             case RESCUEBODYNODE:
                 node = ((RescueBodyNode)node).getBodyNode();
                 continue;
             case RESCUENODE:
                 return rescueNode(runtime, context, node, self, aBlock);
             case RETRYNODE:
                 return retryNode(context);
             case RETURNNODE: 
                 return returnNode(runtime, context, node, self, aBlock);
             case ROOTNODE:
                 return rootNode(runtime, context, node, self, aBlock);
             case SCLASSNODE:
                 return sClassNode(runtime, context, node, self, aBlock);
             case SELFNODE:
                 return pollAndReturn(context, self);
             case SPLATNODE:
                 return splatNode(runtime, context, node, self, aBlock);
             case STRNODE:
                 return strNode(runtime, node);
             case SUPERNODE:
                 return superNode(runtime, context, node, self, aBlock);
             case SVALUENODE:
                 return sValueNode(runtime, context, node, self, aBlock);
             case SYMBOLNODE:
                 return symbolNode(runtime, node);
             case TOARYNODE:
                 return toAryNode(runtime, context, node, self, aBlock);
             case TRUENODE:
                 return trueNode(runtime, context);
             case UNDEFNODE:
                 return undefNode(runtime, context, node);
             case UNTILNODE:
                 return untilNode(runtime, context, node, self, aBlock);
             case VALIASNODE:
                 return valiasNode(runtime, node);
             case VCALLNODE:
                 return vcallNode(runtime, context, node, self);
             case WHENNODE:
                 assert false;
                 return null;
             case WHILENODE:
                 return whileNode(runtime, context, node, self, aBlock);
             case XSTRNODE:
                 return xStrNode(runtime, context, node, self);
             case YIELDNODE:
                 return yieldNode(runtime, context, node, self, aBlock);
             case ZARRAYNODE:
                 return zArrayNode(runtime);
             case ZSUPERNODE:
                 return zsuperNode(runtime, context, node, self, aBlock);
             default:
                 throw new RuntimeException("Invalid node encountered in interpreter: \"" + node.getClass().getName() + "\", please report this at www.jruby.org");
             }
         } while(true);
     }
 
     private static IRubyObject aliasNode(Ruby runtime, ThreadContext context, Node node) {
         AliasNode iVisited = (AliasNode) node;
         RuntimeHelpers.defineAlias(context, iVisited.getNewName(), iVisited.getOldName());
         RubyModule module = context.getRubyClass();
    
         if (module == null) throw runtime.newTypeError("no class to make alias");
    
         module.defineAlias(iVisited.getNewName(), iVisited.getOldName());
         module.callMethod(context, "method_added", runtime.fastNewSymbol(iVisited.getNewName()));
    
         return runtime.getNil();
     }
     
     private static IRubyObject argsCatNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ArgsCatNode iVisited = (ArgsCatNode) node;
    
         IRubyObject args = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
         IRubyObject secondArgs = splatValue(runtime, evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock));
         RubyArray list = args instanceof RubyArray ? (RubyArray) args : runtime.newArray(args);
    
         return list.concat(secondArgs);
     }
 
     private static IRubyObject argsPushNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ArgsPushNode iVisited = (ArgsPushNode) node;
         
         RubyArray args = (RubyArray) evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock).dup();
         return args.append(evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock));
     }
 
     private static IRubyObject arrayNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ArrayNode iVisited = (ArrayNode) node;
         IRubyObject[] array = new IRubyObject[iVisited.size()];
         
         for (int i = 0; i < iVisited.size(); i++) {
             Node next = iVisited.get(i);
    
             array[i] = evalInternal(runtime,context, next, self, aBlock);
         }
    
         if (iVisited.isLightweight()) {
             return runtime.newArrayNoCopyLight(array);
         }
         
         return runtime.newArrayNoCopy(array);
     }
 
     public static RubyArray arrayValue(Ruby runtime, IRubyObject value) {
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
             // remove this hack too.
             if (value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime.getKernel()) {
                 value = value.callMethod(runtime.getCurrentContext(), MethodIndex.TO_A, "to_a");
                 if (!(value instanceof RubyArray)) throw runtime.newTypeError("`to_a' did not return Array");
                 return (RubyArray)value;
             } else {
                 return runtime.newArray(value);
             }
         }
         return (RubyArray)tmp;
     }
 
     public static IRubyObject aryToAry(Ruby runtime, IRubyObject value) {
         if (value instanceof RubyArray) return value;
 
         if (value.respondsTo("to_ary")) {
             return TypeConverter.convertToType(value, runtime.getArray(), MethodIndex.TO_A, "to_ary", false);
         }
 
         return runtime.newArray(value);
     }
 
     private static IRubyObject attrAssignNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         AttrAssignNode iVisited = (AttrAssignNode) node;
    
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self, aBlock);
         
         assert receiver.getMetaClass() != null : receiver.getClass().getName();
         
         // If reciever is self then we do the call the same way as vcall
         CallType callType = (receiver == self ? CallType.VARIABLE : CallType.NORMAL);
    
         RubyModule module = receiver.getMetaClass();
         
         String name = iVisited.getName();
 
         DynamicMethod method = module.searchMethod(name);
 
         if (method.isUndefined() || (!method.isCallableFrom(self, callType))) {
             return RuntimeHelpers.callMethodMissing(context, receiver, method, name, args, self, callType, Block.NULL_BLOCK);
         }
 
         method.call(context, receiver, module, name, args);
 
         return args[args.length - 1];
     }
 
     private static IRubyObject backRefNode(ThreadContext context, Node node) {
         BackRefNode iVisited = (BackRefNode) node;
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         switch (iVisited.getType()) {
         case '~':
             if(backref instanceof RubyMatchData) {
                 ((RubyMatchData)backref).use();
             }
             return backref;
         case '&':
             return RubyRegexp.last_match(backref);
         case '`':
             return RubyRegexp.match_pre(backref);
         case '\'':
             return RubyRegexp.match_post(backref);
         case '+':
             return RubyRegexp.match_last(backref);
         default:
             assert false: "backref with invalid type";
             return null;
         }
     }
 
     private static IRubyObject bignumNode(Ruby runtime, Node node) {
         return RubyBignum.newBignum(runtime, ((BignumNode)node).getValue());
     }
 
     private static IRubyObject blockNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         BlockNode iVisited = (BlockNode) node;
    
         IRubyObject result = runtime.getNil();
         for (int i = 0; i < iVisited.size(); i++) {
             result = evalInternal(runtime,context, iVisited.get(i), self, aBlock);
         }
    
         return result;
     }
 
     private static IRubyObject breakNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         BreakNode iVisited = (BreakNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         throw new JumpException.BreakJump(null, result);
     }
 
     private static IRubyObject callNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         CallNode iVisited = (CallNode) node;
 
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self, aBlock);
         
         assert receiver.getMetaClass() != null : receiver.getClass().getName();
 
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
 
         // No block provided lets look at fast path for STI dispatch.
         if (!block.isGiven()) {
             return iVisited.callAdapter.call(context, receiver, args);
         }
             
         while (true) {
             try {
                 return iVisited.callAdapter.call(context, receiver, args, block);
             } catch (JumpException.RetryJump rj) {
                 // allow loop to retry
             } catch (JumpException.BreakJump bj) {
                 return (IRubyObject) bj.getValue();
             }
         }
     }
 
     private static IRubyObject caseNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         CaseNode iVisited = (CaseNode) node;
         IRubyObject expression = null;
         if (iVisited.getCaseNode() != null) {
             expression = evalInternal(runtime,context, iVisited.getCaseNode(), self, aBlock);
         }
 
         context.pollThreadEvents();
 
         IRubyObject result = runtime.getNil();
 
         Node firstWhenNode = iVisited.getFirstWhenNode();
         while (firstWhenNode != null) {
             if (!(firstWhenNode instanceof WhenNode)) {
                 node = firstWhenNode;
                 return evalInternal(runtime, context, node, self, aBlock);
             }
 
             WhenNode whenNode = (WhenNode) firstWhenNode;
 
             if (whenNode.getExpressionNodes() instanceof ArrayNode) {
                 ArrayNode arrayNode = (ArrayNode)whenNode.getExpressionNodes();
                 // All expressions in a while are in same file
                 context.setFile(arrayNode.getPosition().getFile());
                 for (int i = 0; i < arrayNode.size(); i++) {
                     Node tag = arrayNode.get(i);
 
                     context.setLine(tag.getPosition().getStartLine());
                     
                     if (isTrace(runtime)) {
                         callTraceFunction(runtime, context, EventHook.RUBY_EVENT_LINE);
                     }
 
                     // Ruby grammar has nested whens in a case body because of
                     // productions case_body and when_args.
                     if (tag instanceof WhenNode) {
                         IRubyObject expressionsObject = evalInternal(runtime,context, ((WhenNode) tag)
                                         .getExpressionNodes(), self, aBlock);
                         RubyArray expressions = splatValue(runtime, expressionsObject);
 
                         for (int j = 0,k = expressions.getLength(); j < k; j++) {
                             IRubyObject condition = expressions.eltInternal(j);
 
                             if ((expression != null && condition.callMethod(context, MethodIndex.OP_EQQ, "===", expression)
                                     .isTrue())
                                     || (expression == null && condition.isTrue())) {
                                 node = ((WhenNode) firstWhenNode).getBodyNode();
                                 return evalInternal(runtime, context, node, self, aBlock);
                             }
                         }
                         continue;
                     }
 
                     result = evalInternal(runtime,context, tag, self, aBlock);
 
                     if ((expression != null && result.callMethod(context, MethodIndex.OP_EQQ, "===", expression).isTrue())
                             || (expression == null && result.isTrue())) {
                         node = whenNode.getBodyNode();
                         return evalInternal(runtime, context, node, self, aBlock);
                     }
                 }
             } else {
                 result = evalInternal(runtime,context, whenNode.getExpressionNodes(), self, aBlock);
 
                 if ((expression != null && result.callMethod(context, MethodIndex.OP_EQQ, "===", expression).isTrue())
                         || (expression == null && result.isTrue())) {
                     node = ((WhenNode) firstWhenNode).getBodyNode();
                     return evalInternal(runtime, context, node, self, aBlock);
                 }
             }
 
             context.pollThreadEvents();
 
             firstWhenNode = whenNode.getNextCase();
         }
 
         return runtime.getNil();
     }
 
     private static IRubyObject classNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ClassNode iVisited = (ClassNode) node;
         Node classNameNode = iVisited.getCPath();
 
         RubyModule enclosingClass = getEnclosingModule(runtime, context, classNameNode, self, aBlock);
 
         if (enclosingClass == null) throw runtime.newTypeError("no outer class/module");
 
         Node superNode = iVisited.getSuperNode();
 
         RubyClass superClass = null;
 
         if (superNode != null) {
             IRubyObject superObj = evalInternal(runtime, context, superNode, self, aBlock);
             RubyClass.checkInheritable(superObj);
             superClass = (RubyClass)superObj;
         }
 
         String name = ((INameNode) classNameNode).getName();        
 
         RubyClass clazz = enclosingClass.defineOrGetClassUnder(name, superClass);
 
         StaticScope scope = iVisited.getScope();
         scope.setModule(clazz);
 
         return evalClassDefinitionBody(runtime, context, scope, iVisited.getBodyNode(), clazz, self, aBlock);
     }
 
     private static IRubyObject classVarAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ClassVarAsgnNode iVisited = (ClassVarAsgnNode) node;
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         RubyModule rubyClass = getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.fastSetClassVar(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject classVarDeclNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ClassVarDeclNode iVisited = (ClassVarDeclNode) node;
         RubyModule rubyClass = getClassVariableBase(context, runtime);
         
         if (rubyClass == null) {
             throw runtime.newTypeError("no class/module to define class variable");
         }
         
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         rubyClass.fastSetClassVar(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject classVarNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         ClassVarNode iVisited = (ClassVarNode) node;
         RubyModule rubyClass = getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.getClassVar(iVisited.getName());
     }
 
     private static IRubyObject colon2Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Colon2Node iVisited = (Colon2Node) node;
         Node leftNode = iVisited.getLeftNode();
         // TODO: Made this more colon3 friendly because of cpath production
         // rule in grammar (it is convenient to think of them as the same thing
         // at a grammar level even though evaluation is).
         if (leftNode == null) {
             return runtime.getObject().fastGetConstantFrom(iVisited.getName());
         } else {
             IRubyObject result = evalInternal(runtime,context, iVisited.getLeftNode(), self, aBlock);
             if (result instanceof RubyModule) {
                 return ((RubyModule) result).fastGetConstantFrom(iVisited.getName());
             } else {
                 return result.callMethod(context, iVisited.getName(), IRubyObject.NULL_ARRAY, aBlock);
             }
         }
     }
 
     private static IRubyObject colon3Node(Ruby runtime, Node node) {
         return runtime.getObject().fastGetConstantFrom(((Colon3Node)node).getName());
     }
 
     private static IRubyObject constDeclNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ConstDeclNode iVisited = (ConstDeclNode) node;
         Node constNode = iVisited.getConstNode();
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         
         if (constNode == null) {
             return context.setConstantInCurrent(iVisited.getName(), result);
         } else if (constNode.nodeId == NodeType.COLON2NODE) {
             IRubyObject obj = evalInternal(runtime,context, ((Colon2Node) iVisited.getConstNode()).getLeftNode(), self, aBlock);
-            if (!(obj instanceof RubyModule)) {
-                throw runtime.newTypeError(obj.toString() + " is not a class/module");
-            }
-            return context.setConstantInModule(iVisited.getName(), (RubyModule) obj, result);
+            return context.setConstantInModule(iVisited.getName(), obj, result);
         } else { // colon3
             return context.setConstantInObject(iVisited.getName(), result);
         }
     }
 
     private static IRubyObject constNode(ThreadContext context, Node node) {
         return context.getConstant(((ConstNode)node).getName());
     }
 
     private static IRubyObject dAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DAsgnNode iVisited = (DAsgnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
 
         // System.out.println("DSetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth() + " and set " + result);
         context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
    
         return result;
     }
 
     private static IRubyObject definedNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DefinedNode iVisited = (DefinedNode) node;
         String definition = getDefinition(runtime, context, iVisited.getExpressionNode(), self, aBlock);
         if (definition != null) {
             return runtime.newString(definition);
         } else {
             return runtime.getNil();
         }
     }
 
     private static IRubyObject defnNode(Ruby runtime, ThreadContext context, Node node) {
         DefnNode iVisited = (DefnNode) node;
         
         RubyModule containingClass = context.getRubyClass();
    
         if (containingClass == null) {
             throw runtime.newTypeError("No class to add method.");
         }
    
         String name = iVisited.getName();
 
         if (containingClass == runtime.getObject() && name == "initialize") {
             runtime.getWarnings().warn("redefining Object#initialize may cause infinite loop");
         }
 
         if (name == "__id__" || name == "__send__") {
             runtime.getWarnings().warn("redefining `" + name + "' may cause serious problem"); 
         }
 
         Visibility visibility = context.getCurrentVisibility();
         if (name == "initialize" || visibility == Visibility.MODULE_FUNCTION) {
             visibility = Visibility.PRIVATE;
         }
         
         StaticScope scope = iVisited.getScope();
         scope.determineModule();
         
         DefaultMethod newMethod = new DefaultMethod(containingClass, scope, 
                 iVisited.getBodyNode(), (ArgsNode) iVisited.getArgsNode(), 
                 visibility, iVisited.getPosition());
    
         containingClass.addMethod(name, newMethod);
    
         if (context.getCurrentVisibility() == Visibility.MODULE_FUNCTION) {
             containingClass.getSingletonClass().addMethod(
                     name,
                     new WrapperMethod(containingClass.getSingletonClass(), newMethod,
                             Visibility.PUBLIC));
             containingClass.callMethod(context, "singleton_method_added", runtime.fastNewSymbol(name));
         }
    
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             ((MetaClass) containingClass).getAttached().callMethod(
                     context, "singleton_method_added", runtime.fastNewSymbol(iVisited.getName()));
         } else {
             containingClass.callMethod(context, "method_added", runtime.fastNewSymbol(name));
         }
    
         return runtime.getNil();
     }
     
     private static IRubyObject defsNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DefsNode iVisited = (DefsNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         String name = iVisited.getName();
 
         if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
             throw runtime.newSecurityError("Insecure; can't define singleton method.");
         }
 
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
           throw runtime.newTypeError("can't define singleton method \"" + name
           + "\" for " + receiver.getMetaClass().getBaseName());
         }
 
         if (receiver.isFrozen()) throw runtime.newFrozenError("object");
 
         RubyClass rubyClass = receiver.getSingletonClass();
 
         if (runtime.getSafeLevel() >= 4 && rubyClass.getMethods().get(name) != null) {
             throw runtime.newSecurityError("redefining method prohibited.");
         }
 
         StaticScope scope = iVisited.getScope();
         scope.determineModule();
       
         DefaultMethod newMethod = new DefaultMethod(rubyClass, scope, iVisited.getBodyNode(), 
                 (ArgsNode) iVisited.getArgsNode(), Visibility.PUBLIC, iVisited.getPosition());
    
         rubyClass.addMethod(name, newMethod);
         receiver.callMethod(context, "singleton_method_added", runtime.fastNewSymbol(name));
    
         return runtime.getNil();
     }
 
     private static IRubyObject dotNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DotNode iVisited = (DotNode) node;
         return RubyRange.newRange(runtime, 
                 evalInternal(runtime,context, iVisited.getBeginNode(), self, aBlock), 
                 evalInternal(runtime,context, iVisited.getEndNode(), self, aBlock), 
                 iVisited.isExclusive());
     }
 
     private static IRubyObject dregexpNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DRegexpNode iVisited = (DRegexpNode) node;
         
         RubyRegexp regexp;
         if (iVisited.getOnce()) {
             regexp = iVisited.getOnceRegexp();
             if (regexp != null) {
                 return regexp;
             }
         }
 
         RubyString string = runtime.newString(new ByteList());
         for (int i = 0; i < iVisited.size(); i++) {
             Node iterNode = iVisited.get(i);
             if (iterNode instanceof StrNode) {
                 string.getByteList().append(((StrNode) iterNode).getValue());
             } else {
                 string.append(evalInternal(runtime,context, iterNode, self, aBlock));
             }
         }
    
         try {
             regexp = RubyRegexp.newRegexp(runtime, string.getByteList(), iVisited.getOptions());
         } catch(Exception e) {
         //                    System.err.println(iVisited.getValue().toString());
         //                    e.printStackTrace();
             throw runtime.newRegexpError(e.getMessage());
         }
         
         if (iVisited.getOnce()) {
             iVisited.setOnceRegexp(regexp);
         }
 
         return regexp;
     }
     
     private static IRubyObject dStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DStrNode iVisited = (DStrNode) node;
    
         RubyString string = runtime.newString(new ByteList());
         for (int i = 0; i < iVisited.size(); i++) {
             Node iterNode = iVisited.get(i);
             if (iterNode instanceof StrNode) {
                 string.getByteList().append(((StrNode) iterNode).getValue());
             } else {
                 string.append(evalInternal(runtime,context, iterNode, self, aBlock));
             }
         }
    
         return string;
     }
 
     private static IRubyObject dSymbolNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DSymbolNode iVisited = (DSymbolNode) node;
    
         RubyString string = runtime.newString(new ByteList());
         for (int i = 0; i < iVisited.size(); i++) {
             Node iterNode = iVisited.get(i);
             if (iterNode instanceof StrNode) {
                 string.getByteList().append(((StrNode) iterNode).getValue());
             } else {
                 string.append(evalInternal(runtime,context, iterNode, self, aBlock));
             }
         }
    
         return runtime.newSymbol(string.toString());
     }
 
     private static IRubyObject dVarNode(Ruby runtime, ThreadContext context, Node node) {
         DVarNode iVisited = (DVarNode) node;
 
         // System.out.println("DGetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth());
         IRubyObject obj = context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth());
 
         // FIXME: null check is removable once we figure out how to assign to unset named block args
         return obj == null ? runtime.getNil() : obj;
     }
 
     private static IRubyObject dXStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DXStrNode iVisited = (DXStrNode) node;
    
         RubyString string = runtime.newString(new ByteList());
         for (int i = 0; i < iVisited.size(); i++) {
             Node iterNode = iVisited.get(i);
             if (iterNode instanceof StrNode) {
                 string.getByteList().append(((StrNode) iterNode).getValue());
             } else {
                 string.append(evalInternal(runtime,context, iterNode, self, aBlock));
             }
         }
    
         return self.callMethod(context, "`", string);
     }
 
     private static IRubyObject ensureNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         EnsureNode iVisited = (EnsureNode) node;
         
         // save entering the try if there's nothing to ensure
         if (iVisited.getEnsureNode() != null) {
             IRubyObject result = runtime.getNil();
 
             try {
                 result = evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
             } finally {
                 evalInternal(runtime,context, iVisited.getEnsureNode(), self, aBlock);
             }
 
             return result;
         }
 
         node = iVisited.getBodyNode();
         return evalInternal(runtime, context, node, self, aBlock);
     }
 
     private static IRubyObject evStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return evalInternal(runtime,context, ((EvStrNode)node).getBody(), self, aBlock).asString();
     }
     
     private static IRubyObject falseNode(Ruby runtime, ThreadContext context) {
         return pollAndReturn(context, runtime.getFalse());
     }
 
     private static IRubyObject fCallNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         FCallNode iVisited = (FCallNode) node;
         
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self, aBlock);
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
 
         // No block provided lets look at fast path for STI dispatch.
         if (!block.isGiven()) {
             return iVisited.callAdapter.call(context, self, args);
         }
 
         while (true) {
             try {
                 return iVisited.callAdapter.call(context, self, args, block);
             } catch (JumpException.RetryJump rj) {
                 // allow loop to retry
             }
         }
     }
 
     private static IRubyObject fixnumNode(Ruby runtime, Node node) {
         return ((FixnumNode)node).getFixnum(runtime);
     }
 
     private static IRubyObject flipNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         FlipNode iVisited = (FlipNode) node;
         DynamicScope scope = context.getCurrentScope();
         IRubyObject result = scope.getValue(iVisited.getIndex(), iVisited.getDepth());
    
         if (iVisited.isExclusive()) {
             if (result == null || !result.isTrue()) {
                 result = evalInternal(runtime, context, iVisited.getBeginNode(), self, aBlock).isTrue() ? runtime.getTrue() : runtime.getFalse();
                 scope.setValue(iVisited.getIndex(), result, iVisited.getDepth());
                 return result;
             } else {
                 if (evalInternal(runtime, context, iVisited.getEndNode(), self, aBlock).isTrue()) {
                     scope.setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
                 }
                 
                 return runtime.getTrue();
             }
         } else {
             if (result == null || !result.isTrue()) {
                 if (evalInternal(runtime, context, iVisited.getBeginNode(), self, aBlock).isTrue()) {
                     scope.setValue(iVisited.getIndex(),
                             evalInternal(runtime, context, iVisited.getEndNode(), self, aBlock).isTrue() ? 
                                     runtime.getFalse() : runtime.getTrue(), iVisited.getDepth());
                     return runtime.getTrue();
                 } 
 
                 return runtime.getFalse();
             } else {
                 if (evalInternal(runtime, context, iVisited.getEndNode(), self, aBlock).isTrue()) {
                     scope.setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
                 }
                 return runtime.getTrue();
             }
         }
     }
 
     private static IRubyObject floatNode(Ruby runtime, Node node) {
         return RubyFloat.newFloat(runtime, ((FloatNode)node).getValue());
     }
 
     private static IRubyObject forNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ForNode iVisited = (ForNode) node;
         
         Block block = SharedScopeBlock.newInterpretedSharedScopeClosure(context, iVisited, 
                 context.getCurrentScope(), self);
    
         try {
             while (true) {
                 try {
                     String savedFile = context.getFile();
                     int savedLine = context.getLine();
    
                     IRubyObject recv = null;
                     try {
                         recv = evalInternal(runtime,context, iVisited.getIterNode(), self, aBlock);
                     } finally {
                         context.setFile(savedFile);
                         context.setLine(savedLine);
                     }
    
                     return ForNode.callAdapter.call(context, recv, block);
                 } catch (JumpException.RetryJump rj) {
                     // do nothing, allow loop to retry
                 }
             }
         } catch (JumpException.BreakJump bj) {
             return (IRubyObject) bj.getValue();
         }
     }
 
     private static IRubyObject globalAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         GlobalAsgnNode iVisited = (GlobalAsgnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         runtime.getGlobalVariables().set(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject globalVarNode(Ruby runtime, ThreadContext context, Node node) {
         GlobalVarNode iVisited = (GlobalVarNode) node;
         
         return runtime.getGlobalVariables().get(iVisited.getName());
     }
 
     private static IRubyObject hashNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         HashNode iVisited = (HashNode) node;
    
         RubyHash hash = null;
         if (iVisited.getListNode() != null) {
             hash = RubyHash.newHash(runtime);
    
         for (int i = 0; i < iVisited.getListNode().size();) {
                 // insert all nodes in sequence, hash them in the final instruction
                 // KEY
                 IRubyObject key = evalInternal(runtime,context, iVisited.getListNode().get(i++), self, aBlock);
                 IRubyObject value = evalInternal(runtime,context, iVisited.getListNode().get(i++), self, aBlock);
    
                 hash.fastASet(key, value);
             }
         }
    
         if (hash == null) {
             return RubyHash.newHash(runtime);
         }
    
         return hash;
     }
 
     private static IRubyObject instAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         InstAsgnNode iVisited = (InstAsgnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         self.getInstanceVariables().fastSetInstanceVariable(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject instVarNode(Ruby runtime, Node node, IRubyObject self) {
         InstVarNode iVisited = (InstVarNode) node;
         IRubyObject variable = self.getInstanceVariables().fastGetInstanceVariable(iVisited.getName());
    
         if (variable != null) return variable;
         
         runtime.getWarnings().warning(iVisited.getPosition(), "instance variable " + iVisited.getName() + " not initialized");
         
         return runtime.getNil();
     }
 
     private static IRubyObject localAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         LocalAsgnNode iVisited = (LocalAsgnNode) node;
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         
         //System.out.println("LSetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth() + " and set " + result);
         context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
 
         return result;
     }
 
     private static IRubyObject localVarNode(Ruby runtime, ThreadContext context, Node node) {
         LocalVarNode iVisited = (LocalVarNode) node;
 
         //        System.out.println("DGetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth());
         IRubyObject result = context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth());
 
         return result == null ? runtime.getNil() : result;
     }
 
     private static IRubyObject match2Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Match2Node iVisited = (Match2Node) node;
         IRubyObject recv = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         return ((RubyRegexp) recv).op_match(value);
     }
     
     private static IRubyObject match3Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Match3Node iVisited = (Match3Node) node;
         IRubyObject recv = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         if (value instanceof RubyString) {
             return ((RubyRegexp) recv).op_match(value);
         } else {
             return Match3Node.callAdapter.call(context, value, recv);
         }
     }
 
     private static IRubyObject matchNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return ((RubyRegexp) evalInternal(runtime,context, ((MatchNode)node).getRegexpNode(), self, aBlock)).op_match2();
     }
 
     private static IRubyObject moduleNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ModuleNode iVisited = (ModuleNode) node;
         Node classNameNode = iVisited.getCPath();
 
         RubyModule enclosingModule = getEnclosingModule(runtime, context, classNameNode, self, aBlock);
 
         if (enclosingModule == null) throw runtime.newTypeError("no outer class/module");
 
         String name = ((INameNode) classNameNode).getName();        
 
         RubyModule module = enclosingModule.defineOrGetModuleUnder(name);
 
         StaticScope scope = iVisited.getScope();
         scope.setModule(module);        
 
         return evalClassDefinitionBody(runtime, context, scope, iVisited.getBodyNode(), module, self, aBlock);
     }
 
     private static IRubyObject multipleAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         MultipleAsgnNode iVisited = (MultipleAsgnNode) node;
         
         switch (iVisited.getValueNode().nodeId) {
         case ARRAYNODE: {
             ArrayNode iVisited2 = (ArrayNode) iVisited.getValueNode();
             return multipleAsgnArrayNode(runtime, context, iVisited, iVisited2, self, aBlock);
         }
         case SPLATNODE: {
             SplatNode splatNode = (SplatNode)iVisited.getValueNode();
             RubyArray rubyArray = splatValue(runtime, evalInternal(runtime, context, splatNode.getValue(), self, aBlock));
             return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, rubyArray, false);
         }
         default:
             IRubyObject value = evalInternal(runtime, context, iVisited.getValueNode(), self, aBlock);
 
             if (!(value instanceof RubyArray)) {
                 value = RubyArray.newArray(runtime, value);
             }
             
             return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, (RubyArray)value, false);
         }
     }
 
     private static IRubyObject multipleAsgnArrayNode(Ruby runtime, ThreadContext context, MultipleAsgnNode iVisited, ArrayNode node, IRubyObject self, Block aBlock) {
         IRubyObject[] array = new IRubyObject[node.size()];
 
         for (int i = 0; i < node.size(); i++) {
             Node next = node.get(i);
 
             array[i] = evalInternal(runtime,context, next, self, aBlock);
         }
         return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, RubyArray.newArrayNoCopyLight(runtime, array), false);
     }
 
     private static IRubyObject nextNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         NextNode iVisited = (NextNode) node;
    
         context.pollThreadEvents();
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         // now used as an interpreter event
         throw new JumpException.NextJump(result);
     }
 
     private static IRubyObject nilNode(Ruby runtime, ThreadContext context) {
         return pollAndReturn(context, runtime.getNil());
     }
 
     private static IRubyObject notNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         NotNode iVisited = (NotNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock);
         return result.isTrue() ? runtime.getFalse() : runtime.getTrue();
     }
 
     private static IRubyObject nthRefNode(ThreadContext context, Node node) {
         return RubyRegexp.nth_match(((NthRefNode)node).getMatchNumber(), context.getCurrentFrame().getBackRef());
     }
     
     public static IRubyObject pollAndReturn(ThreadContext context, IRubyObject result) {
         context.pollThreadEvents();
         return result;
     }
 
     private static IRubyObject opAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OpAsgnNode iVisited = (OpAsgnNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = receiver.callMethod(context, iVisited.getVariableName());
    
         if (iVisited.getOperatorName() == "||") {
             if (value.isTrue()) {
                 return pollAndReturn(context, value);
             }
             value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else if (iVisited.getOperatorName() == "&&") {
             if (!value.isTrue()) {
                 return pollAndReturn(context, value);
             }
             value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else {
             value = iVisited.operatorCallAdapter.call(context, value, 
                     evalInternal(runtime, context, iVisited.getValueNode(), self, aBlock));
         }
    
         receiver.callMethod(context, iVisited.getVariableNameAsgn(), value);
    
         return pollAndReturn(context, value);
     }
 
     private static IRubyObject opAsgnOrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OpAsgnOrNode iVisited = (OpAsgnOrNode) node;
         String def = getDefinition(runtime, context, iVisited.getFirstNode(), self, aBlock);
    
         IRubyObject result = runtime.getNil();
         if (def != null) {
             result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
         }
         if (!result.isTrue()) {
             result = evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock);
         }
    
         return pollAndReturn(context, result);
     }
 
     private static IRubyObject opElementAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OpElementAsgnNode iVisited = (OpElementAsgnNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
    
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self, aBlock);
    
         IRubyObject firstValue = RuntimeHelpers.invoke(context, receiver, MethodIndex.AREF, "[]", args);
    
         if (iVisited.getOperatorName() == "||") {
             if (firstValue.isTrue()) {
                 return firstValue;
             }
             firstValue = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else if (iVisited.getOperatorName() == "&&") {
             if (!firstValue.isTrue()) {
                 return firstValue;
             }
             firstValue = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else {
             firstValue = iVisited.callAdapter.call(context, firstValue, 
                     evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock));
         }
    
         IRubyObject[] expandedArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, expandedArgs, 0, args.length);
         expandedArgs[expandedArgs.length - 1] = firstValue;
         RuntimeHelpers.invoke(context, receiver, MethodIndex.ASET, "[]=", expandedArgs);
         
         return firstValue;
     }
 
     private static IRubyObject orNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OrNode iVisited = (OrNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
    
         if (!result.isTrue()) {
             result = evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock);
         }
    
         return result;
     }
 
     private static IRubyObject postExeNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         PostExeNode iVisited = (PostExeNode) node;
         
         Block block = SharedScopeBlock.newInterpretedSharedScopeClosure(context, iVisited, context.getCurrentScope(), self);
         
         runtime.pushExitBlock(runtime.newProc(Block.Type.LAMBDA, block));
         
         return runtime.getNil();
     }
 
     private static IRubyObject preExeNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         PreExeNode iVisited = (PreExeNode) node;
         
         DynamicScope scope = DynamicScope.newDynamicScope(iVisited.getScope());
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
 
         // FIXME: I use a for block to implement END node because we need a proc which captures
         // its enclosing scope.   ForBlock now represents these node and should be renamed.
         Block block = InterpretedBlock.newInterpretedClosure(context, iVisited, self);
         
         block.yield(context, null);
         
         context.postScopedBody();
 
         return runtime.getNil();
     }
 
     private static IRubyObject redoNode(ThreadContext context, Node node) {
         context.pollThreadEvents();
    
         // now used as an interpreter event
         throw JumpException.REDO_JUMP;
     }
 
     private static IRubyObject regexpNode(Ruby runtime, Node node) {
         RegexpNode iVisited = (RegexpNode) node;
         RubyRegexp p = iVisited.getPattern();
         if(p == null) {
             p = RubyRegexp.newRegexp(runtime, iVisited.getValue(), iVisited.getOptions());
             iVisited.setPattern(p);
         }
 
         return p;
     }
 
     private static IRubyObject rescueNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         RescueNode iVisited = (RescueNode)node;
         RescuedBlock : while (true) {
             IRubyObject globalExceptionState = runtime.getGlobalVariables().get("$!");
             boolean anotherExceptionRaised = false;
             try {
                 // Execute rescue block
                 IRubyObject result = evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
 
                 // If no exception is thrown execute else block
                 if (iVisited.getElseNode() != null) {
                     if (iVisited.getRescueNode() == null) {
                         runtime.getWarnings().warn(iVisited.getElseNode().getPosition(), "else without rescue is useless");
                     }
                     result = evalInternal(runtime,context, iVisited.getElseNode(), self, aBlock);
                 }
 
                 return result;
             } catch (RaiseException raiseJump) {
                 RubyException raisedException = raiseJump.getException();
                 // TODO: Rubicon TestKernel dies without this line.  A cursory glance implies we
                 // falsely set $! to nil and this sets it back to something valid.  This should 
                 // get fixed at the same time we address bug #1296484.
                 runtime.getGlobalVariables().set("$!", raisedException);
 
                 RescueBodyNode rescueNode = iVisited.getRescueNode();
 
                 while (rescueNode != null) {
                     Node  exceptionNodes = rescueNode.getExceptionNodes();
                     ListNode exceptionNodesList;
                     
                     if (exceptionNodes instanceof SplatNode) {                    
                         exceptionNodesList = (ListNode) evalInternal(runtime,context, exceptionNodes, self, aBlock);
                     } else {
                         exceptionNodesList = (ListNode) exceptionNodes;
                     }
 
                     IRubyObject[] exceptions;
                     if (exceptionNodesList == null) {
                         exceptions = new IRubyObject[] {runtime.fastGetClass("StandardError")};
                     } else {
                         exceptions = setupArgs(runtime, context, exceptionNodes, self, aBlock);
                     }
                     if (RuntimeHelpers.isExceptionHandled(raisedException, exceptions, runtime, context, self).isTrue()) {
                         try {
                             return evalInternal(runtime,context, rescueNode, self, aBlock);
                         } catch (JumpException.RetryJump rj) {
                             // should be handled in the finally block below
                             //state.runtime.getGlobalVariables().set("$!", state.runtime.getNil());
                             //state.threadContext.setRaisedException(null);
                             continue RescuedBlock;
                         } catch (RaiseException je) {
                             anotherExceptionRaised = true;
                             throw je;
                         }
                     }
                     
                     rescueNode = rescueNode.getOptRescueNode();
                 }
 
                 // no takers; bubble up
                 throw raiseJump;
             } finally {
                 // clear exception when handled or retried
                 if (!anotherExceptionRaised)
                     runtime.getGlobalVariables().set("$!", globalExceptionState);
             }
         }
     }
 
     private static IRubyObject retryNode(ThreadContext context) {
         context.pollThreadEvents();
    
         throw JumpException.RETRY_JUMP;
     }
     
     private static IRubyObject returnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ReturnNode iVisited = (ReturnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         throw new JumpException.ReturnJump(context.getFrameJumpTarget(), result);
     }
 
     private static IRubyObject rootNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         RootNode iVisited = (RootNode) node;
         DynamicScope scope = iVisited.getScope();
         
         // Serialization killed our dynamic scope.  We can just create an empty one
         // since serialization cannot serialize an eval (which is the only thing
         // which is capable of having a non-empty dynamic scope).
         if (scope == null) {
             scope = DynamicScope.newDynamicScope(iVisited.getStaticScope());
         }
         
         StaticScope staticScope = scope.getStaticScope();
         
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
         
         if (staticScope.getModule() == null) {
             staticScope.setModule(runtime.getObject());
         }
 
         try {
             return evalInternal(runtime, context, iVisited.getBodyNode(), self, aBlock);
         } finally {
             context.postScopedBody();
         }
     }
 
     private static IRubyObject sClassNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         SClassNode iVisited = (SClassNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
 
         RubyClass singletonClass;
 
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError("no virtual class for " + receiver.getMetaClass().getBaseName());
         } else {
             if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                 throw runtime.newSecurityError("Insecure: can't extend object.");
             }
 
             singletonClass = receiver.getSingletonClass();
         }
 
         StaticScope scope = iVisited.getScope();
         scope.setModule(singletonClass);
         
         return evalClassDefinitionBody(runtime, context, scope, iVisited.getBodyNode(), singletonClass, self, aBlock);
     }
 
     private static IRubyObject splatNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return splatValue(runtime, evalInternal(runtime,context, ((SplatNode) node).getValue(), self, aBlock));
     }
 
     private static IRubyObject strNode(Ruby runtime, Node node) {
         return runtime.newStringShared((ByteList) ((StrNode) node).getValue());
     }
     
     private static IRubyObject superNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         SuperNode iVisited = (SuperNode) node;
    
         RubyModule klazz = context.getFrameKlazz();
         
         if (klazz == null) {
             String name = context.getFrameName();
             throw runtime.newNameError("Superclass method '" + name
                     + "' disabled.", name);
         }
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self, aBlock);
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
         
         // If no explicit block passed to super, then use the one passed in, unless it's explicitly cleared with nil
         if (iVisited.getIterNode() == null) {
             if (!block.isGiven()) block = aBlock;
         }
         
         return self.callSuper(context, args, block);
     }
     
     private static IRubyObject sValueNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return aValueSplat(runtime, evalInternal(runtime,context, ((SValueNode) node).getValue(), self, aBlock));
     }
     
     private static IRubyObject symbolNode(Ruby runtime, Node node) {
         return ((SymbolNode)node).getSymbol(runtime);
     }
     
     private static IRubyObject toAryNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return aryToAry(runtime, evalInternal(runtime,context, ((ToAryNode) node).getValue(), self, aBlock));
     }
 
     private static IRubyObject trueNode(Ruby runtime, ThreadContext context) {
         return pollAndReturn(context, runtime.getTrue());
     }
     
     private static IRubyObject undefNode(Ruby runtime, ThreadContext context, Node node) {
         UndefNode iVisited = (UndefNode) node;
         RubyModule module = context.getRubyClass();
    
         if (module == null) {
             throw runtime.newTypeError("No class to undef method '" + iVisited.getName() + "'.");
         }
         
         module.undef(iVisited.getName());
    
         return runtime.getNil();
     }
 
     private static IRubyObject untilNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         UntilNode iVisited = (UntilNode) node;
    
         IRubyObject result = null;
         boolean firstTest = iVisited.evaluateAtStart();
         
         outerLoop: while (!firstTest || !(evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock)).isTrue()) {
             firstTest = true;
             loop: while (true) { // Used for the 'redo' command
                 try {
                     evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break loop;
                 } catch (JumpException.RedoJump rj) {
                     continue;
                 } catch (JumpException.NextJump nj) {
                     break loop;
                 } catch (JumpException.BreakJump bj) {
                     // JRUBY-530 until case
                     if (bj.getTarget() == aBlock.getBody()) {
                          bj.setTarget(null);
 
                          throw bj;
                     }
 
                     result = (IRubyObject) bj.getValue();
 
                     break outerLoop;
                 }
             }
         }
 
         if (result == null) {
             result = runtime.getNil();
         }
         return pollAndReturn(context, result);
     }
 
     private static IRubyObject valiasNode(Ruby runtime, Node node) {
         VAliasNode iVisited = (VAliasNode) node;
         runtime.getGlobalVariables().alias(iVisited.getNewName(), iVisited.getOldName());
    
         return runtime.getNil();
     }
 
     private static IRubyObject vcallNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         VCallNode iVisited = (VCallNode) node;
 
         return iVisited.callAdapter.call(context, self);
     }
 
     private static IRubyObject whileNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         WhileNode iVisited = (WhileNode) node;
    
         IRubyObject result = null;
         boolean firstTest = iVisited.evaluateAtStart();
         
         outerLoop: while (!firstTest || evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock).isTrue()) {
             firstTest = true;
             loop: while (true) { // Used for the 'redo' command
                 try {
                     evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break loop;
                 } catch (RaiseException re) {
                     if (runtime.fastGetClass("LocalJumpError").isInstance(re.getException())) {
                         RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
                         
                         IRubyObject reason = jumpError.reason();
                         
                         // admittedly inefficient
                         if (reason.asJavaString().equals("break")) {
                             return jumpError.exit_value();
                         } else if (reason.asJavaString().equals("next")) {
                             break loop;
                         } else if (reason.asJavaString().equals("redo")) {
                             continue;
                         }
                     }
                     
                     throw re;
                 } catch (JumpException.RedoJump rj) {
                     continue;
                 } catch (JumpException.NextJump nj) {
                     break loop;
                 } catch (JumpException.BreakJump bj) {
                     // JRUBY-530, while case
                     if (bj.getTarget() == aBlock.getBody()) {
                         bj.setTarget(null);
 
                         throw bj;
                     }
 
                     result = (IRubyObject) bj.getValue();
                     break outerLoop;
                 }
             }
         }
         if (result == null) {
             result = runtime.getNil();
         }
         return pollAndReturn(context, result);
     }
 
     private static IRubyObject xStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         return self.callMethod(context, "`", runtime.newStringShared((ByteList) ((XStrNode) node).getValue()));
     }
 
     private static IRubyObject yieldNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         YieldNode iVisited = (YieldNode) node;
    
         IRubyObject result = null;
         if (iVisited.getArgsNode() != null) {
             result = evalInternal(runtime, context, iVisited.getArgsNode(), self, aBlock);
         }
 
         Block block = context.getCurrentFrame().getBlock();
 
         return block.yield(context, result, null, null, iVisited.getCheckState());
     }
 
     private static IRubyObject zArrayNode(Ruby runtime) {
         return runtime.newArray();
     }
     
     private static IRubyObject zsuperNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Block block = getBlock(runtime, context, self, aBlock, ((ZSuperNode) node).getIterNode());
         return RuntimeHelpers.callZSuper(runtime, context, block, self);
     }
 
     public static IRubyObject aValueSplat(Ruby runtime, IRubyObject value) {
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return runtime.getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first(IRubyObject.NULL_ARRAY) : array;
     }
 
     private static void callTraceFunction(Ruby runtime, ThreadContext context, int event) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         runtime.callEventHooks(context, event, context.getFile(), context.getLine(), name, type);
     }
 
     /** Evaluates the body in a class or module definition statement.
      *
      */
     private static IRubyObject evalClassDefinitionBody(Ruby runtime, ThreadContext context, StaticScope scope, 
             Node bodyNode, RubyModule type, IRubyObject self, Block block) {
         context.preClassEval(scope, type);
 
         try {
             if (isTrace(runtime)) {
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index 231b28c9fc..c994266873 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -1,806 +1,806 @@
 package org.jruby.javasupport.util;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyHash;
 import org.jruby.RubyKernel;
 import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CompiledBlock;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledBlockLight;
 import org.jruby.runtime.CompiledSharedScopeBlock;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.TypeConverter;
 
 /**
  * Helper methods which are called by the compiler.  Note: These will show no consumers, but
  * generated code does call these so don't remove them thinking they are dead code. 
  *
  */
 public class RuntimeHelpers {
     public static Block createBlock(ThreadContext context, IRubyObject self, int arity, 
             String[] staticScopeNames, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argsNodeType, boolean light) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         if (light) {
             return CompiledBlockLight.newCompiledClosureLight(
                     context,
                     self,
                     Arity.createArity(arity),
                     staticScope,
                     callback,
                     hasMultipleArgsHead,
                     argsNodeType);
         } else {
             return CompiledBlock.newCompiledClosure(
                     context,
                     self,
                     Arity.createArity(arity),
                     staticScope,
                     callback,
                     hasMultipleArgsHead,
                     argsNodeType);
         }
     }
     
     public static IRubyObject runBeginBlock(ThreadContext context, IRubyObject self, String[] staticScopeNames, CompiledBlockCallback callback) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         context.preScopedBody(DynamicScope.newDynamicScope(staticScope, context.getCurrentScope()));
         
         Block block = CompiledBlock.newCompiledClosure(context, self, Arity.createArity(0), staticScope, callback, false, Block.ZERO_ARGS);
         
         block.yield(context, null);
         
         context.postScopedBody();
         
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
         
         if (containingClass == null) {
             throw runtime.newTypeError("No class to add method.");
         }
         
         if (containingClass == runtime.getObject() && name == "initialize") {
             runtime.getWarnings().warn("redefining Object#initialize may cause infinite loop");
         }
 
         if (name == "__id__" || name == "__send__") {
             runtime.getWarnings().warn("redefining `" + name + "' may cause serious problem"); 
         }
 
         StaticScope scope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), scopeNames);
         scope.determineModule();
         scope.setArities(required, optional, rest);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method;
         
         if (name == "initialize" || visibility == Visibility.MODULE_FUNCTION) {
             method = factory.getCompiledMethod(containingClass, javaName, 
                     Arity.createArity(arity), Visibility.PRIVATE, scope, scriptObject, callConfig);
         } else {
             method = factory.getCompiledMethod(containingClass, javaName, 
                     Arity.createArity(arity), visibility, scope, scriptObject, callConfig);
         }
         
         containingClass.addMethod(name, method);
         
         if (visibility == Visibility.MODULE_FUNCTION) {
             containingClass.getSingletonClass().addMethod(name,
                     new WrapperMethod(containingClass.getSingletonClass(), method,
                     Visibility.PUBLIC));
             containingClass.callMethod(context, "singleton_method_added", runtime.fastNewSymbol(name));
         }
         
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             ((MetaClass) containingClass).getAttached().callMethod(
                     context, "singleton_method_added", runtime.fastNewSymbol(name));
         } else {
             containingClass.callMethod(context, "method_added", runtime.fastNewSymbol(name));
         }
         
         return runtime.getNil();
     }
     
     public static IRubyObject defs(ThreadContext context, IRubyObject self, IRubyObject receiver, Object scriptObject, String name, String javaName, String[] scopeNames,
             int arity, int required, int optional, int rest, CallConfiguration callConfig) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
         
         if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
             throw runtime.newSecurityError("Insecure; can't define singleton method.");
         }
 
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
           throw runtime.newTypeError("can't define singleton method \"" + name
           + "\" for " + receiver.getMetaClass().getBaseName());
         }
 
         if (receiver.isFrozen()) throw runtime.newFrozenError("object");
 
         RubyClass rubyClass = receiver.getSingletonClass();
 
         if (runtime.getSafeLevel() >= 4 && rubyClass.getMethods().get(name) != null) {
             throw runtime.newSecurityError("redefining method prohibited.");
         }
         
         StaticScope scope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), scopeNames);
         scope.determineModule();
         scope.setArities(required, optional, rest);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method;
         
         method = factory.getCompiledMethod(rubyClass, javaName, 
                 Arity.createArity(arity), Visibility.PUBLIC, scope, scriptObject, callConfig);
         
         rubyClass.addMethod(name, method);
         receiver.callMethod(context, "singleton_method_added", runtime.fastNewSymbol(name));
         
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
 
     public static IRubyObject doAttrAssign(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, String name, IRubyObject caller, CallType callType, Block block) {
         if (receiver == caller) callType = CallType.VARIABLE;
         
         return compilerCallMethod(context, receiver, name, args, caller, callType, block);
     }
     
     public static IRubyObject doAttrAssignIndexed(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, byte methodIndex, String name, IRubyObject caller, 
             CallType callType, Block block) {
         if (receiver == caller) callType = CallType.VARIABLE;
         
         return compilerCallMethodWithIndex(context, receiver, methodIndex, name, args, caller, 
                 callType, block);
     }
     
     public static IRubyObject doInvokeDynamic(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, String name, IRubyObject caller, CallType callType, Block block) {
         return compilerCallMethod(context, receiver, name, args, caller, callType, block);
     }
     
     public static IRubyObject doInvokeDynamicIndexed(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, byte methodIndex, String name, IRubyObject caller, 
             CallType callType, Block block) {
         return compilerCallMethodWithIndex(context, receiver, methodIndex, name, args, caller, 
                 callType, block);
     }
 
     /**
      * Used by the compiler to ease calling indexed methods, also to handle visibility.
      * NOTE: THIS IS NOT THE SAME AS THE SWITCHVALUE VERSIONS.
      */
     public static IRubyObject compilerCallMethodWithIndex(ThreadContext context, IRubyObject receiver, int methodIndex, String name, IRubyObject[] args, IRubyObject caller, CallType callType, Block block) {
         RubyClass clazz = receiver.getMetaClass();
         
         if (clazz.index != 0) {
             return clazz.invoke(context, receiver, methodIndex, name, args, callType, block);
         }
         
         return compilerCallMethod(context, receiver, name, args, caller, callType, block);
     }
     
     /**
      * Used by the compiler to handle visibility
      */
     public static IRubyObject compilerCallMethod(ThreadContext context, IRubyObject receiver, String name,
             IRubyObject[] args, IRubyObject caller, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         RubyModule rubyclass = receiver.getMetaClass();
         method = rubyclass.searchMethod(name);
         
         if (method.isUndefined() || (!name.equals("method_missing") && !method.isCallableFrom(caller, callType))) {
             return callMethodMissing(context, receiver, method, name, args, caller, callType, block);
         }
 
         return method.call(context, receiver, rubyclass, name, args, block);
     }
     
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, int methodIndex,
                                                 IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         // store call information so method_missing impl can use it            
         context.setLastCallStatus(callType);            
         context.setLastVisibility(method.getVisibility());
 
         if (methodIndex == MethodIndex.METHOD_MISSING) {
             return RubyKernel.method_missing(self, args, block);
         }
 
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 1, args.length);
         newArgs[0] = context.getRuntime().newSymbol(name);
 
         return receiver.callMethod(context, "method_missing", newArgs, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         // store call information so method_missing impl can use it            
         context.setLastCallStatus(callType);            
         context.setLastVisibility(method.getVisibility());
 
         if (name.equals("method_missing")) {
             return RubyKernel.method_missing(self, args, block);
         }
 
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 1, args.length);
         newArgs[0] = context.getRuntime().newSymbol(name);
 
         return receiver.callMethod(context, "method_missing", newArgs, block);
     }
 
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name) {
         return RuntimeHelpers.invoke(context, self, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, self, name, new IRubyObject[] { arg }, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args) {
         return RuntimeHelpers.invoke(context, self, name, args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args, Block block) {
         return RuntimeHelpers.invoke(context, self, name, args, CallType.FUNCTIONAL, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, int methodIndex, String name) {
         return RuntimeHelpers.invoke(context, self, methodIndex, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, int methodIndex, String name, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, self, methodIndex,name,new IRubyObject[]{arg},CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, int methodIndex, String name, IRubyObject[] args) {
         return RuntimeHelpers.invoke(context, self, methodIndex,name,args,CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, int methodIndex, String name, IRubyObject[] args, CallType callType, Block block) {
         return self.getMetaClass().invoke(context, self, methodIndex, name, args, callType, block);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args, CallType callType, Block block) {
         return self.getMetaClass().invoke(context, self, name, args, callType, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject[] args, CallType callType, Block block) {
         return asClass.invoke(context, self, name, args, callType, block);
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
         if (rubyModule == null || rubyModule.isNil()) {
             rubyModule = context.getCurrentScope().getStaticScope().getModule();
             
             if (rubyModule == null) {
                 throw context.getRuntime().newTypeError("no outer class/module");
             }
         }
         
         return (RubyModule)rubyModule;
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
         if (runtime.fastGetClass("LocalJumpError").isInstance(exception)) {
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
         
         RubyProc blockArg;
         
         if (block.getProcObject() != null) {
             blockArg = block.getProcObject();
         } else {
             blockArg = runtime.newProc(Block.Type.PROC, block);
             blockArg.getBlock().type = Block.Type.PROC;
         }
         
         return blockArg;
     }
     
     public static Block getBlockFromBlockPassBody(IRubyObject proc, Block currentBlock) {
         Ruby runtime = proc.getRuntime();
 
         // No block from a nil proc
         if (proc.isNil()) return Block.NULL_BLOCK;
 
         // If not already a proc then we should try and make it one.
         if (!(proc instanceof RubyProc)) {
             proc = TypeConverter.convertToType(proc, runtime.getProc(), 0, "to_proc", false);
 
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
         if (bj.getTarget() == aBlock.getBody()) {
             bj.setTarget(null);
             
             throw bj;
         }
 
         return (IRubyObject) bj.getValue();
     }
     
     public static IRubyObject breakJump(IRubyObject value) {
         throw new JumpException.BreakJump(null, value);
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
         RubyArray expressions = ASTInterpreter.splatValue(context.getRuntime(), expressionsObject);
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
-        return context.setConstantInModule(name, (RubyModule)module, value);
+        return context.setConstantInModule(name, module, value);
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
         
         runtime.getWarnings().warning("instance variable " + name + " not initialized");
         
         return runtime.getNil();
     }
     
     public static IRubyObject fastGetInstanceVariable(Ruby runtime, IRubyObject self, String internedName) {
         IRubyObject result;
         if ((result = self.getInstanceVariables().fastGetInstanceVariable(internedName)) != null) return result;
         
         runtime.getWarnings().warning("instance variable " + internedName + " not initialized");
         
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
             return regexp.op_match(value);
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
 }
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index dc0eb92e9c..cdfdda42fc 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,885 +1,889 @@
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
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
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
 
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyKernel.CatchTarget;
 import org.jruby.RubyModule;
 import org.jruby.RubyThread;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.libraries.FiberLibrary.Fiber;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * @author jpetersen
  */
 public final class ThreadContext {
     public static synchronized ThreadContext newContext(Ruby runtime) {
         ThreadContext context = new ThreadContext(runtime);
         //        if(runtime.getInstanceConfig().isSamplingEnabled()) {
         //    org.jruby.util.SimpleSampler.registerThreadContext(context);
         //}
 
         return context;
     }
     
     private final static int INITIAL_SIZE = 50;
     
     private final Ruby runtime;
     
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
     
     private RubyThread thread;
     private Fiber fiber;
     
     // Error info is per-thread
     private IRubyObject errorInfo;
     
     //private UnsynchronizedStack parentStack;
     private RubyModule[] parentStack = new RubyModule[INITIAL_SIZE];
     private int parentIndex = -1;
     
     //private UnsynchronizedStack frameStack;
     private Frame[] frameStack = new Frame[INITIAL_SIZE];
     private int frameIndex = -1;
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
     
     private CatchTarget[] catchStack = new CatchTarget[INITIAL_SIZE];
     private int catchIndex = -1;
     
     // File where current executing unit is being evaluated
     private String file = "";
     
     // Line where current executing unit is being evaluated
     private int line = 0;
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         
         // init errorInfo to nil
         errorInfo = runtime.getNil();
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         StaticScope topStaticScope = new LocalStaticScope(null);
         pushScope(new ManyVarsDynamicScope(topStaticScope, null));
             
         for (int i = 0; i < frameStack.length; i++) {
             frameStack[i] = new Frame();
         }
     }
     
     CallType lastCallType;
     
     Visibility lastVisibility;
     
     IRubyObject lastExitStatus;
     
     public Ruby getRuntime() {
         return runtime;
     }
     
     public IRubyObject getErrorInfo() {
         return errorInfo;
     }
     
     public IRubyObject setErrorInfo(IRubyObject errorInfo) {
         this.errorInfo = errorInfo;
         return errorInfo;
     }
     
     /**
      * Returns the lastCallStatus.
      * @return LastCallStatus
      */
     public void setLastCallStatus(CallType callType) {
         lastCallType = callType;
     }
 
     public CallType getLastCallType() {
         return lastCallType;
     }
 
     public void setLastVisibility(Visibility visibility) {
         lastVisibility = visibility;
     }
 
     public Visibility getLastVisibility() {
         return lastVisibility;
     }
     
     public IRubyObject getLastExitStatus() {
         return lastExitStatus;
     }
     
     public void setLastExitStatus(IRubyObject lastExitStatus) {
         this.lastExitStatus = lastExitStatus;
     }
 
     public void printScope() {
         System.out.println("SCOPE STACK:");
         for (int i = 0; i <= scopeIndex; i++) {
             System.out.println(scopeStack[i]);
         }
     }
 
     public DynamicScope getCurrentScope() {
         return scopeStack[scopeIndex];
     }
     
     public DynamicScope getPreviousScope() {
         return scopeStack[scopeIndex - 1];
     }
     
     private void expandFramesIfNecessary(int newMax) {
         if (newMax == frameStack.length) {
             int newSize = frameStack.length * 2;
             Frame[] newFrameStack = new Frame[newSize];
             
             System.arraycopy(frameStack, 0, newFrameStack, 0, frameStack.length);
             
             for (int i = frameStack.length; i < newSize; i++) {
                 newFrameStack[i] = new Frame();
             }
             
             frameStack = newFrameStack;
         }
     }
     
     private void expandParentsIfNecessary() {
         if (parentIndex + 1 == parentStack.length) {
             int newSize = parentStack.length * 2;
             RubyModule[] newParentStack = new RubyModule[newSize];
             
             System.arraycopy(parentStack, 0, newParentStack, 0, parentStack.length);
             
             parentStack = newParentStack;
         }
     }
     
     public void pushScope(DynamicScope scope) {
         scopeStack[++scopeIndex] = scope;
         expandScopesIfNecessary();
     }
     
     public void popScope() {
         scopeStack[scopeIndex--] = null;
     }
     
     private void expandScopesIfNecessary() {
         if (scopeIndex + 1 == scopeStack.length) {
             int newSize = scopeStack.length * 2;
             DynamicScope[] newScopeStack = new DynamicScope[newSize];
             
             System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
             
             scopeStack = newScopeStack;
         }
     }
     
     public RubyThread getThread() {
         return thread;
     }
     
     public void setThread(RubyThread thread) {
         this.thread = thread;
     }
     
     public Fiber getFiber() {
         return fiber;
     }
     
     public void setFiber(Fiber fiber) {
         this.fiber = fiber;
     }
     
 //    public IRubyObject getLastline() {
 //        IRubyObject value = getCurrentScope().getLastLine();
 //        
 //        // DynamicScope does not preinitialize these values since they are virtually never used.
 //        return value == null ? runtime.getNil() : value;
 //    }
 //    
 //    public void setLastline(IRubyObject value) {
 //        getCurrentScope().setLastLine(value);
 //    }
     
     //////////////////// CATCH MANAGEMENT ////////////////////////
     private void expandCatchIfNecessary() {
         if (catchIndex + 1 == catchStack.length) {
             int newSize = catchStack.length * 2;
             CatchTarget[] newCatchStack = new CatchTarget[newSize];
             
             System.arraycopy(catchStack, 0, newCatchStack, 0, catchStack.length);
             catchStack = newCatchStack;
         }
     }
     
     public void pushCatch(CatchTarget catchTarget) {
         catchStack[++catchIndex] = catchTarget;
         expandCatchIfNecessary();
     }
     
     public void popCatch() {
         catchIndex--;
     }
     
     public CatchTarget[] getActiveCatches() {
         if (catchIndex < 0) return new CatchTarget[0];
         
         CatchTarget[] activeCatches = new CatchTarget[catchIndex + 1];
         System.arraycopy(catchStack, 0, activeCatches, 0, catchIndex + 1);
         return activeCatches;
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         Frame currentFrame = getCurrentFrame();
         frameStack[++frameIndex].updateFrame(currentFrame);
         expandFramesIfNecessary(frameIndex + 1);
     }
     
     private void pushFrame(Frame frame) {
         frameStack[++frameIndex] = frame;
         expandFramesIfNecessary(frameIndex + 1);
     }
     
     private void pushCallFrame(RubyModule clazz, String name, 
                                IRubyObject self, Block block, JumpTarget jumpTarget) {
         pushFrame(clazz, name, self, block, jumpTarget);        
     }
     
     private void pushBacktraceFrame(String name) {
         pushFrame(name);        
     }
 
     private void pushFrame(String name) {
         frameStack[++frameIndex].updateFrame(name, file, line);
         expandFramesIfNecessary(frameIndex + 1);
     }
 
     private void pushFrame(RubyModule clazz, String name, 
                                IRubyObject self, Block block, JumpTarget jumpTarget) {
         frameStack[++frameIndex].updateFrame(clazz, self, name, block, file, line, jumpTarget);
         expandFramesIfNecessary(frameIndex + 1);
     }
     
     private void pushFrame() {
         frameStack[++frameIndex].updateFrame(file, line);
         expandFramesIfNecessary(frameIndex + 1);
     }
     
     private void popFrame() {
         Frame frame = frameStack[frameIndex];
         frameIndex--;
         setFile(frame.getFile());
         setLine(frame.getLine());
     }
         
     private void popFrameReal() {
         Frame frame = frameStack[frameIndex];
         frameStack[frameIndex] = new Frame();
         frameIndex--;
         setFile(frame.getFile());
         setLine(frame.getLine());
     }
     
     public Frame getCurrentFrame() {
         return frameStack[frameIndex];
     }
     
     public Frame getNextFrame() {
         expandFramesIfNecessary(frameIndex + 1);
         return frameStack[frameIndex + 1];
     }
     
     public Frame getPreviousFrame() {
         int size = frameIndex + 1;
         return size <= 1 ? null : frameStack[size - 2];
     }
     
     public int getFrameCount() {
         return frameIndex + 1;
     }
     
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public JumpTarget getFrameJumpTarget() {
         return getCurrentFrame().getJumpTarget();
     }
     
     public void setFrameJumpTarget(JumpTarget target) {
         getCurrentFrame().setJumpTarget(target);
     }
     
     public RubyModule getFrameKlazz() {
         return getCurrentFrame().getKlazz();
     }
     
     public Block getFrameBlock() {
         return getCurrentFrame().getBlock();
     }
     
     public String getFile() {
         return file;
     }
     
     public int getLine() {
         return line;
     }
     
     public void setFile(String file) {
         this.file = file;
     }
     
     public void setLine(int line) {
         this.line = line;
     }
     
     public Visibility getCurrentVisibility() {
         return getCurrentFrame().getVisibility();
     }
     
     public Visibility getPreviousVisibility() {
         return getPreviousFrame().getVisibility();
     }
     
     public void setCurrentVisibility(Visibility visibility) {
         getCurrentFrame().setVisibility(visibility);
     }
     
     public void pollThreadEvents() {
         getThread().pollThreadEvents();
     }
     
     public void pushRubyClass(RubyModule currentModule) {
         // FIXME: this seems like a good assertion, but it breaks compiled code and the code seems
         // to run without it...
         //assert currentModule != null : "Can't push null RubyClass";
         
         parentStack[++parentIndex] = currentModule;
         expandParentsIfNecessary();
     }
     
     public RubyModule popRubyClass() {
         RubyModule ret = parentStack[parentIndex];
         parentStack[parentIndex--] = null;
         return ret;
     }
     
     public RubyModule getRubyClass() {
         assert !(parentIndex == -1) : "Trying to get RubyClass from empty stack";
         
         RubyModule parentModule = parentStack[parentIndex];
         
         return parentModule.getNonIncludedClass();
     }
     
     public RubyModule getBindingRubyClass() {
         RubyModule parentModule = null;
         if(parentIndex == 0) {
             parentModule = parentStack[parentIndex];
         } else {
             parentModule = parentStack[parentIndex-1];
             
         }
         return parentModule.getNonIncludedClass();
     }
     
     public boolean getConstantDefined(String internedName) {
         IRubyObject result;
         IRubyObject undef = runtime.getUndef();
         
         // flipped from while to do to search current class first
         for (StaticScope scope = getCurrentScope().getStaticScope(); scope != null; scope = scope.getPreviousCRefScope()) {
             RubyModule module = scope.getModule();
             if ((result = module.fastFetchConstant(internedName)) != null) {
                 if (result != undef) return true;
                 return runtime.getLoadService().autoloadFor(module.getName() + "::" + internedName) != null;
             }
         }
         
         return getCurrentScope().getStaticScope().getModule().fastIsConstantDefined(internedName);
     }
     
     /**
      * Used by the evaluator and the compiler to look up a constant by name
      */
     public IRubyObject getConstant(String internedName) {
         StaticScope scope = getCurrentScope().getStaticScope();
         RubyClass object = runtime.getObject();
         IRubyObject undef = runtime.getUndef();
         IRubyObject result;
         
         // flipped from while to do to search current class first
         do {
             RubyModule klass = scope.getModule();
             
             // Not sure how this can happen
             //if (NIL_P(klass)) return rb_const_get(CLASS_OF(self), id);
             if ((result = klass.fastFetchConstant(internedName)) != null) {
                 if (result != undef) {
                     return result;
                 }
                 klass.deleteConstant(internedName);
                 if (runtime.getLoadService().autoload(klass.getName() + "::" + internedName) == null) break;
                 continue;
             }
             scope = scope.getPreviousCRefScope();
         } while (scope != null && scope.getModule() != object);
         
         return getCurrentScope().getStaticScope().getModule().fastGetConstant(internedName);
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a null const decl
      */
     public IRubyObject setConstantInCurrent(String internedName, IRubyObject result) {
         RubyModule module;
 
         if ((module = getCurrentScope().getStaticScope().getModule()) != null) {
             module.fastSetConstant(internedName, result);
             return result;
         }
 
         // TODO: wire into new exception handling mechanism
         throw runtime.newTypeError("no class/module to define constant");
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name.
      * This is for a Colon2 const decl
      */
-    public IRubyObject setConstantInModule(String internedName, RubyModule module, IRubyObject result) {
+    public IRubyObject setConstantInModule(String internedName, IRubyObject target, IRubyObject result) {
+        if (!(target instanceof RubyModule)) {
+            throw runtime.newTypeError(target.toString() + " is not a class/module");
+        }
+        RubyModule module = (RubyModule)target;
         module.fastSetConstant(internedName, result);
         
         return result;
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInObject(String internedName, IRubyObject result) {
         runtime.getObject().fastSetConstant(internedName, result);
         
         return result;
     }
     
     private static void addBackTraceElement(RubyArray backtrace, Frame frame, Frame previousFrame) {
         if (frame.getName() != null && 
                 frame.getName().equals(previousFrame.getName()) &&
                 frame.getFile().equals(previousFrame.getFile()) &&
                 frame.getLine() == previousFrame.getLine()) {
             return;
         }
         
         StringBuffer buf = new StringBuffer(60);
         buf.append(frame.getFile()).append(':').append(frame.getLine() + 1);
         
         if (previousFrame.getName() != null) {
             buf.append(":in `").append(previousFrame.getName()).append('\'');
         }
         
         backtrace.append(backtrace.getRuntime().newString(buf.toString()));
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, Frame[] backtraceFrames) {
         RubyArray backtrace = runtime.newArray();
         
         if (backtraceFrames == null || backtraceFrames.length <= 0) return backtrace;
         
         int traceSize = backtraceFrames.length;
 
         for (int i = traceSize - 1; i > 0; i--) {
             Frame frame = backtraceFrames[i];
             // We are in eval with binding break out early
             if (frame.isBindingFrame()) break;
 
             addBackTraceElement(backtrace, frame, backtraceFrames[i - 1]);
         }
         
         return backtrace;
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public Frame[] createBacktrace(int level, boolean nativeException) {
         int traceSize = frameIndex - level + 1;
         Frame[] traceFrames;
         
         if (traceSize <= 0) return null;
         
         if (nativeException) {
             // assert level == 0;
             traceFrames = new Frame[traceSize + 1];
             traceFrames[traceSize] = frameStack[frameIndex];
         } else {
             traceFrames = new Frame[traceSize];
         }
         
         System.arraycopy(frameStack, 0, traceFrames, 0, traceSize);
         
         return traceFrames;
     }
     
     public void preAdoptThread() {
         pushFrame();
         pushRubyClass(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
     
     public void preCompiledClass(RubyModule type, String[] scopeNames) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         StaticScope staticScope = new LocalStaticScope(getCurrentScope().getStaticScope(), scopeNames);
         staticScope.setModule(type);
         pushScope(new ManyVarsDynamicScope(staticScope, null));
     }
     
     public void postCompiledClass() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preScopeNode(StaticScope staticScope) {
         pushScope(DynamicScope.newDynamicScope(staticScope, getCurrentScope()));
     }
 
     public void postScopeNode() {
         popScope();
     }
 
     public void preClassEval(StaticScope staticScope, RubyModule type) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         pushScope(DynamicScope.newDynamicScope(staticScope, null));
     }
     
     public void postClassEval() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preBsfApply(String[] names) {
         // FIXME: I think we need these pushed somewhere?
         LocalStaticScope staticScope = new LocalStaticScope(null);
         staticScope.setVariables(names);
         pushFrame();
     }
     
     public void postBsfApply() {
         popFrame();
     }
     
     public void preMethodFrameAndScope(RubyModule clazz, String name, IRubyObject self, IRubyObject[] args, int req, Block block, 
             StaticScope staticScope, JumpTarget jumpTarget) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block, jumpTarget);
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void postMethodFrameAndScope() {
         popRubyClass();
         popScope();
         popFrame();
     }
     
     public void preMethodFrameOnly(RubyModule clazz, String name, IRubyObject self, IRubyObject[] args, int req, Block block,
             JumpTarget jumpTarget) {
         pushRubyClass(clazz);
         pushCallFrame(clazz, name, self, block, jumpTarget);
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postMethodFrameOnly() {
         popFrame();
         popRubyClass();
     }
     
     public void preMethodScopeOnly(RubyModule clazz, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void postMethodScopeOnly() {
         popRubyClass();
         popScope();
     }
     
     public void preMethodBacktraceAndScope(String name, RubyModule clazz, StaticScope staticScope) {
         preMethodScopeOnly(clazz, staticScope);
         pushBacktraceFrame(name);
     }
     
     public void postMethodBacktraceAndScope() {
         postMethodScopeOnly();
         popFrame();
     }
     
     public void preMethodBacktraceOnly(String name) {
         pushBacktraceFrame(name);
     }
     
     public void postMethodBacktraceOnly() {
         popFrame();
     }
     
     public void prepareTopLevel(RubyClass objectClass, IRubyObject topSelf) {
         pushFrame();
         setCurrentVisibility(Visibility.PRIVATE);
         
         pushRubyClass(objectClass);
         
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
         
         getCurrentScope().getStaticScope().setModule(objectClass);
     }
     
     public void preNodeEval(RubyModule rubyClass, IRubyObject self) {
         pushRubyClass(rubyClass);
         pushCallFrame(null, null, self, Block.NULL_BLOCK, null);
         // set visibility to private, since toplevel of scripts always started out private
         setCurrentVisibility(Visibility.PRIVATE);
     }
     
     public void postNodeEval() {
         popFrame();
         popRubyClass();
     }
     
     // XXX: Again, screwy evaling under previous frame's scope
     public void preExecuteUnder(RubyModule executeUnderClass, Block block) {
         Frame frame = getCurrentFrame();
         
         pushRubyClass(executeUnderClass);
         DynamicScope scope = getCurrentScope();
         StaticScope sScope = new BlockStaticScope(scope.getStaticScope());
         sScope.setModule(executeUnderClass);
         pushScope(DynamicScope.newDynamicScope(sScope, scope));
         pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), block, frame.getJumpTarget());
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postExecuteUnder() {
         popFrame();
         popScope();
         popRubyClass();
     }
     
     public void preMproc() {
         pushFrame();
     }
     
     public void postMproc() {
         popFrame();
     }
     
     public void preRunThread(Frame currentFrame) {
         pushFrame(currentFrame);
     }
     
     public void preTrace() {
         setWithinTrace(true);
         pushFrame();
     }
     
     public void postTrace() {
         popFrame();
         setWithinTrace(false);
     }
     
     public void preForBlock(Binding binding, RubyModule klass) {
         pushFrame(binding.getFrame());
         getCurrentFrame().setVisibility(binding.getVisibility());
         pushScope(binding.getDynamicScope());
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preYieldSpecificBlock(Binding binding, StaticScope scope, RubyModule klass) {
         pushFrame(binding.getFrame());
         getCurrentFrame().setVisibility(binding.getVisibility());
         // new scope for this invocation of the block, based on parent scope
         pushScope(DynamicScope.newDynamicScope(scope, binding.getDynamicScope()));
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preYieldLightBlock(Binding binding, DynamicScope emptyScope, RubyModule klass) {
         pushFrame(binding.getFrame());
         getCurrentFrame().setVisibility(binding.getVisibility());
         // just push the same empty scope, since we won't use one
         pushScope(emptyScope);
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preYieldNoScope(Binding binding, RubyModule klass) {
         pushFrame(binding.getFrame());
         getCurrentFrame().setVisibility(binding.getVisibility());
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preEvalWithBinding(Binding binding) {
         Frame frame = binding.getFrame();
         frame.setIsBindingFrame(true);
         pushFrame(frame);
         getCurrentFrame().setVisibility(binding.getVisibility());
         pushRubyClass(binding.getKlass());
     }
     
     public void postEvalWithBinding(Binding binding) {
         binding.getFrame().setIsBindingFrame(false);
         popFrameReal();
         popRubyClass();
     }
     
     public void postYield(Binding binding) {
         popScope();
         popFrameReal();
         popRubyClass();
     }
     
     public void postYieldLight(Binding binding) {
         popScope();
         popFrameReal();
         popRubyClass();
     }
     
     public void postYieldNoScope() {
         popFrameReal();
         popRubyClass();
     }
     
     public void preScopedBody(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postScopedBody() {
         popScope();
     }
     
     /**
      * Is this thread actively tracing at this moment.
      *
      * @return true if so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public boolean isWithinTrace() {
         return isWithinTrace;
     }
     
     /**
      * Set whether we are actively tracing or not on this thread.
      *
      * @param isWithinTrace true is so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public void setWithinTrace(boolean isWithinTrace) {
         this.isWithinTrace = isWithinTrace;
     }
     
     /**
      * Is this thread actively in defined? at the moment.
      *
      * @return true if within defined?
      */
     public boolean isWithinDefined() {
         return isWithinDefined;
     }
     
     /**
      * Set whether we are actively within defined? or not.
      *
      * @param isWithinDefined true if so
      */
     public void setWithinDefined(boolean isWithinDefined) {
         this.isWithinDefined = isWithinDefined;
     }
 }
diff --git a/test/testConstant.rb b/test/testConstant.rb
index 016164de26..2771d0e64d 100644
--- a/test/testConstant.rb
+++ b/test/testConstant.rb
@@ -1,223 +1,229 @@
 require 'test/minirunit'
 test_check "Test Constant Scoping:"
 
 C1 = 1
 
 module A
   C2 = 2
 
   module B
     class C
       C3 = 3
 
       def foo
         "ABC"
       end
 
       i1 = ""
       
       class << i1
         test_equal(3, C3)
       end
       
       i2 = Class.new
       
       class << i2
         test_equal(3, C3)
       end
     end
   end
 
   class << self
     test_equal(1, C1)
     test_equal(2, C2)
   end
 end
 
 class D < A::B::C
   test_equal(3, C3)
 end
 
 module A
   module B
     test_equal(1, C1)
     test_equal(2, C2)
   end
 end
 
 module B
   class C
     def foo
       "BC"
     end
   end
 end
 
 test_equal("ABC", A::B::C.new.foo)
 
 module Foo
   class ObjectSpace
   end
 
   test_equal('ObjectSpace', ::ObjectSpace.name)
 end
 
 class Gamma
 end
 
 module Bar
         class Gamma
         end
 
 	test_equal('Gamma', ::Gamma.name)
 end
 
 FOO = "foo"
 
 test_equal(::FOO, "foo")
 
 module X
    def X.const_missing(name)
      "missing"
    end
  end
 
 test_equal(X::String, "missing")
 
 module Y
   String = 1
 end
 
 test_equal(Y::String, 1)
 
 module Z1
   ZOOM = "zoom"
   module Z2
     module Z3
       test_equal(ZOOM, "zoom")
       test_equal(Z1::ZOOM, "zoom")
     end
   end
 end
 
 # Should not cause collision
 module Out
   Argument = "foo"
 end
 
 class Switch
   include Out
   class Argument < self
   end
 end
 
 # Should cause TypeError
 # TODO: Figure out why test_exception(TypeError) {} is not working for this...
 hack_pass = 0
 begin
   class AAAA
     FOO = "foo"
     class FOO < self
     end
   end
 rescue TypeError
   hack_pass = 1
 end
 
 test_ok(1, hack_pass)
 
 # Should not cause collision
 class Out2
   NoArgument = "foo"
 
   class Switch
     class NoArgument < self
     end
   end
 end
 
 module OutA
 end
 
 class OutA::InA
   def ok; "ok"; end
 end
 
 module OutA::InB
   OK = "ok"
 end
 
 test_ok("ok", OutA::InA.new.ok)
 test_ok("ok", OutA::InB::OK)
 
 test_ok("constant", defined? OutA)
 test_equal(nil, defined? OutNonsense)
 
 class Empty
 end
 
 # Declare constant outside of class/module
 test_equal(1, Empty::FOOT = 1)
 
 # Declare constant outside of class/module in multi assign
 b, a = 1, 1
 Empty::BART, a = 1, 1
 test_equal(1, Empty::BART)
 # Declare a constant whose value changes scope
 CONST_SCOPE_CHANGE = begin
      require 'this_will_never_load'
      true
    rescue LoadError
      false
    end
 test_equal(false, CONST_SCOPE_CHANGE)
 Empty::CONST_FOO = begin
      require 'this_will_never_load'
      true
    rescue LoadError
      false
    end
 test_equal(false, Empty::CONST_FOO)
 
 $! = nil
 defined? NoSuchThing::ToTestSideEffect
 test_equal(nil, $!)
 
 # Constants in toplevel should be searched last
 Gobble = "hello"
 module Foo2
   Gobble = "goodbye"
 end
 class Bar2
   include Foo2
   def gobble
     Gobble
   end
 end
 test_equal("goodbye", Bar2.new.gobble)
 
 # Test fix for JRUBY-1339 ("Constants nested in a Module are not included")
 
 module Outer
   class Inner
   end
 end
 
 def const_from_name(name)
   const = ::Object
   name.sub(/\A::/, '').split('::').each do |const_str|
     if const.const_defined?(const_str)
       const = const.const_get(const_str)
       next
     end
     return nil
   end
   const
 end
 
 include Outer
 
 test_equal(Outer::Inner, const_from_name("Inner"))
 test_equal("constant", defined?Inner)
 
 # End test fix for JRUBY-1339
+
+# JRUBY-2004
+test_exception(TypeError) {
+  JRuby2004 = 5
+  JRuby2004::X = 5
+}
