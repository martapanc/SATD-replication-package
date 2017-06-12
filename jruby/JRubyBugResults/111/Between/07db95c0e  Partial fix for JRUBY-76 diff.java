diff --git a/src/org/jruby/RubyBinding.java b/src/org/jruby/RubyBinding.java
index db5638aa18..9031369555 100644
--- a/src/org/jruby/RubyBinding.java
+++ b/src/org/jruby/RubyBinding.java
@@ -1,89 +1,104 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.ThreadContext;
 
 /**
  * @author  jpetersen
  */
 public class RubyBinding extends RubyObject {
     private Block block = null;
     private RubyModule wrapper = null;
 
     public RubyBinding(Ruby runtime, RubyClass rubyClass, Block block, RubyModule wrapper) {
         super(runtime, rubyClass);
         
         this.block = block;
         this.wrapper = wrapper;
     }
 
     public Block getBlock() {
         return block;
     }
 
     public RubyModule getWrapper() {
         return wrapper;
     }
 
     // Proc class
     
     public static RubyBinding newBinding(Ruby runtime, Block block) {
         return new RubyBinding(runtime, runtime.getClass("Binding"), block, block.getKlass());
     }
 
     public static RubyBinding newBinding(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
         
         // FIXME: We should be cloning, not reusing: frame, scope, dynvars, and potentially iter/block info
         RubyModule wrapper = context.getWrapper();
         Frame frame = context.getCurrentFrame();
 
         Block bindingBlock = Block.createBinding(wrapper, frame, context.getCurrentScope());
         return new RubyBinding(runtime, runtime.getClass("Binding"), bindingBlock, context.getBindingRubyClass());
     }
 
+    /**
+     * Create a binding appropriate for a bare "eval", by using the previous (caller's) frame and current
+     * scope.
+     */
+    public static RubyBinding newBindingForEval(Ruby runtime) {
+        ThreadContext context = runtime.getCurrentContext();
+        
+        // FIXME: We should be cloning, not reusing: frame, scope, dynvars, and potentially iter/block info
+        RubyModule wrapper = context.getWrapper();
+        Frame frame = context.getPreviousFrame();
+
+        Block bindingBlock = Block.createBinding(wrapper, frame, context.getCurrentScope());
+        return new RubyBinding(runtime, runtime.getClass("Binding"), bindingBlock, context.getBindingRubyClass());
+    }
+
     public static RubyBinding newBindingOfCaller(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
         
         // FIXME: We should be cloning, not reusing: frame, scope, dynvars, and potentially iter/block info
         RubyModule wrapper = context.getWrapper();
         Frame frame = context.getPreviousFrame();
 
         Block bindingBlock = Block.createBinding(wrapper, frame, context.getPreviousScope());
         return new RubyBinding(runtime, runtime.getClass("Binding"), bindingBlock, context.getBindingRubyClass());
     }
 }
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index e3d92683a9..9983a71a34 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,976 +1,976 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Kiel Hodges <jruby-devel@selfsosoft.com>
  * Copyright (C) 2006 Evan Buswell <evan@heron.sytes.net>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
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
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.util.Calendar;
 import java.util.Iterator;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.meta.FileMetaClass;
 import org.jruby.runtime.builtin.meta.IOMetaClass;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.Sprintf;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  *
  * @author jpetersen
  */
 public class RubyKernel {
     public final static Class IRUBY_OBJECT = IRubyObject.class;
 
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyKernel.class);
         CallbackFactory objectCallbackFactory = runtime.callbackFactory(RubyObject.class);
 
         module.defineFastModuleFunction("Array", callbackFactory.getFastSingletonMethod("new_array", IRUBY_OBJECT));
         module.defineFastModuleFunction("Float", callbackFactory.getFastSingletonMethod("new_float", IRUBY_OBJECT));
         module.defineFastModuleFunction("Integer", callbackFactory.getFastSingletonMethod("new_integer", IRUBY_OBJECT));
         module.defineFastModuleFunction("String", callbackFactory.getFastSingletonMethod("new_string", IRUBY_OBJECT));
         module.defineFastModuleFunction("`", callbackFactory.getFastSingletonMethod("backquote", IRUBY_OBJECT));
         module.defineFastModuleFunction("abort", callbackFactory.getFastOptSingletonMethod("abort"));
         module.defineModuleFunction("at_exit", callbackFactory.getSingletonMethod("at_exit"));
         module.defineFastModuleFunction("autoload", callbackFactory.getFastSingletonMethod("autoload", IRUBY_OBJECT, IRUBY_OBJECT));
         module.defineFastModuleFunction("autoload?", callbackFactory.getFastSingletonMethod("autoload_p", IRUBY_OBJECT));
         module.defineModuleFunction("binding", callbackFactory.getSingletonMethod("binding"));
         module.defineModuleFunction("block_given?", callbackFactory.getSingletonMethod("block_given"));
         module.defineModuleFunction("callcc", callbackFactory.getOptSingletonMethod("callcc"));
         module.defineModuleFunction("caller", callbackFactory.getOptSingletonMethod("caller"));
         module.defineModuleFunction("catch", callbackFactory.getSingletonMethod("rbCatch", IRUBY_OBJECT));
         module.defineFastModuleFunction("chomp", callbackFactory.getFastOptSingletonMethod("chomp"));
         module.defineFastModuleFunction("chomp!", callbackFactory.getFastOptSingletonMethod("chomp_bang"));
         module.defineFastModuleFunction("chop", callbackFactory.getFastSingletonMethod("chop"));
         module.defineFastModuleFunction("chop!", callbackFactory.getFastSingletonMethod("chop_bang"));
         module.defineModuleFunction("eval", callbackFactory.getOptSingletonMethod("eval"));
         module.defineFastModuleFunction("exit", callbackFactory.getFastOptSingletonMethod("exit"));
         module.defineFastModuleFunction("exit!", callbackFactory.getFastOptSingletonMethod("exit_bang"));
         module.defineModuleFunction("fail", callbackFactory.getOptSingletonMethod("raise"));
         // TODO: Implement Kernel#fork
         module.defineFastModuleFunction("format", callbackFactory.getFastOptSingletonMethod("sprintf"));
         module.defineFastModuleFunction("gets", callbackFactory.getFastOptSingletonMethod("gets"));
         module.defineFastModuleFunction("global_variables", callbackFactory.getFastSingletonMethod("global_variables"));
         module.defineModuleFunction("gsub", callbackFactory.getOptSingletonMethod("gsub"));
         module.defineModuleFunction("gsub!", callbackFactory.getOptSingletonMethod("gsub_bang"));
         // TODO: Add deprecation to Kernel#iterator? (maybe formal deprecation mech.)
         module.defineModuleFunction("iterator?", callbackFactory.getSingletonMethod("block_given"));
         module.defineModuleFunction("lambda", callbackFactory.getSingletonMethod("proc"));
         module.defineModuleFunction("load", callbackFactory.getOptSingletonMethod("load"));
         module.defineFastModuleFunction("local_variables", callbackFactory.getFastSingletonMethod("local_variables"));
         module.defineModuleFunction("loop", callbackFactory.getSingletonMethod("loop"));
         // Note: method_missing is documented as being in Object, but ruby appears to stick it in Kernel.
         module.defineModuleFunction("method_missing", callbackFactory.getOptSingletonMethod("method_missing"));
         module.defineModuleFunction("open", callbackFactory.getOptSingletonMethod("open"));
         module.defineFastModuleFunction("p", callbackFactory.getFastOptSingletonMethod("p"));
         module.defineFastModuleFunction("print", callbackFactory.getFastOptSingletonMethod("print"));
         module.defineFastModuleFunction("printf", callbackFactory.getFastOptSingletonMethod("printf"));
         module.defineModuleFunction("proc", callbackFactory.getSingletonMethod("proc"));
         // TODO: implement Kernel#putc
         module.defineFastModuleFunction("putc", callbackFactory.getFastSingletonMethod("putc", IRubyObject.class));
         module.defineFastModuleFunction("puts", callbackFactory.getFastOptSingletonMethod("puts"));
         module.defineModuleFunction("raise", callbackFactory.getOptSingletonMethod("raise"));
         module.defineFastModuleFunction("rand", callbackFactory.getFastOptSingletonMethod("rand"));
         module.defineFastModuleFunction("readline", callbackFactory.getFastOptSingletonMethod("readline"));
         module.defineFastModuleFunction("readlines", callbackFactory.getFastOptSingletonMethod("readlines"));
         module.defineModuleFunction("require", callbackFactory.getSingletonMethod("require", IRUBY_OBJECT));
         module.defineModuleFunction("scan", callbackFactory.getSingletonMethod("scan", IRUBY_OBJECT));
         module.defineFastModuleFunction("select", callbackFactory.getFastOptSingletonMethod("select"));
         module.defineModuleFunction("set_trace_func", callbackFactory.getSingletonMethod("set_trace_func", IRUBY_OBJECT));
         module.defineFastModuleFunction("sleep", callbackFactory.getFastSingletonMethod("sleep", IRUBY_OBJECT));
         module.defineFastModuleFunction("split", callbackFactory.getFastOptSingletonMethod("split"));
         module.defineFastModuleFunction("sprintf", callbackFactory.getFastOptSingletonMethod("sprintf"));
         module.defineFastModuleFunction("srand", callbackFactory.getFastOptSingletonMethod("srand"));
         module.defineModuleFunction("sub", callbackFactory.getOptSingletonMethod("sub"));
         module.defineModuleFunction("sub!", callbackFactory.getOptSingletonMethod("sub_bang"));
         // Skipping: Kernel#syscall (too system dependent)
         module.defineFastModuleFunction("system", callbackFactory.getFastOptSingletonMethod("system"));
         // TODO: Implement Kernel#exec differently?
         module.defineFastModuleFunction("exec", callbackFactory.getFastOptSingletonMethod("system"));
         module.defineFastModuleFunction("test", callbackFactory.getFastOptSingletonMethod("test"));
         module.defineModuleFunction("throw", callbackFactory.getOptSingletonMethod("rbThrow"));
         // TODO: Implement Kernel#trace_var
         module.defineModuleFunction("trap", callbackFactory.getOptSingletonMethod("trap"));
         // TODO: Implement Kernel#untrace_var
         module.defineFastModuleFunction("warn", callbackFactory.getFastSingletonMethod("warn", IRUBY_OBJECT));
         
         // Defined p411 Pickaxe 2nd ed.
         module.defineModuleFunction("singleton_method_added", callbackFactory.getSingletonMethod("singleton_method_added", IRUBY_OBJECT));
         module.defineModuleFunction("singleton_method_removed", callbackFactory.getSingletonMethod("singleton_method_removed", IRUBY_OBJECT));
         module.defineModuleFunction("singleton_method_undefined", callbackFactory.getSingletonMethod("singleton_method_undefined", IRUBY_OBJECT));
         
         // Object methods
         module.defineFastPublicModuleFunction("==", objectCallbackFactory.getFastMethod("obj_equal", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("===", objectCallbackFactory.getFastMethod("equal", IRUBY_OBJECT));
 
         module.defineAlias("eql?", "==");
         module.defineFastPublicModuleFunction("to_s", objectCallbackFactory.getFastMethod("to_s"));
         module.defineFastPublicModuleFunction("nil?", objectCallbackFactory.getFastMethod("nil_p"));
         module.defineFastPublicModuleFunction("to_a", callbackFactory.getFastSingletonMethod("to_a"));
         module.defineFastPublicModuleFunction("hash", objectCallbackFactory.getFastMethod("hash"));
         module.defineFastPublicModuleFunction("id", objectCallbackFactory.getFastMethod("id_deprecated"));
         module.defineFastPublicModuleFunction("object_id", objectCallbackFactory.getFastMethod("id"));
         module.defineAlias("__id__", "object_id");
         module.defineFastPublicModuleFunction("is_a?", objectCallbackFactory.getFastMethod("kind_of", IRUBY_OBJECT));
         module.defineAlias("kind_of?", "is_a?");
         module.defineFastPublicModuleFunction("dup", objectCallbackFactory.getFastMethod("dup"));
         module.defineFastPublicModuleFunction("equal?", objectCallbackFactory.getFastMethod("same", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("type", objectCallbackFactory.getFastMethod("type_deprecated"));
         module.defineFastPublicModuleFunction("class", objectCallbackFactory.getFastMethod("type"));
         module.defineFastPublicModuleFunction("inspect", objectCallbackFactory.getFastMethod("inspect"));
         module.defineFastPublicModuleFunction("=~", objectCallbackFactory.getFastMethod("match", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("clone", objectCallbackFactory.getFastMethod("rbClone"));
         module.defineFastPublicModuleFunction("display", objectCallbackFactory.getFastOptMethod("display"));
         module.defineFastPublicModuleFunction("extend", objectCallbackFactory.getFastOptMethod("extend"));
         module.defineFastPublicModuleFunction("freeze", objectCallbackFactory.getFastMethod("freeze"));
         module.defineFastPublicModuleFunction("frozen?", objectCallbackFactory.getFastMethod("frozen"));
         module.defineFastModuleFunction("initialize_copy", objectCallbackFactory.getFastMethod("initialize_copy", IRUBY_OBJECT));
         module.definePublicModuleFunction("instance_eval", objectCallbackFactory.getOptMethod("instance_eval"));
         module.defineFastPublicModuleFunction("instance_of?", objectCallbackFactory.getFastMethod("instance_of", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("instance_variables", objectCallbackFactory.getFastMethod("instance_variables"));
         module.defineFastPublicModuleFunction("instance_variable_get", objectCallbackFactory.getFastMethod("instance_variable_get", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("instance_variable_set", objectCallbackFactory.getFastMethod("instance_variable_set", IRUBY_OBJECT, IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("method", objectCallbackFactory.getFastMethod("method", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("methods", objectCallbackFactory.getFastOptMethod("methods"));
         module.defineFastPublicModuleFunction("private_methods", objectCallbackFactory.getFastMethod("private_methods"));
         module.defineFastPublicModuleFunction("protected_methods", objectCallbackFactory.getFastMethod("protected_methods"));
         module.defineFastPublicModuleFunction("public_methods", objectCallbackFactory.getFastOptMethod("public_methods"));
         module.defineFastModuleFunction("remove_instance_variable", objectCallbackFactory.getMethod("remove_instance_variable", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("respond_to?", objectCallbackFactory.getFastOptMethod("respond_to"));
         module.definePublicModuleFunction("send", objectCallbackFactory.getOptMethod("send"));
         module.defineAlias("__send__", "send");
         module.defineFastPublicModuleFunction("singleton_methods", objectCallbackFactory.getFastOptMethod("singleton_methods"));
         module.defineFastPublicModuleFunction("taint", objectCallbackFactory.getFastMethod("taint"));
         module.defineFastPublicModuleFunction("tainted?", objectCallbackFactory.getFastMethod("tainted"));
         module.defineFastPublicModuleFunction("untaint", objectCallbackFactory.getFastMethod("untaint"));
 
         runtime.setRespondToMethod(module.searchMethod("respond_to?"));
 
         return module;
     }
 
     public static IRubyObject at_exit(IRubyObject recv, Block block) {
         return recv.getRuntime().pushExitBlock(recv.getRuntime().newProc(false, block));
     }
 
     public static IRubyObject autoload_p(final IRubyObject recv, IRubyObject symbol) {
         String name = symbol.asSymbol();
         if (recv instanceof RubyModule) {
             name = ((RubyModule)recv).getName() + "::" + name;
         }
         
         IAutoloadMethod autoloadMethod = recv.getRuntime().getLoadService().autoloadFor(name);
         if(autoloadMethod == null) return recv.getRuntime().getNil();
 
         return recv.getRuntime().newString(autoloadMethod.file());
     }
 
     public static IRubyObject autoload(final IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         final LoadService loadService = recv.getRuntime().getLoadService();
         final String baseName = symbol.asSymbol();
         String nm = baseName;
         if(recv instanceof RubyModule) {
             nm = ((RubyModule)recv).getName() + "::" + nm;
         }
         loadService.addAutoload(nm, new IAutoloadMethod() {
                 public String file() {
                     return file.toString();
                 }
             /**
              * @see org.jruby.runtime.load.IAutoloadMethod#load(Ruby, String)
              */
             public IRubyObject load(Ruby runtime, String name) {
                 loadService.require(file.toString());
                 if(recv instanceof RubyModule) {
                     return ((RubyModule)recv).getConstant(baseName);
                 }
                 return runtime.getObject().getConstant(baseName);
             }
         });
         return recv;
     }
 
     public static IRubyObject method_missing(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = null;
         if("inspect".equals(name) || "to_s".equals(name)) {
             description = recv.anyToString().toString();
         } else {
             description = recv.inspect().toString();
         }
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = runtime.getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         if(null == lastVis) {
             lastVis = Visibility.PUBLIC;
         }
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         // FIXME: Modify sprintf to accept Object[] as well...
         String msg = Sprintf.sprintf(runtime.newString(format), 
                 runtime.newArray(new IRubyObject[] { 
                         runtime.newString(name), 
                         runtime.newString(description),
                         runtime.newString(noClass ? "" : ":"), 
                         runtime.newString(noClass ? "" : recv.getType().getName())
                 })).toString();
         
         throw lastCallType == CallType.VARIABLE ? runtime.newNameError(msg, name) : runtime.newNoMethodError(msg, name);
     }
 
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(recv.getRuntime(), args,1,3);
         String arg = args[0].convertToString().toString();
         Ruby runtime = recv.getRuntime();
 
         if (arg.startsWith("|")) {
             String command = arg.substring(1);
             // exec process, create IO with process
             try {
                 Process p = new ShellLauncher(runtime).run(RubyString.newString(runtime,command));
                 RubyIO io = new RubyIO(runtime, p);
                 
                 if (block.isGiven()) {
                     try {
                         block.yield(recv.getRuntime().getCurrentContext(), io);
                         return runtime.getNil();
                     } finally {
                         io.close();
                     }
                 }
 
                 return io;
             } catch (IOException ioe) {
                 throw runtime.newIOErrorFromException(ioe);
             }
         } 
 
         return ((FileMetaClass) runtime.getClass("File")).open(args, block);
     }
 
     public static IRubyObject gets(IRubyObject recv, IRubyObject[] args) {
         return ((RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<")).gets(args);
     }
 
     public static IRubyObject abort(IRubyObject recv, IRubyObject[] args) {
         if(Arity.checkArgumentCount(recv.getRuntime(), args,0,1) == 1) {
             recv.getRuntime().getGlobalVariables().get("$stderr").callMethod(recv.getRuntime().getCurrentContext(),"puts",args[0]);
         }
         throw new MainExitException(1,true);
     }
 
     public static IRubyObject new_array(IRubyObject recv, IRubyObject object) {
         IRubyObject value = object.convertToType(recv.getRuntime().getArray(), "to_ary", false, true, true);
         
         if (value.isNil()) {
             DynamicMethod method = object.getMetaClass().searchMethod("to_a");
             
             if (method.getImplementationClass() == recv.getRuntime().getKernel()) {
                 return recv.getRuntime().newArray(object);
             }
             
             // Strange that Ruby has custom code here and not convertToTypeWithCheck equivalent.
             value = object.callMethod(recv.getRuntime().getCurrentContext(), "to_a");
             if (value.getMetaClass() != recv.getRuntime().getClass("Array")) {
                 throw recv.getRuntime().newTypeError("`to_a' did not return Array");
                
             }
         }
         
         return value;
     }
     
     public static IRubyObject new_float(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return object;
         }else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
         }else if(object instanceof RubyString){
             if(((RubyString)object).getValue().length() == 0){ // rb_cstr_to_dbl case
                 throw recv.getRuntime().newArgumentError("invalid value for Float(): " + object.inspect());
             }
             return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
             RubyFloat rFloat = object.convertToFloat();
             if(Double.isNaN(rFloat.getDoubleValue())){
                 recv.getRuntime().newArgumentError("invalid value for Float()");
         }
             return rFloat;
     }
     }
     
     public static IRubyObject new_integer(IRubyObject recv, IRubyObject object) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if(object instanceof RubyString) {
             return RubyNumeric.str2inum(recv.getRuntime(),(RubyString)object,0,true);
                     }
         return object.callMethod(context,"to_i");
     }
     
     public static IRubyObject new_string(IRubyObject recv, IRubyObject object) {
         return object.callMethod(recv.getRuntime().getCurrentContext(), "to_s");
     }
     
     
     public static IRubyObject p(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] != null) {
                 defout.callMethod(context, "write", args[i].callMethod(context, "inspect"));
                 defout.callMethod(context, "write", recv.getRuntime().newString("\n"));
             }
         }
         return recv.getRuntime().getNil();
     }
 
     /** rb_f_putc
      */
     public static IRubyObject putc(IRubyObject recv, IRubyObject ch) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         return defout.callMethod(recv.getRuntime().getCurrentContext(), "putc", ch);
     }
 
     public static IRubyObject puts(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         defout.callMethod(context, "puts", args);
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject print(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
 
         defout.callMethod(context, "print", args);
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject printf(IRubyObject recv, IRubyObject[] args) {
         if (args.length != 0) {
             IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
 
             if (!(args[0] instanceof RubyString)) {
                 defout = args[0];
                 args = ArgsUtil.popArray(args);
             }
 
             ThreadContext context = recv.getRuntime().getCurrentContext();
 
             defout.callMethod(context, "write", RubyKernel.sprintf(recv, args));
         }
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject readline(IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(recv, args);
 
         if (line.isNil()) {
             throw recv.getRuntime().newEOFError();
         }
 
         return line;
     }
 
     public static RubyArray readlines(IRubyObject recv, IRubyObject[] args) {
         return ((RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<")).readlines(args);
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(Ruby runtime) {
         IRubyObject line = runtime.getCurrentContext().getLastline();
 
         if (line.isNil()) {
             throw runtime.newTypeError("$_ value need to be String (nil given).");
         } else if (!(line instanceof RubyString)) {
             throw runtime.newTypeError("$_ value need to be String (" + line.getMetaClass().getName() + " given).");
         } else {
             return (RubyString) line;
         }
     }
 
     public static IRubyObject sub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).sub_bang(args, block);
     }
 
     public static IRubyObject sub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.sub_bang(args, block).isNil()) {
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject gsub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).gsub_bang(args, block);
     }
 
     public static IRubyObject gsub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.gsub_bang(args, block).isNil()) {
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject chop_bang(IRubyObject recv) {
         return getLastlineString(recv.getRuntime()).chop_bang();
     }
 
     public static IRubyObject chop(IRubyObject recv) {
         RubyString str = getLastlineString(recv.getRuntime());
 
         if (str.getValue().length() > 0) {
             str = (RubyString) str.dup();
             str.chop_bang();
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject chomp_bang(IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(recv.getRuntime()).chomp_bang(args);
     }
 
     public static IRubyObject chomp(IRubyObject recv, IRubyObject[] args) {
         RubyString str = getLastlineString(recv.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(args).isNil()) {
             return str;
         } 
 
         recv.getRuntime().getCurrentContext().setLastline(dup);
         return dup;
     }
 
     public static IRubyObject split(IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(recv.getRuntime()).split(args);
     }
 
     public static IRubyObject scan(IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(recv.getRuntime()).scan(pattern, block);
     }
 
     public static IRubyObject select(IRubyObject recv, IRubyObject[] args) {
         return IOMetaClass.select_static(recv.getRuntime(), args);
     }
 
     public static IRubyObject sleep(IRubyObject recv, IRubyObject seconds) {
         long milliseconds = (long) (seconds.convertToFloat().getDoubleValue() * 1000);
         long startTime = System.currentTimeMillis();
         
         RubyThread rubyThread = recv.getRuntime().getThreadService().getCurrentContext().getThread();
         // TODO: fix so that when run is called, we won't continue sleeping
         while(milliseconds > 0) {
             try {
                 rubyThread.sleep(milliseconds);
             } catch (InterruptedException iExcptn) {
             }
             milliseconds -= (System.currentTimeMillis()-startTime);
         }
 
 
         return recv.getRuntime().newFixnum(
                 Math.round((System.currentTimeMillis() - startTime) / 1000.0));
     }
 
     // FIXME: Add at_exit and finalizers to exit, then make exit_bang not call those.
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         recv.getRuntime().secure(4);
 
         int status = 1;
         if (args.length > 0) {
             RubyObject argument = (RubyObject)args[0];
             if (argument instanceof RubyFixnum) {
                 status = RubyNumeric.fix2int(argument);
             } else {
                 status = argument.isFalse() ? 1 : 0;
             }
         }
 
         throw recv.getRuntime().newSystemExit(status);
     }
 
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         return exit(recv, args);
     }
 
 
     /** Returns an Array with the names of all global variables.
      *
      */
     public static RubyArray global_variables(IRubyObject recv) {
         RubyArray globalVariables = recv.getRuntime().newArray();
 
         Iterator iter = recv.getRuntime().getGlobalVariables().getNames();
         while (iter.hasNext()) {
             String globalVariableName = (String) iter.next();
 
             globalVariables.append(recv.getRuntime().newString(globalVariableName));
         }
 
         return globalVariables;
     }
 
     /** Returns an Array with the names of all local variables.
      *
      */
     public static RubyArray local_variables(IRubyObject recv) {
         final Ruby runtime = recv.getRuntime();
         RubyArray localVariables = runtime.newArray();
         
         String[] names = runtime.getCurrentContext().getCurrentScope().getAllNamesInScope();
         for (int i = 0; i < names.length; i++) {
             localVariables.append(runtime.newString(names[i]));
         }
 
         return localVariables;
     }
 
     public static RubyBinding binding(IRubyObject recv, Block block) {
         // FIXME: Pass block into binding
         return recv.getRuntime().newBinding();
     }
 
     public static RubyBoolean block_given(IRubyObject recv, Block block) {
         return recv.getRuntime().newBoolean(recv.getRuntime().getCurrentContext().getPreviousFrame().getBlock().isGiven());
     }
 
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         RubyArray newArgs = recv.getRuntime().newArrayNoCopy(args);
         newArgs.shift();
 
         return str.format(newArgs);
     }
 
     public static IRubyObject raise(IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         Arity.checkArgumentCount(recv.getRuntime(), args, 0, 3); 
         Ruby runtime = recv.getRuntime();
 
         if (args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if (lastException.isNil()) {
                 throw new RaiseException(runtime, runtime.getClass("RuntimeError"), "", false);
             } 
             throw new RaiseException((RubyException) lastException);
         }
 
         IRubyObject exception;
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (args.length == 1) {
             if (args[0] instanceof RubyString) {
                 throw new RaiseException((RubyException)runtime.getClass("RuntimeError").newInstance(args, block));
             }
             
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             exception = args[0].callMethod(context, "exception");
         } else {
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             
             exception = args[0].callMethod(context, "exception", args[1]);
         }
         
         if (!exception.isKindOf(runtime.getClass("Exception"))) {
             throw runtime.newTypeError("exception object expected");
         }
         
         if (args.length == 3) {
             ((RubyException) exception).set_backtrace(args[2]);
         }
         
         throw new RaiseException((RubyException) exception);
     }
     
     /**
      * Require.
      * MRI allows to require ever .rb files or ruby extension dll (.so or .dll depending on system).
      * we allow requiring either .rb files or jars.
      * @param recv ruby object used to call require (any object will do and it won't be used anyway).
      * @param name the name of the file to require
      **/
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         if (recv.getRuntime().getLoadService().require(name.toString())) {
             return recv.getRuntime().getTrue();
         }
         return recv.getRuntime().getFalse();
     }
 
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString file = args[0].convertToString();
         recv.getRuntime().getLoadService().load(file.toString());
         return recv.getRuntime().getTrue();
     }
 
     public static IRubyObject eval(IRubyObject recv, IRubyObject[] args, Block block) {
         if (args == null || args.length == 0) {
             throw recv.getRuntime().newArgumentError(args.length, 1);
         }
             
         RubyString src = args[0].convertToString();
         IRubyObject scope = null;
         String file = "(eval)";
         
         if (args.length > 1) {
             if (!args[1].isNil()) {
                 scope = args[1];
             }
             
             if (args.length > 2) {
                 file = args[2].toString();
             }
         }
         // FIXME: line number is not supported yet
         //int line = args.length > 3 ? RubyNumeric.fix2int(args[3]) : 1;
 
         recv.getRuntime().checkSafeString(src);
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (scope == null) {
-            scope = recv.getRuntime().newBinding();
+            scope = RubyBinding.newBindingForEval(recv.getRuntime());
         }
         
         return recv.evalWithBinding(context, src, scope, file);
     }
 
     public static IRubyObject callcc(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         runtime.getWarnings().warn("Kernel#callcc: Continuations are not implemented in JRuby and will not work");
         IRubyObject cc = runtime.getClass("Continuation").callMethod(runtime.getCurrentContext(),"new");
         cc.dataWrapStruct(block);
         return block.yield(runtime.getCurrentContext(),cc);
     }
 
     public static IRubyObject caller(IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw recv.getRuntime().newArgumentError("negative level(" + level + ')');
         }
         
         return recv.getRuntime().getCurrentContext().createBacktrace(level, false);
     }
 
     public static IRubyObject rbCatch(IRubyObject recv, IRubyObject tag, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         try {
             context.pushCatch(tag.asSymbol());
             return block.yield(context, tag);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ThrowJump &&
                 je.getTarget().equals(tag.asSymbol())) {
                     return (IRubyObject) je.getValue();
             }
             throw je;
         } finally {
             context.popCatch();
         }
     }
 
     public static IRubyObject rbThrow(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
 
         String tag = args[0].asSymbol();
         String[] catches = runtime.getCurrentContext().getActiveCatches();
 
         String message = "uncaught throw '" + tag + '\'';
 
         //Ordering of array traversal not important, just intuitive
         for (int i = catches.length - 1 ; i >= 0 ; i--) {
             if (tag.equals(catches[i])) {
                 //Catch active, throw for catch to handle
                 JumpException je = new JumpException(JumpException.JumpType.ThrowJump);
 
                 je.setTarget(tag);
                 je.setValue(args.length > 1 ? args[1] : runtime.getNil());
                 throw je;
             }
         }
 
         //No catch active for this throw
         throw runtime.newNameError(message, tag);
     }
 
     public static IRubyObject trap(IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: We can probably fake some basic signals, but obviously can't do everything. For now, stub.
         return recv.getRuntime().getNil();
     }
     
     public static IRubyObject warn(IRubyObject recv, IRubyObject message) {
         IRubyObject out = recv.getRuntime().getObject().getConstant("STDERR");
         RubyIO io = (RubyIO) out.convertToType("IO", "to_io", true); 
 
         io.puts(new IRubyObject[] { message });
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject set_trace_func(IRubyObject recv, IRubyObject trace_func, Block block) {
         if (trace_func.isNil()) {
             recv.getRuntime().setTraceFunction(null);
         } else if (!(trace_func instanceof RubyProc)) {
             throw recv.getRuntime().newTypeError("trace_func needs to be Proc.");
         } else {
             recv.getRuntime().setTraceFunction((RubyProc) trace_func);
         }
         return trace_func;
     }
 
     public static IRubyObject singleton_method_added(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject singleton_method_removed(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject singleton_method_undefined(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
     
     
     public static RubyProc proc(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(true, block);
     }
 
     public static IRubyObject loop(IRubyObject recv, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         while (true) {
             try {
                 block.yield(context, recv.getRuntime().getNil());
 
                 Thread.yield();
             } catch (JumpException je) {
                 // JRUBY-530, specifically the Kernel#loop case:
                 // Kernel#loop always takes a block.  But what we're looking
                 // for here is breaking an iteration where the block is one 
                 // used inside loop's block, not loop's block itself.  Set the 
                 // appropriate flag on the JumpException if this is the case
                 // (the FCALLNODE case in EvaluationState will deal with it)
                 if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                     if (je.getTarget() != null && je.getTarget() != block) {
                         je.setBreakInKernelLoop(true);
                     }
                 }
                  
                 throw je;
             }
         }
     }
     public static IRubyObject test(IRubyObject recv, IRubyObject[] args) {
         int cmd = (int) args[0].convertToInteger().getLongValue();
         Ruby runtime = recv.getRuntime();
         File pwd = new File(recv.getRuntime().getCurrentDirectory());
         File file1 = new File(pwd, args[1].toString());
         Calendar calendar;
         switch (cmd) {
         //        ?A  | Time    | Last access time for file1
         //        ?b  | boolean | True if file1 is a block device
         //        ?c  | boolean | True if file1 is a character device
         //        ?C  | Time    | Last change time for file1
         //        ?d  | boolean | True if file1 exists and is a directory
         //        ?e  | boolean | True if file1 exists
         //        ?f  | boolean | True if file1 exists and is a regular file
         case 'f':
             return RubyBoolean.newBoolean(runtime, file1.isFile());
         //        ?g  | boolean | True if file1 has the \CF{setgid} bit
         //            |         | set (false under NT)
         //        ?G  | boolean | True if file1 exists and has a group
         //            |         | ownership equal to the caller's group
         //        ?k  | boolean | True if file1 exists and has the sticky bit set
         //        ?l  | boolean | True if file1 exists and is a symbolic link
         //        ?M  | Time    | Last modification time for file1
         case 'M':
             calendar = Calendar.getInstance();
             calendar.setTimeInMillis(file1.lastModified());
             return RubyTime.newTime(runtime, calendar);
         //        ?o  | boolean | True if file1 exists and is owned by
         //            |         | the caller's effective uid
         //        ?O  | boolean | True if file1 exists and is owned by
         //            |         | the caller's real uid
         //        ?p  | boolean | True if file1 exists and is a fifo
         //        ?r  | boolean | True if file1 is readable by the effective
         //            |         | uid/gid of the caller
         //        ?R  | boolean | True if file is readable by the real
         //            |         | uid/gid of the caller
         //        ?s  | int/nil | If file1 has nonzero size, return the size,
         //            |         | otherwise return nil
         //        ?S  | boolean | True if file1 exists and is a socket
         //        ?u  | boolean | True if file1 has the setuid bit set
         //        ?w  | boolean | True if file1 exists and is writable by
         //            |         | the effective uid/gid
         //        ?W  | boolean | True if file1 exists and is writable by
         //            |         | the real uid/gid
         //        ?x  | boolean | True if file1 exists and is executable by
         //            |         | the effective uid/gid
         //        ?X  | boolean | True if file1 exists and is executable by
         //            |         | the real uid/gid
         //        ?z  | boolean | True if file1 exists and has a zero length
         //
         //        Tests that take two files:
         //
         //        ?-  | boolean | True if file1 and file2 are identical
         //        ?=  | boolean | True if the modification times of file1
         //            |         | and file2 are equal
         //        ?<  | boolean | True if the modification time of file1
         //            |         | is prior to that of file2
         //        ?>  | boolean | True if the modification time of file1
         //            |         | is after that of file2
         }
         throw RaiseException.createNativeRaiseException(runtime, 
             new UnsupportedOperationException("test flag " + ((char) cmd) + " is not implemented"));
     }
 
     public static IRubyObject backquote(IRubyObject recv, IRubyObject aString) {
         Ruby runtime = recv.getRuntime();
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         
         int resultCode = new ShellLauncher(runtime).runAndWait(new IRubyObject[] {aString}, output);
         
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         
         return RubyString.newString(recv.getRuntime(), output.toByteArray());
     }
     
     public static RubyInteger srand(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         long oldRandomSeed = runtime.getRandomSeed();
 
         if (args.length > 0) {
             RubyInteger integerSeed = 
                 (RubyInteger) args[0].convertToType("Integer", "to_i", true);
             runtime.setRandomSeed(integerSeed.getLongValue());
         } else {
             // Not sure how well this works, but it works much better than
             // just currentTimeMillis by itself.
             runtime.setRandomSeed(System.currentTimeMillis() ^
               recv.hashCode() ^ runtime.incrementRandomSeedSequence() ^
               runtime.getRandom().nextInt(Math.max(1, Math.abs((int)runtime.getRandomSeed()))));
         }
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
 
     public static RubyNumeric rand(IRubyObject recv, IRubyObject[] args) {
         long ceil;
         if (args.length == 0) {
             ceil = 0;
         } else if (args.length == 1) {
             RubyInteger integerCeil = (RubyInteger) args[0].convertToType("Integer", "to_i", true);
             ceil = integerCeil.getLongValue();
             ceil = Math.abs(ceil);
         } else {
             throw recv.getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         if (ceil == 0) {
             double result = recv.getRuntime().getRandom().nextDouble();
             return RubyFloat.newFloat(recv.getRuntime(), result);
         }
         if(ceil > Integer.MAX_VALUE) {
             return recv.getRuntime().newFixnum(recv.getRuntime().getRandom().nextLong()%ceil);
         } else {
             return recv.getRuntime().newFixnum(recv.getRuntime().getRandom().nextInt((int)ceil));
         }
     }
 
     public static RubyBoolean system(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         int resultCode = new ShellLauncher(runtime).runAndWait(args);
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         return runtime.newBoolean(resultCode == 0);
     }
     
     public static RubyArray to_a(IRubyObject recv) {
         recv.getRuntime().getWarnings().warn("default 'to_a' will be obsolete");
         return recv.getRuntime().newArray(recv);
     }
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 047bd174ab..5059666921 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1394 +1,1397 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.IdUtil;
 import org.jruby.util.Sprintf;
 import org.jruby.util.collections.SinglyLinkedList;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
+import org.jruby.ast.Node;
+import org.jruby.evaluator.CreateJumpTargetVisitor;
 import org.jruby.runtime.ClassIndex;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyObject implements Cloneable, IRubyObject {
     // The class of this object
     private RubyClass metaClass;
 
     // The instance variables of this object.
     protected Map instanceVariables;
 
     // The two properties frozen and taint
     private boolean frozen;
     private boolean taint;
     protected boolean isTrue = true;
 
     public RubyObject(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
 
     public RubyObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         this.metaClass = metaClass;
         this.frozen = false;
         this.taint = false;
 
         // Do not store any immediate objects into objectspace.
         if (useObjectSpace && !isImmediate()) {
             runtime.getObjectSpace().add(this);
         }
 
         // FIXME are there objects who shouldn't be tainted?
         // (mri: OBJSETUP)
         taint |= runtime.getSafeLevel() >= 3;
     }
 
     public void attachToObjectSpace() {
         getRuntime().getObjectSpace().add(this);
     }
     
     /**
      * This is overridden in the other concrete Java builtins to provide a fast way
      * to determine what type they are.
      */
     public int getNativeTypeIndex() {
         return ClassIndex.OBJECT;
     }
     
     /*
      *  Is object immediate (def: Fixnum, Symbol, true, false, nil?).
      */
     public boolean isImmediate() {
     	return false;
     }
 
     /**
      * Create a new meta class.
      *
      * @since Ruby 1.6.7
      */
     public RubyClass makeMetaClass(RubyClass superClass, SinglyLinkedList parentCRef) {
         RubyClass klass = new MetaClass(getRuntime(), superClass, getMetaClass().getAllocator(), parentCRef);
         setMetaClass(klass);
 		
         klass.setInstanceVariable("__attached__", this);
 
         if (this instanceof RubyClass && isSingleton()) { // could be pulled down to RubyClass in future
             klass.setMetaClass(klass);
             klass.setSuperClass(((RubyClass)this).getSuperClass().getRealClass().getMetaClass());
         } else {
             klass.setMetaClass(superClass.getRealClass().getMetaClass());
         }
         
         // use same ClassIndex as metaclass, since we're technically still of that type 
         klass.index = superClass.index;
         return klass;
     }
         
     public boolean isSingleton() {
         return false;
     }
 
     public Class getJavaClass() {
         return IRubyObject.class;
     }
     
     public static void puts(Object obj) {
         System.out.println(obj.toString());
     }
 
     /**
      * This method is just a wrapper around the Ruby "==" method,
      * provided so that RubyObjects can be used as keys in the Java
      * HashMap object underlying RubyHash.
      */
     public boolean equals(Object other) {
         return other == this || other instanceof IRubyObject && callMethod(getRuntime().getCurrentContext(), "==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return callMethod(getRuntime().getCurrentContext(), "to_s").toString();
     }
 
     /** Getter for property ruby.
      * @return Value of property ruby.
      */
     public Ruby getRuntime() {
         return metaClass.getRuntime();
     }
     
     public boolean safeHasInstanceVariables() {
         return instanceVariables != null && instanceVariables.size() > 0;
     }
     
     public Map safeGetInstanceVariables() {
         return instanceVariables == null ? null : getInstanceVariablesSnapshot();
     }
 
     public IRubyObject removeInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().remove(name);
     }
 
     /**
      * Returns an unmodifiable snapshot of the current state of instance variables.
      * This method synchronizes access to avoid deadlocks.
      */
     public Map getInstanceVariablesSnapshot() {
         synchronized(getInstanceVariables()) {
             return Collections.unmodifiableMap(new HashMap(getInstanceVariables()));
         }
     }
 
     public Map getInstanceVariables() {
     	// TODO: double checking may or may not be safe enough here
     	if (instanceVariables == null) {
 	    	synchronized (this) {
 	    		if (instanceVariables == null) {
                             instanceVariables = Collections.synchronizedMap(new HashMap());
 	    		}
 	    	}
     	}
         return instanceVariables;
     }
 
     public void setInstanceVariables(Map instanceVariables) {
         this.instanceVariables = Collections.synchronizedMap(instanceVariables);
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      *
      */
     public RubyClass getMetaClass() {
         return metaClass;
     }
 
     public void setMetaClass(RubyClass metaClass) {
         this.metaClass = metaClass;
     }
 
     /**
      * Gets the frozen.
      * @return Returns a boolean
      */
     public boolean isFrozen() {
         return frozen;
     }
 
     /**
      * Sets the frozen.
      * @param frozen The frozen to set
      */
     public void setFrozen(boolean frozen) {
         this.frozen = frozen;
     }
 
     /** rb_frozen_class_p
     *
     */
    protected void testFrozen(String message) {
        if (isFrozen()) {
            throw getRuntime().newFrozenError(message + getMetaClass().getName());
        }
    }
 
    protected void checkFrozen() {
        testFrozen("can't modify frozen ");
    }
 
     /**
      * Gets the taint.
      * @return Returns a boolean
      */
     public boolean isTaint() {
         return taint;
     }
 
     /**
      * Sets the taint.
      * @param taint The taint to set
      */
     public void setTaint(boolean taint) {
         this.taint = taint;
     }
 
     public boolean isNil() {
         return false;
     }
 
     public final boolean isTrue() {
         return isTrue;
     }
 
     public final boolean isFalse() {
         return !isTrue;
     }
 
     public boolean respondsTo(String name) {
         if(getMetaClass().searchMethod("respond_to?") == getRuntime().getRespondToMethod()) {
             return getMetaClass().isMethodBound(name, false);
         } else {
             return callMethod(getRuntime().getCurrentContext(),"respond_to?",getRuntime().newSymbol(name)).isTrue();
         }
     }
 
     public boolean isKindOf(RubyModule type) {
         return getMetaClass().hasModuleInHierarchy(type);
     }
 
     /** rb_singleton_class
      *
      */    
     public RubyClass getSingletonClass() {
         RubyClass klass;
         
         if (getMetaClass().isSingleton() && getMetaClass().getInstanceVariable("__attached__") == this) {
             klass = getMetaClass();            
         } else {
             klass = makeMetaClass(getMetaClass(), getMetaClass().getCRef());
         }
         
         klass.setTaint(isTaint());
         klass.setFrozen(isFrozen());
         
         return klass;
     }
     
     /** rb_singleton_class_clone
      *
      */
     public RubyClass getSingletonClassClone() {
        RubyClass klass = getMetaClass();
 
        if (!klass.isSingleton()) {
            return klass;
 		}
        
        MetaClass clone = new MetaClass(getRuntime(), klass.getSuperClass(), getMetaClass().getAllocator(), getMetaClass().getCRef());
        clone.setFrozen(klass.isFrozen());
        clone.setTaint(klass.isTaint());
 
        if (this instanceof RubyClass) {
            clone.setMetaClass(clone);
        } else {
            clone.setMetaClass(klass.getSingletonClassClone());
        }
        
        if (klass.safeHasInstanceVariables()) {
            clone.setInstanceVariables(new HashMap(klass.getInstanceVariables()));
        }
 
        klass.cloneMethods(clone);
 
        clone.getMetaClass().setInstanceVariable("__attached__", clone);
 
        return clone;
     }    
 
     /** rb_define_singleton_method
      *
      */
     public void defineSingletonMethod(String name, Callback method) {
         getSingletonClass().defineMethod(name, method);
     }
 
     /** init_copy
      * 
      */
     public static void initCopy(IRubyObject clone, IRubyObject original) {
         assert original != null;
         assert !clone.isFrozen() : "frozen object (" + clone.getMetaClass().getName() + ") allocated";
 
         clone.setInstanceVariables(new HashMap(original.getInstanceVariables()));
         /* FIXME: finalizer should be dupped here */
         clone.callMethod(clone.getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /** OBJ_INFECT
      *
      */
     public IRubyObject infectBy(IRubyObject obj) {
         setTaint(isTaint() || obj.isTaint());
 
         return this;
     }
 
     public IRubyObject callSuper(ThreadContext context, IRubyObject[] args, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = klazz.getSuperClass();
         
         assert superClass != null : "Superclass should always be something for " + klazz.getBaseName();
 
         return callMethod(context, superClass, context.getFrameName(), args, CallType.SUPER, block);
     }    
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, Block block) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, getMetaClass(), name, args, callType, Block.NULL_BLOCK);
     }
     
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType, Block block) {
         return callMethod(context, getMetaClass(), name, args, callType, block);
     }
 
     public IRubyObject callMethod(ThreadContext context,byte switchValue, String name,
                                   IRubyObject arg) {
         return callMethod(context,getMetaClass(),switchValue,name,new IRubyObject[]{arg},CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context,byte switchValue, String name,
                                   IRubyObject[] args) {
         return callMethod(context,getMetaClass(),switchValue,name,args,CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, byte switchValue, String name,
                                   IRubyObject[] args, CallType callType) {
         return callMethod(context,getMetaClass(),switchValue,name,args,callType, Block.NULL_BLOCK);
     }
     
     /**
      * Used by the compiler to ease calling indexed methods, also to handle visibility.
      * NOTE: THIS IS NOT THE SAME AS THE SWITCHVALUE VERSIONS.
      */
     public IRubyObject compilerCallMethodWithIndex(ThreadContext context, byte methodIndex, String name, IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         RubyModule module = getMetaClass();
         
         if (module.index != 0) {
             return callMethod(context, module, getRuntime().getSelectorTable().table[module.index][methodIndex], name, args, callType, block);
         }
         
         return compilerCallMethod(context, name, args, self, callType, block);
     }
     
     /**
      * Used by the compiler to handle visibility
      */
     public IRubyObject compilerCallMethod(ThreadContext context, String name,
             IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         RubyModule rubyclass = getMetaClass();
         method = rubyclass.searchMethod(name);
         
         IRubyObject mmResult = callMethodMissingIfNecessary(context, this, method, name, args, self, callType, block);
         if (mmResult != null) {
             return mmResult;
         }
 
         return method.call(context, this, rubyclass, name, args, false, block);
     }
     
     public static IRubyObject callMethodMissingIfNecessary(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name,
             IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         if (method.isUndefined() ||
             !(name.equals("method_missing") ||
               method.isCallableFrom(self, callType))) {
 
             if (callType == CallType.SUPER) {
                 throw self.getRuntime().newNameError("super: no superclass method '" + name + "'", name);
             }
 
             // store call information so method_missing impl can use it
             context.setLastCallStatus(callType);
 
             if (name.equals("method_missing")) {
                 return RubyKernel.method_missing(self, args, block);
             }
 
 
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             System.arraycopy(args, 0, newArgs, 1, args.length);
             newArgs[0] = RubySymbol.newSymbol(self.getRuntime(), name);
 
             return receiver.callMethod(context, "method_missing", newArgs, block);
         }
         
         // kludgy.
         return null;
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, rubyclass, switchvalue, name, args, callType, Block.NULL_BLOCK);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType, Block block) {
         return callMethod(context, rubyclass, name, args, callType, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name,
             IRubyObject[] args, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         method = rubyclass.searchMethod(name);
         
         IRubyObject mmResult = callMethodMissingIfNecessary(context, this, method, name, args, context.getFrameSelf(), callType, block);
         if (mmResult != null) {
             return mmResult;
         }
 
         return method.call(context, this, rubyclass, name, args, false, block);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return callMethod(context, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, Block block) {
         return callMethod(context, name, IRubyObject.NULL_ARRAY, null, block);
     }
 
     /**
      * rb_funcall
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject arg) {
         return callMethod(context, name, new IRubyObject[] { arg });
     }
 
     public IRubyObject instance_variable_get(IRubyObject var) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isInstanceVariable(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	IRubyObject variable = getInstanceVariable(varName);
 
     	// Pickaxe v2 says no var should show NameError, but ruby only sends back nil..
     	return variable == null ? getRuntime().getNil() : variable;
     }
 
     public IRubyObject getInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().get(name);
     }
 
     public IRubyObject instance_variable_set(IRubyObject var, IRubyObject value) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isInstanceVariable(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	return setInstanceVariable(var.asSymbol(), value);
     }
 
     public IRubyObject setInstanceVariable(String name, IRubyObject value,
             String taintError, String freezeError) {
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError(taintError);
         }
         testFrozen(freezeError);
 
         getInstanceVariables().put(name, value);
 
         return value;
     }
 
     /** rb_iv_set / rb_ivar_set
      *
      */
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         return setInstanceVariable(name, value,
                 "Insecure: can't modify instance variable", "");
     }
 
     public Iterator instanceVariableNames() {
         return getInstanceVariables().keySet().iterator();
     }
 
     public void callInit(IRubyObject[] args, Block block) {
         callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         throw getRuntime().newTypeError(inspect().toString() + " is not a symbol");
     }
 
     public static String trueFalseNil(IRubyObject v) {
         return trueFalseNil(v.getMetaClass().getName());
     }
 
     public static String trueFalseNil(String v) {
         if("TrueClass".equals(v)) {
             return "true";
         } else if("FalseClass".equals(v)) {
             return "false";
         } else if("NilClass".equals(v)) {
             return "nil";
         }
         return v;
     }
 
     public RubyArray convertToArray() {
         return (RubyArray) convertToType(getRuntime().getArray(), "to_ary", true);
     }
 
     public RubyFloat convertToFloat() {
         return (RubyFloat) convertToType(getRuntime().getClass("Float"), "to_f", true);
     }
 
     public RubyInteger convertToInteger() {
         return (RubyInteger) convertToType(getRuntime().getClass("Integer"), "to_int", true);
     }
 
     public RubyString convertToString() {
         return (RubyString) convertToType(getRuntime().getString(), "to_str", true);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
      */
     public IRubyObject convertToTypeWithCheck(String targetType, String convertMethod) {
         return convertToType(getRuntime().getClass(targetType), convertMethod, false, true, false);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
      */
     public IRubyObject convertToTypeWithCheck(RubyClass targetType, String convertMethod) {
         return convertToType(targetType, convertMethod, false, true, false);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
     public IRubyObject convertToType(String targetType, String convertMethod, boolean raise) {
         return convertToType(getRuntime().getClass(targetType), convertMethod, raise, false, false);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
     public IRubyObject convertToType(RubyClass targetType, String convertMethod, boolean raise) {
         return convertToType(targetType, convertMethod, raise, false, false);
     }
     
     public IRubyObject convertToType(RubyClass targetType, String convertMethod, boolean raiseOnMissingMethod, boolean raiseOnWrongTypeResult, boolean allowNilThrough) {
         if (isKindOf(targetType)) {
             return this;
         }
         
         if (!respondsTo(convertMethod)) {
             if (raiseOnMissingMethod) {
                 throw getRuntime().newTypeError(
                     "can't convert " + trueFalseNil(getMetaClass().getName()) + " into " + trueFalseNil(targetType));
             } 
 
             return getRuntime().getNil();
         }
         
         IRubyObject value = callMethod(getRuntime().getCurrentContext(), convertMethod);
         
         if (allowNilThrough && value.isNil()) {
             return value;
         }
         
         if (raiseOnWrongTypeResult && !value.isKindOf(targetType)) {
             throw getRuntime().newTypeError(getMetaClass().getName() + "#" + convertMethod +
                     " should return " + targetType);
         }
         
         return value;
     }
 
     /** rb_obj_as_string
      */
     public RubyString asString() {
         if (this instanceof RubyString) return (RubyString) this;
         
         IRubyObject str = this.callMethod(getRuntime().getCurrentContext(), "to_s");
         
         if (!(str instanceof RubyString)) str = anyToString();
 
         return (RubyString) str;
     }
     
     /** rb_check_string_type
      *
      */
     public IRubyObject checkStringType() {
         IRubyObject str = convertToTypeWithCheck("String","to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = getRuntime().newString("");
         }
         return str;
     }
 
     /** rb_check_array_type
     *
     */    
     public IRubyObject checkArrayType() {
         return convertToTypeWithCheck("Array","to_ary");
     }
 
     /** specific_eval
      *
      */
     public IRubyObject specificEval(RubyModule mod, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError(args.length, 0);
 
             return yieldUnder(mod, block);
         }
         ThreadContext tc = getRuntime().getCurrentContext();
 
         if (args.length == 0) {
 		    throw getRuntime().newArgumentError("block not supplied");
 		} else if (args.length > 3) {
 		    String lastFuncName = tc.getFrameName();
 		    throw getRuntime().newArgumentError(
 		        "wrong # of arguments: " + lastFuncName + "(src) or " + lastFuncName + "{..}");
 		}
 		/*
 		if (ruby.getSecurityLevel() >= 4) {
 			Check_Type(argv[0], T_STRING);
 		} else {
 			Check_SafeStr(argv[0]);
 		}
 		*/
         
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         args[0].convertToString();
         
 		IRubyObject file = args.length > 1 ? args[1] : getRuntime().newString("(eval)");
 		IRubyObject line = args.length > 2 ? args[2] : RubyFixnum.one(getRuntime());
 
 		Visibility savedVisibility = tc.getCurrentVisibility();
         tc.setCurrentVisibility(Visibility.PUBLIC);
 		try {
 		    return evalUnder(mod, args[0], file, line);
 		} finally {
             tc.setCurrentVisibility(savedVisibility);
 		}
     }
 
     public IRubyObject evalUnder(RubyModule under, IRubyObject src, IRubyObject file, IRubyObject line) {
         /*
         if (ruby_safe_level >= 4) {
         	Check_Type(src, T_STRING);
         } else {
         	Check_SafeStr(src);
         	}
         */
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 IRubyObject source = args[1];
                 IRubyObject filename = args[2];
                 // FIXME: lineNumber is not supported
                 //IRubyObject lineNumber = args[3];
 
                 return args[0].evalSimple(source.getRuntime().getCurrentContext(),
                                   source, ((RubyString) filename).toString());
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this, src, file, line }, Block.NULL_BLOCK);
     }
 
     private IRubyObject yieldUnder(RubyModule under, Block block) {
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 ThreadContext context = getRuntime().getCurrentContext();
 
                 Visibility savedVisibility = block.getVisibility();
 
                 block.setVisibility(Visibility.PUBLIC);
                 try {
                     IRubyObject valueInYield = args[0];
                     IRubyObject selfInYield = args[0];
                     return block.yield(context, valueInYield, selfInYield, context.getRubyClass(), false);
                     //TODO: Should next and return also catch here?
                 } catch (JumpException je) {
                 	if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 		return (IRubyObject) je.getValue();
                 	} 
 
                     throw je;
                 } finally {
                     block.setVisibility(savedVisibility);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this }, block);
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalWithBinding(org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, IRubyObject scope, String file) {
         // both of these are ensured by the (very few) callers
         assert !scope.isNil();
         assert file != null;
 
         ThreadContext threadContext = getRuntime().getCurrentContext();
 
         ISourcePosition savedPosition = threadContext.getPosition();
         IRubyObject result = getRuntime().getNil();
 
         IRubyObject newSelf = null;
 
         if (!(scope instanceof RubyBinding)) {
             if (scope instanceof RubyProc) {
                 scope = ((RubyProc) scope).binding();
             } else {
                 // bomb out, it's not a binding or a proc
                 throw getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
             }
         }
 
         Block blockOfBinding = ((RubyBinding)scope).getBlock();
         try {
             // Binding provided for scope, use it
             threadContext.preEvalWithBinding(blockOfBinding);
             newSelf = threadContext.getFrameSelf();
+            Node node = getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope());
+            CreateJumpTargetVisitor.setJumpTarget(context.getFrameJumpTarget(), node);
 
-            result = EvaluationState.eval(getRuntime(), threadContext, getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope()), newSelf, blockOfBinding);
+            result = EvaluationState.eval(getRuntime(), threadContext, node, newSelf, blockOfBinding);
         } catch (JumpException je) {
-            if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
-                throw getRuntime().newLocalJumpError("unexpected return");
-            } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
+            if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             threadContext.postEvalWithBinding();
 
             // restore position
             threadContext.setPosition(savedPosition);
         }
         return result;
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalSimple(org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalSimple(ThreadContext context, IRubyObject src, String file) {
         // this is ensured by the callers
         assert file != null;
 
         ISourcePosition savedPosition = context.getPosition();
 
         // no binding, just eval in "current" frame (caller's frame)
         try {
-            return EvaluationState.eval(getRuntime(), context, getRuntime().parse(src.toString(), file, context.getCurrentScope()), this, Block.NULL_BLOCK);
+            Node node = getRuntime().parse(src.toString(), file, context.getCurrentScope());
+            CreateJumpTargetVisitor.setJumpTarget(context.getFrameJumpTarget(), node);
+            
+            return EvaluationState.eval(getRuntime(), context, node, this, Block.NULL_BLOCK);
         } catch (JumpException je) {
-            if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
-                throw getRuntime().newLocalJumpError("unexpected return");
-            } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
+            if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             // restore position
             context.setPosition(savedPosition);
         }
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      */
     public IRubyObject obj_equal(IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
 //        if (isNil()) {
 //            return getRuntime().newBoolean(obj.isNil());
 //        }
 //        return getRuntime().newBoolean(this == obj);
     }
 
 	public IRubyObject same(IRubyObject other) {
 		return this == other ? getRuntime().getTrue() : getRuntime().getFalse();
 	}
 
     
     /** rb_obj_init_copy
      * 
      */
 	public IRubyObject initialize_copy(IRubyObject original) {
 	    if (this == original) return this;
 	    
 	    checkFrozen();
         
         if (getMetaClass().getRealClass() != original.getMetaClass().getRealClass()) {
 	            throw getRuntime().newTypeError("initialize_copy should take same class object");
 	    }
 
 	    return this;
 	}
 
     /**
      * respond_to?( aSymbol, includePriv=false ) -> true or false
      *
      * Returns true if this object responds to the given method. Private
      * methods are included in the search only if the optional second
      * parameter evaluates to true.
      *
      * @return true if this responds to the given method
      */
     public RubyBoolean respond_to(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
 
         String name = args[0].asSymbol();
         boolean includePrivate = args.length > 1 ? args[1].isTrue() : false;
 
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate));
     }
 
     /** Return the internal id of an object.
      *
      * <i>CRuby function: rb_obj_id</i>
      *
      */
     public synchronized RubyFixnum id() {
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
 
     public synchronized RubyFixnum id_deprecated() {
         getRuntime().getWarnings().warn("Object#id will be deprecated; use Object#object_id");
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
     
     public RubyFixnum hash() {
         return getRuntime().newFixnum(System.identityHashCode(this));
     }
 
     public int hashCode() {
         IRubyObject hashValue = callMethod(getRuntime().getCurrentContext(), "hash");
         
         if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue); 
         
         return System.identityHashCode(this);
     }
 
     /** rb_obj_type
      *
      */
     public RubyClass type() {
         return getMetaClass().getRealClass();
     }
 
     public RubyClass type_deprecated() {
         getRuntime().getWarnings().warn("Object#type is deprecated; use Object#class");
         return type();
     }
 
     /** rb_obj_clone
      *  should be overriden only by: Proc, Method, UnboundedMethod, Binding
      */
     public IRubyObject rbClone() {
         if (isImmediate()) { // rb_special_const_p(obj) equivalent
             throw getRuntime().newTypeError("can't clone " + getMetaClass().getName());
         }
         
         IRubyObject clone = doClone();
         clone.setMetaClass(getSingletonClassClone());
         clone.setTaint(isTaint());
         initCopy(clone, this);
         clone.setFrozen(isFrozen());
         return clone;
     }
 
     // Hack: allow RubyModule and RubyClass to override the allocation and return the the correct Java instance
     // Cloning a class object doesn't work otherwise and I don't really understand why --sma
     protected IRubyObject doClone() {
         RubyClass realClass = getMetaClass().getRealClass();
     	return realClass.getAllocator().allocate(getRuntime(), realClass);
     }
 
     public IRubyObject display(IRubyObject[] args) {
         IRubyObject port = args.length == 0
             ? getRuntime().getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(getRuntime().getCurrentContext(), "write", this);
 
         return getRuntime().getNil();
     }
 
     /** rb_obj_dup
      *  should be overriden only by: Proc
      */
     public IRubyObject dup() {
         if (isImmediate()) {
             throw getRuntime().newTypeError("can't dup " + getMetaClass().getName());
         }        
         
         IRubyObject dup = doClone();    
 
         dup.setMetaClass(type());
         dup.setFrozen(false);
         dup.setTaint(isTaint());
         
         initCopy(dup, this);
 
         return dup;
     }
 
     /** rb_obj_tainted
      *
      */
     public RubyBoolean tainted() {
         return getRuntime().newBoolean(isTaint());
     }
 
     /** rb_obj_taint
      *
      */
     public IRubyObject taint() {
         getRuntime().secure(4);
         if (!isTaint()) {
         	testFrozen("object");
             setTaint(true);
         }
         return this;
     }
 
     /** rb_obj_untaint
      *
      */
     public IRubyObject untaint() {
         getRuntime().secure(3);
         if (isTaint()) {
         	testFrozen("object");
             setTaint(false);
         }
         return this;
     }
 
     /** Freeze an object.
      *
      * rb_obj_freeze
      *
      */
     public IRubyObject freeze() {
         if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't freeze object");
         }
         setFrozen(true);
         return this;
     }
 
     /** rb_obj_frozen_p
      *
      */
     public RubyBoolean frozen() {
         return getRuntime().newBoolean(isFrozen());
     }
 
     /** rb_obj_inspect
      *
      */
     public IRubyObject inspect() {
         if ((!isImmediate()) &&
                 // TYPE(obj) == T_OBJECT
                 !(this instanceof RubyClass) &&
                 this != getRuntime().getObject() &&
                 this != getRuntime().getClass("Module") &&
                 !(this instanceof RubyModule) &&
                 safeHasInstanceVariables()) {
 
             StringBuffer part = new StringBuffer();
             String cname = getMetaClass().getRealClass().getName();
             part.append("#<").append(cname).append(":0x");
             part.append(Integer.toHexString(System.identityHashCode(this)));
             if(!getRuntime().registerInspecting(this)) {
                 /* 6:tags 16:addr 1:eos */
                 part.append(" ...>");
                 return getRuntime().newString(part.toString());
             }
             try {
                 String sep = "";
                 Map iVars = getInstanceVariablesSnapshot();
                 for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
                     String name = (String) iter.next();
                     if(IdUtil.isInstanceVariable(name)) {
                         part.append(sep);
                         part.append(" ");
                         part.append(name);
                         part.append("=");
                         part.append(((IRubyObject)(iVars.get(name))).callMethod(getRuntime().getCurrentContext(), "inspect"));
                         sep = ",";
                     }
                 }
                 part.append(">");
                 return getRuntime().newString(part.toString());
             } finally {
                 getRuntime().unregisterInspecting(this);
             }
         }
         return callMethod(getRuntime().getCurrentContext(), "to_s");
     }
 
     /** rb_obj_is_instance_of
      *
      */
     public RubyBoolean instance_of(IRubyObject type) {
         return getRuntime().newBoolean(type() == type);
     }
 
 
     public RubyArray instance_variables() {
         ArrayList names = new ArrayList();
         for(Iterator iter = getInstanceVariablesSnapshot().keySet().iterator();iter.hasNext();) {
             String name = (String) iter.next();
 
             // Do not include constants which also get stored in instance var list in classes.
             if (IdUtil.isInstanceVariable(name)) {
                 names.add(getRuntime().newString(name));
             }
         }
         return getRuntime().newArray(names);
     }
 
     /** rb_obj_is_kind_of
      *
      */
     public RubyBoolean kind_of(IRubyObject type) {
         // TODO: Generalize this type-checking code into IRubyObject helper.
         if (!type.isKindOf(getRuntime().getClass("Module"))) {
             // TODO: newTypeError does not offer enough for ruby error string...
             throw getRuntime().newTypeError(type, getRuntime().getClass("Module"));
         }
 
         return getRuntime().newBoolean(isKindOf((RubyModule)type));
     }
 
     /** rb_obj_methods
      *
      */
     public IRubyObject methods(IRubyObject[] args) {
     	Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
     	if (args.length == 0) {
     		args = new IRubyObject[] { getRuntime().getTrue() };
     	}
 
         return getMetaClass().instance_methods(args);
     }
 
 	public IRubyObject public_methods(IRubyObject[] args) {
         return getMetaClass().public_instance_methods(args);
 	}
 
     /** rb_obj_protected_methods
      *
      */
     public IRubyObject protected_methods() {
         return getMetaClass().protected_instance_methods(new IRubyObject[] { getRuntime().getTrue()});
     }
 
     /** rb_obj_private_methods
      *
      */
     public IRubyObject private_methods() {
         return getMetaClass().private_instance_methods(new IRubyObject[] { getRuntime().getTrue()});
     }
 
     /** rb_obj_singleton_methods
      *
      */
     // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
     public RubyArray singleton_methods(IRubyObject[] args) {
         boolean all = true;
         if(Arity.checkArgumentCount(getRuntime(), args,0,1) == 1) {
             all = args[0].isTrue();
         }
 
         RubyArray result = getRuntime().newArray();
 
         for (RubyClass type = getMetaClass(); type != null && ((type instanceof MetaClass) || (all && type.isIncluded()));
              type = type.getSuperClass()) {
         	for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext(); ) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
 
                 // We do not want to capture cached methods
                 if (method.getImplementationClass() != type && !(all && type.isIncluded())) {
                 	continue;
                 }
 
                 RubyString methodName = getRuntime().newString((String) entry.getKey());
                 if (method.getVisibility().isPublic() && ! result.includes(methodName)) {
                     result.append(methodName);
                 }
             }
         }
 
         return result;
     }
 
     public IRubyObject method(IRubyObject symbol) {
         return getMetaClass().newMethod(this, symbol.asSymbol(), true);
     }
 
     public IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
 
     public IRubyObject to_s() {
     	return anyToString();
     }
 
     public IRubyObject instance_eval(IRubyObject[] args, Block block) {
         return specificEval(getSingletonClass(), args, block);
     }
 
     public IRubyObject extend(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, -1);
 
         // Make sure all arguments are modules before calling the callbacks
         RubyClass module = getRuntime().getClass("Module");
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isKindOf(module)) {
                 throw getRuntime().newTypeError(args[i], module);
             }
         }
 
         for (int i = 0; i < args.length; i++) {
             args[i].callMethod(getRuntime().getCurrentContext(), "extend_object", this);
             args[i].callMethod(getRuntime().getCurrentContext(), "extended", this);
         }
         return this;
     }
 
     public IRubyObject inherited(IRubyObject arg, Block block) {
     	return getRuntime().getNil();
     }
     public IRubyObject initialize(IRubyObject[] args, Block block) {
     	return getRuntime().getNil();
     }
 
     public IRubyObject method_missing(IRubyObject[] args, Block block) {
         if (args.length == 0) {
             throw getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = null;
         if("inspect".equals(name) || "to_s".equals(name)) {
             description = anyToString().toString();
         } else {
             description = inspect().toString();
         }
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         Ruby runtime = getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         if(null == lastVis) {
             lastVis = Visibility.PUBLIC;
         }
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = Sprintf.sprintf(runtime.newString(format), 
                 runtime.newArray(new IRubyObject[] { 
                         runtime.newString(name), runtime.newString(description),
                         runtime.newString(noClass ? "" : ":"), 
                         runtime.newString(noClass ? "" : getType().getName())
                 })).toString();
 
         if (lastCallType == CallType.VARIABLE) {
         	throw getRuntime().newNameError(msg, name);
         }
         throw getRuntime().newNoMethodError(msg, name);
     }
 
     /**
      * send( aSymbol  [, args  ]*   ) -> anObject
      *
      * Invokes the method identified by aSymbol, passing it any arguments
      * specified. You can use __send__ if the name send clashes with an
      * existing method in this object.
      *
      * <pre>
      * class Klass
      *   def hello(*args)
      *     "Hello " + args.join(' ')
      *   end
      * end
      *
      * k = Klass.new
      * k.send :hello, "gentle", "readers"
      * </pre>
      *
      * @return the result of invoking the method identified by aSymbol.
      */
     public IRubyObject send(IRubyObject[] args, Block block) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asSymbol();
 
         IRubyObject[] newArgs = new IRubyObject[args.length - 1];
         System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
         return callMethod(getRuntime().getCurrentContext(), name, newArgs, CallType.FUNCTIONAL, block);
     }
     
     public IRubyObject nil_p() {
     	return getRuntime().getFalse();
     }
     
     public IRubyObject match(IRubyObject arg) {
     	return getRuntime().getFalse();
     }
     
    public IRubyObject remove_instance_variable(IRubyObject name, Block block) {
        String id = name.asSymbol();
 
        if (!IdUtil.isInstanceVariable(id)) {
            throw getRuntime().newNameError("wrong instance variable name " + id, id);
        }
        if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
            throw getRuntime().newSecurityError("Insecure: can't remove instance variable");
        }
        testFrozen("class/module");
 
        IRubyObject variable = removeInstanceVariable(id); 
        if (variable != null) {
            return variable;
        }
 
        throw getRuntime().newNameError("instance variable " + id + " not defined", id);
    }
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#scanArgs()
      */
     public IRubyObject[] scanArgs(IRubyObject[] args, int required, int optional) {
         int total = required+optional;
         int real = Arity.checkArgumentCount(getRuntime(), args,required,total);
         IRubyObject[] narr = new IRubyObject[total];
         System.arraycopy(args,0,narr,0,real);
         for(int i=real; i<total; i++) {
             narr[i] = getRuntime().getNil();
         }
         return narr;
     }
 
     private transient Object dataStruct;
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataWrapStruct()
      */
     public synchronized void dataWrapStruct(Object obj) {
         this.dataStruct = obj;
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataGetStruct()
      */
     public synchronized Object dataGetStruct() {
         return dataStruct;
     }
 
     /** rb_equal
      * 
      */
     public IRubyObject equal(IRubyObject other) {
         if(this == other || callMethod(getRuntime().getCurrentContext(), "==",other).isTrue()){
             return getRuntime().getTrue();
         }
  
         return getRuntime().getFalse();
     }
     
     public final IRubyObject equalInternal(final ThreadContext context, final IRubyObject other){
         if (this == other) return getRuntime().getTrue();
         return callMethod(context, "==", other);
     }
 }
diff --git a/src/org/jruby/evaluator/CreateJumpTargetVisitor.java b/src/org/jruby/evaluator/CreateJumpTargetVisitor.java
index 7249fc30b0..44a4a7cc9c 100644
--- a/src/org/jruby/evaluator/CreateJumpTargetVisitor.java
+++ b/src/org/jruby/evaluator/CreateJumpTargetVisitor.java
@@ -1,265 +1,266 @@
 /*******************************************************************************
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
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Charles Oliver Nutter <headius@headius.com>
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
 
 import java.util.Iterator;
 import org.jruby.ast.ArgsCatNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BinaryOperatorNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.CaseNode;
 import org.jruby.ast.ClassNode;
 import org.jruby.ast.ClassVarAsgnNode;
 import org.jruby.ast.ClassVarDeclNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.DAsgnNode;
 import org.jruby.ast.DefinedNode;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EnsureNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FCallNode;
 import org.jruby.ast.FlipNode;
 import org.jruby.ast.ForNode;
 import org.jruby.ast.GlobalAsgnNode;
 import org.jruby.ast.IfNode;
 import org.jruby.ast.InstAsgnNode;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.LocalAsgnNode;
 import org.jruby.ast.Match2Node;
 import org.jruby.ast.Match3Node;
 import org.jruby.ast.MatchNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NewlineNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeTypes;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OpElementAsgnNode;
 import org.jruby.ast.OptNNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.SClassNode;
 import org.jruby.ast.SValueNode;
 import org.jruby.ast.SplatNode;
 import org.jruby.ast.ToAryNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.WhenNode;
 import org.jruby.ast.WhileNode;
 
 public class CreateJumpTargetVisitor {
     public static void setJumpTarget(Object target, Node node) {
         bigloop: do {
             if (node == null) return;
 
             switch (node.nodeId) {
                 case NodeTypes.ANDNODE:
                 case NodeTypes.OPASGNANDNODE:
                 case NodeTypes.OPASGNORNODE:
                 case NodeTypes.ORNODE:
                     setJumpTarget(target, ((BinaryOperatorNode)node).getFirstNode());
                     node = ((BinaryOperatorNode)node).getSecondNode();
                     continue bigloop;
                 case NodeTypes.ARGSCATNODE:
                     setJumpTarget(target, ((ArgsCatNode)node).getFirstNode());
                     node = ((ArgsCatNode)node).getSecondNode();
                     continue bigloop;
                 case NodeTypes.ARRAYNODE:
                 case NodeTypes.BLOCKNODE:
                 case NodeTypes.DREGEXPNODE:
                 case NodeTypes.DSTRNODE:
                 case NodeTypes.DSYMBOLNODE:
                 case NodeTypes.DXSTRNODE:
                 case NodeTypes.HASHNODE:
+                case NodeTypes.ROOTNODE:
                     for (Iterator iter = node.childNodes().iterator(); iter.hasNext();) {
                         setJumpTarget(target, (Node)iter.next());
                     }
                     return;
                 case NodeTypes.BEGINNODE:
                     node = ((BeginNode)node).getBodyNode();
                     continue bigloop;
                 case NodeTypes.BLOCKPASSNODE:
                     setJumpTarget(target, ((BlockPassNode)node).getArgsNode());
                     setJumpTarget(target, ((BlockPassNode)node).getBodyNode());
                     return;
                 case NodeTypes.BREAKNODE:
                     node = ((BreakNode)node).getValueNode();
                     continue bigloop;
                 case NodeTypes.CONSTDECLNODE:
                     node = ((ConstDeclNode)node).getValueNode();
                     continue bigloop;
                 case NodeTypes.CLASSNODE:
                     setJumpTarget(target, ((ClassNode)node).getSuperNode());
                     node = ((ClassNode)node).getBodyNode();
                     continue bigloop;
                 case NodeTypes.CLASSVARASGNNODE:
                     node = ((ClassVarAsgnNode)node).getValueNode();
                     continue bigloop;
                 case NodeTypes.CLASSVARDECLNODE:
                     node = ((ClassVarDeclNode)node).getValueNode();
                     continue bigloop;
                 case NodeTypes.CALLNODE:
                     setJumpTarget(target, ((CallNode)node).getIterNode());
                     setJumpTarget(target, ((CallNode)node).getReceiverNode());
                     setJumpTarget(target, ((CallNode)node).getArgsNode());
                     return;
                 case NodeTypes.CASENODE:
                     setJumpTarget(target, ((CaseNode)node).getCaseNode());
                     node = ((CaseNode)node).getFirstWhenNode();
                     continue bigloop;
                 case NodeTypes.COLON2NODE:
                     node = ((Colon2Node)node).getLeftNode();
                     continue bigloop;
                 case NodeTypes.DASGNNODE:
                     node = ((DAsgnNode)node).getValueNode();
                     continue bigloop;
                 case NodeTypes.DEFINEDNODE:
                     node = ((DefinedNode)node).getExpressionNode();
                     continue bigloop;
                 case NodeTypes.DOTNODE:
                     setJumpTarget(target, ((DotNode)node).getBeginNode());
                     node = ((DotNode)node).getEndNode();
                     continue bigloop;
                 case NodeTypes.ENSURENODE:
                     setJumpTarget(target, ((EnsureNode)node).getBodyNode());
                     node = ((EnsureNode)node).getEnsureNode();
                     continue bigloop;
                 case NodeTypes.EVSTRNODE:
                     node = ((EvStrNode)node).getBody();
                     continue bigloop;
                 case NodeTypes.FCALLNODE:
                     setJumpTarget(target, ((FCallNode)node).getIterNode());
                     setJumpTarget(target, ((FCallNode)node).getArgsNode());
                     return;
                 case NodeTypes.FLIPNODE:
                     setJumpTarget(target, ((FlipNode)node).getBeginNode());
                     node = ((FlipNode)node).getEndNode();
                     continue bigloop;
                 case NodeTypes.FORNODE:
                     setJumpTarget(target, ((ForNode)node).getBodyNode());
                     setJumpTarget(target, ((ForNode)node).getIterNode());
                     node = ((ForNode)node).getVarNode();
                     continue bigloop;
                 case NodeTypes.GLOBALASGNNODE:
                     node = ((GlobalAsgnNode)node).getValueNode();
                     continue bigloop;
                 case NodeTypes.INSTASGNNODE:
                     node = ((InstAsgnNode)node).getValueNode();
                     continue bigloop;
                 case NodeTypes.IFNODE:
                     setJumpTarget(target, ((IfNode)node).getCondition());
                     setJumpTarget(target, ((IfNode)node).getThenBody());
                     node = ((IfNode)node).getElseBody();
                     continue bigloop;
                 case NodeTypes.ITERNODE:
                     setJumpTarget(target, ((IterNode)node).getBodyNode());
                     node = ((IterNode)node).getVarNode();
                     continue bigloop;
                 case NodeTypes.LOCALASGNNODE:
                     node = ((LocalAsgnNode)node).getValueNode();
                     continue bigloop;
                 case NodeTypes.MULTIPLEASGNNODE:
                     node = ((MultipleAsgnNode)node).getValueNode();
                     continue bigloop;
                 case NodeTypes.MATCH2NODE:
                     setJumpTarget(target, ((Match2Node)node).getReceiverNode());
                     node = ((Match2Node)node).getValueNode();
                     continue bigloop;
                 case NodeTypes.MATCH3NODE:
                     setJumpTarget(target, ((Match3Node)node).getReceiverNode());
                     node = ((Match3Node)node).getValueNode();
                     continue bigloop;
                 case NodeTypes.MATCHNODE:
                     node = ((MatchNode)node).getRegexpNode();
                     continue bigloop;
                 case NodeTypes.NEWLINENODE:
                     node = ((NewlineNode)node).getNextNode();
                     continue bigloop;
                 case NodeTypes.NOTNODE:
                     node = ((NotNode)node).getConditionNode();
                     continue bigloop;
                 case NodeTypes.OPELEMENTASGNNODE:
                     setJumpTarget(target, ((OpElementAsgnNode)node).getReceiverNode());
                     node = ((OpElementAsgnNode)node).getValueNode();
                     continue bigloop;
                 case NodeTypes.OPASGNNODE:
                     setJumpTarget(target, ((OpAsgnNode)node).getReceiverNode());
                     node = ((OpAsgnNode)node).getValueNode();
                     continue bigloop;
                 case NodeTypes.OPTNNODE:
                     node = ((OptNNode)node).getBodyNode();
                     continue bigloop;
                 case NodeTypes.RESCUEBODYNODE:
                     setJumpTarget(target, ((RescueBodyNode)node).getBodyNode());
                     setJumpTarget(target, ((RescueBodyNode)node).getExceptionNodes());
                     node = ((RescueBodyNode)node).getOptRescueNode();
                     continue bigloop;
                 case NodeTypes.RESCUENODE:
                     setJumpTarget(target, ((RescueNode)node).getBodyNode());
                     setJumpTarget(target, ((RescueNode)node).getElseNode());
                     node = ((RescueNode)node).getRescueNode();
                     continue bigloop;
                 case NodeTypes.RETURNNODE:
                     ((ReturnNode)node).setTarget(target);
                     return;
                 case NodeTypes.SCLASSNODE:
                     setJumpTarget(target, ((SClassNode)node).getReceiverNode());
                     node = ((SClassNode)node).getBodyNode();
                     continue bigloop;
                 case NodeTypes.SPLATNODE:
                     node = ((SplatNode)node).getValue();
                     continue bigloop;
                 case NodeTypes.SVALUENODE:
                     node = ((SValueNode)node).getValue();
                     continue bigloop;
                 case NodeTypes.TOARYNODE:
                     node = ((ToAryNode)node).getValue();
                     continue bigloop;
                 case NodeTypes.UNTILNODE:
                     setJumpTarget(target, ((UntilNode)node).getConditionNode());
                     node = ((UntilNode)node).getBodyNode();
                     continue bigloop;
                 case NodeTypes.WHENNODE:
                     setJumpTarget(target, ((WhenNode)node).getBodyNode());
                     setJumpTarget(target, ((WhenNode)node).getExpressionNodes());
                     node = ((WhenNode)node).getNextCase();
                     continue bigloop;
                 case NodeTypes.WHILENODE:
                     setJumpTarget(target, ((WhileNode)node).getConditionNode());
                     node = ((WhileNode)node).getBodyNode();
                     continue bigloop;
                 default:
                     return;
             }
         } while (node != null);
     }
 }
\ No newline at end of file
diff --git a/src/org/jruby/evaluator/EvaluationState.java b/src/org/jruby/evaluator/EvaluationState.java
index 7e59cf3c7f..6f1fe67f07 100644
--- a/src/org/jruby/evaluator/EvaluationState.java
+++ b/src/org/jruby/evaluator/EvaluationState.java
@@ -591,1608 +591,1608 @@ public class EvaluationState {
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
                 for (int i = 0; i < arrayNode.size(); i++) {
                     Node tag = arrayNode.get(i);
 
                     context.setPosition(tag.getPosition());
                     if (isTrace(runtime)) {
                         callTraceFunction(runtime, context, "line", self);
                     }
 
                     // Ruby grammar has nested whens in a case body because of
                     // productions case_body and when_args.
                     if (tag instanceof WhenNode) {
                         RubyArray expressions = (RubyArray) evalInternal(runtime,context, ((WhenNode) tag)
                                         .getExpressionNodes(), self, aBlock);
 
                         for (int j = 0,k = expressions.getLength(); j < k; j++) {
                             IRubyObject condition = expressions.eltInternal(j);
 
                             if ((expression != null && condition.callMethod(context, "===", expression)
                                     .isTrue())
                                     || (expression == null && condition.isTrue())) {
                                 node = ((WhenNode) firstWhenNode).getBodyNode();
                                 return evalInternal(runtime, context, node, self, aBlock);
                             }
                         }
                         continue;
                     }
 
                     result = evalInternal(runtime,context, tag, self, aBlock);
 
                     if ((expression != null && result.callMethod(context, "===", expression).isTrue())
                             || (expression == null && result.isTrue())) {
                         node = whenNode.getBodyNode();
                         return evalInternal(runtime, context, node, self, aBlock);
                     }
                 }
             } else {
                 result = evalInternal(runtime,context, whenNode.getExpressionNodes(), self, aBlock);
 
                 if ((expression != null && result.callMethod(context, "===", expression).isTrue())
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
         Node superNode = iVisited.getSuperNode();
         RubyClass superClass = null;
         if(superNode != null) {
             IRubyObject _super = evalInternal(runtime,context, superNode, self, aBlock);
             if(!(_super instanceof RubyClass)) {
                 throw runtime.newTypeError("superclass must be a Class (" + RubyObject.trueFalseNil(_super) + ") given");
             }
             superClass = superNode == null ? null : (RubyClass)_super;
         }
         Node classNameNode = iVisited.getCPath();
         String name = ((INameNode) classNameNode).getName();
         RubyModule enclosingClass = getEnclosingModule(runtime, context, classNameNode, self, aBlock);
         RubyClass rubyClass = enclosingClass.defineOrGetClassUnder(name, superClass);
    
         if (context.getWrapper() != null) {
             context.getWrapper().extend_object(rubyClass);
             rubyClass.includeModule(context.getWrapper());
         }
         return evalClassDefinitionBody(runtime, context, iVisited.getScope(), iVisited.getBodyNode(), rubyClass, self, aBlock);
     }
 
     private static IRubyObject classVarAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ClassVarAsgnNode iVisited = (ClassVarAsgnNode) node;
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         RubyModule rubyClass = getClassVariableBase(context, runtime);
    
         if (rubyClass == null) {
             rubyClass = self.getMetaClass();
         }     
         rubyClass.setClassVar(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject classVarDeclNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ClassVarDeclNode iVisited = (ClassVarDeclNode) node;
    
         RubyModule rubyClass = getClassVariableBase(context, runtime);                
         if (rubyClass == null) {
             throw runtime.newTypeError("no class/module to define class variable");
         }
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         rubyClass.setClassVar(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject classVarNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         ClassVarNode iVisited = (ClassVarNode) node;
         RubyModule rubyClass = getClassVariableBase(context, runtime);
    
         if (rubyClass == null) {
             rubyClass = self.getMetaClass();
         }
 
         return rubyClass.getClassVar(iVisited.getName());
     }
 
     private static IRubyObject colon2Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Colon2Node iVisited = (Colon2Node) node;
         Node leftNode = iVisited.getLeftNode();
    
         // TODO: Made this more colon3 friendly because of cpath production
         // rule in grammar (it is convenient to think of them as the same thing
         // at a grammar level even though evaluation is).
         if (leftNode == null) {
             return runtime.getObject().getConstantFrom(iVisited.getName());
         } else {
             IRubyObject result = evalInternal(runtime,context, iVisited.getLeftNode(), self, aBlock);
             if (result instanceof RubyModule) {
                 return ((RubyModule) result).getConstantFrom(iVisited.getName());
             } else {
                 return result.callMethod(context, iVisited.getName(), aBlock);
             }
         }
     }
 
     private static IRubyObject colon3Node(Ruby runtime, Node node) {
         return runtime.getObject().getConstantFrom(((Colon3Node)node).getName());
     }
 
     private static IRubyObject constDeclNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ConstDeclNode iVisited = (ConstDeclNode) node;
         Node constNode = iVisited.getConstNode();
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         IRubyObject module;
 
         if (constNode == null) {
             // FIXME: why do we check RubyClass and then use CRef?
             if (context.getRubyClass() == null) {
                 // TODO: wire into new exception handling mechanism
                 throw runtime.newTypeError("no class/module to define constant");
             }
             module = (RubyModule) context.peekCRef().getValue();
         } else if (constNode instanceof Colon2Node) {
             module = evalInternal(runtime,context, ((Colon2Node) iVisited.getConstNode()).getLeftNode(), self, aBlock);
         } else { // Colon3
             module = runtime.getObject();
         } 
    
         // FIXME: shouldn't we use the result of this set in setResult?
         ((RubyModule) module).setConstant(iVisited.getName(), result);
    
         return result;
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
    
         Visibility visibility = context.getCurrentVisibility();
         if (name == "initialize" || visibility.isModuleFunction() || context.isTopLevel()) {
             visibility = Visibility.PRIVATE;
         }
         
         DefaultMethod newMethod = new DefaultMethod(containingClass, iVisited.getScope(), 
                 iVisited.getBodyNode(), (ArgsNode) iVisited.getArgsNode(), visibility, context.peekCRef());
    
         containingClass.addMethod(name, newMethod);
    
         if (context.getCurrentVisibility().isModuleFunction()) {
             containingClass.getSingletonClass().addMethod(
                     name,
                     new WrapperMethod(containingClass.getSingletonClass(), newMethod,
                             Visibility.PUBLIC));
             containingClass.callMethod(context, "singleton_method_added", runtime.newSymbol(name));
         }
    
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             ((MetaClass) containingClass).getAttachedObject().callMethod(
                     context, "singleton_method_added", runtime.newSymbol(iVisited.getName()));
         } else {
             containingClass.callMethod(context, "method_added", runtime.newSymbol(name));
         }
    
         return runtime.getNil();
     }
     
     private static IRubyObject defsNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DefsNode iVisited = (DefsNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
    
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
                 throw runtime.newTypeError("can't define singleton method \"" + iVisited.getName()
                                            + "\" for " + receiver.getType());
             }
    
             rubyClass = receiver.getSingletonClass();
         }
    
         if (runtime.getSafeLevel() >= 4) {
             Object method = rubyClass.getMethods().get(iVisited.getName());
             if (method != null) {
                 throw runtime.newSecurityError("Redefining method prohibited.");
             }
         }
    
         DefaultMethod newMethod = new DefaultMethod(rubyClass, iVisited.getScope(), 
                 iVisited.getBodyNode(), (ArgsNode) iVisited.getArgsNode(), 
                 Visibility.PUBLIC, context.peekCRef());
    
         rubyClass.addMethod(iVisited.getName(), newMethod);
         receiver.callMethod(context, "singleton_method_added", runtime.newSymbol(iVisited.getName()));
    
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
    
         RubyString string = runtime.newString(new ByteList());
         for (int i = 0; i < iVisited.size(); i++) {
             Node iterNode = iVisited.get(i);
             if (iterNode instanceof StrNode) {
                 string.getByteList().append(((StrNode) iterNode).getValue());
             } else {
                 string.append(evalInternal(runtime,context, iterNode, self, aBlock));
             }
         }
    
         String lang = null;
         int opts = iVisited.getOptions();
         if((opts & 16) != 0) { // param n
             lang = "n";
         } else if((opts & 48) != 0) { // param s
             lang = "s";
         } else if((opts & 64) != 0) { // param s
             lang = "u";
         }
 
         try {
             return RubyRegexp.newRegexp(runtime, string.toString(), iVisited.getOptions(), lang);
         } catch(jregex.PatternSyntaxException e) {
         //                    System.err.println(iVisited.getValue().toString());
         //                    e.printStackTrace();
             throw runtime.newRegexpError(e.getMessage());
         }
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
         context.pollThreadEvents();
         return runtime.getFalse();
     }
 
     private static IRubyObject fCallNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         FCallNode iVisited = (FCallNode) node;
         
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
         
         // No block provided lets look at fast path for STI dispatch.
         if (!block.isGiven()) {
             RubyModule module = self.getMetaClass();
             if (module.index != 0 && iVisited.index != 0) {
                 return self.callMethod(context, module,
                         runtime.getSelectorTable().table[module.index][iVisited.index],
                         iVisited.getName(), args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
             } else {
                 DynamicMethod method = module.searchMethod(iVisited.getName());
 
                 IRubyObject mmResult = RubyObject.callMethodMissingIfNecessary(context, self, method, iVisited.getName(), args, self, CallType.FUNCTIONAL, Block.NULL_BLOCK);
                 if (mmResult != null) {
                     return mmResult;
                 }
 
                 return method.call(context, self, module, iVisited.getName(), args, false, Block.NULL_BLOCK);
             }
         }
 
         while (true) {
             try {
                 RubyModule module = self.getMetaClass();
                 IRubyObject result = self.callMethod(context, module, iVisited.getName(), args,
                                                      CallType.FUNCTIONAL, block);
                 if (result == null) {
                     result = runtime.getNil();
                 }
                     
                 return result; 
             } catch (JumpException je) {
                 switch (je.getJumpType().getTypeId()) {
                 case JumpType.RETRY:
                     // allow loop to retry
                     break;
                 case JumpType.BREAK:
                     // JRUBY-530, Kernel#loop case:
                     if (je.isBreakInKernelLoop()) {
                         // consume and rethrow or just keep rethrowing?
                         if (block == je.getTarget()) je.setBreakInKernelLoop(false);
                             
                         throw je;
                     }
                         
                     return (IRubyObject) je.getValue();
                 default:
                     throw je;
                 }
             }
         }
     }
 
     private static IRubyObject fixnumNode(Ruby runtime, Node node) {
         return ((FixnumNode)node).getFixnum(runtime);
     }
 
     private static IRubyObject flipNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         FlipNode iVisited = (FlipNode) node;
         IRubyObject result = runtime.getNil();
    
         if (iVisited.isExclusive()) {
             if (!context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth()).isTrue()) {
                 result = evalInternal(runtime,context, iVisited.getBeginNode(), self, aBlock).isTrue() ? runtime.getFalse()
                         : runtime.getTrue();
                 context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
                 return result;
             } else {
                 if (evalInternal(runtime,context, iVisited.getEndNode(), self, aBlock).isTrue()) {
                     context.getCurrentScope().setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
                 }
                 return runtime.getTrue();
             }
         } else {
             if (!context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth()).isTrue()) {
                 if (evalInternal(runtime,context, iVisited.getBeginNode(), self, aBlock).isTrue()) {
                     context.getCurrentScope().setValue(
                             iVisited.getIndex(),
                             evalInternal(runtime,context, iVisited.getEndNode(), self, aBlock).isTrue() ? runtime.getFalse()
                                     : runtime.getTrue(), iVisited.getDepth());
                     return runtime.getTrue();
                 } else {
                     return runtime.getFalse();
                 }
             } else {
                 if (evalInternal(runtime,context, iVisited.getEndNode(), self, aBlock).isTrue()) {
                     context.getCurrentScope().setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
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
         
         Block block = ForBlock.createBlock(context, iVisited.getVarNode(), 
                 context.getCurrentScope(), iVisited.getCallable(), self);
    
         try {
             while (true) {
                 try {
                     ISourcePosition position = context.getPosition();
    
                     IRubyObject recv = null;
                     try {
                         recv = evalInternal(runtime,context, iVisited.getIterNode(), self, aBlock);
                     } finally {
                         context.setPosition(position);
                     }
    
                     return recv.callMethod(context, "each", IRubyObject.NULL_ARRAY, CallType.NORMAL, block);
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
                     case JumpType.RETRY:
                         // do nothing, allow loop to retry
                         break;
                     default:
                         throw je;
                     }
                 }
             }
         } catch (JumpException je) {
             switch (je.getJumpType().getTypeId()) {
             case JumpType.BREAK:
                 return (IRubyObject) je.getValue();
             default:
                 throw je;
             }
         }
     }
 
     private static IRubyObject globalAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         GlobalAsgnNode iVisited = (GlobalAsgnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         
         if (iVisited.getName().length() == 2) {
             switch (iVisited.getName().charAt(1)) {
             case '_':
                 context.getCurrentScope().setLastLine(result);
                 return result;
             case '~':
                 context.getCurrentScope().setBackRef(result);
                 return result;
             }
         }
    
         runtime.getGlobalVariables().set(iVisited.getName(), result);
    
         // FIXME: this should be encapsulated along with the set above
         if (iVisited.getName() == "$KCODE") {
             runtime.setKCode(KCode.create(runtime, result.toString()));
         }
    
         return result;
     }
 
     private static IRubyObject globalVarNode(Ruby runtime, ThreadContext context, Node node) {
         GlobalVarNode iVisited = (GlobalVarNode) node;
         
         if (iVisited.getName().length() == 2) {
             IRubyObject value = null;
             switch (iVisited.getName().charAt(1)) {
             case '_':
                 value = context.getCurrentScope().getLastLine();
                 if (value == null) {
                     return runtime.getNil();
                 }
                 return value;
             case '~':
                 value = context.getCurrentScope().getBackRef();
                 if (value == null) {
                     return runtime.getNil();
                 }
                 return value;
             }
         }
         
         return runtime.getGlobalVariables().get(iVisited.getName());
     }
 
     private static IRubyObject hashNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         HashNode iVisited = (HashNode) node;
    
         Map hash = null;
         if (iVisited.getListNode() != null) {
             hash = new HashMap(iVisited.getListNode().size() / 2);
    
         for (int i = 0; i < iVisited.getListNode().size();) {
                 // insert all nodes in sequence, hash them in the final instruction
                 // KEY
                 IRubyObject key = evalInternal(runtime,context, iVisited.getListNode().get(i++), self, aBlock);
                 IRubyObject value = evalInternal(runtime,context, iVisited.getListNode().get(i++), self, aBlock);
    
                 hash.put(key, value);
             }
         }
    
         if (hash == null) {
             return RubyHash.newHash(runtime);
         }
    
         return RubyHash.newHash(runtime, hash, runtime.getNil());
     }
 
     private static IRubyObject instAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         InstAsgnNode iVisited = (InstAsgnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         self.setInstanceVariable(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject instVarNode(Ruby runtime, Node node, IRubyObject self) {
         InstVarNode iVisited = (InstVarNode) node;
         IRubyObject variable = self.getInstanceVariable(iVisited.getName());
    
         return variable == null ? runtime.getNil() : variable;
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
 
         //System.out.println("DGetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth());
         IRubyObject result = context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth());
 
         return result == null ? runtime.getNil() : result;
     }
 
     private static IRubyObject match2Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Match2Node iVisited = (Match2Node) node;
         IRubyObject recv = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         return ((RubyRegexp) recv).match(value);
     }
     
     private static IRubyObject match3Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Match3Node iVisited = (Match3Node) node;
         IRubyObject recv = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         if (value instanceof RubyString) {
             return ((RubyRegexp) recv).match(value);
         } else {
             return value.callMethod(context, "=~", recv);
         }
     }
 
     private static IRubyObject matchNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return ((RubyRegexp) evalInternal(runtime,context, ((MatchNode)node).getRegexpNode(), self, aBlock)).match2();
     }
 
     private static IRubyObject moduleNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ModuleNode iVisited = (ModuleNode) node;
         Node classNameNode = iVisited.getCPath();
         String name = ((INameNode) classNameNode).getName();
         RubyModule enclosingModule = getEnclosingModule(runtime, context, classNameNode, self, aBlock);
    
         if (enclosingModule == null) {
             throw runtime.newTypeError("no outer class/module");
         }
    
         RubyModule module;
         if (enclosingModule == runtime.getObject()) {
             module = runtime.getOrCreateModule(name);
         } else {
             module = enclosingModule.defineModuleUnder(name);
         }
         return evalClassDefinitionBody(runtime, context, iVisited.getScope(), iVisited.getBodyNode(), module, self, aBlock);
     }
 
     private static IRubyObject multipleAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         MultipleAsgnNode iVisited = (MultipleAsgnNode) node;
         
         switch (iVisited.getValueNode().nodeId) {
         case NodeTypes.ARRAYNODE: {
             ArrayNode iVisited2 = (ArrayNode) iVisited.getValueNode();
             IRubyObject[] array = new IRubyObject[iVisited2.size()];
 
             for (int i = 0; i < iVisited2.size(); i++) {
                 Node next = iVisited2.get(i);
 
                 array[i] = evalInternal(runtime,context, next, self, aBlock);
             }
             return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, RubyArray.newArrayNoCopyLight(runtime, array), false);
         }
         case NodeTypes.SPLATNODE: {
             SplatNode splatNode = (SplatNode)iVisited.getValueNode();
             RubyArray rubyArray = splatValue(runtime, evalInternal(runtime, context, ((SplatNode) splatNode).getValue(), self, aBlock));
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
 
     private static IRubyObject nextNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         NextNode iVisited = (NextNode) node;
    
         context.pollThreadEvents();
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         // now used as an interpreter event
         JumpException je = new JumpException(JumpException.JumpType.NextJump);
    
         je.setTarget(iVisited);
         je.setValue(result);
    
         throw je;
     }
 
     private static IRubyObject nilNode(Ruby runtime) {
         return runtime.getNil();
     }
 
     private static IRubyObject notNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         NotNode iVisited = (NotNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock);
         return result.isTrue() ? runtime.getFalse() : runtime.getTrue();
     }
 
     private static IRubyObject nthRefNode(ThreadContext context, Node node) {
         return RubyRegexp.nth_match(((NthRefNode)node).getMatchNumber(), context.getBackref());
     }
 
     private static IRubyObject opAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OpAsgnNode iVisited = (OpAsgnNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = receiver.callMethod(context, iVisited.getVariableName());
    
         if (iVisited.getOperatorName() == "||") {
             if (value.isTrue()) {
                 return value;
             }
             value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else if (iVisited.getOperatorName() == "&&") {
             if (!value.isTrue()) {
                 return value;
             }
             value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else {
             value = value.callMethod(context, iVisited.getOperatorName(), evalInternal(runtime,context,
                     iVisited.getValueNode(), self, aBlock));
         }
    
         receiver.callMethod(context, iVisited.getVariableNameAsgn(), value);
    
         context.pollThreadEvents();
    
         return value;
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
    
         return result;
     }
 
     private static IRubyObject opElementAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OpElementAsgnNode iVisited = (OpElementAsgnNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
    
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
    
         IRubyObject firstValue = receiver.callMethod(context, "[]", args);
    
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
             firstValue = firstValue.callMethod(context, iVisited.getOperatorName(), evalInternal(runtime,context, iVisited
                             .getValueNode(), self, aBlock));
         }
    
         IRubyObject[] expandedArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, expandedArgs, 0, args.length);
         expandedArgs[expandedArgs.length - 1] = firstValue;
         return receiver.callMethod(context, "[]=", expandedArgs);
     }
 
     private static IRubyObject optNNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OptNNode iVisited = (OptNNode) node;
    
         IRubyObject result = runtime.getNil();
         while (RubyKernel.gets(runtime.getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
             loop: while (true) { // Used for the 'redo' command
                 try {
                     result = evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break;
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
                     case JumpType.REDO:
                         // do nothing, this iteration restarts
                         break;
                     case JumpType.NEXT:
                         // recheck condition
                         break loop;
                     case JumpType.BREAK:
                         // end loop
                         return (IRubyObject) je.getValue();
                     default:
                         throw je;
                     }
                 }
             }
         }
         return result;
     }
 
     private static IRubyObject orNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OrNode iVisited = (OrNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
    
         if (!result.isTrue()) {
             result = evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock);
         }
    
         return result;
     }
 
     private static IRubyObject redoNode(ThreadContext context, Node node) {
         context.pollThreadEvents();
    
         // now used as an interpreter event
         JumpException je = new JumpException(JumpException.JumpType.RedoJump);
    
         je.setValue(node);
    
         throw je;
     }
 
     private static IRubyObject regexpNode(Ruby runtime, Node node) {
         RegexpNode iVisited = (RegexpNode) node;
         String lang = null;
         int opts = iVisited.getOptions();
         if((opts & 16) != 0) { // param n
             lang = "n";
         } else if((opts & 48) != 0) { // param s
             lang = "s";
         } else if((opts & 64) != 0) { // param s
             lang = "u";
         }
         try {
             return RubyRegexp.newRegexp(runtime, iVisited.getPattern(), iVisited.getFlags(), lang);
         } catch(jregex.PatternSyntaxException e) {
             //                    System.err.println(iVisited.getValue().toString());
             //                    e.printStackTrace();
             throw runtime.newRegexpError(e.getMessage());
         }
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
                     
                     if (isRescueHandled(runtime, context, raisedException, exceptionNodesList, self)) {
                         try {
                             return evalInternal(runtime,context, rescueNode, self, aBlock);
                         } catch (JumpException je) {
                             if (je.getJumpType() == JumpException.JumpType.RetryJump) {
                                 // should be handled in the finally block below
                                 //state.runtime.getGlobalVariables().set("$!", state.runtime.getNil());
                                 //state.threadContext.setRaisedException(null);
                                 continue RescuedBlock;
                                 
                             } else {
                                 anotherExceptionRaised = true;
                                 throw je;
                             }
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
    
         JumpException je = new JumpException(JumpException.JumpType.RetryJump);
    
         throw je;
     }
     
     private static IRubyObject returnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ReturnNode iVisited = (ReturnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         JumpException je = new JumpException(JumpException.JumpType.ReturnJump);
-   
+
         je.setTarget(iVisited.getTarget());
         je.setValue(result);
    
         throw je;
     }
 
     private static IRubyObject rootNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         RootNode iVisited = (RootNode) node;
         DynamicScope scope = iVisited.getScope();
         
         // Serialization killed our dynamic scope.  We can just create an empty one
         // since serialization cannot serialize an eval (which is the only thing
         // which is capable of having a non-empty dynamic scope).
         if (scope == null) {
             scope = new DynamicScope(iVisited.getStaticScope(), null);
         }
         
         // Each root node has a top-level scope that we need to push
         context.preRootNode(scope);
         
         // FIXME: Wire up BEGIN and END nodes
 
         try {
             return evalInternal(runtime, context, iVisited.getBodyNode(), self, aBlock);
         } finally {
             context.postRootNode();
         }
     }
 
     private static IRubyObject sClassNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         SClassNode iVisited = (SClassNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
 
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
 
         
 
         if (context.getWrapper() != null) {
             context.getWrapper().extend_object(singletonClass);
             singletonClass.includeModule(context.getWrapper());
         }
 
         return evalClassDefinitionBody(runtime, context, iVisited.getScope(), iVisited.getBodyNode(), singletonClass, self, aBlock);
     }
 
     private static IRubyObject splatNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return splatValue(runtime, evalInternal(runtime,context, ((SplatNode) node).getValue(), self, aBlock));
     }
 
     private static IRubyObject strNode(Ruby runtime, Node node) {
         return runtime.newString((ByteList) ((StrNode) node).getValue().clone());
     }
     
     private static IRubyObject superNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         SuperNode iVisited = (SuperNode) node;
    
         RubyModule klazz = context.getFrameKlazz();
         
         if (klazz == null) {
             String name = context.getFrameName();
             throw runtime.newNameError("Superclass method '" + name
                     + "' disabled.", name);
         }
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
         
         // If no explicit block passed to super, then use the one passed in.
         if (!block.isGiven()) block = aBlock;
         
         return self.callSuper(context, args, block);
     }
     
     private static IRubyObject sValueNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return aValueSplat(runtime, evalInternal(runtime,context, ((SValueNode) node).getValue(), self, aBlock));
     }
     
     private static IRubyObject symbolNode(Ruby runtime, Node node) {
         return runtime.newSymbol(((SymbolNode) node).getName());
     }
     
     private static IRubyObject toAryNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return aryToAry(runtime, evalInternal(runtime,context, ((ToAryNode) node).getValue(), self, aBlock));
     }
 
     private static IRubyObject trueNode(Ruby runtime, ThreadContext context) {
         context.pollThreadEvents();
         return runtime.getTrue();
     }
     
     private static IRubyObject undefNode(Ruby runtime, ThreadContext context, Node node) {
         UndefNode iVisited = (UndefNode) node;
         
    
         if (context.getRubyClass() == null) {
             throw runtime
                     .newTypeError("No class to undef method '" + iVisited.getName() + "'.");
         }
         context.getRubyClass().undef(iVisited.getName());
    
         return runtime.getNil();
     }
 
     private static IRubyObject untilNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         UntilNode iVisited = (UntilNode) node;
    
         IRubyObject result = runtime.getNil();
         
         while (!(result = evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock)).isTrue()) {
             loop: while (true) { // Used for the 'redo' command
                 try {
                     result = evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break loop;
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
                     case JumpType.REDO:
                         continue;
                     case JumpType.NEXT:
                         break loop;
                     case JumpType.BREAK:
                         // JRUBY-530 until case
                         if (je.getTarget() == aBlock) {
                              je.setTarget(null);
                              
                              throw je;
                         }
                         
                         return (IRubyObject) je.getValue();
                     default:
                         throw je;
                     }
                 }
             }
         }
         
         return result;
     }
 
     private static IRubyObject valiasNode(Ruby runtime, Node node) {
         VAliasNode iVisited = (VAliasNode) node;
         runtime.getGlobalVariables().alias(iVisited.getNewName(), iVisited.getOldName());
    
         return runtime.getNil();
     }
 
     private static IRubyObject vcallNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         VCallNode iVisited = (VCallNode) node;
         RubyModule module = self.getMetaClass();
         if (module.index != 0 && iVisited.index != 0) {
             return self.callMethod(context, module, runtime.getSelectorTable().table[module.index][iVisited.index], iVisited.getName(), 
                     IRubyObject.NULL_ARRAY, CallType.VARIABLE, Block.NULL_BLOCK);
         } else {
             DynamicMethod method = module.searchMethod(iVisited.getName());
 
             IRubyObject mmResult = RubyObject.callMethodMissingIfNecessary(context, self, method, iVisited.getName(), IRubyObject.NULL_ARRAY, self, CallType.VARIABLE, Block.NULL_BLOCK);
             if (mmResult != null) {
                 return mmResult;
             }
 
             return method.call(context, self, module, iVisited.getName(), IRubyObject.NULL_ARRAY, false, Block.NULL_BLOCK);
         }
     }
 
     private static IRubyObject whileNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         WhileNode iVisited = (WhileNode) node;
    
         IRubyObject result = runtime.getNil();
         boolean firstTest = iVisited.evaluateAtStart();
         
         while (!firstTest || (result = evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock)).isTrue()) {
             firstTest = true;
             loop: while (true) { // Used for the 'redo' command
                 try {
                     evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break loop;
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
                     case JumpType.REDO:
                         continue;
                     case JumpType.NEXT:
                         break loop;
                     case JumpType.BREAK:
                         // JRUBY-530, while case
                         if (je.getTarget() == aBlock) {
                             je.setTarget(null);
                             
                             throw je;
                         }
                         
                         return result;
                     default:
                         throw je;
                     }
                 }
             }
         }
         
         return result;
     }
 
     private static IRubyObject xStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         return self.callMethod(context, "`", runtime.newString((ByteList) ((XStrNode) node).getValue().clone()));
     }
 
     private static IRubyObject yieldNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         YieldNode iVisited = (YieldNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getArgsNode(), self, aBlock);
         if (iVisited.getArgsNode() == null) {
             result = null;
         }
 
         Block block = context.getCurrentFrame().getBlock();
 
         return block.yield(context, result, null, null, iVisited.getCheckState());
     }
 
     private static IRubyObject zArrayNode(Ruby runtime) {
         return runtime.newArray();
     }
     
     private static IRubyObject zsuperNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         if (context.getFrameKlazz() == null) {
             String name = context.getFrameName();
             throw runtime.newNameError("superclass method '" + name
                     + "' disabled", name);
         }
 
         Block block = getBlock(runtime, context, self, aBlock, ((ZSuperNode) node).getIterNode());
 
         // Has the method that is calling super received a block argument
         if (!block.isGiven()) block = context.getCurrentFrame().getBlock(); 
         
         context.getCurrentScope().getArgValues(context.getFrameArgs(),context.getCurrentFrame().getRequiredArgCount());
         return self.callSuper(context, context.getFrameArgs(), block);
     }
 
     public static IRubyObject aValueSplat(Ruby runtime, IRubyObject value) {
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return runtime.getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first(IRubyObject.NULL_ARRAY) : array;
     }
 
     private static void callTraceFunction(Ruby runtime, ThreadContext context, String event, IRubyObject zelf) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         runtime.callTraceFunction(context, event, context.getPosition(), zelf, name, type);
     }
 
     /** Evaluates the body in a class or module definition statement.
      *
      */
     private static IRubyObject evalClassDefinitionBody(Ruby runtime, ThreadContext context, StaticScope scope, 
             Node bodyNode, RubyModule type, IRubyObject self, Block block) {
         context.preClassEval(scope, type);
 
         try {
             if (isTrace(runtime)) {
                 callTraceFunction(runtime, context, "class", type);
             }
 
             return evalInternal(runtime,context, bodyNode, type, block);
         } finally {
             if (isTrace(runtime)) {
                 callTraceFunction(runtime, context, "end", null);
             }
             
             context.postClassEval();
         }
     }
 
     private static String getArgumentDefinition(Ruby runtime, ThreadContext context, Node node, String type, IRubyObject self, Block block) {
         if (node == null) return type;
             
         if (node instanceof ArrayNode) {
             for (int i = 0; i < ((ArrayNode)node).size(); i++) {
                 Node iterNode = ((ArrayNode)node).get(i);
                 if (getDefinitionInner(runtime, context, iterNode, self, block) == null) return null;
             }
         } else if (getDefinitionInner(runtime, context, node, self, block) == null) {
             return null;
         }
 
         return type;
     }
     
     public static Block getBlock(Ruby runtime, ThreadContext context, IRubyObject self, Block currentBlock, Node blockNode) {
         if (blockNode == null) return Block.NULL_BLOCK;
         
         if (blockNode instanceof IterNode) {
             IterNode iterNode = (IterNode) blockNode;
             // Create block for this iter node
             return Block.createBlock(context, iterNode.getVarNode(),
                     new DynamicScope(iterNode.getScope(), context.getCurrentScope()),
                     iterNode.getCallable(), self);
         } else if (blockNode instanceof BlockPassNode) {
             BlockPassNode blockPassNode = (BlockPassNode) blockNode;
             IRubyObject proc = evalInternal(runtime,context, blockPassNode.getBodyNode(), self, currentBlock);
 
             // No block from a nil proc
             if (proc.isNil()) return Block.NULL_BLOCK;
 
             // If not already a proc then we should try and make it one.
             if (!(proc instanceof RubyProc)) {
                 proc = proc.convertToType("Proc", "to_proc", false);
 
                 if (!(proc instanceof RubyProc)) {
                     throw runtime.newTypeError("wrong argument type "
                             + proc.getMetaClass().getName() + " (expected Proc)");
                 }
             }
 
             // TODO: Add safety check for taintedness
             
             if (currentBlock.isGiven()) {
                 RubyProc procObject = currentBlock.getProcObject();
                 // The current block is already associated with proc.  No need to create a new one
                 if (procObject != null && procObject == proc) return currentBlock;
             }
             
             return ((RubyProc) proc).getBlock();
         }
          
         assert false: "Trying to get block from something which cannot deliver";
         return null;
     }
 
     /* Something like cvar_cbase() from eval.c, factored out for the benefit
      * of all the classvar-related node evaluations */
     private static RubyModule getClassVariableBase(ThreadContext context, Ruby runtime) {
         SinglyLinkedList cref = context.peekCRef();
         RubyModule rubyClass = (RubyModule) cref.getValue();
         if (rubyClass.isSingleton()) {
             cref = cref.getNext();
             rubyClass = (RubyModule) cref.getValue();
             if (cref.getNext() == null) {
                 runtime.getWarnings().warn("class variable access from toplevel singleton method");
             }            
         }
         return rubyClass;
     }
 
     private static String getDefinition(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         try {
             context.setWithinDefined(true);
             return getDefinitionInner(runtime, context, node, self, aBlock);
         } finally {
             context.setWithinDefined(false);
         }
     }
 
     private static String getDefinitionInner(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         if (node == null) return "expression";
         
         switch(node.nodeId) {
         case NodeTypes.ATTRASSIGNNODE: {
             AttrAssignNode iVisited = (AttrAssignNode) node;
             
             if (getDefinitionInner(runtime, context, iVisited.getReceiverNode(), self, aBlock) != null) {
                 try {
                     IRubyObject receiver = eval(runtime, context, iVisited.getReceiverNode(), self, aBlock);
                     RubyClass metaClass = receiver.getMetaClass();
                     DynamicMethod method = metaClass.searchMethod(iVisited.getName());
                     Visibility visibility = method.getVisibility();
 
                     if (!visibility.isPrivate() && 
                             (!visibility.isProtected() || self.isKindOf(metaClass.getRealClass()))) {
                         if (metaClass.isMethodBound(iVisited.getName(), false)) {
                             return getArgumentDefinition(runtime,context, iVisited.getArgsNode(), "assignment", self, aBlock);
                         }
                     }
                 } catch (JumpException excptn) {
                 }
             }
 
             return null;
         }
         case NodeTypes.BACKREFNODE:
             return "$" + ((BackRefNode) node).getType();
         case NodeTypes.CALLNODE: {
             CallNode iVisited = (CallNode) node;
             
             if (getDefinitionInner(runtime, context, iVisited.getReceiverNode(), self, aBlock) != null) {
                 try {
                     IRubyObject receiver = eval(runtime, context, iVisited.getReceiverNode(), self, aBlock);
                     RubyClass metaClass = receiver.getMetaClass();
                     DynamicMethod method = metaClass.searchMethod(iVisited.getName());
                     Visibility visibility = method.getVisibility();
 
                     if (!visibility.isPrivate() && 
                             (!visibility.isProtected() || self.isKindOf(metaClass.getRealClass()))) {
                         if (metaClass.isMethodBound(iVisited.getName(), false)) {
                             return getArgumentDefinition(runtime, context, iVisited.getArgsNode(), "method", self, aBlock);
                         }
                     }
                 } catch (JumpException excptn) {
                 }
             }
 
             return null;
         }
         case NodeTypes.CLASSVARASGNNODE: case NodeTypes.CLASSVARDECLNODE: case NodeTypes.CONSTDECLNODE:
         case NodeTypes.DASGNNODE: case NodeTypes.GLOBALASGNNODE: case NodeTypes.LOCALASGNNODE:
         case NodeTypes.MULTIPLEASGNNODE: case NodeTypes.OPASGNNODE: case NodeTypes.OPELEMENTASGNNODE:
             return "assignment";
             
         case NodeTypes.CLASSVARNODE: {
             ClassVarNode iVisited = (ClassVarNode) node;
             
             if (context.getRubyClass() == null && self.getMetaClass().isClassVarDefined(iVisited.getName())) {
                 return "class_variable";
             } else if (!context.getRubyClass().isSingleton() && context.getRubyClass().isClassVarDefined(iVisited.getName())) {
                 return "class_variable";
             } 
               
             RubyModule module = (RubyModule) context.getRubyClass().getInstanceVariable("__attached__");
             if (module != null && module.isClassVarDefined(iVisited.getName())) return "class_variable"; 
 
             return null;
         }
         case NodeTypes.COLON2NODE: {
             Colon2Node iVisited = (Colon2Node) node;
             
             try {
                 IRubyObject left = EvaluationState.eval(runtime, context, iVisited.getLeftNode(), self, aBlock);
                 if (left instanceof RubyModule &&
                         ((RubyModule) left).getConstantAt(iVisited.getName()) != null) {
                     return "constant";
                 } else if (left.getMetaClass().isMethodBound(iVisited.getName(), true)) {
                     return "method";
                 }
             } catch (JumpException excptn) {}
             
             return null;
         }
         case NodeTypes.CONSTNODE:
             if (context.getConstantDefined(((ConstNode) node).getName())) {
                 return "constant";
             }
             return null;
         case NodeTypes.DVARNODE:
             return "local-variable(in-block)";
         case NodeTypes.FALSENODE:
             return "false";
         case NodeTypes.FCALLNODE: {
             FCallNode iVisited = (FCallNode) node;
             if (self.getMetaClass().isMethodBound(iVisited.getName(), false)) {
                 return getArgumentDefinition(runtime, context, iVisited.getArgsNode(), "method", self, aBlock);
             }
             
             return null;
         }
         case NodeTypes.GLOBALVARNODE:
             if (runtime.getGlobalVariables().isDefined(((GlobalVarNode) node).getName())) {
                 return "global-variable";
             }
             return null;
         case NodeTypes.INSTVARNODE:
             if (self.getInstanceVariable(((InstVarNode) node).getName()) != null) {
                 return "instance-variable";
             }
             return null;
         case NodeTypes.LOCALVARNODE:
             return "local-variable";
         case NodeTypes.MATCH2NODE: case NodeTypes.MATCH3NODE:
             return "method";
         case NodeTypes.NILNODE:
             return "nil";
         case NodeTypes.NTHREFNODE:
             return "$" + ((NthRefNode) node).getMatchNumber();
         case NodeTypes.SELFNODE:
             return "state.getSelf()";
         case NodeTypes.SUPERNODE: {
             SuperNode iVisited = (SuperNode) node;
             String name = context.getFrameName();
             RubyModule klazz = context.getFrameKlazz();
             if (name != null && klazz != null && klazz.getSuperClass().isMethodBound(name, false)) {
                 return getArgumentDefinition(runtime, context, iVisited.getArgsNode(), "super", self, aBlock);
             }
             
             return null;
         }
         case NodeTypes.TRUENODE:
             return "true";
         case NodeTypes.VCALLNODE: {
             VCallNode iVisited = (VCallNode) node;
             if (self.getMetaClass().isMethodBound(iVisited.getName(), false)) {
                 return "method";
             }
             
             return null;
         }
         case NodeTypes.YIELDNODE:
             return aBlock.isGiven() ? "yield" : null;
         case NodeTypes.ZSUPERNODE: {
             String name = context.getFrameName();
             RubyModule klazz = context.getFrameKlazz();
             if (name != null && klazz != null && klazz.getSuperClass().isMethodBound(name, false)) {
                 return "super";
             }
             return null;
         }
         default:
             try {
                 EvaluationState.eval(runtime, context, node, self, aBlock);
                 return "expression";
             } catch (JumpException jumpExcptn) {}
         }
         
         return null;
     }
 
     private static RubyModule getEnclosingModule(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block block) {
         RubyModule enclosingModule = null;
 
         if (node instanceof Colon2Node) {
             IRubyObject result = evalInternal(runtime,context, ((Colon2Node) node).getLeftNode(), self, block);
 
             if (result != null && !result.isNil()) {
                 enclosingModule = (RubyModule) result;
             }
         } else if (node instanceof Colon3Node) {
             enclosingModule = runtime.getObject();
         }
 
         if (enclosingModule == null) {
             enclosingModule = (RubyModule) context.peekCRef().getValue();
         }
 
         return enclosingModule;
     }
 
     private static boolean isRescueHandled(Ruby runtime, ThreadContext context, RubyException currentException, ListNode exceptionNodes,
             IRubyObject self) {
         if (exceptionNodes == null) {
             return currentException.isKindOf(runtime.getClass("StandardError"));
         }
 
         IRubyObject[] args = setupArgs(runtime, context, exceptionNodes, self);
 
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isKindOf(runtime.getClass("Module"))) {
                 throw runtime.newTypeError("class or module required for rescue clause");
             }
             if (args[i].callMethod(context, "===", currentException).isTrue()) return true;
         }
         return false;
     }
 
     /**
      * Helper method.
      *
      * test if a trace function is avaiable.
      *
      */
     private static boolean isTrace(Ruby runtime) {
         return runtime.getTraceFunction() != null;
     }
 
     private static IRubyObject[] setupArgs(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         if (node == null) return IRubyObject.NULL_ARRAY;
 
         if (node instanceof ArrayNode) {
             ArrayNode argsArrayNode = (ArrayNode) node;
             ISourcePosition position = context.getPosition();
             int size = argsArrayNode.size();
             IRubyObject[] argsArray = new IRubyObject[size];
 
             for (int i = 0; i < size; i++) {
                 argsArray[i] = evalInternal(runtime,context, argsArrayNode.get(i), self, Block.NULL_BLOCK);
             }
 
             context.setPosition(position);
 
             return argsArray;
         }
 
         return ArgsUtil.convertToJavaArray(evalInternal(runtime,context, node, self, Block.NULL_BLOCK));
     }
 
     public static RubyArray splatValue(Ruby runtime, IRubyObject value) {
         if (value.isNil()) {
             return runtime.newArray(value);
         }
 
         return arrayValue(runtime, value);
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/DefaultMethod.java b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
index 3d706b1477..425b285d7f 100644
--- a/src/org/jruby/internal/runtime/methods/DefaultMethod.java
+++ b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
@@ -1,314 +1,317 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 package org.jruby.internal.runtime.methods;
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.Script;
 import org.jruby.compiler.NodeCompilerFactory;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.evaluator.AssignmentVisitor;
 import org.jruby.evaluator.CreateJumpTargetVisitor;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  *
  */
 public final class DefaultMethod extends DynamicMethod {
     
     private StaticScope staticScope;
     private Node body;
     private ArgsNode argsNode;
     private SinglyLinkedList cref;
     private boolean hasBeenTargeted = false;
     private int callCount = 0;
     private Script jitCompiledScript;
 
     private static final boolean JIT_ENABLED = Boolean.getBoolean("jruby.jit.enabled");
     private static final boolean JIT_LOGGING = Boolean.getBoolean("jruby.jit.logging");
     private static final int JIT_THRESHOLD = Integer.parseInt(System.getProperty("jruby.jit.threshold", "50"));
     
     public DefaultMethod(RubyModule implementationClass, StaticScope staticScope, Node body, 
             ArgsNode argsNode, Visibility visibility, SinglyLinkedList cref) {
         super(implementationClass, visibility);
         this.body = body;
         this.staticScope = staticScope;
         this.argsNode = argsNode;
 		this.cref = cref;
 		
 		assert argsNode != null;
     }
     
     public void preMethod(ThreadContext context, RubyModule clazz, IRubyObject self, String name, 
             IRubyObject[] args, boolean noSuper, Block block) {
         context.preDefMethodInternalCall(clazz, name, self, args, getArity().required(), block, noSuper, cref, staticScope);
     }
     
     public void postMethod(ThreadContext context) {
         context.postDefMethodInternalCall();
     }
 
     /**
      * @see AbstractCallable#call(Ruby, IRubyObject, String, IRubyObject[], boolean)
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, 
             RubyModule clazz, String name, IRubyObject[] args, boolean noSuper, Block block) {
         if (jitCompiledScript != null) {
             try {
                 context.preCompiledMethod(implementationClass, cref);
                 // FIXME: pass block when available
                 return jitCompiledScript.run(context, self, args, block);
             } finally {
                 context.postCompiledMethod();
             }
         } 
           
         return super.call(context, self, clazz, name, args, noSuper, block);
     }
 
     /**
      * @see AbstractCallable#call(Ruby, IRubyObject, String, IRubyObject[], boolean)
      */
     public IRubyObject internalCall(ThreadContext context, RubyModule clazz, 
             IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         assert args != null;
         
         Ruby runtime = context.getRuntime();
         
         if (JIT_ENABLED) runJIT(runtime, name);
         
         if (JIT_ENABLED && jitCompiledScript != null) {
             return jitCompiledScript.run(context, self, args, block);
         }
         
         if (!hasBeenTargeted) {
             CreateJumpTargetVisitor.setJumpTarget(this, body);
             hasBeenTargeted = true;
         }
+        
+        // set jump target for returns that show up later, like from evals
+        context.setFrameJumpTarget(this);
 
         if (argsNode.getBlockArgNode() != null && block.isGiven()) {
             RubyProc blockArg;
             
             if (block.getProcObject() != null) {
                 blockArg = (RubyProc) block.getProcObject();
             } else {
                 blockArg = runtime.newProc(false, block);
                 blockArg.getBlock().isLambda = block.isLambda;
             }
             // We pass depth zero since we know this only applies to newly created local scope
             context.getCurrentScope().setValue(argsNode.getBlockArgNode().getCount(), blockArg, 0);
         }
 
         try {
             prepareArguments(context, runtime, args);
             
             getArity().checkArity(runtime, args);
 
             traceCall(context, runtime, self, name);
                     
             return EvaluationState.eval(runtime, context, body, self, block);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ReturnJump && je.getTarget() == this) {
 	                return (IRubyObject) je.getValue();
         	}
             
        		throw je;
         } finally {
             traceReturn(context, runtime, self, name);
         }
     }
 
     private void runJIT(Ruby runtime, String name) {
         if (callCount >= 0 && getArity().isFixed() && argsNode.getBlockArgNode() == null && argsNode.getOptArgs() == null && argsNode.getRestArg() == -1) {
             callCount++;
             if (callCount >= JIT_THRESHOLD) {
                 //if (JIT_LOGGING) System.out.println("trying to compile: " + getImplementationClass().getBaseName() + "." + name);
                 try {
                     String cleanName = CodegenUtils.cleanJavaIdentifier(name);
                     StandardASMCompiler compiler = new StandardASMCompiler(cleanName + hashCode(), body.getPosition().getFile());
                     compiler.startScript();
                     Object methodToken = compiler.beginMethod("__file__", getArity().getValue(), staticScope.getNumberOfVariables());
                     NodeCompilerFactory.getCompiler(body).compile(body, compiler);
                     compiler.endMethod(methodToken);
                     compiler.endScript();
                     Class sourceClass = compiler.loadClass(runtime);
                     jitCompiledScript = (Script)sourceClass.newInstance();
                     
                     String className = getImplementationClass().getBaseName();
                     if (className == null) {
                         className = "<anon class>";
                     }
                     if (JIT_LOGGING) System.out.println("compiled: " + className + "." + name);
                 } catch (Exception e) {
                     //                    e.printStackTrace();
                 } finally {
                     callCount = -1;
                 }
             }
         }
     }
 
     private void prepareArguments(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         int expectedArgsCount = argsNode.getArgsCount();
 
         int restArg = argsNode.getRestArg();
         boolean hasOptArgs = argsNode.getOptArgs() != null;
 
         // FIXME: This seems redundant with the arity check in internalCall...is it actually different?
         if (expectedArgsCount > args.length) {
             throw runtime.newArgumentError("Wrong # of arguments(" + args.length + " for " + expectedArgsCount + ")");
         }
 
         // Bind 'normal' parameter values to the local scope for this method.
         if (expectedArgsCount > 0) {
             context.getCurrentScope().setArgValues(args, expectedArgsCount);
         }
 
         // optArgs and restArgs require more work, so isolate them and ArrayList creation here
         if (hasOptArgs || restArg != -1) {
             args = prepareOptOrRestArgs(context, runtime, args, expectedArgsCount, restArg, hasOptArgs);
         }
         
         context.setFrameArgs(args);
     }
 
     private IRubyObject[] prepareOptOrRestArgs(ThreadContext context, Ruby runtime, IRubyObject[] args, int expectedArgsCount, int restArg, boolean hasOptArgs) {
         if (restArg == -1 && hasOptArgs) {
             int opt = expectedArgsCount + argsNode.getOptArgs().size();
 
             if (opt < args.length) {
                 throw runtime.newArgumentError("wrong # of arguments(" + args.length + " for " + opt + ")");
             }
         }
         
         int count = expectedArgsCount;
         if (argsNode.getOptArgs() != null) {
             count += argsNode.getOptArgs().size();
         }
 
         ArrayList allArgs = new ArrayList();
         
         // Combine static and optional args into a single list allArgs
         for (int i = 0; i < count && i < args.length; i++) {
             allArgs.add(args[i]);
         }
         
         if (hasOptArgs) {
             ListNode optArgs = argsNode.getOptArgs();
    
             int j = 0;
             for (int i = expectedArgsCount; i < args.length && j < optArgs.size(); i++, j++) {
                 // in-frame EvalState should already have receiver set as self, continue to use it
                 AssignmentVisitor.assign(runtime, context, context.getFrameSelf(), optArgs.get(j), args[i], Block.NULL_BLOCK, true);
                 expectedArgsCount++;
             }
    
             // assign the default values, adding to the end of allArgs
             while (j < optArgs.size()) {
                 // in-frame EvalState should already have receiver set as self, continue to use it
                 allArgs.add(EvaluationState.eval(runtime, context, optArgs.get(j++), context.getFrameSelf(), Block.NULL_BLOCK));
             }
         }
         
         // build an array from *rest type args, also adding to allArgs
         
         // ENEBO: Does this next comment still need to be done since I killed hasLocalVars:
         // move this out of the scope.hasLocalVariables() condition to deal
         // with anonymous restargs (* versus *rest)
         
         
         // none present ==> -1
         // named restarg ==> >=0
         // anonymous restarg ==> -2
         if (restArg != -1) {
             for (int i = expectedArgsCount; i < args.length; i++) {
                 allArgs.add(args[i]);
             }
 
             // only set in scope if named
             if (restArg >= 0) {
                 RubyArray array = runtime.newArray(args.length - expectedArgsCount);
                 for (int i = expectedArgsCount; i < args.length; i++) {
                     array.append(args[i]);
                 }
 
                 context.getCurrentScope().setValue(restArg, array, 0);
             }
         }
         
         args = (IRubyObject[])allArgs.toArray(new IRubyObject[allArgs.size()]);
         return args;
     }
 
     private void traceReturn(ThreadContext context, Ruby runtime, IRubyObject self, String name) {
         if (runtime.getTraceFunction() == null) {
             return;
         }
 
         ISourcePosition position = context.getPreviousFramePosition();
         runtime.callTraceFunction(context, "return", position, self, name, getImplementationClass());
     }
 
     private void traceCall(ThreadContext context, Ruby runtime, IRubyObject self, String name) {
         if (runtime.getTraceFunction() == null) return;
 
 		ISourcePosition position = body != null ? body.getPosition() : context.getPosition(); 
 
 		runtime.callTraceFunction(context, "call", position, self, name, getImplementationClass());
     }
 
     public Arity getArity() {
         return argsNode.getArity();
     }
     
     public DynamicMethod dup() {
         return new DefaultMethod(getImplementationClass(), staticScope, body, argsNode, getVisibility(), cref);
     }
 }
diff --git a/src/org/jruby/runtime/Frame.java b/src/org/jruby/runtime/Frame.java
index b54d99d004..f2f1e247b6 100644
--- a/src/org/jruby/runtime/Frame.java
+++ b/src/org/jruby/runtime/Frame.java
@@ -1,249 +1,259 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import org.jruby.RubyModule;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * <p>Frame for a full (read: not 'fast') Ruby method invocation.  Any Ruby method which calls 
  * another Ruby method (or yields to a block) will get a Frame.  A fast method by contrast does 
  * not get a Frame because we know that we will not be calling/yielding.</p>  
  * 
  * A Frame is also needed for a few special cases:
  * <ul>
  * <li>Proc.new must check previous frame to get the block it is getting constructed for
  * <li>block_given? must check the previous frame to see if a block is active
  * </li>
  * 
  */
 public class Frame {
     /**
      * The class for the method we are invoking for this frame.  Note: This may not be the
      * class where the implementation of the method lives.
      */
     private RubyModule klazz;
     
     /**
      * The 'self' for this frame.
      */
     private IRubyObject self;
     
     /**
      * The name of the method being invoked in this frame.  Note: Blocks are backed by frames
      * and do not have a name.
      */
     private String name;
     
     /**
      * The arguments passed into the method of this frame.   The frame captures arguments
      * so that they can be reused for things like super/zsuper.
      */
     private IRubyObject[] args;
 
     private int requiredArgCount;
 
     /**
      * The block that was passed in for this frame (as either a block or a &amp;block argument).
      * The frame captures the block for super/zsuper, but also for Proc.new (with no arguments)
      * and also for block_given?.  Both of those methods needs access to the block of the 
      * previous frame to work.
      */ 
     private Block block;
 
     /**
      * The current visibility for anything defined under this frame
      */
     private Visibility visibility = Visibility.PUBLIC;
+    
+    private Object jumpTarget;
+
+    public Object getJumpTarget() {
+        return jumpTarget;
+    }
+
+    public void setJumpTarget(Object jumpTarget) {
+        this.jumpTarget = jumpTarget;
+    }
 
     /**
      * The location in source where this block/method invocation is happening
      */
     private final ISourcePosition position;
 
     public Frame(ISourcePosition position) {
         this(null, null, null, IRubyObject.NULL_ARRAY, 0, Block.NULL_BLOCK, position); 
     }
 
     public Frame(RubyModule klazz, IRubyObject self, String name,
                  IRubyObject[] args, int requiredArgCount, Block block, ISourcePosition position) {
         assert block != null : "Block uses null object pattern.  It should NEVER be null";
         
         this.self = self;
         this.args = args;
         this.requiredArgCount = requiredArgCount;
         this.name = name;
         this.klazz = klazz;
         this.position = position;
         this.block = block;
     }
 
     /** Getter for property args.
      * @return Value of property args.
      */
     IRubyObject[] getArgs() {
         return args;
     }
 
     /** Setter for property args.
      * @param args New value of property args.
      */
     void setArgs(IRubyObject[] args) {
         this.args = args;
     }
 
     public int getRequiredArgCount() {
         return requiredArgCount;
     }
 
     void setRequiredArgCount(int req) {
         this.requiredArgCount = req;
     }
 
     /**
      * @return the frames current position
      */
     ISourcePosition getPosition() {
         return position;
     }
 
     /** 
      * Return class that we are supposedly calling for this invocation
      * 
      * @return the current class
      */
     RubyModule getKlazz() {
         return klazz;
     }
 
     /**
      * Set class that this method is supposedly calling on.  Note: This is different than
      * a native method's implementation class.
      * 
      * @param klazz the new class
      */
     public void setKlazz(RubyModule klazz) {
         this.klazz = klazz;
     }
 
     /**
      * Set the method name associated with this frame
      * 
      * @param name the new name
      */
     public void setName(String name) {
         this.name = name;
     }
 
     /** 
      * Get the method name associated with this frame
      * 
      * @return the method name
      */
     String getName() {
         return name;
     }
 
     /**
      * Get the self associated with this frame
      * 
      * @return the self
      */
     IRubyObject getSelf() {
         return self;
     }
 
     /** 
      * Set the self associated with this frame
      * 
      * @param self is the new value of self
      */
     void setSelf(IRubyObject self) {
         this.self = self;
     }
     
     /**
      * Get the visibility at the time of this frame
      * 
      * @return the visibility
      */
     public Visibility getVisibility() {
         return visibility;
     }
     
     /**
      * Change the visibility associated with this frame
      * 
      * @param visibility the new visibility
      */
     public void setVisibility(Visibility visibility) {
         this.visibility = visibility;
     }
     
     /**
      * What block is associated with this frame?
      * 
      * @return the block of this frame or NULL_BLOCK if no block given
      */
     public Block getBlock() {
         return block;
     }
 
     public Frame duplicate() {
         IRubyObject[] newArgs;
         if (args.length != 0) {
             newArgs = new IRubyObject[args.length];
             System.arraycopy(args, 0, newArgs, 0, args.length);
         } else {
         	newArgs = args;
         }
 
         return new Frame(klazz, self, name, newArgs, requiredArgCount, block, position);
     }
 
     /* (non-Javadoc)
      * @see java.lang.Object#toString()
      */
     public String toString() {
         StringBuffer sb = new StringBuffer(50);
         sb.append(position != null ? position.toString() : "-1");
         sb.append(':');
         sb.append(klazz + " " + name);
         if (name != null) {
             sb.append("in ");
             sb.append(name);
         }
         return sb.toString();
     }
 }
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index c37c5f429a..9792958826 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,810 +1,818 @@
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
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyThread;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.SourcePositionFactory;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * @author jpetersen
  */
 public class ThreadContext {
     public static synchronized ThreadContext newContext(Ruby runtime) {
         ThreadContext context = new ThreadContext(runtime);
         
         return context;
     }
     
     private final static int INITIAL_SIZE = 50;
     
     private final Ruby runtime;
     
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
     
     private RubyThread thread;
     
     //private UnsynchronizedStack parentStack;
     private RubyModule[] parentStack = new RubyModule[INITIAL_SIZE];
     private int parentIndex = -1;
     
     //private UnsynchronizedStack frameStack;
     private Frame[] frameStack = new Frame[INITIAL_SIZE];
     private int frameIndex = -1;
     //private UnsynchronizedStack crefStack;
     private SinglyLinkedList[] crefStack = new SinglyLinkedList[INITIAL_SIZE];
     private int crefIndex = -1;
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
     
     private String[] catchStack = new String[INITIAL_SIZE];
     private int catchIndex = -1;
     
     private int[] bindingFrameStack = new int[INITIAL_SIZE];
     private int bindingFrameIndex = -1;
     
     private RubyModule wrapper;
     
     private ISourcePosition sourcePosition = new SourcePositionFactory(null).getDummyPosition();
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         pushScope(new DynamicScope(new LocalStaticScope(null), null));
     }
     
     CallType lastCallType;
     
     public Ruby getRuntime() {
         return runtime;
     }
     
     /**
      * Returns the lastCallStatus.
      * @return LastCallStatus
      */
     public void setLastCallStatus(CallType callType) {
         lastCallType = callType;
     }
     
     public Visibility getLastVisibility() {
         return getPreviousFrame().getVisibility();
     }
     
     public CallType getLastCallType() {
         return lastCallType;
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
     
     private void expandFramesIfNecessary() {
         if (frameIndex + 1 == frameStack.length) {
             int newSize = frameStack.length * 2;
             Frame[] newFrameStack = new Frame[newSize];
             
             System.arraycopy(frameStack, 0, newFrameStack, 0, frameStack.length);
             
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
     
     private void expandCrefsIfNecessary() {
         if (crefIndex + 1 == crefStack.length) {
             int newSize = crefStack.length * 2;
             SinglyLinkedList[] newCrefStack = new SinglyLinkedList[newSize];
             
             System.arraycopy(crefStack, 0, newCrefStack, 0, crefStack.length);
             
             crefStack = newCrefStack;
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
     
     public IRubyObject getLastline() {
         return getCurrentScope().getLastLine();
     }
     
     public void setLastline(IRubyObject value) {
         getCurrentScope().setLastLine(value);
     }
     
     //////////////////// CATCH MANAGEMENT ////////////////////////
     private void expandCatchIfNecessary() {
         if (catchIndex + 1 == catchStack.length) {
             int newSize = catchStack.length * 2;
             String[] newCatchStack = new String[newSize];
             
             System.arraycopy(catchStack, 0, newCatchStack, 0, catchStack.length);
             catchStack = newCatchStack;
         }
     }
     
     public void pushCatch(String catchSymbol) {
         catchStack[++catchIndex] = catchSymbol;
         expandCatchIfNecessary();
     }
     
     public void popCatch() {
         catchIndex--;
     }
     
     public String[] getActiveCatches() {
         if (catchIndex < 0) return new String[0];
         
         String[] activeCatches = new String[catchIndex + 1];
         System.arraycopy(catchStack, 0, activeCatches, 0, catchIndex + 1);
         return activeCatches;
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         pushFrame(getCurrentFrame().duplicate());
     }
     
     private void pushCallFrame(RubyModule clazz, String name, 
                                IRubyObject self, IRubyObject[] args, int req, Block block) {
         pushFrame(new Frame(clazz, self, name, args, req, block, getPosition()));        
     }
     
     private void pushFrame() {
         pushFrame(new Frame(getPosition()));
     }
     
     private void pushFrame(Frame frame) {
         frameStack[++frameIndex] = frame;
         expandFramesIfNecessary();
     }
     
     private void popFrame() {
         Frame frame = (Frame)frameStack[frameIndex--];
         
         setPosition(frame.getPosition());
     }
     
     public Frame getCurrentFrame() {
         return (Frame)frameStack[frameIndex];
     }
     
     public Frame getPreviousFrame() {
         int size = frameIndex + 1;
         return size <= 1 ? null : (Frame) frameStack[size - 2];
     }
     
     public int getFrameCount() {
         return frameIndex + 1;
     }
     
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject[] getFrameArgs() {
         return getCurrentFrame().getArgs();
     }
     
     public void setFrameArgs(IRubyObject[] args) {
         getCurrentFrame().setArgs(args);
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
+    public Object getFrameJumpTarget() {
+        return getCurrentFrame().getJumpTarget();
+    }
+    
+    public void setFrameJumpTarget(Object target) {
+        getCurrentFrame().setJumpTarget(target);
+    }
+    
     public RubyModule getFrameKlazz() {
         return getCurrentFrame().getKlazz();
     }
     
     public ISourcePosition getFramePosition() {
         return getCurrentFrame().getPosition();
     }
     
     public ISourcePosition getPreviousFramePosition() {
         return getPreviousFrame().getPosition();
     }
     
     private void expandBindingFrameIfNecessary() {
         if (bindingFrameIndex + 1 == bindingFrameStack.length) {
             int newSize = bindingFrameStack.length * 2;
             int[] newbindingFrameStack = new int[newSize];
             
             System.arraycopy(bindingFrameStack, 0, newbindingFrameStack, 0, bindingFrameStack.length);
             bindingFrameStack = newbindingFrameStack;
         }
     }
     
     public void pushBindingFrame(int bindingDepth) {
         bindingFrameStack[++bindingFrameIndex] = bindingDepth;
         expandBindingFrameIfNecessary();
     }
     
     public void popBindingFrame() {
         bindingFrameIndex--;
     }
     
     
     public int currentBindingFrame() {
         if(bindingFrameIndex == -1) {
             return 0;
         } else {
             return bindingFrameStack[bindingFrameIndex];
         }
     }
     
     public ISourcePosition getPosition() {
         return sourcePosition;
     }
     
     public String getSourceFile() {
         return sourcePosition.getFile();
     }
     
     public int getSourceLine() {
         return sourcePosition.getEndLine();
     }
     
     public void setPosition(ISourcePosition position) {
         sourcePosition = position;
     }
     
     public IRubyObject getBackref() {
         IRubyObject value = getCurrentScope().getBackRef();
         
         // DynamicScope does not preinitialize these values since they are virtually
         // never used.
         return value == null ? runtime.getNil() : value;
     }
     
     public void setBackref(IRubyObject backref) {
         getCurrentScope().setBackRef(backref);
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
     
     public SinglyLinkedList peekCRef() {
         return (SinglyLinkedList)crefStack[crefIndex];
     }
     
     public void setCRef(SinglyLinkedList newCRef) {
         crefStack[++crefIndex] = newCRef;
         expandCrefsIfNecessary();
     }
     
     public void unsetCRef() {
         crefStack[crefIndex--] = null;
     }
     
     public SinglyLinkedList pushCRef(RubyModule newModule) {
         if (crefIndex == -1) {
             crefStack[++crefIndex] = new SinglyLinkedList(newModule, null);
         } else {
             crefStack[crefIndex] = new SinglyLinkedList(newModule, (SinglyLinkedList)crefStack[crefIndex]);
         }
         
         return (SinglyLinkedList)peekCRef();
     }
     
     public RubyModule popCRef() {
         assert !(crefIndex == -1) : "Tried to pop from empty CRef stack";
         
         RubyModule module = (RubyModule)peekCRef().getValue();
         
         SinglyLinkedList next = ((SinglyLinkedList)crefStack[crefIndex--]).getNext();
         
         if (next != null) {
             crefStack[++crefIndex] = next;
         } else {
             crefStack[crefIndex+1] = null;
         }
         
         return module;
     }
     
     public void pushRubyClass(RubyModule currentModule) {
         assert currentModule != null : "Can't push null RubyClass";
         
         parentStack[++parentIndex] = currentModule;
         expandParentsIfNecessary();
     }
     
     public RubyModule popRubyClass() {
         RubyModule ret = (RubyModule)parentStack[parentIndex];
         parentStack[parentIndex--] = null;
         return ret;
     }
     
     public RubyModule getRubyClass() {
         assert !(parentIndex == -1) : "Trying to get RubyClass from empty stack";
         
         RubyModule parentModule = (RubyModule)parentStack[parentIndex];
         
         return parentModule.getNonIncludedClass();
     }
     
     public RubyModule getBindingRubyClass() {
         RubyModule parentModule = null;
         if(parentIndex == 0) {
             parentModule = (RubyModule)parentStack[parentIndex];
         } else {
             parentModule = (RubyModule)parentStack[parentIndex-1];
             
         }
         return parentModule.getNonIncludedClass();
     }
     
     public boolean isTopLevel() {
         return parentIndex == 0;
     }
     
     public RubyModule getWrapper() {
         return wrapper;
     }
     
     public void setWrapper(RubyModule wrapper) {
         this.wrapper = wrapper;
     }
     
     public boolean getConstantDefined(String name) {
         IRubyObject result = null;
         
         // flipped from while to do to search current class first
         for (SinglyLinkedList cbase = peekCRef(); cbase != null; cbase = cbase.getNext()) {
             result = ((RubyModule) cbase.getValue()).getConstantAt(name);
             if (result != null || runtime.getLoadService().autoload(name) != null) {
                 return true;
             }
         }
         
         return false;
     }
     
     public IRubyObject getConstant(String name) {
         //RubyModule self = state.threadContext.getRubyClass();
         SinglyLinkedList cbase = peekCRef();
         RubyClass object = runtime.getObject();
         IRubyObject result = null;
         
         // flipped from while to do to search current class first
         do {
             RubyModule klass = (RubyModule) cbase.getValue();
             
             // Not sure how this can happen
             //if (NIL_P(klass)) return rb_const_get(CLASS_OF(self), id);
             result = klass.getConstantAt(name);
             if (result == null) {
                 if (runtime.getLoadService().autoload(name) != null) {
                     continue;
                 }
             } else {
                 return result;
             }
             cbase = cbase.getNext();
         } while (cbase != null && cbase.getValue() != object);
         
         return ((RubyModule) peekCRef().getValue()).getConstant(name);
     }
     
     public IRubyObject getConstant(String name, RubyModule module) {
         //RubyModule self = state.threadContext.getRubyClass();
         SinglyLinkedList cbase = module.getCRef();
         IRubyObject result = null;
         
         // flipped from while to do to search current class first
         do {
             RubyModule klass = (RubyModule) cbase.getValue();
             
             // Not sure how this can happen
             //if (NIL_P(klass)) return rb_const_get(CLASS_OF(self), id);
             result = klass.getConstantAt(name);
             if (result == null) {
                 if (runtime.getLoadService().autoload(name) != null) {
                     continue;
                 }
             } else {
                 return result;
             }
             cbase = cbase.getNext();
         } while (cbase != null);
         
         //System.out.println("CREF is " + state.threadContext.getCRef().getValue());
         return ((RubyModule) peekCRef().getValue()).getConstant(name);
     }
     
     private void addBackTraceElement(RubyArray backtrace, Frame frame, Frame previousFrame) {
         StringBuffer sb = new StringBuffer(100);
         ISourcePosition position = frame.getPosition();
         
         if(previousFrame != null && frame.getName() != null && previousFrame.getName() != null &&
                 frame.getName().equals(previousFrame.getName()) &&
                 frame.getPosition().getFile().equals(previousFrame.getPosition().getFile()) &&
                 frame.getPosition().getEndLine() == previousFrame.getPosition().getEndLine()) {
             return;
         }
         
         sb.append(position.getFile()).append(':').append(position.getEndLine());
         
         if (previousFrame != null && previousFrame.getName() != null) {
             sb.append(":in `").append(previousFrame.getName()).append('\'');
         } else if (previousFrame == null && frame.getName() != null) {
             sb.append(":in `").append(frame.getName()).append('\'');
         }
         
         backtrace.append(backtrace.getRuntime().newString(sb.toString()));
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public IRubyObject createBacktrace(int level, boolean nativeException) {
         RubyArray backtrace = runtime.newArray();
         int base = currentBindingFrame();
         int traceSize = frameIndex - level;
         
         if (traceSize <= 0) {
             return backtrace;
         }
         
         if (nativeException) {
             // assert level == 0;
             addBackTraceElement(backtrace, frameStack[frameIndex], null);
         }
         
         for (int i = traceSize; i > base; i--) {
             addBackTraceElement(backtrace, (Frame) frameStack[i], (Frame) frameStack[i-1]);
         }
         
         return backtrace;
     }
     
     public void preAdoptThread() {
         pushFrame();
         pushRubyClass(runtime.getObject());
         pushCRef(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
     
     public void preClassEval(StaticScope staticScope, RubyModule type) {
         pushCRef(type);
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         pushScope(new DynamicScope(staticScope, getCurrentScope()));
     }
     
     public void postClassEval() {
         popCRef();
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
 
     public void preMethodCall(RubyModule implementationClass, RubyModule clazz, 
                               IRubyObject self, String name, IRubyObject[] args, int req, Block block, boolean noSuper) {
         pushRubyClass((RubyModule)implementationClass.getCRef().getValue());
         pushCallFrame(noSuper ? null : clazz, name, self, args, req, block);
     }
     
     public void postMethodCall() {
         popFrame();
         popRubyClass();
     }
     
     public void preDefMethodInternalCall(RubyModule clazz, String name, 
                                          IRubyObject self, IRubyObject[] args, int req, Block block, boolean noSuper, 
             SinglyLinkedList cref, StaticScope staticScope) {
         RubyModule implementationClass = (RubyModule)cref.getValue();
         setCRef(cref);
         pushCallFrame(noSuper ? null : clazz, name, self, args, req, block);
         pushScope(new DynamicScope(staticScope, getCurrentScope()));
         pushRubyClass(implementationClass);
     }
     
     public void postDefMethodInternalCall() {
         popRubyClass();
         popScope();
         popFrame();
         unsetCRef();
     }
     
     public void preCompiledMethod(RubyModule implementationClass, SinglyLinkedList cref) {
         pushRubyClass(implementationClass);
         setCRef(cref);
     }
     
     public void postCompiledMethod() {
         popRubyClass();
         unsetCRef();
     }
     
     // NEW! Push a scope into the frame, since this is now required to use it
     // XXX: This is screwy...apparently Ruby runs internally-implemented methods in their own frames but in the *caller's* scope
     public void preReflectedMethodInternalCall(RubyModule implementationClass, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, int req, boolean noSuper, Block block) {
         pushRubyClass((RubyModule)implementationClass.getCRef().getValue());
         pushCallFrame(noSuper ? null : klazz, name, self, args, req, block);
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postReflectedMethodInternalCall() {
         popFrame();
         popRubyClass();
     }
     
     public void preInitCoreClasses() {
         pushFrame();
         setCurrentVisibility(Visibility.PRIVATE);
     }
     
     public void preInitBuiltinClasses(RubyClass objectClass, IRubyObject topSelf) {
         pushRubyClass(objectClass);
         setCRef(objectClass.getCRef());
         
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
     }
     
     public void preNodeEval(RubyModule newWrapper, RubyModule rubyClass, IRubyObject self) {
         setWrapper(newWrapper);
         pushRubyClass(rubyClass);
         pushCallFrame(null, null, self, IRubyObject.NULL_ARRAY, 0, Block.NULL_BLOCK);
         
         setCRef(rubyClass.getCRef());
     }
     
     public void postNodeEval(RubyModule newWrapper) {
         popFrame();
         popRubyClass();
         setWrapper(newWrapper);
         unsetCRef();
     }
     
     // XXX: Again, screwy evaling under previous frame's scope
     public void preExecuteUnder(RubyModule executeUnderClass, Block block) {
         Frame frame = getCurrentFrame();
         
         pushRubyClass(executeUnderClass);
         pushCRef(executeUnderClass);
         pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), frame.getArgs(), frame.getRequiredArgCount(), block);
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postExecuteUnder() {
         popFrame();
         popRubyClass();
         popCRef();
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
         pushFrame();
     }
     
     public void postTrace() {
         popFrame();
     }
     
     public void preForBlock(Block block, RubyModule klass) {
         pushFrame(block.getFrame());
         setCRef(block.getCRef());
         getCurrentFrame().setVisibility(block.getVisibility());
         pushScope(block.getDynamicScope());
         pushRubyClass((klass != null) ? klass : block.getKlass());
     }
     
     public void preYieldSpecificBlock(Block block, RubyModule klass) {
         //System.out.println("IN RESTORE BLOCK (" + block.getDynamicScope() + ")");
         pushFrame(block.getFrame());
         setCRef(block.getCRef());
         getCurrentFrame().setVisibility(block.getVisibility());
         pushScope(block.getDynamicScope().cloneScope());
         pushRubyClass((klass != null) ? klass : block.getKlass());
     }
     
     public void preEvalWithBinding(Block block) {
         pushBindingFrame(frameIndex);
         pushFrame(block.getFrame());
         setCRef(block.getCRef());
         getCurrentFrame().setVisibility(block.getVisibility());
         pushRubyClass(block.getKlass());
     }
     
     public void postEvalWithBinding() {
         popFrame();
         unsetCRef();
         popRubyClass();
         popBindingFrame();
     }
     
     public void postYield() {
         popScope();
         popFrame();
         unsetCRef();
         popRubyClass();
     }
     
     public void preRootNode(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postRootNode() {
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
diff --git a/test/testEval.rb b/test/testEval.rb
index fd384b37bd..f7161835cc 100644
--- a/test/testEval.rb
+++ b/test/testEval.rb
@@ -1,145 +1,160 @@
 require 'test/minirunit'
 
 # ensure binding is setting self correctly
 def x
   "foo"
 end
 
 Z = binding
 
 class A
   def x
     "bar"
   end
 
   def y
     eval("x", Z)
   end
 end
 
 old_self = self
 test_equal(A.new.y, "foo")
 test_equal(x, "foo")
 
 #### ensure self is back to pre bound eval
 test_equal(self, old_self)
 
 #### ensure returns within ensures that cross evalstates during an eval are handled properly (whew!)
 def inContext &proc 
    begin
      proc.call
    ensure
    end
 end
 
 def x2
   inContext do
      return "foo"
   end
 end
 
 test_equal(x2, "foo")
 
 # test that evaling a proc doesn't goof up the module nesting for a binding
 proc_binding = eval("proc{binding}.call", TOPLEVEL_BINDING)
 nesting = eval("$nesting = nil; class A; $nesting = Module.nesting; end; $nesting", TOPLEVEL_BINDING)
 test_equal("A", nesting.to_s)
 
 class Foo
   def initialize(p)
     @prefix = p
   end
 
   def result(val)
     redefine_result
     result val
   end
   
   def redefine_result
     method_decl = "def result(val); \"#{@prefix}: \#\{val\}\"; end"
     instance_eval method_decl, "generated code (#{__FILE__}:#{__LINE__})"
   end
 end
 
 f = Foo.new("foo")
 test_equal "foo: hi", f.result("hi")
 
 g = Foo.new("bar")
 test_equal "bar: hi", g.result("hi")
 
 test_equal "foo: bye", f.result("bye")
 test_equal "bar: bye", g.result("bye")
 
 # JRUBY-214 - eval should call to_str on arg 0
 class Bar
   def to_str
     "magic_number"
   end
 end
 magic_number = 1
 test_equal(magic_number, eval(Bar.new))
 
 test_exception(TypeError) { eval(Object.new) }
 
 # JRUBY-386 tests
 # need at least one arg
 test_exception(ArgumentError) { eval }
 test_exception(ArgumentError) {self.class.module_eval}
 test_exception(ArgumentError) {self.class.class_eval}
 test_exception(ArgumentError) {3.instance_eval}
 
 # args must respond to #to_str
 test_exception(TypeError) {eval 3}
 test_exception(TypeError) {self.class.module_eval 3}
 test_exception(TypeError) {self.class.class_eval 4}
 test_exception(TypeError) {3.instance_eval 4}
 
 begin
   eval 'return'
 rescue LocalJumpError => e
   test_ok(e.message =~ /unexpected return$/)
 end
 
 begin
   eval 'break'
 rescue LocalJumpError => e
   test_ok(e.message =~ /unexpected break$/)
 end
 
 begin
   "".instance_eval 'break'
 rescue LocalJumpError => e
   test_ok(e.message =~ /unexpected break$/)
 end
 
 begin
   "".instance_eval 'return'
 rescue LocalJumpError => e
   test_ok(e.message =~ /unexpected return$/)
 end
 
 # If getBindingRubyClass isn't used, this test case will fail,
 # since when eval gets called, Kernel will get pushed on the
 # parent-stack, and this will always become the RubyClass for
 # the evaled string, which is incorrect.
 class AbcTestFooAbc
   eval <<-ENDT
   def foofoo_foofoo
   end
 ENDT
 end
 
 test_equal ["foofoo_foofoo"], AbcTestFooAbc.instance_methods.grep(/foofoo_foofoo/)
 test_equal [], Object.instance_methods.grep(/foofoo_foofoo/)
 
 # test Binding.of_caller
 def foo
   x = 1
   bar
 end
 
 def bar
   eval "x + 1", Binding.of_caller
 end
 
-test_equal(2, foo)
\ No newline at end of file
+test_equal(2, foo)
+
+# test returns within an eval
+def foo
+  eval 'return 1'
+  return 2
+end
+def foo2
+  x = "blah"
+  x.instance_eval "return 1"
+  return 2
+end
+
+test_equal(1, foo)
+# this case is still broken
+#test_equal(1, foo2)
\ No newline at end of file
