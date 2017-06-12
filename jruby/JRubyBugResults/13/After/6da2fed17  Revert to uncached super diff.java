diff --git a/core/src/main/java/org/jruby/ir/IRVisitor.java b/core/src/main/java/org/jruby/ir/IRVisitor.java
index 29c176f544..525c78e599 100644
--- a/core/src/main/java/org/jruby/ir/IRVisitor.java
+++ b/core/src/main/java/org/jruby/ir/IRVisitor.java
@@ -1,177 +1,178 @@
 package org.jruby.ir;
 
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.ir.instructions.*;
 import org.jruby.ir.instructions.boxing.*;
 import org.jruby.ir.instructions.defined.GetErrorInfoInstr;
 import org.jruby.ir.instructions.defined.RestoreErrorInfoInstr;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.operands.Boolean;
 
 /**
  * Superclass for IR visitors.
  */
 public abstract class IRVisitor {
     public void visit(Instr instr) {
         instr.visit(this);
     }
 
     public void visit(Operand operand) {
         operand.visit(this);
     }
 
     private void error(Object object) {
         throw new NotCompilableException("no visitor logic for " + object.getClass().getName() + " in " + getClass().getName());
     }
 
     // standard instructions
     public void AliasInstr(AliasInstr aliasinstr) { error(aliasinstr); }
     public void ArgScopeDepthInstr(ArgScopeDepthInstr instr) { error(instr); }
     public void AttrAssignInstr(AttrAssignInstr attrassigninstr) { error(attrassigninstr); }
     public void BacktickInstr(BacktickInstr instr) { error(instr); }
     public void BEQInstr(BEQInstr beqinstr) { error(beqinstr); }
     public void BFalseInstr(BFalseInstr bfalseinstr) { error(bfalseinstr); }
     public void BlockGivenInstr(BlockGivenInstr blockgiveninstr) { error(blockgiveninstr); }
     public void BNEInstr(BNEInstr bneinstr) { error(bneinstr); }
     public void BNilInstr(BNilInstr bnilinstr) { error(bnilinstr); }
     public void BreakInstr(BreakInstr breakinstr) { error(breakinstr); }
     public void BTrueInstr(BTrueInstr btrueinstr) { error(btrueinstr); }
     public void BUndefInstr(BUndefInstr bundefinstr) { error(bundefinstr); }
     public void BuildCompoundArrayInstr(BuildCompoundArrayInstr instr) { error(instr); }
     public void BuildCompoundStringInstr(BuildCompoundStringInstr instr) { error(instr); }
     public void BuildDynRegExpInstr(BuildDynRegExpInstr instr) { error(instr); }
     public void BuildRangeInstr(BuildRangeInstr instr) { error(instr); }
     public void CallInstr(CallInstr callinstr) { error(callinstr); }
     public void CheckArgsArrayArityInstr(CheckArgsArrayArityInstr checkargsarrayarityinstr) { error(checkargsarrayarityinstr); }
     public void CheckArityInstr(CheckArityInstr checkarityinstr) { error(checkarityinstr); }
     public void ClassSuperInstr(ClassSuperInstr classsuperinstr) { error(classsuperinstr); }
     public void ConstMissingInstr(ConstMissingInstr constmissinginstr) { error(constmissinginstr); }
     public void CopyInstr(CopyInstr copyinstr) { error(copyinstr); }
     public void DefineClassInstr(DefineClassInstr defineclassinstr) { error(defineclassinstr); }
     public void DefineClassMethodInstr(DefineClassMethodInstr defineclassmethodinstr) { error(defineclassmethodinstr); }
     public void DefineInstanceMethodInstr(DefineInstanceMethodInstr defineinstancemethodinstr) { error(defineinstancemethodinstr); }
     public void DefineMetaClassInstr(DefineMetaClassInstr definemetaclassinstr) { error(definemetaclassinstr); }
     public void DefineModuleInstr(DefineModuleInstr definemoduleinstr) { error(definemoduleinstr); }
     public void EQQInstr(EQQInstr eqqinstr) { error(eqqinstr); }
     public void ExceptionRegionEndMarkerInstr(ExceptionRegionEndMarkerInstr exceptionregionendmarkerinstr) { error(exceptionregionendmarkerinstr); }
     public void ExceptionRegionStartMarkerInstr(ExceptionRegionStartMarkerInstr exceptionregionstartmarkerinstr) { error(exceptionregionstartmarkerinstr); }
     public void GetClassVarContainerModuleInstr(GetClassVarContainerModuleInstr getclassvarcontainermoduleinstr) { error(getclassvarcontainermoduleinstr); }
     public void GetClassVariableInstr(GetClassVariableInstr getclassvariableinstr) { error(getclassvariableinstr); }
     public void GetFieldInstr(GetFieldInstr getfieldinstr) { error(getfieldinstr); }
     public void GetGlobalVariableInstr(GetGlobalVariableInstr getglobalvariableinstr) { error(getglobalvariableinstr); }
     public void GVarAliasInstr(GVarAliasInstr gvaraliasinstr) { error(gvaraliasinstr); }
     public void InheritanceSearchConstInstr(InheritanceSearchConstInstr inheritancesearchconstinstr) { error(inheritancesearchconstinstr); }
     public void InstanceSuperInstr(InstanceSuperInstr instancesuperinstr) { error(instancesuperinstr); }
     public void Instr(Instr instr) { error(instr); }
     public void JumpInstr(JumpInstr jumpinstr) { error(jumpinstr); }
     public void LabelInstr(LabelInstr labelinstr) { error(labelinstr); }
     public void LexicalSearchConstInstr(LexicalSearchConstInstr lexicalsearchconstinstr) { error(lexicalsearchconstinstr); }
     public void LineNumberInstr(LineNumberInstr linenumberinstr) { error(linenumberinstr); }
     public void LoadLocalVarInstr(LoadLocalVarInstr loadlocalvarinstr) { error(loadlocalvarinstr); }
     public void Match2Instr(Match2Instr match2instr) { error(match2instr); }
     public void Match3Instr(Match3Instr match3instr) { error(match3instr); }
     public void MatchInstr(MatchInstr matchinstr) { error(matchinstr); }
     public void MethodLookupInstr(MethodLookupInstr methodlookupinstr) { error(methodlookupinstr); }
     public void ModuleVersionGuardInstr(ModuleVersionGuardInstr moduleversionguardinstr) { error(moduleversionguardinstr); }
     public void NonlocalReturnInstr(NonlocalReturnInstr nonlocalreturninstr) { error(nonlocalreturninstr); }
     public void NopInstr(NopInstr nopinstr) { error(nopinstr); }
     public void NoResultCallInstr(NoResultCallInstr noresultcallinstr) { error(noresultcallinstr); }
     public void OptArgMultipleAsgnInstr(OptArgMultipleAsgnInstr optargmultipleasgninstr) { error(optargmultipleasgninstr); }
     public void PopBindingInstr(PopBindingInstr popbindinginstr) { error(popbindinginstr); }
     public void PopFrameInstr(PopFrameInstr popframeinstr) { error(popframeinstr); }
     public void ProcessModuleBodyInstr(ProcessModuleBodyInstr processmodulebodyinstr) { error(processmodulebodyinstr); }
     public void PutClassVariableInstr(PutClassVariableInstr putclassvariableinstr) { error(putclassvariableinstr); }
     public void PutConstInstr(PutConstInstr putconstinstr) { error(putconstinstr); }
     public void PutFieldInstr(PutFieldInstr putfieldinstr) { error(putfieldinstr); }
     public void PutGlobalVarInstr(PutGlobalVarInstr putglobalvarinstr) { error(putglobalvarinstr); }
     public void PushBindingInstr(PushBindingInstr pushbindinginstr) { error(pushbindinginstr); }
     public void PushFrameInstr(PushFrameInstr pushframeinstr) { error(pushframeinstr); }
     public void RaiseArgumentErrorInstr(RaiseArgumentErrorInstr raiseargumenterrorinstr) { error(raiseargumenterrorinstr); }
     public void RaiseRequiredKeywordArgumentErrorInstr(RaiseRequiredKeywordArgumentError instr) { error(instr); }
     public void ReceiveClosureInstr(ReceiveClosureInstr receiveclosureinstr) { error(receiveclosureinstr); }
     public void ReceiveRubyExceptionInstr(ReceiveRubyExceptionInstr receiveexceptioninstr) { error(receiveexceptioninstr); }
     public void ReceiveJRubyExceptionInstr(ReceiveJRubyExceptionInstr receiveexceptioninstr) { error(receiveexceptioninstr); }
     public void ReceiveKeywordArgInstr(ReceiveKeywordArgInstr receiveKeywordArgInstr) { error(receiveKeywordArgInstr); }
     public void ReceiveKeywordRestArgInstr(ReceiveKeywordRestArgInstr receiveKeywordRestArgInstr) { error(receiveKeywordRestArgInstr); }
     public void ReceiveOptArgInstr(ReceiveOptArgInstr receiveoptarginstr) { error(receiveoptarginstr); }
     public void ReceivePreReqdArgInstr(ReceivePreReqdArgInstr receiveprereqdarginstr) { error(receiveprereqdarginstr); }
     public void ReceiveRestArgInstr(ReceiveRestArgInstr receiverestarginstr) { error(receiverestarginstr); }
     public void ReceiveSelfInstr(ReceiveSelfInstr receiveselfinstr) { error(receiveselfinstr); }
     public void RecordEndBlockInstr(RecordEndBlockInstr recordendblockinstr) { error(recordendblockinstr); }
     public void ReqdArgMultipleAsgnInstr(ReqdArgMultipleAsgnInstr reqdargmultipleasgninstr) { error(reqdargmultipleasgninstr); }
     public void RescueEQQInstr(RescueEQQInstr rescueeqqinstr) { error(rescueeqqinstr); }
     public void RestArgMultipleAsgnInstr(RestArgMultipleAsgnInstr restargmultipleasgninstr) { error(restargmultipleasgninstr); }
     public void ReturnInstr(ReturnInstr returninstr) { error(returninstr); }
     public void RuntimeHelperCall(RuntimeHelperCall runtimehelpercall) { error(runtimehelpercall); }
     public void SearchConstInstr(SearchConstInstr searchconstinstr) { error(searchconstinstr); }
     public void SetCapturedVarInstr(SetCapturedVarInstr instr) { error(instr); }
     public void StoreLocalVarInstr(StoreLocalVarInstr storelocalvarinstr) { error(storelocalvarinstr); }
     public void ThreadPollInstr(ThreadPollInstr threadpollinstr) { error(threadpollinstr); }
     public void ThrowExceptionInstr(ThrowExceptionInstr throwexceptioninstr) { error(throwexceptioninstr); }
     public void ToAryInstr(ToAryInstr toaryinstr) { error(toaryinstr); }
     public void UndefMethodInstr(UndefMethodInstr undefmethodinstr) { error(undefmethodinstr); }
     public void UnresolvedSuperInstr(UnresolvedSuperInstr unresolvedsuperinstr) { error(unresolvedsuperinstr); }
     public void YieldInstr(YieldInstr yieldinstr) { error(yieldinstr); }
     public void ZSuperInstr(ZSuperInstr zsuperinstr) { error(zsuperinstr); }
 
     // "defined" instructions
     public void GetErrorInfoInstr(GetErrorInfoInstr geterrorinfoinstr) { error(geterrorinfoinstr); }
     public void RestoreErrorInfoInstr(RestoreErrorInfoInstr restoreerrorinfoinstr) { error(restoreerrorinfoinstr); }
 
     // ruby 1.9 specific
     public void BuildLambdaInstr(BuildLambdaInstr buildlambdainstr) { error(buildlambdainstr); }
     public void GetEncodingInstr(GetEncodingInstr getencodinginstr) { error(getencodinginstr); }
     public void ReceivePostReqdArgInstr(ReceivePostReqdArgInstr receivepostreqdarginstr) { error(receivepostreqdarginstr); }
 
     // unboxing instrs
     public void BoxFloatInstr(BoxFloatInstr instr) { error(instr); }
     public void BoxFixnumInstr(BoxFixnumInstr instr) { error(instr); }
     public void BoxBooleanInstr(BoxBooleanInstr instr) { error(instr); }
     public void AluInstr(AluInstr instr) { error(instr); }
     public void UnboxFloatInstr(UnboxFloatInstr instr) { error(instr); }
     public void UnboxFixnumInstr(UnboxFixnumInstr instr) { error(instr); }
     public void UnboxBooleanInstr(UnboxBooleanInstr instr) { error(instr); }
 
     // operands
     public void Array(Array array) { error(array); }
     public void AsString(AsString asstring) { error(asstring); }
     public void Backref(Backref backref) { error(backref); }
     public void Bignum(Bignum bignum) { error(bignum); }
     public void Boolean(Boolean bool) { error(bool); }
     public void UnboxedBoolean(UnboxedBoolean bool) { error(bool); }
     public void ClosureLocalVariable(ClosureLocalVariable closurelocalvariable) { error(closurelocalvariable); }
     public void Complex(Complex complex) { error(complex); }
     public void CurrentScope(CurrentScope currentscope) { error(currentscope); }
     public void DynamicSymbol(DynamicSymbol dynamicsymbol) { error(dynamicsymbol); }
     public void Fixnum(Fixnum fixnum) { error(fixnum); }
     public void UnboxedFixnum(UnboxedFixnum fixnum) { error(fixnum); }
     public void Float(org.jruby.ir.operands.Float flote) { error(flote); }
     public void UnboxedFloat(org.jruby.ir.operands.UnboxedFloat flote) { error(flote); }
     public void GlobalVariable(GlobalVariable globalvariable) { error(globalvariable); }
     public void Hash(Hash hash) { error(hash); }
     public void IRException(IRException irexception) { error(irexception); }
     public void Label(Label label) { error(label); }
     public void LocalVariable(LocalVariable localvariable) { error(localvariable); }
     public void MethAddr(MethAddr methaddr) { error(methaddr); }
     public void MethodHandle(MethodHandle methodhandle) { error(methodhandle); }
     public void Nil(Nil nil) { error(nil); }
     public void NthRef(NthRef nthref) { error(nthref); }
+    public void Null(Null nul) { error(nul); }
     public void ObjectClass(ObjectClass objectclass) { error(objectclass); }
     public void Rational(Rational rational) { error(rational); }
     public void Regexp(Regexp regexp) { error(regexp); }
     public void ScopeModule(ScopeModule scopemodule) { error(scopemodule); }
     public void Self(Self self) { error(self); }
     public void Splat(Splat splat) { error(splat); }
     public void StandardError(StandardError standarderror) { error(standarderror); }
     public void StringLiteral(StringLiteral stringliteral) { error(stringliteral); }
     public void SValue(SValue svalue) { error(svalue); }
     public void Symbol(Symbol symbol) { error(symbol); }
     public void TemporaryVariable(TemporaryVariable temporaryvariable) { error(temporaryvariable); }
     public void TemporaryLocalVariable(TemporaryLocalVariable temporarylocalvariable) { error(temporarylocalvariable); }
     public void TemporaryFloatVariable(TemporaryFloatVariable temporaryfloatvariable) { error(temporaryfloatvariable); }
     public void TemporaryFixnumVariable(TemporaryFixnumVariable temporaryfixnumvariable) { error(temporaryfixnumvariable); }
     public void TemporaryBooleanVariable(TemporaryBooleanVariable temporarybooleanvariable) { error(temporarybooleanvariable); }
     public void UndefinedValue(UndefinedValue undefinedvalue) { error(undefinedvalue); }
     public void UnexecutableNil(UnexecutableNil unexecutablenil) { error(unexecutablenil); }
     public void WrappedIRClosure(WrappedIRClosure wrappedirclosure) { error(wrappedirclosure); }
 }
