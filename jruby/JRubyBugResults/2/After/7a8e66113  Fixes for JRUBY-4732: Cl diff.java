diff --git a/src/org/jruby/RubyJRuby.java b/src/org/jruby/RubyJRuby.java
index c017d9c19d..4e85860806 100644
--- a/src/org/jruby/RubyJRuby.java
+++ b/src/org/jruby/RubyJRuby.java
@@ -1,779 +1,784 @@
 /*
  **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2009 MenTaLguY <mental@rydia.net>
  * Copyright (C) 2010 Charles Oliver Nutter <headius@headius.com>
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
 package org.jruby;
 
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.lang.management.ManagementFactory;
 import java.lang.management.ThreadMXBean;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.Iterator;
 import java.util.List;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.anno.JRubyClass;
 
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ListNode;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 
 import org.jruby.ast.Node;
 import org.jruby.ast.types.INameNode;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.internal.runtime.methods.MethodArgs;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.InterpretedBlock;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.util.TypeConverter;
 import org.objectweb.asm.ClassReader;
 import org.objectweb.asm.util.TraceClassVisitor;
 
 import java.util.Map;
+import org.jruby.java.proxies.JavaProxy;
 import org.jruby.runtime.ExecutionContext;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.Visibility;
 
 /**
  * Module which defines JRuby-specific methods for use. 
  */
 @JRubyModule(name="JRuby")
 public class RubyJRuby {
     public static RubyModule createJRuby(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
         runtime.getKernel().callMethod(context, "require", runtime.newString("java"));
         RubyModule jrubyModule = runtime.defineModule("JRuby");
 
         jrubyModule.defineAnnotatedMethods(RubyJRuby.class);
         jrubyModule.defineAnnotatedMethods(UtilLibrary.class);
 
         RubyClass compiledScriptClass = jrubyModule.defineClassUnder("CompiledScript",runtime.getObject(), runtime.getObject().getAllocator());
 
         for (String name : new String[] {"name", "class_name", "original_script", "code"}) {
             compiledScriptClass.addReadWriteAttribute(context, name);
         }
         compiledScriptClass.defineAnnotatedMethods(JRubyCompiledScript.class);
 
         RubyClass threadLocalClass = jrubyModule.defineClassUnder("ThreadLocal", runtime.getObject(), JRubyThreadLocal.ALLOCATOR);
         threadLocalClass.defineAnnotatedMethods(JRubyExecutionContextLocal.class);
 
         RubyClass fiberLocalClass = jrubyModule.defineClassUnder("FiberLocal", runtime.getObject(), JRubyFiberLocal.ALLOCATOR);
         fiberLocalClass.defineAnnotatedMethods(JRubyExecutionContextLocal.class);
 
         return jrubyModule;
     }
 
     public static RubyModule createJRubyExt(Ruby runtime) {
         runtime.getKernel().callMethod(runtime.getCurrentContext(),"require", runtime.newString("java"));
         RubyModule mJRubyExt = runtime.getOrCreateModule("JRuby").defineModuleUnder("Extensions");
         
         mJRubyExt.defineAnnotatedMethods(JRubyExtensions.class);
 
         runtime.getObject().includeModule(mJRubyExt);
 
         return mJRubyExt;
     }
 
     public static void createJRubyCoreExt(Ruby runtime) {
         runtime.getClassClass().defineAnnotatedMethods(JRubyClassExtensions.class);
         runtime.getThread().defineAnnotatedMethods(JRubyThreadExtensions.class);
         runtime.getString().defineAnnotatedMethods(JRubyStringExtensions.class);
     }
 
     public static class JRubySynchronizedMeta {
         @JRubyMethod(frame = true, visibility = Visibility.PRIVATE)
         public static IRubyObject append_features(IRubyObject self, IRubyObject target) {
             if (target instanceof RubyClass && self instanceof RubyModule) { // should always be true
                 RubyClass targetModule = ((RubyClass)target);
                 targetModule.becomeSynchronized();
                 return ((RubyModule)self).append_features(target);
             }
             throw target.getRuntime().newTypeError(self + " can only be included into classes");
         }
 
         @JRubyMethod(frame = true, visibility = Visibility.PRIVATE)
         public static IRubyObject extend_object(IRubyObject self, IRubyObject obj) {
             if (self instanceof RubyModule) {
                 RubyClass singletonClass = obj.getSingletonClass();
                 singletonClass.becomeSynchronized();
                 return ((RubyModule)self).extend_object(obj);
             }
             // should never happen
             throw self.getRuntime().newTypeError("JRuby::Singleton.extend_object called against " + self);
         }
     }
 
     public static class ExtLibrary implements Library {
         public void load(Ruby runtime, boolean wrap) throws IOException {
             RubyJRuby.createJRubyExt(runtime);
             
             runtime.getMethod().defineAnnotatedMethods(MethodExtensions.class);
         }
     }
 
     public static class CoreExtLibrary implements Library {
         public void load(Ruby runtime, boolean wrap) throws IOException {
             RubyJRuby.createJRubyCoreExt(runtime);
         }
     }
 
     public static class SynchronizedLibrary implements Library {
         public void load(Ruby runtime, boolean wrap) throws IOException {
             RubyModule syncModule = runtime.getOrCreateModule("JRuby").defineModuleUnder("Synchronized");
             syncModule.getSingletonClass().defineAnnotatedMethods(JRubySynchronizedMeta.class);
 
             // make Synchronized itself be synchronized, so subclasses created later pick that up
             syncModule.becomeSynchronized();
         }
     }
     
     public static class TypeLibrary implements Library {
         public void load(Ruby runtime, boolean wrap) throws IOException {
             RubyModule jrubyType = runtime.defineModule("Type");
             jrubyType.defineAnnotatedMethods(TypeLibrary.class);
         }
         
         @JRubyMethod(module = true)
         public static IRubyObject coerce_to(ThreadContext context, IRubyObject self, IRubyObject object, IRubyObject clazz, IRubyObject method) {
             Ruby ruby = object.getRuntime();
             
             if (!(clazz instanceof RubyClass)) {
                 throw ruby.newTypeError(clazz, ruby.getClassClass());
             }
             if (!(method instanceof RubySymbol)) {
                 throw ruby.newTypeError(method, ruby.getSymbol());
             }
             
             RubyClass rubyClass = (RubyClass)clazz;
             RubySymbol methodSym = (RubySymbol)method;
             
             return TypeConverter.convertToTypeOrRaise(object, rubyClass, methodSym.asJavaString());
         }
     }
 
     /**
      * Utilities library for all those methods that don't need the full 'java' library
      * to be loaded. This is done mostly for performance reasons. For example, for those
      * who only need to enable the object space, not loading 'java' might save 200-300ms
      * of startup time, like in case of jirb.
      */
     public static class UtilLibrary implements Library {
         public void load(Ruby runtime, boolean wrap) throws IOException {
             RubyModule mJRubyUtil = runtime.getOrCreateModule("JRuby").defineModuleUnder("Util");
             mJRubyUtil.defineAnnotatedMethods(UtilLibrary.class);
         }
         @JRubyMethod(module = true)
         public static void gc(IRubyObject recv) {
             System.gc();
         }
         @JRubyMethod(name = "objectspace", module = true)
         public static IRubyObject getObjectSpaceEnabled(IRubyObject recv) {
             Ruby runtime = recv.getRuntime();
             return RubyBoolean.newBoolean(
                     runtime, runtime.isObjectSpaceEnabled());
         }
         @JRubyMethod(name = "objectspace=", module = true)
         public static IRubyObject setObjectSpaceEnabled(
                 IRubyObject recv, IRubyObject arg) {
             Ruby runtime = recv.getRuntime();
             runtime.setObjectSpaceEnabled(arg.isTrue());
             return runtime.getNil();
         }
         @SuppressWarnings("deprecation")
         @JRubyMethod(name = "classloader_resources", module = true)
         public static IRubyObject getClassLoaderResources(IRubyObject recv, IRubyObject arg) {
             Ruby runtime = recv.getRuntime();
             String resource = arg.convertToString().toString();
             final List<RubyString> urlStrings = new ArrayList<RubyString>();
 
             try {
                 Enumeration<URL> urls = runtime.getJRubyClassLoader().getResources(resource);
                 while (urls.hasMoreElements()) {
                     URL url = urls.nextElement();
 
                     String urlString;
                     if ("jar".equals(url.getProtocol()) && url.getFile().startsWith("file:/")) {
                         urlString = URLDecoder.decode(url.getFile());
                     } else {
                         urlString = url.getFile();
                     }
 
                     urlStrings.add(runtime.newString(urlString));
                 }
                 return RubyArray.newArrayNoCopy(runtime,
                         urlStrings.toArray(new IRubyObject[urlStrings.size()]));
             } catch (IOException ignore) {}
 
             return runtime.newEmptyArray();
         }
     }
 
     @JRubyMethod(name = "runtime", frame = true, module = true)
     public static IRubyObject runtime(IRubyObject recv, Block unusedBlock) {
         return JavaUtil.convertJavaToUsableRubyObject(recv.getRuntime(), recv.getRuntime());
     }
 
     @JRubyMethod(frame = true, module = true)
     public static IRubyObject with_current_runtime_as_global(ThreadContext context, IRubyObject recv, Block block) {
         Ruby currentRuntime = context.getRuntime();
         Ruby globalRuntime = Ruby.getGlobalRuntime();
         try {
             if (globalRuntime != currentRuntime) {
                 currentRuntime.useAsGlobalRuntime();
             }
             block.yieldSpecific(context);
         } finally {
             if (Ruby.getGlobalRuntime() != globalRuntime) {
                 globalRuntime.useAsGlobalRuntime();
             }
         }
         return currentRuntime.getNil();
     }
 
     @JRubyMethod(name = {"parse", "ast_for"}, optional = 3, frame = true, module = true)
     public static IRubyObject parse(IRubyObject recv, IRubyObject[] args, Block block) {
         if(block.isGiven()) {
             if(block.getBody() instanceof org.jruby.runtime.CompiledBlock) {
                 throw new RuntimeException("Cannot compile an already compiled block. Use -J-Djruby.jit.enabled=false to avoid this problem.");
             }
             Arity.checkArgumentCount(recv.getRuntime(),args,0,0);
             return JavaUtil.convertJavaToUsableRubyObject(recv.getRuntime(), ((InterpretedBlock)block.getBody()).getBodyNode());
         } else {
             Arity.checkArgumentCount(recv.getRuntime(),args,1,3);
             String filename = "-";
             boolean extraPositionInformation = false;
             RubyString content = args[0].convertToString();
             if(args.length>1) {
                 filename = args[1].convertToString().toString();
                 if(args.length>2) {
                     extraPositionInformation = args[2].isTrue();
                 }
             }
             return JavaUtil.convertJavaToUsableRubyObject(recv.getRuntime(),
                recv.getRuntime().parse(content.getByteList(), filename, null, 0, extraPositionInformation));
         }
     }
 
     @JRubyMethod(name = "compile", optional = 3, frame = true, module = true)
     public static IRubyObject compile(IRubyObject recv, IRubyObject[] args, Block block) {
         Node node;
         String filename;
         RubyString content;
         if(block.isGiven()) {
             Arity.checkArgumentCount(recv.getRuntime(),args,0,0);
             if(block.getBody() instanceof org.jruby.runtime.CompiledBlock) {
                 throw new RuntimeException("Cannot compile an already compiled block. Use -J-Djruby.jit.enabled=false to avoid this problem.");
             }
             content = RubyString.newEmptyString(recv.getRuntime());
             Node bnode = ((InterpretedBlock)block.getBody()).getBodyNode();
             node = new org.jruby.ast.RootNode(bnode.getPosition(), block.getBinding().getDynamicScope(), bnode);
             filename = "__block_" + node.getPosition().getFile();
         } else {
             Arity.checkArgumentCount(recv.getRuntime(),args,1,3);
             filename = "-";
             boolean extraPositionInformation = false;
             content = args[0].convertToString();
             if(args.length>1) {
                 filename = args[1].convertToString().toString();
                 if(args.length>2) {
                     extraPositionInformation = args[2].isTrue();
                 }
             }
 
             node = recv.getRuntime().parse(content.getByteList(), filename, null, 0, extraPositionInformation);
         }
 
         String classname;
         if (filename.equals("-e")) {
             classname = "__dash_e__";
         } else {
             classname = filename.replace('\\', '/').replaceAll(".rb", "").replaceAll("-","_dash_");
         }
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(node);
             
         StandardASMCompiler asmCompiler = new StandardASMCompiler(classname, filename);
         ASTCompiler compiler = recv.getRuntime().getInstanceConfig().newCompiler();
         compiler.compileRoot(node, asmCompiler, inspector);
         byte[] bts = asmCompiler.getClassByteArray();
 
         IRubyObject compiledScript = ((RubyModule)recv).fastGetConstant("CompiledScript").callMethod(recv.getRuntime().getCurrentContext(),"new");
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "name=", recv.getRuntime().newString(filename));
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "class_name=", recv.getRuntime().newString(classname));
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "original_script=", content);
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "code=", JavaUtil.convertJavaToUsableRubyObject(recv.getRuntime(), bts));
 
         return compiledScript;
     }
 
     @JRubyMethod(name = "reference", required = 1, module = true)
     public static IRubyObject reference(ThreadContext context, IRubyObject recv, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
 
         return Java.getInstance(runtime, obj);
     }
 
     @JRubyMethod(name = "dereference", required = 1, module = true)
     public static IRubyObject dereference(ThreadContext context, IRubyObject recv, IRubyObject obj) {
-        if (!(obj.dataGetStruct() instanceof JavaObject)) {
+        Object unwrapped;
+
+        if (obj instanceof JavaProxy) {
+            unwrapped = ((JavaProxy)obj).getObject();
+        } else if (obj.dataGetStruct() instanceof JavaObject) {
+            unwrapped = JavaUtil.unwrapJavaObject(obj);
+        } else {
             throw context.getRuntime().newTypeError("got " + obj + ", expected wrapped Java object");
         }
-        
-        Object unwrapped = JavaUtil.unwrapJavaObject(obj);
 
         if (!(unwrapped instanceof IRubyObject)) {
             throw context.getRuntime().newTypeError("got " + obj + ", expected Java-wrapped Ruby object");
         }
 
         return (IRubyObject)unwrapped;
     }
 
     @JRubyClass(name="JRuby::CompiledScript")
     public static class JRubyCompiledScript {
         @JRubyMethod(name = "to_s")
         public static IRubyObject compiled_script_to_s(IRubyObject recv) {
             return recv.getInstanceVariables().fastGetInstanceVariable("@original_script");
         }
 
         @JRubyMethod(name = "inspect")
         public static IRubyObject compiled_script_inspect(IRubyObject recv) {
             return recv.getRuntime().newString("#<JRuby::CompiledScript " + recv.getInstanceVariables().fastGetInstanceVariable("@name") + ">");
         }
 
         @JRubyMethod(name = "inspect_bytecode")
         public static IRubyObject compiled_script_inspect_bytecode(IRubyObject recv) {
             StringWriter sw = new StringWriter();
             ClassReader cr = new ClassReader((byte[])recv.getInstanceVariables().fastGetInstanceVariable("@code").toJava(byte[].class));
             TraceClassVisitor cv = new TraceClassVisitor(new PrintWriter(sw));
             cr.accept(cv, ClassReader.SKIP_DEBUG);
             return recv.getRuntime().newString(sw.toString());
         }
     }
 
     public abstract static class JRubyExecutionContextLocal extends RubyObject {
         private IRubyObject default_value;
         private RubyProc default_proc;
 
         public JRubyExecutionContextLocal(Ruby runtime, RubyClass type) {
             super(runtime, type);
             default_value = runtime.getNil();
             default_proc = null;
         }
 
         @JRubyMethod(name="initialize", required=0, optional=1)
         public IRubyObject rubyInitialize(ThreadContext context, IRubyObject args[], Block block) {
             if (block.isGiven()) {
                 if (args.length != 0) {
                     throw context.getRuntime().newArgumentError("wrong number of arguments");
                 }
                 default_proc = block.getProcObject();
                 if (default_proc == null) {
                     default_proc = RubyProc.newProc(context.getRuntime(), block, block.type);
                 }
             } else {
                 if (args.length == 1) {
                     default_value = args[0];
                 } else if (args.length != 0) {
                     throw context.getRuntime().newArgumentError("wrong number of arguments");
                 }
             }
             return context.getRuntime().getNil();
         }
 
         @JRubyMethod(name="default", required=0)
         public IRubyObject getDefault(ThreadContext context) {
             return default_value;
         }
 
         @JRubyMethod(name="default_proc", required=0)
         public IRubyObject getDefaultProc(ThreadContext context) {
             if (default_proc != null) {
                 return default_proc;
             } else {
                 return context.getRuntime().getNil();
             }
         }
 
         private static final IRubyObject[] EMPTY_ARGS = new IRubyObject[]{};
 
         @JRubyMethod(name="value", required=0)
         public IRubyObject getValue(ThreadContext context) {
             final IRubyObject value;
             final Map<Object, IRubyObject> contextVariables;
             contextVariables = getContextVariables(context);
             value = contextVariables.get(this);
             if (value != null) {
                 return value;
             } else if (default_proc != null) {
                 // pre-set for the sake of terminating recursive calls
                 contextVariables.put(this, context.getRuntime().getNil());
 
                 final IRubyObject new_value;
                 new_value = default_proc.call(context, EMPTY_ARGS, null, Block.NULL_BLOCK);
                 contextVariables.put(this, new_value);
                 return new_value;
             } else {
                 return default_value;
             }
         }
 
         @JRubyMethod(name="value=", required=1)
         public IRubyObject setValue(ThreadContext context, IRubyObject value) {
             getContextVariables(context).put(this, value);
             return value;
         }
 
         protected final Map<Object, IRubyObject> getContextVariables(ThreadContext context) {
             return getExecutionContext(context).getContextVariables();
         }
 
         protected abstract ExecutionContext getExecutionContext(ThreadContext context);
     }
 
     @JRubyClass(name="JRuby::ThreadLocal")
     public final static class JRubyThreadLocal extends JRubyExecutionContextLocal {
         public static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass type) {
                 return new JRubyThreadLocal(runtime, type);
             }
         };
 
         public JRubyThreadLocal(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         protected final ExecutionContext getExecutionContext(ThreadContext context) {
             return context.getThread();
         }
     }
 
     @JRubyClass(name="JRuby::FiberLocal")
     public final static class JRubyFiberLocal extends JRubyExecutionContextLocal {
         public static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass type) {
                 return new JRubyFiberLocal(runtime, type);
             }
         };
 
         public JRubyFiberLocal(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         @JRubyMethod(name="with_value", required=1)
         public IRubyObject withValue(ThreadContext context, IRubyObject value, Block block) {
             final Map<Object, IRubyObject> contextVariables;
             contextVariables = getContextVariables(context);
             final IRubyObject old_value;
             old_value = contextVariables.get(this);
             contextVariables.put(this, value);
             try {
                 return block.yieldSpecific(context);
             } finally {
                 contextVariables.put(this, old_value);
             }
         }
 
         protected final ExecutionContext getExecutionContext(ThreadContext context) {
             final ExecutionContext fiber;
             fiber = context.getFiber();
             if (fiber != null) {
                 return fiber;
             } else {
                 /* root fiber */
                 return context.getThread();
             }
         }
     }
 
     @JRubyModule(name="JRubyExtensions")
     public static class JRubyExtensions {
         @JRubyMethod(name = "steal_method", required = 2, module = true)
         public static IRubyObject steal_method(IRubyObject recv, IRubyObject type, IRubyObject methodName) {
             RubyModule to_add = null;
             if(recv instanceof RubyModule) {
                 to_add = (RubyModule)recv;
             } else {
                 to_add = recv.getSingletonClass();
             }
             String name = methodName.toString();
             if(!(type instanceof RubyModule)) {
                 throw recv.getRuntime().newArgumentError("First argument must be a module/class");
             }
 
             DynamicMethod method = ((RubyModule)type).searchMethod(name);
             if(method == null || method.isUndefined()) {
                 throw recv.getRuntime().newArgumentError("No such method " + name + " on " + type);
             }
 
             to_add.addMethod(name, method);
             return recv.getRuntime().getNil();
         }
 
         @JRubyMethod(name = "steal_methods", required = 1, rest = true, module = true)
         public static IRubyObject steal_methods(IRubyObject recv, IRubyObject[] args) {
             IRubyObject type = args[0];
             for(int i=1;i<args.length;i++) {
                 steal_method(recv, type, args[i]);
             }
             return recv.getRuntime().getNil();
         }
     }
     
     public static class JRubyClassExtensions {
         // TODO: Someday, enable.
         @JRubyMethod(name = "subclasses", optional = 1)
         public static IRubyObject subclasses(ThreadContext context, IRubyObject maybeClass, IRubyObject[] args) {
             RubyClass clazz;
             if (maybeClass instanceof RubyClass) {
                 clazz = (RubyClass)maybeClass;
             } else {
                 throw context.getRuntime().newTypeError(maybeClass, context.getRuntime().getClassClass());
             }
 
             boolean recursive = false;
             if (args.length == 1) {
                 if (args[0] instanceof RubyBoolean) {
                     recursive = args[0].isTrue();
                 } else {
                     context.getRuntime().newTypeError(args[0], context.getRuntime().fastGetClass("Boolean"));
                 }
             }
 
             return RubyArray.newArray(context.getRuntime(), clazz.subclasses(recursive)).freeze(context);
         }
 
         @JRubyMethod(name = "become_java!", optional = 1)
         public static IRubyObject become_java_bang(ThreadContext context, IRubyObject maybeClass, IRubyObject[] args) {
             RubyClass clazz;
             if (maybeClass instanceof RubyClass) {
                 clazz = (RubyClass)maybeClass;
             } else {
                 throw context.getRuntime().newTypeError(maybeClass, context.getRuntime().getClassClass());
             }
 
             if (args.length > 0) {
                 clazz.reify(args[0].convertToString().asJavaString());
             } else {
                 clazz.reify();
             }
 
             return JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), clazz.getReifiedClass());
         }
 
         @JRubyMethod
         public static IRubyObject java_class(ThreadContext context, IRubyObject maybeClass) {
             RubyClass clazz;
             if (maybeClass instanceof RubyClass) {
                 clazz = (RubyClass)maybeClass;
             } else {
                 throw context.getRuntime().newTypeError(maybeClass, context.getRuntime().getClassClass());
             }
 
             for (RubyClass current = clazz; current != null; current = current.getSuperClass()) {
                 if (current.getReifiedClass() != null) {
                     clazz = current;
                     break;
                 }
             }
 
             return JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), clazz.getReifiedClass());
         }
 
         @JRubyMethod
         public static IRubyObject add_method_annotation(ThreadContext context, IRubyObject maybeClass, IRubyObject methodName, IRubyObject annoMap) {
             RubyClass clazz = getRubyClass(maybeClass, context);
             String method = methodName.convertToString().asJavaString();
 
             Map<Class,Map<String,Object>> annos = (Map<Class,Map<String,Object>>)annoMap;
 
             for (Map.Entry<Class,Map<String,Object>> entry : annos.entrySet()) {
                 Map<String,Object> value = entry.getValue();
                 if (value == null) value = Collections.EMPTY_MAP;
                 clazz.addMethodAnnotation(method, getAnnoClass(context, entry.getKey()), value);
             }
 
             return context.getRuntime().getNil();
         }
 
         @JRubyMethod
         public static IRubyObject add_parameter_annotations(ThreadContext context, IRubyObject maybeClass, IRubyObject methodName, IRubyObject paramAnnoMaps) {
             RubyClass clazz = getRubyClass(maybeClass, context);
             String method = methodName.convertToString().asJavaString();
             List<Map<Class,Map<String,Object>>> annos = (List<Map<Class,Map<String,Object>>>) paramAnnoMaps;
 
             for (int i = annos.size() - 1; i >= 0; i--) {
                 Map<Class, Map<String, Object>> paramAnnos = annos.get(i);
                 for (Map.Entry<Class,Map<String,Object>> entry : paramAnnos.entrySet()) {
                     Map<String,Object> value = entry.getValue();
                     if (value == null) value = Collections.EMPTY_MAP;
                     clazz.addParameterAnnotation(method, i, getAnnoClass(context, entry.getKey()), value);
                 }
             }
             return context.getRuntime().getNil();
         }
 
         @JRubyMethod
         public static IRubyObject add_class_annotation(ThreadContext context, IRubyObject maybeClass, IRubyObject annoMap) {
             RubyClass clazz = getRubyClass(maybeClass, context);
             Map<Class,Map<String,Object>> annos = (Map<Class,Map<String,Object>>)annoMap;
 
             for (Map.Entry<Class,Map<String,Object>> entry : annos.entrySet()) {
                 Map<String,Object> value = entry.getValue();
                 if (value == null) value = Collections.EMPTY_MAP;
                 clazz.addClassAnnotation(getAnnoClass(context, entry.getKey()), value);
             }
 
             return context.getRuntime().getNil();
         }
 
         @JRubyMethod
         public static IRubyObject add_method_signature(ThreadContext context, IRubyObject maybeClass, IRubyObject methodName, IRubyObject clsList) {
             RubyClass clazz = getRubyClass(maybeClass, context);
             List<Class> types = new ArrayList<Class>();
             for (Iterator i = ((List)clsList).iterator(); i.hasNext();) {
                 types.add(getAnnoClass(context, i.next()));
             }
 
             clazz.addMethodSignature(methodName.convertToString().asJavaString(), types.toArray(new Class[types.size()]));
 
             return context.getRuntime().getNil();
         }
 
         private static Class getAnnoClass(ThreadContext context, Object annoClass) {
             if (annoClass instanceof Class) {
                 return (Class) annoClass;
             } else if (annoClass instanceof IRubyObject) {
                 IRubyObject annoMod = (IRubyObject) annoClass;
                 if (annoMod.respondsTo("java_class")) {
                     return (Class) annoMod.callMethod(context, "java_class").toJava(Object.class);
                 }
             }
             throw context.getRuntime().newArgumentError("must supply java class argument instead of " + annoClass.toString());
         }
 
         private static RubyClass getRubyClass(IRubyObject maybeClass, ThreadContext context) throws RaiseException {
             RubyClass clazz;
             if (maybeClass instanceof RubyClass) {
                 clazz = (RubyClass) maybeClass;
             } else {
                 throw context.getRuntime().newTypeError(maybeClass, context.getRuntime().getClassClass());
             }
             return clazz;
         }
     }
 
     public static class JRubyThreadExtensions {
         private static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
         
         @JRubyMethod(name = "times", module = true)
         public static IRubyObject times(IRubyObject recv, Block unusedBlock) {
             Ruby runtime = recv.getRuntime();
             double system = threadBean.getCurrentThreadCpuTime() / 1000000000.0;
             double user = threadBean.getCurrentThreadUserTime() / 1000000000.0;
             RubyFloat zero = runtime.newFloat(0.0);
             return RubyStruct.newStruct(runtime.getTmsStruct(),
                     new IRubyObject[] { RubyFloat.newFloat(runtime, user), RubyFloat.newFloat(runtime, system), zero, zero },
                     Block.NULL_BLOCK);
         }
     }
     
     public static class JRubyStringExtensions {
         @JRubyMethod(name = "alloc", meta = true)
         public static IRubyObject alloc(ThreadContext context, IRubyObject recv, IRubyObject size) {
             return RubyString.newStringLight(context.getRuntime(), (int)size.convertToInteger().getLongValue());
         }
     }
     
     public static class MethodExtensions {
         @JRubyMethod(name = "args")
         public static IRubyObject methodArgs(IRubyObject recv) {
             Ruby runtime = recv.getRuntime();
             RubyMethod rubyMethod = (RubyMethod)recv;
             
             DynamicMethod method = rubyMethod.method;
             
             if (method instanceof MethodArgs) {
                 RubySymbol req = runtime.newSymbol("req");
                 RubySymbol opt = runtime.newSymbol("opt");
                 RubySymbol rest = runtime.newSymbol("rest");
                 RubySymbol block = runtime.newSymbol("block");
                 MethodArgs interpMethod = (MethodArgs)method;
                 ArgsNode args = interpMethod.getArgsNode();
                 RubyArray argsArray = RubyArray.newArray(runtime);
                 
                 ListNode requiredArgs = args.getPre();
                 for (int i = 0; requiredArgs != null && i < requiredArgs.size(); i++) {
                     argsArray.append(RubyArray.newArray(runtime, req, getNameFrom(runtime, (INameNode) requiredArgs.get(i))));
                 }
                 
                 ListNode optArgs = args.getOptArgs();
                 for (int i = 0; optArgs != null && i < optArgs.size(); i++) {
                     argsArray.append(RubyArray.newArray(runtime, opt, getNameFrom(runtime, (INameNode) optArgs.get(i))));
                 }
                 
                 ListNode requiredArgsPost = args.getPost();
                 for (int i = 0; requiredArgsPost != null && i < requiredArgsPost.size(); i++) {
                     argsArray.append(RubyArray.newArray(runtime, req, getNameFrom(runtime, (INameNode) requiredArgsPost.get(i))));
                 }
 
                 if (args.getRestArg() >= 0) {
                     argsArray.append(RubyArray.newArray(runtime, rest, getNameFrom(runtime, args.getRestArgNode())));
                 }
 
                 if (args.getBlock() != null) {
                     argsArray.append(RubyArray.newArray(runtime, block, getNameFrom(runtime, args.getBlock())));
                 }
                 
                 return argsArray;
             }
             
             return runtime.getNil();
         }
     }
 
     private static IRubyObject getNameFrom(Ruby runtime, INameNode node) {
         return node == null ? runtime.getNil() : RubySymbol.newSymbol(runtime, node.getName());
     }
 }
