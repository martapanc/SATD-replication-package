diff --git a/src/org/jruby/IRuby.java b/src/org/jruby/IRuby.java
index 728c8583c4..e660bf4a01 100644
--- a/src/org/jruby/IRuby.java
+++ b/src/org/jruby/IRuby.java
@@ -1,371 +1,374 @@
 package org.jruby;
 
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.Reader;
 import java.util.Hashtable;
 import java.util.List;
 import java.util.Random;
 
 import org.jruby.ast.Node;
 import org.jruby.common.RubyWarnings;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.Parser;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public interface IRuby {
 
 	/**
 	 * Retrieve mappings of cached methods to where they have been cached.  When a cached
 	 * method needs to be invalidated this map can be used to remove all places it has been
 	 * cached.
 	 * 
 	 * @return the mappings of where cached methods have been stored
 	 */
 	public CacheMap getCacheMap();
 
 	/**
 	 * Evaluates a script and returns a RubyObject.
 	 */
 	public IRubyObject evalScript(String script);
 
 	public IRubyObject eval(Node node);
 
 	public RubyClass getObject();
     
     public RubyModule getKernel();
     
     public RubyClass getString();
     
     public RubyClass getFixnum();
 
 	/** Returns the "true" instance from the instance pool.
 	 * @return The "true" instance.
 	 */
 	public RubyBoolean getTrue();
 
 	/** Returns the "false" instance from the instance pool.
 	 * @return The "false" instance.
 	 */
 	public RubyBoolean getFalse();
 
 	/** Returns the "nil" singleton instance.
 	 * @return "nil"
 	 */
 	public IRubyObject getNil();
     
     /**
      * @return The NilClass class
      */
     public RubyClass getNilClass();
 
 	public RubyModule getModule(String name);
 
 	/** Returns a class from the instance pool.
 	 *
 	 * @param name The name of the class.
 	 * @return The class.
 	 */
 	public RubyClass getClass(String name);
 
 	/** Define a new class with name 'name' and super class 'superClass'.
 	 *
 	 * MRI: rb_define_class / rb_define_class_id
 	 *
 	 */
 	public RubyClass defineClass(String name, RubyClass superClass);
 
 	public RubyClass defineClassUnder(String name, RubyClass superClass, SinglyLinkedList parentCRef);
 
 	/** rb_define_module / rb_define_module_id
 	 *
 	 */
 	public RubyModule defineModule(String name);
 
 	public RubyModule defineModuleUnder(String name, SinglyLinkedList parentCRef);
 
 	/**
 	 * In the current context, get the named module. If it doesn't exist a
 	 * new module is created.
 	 */
 	public RubyModule getOrCreateModule(String name);
 
 	/** Getter for property securityLevel.
 	 * @return Value of property securityLevel.
 	 */
 	public int getSafeLevel();
 
 	/** Setter for property securityLevel.
 	 * @param safeLevel New value of property securityLevel.
 	 */
 	public void setSafeLevel(int safeLevel);
 
 	public void secure(int level);
 
 	/** rb_define_global_const
 	 *
 	 */
 	public void defineGlobalConstant(String name, IRubyObject value);
 
 	public IRubyObject getTopConstant(String name);
     
     public String getCurrentDirectory();
     
     public void setCurrentDirectory(String dir);
     
     public InputStream getIn();
     public PrintStream getOut();
     public PrintStream getErr();
 
 	public boolean isClassDefined(String name);
 
 	/** Getter for property rubyTopSelf.
 	 * @return Value of property rubyTopSelf.
 	 */
 	public IRubyObject getTopSelf();
 
     /** Getter for property isVerbose.
 	 * @return Value of property isVerbose.
 	 */
 	public IRubyObject getVerbose();
 
 	/** Setter for property isVerbose.
 	 * @param verbose New value of property isVerbose.
 	 */
 	public void setVerbose(IRubyObject verbose);
 
     public JavaSupport getJavaSupport();
 
     /** Defines a global variable
 	 */
 	public void defineVariable(final GlobalVariable variable);
 
 	/** defines a readonly global variable
 	 *
 	 */
 	public void defineReadonlyVariable(String name, IRubyObject value);
 
 	public Node parse(Reader content, String file);
 
 	public Node parse(String content, String file);
 
     public Parser getParser();
 
 	public ThreadService getThreadService();
 
 	public ThreadContext getCurrentContext();
 
     /**
 	 * Returns the loadService.
 	 * @return ILoadService
 	 */
 	public LoadService getLoadService();
 
 	public RubyWarnings getWarnings();
 
 	public PrintStream getErrorStream();
 
 	public InputStream getInputStream();
 
 	public PrintStream getOutputStream();
 
 	public RubyModule getClassFromPath(String path);
 
 	/** Prints an error with backtrace to the error stream.
 	 *
 	 * MRI: eval.c - error_print()
 	 *
 	 */
 	public void printError(RubyException excp);
 
 	/** This method compiles and interprets a Ruby script.
 	 *
 	 *  It can be used if you want to use JRuby as a Macro language.
 	 *
 	 */
 	public void loadScript(RubyString scriptName, RubyString source,
 			boolean wrap);
 
 	public void loadScript(String scriptName, Reader source, boolean wrap);
 
 	public void loadNode(String scriptName, Node node, boolean wrap);
 
 	/** Loads, compiles and interprets a Ruby file.
 	 *  Used by Kernel#require.
 	 *
 	 *  @mri rb_load
 	 */
 	public void loadFile(File file, boolean wrap);
 
 	/** Call the trace function
 	 *
 	 * MRI: eval.c - call_trace_func
 	 *
 	 */
 	public void callTraceFunction(String event, ISourcePosition position,
 			IRubyObject self, String name, IRubyObject type);
 
 	public RubyProc getTraceFunction();
 
 	public void setTraceFunction(RubyProc traceFunction);
 
 	public GlobalVariables getGlobalVariables();
 	public void setGlobalVariables(GlobalVariables variables);
 
 	public CallbackFactory callbackFactory(Class type);
 
 	/**
 	 * Push block onto exit stack.  When runtime environment exits
 	 * these blocks will be evaluated.
 	 * 
 	 * @return the element that was pushed onto stack
 	 */
 	public IRubyObject pushExitBlock(RubyProc proc);
 
 	/**
 	 * Make sure Kernel#at_exit procs get invoked on runtime shutdown.
 	 * This method needs to be explicitly called to work properly.
 	 * I thought about using finalize(), but that did not work and I
 	 * am not sure the runtime will be at a state to run procs by the
 	 * time Ruby is going away.  This method can contain any other
 	 * things that need to be cleaned up at shutdown.  
 	 */
 	public void tearDown();
 
 	public RubyArray newArray();
 
 	public RubyArray newArray(IRubyObject object);
 
 	public RubyArray newArray(IRubyObject car, IRubyObject cdr);
 
 	public RubyArray newArray(IRubyObject[] objects);
 
 	public RubyArray newArray(List list);
 
 	public RubyArray newArray(int size);
 
 	public RubyBoolean newBoolean(boolean value);
 
 	public RubyFileStat newRubyFileStat(File file);
 
 	public RubyFixnum newFixnum(long value);
 
 	public RubyFloat newFloat(double value);
 
 	public RubyNumeric newNumeric();
 
     public RubyProc newProc();
 
     public RubyBinding newBinding();
     public RubyBinding newBinding(Block block);
 
 	public RubyString newString(String string);
 
 	public RubySymbol newSymbol(String string);
 
     public RaiseException newArgumentError(String message);
     
     public RaiseException newArgumentError(int got, int expected);
     
     public RaiseException newErrnoEBADFError();
 
     public RaiseException newErrnoEINVALError();
 
     public RaiseException newErrnoENOENTError();
 
     public RaiseException newErrnoESPIPEError();
 
     public RaiseException newErrnoEBADFError(String message);
 
     public RaiseException newErrnoEINVALError(String message);
 
     public RaiseException newErrnoENOENTError(String message);
 
     public RaiseException newErrnoESPIPEError(String message);
 
     public RaiseException newErrnoEEXISTError(String message);
 
     public RaiseException newIndexError(String message);
     
     public RaiseException newSecurityError(String message);
     
     public RaiseException newSystemCallError(String message);
 
     public RaiseException newTypeError(String message);
     
     public RaiseException newThreadError(String message);
     
     public RaiseException newSyntaxError(String message);
 
     public RaiseException newRangeError(String message);
 
     public RaiseException newNotImplementedError(String message);
 
     public RaiseException newNoMethodError(String message);
 
     public RaiseException newNameError(String message);
 
     public RaiseException newLocalJumpError(String message);
 
     public RaiseException newLoadError(String message);
 
     public RaiseException newFrozenError(String objectType);
 
     public RaiseException newSystemStackError(String message);
     
     public RaiseException newSystemExit(int status);
     
     public RaiseException newIOError(String message);
     
     public RaiseException newIOErrorFromException(IOException ioe);
     
     public RaiseException newTypeError(IRubyObject receivedObject, RubyClass expectedType);
 
     public RaiseException newEOFError();
     
     public RaiseException newZeroDivisionError();
 
 	public RubySymbol.SymbolTable getSymbolTable();
 
 	public void setStackTraces(int stackTraces);
 
 	public int getStackTraces();
 
 	public void setRandomSeed(long randomSeed);
 
 	public long getRandomSeed();
 
 	public Random getRandom();
 
 	public ObjectSpace getObjectSpace();
 
 	public Hashtable getIoHandlers();
 
 	public RubyFixnum[] getFixnumCache();
 
 	public long incrementRandomSeedSequence();
 
     public RubyTime newTime(long milliseconds);
 
 	public boolean isGlobalAbortOnExceptionEnabled();
 
 	public void setGlobalAbortOnExceptionEnabled(boolean b);
 
 	public boolean isDoNotReverseLookupEnabled();
 
 	public void setDoNotReverseLookupEnabled(boolean b);
-}
\ No newline at end of file
+
+    public boolean registerInspecting(Object obj);
+    public void unregisterInspecting(Object obj);
+}
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 5158a791d2..f36a9a098e 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1191 +1,1197 @@
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
 
     public Map getInstanceVariables() {
     	// TODO: double checking may or may not be safe enough here
     	if (instanceVariables == null) {
 	    	synchronized (this) {
 	    		if (instanceVariables == null) {
 	    			instanceVariables = new HashMap();
 	    		}
 	    	}
     	}
         return instanceVariables;
     }
 
     public void setInstanceVariables(Map instanceVariables) {
         this.instanceVariables = instanceVariables;
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
-        // TODO Review this and either remove the comment, or do it
-        //     if (TYPE(obj) == T_OBJECT
-        // 	&& ROBJECT(obj)->iv_tbl
-        // 	&& ROBJECT(obj)->iv_tbl->num_entries > 0) {
-        // 	VALUE str;
-        // 	char *c;
-        //
-        // 	c = rb_class2name(CLASS_OF(obj));
-        // 	/*if (rb_inspecting_p(obj)) {
-        // 	    str = rb_str_new(0, strlen(c)+10+16+1); /* 10:tags 16:addr 1:eos */
-        // 	    sprintf(RSTRING(str)->ptr, "#<%s:0x%lx ...>", c, obj);
-        // 	    RSTRING(str)->len = strlen(RSTRING(str)->ptr);
-        // 	    return str;
-        // 	}*/
-        // 	str = rb_str_new(0, strlen(c)+6+16+1); /* 6:tags 16:addr 1:eos */
-        // 	sprintf(RSTRING(str)->ptr, "-<%s:0x%lx ", c, obj);
-        // 	RSTRING(str)->len = strlen(RSTRING(str)->ptr);
-        // 	return rb_protect_inspect(inspect_obj, obj, str);
-        //     }
-        //     return rb_funcall(obj, rb_intern("to_s"), 0, 0);
-        // }
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
+                for (Iterator iter = instanceVariableNames(); iter.hasNext();) {
+                    String name = (String) iter.next();
+                    part.append(sep);
+                    part.append(name);
+                    part.append("=");
+                    part.append(getInstanceVariable(name).callMethod("inspect"));
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
         Iterator iter = instanceVariableNames();
         while (iter.hasNext()) {
             String name = (String) iter.next();
             names.add(getRuntime().newString(name));
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
 
         output.dumpInt(getInstanceVariables().size());
         
         for (Iterator iter = instanceVariableNames(); iter.hasNext();) {
             String name = (String) iter.next();
             IRubyObject value = getInstanceVariable(name);
 
             // Between getting name and retrieving value the instance variable could have been
             // removed
             if (value != null) {
             	output.dumpObject(RubySymbol.newSymbol(getRuntime(), name));
             	output.dumpObject(value);
             }
         }
     }
    
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 }
diff --git a/test/org/jruby/test/BaseMockRuby.java b/test/org/jruby/test/BaseMockRuby.java
index 1b11ed77c8..9bd25f2a93 100644
--- a/test/org/jruby/test/BaseMockRuby.java
+++ b/test/org/jruby/test/BaseMockRuby.java
@@ -1,656 +1,662 @@
 package org.jruby.test;
 
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.Reader;
 import java.util.Hashtable;
 import java.util.List;
 import java.util.Random;
 
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBinding;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFileStat;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.RubyTime;
 import org.jruby.RubySymbol.SymbolTable;
 import org.jruby.ast.Node;
 import org.jruby.common.RubyWarnings;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.Parser;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class BaseMockRuby implements IRuby {
 
 	public CacheMap getCacheMap() {
 		throw new MockException();
 	}
 
 	public IRubyObject evalScript(String script) {
 		throw new MockException();
 	}
 
 	public IRubyObject eval(Node node) {
 		throw new MockException();
 	}
 
     public RubyClass getObject() {
         throw new MockException();
     }
 
     public RubyModule getKernel() {
         throw new MockException();
     }
     
     public RubyClass getString() {
         throw new MockException();
     }
     
     public RubyClass getFixnum() {
         throw new MockException();
     }
 
 	public RubyBoolean getTrue() {
 		throw new MockException();
 	}
 
 	public RubyBoolean getFalse() {
 		throw new MockException();
 	}
 
 	public IRubyObject getNil() {
 		throw new MockException();
 	}
 
     public RubyClass getNilClass() {
         throw new MockException();
     }
 
 	public RubyModule getModule(String name) {
 		throw new MockException();
 	}
 
 	public RubyClass getClass(String name) {
 		throw new MockException();
 	}
 
 	public RubyClass defineClass(String name, RubyClass superClass) {
 		throw new MockException();
 	}
 
 	public RubyClass defineClassUnder(String name, RubyClass superClass,
             SinglyLinkedList parentCRef) {
 		throw new MockException();
 	}
 
 	public RubyModule defineModule(String name) {
 		throw new MockException();
 	}
 
 	public RubyModule defineModuleUnder(String name, SinglyLinkedList parentCRef) {
 		throw new MockException();
 	}
 
 	public RubyModule getOrCreateModule(String name) {
 		throw new MockException();
 	}
 
 	public int getSafeLevel() {
 		throw new MockException();
 	}
 
 	public void setSafeLevel(int safeLevel) {
 		throw new MockException();
 	}
 
 	public void secure(int level) {
 		throw new MockException();
 	}
 
 	public void defineGlobalConstant(String name, IRubyObject value) {
 		throw new MockException();
 	}
 
 	public IRubyObject getTopConstant(String name) {
 		throw new MockException();
 	}
 
 	public boolean isClassDefined(String name) {
 		throw new MockException();
 	}
 
 	public IRubyObject yield(IRubyObject value) {
 		throw new MockException();
 	}
 
 	public IRubyObject yield(IRubyObject value, IRubyObject self,
 			RubyModule klass, boolean checkArguments) {
 		throw new MockException();
 	}
 
 	public IRubyObject getTopSelf() {
 		throw new MockException();
 	}
 
 	public String getSourceFile() {
 		throw new MockException();
 	}
 
 	public int getSourceLine() {
 		throw new MockException();
 	}
 
 	public IRubyObject getVerbose() {
 		throw new MockException();
 	}
 
 	public boolean isBlockGiven() {
 		throw new MockException();
 	}
 
 	public boolean isFBlockGiven() {
 		throw new MockException();
 		
 	}
 
 	public void setVerbose(IRubyObject verbose) {
 		throw new MockException();
 
 	}
 
 	public Visibility getCurrentVisibility() {
 		throw new MockException();
 		
 	}
 
 	public void setCurrentVisibility(Visibility visibility) {
 		throw new MockException();
 
 	}
 
 	public void defineVariable(GlobalVariable variable) {
 		throw new MockException();
 
 	}
 
 	public void defineReadonlyVariable(String name, IRubyObject value) {
 		throw new MockException();
 
 	}
 
 	public Node parse(Reader content, String file) {
 		throw new MockException();
 		
 	}
 
 	public Node parse(String content, String file) {
 		throw new MockException();
 		
 	}
 
 	public Parser getParser() {
 		throw new MockException();
 		
 	}
 
 	public ThreadService getThreadService() {
 		throw new MockException();
 		
 	}
 
 	public ThreadContext getCurrentContext() {
 		throw new MockException();
 		
 	}
 
 	public LoadService getLoadService() {
 		throw new MockException();
 		
 	}
 
 	public RubyWarnings getWarnings() {
 		throw new MockException();
 		
 	}
 
 	public PrintStream getErrorStream() {
 		throw new MockException();
 		
 	}
 
 	public InputStream getInputStream() {
 		throw new MockException();
 		
 	}
 
 	public PrintStream getOutputStream() {
 		throw new MockException();
 		
 	}
 
 	public RubyModule getClassFromPath(String path) {
 		throw new MockException();
 		
 	}
 
 	public void printError(RubyException excp) {
 		throw new MockException();
 
 	}
 
 	public void loadScript(RubyString scriptName, RubyString source,
 			boolean wrap) {
 		throw new MockException();
 
 	}
 
 	public void loadScript(String scriptName, Reader source, boolean wrap) {
 		throw new MockException();
 
 	}
 
 	public void loadNode(String scriptName, Node node, boolean wrap) {
 		throw new MockException();
 
 	}
 
 	public void loadFile(File file, boolean wrap) {
 		throw new MockException();
 
 	}
 
 	public void callTraceFunction(String event, ISourcePosition position,
 			IRubyObject self, String name, IRubyObject type) {
 		throw new MockException();
 
 	}
 
 	public RubyProc getTraceFunction() {
 		throw new MockException();
 		
 	}
 
 	public void setTraceFunction(RubyProc traceFunction) {
 		throw new MockException();
 
 	}
 
 	public GlobalVariables getGlobalVariables() {
 		throw new MockException();
 		
 	}
 
 	public void setGlobalVariables(GlobalVariables variables) {
 		throw new MockException();
 		
 	}
 
 	public CallbackFactory callbackFactory(Class type) {
 		throw new MockException();
 		
 	}
 
 	public IRubyObject pushExitBlock(RubyProc proc) {
 		throw new MockException();
 		
 	}
 
 	public void tearDown() {
 		throw new MockException();
 
 	}
 
 	public RubyArray newArray() {
 		throw new MockException();
 		
 	}
 
 	public RubyArray newArray(IRubyObject object) {
 		throw new MockException();
 		
 	}
 
 	public RubyArray newArray(IRubyObject car, IRubyObject cdr) {
 		throw new MockException();
 		
 	}
 
 	public RubyArray newArray(IRubyObject[] objects) {
 		throw new MockException();
 		
 	}
 
 	public RubyArray newArray(List list) {
 		throw new MockException();
 		
 	}
 
 	public RubyArray newArray(int size) {
 		throw new MockException();
 		
 	}
 
 	public RubyBoolean newBoolean(boolean value) {
 		throw new MockException();
 		
 	}
 
 	public RubyFileStat newRubyFileStat(File file) {
 		throw new MockException();
 		
 	}
 
 	public RubyFixnum newFixnum(long value) {
 		throw new MockException();
 		
 	}
 
 	public RubyFloat newFloat(double value) {
 		throw new MockException();
 		
 	}
 
 	public RubyNumeric newNumeric() {
 		throw new MockException();
 
     }
 
     public RubyProc newProc() {
         throw new MockException();
         
     }
 
     public RubyBinding newBinding() {
         throw new MockException();
         
     }
 
     public RubyBinding newBinding(Block block) {
         throw new MockException();
         
     }
 
 	public RubyString newString(String string) {
 		throw new MockException();
 		
 	}
 
 	public RubySymbol newSymbol(String string) {
 		throw new MockException();
 		
 	}
     
     public RubyTime newTime(long milliseconds) {
         throw new MockException();
     }
 
 	public RaiseException newArgumentError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newArgumentError(int got, int expected) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoEBADFError() {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoEINVALError() {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoENOENTError() {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoESPIPEError() {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoEBADFError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoEINVALError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoENOENTError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoESPIPEError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoEEXISTError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newIndexError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newSecurityError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newSystemCallError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newTypeError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newThreadError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newSyntaxError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newRangeError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newNotImplementedError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newNoMethodError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newNameError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newLocalJumpError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newLoadError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newFrozenError(String objectType) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newSystemStackError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newSystemExit(int status) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newIOError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newIOErrorFromException(IOException ioe) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newTypeError(IRubyObject receivedObject,
 			RubyClass expectedType) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newEOFError() {
 		throw new MockException();
 		
 	}
 
 	public SymbolTable getSymbolTable() {
 		throw new MockException();
 		
 	}
 
 	public void setStackTraces(int stackTraces) {
 		throw new MockException();
 
 	}
 
 	public int getStackTraces() {
 		throw new MockException();
 		
 	}
 
 	public void setRandomSeed(long randomSeed) {
 		throw new MockException();
 
 	}
 
 	public long getRandomSeed() {
 		throw new MockException();
 		
 	}
 
 	public Random getRandom() {
 		throw new MockException();
 		
 	}
 
 	public ObjectSpace getObjectSpace() {
 		throw new MockException();
 		
 	}
 
 	public Hashtable getIoHandlers() {
 		throw new MockException();
 		
 	}
 
 	public RubyFixnum[] getFixnumCache() {
 		throw new MockException();
 		
 	}
 
 	public long incrementRandomSeedSequence() {
 		throw new MockException();
 		
 	}
 
 	public JavaSupport getJavaSupport() {
 		throw new MockException();
 	}
 
     public String getCurrentDirectory() {
         throw new MockException();
     }
 
     public void setCurrentDirectory(String dir) {
         throw new MockException();
     }
 
 	public RaiseException newZeroDivisionError() {
         throw new MockException();
 	}
 
 	public InputStream getIn() {
         throw new MockException();
 	}
 
 	public PrintStream getOut() {
         throw new MockException();
 	}
 
 	public PrintStream getErr() {
         throw new MockException();
 	}
 
 	public boolean isGlobalAbortOnExceptionEnabled() {
         throw new MockException();
 	}
 
 	public void setGlobalAbortOnExceptionEnabled(boolean b) {
         throw new MockException();
 	}
 
 	public boolean isDoNotReverseLookupEnabled() {
         throw new MockException();
 	}
 
 	public void setDoNotReverseLookupEnabled(boolean b) {
         throw new MockException();
 	}
 
+    public boolean registerInspecting(Object o) {
+        throw new MockException();
+    }
+    public void unregisterInspecting(Object o) {
+        throw new MockException();
+    }
 }
diff --git a/test/testInspect.rb b/test/testInspect.rb
new file mode 100644
index 0000000000..60e3b97a7f
--- /dev/null
+++ b/test/testInspect.rb
@@ -0,0 +1,28 @@
+require 'test/minirunit'
+
+class InspectInner
+  def initialize(); @a = 1; end
+end
+
+class InspectOuter
+  def initialize(foo); @b = foo; end
+end
+
+IRE = /(#<([^:]+):\S+(?:|\s@([^=]+)=([^#\s>]+))*>)/
+
+def test_inspect_meat(inspect_string, *expected)
+  match = IRE.match(inspect_string)
+
+  test_equal(expected[0], match[2])
+  test_equal(expected[1], match[3])
+  test_equal(expected[2], match[4])
+end
+
+b = InspectOuter.new(InspectInner.new)
+
+outer = b.inspect
+outer.sub!(IRE, '___')
+inner = $1
+
+test_inspect_meat(outer, "InspectOuter", "b", "___")
+test_inspect_meat(inner, "InspectInner", "a", "1")
diff --git a/test/test_index b/test/test_index
index a73bec14a4..b8afb73aef 100644
--- a/test/test_index
+++ b/test/test_index
@@ -1,88 +1,89 @@
 
 # Basic JRuby unit tests
 
 testPositions.rb
 testConstant.rb
 testReturn.rb
 testRegexp.rb
 testStringEval.rb
 testHereDocument.rb
 testClass.rb
 testArray.rb
 testVariableAndMethod.rb
 testIf.rb
 testLoops.rb
 testMethods.rb
 testGlobalVars.rb
 testClasses.rb
 testNumber.rb
 testObject.rb
 testFloat.rb
 testBlock.rb
 testRange.rb
 testString.rb
 testStringChomp.rb
 testException.rb
 testException2.rb
 testFile.rb
 testFileTest.rb
 testIO.rb
 testThread.rb
 testMarshal.rb
 testBackquote.rb
 testHash.rb
 testSymbol.rb
 testPackUnpack.rb
 testRandom.rb
 testRuntimeCallbacks.rb
 testStruct.rb
 testGC.rb
 testLowerJavaSupport.rb
 testHigherJavaSupport.rb
 testJavaExtensions.rb
 testDigest.rb
 testDir.rb
 testVisibility.rb
 testModule.rb
 #testRbConfig.rb
 testLoad.rb
 testKernel.rb
 testCase.rb
 testLine.rb
 testJavaProxy.rb
 testRedefine.rb
 testDupCloneTaintFreeze.rb
 testProc.rb
 testNesting.rb
 testXML.rb
 testStringIO.rb
 testStringScan.rb
 testTime.rb
 testEval.rb
 testSuper.rb
 testZlib.rb
 testExpressions.rb
 testEnumerable.rb
 testEnv.rb
 testSocket.rb
 testUnboundMethod.rb
 testComparable.rb
+testInspect.rb
 
 # MRI Ruby tests (from sample/test.rb in Matz's Ruby Interpreter):
 
 mri/testAssignment.rb
 mri/testCondition.rb
 mri/testCase.rb
 mri/testIfUnless.rb
 mri/testWhileUntil.rb
 mri/testException.rb
 mri/testArray.rb
 mri/testHash.rb
 mri/testIterator.rb
 mri/testFloat.rb
 mri/testBignum.rb
 mri/testString.rb
 mri/testAssignment2.rb
 mri/testCall.rb
 mri/testProc.rb
 mri/sample/test.rb
