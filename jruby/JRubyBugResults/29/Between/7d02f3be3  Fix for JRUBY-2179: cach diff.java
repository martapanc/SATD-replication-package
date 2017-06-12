diff --git a/src/org/jruby/compiler/CacheCompiler.java b/src/org/jruby/compiler/CacheCompiler.java
index 6a31b92bdd..782eac1d5b 100644
--- a/src/org/jruby/compiler/CacheCompiler.java
+++ b/src/org/jruby/compiler/CacheCompiler.java
@@ -1,21 +1,24 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.compiler;
 
+import java.math.BigInteger;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.runtime.CallType;
 
 /**
  *
  * @author headius
  */
 public interface CacheCompiler {
     public void cacheCallSite(SkinnyMethodAdapter method, String name, CallType callType);
     
     public void cacheByteList(SkinnyMethodAdapter method, String contents);
     
     public void cacheSymbol(SkinnyMethodAdapter method, String symbol);
+    
+    public void cacheBigInteger(SkinnyMethodAdapter method, BigInteger bigint);
 }
diff --git a/src/org/jruby/compiler/impl/FieldBasedCacheCompiler.java b/src/org/jruby/compiler/impl/FieldBasedCacheCompiler.java
index e74c4bfb00..be9d0d0e21 100644
--- a/src/org/jruby/compiler/impl/FieldBasedCacheCompiler.java
+++ b/src/org/jruby/compiler/impl/FieldBasedCacheCompiler.java
@@ -1,132 +1,151 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.compiler.impl;
 