diff --git a/src/org/jruby/java/addons/KernelJavaAddons.java b/src/org/jruby/java/addons/KernelJavaAddons.java
index 66c6a58ea2..dc6ad05f9d 100644
--- a/src/org/jruby/java/addons/KernelJavaAddons.java
+++ b/src/org/jruby/java/addons/KernelJavaAddons.java
@@ -1,114 +1,114 @@
 package org.jruby.java.addons;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.javasupport.*;
 import org.jruby.RubyKernel;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.java.proxies.ConcreteJavaProxy;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.unsafe.UnsafeFactory;
 
 public class KernelJavaAddons {
     @JRubyMethod(name = "raise", optional = 3, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject rbRaise(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         
         if (args.length == 1 && args[0] instanceof ConcreteJavaProxy) {
             // looks like someone's trying to raise a Java exception. Let them.
-            Object maybeThrowable = ((JavaObject)args[0].dataGetStruct()).getValue();
+            Object maybeThrowable = ((ConcreteJavaProxy)args[0]).getObject();
             
             if (maybeThrowable instanceof Throwable) {
                 // yes, we're cheating here.
                 UnsafeFactory.getUnsafe().throwException((Throwable)maybeThrowable);
                 return recv; // not reached
             } else {
                 throw context.getRuntime().newTypeError("can't raise a non-Throwable Java object");
             }
         } else {
             return RubyKernel.raise(context, recv, args, block);
         }
     }
 
     @JRubyMethod(backtrace = true)
     public static IRubyObject to_java(ThreadContext context, IRubyObject fromObject) {
         if (fromObject instanceof RubyArray) {
             return context.getRuntime().getJavaSupport().getObjectJavaClass().javaArrayFromRubyArray(context, fromObject);
         } else {
             return Java.getInstance(context.getRuntime(), fromObject.toJava(Object.class));
         }
     }
     
     @JRubyMethod(backtrace = true)
     public static IRubyObject to_java(ThreadContext context, IRubyObject fromObject, IRubyObject type) {
         if (type.isNil()) {
             return to_java(context, fromObject);
         }
 
         Ruby runtime = context.getRuntime();
         JavaClass targetType = getTargetType(context, runtime, type);
 
         if (fromObject instanceof RubyArray) {
             return targetType.javaArrayFromRubyArray(context, fromObject);
         } else {
             return Java.getInstance(runtime, fromObject.toJava(targetType.javaClass()));
         }
     }
 
     @JRubyMethod(rest = true)
     public static IRubyObject java_signature(IRubyObject recv, IRubyObject[] args) {
         // empty stub for now
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true)
     public static IRubyObject java_name(IRubyObject recv, IRubyObject[] args) {
         // empty stub for now
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true)
     public static IRubyObject java_implements(IRubyObject recv, IRubyObject[] args) {
         // empty stub for now
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true)
     public static IRubyObject java_annotation(IRubyObject recv, IRubyObject[] args) {
         // empty stub for now
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true)
     public static IRubyObject java_require(IRubyObject recv, IRubyObject[] args) {
         // empty stub for now
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true)
     public static IRubyObject java_package(IRubyObject recv, IRubyObject[] args) {
         // empty stub for now
         return recv.getRuntime().getNil();
     }
 
     private static JavaClass getTargetType(ThreadContext context, Ruby runtime, IRubyObject type) {
         JavaClass targetType;
 
         if (type instanceof RubyString || type instanceof RubySymbol) {
             targetType = runtime.getJavaSupport().getNameClassMap().get(type.asJavaString());
             if (targetType == null) targetType = JavaClass.forNameVerbose(runtime, type.asJavaString());
         } else if (type instanceof RubyModule && type.respondsTo("java_class")) {
             targetType = (JavaClass)RuntimeHelpers.invoke(context, type, "java_class");
         } else {
             throw runtime.newTypeError("unable to convert array to type: " + type);
         }
 
         return targetType;
     }
 }
diff --git a/src/org/jruby/java/proxies/JavaProxy.java b/src/org/jruby/java/proxies/JavaProxy.java
index 15fec79473..d9fe0db96e 100644
--- a/src/org/jruby/java/proxies/JavaProxy.java
+++ b/src/org/jruby/java/proxies/JavaProxy.java
@@ -1,414 +1,418 @@
 package org.jruby.java.proxies;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyHash;
 import org.jruby.RubyHash.Visitor;
 import org.jruby.RubyMethod;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.java.invokers.InstanceFieldGetter;
 import org.jruby.java.invokers.InstanceFieldSetter;
 import org.jruby.java.invokers.InstanceMethodInvoker;
 import org.jruby.java.invokers.MethodInvoker;
 import org.jruby.java.invokers.StaticMethodInvoker;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaClass;
 import org.jruby.javasupport.JavaMethod;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.CodegenUtils;
 
 public class JavaProxy extends RubyObject {
+    private static final boolean DEBUG = false;
     private JavaObject javaObject;
     protected Object object;
     
     public JavaProxy(Ruby runtime, RubyClass klazz) {
         super(runtime, klazz);
     }
 
+    @Override
     public Object dataGetStruct() {
+        // for investigating and eliminating code that causes JavaObject to live
+        if (DEBUG) Thread.dumpStack();
         lazyJavaObject();
         return javaObject;
     }
 
+    @Override
     public void dataWrapStruct(Object object) {
         this.javaObject = (JavaObject)object;
         this.object = javaObject.getValue();
     }
 
     public Object getObject() {
         // FIXME: Added this because marshal_spec seemed to reconstitute objects without calling dataWrapStruct
         // this resulted in object being null after unmarshalling...
         if (object == null) {
             if (javaObject == null) {
                 throw getRuntime().newRuntimeError("Java wrapper with no contents: " + this);
             } else {
                 object = javaObject.getValue();
             }
         }
         return object;
     }
 
     public void setObject(Object object) {
         this.object = object;
     }
 
     private JavaObject getJavaObject() {
         lazyJavaObject();
         return (JavaObject)dataGetStruct();
     }
 
     private void lazyJavaObject() {
         if (javaObject == null) {
             javaObject = JavaObject.wrap(getRuntime(), object);
         }
     }
 
     @Override
     public Class getJavaClass() {
         return object.getClass();
     }
     
     public static RubyClass createJavaProxy(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
         RubyClass javaProxy = runtime.defineClass("JavaProxy", runtime.getObject(), new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                 return new JavaProxy(runtime, klazz);
             }
         });
         
         RubyClass singleton = javaProxy.getSingletonClass();
         
         singleton.addReadWriteAttribute(context, "java_class");
         
         javaProxy.defineAnnotatedMethods(JavaProxy.class);
         javaProxy.includeModule(runtime.fastGetModule("JavaProxyMethods"));
         
         return javaProxy;
     }
     
     @JRubyMethod(frame = true, meta = true)
     public static IRubyObject inherited(ThreadContext context, IRubyObject recv, IRubyObject subclass) {
         IRubyObject subJavaClass = RuntimeHelpers.invoke(context, subclass, "java_class");
         if (subJavaClass.isNil()) {
             subJavaClass = RuntimeHelpers.invoke(context, recv, "java_class");
             RuntimeHelpers.invoke(context, subclass, "java_class=", subJavaClass);
         }
         return RuntimeHelpers.invokeSuper(context, recv, subclass, Block.NULL_BLOCK);
     }
     
     @JRubyMethod(meta = true)
     public static IRubyObject singleton_class(IRubyObject recv) {
         return ((RubyClass)recv).getSingletonClass();
     }
     
     @JRubyMethod(name = "[]", meta = true, rest = true)
     public static IRubyObject op_aref(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject javaClass = RuntimeHelpers.invoke(context, recv, "java_class");
         if (args.length > 0) {
             // construct new array proxy (ArrayJavaProxy)
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             newArgs[0] = javaClass;
             System.arraycopy(args, 0, newArgs, 1, args.length);
             return context.getRuntime().fastGetClass("ArrayJavaProxyCreator").newInstance(context, newArgs, Block.NULL_BLOCK);
         } else {
             return Java.get_proxy_class(javaClass, RuntimeHelpers.invoke(context, javaClass, "array_class"));
         }
     }
 
     @Override
     public IRubyObject initialize_copy(IRubyObject original) {
         super.initialize_copy(original);
         // because we lazily init JavaObject in the data-wrapped slot, explicitly copy over the object
         setObject(((JavaProxy)original).object);
         return this;
     }
 
     private static Class<?> getJavaClass(ThreadContext context, RubyModule module) {
         try {
         IRubyObject jClass = RuntimeHelpers.invoke(context, module, "java_class");
 
 
         return !(jClass instanceof JavaClass) ? null : ((JavaClass) jClass).javaClass();
         } catch (Exception e) { return null; }
     }
     
     /**
      * Create a name/newname map of fields to be exposed as methods.
      */
     private static Map<String, String> getFieldListFromArgs(IRubyObject[] args) {
         final Map<String, String> map = new HashMap<String, String>();
         
         // Get map of all fields we want to define.  
         for (int i = 0; i < args.length; i++) {
             if (args[i] instanceof RubyHash) {
                 ((RubyHash) args[i]).visitAll(new Visitor() {
                     @Override
                     public void visit(IRubyObject key, IRubyObject value) {
                         map.put(key.asString().toString(), value.asString().toString());
                     }
                 });
             } else {
                 String value = args[i].asString().toString();
                 map.put(value, value);
             }
         }
         
         return map;
     }
 
     // Look through all mappings to find a match entry for this field
     private static void installField(ThreadContext context, Map<String, String> fieldMap,
             Field field, RubyModule module, boolean asReader, boolean asWriter) {
         boolean isFinal = Modifier.isFinal(field.getModifiers());
 
         for (Iterator<Map.Entry<String,String>> iter = fieldMap.entrySet().iterator(); iter.hasNext();) {
             Map.Entry<String,String> entry = iter.next();
             String key = entry.getKey();
             if (key.equals(field.getName())) {
                 if (Ruby.isSecurityRestricted() && !Modifier.isPublic(field.getModifiers())) {
                     throw context.getRuntime().newSecurityError("Cannot change accessibility on fields in a restricted mode: field '" + field.getName() + "'");
                 }
                 
                 String asName = entry.getValue();
 
                 if (asReader) module.addMethod(asName, new InstanceFieldGetter(key, module, field));
                 if (asWriter) {
                     if (isFinal) throw context.getRuntime().newSecurityError("Cannot change final field '" + field.getName() + "'");
                     module.addMethod(asName + "=", new InstanceFieldSetter(key, module, field));
                 }
                 
                 iter.remove();
                 break;
             }
         }
     }    
 
     private static void findFields(ThreadContext context, RubyModule topModule,
             IRubyObject args[], boolean asReader, boolean asWriter) {
         Map<String, String> fieldMap = getFieldListFromArgs(args);
         
         for (RubyModule module = topModule; module != null; module = module.getSuperClass()) {
             Class<?> javaClass = getJavaClass(context, module);
             
             // Hit a non-java proxy class (included Modules can be a cause of this...skip)
             if (javaClass == null) continue;
 
             Field[] fields = JavaClass.getDeclaredFields(javaClass);
             for (int j = 0; j < fields.length; j++) {
                 installField(context, fieldMap, fields[j], module, asReader, asWriter);
             }
         }
         
         // We could not find all of them print out first one (we could print them all?)
         if (!fieldMap.isEmpty()) {
             throw JavaClass.undefinedFieldError(context.getRuntime(),
                     topModule.getName(), fieldMap.keySet().iterator().next());
         }
 
     }
     
     @JRubyMethod(meta = true, rest = true)
     public static IRubyObject field_accessor(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         findFields(context, (RubyModule) recv, args, true, true);
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(meta = true, rest = true)
     public static IRubyObject field_reader(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         findFields(context, (RubyModule) recv, args, true, false);
 
         return context.getRuntime().getNil();
     }
     
     @JRubyMethod(meta = true, rest = true)
     public static IRubyObject field_writer(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         findFields(context, (RubyModule) recv, args, false, true);
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "equal?")
     public IRubyObject equal_p(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (other instanceof JavaProxy) {
             boolean equal = getObject() == ((JavaProxy)other).getObject();
             return runtime.newBoolean(equal);
         }
         return runtime.getFalse();
     }
 
     @JRubyMethod(backtrace = true)
     public IRubyObject java_send(ThreadContext context, IRubyObject rubyName) {
         String name = rubyName.asJavaString();
         Ruby runtime = context.getRuntime();
         
         JavaMethod method = new JavaMethod(runtime, getMethod(name));
         return method.invokeDirect(getObject());
     }
 
     @JRubyMethod(backtrace = true)
     public IRubyObject java_send(ThreadContext context, IRubyObject rubyName, IRubyObject argTypes) {
         String name = rubyName.asJavaString();
         RubyArray argTypesAry = argTypes.convertToArray();
         Ruby runtime = context.getRuntime();
 
         if (argTypesAry.size() != 0) {
             Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
             throw JavaMethod.newArgSizeMismatchError(runtime, argTypesClasses);
         }
 
         JavaMethod method = new JavaMethod(runtime, getMethod(name));
         return method.invokeDirect(getObject());
     }
 
     @JRubyMethod(backtrace = true)
     public IRubyObject java_send(ThreadContext context, IRubyObject rubyName, IRubyObject argTypes, IRubyObject arg0) {
         String name = rubyName.asJavaString();
         RubyArray argTypesAry = argTypes.convertToArray();
         Ruby runtime = context.getRuntime();
 
         if (argTypesAry.size() != 1) {
             Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
             throw JavaMethod.newArgSizeMismatchError(runtime, argTypesClasses);
         }
 
         Class argTypeClass = (Class)argTypesAry.eltInternal(0).toJava(Class.class);
 
         JavaMethod method = new JavaMethod(runtime, getMethod(name, argTypeClass));
         return method.invokeDirect(getObject(), arg0.toJava(argTypeClass));
     }
 
     @JRubyMethod(required = 4, rest = true, backtrace = true)
     public IRubyObject java_send(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         
         String name = args[0].asJavaString();
         RubyArray argTypesAry = args[1].convertToArray();
         int argsLen = args.length - 2;
 
         if (argTypesAry.size() != argsLen) {
             Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
             throw JavaMethod.newArgSizeMismatchError(runtime, argTypesClasses);
         }
 
         Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argsLen]);
 
         Object[] argsAry = new Object[argsLen];
         for (int i = 0; i < argsLen; i++) {
             argsAry[i] = args[i + 2].toJava(argTypesClasses[i]);
         }
 
         JavaMethod method = new JavaMethod(runtime, getMethod(name, argTypesClasses));
         return method.invokeDirect(getObject(), argsAry);
     }
 
     @JRubyMethod(backtrace = true)
     public IRubyObject java_method(ThreadContext context, IRubyObject rubyName) {
         String name = rubyName.asJavaString();
 
         return getRubyMethod(name);
     }
 
     @JRubyMethod(backtrace = true)
     public IRubyObject java_method(ThreadContext context, IRubyObject rubyName, IRubyObject argTypes) {
         String name = rubyName.asJavaString();
         RubyArray argTypesAry = argTypes.convertToArray();
         Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
 
         return getRubyMethod(name, argTypesClasses);
     }
 
     @JRubyMethod(frame = true)
     public IRubyObject marshal_dump() {
         if (Serializable.class.isAssignableFrom(object.getClass())) {
             try {
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos);
 
                 oos.writeObject(object);
 
                 return getRuntime().newString(new ByteList(baos.toByteArray()));
             } catch (IOException ioe) {
                 throw getRuntime().newIOErrorFromException(ioe);
             }
         } else {
             throw getRuntime().newTypeError("no marshal_dump is defined for class " + getJavaClass());
         }
     }
 
     @JRubyMethod(frame = true)
     public IRubyObject marshal_load(ThreadContext context, IRubyObject str) {
         try {
             ByteList byteList = str.convertToString().getByteList();
             ByteArrayInputStream bais = new ByteArrayInputStream(byteList.getUnsafeBytes(), byteList.getBegin(), byteList.getRealSize());
             ObjectInputStream ois = new ObjectInputStream(bais);
 
             object = ois.readObject();
 
             return this;
         } catch (IOException ioe) {
             throw context.getRuntime().newIOErrorFromException(ioe);
         } catch (ClassNotFoundException cnfe) {
             throw context.getRuntime().newTypeError("Class not found unmarshaling Java type: " + cnfe.getLocalizedMessage());
         }
     }
 
     private Method getMethod(String name, Class... argTypes) {
-        Class jclass = getJavaObject().getJavaClass();
         try {
-            return jclass.getMethod(name, argTypes);
+            return getObject().getClass().getMethod(name, argTypes);
         } catch (NoSuchMethodException nsme) {
-            throw JavaMethod.newMethodNotFoundError(getRuntime(), jclass, name + CodegenUtils.prettyParams(argTypes), name);
+            throw JavaMethod.newMethodNotFoundError(getRuntime(), getObject().getClass(), name + CodegenUtils.prettyParams(argTypes), name);
         }
     }
 
     private MethodInvoker getMethodInvoker(Method method) {
         if (Modifier.isStatic(method.getModifiers())) {
             return new StaticMethodInvoker(metaClass.getMetaClass(), method);
         } else {
             return new InstanceMethodInvoker(metaClass, method);
         }
     }
 
     private RubyMethod getRubyMethod(String name, Class... argTypes) {
         Method jmethod = getMethod(name, argTypes);
         if (Modifier.isStatic(jmethod.getModifiers())) {
             return RubyMethod.newMethod(metaClass.getSingletonClass(), CodegenUtils.prettyParams(argTypes), metaClass.getSingletonClass(), name, getMethodInvoker(jmethod), getMetaClass());
         } else {
             return RubyMethod.newMethod(metaClass, CodegenUtils.prettyParams(argTypes), metaClass, name, getMethodInvoker(jmethod), this);
         }
     }
 
     @Override
     public Object toJava(Class type) {
         if (Java.OBJECT_PROXY_CACHE) getRuntime().getJavaSupport().getObjectProxyCache().put(getObject(), this);
         return getObject();
     }
     
     public Object unwrap() {
         return getObject();
     }
 }
