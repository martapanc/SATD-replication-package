diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index b684587cd5..e8c2eb80f5 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1082 +1,1079 @@
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
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.StringTokenizer;
 import java.util.regex.Pattern;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicMethod;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.meta.FileMetaClass;
 import org.jruby.runtime.builtin.meta.IOMetaClass;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.PrintfFormat;
 import org.jruby.util.UnsynchronizedStack;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  *
  * @author jpetersen
  */
 public class RubyKernel {
     public static RubyModule createKernelModule(IRuby runtime) {
-        // FIXME: All these "defineFastModuleFunction" calls are resulting in Kernel being
-        // singltonized, and these methods are defined on the singleton. This does not seem right,
-        // but just using Kernel's metaclass did not seem to work. Examine this and determine correct behavior.
         RubyModule module = runtime.defineModule("Kernel");
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyKernel.class);
         CallbackFactory objectCallbackFactory = runtime.callbackFactory(RubyObject.class);
 
         module.defineFastModuleFunction("Array", callbackFactory.getFastSingletonMethod("new_array", IRubyObject.class));
         module.defineFastModuleFunction("Float", callbackFactory.getFastSingletonMethod("new_float", IRubyObject.class));
         module.defineFastModuleFunction("Integer", callbackFactory.getFastSingletonMethod("new_integer", IRubyObject.class));
         module.defineFastModuleFunction("String", callbackFactory.getFastSingletonMethod("new_string", IRubyObject.class));
         module.defineFastModuleFunction("`", callbackFactory.getFastSingletonMethod("backquote", IRubyObject.class));
         module.defineFastModuleFunction("abort", callbackFactory.getFastOptSingletonMethod("abort"));
         module.defineModuleFunction("at_exit", callbackFactory.getSingletonMethod("at_exit"));
         module.defineFastModuleFunction("autoload", callbackFactory.getFastSingletonMethod("autoload", IRubyObject.class, IRubyObject.class));
         module.defineFastPublicModuleFunction("autoload?", callbackFactory.getFastSingletonMethod("autoload_p", IRubyObject.class));
         module.defineModuleFunction("binding", callbackFactory.getSingletonMethod("binding"));
         module.defineModuleFunction("block_given?", callbackFactory.getSingletonMethod("block_given"));
         // TODO: Implement Kernel#callcc
         module.defineModuleFunction("caller", callbackFactory.getOptSingletonMethod("caller"));
         module.defineModuleFunction("catch", callbackFactory.getSingletonMethod("rbCatch", IRubyObject.class));
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
         module.defineFastModuleFunction("puts", callbackFactory.getFastOptSingletonMethod("puts"));
         module.defineModuleFunction("raise", callbackFactory.getOptSingletonMethod("raise"));
         module.defineFastModuleFunction("rand", callbackFactory.getFastOptSingletonMethod("rand"));
         module.defineFastModuleFunction("readline", callbackFactory.getFastOptSingletonMethod("readline"));
         module.defineFastModuleFunction("readlines", callbackFactory.getFastOptSingletonMethod("readlines"));
         module.defineModuleFunction("require", callbackFactory.getSingletonMethod("require", IRubyObject.class));
         module.defineModuleFunction("scan", callbackFactory.getSingletonMethod("scan", IRubyObject.class));
         module.defineFastModuleFunction("select", callbackFactory.getFastOptSingletonMethod("select"));
         module.defineModuleFunction("set_trace_func", callbackFactory.getSingletonMethod("set_trace_func", IRubyObject.class));
         module.defineFastModuleFunction("sleep", callbackFactory.getFastSingletonMethod("sleep", IRubyObject.class));
         module.defineFastModuleFunction("split", callbackFactory.getFastOptSingletonMethod("split"));
         module.defineFastModuleFunction("sprintf", callbackFactory.getFastOptSingletonMethod("sprintf"));
         module.defineFastModuleFunction("srand", callbackFactory.getFastOptSingletonMethod("srand"));
         module.defineModuleFunction("sub", callbackFactory.getOptSingletonMethod("sub"));
         module.defineModuleFunction("sub!", callbackFactory.getOptSingletonMethod("sub_bang"));
         // Skipping: Kernel#syscall (too system dependent)
         module.defineFastModuleFunction("system", callbackFactory.getFastOptSingletonMethod("system"));
         // TODO: Implement Kernel#exec differently?
         module.defineFastModuleFunction("exec", callbackFactory.getFastOptSingletonMethod("system"));
         // TODO: Implement Kernel#test (partial impl)
         module.defineModuleFunction("throw", callbackFactory.getOptSingletonMethod("rbThrow"));
         // TODO: Implement Kernel#trace_var
         module.defineModuleFunction("trap", callbackFactory.getOptSingletonMethod("trap"));
         // TODO: Implement Kernel#untrace_var
         module.defineFastModuleFunction("warn", callbackFactory.getFastSingletonMethod("warn", IRubyObject.class));
         
         // Defined p411 Pickaxe 2nd ed.
         module.defineModuleFunction("singleton_method_added", callbackFactory.getSingletonMethod("singleton_method_added", IRubyObject.class));
         
         // Object methods
         module.defineFastPublicModuleFunction("==", objectCallbackFactory.getFastMethod("obj_equal", IRubyObject.class));
         module.defineFastPublicModuleFunction("===", objectCallbackFactory.getFastMethod("equal", IRubyObject.class));
 
         module.defineAlias("eql?", "==");
         module.defineFastPublicModuleFunction("to_s", objectCallbackFactory.getFastMethod("to_s"));
         module.defineFastPublicModuleFunction("nil?", objectCallbackFactory.getFastMethod("nil_p"));
         module.defineFastPublicModuleFunction("to_a", callbackFactory.getFastSingletonMethod("to_a"));
         module.defineFastPublicModuleFunction("hash", objectCallbackFactory.getFastMethod("hash"));
         module.defineFastPublicModuleFunction("id", objectCallbackFactory.getFastMethod("id"));
         module.defineAlias("__id__", "id");
         module.defineAlias("object_id", "id");
         module.defineFastPublicModuleFunction("is_a?", objectCallbackFactory.getFastMethod("kind_of", IRubyObject.class));
         module.defineAlias("kind_of?", "is_a?");
         module.defineFastPublicModuleFunction("dup", objectCallbackFactory.getFastMethod("dup"));
         module.defineFastPublicModuleFunction("equal?", objectCallbackFactory.getFastMethod("same", IRubyObject.class));
         module.defineFastPublicModuleFunction("type", objectCallbackFactory.getFastMethod("type_deprecated"));
         module.defineFastPublicModuleFunction("class", objectCallbackFactory.getFastMethod("type"));
         module.defineFastPublicModuleFunction("inspect", objectCallbackFactory.getFastMethod("inspect"));
         module.defineFastPublicModuleFunction("=~", objectCallbackFactory.getFastMethod("match", IRubyObject.class));
         module.defineFastPublicModuleFunction("clone", objectCallbackFactory.getFastMethod("rbClone"));
         module.defineFastPublicModuleFunction("display", objectCallbackFactory.getFastOptMethod("display"));
         module.defineFastPublicModuleFunction("extend", objectCallbackFactory.getFastOptMethod("extend"));
         module.defineFastPublicModuleFunction("freeze", objectCallbackFactory.getFastMethod("freeze"));
         module.defineFastPublicModuleFunction("frozen?", objectCallbackFactory.getFastMethod("frozen"));
         module.defineFastModuleFunction("initialize_copy", objectCallbackFactory.getFastMethod("initialize_copy", IRubyObject.class));
         module.definePublicModuleFunction("instance_eval", objectCallbackFactory.getOptMethod("instance_eval"));
         module.defineFastPublicModuleFunction("instance_of?", objectCallbackFactory.getFastMethod("instance_of", IRubyObject.class));
         module.defineFastPublicModuleFunction("instance_variables", objectCallbackFactory.getFastMethod("instance_variables"));
         module.defineFastPublicModuleFunction("instance_variable_get", objectCallbackFactory.getFastMethod("instance_variable_get", IRubyObject.class));
         module.defineFastPublicModuleFunction("instance_variable_set", objectCallbackFactory.getFastMethod("instance_variable_set", IRubyObject.class, IRubyObject.class));
         module.defineFastPublicModuleFunction("method", objectCallbackFactory.getFastMethod("method", IRubyObject.class));
         module.defineFastPublicModuleFunction("methods", objectCallbackFactory.getFastOptMethod("methods"));
         module.defineFastPublicModuleFunction("private_methods", objectCallbackFactory.getFastMethod("private_methods"));
         module.defineFastPublicModuleFunction("protected_methods", objectCallbackFactory.getFastMethod("protected_methods"));
         module.defineFastPublicModuleFunction("public_methods", objectCallbackFactory.getFastOptMethod("public_methods"));
         module.defineFastModuleFunction("remove_instance_variable", objectCallbackFactory.getMethod("remove_instance_variable", IRubyObject.class));
         module.defineFastPublicModuleFunction("respond_to?", objectCallbackFactory.getFastOptMethod("respond_to"));
         module.definePublicModuleFunction("send", objectCallbackFactory.getOptMethod("send"));
         module.defineAlias("__send__", "send");
         module.defineFastPublicModuleFunction("singleton_methods", objectCallbackFactory.getFastOptMethod("singleton_methods"));
         module.defineFastPublicModuleFunction("taint", objectCallbackFactory.getFastMethod("taint"));
         module.defineFastPublicModuleFunction("tainted?", objectCallbackFactory.getFastMethod("tainted"));
         module.defineFastPublicModuleFunction("untaint", objectCallbackFactory.getFastMethod("untaint"));
 
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
              * @see org.jruby.runtime.load.IAutoloadMethod#load(IRuby, String)
              */
             public IRubyObject load(IRuby runtime, String name) {
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
         IRuby runtime = recv.getRuntime();
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = recv.callMethod(runtime.getCurrentContext(), "inspect").toString();
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = runtime.getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = new PrintfFormat(format).sprintf(new Object[] { name, description, 
             noClass ? "" : ":", noClass ? "" : recv.getType().getName()});
 
         throw lastCallType == CallType.VARIABLE ? runtime.newNameError(msg, name) : runtime.newNoMethodError(msg, name);
     }
 
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         String arg = args[0].convertToString().toString();
 
         // Should this logic be pushed into RubyIO Somewhere?
         if (arg.startsWith("|")) {
             String command = arg.substring(1);
         	// exec process, create IO with process
         	try {
                 // TODO: may need to part cli parms out ourself?
                 Process p = Runtime.getRuntime().exec(command,getCurrentEnv(recv.getRuntime()));
                 RubyIO io = new RubyIO(recv.getRuntime(), p);
         		
         	    if (block != null) {
         	        try {
                         recv.getRuntime().getCurrentContext().yield(io, block);
         	            
             	        return recv.getRuntime().getNil();
         	        } finally {
         	            io.close();
         	        }
         	    }
 
                 return io;
         	} catch (IOException ioe) {
         		throw recv.getRuntime().newIOErrorFromException(ioe);
         	}
         } 
 
         return ((FileMetaClass) recv.getRuntime().getClass("File")).open(args, block);
     }
 
     public static IRubyObject gets(IRubyObject recv, IRubyObject[] args) {
         RubyArgsFile argsFile = (RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<");
 
         IRubyObject line = argsFile.internalGets(args);
 
         recv.getRuntime().getCurrentContext().setLastline(line);
 
         return line;
     }
 
     public static IRubyObject abort(IRubyObject recv, IRubyObject[] args) {
         if(recv.checkArgumentCount(args,0,1) == 1) {
             recv.getRuntime().getGlobalVariables().get("$stderr").callMethod(recv.getRuntime().getCurrentContext(),"puts",args[0]);
         }
         throw new MainExitException(1,true);
     }
 
     public static IRubyObject new_array(IRubyObject recv, IRubyObject object) {
         IRubyObject value = object.convertToTypeWithCheck("Array", "to_ary");
         
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
             return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
             RubyFloat rFloat = object.convertToFloat();
             if(rFloat.getDoubleValue() == Double.NaN){
                 recv.getRuntime().newTypeError("invalid value for Float()");
         }
             return rFloat;
     }
     }
     
     public static IRubyObject new_integer(IRubyObject recv, IRubyObject object) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if(object instanceof RubyString) {
             String val = object.toString();
             if(val.length() > 0 && val.charAt(0) == '0') {
                 if(val.length() > 1) {
                     if(val.charAt(1) == 'x') {
                         return RubyNumeric.str2inum(recv.getRuntime(),recv.getRuntime().newString(val.substring(2)),16,true);
                     } else if(val.charAt(1) == 'b') {
                         return RubyNumeric.str2inum(recv.getRuntime(),recv.getRuntime().newString(val.substring(2)),2,true);
                     } else {
                         return RubyNumeric.str2inum(recv.getRuntime(),recv.getRuntime().newString(val.substring(1)),8,true);
                     }
                 }
             }
             return RubyNumeric.str2inum(recv.getRuntime(),(RubyString)object,10,true);
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
         RubyArgsFile argsFile = (RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<");
 
         RubyArray lines = recv.getRuntime().newArray();
 
         IRubyObject line = argsFile.internalGets(args);
         while (!line.isNil()) {
             lines.append(line);
 
             line = argsFile.internalGets(args);
         }
 
         return lines;
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(IRuby runtime) {
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
         try {
         	rubyThread.sleep(milliseconds);
         } catch (InterruptedException iExcptn) {
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
         final IRuby runtime = recv.getRuntime();
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
         return recv.getRuntime().newBoolean(recv.getRuntime().getCurrentContext().getPreviousFrame().getBlock() != null);
     }
 
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         RubyArray newArgs = recv.getRuntime().newArray(args);
         newArgs.shift();
 
         return str.format(newArgs);
     }
 
     public static IRubyObject raise(IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         recv.checkArgumentCount(args, 0, 3); 
         IRuby runtime = recv.getRuntime();
 
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
 
         src.checkSafeString();
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (scope == null) {
             scope = recv.getRuntime().newBinding();
         }
         
         return recv.evalWithBinding(context, src, scope, file);
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
             return context.yield(tag, block);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ThrowJump) {
 	            if (je.getPrimaryData().equals(tag.asSymbol())) {
 	                return (IRubyObject)je.getSecondaryData();
 	            }
         	}
        		throw je;
         } finally {
        		context.popCatch();
         }
     }
 
     public static IRubyObject rbThrow(IRubyObject recv, IRubyObject[] args, Block block) {
         IRuby runtime = recv.getRuntime();
 
         String tag = args[0].asSymbol();
         String[] catches = runtime.getCurrentContext().getActiveCatches();
 
         String message = "uncaught throw '" + tag + '\'';
 
         //Ordering of array traversal not important, just intuitive
         for (int i = catches.length - 1 ; i >= 0 ; i--) {
             if (tag.equals(catches[i])) {
                 //Catch active, throw for catch to handle
                 JumpException je = new JumpException(JumpException.JumpType.ThrowJump);
 
                 je.setPrimaryData(tag);
                 je.setSecondaryData(args.length > 1 ? args[1] : runtime.getNil());
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
 
     public static RubyProc proc(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(true, block);
     }
 
     public static IRubyObject loop(IRubyObject recv, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         while (true) {
             context.yield(recv.getRuntime().getNil(), block);
 
             Thread.yield();
         }
     }
 
     public static IRubyObject backquote(IRubyObject recv, IRubyObject aString) {
         IRuby runtime = recv.getRuntime();
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         
         int resultCode = runInShell(runtime, new IRubyObject[] {aString}, output);
         
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         
         return recv.getRuntime().newString(output.toString());
     }
     
     private static final Pattern PATH_SEPARATORS = Pattern.compile("[/\\\\]");
     
     /**
      * For the first full token on the command, most likely the actual executable to run, replace
      * all dir separators with that which is appropriate for the current platform. Return the new
      * with this executable string at the beginning.
      * 
      * @param command The all-forward-slashes command to be "fixed"
      * @return The "fixed" full command line
      */
     private static String repairDirSeps(String command) {
         String executable = "", remainder = "";
         command = command.trim();
         if (command.startsWith("'")) {
             String [] tokens = command.split("'", 3);
             executable = "'"+tokens[1]+"'";
             if (tokens.length > 2)
                 remainder = tokens[2];
         } else if (command.startsWith("\"")) {
             String [] tokens = command.split("\"", 3);
             executable = "\""+tokens[1]+"\"";
             if (tokens.length > 2)
                 remainder = tokens[2];
         } else {
             String [] tokens = command.split(" ", 2);
             executable = tokens[0];
             if (tokens.length > 1)
                 remainder = " "+tokens[1];
         }
         
         // Matcher.replaceAll treats backslashes in the replacement string as escaped characters
         String replacement = File.separator;
         if (File.separatorChar == '\\')
             replacement = "\\\\";
             
         return PATH_SEPARATORS.matcher(executable).replaceAll(replacement) + remainder;
                 }
 
     private static List parseCommandLine(IRubyObject[] rawArgs) {
         // first parse the first element of rawArgs since this may contain
         // the whole command line
         String command = rawArgs[0].toString();
         UnsynchronizedStack args = new UnsynchronizedStack();
         StringTokenizer st = new StringTokenizer(command, " ");
         String quoteChar = null;
 
         while (st.hasMoreTokens()) {
             String token = st.nextToken();
             if (quoteChar == null) {
                 // not currently in the middle of a quoted token
                 if (token.startsWith("'") || token.startsWith("\"")) {
                     // note quote char and remove from beginning of token
                     quoteChar = token.substring(0, 1);
                     token = token.substring(1);
                 }
                 if (quoteChar!=null && token.endsWith(quoteChar)) {
                     // quoted token self contained, remove from end of token
                     token = token.substring(0, token.length()-1);
                     quoteChar = null;
                 }
                 // add new token to list
                 args.push(token);
             } else {
                 // in the middle of quoted token
                 if (token.endsWith(quoteChar)) {
                     // end of quoted token
                     token = token.substring(0, token.length()-1);
                     quoteChar = null;
                 }
                 // update token at end of list
                 token = args.pop() + " " + token;
                 args.push(token);
             }
         }
         
         // now append the remaining raw args to the cooked arg list
         for (int i=1;i<rawArgs.length;i++) {
             args.push(rawArgs[i].toString());
         }
         
         return args;
     }
         
     private static boolean isRubyCommand(String command) {
         command = command.trim();
         String [] spaceDelimitedTokens = command.split(" ", 2);
         String [] slashDelimitedTokens = spaceDelimitedTokens[0].split("/");
         String finalToken = slashDelimitedTokens[slashDelimitedTokens.length-1];
         if (finalToken.indexOf("ruby") != -1 || finalToken.endsWith(".rb") || finalToken.indexOf("irb") != -1)
             return true;
         else
             return false;
     }
     
     private static class InProcessScript extends Thread {
     	private String[] argArray;
     	private InputStream in;
     	private PrintStream out;
     	private PrintStream err;
     	private int result;
     	private File dir;
     	
     	public InProcessScript(String[] argArray, InputStream in, PrintStream out, PrintStream err, File dir) {
     		this.argArray = argArray;
     		this.in = in;
     		this.out = out;
     		this.err = err;
     		this.dir = dir;
     	}
 
 		public int getResult() {
 			return result;
 		}
 
 		public void setResult(int result) {
 			this.result = result;
 		}
 		
         public void run() {
             String oldDir = System.getProperty("user.dir");
             try {
                 System.setProperty("user.dir", dir.getAbsolutePath());
                 result = new Main(in, out, err).run(argArray);
             } finally {
                 System.setProperty("user.dir", oldDir);
             }
         }
     }
 
     public static int runInShell(IRuby runtime, IRubyObject[] rawArgs) {
         return runInShell(runtime,rawArgs,runtime.getOutputStream());
     }
 
     private static String[] getCurrentEnv(IRuby runtime) {
         Map h = ((RubyHash)runtime.getObject().getConstant("ENV")).getValueMap();
         String[] ret = new String[h.size()];
         int i=0;
         for(Iterator iter = h.entrySet().iterator();iter.hasNext();i++) {
             Map.Entry e = (Map.Entry)iter.next();
             ret[i] = e.getKey().toString() + "=" + e.getValue().toString();
         }
         return ret;
     }
 
     public static int runInShell(IRuby runtime, IRubyObject[] rawArgs, OutputStream output) {
         OutputStream error = runtime.getErrorStream();
         InputStream input = runtime.getInputStream();
         try {
             String shell = runtime.evalScript("require 'rbconfig'; Config::CONFIG['SHELL']").toString();
             rawArgs[0] = runtime.newString(repairDirSeps(rawArgs[0].toString()));
             Process aProcess = null;
             InProcessScript ipScript = null;
             File pwd = new File(runtime.evalScript("Dir.pwd").toString());
             
             if (isRubyCommand(rawArgs[0].toString())) {
                 List args = parseCommandLine(rawArgs);
                 String command = (String)args.get(0);
 
                 // snip off ruby or jruby command from list of arguments
                 // leave alone if the command is the name of a script
                 int startIndex = command.endsWith(".rb") ? 0 : 1;
                 if(command.trim().endsWith("irb")) {
                     startIndex = 0;
                     args.set(0,runtime.getJRubyHome() + File.separator + "bin" + File.separator + "jirb");
                 }
                 String[] argArray = (String[])args.subList(startIndex,args.size()).toArray(new String[0]);
                 ipScript = new InProcessScript(argArray, input, new PrintStream(output), new PrintStream(error), pwd);
                 
                 // execute ruby command in-process
                 ipScript.start();
                 ipScript.join();
             } else if (shell != null && rawArgs.length == 1) {
                 // execute command with sh -c or cmd.exe /c
                 // this does shell expansion of wildcards
                 String shellSwitch = shell.endsWith("sh") ? "-c" : "/c";
                 String[] argArray = new String[3];
                 argArray[0] = shell;
                 argArray[1] = shellSwitch;
                 argArray[2] = rawArgs[0].toString();
                 aProcess = Runtime.getRuntime().exec(argArray, getCurrentEnv(runtime), pwd);
             } else {
                 // execute command directly, no wildcard expansion
                 if (rawArgs.length > 1) {
                     String[] argArray = new String[rawArgs.length];
                     for (int i=0;i<rawArgs.length;i++) {
                         argArray[i] = rawArgs[i].toString();
                     }
                     aProcess = Runtime.getRuntime().exec(argArray,getCurrentEnv(runtime), pwd);
                 } else {
                     aProcess = Runtime.getRuntime().exec(rawArgs[0].toString(), getCurrentEnv(runtime), pwd);
                 }
             }
             
             if (aProcess != null) {
                 handleStreams(aProcess,input,output,error);
                 return aProcess.waitFor();
             } else if (ipScript != null) {
             	return ipScript.getResult();
             } else {
                 return 0;
             }
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
     
     private static void handleStreams(Process p, InputStream in, OutputStream out, OutputStream err) throws IOException {
         InputStream pOut = p.getInputStream();
         InputStream pErr = p.getErrorStream();
         OutputStream pIn = p.getOutputStream();
 
         boolean done = false;
         int b;
         boolean proc = false;
         while(!done) {
             if(pOut.available() > 0) {
                 byte[] input = new byte[pOut.available()];
                 if((b = pOut.read(input)) == -1) {
                     done = true;
                 } else {
                     out.write(input);
                 }
                 proc = true;
             }
             if(pErr.available() > 0) {
                 byte[] input = new byte[pErr.available()];
                 if((b = pErr.read(input)) != -1) {
                     err.write(input);
                 }
                 proc = true;
             }
             if(in.available() > 0) {
                 byte[] input = new byte[in.available()];
                 if((b = in.read(input)) != -1) {
                     pIn.write(input);
                 }
                 proc = true;
             }
             if(!proc) {
                 if((b = pOut.read()) == -1) {
                     if((b = pErr.read()) == -1) {
                         done = true;
                     } else {
                         err.write(b);
                     }
                 } else {
                     out.write(b);
                 }
             }
             proc = false;
         }
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index e33cd34fd4..02fb0a2268 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -1,1634 +1,1623 @@
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006-2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.jruby.internal.runtime.methods.AliasMethod;
 import org.jruby.internal.runtime.methods.FullFunctionCallbackMethod;
 import org.jruby.internal.runtime.methods.SimpleCallbackMethod;
 import org.jruby.internal.runtime.methods.MethodMethod;
 import org.jruby.internal.runtime.methods.ProcMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.DynamicMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.util.collections.SinglyLinkedList;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.ClassIndex;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyModule extends RubyObject {
     private static final String CVAR_TAINT_ERROR =
         "Insecure: can't modify class variable";
     private static final String CVAR_FREEZE_ERROR = "class/module";
 
     // superClass may be null.
     private RubyClass superClass;
 
     public int index;
 
     // Containing class...The parent of Object is null. Object should always be last in chain.
     //public RubyModule parentModule;
 
     // CRef...to eventually replace parentModule
     public SinglyLinkedList cref;
 
     // ClassId is the name of the class/module sans where it is located.
     // If it is null, then it an anonymous class.
     private String classId;
 
     // All methods and all CACHED methods for the module.  The cached methods will be removed
     // when appropriate (e.g. when method is removed by source class or a new method is added
     // with same name by one of its subclasses).
     private Map methods = new HashMap();
 
     protected RubyModule(IRuby runtime, RubyClass metaClass, RubyClass superClass, SinglyLinkedList parentCRef, String name) {
         super(runtime, metaClass);
 
         this.superClass = superClass;
 
         setBaseName(name);
 
         // If no parent is passed in, it is safe to assume Object.
         if (parentCRef == null) {
             if (runtime.getObject() != null) {
                 parentCRef = runtime.getObject().getCRef();
             }
         }
         this.cref = new SinglyLinkedList(this, parentCRef);
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.MODULE;
     }
 
     /** Getter for property superClass.
      * @return Value of property superClass.
      */
     public RubyClass getSuperClass() {
         return superClass;
     }
 
     private void setSuperClass(RubyClass superClass) {
         this.superClass = superClass;
     }
 
     public RubyModule getParent() {
         if (cref.getNext() == null) {
             return null;
         }
 
         return (RubyModule)cref.getNext().getValue();
     }
 
     public void setParent(RubyModule p) {
         cref.setNext(p.getCRef());
     }
 
     public Map getMethods() {
         return methods;
     }
 
     public boolean isModule() {
         return true;
     }
 
     public boolean isClass() {
         return false;
     }
 
     public boolean isSingleton() {
         return false;
     }
 
     /**
      * Is this module one that in an included one (e.g. an IncludedModuleWrapper). 
      */
     public boolean isIncluded() {
         return false;
     }
 
     public RubyModule getNonIncludedClass() {
         return this;
     }
 
     public String getBaseName() {
         return classId;
     }
 
     public void setBaseName(String name) {
         classId = name;
     }
 
     /**
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (getBaseName() == null) {
             if (isClass()) {
                 return "#<" + "Class" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             } else {
                 return "#<" + "Module" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             }
         }
 
         StringBuffer result = new StringBuffer(getBaseName());
         RubyClass objectClass = getRuntime().getObject();
 
         for (RubyModule p = this.getParent(); p != null && p != objectClass; p = p.getParent()) {
             result.insert(0, "::").insert(0, p.getBaseName());
         }
 
         return result.toString();
     }
 
     /**
      * Create a wrapper to use for including the specified module into this one.
      * 
      * Ruby C equivalent = "include_class_new"
      * 
      * @return The module wrapper
      */
     public IncludedModuleWrapper newIncludeClass(RubyClass superClazz) {
         IncludedModuleWrapper includedModule = new IncludedModuleWrapper(getRuntime(), superClazz, this);
 
         // include its parent (and in turn that module's parents)
         if (getSuperClass() != null) {
             includedModule.includeModule(getSuperClass());
         }
 
         return includedModule;
     }
 
     /**
      * Search this and parent modules for the named variable.
      * 
      * @param name The variable to search for
      * @return The module in which that variable is found, or null if not found
      */
     private RubyModule getModuleWithInstanceVar(String name) {
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getInstanceVariable(name) != null) {
                 return p;
             }
         }
         return null;
     }
 
     /**
      * Set the named class variable to the given value, provided taint and freeze allow setting it.
      * 
      * Ruby C equivalent = "rb_cvar_set"
      * 
      * @param name The variable name to set
      * @param value The value to set it to
      */
     public IRubyObject setClassVar(String name, IRubyObject value) {
         RubyModule module = getModuleWithInstanceVar(name);
 
         if (module == null) {
             module = this;
         }
 
         return module.setInstanceVariable(name, value, CVAR_TAINT_ERROR, CVAR_FREEZE_ERROR);
     }
 
     /**
      * Retrieve the specified class variable, searching through this module, included modules, and supermodules.
      * 
      * Ruby C equivalent = "rb_cvar_get"
      * 
      * @param name The name of the variable to retrieve
      * @return The variable's value, or throws NameError if not found
      */
     public IRubyObject getClassVar(String name) {
         RubyModule module = getModuleWithInstanceVar(name);
 
         if (module != null) {
             IRubyObject variable = module.getInstanceVariable(name);
 
             return variable == null ? getRuntime().getNil() : variable;
         }
 
         throw getRuntime().newNameError("uninitialized class variable " + name + " in " + getName(), name);
     }
 
     /**
      * Is class var defined?
      * 
      * Ruby C equivalent = "rb_cvar_defined"
      * 
      * @param name The class var to determine "is defined?"
      * @return true if true, false if false
      */
     public boolean isClassVarDefined(String name) {
         return getModuleWithInstanceVar(name) != null;
     }
 
     /**
      * Set the named constant on this module. Also, if the value provided is another Module and
      * that module has not yet been named, assign it the specified name.
      * 
      * @param name The name to assign
      * @param value The value to assign to it; if an unnamed Module, also set its basename to name
      * @return The result of setting the variable.
      * @see RubyObject#setInstanceVariable(String, IRubyObject, String, String)
      */
     public IRubyObject setConstant(String name, IRubyObject value) {
         if(getConstantAt(name) != null) {
             getRuntime().getWarnings().warn("already initialized constant " + name);
         }
 
         IRubyObject result = setInstanceVariable(name, value, "Insecure: can't set constant",
                 "class/module");
 
         // if adding a module under a constant name, set that module's basename to the constant name
         if (value instanceof RubyModule) {
             RubyModule module = (RubyModule)value;
             if (module.getBaseName() == null) {
                 module.setBaseName(name);
                 module.setParent(this);
             }
             /*
             module.setParent(this);
             */
         }
         return result;
     }
 
     /**
      * Finds a class that is within the current module (or class).
      * 
      * @param name to be found in this module (or class)
      * @return the class or null if no such class
      */
     public RubyClass getClass(String name) {
         IRubyObject module = getConstantAt(name);
 
         return  (module instanceof RubyClass) ? (RubyClass) module : null;
     }
 
     /**
      * Base implementation of Module#const_missing, throws NameError for specific missing constant.
      * 
      * @param name The constant name which was found to be missing
      * @return Nothing! Absolutely nothing! (though subclasses might choose to return something)
      */
     public IRubyObject const_missing(IRubyObject name, Block block) {
         /* Uninitialized constant */
         if (this != getRuntime().getObject()) {
             throw getRuntime().newNameError("uninitialized constant " + getName() + "::" + name.asSymbol(), "" + getName() + "::" + name.asSymbol());
         }
 
         throw getRuntime().newNameError("uninitialized constant " + name.asSymbol(), name.asSymbol());
     }
 
     /**
      * Include a new module in this module or class.
      * 
      * @param arg The module to include
      */
     public synchronized void includeModule(IRubyObject arg) {
         assert arg != null;
 
         testFrozen("module");
         if (!isTaint()) {
             getRuntime().secure(4);
         }
 
         if (!(arg instanceof RubyModule)) {
             throw getRuntime().newTypeError("Wrong argument type " + arg.getMetaClass().getName() +
                     " (expected Module).");
         }
 
         RubyModule module = (RubyModule) arg;
 
         // Make sure the module we include does not already exist
         if (isSame(module)) {
             return;
         }
 
         infectBy(module);
 
         RubyModule p, c;
         boolean changed = false;
         boolean skip = false;
 
         c = this;
         while (module != null) {
             if (getNonIncludedClass() == module.getNonIncludedClass()) {
                 throw getRuntime().newArgumentError("cyclic include detected");
             }
 
             boolean superclassSeen = false;
             for (p = getSuperClass(); p != null; p = p.getSuperClass()) {
                 if (p instanceof IncludedModuleWrapper) {
                     if (p.getNonIncludedClass() == module.getNonIncludedClass()) {
                         if (!superclassSeen) {
                             c = p;
                         }
                         skip = true;
                         break;
                     }
                 } else {
                     superclassSeen = true;
                 }
             }
             if (!skip) {
                 // In the current logic, if we get here we know that module is not an 
                 // IncludedModuleWrapper, so there's no need to fish out the delegate. But just 
                 // in case the logic should change later, let's do it anyway:
                 c.setSuperClass(new IncludedModuleWrapper(getRuntime(), c.getSuperClass(),
                         module.getNonIncludedClass()));
                 c = c.getSuperClass();
                 changed = true;
             }
 
             module = module.getSuperClass();
             skip = false;
         }
 
         if (changed) {
             // MRI seems to blow away its cache completely after an include; is
             // what we're doing here really safe?
             List methodNames = new ArrayList(((RubyModule) arg).getMethods().keySet());
             for (Iterator iter = methodNames.iterator();
                  iter.hasNext();) {
                 String methodName = (String) iter.next();
                 getRuntime().getCacheMap().remove(methodName, searchMethod(methodName));
             }
         }
 
     }
 
     public void defineMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new FullFunctionCallbackMethod(this, method, visibility));
     }
 
     public void defineFastMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void definePrivateMethod(String name, Callback method) {
         addMethod(name, new FullFunctionCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void defineFastPrivateMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void undefineMethod(String name) {
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     /** rb_undef
      *
      */
     public void undef(String name) {
         IRuby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw new SecurityException("Insecure: can't undef");
         }
         testFrozen("module");
         if (name.equals("__id__") || name.equals("__send__")) {
             getRuntime().getWarnings().warn("undefining `"+ name +"' may cause serious problem");
         }
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             String s0 = " class";
             RubyModule c = this;
 
             if (c.isSingleton()) {
                 IRubyObject obj = getInstanceVariable("__attached__");
 
                 if (obj != null && obj instanceof RubyModule) {
                     c = (RubyModule) obj;
                     s0 = "";
                 }
             } else if (c.isModule()) {
                 s0 = " module";
             }
 
             throw getRuntime().newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'", name);
         }
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     private void addCachedMethod(String name, DynamicMethod method) {
         // Included modules modify the original 'included' modules class.  Since multiple
         // classes can include the same module, we cannot cache in the original included module.
         if (!isIncluded()) {
             getMethods().put(name, method);
             getRuntime().getCacheMap().add(method, this);
         }
     }
 
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, DynamicMethod method) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't define method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             // If we add a method which already is cached in this class, then we should update the 
             // cachemap so it stays up to date.
             DynamicMethod existingMethod = (DynamicMethod) getMethods().remove(name);
             if (existingMethod != null) {
                 getRuntime().getCacheMap().remove(name, existingMethod);
             }
 
             getMethods().put(name, method);
         }
     }
 
     public void removeCachedMethod(String name) {
         getMethods().remove(name);
     }
 
     public void removeMethod(String name) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't remove method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             DynamicMethod method = (DynamicMethod) getMethods().remove(name);
             if (method == null) {
                 throw getRuntime().newNameError("method '" + name + "' not defined in " + getName(), name);
             }
 
             getRuntime().getCacheMap().remove(name, method);
         }
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod searchMethod(String name) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             // included modules use delegates methods for we need to synchronize on result of getMethods
             synchronized(searchModule.getMethods()) {
                 // See if current class has method or if it has been cached here already
                 DynamicMethod method = (DynamicMethod) searchModule.getMethods().get(name);
                 if (method != null) {
                     if (searchModule != this) {
                         addCachedMethod(name, method);
                     }
 
                     return method;
                 }
             }
         }
 
         return UndefinedMethod.getInstance();
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod retrieveMethod(String name) {
         return (DynamicMethod)getMethods().get(name);
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public RubyModule findImplementer(RubyModule clazz) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             if (searchModule.isSame(clazz)) {
                 return searchModule;
             }
         }
 
         return null;
     }
 