diff --git a/core/src/main/java/org/jruby/ir/operands/Null.java b/core/src/main/java/org/jruby/ir/operands/Null.java
new file mode 100644
index 0000000000..8ff4b1dda7
--- /dev/null
+++ b/core/src/main/java/org/jruby/ir/operands/Null.java
@@ -0,0 +1,42 @@
+package org.jruby.ir.operands;
+
+import org.jruby.ir.IRVisitor;
+import org.jruby.ir.JIT;
+import org.jruby.runtime.ThreadContext;
+
+/**
+ * Used by JIT to represent a JVM null.
+ */
+@JIT
+public class Null extends ImmutableLiteral {
+    public static final Null INSTANCE = new Null();
+
+    Null() {
+        super(OperandType.NULL);
+    }
+
+    @Override
+    public Object createCacheObject(ThreadContext context) {
+        return null;
+    }
+
+    @Override
+    public int hashCode() {
+        return 0;
+    }
+
+    @Override
+    public boolean equals(Object other) {
+        return other instanceof Null;
+    }
+
+    @Override
+    public void visit(IRVisitor visitor) {
+        visitor.Null(this);
+    }
+
+    @Override
+    public String toString() {
+        return "Null";
+    }
+}
diff --git a/core/src/main/java/org/jruby/ir/operands/OperandType.java b/core/src/main/java/org/jruby/ir/operands/OperandType.java
index 394fd92ddd..7a155375ab 100644
--- a/core/src/main/java/org/jruby/ir/operands/OperandType.java
+++ b/core/src/main/java/org/jruby/ir/operands/OperandType.java
@@ -1,82 +1,83 @@
 /*
  * To change this license header, choose License Headers in Project Properties.
  * To change this template file, choose Tools | Templates
  * and open the template in the editor.
  */
 package org.jruby.ir.operands;
 
 public enum OperandType {
 
     ARRAY((byte) 'A'),
     AS_STRING((byte) 'a'),
     BACKREF((byte) '\\'),
     BACKTICK_STRING((byte) '`'),
     BIGNUM((byte) 'B'),
     BOOLEAN((byte) 'b'),
     LOCAL_VARIABLE((byte) 'l'), // Also applicable for ClosureLocalVariable
     COMPLEX((byte) 'C'),
     COMPOUND_ARRAY((byte) 'c'),
     COMPOUND_STRING((byte) '"'),
     CURRENT_SCOPE((byte) 's'),
     DYNAMIC_SYMBOL((byte) 'd'),
     FIXNUM((byte) 'f'),
     FLOAT((byte) 'F'),
     GLOBAL_VARIABLE((byte) '$'),
     HASH((byte) '{'),
     IR_EXCEPTION((byte) '!'),
     LABEL((byte) 'L'),
     METH_ADDR((byte) 'M'),
     METHOD_HANDLE((byte) 'm'),
     NIL((byte) 'N'),
     NTH_REF((byte) '1'),
+    NULL((byte) '0'),
     OBJECT_CLASS((byte) 'O'),
     RANGE((byte) '.'),
     RATIONAL((byte) 'r'),
     REGEXP((byte) '/'),
     SCOPE_MODULE((byte) '_'),
     SELF((byte) 'S'),
     SPLAT((byte) '*'),
     STANDARD_ERROR((byte) 'E'),
     STRING_LITERAL((byte) '\''),
     SVALUE((byte) 'V'),
     SYMBOL((byte) ':'),
     TEMPORARY_VARIABLE((byte) 't'),
     UNBOXED_BOOLEAN((byte) 'v'),
     UNBOXED_FIXNUM((byte) 'j'),
     UNBOXED_FLOAT((byte) 'J'),
     UNDEFINED_VALUE((byte) 'u'),
     UNEXECUTABLE_NIL((byte) 'n'),
     WRAPPED_IR_CLOSURE((byte) 'w'),
     ;
 
     private final byte coded;
     private static final OperandType[] byteToOperand = new OperandType[256];
 
     OperandType(byte coded) {
         this.coded = coded;
     }
 
     public byte getCoded() {
         return coded;
     }
 
     @Override
     public String toString() {
         return name().toLowerCase();
     };
 
     public static OperandType fromCoded(byte coded) {
         return byteToOperand[coded];
     }
 
     public static OperandType fromOrdinal(int value) {
         return value < 0 || value >= values().length ? null : values()[value];
     }
 
     static {
         for (OperandType type : values()) {
             byteToOperand[type.coded] = type;
         }
     }
 }
 
diff --git a/core/src/main/java/org/jruby/ir/targets/ClassSuperInvokeSite.java b/core/src/main/java/org/jruby/ir/targets/ClassSuperInvokeSite.java
index e8a6e9f596..ba39a09cfd 100644
--- a/core/src/main/java/org/jruby/ir/targets/ClassSuperInvokeSite.java
+++ b/core/src/main/java/org/jruby/ir/targets/ClassSuperInvokeSite.java
@@ -1,32 +1,40 @@
 package org.jruby.ir.targets;
 
 import org.jruby.RubyClass;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callsite.CacheEntry;
 
 import java.lang.invoke.MethodHandle;
 import java.lang.invoke.MethodType;
 import java.lang.invoke.SwitchPoint;
 
 import static org.jruby.ir.runtime.IRRuntimeHelpers.splatArguments;
 
 /**
 * Created by headius on 10/23/14.
 */
 public class ClassSuperInvokeSite extends ResolvedSuperInvokeSite {
     public ClassSuperInvokeSite(MethodType type, String name, String splatmapString) {
         super(type, name, splatmapString);
     }
 
     @Override
     protected RubyClass getSuperClass(RubyClass definingModule) {
         return definingModule.getMetaClass().getMetaClass().getSuperClass();
     }
+
+    // FIXME: indy cached version was not doing splat mapping; revert to slow logic for now
+
+    public IRubyObject invoke(ThreadContext context, IRubyObject caller, IRubyObject self, RubyClass definingModule, IRubyObject[] args, Block block) throws Throwable {
+        // TODO: get rid of caller
+        // TODO: caching
+        return IRRuntimeHelpers.classSuperSplatArgs(context, self, superName, definingModule, args, block, splatMap);
+    }
 }