diff --git a/src/org/jruby/javasupport/JavaArray.java b/src/org/jruby/javasupport/JavaArray.java
index 0bffff5b0c..b25591701f 100644
--- a/src/org/jruby/javasupport/JavaArray.java
+++ b/src/org/jruby/javasupport/JavaArray.java
@@ -1,176 +1,181 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 package org.jruby.javasupport;
 
 import java.lang.reflect.Array;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.anno.JRubyClass;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 @JRubyClass(name="Java::JavaArray", parent="Java::JavaObject")
 public class JavaArray extends JavaObject {
     private JavaUtil.JavaConverter javaConverter;
     
     public JavaArray(Ruby runtime, Object array) {
         super(runtime, runtime.getJavaSupport().getJavaArrayClass(), array);
         assert array.getClass().isArray();
         javaConverter = JavaUtil.getJavaConverter(array.getClass().getComponentType());
     }
 
     public static RubyClass createJavaArrayClass(Ruby runtime, RubyModule javaModule) {
         // FIXME: NOT_ALLOCATABLE_ALLOCATOR is probably not right here, since we might
         // eventually want JavaArray to be marshallable. JRUBY-414
         return javaModule.defineClassUnder("JavaArray", javaModule.fastGetClass("JavaObject"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
     }
     
     public Class getComponentType() {
         return getValue().getClass().getComponentType();
     }
 
     public RubyFixnum length() {
         return getRuntime().newFixnum(getLength());
     }
 
     private int getLength() {
         return Array.getLength(getValue());
     }
 
     public boolean equals(Object other) {
         return other instanceof JavaArray &&
             this.getValue() == ((JavaArray)other).getValue();
     }
 
     @Deprecated
     public IRubyObject aref(IRubyObject index) {
         if (! (index instanceof RubyInteger)) {
             throw getRuntime().newTypeError(index, getRuntime().getInteger());
         }
         int intIndex = (int) ((RubyInteger) index).getLongValue();
         if (intIndex < 0 || intIndex >= getLength()) {
             throw getRuntime().newArgumentError(
                                     "index out of bounds for java array (" + intIndex +
                                     " for length " + getLength() + ")");
         }
         Object result = Array.get(getValue(), intIndex);
         if (result == null) {
             return getRuntime().getNil();
         }
         return JavaObject.wrap(getRuntime(), result);
     }
 
     public IRubyObject arefDirect(int intIndex) {
-        if (intIndex < 0 || intIndex >= getLength()) {
-            throw getRuntime().newArgumentError(
+        return arefDirect(getRuntime(), javaConverter, getValue(), intIndex);
+    }
+
+    public static IRubyObject arefDirect(Ruby runtime, JavaUtil.JavaConverter converter, Object array, int intIndex) {
+        int length = Array.getLength(array);
+        if (intIndex < 0 || intIndex >= length) {
+            throw runtime.newArgumentError(
                                     "index out of bounds for java array (" + intIndex +
-                                    " for length " + getLength() + ")");
+                                    " for length " + length + ")");
         }
-        return JavaUtil.convertJavaArrayElementToRuby(getRuntime(), javaConverter, getValue(), intIndex);
+        return JavaUtil.convertJavaArrayElementToRuby(runtime, converter, array, intIndex);
     }
 
     @Deprecated
     public IRubyObject at(int index) {
         Object result = Array.get(getValue(), index);
         if (result == null) {
             return getRuntime().getNil();
         }
         return JavaObject.wrap(getRuntime(), result);
     }
 
     public IRubyObject aset(IRubyObject index, IRubyObject value) {
          if (! (index instanceof RubyInteger)) {
             throw getRuntime().newTypeError(index, getRuntime().getInteger());
         }
         int intIndex = (int) ((RubyInteger) index).getLongValue();
         if (! (value instanceof JavaObject)) {
             throw getRuntime().newTypeError("not a java object:" + value);
         }
         Object javaObject = ((JavaObject) value).getValue();
         setWithExceptionHandling(intIndex, javaObject);
         return value;
     }
     
     public void setWithExceptionHandling(int intIndex, Object javaObject) {
         try {
             Array.set(getValue(), intIndex, javaObject);
         } catch (IndexOutOfBoundsException e) {
             throw getRuntime().newArgumentError(
                                     "index out of bounds for java array (" + intIndex +
                                     " for length " + getLength() + ")");
         } catch (ArrayStoreException e) {
             throw getRuntime().newArgumentError(
                                     "wrong element type " + javaObject.getClass() + "(array contains " +
                                     getValue().getClass().getComponentType().getName() + ")");
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newArgumentError(
                                     "wrong element type " + javaObject.getClass() + "(array contains " +
                                     getValue().getClass().getComponentType().getName() + ")");
         }
     }
 
     public IRubyObject afill(IRubyObject beginIndex, IRubyObject endIndex, IRubyObject value) {
         if (! (beginIndex instanceof RubyInteger)) {
             throw getRuntime().newTypeError(beginIndex, getRuntime().getInteger());
         }
         int intIndex = (int) ((RubyInteger) beginIndex).getLongValue();
         if (! (endIndex instanceof RubyInteger)) {
             throw getRuntime().newTypeError(endIndex, getRuntime().getInteger());
         }
         int intEndIndex = (int) ((RubyInteger) endIndex).getLongValue();
         if (! (value instanceof JavaObject)) {
             throw getRuntime().newTypeError("not a java object:" + value);
         }
         Object javaObject = ((JavaObject) value).getValue();
         fillWithExceptionHandling(intIndex, intEndIndex, javaObject);
         return value;
     }
     
     public void fillWithExceptionHandling(int intIndex, int intEndIndex, Object javaObject) {
         try {
           for ( ; intIndex < intEndIndex; intIndex++) {
             Array.set(getValue(), intIndex, javaObject);
           }
         } catch (IndexOutOfBoundsException e) {
             throw getRuntime().newArgumentError(
                                     "index out of bounds for java array (" + intIndex +
                                     " for length " + getLength() + ")");
         } catch (ArrayStoreException e) {
             throw getRuntime().newArgumentError(
                                     "wrong element type " + javaObject.getClass() + "(array is " +
                                     getValue().getClass() + ")");
         }
     }
 }
diff --git a/src/org/jruby/javasupport/JavaArrayUtilities.java b/src/org/jruby/javasupport/JavaArrayUtilities.java
index 8ecff5028d..fb2437f364 100644
--- a/src/org/jruby/javasupport/JavaArrayUtilities.java
+++ b/src/org/jruby/javasupport/JavaArrayUtilities.java
@@ -1,73 +1,88 @@
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
 package org.jruby.javasupport;
 
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.anno.JRubyModule;
+import org.jruby.java.proxies.JavaProxy;
 import org.jruby.runtime.Visibility;
 
 /**
  * @author Bill Dortch
  *
  */
 @JRubyModule(name="JavaArrayUtilities")
 public class JavaArrayUtilities {
 
     public static RubyModule createJavaArrayUtilitiesModule(Ruby runtime) {
         RubyModule javaArrayUtils = runtime.defineModule("JavaArrayUtilities");
         javaArrayUtils.defineAnnotatedMethods(JavaArrayUtilities.class);
         return javaArrayUtils;
     }
     
     @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject bytes_to_ruby_string(IRubyObject recv, IRubyObject wrappedObject) {
         Ruby runtime = recv.getRuntime();
-        IRubyObject byteArray = (JavaObject)wrappedObject.dataGetStruct();
-        if (!(byteArray instanceof JavaArray &&
-                ((JavaArray)byteArray).getValue() instanceof byte[])) {
+        byte[] bytes = null;
+        
+        if (wrappedObject instanceof JavaProxy) {
+            Object wrapped = ((JavaProxy)wrappedObject).getObject();
+            if (wrapped instanceof byte[]) {
+                bytes = (byte[])wrapped;
+            }
+        } else {
+            IRubyObject byteArray = (JavaObject)wrappedObject.dataGetStruct();
+            if (byteArray instanceof JavaArray &&
+                    ((JavaArray)byteArray).getValue() instanceof byte[]) {
+                bytes = (byte[])((JavaArray)byteArray).getValue();
+            }
+        }
+
+        if (bytes == null) {
             throw runtime.newTypeError("wrong argument type " + wrappedObject.getMetaClass() +
                     " (expected byte[])");
         }
-        return runtime.newString(new ByteList((byte[])((JavaArray)byteArray).getValue(), true));
+
+        return runtime.newString(new ByteList(bytes, true));
     }
     
     @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject ruby_string_to_bytes(IRubyObject recv, IRubyObject string) {
         Ruby runtime = recv.getRuntime();
         if (!(string instanceof RubyString)) {
             throw runtime.newTypeError(string, runtime.getString());
         }
         return JavaUtil.convertJavaToUsableRubyObject(runtime, ((RubyString)string).getBytes());
     }
 
 }
diff --git a/src/org/jruby/javasupport/JavaMethod.java b/src/org/jruby/javasupport/JavaMethod.java
index 5af942bae5..83c5dbfc7e 100644
--- a/src/org/jruby/javasupport/JavaMethod.java
+++ b/src/org/jruby/javasupport/JavaMethod.java
@@ -1,740 +1,740 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
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
 package org.jruby.javasupport;
 
 import java.lang.annotation.Annotation;
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.Type;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.compiler.util.HandleFactory;
 import org.jruby.compiler.util.HandleFactory.Handle;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.proxy.InternalJavaProxy;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CodegenUtils;
 
 @JRubyClass(name="Java::JavaMethod")
 public class JavaMethod extends JavaCallable {
     private final static boolean USE_HANDLES = RubyInstanceConfig.USE_GENERATED_HANDLES;
     private final static boolean HANDLE_DEBUG = false;
     private final Method method;
     private final Class boxedReturnType;
     private final boolean isFinal;
     private final Handle handle;
     private final JavaUtil.JavaConverter returnConverter;
 
     public Object getValue() {
         return method;
     }
 
     public static RubyClass createJavaMethodClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result = 
             javaModule.defineClassUnder("JavaMethod", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
         JavaAccessibleObject.registerRubyMethods(runtime, result);
         JavaCallable.registerRubyMethods(runtime, result);
         
         result.defineAnnotatedMethods(JavaMethod.class);
 
         return result;
     }
 
     public JavaMethod(Ruby runtime, Method method) {
         super(runtime, runtime.getJavaSupport().getJavaMethodClass(), method.getParameterTypes());
         this.method = method;
         this.isFinal = Modifier.isFinal(method.getModifiers());
         if (method.getReturnType().isPrimitive() && method.getReturnType() != void.class) {
             this.boxedReturnType = CodegenUtils.getBoxType(method.getReturnType());
         } else {
             this.boxedReturnType = method.getReturnType();
         }
 
         boolean methodIsPublic = Modifier.isPublic(method.getModifiers());
         boolean classIsPublic = Modifier.isPublic(method.getDeclaringClass().getModifiers());
 
         // try to find a "totally" public version of the method using superclasses and interfaces
         if (methodIsPublic && !classIsPublic) {
             if (HANDLE_DEBUG) System.out.println("Method " + method + " is not on a public class, searching for a better one");
             Method newMethod = method;
             Class newClass = method.getDeclaringClass();
 
             OUTER: while (newClass != null) {
                 // try class
                 try {
                     if (HANDLE_DEBUG) System.out.println("Trying to find " + method + " on " + newClass);
                     newMethod = newClass.getMethod(method.getName(), method.getParameterTypes());
 
                     // got it; break if this class is public
                     if (Modifier.isPublic(newMethod.getDeclaringClass().getModifiers())) {
                         break;
                     }
                 } catch (NoSuchMethodException nsme) {
                 }
 
                 // try interfaces
                 for (Class ifc : newClass.getInterfaces()) {
                     try {
                         if (HANDLE_DEBUG) System.out.println("Trying to find " + method + " on " + ifc);
                         newMethod = ifc.getMethod(method.getName(), method.getParameterTypes());
                         break OUTER;
                     } catch (NoSuchMethodException nsme) {
                     }
                 }
 
                 // go to superclass
                 newClass = newClass.getSuperclass();
                 newMethod = null;
             }
             
             if (newMethod != null) {
                 if (HANDLE_DEBUG) System.out.println("Found a better method target: " + newMethod);
                 method = newMethod;
                 methodIsPublic = Modifier.isPublic(method.getModifiers());
                 classIsPublic = Modifier.isPublic(method.getDeclaringClass().getModifiers());
             }
         }
 
         // prepare a faster handle if handles are enabled and the method and class are public
         Handle tmpHandle = null;
         try {
             if (USE_HANDLES &&
                     // must be a public method
                     methodIsPublic &&
                     // must be a public class
                     classIsPublic &&
                     // must have been loaded from our known classloader hierarchy
                     runtime.getJRubyClassLoader().loadClass(method.getDeclaringClass().getCanonicalName()) == method.getDeclaringClass()) {
                 tmpHandle = HandleFactory.createHandle(runtime.getJRubyClassLoader(), method);
             } else {
                 tmpHandle = null;
             }
         } catch (ClassNotFoundException cnfe) {
             tmpHandle = null;
         }
 
         if (tmpHandle == null) {
             if (HANDLE_DEBUG) System.out.println("did not use handle for " + method);
         }
         
         handle = tmpHandle;
 
         // Special classes like Collections.EMPTY_LIST are inner classes that are private but 
         // implement public interfaces.  Their methods are all public methods for the public 
         // interface.  Let these public methods execute via setAccessible(true). 
         if (methodIsPublic &&
             !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
             accessibleObject().setAccessible(true);
         }
         
         returnConverter = JavaUtil.getJavaConverter(method.getReturnType());
     }
 
     public static JavaMethod create(Ruby runtime, Method method) {
         return new JavaMethod(runtime, method);
     }
 
     public static JavaMethod create(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         try {
             Method method = javaClass.getMethod(methodName, argumentTypes);
             return create(runtime, method);
         } catch (NoSuchMethodException e) {
             throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                     methodName);
         }
     }
 
     public static JavaMethod createDeclared(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         try {
             return create(runtime, javaClass.getDeclaredMethod(methodName, argumentTypes));
         } catch (NoSuchMethodException e) {
             throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                     methodName);
         }
     }
 
     public static JavaMethod getMatchingDeclaredMethod(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
         // include superclass methods.  also, the getDeclared calls may throw SecurityException if
         // we're running under a restrictive security policy.
         try {
             return create(runtime, javaClass.getDeclaredMethod(methodName, argumentTypes));
         } catch (NoSuchMethodException e) {
             // search through all declared methods to find a closest match
             MethodSearch: for (Method method : javaClass.getDeclaredMethods()) {
                 if (method.getName().equals(methodName)) {
                     Class<?>[] targetTypes = method.getParameterTypes();
                 
                     // for zero args case we can stop searching
                     if (targetTypes.length == 0 && argumentTypes.length == 0) {
                         return create(runtime, method);
                     }
                     
                     TypeScan: for (int i = 0; i < argumentTypes.length; i++) {
                         if (i >= targetTypes.length) continue MethodSearch;
 
                         if (targetTypes[i].isAssignableFrom(argumentTypes[i])) {
                             continue TypeScan;
                         } else {
                             continue MethodSearch;
                         }
                     }
 
                     // if we get here, we found a matching method, use it
                     // TODO: choose narrowest method by continuing to search
                     return create(runtime, method);
                 }
             }
         }
         // no matching method found
         return null;
     }
     
     @Override
     public boolean equals(Object other) {
         return other instanceof JavaMethod &&
             this.method == ((JavaMethod)other).method;
     }
     
     @Override
     public int hashCode() {
         return method.hashCode();
     }
 
     @JRubyMethod
     @Override
     public RubyString name() {
         return getRuntime().newString(method.getName());
     }
 
     public int getArity() {
         return parameterTypes.length;
     }
 
     @JRubyMethod(name = "public?")
     @Override
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(method.getModifiers()));
     }
 
     @JRubyMethod(name = "final?")
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(method.getModifiers()));
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject invoke(IRubyObject[] args) {
         checkArity(args.length - 1);
         Object[] arguments = new Object[args.length - 1];
         convertArguments(args, arguments, 1);
 
         IRubyObject invokee = args[0];
         if(invokee.isNil()) {
             return invokeWithExceptionHandling(method, null, arguments);
         }
 
         Object javaInvokee = null;
 
         if (!isStatic()) {
-            javaInvokee = JavaUtil.unwrapJavaObject(getRuntime(), invokee, "invokee not a java object").getValue();
+            javaInvokee = JavaUtil.unwrapJavaValue(getRuntime(), invokee, "invokee not a java object");
 
             if (! method.getDeclaringClass().isInstance(javaInvokee)) {
                 throw getRuntime().newTypeError("invokee not instance of method's class (" +
                                                   "got" + javaInvokee.getClass().getName() + " wanted " +
                                                   method.getDeclaringClass().getName() + ")");
             }
 
             //
             // this test really means, that this is a ruby-defined subclass of a java class
             //
             if (javaInvokee instanceof InternalJavaProxy &&
                     // don't bother to check if final method, it won't
                     // be there (not generated, can't be!)
                     !Modifier.isFinal(method.getModifiers())) {
                 JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee)
                         .___getProxyClass();
                 JavaProxyMethod jpm;
                 if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null &&
                         jpm.hasSuperImplementation()) {
                     return invokeWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arguments);
                 }
             }
         }
         return invokeWithExceptionHandling(method, javaInvokee, arguments);
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject invoke_static(IRubyObject[] args) {
         checkArity(args.length);
         Object[] arguments = new Object[args.length];
         System.arraycopy(args, 0, arguments, 0, arguments.length);
         convertArguments(args, arguments, 0);
         return invokeWithExceptionHandling(method, null, arguments);
     }
 
     @JRubyMethod
     public IRubyObject return_type() {
         Class<?> klass = method.getReturnType();
         
         if (klass.equals(void.class)) {
             return getRuntime().getNil();
         }
         return JavaClass.get(getRuntime(), klass);
     }
 
     @JRubyMethod
     public IRubyObject type_parameters() {
         return Java.getInstance(getRuntime(), method.getTypeParameters());
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object[] args) {
         checkArity(args.length);
         checkInstanceof(javaInvokee);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, args);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, args);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(0);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object arg0) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(1);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, arg0);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, arg0);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object arg0, Object arg1) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(2);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, arg0, arg1);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object arg0, Object arg1, Object arg2) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(3);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, arg0, arg1, arg2);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1, arg2);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object arg0, Object arg1, Object arg2, Object arg3) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(4);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, arg0, arg1, arg2, arg3);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1, arg2, arg3);
     }
 
     public IRubyObject invokeStaticDirect(Object[] args) {
         checkArity(args.length);
         return invokeDirectWithExceptionHandling(method, null, args);
     }
 
     public IRubyObject invokeStaticDirect() {
         checkArity(0);
         return invokeDirectWithExceptionHandling(method, null);
     }
 
     public IRubyObject invokeStaticDirect(Object arg0) {
         checkArity(1);
         return invokeDirectWithExceptionHandling(method, null, arg0);
     }
 
     public IRubyObject invokeStaticDirect(Object arg0, Object arg1) {
         checkArity(2);
         return invokeDirectWithExceptionHandling(method, null, arg0, arg1);
     }
 
     public IRubyObject invokeStaticDirect(Object arg0, Object arg1, Object arg2) {
         checkArity(3);
         return invokeDirectWithExceptionHandling(method, null, arg0, arg1, arg2);
     }
 
     public IRubyObject invokeStaticDirect(Object arg0, Object arg1, Object arg2, Object arg3) {
         checkArity(4);
         return invokeDirectWithExceptionHandling(method, null, arg0, arg1, arg2, arg3);
     }
 
     private void checkInstanceof(Object javaInvokee) throws RaiseException {
         if (!method.getDeclaringClass().isInstance(javaInvokee)) {
             throw getRuntime().newTypeError("invokee not instance of method's class (" + "got" + javaInvokee.getClass().getName() + " wanted " + method.getDeclaringClass().getName() + ")");
         }
     }
 
     private IRubyObject invokeWithExceptionHandling(Method method, Object javaInvokee, Object[] arguments) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee, arguments)
                     : method.invoke(javaInvokee, arguments);
             return returnConverter.convert(getRuntime(), result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arguments);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectSuperWithExceptionHandling(Method method, Object javaInvokee, Object... arguments) {
         // super calls from proxies must use reflected method
         // FIXME: possible to make handles do the superclass call?
         try {
             Object result = method.invoke(javaInvokee, arguments);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arguments);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object[] arguments) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee, arguments)
                     : method.invoke(javaInvokee, arguments);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arguments);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee)
                     : method.invoke(javaInvokee);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object arg0) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee, arg0)
                     : method.invoke(javaInvokee, arg0);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arg0);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object arg0, Object arg1) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee, arg0, arg1)
                     : method.invoke(javaInvokee, arg0, arg1);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arg0, arg1);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object arg0, Object arg1, Object arg2) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee, arg0, arg1, arg2)
                     : method.invoke(javaInvokee, arg0, arg1, arg2);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arg0, arg1, arg2);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object arg0, Object arg1, Object arg2, Object arg3) {
         try {
             Object result = handle != null
                     ? handle.invoke(javaInvokee, arg0, arg1, arg2, arg3)
                     : method.invoke(javaInvokee, arg0, arg1, arg2, arg3);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arg0, arg1, arg2, arg3);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject convertReturn(Object result) {
         if (result != null && result.getClass() != boxedReturnType) {
             // actual type does not exactly match method return type, re-get converter
             // FIXME: when the only autoconversions are primitives, this won't be needed
             return JavaUtil.convertJavaToUsableRubyObject(getRuntime(), result);
         }
         return JavaUtil.convertJavaToUsableRubyObjectWithConverter(getRuntime(), result, returnConverter);
     }
 
     private IRubyObject handleIllegalAccessEx(Method method, IllegalAccessException iae) throws RaiseException {
         throw getRuntime().newTypeError("illegal access on '" + method.getName() + "': " + iae.getMessage());
     }
 
     private IRubyObject handlelIllegalArgumentEx(Method method, IllegalArgumentException iae, Object... arguments) throws RaiseException {
         throw getRuntime().newTypeError(
                 "for method " +
                 method.getDeclaringClass().getSimpleName() +
                 "." +
                 method.getName() +
                 " expected " +
                 argument_types().inspect() +
                 "; got: " +
                 dumpArgTypes(arguments) +
                 "; error: " +
                 iae.getMessage());
     }
 
     private void convertArguments(IRubyObject[] argsIn, Object[] argsOut, int from) {
         Class<?>[] types = parameterTypes;
         for (int i = argsOut.length; --i >= 0; ) {
             argsOut[i] = argsIn[i+from].toJava(types[i]);
         }
     }
 
     public Class<?>[] getParameterTypes() {
         return parameterTypes;
     }
 
     public Class<?>[] getExceptionTypes() {
         return method.getExceptionTypes();
     }
 
     public Type[] getGenericParameterTypes() {
         return method.getGenericParameterTypes();
     }
 
     public Type[] getGenericExceptionTypes() {
         return method.getGenericExceptionTypes();
     }
     
     public Annotation[][] getParameterAnnotations() {
         return method.getParameterAnnotations();
     }
 
     public boolean isVarArgs() {
         return method.isVarArgs();
     }
 
     protected String nameOnInspection() {
         return "#<" + getType().toString() + "/" + method.getName() + "(";
     }
 
     public RubyBoolean static_p() {
         return getRuntime().newBoolean(isStatic());
     }
     
     public RubyBoolean bridge_p() {
         return getRuntime().newBoolean(method.isBridge());
     }
 
     private boolean isStatic() {
         return Modifier.isStatic(method.getModifiers());
     }
 
     public int getModifiers() {
         return method.getModifiers();
     }
 
     public String toGenericString() {
         return method.toGenericString();
     }
 
     protected AccessibleObject accessibleObject() {
         return method;
     }
 
     private boolean mightBeProxy(Object javaInvokee) {
         // this test really means, that this is a ruby-defined subclass of a java class
         return javaInvokee instanceof InternalJavaProxy && !isFinal;
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object... args) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, args);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, args);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object arg0) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arg0);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, arg0);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object arg0, Object arg1) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arg0, arg1);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object arg0, Object arg1, Object arg2) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arg0, arg1, arg2);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1, arg2);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object arg0, Object arg1, Object arg2, Object arg3) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arg0, arg1, arg2, arg3);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1, arg2, arg3);
         }
     }
 
     public static RaiseException newMethodNotFoundError(Ruby runtime, Class target, String prettyName, String simpleName) {
         return runtime.newNameError("java method not found: " + target.getName() + "." + prettyName, simpleName);
     }
 
     public static RaiseException newArgSizeMismatchError(Ruby runtime, Class ... argTypes) {
         return runtime.newArgumentError("argument count mismatch for method signature " + CodegenUtils.prettyParams(argTypes));
     }
 }