-    // FIXME: Why do module functions need singleton classes here? This messages with
-    // marshalling tests for dumping modules, since they now always have a singleton
-    // class, which isn't allowed to dump.
     public void addModuleFunction(String name, DynamicMethod method) {
         addMethod(name, method);
         getSingletonClass().addMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineModuleFunction(String name, Callback method) {
         definePrivateMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void definePublicModuleFunction(String name, Callback method) {
         defineMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastModuleFunction(String name, Callback method) {
         defineFastPrivateMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastPublicModuleFunction(String name, Callback method) {
         defineFastMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
-    /** rb_define_module_function
-     *
-     */
-    public void defineFastPublicModuleFunction2(String name, Callback method) {
-        defineFastMethod(name, method);
-        getMetaClass().defineFastMethod(name, method);
-    }
-
     private IRubyObject getConstantInner(String name, boolean exclude) {
         IRubyObject objectClass = getRuntime().getObject();
         boolean retryForModule = false;
         RubyModule p = this;
 
         retry: while (true) {
             while (p != null) {
                 IRubyObject constant = p.getConstantAt(name);
 
                 if (constant == null) {
                     if (getRuntime().getLoadService().autoload(p.getName() + "::" + name) != null) {
                         continue;
                     }
                 }
                 if (constant != null) {
                     if (exclude && p == objectClass && this != objectClass) {
                         getRuntime().getWarnings().warn("toplevel constant " + name +
                                 " referenced by " + getName() + "::" + name);
                     }
 
                     return constant;
                 }
                 p = p.getSuperClass();
             }
 
             if (!exclude && !retryForModule && getClass().equals(RubyModule.class)) {
                 retryForModule = true;
                 p = getRuntime().getObject();
                 continue retry;
             }
 
             break;
         }
 
         return callMethod(getRuntime().getCurrentContext(), "const_missing", RubySymbol.newSymbol(getRuntime(), name));
     }
 
     /**
      * Retrieve the named constant, invoking 'const_missing' should that be appropriate.
      * 
      * @param name The constant to retrieve
      * @return The value for the constant, or null if not found
      */
     public IRubyObject getConstant(String name) {
         return getConstantInner(name, false);
     }
 
     public IRubyObject getConstantFrom(String name) {
         return getConstantInner(name, true);
     }
 
     public IRubyObject getConstantAt(String name) {
         return getInstanceVariable(name);
     }
 
     /** rb_alias
      *
      */
     public synchronized void defineAlias(String name, String oldName) {
         testFrozen("module");
         if (oldName.equals(name)) {
             return;
         }
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
         DynamicMethod method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = getRuntime().getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw getRuntime().newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
         getRuntime().getCacheMap().remove(name, searchMethod(name));
         getMethods().put(name, new AliasMethod(method, oldName));
     }
 
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         // This method is intended only for defining new classes in Ruby code,
         // so it uses the allocator of the specified superclass or default to
         // the Object allocator. It should NOT be used to define classes that require a native allocator.
         IRubyObject type = getConstantAt(name);
         ObjectAllocator allocator = superClazz == null ? getRuntime().getObject().getAllocator() : superClazz.getAllocator();
 
         if (type == null) {
             return getRuntime().defineClassUnder(name, superClazz, allocator, cref);
         } 
 
         if (!(type instanceof RubyClass)) {
             throw getRuntime().newTypeError(name + " is not a class.");
         } else if (superClazz != null && ((RubyClass) type).getSuperClass().getRealClass() != superClazz) {
             throw getRuntime().newTypeError("superclass mismatch for class " + name);
         }
 
         return (RubyClass) type;
     }
 
     /** rb_define_class_under
      *
      */
     public RubyClass defineClassUnder(String name, RubyClass superClazz, ObjectAllocator allocator) {
         IRubyObject type = getConstantAt(name);
 
         if (type == null) {
             return getRuntime().defineClassUnder(name, superClazz, allocator, cref);
         }
 
         if (!(type instanceof RubyClass)) {
             throw getRuntime().newTypeError(name + " is not a class.");
         } else if (((RubyClass) type).getSuperClass().getRealClass() != superClazz) {
             throw getRuntime().newNameError(name + " is already defined.", name);
         }
 
         return (RubyClass) type;
     }
 
     public RubyModule defineModuleUnder(String name) {
         IRubyObject type = getConstantAt(name);
 
         if (type == null) {
             return getRuntime().defineModuleUnder(name, cref);
         }
 
         if (!(type instanceof RubyModule)) {
             throw getRuntime().newTypeError(name + " is not a module.");
         }
 
         return (RubyModule) type;
     }
 
     /** rb_define_const
      *
      */
     public void defineConstant(String name, IRubyObject value) {
         assert value != null;
 
         if (this == getRuntime().getClass("Class")) {
             getRuntime().secure(4);
         }
 
         if (!IdUtil.isConstant(name)) {
             throw getRuntime().newNameError("bad constant name " + name, name);
         }
 
         setConstant(name, value);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     public IRubyObject removeCvar(IRubyObject name) { // Wrong Parameter ?
         if (!IdUtil.isClassVariable(name.asSymbol())) {
             throw getRuntime().newNameError("wrong class variable name " + name.asSymbol(), name.asSymbol());
         }
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't remove class variable");
         }
         testFrozen("class/module");
 
         IRubyObject value = removeInstanceVariable(name.asSymbol());
 
         if (value != null) {
             return value;
         }
 
         if (isClassVarDefined(name.asSymbol())) {
             throw cannotRemoveError(name.asSymbol());
         }
 
         throw getRuntime().newNameError("class variable " + name.asSymbol() + " not defined for " + getName(), name.asSymbol());
     }
 
     private void addAccessor(String name, boolean readable, boolean writeable) {
         ThreadContext tc = getRuntime().getCurrentContext();
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility attributeScope = tc.getCurrentVisibility();
         if (attributeScope.isPrivate()) {
             //FIXME warning
         } else if (attributeScope.isModuleFunction()) {
             attributeScope = Visibility.PRIVATE;
             // FIXME warning
         }
         final String variableName = "@" + name;
         final IRuby runtime = getRuntime();
         ThreadContext context = getRuntime().getCurrentContext();
         if (readable) {
             defineMethod(name, new Callback() {
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                     checkArgumentCount(args, 0, 0);
 
                     IRubyObject variable = self.getInstanceVariable(variableName);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             callMethod(context, "method_added", RubySymbol.newSymbol(getRuntime(), name));
         }
         if (writeable) {
             name = name + "=";
             defineMethod(name, new Callback() {
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                     IRubyObject[] fargs = runtime.getCurrentContext().getFrameArgs();
 
                     if (fargs.length != 1) {
                         throw runtime.newArgumentError("wrong # of arguments(" + fargs.length + "for 1)");
                     }
 
                     return self.setInstanceVariable(variableName, fargs[0]);
                 }
 
                 public Arity getArity() {
                     return Arity.singleArgument();
                 }
             });
             callMethod(context, "method_added", RubySymbol.newSymbol(getRuntime(), name));
         }
     }
 
     /** set_method_visibility
      *
      */
     public void setMethodVisibility(IRubyObject[] methods, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         for (int i = 0; i < methods.length; i++) {
             exportMethod(methods[i].asSymbol(), visibility);
         }
     }
 
     /** rb_export_method
      *
      */
     public void exportMethod(String name, Visibility visibility) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method '" + name + "' for " +
                                 (isModule() ? "module" : "class") + " '" + getName() + "'", name);
         }
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 addMethod(name, new FullFunctionCallbackMethod(this, new Callback() {
                     public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                         ThreadContext tc = self.getRuntime().getCurrentContext();
                         return tc.callSuper(tc.getFrameArgs(), block);
                     }
 
                     public Arity getArity() {
                         return Arity.optional();
                     }
                 }, visibility));
             }
         }
     }
 
     /**
      * MRI: rb_method_boundp
      *
      */
     public boolean isMethodBound(String name, boolean checkVisibility) {
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined()) {
             return !(checkVisibility && method.getVisibility().isPrivate());
         }
         return false;
     }
 
     public IRubyObject newMethod(IRubyObject receiver, String name, boolean bound) {
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method `" + name +
                 "' for class `" + this.getName() + "'", name);
         }
 
         RubyMethod newMethod = null;
         if (bound) {
             newMethod = RubyMethod.newMethod(method.getImplementationClass(), name, this, name, method, receiver);
         } else {
             newMethod = RubyUnboundMethod.newUnboundMethod(method.getImplementationClass(), name, this, name, method);
         }
         newMethod.infectBy(this);
 
         return newMethod;
     }
 
     // What is argument 1 for in this method?
     public IRubyObject define_method(IRubyObject[] args, Block block) {
         if (args.length < 1 || args.length > 2) {
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         IRubyObject body;
         String name = args[0].asSymbol();
         DynamicMethod newMethod = null;
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility visibility = tc.getCurrentVisibility();
 
         if (visibility.isModuleFunction()) {
             visibility = Visibility.PRIVATE;
         }
 
 
         if (args.length == 1 || args[1].isKindOf(getRuntime().getClass("Proc"))) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = (args.length == 1) ? getRuntime().newProc(false, block) : (RubyProc)args[1];
             body = proc;
 
             proc.getBlock().isLambda = true;
             proc.getBlock().getFrame().setLastClass(this);
             proc.getBlock().getFrame().setLastFunc(name);
 
             newMethod = new ProcMethod(this, proc, visibility);
         } else if (args[1].isKindOf(getRuntime().getClass("Method"))) {
             RubyMethod method = (RubyMethod)args[1];
             body = method;
 
             newMethod = new MethodMethod(this, method.unbind(null), visibility);
         } else {
             throw getRuntime().newTypeError("wrong argument type " + args[0].getType().getName() + " (expected Proc/Method)");
         }
 
         addMethod(name, newMethod);
 
         RubySymbol symbol = RubySymbol.newSymbol(getRuntime(), name);
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (tc.getPreviousVisibility().isModuleFunction()) {
             getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), newMethod, Visibility.PUBLIC));
             callMethod(context, "singleton_method_added", symbol);
         }
 
         callMethod(context, "method_added", symbol);
 
         return body;
     }
 
     public IRubyObject executeUnder(Callback method, IRubyObject[] args, Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
 
         context.preExecuteUnder(this, block);
 
         try {
             return method.execute(this, args, block);
         } finally {
             context.postExecuteUnder();
         }
     }
 
     // Methods of the Module Class (rb_mod_*):
 
     public static RubyModule newModule(IRuby runtime, String name) {
         return newModule(runtime, name, null);
     }
 
     public static RubyModule newModule(IRuby runtime, String name, SinglyLinkedList parentCRef) {
         RubyModule module = new RubyModule(runtime, runtime.getClass("Module"), null, parentCRef, name);
         
         return module;
     }
 
     public RubyString name() {
         return getRuntime().newString(getBaseName() == null ? "" : getName());
     }
 
     /** rb_mod_class_variables
      *
      */
     public RubyArray class_variables() {
         RubyArray ary = getRuntime().newArray();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             for (Iterator iter = p.instanceVariableNames(); iter.hasNext();) {
                 String id = (String) iter.next();
                 if (IdUtil.isClassVariable(id)) {
                     RubyString kval = getRuntime().newString(id);
                     if (!ary.includes(kval)) {
                         ary.append(kval);
                     }
                 }
             }
         }
         return ary;
     }
 
     /** rb_mod_clone
      *
      */
     public IRubyObject rbClone() {
         return cloneMethods((RubyModule) super.rbClone());
     }
 
     protected IRubyObject cloneMethods(RubyModule clone) {
         RubyModule realType = this.getNonIncludedClass();
         for (Iterator iter = getMethods().entrySet().iterator(); iter.hasNext(); ) {
             Map.Entry entry = (Map.Entry) iter.next();
             DynamicMethod method = (DynamicMethod) entry.getValue();
 
             // Do not clone cached methods
             if (method.getImplementationClass() == realType) {
                 // A cloned method now belongs to a new class.  Set it.
                 // TODO: Make DynamicMethod immutable
                 DynamicMethod clonedMethod = (DynamicMethod)method.dup();
                 clonedMethod.setImplementationClass(clone);
                 clone.getMethods().put(entry.getKey(), clonedMethod);
             }
         }
 
         return clone;
     }
 
     protected IRubyObject doClone() {
         return RubyModule.newModule(getRuntime(), getBaseName(), cref.getNext());
     }
 
     /** rb_mod_dup
      *
      */
     public IRubyObject dup() {
         RubyModule dup = (RubyModule) rbClone();
         dup.setMetaClass(getMetaClass());
         dup.setFrozen(false);
         // +++ jpetersen
         // dup.setSingleton(isSingleton());
         // --- jpetersen
 
         return dup;
     }
 
     /** rb_mod_included_modules
      *
      */
     public RubyArray included_modules() {
         RubyArray ary = getRuntime().newArray();
 
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isIncluded()) {
                 ary.append(p.getNonIncludedClass());
             }
         }
 
         return ary;
     }
 
     /** rb_mod_ancestors
      *
      */
     public RubyArray ancestors() {
         RubyArray ary = getRuntime().newArray(getAncestorList());
 
         return ary;
     }
 
     public List getAncestorList() {
         ArrayList list = new ArrayList();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if(!p.isSingleton()) {
                 list.add(p.getNonIncludedClass());
             }
         }
 
         return list;
     }
 
     public boolean hasModuleInHierarchy(RubyModule type) {
         // XXX: This check previously used callMethod("==") to check for equality between classes
         // when scanning the hierarchy. However the == check may be safe; we should only ever have
         // one instance bound to a given type/constant. If it's found to be unsafe, examine ways
         // to avoid the == call.
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getNonIncludedClass() == type) return true;
         }
 
         return false;
     }
 
     /** rb_mod_to_s
      *
      */
     public IRubyObject to_s() {
         return getRuntime().newString(getName());
     }
 
     /** rb_mod_eqq
      *
      */
     public RubyBoolean op_eqq(IRubyObject obj) {
         return getRuntime().newBoolean(obj.isKindOf(this));
     }
 
     /** rb_mod_le
     *
     */
    public IRubyObject op_le(IRubyObject obj) {
        if (!(obj instanceof RubyModule)) {
            throw getRuntime().newTypeError("compared with non class/module");
        }
 
        if (isKindOfModule((RubyModule)obj)) {
            return getRuntime().getTrue();
        } else if (((RubyModule)obj).isKindOfModule(this)) {
            return getRuntime().getFalse();
        }
 
        return getRuntime().getNil();
    }
 
    /** rb_mod_lt
     *
     */
    public IRubyObject op_lt(IRubyObject obj) {
     return obj == this ? getRuntime().getFalse() : op_le(obj);
    }
 
    /** rb_mod_ge
     *
     */
    public IRubyObject op_ge(IRubyObject obj) {
        if (!(obj instanceof RubyModule)) {
            throw getRuntime().newTypeError("compared with non class/module");
        }
 
        return ((RubyModule) obj).op_le(this);
    }
 
    /** rb_mod_gt
     *
     */
    public IRubyObject op_gt(IRubyObject obj) {
        return this == obj ? getRuntime().getFalse() : op_ge(obj);
    }
 
    /** rb_mod_cmp
     *
     */
    public IRubyObject op_cmp(IRubyObject obj) {
        if (this == obj) {
            return getRuntime().newFixnum(0);
        }
 
        if (!(obj instanceof RubyModule)) {
            throw getRuntime().newTypeError(
                "<=> requires Class or Module (" + getMetaClass().getName() + " given)");
        }
 
        RubyModule module = (RubyModule)obj;
 
        if (module.isKindOfModule(this)) {
            return getRuntime().newFixnum(1);
        } else if (this.isKindOfModule(module)) {
            return getRuntime().newFixnum(-1);
        }
 
        return getRuntime().getNil();
    }
 
    public boolean isKindOfModule(RubyModule type) {
        for (RubyModule p = this; p != null; p = p.getSuperClass()) {
            if (p.isSame(type)) {
                return true;
            }
        }
 
        return false;
    }
 
    public boolean isSame(RubyModule module) {
        return this == module;
    }
 
     /** rb_mod_initialize
      *
      */
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (block != null) block.yield(getRuntime().getCurrentContext(), null, this, this, false);
         
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr
      *
      */
     public IRubyObject attr(IRubyObject[] args) {
         checkArgumentCount(args, 1, 2);
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
 
         addAccessor(args[0].asSymbol(), true, writeable);
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_reader
      *
      */
     public IRubyObject attr_reader(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asSymbol(), true, false);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_writer
      *
      */
     public IRubyObject attr_writer(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asSymbol(), false, true);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_accessor
      *
      */
     public IRubyObject attr_accessor(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asSymbol(), true, true);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_const_get
      *
      */
     public IRubyObject const_get(IRubyObject symbol) {
         String name = symbol.asSymbol();
 
         if (!IdUtil.isConstant(name)) {
             throw wrongConstantNameError(name);
         }
 
         return getConstant(name);
     }
 
     /** rb_mod_const_set
      *
      */
     public IRubyObject const_set(IRubyObject symbol, IRubyObject value) {
         String name = symbol.asSymbol();
 
         if (!IdUtil.isConstant(name)) {
             throw wrongConstantNameError(name);
         }
 
         return setConstant(name, value);
     }
 
     /** rb_mod_const_defined
      *
      */
     public RubyBoolean const_defined(IRubyObject symbol) {
         String name = symbol.asSymbol();
 
         if (!IdUtil.isConstant(name)) {
             throw wrongConstantNameError(name);
         }
 
         return getRuntime().newBoolean(getConstantAt(name) != null);
     }
 
     private RaiseException wrongConstantNameError(String name) {
         return getRuntime().newNameError("wrong constant name " + name, name);
     }
 
     private RubyArray instance_methods(IRubyObject[] args, final Visibility visibility) {
         boolean includeSuper = args.length > 0 ? args[0].isTrue() : true;
         RubyArray ary = getRuntime().newArray();
         HashMap undefinedMethods = new HashMap();
 
         for (RubyModule type = this; type != null; type = type.getSuperClass()) {
             RubyModule realType = type.getNonIncludedClass();
             for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext();) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
                 String methodName = (String) entry.getKey();
 
                 if (method.isUndefined()) {
                     undefinedMethods.put(methodName, Boolean.TRUE);
                     continue;
                 }
                 if (method.getImplementationClass() == realType &&
                     method.getVisibility().is(visibility) && undefinedMethods.get(methodName) == null) {
                     RubyString name = getRuntime().newString(methodName);
 
                     if (!ary.includes(name)) {
                         ary.append(name);
                     }
                 }
             }
 
             if (!includeSuper) {
                 break;
             }
         }
 
         return ary;
     }
 
     public RubyArray instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PUBLIC_PROTECTED);
     }
 
     public RubyArray public_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PUBLIC);
     }
 
     public IRubyObject instance_method(IRubyObject symbol) {
         return newMethod(null, symbol.asSymbol(), false);
     }
 
     /** rb_class_protected_instance_methods
      *
      */
     public RubyArray protected_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PROTECTED);
     }
 
     /** rb_class_private_instance_methods
      *
      */
     public RubyArray private_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PRIVATE);
     }
 
     /** rb_mod_constants
      *
      */
     public RubyArray constants() {
         ArrayList constantNames = new ArrayList();
         RubyModule objectClass = getRuntime().getObject();
 
         if (getRuntime().getClass("Module") == this) {
             for (Iterator vars = objectClass.instanceVariableNames();
                  vars.hasNext();) {
                 String name = (String) vars.next();
                 if (IdUtil.isConstant(name)) {
                     constantNames.add(getRuntime().newString(name));
                 }
             }
 
             return getRuntime().newArray(constantNames);
         } else if (getRuntime().getObject() == this) {
             for (Iterator vars = instanceVariableNames(); vars.hasNext();) {
                 String name = (String) vars.next();
                 if (IdUtil.isConstant(name)) {
                     constantNames.add(getRuntime().newString(name));
                 }
             }
 
             return getRuntime().newArray(constantNames);
         }
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (objectClass == p) {
                 continue;
             }
 
             for (Iterator vars = p.instanceVariableNames(); vars.hasNext();) {
                 String name = (String) vars.next();
                 if (IdUtil.isConstant(name)) {
                     constantNames.add(getRuntime().newString(name));
                 }
             }
         }
 
         return getRuntime().newArray(constantNames);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     public IRubyObject remove_class_variable(IRubyObject name) {
         String id = name.asSymbol();
 
         if (!IdUtil.isClassVariable(id)) {
             throw getRuntime().newNameError("wrong class variable name " + id, id);
         }
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't remove class variable");
         }
         testFrozen("class/module");
 
         IRubyObject variable = removeInstanceVariable(id);
         if (variable != null) {
             return variable;
         }
 
         if (isClassVarDefined(id)) {
             throw cannotRemoveError(id);
         }
         throw getRuntime().newNameError("class variable " + id + " not defined for " + getName(), id);
     }
 
     private RaiseException cannotRemoveError(String id) {
         return getRuntime().newNameError("cannot remove " + id + " for " + getName(), id);
     }
 
     public IRubyObject remove_const(IRubyObject name) {
         String id = name.asSymbol();
 
         if (!IdUtil.isConstant(id)) {
             throw wrongConstantNameError(id);
         }
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't remove class variable");
         }
         testFrozen("class/module");
 
         IRubyObject variable = getInstanceVariable(id);
         if (variable != null) {
             return removeInstanceVariable(id);
         }
 
         if (isClassVarDefined(id)) {
             throw cannotRemoveError(id);
         }
         throw getRuntime().newNameError("constant " + id + " not defined for " + getName(), id);
     }
 
     /** rb_mod_append_features
      *
      */
     // TODO: Proper argument check (conversion?)
     public RubyModule append_features(IRubyObject module) {
         ((RubyModule) module).includeModule(this);
         return this;
     }
 
     /** rb_mod_extend_object
      *
      */
     public IRubyObject extend_object(IRubyObject obj) {
         obj.extendObject(this);
         return obj;
     }
 
     /** rb_mod_include
      *
      */
     public RubyModule include(IRubyObject[] modules) {
         ThreadContext context = getRuntime().getCurrentContext();
 
         for (int i = modules.length - 1; i >= 0; i--) {
             modules[i].callMethod(context, "append_features", this);
             modules[i].callMethod(context, "included", this);
         }
 
         return this;
     }
 
     public IRubyObject included(IRubyObject other) {
         return getRuntime().getNil();
     }
 
     public IRubyObject extended(IRubyObject other, Block block) {
         return getRuntime().getNil();
     }
 
     private void setVisibility(IRubyObject[] args, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         if (args.length == 0) {
             getRuntime().getCurrentContext().setCurrentVisibility(visibility);
         } else {
             setMethodVisibility(args, visibility);
         }
     }
 
     /** rb_mod_public
      *
      */
     public RubyModule rbPublic(IRubyObject[] args, Block block) {
         setVisibility(args, Visibility.PUBLIC);
         return this;
     }
 
     /** rb_mod_protected
      *
      */
     public RubyModule rbProtected(IRubyObject[] args, Block block) {
         setVisibility(args, Visibility.PROTECTED);
         return this;
     }
 
     /** rb_mod_private
      *
      */
     public RubyModule rbPrivate(IRubyObject[] args, Block block) {
         setVisibility(args, Visibility.PRIVATE);
         return this;
     }
 
     /** rb_mod_modfunc
      *
      */
     public RubyModule module_function(IRubyObject[] args) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (args.length == 0) {
             context.setCurrentVisibility(Visibility.MODULE_FUNCTION);
         } else {
             setMethodVisibility(args, Visibility.PRIVATE);
 
             for (int i = 0; i < args.length; i++) {
                 String name = args[i].asSymbol();
                 DynamicMethod method = searchMethod(name);
                 assert !method.isUndefined() : "undefined method '" + name + "'";
                 getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), method, Visibility.PUBLIC));
                 callMethod(context, "singleton_method_added", RubySymbol.newSymbol(getRuntime(), name));
             }
         }
         return this;
     }
 
     public IRubyObject method_added(IRubyObject nothing, Block block) {
         return getRuntime().getNil();
     }
 
     public RubyBoolean method_defined(IRubyObject symbol) {
         return isMethodBound(symbol.asSymbol(), true) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public RubyModule public_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, Visibility.PUBLIC);
         return this;
     }
 
     public RubyModule private_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, Visibility.PRIVATE);
         return this;
     }
 
     public RubyModule alias_method(IRubyObject newId, IRubyObject oldId) {
         defineAlias(newId.asSymbol(), oldId.asSymbol());
         return this;
     }
 
     public RubyModule undef_method(IRubyObject name) {
         undef(name.asSymbol());
         return this;
     }
 
     public IRubyObject module_eval(IRubyObject[] args, Block block) {
         return specificEval(this, args, block);
     }
 
     public RubyModule remove_method(IRubyObject[] args) {
         for(int i=0;i<args.length;i++) {
             removeMethod(args[i].asSymbol());
         }
         return this;
     }
 
     public static void marshalTo(RubyModule module, MarshalStream output) throws java.io.IOException {
         output.writeString(module.name().toString());
     }
 
     public static RubyModule unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = input.unmarshalString();
         IRuby runtime = input.getRuntime();
         RubyModule result = runtime.getClassFromPath(name);
         if (result == null) {
             throw runtime.newNameError("uninitialized constant " + name, name);
         }
         input.registerLinkTarget(result);
         return result;
     }
 
     public SinglyLinkedList getCRef() {
         return cref;
     }
 
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 699f8afb87..922f4dd66e 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1123 +1,1127 @@
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
 
 import org.jruby.ast.Node;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.DynamicMethod;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.util.PrintfFormat;
 import org.jruby.util.collections.SinglyLinkedList;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
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
 
     public RubyObject(IRuby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
 
     public RubyObject(IRuby runtime, RubyClass metaClass, boolean useObjectSpace) {
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
     public MetaClass makeMetaClass(RubyClass type, SinglyLinkedList parentCRef) {
         MetaClass newMetaClass = type.newSingletonClass(parentCRef);
 		
         if (!isNil()) {
             setMetaClass(newMetaClass);
         }
         newMetaClass.attachToObject(this);
+        
+        // use same ClassIndex as metaclass, since we're technically still of that type
+        newMetaClass.index = type.index;
+        
         return newMetaClass;
     }
 
     public boolean singletonMethodsAllowed() {
         return true;
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
         return ((RubyString) callMethod(getRuntime().getCurrentContext(), "to_s")).toString();
     }
 
     /** Getter for property ruby.
      * @return Value of property ruby.
      */
     public IRuby getRuntime() {
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
            throw getRuntime().newFrozenError(message);
        }
    }
 
    protected void checkFrozen() {
        testFrozen("can't modify frozen " + getMetaClass().getName());
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
 
     public boolean isTrue() {
         return !isNil();
     }
 
     public boolean isFalse() {
         return isNil();
     }
 
     public boolean respondsTo(String name) {
         return getMetaClass().isMethodBound(name, false);
     }
 
     // Some helper functions:
 
     public int checkArgumentCount(IRubyObject[] args, int min, int max) {
         if (args.length < min) {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for " + min + ")");
         }
         if (max > -1 && args.length > max) {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for " + max + ")");
         }
         return args.length;
     }
 
     public boolean isKindOf(RubyModule type) {
         return getMetaClass().hasModuleInHierarchy(type);
     }
 
     /** rb_singleton_class
      *
      */
     public MetaClass getSingletonClass() {
         RubyClass type = getMetaClass();
         if (!type.isSingleton()) {
             type = makeMetaClass(type, type.getCRef());
         }
 
         assert type instanceof MetaClass;
 
 		if (!isNil()) {
 			type.setTaint(isTaint());
 			type.setFrozen(isFrozen());
 		}
 
         return (MetaClass)type;
     }
 
     /** rb_define_singleton_method
      *
      */
     public void defineSingletonMethod(String name, Callback method) {
         getSingletonClass().defineMethod(name, method);
     }
 
     /* rb_init_ccopy */
     public void initCopy(IRubyObject original) {
         assert original != null;
         assert !isFrozen() : "frozen object (" + getMetaClass().getName() + ") allocated";
 
         setInstanceVariables(new HashMap(original.getInstanceVariables()));
 
         callMethod(getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /** OBJ_INFECT
      *
      */
     public IRubyObject infectBy(IRubyObject obj) {
         setTaint(isTaint() || obj.isTaint());
 
         return this;
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, null);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, Block block) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, getMetaClass(), name, args, callType, null);
     }
     
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType, Block block) {
         return callMethod(context, getMetaClass(), name, args, callType, block);
     }
 
     /**
      * Used by the compiler to ease calling indexed methods
      */
     public IRubyObject callMethod(ThreadContext context, byte methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
         RubyModule module = getMetaClass();
         
         if (module.index != 0) {
             return callMethod(context, module, getRuntime().getSelectorTable().table[module.index][methodIndex], name, args, callType, block);
         } 
             
         return callMethod(context, module, name, args, callType, block);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, rubyclass, name, args, callType, null);
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
 
         if (method.isUndefined() ||
             !(name.equals("method_missing") ||
               method.isCallableFrom(context.getFrameSelf(), callType))) {
             if (callType == CallType.SUPER) {
                 throw getRuntime().newNameError("super: no superclass method '" + name + "'", name);
             }
 
             // store call information so method_missing impl can use it
             context.setLastCallStatus(method.getVisibility(), callType);
 
             if (name.equals("method_missing")) {
                 return RubyKernel.method_missing(this, args, block);
             }
 
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             System.arraycopy(args, 0, newArgs, 1, args.length);
             newArgs[0] = RubySymbol.newSymbol(getRuntime(), name);
 
             return callMethod(context, "method_missing", newArgs, block);
         }
 
         RubyModule implementer = null;
         if (method.needsImplementer()) {
             // modules are included with a shim class; we must find that shim to handle super() appropriately
             implementer = rubyclass.findImplementer(method.getImplementationClass());
         } else {
             // classes are directly in the hierarchy, so no special logic is necessary for implementer
             implementer = method.getImplementationClass();
         }
 
         String originalName = method.getOriginalName();
         if (originalName != null) {
             name = originalName;
         }
 
         return method.call(context, this, implementer, name, args, false, block);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return callMethod(context, name, IRubyObject.NULL_ARRAY, null, null);
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
 
     /** rb_eval
      *
      */
     public IRubyObject eval(Node n) {
         //return new EvaluationState(getRuntime(), this).begin(n);
         // need to continue evaluation with a new self, so save the old one (should be a stack?)
         return EvaluationState.eval(getRuntime().getCurrentContext(), n, this, null);
     }
 
     public void callInit(IRubyObject[] args, Block block) {
         callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
     }
 
     public void extendObject(RubyModule module) {
         getSingletonClass().includeModule(module);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         throw getRuntime().newTypeError(inspect().toString() + " is not a symbol");
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
      */
     public IRubyObject convertToTypeWithCheck(String targetType, String convertMethod) {
         if (targetType.equals(getMetaClass().getName())) {
             return this;
         }
 
         IRubyObject value = convertToType(targetType, convertMethod, false);
         if (value.isNil()) {
             return value;
         }
 
         if (!targetType.equals(value.getMetaClass().getName())) {
             throw getRuntime().newTypeError(value.getMetaClass().getName() + "#" + convertMethod +
                     "should return " + targetType);
         }
 
         return value;
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
     public IRubyObject convertToType(String targetType, String convertMethod, boolean raise) {
         // No need to convert something already of the correct type.
         // XXXEnebo - Could this pass actual class reference instead of String?
         if (targetType.equals(getMetaClass().getName())) {
             return this;
         }
         
         if (!respondsTo(convertMethod)) {
             if (raise) {
                 throw getRuntime().newTypeError(
                     "can't convert " + trueFalseNil(getMetaClass().getName()) + " into " + trueFalseNil(targetType));
             } 
 
             return getRuntime().getNil();
         }
         return callMethod(getRuntime().getCurrentContext(), convertMethod);
     }
 
     protected String trueFalseNil(String v) {
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
         return (RubyArray) convertToType("Array", "to_ary", true);
     }
 
     public RubyFloat convertToFloat() {
         return (RubyFloat) convertToType("Float", "to_f", true);
     }
 
     public RubyInteger convertToInteger() {
         return (RubyInteger) convertToType("Integer", "to_int", true);
     }
 
     public RubyString convertToString() {
         return (RubyString) convertToType("String", "to_str", true);
     }
 
     /** rb_convert_type
      *
      */
     public IRubyObject convertType(Class type, String targetType, String convertMethod) {
         if (type.isAssignableFrom(getClass())) {
             return this;
         }
 
         IRubyObject result = convertToType(targetType, convertMethod, true);
 
         if (!type.isAssignableFrom(result.getClass())) {
             throw getRuntime().newTypeError(
                 getMetaClass().getName() + "#" + convertMethod + " should return " + targetType + ".");
         }
 
         return result;
     }
 
     public void checkSafeString() {
         if (getRuntime().getSafeLevel() > 0 && isTaint()) {
             ThreadContext tc = getRuntime().getCurrentContext();
             if (tc.getFrameLastFunc() != null) {
                 throw getRuntime().newSecurityError("Insecure operation - " + tc.getFrameLastFunc());
             }
             throw getRuntime().newSecurityError("Insecure operation: -r");
         }
         getRuntime().secure(4);
         if (!(this instanceof RubyString)) {
             throw getRuntime().newTypeError(
                 "wrong argument type " + getMetaClass().getName() + " (expected String)");
         }
     }
 
     /** specific_eval
      *
      */
     public IRubyObject specificEval(RubyModule mod, IRubyObject[] args, Block block) {
         if (block != null) {
             if (args.length > 0) throw getRuntime().newArgumentError(args.length, 0);
 
             return yieldUnder(mod, block);
         }
         ThreadContext tc = getRuntime().getCurrentContext();
 
         if (args.length == 0) {
 		    throw getRuntime().newArgumentError("block not supplied");
 		} else if (args.length > 3) {
 		    String lastFuncName = tc.getFrameLastFunc();
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
         }, new IRubyObject[] { this, src, file, line }, null);
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
                 		IRubyObject breakValue = (IRubyObject)je.getPrimaryData();
 
                 		return breakValue == null ? getRuntime().getNil() : breakValue;
                 	} else {
                 		throw je;
                 	}
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
 
             result = EvaluationState.eval(threadContext, getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope()), newSelf, blockOfBinding);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             threadContext.postEvalWithBinding(blockOfBinding);
 
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
 
         ThreadContext threadContext = getRuntime().getCurrentContext();
         ISourcePosition savedPosition = threadContext.getPosition();
 
         // no binding, just eval in "current" frame (caller's frame)
         try {
             return EvaluationState.eval(threadContext, getRuntime().parse(src.toString(), file, threadContext.getCurrentScope()), this, null);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             // restore position
             threadContext.setPosition(savedPosition);
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
 
 	public IRubyObject initialize_copy(IRubyObject original) {
 	    if (this != original) {
 	        checkFrozen();
 	        if (!getClass().equals(original.getClass())) {
 	            throw getRuntime().newTypeError("initialize_copy should take same class object");
 	        }
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
         checkArgumentCount(args, 1, 2);
 
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
     
     public RubyFixnum hash() {
         return getRuntime().newFixnum(System.identityHashCode(this));
     }
 
     public int hashCode() {
     	return (int) RubyNumeric.fix2long(callMethod(getRuntime().getCurrentContext(), "hash"));
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
      *
      */
     public IRubyObject rbClone() {
         if (isImmediate()) { // rb_special_const_p(obj) equivalent
             getRuntime().newTypeError("can't clone " + getMetaClass().getName());
         }
         
         IRubyObject clone = doClone();
         clone.setMetaClass(getMetaClass().getSingletonClassClone());
         clone.setTaint(this.isTaint());
         clone.initCopy(this);
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
      *
      */
     public IRubyObject dup() {
         IRubyObject dup = callMethod(getRuntime().getCurrentContext(), "clone");
         if (!dup.getClass().equals(getClass())) {
             throw getRuntime().newTypeError("duplicated object must be same type");
         }
 
         dup.setMetaClass(type());
         dup.setFrozen(false);
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
         if(getInstanceVariables().size() > 0) {
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
                         part.append(" ");
                         part.append(sep);
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
     	checkArgumentCount(args, 0, 1);
 
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
         if(checkArgumentCount(args,0,1) == 1) {
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
 
     protected IRubyObject anyToString() {
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
 
diff --git a/test/testMarshal.rb b/test/testMarshal.rb
index 1a6bbb9bef..548a080696 100644
--- a/test/testMarshal.rb
+++ b/test/testMarshal.rb
@@ -1,241 +1,241 @@
 require 'test/minirunit'
 test_check "Test Marshal:"
 
 MARSHAL_HEADER = Marshal.dump(nil).chop
 
 def test_marshal(expected, marshalee)
   test_equal(MARSHAL_HEADER + expected, Marshal.dump(marshalee))
 end
 
 test_marshal("0", nil)
 test_marshal("T", true)
 test_marshal("F", false)
 test_marshal("i\000", 0)
 test_marshal("i\006", 1)
 test_marshal("i\372", -1)
 test_marshal("i\002\320\a", 2000)
 test_marshal("i\3760\370", -2000)
 test_marshal("i\004\000\312\232;", 1000000000)
 test_marshal(":\017somesymbol", :somesymbol)
 test_marshal("f\n2.002", 2.002)
 test_marshal("f\013-2.002", -2.002)
 test_marshal("\"\nhello", "hello")
 test_marshal("[\010i\006i\ai\010", [1,2,3])
 test_marshal("{\006i\006i\a", {1=>2})
 test_marshal("c\013Object", Object)
 module Foo
   class Bar
   end
 end
 test_marshal("c\rFoo::Bar", Foo::Bar)
 test_marshal("m\017Enumerable", Enumerable)
 test_marshal("/\013regexp\000", /regexp/)
 test_marshal("l+\n\000\000\000\000\000\000\000\000@\000", 2 ** 70)
 #test_marshal("l+\f\313\220\263z\e\330p\260\200-\326\311\264\000",
 #             14323534664547457526224437612747)
 test_marshal("l+\n\001\000\001@\000\000\000\000@\000",
              1 + (2 ** 16) + (2 ** 30) + (2 ** 70))
 test_marshal("l+\n6\361\3100_/\205\177Iq",
              534983213684351312654646)
 test_marshal("l-\n6\361\3100_/\205\177Iq",
              -534983213684351312654646)
 test_marshal("l+\n\331\347\365%\200\342a\220\336\220",
              684126354563246654351321)
 #test_marshal("l+\vIZ\210*,u\006\025\304\016\207\001",
 #            472759725676945786624563785)
 
 test_marshal("c\023Struct::Froboz",
              Struct.new("Froboz", :x, :y))
 test_marshal("S:\023Struct::Froboz\a:\006xi\n:\006yi\f",
              Struct::Froboz.new(5, 7))
 
 # Can't dump anonymous class
 #test_exception(ArgumentError) { Marshal.dump(Struct.new(:x, :y).new(5, 7)) }
 
 
 # FIXME: Bignum marshaling is broken.
 
 # FIXME: IVAR, MODULE_OLD, 'U', ...
 
 test_marshal("o:\013Object\000", Object.new)
 class MarshalTestClass
   def initialize
     @foo = "bar"
   end
 end
 test_marshal("o:\025MarshalTestClass\006:\t@foo\"\010bar",
              MarshalTestClass.new)
 o = Object.new
 test_marshal("[\to:\013Object\000@\006@\006@\006",
              [o, o, o, o])
 class MarshalTestClass
   def initialize
     @foo = self
   end
 end
 test_marshal("o:\025MarshalTestClass\006:\t@foo@\000",
              MarshalTestClass.new)
 
 class UserMarshaled
   attr :foo
   def initialize(foo)
     @foo = foo
   end
   class << self
     def _load(str)
       return self.new(str.reverse.to_i)
     end
   end
   def _dump(depth)
     @foo.to_s.reverse
   end
   def ==(other)
     self.class == other.class && self.foo == other.foo
   end
 end
 um = UserMarshaled.new(123)
 test_marshal("u:\022UserMarshaled\010321", um)
 test_equal(um, Marshal.load(Marshal.dump(um)))
 
 test_marshal("[\a00", [nil, nil])
 test_marshal("[\aTT", [true, true])
 test_marshal("[\ai\006i\006", [1, 1])
 test_marshal("[\a:\ahi;\000", [:hi, :hi])
 o = Object.new
 test_marshal("[\ao:\013Object\000@\006", [o, o])
 
 test_exception(ArgumentError) {
   Marshal.load("\004\bu:\026SomeUnknownClassX\nhello")
 }
 
 module UM
   class UserMarshal
     def _dump(depth)
       "hello"
     end
   end
 end
 begin
   Marshal.load("\004\bu:\024UM::UserMarshal\nhello")
   test_fail
 rescue TypeError => e
   test_equal("class UM::UserMarshal needs to have method `_load'", e.message)
 end
 
 # Unmarshaling
 
 object = Marshal.load(MARSHAL_HEADER + "o:\025MarshalTestClass\006:\t@foo\"\010bar")
 test_equal(["@foo"], object.instance_variables)
 
 test_equal(true, Marshal.load(MARSHAL_HEADER + "T"))
 test_equal(false, Marshal.load(MARSHAL_HEADER + "F"))
 test_equal(nil, Marshal.load(MARSHAL_HEADER + "0"))
 test_equal("hello", Marshal.load(MARSHAL_HEADER + "\"\nhello"))
 test_equal(1, Marshal.load(MARSHAL_HEADER + "i\006"))
 test_equal(-1, Marshal.load(MARSHAL_HEADER + "i\372"))
 test_equal(-2, Marshal.load(MARSHAL_HEADER + "i\371"))
 test_equal(2000, Marshal.load(MARSHAL_HEADER + "i\002\320\a"))
 test_equal(-2000, Marshal.load(MARSHAL_HEADER + "i\3760\370"))
 test_equal(1000000000, Marshal.load(MARSHAL_HEADER + "i\004\000\312\232;"))
 test_equal([1, 2, 3], Marshal.load(MARSHAL_HEADER + "[\010i\006i\ai\010"))
 test_equal({1=>2}, Marshal.load(MARSHAL_HEADER + "{\006i\006i\a"))
 test_equal(String, Marshal.load(MARSHAL_HEADER + "c\013String"))
 #test_equal(Enumerable, Marshal.load(MARSHAL_HEADER + "m\017Enumerable"))
 test_equal(Foo::Bar, Marshal.load(MARSHAL_HEADER + "c\rFoo::Bar"))
 
 s = Marshal.load(MARSHAL_HEADER + "S:\023Struct::Froboz\a:\006xi\n:\006yi\f")
 test_equal(Struct::Froboz, s.class)
 test_equal(5, s.x)
 test_equal(7, s.y)
 
 test_equal(2 ** 70, Marshal.load(MARSHAL_HEADER + "l+\n\000\000\000\000\000\000\000\000@\000"))
 
 object = Marshal.load(MARSHAL_HEADER + "o:\013Object\000")
 test_equal(Object, object.class)
 
 Marshal.dump([1,2,3], 2)
 test_exception(ArgumentError) { Marshal.dump([1,2,3], 1) }
 
 o = Object.new
 a = Marshal.load(Marshal.dump([o, o, o, o]))
 test_ok(a[0] == a[1])
 a = Marshal.load(Marshal.dump([:hi, :hi, :hi, :hi]))
 test_ok(a[0] == :hi)
 test_ok(a[1] == :hi)
 
 # simple extensions of builtins should retain their types
 class MyHash < Hash
   attr_accessor :used, :me
   
   def initialize
   	super
     @used = {}
     @me = 'a'
   end
   
   def []=(k, v) #:nodoc:
     @used[k] = false
     super
   end
   
   def foo; end
 end
 
 x = MyHash.new
 
 test_equal(MyHash, Marshal.load(Marshal.dump(x)).class)
 test_equal(x, Marshal.load(Marshal.dump(x)))
 
 x['a'] = 'b'
 #test_equal(x, Marshal.load(Marshal.dump(x)))
 
 class F < Hash
   def initialize #:nodoc:
     super
     @val = { :notice=>true }
     @val2 = { :notice=>false }
   end
 end
 
 test_equal(F.new,Marshal.load(Marshal.dump(F.new)))
 
 test_equal(4, Marshal::MAJOR_VERSION)
 test_equal(8, Marshal::MINOR_VERSION)
 
 # Hashes with defaults serialize a bit differently; confirm the default is coming back correctly
 x = {}
 x.default = "foo"
 test_equal("foo", Marshal.load(Marshal.dump(x)).default)
 
 # Range tests
 x = 1..10
 y = Marshal.load(Marshal.dump(x))
 test_equal(x, y)
 test_equal(x.class, y.class)
 test_no_exception {
   test_equal(10, y.max)
   test_equal(1, y.min)
   test_equal(false, y.exclude_end?)
   y.each {}
 }
 z = Marshal.dump(x)
 test_ok(z.include?("excl"))
 test_ok(z.include?("begin"))
 test_ok(z.include?("end"))
 
 class MyRange < Range
   attr_accessor :foo
 
   def initialize(a,b)
     super
     @foo = "hello"
   end
 end
 
 x = MyRange.new(1,10)
 y = Marshal.load(Marshal.dump(x))
 test_equal(MyRange, y.class)
-  test_equal(10, y.max)
 test_no_exception {
+  test_equal(10, y.max)
   test_equal("hello", y.foo)
 }
