diff --git a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
index d3f88be182..9a8f32ae1b 100644
--- a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+++ b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
@@ -1036,1226 +1036,1226 @@ public class JVMVisitor extends IRVisitor {
 
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
     public void PopFrameInstr(PopFrameInstr popframeinstr) {
         jvmMethod().loadContext();
         jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("void postMethodFrameOnly()"));
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
         jvmLoadLocal("$block");
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
         jvmAdapter().pushInt(instr.postReqdArgsCount);
         jvmAdapter().pushInt(instr.getArgIndex());
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
         jvmMethod().invokeIRHelper("receivePostReqdArg", sig(IRubyObject.class, ThreadContext.class, IRubyObject[].class, int.class, int.class, int.class, boolean.class));
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
-        jvmAdapter().invokevirtual(p(Ruby.class), "newString", sig(String.class));
+        jvmAdapter().invokevirtual(p(Ruby.class), "newString", sig(RubyString.class, String.class));
     }
 
 
     @Override
     public void Fixnum(Fixnum fixnum) {
         jvmMethod().pushFixnum(fixnum.getValue());
     }
 
     @Override
     public void FrozenString(FrozenString frozen) {
         jvmMethod().pushFrozenString(frozen.getByteList(), frozen.getCodeRange());
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
 
         jvmMethod().pushBlockBody(closure.getHandle(), closure.getSignature(), jvm.clsData().clsName);
 
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
diff --git a/spec/compiler/general_spec.rb b/spec/compiler/general_spec.rb
index d29efc5c7d..39c9eedb90 100644
--- a/spec/compiler/general_spec.rb
+++ b/spec/compiler/general_spec.rb
@@ -1,1063 +1,1070 @@
 require 'jruby'
 require 'java'
 require 'rspec'
 
 module CompilerSpecUtils
   def silence_warnings
     verb = $VERBOSE
     $VERBOSE = nil
     yield
   ensure
     $VERBOSE = verb
   end
 end
 
 module InterpreterSpecUtils
   include CompilerSpecUtils
 
   def run_in_method(src, filename = caller_locations[0].path, line = caller_locations[0].lineno)
     run( "def __temp; #{src}; end; __temp", filename, line)
   end
 
   def run(src, filename = caller_locations[0].path, line = caller_locations[0].lineno)
     yield eval(src, TOPLEVEL_BINDING, filename, line) unless (ENV['INTERPRETER_TEST'] == 'false')
   end
 
   def self.name; "interpreter"; end
 end
 
 module JITSpecUtils
   include CompilerSpecUtils
 
   def run_in_method(src, filename = caller_locations[0].path, line = caller_locations[0].lineno)
     run( "def __temp; #{src}; end; __temp", filename, line)
   end
 
   def run(src, filename = caller_locations[0].path, line = caller_locations[0].lineno)
     yield compile_run(src, filename, line) unless (ENV['COMPILER_TEST'] == 'false')
   end
 
   def self.name; "jit"; end
 
   private
 
   def compile_to_method(src, filename, lineno)
     node = JRuby.parse(src, filename, false, lineno)
     oj = org.jruby
 
     # This logic is a mix of logic from InterpretedIRMethod's JIT, o.j.Ruby's script compilation, and IRScriptBody's
     # interpret. We need to figure out a cleaner path.
 
     scope = node.getStaticScope
     currModule = scope.getModule
     if currModule == nil
       currModule = JRuby.runtime.top_self.class
       scope.setModule(currModule)
     end
 
     method = oj.ir.IRBuilder.build_root(JRuby.runtime.getIRManager(), node).scope
     method.prepareForInitialCompilation
 
     compiler = oj.ir.targets.JVMVisitor.new
     compiled = compiler.compile(method, oj.util.OneShotClassLoader.new(JRuby.runtime.getJRubyClassLoader()))
     scriptMethod = compiled.getMethod(
         "RUBY$script",
         oj.runtime.ThreadContext.java_class,
         oj.parser.StaticScope.java_class,
         oj.runtime.builtin.IRubyObject.java_class,
         oj.runtime.builtin.IRubyObject[].java_class,
         oj.runtime.Block.java_class,
         oj.RubyModule.java_class,
         java.lang.String.java_class)
     handle = java.lang.invoke.MethodHandles.publicLookup().unreflect(scriptMethod)
 
     return oj.internal.runtime.methods.CompiledIRMethod.new(
         handle,
         method,
         oj.runtime.Visibility::PUBLIC,
         currModule,
         false)
   end
 
   def compile_run(src, filename, line)
-    cls = compile_to_method(src, filename, line)
+    cls = compile_to_method(src, filename, line - 1) # compiler expects zero-based lines
 
     cls.call(
         JRuby.runtime.current_context,
         JRuby.runtime.top_self,
         JRuby.runtime.top_self.class,
         "script",
         IRubyObject[0].new,
         Block::NULL_BLOCK)
   end
 end
 
 modes = []
 modes << InterpreterSpecUtils unless (ENV['INTERPRETER_TEST'] == 'false')
 modes << JITSpecUtils unless (ENV['COMPILER_TEST'] == 'false')
 
 Block = org.jruby.runtime.Block
 IRubyObject = org.jruby.runtime.builtin.IRubyObject
 
 modes.each do |mode|
   describe "JRuby's #{mode.name}" do
     include mode
 
     it "assigns literal values to locals" do
       run("a = 5; a") {|result| expect(result).to eq 5 }
       run("a = 5.5; a") {|result| expect(result).to eq 5.5 }
       run("a = 'hello'; a") {|result| expect(result).to eq 'hello' }
       run("a = :hello; a") {|result| expect(result).to eq :hello }
       run("a = 1111111111111111111111111111; a") {|result| expect(result).to eq 1111111111111111111111111111 }
       run("a = [1, ['foo', :hello]]; a") {|result| expect(result).to eq([1, ['foo', :hello]]) }
       run("{}") {|result| expect(result).to eq({}) }
       run("a = {:foo => {:bar => 5.5}}; a") {|result| expect(result).to eq({:foo => {:bar => 5.5}}) }
       run("a = /foo/; a") {|result| expect(result).to eq(/foo/) }
       run("1..2") {|result| expect(result).to eq (1..2) }
       run("1r") {|result| expect(result).to eq (Rational(1, 1))}
       run("1.1r") {|result| expect(result).to eq (Rational(11, 10))}
       run("1i") {|result| expect(result).to eq (Complex(0, 1))}
       run("1.1i") {|result| expect(result).to eq (Complex(0, 1.1))}
     end
 
     it "compiles interpolated strings" do
       run('a = "hello#{42}"; a') {|result| expect(result).to eq('hello42') }
       run('i = 1; a = "hello#{i + 42}"; a') {|result| expect(result).to eq("hello43") }
     end
 
     it "compiles calls" do
       run("'bar'.capitalize") {|result| expect(result).to eq 'Bar' }
       run("rand(10)") {|result| expect(result).to be_a_kind_of Fixnum }
     end
 
     it "compiles branches" do
       run("a = 1; if 1 == a; 2; else; 3; end") {|result| expect(result).to eq 2 }
       run("a = 1; unless 1 == a; 2; else; 3; end") {|result| expect(result).to eq 3 }
       run("a = 1; while a < 10; a += 1; end; a") {|result| expect(result).to eq 10 }
       run("a = 1; until a == 10; a += 1; end; a") {|result| expect(result).to eq 10 }
       run("2 if true") {|result| expect(result).to eq 2 }
       run("2 if false") {|result| expect(result).to be_nil }
       run("2 unless true") {|result| expect(result).to be_nil }
       run("2 unless false") {|result| expect(result).to eq 2 }
     end
 
     it "compiles while loops with no body" do
       run("@foo = true; def flip; @foo = !@foo; end; while flip; end") do |result|
         expect(result).to eq nil
       end
     end
 
     it "compiles boolean operators" do
       run("1 && 2") {|result| expect(result).to eq 2 }
       run("nil && 2") {|result| expect(result).to be_nil }
       run("nil && fail") {|result| expect(result).to be_nil }
       run("1 || 2") {|result| expect(result).to eq 1 }
       run("nil || 2") {|result| expect(result).to eq 2 }
       expect {run(nil || fail){}}.to raise_error(RuntimeError)
       run("1 and 2") {|result| expect(result).to eq 2 }
       run("1 or 2") {|result| expect(result).to eq 1 }
     end
 
     it "compiles begin blocks" do
       run("begin; a = 4; end; a") {|result| expect(result).to eq 4 }
     end
 
     it "compiles regexp matches" do
       run("/foo/ =~ 'foo'") {|result| expect(result).to eq 0 }
       run("'foo' =~ /foo/") {|result| expect(result).to eq 0 }
       run(":aaa =~ /foo/") {|result| expect(result).to be_nil }
     end
 
     it "compiles method definitions" do
       run("def foo3(arg); arg + '2'; end; foo3('baz')") {|result| expect(result).to eq 'baz2' }
       run("def self.foo3(arg); arg + '2'; end; self.foo3('baz')") {|result| expect(result).to eq 'baz2' }
     end
 
     it "compiles calls with closures" do
       run("def foo2(a); a + yield.to_s; end; foo2('baz') { 4 }") {|result| expect(result).to eq 'baz4' }
       run("def foo2(a); a + yield.to_s; end; foo2('baz') {}") {|result| expect(result).to eq 'baz' }
       run("def self.foo2(a); a + yield.to_s; end; self.foo2('baz') { 4 }") {|result| expect(result).to eq 'baz4' }
       run("def self.foo2(a); a + yield.to_s; end; self.foo2('baz') {}") {|result| expect(result).to eq 'baz' }
     end
 
     it "compiles strings with encoding" do
       str8bit = '"\300"'
       run(str8bit) do |str8bit_result|
         expect(str8bit_result).to eq "\300"
         expect(str8bit_result.encoding).to eq Encoding::UTF_8
       end
     end
 
     it "compiles backrefs" do
       base = "'0123456789A' =~ /(1)(2)(3)(4)(5)(6)(7)(8)(9)/; "
       run(base + "$~") {|result| expect(result).to be_a_kind_of MatchData }
       run(base + "$`") {|result| expect(result).to eq '0' }
       run(base + "$'") {|result| expect(result).to eq 'A' }
       run(base + "$+") {|result| expect(result).to eq '9' }
       run(base + "$0") {|result| expect(result).to eq $0 } # main script name, not related to matching
       run(base + "$1") {|result| expect(result).to eq '1' }
       run(base + "$2") {|result| expect(result).to eq '2' }
       run(base + "$3") {|result| expect(result).to eq '3' }
       run(base + "$4") {|result| expect(result).to eq '4' }
       run(base + "$5") {|result| expect(result).to eq '5' }
       run(base + "$6") {|result| expect(result).to eq '6' }
       run(base + "$7") {|result| expect(result).to eq '7' }
       run(base + "$8") {|result| expect(result).to eq '8' }
       run(base + "$9") {|result| expect(result).to eq '9' }
     end
 
     it "compiles aliases" do
       run("alias :to_string1 :to_s; defined?(self.to_string1)") {|result| expect(result).to eq "method" }
       run("alias to_string2 to_s; defined?(self.to_string2)") {|result| expect(result).to eq "method" }
     end
 
     it "compiles block-local variables" do
       blocks_code = <<-EOS
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
         arr
         EOS
       run(blocks_code) {|result| expect(result).to eq([1,2,3,4,5,6]) }
     end
 
     it "compiles yield" do
       run("def foo; yield 1; end; foo {|a| a + 2}") {|result| expect(result).to eq 3 }
 
       yield_in_block = <<-EOS
         def foo
           bar { yield }
         end
         def bar
           yield
         end
         foo { 1 }
         EOS
       run(yield_in_block) {|result| expect(result).to eq 1}
 
       yield_in_proc = <<-EOS
         def foo
           proc { yield }
         end
         p = foo { 1 }
         p.call
         EOS
       run(yield_in_proc) {|result| expect(result).to eq 1 }
     end
 
     it "compiles attribute assignment" do
       run("public; def a=(x); 2; end; self.a = 1") {|result| expect(result).to eq 1 }
       run("public; def a; 1; end; def a=(arg); fail; end; self.a ||= 2") {|result| expect(result).to eq 1 }
       run("public; def a; @a; end; def a=(arg); @a = arg; 4; end; x = self.a ||= 1; [x, self.a]") {|result| expect(result).to eq([1,1]) }
       run("public; def a; nil; end; def a=(arg); fail; end; self.a &&= 2") {|result| expect(result).to be_nil }
       run("public; def a; @a; end; def a=(arg); @a = arg; end; @a = 3; x = self.a &&= 1; [x, self.a]") {|result| expect(result).to eq([1,1]) }
     end
 
     it "compiles lastline" do
       run("def foo; $_ = 1; bar; $_; end; def bar; $_ = 2; end; foo") {|result| expect(result).to eq 1 }
     end
 
     it "compiles closure arguments" do
       run("a = 0; [1].each {|a|}; a") {|result| expect(result).to eq(0) }
       run("a = 0; [1].each {|x| a = x}; a") {|result| expect(result).to eq 1 }
       run("[[1,2,3]].each {|x,*y| break y}") {|result| expect(result).to eq([2,3]) }
       run("1.times {|x,*y| break y}") {|result| expect(result).to eq([]) }
       run("1.times {|x,*|; break x}") {|result| expect(result).to eq 0 }
     end
 
     it "compiles class definitions" do
       class_string = <<-EOS
         class CompiledClass1
           def foo
             "cc1"
           end
         end
         CompiledClass1.new.foo
         EOS
       run(class_string) {|result| expect(result).to eq 'cc1' }
     end
 
     it "compiles module definitions" do
       module_string = <<-EOS
         module CompiledModule1
           def self.bar
             "cm1"
           end
         end
         CompiledModule1.bar
       EOS
 
       run(module_string) {|result| expect(result).to eq 'cm1' }
     end
 
     it "compiles operator assignment" do
       run("class H; attr_accessor :v; end; H.new.v ||= 1") {|result| expect(result).to eq 1 }
       run("class H; def initialize; @v = true; end; attr_accessor :v; end; H.new.v &&= 2") {|result| expect(result).to eq 2 }
       run("class H; def initialize; @v = 1; end; attr_accessor :v; end; H.new.v += 3") {|result| expect(result).to eq 4 }
     end
 
     it "compiles optional method arguments" do
       run("def foo(a,b=1);[a,b];end;foo(1)") {|result| expect(result).to eq([1,1]) }
       run("def foo(a,b=1);[a,b];end;foo(1,2)") {|result| expect(result).to eq([1,2]) }
       expect{run("def foo(a,b=1);[a,b];end;foo")}.to raise_error(ArgumentError)
       expect{run("def foo(a,b=1);[a,b];end;foo(1,2,3)")}.to raise_error(ArgumentError)
       run("def foo(a=(b=1));[a,b];end;foo") {|result| expect(result).to eq([1,1]) }
       run("def foo(a=(b=1));[a,b];end;foo(2)") {|result| expect(result).to eq([2,nil]) }
       run("def foo(a, b=(c=1));[a,b,c];end;foo(1)") {|result| expect(result).to eq([1,1,1]) }
       run("def foo(a, b=(c=1));[a,b,c];end;foo(1,2)") {|result| expect(result).to eq([1,2,nil]) }
       expect{run("def foo(a, b=(c=1));[a,b,c];end;foo(1,2,3)")}.to raise_error(ArgumentError)
     end
 
     it "compiles grouped and intra-list rest args" do
       run("def foo(a, (b, *, c), d, *e, f, (g, *h, i), j); [a,b,c,d,e,f,g,h,i,j]; end; foo(1,[2,3,4],5,6,7,8,[9,10,11],12)") do |result|
         expect(result).to eq([1, 2, 4, 5, [6, 7], 8, 9, [10], 11, 12])
       end
     end
 
     it "compiles splatted values" do
       run("def foo(a,b,c);[a,b,c];end;foo(1, *[2, 3])") {|result| expect(result).to eq([1,2,3]) }
     end
 
     it "compiles multiple assignment" do
       run("a = nil; 1.times { a, b, @c = 1, 2, 3; a = [a, b, @c] }; a") {|result| expect(result).to eq([1,2,3]) }
       run("a, (b, c) = 1; [a, b, c]") {|result| expect(result).to eq([1,nil,nil]) }
       run("a, (b, c) = 1, 2; [a, b, c]") {|result| expect(result).to eq([1,2,nil]) }
       run("a, (b, c) = 1, [2, 3]; [a, b, c]") {|result| expect(result).to eq([1,2,3]) }
       run("class Coercible2;def to_ary;[2,3]; end; end; a, (b, c) = 1, Coercible2.new; [a, b, c]") {|result| expect(result).to eq([1,2,3]) }
       run("a, (b, *, c), d, *e, f, (g, *h, i), j = 1,[2,3,4],5,6,7,8,[9,10,11],12; [a,b,c,d,e,f,g,h,i,j]") do |result|
         expect(result).to eq([1, 2, 4, 5, [6, 7], 8, 9, [10], 11, 12])
       end
     end
 
     it "compiles dynamic regexp" do
       run('"foo" =~ /#{"foo"}/') {|result| expect(result).to eq 0 }
       run('ary = []; 2.times {|i| ary << ("foo0" =~ /#{"foo" + i.to_s}/o)}; ary') {|result| expect(result).to eq([0, 0]) }
     end
 
     it "compiles implicit and explicit return" do
       run("def foo; 1; end; foo") {|result| expect(result).to eq 1 }
       run("def foo; return; end; foo") {|result| expect(result).to be_nil }
       run("def foo; return 1; end; foo") {|result| expect(result).to eq 1 }
     end
 
     it "compiles class reopening" do
       run("class Fixnum; def x; 3; end; end; 1.x") {|result| expect(result).to eq 3 }
     end
 
     it "compiles singleton method definitions" do
       run("a = 'bar'; def a.foo; 'foo'; end; a.foo") {|result| expect(result).to eq "foo" }
       run("class Fixnum; def self.foo; 'foo'; end; end; Fixnum.foo") {|result| expect(result).to eq "foo" }
       run("def String.foo; 'foo'; end; String.foo") {|result| expect(result).to eq "foo" }
     end
 
     it "compiles singleton class definitions" do
       run("a = 'bar'; class << a; def bar; 'bar'; end; end; a.bar") {|result| expect(result).to eq "bar" }
       run("class Fixnum; class << self; def bar; 'bar'; end; end; end; Fixnum.bar") {|result| expect(result).to eq "bar" }
       run("class Fixnum; def self.metaclass; class << self; self; end; end; end; Fixnum.metaclass") do |result|
         expect(result).to eq class << Fixnum; self; end
       end
     end
 
     it "compiles loops with flow control" do
       # some loop flow control tests
       run("a = true; b = while a; a = false; break; end; b") {|result| expect(result).to be_nil }
       run("a = true; b = while a; a = false; break 1; end; b") {|result| expect(result).to eq 1 }
       run("a = 0; while true; a += 1; next if a < 2; break; end; a") {|result| expect(result).to eq 2 }
       run("a = 0; while true; a += 1; next 1 if a < 2; break; end; a") {|result| expect(result).to eq 2 }
       run("a = 0; while true; a += 1; redo if a < 2; break; end; a") {|result| expect(result).to eq 2 }
       run("a = false; b = until a; a = true; break; end; b") {|result| expect(result).to be_nil }
       run("a = false; b = until a; a = true; break 1; end; b") {|result| expect(result).to eq 1 }
       run("a = 0; until false; a += 1; next if a < 2; break; end; a") {|result| expect(result).to eq 2 }
       run("a = 0; until false; a += 1; next 1 if a < 2; break; end; a") {|result| expect(result).to eq 2 }
       run("a = 0; until false; a += 1; redo if a < 2; break; end; a") {|result| expect(result).to eq 2 }
     end
 
     it "compiles loops with non-local flow control" do
       # non-local flow control with while loops
       run("a = 0; 1.times { a += 1; redo if a < 2 }; a") {|result| expect(result).to eq 2 }
       run("def foo(&b); while true; b.call; end; end; foo { break 3 }") {|result| expect(result).to eq 3 }
     end
 
     it "compiles block passing" do
       # block pass node compilation
       run("def foo; block_given?; end; p = proc {}; [foo(&nil),foo(&p)]") {|result| expect(result).to eq([false, true]) }
       run("public; def foo; block_given?; end; p = proc {}; [self.foo(&nil),self.foo(&p)]") {|result| expect(result).to eq([false, true]) }
     end
 
     it "compiles splatted element assignment" do
       run("a = 'foo'; y = ['o']; a[*y] = 'asdf'; a") {|result| expect(result).to match "fasdfo" }
     end
 
     it "compiles constant access" do
       const_code = <<-EOS
         A ||= 'a'; module X; B ||= 'b'; end; module Y; def self.go; [A, X::B, ::A]; end; end; Y.go
       EOS
       run(const_code) {|result| expect(result).to eq(["a", "b", "a"]) }
     end
 
     # it "compiles flip-flop" do
     #   # flip (taken from http://redhanded.hobix.com/inspect/hopscotchingArraysWithFlipFlops.html)
     #   run_in_method("s = true; (1..10).reject { true if (s = !s) .. (s) }") {|result| expect(result).to eq([1, 3, 5, 7, 9]) }
     #   run_in_method("s = true; (1..10).reject { true if (s = !s) .. (s = !s) }") {|result| expect(result).to eq([1, 4, 7, 10]) }
     #   big_flip = <<-EOS
     #   s = true; (1..10).inject([]) do |ary, v|; ary << [] unless (s = !s) .. (s = !s); ary.last << v; ary; end
     #   EOS
     #   run_in_method(big_flip) {|result| expect(result).to eq([[1, 2, 3], [4, 5, 6], [7, 8, 9], [10]]) }
     #   big_triple_flip = <<-EOS
     #   s = true
     #   (1..64).inject([]) do |ary, v|
     #       unless (s ^= v[2].zero?)...(s ^= !v[1].zero?)
     #           ary << []
     #       end
     #       ary.last << v
     #       ary
     #   end
     #   EOS
     #   expected = [[1, 2, 3, 4, 5, 6, 7, 8],
     #               [9, 10, 11, 12, 13, 14, 15, 16],
     #               [17, 18, 19, 20, 21, 22, 23, 24],
     #               [25, 26, 27, 28, 29, 30, 31, 32],
     #               [33, 34, 35, 36, 37, 38, 39, 40],
     #               [41, 42, 43, 44, 45, 46, 47, 48],
     #               [49, 50, 51, 52, 53, 54, 55, 56],
     #               [57, 58, 59, 60, 61, 62, 63, 64]]
     #   run_in_method(big_triple_flip) {|result| expect(result).to eq(expected) }
     # end
 
     it "gracefully handles named captures when there's no match" do
       expect do
         run('/(?<a>.+)/ =~ ""') {}
       end.to_not raise_error
     end
 
     it "handles module/class opening from colon2 with non-method, non-const LHS" do
       expect do
         run('m = Object; class m::FOOCLASS1234; end; module m::FOOMOD1234; end') {}
       end.to_not raise_error
     end
 
     it "properly handles non-local flow for a loop inside an ensure (JRUBY-6836)" do
       ary = []
       run('
         def main
           ary = []
           while true
             begin
               break
             ensure
               ary << 1
             end
           end
           ary << 2
         ensure
           ary << 3
         end
 
         main') do |result|
         expect(result).to eq([1,2,3])
       end
     end
 
     it "prepares a proper caller scope for partition/rpartition (JRUBY-6827)" do
       run(%q[
         def foo
           Object
           "/Users/headius/projects/jruby/tmp/perfer/examples/file_stat.rb:4:in `(root)'".rpartition(/:\d+(?:$|:in )/).first
         end
 
         foo]) do |result|
         expect(result).to eq '/Users/headius/projects/jruby/tmp/perfer/examples/file_stat.rb'
       end
     end
 
     it "handles attr accessors for unassigned vars properly" do
       # under invokedynamic, we were caching the "dummy" accessor that never saw any value
       run('
   class AttrAccessorUnassigned
     attr_accessor :foo
   end
 
   obj = AttrAccessorUnassigned.new
   ary = []
   2.times { ary << obj.foo; obj.foo = 1}
   ary
       ') do |result|
         expect(result).to eq([nil, 1])
       end
     end
 
 
     it "does not break String#to_r and to_c" do
       # This is structured to cause a "dummy" scope because of the String constant
       # This caused to_r and to_c to fail since that scope always returns nil
       run('
       def foo
         [String.new("0.1".to_c.to_s), String.new("0.1".to_r.to_s)]
       end
       foo
       ') do |result|
         expect(result).to eq(["0.1+0i", "1/10"])
       end
     end
 
     it "handles 0-4 arg and splatted whens in a caseless case/when" do
       run('
         case
         when false
           fail
         when false, false
           fail
         when false, false, false
           fail
         when false, false, false, false
           fail
         when *[false, false, false, false]
         else
           42
         end
       ') do |result|
         expect(result).to eq 42
       end
     end
 
     it "matches any true value for a caseless case/when with > 3 args" do
       result = run('
         case
         when false, false, false, true
           42
         end
       ') do |result|
         expect(result).to eq 42
       end
     end
 
     it "properly handles method-root rescue logic with returns (GH\#733)" do
       run("def foo; return 1; rescue; return 2; else; return 3; end; foo") {|result| expect(result).to eq 1 }
       run("def foo; 1; rescue; return 2; else; return 3; end; foo") {|result| expect(result).to eq 3 }
       run("def foo; raise; rescue; return 2; else; return 3; end; foo") {|result| expect(result).to eq 2 }
     end
 
     it "mangles filenames internally to avoid conflicting delimiters when building descriptors (GH\#961)" do
       run(
         "1.times { 1 }",
         "my,0.25,file:with:many|odd|delimiters.rb"
       ) do |result|
         expect(result).to eq 1
       end
     end
 
     it "keeps backref local to the caller scope when calling !~" do
       run('
         Class.new do
           def blank?
             "a" !~ /[^[:space:]]/
           end
         end.new
       ') do |obj|
         $~ = nil
         expect(obj).not_to be_blank
         expect($~).to be_nil
       end
     end
 
     # GH-1239
     it "properly scopes singleton method definitions in a compiled body" do
       run("
         class GH1239
           def self.define; def gh1239; end; end
           def self.remove; remove_method :gh1239; end
         end
         GH1239
       ") do |cls|
 
         cls.define
         expect(cls.methods).not_to be_include :gh1239
         expect{cls.remove}.not_to raise_error
       end
     end
 
     it "yields nil when yielding no arguments" do
       silence_warnings {
         # bug 1305, no values yielded to single-arg block assigns a null into the arg
         run("def foo; yield; end; foo {|x| x.class}") {|result| expect(result).to eq NilClass }
       }
     end
 
     it "prevents reopening or extending non-modules" do
       # ensure that invalid classes and modules raise errors
       AFixnum ||= 1
       expect { run("class AFixnum; end")}.to raise_error(TypeError)
       expect { run("class B < AFixnum; end")}.to raise_error(TypeError)
       expect { run("module AFixnum; end")}.to raise_error(TypeError)
     end
 
     it "assigns array elements properly as LHS of masgn" do
       # attr assignment in multiple assign
       run("a = Object.new; class << a; attr_accessor :b; end; a.b, a.b = 'baz','bar'; a.b") {|result| expect(result).to eq "bar" }
       run("a = []; a[0], a[1] = 'foo','bar'; a") {|result| expect(result).to eq(["foo", "bar"]) }
     end
 
     it "executes for loops properly" do
       # for loops
       run("a = []; for b in [1, 2, 3]; a << b * 2; end; a") {|result| expect(result).to eq([2, 4, 6]) }
       run("a = []; for b, c in {:a => 1, :b => 2, :c => 3}; a << c; end; a.sort") {|result| expect(result).to eq([1, 2, 3]) }
     end
 
     it "fires ensure blocks after normal or early block termination" do
       # ensure blocks
       run("a = 2; begin; a = 3; ensure; a = 1; end; a") {|result| expect(result).to eq 1 }
       run("$a = 2; def foo; return; ensure; $a = 1; end; foo; $a") {|result| expect(result).to eq 1 }
     end
 
     it "handles array element assignment with ||, +, and && operators" do
       # op element assign
       run("a = []; [a[0] ||= 4, a[0]]") {|result| expect(result).to eq([4, 4]) }
       run("a = [4]; [a[0] ||= 5, a[0]]") {|result| expect(result).to eq([4, 4]) }
       run("a = [1]; [a[0] += 3, a[0]]") {|result| expect(result).to eq([4, 4]) }
       run("a = {}; a[0] ||= [1]; a[0]") {|result| expect(result).to eq([1]) }
       run("a = [1]; a[0] &&= 2; a[0]") {|result| expect(result).to eq 2 }
     end
 
     it "propagates closure returns to the method body" do
       # non-local return
       run("def foo; loop {return 3}; return 4; end; foo") {|result| expect(result).to eq 3 }
     end
 
     it "handles class variable declaration and access" do
       # class var declaration
       run("class Foo; @@foo = 3; end") {|result| expect(result).to eq 3 }
       run("class Bar; @@bar = 3; def self.bar; @@bar; end; end; Bar.bar") {|result| expect(result).to eq 3 }
     end
 
     it "handles exceptional flow transfer to rescue blocks" do
       # rescue
       run("x = begin; 1; raise; rescue; 2; end") {|result| expect(result).to eq 2 }
       run("x = begin; 1; raise; rescue TypeError; 2; rescue; 3; end") {|result| expect(result).to eq 3 }
       run("x = begin; 1; rescue; 2; else; 4; end") {|result| expect(result).to eq 4 }
       run("def foo; begin; return 4; rescue; end; return 3; end; foo") {|result| expect(result).to eq 4 }
     end
 
     it "properly resets $! to nil upon normal exit from a rescue" do
       # test that $! is getting reset/cleared appropriately
       $! = nil
       run("begin; raise; rescue; end; $!") {|result| expect(result).to be_nil }
       run("1.times { begin; raise; rescue; next; end }; $!") {|result| expect(result).to be_nil }
       run("begin; raise; rescue; begin; raise; rescue; end; $!; end") {|result| expect(result).to_not be_nil }
       run("begin; raise; rescue; 1.times { begin; raise; rescue; next; end }; $!; end") {|result| expect(result).to_not be_nil }
     end
 
     it "executes ensure wrapping a while body that breaks after the loop has terminated" do
       # break in a while in an ensure
       run("begin; x = while true; break 5; end; ensure; end") {|result| expect(result).to eq 5 }
     end
 
     it "resolves Foo::Bar style constants" do
       # JRUBY-1388, Foo::Bar broke in the compiler
       silence_warnings do
         run("module Foo2; end; Foo2::Foo3 = 5; Foo2::Foo3") {|result| expect(result).to eq 5 }
       end
     end
 
     it "re-runs enclosing block when redo is called from ensure" do
       run("def foo; yield; end; x = false; foo { break 5 if x; begin; ensure; x = true; redo; end; break 6}") {|result| expect(result).to eq 5 }
     end
 
     it "compiles END Blocks" do
       # END block
       expect { run("END {}"){} }.to_not raise_error
     end
 
     it "compiles BEGIN blocks" do
       # BEGIN block
       run("BEGIN { $begin = 5 }; $begin") {|result| expect(result).to eq 5 }
     end
 
     it "compiles empty source" do
       # nothing at all!
       run("") {|result| expect(result).to be_nil }
     end
 
     it "properly assigns values in masgn without overwriting neighboring values" do
       # JRUBY-2043
       run("def foo; 1.times { a, b = [], 5; a[1] = []; return b; }; end; foo") {|result| expect(result).to eq 5 }
       run("def foo; x = {1 => 2}; x.inject({}) do |hash, (key, value)|; hash[key.to_s] = value; hash; end; end; foo") {|result| expect(result).to eq({"1" => 2}) }
     end
 
     it "compiles very long code bodies" do
       skip "JRUBY-2246"
       long_src = "a = 1\n"
       5000.times { long_src << "a += 1\n" }
       run(long_src) {|result| expect(result).to eq 5001 }
     end
 
     it "assigns the result of a terminated loop to LHS variable" do
       # variable assignment of various types from loop results
       run("a = while true; break 1; end; a") {|result| expect(result).to eq 1 }
       run("@a = while true; break 1; end; @a") {|result| expect(result).to eq 1 }
       run("@@a = while true; break 1; end; @@a") {|result| expect(result).to eq 1 }
       run("$a = while true; break 1; end; $a") {|result| expect(result).to eq 1 }
       run("a = until false; break 1; end; a") {|result| expect(result).to eq 1 }
       run("@a = until false; break 1; end; @a") {|result| expect(result).to eq 1 }
       run("@@a = until false; break 1; end; @@a") {|result| expect(result).to eq 1 }
       run("$a = until false; break 1; end; $a") {|result| expect(result).to eq 1 }
 
       # same assignments but loop is within a begin
       run("a = begin; while true; break 1; end; end; a") {|result| expect(result).to eq 1 }
       run("@a = begin; while true; break 1; end; end; @a") {|result| expect(result).to eq 1 }
       run("@@a = begin; while true; break 1; end; end; @@a") {|result| expect(result).to eq 1 }
       run("$a = begin; while true; break 1; end; end; $a") {|result| expect(result).to eq 1 }
       run("a = begin; until false; break 1; end; end; a") {|result| expect(result).to eq 1 }
       run("@a = begin; until false; break 1; end; end; @a") {|result| expect(result).to eq 1 }
       run("@@a = begin; until false; break 1; end; end; @@a") {|result| expect(result).to eq 1 }
       run("$a = begin; until false; break 1; end; end; $a") {|result| expect(result).to eq 1 }
 
       # other contexts that require while to preserve stack
       run("1 + while true; break 1; end") {|result| expect(result).to eq 2 }
       run("1 + begin; while true; break 1; end; end") {|result| expect(result).to eq 2 }
       run("1 + until false; break 1; end") {|result| expect(result).to eq 2 }
       run("1 + begin; until false; break 1; end; end") {|result| expect(result).to eq 2 }
       run("def foo(a); a; end; foo(while false; end)") {|result| expect(result).to be_nil }
       run("def foo(a); a; end; foo(until true; end)") {|result| expect(result).to be_nil }
     end
 
     it "constructs symbols on first execution and retrieves them from cache on subsequent executions" do
       # test that 100 symbols compiles ok; that hits both types of symbol caching/creation
       syms = [:a]
       99.times {|i| syms << ('foo' + i.to_s).intern }
       # 100 first instances of a symbol
       run(syms.inspect) {|result| expect(result).to eq syms }
       # 100 first instances and 100 second instances (caching)
       run("[#{syms.inspect},#{syms.inspect}]") {|result| expect(result).to eq([syms,syms]) }
     end
 
     it "can extend a class contained in a local variable" do
       # class created using local var as superclass
       run(<<-EOS) {|result| expect(result).to eq 'AFromLocal' }
       a = Object
       class AFromLocal < a
       end
       AFromLocal.to_s
       EOS
     end
 
     it "can compile large literal arrays and hashes" do
       skip "JRUBY-4757 and JRUBY-2621: can't compile large array/hash"
 
       large_array = (1..10000).to_a.inspect
       large_hash = large_array.clone
       large_hash.gsub!('[', '{')
       large_hash.gsub!(']', '}')
       run(large_array) do |result|
         expect(result).to eq(eval(large_array) {|result| expect(result) })
       end
     end
 
     it "properly spreads incoming array when block args contain multiple variables" do
       # block arg spreading cases
       run("def foo; a = [1]; yield a; end; foo {|a| a}") {|result| expect(result).to eq([1]) }
       run("x = nil; [[1]].each {|a| x = a}; x") {|result| expect(result).to eq([1]) }
       run("def foo; yield [1, 2]; end; foo {|x, y| [x, y]}") {|result| expect(result).to eq([1,2]) }
     end
 
     it "compiles non-expression case statements without an else clause" do
       # non-expr case statement with return with if modified with call
       # broke in 1.9 compiler due to null "else" node pushing a nil when non-expr
       run("def foo; case 0; when 1; return 2 if self.nil?; end; return 3; end; foo") {|result| expect(result).to eq 3 }
     end
 
     it "assigns named groups in regular expressions to local variables" do
       # named groups with capture
       run("
       def foo
         ary = []
         a = nil
         b = nil
         1.times {
           /(?<b>ell)(?<c>o)/ =~ 'hello'
           ary << a
           ary << b
           ary << c
         }
         ary << b
         ary
       end
       foo") do |result|
         expect(result).to eq([nil,'ell', 'o', 'ell'])
       end
     end
 
     it "handles complicated splatting at beginning and end of literal array" do
       # chained argscat and argspush
       run("a=[1,2];b=[4,5];[*a,3,*a,*b]") {|result| expect(result).to eq([1,2,3,1,2,4,5]) }
     end
 
     it "dispatches super and zsuper arguments correctly in the presence of a rest argument" do
       # JRUBY-5871: test that "special" args dispatch along specific-arity path
       test = '
       %w[foo bar].__send__ :to_enum, *[], &nil
       '
       run(test) do |result|
         expect(result.map {|line| line + 'yum'}).to eq(["fooyum", "baryum"])
       end
 
       # These two cases triggered ArgumentError when Enumerator was fixed to enforce
       # 3 required along its varargs path. Testing both here to ensure super/zsuper
       # also dispatch along arity-specific paths as appropriate
       enumerable = "Enumerator"
       expect{run("
       class JRuby5871A < #{enumerable}
         def initialize(x, y, *z)
           super
         end
       end
       "){}}.to_not raise_error
 
       expect {
         JRuby5871A.new("foo", :each_byte)
       }.to_not raise_error
 
       expect{run("
       class JRuby5871B < #{enumerable}
         def initialize(x, y, *z)
           super(x, y, *z)
         end
       end
       "){}}.to_not raise_error
 
       expect {
         JRuby5871B.new("foo", :each_byte)
       }.to_not raise_error
     end
 
     it "allows colon2 const assignment on LHS of masgn" do
       class JRUBY4925
       end
 
       silence_warnings do
         run 'JRUBY4925::BLAH, a = 1, 2' do |x|
           expect(JRUBY4925::BLAH).to eq 1
         end
         run '::JRUBY4925_BLAH, a = 1, 2' do |x|
           expect(JRUBY4925_BLAH).to eq 1
         end
       end
     end
 
     it "compiles backquotes (backtick)" do
       run 'o = Object.new; def o.`(str); str; end; def o.go; `hello`; end; o.go' do |x|
         expect(x).to eq 'hello'
       end
     end
 
     it "creates frozen strings for backquotes (backtick)" do
       run 'o = Object.new; def o.`(str); str; end; def o.go; `hello`; end; o.go' do |x|
         expect(x).to be_frozen
       end
     end
 
     it "compiles rest args passed to return, break, and next (svalue)" do
       run 'a = [1,2,3]; 1.times { break *a }' do |x|
         expect(x).to eq [1,2,3]
       end
 
       run 'a = [1,2,3]; lambda { return *a }.call' do |x|
         expect(x).to eq [1,2,3]
       end
 
       run 'a = [1,2,3]; def foo; yield; end; foo { next *a }' do |x|
         expect(x).to eq [1,2,3]
       end
     end
 
     it "compiles optional arguments in a method with toplevel rescue" do
       run 'def foo(a = false); raise; rescue; a; end; foo' do |x|
         expect(x).to eq false
       end
     end
 
     it "compiles optional arguments with a constant" do
       run 'def foo(a = Object); a; end; foo' do |x|
         expect(x).to eq Object
       end
     end
 
     it "retrieves toplevel constants with ::Const form" do
       run '::Object' do |x|
         expect(x).to eq Object
       end
     end
 
     it "splats arguments to super" do
       run '
         class SplatSuperArgs0
           def foo(a, b, c)
             a + b + c
           end
         end
         class SplatSuperArgs1 < SplatSuperArgs0
           def foo(*args)
             super(*args)
           end
         end
         SplatSuperArgs1.new.foo(1, 2, 3)' do |x|
         expect(x).to eq 6
       end
     end
 
     it "performs super calls within a closure" do
       run '
         class SplatSuperArgs0
           def foo(a)
             a
           end
         end
         class SplatSuperArgs1 < SplatSuperArgs0
           def foo(a)
             1.times do
               super(a)
             end
           end
         end
         SplatSuperArgs1.new.foo(1)' do |x|
         expect(x).to eq 1
       end
     end
 
     it "passes kwargs through zsuper correctly" do
       run 'class X1; def foo(a:1, b:2); [a, b]; end; end; class X2 < X1; def foo(a:1, b:2); a = 5; super; end; end; X2.new.foo(a:3, b:4)' do |x|
         expect(x).to eq [5,4]
       end
     end
 
     it "raises errors for missing required keyword arguments" do
       expect {run('def foo(a:); end; foo'){}}.to raise_error(ArgumentError)
     end
 
     it "passes keyrest arguments through zsuper correctly" do
       run '
         class C
           def foo(str: "foo", num: 42, **opts)
           [str, num, opts]
           end
         end
 
         class D < C
           def foo(str: "bar", num: 45, **opts)
           super
           end
         end
 
         [C.new.foo, D.new.foo, D.new.foo(str: "d", num:75, a:1, b:2)]
       ' do |x|
 
         expect(x).to eq [
             ["foo", 42, {}],
             ["bar", 45, {}],
             ["d",   75, {a:1,b:2}]
                         ]
       end
     end
 
     it "handles dynamic case/when elements" do
       # These use a global so IR does not inline it from a local var
       run('$case_str = "z"; case "xyz"; when /#{$case_str}/; true; else; false; end') do |x|
         expect(x).to eq(true)
       end
       run('$case_str = "xyz"; case "xyz"; when "#{$case_str}"; true; else; false; end') do |x|
         expect(x).to eq(true)
       end
       run('$case_str = "xyz"; case :xyz; when :"#{$case_str}"; true; else; false; end') do |x|
         expect(x).to eq(true)
       end
     end
 
     it "handles lists of conditions in case/when" do
       run('$case_str = "z"; case "xyz"; when /#{$$}/, /#{$case_str}/; true; else; false; end') do |x|
         expect(x).to eq(true)
       end
     end
 
     it "handles literal arrays in case/when" do
       run('$case_ary = [1,2,3]; case $case_ary; when [1,2,3]; true; else; false; end') do |x|
         expect(x).to eq(true)
       end
     end
 
     it "enforces visibility" do
       run('obj = Class.new do
              def a; true; end
              private def b; true; end
              protected; def c; true; end
            end.new
            [obj.a, (obj.b rescue false), (obj.c rescue false)]') do |x|
         expect(x).to eq([true, false, false])
       end
     end
 
     it "pushes call name into frame" do
       run('obj = Class.new do
              def a; __callee__; end
              define_method :b, instance_method(:a)
            end.new
            [obj.a, obj.b]') do |x|
         expect(x).to eq([:a, :b])
       end
     end
 
     it "raises appropriate missing-method error for call type" do
       # Variable
       run('begin; does_not_exist; rescue NameError; $!; end') do |x|
         expect(x).to be_instance_of(NameError)
       end
 
       # Functional
       run('begin; does_not_exist(); rescue NameError; $!; end') do |x|
         expect(x).to be_instance_of(NoMethodError)
       end
 
       # Normal
       run('begin; self.does_not_exist; rescue NameError; $!; end') do |x|
         expect(x).to be_instance_of(NoMethodError)
       end
     end
 
     it "preserves 'encoding none' flag for literal regexp" do
       run('/a/n.options') do |x|
         expect(x).to eq(32)
       end
     end
 
     it "handles nested [], loops, and break" do
       run('def foo
              while 1
                "x"[0]
                while 1
                  raise "ok"
                  break
                end
              end
            end
            foo rescue $!') do |x|
         expect(x.message).to eq("ok")
       end
     end
+
+    it "returns a proper __FILE__ and __LINE__" do
+      run('[__FILE__, __LINE__]', 'foobar.rb', 1) do |x, y|
+        expect(x).to eq('foobar.rb')
+        expect(y).to eq(1)
+      end
+    end
   end
 end
