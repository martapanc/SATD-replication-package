diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 45c1ca1218..53ebbb41f9 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,280 +1,290 @@
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
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2003 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
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
 package org.jruby;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Reader;
 import java.nio.channels.FileChannel;
 import java.nio.channels.FileLock;
 
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.meta.FileMetaClass;
 import org.jruby.util.IOHandler;
 import org.jruby.util.IOHandlerNull;
 import org.jruby.util.IOHandlerSeekable;
 import org.jruby.util.IOHandlerUnseekable;
 import org.jruby.util.IOModes;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.IOHandler.InvalidValueException;
 
 /**
  * Ruby File class equivalent in java.
  *
  * @author jpetersen
  **/
 public class RubyFile extends RubyIO {
 	public static final int LOCK_SH = 1;
 	public static final int LOCK_EX = 2;
 	public static final int LOCK_NB = 4;
 	public static final int LOCK_UN = 8;
 	
     protected String path;
     
 	public RubyFile(IRuby runtime, RubyClass type) {
 	    super(runtime, type);
 	}
 
 	public RubyFile(IRuby runtime, String path) {
 		this(runtime, path, open(runtime, path));
     }
 
 	// use static function because constructor call must be first statement in above constructor 
 	private static InputStream open(IRuby runtime, String path) {
 		try {
 			return new FileInputStream(path);
 		} catch (FileNotFoundException e) {
             throw runtime.newIOError(e.getMessage());
         }
 	}
     
 	// XXX This constructor is a hack to implement the __END__ syntax.
 	//     Converting a reader back into an InputStream doesn't generally work.
 	public RubyFile(IRuby runtime, String path, final Reader reader) {
 		this(runtime, path, new InputStream() {
 			public int read() throws IOException {
 				return reader.read();
 			}
 		});
 	}
 	
 	private RubyFile(IRuby runtime, String path, InputStream in) {
         super(runtime, runtime.getClass("File"));
         this.path = path;
 		try {
             this.handler = new IOHandlerUnseekable(runtime, in, null);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());  
         }
         this.modes = handler.getModes();
         registerIOHandler(handler);
 	}
     
     public void openInternal(String newPath, IOModes newModes) {
         this.path = newPath;
         this.modes = newModes;
         
         try {
             if (newPath.equals("/dev/null")) {
                 handler = new IOHandlerNull(getRuntime(), newModes);
             } else {
                 handler = new IOHandlerSeekable(getRuntime(), newPath, newModes);
             }
             
             registerIOHandler(handler);
         } catch (InvalidValueException e) {
         	throw getRuntime().newErrnoEINVALError();
         } catch (FileNotFoundException e) {
         	throw getRuntime().newErrnoENOENTError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
 		}
     }
     
     private FileLock currentLock;
 	
 	public IRubyObject flock(IRubyObject lockingConstant) {
         FileChannel fileChannel = handler.getFileChannel();
         int lockMode = (int) ((RubyFixnum) lockingConstant.convertToType("Fixnum", "to_int", 
             true)).getLongValue();
 
         try {
 			switch(lockMode) {
 			case LOCK_UN:
 				if (currentLock != null) {
 					currentLock.release();
 					currentLock = null;
 					
 					return getRuntime().newFixnum(0);
 				}
 				break;
 			case LOCK_EX:
 			case LOCK_EX | LOCK_NB:
 				if (currentLock != null) {
 					currentLock.release();
 					currentLock = null;
 				}
 				currentLock = fileChannel.tryLock();
 				if (currentLock != null) {
 					return getRuntime().newFixnum(0);
 				}
 
 				break;
 			case LOCK_SH:
 			case LOCK_SH | LOCK_NB:
 				if (currentLock != null) {
 					currentLock.release();
 					currentLock = null;
 				}
 				
 				currentLock = fileChannel.tryLock(0L, Long.MAX_VALUE, true);
 				if (currentLock != null) {
 					return getRuntime().newFixnum(0);
 				}
 
 				break;
 			default:	
 			}
         } catch (IOException ioe) {
         	throw new RaiseException(new NativeException(getRuntime(), getRuntime().getClass("IOError"), ioe));
         }
 		
 		return getRuntime().getFalse();
 	}
 
 	public IRubyObject initialize(IRubyObject[] args) {
 	    if (args.length == 0) {
 	        throw getRuntime().newArgumentError(0, 1);
 	    }
 
 	    args[0].checkSafeString();
 	    path = args[0].toString();
 	    modes = args.length > 1 ? getModes(args[1]) :
 	    	new IOModes(getRuntime(), IOModes.RDONLY);
 	    
 	    // One of the few places where handler may be null.
 	    // If handler is not null, it indicates that this object
 	    // is being reused.
 	    if (handler != null) {
 	        close();
 	    }
 	    openInternal(path, modes);
 	    
 	    if (getRuntime().getCurrentContext().isBlockGiven()) {
 	        // getRuby().getRuntime().warn("File::new does not take block; use File::open instead");
 	    }
 	    return this;
 	}
 
     public IRubyObject chmod(IRubyObject[] args) {
         checkArgumentCount(args, 1, 1);
         
         RubyInteger mode = args[0].convertToInteger();
         System.out.println(mode);
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
             
         try {
             Process chown = Runtime.getRuntime().exec("chmod " + FileMetaClass.OCTAL_FORMATTER.sprintf(mode.getLongValue()) + " " + path);
             chown.waitFor();
         } catch (IOException ioe) {
             // FIXME: ignore?
         } catch (InterruptedException ie) {
             // FIXME: ignore?
         }
         
         return getRuntime().newFixnum(0);
     }
 
     public IRubyObject chown(IRubyObject[] args) {
         checkArgumentCount(args, 1, 1);
         
         RubyInteger owner = args[0].convertToInteger();
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
             
         try {
             Process chown = Runtime.getRuntime().exec("chown " + owner + " " + path);
             chown.waitFor();
         } catch (IOException ioe) {
             // FIXME: ignore?
         } catch (InterruptedException ie) {
             // FIXME: ignore?
         }
         
         return getRuntime().newFixnum(0);
     }
 	
 	public RubyString path() {
 		return getRuntime().newString(path);
 	}
 
 	public IRubyObject stat() {
         return getRuntime().newRubyFileStat(JRubyFile.create(getRuntime().getCurrentDirectory(),path));
 	}
 	
     public IRubyObject truncate(IRubyObject arg) {
     	RubyFixnum newLength = (RubyFixnum) arg.convertToType("Fixnum", "to_int", true);
         try {
             handler.truncate(newLength.getLongValue());
         } catch (IOHandler.PipeException e) {
         	throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             // Should we do anything?
         }
         
         return RubyFixnum.zero(getRuntime());
     }
     
     public String toString() {
         return "RubyFile(" + path + ", " + modes + ", " + fileno + ")";
     }
 
     // TODO: This is also defined in the MetaClass too...Consolidate somewhere.
 	private IOModes getModes(IRubyObject object) {
 		if (object instanceof RubyString) {
 			return new IOModes(getRuntime(), ((RubyString)object).toString());
 		} else if (object instanceof RubyFixnum) {
 			return new IOModes(getRuntime(), ((RubyFixnum)object).getLongValue());
 		}
 
 		throw getRuntime().newTypeError("Invalid type for modes");
 	}