diff --git a/src/org/jruby/javasupport/JavaObject.java b/src/org/jruby/javasupport/JavaObject.java
index 66cb82d669..22a041d2c7 100644
--- a/src/org/jruby/javasupport/JavaObject.java
+++ b/src/org/jruby/javasupport/JavaObject.java
@@ -1,325 +1,343 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
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
 package org.jruby.javasupport;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.java.proxies.JavaProxy;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Java::JavaObject")
 public class JavaObject extends RubyObject {
 
     private static Object NULL_LOCK = new Object();
     private final RubyClass.VariableAccessor objectAccessor;
 
     protected JavaObject(Ruby runtime, RubyClass rubyClass, Object value) {
         super(runtime, rubyClass);
         objectAccessor = rubyClass.getVariableAccessorForWrite("__wrap_struct__");
         dataWrapStruct(value);
     }
 
     @Override
     public Object dataGetStruct() {
         return objectAccessor.get(this);
     }
 
     @Override
     public void dataWrapStruct(Object object) {
         objectAccessor.set(this, object);
     }
 
     protected JavaObject(Ruby runtime, Object value) {
         this(runtime, runtime.getJavaSupport().getJavaObjectClass(), value);
     }
 
     public static JavaObject wrap(Ruby runtime, Object value) {
         if (value != null) {
             if (value instanceof Class) {
                 return JavaClass.get(runtime, (Class<?>) value);
             } else if (value.getClass().isArray()) {
                 return new JavaArray(runtime, value);
             }
         }
         return new JavaObject(runtime, value);
     }
 
     @JRubyMethod(meta = true)
     public static IRubyObject wrap(ThreadContext context, IRubyObject self, IRubyObject object) {
         Ruby runtime = context.getRuntime();
         Object obj = getWrappedObject(object, NEVER);
 
         if (obj == NEVER) return runtime.getNil();
 
         return wrap(runtime, obj);
     }
 
     @Override
     public Class<?> getJavaClass() {
         Object dataStruct = dataGetStruct();
         return dataStruct != null ? dataStruct.getClass() : Void.TYPE;
     }
 
     public Object getValue() {
         return dataGetStruct();
     }
 
     public static RubyClass createJavaObjectClass(Ruby runtime, RubyModule javaModule) {
         // FIXME: Ideally JavaObject instances should be marshallable, which means that
         // the JavaObject metaclass should have an appropriate allocator. JRUBY-414
         RubyClass result = javaModule.defineClassUnder("JavaObject", runtime.getObject(), JAVA_OBJECT_ALLOCATOR);
 
         registerRubyMethods(runtime, result);
 
         result.getMetaClass().undefineMethod("new");
         result.getMetaClass().undefineMethod("allocate");
 
         return result;
     }
 
     protected static void registerRubyMethods(Ruby runtime, RubyClass result) {
         result.defineAnnotatedMethods(JavaObject.class);
     }
 
     @Override
     public boolean equals(Object other) {
         Ruby runtime = getRuntime();
         Object myValue = getValue();
         Object otherValue = other;
         if (other instanceof IRubyObject) {
             otherValue = getWrappedObject((IRubyObject)other, NEVER);
         }
 
         if (otherValue == NEVER) {
             // not a wrapped object
             return false;
         }
         return myValue == otherValue;
     }
 
     @Override
     public int hashCode() {
         Object dataStruct = dataGetStruct();
         if (dataStruct != null) {
             return dataStruct.hashCode();
         }
         return 0;
     }
 
     @JRubyMethod
     @Override
     public RubyFixnum hash() {
         return getRuntime().newFixnum(hashCode());
     }
 
     @JRubyMethod
     @Override
     public IRubyObject to_s() {
-        Object dataStruct = dataGetStruct();
+        return to_s(getRuntime(), dataGetStruct());
+    }
+
+    public static IRubyObject to_s(Ruby runtime, Object dataStruct) {
         if (dataStruct != null) {
             String stringValue = dataStruct.toString();
             if (stringValue != null) {
-                return RubyString.newUnicodeString(getRuntime(), dataStruct.toString());
+                return RubyString.newUnicodeString(runtime, dataStruct.toString());
             }
 
-            return getRuntime().getNil();
+            return runtime.getNil();
         }
-        return RubyString.newEmptyString(getRuntime());
+        return RubyString.newEmptyString(runtime);
     }
 
     @JRubyMethod(name = {"==", "eql?"}, required = 1)
     public IRubyObject op_equal(IRubyObject other) {
-        Ruby runtime = getRuntime();
         Object myValue = getValue();
+        return opEqualShared(myValue, other);
+    }
+
+    public static IRubyObject op_equal(JavaProxy self, IRubyObject other) {
+        Object myValue = self.getObject();
+        return opEqualShared(myValue, other);
+    }
+
+    private static IRubyObject opEqualShared(Object myValue, IRubyObject other) {
+        Ruby runtime = other.getRuntime();
         Object otherValue = getWrappedObject(other, NEVER);
-        
+
         if (other == NEVER) {
             // not a wrapped object
             return runtime.getFalse();
         }
 
         if (myValue == null && otherValue == null) {
             return runtime.getTrue();
         }
 
         return runtime.newBoolean(myValue.equals(otherValue));
     }
 
     @JRubyMethod(name = "equal?", required = 1)
     public IRubyObject same(IRubyObject other) {
         Ruby runtime = getRuntime();
         Object myValue = getValue();
         Object otherValue = getWrappedObject(other, NEVER);
 
         if (other == NEVER) {
             // not a wrapped object
             return runtime.getFalse();
         }
 
         if (myValue == null && otherValue == null) {
             return getRuntime().getTrue();
         }
 
         boolean isSame = getValue() == ((JavaObject) other).getValue();
         return isSame ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     private static Object getWrappedObject(IRubyObject other, Object def) {
         if (other instanceof JavaObject) {
             return ((JavaObject)other).getValue();
         } else if (other instanceof JavaProxy) {
             return ((JavaProxy)other).getObject();
         } else {
             return def;
         }
     }
 
     @JRubyMethod
     public RubyString java_type() {
         return getRuntime().newString(getJavaClass().getName());
     }
 
     @JRubyMethod
     public IRubyObject java_class() {
         return JavaClass.get(getRuntime(), getJavaClass());
     }
 
     @JRubyMethod
     public RubyFixnum length() {
         throw getRuntime().newTypeError("not a java array");
     }
 
     @JRubyMethod(name = "[]", required = 1)
     public IRubyObject aref(IRubyObject index) {
         throw getRuntime().newTypeError("not a java array");
     }
 
     @JRubyMethod(name = "[]=", required = 2)
     public IRubyObject aset(IRubyObject index, IRubyObject someValue) {
         throw getRuntime().newTypeError("not a java array");
     }
 
     @JRubyMethod(name = "fill", required = 3)
     public IRubyObject afill(IRubyObject beginIndex, IRubyObject endIndex, IRubyObject someValue) {
         throw getRuntime().newTypeError("not a java array");
     }
 
     @JRubyMethod(name = "java_proxy?")
     public IRubyObject is_java_proxy() {
         return getRuntime().getTrue();
     }
 
     @JRubyMethod(name = "synchronized")
     public IRubyObject ruby_synchronized(ThreadContext context, Block block) {
         Object lock = getValue();
         synchronized (lock != null ? lock : NULL_LOCK) {
             return block.yield(context, null);
         }
     }
     
+    public static IRubyObject ruby_synchronized(ThreadContext context, Object lock, Block block) {
+        synchronized (lock != null ? lock : NULL_LOCK) {
+            return block.yield(context, null);
+        }
+    }
+    
     @JRubyMethod(frame = true)
     public IRubyObject marshal_dump() {
         if (Serializable.class.isAssignableFrom(getJavaClass())) {
             try {
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos);
 
                 oos.writeObject(getValue());
 
                 return getRuntime().newString(new ByteList(baos.toByteArray()));
             } catch (IOException ioe) {
                 throw getRuntime().newIOErrorFromException(ioe);
             }
         } else {
             throw getRuntime().newTypeError("no marshal_dump is defined for class " + getJavaClass());
         }
     }
 
     @JRubyMethod(frame = true)
     public IRubyObject marshal_load(ThreadContext context, IRubyObject str) {
         try {
             ByteList byteList = str.convertToString().getByteList();
             ByteArrayInputStream bais = new ByteArrayInputStream(byteList.getUnsafeBytes(), byteList.getBegin(), byteList.getRealSize());
             ObjectInputStream ois = new ObjectInputStream(bais);
 
             dataWrapStruct(ois.readObject());
 
             return this;
         } catch (IOException ioe) {
             throw context.getRuntime().newIOErrorFromException(ioe);
         } catch (ClassNotFoundException cnfe) {
             throw context.getRuntime().newTypeError("Class not found unmarshaling Java type: " + cnfe.getLocalizedMessage());
         }
     }
 
     @Override
     public Object toJava(Class cls) {
         if (getValue() == null) {
             // THIS SHOULD NEVER HAPPEN, but it DOES
             return getValue();
         }
         
         if (cls.isAssignableFrom(getValue().getClass())) {
             return getValue();
         }
         throw getRuntime().newTypeError("cannot convert instance of " + getValue().getClass() + " to " + cls);
     }
 
     private static final ObjectAllocator JAVA_OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
             return new JavaObject(runtime, klazz, null);
         }
     };
 
 }