+import java.math.BigInteger;
 import java.util.HashMap;
 import java.util.Map;
 import org.jruby.Ruby;
 import org.jruby.RubySymbol;
 import org.jruby.compiler.CacheCompiler;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.util.ByteList;
 import org.jruby.util.JavaNameMangler;
 import org.objectweb.asm.ClassVisitor;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.Opcodes;
 import static org.jruby.util.CodegenUtils.*;
 
 /**
  *
  * @author headius
  */
 public class FieldBasedCacheCompiler implements CacheCompiler {
-    private StandardASMCompiler scriptCompiler;
+    protected StandardASMCompiler scriptCompiler;
     
     Map<String, String> sourcePositions = new HashMap<String, String>();
     Map<String, String> byteLists = new HashMap<String, String>();
+    Map<BigInteger, String> bigIntegers = new HashMap<BigInteger, String>();
     Map<String, String> symbols = new HashMap<String, String>();
     
     public FieldBasedCacheCompiler(StandardASMCompiler scriptCompiler) {
         this.scriptCompiler = scriptCompiler;
     }
     
     public void cacheCallSite(SkinnyMethodAdapter method, String name, CallType callType) {
         String fieldName = scriptCompiler.getNewConstant(ci(CallSite.class), JavaNameMangler.mangleStringForCleanJavaIdentifier(name));
         
         // retrieve call adapter
         SkinnyMethodAdapter initMethod = scriptCompiler.getInitMethod();
         initMethod.aload(StandardASMCompiler.THIS);
         initMethod.ldc(name);
         
         if (callType.equals(CallType.NORMAL)) {
             initMethod.invokestatic(p(MethodIndex.class), "getCallSite", sig(CallSite.class, params(String.class)));
         } else if (callType.equals(CallType.FUNCTIONAL)) {
             initMethod.invokestatic(p(MethodIndex.class), "getFunctionalCallSite", sig(CallSite.class, params(String.class)));
         } else if (callType.equals(CallType.VARIABLE)) {
             initMethod.invokestatic(p(MethodIndex.class), "getVariableCallSite", sig(CallSite.class, params(String.class)));
         }
 
         initMethod.putfield(scriptCompiler.getClassname(), fieldName, ci(CallSite.class));
         
         method.getfield(scriptCompiler.getClassname(), fieldName, ci(CallSite.class));
     }
     
     public void cachePosition(SkinnyMethodAdapter method, String file, int line) {
         String cleanName = JavaNameMangler.mangleStringForCleanJavaIdentifier(file + "$" + line);
         String fieldName = sourcePositions.get(cleanName);
         if (fieldName == null) {
             SkinnyMethodAdapter clinitMethod = scriptCompiler.getClassInitMethod();
             fieldName = scriptCompiler.getNewStaticConstant(ci(ISourcePosition.class), cleanName);
             sourcePositions.put(JavaNameMangler.mangleStringForCleanJavaIdentifier(file + "$" + line), fieldName);
 
             clinitMethod.ldc(file);
             clinitMethod.ldc(line);
             clinitMethod.invokestatic(p(RuntimeHelpers.class), "constructPosition", sig(ISourcePosition.class, String.class, int.class));
             clinitMethod.putstatic(scriptCompiler.getClassname(), fieldName, ci(ISourcePosition.class));
         }
         
         method.getstatic(scriptCompiler.getClassname(), fieldName, ci(ISourcePosition.class));
     }
     
     public void cacheByteList(SkinnyMethodAdapter method, String contents) {
         String fieldName = byteLists.get(contents);
         if (fieldName == null) {
             SkinnyMethodAdapter clinitMethod = scriptCompiler.getClassInitMethod();
             fieldName = scriptCompiler.getNewStaticConstant(ci(ByteList.class), "byteList");
             byteLists.put(contents, fieldName);
 
             clinitMethod.ldc(contents);
             clinitMethod.invokestatic(p(ByteList.class), "create", sig(ByteList.class, CharSequence.class));
             clinitMethod.putstatic(scriptCompiler.getClassname(), fieldName, ci(ByteList.class));
         }
         
         method.getstatic(scriptCompiler.getClassname(), fieldName, ci(ByteList.class));
     }
     
+    public void cacheBigInteger(SkinnyMethodAdapter method, BigInteger bigint) {
+        String fieldName = bigIntegers.get(bigint);
+        if (fieldName == null) {
+            SkinnyMethodAdapter clinitMethod = scriptCompiler.getClassInitMethod();
+            fieldName = scriptCompiler.getNewStaticConstant(ci(BigInteger.class), "bigInt");
+            bigIntegers.put(bigint, fieldName);
+
+            clinitMethod.newobj(p(BigInteger.class));
+            clinitMethod.dup();
+            clinitMethod.ldc(bigint.toString());
+            clinitMethod.invokespecial(p(BigInteger.class), "<init>", sig(void.class, String.class));
+            clinitMethod.putstatic(scriptCompiler.getClassname(), fieldName, ci(BigInteger.class));
+        }
+        
+        method.getstatic(scriptCompiler.getClassname(), fieldName, ci(BigInteger.class));
+    }
+    
     public void cacheSymbol(SkinnyMethodAdapter method, String symbol) {
         String methodName = symbols.get(symbol);
         if (methodName == null) {
             String fieldName = scriptCompiler.getNewConstant(ci(RubySymbol.class), "symbol");
             
             methodName = "getSymbol" + fieldName;
             symbols.put(symbol, methodName);
             
             ClassVisitor cv = scriptCompiler.getClassVisitor();
             
             SkinnyMethodAdapter symMethod = new SkinnyMethodAdapter(
                     cv.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC, methodName, 
                             sig(RubySymbol.class, Ruby.class), null, null));
             symMethod.start();
             symMethod.aload(0);
             symMethod.getfield(scriptCompiler.getClassname(), fieldName, ci(RubySymbol.class));
             symMethod.dup();
             symMethod.astore(2);
             
             Label ifNullEnd = new Label();
             symMethod.ifnull(ifNullEnd);
             symMethod.aload(2);
             symMethod.areturn();
             symMethod.label(ifNullEnd);
             symMethod.aload(0);
             symMethod.aload(1);
             symMethod.ldc(symbol);
             symMethod.invokevirtual(p(Ruby.class), "fastNewSymbol", sig(RubySymbol.class, String.class));
             symMethod.dup_x1();
             symMethod.putfield(scriptCompiler.getClassname(), fieldName, ci(RubySymbol.class));
             symMethod.areturn();
             symMethod.end();
         }
         
         method.aload(0);
         method.aload(StandardASMCompiler.RUNTIME_INDEX);
         method.invokevirtual(scriptCompiler.getClassname(), methodName, 
                 sig(RubySymbol.class, params(Ruby.class)));
     }
 }
