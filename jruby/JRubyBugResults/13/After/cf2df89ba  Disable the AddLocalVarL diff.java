diff --git a/core/src/main/java/org/jruby/ir/IRManager.java b/core/src/main/java/org/jruby/ir/IRManager.java
index 998c522a48..f712eff84a 100644
--- a/core/src/main/java/org/jruby/ir/IRManager.java
+++ b/core/src/main/java/org/jruby/ir/IRManager.java
@@ -1,288 +1,288 @@
 package org.jruby.ir;
 
 import java.util.EnumSet;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.ir.instructions.Instr;
 import org.jruby.ir.instructions.LineNumberInstr;
 import org.jruby.ir.instructions.ReceiveSelfInstr;
 import org.jruby.ir.instructions.ToggleBacktraceInstr;
 import org.jruby.ir.listeners.IRScopeListener;
 import org.jruby.ir.listeners.InstructionsListener;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.operands.Boolean;
 import org.jruby.ir.passes.BasicCompilerPassListener;
 import org.jruby.ir.passes.CompilerPass;
 import org.jruby.ir.passes.CompilerPassListener;
 import org.jruby.ir.passes.CompilerPassScheduler;
 
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 import org.jruby.ir.passes.DeadCodeElimination;
 import org.jruby.ir.passes.LocalOptimizationPass;
 import org.jruby.ir.passes.OptimizeDelegationPass;
 import org.jruby.ir.passes.OptimizeDynScopesPass;
 
 import static org.jruby.ir.IRFlags.RECEIVES_CLOSURE_ARG;
 import static org.jruby.ir.IRFlags.REQUIRES_DYNSCOPE;
 
 public class IRManager {
     public static final String SAFE_COMPILER_PASSES = "";
     public static final String DEFAULT_BUILD_PASSES = "";
-    public static final String DEFAULT_JIT_PASSES = "LocalOptimizationPass,OptimizeDelegationPass,DeadCodeElimination,AddLocalVarLoadStoreInstructions,OptimizeDynScopesPass,AddCallProtocolInstructions,EnsureTempsAssigned";
+    public static final String DEFAULT_JIT_PASSES = "LocalOptimizationPass,OptimizeDelegationPass,DeadCodeElimination,OptimizeDynScopesPass,AddCallProtocolInstructions,EnsureTempsAssigned";
     public static final String DEFAULT_INLINING_COMPILER_PASSES = "LocalOptimizationPass";
 
     private final CompilerPass deadCodeEliminationPass = new DeadCodeElimination();
     private final CompilerPass optimizeDynScopesPass = new OptimizeDynScopesPass();
     private final CompilerPass optimizeDelegationPass = new OptimizeDelegationPass();
 
     private int dummyMetaClassCount = 0;
     private final IRModuleBody object = new IRClassBody(this, null, "Object", 0, null);
     private final Nil nil = new Nil();
     private final Boolean tru = new Boolean(true);
     private final Boolean fals = new Boolean(false);
     private final StandardError standardError = new StandardError();
     public final ToggleBacktraceInstr needsBacktrace = new ToggleBacktraceInstr(true);
     public final ToggleBacktraceInstr needsNoBacktrace = new ToggleBacktraceInstr(false);
 
     // Listeners for debugging and testing of IR
     private Set<CompilerPassListener> passListeners = new HashSet<CompilerPassListener>();
     private CompilerPassListener defaultListener = new BasicCompilerPassListener();
 
     private InstructionsListener instrsListener = null;
     private IRScopeListener irScopeListener = null;
 
     // FIXME: Eventually make these attrs into either a) set b) part of state machine
     private List<CompilerPass> compilerPasses;
     private List<CompilerPass> inliningCompilerPasses;
     private List<CompilerPass> jitPasses;
     private List<CompilerPass> safePasses;
     private final RubyInstanceConfig config;
 
     // If true then code will not execute (see ir/ast tool)
     private boolean dryRun = false;
 
     public IRManager(RubyInstanceConfig config) {
         this.config = config;
         compilerPasses = CompilerPass.getPassesFromString(RubyInstanceConfig.IR_COMPILER_PASSES, DEFAULT_BUILD_PASSES);
         inliningCompilerPasses = CompilerPass.getPassesFromString(RubyInstanceConfig.IR_COMPILER_PASSES, DEFAULT_INLINING_COMPILER_PASSES);
         jitPasses = CompilerPass.getPassesFromString(RubyInstanceConfig.IR_JIT_PASSES, DEFAULT_JIT_PASSES);
         safePasses = CompilerPass.getPassesFromString(null, SAFE_COMPILER_PASSES);
     }
 
     public boolean isDryRun() {
         return dryRun;
     }
 
     public void setDryRun(boolean value) {
         this.dryRun = value;
     }
 
     public Nil getNil() {
         return nil;
     }
 
     public StandardError getStandardError() {
         return standardError;
     }
 
     public org.jruby.ir.operands.Boolean getTrue() {
         return tru;
     }
 
     public org.jruby.ir.operands.Boolean getFalse() {
         return fals;
     }
 
     public IRModuleBody getObject() {
         return object;
     }
 
     public ToggleBacktraceInstr needsBacktrace(boolean needsIt) {
         return needsIt ? needsBacktrace : needsNoBacktrace;
     }
 
     public CompilerPassScheduler schedulePasses() {
         return schedulePasses(compilerPasses);
     }
 
     public static CompilerPassScheduler schedulePasses(final List<CompilerPass> passes) {
         CompilerPassScheduler scheduler = new CompilerPassScheduler() {
             private Iterator<CompilerPass> iterator;
             {
                 this.iterator = passes.iterator();
             }
 
             @Override
             public Iterator<CompilerPass> iterator() {
                 return this.iterator;
             }
 
         };
         return scheduler;
     }
 
     public List<CompilerPass> getCompilerPasses(IRScope scope) {
         return compilerPasses;
     }
 
     public List<CompilerPass> getInliningCompilerPasses(IRScope scope) {
         return inliningCompilerPasses;
     }
 
     public List<CompilerPass> getJITPasses(IRScope scope) {
         return jitPasses;
     }
 
     public List<CompilerPass> getSafePasses(IRScope scope) {
         return safePasses;
     }
 
     public Set<CompilerPassListener> getListeners() {
         // FIXME: This is ugly but we want to conditionalize output based on JRuby module setting/unsetting
         if (RubyInstanceConfig.IR_COMPILER_DEBUG) {
             addListener(defaultListener);
         } else {
             removeListener(defaultListener);
         }
 
         return passListeners;
     }
 
     public InstructionsListener getInstructionsListener() {
         return instrsListener;
     }
 
     public IRScopeListener getIRScopeListener() {
         return irScopeListener;
     }
 
     public void addListener(CompilerPassListener listener) {
         passListeners.add(listener);
     }
 
     public void removeListener(CompilerPassListener listener) {
         passListeners.remove(listener);
     }
 
     public void addListener(InstructionsListener listener) {
         if (RubyInstanceConfig.IR_COMPILER_DEBUG || RubyInstanceConfig.IR_VISUALIZER) {
             if (instrsListener != null) {
                 throw new RuntimeException("InstructionsListener is set and other are currently not allowed");
             }
 
             instrsListener = listener;
         }
     }
 
     public void removeListener(InstructionsListener listener) {
         if (instrsListener.equals(listener)) instrsListener = null;
     }
 
     public void addListener(IRScopeListener listener) {
         if (RubyInstanceConfig.IR_COMPILER_DEBUG || RubyInstanceConfig.IR_VISUALIZER) {
             if (irScopeListener != null) {
                 throw new RuntimeException("IRScopeListener is set and other are currently not allowed");
             }
 
             irScopeListener = listener;
         }
     }
 
     public LineNumberInstr newLineNumber(int line) {
         if (line >= lineNumbers.length-1) growLineNumbersPool(line);
 
         if (line < 0) line = 0;
         LineNumberInstr tempVar = lineNumbers[line];
 
         if (tempVar == null) {
             tempVar = new LineNumberInstr(line);
             lineNumbers[line] = tempVar;
         }
 
         return tempVar;
 
     }
 
     private ReceiveSelfInstr receiveSelfInstr = new ReceiveSelfInstr(Self.SELF);
 
     public ReceiveSelfInstr getReceiveSelfInstr() {
         return receiveSelfInstr;
     }
 
     private LineNumberInstr[] lineNumbers = new LineNumberInstr[3000];
 
     protected LineNumberInstr[] growLineNumbersPool(int index) {
         int newLength = index * 2;
         LineNumberInstr[] newPool = new LineNumberInstr[newLength];
 
         System.arraycopy(lineNumbers, 0, newPool, 0, lineNumbers.length);
         lineNumbers = newPool;
         return newPool;
     }
 
 
     public void removeListener(IRScopeListener listener) {
         if (irScopeListener.equals(listener)) irScopeListener = null;
     }
 
     public String getMetaClassName() {
         return "<DUMMY_MC:" + dummyMetaClassCount++ + ">";
     }
 
     private TemporaryLocalVariable[] temporaryLocalVariables = new TemporaryLocalVariable[1600];
 
     protected TemporaryLocalVariable[] growTemporaryVariablePool(int index) {
         int newLength = index * 2;
         TemporaryLocalVariable[] newPool = new TemporaryLocalVariable[newLength];
 
         System.arraycopy(temporaryLocalVariables, 0, newPool, 0, temporaryLocalVariables.length);
         temporaryLocalVariables = newPool;
         return newPool;
     }
 
     // FIXME: Consider IRBuilder not using so many temporary variables for literal initialization.  This is the
     // vast majority of high index temp variables.
     /**
      * Temporary local variables are immutable and always start from a low index value and increment up
      * to a higher index value per scope.  So we can share these and store the ones in a simple list.  If
      * hard pinning is ever an issue we can periodically evict the list and start over at the cost of more
      * live objects but this list cache reduces a simple empty Rails app console from over 140K instances
      * to about 1200 instances.
      *
      */
     public TemporaryLocalVariable newTemporaryLocalVariable(int index) {
         if (index >= temporaryLocalVariables.length-1) growTemporaryVariablePool(index);
 
         TemporaryLocalVariable tempVar = temporaryLocalVariables[index];
 
         if (tempVar == null) {
             tempVar = new TemporaryLocalVariable(index);
             temporaryLocalVariables[index] = tempVar;
         }
 
         return tempVar;
     }
 
     /**
      * For scopes that don't require a dynamic scope we can run DCE and some other passes which cannot
      * be stymied by escaped bindings.
      */
     protected void optimizeIfSimpleScope(IRScope scope) {
         // We cannot pick the passes if we want an explicit set to run.
         if (RubyInstanceConfig.IR_COMPILER_PASSES != null) return;
 
         EnumSet<IRFlags> flags = scope.getFlags();
 
         if (!scope.isUnsafeScope() && !flags.contains(REQUIRES_DYNSCOPE)) {
             if (flags.contains(RECEIVES_CLOSURE_ARG)) optimizeDelegationPass.run(scope);
             deadCodeEliminationPass.run(scope);
             optimizeDynScopesPass.run(scope);
         }
     }
 
     public RubyInstanceConfig getInstanceConfig() {
         return config;
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
index f76189ee28..573606d251 100644
--- a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+++ b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
@@ -19,2008 +19,2053 @@ import org.jruby.ir.instructions.specialized.OneFloatArgNoBlockCallInstr;
 import org.jruby.ir.instructions.specialized.OneOperandArgNoBlockCallInstr;
 import org.jruby.ir.instructions.specialized.ZeroOperandArgNoBlockCallInstr;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.operands.Boolean;
 import org.jruby.ir.operands.Float;
 import org.jruby.ir.operands.Label;
 import org.jruby.ir.persistence.IRDumper;
 import org.jruby.ir.representations.BasicBlock;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callsite.RefinedCachingCallSite;
 import org.jruby.util.ByteList;
 import org.jruby.util.ClassDefiningClassLoader;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.KeyValuePair;
 import org.jruby.util.RegexpOptions;
 import org.jruby.util.StringSupport;
 import org.jruby.util.cli.Options;
 import org.jruby.util.collections.IntHashMap;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 import org.objectweb.asm.Handle;
 import org.objectweb.asm.Opcodes;
 import org.objectweb.asm.Type;
 import org.objectweb.asm.commons.Method;
 
 import java.io.ByteArrayOutputStream;
 import java.lang.invoke.MethodType;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import static org.jruby.util.CodegenUtils.*;
 
 /**
  * Implementation of IRCompiler for the JVM.
  */
 public class JVMVisitor extends IRVisitor {
 
     private static final Logger LOG = LoggerFactory.getLogger(JVMVisitor.class);
     public static final String DYNAMIC_SCOPE = "$dynamicScope";
     private static final boolean DEBUG = false;
     public static final String BLOCK_ARG_NAME = "blockArg";
     public static final String SELF_BLOCK_NAME = "selfBlock";
 
     private static final Signature METHOD_SIGNATURE_BASE = Signature
             .returning(IRubyObject.class)
             .appendArgs(new String[]{"context", "scope", "self", BLOCK_ARG_NAME, "class", "callName"}, ThreadContext.class, StaticScope.class, IRubyObject.class, Block.class, RubyModule.class, String.class);
 
     public static final Signature CLOSURE_SIGNATURE = Signature
             .returning(IRubyObject.class)
             .appendArgs(new String[]{"context", SELF_BLOCK_NAME, "scope", "self", "args", BLOCK_ARG_NAME, "superName", "type"}, ThreadContext.class, Block.class, StaticScope.class, IRubyObject.class, IRubyObject[].class, Block.class, String.class, Block.Type.class);
 
     public JVMVisitor() {
         this.jvm = Options.COMPILE_INVOKEDYNAMIC.load() ? new JVM7() : new JVM6();
         this.methodIndex = 0;
         this.scopeMap = new HashMap();
     }
 
     public Class compile(IRScope scope, ClassDefiningClassLoader jrubyClassLoader) {
         JVMVisitorMethodContext context = new JVMVisitorMethodContext();
         return defineFromBytecode(scope, compileToBytecode(scope, context), jrubyClassLoader);
     }
 
     public byte[] compileToBytecode(IRScope scope, JVMVisitorMethodContext context) {
         codegenScope(scope, context);
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
 
     public void codegenScope(IRScope scope, JVMVisitorMethodContext context) {
         if (scope instanceof IRScriptBody) {
             codegenScriptBody((IRScriptBody)scope);
         } else if (scope instanceof IRMethod) {
             emitMethodJIT((IRMethod)scope, context);
         } else if (scope instanceof IRClosure) {
             emitBlockJIT((IRClosure) scope, context);
         } else if (scope instanceof IRModuleBody) {
             emitModuleBodyJIT((IRModuleBody)scope);
         } else {
             throw new NotCompilableException("don't know how to JIT: " + scope);
         }
     }
 
     public void codegenScriptBody(IRScriptBody script) {
         emitScriptBody(script);
     }
 
     public void emitScope(IRScope scope, String name, Signature signature, boolean specificArity, boolean print) {
         BasicBlock[] bbs = scope.prepareForCompilation();
 
         if (print && Options.IR_PRINT.load()) {
             ByteArrayOutputStream baos = IRDumper.printIR(scope, true);
 
             LOG.info("Printing JIT IR for " + scope.getName(), "\n" + new String(baos.toByteArray()));
         }
 
         Map<BasicBlock, Label> exceptionTable = scope.buildJVMExceptionTable();
 
         emitClosures(scope, print);
 
         jvm.pushmethod(name, scope, signature, specificArity);
 
         // store IRScope in map for insertion into class later
         String scopeField = name + "_IRScope";
         if (scopeMap.get(scopeField) == null) {
             scopeMap.put(scopeField, scope);
             jvm.cls().visitField(Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC | Opcodes.ACC_VOLATILE, scopeField, ci(IRScope.class), null, null).visitEnd();
         }
 
         if (!scope.hasExplicitCallProtocol()) {
             // No call protocol, dynscope has been prepared for us
             jvmMethod().loadContext();
             jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("org.jruby.runtime.DynamicScope getCurrentScope()"));
             jvmStoreLocal(DYNAMIC_SCOPE);
         } else if (scope instanceof IRClosure) {
 //            // just load scope from context
 //            // FIXME: don't do this if we won't need the scope
 //            jvmMethod().loadContext();
 //            jvmAdapter().invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
 
             // just load null so it is initialized; if we need it, we'll set it later
             jvmAdapter().aconst_null();
             jvmStoreLocal(DYNAMIC_SCOPE);
         }
 
         IRBytecodeAdapter m = jvmMethod();
 
         int ipc = 0; // synthetic, used for debug traces that show which instr failed
 
         Label currentRescue = null;
         Label currentRegionStart = null;
         Label currentBlockStart = null;
         Map<Label, org.objectweb.asm.Label> rescueEndForStart = new HashMap<>();
         Map<Label, org.objectweb.asm.Label> syntheticEndForStart = new HashMap<>();
 
         for (BasicBlock bb: bbs) {
             currentBlockStart = bb.getLabel();
             Label rescueLabel = exceptionTable.get(bb);
 
             // not in a region at all (null-null) or in a region (a-a) but not at a boundary of the region.
             if (rescueLabel == currentRescue) continue;
 
             if (currentRescue != null) { // end of active region
                 rescueEndForStart.put(currentRegionStart, jvm.methodData().getLabel(bb.getLabel()));
             }
 
             if (rescueLabel != null) { // new region
                 currentRescue = rescueLabel;
                 currentRegionStart = bb.getLabel();
             } else { // end of active region but no new region
                 currentRescue = null;
                 currentRegionStart = null;
             }
         }
 
         // handle unclosed final region
         if (currentRegionStart != null) {
             org.objectweb.asm.Label syntheticEnd = new org.objectweb.asm.Label();
             rescueEndForStart.put(currentRegionStart, syntheticEnd);
             syntheticEndForStart.put(currentBlockStart, syntheticEnd);
         }
 
         for (BasicBlock bb: bbs) {
             org.objectweb.asm.Label start = jvm.methodData().getLabel(bb.getLabel());
             Label rescueLabel = exceptionTable.get(bb);
 
             m.mark(start);
 
             // if this is the start of a rescued region, emit trycatch
             org.objectweb.asm.Label end;
             if (rescueLabel != null && (end = rescueEndForStart.get(bb.getLabel())) != null) {
                 // first entry into a rescue region, do the try/catch
                 org.objectweb.asm.Label rescue = jvm.methodData().getLabel(rescueLabel);
                 jvmAdapter().trycatch(start, end, rescue, p(Throwable.class));
             }
 
             // ensure there's at least one instr per block
             /* FIXME: (CON 20150507) This used to filter blocks that had no instructions and only emit nop for them,
                       but this led to BBs with no *bytecode-emitting* instructions failing to have a nop and triggering
                       verification errors when we attached an exception-handling range to them (because the leading
                       label failed to anchor anywhere, or anchored the same place as the trailing label). Until we can
                       detect that a BB will not emit any code, we return to always emitting the nop. */
             m.adapter.nop();
 
             // visit remaining instrs
             for (Instr instr : bb.getInstrs()) {
                 if (DEBUG) instr.setIPC(ipc++); // debug mode uses instr offset for backtrace
                 visit(instr);
             }
 
             org.objectweb.asm.Label syntheticEnd = syntheticEndForStart.get(bb.getLabel());
             if (syntheticEnd != null) {
                 m.mark(syntheticEnd);
             }
         }
 
         jvm.popmethod();
     }
 
     public static final Signature signatureFor(IRScope method, boolean aritySplit) {
         if (aritySplit) {
             StaticScope argScope = method.getStaticScope();
             if (argScope.isArgumentScope() &&
                     argScope.getSignature().isFixed() &&
                     !argScope.getSignature().hasKwargs()) {
                 // we have only required arguments...emit a signature appropriate to that arity
                 String[] args = new String[argScope.getSignature().required()];
                 Class[] types = new Class[args.length]; // Class...
                 for (int i = 0; i < args.length; i++) {
                     args[i] = "arg" + i;
                     types[i] = IRubyObject.class;
                 }
                 return METHOD_SIGNATURE_BASE.insertArgs(3, args, types);
             }
             // we can't do an specific-arity signature
             return null;
         }
 
         // normal boxed arg list signature
         return METHOD_SIGNATURE_BASE.insertArgs(3, new String[]{"args"}, IRubyObject[].class);
     }
 
     public void emitScriptBody(IRScriptBody script) {
         // Note: no index attached because there should be at most one script body per .class
         String name = JavaNameMangler.encodeScopeForBacktrace(script);
         String clsName = jvm.scriptToClass(script.getFileName());
         jvm.pushscript(clsName, script.getFileName());
 
         emitScope(script, name, signatureFor(script, false), false, true);
 
         jvm.cls().visitEnd();
         jvm.popclass();
     }
 
     public void emitMethod(IRMethod method, JVMVisitorMethodContext context) {
         String name = JavaNameMangler.encodeScopeForBacktrace(method) + '$' + methodIndex++;
 
         emitWithSignatures(method, context, name);
     }
 
     public void emitMethodJIT(IRMethod method, JVMVisitorMethodContext context) {
         String clsName = jvm.scriptToClass(method.getFileName());
         String name = JavaNameMangler.encodeScopeForBacktrace(method) + '$' + methodIndex++;
         jvm.pushscript(clsName, method.getFileName());
 
         emitWithSignatures(method, context, name);
 
         jvm.cls().visitEnd();
         jvm.popclass();
     }
 
     public void emitBlockJIT(IRClosure closure, JVMVisitorMethodContext context) {
         String clsName = jvm.scriptToClass(closure.getFileName());
         String name = JavaNameMangler.encodeScopeForBacktrace(closure) + '$' + methodIndex++;
         jvm.pushscript(clsName, closure.getFileName());
 
         emitScope(closure, name, CLOSURE_SIGNATURE, false, true);
 
         context.setJittedName(name);
 
         jvm.cls().visitEnd();
         jvm.popclass();
     }
 
     private void emitWithSignatures(IRMethod method, JVMVisitorMethodContext context, String name) {
         context.setJittedName(name);
 
         Signature signature = signatureFor(method, false);
         emitScope(method, name, signature, false, true);
         context.addNativeSignature(-1, signature.type());
 
         Signature specificSig = signatureFor(method, true);
         if (specificSig != null) {
             emitScope(method, name, specificSig, true, false);
             context.addNativeSignature(method.getStaticScope().getSignature().required(), specificSig.type());
         }
     }
 
     public Handle emitModuleBodyJIT(IRModuleBody method) {
         String name = JavaNameMangler.encodeScopeForBacktrace(method) + '$' + methodIndex++;
 
         String clsName = jvm.scriptToClass(method.getFileName());
         jvm.pushscript(clsName, method.getFileName());
 
         Signature signature = signatureFor(method, false);
         emitScope(method, name, signature, false, true);
 
         Handle handle = new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(signature.type().returnType(), signature.type().parameterArray()));
 
         jvm.cls().visitEnd();
         jvm.popclass();
 
         return handle;
     }
 
     private void emitClosures(IRScope s, boolean print) {
         // Emit code for all nested closures
         for (IRClosure c: s.getClosures()) {
             c.setHandle(emitClosure(c, print));
         }
     }
 
     public Handle emitClosure(IRClosure closure, boolean print) {
         /* Compile the closure like a method */
         String name = JavaNameMangler.encodeScopeForBacktrace(closure) + '$' + methodIndex++;
 
         emitScope(closure, name, CLOSURE_SIGNATURE, false, print);
 
         return new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(CLOSURE_SIGNATURE.type().returnType(), CLOSURE_SIGNATURE.type().parameterArray()));
     }
 
     public Handle emitModuleBody(IRModuleBody method) {
         String name = JavaNameMangler.encodeScopeForBacktrace(method) + '$' + methodIndex++;
 
         Signature signature = signatureFor(method, false);
         emitScope(method, name, signature, false, true);
 
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
     public void ArgScopeDepthInstr(ArgScopeDepthInstr instr) {
         jvmMethod().loadContext();
         jvmMethod().loadStaticScope();
         jvmMethod().invokeIRHelper("getArgScopeDepth", sig(RubyFixnum.class, ThreadContext.class, StaticScope.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void AttrAssignInstr(AttrAssignInstr attrAssignInstr) {
         Operand[] callArgs = attrAssignInstr.getCallArgs();
 
         compileCallCommon(
                 jvmMethod(),
                 attrAssignInstr.getName(),
                 callArgs,
                 attrAssignInstr.getReceiver(),
                 callArgs.length,
                 null,
                 false,
                 attrAssignInstr.getReceiver() instanceof Self ? CallType.FUNCTIONAL : CallType.NORMAL,
                 null,
                 attrAssignInstr.isPotentiallyRefined());
     }
 
     @Override
     public void ArrayDerefInstr(ArrayDerefInstr arrayderefinstr) {
         jvmMethod().loadContext();
         jvmMethod().loadSelf();
         visit(arrayderefinstr.getReceiver());
         visit(arrayderefinstr.getKey());
         jvmMethod().invokeArrayDeref();
         jvmStoreLocal(arrayderefinstr.getResult());
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
         // this is a gross hack because we don't have distinction in boolean instrs between boxed and unboxed
         if (arg1 instanceof TemporaryBooleanVariable || arg1 instanceof UnboxedBoolean) {
             // no need to unbox
             visit(arg1);
             jvmMethod().bfalse(getJVMLabel(bFalseInstr.getJumpTarget()));
         } else if (arg1 instanceof UnboxedFixnum || arg1 instanceof UnboxedFloat) {
             // always true, don't branch
         } else {
             // unbox
             visit(arg1);
             jvmAdapter().invokeinterface(p(IRubyObject.class), "isTrue", sig(boolean.class));
             jvmMethod().bfalse(getJVMLabel(bFalseInstr.getJumpTarget()));
         }
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
         jvmMethod().pushString(csByteList, StringSupport.CR_BROKEN);
 
         for (Operand p : instr.getOperands()) {
             // visit piece and ensure it's a string
             visit(p);
             jvmAdapter().dup();
             org.objectweb.asm.Label after = new org.objectweb.asm.Label();
             jvmAdapter().instance_of(p(RubyString.class));
             jvmAdapter().iftrue(after);
             jvmAdapter().invokevirtual(p(IRubyObject.class), "anyToString", sig(IRubyObject.class));
 
             jvmAdapter().label(after);
             jvmAdapter().invokevirtual(p(RubyString.class), "append19", sig(RubyString.class, IRubyObject.class));
         }
 
         // freeze the string
         jvmAdapter().dup();
         jvmAdapter().ldc(true);
         jvmAdapter().invokeinterface(p(IRubyObject.class), "setFrozen", sig(void.class, boolean.class));
 
         // invoke the "`" method on self
         jvmMethod().invokeSelf("`", 1, false, CallType.FUNCTIONAL, false);
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
         // this is a gross hack because we don't have distinction in boolean instrs between boxed and unboxed
         if (arg1 instanceof TemporaryBooleanVariable || arg1 instanceof UnboxedBoolean) {
             // no need to unbox, just branch
             visit(arg1);
             jvmMethod().btrue(getJVMLabel(btrueinstr.getJumpTarget()));
         } else if (arg1 instanceof UnboxedFixnum || arg1 instanceof UnboxedFloat) {
             // always true, always branch
             jvmMethod().goTo(getJVMLabel(btrueinstr.getJumpTarget()));
         } else {
             // unbox and branch
             visit(arg1);
             jvmAdapter().invokeinterface(p(IRubyObject.class), "isTrue", sig(boolean.class));
             jvmMethod().btrue(getJVMLabel(btrueinstr.getJumpTarget()));
         }
     }
 
     @Override
     public void BUndefInstr(BUndefInstr bundefinstr) {
         visit(bundefinstr.getArg1());
         jvmMethod().pushUndefined();
         jvmAdapter().if_acmpeq(getJVMLabel(bundefinstr.getJumpTarget()));
     }
 
     @Override
     public void BuildBackrefInstr(BuildBackrefInstr instr) {
         jvmMethod().loadContext();
         jvmAdapter().invokevirtual(p(ThreadContext.class), "getBackRef", sig(IRubyObject.class));
 
         switch (instr.type) {
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
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BuildCompoundArrayInstr(BuildCompoundArrayInstr instr) {
         jvmMethod().loadContext();
         visit(instr.getAppendingArg());
         if (instr.isArgsPush()) jvmAdapter().checkcast("org/jruby/RubyArray");
         visit(instr.getAppendedArg());
         if (instr.isArgsPush()) {
             jvmMethod().invokeHelper("argsPush", RubyArray.class, ThreadContext.class, RubyArray.class, IRubyObject.class);
         } else {
             jvmMethod().invokeHelper("argsCat", RubyArray.class, ThreadContext.class, IRubyObject.class, IRubyObject.class);
         }
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BuildCompoundStringInstr(BuildCompoundStringInstr compoundstring) {
         ByteList csByteList = new ByteList();
         csByteList.setEncoding(compoundstring.getEncoding());
         jvmMethod().pushString(csByteList, StringSupport.CR_UNKNOWN);
         for (Operand p : compoundstring.getPieces()) {
 //            if ((p instanceof StringLiteral) && (compoundstring.isSameEncodingAndCodeRange((StringLiteral)p))) {
 //                jvmMethod().pushByteList(((StringLiteral)p).bytelist);
 //                jvmAdapter().invokevirtual(p(RubyString.class), "cat", sig(RubyString.class, ByteList.class));
 //            } else {
                 visit(p);
                 jvmAdapter().invokevirtual(p(RubyString.class), "append19", sig(RubyString.class, IRubyObject.class));
 //            }
         }
         if (compoundstring.isFrozen()) {
             jvmMethod().loadContext();
             jvmAdapter().swap();
             jvmAdapter().ldc(compoundstring.getFile());
             jvmAdapter().ldc(compoundstring.getLine());
             jvmMethod().invokeIRHelper("freezeLiteralString", sig(RubyString.class, ThreadContext.class, RubyString.class, String.class, int.class));
         }
         jvmStoreLocal(compoundstring.getResult());
     }
 
     @Override
     public void BuildDynRegExpInstr(BuildDynRegExpInstr instr) {
         final IRBytecodeAdapter m = jvmMethod();
 
         if (instr.getOptions().isOnce() && instr.getRegexp() != null) {
             visit(new Regexp(instr.getRegexp().source().convertToString().getByteList(), instr.getOptions()));
             jvmStoreLocal(instr.getResult());
             return;
         }
 
         RegexpOptions options = instr.getOptions();
         final Operand[] operands = instr.getPieces();
 
         Runnable r = new Runnable() {
             @Override
             public void run() {
                 m.loadContext();
                 for (int i = 0; i < operands.length; i++) {
                     Operand operand = operands[i];
                     visit(operand);
                 }
             }
         };
 
         m.pushDRegexp(r, options, operands.length);
 
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BuildRangeInstr(BuildRangeInstr instr) {
         jvmMethod().loadContext();
         visit(instr.getBegin());
         visit(instr.getEnd());
         jvmAdapter().ldc(instr.isExclusive());
         jvmAdapter().invokestatic(p(RubyRange.class), "newRange", sig(RubyRange.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BuildSplatInstr(BuildSplatInstr instr) {
         jvmMethod().loadContext();
         visit(instr.getArray());
         jvmMethod().invokeIRHelper("irSplat", sig(RubyArray.class, ThreadContext.class, IRubyObject.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void CallInstr(CallInstr callInstr) {
         // JIT does not support refinements yet
         if (callInstr.getCallSite() instanceof RefinedCachingCallSite) {
             throw new NotCompilableException("refinements are unsupported in JIT");
         }
 
         IRBytecodeAdapter m = jvmMethod();
         String name = callInstr.getName();
         Operand[] args = callInstr.getCallArgs();
         Operand receiver = callInstr.getReceiver();
         int numArgs = args.length;
         Operand closure = callInstr.getClosureArg(null);
         boolean hasClosure = closure != null;
         CallType callType = callInstr.getCallType();
         Variable result = callInstr.getResult();
 
         compileCallCommon(m, name, args, receiver, numArgs, closure, hasClosure, callType, result, callInstr.isPotentiallyRefined());
     }
 
     private void compileCallCommon(IRBytecodeAdapter m, String name, Operand[] args, Operand receiver, int numArgs, Operand closure, boolean hasClosure, CallType callType, Variable result, boolean isPotentiallyRefined) {
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
                 m.invokeSelf(name, arity, hasClosure, CallType.FUNCTIONAL, isPotentiallyRefined);
                 break;
             case VARIABLE:
                 m.invokeSelf(name, arity, hasClosure, CallType.VARIABLE, isPotentiallyRefined);
                 break;
             case NORMAL:
                 m.invokeOther(name, arity, hasClosure, isPotentiallyRefined);
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
         jvmAdapter().pushBoolean(checkargsarrayarityinstr.rest);
         jvmMethod().invokeStatic(Type.getType(Helpers.class), Method.getMethod("void irCheckArgsArrayArity(org.jruby.runtime.ThreadContext, org.jruby.RubyArray, int, int, boolean)"));
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
             jvmMethod().loadBlockType();
             jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "checkArity", sig(void.class, ThreadContext.class, Object[].class, int.class, int.class, boolean.class, boolean.class, int.class, Block.Type.class));
         }
     }
 
     @Override
     public void CheckForLJEInstr(CheckForLJEInstr checkForljeinstr) {
         jvmMethod().loadContext();
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmAdapter().ldc(checkForljeinstr.maybeLambda());
         jvmMethod().loadBlockType();
         jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "checkForLJE", sig(void.class, ThreadContext.class, DynamicScope.class, boolean.class, Block.Type.class));
     }
 
         @Override
     public void ClassSuperInstr(ClassSuperInstr classsuperinstr) {
         String name = classsuperinstr.getName();
         Operand[] args = classsuperinstr.getCallArgs();
         Operand definingModule = classsuperinstr.getDefiningModule();
         boolean containsArgSplat = classsuperinstr.containsArgSplat();
         Operand closure = classsuperinstr.getClosureArg(null);
 
         superCommon(name, classsuperinstr, args, definingModule, containsArgSplat, closure);
     }
 
     @Override
     public void ConstMissingInstr(ConstMissingInstr constmissinginstr) {
         visit(constmissinginstr.getReceiver());
         jvmAdapter().checkcast("org/jruby/RubyModule");
         jvmMethod().loadContext();
         jvmAdapter().ldc("const_missing");
         // FIXME: This has lost it's encoding info by this point
         jvmMethod().pushSymbol(constmissinginstr.getMissingConst(), USASCIIEncoding.INSTANCE);
         jvmMethod().invokeVirtual(Type.getType(RubyModule.class), Method.getMethod("org.jruby.runtime.builtin.IRubyObject callMethod(org.jruby.runtime.ThreadContext, java.lang.String, org.jruby.runtime.builtin.IRubyObject)"));
         jvmStoreLocal(constmissinginstr.getResult());
     }
 
     @Override
     public void CopyInstr(CopyInstr copyinstr) {
         Operand  src = copyinstr.getSource();
         Variable res = copyinstr.getResult();
-        if (res instanceof TemporaryFloatVariable) {
-            loadFloatArg(src);
-        } else if (res instanceof TemporaryFixnumVariable) {
-            loadFixnumArg(src);
+
+        if (res instanceof LocalVariable) {
+
+            // CON FIXME: This isn't as efficient as it could be
+            LocalVariable localvariable = (LocalVariable) res;
+
+            jvmLoadLocal(DYNAMIC_SCOPE);
+
+            if (localvariable.getScopeDepth() == 0) {
+
+                visit(src);
+
+                switch (localvariable.getOffset()) {
+                    case 0:
+                        jvmAdapter().invokevirtual(p(DynamicScope.class), "setValueZeroDepthZero", sig(IRubyObject.class, IRubyObject.class));
+                        break;
+                    case 1:
+                        jvmAdapter().invokevirtual(p(DynamicScope.class), "setValueOneDepthZero", sig(IRubyObject.class, IRubyObject.class));
+                        break;
+                    case 2:
+                        jvmAdapter().invokevirtual(p(DynamicScope.class), "setValueTwoDepthZero", sig(IRubyObject.class, IRubyObject.class));
+                        break;
+                    case 3:
+                        jvmAdapter().invokevirtual(p(DynamicScope.class), "setValueThreeDepthZero", sig(IRubyObject.class, IRubyObject.class));
+                        break;
+                    default:
+                        jvmAdapter().ldc(localvariable.getOffset());
+                        jvmAdapter().invokevirtual(p(DynamicScope.class), "setValueDepthZero", sig(IRubyObject.class, IRubyObject.class, int.class));
+                        break;
+                }
+
+            } else {
+
+                jvmAdapter().ldc(localvariable.getOffset());
+                jvmAdapter().ldc(localvariable.getScopeDepth());
+                visit(src);
+                jvmAdapter().invokevirtual(p(DynamicScope.class), "setValue", sig(IRubyObject.class, int.class, int.class, IRubyObject.class));
+
+            }
+
         } else {
-            visit(src);
+
+            if (res instanceof TemporaryFloatVariable) {
+                loadFloatArg(src);
+            } else if (res instanceof TemporaryFixnumVariable) {
+                loadFixnumArg(src);
+            } else {
+                visit(src);
+            }
+
+            jvmStoreLocal(res);
+
         }
-        jvmStoreLocal(res);
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
 
         JVMVisitorMethodContext context = new JVMVisitorMethodContext();
         emitMethod(method, context);
 
         MethodType variable = context.getNativeSignature(-1); // always a variable arity handle
         assert(variable != null);
 
         String defSignature = pushHandlesForDef(
                 context.getJittedName(),
                 context.getNativeSignaturesExceptVariable(),
                 variable,
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, IRubyObject.class),
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, java.lang.invoke.MethodHandle.class, int.class, IRScope.class, IRubyObject.class));
 
         jvmAdapter().getstatic(jvm.clsData().clsName, context.getJittedName() + "_IRScope", ci(IRScope.class));
         visit(defineclassmethodinstr.getContainer());
 
         // add method
         jvmMethod().adapter.invokestatic(p(IRRuntimeHelpers.class), "defCompiledClassMethod", defSignature);
     }
 
     // SSS FIXME: Needs an update to reflect instr. change
     @Override
     public void DefineInstanceMethodInstr(DefineInstanceMethodInstr defineinstancemethodinstr) {
         IRMethod method = defineinstancemethodinstr.getMethod();
         JVMVisitorMethodContext context = new JVMVisitorMethodContext();
 
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         m.loadContext();
 
         emitMethod(method, context);
 
         MethodType variable = context.getNativeSignature(-1); // always a variable arity handle
         assert(variable != null);
 
         String defSignature = pushHandlesForDef(
                 context.getJittedName(),
                 context.getNativeSignaturesExceptVariable(),
                 variable,
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, DynamicScope.class, IRubyObject.class),
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, java.lang.invoke.MethodHandle.class, int.class, IRScope.class, DynamicScope.class, IRubyObject.class));
 
         a.getstatic(jvm.clsData().clsName, context.getJittedName() + "_IRScope", ci(IRScope.class));
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmMethod().loadSelf();
 
         // add method
         a.invokestatic(p(IRRuntimeHelpers.class), "defCompiledInstanceMethod", defSignature);
     }
 
     private String pushHandlesForDef(String name, IntHashMap<MethodType> signaturesExceptVariable, MethodType variable, String variableOnly, String variableAndSpecific) {
         String defSignature;
 
         jvmMethod().pushHandle(new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(variable.returnType(), variable.parameterArray())));
 
         if (signaturesExceptVariable.size() == 0) {
             defSignature = variableOnly;
         } else {
             defSignature = variableAndSpecific;
 
             for (IntHashMap.Entry<MethodType> entry : signaturesExceptVariable.entrySet()) {
                 jvmMethod().pushHandle(new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(entry.getValue().returnType(), entry.getValue().parameterArray())));
                 jvmAdapter().pushInt(entry.getKey());
                 break; // FIXME: only supports one arity
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
         String siteName = jvmMethod().getUniqueSiteName("===");
         IRBytecodeAdapter.cacheCallSite(jvmAdapter(), jvmMethod().getClassData().clsName, siteName, "===", CallType.FUNCTIONAL, false);
         jvmMethod().invokeIRHelper("isEQQ", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, CallSite.class));
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
 
         jvmMethod().inheritanceSearchConst(inheritancesearchconstinstr.getConstName(), false);
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
         boolean reuseParentDynScope = scope.getFlags().contains(IRFlags.REUSE_PARENT_DYNSCOPE);
         boolean pushNewDynScope = !scope.getFlags().contains(IRFlags.DYNSCOPE_ELIMINATED) && !reuseParentDynScope;
 
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
         jvmMethod().setGlobalVariable(putglobalvarinstr.getTarget().getName());
         // leaves copy of value on stack
         jvmAdapter().pop();
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
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[1]).getString());
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
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[0]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedGlobal", sig(IRubyObject.class, ThreadContext.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_INSTANCE_VAR:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedInstanceVar", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_CLASS_VAR:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().checkcast(p(RubyModule.class));
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedClassVar", sig(IRubyObject.class, ThreadContext.class, RubyModule.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_SUPER:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((Stringable)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedSuper", sig(IRubyObject.class, ThreadContext.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_METHOD:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((Stringable) runtimehelpercall.getArgs()[1]).getString());
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