diff --git a/core/src/main/java/org/jruby/ir/targets/InstanceSuperInvokeSite.java b/core/src/main/java/org/jruby/ir/targets/InstanceSuperInvokeSite.java
index 7567ccced8..cd3478cf89 100644
--- a/core/src/main/java/org/jruby/ir/targets/InstanceSuperInvokeSite.java
+++ b/core/src/main/java/org/jruby/ir/targets/InstanceSuperInvokeSite.java
@@ -1,33 +1,41 @@
 package org.jruby.ir.targets;
 
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callsite.CacheEntry;
 
 import java.lang.invoke.MethodHandle;
 import java.lang.invoke.MethodType;
 import java.lang.invoke.SwitchPoint;
 
 import static org.jruby.ir.runtime.IRRuntimeHelpers.splatArguments;
 
 /**
 * Created by headius on 10/23/14.
 */
 public class InstanceSuperInvokeSite extends ResolvedSuperInvokeSite {
     public InstanceSuperInvokeSite(MethodType type, String name, String splatmapString) {
         super(type, name, splatmapString);
     }
 
     @Override
     protected RubyClass getSuperClass(RubyClass definingModule) {
         return definingModule.getSuperClass();
     }
+
+    // FIXME: indy cached version was not doing splat mapping; revert to slow logic for now
+
+    public IRubyObject invoke(ThreadContext context, IRubyObject caller, IRubyObject self, RubyClass definingModule, IRubyObject[] args, Block block) throws Throwable {
+        // TODO: get rid of caller
+        // TODO: caching
+        return IRRuntimeHelpers.instanceSuperSplatArgs(context, self, superName, definingModule, args, block, splatMap);
+    }
 }