diff --git a/src/org/jruby/compiler/impl/InheritedCacheCompiler.java b/src/org/jruby/compiler/impl/InheritedCacheCompiler.java
index 5a208cdf64..3cb60d10c1 100644
--- a/src/org/jruby/compiler/impl/InheritedCacheCompiler.java
+++ b/src/org/jruby/compiler/impl/InheritedCacheCompiler.java
@@ -1,125 +1,56 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.compiler.impl;
 
-import java.util.HashMap;
-import java.util.Map;
-import org.jruby.Ruby;
-import org.jruby.RubySymbol;
-import org.jruby.compiler.CacheCompiler;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.MethodIndex;
-import org.jruby.util.ByteList;
-import org.objectweb.asm.ClassVisitor;
-import org.objectweb.asm.Label;
-import org.objectweb.asm.Opcodes;
 import static org.jruby.util.CodegenUtils.*;
 
 /**
  *
  * @author headius
  */
-public class InheritedCacheCompiler implements CacheCompiler {
-    private StandardASMCompiler scriptCompiler;
-    
+public class InheritedCacheCompiler extends FieldBasedCacheCompiler {
     public static final int MAX_INHERITED_CALL_SITES = 0;
     
     int callSiteCount = 0;
 //    int byteListCount = 0;
 //    int sourcePositionsCount = 0;
 //    int symbolCount = 0;
     
-    Map<String, String> byteLists = new HashMap<String, String>();
-    Map<String, String> symbols = new HashMap<String, String>();
-    
     public InheritedCacheCompiler(StandardASMCompiler scriptCompiler) {
-        this.scriptCompiler = scriptCompiler;
+        super(scriptCompiler);
     }
     
     public void cacheCallSite(SkinnyMethodAdapter method, String name, CallType callType) {
         String fieldName = "site" + callSiteCount;
         
         // retrieve call adapter
         SkinnyMethodAdapter initMethod = scriptCompiler.getInitMethod();
         initMethod.aload(StandardASMCompiler.THIS);
         method.aload(StandardASMCompiler.THIS);
         initMethod.ldc(name);
         
         if (callType.equals(CallType.NORMAL)) {
             initMethod.invokestatic(p(MethodIndex.class), "getCallSite", sig(CallSite.class, params(String.class)));
         } else if (callType.equals(CallType.FUNCTIONAL)) {
             initMethod.invokestatic(p(MethodIndex.class), "getFunctionalCallSite", sig(CallSite.class, params(String.class)));
         } else if (callType.equals(CallType.VARIABLE)) {
             initMethod.invokestatic(p(MethodIndex.class), "getVariableCallSite", sig(CallSite.class, params(String.class)));
         }
 
         if (callSiteCount >= MAX_INHERITED_CALL_SITES) {
             scriptCompiler.getNewField(ci(CallSite.class), fieldName, null);
             initMethod.putfield(scriptCompiler.getClassname(), fieldName, ci(CallSite.class));
             method.getfield(scriptCompiler.getClassname(), fieldName, ci(CallSite.class));
         } else {
             initMethod.putfield(scriptCompiler.getClassname(), fieldName, ci(CallSite.class));
             method.getfield(scriptCompiler.getClassname(), fieldName, ci(CallSite.class));
         }
         callSiteCount++;
     }
-    
-    public void cacheByteList(SkinnyMethodAdapter method, String contents) {
-        String fieldName = byteLists.get(contents);
-        if (fieldName == null) {
-            SkinnyMethodAdapter clinitMethod = scriptCompiler.getClassInitMethod();
-            fieldName = scriptCompiler.getNewStaticConstant(ci(ByteList.class), "byteList");
-            byteLists.put(contents, fieldName);
-
-            clinitMethod.ldc(contents);
-            clinitMethod.invokestatic(p(ByteList.class), "create", sig(ByteList.class, CharSequence.class));
-            clinitMethod.putstatic(scriptCompiler.getClassname(), fieldName, ci(ByteList.class));
-        }
-        
-        method.getstatic(scriptCompiler.getClassname(), fieldName, ci(ByteList.class));
-    }
-    
-    public void cacheSymbol(SkinnyMethodAdapter method, String symbol) {
-        String methodName = symbols.get(symbol);
-        if (methodName == null) {
-            String fieldName = scriptCompiler.getNewConstant(ci(RubySymbol.class), "symbol");
-            
-            methodName = "getSymbol" + fieldName;
-            symbols.put(symbol, methodName);
-            
-            ClassVisitor cv = scriptCompiler.getClassVisitor();
-            
-            SkinnyMethodAdapter symMethod = new SkinnyMethodAdapter(
-                    cv.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC, methodName, 
-                            sig(RubySymbol.class, Ruby.class), null, null));
-            symMethod.start();
-            symMethod.aload(0);
-            symMethod.getfield(scriptCompiler.getClassname(), fieldName, ci(RubySymbol.class));
-            symMethod.dup();
-            symMethod.astore(2);
-            
-            Label ifNullEnd = new Label();
-            symMethod.ifnull(ifNullEnd);
-            symMethod.aload(2);
-            symMethod.areturn();
-            symMethod.label(ifNullEnd);
-            symMethod.aload(0);
-            symMethod.aload(1);
-            symMethod.ldc(symbol);
-            symMethod.invokevirtual(p(Ruby.class), "fastNewSymbol", sig(RubySymbol.class, String.class));
-            symMethod.dup_x1();
-            symMethod.putfield(scriptCompiler.getClassname(), fieldName, ci(RubySymbol.class));
-            symMethod.areturn();
-            symMethod.end();
-        }
-        
-        method.aload(0);
-        method.aload(StandardASMCompiler.RUNTIME_INDEX);
-        method.invokevirtual(scriptCompiler.getClassname(), methodName, 
-                sig(RubySymbol.class, params(Ruby.class)));
-    }
 }