+
+    public IRubyObject inspect() {
+        StringBuffer val = new StringBuffer();
+        val.append("#<File:").append(path);
+        if(!isOpen()) {
+            val.append(" (closed)");
+        }
+        val.append(">");
+        return getRuntime().newString(val.toString());
+    }
 }
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index 579e483118..ea431392f2 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -560,1001 +560,1005 @@ public class RubyModule extends RubyObject {
     
     public void addModuleFunction(String name, ICallable method) {
         addMethod(name, method);
         addSingletonMethod(name, method);
     }   
 
     /** rb_define_module_function
      *
      */
     public void defineModuleFunction(String name, Callback method) {
         definePrivateMethod(name, method);
         defineSingletonMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void definePublicModuleFunction(String name, Callback method) {
         defineMethod(name, method);
         defineSingletonMethod(name, method);
     }
 
     private IRubyObject getConstantInner(String name, boolean exclude) {
         IRubyObject objectClass = getRuntime().getObject();
         boolean retryForModule = false;
         RubyModule p = this;
 
         retry: while (true) { 
             while (p != null) {
                 IRubyObject constant = p.getConstantAt(name);
             
                 if (constant == null) {
                     if (getRuntime().getLoadService().autoload(name) != null) {
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
         
         return callMethod("const_missing", RubySymbol.newSymbol(getRuntime(), name));
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
         ICallable method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = getRuntime().getObject().searchMethod(oldName);
             }
             
             if (method.isUndefined()) {
                 throw getRuntime().newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'");
             }
         }
         getRuntime().getCacheMap().remove(name, searchMethod(name));
         getMethods().put(name, new AliasMethod(method, oldName));
     }
 
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         IRubyObject type = getConstantAt(name);
         
         if (type == null) {
             return (RubyClass) setConstant(name, 
             		getRuntime().defineClassUnder(name, superClazz, cref)); 
         }
 
         if (!(type instanceof RubyClass)) {
         	throw getRuntime().newTypeError(name + " is not a class.");
         }
             
         return (RubyClass) type;
     }
     
     /** rb_define_class_under
      *
      */
     public RubyClass defineClassUnder(String name, RubyClass superClazz) {
     	IRubyObject type = getConstantAt(name);
     	
     	if (type == null) {
             return (RubyClass) setConstant(name, 
             		getRuntime().defineClassUnder(name, superClazz, cref)); 
     	}
 
     	if (!(type instanceof RubyClass)) {
     		throw getRuntime().newTypeError(name + " is not a class.");
         } else if (((RubyClass) type).getSuperClass().getRealClass() != superClazz) {
         	throw getRuntime().newNameError(name + " is already defined.");
         } 
             
     	return (RubyClass) type;
     }
 
     public RubyModule defineModuleUnder(String name) {
         IRubyObject type = getConstantAt(name);
         
         if (type == null) {
             return (RubyModule) setConstant(name, 
             		getRuntime().defineModuleUnder(name, cref)); 
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
             throw getRuntime().newNameError("bad constant name " + name);
         }
 
         setConstant(name, value);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     public IRubyObject removeCvar(IRubyObject name) { // Wrong Parameter ?
         if (!IdUtil.isClassVariable(name.asSymbol())) {
             throw getRuntime().newNameError("wrong class variable name " + name.asSymbol());
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
             throw getRuntime().newNameError("cannot remove " + name.asSymbol() + " for " + getName());
         }
 
         throw getRuntime().newNameError("class variable " + name.asSymbol() + " not defined for " + getName());
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
         if (readable) {
             defineMethod(name, new Callback() {
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
                 	checkArgumentCount(args, 0, 0);
 
 		    	    IRubyObject variable = self.getInstanceVariable(variableName);
 		    	
 		            return variable == null ? runtime.getNil() : variable;
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             callMethod("method_added", RubySymbol.newSymbol(getRuntime(), name));
         }
         if (writeable) {
             name = name + "=";
             defineMethod(name, new Callback() {
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
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
             callMethod("method_added", RubySymbol.newSymbol(getRuntime(), name));
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
 
         ICallable method = searchMethod(name);
 
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method '" + name + "' for " + 
                                 (isModule() ? "module" : "class") + " '" + getName() + "'");
         }
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 final ThreadContext context = getRuntime().getCurrentContext();
                 addMethod(name, new CallbackMethod(this, new Callback() {
 	                public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
 				        return context.callSuper(context.getFrameArgs());
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
         ICallable method = searchMethod(name);
         if (!method.isUndefined()) {
             return !(checkVisibility && method.getVisibility().isPrivate());
         }
         return false;
     }
 
     public IRubyObject newMethod(IRubyObject receiver, String name, boolean bound) {
         ICallable method = searchMethod(name);
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method `" + name + 
                 "' for class `" + this.getName() + "'");
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
     public IRubyObject define_method(IRubyObject[] args) {
         if (args.length < 1 || args.length > 2) {
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         IRubyObject body;
         String name = args[0].asSymbol();
         ICallable newMethod = null;
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility visibility = tc.getCurrentVisibility();
 
         if (visibility.isModuleFunction()) {
             visibility = Visibility.PRIVATE;
         }
         
         
         if (args.length == 1 || args[1].isKindOf(getRuntime().getClass("Proc"))) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = (args.length == 1) ? getRuntime().newProc() : (RubyProc)args[1];
             body = proc;
             
             proc.getBlock().isLambda = true;
             proc.getBlock().getFrame().setLastClass(this);
             proc.getBlock().getFrame().setLastFunc(name);
             
             newMethod = new ProcMethod(this, proc, visibility);
         } else if (args[1].isKindOf(getRuntime().getClass("Method"))) {
             RubyMethod method = (RubyMethod)args[1];
             body = method;
             
             newMethod = new MethodMethod(this, method.unbind(), visibility);
         } else {
             throw getRuntime().newTypeError("wrong argument type " + args[0].getType().getName() + " (expected Proc/Method)");
         }
 
         addMethod(name, newMethod);
 
         RubySymbol symbol = RubySymbol.newSymbol(getRuntime(), name);
         if (tc.getPreviousVisibility().isModuleFunction()) {
             getSingletonClass().addMethod(name, new WrapperCallable(getSingletonClass(), newMethod, Visibility.PUBLIC));
             callMethod("singleton_method_added", symbol);
         }
 
         callMethod("method_added", symbol);
 
         return body;
     }
 
     public IRubyObject executeUnder(Callback method, IRubyObject[] args) {
         ThreadContext context = getRuntime().getCurrentContext();
         
         context.preExecuteUnder(this);
 
         try {
             return method.execute(this, args);
         } finally {
             context.postExecuteUnder();
         }
     }
 
     // Methods of the Module Class (rb_mod_*):
 
     public static RubyModule newModule(IRuby runtime, String name) {
         return newModule(runtime, name, null);
     }
 
     public static RubyModule newModule(IRuby runtime, String name, SinglyLinkedList parentCRef) {
         // Modules do not directly define Object as their superClass even though in theory they
     	// should.  The C version of Ruby may also do this (special checks in rb_alias for Module
     	// makes me think this).
         // TODO cnutter: Shouldn't new modules have Module as their superclass?
         return new RubyModule(runtime, runtime.getClass("Module"), null, parentCRef, name);
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
             ICallable method = (ICallable) entry.getValue();
 
             // Do not clone cached methods
             if (method.getImplementationClass() == realType) {            
                 // A cloned method now belongs to a new class.  Set it.
                 // TODO: Make ICallable immutable
                 ICallable clonedMethod = method.dup();
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
             list.add(p.getNonIncludedClass());
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
     public IRubyObject initialize(IRubyObject[] args) {
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
             throw getRuntime().newNameError("wrong constant name " + name);
         }
 
         return getConstant(name);
     }
 
     /** rb_mod_const_set
      *
      */
     public IRubyObject const_set(IRubyObject symbol, IRubyObject value) {
         String name = symbol.asSymbol();
 
         if (!IdUtil.isConstant(name)) {
             throw getRuntime().newNameError("wrong constant name " + name);
         }
 
         return setConstant(name, value); 
     }
 
     /** rb_mod_const_defined
      *
      */
     public RubyBoolean const_defined(IRubyObject symbol) {
         String name = symbol.asSymbol();
 
         if (!IdUtil.isConstant(name)) {
             throw getRuntime().newNameError("wrong constant name " + name);
         }
 
         return getRuntime().newBoolean(getConstantAt(name) != null);
     }
 
     private RubyArray instance_methods(IRubyObject[] args, final Visibility visibility) {
         boolean includeSuper = args.length > 0 ? args[0].isTrue() : true;
         RubyArray ary = getRuntime().newArray();
         HashMap undefinedMethods = new HashMap();
 
         for (RubyModule type = this; type != null; type = type.getSuperClass()) {
         	RubyModule realType = type.getNonIncludedClass();
             for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext();) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 ICallable method = (ICallable) entry.getValue();
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
             throw getRuntime().newNameError("wrong class variable name " + id);
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
             throw getRuntime().newNameError("cannot remove " + id + " for " + getName());
         }
         throw getRuntime().newNameError("class variable " + id + " not defined for " + getName());
     }
     
     public IRubyObject remove_const(IRubyObject name) {
         String id = name.asSymbol();
 
         if (!IdUtil.isConstant(id)) {
             throw getRuntime().newNameError("wrong constant name " + id);
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
             throw getRuntime().newNameError("cannot remove " + id + " for " + getName());
         }
         throw getRuntime().newNameError("constant " + id + " not defined for " + getName());
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
         for (int i = modules.length - 1; i >= 0; i--) {
             modules[i].callMethod("append_features", this);
             modules[i].callMethod("included", this);
         }
 
         return this;
     }
     
     public IRubyObject included(IRubyObject other) {
         return getRuntime().getNil();
     }
     
     public IRubyObject extended(IRubyObject other) {
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
     public RubyModule rbPublic(IRubyObject[] args) {
         setVisibility(args, Visibility.PUBLIC);
         return this;
     }
 
     /** rb_mod_protected
      *
      */
     public RubyModule rbProtected(IRubyObject[] args) {
         setVisibility(args, Visibility.PROTECTED);
         return this;
     }
 
     /** rb_mod_private
      *
      */
     public RubyModule rbPrivate(IRubyObject[] args) {
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
 
         if (args.length == 0) {
             getRuntime().getCurrentContext().setCurrentVisibility(Visibility.MODULE_FUNCTION);
         } else {
             setMethodVisibility(args, Visibility.PRIVATE);
 
             for (int i = 0; i < args.length; i++) {
                 String name = args[i].asSymbol();
                 ICallable method = searchMethod(name);
                 assert !method.isUndefined() : "undefined method '" + name + "'";
                 getSingletonClass().addMethod(name, new WrapperCallable(getSingletonClass(), method, Visibility.PUBLIC));
                 callMethod("singleton_method_added", RubySymbol.newSymbol(getRuntime(), name));
             }
         }
         return this;
     }
     
     public IRubyObject method_added(IRubyObject nothing) {
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
 
     public IRubyObject module_eval(IRubyObject[] args) {
         return specificEval(this, args);
     }
 
     public RubyModule remove_method(IRubyObject name) {
         removeMethod(name.asSymbol());
         return this;
     }
 
     public void marshalTo(MarshalStream output) throws java.io.IOException {
         output.write('m');
         output.dumpString(name().toString());
     }
 
     public static RubyModule unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = input.unmarshalString();
         IRuby runtime = input.getRuntime();
         RubyModule result = runtime.getClassFromPath(name);
         if (result == null) {
             throw runtime.newNameError("uninitialized constant " + name);
         }
         input.registerLinkTarget(result);
         return result;
     }
 
     public SinglyLinkedList getCRef() {
         return cref;
     }
+
+    public IRubyObject inspect() {
+        return callMethod("to_s");
+    }
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index bd44615932..c8dd4864ea 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1197 +1,1203 @@
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
+ * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
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
 import java.util.Map;
+import java.util.Collections;
 
 import org.jruby.ast.Node;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ICallable;
 import org.jruby.runtime.Iter;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.util.PrintfFormat;
 import org.jruby.util.collections.SinglyLinkedList;
 
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
         this(runtime, metaClass, true);
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
         return other == this || other instanceof IRubyObject && callMethod("==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return ((RubyString) callMethod("to_s")).toString();
     }
 
     /** Getter for property ruby.
      * @return Value of property ruby.
      */
     public IRuby getRuntime() {
         return metaClass.getRuntime();
     }
 
     public IRubyObject removeInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().remove(name);
     }
 
+    /**
+     * Returns an unmodifiable snapshot of the current state of instance variables.
+     * This method synchronizes access to avoid deadlocks.
+     */
+    public Map getInstanceVariablesSnapshot() {
+        synchronized(getInstanceVariables()) {
+            return Collections.unmodifiableMap(new HashMap(getInstanceVariables()));
+        }
+    }
+
     public Map getInstanceVariables() {
     	// TODO: double checking may or may not be safe enough here
     	if (instanceVariables == null) {
 	    	synchronized (this) {
 	    		if (instanceVariables == null) {
-	    			instanceVariables = new HashMap();
+                            instanceVariables = Collections.synchronizedMap(new HashMap());
 	    		}
 	    	}
     	}
         return instanceVariables;
     }
 
     public void setInstanceVariables(Map instanceVariables) {
-        this.instanceVariables = instanceVariables;
+        this.instanceVariables = Collections.synchronizedMap(instanceVariables);
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
 
     public void addSingletonMethod(String name, ICallable method) {
         getSingletonClass().addMethod(name, method);
     }
 
     /* rb_init_ccopy */
     public void initCopy(IRubyObject original) {
         assert original != null;
         assert !isFrozen() : "frozen object (" + getMetaClass().getName() + ") allocated";
 
         setInstanceVariables(new HashMap(original.getInstanceVariables()));
 
         callMethod("initialize_copy", original);        
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
     public IRubyObject callMethod(String name, IRubyObject[] args) {
         return callMethod(getMetaClass(), name, args, CallType.FUNCTIONAL);
     }
 
     /**
      * 
      */
     public IRubyObject callMethod(String name, IRubyObject[] args,
             CallType callType) {
         return callMethod(getMetaClass(), name, args, callType);
     }
 
     /**
      * 
      */
     public IRubyObject callMethod(RubyModule context, String name, IRubyObject[] args, 
             CallType callType) {
         assert args != null;
         ICallable method = null;
         
         method = context.searchMethod(name);
         
         if (method.isUndefined() ||
             !(name.equals("method_missing") ||
               method.isCallableFrom(getRuntime().getCurrentContext().getFrameSelf(), callType))) {
             if (callType == CallType.SUPER) {
                 throw getRuntime().newNameError("super: no superclass method '" + name + "'");
             }
 
             // store call information so method_missing impl can use it
             getRuntime().getCurrentContext().setLastCallStatus(method.getVisibility(), callType);
 
             if (name.equals("method_missing")) {
                 return RubyKernel.method_missing(this, args);
             }
 
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             System.arraycopy(args, 0, newArgs, 1, args.length);
             newArgs[0] = RubySymbol.newSymbol(getRuntime(), name);
 
             return callMethod("method_missing", newArgs);
         }
         
         RubyModule implementer = null;
         if (method.needsImplementer()) {
             // modules are included with a shim class; we must find that shim to handle super() appropriately
             implementer = context.findImplementer(method.getImplementationClass());
         } else {
             // classes are directly in the hierarchy, so no special logic is necessary for implementer
             implementer = method.getImplementationClass();
         }
         
         String originalName = method.getOriginalName();
         if (originalName != null) {
             name = originalName;
         }
 
         IRubyObject result = method.call(getRuntime(), this, implementer, name, args, false);
         
         return result;
     }
 
     public IRubyObject callMethod(String name) {
         return callMethod(name, IRubyObject.NULL_ARRAY);
     }
 
     /**
      * rb_funcall
      * 
      */
     public IRubyObject callMethod(String name, IRubyObject arg) {
         return callMethod(name, new IRubyObject[] { arg });
     }
     
     public IRubyObject instance_variable_get(IRubyObject var) {
     	String varName = var.asSymbol();
     	
     	if (!varName.startsWith("@")) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name");
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
     	
     	if (!varName.startsWith("@")) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name");
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
         EvaluationState state = getRuntime().getCurrentContext().getFrameEvalState();
         IRubyObject oldSelf = state.getSelf();
         state.setSelf(this);
         try {
             return state.begin(n);
         } finally {
             state.setSelf(oldSelf);
         }
     }
 
     public void callInit(IRubyObject[] args) {
         ThreadContext tc = getRuntime().getCurrentContext();
         
         tc.setIfBlockAvailable();
         try {
             callMethod("initialize", args);
         } finally {
             tc.clearIfBlockAvailable();
         }
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
                     "cannot convert " + getMetaClass().getName() + " into " + targetType);
                 // FIXME nil, true and false instead of NilClass, TrueClass, FalseClass;
             } 
 
             return getRuntime().getNil();
         }
         return callMethod(convertMethod);
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
     public IRubyObject specificEval(RubyModule mod, IRubyObject[] args) {
         ThreadContext tc = getRuntime().getCurrentContext();
         
         if (tc.isBlockGiven()) {
             if (args.length > 0) {
                 throw getRuntime().newArgumentError(args.length, 0);
             }
             return yieldUnder(mod);
         }
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
             public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
                 IRubyObject source = args[1];
                 IRubyObject filename = args[2];
                 // FIXME: lineNumber is not supported
                 //IRubyObject lineNumber = args[3];
                 
                 return args[0].evalSimple(source,
                                   ((RubyString) filename).toString());
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this, src, file, line });
     }
 
     private IRubyObject yieldUnder(RubyModule under) {
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
                 ThreadContext context = getRuntime().getCurrentContext();
 
                 Block block = (Block) context.getCurrentBlock();
                 Visibility savedVisibility = block.getVisibility();
 
                 block.setVisibility(Visibility.PUBLIC);
                 try {
                     IRubyObject valueInYield = args[0];
                     IRubyObject selfInYield = args[0];
                     return context.yieldCurrentBlock(valueInYield, selfInYield, context.getRubyClass(), false);
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
         }, new IRubyObject[] { this });
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalWithBinding(org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalWithBinding(IRubyObject src, IRubyObject scope, String file) {
         // both of these are ensured by the (very few) callers
         assert !scope.isNil();
         assert file != null;
         
         ThreadContext threadContext = getRuntime().getCurrentContext();
         
         ISourcePosition savedPosition = threadContext.getPosition();
         IRubyObject oldSelf = null;
         EvaluationState state = null;
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
         
         try {
             // Binding provided for scope, use it
             threadContext.preEvalWithBinding((RubyBinding)scope);            
             newSelf = threadContext.getFrameSelf();
 
             state = threadContext.getFrameEvalState();
             oldSelf = state.getSelf();
             state.setSelf(newSelf);
             
             result = state.begin(getRuntime().parse(src.toString(), file));
         } finally {
             // return the eval state to its original self
             state.setSelf(oldSelf);
             threadContext.postBoundEvalOrYield();
             
             // restore position
             threadContext.setPosition(savedPosition);
         }
         return result;
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalSimple(org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalSimple(IRubyObject src, String file) {
         // this is ensured by the callers
         assert file != null;
         
         ThreadContext threadContext = getRuntime().getCurrentContext();
         
         ISourcePosition savedPosition = threadContext.getPosition();
         // no binding, just eval in "current" frame (caller's frame)
         Iter iter = threadContext.getFrameIter();
         EvaluationState state = threadContext.getFrameEvalState();
         IRubyObject oldSelf = state.getSelf();
         IRubyObject result = getRuntime().getNil();
         
         try {
             // hack to avoid using previous frame if we're the first frame, since this eval is used to start execution too
             if (threadContext.getPreviousFrame() != null) {
                 threadContext.setFrameIter(threadContext.getPreviousFrameIter());
             }
             
             state.setSelf(this);
             
             result = state.begin(getRuntime().parse(src.toString(), file));
         } finally {
             // return the eval state to its original self
             state.setSelf(oldSelf);
             
             // FIXME: this is broken for Proc, see above
             threadContext.setFrameIter(iter);
             
             // restore position
             threadContext.setPosition(savedPosition);
         }
         
         return result;
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      */
     public IRubyObject equal(IRubyObject obj) {
         if (isNil()) {
             return getRuntime().newBoolean(obj.isNil());
         }
         return getRuntime().newBoolean(this == obj);
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
      * <b>Warning:</b> In JRuby there is no guarantee that two objects have different ids.
      *
      * <i>CRuby function: rb_obj_id</i>
      *
      */
     public RubyFixnum id() {
         return getRuntime().newFixnum(System.identityHashCode(this));
     }
 
     public RubyFixnum hash() {
         return getRuntime().newFixnum(System.identityHashCode(this));
     }
     
     public final int hashCode() {
     	return (int) RubyNumeric.fix2int(callMethod("hash"));
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
     	return getMetaClass().getRealClass().allocate();
     }
     
     public IRubyObject display(IRubyObject[] args) {
         IRubyObject port = args.length == 0
             ? getRuntime().getGlobalVariables().get("$>") : args[0];
         
         port.callMethod("write", this);
 
         return getRuntime().getNil();
     }
     
     /** rb_obj_dup
      *
      */
     public IRubyObject dup() {
         IRubyObject dup = callMethod("clone");
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
-//        if(getInstanceVariables().size() > 0) {
-//            StringBuffer part = new StringBuffer();
-//            String cname = getMetaClass().getRealClass().getName();
-//            part.append("#<").append(cname).append(":0x");
-//            part.append(Integer.toHexString(System.identityHashCode(this)));
-//            part.append(" ");
-//            if(!getRuntime().registerInspecting(this)) {
-//                /* 6:tags 16:addr 1:eos */
-//                part.append("...>");
-//                return getRuntime().newString(part.toString());
-//            }
-//            try {
-//                String sep = "";
-//                for (Iterator iter = instanceVariableNames(); iter.hasNext();) {
-//                    String name = (String) iter.next();
-//                    part.append(sep);
-//                    part.append(name);
-//                    part.append("=");
-//                    part.append(getInstanceVariable(name).callMethod("inspect"));
-//                    sep = ", ";
-//                }
-//                part.append(">");
-//                return getRuntime().newString(part.toString());
-//            } finally {
-//                getRuntime().unregisterInspecting(this);
-//            }
-//        }
+        if(getInstanceVariables().size() > 0) {
+            StringBuffer part = new StringBuffer();
+            String cname = getMetaClass().getRealClass().getName();
+            part.append("#<").append(cname).append(":0x");
+            part.append(Integer.toHexString(System.identityHashCode(this)));
+            part.append(" ");
+            if(!getRuntime().registerInspecting(this)) {
+                /* 6:tags 16:addr 1:eos */
+                part.append("...>");
+                return getRuntime().newString(part.toString());
+            }
+            try {
+                String sep = "";
+                Map iVars = getInstanceVariablesSnapshot();
+                for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
+                    String name = (String) iter.next();
+                    part.append(sep);
+                    part.append(name);
+                    part.append("=");
+                    part.append(((IRubyObject)(iVars.get(name))).callMethod("inspect"));
+                    sep = ", ";
+                }
+                part.append(">");
+                return getRuntime().newString(part.toString());
+            } finally {
+                getRuntime().unregisterInspecting(this);
+            }
+        }
         return callMethod("to_s");
     }
 
     /** rb_obj_is_instance_of
      *
      */
     public RubyBoolean instance_of(IRubyObject type) {
         return getRuntime().newBoolean(type() == type);
     }
 
     public RubyArray instance_variables() {
         ArrayList names = new ArrayList();
-        Iterator iter = instanceVariableNames();
-        while (iter.hasNext()) {
-            String name = (String) iter.next();
-            names.add(getRuntime().newString(name));
+        for(Iterator iter = getInstanceVariablesSnapshot().keySet().iterator();iter.hasNext();) {
+            names.add(getRuntime().newString((String)iter.next()));
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
     public RubyArray singleton_methods() {
         RubyArray result = getRuntime().newArray();
         
         for (RubyClass type = getMetaClass(); type != null && type instanceof MetaClass; 
              type = type.getSuperClass()) { 
         	for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext(); ) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 ICallable method = (ICallable) entry.getValue();
 
                 // We do not want to capture cached methods
                 if (method.getImplementationClass() != type) {
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
 
     public IRubyObject instance_eval(IRubyObject[] args) {
         return specificEval(getSingletonClass(), args);
     }
 
     public IRubyObject extend(IRubyObject[] args) {
         checkArgumentCount(args, 1, -1);
         
         // Make sure all arguments are modules before calling the callbacks
         RubyClass module = getRuntime().getClass("Module");
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isKindOf(module)) {
                 throw getRuntime().newTypeError(args[i], module);
             }
         }
         
         for (int i = 0; i < args.length; i++) {
             args[i].callMethod("extend_object", this);
             args[i].callMethod("extended", this);
         }
         return this;
     }
     
     public IRubyObject inherited(IRubyObject arg) {
     	return getRuntime().getNil();
     }
     public IRubyObject initialize(IRubyObject[] args) {
     	return getRuntime().getNil();
     }
 
     public IRubyObject method_missing(IRubyObject[] args) {
         if (args.length == 0) {
             throw getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = callMethod("inspect").toString();
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = new PrintfFormat(format).sprintf(new Object[] { name, description, 
             noClass ? "" : ":", noClass ? "" : getType().getName()});
 
         if (lastCallType == CallType.VARIABLE) {
         	throw getRuntime().newNameError(msg);
         }
         throw getRuntime().newNoMethodError(msg);
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
     public IRubyObject send(IRubyObject[] args) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asSymbol();
 
         IRubyObject[] newArgs = new IRubyObject[args.length - 1];
         System.arraycopy(args, 1, newArgs, 0, newArgs.length);
         
         ThreadContext tc = getRuntime().getCurrentContext();
         
         tc.setIfBlockAvailable();
         try {
             return callMethod(name, newArgs, CallType.FUNCTIONAL);
         } finally {
             tc.clearIfBlockAvailable();
         }
     }
     
     public IRubyObject nil_p() {
     	return getRuntime().getFalse();
     }
     
     public IRubyObject match(IRubyObject arg) {
     	return getRuntime().getFalse();
     }
     
    public IRubyObject remove_instance_variable(IRubyObject name) {
        String id = name.asSymbol();
 
        if (!IdUtil.isInstanceVariable(id)) {
            throw getRuntime().newNameError("wrong instance variable name " + id);
        }
        if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
            throw getRuntime().newSecurityError("Insecure: can't remove instance variable");
        }
        testFrozen("class/module");
 
        IRubyObject variable = removeInstanceVariable(id); 
        if (variable != null) {
            return variable;
        }
 
        throw getRuntime().newNameError("instance variable " + id + " not defined");
    }
 
     public void marshalTo(MarshalStream output) throws java.io.IOException {
         output.write('o');
         RubySymbol classname = RubySymbol.newSymbol(getRuntime(), getMetaClass().getName());
         output.dumpObject(classname);
-
-        output.dumpInt(getInstanceVariables().size());
-        
-        for (Iterator iter = instanceVariableNames(); iter.hasNext();) {
+        Map iVars = getInstanceVariablesSnapshot();
+        output.dumpInt(iVars.size());
+        for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
             String name = (String) iter.next();
-            IRubyObject value = getInstanceVariable(name);
-
-            // Between getting name and retrieving value the instance variable could have been
-            // removed
-            if (value != null) {
-            	output.dumpObject(RubySymbol.newSymbol(getRuntime(), name));
-            	output.dumpObject(value);
-            }
+            IRubyObject value = (IRubyObject)iVars.get(name);
+            
+            output.dumpObject(RubySymbol.newSymbol(getRuntime(), name));
+            output.dumpObject(value);
         }
     }
    
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 }
diff --git a/src/org/jruby/runtime/builtin/IRubyObject.java b/src/org/jruby/runtime/builtin/IRubyObject.java
index 708fc941bf..f6027fd159 100644
--- a/src/org/jruby/runtime/builtin/IRubyObject.java
+++ b/src/org/jruby/runtime/builtin/IRubyObject.java
@@ -1,313 +1,315 @@
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
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
+ * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
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
 package org.jruby.runtime.builtin;
 
 import java.io.IOException;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jruby.MetaClass;
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyFloat;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.ast.Node;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 
 /** Object is the parent class of all classes in Ruby. Its methods are
  * therefore available to all objects unless explicitly overridden.
  *
  * @author  jpetersen
  */
 public interface IRubyObject {
     public static final IRubyObject[] NULL_ARRAY = new IRubyObject[0];
     
     /**
      * RubyMethod getInstanceVar.
      * @param string
      * @return RubyObject
      */
     IRubyObject getInstanceVariable(String string);
 
     /**
      * RubyMethod setInstanceVar.
      * @param string
      * @param rubyObject
      * @return RubyObject
      */
     IRubyObject setInstanceVariable(String string, IRubyObject rubyObject);
     
     Map getInstanceVariables();
+    Map getInstanceVariablesSnapshot();
 
     IRubyObject callMethod(RubyModule context, String name, IRubyObject[] args, CallType callType);
     
     IRubyObject callMethod(String name, IRubyObject[] args, CallType callType);
     
     /**
      * RubyMethod funcall.
      * @param string
      * @return RubyObject
      */
     IRubyObject callMethod(String string);
 
     /**
      * RubyMethod isNil.
      * @return boolean
      */
     boolean isNil();
 
     boolean isTrue();
 
     /**
      * RubyMethod isTaint.
      * @return boolean
      */
     boolean isTaint();
 
     /**
      * RubyMethod isFrozen.
      * @return boolean
      */
     boolean isFrozen();
 
     /**
      * RubyMethod funcall.
      * @param string
      * @param arg
      * @return RubyObject
      */
     IRubyObject callMethod(String string, IRubyObject arg);
 
     /**
      * RubyMethod getRubyClass.
      */
     RubyClass getMetaClass();
 
     void setMetaClass(RubyClass metaClass);
 
     /**
      * RubyMethod getSingletonClass.
      * @return RubyClass
      */
     MetaClass getSingletonClass();
 
     /**
      * RubyMethod getType.
      * @return RubyClass
      */
     RubyClass getType();
 
     /**
      * RubyMethod isKindOf.
      * @param rubyClass
      * @return boolean
      */
     boolean isKindOf(RubyModule rubyClass);
 
     /**
      * RubyMethod respondsTo.
      * @param string
      * @return boolean
      */
     boolean respondsTo(String string);
 
     /**
      * RubyMethod getRuntime.
      */
     IRuby getRuntime();
 
     /**
      * RubyMethod getJavaClass.
      * @return Class
      */
     Class getJavaClass();
 
     /**
      * RubyMethod callMethod.
      * @param method
      * @param rubyArgs
      * @return IRubyObject
      */
     IRubyObject callMethod(String method, IRubyObject[] rubyArgs);
 
     /**
      * RubyMethod eval.
      * @param iNode
      * @return IRubyObject
      */
     IRubyObject eval(Node iNode);
 
     /**
      * Evaluate the given string under the specified binding object. If the binding is not a Proc or Binding object
      * (RubyProc or RubyBinding) throw an appropriate type error.
      * @param evalString The string containing the text to be evaluated
      * @param binding The binding object under which to perform the evaluation
      * @param file The filename to use when reporting errors during the evaluation
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalWithBinding(IRubyObject evalString, IRubyObject binding, String file);
 
     /**
      * Evaluate the given string.
      * @param evalString The string containing the text to be evaluated
      * @param binding The binding object under which to perform the evaluation
      * @param file The filename to use when reporting errors during the evaluation
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalSimple(IRubyObject evalString, String file);
 
     /**
      * RubyMethod extendObject.
      * @param rubyModule
      */
     void extendObject(RubyModule rubyModule);
 
     /**
      * Convert the object into a symbol name if possible.
      * 
      * @return String the symbol name
      */
     String asSymbol();
 
     /**
      * Methods which perform to_xxx if the object has such a method
      */
     RubyArray convertToArray();
     RubyFloat convertToFloat();
     RubyInteger convertToInteger();
     RubyString convertToString();
 
     /**
      * Converts this object to type 'targetType' using 'convertMethod' method (MRI: convert_type).
      * 
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @param raiseOnError will throw an Error if conversion does not work
      * @return the converted value
      */
     IRubyObject convertToType(String targetType, String convertMethod, boolean raiseOnError);
 
     /**
      * Higher level conversion utility similiar to convertToType but it can throw an
      * additional TypeError during conversion (MRI: rb_check_convert_type).
      * 
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @return the converted value
      */
     IRubyObject convertToTypeWithCheck(String targetType, String convertMethod);
 
 
     /**
      * RubyMethod setTaint.
      * @param b
      */
     void setTaint(boolean b);
 
     /**
      * RubyMethod checkSafeString.
      */
     void checkSafeString();
 
     /**
      * RubyMethod marshalTo.
      * @param marshalStream
      */
     void marshalTo(MarshalStream marshalStream) throws IOException;
 
     /**
      * RubyMethod convertType.
      * @param type
      * @param string
      * @param string1
      */
     IRubyObject convertType(Class type, String string, String string1);
 
     /**
      * RubyMethod dup.
      */
     IRubyObject dup();
 
     /**
      * RubyMethod setupClone.
      * @param original
      */
     void initCopy(IRubyObject original);
 
     /**
      * RubyMethod setFrozen.
      * @param b
      */
     void setFrozen(boolean b);
 
     /**
      * RubyMethod inspect.
      * @return String
      */
     IRubyObject inspect();
 
     /**
      * Make sure the arguments fit the range specified by minimum and maximum.  On
      * a failure, The Ruby runtime will generate an ArgumentError.
      * 
      * @param arguments to check
      * @param minimum number of args
      * @param maximum number of args (-1 for any number of args)
      * @return the number of arguments in args
      */
     int checkArgumentCount(IRubyObject[] arguments, int minimum, int maximum);
 
     /**
      * RubyMethod rbClone.
      * @return IRubyObject
      */
     IRubyObject rbClone();
 
 
     public void callInit(IRubyObject[] args);
 
     /**
      * RubyMethod defineSingletonMethod.
      * @param name
      * @param callback
      */
     void defineSingletonMethod(String name, Callback callback);
 
     boolean singletonMethodsAllowed();
 
 	Iterator instanceVariableNames();
 }
diff --git a/src/org/jruby/runtime/marshal/MarshalStream.java b/src/org/jruby/runtime/marshal/MarshalStream.java
index 57cb7bfe62..a41f611f60 100644
--- a/src/org/jruby/runtime/marshal/MarshalStream.java
+++ b/src/org/jruby/runtime/marshal/MarshalStream.java
@@ -1,221 +1,218 @@
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
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
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
 package org.jruby.runtime.marshal;
 
 import java.io.FilterOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.util.Iterator;
+import java.util.Map;
 
 import org.jruby.IRuby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * Marshals objects into Ruby's binary marshal format.
  *
  * @author Anders
  */
 public class MarshalStream extends FilterOutputStream {
     private final IRuby runtime;
     private final int depthLimit;
     private int depth = 0;
     private MarshalCache cache;
 
     private final static char TYPE_IVAR = 'I';
     private final static char TYPE_USRMARSHAL = 'U';
     private final static char TYPE_USERDEF = 'u';
     
     protected static char TYPE_UCLASS = 'C';
 
     public MarshalStream(IRuby runtime, OutputStream out, int depthLimit) throws IOException {
         super(out);
 
         this.runtime = runtime;
         this.depthLimit = depthLimit >= 0 ? depthLimit : Integer.MAX_VALUE;
         this.cache = new MarshalCache();
 
         out.write(Constants.MARSHAL_MAJOR);
         out.write(Constants.MARSHAL_MINOR);
     }
 
     public void dumpObject(IRubyObject value) throws IOException {
         depth++;
         if (depth > depthLimit) {
             throw runtime.newArgumentError("exceed depth limit");
         }
         if (! shouldBeRegistered(value)) {
             writeDirectly(value);
         } else if (hasNewUserDefinedMarshaling(value)) {
             userNewMarshal(value);
         } else if (hasUserDefinedMarshaling(value)) {
             userMarshal(value);
         } else {
             writeAndRegister(value);
         }
         depth--;
         if (depth == 0) {
         	out.flush(); // flush afer whole dump is complete
         }
     }
     
     public void writeUserClass(IRubyObject obj, RubyClass baseClass, MarshalStream output) throws IOException {
     	// TODO: handle w_extended code here
     	
     	if (obj.getMetaClass().equals(baseClass)) {
     		// do nothing, simple builtin type marshalling
     		return;
     	}
     	
     	output.write(TYPE_UCLASS);
     	
     	// w_unique
     	if (obj.getMetaClass().getName().charAt(0) == '#') {
     		throw obj.getRuntime().newTypeError("Can't dump anonymous class");
     	}
     	
     	// w_symbol
     	// TODO: handle symlink?
     	output.dumpObject(RubySymbol.newSymbol(obj.getRuntime(), obj.getMetaClass().getName()));
     }
     
     public void writeInstanceVars(IRubyObject obj, MarshalStream output) throws IOException {
     	IRuby runtime = obj.getRuntime();
-        output.dumpInt(obj.getInstanceVariables().size());
-        
-        for (Iterator iter = obj.instanceVariableNames(); iter.hasNext();) {
+        Map iVars = obj.getInstanceVariablesSnapshot();
+        output.dumpInt(iVars.size());
+        for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
             String name = (String) iter.next();
-            IRubyObject value = obj.getInstanceVariable(name);
-
-            // Between getting name and retrieving value the instance variable could have been
-            // removed
-            if (value != null) {
-            	output.dumpObject(runtime.newSymbol(name));
-            	output.dumpObject(value);
-            }
+            IRubyObject value = (IRubyObject)iVars.get(name);
+            
+            output.dumpObject(runtime.newSymbol(name));
+            output.dumpObject(value);
         }
     }
 
     private void writeDirectly(IRubyObject value) throws IOException {
         if (value.isNil()) {
             out.write('0');
         } else {
             value.marshalTo(this);
         }
     }
 
     private boolean shouldBeRegistered(IRubyObject value) {
         if (value.isNil()) {
             return false;
         } else if (value instanceof RubyBoolean) {
             return false;
         } else if (value instanceof RubyFixnum) {
             return false;
         }
         return true;
     }
 
     private void writeAndRegister(IRubyObject value) throws IOException {
         if (cache.isRegistered(value)) {
             cache.writeLink(this, value);
         } else {
             cache.register(value);
             value.marshalTo(this);
         }
     }
 
     private boolean hasUserDefinedMarshaling(IRubyObject value) {
         return value.respondsTo("_dump");
     }
 
     private boolean hasNewUserDefinedMarshaling(IRubyObject value) {
         return value.respondsTo("marshal_dump");
     }
 
     private void userMarshal(IRubyObject value) throws IOException {
         out.write(TYPE_USERDEF);
         dumpObject(RubySymbol.newSymbol(runtime, value.getMetaClass().getName()));
 
         RubyString marshaled = (RubyString) value.callMethod("_dump", runtime.newFixnum(depthLimit)); 
         dumpString(marshaled.toString());
     }
 
     private void userNewMarshal(final IRubyObject value) throws IOException {
         out.write(TYPE_USRMARSHAL);
         dumpObject(RubySymbol.newSymbol(runtime, value.getMetaClass().getName()));
 
         IRubyObject marshaled =  value.callMethod("marshal_dump"); 
         dumpObject(marshaled);
     }
 
     public void dumpString(String value) throws IOException {
         dumpInt(value.length());
         out.write(RubyString.stringToBytes(value));
     }
 
     public void dumpInt(int value) throws IOException {
         if (value == 0) {
             out.write(0);
         } else if (0 < value && value < 123) {
             out.write(value + 5);
         } else if (-124 < value && value < 0) {
             out.write((value - 5) & 0xff);
         } else {
             int[] buf = new int[4];
             int i;
             for (i = 0; i < buf.length; i++) {
                 buf[i] = value & 0xff;
                 value = value >> 8;
                 if (value == 0 || value == -1) {
                     break;
                 }
             }
             int len = i + 1;
             out.write(value < 0 ? -len : len);
             for (i = 0; i < len; i++) {
                 out.write(buf[i]);
             }
         }
     }
 
 	public void writeIVar(IRubyObject obj, MarshalStream output) throws IOException {
 		if (obj.getInstanceVariables().size() > 0) {
 			out.write(TYPE_IVAR);
 		} 
 	}
 }