diff --git a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
index 9090b5bf5f..91d8d0c522 100644
--- a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+++ b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
@@ -1,2170 +1,2122 @@
 package org.jruby.ir.targets;
 
 import com.headius.invokebinder.Signature;
 import org.jruby.*;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.ir.*;
 import org.jruby.ir.instructions.*;
 import org.jruby.ir.instructions.boxing.*;
 import org.jruby.ir.instructions.defined.GetErrorInfoInstr;
 import org.jruby.ir.instructions.defined.RestoreErrorInfoInstr;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.operands.Boolean;
 import org.jruby.ir.operands.Float;
 import org.jruby.ir.operands.GlobalVariable;
 import org.jruby.ir.operands.Label;
 import org.jruby.ir.operands.MethodHandle;
 import org.jruby.ir.representations.BasicBlock;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.ClassDefiningClassLoader;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.KeyValuePair;
 import org.jruby.util.RegexpOptions;
 import org.jruby.util.cli.Options;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 import org.objectweb.asm.Handle;
 import org.objectweb.asm.Opcodes;
 import org.objectweb.asm.Type;
 import org.objectweb.asm.commons.Method;
 
 import java.lang.invoke.MethodType;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import static org.jruby.util.CodegenUtils.*;
 
 /**
  * Implementation of IRCompiler for the JVM.
  */
 public class JVMVisitor extends IRVisitor {
 
     private static final Logger LOG = LoggerFactory.getLogger("JVMVisitor");
     public static final String DYNAMIC_SCOPE = "$dynamicScope";
     private static final boolean DEBUG = false;
 
     public JVMVisitor() {
         this.jvm = Options.COMPILE_INVOKEDYNAMIC.load() ? new JVM7() : new JVM6();
         this.methodIndex = 0;
         this.scopeMap = new HashMap();
     }
 
     public Class compile(IRScope scope, ClassDefiningClassLoader jrubyClassLoader) {
         return defineFromBytecode(scope, compileToBytecode(scope), jrubyClassLoader);
     }
 
     public byte[] compileToBytecode(IRScope scope) {
         codegenScope(scope);
 
 //        try {
 //            FileOutputStream fos = new FileOutputStream("tmp.class");
 //            fos.write(target.code());
 //            fos.close();
 //        } catch (Exception e) {
 //            e.printStackTrace();
 //        }
 
         return code();
     }
 
     public Class defineFromBytecode(IRScope scope, byte[] code, ClassDefiningClassLoader jrubyClassLoader) {
         Class result = jrubyClassLoader.defineClass(c(JVM.scriptToClass(scope.getFileName())), code);
 
         for (Map.Entry<String, IRScope> entry : scopeMap.entrySet()) {
             try {
                 result.getField(entry.getKey()).set(null, entry.getValue());
             } catch (Exception e) {
                 throw new NotCompilableException(e);
             }
         }
 
         return result;
     }
 
     public byte[] code() {
         return jvm.code();
     }
 
     public void codegenScope(IRScope scope) {
         if (scope instanceof IRScriptBody) {
             codegenScriptBody((IRScriptBody)scope);
         } else if (scope instanceof IRMethod) {
             emitMethodJIT((IRMethod)scope);
         } else if (scope instanceof IRModuleBody) {
             emitModuleBodyJIT((IRModuleBody)scope);
         } else {
             throw new NotCompilableException("don't know how to JIT: " + scope);
         }
     }
 
     public void codegenScriptBody(IRScriptBody script) {
         emitScriptBody(script);
     }
 
     private void logScope(IRScope scope) {
         StringBuilder b = new StringBuilder();
 
         b.append("\n\nLinearized instructions for JIT:\n");
 
         int i = 0;
         for (BasicBlock bb : scope.buildLinearization()) {
             for (Instr instr : bb.getInstrsArray()) {
                 if (i > 0) b.append("\n");
 
                 b.append("  ").append(i).append('\t').append(instr);
 
                 i++;
             }
         }
 
         LOG.info("Starting JVM compilation on scope " + scope);
         LOG.info(b.toString());
     }
 
     public void emitScope(IRScope scope, String name, Signature signature, boolean specificArity) {
         List <BasicBlock> bbs = scope.prepareForCompilation();
 
         Map <BasicBlock, Label> exceptionTable = scope.buildJVMExceptionTable();
 
         if (Options.IR_COMPILER_DEBUG.load()) logScope(scope);
 
         emitClosures(scope);
 
         jvm.pushmethod(name, scope, signature, specificArity);
 
         // store IRScope in map for insertion into class later
         String scopeField = name + "_IRScope";
         if (scopeMap.get(scopeField) == null) {
             scopeMap.put(scopeField, scope);
             jvm.cls().visitField(Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC | Opcodes.ACC_VOLATILE, scopeField, ci(IRScope.class), null, null).visitEnd();
         }
 
         // Some scopes (closures, module/class bodies) do not have explicit call protocol yet.
         // Unconditionally load current dynamic scope for those bodies.
         // FIXME: If I don't load this there are some scopes that end up trying to use it before it is there
         // Try uncommenting and running compiler specs.
 //        if (!scope.hasExplicitCallProtocol()) {
             jvmMethod().loadContext();
             jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("org.jruby.runtime.DynamicScope getCurrentScope()"));
             jvmStoreLocal(DYNAMIC_SCOPE);
 //        }
 
         IRBytecodeAdapter m = jvmMethod();
 
         int numberOfLabels = bbs.size();
         int ipc = 0; // synthetic, used for debug traces that show which instr failed
         for (int i = 0; i < numberOfLabels; i++) {
             BasicBlock bb = bbs.get(i);
             org.objectweb.asm.Label start = jvm.methodData().getLabel(bb.getLabel());
             Label rescueLabel = exceptionTable.get(bb);
             org.objectweb.asm.Label end = null;
 
             m.mark(start);
 
             boolean newEnd = false;
             if (rescueLabel != null) {
                 if (i+1 < numberOfLabels) {
                     end = jvm.methodData().getLabel(bbs.get(i+1).getLabel());
                 } else {
                     newEnd = true;
                     end = new org.objectweb.asm.Label();
                 }
 
                 org.objectweb.asm.Label rescue = jvm.methodData().getLabel(rescueLabel);
                 jvmAdapter().trycatch(start, end, rescue, p(Throwable.class));
             }
 
             // ensure there's at least one instr per block
             m.adapter.nop();
 
             // visit remaining instrs
             for (Instr instr : bb.getInstrs()) {
                 if (DEBUG) instr.setIPC(ipc++); // debug mode uses instr offset for backtrace
                 visit(instr);
             }
 
             if (newEnd) {
                 m.mark(end);
             }
         }
 
         jvm.popmethod();
     }
 
     private static final Signature METHOD_SIGNATURE_BASE = Signature
             .returning(IRubyObject.class)
             .appendArgs(new String[]{"context", "scope", "self", "block", "class"}, ThreadContext.class, StaticScope.class, IRubyObject.class, Block.class, RubyModule.class);
 
     public static final Signature signatureFor(IRScope method, boolean aritySplit) {
         if (aritySplit) {
             StaticScope argScope = method.getStaticScope();
             if (argScope.isArgumentScope() &&
                     argScope.getOptionalArgs() == 0 &&
                     argScope.getRestArg() == -1 &&
                     !method.receivesKeywordArgs()) {
                 // we have only required arguments...emit a signature appropriate to that arity
                 String[] args = new String[argScope.getRequiredArgs()];
                 Class[] types = Helpers.arrayOf(Class.class, args.length, IRubyObject.class);
                 for (int i = 0; i < args.length; i++) {
                     args[i] = "arg" + i;
                 }
                 return METHOD_SIGNATURE_BASE.insertArgs(3, args, types);
             }
             // we can't do an specific-arity signature
             return null;
         }
 
         // normal boxed arg list signature
         return METHOD_SIGNATURE_BASE.insertArgs(3, new String[]{"args"}, IRubyObject[].class);
     }
 
     private static final Signature CLOSURE_SIGNATURE = Signature
             .returning(IRubyObject.class)
             .appendArgs(new String[]{"context", "scope", "self", "args", "block", "superName", "type"}, ThreadContext.class, StaticScope.class, IRubyObject.class, IRubyObject[].class, Block.class, String.class, Block.Type.class);
 
     public void emitScriptBody(IRScriptBody script) {
         String clsName = jvm.scriptToClass(script.getFileName());
         jvm.pushscript(clsName, script.getFileName());
 
         emitScope(script, "__script__", signatureFor(script, false), false);
 
         jvm.cls().visitEnd();
         jvm.popclass();
     }
 
     public void emitMethod(IRMethod method) {
         String name = JavaNameMangler.mangleMethodName(method.getName() + "_" + methodIndex++);
 
         emitWithSignatures(method, name);
     }
 
     public void  emitMethodJIT(IRMethod method) {
         String clsName = jvm.scriptToClass(method.getFileName());
         jvm.pushscript(clsName, method.getFileName());
 
         emitWithSignatures(method, "__script__");
 
         jvm.cls().visitEnd();
         jvm.popclass();
     }
 
     private void emitWithSignatures(IRMethod method, String name) {
         method.setJittedName(name);
 
         Signature signature = signatureFor(method, false);
         emitScope(method, name, signature, false);
         method.addNativeSignature(-1, signature.type());
 
         Signature specificSig = signatureFor(method, true);
         if (specificSig != null) {
             emitScope(method, name, specificSig, true);
             method.addNativeSignature(method.getStaticScope().getRequiredArgs(), specificSig.type());
         }
     }
 
     public Handle emitModuleBodyJIT(IRModuleBody method) {
         String baseName = method.getName() + "_" + methodIndex++;
         String name;
 
         if (baseName.indexOf("DUMMY_MC") != -1) {
             name = "METACLASS_" + methodIndex++;
         } else {
             name = baseName + "_" + methodIndex++;
         }
         String clsName = jvm.scriptToClass(method.getFileName());
         jvm.pushscript(clsName, method.getFileName());
 
         Signature signature = signatureFor(method, false);
         emitScope(method, "__script__", signature, false);
 
         Handle handle = new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(signature.type().returnType(), signature.type().parameterArray()));
 
         jvm.cls().visitEnd();
         jvm.popclass();
 
         return handle;
     }
 
     private void emitClosures(IRScope s) {
         // Emit code for all nested closures
         for (IRClosure c: s.getClosures()) {
             c.setHandle(emitClosure(c));
         }
     }
 
     public Handle emitClosure(IRClosure closure) {
         /* Compile the closure like a method */
         String name = JavaNameMangler.mangleMethodName(closure.getName() + "__" + closure.getLexicalParent().getName() + "_" + methodIndex++);
 
         emitScope(closure, name, CLOSURE_SIGNATURE, false);
 
         return new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(CLOSURE_SIGNATURE.type().returnType(), CLOSURE_SIGNATURE.type().parameterArray()));
     }
 
     public Handle emitModuleBody(IRModuleBody method) {
         String baseName = method.getName() + "_" + methodIndex++;
         String name;
 
         if (baseName.indexOf("DUMMY_MC") != -1) {
             name = "METACLASS_" + methodIndex++;
         } else {
             name = baseName + "_" + methodIndex++;
         }
 
         Signature signature = signatureFor(method, false);
         emitScope(method, name, signature, false);
 
         return new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(signature.type().returnType(), signature.type().parameterArray()));
     }
 
     public void visit(Instr instr) {
         if (DEBUG) { // debug will skip emitting actual file line numbers
             jvmAdapter().line(instr.getIPC());
         }
         instr.visit(this);
     }
 
     public void visit(Operand operand) {
         operand.visit(this);
     }
 
     private int getJVMLocalVarIndex(Variable variable) {
         if (variable instanceof TemporaryLocalVariable) {
             switch (((TemporaryLocalVariable)variable).getType()) {
             case FLOAT: return jvm.methodData().local(variable, JVM.DOUBLE_TYPE);
             case FIXNUM: return jvm.methodData().local(variable, JVM.LONG_TYPE);
             case BOOLEAN: return jvm.methodData().local(variable, JVM.BOOLEAN_TYPE);
             default: return jvm.methodData().local(variable);
             }
         } else {
             return jvm.methodData().local(variable);
         }
     }
 
     private int getJVMLocalVarIndex(String specialVar) {
         return jvm.methodData().local(specialVar);
     }
 
     private org.objectweb.asm.Label getJVMLabel(Label label) {
         return jvm.methodData().getLabel(label);
     }
 
     private void jvmStoreLocal(Variable variable) {
         if (variable instanceof TemporaryLocalVariable) {
             switch (((TemporaryLocalVariable)variable).getType()) {
             case FLOAT: jvmAdapter().dstore(getJVMLocalVarIndex(variable)); break;
             case FIXNUM: jvmAdapter().lstore(getJVMLocalVarIndex(variable)); break;
             case BOOLEAN: jvmAdapter().istore(getJVMLocalVarIndex(variable)); break;
             default: jvmMethod().storeLocal(getJVMLocalVarIndex(variable)); break;
             }
         } else {
             jvmMethod().storeLocal(getJVMLocalVarIndex(variable));
         }
     }
 
     private void jvmStoreLocal(String specialVar) {
         jvmMethod().storeLocal(getJVMLocalVarIndex(specialVar));
     }
 
     private void jvmLoadLocal(Variable variable) {
         if (variable instanceof TemporaryLocalVariable) {
             switch (((TemporaryLocalVariable)variable).getType()) {
             case FLOAT: jvmAdapter().dload(getJVMLocalVarIndex(variable)); break;
             case FIXNUM: jvmAdapter().lload(getJVMLocalVarIndex(variable)); break;
             case BOOLEAN: jvmAdapter().iload(getJVMLocalVarIndex(variable)); break;
             default: jvmMethod().loadLocal(getJVMLocalVarIndex(variable)); break;
             }
         } else {
             jvmMethod().loadLocal(getJVMLocalVarIndex(variable));
         }
     }
 
     private void jvmLoadLocal(String specialVar) {
         jvmMethod().loadLocal(getJVMLocalVarIndex(specialVar));
     }
 
     // JVM maintains a stack of ClassData (for nested classes being compiled)
     // Each class maintains a stack of MethodData (for methods being compiled in the class)
     // MethodData wraps a IRBytecodeAdapter which wraps a SkinnyMethodAdapter which has a ASM MethodVisitor which emits bytecode
     // A long chain of indirection: JVM -> MethodData -> IRBytecodeAdapter -> SkinnyMethodAdapter -> ASM.MethodVisitor
     // In some places, methods reference JVM -> MethodData -> IRBytecodeAdapter (via jvm.method()) and ask it to walk the last 2 links
     // In other places, methods reference JVM -> MethodData -> IRBytecodeAdapter -> SkinnyMethodAdapter (via jvm.method().adapter) and ask it to walk the last link
     // Can this be cleaned up to either (a) get rid of IRBytecodeAdapter OR (b) implement passthru' methods for SkinnyMethodAdapter methods (like the others it implements)?
 
     // SSS FIXME: Needs an update to reflect instr. change
     @Override
     public void AliasInstr(AliasInstr aliasInstr) {
         IRBytecodeAdapter m = jvm.method();
         m.loadContext();
         m.loadSelf();
         jvmLoadLocal(DYNAMIC_SCOPE);
         // CON FIXME: Ideally this would not have to pass through RubyString and toString
         visit(aliasInstr.getNewName());
         jvmAdapter().invokevirtual(p(Object.class), "toString", sig(String.class));
         visit(aliasInstr.getOldName());
         jvmAdapter().invokevirtual(p(Object.class), "toString", sig(String.class));
         m.invokeIRHelper("defineAlias", sig(void.class, ThreadContext.class, IRubyObject.class, DynamicScope.class, String.class, String.class));
     }
 
     @Override
     public void AttrAssignInstr(AttrAssignInstr attrAssignInstr) {
         compileCallCommon(
                 jvmMethod(),
                 attrAssignInstr.getMethodAddr().getName(),
                 attrAssignInstr.getCallArgs(),
                 attrAssignInstr.getReceiver(),
                 attrAssignInstr.getCallArgs().length,
                 null,
                 false,
                 attrAssignInstr.getReceiver() instanceof Self ? CallType.FUNCTIONAL : CallType.NORMAL,
                 null);
     }
 
     @Override
     public void BEQInstr(BEQInstr beqInstr) {
         jvmMethod().loadContext();
         visit(beqInstr.getArg1());
         visit(beqInstr.getArg2());
         jvmMethod().invokeHelper("BEQ", boolean.class, ThreadContext.class, IRubyObject.class, IRubyObject.class);
         jvmAdapter().iftrue(getJVMLabel(beqInstr.getJumpTarget()));
     }
 
     @Override
     public void BFalseInstr(BFalseInstr bFalseInstr) {
         Operand arg1 = bFalseInstr.getArg1();
         visit(arg1);
         // this is a gross hack because we don't have distinction in boolean instrs between boxed and unboxed
         if (!(arg1 instanceof TemporaryBooleanVariable) && !(arg1 instanceof UnboxedBoolean)) {
             // unbox
             jvmAdapter().invokeinterface(p(IRubyObject.class), "isTrue", sig(boolean.class));
         }
         jvmMethod().bfalse(getJVMLabel(bFalseInstr.getJumpTarget()));
     }
 
     @Override
     public void BlockGivenInstr(BlockGivenInstr blockGivenInstr) {
         jvmMethod().loadContext();
         visit(blockGivenInstr.getBlockArg());
         jvmMethod().invokeIRHelper("isBlockGiven", sig(RubyBoolean.class, ThreadContext.class, Object.class));
         jvmStoreLocal(blockGivenInstr.getResult());
     }
 
     private void loadFloatArg(Operand arg) {
         if (arg instanceof Variable) {
             visit(arg);
         } else {
             double val;
             if (arg instanceof Float) {
                 val = ((Float)arg).value;
             } else if (arg instanceof Fixnum) {
                 val = (double)((Fixnum)arg).value;
             } else {
                 // Should not happen -- so, forcing an exception.
                 throw new NotCompilableException("Non-float/fixnum in loadFloatArg!" + arg);
             }
             jvmAdapter().ldc(val);
         }
     }
 
     private void loadFixnumArg(Operand arg) {
         if (arg instanceof Variable) {
             visit(arg);
         } else {
             long val;
             if (arg instanceof Float) {
                 val = (long)((Float)arg).value;
             } else if (arg instanceof Fixnum) {
                 val = ((Fixnum)arg).value;
             } else {
                 // Should not happen -- so, forcing an exception.
                 throw new NotCompilableException("Non-float/fixnum in loadFixnumArg!" + arg);
             }
             jvmAdapter().ldc(val);
         }
     }
 
     private void loadBooleanArg(Operand arg) {
         if (arg instanceof Variable) {
             visit(arg);
         } else {
             boolean val;
             if (arg instanceof UnboxedBoolean) {
                 val = ((UnboxedBoolean)arg).isTrue();
             } else {
                 // Should not happen -- so, forcing an exception.
                 throw new NotCompilableException("Non-float/fixnum in loadFixnumArg!" + arg);
             }
             jvmAdapter().ldc(val);
         }
     }
 
     @Override
     public void BoxFloatInstr(BoxFloatInstr instr) {
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         // Load runtime
         m.loadContext();
         a.getfield(p(ThreadContext.class), "runtime", ci(Ruby.class));
 
         // Get unboxed float
         loadFloatArg(instr.getValue());
 
         // Box the float
         a.invokevirtual(p(Ruby.class), "newFloat", sig(RubyFloat.class, double.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BoxFixnumInstr(BoxFixnumInstr instr) {
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         // Load runtime
         m.loadContext();
         a.getfield(p(ThreadContext.class), "runtime", ci(Ruby.class));
 
         // Get unboxed fixnum
         loadFixnumArg(instr.getValue());
 
         // Box the fixnum
         a.invokevirtual(p(Ruby.class), "newFixnum", sig(RubyFixnum.class, long.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BoxBooleanInstr(BoxBooleanInstr instr) {
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         // Load runtime
         m.loadContext();
         a.getfield(p(ThreadContext.class), "runtime", ci(Ruby.class));
 
         // Get unboxed boolean
         loadBooleanArg(instr.getValue());
 
         // Box the fixnum
         a.invokevirtual(p(Ruby.class), "newBoolean", sig(RubyBoolean.class, boolean.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void UnboxFloatInstr(UnboxFloatInstr instr) {
         // Load boxed value
         visit(instr.getValue());
 
         // Unbox it
         jvmMethod().invokeIRHelper("unboxFloat", sig(double.class, IRubyObject.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void UnboxFixnumInstr(UnboxFixnumInstr instr) {
         // Load boxed value
         visit(instr.getValue());
 
         // Unbox it
         jvmMethod().invokeIRHelper("unboxFixnum", sig(long.class, IRubyObject.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void UnboxBooleanInstr(UnboxBooleanInstr instr) {
         // Load boxed value
         visit(instr.getValue());
 
         // Unbox it
         jvmMethod().invokeIRHelper("unboxBoolean", sig(boolean.class, IRubyObject.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     public void AluInstr(AluInstr instr) {
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         // Load args
         visit(instr.getArg1());
         visit(instr.getArg2());
 
         // Compute result
         switch (instr.getOperation()) {
             case FADD: a.dadd(); break;
             case FSUB: a.dsub(); break;
             case FMUL: a.dmul(); break;
             case FDIV: a.ddiv(); break;
             case FLT: m.invokeIRHelper("flt", sig(boolean.class, double.class, double.class)); break; // annoying to have to do it in a method
             case FGT: m.invokeIRHelper("fgt", sig(boolean.class, double.class, double.class)); break; // annoying to have to do it in a method
             case FEQ: m.invokeIRHelper("feq", sig(boolean.class, double.class, double.class)); break; // annoying to have to do it in a method
             case IADD: a.ladd(); break;
             case ISUB: a.lsub(); break;
             case IMUL: a.lmul(); break;
             case IDIV: a.ldiv(); break;
             case ILT: m.invokeIRHelper("ilt", sig(boolean.class, long.class, long.class)); break; // annoying to have to do it in a method
             case IGT: m.invokeIRHelper("igt", sig(boolean.class, long.class, long.class)); break; // annoying to have to do it in a method
             case IOR: a.lor(); break;
             case IAND: a.land(); break;
             case IXOR: a.lxor(); break;
             case ISHL: a.lshl(); break;
             case ISHR: a.lshr(); break;
             case IEQ: m.invokeIRHelper("ilt", sig(boolean.class, long.class, long.class)); break; // annoying to have to do it in a method
             default: throw new NotCompilableException("UNHANDLED!");
         }
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BacktickInstr(BacktickInstr instr) {
         // prepare for call to "`" below
         jvmMethod().loadContext();
         jvmMethod().loadSelf(); // TODO: remove caller
         jvmMethod().loadSelf();
 
         ByteList csByteList = new ByteList();
         jvmMethod().pushString(csByteList);
 
         for (Operand p : instr.getPieces()) {
             // visit piece and ensure it's a string
             visit(p);
             jvmAdapter().dup();
             org.objectweb.asm.Label after = new org.objectweb.asm.Label();
             jvmAdapter().instance_of(p(RubyString.class));
             jvmAdapter().iftrue(after);
             jvmAdapter().invokevirtual(p(IRubyObject.class), "anyToString", sig(IRubyObject.class));
 
             jvmAdapter().label(after);
             jvmAdapter().invokevirtual(p(RubyString.class), "append", sig(RubyString.class, IRubyObject.class));
         }
 
         // freeze the string
         jvmAdapter().dup();
         jvmAdapter().ldc(true);
         jvmAdapter().invokeinterface(p(IRubyObject.class), "setFrozen", sig(void.class, boolean.class));
 
         // invoke the "`" method on self
         jvmMethod().invokeSelf("`", 1, false);
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
 
     @Override
     public void BTrueInstr(BTrueInstr btrueinstr) {
         Operand arg1 = btrueinstr.getArg1();
         visit(arg1);
         // this is a gross hack because we don't have distinction in boolean instrs between boxed and unboxed
         if (!(arg1 instanceof TemporaryBooleanVariable) && !(arg1 instanceof UnboxedBoolean)) {
             jvmMethod().isTrue();
         }
         jvmMethod().btrue(getJVMLabel(btrueinstr.getJumpTarget()));
     }
 
     @Override
     public void BUndefInstr(BUndefInstr bundefinstr) {
         visit(bundefinstr.getArg1());
         jvmMethod().pushUndefined();
         jvmAdapter().if_acmpeq(getJVMLabel(bundefinstr.getJumpTarget()));
     }
 
     @Override
     public void BuildCompoundArrayInstr(BuildCompoundArrayInstr instr) {
         visit(instr.getAppendingArg());
         if (instr.isArgsPush()) jvmAdapter().checkcast("org/jruby/RubyArray");
         visit(instr.getAppendedArg());
         if (instr.isArgsPush()) {
             jvmMethod().invokeHelper("argsPush", RubyArray.class, RubyArray.class, IRubyObject.class);
         } else {
             jvmMethod().invokeHelper("argsCat", RubyArray.class, IRubyObject.class, IRubyObject.class);
         }
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BuildCompoundStringInstr(BuildCompoundStringInstr compoundstring) {
         ByteList csByteList = new ByteList();
         csByteList.setEncoding(compoundstring.getEncoding());
         jvmMethod().pushString(csByteList);
         for (Operand p : compoundstring.getPieces()) {
             if ((p instanceof StringLiteral) && (compoundstring.isSameEncoding((StringLiteral)p))) {
                 jvmMethod().pushByteList(((StringLiteral)p).bytelist);
                 jvmAdapter().invokevirtual(p(RubyString.class), "cat", sig(RubyString.class, ByteList.class));
             } else {
                 visit(p);
                 jvmAdapter().invokevirtual(p(RubyString.class), "append19", sig(RubyString.class, IRubyObject.class));
             }
         }
         jvmStoreLocal(compoundstring.getResult());
     }
 
     @Override
     public void BuildDynRegExpInstr(BuildDynRegExpInstr instr) {
         final IRBytecodeAdapter m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         RegexpOptions options = instr.getOptions();
         final List<Operand> operands = instr.getPieces();
 
         Runnable r = new Runnable() {
             @Override
             public void run() {
                 m.loadContext();
                 for (int i = 0; i < operands.size(); i++) {
                     Operand operand = operands.get(i);
                     visit(operand);
                 }
             }
         };
 
         m.pushDRegexp(r, options, operands.size());
 
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BuildRangeInstr(BuildRangeInstr instr) {
         jvmMethod().loadRuntime();
         jvmMethod().loadContext();
         visit(instr.getBegin());
         visit(instr.getEnd());
         jvmAdapter().ldc(instr.isExclusive());
         jvmAdapter().invokestatic(p(RubyRange.class), "newRange", sig(RubyRange.class, Ruby.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void CallInstr(CallInstr callInstr) {
         IRBytecodeAdapter m = jvmMethod();
         String name = callInstr.getMethodAddr().getName();
         Operand[] args = callInstr.getCallArgs();
         Operand receiver = callInstr.getReceiver();
         int numArgs = args.length;
         Operand closure = callInstr.getClosureArg(null);
         boolean hasClosure = closure != null;
         CallType callType = callInstr.getCallType();
         Variable result = callInstr.getResult();
 
         compileCallCommon(m, name, args, receiver, numArgs, closure, hasClosure, callType, result);
     }
 
     private void compileCallCommon(IRBytecodeAdapter m, String name, Operand[] args, Operand receiver, int numArgs, Operand closure, boolean hasClosure, CallType callType, Variable result) {
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
             case VARIABLE:
                 m.invokeSelf(name, arity, hasClosure);
                 break;
             case NORMAL:
                 m.invokeOther(name, arity, hasClosure);
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
         jvmAdapter().pushInt(checkargsarrayarityinstr.rest);
         jvmMethod().invokeStatic(Type.getType(Helpers.class), Method.getMethod("void irCheckArgsArrayArity(org.jruby.runtime.ThreadContext, org.jruby.RubyArray, int, int, int)"));
     }
 
     @Override
     public void CheckArityInstr(CheckArityInstr checkarityinstr) {
         if (jvm.methodData().specificArity >= 0) {
             // no arity check in specific arity path
         } else {
             jvmMethod().loadContext();
             jvmMethod().loadArgs();
             jvmAdapter().ldc(checkarityinstr.required);
             jvmAdapter().ldc(checkarityinstr.opt);
             jvmAdapter().ldc(checkarityinstr.rest);
             jvmAdapter().ldc(checkarityinstr.receivesKeywords);
             jvmAdapter().ldc(checkarityinstr.restKey);
             jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "checkArity", sig(void.class, ThreadContext.class, Object[].class, int.class, int.class, int.class, boolean.class, int.class));
         }
     }
 
     @Override
     public void ClassSuperInstr(ClassSuperInstr classsuperinstr) {
-        // TODO: Nearly identical to InstanceSuperInstr
-        IRBytecodeAdapter m = jvmMethod();
         String name = classsuperinstr.getMethodAddr().getName();
         Operand[] args = classsuperinstr.getCallArgs();
-
-        m.loadContext();
-        m.loadSelf(); // TODO: get rid of caller
-        m.loadSelf();
-        visit(classsuperinstr.getDefiningModule());
-
-        // TODO: CON: is this safe?
-        jvmAdapter().checkcast(p(RubyClass.class));
-
-        for (int i = 0; i < args.length; i++) {
-            Operand operand = args[i];
-            visit(operand);
-        }
-
-        // if there's splats, provide a map and let the call site sort it out
-        boolean[] splatMap = IRRuntimeHelpers.buildSplatMap(args, classsuperinstr.containsArgSplat());
-
+        Operand definingModule = classsuperinstr.getDefiningModule();
+        boolean containsArgSplat = classsuperinstr.containsArgSplat();
         Operand closure = classsuperinstr.getClosureArg(null);
-        boolean hasClosure = closure != null;
-        if (hasClosure) {
-            m.loadContext();
-            visit(closure);
-            m.invokeIRHelper("getBlockFromObject", sig(Block.class, ThreadContext.class, Object.class));
-        }
-
-        m.invokeClassSuper(name, args.length, hasClosure, splatMap);
 
-        jvmStoreLocal(classsuperinstr.getResult());
+        superCommon(name, classsuperinstr, args, definingModule, containsArgSplat, closure, classsuperinstr.getResult());
     }
 
     @Override
     public void ConstMissingInstr(ConstMissingInstr constmissinginstr) {
         visit(constmissinginstr.getReceiver());
         jvmAdapter().checkcast("org/jruby/RubyModule");
         jvmMethod().loadContext();
         jvmAdapter().ldc("const_missing");
         jvmMethod().pushSymbol(constmissinginstr.getMissingConst());
         jvmMethod().invokeVirtual(Type.getType(RubyModule.class), Method.getMethod("org.jruby.runtime.builtin.IRubyObject callMethod(org.jruby.runtime.ThreadContext, java.lang.String, org.jruby.runtime.builtin.IRubyObject)"));
         jvmStoreLocal(constmissinginstr.getResult());
     }
 
     @Override
     public void CopyInstr(CopyInstr copyinstr) {
         Operand  src = copyinstr.getSource();
         Variable res = copyinstr.getResult();
         if (res instanceof TemporaryFloatVariable) {
             loadFloatArg(src);
         } else if (res instanceof TemporaryFixnumVariable) {
             loadFixnumArg(src);
         } else {
             visit(src);
         }
         jvmStoreLocal(res);
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
 
         emitMethod(method);
 
         Map<Integer, MethodType> signatures = method.getNativeSignatures();
 
         MethodType signature = signatures.get(-1);
 
         String defSignature = pushHandlesForDef(
                 method.getJittedName(),
                 signatures,
                 signature,
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, IRubyObject.class),
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, java.lang.invoke.MethodHandle.class, int.class, IRScope.class, IRubyObject.class));
 
         jvmAdapter().getstatic(jvm.clsData().clsName, method.getJittedName() + "_IRScope", ci(IRScope.class));
         visit(defineclassmethodinstr.getContainer());
 
         // add method
         jvmMethod().adapter.invokestatic(p(IRRuntimeHelpers.class), "defCompiledClassMethod", defSignature);
     }
 
     // SSS FIXME: Needs an update to reflect instr. change
     @Override
     public void DefineInstanceMethodInstr(DefineInstanceMethodInstr defineinstancemethodinstr) {
         IRMethod method = defineinstancemethodinstr.getMethod();
 
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         m.loadContext();
 
         emitMethod(method);
         Map<Integer, MethodType> signatures = method.getNativeSignatures();
 
         MethodType variable = signatures.get(-1); // always a variable arity handle
 
         String defSignature = pushHandlesForDef(
                 method.getJittedName(),
                 signatures,
                 variable,
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, DynamicScope.class, IRubyObject.class),
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, java.lang.invoke.MethodHandle.class, int.class, IRScope.class, DynamicScope.class, IRubyObject.class));
 
         a.getstatic(jvm.clsData().clsName, method.getJittedName() + "_IRScope", ci(IRScope.class));
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmMethod().loadSelf();
 
         // add method
         a.invokestatic(p(IRRuntimeHelpers.class), "defCompiledInstanceMethod", defSignature);
     }
 
     public String pushHandlesForDef(String name, Map<Integer, MethodType> signatures, MethodType variable, String variableOnly, String variableAndSpecific) {
         String defSignature;
 
         jvmMethod().pushHandle(new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(variable.returnType(), variable.parameterArray())));
 
         if (signatures.size() == 1) {
             defSignature = variableOnly;
         } else {
             defSignature = variableAndSpecific;
 
             // FIXME: only supports one arity
             for (Map.Entry<Integer, MethodType> entry : signatures.entrySet()) {
                 if (entry.getKey() == -1) continue; // variable arity signature pushed above
                 jvmMethod().pushHandle(new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(entry.getValue().returnType(), entry.getValue().parameterArray())));
                 jvmAdapter().pushInt(entry.getKey());
                 break;
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
         Operand source = getglobalvariableinstr.getSource();
         GlobalVariable gvar = (GlobalVariable)source;
         String name = gvar.getName();
         jvmMethod().loadRuntime();
         jvmMethod().invokeVirtual(Type.getType(Ruby.class), Method.getMethod("org.jruby.internal.runtime.GlobalVariables getGlobalVariables()"));
         jvmAdapter().ldc(name);
         jvmMethod().invokeVirtual(Type.getType(GlobalVariables.class), Method.getMethod("org.jruby.runtime.builtin.IRubyObject get(String)"));
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
-        IRBytecodeAdapter m = jvmMethod();
         String name = instancesuperinstr.getMethodAddr().getName();
         Operand[] args = instancesuperinstr.getCallArgs();
+        Operand definingModule = instancesuperinstr.getDefiningModule();
+        boolean containsArgSplat = instancesuperinstr.containsArgSplat();
+        Operand closure = instancesuperinstr.getClosureArg(null);
+
+        superCommon(name, instancesuperinstr, args, definingModule, containsArgSplat, closure, instancesuperinstr.getResult());
+    }
+
+    private void superCommon(String name, CallInstr instr, Operand[] args, Operand definingModule, boolean containsArgSplat, Operand closure, Variable result) {
+        IRBytecodeAdapter m = jvmMethod();
+        Operation operation = instr.getOperation();
 
         m.loadContext();
         m.loadSelf(); // TODO: get rid of caller
         m.loadSelf();
-        visit(instancesuperinstr.getDefiningModule());
+        visit(definingModule);
 
         // TODO: CON: is this safe?
         jvmAdapter().checkcast(p(RubyClass.class));
 
         // process args
         for (int i = 0; i < args.length; i++) {
             Operand operand = args[i];
             visit(operand);
         }
 
         // if there's splats, provide a map and let the call site sort it out
-        boolean[] splatMap = IRRuntimeHelpers.buildSplatMap(args, instancesuperinstr.containsArgSplat());
+        boolean[] splatMap = IRRuntimeHelpers.buildSplatMap(args, containsArgSplat);
 
-        Operand closure = instancesuperinstr.getClosureArg(null);
         boolean hasClosure = closure != null;
         if (hasClosure) {
             m.loadContext();
             visit(closure);
             m.invokeIRHelper("getBlockFromObject", sig(Block.class, ThreadContext.class, Object.class));
         }
 
-        m.invokeInstanceSuper(name, args.length, hasClosure, splatMap);
+        switch (operation) {
+            case INSTANCE_SUPER:
+                m.invokeInstanceSuper(name, args.length, hasClosure, splatMap);
+                break;
+            case CLASS_SUPER:
+                m.invokeClassSuper(name, args.length, hasClosure, splatMap);
+                break;
+            case UNRESOLVED_SUPER:
+                m.invokeUnresolvedSuper(name, args.length, hasClosure, splatMap);
+                break;
+            case ZSUPER:
+                m.invokeZSuper(name, args.length, hasClosure, splatMap);
+                break;
+            default:
+                throw new NotCompilableException("unknown super type " + operation + " in " + instr);
+        }
 
-        jvmStoreLocal(instancesuperinstr.getResult());
+        if (instr instanceof ResultInstr) {
+            jvmStoreLocal(((ResultInstr) instr).getResult());
+        } else {
+            jvmAdapter().pop();
+        }
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
     public void Match2Instr(Match2Instr match2instr) {
         visit(match2instr.getReceiver());
         jvmMethod().loadContext();
         visit(match2instr.getArg());
         jvmAdapter().invokevirtual(p(RubyRegexp.class), "op_match19", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class));
         jvmStoreLocal(match2instr.getResult());
     }
 
     @Override
     public void Match3Instr(Match3Instr match3instr) {
         jvmMethod().loadContext();
         visit(match3instr.getReceiver());
         visit(match3instr.getArg());
         jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "match3", sig(IRubyObject.class, ThreadContext.class, RubyRegexp.class, IRubyObject.class));
         jvmStoreLocal(match3instr.getResult());
     }
 
     @Override
     public void MatchInstr(MatchInstr matchinstr) {
         visit(matchinstr.getReceiver());
         jvmMethod().loadContext();
         jvmAdapter().invokevirtual(p(RubyRegexp.class), "op_match2_19", sig(IRubyObject.class, ThreadContext.class));
         jvmStoreLocal(matchinstr.getResult());
     }
 
     @Override
     public void MethodLookupInstr(MethodLookupInstr methodlookupinstr) {
         // SSS FIXME: Unused at this time
         throw new NotCompilableException("Unsupported instruction: " + methodlookupinstr);
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
         String name = noResultCallInstr.getMethodAddr().getName();
         Operand[] args = noResultCallInstr.getCallArgs();
         Operand receiver = noResultCallInstr.getReceiver();
         int numArgs = args.length;
         Operand closure = noResultCallInstr.getClosureArg(null);
         boolean hasClosure = closure != null;
         CallType callType = noResultCallInstr.getCallType();
 
         compileCallCommon(m, name, args, receiver, numArgs, closure, hasClosure, callType, null);
     }
 
     @Override
     public void OptArgMultipleAsgnInstr(OptArgMultipleAsgnInstr optargmultipleasgninstr) {
         visit(optargmultipleasgninstr.getArrayArg());
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
         jvmMethod().invokeIRHelper("invokeModuleBody", sig(IRubyObject.class, ThreadContext.class, DynamicMethod.class));
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
     public void PushFrameInstr(PushFrameInstr pushframeinstr) {
         jvmMethod().loadContext();
         jvmMethod().loadFrameClass();
         jvmAdapter().ldc(pushframeinstr.getFrameName().getName());
         jvmMethod().loadSelf();
         jvmMethod().loadBlock();
         jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("void preMethodFrameOnly(org.jruby.RubyModule, String, org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.Block)"));
 
         // FIXME: this should be part of explicit call protocol only when needed, optimizable, and correct for the scope
         // See also CompiledIRMethod.call
         jvmMethod().loadContext();
         jvmAdapter().invokestatic(p(Visibility.class), "values", sig(Visibility[].class));
         jvmAdapter().ldc(Visibility.PUBLIC.ordinal());
         jvmAdapter().aaload();
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
         GlobalVariable target = (GlobalVariable)putglobalvarinstr.getTarget();
         String name = target.getName();
         jvmMethod().loadRuntime();
         jvmMethod().invokeVirtual(Type.getType(Ruby.class), Method.getMethod("org.jruby.internal.runtime.GlobalVariables getGlobalVariables()"));
         jvmAdapter().ldc(name);
         visit(putglobalvarinstr.getValue());
         jvmMethod().invokeVirtual(Type.getType(GlobalVariables.class), Method.getMethod("org.jruby.runtime.builtin.IRubyObject set(String, org.jruby.runtime.builtin.IRubyObject)"));
         // leaves copy of value on stack
         jvmAdapter().pop();
     }
 
     @Override
     public void RaiseArgumentErrorInstr(RaiseArgumentErrorInstr raiseargumenterrorinstr) {
         super.RaiseArgumentErrorInstr(raiseargumenterrorinstr);
     }
 
     @Override
     public void ReceiveClosureInstr(ReceiveClosureInstr receiveclosureinstr) {
         jvmMethod().loadRuntime();
         jvmLoadLocal("$block");
         jvmMethod().invokeIRHelper("newProc", sig(IRubyObject.class, Ruby.class, Block.class));
         jvmStoreLocal(receiveclosureinstr.getResult());
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
         jvmMethod().loadArgs();
         jvmAdapter().pushInt(instr.preReqdArgsCount);
         jvmAdapter().pushInt(instr.postReqdArgsCount);
         jvmAdapter().pushInt(instr.getArgIndex());
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
         jvmMethod().invokeIRHelper("receivePostReqdArg", sig(IRubyObject.class, IRubyObject[].class, int.class, int.class, int.class, boolean.class));
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
         jvmMethod().loadSelf();
         jvmStoreLocal(receiveselfinstr.getResult());
     }
 
     @Override
     public void RecordEndBlockInstr(RecordEndBlockInstr recordendblockinstr) {
         super.RecordEndBlockInstr(recordendblockinstr);    //To change body of overridden methods use File | Settings | File Templates.
     }
 
     @Override
     public void ReqdArgMultipleAsgnInstr(ReqdArgMultipleAsgnInstr reqdargmultipleasgninstr) {
         jvmMethod().loadContext();
         visit(reqdargmultipleasgninstr.getArrayArg());
         jvmAdapter().checkcast(p(RubyArray.class));
         jvmAdapter().pushInt(reqdargmultipleasgninstr.getPreArgsCount());
         jvmAdapter().pushInt(reqdargmultipleasgninstr.getIndex());
         jvmAdapter().pushInt(reqdargmultipleasgninstr.getPostArgsCount());
         jvmMethod().invokeHelper("irReqdArgMultipleAsgn", IRubyObject.class, ThreadContext.class, RubyArray.class, int.class, int.class, int.class);
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
         visit(restargmultipleasgninstr.getArrayArg());
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
             default:
                 throw new NotCompilableException("Unknown IR runtime helper method: " + runtimehelpercall.getHelperMethod() + "; INSTR: " + this);
         }
     }
 
     @Override
     public void NonlocalReturnInstr(NonlocalReturnInstr returninstr) {
         jvmMethod().loadContext();
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmMethod().loadBlockType();
         jvmAdapter().ldc(returninstr.maybeLambda);
         visit(returninstr.getReturnValue());
 
         jvmMethod().invokeIRHelper("initiateNonLocalReturn", sig(IRubyObject.class, ThreadContext.class, DynamicScope.class, Block.Type.class, boolean.class, IRubyObject.class));
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
         visit(throwexceptioninstr.getExceptionArg());
         jvmAdapter().athrow();
     }
 
     @Override
     public void ToAryInstr(ToAryInstr toaryinstr) {
         jvmMethod().loadContext();
         visit(toaryinstr.getArrayArg());
         jvmMethod().invokeHelper("irToAry", IRubyObject.class, ThreadContext.class, IRubyObject.class);
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
-
-        IRBytecodeAdapter m = jvmMethod();
         String name = unresolvedsuperinstr.getMethodAddr().getName();
         Operand[] args = unresolvedsuperinstr.getCallArgs();
-
-        m.loadContext();
-        m.loadSelf(); // TODO: get rid of caller
-        m.loadSelf();
         // this would be getDefiningModule but that is not used for unresolved super
-//        visit(unresolvedsuperinstr.getDefiningModule());
-        jvmAdapter().aconst_null();
-
-//        // TODO: CON: is this safe?
-//        jvmAdapter().checkcast(p(RubyClass.class));
-
-        for (int i = 0; i < args.length; i++) {
-            Operand operand = args[i];
-            visit(operand);
-        }
-
-        // if there's splats, provide a map and let the call site sort it out
-        boolean[] splatMap = IRRuntimeHelpers.buildSplatMap(args, unresolvedsuperinstr.containsArgSplat());
-
+        Operand definingModule = Null.INSTANCE;
+        boolean containsArgSplat = unresolvedsuperinstr.containsArgSplat();
         Operand closure = unresolvedsuperinstr.getClosureArg(null);
-        boolean hasClosure = closure != null;
-        if (hasClosure) {
-            m.loadContext();
-            visit(closure);
-            m.invokeIRHelper("getBlockFromObject", sig(Block.class, ThreadContext.class, Object.class));
-        }
-
-        m.invokeUnresolvedSuper(name, args.length, hasClosure, splatMap);
 
-        jvmStoreLocal(unresolvedsuperinstr.getResult());
+        superCommon(name, unresolvedsuperinstr, args, definingModule, containsArgSplat, closure, unresolvedsuperinstr.getResult());
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
     public void ZSuperInstr(ZSuperInstr zsuperinstr) {
-        // TODO: Nearly identical to InstanceSuperInstr
-        IRBytecodeAdapter m = jvmMethod();
-        String name = zsuperinstr.getMethodAddr().getName(); // ignored on the other side since it is unresolved
+        String name = zsuperinstr.getMethodAddr().getName();
         Operand[] args = zsuperinstr.getCallArgs();
-
-        m.loadContext();
-        m.loadSelf(); // TODO: get rid of caller
-        m.loadSelf();
-        jvmAdapter().aconst_null(); // no defining class
-
-        // TODO: CON: is this safe?
-        jvmAdapter().checkcast(p(RubyClass.class));
-
-        for (int i = 0; i < args.length; i++) {
-            Operand operand = args[i];
-            visit(operand);
-        }
-
-        // if there's splats, provide a map and let the call site sort it out
-        boolean[] splatMap = IRRuntimeHelpers.buildSplatMap(args, zsuperinstr.containsArgSplat());
-
+        // this would be getDefiningModule but that is not used for unresolved super
+        Operand definingModule = Null.INSTANCE;
+        boolean containsArgSplat = zsuperinstr.containsArgSplat();
         Operand closure = zsuperinstr.getClosureArg(null);
-        boolean hasClosure = closure != null;
-        if (hasClosure) {
-            m.loadContext();
-            visit(closure);
-            m.invokeIRHelper("getBlockFromObject", sig(Block.class, ThreadContext.class, Object.class));
-        }
-
-        m.invokeZSuper(name, args.length, hasClosure, splatMap);
 
-        jvmStoreLocal(zsuperinstr.getResult());
+        superCommon(name, zsuperinstr, args, definingModule, containsArgSplat, closure, zsuperinstr.getResult());
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
         jvmAdapter().ldc(buildlambdainstr.getPosition().getFile());
         jvmAdapter().pushInt(buildlambdainstr.getPosition().getLine());
 
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
     public void CurrentScope(CurrentScope currentscope) {
         jvmMethod().loadStaticScope();
     }
 
     @Override
     public void DynamicSymbol(DynamicSymbol dynamicsymbol) {
         jvmMethod().loadRuntime();
         visit(dynamicsymbol.getSymbolName());
         jvmAdapter().invokeinterface(p(IRubyObject.class), "asJavaString", sig(String.class));
         jvmAdapter().invokevirtual(p(Ruby.class), "newSymbol", sig(RubySymbol.class, String.class));
     }
 
     @Override
     public void Fixnum(Fixnum fixnum) {
         jvmMethod().pushFixnum(fixnum.getValue());
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
     public void GlobalVariable(GlobalVariable globalvariable) {
         super.GlobalVariable(globalvariable);    //To change body of overridden methods use File | Settings | File Templates.
     }
 
     @Override
     public void Hash(Hash hash) {
         jvmMethod().loadContext();
         for (KeyValuePair<Operand, Operand> pair: hash.getPairs()) {
             visit(pair.getKey());
             visit(pair.getValue());
         }
         jvmMethod().hash(hash.getPairs().size());
     }
 
     @Override
     public void IRException(IRException irexception) {
         super.IRException(irexception);    //To change body of overridden methods use File | Settings | File Templates.
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
     public void MethAddr(MethAddr methaddr) {
         jvmAdapter().ldc(methaddr.getName());
     }
 
     @Override
     public void MethodHandle(MethodHandle methodhandle) {
         // SSS FIXME: Unused at this time
         throw new NotCompilableException("Unsupported operand: " + methodhandle);
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
     public void ObjectClass(ObjectClass objectclass) {
         jvmMethod().pushObjectClass();
     }
 
     @Override
     public void Regexp(Regexp regexp) {
         if (!regexp.hasKnownValue() && !regexp.options.isOnce()) {
             jvmMethod().loadRuntime();
             visit(regexp.getRegexp());
             jvmAdapter().invokevirtual(p(RubyString.class), "getByteList", sig(ByteList.class));
             jvmAdapter().ldc(regexp.options.toEmbeddedOptions());
             jvmAdapter().invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, Ruby.class, RubyString.class, int.class));
             jvmAdapter().dup();
             jvmAdapter().invokevirtual(p(RubyRegexp.class), "setLiteral", sig(void.class));
         } else {
             // FIXME: need to check this on cached path
             // context.runtime.getKCode() != rubyRegexp.getKCode()) {
             jvmMethod().loadContext();
             visit(regexp.getRegexp());
             jvmMethod().pushRegexp(regexp.options.toEmbeddedOptions());
         }
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
         jvmMethod().loadContext();
         visit(splat.getArray());
         jvmMethod().invokeHelper("irSplat", RubyArray.class, ThreadContext.class, IRubyObject.class);
     }
 
     @Override
     public void StandardError(StandardError standarderror) {
         jvmMethod().loadRuntime();
         jvmAdapter().invokevirtual(p(Ruby.class), "getStandardError", sig(RubyClass.class));
     }
 
     @Override
     public void StringLiteral(StringLiteral stringliteral) {
         jvmMethod().pushString(stringliteral.getByteList());
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
         jvmMethod().pushSymbol(symbol.getName());
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
 
         { // FIXME: block body should be cached
             jvmAdapter().newobj(p(CompiledIRBlockBody.class));
             jvmAdapter().dup();
 
             jvmAdapter().ldc(closure.getHandle());
             jvmAdapter().getstatic(jvm.clsData().clsName, closure.getHandle().getName() + "_IRScope", ci(IRScope.class));
             jvmAdapter().ldc(closure.getArity().getValue());
 
             jvmAdapter().invokespecial(p(CompiledIRBlockBody.class), "<init>", sig(void.class, java.lang.invoke.MethodHandle.class, IRScope.class, int.class));
         }
 
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
diff --git a/core/src/main/java/org/jruby/ir/targets/ResolvedSuperInvokeSite.java b/core/src/main/java/org/jruby/ir/targets/ResolvedSuperInvokeSite.java
index f7cc38ead6..b668ac3dc6 100644
--- a/core/src/main/java/org/jruby/ir/targets/ResolvedSuperInvokeSite.java
+++ b/core/src/main/java/org/jruby/ir/targets/ResolvedSuperInvokeSite.java
@@ -1,115 +1,111 @@
 package org.jruby.ir.targets;
 
 import org.jruby.RubyClass;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callsite.CacheEntry;
 import org.jruby.util.JavaNameMangler;
 import org.objectweb.asm.Handle;
 import org.objectweb.asm.Opcodes;
 
 import java.lang.invoke.CallSite;
 import java.lang.invoke.MethodHandle;
 import java.lang.invoke.MethodHandles;
 import java.lang.invoke.MethodType;
 import java.lang.invoke.SwitchPoint;
 
 import static org.jruby.ir.runtime.IRRuntimeHelpers.splatArguments;
 import static org.jruby.util.CodegenUtils.p;
 import static org.jruby.util.CodegenUtils.sig;
 
 /**
 * Created by headius on 10/23/14.
 */
 public abstract class ResolvedSuperInvokeSite extends SelfInvokeSite {
     protected final String superName;
     protected final boolean[] splatMap;
 
     public ResolvedSuperInvokeSite(MethodType type, String superName, String splatmapString) {
         super(type, superName, CallType.SUPER);
 
         this.superName = superName;
         this.splatMap = IRRuntimeHelpers.decodeSplatmap(splatmapString);
     }
 
     public static final Handle BOOTSTRAP = new Handle(Opcodes.H_INVOKESTATIC, p(ResolvedSuperInvokeSite.class), "bootstrap", sig(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class));
 
     public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type, String splatmapString) {
         String[] targetAndMethod = name.split(":");
         String superName = JavaNameMangler.demangleMethodName(targetAndMethod[1]);
 
         InvokeSite site;
 
         switch (targetAndMethod[0]) {
             case "invokeInstanceSuper":
                 site = new InstanceSuperInvokeSite(type, superName, splatmapString);
                 break;
             case "invokeClassSuper":
                 site = new ClassSuperInvokeSite(type, superName, splatmapString);
                 break;
-            case "invokeUnresolvedSuper":
-                site = new UnresolvedSuperInvokeSite(type, superName, splatmapString);
-                break;
-            case "invokeZSuper":
-                site = new ZSuperInvokeSite(type, superName, splatmapString);
-                break;
             default:
                 throw new RuntimeException("invalid super call: " + name);
         }
 
         return InvokeSite.bootstrap(site, lookup);
     }
 
-    public IRubyObject invoke(ThreadContext context, IRubyObject caller, IRubyObject self, RubyClass definingModule, IRubyObject[] args, Block block) throws Throwable {
-        // TODO: mostly copy of org.jruby.ir.targets.InvokeSite because of different target class logic
-
-        RubyClass selfClass = pollAndGetClass(context, self);
-        RubyClass superClass = getSuperClass(definingModule);
-        SwitchPoint switchPoint = (SwitchPoint) superClass.getInvalidator().getData();
-        CacheEntry entry = superClass.searchWithCache(methodName);
-        DynamicMethod method = entry.method;
-
-        if (methodMissing(entry, caller)) {
-            return callMethodMissing(entry, callType, context, self, methodName, args, block);
-        }
-
-        MethodHandle mh = getHandle(superClass, this, method);
-
-        updateInvocationTarget(mh, self, selfClass, entry, switchPoint);
-
-        return method.call(context, self, superClass, methodName, args, block);
-    }
-
-    public IRubyObject fail(ThreadContext context, IRubyObject caller, IRubyObject self, RubyClass definingModule, IRubyObject[] args, Block block) throws Throwable {
-        // TODO: get rid of caller
-
-        context.callThreadPoll();
-
-        RubyClass superClass = getSuperClass(definingModule);
-        String name = methodName;
-        CacheEntry entry = cache;
-
-        if (entry.typeOk(superClass)) {
-            return entry.method.call(context, self, superClass, name, splatArguments(args, splatMap), block);
-        }
-
-        entry = superClass != null ? superClass.searchWithCache(name) : CacheEntry.NULL_CACHE;
-
-        DynamicMethod method = entry.method;
-
-        if (method.isUndefined()) {
-            return Helpers.callMethodMissing(context, self, method.getVisibility(), name, callType, splatArguments(args, splatMap), block);
-        }
-
-        cache = entry;
-
-        return method.call(context, self, superClass, name, splatArguments(args, splatMap), block);
-    }
+    // FIXME: indy cached version was not doing splat mapping; revert to slow logic for now
+
+//    public IRubyObject invoke(ThreadContext context, IRubyObject caller, IRubyObject self, RubyClass definingModule, IRubyObject[] args, Block block) throws Throwable {
+//        // TODO: mostly copy of org.jruby.ir.targets.InvokeSite because of different target class logic
+//
+//        RubyClass selfClass = pollAndGetClass(context, self);
+//        RubyClass superClass = getSuperClass(definingModule);
+//        SwitchPoint switchPoint = (SwitchPoint) superClass.getInvalidator().getData();
+//        CacheEntry entry = superClass.searchWithCache(methodName);
+//        DynamicMethod method = entry.method;
+//
+//        if (methodMissing(entry, caller)) {
+//            return callMethodMissing(entry, callType, context, self, methodName, args, block);
+//        }
+//
+//        MethodHandle mh = getHandle(superClass, this, method);
+//
+//        updateInvocationTarget(mh, self, selfClass, entry, switchPoint);
+//
+//        return method.call(context, self, superClass, methodName, args, block);
+//    }
+//
+//    public IRubyObject fail(ThreadContext context, IRubyObject caller, IRubyObject self, RubyClass definingModule, IRubyObject[] args, Block block) throws Throwable {
+//        // TODO: get rid of caller
+//
+//        context.callThreadPoll();
+//
+//        RubyClass superClass = getSuperClass(definingModule);
+//        String name = methodName;
+//        CacheEntry entry = cache;
+//
+//        if (entry.typeOk(superClass)) {
+//            return entry.method.call(context, self, superClass, name, splatArguments(args, splatMap), block);
+//        }
+//
+//        entry = superClass != null ? superClass.searchWithCache(name) : CacheEntry.NULL_CACHE;
+//
+//        DynamicMethod method = entry.method;
+//
+//        if (method.isUndefined()) {
+//            return Helpers.callMethodMissing(context, self, method.getVisibility(), name, callType, splatArguments(args, splatMap), block);
+//        }
+//
+//        cache = entry;
+//
+//        return method.call(context, self, superClass, name, splatArguments(args, splatMap), block);
+//    }
 
     protected abstract RubyClass getSuperClass(RubyClass definingModule);
 }
\ No newline at end of file
diff --git a/spec/compiler/general_spec.rb b/spec/compiler/general_spec.rb
index 1ea9e34032..32e70c2b13 100644
--- a/spec/compiler/general_spec.rb
+++ b/spec/compiler/general_spec.rb
@@ -1,875 +1,892 @@
 require 'jruby'
 require 'java'
 require 'rspec'
 
 module CompilerTestUtils
   def compile_to_method(src, filename = nil, lineno = 0)
     next_src_id = next_id
     node = JRuby.parse(src, filename || "testCompiler#{next_src_id}", false, lineno)
     filename = node.position.file
     oj = org.jruby
 
     # This logic is a mix of logic from InterpretedIRMethod's JIT, o.j.Ruby's script compilation, and IRScriptBody's
     # interpret. We need to figure out a cleaner path.
 
     scope = node.getStaticScope
     currModule = scope.getModule
     if currModule == nil
       currModule = JRuby.runtime.top_self.class
       scope.setModule(currModule)
     end
 
     method = oj.ir.IRBuilder.createIRBuilder(JRuby.runtime, JRuby.runtime.getIRManager()).buildRoot(node)
 
     method.prepareForCompilation
 
     compiler = oj.ir.targets.JVMVisitor.new
     compiled = compiler.compile(method, oj.util.OneShotClassLoader.new(JRuby.runtime.getJRubyClassLoader()))
     scriptMethod = compiled.getMethod(
         "__script__",
         oj.runtime.ThreadContext.java_class,
         oj.parser.StaticScope.java_class,
         oj.runtime.builtin.IRubyObject.java_class,
         oj.runtime.builtin.IRubyObject[].java_class,
         oj.runtime.Block.java_class,
         oj.RubyModule.java_class);
     handle = java.lang.invoke.MethodHandles.publicLookup().unreflect(scriptMethod);
 
     return oj.internal.runtime.methods.CompiledIRMethod.new(
         handle,
         method,
         oj.runtime.Visibility::PUBLIC,
         currModule)
   end
 
   def next_id
     $compiler_spec_id ||= 0
     $compiler_spec_id += 1
   end
   
   def silence_warnings
     verb = $VERBOSE
     $VERBOSE = nil
     yield
   ensure
     $VERBOSE = verb
   end
   
   def run(src, filename = caller_locations[0].path, line = caller_locations[0].lineno)
     yield eval(src, TOPLEVEL_BINDING, filename, line) unless (ENV['INTERPRETER_TEST'] == 'false')
     yield compile_run(src, filename, line) unless (ENV['COMPILER_TEST'] == 'false')
   end
 
   def compile_run(src, filename, line)
     cls = compile_to_method(src, filename, line)
 
     cls.call(
         JRuby.runtime.current_context,
         JRuby.runtime.top_self,
         JRuby.runtime.top_self.class,
         "script",
         IRubyObject[0].new,
         Block::NULL_BLOCK)
   end
 end
 
 describe "JRuby's bytecode compiler" do
   include CompilerTestUtils
 
   Block = org.jruby.runtime.Block
   IRubyObject = org.jruby.runtime.builtin.IRubyObject
 
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
     run("class Coercible1;def to_ary;[2,3];end;end; [1, *Coercible1.new]") {|result| expect(result).to eq([1,2,3]) }
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
       A = 'a'; module X; B = 'b'; end; module Y; def self.go; [A, X::B, ::A]; end; end; Y.go
     EOS
     run(const_code) {|result| expect(result).to eq(["a", "b", "a"]) }
   end
   
   it "compiles flip-flop" do
     # flip (taken from http://redhanded.hobix.com/inspect/hopscotchingArraysWithFlipFlops.html)
     run("s = true; (1..10).reject { true if (s = !s) .. (s) }") {|result| expect(result).to eq([1, 3, 5, 7, 9]) }
     run("s = true; (1..10).reject { true if (s = !s) .. (s = !s) }") {|result| expect(result).to eq([1, 4, 7, 10]) }
     big_flip = <<-EOS
     s = true; (1..10).inject([]) do |ary, v|; ary << [] unless (s = !s) .. (s = !s); ary.last << v; ary; end
     EOS
     run(big_flip) {|result| expect(result).to eq([[1, 2, 3], [4, 5, 6], [7, 8, 9], [10]]) }
     big_triple_flip = <<-EOS
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
     run(big_triple_flip) {|result| expect(result).to eq(expected) }
   end
 
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
       obj.blank?.should == false
       $~.should be_nil
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
     AFixnum = 1;
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
     run("module Foo2; end; Foo2::Foo3 = 5; Foo2::Foo3") {|result| expect(result).to eq 5 }
   end
 
   it "re-runs enclosing block when redo is called from ensure" do
     run("def foo; yield; end; x = false; foo { break 5 if x; begin; ensure; x = true; redo; end; break 6}") {|result| expect(result).to eq 5 }
   end
 
   it "compiles END BLocks" do
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
     # JRUBY-2246
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
     # JRUBY-4757 and JRUBY-2621: can't compile large array/hash
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
 
     run 'JRUBY4925::BLAH, a = 1, 2' do |x|
       expect(JRUBY4925::BLAH).to eq 1
     end
     run '::JRUBY4925_BLAH, a = 1, 2' do |x|
       expect(JRUBY4925_BLAH).to eq 1
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
+
+  it "splats arguments to super" do
+    run '
+      class SplatSuperArgs0
+        def foo(a, b, c)
+          a + b + c
+        end
+      end
+      class SplatSuperArgs1 < SplatSuperArgs0
+        def foo(*args)
+          super(*args)
+        end
+      end
+      SplatSuperArgs1.new.foo(1, 2, 3)' do |x|
+      expect(x).to eq 6
+    end
+  end
 end
