diff --git a/src/org/jruby/compiler/impl/SkinnyMethodAdapter.java b/src/org/jruby/compiler/impl/SkinnyMethodAdapter.java
index 53fb82750a..6b1dbfc7ac 100644
--- a/src/org/jruby/compiler/impl/SkinnyMethodAdapter.java
+++ b/src/org/jruby/compiler/impl/SkinnyMethodAdapter.java
@@ -1,398 +1,402 @@
 /*
  * SkinnyMethodAdapter.java
  *
  * Created on March 10, 2007, 2:52 AM
  *
  * To change this template, choose Tools | Template Manager
  * and open the template in the editor.
  */
 
 package org.jruby.compiler.impl;
 
 import java.io.PrintStream;
 import org.jruby.util.CodegenUtils;
 import org.objectweb.asm.AnnotationVisitor;
 import org.objectweb.asm.Attribute;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.MethodVisitor;
 import org.objectweb.asm.Opcodes;
 
 /**
  *
  * @author headius
  */
 public class SkinnyMethodAdapter implements MethodVisitor, Opcodes {
     private MethodVisitor mv;
     
     /** Creates a new instance of SkinnyMethodAdapter */
     public SkinnyMethodAdapter(MethodVisitor mv) {
         this.mv = mv;
     }
     
     public void aload(int arg0) {
         mv.visitVarInsn(ALOAD, arg0);
     }
     
     public void iload(int arg0) {
         mv.visitVarInsn(ILOAD, arg0);
     }
     
     public void astore(int arg0) {
         mv.visitVarInsn(ASTORE, arg0);
     }
     
     public void ldc(Object arg0) {
         mv.visitLdcInsn(arg0);
     }
     
     public void invokestatic(String arg1, String arg2, String arg3) {
         mv.visitMethodInsn(INVOKESTATIC, arg1, arg2, arg3);
     }
     
     public void invokespecial(String arg1, String arg2, String arg3) {
         mv.visitMethodInsn(INVOKESPECIAL, arg1, arg2, arg3);
     }
     
     public void invokevirtual(String arg1, String arg2, String arg3) {
         mv.visitMethodInsn(INVOKEVIRTUAL, arg1, arg2, arg3);
     }
     
     public void invokeinterface(String arg1, String arg2, String arg3) {
         mv.visitMethodInsn(INVOKEINTERFACE, arg1, arg2, arg3);
     }
     
     public void aprintln() {
         dup();
         getstatic(CodegenUtils.cg.p(System.class), "out", CodegenUtils.cg.ci(PrintStream.class));
         swap();
         invokevirtual(CodegenUtils.cg.p(PrintStream.class), "println", CodegenUtils.cg.sig(void.class, CodegenUtils.cg.params(Object.class)));
     }
     
     public void areturn() {
         mv.visitInsn(ARETURN);
     }
     
     public void newobj(String arg0) {
         mv.visitTypeInsn(NEW, arg0);
     }
     
     public void dup() {
         mv.visitInsn(DUP);
     }
     
     public void swap() {
         mv.visitInsn(SWAP);
     }
     
     public void swap2() {
         dup2_x2();
         pop2();
     }
     
     public void getstatic(String arg1, String arg2, String arg3) {
         mv.visitFieldInsn(GETSTATIC, arg1, arg2, arg3);
     }
     
     public void putstatic(String arg1, String arg2, String arg3) {
         mv.visitFieldInsn(PUTSTATIC, arg1, arg2, arg3);
     }
     
     public void getfield(String arg1, String arg2, String arg3) {
         mv.visitFieldInsn(GETFIELD, arg1, arg2, arg3);
     }
     
     public void putfield(String arg1, String arg2, String arg3) {
         mv.visitFieldInsn(PUTFIELD, arg1, arg2, arg3);
     }
     
     public void voidreturn() {
         mv.visitInsn(RETURN);
     }
     
     public void anewarray(String arg0) {
         mv.visitTypeInsn(ANEWARRAY, arg0);
     }
     
     public void newarray(int arg0) {
         mv.visitIntInsn(NEWARRAY, arg0);
     }
     
     public void iconst_0() {
         mv.visitInsn(ICONST_0);
     }
     
     public void iconst_1() {
         mv.visitInsn(ICONST_1);
     }
     
     public void iconst_2() {
         mv.visitInsn(ICONST_2);
     }
     
     public void iconst_3() {
         mv.visitInsn(ICONST_3);
     }
     
     public void lconst_0() {
         mv.visitInsn(LCONST_0);
     }
     
     public void isub() {
         mv.visitInsn(ISUB);
     }
     
     public void aconst_null() {
         mv.visitInsn(ACONST_NULL);
     }
     
     public void label(Label label) {
         mv.visitLabel(label);
     }
     
+    public void nop() {
+        mv.visitInsn(NOP);
+    }
+    
     public void pop() {
         mv.visitInsn(POP);
     }
     
     public void pop2() {
         mv.visitInsn(POP2);
     }
     
     public void arrayload() {
         mv.visitInsn(AALOAD);
     }
     
     public void arraystore() {
         mv.visitInsn(AASTORE);
     }
     
     public void iarrayload() {
         mv.visitInsn(IALOAD);
     }
     
     public void barrayload() {
         mv.visitInsn(BALOAD);
     }
     
     public void barraystore() {
         mv.visitInsn(BASTORE);
     }
     
     public void dup_x2() {
         mv.visitInsn(DUP_X2);
     }
     
     public void dup_x1() {
         mv.visitInsn(DUP_X1);
     }
     
     public void dup2_x2() {
         mv.visitInsn(DUP2_X2);
     }
     
     public void dup2_x1() {
         mv.visitInsn(DUP2_X1);
     }
     
     public void dup2() {
         mv.visitInsn(DUP2);
     }
     
     public void trycatch(Label arg0, Label arg1, Label arg2,
                                    String arg3) {
         mv.visitTryCatchBlock(arg0, arg1, arg2, arg3);
     }
     
     public void go_to(Label arg0) {
         mv.visitJumpInsn(GOTO, arg0);
     }
     
     public void lookupswitch(Label arg0, int[] arg1, Label[] arg2) {
         mv.visitLookupSwitchInsn(arg0, arg1, arg2);
     }
     
     public void athrow() {
         mv.visitInsn(ATHROW);
     }
     
     public void instance_of(String arg0) {
         mv.visitTypeInsn(INSTANCEOF, arg0);
     }
     
     public void ifeq(Label arg0) {
         mv.visitJumpInsn(IFEQ, arg0);
     }
     
     public void ifne(Label arg0) {
         mv.visitJumpInsn(IFNE, arg0);
     }
     
     public void if_acmpne(Label arg0) {
         mv.visitJumpInsn(IF_ACMPNE, arg0);
     }
     
     public void if_acmpeq(Label arg0) {
         mv.visitJumpInsn(IF_ACMPNE, arg0);
     }
     
     public void if_icmple(Label arg0) {
         mv.visitJumpInsn(IF_ICMPLE, arg0);
     }
     
     public void if_icmpgt(Label arg0) {
         mv.visitJumpInsn(IF_ICMPGT, arg0);
     }
     
     public void if_icmplt(Label arg0) {
         mv.visitJumpInsn(IF_ICMPLT, arg0);
     }
     
     public void if_icmpne(Label arg0) {
         mv.visitJumpInsn(IF_ICMPNE, arg0);
     }
     
     public void if_icmpeq(Label arg0) {
         mv.visitJumpInsn(IF_ICMPEQ, arg0);
     }
     
     public void checkcast(String arg0) {
         mv.visitTypeInsn(CHECKCAST, arg0);
     }
     
     public void start() {
         mv.visitCode();
     }
     
     public void end() {
         mv.visitMaxs(1, 1);
         mv.visitEnd();
     }
     
     public void ifnonnull(Label arg0) {
         mv.visitJumpInsn(IFNONNULL, arg0);
     }
     
     public void ifnull(Label arg0) {
         mv.visitJumpInsn(IFNULL, arg0);
     }
     
     public void ifle(Label arg0) {
         mv.visitJumpInsn(IFLE, arg0);
     }
     
     public void arraylength() {
         mv.visitInsn(ARRAYLENGTH);
     }
     
     public void iadd() {
         mv.visitInsn(IADD);
     }
     
     public void iinc() {
         mv.visitInsn(IINC);
     }
     
     public AnnotationVisitor visitAnnotationDefault() {
         return mv.visitAnnotationDefault();
     }
 
     public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
         return mv.visitAnnotation(arg0, arg1);
     }
 
     public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1,
                                                       boolean arg2) {
         return mv.visitParameterAnnotation(arg0, arg1, arg2);
     }
 
     public void visitAttribute(Attribute arg0) {
         mv.visitAttribute(arg0);
     }
 
     public void visitCode() {
         mv.visitCode();
     }
 
     public void visitInsn(int arg0) {
         mv.visitInsn(arg0);
     }
 
     public void visitIntInsn(int arg0, int arg1) {
         mv.visitIntInsn(arg0, arg1);
     }
 
     public void visitVarInsn(int arg0, int arg1) {
         mv.visitVarInsn(arg0, arg1);
     }
 
     public void visitTypeInsn(int arg0, String arg1) {
         mv.visitTypeInsn(arg0, arg1);
     }
 
     public void visitFieldInsn(int arg0, String arg1, String arg2, String arg3) {
         mv.visitFieldInsn(arg0, arg1, arg2, arg3);
     }
 
     public void visitMethodInsn(int arg0, String arg1, String arg2, String arg3) {
         mv.visitMethodInsn(arg0, arg1, arg2, arg3);
     }
 
     public void visitJumpInsn(int arg0, Label arg1) {
         mv.visitJumpInsn(arg0, arg1);
     }
 
     public void visitLabel(Label arg0) {
         mv.visitLabel(arg0);
     }
 
     public void visitLdcInsn(Object arg0) {
         mv.visitLdcInsn(arg0);
     }
 
     public void visitIincInsn(int arg0, int arg1) {
         mv.visitIincInsn(arg0, arg1);
     }
 
     public void visitTableSwitchInsn(int arg0, int arg1, Label arg2,
                                      Label[] arg3) {
         mv.visitTableSwitchInsn(arg0, arg1, arg2, arg3);
     }
 
     public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
         mv.visitLookupSwitchInsn(arg0, arg1, arg2);
     }
 
     public void visitMultiANewArrayInsn(String arg0, int arg1) {
         mv.visitMultiANewArrayInsn(arg0, arg1);
     }
 
     public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2,
                                    String arg3) {
         mv.visitTryCatchBlock(arg0, arg1, arg2, arg3);
     }
 
     public void visitLocalVariable(String arg0, String arg1, String arg2,
                                    Label arg3, Label arg4, int arg5) {
         mv.visitLocalVariable(arg0, arg1, arg2, arg3, arg4, arg5);
     }
 
     public void visitLineNumber(int arg0, Label arg1) {
         mv.visitLineNumber(arg0, arg1);
     }
 
     public void visitMaxs(int arg0, int arg1) {
         mv.visitMaxs(arg0, arg1);
     }
 
     public void visitEnd() {
         mv.visitEnd();
     }
     
     public void tableswitch(int min, int max, Label defaultLabel, Label[] cases) {
         mv.visitTableSwitchInsn(min, max, defaultLabel, cases);
     }
 
     public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {
         mv.visitFrame(arg0, arg1, arg2, arg3, arg4);
     }
 
 }