diff --git a/src/org/jruby/javasupport/JavaProxyMethods.java b/src/org/jruby/javasupport/JavaProxyMethods.java
index 6844d29c41..5b4766ec7b 100644
--- a/src/org/jruby/javasupport/JavaProxyMethods.java
+++ b/src/org/jruby/javasupport/JavaProxyMethods.java
@@ -1,81 +1,95 @@
 package org.jruby.javasupport;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBasicObject;
+import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
+import org.jruby.RubyString;
 import org.jruby.anno.JRubyMethod;
+import org.jruby.java.proxies.JavaProxy;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaProxyMethods {
     public static RubyModule createJavaProxyMethods(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         RubyModule javaProxyMethods = runtime.defineModule("JavaProxyMethods");
         
         javaProxyMethods.defineAnnotatedMethods(JavaProxyMethods.class);
         
         return javaProxyMethods;
     }
     
     @JRubyMethod
     public static IRubyObject java_class(ThreadContext context, IRubyObject recv) {
         return recv.getMetaClass().getRealClass().fastGetInstanceVariable("@java_class");
     }
 
     @JRubyMethod
     public static IRubyObject java_object(ThreadContext context, IRubyObject recv) {
         return (IRubyObject)recv.dataGetStruct();
     }
 
     @JRubyMethod(name = "java_object=")
     public static IRubyObject java_object_set(ThreadContext context, IRubyObject recv, IRubyObject obj) {
         // XXX: Check if it's appropriate type?
         recv.dataWrapStruct(obj);
         return obj;
     }
 
     @JRubyMethod(name = {"=="})
     public static IRubyObject op_equal(IRubyObject recv, IRubyObject rhs) {
+        if (recv instanceof JavaProxy) {
+            return JavaObject.op_equal((JavaProxy)recv, rhs);
+        }
         return ((JavaObject)recv.dataGetStruct()).op_equal(rhs);
     }
     
     @JRubyMethod
     public static IRubyObject to_s(IRubyObject recv) {
-        if(recv.dataGetStruct() != null) {
+        if (recv instanceof JavaProxy) {
+            return JavaObject.to_s(recv.getRuntime(), ((JavaProxy)recv).getObject());
+        } else if (recv.dataGetStruct() != null) {
             return ((JavaObject)recv.dataGetStruct()).to_s();
         } else {
             return ((RubyObject)recv).to_s();
         }
     }
 
     @JRubyMethod
     public static IRubyObject inspect(IRubyObject recv) {
         if (recv instanceof RubyBasicObject) {
             return ((RubyBasicObject)recv).hashyInspect();
         } else {
             return recv.inspect();
         }
     }
     
     @JRubyMethod(name = "eql?")
     public static IRubyObject op_eql(IRubyObject recv, IRubyObject rhs) {
-        return ((JavaObject)recv.dataGetStruct()).op_equal(rhs);
+        return op_equal(recv, rhs);
     }
     
     @JRubyMethod
     public static IRubyObject hash(IRubyObject recv) {
+        if (recv instanceof JavaProxy) {
+            return RubyFixnum.newFixnum(recv.getRuntime(), ((JavaProxy)recv).getObject().hashCode());
+        }
         return ((JavaObject)recv.dataGetStruct()).hash();
     }
 
     @JRubyMethod
     public static IRubyObject to_java_object(IRubyObject recv) {
         return (JavaObject)recv.dataGetStruct();
     }
     
     @JRubyMethod(name = "synchronized")
     public static IRubyObject rbSynchronized(ThreadContext context, IRubyObject recv, Block block) {
+        if (recv instanceof JavaProxy) {
+            return JavaObject.ruby_synchronized(context, ((JavaProxy)recv).getObject(), block);
+        }
         return ((JavaObject)recv.dataGetStruct()).ruby_synchronized(context, block);
     }
 }
diff --git a/src/org/jruby/javasupport/JavaUtil.java b/src/org/jruby/javasupport/JavaUtil.java
index 00c9775042..abc9b0f0ae 100644
--- a/src/org/jruby/javasupport/JavaUtil.java
+++ b/src/org/jruby/javasupport/JavaUtil.java
@@ -1,1333 +1,1334 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Don Schwartz <schwardo@users.sourceforge.net>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
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
 package org.jruby.javasupport;
 
 import java.io.UnsupportedEncodingException;
 import java.lang.reflect.Method;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.HashMap;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 
 import java.util.Set;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import org.jruby.Ruby;
 import org.jruby.RubyBasicObject;
 import org.jruby.RubyBigDecimal;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyEncoding;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyModule;
 import org.jruby.RubyNil;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyObject;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.RubyTime;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
+import org.jruby.java.proxies.JavaProxy;
 import org.jruby.java.proxies.RubyObjectHolderProxy;
 import org.jruby.javasupport.proxy.InternalJavaProxy;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import org.jruby.util.ByteList;
 import org.jruby.util.TypeConverter;
 
 public class JavaUtil {
     public static IRubyObject[] convertJavaArrayToRuby(Ruby runtime, Object[] objects) {
         if (objects == null) return IRubyObject.NULL_ARRAY;
         
         IRubyObject[] rubyObjects = new IRubyObject[objects.length];
         for (int i = 0; i < objects.length; i++) {
             rubyObjects[i] = convertJavaToUsableRubyObject(runtime, objects[i]);
         }
         return rubyObjects;
     }
 
     public static JavaConverter getJavaConverter(Class clazz) {
         JavaConverter converter = JAVA_CONVERTERS.get(clazz);
 
         if (converter == null) {
             converter = JAVA_DEFAULT_CONVERTER;
         }
 
         return converter;
     }
 
     public static IRubyObject convertJavaToRuby(Ruby runtime, Object object) {
         return convertJavaToUsableRubyObject(runtime, object);
     }
 
     public static IRubyObject convertJavaToRuby(Ruby runtime, Object object, Class javaClass) {
         return convertJavaToUsableRubyObjectWithConverter(runtime, object, getJavaConverter(javaClass));
     }
 
     public static IRubyObject convertJavaToRuby(Ruby runtime, int i) {
         return runtime.newFixnum(i);
     }
 
     public static IRubyObject convertJavaToRuby(Ruby runtime, long l) {
         return runtime.newFixnum(l);
     }
 
     public static IRubyObject convertJavaToRuby(Ruby runtime, float f) {
         return runtime.newFloat(f);
     }
 
     public static IRubyObject convertJavaToRuby(Ruby runtime, double d) {
         return runtime.newFloat(d);
     }
 
     public static IRubyObject convertJavaToRuby(Ruby runtime, boolean b) {
         return runtime.newBoolean(b);
     }
 
     /**
      * Returns a usable RubyObject; for types that are not converted to Ruby native
      * types, a Java proxy will be returned.
      *
      * @param runtime
      * @param object
      * @return corresponding Ruby type, or a functional Java proxy
      */
     public static IRubyObject convertJavaToUsableRubyObject(Ruby runtime, Object object) {
         IRubyObject result = trySimpleConversions(runtime, object);
 
         if (result != null) return result;
 
         JavaConverter converter = getJavaConverter(object.getClass());
         if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
             return Java.getInstance(runtime, object);
         }
         return converter.convert(runtime, object);
     }
 
     public static IRubyObject convertJavaToUsableRubyObjectWithConverter(Ruby runtime, Object object, JavaConverter converter) {
         IRubyObject result = trySimpleConversions(runtime, object);
 
         if (result != null) return result;
 
         if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
             return Java.getInstance(runtime, object);
         }
         return converter.convert(runtime, object);
     }
 
     public static IRubyObject convertJavaArrayElementToRuby(Ruby runtime, JavaConverter converter, Object array, int i) {
         if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
             IRubyObject x = convertJavaToUsableRubyObject(runtime, ((Object[])array)[i]);
             return x;
         }
         return converter.get(runtime, array, i);
     }
 
     public static Class<?> primitiveToWrapper(Class<?> type) {
         if (type.isPrimitive()) {
             if (type == boolean.class) {
                 return Boolean.class;
             } else if (type == byte.class) {
                 return Byte.class;
             } else if (type == short.class) {
                 return Short.class;
             } else if (type == char.class) {
                 return Character.class;
             } else if (type == int.class) {
                 return Integer.class;
             } else if (type == long.class) {
                 return Long.class;
             } else if (type == float.class) {
                 return Float.class;
             } else if (type == double.class) {
                 return Double.class;
             } else if (type == void.class) {
                 return Void.class;
             }
         }
         return type;
     }
 
     public static boolean isDuckTypeConvertable(Class providedArgumentType, Class parameterType) {
         return
                 parameterType.isInterface() &&
                 !parameterType.isAssignableFrom(providedArgumentType) &&
                 RubyObject.class.isAssignableFrom(providedArgumentType);
     }
 
     public static Object convertProcToInterface(ThreadContext context, RubyObject rubyObject, Class target) {
         return convertProcToInterface(context, (RubyBasicObject)rubyObject, target);
     }
 
     public static Object convertProcToInterface(ThreadContext context, RubyBasicObject rubyObject, Class target) {
         Ruby runtime = context.getRuntime();
         RubyModule javaInterfaceModule = (RubyModule)Java.get_interface_module(runtime, JavaClass.get(runtime, target));
         if (!((RubyModule) javaInterfaceModule).isInstance(rubyObject)) {
             javaInterfaceModule.callMethod(context, "extend_object", rubyObject);
             javaInterfaceModule.callMethod(context, "extended", rubyObject);
         }
 
         if (rubyObject instanceof RubyProc) {
             // Proc implementing an interface, pull in the catch-all code that lets the proc get invoked
             // no matter what method is called on the interface
             RubyClass singletonClass = rubyObject.getSingletonClass();
 
             singletonClass.addMethod("method_missing", new DynamicMethod(singletonClass, Visibility.PUBLIC, CallConfiguration.FrameNoneScopeNone) {
 
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (!(self instanceof RubyProc)) {
                         throw context.getRuntime().newTypeError("interface impl method_missing for block used with non-Proc object");
                     }
                     RubyProc proc = (RubyProc)self;
                     IRubyObject[] newArgs;
                     if (args.length == 1) {
                         newArgs = IRubyObject.NULL_ARRAY;
                     } else {
                         newArgs = new IRubyObject[args.length - 1];
                         System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                     }
                     return proc.call(context, newArgs);
                 }
 
                 @Override
                 public DynamicMethod dup() {
                     return this;
                 }
             });
         }
         JavaObject jo = (JavaObject) RuntimeHelpers.invoke(context, rubyObject, "__jcreate_meta!");
         return jo.getValue();
     }
 
     public static NumericConverter getNumericConverter(Class target) {
         NumericConverter converter = NUMERIC_CONVERTERS.get(target);
         if (converter == null) {
             return NUMERIC_TO_OTHER;
         }
         return converter;
     }
 
     public static boolean isJavaObject(IRubyObject candidate) {
-        return candidate.dataGetStruct() instanceof JavaObject;
+        return candidate instanceof JavaProxy || candidate.dataGetStruct() instanceof JavaObject;
     }
 
     public static Object unwrapJavaObject(IRubyObject object) {
-        return ((JavaObject)object.dataGetStruct()).getValue();
-    }
-
-    public static JavaObject unwrapJavaObject(Ruby runtime, IRubyObject convertee, String errorMessage) {
-        IRubyObject obj = convertee;
-        if(!(obj instanceof JavaObject)) {
-            if (obj.dataGetStruct() != null && (obj.dataGetStruct() instanceof JavaObject)) {
-                obj = (JavaObject)obj.dataGetStruct();
-            } else {
-                throw runtime.newTypeError(errorMessage);
-            }
+        if (object instanceof JavaProxy) {
+            return ((JavaProxy)object).getObject();
         }
-        return (JavaObject)obj;
+        return ((JavaObject)object.dataGetStruct()).getValue();
     }
 
     public static Object unwrapJavaValue(Ruby runtime, IRubyObject obj, String errorMessage) {
-        if(obj instanceof JavaMethod) {
-            return ((JavaMethod)obj).getValue();
-        } else if(obj instanceof JavaConstructor) {
-            return ((JavaConstructor)obj).getValue();
-        } else if(obj instanceof JavaField) {
-            return ((JavaField)obj).getValue();
-        } else if(obj instanceof JavaObject) {
+        if (obj instanceof JavaProxy) {
+            return ((JavaProxy)obj).getObject();
+        } else if (obj instanceof JavaObject) {
             return ((JavaObject)obj).getValue();
         } else if(obj.dataGetStruct() != null && (obj.dataGetStruct() instanceof IRubyObject)) {
             return unwrapJavaValue(runtime, ((IRubyObject)obj.dataGetStruct()), errorMessage);
         } else {
             throw runtime.newTypeError(errorMessage);
         }
     }
 
     private static final Pattern JAVA_PROPERTY_CHOPPER = Pattern.compile("(get|set|is)([A-Z0-9])(.*)");
     public static String getJavaPropertyName(String beanMethodName) {
         Matcher m = JAVA_PROPERTY_CHOPPER.matcher(beanMethodName);
 
         if (!m.find()) return null;
         String javaPropertyName = m.group(2).toLowerCase() + m.group(3);
         return javaPropertyName;
     }
 
     private static final Pattern CAMEL_CASE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");
     public static String getRubyCasedName(String javaCasedName) {
         Matcher m = CAMEL_CASE_SPLITTER.matcher(javaCasedName);
         return m.replaceAll("$1_$2").toLowerCase();
     }
 
     private static final Pattern RUBY_CASE_SPLITTER = Pattern.compile("([a-z][0-9]*)_([a-z])");
     public static String getJavaCasedName(String javaCasedName) {
         Matcher m = RUBY_CASE_SPLITTER.matcher(javaCasedName);
         StringBuffer newName = new StringBuffer();
         if (!m.find()) {
             return null;
         }
         m.reset();
 
         while (m.find()) {
             m.appendReplacement(newName, m.group(1) + Character.toUpperCase(m.group(2).charAt(0)));
         }
 
         m.appendTail(newName);
 
         return newName.toString();
     }
 
     /**
      * Given a simple Java method name and the Java Method objects that represent
      * all its overloads, add to the given nameSet all possible Ruby names that would
      * be valid.
      *
      * @param simpleName
      * @param nameSet
      * @param methods
      */
     public static Set<String> getRubyNamesForJavaName(String javaName, List<Method> methods) {
         String javaPropertyName = JavaUtil.getJavaPropertyName(javaName);
         String rubyName = JavaUtil.getRubyCasedName(javaName);
         Set<String> nameSet = new LinkedHashSet<String>();
         nameSet.add(javaName);
         nameSet.add(rubyName);
         String rubyPropertyName = null;
         for (Method method: methods) {
             Class<?>[] argTypes = method.getParameterTypes();
             Class<?> resultType = method.getReturnType();
             int argCount = argTypes.length;
 
             // Add property name aliases
             if (javaPropertyName != null) {
                 if (rubyName.startsWith("get_")) {
                     rubyPropertyName = rubyName.substring(4);
                     if (argCount == 0 ||                                // getFoo      => foo
                         argCount == 1 && argTypes[0] == int.class) {    // getFoo(int) => foo(int)
 
                         nameSet.add(javaPropertyName);
                         nameSet.add(rubyPropertyName);
                         if (resultType == boolean.class) {              // getFooBar() => fooBar?, foo_bar?(*)
                             nameSet.add(javaPropertyName + '?');
                             nameSet.add(rubyPropertyName + '?');
                         }
                     }
                 } else if (rubyName.startsWith("set_")) {
                     rubyPropertyName = rubyName.substring(4);
                     if (argCount == 1 && resultType == void.class) {    // setFoo(Foo) => foo=(Foo)
                         nameSet.add(javaPropertyName + '=');
                         nameSet.add(rubyPropertyName + '=');
                     }
                 } else if (rubyName.startsWith("is_")) {
                     rubyPropertyName = rubyName.substring(3);
                     if (resultType == boolean.class) {                  // isFoo() => foo, isFoo(*) => foo(*)
                         nameSet.add(javaPropertyName);
                         nameSet.add(rubyPropertyName);
                         nameSet.add(javaPropertyName + '?');
                         nameSet.add(rubyPropertyName + '?');
                     }
                 }
             } else {
                 // If not a property, but is boolean add ?-postfixed aliases.
                 if (resultType == boolean.class) {
                     // is_something?, contains_thing?
                     nameSet.add(javaName + '?');
                     nameSet.add(rubyName + '?');
                 }
             }
         }
 
         return nameSet;
     }
     
     public static abstract class JavaConverter {
         private final Class type;
         public JavaConverter(Class type) {this.type = type;}
         public abstract IRubyObject convert(Ruby runtime, Object object);
         public abstract IRubyObject get(Ruby runtime, Object array, int i);
         public String toString() {return type.getName() + " converter";}
     }
 
     public interface NumericConverter {
         public Object coerce(RubyNumeric numeric, Class target);
     }
 
     private static IRubyObject trySimpleConversions(Ruby runtime, Object object) {
         if (object == null) {
             return runtime.getNil();
         }
 
         if (object instanceof IRubyObject) {
             return (IRubyObject) object;
         }
 
         if (object instanceof RubyObjectHolderProxy) {
             return ((RubyObjectHolderProxy) object).__ruby_object();
         }
 
         if (object instanceof InternalJavaProxy) {
             InternalJavaProxy internalJavaProxy = (InternalJavaProxy) object;
             IRubyObject orig = internalJavaProxy.___getInvocationHandler().getOrig();
 
             if (orig != null) {
                 return orig;
             }
         }
 
         return null;
     }
     
     private static final JavaConverter JAVA_DEFAULT_CONVERTER = new JavaConverter(Object.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             IRubyObject result = trySimpleConversions(runtime, object);
 
             if (result != null) return result;
             
             return JavaObject.wrap(runtime, object);
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Object[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_BOOLEAN_CONVERTER = new JavaConverter(Boolean.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyBoolean.newBoolean(runtime, ((Boolean)object).booleanValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Boolean[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_FLOAT_CONVERTER = new JavaConverter(Float.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Float)object).doubleValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Float[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_DOUBLE_CONVERTER = new JavaConverter(Double.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Double)object).doubleValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Double[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_CHAR_CONVERTER = new JavaConverter(Character.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Character)object).charValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Character[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_BYTE_CONVERTER = new JavaConverter(Byte.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Byte)object).byteValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Byte[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_SHORT_CONVERTER = new JavaConverter(Short.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Short)object).shortValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Short[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_INT_CONVERTER = new JavaConverter(Integer.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Integer)object).intValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Integer[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_LONG_CONVERTER = new JavaConverter(Long.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Long)object).longValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((Long[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_BOOLEANPRIM_CONVERTER = new JavaConverter(boolean.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyBoolean.newBoolean(runtime, ((Boolean)object).booleanValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyBoolean.newBoolean(runtime, ((boolean[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_FLOATPRIM_CONVERTER = new JavaConverter(float.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Float)object).doubleValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFloat.newFloat(runtime, ((float[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_DOUBLEPRIM_CONVERTER = new JavaConverter(double.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Double)object).doubleValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFloat.newFloat(runtime, ((double[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_CHARPRIM_CONVERTER = new JavaConverter(char.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Character)object).charValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFixnum.newFixnum(runtime, ((char[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_BYTEPRIM_CONVERTER = new JavaConverter(byte.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Byte)object).byteValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFixnum.newFixnum(runtime, ((byte[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_SHORTPRIM_CONVERTER = new JavaConverter(short.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Short)object).shortValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFixnum.newFixnum(runtime, ((short[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_INTPRIM_CONVERTER = new JavaConverter(int.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Integer)object).intValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFixnum.newFixnum(runtime, ((int[])array)[i]);
         }
     };
 
     private static final JavaConverter JAVA_LONGPRIM_CONVERTER = new JavaConverter(long.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Long)object).longValue());
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return RubyFixnum.newFixnum(runtime, ((long[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_STRING_CONVERTER = new JavaConverter(String.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyString.newUnicodeString(runtime, (String)object);
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((String[])array)[i]);
         }
     };
     
     private static final JavaConverter BYTELIST_CONVERTER = new JavaConverter(ByteList.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyString.newString(runtime, (ByteList)object);
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((ByteList[])array)[i]);
         }
     };
     
     private static final JavaConverter JAVA_BIGINTEGER_CONVERTER = new JavaConverter(BigInteger.class) {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyBignum.newBignum(runtime, (BigInteger)object);
         }
         public IRubyObject get(Ruby runtime, Object array, int i) {
             return convert(runtime, ((BigInteger[])array)[i]);
         }
     };
     
     private static final Map<Class,JavaConverter> JAVA_CONVERTERS =
         new HashMap<Class,JavaConverter>();
     
     static {
         JAVA_CONVERTERS.put(Byte.class, JAVA_BYTE_CONVERTER);
         JAVA_CONVERTERS.put(Byte.TYPE, JAVA_BYTEPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Short.class, JAVA_SHORT_CONVERTER);
         JAVA_CONVERTERS.put(Short.TYPE, JAVA_SHORTPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Character.class, JAVA_CHAR_CONVERTER);
         JAVA_CONVERTERS.put(Character.TYPE, JAVA_CHARPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Integer.class, JAVA_INT_CONVERTER);
         JAVA_CONVERTERS.put(Integer.TYPE, JAVA_INTPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Long.class, JAVA_LONG_CONVERTER);
         JAVA_CONVERTERS.put(Long.TYPE, JAVA_LONGPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Float.class, JAVA_FLOAT_CONVERTER);
         JAVA_CONVERTERS.put(Float.TYPE, JAVA_FLOATPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Double.class, JAVA_DOUBLE_CONVERTER);
         JAVA_CONVERTERS.put(Double.TYPE, JAVA_DOUBLEPRIM_CONVERTER);
         JAVA_CONVERTERS.put(Boolean.class, JAVA_BOOLEAN_CONVERTER);
         JAVA_CONVERTERS.put(Boolean.TYPE, JAVA_BOOLEANPRIM_CONVERTER);
         
         JAVA_CONVERTERS.put(String.class, JAVA_STRING_CONVERTER);
         
         JAVA_CONVERTERS.put(ByteList.class, BYTELIST_CONVERTER);
         
         JAVA_CONVERTERS.put(BigInteger.class, JAVA_BIGINTEGER_CONVERTER);
     }
 
     private static NumericConverter NUMERIC_TO_BYTE = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             long value = numeric.getLongValue();
             if (isLongByteable(value)) {
                 return Byte.valueOf((byte)value);
             }
             throw numeric.getRuntime().newRangeError("too big for byte: " + numeric);
         }
     };
     private static NumericConverter NUMERIC_TO_SHORT = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             long value = numeric.getLongValue();
             if (isLongShortable(value)) {
                 return Short.valueOf((short)value);
             }
             throw numeric.getRuntime().newRangeError("too big for short: " + numeric);
         }
     };
     private static NumericConverter NUMERIC_TO_CHARACTER = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             long value = numeric.getLongValue();
             if (isLongCharable(value)) {
                 return Character.valueOf((char)value);
             }
             throw numeric.getRuntime().newRangeError("too big for char: " + numeric);
         }
     };
     private static NumericConverter NUMERIC_TO_INTEGER = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             long value = numeric.getLongValue();
             if (isLongIntable(value)) {
                 return Integer.valueOf((int)value);
             }
             throw numeric.getRuntime().newRangeError("too big for int: " + numeric);
         }
     };
     private static NumericConverter NUMERIC_TO_LONG = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             return Long.valueOf(numeric.getLongValue());
         }
     };
     private static NumericConverter NUMERIC_TO_FLOAT = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             double value = numeric.getDoubleValue();
             // many cases are ok to convert to float; if not one of these, error
             if (isDoubleFloatable(value)) {
                 return Float.valueOf((float)value);
             } else {
                 throw numeric.getRuntime().newTypeError("too big for float: " + numeric);
             }
         }
     };
     private static NumericConverter NUMERIC_TO_DOUBLE = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             return Double.valueOf(numeric.getDoubleValue());
         }
     };
     private static NumericConverter NUMERIC_TO_BIGINTEGER = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             return numeric.getBigIntegerValue();
         }
     };
     private static NumericConverter NUMERIC_TO_OBJECT = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             // for Object, default to natural wrapper type
             if (numeric instanceof RubyFixnum) {
                 long value = numeric.getLongValue();
                 return Long.valueOf(value);
             } else if (numeric instanceof RubyFloat) {
                 double value = numeric.getDoubleValue();
                 return Double.valueOf(value);
             } else if (numeric instanceof RubyBignum) {
                 return ((RubyBignum)numeric).getValue();
             } else if (numeric instanceof RubyBigDecimal) {
                 return ((RubyBigDecimal)numeric).getValue();
             } else {
                 return NUMERIC_TO_OTHER.coerce(numeric, target);
             }
         }
     };
     private static NumericConverter NUMERIC_TO_OTHER = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             throw numeric.getRuntime().newTypeError("could not coerce " + numeric.getMetaClass() + " to " + target);
         }
     };
     private static NumericConverter NUMERIC_TO_VOID = new NumericConverter() {
         public Object coerce(RubyNumeric numeric, Class target) {
             return null;
         }
     };
     private static boolean isDoubleFloatable(double value) {
         return true;
     }
     private static boolean isLongByteable(long value) {
         return value >= Byte.MIN_VALUE && value <= 0xFF;
     }
     private static boolean isLongShortable(long value) {
         return value >= Short.MIN_VALUE && value <= 0xFFFF;
     }
     private static boolean isLongCharable(long value) {
         return value >= Character.MIN_VALUE && value <= Character.MAX_VALUE;
     }
     private static boolean isLongIntable(long value) {
         return value >= Integer.MIN_VALUE && value <= 0xFFFFFFFFL;
     }
     
     private static Map<Class, NumericConverter> NUMERIC_CONVERTERS = new HashMap<Class, NumericConverter>();
 
     static {
         NUMERIC_CONVERTERS.put(Byte.TYPE, NUMERIC_TO_BYTE);
         NUMERIC_CONVERTERS.put(Byte.class, NUMERIC_TO_BYTE);
         NUMERIC_CONVERTERS.put(Short.TYPE, NUMERIC_TO_SHORT);
         NUMERIC_CONVERTERS.put(Short.class, NUMERIC_TO_SHORT);
         NUMERIC_CONVERTERS.put(Character.TYPE, NUMERIC_TO_CHARACTER);
         NUMERIC_CONVERTERS.put(Character.class, NUMERIC_TO_CHARACTER);
         NUMERIC_CONVERTERS.put(Integer.TYPE, NUMERIC_TO_INTEGER);
         NUMERIC_CONVERTERS.put(Integer.class, NUMERIC_TO_INTEGER);
         NUMERIC_CONVERTERS.put(Long.TYPE, NUMERIC_TO_LONG);
         NUMERIC_CONVERTERS.put(Long.class, NUMERIC_TO_LONG);
         NUMERIC_CONVERTERS.put(Float.TYPE, NUMERIC_TO_FLOAT);
         NUMERIC_CONVERTERS.put(Float.class, NUMERIC_TO_FLOAT);
         NUMERIC_CONVERTERS.put(Double.TYPE, NUMERIC_TO_DOUBLE);
         NUMERIC_CONVERTERS.put(Double.class, NUMERIC_TO_DOUBLE);
         NUMERIC_CONVERTERS.put(BigInteger.class, NUMERIC_TO_BIGINTEGER);
         NUMERIC_CONVERTERS.put(Object.class, NUMERIC_TO_OBJECT);
         NUMERIC_CONVERTERS.put(void.class, NUMERIC_TO_VOID);
     }
 
     @Deprecated
     public static Object convertRubyToJava(IRubyObject rubyObject) {
         return convertRubyToJava(rubyObject, Object.class);
     }
 
     @Deprecated
     public static Object convertRubyToJava(IRubyObject rubyObject, Class javaClass) {
         if (javaClass == void.class || rubyObject == null || rubyObject.isNil()) {
             return null;
         }
 
         ThreadContext context = rubyObject.getRuntime().getCurrentContext();
         IRubyObject origObject = rubyObject;
         if (rubyObject.dataGetStruct() instanceof JavaObject) {
             rubyObject = (JavaObject) rubyObject.dataGetStruct();
             if(rubyObject == null) {
                 throw new RuntimeException("dataGetStruct returned null for " + origObject.getType().getName());
             }
         } else if (rubyObject.respondsTo("java_object")) {
             rubyObject = rubyObject.callMethod(context, "java_object");
             if(rubyObject == null) {
                 throw new RuntimeException("java_object returned null for " + origObject.getType().getName());
             }
         }
 
         if (rubyObject instanceof JavaObject) {
             Object value =  ((JavaObject) rubyObject).getValue();
 
             return convertArgument(rubyObject.getRuntime(), value, value.getClass());
 
         } else if (javaClass == Object.class || javaClass == null) {
             /* The Java method doesn't care what class it is, but we need to
                know what to convert it to, so we use the object's own class.
                If that doesn't help, we use String to force a call to the
                object's "to_s" method. */
             javaClass = rubyObject.getJavaClass();
         }
 
         if (javaClass.isInstance(rubyObject)) {
             // rubyObject is already of the required jruby class (or subclass)
             return rubyObject;
         }
 
         // the converters handle not only primitive types but also their boxed versions, so we should check
         // if we have a converter before checking for isPrimitive()
         RubyConverter converter = RUBY_CONVERTERS.get(javaClass);
         if (converter != null) {
             return converter.convert(context, rubyObject);
         }
 
         if (javaClass.isPrimitive()) {
             String s = ((RubyString)TypeConverter.convertToType(rubyObject, rubyObject.getRuntime().getString(), "to_s", true)).getUnicodeValue();
             if (s.length() > 0) {
                 return Character.valueOf(s.charAt(0));
             }
             return Character.valueOf('\0');
         } else if (javaClass == String.class) {
             RubyString rubyString = (RubyString) rubyObject.callMethod(context, "to_s");
             ByteList bytes = rubyString.getByteList();
             return RubyEncoding.decodeUTF8(bytes.getUnsafeBytes(), bytes.begin(), bytes.length());
         } else if (javaClass == ByteList.class) {
             return rubyObject.convertToString().getByteList();
         } else if (javaClass == BigInteger.class) {
          	if (rubyObject instanceof RubyBignum) {
          		return ((RubyBignum)rubyObject).getValue();
          	} else if (rubyObject instanceof RubyNumeric) {
  				return  BigInteger.valueOf (((RubyNumeric)rubyObject).getLongValue());
          	} else if (rubyObject.respondsTo("to_i")) {
          		RubyNumeric rubyNumeric = ((RubyNumeric)rubyObject.callMethod(context, "to_f"));
  				return  BigInteger.valueOf (rubyNumeric.getLongValue());
          	}
         } else if (javaClass == BigDecimal.class && !(rubyObject instanceof JavaObject)) {
          	if (rubyObject.respondsTo("to_f")) {
              	double double_value = ((RubyNumeric)rubyObject.callMethod(context, "to_f")).getDoubleValue();
              	return new BigDecimal(double_value);
          	}
         }
 
         try {
             if (isDuckTypeConvertable(rubyObject.getClass(), javaClass)) {
                 return convertProcToInterface(context, (RubyObject) rubyObject, javaClass);
             }
             return ((JavaObject) rubyObject).getValue();
         } catch (ClassCastException ex) {
             if (rubyObject.getRuntime().getDebug().isTrue()) ex.printStackTrace();
             return null;
         }
     }
 
     @Deprecated
     public static byte convertRubyToJavaByte(IRubyObject rubyObject) {
         return ((Byte)convertRubyToJava(rubyObject, byte.class)).byteValue();
     }
 
     @Deprecated
     public static short convertRubyToJavaShort(IRubyObject rubyObject) {
         return ((Short)convertRubyToJava(rubyObject, short.class)).shortValue();
     }
 
     @Deprecated
     public static char convertRubyToJavaChar(IRubyObject rubyObject) {
         return ((Character)convertRubyToJava(rubyObject, char.class)).charValue();
     }
 
     @Deprecated
     public static int convertRubyToJavaInt(IRubyObject rubyObject) {
         return ((Integer)convertRubyToJava(rubyObject, int.class)).intValue();
     }
 
     @Deprecated
     public static long convertRubyToJavaLong(IRubyObject rubyObject) {
         return ((Long)convertRubyToJava(rubyObject, long.class)).longValue();
     }
 
     @Deprecated
     public static float convertRubyToJavaFloat(IRubyObject rubyObject) {
         return ((Float)convertRubyToJava(rubyObject, float.class)).floatValue();
     }
 
     @Deprecated
     public static double convertRubyToJavaDouble(IRubyObject rubyObject) {
         return ((Double)convertRubyToJava(rubyObject, double.class)).doubleValue();
     }
 
     @Deprecated
     public static boolean convertRubyToJavaBoolean(IRubyObject rubyObject) {
         return ((Boolean)convertRubyToJava(rubyObject, boolean.class)).booleanValue();
     }
 
     @Deprecated
     public static Object convertArgumentToType(ThreadContext context, IRubyObject arg, Class target) {
         return arg.toJava(target);
     }
 
     @Deprecated
     public static Object coerceNilToType(RubyNil nil, Class target) {
         return nil.toJava(target);
     }
 
     @Deprecated
     public static final RubyConverter RUBY_BOOLEAN_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return Boolean.valueOf(rubyObject.isTrue());
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_BYTE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return Byte.valueOf((byte) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return Byte.valueOf((byte) 0);
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_SHORT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return Short.valueOf((short) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return Short.valueOf((short) 0);
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_CHAR_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return Character.valueOf((char) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return Character.valueOf((char) 0);
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_INTEGER_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return Integer.valueOf((int) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return Integer.valueOf(0);
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_LONG_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return Long.valueOf(((RubyNumeric) rubyObject.callMethod(
                         context, "to_i")).getLongValue());
             }
             return Long.valueOf(0);
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_FLOAT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Float((float) ((RubyNumeric) rubyObject.callMethod(
                         context, "to_f")).getDoubleValue());
             }
             return new Float(0.0);
         }
     };
 
     @Deprecated
     public static final RubyConverter RUBY_DOUBLE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Double(((RubyNumeric) rubyObject.callMethod(
                         context, "to_f")).getDoubleValue());
             }
             return new Double(0.0);
         }
     };
 
     @Deprecated
     public static final Map<Class, RubyConverter> RUBY_CONVERTERS = new HashMap<Class, RubyConverter>();
     static {
         RUBY_CONVERTERS.put(Boolean.class, RUBY_BOOLEAN_CONVERTER);
         RUBY_CONVERTERS.put(Boolean.TYPE, RUBY_BOOLEAN_CONVERTER);
         RUBY_CONVERTERS.put(Byte.class, RUBY_BYTE_CONVERTER);
         RUBY_CONVERTERS.put(Byte.TYPE, RUBY_BYTE_CONVERTER);
         RUBY_CONVERTERS.put(Short.class, RUBY_SHORT_CONVERTER);
         RUBY_CONVERTERS.put(Short.TYPE, RUBY_SHORT_CONVERTER);
         RUBY_CONVERTERS.put(Integer.class, RUBY_INTEGER_CONVERTER);
         RUBY_CONVERTERS.put(Integer.TYPE, RUBY_INTEGER_CONVERTER);
         RUBY_CONVERTERS.put(Long.class, RUBY_LONG_CONVERTER);
         RUBY_CONVERTERS.put(Long.TYPE, RUBY_LONG_CONVERTER);
         RUBY_CONVERTERS.put(Float.class, RUBY_FLOAT_CONVERTER);
         RUBY_CONVERTERS.put(Float.TYPE, RUBY_FLOAT_CONVERTER);
         RUBY_CONVERTERS.put(Double.class, RUBY_DOUBLE_CONVERTER);
         RUBY_CONVERTERS.put(Double.TYPE, RUBY_DOUBLE_CONVERTER);
     }
 
     @Deprecated
     public static IRubyObject convertJavaToRuby(Ruby runtime, JavaConverter converter, Object object) {
         if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
             return Java.getInstance(runtime, object);
         }
         return converter.convert(runtime, object);
     }
 
     @Deprecated
     public interface RubyConverter {
         public Object convert(ThreadContext context, IRubyObject rubyObject);
     }
 
     @Deprecated
     public static final RubyConverter ARRAY_BOOLEAN_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Boolean.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_BYTE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Byte.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_SHORT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Short.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_CHAR_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Character.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_INT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Integer.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_LONG_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Long.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_FLOAT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Float.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_DOUBLE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Double.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_OBJECT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Object.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_CLASS_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(Class.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_STRING_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(String.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_BIGINTEGER_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(BigInteger.class);
         }
     };
 
     @Deprecated
     public static final RubyConverter ARRAY_BIGDECIMAL_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return rubyObject.toJava(BigDecimal.class);
         }
     };
 
     @Deprecated
     public static final Map<Class, RubyConverter> ARRAY_CONVERTERS = new HashMap<Class, RubyConverter>();
     static {
         ARRAY_CONVERTERS.put(Boolean.class, ARRAY_BOOLEAN_CONVERTER);
         ARRAY_CONVERTERS.put(Boolean.TYPE, ARRAY_BOOLEAN_CONVERTER);
         ARRAY_CONVERTERS.put(Byte.class, ARRAY_BYTE_CONVERTER);
         ARRAY_CONVERTERS.put(Byte.TYPE, ARRAY_BYTE_CONVERTER);
         ARRAY_CONVERTERS.put(Short.class, ARRAY_SHORT_CONVERTER);
         ARRAY_CONVERTERS.put(Short.TYPE, ARRAY_SHORT_CONVERTER);
         ARRAY_CONVERTERS.put(Character.class, ARRAY_CHAR_CONVERTER);
         ARRAY_CONVERTERS.put(Character.TYPE, ARRAY_CHAR_CONVERTER);
         ARRAY_CONVERTERS.put(Integer.class, ARRAY_INT_CONVERTER);
         ARRAY_CONVERTERS.put(Integer.TYPE, ARRAY_INT_CONVERTER);
         ARRAY_CONVERTERS.put(Long.class, ARRAY_LONG_CONVERTER);
         ARRAY_CONVERTERS.put(Long.TYPE, ARRAY_LONG_CONVERTER);
         ARRAY_CONVERTERS.put(Float.class, ARRAY_FLOAT_CONVERTER);
         ARRAY_CONVERTERS.put(Float.TYPE, ARRAY_FLOAT_CONVERTER);
         ARRAY_CONVERTERS.put(Double.class, ARRAY_DOUBLE_CONVERTER);
         ARRAY_CONVERTERS.put(Double.TYPE, ARRAY_DOUBLE_CONVERTER);
         ARRAY_CONVERTERS.put(String.class, ARRAY_STRING_CONVERTER);
         ARRAY_CONVERTERS.put(Class.class, ARRAY_CLASS_CONVERTER);
         ARRAY_CONVERTERS.put(BigInteger.class, ARRAY_BIGINTEGER_CONVERTER);
         ARRAY_CONVERTERS.put(BigDecimal.class, ARRAY_BIGDECIMAL_CONVERTER);
     }
 
     @Deprecated
     public static RubyConverter getArrayConverter(Class type) {
         RubyConverter converter = ARRAY_CONVERTERS.get(type);
         if (converter == null) {
             return ARRAY_OBJECT_CONVERTER;
         }
         return converter;
     }
 
     /**
      * High-level object conversion utility.
      */
     @Deprecated
     public static IRubyObject ruby_to_java(final IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object.respondsTo("to_java_object")) {
             IRubyObject result = (JavaObject)object.dataGetStruct();
             if (result == null) {
                 result = object.callMethod(recv.getRuntime().getCurrentContext(), "to_java_object");
             }
             if (result instanceof JavaObject) {
                 recv.getRuntime().getJavaSupport().getObjectProxyCache().put(((JavaObject) result).getValue(), object);
             }
             return result;
         }
 
         return primitive_to_java(recv, object, unusedBlock);
     }
 
     @Deprecated
     public static IRubyObject java_to_primitive(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object instanceof JavaObject) {
             return JavaUtil.convertJavaToRuby(recv.getRuntime(), ((JavaObject) object).getValue());
         }
 
         return object;
     }
 
     @Deprecated
     public static IRubyObject primitive_to_java(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object instanceof JavaObject) {
             return object;
         }
         Ruby runtime = recv.getRuntime();
         Object javaObject;
         switch (object.getMetaClass().index) {
         case ClassIndex.NIL:
             javaObject = null;
             break;
         case ClassIndex.FIXNUM:
             javaObject = Long.valueOf(((RubyFixnum) object).getLongValue());
             break;
         case ClassIndex.BIGNUM:
             javaObject = ((RubyBignum) object).getValue();
             break;
         case ClassIndex.FLOAT:
             javaObject = new Double(((RubyFloat) object).getValue());
             break;
         case ClassIndex.STRING:
             ByteList bytes = ((RubyString) object).getByteList();
             javaObject = RubyEncoding.decodeUTF8(bytes.getUnsafeBytes(), bytes.begin(), bytes.length());
             break;
         case ClassIndex.TRUE:
             javaObject = Boolean.TRUE;
             break;
         case ClassIndex.FALSE:
             javaObject = Boolean.FALSE;
             break;
         case ClassIndex.TIME:
             javaObject = ((RubyTime) object).getJavaDate();
             break;
         default:
             // it's not one of the types we convert, so just pass it out as-is without wrapping
             return object;
         }
 
         // we've found a Java type to which we've coerced the Ruby value, wrap it
         return JavaObject.wrap(runtime, javaObject);
     }
 
     @Deprecated
     public static Object convertArgument(Ruby runtime, Object argument, Class<?> parameterType) {
         if (argument == null) {
           if(parameterType.isPrimitive()) {
             throw runtime.newTypeError("primitives do not accept null");
           } else {
             return null;
           }
         }
 
         if (argument instanceof JavaObject) {
             argument = ((JavaObject) argument).getValue();
             if (argument == null) {
                 return null;
             }
         }
         Class<?> type = primitiveToWrapper(parameterType);
         if (type == Void.class) {
             return null;
         }
         if (argument instanceof Number) {
             final Number number = (Number) argument;
             if (type == Long.class) {
                 return Long.valueOf(number.longValue());
             } else if (type == Integer.class) {
                 return Integer.valueOf(number.intValue());
             } else if (type == Byte.class) {
                 return Byte.valueOf(number.byteValue());
             } else if (type == Character.class) {
                 return Character.valueOf((char) number.intValue());
             } else if (type == Double.class) {
                 return new Double(number.doubleValue());
             } else if (type == Float.class) {
                 return new Float(number.floatValue());
             } else if (type == Short.class) {
                 return Short.valueOf(number.shortValue());
             }
         }
         if (isDuckTypeConvertable(argument.getClass(), parameterType)) {
             RubyObject rubyObject = (RubyObject) argument;
             if (!rubyObject.respondsTo("java_object")) {
                 return convertProcToInterface(runtime.getCurrentContext(), rubyObject, parameterType);
             }
         }
         return argument;
     }
 
     /**
      * High-level object conversion utility function 'java_to_primitive' is the low-level version
      */
     @Deprecated
     public static IRubyObject java_to_ruby(Ruby runtime, IRubyObject object) {
         if (object instanceof JavaObject) {
             return JavaUtil.convertJavaToUsableRubyObject(runtime, ((JavaObject) object).getValue());
         }
         return object;
     }
 
     // FIXME: This doesn't actually support anything but String
     @Deprecated
     public static Object coerceStringToType(RubyString string, Class target) {
         try {
             ByteList bytes = string.getByteList();
 
             // 1.9 support for encodings
             // TODO: Fix charset use for JRUBY-4553
             if (string.getRuntime().is1_9()) {
                 return new String(bytes.getUnsafeBytes(), bytes.begin(), bytes.length(), string.getEncoding().toString());
             }
 
             return RubyEncoding.decodeUTF8(bytes.getUnsafeBytes(), bytes.begin(), bytes.length());
         } catch (UnsupportedEncodingException uee) {
             return string.toString();
         }
     }
 
     @Deprecated
     public static Object coerceOtherToType(ThreadContext context, IRubyObject arg, Class target) {
         if (isDuckTypeConvertable(arg.getClass(), target)) {
             RubyObject rubyObject = (RubyObject) arg;
             if (!rubyObject.respondsTo("java_object")) {
                 return convertProcToInterface(context, rubyObject, target);
             }
         }
 
         // it's either as converted as we can make it via above logic or it's
         // not one of the types we convert, so just pass it out as-is without wrapping
         return arg;
     }
 
     @Deprecated
     public static Object coerceJavaObjectToType(ThreadContext context, Object javaObject, Class target) {
         if (javaObject != null && isDuckTypeConvertable(javaObject.getClass(), target)) {
             RubyObject rubyObject = (RubyObject) javaObject;
             if (!rubyObject.respondsTo("java_object")) {
                 return convertProcToInterface(context, rubyObject, target);
             }
 
             // can't be converted any more, return it
             return javaObject;
         } else {
             return javaObject;
         }
     }
+
+    @Deprecated
+    public static JavaObject unwrapJavaObject(Ruby runtime, IRubyObject convertee, String errorMessage) {
+        IRubyObject obj = convertee;
+        if(!(obj instanceof JavaObject)) {
+            if (obj.dataGetStruct() != null && (obj.dataGetStruct() instanceof JavaObject)) {
+                obj = (JavaObject)obj.dataGetStruct();
+            } else {
+                throw runtime.newTypeError(errorMessage);
+            }
+        }
+        return (JavaObject)obj;
+    }
 }