diff --git a/src/org/jruby/compiler/impl/StandardASMCompiler.java b/src/org/jruby/compiler/impl/StandardASMCompiler.java
index 290e15255f..4efe05e389 100644
--- a/src/org/jruby/compiler/impl/StandardASMCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardASMCompiler.java
@@ -1,1613 +1,1613 @@
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
+import java.io.UnsupportedEncodingException;
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
     public static final int FIRST_TEMP_INDEX = 11;
     
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
         private int lastLine = -1;
 
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
             int thisLine = position.getStartLine();
             
             // No point in updating number if last number was same value.
             if (thisLine != lastLine) {
                 lastLine = thisLine;
             } else {
                 return;
             }
             
             Label line = new Label();
             method.label(line);
             method.visitLineNumber(thisLine + 1, line);
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
-            method.ldc(value.toString());
-
-            method.invokestatic(p(RubyBignum.class), "newBignum", sig(RubyBignum.class, params(Ruby.class, String.class)));
+            getCacheCompiler().cacheBigInteger(method, value);
+            method.invokestatic(p(RubyBignum.class), "newBignum", sig(RubyBignum.class, params(Ruby.class, BigInteger.class)));
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
                 int line,
                 StaticScope scope,
                 int arity,
                 CompilerCallback body,
                 CompilerCallback args,
                 boolean hasMultipleArgsHead,
                 NodeType argsNodeId,
                 ASTInspector inspector) {
             String closureMethodName = "closure_" + line + "_" + ++innerIndex;
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
                 method.putfield(classname, regexpField, ci(RubyRegexp.class));
                 method.label(alreadyCreated);
                 method.aload(THIS);
                 method.getfield(classname, regexpField, ci(RubyRegexp.class));
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