diff --git a/src/org/jruby/compiler/impl/StandardASMCompiler.java b/src/org/jruby/compiler/impl/StandardASMCompiler.java
index 34040d6b2c..2a68c63871 100644
--- a/src/org/jruby/compiler/impl/StandardASMCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardASMCompiler.java
@@ -1,2692 +1,2716 @@
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
+    private static final int PREVIOUS_EXCEPTION_INDEX = 10;
     
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
 
         // invoke run with threadcontext and topself
         method.invokestatic(cg.p(Ruby.class), "getDefaultInstance", cg.sig(Ruby.class));
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
-        protected int localVariable = EXCEPTION_INDEX + 1;
+        protected int localVariable = PREVIOUS_EXCEPTION_INDEX + 1;
 
         public abstract void beginMethod(ClosureCallback args, StaticScope scope);
 
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
             loadRuntime();
             method.ldc(name);
             invokeIRuby("newSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
         }
 
         public void createNewSymbol(int id, String name) {
             loadRuntime(); 
             if(id != -1) {
                 method.ldc(id);
                 method.invokestatic(cg.p(RubySymbol.class), "getSymbol", cg.sig(RubySymbol.class, cg.params(Ruby.class, int.class)));
             } else {
                 String symField = getNewConstant(cg.ci(int.class), "lit_sym_", new Integer(-1));
 
                 // in current method, load the field to see if we've created a Pattern yet
                 method.aload(THIS); 
                 method.getfield(classname, symField, cg.ci(int.class));
 
                 Label alreadyCreated = new Label();
                 method.ldc(-1);
                 method.if_icmpne(alreadyCreated);
 
                 method.ldc(name); 
                 invokeIRuby("newSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
                 method.dup(); 
                 method.aload(THIS);
                 method.swap(); 
                 method.invokevirtual(cg.p(RubySymbol.class), "getId", cg.sig(int.class, cg.params())); 
                 method.putfield(classname, symField, cg.ci(int.class)); 
                 Label ret = new Label();
                 method.go_to(ret); 
 
                 method.visitLabel(alreadyCreated); 
 
                 method.aload(THIS);
                 method.getfield(classname, symField, cg.ci(int.class)); 
 
                 method.invokestatic(cg.p(RubySymbol.class), "getSymbol", cg.sig(RubySymbol.class, cg.params(Ruby.class, int.class)));
 
                 method.visitLabel(ret);
             }
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
             } else if (sourceArray.length < RuntimeHelpers.MAX_SPECIFIC_ARITY_OBJECT_ARRAY) {
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
             
             if (keyCount < RuntimeHelpers.MAX_SPECIFIC_ARITY_HASH) {
                 // we have a specific-arity method we can use to construct, so use that
                 for (int i = 0; i < keyCount; i++) {
                     callback.nextValue(this, elements, i);
                 }
                 
                 invokeUtilityMethod("constructHash", cg.sig(RubyHash.class, cg.params(Ruby.class, IRubyObject.class, keyCount * 2)));
             } else {
                 // create a new hashmap the brute-force way
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
                 ClosureCallback body,
                 ClosureCallback args,
                 boolean hasMultipleArgsHead,
                 NodeType argsNodeId,
                 ASTInspector inspector) {
             String closureMethodName = "closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, inspector);
             
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
             // if there's a sub-closure or there's scope-aware methods, it can't be "light"
             method.ldc(!(inspector.hasClosure() || inspector.hasScopeAwareMethods()));
 
             invokeUtilityMethod("createBlock", cg.sig(CompiledBlock.class,
                     cg.params(ThreadContext.class, IRubyObject.class, Integer.TYPE, String[].class, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE, boolean.class)));
         }
 
         public void runBeginBlock(StaticScope scope, ClosureCallback body) {
             String closureMethodName = "closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, null);
             
             closureCompiler.beginMethod(null, scope);
             
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
 
             buildStaticScopeNames(method, scope);
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
 
             invokeUtilityMethod("runBeginBlock", cg.sig(IRubyObject.class,
                     cg.params(ThreadContext.class, IRubyObject.class, String[].class, CompiledBlockCallback.class)));
         }
 
         public void createNewForLoop(int arity, ClosureCallback body, ClosureCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId) {
             String closureMethodName = "closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, null);
             
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
 
         public void createNewEndBlock(ClosureCallback body) {
             String closureMethodName = "END_closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, null);
             
             closureCompiler.beginMethod(null, null);
             
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
             method.ldc(new Integer(0));
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             method.ldc(false);
             method.ldc(Block.ZERO_ARGS);
 
             invokeUtilityMethod("createSharedScopeBlock", cg.sig(CompiledSharedScopeBlock.class,
                     cg.params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
             
             loadRuntime();
             invokeUtilityMethod("registerEndBlock", cg.sig(void.class, CompiledSharedScopeBlock.class, Ruby.class));
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
             method.swap();
 
             method.ldc(name);
             method.swap();
 
             invokeIRubyObject("fastSetInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
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
 
         public void forEachInValueArray(int start, int count, Object source, ArrayCallback callback, ArrayCallback nilCallback, ClosureCallback argsCallback) {
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
                 argsCallback.compile(this);
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
 
         public void createNewRegexp(final ByteList value, final int options, final String lang) {
             String regexpField = getNewConstant(cg.ci(RubyRegexp.class), "lit_reg_");
             String patternField = getNewConstant(cg.ci(RegexpPattern.class), "lit_pat_");
 
             // in current method, load the field to see if we've created a Pattern yet
             method.aload(THIS);
             method.getfield(classname, regexpField, cg.ci(RubyRegexp.class));
 
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
             method.putfield(classname, patternField, cg.ci(RegexpPattern.class));
 
             if (null == lang) {
                 method.aconst_null();
             } else {
                 method.ldc(lang);
             }
 
             method.invokestatic(cg.p(RubyRegexp.class), "newRegexp", cg.sig(RubyRegexp.class, cg.params(Ruby.class, RegexpPattern.class, String.class)));
 
             method.aload(THIS);
             method.swap();
             method.putfield(classname, regexpField, cg.ci(RubyRegexp.class));
             method.label(alreadyCreated);
             method.aload(THIS);
             method.getfield(classname, regexpField, cg.ci(RubyRegexp.class));
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
-            Label beforeMethodBody = new Label();
             Label afterMethodBody = new Label();
             Label catchRetry = new Label();
+            Label catchRaised = new Label();
+            Label catchJumps = new Label();
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
-                
-                mv.label(beforeMethodBody);
 
                 // set up a local IRuby variable
                 mv.aload(THREADCONTEXT_INDEX);
                 mv.dup();
                 mv.invokevirtual(cg.p(ThreadContext.class), "getRuntime", cg.sig(Ruby.class));
                 mv.dup();
                 mv.astore(RUNTIME_INDEX);
+                
+                // store previous exception for restoration if we rescue something
+                loadRuntime();
+                invokeUtilityMethod("getErrorInfo", cg.sig(IRubyObject.class, Ruby.class));
+                mv.astore(PREVIOUS_EXCEPTION_INDEX);
             
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
                 
-                mv.trycatch(beforeMethodBody, afterMethodBody, catchRetry, cg.p(JumpException.RetryJump.class));
+                mv.trycatch(beforeBody, afterMethodBody, catchRetry, cg.p(JumpException.RetryJump.class));
                 mv.label(catchRetry);
                 mv.pop();
-                mv.go_to(beforeMethodBody);
+                mv.go_to(beforeBody);
+                
+                // any exceptions raised must continue to be raised, skipping $! restoration
+                mv.trycatch(beforeBody, afterMethodBody, catchRaised, cg.p(RaiseException.class));
+                mv.label(catchRaised);
+                mv.athrow();
+                
+                // and remaining jump exceptions should restore $!
+                mv.trycatch(beforeBody, afterMethodBody, catchJumps, cg.p(JumpException.class));
+                mv.label(catchJumps);
+                loadRuntime();
+                mv.aload(PREVIOUS_EXCEPTION_INDEX);
+                invokeUtilityMethod("setErrorInfo", cg.sig(void.class, Ruby.class, IRubyObject.class));
+                mv.athrow();
                 
                 mv.label(exitRescue);
+                
+                // restore the original exception
+                loadRuntime();
+                mv.aload(PREVIOUS_EXCEPTION_INDEX);
+                invokeUtilityMethod("setErrorInfo", cg.sig(void.class, Ruby.class, IRubyObject.class));
+                
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
             method.ldc(name);
             //method.invokeinterface(cg.p(IRubyObject.class), "getInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class)));
             method.invokeinterface(cg.p(IRubyObject.class), "fastHasInstanceVariable", cg.sig(boolean.class, cg.params(String.class)));
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
             loadSelf();
             method.swap();
             method.invokevirtual(cg.p(RubyClass.class), "getRealClass", cg.sig(RubyClass.class));
             method.invokeinterface(cg.p(IRubyObject.class), "isKindOf", cg.sig(boolean.class, cg.params(RubyModule.class)));
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
             method.invokeinterface(cg.p(IRubyObject.class), "fastGetInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class)));
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
                 final ClosureCallback superCallback, 
                 final ClosureCallback pathCallback, 
                 final ClosureCallback bodyCallback, 
                 final ClosureCallback receiverCallback) {
             String methodName = "rubyclass__" + cg.cleanJavaIdentifier(name) + "__" + ++methodIndex;
 
             final ASMMethodCompiler methodCompiler = new ASMMethodCompiler(methodName, null);
             
             ClosureCallback bodyPrep = new ClosureCallback() {
                 public void compile(MethodCompiler context) {
                     if (receiverCallback == null) {
                         if (superCallback != null) {
                             methodCompiler.loadRuntime();
                             superCallback.compile(methodCompiler);
 
                             methodCompiler.invokeUtilityMethod("prepareSuperClass", cg.sig(RubyClass.class, cg.params(Ruby.class, IRubyObject.class)));
                         } else {
                             methodCompiler.method.aconst_null();
                         }
 
                         methodCompiler.loadThreadContext();
 
                         pathCallback.compile(methodCompiler);
 
                         methodCompiler.invokeUtilityMethod("prepareClassNamespace", cg.sig(RubyModule.class, cg.params(ThreadContext.class, IRubyObject.class)));
 
                         methodCompiler.method.swap();
 
                         methodCompiler.method.ldc(name);
 
                         methodCompiler.method.swap();
 
                         methodCompiler.method.invokevirtual(cg.p(RubyModule.class), "defineOrGetClassUnder", cg.sig(RubyClass.class, cg.params(String.class, RubyClass.class)));
                     } else {
                         methodCompiler.loadRuntime();
 
                         receiverCallback.compile(methodCompiler);
 
                         methodCompiler.invokeUtilityMethod("getSingletonClass", cg.sig(RubyClass.class, cg.params(Ruby.class, IRubyObject.class)));
                     }
 
                     // set self to the class
                     methodCompiler.method.dup();
                     methodCompiler.method.astore(SELF_INDEX);
 
                     // CLASS BODY
                     methodCompiler.loadThreadContext();
                     methodCompiler.method.swap();
 
                     // static scope
                     buildStaticScopeNames(methodCompiler.method, staticScope);
                     methodCompiler.invokeThreadContext("preCompiledClass", cg.sig(Void.TYPE, cg.params(RubyModule.class, String[].class)));
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
 
             bodyCallback.compile(methodCompiler);
             methodCompiler.method.label(end);
             // finally with no exception
             methodCompiler.loadThreadContext();
             methodCompiler.invokeThreadContext("postCompiledClass", cg.sig(Void.TYPE, cg.params()));
             
             methodCompiler.method.go_to(noException);
             
             methodCompiler.method.label(after);
             // finally with exception
             methodCompiler.loadThreadContext();
             methodCompiler.invokeThreadContext("postCompiledClass", cg.sig(Void.TYPE, cg.params()));
             methodCompiler.method.athrow();
             
             methodCompiler.method.label(noException);
 
             methodCompiler.endMethod();
 
             // prepare to call class definition method
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
             method.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
 
             method.invokevirtual(classname, methodName, METHOD_SIGNATURE);
         }
 
         public void defineModule(final String name, final StaticScope staticScope, final ClosureCallback pathCallback, final ClosureCallback bodyCallback) {
             String methodName = "rubyclass__" + cg.cleanJavaIdentifier(name) + "__" + ++methodIndex;
 
             final ASMMethodCompiler methodCompiler = new ASMMethodCompiler(methodName, null);
 
             ClosureCallback bodyPrep = new ClosureCallback() {
                 public void compile(MethodCompiler context) {
                     methodCompiler.loadThreadContext();
 
                     pathCallback.compile(methodCompiler);
 
                     methodCompiler.invokeUtilityMethod("prepareClassNamespace", cg.sig(RubyModule.class, cg.params(ThreadContext.class, IRubyObject.class)));
 
                     methodCompiler.method.ldc(name);
 
                     methodCompiler.method.invokevirtual(cg.p(RubyModule.class), "defineOrGetModuleUnder", cg.sig(RubyModule.class, cg.params(String.class)));
 
                     // set self to the class
                     methodCompiler.method.dup();
                     methodCompiler.method.astore(SELF_INDEX);
 
                     // CLASS BODY
                     methodCompiler.loadThreadContext();
                     methodCompiler.method.swap();
 
                     // static scope
                     buildStaticScopeNames(methodCompiler.method, staticScope);
 
                     methodCompiler.invokeThreadContext("preCompiledClass", cg.sig(Void.TYPE, cg.params(RubyModule.class, String[].class)));
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
 
             bodyCallback.compile(methodCompiler);
             methodCompiler.method.label(end);
             
             methodCompiler.method.go_to(noException);
             
             methodCompiler.method.label(after);
             methodCompiler.loadThreadContext();
             methodCompiler.invokeThreadContext("postCompiledClass", cg.sig(Void.TYPE, cg.params()));
             methodCompiler.method.athrow();
             
             methodCompiler.method.label(noException);
             methodCompiler.loadThreadContext();
             methodCompiler.invokeThreadContext("postCompiledClass", cg.sig(Void.TYPE, cg.params()));
 
             methodCompiler.endMethod();
 
             // prepare to call class definition method
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
             method.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
 
             method.invokevirtual(classname, methodName, METHOD_SIGNATURE);
         }
         
         public void unwrapPassedBlock() {
             loadBlock();
             invokeUtilityMethod("getBlockFromBlockPassBody", cg.sig(Block.class, cg.params(IRubyObject.class, Block.class)));
         }
         
         public void performBackref(char type) {
             loadThreadContext();
             switch (type) {
             case '~':
                 invokeUtilityMethod("backref", cg.sig(IRubyObject.class, cg.params(ThreadContext.class)));
                 break;
             case '&':
                 invokeUtilityMethod("backrefLastMatch", cg.sig(IRubyObject.class, cg.params(ThreadContext.class)));
                 break;
             case '`':
                 invokeUtilityMethod("backrefMatchPre", cg.sig(IRubyObject.class, cg.params(ThreadContext.class)));
                 break;
             case '\'':
                 invokeUtilityMethod("backrefMatchPost", cg.sig(IRubyObject.class, cg.params(ThreadContext.class)));
                 break;
             case '+':
                 invokeUtilityMethod("backrefMatchLast", cg.sig(IRubyObject.class, cg.params(ThreadContext.class)));
                 break;
             default:
                 throw new NotCompilableException("ERROR: backref with invalid type");
             }
         }
         
         public void callZSuper(ClosureCallback closure) {
             loadRuntime();
             loadThreadContext();
             if (closure != null) {
                 closure.compile(this);
             } else {
                 method.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
             }
             loadSelf();
             
             invokeUtilityMethod("callZSuper", cg.sig(IRubyObject.class, cg.params(Ruby.class, ThreadContext.class, Block.class, IRubyObject.class)));
         }
         
         public void checkIsExceptionHandled() {
             // ruby exception and list of exception types is on the stack
             loadRuntime();
             loadThreadContext();
             loadSelf();
             invokeUtilityMethod("isExceptionHandled", cg.sig(IRubyObject.class, RubyException.class, IRubyObject[].class, Ruby.class, ThreadContext.class, IRubyObject.class));
         }
         
         public void rethrowException() {
             loadException();
             method.athrow();
         }
         
         public void loadClass(String name) {
             loadRuntime();
             method.ldc(name);
             invokeIRuby("getClass", cg.sig(RubyClass.class, String.class));
         }
         
         public void unwrapRaiseException() {
             // RaiseException is on stack, get RubyException out
             method.invokevirtual(cg.p(RaiseException.class), "getException", cg.sig(RubyException.class));
         }
         
         public void loadException() {
             method.aload(EXCEPTION_INDEX);
         }
         
         public void setPosition(ISourcePosition position) {
             // FIXME I'm still not happy with this additional overhead per line,
             // nor about the extra script construction cost, but it will have to do for now.
             loadThreadContext();
             method.getstatic(classname, cachePosition(position.getFile(), position.getEndLine()), cg.ci(ISourcePosition.class));
             invokeThreadContext("setPosition", cg.sig(void.class, ISourcePosition.class));
         }
         
         public void checkWhenWithSplat() {
             loadThreadContext();
             invokeUtilityMethod("isWhenTriggered", cg.sig(RubyBoolean.class, IRubyObject.class, IRubyObject.class, ThreadContext.class));
         }
         
         public void issueRetryEvent() {
             invokeUtilityMethod("retryJump", cg.sig(IRubyObject.class));
         }
 
         public void defineNewMethod(String name, StaticScope scope, ClosureCallback body, ClosureCallback args, ClosureCallback receiver, ASTInspector inspector) {
             // TODO: build arg list based on number of args, optionals, etc
             ++methodIndex;
             String methodName = cg.cleanJavaIdentifier(name) + "__" + methodIndex;
 
             MethodCompiler methodCompiler = startMethod(methodName, args, scope, inspector);
 
             // callbacks to fill in method body
             body.compile(methodCompiler);
 
             methodCompiler.endMethod();
 
             // prepare to call "def" utility method to handle def logic
             loadThreadContext();
 
             loadSelf();
             
             if (receiver != null) receiver.compile(this);
             
             // script object
             method.aload(THIS);
 
             method.ldc(name);
 
             method.ldc(methodName);
 
             buildStaticScopeNames(method, scope);
 
             method.ldc(scope.getArity().getValue());
             
             // arities
             method.ldc(scope.getRequiredArgs());
             method.ldc(scope.getOptionalArgs());
             method.ldc(scope.getRestArg());
             
             if (inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                 method.getstatic(cg.p(CallConfiguration.class), "RUBY_FULL", cg.ci(CallConfiguration.class));
             } else {
                 method.getstatic(cg.p(CallConfiguration.class), "JAVA_FULL", cg.ci(CallConfiguration.class));
             }
             
             if (receiver != null) {
                 invokeUtilityMethod("defs", cg.sig(IRubyObject.class, 
                         cg.params(ThreadContext.class, IRubyObject.class, IRubyObject.class, Object.class, String.class, String.class, String[].class, int.class, int.class, int.class, int.class, CallConfiguration.class)));
             } else {
                 invokeUtilityMethod("def", cg.sig(IRubyObject.class, 
                         cg.params(ThreadContext.class, IRubyObject.class, Object.class, String.class, String.class, String[].class, int.class, int.class, int.class, int.class, CallConfiguration.class)));
             }
         }
     }
 
     public class ASMClosureCompiler extends AbstractMethodCompiler {
         private String closureMethodName;
         
         public ASMClosureCompiler(String closureMethodName, String closureFieldName, ASTInspector inspector) {
             this.closureMethodName = closureMethodName;
 
             // declare the field
             getClassVisitor().visitField(ACC_PRIVATE, closureFieldName, cg.ci(CompiledBlockCallback.class), null, null);
             
             method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, closureMethodName, CLOSURE_SIGNATURE, null, null));
             if (inspector == null || inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                 variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX);
             } else {
                 variableCompiler = new StackBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, ARGS_INDEX, CLOSURE_INDEX);
             }
             invocationCompiler = new StandardInvocationCompiler(this, method);
         }
 
         public void beginMethod(ClosureCallback args, StaticScope scope) {
             method.start();
 
             // set up a local IRuby variable
             method.aload(THREADCONTEXT_INDEX);
             invokeThreadContext("getRuntime", cg.sig(Ruby.class));
             method.dup();
             method.astore(RUNTIME_INDEX);
             
             // grab nil for local variables
             invokeIRuby("getNil", cg.sig(IRubyObject.class));
             method.astore(NIL_INDEX);
             
             variableCompiler.beginClosure(args, scope);
 
             // start of scoping for closure's vars
             scopeStart = new Label();
             scopeEnd = new Label();
             redoJump = new Label();
             method.label(scopeStart);
         }
 
         public void beginClass(ClosureCallback bodyPrep, StaticScope scope) {
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
             
             method.trycatch(scopeStart, scopeEnd, scopeEnd, cg.p(JumpException.RedoJump.class));
             method.end();
         }
 
         public void loadBlock() {
             loadThreadContext();
             invokeThreadContext("getFrameBlock", cg.sig(Block.class));
         }
 
         protected String getNewRescueName() {
             return closureMethodName + "_" + super.getNewRescueName();
         }
 
         protected String getNewEnsureName() {
             return closureMethodName + "_" + super.getNewEnsureName();
         }
 
         public void performReturn() {
             loadThreadContext();
             invokeUtilityMethod("returnJump", cg.sig(IRubyObject.class, IRubyObject.class, ThreadContext.class));
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
             invokeUtilityMethod("processBlockArgument", cg.sig(void.class, cg.params(Ruby.class, ThreadContext.class, Block.class, int.class)));
         }
         
         public void issueBreakEvent(ClosureCallback value) {
             if (withinProtection || currentLoopLabels == null) {
                 value.compile(this);
                 invokeUtilityMethod("breakJump", cg.sig(IRubyObject.class, IRubyObject.class));
             } else {
                 value.compile(this);
                 issueLoopBreak();
             }
         }
 
         public void issueNextEvent(ClosureCallback value) {
             if (withinProtection || currentLoopLabels == null) {
                 value.compile(this);
                 invokeUtilityMethod("nextJump", cg.sig(IRubyObject.class, IRubyObject.class));
             } else {
                 value.compile(this);
                 issueLoopNext();
             }
         }
 
         public void issueRedoEvent() {
             // FIXME: This isn't right for within ensured/rescued code
             if (withinProtection) {
                 invokeUtilityMethod("redoJump", cg.sig(IRubyObject.class));
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
 
             method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, friendlyName, METHOD_SIGNATURE, null, null));
             if (inspector == null || inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                 variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX);
             } else {
                 variableCompiler = new StackBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, ARGS_INDEX, CLOSURE_INDEX);
             }
             invocationCompiler = new StandardInvocationCompiler(this, method);
         }
         
         public void beginChainedMethod() {
             method.aload(THREADCONTEXT_INDEX);
             method.dup();
             method.invokevirtual(cg.p(ThreadContext.class), "getRuntime", cg.sig(Ruby.class));
             method.dup();
             method.astore(RUNTIME_INDEX);
 
             // grab nil for local variables
             method.invokevirtual(cg.p(Ruby.class), "getNil", cg.sig(IRubyObject.class));
             method.astore(NIL_INDEX);
 
             method.invokevirtual(cg.p(ThreadContext.class), "getCurrentScope", cg.sig(DynamicScope.class));
             method.dup();
             method.astore(DYNAMIC_SCOPE_INDEX);
             method.invokevirtual(cg.p(DynamicScope.class), "getValues", cg.sig(IRubyObject[].class));
             method.astore(VARS_ARRAY_INDEX);
         }
 
         public void beginMethod(ClosureCallback args, StaticScope scope) {
             method.start();
 
             // set up a local IRuby variable
             method.aload(THREADCONTEXT_INDEX);
             invokeThreadContext("getRuntime", cg.sig(Ruby.class));
             method.dup();
             method.astore(RUNTIME_INDEX);
             
             
             // grab nil for local variables
             invokeIRuby("getNil", cg.sig(IRubyObject.class));
             method.astore(NIL_INDEX);
             
             variableCompiler.beginMethod(args, scope);
 
             // visit a label to start scoping for local vars in this method
             Label start = new Label();
             method.label(start);
 
             scopeStart = start;
         }
 
         public void beginClass(ClosureCallback bodyPrep, StaticScope scope) {
             method.start();
 
             // set up a local IRuby variable
             method.aload(THREADCONTEXT_INDEX);
             invokeThreadContext("getRuntime", cg.sig(Ruby.class));
             method.dup();
             method.astore(RUNTIME_INDEX);
             
             // grab nil for local variables
             invokeIRuby("getNil", cg.sig(IRubyObject.class));
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
                 invokeUtilityMethod("returnJump", cg.sig(IRubyObject.class, IRubyObject.class, ThreadContext.class));
             } else {
                 method.areturn();
             }
         }
 
         public void issueBreakEvent(ClosureCallback value) {
             if (withinProtection) {
                 value.compile(this);
                 invokeUtilityMethod("breakJump", cg.sig(IRubyObject.class, IRubyObject.class));
             } else if (currentLoopLabels != null) {
                 value.compile(this);
                 issueLoopBreak();
             } else {
                 // in method body with no containing loop, issue jump error
                 // load runtime and value, issue jump error
                 loadRuntime();
                 value.compile(this);
                 invokeUtilityMethod("breakLocalJumpError", cg.sig(IRubyObject.class, Ruby.class, IRubyObject.class));
             }
         }
 
         public void issueNextEvent(ClosureCallback value) {
             if (withinProtection) {
                 value.compile(this);
                 invokeUtilityMethod("nextJump", cg.sig(IRubyObject.class, IRubyObject.class));
             } else if (currentLoopLabels != null) {
                 value.compile(this);
                 issueLoopNext();
             } else {
                 // in method body with no containing loop, issue jump error
                 // load runtime and value, issue jump error
                 loadRuntime();
                 value.compile(this);
                 invokeUtilityMethod("nextLocalJumpError", cg.sig(IRubyObject.class, Ruby.class, IRubyObject.class));
             }
         }
 
         public void issueRedoEvent() {
             if (withinProtection) {
                 invokeUtilityMethod("redoJump", cg.sig(IRubyObject.class));
             } else if (currentLoopLabels != null) {
                 issueLoopRedo();
             } else {
                 // in method body with no containing loop, issue jump error
                 // load runtime and value, issue jump error
                 loadRuntime();
                 invokeUtilityMethod("redoLocalJumpError", cg.sig(IRubyObject.class, Ruby.class));
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
     
     public String cacheCallAdapter(String name, CallType callType) {
         String fieldName = getNewConstant(cg.ci(CallAdapter.class), cg.cleanJavaIdentifier(name));
         
         // retrieve call adapter
         initMethod.aload(THIS);
         initMethod.ldc(name);
         if (callType.equals(CallType.NORMAL)) {
             initMethod.invokestatic(cg.p(MethodIndex.class), "getCallAdapter", cg.sig(CallAdapter.class, cg.params(String.class)));
         } else if (callType.equals(CallType.FUNCTIONAL)) {
             initMethod.invokestatic(cg.p(MethodIndex.class), "getFunctionAdapter", cg.sig(CallAdapter.class, cg.params(String.class)));
         } else if (callType.equals(CallType.VARIABLE)) {
             initMethod.invokestatic(cg.p(MethodIndex.class), "getVariableAdapter", cg.sig(CallAdapter.class, cg.params(String.class)));
         }
         initMethod.putfield(classname, fieldName, cg.ci(CallAdapter.class));
         
         return fieldName;
     }
     
     public String cachePosition(String file, int line) {
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index 304bc4b690..d8bd0ac66d 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -1,795 +1,803 @@
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
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.SimpleSourcePosition;
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
 public class RuntimeHelpers {
     public static CompiledBlock createBlock(ThreadContext context, IRubyObject self, int arity, 
             String[] staticScopeNames, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argsNodeType, boolean light) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         CompiledBlock block = new CompiledBlock(
                     context,
                     self,
                     Arity.createArity(arity),
                     new DynamicScope(staticScope, context.getCurrentScope()),
                     callback,
                     hasMultipleArgsHead,
                     argsNodeType);
         block.setLight(light);
         
         return block;
     }
     
     public static IRubyObject runBeginBlock(ThreadContext context, IRubyObject self, String[] staticScopeNames, CompiledBlockCallback callback) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         context.preScopedBody(new DynamicScope(staticScope, context.getCurrentScope()));
         
         Block block = new CompiledBlock(context, self, Arity.createArity(0), 
                 context.getCurrentScope(), callback, false, Block.ZERO_ARGS);
         
         block.yield(context, null);
         
         context.postScopedBody();
         
         return context.getRuntime().getNil();
     }
     
     public static CompiledSharedScopeBlock createSharedScopeBlock(ThreadContext context, IRubyObject self, int arity, 
             CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argsNodeType) {
         
         return new CompiledSharedScopeBlock(context, self, Arity.createArity(arity), 
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
                     Arity.createArity(arity), Visibility.PRIVATE, scope, scriptObject);
         } else {
             method = factory.getCompiledMethod(containingClass, javaName, 
                     Arity.createArity(arity), visibility, scope, scriptObject);
         }
         
         method.setCallConfig(callConfig);
         
         containingClass.addMethod(name, method);
         
         if (visibility == Visibility.MODULE_FUNCTION) {
             containingClass.getSingletonClass().addMethod(name,
                     new WrapperMethod(containingClass.getSingletonClass(), method,
                     Visibility.PUBLIC));
             containingClass.callMethod(context, "singleton_method_added", runtime.newSymbol(name));
         }
         
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             ((MetaClass) containingClass).getAttached().callMethod(
                     context, "singleton_method_added", runtime.newSymbol(name));
         } else {
             containingClass.callMethod(context, "method_added", runtime.newSymbol(name));
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
                 Arity.createArity(arity), Visibility.PUBLIC, scope, scriptObject);
         
         method.setCallConfig(callConfig);
         
         rubyClass.addMethod(name, method);
         receiver.callMethod(context, "singleton_method_added", runtime.newSymbol(name));
         
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
         
         try {
             return compilerCallMethod(context, receiver, name, args, caller, callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
     
     public static IRubyObject doAttrAssignIndexed(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, byte methodIndex, String name, IRubyObject caller, 
             CallType callType, Block block) {
         if (receiver == caller) callType = CallType.VARIABLE;
         
         try {
             return compilerCallMethodWithIndex(context, receiver, methodIndex, name, args, caller, 
                     callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
     
     public static IRubyObject doInvokeDynamic(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, String name, IRubyObject caller, CallType callType, Block block) {
         try {
             return compilerCallMethod(context, receiver, name, args, caller, callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
     
     public static IRubyObject doInvokeDynamicIndexed(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, byte methodIndex, String name, IRubyObject caller, 
             CallType callType, Block block) {
         try {
             return compilerCallMethodWithIndex(context, receiver, methodIndex, name, args, caller, 
                     callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
 
     /**
      * Used by the compiler to ease calling indexed methods, also to handle visibility.
      * NOTE: THIS IS NOT THE SAME AS THE SWITCHVALUE VERSIONS.
      */
     public static IRubyObject compilerCallMethodWithIndex(ThreadContext context, IRubyObject receiver, int methodIndex, String name, IRubyObject[] args, IRubyObject caller, CallType callType, Block block) {
         RubyModule module = receiver.getMetaClass();
         
         if (module.index != 0) {
             return receiver.callMethod(context, module, methodIndex, name, args, callType, block);
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
         newArgs[0] = RubySymbol.newSymbol(self.getRuntime(), name);
 
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
         newArgs[0] = RubySymbol.newSymbol(self.getRuntime(), name);
 
         return receiver.callMethod(context, "method_missing", newArgs, block);
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
         if (exception.isKindOf(runtime.fastGetClass("LocalJumpError"))) {
             RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
 
             IRubyObject reason = jumpError.reason();
 
             return reason.asSymbol();
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
             blockArg = runtime.newProc(false, block);
             blockArg.getBlock().isLambda = block.isLambda;
         }
         
         return blockArg;
     }
     
     public static Block getBlockFromBlockPassBody(IRubyObject proc, Block currentBlock) {
         Ruby runtime = proc.getRuntime();
 
         // No block from a nil proc
         if (proc.isNil()) return Block.NULL_BLOCK;
 
         // If not already a proc then we should try and make it one.
         if (!(proc instanceof RubyProc)) {
             proc = proc.convertToType(runtime.getProc(), 0, "to_proc", false);
 
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
             if (!exceptions[i].isKindOf(runtime.getModule())) {
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
         return context.setConstantInModule(name, (RubyModule)module, value);
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
     
     public static ISourcePosition constructPosition(String file, int line) {
         return new SimpleSourcePosition(file, line);
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
         hash.put(key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.put(key1, value1);
         hash.put(key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.put(key1, value1);
         hash.put(key2, value2);
         hash.put(key3, value3);
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
         IRubyObject result = self.getInstanceVariable(name);
         if (result == null) return runtime.getNil();
         return result;
     }
     
     public static IRubyObject fastGetInstanceVariable(Ruby runtime, IRubyObject self, String internedName) {
         IRubyObject result;
         if ((result = self.fastGetInstanceVariable(internedName)) != null) return result;
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
         StaticScope staticScope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), varNames);
         staticScope.setModule(context.getRuntime().getObject());
         DynamicScope scope = new DynamicScope(staticScope);
         
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
     }
     
     public static void postLoad(ThreadContext context) {
         context.postScopedBody();
     }
     
     public static void registerEndBlock(CompiledSharedScopeBlock block, Ruby runtime) {
         runtime.pushExitBlock(runtime.newProc(true, block));
     }
     
     public static IRubyObject match3(RubyRegexp regexp, IRubyObject value, ThreadContext context) {
         if (value instanceof RubyString) {
             return regexp.op_match(value);
         } else {
             return value.callMethod(context, "=~", regexp);
         }
     }
+    
+    public static IRubyObject getErrorInfo(Ruby runtime) {
+        return runtime.getGlobalVariables().get("$!");
+    }
+    
+    public static void setErrorInfo(Ruby runtime, IRubyObject error) {
+        runtime.getGlobalVariables().set("$!", error);
+    }
 }
diff --git a/test/rubicon/test_exception.rb b/test/rubicon/test_exception.rb
index 627f11edd1..88a7f8959c 100644
--- a/test/rubicon/test_exception.rb
+++ b/test/rubicon/test_exception.rb
@@ -1,125 +1,123 @@
 require 'test/unit'
 
 
 class TestException < Test::Unit::TestCase
 
   MSG = "duck"
 
   def test_s_exception
     e = Exception.exception
     assert_equal(Exception, e.class)
 
     e = Exception.exception(MSG)
     assert_equal(MSG, e.message)
   end
 
   def test_backtrace
     assert_nil(Exception.exception.backtrace)
     begin
       line=__LINE__; file=__FILE__; raise MSG
     rescue RuntimeError => detail
       assert_equal(RuntimeError, detail.class)
       assert_equal(MSG, detail.message)
       expected = "#{file}:#{line}:in `test_backtrace'"
       assert_equal(expected, detail.backtrace[0])
     end
   end
 
   def test_exception
     e = IOError.new
     assert_equal(IOError, e.class)
     assert_equal(IOError, e.exception.class)
     assert_equal(e,       e.exception)
 
     e = IOError.new
     e1 = e.exception(MSG)
     assert_equal(IOError, e1.class)
     assert_equal(MSG,     e1.message)
   end
 
   def test_message
     e = IOError.new(MSG)
     assert_equal(MSG, e.message)
   end
 
   def test_set_backtrace
     e = IOError.new
     a = %w( here there everywhere )
     assert_equal(a, e.set_backtrace(a))
     assert_equal(a, e.backtrace)
   end
 
   # exercise bug in Exception#set_backtrace, see [ruby-talk:96273].
   class Problem # helper class for #test_set_backtrace2
     STACK = %w(a:0:A b:1:B c:2:C)
     def self.mk_problem
       raise IOError, "got nuked"
     rescue IOError => e
       error = IOError.new("got nuked")
       error.set_backtrace(STACK)
       raise error
     end
     def self.mk_exception
       begin
         self.mk_problem
         raise "should not happen"
       rescue IOError => e
         return e
       end
     end
   end
   def test_set_backtrace2
     e = Problem.mk_exception
     assert_equal("got nuked", e.message)
     # this is how set_backtrace is suppose to work
     assert_equal(Problem::STACK, e.backtrace)
   end
   
   # Test that $! and $@ are thread-local
   def test_error_info_thread_local
     exception_in_thread = nil
     exception_setting_backtrace = nil
     backtrace_in_thread = nil
     
     t = Thread.new {
       Thread.stop
       exception_in_thread = $!
       begin
         backtrace_in_thread = $@
         $@ = ["foo"]
       rescue ArgumentError => exception_setting_backtrace
       end
     }
     
     1 while t.status != "sleep" # wait for it to sleep
     
     begin
       raise
     rescue Exception => e
       assert_equal($!, e)
       t.run
       t.join
       $@ = ["bar"]
       assert_equal(["bar"], $@)
     end
     
     assert_equal(nil, exception_in_thread)
     assert_equal(ArgumentError, exception_setting_backtrace.class)
     assert_equal(nil, backtrace_in_thread)
   end
   
   # test that $! gets cleared when "next"ing in a rescue block
-=begin disabled until I can fix the compiler
   def test_exception_cleared_when_non_local_flow_control
     1.times do
       begin
         raise
       rescue
         next
       end
     end
     
     assert_equal(nil, $!)
   end
-=end
 end
diff --git a/test/testCompiler.rb b/test/testCompiler.rb
index 2ad95b32d8..cf1de03665 100644
--- a/test/testCompiler.rb
+++ b/test/testCompiler.rb
@@ -1,451 +1,458 @@
 require 'jruby'
 require 'java'
 require 'test/minirunit'
 
 StandardASMCompiler = org.jruby.compiler.impl.StandardASMCompiler
 ASTCompiler = org.jruby.compiler.ASTCompiler
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
   ASTCompiler.compileRoot(node, context, inspector)
 
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
+  
+  # test that $! is getting reset/cleared appropriately
+  $! = nil
+  test_equal(nil, compile_and_run("begin; raise; rescue; end; $!"))
+  test_equal(nil, compile_and_run("1.times { begin; raise; rescue; next; end }; $!"))
+  test_ok(nil != compile_and_run("begin; raise; rescue; begin; raise; rescue; end; $!; end"))
+  test_ok(nil != compile_and_run("begin; raise; rescue; 1.times { begin; raise; rescue; next; end }; $!; end"))
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
\ No newline at end of file
